package com.fedco.mbc.service;

/**
 * Created by soubhagyarm on 21-08-2016.
 */

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.activitysurvey.SurveyDetails;

import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.ConnectionDetector;
import com.fedco.mbc.utils.FileUpload;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Soubhagya Mishra
 */
public class UploadService extends IntentService {

    public Runnable mRunnable = null;

    private NotificationManager nm;
    ConnectionDetector connectionDetector;
    private final Calendar time = Calendar.getInstance();
    Context context;
    private String fName;
    private String lName;
    private String phoneNumber;
    private String latitude;
    private String longitude;

    private String REsponse;
    int id;
    int consumer_Pending,dtr_Pending,feeder_Pending,pole_Pending;

    SQLiteDatabase SD, SD2, SD3;
    DB dbHelper, dbHelper2, dbHelper3, dbHelper4;

    public int globalcount = 10;

    String ZipCSCSVPath         = Environment.getExternalStorageDirectory() + "/MBC/CSCSV/";
    String ZipDTRCSVPath        = Environment.getExternalStorageDirectory() + "/MBC/DTRCSV/";
    String ZipFEEDCSVPath       = Environment.getExternalStorageDirectory() + "/MBC/FEEDERCSV/";
    String ZipPOLECSVPath       = Environment.getExternalStorageDirectory() + "/MBC/POLECSV/";

    String ZipSourcePath        = Environment.getExternalStorageDirectory() + "/MBC/Images/";
    String ZipImgLimitPathCS    = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesCS/";
    String ZipImgCountPathCS    = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesCS";
    String ZipImgLimitPathDTR   = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesDTR/"; //20 count image folder path
    String ZipImgCountPathDTR   = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesDTR";

    String ZipImgLimitPathFeed  = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesFeed/"; //20 count image folder path
    String ZipImgCountPathFeed  = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesFeed";

    String ZipImgLimitPathPole  = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesPOLE/"; //20 count image folder path
    String ZipImgCountPathPole  = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesPOLE";


    public String ZipDesPath, ZipDesColPath;
    String ZipDesPathdup, ZipDesColPathdup;
    String keymap, name;
    SessionManager session;
    UtilAppCommon appCommon;
    Thread t;
    boolean flag = true;
    int pendingcount = 0;
    public UploadService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO Auto-generated method stub

        t = new Thread(new Runnable() {
            @Override
            public void run() {

                while (flag) {
                    startJob();
                    System.out.println("success1");
                    try {
                        Thread.sleep(10 * 60 * 1000);
                        System.out.println("success2");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        t.start();

    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void startJob() {
        //do job here
        appCommon = new UtilAppCommon();
        name = appCommon.UniqueCode(getApplicationContext());

        this.ZipDesPathdup      = "/MBC/" + name + GSBilling.getInstance().captureDatetime();
        this.ZipDesPath         = Environment.getExternalStorageDirectory() + "/MBC/" + name + GSBilling.getInstance().captureDatetime() + ".zip";
        this.ZipDesColPathdup   = "/MBC/" + name + "_col_" + GSBilling.getInstance().captureDatetime();
        this.ZipDesColPath      = Environment.getExternalStorageDirectory() + "/MBC/" + name + "_col_" + GSBilling.getInstance().captureDatetime() + ".zip";

        checkPendingRecord();

        if (isMobileDataEnabled()) {

            System.out.println("success");


            if(consumer_Pending != 0){
                Log.e("Consumer Pending "," ON");
                new TXTConSurvey().execute();

                stopSelf();

            }if(dtr_Pending != 0){
                Log.e("DTR Pending"," ON");
                new TXTDTRSurvey().execute();

                stopSelf();

            }if(feeder_Pending != 0){
                Log.e("Feeder Pending"," ON");
                new TXTFEEDERSurvey().execute();

                stopSelf();

            }if(pole_Pending != 0){
                Log.e("Pole Pending"," ON");
                new TXTPOLESurvey().execute();

                stopSelf();

            }

        } else {

            System.out.println("FAILED ATTEMPT");

        }
    }

    public void checkPendingRecord(){

        dbHelper4 = new DB(getApplicationContext());
        SD3 = dbHelper4.getWritableDatabase();

        String consumerPending   =  "SELECT  count(*) from 'TBL_CONSUMERSURVEY_UPOLOAD' WHERE FLAG_UPLOAD='N'";
        String selDTRCount       =  "SELECT count(*) FROM 'TBL_DTR_UPLOAD' WHERE FLAG_UPLOAD='N'";//

        String feederPending     =  "SELECT  count(*) from 'TBL_11KVFEEDER_UPLOAD' WHERE FLAG_UPLOAD='N'";
        String polePending       =  "SELECT  count(*) from 'TBL_POLE_UPLOAD' WHERE FLAG_UPLOAD='N'";

        Cursor curConPend  = SD3.rawQuery(consumerPending, null);
        Cursor curDTRcount = SD3.rawQuery(selDTRCount, null);
        Cursor curFedPend  = SD3.rawQuery(feederPending, null);
        Cursor curPolePend = SD3.rawQuery(polePending, null);

        if(curConPend != null && curConPend.moveToFirst()){
            consumer_Pending     = Integer.parseInt(curConPend.getString(0));
        }if (curDTRcount != null && curDTRcount.moveToFirst()) {
            dtr_Pending = Integer.parseInt(curDTRcount.getString(0));
        }if(curFedPend != null && curFedPend.moveToFirst()){
            feeder_Pending       = Integer.parseInt(curFedPend.getString(0));
        }if(curPolePend != null && curPolePend.moveToFirst()){
            pole_Pending         = Integer.parseInt(curPolePend.getString(0));
        }
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

    private class TXTConSurvey extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
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


                        createfolder(ZipImgLimitPathCS + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount);
                        moveFile(ZipSourcePath, curMetselect.getString(71), ZipImgLimitPathCS + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");
                        moveFile(ZipSourcePath, curMetselect.getString(72), ZipImgLimitPathCS + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");

                        curMetselect.moveToNext();

                    }
                    for (int i = 0; i <= counter / globalcount; i++) {

                        selectZipFolder(ZipImgLimitPathCS + "/" + name + GSBilling.getInstance().captureDate() + "_" + i, ZipImgLimitPathCS + "/" + name + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                    }
                    generateNoteOnSD(getApplicationContext(), "consumer.csv", mylist,"CSCSV");
                }

            } catch (NullPointerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new PostConSurvey().execute();
        }
    }
    private class PostConSurvey extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            zipFolder(ZipCSCSVPath, ZipDesPath);
            GSBilling.getInstance().setFinalZipName(ZipDesPathdup);

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip");
                HttpFileUpload hfu = new HttpFileUpload("http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles/", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadMobileReadingFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                // HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");

                int status = hfu.Send_Now(fstrm);

                if (status != 200) {

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
                                    if (fileNames[image].contains("_MTR.jpg") ) {

                                        dbHelper2 = new DB(getApplicationContext());
                                        SD2 = dbHelper2.getWritableDatabase();

                                        String updatequer = "UPDATE  TBL_CONSUMERSURVEY_UPOLOAD  SET FLAG_UPLOAD = 'Y' WHERE  Consumer_Number = '" + fileNames[image].split("_")[1] + "';";

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

                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            System.out.println("sucess in post");
            new File(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip").delete();

            checkPendingRecord();
            // if(curPend.getCount()>0){
            if (dtr_Pending > pendingcount || feeder_Pending > pendingcount || pole_Pending > pendingcount ) {

                flag =true;

            }else{

                flag = false;

            }

        }
    }

    private class TXTDTRSurvey extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                dbHelper2 = new DB(getApplicationContext());
                SD2 = dbHelper2.getWritableDatabase();

                String selDTR = "SELECT * FROM 'TBL_DTR_UPLOAD' WHERE FLAG_UPLOAD='N'";//

                Cursor curDTRselect = SD2.rawQuery(selDTR, null);
                String arrStr[] = null;
                ArrayList<String> mylist = new ArrayList<String>();
                // Removefolder(ZipImgLimitPath);
                if (curDTRselect != null && curDTRselect.moveToFirst()) {
                    int counter = 0;
                    while (!curDTRselect.isAfterLast()) {
                        counter++;
                        // String name = curBillselect.getString(0);

                        mylist.add(curDTRselect.getString(0)  + "}" + curDTRselect.getString(1)  + "}" + curDTRselect.getString(2)  +
                                "}" + curDTRselect.getString(3)  + "}" + curDTRselect.getString(4)  + "}" + curDTRselect.getString(5)  +
                                "}" + curDTRselect.getString(6)  + "}" + curDTRselect.getString(7)  + "}" + curDTRselect.getString(8)  +
                                "}" + curDTRselect.getString(9)  + "}" + curDTRselect.getString(10) + "}" + curDTRselect.getString(11) +
                                "}" + curDTRselect.getString(12) + "}" + curDTRselect.getString(13) + "}" + curDTRselect.getString(14) +
                                "}" + curDTRselect.getString(15) + "}" + curDTRselect.getString(16) + "}" + curDTRselect.getString(17) +
                                "}" + curDTRselect.getString(18) + "}" + curDTRselect.getString(19) + "}" + curDTRselect.getString(20) +
                                "}" + curDTRselect.getString(21) + "}" + curDTRselect.getString(22) + "}" + curDTRselect.getString(23) +
                                "}" + curDTRselect.getString(24) + "}" + curDTRselect.getString(25) + "}" + curDTRselect.getString(26) +
                                "}" + curDTRselect.getString(27) + "}" + curDTRselect.getString(15) + "}" + curDTRselect.getString(29) +
                                "}" + curDTRselect.getString(30) + "}" + curDTRselect.getString(31) + "}" + curDTRselect.getString(32)+
                                "}" + curDTRselect.getString(33) + "}" + curDTRselect.getString(34));


                        createfolder(ZipImgLimitPathDTR + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount);
                        moveFile(ZipSourcePath, curDTRselect.getString(10), ZipImgLimitPathDTR + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");
                        moveFile(ZipSourcePath, curDTRselect.getString(11), ZipImgLimitPathDTR + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");

                        curDTRselect.moveToNext();

                    }
                    for (int i = 0; i <= counter / globalcount; i++) {

                        selectZipFolder(ZipImgLimitPathDTR + "/" + name + GSBilling.getInstance().captureDate() + "_" + i, ZipImgLimitPathDTR + "/" +
                                name + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                    }
                    generateNoteOnSD(getApplicationContext(), "dtr.csv", mylist,"DTRCSV");
                }

            } catch (NullPointerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new PostDTRSurvey().execute();
        }

    }
    private class PostDTRSurvey extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            zipFolder(ZipDTRCSVPath, ZipDesPath);
            GSBilling.getInstance().setFinalZipName(ZipDesPathdup);

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip");
                HttpFileUpload hfu = new HttpFileUpload("http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles/", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                //  HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadMobileReadingFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                // HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");

                int status = hfu.Send_Now(fstrm);

                if (status != 200) {



                } else {


                    File file = new File(ZipImgCountPathDTR);
                    int filecount = CountRecursive(file);

                    for (int i = 0; i < filecount; i++) {

                        FileInputStream fstrm2 = null;
                        try {
                            fstrm2 = new FileInputStream(ZipImgLimitPathDTR + name + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                            FileUpload hfu2 = new FileUpload("http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles", "" + ZipImgLimitPathDTR + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");
//                            FileUpload hfu2 = new FileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadMobileReadingFiles", "" + ZipImgLimitPathMeter + MRname + GSBilling.getInstance().captureDate() + "_" + i, ".zip");
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
                                    if (fileNames[image].contains("_MTR.jpg") ) {

                                        dbHelper2 = new DB(getApplicationContext());
                                        SD2 = dbHelper2.getWritableDatabase();

                                        String updatequer = "UPDATE  'TBL_DTR_UPLOAD'  SET FLAG_UPLOAD = 'Y' WHERE  DTR_CODING = '" + fileNames[image].split("_")[1] + "';";

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

                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            System.out.println("sucess in post");
            new File(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip").delete();

            checkPendingRecord();
            // if(curPend.getCount()>0){
            if (consumer_Pending > pendingcount || feeder_Pending > pendingcount || pole_Pending > pendingcount ) {

                flag =true;

            }else{

                flag = false;

            }

        }

    }

    private class TXTFEEDERSurvey extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                dbHelper2 = new DB(getApplicationContext());
                SD2 = dbHelper2.getWritableDatabase();

                String selquer = "SELECT * FROM 'TBL_11KVFEEDER_UPLOAD' WHERE FLAG_UPLOAD='N'";

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

                        createfolder(ZipImgLimitPathFeed + "/" +name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount);
                        moveFile(ZipSourcePath, curMetselect.getString(26), ZipImgLimitPathFeed + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");
                        //moveFile(ZipSourcePath, curMetselect.getString(11), ZipImgLimitPathMeter + "/" + MRname + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");

                        curMetselect.moveToNext();

                    }
                    for (int i = 0; i <= counter / globalcount; i++) {

                        selectZipFolder(ZipImgLimitPathFeed + "/" + name + GSBilling.getInstance().captureDate() + "_" + i, ZipImgLimitPathFeed + "/" + name + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                    }
                    generateNoteOnSD(getApplicationContext(), "feeder.csv", mylist,"FEEDERCSV");
                }

            } catch (NullPointerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new PostFEEDERSurvey().execute();
        }

    }
    private class PostFEEDERSurvey extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            zipFolder(ZipFEEDCSVPath, ZipDesPath);
            GSBilling.getInstance().setFinalZipName(ZipDesPathdup);

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip");
                HttpFileUpload hfu = new HttpFileUpload("http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles/", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadMobileReadingFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                // HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");

                int status = hfu.Send_Now(fstrm);

                if (status != 200) {


                } else {


                    File file = new File(ZipImgCountPathFeed);
                    int filecount = CountRecursive(file);

                    for (int i = 0; i < filecount; i++) {

                        FileInputStream fstrm2 = null;
                        try {
                            fstrm2 = new FileInputStream(ZipImgLimitPathFeed + name + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                            FileUpload hfu2 = new FileUpload("http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles", "" + ZipImgLimitPathFeed + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");
//                            FileUpload hfu2 = new FileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadMobileReadingFiles", "" + ZipImgLimitPathMeter + MRname + GSBilling.getInstance().captureDate() + "_" + i, ".zip");
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

                                        String updatequer = "UPDATE  'TBL_11KVFEEDER_UPLOAD'  SET FLAG_UPLOAD = 'Y' WHERE  FEEDER_NAME = '" + fileNames[image].split("_")[1] + "';";

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

                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            System.out.println("sucess in post");
            new File(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip").delete();

            checkPendingRecord();
            // if(curPend.getCount()>0){
            if (consumer_Pending > pendingcount || dtr_Pending > pendingcount || pole_Pending > pendingcount) {

                flag = true;

            } else {

                flag = false;

            }

        }

    }

    private class TXTPOLESurvey extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

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
                        // String name = curBillselect.getString(0);


                        mylist.add(curMetselect.getString(0)     + "}" + curMetselect.getString(1)  + "}" + curMetselect.getString(2)  +
                                "}" + curMetselect.getString(3)  + "}" + curMetselect.getString(4)  + "}" + curMetselect.getString(5)  +
                                "}" + curMetselect.getString(6)  + "}" + curMetselect.getString(7)  + "}" + curMetselect.getString(8)  +
                                "}" + curMetselect.getString(9)  + "}" + curMetselect.getString(10) + "}" + curMetselect.getString(11) +
                                "}" + curMetselect.getString(12) + "}" + curMetselect.getString(13) + "}" + curMetselect.getString(14) +
                                "}" + curMetselect.getString(15) + "}" + curMetselect.getString(16) + "}" + curMetselect.getString(17) +
                                "}" + curMetselect.getString(7) + "}" + curMetselect.getString(19) + "}" + curMetselect.getString(20) +
                                "}" + curMetselect.getString(21)+"}" + curMetselect.getString(22)+"}" + curMetselect.getString(23)+
                                "}" + curMetselect.getString(24));

                        createfolder(ZipImgLimitPathPole + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount);
                        // moveFile(ZipSourcePath, curMetselect.getString(60), ZipImgLimitPathMeter + "/" + MRname + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");
                        // moveFile(ZipSourcePath, curMetselect.getString(61), ZipImgLimitPathMeter + "/" + MRname + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");

                        curMetselect.moveToNext();

                    }
                    for (int i = 0; i <= counter / globalcount; i++) {

                        selectZipFolder(ZipImgLimitPathPole + "/" + name + GSBilling.getInstance().captureDate() + "_" + i, ZipImgLimitPathPole + "/" + name + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                    }
                    generateNoteOnSD(getApplicationContext(), "pole.csv", mylist,"POLECSV");
                }

            } catch (NullPointerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new PostPOLESurvey().execute();
        }
    }
    private class PostPOLESurvey extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            zipFolder(ZipPOLECSVPath, ZipDesPath);
            GSBilling.getInstance().setFinalZipName(ZipDesPathdup);

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip");
                HttpFileUpload hfu = new HttpFileUpload("http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles/", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                // HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadMobileReadingFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                // HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");

                int status = hfu.Send_Now(fstrm);
                if (status != 200) {

                } else{
                    dbHelper2 = new DB(getApplicationContext());
                    SD2 = dbHelper2.getWritableDatabase();

                    String updatequer = "UPDATE  TBL_POLE_UPLOAD  SET FLAG_UPLOAD = 'Y'";

                    Cursor update = SD2.rawQuery(updatequer, null);
                    if (update != null && update.moveToFirst()) {
                        Log.v("Update ", "Success");
                    }
                }





            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            System.out.println("sucess in post");
            new File(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip").delete();

            checkPendingRecord();
            // if(curPend.getCount()>0){
            if (consumer_Pending > pendingcount || dtr_Pending > pendingcount || feeder_Pending > pendingcount) {

                flag = true;

            } else {

                flag = false;

            }

        }
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
//            new File(inputPath + inputFile).delete();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateNoteOnSD(Context context, String sFileName, ArrayList sBody ,String filePath) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "MBC/"+filePath+"/");
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
            System.out.println("success file");

//                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
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

    public void createfolder(String outputPath) {
        //create output directory if it doesn't exist
        File dir = new File(outputPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

}
