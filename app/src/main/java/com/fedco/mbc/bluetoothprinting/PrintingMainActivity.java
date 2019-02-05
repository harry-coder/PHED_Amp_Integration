package com.fedco.mbc.bluetoothprinting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.BillingtypesActivity;
import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.bluetoothprinting.bluetooth.BluetoothComm;
import com.fedco.mbc.bluetoothprinting.bluetooth.BluetoothPair;
import com.fedco.mbc.model.Structbilling;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.AndroidBmpUtil;
import com.fedco.mbc.utils.SweetAlertDialog.SweetAlertDialogBox;
import com.fedco.mbc.utils.UtilAppCommon;
import com.prowesspride.api.Printer_GEN;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * The main interface <br />
 *   * Maintain a connection with the Bluetooth communication operations,
 * check Bluetooth status after the first entry, did not start then turn on Bluetooth,
 * then immediately into the search interface. <br/>
 *   * The need to connect the device to get built on the main interface paired with a connection,
 * Bluetooth object is stored in globalPool so that other functional modules of different
 * communication modes calls.
 */
public class PrintingMainActivity extends Activity {
    /**
     * CONST: scan device menu id
     */
    SweetAlertDialogBox sweetAlertDialogBox;
    private String sBarcodestr, sAdddatastr;
    private GlobalPool mGP = null;
    public  static BluetoothAdapter mBT = BluetoothAdapter.getDefaultAdapter();
    public  static BluetoothDevice mBDevice = null;
    private TextView mtvDeviceInfo = null;
    private TextView mtvServiceUUID = null;
    private TextView tvScanbt;
    private LinearLayout mllDeviceCtrl = null;
    private Button btnPair = null;
    private Button btnComm = null;
    private Button btnBack, btnScanbt, btnContinue;
    public  static final byte REQUEST_DISCOVERY = 0x01;
    public  static final byte REQUEST_ABOUT = 0x05;
    public  static final int EXIT_ON_RETURN = 21;
    private Hashtable<String, String> mhtDeviceInfo = new Hashtable<String, String>();
    private boolean mblBonded = false;
    public  static boolean blResetBtnEnable = false;
    public  final static String EXTRA_DEVICE_TYPE = "android.bluetooth.device.extra.DEVICE_TYPE";
    private boolean blBleStatusBefore = false;
    final   Context context = this;
    public  Dialog dlgRadioBtn, dlgSupport;
    public  static ProgressDialog prgDialog;
    private String sTo, sSubject, sMessage, sDevicetype;
    private EditText edtTo, edtSubject, edtMessage;
    private ScrollView svScroll, svRadio;
    private RadioGroup rgProtocol;
    private RadioButton rbtnProtocol;
    private Printer_GEN ptrGen;
    private static byte bFontstyle = (byte) 0x01;
    int     iRetVal;
    public static final int DEVICE_NOTCONNECTED = -100;
    public Dialog dlgCustomdialog;
    UtilAppCommon uc;
    ScaleAnimation scale;
    SQLiteDatabase SD;
    DB dbHelper;
    SessionManager session;
    /*ACTIVATE WHEN createBLACKANDWHITE FUNTION USED*/
    File compressSignature;// = Environment.getExternalStorageDirectory()+"/MBC"+"/signature.bmp";//+"/temp_photo.bmp";
//    String photoPath = Environment.getExternalStorageDirectory() + "/MBC/"+GSBilling.getInstance().getMR_LICENSE_KEY()+"_"+Structconsmas.Consumer_Number+"_mtr.jpg";
    String photoPath;
    String QRPath = Environment.getExternalStorageDirectory() + "/MBC/Images"
            + "/Image.bmp";
//    String signaturePath = Environment.getExternalStorageDirectory() + "/"+GSBilling.getInstance().getMR_LICENSE_KEY()+"_"+ Structconsmas.Consumer_Number+"_sig.JPEG";
    String signaturePath ;
//    String signaturePathDes = Environment.getExternalStorageDirectory() + "/MBC" + "/"+ GSBilling.getInstance().getMR_LICENSE_KEY()+"_"+Structconsmas.Consumer_Number+"_sig.bmp";
    String signaturePathDes ;
    String QRPathDes = Environment.getExternalStorageDirectory() + "/MBC/Images"
            + "/QRimage.bmp";
//    String photoPathDes = Environment.getExternalStorageDirectory() + "/MBC/"
//            + "/"+ GSBilling.getInstance().getMR_LICENSE_KEY()+"_"+Structconsmas.Consumer_Number+"_mtr.bmp";
    String photoPathDes ;
    String LicKey;

    private BroadcastReceiver _mPairingRequest = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = null;
            if (intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDED)
                    mblBonded = true;
                else
                    mblBonded = false;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        Log.e("activity", "MainActivity");
        dbHelper                = new DB(getApplicationContext());
        SD                      = dbHelper.getWritableDatabase();

        sweetAlertDialogBox     = new SweetAlertDialogBox(PrintingMainActivity.this);
        session                 = new SessionManager(getApplicationContext());
        LicKey                  = session.retLicence();

        svScroll                = (ScrollView) findViewById(R.id.scroll);
        svRadio                 = (ScrollView) findViewById(R.id.redioscroll);

        if (null == mBT) {
            Toast.makeText(this, "Bluetooth module not found", Toast.LENGTH_LONG).show();
            this.finish();
        }

        this.mtvDeviceInfo      = (TextView) this.findViewById(R.id.actMain_tv_device_info);
        this.mllDeviceCtrl      = (LinearLayout) this.findViewById(R.id.actMain_ll_device_ctrl);
        this.btnPair            = (Button) this.findViewById(R.id.actMain_btn_pair);
        this.btnComm            = (Button) this.findViewById(R.id.actMain_btn_conn);

        this.photoPath          = Environment.getExternalStorageDirectory() + "/MBC/"+LicKey+"_"+Structconsmas.Consumer_Number+"_mtr.jpg";
        this.signaturePath      = Environment.getExternalStorageDirectory() + "/"+LicKey+"_"+ Structconsmas.Consumer_Number+"_sig.JPEG";
        this.signaturePathDes   = Environment.getExternalStorageDirectory() + "/MBC" + "/"+ LicKey+"_"+Structconsmas.Consumer_Number+"_sig.bmp";
        this.photoPathDes       = Environment.getExternalStorageDirectory() + "/MBC/"+ "/"+ LicKey+"_"+Structconsmas.Consumer_Number+"_mtr.bmp";

        System.out.println("BEFORE BLUETOOTH DEVICE TASK IN ON CREATE");
        new StartBluetoothDeviceTask().execute("");

		/*scan bluetooth devices*/
        btnScanbt                 = (Button) findViewById(R.id.btnScanbt);
        final Animation animScale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);

        btnScanbt.startAnimation(animScale);
        this.mGP = ((GlobalPool) this.getApplicationContext());
        btnScanbt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                svScroll.setVisibility(View.GONE);
                svRadio.setVisibility(View.GONE);
                new StartBluetoothDeviceTask().execute("");
            }
        });

		/*scan bluetooth devices*/
        tvScanbt = (TextView) findViewById(R.id.tvScanbt);
        tvScanbt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                svScroll.setVisibility(View.GONE);
                svRadio.setVisibility(View.GONE);
                new StartBluetoothDeviceTask().execute("");
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mGP.closeConn();
        if (null != mBT && !this.blBleStatusBefore)
            mBT.disable();
    }

    private void dlgShowDeviceInfo() {
        if (this.mhtDeviceInfo.get("COD").equals("800614")) {
            this.mtvDeviceInfo.setText(String.format(getString(R.string.actMain_device_info),
                            this.mhtDeviceInfo.get("NAME"),
                            this.mhtDeviceInfo.get("MAC"),
                            this.mhtDeviceInfo.get("COD"),
                            this.mhtDeviceInfo.get("RSSI"),
                            this.mhtDeviceInfo.get("DEVICE_TYPE"),
                            this.mhtDeviceInfo.get("BOND"))
            );
        }
        else {
        }

    }

    private void showServiceUUIDs() {
        if (Build.VERSION.SDK_INT >= 15) {
        } else {
            this.mtvServiceUUID.setText(getString(R.string.actMain_msg_does_not_support_uuid_service));
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        svScroll.setVisibility(View.VISIBLE);
        if (requestCode == REQUEST_DISCOVERY) {
//			Log.e("cod", data.getStringExtra("COD"));
            if (Activity.RESULT_OK == resultCode) {
                // Device is selected
                if (data.getStringExtra("COD").equals("800614")) {
//					Log.e("cod", data.getStringExtra("COD"));
                    this.mllDeviceCtrl.setVisibility(View.VISIBLE);
                    this.mhtDeviceInfo.put("NAME", data.getStringExtra("NAME"));
                    this.mhtDeviceInfo.put("MAC", data.getStringExtra("MAC"));
                    this.mhtDeviceInfo.put("COD", data.getStringExtra("COD"));
                    this.mhtDeviceInfo.put("RSSI", data.getStringExtra("RSSI"));
                    this.mhtDeviceInfo.put("DEVICE_TYPE", data.getStringExtra("DEVICE_TYPE"));
                    this.mhtDeviceInfo.put("BOND", data.getStringExtra("BOND"));
                    this.dlgShowDeviceInfo();
                    try { // try catch added by me

                        if (this.mhtDeviceInfo.get("BOND").equals(getString(R.string.actDiscovery_bond_nothing))) {
                            //############################## By me ####################################
//													this.btnPair.setVisibility(View.VISIBLE); //hide by me
                            this.btnComm.setVisibility(View.GONE); //no any action
                            btnPair.setVisibility(View.GONE);//add by me
                            new PairTask().execute(this.mhtDeviceInfo.get("MAC"));//add by me
                        } else {
                            mBDevice = mBT.getRemoteDevice(this.mhtDeviceInfo.get("MAC"));
//													################### by me  ########################
                            btnPair.setVisibility(View.GONE); //no any action
                            this.btnComm.setVisibility(View.GONE);// add  by me
//													btnComm.setVisibility(View.VISIBLE); // hide by me
                            new ConnSocketTask().execute(mBDevice.getAddress());//add by me
                        }

                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                } else {
                }
            } else if (Activity.RESULT_CANCELED == resultCode) {
                // None of device is selected
                this.finish();
            }
        } else if (requestCode == EXIT_ON_RETURN) {
            finish();
        }
    }

    /**
     * Pairing button click event
     *
     * @return void
     */
    public void onClickBtnPair(View v) {
        Log.e("btnPair", "onClickPair");
        new PairTask().execute(this.mhtDeviceInfo.get("MAC"));

        this.btnPair.setEnabled(false);
    }

    /**
     * Connect button click event
     *
     * @return void
     */
    public void onClickBtnConn(View v) {
        new ConnSocketTask().execute(mBDevice.getAddress());
    }

    // Turn on Bluetooth of the device
    private class StartBluetoothDeviceTask extends AsyncTask<String, String, Integer> {
        private static final int RET_BULETOOTH_IS_START = 0x0001;
        private static final int RET_BLUETOOTH_START_FAIL = 0x04;
        private static final int miWATI_TIME = 15;
        private static final int miSLEEP_TIME = 150;
        private ProgressDialog mpd;
        /*SweetAlertDialog saDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Loading");
        saDialog.show();
        saDialog.setCancelable(false);*/

        @Override
        public void onPreExecute() {
            mpd = new ProgressDialog(PrintingMainActivity.this);
            mpd.setMessage(getString(R.string.actDiscovery_msg_starting_device));
            mpd.setCancelable(false);
            mpd.setCanceledOnTouchOutside(false);
            mpd.show();
            blBleStatusBefore = mBT.isEnabled();

        }

        @Override
        protected Integer doInBackground(String... arg0) {
            int iWait = miWATI_TIME * 1000;
            /* BT isEnable */
            if (!mBT.isEnabled()) {
                mBT.enable();
                //Wait miSLEEP_TIME seconds, start the Bluetooth device before you start scanning
                while (iWait > 0) {
                    if (!mBT.isEnabled())
                        iWait -= miSLEEP_TIME;
                    else
                        break;
                    SystemClock.sleep(miSLEEP_TIME);
                }
                if (iWait < 0)
                    return RET_BLUETOOTH_START_FAIL;
            }
            return RET_BULETOOTH_IS_START;
        }

        /**
         * After blocking clean up task execution
         */
        @Override
        public void onPostExecute(Integer result) {
            if (mpd.isShowing())
                mpd.dismiss();
            if (RET_BLUETOOTH_START_FAIL == result) {
                // Turning ON Bluetooth failed
                AlertDialog.Builder builder = new AlertDialog.Builder(PrintingMainActivity.this);
                builder.setTitle(getString(R.string.dialog_title_sys_err));
                builder.setMessage(getString(R.string.actDiscovery_msg_start_bluetooth_fail));
                builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBT.disable();
                        finish();
                    }
                });
                builder.create().show();
            } else if (RET_BULETOOTH_IS_START == result) {
                // Bluetooth of the device successfully started
                // Start the nearby device search
                System.out.println("IN POST ESLEIF GOING BLUETOOTHDISCOVERY BLUETOOTH DEVICE TASK IN ON CREATE");
                Intent intent = new Intent(PrintingMainActivity.this, BTDiscovery.class);
                PrintingMainActivity.this.startActivityForResult(intent, REQUEST_DISCOVERY);
            }
        }
    }

    /*   This method shows the PairTask  PairTask operation */
    private class PairTask extends AsyncTask<String, String, Integer> {
        /**
         * Constants: the pairing is successful
         */
        static private final int RET_BOND_OK = 0x00;
        /**
         * Constants: Pairing failed
         */
        static private final int RET_BOND_FAIL = 0x01;
        /**
         * Constants: Pairing waiting time (15 seconds)
         */
        static private final int iTIMEOUT = 1000 * 15;

        /**
         * Thread start initialization
         */
        @Override
        public void onPreExecute() {
            Toast.makeText(PrintingMainActivity.this, getString(R.string.actMain_msg_bluetooth_Bonding), Toast.LENGTH_SHORT).show();
            registerReceiver(_mPairingRequest, new IntentFilter(BluetoothPair.PAIRING_REQUEST));
            registerReceiver(_mPairingRequest, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        }

        /* Task of PairTask performing in the background*/
        @Override
        protected Integer doInBackground(String... arg0) {
            final int iStepTime = 150;
            int iWait = iTIMEOUT;
            try {
                mBDevice = mBT.getRemoteDevice(arg0[0]);//arg0[0] is MAC address
                BluetoothPair.createBond(mBDevice);
                mblBonded = false;
            } catch (Exception e1) {
                Log.d(getString(R.string.app_name), "create Bond failed!");
                e1.printStackTrace();
                return RET_BOND_FAIL;
            }
            while (!mblBonded && iWait > 0) {
                SystemClock.sleep(iStepTime);
                iWait -= iStepTime;
            }
            if (iWait > 0) {
                //RET_BOND_OK
                Log.e("Application", "create Bond failed! RET_BOND_OK ");
            } else {
                //RET_BOND_FAIL
                Log.e("Application", "create Bond failed! RET_BOND_FAIL ");
            }
            return (int) ((iWait > 0) ? RET_BOND_OK : RET_BOND_FAIL);
        }

        /* This displays the status messages of PairTask in the dialog box */
        @Override
        public void onPostExecute(Integer result) {
            unregisterReceiver(_mPairingRequest);
            if (RET_BOND_OK == result) {
                Toast.makeText(PrintingMainActivity.this, getString(R.string.actMain_msg_bluetooth_Bond_Success), Toast.LENGTH_SHORT).show();
                btnPair.setVisibility(View.GONE);
                btnComm.setVisibility(View.VISIBLE);
                mhtDeviceInfo.put("BOND", getString(R.string.actDiscovery_bond_bonded));
                dlgShowDeviceInfo();
                showServiceUUIDs();
            } else {
                Toast.makeText(PrintingMainActivity.this, getString(R.string.actMain_msg_bluetooth_Bond_fail), Toast.LENGTH_LONG).show();
                try {
                    BluetoothPair.removeBond(mBDevice);
                } catch (Exception e) {
                    Log.d(getString(R.string.app_name), "removeBond failed!");
                    e.printStackTrace();
                }
                Log.e("btnPair", "onpostExecute");
                btnPair.setEnabled(true);
                new ConnSocketTask().execute(mBDevice.getAddress());
            }
        }
    }

    /*   This method shows the connSocketTask  PairTask operation */
    private class ConnSocketTask extends AsyncTask<String, String, Integer> {
        /**
         * Process waits prompt box
         */
        private ProgressDialog mpd = null;
        /**
         * Constants: connection fails
         */
        private static final int CONN_FAIL = 0x01;
        /**
         * Constant: the connection is established
         */
        private static final int CONN_SUCCESS = 0x02;

        /**
         * Thread start initialization
         */
        @Override
        public void onPreExecute() {
            this.mpd = new ProgressDialog(PrintingMainActivity.this);
            this.mpd.setMessage(getString(R.string.actMain_msg_device_connecting));
            this.mpd.setCancelable(false);
            this.mpd.setCanceledOnTouchOutside(false);
            this.mpd.show();
        }

        /* Task of connSocketTask performing in the background*/
        @Override
        protected Integer doInBackground(String... arg0) {
            if (mGP.createConn(arg0[0]))
                return CONN_SUCCESS;
            else
                return CONN_FAIL;
        }

        /* This displays the status messages of connSocketTask in the dialog box */
        @Override
        public void onPostExecute(Integer result) {
            this.mpd.dismiss();
            if (CONN_SUCCESS == result) {
                btnComm.setVisibility(View.GONE);
//				dlgShow("BT Connection Established Successfully");//hide by mew
//				svRadio.setVisibility(View.VISIBLE);//hide by me
                try {
                    //Reading device serial number
                    sDevicetype = BTDiscovery.impressSetUp.sGetDevSerNo(BluetoothComm.mosOut, BluetoothComm.misIn);
                    Log.e("Setup ", "DEVICE TYPE.........>" + sDevicetype);
                    sDevicetype = sDevicetype.substring(0, 2);
                    Log.e("Setup ", "DEVICE TYPE.........>" + sDevicetype);
                } catch (NullPointerException e) {
                    // TODO: handle exception
                } catch (IndexOutOfBoundsException e) {
                    // TODO: handle exception
                }
//				Added by me
                try {
                    if (mGP.connection == true) {
                        /*Intent protocol8a = new Intent(MainActivity.this,GeneralPrinterActivity.class);
						startActivityForResult(protocol8a, EXIT_ON_RETURN);*/
                        EnterTextAsyc asynctask = new EnterTextAsyc();
                        asynctask.execute(0);
                        //EnterText();
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    String str = "Please Select a Protocol";
                   /* EnterTextAsyc asynctask = new EnterTextAsyc();
                    asynctask.execute(0);*/
                    dlgShow(str);

                }

//				addListenerOnButton(); hide by me

            } else {
                Toast.makeText(PrintingMainActivity.this, getString(R.string.actMain_msg_device_connect_fail), Toast.LENGTH_SHORT).show();
            }
        }
    }


    /* Handler to display UI response messages   */
    Handler ptrHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    try {
                        TextView tvMessage = (TextView) dlgCustomdialog.findViewById(R.id.tvMessage);
                        tvMessage.setText("" + msg.obj);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    break;
                case 2:
                    String str1 = (String) msg.obj;
                    dlgShow(str1);
                    break;

                default:
                    break;
            }
        }

        ;
    };

    /*   This method shows the EnterTextAsyc  AsynTask operation */
    public class EnterTextAsyc extends AsyncTask<Integer, Integer, Integer> {
        /* displays the progress dialog until background task is completed*/
        @Override
        protected void onPreExecute() {
            // ProgressDialog(context, "Please Wait....");
            prgDialog = new ProgressDialog(context);
            prgDialog.setMessage("Please Wait ....");
            prgDialog.setIndeterminate(true);
            prgDialog.setCancelable(false);
            prgDialog.show();

            try {

                OutputStream outSt = BluetoothComm.mosOut;
                InputStream inSt = BluetoothComm.misIn;
                ptrGen = new Printer_GEN(BTDiscovery.impressSetUp, outSt, inSt);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /* Task of EnterTextAsyc performing in the background*/
        @Override
        protected Integer doInBackground(Integer... params) {
//            getdata();
            try {
                AndroidBmpUtil.convertJpeg2Bmp(getApplicationContext(), photoPath, photoPathDes);
                AndroidBmpUtil.convertJpeg2Bmp(getApplicationContext(), QRPath, QRPathDes);
                AndroidBmpUtil.convertJpeg2Bmp(getApplicationContext(), signaturePath, signaturePathDes);
////                photoPathDes =new File(Environment.getExternalStorageDirectory()+"/MBC");
//                File direct = new File(signaturePathDes);
//                  Bitmap b2= decodeFile(direct);
//                    getResizedBitmap(photoPathDes,384,120);
//                    iRetVal=ptrGen.
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                //sAddData = edt_Text.getText().toString();
                ptrGen.iFlushBuf();
                //String empty = sAddData + "\n" + "\n" + "\n" + "\n" + "\n"+ "\n";
                ptrGen.iAddData((byte) 0x02, "CON CODE : " + Structbilling.Cons_Number);
                ptrGen.iAddData(bFontstyle, "B. ID    : "+Structbilling.Bill_No);//+Structbilling.Bill_No
                ptrGen.iAddData(bFontstyle, "BILL DATE: " + Structbilling.Bill_Date);
                //ptrGen.iAddData(bFontstyle, "PAY BY   : "+pay_by);
                ptrGen.iAddData(bFontstyle, "Upt(" + Structbilling.Due_Date + "):" + String.format("%.2f", Structbilling.Cur_Bill_Total));//total_amount
                ptrGen.iAddData(bFontstyle, "By(" + Structbilling.Due_Date + ") :" + String.format("%.2f", +Structbilling.Amnt_bPaid_on_Rbt_Date));//due_amount
//				double x=Double.parseDouble(due_amount);
//				double due=x+30;
                ptrGen.iAddData(bFontstyle, "Aft(" + Structbilling.Due_Date.trim() + "):" + String.format("%.2f", Structbilling.Amnt_Paidafter_Rbt_Date));
//				ptrGen.iAddData(bFontstyle, "------------------------");
//				ptrGen.iAddData(bFontstyle, "\nCheque and Draft Infavour of SBPDCL, Patna");
                ptrGen.iAddData((byte) 0x02, "  CESU ELECTRICITY BILL");
                ptrGen.iAddData(bFontstyle, "         2016 ");
                ptrGen.iAddData(bFontstyle, "------------------------\n");
                ptrGen.iAddData(bFontstyle, "B. ID      : 0001");
                ptrGen.iAddData(bFontstyle, "DATE & TIME: " + Structbilling.Bill_Date);
                ptrGen.iAddData(bFontstyle, "BILL MONTHS: " + Structbilling.Bill_Month);
                ptrGen.iAddData(bFontstyle, "DUE DATE   : " + Structbilling.Due_Date);
                ptrGen.iAddData(bFontstyle, "DIVISION   : " + Structconsmas.Division_Name);
                ptrGen.iAddData(bFontstyle, "SUB-DIV    : " + Structconsmas.Sub_division_Name);
                ptrGen.iAddData(bFontstyle, "SECTION    : " + Structconsmas.Section_Name);
                //       ptrGen.iAddData(bFontstyle, "CON CODE   : "+Structbilling.Cons_Number);
                ptrGen.iAddData(bFontstyle, "NAME : " + Structconsmas.Name);
                ptrGen.iAddData((byte) 0x02, "ADDRESS:         ");
                ptrGen.iAddData(bFontstyle, Structconsmas.address1);
                ptrGen.iAddData(bFontstyle, Structconsmas.address2);
                ptrGen.iAddData(bFontstyle, "TARIFF CATG: " + Structconsmas.Tariff_Code);
                ptrGen.iAddData(bFontstyle, "PHASE      : " + "1");
                ptrGen.iAddData(bFontstyle, "C.LOAD     : " + Structconsmas.Load);
                ptrGen.iAddData(bFontstyle, "MF         : " + Structconsmas.Multiply_Factor);
                ptrGen.iAddData(bFontstyle, "------------------------");
                ptrGen.iAddData(bFontstyle, "PRES.BL DATE :" + Structbilling.Bill_Date);
                ptrGen.iAddData(bFontstyle, "PREV.BL DATE :" + Structconsmas.Prev_Meter_Reading_Date);
                ptrGen.iAddData(bFontstyle, "PRES.READING : " + Structbilling.Cur_Meter_Reading);
                ptrGen.iAddData(bFontstyle, "PRES.STATUS  : " + Structbilling.Cur_Meter_Stat);
                ptrGen.iAddData(bFontstyle, "PREV.READING : " + Structconsmas.Prev_Meter_Reading);
                ptrGen.iAddData(bFontstyle, "C.UNITS      : " + Structbilling.Units_Consumed);
                ptrGen.iAddData(bFontstyle, "C.MONTHS     : " + Structbilling.Bill_Period);
                ptrGen.iAddData(bFontstyle, "\n");
                iRetVal = ptrGen.iStartPrinting(1);
                iRetVal = ptrGen.iBmpPrint(new File(photoPathDes));
                ptrGen.iFlushBuf();
                ptrGen.iAddData(bFontstyle, "\n");
                ptrGen.iAddData((byte) 0x02, "ARREARS : ");
                ptrGen.iAddData(bFontstyle, "PREV FY ARR   : " + padLeft(String.format("%.2f", Structconsmas.Pre_Financial_Yr_Arr), 8)+"ss");
                ptrGen.iAddData(bFontstyle, "CURR FY ARR   : " + padLeft(String.format("%.2f", Structconsmas.Cur_Fiancial_Yr_Arr), 8));
//				ptrGen.iAddData(bFontstyle, "Credit (-)    :    0.00");
//				ptrGen.iAddData(bFontstyle, "Energy Dues   : ");
//				ptrGen.iAddData(bFontstyle, "ED Arrears    :"+arrear);
//				ptrGen.iAddData(bFontstyle, "D.P.S         :    0.00");
//				ptrGen.iAddData(bFontstyle, "                --------");
//				ptrGen.iAddData(bFontstyle, "Sub Total     :"+arrear);
                ptrGen.iAddData(bFontstyle, "Current Bill    --------");
                ptrGen.iAddData(bFontstyle, "Current D.P.S :  " + padLeft(String.format("%.2f", Structconsmas.Delay_Payment_Surcharge), 7));
//				ptrGen.iAddData(bFontstyle, "Current DPSSD :"+curr_amount);
                ptrGen.iAddData(bFontstyle, "Fix/Mis Chg   :  " + padLeft(String.format("%.2f", Structbilling.Monthly_Min_Charg_DC), 7));
                ptrGen.iAddData(bFontstyle, "EC1           :  " + padLeft(String.format("%.2f", Structbilling.Slab_1_EC), 7));
                ptrGen.iAddData(bFontstyle, "EC2           :  " + padLeft(String.format("%.2f", Structbilling.Slab_2_EC), 7));
                ptrGen.iAddData(bFontstyle, "EC3           :  " + padLeft(String.format("%.2f", Structbilling.Slab_3_EC), 7));
                ptrGen.iAddData(bFontstyle, "EC4           :  " + padLeft(String.format("%.2f", Structbilling.Slab_4_EC), 7));
                ptrGen.iAddData(bFontstyle, "Energy Chg    :  " + padLeft(String.format("%.2f", Structbilling.Total_Energy_Charg), 7));
                ptrGen.iAddData(bFontstyle, "ED            :  " + padLeft(String.format("%.2f", Structbilling.Electricity_Duty_Charges), 7));
                ptrGen.iAddData(bFontstyle, "Meter Rent    :  " + padLeft(String.format("%.2f", Structconsmas.Meter_Rent), 7));
                ptrGen.iAddData(bFontstyle, "FPPCA Chg     :     0.00");
                ptrGen.iAddData(bFontstyle, "Shunt Chg     :     0.00");
                ptrGen.iAddData(bFontstyle, "Other Chg     :     0.00");
                ptrGen.iAddData(bFontstyle, "                --------");
                ptrGen.iAddData(bFontstyle, "Sub Total     :  " + padLeft(String.format("%.2f", Structbilling.Cur_Bill_Total), 7));
                ptrGen.iAddData(bFontstyle, "InstlonSD     :  " + padLeft(String.format("%.2f", Structconsmas.SD_Interest_chngto_SD_AVAIL), 7));
                ptrGen.iAddData(bFontstyle, "                --------");
                ptrGen.iAddData(bFontstyle, "Gross Total   :  " + padLeft(String.format("%.2f", Structbilling.Amnt_Paidafter_Rbt_Date), 7));
                ptrGen.iAddData(bFontstyle, "Rebate        :  " + padLeft(String.format("%.2f", Structbilling.Rbt_Amnt), 7));
                ptrGen.iAddData((byte) 0x06, "AMOUNT PAYABLE");
                ptrGen.iAddData(bFontstyle, "Upt(" + Structbilling.Due_Date + "): " + String.format("%.2f", Structbilling.Amnt_bPaid_on_Rbt_Date));
//				ptrGen.iAddData(bFontstyle, "By(20/04/15):"+due_amount);
                ptrGen.iAddData(bFontstyle, "Aft(" + Structbilling.Due_Date + "): " + String.format("%.2f", Structbilling.Amnt_Paidafter_Rbt_Date));
                ptrGen.iAddData(bFontstyle, "------------------------");
//				ptrGen.iAddData(bFontstyle, "Lst 3Cons   : 00-000-000");
//				ptrGen.iAddData(bFontstyle, "SD Held     : 0.00");
//				ptrGen.iAddData(bFontstyle, "SD Required : N/A");
                ptrGen.iAddData(bFontstyle, "L Paid Amt  :" + Structconsmas.Last_Total_Pay_Paid);
                ptrGen.iAddData(bFontstyle, "Rec No.     :" + Structconsmas.Last_Pay_Receipt_No);
                ptrGen.iAddData(bFontstyle, "L Paid Date :" + Structconsmas.Last_Pay_Date);
                ptrGen.iAddData(bFontstyle, "\nHELPLINE NO:18003456198");
                ptrGen.iAddData(bFontstyle, "Bill Generated By :");
                ptrGen.iAddData(bFontstyle, "Feedback Infra Pvt. Ltd");
                iRetVal = ptrGen.iStartPrinting(1);
                ptrGen.iFlushBuf();
                iRetVal = ptrGen.iBmpPrint(new File(QRPathDes));
                ptrGen.iAddData(bFontstyle, "\n" + "\n");
                ptrGen.iFlushBuf();
                iRetVal = ptrGen.iStartPrinting(1);
                ptrGen.iFlushBuf();
                iRetVal = ptrGen.iBmpPrint(new File(signaturePathDes));
                ptrGen.iAddData(bFontstyle, "\n" + "\n");
                ptrGen.iFlushBuf();

                Log.e("total_amount_barcode", " " + iRetVal);
                System.out.println("<<<<<<<<<<< HERE AM >>>>>>>>>");
                iRetVal = ptrGen.iStartPrinting(1);
//                SD= dbHelper.getWritableDatabase();
////               Cursor cr= SD.rawQuery("ALTER TABLE 'TBL_BILLING' ADD COLUMN 'User_Long' TEXT ", null);
//                Cursor cr2= SD.rawQuery("ALTER TABLE 'TBL_BILLING' ADD COLUMN 'User_Lat' TEXT ", null);
//                if ( cr2 != null && cr2.moveToFirst()) {
//                    System.out.println("derived meterstatus is :" + cr2.getColumnName(46));
//                    System.out.println("derived meterstatus is :" + cr2.getColumnName(47));
//                }
//                dbHelper.insertIntoBillingTable();
            } catch (NullPointerException e) {
                e.printStackTrace();
                iRetVal = DEVICE_NOTCONNECTED;
                System.out.println("<<<<<<<<<<< HERE AM AT CATCH >>>>>>>>>");

                return iRetVal;
            }
            return iRetVal;
        }

        public String padLeft(String s, int n) {
            if (s.length() < n) {
                return String.format("%1$" + n + "s", s);
            } else return s;
        }

        /* This displays the status messages of EnterTextAsyc in the dialog box */
        @Override
        protected void onPostExecute(Integer result) {
            //dlgPg.dismiss();
            prgDialog.dismiss();
            prgDialog.hide();
            showdialog();

            /*BarCodeTask barCodeTask = new BarCodeTask();
            barCodeTask.execute(0);*/
            System.out.println("returning is :" + iRetVal);
            if (iRetVal == DEVICE_NOTCONNECTED) {
                ptrHandler.obtainMessage(1, "Device not connected").sendToTarget();
            } else if (iRetVal == Printer_GEN.SUCCESS) {
                ptrHandler.obtainMessage(1, "Printing  Successful").sendToTarget();
            } else if (iRetVal == Printer_GEN.PLATEN_OPEN) {
                ptrHandler.obtainMessage(1, "Platen open").sendToTarget();
            } else if (iRetVal == Printer_GEN.PAPER_OUT) {
                ptrHandler.obtainMessage(1, "Paper out").sendToTarget();
            } else if (iRetVal == Printer_GEN.IMPROPER_VOLTAGE) {
                ptrHandler.obtainMessage(1, "Printer at improper voltage").sendToTarget();
            } else if (iRetVal == Printer_GEN.FAILURE) {
                ptrHandler.obtainMessage(1, "Printing failed").sendToTarget();
            } else if (iRetVal == Printer_GEN.PARAM_ERROR) {
                ptrHandler.obtainMessage(1, "Parameter error").sendToTarget();
            } else if (iRetVal == Printer_GEN.NO_RESPONSE) {
                ptrHandler.obtainMessage(1, "No response from Pride device").sendToTarget();
            } else if (iRetVal == Printer_GEN.DEMO_VERSION) {
                ptrHandler.obtainMessage(1, "Library in demo version").sendToTarget();
            } else if (iRetVal == Printer_GEN.INVALID_DEVICE_ID) {
                ptrHandler.obtainMessage(1, "Connected  device is not authenticated.").sendToTarget();
            } else if (iRetVal == Printer_GEN.NOT_ACTIVATED) {
                ptrHandler.obtainMessage(1, "Library not activated").sendToTarget();
            } else if (iRetVal == Printer_GEN.NOT_SUPPORTED) {
                ptrHandler.obtainMessage(1, "Not Supported").sendToTarget();
            } else {
                ptrHandler.obtainMessage(1, "Unknown Response from Device").sendToTarget();
            }
            super.onPostExecute(result);
        }
    }


    public static void ProgressDialog(Context context, String msg) {
        prgDialog = new ProgressDialog(context);
        prgDialog.setMessage(msg);
        prgDialog.setIndeterminate(true);
        prgDialog.setCancelable(false);
        prgDialog.show();
    }


    //Exit confirmation dialog box
    public void dlgExit() {

        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Spot Billing Application")
                .setContentText("Do You Want to Exit ?")
                .setCancelText("No")
                .setConfirmText("Yes")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        sDialog.dismissWithAnimation();

                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        try {
                            BluetoothComm.mosOut = null;
                            BluetoothComm.misIn = null;
                        } catch (NullPointerException e) {
                        }
                        Intent intent = new Intent(getApplicationContext(), BillingtypesActivity.class);
                        startActivity(intent);
                        System.gc();
                        finish();

                    }
                })
                .show();
    }

    //-------------------by me
// Decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    //    ----------------by me end
    // display information dialog box
    public void dlgInformationBox() { //TODO
        Dialog alert = new Dialog(context);
        alert.getWindow();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // custom layout for information display
        alert.setContentView(R.layout.informationbox);
        TextView site_tv = (TextView) alert.findViewById(R.id.site_tv);
        String str_links = "<a href='http://www.evolute-sys.com'>www.evolute-sys.com</a><br />";
        site_tv.setLinksClickable(true);
        site_tv.setMovementMethod(LinkMovementMethod.getInstance());
        site_tv.setText(Html.fromHtml(str_links));
        alert.show();
    }

    // displays a dialog box for composing a mail
    public void dlgSupportEmail(String stEmailId) { //TODO
        Button buttonSend;
        Display display = getWindowManager().getDefaultDisplay();
        @SuppressWarnings("deprecation")
        int width = display.getWidth();
        dlgSupport = new Dialog(context);
        dlgSupport.getWindow();
        dlgSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlgSupport.setContentView(R.layout.bdteamsupport);
        edtTo = (EditText) dlgSupport.findViewById(R.id.editTextTo);
        edtTo.setText(stEmailId);
        edtTo.setWidth(width);
        edtSubject = (EditText) dlgSupport.findViewById(R.id.editTextSubject);
        edtMessage = (EditText) dlgSupport.findViewById(R.id.editTextMessage);
        buttonSend = (Button) dlgSupport.findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sTo = edtTo.getText().toString();
                sSubject = edtSubject.getText().toString();
                sMessage = edtMessage.getText().toString();
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{sTo});
                email.putExtra(Intent.EXTRA_SUBJECT, sSubject);
                email.putExtra(Intent.EXTRA_TEXT, sMessage);
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
                dlgSupport.cancel();
            }
        });
        dlgSupport.show();
    }

    Dialog helpdialog;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dlgExit();
        }
        return super.onKeyDown(keyCode, event);
    }

    /*  To show response messages  */
    public void dlgShow(String str) {
        sweetAlertDialogBox.linetext("Spot Billing Application", str);
    }

    /*  To show response messages  */
    public void showdialog() {

        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Bluetooth Printer")
                .setContentText("Successfully Printed")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
//                        Intent i = new Intent(PrintingMainActivity.this, Signature_Activity.class);
//                        startActivity(i);
//                        finish();
//                        overridePendingTransition(R.anim.anim_slide_in_right,
//                                R.anim.anim_slide_out_right);
                    }
                })
                .show();


    }
}