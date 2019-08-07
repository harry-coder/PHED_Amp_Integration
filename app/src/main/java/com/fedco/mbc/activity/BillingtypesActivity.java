package com.fedco.mbc.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fedco.mbc.R;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.bluetoothprinting.GlobalPool;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.model.Structbilling;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.service.FullUploadService;
import com.fedco.mbc.service.UploadService;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.testing.TestMainActivity;
import com.fedco.mbc.utils.FileUpload;
import com.fedco.mbc.utils.HttpFileUpload;
import com.fedco.mbc.utils.URLS;
import com.fedco.mbc.utils.UtilAppCommon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BillingtypesActivity extends AppCompatActivity implements LogoutListaner {


    final Context context = this;
    UtilAppCommon uac;
    DB dbHelper, dbHelper2;
    SQLiteDatabase SD, SD2;
    SessionManager session;
    Logger Log;
    private ProgressDialog progress;
    Button btnOne, btnTwo, btnThree, btnFour, btnFive, btnSix, btnSeven, btnEight;
    public int globalcount = 10;

    String ZipBillCSVPath   = Environment.getExternalStorageDirectory() + "/MBC/BillCSV/";
    String ZipCopyPath = Environment.getExternalStorageDirectory() + "/MBC/BulkUpload/";
    String ZipImgLimitPath = Environment.getExternalStorageDirectory() + "/MBC/LimitImages/";
    String ZipImgCountPath = Environment.getExternalStorageDirectory() + "/MBC/LimitImages";
    String ZipSourcePath = Environment.getExternalStorageDirectory() + "/MBC/Images/";
    public String ZipDesPath;
    String ZipDesPathdup;

    SweetAlertDialog sDialog;
    String name;
    String accountnum;
    int billingPending,billingPendingBulk;

    Button bt_searchByBookId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billingtypes);


        try {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
            getSupportActionBar().setLogo(R.mipmap.logo);
            setTitle("Customer Search");
        } catch (NullPointerException npe) {
            Log.e(getApplicationContext(), "Billing Act", "ActionBar Throwing null Pointer", npe);
        }

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        name = session.retLicence();

        this.ZipDesPath = Environment.getExternalStorageDirectory() + "/MBC/" + name + GSBilling.getInstance().captureDatetime() + ".zip";
        this.ZipDesPathdup = Environment.getExternalStorageDirectory() + "/MBC/" + name + GSBilling.getInstance().captureDatetime() ;

        GSBilling.getInstance().clearData();
        ((GlobalPool)getApplication()).registerSessionListaner(this);
        ((GlobalPool)getApplication()).startUserSession();

        /****BUTTON  INITIALISATION****/
        btnEight    = (Button) findViewById(R.id.ButtonMeter);
        btnSeven    = (Button) findViewById(R.id.ButtonOldacc);
        btnSix      = (Button) findViewById(R.id.Button6);
        btnFive     = (Button) findViewById(R.id.Button5);
        btnFour     = (Button) findViewById(R.id.Button4);
        btnThree    = (Button) findViewById(R.id.ButtonRoute);
        btnTwo      = (Button) findViewById(R.id.Button2);
        btnOne      = (Button) findViewById(R.id.Button1);
        bt_searchByBookId= (Button) findViewById ( R.id.bt_searchByBookId );


        btnThree.setEnabled(false);
        btnSix.setEnabled(true);
        btnFive.setEnabled(true);


        billindDivRecord();
        /****
         * ENTER ACCOUNT NUMBER*
         * ***/
        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /****DIALOG BOX INITIALIZATION PROVIDING A LAYOUT TO DIALOUGE****/
                final Dialog dialogAccount = new Dialog(context, R.style.DialogeAppTheme);
                dialogAccount.setContentView(R.layout.custom_dialoge_billing_search);
                dialogAccount.setTitle("Customer Search");

                // set the custom dialog components - text, image and button
                final EditText editTextAccno = (EditText) dialogAccount.findViewById(R.id.EditTextACC);
                editTextAccno.setHint("Enter Customer Number");
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
                            Toast.makeText(context, "Please Enter Customer Number.", Toast.LENGTH_LONG).show();
                        } else {

                            Log.e(getApplicationContext(), "BillTypeAct", "Consumer Num : " + accountnum);

                            dbHelper = new DB(getApplicationContext());
                            SD = dbHelper.getWritableDatabase();

                            String ret = "SELECT * FROM TBL_CONSMAST WHERE Consumer_Number ='" + accountnum+ "'";
                            Cursor curconsmasData = SD.rawQuery(ret, null);

                            if (curconsmasData != null && curconsmasData.moveToFirst()) {

                                uac = new UtilAppCommon();
                                try {
                                    uac.copyResultsetToConmasClass(curconsmasData);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                dialogAccount.dismiss();
                                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                                // intent.putExtra("datalist", allist);
                                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left);

                            } else {

                                dialogAccount.dismiss();
                                Toast.makeText(context, "Customer Number Not Found", Toast.LENGTH_LONG).show();

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


                Intent intent = new Intent(getApplicationContext(), ConsumerSearchByMeterActivity.class);
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
                dialogAccount.setContentView(R.layout.custom_dialoge_billing_search);
                dialogAccount.setTitle("Customer No Search");

                // set the custom dialog components - text, image and button
                final EditText editTextAccno = (EditText) dialogAccount.findViewById(R.id.EditTextACC);
                editTextAccno.setHint("Enter Customer Number");
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
                            Toast.makeText(context, "Please Enter Cusotmer Number.", Toast.LENGTH_LONG).show();
                        } else {

                            Log.e(getApplicationContext(), "BillTypeAct", "Account no : " + accountnum);

                            dbHelper = new DB(getApplicationContext());
                            SD = dbHelper.getWritableDatabase();

                            String ret = "SELECT * FROM TBL_CONSMAST WHERE MAIN_CONS_LNK_NO='" + accountnum + "'";
                            //String ret = "SELECT Consumer_Number FROM TBL_CONSMAST WHERE Name='SAPHALA PARIDA'";
                            Cursor curconsmasData = SD.rawQuery(ret, null);
                            if (curconsmasData != null && curconsmasData.moveToFirst()) {

                                uac = new UtilAppCommon();
                                try {
                                    uac.copyResultsetToConmasClass(curconsmasData);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                /****INITIALISATION OF INTENT AND ADDING SENDING DATA TO NEXT SCREEN , STARING INTENT****/
                                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                //  intent.putExtra("datalist", allist);
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left);

                            } else {
                                dialogAccount.dismiss();
                                Toast.makeText(context, "Old-Customer Number Not Found", Toast.LENGTH_LONG).show();
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

                if(isMobileDataEnabled()){

                    checkPendingRecord();
/*//                    checkPendingRecordBulk();
//                    if( billingPendingBulk > 10 ){
                    if( billingPendingBulk > 0 ){

                        final Dialog dialogAccount = new Dialog(BillingtypesActivity.this, R.style
                                .DialogeAppTheme);

                        dialogAccount.setContentView(R.layout.custom_dialoge_warning);
                        dialogAccount.setTitle("Account Search");

                        // set the custom dialog components - text, image and button

                        Button dialogButton = (Button) dialogAccount.findViewById(R.id.dialogButtonACCOK);
                        ImageView dialogImage = (ImageView) dialogAccount.findViewById(R.id
                                .dialogIV);
                        dialogImage.setImageResource(R.drawable.bulk_upload_img);
                        GSBilling.getInstance().setConsumptionchkhigh("  ");
                        // if button is clicked, close the custom dialog
                        *//****DIALOUGE BOX BUTTON INTIALISATION AND EVENT ADDING****//*
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialogAccount.dismiss();
//
                                Toast.makeText(context, "Please Upload Pending Data", Toast
                                        .LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), BillingtypesActivity.class);
                                intent.putExtra("flowkey", "billing");
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left);

                            }
                        });

                        dialogAccount.setOnKeyListener(new Dialog.OnKeyListener() {

                            @Override
                            public boolean onKey(DialogInterface arg0, int keyCode,
                                                 KeyEvent event) {
                                // TODO Auto-generated method stub
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    Intent intent = new Intent(BillingtypesActivity.this, Billing.class);
                                    GSBilling.getInstance().setPrintSingle("<0x09>");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.anim_slide_in_left,
                                            R.anim.anim_slide_out_left);
                                    dialogAccount.dismiss();
                                }
                                return true;
                            }

                        });
                        dialogAccount.show();
//                        new BulkTextFileCreate().execute();
                    }
                    else if(billingPendingBulk == 0 ) {
                        Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
                    }else{
                        new BulkTextFileCreate().execute();
                    }*/

                    if(billingPending > 0){
                        new BulkTextFileCreate().execute();
//                        new TextFileClassFull(BillingtypesActivity.this).execute();

                    }else{
                        Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
                    }

                }else{

                    Toast.makeText(BillingtypesActivity.this, "Internally stored due to no connectivity", Toast.LENGTH_SHORT).show();


                }

            }
        });
        btnSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new TextFileClass(BillingtypesActivity.this).execute();
//                new BulkTextFileCreate().execute();
//                new TextFileClassFull(BillingtypesActivity.this).execute();
                Intent intent = new Intent(BillingtypesActivity.this, FullUploadService.class);
                startService(intent);
            }
        });


        bt_searchByBookId.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(BillingtypesActivity.this, SearchCustByBookId.class);
                startActivity (intent);
            }
        } );

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

        Intent intent = new Intent(BillingtypesActivity.this, Billing.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);

    }

    private void showProgressDialog() {
        if (sDialog == null) {
            sDialog = new SweetAlertDialog(BillingtypesActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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

        String billpending = "SELECT Cons_Number from 'TBL_BILLING'";

        final Cursor curBillPend = SD2.rawQuery(billpending, null);
        billingPending      = curBillPend.getCount();
        // collectionPending   = curColPend.getCount();
        // meterPending        = curMetPend.getCount();

    }
    public void checkPendingRecordBulk(){

        dbHelper2 = new DB(getApplicationContext());
        SD2 = dbHelper2.getWritableDatabase();

        String billpending = "SELECT Cons_Number from 'TBL_BILLING' Where Upload_Flag='N'";

        final Cursor curBillPend = SD2.rawQuery(billpending, null);
        billingPendingBulk      = curBillPend.getCount();
        // collectionPending   = curColPend.getCount();
        // meterPending        = curMetPend.getCount();

    }

    public void billindDivRecord(){

        dbHelper2 = new DB(getApplicationContext());
        SD2 = dbHelper2.getWritableDatabase();

        String billpending = "SELECT * from 'TBL_BILLING_DIV_MASTER'";
        // String colpending  = "SELECT  CON_NO from 'TBL_COLMASTER_MP' WHERE Upload_Flag='N'";
        // String metpending  = "SELECT  CONSUMERNO from 'TBL_METERUPLOAD' WHERE UPLOADFLAG='N'";

        final Cursor curBillPend = SD2.rawQuery(billpending, null);
        if (curBillPend != null && curBillPend.moveToFirst()) {

            uac = new UtilAppCommon();
            try {
                uac.copyResultBillDiv(curBillPend);
            } catch (SQLException e) {
                e.printStackTrace();
            }


        } else {

            Toast.makeText(context, "Div Not Found", Toast.LENGTH_LONG).show();
        }
        // final Cursor curColPend  = SD3.rawQuery(colpending, null);
        // final Cursor curMetPend  = SD3.rawQuery(metpending, null);

        billingPending      = curBillPend.getCount();
        // collectionPending   = curColPend.getCount();
        // meterPending        = curMetPend.getCount();

    }

    /*--------------------- Billing Bulk Upload Text File Creation ----------------------------------*/
    private class BulkTextFileCreate extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            dbHelper2 = new DB(getApplicationContext());
            SD2 = dbHelper2.getWritableDatabase();

            String selquer = "SELECT * FROM TBL_BILLING";//WHERE Upload_Flag='N'

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
                            curBillselect.getString(36) + "}" + curBillselect.getString(37) + "}" + curBillselect.getString(38) + "}" +
                            curBillselect.getString(39) + "}" + curBillselect.getString(40) + "}" + curBillselect.getString(41) + "}" +
                            curBillselect.getString(42) + "}" + curBillselect.getString(43) + "}" + curBillselect.getString(44) + "}" +
                            curBillselect.getString(45) + "}" + curBillselect.getString(46) + "}" + curBillselect.getString(47) + "}" +
                            curBillselect.getString(48) + "}" + curBillselect.getString(49) + "}" + curBillselect.getString(50) + "}" +
                            curBillselect.getString(51) + "}" + curBillselect.getString(52) + "}" + curBillselect.getString(53) + "}" +
                            curBillselect.getString(54) + "}" + curBillselect.getString(55) + "}" + curBillselect.getString(56) + "}" +
                            curBillselect.getString(57) + "}" + curBillselect.getString(58) + "}" + curBillselect.getString(59) + "}" +
                            curBillselect.getString(60) + "}" + curBillselect.getString(61) + "}" + curBillselect.getString(62) + "}" +
                            curBillselect.getString(63) + "}" + curBillselect.getString(64) + "}" + curBillselect.getString(65) + "}" +
                            curBillselect.getString(66) + "}" + curBillselect.getString(67) + "}" + curBillselect.getString(68) + "}" +
                            curBillselect.getString(69) + "}" + curBillselect.getString(70) + "}" + curBillselect.getString(71) + "}" +
                            curBillselect.getString(72) + "}" + curBillselect.getString(73) + "}" + curBillselect.getString(74) + "}" +
                            curBillselect.getString(75) + "}" + curBillselect.getString(76) + "}" + curBillselect.getString(77) + "}" +
                            curBillselect.getString(78) + "}" + curBillselect.getString(79) + "}" + curBillselect.getString(80) + "}" +
                            curBillselect.getString(81) + "}" + curBillselect.getString(82) + "}" + curBillselect.getString(83) + "}" +
                            curBillselect.getString(84) + "}" + curBillselect.getString(85) + "}" + curBillselect.getString(86) + "}" +
                            curBillselect.getString(87) + "}" + curBillselect.getString(88) + "}" + curBillselect.getString(89) + "}" +
                            curBillselect.getString(90) + "}" + curBillselect.getString(91) + "}" + curBillselect.getString(92) + "}" +
                            curBillselect.getString(93) + "}" + curBillselect.getString(94) + "}" + curBillselect.getString(95) + "}" +
                            curBillselect.getString(96) + "}" + curBillselect.getString(97) + "}" + curBillselect.getString(98) + "}" +
                            curBillselect.getString(99) + "}" + curBillselect.getString(100) + "}" + curBillselect.getString(101) + "}" +
                            curBillselect.getString(102) + "}" + curBillselect.getString(103) + "}" + curBillselect.getString(104) + "}" +
                            curBillselect.getString(105) + "}" + curBillselect.getString(106) + "}" + curBillselect.getString(107) + "}" +
                            curBillselect.getString(108) + "}" + curBillselect.getString(109) + "}" + curBillselect.getString(110) + "}" +
                            curBillselect.getString(111) + "}" + curBillselect.getString(112) + "}" + curBillselect.getString(113) + "}" +
                            curBillselect.getString(114) + "}" + curBillselect.getString(115) + "}" + curBillselect.getString(116) + "}" +
                            curBillselect.getString(117) + "}" + curBillselect.getString(118) + "}" + curBillselect.getString(119) + "}" +
                            curBillselect.getString(120) + "}" + curBillselect.getString(121) + "}" + curBillselect.getString(122) + "}" +
                            curBillselect.getString(123) + "}" + curBillselect.getString(124) + "}" + curBillselect.getString(125) + "}" +
                            curBillselect.getString(126) + "}" + curBillselect.getString(127) + "}" + curBillselect.getString(128) + "}" +
                            curBillselect.getString(129) + "}" + curBillselect.getString(130) + "}" + curBillselect.getString(131) + "}" +
                            curBillselect.getString(132) + "}" + curBillselect.getString(133) + "}" + curBillselect.getString(134) + "}" +
                            curBillselect.getString(135) + "}" + curBillselect.getString(136) + "}" + curBillselect.getString(137) + "}" +
                            curBillselect.getString(138) + "}" + curBillselect.getString(139) + "}" + curBillselect.getString(140) + "}" +
                            curBillselect.getString(141) + "}" + curBillselect.getString(142) + "}" + curBillselect.getString(143) + "}" +
                            curBillselect.getString(144) + "}" + curBillselect.getString(145) + "}" + curBillselect.getString(146) + "}" +
                            curBillselect.getString(147) + "}" + curBillselect.getString(148) + "}" + curBillselect.getString(149) + "}" +
                            curBillselect.getString(150) + "}" + curBillselect.getString(151) + "}" + curBillselect.getString(152) + "}" +
                            curBillselect.getString(153) + "}" + curBillselect.getString(154) + "}" + curBillselect.getString(155) + "}" +
                            curBillselect.getString(156) + "}" + curBillselect.getString(157) + "}" + curBillselect.getString(158) + "}" +
                            curBillselect.getString(159) + "}" + curBillselect.getString(160) + "}" + curBillselect.getString(161) + "}" +
                            curBillselect.getString(162) + "}" + curBillselect.getString(163) + "}" + curBillselect.getString(164) + "}" +
                            curBillselect.getString(165) + "}" + curBillselect.getString(166) + "}" + curBillselect.getString(167) + "}" +
                            curBillselect.getString(168) + "}" + curBillselect.getString(169) + "}" + curBillselect.getString(170) + "}" +
                            curBillselect.getString(171) + "}" + curBillselect.getString(172) + "}" + curBillselect.getString(173) + "}" +
                            curBillselect.getString(174) + "}" + curBillselect.getString(175) + "}" + curBillselect.getString(176) + "}" +
                            curBillselect.getString(177) + "}" + curBillselect.getString(178) + "}" + curBillselect.getString(179) + "}" +
                            curBillselect.getString(180) + "}" + curBillselect.getString(181) + "}" + curBillselect.getString(182) + "}" +
                            curBillselect.getString(183) + "}" + curBillselect.getString(184) + "}" + curBillselect.getString(185) + "}" +
                            curBillselect.getString(186) + "}" + curBillselect.getString(187) + "}" + curBillselect.getString(188) + "}" +
                            curBillselect.getString(189) + "}" + curBillselect.getString(190) + "}" + curBillselect.getString(191) + "}" +
                            curBillselect.getString(192) + "}" + curBillselect.getString(193) + "}" + curBillselect.getString(194) + "}" +
                            curBillselect.getString(195) + "}" + curBillselect.getString(196) + "}" + curBillselect.getString(197) + "}" +
                            curBillselect.getString(198) + "}" + curBillselect.getString(199) + "}" + curBillselect.getString(200) + "}" +
                            curBillselect.getString(201) + "}" + curBillselect.getString(202) + "}" + curBillselect.getString(203) + "}" +
                            curBillselect.getString(204) + "}" + curBillselect.getString(205) + "}" + curBillselect.getString(206) + "}" +
                            curBillselect.getString(207) + "}" + curBillselect.getString(208) + "}" + curBillselect.getString(209) + "}" +
                            curBillselect.getString(210) + "}" + curBillselect.getString(211)+ "}" + curBillselect.getString(212)+ "}" +
                            curBillselect.getString(213)+ "}" + curBillselect.getString(214)+ "}" + curBillselect.getString(215)+ "}"+
                            curBillselect.getString(216));
                    mylist1.add(curBillselect.getString(60)+ "$" + curBillselect.getString(0) + "$" + curBillselect.getString(5) + "$" +
                            curBillselect.getString(61)+ "$" + curBillselect.getString(11)+ "$" + curBillselect.getString(62)+ "$" +
                            curBillselect.getString(2) + "$" + i + "$" + curBillselect.getString(7) + "$" +
                            curBillselect.getString(212)+ "$" + curBillselect.getString(1) + "$" + formattedDate + "$" +
                            curBillselect.getString(63)+ "$" + curBillselect.getString(8) + "$" + curBillselect.getString(64)+ "$" +
                            curBillselect.getString(65)+ "$" + curBillselect.getString(66)+ "$" + curBillselect.getString(39)+ "$" +
                            curBillselect.getString(10)+ "$" + curBillselect.getString(67)+ "$" + curBillselect.getString(68)+ "$" +
                            curBillselect.getString(69)+ "$" + curBillselect.getString(70)+ "$" + curBillselect.getString(71)+ "$" +
                            curBillselect.getString(182) + "$" + curBillselect.getString(72)+ "$" + curBillselect.getString(73)+ "$" +
                            curBillselect.getString(24)+ "$" + curBillselect.getString(74)+ "$" + "0"+ "$" + curBillselect.getString(75)+ "$" +
                            curBillselect.getString(76)+ "$" + curBillselect.getString(27)+ "$" + curBillselect.getString(77)+ "$" +
                            curBillselect.getString(26)+ "$" + curBillselect.getString(78)+ "$" + curBillselect.getString(79)+ "$" +
                            curBillselect.getString(80)+ "$" + curBillselect.getString(81)+ "$" + curBillselect.getString(82)+ "$" +
                            curBillselect.getString(201) + "$" + curBillselect.getString(83)+ "$" + curBillselect.getString(84)+ "$" +
                            curBillselect.getString(85)+ "$" + curBillselect.getString(86)+ "$" + curBillselect.getString(169) + "$" +
                            curBillselect.getString(87)+ "$" + curBillselect.getString(88)+ "$" + curBillselect.getString(89)+ "$" +
                            curBillselect.getString(90)+ "$" + curBillselect.getString(91)+ "$" + curBillselect.getString(92)+ "$" +
                            curBillselect.getString(93)+ "$" + curBillselect.getString(94)+ "$" + curBillselect.getString(95)+ "$" +
                            curBillselect.getString(96)+ "$" + curBillselect.getString(97)+ "$" + curBillselect.getString(98)+ "$" +
                            curBillselect.getString(99)+ "$" + curBillselect.getString(178) + "$0$" );

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
                            curBillselect.getString(36) + "}" + curBillselect.getString(37) + "}" + curBillselect.getString(38) + "}" +
                            curBillselect.getString(39) + "}" + curBillselect.getString(40) + "}" + curBillselect.getString(41) + "}" +
                            curBillselect.getString(42) + "}" + curBillselect.getString(43) + "}" + curBillselect.getString(44) + "}" +
                            curBillselect.getString(45) + "}" + curBillselect.getString(46) + "}" + curBillselect.getString(47) + "}" +
                            curBillselect.getString(48) + "}" + curBillselect.getString(49) + "}" + curBillselect.getString(50) + "}" +
                            curBillselect.getString(51) + "}" + curBillselect.getString(52) + "}" + curBillselect.getString(53) + "}" +
                            curBillselect.getString(54) + "}" + curBillselect.getString(55) + "}" + curBillselect.getString(56) + "}" +
                            curBillselect.getString(57) + "}" + curBillselect.getString(58) + "}" + curBillselect.getString(59) + "}" +
                            curBillselect.getString(60) + "}" + curBillselect.getString(61) + "}" + curBillselect.getString(62) + "}" +
                            curBillselect.getString(63) + "}" + curBillselect.getString(64) + "}" + curBillselect.getString(65) + "}" +
                            curBillselect.getString(66) + "}" + curBillselect.getString(67) + "}" + curBillselect.getString(68) + "}" +
                            curBillselect.getString(69) + "}" + curBillselect.getString(70) + "}" + curBillselect.getString(71) + "}" +
                            curBillselect.getString(72) + "}" + curBillselect.getString(73) + "}" + curBillselect.getString(74) + "}" +
                            curBillselect.getString(75) + "}" + curBillselect.getString(76) + "}" + curBillselect.getString(77) + "}" +
                            curBillselect.getString(78) + "}" + curBillselect.getString(79) + "}" + curBillselect.getString(80) + "}" +
                            curBillselect.getString(81) + "}" + curBillselect.getString(82) + "}" + curBillselect.getString(83) + "}" +
                            curBillselect.getString(84) + "}" + curBillselect.getString(85) + "}" + curBillselect.getString(86) + "}" +
                            curBillselect.getString(87) + "}" + curBillselect.getString(88) + "}" + curBillselect.getString(89) + "}" +
                            curBillselect.getString(90) + "}" + curBillselect.getString(91) + "}" + curBillselect.getString(92) + "}" +
                            curBillselect.getString(93) + "}" + curBillselect.getString(94) + "}" + curBillselect.getString(95) + "}" +
                            curBillselect.getString(96) + "}" + curBillselect.getString(97) + "}" + curBillselect.getString(98) + "}" +
                            curBillselect.getString(99) + "}" + curBillselect.getString(100) + "}" + curBillselect.getString(101) + "}" +
                            curBillselect.getString(102) + "}" + curBillselect.getString(103) + "}" + curBillselect.getString(104) + "}" +
                            curBillselect.getString(105) + "}" + curBillselect.getString(106) + "}" + curBillselect.getString(107) + "}" +
                            curBillselect.getString(108) + "}" + curBillselect.getString(109) + "}" + curBillselect.getString(110) + "}" +
                            curBillselect.getString(111) + "}" + curBillselect.getString(112) + "}" + curBillselect.getString(113) + "}" +
                            curBillselect.getString(114) + "}" + curBillselect.getString(115) + "}" + curBillselect.getString(116) + "}" +
                            curBillselect.getString(117) + "}" + curBillselect.getString(118) + "}" + curBillselect.getString(119) + "}" +
                            curBillselect.getString(120) + "}" + curBillselect.getString(121) + "}" + curBillselect.getString(122) + "}" +
                            curBillselect.getString(123) + "}" + curBillselect.getString(124) + "}" + curBillselect.getString(125) + "}" +
                            curBillselect.getString(126) + "}" + curBillselect.getString(127) + "}" + curBillselect.getString(128) + "}" +
                            curBillselect.getString(129) + "}" + curBillselect.getString(130) + "}" + curBillselect.getString(131) + "}" +
                            curBillselect.getString(132) + "}" + curBillselect.getString(133) + "}" + curBillselect.getString(134) + "}" +
                            curBillselect.getString(135) + "}" + curBillselect.getString(136) + "}" + curBillselect.getString(137) + "}" +
                            curBillselect.getString(138) + "}" + curBillselect.getString(139) + "}" + curBillselect.getString(140) + "}" +
                            curBillselect.getString(141) + "}" + curBillselect.getString(142) + "}" + curBillselect.getString(143) + "}" +
                            curBillselect.getString(144) + "}" + curBillselect.getString(145) + "}" + curBillselect.getString(146) + "}" +
                            curBillselect.getString(147) + "}" + curBillselect.getString(148) + "}" + curBillselect.getString(149) + "}" +
                            curBillselect.getString(150) + "}" + curBillselect.getString(151) + "}" + curBillselect.getString(152) + "}" +
                            curBillselect.getString(153) + "}" + curBillselect.getString(154) + "}" + curBillselect.getString(155) + "}" +
                            curBillselect.getString(156) + "}" + curBillselect.getString(157) + "}" + curBillselect.getString(158) + "}" +
                            curBillselect.getString(159) + "}" + curBillselect.getString(160) + "}" + curBillselect.getString(161) + "}" +
                            curBillselect.getString(162) + "}" + curBillselect.getString(163) + "}" + curBillselect.getString(164) + "}" +
                            curBillselect.getString(165) + "}" + curBillselect.getString(166) + "}" + curBillselect.getString(167) + "}" +
                            curBillselect.getString(168) + "}" + curBillselect.getString(169) + "}" + curBillselect.getString(170) + "}" +
                            curBillselect.getString(171) + "}" + curBillselect.getString(172) + "}" + curBillselect.getString(173) + "}" +
                            curBillselect.getString(174) + "}" + curBillselect.getString(175) + "}" + curBillselect.getString(176) + "}" +
                            curBillselect.getString(177) + "}" + curBillselect.getString(178) + "}" + curBillselect.getString(179) + "}" +
                            curBillselect.getString(180) + "}" + curBillselect.getString(181) + "}" + curBillselect.getString(182) + "}" +
                            curBillselect.getString(183) + "}" + curBillselect.getString(184) + "}" + curBillselect.getString(185) + "}" +
                            curBillselect.getString(186) + "}" + curBillselect.getString(187) + "}" + curBillselect.getString(188) + "}" +
                            curBillselect.getString(189) + "}" + curBillselect.getString(190) + "}" + curBillselect.getString(191) + "}" +
                            curBillselect.getString(192) + "}" + curBillselect.getString(193) + "}" + curBillselect.getString(194) + "}" +
                            curBillselect.getString(195) + "}" + curBillselect.getString(196) + "}" + curBillselect.getString(197) + "}" +
                            curBillselect.getString(198) + "}" + curBillselect.getString(199) + "}" + curBillselect.getString(200) + "}" +
                            curBillselect.getString(201) + "}" + curBillselect.getString(202) + "}" + curBillselect.getString(203) + "}" +
                            curBillselect.getString(204) + "}" + curBillselect.getString(205) + "}" + curBillselect.getString(206) + "}" +
                            curBillselect.getString(207) + "}" + curBillselect.getString(208) + "}" + curBillselect.getString(209) + "}" +
                            curBillselect.getString(210) + "}" + curBillselect.getString(211)+ "}" + curBillselect.getString(212)+ "}" +
                            curBillselect.getString(213)+ "}" + curBillselect.getString(214)+ "}" + curBillselect.getString(215)+ "}"+
                            curBillselect.getString(216));
                    curBillselect.moveToNext();

                }
                generateNoteOnSD(getApplicationContext(), "billing.csv", mylist);
                generateNoteOnSD(getApplicationContext(), "billing1.csv", mylist1);

                generatebckOnSD(getApplicationContext(), "billing_bulk.csv", mylist2,"MBC/BulkUploadBck/");
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            if (BillingtypesActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
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


                FileInputStream fstrm = new FileInputStream( GSBilling.getInstance().getFinalZipName() + ".zip");
                HttpFileUpload hfu = new HttpFileUpload(URLS.DataComm.billUpload, "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                Log.e(getApplicationContext(), "SLPrintAct", "going out " + GSBilling.getInstance().getFinalZipName() + ".zip");

                int status = hfu.Send_Now(fstrm);
                if (status != 200) {

                    BillingtypesActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (BillingtypesActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                                return;
                            }
                            dismissProgressDialog();
                            Toast.makeText(BillingtypesActivity.this, "Unable to upload try again ", Toast.LENGTH_LONG).show();

                        }
                    });

                } else {

                    BillingtypesActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (BillingtypesActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                                return;
                            }
                            dismissProgressDialog();
                            Toast.makeText(BillingtypesActivity.this, "uploaded successfully", Toast.LENGTH_LONG).show();

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

            if (BillingtypesActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
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

    /*--------------------- Billing Uplaod Text File Creation ----------------------------------*/
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

                String selquer = "SELECT * FROM TBL_BILLING WHERE Upload_Flag='N'";//WHERE Upload_Flag='N'
                String selColQuer = "SELECT * FROM TBL_COLMASTER_MP WHERE Upload_Flag='N'";//WHERE Upload_Flag='N'
                Cursor curBillselect = SD2.rawQuery(selquer, null);
                String arrStr[] = null;
                ArrayList<String> mylist = new ArrayList<String>();
                ArrayList<String> mylist1 = new ArrayList<String>();

                // Removefolder(ZipImgLimitPath);
                if (curBillselect != null && curBillselect.moveToFirst()) {
                    int counter = 0;
                    int iterate = 0;
                    while (curBillselect.isAfterLast() == false) {
                        counter++;
                        Calendar c = Calendar.getInstance();
                        System.out.println("Current time => " + c.getTime());
                        iterate=iterate+1;
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yy");
                        String formattedDate = df.format(c.getTime());
//                        String name = curBillselect.getString(0);
                        counter++;
                        // String name = curBillselect.getString(0);
                        String column_24=String.valueOf(Double.valueOf(curBillselect.getString(24))+Double.valueOf(curBillselect.getString(31)));


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
                                curBillselect.getString(36) + "}" + curBillselect.getString(37) + "}" + curBillselect.getString(38) + "}" +
                                curBillselect.getString(39) + "}" + curBillselect.getString(40) + "}" + curBillselect.getString(41) + "}" +
                                curBillselect.getString(42) + "}" + curBillselect.getString(43) + "}" + curBillselect.getString(44) + "}" +
                                curBillselect.getString(45) + "}" + curBillselect.getString(46) + "}" + curBillselect.getString(47) + "}" +
                                curBillselect.getString(48) + "}" + curBillselect.getString(49) + "}" + curBillselect.getString(50) + "}" +
                                curBillselect.getString(51) + "}" + curBillselect.getString(52) + "}" + curBillselect.getString(53) + "}" +
                                curBillselect.getString(54) + "}" + curBillselect.getString(55) + "}" + curBillselect.getString(56) + "}" +
                                curBillselect.getString(57) + "}" + curBillselect.getString(58) + "}" + curBillselect.getString(59) + "}" +
                                curBillselect.getString(60) + "}" + curBillselect.getString(61) + "}" + curBillselect.getString(62) + "}" +
                                curBillselect.getString(63) + "}" + curBillselect.getString(64) + "}" + curBillselect.getString(65) + "}" +
                                curBillselect.getString(66) + "}" + curBillselect.getString(67) + "}" + curBillselect.getString(68) + "}" +
                                curBillselect.getString(69) + "}" + curBillselect.getString(70) + "}" + curBillselect.getString(71) + "}" +
                                curBillselect.getString(72) + "}" + curBillselect.getString(73) + "}" + curBillselect.getString(74) + "}" +
                                curBillselect.getString(75) + "}" + curBillselect.getString(76) + "}" + curBillselect.getString(77) + "}" +
                                curBillselect.getString(78) + "}" + curBillselect.getString(79) + "}" + curBillselect.getString(80) + "}" +
                                curBillselect.getString(81) + "}" + curBillselect.getString(82) + "}" + curBillselect.getString(83) + "}" +
                                curBillselect.getString(84) + "}" + curBillselect.getString(85) + "}" + curBillselect.getString(86) + "}" +
                                curBillselect.getString(87) + "}" + curBillselect.getString(88) + "}" + curBillselect.getString(89) + "}" +
                                curBillselect.getString(90) + "}" + curBillselect.getString(91) + "}" + curBillselect.getString(92) + "}" +
                                curBillselect.getString(93) + "}" + curBillselect.getString(94) + "}" + curBillselect.getString(95) + "}" +
                                curBillselect.getString(96) + "}" + curBillselect.getString(97) + "}" + curBillselect.getString(98) + "}" +
                                curBillselect.getString(99) + "}" + curBillselect.getString(100) + "}" + curBillselect.getString(101) + "}" +
                                curBillselect.getString(102) + "}" + curBillselect.getString(103) + "}" + curBillselect.getString(104) + "}" +
                                curBillselect.getString(105) + "}" + curBillselect.getString(106) + "}" + curBillselect.getString(107) + "}" +
                                curBillselect.getString(108) + "}" + curBillselect.getString(109) + "}" + curBillselect.getString(110) + "}" +
                                curBillselect.getString(111) + "}" + curBillselect.getString(112) + "}" + curBillselect.getString(113) + "}" +
                                curBillselect.getString(114) + "}" + curBillselect.getString(115) + "}" + curBillselect.getString(116) + "}" +
                                curBillselect.getString(117) + "}" + curBillselect.getString(118) + "}" + curBillselect.getString(119) + "}" +
                                curBillselect.getString(120) + "}" + curBillselect.getString(121) + "}" + curBillselect.getString(122) + "}" +
                                curBillselect.getString(123) + "}" + curBillselect.getString(124) + "}" + curBillselect.getString(125) + "}" +
                                curBillselect.getString(126) + "}" + curBillselect.getString(127) + "}" + curBillselect.getString(128) + "}" +
                                curBillselect.getString(129) + "}" + curBillselect.getString(130) + "}" + curBillselect.getString(131) + "}" +
                                curBillselect.getString(132) + "}" + curBillselect.getString(133) + "}" + curBillselect.getString(134) + "}" +
                                curBillselect.getString(135) + "}" + curBillselect.getString(136) + "}" + curBillselect.getString(137) + "}" +
                                curBillselect.getString(138) + "}" + curBillselect.getString(139) + "}" + curBillselect.getString(140) + "}" +
                                curBillselect.getString(141) + "}" + curBillselect.getString(142) + "}" + curBillselect.getString(143) + "}" +
                                curBillselect.getString(144) + "}" + curBillselect.getString(145) + "}" + curBillselect.getString(146) + "}" +
                                curBillselect.getString(147) + "}" + curBillselect.getString(148) + "}" + curBillselect.getString(149) + "}" +
                                curBillselect.getString(150) + "}" + curBillselect.getString(151) + "}" + curBillselect.getString(152) + "}" +
                                curBillselect.getString(153) + "}" + curBillselect.getString(154) + "}" + curBillselect.getString(155) + "}" +
                                curBillselect.getString(156) + "}" + curBillselect.getString(157) + "}" + curBillselect.getString(158) + "}" +
                                curBillselect.getString(159) + "}" + curBillselect.getString(160) + "}" + curBillselect.getString(161) + "}" +
                                curBillselect.getString(162) + "}" + curBillselect.getString(163) + "}" + curBillselect.getString(164) + "}" +
                                curBillselect.getString(165) + "}" + curBillselect.getString(166) + "}" + curBillselect.getString(167) + "}" +
                                curBillselect.getString(168) + "}" + curBillselect.getString(169) + "}" + curBillselect.getString(170) + "}" +
                                curBillselect.getString(171) + "}" + curBillselect.getString(172) + "}" + curBillselect.getString(173) + "}" +
                                curBillselect.getString(174) + "}" + curBillselect.getString(175) + "}" + curBillselect.getString(176) + "}" +
                                curBillselect.getString(177) + "}" + curBillselect.getString(178) + "}" + curBillselect.getString(179) + "}" +
                                curBillselect.getString(180) + "}" + curBillselect.getString(181) + "}" + curBillselect.getString(182) + "}" +
                                curBillselect.getString(183) + "}" + curBillselect.getString(184) + "}" + curBillselect.getString(185) + "}" +
                                curBillselect.getString(186) + "}" + curBillselect.getString(187) + "}" + curBillselect.getString(188) + "}" +
                                curBillselect.getString(189) + "}" + curBillselect.getString(190) + "}" + curBillselect.getString(191) + "}" +
                                curBillselect.getString(192) + "}" + curBillselect.getString(193) + "}" + curBillselect.getString(194) + "}" +
                                curBillselect.getString(195) + "}" + curBillselect.getString(196) + "}" + curBillselect.getString(197) + "}" +
                                curBillselect.getString(198) + "}" + curBillselect.getString(199) + "}" + curBillselect.getString(200) + "}" +
                                curBillselect.getString(201) + "}" + curBillselect.getString(202) + "}" + curBillselect.getString(203) + "}" +
                                curBillselect.getString(204) + "}" + curBillselect.getString(205) + "}" + curBillselect.getString(206) + "}" +
                                curBillselect.getString(207) + "}" + curBillselect.getString(208) + "}" + curBillselect.getString(209) + "}" +
                                curBillselect.getString(210) + "}" + curBillselect.getString(211)+ "}" + curBillselect.getString(212)+ "}" +
                                curBillselect.getString(213)+ "}" + curBillselect.getString(214)+ "}" + curBillselect.getString(215)+ "}"+
                                curBillselect.getString(216));



                        createfolder(ZipImgCountPath + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount);
                        moveFile(ZipSourcePath, curBillselect.getString(48), ZipImgCountPath + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");
                        moveFile(ZipSourcePath, curBillselect.getString(49), ZipImgCountPath + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");

                        curBillselect.moveToNext();

                    }
                    for (int i = 0; i <= counter / globalcount; i++) {

                        selectZipFolder(ZipImgCountPath + "/" + name + GSBilling.getInstance().captureDate() + "_" + i+"/", ZipImgCountPath + "/" + name + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                    }
                    generateNoteOnSD(getApplicationContext(), "billing.csv", mylist);
                    generateNoteOnSD(getApplicationContext(), "billing1.csv", mylist1);

                }

                BillingtypesActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progress.dismiss();
                        new BillingtypesActivity.PostClass(BillingtypesActivity.this).execute();
                    }
                });


            } catch (NullPointerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute() {
            progress.dismiss();
            // new PostClass(BillingtypesActivity.this).execute();
        }

    }
    /*--------------------- Billing Uplaod Upload billing File  ----------------------------------*/
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

            zipFolder(ZipBillCSVPath, ZipDesPath);
            System.out.println("++++++++" + ZipDesPathdup);
            GSBilling.getInstance().setFinalZipName(ZipDesPathdup);

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip");
                HttpFileUpload hfu = new HttpFileUpload(URLS.DataComm.billUpload, "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                 HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");

                int status = hfu.Send_Now(fstrm);
                // sendFileToServer sendnowFile = new sendFileToServer();
                // sendnowFile.sendFileToServer(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip","http://enserv.feedbackinfra.com/Webapi_Testing/api/UploadFile/UploadFiles"+"" + GSBilling.getInstance().getFinalZipName()+".zip");
                if (status != 200) {

                    BillingtypesActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();

                            Toast.makeText(BillingtypesActivity.this, "Internaly Stored Due to No Connectivity", Toast.LENGTH_LONG).show();

                        }
                    });

                } else {
                    BillingtypesActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            dbHelper2 = new DB(getApplicationContext());
                            SD2 = dbHelper2.getWritableDatabase();

                            String updatequer = "UPDATE  TBL_BILLING  SET Upload_Flag = 'Y'";// WHERE  Cons_Number = '" + fileNames[image].split("_")[1] + "';";

                            Cursor update = SD2.rawQuery(updatequer, null);
                            if (update != null && update.moveToFirst()) {
                                android.util.Log.v("Update ", "Success");
                            }

                            File file = new File(ZipImgCountPath);
                            int filecount = CountRecursive(file);

                            for (int i = 0; i < filecount; i++) {

                                FileInputStream fstrm = null;
                                try {
                                    fstrm = new FileInputStream(ZipImgLimitPath + name + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                                    FileUpload hfu = new FileUpload("http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles", "" + ZipImgLimitPath + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");
//                                     FileUpload hfu = new FileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + ZipImgLimitPath + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");

                                    int status = hfu.Send_Now(fstrm);
                                    if (status != 200) {
                                    } else {

                                    }
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            progress.dismiss();
                            // new UpdateUI().execute();
                            Toast.makeText(BillingtypesActivity.this, "Successfully Uploaded to Server", Toast.LENGTH_LONG).show();
                            checkPendingRecord();

                        }
                    });
                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        protected void onPostExecute() {
            progress.dismiss();
            new File(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip").delete();

        }

    }


    /*--------------------- Billing Uplaod Text File Creation ----------------------------------*/
    private class TextFileClassFull extends AsyncTask<String, Void, Void> {

        private final Context context;

        public TextFileClassFull(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {
            sDialog = new SweetAlertDialog(BillingtypesActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            sDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            sDialog.setTitleText("Loading");
            sDialog.setCancelable(false);
            sDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                dbHelper2 = new DB(getApplicationContext());
                SD2 = dbHelper2.getWritableDatabase();

                String selquer = "SELECT * FROM TBL_BILLING";//WHERE Upload_Flag='N'
                String selColQuer = "SELECT * FROM TBL_COLMASTER_MP WHERE Upload_Flag='N'";//WHERE Upload_Flag='N'
                Cursor curBillselect = SD2.rawQuery(selquer, null);
                String arrStr[] = null;
                ArrayList<String> mylist = new ArrayList<String>();
                ArrayList<String> mylist1 = new ArrayList<String>();

                // Removefolder(ZipImgLimitPath);
                if (curBillselect != null && curBillselect.moveToFirst()) {
                    int counter = 0;
                    int iterate = 0;
                    while (curBillselect.isAfterLast() == false) {
                        counter++;
                        Calendar c = Calendar.getInstance();
                        System.out.println("Current time => " + c.getTime());
                        iterate=iterate+1;
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yy");
                        String formattedDate = df.format(c.getTime());
//                        String name = curBillselect.getString(0);
                        counter++;
                        // String name = curBillselect.getString(0);
                        String column_24=String.valueOf(Double.valueOf(curBillselect.getString(24))+Double.valueOf(curBillselect.getString(31)));


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
                                curBillselect.getString(36) + "}" + curBillselect.getString(37) + "}" + curBillselect.getString(38) + "}" +
                                curBillselect.getString(39) + "}" + curBillselect.getString(40) + "}" + curBillselect.getString(41) + "}" +
                                curBillselect.getString(42) + "}" + curBillselect.getString(43) + "}" + curBillselect.getString(44) + "}" +
                                curBillselect.getString(45) + "}" + curBillselect.getString(46) + "}" + curBillselect.getString(47) + "}" +
                                curBillselect.getString(48) + "}" + curBillselect.getString(49) + "}" + curBillselect.getString(50) + "}" +
                                curBillselect.getString(51) + "}" + curBillselect.getString(52) + "}" + curBillselect.getString(53) + "}" +
                                curBillselect.getString(54) + "}" + curBillselect.getString(55) + "}" + curBillselect.getString(56) + "}" +
                                curBillselect.getString(57) + "}" + curBillselect.getString(58) + "}" + curBillselect.getString(59) + "}" +
                                curBillselect.getString(60) + "}" + curBillselect.getString(61) + "}" + curBillselect.getString(62) + "}" +
                                curBillselect.getString(63) + "}" + curBillselect.getString(64) + "}" + curBillselect.getString(65) + "}" +
                                curBillselect.getString(66) + "}" + curBillselect.getString(67) + "}" + curBillselect.getString(68) + "}" +
                                curBillselect.getString(69) + "}" + curBillselect.getString(70) + "}" + curBillselect.getString(71) + "}" +
                                curBillselect.getString(72) + "}" + curBillselect.getString(73) + "}" + curBillselect.getString(74) + "}" +
                                curBillselect.getString(75) + "}" + curBillselect.getString(76) + "}" + curBillselect.getString(77) + "}" +
                                curBillselect.getString(78) + "}" + curBillselect.getString(79) + "}" + curBillselect.getString(80) + "}" +
                                curBillselect.getString(81) + "}" + curBillselect.getString(82) + "}" + curBillselect.getString(83) + "}" +
                                curBillselect.getString(84) + "}" + curBillselect.getString(85) + "}" + curBillselect.getString(86) + "}" +
                                curBillselect.getString(87) + "}" + curBillselect.getString(88) + "}" + curBillselect.getString(89) + "}" +
                                curBillselect.getString(90) + "}" + curBillselect.getString(91) + "}" + curBillselect.getString(92) + "}" +
                                curBillselect.getString(93) + "}" + curBillselect.getString(94) + "}" + curBillselect.getString(95) + "}" +
                                curBillselect.getString(96) + "}" + curBillselect.getString(97) + "}" + curBillselect.getString(98) + "}" +
                                curBillselect.getString(99) + "}" + curBillselect.getString(100) + "}" + curBillselect.getString(101) + "}" +
                                curBillselect.getString(102) + "}" + curBillselect.getString(103) + "}" + curBillselect.getString(104) + "}" +
                                curBillselect.getString(105) + "}" + curBillselect.getString(106) + "}" + curBillselect.getString(107) + "}" +
                                curBillselect.getString(108) + "}" + curBillselect.getString(109) + "}" + curBillselect.getString(110) + "}" +
                                curBillselect.getString(111) + "}" + curBillselect.getString(112) + "}" + curBillselect.getString(113) + "}" +
                                curBillselect.getString(114) + "}" + curBillselect.getString(115) + "}" + curBillselect.getString(116) + "}" +
                                curBillselect.getString(117) + "}" + curBillselect.getString(118) + "}" + curBillselect.getString(119) + "}" +
                                curBillselect.getString(120) + "}" + curBillselect.getString(121) + "}" + curBillselect.getString(122) + "}" +
                                curBillselect.getString(123) + "}" + curBillselect.getString(124) + "}" + curBillselect.getString(125) + "}" +
                                curBillselect.getString(126) + "}" + curBillselect.getString(127) + "}" + curBillselect.getString(128) + "}" +
                                curBillselect.getString(129) + "}" + curBillselect.getString(130) + "}" + curBillselect.getString(131) + "}" +
                                curBillselect.getString(132) + "}" + curBillselect.getString(133) + "}" + curBillselect.getString(134) + "}" +
                                curBillselect.getString(135) + "}" + curBillselect.getString(136) + "}" + curBillselect.getString(137) + "}" +
                                curBillselect.getString(138) + "}" + curBillselect.getString(139) + "}" + curBillselect.getString(140) + "}" +
                                curBillselect.getString(141) + "}" + curBillselect.getString(142) + "}" + curBillselect.getString(143) + "}" +
                                curBillselect.getString(144) + "}" + curBillselect.getString(145) + "}" + curBillselect.getString(146) + "}" +
                                curBillselect.getString(147) + "}" + curBillselect.getString(148) + "}" + curBillselect.getString(149) + "}" +
                                curBillselect.getString(150) + "}" + curBillselect.getString(151) + "}" + curBillselect.getString(152) + "}" +
                                curBillselect.getString(153) + "}" + curBillselect.getString(154) + "}" + curBillselect.getString(155) + "}" +
                                curBillselect.getString(156) + "}" + curBillselect.getString(157) + "}" + curBillselect.getString(158) + "}" +
                                curBillselect.getString(159) + "}" + curBillselect.getString(160) + "}" + curBillselect.getString(161) + "}" +
                                curBillselect.getString(162) + "}" + curBillselect.getString(163) + "}" + curBillselect.getString(164) + "}" +
                                curBillselect.getString(165) + "}" + curBillselect.getString(166) + "}" + curBillselect.getString(167) + "}" +
                                curBillselect.getString(168) + "}" + curBillselect.getString(169) + "}" + curBillselect.getString(170) + "}" +
                                curBillselect.getString(171) + "}" + curBillselect.getString(172) + "}" + curBillselect.getString(173) + "}" +
                                curBillselect.getString(174) + "}" + curBillselect.getString(175) + "}" + curBillselect.getString(176) + "}" +
                                curBillselect.getString(177) + "}" + curBillselect.getString(178) + "}" + curBillselect.getString(179) + "}" +
                                curBillselect.getString(180) + "}" + curBillselect.getString(181) + "}" + curBillselect.getString(182) + "}" +
                                curBillselect.getString(183) + "}" + curBillselect.getString(184) + "}" + curBillselect.getString(185) + "}" +
                                curBillselect.getString(186) + "}" + curBillselect.getString(187) + "}" + curBillselect.getString(188) + "}" +
                                curBillselect.getString(189) + "}" + curBillselect.getString(190) + "}" + curBillselect.getString(191) + "}" +
                                curBillselect.getString(192) + "}" + curBillselect.getString(193) + "}" + curBillselect.getString(194) + "}" +
                                curBillselect.getString(195) + "}" + curBillselect.getString(196) + "}" + curBillselect.getString(197) + "}" +
                                curBillselect.getString(198) + "}" + curBillselect.getString(199) + "}" + curBillselect.getString(200) + "}" +
                                curBillselect.getString(201) + "}" + curBillselect.getString(202) + "}" + curBillselect.getString(203) + "}" +
                                curBillselect.getString(204) + "}" + curBillselect.getString(205) + "}" + curBillselect.getString(206) + "}" +
                                curBillselect.getString(207) + "}" + curBillselect.getString(208) + "}" + curBillselect.getString(209) + "}" +
                                curBillselect.getString(210) + "}" + curBillselect.getString(211)+ "}" + curBillselect.getString(212)+ "}" +
                                curBillselect.getString(213)+ "}" + curBillselect.getString(214)+ "}" + curBillselect.getString(215)+ "}"+
                                curBillselect.getString(216));

                        mylist1.add(curBillselect.getString(60)+ "$" + curBillselect.getString(0) + "$" + curBillselect.getString(5) + "$" +
                                curBillselect.getString(61)+ "$" + curBillselect.getString(11)+ "$" + curBillselect.getString(62)+ "$" +
                                curBillselect.getString(2) + "$" + iterate + "$" + curBillselect.getString(7) + "$" +
                                curBillselect.getString(212)+ "$" + curBillselect.getString(1) + "$" + formattedDate + "$" +
                                curBillselect.getString(63)+ "$" + curBillselect.getString(8) + "$" + curBillselect.getString(64)+ "$" +
                                curBillselect.getString(65)+ "$" + curBillselect.getString(66)+ "$" + curBillselect.getString(39)+ "$" +
                                curBillselect.getString(10)+ "$" + curBillselect.getString(67)+ "$" + curBillselect.getString(68)+ "$" +
                                curBillselect.getString(69)+ "$" + curBillselect.getString(70)+ "$" + curBillselect.getString(71)+ "$" +
                                curBillselect.getString(182) + "$" + curBillselect.getString(72)+ "$" + curBillselect.getString(73)+ "$" +
                                column_24+ "$" + curBillselect.getString(74)+ "$" + "0"+ "$" + curBillselect.getString(75)+ "$" +
                                curBillselect.getString(76)+ "$" + curBillselect.getString(27)+ "$" + curBillselect.getString(77)+ "$" +
                                curBillselect.getString(26)+ "$" + curBillselect.getString(78)+ "$" + curBillselect.getString(79)+ "$" +
                                curBillselect.getString(80)+ "$" + curBillselect.getString(81)+ "$" + curBillselect.getString(82)+ "$" +
                                curBillselect.getString(201) + "$" + curBillselect.getString(83)+ "$" + curBillselect.getString(179)+ "$" +
                                curBillselect.getString(85)+ "$" + curBillselect.getString(86)+ "$" + curBillselect.getString(169) + "$" +
                                curBillselect.getString(87)+ "$" + curBillselect.getString(88)+ "$" + curBillselect.getString(89)+ "$" +
                                curBillselect.getString(90)+ "$" + curBillselect.getString(91)+ "$" + curBillselect.getString(92)+ "$" +
                                curBillselect.getString(93)+ "$" + curBillselect.getString(94)+ "$" + curBillselect.getString(95)+ "$" +
                                curBillselect.getString(96)+ "$" + curBillselect.getString(97)+ "$" + curBillselect.getString(98)+ "$" +
                                curBillselect.getString(99)+ "$" + curBillselect.getString(178) + "$0$" );

                        createfolder(ZipImgCountPath + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount);
                        moveFile(ZipSourcePath, curBillselect.getString(48), ZipImgCountPath + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");
                        moveFile(ZipSourcePath, curBillselect.getString(49), ZipImgCountPath + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");

                        curBillselect.moveToNext();

                    }
                    for (int i = 0; i <= counter / globalcount; i++) {

                        selectZipFolder(ZipImgCountPath + "/" + name + GSBilling.getInstance().captureDate() + "_" + i+"/", ZipImgCountPath + "/" + name + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                    }
                    generateNoteOnSD(getApplicationContext(), "billing.csv", mylist);
                    generateNoteOnSD(getApplicationContext(), "billing1.csv", mylist1);

                }
                String bckquer = "SELECT * FROM TBL_BILLING ";//WHERE Upload_Flag='N'
                Cursor curBbckquer = SD2.rawQuery(bckquer, null);
                String arrStr3[] = null;
                ArrayList<String> mylist3 = new ArrayList<String>();
                ArrayList<String> mylist4 = new ArrayList<String>();

                if (curBbckquer != null && curBbckquer.moveToFirst()) {
                    int iterate=0;
                    while (curBbckquer.isAfterLast() == false) {
                        Calendar c = Calendar.getInstance();
                        System.out.println("Current time => " + c.getTime());
                        iterate=iterate+1;
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yy");
                        String formattedDate = df.format(c.getTime());
                        String column_24=String.valueOf(Double.valueOf(curBbckquer.getString(24))+Double.valueOf(curBbckquer.getString(31)));
                        mylist3.add(curBbckquer.getString(0) + "}" + curBbckquer.getString(1) + "}" + curBbckquer.getString(2) + "}" +
                                curBbckquer.getString(3) + "}" + curBbckquer.getString(4) + "}" + curBbckquer.getString(5) + "}" +
                                curBbckquer.getString(6) + "}" + curBbckquer.getString(7) + "}" + curBbckquer.getString(8) + "}" +
                                curBbckquer.getString(9) + "}" + curBbckquer.getString(10) + "}" +curBbckquer.getString(11) + "}" +
                                curBbckquer.getString(12) + "}" + curBbckquer.getString(13) + "}" + curBbckquer.getString(14) + "}" +
                                curBbckquer.getString(15) + "}" + curBbckquer.getString(16) + "}" + curBbckquer.getString(17) + "}" +
                                curBbckquer.getString(18) + "}" + curBbckquer.getString(19) + "}" + curBbckquer.getString(20) + "}" +
                                curBbckquer.getString(21) + "}" + curBbckquer.getString(22) + "}" + curBbckquer.getString(23) + "}" +
                                curBbckquer.getString(24) + "}" + curBbckquer.getString(25) + "}" + curBbckquer.getString(26) + "}" +
                                curBbckquer.getString(27) + "}" + curBbckquer.getString(28) + "}" + curBbckquer.getString(29) + "}" +
                                curBbckquer.getString(30) + "}" + curBbckquer.getString(31) + "}" + curBbckquer.getString(32) + "}" +
                                curBbckquer.getString(33) + "}" + curBbckquer.getString(34) + "}" + curBbckquer.getString(35) + "}" +
                                curBbckquer.getString(36) + "}" + curBbckquer.getString(37) + "}" + curBbckquer.getString(38) + "}" +
                                curBbckquer.getString(39) + "}" + curBbckquer.getString(40) + "}" + curBbckquer.getString(41) + "}" +
                                curBbckquer.getString(42) + "}" + curBbckquer.getString(43) + "}" + curBbckquer.getString(44) + "}" +
                                curBbckquer.getString(45) + "}" + curBbckquer.getString(46) + "}" + curBbckquer.getString(47) + "}" +
                                curBbckquer.getString(48) + "}" + curBbckquer.getString(49) + "}" + curBbckquer.getString(50) + "}" +
                                curBbckquer.getString(51) + "}" + curBbckquer.getString(52) + "}" + curBbckquer.getString(53) + "}" +
                                curBbckquer.getString(54) + "}" + curBbckquer.getString(55) + "}" + curBbckquer.getString(56) + "}" +
                                curBbckquer.getString(57) + "}" + curBbckquer.getString(58) + "}" + curBbckquer.getString(59) + "}" +
                                curBbckquer.getString(60) + "}" + curBbckquer.getString(61) + "}" + curBbckquer.getString(62) + "}" +
                                curBbckquer.getString(63) + "}" + curBbckquer.getString(64) + "}" + curBbckquer.getString(65) + "}" +
                                curBbckquer.getString(66) + "}" + curBbckquer.getString(67) + "}" + curBbckquer.getString(68) + "}" +
                                curBbckquer.getString(69) + "}" + curBbckquer.getString(70) + "}" + curBbckquer.getString(71) + "}" +
                                curBbckquer.getString(72) + "}" + curBbckquer.getString(73) + "}" + curBbckquer.getString(74) + "}" +
                                curBbckquer.getString(75) + "}" + curBbckquer.getString(76) + "}" + curBbckquer.getString(77) + "}" +
                                curBbckquer.getString(78) + "}" + curBbckquer.getString(79) + "}" + curBbckquer.getString(80) + "}" +
                                curBbckquer.getString(81) + "}" + curBbckquer.getString(82) + "}" + curBbckquer.getString(83) + "}" +
                                curBbckquer.getString(84) + "}" + curBbckquer.getString(85) + "}" + curBbckquer.getString(86) + "}" +
                                curBbckquer.getString(87) + "}" + curBbckquer.getString(88) + "}" + curBbckquer.getString(89) + "}" +
                                curBbckquer.getString(90) + "}" + curBbckquer.getString(91) + "}" + curBbckquer.getString(92) + "}" +
                                curBbckquer.getString(93) + "}" + curBbckquer.getString(94) + "}" + curBbckquer.getString(95) + "}" +
                                curBbckquer.getString(96) + "}" + curBbckquer.getString(97) + "}" + curBbckquer.getString(98) + "}" +
                                curBbckquer.getString(99) + "}" + curBbckquer.getString(100) + "}" + curBbckquer.getString(101) + "}" +
                                curBbckquer.getString(102) + "}" + curBbckquer.getString(103) + "}" + curBbckquer.getString(104) + "}" +
                                curBbckquer.getString(105) + "}" + curBbckquer.getString(106) + "}" + curBbckquer.getString(107) + "}" +
                                curBbckquer.getString(108) + "}" + curBbckquer.getString(109) + "}" + curBbckquer.getString(110) + "}" +
                                curBbckquer.getString(111) + "}" + curBbckquer.getString(112) + "}" + curBbckquer.getString(113) + "}" +
                                curBbckquer.getString(114) + "}" + curBbckquer.getString(115) + "}" + curBbckquer.getString(116) + "}" +
                                curBbckquer.getString(117) + "}" + curBbckquer.getString(118) + "}" + curBbckquer.getString(119) + "}" +
                                curBbckquer.getString(120) + "}" + curBbckquer.getString(121) + "}" + curBbckquer.getString(122) + "}" +
                                curBbckquer.getString(123) + "}" + curBbckquer.getString(124) + "}" + curBbckquer.getString(125) + "}" +
                                curBbckquer.getString(126) + "}" + curBbckquer.getString(127) + "}" + curBbckquer.getString(128) + "}" +
                                curBbckquer.getString(129) + "}" + curBbckquer.getString(130) + "}" + curBbckquer.getString(131) + "}" +
                                curBbckquer.getString(132) + "}" + curBbckquer.getString(133) + "}" + curBbckquer.getString(134) + "}" +
                                curBbckquer.getString(135) + "}" + curBbckquer.getString(136) + "}" + curBbckquer.getString(137) + "}" +
                                curBbckquer.getString(138) + "}" + curBbckquer.getString(139) + "}" + curBbckquer.getString(140) + "}" +
                                curBbckquer.getString(141) + "}" + curBbckquer.getString(142) + "}" + curBbckquer.getString(143) + "}" +
                                curBbckquer.getString(144) + "}" + curBbckquer.getString(145) + "}" + curBbckquer.getString(146) + "}" +
                                curBbckquer.getString(147) + "}" + curBbckquer.getString(148) + "}" + curBbckquer.getString(149) + "}" +
                                curBbckquer.getString(150) + "}" + curBbckquer.getString(151) + "}" + curBbckquer.getString(152) + "}" +
                                curBbckquer.getString(153) + "}" + curBbckquer.getString(154) + "}" + curBbckquer.getString(155) + "}" +
                                curBbckquer.getString(156) + "}" + curBbckquer.getString(157) + "}" + curBbckquer.getString(158) + "}" +
                                curBbckquer.getString(159) + "}" + curBbckquer.getString(160) + "}" + curBbckquer.getString(161) + "}" +
                                curBbckquer.getString(162) + "}" + curBbckquer.getString(163) + "}" + curBbckquer.getString(164) + "}" +
                                curBbckquer.getString(165) + "}" + curBbckquer.getString(166) + "}" + curBbckquer.getString(167) + "}" +
                                curBbckquer.getString(168) + "}" + curBbckquer.getString(169) + "}" + curBbckquer.getString(170) + "}" +
                                curBbckquer.getString(171) + "}" + curBbckquer.getString(172) + "}" + curBbckquer.getString(173) + "}" +
                                curBbckquer.getString(174) + "}" + curBbckquer.getString(175) + "}" + curBbckquer.getString(176) + "}" +
                                curBbckquer.getString(177) + "}" + curBbckquer.getString(178) + "}" + curBbckquer.getString(179) + "}" +
                                curBbckquer.getString(180) + "}" + curBbckquer.getString(181) + "}" + curBbckquer.getString(182) + "}" +
                                curBbckquer.getString(183) + "}" + curBbckquer.getString(184) + "}" + curBbckquer.getString(185) + "}" +
                                curBbckquer.getString(186) + "}" + curBbckquer.getString(187) + "}" + curBbckquer.getString(188) + "}" +
                                curBbckquer.getString(189) + "}" + curBbckquer.getString(190) + "}" + curBbckquer.getString(191) + "}" +
                                curBbckquer.getString(192) + "}" + curBbckquer.getString(193) + "}" + curBbckquer.getString(194) + "}" +
                                curBbckquer.getString(195) + "}" + curBbckquer.getString(196) + "}" + curBbckquer.getString(197) + "}" +
                                curBbckquer.getString(198) + "}" + curBbckquer.getString(199) + "}" + curBbckquer.getString(200) + "}" +
                                curBbckquer.getString(201) + "}" + curBbckquer.getString(202) + "}" + curBbckquer.getString(203) + "}" +
                                curBbckquer.getString(204) + "}" + curBbckquer.getString(205) + "}" + curBbckquer.getString(206) + "}" +
                                curBbckquer.getString(207) + "}" + curBbckquer.getString(208) + "}" + curBbckquer.getString(209) + "}" +
                                curBbckquer.getString(210) + "}" + curBbckquer.getString(211)+ "}" + curBbckquer.getString(212)+ "}" +
                                curBbckquer.getString(213)+ "}" + curBbckquer.getString(214)+ "}" + curBbckquer.getString(215)+ "}" +
                                curBbckquer.getString(216));

                        mylist4.add(curBbckquer.getString(60)+ "$" + curBbckquer.getString(0) + "$" + curBbckquer.getString(5) + "$" +
                                curBbckquer.getString(61)+ "$" + curBbckquer.getString(11)+ "$" + curBbckquer.getString(62)+ "$" +
                                curBbckquer.getString(2) + "$" + iterate + "$" + curBbckquer.getString(7) + "$" +
                                curBbckquer.getString(212)+ "$" + curBbckquer.getString(1) + "$" + formattedDate + "$" +
                                curBbckquer.getString(63)+ "$" + curBbckquer.getString(8) + "$" + curBbckquer.getString(64)+ "$" +
                                curBbckquer.getString(65)+ "$" + curBbckquer.getString(66)+ "$" + curBbckquer.getString(39)+ "$" +
                                curBbckquer.getString(10)+ "$" + curBbckquer.getString(67)+ "$" + curBbckquer.getString(68)+ "$" +
                                curBbckquer.getString(69)+ "$" + curBbckquer.getString(70)+ "$" + curBbckquer.getString(71)+ "$" +
                                curBbckquer.getString(182) + "$" + curBbckquer.getString(72)+ "$" + curBbckquer.getString(73)+ "$" +
                                column_24+ "$" + curBbckquer.getString(74)+ "$" + "0"+ "$" + curBbckquer.getString(75)+ "$" +
                                curBbckquer.getString(76)+ "$" + curBbckquer.getString(27)+ "$" + curBbckquer.getString(77)+ "$" +
                                curBbckquer.getString(26)+ "$" + curBbckquer.getString(78)+ "$" + curBbckquer.getString(79)+ "$" +
                                curBbckquer.getString(80)+ "$" + curBbckquer.getString(81)+ "$" + curBbckquer.getString(82)+ "$" +
                                curBbckquer.getString(201) + "$" + curBbckquer.getString(83)+ "$" + curBbckquer.getString(179)+ "$" +
                                curBbckquer.getString(85)+ "$" + curBbckquer.getString(86)+ "$" + curBbckquer.getString(169) + "$" +
                                curBbckquer.getString(87)+ "$" + curBbckquer.getString(88)+ "$" + curBbckquer.getString(89)+ "$" +
                                curBbckquer.getString(90)+ "$" + curBbckquer.getString(91)+ "$" + curBbckquer.getString(92)+ "$" +
                                curBbckquer.getString(93)+ "$" + curBbckquer.getString(94)+ "$" + curBbckquer.getString(95)+ "$" +
                                curBbckquer.getString(96)+ "$" + curBbckquer.getString(97)+ "$" + curBbckquer.getString(98)+ "$" +
                                curBbckquer.getString(99)+ "$" + curBbckquer.getString(178) + "$0$" );
                        curBbckquer.moveToNext();
                    }
//                    genBck(getApplicationContext(), "bill_bck.csv", mylist3);
//                    genBck(getApplicationContext(), "billing1.csv", mylist4);

                }

                BillingtypesActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        sDialog.dismiss();
                        new PostClassFull(BillingtypesActivity.this).execute();
                    }
                });


            } catch (NullPointerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute() {
            sDialog.dismiss();
            // new PostClass(SDActivity.this).execute();
        }

    }
    /*--------------------- Billing Uplaod Upload billing File  ----------------------------------*/
    private class PostClassFull extends AsyncTask<String, Void, Boolean> {

        private final Context context;
        public String succsess = null;

        public PostClassFull(Context c) {

            this.context = c;
        }

        protected void onPreExecute() {
            sDialog = new SweetAlertDialog(BillingtypesActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            sDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            sDialog.setTitleText("Loading");
            sDialog.setCancelable(false);
            sDialog.show();

            zipFolder(ZipBillCSVPath, ZipDesPath);
            System.out.println("++++++++" + ZipDesPathdup);
            GSBilling.getInstance().setFinalZipName(ZipDesPathdup);

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                FileInputStream fstrm = new FileInputStream(GSBilling.getInstance().getFinalZipName() + ".zip");
                HttpFileUpload hfu = new HttpFileUpload(URLS.DataComm.billUpload, "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                 HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");

                int status = hfu.Send_Now(fstrm);
                // sendFileToServer sendnowFile = new sendFileToServer();
                // sendnowFile.sendFileToServer(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip","http://enserv.feedbackinfra.com/Webapi_Testing/api/UploadFile/UploadFiles"+"" + GSBilling.getInstance().getFinalZipName()+".zip");
                if (status != 200) {

                    BillingtypesActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            sDialog.dismiss();

                            Toast.makeText(BillingtypesActivity.this, "Internaly Stored Due to No Connectivity", Toast.LENGTH_LONG).show();

                        }
                    });

                } else {
//                    BillingtypesActivity.this.runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
                    dbHelper2 = new DB(getApplicationContext());
                    SD2 = dbHelper2.getWritableDatabase();

                    String updatequer = "UPDATE  TBL_BILLING  SET Upload_Flag = 'Y'";// WHERE  Cons_Number = '" + fileNames[image].split("_")[1] + "';";

                    Cursor update = SD2.rawQuery(updatequer, null);
                    if (update != null && update.moveToFirst()) {

                    }

                    File file = new File(ZipImgCountPath);
                    int filecount = CountRecursive(file);

                    for (int i = 0; i < filecount; i++) {

                        FileInputStream fstrmImage = null;
                        try {
                            fstrmImage = new FileInputStream(ZipImgLimitPath + name +
                                    GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                            HttpFileUpload hfuImage = new HttpFileUpload(URLS.DataComm
                                    .billUpload, "" + ZipImgLimitPath + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");
//                                     FileUpload hfu = new FileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + ZipImgLimitPath + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");

                            int statusImage = hfuImage.Send_Now(fstrmImage);
                            if (statusImage != 200) {
                            } else {

//                                        String[] fileNames = new String[0];
//                                        File fileImage = new File(ZipImgLimitPath + name + GSBilling.getInstance().captureDate() + "_" + i + "/");
//                                        if (fileImage.isDirectory()) {
//                                            fileNames = fileImage.list();
//                                        }
//                                        int total = 0;
//                                        for (int image = 0; image < fileNames.length; image++) {
//                                            if (fileNames[image].contains("_mtr.jpg")) {
//
//                                                dbHelper2 = new DB(getApplicationContext());
//                                                SD2 = dbHelper2.getWritableDatabase();
//
//                                                String updatequer = "UPDATE  TBL_BILLING  SET Upload_Flag = 'Y' WHERE  Cons_Number = '" + fileNames[image].split("_")[1] + "';";
//
//                                                Cursor update = SD2.rawQuery(updatequer, null);
//                                                if (update != null && update.moveToFirst()) {
//                                                    Log.v("Update ", "Success");
//                                                }
//                                            }
//                                        }
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    BillingtypesActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            sDialog.dismiss();

                            Toast.makeText(BillingtypesActivity.this, "Successfully Uploaded to Server", Toast.LENGTH_LONG).show();

                        }
                    });

//                        }
//                    });
                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        protected void onPostExecute() {
            sDialog.dismiss();
            new File(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip").delete();

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

    public  void zipFolder(String inputFolderPath, String outZipPath) {
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
            Toast.makeText(context, "Unable to upload", Toast.LENGTH_SHORT).show();
            dismissProgressDialog();
        }catch (NullPointerException ioe) {
            android.util.Log.e("", ioe.getMessage());
            Toast.makeText(context, "Unable to upload", Toast.LENGTH_SHORT).show();
            dismissProgressDialog();
        }
    }
    public void createfolder(String outputPath) {
        //create output directory if it doesn't exist
        File dir = new File(outputPath);
        if (!dir.exists()) {
            dir.mkdirs();
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

            // delete the original file
            // new File(inputPath + inputFile).delete();


        } catch (FileNotFoundException e) {
//            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void selectZipFolder(String inputFolderPath, String outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outZipPath);
            // GSBilling.getInstance().setFinalZipName();
            ZipOutputStream zos = new ZipOutputStream(fos);
            File srcFile = new File(inputFolderPath);

            File[] files = srcFile.listFiles();

            for (int i = 0; i < files.length; i++) {

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
            zos.close();
        } catch (IOException ioe) {

        }
    }
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        ((GlobalPool)getApplication()).onUserInteraction();
    }

    @Override
    public void userLogoutListaner() {
        finish();
        Intent intent=new Intent(BillingtypesActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
    public int CountRecursive(File dir) {
        String[] fileNames;
        fileNames = dir.list();
        int total = 0;
        for (int i = 0; i < fileNames.length; i++) {
            if (fileNames[i].contains(".zip")) {
                total++;
            }
        }
        return total;
    }

}