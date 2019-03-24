package com.fedco.mbc.amigos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.support.v4.view.MotionEventCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.BillingtypesActivity;
import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.model.Structbilling;
import com.fedco.mbc.model.Structcolmas;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.model.Structtariff;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.AndroidBmpUtil;
import com.fedco.mbc.utils.HttpFileUpload;
import com.fedco.mbc.utils.URLS;
import com.fedco.mbc.utils.UtilAppCommon;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.qps.btgenie.BluetoothManager;
import com.qps.btgenie.QABTPAccessory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener, QABTPAccessory {
    private static final String TAG = MainActivity.class.getSimpleName ( );
    //    @InjectView(R.id.log_txt)
    TextView mLogTxt, tv_Print;
    //    EditText etText;
    int scalingVal, position;
    //    @InjectView(R.id.scan)
//    Button mScanBtn, mConnectBtn, mStartBT, mPrintSmallHeight, mLinefedBtn, Enter;
    //    @InjectView(R.id.communication)
    Spinner devListSpinner;
    ArrayAdapter <String> spinAdapter;
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    BluetoothManager btpObject;

    String test;
    String sampleText, strMeterMake, strMeterOwner;
    SharedPreferences sharedPreferences;
    String Mac;
    String spinMac;
    String print_type = "0";

    String Hour, Minute;
    int HOURS, MIN, AP;
    String strtime;
    String key;
    String strdcSeq;
    Long billseq;
    Long dcSeq;
    SweetAlertDialog sDialog;

    Logger Log;
    SessionManager session;
    SQLiteDatabase SD, SD2, SD3, SD4;
    DB dbHelper, dbHelper2, dbHelper3, dbHelper4;
    UtilAppCommon appcomUtil;
    String printerName = null;

    private ProgressDialog progress;
    ArrayList <String> mylistimagename = new ArrayList <String> ( );

    //    String imgSrcPath        = Environment.getExternalStorageDirectory()
    //            + File.separator + "/MBC/Images/" + GSBilling.getInstance().getKEYNAME() + "_" + Structconsmas.Consumer_Number + "_mtr.jpg";
    //    String imgSrcPathDes     = Environment.getExternalStorageDirectory()
    //            + File.separator + "/MBC/Images/" + GSBilling.getInstance().getKEYNAME() + "_mtr.bmp";

    //    String ZipDesPath = Environment.getExternalStorageDirectory() + "/MBC/" + GSBilling.getInstance().getKEYNAME() + GSBilling.getInstance().captureDatetime() + ".zip";

    //    String ZipDesPathdup     = "/MBC/" + GSBilling.getInstance().getKEYNAME() + GSBilling.getInstance().captureDatetime();
    //    String signaturePathDes  = Environment.getExternalStorageDirectory() + "/MBC" + "/" + GSBilling.getInstance().getKEYNAME() + "_" + Structconsmas.Consumer_Number + "_sig.jpg";
    //    String photoPathDes      = Environment.getExternalStorageDirectory() + "/MBC/" + GSBilling.getInstance().getKEYNAME() + "_" + Structconsmas.Consumer_Number + "_mtr.jpg";

    String ZipSourcePath = Environment.getExternalStorageDirectory ( ) + "/MBC/Images/";
    String ZipCopyPath = Environment.getExternalStorageDirectory ( ) + "/MBC/Downloadsingular/";
    String ZipDeletPath2 = Environment.getExternalStorageDirectory ( ) + "/MBC/Downloadsingular/";
    String Zip = Environment.getExternalStorageDirectory ( ) + "/Notes/billing.csv";
    String ZipDesPathdup;
    String signaturePathDes;
    String photoPathDes;
    String ZipDesPath;
    String imgSrcPathDes;
    String imgSrcPath;
    private static SimpleDateFormat simpleDateFormat;

    private static Date connvertedDate;
    private static DateFormat dateFormat;
    private static String actualDate;
    String newDate;
    String mdValue = null, billTime;
    String pfValue;
    Float miscValue;
    StringBuffer stringBuffer = new StringBuffer ( );
    public final int singleVarLinefeed = 1;
    public final int doubleVarlinefeed = 2;
    public final int tripleVarlinefeed = 3;
    public final int fourVarlinefeed = 4;
    public final int fiveVarlinefeed = 5;
    public final int sixVarlinefeed = 6;
    private int regionFlag = 0;
    private int indoreFlag = 0;

    int Cur_Meter_Reading;
    int Prev_Meter_Reading;
    String Bill_Basis;
    String Reading_type_code;
    String test2;

    public String fixedLengthString(String str, int leng) {
        for (int i = str.length ( ); i <= leng; i++)
            str += " ";
        return str;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main_amigo );
//        tv_Print = (TextView)findViewById(R.id.tv_print);
        btpObject = BluetoothManager.getInstance ( this, MainActivity.this );
        sharedPreferences = this.getSharedPreferences ( "QABTprefs", 0 );
        Mac = sharedPreferences.getString ( "BTDeviceMac", "NA" );
//        mStartBT = (Button) findViewById(R.id.bt1);
//        mScanBtn = (Button) findViewById(R.id.bt2);
//        mConnectBtn = (Button) findViewById(R.id.bt3);
//        mPrintSmallHeight = (Button) findViewById(R.id.bt5);
//        mLinefedBtn = (Button) findViewById(R.id.bt9);
        mLogTxt = (TextView) findViewById ( R.id.log_txt );
//        etText = (EditText) findViewById(R.id.edittext1);
//        Enter = (Button) findViewById(R.id.bt4);
//        Enter.setOnClickListener(this);
//        mStartBT.setOnClickListener(this);
//        mScanBtn.setOnClickListener(this);
//        mConnectBtn.setOnClickListener(this);
//        mPrintSmallHeight.setOnClickListener(this);
//        mLinefedBtn.setOnClickListener(this);
        devListSpinner = (Spinner) findViewById ( R.id.device_listspinner );
        devListSpinner.setOnItemSelectedListener ( this );
        spinAdapter = new ArrayAdapter <String> ( this, android.R.layout.simple_spinner_item );
        spinAdapter.setDropDownViewResource ( android.R.layout.simple_spinner_dropdown_item );
        devListSpinner.setAdapter ( spinAdapter );
//        spinAdapter.add("Click to connect BTP");
        spinAdapter.notifyDataSetChanged ( );
//        mConnectBtn.setText("connect");
//        checkBTP_Permissions();
//        btpObject.startDiscovery();
//        checkBTP_Permissions();
//        btpObject.scanAllBluetoothDevice();
        checkBTP_Permissions ( );
        setLogText ( "===> Start Scanning devices.." );
        /* btpObject.startDiscovery();*/
        btpObject.scanAllBluetoothDevice ( );


        if (Structbilling.MDI != null) {
            mdValue = String.valueOf ( Structbilling.MDI ) + " " + GSBilling.getInstance ( ).getUnitMaxDemand ( );
            ;
        } else {
            mdValue = "0";
        }

//        if (!Mac.equals("NA")) {
//            connect_device();
//        }


        //---------------------
//        dbHelper = new DB(getApplicationContext());
//        SD = dbHelper.getWritableDatabase();
        appcomUtil = new UtilAppCommon ( );
        Calendar c = Calendar.getInstance ( );

        //-------------not used
        Structconsmas.Meter_Type = "00";
        Structconsmas.Meter_Ownership = "A";
        //----------

        imgSrcPath = Environment.getExternalStorageDirectory ( )
                + File.separator + "/MBC/Images/" + appcomUtil.UniqueCode ( getApplicationContext ( ) ) + "_" + Structconsmas.LOC_CD + "_" + Structconsmas.MAIN_CONS_LNK_NO + "_" + appcomUtil.billMonthConvert ( Structconsmas.Bill_Mon ) + "_mtr.jpg";
        imgSrcPathDes = Environment.getExternalStorageDirectory ( )
                + File.separator + "/MBC/Images/" + appcomUtil.UniqueCode ( getApplicationContext ( ) ) + "_mtr.bmp";
        ZipDesPath = Environment.getExternalStorageDirectory ( ) + "/MBC/" + appcomUtil.UniqueCode ( getApplicationContext ( ) ) + GSBilling.getInstance ( ).captureDatetime ( ) + ".zip";
        ZipDesPathdup = "/MBC/" + appcomUtil.UniqueCode ( getApplicationContext ( ) ) + GSBilling.getInstance ( ).captureDatetime ( );
        signaturePathDes = Environment.getExternalStorageDirectory ( ) + "/MBC" + "/" + appcomUtil.UniqueCode ( getApplicationContext ( ) ) + "_" + Structconsmas.LOC_CD + "_" + Structconsmas.MAIN_CONS_LNK_NO + "_" + appcomUtil.billMonthConvert ( Structconsmas.Bill_Mon ) + "_sig.jpg";
        photoPathDes = Environment.getExternalStorageDirectory ( ) + "/MBC/" + appcomUtil.UniqueCode ( getApplicationContext ( ) ) + "_" + Structconsmas.LOC_CD + "_" + Structconsmas.MAIN_CONS_LNK_NO + "_" + appcomUtil.billMonthConvert ( Structconsmas.Bill_Mon ) + "_mtr.jpg";

        File dir2 = new File ( ZipDeletPath2 );
        DeleteRecursive ( dir2 );

        SimpleDateFormat fds = new SimpleDateFormat ( "kk:mm" );
        strtime = fds.format ( c.getTime ( ) );

        session = new SessionManager ( getApplicationContext ( ) );
        HashMap <String, String> user = session.getUserDetails ( );
//        key = session.retLicence();
        key = appcomUtil.UniqueCode ( getApplicationContext ( ) );

        Structbilling.SBM_No = key;
        Structbilling.Meter_Reader_Name = session.retMRName ( );
        Structbilling.Meter_Reader_ID = session.retMRID ( );

        try {
            AndroidBmpUtil.convertJpeg2Bmp ( getApplicationContext ( ), imgSrcPath, imgSrcPathDes );
        } catch (Exception e) {
            e.printStackTrace ( );
        }
//------------------
//        actualDate = Structbilling.Bill_Month;
//        simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//        Date date = null;
//        newDate = null;
//        try{
//            date = simpleDateFormat.parse(actualDate);
//            SimpleDateFormat postFormater = new SimpleDateFormat("dd-MMM-yyyy");
//            newDate = postFormater.format(date);
//        }
//        catch(Exception e){
//            e.printStackTrace();
//        }
//        ------------------------
        HOURS = c.get ( Calendar.HOUR );
        MIN = c.get ( Calendar.MINUTE );
        AP = c.get ( Calendar.AM_PM );

        if (HOURS < 10) {
            Hour = "0" + HOURS;
        }
        if (MIN <= 10) {
            Minute = "0" + MIN;
        }

        print_type = GSBilling.getInstance ( ).getPrinttype ( );
        Calendar c1 = Calendar.getInstance ( );
        SimpleDateFormat fds2 = new SimpleDateFormat ( "kk:mm" );
        billTime = fds2.format ( c1.getTime ( ) );
        switch (Structconsmas.Meter_Type.trim ( )) {
            case "03":
                System.out.println ( "in if3" + strMeterMake );
                this.strMeterMake = "1ph Static";
                break;
            case "01":
                System.out.println ( "in if1" + strMeterMake );
                this.strMeterMake = "1ph EM";
                break;
            case "28":
                System.out.println ( "in if28" + strMeterMake );
                this.strMeterMake = "LT 1 Ph AMR/AMI";
                break;
            default:
                System.out.println ( "in ELSE" + strMeterMake );
                System.out.println ( "in default" + Structconsmas.Meter_Type );
                this.strMeterMake = "";
                break;
        }

        switch (Structconsmas.Meter_Ownership) {
            case "D":
                System.out.println ( "in if3" + strMeterOwner );
                this.strMeterOwner = "DF";
                break;
            case "G":
                System.out.println ( "in if1" + strMeterOwner );
                this.strMeterOwner = "CESU";
                break;
            case "C":
                System.out.println ( "in if28" + strMeterMake );
                this.strMeterOwner = "CONSUMER";
                break;
            default:
                System.out.println ( "in ELSE" + strMeterMake );
                this.strMeterOwner = "";
                break;
        }

//      EnterTextAsyc asynctask = new En Prev_Meter_Reading =Structconsmas.Prev_Meter_Reading;
        Prev_Meter_Reading = Structconsmas.Prev_Meter_Reading;
        Cur_Meter_Reading = Structbilling.Cur_Meter_Reading;
        Bill_Basis = Structbilling.Bill_Basis;
        Reading_type_code = RDG_TYP_CD_Print ( );

        if (Structbilling.Reasons != null && !Structbilling.Reasons.isEmpty ( )) {

            if (Structbilling.Reasons.equalsIgnoreCase ( "Previous reading is higher than Current reading" )) {
                Prev_Meter_Reading = Structconsmas.Prev_Meter_Reading;
                Cur_Meter_Reading = Structconsmas.Prev_Meter_Reading;
                Bill_Basis = "ACT";
                Reading_type_code = "NORMAL";
            }
        }
//        asynctask.execute(0);

        //
        String printtariif = (Structconsmas.Tariff_Code + "-" + (((Structconsmas.URBAN_FLG.equalsIgnoreCase ( "U" )) ? Structtariff.MMFC_URBAN_RATE_1 : Structtariff.MMFC_RURAL_RATE_1) * Structtariff.Overdrawl_Rate2));
//        test = "     MADHYA PRADESH" + "\n" +
//                "     M.P.P.K.V.V.C.L" + "\n" +
//                "    ELECTRICITY BILL" + "\n" +
//                "-----------------------" + "\n" +
//                " CUSTOMER CARE NUMBERS" + "\n" +
//                "-----------------------" + "\n" +
//                "    1800-233-1912" + "\n" +
//                //"<0x10> 0755-2551222" + "\n" +
//                "-----------------------" + "\n" +
//
//                "BILL MONTH : " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" + //201706
//                "BILL DATE  : " + (String.format("%1$6s", Structbilling.Bill_Date)) + "\n" +
//                "BILL NO    : " + (String.format("%1$6s", Structbilling.Bill_No)) + "\n" +
//                "BILL PERIOD: " + (String.format("%1$6s", Structbilling.Bill_Period)) + "\n" +
//
//                "-----------------------" + "\n" +
//
//                "DIVISION   : " + (String.format("%1$6s", "BURHANPUR")) + "\n" +
//                "DC NAME    : " + (String.format("%1$6s", sectionName)) + "\n" +
//                "GROUP/DAIRY NO     : " + (String.format("%1$8s", Structconsmas.Cycle)) + "/" + (String.format("%1$8s", Structconsmas.Route_Number)) + "\n" +
//                "FDR DESC  : " + (String.format("%1$6s", Structconsmas.FDR_SHRT_DESC)) + "\n" +
//                "POLE ID   : " + (String.format("%1$9s", Structconsmas.POLE_ID)) + "\n" +
//                "ACC ID    : " + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
//                "IVRS/SC NO: " + (String.format("%1$8s", Structconsmas.LOC_CD + "-" + Structconsmas.Consumer_Number)) + "\n" +
//                "NAME      : " + (String.format("%1$6s", Structconsmas.Name)) + "\n" +
//                "ADDRESS   : " + "\n" +
//                "" + (String.format("%1$6s", Structconsmas.address1 + Structconsmas.address2)) + "\n" +
//                "-----------------------" + "\n" +
//
//                "TARIFF CATG  : " + (String.format("%1$8s", printtariif)) + "\n" +
//                "LOAD         : " + (String.format("%1$8s", Structconsmas.Load)) + getLoadType() + "\n" +
//                "SECURITY DEPS: " + (String.format("%1$8s", Structconsmas.TOT_SD_HELD)) + "\n" +
//                "MTR SL NO    : " + (String.format("%1$8s", Structconsmas.Meter_S_No)) + "\n" +
//                "MF           : " + (String.format("%1$8s", Structconsmas.MF)) + "\n" +
//                "PHASE        : " + (String.format("%1$8s", Structconsmas.PHASE_CD)) + "\n" +
//                "---------------------" + "\n" +
//                "READING DATE :" + (String.format("%1$4s", Structbilling.Cur_Meter_Reading_Date)) + "\n" +
//                "READING STS  : " + (String.format("%1$8s", Structbilling.Bill_Basis)) + "\n" +
//                "PRES READING : " + (String.format("%1$8s", Structbilling.Cur_Meter_Reading)) + "\n" +
//                "PREV READING : " + (String.format("%1$8s", Structconsmas.Prev_Meter_Reading)) + "\n" +
//                "MD           : " + (String.format("%1$8s", mdValue)) + "\n" +
//                "PF           : " + (String.format("%1$8s", Structbilling.Avrg_PF)) + "\n" +
//                "MTR CONSP    : " + (String.format("%1$8s", Structbilling.MTR_CONSMP)) + "\n" +
//                "UNIT BILLED  : " + (String.format("%1$8s", Structbilling.Billed_Units)) + "\n" +
//                "BILL TYPE    : " + (String.format("%1$8s", Structbilling.Bill_Basis)) + "\n" +
//                "---------------------" + "\n" +
//                "MIN CHARG    : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.MIN_CHRG))))) + "\n" +
//                "FIXED CHARG  : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.FIXED_CHARGE))))) + "\n" +
//                "ENERGY CHRG  : " + (String.format("%1$8s", (String.format("%.2f", Structbilling.Total_Energy_Charg)))) + "\n" +
//                "FCA CHARG    : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_FCA))))) + "\n" +
//                "---------------------" + "\n" +
//                "DUTY CHARGE  : " + (String.format("%1$8s", (String.format("%.2f", Structbilling.Electricity_Duty_Charges)))) + "\n" +
//                "METER RENT   : " + (String.format("%1$8s", (String.format("%.2f", Structbilling.Meter_Rent)))) + "\n" +
//                "ADJ AMT      : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structconsmas.Sundry_Allow_EC))))) + "\n" +
//                "PANEL CHRG   : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_OverdrwalPenalty))))) + "\n" +
//                "P.F CHRG     : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_PFPenalty))))) + "\n" +
//                "TOD CHRG     : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(0))))) + "\n" +
//                "SD BILLED    : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(UtilAppCommon.doublecheck(Structconsmas.SD_BILLED)))))) + "\n" +
//                "OTHER CHRG   : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_welding_charges))))) + "\n" +
//                "------------------" + "\n" +
//                "SD INT       : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structconsmas.SD_INSTT_AMT))))) + "\n" +
//                "LOCK CR      : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_LockCreditAmount))))) + "\n" +
//                "EMP. REBATE  : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_Employee_Incentive))))) + "\n" +
//                "SUBSIDY      : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Subsidy))))) + "\n" +
//                "------------------" + "\n" +
//                "CUR BILL AMT : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_Current_Demand))))) + "\n" +
//                "PREV ARREARS : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_Arrear_Demand))))) + "\n" +
//                "SURCHARGE ARS: " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structconsmas.SURCHARGE_ARREARS))))) + "\n" +
//                "PREV BILL DIFF. AMT.: " + "\n" + (String.format("%1$8s", "0.00")) + "\n" +                                                                // No diff Amount
//                "ROUND AMT    : " + (String.format("%1$8s", Structbilling.Round_Amnt)) + "\n" +
//                "PAYABLE BY DUE DT:" + "\n" +
//                "" + (String.format("%1$6s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
//                "DELAY PAY SURCHARG : " + "\n" +
//                "" + (String.format("%1$6s", Structbilling.O_Surcharge)) + "\n" +
//                "PAYABLE AFTER DUE DT:" + "\n" +
//                "" + (String.format("%1$6s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" +  // doubt
//                "CHQ/ONLINE DUE DT  :" + "\n" +
//                "" + (String.format("%1$6s", Structconsmas.FIRST_CHQ_DUE_DATE)) + "\n" +
//                "CASH DUE DT        :" + "\n" +
//                "" + (String.format("%1$6s", Structconsmas.FIRST_CASH_DUE_DATE)) + "\n" +
//                "....................." + "\n" +
//                "MACHINE NO : " + (String.format("%1$6s", Structbilling.SBM_No)) + "\n" +
//                "MR NAME: " + "\n" +
//                "" + (String.format("%1$6s", Structbilling.Meter_Reader_Name)) + "\n" +
//                "MR ID       : " + (String.format("%1$4s", Structbilling.Meter_Reader_ID)) + "\n" +
//                "" + " " + "\n" +
//                "" + " " + "\n" +
//                "" + " " + "\n" +
//                "" + " " + "\n" +
//                "" + " " + "\n" +
//                "" + " " + "\n" +
//                "          RECEIPT        " + "\n" +
//                "BILL MONTH : " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" +
//                "BILL DATE  : " + (String.format("%1$6s", Structbilling.Bill_Date)) + "\n" +
//                "BILL NO    : " + (String.format("%1$6s", Structbilling.Bill_No)) + "\n" +
//                "DIVISION   : " + (String.format("%1$6s", "BURHANPUR")) + "\n" +
//                "DC NAME    : " + (String.format("%1$6s", sectionName)) + "\n" +
//                "GROUP/DAIRY NO: " + (String.format("%1$8s", Structconsmas.Cycle)) + "/" + (String.format("%1$8s", Structconsmas.Route_Number)) + "\n" +
//                "ACC ID/SC NO: " + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
//                "IVRS    NO  : " + (String.format("%1$8s", Structconsmas.LOC_CD + "-" + Structconsmas.Consumer_Number)) + "\n" +
//                "CONS NAME        : " + (String.format("%1$6s", Structconsmas.Name)) + "\n" +
//                "PAYABLE BY DUE DT:" + "\n" +
//                "" + (String.format("%1$6s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
//                "PAYABLE AFTER DUE DT:" + "\n" +
//                "" + (String.format("%1$6s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" + // doubt
//                "" + " " + "\n" +
//                "" + " " + "\n" +
//                "" + " " + "\n" +
//                "" + " " + "\n";
       /* String meterReaderName = Structbilling.Meter_Reader_Name;
        if (Structbilling.Meter_Reader_Name.length ( ) < 20) {
            meterReaderName = Structbilling.Meter_Reader_Name;
        } else {
            meterReaderName = Structbilling.Meter_Reader_Name.substring ( 0, 18 );
        }
*/
        String consumerName = Structconsmas.Name;
        if (Structconsmas.Name.length ( ) < 22) {
            //   consumerName = Structconsmas.Name + "\n";
            consumerName = fixedLengthString ( consumerName, 44 );// + "\n";
        } else {
            consumerName = fixedLengthString ( Structconsmas.Name, 44 );
            consumerName = consumerName.substring ( 0, 44 );
        }
        Structconsmas.Name = consumerName;
        String jblConsName = Structconsmas.Name;
        if (Structconsmas.Name.length ( ) < 22) {
            jblConsName = Structconsmas.Name;
        } else {
            jblConsName = Structconsmas.Name.substring ( 0, 21 );
        }

        String feederDesc = Structconsmas.FDR_SHRT_DESC;
        if (Structconsmas.FDR_SHRT_DESC.length ( ) < 13) {
            feederDesc = Structconsmas.FDR_SHRT_DESC;
        } else {
            feederDesc = Structconsmas.FDR_SHRT_DESC.substring ( 0, 12 );
        }

        String address = Structconsmas.address1 + Structconsmas.address2 + Structconsmas.MOH;
        String subadd1;
        String subadd2;
        String PrinDate;

        PrinDate = Structbilling.Cur_Meter_Reading_Date.substring ( 0, 6 ) + "-" + Structbilling.Cur_Meter_Reading_Date.substring ( 9 );
        if (address.length ( ) < 22) {
            address = fixedLengthString ( Structconsmas.address1 + Structconsmas.address2 + Structconsmas.MOH, 43 );// + "\n";
        } else {
            if (address.length ( ) >= 45) {
                subadd1 = address.substring ( 0, 43 );
            } else {
                subadd1 = address;
            }

            address = fixedLengthString ( subadd1, 43 );
        }
        String sectionName = Structconsmas.Section_Name;
        if (sectionName.length ( ) >= 19) {
            sectionName = Structconsmas.Section_Name.substring ( 0, 19 );
        } else {
            sectionName = Structconsmas.Section_Name;
        }

        appcomUtil = new UtilAppCommon ( );
//            if (print_type.equals("1")) {
//                sendMessage("<0x9>CON CODE :" + Structbilling.Cons_Number + "\n" +
//                        "<0x9>B. ID    : 0001 " + "\n" +
//                        "<0x09>BILL DATE: " + Structbilling.Bill_Date + "\n" +
//                        "<0x09>Upt(" + Structbilling.Due_Date + "):" + (String.format("%.2f", Structbilling.Cur_Bill_Total)) + "\n" +
//                        "<0x09>By(" + Structbilling.Due_Date + "):" + (String.format("%.2f", +Structbilling.Amnt_bPaid_on_Rbt_Date)) + "\n" +
//                        "<0x09>Aft(" + Structbilling.Due_Date.trim() + "):" + (String.format("%.2f", Structbilling.Amnt_Paidafter_Rbt_Date)) + "\n" +
//                        "<0x10>  CESU ELECTRICITY BILL" + "\n" +
//                        "<0x10>           2016" + "\n" +
//                        "<0x09>------------------------" + "\n" +
//                        "<0x09>B. ID      : 0001" + "\n" +
//                        "<0x09>DATE & TIME: " + Structbilling.Bill_Date + "\n" +
//                        "<0x09>BILL MONTHS: " + Structbilling.Bill_Month + "\n" +
//                        "<0x09>DUE DATE   : " + Structbilling.Due_Date + "\n" +
//                        "<0x09>DIVISION   : " + Structconsmas.Division_Name + "\n" +
//                        "<0x09>SUB-DIV    : " + Structconsmas.Sub_division_Name + "\n" +
//                        "<0x09>SECTION    : " + Structconsmas.Section_Name + "\n" +
//                        "<0x09>NAME :  " + Structconsmas.Name + "\n" +
//                        "<0x10>ADDRESS   : " + "\n" +
//                        "<0x09>" + Structconsmas.address1 + Structconsmas.address2 +
//                        "<0x09>TARIFF CATG:  " + Structconsmas.Tariff_Code + "\n" +
//                        "<0x09>PHASE      : " + "1" + "\n" +
//                        "<0x09>C.LOAD     : " + Structconsmas.Load + "\n" +
//                        "<0x09>MF         : " + Structconsmas.Multiply_Factor + "\n" +
//                        "<0x09>---------------------" + "\n" +
//                        "<0x09>PRES.BL DATE :" + Structbilling.Bill_Date + "\n" +
//                        "<0x09>PREV.BL DATE :" + Structconsmas.Prev_Meter_Reading_Date + "\n" +
//                        "<0x09>PRES.READING :  " + Structbilling.Cur_Meter_Reading + "\n" +
//                        "<0x09>PREV.READING :  " + Structconsmas.Prev_Meter_Reading + "\n" +
//                        "<0x09>PRES.STATUS  :  " + Structbilling.Cur_Meter_Stat + "\n" +
//                        "<0x09>C.UNITS      :  " + Structbilling.Units_Consumed + "\n" +
//                        "<0x09>C.MONTHS     :  " + Structbilling.Bill_Period + "\n" +
//                        "<0x10>ARREARS : " + "\n" +
//                        "<0x09>PREV FY ARR   :  " + (String.format("%.2f", Structconsmas.Pre_Financial_Yr_Arr)) + "\n" +
//                        "<0x09>CURR FY ARR   :  " + (String.format("%.2f", Structconsmas.Cur_Fiancial_Yr_Arr)) + "\n" +
//                        "<0x09>Current Bill   " + "\n" +
//                        "<0x09>---------------" + "\n" +
//                        "<0x09>Current D.P.S :  " + (String.format("%.2f", Structconsmas.Delay_Payment_Surcharge)) + "\n" +
//                        "<0x09>Fix/Mis Chg   :  " + (String.format("%.2f", Structbilling.Monthly_Min_Charg_DC)) + "\n" +
//                        "<0x09>EC1           :  " + (String.format("%.2f", Structbilling.Slab_1_EC)) + "\n" +
//                        "<0x09>EC2           :  " + (String.format("%.2f", Structbilling.Slab_2_EC)) + "\n" +
//                        "<0x09>EC3           :  " + (String.format("%.2f", Structbilling.Slab_3_EC)) + "\n" +
//                        "<0x09>EC4           :  " + (String.format("%.2f", Structbilling.Slab_4_EC)) + "\n" +
//                        "<0x09>Energy Chg    :  " + (String.format("%.2f", Structbilling.Slab_1_EC)) + "\n" +
//                        "<0x09>ED            :  " + (String.format("%.2f", Structbilling.Electricity_Duty_Charges)) + "\n" +
//                        "<0x09>METER RENT    :  " + (String.format("%.2f", Structconsmas.Meter_Rent)) + "\n" +
//                        "<0x09>FPPCA Chg     :  0.00" + "\n" +
//                        "<0x09>Shunt Chg     :  0.00" + "\n" +
//                        "<0x09>Other Chg     :  0.00" + "\n" +
//                        "<0x09>              --------" + "\n" +
//                        "<0x09>Sub Total     :  " + (String.format("%.2f", Structbilling.Cur_Bill_Total)) + "\n" +
//                        "<0x09>InstlonSD     :  " + (String.format("%.2f", Structconsmas.SD_Interest_chngto_SD_AVAIL)) + "\n" +
//                        "<0x09>              --------" + "\n" +
//                        "<0x09>Gross Total   :  " + (String.format("%.2f", Structbilling.Cur_Bill_Total)) + "\n" +
//                        "<0x09>Rebate        :  " + (String.format("%.2f", Structbilling.Rbt_Amnt)) + "\n" +
//                        "<0x10>AMOUNT PAYABLE" + "\n" +
//                        "<0x10>--------------" + "\n" +
//                        "<0x09>Upt(" + Structbilling.Due_Date + "):" + (String.format("%.2f", Structbilling.Amnt_bPaid_on_Rbt_Date)) + "\n" +
//                        "<0x09>Aft(" + Structbilling.Due_Date.trim() + "):" + (String.format("%.2f", Structbilling.Amnt_Paidafter_Rbt_Date)) + "\n" +
//                        "<0x09>---------------------" + "\n" +
//                        "<0x09>L Paid Amt  :" + Structconsmas.Last_Total_Pay_Paid + "\n" +
//                        "<0x09>Rec No.     :" + Structconsmas.Last_Pay_Receipt_No + "\n" +
//                        "<0x09>L Paid Date :" + Structconsmas.Last_Pay_Date + "\n" +
//                        "<0x09>\nHELPLINE NO:18003456198" + "\n" +
//                        "<0x09>Bill Generated By :" + "\n" +
//                        "<0x09>Feedback Infra Pvt. Ltd" + "\n");

//            }
// else {
//            actualDate = curconmas.getString(51).trim();
        actualDate = Structconsmas.Bill_Mon;
        simpleDateFormat = new SimpleDateFormat ( "yyyyMMdd" );
        Date date = null;
        String newDate = null;

        try {
            date = simpleDateFormat.parse ( actualDate );
            SimpleDateFormat postFormater = new SimpleDateFormat ( "dd-MMM-yyyy" );
            newDate = postFormater.format ( date ).trim ( ).substring ( 3 );
        } catch (Exception e) {
            e.printStackTrace ( );
        }

//            float Arr = Structconsmas.Pre_Financial_Yr_Arr + Structconsmas.Cur_Fiancial_Yr_Arr;
        Structbilling.DCNumber = "";

//            Structbilling.DSIG_STATE = "50";

        billseq = appcomUtil.findSequence ( getApplicationContext ( ), "BillNumber" );

        UtilAppCommon uc = new UtilAppCommon ( );
        uc.print_Bill ( );
        uc.princonmas ( );

//            double totAMount = (Math.ceil(Structbilling.Amnt_bPaid_on_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL));

        //      if (Arr > 5000 && totAMount > 5000) {

        dcSeq = appcomUtil.findSequence ( getApplicationContext ( ), "DCNumber" );
        strdcSeq = String.format ( "%05d", dcSeq );
        Structbilling.DCNumber = key + "/DN" + dcSeq;

//                sendMessage("<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + "           " + Structbilling.Bill_Month + "\n" +
//                        "<0x09>" + "       " + Structbilling.Bill_Date + "  " + strtime +
//                        "<0x09>" + "       " + Structbilling.Bill_No + "    " + "1.0" +
//                        "<0x09>" + "              " + Structbilling.Bill_Period + "\n" +
//                        "<0x09>" + "           " + Structconsmas.Division_Name + "\n" +
//                        "<0x09>" + "           " + Structconsmas.Sub_division_Name + "\n" +
//                        "<0x09>" + "           " + Structconsmas.Section_Name + "\n" +
//                        "<0x09>" + "           " + Structbilling.SBM_No + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + "    " + Structbilling.Meter_Reader_Name.trim() + "\n" +
//                        "<0x09>" + "           " + Structbilling.Meter_Reader_ID.trim() + "\n" +
//                        "<0x09>" + "      " + Structconsmas.Electrical_Address + "\n" +
//                        "<0x09>" + "        " + Structbilling.Cons_Number + "\n" +
//                        "<0x09>" + "        " + Structconsmas.Old_Consumer_Number + "\n" +
//                        "<0x09>" + "  " + Structconsmas.Name.trim() + "\n" +
//                        "<0x09>" + "        " + Structconsmas.address1.trim() + Structconsmas.address2.trim() + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + "       " + Structconsmas.Tariff_Code + "\n" +
//                        "<0x09>" + "        " + Structconsmas.Category + "       " + Structconsmas.Load + "\n" +
//                            "<0x09>" + "\n" +
//                        "<0x09>" + "          " + Structconsmas.Cycle + "\n" +
//                        "<0x09>" + "          " + Structconsmas.Route_Number + "\n" +
//                        "<0x09>" + "          " + Structconsmas.Meter_S_No + "\n" +
//                        "<0x09>" + "          " + strMeterOwner + "\n" +
//                        "<0x09>" + "          " + strMeterMake + "\n" +
//                        "<0x09>" + "        " + Structconsmas.Multiply_Factor + "             " + "1" + "\n" +
//                        "<0x09>" + "           " + Structbilling.Avrg_Units_Billed + "\n" +
//                        "<0x09>" + " " + "      \n" +
//                        "<0x09>" + "     " + Structbilling.Cur_Meter_Reading + " " + Structbilling.Bill_Date + " " + Structbilling.Cur_Meter_Stat + "\n" +
//                        "<0x09>" + "     " + Structconsmas.Prev_Meter_Reading + " " + Structconsmas.Prev_Meter_Reading_Date + " " + Structconsmas.Prev_Meter_Status +
//                        "<0x09>" + "    " + " 0 " + "            " + " 0 " + "\n" +
//                        "<0x09>" + "          " + Structbilling.Units_Consumed + "\n" +
//                        "<0x09>" + "          " + Structbilling.Bill_Basis + "\n" +
//                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Monthly_Min_Charg_DC))) + "\n" +
//                        "<0x09>" + "      " + String.format("%5.5s", slab1unit) + " =" + (String.format("%1$6s", String.format("%.2f", Structbilling.Slab_1_EC))) + "\n" +
//                        "<0x09>" + "      " + String.format("%5.5s", slab2unit) + " =" + (String.format("%1$6s", String.format("%.2f", Structbilling.Slab_2_EC))) + "\n" +
//                        "<0x09>" + "      " + String.format("%5.5s", slab3unit) + " =" + (String.format("%1$6s", String.format("%.2f", Structbilling.Slab_3_EC))) + "\n" +
//                        "<0x09>" + "      " + String.format("%5.5s", slab4unit) + " =" + (String.format("%1$6s", String.format("%.2f", Structbilling.Slab_4_EC))) + "\n" +
//                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Total_Energy_Charg))) + "\n" +
//                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Electricity_Duty_Charges))) + "\n" +
//                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structconsmas.Meter_Rent))) + "\n" +
//                        "<0x09>" + "             " + (String.format("%1$6s", "0.00")) + "\n" +
//                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structconsmas.Delay_Payment_Surcharge))) + "\n" +
//                        "<0x09>" + "             " + (String.format("%1$6s", "0.00")) + "\n" +
//
//
//                            "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structconsmas.SD_Interest_chngto_SD_AVAIL))) + "\n" +
//                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Cur_Bill_Total))) + "\n" +
//                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structconsmas.Pre_Financial_Yr_Arr))) + "\n" +
//                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structconsmas.Cur_Fiancial_Yr_Arr))) + "\n" +
//                        "<0x09>" + "             " + (String.format("%1$6s", "0.00")) + "\n" +
//                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.House_Lck_Adju_Amnt))) + "\n" +
//                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structconsmas.SD_Interest_chngto_SD_AVAIL))) + "\n" +
//                                "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Cur_Bill_Total))) + "\n" +
//                                "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Cur_Bill_Total))) + "\n" +
//                        "<0x09>" + "             " + (String.format("%1$6s", "0.00")) + "\n" +
//                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Amnt_Paidafter_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL))) + "\n" +
//                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Rbt_Amnt))) + "\n" +
//                        "<0x10>" + "             " + (String.format("%1$6s", String.format("%.2f", Math.ceil(Structbilling.Amnt_bPaid_on_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL)))) + "\n" +
//                        "<0x09>" + "             " + Structbilling.Due_Date +
//                        "<0x10>" + "             " + (String.format("%1$6s", String.format("%.2f", Math.ceil(Structbilling.Amnt_Paidafter_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL)))) + "\n" +
//                        "<0x09>" + "             " + (String.format("%1$6s", "0.00")) + "\n" +
//                        "<0x09>" + " " + "\n" +
//                            "<0x09>" + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + "     " + Structconsmas.Last_Pay_Receipt_Book_No + "\n" +
//                        "<0x09>" + "     " + Structconsmas.Last_Pay_Receipt_No + "\n" +
//                        "<0x09>" + "     " + Structconsmas.Last_Total_Pay_Paid + "    " + Structconsmas.Last_Pay_Date + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + "     " + Structbilling.DCNumber + " " + Structbilling.Bill_Date +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + "  " + Structbilling.Cons_Number +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + "          " + (Structconsmas.Pre_Financial_Yr_Arr + Structconsmas.Cur_Fiancial_Yr_Arr) +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + "     " + Structbilling.Bill_Month + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n" +
//                        "<0x09>" + " " + "\n");

//                    }

//            -----------------------Commented for Parallal Billing
//*****************************************************************************//
        /// paral billing
        if (Structbilling.md_input != null && !Structbilling.md_input.isEmpty ( )) {
            mdValue = Structbilling.md_input + " " + GSBilling.getInstance ( ).getUnitMaxDemand ( );
        } else {
            mdValue = "0" + " " + GSBilling.getInstance ( ).getUnitMaxDemand ( );
        }

        if (GSBilling.getInstance ( ).getPowerFactor ( ) == 0) {
            pfValue = "0";
        } else {
            pfValue = String.valueOf ( GSBilling.getInstance ( ).getPowerFactor ( ) );
        }

        if (Structconsmas.misc_charges != null) {
            miscValue = Structconsmas.misc_charges;
        } else {
            miscValue = 0.0f;
        }

        dbHelper4 = new DB ( getApplicationContext ( ) );
        SD4 = dbHelper4.getWritableDatabase ( );
        dbHelper4.insertSequence ( "DCNumber", dcSeq );
        //  dbHelper4.insertIntoBillingTable();
        dbHelper4.insertIntoMPBillingTable ( );
        dbHelper4.insertSequence ( "BillNumber", billseq );


        System.out.println ("This is Cashier Name "+Structbilling.Meter_Reader_Name );
        test = " " + "\n" +
                "  ELECTRICITY BILL     " + "\n" +
                "----------------------" + "\n" +
//                    "DC NAME:"+(String.format("%1$6s",(Structconsmas.Zone_Code))) + "\n" +
                "VERSION:" + (String.format ( "%1$6s", Structbilling.VER_CODE )) + "\n" +
                "ACC.NO. :" + (String.format ( "%1$6s", Structbilling.Cons_Number )) + "\n" +
                "DATE: " + (String.format ( "%1$6s", Structbilling.Bill_Date )) + (String.format ( "%1$6s", Structbilling.Bill_Time )) + "\n" +
//                    "MRU/GROUP:"+(String.format("%1$6s",Structconsmas.MRU)) + "\n" +
//                    "BP NO:"+(String.format("%1$6s",Structbilling.Cons_Number)) + "\n" +
//                   p "LNO:"+(String.format("%1$6s",Structconsmas.Old_Consmumer_Number))+ "\n" +
//                    "L.PAY.DATE:"+(String.format("%1$6s",formatDate(Structconsmas.Last_Payment_Date))) + "\n" +
                "---------------------" + "\n" +
                "NAME  &  SUPPLY ADDRESS:       " + "\n" +
                "" + String.format ( "%1$6s", Structconsmas.Name ) + "\n" +
                "" + String.format ( "%1$8s", Structconsmas.address1 )  +
                "" + String.format ( "%1$4s", Structconsmas.address2 ) + "\n" +
//                    ""+String.format("%1$4s", Structconsmas.Addrress3 ) + "\n" +
//                    "OFF.PH NO:"+String.format("%1$6s",Structconsmas.Office_Phone) + "\n" +
//                    "CIN:"+(String.format("%1$6s",Structconsmas.Consumer_Index_Number)) + "\n" +
//                    "TARIF:"+String.format("%1$6s",Structconsmas.rate_category_for_analogic_use) + "\n" +
//                    "USAGE:"+(String.format("%1$6s",Structconsmas.Purpose)) + "\n" +
                //  "SAN LOAD :"+String.format("%1$6s", Structconsmas.Load) + "\n" +

                "IBC:" + String.format ( "%1$6s", Structconsmas.Section_Name ) + "\n" +
                "BSC:" + String.format ( "%1$6s", Structconsmas.MOH ) + "\n" +

                "TARIF Code:" + String.format ( "%1$6s", Structconsmas.Tariff_Code ) + "\n" +

                "SAN LOAD  :" + "\n" +

//                    "S.D.HELD :"+(String.format("%1$6s",Structconsmas.SD_Held)) + "\n" +
//                    "MTR NO   :"+String.format("%1$6s", Structconsmas.Meter_S_No) + "\n" +
//                    "AVG UNIT:"+String.format("%1$4s",Structconsmas.Average_Unit_for_Defective)+" MF:"+String.format("%1$4s",Structconsmas.KWH_MF) + "\n" +
                "---------------------" + "\n" +
                "CUR    :" + Structbilling.Cur_Meter_Reading + " " + Structbilling.Bill_Date + " " + Structbilling.Cur_Meter_Stat + "\n" +
                "PRV    :" + Structconsmas.Prev_Meter_Reading + " " + Structconsmas.Prev_Meter_Reading_Date + " " + Structconsmas.Prev_Meter_Status + "\n" +

//                    "CUR:"+String.format("%1$4s",Structbilling.Cur_Meter_Reading+" /"+ appcomUtil.getBillMonth(Structconsmas.Bill_Mon)+"-"+getBillYear(Structconsmas.Bill_Year)+" /"+Structbilling.RDG_TYP_CD) + "\n" +
//                    "PRV:"+String.format("%1$4s",Structconsmas.Meter_Reading_Previous_KWH+" /"+Structconsmas.Bill_Mon+"-" +Structconsmas.Bill_Year+" /"+Structconsmas.Meter_Status) +"\n" +

                // "UNITS:"+Structbilling.Units_Consumed + "\n" +
                "UNITS  :" + Structbilling.Units_Consumed + " KWH" + "\n" +

//                    "P FACT:"+(String.format("%1$4s",Structbilling.Avrg_PF))+" R.M.D:"+(String.format("%1$4s",Structbilling.MDI))+ "\n" +
                "BILL BASIS:     " + Structbilling.Bill_Basis + "\n" +
                "---------------------" + "\n" +
//                    "FIXED CHG    :"+String.format("%1$8s",String.format("%.2f",Double.valueOf(Structbilling.Total_Energy_Charg))) + "\n" +
                "ENERGY CHG       :" + String.format ( "%1$8s", String.format ( "%1$6s", MainActivityCollectionPrint.internationAnotation ( String.valueOf ( Structbilling.Total_Energy_Charg ) ) ) ) + "\n" +
                // "VAT         :"+String.format("%1$8s",String.format("%.2f",Double.valueOf(Structbilling.Electricity_Duty_Charges))) + "\n" +
                "VAT(5%)          :" + String.format ( "%1$8s", String.format ( "%1$6s", MainActivityCollectionPrint.internationAnotation ( String.valueOf ( Structbilling.Electricity_Duty_Charges ) ) ) ) + "\n" +

//                    "CESS         :"+String.format("%1$8s",String.format("%.2f",Double.valueOf(Structbilling.CESS))) + "\n" +
//                    "M.RENT       :"+String.format("%1$8s",String.format("%.2f",Double.valueOf(Structconsmas.Meter_Rent))) + "\n" +
//                    "LT/WT S.CHG  :"+String.format("%1$8s",String.format("%.2f",Double.valueOf((Structbilling.LTCS_Charge + Structbilling.WTCS_Surcharge))))+"\n" +
//                    "PEN.CHG E+F  :"+String.format("%1$8s",String.format("%.2f",Double.valueOf(Structbilling.O_PEN))) +"\n" +
//                    "D.L. ADJ     :"+String.format("%1$8s",String.format("%.2f",Double.valueOf(Structbilling.ADV_INSTT_AMT ))) + "\n" +
//                    "REBATE       :"+String.format("%1$8s",String.format("%.2f",Double.valueOf(Structbilling.O_RebateAmount))) + "\n" +
//                    "MISC.CHG     :"+String.format("%1$8s",String.format("%.2f",Double.valueOf(Structconsmas.Manual_Demand_Misc_Charges))) + "\n" +
//                    "ASD RAISED   :"+String.format("%1$8s",String.format("%.2f",Double.valueOf(Structconsmas.Additional_Security_Raised))) + "\n" +
//                    "S.D INT.     :"+String.format("%1$8s",String.format("%.2f",Double.valueOf(Structconsmas.SD_Interest ))) + "\n" +
//                    "VCA CHARGES  :"+String.format("%1$8s",String.format("%.2f",Double.valueOf(Structbilling.VCA_Charge ))) + "\n" +
//                    "TOTAL BILL   :"+String.format("%1$8s",String.format("%.2f",Double.valueOf(Structbilling.O_Current_Demand))) + "\n" +
//                    "SD ARREAR    :"+String.format("%1$8s",String.format("%.2f",Double.valueOf(Structconsmas.SD_Arrear))) + "\n" +
                "PREV.ARREARS     :" + String.format ( "%1$8s", String.format ( "%1$6s", MainActivityCollectionPrint.internationAnotation ( String.valueOf ( Structbilling.O_Arrear_Demand ) ) ) ) + "\n" +
//                    "SCBG.ARREARS :"+String.format("%1$8s",String.format("%.2f",Double.valueOf(Structconsmas.Sucharge_Arrears ))) + "\n" +
//                    "ADJ.AMOUNT   :"+String.format("%1$8s",String.format("%.2f",Double.valueOf(Structbilling.Round_Off_amount)) ) + "\n" +
                "-----------------------" + "\n" +
                //"TOTAL AMOUNT      :"+String.format("%1$8s",String.format("%.2f",MainActivityCollectionPrint.internationAnotation(String.valueOf(Structbilling.Amnt_Paidafter_Rbt_Date)))) + "\n" +
                "TOTAL AMOUNT     :" + String.format ( "%1$8s", String.format ( "%1$6s", MainActivityCollectionPrint.internationAnotation ( String.valueOf ( Structbilling.Amnt_Paidafter_Rbt_Date ) ) ) ) + "\n" +

                "---------------------" + "\n" +
//                            "PAY TOTAL DUE NOW    :"+String.format("%1$8s",String.format("%.2f",Double.valueOf(Structbilling.Cur_Bill_Total))) + "\n" +
//                    "DUE DT BY CHQ:"+String.format("%1$8s", Structbilling.cheque_due_date ) + "\n" +
                "LAST PAY DATE    :" + String.format ( "%1$8s", (Structconsmas.LAST_ACT_BILL_MON) ) + "\n" +
                "LAST PAID AMOUNT :" + String.format ( "%1$8s", String.format ( "%1$6s", MainActivityCollectionPrint.internationAnotation ( String.valueOf ( Structconsmas.LAST_MONTH_AV ) ) ) ) + "\n" +
//                    "DUE DT BY CASH:"+String.format("%1$5s", Structbilling.cash_due_date) + "\n" +
                "READER NAME      :" + String.format ( "%1$6s", com.fedco.mbc.activity.MainActivity.MRNME ) + "\n" +



                //  "READER NAME       :"+String.format("%1$6s",Structbilling.Meter_Reader_Name) + "\n" +

                "Customer Care    :" + "070022557433" + "\n" +

                "---------------------" + "\n";

        test2 = "    " + "\n" +
                "    " + "\n";

        Log.e ( getApplicationContext ( ), "Find", " ME " + test );
    }

    private String printables(String PTR) {
        String consumerName = Structconsmas.Name;
        Log.e ( getApplicationContext ( ), TAG, "" + consumerName );
        Log.e ( getApplicationContext ( ), TAG, "" + consumerName.length ( ) );
        if (Structconsmas.Name.length ( ) < 19) {
            consumerName = Structconsmas.Name;
        } else {
            consumerName = Structconsmas.Name.substring ( 0, 19 );
        }
        String meterReaderName = Structbilling.Meter_Reader_Name;
        if (Structbilling.Meter_Reader_Name.length ( ) < 20) {
            meterReaderName = Structbilling.Meter_Reader_Name;
        } else {
            meterReaderName = Structbilling.Meter_Reader_Name.substring ( 0, 18 );
        }
        String feederDesc = Structconsmas.FDR_SHRT_DESC;
        if (Structconsmas.FDR_SHRT_DESC.length ( ) < 13) {
            feederDesc = Structconsmas.FDR_SHRT_DESC;
        } else {
            feederDesc = Structconsmas.FDR_SHRT_DESC.substring ( 0, 12 );
        }
        String address = Structconsmas.address1 + Structconsmas.address2;
        if (address.length ( ) < 24) {
            address = Structconsmas.address1 + Structconsmas.address2 + "\n";
        } else {
            address = Structconsmas.address1 + Structconsmas.address2;
        }
        String sectionName = Structconsmas.Section_Name;
        if (sectionName.length ( ) >= 19) {
            sectionName = Structconsmas.Section_Name.substring ( 0, 19 );
        } else {
            sectionName = Structconsmas.Section_Name;
        }

        String PrintString = PTR + "     " + "\n" +
                PTR + "     " + "\n" +
                PTR + "    " + "\n" +
                PTR + "    " + "\n" +
                PTR + "    " + "\n" +
                PTR + "     " + (String.format ( "%1$6s", getBillMonth ( Structconsmas.Bill_Mon ) )) + "\n" + //201706
                PTR + "     " + (String.format ( "%1$6s", Structconsmas.BILL_ISSUE_DATE )) + "  " + billTime + "\n" +
                PTR + "     " + (String.format ( "%1$6s", Structbilling.Bill_No )) + "    " + Structbilling.VER_CODE + "\n" +
                PTR + "  " + (String.format ( "%1$6s", Structbilling.Bill_Period )) + "\n" +
                PTR + "  " + "\n" +
                PTR + "    " + (String.format ( "%1$6s", Structconsmas.DIV_NAME )) + "\n" +
                PTR + "   " + (String.format ( "%1$6s", sectionName )) + "\n" +
                PTR + "      " + (String.format ( "%1$8s", Structconsmas.Cycle )) + "/" + Structconsmas.Route_Number + "\n" +
                PTR + "     " + (String.format ( "%1$6s", feederDesc )) + "\n" +
                PTR + "        " + (String.format ( "%1$9s", Structconsmas.POLE_ID )) + "\n" +
                PTR + "      " + (String.format ( "%1$9s", Structconsmas.Consumer_Number )) + "\n" +
//  PTR +                 "         " + (String.format("%1$8s", Structconsmas.LOC_CD + "-" + Structconsmas.Consumer_Number)) + "\n" +
                PTR + "       " + (String.format ( "%1$8s", appcomUtil.IVRS_NO_PRINT ( ) )) + "\n" +
//  PTR +                 "  " + (String.format("%1$6s", Structconsmas.Name)) + "\n" +
                PTR + "  " + (String.format ( "%1$6s", consumerName )) + "\n" +
                PTR + " " + (String.format ( "%1$6s", address )) + "\n" +
                PTR + "   " + (String.format ( "%1$8s", printtariif ( ) )) + "\n" +
                PTR + "     " + (String.format ( "%1$8s", Structconsmas.Load )) + getLoadType ( ) + "\n" +
                PTR + "      " + (String.format ( "%1$8s", Structconsmas.TOT_SD_HELD )) + "\n" +
                PTR + "       " + (String.format ( "%1$8s", Structconsmas.Meter_S_No )) + "\n" +
                PTR + "    " + (String.format ( "%1$8s", Structconsmas.MF )) + " / " + Structconsmas.PHASE_CD + "\n" +
//  PTR +                 " " + (String.format("%1$8s", Structconsmas.PHASE_CD)) + "\n" +
                PTR + "            " + (String.format ( "%1$4s", Structbilling.Cur_Meter_Reading_Date )) + "\n" +
                PTR + "   " + (String.format ( "%1$8s", Reading_type_code )) + "\n" +
                PTR + "               " + (String.format ( "%1$8s", Cur_Meter_Reading )) + "\n" +
                PTR + "               " + (String.format ( "%1$8s", Prev_Meter_Reading )) + "\n" +
                PTR + "     " + (String.format ( "%1$8s", mdValue )) + "      " + Structbilling.Avrg_PF + "\n" +
//   PTR +                " " + (String.format("%1$8s", Structbilling.Avrg_PF)) + "\n" +
//   PTR +                "              " + (String.format("%1$8s", Structbilling.MTR_CONSMP)) + "\n" +
                PTR + "            " + (String.format ( "%1$8s", Integer.getInteger ( Structbilling.Billed_Units ) + " / " + Integer.getInteger ( Structbilling.O_BilledUnit_Actual ) )) + "\n" +
                PTR + "               " + (String.format ( "%1$8s", Bill_Basis )) + "\n" +
                PTR + "               " + (String.format ( "%1$8s", (String.format ( "%.2f", Float.valueOf ( Structbilling.MIN_CHRG ) )) )) + "\n" +
                PTR + "               " + (String.format ( "%1$8s", (String.format ( "%.2f", Float.valueOf ( Structbilling.FIXED_CHARGE ) )) )) + "\n" +
                PTR + "               " + (String.format ( "%1$8s", (String.format ( "%.2f", Structbilling.Total_Energy_Charg )) )) + "\n" +
                PTR + "               " + (String.format ( "%1$8s", (String.format ( "%.2f", Float.valueOf ( Structbilling.O_FCA ) )) )) + "\n" +
                PTR + "               " + (String.format ( "%1$8s", (String.format ( "%.2f", Structbilling.Electricity_Duty_Charges )) )) + "\n" +
                PTR + "               " + (String.format ( "%1$8s", (String.format ( "%.2f", Structbilling.Meter_Rent )) )) + "\n" +
                PTR + "              " + (String.format ( "%1$8s", (String.format ( "%.2f", Float.valueOf ( Structconsmas.Sundry_Allow_EC ) )) )) + "\n" +
                PTR + "               " + (String.format ( "%1$8s", (String.format ( "%.2f", Float.valueOf ( Structbilling.O_OverdrwalPenalty ) )) )) + "\n" +
                PTR + "               " + (String.format ( "%1$8s", (String.format ( "%.2f", Float.valueOf ( Structbilling.O_PFPenalty ) )) )) + "\n" +
                PTR + "               " + (String.format ( "%1$8s", (String.format ( "%.2f", Float.valueOf ( Structbilling.O_welding_charges ) )) )) + "\n" +
                PTR + "               " + (String.format ( "%1$8s", (String.format ( "%.2f", Float.valueOf ( UtilAppCommon.doublecheck ( Structconsmas.SD_BILLED ) ) )) )) + "\n" +
                PTR + "               " + (String.format ( "%1$8s", (String.format ( "%.2f", Float.valueOf ( 0 ) )) )) + "\n" +
                PTR + "               " + (String.format ( "%1$8s", (String.format ( "%.2f", Float.valueOf ( Structconsmas.SD_INSTT_AMT ) * -1f )) )) + "\n" +
                PTR + "               " + (String.format ( "%1$8s", (String.format ( "%.2f", Float.valueOf ( Structbilling.O_LockCreditAmount ) * -1f )) )) + "\n" +
                PTR + "               " + (String.format ( "%1$8s", (String.format ( "%.2f", Float.valueOf ( Structbilling.O_Employee_Incentive ) * -1f )) )) + "\n" +
                PTR + "               " + (String.format ( "%1$8s", (String.format ( "%.2f", Float.valueOf ( Structbilling.O_Total_Incentives ) * -1f )) )) + "\n" +
                PTR + "               " + (String.format ( "%1$8s", (String.format ( "%.2f", Float.valueOf ( Structbilling.O_Total_Subsidy ) * -1f )) )) + "\n" +
                PTR + "               " + (String.format ( "%1$8s", (String.format ( "%.2f", Float.valueOf ( Structbilling.O_Total_Bill ) - Float.valueOf ( Structbilling.O_Arrear_Demand ) + Float.valueOf ( Structconsmas.Sundry_Allow_EC ) )) )) + "\n" +
                PTR + "               " + (String.format ( "%1$8s", (String.format ( "%.2f", Float.valueOf ( Structconsmas.Cur_Fiancial_Yr_Arr ) )) )) + "\n" +
//  PTR +                 "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structconsmas.SURCHARGE_ARREARS))))) + "\n" +
                PTR + "               " + (String.format ( "%1$8s", (String.format ( "%.2f", Float.valueOf ( printSurcharge ( ) ) )) )) + "\n" +
//   PTR +               ik,  "               " + (String.format("%1$8s", "0.00")) + "\n" +                // No diff Amount
//   PTR +                "               " + (String.format("%1$8s", Structbilling.Round_Amnt)) + "\n" +
                PTR + "               " + (String.format ( "%1$6s", (String.format ( "%.2f", Float.valueOf ( Math.round ( Double.valueOf ( Structbilling.O_Total_Bill ) ) ) )) )) + "\n" +
                PTR + "               " + (String.format ( "%1$6s", Structbilling.O_Surcharge )) + "\n" +
                PTR + "               " + (String.format ( "%1$6s", (String.format ( "%.2f", (float) Math.round ( (float) (Math.round ( Double.valueOf ( Structbilling.O_Total_Bill ) ) + Double.valueOf ( Structbilling.O_Surcharge )) ) )) )) + "\n" +  // doubt
                PTR + " " + "\n" +
                PTR + "              " + (String.format ( "%1$6s", Structconsmas.FIRST_CHQ_DUE_DATE )) + "\n" +
                PTR + "              " + (String.format ( "%1$6s", Structconsmas.FIRST_CASH_DUE_DATE )) + "\n" +
                PTR + " " + "\n" +
                PTR + "         " + (String.format ( "%1$4s", meterReaderName )) + " " + (String.format ( "%1$6s", Structbilling.SBM_No )) + "\n" +
//  PTR +                 "         " + (String.format("%1$6s", Structbilling.SBM_No)) + "\n" +
//  PTR +                 "       " + (String.format("%1$4s", Structbilling.Meter_Reader_ID)) + "\n" +
                PTR + " " + "\n" +
                PTR + " " + "\n" +
                PTR + " " + "\n" +
                PTR + " " + "\n" +
                PTR + " " + "\n" +
//                PTR + " " + "\n" +
                PTR + "             " + (String.format ( "%1$6s", getBillMonth ( Structconsmas.Bill_Mon ) )) + "\n" +
//  PTR +                 "           " + (String.format("%1$6s", Structbilling.Bill_Date)) + "\n" +
//  PTR +                 "           " + (String.format("%1$6s", Structbilling.Bill_No)) + "\n" +
                PTR + "        " + (String.format ( "%1$6s", Structconsmas.DIV_NAME )) + "\n" +
                PTR + "      " + (String.format ( "%1$6s", Structconsmas.DC_NAME )) + "\n" +
                PTR + "        " + (String.format ( "%1$8s", Structconsmas.Cycle )) + "/" + Structconsmas.Route_Number + "\n" +
                PTR + "        " + (String.format ( "%1$9s", Structconsmas.Consumer_Number )) + "\n" +
//  PTR +                 "          " + (String.format("%1$8s", Structconsmas.LOC_CD + "-" + Structconsmas.Consumer_Number)) + "\n" +
                PTR + "        " + (String.format ( "%1$8s", appcomUtil.IVRS_NO_PRINT ( ) )) + "\n" +
                PTR + " " + "\n" +
                PTR + "   " + (String.format ( "%1$6s", consumerName )) + "\n" +
                PTR + "               " + (String.format ( "%1$6s", (String.format ( "%.2f", Float.valueOf ( Math.round ( Double.valueOf ( Structbilling.O_Total_Bill ) ) ) )) )) + "\n" + " " + "\n" +
                PTR + "               " + (String.format ( "%1$6s", (String.format ( "%.2f", (float) Math.round ( (float) (Math.round ( Double.valueOf ( Structbilling.O_Total_Bill ) ) + Double.valueOf ( Structbilling.O_Surcharge )) ) )) )) + "\n" + // doubt
                PTR + " " + " " + "\n" +
                PTR + " " + " " + "\n" +
                PTR + " " + " " + "\n" +
                PTR + " " + " " + "\n" +
                PTR + " " + " " + "\n" +
                PTR + " " + " " + "\n" +
                PTR + " " + " " + "\n" +
                PTR + " " + " " + "\n" +
                PTR + " " + " " + "\n" +
                PTR + " " + " " + "\n";

        return PrintString;
    }

    private String printSurcharge() {

        if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase ( "S" )) {
            return "0";
        } else {
            return Structconsmas.SURCHARGE_ARREARS;
        }

    }

    private String dotSeparate(String value) {

        if (value.contains ( "." )) {
            String[] parts = value.split ( "\\." ); // escape .
            String part1 = parts[0];
            return part1;
        }
        return value;
    }

    private String RDG_TYP_CD_Print() {

        dbHelper = new DB ( MainActivity.this );
        SD = dbHelper.getWritableDatabase ( );

        String strPref = "SELECT STATUS FROM TBL_METERSTATUSCODE_MP WHERE BILL_BASIS='" + Structbilling.Bill_Basis + "'";

        Cursor getPref = SD.rawQuery ( strPref, null );

        if (getPref != null && getPref.moveToFirst ( )) {

            return getPref.getString ( 0 );

        } else {
            return "NO METER";
        }

    }

    private String printtariif() {
        String st1 = null;
        String printtariif = null;
        if (Structconsmas.URBAN_FLG.equalsIgnoreCase ( "U" )) {
            st1 = Structtariff.Tariff_URBAN;
        } else {
            st1 = Structtariff.Tariff_RURAL;
        }

        return Structconsmas.Tariff_Code + "-" + st1;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            onBackPressed ( );
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder ( this );
        dialog.setTitle ( "" );
        dialog.setMessage ( "Are You Sure To Exit?" );
        dialog.setCancelable ( false );
        dialog.setPositiveButton ( "YES", new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel ( );
//                btpObject.stopBluetoothService();

                UtilAppCommon ucom = new UtilAppCommon ( );
                ucom.nullyfimodelBill ( );
                ucom.nullyfimodelCon ( );
                Intent intent = new Intent ( );
                intent.setComponent ( new ComponentName ( getApplicationContext ( ), BillingtypesActivity.class ) );
                startActivity ( intent );
                overridePendingTransition ( R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left );
            }
        } );
        dialog.setNegativeButton ( "NO", new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel ( );
            }
        } );
        dialog.create ( ).show ( );
    }

    @Override
    protected void onStart() {
        super.onStart ( );
    }

    @Override
    public void onClick(View v) {

    }

    @SuppressLint("WrongConstant")
    public void checkBTP_Permissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int permissionCheck = 0;
            permissionCheck = this.checkSelfPermission ( "Manifest.permission.ACCESS_FINE_LOCATION" );
            permissionCheck += this.checkSelfPermission ( "Manifest.permission.ACCESS_COARSE_LOCATION" );
            if (permissionCheck != 0) {

                this.requestPermissions ( new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001 ); //Any number
            }
        } else {
            // Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    @Override
    public void onBluetoothStartDiscovery() {
//        mScanBtn.setEnabled(true);
//        setLogText("===> Start discovering !");
    }

    @Override
    public void onClientDisconnectSuccess() {
//        setLogText("===> Client Disconnected Success..");
//        mConnectBtn.setText("Reconnect");
    }

    @Override
    public void onNoClientConnected() {
//        setLogText("===> No client connected..");
    }

    @Override
    public void onBluetoothDeviceFound(BluetoothDevice device) {
        int i = 0;
        printerName = device.getName ( );
        if (device.getName ( ) != null && ((device.getName ( ).contains ( "QA" )) || (device.getName ( ).contains ( "ESBAA0050" )) || (device.getName ( ).contains ( "MHT-P5801" )) || (device.getName ( ).contains ( "TM-P20_001644" )) || (device.getName ( ).contains ( "SP120E" )) || (device.getName ( ).contains ( "SP120" )) || (device.getName ( ).contains ( "XL-1880" )) || (device.getName ( ).contains ( "Dual-SPP" )) || (device.getName ( ).contains ( "QSPrinter" )) || (device.getName ( ).contains ( "QSprinter" )) || (device.getName ( ).contains ( "QuantumAeon" )))) {
            String dev_name = device.getName ( ).trim ( );
            String dev_adrs = device.getAddress ( ).trim ( );
            if (device.getBondState ( ) == BluetoothDevice.BOND_BONDED) {
                if (spinAdapter.getCount ( ) == 0) {
                    spinAdapter.add ( dev_adrs );
                    spinAdapter.notifyDataSetChanged ( );
                    setLogText ( "===> Device detected : " + device.getAddress ( ) );
                }
                for (int x = 0; x < spinAdapter.getCount ( ); x++) {
                    String tmp = spinAdapter.getItem ( x ).toString ( );
                    if (tmp.equalsIgnoreCase ( dev_adrs )) {
                        i++;
                    }
                }
                if (i == 0) {
                    spinAdapter.add ( dev_adrs );
                    spinAdapter.notifyDataSetChanged ( );
                    setLogText ( "===> Device detected : " + device.getAddress ( ) );
//                    mConnectBtn.setEnabled(true);
                } else {
                    i = 0;
                }
            }

        }
    }

    public void save_mac_address(String mac) {
        if (!mac.equals ( "" )) {
            SharedPreferences.Editor editor = sharedPreferences.edit ( );
            editor.clear ( );
            editor.putString ( "BTDeviceMac", mac );
            editor.commit ( );
        }
    }

    @Override
    public void onClientConnectionSuccess() {
        setLogText ( "===> Client Connection success !" );

    }

    @Override
    public void onClientConnectionFail() {

    }

    //    public void mSendMessage(String message) {
//        // TODO Auto-generated method stub
//        Log.e(getApplicationContext(), "SLPrintAct", "MESSAGE " + message);
//        Intent myIntent = new Intent();
//        myIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
//        myIntent.putExtra("message", message);
//        myIntent.setAction("com.softland.iprintMarvel.Bluetooth.message");
//        myIntent.setType("*/*");
//        sendBroadcast(myIntent);
//
////        dbHelper.insertIntoBillingTable();
////        dbHelper.insertSequence("BillNumber", billseq);
//
//    }
    @Override
    public void onClientConnecting() {
        setLogText ( "===> Connecting.." );
    }

    @Override
    public void onBluetoothNotAvailable() {
        setLogText ( "===> Bluetooth not available on this device" );
//        mStartBT.setEnabled(false);
    }

    @Override
    public void onBatterystatuscheck(String s) {

    }

    @Override
    public void onresponsefrmBluetoothdevice(String s) {

    }

    @Override
    public void onError(String s) {

    }

    public void setLogText(String text) {
        mLogTxt.setText ( mLogTxt.getText ( ) + "\n" + text );
        mLogTxt.setText ( text + "\n" );
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789abcdef".indexOf ( c );
    }

    public void printPhoto() {
        Bitmap bmp = BitmapFactory.decodeResource ( getResources ( ), R.drawable.phedlogo );
        if (bmp != null) {
            byte[] command = Utils.decodeBitmap ( bmp );

            btpObject.sendMessage ( PrinterCommands.ESC_ALIGN_LEFT );
            btpObject.sendMessage ( command );
        } else {
            // Log.e("Print Photo error", "the file doesn't exists");
        }
    }

    public static StringBuffer bytesToString(byte[] bytes) {
        StringBuffer sBuffer = new StringBuffer ( );
        for (byte b : bytes) {
            String s = Integer.toHexString ( b & MotionEventCompat.ACTION_MASK );
            if (s.length ( ) < 2) {
                sBuffer.append ( '0' );
            }
            sBuffer.append ( new StringBuilder ( String.valueOf ( s ) ).append ( " " ).toString ( ) );
        }
        return sBuffer;
    }

    public void printQrcode() {
        byte[] btdata = null;
        try {
            btpObject.sendMessage ( PrinterCommands.ESC_ALIGN_CENTER );
            btdata = Structbilling.Cons_Number.getBytes ( "ASCII" );
//            btdata = "12345".toString().getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace ( );
        }
        String strdata = this.bytesToString ( btdata ).toString ( );
        short datalen = (short) (strdata.length ( ) + 3);
        byte pL = (byte) (datalen & MotionEventCompat.ACTION_MASK);
        byte pH = (byte) (datalen >> 8);
        //  BtService btService = MainActivity.pl;
        byte[] bArr = new byte[8];
        bArr[0] = (byte) 29;
        bArr[1] = (byte) 40;
        bArr[2] = (byte) 107;
        bArr[3] = (byte) 3;
        bArr[5] = (byte) 49;
        bArr[6] = (byte) 67;
        bArr[7] = (byte) 51;
        // btService.write(bArr);
        //btService = MainActivity.pl;
        bArr = new byte[8];
        bArr[0] = (byte) 29;
        bArr[1] = (byte) 40;
        bArr[2] = (byte) 107;
        bArr[3] = (byte) 3;
        bArr[5] = (byte) 49;
        bArr[6] = (byte) 69;
        bArr[7] = (byte) 5;
        btpObject.sendMessage ( bArr );
        byte[] qrHead = new byte[]{(byte) 29, (byte) 40, (byte) 107, pL, pH, (byte) 49, (byte) 80, (byte) 48};
        byte[] qrData = new byte[(qrHead.length + datalen)];
        System.arraycopy ( qrHead, 0, qrData, 0, qrHead.length );
        System.arraycopy ( btdata, 0, qrData, qrHead.length, btdata.length );
        btpObject.sendMessage ( qrData );
        // btService = MainActivity.pl;
        bArr = new byte[8];
        bArr[0] = (byte) 29;
        bArr[1] = (byte) 40;
        bArr[2] = (byte) 107;
        bArr[3] = (byte) 3;
        bArr[5] = (byte) 49;
        bArr[6] = (byte) 81;
        bArr[7] = (byte) 48;
        btpObject.sendMessage ( bArr );
    }

    public void printQrcodeSP() {
        byte[] btdata = null;
        try {
            btpObject.sendMessage ( PrinterCommands.ESC_ALIGN_CENTER );
//            btdata = GSBilling.getInstance().ConsumerNO.getBytes("ASCII");
            btdata = (GSBilling.getInstance ( ).ConsumerNO + "," + GSBilling.getInstance ( ).MeterNo + "," + Structcolmas.AMOUNT + "," + GSBilling.getInstance ( ).RecieptNo).getBytes ( "ASCII" );
//            btdata = "12345".toString().getBytes("ASCII");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace ( );
        }
        String strdata = this.bytesToString ( btdata ).toString ( );
        short datalen = (short) (strdata.length ( ) + 3);
        byte pL = (byte) (datalen & MotionEventCompat.ACTION_MASK);
        byte pH = (byte) (datalen >> 8);
        //  BtService btService = MainActivity.pl;
        byte[] bArr = new byte[8];
        bArr[0] = (byte) 29;
        bArr[1] = (byte) 40;
        bArr[2] = (byte) 107;
        bArr[3] = (byte) 3;
        bArr[5] = (byte) 49;
        bArr[6] = (byte) 67;
        bArr[7] = (byte) 51;
        // btService.write(bArr);
        //btService = MainActivity.pl;
        bArr = new byte[8];
        bArr[0] = (byte) 29;
        bArr[1] = (byte) 40;
        bArr[2] = (byte) 107;
        bArr[3] = (byte) 3;
        bArr[5] = (byte) 49;
        bArr[6] = (byte) 69;
        bArr[7] = (byte) 5;
        btpObject.sendMessage ( bArr );
        byte[] qrHead = new byte[]{(byte) 29, (byte) 40, (byte) 107, pL, pH, (byte) 49, (byte) 80, (byte) 48};
        byte[] qrData = new byte[(qrHead.length + datalen)];
        System.arraycopy ( qrHead, 0, qrData, 0, qrHead.length );
        System.arraycopy ( btdata, 0, qrData, qrHead.length, btdata.length );
        btpObject.sendMessage ( qrData );
        // btService = MainActivity.pl;
        bArr = new byte[8];
        bArr[0] = (byte) 29;
        bArr[1] = (byte) 40;
        bArr[2] = (byte) 107;
        bArr[3] = (byte) 3;
        bArr[5] = (byte) 49;
        bArr[6] = (byte) 81;
        bArr[7] = (byte) 48;
        btpObject.sendMessage ( bArr );
    }

    private Bitmap textToImage(String text, int width, int height) throws WriterException, NullPointerException {
        BitMatrix bitMatrix;
        Bitmap bitmap = null;
        try {
            bitMatrix = new MultiFormatWriter ( ).encode ( text, BarcodeFormat.DATA_MATRIX.QR_CODE, width, height, null );
            int bitMatrixWidth = bitMatrix.getWidth ( );
            int bitMatrixHeight = bitMatrix.getHeight ( );
            int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

            int colorWhite = 0xFFFFFFFF;
            int colorBlack = 0xFF000000;

            for (int y = 0; y < bitMatrixHeight; y++) {
                int offset = y * bitMatrixWidth;
                for (int x = 0; x < bitMatrixWidth; x++) {
                    pixels[offset + x] = bitMatrix.get ( x, y ) ? colorBlack : colorWhite;
                }
            }
            bitmap = Bitmap.createBitmap ( bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444 );
            bitmap.setPixels ( pixels, 0, width, 0, 0, bitMatrixWidth, bitMatrixHeight );

            return bitmap;
        } catch (Exception ex) {
            Toast.makeText ( getApplicationContext ( ), ex.getMessage ( ), Toast.LENGTH_LONG ).show ( );
        }
        return bitmap;
    }


    @Override
//    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        String item = adapterView.getItemAtPosition(i).toString();
//        Toast.makeText(MainActivity.this, item + "asfdasdasd", Toast.LENGTH_LONG).show();
//        checkBTP_Permissions();
//
//        if (item.contains("Click to connect BTP")) {
//        } else {
//            setLogText("===> Start client connection on device : " + item);
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            btpObject.createClient(item);
//            try {
//                Thread.sleep(3000);
//                if (regionFlag == 1) {
//                    printPhoto();  //now it is working
//                    stringBuffer.append(test);
//                     String testString[] = stringBuffer.toString().split("\n");
//                    for (String ind : testString) {
//                        android.util.Log.e(TAG, "" + ind);
//                        btpObject.sendMessage(ind);
//                        if(printerName.equalsIgnoreCase("SP120E")) {
//                            String strl = (GSBilling.getInstance().ConsumerNO + "," + GSBilling.getInstance().MeterNo + ","+ Structcolmas.AMOUNT + "," + GSBilling.getInstance().RecieptNo );
//                            Bitmap btt= null;
//                            try {
//                                btt = textToImage(strl, 500, 500);
//                            } catch (WriterException e) {
//                                e.printStackTrace();
//                            }
//                            byte[] command = Utils.decodeBitmap(Bitmap.createScaledBitmap(btt, 200, 200, false));
//                            btpObject.sendMessage(PrinterCommands.ESC_ALIGN_CENTER);
//                            btpObject.sendMessage(command);
//                            btpObject.sendMessage("\n");
//                            //   btpObject.sendMessage("\n ");
//                            //  printQrcode();
//                        }else{
//                            printQrcodeSP();
//                        }
//                    }
//
////                    for (int j = 0; j < 18; j++) {
////                        btpObject.sendMessage(testString[j]);
////                        btpObject.varblankdotlinespace(singleVarLinefeed);
////                    }
////                    for (int k = 18; k < 19; k++) {
////                        btpObject.sendMessage(testString[k]);
////                        btpObject.varblankdotlinespace(doubleVarlinefeed);
////                    }
////                    for (int m = 19; m < 20; m++) {
////                        btpObject.sendMessage(testString[m]);
////                        btpObject.varblankdotlinespace(doubleVarlinefeed);
////                    }
////                    btpObject.varblankdotlinespace(singleVarLinefeed);
////                    for (int n = 20; n < 21; n++) {
////                        btpObject.sendMessage(testString[n]);
////                        btpObject.varblankdotlinespace(singleVarLinefeed);
////                    }
////                    btpObject.varblankdotlinespace(singleVarLinefeed);
////                    for (int o = 21; o < 22; o++) {
////                        btpObject.sendMessage(testString[o]);
////                        btpObject.varblankdotlinespace(singleVarLinefeed);
////                    }
////                    btpObject.varblankdotlinespace(tripleVarlinefeed);
////                    for (int p = 22; p < 23; p++) {
////                        btpObject.sendMessage(testString[p]);
////                        btpObject.varblankdotlinespace(singleVarLinefeed);
////                    }
////                    for (int q = 23; q < 24; q++) {
////                        btpObject.sendMessage(testString[q]);
////                        btpObject.varblankdotlinespace(singleVarLinefeed);
////                    }
////                    btpObject.varblankdotlinespace(singleVarLinefeed);
////                    for (int r = 24; r < testString.length; r++) {
////                        btpObject.sendMessage(testString[r]);
////                        btpObject.varblankdotlinespace(singleVarLinefeed);
////                    }
//                    regionFlag = 0;
//                } else if (regionFlag == 2) {
//                    printPhoto();
//                    stringBuffer.append(test);
//
//                    String testString[] = stringBuffer.toString().split("\n");
//                    for (String ind : testString) {
//                        android.util.Log.e(TAG, "" + ind);
//                        btpObject.sendMessage(ind);
//                        if(printerName.equalsIgnoreCase("SP120E")) {
//                            String strl = (GSBilling.getInstance().ConsumerNO + "," + GSBilling.getInstance().MeterNo + ","+ Structcolmas.AMOUNT + "," + GSBilling.getInstance().RecieptNo );
//                            Bitmap btt= null;
//                            try {
//                                btt = textToImage(strl, 500, 500);
//                            } catch (WriterException e) {
//                                e.printStackTrace();
//                            }
//                            byte[] command = Utils.decodeBitmap(Bitmap.createScaledBitmap(btt, 200, 200, false));
//                            btpObject.sendMessage(PrinterCommands.ESC_ALIGN_CENTER);
//                            btpObject.sendMessage(command);
//                            btpObject.sendMessage("\n");
//                            //   btpObject.sendMessage("\n ");
//                            //  printQrcode();
//                        }else{
//                            printQrcodeSP();
//                        }
//                    }
//
////                    for (int j = 0; j < 18; j++) {
////                        btpObject.sendMessage(testString[j]);
////                        btpObject.varblankdotlinespace(singleVarLinefeed);
////                    }
////                    btpObject.varblankdotlinespace(singleVarLinefeed);
////                    for (int k = 18; k < 19; k++) {
////                        btpObject.sendMessage(testString[k]);
////                        btpObject.varblankdotlinespace(doubleVarlinefeed);
////                    }
////                    for (int m = 19; m < 20; m++) {
////                        btpObject.sendMessage(testString[m]);
////                        btpObject.varblankdotlinespace(doubleVarlinefeed);
////                    }
////                    btpObject.varblankdotlinespace(singleVarLinefeed);
////                    for (int n = 20; n < 21; n++) {
////                        btpObject.sendMessage(testString[n]);
////                        btpObject.varblankdotlinespace(doubleVarlinefeed);
////                    }
////                    btpObject.varblankdotlinespace(singleVarLinefeed);
////                    for (int o = 21; o < 22; o++) {
////                        btpObject.sendMessage(testString[o]);
////                        btpObject.varblankdotlinespace(doubleVarlinefeed);
////                    }
////                    for (int p = 22; p < 23; p++) {
////                        btpObject.sendMessage(testString[p]);
////                        btpObject.varblankdotlinespace(singleVarLinefeed);
////                    }
////                    btpObject.varblankdotlinespace(fourVarlinefeed);
////                    for (int q = 23; q < testString.length; q++) {
////                        btpObject.sendMessage(testString[q]);
////                        btpObject.varblankdotlinespace(singleVarLinefeed);
////                    }
////                    btpObject.varblankdotlinespace(singleVarLinefeed);
//                    regionFlag = 0;
//                } else if (regionFlag == 3) {
//                    printPhoto();
//                    stringBuffer.append(test);
//
//                    String testString[] = stringBuffer.toString().split("\n");
//                    for (String ind : testString) {
//                        android.util.Log.e(TAG, "" + ind);
//                        btpObject.sendMessage(ind); if(printerName.equalsIgnoreCase("SP120E")) {
//                            String strl = (GSBilling.getInstance().ConsumerNO + "," + GSBilling.getInstance().MeterNo + ","+ Structcolmas.AMOUNT + "," + GSBilling.getInstance().RecieptNo );
//                            Bitmap btt= null;
//                            try {
//                                btt = textToImage(strl, 500, 500);
//                            } catch (WriterException e) {
//                                e.printStackTrace();
//                            }
//                            byte[] command = Utils.decodeBitmap(Bitmap.createScaledBitmap(btt, 200, 200, false));
//                            btpObject.sendMessage(PrinterCommands.ESC_ALIGN_CENTER);
//                            btpObject.sendMessage(command);
//                            btpObject.sendMessage("\n");
//                            //   btpObject.sendMessage("\n ");
//                            //  printQrcode();
//                        }else{
//                            printQrcodeSP();
//                        }
//                    }
//
////                    for (int j = 0; j < 18; j++) {
////                        btpObject.sendMessage(testString[j]);
////                                           }
//
//////                    btpObject.varblankdotlinespace(doubleVarlinefeed);
////                    for (int k = 18; k < 19; k++) {
////                        btpObject.sendMessage(testString[k]);
////                        btpObject.varblankdotlinespace(doubleVarlinefeed);
////                    }
////                    for (int m = 19; m < 20; m++) {
////                        btpObject.sendMessage(testString[m]);
////                        btpObject.varblankdotlinespace(doubleVarlinefeed);
////                    }
////                    btpObject.varblankdotlinespace(singleVarLinefeed);
////                    for (int n = 20; n < 21; n++) {
////                        btpObject.sendMessage(testString[n]);
////                        btpObject.varblankdotlinespace(singleVarLinefeed);
////                    }
////                    btpObject.varblankdotlinespace(singleVarLinefeed);
////                    for (int o = 21; o < 22; o++) {
////                        btpObject.sendMessage(testString[o]);
////                        btpObject.varblankdotlinespace(sixVarlinefeed);
////                    }
////                    btpObject.varblankdotlinespace(singleVarLinefeed);
////                    for (int p = 22; p < testString.length - 1; p++) {
////                        btpObject.sendMessage(testString[p]);
////                        btpObject.varblankdotlinespace(singleVarLinefeed);
////                    }
////                    btpObject.varblankdotlinespace(5);
////                    btpObject.varblankdotlinespace(5);
//                    regionFlag = 0;
//                } else if (indoreFlag == 1) {
//                    printPhoto();
//                    stringBuffer.append(test);
////                    btpObject.varblankdotlinespace(doubleVarlinefeed);
//                    String testString[] = stringBuffer.toString().split("\n");
//                    for (String ind : testString) {
//                        android.util.Log.e(TAG, "" + ind);
//                        btpObject.sendMessage(ind);
//                        if(printerName.equalsIgnoreCase("SP120E")) {
//                            String strl = (GSBilling.getInstance().ConsumerNO + "," + GSBilling.getInstance().MeterNo + ","+ Structcolmas.AMOUNT + "," + GSBilling.getInstance().RecieptNo );
//                            Bitmap btt= null;
//                            try {
//                                btt = textToImage(strl, 500, 500);
//                            } catch (WriterException e) {
//                                e.printStackTrace();
//                            }
//                            byte[] command = Utils.decodeBitmap(Bitmap.createScaledBitmap(btt, 200, 200, false));
//                            btpObject.sendMessage(PrinterCommands.ESC_ALIGN_CENTER);
//                            btpObject.sendMessage(command);
//                            btpObject.sendMessage("\n");
//                            //   btpObject.sendMessage("\n ");
//                            //  printQrcode();
//                        }else{
//                            printQrcodeSP();
//                        }
//                    }
//
////                    for (int r = 0; r < 24; r++) {
////                        btpObject.sendMessage(testString[r]);
////                        btpObject.varblankdotlinespace(singleVarLinefeed);
////                    }
////                    btpObject.varblankdotlinespace(tripleVarlinefeed);
////                    for (int s = 24; s < 64; s++) {
////                        btpObject.sendMessage(testString[s]);
////                        btpObject.varblankdotlinespace(singleVarLinefeed);
////                    }
////                    btpObject.varblankdotlinespace(doubleVarlinefeed);
////                    for (int t = 64; t < testString.length - 1; t++) {
////                        btpObject.sendMessage(testString[t]);
////                        btpObject.varblankdotlinespace(singleVarLinefeed);
////                    }
////                    btpObject.varblankdotlinespace(5);
////                    btpObject.varblankdotlinespace(5);
//                    indoreFlag = 0;
//                } else {
//                    //printer reads from here
//                   stringBuffer.append(test);
//                    printPhoto();
////                    printQrcode();
//                    String testString[] = stringBuffer.toString().split("\n");
//                    for (String ind : testString) {
//                        android.util.Log.e(TAG, "" + ind);
//                        btpObject.sendMessage(ind);
////                        btpObject.varblankdotlinespace(singleVarLinefeed);
//                        if(printerName.equalsIgnoreCase("SP120E")) {
//                            String strl = (GSBilling.getInstance().ConsumerNO + "," + GSBilling.getInstance().MeterNo + ","+ Structcolmas.AMOUNT + "," + GSBilling.getInstance().RecieptNo );
//                            Bitmap btt= null;
//                            try {
//                                btt = textToImage(strl, 500, 500);
//                            } catch (WriterException e) {
//                                e.printStackTrace();
//                            }
//                            byte[] command = Utils.decodeBitmap(Bitmap.createScaledBitmap(btt, 200, 200, false));
//                            btpObject.sendMessage(PrinterCommands.ESC_ALIGN_CENTER);
//                            btpObject.sendMessage(command);
//                            btpObject.sendMessage("\n");
//                            //   btpObject.sendMessage("\n ");
//                            //  printQrcode();
//                        }else{
//                            printQrcodeSP();
//                        }
//                    }
//
//                }
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                btpObject.closeConnection();
//                new PrintProcess().execute();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
    public void onItemSelected(AdapterView <?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition ( i ).toString ( );
        Toast.makeText ( MainActivity.this, item + "asfdasdasd", Toast.LENGTH_LONG ).show ( );
        checkBTP_Permissions ( );

        if (item.contains ( "Click to connect BTP" )) {
        } else {
            setLogText ( "===> Start client connection on device : " + item );
            try {
                Thread.sleep ( 2000 );
            } catch (InterruptedException e) {
                e.printStackTrace ( );
            }
            btpObject.createClient ( item );
            try {
                Thread.sleep ( 3000 );
//                String testString[] = test.split("\n");
                if (test.length ( ) > 100) {
                    printPhoto ( );
                    btpObject.sendMessage ( test );
                    if (printerName.equalsIgnoreCase ( "SP120E" )) {
                        String strl = (GSBilling.getInstance ( ).ConsumerNO + "," + GSBilling.getInstance ( ).MeterNo + "," + Structcolmas.AMOUNT + "," + GSBilling.getInstance ( ).RecieptNo);
                        Bitmap btt = textToImage ( strl, 500, 500 );
                        byte[] command = Utils.decodeBitmap ( Bitmap.createScaledBitmap ( btt, 200, 200, false ) );
                        btpObject.sendMessage ( PrinterCommands.ESC_ALIGN_CENTER );
                        btpObject.sendMessage ( command );
                        btpObject.sendMessage ( "\n" );
                        //   btpObject.sendMessage("\n ");
                        //  printQrcode();
                    } else {
                        printQrcodeSP ( );
                    }
                    btpObject.sendMessage(test2);
                }
//                for (int j = 0; j < testString.length; j++) {
//                    btpObject.sendMessage(testString[j]);
//                 //   final int singleVarLinefeed = 1;
//                 //   btpObject.varblankdotlinespace(singleVarLinefeed);
//                }
                try {
                    Thread.sleep ( 2000 );
                } catch (InterruptedException e) {
                    e.printStackTrace ( );
                }
                if (printerName.equalsIgnoreCase ( "SP120E" )) {

                } else {
                    btpObject.closeConnection ( );
                }
//                btpObject.closeConnection();
                new MainActivity.PrintProcess ( ).execute ( );
            } catch (InterruptedException e) {
                e.printStackTrace ( );
            } catch (WriterException e) {
                e.printStackTrace ( );
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView <?> adapterView) {

    }

    /*--------------------- Billing Masters Download Initiation ----------------------------------*/
    private class PrintProcess extends AsyncTask <String, Void, String> {
        /* displays the progress dialog until background task is completed*/
        private SimpleDateFormat simpleDateFormat;

        private Date connvertedDate;
        private DateFormat dateFormat;
        private String actualDate;

        @Override
        protected String doInBackground(String... params) {

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            sDialog = new SweetAlertDialog ( MainActivity.this, SweetAlertDialog.SUCCESS_TYPE );
            sDialog.setTitleText ( "Bluetooth Printer" );
            sDialog.setContentText ( "Successfully Printed" );
            sDialog.show ( );
            sDialog.setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismissWithAnimation ( );

                    UtilAppCommon ucom = new UtilAppCommon ( );
                    ucom.nullyfimodelCon ( );
                    ucom.nullyfimodelBill ( );

                    if (isMobileDataEnabled ( )) {

                        new TextFileClass ( MainActivity.this ).execute ( );

                    } else {

                        Toast.makeText ( MainActivity.this, "Internally stored due to no connectivity", Toast.LENGTH_SHORT ).show ( );

                        Intent intent = new Intent ( MainActivity.this, BillingtypesActivity.class );
                        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                        intent.putExtra ( "printtype", print_type );
                        startActivity ( intent );
                        overridePendingTransition ( R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left );
                    }
                }
            } );


//            sDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {


        }


    }

    private class TextFileClass extends AsyncTask <String, Void, Void> {

        private final Context context;

        public TextFileClass(Context c) {

            this.context = c;
//            this.error = status;
//            this.type = t;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog ( this.context );
            progress.setMessage ( "Please Wait.." );
            progress.show ( );
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                dbHelper2 = new DB ( getApplicationContext ( ) );
                SD2 = dbHelper2.getWritableDatabase ( );

                String selquer = "SELECT * FROM TBL_BILLING WHERE Upload_Flag='N' ";//WHERE Upload_Flag='N'
                Cursor curBillselect = SD2.rawQuery ( selquer, null );
                String arrStr[] = null;
                ArrayList <String> mylist = new ArrayList <String> ( );
                ArrayList <String> mylist1 = new ArrayList <String> ( );


                if (curBillselect != null && curBillselect.moveToFirst ( )) {
                    int i = 0;

                    Log.e ( getApplicationContext ( ), "SLPrintAct", "UpdateFLag to N : " + curBillselect.getString ( 0 ) );

                    while (curBillselect.isAfterLast ( ) == false) {
                        Calendar c = Calendar.getInstance ( );
                        System.out.println ( "Current time => " + c.getTime ( ) );
                        i = i + 1;
                        SimpleDateFormat df = new SimpleDateFormat ( "dd-MMM-yy" );
                        String formattedDate = df.format ( c.getTime ( ) );
                        String column_24 = String.valueOf ( Double.valueOf ( curBillselect.getString ( 24 ) ) + Double.valueOf ( curBillselect.getString ( 31 ) ) );


                        mylist.add ( curBillselect.getString ( 0 ) + "}" + curBillselect.getString ( 1 ) + "}" + curBillselect.getString ( 2 ) + "}" +
                                curBillselect.getString ( 3 ) + "}" + curBillselect.getString ( 4 ) + "}" + curBillselect.getString ( 5 ) + "}" +
                                curBillselect.getString ( 6 ) + "}" + curBillselect.getString ( 7 ) + "}" + curBillselect.getString ( 8 ) + "}" +
                                curBillselect.getString ( 9 ) + "}" + curBillselect.getString ( 10 ) + "}" + curBillselect.getString ( 11 ) + "}" +
                                curBillselect.getString ( 12 ) + "}" + curBillselect.getString ( 13 ) + "}" + curBillselect.getString ( 14 ) + "}" +
                                curBillselect.getString ( 15 ) + "}" + curBillselect.getString ( 16 ) + "}" + curBillselect.getString ( 17 ) + "}" +
                                curBillselect.getString ( 18 ) + "}" + curBillselect.getString ( 19 ) + "}" + curBillselect.getString ( 20 ) + "}" +
                                curBillselect.getString ( 21 ) + "}" + curBillselect.getString ( 22 ) + "}" + curBillselect.getString ( 23 ) + "}" +
                                curBillselect.getString ( 24 ) + "}" + curBillselect.getString ( 25 ) + "}" + curBillselect.getString ( 26 ) + "}" +
                                curBillselect.getString ( 27 ) + "}" + curBillselect.getString ( 28 ) + "}" + curBillselect.getString ( 29 ) + "}" +
                                curBillselect.getString ( 30 ) + "}" + curBillselect.getString ( 31 ) + "}" + curBillselect.getString ( 32 ) + "}" +
                                curBillselect.getString ( 33 ) + "}" + curBillselect.getString ( 34 ) + "}" + curBillselect.getString ( 35 ) + "}" +
                                curBillselect.getString ( 36 ) + "}" + curBillselect.getString ( 37 ) + "}" + curBillselect.getString ( 38 ) + "}" +
                                curBillselect.getString ( 39 ) + "}" + curBillselect.getString ( 40 ) + "}" + curBillselect.getString ( 41 ) + "}" +
                                curBillselect.getString ( 42 ) + "}" + curBillselect.getString ( 43 ) + "}" + curBillselect.getString ( 44 ) + "}" +
                                curBillselect.getString ( 45 ) + "}" + curBillselect.getString ( 46 ) + "}" + curBillselect.getString ( 47 ) + "}" +
                                curBillselect.getString ( 48 ) + "}" + curBillselect.getString ( 49 ) + "}" + curBillselect.getString ( 50 ) + "}" +
                                curBillselect.getString ( 51 ) + "}" + curBillselect.getString ( 52 ) + "}" + curBillselect.getString ( 53 ) + "}" +
                                curBillselect.getString ( 54 ) + "}" + curBillselect.getString ( 55 ) + "}" + curBillselect.getString ( 56 ) + "}" +
                                curBillselect.getString ( 57 ) + "}" + curBillselect.getString ( 58 ) + "}" + curBillselect.getString ( 59 ) + "}" +
                                curBillselect.getString ( 60 ) + "}" + curBillselect.getString ( 61 ) + "}" + curBillselect.getString ( 62 ) + "}" +
                                curBillselect.getString ( 63 ) + "}" + curBillselect.getString ( 64 ) + "}" + curBillselect.getString ( 65 ) + "}" +
                                curBillselect.getString ( 66 ) + "}" + curBillselect.getString ( 67 ) + "}" + curBillselect.getString ( 68 ) + "}" +
                                curBillselect.getString ( 69 ) + "}" + curBillselect.getString ( 70 ) + "}" + curBillselect.getString ( 71 ) + "}" +
                                curBillselect.getString ( 72 ) + "}" + curBillselect.getString ( 73 ) + "}" + curBillselect.getString ( 74 ) + "}" +
                                curBillselect.getString ( 75 ) + "}" + curBillselect.getString ( 76 ) + "}" + curBillselect.getString ( 77 ) + "}" +
                                curBillselect.getString ( 78 ) + "}" + curBillselect.getString ( 79 ) + "}" + curBillselect.getString ( 80 ) + "}" +
                                curBillselect.getString ( 81 ) + "}" + curBillselect.getString ( 82 ) + "}" + curBillselect.getString ( 83 ) + "}" +
                                curBillselect.getString ( 84 ) + "}" + curBillselect.getString ( 85 ) + "}" + curBillselect.getString ( 86 ) + "}" +
                                curBillselect.getString ( 87 ) + "}" + curBillselect.getString ( 88 ) + "}" + curBillselect.getString ( 89 ) + "}" +
                                curBillselect.getString ( 90 ) + "}" + curBillselect.getString ( 91 ) + "}" + curBillselect.getString ( 92 ) + "}" +
                                curBillselect.getString ( 93 ) + "}" + curBillselect.getString ( 94 ) + "}" + curBillselect.getString ( 95 ) + "}" +
                                curBillselect.getString ( 96 ) + "}" + curBillselect.getString ( 97 ) + "}" + curBillselect.getString ( 98 ) + "}" +
                                curBillselect.getString ( 99 ) + "}" + curBillselect.getString ( 100 ) + "}" + curBillselect.getString ( 101 ) + "}" +
                                curBillselect.getString ( 102 ) + "}" + curBillselect.getString ( 103 ) + "}" + curBillselect.getString ( 104 ) + "}" +
                                curBillselect.getString ( 105 ) + "}" + curBillselect.getString ( 106 ) + "}" + curBillselect.getString ( 107 ) + "}" +
                                curBillselect.getString ( 108 ) + "}" + curBillselect.getString ( 109 ) + "}" + curBillselect.getString ( 110 ) + "}" +
                                curBillselect.getString ( 111 ) + "}" + curBillselect.getString ( 112 ) + "}" + curBillselect.getString ( 113 ) + "}" +
                                curBillselect.getString ( 114 ) + "}" + curBillselect.getString ( 115 ) + "}" + curBillselect.getString ( 116 ) + "}" +
                                curBillselect.getString ( 117 ) + "}" + curBillselect.getString ( 118 ) + "}" + curBillselect.getString ( 119 ) + "}" +
                                curBillselect.getString ( 120 ) + "}" + curBillselect.getString ( 121 ) + "}" + curBillselect.getString ( 122 ) + "}" +
                                curBillselect.getString ( 123 ) + "}" + curBillselect.getString ( 124 ) + "}" + curBillselect.getString ( 125 ) + "}" +
                                curBillselect.getString ( 126 ) + "}" + curBillselect.getString ( 127 ) + "}" + curBillselect.getString ( 128 ) + "}" +
                                curBillselect.getString ( 129 ) + "}" + curBillselect.getString ( 130 ) + "}" + curBillselect.getString ( 131 ) + "}" +
                                curBillselect.getString ( 132 ) + "}" + curBillselect.getString ( 133 ) + "}" + curBillselect.getString ( 134 ) + "}" +
                                curBillselect.getString ( 135 ) + "}" + curBillselect.getString ( 136 ) + "}" + curBillselect.getString ( 137 ) + "}" +
                                curBillselect.getString ( 138 ) + "}" + curBillselect.getString ( 139 ) + "}" + curBillselect.getString ( 140 ) + "}" +
                                curBillselect.getString ( 141 ) + "}" + curBillselect.getString ( 142 ) + "}" + curBillselect.getString ( 143 ) + "}" +
                                curBillselect.getString ( 144 ) + "}" + curBillselect.getString ( 145 ) + "}" + curBillselect.getString ( 146 ) + "}" +
                                curBillselect.getString ( 147 ) + "}" + curBillselect.getString ( 148 ) + "}" + curBillselect.getString ( 149 ) + "}" +
                                curBillselect.getString ( 150 ) + "}" + curBillselect.getString ( 151 ) + "}" + curBillselect.getString ( 152 ) + "}" +
                                curBillselect.getString ( 153 ) + "}" + curBillselect.getString ( 154 ) + "}" + curBillselect.getString ( 155 ) + "}" +
                                curBillselect.getString ( 156 ) + "}" + curBillselect.getString ( 157 ) + "}" + curBillselect.getString ( 158 ) + "}" +
                                curBillselect.getString ( 159 ) + "}" + curBillselect.getString ( 160 ) + "}" + curBillselect.getString ( 161 ) + "}" +
                                curBillselect.getString ( 162 ) + "}" + curBillselect.getString ( 163 ) + "}" + curBillselect.getString ( 164 ) + "}" +
                                curBillselect.getString ( 165 ) + "}" + curBillselect.getString ( 166 ) + "}" + curBillselect.getString ( 167 ) + "}" +
                                curBillselect.getString ( 168 ) + "}" + curBillselect.getString ( 169 ) + "}" + curBillselect.getString ( 170 ) + "}" +
                                curBillselect.getString ( 171 ) + "}" + curBillselect.getString ( 172 ) + "}" + curBillselect.getString ( 173 ) + "}" +
                                curBillselect.getString ( 174 ) + "}" + curBillselect.getString ( 175 ) + "}" + curBillselect.getString ( 176 ) + "}" +
                                curBillselect.getString ( 177 ) + "}" + curBillselect.getString ( 178 ) + "}" + curBillselect.getString ( 179 ) + "}" +
                                curBillselect.getString ( 180 ) + "}" + curBillselect.getString ( 181 ) + "}" + curBillselect.getString ( 182 ) + "}" +
                                curBillselect.getString ( 183 ) + "}" + curBillselect.getString ( 184 ) + "}" + curBillselect.getString ( 185 ) + "}" +
                                curBillselect.getString ( 186 ) + "}" + curBillselect.getString ( 187 ) + "}" + curBillselect.getString ( 188 ) + "}" +
                                curBillselect.getString ( 189 ) + "}" + curBillselect.getString ( 190 ) + "}" + curBillselect.getString ( 191 ) + "}" +
                                curBillselect.getString ( 192 ) + "}" + curBillselect.getString ( 193 ) + "}" + curBillselect.getString ( 194 ) + "}" +
                                curBillselect.getString ( 195 ) + "}" + curBillselect.getString ( 196 ) + "}" + curBillselect.getString ( 197 ) + "}" +
                                curBillselect.getString ( 198 ) + "}" + curBillselect.getString ( 199 ) + "}" + curBillselect.getString ( 200 ) + "}" +
                                curBillselect.getString ( 201 ) + "}" + curBillselect.getString ( 202 ) + "}" + curBillselect.getString ( 203 ) + "}" +
                                curBillselect.getString ( 204 ) + "}" + curBillselect.getString ( 205 ) + "}" + curBillselect.getString ( 206 ) + "}" +
                                curBillselect.getString ( 207 ) + "}" + curBillselect.getString ( 208 ) + "}" + curBillselect.getString ( 209 ) + "}" +
                                curBillselect.getString ( 210 ) + "}" + curBillselect.getString ( 211 ) + "}" + curBillselect.getString ( 212 ) + "}" +
                                curBillselect.getString ( 213 ) + "}" + curBillselect.getString ( 214 ) + "}" + curBillselect.getString ( 215 ) + "}" +
                                curBillselect.getString ( 216 ) );


                        mylist1.add ( curBillselect.getString ( 60 ) + "$" + curBillselect.getString ( 0 ) + "$" + curBillselect.getString ( 5 ) + "$" +
                                curBillselect.getString ( 61 ) + "$" + curBillselect.getString ( 11 ) + "$" + curBillselect.getString ( 62 ) + "$" +
                                curBillselect.getString ( 2 ) + "$" + i + "$" + curBillselect.getString ( 7 ) + "$" +
                                curBillselect.getString ( 212 ) + "$" + curBillselect.getString ( 1 ) + "$" + formattedDate + "$" +
                                curBillselect.getString ( 63 ) + "$" + curBillselect.getString ( 8 ) + "$" + curBillselect.getString ( 64 ) + "$" +
                                curBillselect.getString ( 65 ) + "$" + curBillselect.getString ( 66 ) + "$" + curBillselect.getString ( 39 ) + "$" +
                                curBillselect.getString ( 10 ) + "$" + curBillselect.getString ( 67 ) + "$" + curBillselect.getString ( 68 ) + "$" +
                                curBillselect.getString ( 69 ) + "$" + curBillselect.getString ( 70 ) + "$" + curBillselect.getString ( 71 ) + "$" +
                                curBillselect.getString ( 182 ) + "$" + curBillselect.getString ( 72 ) + "$" + curBillselect.getString ( 73 ) + "$" +
                                column_24 + "$" + curBillselect.getString ( 74 ) + "$" + "0" + "$" + curBillselect.getString ( 75 ) + "$" +
                                curBillselect.getString ( 76 ) + "$" + curBillselect.getString ( 27 ) + "$" + curBillselect.getString ( 77 ) + "$" +
                                curBillselect.getString ( 26 ) + "$" + curBillselect.getString ( 78 ) + "$" + curBillselect.getString ( 79 ) + "$" +
                                curBillselect.getString ( 80 ) + "$" + curBillselect.getString ( 81 ) + "$" + curBillselect.getString ( 82 ) + "$" +
                                curBillselect.getString ( 201 ) + "$" + curBillselect.getString ( 83 ) + "$" + curBillselect.getString ( 179 ) + "$" +
                                curBillselect.getString ( 85 ) + "$" + curBillselect.getString ( 86 ) + "$" + curBillselect.getString ( 169 ) + "$" +
                                curBillselect.getString ( 87 ) + "$" + curBillselect.getString ( 88 ) + "$" + curBillselect.getString ( 89 ) + "$" +
                                curBillselect.getString ( 90 ) + "$" + curBillselect.getString ( 91 ) + "$" + curBillselect.getString ( 92 ) + "$" +
                                curBillselect.getString ( 93 ) + "$" + curBillselect.getString ( 94 ) + "$" + curBillselect.getString ( 95 ) + "$" +
                                curBillselect.getString ( 96 ) + "$" + curBillselect.getString ( 97 ) + "$" + curBillselect.getString ( 98 ) + "$" +
                                curBillselect.getString ( 99 ) + "$" + curBillselect.getString ( 178 ) + "$0$" );

//                        mylistbck.add(curBillselect.getString(44)+ "|" +curBillselect.getString(50) );

                        moveFile ( ZipSourcePath, curBillselect.getString ( 48 ), ZipCopyPath );
                        moveFile ( ZipSourcePath, curBillselect.getString ( 49 ), ZipCopyPath );

                        curBillselect.moveToNext ( );
                    }

                    generateNoteOnSD ( getApplicationContext ( ), "billing.csv", mylist );
                    generateNoteOnSD ( getApplicationContext ( ), "billing1.csv", mylist1 );
//                    generatebackupNoteOnSD(getApplicationContext(), "mbc_Ob.csv", mylist);

                }

                MainActivity.this.runOnUiThread ( new Runnable ( ) {

                    @Override
                    public void run() {
                        progress.dismiss ( );
                        new PostClass ( MainActivity.this ).execute ( );
                    }
                } );


            } catch (NullPointerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace ( );
            }
            return null;
        }

        protected void onPostExecute() {
            progress.dismiss ( );
//            new PostClass(SDActivity.this).execute();
        }

    }

    private void moveFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File ( outputPath );
            if (!dir.exists ( )) {
                dir.mkdirs ( );
            }


            in = new FileInputStream ( inputPath + inputFile );
            out = new FileOutputStream ( outputPath + inputFile );

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read ( buffer )) != -1) {
                out.write ( buffer, 0, read );
            }
            in.close ( );
            in = null;

            // write the output file
            out.flush ( );
            out.close ( );
            out = null;

////             delete the original file
//            new File(inputPath + inputFile).delete();


        } catch (FileNotFoundException e) {
            e.printStackTrace ( );
        } catch (IOException e) {
            e.printStackTrace ( );
        }
    }

    private void deleteFile(String inputPath, String inputFile) {
        try {
            // delete the original file
            new File ( inputPath + inputFile ).delete ( );


        } catch (Exception e) {
            Log.e ( getApplicationContext ( ), "SLPrintAct", "tag" + e.getMessage ( ) );
        }
    }

    public void generateLastREC(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File ( Environment.getExternalStorageDirectory ( ), "mrcn/last/" );
            if (!root.exists ( )) {
                root.mkdirs ( );
            }
            File gpxfile = new File ( root, sFileName );
            FileWriter writer = new FileWriter ( gpxfile );
            int length = sBody.size ( );
            for (int i = 0; i < length; i++) {
//                System.out.println("selqwer1234 " + sBody.get(i));

                writer.append ( sBody.get ( i ).toString ( ) );
                writer.append ( "\n" );
            }

            writer.flush ( );
            writer.close ( );
            System.out.println ( "success file" );

//                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace ( );
        }
    }

    public void generateNoteOnSD(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File ( Environment.getExternalStorageDirectory ( ), "MBC/Downloadsingular/" );
            if (!root.exists ( )) {
                root.mkdirs ( );
            }
            File gpxfile = new File ( root, sFileName );
            FileWriter writer = new FileWriter ( gpxfile );
            int length = sBody.size ( );
            for (int i = 0; i < length; i++) {
                System.out.println ( "selqwer1234 " + sBody.get ( i ) );

                writer.append ( sBody.get ( i ).toString ( ) );
                writer.append ( "\n" );
            }

            writer.flush ( );
            writer.close ( );

        } catch (IOException e) {
            e.printStackTrace ( );
        }
    }

    public void generatebackupNoteOnSD(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File ( Environment.getExternalStorageDirectory ( ), "MBC/" );
            if (!root.exists ( )) {
                root.mkdirs ( );
            }
            File gpxfile = new File ( root, sFileName );
            FileWriter writer = new FileWriter ( gpxfile );
            int length = sBody.size ( );
            for (int i = 0; i < length; i++) {
                System.out.println ( "selqwer1234 " + sBody.get ( i ) );
                BufferedWriter writerbuf = new BufferedWriter ( new FileWriter ( gpxfile, true ) );
                writerbuf.write ( sBody.get ( i ).toString ( ) );
                writerbuf.close ( );
//                writer.append(sBody.get(i).toString());
//                writer.append("\n");
            }

            writer.flush ( );
            writer.close ( );

        } catch (IOException e) {
            e.printStackTrace ( );
        }
    }

    private class PostClass extends AsyncTask <String, Void, Boolean> {

        private final Context context;
        public String succsess = null;

        public PostClass(Context c) {

            this.context = c;

        }

        protected void onPreExecute() {
            progress = new ProgressDialog ( this.context );
            progress.setMessage ( "Sending Data..." );
            progress.show ( );

            dbHelper3 = new DB ( getApplicationContext ( ) );
            SD3 = dbHelper3.getWritableDatabase ( );
            String frt[] = new String[0];

            String serImgQwer = "Select User_Mtr_Img,User_Sig_Img from TBL_BILLING WHERE Upload_Flag='N'";
            Cursor curBillImg = SD3.rawQuery ( serImgQwer, null );
            if (curBillImg != null && curBillImg.moveToFirst ( )) {

                Log.e ( getApplicationContext ( ), "SLPrintAct", "Update Success" );


                mylistimagename.add ( curBillImg.getString ( 0 ) );
                mylistimagename.add ( curBillImg.getString ( 1 ) );
                Log.e ( getApplicationContext ( ), "SLPrintAct", "mtr_img" + curBillImg.getString ( 0 ) + "sig_img" + curBillImg.getString ( 1 ) );
            }

            ArrayList <String> stringArrayList = new ArrayList <String> ( );
            for (int j = 0; j < mylistimagename.size ( ); j++) {

                stringArrayList.add ( Environment.getExternalStorageDirectory ( ) + "/MBC/" + mylistimagename.get ( j ) ); //add to arraylist
            }
            String[] files = stringArrayList.toArray ( new String[stringArrayList.size ( )] );
            String[] file = {Zip, signaturePathDes, photoPathDes};

            zipFolder ( ZipCopyPath, ZipDesPath );
            GSBilling.getInstance ( ).setFinalZipName ( ZipDesPathdup );

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                // Set your file path here
//                System.out.println("FILENAME IS1 "+GSBilling.getInstance().getFinalZipName());
                FileInputStream fstrm = new FileInputStream ( Environment.getExternalStorageDirectory ( ).toString ( ) + GSBilling.getInstance ( ).getFinalZipName ( ) + ".zip" );
                Log.e ( getApplicationContext ( ), "SLPrintAct", "FILENAME IS12 " + fstrm );

                // Set your server page url (and the file title/description)

//                HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi_Testing/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                HttpFileUpload hfu = new HttpFileUpload ( URLS.DataComm.billUpload, "" + GSBilling.getInstance ( ).getFinalZipName ( ), ".zip" );
//                HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");

                // Log.e(getApplicationContext(), "http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles", "" + GSBilling.getInstance().getFinalZipName()+".zip");
                Log.e ( getApplicationContext ( ), "SLPrintAct", "going out " + GSBilling.getInstance ( ).getFinalZipName ( ) + ".zip" );
                int status = hfu.Send_Now ( fstrm );
                if (status != 200) {
//                    succsess = "1";
                    MainActivity.this.runOnUiThread ( new Runnable ( ) {

                        @Override
                        public void run() {
                            progress.dismiss ( );
                            Toast.makeText ( MainActivity.this, "Internaly Stored Due to No Connectivity", Toast.LENGTH_LONG ).show ( );

                            Intent intent = new Intent ( MainActivity.this, BillingtypesActivity.class );
                            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                            intent.putExtra ( "printtype", print_type );
                            startActivity ( intent );
                            overridePendingTransition ( R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left );

                        }
                    } );

                } else {
//                    succsess = "0";
                    dbHelper2 = new DB ( getApplicationContext ( ) );
                    SD2 = dbHelper2.getWritableDatabase ( );

//                        String updatequer = "UPDATE  TBL_BILLING  SET Upload_Flag = 'Y' WHERE  Cons_Number = '" + curBillselect.getString(0) + "' and  Bill_Month='" + curBillselect.getString(5) + "'";
                    String updatequer = "UPDATE  TBL_BILLING  SET Upload_Flag = 'Y'";
                    Cursor curBillupdate = SD2.rawQuery ( updatequer, null );
                    if (curBillupdate != null && curBillupdate.moveToFirst ( )) {
                        Log.e ( getApplicationContext ( ), "SLPrintAct", "Update Success" );
                    }

                    MainActivity.this.runOnUiThread ( new Runnable ( ) {

                        @Override
                        public void run() {
                            progress.dismiss ( );
                            Toast.makeText ( MainActivity.this, " Successfully Uploaded to Server ", Toast.LENGTH_LONG ).show ( );
                            Intent intent = new Intent ( MainActivity.this, BillingtypesActivity.class );
                            //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            //  intent.putExtra("printtype", print_type);
                            startActivity ( intent );
                            finish ( );
                            overridePendingTransition ( R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left );

                        }
                    } );
                }

//                return true;

            } catch (Exception e) {
                // Error: File not found
                succsess = "0";
                e.printStackTrace ( );
//                return  false;
            }

            return true;

        }

        protected void onPostExecute() {
            progress.dismiss ( );


            new File ( Environment.getExternalStorageDirectory ( ).toString ( ) + GSBilling.getInstance ( ).getFinalZipName ( ) + ".zip" ).delete ( );
            Intent intent = new Intent ( MainActivity.this, BillingtypesActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            intent.putExtra ( "printtype", print_type );
            startActivity ( intent );
            overridePendingTransition ( R.anim.anim_slide_in_left,
                    R.anim.anim_slide_out_left );

        }
    }

    private void dismissProgressDialog() {
        if (sDialog != null && sDialog.isShowing ( )) {
            sDialog.dismiss ( );
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        dismissProgressDialog ( );
        Debug.stopMethodTracing ( );
        super.onDestroy ( );
    }

    void DeleteRecursive(File dir) {
        Log.e ( getApplicationContext ( ), "SLPrintAct", "DeleteRecursive DELETEPREVIOUS TOP" + dir.getPath ( ) );
        if (dir.isDirectory ( )) {
            String[] children = dir.list ( );
            for (int i = 0; i < children.length; i++) {
                File temp = new File ( dir, children[i] );
                if (temp.isDirectory ( )) {
                    Log.e ( getApplicationContext ( ), "SLPrintAct", "DeleteRecursive Recursive Call" + temp.getPath ( ) );
                    DeleteRecursive ( temp );
                } else {
                    Log.e ( getApplicationContext ( ), "SLPrintAct", "DeleteRecursive Delete File" + temp.getPath ( ) );
                    boolean b = temp.delete ( );
                    if (b == false) {
                        Log.e ( getApplicationContext ( ), "SLPrintAct", "DeleteRecursive DELETE FAIL" );
                    }
                }
            }
        }
        dir.delete ( );
    }

    public static void zipFolder(String inputFolderPath, String outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream ( outZipPath );
//            GSBilling.getInstance().setFinalZipName();
            ZipOutputStream zos = new ZipOutputStream ( fos );
            File srcFile = new File ( inputFolderPath );
            File[] files = srcFile.listFiles ( );
            android.util.Log.d ( "", "Zip directory: " + srcFile.getName ( ) );
            for (int i = 0; i < files.length; i++) {
                android.util.Log.d ( "", "Adding file: " + files[i].getName ( ) );
                byte[] buffer = new byte[1024];
                FileInputStream fis = new FileInputStream ( files[i] );
                zos.putNextEntry ( new ZipEntry ( files[i].getName ( ) ) );
                int length;
                while ((length = fis.read ( buffer )) > 0) {
                    zos.write ( buffer, 0, length );
                }
                zos.closeEntry ( );
                fis.close ( );
            }
            System.out.println ( "helloooo" + srcFile.delete ( ) );
            zos.close ( );
        } catch (IOException ioe) {
            android.util.Log.e ( "", ioe.getMessage ( ) );
        }
    }

    private String getBillMonth(String month) {

        switch (month.substring ( 4, 6 )) {
            case "01":
                return "Jan-" + month.substring ( 2, 4 );
            case "02":
                return "Feb-" + month.substring ( 2, 4 );
            case "03":
                return "Mar-" + month.substring ( 2, 4 );
            case "04":
                return "Apr-" + month.substring ( 2, 4 );
            case "05":
                return "May-" + month.substring ( 2, 4 );
            case "06":
                return "Jun-" + month.substring ( 2, 4 );
            case "07":
                return "Jul-" + month.substring ( 2, 4 );
            case "08":
                return "Aug-" + month.substring ( 2, 4 );
            case "09":
                return "Sep-" + month.substring ( 2, 4 );
            case "10":
                return "Oct-" + month.substring ( 2, 4 );
            case "11":
                return "Nov-" + month.substring ( 2, 4 );
            case "12":
                return "Dec-" + month.substring ( 2, 4 );
        }
        return month;
    }

    private String getLoadType() {

        //UtilAppCommon uc = new UtilAppCommon();

        switch (Structconsmas.Load_Type) {
            case "W":
            case "1":
                return " W";
            case "KW":
            case "2"://kw
                return " KW";
            //return uc.ConvertMD(Structconsmas.Load, "KW", "HP");//load,string units,string target units
            case "BHP":
            case "3"://hp'HP
                return " HP";
            //               return uc.ConvertMD(Structconsmas.Load, "HP", "HP");
            case "KVA":
            case "4"://KVA
                return " KVA";
            //return uc.ConvertMD(Structconsmas.Load, "KVA", "KVA");
        }
        return "W";
    }

    public void connect_device() {
        checkBTP_Permissions ( );
        btpObject.startDiscovery ( );    //getting Bluetooth object
        checkBTP_Permissions ( );    // just to check the Permissions
        btpObject.startDiscovery ( );
        btpObject.createClient ( Mac );
    }

    public Boolean isMobileDataEnabled() {
        Object connectivityService = getSystemService ( CONNECTIVITY_SERVICE );
        ConnectivityManager cm = (ConnectivityManager) connectivityService;

        try {
            Class <?> c = Class.forName ( cm.getClass ( ).getName ( ) );
            Method m = c.getDeclaredMethod ( "getMobileDataEnabled" );
            m.setAccessible ( true );
            return (Boolean) m.invoke ( cm );
        } catch (Exception e) {
            e.printStackTrace ( );
            return null;
        }
    }
}