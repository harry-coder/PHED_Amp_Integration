
package com.fedco.mbc.amigos;

import android.Manifest;
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
import com.fedco.mbc.activity.MainActivity;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.model.Structbilling;
import com.fedco.mbc.model.Structcolmas;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.model.Structtariff;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.AndroidBmpUtil;
import com.fedco.mbc.utils.HttpFileUpload;
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


public class DuplicateAmigoPrinting extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener, QABTPAccessory {
    private static final String TAG = DuplicateAmigoPrinting.class.getSimpleName();
    //    @InjectView(R.id.log_txt)
    TextView mLogTxt, tv_Print;
    //    EditText etText;
    int scalingVal, position;
    Spinner devListSpinner;
    ArrayAdapter<String> spinAdapter;
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    BluetoothManager btpObject;

    String test, strMeterMake, strMeterOwner;
    String sampleText;
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
    String printerName=null;

    Logger Log;
    SessionManager session;
    SQLiteDatabase SD, SD2, SD3, SD4;
    DB dbHelper, dbHelper2, dbHelper3, dbHelper4;
    UtilAppCommon appcomUtil;

    private ProgressDialog progress;
    int Cur_Meter_Reading;
    int Prev_Meter_Reading;
    String Bill_Basis;
    String Reading_type_code;

    ArrayList<String> mylistimagename = new ArrayList<String>();

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
    String mdValue, billTime;
    String pfValue;
    Float miscValue;

    StringBuffer stringBuffer = new StringBuffer();
    public final int singleVarLinefeed = 1;
    public final int doubleVarlinefeed = 2;
    public final int tripleVarlinefeed = 3;
    public final int fourVarlinefeed = 4;
    public final int fiveVarlinefeed = 5;
    public final int sixVarlinefeed = 6;

    private int regionFlag = 0;
    private int indoreFlag = 0;

    String test2;


    public String fixedLengthString(String str, int leng) {
        for (int i = str.length(); i <= leng; i++)
            str += " ";
        return str;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_amigo);

        btpObject = BluetoothManager.getInstance(this, DuplicateAmigoPrinting.this);
        sharedPreferences = this.getSharedPreferences("QABTprefs", 0);
        Mac = sharedPreferences.getString("BTDeviceMac", "NA");

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

        if (Structbilling.md_input != null) {
            mdValue = String.valueOf(Structbilling.md_input) + " " + "KW";
        } else {
            mdValue = "0";
        }

        appcomUtil = new UtilAppCommon();
        Calendar c = Calendar.getInstance();

        //-------------not used
        Structconsmas.Meter_Type = "00";
        Structconsmas.Meter_Ownership = "A";
        //----------

        imgSrcPath = Environment.getExternalStorageDirectory()
                + File.separator + "/MBC/Images/" + appcomUtil.UniqueCode(getApplicationContext()) + "_" + Structconsmas.MAIN_CONS_LNK_NO + "_mtr.jpg";
        imgSrcPathDes = Environment.getExternalStorageDirectory()
                + File.separator + "/MBC/Images/" + appcomUtil.UniqueCode(getApplicationContext()) + "_mtr.bmp";
        ZipDesPath = Environment.getExternalStorageDirectory() + "/MBC/" + appcomUtil.UniqueCode(getApplicationContext()) + GSBilling.getInstance().captureDatetime() + ".zip";
        ZipDesPathdup = "/MBC/" + appcomUtil.UniqueCode(getApplicationContext()) + GSBilling.getInstance().captureDatetime();
        signaturePathDes = Environment.getExternalStorageDirectory() + "/MBC" + "/" + appcomUtil.UniqueCode(getApplicationContext()) + "_" + Structconsmas.MAIN_CONS_LNK_NO + "_sig.jpg";
        photoPathDes = Environment.getExternalStorageDirectory() + "/MBC/" + appcomUtil.UniqueCode(getApplicationContext()) + "_" + Structconsmas.MAIN_CONS_LNK_NO + "_mtr.jpg";

        File dir2 = new File(ZipDeletPath2);
        DeleteRecursive(dir2);

        SimpleDateFormat fds = new SimpleDateFormat("kk:mm");
        strtime = fds.format(c.getTime());

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        key = appcomUtil.UniqueCode(getApplicationContext());

        Structbilling.SBM_No = key;
        Structbilling.Meter_Reader_Name = session.retMRName();
        Structbilling.Meter_Reader_ID = session.retMRID();

        try {
            AndroidBmpUtil.convertJpeg2Bmp(getApplicationContext(), imgSrcPath, imgSrcPathDes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HOURS = c.get(Calendar.HOUR);
        MIN = c.get(Calendar.MINUTE);
        AP = c.get(Calendar.AM_PM);

        if (HOURS < 10) {
            Hour = "0" + HOURS;
        }
        if (MIN <= 10) {
            Minute = "0" + MIN;
        }

        print_type = GSBilling.getInstance().getPrinttype();
        Calendar c1 = Calendar.getInstance();
        SimpleDateFormat fds2 = new SimpleDateFormat("kk:mm");
        billTime = fds2.format(c1.getTime());

        String PrinDate;

        PrinDate = Structbilling.Cur_Meter_Reading_Date.substring(0, 6) + Structbilling.Cur_Meter_Reading_Date.substring(9);
        switch (Structconsmas.Meter_Type.trim()) {
            case "03":
                System.out.println("in if3" + strMeterMake);
                this.strMeterMake = "1ph Static";
                break;
            case "01":
                System.out.println("in if1" + strMeterMake);
                this.strMeterMake = "1ph EM";
                break;
            case "28":
                System.out.println("in if28" + strMeterMake);
                this.strMeterMake = "LT 1 Ph AMR/AMI";
                break;
            default:
                System.out.println("in ELSE" + strMeterMake);
                System.out.println("in default" + Structconsmas.Meter_Type);
                this.strMeterMake = "";
                break;
        }

        switch (Structconsmas.Meter_Ownership) {
            case "D":
                System.out.println("in if3" + strMeterOwner);
                this.strMeterOwner = "DF";
                break;
            case "G":
                System.out.println("in if1" + strMeterOwner);
                this.strMeterOwner = "CESU";
                break;
            case "C":
                System.out.println("in if28" + strMeterMake);
                this.strMeterOwner = "CONSUMER";
                break;
            default:
                System.out.println("in ELSE" + strMeterMake);
                this.strMeterOwner = "";
                break;
        }

//        String consumerName = Structconsmas.Name;
//        if (Structconsmas.Name.length() < 22) {
//            consumerName = Structconsmas.Name + "\n";
//        } else {
//            consumerName = Structconsmas.Name.substring(0,22);
//        }

        Prev_Meter_Reading = Structconsmas.Prev_Meter_Reading;
        Cur_Meter_Reading  = Structbilling.Cur_Meter_Reading;
        Bill_Basis = Structbilling.Bill_Basis;
        Reading_type_code = RDG_TYP_CD_Print();


        if(Structbilling.Reasons != null && !Structbilling.Reasons .isEmpty()){

            if(Structbilling.Reasons.equalsIgnoreCase("Previous reading is higher than Current reading")){
                Prev_Meter_Reading = Structconsmas.Prev_Meter_Reading;
                Cur_Meter_Reading = Structconsmas.Prev_Meter_Reading;
                Bill_Basis = "ACT";
                Reading_type_code = "NORMAL";

            }

        }




        String consumerName = Structconsmas.Name;
        if (Structconsmas.Name.length() < 22) {
            //   consumerName = Structconsmas.Name + "\n";
            consumerName = fixedLengthString(consumerName, 43);// + "\n";

        } else {
            consumerName = fixedLengthString(Structconsmas.Name, 43);
            consumerName = consumerName.substring(0, 43);
        }
        Structconsmas.Name = consumerName;
        String jblConsName = Structconsmas.Name;
        if (Structconsmas.Name.length() < 22) {
            jblConsName = Structconsmas.Name;
        } else {
            jblConsName = Structconsmas.Name.substring(0, 21);
        }

/*
        String meterReaderName = Structbilling.Meter_Reader_Name;
        if (Structbilling.Meter_Reader_Name.length() < 20) {
            meterReaderName = Structbilling.Meter_Reader_Name;
        } else {
            meterReaderName = Structbilling.Meter_Reader_Name.substring(0, 18);
        }
*/

        String feederDesc = Structconsmas.FDR_SHRT_DESC;
        if (Structconsmas.FDR_SHRT_DESC.length() < 13) {
            feederDesc = Structconsmas.FDR_SHRT_DESC;
        } else {
            feederDesc = Structconsmas.FDR_SHRT_DESC.substring(0, 12);
        }

        String address = Structconsmas.address1 + Structconsmas.address2 + Structconsmas.MOH;
        String subadd1;
        String subadd2;

        if (address.length() < 22) {
            address = fixedLengthString(Structconsmas.address1 + Structconsmas.address2+ Structconsmas.MOH, 43);// + "\n";
        } else {
            if (address.length() >= 45) {
                subadd1 = address.substring(0, 43);
            } else {
                subadd1 = address;
            }

            address = fixedLengthString(subadd1, 43);

        }
        String sectionName = Structconsmas.Section_Name;
        if (sectionName.length() >= 19){
            sectionName = Structconsmas.Section_Name.substring(0,19);
        }else{
            sectionName = Structconsmas.Section_Name;
        }

        switch (Structconsmas.PICK_REGION) {

            case "10"://bhopal
                if (Structconsmas.Name.length() < 23 && address.length() < 23) {
                    test = "    " + "\n" +
                            "  ELECTRICITY BILL     " + "\n" +
                            "----------------------" + "\n" +
                            "VERSION:" +(String.format("%1$6s",Structbilling.VER_CODE))+ "\n" +
                            "ACC.NO. :" + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                            "DATE: " +(String.format("%1$6s",PrinDate)) + "  " + billTime + "\n" +
                            "---------------------" + "\n" +
                            "NAME  &  SUPPLY ADDRESS:       " + "\n" +
                            ""+String.format("%1$6s", consumerName ) + "\n" +
                            ""+String.format("%1$8s",address) + "\n" +
                            "SAN LOAD :"+ (String.format("%1$8s", Structconsmas.Load))+ "\n" +


                            "---------------------" + "\n" +
                            "CUR :" + (String.format("%1$8s", Cur_Meter_Reading)) + " " + Structbilling.Cur_Meter_Reading_Date + " " + Structbilling.Cur_Meter_Stat + "\n" +
                            "PRV :" + (String.format("%1$8s", Prev_Meter_Reading))+ " " + Structconsmas.Prev_Meter_Reading_Date + " " + Structconsmas.Prev_Meter_Status +"\n" +
                            "ENERGY CHG   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                            "VAT         :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                            "PREV.ARREARS :"+ (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Arrear_Demand) )))) + "\n" +
                            "TOTAL AMOUNT   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                            " " + " " + "\n";

                } else if (Structconsmas.Name.length() < 23 && address.length() > 23) {
                    test = "    " + "\n" +
                            "  ELECTRICITY BILL     " + "\n" +
                            "----------------------" + "\n" +
                            "VERSION:" +(String.format("%1$6s",Structbilling.VER_CODE))+ "\n" +
                            "ACC.NO. :" + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                            "DATE: " +(String.format("%1$6s",PrinDate)) + "  " + billTime + "\n" +
                            "---------------------" + "\n" +
                            "NAME  &  SUPPLY ADDRESS:       " + "\n" +
                            ""+String.format("%1$6s", consumerName ) + "\n" +
                            ""+String.format("%1$8s",address) + "\n" +
                           // "SAN LOAD :"+ (String.format("%1$8s", Structconsmas.Load))+ "\n" +
                            "SAN LOAD :"+ (String.format("%1$8s", Structconsmas.Load))+ "\n" +

                            "---------------------" + "\n" +
                            "CUR :" + (String.format("%1$8s", Cur_Meter_Reading)) + " " + Structbilling.Cur_Meter_Reading_Date + " " + Structbilling.Cur_Meter_Stat + "\n" +
                            "PRV :" + (String.format("%1$8s", Prev_Meter_Reading))+ " " + Structconsmas.Prev_Meter_Reading_Date + " " + Structconsmas.Prev_Meter_Status +"\n" +
                            "ENERGY CHG   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                            "VAT         :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                            "PREV.ARREARS :"+ (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Arrear_Demand) )))) + "\n" +
                            "TOTAL AMOUNT   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                            " " + " " + "\n";
                } else {
                    test = "    " + "\n" +
                            "  ELECTRICITY BILL     " + "\n" +
                            "----------------------" + "\n" +
                            "VERSION:" +(String.format("%1$6s",Structbilling.VER_CODE))+ "\n" +
                            "ACC.NO. :" + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                            "DATE: " +(String.format("%1$6s",PrinDate)) + "  " + billTime + "\n" +
                            "---------------------" + "\n" +
                            "NAME  &  SUPPLY ADDRESS:       " + "\n" +
                            ""+String.format("%1$6s", consumerName ) + "\n" +
                            ""+String.format("%1$8s",address) + "\n" +
                            "SAN LOAD :"+ (String.format("%1$8s", Structconsmas.Load))+ "\n" +
                            "---------------------" + "\n" +
                            "CUR :" + (String.format("%1$8s", Cur_Meter_Reading)) + " " + Structbilling.Cur_Meter_Reading_Date + " " + Structbilling.Cur_Meter_Stat + "\n" +
                            "PRV :" + (String.format("%1$8s", Prev_Meter_Reading))+ " " + Structconsmas.Prev_Meter_Reading_Date + " " + Structconsmas.Prev_Meter_Status +"\n" +
                            "ENERGY CHG   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                            "VAT         :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                            "PREV.ARREARS :"+ (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Arrear_Demand) )))) + "\n" +
                            "TOTAL AMOUNT   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                            " " + " " + "\n";
                }
                break;
            case "11"://jabalpur
                if (Structconsmas.Name.length() < 23 && address.length() < 23) {
                    regionFlag = 1;
                    test = "    " + "\n" +
                            "  ELECTRICITY BILL     " + "\n" +
                            "----------------------" + "\n" +
                            "VERSION:" +(String.format("%1$6s",Structbilling.VER_CODE))+ "\n" +
                            "ACC.NO. :" + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                            "DATE: " +(String.format("%1$6s",PrinDate)) + "  " + billTime + "\n" +
                            "---------------------" + "\n" +
                            "NAME  &  SUPPLY ADDRESS:       " + "\n" +
                            ""+String.format("%1$6s", consumerName ) + "\n" +
                            ""+String.format("%1$8s",address) + "\n" +
                            "SAN LOAD :"+ (String.format("%1$8s", Structconsmas.Load))+ "\n" +
                            "---------------------" + "\n" +
                            "CUR :" + (String.format("%1$8s", Cur_Meter_Reading)) + " " + Structbilling.Cur_Meter_Reading_Date + " " + Structbilling.Cur_Meter_Stat + "\n" +
                            "PRV :" + (String.format("%1$8s", Prev_Meter_Reading))+ " " + Structconsmas.Prev_Meter_Reading_Date + " " + Structconsmas.Prev_Meter_Status +"\n" +
                            "ENERGY CHG   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                            "VAT         :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                            "PREV.ARREARS :"+ (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Arrear_Demand) )))) + "\n" +
                            "TOTAL AMOUNT   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                            " " + " " + "\n";

                } else if (Structconsmas.Name.length() < 23 && address.length() > 23) {
                    regionFlag = 2;
                    test = "    " + "\n" +
                            "  ELECTRICITY BILL     " + "\n" +
                            "----------------------" + "\n" +
                            "VERSION:" +(String.format("%1$6s",Structbilling.VER_CODE))+ "\n" +
                            "ACC.NO. :" + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                            "DATE: " +(String.format("%1$6s",PrinDate)) + "  " + billTime + "\n" +
                            "---------------------" + "\n" +
                            "NAME  &  SUPPLY ADDRESS:       " + "\n" +
                            ""+String.format("%1$6s", consumerName ) + "\n" +
                            ""+String.format("%1$8s",address) + "\n" +
                            "SAN LOAD :"+ (String.format("%1$8s", Structconsmas.Load))+ "\n" +
                            "---------------------" + "\n" +
                            "CUR :" + (String.format("%1$8s", Cur_Meter_Reading)) + " " + Structbilling.Cur_Meter_Reading_Date + " " + Structbilling.Cur_Meter_Stat + "\n" +
                            "PRV :" + (String.format("%1$8s", Prev_Meter_Reading))+ " " + Structconsmas.Prev_Meter_Reading_Date + " " + Structconsmas.Prev_Meter_Status +"\n" +
                            "ENERGY CHG   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                            "VAT         :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                            "PREV.ARREARS :"+ (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Arrear_Demand) )))) + "\n" +
                            "TOTAL AMOUNT   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                            " " + " " + "\n";
                } else {
                    regionFlag = 3;
                    test = "    " + "\n" +
                            "  ELECTRICITY BILL     " + "\n" +
                            "----------------------" + "\n" +
                            "VERSION:" +(String.format("%1$6s",Structbilling.VER_CODE))+ "\n" +
                            "ACC.NO. :" + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                            "DATE: " +(String.format("%1$6s",PrinDate)) + "  " + billTime + "\n" +
                            "---------------------" + "\n" +
                            "NAME  &  SUPPLY ADDRESS:       " + "\n" +
                            ""+String.format("%1$6s", consumerName ) + "\n" +
                            ""+String.format("%1$8s",address) + "\n" +
                            "SAN LOAD :"+ (String.format("%1$8s", Structconsmas.Load))+ "\n" +
                            "---------------------" + "\n" +
                            "CUR :" + (String.format("%1$8s", Cur_Meter_Reading)) + " " + Structbilling.Cur_Meter_Reading_Date + " " + Structbilling.Cur_Meter_Stat + "\n" +
                            "PRV :" + (String.format("%1$8s", Prev_Meter_Reading))+ " " + Structconsmas.Prev_Meter_Reading_Date + " " + Structconsmas.Prev_Meter_Status +"\n" +
                            "ENERGY CHG   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                            "VAT         :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                            "PREV.ARREARS :"+ (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Arrear_Demand) )))) + "\n" +
                            "TOTAL AMOUNT   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                            " " + " " + "\n";
                }
                break;
            case "12"://INDORE
                indoreFlag = 1;
                if (Structconsmas.Name.length() < 23 && address.length() < 23) {
                    if(Structconsmas.XMER_RENT.equalsIgnoreCase("1") && Structbilling
                            .Cumul_Units > 0d){

                        if(Double.parseDouble(Structconsmas.IND_ENERGY_BAL )> 0d ){
                            test = "    " + "\n" +
                                    "  ELECTRICITY BILL     " + "\n" +
                                    "----------------------" + "\n" +
                                    "VERSION:" +(String.format("%1$6s",Structbilling.VER_CODE))+ "\n" +
                                    "ACC.NO. :" + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                                    "DATE: " +(String.format("%1$6s",PrinDate)) + "  " + billTime + "\n" +
                                    "---------------------" + "\n" +
                                    "NAME  &  SUPPLY ADDRESS:       " + "\n" +
                                    ""+String.format("%1$6s", consumerName ) + "\n" +
                                    ""+String.format("%1$8s",address) + "\n" +
                                    "SAN LOAD :"+ (String.format("%1$8s", Structconsmas.Load))+ "\n" +
                                    "---------------------" + "\n" +
                                    "CUR :" + (String.format("%1$8s", Cur_Meter_Reading)) + " " + Structbilling.Cur_Meter_Reading_Date + " " + Structbilling.Cur_Meter_Stat + "\n" +
                                    "PRV :" + (String.format("%1$8s", Prev_Meter_Reading))+ " " + Structconsmas.Prev_Meter_Reading_Date + " " + Structconsmas.Prev_Meter_Status +"\n" +
                                    "ENERGY CHG   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                                    "VAT         :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                                    "PREV.ARREARS :"+ (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Arrear_Demand) )))) + "\n" +
                                    "TOTAL AMOUNT   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                                    " " + " " + "\n";
                        }else{
                            test = "    " + "\n" +
                                    "  ELECTRICITY BILL     " + "\n" +
                                    "----------------------" + "\n" +
                                    "VERSION:" +(String.format("%1$6s",Structbilling.VER_CODE))+ "\n" +
                                    "ACC.NO. :" + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                                    "DATE: " +(String.format("%1$6s",PrinDate)) + "  " + billTime + "\n" +
                                    "---------------------" + "\n" +
                                    "NAME  &  SUPPLY ADDRESS:       " + "\n" +
                                    ""+String.format("%1$6s", consumerName ) + "\n" +
                                    ""+String.format("%1$8s",address) + "\n" +
                                    "SAN LOAD :"+ (String.format("%1$8s", Structconsmas.Load))+ "\n" +
                                    "---------------------" + "\n" +
                                    "CUR :" + (String.format("%1$8s", Cur_Meter_Reading)) + " " + Structbilling.Cur_Meter_Reading_Date + " " + Structbilling.Cur_Meter_Stat + "\n" +
                                    "PRV :" + (String.format("%1$8s", Prev_Meter_Reading))+ " " + Structconsmas.Prev_Meter_Reading_Date + " " + Structconsmas.Prev_Meter_Status +"\n" +
                                    "ENERGY CHG   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                                    "VAT         :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                                    "PREV.ARREARS :"+ (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Arrear_Demand) )))) + "\n" +
                                    "TOTAL AMOUNT   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                                    " " + " " + "\n";
                        }

                    }else{
                        test = "    " + "\n" +
                                "  ELECTRICITY BILL     " + "\n" +
                                "----------------------" + "\n" +
                                "VERSION:" +(String.format("%1$6s",Structbilling.VER_CODE))+ "\n" +
                                "ACC.NO. :" + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                                "DATE: " +(String.format("%1$6s",PrinDate)) + "  " + billTime + "\n" +
                                "---------------------" + "\n" +
                                "NAME  &  SUPPLY ADDRESS:       " + "\n" +
                                ""+String.format("%1$6s", consumerName ) + "\n" +
                                ""+String.format("%1$8s",address) + "\n" +
                                "SAN LOAD :"+ (String.format("%1$8s", Structconsmas.Load))+ "\n" +
                                "---------------------" + "\n" +
                                "CUR :" + (String.format("%1$8s", Cur_Meter_Reading)) + " " + Structbilling.Cur_Meter_Reading_Date + " " + Structbilling.Cur_Meter_Stat + "\n" +
                                "PRV :" + (String.format("%1$8s", Prev_Meter_Reading))+ " " + Structconsmas.Prev_Meter_Reading_Date + " " + Structconsmas.Prev_Meter_Status +"\n" +
                                "ENERGY CHG   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                                "VAT         :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                                "PREV.ARREARS :"+ (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Arrear_Demand) )))) + "\n" +
                                "TOTAL AMOUNT   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                                " " + " " + "\n";
                    }


                } else if (Structconsmas.Name.length() < 23 && address.length() > 23) {
                    if(Structconsmas.XMER_RENT.equalsIgnoreCase("1") && Structbilling
                            .Cumul_Units > 0d){
                        if(Double.parseDouble(Structconsmas.IND_ENERGY_BAL )> 0d ){
                            test = "    " + "\n" +
                                    "  ELECTRICITY BILL     " + "\n" +
                                    "----------------------" + "\n" +
                                    "VERSION:" +(String.format("%1$6s",Structbilling.VER_CODE))+ "\n" +
                                    "ACC.NO. :" + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                                    "DATE: " +(String.format("%1$6s",PrinDate)) + "  " + billTime + "\n" +
                                    "---------------------" + "\n" +
                                    "NAME  &  SUPPLY ADDRESS:       " + "\n" +
                                    ""+String.format("%1$6s", consumerName ) + "\n" +
                                    ""+String.format("%1$8s",address) + "\n" +
                                    "SAN LOAD :"+ (String.format("%1$8s", Structconsmas.Load))+ "\n" +
                                    "---------------------" + "\n" +
                                    "CUR :" + (String.format("%1$8s", Cur_Meter_Reading)) + " " + Structbilling.Cur_Meter_Reading_Date + " " + Structbilling.Cur_Meter_Stat + "\n" +
                                    "PRV :" + (String.format("%1$8s", Prev_Meter_Reading))+ " " + Structconsmas.Prev_Meter_Reading_Date + " " + Structconsmas.Prev_Meter_Status +"\n" +
                                    "ENERGY CHG   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                                    "VAT         :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                                    "PREV.ARREARS :"+ (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Arrear_Demand) )))) + "\n" +
                                    "TOTAL AMOUNT   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                                    " " + " " + "\n";
                        }else{
                            test = "    " + "\n" +
                                    "  ELECTRICITY BILL     " + "\n" +
                                    "----------------------" + "\n" +
                                    "VERSION:" +(String.format("%1$6s",Structbilling.VER_CODE))+ "\n" +
                                    "ACC.NO. :" + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                                    "DATE: " +(String.format("%1$6s",PrinDate)) + "  " + billTime + "\n" +
                                    "---------------------" + "\n" +
                                    "NAME  &  SUPPLY ADDRESS:       " + "\n" +
                                    ""+String.format("%1$6s", consumerName ) + "\n" +
                                    ""+String.format("%1$8s",address) + "\n" +
                                    "SAN LOAD :"+ (String.format("%1$8s", Structconsmas.Load))+ "\n" +
                                    "---------------------" + "\n" +
                                    "CUR :" + (String.format("%1$8s", Cur_Meter_Reading)) + " " + Structbilling.Cur_Meter_Reading_Date + " " + Structbilling.Cur_Meter_Stat + "\n" +
                                    "PRV :" + (String.format("%1$8s", Prev_Meter_Reading))+ " " + Structconsmas.Prev_Meter_Reading_Date + " " + Structconsmas.Prev_Meter_Status +"\n" +
                                    "ENERGY CHG   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                                    "VAT         :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                                    "PREV.ARREARS :"+ (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Arrear_Demand) )))) + "\n" +
                                    "TOTAL AMOUNT   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                                    " " + " " + "\n";
                        }

                    }else{
                        test = "    " + "\n" +
                                "  ELECTRICITY BILL     " + "\n" +
                                "----------------------" + "\n" +
                                "VERSION:" +(String.format("%1$6s",Structbilling.VER_CODE))+ "\n" +
                                "ACC.NO. :" + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                                "DATE: " +(String.format("%1$6s",PrinDate)) + "  " + billTime + "\n" +
                                "---------------------" + "\n" +
                                "NAME  &  SUPPLY ADDRESS:       " + "\n" +
                                ""+String.format("%1$6s", consumerName ) + "\n" +
                                ""+String.format("%1$8s",address) + "\n" +
                                "SAN LOAD :"+ (String.format("%1$8s", Structconsmas.Load))+ "\n" +
                                "---------------------" + "\n" +
                                "CUR :" + (String.format("%1$8s", Cur_Meter_Reading)) + " " + Structbilling.Cur_Meter_Reading_Date + " " + Structbilling.Cur_Meter_Stat + "\n" +
                                "PRV :" + (String.format("%1$8s", Prev_Meter_Reading))+ " " + Structconsmas.Prev_Meter_Reading_Date + " " + Structconsmas.Prev_Meter_Status +"\n" +
                                "ENERGY CHG   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                                "VAT         :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                                "PREV.ARREARS :"+ (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Arrear_Demand) )))) + "\n" +
                                "TOTAL AMOUNT   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                                " " + " " + "\n";
                    }

                } else {
                    if(Structconsmas.XMER_RENT.equalsIgnoreCase("1") && Structbilling
                            .Cumul_Units > 0d){
                        if(Double.parseDouble(Structconsmas.IND_ENERGY_BAL )> 0d ){
                            test = "    " + "\n" +
                                    "  ELECTRICITY BILL     " + "\n" +
                                    "----------------------" + "\n" +
                                    "VERSION:" +(String.format("%1$6s",Structbilling.VER_CODE))+ "\n" +
                                    "ACC.NO. :" + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                                    "DATE: " +(String.format("%1$6s",PrinDate)) + "  " + billTime + "\n" +
                                    "---------------------" + "\n" +
                                    "NAME  &  SUPPLY ADDRESS:       " + "\n" +
                                    ""+String.format("%1$6s", consumerName ) + "\n" +
                                    ""+String.format("%1$8s",address) + "\n" +
                                    "SAN LOAD :"+ (String.format("%1$8s", Structconsmas.Load))+ "\n" +
                                    "---------------------" + "\n" +
                                    "CUR :" + (String.format("%1$8s", Cur_Meter_Reading)) + " " + Structbilling.Cur_Meter_Reading_Date + " " + Structbilling.Cur_Meter_Stat + "\n" +
                                    "PRV :" + (String.format("%1$8s", Prev_Meter_Reading))+ " " + Structconsmas.Prev_Meter_Reading_Date + " " + Structconsmas.Prev_Meter_Status +"\n" +
                                    "ENERGY CHG   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                                    "VAT         :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                                    "PREV.ARREARS :"+ (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Arrear_Demand) )))) + "\n" +
                                    "TOTAL AMOUNT   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                                    " " + " " + "\n";
                        }else{
                            test = "    " + "\n" +
                                    "  ELECTRICITY BILL     " + "\n" +
                                    "----------------------" + "\n" +
                                    "VERSION:" +(String.format("%1$6s",Structbilling.VER_CODE))+ "\n" +
                                    "ACC.NO. :" + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                                    "DATE: " +(String.format("%1$6s",PrinDate)) + "  " + billTime + "\n" +
                                    "---------------------" + "\n" +
                                    "NAME  &  SUPPLY ADDRESS:       " + "\n" +
                                    ""+String.format("%1$6s", consumerName ) + "\n" +
                                    ""+String.format("%1$8s",address) + "\n" +
                                    "SAN LOAD :"+ (String.format("%1$8s", Structconsmas.Load))+ "\n" +
                                    "---------------------" + "\n" +
                                    "CUR :" + (String.format("%1$8s", Cur_Meter_Reading)) + " " + Structbilling.Cur_Meter_Reading_Date + " " + Structbilling.Cur_Meter_Stat + "\n" +
                                    "PRV :" + (String.format("%1$8s", Prev_Meter_Reading))+ " " + Structconsmas.Prev_Meter_Reading_Date + " " + Structconsmas.Prev_Meter_Status +"\n" +
                                    "ENERGY CHG   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                                    "VAT         :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                                    "PREV.ARREARS :"+ (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Arrear_Demand) )))) + "\n" +
                                    "TOTAL AMOUNT   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                                    " " + " " + "\n";
                        }

                    }else{
                        test = "    " + "\n" +
                                "  ELECTRICITY BILL     " + "\n" +
                                "----------------------" + "\n" +
                                "VERSION:" +(String.format("%1$6s",Structbilling.VER_CODE))+ "\n" +
                                "ACC.NO. :" + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                                "DATE: " +(String.format("%1$6s",PrinDate)) + "  " + billTime + "\n" +
                                "---------------------" + "\n" +
                                "NAME  &  SUPPLY ADDRESS:       " + "\n" +
                                ""+String.format("%1$6s", consumerName ) + "\n" +
                                ""+String.format("%1$8s",address) + "\n" +
                                "SAN LOAD :"+ (String.format("%1$8s", Structconsmas.Load))+ "\n" +
                                "---------------------" + "\n" +
                                "CUR :" + (String.format("%1$8s", Cur_Meter_Reading)) + " " + Structbilling.Cur_Meter_Reading_Date + " " + Structbilling.Cur_Meter_Stat + "\n" +
                                "PRV :" + (String.format("%1$8s", Prev_Meter_Reading))+ " " + Structconsmas.Prev_Meter_Reading_Date + " " + Structconsmas.Prev_Meter_Status +"\n" +
                                "ENERGY CHG   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                                "VAT         :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                                "PREV.ARREARS :"+ (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Arrear_Demand) )))) + "\n" +
                                "TOTAL AMOUNT   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                                " " + " " + "\n";
                    }

                }
                break;
            default:
                if (Structconsmas.Name.length() < 23) {

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
                            "READER NAME      :" + String.format ( "%1$6s", MainActivity.MRNME ) + "\n" +
                            //  "READER NAME       :"+String.format("%1$6s",Structbilling.Meter_Reader_Name) + "\n" +

                            "Customer Care    :" + "070022557433" + "\n" +

                            "---------------------" + "\n";

                    test2 = "    " + "\n" +
                            "    " + "\n";

                   /* test = "    " + "\n" +
                            "  ELECTRICITY BILL     " + "\n" +
                            "----------------------" + "\n" +
                            "VERSION:" +(String.format("%1$6s",Structbilling.VER_CODE))+ "\n" +
                            "ACC.NO. :" + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                            "DATE: " +(String.format("%1$6s",PrinDate)) + "  " + billTime + "\n" +
                            "---------------------" + "\n" +
                            "NAME  &  SUPPLY ADDRESS:       " + "\n" +
                            ""+String.format("%1$6s", consumerName ) + "\n" +
                            ""+String.format("%1$8s",address) + "\n" +
                           // "SAN LOAD :"+ (String.format("%1$8s", Structconsmas.Load))+ "\n" +
                            "SAN LOAD :"+  "\n" +
                            "IBC:" + String.format ( "%1$6s", Structconsmas.Section_Name ) + "\n" +
                            "BSC:" + String.format ( "%1$6s", Structconsmas.MOH ) + "\n" +

                            "---------------------" + "\n" +
                            "CUR :" + (String.format("%1$8s", Cur_Meter_Reading)) + " " + Structbilling.Cur_Meter_Reading_Date + " " + Structbilling.Cur_Meter_Stat + "\n" +
                            "PRV :" + (String.format("%1$8s", Prev_Meter_Reading))+ " " + Structconsmas.Prev_Meter_Reading_Date + " " + Structconsmas.Prev_Meter_Status +"\n" +

                            "ENERGY CHG   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                            "VAT         :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                            "PREV.ARREARS :"+ (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Arrear_Demand) )))) + "\n" +

                            "TOTAL AMOUNT   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                            " " + " " + "\n";*/

                } else {




                    test = " " + "\n" +
                            "  ELECTRICITY BILL(Duplicate)" + "\n" +
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
                            "ENERGY CHG       :" + String.format ( "%1$8s", String.format ( "%1$6s", MainActivityCollectionPrint.internationAnotation ( String.valueOf ( Structbilling.O_TotalEnergyCharge) ) ) ) + "\n" +
                            // "VAT         :"+String.format("%1$8s",String.format("%.2f",Double.valueOf(Structbilling.Electricity_Duty_Charges))) + "\n" +
                            "VAT(5%)          :" + String.format ( "%1$8s", String.format ( "%1$6s", MainActivityCollectionPrint.internationAnotation ( String.valueOf ( Structbilling.O_ElectricityDutyCharges ) ) ) ) + "\n" +

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
                            "TOTAL AMOUNT     :" + String.format ( "%1$8s", String.format ( "%1$6s", MainActivityCollectionPrint.internationAnotation ( String.valueOf ( Structbilling.O_Total_Bill ) ) ) ) + "\n" +

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
                   /* test = "    " + "\n" +
                            "  ELECTRICITY BILL     " + "\n" +
                            "----------------------" + "\n" +
                            "VERSION:" +(String.format("%1$6s",Structbilling.VER_CODE))+ "\n" +
                            "ACC.NO. :" + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                            "DATE: " +(String.format("%1$6s",PrinDate)) + "  " + billTime + "\n" +
                            "---------------------" + "\n" +
                            "NAME  &  SUPPLY ADDRESS:       " + "\n" +
                            ""+String.format("%1$6s", consumerName ) + "\n" +
                            ""+String.format("%1$8s",address) + "\n" +
                            // "SAN LOAD :"+ (String.format("%1$8s", Structconsmas.Load))+ "\n" +
                            "SAN LOAD :"+  "\n" +
                            "IBC:" + String.format ( "%1$6s", Structconsmas.Section_Name ) + "\n" +
                            "BSC:" + String.format ( "%1$6s", Structconsmas.MOH ) + "\n" +

                            "---------------------" + "\n" +
                            "CUR :" + (String.format("%1$8s", Cur_Meter_Reading)) + " " + Structbilling.Cur_Meter_Reading_Date + " " + Structbilling.Cur_Meter_Stat + "\n" +
                            "PRV :" + (String.format("%1$8s", Prev_Meter_Reading))+ " " + Structconsmas.Prev_Meter_Reading_Date + " " + Structconsmas.Prev_Meter_Status +"\n" +
                            "ENERGY CHG   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                            "VAT         :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                            "PREV.ARREARS :"+ (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Arrear_Demand) )))) + "\n" +
                            "TOTAL AMOUNT   :" + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                            " " + " " + "\n";*/
                }
                break;
        }

        Log.e(getApplicationContext(),"Find Duplcaite"," ME "+test);

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
        dialog.setMessage("Are You Sure To Exit.?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
//                btpObject.stopBluetoothService();

                UtilAppCommon ucom = new UtilAppCommon();
                UtilAppCommon.nullyfimodelCon();
                UtilAppCommon.nullyfimodelBill();

                Intent intent = new Intent();
                intent.setComponent(new ComponentName(getApplicationContext(), BillingtypesActivity.class));
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

    public void checkBTP_Permissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int permissionCheck = 0;
            permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        } else {
            // Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    @Override
    public void onBluetoothStartDiscovery() {
    }

    @Override
    public void onClientDisconnectSuccess() {
    }

    @Override
    public void onNoClientConnected() {
    }

    @Override
    public void onBluetoothDeviceFound(BluetoothDevice device) {
        int i = 0;

        printerName = device.getName();
        if (device.getName() != null && ((device.getName().contains("QA"))||(device.getName().contains("ESBAA0050"))||(device.getName().contains("MHT-P5801"))||(device.getName().contains("TM-P20_001644"))|| (device.getName().contains("SP120E"))|| (device.getName().contains("SP120"))|| (device.getName().contains("XL-1880")) || (device.getName().contains("Dual-SPP")) ||(device.getName().contains("QSPrinter")) ||(device.getName().contains("QSprinter"))|| (device.getName().contains("QuantumAeon")))) {
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
    public void printQrcodeSP(){
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
    public void printPhoto() {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.phedlogo);
        if(bmp!=null){
            byte[] command = Utils.decodeBitmap(bmp);

            btpObject.sendMessage(PrinterCommands.ESC_ALIGN_LEFT);
            btpObject.sendMessage(command);
        }else{
            // Log.e("Print Photo error", "the file doesn't exists");
        }
    }

    private Bitmap textToImage(String text, int width, int height) throws WriterException, NullPointerException {
        BitMatrix bitMatrix;
        Bitmap bitmap = null;
        try {
            bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.DATA_MATRIX.QR_CODE, width, height, null);
            int bitMatrixWidth = bitMatrix.getWidth();
            int bitMatrixHeight = bitMatrix.getHeight();
            int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

            int colorWhite = 0xFFFFFFFF;
            int colorBlack = 0xFF000000;

            for (int y = 0; y < bitMatrixHeight; y++) {
                int offset = y * bitMatrixWidth;
                for (int x = 0; x < bitMatrixWidth; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? colorBlack : colorWhite;
                }
            }
            bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
            bitmap.setPixels(pixels, 0, width, 0, 0, bitMatrixWidth, bitMatrixHeight);

            return bitmap;
        }catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
        }
        return bitmap;
    }


    public void setLogText(String text) {
        mLogTxt.setText(mLogTxt.getText() + "\n" + text);
        mLogTxt.setText(text + "\n");
    }

    @Override
//    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        String item = adapterView.getItemAtPosition(i).toString();
//        Toast.makeText(DuplicateAmigoPrinting.this, item + "asfdasdasd", Toast.LENGTH_LONG).show();
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
//                    stringBuffer.append(test);
//                    String testString[] = stringBuffer.toString().split("\n");
//                    for (int j = 0; j < 18; j++) {
//                        btpObject.sendMessage(testString[j]);
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
////                        btpObject.varblankdotlinespace(singleVarLinefeed);
//                    }
//                    for (int k = 18; k < 19; k++) {
//                        btpObject.sendMessage(testString[k]);
////                        btpObject.varblankdotlinespace(doubleVarlinefeed);
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
//                    for (int m = 19; m < 20; m++) {
//                        btpObject.sendMessage(testString[m]);
////                        btpObject.varblankdotlinespace(doubleVarlinefeed);
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
//                    btpObject.varblankdotlinespace(singleVarLinefeed);
//                    for (int n = 20; n < 21; n++) {
//                        btpObject.sendMessage(testString[n]);
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
//                    btpObject.varblankdotlinespace(singleVarLinefeed);
//                    for (int o = 21; o < 22; o++) {
//                        btpObject.sendMessage(testString[o]);
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
//                    btpObject.varblankdotlinespace(tripleVarlinefeed);
//                    for (int p = 22; p < 23; p++) {
//                        btpObject.sendMessage(testString[p]);
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
//                    for (int q = 23; q < 24; q++) {
//                        btpObject.sendMessage(testString[q]);
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
//                    btpObject.varblankdotlinespace(singleVarLinefeed);
//                    for (int r = 24; r < testString.length; r++) {
//                        btpObject.sendMessage(testString[r]);
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
//                    regionFlag = 0;
//                } else if (regionFlag == 2) {
//                    stringBuffer.append(test);
//                    String testString[] = stringBuffer.toString().split("\n");
//                    for (int j = 0; j < 18; j++) {
//                        btpObject.sendMessage(testString[j]);
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
//                    btpObject.varblankdotlinespace(singleVarLinefeed);
//                    for (int k = 18; k < 19; k++) {
//                        btpObject.sendMessage(testString[k]);
////                        btpObject.varblankdotlinespace(doubleVarlinefeed);
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
//                    for (int m = 19; m < 20; m++) {
//                        btpObject.sendMessage(testString[m]);
////                        btpObject.varblankdotlinespace(doubleVarlinefeed);
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
//                    btpObject.varblankdotlinespace(singleVarLinefeed);
//                    for (int n = 20; n < 21; n++) {
//                        btpObject.sendMessage(testString[n]);
////                        btpObject.varblankdotlinespace(doubleVarlinefeed);
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
//                    btpObject.varblankdotlinespace(singleVarLinefeed);
//                    for (int o = 21; o < 22; o++) {
//                        btpObject.sendMessage(testString[o]);
////                        btpObject.varblankdotlinespace(doubleVarlinefeed);
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
//                    for (int p = 22; p < 23; p++) {
//                        btpObject.sendMessage(testString[p]);
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
//                    btpObject.varblankdotlinespace(fourVarlinefeed);
//                    for (int q = 23; q < testString.length; q++) {
//                        btpObject.sendMessage(testString[q]);
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
//                    btpObject.varblankdotlinespace(singleVarLinefeed);
//                    regionFlag = 0;
//                } else if (regionFlag == 3) {
//                    stringBuffer.append(test);
//                    String testString[] = stringBuffer.toString().split("\n");
//                    for (int j = 0; j < 18; j++) {
//                        btpObject.sendMessage(testString[j]);
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
//                    btpObject.varblankdotlinespace(doubleVarlinefeed);
//                    for (int k = 18; k < 19; k++) {
//                        btpObject.sendMessage(testString[k]);
////                        btpObject.varblankdotlinespace(doubleVarlinefeed);
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
//                    for (int m = 19; m < 20; m++) {
//                        btpObject.sendMessage(testString[m]);
////                        btpObject.varblankdotlinespace(doubleVarlinefeed);
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
//                    btpObject.varblankdotlinespace(singleVarLinefeed);
//                    for (int n = 20; n < 21; n++) {
//                        btpObject.sendMessage(testString[n]);
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
//                    btpObject.varblankdotlinespace(singleVarLinefeed);
//                    for (int o = 21; o < 22; o++) {
//                        btpObject.sendMessage(testString[o]);
////                        btpObject.varblankdotlinespace(sixVarlinefeed);
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
//                    btpObject.varblankdotlinespace(singleVarLinefeed);
//                    for (int p = 22; p < testString.length - 1; p++) {
//                        btpObject.sendMessage(testString[p]);
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
//                    btpObject.varblankdotlinespace(5);
//                    btpObject.varblankdotlinespace(5);
//                    regionFlag = 0;
//                } else if (indoreFlag == 1) {
//                    stringBuffer.append(test);
//                    btpObject.varblankdotlinespace(doubleVarlinefeed);
//                    String testString[] = stringBuffer.toString().split("\n");
//                    for (int r = 0; r < 24; r++) {
//                        btpObject.sendMessage(testString[r]);
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
//                    btpObject.varblankdotlinespace(tripleVarlinefeed);
//                    for (int s = 24; s < 64; s++) {
//                        btpObject.sendMessage(testString[s]);
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
//                    btpObject.varblankdotlinespace(doubleVarlinefeed);
//                    for (int t = 64; t < testString.length - 1; t++) {
//                        btpObject.sendMessage(testString[t]);
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
//                    btpObject.varblankdotlinespace(5);
//                    btpObject.varblankdotlinespace(5);
//                    indoreFlag = 0;
//                } else {
//                    stringBuffer.append(test);
////                    btpObject.varblankdotlinespace(doubleVarlinefeed);
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
////                    btpObject.varblankdotlinespace(doubleVarlinefeed);
//                }
//
//                try {
//                    Thread.sleep(3000);
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(DuplicateAmigoPrinting.this, item + "asfdasdasd", Toast.LENGTH_LONG).show();
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
                if (test.length()>100) {
                    printPhoto();
                    btpObject.sendMessage(test);

                    if(printerName.equalsIgnoreCase("SP120E")) {
                        String strl = (GSBilling.getInstance().ConsumerNO + "," + GSBilling.getInstance().MeterNo + ","+ Structcolmas.AMOUNT + "," + GSBilling.getInstance().RecieptNo );
                        Bitmap btt=textToImage(strl, 500, 500);
                        byte[] command = Utils.decodeBitmap(Bitmap.createScaledBitmap(btt, 200, 200, false));
                        btpObject.sendMessage(PrinterCommands.ESC_ALIGN_CENTER);
                        btpObject.sendMessage(command);
                        btpObject.sendMessage("\n");

                        //   btpObject.sendMessage("\n ");
                        //  printQrcode();
                    }else{
                        printQrcodeSP();
                    }
                    btpObject.sendMessage(test2);
//                    btpObject.sendMessage(test2);
                }
//                for (int j = 0; j < testString.length; j++) {
//                    btpObject.sendMessage(testString[j]);
//                 //   final int singleVarLinefeed = 1;
//                 //   btpObject.varblankdotlinespace(singleVarLinefeed);
//                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }if(printerName.equalsIgnoreCase("SP120E")){

                }else {
                    btpObject.closeConnection();
                }
//                btpObject.closeConnection();
                new DuplicateAmigoPrinting.PrintProcess().execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private String RDG_TYP_CD_Print() {

        dbHelper = new DB(DuplicateAmigoPrinting.this);
        SD = dbHelper.getWritableDatabase();

        String strPref = "SELECT STATUS FROM TBL_METERSTATUSCODE_MP WHERE BILL_BASIS='" + Structbilling.Bill_Basis + "'";


        Cursor getPref = SD.rawQuery(strPref, null);

        if (getPref != null && getPref.moveToFirst()) {

            return getPref.getString(0);

        } else {
            return "NO METER";
        }

    }

    private String printSurcharge() {

        if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
            return "0";
        } else {
            return Structconsmas.SURCHARGE_ARREARS;
        }

    }

    private String dotSeparate(String value) {

        if (value.contains(".")) {
            String[] parts = value.split("\\."); // escape .
            String part1 = parts[0];
            return part1;
        }
        return value;
    }

    private String printtariif() {
        String st1 = null;
        String printtariif = null;
        if (Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) {
            st1 = Structtariff.Tariff_URBAN;
        } else {
            st1 = Structtariff.Tariff_RURAL;
        }

        return Structconsmas.Tariff_Code + "-" + st1;
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
            appcomUtil = new UtilAppCommon();
//            if (print_type.equals("1")) {
////                sendMessage("<0x9>CON CODE :" + Structbilling.Cons_Number + "\n" +
////                        "<0x9>B. ID    : 0001 " + "\n" +
////                        "<0x09>BILL DATE: " + Structbilling.Bill_Date + "\n" +
////                        "<0x09>Upt(" + Structbilling.Due_Date + "):" + (String.format("%.2f", Structbilling.Cur_Bill_Total)) + "\n" +
////                        "<0x09>By(" + Structbilling.Due_Date + "):" + (String.format("%.2f", +Structbilling.Amnt_bPaid_on_Rbt_Date)) + "\n" +
////                        "<0x09>Aft(" + Structbilling.Due_Date.trim() + "):" + (String.format("%.2f", Structbilling.Amnt_Paidafter_Rbt_Date)) + "\n" +
////                        "<0x10>  CESU ELECTRICITY BILL" + "\n" +
////                        "<0x10>           2016" + "\n" +
////                        "<0x09>------------------------" + "\n" +
////                        "<0x09>B. ID      : 0001" + "\n" +
////                        "<0x09>DATE & TIME: " + Structbilling.Bill_Date + "\n" +
////                        "<0x09>BILL MONTHS: " + Structbilling.Bill_Month + "\n" +
////                        "<0x09>DUE DATE   : " + Structbilling.Due_Date + "\n" +
////                        "<0x09>DIVISION   : " + Structconsmas.Division_Name + "\n" +
////                        "<0x09>SUB-DIV    : " + Structconsmas.Sub_division_Name + "\n" +
////                        "<0x09>SECTION    : " + Structconsmas.Section_Name + "\n" +
////                        "<0x09>NAME :  " + Structconsmas.Name + "\n" +
////                        "<0x10>ADDRESS   : " + "\n" +
////                        "<0x09>" + Structconsmas.address1 + Structconsmas.address2 +
////                        "<0x09>TARIFF CATG:  " + Structconsmas.Tariff_Code + "\n" +
////                        "<0x09>PHASE      : " + "1" + "\n" +
////                        "<0x09>C.LOAD     : " + Structconsmas.Load + "\n" +
////                        "<0x09>MF         : " + Structconsmas.Multiply_Factor + "\n" +
////                        "<0x09>---------------------" + "\n" +
////                        "<0x09>PRES.BL DATE :" + Structbilling.Bill_Date + "\n" +
////                        "<0x09>PREV.BL DATE :" + Structconsmas.Prev_Meter_Reading_Date + "\n" +
////                        "<0x09>PRES.READING :  " + Structbilling.Cur_Meter_Reading + "\n" +
////                        "<0x09>PREV.READING :  " + Structconsmas.Prev_Meter_Reading + "\n" +
////                        "<0x09>PRES.STATUS  :  " + Structbilling.Cur_Meter_Stat + "\n" +
////                        "<0x09>C.UNITS      :  " + Structbilling.Units_Consumed + "\n" +
////                        "<0x09>C.MONTHS     :  " + Structbilling.Bill_Period + "\n" +
////                        "<0x10>ARREARS : " + "\n" +
////                        "<0x09>PREV FY ARR   :  " + (String.format("%.2f", Structconsmas.Pre_Financial_Yr_Arr)) + "\n" +
////                        "<0x09>CURR FY ARR   :  " + (String.format("%.2f", Structconsmas.Cur_Fiancial_Yr_Arr)) + "\n" +
////                        "<0x09>Current Bill   " + "\n" +
////                        "<0x09>---------------" + "\n" +
////                        "<0x09>Current D.P.S :  " + (String.format("%.2f", Structconsmas.Delay_Payment_Surcharge)) + "\n" +
////                        "<0x09>Fix/Mis Chg   :  " + (String.format("%.2f", Structbilling.Monthly_Min_Charg_DC)) + "\n" +
////                        "<0x09>EC1           :  " + (String.format("%.2f", Structbilling.Slab_1_EC)) + "\n" +
////                        "<0x09>EC2           :  " + (String.format("%.2f", Structbilling.Slab_2_EC)) + "\n" +
////                        "<0x09>EC3           :  " + (String.format("%.2f", Structbilling.Slab_3_EC)) + "\n" +
////                        "<0x09>EC4           :  " + (String.format("%.2f", Structbilling.Slab_4_EC)) + "\n" +
////                        "<0x09>Energy Chg    :  " + (String.format("%.2f", Structbilling.Slab_1_EC)) + "\n" +
////                        "<0x09>ED            :  " + (String.format("%.2f", Structbilling.Electricity_Duty_Charges)) + "\n" +
////                        "<0x09>METER RENT    :  " + (String.format("%.2f", Structconsmas.Meter_Rent)) + "\n" +
////                        "<0x09>FPPCA Chg     :  0.00" + "\n" +
////                        "<0x09>Shunt Chg     :  0.00" + "\n" +
////                        "<0x09>Other Chg     :  0.00" + "\n" +
////                        "<0x09>              --------" + "\n" +
////                        "<0x09>Sub Total     :  " + (String.format("%.2f", Structbilling.Cur_Bill_Total)) + "\n" +
////                        "<0x09>InstlonSD     :  " + (String.format("%.2f", Structconsmas.SD_Interest_chngto_SD_AVAIL)) + "\n" +
////                        "<0x09>              --------" + "\n" +
////                        "<0x09>Gross Total   :  " + (String.format("%.2f", Structbilling.Cur_Bill_Total)) + "\n" +
////                        "<0x09>Rebate        :  " + (String.format("%.2f", Structbilling.Rbt_Amnt)) + "\n" +
////                        "<0x10>AMOUNT PAYABLE" + "\n" +
////                        "<0x10>--------------" + "\n" +
////                        "<0x09>Upt(" + Structbilling.Due_Date + "):" + (String.format("%.2f", Structbilling.Amnt_bPaid_on_Rbt_Date)) + "\n" +
////                        "<0x09>Aft(" + Structbilling.Due_Date.trim() + "):" + (String.format("%.2f", Structbilling.Amnt_Paidafter_Rbt_Date)) + "\n" +
////                        "<0x09>---------------------" + "\n" +
////                        "<0x09>L Paid Amt  :" + Structconsmas.Last_Total_Pay_Paid + "\n" +
////                        "<0x09>Rec No.     :" + Structconsmas.Last_Pay_Receipt_No + "\n" +
////                        "<0x09>L Paid Date :" + Structconsmas.Last_Pay_Date + "\n" +
////                        "<0x09>\nHELPLINE NO:18003456198" + "\n" +
////                        "<0x09>Bill Generated By :" + "\n" +
////                        "<0x09>Feedback Infra Pvt. Ltd" + "\n");
//
//            }
// else {
//            actualDate = curconmas.getString(51).trim();
            actualDate = Structconsmas.Bill_Mon;
            simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = null;
            String newDate = null;

            try {
                date = simpleDateFormat.parse(actualDate);
                SimpleDateFormat postFormater = new SimpleDateFormat("dd-MMM-yyyy");
                newDate = postFormater.format(date).trim().substring(3);
            } catch (Exception e) {
                e.printStackTrace();
            }


            Structbilling.DCNumber = "";

            billseq = appcomUtil.findSequence(getApplicationContext(), "BillNumber");

            UtilAppCommon uc = new UtilAppCommon();
            uc.print_Bill();
            uc.princonmas();

            dcSeq = appcomUtil.findSequence(getApplicationContext(), "DCNumber");
            strdcSeq = String.format("%05d", dcSeq);
            Structbilling.DCNumber = key + "/DN" + dcSeq;

            if (Structbilling.md_input != null && !Structbilling.md_input.isEmpty()) {
                mdValue = Structbilling.md_input + " " + GSBilling.getInstance().getUnitMaxDemand();
            } else {
                mdValue = "0" + " " + GSBilling.getInstance().getUnitMaxDemand();
            }

            if (GSBilling.getInstance().getPowerFactor() == 0) {
                pfValue = "0";
            } else {
                pfValue = String.valueOf(GSBilling.getInstance().getPowerFactor());
            }

            if (Structconsmas.misc_charges != null) {
                miscValue = Structconsmas.misc_charges;
            } else {
                miscValue = 0.0f;
            }
            String printtariif = (Structconsmas.Tariff_Code + "-" + (((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.Tariff_URBAN : Structtariff.Tariff_RURAL)));

//            mSendMessage(
//                    "<0x09>     MADHYA PRADESH" + "\n" +
//                            "<0x09>     M.P.M.K.V.V.C.L" + "\n" +
//                            "<0x09>    ELECTRICITY BILL" + "\n" +
//                            "<0x09>-----------------------" + "\n" +
//                            "<0x09> CUSTOMER CARE NUMBERS" + "\n" +
//                            "<0x09>-----------------------" + "\n" +
//                            "<0x09>    1800-233-1912" + "\n" +
//                            //"<0x10> 0755-2551222" + "\n" +
//                            "<0x09>-----------------------" + "\n" +
//
//                            "<0x09>BILL MONTH : " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" + //201706
//                            "<0x09>BILL DATE  : " + (String.format("%1$6s", Structbilling.Bill_Date)) + "\n" +
//                            "<0x09>BILL NO    : " + (String.format("%1$6s", Structbilling.Bill_No)) + "\n" +
//                            "<0x09>BILL PERIOD: " + (String.format("%1$6s", Structbilling.Bill_Period)) + "\n" +
//                            //  "<0x09>MACHINE NO : " + (String.format("%1$6s",Structbilling.SBM_No)) + "\n" +
//                            //"<0x09>MR NAME: " + "\n" +
//                            //"<0x09>" + (String.format("%1$6s",Structbilling.Meter_Reader_Name)) + "\n" +
//                            //"<0x09>MR ID       : " + (String.format("%1$4s",Structbilling.Meter_Reader_ID)) + "\n" +
//
//                            "<0x09>-----------------------" + "\n" +
//
//                            "<0x09>DIVISION   : " + (String.format("%1$6s", "BHOPAL CITY")) + "\n" +
//                            "<0x09>DC NAME    : " + (String.format("%1$6s", sectionName)) + "\n" +
//                            "<0x09>GROUP/DAIRY NO     : " + (String.format("%1$8s", Structconsmas.Cycle)) + "/" + (String.format("%1$8s", Structconsmas.Route_Number)) + "\n" +
//                            "<0x09>FDR DESC  : " + (String.format("%1$6s", Structconsmas.FDR_SHRT_DESC)) + "\n" +
//                            "<0x09>POLE ID   : " + (String.format("%1$9s", Structconsmas.POLE_ID)) + "\n" +
//                            "<0x09>ACC ID    : " + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
//                            "<0x09>IVRS/SC NO: " + (String.format("%1$8s", Structconsmas.LOC_CD + "-" + Structconsmas.Consumer_Number)) + "\n" +
////                    "<0x09>SUB-DIV    : " + Structconsmas.Sub_division_Name + "\n" +
////                    "<0x09>SECTION    : " + Structconsmas.Section_Name + "\n" +
//                            "<0x09>NAME      : " + (String.format("%1$6s", Structconsmas.Name)) + "\n" +
//                            "<0x10>ADDRESS   : " + "\n" +
//                            "<0x09>" + (String.format("%1$6s", Structconsmas.address1 + Structconsmas.address2)) + "\n" +
//                            "<0x09>-----------------------" + "\n" +
//
////                            "<0x09>TARIFF CATG  : " + (String.format("%1$8s",Structconsmas.Tariff_Code)) + "\n" +
//                            "<0x09>TARIFF CATG  : " + (String.format("%1$8s", printtariif)) + "\n" +
//                            "<0x09>LOAD         : " + (String.format("%1$8s", Structconsmas.Load)) + getLoadType() + "\n" +
//                            "<0x09>SECURITY DEPS: " + (String.format("%1$8s", Structconsmas.TOT_SD_HELD)) + "\n" +
//                            //   "<0x09>DAIRY NO     : " + (String.format("%1$8s",Structconsmas.Route_Number)) + "\n" +
//                            "<0x09>MTR SL NO    : " + (String.format("%1$8s", Structconsmas.Meter_S_No)) + "\n" +
////                        "<0x09>MTR OWNER    : " + (String.format("%1$6s",Structconsmas.Meter_Ownership)) + "\n" +
////                        "<0x09>MAKE         : "  + "\n" +
//                            "<0x09>MF           : " + (String.format("%1$8s", Structconsmas.MF)) + "\n" +
//                            "<0x09>PHASE        : " + (String.format("%1$8s", Structconsmas.PHASE_CD)) + "\n" +
//                            "<0x09>---------------------" + "\n" +
//                            "<0x09>READING DATE :" + (String.format("%1$4s", Structbilling.Cur_Meter_Reading_Date)) + "\n" +
//                            // "<0x09>PREV AVG UNIT: " + (String.format("%1$8s",Structconsmas.PREV_AVG_UNIT)) + "\n" +
//                            "<0x09>READING STS  : " + (String.format("%1$8s", Structbilling.Bill_Basis)) + "\n" +
////                    "<0x09>PRES.BL DATE :" + Structbilling.Bill_Date + "\n" +
////                    "<0x09>PREV.BL DATE :" + Structconsmas.Prev_Meter_Reading_Date + "\n" +
//                            "<0x09>PRES READING : " + (String.format("%1$8s", Structbilling.Cur_Meter_Reading)) + "\n" +
//                            "<0x09>PREV READING : " + (String.format("%1$8s", Structconsmas.Prev_Meter_Reading)) + "\n" +
//                            "<0x09>MD           : " + (String.format("%1$8s", mdValue)) + "\n" +
//                            "<0x09>PF           : " + (String.format("%1$8s", pfValue)) + "\n" +
//                            //  "<0x09>AVG UNITS    : " + (String.format("%1$8s",Structbilling.Units_Consumed)) + "\n" +
//                            "<0x09>MTR CONSP    : " + (String.format("%1$8s", Structbilling.MTR_CONSMP)) + "\n" +
//                            "<0x09>UNIT BILLED  : " + (String.format("%1$8s", Structbilling.O_BilledUnit_Actual)) + "\n" +
//                            "<0x09>BILL TYPE    : " + (String.format("%1$8s", Structbilling.Bill_Basis)) + "\n" +
//                            "<0x09>---------------------" + "\n" +
//                            "<0x09>MIN CHARG    : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.MIN_CHRG))))) + "\n" +
//                            "<0x09>FIXED CHARG  : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.FIXED_CHARGE))))) + "\n" +
////                    "<0x09>Current Bill   " + "\n" +
////                    "<0x09>---------------" + "\n" +
////                    "<0x09>Current D.P.S :  " + (String.format("%.2f", Structconsmas.Delay_Payment_Surcharge)) + "\n" +
////                    "<0x09>Fix/Mis Chg   :  " + (String.format("%.2f", Structbilling.Monthly_Min_Charg_DC)) + "\n" +
//                            // "<0x09>EC1          : " + (String.format("%1$8s",(String.format("%.2f", Structbilling.Slab_1_EC)))) + "\n" +
//                            //"<0x09>EC2          : " + (String.format("%1$8s",(String.format("%.2f", Structbilling.Slab_2_EC)))) + "\n" +
//                            //"<0x09>EC3          : " + (String.format("%1$8s",(String.format("%.2f", Structbilling.Slab_3_EC)))) + "\n" +
//                            //"<0x09>EC4          : " + (String.format("%1$8s",(String.format("%.2f", Structbilling.Slab_4_EC)))) + "\n" +
//                            "<0x09>ENERGY CHRG  : " + (String.format("%1$8s", (String.format("%.2f", Structbilling.Total_Energy_Charg)))) + "\n" +
//                            "<0x09>FCA CHARG    : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_FCA))))) + "\n" +
//                            "<0x09>---------------------" + "\n" +
////                        "<0x09>FIX  CHARGE   :  " + (String.format("%1$6s",Structbilling.O_Total_Fixed_Charges)) + "\n" +
//                            "<0x09>DUTY CHARGE  : " + (String.format("%1$8s", (String.format("%.2f", Structbilling.Electricity_Duty_Charges)))) + "\n" +
//                            "<0x09>METER RENT   : " + (String.format("%1$8s", (String.format("%.2f", Structbilling.Meter_Rent)))) + "\n" +
//                            "<0x09>ADJ AMT      : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structconsmas.Sundry_Allow_EC))))) + "\n" +
//                            "<0x09>PANEL CHRG   : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_OverdrwalPenalty))))) + "\n" +
//                            "<0x09>P.F CHRG     : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_PFPenalty))))) + "\n" +
////                            "<0x09>P.F CHRG     : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf("0"))))) + "\n" + //JUGAD
//                            "<0x09>TOD CHRG     : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(0))))) + "\n" +
//                            "<0x09>SD BILLED    : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(UtilAppCommon.doublecheck(Structconsmas.SD_BILLED)))))) + "\n" +
//                            "<0x09>OTHER CHRG   : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_welding_charges))))) + "\n" +
//                            "<0x09>------------------" + "\n" +
//                            "<0x09>SD INT       : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf("-"+Structconsmas.SD_INSTT_AMT))))) + "\n" +
//                            "<0x09>LOCK CR      : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_LockCreditAmount))))) + "\n" +
//                            "<0x09>EMP. REBATE  : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_Employee_Incentive))))) + "\n" +
//                            "<0x09>INCENTIVES   : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf("-"+Structbilling.Prinatable_Total_Incentives))))) + "\n" +
//                            "<0x09>SUBSIDY      : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Subsidy))))) + "\n" +
//                            "<0x09>------------------" + "\n" +
//                            "<0x09>CUR BILL AMT : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_Current_Demand))))) + "\n" +
//                            "<0x09>PREV ARREARS : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_Arrear_Demand))))) + "\n" +
//                            "<0x09>SURCHARGE ARS: " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structconsmas.SURCHARGE_ARREARS))))) + "\n" +
//                            "<0x09>PREV BILL DIFF. AMT.: " + "\n" + (String.format("%1$8s", "0.00")) + "\n" +                                                                // No diff Amount
//
//                            // "<0x09>MSC CHARG    : " + (String.format("%1$8s",(String.format("%.2f", miscValue)))) + "\n" +
//                            //"<0x09>INCENTIVES/PENALTIES : "  + "\n" + //+ (String.format("%1$6s",(String.format("%.2f", Float.valueOf(Structbilling.O_PFPenalty) + Float.valueOf(Structbilling.O_OverdrwalPenalty) -Float.valueOf(Structbilling.O_Total_Incentives))))) + "\n" +                  // doubt
//                            //"<0x09> " + (String.format("%1$6s",(String.format("%.2f", Float.valueOf(Structbilling.O_PFPenalty) + Float.valueOf(Structbilling.O_OverdrwalPenalty) - Float.valueOf(Structbilling.O_Total_Incentives))))) + "\n" +                  // doubt
//                            // doubt
//                            //    "<0x09>SD ARREARS   : " + (String.format("%1$8s",(String.format("%.2f", Float.valueOf(Structconsmas.SD_ARREAR))))) + "\n" +
//
//
//                            "<0x09>ROUND AMT    : " + (String.format("%1$8s", Structbilling.Round_Amnt)) + "\n" +
////                    "<0x09>Upt(" + Structbilling.Due_Date + "):" + (String.format("%.2f", Structbilling.Amnt_bPaid_on_Rbt_Date)) + "\n" +
////                    "<0x09>Aft(" + Structbilling.Due_Date.trim() + "):" + (String.format("%.2f", Structbilling.Amnt_Paidafter_Rbt_Date)) + "\n" +
//                            //  "<0x09>TOT AMT      : " + (String.format("%1$8s",(String.format("%.2f", Float.valueOf(Math.round(Double.parseDouble(Structbilling.O_Total_Bill))))))) + "\n" +
//                            "<0x10>PAYABLE BY DUE DT:" + "\n" +
//                            "<0x09>" + (String.format("%1$6s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
//
//                            "<0x09>DELAY PAY SURCHARG : " + "\n" +
//                            "<0x09>" + (String.format("%1$6s", Structbilling.O_Surcharge)) + "\n" +
//                            "<0x09>PAYABLE AFTER DUE DT:" + "\n" +
//                            "<0x09>" + (String.format("%1$6s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" +  // doubt
//                            "<0x09>CHEQUE DUE DT      :" + "\n" +
//                            "<0x09>" + (String.format("%1$6s", Structconsmas.FIRST_CHQ_DUE_DATE)) + "\n" +
//                            "<0x09>CASH DUE DT        :" + "\n" +
//                            "<0x09>" + (String.format("%1$6s", Structconsmas.FIRST_CASH_DUE_DATE)) + "\n" +
//                            "<0x09>....................." + "\n" +
//                            "<0x09>MACHINE NO : " + (String.format("%1$6s", Structbilling.SBM_No)) + "\n" +
//                            "<0x09>MR NAME: " + "\n" +
//                            "<0x09>" + (String.format("%1$6s", Structbilling.Meter_Reader_Name)) + "\n" +
//                            "<0x09>MR ID       : " + (String.format("%1$4s", Structbilling.Meter_Reader_ID)) + "\n" +
//
//                            "<0x09>" + " " + "\n" +
//                            "<0x09>" + " " + "\n" +
//                            "<0x09>" + " " + "\n" +
//                            "<0x09>" + " " + "\n" +
//                            "<0x09>" + " " + "\n" +
//                            "<0x09>" + " " + "\n" +
//                            "<0x10>          RECEIPT        " + "\n" +
//                            "<0x09>BILL MONTH : " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" +
//                            "<0x09>BILL DATE  : " + (String.format("%1$6s", Structbilling.Bill_Date)) + "\n" +
//                            "<0x09>BILL NO    : " + (String.format("%1$6s", Structbilling.Bill_No)) + "\n" +
//                            "<0x09>DIVISION   : " + (String.format("%1$6s", "BURHANPUR")) + "\n" +
//                            "<0x09>DC NAME    : " + (String.format("%1$6s", sectionName)) + "\n" +
//                            "<0x09>GROUP/DAIRY NO: " + (String.format("%1$8s", Structconsmas.Cycle)) + "/" + (String.format("%1$8s", Structconsmas.Route_Number)) + "\n" +
//                            "<0x09>ACC ID/SC NO: " + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
//                            "<0x09>IVRS    NO  : " + (String.format("%1$8s", Structconsmas.LOC_CD + "-" + Structconsmas.Consumer_Number)) + "\n" +
////                    "<0x09>SUB-DIV    : " + Structconsmas.Sub_division_Name + "\n" +
////                    "<0x09>SECTION    : " + Structconsmas.Section_Name + "\n" +
//                            "<0x09>CONS NAME        : " + (String.format("%1$6s", Structconsmas.Name)) + "\n" +
//                            "<0x09>PAYABLE BY DUE DT:" + "\n" +
//                            "<0x09>" + (String.format("%1$6s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
//                            "<0x09>PAYABLE AFTER DUE DT:" + "\n" +
//                            "<0x09>" + (String.format("%1$6s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" + // doubt
//                            "<0x09>" + " " + "\n" +
//                            "<0x09>" + " " + "\n" +
//                            "<0x09>" + " " + "\n" +
//                            "<0x09>" + " " + "\n");     // doubt


//            }
//            else
//            {
////                sendMessage("<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + "           " + Structbilling.Bill_Month + "\n" +
////                        "<0x09>" + "       " + Structbilling.Bill_Date + "  " + strtime +
////                        "<0x09>" + "       " + Structbilling.Bill_No + "    " + "1.0" +
////                        "<0x09>" + "              " + Structbilling.Bill_Period + "\n" +
////                        "<0x09>" + "           " + Structconsmas.Division_Name + "\n" +
////                        "<0x09>" + "           " + Structconsmas.Sub_division_Name + "\n" +
////                        "<0x09>" + "           " + Structconsmas.Section_Name + "\n" +
////                        "<0x09>" + "           " + Structbilling.SBM_No + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + "    " + Structbilling.Meter_Reader_Name.trim() + "\n" +
////                        "<0x09>" + "           " + Structbilling.Meter_Reader_ID.trim() + "\n" +
////                        "<0x09>" + "      " + Structconsmas.Electrical_Address + "\n" +
////                        "<0x09>" + "        " + Structbilling.Cons_Number + "\n" +
////                        "<0x09>" + "        " + Structconsmas.Old_Consumer_Number + "\n" +
////                        "<0x09>" + "  " + Structconsmas.Name.trim() + "\n" +
////                        "<0x09>" + "        " + Structconsmas.address1.trim() + Structconsmas.address2.trim() + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + "       " + Structconsmas.Tariff_Code + "\n" +
////                        "<0x09>" + "        " + Structconsmas.Category + "        " + Structconsmas.Load + "\n" +
//////                            "<0x09>" + "\n" +
////                        "<0x09>" + "          " + Structconsmas.Cycle + "\n" +
////                        "<0x09>" + "          " + Structconsmas.Route_Number + "\n" +
////                        "<0x09>" + "          " + Structconsmas.Meter_S_No + "\n" +
////                        "<0x09>" + "          " + strMeterOwner + "\n" +
////                        "<0x09>" + "          " + strMeterMake + "\n" +
////                        "<0x09>" + "        " + Structconsmas.Multiply_Factor + "             " + "1" + "\n" +
////                        "<0x09>" + "           " + Structbilling.Avrg_Units_Billed + "\n" +
////                        "<0x09>" + " " + "      \n" +
////                        "<0x09>" + "     " + Structbilling.Cur_Meter_Reading + " " + Structbilling.Bill_Date + " " + Structbilling.Cur_Meter_Stat + "\n" +
////                        "<0x09>" + "     " + Structconsmas.Prev_Meter_Reading + " " + Structconsmas.Prev_Meter_Reading_Date + " " + Structconsmas.Prev_Meter_Status +
////                        "<0x09>" + "    " + " 0 " + "            " + " 0 " + "\n" +
////                        "<0x09>" + "          " + Structbilling.Units_Consumed + "\n" +
////                        "<0x09>" + "          " + Structbilling.Bill_Basis + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Monthly_Min_Charg_DC))) + "\n" +
////                        "<0x09>" + "      " + String.format("%5.5s", slab1unit) + " =" + (String.format("%1$6s", String.format("%.2f", Structbilling.Slab_1_EC))) + "\n" +
////                        "<0x09>" + "      " + String.format("%5.5s", slab2unit) + " =" + (String.format("%1$6s", String.format("%.2f", Structbilling.Slab_2_EC))) + "\n" +
////                        "<0x09>" + "      " + String.format("%5.5s", slab3unit) + " =" + (String.format("%1$6s", String.format("%.2f", Structbilling.Slab_3_EC))) + "\n" +
////                        "<0x09>" + "      " + String.format("%5.5s", slab4unit) + " =" + (String.format("%1$6s", String.format("%.2f", Structbilling.Slab_4_EC))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Total_Energy_Charg))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Electricity_Duty_Charges))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structconsmas.Meter_Rent))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", "0.00")) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structconsmas.Delay_Payment_Surcharge))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", "0.00")) + "\n" +
//////                            "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structconsmas.SD_Interest_chngto_SD_AVAIL))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Cur_Bill_Total))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structconsmas.Pre_Financial_Yr_Arr))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structconsmas.Cur_Fiancial_Yr_Arr))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", "0.00")) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.House_Lck_Adju_Amnt))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structconsmas.SD_Interest_chngto_SD_AVAIL))) + "\n" +
//////                                "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Cur_Bill_Total))) + "\n" +
//////                                "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Cur_Bill_Total))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", "0.00")) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Amnt_Paidafter_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Rbt_Amnt))) + "\n" +
////                        "<0x10>" + "             " + (String.format("%1$6s", String.format("%.2f", Math.ceil(Structbilling.Amnt_bPaid_on_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL)))) + "\n" +
////                        "<0x09>" + "             " + Structbilling.Due_Date +
////                        "<0x10>" + "             " + (String.format("%1$6s", String.format("%.2f", Math.ceil(Structbilling.Amnt_Paidafter_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL)))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", "0.00")) + "\n" +
////                        "<0x09>" + " " + "\n" +
//////                            "<0x09>" + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + "     " + Structconsmas.Last_Pay_Receipt_Book_No + "\n" +
////                        "<0x09>" + "     " + Structconsmas.Last_Pay_Receipt_No + "\n" +
////                        "<0x09>" + "     " + Structconsmas.Last_Total_Pay_Paid + "    " + Structconsmas.Last_Pay_Date + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
//////                            "<0x80>" +imgSrcPathDes+ "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + "  " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
//////                            "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n");
//                mSendMessage("<0x9>BILL MONTH :" + (String.format("%1$6s", Structconsmas.Bill_Mon)) + "\n" +
//                        "<0x09>BILL DATE: " + (String.format("%1$6s", Structbilling.Bill_Date)) + "\n" +
//                        "<0x09>BILL NO:" + (String.format("%1$6s",Structbilling.Bill_No)) + "\n" +
//                        "<0x09>BILL PERIOD:" + (String.format("%1$6s",Structbilling.Bill_Period)) + "\n" +
//                        "<0x09>DIVISION:" + (String.format("%1$6s",Structconsmas.Division_Name)) + "\n" +
//                        "<0x10>DC NAME:" + (String.format("%1$6s",Structconsmas.Section_Name)) + "\n" +
//                        "<0x10>MACHINE NO:" + (String.format("%1$6s",Structbilling.SBM_No)) + "\n" +
//                        "<0x09>------------------------" + "\n" +
//                        "<0x09>METER READER NAME" + (String.format("%1$6s",Structbilling.Meter_Reader_Name)) +"\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>METER READER ID: " + (String.format("%1$6s",Structbilling.Meter_Reader_ID)) + "\n" +
//                        "<0x09>POLE ID : " + (String.format("%1$6s",Structconsmas.POLE_ID)) + "\n" +
//                        "<0x09>ACC ID   : " + (String.format("%1$6s",Structconsmas.Consumer_Number)) + "\n" +
//                        "<0x09>IVRS/SC NO: " + (String.format("%1$6s",Structconsmas.Consumer_Number + "" + Structconsmas.LOC_CD)) + "\n" +
////                    "<0x09>SUB-DIV    : " + Structconsmas.Sub_division_Name + "\n" +
////                    "<0x09>SECTION    : " + Structconsmas.Section_Name + "\n" +
//                        "<0x09>NAME :  " + (String.format("%1$6s",Structconsmas.Name)) + "\n" +
//                        "<0x10>ADDRESS   : " + "\n" +
//                        "<0x09>" + (String.format("%1$6s",Structconsmas.address1 + Structconsmas.address2)) + "\n" +
//                        "<0x09>TARIFF CATG:  " + (String.format("%1$6s",Structconsmas.Tariff_Code)) + "\n" +
//
//                        "<0x09>C.LOAD     : " + (String.format("%1$6s",Structconsmas.Load)) + "\n" +
//                        "<0x09>GROUP NO     : " + (String.format("%1$6s",Structconsmas.Cycle)) + "\n" +
//                        "<0x09>DAIRY NO     : " + (String.format("%1$6s",Structconsmas.Route_Number)) + "\n" +
//                        "<0x09>MTR SL NO    : " + (String.format("%1$6s",Structconsmas.Meter_S_No)) + "\n" +
//                        "<0x09>MTR OWNER    : " + (String.format("%1$6s",Structconsmas.Meter_Ownership)) + "\n" +
//                        "<0x09>MAKE         : "  + "\n" +
//                        "<0x09>MF           : " + (String.format("%1$6s",Structconsmas.Multiply_Factor)) + "<0x09>      PHASE: " + (String.format("%1$6s",Structconsmas.PHASE_CD)) + "\n" +
//                        "<0x09>---------------------" + "\n" +
//                        "<0x09>RDG DATE     :" + (String.format("%1$6s",Structbilling.Cur_Meter_Reading_Date)) + "\n" + "<0x09>      STS: " + (String.format("%1$6s",Structbilling.Bill_Basis)) + "\n" +
//                        "<0x09>" + "\n" +
////                    "<0x09>PRES.BL DATE :" + Structbilling.Bill_Date + "\n" +
////                    "<0x09>PREV.BL DATE :" + Structconsmas.Prev_Meter_Reading_Date + "\n" +
//                        "<0x09>PRES.READING :  " + (String.format("%1$6s",Structbilling.Cur_Meter_Reading)) + "\n" +
//                        "<0x09>PREV.READING :  " + (String.format("%1$6s",Structconsmas.Prev_Meter_Reading)) + "\n" +
//                        "<0x09>MD           :  " + (String.format("%1$6s",Structbilling.md_input)) + "\n" + "<0x09>      PF: " + (String.format("%1$6s",Structbilling.Avrg_PF)) + "\n" +
//                        "<0x09>AVG UNITS    :  " + (String.format("%1$6s",Structbilling.Units_Consumed)) + "\n" +
//                        "<0x09>UNIT BILLED  :  " + (String.format("%1$6s",Structbilling.Billed_Units)) + "\n" +
//                        "<0x09>BILL TYPE    : " + "\n" +                                // NO BILL TYPE
//                        "<0x09>---------------------" + "\n" +
//                        "<0x09>FIXED CHARGE :  " + (String.format("%1$6s",Structbilling.FIXED_CHARGE)) + "\n" +
//                        "<0x09>MIN CHARGE   :  " + (String.format("%1$6s",Structbilling.MIN_CHRG)) + "\n" +
////                    "<0x09>Current Bill   " + "\n" +
////                    "<0x09>---------------" + "\n" +
////                    "<0x09>Current D.P.S :  " + (String.format("%.2f", Structconsmas.Delay_Payment_Surcharge)) + "\n" +
////                    "<0x09>Fix/Mis Chg   :  " + (String.format("%.2f", Structbilling.Monthly_Min_Charg_DC)) + "\n" +
//                        "<0x09>EC1           :  " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Slab_1_EC)))) + "\n" +
//                        "<0x09>EC2           :  " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Slab_2_EC)))) + "\n" +
//                        "<0x09>EC3           :  " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Slab_3_EC)))) + "\n" +
//                        "<0x09>EC4           :  " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Slab_4_EC)))) + "\n" +
//                        "<0x09>Energy Chg    :  " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Total_Energy_Charg)))) + "\n" +
//                        "<0x09>FCA CHARGE    :  " + (String.format("%1$6s",Structbilling.O_Total_Fixed_Charges)) + "\n" +
//                        "<0x09>DUTY CHARGE   :  " + (String.format("%1$6s",Structbilling.O_ElectricityDutyCharges)) + "\n" +
//                        "<0x09>METER RENT    :  " + (String.format("%1$6s",Structbilling.O_MeterRent)) + "\n" +
//                        "<0x09>SURCHARGES    :  " + (String.format("%1$6s",Structbilling.O_Surcharge)) + "\n" +
//                        "<0x09>MSC CHARG     :  " + (String.format("%1$6s",(String.format("%.2f", Structconsmas.misc_charges)))) + "\n" +
//                        "<0x09>INCENTIVES/PENALITIES :" + (String.format("%1$6s",Structbilling.O_Total_Incentives)) + "\n" +                  // doubt
//                        "<0x09>" + "\n" +
//                        "<0x10>CUR BILL AMT  :  " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Cur_Bill_Total)))) + "\n" +
//                        "<0x09>SD BILLED     :  " + (String.format("%1$6s",Structconsmas.SD_BILLED))+ "\n" +
//                        "<0x09>SD ARREARS    :  " + (String.format("%1$6s",Structconsmas.SD_ARREAR)) + "\n" +
//                        "<0x09>PRE ARREARS   :" + (String.format("%1$6s",Structconsmas.Cur_Fiancial_Yr_Arr)) + "\n" +
//                        "<0x09>ADJ AMT       :" + (String.format("%1$6s",(String.format("%.2f", Structconsmas.Sundry_Allow_EC)))) + "\n" +
//                        "<0x09>SD INT        :" + (String.format("%1$6s",Structconsmas.SD_INSTT_AMT)) + "\n" +
//                        "<0x09>DIFF AMT      : " + (String.format("%1$6s","0.00")) + "\n" +                                                                // No diff Amount
//                        "<0x09>ROUND AMT     :" + (String.format("%1$6s",Structbilling.Round_Amnt)) + "\n" +
////                    "<0x09>Upt(" + Structbilling.Due_Date + "):" + (String.format("%.2f", Structbilling.Amnt_bPaid_on_Rbt_Date)) + "\n" +
////                    "<0x09>Aft(" + Structbilling.Due_Date.trim() + "):" + (String.format("%.2f", Structbilling.Amnt_Paidafter_Rbt_Date)) + "\n" +
//                        "<0x09>TOT AMT       :" + (String.format("%1$6s",Structbilling.O_Total_Bill)) + "\n" +
//                        "<0x09>DPS AMT       :" + (String.format("%1$6s",Structbilling.Delay_Pay_Surcharge)) + "\n" +
//                        "<0x09>....................." + "\n" +
//                        "<0x10>PAY BL BY DUE DT   :" + (String.format("%1$6s",(String.format("%.2f",(Double.valueOf(Structbilling.O_Total_Bill) - Double.valueOf(Structbilling.O_Surcharge)))))) + "\n" +  // doubt
//                        "<0x10>PAY BL AFTER DUE DT:" + (String.format("%1$6s",Structbilling.O_Total_Bill)) + "\n" +
//                        "<0x10>CHQ/ONLINE DUE DT  :" + (String.format("%1$6s",Structconsmas.FIRST_CHQ_DUE_DATE)) + "\n" +
//                        "<0x10>CASH DUE DT        :" + (String.format("%1$6s",Structconsmas.FIRST_CASH_DUE_DATE)) +"\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>" + "\n" +
//                        "<0x10>          RECEIPT        " + "\n" +
//                        "<0x09>BILL MON :" +  (String.format("%1$6s",Structconsmas.Bill_Mon)) + "\n" +
//                        "<0x09>DIV      :" +  (String.format("%1$6s",Structconsmas.Division_Name)) +"\n" +
//                        "<0x09>ACC ID   :" + (String.format("%1$6s",Structconsmas.Consumer_Number)) + "\n" +
//                        "<0x09>DIVISION :" + (String.format("%1$6s",Structconsmas.Division_Name)) + "\n" +
//                        "<0x09>BILL DATE :" + (String.format("%1$6s",Structbilling.Bill_Date)) + "\n" +
//                        "<0x09>IVRS/SC NO :" + (String.format("%1$6s",Structconsmas.Consumer_Number + "" + Structconsmas.LOC_CD)) + "\n" +
//                        "<0x10>TOTAL      :" + (String.format("%1$6s",(String.format("%.2f", Structbilling.Cur_Bill_Total)))) + "\n");     // doubt
//            }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            sDialog = new SweetAlertDialog(DuplicateAmigoPrinting.this, SweetAlertDialog.SUCCESS_TYPE);
            sDialog.setTitleText("Bluetooth Printer");
            sDialog.setContentText("Successfully Printed");
            sDialog.show();
            sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismissWithAnimation();

                    UtilAppCommon ucom = new UtilAppCommon();
                    UtilAppCommon.nullyfimodelCon();
                    UtilAppCommon.nullyfimodelBill();


                    Toast.makeText(DuplicateAmigoPrinting.this, "Internally stored due to no connectivity", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(DuplicateAmigoPrinting.this, BillingtypesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("printtype", print_type);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);

                }
            });


        }

        @Override
        protected void onPreExecute() {


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
            Logger.e(getApplicationContext(), "SLPrintAct", "tag" + e.getMessage());
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

            dbHelper3 = new DB(getApplicationContext());
            SD3 = dbHelper3.getWritableDatabase();
            String frt[] = new String[0];

            String serImgQwer = "Select User_Mtr_Img,User_Sig_Img from TBL_BILLING WHERE Upload_Flag='N'";
            Cursor curBillImg = SD3.rawQuery(serImgQwer, null);
            if (curBillImg != null && curBillImg.moveToFirst()) {

                Logger.e(getApplicationContext(), "SLPrintAct", "Update Success");


                mylistimagename.add(curBillImg.getString(0));
                mylistimagename.add(curBillImg.getString(1));
                Logger.e(getApplicationContext(), "SLPrintAct", "mtr_img" + curBillImg.getString(0) + "sig_img" + curBillImg.getString(1));
            }

            ArrayList<String> stringArrayList = new ArrayList<String>();
            for (int j = 0; j < mylistimagename.size(); j++) {

                stringArrayList.add(Environment.getExternalStorageDirectory() + "/MBC/" + mylistimagename.get(j)); //add to arraylist
            }
            String[] files = stringArrayList.toArray(new String[stringArrayList.size()]);
            String[] file = {Zip, signaturePathDes, photoPathDes};

            zipFolder(ZipCopyPath, ZipDesPath);
            GSBilling.getInstance().setFinalZipName(ZipDesPathdup);

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                // Set your file path here
//                System.out.println("FILENAME IS1 "+GSBilling.getInstance().getFinalZipName());
                FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip");
                Logger.e(getApplicationContext(), "SLPrintAct", "FILENAME IS12 " + fstrm);

                // Set your server page url (and the file title/description)

//                HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi_Testing/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                HttpFileUpload hfu = new HttpFileUpload("http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");

                // Log.e(getApplicationContext(), "http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles", "" + GSBilling.getInstance().getFinalZipName()+".zip");
                Logger.e(getApplicationContext(), "SLPrintAct", "going out " + GSBilling.getInstance().getFinalZipName() + ".zip");
                int status = hfu.Send_Now(fstrm);
                if (status != 200) {
//                    succsess = "1";
                    DuplicateAmigoPrinting.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();
                            Toast.makeText(DuplicateAmigoPrinting.this, "Internaly Stored Due to No Connectivity", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(DuplicateAmigoPrinting.this, BillingtypesActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("printtype", print_type);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);

                        }
                    });

                } else {
//                    succsess = "0";
                    dbHelper2 = new DB(getApplicationContext());
                    SD2 = dbHelper2.getWritableDatabase();

//                        String updatequer = "UPDATE  TBL_BILLING  SET Upload_Flag = 'Y' WHERE  Cons_Number = '" + curBillselect.getString(0) + "' and  Bill_Month='" + curBillselect.getString(5) + "'";
                    String updatequer = "UPDATE  TBL_BILLING  SET Upload_Flag = 'Y'";
                    Cursor curBillupdate = SD2.rawQuery(updatequer, null);
                    if (curBillupdate != null && curBillupdate.moveToFirst()) {
                        Logger.e(getApplicationContext(), "SLPrintAct", "Update Success");
                    }

                    DuplicateAmigoPrinting.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();
                            Toast.makeText(DuplicateAmigoPrinting.this, " Successfully Uploaded to Server ", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(DuplicateAmigoPrinting.this, BillingtypesActivity.class);
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
            Intent intent = new Intent(DuplicateAmigoPrinting.this, BillingtypesActivity.class);
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
        Logger.e(getApplicationContext(), "SLPrintAct", "DeleteRecursive DELETEPREVIOUS TOP" + dir.getPath());
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                File temp = new File(dir, children[i]);
                if (temp.isDirectory()) {
                    Logger.e(getApplicationContext(), "SLPrintAct", "DeleteRecursive Recursive Call" + temp.getPath());
                    DeleteRecursive(temp);
                } else {
                    Logger.e(getApplicationContext(), "SLPrintAct", "DeleteRecursive Delete File" + temp.getPath());
                    boolean b = temp.delete();
                    if (b == false) {
                        Logger.e(getApplicationContext(), "SLPrintAct", "DeleteRecursive DELETE FAIL");
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

    public void getTableBillingData(){

        String updatequer = "Select * from  TBL_BILLING  where Upload_Flag = 'Y'";
        Cursor curBillupdate = SD2.rawQuery(updatequer, null);
        while(curBillupdate.moveToNext ()){

            System.out.println ("THIS IS the cursor "+curBillupdate.getString ( 14 ) );
        }
        if (curBillupdate != null && curBillupdate.moveToFirst()) {
            Logger.e(getApplicationContext(), "SLPrintAct", "Update Success");
        }

    }
}