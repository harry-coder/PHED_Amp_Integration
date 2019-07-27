package com.fedco.mbc.activitymetering;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.activity.StartLocationAlert;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.model.Structmetering;
import com.fedco.mbc.model.Structmeterupload;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.FileUpload;
import com.fedco.mbc.utils.GPSTracker;
import com.fedco.mbc.utils.HttpFileUpload;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.fedco.mbc.activity.StartLocationAlert.REQUEST_CHECK_SETTINGS;

public class Remarks extends Activity {


    Button btn_Next;
    EditText et_Remark;
    String str_Remark;

    UtilAppCommon comApp;
    Boolean flag;

    GPSTracker gps, gps2;
    double latitude;
    double longitude;
    double latitude1 = 0.00;
    double longitude1 = 0.00;
    long gpstime;
    double altitude;
    double accuracy;
    LocationManager locationManager;
    Context mContext;
    StartLocationAlert startLocationAlert;

    Logger Log;

    SessionManager sessionManager;

    private ProgressDialog progress;
    DB dbHelper, dbHelper2, dbHelper4;

    SQLiteDatabase SD, SD2, SD3;
    String MRname;
    public int globalcount = 10;
    String ZipSourcePath = Environment.getExternalStorageDirectory() + "/MBC/Images/"; //All image source path
    String ZipCopyPath = Environment.getExternalStorageDirectory() + "/MBC/MeterUpload/";
    String ZipImgLimitPathMeter = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesMeter/"; //20 count image folder path
    String ZipImgCountPath = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesMeter"; //count zip folders path
    String ZipMetCSVPath = Environment.getExternalStorageDirectory() + "/MBC/MetCSV/"; //Single data CSV path
//    public String ZipDesPath = Environment.getExternalStorageDirectory() + "/MBC/" + GSBilling.getInstance().getKEYNAME() + GSBilling.getInstance().captureDatetime() + ".zip"; //single CSV after zip Destination path
    public String ZipDesPath;
//    String ZipDesPathdup = "/MBC/" + GSBilling.getInstance().getKEYNAME() + GSBilling.getInstance().captureDatetime(); // duplicate of zipDespath
    String ZipDesPathdup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remarks);

        mContext = this;
        flag = getIntent().getExtras().getBoolean("Flag");  //FLAG true if NORMAL Meter Status , false Otherwise
        btn_Next = (Button) findViewById(R.id.nextBTNRem);
        et_Remark = (EditText) findViewById(R.id.editextRemark);

        comApp = new UtilAppCommon();
        dbHelper = new DB(getApplicationContext());
        MRname = comApp.UniqueCode(getApplicationContext());

        ZipDesPath=Environment.getExternalStorageDirectory() + "/MBC/" + MRname + GSBilling.getInstance().captureDatetime() + ".zip"; //single CSV after zip Destination path
        ZipDesPathdup = "/MBC/" + MRname + GSBilling.getInstance().captureDatetime(); // duplicate of zipDespath


        Logger.v(getApplicationContext(), "battery", "" + Structmeterupload.BATERY_STAT);
        Logger.v(getApplicationContext(), "signal", "" + Structmeterupload.SIG_STRENGTH);
        Logger.v(getApplicationContext(), "version", "" + Structmeterupload.VER_CODE);

        Logger.v(getApplicationContext(), "getter", "" + GSBilling.getInstance().getBatStr());
        Logger.v(getApplicationContext(), "getter", "" + GSBilling.getInstance().getSignalStr());
        Logger.v(getApplicationContext(), "getter", "" + GSBilling.getInstance().getVerCode());


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        startLocationAlert = new StartLocationAlert(this);
        startLocationAlert.settingsrequest();
        sessionManager = new SessionManager(getApplicationContext());

        // MRname = sessionManager.retLicence();

        Structmeterupload.MRCODE =MRname;

        gps = new GPSTracker(Remarks.this);

        latitude = gps.getLatitude();
        longitude = gps.getLongitude();
        gpstime = gps.getTime();
        accuracy = gps.getAccuracy();
        altitude = gps.getAltitude();

        final SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
        final SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");

        Date d = new Date(gps.getTime());
        Date d2 = new Date();

        String sDate = sdf1.format(d);
        String s2Date = sdf2.format(d2);

        Structmeterupload.USER_LONG = String.valueOf(longitude);
        Structmeterupload.USER_LAT = String.valueOf(latitude);
        Structmeterupload.GPS_TIME = sDate;
        Structmeterupload.USER_ACCURACY = String.valueOf(accuracy);
        Structmeterupload.USER_ALT = String.valueOf(altitude);
        Structmeterupload.METERREADINGDATE = Structmetering.METERREADINGDATE;
        Structmeterupload.CURMETERDATE = s2Date;
        Structmeterupload.BATERY_STAT = GSBilling.getInstance().getBatStr();
        Structmeterupload.SIG_STRENGTH = GSBilling.getInstance().getSignalStr();
        Structmeterupload.VER_CODE = GSBilling.getInstance().getVerCode();

        File file2 = new File(ZipImgCountPath);
        DeleteRecursive(file2);


        btn_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_Next.setVisibility(View.INVISIBLE);
                str_Remark = et_Remark.getText().toString();

                if (str_Remark != null && !str_Remark.isEmpty()) {
                    Structmeterupload.REMARK = str_Remark;
                } else {
                    Structmeterupload.REMARK = "";
                }

                if (isLocationEnabled(getApplicationContext())) {      //Location Enable Check

                    if (isMobileDataEnabled()) {         //Mobile Data Enable Check

                        if (latitude1 == 0.00) {   //Location 0.00 Enable Check

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i <= 50; i++) {     //Force Location Capture

                                        GPSTracker gps3 = new GPSTracker(Remarks.this);
                                        if (gps3.canGetLocation()) {

                                            latitude1 = gps3.getLatitude();
                                            longitude1 = gps3.getLongitude();
                                            gpstime = gps3.getTime();
                                            accuracy = gps3.getAccuracy();
                                            altitude = gps3.getAltitude();
                                            System.out.println("LAT FOR " + latitude1);
                                            System.out.println("LONG FOR " + longitude1);

                                            final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
                                            Date d = new Date(gps3.getTime());
                                            String sDate = sdf.format(d);

                                            Structmeterupload.USER_LONG = String.valueOf(longitude1);
                                            Structmeterupload.USER_LAT = String.valueOf(latitude1);
                                            Structmeterupload.GPS_TIME = sDate;
                                            Structmeterupload.USER_ACCURACY = String.valueOf(accuracy);
                                            Structmeterupload.USER_ALT = String.valueOf(altitude);

                                        }

                                        if (latitude1 != 0.0) {
                                            break; // A unlabeled break is enough. You don't need a labeled break here.
                                        }

                                    }
                                    if (flag) {

                                        Structmeterupload.CONSUMERNO            = Structmetering.CONSUMERNO;
                                        Structmeterupload.OLDCONSUMERNO         = Structmetering.OLDCONSUMERNO;
                                        Structmeterupload.TARIFFCODE            = Structmetering.TARIFFCODE;
                                        Structmeterupload.METERDEVICESERIALNO   = Structmetering.METERDEVICESERIALNO;
                                        Structmeterupload.NAME                  = Structmetering.NAME;
                                        Structmeterupload.ADDRESS               = Structmetering.ADDRESS;
                                        Structmeterupload.CYCLE                 = Structmetering.CYCLE;
                                        Structmeterupload.ROUTENO               = Structmetering.ROUTENO;
                                        Structmeterupload.DIVISION              = Structmetering.DIVISION;
                                        Structmeterupload.SUBDIVISION           = Structmetering.SUBDIVISION;
                                        Structmeterupload.SECTION               = Structmetering.SECTION;
                                        Structmeterupload.BILLMONTH             = Structmetering.BILLMONTH;
                                        Structmeterupload.NORMALMDUNIT          = Structmetering.NORMALMDUNIT;
                                        Structmeterupload.PEAKKWH               = Structmetering.PEAKKWH;
                                        Structmeterupload.PEAKKVAH              = Structmetering.PEAKKVAH;
                                        Structmeterupload.PEAKKHARH             = Structmetering.PEAKKHARH;
                                        Structmeterupload.PEAKMD                = Structmetering.PEAKMD;
                                        Structmeterupload.PEAKMDUNIT            = Structmetering.PEAKMDUNIT;
                                        Structmeterupload.RIFLAG                = Structmetering.RIFLAG;
                                        Structmeterupload.OFFPEAKKVAH           = Structmetering.OFFPEAKKVAH;
                                        Structmeterupload.OFFPEAKKHARH          = Structmetering.OFFPEAKKHARH;
                                        // Structmeterupload.CUMULATIVEMD = Structmetering.CUMULATIVEMD;
                                        Structmeterupload.KWH3CON               = Structmetering.KWH3CON;
                                        Structmeterupload.KWH6CON               = Structmetering.KWHLASTMONCON;
                                        Structmeterupload.MD3CON                = Structmetering.MD3CON;
                                        Structmeterupload.MD6CON                = Structmetering.MDLASTMONCON;
                                        Structmeterupload.OFFPEAK3CON           = Structmetering.OFFPEAKKWH3CON;
                                        Structmeterupload.OFFPEAK6CON           = Structmetering.OFFPEAKKWHLASTMONCON;
                                        Structmeterupload.DIVCODE 	            = Structmetering.DIVCODE 				;
                                        Structmeterupload.SUBDIVCODE            = Structmetering.SUBDIVCODE			;
                                        Structmeterupload.SECCODE  	            = Structmetering.SECCODE  				;
                                        Structmeterupload.CESUDIV 	            = Structmetering.CESUDIV 	   			;
                                        Structmeterupload.CESUSUBDIV            = Structmetering.CESUSUBDIV   		;
                                        Structmeterupload.CESUSEC  	            = Structmetering.CESUSEC  	   		;
                                        Alert(mContext);

                                    } else {

                                        Structmeterupload.OLDOUTTERBOXSEAL  = "0.00";
                                        Structmeterupload.OLDINNERBOXSEAL   = "0.00";
                                        Structmeterupload.OLDOPTICALSEAL    = "0.00";
                                        Structmeterupload.OLDMDBUTTONSEAL   = "0.00";
                                        Structmeterupload.METERVOLTR        = "0.00";
                                        Structmeterupload.METERVOLTY        = "0.00";
                                        Structmeterupload.METERVOLTB        = "0.00";
                                        Structmeterupload.METERCURR         = "0.00";
                                        Structmeterupload.METERCURY         = "0.00";
                                        Structmeterupload.METERCURB         = "0.00";
                                        Structmeterupload.TONGUEVOLTR       = "0.00";
                                        Structmeterupload.TONGUEVOLTY       = "0.00";
                                        Structmeterupload.TONGUEVOLTB       = "0.00";
                                        Structmeterupload.TONGUECURR        = "0.00";
                                        Structmeterupload.TONGUECURY        = "0.00";
                                        Structmeterupload.TONGUECURB        = "0.00";

                                        comApp.copy2MTRUpload();

                                        Alert(mContext);

                                    }

                                }
                            }, 1 * 5 * 1000);

                        } else {

                            if (flag) {

                                Structmeterupload.CONSUMERNO          = Structmetering.CONSUMERNO;
                                Structmeterupload.OLDCONSUMERNO       = Structmetering.OLDCONSUMERNO;
                                Structmeterupload.TARIFFCODE          = Structmetering.TARIFFCODE;
                                Structmeterupload.METERDEVICESERIALNO = Structmetering.METERDEVICESERIALNO;
                                Structmeterupload.NAME          = Structmetering.NAME;
                                Structmeterupload.ADDRESS       = Structmetering.ADDRESS;
                                Structmeterupload.CYCLE         = Structmetering.CYCLE;
                                Structmeterupload.ROUTENO       = Structmetering.ROUTENO;
                                Structmeterupload.DIVISION      = Structmetering.DIVISION;
                                Structmeterupload.SUBDIVISION   = Structmetering.SUBDIVISION;
                                Structmeterupload.SECTION       = Structmetering.SECTION;
                                Structmeterupload.BILLMONTH     = Structmetering.BILLMONTH;
                                Structmeterupload.NORMALMDUNIT  = Structmetering.NORMALMDUNIT;
                                Structmeterupload.PEAKKWH       = Structmetering.PEAKKWH;
                                Structmeterupload.PEAKKVAH      = Structmetering.PEAKKVAH;
                                Structmeterupload.PEAKKHARH     = Structmetering.PEAKKHARH;
                                Structmeterupload.PEAKMD        = Structmetering.PEAKMD;
                                Structmeterupload.PEAKMDUNIT    = Structmetering.PEAKMDUNIT;
                                Structmeterupload.RIFLAG        = Structmetering.RIFLAG;
                                Structmeterupload.OFFPEAKKVAH   = Structmetering.OFFPEAKKVAH;
                                Structmeterupload.OFFPEAKKHARH  = Structmetering.OFFPEAKKHARH;
                                // Structmeterupload.CUMULATIVEMD  = Structmetering.CUMULATIVEMD;
                                Structmeterupload.KWH3CON       = Structmetering.KWH3CON;
                                Structmeterupload.KWH6CON       = Structmetering.KWHLASTMONCON;
                                Structmeterupload.MD3CON        = Structmetering.MD3CON;
                                Structmeterupload.MD6CON        = Structmetering.MDLASTMONCON;
                                Structmeterupload.OFFPEAK3CON   = Structmetering.OFFPEAKKWH3CON;
                                Structmeterupload.OFFPEAK6CON   = Structmetering.OFFPEAKKWHLASTMONCON;
                                Structmeterupload.DIVCODE 	    = Structmetering.DIVCODE 				;
                                Structmeterupload.SUBDIVCODE    = Structmetering.SUBDIVCODE			;
                                Structmeterupload.SECCODE  	    = Structmetering.SECCODE  				;
                                Structmeterupload.CESUDIV 	    = Structmetering.CESUDIV 	   			;
                                Structmeterupload.CESUSUBDIV    = Structmetering.CESUSUBDIV   		;
                                Structmeterupload.CESUSEC  	    = Structmetering.CESUSEC  	   			;

                                Alert(mContext);

                            } else {    //if comes DIrect

                                Structmeterupload.OLDOUTTERBOXSEAL  = "0.00";
                                Structmeterupload.OLDINNERBOXSEAL   = "0.00";
                                Structmeterupload.OLDOPTICALSEAL    = "0.00";
                                Structmeterupload.OLDMDBUTTONSEAL   = "0.00";
                                Structmeterupload.METERVOLTR        = "0.00";
                                Structmeterupload.METERVOLTY        = "0.00";
                                Structmeterupload.METERVOLTB        = "0.00";
                                Structmeterupload.METERCURR         = "0.00";
                                Structmeterupload.METERCURY         = "0.00";
                                Structmeterupload.METERCURB         = "0.00";
                                Structmeterupload.TONGUEVOLTR       = "0.00";
                                Structmeterupload.TONGUEVOLTY       = "0.00";
                                Structmeterupload.TONGUEVOLTB       = "0.00";
                                Structmeterupload.TONGUECURR        = "0.00";
                                Structmeterupload.TONGUECURY        = "0.00";
                                Structmeterupload.TONGUECURB        = "0.00";

                                comApp.copy2MTRUpload();

                                Alert(mContext);


                            }


                        }


                    } else {

                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(
                                "com.android.settings",
                                "com.android.settings.Settings$DataUsageSummaryActivity"));
                        startActivity(intent);

                    }

                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
//                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 2);
                    startLocationAlert.settingsrequest();
                }


            }
        });


    }

    public void onBackPressed() {

        finish(); // finish activity
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // startLocationUpdates();

                        break;
                    case Activity.RESULT_CANCELED:
                        startLocationAlert.settingsrequest();//keep asking if imp or do whatever
                        break;
                }

                break;


        }
    }

    public boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }


    public void Alert(final Context context) {
        btn_Next.setVisibility(View.VISIBLE);
        dbHelper.insertInMeterUploadTable();    //Database Insert

                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure to Upload?")
                        .setContentText("the data will be sent to server")
                        .setCancelText("No")
                        .setConfirmText("Yes")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                // reuse previous dialog instance, keep widget user state, reset them if you need
                                sDialog.cancel();

                                Toast.makeText(Remarks.this, "Internally Stored", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getApplicationContext(), Meteringtypes.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                // intent.putExtra("Position", 1);

                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left);
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();

                                if(isMobileDataEnabled()){

                                    new TextFileMeterClass(Remarks.this).execute();

                                }else{

                                    Toast.makeText(context, "Internally Stored Due to No-Connectivity", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), Meteringtypes.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    // intent.putExtra("Position", 1);

                                    startActivity(intent);
                                    overridePendingTransition(R.anim.anim_slide_in_left,
                                            R.anim.anim_slide_out_left);

                                }


                            }
                        })
                        .show();

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

    /*--------------------- Meter Reading Uplaod Text File Creation ----------------------------------*/
    private class TextFileMeterClass extends AsyncTask<String, Void, Void> {

        private final Context context;
        TextFileMeterClass(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {

            progress = new ProgressDialog(this.context);
            progress.setMessage("Uploading Please Wait..");
            progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                dbHelper2 = new DB(getApplicationContext());
                SD2 = dbHelper2.getWritableDatabase();

                String selquer = "SELECT * FROM TBL_METERUPLOAD WHERE UPLOADFLAG='N' ";//WHERE Upload_Flag='N'

                Cursor curMetselect = SD2.rawQuery(selquer, null);
                String arrStr[] = null;
                ArrayList<String> mylist = new ArrayList<String>();
                // Removefolder(ZipImgLimitPath);
                if (curMetselect != null && curMetselect.moveToFirst()) {
                    int counter = 0;
                    while (!curMetselect.isAfterLast()) {
                        counter++;
                        // String name = curBillselect.getString(0);

                        mylist.add(curMetselect.getString(0)  + "}" + curMetselect.getString(1)  + "}" + curMetselect.getString(2)  +
                             "}" + curMetselect.getString(3)  + "}" + curMetselect.getString(4)  + "}" + curMetselect.getString(5)  +
                             "}" + curMetselect.getString(6)  + "}" + curMetselect.getString(7)  + "}" + curMetselect.getString(8)  +
                             "}" + curMetselect.getString(9)  + "}" + curMetselect.getString(10) + "}" + curMetselect.getString(11) +
                             "}" + curMetselect.getString(12) + "}" + curMetselect.getString(13) + "}" + curMetselect.getString(14) +
                             "}" + curMetselect.getString(15) + "}" + curMetselect.getString(16) + "}" + curMetselect.getString(17) +
                             "}" + curMetselect.getString(18) + "}" + curMetselect.getString(19) + "}" + curMetselect.getString(20) +
                             "}" + curMetselect.getString(21) + "}" + curMetselect.getString(22) + "}" + curMetselect.getString(23) +
                             "}" + curMetselect.getString(24) + "}" + curMetselect.getString(25) + "}" + curMetselect.getString(26) +
                             "}" + curMetselect.getString(27) + "}" + curMetselect.getString(28) + "}" + curMetselect.getString(29) +
                             "}" + curMetselect.getString(30) + "}" + curMetselect.getString(31) + "}" + curMetselect.getString(32) +
                             "}" + curMetselect.getString(33) + "}" + curMetselect.getString(34) + "}" + curMetselect.getString(35) +
                             "}" + curMetselect.getString(36) + "}" + curMetselect.getString(37) + "}" + curMetselect.getString(38) +
                             "}" + curMetselect.getString(39) + "}" + curMetselect.getString(40) + "}" + curMetselect.getString(41) +
                             "}" + curMetselect.getString(42) + "}" + curMetselect.getString(43) + "}" + curMetselect.getString(44) +
                             "}" + curMetselect.getString(45) + "}" + curMetselect.getString(46) + "}" + curMetselect.getString(47) +
                             "}" + curMetselect.getString(48) + "}" + curMetselect.getString(49) + "}" + curMetselect.getString(50) +
                             "}" + curMetselect.getString(51) + "}" + curMetselect.getString(52) + "}" + curMetselect.getString(53) +
                             "}" + curMetselect.getString(54) + "}" + curMetselect.getString(55) + "}" + curMetselect.getString(56) +
                             "}" + curMetselect.getString(57) + "}" + curMetselect.getString(58) + "}" + curMetselect.getString(60) +
                             "}" + curMetselect.getString(61) + "}" + curMetselect.getString(62) + "}" + curMetselect.getString(63) +
                             "}" + curMetselect.getString(64) + "}" + curMetselect.getString(65) + "}" + curMetselect.getString(66) +
                             "}" + curMetselect.getString(67) + "}" + curMetselect.getString(68) + "}" + curMetselect.getString(69) +
                             "}" + curMetselect.getString(70) + "}" + curMetselect.getString(71) + "}" + curMetselect.getString(72)+
                             "}" + curMetselect.getString(77) + "}" + curMetselect.getString(78) + "}" + curMetselect.getString(79)+
                             "}" + curMetselect.getString(80) + "}" + curMetselect.getString(81)+ "}" + curMetselect.getString(82)+
                             "}" + curMetselect.getString(83) + "}" + curMetselect.getString(84)+ "}" + curMetselect.getString(85));

                        createfolder(ZipImgLimitPathMeter + "/" + MRname + GSBilling.getInstance().captureDate() + "_" + counter / globalcount);
                        moveFile(ZipSourcePath, curMetselect.getString(60), ZipImgLimitPathMeter + "/" + MRname + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");
                        moveFile(ZipSourcePath, curMetselect.getString(61), ZipImgLimitPathMeter + "/" + MRname + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");

                        curMetselect.moveToNext();

                    }
                    for (int i = 0; i <= counter / globalcount; i++) {

                        selectZipFolder(ZipImgLimitPathMeter + "/" + MRname + GSBilling.getInstance().captureDate() + "_" + i, ZipImgLimitPathMeter + "/" + MRname + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                    }
                    generateNoteMeterOnSD(getApplicationContext(), "metering.csv", mylist);
                }

                Remarks.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progress.dismiss();
                        new PostMeterClass(Remarks.this).execute();
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
            // new PostClass(SDActivity.this).execute();
        }

    }


    /*--------------------- Meter Reading Uplaod Upload billing File  ----------------------------------*/
    public class PostMeterClass extends AsyncTask<String, Void, Boolean> {

        private final Context context;
        public String succsess = null;

        PostMeterClass(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Please Wait..");
            progress.show();

            zipFolder(ZipMetCSVPath, ZipDesPath);
            System.out.println("++++++++" + ZipDesPathdup);
            GSBilling.getInstance().setFinalZipName(ZipDesPathdup);

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip");
                 HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi_Testing/api/UploadFile/UploadMobileReadingFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadMobileReadingFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                // HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");

                int status = hfu.Send_Now(fstrm);

                if (status != 200) {

                    Remarks.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();

                            Toast.makeText(Remarks.this, "Internaly Stored Due to No Connectivity", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), Meteringtypes.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            // intent.putExtra("Position", 1);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);
                        }
                    });

                } else {


                    File file = new File(ZipImgCountPath);
                    int filecount = CountRecursive(file);

                    for (int i = 0; i < filecount; i++) {

                        FileInputStream fstrm2 = null;
                        try {
                            fstrm2 = new FileInputStream(ZipImgLimitPathMeter + MRname + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                             FileUpload hfu2 = new FileUpload("http://enserv.feedbackinfra.com/Webapi_Testing/api/UploadFile/UploadMobileReadingFiles", "" + ZipImgLimitPathMeter + MRname + GSBilling.getInstance().captureDate() + "_" + i, ".zip");
//                            FileUpload hfu2 = new FileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadMobileReadingFiles", "" + ZipImgLimitPathMeter + MRname + GSBilling.getInstance().captureDate() + "_" + i, ".zip");
                            // FileUpload hfu = new FileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + ZipImgLimitPath + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");

                            int status2 = hfu2.Send_Now(fstrm2);
                            if (status2 != 200) {
                            } else {

                                String[] fileNames = new String[0];
                                File fileImage = new File(ZipImgLimitPathMeter + MRname + GSBilling.getInstance().captureDate() + "_" + i + "/");
                                if (fileImage.isDirectory()) {
                                    fileNames = fileImage.list();
                                }
                                int total = 0;
                                for (String fileName : fileNames) {
                                    if (fileName.contains("_kwh.jpg")) {

                                        dbHelper2 = new DB(getApplicationContext());
                                        SD2 = dbHelper2.getWritableDatabase();

                                        String updatequer = "UPDATE  TBL_METERUPLOAD  SET UPLOADFLAG = 'Y' WHERE  CONSUMERNO = '" + fileName.split("_")[1] + "';";

                                        Cursor update = SD2.rawQuery(updatequer, null);
                                        if (update != null && update.moveToFirst()) {
                                            android.util.Log.v("Update ", "Success");
                                        }
                                    }
                                }

                            }
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    }
                    Remarks.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();
                            // new UpdateUI().execute();
                            Toast.makeText(Remarks.this, "Successfully Uploaded to Server", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(getApplicationContext(), Meteringtypes.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            // intent.putExtra("Position", 1);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);

                        }
                    });
                }


            } catch (IOException e) {
                e.printStackTrace();

            }
            return true;
        }

        protected void onPostExecute() {
            progress.dismiss();
//            File file = new File(ZipImgCountPath);
//            DeleteRecursive(file);
            new File(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip").delete();

        }

    }

    public void createfolder(String outputPath) {
        //create output directory if it doesn't exist
        File dir = new File(outputPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public int CountRecursive(File dir) {
        String[] fileNames;
        fileNames = dir.list();
        int total = 0;
        for (String fileName : fileNames) {
            if (fileName.contains(".zip")) {
                total++;
            }
        }
        Log.e(getApplicationContext(),"","1"+total);
        Log.e(getApplicationContext(),"","2"+fileNames.length);
        return total;
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
    public static void zipFolder(String inputFolderPath, String outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outZipPath);
            ZipOutputStream zos = new ZipOutputStream(fos);
            File srcFile = new File(inputFolderPath);
            File[] files = srcFile.listFiles();
            android.util.Log.d("", "Zip directory: " + srcFile.getName());
            for (File file : files) {
                android.util.Log.d("", "Adding file: " + file.getName());
                byte[] buffer = new byte[1024];
                FileInputStream fis = new FileInputStream(file);
                zos.putNextEntry(new ZipEntry(file.getName()));
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

    public static void selectZipFolder(String inputFolderPath, String outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outZipPath);
            // GSBilling.getInstance().setFinalZipName();
            ZipOutputStream zos = new ZipOutputStream(fos);
            File srcFile = new File(inputFolderPath);
            File[] files = srcFile.listFiles();
            android.util.Log.d("", "Zip directory: " + srcFile.getName());
            for (File file : files) {
                android.util.Log.d("", "Adding file: " + file.getName());
                byte[] buffer = new byte[1024];
                FileInputStream fis = new FileInputStream(file);
                zos.putNextEntry(new ZipEntry(file.getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
        } catch (IOException ioe) {
            android.util.Log.e("", ioe.getMessage());
        }
    }

    public void generateNoteMeterOnSD(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "MBC/MetCSV/");
            if (!root.exists()) {
                root.mkdirs();
            } else {
                root.delete();
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

            ////  delete the original file
            // new File(inputPath + inputFile).delete();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
