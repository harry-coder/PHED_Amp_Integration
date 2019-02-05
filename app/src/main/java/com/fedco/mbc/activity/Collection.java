package com.fedco.mbc.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.amigos.DuplicateCollectionPrint;
import com.fedco.mbc.amigos.MainActivityReceiptPrint;
import com.fedco.mbc.authentication.PrinterSessionManager;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.bluetoothprinting.DuplicateCollPrint;
import com.fedco.mbc.bluetoothprinting.GlobalPool;
import com.fedco.mbc.collection.CollectiontypesActivity;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.model.Structcolmas;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.ConnectionDetector;
import com.fedco.mbc.utils.HttpFileUpload;
import com.fedco.mbc.utils.URLS;
import com.fedco.mbc.utils.UtilAppCommon;
import com.fedco.mbc.utils.Utility;

import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.HashMap;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by soubhagyarm on 13-05-2016.
 */
public class Collection extends AppCompatActivity implements LogoutListaner {

    private Boolean exit = false;
    Logger Log;
    final Context context = this;
    UtilAppCommon uac;
    Button btnBilling, btnUBT, btnReport, btnSummery, btnLRP, btnBulk, btnExit, btnBack, btnSession;
    LinearLayout Li;
    String Thermal_enable,radiovaluepre,radiovaluepos;
    SweetAlertDialog sDialog;
    ConnectionDetector connectionDetector;
    TelephonyManager telephonyManager;
    String codeIMEI;
    Boolean checkFlagNet = false;
    Dialog dialogAccount;
    String accountnum, pass;
    DB dbHelper2;
    SQLiteDatabase SD2;
    SQLiteDatabase SD, SD4;
    DB dbHelper, dbHelper4;
    int pendingcount = 150;
    Cursor curconsmasData;
    // Session Manager Class
    SessionManager session;
    PrinterSessionManager printsession;
    Handler handler;
    UtilAppCommon appCommon;
    long session_key;
    final boolean isEnabled = false;
    public static String METERNO;
    public static String ARREARS;
    DB databasehelper;
    //    String ZipCopyPath = Environment.getExternalStorageDirectory() + "/MBC/BulkUpload/";
    String ZipCopyPathSession = Environment.getExternalStorageDirectory() + "/MBC/Session/";
    public String ZipDesPathSession;
    String ZipDesPathSessiondup, ZipDesPathdup, prev_pref, unique_key, paymentType;
    int collectionPending;
    int collectionCount;
    int billingPending;
    RadioGroup rgPaymentType;
    TextView tvSessionID, tvUserID,tvWallet;
    public String lastSessionID;
    HashMap<String, String> map = new HashMap<>();
    final DecimalFormat numberFormat = new DecimalFormat("########0.00");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        try {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
            getSupportActionBar().setLogo(R.mipmap.logo);
            setTitle("SMARTPHED");
        } catch (NullPointerException npe) {
            Log.e(getApplicationContext(), "Billing Act", "ActionBar Throwing null Pointer", npe);
        }
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        ((GlobalPool)getApplication()).registerSessionListaner(this);
        ((GlobalPool)getApplication()).startUserSession();
        codeIMEI = telephonyManager.getDeviceId();

        final GSBilling gsBilling = new GSBilling();
        uac = new UtilAppCommon();

        Li =(LinearLayout)findViewById(R.id.Linear_wallet);
        tvWallet = (TextView)findViewById(R.id.user_id) ;
        btnBilling = (Button) findViewById(R.id.buttonBilling1);
        btnBilling.setText("  Collection");
        btnBilling.setTextSize(16f);

        btnUBT = (Button) findViewById(R.id.buttonBilling2);
        btnReport = (Button) findViewById(R.id.buttonBilling3);
        btnSummery = (Button) findViewById(R.id.buttonBilling4);
        btnBulk = (Button) findViewById(R.id.buttonBilling6);
        btnLRP = (Button) findViewById(R.id.buttonBilling5);
        btnSession = (Button) findViewById(R.id.buttonSession);
        tvSessionID = (TextView) findViewById(R.id.tv_session_id);

        tvUserID = (TextView) findViewById(R.id.tv_user_id);
        try {
            if(new CommonHttpConnection(getApplicationContext()).isConnectingToInternet()){
            if(GSBilling.getInstance().Agent.equalsIgnoreCase("0")){
                Li.setVisibility(View.INVISIBLE);
                tvWallet.setVisibility(View.INVISIBLE);
                tvUserID.setVisibility(View.INVISIBLE);
                tvUserID.setText(GSBilling.getInstance().Wallet);
            }else{
                Li.setVisibility(View.VISIBLE);
                tvWallet.setVisibility(View.VISIBLE);
                tvUserID.setVisibility(View.VISIBLE);
                tvUserID.setText(GSBilling.getInstance().Wallet);
            }
            }
            else{
                Toast.makeText(context, "Internet connection required", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(context, "Internet connection required", Toast.LENGTH_SHORT).show();
            sDialog = new SweetAlertDialog(Collection.this, SweetAlertDialog.ERROR_TYPE);
            sDialog.setTitleText("Connection");
            sDialog.setContentText("failed.Try again!");
            sDialog.show();
            sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismissWithAnimation();

                }
            });
        }

        System.out.println(GSBilling.getInstance().Wallet);
//        paymentType = GSBilling.getInstance().getPayType();

        btnLRP.setVisibility(View.VISIBLE);
        btnBulk.setVisibility(View.INVISIBLE);
        btnSession.setVisibility(View.VISIBLE);
        tvSessionID.setVisibility(View.INVISIBLE);

        paymentType = GSBilling.getInstance().getPayType();

        this.ZipDesPathSession = Environment.getExternalStorageDirectory() + "/MBC/" + uac.UniqueCode(getApplicationContext()) + GSBilling.getInstance().captureDatetime() + ".zip";
        this.ZipDesPathSessiondup = Environment.getExternalStorageDirectory() + "/MBC/" + uac.UniqueCode(getApplicationContext()) + GSBilling.getInstance().captureDatetime();


        session = new SessionManager(getApplicationContext());
        printsession = new PrinterSessionManager(getApplicationContext());
        databasehelper = new DB(getApplicationContext());
        SD = databasehelper.getWritableDatabase();
        appCommon = new UtilAppCommon();

        session_key = 100000;//appCommon.findSequence(getApplicationContext(), "SessionNo");
        Structcolmas.SESSION_KEY = String.valueOf(session_key);
        if (session_key == 100000l) {
            continueSessionKeyDefault(String.valueOf(session_key), uac.UniqueCode(context));
        }

        String divCode = "SELECT CENTER_NAME FROM TBL_BILLING_DIV_MASTER";
        String DIV_NAME = null;

        Cursor curDivCode = SD.rawQuery(divCode, null);

        if (curDivCode != null && curDivCode.moveToFirst()) {
            DIV_NAME = curDivCode.getString(0);
        }

        if (DIV_NAME != null && DIV_NAME.equalsIgnoreCase("A2")) {

            btnBilling.setEnabled(false);
//            btnUBT.setEnabled(false);
            btnReport.setEnabled(false);
            btnSummery.setEnabled(false);
            btnBulk.setEnabled(false);
            btnLRP.setEnabled(false);

            Toast.makeText(getApplicationContext(), " You are not authorized to use collection", Toast.LENGTH_SHORT).show();
        }
        showSession();
        btnBilling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dbHelper4 = new DB(getApplicationContext());
//                SD4 = dbHelper4.getWritableDatabase();
//                String pending = "SELECT CON_NO from 'TBL_COLMASTER_MP' WHERE Upload_Flag='N'";
//                final Cursor curPend = SD4.rawQuery(pending, null);
//                curPend.getCount();
//                // if(curPend.getCount()>0){
//                if (curPend.getCount() > pendingcount) {
//                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//                            .setTitleText("Records are Pending ?")
//                            .setContentText("More Than " + curPend.getCount() + " records are pending for upload!")
//                            .setCancelText("No,cancel plz!")
//                            .setConfirmText("OK!")
//                            .showCancelButton(true)
//                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                @Override
//                                public void onClick(SweetAlertDialog sDialog) {
//
//                                    Intent intent = new Intent(context, SDActivity.class);
//                                    startActivity(intent);
//                                    overridePendingTransition(R.anim.anim_slide_in_left,
//                                            R.anim.anim_slide_out_left);
//                                }
//                            })
//                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                @Override
//                                public void onClick(SweetAlertDialog sDialog) {
//                                    sDialog.cancel();
//                                }
//                            })
//                            .show();
//
//                } else {
//
//                    final Dialog dialogAccount = new Dialog(context, R.style.DialogeAppTheme);
//                    dialogAccount.setContentView(R.layout.customcol_dialoge_search);
//                    dialogAccount.setTitle("Account Search");
//                    // set the custom dialog components - text, image and button
//                    final EditText editTextAccno = (EditText) dialogAccount.findViewById(R.id.EditTextACC);
//                    editTextAccno.setHint("Enter Account Number");
//                    editTextAccno.setInputType(InputType.TYPE_CLASS_NUMBER);
//                    Button dialogButton = (Button) dialogAccount.findViewById(R.id.dialogButtonACCOK);
//                    // if button is clicked, close the custom dialog
//                    Button dHomeButton = (Button) dialogAccount.findViewById(R.id.HomeButton);
//                    dHomeButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            finish();
//                            overridePendingTransition(R.anim.anim_slide_in_right,
//                                    R.anim.anim_slide_out_right);
//
//                        }
//                    });
////                dHprintButton.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////
////                        dbHelper = new DB(getApplicationContext());
////                        SD = dbHelper.getWritableDatabase();
////
////                        /****RAW QUERY EXICUTION AND STORING IN CURSOR****/
////                        String ret = "SELECT * FROM TBL_COLLECTION WHERE CON_NO =" + accountnum;
//////                        String ret = "SELECT * FROM TBL_COLMASTER_MP WHERE CON_NO =" + accountnum+"AND (COL_DATE+COL_TIME) ORDERBY ";
////                        System.out.println("QUERYYYYY " + ret);
////                        //String ret = "SELECT Consumer_Number FROM TBL_CONSMAST WHERE Name='SAPHALA PARIDA'";
////                        Cursor curconsmasData = SD.rawQuery(ret, null);
////
////                        /****GETTING DATA FROM CURSOR WHEN CURSOR IS NOT NULL AND ON THE FORST INDEX****/
////                        if (curconsmasData != null && curconsmasData.moveToFirst()) {
////
//////                            uac = new UtilAppCommon();
////                            try {
////                                uac.copyResultsetToCollClass(curconsmasData);
////                            } catch (SQLException e) {
////                                e.printStackTrace();
////                            }
////                            /****INITIALISATION OF INTENT AND ADDING SENDING DATA TO NEXT SCREEN , STARING INTENT****/
////
////                            Intent intent = new Intent(getApplicationContext(), CollectionView.class);
//////                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//////                                        intent.putExtra("datalist", allist);
////                            startActivity(intent);
////                            overridePendingTransition(R.anim.anim_slide_in_left,
////                                    R.anim.anim_slide_out_left);
////                            //String temp_address = c.getString(3);
////                            //System.out.println(temp_address+" result of select Query");
////                        } else {
////                            dialogAccount.dismiss();
////                            Toast.makeText(context, "Account Number Not Found", Toast.LENGTH_LONG).show();
////                        }
//
////                    }
////                });
//                    /****DIALOUGE BOX BUTTON INTIALISATION AND EVENT ADDING****/
//                    dialogButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            try {
//                                accountnum = editTextAccno.getText().toString().trim();
//                            } catch (NullPointerException N) {
//                                N.printStackTrace();
//                            }
//
//                            if (accountnum.isEmpty()) {
//                                Toast.makeText(context, "Please Enter Account Number.", Toast.LENGTH_LONG).show();
//                            } else {
//
//                                dbHelper = new DB(getApplicationContext());
//                                SD = dbHelper.getWritableDatabase();
//
//                                /****RAW QUERY EXICUTION AND STORING IN CURSOR****/
//                                String ret = "SELECT * FROM TBL_COLLECTION WHERE CON_NO =" + accountnum.trim();
//
//                                Log.e(getApplicationContext(), "ColAct", "SelectQwery" + ret);
//
//                                curconsmasData = SD.rawQuery(ret, null);
//                                int getDupCount=chkDup(accountnum.trim());
//                                /****GETTING DATA FROM CURSOR WHEN CURSOR IS NOT NULL AND ON THE FORST INDEX****/
//                                if (curconsmasData != null && curconsmasData.moveToFirst()) {
//
//                                    if(getDupCount >0){
//                                        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//                                                .setTitleText("Collection is Already Done !")
//                                                .setContentText("Are you sure to collect again ?")
//                                                .setCancelText("No,cancel plz!")
//                                                .setConfirmText("Yes,plz!")
//                                                .showCancelButton(true)
//                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                                    @Override
//                                                    public void onClick(SweetAlertDialog sDialog) {
//                                                        try {
//                                                            uac.copyResultsetToCollClass(curconsmasData);
//                                                        } catch (SQLException e) {
//                                                            e.printStackTrace();
//                                                        }
//
//                                                        Intent intent = new Intent(getApplicationContext(), CollectionView.class);
//                                                        startActivity(intent);
//                                                        overridePendingTransition(R.anim.anim_slide_in_left,
//                                                                R.anim.anim_slide_out_left);
//                                                    }
//                                                })
//                                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                                    @Override
//                                                    public void onClick(SweetAlertDialog sDialog) {
//                                                        sDialog.cancel();
//                                                    }
//                                                })
//                                                .show();
//                                    }else{
//
//                                        try {
//                                            uac.copyResultsetToCollClass(curconsmasData);
//                                        } catch (SQLException e) {
//                                            e.printStackTrace();
//                                        }
//
//                                        Intent intent = new Intent(getApplicationContext(), CollectionView.class);
//                                        startActivity(intent);
//                                        overridePendingTransition(R.anim.anim_slide_in_left,
//                                                R.anim.anim_slide_out_left);
//                                    }
//
//                                } else {
//                                    dialogAccount.dismiss();
//                                    Toast.makeText(context, "Account Number Not Found", Toast.LENGTH_LONG).show();
//                                }
//
//                            }
//
//                        }
//                    });
//                    dialogAccount.show();
//
//                }

//                Intent intent = new Intent(getApplicationContext(), CollectiontypesActivity.class);
//                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                intent.putExtra("flowkey", "collection");
//                startActivity(intent);
//                overridePendingTransition(R.anim.anim_slide_in_left,
//                        R.anim.anim_slide_out_left);

                /****DIALOG BOX INITIALIZATION PROVIDING A LAYOUT TO DIALOUGE****/
                final Dialog dialogAccount = new Dialog(context,android.R.style.Theme_DeviceDefault_Light_NoActionBar);
                dialogAccount.setContentView(R.layout.custom_dialoge_search);
                dialogAccount.setTitle("Account Search");

                // set the custom dialog components - text, image and button
                final EditText editTextAccno = (EditText) dialogAccount.findViewById(R.id.EditTextACCno);
//                editTextAccno.setHint("Enter Account No./Meter No.");
//                editTextAccno.setInputType(InputType.TYPE_CLASS_NUMBER);
                RadioGroup rgPaymentType;
                rgPaymentType = (RadioGroup) dialogAccount.findViewById(R.id.RGMode);
                final RadioButton rpre,rpos;
                int maxlength =13;
//                editTextAccno.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxlength) { }});
                rpre =(RadioButton)dialogAccount.findViewById(R.id.RBpre) ;
                rpos =(RadioButton)dialogAccount.findViewById(R.id.RBpos) ;
                radiovaluepre =rpre.getText().toString();
                radiovaluepos =rpos.getText().toString();
                Button dialogButton = (Button) dialogAccount.findViewById(R.id.dialogButtonACCnoOK);
                Button dHomeButton = (Button) dialogAccount.findViewById(R.id.HomeButton);

                dHomeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        finish();
                        overridePendingTransition(R.anim.anim_slide_in_right,
                                R.anim.anim_slide_out_right);

                    }
                });
                // if button is clicked, close the custom dialog
                /****DIALOUGE BOX BUTTON INTIALISATION AND EVENT ADDING****/
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            METERNO = editTextAccno.getText().toString().trim();
                        } catch (NullPointerException N) {
                            N.printStackTrace();
                        }

                        if (METERNO.isEmpty()) {
                            if(radiovaluepre.equalsIgnoreCase("PREPAID")) {
                                Toast.makeText(context, "Please Enter Meter Number.", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(context, "Please Enter Customer Number.", Toast.LENGTH_LONG).show();
                            }

                        }
//                        else if((radiovaluepre.equalsIgnoreCase("PREPAID") && METERNO.length()<=10)||(radiovaluepre.equalsIgnoreCase("PREPAID") && METERNO.length()==12)){
//                            Toast.makeText(context, "not a valid meter no", Toast.LENGTH_LONG).show();
//                        }
                        else if(radiovaluepre.equalsIgnoreCase("POSTPAID") && METERNO.length() <= 11){
                            Toast.makeText(context, "not a valid account no", Toast.LENGTH_LONG).show();
                        }else  {
                            try {
                                if(new CommonHttpConnection(getApplicationContext()).isConnectingToInternet()){
                                    new LoadProfile().execute(METERNO);
                                }
                                else{
                                    Toast.makeText(context, "Internet connection required", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


//                            new LoadProfile().execute(METERNO);

//                            Log.e(getApplicationContext(), "BillTypeAct", "Consumer Num : " + accountnum);
//
//                            dbHelper = new DB(getApplicationContext());
//                            SD = dbHelper.getWritableDatabase();
//
//                            String retCOLSMAS = "SELECT * FROM TBL_COLLECTION WHERE CON_NO ='" + accountnum + "'";
//                            Cursor curconsmasData = SD.rawQuery(retCOLSMAS, null);
//
//                            String retCONMAS = "SELECT\n" +
//                                    "t3.DIV_NAME as DIV_NAME ,t2.Section_Name as SUB_DIV,t2.Section_Name as SECTION,IFNULL(t1.Cons_Number,0) as CON_No ,t2.Name as CON_NAME,t1.METER_DEF_FLAG as METER_INST_FLAG,IFNULL(t1.O_Current_Demand,0) as CUR_TOT_BILL,IFNULL(t1.O_Total_Bill,0)+IFNULL(t1.O_Surcharge,0) as AMNT_AFT_RBT_DATE,t1.cheque_due_date as RBT_DATE,IFNULL(t1.O_Total_Bill,0) as AMNT_BFR_RBT_DATE,t1.cash_due_date as DUE_DATE,t3.DIVISION_CODE as AMNT_AFT_DUE_DATE,t2.Section_Name as DIV_CODE,t2.Section_Name as SUB_DIV_CODE,t2.LOC_CD as SEC_CODE,IFNULL(t2.Section_Name,0) as MCP,t1.MOB_NO as MOB_NO,t2.MAIN_CONS_LNK_NO as IVRS_NO,t2.LOC_CD as LOC_CD,\n" +
//                                    "t2.BILL_MON as BILL_MON\n" +
//                                    " FROM TBL_CONSMAST t2 \n" +
//                                    "left join \n" +
//                                    "TBL_BILLING t1 \n" +
//                                    "on t2.Consumer_Number =t1.Cons_Number ,\n" +
//                                    "TBL_BILLING_DIV_MASTER t3 WHERE t2.Consumer_Number ='" + accountnum + "'";
//                            Cursor curcolmasData = SD.rawQuery(retCONMAS, null);
//
//                            if (curconsmasData != null && curconsmasData.moveToFirst()) {
//
//                                uac = new UtilAppCommon();
//                                try {
//                                    uac.copyResultsetToCollClass(curconsmasData);
//                                } catch (SQLException e) {
//                                    e.printStackTrace();
//                                }
//
//                                Intent intent = new Intent(getApplicationContext(), CollectionView.class);
//                                // intent.putExtra("datalist", allist);
//                                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(intent);
//                                overridePendingTransition(R.anim.anim_slide_in_left,
//                                        R.anim.anim_slide_out_left);
//
//                            } else {
//
//                                if (curcolmasData != null && curcolmasData.moveToFirst()) {
//
//                                    uac = new UtilAppCommon();
//                                    try {
//                                        uac.copyResultsetToCollClass(curcolmasData);
//                                    } catch (SQLException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    Intent intent = new Intent(getApplicationContext(), CollectionView.class);
//                                    // intent.putExtra("datalist", allist);
//                                    // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(intent);
//                                    overridePendingTransition(R.anim.anim_slide_in_left,
//                                            R.anim.anim_slide_out_left);
//
//                                } else {
//
//                                    dialogAccount.dismiss();
//                                    Toast.makeText(context, "Account Number Not Found", Toast.LENGTH_LONG).show();
//
//                                }
//
//                            }
//
                        }

                    }
                });

                rgPaymentType.setOnCheckedChangeListener(
                        new RadioGroup.OnCheckedChangeListener() {

                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                if (checkedId == R.id.RBpre) {
                                    int maxLength = 100;
                                    radiovaluepre =rpre.getText().toString();
                                    editTextAccno.setHint("Enter Meter No.");
                                    editTextAccno.setInputType(InputType.TYPE_CLASS_TEXT);
                                    editTextAccno.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength) { }});
                                    editTextAccno.setText("");
                                }else{
                                    int maxLength = 12;
                                    editTextAccno.setHint("Enter Customer No.");
                                    radiovaluepre  = rpos.getText().toString();
                                    editTextAccno.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    editTextAccno.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength) { }});
                                    editTextAccno.setText("");

                                }

                            }
                        }
                            );

                dialogAccount.show();

            }
        });

        btnUBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /****DIALOG BOX INITIALIZATION PROVIDING A LAYOUT TO DIALOUGE****/
                final Dialog dialogAccount = new Dialog(context, R.style.DialogeAppTheme);
                dialogAccount.setContentView(R.layout.custom_dialoge_password);
                dialogAccount.setTitle("Account Search");
                // set the custom dialog components - text, image and button
                final EditText editTextAccno = (EditText) dialogAccount.findViewById(R.id.EditTextACC);

                Button dialogButton = (Button) dialogAccount.findViewById(R.id.dialogButtonACCOK);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            pass = editTextAccno.getText().toString().trim();
                        } catch (NullPointerException N) {
                            N.printStackTrace();
                        }
                        if (pass.isEmpty()) {
                            Toast.makeText(context, "Please Enter Secure Password", Toast.LENGTH_LONG).show();
                        } else if (pass.equals("data@123")) {
                            Intent intent = new Intent(getApplicationContext(), SDActivity.class);
                            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("flowkey", "collection");
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);            //String temp_address = c.getString(3);
                        } else {
                            dialogAccount.dismiss();
                            Toast.makeText(context, " UnAuthorized Access ", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                dialogAccount.show();

            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), UnCollectedConsumerReportActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.anim_slide_in_left,
//                        R.anim.anim_slide_out_left);
//                /****SQLITE DATBASE ACCESS BY CALLING THE SQLITEDATABASE AND DB CLASS****/
//                dbHelper = new DB(getApplicationContext());
//                SD = dbHelper.getWritableDatabase();
//
//                /****RAW QUERY EXECUTIONS AND STORING IN CURSOR****/
//                String ret = "DELETE FROM TBL_COLMASTER_MP";
//                //String ret = "SELECT Consumer_Number FROM TBL_CONSMAST WHERE Name='SAPHALA PARIDA'";
//                Cursor curdlebill = SD.rawQuery(ret, null);
//
//                /****GETTING DATA FROM CURSOR WHEN CURSOR IS NOT NULL AND ON THE FORST INDEX****/
//                if (curdlebill!= null && curdlebill.moveToFirst()) {
//
////                    uac = new UtilAppCommon();
//                    //                        uac.copyResultsetToConmasClass(curdlebill);
//
//                    Toast.makeText(Collection.this, "NOTHING TO DELETE", Toast.LENGTH_SHORT).show();
//                    //Intent intent=new Intent(getApplicationContext(),CameraActivity.class);
//                    //startActivity(intent);
//                }
//                Toast.makeText(Collection.this, "DELETED", Toast.LENGTH_SHORT).show();

            }
        });

        btnSummery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Phed_summary_report.class);
                intent.putExtra("FLAG", "Collection");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);

                /****DIALOG BOX INITIALIZATION PROVIDING A LAYOUT TO DIALOUGE****/
//                final Dialog dialogAccount = new Dialog(context, R.style.DialogeAppTheme);
////                dialogAccount.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
////                dialogAccount.getWindow().getAttributes().windowAnimations = R.anim.anim_slide_out_left;
//
//                dialogAccount.setContentView(R.layout.custom_dialoge_password);
//                dialogAccount.setTitle("Account Search");
//                // set the custom dialog components - text, image and button
//                final EditText editTextAccno = (EditText) dialogAccount.findViewById(R.id.EditTextACC);
//                Button dialogButton = (Button) dialogAccount.findViewById(R.id.dialogButtonACCOK);
//                // if button is clicked, close the custom dialog
//
//                /****DIALOUGE BOX BUTTON INTIALISATION AND EVENT ADDING****/
//                dialogButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        try {
//                            pass = editTextAccno.getText().toString().trim();
//                        } catch (NullPointerException N) {
//                            N.printStackTrace();
//                        }
//
//                        if (pass.isEmpty()) {
//                            Toast.makeText(context, "Please Enter Account Number.", Toast.LENGTH_LONG).show();
//                        } else if(pass.equals("secure")) {
//                            System.out.println("getting account no." + editTextAccno.getText());
////                                NullPointerException edittextnull=null;
////                                log.trace("EditTextF INTENT AND ADDING SENDING DATA TO NEXT SCREEN , STARING INTENT****/
//                            Intent intent = new Intent(getApplicationContext(), Summery.class);
//                            intent.putExtra("FLAG","COl");
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                    R.anim.anim_slide_out_left);
//                            //System.out.println(temp_address+" result of select Query");
//                        } else {
//                            dialogAccount.dismiss();
//                            Toast.makeText(context, " UnAuthorized Access ", Toast.LENGTH_LONG).show();
//                        }
//
//                    }
//                });
//                dialogAccount.show();


            }
        });
        btnLRP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(Collection.this, "GGGGGG", Toast.LENGTH_SHORT).show();

//                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//                        .setTitleText("Are You Sure To Print ?")
//                        .setContentText("Last Collected Receipt Will be Printed")
//                        .setCancelText("Cancel")
//                        .setConfirmText("OK")
//                        .showCancelButton(true)
//                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sDialog) {
                try {
                    dbHelper = new DB(getApplicationContext());
                    SD = dbHelper.getWritableDatabase();
//                  String qweryDup = "SELECT * FROM TBL_COLMASTER_MP  WHERE REC_ID = (SELECT MAX(REC_ID)  FROM TBL_COLMASTER_MP)";
                    String qweryDup = "SELECT * FROM TBL_COLMASTER_MP ORDER BY MAN_RECP_NO DESC LIMIT 1";
                    Cursor curDup = SD.rawQuery(qweryDup, null);

                    StringBuilder sb = new StringBuilder(qweryDup);
                    Log.e(context, "COLMAS ", "query:" + sb.toString());

                    if (curDup != null && curDup.moveToFirst()) {
                        System.out.println("hy-------" + curDup.getString(3));
                        try {
                            uac.copyFinalCol(curDup);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
                    }

                    String qweryCheckDup = "SELECT * FROM TBL_COLMASTER_MP ORDER BY MAN_RECP_NO DESC LIMIT 1";
//                    String qweryCheckDup = "SELECT * FROM TBL_COLLECTION WHERE CON_NO = " + curDup.getString(36);
                    Cursor curCheckDup = SD.rawQuery(qweryCheckDup, null);

                    if (curCheckDup != null && curCheckDup.moveToFirst()) {
                        try {
                            uac.copDupCollection(curCheckDup);
                            if (Structcolmas.PYMNT_MODE.equalsIgnoreCase("C")) {
                                paymentType = "CASH";
                            } else if (Structcolmas.PYMNT_MODE.equalsIgnoreCase("Q")) {
                                paymentType = "CHEQUE";
                            } else if (Structcolmas.PYMNT_MODE.equalsIgnoreCase("D")) {
                                paymentType = "POS";
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
                    }

                    dbHelper = new DB(Collection.this);
                    SD = dbHelper.getWritableDatabase();

                    String strPref = "SELECT PRINTER_PREF FROM USER_MASTER";
                    android.util.Log.e("Sequence", "update " + strPref);

                    Cursor getPref = SD.rawQuery(strPref, null);

                    if (getPref != null && getPref.moveToFirst()) {
                        prev_pref = getPref.getString(0);
                    }
                    if (paymentType != null && paymentType.equalsIgnoreCase("CASH")) {
                        if (prev_pref.equalsIgnoreCase("0_0_0") || prev_pref.equalsIgnoreCase("0_0_1")) {//IMP_AMI
                            Intent intent = new Intent(Collection.this, DuplicateCollectionPrint.class);
                            GSBilling.getInstance().setPayType("CASH");
                            // intent.putExtra("payType",typePay);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);
                        } else if (prev_pref.equalsIgnoreCase("0_1_0") || prev_pref.equalsIgnoreCase("0_1_1")) {//IMP_REG
                            Toast.makeText(Collection.this, "Under process", Toast.LENGTH_SHORT).show();
                        } else if (prev_pref.equalsIgnoreCase("0_2_0") || prev_pref.equalsIgnoreCase("0_2_1")) {

                            Intent intent = new Intent(Collection.this, DuplicateCollPrint.class);
                            GSBilling.getInstance().setPayType("CASH");
                            // intent.putExtra("payType",typePay);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);

                        } else {
                            Toast.makeText(Collection.this, "Unable to find Printer", Toast.LENGTH_SHORT).show();
                        }
                    } else if (paymentType != null && paymentType.equalsIgnoreCase("CHEQUE")) {
                        if (prev_pref.equalsIgnoreCase("0_0_0") || prev_pref.equalsIgnoreCase("0_0_1")) {//IMP_AMI
                            Intent intent = new Intent(Collection.this, DuplicateCollectionPrint.class);
                            GSBilling.getInstance().setPayType("CHEQUE");
                            // intent.putExtra("payType",typePay);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);
                        } else if (prev_pref.equalsIgnoreCase("0_1_0") || prev_pref.equalsIgnoreCase("0_1_1")) {//IMP_REG
                            Toast.makeText(Collection.this, "Under process", Toast.LENGTH_SHORT).show();
                        } else if (prev_pref.equalsIgnoreCase("0_2_0") || prev_pref.equalsIgnoreCase("0_2_1")) {

                            Intent intent = new Intent(Collection.this, DuplicateCollPrint.class);
                            GSBilling.getInstance().setPayType("CHEQUE");
                            // intent.putExtra("payType",typePay);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);

                        } else {
                            Toast.makeText(Collection.this, "Unable to find Printer", Toast.LENGTH_SHORT).show();
                        }
                    } else if (paymentType != null && paymentType.equalsIgnoreCase("POS")) {
                        if (prev_pref.equalsIgnoreCase("0_0_0") || prev_pref.equalsIgnoreCase("0_0_1")) {//IMP_AMI
                            Intent intent = new Intent(Collection.this, DuplicateCollectionPrint.class);
                            GSBilling.getInstance().setPayType("POS");
                            // intent.putExtra("payType",typePay);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);
                        } else if (prev_pref.equalsIgnoreCase("0_1_0") || prev_pref.equalsIgnoreCase("0_1_1")) {//IMP_REG
                            Toast.makeText(Collection.this, "Under process", Toast.LENGTH_SHORT).show();
                        } else if (prev_pref.equalsIgnoreCase("0_2_0") || prev_pref.equalsIgnoreCase("0_2_1")) {

                            Intent intent = new Intent(Collection.this, DuplicateCollPrint.class);
                            GSBilling.getInstance().setPayType("POS");
                            // intent.putExtra("payType",typePay);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);

                        } else {
                            Toast.makeText(Collection.this, "Unable to find Printer", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to find Payment Mode / Download Collected Data", Toast.LENGTH_SHORT).show();
                    }
                } catch (IndexOutOfBoundsException e) {
                    Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
//                                    sDialog.dismiss();
                    e.printStackTrace();
                }
//                            }
//                        })
//                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sDialog) {
//                                sDialog.cancel();
            }
//                        })
//                        .show();
//            }
        });
        btnSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                checkFlagNet = false;
//                final boolean isEnabled = Settings.System.getInt(Collection.this.getContentResolver(),
//                        Settings.System.AIRPLANE_MODE_ON, 0) == 1;
//
//                checkPendingRecord();
//
//
//                if (Utility.isConnectingToInternet() || isMobileDataEnabled()
//                        ) {
//
//                    if (!Utility.isAirplaneModeOn(getApplicationContext())) {
//                        if (collectionPending > 0) {
//                            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//                                    .setTitleText("Uploads are pending")
//                                    .setContentText("Unable to end session, please upload ")
//                                    .setCancelText("Cancle")
//                                    .setConfirmText("OK")
//                                    .showCancelButton(true)
//                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                        @Override
//                                        public void onClick(SweetAlertDialog sDialog) {
//
//                                            Intent intent = new Intent(getApplicationContext(), SDActivity.class);
//                                            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                            intent.putExtra("flowkey", "collection");
//                                            startActivity(intent);
//                                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                                    R.anim.anim_slide_out_left);
//                                            sDialog.dismiss();
//
//                                        }
//                                    })
//                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                        @Override
//                                        public void onClick(SweetAlertDialog sDialog) {
//                                            sDialog.cancel();
//                                        }
//                                    })
//                                    .show();
//                        } else {
//
//                            uac = new UtilAppCommon();
//                            session_key = uac.findSequence(getApplicationContext(), "SessionNo");
//                            unique_key = uac.UniqueCode(getApplicationContext());
//
//                            dbHelper = new DB(getApplicationContext());
//                            SD = dbHelper.getWritableDatabase();
//                            if (collectionCount > 0) {
//
//                                /****DIALOG BOX INITIALIZATION PROVIDING A LAYOUT TO DIALOUGE****/
//                                dialogAccount = new Dialog(context, R.style.DialogeAppTheme);
//                                dialogAccount.setContentView(R.layout.custom_dialoge_password);
//                                dialogAccount.setTitle("Password confirmation");
//
//                                final EditText editTextAccno = (EditText) dialogAccount.findViewById(R.id.EditTextACC);
//
//                                Button dHomeButton = (Button) dialogAccount.findViewById(R.id.HomeButton);
//                                dHomeButton.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//
//                                        Intent intent = new Intent(getApplicationContext(), Collection.class);
//                                        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                        startActivity(intent);
//                                        overridePendingTransition(R.anim.anim_slide_in_left,
//                                                R.anim.anim_slide_out_left);
//                                        dialogAccount.dismiss();
//
//                                    }
//                                });
//
//                                Button dialogButton = (Button) dialogAccount.findViewById(R.id.dialogButtonACCOK);
//                                dialogButton.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        try {
//                                            pass = editTextAccno.getText().toString().trim();
//                                        } catch (NullPointerException N) {
//                                            N.printStackTrace();
//                                        }
//
//                                        if (pass.isEmpty()) {
//                                            Toast.makeText(context, "please enter password", Toast.LENGTH_LONG).show();
//                                        } else {
//                                            new VerifyPassword().execute(pass);
//                                            dialogAccount.dismiss();
//                                        }
//
//                                    }
//                                });
//                                dialogAccount.show();
//                            } else {
//                                Toast.makeText(context, "No collection found", Toast.LENGTH_SHORT).show();
//                            }
//
//                        }
//                    } else {
//                        new SweetAlertDialog(Collection.this, SweetAlertDialog.WARNING_TYPE)
//                                .setTitleText("Connection Error")
//                                .setContentText("Internet connection not available")
//                                .setConfirmText("OK!")
//                                .show();
//                    }
//                } else {
//                    new SweetAlertDialog(Collection.this, SweetAlertDialog.WARNING_TYPE)
//                            .setTitleText("Connection Error")
//                            .setContentText("Internet connection not available")
//                            .setConfirmText("OK!")
//                            .show();
//                }
                dbHelper = new DB(getApplicationContext());
                SD = dbHelper.getWritableDatabase();
                Cursor cursorreport = SD.rawQuery("select ifnull(col_date,'NA')col_date,mr_name,sum(totalcashrecpt) as totalcashrecpt,sum(chequerecept) as chequerecept,sum(ddrecept) as ddrecept,sum(posrecept) as posrecept ,sum(totalcashamount ) as totalcashamount ,sum(totalchequeamount  ) as totalchequeamount  , sum(totalddamount ) as totalddamount , sum(totalposamount ) as totalposamount , sum ( totalcashamount + totalchequeamount + totalddamount + totalposamount) as Totalamount from ( select  * from ( SELECT col_date,mr_name,count(*) as totalcashrecpt,0 as chequerecept,0 as ddrecept,0 as posrecept,sum(amount) as totalcashamount ,sum(0) as totalchequeamount  ,sum(0) as  totalddamount ,sum(0) as totalposamount FROM  tbl_colmaster_mp where DATE(substr(col_date,7,4)||'-'||substr(col_date,4,2)||'-'||substr(col_date,1,2)) between  strftime('%Y-%m-%d', date('now'))  and strftime('%Y-%m-%d', date('now')) and pymnt_type='C' GROUP BY col_date,mr_name union SELECT col_date,mr_name,0 as totalcashrecpt,count(*) as chequerecept,0 as ddrecept,0 as posrecept ,sum(0) as totalcashamount ,sum(amount) as totalchequeamount  ,sum(0) as  totalddamount ,sum(0) as totalposamount FROM  tbl_colmaster_mp where DATE(substr(col_date,7,4)||'-'||substr(col_date,4,2)||'-'||substr(col_date,1,2)) between  strftime('%Y-%m-%d', date('now'))  and strftime('%Y-%m-%d', date('now')) and pymnt_type='Q' GROUP BY col_date,mr_name union SELECT col_date,mr_name,0 as totalcashrecpt,0 as chequerecept,count(*) as ddrecept,0 as posrecept ,sum(0) as totalcashamount ,sum(0) as totalchequeamount  ,sum(amount) as  totalddamount ,sum(0) as totalposamount FROM  tbl_colmaster_mp where DATE(substr(col_date,7,4)||'-'||substr(col_date,4,2)||'-'||substr(col_date,1,2)) between  strftime('%Y-%m-%d', date('now'))  and strftime('%Y-%m-%d', date('now')) and pymnt_type='D' GROUP BY col_date,mr_name union SELECT col_date,mr_name,0 as totalcashrecpt,0 as chequerecept,0 as ddrecept,count(*) as posrecept ,sum(0) as totalcashamount ,sum(0) as totalchequeamount  ,sum(0) as  totalddamount ,sum(amount) as totalposamount FROM  tbl_colmaster_mp where DATE(substr(col_date,7,4)||'-'||substr(col_date,4,2)||'-'||substr(col_date,1,2)) between  strftime('%Y-%m-%d', date('now'))  and strftime('%Y-%m-%d', date('now')) and pymnt_type='P' GROUP BY col_date,mr_name )) ",null);

                try{
                    String _todayrecpt = null,_date = null,_name = null,_cash = null,_cheque = null,_dd = null,_pos = null,_cashamount = null,_chequeamount = null,_ddamount = null,_posamount = null,_totalamount=null;
                    if (cursorreport.getCount () > 0 && cursorreport.moveToFirst()&&!cursorreport.getString(0).equals("NA")) {

                        do {

                            _date = cursorreport.getString(0);
                            _name = cursorreport.getString(1);
                            _cash = cursorreport.getString(2);
                            _cashamount = cursorreport.getString(6);
                            _cheque = cursorreport.getString(3);
                            _chequeamount = cursorreport.getString(7);
                            _dd = cursorreport.getString(4);
                            _ddamount = cursorreport.getString(8);
                            _pos = cursorreport.getString(5);
                            _posamount = cursorreport.getString(9);
                            _totalamount = cursorreport.getString(10);

                            Structcolmas.COL_DATE = _date;
                            Structcolmas.MR_NAME=_name;
                            Structcolmas.CASHCOUNT =_cash;
                            Structcolmas.CHEQUECOUNT = _cheque;
                            Structcolmas.DDCOUNT = _dd;
                            Structcolmas.POSCOUNT = _pos;
                            Structcolmas.CASHAMOUNT = numberFormat.format(Double.parseDouble(_cashamount.toString()));
                            Structcolmas.CHEQUEAMOUNT = numberFormat.format(Double.parseDouble(_chequeamount.toString()));
                            Structcolmas.DDAMOUNT = numberFormat.format(Double.parseDouble(_ddamount.toString()));
                            Structcolmas.POSAMOUNT =numberFormat.format(Double.parseDouble(_posamount.toString()));
                            Structcolmas.TOTALAMOUNT =numberFormat.format(Double.parseDouble(_totalamount.toString()));


                        } while (cursorreport.moveToNext());
                        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View customView =null;
                        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
                        switch (screenSize) {
                            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                                customView = layoutInflater.inflate(R.layout.receipt_dialogue, null);
                                break;
                            case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                                customView = layoutInflater.inflate(R.layout.receipt_dialogue, null);
                                break;
                            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                                customView = layoutInflater.inflate(R.layout.receipt_dialogue, null);
                                break;
                            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                                customView = layoutInflater.inflate(R.layout.receipt_dialogue, null);
                                break;
                            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                                customView = layoutInflater.inflate(R.layout.receipt_dialogue, null);
                                break;
                            default:
                                customView = layoutInflater.inflate(R.layout.receipt_dialogue, null);
                        }
                        Button btnno =(Button)customView.findViewById(R.id.btn_no);
                        Button btnyes =(Button)customView.findViewById(R.id.btn_yes);
                        AlertDialog.Builder alert = new AlertDialog.Builder(Collection.this);
                        // this is set the view from XML inside AlertDialog
                        alert.setView(customView);
                        final AlertDialog dialog = alert.create();
                        dialog.show();
                        btnno.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialog.cancel();
                            }
                        });
                        btnyes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Collection.this, MainActivityReceiptPrint.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left);



                            }
                        });
                    }else
                    {
                        Toast.makeText(getApplicationContext(),"no data found",Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    e.getStackTrace();

                }


//                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View customView =null;
//                int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
//                switch (screenSize) {
//                    case Configuration.SCREENLAYOUT_SIZE_XLARGE:
//                        customView = layoutInflater.inflate(R.layout.receipt_dialogue, null);
//                        break;
//                    case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
//                        customView = layoutInflater.inflate(R.layout.receipt_dialogue, null);
//                        break;
//                    case Configuration.SCREENLAYOUT_SIZE_LARGE:
//                        customView = layoutInflater.inflate(R.layout.receipt_dialogue, null);
//                        break;
//                    case Configuration.SCREENLAYOUT_SIZE_NORMAL:
//                        customView = layoutInflater.inflate(R.layout.receipt_dialogue, null);
//                        break;
//                    case Configuration.SCREENLAYOUT_SIZE_SMALL:
//                        customView = layoutInflater.inflate(R.layout.receipt_dialogue, null);
//                        break;
//                    default:
//                        customView = layoutInflater.inflate(R.layout.receipt_dialogue, null);
//                }
//                Button btnno =(Button)customView.findViewById(R.id.btn_no);
//                Button btnyes =(Button)customView.findViewById(R.id.btn_yes);
//                AlertDialog.Builder alert = new AlertDialog.Builder(Collection.this);
//                // this is set the view from XML inside AlertDialog
//                alert.setView(customView);
//                final AlertDialog dialog = alert.create();
//                dialog.show();
//                btnno.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        dialog.cancel();
//                    }
//                });
//                btnyes.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(Collection.this, MainActivityReceiptPrint.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.anim_slide_in_left,
//                                R.anim.anim_slide_out_left);
//
//
//
//                    }
//                });
            }
        });

        btnBulk.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
//                if (isMobileDataEnabled()) {
//
//                    checkPendingRecord();
//                    if (collectionPending > 0) {
//                        new BulkTextFileCreate().execute();
//                    } else {
//                        Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
//                    }
//
//
//                } else {
//
//                    Toast.makeText(Collection.this, "Internally stored due to no connectivity", Toast.LENGTH_SHORT).show();
//
//
//                }
                if (Utility.isConnectingToInternet()) {
                    checkPendingRecord();
                    if (collectionPending > 0) {
                        new BulkTextFileCreate().execute();
                    } else {
                        Toast.makeText(context, "No Data Found", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please Try With Working Internet..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Called when the activity has become visible.
     */
    @Override
    protected void onResume() {
        super.onResume();
        databasehelper = new DB(context);
        SD = databasehelper.getWritableDatabase();
        String divCode = "SELECT CENTER_NAME FROM TBL_BILLING_DIV_MASTER";
        String DIV_NAME = null;
        Cursor curDivCode = SD.rawQuery(divCode, null);

        if (curDivCode != null && curDivCode.moveToFirst()) {

            DIV_NAME = curDivCode.getString(0);

        }

        if (DIV_NAME != null && DIV_NAME.equalsIgnoreCase("A2")) {

            btnBilling.setEnabled(false);
//            btnUBT.setEnabled(false);
            btnReport.setEnabled(false);
            btnSummery.setEnabled(false);
            btnBulk.setEnabled(false);
            btnLRP.setEnabled(false);

            Toast.makeText(getApplicationContext(), " You are not authorized to use collection", Toast.LENGTH_SHORT).show();

        }
        Log.e(getApplicationContext(), "ttt", "The onResume() event");
    }

    public void checkPendingRecord() {

        dbHelper2 = new DB(getApplicationContext());
        SD2 = dbHelper2.getWritableDatabase();

        String colrecord = "SELECT  CON_NO from 'TBL_COLMASTER_MP' where   SESSIONID='" + session_key + "'";
        String colpending = "SELECT  CON_NO from 'TBL_COLMASTER_MP' WHERE Upload_Flag='N'";
        final Cursor curColPend = SD2.rawQuery(colpending, null);
        final Cursor curColCount = SD2.rawQuery(colrecord, null);
        collectionPending = curColPend.getCount();
        collectionCount = curColCount.getCount();

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

    public int chkDup(String acc) {

        dbHelper = new DB(getApplicationContext());
        SD = dbHelper.getWritableDatabase();

        String chkDupQwer = "SELECT * FROM TBL_COLMASTER_MP  WHERE CON_NO =" + acc;
        Cursor curDup = SD.rawQuery(chkDupQwer, null);

        return curDup.getCount();
    }

    public void onBackPressed() {

        Intent intent = new Intent(Collection.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.billing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), "Configuration is Under Process", Toast.LENGTH_LONG).show();
                break;
            case R.id.previous:

                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are You Sure to Logout ?")
                        .setContentText("Cannot use this application Until Login")
                        .setCancelText("No,cancel plz!")
                        .setConfirmText("OK!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                LogoutUser();
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .show();

                break;
            case R.id.next:
                GSBilling.getInstance().setAappFlow("collection");
                Intent intent = new Intent(context, PasswordReset.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
                break;
            case android.R.id.home:

                finish();
                overridePendingTransition(R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right);

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void LogoutUser() {

        SD = databasehelper.getWritableDatabase();
        String delStr = "DELETE FROM USER_MASTER";
        SD.execSQL(delStr);
        session.logoutUser();
        this.finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        ((GlobalPool)getApplication()).onUserInteraction();
    }

    @Override
    public void userLogoutListaner() {
        finish();
        Intent intent=new Intent(Collection.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    /*--------------------- Billing Bulk Upload Text File Creation ----------------------------------*/
    private class BulkTextFileCreate extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            dbHelper2 = new DB(getApplicationContext());
            SD2 = dbHelper2.getWritableDatabase();

            String selquer = "SELECT * FROM TBL_COLMASTER_MP";//WHERE Upload_Flag='N'

            Cursor curColselect = SD2.rawQuery(selquer, null);
            ArrayList<String> mylist = new ArrayList<String>();

            if (curColselect != null && curColselect.moveToFirst()) {

                while (curColselect.isAfterLast() == false) {

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

                    curColselect.moveToNext();

                }
                generateNoteOnSD(getApplicationContext(), "colmobile.csv", mylist);
//                generatebckOnSD(getApplicationContext(), "collection_bulk.csv", mylist, "MBC/BulkUploadBck/");
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            sDialog.dismiss();
            new BulkUpload().execute();
        }

        @Override
        protected void onPreExecute() {
            sDialog = new SweetAlertDialog(Collection.this, SweetAlertDialog.PROGRESS_TYPE);
            sDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            sDialog.setTitleText("Loading");
            sDialog.setCancelable(false);
            sDialog.show();
        }
    }

    /*--------------------- Billing Bulk Upload to Server ----------------------------------*/
    private class BulkUpload extends AsyncTask<String, Void, Boolean> {
        private String ZipCopyPath = Environment.getExternalStorageDirectory() + "/MBC/BulkUploadBck/";
        private String ZipDesPath = Environment.getExternalStorageDirectory() + File.separator + "ColBulk" + "/" + "col_bulk_" + (new SimpleDateFormat("dd_MMM_yyyy").format(Calendar.getInstance().getTime()) + ".zip");

        public BulkUpload() {

        }

        @Override
        protected void onPreExecute() {
            sDialog = new SweetAlertDialog(Collection.this, SweetAlertDialog.PROGRESS_TYPE);
            sDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            sDialog.setTitleText("Loading");
            sDialog.setCancelable(false);
            sDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                zipFolder(ZipCopyPath, ZipDesPath);
                GSBilling.getInstance().setUploadColZipToServer(ZipDesPath);

                FileInputStream fstrm = new FileInputStream(GSBilling.getInstance().getUploadColZipToServer());
//                HttpFileUpload hfu = new HttpFileUpload("http://enservtest.fedco.co.in/MPSurvey/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                HttpFileUpload hfu = new HttpFileUpload("http://enservtest.fedco.co.in/MPSurvey/api/UploadFile/UploadFiles", "", ZipDesPath);
                //        HttpFileUpload hfu = new HttpFileUpload("http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles", "", ZipDesPath);
//                Log.e(getApplicationContext(), "SLPrintAct", "going out " + GSBilling.getInstance().getFinalZipName() + ".zip");

                int status = hfu.Send_Now(fstrm);
                if (status != 200) {
//                    succsess = "1";
                    Collection.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            sDialog.dismiss();
                            Toast.makeText(Collection.this, "Unable to upload try again ", Toast.LENGTH_LONG).show();

                        }
                    });
                    return false;
                } else {

                    Collection.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            sDialog.dismiss();
                            Toast.makeText(Collection.this, "uploaded successfully", Toast.LENGTH_LONG).show();

                        }
                    });
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean isFileUpload) {
            super.onPostExecute(isFileUpload);
            sDialog.dismiss();
            if (isFileUpload) {
                Toast.makeText(getApplicationContext(), "File uploaded successfully", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(), "Fail to uploaded file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void generateNoteOnSD(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "MBC/BulkUpload/");
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

    public static void zipFolder(String inputFolderPath, String outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outZipPath);
//            GSBilling.getInstance().setFinalZipName();
            ZipOutputStream zos = new ZipOutputStream(fos);
            File srcFile = new File(inputFolderPath);
            File[] files = srcFile.listFiles();

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

    void DeleteRecursive(File dir) {

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                File temp = new File(dir, children[i]);
                if (temp.isDirectory()) {

                    DeleteRecursive(temp);
                } else {

                    boolean b = temp.delete();
                    if (b == false) {

                    }
                }
            }

        }
        dir.delete();
    }

    public void generateNote(Context context, String sFileName, ArrayList sBody, String filepath) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), filepath);
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

    public void generatebckOnSD(Context context, String sFileName, ArrayList sBody, String Fileroot) {
        try {
//            File root = new File(Environment.getExternalStorageDirectory(), "MBC/BulkUpload/");
            File root = new File(Environment.getExternalStorageDirectory(), Fileroot);
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

    private void showProgressDialog() {
        if (sDialog == null) {
            sDialog = new SweetAlertDialog(Collection.this, SweetAlertDialog.PROGRESS_TYPE);
            sDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            sDialog.setTitleText("Loading");
            sDialog.setCancelable(false);
            sDialog.show();
        }
        sDialog.show();
    }

    private void dismissProgressDialog() {
        if (sDialog != null && sDialog.isShowing() ) {
            sDialog.dismiss();
            sDialog.cancel();
//            dialogAccount.dismiss();
        }
    }

    private void showSession() {
        uac = new UtilAppCommon();
        session_key = uac.findSequence(getApplicationContext(), "SessionNo");
//        String last_session_id = GSBilling.getInstance().getLastSessionID();
        unique_key = uac.UniqueCode(getApplicationContext());
//        tvSessionID.setText(" : " + last_session_id);
//        Toast.makeText(getApplicationContext(), "last Session ID" + last_session_id, Toast.LENGTH_SHORT).show();
//        tvSessionID.setText(" : " + session_key);
//        tvUserID.setText(" : " + unique_key);
        GSBilling.getInstance().setSessionKey(session_key);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        dismissProgressDialog();
        Debug.stopMethodTracing();
        super.onDestroy();
    }

    public void continueSessionKey(String SessionKey, String MR_ID) {
        String start_date;
        String end_date;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        start_date = dateformat.format(c.getTime());
        end_date = "0";
        DB dbHelper = new DB(getApplicationContext());
        long l = Long.parseLong(SessionKey.trim());
        dbHelper.insertSession(l + 1l, MR_ID, start_date, end_date);
        GSBilling.getInstance().setSessionKey(l + 1l);
    }

    public void continueSessionKeyDefault(String SessionKey, String MR_ID) {
        String start_date;
        String end_date;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        start_date = dateformat.format(c.getTime());
        end_date = "0";

        DB dbHelper = new DB(getApplicationContext());
        long l = Long.parseLong(SessionKey.trim());
        dbHelper.insertSession(l, MR_ID, start_date, end_date);
        GSBilling.getInstance().setSessionKey(l);
    }

    private class VerifyPassword extends AsyncTask<String, String, String> {
        SweetAlertDialog pDialog;
        String MRNME, MRID, getUID, getPass, getResponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();

        }

        @Override
        protected String doInBackground(String... params) {

            // getUID="select * from USER_MASTER limit 1 ";
            getUID = "SELECT EXISTS( SELECT 1  FROM TBL_PASSWORD_MASTER WHERE PASSWORD='" + params[0] + "')";
            Cursor curGetUser = SD.rawQuery(getUID, null);

            if (curGetUser != null && curGetUser.moveToFirst()) {
                Log.e(context, "MainAct", "USER_ID" + curGetUser.getString(0));
                getResponse = curGetUser.getString(0);
                //  Log.e(context,"MainAct","USER_Pass"+curGetUser.getString(1));
            } else if (pass.equalsIgnoreCase("session@123")) {
                getResponse = "1";
            }
            return getResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (Collection.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                return;
            }
            dismissProgressDialog();

            if (getResponse.equals("1")) {

                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("End Session ?")
                        .setContentText("are you sure to end!")
                        .setCancelText("NO")
                        .setConfirmText("YES")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {


                                if (Utility.isConnectingToInternet() || isMobileDataEnabled()
                                        ) {
                                    if (Utility.isAirplaneModeOn(getApplicationContext())) {

                                        sDialog.cancel();
                                        showSession();
                                        dialogAccount.dismiss();
                                        new SweetAlertDialog(Collection.this, SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText("Connection Error")
                                                .setContentText("Internet connection not available")
                                                .setConfirmText("OK!")
                                                .show();
                                    } else {

                                        sDialog.cancel();

                                        String end_date;
                                        Calendar c = Calendar.getInstance();
                                        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                        end_date = dateformat.format(c.getTime());

                                        dbHelper = new DB(getApplicationContext());
                                        SD = dbHelper.getWritableDatabase();
                                        dbHelper.updateSession(uac.UniqueCode(context), session_key, end_date);

                                        new SessionTextFileCreate().execute();

                                    }

                                    //  Toast.makeText(Collection.this, "SESSION TXT", Toast.LENGTH_SHORT).show();

                                } else {
                                    sDialog.cancel();
                                    showSession();
                                    dialogAccount.dismiss();
                                    new SweetAlertDialog(Collection.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Connection Error")
                                            .setContentText("Internet connection not available")
                                            .setConfirmText("OK!")
                                            .show();
                                    // Toast.makeText(Collection.this, "Session updated internally", Toast.LENGTH_SHORT).show();
                                }

//                                session_key = session_key + 1;
                                Structcolmas.SESSION_KEY = String.valueOf(session_key + 1);

                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                                dialogAccount.dismiss();
                            }
                        })
                        .show();
            } else {
                Toast.makeText(Collection.this, "You are not authenticated to use this feature", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private class SessionTextFileCreate extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            dbHelper2 = new DB(getApplicationContext());
            SD2 = dbHelper2.getWritableDatabase();

            String selquer = "SELECT * FROM TBL_SESSION_MASTER";//WHERE Upload_Flag='N'
            Cursor curColselect = SD2.rawQuery(selquer, null);
            String arrStr[] = null;
            ArrayList<String> mylist = new ArrayList<String>();

            if (curColselect != null && curColselect.moveToFirst()) {
                while (curColselect.isAfterLast() == false) {
                    mylist.add(curColselect.getString(0) + "}" + curColselect.getString(0) + "-" + curColselect.getString(1) + "}" + curColselect.getString(2) +
                            "}" + curColselect.getString(3) + "}" + curColselect.getString(4));

                    curColselect.moveToNext();
                }
                generateNote(getApplicationContext(), "sessionlog.csv", mylist, "MBC/Session/");
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            if (Collection.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                return;
            }
            dismissProgressDialog();
            showSession();
            new SessionUpload().execute();
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();

        }
    }

    /*--------------------- Billing Bulk Upload to Server ----------------------------------*/
    private class SessionUpload extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {


                FileInputStream fstrm = new FileInputStream(GSBilling.getInstance().getFinalZipName() + ".zip");
                HttpFileUpload hfu = new HttpFileUpload("http://enservtest.fedco.co.in/MPSurvey/UploadFile/LogSession", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                HttpFileUpload hfu = new HttpFileUpload("http://enservtest.fedco.co.in/MPSurvey/api/UploadFile/LogSessionMobile", "" + "/storage/emulated/0/MBC/testing", ".txt");
//                HttpFileUpload hfu = new HttpFileUpload("http://enservtest.fedco.co.in/MPSurvey/api/UploadFile/LogSessionMobile", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                Log.e(getApplicationContext(), "SLPrintAct", "http://enservtest.fedco.co.in/MPSurvey/api/UploadFile/LogSessionMobile" + GSBilling.getInstance().getFinalZipName() + ".zip");
                Log.e(getApplicationContext(), "SLPrintAct", "going out " + GSBilling.getInstance().getFinalZipName() + ".zip");

                int status = hfu.Send_Now(fstrm);
                if (status != 200) {

                    Collection.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (Collection.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                                return;
                            }
                            dismissProgressDialog();
                            Toast.makeText(Collection.this, "Unable to upload try again ", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Collection.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (Collection.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                                return;
                            }

                            //continueSessionKey(""+session_key,uac.UniqueCode(context));

                            dbHelper = new DB(getApplicationContext());
                            SD = dbHelper.getWritableDatabase();
                            dbHelper.insertSequence("SessionNo", session_key);

                            continueSessionKey("" + session_key, uac.UniqueCode(context));
                            showSession();
                            Toast.makeText(Collection.this, unique_key + "-" + (session_key - 1l) +
                                    " session ended", Toast.LENGTH_SHORT).show();

                            dismissProgressDialog();
                            Toast.makeText(Collection.this, "Uploaded successfully", Toast
                                    .LENGTH_LONG).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            if (Collection.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                return;
            }


            dismissProgressDialog();
            File prevFile = new File(ZipCopyPathSession);
            DeleteRecursive(prevFile);
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();

            zipFolder(ZipCopyPathSession, ZipDesPathSession);
            GSBilling.getInstance().setFinalZipName(ZipDesPathSessiondup);
        }
    }

    /*---------------------------Capture DeviceSpecified UID & PASS Online------------------------*/
    private class FetOnlineUserAuth extends AsyncTask<Void, Void, Wrapper> {
        Wrapper wrap;

        @SuppressLint("MissingPermission")
        @Override
        protected Wrapper doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String id = null;
            String line;

            String forecastJsonStr = null;

            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            codeIMEI = telephonyManager.getDeviceId();

            try {
                wrap = new Wrapper();
                URL url = new URL("http://enservmp.fedco.co.in/MPSurvey/api/Login/GenerateLicenceKey?ImeiNo=" + codeIMEI);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    return wrap;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return wrap;
                }

                forecastJsonStr = buffer.toString();
                JSONArray myListsAll = new JSONArray(forecastJsonStr);


                for (int i = 0; i < myListsAll.length(); i++) {
                    JSONObject jsonobject = (JSONObject) myListsAll.get(i);

                    wrap.uniqCode = jsonobject.optString("MR_LICENSE_KEY");
                }

                return wrap;

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        return wrap;
                    }
                }
            }
            return wrap;
        }

        @Override
        protected void onPostExecute(Wrapper s) {
            super.onPostExecute(s);

            if (s.uniqCode != null && !s.uniqCode.isEmpty()) {
                checkFlagNet = true;
            } else {
                new SweetAlertDialog(Collection.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Connection Error")
                        .setContentText("Internet connection not available")
                        .setConfirmText("OK!")
                        .show();
            }

        }
    }

    public class Wrapper {
        public String uniqCode;
        public String passCode;
        public String LSTMR;
        public Double verCode;
    }
    public boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {


            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {


            return false;
        }
        return false;
    }
    private class LoadProfile extends AsyncTask<String, String, String> {
      SweetAlertDialog sweetAlertDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            if(isMobileDataEnabled()) {
//                showProgressDialog();
            sweetAlertDialog = new SweetAlertDialog(Collection.this, SweetAlertDialog.PROGRESS_TYPE);
            sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            sweetAlertDialog.setTitleText("Loading");
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.show();
//            }else {
//                Toast.makeText(Collection.this, "internet is required", Toast.LENGTH_SHORT).show();
//            }

        }

        /**
         * getting Profile JSON
         * */
        protected String doInBackground(String... params) {

            String last_recept_no="";
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;


            try {

//to do fetch last receipotno from sqllite db collmast_mp table
                dbHelper = new DB(getApplicationContext());
                SD = dbHelper.getWritableDatabase();

                //String ReceiptnoCol = "SELECT RECIP_NO,max(COL_DT)  from tbl_colmaster_mp  where ( CON_NO='"+params[0]+"' or  METER_NO='"+params[0]+"')";
                String ReceiptnoCol = "SELECT ifnull(MAN_RECP_NO,0),max(COL_DT)  from tbl_colmaster_mp";

                Cursor todoCursor = SD.rawQuery(ReceiptnoCol, null);
                if (todoCursor != null && todoCursor.moveToFirst()) {
                    do {
                        last_recept_no = todoCursor.getString(0);
                    }
                    while (todoCursor.moveToNext());

                } else {
                    Toast.makeText(Collection.this, "No data Found", Toast.LENGTH_SHORT).show();
                }


                //String url=URLS.VersionCode.meterno + codeIMEI + "/" + params[0]+"/"+last_recept_no;
                String url=URLS.VersionCode.meterno + codeIMEI + "/" + params[0]+"/"+last_recept_no;

                // URL url = new URL(URLS.VersionCode.meterno + codeIMEI + "/" + params[0]+"/"+last_recept_no);

                Log.e(context, "MainAct", URLS.VersionCode.meterno + codeIMEI + "/" + params[0]);
                Log.e(context, "MainAct", "para1 " + params[0]);
                try {
                    if(new CommonHttpConnection(getApplicationContext()).isConnectingToInternet()) {
                        forecastJsonStr= new CommonHttpConnection(getApplicationContext()).GetHttpConnection(URLS.UserAccess.checkConnection, 60000);
                        if(forecastJsonStr.equals("OK")) {
                            forecastJsonStr = new CommonHttpConnection(getApplicationContext()).GetHttpConnection(url, 60000);
                        }else if(forecastJsonStr.equals("404 Not Found")){
                            return "Could not connect to server";
                        }
                        else {
                            return "Mobile data not available";
                        }

                         }
                }catch (Exception ex)
                {


                    System.out.println ("This is the exception "+ex.getMessage () );
                    return ex.toString();
//                    Toast.makeText(getApplicationContext(),ex.toString(),Toast.LENGTH_LONG).show();


                }
//                Log.e(context, "MainAct", "para1 " + params[1]);

                // Create the request to OpenWeatherMap, and open the connection
                /*urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
*/
//                    urlConnection.setConnectTimeout(5000);
                // Read the input stream into a String
                /*String id = null;
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return "123";
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return "123";
                }
                forecastJsonStr = buffer.toString();

                return forecastJsonStr;*/
            } catch (Exception e) {
                e.printStackTrace();
//                Toast.makeText(Collection.this, "connection time out try again", Toast.LENGTH_SHORT).show();
            } finally {
               /* if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(getApplicationContext(), "PlaceholderFragment", "Error closing stream", e);
                    }
                }*/
            }
//                return null;

            return forecastJsonStr;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            if (Collection.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                return;
            }
//            dismissProgressDialog();
            sweetAlertDialog.cancel();
            Log.e(context, "MainAct", "PostString " + s);
            if (s == null)
            {
                sDialog = new SweetAlertDialog(Collection.this, SweetAlertDialog.ERROR_TYPE);
                sDialog.setTitleText("Error");
                sDialog.setContentText("Could not connect to server");
                sDialog.show();
                sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

                    }
                });

            }
           else if(!s.equals("null")&& !s.isEmpty()&& !s.equals("NULL")&&s.length()>15) {
              if(s.equals("Mobile data not available")){
                  sDialog = new SweetAlertDialog(Collection.this, SweetAlertDialog.ERROR_TYPE);
                  sDialog.setTitleText("Error");
                  sDialog.setContentText("Mobile data not available");
                  sDialog.show();
                  sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                      @Override
                      public void onClick(SweetAlertDialog sDialog) {
                          sDialog.dismissWithAnimation();

                      }
                  });
//                  Toast.makeText(Collection.this, "Mobile data not available", Toast.LENGTH_SHORT).show();
              }else if(s.equals("Could not connect to server")){
                  sDialog = new SweetAlertDialog(Collection.this, SweetAlertDialog.ERROR_TYPE);
                  sDialog.setTitleText("Error");
                  sDialog.setContentText("Could not connect to server");
                  sDialog.show();
                  sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                      @Override
                      public void onClick(SweetAlertDialog sDialog) {
                          sDialog.dismissWithAnimation();

                      }
                  });
              }
              else if( s.contains("timed out")){
                  sDialog = new SweetAlertDialog(Collection.this, SweetAlertDialog.ERROR_TYPE);
                  sDialog.setTitleText("Error");
                  sDialog.setContentText("Please try again");
                  sDialog.show();
                  sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                      @Override
                      public void onClick(SweetAlertDialog sDialog) {
                          sDialog.dismissWithAnimation();

                      }
                  });

              }else if(s.contains("error has occurred")){
                  sDialog = new SweetAlertDialog(Collection.this, SweetAlertDialog.ERROR_TYPE);
                  sDialog.setTitleText("Error");
                  sDialog.setContentText("Customer not found");
                  sDialog.show();
                  sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                      @Override
                      public void onClick(SweetAlertDialog sDialog) {
                          sDialog.dismissWithAnimation();

                      }
                  });

              }else if(s.contains("Message")){
                  sDialog = new SweetAlertDialog(Collection.this, SweetAlertDialog.ERROR_TYPE);
                  sDialog.setTitleText("Error");
                  sDialog.setContentText("Customer not found");
                  sDialog.show();
                  sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                      @Override
                      public void onClick(SweetAlertDialog sDialog) {
                          sDialog.dismissWithAnimation();

                      }
                  });
              }
                  else if (s.equals("Consumer Not Found")|| s.equals("404 Not Found"))
              {
//                  Toast.makeText(Collection.this, "consumer not found", Toast.LENGTH_SHORT).show();
                  sDialog = new SweetAlertDialog(Collection.this, SweetAlertDialog.ERROR_TYPE);
                  sDialog.setTitleText("Error");
                  sDialog.setContentText("Customer not found");
                  sDialog.show();
                  sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                      @Override
                      public void onClick(SweetAlertDialog sDialog) {
                          sDialog.dismissWithAnimation();

                      }
                  });
              }else if (s.contains("METER_INST_FLAG"))
              {
                  map.clear();
                  JSONArray ja = null;
                  try {
                      ja = new JSONArray(s);
                      for (int i = 0; i < ja.length(); i++) {
                          JSONObject jsonSection = ja.getJSONObject(i);
                          Log.e(context, "MainAct", "METERNO" + jsonSection.getString("METER_INST_FLAG"));
                          Log.e(context, "MainAct", "ARREARS" + jsonSection.getString("ARREAR"));
                          GSBilling.getInstance().MeterNo = jsonSection.getString("METER_INST_FLAG");
                          GSBilling.getInstance().ARREARS = jsonSection.getString("ARREAR");
                          GSBilling.getInstance().CON_TYPE = jsonSection.getString("CONS_TYPE");
                          GSBilling.getInstance().IBC = jsonSection.getString("DIV_NAME");
                          GSBilling.getInstance().BSC = jsonSection.getString("SEC_NAME");
                          GSBilling.getInstance().CONS_NAME = jsonSection.getString("CONS_NAME");
                          GSBilling.getInstance().ConsumerNO = jsonSection.getString("CONS_OLDCONSUMERNO");
                          GSBilling.getInstance().BILL_MONTH = jsonSection.getString("BILLMONTH");
                          GSBilling.getInstance().SEC_CODE = jsonSection.getString("SEC_CODE");
                          GSBilling.getInstance().Addresses = jsonSection.getString("ADDRESS");
                          GSBilling.getInstance().MRID = jsonSection.getString("MR_ID");
                          GSBilling.getInstance().MRNAME = jsonSection.getString("MR_NAME");
                          GSBilling.getInstance().MOBILENO = jsonSection.getString("MOB_NO");
                          GSBilling.getInstance().TARIFFCODE = jsonSection.getString("TARIFFCODE");
                          GSBilling.getInstance().TARIFF_RATE = jsonSection.getString("TARIFF_RATE");
                          GSBilling.getInstance().TARIFF_INDEX = jsonSection.getString("TARIFF_INDEX");
                          GSBilling.getInstance().FACTOR = jsonSection.getString("FACTOR");
                          GSBilling.getInstance().FACTORAMOUNT = jsonSection.getString("FACTORAMOUNT");
                          GSBilling.getInstance().CONSSTATRUS = jsonSection.getString("CONSSTATRUS");
                          GSBilling.getInstance().CON_REMARKS = jsonSection.getString("REMARKS");
                          System.out.println(GSBilling.getInstance().FACTOR);
                          System.out.println(GSBilling.getInstance().FACTORAMOUNT);
                          String PayDtl =jsonSection.getString("PayDtl");
                          JSONArray innerJSON = new JSONArray(PayDtl);
                          for(int j=0; j < innerJSON.length(); j++) {
                              JSONObject jsonobj = innerJSON.getJSONObject(j);
                              map.put(jsonobj.getString("HEAED"),jsonobj.getString("AMOUNT"));
                          }
                      }
                      if(GSBilling.getInstance().CONSSTATRUS.equals("0")) {
                          Intent intent = new Intent(getApplicationContext(), CollectionView.class);
                          intent.putExtra("HeaderDeatils", map);
                          startActivity(intent);
                          overridePendingTransition(R.anim.anim_slide_in_left,
                                  R.anim.anim_slide_out_left);
                      }else{
                          sDialog = new SweetAlertDialog(Collection.this, SweetAlertDialog.ERROR_TYPE);
                          sDialog.setTitleText("Message");
                          sDialog.setContentText(GSBilling.getInstance().CON_REMARKS);
                          sDialog.show();
                          sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                              @Override
                              public void onClick(SweetAlertDialog sDialog) {
                                  sDialog.dismissWithAnimation();

                              }
                          });
                      }
                  } catch (JSONException e) {
                      e.printStackTrace();
                  }
              }else if(s.contains("java.lang.Exception: sendto failed: EPIPE (Broken pipe)")){

                  Toast.makeText(getApplicationContext(),"Please try again",Toast.LENGTH_LONG).show();
              }else if(s.contains("java.lang.Exception")){

                  Toast.makeText(getApplicationContext(),"Please try again",Toast.LENGTH_LONG).show();
              }
          }else {
              sDialog = new SweetAlertDialog(Collection.this, SweetAlertDialog.ERROR_TYPE);
              sDialog.setTitleText("Error");
              sDialog.setContentText("InternetConnection Problem Try Again");
              sDialog.show();
              sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                  @Override
                  public void onClick(SweetAlertDialog sDialog) {
                      sDialog.dismissWithAnimation();

                  }
              });
//              Toast.makeText(Collection.this, "InternetConnection Problem Try Again", Toast.LENGTH_SHORT).show();
          }

        }

    }

}
