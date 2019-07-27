package com.fedco.mbc.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.fedco.mbc.activity.SDActivity;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.sqlite.DB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Background Async Task to download file
 */
public class DownloadSyncFileFromURL extends AsyncTask<String, String, String> implements OnCustomDialogClick {


    // Progress Dialog
    private CustomDialogFile pDialog;
    private boolean isDownloadCanceled;
    /*********
     * work only for Dedicated IP
     ***********/
    //static final String FTP_HOST = "103.192.172.42";
    static final String FTP_HOST = "http://enserv.feedbackinfra.com/Webapi/api/DownloadZip/ZipDownload?FileName=BED.zip";
    //static final String FTP_FILE_PATH = "abc.zip";
    static final String FTP_FILE_PATH = "BED.zip";
    HttpURLConnection conn = null;
    TelephonyManager telephonyManager;
    String codeIMEI;
    //private final static int PROGRESS_FAILED = -100;
    private static final String DOWNLOAD_COMPLETED = "Your Download is Completed. Reading is Started";
    private static final String DOWNLOAD_FAILED = "Your Download is Failed.";
    /*********
     * FTP USERNAME
     ***********/
    static final String FTP_USER = "Anonymous";
    public Context context;
    /*********
     * FTP PASSWORD
     ***********/
    static final String FTP_PASS = "anonymous";
    SQLiteDatabase SD, SD2, SD3;
    DB dbHelper, dbHelper2, dbHelper3, dbHelper4;
    SessionManager session;
    private static int mProgress = 0;
    private final static int PROGRESS_FAILED = -100;
    FTPUtility util;
    //Denotes uploaded successfully
    private final static int PROGRESS_COMPLETED = 30;
    private static final int FTP_PORT = 21;
    private static final String LOCAL_PATH = "/MBC/";
    private static final int BUFFER_SIZE = 4096;
    InputStream input;
    String name, IMEInum, loc_Code, gr_no, bill_month,nameLR;

    public DownloadSyncFileFromURL(Context context,String name, String loc_Code, String gr_no, String bill_month,String Name) {
        this.name = name;
        this.loc_Code = loc_Code;
        this.gr_no = gr_no;
        this.bill_month = bill_month;
        this.context=context;
        this.nameLR=Name;
    }

    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Log.d("TAG", " is cancelled " + isCancelled());

    }

    /**
     * Downloading file in background thread
     */
    @Override
    protected String doInBackground(String... f_url) {

        util = new FTPUtility(FTP_HOST, FTP_PORT, FTP_USER, FTP_PASS);

        try {

//            conn = (HttpURLConnection) new URL("http://enservmp.fedco.co.in/mpsurvey/DownloadZip/GetBilledConsumerList/" + loc_Code + "/" + gr_no + "/" + bill_month+ "/" + nameLR).openConnection();
            conn = (HttpURLConnection) new URL("http://enservtest.fedco.co.in/mpsurvey/DownloadZip/GetBilledConsumerList/" + loc_Code + "/" + gr_no + "/" + bill_month+ "/" + nameLR).openConnection();

            System.out.println("reading " + "http://enservtest.fedco.co.in/mpsurvey/DownloadZip/GetBilledConsumerList/" + loc_Code + "/" + gr_no + "/" + bill_month+ "/" + nameLR);

        } catch (IOException e) {
            e.printStackTrace();
        }
        conn.setDoInput(true);
        conn.setConnectTimeout(1000); // timeout 10 secs
        try {
            conn.connect();
            input = conn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            long totalBytesRead = 0;
            int percentCompleted = 0;

            File downloadFile = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + LOCAL_PATH);
            if (!downloadFile.exists()) {
                downloadFile.mkdir();
            }
            String fileName = FTP_FILE_PATH;//new File(downloadFile.getAbsolutePath() + FTP_FILE_PATH).getName();

            FileOutputStream outputStream = new FileOutputStream(new File(downloadFile
                    .getAbsolutePath() + "/" + name + ".zip"));

            InputStream inputStream = util.getInputStream();
            while ((bytesRead = input.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                System.out.println("reading " + totalBytesRead);

            }

            outputStream.flush();
            outputStream.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            cancel(true);

        } finally {
            try {
//                util.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    /**
     * Updating progress bar
     */
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
//        pDialog.setProgress(mProgress);
        Log.d("TAG ", "Progress Value == " + mProgress);
    }

    /**
     * After completing background task
     * Dismiss the progress dialog
     **/
    @Override
    protected void onPostExecute(String file_url) {
        // dismiss the dialog after the file was downloaded
//        pDialog.dismiss();
//        new UnzippingSyncFile(name,loc_Code).execute();

        if (!isDownloadCanceled) {

            Log.e("", "LLLLLLLLLLLLLLLLL");
            new UnzippingSyncFile(context,name, loc_Code).execute();
            Log.e("", "QQQQQQQQQQQQQQQQQ");

        } else {
            File downloadFile = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + LOCAL_PATH + "/" + FTP_FILE_PATH);
            if (downloadFile.exists()) {
                downloadFile.delete();
            }
        }
//        mProgress = 0;
    }

    CountDownTimer timer;

    /**
     * Called to initialize download progresss Bar
     */
    private void notifyProgressFoDownload() {

        final int UPLOAD_TIME = 2 * 60 * 2000;//max download time of 2 min
        final int TIME_INTERVAL = 100;
        final int TIME_OF_PROGRESS = UPLOAD_TIME / 100;
        final int TOTAL_PROGRESS = 100;
        timer = new CountDownTimer(UPLOAD_TIME, TIME_INTERVAL) {
            @Override
            public void onTick(long timeFinished) {

                if (mProgress == PROGRESS_FAILED) {
                    publishProgress(null);
                    timer.cancel();
                    pDialog.dismiss();
                } else if (mProgress == PROGRESS_COMPLETED) {
                    publishProgress(null);
                    pDialog.dismiss();
                    timer.cancel();
                } else {
                    mProgress = TOTAL_PROGRESS - (int) timeFinished / TIME_OF_PROGRESS;
                    timer.cancel();
                    publishProgress(null);
                }
            }

            @Override
            public void onFinish() {
                publishProgress(null);
                pDialog.dismiss();
                timer.cancel();
                onPostExecute(null);
            }
        };

        timer.start();
    }

    @Override
    public void onCustomDialogClickView(boolean isPositiveButtonClick) {
        if (!isPositiveButtonClick) {
            if (conn != null) {
                try {
                    pDialog.dismiss();
                    conn.disconnect();
                    timer.cancel();
                    timer = null;
                    isDownloadCanceled = true;
                    cancel(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Called to show the dialog
     *
     * @param iListener : instance of {@link OnCustomDialogClick}
     */
    public CustomDialogFile showDialog(OnCustomDialogClick iListener) {
        CustomDialogFile dialog = new CustomDialogFile();
        dialog.setCancelable(false);
        dialog.setmClickListener(iListener);

        try {
            dialog.show(SDActivity.getsActivity().getFragmentManager(), CustomDialog.TAG);
        } catch (Exception x) {
            x.printStackTrace();
        }
        return dialog;
    }


}