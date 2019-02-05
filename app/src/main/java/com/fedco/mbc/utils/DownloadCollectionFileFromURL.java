package com.fedco.mbc.utils;

import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.fedco.mbc.activity.SDActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Background Async Task to download file
 */
 public class DownloadCollectionFileFromURL extends AsyncTask<String, String, String> implements OnCustomDialogClick{


    // Progress Dialog
    private CustomDialogFile pDialog;
    private boolean isDownloadCanceled;
    HttpURLConnection conn = null;
    InputStream input;
    /*********
     * work only for Dedicated IP
     ***********/
//    static final String FTP_HOST = "103.192.172.42";
    static final String FTP_HOST = "http://enserv.feedbackinfra.com/Webapi/api/DownloadZip/ZipDownload?FileName=CED.zip";
    static final String FTP_FILE_PATH = "CED.zip";

//    private final static int PROGRESS_FAILED = -100;
    private static final String DOWNLOAD_COMPLETED = "Your Download is Completed. Reading is Started";
    private static final String DOWNLOAD_FAILED = "Your Download is Failed.";

    /*********
     * FTP USERNAME
     ***********/
    static final String FTP_USER = "Anonymous";

    /*********
     * FTP PASSWORD
     ***********/
    static final String FTP_PASS = "anonymous";
    String name,IMEInum;

    private static int mProgress = 0;
    private final static int PROGRESS_FAILED = -100;
    FTPUtility util;

    //Denotes uploaded successfully
    private final static int PROGRESS_COMPLETED = 100;
    private static final int FTP_PORT = 21;
    private static final String LOCAL_PATH = "/MBC";
    private static final int BUFFER_SIZE = 4096;

    public DownloadCollectionFileFromURL(String zipFilename , String imeinum ){
        this.name=zipFilename;
        this.IMEInum=imeinum;
    }
    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = showDialog(this);
        Log.d("TAG", " is cancelled " + isCancelled());
        notifyProgressFoDownload();
    }

    /**
     * Downloading file in background thread
     */
    @Override
    protected String doInBackground(String... f_url) {

        util = new FTPUtility(FTP_HOST, FTP_PORT, FTP_USER, FTP_PASS);
        try {
//            conn = (HttpURLConnection) new URL(FTP_HOST).openConnection();
            conn = (HttpURLConnection) new URL(URLS.DataComm.colDownload + IMEInum).openConnection();
//            conn = (HttpURLConnection) new URL("http://enserv.feedbackinfra.com/Webapi_Testing/api/DownloadZip/ZipDownloadCollectionFile?imieno=" + IMEInum).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        conn.setDoInput(true);
        conn.setConnectTimeout(10000); // timeout 10 secs
        try {
            conn.connect();
            input = conn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
//            util.connect();

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            long totalBytesRead = 0;
            int percentCompleted = 0;

//                long fileSize = util.getFileSize(FTP_FILE_PATH);
//                gui.setFileSize(fileSize);

            File downloadFile = new File(Environment.getExternalStorageDirectory()
                                                    .getAbsolutePath() + LOCAL_PATH);
            if (!downloadFile.exists()) {
                downloadFile.mkdir();
            }
            String fileName = FTP_FILE_PATH;//new File(downloadFile.getAbsolutePath() + FTP_FILE_PATH).getName();


            FileOutputStream outputStream = new FileOutputStream(new File(downloadFile
                    .getAbsolutePath() + "/" + FTP_FILE_PATH));

//            util.downloadFile(FTP_FILE_PATH);
//            InputStream inputStream = util.getInputStream();

            while ((bytesRead = input.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
//                    percentCompleted = (int) (totalBytesRead * 100 / fileSize);
//                    setProgress(percentCompleted);
//                publishProgress(null);
            }
            mProgress = PROGRESS_COMPLETED;
            outputStream.close();
//            util.finish();
        } catch (Exception ex) {
            ex.printStackTrace();
            cancel(true);
            mProgress = PROGRESS_FAILED;
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
        pDialog.setProgress(mProgress);
//        Log.d("TAG ", "Progress Value == " + mProgress);
    }

    /**
     * After completing background task
     * Dismiss the progress dialog
     **/
    @Override
    protected void onPostExecute(String file_url) {
        // dismiss the dialog after the file was downloaded
        pDialog.dismiss();
        if(!isDownloadCanceled) {
            if (mProgress == PROGRESS_COMPLETED) {
                new UnzippingCollectionFile().execute();
                Toast.makeText(SDActivity.getsActivity(), DOWNLOAD_COMPLETED, Toast.LENGTH_SHORT)
                     .show();
            } else {
                Toast.makeText(SDActivity.getsActivity(), DOWNLOAD_FAILED, Toast.LENGTH_SHORT)
                     .show();
            }
        }else{
            File downloadFile = new File(Environment.getExternalStorageDirectory()
                                                    .getAbsolutePath() + LOCAL_PATH + "/" + FTP_FILE_PATH);
            if (downloadFile.exists()) {
                downloadFile.delete();
            }
        }
        mProgress = 0;
    }

    CountDownTimer timer;
    /**
     * Called to initialize download progresss Bar
     */
    private void notifyProgressFoDownload() {

        final int UPLOAD_TIME = 3 * 60 * 7000;//max download time of 2 min
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
                }else if(mProgress == PROGRESS_COMPLETED) {
                    publishProgress(null);
                    pDialog.dismiss();
                    timer.cancel();
                } else{
                    mProgress = TOTAL_PROGRESS - (int) timeFinished / TIME_OF_PROGRESS;
                    publishProgress(null);
                }
            }

            @Override
            public void onFinish() {
                publishProgress(null);
                pDialog.dismiss();
                onPostExecute(null);
            }
        };

        timer.start();
    }
    @Override
    public void onCustomDialogClickView(boolean isPositiveButtonClick) {
        if(!isPositiveButtonClick){
            if(util != null){
                try {
                    pDialog.dismiss();
//                    util.disconnect();
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
     * @param iListener : instance of {@link OnCustomDialogClick}
     */
    public CustomDialogFile showDialog(OnCustomDialogClick iListener) {
        CustomDialogFile dialog = new CustomDialogFile();
        dialog.setCancelable(false);
        dialog.setmClickListener(iListener);

        try{
            dialog.show(SDActivity.getsActivity().getFragmentManager(), CustomDialog.TAG);
        }catch(Exception x){
            x.printStackTrace();
        }
        return dialog;
    }


}