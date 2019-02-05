package com.fedco.mbc.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.fedco.mbc.activity.SDActivity;
import com.fedco.mbc.sqlite.DB;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by soubhagyarm on 16-02-2016.
 */
public class UnzippingNSFile extends AsyncTask<String, Integer, String> {

    String zipFile = Environment.getExternalStorageDirectory() + "/MBC/NSC/CED.zip";
    String unzipLocation = Environment.getExternalStorageDirectory() + "/unzippednscfolder/";
    ArrayList<ArrayList<String>> csvdata;
    int countSize;
    String _zipFile;
    String _location;

    Context context;
    List<File> files;

    DB databasehelper;
    SQLiteDatabase SD;
    private ProgressDialog mpd;
    // Progress Dialog
    private CustomDialog pDialog;

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

    public CustomDialog showDialog() {
        CustomDialog dialog = new CustomDialog();
        dialog.setCancelable(false);

        try {
            dialog.show(SDActivity.getsActivity().getFragmentManager(), CustomDialog.TAG);
        } catch (Exception x) {
            x.printStackTrace();
        }
        return dialog;
    }

    // Show Progress bar before downloading Music
    // Runs in UI before background thread is called
    @Override
    protected void onPreExecute() {
//        pDialog = showDialog();
        //            pDialogFile= showDialogFile();
        Log.d("TAG", " is cancelled " + isCancelled());
        mpd = new ProgressDialog(SDActivity.getsActivity());
        mpd.setMessage("Please Wait for Few Seconds...");
        mpd.setCancelable(false);
        mpd.setCanceledOnTouchOutside(false);
        mpd.show();
        super.onPreExecute();

    }

    // Download Music File from Internet
    // This is run in a background thread
    @Override
    protected String doInBackground(String... f_url) {
        try {
//            ??UNZIPPING THE DOWNLOAD FILE
            Decompress(zipFile, unzipLocation);
            FileInputStream fin = new FileInputStream(zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                Log.v("Decompress", "Unzipping " + ze.getName());

                if (ze.isDirectory()) {
                    _dirChecker(ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(unzipLocation + ze.getName());
                    files = getListFiles(new File(unzipLocation));
                    byte[] buffer = new byte[80000];
                    int len;
                    while ((len = zin.read(buffer)) != -1) {
                        fout.write(buffer, 0, len);
                    }
                    fout.close();
                    zin.closeEntry();
                }
            }
//            ??READING EACH CSV FILE
            for (int i = 0; i < files.size(); i++) {
                Log.d("Files", "FileName:" + files.get(i));
                CSVReader csvreader = new CSVReader(context,files.get(i).toString());
                try {
                    //??STORING CSV FILE TO ARRAYLIST
                    csvdata = csvreader.ReadCSV();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                countSize = 0;
                databasehelper = new DB(SDActivity.getsActivity());
                SD = databasehelper.getWritableDatabase();
                SD.beginTransaction();
                for (int t = 0; t < csvdata.size(); t++) {

//                    databasehelper = new DB(SDActivity.getsActivity());
                    databasehelper.insertNSCMetrTable(csvdata.get(t),SD );
                    try {
                        //            ??PROGRESS VALUE SEND TO PROGRESS UPDATE
                        //            ??countSize always increase upto last vlue of for loop
                        //            ??countlines will give total no. of lines present in CSV
                        //            ??fileSize gives no. of files in Zip
                        //            ??i+1 gives no. of CSV File
//                        publishProgress(countSize, countLines(files.get(i)), files.size(), (i + 1));// sends progress to onProgressUpdate
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    countSize++;
                }
                SD.setTransactionSuccessful();
                SD.endTransaction();
                SD.close();

            }

            zin.close();
        } catch (Exception e) {
            Log.e("Decompress", "unzip", e);
        }
        return null;
    }

//    /**
//     * Updating progress bar
//     */// This is called from background thread but runs in UI
//    protected void onProgressUpdate(Integer... values) {
//        super.onProgressUpdate(values);
//        pDialog.setProgress(values);
//    }

    // This runs in UI when background thread finishes
    @Override
    protected void onPostExecute(String file_url) {

        Toast.makeText(SDActivity.getsActivity(), "Downloaded Successfully", Toast.LENGTH_LONG).show();
//        pDialog.dismiss();
        mpd.dismiss();
    }
}