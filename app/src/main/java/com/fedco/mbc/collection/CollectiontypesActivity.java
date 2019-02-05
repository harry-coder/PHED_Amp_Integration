package com.fedco.mbc.collection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.InputType;
import android.util.EventLogTags;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.Collection;
import com.fedco.mbc.activity.CollectionView;
import com.fedco.mbc.activity.ConsumerSearchByAddressActivity;
import com.fedco.mbc.activity.ConsumerSearchByName_Activity;
import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.activity.MainActivity;
import com.fedco.mbc.activity.SDActivity;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.testing.TestMainActivity;
import com.fedco.mbc.utils.HttpFileUpload;
import com.fedco.mbc.utils.URLS;
import com.fedco.mbc.utils.UtilAppCommon;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CollectiontypesActivity extends AppCompatActivity {


    final Context context = this;
    UtilAppCommon uac;
    DB dbHelper, dbHelper2;
    SQLiteDatabase SD, SD2;
    SessionManager session;
    Logger Log;
    String codeIMEI;
    Button btnOne, btnTwo, btnThree, btnFour, btnFive, btnSix, btnSeven, btnEight, btnSession;

    String ZipCopyPath = Environment.getExternalStorageDirectory() + "/MBC/BulkUpload/";
    public String ZipDesPath;
    String ZipDesPathdup;

    public static String METERNO;
    public static String ARREARS;
    TelephonyManager telephonyManager;

    Long session_key;
    SweetAlertDialog sDialog;
    String name, unique_key;
    String accountnum;
    int billingPending;
    GSBilling gs;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billingtypes);
        try {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
            getSupportActionBar().setLogo(R.mipmap.logo);
            setTitle("Consumer search");
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
        codeIMEI = telephonyManager.getDeviceId();

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        name = session.retLicence();

        this.ZipDesPath = Environment.getExternalStorageDirectory() + "/MBC/" + name + GSBilling.getInstance().captureDatetime() + ".zip";
        this.ZipDesPathdup = Environment.getExternalStorageDirectory() + "/MBC/" + name + GSBilling.getInstance().captureDatetime();

        GSBilling.getInstance().clearData();

        /****BUTTON  INITIALISATION****/
        btnEight = (Button) findViewById(R.id.ButtonMeter);//mtr
        btnSeven = (Button) findViewById(R.id.ButtonOldacc);//ivrs
        btnSix = (Button) findViewById(R.id.Button6);
        btnFive = (Button) findViewById(R.id.Button5);
        btnFour = (Button) findViewById(R.id.Button4);
        btnThree = (Button) findViewById(R.id.ButtonRoute);
        btnTwo = (Button) findViewById(R.id.Button2);
        btnOne = (Button) findViewById(R.id.Button1);//acc
        btnSession = (Button) findViewById(R.id.ButtonSession);//acc

        btnThree.setEnabled(false);
        btnSix.setEnabled(false);

        btnEight.setVisibility(View.GONE);
        btnSix.setVisibility(View.GONE);
        btnTwo.setVisibility(View.GONE);
        btnFour.setVisibility(View.GONE);
        btnFive.setVisibility(View.GONE);
        btnSession.setVisibility(View.GONE);
        btnThree.setVisibility(View.GONE);
        //         btnFive.setEnabled(false);


        /****
         * ENTER ACCOUNT NUMBER*
         * ***/
        /****DIALOG BOX INITIALIZATION PROVIDING A LAYOUT TO DIALOUGE****/
        final Dialog dialogAccount = new Dialog(context, R.style.DialogeAppTheme);
        dialogAccount.setContentView(R.layout.custom_dialoge_search);
        dialogAccount.setTitle("Account Search");

        // set the custom dialog components - text, image and button
        final EditText editTextAccno = (EditText) dialogAccount.findViewById(R.id.EditTextACC);
        editTextAccno.setHint("Enter Account Number");
        editTextAccno.setInputType(InputType.TYPE_CLASS_NUMBER);
        Button dialogButton = (Button) dialogAccount.findViewById(R.id.dialogButtonACCOK);
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
                    Toast.makeText(context, "Please Enter Account Number.", Toast.LENGTH_LONG).show();
                } else  {
                    if(isInternetOn()){
                        new LoadProfile().execute(METERNO);
                    }
                    else{
                        Toast.makeText(context, "Internet connection required", Toast.LENGTH_SHORT).show();
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
        dialogAccount.show();


        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /****DIALOG BOX INITIALIZATION PROVIDING A LAYOUT TO DIALOUGE****/
                final Dialog dialogAccount = new Dialog(context, R.style.DialogeAppTheme);
                dialogAccount.setContentView(R.layout.custom_dialoge_search);
                dialogAccount.setTitle("Account Search");

                // set the custom dialog components - text, image and button
                final EditText editTextAccno = (EditText) dialogAccount.findViewById(R.id.EditTextACC);
                editTextAccno.setHint("Enter Account Number");
                editTextAccno.setInputType(InputType.TYPE_CLASS_NUMBER);
                Button dialogButton = (Button) dialogAccount.findViewById(R.id.dialogButtonACCOK);
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
                            Toast.makeText(context, "Please Enter Account Number.", Toast.LENGTH_LONG).show();
                        } else  {
                            if(isInternetOn()){
                                new LoadProfile().execute(METERNO);
                            }
                            else{
                                Toast.makeText(context, "Internet connection required", Toast.LENGTH_SHORT).show();
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
                dialogAccount.show();
            }
        });

        /****
         * ENTER METER NUMBER*
         * ***/
        btnEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /****DIALOG BOX INITIALIZATION PROVIDING A LAYOUT TO DIALOUGE****/
                final Dialog dialogAccount = new Dialog(context, R.style.DialogeAppTheme);
                dialogAccount.getWindow().getAttributes().windowAnimations = R.anim.anim_slide_out_left;
                dialogAccount.setContentView(R.layout.custom_dialoge_search);
                dialogAccount.setTitle("Meter Number Search");

                // set the custom dialog components - text, image and button
                final EditText editTextAccno = (EditText) dialogAccount.findViewById(R.id.EditTextACC);
                editTextAccno.setHint("Enter Meter Number");
                Button dialogButton = (Button) dialogAccount.findViewById(R.id.dialogButtonACCOK);
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
                            accountnum = editTextAccno.getText().toString().trim();
                        } catch (NullPointerException N) {
                            N.printStackTrace();
                        }

                        if (accountnum.isEmpty()) {
                            Toast.makeText(context, "Please Enter Meter Number.", Toast.LENGTH_LONG).show();
                        } else {

//                            Log.e(getApplicationContext(), "BillTypeAct", "Consumer Num : " + accountnum);
//
//                            dbHelper = new DB(getApplicationContext());
//                            SD = dbHelper.getWritableDatabase();
//
//                            String retCOLSMAS = "SELECT * FROM TBL_COLLECTION WHERE CON_NO ='" + accountnum + "'";
//                            Cursor curconsmasData = SD.rawQuery(retCOLSMAS, null);
//
//                            String retCONMAS = "SELECT\n" +
//                                    "t3.DIV_NAME,\n" +
//                                    "t2.Section_Name,\n" +
//                                    "t2.Section_Name,\n" +
//                                    "t1.Cons_Number,\n" +
//                                    "t2.Name,\n" +
//                                    "t1.METER_DEF_FLAG,\n" +
//                                    "t1.O_Total_Bill,\n" +
//                                    "t1.O_Surcharge,\n" +
//                                    "t1.Amnt_Paidafter_Rbt_Date ,\n" +
//                                    "t1.cheque_due_date,\n" +
//                                    "t1.Amnt_bPaid_on_Rbt_Date,\n" +
//                                    "t1.cash_due_date,\n" +
//                                    "t3.DIVISION_CODE,\n" +
//                                    "t2.Section_Name,\n" +
//                                    "t2.LOC_CD,\n" +
//                                    "t1.MOB_NO,\n" +
//                                    "t2.LOC_CD FROM \n" +
//                                    "TBL_BILLING t1 , \n" +
//                                    "TBL_CONSMAST t2,\n" +
//                                    "TBL_BILLING_DIV_MASTER t3\n" +
//                                    "WHERE \n" +
//                                    "t1.Cons_Number = t2.Consumer_Number \n" +
//                                    "AND  \n" +
//                                    "t1.Cons_Number ='" + accountnum + "'";
//
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
                            new LoadProfile().execute(METERNO);

                        }

                    }
                });
                dialogAccount.show();
            }
        });


        /****
         * ENTER IVRS NUMBER*
         * ***/
        btnSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialogAccount = new Dialog(context, R.style.DialogeAppTheme);
                dialogAccount.getWindow().getAttributes().windowAnimations = R.anim.anim_slide_out_left;
                dialogAccount.setContentView(R.layout.custom_dialoge_search);
                dialogAccount.setTitle("Meter No Search");

                // set the custom dialog components - text, image and button
                final EditText editTextAccno = (EditText) dialogAccount.findViewById(R.id.EditTextACC);
                editTextAccno.setHint("Enter Meter Number");
                editTextAccno.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                Button dialogButton = (Button) dialogAccount.findViewById(R.id.dialogButtonACCOK);
                Button dHomeButton = (Button) dialogAccount.findViewById(R.id.HomeButton);

                dHomeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        finish();
                        overridePendingTransition(R.anim.anim_slide_in_right,
                                R.anim.anim_slide_out_right);

                    }
                });

                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            accountnum = editTextAccno.getText().toString().trim();
                        } catch (NullPointerException N) {
                            N.printStackTrace();
                        }

                        if (accountnum.isEmpty()) {
                            Toast.makeText(context, "Please Enter Meter Number.", Toast.LENGTH_LONG).show();
                        } else {

//                            Log.e(getApplicationContext(), "BillTypeAct", "Consumer Num : " + accountnum);
//
//                            dbHelper = new DB(getApplicationContext());
//                            SD = dbHelper.getWritableDatabase();
//
//                            String retCOLSMAS = "SELECT * FROM TBL_COLLECTION WHERE IVRS_NO ='" + accountnum + "'";
//                            Cursor curconsmasData = SD.rawQuery(retCOLSMAS, null);
//
//                            String retCONMAS = "SELECT\n" +
//                                    "t3.DIV_NAME as DIV_NAME ,\n" +
//                                    "t2.Section_Name as SUB_DIV,\n" +
//                                    "t2.Section_Name as SECTION,\n" +
//                                    "IFNULL(t1.Cons_Number,0) as CON_No ,\n" +
//                                    "t2.Name as CON_NAME,\n" +
//                                    "t1.METER_DEF_FLAG as METER_INST_FLAG,\n" +
//                                    "IFNULL(t1.O_Current_Demand,0) as CUR_TOT_BILL,\n" +
//                                    "IFNULL(t1.O_Total_Bill,0)+IFNULL(t1.O_Surcharge,0) as AMNT_AFT_RBT_DATE,\n" +
//                                    "t1.cheque_due_date as RBT_DATE,\n" +
//                                    "IFNULL(t1.O_Total_Bill,0) as AMNT_BFR_RBT_DATE,\n" +
//                                    "t1.cash_due_date as DUE_DATE,\n" +
//                                    "t3.DIVISION_CODE as AMNT_AFT_DUE_DATE,\n" +
//                                    "t2.Section_Name as DIV_CODE,\n" +
//                                    "t2.Section_Name as SUB_DIV_CODE,\n" +
//                                    "t2.LOC_CD as SEC_CODE,\n" +
//                                    "IFNULL(t2.Section_Name,0) as MCP,\n" +
//                                    "t1.MOB_NO as MOB_NO,\n" +
//                                    "t2.MAIN_CONS_LNK_NO as IVRS_NO\n" +
//                                    " FROM\n" +
//                                    "TBL_CONSMAST t2 left join\n" +
//                                    "TBL_BILLING t1 on t2.MAIN_CONS_LNK_NO =t1.IVRS_NO ,\n" +
//                                    "TBL_BILLING_DIV_MASTER t3\n" +
//                                    "WHERE \n" +
//                                    "t1.IVRS_NO='" + accountnum + "'";
//
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
//                                    Toast.makeText(context, "Meter Number Not Found", Toast.LENGTH_LONG).show();
//
//                                }
//
//                            }
                            new LoadProfile().execute(METERNO);

                        }


                    }
                });
                dialogAccount.show();
            }
        });

        /****
         * SEARCH BY NAME*
         * ***/
        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ConsumerSearchByName_Activity.class);
                //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });

        /****
         * SEND MULTIPLE RECORDS TO SERVER *
         * ***/
        btnThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TestMainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);

            }
        });

        /****
         * SEARCH BY ADDRESS*
         * ***/
        btnFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ConsumerSearchByAddressActivity.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });

        /****
         * PRINT DUPLICATE BILL*
         * ***/

        btnFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isMobileDataEnabled()) {

                    checkPendingRecord();
                    if (billingPending > 0) {
                        new BulkTextFileCreate().execute();
                    } else {
                        Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
                    }


                } else {

                    Toast.makeText(CollectiontypesActivity.this, "Internally stored due to no connectivity", Toast.LENGTH_SHORT).show();


                }

            }
        });
        btnSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                -----------------------------Delete ALL--------------------------------
////                Intent intent = new Intent(getApplicationContext(),SqliteActivity.class);
////                startActivity(intent);
//
//                /****SQLITE DATBASE ACCESS BY CALLING THE SQLITEDATABASE AND DB CLASS****/
//                dbHelper = new DB(getApplicationContext());
//                SD = dbHelper.getWritableDatabase();
//
//                /****RAW QUERY EXECUTIONS AND STORING IN CURSOR****/
//                String ret = "DELETE FROM TBL_BILLING";
//                //String ret = "SELECT Consumer_Number FROM TBL_CONSMAST WHERE Name='SAPHALA PARIDA'";
//                Cursor curdlebill = SD.rawQuery(ret, null);
//
//                /****GETTING DATA FROM CURSOR WHEN CURSOR IS NOT NULL AND ON THE FORST INDEX****/
//                if (curdlebill!= null && curdlebill.moveToFirst()) {
//
////                    uac = new UtilAppCommon();
//                    //                        uac.copyResultsetToConmasClass(curdlebill);
//
//                    Toast.makeText(BillingtypesActivity.this, "NOTHING TO DELETE", Toast.LENGTH_SHORT).show();
//                    //Intent intent=new Intent(getApplicationContext(),CameraActivity.class);
//                //startActivity(intent);
//            }
//                Toast.makeText(BillingtypesActivity.this, "DELETED", Toast.LENGTH_SHORT).show();
            }
        });
        btnSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPendingRecord();
                if (billingPending > 0) {
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Uploads are pending")
                            .setContentText("Unable to end session, please upload ")
                            .setCancelText("Cancle")
                            .setConfirmText("OK")
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {

                                    Intent intent = new Intent(getApplicationContext(), SDActivity.class);
                                    // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("flowkey", "collection");
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.anim_slide_in_left,
                                            R.anim.anim_slide_out_left);

                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.cancel();
                                }
                            })
                            .show();
                } else {
                    uac= new UtilAppCommon();
                    session_key = uac.findSequence(getApplicationContext(), "SessionNo");
                    unique_key = uac.UniqueCode(getApplicationContext());

                    dbHelper = new DB(getApplicationContext());
                    SD = dbHelper.getWritableDatabase();


                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("End Session ?")
                            .setContentText("are you sure to end!")
                            .setCancelText("NO")
                            .setConfirmText("YES")
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {


                                    String end_date;
                                    Calendar c = Calendar.getInstance();
                                    SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
                                    end_date                    = dateformat.format(c.getTime());

                                    dbHelper = new DB(getApplicationContext());
                                    SD = dbHelper.getWritableDatabase();

                                    dbHelper.updateSession(uac.UniqueCode(context), session_key,end_date);
                                    dbHelper = new DB(getApplicationContext());
                                    SD = dbHelper.getWritableDatabase();
                                    dbHelper.insertSequence("SessionNo", session_key);

                                    if (isMobileDataEnabled()) {

                                        sDialog.cancel();
                                        new SessionTextFileCreate().execute();

//                                        Toast.makeText(CollectiontypesActivity.this, "SESSION TXT", Toast.LENGTH_SHORT).show();
                                    } else {
                                        sDialog.cancel();
                                        Toast.makeText(CollectiontypesActivity.this, "Session updated internally", Toast.LENGTH_SHORT).show();

                                    }

                                    Toast.makeText(CollectiontypesActivity.this, unique_key+"-"+session_key +" session ended", Toast.LENGTH_SHORT).show();

                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.cancel();
                                }
                            })
                            .show();



                }



            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.home:

                finish();
                overridePendingTransition(R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);

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


    public void onBackPressed() {

        Intent intent = new Intent(CollectiontypesActivity.this, Collection.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);

    }

    private void showProgressDialog() {
        if (sDialog == null) {
            sDialog = new SweetAlertDialog(CollectiontypesActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            sDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            sDialog.setTitleText("Loading");
            sDialog.setCancelable(false);
            sDialog.show();
        }
        sDialog.show();
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


    public void checkPendingRecord() {

        dbHelper2 = new DB(getApplicationContext());
        SD2 = dbHelper2.getWritableDatabase();

        String billpending = "SELECT Cons_Number from 'TBL_BILLING'";
        // String colpending  = "SELECT  CON_NO from 'TBL_COLMASTER_MP' WHERE Upload_Flag='N'";
        // String metpending  = "SELECT  CONSUMERNO from 'TBL_METERUPLOAD' WHERE UPLOADFLAG='N'";

        final Cursor curBillPend = SD2.rawQuery(billpending, null);
        // final Cursor curColPend  = SD3.rawQuery(colpending, null);
        // final Cursor curMetPend  = SD3.rawQuery(metpending, null);

        billingPending = curBillPend.getCount();
        // collectionPending   = curColPend.getCount();
        // meterPending        = curMetPend.getCount();

    }

    /*--------------------- Billing Bulk Upload Text File Creation ----------------------------------*/
    private class BulkTextFileCreate extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
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
                            "}" + curColselect.getString(27) + "}"+ curColselect.getString(28) + "}"+ curColselect.getString(29) +
                            "}"+ curColselect.getString(30) + "}"+ curColselect.getString(31) + "}"+ curColselect.getString(32) +
                            "}"+ curColselect.getString(33) + "}"+ curColselect.getString(34) + "}"+ curColselect.getString(35) +
                            "}"+ curColselect.getString(37) + "}");

                    curColselect.moveToNext();
                }
                generateNoteOnSD(getApplicationContext(), "collection.csv", mylist);

            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            if (CollectiontypesActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                return;
            }
            dismissProgressDialog();
            new BulkUpload().execute();


        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();

        }


    }

    /*--------------------- Billing Bulk Upload to Server ----------------------------------*/
    private class BulkUpload extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {


                FileInputStream fstrm = new FileInputStream(GSBilling.getInstance().getFinalZipName() + ".zip");
                HttpFileUpload hfu = new HttpFileUpload("http://enservtest.fedco.co.in/MPSurvey/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                Log.e(getApplicationContext(), "SLPrintAct", "going out " + GSBilling.getInstance().getFinalZipName() + ".zip");

                int status = hfu.Send_Now(fstrm);
                if (status != 200) {

                    CollectiontypesActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (CollectiontypesActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                                return;
                            }
                            dismissProgressDialog();
                            Toast.makeText(CollectiontypesActivity.this, "Unable to upload try again ", Toast.LENGTH_LONG).show();

                        }
                    });

                } else {

                    CollectiontypesActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (CollectiontypesActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                                return;
                            }
                            dismissProgressDialog();
                            Toast.makeText(CollectiontypesActivity.this, "uploaded successfully", Toast.LENGTH_LONG).show();

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

            if (CollectiontypesActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                return;
            }
            dismissProgressDialog();

            File prevFile = new File(ZipCopyPath);
            DeleteRecursive(prevFile);

        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();

            zipFolder(ZipCopyPath, ZipDesPath);
            GSBilling.getInstance().setFinalZipName(ZipDesPathdup);
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

    public void generateNote(Context context, String sFileName, ArrayList sBody,String filepath) {
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

    public static void zipFolder(String inputFolderPath, String outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outZipPath);
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

                    mylist.add(curColselect.getString(0) + "}" + curColselect.getString(0)+"-"+curColselect.getString(1) + "}" + curColselect.getString(2) +
                            "}" + curColselect.getString(3) + "}" + curColselect.getString(4) );

                    curColselect.moveToNext();
                }
                generateNote(getApplicationContext(), "sessionlog.csv", mylist,"MBC/Session/");

            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            if (CollectiontypesActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                return;
            }
            dismissProgressDialog();
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
                HttpFileUpload hfu = new HttpFileUpload("http://enservtest.fedco.co.in/MPSurvey/api/UploadFile/LogSessionMobile", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                Log.e(getApplicationContext(), "SLPrintAct", "going out " + GSBilling.getInstance().getFinalZipName() + ".zip");

                int status = hfu.Send_Now(fstrm);
                if (status != 200) {

                    CollectiontypesActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (CollectiontypesActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                                return;
                            }
                            dismissProgressDialog();
                            Toast.makeText(CollectiontypesActivity.this, "Unable to upload try again ", Toast.LENGTH_LONG).show();

                        }
                    });

                } else {

                    CollectiontypesActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (CollectiontypesActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                                return;
                            }
                            dismissProgressDialog();
                            Toast.makeText(CollectiontypesActivity.this, "uploaded successfully", Toast.LENGTH_LONG).show();

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

            if (CollectiontypesActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                return;
            }
            dismissProgressDialog();

            File prevFile = new File(ZipCopyPath);
            DeleteRecursive(prevFile);

        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();

            zipFolder(ZipCopyPath, ZipDesPath);
            GSBilling.getInstance().setFinalZipName(ZipDesPathdup);
        }





    }
    //        ------------for online data fetch -------------------------------

   private class LoadProfile extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(isMobileDataEnabled()) {
                showProgressDialog();
            }else {
                Toast.makeText(CollectiontypesActivity.this, "internet is required", Toast.LENGTH_SHORT).show();
            }

        }

        /**
         * getting Profile JSON
         * */
        protected String doInBackground(String... params) {


                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                // Will contain the raw JSON response as a string.
                String forecastJsonStr = null;


                try {


                    URL url = new URL(URLS.VersionCode.meterno + codeIMEI + "/" + params[0]);

                    Log.e(context, "MainAct", URLS.VersionCode.meterno + codeIMEI + "/" + params[0]);
                    Log.e(context, "MainAct", "para1 " + params[0]);
//                Log.e(context, "MainAct", "para1 " + params[1]);

                    // Create the request to OpenWeatherMap, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    String id = null;
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

                    return forecastJsonStr;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.v(getApplicationContext(), "MainAct : ", "Authenticate", e);
                    // If the code didn't successfully get the weather data, there's no point in attemping
                    // to parse it.
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(getApplicationContext(), "PlaceholderFragment", "Error closing stream", e);
                        }
                    }
                }
//                return null;

            return null;
        }
       @Override
       protected void onPostExecute(String s) {
           super.onPostExecute(s);
           if (CollectiontypesActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
               return;
           }
           dismissProgressDialog();
           Log.e(context, "MainAct", "PostString " + s);

           if (s != null && !s.isEmpty()) {

               JSONArray ja = null;
               try {
                   ja = new JSONArray(s);
                   for (int i = 0; i < ja.length(); i++) {

                       JSONObject jsonSection = ja.getJSONObject(i);

                       Log.e(context, "MainAct", "METERNO" + jsonSection.getString("METER_INST_FLAG"));
                       Log.e(context, "MainAct", "ARREARS" + jsonSection.getString("ARREAR"));

                     GSBilling.getInstance().MeterNo= jsonSection.getString("METER_INST_FLAG");
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
                       GSBilling.getInstance().MRNAME =jsonSection.getString("MR_NAME");
                       GSBilling.getInstance().MOBILENO = jsonSection.getString("MOB_NO");
                       GSBilling.getInstance().RecieptNo = jsonSection.getString("ReceiptNo");
                       GSBilling.getInstance().punit = jsonSection.getString("unitsActual");




                   }
               } catch (JSONException e) {
                   e.printStackTrace();
               }
               Intent intent = new Intent(getApplicationContext(), CollectionView.class);
               startActivity(intent);
               overridePendingTransition(R.anim.anim_slide_in_left,
                       R.anim.anim_slide_out_left);

           }

        }

       }


}