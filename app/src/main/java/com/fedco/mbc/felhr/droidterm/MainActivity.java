package com.fedco.mbc.felhr.droidterm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.felhr.connectivityservices.UsbService;
import com.fedco.mbc.felhr.connectivityservices.UsbService.UsbBinder;
import com.fedco.mbc.felhr.droidterm.ActivityInterfaces.INoUsbDialogCommunicator;
import com.fedco.mbc.felhr.droidterm.ActivityInterfaces.IOnDeviceViewer;
import com.fedco.mbc.felhr.droidterm.ActivityInterfaces.IOnToggleKeyboard;
import com.fedco.mbc.felhr.droidterm.ActivityInterfaces.ISendLogData;
import com.fedco.mbc.felhr.droidterm.ConfigurationDialogFragment.IConfigurationCommunicator;
import com.fedco.mbc.felhr.droidterm.DroidTermBuild.BUILD_TYPE;
import com.fedco.mbc.felhr.droidterm.EncodingDialogFragment.IEncodingFragmentCommunicator;
import com.fedco.mbc.felhr.droidterm.FileBrowserDialogFragment.IFileBrowserCommunicator;
import com.fedco.mbc.felhr.droidterm.InputFragment.IInputFragmentCommunicator;
import com.fedco.mbc.felhr.droidterm.LogViewerDialogFragment.ILogViewerCommunicator;
import com.fedco.mbc.felhr.droidterm.MainDialogFragment.IActivityCommunicator;
import com.fedco.mbc.felhr.droidterm.ProfilesDialogFragment.IProfilesDialogCommunicator;
import com.fedco.mbc.felhr.droidterm.SaveProfileDialogFragment.ISaveProfileCommunicator;
import com.fedco.mbc.felhr.droidterm.SendDataDialogFragment.ISendDataCommunicator;
import com.fedco.mbc.felhr.droidterm.TerminalFragment.ITerminalCommunicator;
import com.fedco.mbc.felhr.droidterm.UsbNotCompatibleFragment.INotUsbCommunicator;
import com.fedco.mbc.felhr.droidterm.UsbViewerDialogFragment.UsbViewerCommunicator;
import com.fedco.mbc.felhr.droidterm.usbids.UsbData;
import com.fedco.mbc.felhr.droidterm.usbids.UsbDataProvider;
import com.fedco.mbc.felhr.droidterm.usbids.UsbDataProvider.UsbDbCallback;
import com.fedco.mbc.felhr.droidterm.utilities.HexData;
import com.fedco.mbc.felhr.log.LogGenerator;
import com.fedco.mbc.felhr.profile.Profile;
import com.fedco.mbc.felhr.profile.ProfileProvider;
import com.fedco.mbc.felhr.usbviewerdata.UsbViewerData;

import java.lang.ref.WeakReference;
import java.util.List;

public class MainActivity extends Activity implements IActivityCommunicator, INotUsbCommunicator, IConfigurationCommunicator, ITerminalCommunicator, UsbViewerCommunicator, IFileBrowserCommunicator, ILogViewerCommunicator, ISendDataCommunicator, IProfilesDialogCommunicator, ISaveProfileCommunicator, IInputFragmentCommunicator, IOnToggleKeyboard, IEncodingFragmentCommunicator {
    public static final String BAUD_KEY = "baud_rate";
    public static final String CL_KEY = "command_line";
    private static final String CURRENT_DIALOG = "com.felhr.MainActivity.CURRENT_DIALOG";
    public static final String DATA_BITS_KEY = "data_bits";
    private static final int DB_NOT_CREATED = 4;
    public static final String DEVICE_NAME_KEY = "device_name";
    public static final String FLOW_KEY = "flow";
    private static final String HEX_MODE = "Hex Mode";
    private static final String LOG_VIEWER_DATA = "LOG_VIEWER_DATA";
    private static final String NO_DIALOG = "com.felhr.MainActivity.NONE";
    private static final String NO_USB_DIALOG_MESSAGE = "com.felhr.MainActivity.NO_USB_DIALOG_MESSAGE";
    public static final String PARITY_KEY = "parity";
    private static final String REPRESENTATION_MODE = "RepresentationMode";
    public static final String STOP_BITS_KEY = "stop_bits";
    private static final String STRING_MODE = "String Mode";
    private AboutDialogFragment aboutFragment;
    private Bundle configDataCurrentSession;
    private ConfigurationDialogFragment configurationFragment;
    private Context context;
    private String currentDialog = MainDialogFragment.CLASS_ID;
    private UsbDataProvider dataProvider;
    private boolean dbLoaded;
    private int dbOperation;
    private EncodingDialogFragment encodingFragment;
    private FileBrowserDialogFragment fileBrowserFragment;
    private FragmentManager fm;
    private InputFragment inputFragment;
    private MenuItem inputMode;
    private ISendLogData logViewerComm;
    private String logViewerData;
    private LogViewerDialogFragment logViewerFragment;
    private UsbDbCallback mCallback = new C03193();
    private MyHandler mHandler;
    private final BroadcastReceiver mUsbReceiver = new C01051();
    private MainDialogFragment mainDialogFragment;
    private Object monitor;
    private INoUsbDialogCommunicator noUsbCommunicator;
    private IOnDeviceViewer onDeviceViewer;
    private ProfileProvider profileProvider;
    private List<Profile> profiles;
    private ProfilesDialogFragment profilesFragment;
//    private MenuItem representationMode;
//    private String representationValue = HEX_MODE;
    private SaveProfileDialogFragment saveProfileFragment;
    private Bundle savedState;
    private SendDataDialogFragment sendDataFragment;
    private SerialDataFragment serialDataFragment;
    private TerminalFragment terminalFragment;
    private final ServiceConnection usbConnection = new C01062();
    private UsbData usbData;
    private UsbNotCompatibleFragment usbNotCompatibleFragment;
    private UsbService usbService;
    private UsbViewerDialogFragment usbViewerFragment;
    private int flag;

    class C01051 extends BroadcastReceiver {
        C01051() {
        }

        @SuppressLint({"DefaultLocale"})
        public void onReceive(Context arg0, Intent arg1) {
            if (arg1.getAction().equals(UsbService.ACTION_USB_PERMISSION_GRANTED)) {
                String vid = arg1.getStringExtra(UsbService.VID_SUPPORTED);
                String pid = arg1.getStringExtra(UsbService.PID_SUPPORTED);
                MainActivity.this.usbData = MainActivity.this.dataProvider.lookup(vid, pid);
                if (MainActivity.this.usbData == null) {
                    MainActivity.this.usbData = new UsbData(vid, "None", pid, "None");
                }
                MainActivity.this.profiles = MainActivity.this.profileProvider.lookup(vid, pid);
                if (MainActivity.this.profiles != null) {
                    MainActivity.this.profilesFragment.populateList(MainActivity.this.profiles);
                    return;
                }
                int switchcase = GSBilling.getInstance().getMeterCode();
                switch (switchcase) {
                    case 1:
                        loadProfileUpdate("elloyed", 2400, 8, 1, 0, 0);
                        flag = 1;
                        break;
                    case 2:
                        loadProfileUpdate("LnT", 300, 7, 1, 2, 0);
                        flag = 2;
                        break;
                    case 3:
                        loadProfileUpdate("Secure", 1200, 8, 1, 0, 0);
                        flag = 3;
                        break;
                    case 4:
                        loadProfileUpdate("Genus", 9600, 8, 1, 0, 0);
                        flag = 4;
                        break;
                    case 5:
                        loadProfileUpdate("Palmohan", 9600, 8, 1, 0, 0);
                        flag = 5;
                        break;
                    case 6:
                        loadProfileUpdate("genusopt", 9600, 8, 1, 0, 0);
                        flag = 6;
                        break;
                    case 7:
                        loadProfileUpdate("lpr54382", 115200, 8, 1, 0, 0);
                        flag = 7;
                        break;
                    case 8:
                        loadProfileUpdate("lpr43580", 115200, 8, 1, 0, 0);
                        flag = 8;
                        break;
                    case 9:
                        loadProfileUpdate("lpr09682", 115200, 8, 1, 0, 0);
                        flag = 9;
                        break;
                    case 10:
                        loadProfileUpdate("visiontek", 9600, 8, 1, 0, 0);
                        flag = 10;
                        break;
                    case 11:
                        loadProfileUpdate("montel", 9600, 8, 1, 0, 0);
                        flag = 11;
                        break;
                    case 12:
                        loadProfileUpdate("avon", 9600, 8, 1, 0, 0);
                        flag = 12;
                        break;
                    case 13:
                        loadProfileUpdate("bentek", 4800, 8, 1, 0, 0);
                        flag = 13;
                        break;
                    case 14:
                        loadProfileUpdate("avon_opt", 2400, 8, 1, 0, 0);
                        flag = 14;
                        break;
                    case 15:
                        loadProfileUpdate("alloyed_opt", 9600, 8, 1, 0, 0);
                        flag = 15;
                        break;
                    case 16:
                        loadProfileUpdate("avon_3ph", 9600, 8, 1, 0, 0);
                        flag = 16;
                        break;
                    case 17:
                        loadProfileUpdate("lnt_dlms", 9600, 8, 1, 0, 0);
                        flag = 17;
                        break;
                    case 18:
                        loadProfileUpdate("genus_dlms", 9600, 8, 1, 0, 0);
                        flag = 18;
                        break;
                    case 19:
                        loadProfileUpdate("emco_dlms", 9600, 8, 1, 0, 0);
                        flag = 19;
                        break;
                    case 20:
                        loadProfileUpdate("avon_dlms", 9600, 8, 1, 0, 0);
                        flag = 20;
                        break;
                    case 21:
                        loadProfileUpdate("visiontek_dlms", 9600, 8, 1, 0, 0);
                        flag = 21;
                        break;
                    case 22:
                        loadProfileUpdate("hpl_dlms", 9600, 8, 1, 0, 0);
                        flag = 22;
                        break;
                    case 23:
                        loadProfileUpdate("landis_dlms", 9600, 8, 1, 0, 0);
                        flag = 23;
                        break;
                    case 24:
                        loadProfileUpdate("visiontel_6lowpan", 9600, 8, 1, 0, 0);
                        flag = 24;
                        break;

                    case 25:
                        loadProfileUpdate("secure_dlms", 9600, 8, 1, 0, 0);
                        flag = 25;
                        break;
                    default:
                        flag = 0;
                        Toast.makeText(context, " No Profile Found ", Toast.LENGTH_SHORT).show();
                }
            } else if (arg1.getAction().equals(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED)) {
                closeApplication("USB Permission Not Granted");

            } else if (arg1.getAction().equals(UsbService.ACTION_NO_USB)) {
                closeApplication("No USB Detected");

            } else if (arg1.getAction().equals(UsbService.ACTION_USB_DISCONNECTED)) {
                MainActivity.this.serialDataFragment.reset();
                closeApplication("Serial Port disconnected");
                MainActivity.this.terminalFragment.clearTerminal();
                MainActivity.this.terminalFragment.closeKeyboard();

                if (DroidTermBuild.TYPE == BUILD_TYPE.LITE) {
                    MainActivity.this.serialDataFragment.setUsbDevice("DroidTerm");
                } else {
                    MainActivity.this.serialDataFragment.setUsbDevice("DroidTerm PRO");
                }
//                MainActivity.this.representationMode.setTitle(MainActivity.HEX_MODE);
                // MainActivity.this.showDialog(MainDialogFragment.CLASS_ID, null, true);
            } else if (arg1.getAction().equals(UsbService.ACTION_USB_NOT_SUPPORTED)) {
                int pid2 = arg1.getExtras().getInt(UsbService.PID_NOT_SUPPORTED);
                int vid2 = arg1.getExtras().getInt(UsbService.VID_NOT_SUPPORTED);
                UsbData usbNotSupportedData = MainActivity.this.dataProvider.lookup(HexData.hex4digits(Integer.toHexString(vid2)), HexData.hex4digits(Integer.toHexString(pid2)));
                if (usbNotSupportedData != null) {
                    String productName;
                    String vendorName;
                    if (usbNotSupportedData.getProductName().equals("None")) {
                        productName = "Unknown device";
                    } else {
                        productName = usbNotSupportedData.getProductName();
                    }
                    if (usbNotSupportedData.getVendorName().equals("None")) {
                        vendorName = "Unknown vendor";
                    } else {
                        vendorName = usbNotSupportedData.getVendorName();
                    }
                    // MainActivity.this.showDialog(UsbNotCompatibleFragment.CLASS_ID, "Usb Device not supported: " + productName + System.getProperty("line.separator") + "Vendor: " + vendorName, true);
                } else {
                    // MainActivity.this.showDialog(UsbNotCompatibleFragment.CLASS_ID, "Usb Device not supported: " + System.getProperty("line.separator") + "VID: " + HexData.hex4digits(Integer.toHexString(vid2)) + " PID: " + HexData.hex4digits(Integer.toHexString(pid2)), true);
                }
                closeApplication("Unknow error");
                //MainActivity.this.stopService(UsbService.class, MainActivity.this.usbConnection);
            } else if (arg1.getAction().equals(UsbService.ACTION_USB_READY)) {
                // Toast.makeText(arg0, "Serial Port ready 198", Toast.LENGTH_SHORT).show();

                switch (flag) {
                    case 1:
                        MainActivity.this.terminalFragment.elloyedClick();
                        break;
                    case 2:
                        MainActivity.this.terminalFragment.LnTClick();
                        break;
                    case 3:
                        MainActivity.this.terminalFragment.secureClick();
                        break;
                    case 4:
                        MainActivity.this.terminalFragment.genusClick();
                        break;
                    case 5:
                        MainActivity.this.terminalFragment.palmohanClick();
                        break;
                    case 6:
                        MainActivity.this.terminalFragment.genusOPTClick();
                        break;
                    case 7:
                        MainActivity.this.terminalFragment.lpr54382Click();
                        break;
                    case 8:
                        MainActivity.this.terminalFragment.lpr43580Click();
                        break;
                    case 9:
                        MainActivity.this.terminalFragment.lpr09682Click();
                        break;
                    case 10:
                        MainActivity.this.terminalFragment.visionTekClick();
                        break;
                    case 11:
                        MainActivity.this.terminalFragment.montelClick();
                        break;
                    case 12:
                        MainActivity.this.terminalFragment.avonClick();
                        break;
                    case 13:
                        MainActivity.this.terminalFragment.bentekClick();
                        break;
                    case 14:
                        MainActivity.this.terminalFragment.avonOptClick();
                        break;
                    case 15:
                        MainActivity.this.terminalFragment.alloyedOptClick();
                        break;
                    case 16:
                        MainActivity.this.terminalFragment.avon3PhClick();
                        break;
                    case 17:
                        MainActivity.this.terminalFragment.dlmsClick();
                        break;
                    case 18:
                        MainActivity.this.terminalFragment.dlmsClick();
                        break;
                    case 19:
                        MainActivity.this.terminalFragment.dlmsClick();
                        break;
                    case 20:
                        MainActivity.this.terminalFragment.dlmsClick();
                        break;
                    case 21:
                        MainActivity.this.terminalFragment.dlmsClick();
                        break;
                    case 22:
                        MainActivity.this.terminalFragment.dlmsClick();
                        break;
                    case 23:
                        MainActivity.this.terminalFragment.dlmsClick();
                        break;
                    case 24:
                        MainActivity.this.terminalFragment.visiontek6LowPanClick();
                        break;
                    case 25:
                        MainActivity.this.terminalFragment.dlmsClick();
                        break;
                }
            } else if (arg1.getAction().equals("com.felhr..connectivityservices.LOG_SAVED")) {
                Toast.makeText(arg0, "Log saved", Toast.LENGTH_SHORT).show();
            } else if (arg1.getAction().equals("com.felhr..connectivityservices.LOG_NOT_SAVED")) {
                Toast.makeText(arg0, "Log could not be saved", Toast.LENGTH_SHORT).show();
            } else if (arg1.getAction().equals(UsbService.ACTION_CDC_DRIVER_NOT_WORKING) || arg1.getAction().equals(UsbService.ACTION_USB_DEVICE_NOT_WORKING)) {
                String error_message = "";
                if (arg1.getAction().equals(UsbService.ACTION_CDC_DRIVER_NOT_WORKING)) {
                    error_message = "CDC driver not working correctly";
                } else {
                    // this.usbService.openSerialPort(config);
                    error_message = "Serial port could not be opened.\nPlease remove cable and connect again";
                }

                closeApplication(error_message);
            }
        }
    }

    class C01062 implements ServiceConnection {
        C01062() {
        }

        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            MainActivity.this.usbService = ((UsbBinder) arg1).getService();
            MainActivity.this.usbService.setHandler(MainActivity.this.mHandler);
            if (!MainActivity.this.usbService.SERIAL_MODE) {
                String[] vidPid = MainActivity.this.usbService.getVidPid();
                if (vidPid != null) {
                    UsbData data = MainActivity.this.dataProvider.lookup(vidPid[0], vidPid[1]);
                    if (data != null) {
                        UsbViewerData viewerData = MainActivity.this.usbService.getUsbData(data.getVendorName(), data.getProductName());
                        // MainActivity.this.showDialog(UsbViewerDialogFragment.CLASS_ID, null, true);
                        MainActivity.this.onDeviceViewer.sendDeviceData(viewerData);
                        return;
                    }
                    MainActivity.this.mHandler.obtainMessage(4).sendToTarget();
                    //MainActivity.this.stopService(UsbService.class, MainActivity.this.usbConnection);
                    closeApplication("Unknown error");
                    return;
                }
                // MainActivity.this.showDialog(UsbNotCompatibleFragment.CLASS_ID, "No Usb Connected", true);
//                Toast.makeText(context, "line 310", Toast.LENGTH_SHORT).show();
//                MainActivity.this.stopService(UsbService.class, MainActivity.this.usbConnection);

                closeApplication("Unknown error");
            }
        }

        public void onServiceDisconnected(ComponentName arg0) {
            MainActivity.this.usbService = null;
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            this.mActivity = new WeakReference(activity);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    TextView tv1 = (TextView) msg.obj;
                    tv1.setBackgroundColor(0);
                    tv1.setText("");
                    return;
                case 1:
                    TextView tv2 = (TextView) msg.obj;
                    tv2.setBackgroundColor(0);
                    tv2.setTextColor(-1);
                    return;
                case 3:
                    if (this.mActivity.get() != null) {
                        this.mActivity.get().terminalFragment.printText((String) msg.obj);
                        this.mActivity.get().serialDataFragment.lightIndicator();
                        return;
                    }
                    return;
                case 4:
                    Toast.makeText(this.mActivity.get().context, "UsbViewer not available. Restart DroidTerm with Internet available", Toast.LENGTH_LONG).show();
                    return;
                default:
                    return;
            }
        }
    }

    class C03193 implements UsbDbCallback {
        C03193() {
        }

        public void onDbOpenedFirstTime(boolean status) {
            if (status) {
                MainActivity.this.dbLoaded = true;
                Log.i("DroidTerm", "DB opened first time");
                synchronized (MainActivity.this.monitor) {
                    MainActivity.this.monitor.notify();
                }
                return;
            }
            MainActivity.this.dbLoaded = true;
            synchronized (MainActivity.this.monitor) {
                MainActivity.this.monitor.notify();
            }
            Log.i("DroidTerm", "DB could not be loaded first time");
        }

        public void onDbOpened() {
            Log.i("DroidTerm", "Db opened");
            MainActivity.this.dbLoaded = true;
            synchronized (MainActivity.this.monitor) {
                MainActivity.this.monitor.notify();
            }
        }

        public void onDbUpdated(String newVersion) {
            Log.i("DroidTerm", "Db updated with new version: " + newVersion);
            MainActivity.this.dbLoaded = true;
            synchronized (MainActivity.this.monitor) {
                MainActivity.this.monitor.notify();
            }
        }

        public void onBeginDbOperation(int operation) {
            switch (operation) {
                case 0:
                    MainActivity.this.dbOperation = 0;
                    return;
                case 1:
                    MainActivity.this.dbOperation = 1;
                    return;
                case 2:
                    MainActivity.this.dbOperation = 2;
                    return;
                default:
                    return;
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_droid);
        this.context = this;
        this.fm = getFragmentManager();
        this.dbLoaded = false;
        this.dbOperation = 0;
        this.monitor = new Object();
        this.terminalFragment = (TerminalFragment) getFragmentManager().findFragmentById(R.id.fragment1);
        this.serialDataFragment = (SerialDataFragment) getFragmentManager().findFragmentById(R.id.fragment2);
        this.mHandler = new MyHandler(this);
        this.terminalFragment.setHandler(this.mHandler);
        if (savedInstanceState != null) {
            this.savedState = savedInstanceState;
        }
        this.dataProvider = new UsbDataProvider(this, this.mCallback);
        this.profileProvider = new ProfileProvider(this);


    }

//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        this.inputMode = menu.add(0, 1, 0, "Input options");
//        menu.add(0, 2, 0, "Send bulk data");
//        menu.add(0, 3, 0, "Disconnect");
//        menu.add(0, 4, 0, "Clear screen");
//        this.representationMode = menu.add(0, 5, 0, this.representationValue);
//        menu.add(0, 6, 0, "Save log");
//        menu.add(0, 7, 0, "Save profile");
//        BUILD_TYPE build_type = DroidTermBuild.TYPE;
//        BUILD_TYPE build_type2 = DroidTermBuild.TYPE;
//        if (build_type != BUILD_TYPE.LITE) {
//            menu.add(0, 8, 0, "Character Encoding");
//            menu.add(0, 9, 0, "About");
//        } else {
//            menu.add(0, 8, 0, "About");
//        }
//        return true;
//    }

//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == 1) {
////            showDialog(InputFragment.CLASS_ID, null, true);
//        } else if (item.getItemId() == 2) {
////            showDialog(SendDataDialogFragment.CLASS_ID, null, true);
//        } else if (item.getItemId() == 3) {
//            if (UsbService.SERVICE_CONNECTED) {
//                try {
//                    stopService(UsbService.class, this.usbConnection);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//            this.terminalFragment.clearTerminal();
//            this.terminalFragment.closeKeyboard();
//            if (DroidTermBuild.TYPE == BUILD_TYPE.LITE) {
//                this.serialDataFragment.setUsbDevice("DroidTerm");
//            } else {
//                this.serialDataFragment.setUsbDevice("DroidTerm PRO");
//            }
//            this.representationMode.setTitle(HEX_MODE);
//            showDialog(MainDialogFragment.CLASS_ID, null, true);
//            Toast.makeText(context, "line 467", Toast.LENGTH_SHORT).show();
//        } else if (item.getItemId() == 4) {
//            this.terminalFragment.clearTerminal();
//        } else if (item.getItemId() == 5) {
//            if (item.getTitle().equals(HEX_MODE)) {
//                this.terminalFragment.showHexValues();
//                if (UsbService.SERVICE_CONNECTED) {
//                    this.usbService.setPresentationMode("com.felhr.connectivityservices.MODE_HEX");
//                }
//                item.setTitle(STRING_MODE);
//            } else if (item.getTitle().equals(STRING_MODE)) {
//                this.terminalFragment.showString();
//                if (UsbService.SERVICE_CONNECTED) {
//                    this.usbService.setPresentationMode("com.felhr.connectivityservices.MODE_STRING");
//                }
//                item.setTitle(HEX_MODE);
//            }
//        } else if (item.getItemId() == 6) {
//            if (UsbService.SERVICE_CONNECTED) {
//                this.usbService.saveLog(this.terminalFragment.getText());
//            }
//        } else if (item.getItemId() == 7) {
//            showDialog(SaveProfileDialogFragment.CLASS_ID, null, true);
//            Toast.makeText(context, "line 490", Toast.LENGTH_SHORT).show();
//        } else if (item.getItemId() == 8) {
//            BUILD_TYPE build_type = DroidTermBuild.TYPE;
//            BUILD_TYPE build_type2 = DroidTermBuild.TYPE;
//            if (build_type != BUILD_TYPE.LITE) {
//                showDialog(EncodingDialogFragment.CLASS_ID, null, true);
//                Toast.makeText(context, "line 496", Toast.LENGTH_SHORT).show();
//            } else {
//                showDialog(AboutDialogFragment.CLASS_ID, null, true);
//                Toast.makeText(context, "line 499", Toast.LENGTH_SHORT).show();
//            }
//        } else if (item.getItemId() == 9) {
//            showDialog(AboutDialogFragment.CLASS_ID, null, true);
//            Toast.makeText(context, "line 503", Toast.LENGTH_SHORT).show();
//        }
//        return false;
//    }

    public void onResume() {
        super.onResume();
        setFilters();
        if (UsbService.SERVICE_CONNECTED) {
            bindService(new Intent(this, UsbService.class), this.usbConnection, Context.BIND_AUTO_CREATE);
        }
        if (this.currentDialog.equals(NO_DIALOG) && !UsbService.SERIAL_PORT_CONNECTED) {
            if (this.usbService != null) {
                try {
                    stopService(UsbService.class, this.usbConnection);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            this.terminalFragment.clearTerminal();
            if (DroidTermBuild.TYPE == BUILD_TYPE.LITE) {
                this.serialDataFragment.setUsbDevice("DroidTerm");
            } else {
                this.serialDataFragment.setUsbDevice("DroidTerm PRO");
            }
            this.currentDialog = MainDialogFragment.CLASS_ID;
        }
        closeDialogs();
        if (this.savedState != null) {
            this.currentDialog = this.savedState.getString(CURRENT_DIALOG);
//            this.representationValue = this.savedState.getString(REPRESENTATION_MODE);
            if (this.currentDialog == MainDialogFragment.CLASS_ID) {
                showDialog(MainDialogFragment.CLASS_ID, null, true);
                Toast.makeText(context, "line 532", Toast.LENGTH_SHORT).show();
            }
//            else if (this.currentDialog == UsbNotCompatibleFragment.CLASS_ID) {
////                showDialog(UsbNotCompatibleFragment.CLASS_ID, this.savedState.getString(NO_USB_DIALOG_MESSAGE), true);
//            }
//
            else if (this.currentDialog == ConfigurationDialogFragment.CLASS_ID) {
//                showDialog(ConfigurationDialogFragment.CLASS_ID, null, true);
            } else if (this.currentDialog != BluetoothDevicesDialogFragment.CLASS_ID) {
                if (this.currentDialog == AboutDialogFragment.CLASS_ID) {
                    showDialog(AboutDialogFragment.CLASS_ID, null, true);
                    Toast.makeText(context, "line 540", Toast.LENGTH_SHORT).show();
                } else if (this.currentDialog == FileBrowserDialogFragment.CLASS_ID) {
                    showDialog(FileBrowserDialogFragment.CLASS_ID, null, true);
                    Toast.makeText(context, "line 543", Toast.LENGTH_SHORT).show();
                } else if (this.currentDialog == LogViewerDialogFragment.CLASS_ID) {
                    showDialog(LogViewerDialogFragment.CLASS_ID, null, true);
                    Toast.makeText(context, "line 546", Toast.LENGTH_SHORT).show();
                    this.logViewerData = this.savedState.getString(LOG_VIEWER_DATA);
                    this.logViewerComm.sendLogData(this.logViewerData);
                } else if (this.currentDialog == ProfilesDialogFragment.CLASS_ID) {
                    showDialog(ProfilesDialogFragment.CLASS_ID, null, true);
                    Toast.makeText(context, "line 551", Toast.LENGTH_SHORT).show();
                }
            }
            String vendorId = this.savedState.getString(UsbData.VENDOR_ID);
            if (vendorId != null) {
                this.usbData = new UsbData(vendorId, this.savedState.getString(UsbData.VENDOR_NAME), this.savedState.getString(UsbData.PRODUCT_ID), this.savedState.getString(UsbData.PRODUCT_NAME));
            }
            if (this.profilesFragment != null) {
                this.profiles = this.profileProvider.lookup(this.usbData.getVendorId(), this.usbData.getProductId());
                this.profilesFragment.populateList(this.profiles);
            }
            this.savedState = null;
        } else if (this.currentDialog.equals(MainDialogFragment.CLASS_ID)) {
//            showDialog(MainDialogFragment.CLASS_ID, null, true);
            savedState = new Bundle();
            savedState.putString(UsbService.MODE, UsbService.MODE_SERIAL);
            startService(UsbService.class, this.usbConnection, savedState);

        } else if (this.currentDialog.equals(UsbNotCompatibleFragment.CLASS_ID)) {
            this.currentDialog = MainDialogFragment.CLASS_ID;
//            showDialog(MainDialogFragment.CLASS_ID, null, true);
        } else if (this.currentDialog.equals(ConfigurationDialogFragment.CLASS_ID)) {
//            showDialog(ConfigurationDialogFragment.CLASS_ID, null, true);
        } else if (this.currentDialog == BluetoothDevicesDialogFragment.CLASS_ID) {
        } else {
            if (this.currentDialog == AboutDialogFragment.CLASS_ID) {
                showDialog(AboutDialogFragment.CLASS_ID, null, true);
                Toast.makeText(context, "line 582", Toast.LENGTH_SHORT).show();
            } else if (this.currentDialog == FileBrowserDialogFragment.CLASS_ID) {
                showDialog(FileBrowserDialogFragment.CLASS_ID, null, true);
                Toast.makeText(context, "line 585", Toast.LENGTH_SHORT).show();
            } else if (this.currentDialog == LogViewerDialogFragment.CLASS_ID) {
                showDialog(LogViewerDialogFragment.CLASS_ID, null, true);
                Toast.makeText(context, "line 587", Toast.LENGTH_SHORT).show();
                this.logViewerComm.sendLogData(this.logViewerData);
            } else if (this.currentDialog == ProfilesDialogFragment.CLASS_ID) {
                showDialog(ProfilesDialogFragment.CLASS_ID, null, true);
                Toast.makeText(context, "line 592", Toast.LENGTH_SHORT).show();
                this.profilesFragment.populateList(this.profiles);
            }
        }
    }

    public void onPause() {
        super.onPause();
        if (!this.dbLoaded) {
            long waitTime = 0;
            if (this.dbOperation == 0 || this.dbOperation == 2) {
                waitTime = 0;
            } else if (this.dbOperation == 1) {
                waitTime = 800;
            }
            synchronized (this.monitor) {
                try {
                    this.monitor.wait(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        unregisterReceiver(this.mUsbReceiver);
        if (UsbService.SERVICE_CONNECTED) {
            unbindService(this.usbConnection);
        }
        closeDialogs();
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.dataProvider != null) {
            this.dataProvider.close();
        }
        if (this.profileProvider != null) {
            this.profileProvider.close();
        }
    }

    public void onBackPressed() {
        if (UsbService.SERVICE_CONNECTED) {
            try {
                stopService(UsbService.class, this.usbConnection);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //stopService(UsbService.class, this.usbConnection);
            GSBilling.getInstance().setCodeMetre(0);
            Intent intentsecure = new Intent();
            intentsecure.putExtra("Data", " Unable to Read  ");

            MainActivity.this.setResult(0, intentsecure);
            MainActivity.this.finish();//finishing activity
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(CURRENT_DIALOG, this.currentDialog);
        if (this.currentDialog == UsbNotCompatibleFragment.CLASS_ID) {
            savedInstanceState.putString(NO_USB_DIALOG_MESSAGE, this.usbNotCompatibleFragment.getMessage());
        }
        if (this.usbData != null) {
            savedInstanceState.putAll(this.usbData.getBundledData());
        }
        if (this.logViewerData != null) {
            savedInstanceState.putString(LOG_VIEWER_DATA, this.logViewerData);
        }
//        if (!(this.representationMode == null || this.representationMode.getTitle() == null)) {
//            savedInstanceState.putString(REPRESENTATION_MODE, this.representationMode.getTitle().toString());
//        }
        super.onSaveInstanceState(savedInstanceState);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_READY);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        filter.addAction("com.felhr..connectivityservices.LOG_SAVED");
        filter.addAction("com.felhr..connectivityservices.LOG_NOT_SAVED");
        filter.addAction(UsbService.ACTION_CDC_DRIVER_NOT_WORKING);
        filter.addAction(UsbService.ACTION_USB_DEVICE_NOT_WORKING);
        registerReceiver(this.mUsbReceiver, filter);
    }

    private void showDialog(String dialogTag, String message, boolean restore) {
        FragmentTransaction ft = this.fm.beginTransaction();
        int count = this.fm.getBackStackEntryCount();
        if (!dialogTag.equals(AboutDialogFragment.CLASS_ID)) {
            if (count > 0) {
                this.fm.popBackStack();
            }
            ft.addToBackStack(null);
        }
        if (dialogTag.equals(MainDialogFragment.CLASS_ID)) {
            this.mainDialogFragment = new MainDialogFragment();
            this.mainDialogFragment.setCancelable(false);
            this.currentDialog = MainDialogFragment.CLASS_ID;
            this.mainDialogFragment.show(ft, MainDialogFragment.CLASS_ID);
        } else if (dialogTag.equals(UsbNotCompatibleFragment.CLASS_ID)) {
            this.usbNotCompatibleFragment = new UsbNotCompatibleFragment();
            this.usbNotCompatibleFragment.setCancelable(false);
            this.noUsbCommunicator = this.usbNotCompatibleFragment;
            this.currentDialog = UsbNotCompatibleFragment.CLASS_ID;
            this.usbNotCompatibleFragment.show(ft, UsbNotCompatibleFragment.CLASS_ID);
            if (message != null) {
                this.noUsbCommunicator.sendMessage(message);
            }
        } else if (dialogTag.equals(ConfigurationDialogFragment.CLASS_ID)) {
            this.configurationFragment = new ConfigurationDialogFragment();
            this.configurationFragment.setCancelable(false);
            this.currentDialog = ConfigurationDialogFragment.CLASS_ID;
            this.configurationFragment.show(ft, ConfigurationDialogFragment.CLASS_ID);
        } else if (!dialogTag.equals(BluetoothDevicesDialogFragment.CLASS_ID)) {
            if (dialogTag.equals(AboutDialogFragment.CLASS_ID)) {
                this.aboutFragment = new AboutDialogFragment();
                this.aboutFragment.show(ft, AboutDialogFragment.CLASS_ID);
            } else if (dialogTag.equals(UsbViewerDialogFragment.CLASS_ID)) {
                this.usbViewerFragment = new UsbViewerDialogFragment();
                this.usbViewerFragment.setCancelable(false);
                this.currentDialog = UsbViewerDialogFragment.CLASS_ID;
                this.onDeviceViewer = this.usbViewerFragment;
                this.usbViewerFragment.show(ft, UsbViewerDialogFragment.CLASS_ID);
            } else if (dialogTag.equals(FileBrowserDialogFragment.CLASS_ID)) {
                this.fileBrowserFragment = new FileBrowserDialogFragment();
                this.fileBrowserFragment.setCancelable(false);
                this.currentDialog = FileBrowserDialogFragment.CLASS_ID;
                this.fileBrowserFragment.show(ft, FileBrowserDialogFragment.CLASS_ID);
            } else if (dialogTag.equals(LogViewerDialogFragment.CLASS_ID)) {
                this.logViewerFragment = new LogViewerDialogFragment();
                this.logViewerComm = this.logViewerFragment;
                this.logViewerFragment.setCancelable(false);
                this.currentDialog = LogViewerDialogFragment.CLASS_ID;
                this.logViewerFragment.show(ft, LogViewerDialogFragment.CLASS_ID);
            } else if (dialogTag.equals(SendDataDialogFragment.CLASS_ID)) {
                this.sendDataFragment = new SendDataDialogFragment();
                this.sendDataFragment.setCancelable(false);
                this.sendDataFragment.show(ft, SendDataDialogFragment.CLASS_ID);
            } else if (dialogTag.equals(ProfilesDialogFragment.CLASS_ID)) {
                this.profilesFragment = new ProfilesDialogFragment();
                this.profilesFragment.setCancelable(true);
                this.currentDialog = ProfilesDialogFragment.CLASS_ID;
                this.profilesFragment.show(ft, ProfilesDialogFragment.CLASS_ID);
            } else if (dialogTag.equals(SaveProfileDialogFragment.CLASS_ID)) {
                this.saveProfileFragment = new SaveProfileDialogFragment();
                this.saveProfileFragment.setCancelable(true);
                this.saveProfileFragment.show(ft, SaveProfileDialogFragment.CLASS_ID);
            } else if (dialogTag.equals(InputFragment.CLASS_ID)) {
                this.inputFragment = new InputFragment();
                this.inputFragment.setCancelable(false);
                this.inputFragment.show(ft, InputFragment.CLASS_ID);
            } else if (dialogTag.equals(EncodingDialogFragment.CLASS_ID)) {
                this.encodingFragment = new EncodingDialogFragment();
                this.encodingFragment.setCancelable(false);
                this.encodingFragment.show(ft, EncodingDialogFragment.CLASS_ID);
            }
        }
        this.fm.executePendingTransactions();
    }

    private void closeDialogs() {
        if (this.fm.getBackStackEntryCount() > 0) {
            Fragment df = this.fm.findFragmentById(this.fm.getBackStackEntryAt(this.fm.getBackStackEntryCount() - 1).getId());
            this.fm.popBackStack();
            if (df != null) {
                ((DialogFragment) df).dismiss();
            }
        }
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        Intent startService = new Intent(this, service);
        if (!(extras == null || extras.isEmpty())) {
            for (String key : extras.keySet()) {
                startService.putExtra(key, extras.getString(key));
            }
        }
        startService(startService);
        bindService(new Intent(this, service), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void stopService(Class<?> service, ServiceConnection serviceConnection) throws Exception {

        unbindService(serviceConnection);
        stopService(new Intent(this, service));

    }

    public void getButtonPressed(int buttonId) {
        switch (buttonId) {
            case R.id.button_usb:
//                Bundle bundleSerial = new Bundle();
//                bundleSerial.putString(UsbService.MODE, UsbService.MODE_SERIAL);
//                startService(UsbService.class, this.usbConnection, bundleSerial);
                return;
            case R.id.button_viewer:
                Bundle bundleViewer = new Bundle();
                bundleViewer.putString(UsbService.MODE, UsbService.MODE_VIEWER);
                startService(UsbService.class, this.usbConnection, bundleViewer);
                return;
            case R.id.button_log_viewer:
                showDialog(FileBrowserDialogFragment.CLASS_ID, null, true);
                Toast.makeText(context, "line 786", Toast.LENGTH_SHORT).show();
                return;
            case R.id.button_droidterm_pro:
                try {
                    startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.felhr.droidtermpro")));
                    return;
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=com.felhr.droidtermpro")));
                    return;
                }
            case R.id.button_about:
                showDialog(AboutDialogFragment.CLASS_ID, null, true);
                Toast.makeText(context, "line 798", Toast.LENGTH_SHORT).show();
                return;
            default:
                return;
        }
    }

    public void getButtonPressed() {
        this.currentDialog = MainDialogFragment.CLASS_ID;
        showDialog(MainDialogFragment.CLASS_ID, null, true);
        Toast.makeText(context, "line 808", Toast.LENGTH_SHORT).show();
    }

    public void sendConfiguration(Bundle config) {
        this.configDataCurrentSession = config;
        closeDialogs();
        this.currentDialog = NO_DIALOG;
        if (this.usbData == null || this.usbData.getProductName().equals("None")) {
            this.serialDataFragment.setUsbDevice("Usb Serial Device");
            config.putString(LogGenerator.DEVICE_NAME, "Usb Serial Device");
        } else {
            this.serialDataFragment.setUsbDevice(this.usbData.getProductName());
            config.putString(LogGenerator.DEVICE_NAME, this.usbData.getProductName());
        }
        this.usbService.openSerialPort(config);
    }

    public void write(byte[] data) {
        if (UsbService.SERVICE_CONNECTED) {
            this.usbService.write(data);
            if (this.serialDataFragment.isEchoOn()) {
                this.terminalFragment.printText(data);

                Log.e("MainActivity", "Response data: " + data.toString());
            }
        }
    }

    public void backToMainMenuFromViewer() {
        try {
            stopService(UsbService.class, this.usbConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }

        closeDialogs();
        showDialog(MainDialogFragment.CLASS_ID, null, true);
        Toast.makeText(context, "line 840", Toast.LENGTH_SHORT).show();
    }

    public void backToMainMenuFromBrowser() {
        closeDialogs();
        showDialog(MainDialogFragment.CLASS_ID, null, true);
        Toast.makeText(context, "line 846", Toast.LENGTH_SHORT).show();
    }

    public void goToLogViewer(String text) {
        showDialog(LogViewerDialogFragment.CLASS_ID, null, true);
        this.logViewerData = text;
        this.logViewerComm.sendLogData(text);
    }

    public void backToFileBrowser() {
        showDialog(FileBrowserDialogFragment.CLASS_ID, null, true);
    }

    public void sendData(byte[] data) {
        this.usbService.write(data);
        if (this.serialDataFragment.isEchoOn()) {
            this.terminalFragment.printText(data);
        }
    }

    public void backToTerminal() {
        closeDialogs();
    }

    public void loadProfile(Profile profile) {
        Bundle bundle = new Bundle();
        bundle.putString(UsbService.CONFIG_PROFILE, profile.getProfileName());
        bundle.putInt(UsbService.CONFIG_BAUDRATE, profile.getBaudRate());
        bundle.putInt(UsbService.CONFIG_DATA_BITS, profile.getDataBits());
        bundle.putInt(UsbService.CONFIG_STOP_BITS, profile.getStopBits());
        bundle.putInt(UsbService.CONFIG_PARITY, profile.getParity());
        bundle.putInt(UsbService.CONFIG_FLOW, profile.getFlowControl());
        sendConfiguration(bundle);
        closeDialogs();
    }

    public void loadProfileUpdate(String pro, int Baud, int databits, int stopbits, int parity, int flow) {
        Bundle bundle = new Bundle();
        bundle.putString(UsbService.CONFIG_PROFILE, pro);
        bundle.putInt(UsbService.CONFIG_BAUDRATE, Baud);
        bundle.putInt(UsbService.CONFIG_DATA_BITS, databits);
        bundle.putInt(UsbService.CONFIG_STOP_BITS, stopbits);
        bundle.putInt(UsbService.CONFIG_PARITY, parity);
        bundle.putInt(UsbService.CONFIG_FLOW, flow);
        sendConfiguration(bundle);
        closeDialogs();
    }

    public void backToMainMenu() {
        try {
            stopService(UsbService.class, this.usbConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }

        showDialog(MainDialogFragment.CLASS_ID, null, true);
    }

    public void newConfig() {
        showDialog(ConfigurationDialogFragment.CLASS_ID, null, true);
        Toast.makeText(context, "line 903", Toast.LENGTH_SHORT).show();
    }

    public void back() {
        closeDialogs();
    }

    public void saveProfile(String profileName) {
        if (profileName.length() > 0) {
            Profile profile = new Profile(profileName, this.usbData.getVendorId(), this.usbData.getProductId(), this.configDataCurrentSession.getInt(UsbService.CONFIG_BAUDRATE), this.configDataCurrentSession.getInt(UsbService.CONFIG_DATA_BITS), this.configDataCurrentSession.getInt(UsbService.CONFIG_STOP_BITS), this.configDataCurrentSession.getInt(UsbService.CONFIG_PARITY), this.configDataCurrentSession.getInt(UsbService.CONFIG_FLOW));
            if (this.profileProvider.insertProfile(profile)) {
                closeDialogs();
                this.usbService.startAutoLog(profile.getProfileName());
                Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Profile Name not valid", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Profile name not set", Toast.LENGTH_SHORT).show();
    }

    public void configInput(int id) {
        this.terminalFragment.setInput(id);
        closeDialogs();
    }

    public void requestFocus() {
        this.serialDataFragment.requestFocus();
    }

    public void configEncoding(int id) {
        this.usbService.configureEncoding(id);
        this.terminalFragment.configureEncoding(id);
        closeDialogs();
    }

    /**
     * close the app when
     * Serial port disconnected
     */
    public void closeApplication(String error_message) {
        try {
            MainActivity.this.stopService(UsbService.class, MainActivity.this.usbConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        GSBilling.getInstance().setCodeMetre(0);
        Intent intentsecure = new Intent();
        intentsecure.putExtra("Data", error_message);
        MainActivity.this.setResult(0, intentsecure);
        MainActivity.this.finish();//finishing activity

    }
}
