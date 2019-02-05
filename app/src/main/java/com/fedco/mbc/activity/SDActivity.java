package com.fedco.mbc.activity;

 import android.app.Activity;
 import android.app.ProgressDialog;
 import android.content.Context;
 import android.content.Intent;
 import android.database.Cursor;
 import android.database.sqlite.SQLiteDatabase;
 import android.graphics.Color;
 import android.graphics.Typeface;
 import android.net.ConnectivityManager;
 import android.net.NetworkInfo;
 import android.os.AsyncTask;
 import android.os.Bundle;
 import android.os.Environment;
 import android.os.StrictMode;
 import android.telephony.TelephonyManager;
 import android.util.Log;
 import android.view.View;
 import android.widget.Button;
 import android.widget.TextView;
 import android.widget.Toast;

 import com.fedco.mbc.R;
 import com.fedco.mbc.activitysurvey.SurveyDetails;
 import com.fedco.mbc.authentication.SessionManager;
 import com.fedco.mbc.bluetoothprinting.GlobalPool;
 import com.fedco.mbc.model.Structbilling;
 import com.fedco.mbc.sqlite.DB;
 import com.fedco.mbc.utils.CustomDialog;
 import com.fedco.mbc.utils.DownloadCollectionFileFromURL;
 import com.fedco.mbc.utils.DownloadFileFromURL;
 import com.fedco.mbc.utils.DownloadMeterFileFromURL;
 import com.fedco.mbc.utils.DownloadNMFileFromURL;
 import com.fedco.mbc.utils.DownloadReplaceFileFromURL;
 import com.fedco.mbc.utils.DownloadSurveyFileFromURL;
 import com.fedco.mbc.utils.FileUpload;
 import com.fedco.mbc.utils.HttpFileUpload;
 import com.fedco.mbc.utils.URLS;
 import com.fedco.mbc.utils.UtilAppCommon;

 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.FileReader;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.LineNumberReader;
 import java.io.OutputStream;
 import java.net.HttpURLConnection;
 import java.net.URL;
 import java.sql.Struct;
 import java.text.SimpleDateFormat;
 import java.util.ArrayList;
 import java.util.Calendar;
 import java.util.List;
 import java.util.zip.ZipEntry;
 import java.util.zip.ZipOutputStream;

 import cn.pedant.SweetAlert.SweetAlertDialog;

 /**
 * Created by nitinb on 30-12-2015.o
 */
public class SDActivity extends Activity implements LogoutListaner {

    String srcFilePath = Environment.getExternalStorageDirectory() + "/MBC/";
    String zipFile = Environment.getExternalStorageDirectory() + "/MBC/BED.zip";
    String unzipLocation = Environment.getExternalStorageDirectory() + "/unzippedfolder/";
    String unzipColLocation = Environment.getExternalStorageDirectory() + "/unzippedcolfolder/";
    String unzipMetLocation = Environment.getExternalStorageDirectory() + "/unzippedmetfolder/";

    String ZipBillCSVPath   = Environment.getExternalStorageDirectory() + "/MBC/BillCSV/";
    String ZipMetCSVPath    = Environment.getExternalStorageDirectory() + "/MBC/MetCSV/";
    String ZipCSCSVPath     = Environment.getExternalStorageDirectory() + "/MBC/CSCSV/";
    String ZipDTRCSVPath    = Environment.getExternalStorageDirectory() + "/MBC/DTRCSV/";
    String ZipFEEDCSVPath   = Environment.getExternalStorageDirectory() + "/MBC/FEEDERCSV/";
    String ZipPOLECSVPath   = Environment.getExternalStorageDirectory() + "/MBC/POLECSV/";
    String ZipReplaceCSVPath   = Environment.getExternalStorageDirectory() + "/MBC/RepalceCSV/";

    String ZipImgLimitPath = Environment.getExternalStorageDirectory() + "/MBC/LimitImages/";
    String ZipImgCountPath = Environment.getExternalStorageDirectory() + "/MBC/LimitImages";

    String ZipImgLimitPathCS = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesCS/";
    String ZipImgCountPathCS = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesCS";

    String ZipImgLimitPathDTR = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesDTR/"; //20 count image folder path
    String ZipImgCountPathDTR = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesDTR";

    String ZipImgLimitPathFeed = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesFeed/"; //20 count image folder path
    String ZipImgCountPathFeed = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesFeed";

    String ZipImgLimitPathPole = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesPOLE/"; //20 count image folder path
    String ZipImgCountPathPole = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesPOLE";

    String ZipImgLimitPathMeter = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesMeter/";
    String ZipImgCountPathMeter = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesMeter";

    String ZipCopyColPath = Environment.getExternalStorageDirectory() + "/MBC/ColDownloads/";
    String ZipSourcePath = Environment.getExternalStorageDirectory() + "/MBC/Images/";

    String ZipImgLimitPathReplace = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesReplace/";
    String ZipImgCountPathReplace = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesReplace";

    public String ZipDesPath, ZipDesColPath;
    String ZipDesPathdup, ZipDesColPathdup;

    String _zipFile;
    String _location;

    String[] splitData;
    String line = "";
    String pending, colpending, metpending,replacepending, surveyConpending, surveyFeederpending, surveyDTRpending, surveyPolepending;
    int billing_Pending,replce_Pending,collection_Pending,meter_Pending,consumer_Pending,dtr_Pending,feeder_Pending,pole_Pending;
    int countSize;
    public int globalcount = 10;
    private ProgressDialog progress;
    SweetAlertDialog sDialog;

    List<File> files;

    DB databasehelper;
    SQLiteDatabase SD, SD2, SD3;
    DB dbHelper, dbHelper2, dbHelper4;

    Button btnShowProgress, btnUpload, btnNSCMRDownload;

    // Progress Dialog
    private CustomDialog pDialog;
    private static SDActivity sActivity;
    File filelocal;
    Context context;
    SessionManager session;

    Cursor curPend, curColPend, curMetPend,curReplacePend, curSurConPend, curSurFeedPend, curSurDTRPend, curSurPolePend;

    public static SDActivity getsActivity() {
        return sActivity;
    }

    TextView pend, colPend, metPend, repalcePend, csPend, feedPend, dtrPend, polePend;
    String keymap, name;
    TelephonyManager telephonyManager;
    String codeIMEI;
    UtilAppCommon appCommon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sd);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        sActivity = this;
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        codeIMEI = telephonyManager.getDeviceId();
        appCommon = new UtilAppCommon();

        session = new SessionManager(getApplicationContext());
        name = appCommon.UniqueCode(getApplicationContext());
        keymap = getIntent().getStringExtra("flowkey");

        this.ZipDesPathdup = "/MBC/" + name + GSBilling.getInstance().captureDatetime();
        this.ZipDesPath = Environment.getExternalStorageDirectory() + "/MBC/" + name + GSBilling.getInstance().captureDatetime() + ".zip";
        this.ZipDesColPathdup = "/MBC/" + name + "_col_" + GSBilling.getInstance().captureDatetime();
        this.ZipDesColPath = Environment.getExternalStorageDirectory() + "/MBC/" + name + "_col_" + GSBilling.getInstance().captureDatetime() + ".zip";

        pend = (TextView) findViewById(R.id.tvPend);
        colPend = (TextView) findViewById(R.id.tvColPend);
        metPend = (TextView) findViewById(R.id.tvMetPend);
        csPend = (TextView) findViewById(R.id.tvCSPend);
        feedPend = (TextView) findViewById(R.id.tvFEEDPend);
        dtrPend = (TextView) findViewById(R.id.tvDTRPend);
        polePend = (TextView) findViewById(R.id.tvPOLEPend);
        repalcePend = (TextView) findViewById(R.id.tvReplacePend);

        btnShowProgress = (Button) findViewById(R.id.btnProgressBar);
        btnNSCMRDownload = (Button) findViewById(R.id.btnNSCMR);
        btnNSCMRDownload.setVisibility(View.GONE);

        if (keymap.equals("metering")) {
            btnNSCMRDownload.setVisibility(View.VISIBLE);
        }
        ((GlobalPool)getApplication()).registerSessionListaner(this);
        ((GlobalPool)getApplication()).startUserSession();

        dbHelper4 = new DB(getApplicationContext());
        SD3 = dbHelper4.getWritableDatabase();

        btnUpload = (Button) findViewById(R.id.btnProgressBarupload);
        btnUpload.setVisibility(View.GONE);

        checkPendingRecord();
        pending();
        setVisible();

        /*--------------------- Download Button Click ----------------------*/
        btnShowProgress.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isNetworkAvailable(getApplicationContext())) {

                    switch (keymap) {
                        case "billing":

                            File prevFile = new File(unzipLocation);
                            DeleteRecursive(prevFile);
//                            if (curPend.getString(0).matches("0") && curColPend.getString(0).matches("0") && curMetPend.getString(0).matches("0")) {
                            if (curPend.getString(0).matches("0")  && curMetPend.getString(0).matches("0")) {

                                File dltPrevZip = new File(srcFilePath);
                                DeleteZIP(dltPrevZip);

                                new CreateBackupBilling(SDActivity.this).execute();

                                String clearTable = "DELETE FROM TBL_BILLING";
                                SD3 = dbHelper4.getWritableDatabase();
                                SD3.beginTransaction();
                                SD3.execSQL(clearTable);
                                SD3.setTransactionSuccessful();
                                SD3.endTransaction();
                                SD3.close();

                                new LongOperation().execute();

                            } else {
                                Toast.makeText(SDActivity.this, " PLEASE UPLOAD PENDING RECORDS ", Toast.LENGTH_SHORT).show();
                            }

                            break;
                        case "collection":

//                            File prevFile2 = new File(unzipColLocation);
//                            DeleteRecursive(prevFile2);
////                            if (curPend.getString(0).matches("0") && curColPend.getString(0).matches("0") && curMetPend.getString(0).matches("0")) {
//
//                                File dltPrevZip7 = new File(srcFilePath);
//                                DeleteZIP(dltPrevZip7);

//                                new CreateBackupCollection(SDActivity.this).execute();

//                                String clearTable12 = "DELETE FROM TBL_COLMASTER_MP";
//                                SD3 = dbHelper4.getWritableDatabase();
//                                SD3.beginTransaction();
//                                SD3.execSQL(clearTable12);
//                                SD3.setTransactionSuccessful();
//                                SD3.endTransaction();
//                                SD3.close();

                                new LongOperationCol().execute();

//                            } else {
//                                Toast.makeText(SDActivity.this, " PLEASE UPLOAD PENDING RECORDS ", Toast.LENGTH_SHORT).show();
//                            }

                            break;
                        case "metering":

                            File prevFile3 = new File(unzipMetLocation);
                            DeleteRecursive(prevFile3);

                            if (curPend.getString(0).matches("0") && curMetPend.getString(0).matches("0")) {

                                File dltPrevZip = new File(srcFilePath);
                                DeleteZIP(dltPrevZip);

                                String clearTable = "DELETE FROM TBL_METERUPLOAD";
                                SD3 = dbHelper4.getWritableDatabase();
                                SD3.beginTransaction();
                                SD3.execSQL(clearTable);
                                SD3.setTransactionSuccessful();
                                SD3.endTransaction();
                                SD3.close();

                                new LongOperationCoMetering().execute();

                            } else {

                                Toast.makeText(SDActivity.this, " PLEASE UPLOAD PENDING RECORDS ", Toast.LENGTH_SHORT).show();
                            }

                            break;
                        case "survey":

                            File prevFile4 = new File(unzipMetLocation);
                            DeleteRecursive(prevFile4);

                            if (curSurConPend.getString(0).matches("0") && curSurFeedPend.getString(0).matches("0") && curSurDTRPend.getString(0).matches("0")&& curSurPolePend.getString(0).matches("0")) {

                                File dltPrevZip = new File(srcFilePath);
                                DeleteZIP(dltPrevZip);

                                String clearTableDiv = "DELETE FROM TBL_DIVISION_MASTER";
                                SD3 = dbHelper4.getWritableDatabase();
                                SD3.beginTransaction();
                                SD3.execSQL(clearTableDiv);
                                SD3.setTransactionSuccessful();
                                SD3.endTransaction();
                                SD3.close();

                                String clearTableSubDiv = "DELETE FROM TBL_SUB_DIVISION_MASTER";
                                SD3 = dbHelper4.getWritableDatabase();
                                SD3.beginTransaction();
                                SD3.execSQL(clearTableSubDiv);
                                SD3.setTransactionSuccessful();
                                SD3.endTransaction();
                                SD3.close();

                                String clearTableSec = "DELETE FROM TBL_SECTION_MASTER";
                                SD3 = dbHelper4.getWritableDatabase();
                                SD3.beginTransaction();
                                SD3.execSQL(clearTableSec);
                                SD3.setTransactionSuccessful();
                                SD3.endTransaction();
                                SD3.close();

                                String clearTableTHIFeeder = "DELETE FROM TBL_33KVFEEDER_MASTER";
                                SD3 = dbHelper4.getWritableDatabase();
                                SD3.beginTransaction();
                                SD3.execSQL(clearTableTHIFeeder);
                                SD3.setTransactionSuccessful();
                                SD3.endTransaction();
                                SD3.close();

                                String clearTableDTR = "DELETE FROM TBL_DTR_MASTER";
                                SD3 = dbHelper4.getWritableDatabase();
                                SD3.beginTransaction();
                                SD3.execSQL(clearTableDTR);
                                SD3.setTransactionSuccessful();
                                SD3.endTransaction();
                                SD3.close();

                                String clearTablePole = "DELETE FROM TBL_POLE_MASTER";
                                SD3 = dbHelper4.getWritableDatabase();
                                SD3.beginTransaction();
                                SD3.execSQL(clearTablePole);
                                SD3.setTransactionSuccessful();
                                SD3.endTransaction();
                                SD3.close();

                                String clearTableMTRMFG = "DELETE FROM TBL_METER_MFG";
                                SD3 = dbHelper4.getWritableDatabase();
                                SD3.beginTransaction();
                                SD3.execSQL(clearTableMTRMFG);
                                SD3.setTransactionSuccessful();
                                SD3.endTransaction();
                                SD3.close();

                                String clearTableELEFeeder = "DELETE FROM TBL_11KVFEEDER_MASTER";
                                SD3 = dbHelper4.getWritableDatabase();
                                SD3.beginTransaction();
                                SD3.execSQL(clearTableELEFeeder);
                                SD3.setTransactionSuccessful();
                                SD3.endTransaction();
                                SD3.close();

                                String clearTableCon = "DELETE FROM TBL_CONSUMERSURVEY_MASTER";
                                SD3 = dbHelper4.getWritableDatabase();
                                SD3.beginTransaction();
                                SD3.execSQL(clearTableCon);
                                SD3.setTransactionSuccessful();
                                SD3.endTransaction();
                                SD3.close();


                                new LongOperationSurvey().execute();

                            } else {

                                Toast.makeText(SDActivity.this, " PLEASE UPLOAD PENDING RECORDS ", Toast.LENGTH_SHORT).show();
                            }

                            break;
                        case "replacement":
                            File prevFileReplace = new File(unzipLocation);
                            DeleteRecursive(prevFileReplace);
                            if (curReplacePend.getString(0).matches("0") ) {

                                File dltPrevZip = new File(srcFilePath);
                                DeleteZIP(dltPrevZip);

                                String clearTable = "DELETE FROM TBL_METER_REPLACEMENT";
                                SD3 = dbHelper4.getWritableDatabase();
                                SD3.beginTransaction();
                                SD3.execSQL(clearTable);
                                SD3.setTransactionSuccessful();
                                SD3.endTransaction();
                                SD3.close();

                                new LongOperationReplace().execute();

                            } else {
                                Toast.makeText(SDActivity.this, " PLEASE UPLOAD PENDING RECORDS ", Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                } else {

                    Toast.makeText(getApplicationContext(), "Internet Connection Required", Toast.LENGTH_SHORT).show();

                }

            }
        });

        /*--------------------- Upload Button Click ----------------------*/

        setVisible();
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNetworkAvailable(getApplicationContext())) {

                    switch (keymap) {
                        case "billing":

                            File prevFile = new File(ZipImgLimitPath);
//                            DeleteRecursive(prevFile);

                            new TextFileClass(SDActivity.this).execute();

                            break;

                        case "collection":

                            new ColTextFileClass(SDActivity.this).execute();

                            break;

                        case "metering":

                            File prevFileMetre = new File(ZipImgLimitPathMeter);
                            DeleteRecursive(prevFileMetre);

                            new TextFileMeterClass(SDActivity.this).execute();

                            break;
                        case "survey":

                            Log.e("SD SURVEY ", "UPLOAD CLICK ");
                            File prevFileSurvey = new File(ZipImgLimitPathCS);
                            File prevFileFEEDER = new File(ZipImgLimitPathFeed);
                            File prevFileDTR = new File(ZipImgLimitPathDTR);
                            File prevFilePole = new File(ZipImgLimitPathPole);

                            DeleteRecursive(prevFileSurvey);
                            DeleteRecursive(prevFileFEEDER);
                            DeleteRecursive(prevFileDTR);
                            DeleteRecursive(prevFilePole);

                            checkPendingRecord();

                            if (consumer_Pending != 0) {
                                Log.e("BLLING", " ON");
                                new TXTConSurvey(SDActivity.this).execute();

                                checkPendingRecord();
                                pending();
                                setVisible();
                            }
                            if (feeder_Pending != 0) {
                                Log.e("Collection", " ON");
                                new TXTFeederClass(SDActivity.this).execute();

                                checkPendingRecord();
                                pending();
                                setVisible();

                            }
                            if (dtr_Pending != 0) {
                                Log.e("Meter", " ON");
                                new TXTDTRurvey(SDActivity.this).execute();

                                checkPendingRecord();
                                pending();
                                setVisible();

                            }
                            if (pole_Pending != 0) {
                                Log.e("Meter", " ON");
                                new TXTPoleClass(SDActivity.this).execute();

                                checkPendingRecord();
                                pending();
                                setVisible();
                            }


                            break;
                        case "replacement":

                            File prevFileReplace = new File(ZipImgLimitPath);
//                            DeleteRecursive(prevFileReplace);

                            new TextReplaceClass(SDActivity.this).execute();

                            break;

                    }
                } else {

                    Toast.makeText(getApplicationContext(), "Internet Connection Required", Toast.LENGTH_SHORT).show();

                }
            }
        });

        btnNSCMRDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable(getApplicationContext())) {

                    new DownloadNMFileFromURL(name, codeIMEI).execute();

                } else {

                    Toast.makeText(getApplicationContext(), "Internet Connection Required", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }


    /*--------------------- Billing Masters Download Initiation ----------------------------------*/
    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            dbHelper4 = new DB(getApplicationContext());
            SD3 = dbHelper4.getWritableDatabase();

            String delPrev = "DELETE FROM TBL_CONSMAST";
//            String delPrev = "DELETE FROM TBL_COLLECTION";
            String delPrevDC = "DELETE FROM TBL_BILLING_DC_MASTER";
            String delPrevDIV = "DELETE FROM TBL_BILLING_DIV_MASTER";
            String delPrevREM = "DELETE FROM TBL_REMARKS_MASTER";
            String delPrevTARIFF = "DELETE FROM TBL_TARRIF_MP";
            String delPrevMTRMFG    = "DELETE FROM TBL_METER_MFG";

            SD3.beginTransaction();
            SD3.execSQL(delPrev);
            SD3.setTransactionSuccessful();
            SD3.endTransaction();

            SD3.beginTransaction();
            SD3.execSQL(delPrevDC);
            SD3.setTransactionSuccessful();
            SD3.endTransaction();

            SD3.beginTransaction();
            SD3.execSQL(delPrevDIV);
            SD3.setTransactionSuccessful();
            SD3.endTransaction();

            SD3.beginTransaction();
            SD3.execSQL(delPrevREM);
            SD3.setTransactionSuccessful();
            SD3.endTransaction();

            SD3.beginTransaction();
            SD3.execSQL(delPrevTARIFF);
            SD3.setTransactionSuccessful();
            SD3.endTransaction();

            SD3.beginTransaction();
            SD3.execSQL(delPrevMTRMFG);
            SD3.setTransactionSuccessful();
            SD3.endTransaction();
            SD3.close();


            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            if (SDActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                return;
            }
            dismissProgressDialog();
            // new DownloadTarFileFromURL(name,codeIMEI).execute();
            //LIVE ONE
            new DownloadFileFromURL(name, codeIMEI).execute();
//            new DownloadCollectionFileFromURL(name, codeIMEI).execute();
            //for TESTING
            // new UnzippingTestSurveyFile().execute();

        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();

        }


    }/*--------------------- Billing Masters Download Initiation ----------------------------------*/
    private class LongOperationReplace extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            dbHelper4 = new DB(getApplicationContext());
            SD3 = dbHelper4.getWritableDatabase();

            String delPrev = "DELETE FROM TBL_CONSMAST";
//          String delPrev = "DELETE FROM TBL_COLLECTION";
            String delPrevDC = "DELETE FROM TBL_BILLING_DC_MASTER";
            String delPrevDIV = "DELETE FROM TBL_BILLING_DIV_MASTER";
//          String delPrevREM = "DELETE FROM TBL_REMARKS_MASTER";
//          String delPrevTARIFF = "DELETE FROM TBL_TARRIF_MP";
            String delPrevMTRMFG    = "DELETE FROM TBL_METER_MFG";


            SD3.beginTransaction();
            SD3.execSQL(delPrev);
            SD3.setTransactionSuccessful();
            SD3.endTransaction();

            SD3.beginTransaction();
            SD3.execSQL(delPrevDC);
            SD3.setTransactionSuccessful();
            SD3.endTransaction();

            SD3.beginTransaction();
            SD3.execSQL(delPrevDIV);
            SD3.setTransactionSuccessful();
            SD3.endTransaction();

            SD3.beginTransaction();
            SD3.execSQL(delPrevMTRMFG);
            SD3.setTransactionSuccessful();
            SD3.endTransaction();
            SD3.close();


            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            if (SDActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                return;
            }
            dismissProgressDialog();
            // new DownloadTarFileFromURL(name,codeIMEI).execute();
            //LIVE ONE
            new DownloadReplaceFileFromURL(name, codeIMEI).execute();
//            new DownloadCollectionFileFromURL(name, codeIMEI).execute();
            //for TESTING
            // new UnzippingTestSurveyFile().execute();

        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();

        }


    }
    /* -------------------- Collection Masters Download Initiation------------------------ */
    private class LongOperationCol extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            dbHelper4 = new DB(getApplicationContext());
            SD3 = dbHelper4.getWritableDatabase();

            String delPrev = "DELETE FROM TBL_COLLECTION";
            String delPrevDC = "DELETE FROM TBL_BILLING_DC_MASTER";
            String delPrevDIV = "DELETE FROM TBL_BILLING_DIV_MASTER";

            SD3.beginTransaction();
            SD3.execSQL(delPrev);
            SD3.setTransactionSuccessful();
            SD3.endTransaction();

            SD3.beginTransaction();
            SD3.execSQL(delPrevDC);
            SD3.setTransactionSuccessful();
            SD3.endTransaction();

            SD3.beginTransaction();
            SD3.execSQL(delPrevDIV);
            SD3.setTransactionSuccessful();
            SD3.endTransaction();
            SD3.close();

            return "Executed";

        }

        @Override
        protected void onPostExecute(String result) {

            sDialog.dismiss();
            new DownloadCollectionFileFromURL(name, codeIMEI).execute();
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {
            sDialog = new SweetAlertDialog(SDActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            sDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            sDialog.setTitleText("Loading");
            sDialog.setCancelable(false);
            sDialog.show();

        }


    }

    /*--------------------- Metering Masters Download Initiation ----------------------------*/
    private class LongOperationCoMetering extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            dbHelper4 = new DB(getApplicationContext());
            SD3 = dbHelper4.getWritableDatabase();

            String delPrev = "DELETE FROM TBL_METERMASTER";

            SD3.beginTransaction();
            SD3.execSQL(delPrev);
            SD3.setTransactionSuccessful();
            SD3.endTransaction();
            SD3.close();


            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            sDialog.dismiss();
            new DownloadMeterFileFromURL(name, codeIMEI).execute();

            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {
            sDialog = new SweetAlertDialog(SDActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            sDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            sDialog.setTitleText("Loading");
            sDialog.setCancelable(false);
            sDialog.show();

        }


    }
    /*--------------------- Survey Masters Download Initiation ----------------------------*/
    private class LongOperationSurvey extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

//            String delPrev = "DELETE FROM TBL_SURVEYMASTER";
//
//            SD3.beginTransaction();
//            SD3.execSQL(delPrev);
//            SD3.setTransactionSuccessful();
//            SD3.endTransaction();
//            SD3.close();
            dbHelper4 = new DB(getApplicationContext());
            SD3 = dbHelper4.getWritableDatabase();

            String del_Prev_Con_Data = "DELETE FROM TBL_CONSUMERSURVEY_UPOLOAD";
            String del_Prev_Feeder_Data = "DELETE FROM TBL_11KVFEEDER_UPLOAD";
            String del_Prev_DTR_Data = "DELETE FROM TBL_DTR_UPLOAD";
            String del_Prev_Pole_Data = "DELETE FROM TBL_POLE_UPLOAD";

            SD3.beginTransaction();
            SD3.execSQL(del_Prev_Feeder_Data);
            SD3.setTransactionSuccessful();
            SD3.endTransaction();
//            SD3.close();

            SD3.beginTransaction();
            SD3.execSQL(del_Prev_DTR_Data);
            SD3.setTransactionSuccessful();
            SD3.endTransaction();
//            SD3.close();

            SD3.beginTransaction();
            SD3.execSQL(del_Prev_Pole_Data);
            SD3.setTransactionSuccessful();
            SD3.endTransaction();
//            SD3.close();

            SD3.beginTransaction();
            SD3.execSQL(del_Prev_Con_Data);
            SD3.setTransactionSuccessful();
            SD3.endTransaction();
//            SD3.close();

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            sDialog.dismiss();
            new DownloadSurveyFileFromURL(name, codeIMEI).execute();


            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {
            sDialog = new SweetAlertDialog(SDActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            sDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            sDialog.setTitleText("Loading");
            sDialog.setCancelable(false);
            sDialog.show();

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
                    genBck(getApplicationContext(), "bill_bck.csv", mylist3);
                    genBck(getApplicationContext(), "billing1.csv", mylist4);

                }

                SDActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progress.dismiss();
                        new PostClass(SDActivity.this).execute();
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
            // new PostClass(SDActivity.this).execute();
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

                    SDActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();

                            Toast.makeText(SDActivity.this, "Internaly Stored Due to No Connectivity", Toast.LENGTH_LONG).show();

                        }
                    });

                } else {
                    SDActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            dbHelper2 = new DB(getApplicationContext());
                            SD2 = dbHelper2.getWritableDatabase();

                            String updatequer = "UPDATE  TBL_BILLING  SET Upload_Flag = 'Y'";// WHERE  Cons_Number = '" + fileNames[image].split("_")[1] + "';";

                            Cursor update = SD2.rawQuery(updatequer, null);
                            if (update != null && update.moveToFirst()) {
                                Log.v("Update ", "Success");
                            }

                            File file = new File(ZipImgCountPath);
                            int filecount = CountRecursive(file);

                            for (int i = 0; i < filecount; i++) {

                                FileInputStream fstrm = null;
                                try {
                                    fstrm = new FileInputStream(ZipImgLimitPath + name + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                                    FileUpload hfu = new FileUpload(URLS.DataComm.billUpload, "" + ZipImgLimitPath + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");
//                                     FileUpload hfu = new FileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + ZipImgLimitPath + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");

                                    int status = hfu.Send_Now(fstrm);
                                    if (status != 200) {
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
                            progress.dismiss();
                            // new UpdateUI().execute();
                            Toast.makeText(SDActivity.this, "Successfully Uploaded to Server", Toast.LENGTH_LONG).show();
                            checkPendingRecord();
                            pending();
                            setVisible();
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


    /*--------------------- Collection Uplaod Text File Creation ----------------------------------*/
    private class ColTextFileClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        public ColTextFileClass(Context c) {
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
                                "}" + curColselect.getString(27) + "}"+ curColselect.getString(28) + "}"+ curColselect.getString(29) +
                                "}"+ curColselect.getString(30) + "}"+ curColselect.getString(31) + "}"+ curColselect.getString(32) +
                                "}"+ curColselect.getString(33) + "}"+ curColselect.getString(34) + "}"+ curColselect.getString(35) +
                                "}"+ curColselect.getString(37) +"}"+ curColselect.getString(38) + "}"+curColselect.getString(39) + "}");

                        curColselect.moveToNext();
                    }
                    generateColNoteOnSD(getApplicationContext(), "colmobile.csv", mylist);

                }

                SDActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progress.dismiss();
                        new PostClassCol(SDActivity.this).execute();
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
    /*--------------------- Collection Uplaod Zip File  ----------------------------------*/
    private class PostClassCol extends AsyncTask<String, Void, Boolean> {

        private final Context context;
        public String succsess = null;

        public PostClassCol(Context c) {

            this.context = c;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();

            zipFolder(ZipCopyColPath, ZipDesColPath);
            GSBilling.getInstance().setFinalZipName(ZipDesColPathdup);

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip");
               HttpFileUpload hfu = new HttpFileUpload("http://enservtest.fedco.co.in/MPSurvey/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
     //           HttpFileUpload hfu = new HttpFileUpload("http://enservtest.fedco.co.in/MPSurvey/api/UploadFile/LogSessionMobile", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                 HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");

                int status = hfu.Send_Now(fstrm);
                if (status != 200) {

                    SDActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();
                            Toast.makeText(SDActivity.this, "Internaly Stored Due to No Connectivity", Toast.LENGTH_LONG).show();

                        }
                    });

                } else {

                    dbHelper2 = new DB(getApplicationContext());
                    SD2 = dbHelper2.getWritableDatabase();

                    String updatequer = "UPDATE  TBL_COLMASTER_MP  SET Upload_Flag = 'Y'";
                    Cursor curBillupdate = SD2.rawQuery(updatequer, null);
                    if (curBillupdate != null && curBillupdate.moveToFirst()) {
                        Log.v("Update ", "Success");
                    }

                    SDActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();

                            Toast.makeText(SDActivity.this, "Successfully Uploaded to Server", Toast.LENGTH_LONG).show();
                            pending();
                            setVisible();
                        }
                    });
                }
            } catch (Exception e) {
                // Error: File not found
                succsess = "0";
                e.printStackTrace();

            }

            return true;

        }

        protected void onPostExecute() {
            progress.dismiss();

            System.out.println("sucess in post");
            new File(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip").delete();

        }

    }


    /*--------------------- Meter Reading Uplaod Text File Creation ----------------------------------*/
    private class TextFileMeterClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        public TextFileMeterClass(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();

            File prevFile = new File(ZipImgLimitPathMeter);
            DeleteRecursive(prevFile);
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
                    while (curMetselect.isAfterLast() == false) {
                        counter++;
                        // String name = curBillselect.getString(0);


                        mylist.add(curMetselect.getString(0) + "}" + curMetselect.getString(1) + "}" + curMetselect.getString(2) +
                                "}" + curMetselect.getString(3) + "}" + curMetselect.getString(4) + "}" + curMetselect.getString(5) +
                                "}" + curMetselect.getString(6) + "}" + curMetselect.getString(7) + "}" + curMetselect.getString(8) +
                                "}" + curMetselect.getString(9) + "}" + curMetselect.getString(10) + "}" + curMetselect.getString(11) +
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
                                "}" + curMetselect.getString(70) + "}" + curMetselect.getString(71) + "}" + curMetselect.getString(72) +
                                "}" + curMetselect.getString(77) + "}" + curMetselect.getString(78) + "}" + curMetselect.getString(79) +
                                "}" + curMetselect.getString(80) + "}" + curMetselect.getString(81) + "}" + curMetselect.getString(82) +
                                "}" + curMetselect.getString(83) + "}" + curMetselect.getString(84) + "}" + curMetselect.getString(85));

                        createfolder(ZipImgCountPathMeter + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount);
                        moveFile(ZipSourcePath, curMetselect.getString(60), ZipImgCountPathMeter + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");
                        moveFile(ZipSourcePath, curMetselect.getString(61), ZipImgCountPathMeter + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");

                        curMetselect.moveToNext();

                    }
                    for (int i = 0; i <= counter / globalcount; i++) {

                        selectZipFolder(ZipImgCountPathMeter + "/" + name + GSBilling.getInstance().captureDate() + "_" + i, ZipImgCountPathMeter + "/" + name + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                    }
                    generateNoteMeterOnSD(getApplicationContext(), "metering.csv", mylist);
                }
                String bckquer = "SELECT * FROM TBL_METERUPLOAD ";//WHERE Upload_Flag='N'
                Cursor curBbckquer = SD2.rawQuery(bckquer, null);
                String arrStr3[] = null;
                ArrayList<String> mylist3 = new ArrayList<String>();

                if (curBbckquer != null && curBbckquer.moveToFirst()) {

                    while (curBbckquer.isAfterLast() == false) {

                        mylist3.add(curBbckquer.getString(0) + "}" + curBbckquer.getString(1) + "}" + curBbckquer.getString(2) +
                                "}" + curBbckquer.getString(3) + "}" + curBbckquer.getString(4) + "}" + curBbckquer.getString(5) +
                                "}" + curBbckquer.getString(6) + "}" + curBbckquer.getString(7) + "}" + curBbckquer.getString(8) +
                                "}" + curBbckquer.getString(9) + "}" + curBbckquer.getString(10) + "}" + curBbckquer.getString(11) +
                                "}" + curBbckquer.getString(12) + "}" + curBbckquer.getString(13) + "}" + curBbckquer.getString(14) +
                                "}" + curBbckquer.getString(15) + "}" + curBbckquer.getString(16) + "}" + curBbckquer.getString(17) +
                                "}" + curBbckquer.getString(18) + "}" + curBbckquer.getString(19) + "}" + curBbckquer.getString(20) +
                                "}" + curBbckquer.getString(21) + "}" + curBbckquer.getString(22) + "}" + curBbckquer.getString(23) +
                                "}" + curBbckquer.getString(24) + "}" + curBbckquer.getString(25) + "}" + curBbckquer.getString(26) +
                                "}" + curBbckquer.getString(27) + "}" + curBbckquer.getString(28) + "}" + curBbckquer.getString(29) +
                                "}" + curBbckquer.getString(30) + "}" + curBbckquer.getString(31) + "}" + curBbckquer.getString(32) +
                                "}" + curBbckquer.getString(33) + "}" + curBbckquer.getString(34) + "}" + curBbckquer.getString(35) +
                                "}" + curBbckquer.getString(36) + "}" + curBbckquer.getString(37) + "}" + curBbckquer.getString(38) +
                                "}" + curBbckquer.getString(39) + "}" + curBbckquer.getString(40) + "}" + curBbckquer.getString(41) +
                                "}" + curBbckquer.getString(42) + "}" + curBbckquer.getString(43) + "}" + curBbckquer.getString(44) +
                                "}" + curBbckquer.getString(45) + "}" + curBbckquer.getString(46) + "}" + curBbckquer.getString(47) +
                                "}" + curBbckquer.getString(48) + "}" + curBbckquer.getString(49) + "}" + curBbckquer.getString(50) +
                                "}" + curBbckquer.getString(51) + "}" + curBbckquer.getString(52) + "}" + curBbckquer.getString(53) +
                                "}" + curBbckquer.getString(54) + "}" + curBbckquer.getString(55) + "}" + curBbckquer.getString(56) +
                                "}" + curBbckquer.getString(57) + "}" + curBbckquer.getString(58) + "}" + curBbckquer.getString(60) +
                                "}" + curBbckquer.getString(61) + "}" + curBbckquer.getString(62) + "}" + curBbckquer.getString(63) +
                                "}" + curBbckquer.getString(64) + "}" + curBbckquer.getString(65) + "}" + curBbckquer.getString(66) +
                                "}" + curBbckquer.getString(67) + "}" + curBbckquer.getString(68) + "}" + curBbckquer.getString(69) +
                                "}" + curBbckquer.getString(70) + "}" + curBbckquer.getString(71) + "}" + curBbckquer.getString(72) +
                                "}" + curBbckquer.getString(77) + "}" + curBbckquer.getString(78) + "}" + curBbckquer.getString(79) +
                                "}" + curBbckquer.getString(80) + "}" + curBbckquer.getString(81) + "}" + curBbckquer.getString(82) +
                                "}" + curBbckquer.getString(83) + "}" + curBbckquer.getString(84) + "}" + curBbckquer.getString(85));
                        curBbckquer.moveToNext();
                    }
                    genBck(getApplicationContext(), "meter_bck.csv", mylist3);
                }

                SDActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progress.dismiss();
                        new PostMeterClass(SDActivity.this).execute();
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
    private class PostMeterClass extends AsyncTask<String, Void, Boolean> {

        private final Context context;
        public String succsess = null;

        public PostMeterClass(Context c) {

            this.context = c;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();

            zipFolder(ZipMetCSVPath, ZipDesPath);
            System.out.println("++++++++" + ZipDesPathdup);
            GSBilling.getInstance().setFinalZipName(ZipDesPathdup);

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip");
                FileUpload hfu = new FileUpload("http://enserv.feedbackinfra.com/Webapi_Testing/api/UploadFile/UploadMobileReadingFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                 HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadMobileReadingFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");

                int status = hfu.Send_Now(fstrm);
                // sendFileToServer sendnowFile = new sendFileToServer();
                // sendnowFile.sendFileToServer(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip","http://enserv.feedbackinfra.com/Webapi_Testing/api/UploadFile/UploadFiles"+"" + GSBilling.getInstance().getFinalZipName()+".zip");
                if (status != 200) {

                    SDActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();

                            Toast.makeText(SDActivity.this, "Internaly Stored Due to No Connectivity", Toast.LENGTH_LONG).show();

                        }
                    });

                } else {
                    SDActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            File file = new File(ZipImgCountPathMeter);
                            int filecount = CountRecursive(file);

                            for (int i = 0; i < filecount; i++) {

                                FileInputStream fstrm = null;
                                try {
                                    fstrm = new FileInputStream(ZipImgLimitPathMeter + name + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                                    FileUpload hfu = new FileUpload("http://enserv.feedbackinfra.com/Webapi_Testing/api/UploadFile/UploadMobileReadingFiles", "" + ZipImgLimitPathMeter + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");
//                                     FileUpload hfu = new FileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadMobileReadingFiles", "" + ZipImgLimitPathMeter + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");

                                    int status = hfu.Send_Now(fstrm);
                                    if (status != 200) {
                                    } else {

                                        String[] fileNames = new String[0];
                                        File fileImage = new File(ZipImgLimitPathMeter + name + GSBilling.getInstance().captureDate() + "_" + i + "/");
                                        if (fileImage.isDirectory()) {
                                            fileNames = fileImage.list();
                                        }
                                        int total = 0;
                                        for (int image = 0; image < fileNames.length; image++) {
                                            if (fileNames[image].contains("_kwh.jpg")) {

                                                dbHelper2 = new DB(getApplicationContext());
                                                SD2 = dbHelper2.getWritableDatabase();

                                                String updatequer = "UPDATE  TBL_METERUPLOAD  SET UPLOADFLAG = 'Y' WHERE  CONSUMERNO = '" + fileNames[image].split("_")[1] + "';";

                                                Cursor update = SD2.rawQuery(updatequer, null);
                                                if (update != null && update.moveToFirst()) {
                                                    Log.v("Update ", "Success");
                                                }
                                            }
                                        }
                                    }
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            progress.dismiss();
                            // new UpdateUI().execute();
                            Toast.makeText(SDActivity.this, "Successfully Uploaded to Server", Toast.LENGTH_LONG).show();
                            pending();
                            setVisible();
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


    /*--------------------- Meter Reading Uplaod Text File Creation ----------------------------------*/
    private class TXTConSurvey extends AsyncTask<String, Void, Void> {

        private final Context context;

        TXTConSurvey(Context c) {
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
                Log.e("CONSUMER TEXT ", "DOIN ");
                dbHelper2 = new DB(getApplicationContext());
                SD2 = dbHelper2.getWritableDatabase();

                String selquer = "SELECT * FROM TBL_CONSUMERSURVEY_UPOLOAD WHERE FLAG_UPLOAD='N'";

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
                                "}" + curMetselect.getString(57) + "}" + curMetselect.getString(58) + "}" + curMetselect.getString(59) +
                                "}" + curMetselect.getString(60) + "}" + curMetselect.getString(61) + "}" + curMetselect.getString(62) +
                                "}" + curMetselect.getString(63) + "}" + curMetselect.getString(64) + "}" + curMetselect.getString(65) +
                                "}" + curMetselect.getString(66) + "}" + curMetselect.getString(67) + "}" + curMetselect.getString(68) +
                                "}" + curMetselect.getString(69) + "}" + curMetselect.getString(70) + "}" + curMetselect.getString(71) +
                                "}" + curMetselect.getString(72) + "}" + curMetselect.getString(73) + "}" + curMetselect.getString(74) +
                                "}" + curMetselect.getString(75) + "}" + curMetselect.getString(76) + "}" + curMetselect.getString(77) +
                                "}" + curMetselect.getString(78) + "}" + curMetselect.getString(79) + "}" + curMetselect.getString(80) +
                                "}" + curMetselect.getString(81) + "}" + curMetselect.getString(82) + "}" + curMetselect.getString(83) +
                                "}" + curMetselect.getString(84) + "}" + curMetselect.getString(85) + "}" + curMetselect.getString(86) +
                                "}" + curMetselect.getString(87) + "}" + curMetselect.getString(88) + "}" + curMetselect.getString(89) +
                                "}" + curMetselect.getString(90) + "}" + curMetselect.getString(91) + "}" + curMetselect.getString(92) +
                                "}" + curMetselect.getString(93) + "}" + curMetselect.getString(94) + "}" +curMetselect.getString(76)  +
                                "}" + curMetselect.getString(96) + "}" + curMetselect.getString(97) + "}" + curMetselect.getString(98)+
                                "}" + curMetselect.getString(99)+"}" + curMetselect.getString(100)+"}" + curMetselect.getString(101)+
                                "}" + curMetselect.getString(102)+"}" + curMetselect.getString(103)+"}" + curMetselect.getString(104)+
                                "}" + curMetselect.getString(105)+"}" + curMetselect.getString(106)+"}" + curMetselect.getString(107)+
                                "}" + curMetselect.getString(108)+"}" + curMetselect.getString(109)+"}" + curMetselect.getString(110));


                        createfolder(ZipImgCountPathCS + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount);
                        moveFile(ZipSourcePath, curMetselect.getString(71), ZipImgCountPathCS + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");
                        moveFile(ZipSourcePath, curMetselect.getString(72), ZipImgCountPathCS + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");

                        curMetselect.moveToNext();

                    }
                    for (int i = 0; i <= counter / globalcount; i++) {

                        selectZipFolder(ZipImgCountPathCS + "/" + name + GSBilling.getInstance().captureDate() + "_" + i, ZipImgCountPathCS + "/" + name + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                    }
                    generateNoteCSOnSD(getApplicationContext(), "consumer.csv", mylist);
                }

                SDActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progress.dismiss();
                        new PostConSurvey(SDActivity.this).execute();
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
    public class PostConSurvey extends AsyncTask<String, Void, Boolean> {

        private final Context context;
        public String succsess = null;

        PostConSurvey(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Please Wait..");
            progress.show();

            Log.e("CONSUMER POST ", "PRE ");
            zipFolder(ZipCSCSVPath, ZipDesPath);
            System.out.println("++++++++" + ZipDesPathdup);
            GSBilling.getInstance().setFinalZipName(ZipDesPathdup);

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                Log.e("CONSUMER POST ", "DOIN  ");
                FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip");
                HttpFileUpload hfu = new HttpFileUpload("http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles/", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadMobileReadingFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                // HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");

                int status = hfu.Send_Now(fstrm);

                if (status != 200) {

                    SDActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();

                            Toast.makeText(SDActivity.this, "Internaly Stored Due to No Connectivity", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), SurveyDetails.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            // intent.putExtra("Position", 1);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);
                        }
                    });

                } else {


                    File file = new File(ZipImgCountPathCS);
                    int filecount = CountRecursive(file);

                    for (int i = 0; i < filecount; i++) {

                        FileInputStream fstrm2 = null;
                        try {
                            fstrm2 = new FileInputStream(ZipImgLimitPathCS + name + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                            FileUpload hfu2 = new FileUpload("http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles", "" + ZipImgLimitPathCS + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");
//                            FileUpload hfu2 = new FileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadMobileReadingFiles", "" + ZipImgLimitPathMeter + MRname + GSBilling.getInstance().captureDate() + "_" + i, ".zip");
                            // FileUpload hfu = new FileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + ZipImgLimitPath + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");

                            int status2 = hfu2.Send_Now(fstrm2);
                            if (status2 != 200) {


                            } else {
                                String[] fileNames = new String[0];
                                File fileImage = new File(ZipImgLimitPathCS + name + GSBilling.getInstance().captureDate() + "_" + i + "/");
                                if (fileImage.isDirectory()) {
                                    fileNames = fileImage.list();
                                }
                                int total = 0;
                                for (int image = 0; image < fileNames.length; image++) {
                                    if (fileNames[image].contains("_MTR.jpg")) {

                                        dbHelper2 = new DB(getApplicationContext());
                                        SD2 = dbHelper2.getWritableDatabase();

                                        String updatequer = "UPDATE  TBL_CONSUMERSURVEY_UPOLOAD  SET FLAG_UPLOAD = 'Y' WHERE  Consumer_Number = '" + fileNames[image].split("_")[1] + "';";
                                        Log.v("Update ", "QWERY "+updatequer);
                                        Cursor update = SD2.rawQuery(updatequer, null);
                                        if (update != null && update.moveToFirst()) {
                                            Log.v("Update ", "Success");
                                        }
                                    }
                                }

                            }
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    }
                    SDActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();
                            // new UpdateUI().execute();
//                            Toast.makeText(SurveySDActivity.this, "Successfully Uploaded to Server", Toast.LENGTH_LONG).show();
                            pending();
                            setVisible();
                            checkPendingRecord();
//////                            consumer_Pending,dtr_Pending,feeder_Pending,pole_Pending;
//                            if (feeder_Pending != 0) {
//                                Log.e("Collection", " ON");
//                                new TXTFeederClass(SDActivity.this).execute();
//
//                                pending();
//                                setVisible();
//                                checkPendingRecord();
//                            }
//                            if (dtr_Pending != 0) {
//                                Log.e("Meter", " ON");
//                                new TXTDTRurvey(SDActivity.this).execute();
//
//                                pending();
//                                setVisible();
//                                checkPendingRecord();
//                            }
//                            if (pole_Pending != 0) {
//                                Log.e("Meter", " ON");
//                                new TXTPoleClass(SDActivity.this).execute();
//
//                                pending();
//                                setVisible();
//                                checkPendingRecord();
////
//                            }
//

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


    /*--------------------- Meter Reading Uplaod Text File Creation ----------------------------------*/
    private class TXTDTRurvey extends AsyncTask<String, Void, Void> {

        private final Context context;

        TXTDTRurvey(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {

            Log.e("DTR TEXT ", "PRE ");
            progress = new ProgressDialog(this.context);
            progress.setMessage("Uploading Please Wait..");
            progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                Log.e("DTR TEXT ", "DOIN ");
                dbHelper2 = new DB(getApplicationContext());
                SD2 = dbHelper2.getWritableDatabase();

                String selquer = "SELECT * FROM TBL_DTR_UPLOAD WHERE FLAG_UPLOAD='N'";//

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
                                "}" + curMetselect.getString(27) + "}" + curMetselect.getString(15) + "}" + curMetselect.getString(29) +
                                "}" + curMetselect.getString(30) + "}" + curMetselect.getString(31) + "}" + curMetselect.getString(32)+
                                "}" + curMetselect.getString(33)+ "}" + curMetselect.getString(34));


                        createfolder(ZipImgCountPathDTR + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount);
                        moveFile(ZipSourcePath, curMetselect.getString(10), ZipImgCountPathDTR + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");
                        moveFile(ZipSourcePath, curMetselect.getString(11), ZipImgCountPathDTR + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");

                        curMetselect.moveToNext();

                    }
                    for (int i = 0; i < counter / globalcount; i++) {

                        selectZipFolder(ZipImgCountPathDTR + "/" + name + GSBilling.getInstance().captureDate() + "_" + i, ZipImgCountPathDTR + "/" +
                                name + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                    }
                    generateNoteDTROnSD(getApplicationContext(), "dtr.csv", mylist);
                }

                SDActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progress.dismiss();
                        new PostDTRClass(SDActivity.this).execute();
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
    /*--------------------- DTR SURVEY Uplaod File  ----------------------------------*/
    public class PostDTRClass extends AsyncTask<String, Void, Boolean> {

        private final Context context;
        public String succsess = null;

        PostDTRClass(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Please Wait..");
            progress.show();

            Log.e("DTR POST ", "PRE ");
            zipFolder(ZipDTRCSVPath, ZipDesPath);
            System.out.println("++++++++" + ZipDesPathdup);
            GSBilling.getInstance().setFinalZipName(ZipDesPathdup);

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                Log.e("DTR POST ", "DOIN ");
                FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip");
                HttpFileUpload hfu = new HttpFileUpload("http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles/", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                //                HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadMobileReadingFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                // HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");

                int status = hfu.Send_Now(fstrm);

                if (status != 200) {

                    SDActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();

                            Toast.makeText(SDActivity.this, "Internaly Stored Due to No Connectivity", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), SurveyDetails.class);
                            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            // intent.putExtra("Position", 1);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);
                        }
                    });

                } else {


                    File file = new File(ZipImgCountPathDTR);
                    int filecount = CountRecursive(file);

                    for (int i = 0; i < filecount; i++) {

                        FileInputStream fstrm2 = null;
                        try {
                            fstrm2 = new FileInputStream(ZipImgLimitPathDTR + name + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                            FileUpload hfu2 = new FileUpload("http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles", "" + ZipImgLimitPathDTR + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");
                            // FileUpload hfu2 = new FileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadMobileReadingFiles", "" + ZipImgLimitPathMeter + MRname + GSBilling.getInstance().captureDate() + "_" + i, ".zip");
                            // FileUpload hfu = new FileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + ZipImgLimitPath + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");

                            int status2 = hfu2.Send_Now(fstrm2);
                            if (status2 != 200) {
                            } else {

                                String[] fileNames = new String[0];
                                File fileImage = new File(ZipImgLimitPathDTR + name + GSBilling.getInstance().captureDate() + "_" + i + "/");
                                if (fileImage.isDirectory()) {
                                    fileNames = fileImage.list();
                                }
                                int total = 0;
                                for (int image = 0; image < fileNames.length; image++) {
                                    if (fileNames[image].contains("_MTR.jpg")) {

                                        dbHelper2 = new DB(getApplicationContext());
                                        SD2 = dbHelper2.getWritableDatabase();

                                        String updatequer = "UPDATE  TBL_DTR_UPLOAD  SET FLAG_UPLOAD = 'Y' WHERE  DTR_CODING = '" + fileNames[image].split("_")[1] + "';";

                                        Cursor update = SD2.rawQuery(updatequer, null);
                                        if (update != null && update.moveToFirst()) {
                                            Log.v("Update ", "Success");
                                        }
                                    }
                                }

                            }
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    }
                    SDActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();
                            // new UpdateUI().execute();
                            checkPendingRecord();
                            pending();
                            setVisible();

                            //   if (consumer_Pending != 0) {
                            //       Log.e("BLLING", " ON");
                            //       new TXTConSurvey(SDActivity.this).execute();

                            //       checkPendingRecord();
                            //       pending();
                            //       setVisible();

                            //   }
                            //   if (feeder_Pending != 0) {
                            //       Log.e("Collection", " ON");
                            //       new TXTFeederClass(SDActivity.this).execute();

                            //       checkPendingRecord();
                            //       pending();
                            //       setVisible();

                            //   }if (pole_Pending != 0) {
                            //       Log.e("Meter", " ON");
                            //       new TXTPoleClass(SDActivity.this).execute();

                            //       checkPendingRecord();
                            //       pending();
                            //       setVisible();
                            //   }


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
            // File file = new File(ZipImgCountPath);
            // DeleteRecursive(file);
            new File(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip").delete();

        }

    }


    /*--------------------- Meter Reading Uplaod Text File Creation ----------------------------------*/
    private class TXTFeederClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        TXTFeederClass(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {

            Log.e("FEEDER POST ", "PRE ");
            progress = new ProgressDialog(this.context);
            progress.setMessage("Uploading Please Wait..");
            progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                Log.e("FEEDER POST ", "DOIN ");
                dbHelper2 = new DB(getApplicationContext());
                SD2 = dbHelper2.getWritableDatabase();

                String selquer = "SELECT * FROM TBL_11KVFEEDER_UPLOAD WHERE FLAG_UPLOAD='N'";

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
                                "}" + curMetselect.getString(42) + "}" + curMetselect.getString(30)+"}" + curMetselect.getString(44)+
                                "}" + curMetselect.getString(45) + "}" + curMetselect.getString(46)+ "}" + curMetselect.getString(47)+
                                "}" + curMetselect.getString(48) + "}" + curMetselect.getString(49)+ "}" + curMetselect.getString(50)+
                                "}" + curMetselect.getString(51) + "}" + curMetselect.getString(52)+ "}" + curMetselect.getString(53)+
                                "}" + curMetselect.getString(54));

                        createfolder(ZipImgLimitPathFeed + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount);
                        moveFile(ZipSourcePath, curMetselect.getString(26), ZipImgLimitPathFeed + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");
                        //moveFile(ZipSourcePath, curMetselect.getString(11), ZipImgLimitPathMeter + "/" + MRname + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");

                        curMetselect.moveToNext();

                    }
                    for (int i = 0; i <= counter / globalcount; i++) {

                        selectZipFolder(ZipImgLimitPathFeed + "/" + name + GSBilling.getInstance().captureDate() + "_" + i, ZipImgLimitPathFeed + "/" + name + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                    }
                    generateNoteFeederOnSD(getApplicationContext(), "feeder.csv", mylist);
                }

                SDActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progress.dismiss();
                        new PostFeederClass(SDActivity.this).execute();
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
    public class PostFeederClass extends AsyncTask<String, Void, Boolean> {

        private final Context context;
        public String succsess = null;

        PostFeederClass(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {

            Log.e("FEEDER POST", "PRE ");
            progress = new ProgressDialog(this.context);
            progress.setMessage("Please Wait..");
            progress.show();

            zipFolder(ZipFEEDCSVPath, ZipDesPath);
            System.out.println("++++++++" + ZipDesPathdup);
            GSBilling.getInstance().setFinalZipName(ZipDesPathdup);

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                Log.e("FEEDER POST", "DOIN ");
                FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip");
                HttpFileUpload hfu = new HttpFileUpload("http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles/", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                // HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadMobileReadingFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                // HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");

                int status = hfu.Send_Now(fstrm);

                if (status != 200) {

                    SDActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();

                            Toast.makeText(SDActivity.this, "Internaly Stored Due to No Connectivity", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), SurveyDetails.class);
                            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            // intent.putExtra("Position", 1);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);
                        }
                    });

                } else {


                    File file = new File(ZipImgLimitPathFeed);
                    int filecount = CountRecursive(file);

                    for (int i = 0; i < filecount; i++) {

                        FileInputStream fstrm2 = null;
                        try {
                            fstrm2 = new FileInputStream(ZipImgLimitPathFeed + name + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                            FileUpload hfu2 = new FileUpload("http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles", "" + ZipImgLimitPathFeed + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");
                            // FileUpload hfu2 = new FileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadMobileReadingFiles", "" + ZipImgLimitPathMeter + MRname + GSBilling.getInstance().captureDate() + "_" + i, ".zip");
                            // FileUpload hfu = new FileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + ZipImgLimitPath + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");

                            int status2 = hfu2.Send_Now(fstrm2);
                            if (status2 != 200) {
                            } else {

                                String[] fileNames = new String[0];
                                File fileImage = new File(ZipImgLimitPathFeed + name + GSBilling.getInstance().captureDate() + "_" + i + "/");
                                if (fileImage.isDirectory()) {
                                    fileNames = fileImage.list();
                                }
                                int total = 0;
                                for (int image = 0; image < fileNames.length; image++) {
                                    if (fileNames[image].contains("_MTR.jpg")) {

                                        dbHelper2 = new DB(getApplicationContext());
                                        SD2 = dbHelper2.getWritableDatabase();

                                        String updatequer = "UPDATE  TBL_11KVFEEDER_UPLOAD  SET FLAG_UPLOAD = 'Y' WHERE  FEEDER_NAME = '" + fileNames[image].split("_")[1] + "';";

                                        Cursor update = SD2.rawQuery(updatequer, null);
                                        if (update != null && update.moveToFirst()) {
                                            Log.v("Update ", "Success");
                                        }
                                    }
                                }

                            }
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    }
                    SDActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();
                            checkPendingRecord();
                            pending();
                            setVisible();

//                            if (consumer_Pending != 0) {
//                                Log.e("BLLING", " ON");
//                                new TXTConSurvey(SDActivity.this).execute();
//
//
//                                checkPendingRecord();
//                                pending();
//                                setVisible();
//
//                            }
//                            if (dtr_Pending != 0) {
//                                Log.e("Meter", " ON");
//                                new TXTDTRurvey(SDActivity.this).execute();
//
//                                checkPendingRecord();
//                                pending();
//                                setVisible();
//
//                            }
//                            if (pole_Pending != 0) {
//                                Log.e("Meter", " ON");
//                                new TXTPoleClass(SDActivity.this).execute();
//
//                                checkPendingRecord();
//                                pending();
//                                setVisible();
//                            }

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


    /*--------------------- Meter Reading Uplaod Text File Creation ----------------------------------*/
    private class TXTPoleClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        TXTPoleClass(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {

            Log.e("POLE TXT ", "PRE ");
            progress = new ProgressDialog(this.context);
            progress.setMessage("Uploading Please Wait..");
            progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                Log.e("POLE TXT ", "DOIN ");
                dbHelper2 = new DB(getApplicationContext());
                SD2 = dbHelper2.getWritableDatabase();

                String selquer = "SELECT * FROM TBL_POLE_UPLOAD WHERE FLAG_UPLOAD='N'";//

                Cursor curMetselect = SD2.rawQuery(selquer, null);
                String arrStr[] = null;
                ArrayList<String> mylist = new ArrayList<String>();
                // Removefolder(ZipImgLimitPath);
                if (curMetselect != null && curMetselect.moveToFirst()) {
                    int counter = 0;
                    while (!curMetselect.isAfterLast()) {
                        counter++;


                        mylist.add(curMetselect.getString(0)+ "}" + curMetselect.getString(1) + "}" + curMetselect.getString(2) +
                                "}" + curMetselect.getString(3)  + "}" + curMetselect.getString(4) + "}" + curMetselect.getString(5) +
                                "}" + curMetselect.getString(6)  + "}" + curMetselect.getString(7) + "}" + curMetselect.getString(8) +
                                "}" + curMetselect.getString(9)  + "}" + curMetselect.getString(10) + "}" + curMetselect.getString(11) +
                                "}" + curMetselect.getString(12) + "}" + curMetselect.getString(13) + "}" + curMetselect.getString(14) +
                                "}" + curMetselect.getString(15) + "}" + curMetselect.getString(16) + "}" + curMetselect.getString(17)+
                                "}" + curMetselect.getString(7) + "}" + curMetselect.getString(19) + "}" + curMetselect.getString(20)+
                                "}" + curMetselect.getString(21)+"}" + curMetselect.getString(22)+"}" + curMetselect.getString(23)+
                                "}" + curMetselect.getString(24));
                    }
                    generatePoleNoteOnSD(getApplicationContext(), "pole.csv", mylist);
                }
                SDActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progress.dismiss();
//
                        new PostPoleClass(SDActivity.this).execute();


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
    /*--------------------- Meter Reading Uplaod Upload billing File  ----------------------------------*/
    public class PostPoleClass extends AsyncTask<String, Void, Boolean> {

        private final Context context;
        public String succsess = null;

        PostPoleClass(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {

            Log.e("POLE POST ", "PRE ");
            progress = new ProgressDialog(this.context);
            progress.setMessage("Please Wait..");
            progress.show();

            zipFolder(ZipPOLECSVPath, ZipDesPath);
            System.out.println("++++++++" + ZipDesPathdup);
            GSBilling.getInstance().setFinalZipName(ZipDesPathdup);

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {


                Log.e("POLE POST ", "DOIN ");
                FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip");
                HttpFileUpload hfu = new HttpFileUpload("http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadMobileReadingFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                // HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");

                int status = hfu.Send_Now(fstrm);

                if (status != 200) {

                    SDActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();
                            Toast.makeText(SDActivity.this, "Internaly Stored Due to No Connectivity", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), SurveyDetails.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            // intent.putExtra("Position", 1);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);
                        }
                    });

                } else {

                    SDActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();

                            checkPendingRecord();
                            pending();
                            setVisible();

//                            if (consumer_Pending != 0) {
//                                Log.e("BLLING", " ON");
//                                new TXTConSurvey(SDActivity.this).execute();
//
//                                checkPendingRecord();
//                                pending();
//                                setVisible();
//
//                            }
//                            if (feeder_Pending != 0) {
//                                Log.e("Collection", " ON");
//                                new TXTFeederClass(SDActivity.this).execute();
//
//                                checkPendingRecord();
//                                pending();
//                                setVisible();
//
//                            }
//                            if (dtr_Pending != 0) {
//                                Log.e("Meter", " ON");
//                                new TXTDTRurvey(SDActivity.this).execute();
//
//                                checkPendingRecord();
//                                pending();
//                                setVisible();
//                            }

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


    /*--------------------- Billing Uplaod Text File Creation ----------------------------------*/
    private class TextReplaceClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        public TextReplaceClass(Context c) {
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

                String selquer = "SELECT * FROM TBL_METER_REPLACEMENT WHERE UPLOADFLAG='N'";//WHERE Upload_Flag='N'
//                String selColQuer = "SELECT * FROM TBL_COLMASTER_MP WHERE Upload_Flag='N'";//WHERE Upload_Flag='N'
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
                                curBillselect.getString(36) + "}" + curBillselect.getString(37) );


                        createfolder(ZipImgCountPathReplace + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount);
                        moveFile(ZipSourcePath, curBillselect.getString(16), ZipImgCountPathReplace + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");
                        moveFile(ZipSourcePath, curBillselect.getString(28), ZipImgCountPathReplace + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");

                        curBillselect.moveToNext();

                    }
                    for (int i = 0; i <= counter / globalcount; i++) {

                        selectZipFolder(ZipImgCountPathReplace + "/" + name + GSBilling.getInstance().captureDate() + "_" + i+"/", ZipImgCountPathReplace + "/" + name + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                    }
                    NoteOnSD(getApplicationContext(), "meter.csv", mylist,"/MBC/RepalceCSV/");

                }
                String bckquer = "SELECT * FROM TBL_METER_REPLACEMENT ";//WHERE Upload_Flag='N'
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
                                curBbckquer.getString(36) + "}" + curBbckquer.getString(37) );


                        curBbckquer.moveToNext();
                    }
                    NoteOnSD(getApplicationContext(), "repalce_bck.csv", mylist3,"MBC/");

                }

                SDActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progress.dismiss();
                        new PostReplaceClass(SDActivity.this).execute();
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
            // new PostClass(SDActivity.this).execute();
        }

    }
    /*--------------------- Billing Uplaod Upload billing File  ----------------------------------*/
    private class PostReplaceClass extends AsyncTask<String, Void, Boolean> {

        private final Context context;
        public String succsess = null;

        public PostReplaceClass(Context c) {

            this.context = c;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();

            zipFolder(ZipReplaceCSVPath, ZipDesPath);
            System.out.println("++++++++" + ZipDesPathdup);
            GSBilling.getInstance().setFinalZipName(ZipDesPathdup);

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip");
                HttpFileUpload hfu = new HttpFileUpload("http://enservtest.fedco.co.in/MPSurvey/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                 HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");

                int status = hfu.Send_Now(fstrm);
                // sendFileToServer sendnowFile = new sendFileToServer();
                // sendnowFile.sendFileToServer(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip","http://enserv.feedbackinfra.com/Webapi_Testing/api/UploadFile/UploadFiles"+"" + GSBilling.getInstance().getFinalZipName()+".zip");
                if (status != 200) {

                    SDActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();

                            Toast.makeText(SDActivity.this, "Internaly Stored Due to No Connectivity", Toast.LENGTH_LONG).show();

                        }
                    });

                } else {
                    SDActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            dbHelper2 = new DB(getApplicationContext());
                            SD2 = dbHelper2.getWritableDatabase();

                            String updatequer = "UPDATE  TBL_METER_REPLACEMENT  SET UPLOADFLAG = 'Y'";// WHERE  Cons_Number = '" + fileNames[image].split("_")[1] + "';";

                            Cursor update = SD2.rawQuery(updatequer, null);
                            if (update != null && update.moveToFirst()) {
                                Log.v("Update ", "Success");
                            }

                            File file = new File(ZipImgCountPathReplace);
                            int filecount = CountRecursive(file);

                            for (int i = 0; i < filecount; i++) {

                                FileInputStream fstrm = null;
                                try {
                                    fstrm = new FileInputStream(ZipImgLimitPathReplace + name + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                                    FileUpload hfu = new FileUpload("http://enservtest.fedco.co.in/MPSurvey/api/UploadFile/UploadFiles", "" + ZipImgLimitPathReplace + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");
//                                     FileUpload hfu = new FileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + ZipImgLimitPath + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");

                                    int status = hfu.Send_Now(fstrm);
                                    if (status != 200) {
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
                            progress.dismiss();
                            // new UpdateUI().execute();
                            Toast.makeText(SDActivity.this, "Successfully Uploaded to Server", Toast.LENGTH_LONG).show();
                            checkPendingRecord();
                            pending();
                            setVisible();
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
     private class CreateBackupBilling extends AsyncTask<String, Void, Void> {

         private final Context context;

         public CreateBackupBilling(Context c) {
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

                 String bckquer = "SELECT * FROM TBL_BILLING ";
                 Cursor curBbckquer = SD2.rawQuery(bckquer, null);

                 ArrayList<String> mylist3 = new ArrayList<String>();

                 if (curBbckquer != null && curBbckquer.moveToFirst()) {

                     while (curBbckquer.isAfterLast() == false) {

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


                         curBbckquer.moveToNext();

                     }
                     generateBackupFile(getApplicationContext(), "bill_bck.csv", mylist3,
                             "MBC/Backup/Bill/"+GSBilling.getInstance().getFinalZipName()+"/");

                 }

                 SDActivity.this.runOnUiThread(new Runnable() {

                     @Override
                     public void run() {
                         progress.dismiss();

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
             // new PostClass(SDActivity.this).execute();
         }

     }

     /*--------------------- Billing Uplaod Text File Creation ----------------------------------*/
     private class CreateBackupCollection extends AsyncTask<String, Void, Void> {

         private final Context context;

         public CreateBackupCollection(Context c) {
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

                 String bckquer = "SELECT * FROM TBL_COLMASTER_MP";
                 Cursor curBbckquer = SD2.rawQuery(bckquer, null);

                 ArrayList<String> mylist3 = new ArrayList<String>();

                 if (curBbckquer != null && curBbckquer.moveToFirst()) {

                     while (curBbckquer.isAfterLast() == false) {

                         mylist3.add(curBbckquer.getString(1) + "}" + curBbckquer.getString(2) + "}" + curBbckquer.getString(3) +
                                 "}" + curBbckquer.getString(4) + "}" + curBbckquer.getString(5) + "}" + curBbckquer.getString(6) +
                                 "}" + curBbckquer.getString(10) + "}" + curBbckquer.getString(7) + "}" + curBbckquer.getString(8) +
                                 "}" + curBbckquer.getString(9) + "}" + curBbckquer.getString(11) + "}" + curBbckquer.getString(12) +
                                 "}" + curBbckquer.getString(13) + "}" + curBbckquer.getString(14) + "}" + curBbckquer.getString(15) +
                                 "}" + curBbckquer.getString(18) + "}" + curBbckquer.getString(19) + "}" + curBbckquer.getString(20) +
                                 "}" + curBbckquer.getString(21) + "}" + curBbckquer.getString(22) + "}" + curBbckquer.getString(23) +
                                 "}" + curBbckquer.getString(24) + "}" + curBbckquer.getString(25) + "}" + curBbckquer.getString(26) +
                                 "}" + curBbckquer.getString(27) + "}"+ curBbckquer.getString(28) + "}"+ curBbckquer.getString(29) +
                                 "}"+ curBbckquer.getString(30) + "}"+ curBbckquer.getString(31) + "}"+ curBbckquer.getString(32) +
                                 "}"+ curBbckquer.getString(33) + "}"+ curBbckquer.getString(34) + "}"+ curBbckquer.getString(35) +
                                 "}"+ curBbckquer.getString(37) +"}"+ curBbckquer.getString(38) + "}"+curBbckquer.getString(39) + "}");



                         curBbckquer.moveToNext();

                     }
                     generateBackupFile(getApplicationContext(), "col_bck.csv", mylist3,
                             "MBC/Backup/Col/"+GSBilling.getInstance().getFinalZipName()+"/");

                 }

                 SDActivity.this.runOnUiThread(new Runnable() {

                     @Override
                     public void run() {
                         progress.dismiss();

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
             // new PostClass(SDActivity.this).execute();
         }

     }


    public void createfolder(String outputPath) {
        //create output directory if it doesn't exist
        File dir = new File(outputPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void Removefolder(String outputPath) {
        //create output directory if it doesn't exist
        File dir = new File(outputPath);
        if (dir.exists()) {
            dir.delete();
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

    public void generateNoteOnSD(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "MBC/BillCSV/");
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

    public void NoteOnSD(Context context, String sFileName, ArrayList sBody,String filePath) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), filePath);
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

    public void generateNoteFeederOnSD(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "MBC/FEEDERCSV/");
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

    public void generateNoteDTROnSD(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "MBC/DTRCSV/");
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

    public void generateNoteCSOnSD(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "MBC/CSCSV/");
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

    public void generatePoleNoteOnSD(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "MBC/POOLECSV/");
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

    public void generateColNoteOnSD(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "MBC/ColDownloads/");
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

     public void generateBackupFile(Context context, String sFileName, ArrayList sBody, String
             pathFile) {
         try {
             File root = new File(Environment.getExternalStorageDirectory(), pathFile);
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
            zos.close();
        } catch (IOException ioe) {
            Log.e("", ioe.getMessage());
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
            zos.close();
        } catch (IOException ioe) {
            Log.e("", ioe.getMessage());
        }
    }

    public void onBackPressed() {

        finish(); // finish activity
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);

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

    public void DeleteZIP(File dir) {
        String[] fileNames;
        fileNames = dir.list();
        int total = 0;
        for (int i = 0; i < fileNames.length; i++) {
            if (fileNames[i].contains(".zip")) {

                File temp = new File(dir, fileNames[i]);
                boolean b = temp.delete();

            }
        }

    }

    public boolean hasActiveInternetConnection(Context context) {
        if (isNetworkAvailable(getApplicationContext())) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e("NOnet", "Error checking internet connection", e);
            }
        } else {
            Log.d("NOnet", "No network available!");
        }
        return false;
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public void Decompress(String zipFile, String location) {
        _zipFile = zipFile;
        _location = location;

        _dirChecker("");
    }

    public List<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getListFiles(file));
            } else {
                if (file.getName().endsWith(".csv")) {
                    inFiles.add(file);

                }
            }

        }
        return inFiles;
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

    public void _dirChecker(String dir) {
        File f = new File(_location + dir);

        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }

    public static int countLines(File aFile) throws IOException {
        LineNumberReader reader = null;
        try {
            reader = new LineNumberReader(new FileReader(aFile));
            while ((reader.readLine()) != null) ;
            return reader.getLineNumber();
        } catch (Exception ex) {
            return -1;
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    //---pending button code by nitin-------//
    public void pending() {

        pending         = "SELECT  count(*) from 'TBL_BILLING' WHERE Upload_Flag='N'";
        colpending      = "SELECT  count(*) from 'TBL_COLMASTER_MP' WHERE Upload_Flag='N'";
        metpending      = "SELECT  count(*) from 'TBL_METERUPLOAD' WHERE UPLOADFLAG='N'";
        replacepending  = "SELECT  count(*) from 'TBL_METER_REPLACEMENT' WHERE UPLOADFLAG='N'";

        surveyConpending    =   "SELECT  count(*) from 'TBL_CONSUMERSURVEY_UPOLOAD' WHERE FLAG_UPLOAD='N'";
        surveyDTRpending    =  "SELECT count(*) FROM 'TBL_DTR_UPLOAD' WHERE FLAG_UPLOAD='N'";//
        surveyFeederpending =   "SELECT  count(*) from 'TBL_11KVFEEDER_UPLOAD' WHERE FLAG_UPLOAD='N'";
        surveyPolepending   =   "SELECT  count(*) from 'TBL_POLE_UPLOAD' WHERE FLAG_UPLOAD='N'";

        String colpen = "SELECT  * from 'TBL_METERMASTER'";

        curPend = SD3.rawQuery(pending, null);
        curColPend = SD3.rawQuery(colpending, null);
        curMetPend = SD3.rawQuery(metpending, null);
        curReplacePend= SD3.rawQuery(replacepending, null);

        curSurConPend = SD3.rawQuery(surveyConpending, null);
        curSurFeedPend = SD3.rawQuery(surveyFeederpending, null);
        curSurDTRPend = SD3.rawQuery(surveyDTRpending, null);
        curSurPolePend = SD3.rawQuery(surveyPolepending, null);
//        sumCur = SD3.rawQuery(colpen, null);

        if (curPend != null && curPend.moveToFirst()) {
            pend.setText("Total Billing Pending Records           " + curPend.getString(0));
            pend.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));

        }
//        if (curColPend != null && curColPend.moveToFirst()) {
//            colPend.setText("Total Collection Pending Records        " + curColPend.getString(0));
//            colPend.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
//
//        }
        if (curMetPend != null && curMetPend.moveToFirst()) {
            metPend.setText("Total Meter Reading Pending Records      " + curMetPend.getString(0));
            metPend.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));

        }
        if (curSurConPend != null && curSurConPend.moveToFirst()) {
            csPend.setText("Total Consumer Survey Pending Records : " + curSurConPend.getString(0));
            csPend.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));

        }
        if (curSurFeedPend != null && curSurFeedPend.moveToFirst()) {
            feedPend.setText("Total Feeder Survey Pending Records : " + curSurFeedPend.getString(0));
            feedPend.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));

        }
        if (curSurDTRPend != null && curSurDTRPend.moveToFirst()) {
            dtrPend.setText("Total DTR Survey Pending Records : " + curSurDTRPend.getString(0));
            dtrPend.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));

        }
        if (curSurPolePend != null && curSurPolePend.moveToFirst()) {
            polePend.setText("Total Pole Survey Pending Records : " + curSurPolePend.getString(0));
            polePend.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));

        } if (curReplacePend != null && curReplacePend.moveToFirst()) {
            repalcePend.setText("Total Replacement Pending Records : " + curReplacePend.getString(0));
            repalcePend.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));

        }
    }

    public void checkPendingRecord(){

        dbHelper4 = new DB(getApplicationContext());
        SD3 = dbHelper4.getWritableDatabase();

        String pending           = "SELECT  count(*) from 'TBL_BILLING' WHERE Upload_Flag='N'";
        String colpending        = "SELECT  count(*) from 'TBL_COLMASTER_MP' WHERE Upload_Flag='N'";
        String metpending        = "SELECT  count(*) from 'TBL_METERUPLOAD' WHERE UPLOADFLAG='N'";
        String consumerPending   = "SELECT  count(*) from 'TBL_CONSUMERSURVEY_UPOLOAD' WHERE FLAG_UPLOAD='N'";
        String selDTRCount       = "SELECT  count(*) FROM 'TBL_DTR_UPLOAD' WHERE FLAG_UPLOAD='N'";//
        String feederPending     = "SELECT  count(*) from 'TBL_11KVFEEDER_UPLOAD' WHERE FLAG_UPLOAD='N'";
        String polePending       = "SELECT  count(*) from 'TBL_POLE_UPLOAD' WHERE FLAG_UPLOAD='N'";
        String replacepending    = "SELECT  count(*) from 'TBL_METER_REPLACEMENT' WHERE UPLOADFLAG='N'";

        Cursor curBillPend       = SD3.rawQuery(pending, null);
        Cursor curColPend        = SD3.rawQuery(colpending, null);
        Cursor curMetPend        = SD3.rawQuery(metpending, null);
        Cursor curConPend        = SD3.rawQuery(consumerPending, null);
        Cursor curDTRcount       = SD3.rawQuery(selDTRCount, null);
        Cursor curFedPend        = SD3.rawQuery(feederPending, null);
        Cursor curPolePend       = SD3.rawQuery(polePending, null);
        Cursor curReplacePend    = SD3.rawQuery(replacepending, null);

        if(curBillPend != null && curBillPend.moveToFirst()){
            billing_Pending     = Integer.parseInt(curBillPend.getString(0));
        }        if(curColPend != null && curColPend.moveToFirst()){
            collection_Pending     = Integer.parseInt(curColPend.getString(0));
        }        if(curMetPend != null && curMetPend.moveToFirst()){
            meter_Pending     = Integer.parseInt(curMetPend.getString(0));
        }        if(curConPend != null && curConPend.moveToFirst()){
            consumer_Pending     = Integer.parseInt(curConPend.getString(0));
        }if (curDTRcount != null && curDTRcount.moveToFirst()) {
            dtr_Pending = Integer.parseInt(curDTRcount.getString(0));
        }if(curFedPend != null && curFedPend.moveToFirst()){
            feeder_Pending       = Integer.parseInt(curFedPend.getString(0));
        }if(curPolePend != null && curPolePend.moveToFirst()){
            pole_Pending         = Integer.parseInt(curPolePend.getString(0));
        }if(curReplacePend != null && curReplacePend.moveToFirst()){
            replce_Pending       = Integer.parseInt(curReplacePend.getString(0));
        }
    }
    //----For pending button visiblity by nitin----------//
    public void setVisible() {

//        if (billing_Pending > 0 || collection_Pending > 0 || meter_Pending > 0 ||consumer_Pending > 0 || dtr_Pending > 0 || feeder_Pending > 0 || pole_Pending > 0|| replce_Pending > 0) {
        if (billing_Pending > 0 || meter_Pending > 0 ||consumer_Pending > 0 || dtr_Pending > 0 || feeder_Pending > 0 || pole_Pending > 0|| replce_Pending > 0) {
            btnUpload.setVisibility(View.VISIBLE);

        } else {
            btnUpload.setVisibility(View.INVISIBLE);
            btnUpload.setVisibility(View.GONE);
        }

    }

    private void showProgressDialog() {
        if (sDialog == null) {
            sDialog = new SweetAlertDialog(SDActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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
     public void onUserInteraction() {
         super.onUserInteraction();
         ((GlobalPool)getApplication()).onUserInteraction();
     }

     @Override
     public void userLogoutListaner() {
         finish();
         Intent intent=new Intent(SDActivity.this,MainActivity.class);
         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
         startActivity(intent);

     }


     @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }
}
