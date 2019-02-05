package com.fedco.mbc.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.bluetoothprinting.GlobalPool;
import com.fedco.mbc.model.Structbilling;
import com.fedco.mbc.model.Structcolmas;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.model.Structmeterupload;
import com.fedco.mbc.replacement.Replacement;
import com.fedco.mbc.service.SyncService;
import com.fedco.mbc.service.UploadService;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.activitysurvey.Survey;
import com.fedco.mbc.utils.UtilAppCommon;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import java.io.File;
import java.util.HashMap;


public class Home extends Activity implements OnMenuItemClickListener,LogoutListaner {

    ImageButton IBtnBilling, IBtnMetering, IBtnCollection, IBtnSurvey, IBtnRepalce;
    TextView texViewVer, textViewLic, textViewBillMon;

    UtilAppCommon uac;
    SessionManager session;
    SQLiteDatabase SD, SD4;
    DB dbHelper, dbHelper4;
    public static String Mflag ="N";

    // Session Manager Class
    public String ZipDesPath, ZipDesColPath;
    String ZipDesPathdup, ZipDesColPathdup;
    String name;
    int pendingcount = 0;
    int bill_Pending,col_Pending,met_Pending,consumer_Pending,dtr_Pending,feeder_Pending,pole_Pending,conmas_Value;
    private Boolean exit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the application environment
        setContentView(R.layout.activity_home);
        setTitle("SMARTPHED");

        // Session class instance
        session = new SessionManager(getApplicationContext());
        // Utill App common
        uac = new UtilAppCommon();
        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
//        session.checkLogin();
        ((GlobalPool)getApplication()).registerSessionListaner(this);
        ((GlobalPool)getApplication()).startUserSession();


        //create output directory if it doesn't exist
        File dir = new File(Environment.getExternalStorageDirectory() + "/MBC/Images/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        String email = user.get(SessionManager.KEY_EMAIL);
        String Licekey = session.retLicence();
        name = session.retLicence();

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        this.ZipDesPathdup = "/MBC/" + name + GSBilling.getInstance().captureDatetime();
        this.ZipDesPath    = Environment.getExternalStorageDirectory() + "/MBC/" + name + GSBilling.getInstance().captureDatetime() + ".zip";
        this.ZipDesColPathdup = "/MBC/" + name + GSBilling.getInstance().captureDatetime();
        this.ZipDesColPath    = Environment.getExternalStorageDirectory() + "/MBC/" + name + "_col_" + GSBilling.getInstance().captureDatetime() + ".zip";


        String version = pInfo.versionName;
        Structbilling.VER_CODE = version;
        Structcolmas.VER_CODE = version;
        Structmeterupload.VER_CODE = version;


        IBtnMetering = (ImageButton) findViewById(R.id.ImageButtonMetering);
        IBtnBilling = (ImageButton) findViewById(R.id.ImageButtonBilling);
        IBtnCollection = (ImageButton) findViewById(R.id.ImageButtonCollection);
        IBtnSurvey = (ImageButton) findViewById(R.id.ImageButtonSurvey);
        IBtnRepalce = (ImageButton) findViewById(R.id.ImageButtonReplacement);


        IBtnMetering.setEnabled(true);
        IBtnBilling.setEnabled(true);
        IBtnCollection.setEnabled(true);
//        IBtnRepalce.setVisibility(View.GONE);

        texViewVer = (TextView) findViewById(R.id.tvVer);
        textViewLic = (TextView) findViewById(R.id.tvLic);
        textViewBillMon = (TextView) findViewById(R.id.tvBillMon);

        textViewLic.setText(name);
        texViewVer.setText(version);
        textViewBillMon.setText(Structconsmas.Bill_Mon);

        dbHelper4 = new DB(getApplicationContext());
        SD4 = dbHelper4.getWritableDatabase();

//        SharedPreferences prefsCol = PreferenceManager.getDefaultSharedPreferences(Home.this);
//        if (!prefsCol.getBoolean("mpcollection", false)) {
//            // // run your one time code
//
//            databaseHelper = new DB(getApplicationContext());
//            SD = databaseHelper.getWritableDatabase();
//
//            databaseHelper.alterScript("MBC_MP_COLLECTION.sql");
//
//            SharedPreferences.Editor editor = prefsCol.edit();
//            editor.putBoolean("mpcollection", true);
//            editor.commit();
//        }


        String conmasVal    =  "SELECT  count(*) from 'TBL_CONSMAST'";
        String pendingBILL  =  "SELECT  count(*) from 'TBL_BILLING' WHERE Upload_Flag='N'";
        String pendingCOL   =  "SELECT  count(*) from 'TBL_COLMASTER_MP' WHERE Upload_Flag='N'";
        String pendingMET   =  "SELECT  count(*) from 'TBL_METERUPLOAD' WHERE UPLOADFLAG='N'";
        String pendingCON   =  "SELECT  count(*) from 'TBL_CONSUMERSURVEY_UPOLOAD' WHERE FLAG_UPLOAD='N'";
        String pendingFEED  =  "SELECT  count(*) from 'TBL_11KVFEEDER_UPLOAD' WHERE FLAG_UPLOAD='N'";
        String pendingDTR   =  "SELECT  count(*) from 'TBL_DTR_UPLOAD' WHERE FLAG_UPLOAD='N'";
        String pendingPOLE  =  "SELECT  count(*) from 'TBL_POLE_UPLOAD' WHERE FLAG_UPLOAD='N'";

        final Cursor curValConmas= SD4.rawQuery(conmasVal, null);
        final Cursor curPendBILL = SD4.rawQuery(pendingBILL, null);
        final Cursor curPendCOL  = SD4.rawQuery(pendingCOL, null);
        final Cursor curPendMET  = SD4.rawQuery(pendingMET, null);
        final Cursor curPendCON  = SD4.rawQuery(pendingCON, null);
        final Cursor curPendFEED = SD4.rawQuery(pendingFEED, null);
        final Cursor curPendDTR  = SD4.rawQuery(pendingDTR, null);
        final Cursor curPendPOLE = SD4.rawQuery(pendingPOLE, null);

        if(curPendBILL != null && curPendBILL.moveToFirst()){
            bill_Pending        = Integer.parseInt(curPendBILL.getString(0));
        } if(curPendCOL != null && curPendCOL.moveToFirst()){
            col_Pending        = Integer.parseInt(curPendCOL.getString(0));
        } if(curPendMET != null && curPendMET.moveToFirst()){
            met_Pending        = Integer.parseInt(curPendMET.getString(0));
        } if(curPendCON != null && curPendCON.moveToFirst()){
            consumer_Pending        = Integer.parseInt(curPendCON.getString(0));
        }if(curPendFEED != null && curPendFEED.moveToFirst()){
            dtr_Pending             = Integer.parseInt(curPendFEED.getString(0));
        }if(curPendDTR != null && curPendDTR.moveToFirst()){
            feeder_Pending          = Integer.parseInt(curPendDTR.getString(0));
        }if(curPendPOLE != null && curPendPOLE.moveToFirst()){
            pole_Pending            = Integer.parseInt(curPendPOLE.getString(0));
        }if(curValConmas != null && curValConmas.moveToFirst()){
            conmas_Value            = Integer.parseInt(curValConmas.getString(0));
        }

        curPendBILL .getCount();
        curPendCOL  .getCount();
        curPendMET  .getCount();
        curPendCON  .getCount();
        curPendFEED .getCount();
        curPendDTR  .getCount();
        curPendPOLE .getCount();

        if (bill_Pending > pendingcount || col_Pending > pendingcount || met_Pending > pendingcount ) {

//            Intent intent = new Intent(Home.this, BackgroundService.class);
//            startService(intent);

        }
        if (consumer_Pending > pendingcount || dtr_Pending > pendingcount || feeder_Pending > pendingcount|| pole_Pending > pendingcount ) {

            Intent intent = new Intent(Home.this, UploadService.class);
            startService(intent);

        }

        if (conmas_Value > pendingcount ) {

            Intent intent = new Intent(Home.this, SyncService.class);
            startService(intent);

        }

        IBtnMetering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Mflag ="Y";
                Intent intent = new Intent(getApplicationContext(), Billing.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);

            }
        });

        IBtnBilling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Billing.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);

            }
        });

        IBtnCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Collection.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);

            }
        });

        IBtnSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Survey.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });

        IBtnRepalce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Replacement.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        ((GlobalPool)getApplication()).onUserInteraction();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }



    public void onMenuItemClick(View clickedView, int position) {
        switch (position) {
            case 1:
                //  ShowAlert();
                break;

        }
    }



    public void onBackPressed() {

//        finish(); // finish activity
//        this.overridePendingTransition(R.anim.anim_slide_in_right,
//                R.anim.anim_slide_out_right);
        if (exit) {
//            bDialog.setCancelable(false);
            finish(); // finish activity
        } else {

            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }



    @Override
    public void userLogoutListaner() {

        finish();
        Intent intent=new Intent(Home.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}

