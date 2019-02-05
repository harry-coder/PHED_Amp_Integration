package com.fedco.mbc.felhr.connectivityservices;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.fedco.mbc.felhr.deviceids.CH34xIds;
import com.fedco.mbc.felhr.deviceids.CP210xIds;
import com.fedco.mbc.felhr.deviceids.FTDISioIds;
import com.fedco.mbc.felhr.deviceids.PL2303Ids;
import com.fedco.mbc.felhr.droidterm.DroidTermBuild;
import com.fedco.mbc.felhr.droidterm.DroidTermBuild.BUILD_TYPE;
import com.fedco.mbc.felhr.droidterm.TerminalFragment;
import com.fedco.mbc.felhr.droidterm.utilities.HexData;
import com.fedco.mbc.felhr.encodings.Cp437;
import com.fedco.mbc.felhr.encodings.EncodingBuffer;
import com.fedco.mbc.felhr.log.LogGenerator;
import com.fedco.mbc.felhr.usbserial.CDCSerialDevice;
import com.fedco.mbc.felhr.usbserial.UsbSerialDebugger;
import com.fedco.mbc.felhr.usbserial.UsbSerialDevice;
import com.fedco.mbc.felhr.usbserial.UsbSerialInterface.UsbReadCallback;
import com.fedco.mbc.felhr.usbviewerdata.UsbViewerData;
import com.fedco.mbc.felhr.usbviewerdata.UsbViewerData.UsbViewerDataUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class UsbService extends Service {
    private static final String TAG = UsbService.class.getSimpleName();
    public static final String ACTION_CDC_DRIVER_NOT_WORKING = "com.felhr.connectivityservices.ACTION_CDC_DRIVER_NOT_WORKING";
    public static final String ACTION_LOG_SAVED = "com.felhr..connectivityservices.LOG_SAVED";
    public static final String ACTION_NOT_LOG_SAVED = "com.felhr..connectivityservices.LOG_NOT_SAVED";
    public static final String ACTION_NO_USB = "com.felhr.connectivityservices.NO_USB";
    public static final String ACTION_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    public static final String ACTION_USB_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";
    public static final String ACTION_USB_DEVICE_NOT_WORKING = "com.felhr.connectivityservices.ACTION_USB_DEVICE_NOT_WORKING";
    public static final String ACTION_USB_DISCONNECTED = "com.felhr.connectivityservices.USB_DISCONNECTED";
    public static final String ACTION_USB_NOT_SUPPORTED = "com.felhr.connectivityservices.USB_NOT_SUPPORTED";
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    public static final String ACTION_USB_PERMISSION_GRANTED = "com.felhr.connectivityservices.USB_PERMISSION_GRANTED";
    public static final String ACTION_USB_PERMISSION_NOT_GRANTED = "com.felhr.connectivityservices.USB_PERMISSION_NOT_GRANTED";
    public static final String ACTION_USB_READY = "com.felhr.connectivityservices.USB_READY";
    private static final String CLASS_ID = UsbService.class.getSimpleName();
    public static final String CONFIG_BAUDRATE = "com.felhr.connectivityservices.BAUD_RATE";
    public static final String CONFIG_DATA_BITS = "com.felhr.connectivityservices.DATA_BITS";
    public static final String CONFIG_FLOW = "com.felhr.connectivityservices.FLOW";
    public static final String CONFIG_PARITY = "com.felhr.connectivityservices.PARITY";
    public static final String CONFIG_PROFILE = "com.felhr.connectivityservices.PROFILE";
    public static final String CONFIG_STOP_BITS = "com.felhr.connectivityservices.STOP_BITS";
    public static final int DATA_RECEIVED = 3;
    private static final String ENCODING_MODE = "input_mode";
    private static final String ENCODING_PREFERENCES = "input_preferences";
    public static final String MODE = "com.felhr.connectivityservices.MODE";
    public static final String MODE_HEX = "com.felhr.connectivityservices.MODE_HEX";
    public static final String MODE_SERIAL = "com.felhr.connectivityservices.MODE_SERIAL";
    public static final String MODE_STRING = "com.felhr.connectivityservices.MODE_STRING";
    public static final String MODE_VIEWER = "com.felhr.connectivityservices.MODE_VIEWER";
    public static final String PID_NOT_SUPPORTED = "com.felhr.connectivityservices.PID_NOT_SUPPORTED";
    public static final String PID_SUPPORTED = "com.felhr.connectivityservices.PID_SUPPORTED";
    public static boolean SERIAL_PORT_CONNECTED = false;
    public static boolean SERVICE_CONNECTED = false;
    public static final String USB_DEVICE_NOT_SUPPORTED = "com.felhr.connectivityservices.USB_DEVICE_NOT_SUPPORTED";
    public static final String VID_NOT_SUPPORTED = "com.felhr.connectivityservices.VID_NOT_SUPPORTED";
    public static final String VID_SUPPORTED = "com.felhr.connectivityservices.VID_SUPPORTED";
    public boolean SERIAL_MODE;
    private Handler autoLogHandler;
    private AutoLogWriterThread autoLogWriter;
    private IBinder binder = new UsbBinder();
    private Bundle configMetaData;
    private Context context;
    private EncodingBuffer encodingBuffer;
    private int encodingMode;
    private boolean hexMode;
    private LogWriterThread logWriter;
    private UsbReadCallback mCallback = new C03182();
    private Handler mHandler;
    private UsbDevice serialDevice;
    public UsbSerialDevice serialPort;
    private SharedPreferences sharedPreferences;
    private boolean stringMode;
    private UsbDevice usbDevice;
    private UsbManager usbManager;
    private final BroadcastReceiver usbReceiver = new C01021();
    private WakeLock wakeLock;
    private AtomicBoolean writingLog;

    public static byte[] rawData = new byte[2408];
    public static Queue queue;
    public static Queue<byte[]> myQ;
    public static Queue<String> myQ2;
    public static StringBuffer stringBuffer = new StringBuffer();
    public static StringBuffer genusOpticalStringBuffer = new StringBuffer();


    class C01021 extends BroadcastReceiver {
        C01021() {
        }

        public void onReceive(Context arg0, Intent arg1) {
            if (arg1.getAction().equals(UsbService.ACTION_USB_PERMISSION)) {
                if (arg1.getExtras().getBoolean("permission")) {
                    try {
                        Intent intent = new Intent(UsbService.ACTION_USB_PERMISSION_GRANTED);
                        String vendorId = HexData.hex4digits(Integer.toHexString(UsbService.this.serialDevice.getVendorId()));
                        String productId = HexData.hex4digits(Integer.toHexString(UsbService.this.serialDevice.getProductId()));
                        Log.i(UsbService.CLASS_ID, "VendorId: " + vendorId);
                        Log.i(UsbService.CLASS_ID, "ProductId: " + productId);
                        intent.putExtra(UsbService.VID_SUPPORTED, vendorId);
                        intent.putExtra(UsbService.PID_SUPPORTED, productId);
                        UsbService.this.context.sendBroadcast(intent);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

                UsbService.this.context.sendBroadcast(new Intent(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED));

            } else if (arg1.getAction().equals(UsbService.ACTION_USB_ATTACHED)) {
                UsbDevice usbDevice = (UsbDevice) UsbService.this.usbManager.getDeviceList().get(arg1.getStringExtra("device"));
            } else if (arg1.getAction().equals(UsbService.ACTION_USB_DETACHED)) {
                UsbService.this.serialDevice = null;
                UsbService.this.serialPort = null;
                UsbService.SERIAL_PORT_CONNECTED = false;
                UsbService.this.context.sendBroadcast(new Intent(UsbService.ACTION_USB_DISCONNECTED));
            }
        }
    }

    private class AutoLogWriterThread extends Thread {
        private String fileName;

        class C01031 extends Handler {
            C01031() {
            }

            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 3:
                        UsbService.this.writingLog.set(true);
                        LogGenerator.updateAutoLog(AutoLogWriterThread.this.fileName, (String) msg.obj);
                        UsbService.this.writingLog.set(false);
                        return;
                    default:
                        return;
                }
            }
        }

        public AutoLogWriterThread(String profileName) {
            this.fileName = profileName + "_log.txt";
        }

        public void run() {
            Looper.prepare();
            UsbService.this.autoLogHandler = new C01031();
            Looper.loop();
        }
    }

    private class LogWriterThread extends Thread {
        private String data;

        public LogWriterThread(String data) {
            this.data = data;
        }

        public void run() {
            UsbService.this.writingLog.set(true);
            if (LogGenerator.saveLog(this.data, UsbService.this.configMetaData)) {
                UsbService.this.context.sendBroadcast(new Intent("com.felhr..connectivityservices.LOG_SAVED"));
            } else {
                UsbService.this.context.sendBroadcast(new Intent("com.felhr..connectivityservices.LOG_NOT_SAVED"));
            }
            UsbService.this.writingLog.set(false);
        }
    }

    private class SerialConfigThread extends Thread {
        private int baudRate;
        private int dataBits;
        private int flow;
        private int parity;
        private int stopBits;

        public SerialConfigThread(int baudRate, int dataBits, int stopBits, int parity, int flow) {
            this.baudRate = baudRate;
            this.dataBits = dataBits;
            this.stopBits = stopBits;
            this.parity = parity;
            this.flow = flow;
        }

        public void run() {
            UsbService.this.serialPort = UsbSerialDevice.createUsbSerialDevice(UsbService.this.serialDevice, UsbService.this.usbManager.openDevice(UsbService.this.serialDevice));
            if (UsbService.this.serialPort.open()) {
                if (this.baudRate != 0) {
                    UsbService.this.serialPort.setBaudRate(this.baudRate);
                }
                UsbService.this.configMetaData.putString(LogGenerator.BAUD_RATE, String.valueOf(this.baudRate));
                if (this.dataBits != 8) {
                    UsbService.this.serialPort.setDataBits(this.dataBits);
                }
                UsbService.this.configMetaData.putString(LogGenerator.DATA_BITS, String.valueOf(this.dataBits));
                if (this.stopBits != 1) {
                    UsbService.this.serialPort.setStopBits(this.stopBits);
                }
                if (this.stopBits == 3) {
                    UsbService.this.configMetaData.putString(LogGenerator.STOP_BITS, "1.5");
                } else {
                    UsbService.this.configMetaData.putString(LogGenerator.STOP_BITS, String.valueOf(this.stopBits));
                }
                if (this.parity != 0) {
                    UsbService.this.serialPort.setParity(this.parity);
                }
                if (this.parity == 0) {
                    UsbService.this.configMetaData.putString(LogGenerator.PARITY, "None");
                } else if (this.parity == 1) {
                    UsbService.this.configMetaData.putString(LogGenerator.PARITY, "Odd");
                } else if (this.parity == 2) {
                    UsbService.this.configMetaData.putString(LogGenerator.PARITY, "Even");
                } else if (this.parity == 3) {
                    UsbService.this.configMetaData.putString(LogGenerator.PARITY, "Mark");
                } else if (this.parity == 4) {
                    UsbService.this.configMetaData.putString(LogGenerator.PARITY, "Space");
                }
                if (this.flow != 0) {
                    UsbService.this.serialPort.setFlowControl(this.flow);
                }
                UsbService.this.configMetaData.putString(LogGenerator.FLOW, "None");
                UsbService.this.serialPort.read(UsbService.this.mCallback);
                String profileName = UsbService.this.configMetaData.getString(LogGenerator.PROFILE);
                if (profileName != null) {
                    UsbService.this.startAutoLog(profileName);
                }
                UsbService.this.context.sendBroadcast(new Intent(UsbService.ACTION_USB_READY));
                UsbService.SERIAL_PORT_CONNECTED = true;
            } else if (UsbService.this.serialPort instanceof CDCSerialDevice) {
                UsbService.this.context.sendBroadcast(new Intent(UsbService.ACTION_CDC_DRIVER_NOT_WORKING));
            } else {
                UsbService.this.context.sendBroadcast(new Intent(UsbService.ACTION_USB_DEVICE_NOT_WORKING));
            }
        }
    }

    public class UsbBinder extends Binder {
        public UsbService getService() {
            return UsbService.this;
        }
    }

    class C03182 implements UsbReadCallback {
        C03182() {
        }

        public void onReceivedData(byte[] arg0) {
            if (arg0.length <= 0) {
                return;
            }
            /*
            * Here we are getitng raw received data from Meter*/
            else {
                if (TerminalFragment.visionTeachFlag == 1) {
                    String str = HexData.hexToString(arg0);
                    Log.e(TAG, "(visionTeachFlag)usb SERVICE ARG-HEX :" + str);
                    stringBuffer.append(str);

                } else if (TerminalFragment.isGenusOptical) {
                    String str = HexData.hexToString(arg0);
                    Log.e(TAG, "(isGenusOptical)usb SERVICE ARG-HEX :" + str);
                    genusOpticalStringBuffer.append(str);

                } else if (TerminalFragment.isMontel) {
                    String str = HexData.hexToString(arg0);
                    Log.e(TAG, "(isMontel)usb SERVICE ARG-HEX :" + str);
                    stringBuffer.append(str);

                } else if (TerminalFragment.isAvonResponse) {
                    String str = HexData.hexToString(arg0);
                    Log.e(TAG, "(isAvonResponse)usb SERVICE ARG-HEX :" + str);
                    stringBuffer.append(str);

                } else if (TerminalFragment.isBentek) {
                    String str = HexData.hexToString(arg0);
                    Log.e(TAG, "(isBentek)usb SERVICE ARG-HEX :" + str);
                    stringBuffer.append(str);
                } else if (TerminalFragment.isAlloyedOpt) {
                    String str = HexData.hexToString(arg0);
                    Log.e(TAG, "isAlloyedOpt usb SERVICE ARG-HEX :" + str);
                    stringBuffer.append(str);
                } else if (TerminalFragment.isAvon3Ph) {
                    String str = HexData.hexToString(arg0);
                    Log.e(TAG, "isAvon3Ph usb SERVICE ARG-HEX :" + str);
                    stringBuffer.append(str);
                } else if (TerminalFragment.isDlms) {
                    String str = HexData.hexToString(arg0);
                    Log.e(TAG, "isDlms usb SERVICE ARG-HEX :" + str);
                    stringBuffer.append(str);
                } else if (TerminalFragment.isVisiontek6LowPan) {
                    String str = HexData.hexToString(arg0);
                    Log.e(TAG, "isDlms usb SERVICE ARG-HEX :" + str);
                    stringBuffer.append(str);
                } else {
                    String str = HexData.hexToString(arg0);
                    Log.e(TAG, "usb SERVICE ARG-HEX :" + str);
                    myQ2.add(HexData.hexToString(arg0));
                    myQ.add(arg0);
                    UsbService.this.rawData = arg0;
                }
            }
            if (UsbService.this.stringMode) {
                if (UsbService.this.encodingMode != 2 && UsbService.this.encodingMode != 3) {
                    try {
                        UsbService.this.convertAndSend(arg0);
                    } catch (UnsupportedEncodingException e) {
                        Log.i(UsbService.CLASS_ID, "Encoding not supported: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else if (UsbService.this.encodingMode == 2) {
                    UsbService.this.encodingBuffer.putByte(arg0);
                    if (!UsbService.this.encodingBuffer.isRemainingUTF16Bytes()) {
                        try {
                            UsbService.this.convertAndSend(UsbService.this.encodingBuffer.getUTF16Char());
                        } catch (UnsupportedEncodingException e2) {
                            Log.i(UsbService.CLASS_ID, "Encoding not supported: " + e2.getMessage());
                            e2.printStackTrace();
                        }
                    }
                } else if (UsbService.this.encodingMode == 3) {
                    UsbService.this.encodingBuffer.putByte(arg0);
                    if (!UsbService.this.encodingBuffer.isRemainingBytes()) {
                        try {
                            UsbService.this.convertAndSend(UsbService.this.encodingBuffer.getUTF8Char());
                        } catch (UnsupportedEncodingException e22) {
                            Log.i(UsbService.CLASS_ID, "Encoding not supported: " + e22.getMessage());
                            e22.printStackTrace();
                        }
                    }
                }
            } else if (UsbService.this.hexMode) {
                String data = HexData.hexToString(arg0);
                Log.e(TAG, "" + arg0);
                UsbService.this.mHandler.obtainMessage(3, data).sendToTarget();
                if (UsbService.this.autoLogHandler != null) {
                    UsbService.this.autoLogHandler.obtainMessage(3, data).sendToTarget();
                }
            }
        }
    }

    public void onCreate() {
        boolean z = false;
        this.context = this;
        this.stringMode = true;
        this.hexMode = false;
        this.writingLog = new AtomicBoolean(false);
        SERVICE_CONNECTED = true;
        this.configMetaData = new Bundle();
        this.sharedPreferences = getSharedPreferences(ENCODING_PREFERENCES, 0);
        this.myQ = new LinkedList<byte[]>();
        this.myQ2 = new LinkedList<String>();

        if (DroidTermBuild.TYPE == BUILD_TYPE.LITE) {
            this.encodingMode = this.sharedPreferences.getInt(ENCODING_MODE, 1);
        } else {
            this.encodingMode = this.sharedPreferences.getInt(ENCODING_MODE, 1);
        }
        if (this.encodingMode == 2 || this.encodingMode == 3) {
            if (this.encodingMode == 3) {
                z = true;
            }
            this.encodingBuffer = new EncodingBuffer(z);
        }
        setFilter();
        this.usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        this.wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(1, "UsbService");
        this.wakeLock.acquire();
    }

    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    @SuppressLint("WrongConstant")
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getExtras().getString(MODE).equals(MODE_SERIAL)) {
            this.SERIAL_MODE = true;
        } else if (intent.getExtras().getString(MODE).equals(MODE_VIEWER)) {
            this.SERIAL_MODE = false;
        }
        HashMap<String, UsbDevice> devices = getUsbDevices();
        if (!this.SERIAL_MODE) {
            this.usbDevice = getDeviceData(devices);
        } else if (devices.isEmpty()) {
            this.context.sendBroadcast(new Intent(ACTION_NO_USB));
        } else {
            this.serialDevice = getSupportedDevice(devices);
            if (this.serialDevice != null) {
                configureDevice(this.serialDevice);
            } else {
                notifyUnsupportedDevice(devices);
            }
        }
        return 2;
    }

    public void onDestroy() {
        super.onDestroy();
        SERVICE_CONNECTED = false;
        unregisterReceiver(this.usbReceiver);
        if (this.serialPort != null) {
            this.serialPort.close();
            this.serialPort = null;
            this.serialDevice = null;
        } else {
            this.serialPort = null;
            this.serialDevice = null;
        }
        this.wakeLock.release();
    }

    public void openSerialPort(Bundle serialConfig) {
        int baudRate = serialConfig.getInt(CONFIG_BAUDRATE, 0);
        int dataBits = serialConfig.getInt(CONFIG_DATA_BITS, 8);
        int stopBits = serialConfig.getInt(CONFIG_STOP_BITS, 1);
        int parity = serialConfig.getInt(CONFIG_PARITY, 0);
        int flow = serialConfig.getInt(CONFIG_FLOW, 0);
        this.configMetaData.putString(LogGenerator.DEVICE_NAME, serialConfig.getString(LogGenerator.DEVICE_NAME));
        this.configMetaData.putString(LogGenerator.PROFILE, serialConfig.getString(CONFIG_PROFILE));
        setupSerialDevice(baudRate, dataBits, stopBits, parity, flow);
    }

    public void closeSerialPort() {
        if (SERIAL_PORT_CONNECTED) {
            this.serialPort.close();
        }
    }

    public void write(byte[] data) {
        if (SERIAL_PORT_CONNECTED) {
            try{
                this.serialPort.write(data);
            }catch (Exception e){
                closeSerialPort();
                e.printStackTrace();
            }

        }
    }

    public String[] getVidPid() {
        if (this.SERIAL_MODE || this.usbDevice == null) {
            return null;
        }
        return new String[]{HexData.hex4digits(Integer.toHexString(this.usbDevice.getVendorId())), HexData.hex4digits(Integer.toHexString(this.usbDevice.getProductId()))};
    }

    public void configureEncoding(int encoding) {
        this.encodingMode = 1;
        if ((this.encodingMode == 2 || this.encodingMode == 3) && this.encodingBuffer == null) {
            this.encodingBuffer = new EncodingBuffer(this.encodingMode == 3);
        }
    }

    public UsbViewerData getUsbData(String vendorName, String productName) {
        if (this.SERIAL_MODE) {
            return null;
        }
        UsbViewerData usbData = new UsbViewerData();
        usbData.vendorName = vendorName;
        usbData.vendorId = "0x" + HexData.hex4digits(Integer.toHexString(this.usbDevice.getVendorId()));
        usbData.productName = productName;
        usbData.productId = "0x" + HexData.hex4digits(Integer.toHexString(this.usbDevice.getProductId()));
        int interfaceCount = this.usbDevice.getInterfaceCount();
        for (int i = 0; i <= interfaceCount - 1; i++) {
            UsbInterface interfaceUsb = this.usbDevice.getInterface(i);
            usbData.addInterface(UsbViewerDataUtils.getInterfaceClass(interfaceUsb.getInterfaceClass()));
            int endpointCount = interfaceUsb.getEndpointCount();
            for (int j = 0; j <= endpointCount - 1; j++) {
                UsbEndpoint endpoint = interfaceUsb.getEndpoint(j);
                int attr = endpoint.getAttributes();
                int direction = endpoint.getDirection();
                int type = endpoint.getType();
                int interval = endpoint.getInterval();
                int packSize = endpoint.getMaxPacketSize();
                UsbViewerData usbViewerData = usbData;
                usbViewerData.addEndpoint(UsbViewerDataUtils.getAttributes(attr, direction), UsbViewerDataUtils.getType(type), UsbViewerDataUtils.getInterval(interval), UsbViewerDataUtils.getPackSize(packSize));
            }
        }
        usbData.addLastInterface();
        return usbData;
    }

    public Bundle getConnectionData() {
        return this.configMetaData;
    }

    public void setPresentationMode(String mode) {
        if (mode.equals("com.felhr.connectivityservices.MODE_HEX")) {
            this.stringMode = false;
            this.hexMode = true;
        } else if (mode.equals("com.felhr.connectivityservices.MODE_STRING")) {
            this.hexMode = false;
            this.stringMode = true;
        }
    }

    public void startAutoLog(String profileName) {
        if (this.autoLogWriter != null || this.autoLogHandler != null) {
            LogGenerator.createAutoLog(profileName, this.configMetaData);
        } else if (profileName != null) {
            Log.i(CLASS_ID, "Profile Name not null, Start Auto Log");
            this.autoLogWriter = new AutoLogWriterThread(profileName);
            this.autoLogWriter.start();
            LogGenerator.createAutoLog(profileName, this.configMetaData);
        } else {
            Log.i(CLASS_ID, "Profile Name: None");
            this.autoLogWriter = null;
        }
    }

    public boolean saveLog(String data) {
        if (this.writingLog.get()) {
            return false;
        }
        this.logWriter = new LogWriterThread(data);
        this.logWriter.start();
        return true;
    }

    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    private void convertAndSend(byte[] source) throws UnsupportedEncodingException {
        String data = null;
        if (this.encodingMode == 0) {
            data = new String(source, "US-ASCII");
        } else if (this.encodingMode == 1) {
            data = new String(source, "ISO-8859-1");
        } else if (this.encodingMode == 2) {
            data = new String(source, "UTF-16");
        } else if (this.encodingMode == 3) {
            data = new String(source, UsbSerialDebugger.ENCODING);
        } else if (this.encodingMode == 4) {
            data = new String(new Cp437().toUTF8(source), UsbSerialDebugger.ENCODING);
        }

        // Log.e(TAG,"Response data: "+data);

        this.mHandler.obtainMessage(3, data).sendToTarget();
        if (this.autoLogHandler != null) {
            this.autoLogHandler.obtainMessage(3, data).sendToTarget();
        }
    }

    private HashMap<String, UsbDevice> getUsbDevices() {
        return this.usbManager.getDeviceList();
    }

    private UsbDevice getSupportedDevice(HashMap<String, UsbDevice> deviceList) {
        for (Entry<String, UsbDevice> entry : deviceList.entrySet()) {
            UsbDevice device = (UsbDevice) entry.getValue();
            int vendorId = device.getVendorId();
            int productId = device.getProductId();
            if (CP210xIds.isDeviceSupported(vendorId, productId) || FTDISioIds.isDeviceSupported(vendorId, productId) || PL2303Ids.isDeviceSupported(vendorId, productId) || CH34xIds.isDeviceSupported(vendorId, productId)) {
                return device;
            }
            if (vendorId != 5401 && productId != 32 && isCdcDevice(device)) {
                return device;
            }
        }
        return null;
    }

    private boolean isCdcDevice(UsbDevice device) {
        int iIndex = device.getInterfaceCount();
        for (int i = 0; i <= iIndex - 1; i++) {
            if (device.getInterface(i).getInterfaceClass() == 10) {
                return true;
            }
        }
        return false;
    }

    private void setupSerialDevice(int baudRate, int dataBits, int stopBits, int parity, int flow) {
        new SerialConfigThread(baudRate, dataBits, stopBits, parity, flow).start();
    }

    private void setFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(ACTION_USB_DETACHED);
        filter.addAction(ACTION_USB_ATTACHED);
        registerReceiver(this.usbReceiver, filter);
    }

    private void configureDevice(UsbDevice device) {
        this.usbManager.requestPermission(device, PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0));
    }

    private void notifyUnsupportedDevice(HashMap<String, UsbDevice> devices) {
        UsbDevice device = (UsbDevice) ((Entry) devices.entrySet().iterator().next()).getValue();
        int productId = device.getProductId();
        int vendorId = device.getVendorId();
        Intent intent = new Intent(ACTION_USB_NOT_SUPPORTED);
        intent.putExtra(PID_NOT_SUPPORTED, productId);
        intent.putExtra(VID_NOT_SUPPORTED, vendorId);
        this.context.sendBroadcast(intent);
    }

    private UsbDevice getDeviceData(HashMap<String, UsbDevice> deviceList) {
        for (Entry<String, UsbDevice> entry : deviceList.entrySet()) {
            UsbDevice device = (UsbDevice) entry.getValue();
            int deviceVid = device.getVendorId();
            int devicePid = device.getProductId();
            if ((deviceVid != 7531 || devicePid != 1 || devicePid != 2 || devicePid != 3) && deviceVid != 5401 && devicePid != 32) {
                return device;
            }
        }
        return null;
    }
}
