package com.fedco.mbc.felhr.connectivityservices;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.fedco.mbc.felhr.droidterm.utilities.HexData;
import com.fedco.mbc.felhr.log.LogGenerator;
import com.fedco.mbc.felhr.usbserial.UsbSerialDebugger;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class BluetoothService extends Service {
    public static final String ACTION_BLUETOOTH_ACTIVATED = "com.felhr.connectivityservices.ACTION_BLUETOOTH_ACTIVATED";
    public static final String ACTION_COULD_NOT_CONNECT = "com.felhr.connectivityservices.ACTION_COULD_NOT_CONNECT";
    public static final String ACTION_DEVICE_CONNECTED = "com.felhr.connectivityservices.ACTION_DEVICE_CONNECTED";
    public static final String ACTION_DEVICE_FOUND = "com.felhr.connectivityservices.ACTION_DEVICE_FOUND";
    public static final String ACTION_DISCONNECT = "com.felhr.connectivityservices.ACTION_DISCONNECT";
    public static final String ACTION_LOG_SAVED = "com.felhr..connectivityservices.LOG_SAVED";
    public static final String ACTION_NOT_LOG_SAVED = "com.felhr..connectivityservices.LOG_NOT_SAVED";
    public static final String ACTION_NO_BLUETOOTH = "com.felhr.connectivityservices.ACTION_NO_BLUETOOTH";
    public static final String ACTION_NO_DATA_STREAM = "com.felhr.connectivityservices.ACTION_NO_DATA_STREAM";
    public static final String ACTION_SCAN_FINISHED = "com.felhr.connectivityservices.ACTION_SCAN_FINISHED";
    public static final String ACTION_SCAN_STARTED = "com.felhr.connectivityservices.ACTION_SCAN_STARTED";
    private static final String CLASS_ID = BluetoothService.class.getSimpleName();
    public static final int DATA_RECEIVED_BT = 2;
    public static final String EXTRA_DEVICE_FOUND_ADDRESS = "com.felhr.connectivityservices.EXTRA_DEVICE_FOUND_ADDRESS";
    public static final String EXTRA_DEVICE_FOUND_NAME = "com.felhr.connectivityservices.EXTRA_DEVICE_FOUND_NAME";
    public static final String MODE_HEX = "com.felhr.connectivityservices.MODE_HEX";
    public static final String MODE_STRING = "com.felhr.connectivityservices.MODE_STRING";
    public static boolean SERVICE_CONNECTED = false;
    public static boolean SPP_CONNECTED = false;
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private static final int WRITE_DATA = 0;
    private IBinder binder = new BluetoothBinder();
    private BluetoothAdapter btAdapter;
    private BluetoothSocket btSocket;
    private Context context;
    private Map<String, BluetoothDevice> devicesSpotted;
    private boolean hexMode;
    private LogWriterThread logWriter;
    private Handler mHandler;
    private BroadcastReceiver mReceiver = new C01001();
    private ReadThread rThread;
    private BluetoothDevice remoteDevice;
    private boolean stringMode;
    private Handler wHandler;
    private WriteThread wThread;
    private AtomicBoolean writingLog;

    class C01001 extends BroadcastReceiver {
        C01001() {
        }

        public void onReceive(Context arg0, Intent arg1) {
            if (arg1.getAction().equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                int previousState = arg1.getIntExtra("android.bluetooth.adapter.extra.PREVIOUS_STATE", 0);
                int currentState = arg1.getIntExtra("android.bluetooth.adapter.extra.STATE", 0);
                Log.i(BluetoothService.CLASS_ID, "Previous State: " + String.valueOf(previousState));
                Log.i(BluetoothService.CLASS_ID, "Current State: " + String.valueOf(currentState));
                if (currentState == 12) {
                    BluetoothService.this.throwIntent(BluetoothService.ACTION_BLUETOOTH_ACTIVATED);
                    BluetoothService.this.btAdapter.startDiscovery();
                } else if (currentState == 10) {
                    BluetoothService.this.stopWorkingThreads();
                    BluetoothService.this.throwIntent(BluetoothService.ACTION_DISCONNECT);
                } else if (currentState == 0) {
                    BluetoothService.this.stopWorkingThreads();
                    BluetoothService.this.throwIntent(BluetoothService.ACTION_DISCONNECT);
                } else if (currentState == 3) {
                    BluetoothService.this.stopWorkingThreads();
                    BluetoothService.this.throwIntent(BluetoothService.ACTION_DISCONNECT);
                }
            } else if (arg1.getAction().equals("android.bluetooth.adapter.action.DISCOVERY_STARTED")) {
                BluetoothService.this.throwIntent(BluetoothService.ACTION_SCAN_STARTED);
            } else if (arg1.getAction().equals("android.bluetooth.adapter.action.DISCOVERY_FINISHED")) {
                BluetoothService.this.throwIntent(BluetoothService.ACTION_SCAN_FINISHED);
            } else if (arg1.getAction().equals("android.bluetooth.device.action.FOUND")) {
                BluetoothDevice deviceSpotted = (BluetoothDevice) arg1.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                String deviceName = deviceSpotted.getName();
                String deviceAddress = deviceSpotted.getAddress();
                BluetoothService.this.devicesSpotted.put(deviceAddress, deviceSpotted);
                Intent intent = new Intent(BluetoothService.ACTION_DEVICE_FOUND);
                intent.putExtra(BluetoothService.EXTRA_DEVICE_FOUND_NAME, deviceName);
                intent.putExtra(BluetoothService.EXTRA_DEVICE_FOUND_ADDRESS, deviceAddress);
                arg0.sendBroadcast(intent);
            } else if (arg1.getAction().equals("android.bluetooth.device.action.ACL_DISCONNECTED")) {
                BluetoothService.this.stopWorkingThreads();
                BluetoothService.this.throwIntent(BluetoothService.ACTION_DISCONNECT);
            }
        }
    }

    public class BluetoothBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    private class ConnectionThread extends Thread {
        private ConnectionThread() {
        }

        public void run() {
            BluetoothService.this.btAdapter.cancelDiscovery();
            try {
                BluetoothService.this.btSocket = BluetoothService.this.remoteDevice.createRfcommSocketToServiceRecord(BluetoothService.SPP_UUID);
                BluetoothService.this.btSocket.connect();
                BluetoothService.this.startWorkingThreads();
                BluetoothService.this.throwIntent(BluetoothService.ACTION_DEVICE_CONNECTED);
                BluetoothService.SPP_CONNECTED = true;
            } catch (IOException e) {
                e.printStackTrace();
                BluetoothService.this.throwIntent(BluetoothService.ACTION_COULD_NOT_CONNECT);
            }
        }
    }

    private class LogWriterThread extends Thread {
        private String data;

        public LogWriterThread(String data) {
            this.data = data;
        }

        public void run() {
            BluetoothService.this.writingLog.set(true);
            if (LogGenerator.saveLog(this.data, null)) {
                BluetoothService.this.context.sendBroadcast(new Intent("com.felhr..connectivityservices.LOG_SAVED"));
            } else {
                BluetoothService.this.context.sendBroadcast(new Intent("com.felhr..connectivityservices.LOG_NOT_SAVED"));
            }
            BluetoothService.this.writingLog.set(false);
        }
    }

    private class ReadThread extends Thread {
        private byte[] bufferRx = new byte[1024];
        private int byteCount;
        private InputStream iStream;
        private AtomicBoolean keepWorking = new AtomicBoolean(true);

        public ReadThread() {
            try {
                this.iStream = BluetoothService.this.btSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                BluetoothService.this.throwIntent(BluetoothService.ACTION_NO_DATA_STREAM);
            }
        }

        public void run() {
            while (this.keepWorking.get()) {
                try {
                    this.byteCount = this.iStream.read(this.bufferRx);
                    if (this.byteCount != -1) {
                        byte[] rxData = Arrays.copyOfRange(this.bufferRx, 0, this.byteCount);
                        if (BluetoothService.this.stringMode) {
                            BluetoothService.this.mHandler.obtainMessage(2, new String(rxData, UsbSerialDebugger.ENCODING)).sendToTarget();
                        } else if (BluetoothService.this.hexMode) {
                            BluetoothService.this.mHandler.obtainMessage(2, HexData.hexToString(rxData)).sendToTarget();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    this.keepWorking.set(false);
                }
            }
        }

        public void stopReadThread() {
            this.keepWorking.set(false);
        }
    }

    private class WriteThread extends Thread {
        private OutputStream oStream;

        class C01011 extends Handler {
            C01011() {
            }

            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        try {
                            WriteThread.this.oStream.write((byte[]) msg.obj);
                            return;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                    default:
                        return;
                }
            }
        }

        public WriteThread() {
            try {
                this.oStream = BluetoothService.this.btSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            Looper.prepare();
            BluetoothService.this.wHandler = new C01011();
            Looper.loop();
        }
    }

    public void onCreate() {
        this.context = this;
        SERVICE_CONNECTED = true;
        this.writingLog = new AtomicBoolean(false);
        this.stringMode = true;
        this.hexMode = false;
        setFilters();
        this.devicesSpotted = new Hashtable();
        this.btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.btAdapter != null && this.btAdapter.isEnabled()) {
            this.btAdapter.startDiscovery();
        } else if (this.btAdapter != null && !this.btAdapter.isEnabled()) {
            this.btAdapter.enable();
        } else if (this.btAdapter == null) {
            throwIntent(ACTION_NO_BLUETOOTH);
        }
    }

    public IBinder onBind(Intent arg0) {
        return this.binder;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return 2;
    }

    public void onDestroy() {
        super.onDestroy();
        this.btAdapter.cancelDiscovery();
        SERVICE_CONNECTED = false;
        unregisterReceiver(this.mReceiver);
    }

    public void connect(String deviceAddress) {
        this.remoteDevice = (BluetoothDevice) this.devicesSpotted.get(deviceAddress);
        if (this.remoteDevice != null) {
            new ConnectionThread().start();
        }
    }

    public void write(byte[] data) {
        this.wHandler.obtainMessage(0, data).sendToTarget();
    }

    public Bundle getDiscoveredDevices() {
        Bundle bundle = new Bundle();
        for (Entry<String, BluetoothDevice> entry : this.devicesSpotted.entrySet()) {
            BluetoothDevice device = (BluetoothDevice) entry.getValue();
            bundle.putString(device.getAddress(), device.getName());
        }
        return bundle;
    }

    public boolean saveLog(String data) {
        if (this.writingLog.get()) {
            return false;
        }
        this.logWriter = new LogWriterThread(data);
        this.logWriter.start();
        return true;
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

    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.bluetooth.device.action.FOUND");
        filter.addAction("android.bluetooth.adapter.action.DISCOVERY_STARTED");
        filter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
        filter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        filter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        registerReceiver(this.mReceiver, filter);
    }

    private void throwIntent(String action) {
        sendBroadcast(new Intent(action));
    }

    private void startWorkingThreads() {
        this.wThread = new WriteThread();
        this.rThread = new ReadThread();
        this.wThread.start();
        this.rThread.start();
    }

    private void stopWorkingThreads() {
        if (this.rThread != null) {
            this.rThread.stopReadThread();
            this.rThread = null;
        }
        if (this.wHandler != null) {
            this.wHandler.getLooper().quit();
            this.wThread = null;
        }
    }
}
