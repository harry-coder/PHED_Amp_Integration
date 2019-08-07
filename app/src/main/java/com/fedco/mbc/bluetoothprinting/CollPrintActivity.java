package com.fedco.mbc.bluetoothprinting;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.Collection;
import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.model.Structcollection;
import com.fedco.mbc.model.Structcolmas;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.HttpFileUpload;
import com.fedco.mbc.utils.UtilAppCommon;
import com.fedco.mbc.utils.Words;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.SimpleFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CollPrintActivity extends AppCompatActivity {

    Button btnPrint, btnSettings, btnBack, btnimagepath;
    EditText edtText;
    Dialog myDialog;
    RadioButton rdbtn_Normal, rdbtn_DoubleHeight, rdbtn_DoubleWidth, rdbtn_Double, rdbtn_LandScape, rdbtn_BarCode, rdbtn_Arabic, rbtn_BarCode, rdBtn_Image;
    String myCmd = "<0x09>", selectedImagePath, strMeterMake, strMeterOwner;
    String print_type;
    TextView imagepath;
    File mSdcard, path;
    int HOURS, MIN, AP;
    long ser_key;
    public static ProgressDialog prgDialog;
    private static final int SELECT_PICTURE = 1;
    SQLiteDatabase SD, SD2, SD3;
    DB dbHelper, dbHelper2, dbHelper3;
    String strDate, strBillMonth;
    private ProgressDialog progress;
    String requestURL = "http://103.192.172.42/Webapi/api/UploadFile/UploadFiles";
    SessionManager session;
    ArrayList<String> mylistimagename = new ArrayList<String>();
    String key, username, codeIMEI;
    UtilAppCommon comApp;
    TelephonyManager telephonyManager;
    String strtime;
    String ZipSourcePath = Environment.getExternalStorageDirectory() + "/MBC/Images/";
    String ZipCopyPath = Environment.getExternalStorageDirectory() + "/MBC/Downloadcol/";
    String ZipDeletPath = Environment.getExternalStorageDirectory() + "/MBC/Downloadcol";
    String ZipDeletPath2 = Environment.getExternalStorageDirectory() + "/MBC/Downloadcol/";
    public String ZipDesPath;
    //    public String ZipDesPath = Environment.getExternalStorageDirectory() + "/MBC/" + GSBilling.getInstance().getKEYNAME() + "_col_" + GSBilling.getInstance().captureDatetime() + ".zip";
    //    public String ZipDesPath=Environment.getExternalStorageDirectory() + "/MBC/"+SessionManager.getInstance().KEY_LICENCE +GSBilling.getInstance().captureDatetime()+ ".zip";
    String Zip = Environment.getExternalStorageDirectory() + "/Notes/Coltextfile.csv";
    //    String ZipDesPathdup="/MBC/"+SessionManager.getInstance().KEY_LICENCE +GSBilling.getInstance().captureDatetime();
    String ZipDesPathdup;
    //    String ZipDesPathdup = "/MBC/" + GSBilling.getInstance().getKEYNAME() + GSBilling.getInstance().captureDatetime();
    String outZip = Environment.getExternalStorageDirectory() + "/" + GSBilling.getInstance().getKEYNAME() + ".zip";
    //    String signaturePathDes = Environment.getExternalStorageDirectory() + "/MBC" + "/" + GSBilling.getInstance().getKEYNAME() + "_" + Structconsmas.Consumer_Number + "_sig.jpg";
//    String photoPathDes = Environment.getExternalStorageDirectory() + "/MBC/" + GSBilling.getInstance().getKEYNAME() + "_" + Structconsmas.Consumer_Number + "_mtr.jpg";
    String convertedNumber, typePay;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File dir2 = new File(ZipDeletPath2);

        DeleteRecursive(dir2);
        dbHelper = new DB(getApplicationContext());
        SD = dbHelper.getWritableDatabase();
        Calendar c = Calendar.getInstance();
        HOURS = c.get(Calendar.HOUR);
        MIN = c.get(Calendar.MINUTE);
        AP = c.get(Calendar.AM_PM);


        ProgressDialog progress;
        comApp = new UtilAppCommon();
        ser_key = comApp.findSequence(getApplicationContext(), "ReceiptNumber");
        typePay = GSBilling.getInstance().getPayType();

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


        Structcolmas.COL_DATE = strDate;
        Structcolmas.COL_TIME = strtime;
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
//        key         = session.retLicence();
        key = comApp.UniqueCode(getApplicationContext());
        username = user.get(SessionManager.KEY_EMAIL);
        int a = Integer.valueOf(Structcolmas.AMOUNT);
        try {
            convertedNumber = Words.convertNumberToWords(a);
        } catch (Exception e) {

        }

        this.ZipDesPathdup = "/MBC/" + key + "_col_" + GSBilling.getInstance().captureDatetime();
        this.ZipDesPath = Environment.getExternalStorageDirectory() + "/MBC/" + key + "_col_" + GSBilling.getInstance().captureDatetime() + ".zip";
//        Structcolmas.RECEIPT_NO=key+"_"+strDate.replace("-","")+"_"+ser_key;
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

        new EnterTextAsyc().execute();
//        EnterTextAsyc asynctask  = new EnterTextAsyc();
//        asynctask.execute(0);

    }

    /*   This method shows the EnterTextAsyc  AsynTask operation */
    public class EnterTextAsyc extends AsyncTask<Integer, Integer, Integer> {
        /* displays the progress dialog until background task is completed*/
        @Override
        protected void onPreExecute() {
            // ProgressDialog(context, "Please Wait....");
            ArrayList<String> listSeq = new ArrayList<String>();
            prgDialog = new ProgressDialog(CollPrintActivity.this);
            prgDialog.setMessage("Please Wait ....");
            prgDialog.setIndeterminate(true);
            prgDialog.setCancelable(false);
            prgDialog.show();

            /*Table Insert Before Printing*/
            dbHelper.insertIntoColmasTable();
            dbHelper.insertSequence("ReceiptNumber", ser_key);

            listSeq.add(Structcolmas.RECEIPT_NO + "|" + ser_key);
            generateLastREC(getApplicationContext(), "Rec.txt", listSeq);

        }

        /* Task of EnterTextAsyc performing in the background*/
        @Override
        protected Integer doInBackground(Integer... params) {
//            if(Structcolmas.PYMNT_MODE.equals("C")){
            switch (Structconsmas.PICK_REGION) {

                case "10"://bhopal
                    if (typePay.equalsIgnoreCase("CASH")) {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "------ cut here ------" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + Structcollection.BILL_MON + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    } else if (typePay.equalsIgnoreCase("CHEQUE")) {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "CHEQUE DATE :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "CHEQUE NO.  :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "------ cut here ------" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + Structcollection.BILL_MON + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "CHEQUE DATE :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "CHEQUE NO.  :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    } else if (typePay.equalsIgnoreCase("DD")) {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "DD DATE     :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "DD NO.      :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "------ cut here ------" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + Structcollection.BILL_MON + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "DD DATE     :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "DD NO.      :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    } else {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "------ cut here ------" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    }
                    break;
                case "11"://jabalpur
                    if (typePay.equalsIgnoreCase("CASH")) {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       JABALPUR       " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "------ cut here ------" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       JABALPUR       " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    } else if (typePay.equalsIgnoreCase("CHEQUE")) {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       JABALPUR       " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "CHEQUE DATE :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "CHEQUE NO.  :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "------ cut here ------" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       JABALPUR       " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "CHEQUE DATE :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "CHEQUE NO.  :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    } else if (typePay.equalsIgnoreCase("DD")) {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       JABALPUR       " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "DD DATE     :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "DD NO.      :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "------ cut here ------" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       JABALPUR       " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "DD DATE     :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "DD NO.      :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    } else {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       JABALPUR       " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "------ cut here ------" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       JABALPUR       " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    }

                    break;
                case "12"://indore
                    if (typePay.equalsIgnoreCase("CASH")) {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       INDORE         " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +

                                        "<0x09>" + "------ cut here ------" + "\n" +

                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       INDORE         " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    }
                    if (typePay.equalsIgnoreCase("CHEQUE")) {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       INDORE         " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "CHEQUE DATE :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "CHEQUE NO.  :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +

                                        "<0x09>" + "------ cut here ------" + "\n" +

                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       INDORE         " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "CHEQUE DATE :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "CHEQUE NO.  :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    } else if (typePay.equalsIgnoreCase("DD")) {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       JABALPUR       " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "DD DATE     :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "DD NO.      :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +

                                        "<0x09>" + "------ cut here ------" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +

                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       JABALPUR       " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + Structcollection.BILL_MON + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "DD DATE     :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "DD NO.      :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    } else {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       INDORE         " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +

                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "------ cut here ------" + "\n" +

                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       INDORE         " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    }
                    break;
                default:
                    if (typePay.equalsIgnoreCase("CASH")) {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "------ cut here ------" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +

                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +

                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "CHECK DATE  :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "CHECK NO.   :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    } else if (typePay.equalsIgnoreCase("CHEQUE")) {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "CHEQUE DATE :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "CHEQUE NO.  :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "------ cut here ------" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +

                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "CHEQUE DATE :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "CHEQUE NO.  :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    } else if (typePay.equalsIgnoreCase("DD")) {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "DD DATE     :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "DD NO.      :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +

                                        "<0x09>" + "------ cut here ------" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +

                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + Structcollection.BILL_MON + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "DD DATE     :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "DD NO.      :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    } else {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + Structcollection.BILL_MON + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "  Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +

                                        "<0x09>" + "------ cut here ------" + "\n" +

                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + Structcollection.BILL_MON + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    }
                    break;
            }
            return 0;
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
            dbHelper3 = new DB(getApplicationContext());
            SD3 = dbHelper3.getWritableDatabase();

            String ColBckQwer = "SELECT * FROM TBL_COLMASTER_MP WHERE Upload_Flag='N' ";//WHERE Upload_Flag='N'
            Cursor curColBck = SD3.rawQuery(ColBckQwer, null);

            String arrStr[] = null;
            ArrayList<String> listBck = new ArrayList<String>();


            if (curColBck != null && curColBck.moveToFirst()) {

                while (curColBck.isAfterLast() == false) {

                    listBck.add(curColBck.getString(1) + "}" + curColBck.getString(2) + "}" + curColBck.getString(3) +
                            "}" + curColBck.getString(4) + "}" + curColBck.getString(5) + "}" + curColBck.getString(6) +
                            "}" + curColBck.getString(10) + "}" + curColBck.getString(7) + "}" + curColBck.getString(8) +
                            "}" + curColBck.getString(9) + "}" + curColBck.getString(11) + "}" + curColBck.getString(12) +
                            "}" + curColBck.getString(13) + "}" + curColBck.getString(14) + "}" + curColBck.getString(15) +
                            "}" + curColBck.getString(18) + "}" + curColBck.getString(19) + "}" + curColBck.getString(20) +
                            "}" + curColBck.getString(21) + "}" + curColBck.getString(22) + "}" + curColBck.getString(23) +
                            "}" + curColBck.getString(24) + "}" + curColBck.getString(25) + "}" + curColBck.getString(26) +
                            "}" + curColBck.getString(27) + "}");
                    // mylist2.add(curColselect.getString(7));


                    curColBck.moveToNext();
                }

                genBck(getApplicationContext(), "Col_bck.csv", listBck);
            }

            showdialog();

            super.onPostExecute(result);
        }
    }

    /*  To show response messages  */
    public void showdialog() {

        new SweetAlertDialog(CollPrintActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Bluetooth Printer")
                .setContentText("Successfully Printed")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

                        if (isMobileDataEnabled()) {

                            new TextFileClass(CollPrintActivity.this).execute();

                        } else {

                            Toast.makeText(CollPrintActivity.this, "Internally stored due to no connectivity", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(CollPrintActivity.this, Collection.class);
                            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("printtype", print_type);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);
                        }

                    }
                })
                .show();


    }


    private void sendMessage(String message) {
        // TODO Auto-generated method stub

        Log.e("", "MESSAGE " + message);

        Intent myIntent = new Intent();
        myIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        myIntent.putExtra("message", message);
        myIntent.setAction("com.softland.iprintMarvel.Bluetooth.message");
        myIntent.setType("*/*");
        sendBroadcast(myIntent);
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

    public void onBackPressed() {

        Intent intent = new Intent(CollPrintActivity.this, Collection.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);

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


                if (curColselect != null && curColselect.moveToFirst()) {

                    while (curColselect.isAfterLast() == false) {
                        String name = curColselect.getString(0);

                        mylist.add(curColselect.getString(1) + "}" + curColselect.getString(2) + "}" + curColselect.getString(3) +
                                "}" + curColselect.getString(4) + "}" + curColselect.getString(5) + "}" + curColselect.getString(6) +
                                "}" + curColselect.getString(10) + "}" + curColselect.getString(7) + "}" + curColselect.getString(8) +
                                "}" + curColselect.getString(9) + "}" + curColselect.getString(11) + "}" + curColselect.getString(12) +
                                "}" + curColselect.getString(13) + "}" + curColselect.getString(14) + "}" + curColselect.getString(15) +
                                "}" + curColselect.getString(18) + "}" + curColselect.getString(19) + "}" + curColselect.getString(20) +
                                "}" + curColselect.getString(21) + "}" + curColselect.getString(22) + "}" + curColselect.getString(23) +
                                "}" + curColselect.getString(24) + "}" + curColselect.getString(25) + "}" + curColselect.getString(26) +
                                "}" + curColselect.getString(27) + "}" + curColselect.getString(28) + "}" + curColselect.getString(29) +
                                "}" + curColselect.getString(30) + "}" + curColselect.getString(31) + "}" + curColselect.getString(32) +
                                "}" + curColselect.getString(33) + "}" + curColselect.getString(34) + "}" + curColselect.getString(35) +
                                "}" + curColselect.getString(37) + "}" + curColselect.getString(38) + "}" + curColselect.getString(39) + "}");
                        // mylist2.add(curColselect.getString(7));


                        curColselect.moveToNext();
                    }
                    generateNoteOnSD(getApplicationContext(), "colmobile.csv", mylist);


                }

                CollPrintActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progress.dismiss();
                        new PostClass(CollPrintActivity.this).execute();
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
            Log.e("tag", e.getMessage());
        }
    }

    public void generateNoteOnSD(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "MBC/Downloadcol/");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            int length = sBody.size();
            for (int i = 0; i < length; i++) {
                // System.out.println("selqwer1234 " + sBody.get(i));

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

    public void genBck(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "MBC/");
            if (!root.exists()) {
                root.mkdirs();
            } else {
                root.delete();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            int length = sBody.size();
            for (int i = 0; i < length; i++) {

                writer.append(sBody.get(i).toString());
                writer.append("\n");
            }

            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
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

    private class PostClass extends AsyncTask<String, Void, Boolean> {

        private final Context context;
        public String succsess = null;

        public PostClass(Context c) {

            this.context = c;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();

            zipFolder(ZipCopyPath, ZipDesPath);
            GSBilling.getInstance().setFinalZipName(ZipDesPathdup);

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip");
                HttpFileUpload hfu = new HttpFileUpload("http://enservtest.fedco.co.in/MPSurvey/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                 HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");

                int status = hfu.Send_Now(fstrm);
                if (status != 200) {
                    // succsess = "1";
                    CollPrintActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();
                            Toast.makeText(CollPrintActivity.this, "Internaly Stored Due to No Connectivity", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(CollPrintActivity.this, Collection.class);
                            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("printtype", print_type);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);
                        }
                    });

                } else {
                    // succsess = "0";
                    dbHelper2 = new DB(getApplicationContext());
                    SD2 = dbHelper2.getWritableDatabase();

                    String updatequer = "UPDATE  TBL_COLMASTER_MP  SET Upload_Flag = 'Y'";
                    Cursor curBillupdate = SD2.rawQuery(updatequer, null);
                    if (curBillupdate != null && curBillupdate.moveToFirst()) {
                        Log.v("Update ", "Success");
                    }

                    CollPrintActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();
                            Toast.makeText(CollPrintActivity.this, " Successfully Uploaded to Server ", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(CollPrintActivity.this, Collection.class);
                            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("printtype", print_type);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);
                        }
                    });
                }

            } catch (Exception e) {
                // Error: File not found
//                succsess = "0";
                e.printStackTrace();

            }


            return true;

        }

        protected void onPostExecute() {
            progress.dismiss();

            new File(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip").delete();
            Intent intent = new Intent(CollPrintActivity.this, Collection.class);
            //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("printtype", print_type);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_in_left,
                    R.anim.anim_slide_out_left);

        }

    }

    void DeleteRecursive(File dir) {
        Log.d("DeleteRecursive", "DELETEPREVIOUS TOP" + dir.getPath());
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                File temp = new File(dir, children[i]);
                if (temp.isDirectory()) {
                    Log.d("DeleteRecursive", "Recursive Call" + temp.getPath());
                    DeleteRecursive(temp);
                } else {
                    Log.d("DeleteRecursive", "Delete File" + temp.getPath());
                    boolean b = temp.delete();
                    if (b == false) {
                        Log.d("DeleteRecursive", "DELETE FAIL");
                    }
                }
            }

        }
        dir.delete();
    }

    public static void zipFolder(String inputFolderPath, String outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outZipPath);
            // GSBilling.getInstance().setFinalZipName();
            ZipOutputStream zos = new ZipOutputStream(fos);
            File srcFile = new File(inputFolderPath);
            File[] files = srcFile.listFiles();
            Log.d("", "Zip directory: " + srcFile.getName());
            for (int i = 0; i < files.length; i++) {
                Log.d("", "Adding file: " + files[i].getName());
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
            Log.e("", ioe.getMessage());
        }
    }

}
