package com.fedco.mbc.service;

/**
 * Created by soubhagyarm on 21-08-2016.
 */

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.ConnectionDetector;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Soubhagya Mishra
 */
public class FullUploadService extends IntentService {

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
    int billingPending,collectionPending,meterPending;

    SQLiteDatabase SD, SD2, SD3;
    DB dbHelper, dbHelper2, dbHelper3, dbHelper4;

    public int globalcount = 10;
    String ZipCopyPath = Environment.getExternalStorageDirectory() + "/MBC/Downloads/";
    String ZipBillCSVPath = Environment.getExternalStorageDirectory() + "/MBC/BillCSV/";
    String ZipImgLimitPath = Environment.getExternalStorageDirectory() + "/MBC/LimitImages/";
    String ZipImgCountPath = Environment.getExternalStorageDirectory() + "/MBC/LimitImages";
    String ZipCopyColPath = Environment.getExternalStorageDirectory() + "/MBC/ColDownloads/";
    String ZipSourcePath = Environment.getExternalStorageDirectory() + "/MBC/Images/";
    String ZipSourceColPath = Environment.getExternalStorageDirectory() + "/MBC/Images/";
    String ZipImgLimitPathMeter  = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesMeter/";
    String ZipImgLimitCountPathMeter  = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesMeter";
    String ZipMetCSVPath   = Environment.getExternalStorageDirectory() + "/MBC/MetCSV/";
    public String ZipDesPath, ZipDesColPath;
    String ZipDesPathdup, ZipDesColPathdup;
    String keymap, name;
    SessionManager session;
    UtilAppCommon appCommon;
    Thread t;
    boolean flag = true;
    int pendingcount = 0;
    public FullUploadService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO Auto-generated method stub

        t = new Thread(new Runnable() {
            @Override
            public void run() {

//                while (flag) {
                    startJob();
                    System.out.println("Upload Success");
//                    try {
//                        Thread.sleep(10 * 60 * 1000);
//                        System.out.println("success2");
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }

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


            if(billingPending != 0){
                Log.e("BLLING"," ON");
                new CreateTextBilling().execute();

                stopSelf();

            }if(collectionPending != 0){
                Log.e("Collection"," ON");
                new CreateTextCol().execute();

                stopSelf();

            }if(meterPending != 0){
                Log.e("Meter"," ON");
                new CreateTextMeter().execute();

                stopSelf();

            }

        } else {

            System.out.println("FAIL");

        }
    }

    public void checkPendingRecord(){

        dbHelper4 = new DB(getApplicationContext());
        SD3 = dbHelper4.getWritableDatabase();

        String billpending = "SELECT Cons_Number from 'TBL_BILLING'";
        String colpending  = "SELECT  CON_NO from 'TBL_COLMASTER_MP' WHERE Upload_Flag='N'";
        String metpending  = "SELECT  CONSUMERNO from 'TBL_METERUPLOAD' WHERE UPLOADFLAG='N'";

        final Cursor curBillPend = SD3.rawQuery(billpending, null);
        final Cursor curColPend  = SD3.rawQuery(colpending, null);
        final Cursor curMetPend  = SD3.rawQuery(metpending, null);

        billingPending      = curBillPend.getCount();
        collectionPending   = curColPend.getCount();
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


    private class CreateTextBilling extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                dbHelper2 = new DB(getApplicationContext());
                SD2 = dbHelper2.getWritableDatabase();

                String selquer = "SELECT * FROM TBL_BILLING";//WHERE Upload_Flag='N'
                Cursor curBillselect = SD2.rawQuery(selquer, null);
                String arrStr[] = null;
                ArrayList<String> mylist = new ArrayList<String>();
                ArrayList<String> mylist1 = new ArrayList<String>();

                if (curBillselect != null && curBillselect.moveToFirst()) {
                    int counter = 0;
                    int iterate=0;
                    while (curBillselect.isAfterLast() == false) {
                        counter++;
                        Calendar c = Calendar.getInstance();
                        System.out.println("Current time => " + c.getTime());
                        iterate=iterate+1;
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yy");
                        String formattedDate = df.format(c.getTime());
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

                        selectZipFolder(ZipImgCountPath + "/" + name + GSBilling.getInstance().captureDate() + "_" + i, ZipImgCountPath + "/" + name + GSBilling.getInstance().captureDate() + "_" + i + ".zip");

                    }

                    generateNoteOnSD(getApplicationContext(), "billing.csv", mylist);
                    generateNoteOnSD(getApplicationContext(), "billing1.csv", mylist1);

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
            new SendBillingServer().execute();
        }
    }
    private class SendBillingServer extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            zipFolder(ZipBillCSVPath, ZipDesPath);
            GSBilling.getInstance().setFinalZipName(ZipDesPathdup);

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip");
                // Set your server page url (and the file title/description)
                HttpFileUpload hfu = new HttpFileUpload(URLS.DataComm.billUpload, "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                 HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                int status = hfu.Send_Now(fstrm);

                if (status != 200) {

                } else {

                    File file = new File(ZipImgCountPath);
                    //  int filecount=getFolderCount(file);
                    int filecount = CountRecursive(file);

                    for (int i = 0; i < filecount; i++) {

                        FileInputStream fstrmcount = null;
                        try {
                            fstrm = new FileInputStream(ZipImgLimitPath + name + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
//                            FileUpload hfucount = new FileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + ZipImgLimitPath + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");
                            FileUpload hfucount = new FileUpload(URLS.DataComm.billUpload, "" + ZipImgLimitPath + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");
                            // FileUpload hfu   = new FileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + ZipImgLimitPath + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");

                            int statuscount = hfucount.Send_Now(fstrm);
                            if (statuscount != 200) {
                            } else {

                                String[] fileNames = new String[0];
                                File fileImage = new File(ZipImgLimitPath + name + GSBilling.getInstance().captureDate() + "_" + i + "/");
                                if (fileImage.isDirectory()) {
                                    fileNames = fileImage.list();
                                }
                                int total = 0;
                                for (int image = 0; image < fileNames.length; image++) {
                                    if (fileNames[image].contains("_mtr.jpg")) {

                                        dbHelper2 = new DB(getApplicationContext());
                                        SD2 = dbHelper2.getWritableDatabase();

                                        Log.e("Update ", "Successfully updated");
                                        String updatequer = "UPDATE  TBL_BILLING  SET Upload_Flag = 'Y' WHERE  IVRS_NO = '" + fileNames[image].split("_")[2] + "';";
                                        System.out.println("capta " + updatequer);

                                        Cursor update = SD2.rawQuery(updatequer, null);
                                        if (update != null && update.moveToFirst()) {
                                            Log.v("Update ", "Successfully updated");
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
            if (collectionPending > pendingcount || meterPending > pendingcount ) {

                flag =true;

            }else{

                flag = false;

            }

        }
    }

    private class CreateTextCol extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... params) {
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
                        System.out.println("selqwer123 " + name);

                        mylist.add(curColselect.getString(1) + "}" + curColselect.getString(2) + "}" + curColselect.getString(3) +
                                "}" + curColselect.getString(4) + "}" + curColselect.getString(5) + "}" + curColselect.getString(6) +
                                "}" + curColselect.getString(10) + "}" + curColselect.getString(7) + "}" + curColselect.getString(8) +
                                "}" + curColselect.getString(9) + "}" + curColselect.getString(11) + "}" + curColselect.getString(12) +
                                "}" + curColselect.getString(13) + "}" + curColselect.getString(14) + "}" + curColselect.getString(15) +
                                "}" + curColselect.getString(18) + "}" + curColselect.getString(19) + "}" + curColselect.getString(20) +
                                "}" + curColselect.getString(21) + "}" + curColselect.getString(22) + "}" + curColselect.getString(23) +
                                "}" + curColselect.getString(24) + "}" + curColselect.getString(25) + "}" + curColselect.getString(26) +
                                "}" + curColselect.getString(27) + "}"+ curColselect.getString(28) + "}"+ curColselect.getString(29) +
                                "}" + curColselect.getString(30) + "}"+ curColselect.getString(31) + "}"+ curColselect.getString(32) +
                                "}" + curColselect.getString(33) + "}"+ curColselect.getString(34) + "}"+ curColselect.getString(35) +
                              
							    "}" +  curColselect.getString(37) +"}"+ curColselect.getString(38) + "}"+curColselect.getString(39) + "}");

                        // mylist2.add(curColselect.getString(7));
                        curColselect.moveToNext();
                    }
                    generateColNoteOnSD(getApplicationContext(), "colmobile.csv", mylist);

                    String dataBck = "SELECT * FROM TBL_COLMASTER_MP";//WHERE Upload_Flag='N'
                    Cursor curColbck = SD2.rawQuery(dataBck, null);
                    String arrStr2[] = null;
                    ArrayList<String> mylist2 = new ArrayList<String>();

                    if (curColbck != null && curColbck.moveToFirst()) {

                        while (curColbck.isAfterLast() == false) {
                            String name = curColbck.getString(0);

                            mylist.add(curColbck.getString(1) + "}" + curColbck.getString(2) + "}" + curColbck.getString(3) +
                                    "}" + curColbck.getString(4)  + "}" + curColbck.getString(5)  + "}" + curColbck.getString(6) +
                                    "}" + curColbck.getString(10) + "}" + curColbck.getString(7)  + "}" + curColbck.getString(8) +
                                    "}" + curColbck.getString(9)  + "}" + curColbck.getString(11) + "}" + curColbck.getString(12) +
                                    "}" + curColbck.getString(13) + "}" + curColbck.getString(14) + "}" + curColbck.getString(15) +
                                    "}" + curColbck.getString(18) + "}" + curColbck.getString(19) + "}" + curColbck.getString(20) +
                                    "}" + curColbck.getString(21) + "}" + curColbck.getString(22) + "}" + curColbck.getString(23) +
                                    "}" + curColbck.getString(24) + "}" + curColbck.getString(25) + "}" + curColbck.getString(26) +
                                    "}" + curColbck.getString(27) + "}" + curColbck.getString(28) + "}" + curColbck.getString(29) +
                                    "}" + curColbck.getString(30) + "}" + curColbck.getString(31) + "}" + curColbck.getString(32) +
                                    "}" + curColbck.getString(33) + "}" + curColbck.getString(34) + "}" + curColbck.getString(35) +
                                    "}" +  curColbck.getString(37) + "}" + curColbck.getString(38) + "}"+curColbck.getString(39) + "}");
                            // mylist2.add(curColselect.getString(7));


                            curColbck.moveToNext();
                        }
                    }
//                    genBck(getApplicationContext(), "auto_col_bck.csv", mylist2);

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

            Log.e("POST COL TEXT ", "Successfully");
            new SendColServer().execute();

        }
    }
    private class SendColServer extends AsyncTask<String, String, String> {

        public String succsess = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            zipFolder(ZipCopyColPath, ZipDesColPath);
            GSBilling.getInstance().setFinalZipName(ZipDesColPathdup);
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip");
                HttpFileUpload hfu = new HttpFileUpload("http://enservtest.fedco.co.in/MPSurvey/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                // HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                System.out.println("going out " + GSBilling.getInstance().getFinalZipName() + ".zip");
                // hfu.Send_Now(fstrm);
                int status = hfu.Send_Now(fstrm);
                if (status != 200) {
                    // succsess = "1";
                } else {
                    // succsess = "0";
                    dbHelper2 = new DB(getApplicationContext());
                    SD2 = dbHelper2.getWritableDatabase();
                    Log.e("POST COL UPLOAD ", "Successfully");
                    String updatequer = "UPDATE  TBL_COLMASTER_MP  SET Upload_Flag = 'Y'";
                    Cursor curBillupdate = SD2.rawQuery(updatequer, null);
                    if (curBillupdate != null && curBillupdate.moveToFirst()) {
                        Log.v("Update ", "Success");
                    }

                }
            } catch (Exception e) {
                // Error: File not found
                succsess = "0";
                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new File(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip").delete();
            checkPendingRecord();
            // if(curPend.getCount()>0){
            if (billingPending > pendingcount || meterPending > pendingcount ) {

                flag =true;

            }else{

                flag = false;

            }

        }
    }

    private class CreateTextMeter extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... params) {
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
                                "}" + curMetselect.getString(77) + "}" + curMetselect.getString(78) + "}" + curMetselect.getString(79)+
                                "}" + curMetselect.getString(80) + "}" + curMetselect.getString(81)+ "}" + curMetselect.getString(82)+
                                "}" + curMetselect.getString(83)+ "}" + curMetselect.getString(84)+ "}" + curMetselect.getString(85));

                        createfolder(ZipImgLimitPathMeter + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount);
                        moveFile(ZipSourcePath, curMetselect.getString(60), ZipImgLimitPathMeter + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");
                        moveFile(ZipSourcePath, curMetselect.getString(61), ZipImgLimitPathMeter + "/" + name + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");

                        curMetselect.moveToNext();

                    }
                    for (int i = 0; i <= counter / globalcount; i++) {

                        selectZipFolder(ZipImgLimitPathMeter + "/" + name + GSBilling.getInstance().captureDate() + "_" + i, ZipImgLimitPathMeter + "/" + name + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                    }
                    generateNoteMeterOnSD(getApplicationContext(), "metering.csv", mylist);
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

            Log.e("METER TEXT CREATE ", "IN POST Successfully");
            new PostMeterClass(FullUploadService.this).execute();
        }
    }
    public class PostMeterClass extends AsyncTask<String, Void, Boolean> {

        private final Context context;
        public String succsess = null;

        public PostMeterClass(Context c) {

            this.context = c;
        }

        protected void onPreExecute() {

            zipFolder(ZipMetCSVPath, ZipDesPath);
            GSBilling.getInstance().setFinalZipName(ZipDesPathdup);

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                FileInputStream fstrm   = new FileInputStream(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip");
                HttpFileUpload hfu      = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi_Testing/api/UploadFile/UploadMobileReadingFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                HttpFileUpload hfu      = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadMobileReadingFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                // HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");

                int status = hfu.Send_Now(fstrm);
                // sendFileToServer sendnowFile = new sendFileToServer();
                // sendnowFile.sendFileToServer(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip","http://enserv.feedbackinfra.com/Webapi_Testing/api/UploadFile/UploadFiles"+"" + GSBilling.getInstance().getFinalZipName()+".zip");
                if (status != 200) {

                } else {


                    File file       = new File(ZipImgLimitCountPathMeter);
                    int filecount   = CountRecursive(file);

                    for (int i = 0; i < filecount; i++) {

                        FileInputStream fstrm2 = null;
                        try {
                            fstrm2           = new FileInputStream(ZipImgLimitPathMeter + name + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                            HttpFileUpload hfu2  = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi_Testing/api/UploadFile/UploadMobileReadingFiles", "" + ZipImgLimitPathMeter + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");
//                            HttpFileUpload hfu2  = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadMobileReadingFiles", "" + ZipImgLimitPathMeter + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");
                            // FileUpload hfu = new FileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + ZipImgLimitPath + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");

                            int status2  = hfu2.Send_Now(fstrm2);
                            if (status2 != 200) {
                            } else {

                                String[] fileNames  = new String[0];
                                File fileImage      = new File(ZipImgLimitPathMeter + name + GSBilling.getInstance().captureDate() + "_" + i + "/");
                                if (fileImage.isDirectory()) {
                                    fileNames = fileImage.list();
                                }
                                int total = 0;
                                for (int image = 0; image < fileNames.length; image++) {
                                    if (fileNames[image].contains("_kwh.jpg") ) {

                                        dbHelper2   = new DB(getApplicationContext());
                                        SD2         = dbHelper2.getWritableDatabase();

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

                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        protected void onPostExecute() {
            checkPendingRecord();
            Log.e("METER SERVER SEND ", "IN POST Successfully");
            // if(curPend.getCount()>0){
            if (collectionPending > pendingcount || billingPending > pendingcount ) {

                flag =true;

            }else{

                flag = false;

            }
            new File(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip").delete();

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


//                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateNoteOnSD(Context context, String sFileName, ArrayList sBody) {
        try {
//            File root = new File(Environment.get(), "MBC/BillCSVUPL/");
//            if (!root.exists()) {
//                root.mkdirs();
//            } else {
//                root.delete();
//            }
            File root = new File(Environment.getExternalStorageDirectory() + "/MBC/BillCSV/");
            boolean success = true;
            if (!root.exists()) {
                success = root.mkdir();
            }
            if (success) {
                // Do something on success
            } else {
                // Do something else on failure
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
            File srcFile2 = new File(Environment.getExternalStorageDirectory() + "/MBC/LimitImages");
            File[] files2 = srcFile2.listFiles();
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
        File root = new File(Environment.getExternalStorageDirectory() + outputPath);
        boolean success = true;
        if (!root.exists()) {
            success = root.mkdirs();
        }
        if (success) {
            // Do something on success
        } else {
            // Do something else on failure
        }
    }

}
