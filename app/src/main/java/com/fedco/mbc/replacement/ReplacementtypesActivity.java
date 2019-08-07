package com.fedco.mbc.replacement;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;

import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.service.FullUploadService;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.testing.TestMainActivity;
import com.fedco.mbc.utils.HttpFileUpload;
import com.fedco.mbc.utils.UtilAppCommon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ReplacementtypesActivity extends AppCompatActivity {


    final Context context = this;
    UtilAppCommon uac;
    DB dbHelper, dbHelper2;
    SQLiteDatabase SD, SD2;
    SessionManager session;
    Logger Log;
    Button btnOne, btnTwo, btnThree, btnFour, btnFive, btnSix, btnSeven, btnEight;

    String ZipCopyPath = Environment.getExternalStorageDirectory() + "/MBC/BulkUploadMeter/";
    public String ZipDesPath;
    String ZipDesPathdup;

    SweetAlertDialog sDialog;
    String name;
    String accountnum;
    int billingPending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billingtypes);
        try {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
            getSupportActionBar().setLogo(R.mipmap.logo);
            setTitle("Consumer search");
        } catch (NullPointerException npe) {
            Log.e(getApplicationContext(), "Replacement Act", "ActionBar Throwing null Pointer", npe);
        }

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        name = session.retLicence();

        this.ZipDesPath = Environment.getExternalStorageDirectory() + "/MBC/" + name + GSBilling.getInstance().captureDatetime() + ".zip";
        this.ZipDesPathdup = Environment.getExternalStorageDirectory() + "/MBC/" + name + GSBilling.getInstance().captureDatetime() ;

        GSBilling.getInstance().clearData();

        /****BUTTON  INITIALISATION****/
        btnEight    = (Button) findViewById(R.id.ButtonMeter);
        btnSeven    = (Button) findViewById(R.id.ButtonOldacc);
        btnSix      = (Button) findViewById(R.id.Button6);
        btnFive     = (Button) findViewById(R.id.Button5);
        btnFour     = (Button) findViewById(R.id.Button4);
        btnThree    = (Button) findViewById(R.id.ButtonRoute);
        btnTwo      = (Button) findViewById(R.id.Button2);
        btnOne      = (Button) findViewById(R.id.Button1);

        btnThree.setEnabled(false);
        btnSix.setEnabled(false);
        // btnFive.setEnabled(false);

        /****
         * ENTER ACCOUNT NUMBER*
         * ***/
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
                            accountnum = editTextAccno.getText().toString().trim();
                        } catch (NullPointerException N) {
                            N.printStackTrace();
                        }

                        if (accountnum.isEmpty()) {
                            Toast.makeText(context, "Please Enter Account Number.", Toast.LENGTH_LONG).show();
                        } else {

                            Log.e(getApplicationContext(), "BillTypeAct", "Consumer Num : " + accountnum);

                            dbHelper = new DB(getApplicationContext());
                            SD = dbHelper.getWritableDatabase();

//                            String ret = "SELECT * FROM TBL_CONSMAST WHERE Consumer_Number ='" + accountnum+ "'";
                            String ret = "SELECT * FROM TBL_COLLECTION WHERE CON_NO ='" + accountnum+ "'";
                            Cursor curconsmasData = SD.rawQuery(ret, null);

                            if (curconsmasData != null && curconsmasData.moveToFirst()) {

                                uac = new UtilAppCommon();
                                try {
                                    uac.copyResultsetToConmasClass(curconsmasData);
//                                    uac.copyResultsetToCollClass(curconsmasData);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                // intent.putExtra("datalist", allist);
                                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left);

                            } else {
                                dialogAccount.dismiss();
                                Toast.makeText(context, "Account Number Not Found", Toast.LENGTH_LONG).show();
                            }

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


                Intent intent = new Intent(getApplicationContext(), SearchByMeterActivity.class);
                //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);


//                /****DIALOG BOX INITIALIZATION PROVIDING A LAYOUT TO DIALOUGE****/
//                final Dialog dialogAccount = new Dialog(context, R.style.DialogeAppTheme);
//                dialogAccount.getWindow().getAttributes().windowAnimations = R.anim.anim_slide_out_left;
//                dialogAccount.setContentView(R.layout.custom_dialoge_search);
//                dialogAccount.setTitle("Meter Number Search");
//
//                // set the custom dialog components - text, image and button
//                final EditText editTextAccno = (EditText) dialogAccount.findViewById(R.id.EditTextACC);
//                editTextAccno.setHint("Enter Meter Number");
//                Button dialogButton = (Button) dialogAccount.findViewById(R.id.dialogButtonACCOK);
//                Button dHomeButton = (Button) dialogAccount.findViewById(R.id.HomeButton);
//
//                dHomeButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        finish();
//                        overridePendingTransition(R.anim.anim_slide_in_right,
//                                R.anim.anim_slide_out_right);
//
//                    }
//                });
//                // if button is clicked, close the custom dialog
//                /****DIALOUGE BOX BUTTON INTIALISATION AND EVENT ADDING****/
//                dialogButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        try {
//                            accountnum = editTextAccno.getText().toString().trim();
//                        } catch (NullPointerException N) {
//                            N.printStackTrace();
//                        }
//
//                        if (accountnum.isEmpty()) {
//                            Toast.makeText(context, "Please Enter Meter Number.", Toast.LENGTH_LONG).show();
//                        } else {
//                            Log.e(getApplicationContext(), "BillTypeAct", "Account no : " + accountnum);
//
//                            dbHelper = new DB(getApplicationContext());
//                            SD = dbHelper.getWritableDatabase();
//
//                            /****RAW QUERY EXICUTION AND STORING IN CURSOR****/
//                            String ret = "SELECT * FROM TBL_CONSMAST WHERE Meter_S_No ='" + accountnum + "'";
//                            //String ret = "SELECT Consumer_Number FROM TBL_CONSMAST WHERE Name='SAPHALA PARIDA'";
//                            Cursor curconsmasData = SD.rawQuery(ret, null);
//
//                            /****GETTING DATA FROM CURSOR WHEN CURSOR IS NOT NULL AND ON THE FORST INDEX****/
//                            if (curconsmasData != null && curconsmasData.moveToFirst()) {
//
//                                uac = new UtilAppCommon();
//                                try {
//                                    uac.copyResultsetToConmasClass(curconsmasData);
//                                } catch (SQLException e) {
//                                    e.printStackTrace();
//                                }
//
//
//                                /****INITIALISATION OF INTENT AND ADDING SENDING DATA TO NEXT SCREEN , STARING INTENT****/
//                                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
//                                //  intent.putExtra("datalist", allist);
//                                //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(intent);
//                                overridePendingTransition(R.anim.anim_slide_in_left,
//                                        R.anim.anim_slide_out_left);
//
//                            } else {
//                                dialogAccount.dismiss();
//                                Toast.makeText(context, "Meter Number Not Found", Toast.LENGTH_LONG).show();
//                            }
//
//                        }
//
//                    }
//                });
//                dialogAccount.show();
            }
        });

        /****
         * ENTER OLD-ACCOUNT NUMBER*
         * ***/

        btnSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialogAccount = new Dialog(context, R.style.DialogeAppTheme);
                dialogAccount.getWindow().getAttributes().windowAnimations = R.anim.anim_slide_out_left;
                dialogAccount.setContentView(R.layout.custom_dialoge_search);
                dialogAccount.setTitle("IVRS No Search");

                // set the custom dialog components - text, image and button
                final EditText editTextAccno = (EditText) dialogAccount.findViewById(R.id.EditTextACC);
                editTextAccno.setHint("Enter IVRS Number");
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
                            Toast.makeText(context, "Please Enter IVRS Number.", Toast.LENGTH_LONG).show();
                        } else {

                            Log.e(getApplicationContext(), "BillTypeAct", "Account no : " + accountnum);

                            dbHelper = new DB(getApplicationContext());
                            SD = dbHelper.getWritableDatabase();

//                            String ret = "SELECT * FROM TBL_CONSMAST WHERE MAIN_CONS_LNK_NO='" + accountnum + "'";
                            String ret = "SELECT * FROM TBL_COLLECTION WHERE IVRS_NO='" + accountnum + "'";
                            //String ret = "SELECT Consumer_Number FROM TBL_CONSMAST WHERE Name='SAPHALA PARIDA'";
                            Cursor curconsmasData = SD.rawQuery(ret, null);
                            if (curconsmasData != null && curconsmasData.moveToFirst()) {

                                uac = new UtilAppCommon();
                                try {
                                    uac.copyResultsetToConmasClass(curconsmasData);
//                                    uac.copyResultsetToCollClass(curconsmasData);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                /****INITIALISATION OF INTENT AND ADDING SENDING DATA TO NEXT SCREEN , STARING INTENT****/
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                //  intent.putExtra("datalist", allist);
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left);

                            } else {
                                dialogAccount.dismiss();
                                Toast.makeText(context, "Old-Account Number Not Found", Toast.LENGTH_LONG).show();
                            }

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
                Intent intent = new Intent(getApplicationContext(), SearchByName_Activity.class);
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
                Intent intent = new Intent(getApplicationContext(),TestMainActivity.class);
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
                Intent intent = new Intent(getApplicationContext(), SearchByAddressActivity.class);
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

                if(isMobileDataEnabled()){

                    checkPendingRecord();
                    if( billingPending > 0 ){
                        new BulkTextFileCreate().execute();
                    }
                    else {
                        Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
                    }


                }else{

                    Toast.makeText(ReplacementtypesActivity.this, "Internally stored due to no connectivity", Toast.LENGTH_SHORT).show();


                }

            }
        });
        btnSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new TextFileClass(SDActivity.this).execute();
                Intent intent = new Intent(ReplacementtypesActivity.this, FullUploadService.class);
                startService(intent);
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

    public void onBackPressed() {

        Intent intent = new Intent(ReplacementtypesActivity.this, Replacement.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);

    }

    private void showProgressDialog() {
        if (sDialog == null) {
            sDialog = new SweetAlertDialog(ReplacementtypesActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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

    public void checkPendingRecord(){

        dbHelper2 = new DB(getApplicationContext());
        SD2 = dbHelper2.getWritableDatabase();

        String billpending = "SELECT CONSUMERNO from 'TBL_METER_REPLACEMENT'";
        // String colpending  = "SELECT  CON_NO from 'TBL_COLMASTER_MP' WHERE Upload_Flag='N'";
        // String metpending  = "SELECT  CONSUMERNO from 'TBL_METERUPLOAD' WHERE UPLOADFLAG='N'";

        final Cursor curBillPend = SD2.rawQuery(billpending, null);
        // final Cursor curColPend  = SD3.rawQuery(colpending, null);
        // final Cursor curMetPend  = SD3.rawQuery(metpending, null);

        billingPending      = curBillPend.getCount();
        // collectionPending   = curColPend.getCount();
        // meterPending        = curMetPend.getCount();

    }

    /*--------------------- Replacement Bulk Upload Text File Creation ----------------------------------*/
    private class BulkTextFileCreate extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            dbHelper2 = new DB(getApplicationContext());
            SD2 = dbHelper2.getWritableDatabase();

            String selquer = "SELECT * FROM TBL_METER_REPLACEMENT";//WHERE Upload_Flag='N'

            Cursor curBillselect = SD2.rawQuery(selquer, null);
            ArrayList<String> mylist = new ArrayList<String>();
            ArrayList<String> mylist1 = new ArrayList<String>();
            ArrayList<String> mylist2 = new ArrayList<String>();

            if (curBillselect != null && curBillselect.moveToFirst()) {
                int i=0;
                while (curBillselect.isAfterLast() == false) {
                    Calendar c = Calendar.getInstance();
                    System.out.println("Current time => " + c.getTime());
                    i=i+1;
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yy");
                    String formattedDate = df.format(c.getTime());

                    mylist.add(curBillselect.getString(0) + "}" + curBillselect.getString(1) + "}" + curBillselect.getString(2) + "}" +
                            curBillselect.getString(3) + "}" + curBillselect.getString(4) + "}" + curBillselect.getString(5) + "}" +
                            curBillselect.getString(6) + "}" + curBillselect.getString(7) + "}" + curBillselect.getString(8) + "}" +
                            curBillselect.getString(9) + "}" + curBillselect.getString(10) + "}" + curBillselect.getString(11) + "}" +
                            curBillselect.getString(12) + "}" + curBillselect.getString(13) + "}" + curBillselect.getString(14) + "}" +
                            curBillselect.getString(15) + "}" + curBillselect.getString(16) + "}" + curBillselect.getString(17) + "}" +
                            curBillselect.getString(18) + "}" + curBillselect.getString(19) + "}" + curBillselect.getString(20) + "}" +
                            curBillselect.getString(21) + "}" + curBillselect.getString(22) + "}" + curBillselect.getString(23) + "}" +
                            curBillselect.getString(24) + "}" + curBillselect.getString(25) + "}" + curBillselect.getString(26) + "}" +
                            curBillselect.getString(27) + "}" + curBillselect.getString(28) + "}" + curBillselect.getString(29) + "}" +
                            curBillselect.getString(30) + "}" + curBillselect.getString(31) + "}" + curBillselect.getString(32) + "}" +
                            curBillselect.getString(33) + "}" + curBillselect.getString(34) + "}" + curBillselect.getString(35) + "}" +
                            curBillselect.getString(36) + "}" + curBillselect.getString(37) );


                    mylist2.add(curBillselect.getString(0) + "}" + curBillselect.getString(1) + "}" + curBillselect.getString(2) + "}" +
                            curBillselect.getString(3) + "}" + curBillselect.getString(4) + "}" + curBillselect.getString(5) + "}" +
                            curBillselect.getString(6) + "}" + curBillselect.getString(7) + "}" + curBillselect.getString(8) + "}" +
                            curBillselect.getString(9) + "}" + curBillselect.getString(10) + "}" + curBillselect.getString(11) + "}" +
                            curBillselect.getString(12) + "}" + curBillselect.getString(13) + "}" + curBillselect.getString(14) + "}" +
                            curBillselect.getString(15) + "}" + curBillselect.getString(16) + "}" + curBillselect.getString(17) + "}" +
                            curBillselect.getString(18) + "}" + curBillselect.getString(19) + "}" + curBillselect.getString(20) + "}" +
                            curBillselect.getString(21) + "}" + curBillselect.getString(22) + "}" + curBillselect.getString(23) + "}" +
                            curBillselect.getString(24) + "}" + curBillselect.getString(25) + "}" + curBillselect.getString(26) + "}" +
                            curBillselect.getString(27) + "}" + curBillselect.getString(28) + "}" + curBillselect.getString(29) + "}" +
                            curBillselect.getString(30) + "}" + curBillselect.getString(31) + "}" + curBillselect.getString(32) + "}" +
                            curBillselect.getString(33) + "}" + curBillselect.getString(34) + "}" + curBillselect.getString(35) + "}" +
                            curBillselect.getString(36) + "}" + curBillselect.getString(37) );
                    curBillselect.moveToNext();

                }
                generateNoteOnSD(getApplicationContext(), "meter.csv", mylist);
//                generateNoteOnSD(getApplicationContext(), "billing1.csv", mylist1);

                generatebckOnSD(getApplicationContext(), "meter_bulk.csv", mylist2,"MBC/BulkUploadBck/");
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            if (ReplacementtypesActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
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

    /*--------------------- Replacement Bulk Upload to Server ----------------------------------*/
    private class BulkUpload extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {


                FileInputStream fstrm = new FileInputStream( GSBilling.getInstance().getFinalZipName() + ".zip");
                HttpFileUpload hfu = new HttpFileUpload("http://enservtest.fedco.co.in/MPSurvey/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                Log.e(getApplicationContext(), "SLPrintAct", "going out " + GSBilling.getInstance().getFinalZipName() + ".zip");

                int status = hfu.Send_Now(fstrm);
                if (status != 200) {

                    ReplacementtypesActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (ReplacementtypesActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                                return;
                            }
                            dismissProgressDialog();
                            Toast.makeText(ReplacementtypesActivity.this, "Unable to upload try again ", Toast.LENGTH_LONG).show();

                        }
                    });

                } else {

                    ReplacementtypesActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (ReplacementtypesActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                                return;
                            }
                            dismissProgressDialog();
                            Toast.makeText(ReplacementtypesActivity.this, "uploaded successfully", Toast.LENGTH_LONG).show();

                        }
                    });

                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            if (ReplacementtypesActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
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

    public void generatebckOnSD(Context context, String sFileName, ArrayList sBody,String Fileroot) {
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

}