package com.fedco.mbc.activitysurvey;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.Home;
import com.fedco.mbc.activity.SDActivity;

import com.fedco.mbc.activitymetering.SummeryFilter;
import com.fedco.mbc.authentication.PrinterSessionManager;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.UtilAppCommon;

import java.lang.reflect.Method;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class Survey extends AppCompatActivity {

    Logger Log;
    final Context context = this;
    private Button btnSurvey,/*btnESurvey,*/ btnUBT, btnReport, btnSummery, btnBulkUpload;
    String pass;

    SessionManager session;
    PrinterSessionManager printsession;
    Handler handler;
    UtilAppCommon appCom;
    SQLiteDatabase SD,SD2;
    DB dbHelper,dbHelper2;
    //    int pendingcount = 300;
    int pendingcount = 150;

    String ZipCopyPath = Environment.getExternalStorageDirectory() + "/MBC/BulkUpload/";
    public String ZipDesPath;
    String ZipDesPathdup;
    SweetAlertDialog sDialog;
    int meterPending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        btnSurvey = (Button) findViewById(R.id.buttonSurvey);
        // btnESurvey = (Button) findViewById(R.id.buttonESurvey);
        btnUBT = (Button) findViewById(R.id.buttonSurveyUD);
        btnReport = (Button) findViewById(R.id.buttonSurveyReport);
        btnSummery = (Button) findViewById(R.id.buttonSurveySummery);
        btnBulkUpload = (Button) findViewById(R.id.buttonMSurveyBU);

        btnSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), SurveyDetails.class);
//                intent.putExtra("Filter","Metering");
                //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);

            }
        });
//        btnESurvey.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {

//                Intent intent = new Intent(getApplicationContext(), SummeryFilter.class);
//                intent.putExtra("Filter","Metering");
//                //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                overridePendingTransition(R.anim.anim_slide_in_left,
//                        R.anim.anim_slide_out_left);

//            }
//        });

        btnUBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /****DIALOG BOX INITIALIZATION PROVIDING A LAYOUT TO DIALOUGE****/
                final Dialog dialogAccount = new Dialog(context, R.style.DialogeAppTheme);

                dialogAccount.setContentView(R.layout.custom_dialoge_password);
                dialogAccount.setTitle("Account Search");

                final EditText editTextAccno = (EditText) dialogAccount.findViewById(R.id.EditTextACC);

                Button dHomeButton = (Button) dialogAccount.findViewById(R.id.HomeButton);
                dHomeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialogAccount.dismiss();
                        overridePendingTransition(R.anim.anim_slide_in_right,
                                R.anim.anim_slide_out_right);

                    }
                });

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
                            Toast.makeText(context, "Please Enter Account Number.", Toast.LENGTH_LONG).show();
                        } else if (pass.equals("data@123")) {

                            Intent intent = new Intent(getApplicationContext(), SDActivity.class);
                            intent.putExtra("flowkey", "survey");
                            //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);

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

                Intent intent = new Intent(getApplicationContext(), SurveyReports.class);
                intent.putExtra("Filter","Metering");
                //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);

            }
        });

        btnSummery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), SurveySummary.class);
                intent.putExtra("Filter","Summery");
                //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);

            }
        });

        btnBulkUpload .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isMobileDataEnabled()){

                    checkPendingRecord();
                    if( meterPending > 0 ){
//                        new Metering.BulkTextFileCreate().execute();
                    }
                    else {
                        Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
                    }


                }else{

                    Toast.makeText(Survey.this, "Internally stored due to no connectivity", Toast.LENGTH_SHORT).show();


                }

            }
        });

    }


    public void checkPendingRecord(){

        dbHelper2 = new DB(getApplicationContext());
        SD2 = dbHelper2.getWritableDatabase();
//        String billpending = "SELECT Cons_Number from 'TBL_BILLING''";
//        String colpending  = "SELECT  CON_NO from 'TBL_COLMASTER_MP'";
        String metpending  = "SELECT  CONSUMERNO from 'TBL_METERUPLOAD'";
//        final Cursor curBillPend = SD2.rawQuery(billpending, null);
//        final Cursor curColPend  = SD2.rawQuery(colpending, null);
        final Cursor curMetPend  = SD2.rawQuery(metpending, null);
//        billingPending      = curBillPend.getCount();
//        collectionPending   = curColPend.getCount();
        meterPending        = curMetPend.getCount();

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

        Intent intent = new Intent(Survey.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);

    }
}
