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
import androidx.core.view.MotionEventCompat;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.BillingtypesActivity;
import com.fedco.mbc.activity.Collection;
import com.fedco.mbc.activity.CommonHttpConnection;
import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.activity.Home;
import com.fedco.mbc.activity.MainActivity;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.collection.CollectiontypesActivity;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.model.Structcollection;
import com.fedco.mbc.model.Structcolmas;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.HttpFileUpload;
import com.fedco.mbc.utils.URLS;
import com.fedco.mbc.utils.UtilAppCommon;
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

public class MainActivityReceiptPrint  extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener, QABTPAccessory {
    private static final String TAG = MainActivityCollectionPrint.class.getSimpleName();
    //    @InjectView(R.id.log_txt)
    TextView mLogTxt, tv_Print;
    //    EditText etText;
    int scalingVal, position;
    //    @InjectView(R.id.scan)
    //    Button mScanBtn, mConnectBtn, mStartBT, mPrintSmallHeight, mLinefedBtn, Enter;
    //    @InjectView(R.id.communication)

    Spinner devListSpinner;
    ArrayAdapter<String> spinAdapter;
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    BluetoothManager btpObject;
    DB databasehelper;

    String test,test2, strMeterMake, strMeterOwner;
    SharedPreferences sharedPreferences;
    String Mac;
    String spinMac;
    String print_type = "0";

    String Hour, Minute;

    String strtime, strBillMonth;

    String strdcSeq;
    Long billseq;
    Long dcSeq;
    SweetAlertDialog sDialog;

    Logger Log;
    SessionManager session;
    SQLiteDatabase SD, SD2, SD3, SD4 ,SD5;
    DB dbHelper, dbHelper2, dbHelper3, dbHelper4;
    UtilAppCommon comApp;

    private ProgressDialog progress;
    ArrayList<String> mylistimagename = new ArrayList<String>();

    String cashNumber,cashAmount,chequeNumber,chequeAmount,ddNumber,ddAmount,totalAmount;

    //    String imgSrcPath        = Environment.getExternalStorageDirectory()
    //            + File.separator + "/MBC/Images/" + GSBilling.getInstance().getKEYNAME() + "_" + Structconsmas.Consumer_Number + "_mtr.jpg";
    //    String imgSrcPathDes     = Environment.getExternalStorageDirectory()
    //            + File.separator + "/MBC/Images/" + GSBilling.getInstance().getKEYNAME() + "_mtr.bmp";

    //    String ZipDesPath = Environment.getExternalStorageDirectory() + "/MBC/" + GSBilling.getInstance().getKEYNAME() + GSBilling.getInstance().captureDatetime() + ".zip";

    //    String ZipDesPathdup     = "/MBC/" + GSBilling.getInstance().getKEYNAME() + GSBilling.getInstance().captureDatetime();
    //    String signaturePathDes  = Environment.getExternalStorageDirectory() + "/MBC" + "/" + GSBilling.getInstance().getKEYNAME() + "_" + Structconsmas.Consumer_Number + "_sig.jpg";
    //    String photoPathDes      = Environment.getExternalStorageDirectory() + "/MBC/" + GSBilling.getInstance().getKEYNAME() + "_" + Structconsmas.Consumer_Number + "_mtr.jpg";

    String ZipSourcePath = Environment.getExternalStorageDirectory() + "/MBC/Images/";
    String ZipCopyPath = Environment.getExternalStorageDirectory() + "/MBC/Downloadsingular/";
    String ZipDeletPath2 = Environment.getExternalStorageDirectory() + "/MBC/Downloadsingular/";
    String Zip = Environment.getExternalStorageDirectory() + "/Notes/billing.csv";
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
    public String convertedNumber, typePay,typeMode;
    int HOURS, MIN, AP;
    long ser_key;
    String strDate;
    String key, username, codeIMEI;

    TelephonyManager telephonyManager;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_amigo);

        btpObject = BluetoothManager.getInstance(this, MainActivityReceiptPrint.this);
        sharedPreferences = this.getSharedPreferences("QABTprefs", 0);
        Mac = sharedPreferences.getString("BTDeviceMac", "NA");
//        new TextFileClass(MainActivityCollectionPrint.this).execute();

        mLogTxt = (TextView) findViewById(R.id.log_txt);

        devListSpinner = (Spinner) findViewById(R.id.device_listspinner);
        devListSpinner.setOnItemSelectedListener(this);
        spinAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        devListSpinner.setAdapter(spinAdapter);

        spinAdapter.notifyDataSetChanged();

        checkBTP_Permissions();
        setLogText("===> Start Scanning devices..");
        /* btpObject.startDiscovery();*/
        btpObject.scanAllBluetoothDevice();

        File dir2 = new File(ZipDeletPath2);

//        DeleteRecursive(dir2);
        dbHelper = new DB(getApplicationContext());
        SD = dbHelper.getWritableDatabase();
        databasehelper = new DB(getApplicationContext());
        SD5 = databasehelper.getWritableDatabase();

        Calendar c = Calendar.getInstance();
        HOURS = c.get(Calendar.HOUR);
        MIN = c.get(Calendar.MINUTE);
        AP = c.get(Calendar.AM_PM);

        ProgressDialog progress;
        comApp = new UtilAppCommon();
//        ser_key = comApp.findSequence(getApplicationContext(), "ReceiptNumber");
        ser_key = comApp.findSequence(getApplicationContext(), "ReceiptNumber");
        typeMode = GSBilling.getInstance().getPayType();
//        typePay = GSBilling.getInstance().getCON_TYPE();
        typePay = GSBilling.getInstance().CON_TYPE;

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat fds = new SimpleDateFormat("kk:mm");
        strDate = sdf.format(cal.getTime());
        strtime = fds.format(cal.getTime());

        Date date = new Date();

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        String billMonth = formatter.format(date);
        String str[] = billMonth.split(" ");
        String month = str[1].substring(0, 3);
        String year = str[2].substring(2);
        strBillMonth = month + "-" + year;

//        Structcolmas.COL_DATE = GSBilling.getInstance().Serverdate;
//        Structcolmas.COL_TIME = GSBilling.getInstance().Servertime;
        Structcolmas.CON_NO = Structcollection.CON_NO;
        Structcolmas.UPLOAD_FLAG = "N";

        //--------------------------------------
        print_type = getIntent().getStringExtra("printtype");
        session = new SessionManager(getApplicationContext());
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        codeIMEI = telephonyManager.getDeviceId();
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
         key         = session.retLicence();

//        key = comApp.UniqueCode(getApplicationContext());
//        key = comApp.UniqueCode(getApplicationContext());
//        key =GSBilling.getInstance().MRID;
        username = user.get(SessionManager.KEY_EMAIL);
//        int a = Integer.valueOf(Structcolmas.AMOUNT);
//        try {
//            convertedNumber = Words.convertNumberToWords(a);
//        } catch (Exception e) {
//
//        }



        if(Collection.cashNumber==null){

            cashNumber="0";
        }
        if(Collection.cashAmount==null){
            cashAmount="0";

        }
        if(Collection.chequeNumber==null){

            chequeNumber="0";
        }
        if(Collection.chequeAmount==null){

            chequeAmount="0";
        }
        if(Collection.ddNumber==null){

            ddNumber="0";
        }
        if(Collection.ddAmount==null){

            ddAmount="0";
        }
        if(Collection.totalAmount==null){

            totalAmount="0";
        }


       /* double cashNumber=Collection.cashNumber;
        double cashAmount=Collection.cashAmount;

        double chequeNumber=Collection.chequeNumber;
        double chequeAmount=Collection.chequeAmount;

        double ddNumber=Collection.ddNumber;
        double ddAmount=Collection.ddAmount;

        double totalCash=Collection.totalCash;
        double totalAmount=Collection.totalAmount;


        System.out.println ("CashNo. "+cashNumber );
        System.out.println ("CashAmount "+cashAmount );
        System.out.println ("ChequeNo. "+chequeNumber );
        System.out.println ("ChequeAmount. "+chequeAmount );
        System.out.println ("DD Number. "+ddNumber );
        System.out.println ("DD Amount. "+ddAmount );
        System.out.println ("Total Cash. "+totalCash );
        System.out.println ("TotalAmount. "+totalAmount );
*/

        this.ZipDesPathdup = "/MBC/" + key + "_col_" + GSBilling.getInstance().captureDatetime();
        this.ZipDesPath = Environment.getExternalStorageDirectory() + "/MBC/" + key + "_col_" + GSBilling.getInstance().captureDatetime() + ".zip";

        Structcolmas.DEV_ID = key;
        Structcolmas.RECEIPT_NO = key + "/" + strDate.replace("-", "") + "/" + String.format("%05d", ser_key);
        Structcolmas.MR_ID = session.retMRID();
        Structcolmas.MR_NAME = session.retMRName();
        Structcolmas.MAN_BOOK_NO = key;
        Structcolmas.MAN_RECP_NO = String.format("%05d", ser_key);

        String divCode = "SELECT DIV_NAME,DISPLAY_CODE FROM TBL_BILLING_DIV_MASTER";

        Cursor curDivCode = SD.rawQuery(divCode, null);

        if (curDivCode != null && curDivCode.moveToFirst()) {

            Structconsmas.DIV_NAME = curDivCode.getString(0);
            Structconsmas.PICK_REGION = curDivCode.getString(1);

        }
//        DecimalFormat decformat = new DecimalFormat("#,###,###");
//        DecimalFormat df = new DecimalFormat("#.00");
//        String amount =decformat.format(Structcolmas.AMOUNT);
//        System.out.println("tokennnn :"+ GSBilling.getInstance().getTokenDec());

//        dbHelper.insertIntoColmasTable();
//        dbHelper.insertIntoColphedTable();
        dbHelper.insertSequence("ReceiptNumber", ser_key);




//        if (typePay.equalsIgnoreCase("Prepaid")) {
//                    test = " " + "    MADHYA PRADESH    " + "\n" +
//                            " " + "    M.P.M.K.V.V.C.L   " + "\n" +
//                            " " + "**********************" + "\n" +
//                            " " + "RECEIPT(CONSUMER COPY)" + "\n" +
//                            " " + "**********************" + "\n" +
//                            " " + "     " + "\n" +
//                            "MONTH  :" + strBillMonth + "\n" +
//                            "MR NO. :" + " " + "\n" +
//                            " " + Structcolmas.RECEIPT_NO + "\n" +
//                            "DATE        :" + strDate + "\n" +
//                            "TIME        :" + strtime + "\n" +
//                            "DIVISION    :" + "\n" +
//                            " " + Structcollection.DIV + "\n" +
//                            "DC          :" + "\n" +
//                            " " + Structcollection.SECTION + "\n" +
//                            "CONS NO.    :" + Structcollection.CON_NO + "\n" +
//                            "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
//                            "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
//                            "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
//                            " (" + convertedNumber + " Rupees Only )" + "\n" +
//                            " " + "     " + "\n" +
//                            "PAYMENT TYPE:" + typePay + "\n" +
//                            " " + "     " + "\n" +
//                            "READER ID   :" + Structcolmas.MR_ID + "\n" +
//                            "READER NAME :" + Structcolmas.MR_NAME + "\n" +
//                            "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
//                            " " + "     " + "\n" +
//                            " " + "     " + "\n" +
//                            "<----- cut here ----->" + "\n" +
//                            " " + "     " + "\n" +
//                            " " + "    MADHYA PRADESH    " + "\n" +
//                            " " + "    M.P.M.K.V.V.C.L   " + "\n" +
//                            " " + "**********************" + "\n" +
//                            "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
//                            " " + "**********************" + "\n" +
//                            " " + "     " + "\n" +
//                            "MONTH  :" + strBillMonth + "\n" +
//                            "MR NO. :" + " " + "\n" +
//                            " " + Structcolmas.RECEIPT_NO + "\n" +
//                            "DATE        :" + strDate + "\n" +
//                            "TIME        :" + strtime + "\n" +
//                            "DIVISION    :" + "\n" +
//                            " " + Structcollection.DIV + "\n" +
//                            "DC          :" + "\n" +
//                            " " + Structcollection.SECTION + "\n" +
//                            "CONS NO.    :" + Structcollection.CON_NO + "\n" +
//                            "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
//                            "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
//                            "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
//                            " (" + convertedNumber + " Rupees Only )" + "\n" +
//                            " " + "     " + "\n" +
//                            "PAYMENT TYPE:" + typePay + "\n" +
//                            " " + "     " + "\n" +
//                            "READER ID   :" + Structcolmas.MR_ID + "\n" +
//                            "READER NAME :" + Structcolmas.MR_NAME + "\n" +
//                            "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
//                            " " + "     " + "\n" +
//                            " " + "     " + "\n";
            test = "    " + "\n" +
                    "    " + "\n" +
                    "  Port Harcourt Electricity   " + "\n" +
                    "     Distribution Company " + "\n" +
                    "-------------------------------    " +
                    " Total Collection Receipt  " +  "\n" +
                    "-------------------------------  " + "\n" +
//                            "     " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" + //201706
                    "Date:" + (String.format("%1$6s", Collection.todayDate)) + "\n" +
                    "Cashier Name:"+(String.format("%1$6s", MainActivity.MRNME )) + "\n" +
                    "Cash:" + "\n" +
                    "Response:"+(String.format("%1$4s",Collection.cashNumber)+"\n"+
                    "Amount(NGN):"+(String.format("%1$6s",MainActivityCollectionPrint.internationAnotation ( ""+Collection.cashAmount) )))+"\n"+
                    // "Account/Meter No:"+(String.format("%1$6s", GSBilling.getInstance().ConsumerNO)) + "\n" +
                    "Cheque:"+ "\n" +
                    "Response:"+(String.format("%1$4s",Collection.chequeNumber))+"\n"+
                    "Amount(NGN):"+(String.format("%1$6s",MainActivityCollectionPrint.internationAnotation(""+Collection.chequeAmount)))+"\n"+
                    /*"Pos:"+"\n" +
*//*
                    "Response:"+(String.format("%1$4s",Structcolmas.POSCOUNT))+"\n"+
                    "Amount(NGN):"+(String.format("%1$6s",MainActivityCollectionPrint.internationAnotation(Structcolmas.POSAMOUNT)))+"\n"+
*//*
                    "Response:"+(String.format("%1$4s","0"))+"\n"+
                    "Amount(NGN):"+(String.format("%1$6s","0"))+"\n"+
*/
                    "DD:"+ "\n" +
                    "Response:"+(String.format("%1$4s",Collection.ddNumber))+"\n"+
                    "Amount(NGN):"+(String.format("%1$6s",MainActivityCollectionPrint.internationAnotation(Collection.ddAmount)))+"\n"+

                    "Total Response:"+(String.format("%1$4s",Collection.totalCash))+"\n"+
                    "Total Amount:"+(String.format("%1$6s", MainActivityCollectionPrint.internationAnotation(Collection.totalAmount))) + "\n" +
                    //  "Address:"+(String.format("%1$6s", address.trim())) + "\n" +
//                            "Account Type: Prepaid"  + "\n" +
                    "Customer Care : 070022557433" +  "\n" ;

//                } else if (typePay.equalsIgnoreCase("CHEQUE")) {
//                    test = "    " + "\n" +
//                            "    " + "\n" +
//                            "    PortHartCourt Electricity   " + "\n" +
//                            "     Distribution Company " + "\n" +
//                            "-------------------------------    " +
//                            "     Payment Reciept  " +  "\n" +
//                            "-------------------------------  " + "\n" +
////                            "     " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" + //201706
//                            "" + (String.format("%1$6s", strDate)) + "  " + strtime + "\n" +
//                            "Account/Meter No:"+(String.format("%1$6s", GSBilling.getInstance().ConsumerNO)) + "\n" +
//                            "Name:"+(String.format("%1$6s",GSBilling.getInstance().CONS_NAME)) + "\n" +
//                            //  "Address:"+(String.format("%1$6s", address.trim())) + "\n" +
////                            "Account Type:Postpaid"  + "\n" +
//                            "TRANSACTION DETAILS"  + "\n" +
//                            "Type: Prepaid "+ "\n" +
//                            "Amount (NGN ):" + (String.format("%1$6s", Structcolmas.AMOUNT )) + "\n" +
//                            "Units:  1.00" + "\n" +//+(String.format("%1$6s", Structconsmas.MF)) + " / " + Structconsmas.PHASE_CD + "\n" +
//                            "Token: " +(String.format("%1$6s", GSBilling.getInstance().TokenNo)) + "\n" +
//                            "Handled by :"+(String.format("%1$4s", Structcolmas.MR_NAME)) + "\n" ;
//
//                } else if (typePay.equalsIgnoreCase("DD")) {
//                    test = "    " + "\n" +
//                            "    " + "\n" +
//                            "    PortHartCourt Electricity   " + "\n" +
//                            "     Distribution Company " + "\n" +
//                            "-------------------------------    " +
//                            "     Payment Reciept  " +  "\n" +
//                            "-------------------------------  " + "\n" +
////                            "     " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" + //201706
//                            "" + (String.format("%1$6s", strDate)) + "  " + strtime + "\n" +
//                            "Account/Meter No:"+(String.format("%1$6s",GSBilling.getInstance().ConsumerNO)) + "\n" +
//                            "Name:"+(String.format("%1$6s", GSBilling.getInstance().CONS_NAME)) + "\n" +
//                            //  "Address:"+(String.format("%1$6s", address.trim())) + "\n" +
////                            "Account Type:prepaid" + "\n" +
//                            "TRANSACTION DETAILS"  + "\n" +
//                            "Type: Prepaid "+ "\n" +
//                            "Amount (NGN ):" + (String.format("%1$6s", Structcolmas.AMOUNT )) + "\n" +
//                            "Units:  1.00" + "\n" +//+(String.format("%1$6s", Structconsmas.MF)) + " / " + Structconsmas.PHASE_CD + "\n" +
//                            "Token: " +(String.format("%1$6s", GSBilling.getInstance().TokenNo)) + "\n" +
//                            "Handled by :"+(String.format("%1$4s", Structcolmas.MR_NAME)) + "\n" ;

//        } else {
//            test = "    " + "\n" +
//                    "    " + "\n" +
//                    "  Port Harcourt Electricity  " + "\n" +
//                    "     Distribution Company " + "\n" +
//                    "-------------------------------    " +
//                    "     Payment Receipt  " +  "\n" +
//                    "-------------------------------  " + "\n" +
////                            "     " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" + //201706
//                    "" + (String.format("%1$6s",GSBilling.getInstance().Serverdate + "  " +  GSBilling.getInstance().Servertime)) + "\n" +
//                    "Account:"+(String.format("%1$6s", GSBilling.getInstance().ConsumerNO)) + "\n" +
//                    "Meter No:"+(String.format("%1$6s", GSBilling.getInstance().MeterNo)) + "\n" +
//                    "Name:"+(String.format("%1$6s",GSBilling.getInstance().CONS_NAME)) + "\n" +
//                    "Address:"+(String.format("%1$6s", GSBilling.getInstance().Addresses.trim())) + "\n" +
//                    "Account Type:" + (String.format("%1$6s", GSBilling.getInstance().CON_TYPE )) + "\n" +
//                    "Payment Type:" + typeMode + "\n"+
//                    "TRANSACTION DETAILS"  + "\n" ;
//            if(Structcolmas.PYMNT_MODE.equalsIgnoreCase("Q")) {
//                test  = test +"Cheque Date :" + Structcolmas.CH_DATE + "\n" +
//                        "Cheque No.  :" + Structcolmas.CHEQUE_NO + "\n" +
//                        "Bank Name   :" + Structcolmas.BANK_NAME + "\n" +
//                        "SUBJECT TO CLEARANCE"  + "\n" ;
//            }
//            test  =test + "Amount (NGN ):" + (String.format("%1$6s",(Structcolmas.AMOUNT))) + "\n" +
////                            "Units:" + (String.format("%1$6s", GSBilling.getInstance().punit)) +  "\n" +
//                    "eReceipt: " + (String.format("%1$6s", GSBilling.getInstance().RecieptNo)) + "\n" +
//                    "Status: Successful"  + "\n" +
//                    "Handled by :"+(String.format("%1$4s", Structcolmas.MR_NAME)) + "\n" +
//                    "Customer Care : 070022557433" +  "\n" ;
//        }
        test2 = "    " + "\n" +
                "    " + "\n" ;
    }

    private String dotSeparate(String value) {

        if (value.contains(".")) {
            String[] parts = value.split("\\."); // escape .
            String part1 = parts[0];
            return part1;
        }
        return value;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("");
        dialog.setMessage("Are you sure to Exit.?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

                Intent intent = new Intent();
                intent.setComponent(new ComponentName(getApplicationContext(), Home.class));
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });
        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        dialog.create().show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {

    }

    @SuppressLint("WrongConstant")
    public void checkBTP_Permissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int permissionCheck = 0;
            permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        } else {

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
        if (device.getName() != null && ((device.getName().contains("QA")) || (device.getName().contains("Dual-SPP")) ||(device.getName().contains("MHT-P5801"))|| (device.getName().contains("SP120E"))|| (device.getName().contains("XL-1880"))||(device.getName().contains("SP120"))||(device.getName().contains("ESBAA0050"))||(device.getName().contains("QSPrinter")) ||(device.getName().contains("QSprinter"))|| (device.getName().contains("QuantumAeon")))) {
            String dev_name = device.getName().trim();
            String dev_adrs = device.getAddress().trim();
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                if (spinAdapter.getCount() == 0) {
                    spinAdapter.add(dev_adrs);
                    spinAdapter.notifyDataSetChanged();
                    setLogText("===> Device detected : " + device.getAddress());
                }
                for (int x = 0; x < spinAdapter.getCount(); x++) {
                    String tmp = spinAdapter.getItem(x).toString();
                    if (tmp.equalsIgnoreCase(dev_adrs)) {
                        i++;
                    }
                }
                if (i == 0) {
                    spinAdapter.add(dev_adrs);
                    spinAdapter.notifyDataSetChanged();
                    setLogText("===> Device detected : " + device.getAddress());
//                    mConnectBtn.setEnabled(true);
                } else {
                    i = 0;
                }
            }

        }
    }

    public void save_mac_address(String mac) {
        if (!mac.equals("")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.putString("BTDeviceMac", mac);
            editor.commit();
        }
    }

    @Override
    public void onClientConnectionSuccess() {
        setLogText("===> Client Connection success !");



    }

    @Override
    public void onClientConnectionFail() {

    }


    @Override
    public void onClientConnecting() {
        setLogText("===> Connecting..");
    }

    @Override
    public void onBluetoothNotAvailable() {
        setLogText("===> Bluetooth not available on this device");
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
    public void printPhoto() {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.phedlogo);

        if(bmp!=null){
            byte[] command = Utils.decodeBitmap(Bitmap.createScaledBitmap(bmp, 300, 100, false));

            btpObject.sendMessage(PrinterCommands.ESC_ALIGN_LEFT);
            btpObject.sendMessage(command);
        }else{
            // Log.e("Print Photo error", "the file doesn't exists");
        }
    }
    public static StringBuffer bytesToString(byte[] bytes) {
        StringBuffer sBuffer = new StringBuffer();
        for (byte b : bytes) {
            String s = Integer.toHexString(b & MotionEventCompat.ACTION_MASK);
            if (s.length() < 2) {
                sBuffer.append('0');
            }
            sBuffer.append(new StringBuilder(String.valueOf(s)).append(" ").toString());
        }
        return sBuffer;
    }

    public void printQrcode(){
        byte[] btdata = null;
        try {
            btpObject.sendMessage(PrinterCommands.ESC_ALIGN_CENTER);
//            btdata = GSBilling.getInstance().ConsumerNO.getBytes("ASCII");
            btdata = (GSBilling.getInstance().ConsumerNO + "," + GSBilling.getInstance().MeterNo + ","+ Structcolmas.AMOUNT + "," + GSBilling.getInstance().RecieptNo ).getBytes("ASCII");
//            btdata = "12345".toString().getBytes("ASCII");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String strdata = this.bytesToString(btdata).toString();
        short datalen = (short) (strdata.length() + 3);
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
        btpObject.sendMessage(bArr);
        byte[] qrHead = new byte[]{(byte) 29, (byte) 40, (byte) 107, pL, pH, (byte) 49, (byte) 80, (byte) 48};
        byte[] qrData = new byte[(qrHead.length + datalen)];
        System.arraycopy(qrHead, 0, qrData, 0, qrHead.length);
        System.arraycopy(btdata, 0, qrData, qrHead.length, btdata.length);
        btpObject.sendMessage(qrData);
        // btService = MainActivity.pl;
        bArr = new byte[8];
        bArr[0] = (byte) 29;
        bArr[1] = (byte) 40;
        bArr[2] = (byte) 107;
        bArr[3] = (byte) 3;
        bArr[5] = (byte) 49;
        bArr[6] = (byte) 81;
        bArr[7] = (byte) 48;
        btpObject.sendMessage(bArr);
    }

    public void setLogText(String text) {
        mLogTxt.setText(mLogTxt.getText() + "\n" + text);
        mLogTxt.setText(text + "\n");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(MainActivityReceiptPrint.this, item + "asfdasdasd", Toast.LENGTH_LONG).show();
        checkBTP_Permissions();

        if (item.contains("Click to connect BTP")) {
        } else {
            setLogText("===> Start client connection on device : " + item);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            btpObject.createClient(item);
            try {
                Thread.sleep(3000);
//                String testString[] = test.split("\n");
                printPhoto();
                btpObject.sendMessage(test);
//                printQrcode();
                btpObject.sendMessage(test2);
//                for (int j = 0; j < testString.length; j++) {
//                    btpObject.sendMessage(testString[j]);
//                 //   final int singleVarLinefeed = 1;
//                 //   btpObject.varblankdotlinespace(singleVarLinefeed);
//                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                btpObject.closeConnection();
                new PrintProcess().execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /*--------------------- Billing Masters Download Initiation ----------------------------------*/
    private class PrintProcess extends AsyncTask<String, Void, String> {
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
//            if (typePay.equalsIgnoreCase("Prepaid")) {
                sDialog = new SweetAlertDialog(MainActivityReceiptPrint.this, SweetAlertDialog.SUCCESS_TYPE);
                sDialog.setTitleText("Success");
//            sDialog.setContentText("Successfully Printed");
                sDialog.setContentText("Logout");
                sDialog.show();
                sDialog.showCancelButton(true);
                sDialog.setCancelText("Retry");
                sDialog.setCanceledOnTouchOutside(false);
                sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        new dayend().execute();


//                        try {
//                            String url= URLS.UserAccess._endOfday+"/"+Structcolmas.DEV_ID;
//                            if (new CommonHttpConnection(getApplicationContext()).isConnectingToInternet())
//                            {
//                                String _response = new CommonHttpConnection(getApplicationContext()).GetHttpConnection(URLS.UserAccess.checkConnection,10000);
//                                if(_response.equals("OK")) {
//                                    _response = new CommonHttpConnection(getApplicationContext()).GetHttpConnection(url,10000);
//                                    if
//                                            (_response.toLowerCase().equals("ok"))
//                                    {
////                                        Intent intent=new
////                                                Intent(MainActivityReceiptPrint.this, MainActivity.class);
////
////                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
////                                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                                        startActivity(intent);
//                                        LogoutUser();
//                                    }
//                                }else if(_response.equals("404 Not Found")){
//// return "Could not connect tserver";
//                            }
//                        }
//                    }catch (Exception ex)
//                    {
//                        ex.printStackTrace();
//                    }

//                        if (isMobileDataEnabled()) {
//
////                        new TextFileClass(MainActivityCollectionPrint.this).execute();
////                            Intent intent = new Intent(MainActivityReceiptPrint.this, Collection.class);
////                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//////                        intent.putExtra("printtype", print_type);
////                            startActivity(intent);
////                            overridePendingTransition(R.anim.anim_slide_in_left,
////                                    R.anim.anim_slide_out_left);
//                            LogoutUser();
//
//                        } else {
//
////                            Toast.makeText(MainActivityReceiptPrint.this, "No Internet", Toast.LENGTH_SHORT).show();
////
////                            Intent intent = new Intent(MainActivityReceiptPrint.this, Collection.class);
////                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//////                        intent.putExtra("printtype", print_type);
////                            startActivity(intent);
////                            overridePendingTransition(R.anim.anim_slide_in_left,
////                                    R.anim.anim_slide_out_left);
//                            LogoutUser();
//                        }
                    }
                });
            sDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismissWithAnimation();
                    Intent intent = new Intent(MainActivityReceiptPrint.this, Collection.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        intent.putExtra("printtype", print_type);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);
                }
            });
//            } else {
//                sDialog = new SweetAlertDialog(MainActivityReceiptPrint.this, SweetAlertDialog.SUCCESS_TYPE);
//                sDialog.setTitleText("Receipt Number");
////            sDialog.setContentText("Successfully Printed");
//                sDialog.setContentText(GSBilling.getInstance().RecieptNo);
//                sDialog.show();
//                sDialog.setCanceledOnTouchOutside(false);
//                sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sDialog) {
//                        sDialog.dismissWithAnimation();
//
//                        UtilAppCommon ucom = new UtilAppCommon();
//                        ucom.nullyfimodelCon();
//                        ucom.nullyfimodelBill();
//
//                        if (isMobileDataEnabled()) {
//
////                        new TextFileClass(MainActivityCollectionPrint.this).execute();
//                            Intent intent = new Intent(MainActivityReceiptPrint.this, Collection.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                        intent.putExtra("printtype", print_type);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                    R.anim.anim_slide_out_left);
//
//                        } else {
//
//                            Toast.makeText(MainActivityReceiptPrint.this, "No Internet", Toast.LENGTH_SHORT).show();
//
//                            Intent intent = new Intent(MainActivityReceiptPrint.this, Collection.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                        intent.putExtra("printtype", print_type);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                    R.anim.anim_slide_out_left);
//                        }
//                    }
//                });
//            }



        }

        @Override
        protected void onPreExecute() {


        }


    }

    private class dayend extends AsyncTask<String,Void,Void>{
        protected void onPreExecute() {

        }
        @Override
        protected Void doInBackground(String... params) {
            try {
                String url= URLS.UserAccess._endOfday+"/"+Structcolmas.DEV_ID;
                try {
                    if (new CommonHttpConnection(getApplicationContext()).isConnectingToInternet())
                    {
                        String _response = new CommonHttpConnection(getApplicationContext()).GetHttpConnection(URLS.UserAccess.checkConnection,10000);
                        if(_response.equals("OK")) {
                            _response = new CommonHttpConnection(getApplicationContext()).GetHttpConnection(url,10000);
                            if
                                    (_response.toLowerCase().equals("ok"))
                            {
    //                                        Intent intent=new
    //                                                Intent(MainActivityReceiptPrint.this, MainActivity.class);
    //
    //                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
    //                                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
    //                                        startActivity(intent);
                                LogoutUser();
                            }
                        }else if(_response.equals("404 Not Found")){
    // return "Could not connect tserver";
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } catch (NullPointerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;

        }
        protected void onPostExecute() {

        }

    }

    private class TextFileClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        public TextFileClass(Context c) {

            this.context = c;

        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                dbHelper2 = new DB(getApplicationContext());
                SD2 = dbHelper2.getWritableDatabase();

                String selquer = "SELECT * FROM TBL_COLMASTER_MP WHERE Upload_Flag='N' ";//WHERE Upload_Flag='N'
                Cursor curColselect = SD2.rawQuery(selquer, null);

                String arrStr[] = null;
                ArrayList<String> mylist = new ArrayList<String>();



//                mylist.add((Structcolmas.DEV_ID) + "}" + (Structcolmas.MR_NAME) + "}" + (Structcolmas.MR_ID) +"}"
//                           + (GSBilling.getInstance().ConsumerNO) +"}"+ (Structcolmas.COL_DATE) +"}"+ (Structcolmas.COL_TIME)+"}"+
//                        (Structcolmas.AMOUNT)+"}"+ (Structcolmas.CH_DATE) +"}"+  (Structcolmas.CHEQUE_NO) +"}"+ "}"+(Structcolmas.MAN_BOOK_NO)
//                          +"}"+(Structcolmas.MAN_RECP_NO)+"}"+ (Structcolmas.PYMNT_MODE) +"}"+(Structcolmas.INSTA_FLAG)+"}"+(Structcolmas.COL_DATE)
//                           +"}"+ (Structcolmas.COL_TIME) +"}"+ (Structcolmas.UPLOAD_FLAG) +"}"+(Structcolmas.USER_LONG)+"}"+(Structcolmas.USER_LAT)
//                         +"}"+(Structcolmas.BAT_STATE)+"}"+(Structcolmas.SIG_STATE)+"}"+(GSBilling.getInstance().MOBILENO)+"}"+(Structcolmas.GPS_TIME)
//                        +"}"+(Structcolmas.PRNT_BAT_STAT)+"}"+(Structcolmas.VER_CODE)+"}"+(Structcolmas.GPS_ALTITUDE)+"}"+(Structcolmas.GPS_ACCURACY)
//                        +"}"+(Structcolmas.MBC_CONSUMPTION)+"}"+(Structcolmas.TOTAL_CONSUMPTION)+"}"+(Structcolmas.APP1_CONSUMPTION)+"}"+
//                        (Structcolmas.APP1_NAME)+"}"+ (Structcolmas.APP2_CONSUMPTION)+"}"+(Structcolmas.APP2_NAME)+"}"+(Structcolmas.APP3_CONSUMPTION)
//                        +"}"+(Structcolmas.APP3_NAME)+"}"+(GSBilling.getInstance().ConsumerNO)+"}"+(Structcolmas.SESSION_KEY)+"}"+
//                        (GSBilling.getInstance().ConsumerNO)+"}"+(GSBilling.getInstance().SEC_CODE)+"}"+(GSBilling.getInstance().MeterNo)+"}"+
//                        (GSBilling.getInstance().Payment_type));


                mylist.add((Structcolmas.DEV_ID) + "}" + (GSBilling.getInstance().MRNAME) + "}" + (GSBilling.getInstance().MRID) +"}"
                        + (GSBilling.getInstance().ConsumerNO) +"}"+ (Structcolmas.COL_DATE) +"}"+ (Structcolmas.COL_TIME)+"}"+
                        (Structcolmas.AMOUNT)+"}" + "}"+  (Structcolmas.CHEQUE_NO) +"}"+ (Structcolmas.CH_DATE) +"}"+  "}"+ (Structcolmas.MAN_BOOK_NO)
                        +"}"+(Structcolmas.MAN_RECP_NO)+"}"+ (Structcolmas.PYMNT_MODE) +"}"+ (Structcolmas.INSTA_FLAG)+"}"+ (Structcolmas.USER_LONG)+"}"+(Structcolmas.USER_LAT)
                        +"}"+(Structcolmas.BAT_STATE)+"}"+(Structcolmas.SIG_STATE)+"}"+(GSBilling.getInstance().MOBILENO)+"}"+(Structcolmas.GPS_TIME)
                        +"}"+(Structcolmas.PRNT_BAT_STAT)+"}"+(Structcolmas.VER_CODE)+"}"+(Structcolmas.GPS_ALTITUDE)+"}"+(Structcolmas.GPS_ACCURACY)
                        +"}"+(Structcolmas.TOTAL_CONSUMPTION)+"}"+(Structcolmas.APP1_CONSUMPTION)+"}"+
                        (Structcolmas.APP1_NAME)+"}"+ (Structcolmas.APP2_CONSUMPTION)+"}"+(Structcolmas.APP2_NAME)+"}"+(Structcolmas.APP3_CONSUMPTION)
                        +"}"+(Structcolmas.APP3_NAME)+"}"+(Structcolmas.SESSION_KEY)+"}"+
                        (GSBilling.getInstance().ConsumerNO)+"}"+(GSBilling.getInstance().SEC_CODE)+"}"+(GSBilling.getInstance().MeterNo)+"}"+
                        (GSBilling.getInstance().Payment_type));
//                        mylist.add(curColselect.getString(1) + "}" + curColselect.getString(2) + "}" + curColselect.getString(3) +
//                                "}" + curColselect.getString(4) + "}" + curColselect.getString(5) + "}" + curColselect.getString(6) +
//                                "}" + curColselect.getString(10) + "}" + curColselect.getString(7) + "}" + curColselect.getString(8) +
//                                "}" + curColselect.getString(9) + "}" + curColselect.getString(11) + "}" + curColselect.getString(12) +
//                                "}" + curColselect.getString(13) + "}" + curColselect.getString(14) + "}" + curColselect.getString(15) +
//                                "}" + curColselect.getString(18) + "}" + curColselect.getString(19) + "}" + curColselect.getString(20) +
//                                "}" + curColselect.getString(21) + "}" + curColselect.getString(22) + "}" + curColselect.getString(23) +
//                                "}" + curColselect.getString(24) + "}" + curColselect.getString(25) + "}" + curColselect.getString(26) +
//                                "}" + curColselect.getString(27) + "}" + curColselect.getString(28) + "}" + curColselect.getString(29) +
//                                "}" + curColselect.getString(30) + "}" + curColselect.getString(31) + "}" + curColselect.getString(32) +
//                                "}" + curColselect.getString(33) + "}" + curColselect.getString(34) + "}" + curColselect.getString(35) +
//                                "}" + curColselect.getString(37) + "}" + curColselect.getString(38) + "}" + curColselect.getString(39) + "}");
//
                generateNoteOnSD(getApplicationContext(), "colmobile.csv", mylist);



//                if (curColselect != null && curColselect.moveToFirst()) {
//
//                    while (curColselect.isAfterLast() == false) {
//                        String name = curColselect.getString(0);
//
//                        mylist.add(curColselect.getString(1) + "}" + curColselect.getString(2) + "}" + curColselect.getString(3) +
//                                "}" + curColselect.getString(4) + "}" + curColselect.getString(5) + "}" + curColselect.getString(6) +
//                                "}" + curColselect.getString(10) + "}" + curColselect.getString(7) + "}" + curColselect.getString(8) +
//                                "}" + curColselect.getString(9) + "}" + curColselect.getString(11) + "}" + curColselect.getString(12) +
//                                "}" + curColselect.getString(13) + "}" + curColselect.getString(14) + "}" + curColselect.getString(15) +
//                                "}" + curColselect.getString(18) + "}" + curColselect.getString(19) + "}" + curColselect.getString(20) +
//                                "}" + curColselect.getString(21) + "}" + curColselect.getString(22) + "}" + curColselect.getString(23) +
//                                "}" + curColselect.getString(24) + "}" + curColselect.getString(25) + "}" + curColselect.getString(26) +
//                                "}" + curColselect.getString(27) + "}" + curColselect.getString(28) + "}" + curColselect.getString(29) +
//                                "}" + curColselect.getString(30) + "}" + curColselect.getString(31) + "}" + curColselect.getString(32) +
//                                "}" + curColselect.getString(33) + "}" + curColselect.getString(34) + "}" + curColselect.getString(35) +
//                                "}" + curColselect.getString(37) + "}" + curColselect.getString(38) + "}" + curColselect.getString(39) + "}");
//                        // mylist2.add(curColselect.getString(7));
//
//
//                        curColselect.moveToNext();
//                    }
//                    generateNoteOnSD(getApplicationContext(), "colmobile.csv", mylist);
//
//                }

                MainActivityReceiptPrint.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progress.dismiss();
                        new PostClass(MainActivityReceiptPrint.this).execute();
                    }
                });


            } catch (NullPointerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute() {
            progress.dismiss();
        }

    }

    private void moveFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

////             delete the original file
//            new File(inputPath + inputFile).delete();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteFile(String inputPath, String inputFile) {
        try {
            // delete the original file
            new File(inputPath + inputFile).delete();


        } catch (Exception e) {
            Log.e(getApplicationContext(), "SLPrintAct", "tag" + e.getMessage());
        }
    }

    public void generateLastREC(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "mrcn/last/");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            int length = sBody.size();
            for (int i = 0; i < length; i++) {
//                System.out.println("selqwer1234 " + sBody.get(i));

                writer.append(sBody.get(i).toString());
                writer.append("\n");
            }

            writer.flush();
            writer.close();
            System.out.println("success file");

//                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateNoteOnSD(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "MBC/Downloadsingular/");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            int length = sBody.size();
            for (int i = 0; i < length; i++) {
                System.out.println("selqwer1234 " + sBody.get(i));

                writer.append(sBody.get(i).toString());
                writer.append("\n");
            }

            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generatebackupNoteOnSD(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "MBC/");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            int length = sBody.size();
            for (int i = 0; i < length; i++) {
                System.out.println("selqwer1234 " + sBody.get(i));
                BufferedWriter writerbuf = new BufferedWriter(new FileWriter(gpxfile, true));
                writerbuf.write(sBody.get(i).toString());
                writerbuf.close();
//                writer.append(sBody.get(i).toString());
//                writer.append("\n");

            }

            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class PostClass extends AsyncTask<String, Void, Boolean> {

        private final Context context;
        public String succsess = null;

        public PostClass(Context c) {

            this.context = c;

        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Sending Data...");
            progress.show();

//            dbHelper3 = new DB(getApplicationContext());
//            SD3 = dbHelper3.getWritableDatabase();
//            String frt[] = new String[0];
//
//            String serImgQwer = "Select User_Mtr_Img,User_Sig_Img from TBL_BILLING WHERE Upload_Flag='N'";
//            Cursor curBillImg = SD3.rawQuery(serImgQwer, null);
//            if (curBillImg != null && curBillImg.moveToFirst()) {
//
//                Log.e(getApplicationContext(), "SLPrintAct", "Update Success");
//
//                mylistimagename.add(curBillImg.getString(0));
//                mylistimagename.add(curBillImg.getString(1));
//                Log.e(getApplicationContext(), "SLPrintAct", "mtr_img" + curBillImg.getString(0) + "sig_img" + curBillImg.getString(1));
//            }

//            ArrayList<String> stringArrayList = new ArrayList<String>();
//            for (int j = 0; j < mylistimagename.size(); j++) {
//
//                stringArrayList.add(Environment.getExternalStorageDirectory() + "/MBC/" + mylistimagename.get(j)); //add to arraylist
//            }
            //String[] files = stringArrayList.toArray(new String[stringArrayList.size()]);
            //String[] file = {Zip, signaturePathDes, photoPathDes};

            zipFolder(ZipCopyPath, ZipDesPath);
            GSBilling.getInstance().setFinalZipName(ZipDesPathdup);

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                // Set your file path here
//                System.out.println("FILENAME IS1 "+GSBilling.getInstance().getFinalZipName());
                FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip");
                Log.e(getApplicationContext(), "SLPrintAct", "FILENAME IS12 " + fstrm);

                // Set your server page url (and the file title/description)

//                HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi_Testing/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                HttpFileUpload hfu = new HttpFileUpload("http://enservtest.fedco.co.in/MPSurvey/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                HttpFileUpload hfu = new HttpFileUpload("http://10.10.13.80:8080/Collection/UploadPrePaidFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                HttpFileUpload hfu = new HttpFileUpload("http://10.10.10.166:8080/Collection/UploadPrePaidFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                HttpFileUpload hfu = new HttpFileUpload("http://192.168.1.8:8080/Collection/UploadPrePaidFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                HttpFileUpload hfu = new HttpFileUpload("http://192.168.0.138:8080/Collection/UploadPrePaidFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");

                // Log.e(getApplicationContext(), "http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles", "" + GSBilling.getInstance().getFinalZipName()+".zip");
                Log.e(getApplicationContext(), "SLPrintAct", "going out " + GSBilling.getInstance().getFinalZipName() + ".zip");
                int status = hfu.Send_Now(fstrm);
                if (status != 200) {
//                    succsess = "1";
                    MainActivityReceiptPrint.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();
                            Toast.makeText(MainActivityReceiptPrint.this, "Internaly Stored Due to No Connectivity", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(MainActivityReceiptPrint.this, Collection.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("printtype", print_type);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);

                        }
                    });

                } else {
//                    succsess = "0";

                    //-------Printing should be done here--------//

                    dbHelper2 = new DB(getApplicationContext());
                    SD2 = dbHelper2.getWritableDatabase();

//                        String updatequer = "UPDATE  TBL_BILLING  SET Upload_Flag = 'Y' WHERE  Cons_Number = '" + curBillselect.getString(0) + "' and  Bill_Month='" + curBillselect.getString(5) + "'";
                    String updatequer = "UPDATE  TBL_COLMASTER_MP  SET Upload_Flag = 'Y'";
                    Cursor curBillupdate = SD2.rawQuery(updatequer, null);
                    if (curBillupdate != null && curBillupdate.moveToFirst()) {
                        Log.e(getApplicationContext(), "SLPrintAct", "Update Success");
                    }

                    MainActivityReceiptPrint.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();
                            Toast.makeText(MainActivityReceiptPrint.this, " Successfully Uploaded to Server ", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(MainActivityReceiptPrint.this, CollectiontypesActivity.class);
                            //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            //  intent.putExtra("printtype", print_type);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);

                        }
                    });
                }

//                return true;

            } catch (Exception e) {
                // Error: File not found
                succsess = "0";
                e.printStackTrace();
//                return  false;
            }

            return true;

        }

        protected void onPostExecute() {
            progress.dismiss();


            new File(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip").delete();
            Intent intent = new Intent(MainActivityReceiptPrint.this, BillingtypesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("printtype", print_type);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_in_left,
                    R.anim.anim_slide_out_left);

        }
    }

    private void dismissProgressDialog() {
        if (sDialog != null && sDialog.isShowing()) {
            sDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        dismissProgressDialog();
        Debug.stopMethodTracing();
        super.onDestroy();
    }

    void DeleteRecursive(File dir) {
        Log.e(getApplicationContext(), "SLPrintAct", "DeleteRecursive DELETEPREVIOUS TOP" + dir.getPath());
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                File temp = new File(dir, children[i]);
                if (temp.isDirectory()) {
                    Log.e(getApplicationContext(), "SLPrintAct", "DeleteRecursive Recursive Call" + temp.getPath());
                    DeleteRecursive(temp);
                } else {
                    Log.e(getApplicationContext(), "SLPrintAct", "DeleteRecursive Delete File" + temp.getPath());
                    boolean b = temp.delete();
                    if (b == false) {
                        Log.e(getApplicationContext(), "SLPrintAct", "DeleteRecursive DELETE FAIL");
                    }
                }
            }
        }
        dir.delete();
    }

    public static void zipFolder(String inputFolderPath, String outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outZipPath);
//            GSBilling.getInstance().setFinalZipName();
            ZipOutputStream zos = new ZipOutputStream(fos);
            File srcFile = new File(inputFolderPath);
            File[] files = srcFile.listFiles();
            android.util.Log.d("", "Zip directory: " + srcFile.getName());
            for (int i = 0; i < files.length; i++) {
                android.util.Log.d("", "Adding file: " + files[i].getName());
                byte[] buffer = new byte[1024];
                FileInputStream fis = new FileInputStream(files[i]);
                zos.putNextEntry(new ZipEntry(files[i].getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }
            System.out.println("helloooo" + srcFile.delete());
            zos.close();
        } catch (IOException ioe) {
            android.util.Log.e("", ioe.getMessage());
        }
    }

    private String getBillMonth(String month) {

        switch (month.substring(4, 6)) {
            case "01":
                return "Jan-" + month.substring(2, 4);
            case "02":
                return "Feb-" + month.substring(2, 4);
            case "03":
                return "Mar-" + month.substring(2, 4);
            case "04":
                return "Apr-" + month.substring(2, 4);
            case "05":
                return "May-" + month.substring(2, 4);
            case "06":
                return "Jun-" + month.substring(2, 4);
            case "07":
                return "Jul-" + month.substring(2, 4);
            case "08":
                return "Aug-" + month.substring(2, 4);
            case "09":
                return "Sep-" + month.substring(2, 4);
            case "10":
                return "Oct-" + month.substring(2, 4);
            case "11":
                return "Nov-" + month.substring(2, 4);
            case "12":
                return "Dec-" + month.substring(2, 4);
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
        checkBTP_Permissions();
        btpObject.startDiscovery();    //getting Bluetooth object
        checkBTP_Permissions();    // just to check the Permissions
        btpObject.startDiscovery();
        btpObject.createClient(Mac);
    }
    public void LogoutUser() {

        SD5 = databasehelper.getWritableDatabase();
        String delStr = "DELETE FROM USER_MASTER";
        SD5.execSQL(delStr);
        session.logoutUser();
        this.finish();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);
    }

    public Boolean isMobileDataEnabled() {
        Object connectivityService = getSystemService(CONNECTIVITY_SERVICE);
        ConnectivityManager cm = (ConnectivityManager) connectivityService;

        try {
            Class<?> c = Class.forName(cm.getClass().getName());
            Method m = c.getDeclaredMethod("getMobileDataEnabled");
            m.setAccessible(true);
            return (Boolean) m.invoke(cm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
