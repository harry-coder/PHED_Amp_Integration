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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by soubhagyarm on 16-02-2016.
 */
public class UnzippingTestSurveyFile extends AsyncTask<String, Integer, String> {

    String zipFile = Environment.getExternalStorageDirectory() + "/MBC/CED.zip";
    String unzipLocation = Environment.getExternalStorageDirectory() + "/unzipsurveyfolder/";
    ArrayList<ArrayList<String>> csvdata;
    int countSize;
    String _zipFile;
    String _location;

    Context context;
    List<File> files;String line = "";
    String[] splitData;

    DB databasehelper;
    SQLiteDatabase SD,SD2;
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
//            Decompress(zipFile, unzipLocation);
//            FileInputStream fin = new FileInputStream(zipFile);
//            ZipInputStream zin = new ZipInputStream(fin);
//            ZipEntry ze = null;
//            while ((ze = zin.getNextEntry()) != null) {
//                Log.v("Decompress", "Unzipping " + ze.getName());
//
//                if (ze.isDirectory()) {
//                    _dirChecker(ze.getName());
//                } else {
//                    FileOutputStream fout = new FileOutputStream(unzipLocation + ze.getName());
//                    files = getListFiles(new File(unzipLocation));
//                    byte[] buffer = new byte[80000];
//                    int len;
//                    while ((len = zin.read(buffer)) != -1) {
//                        fout.write(buffer, 0, len);
//                    }
//                    fout.close();
//                    zin.closeEntry();
//                }
//            }
//            ??READING EACH CSV FILE
            files = getListFiles(new File(unzipLocation));

            for (int i = 0; i < files.size(); i++) {
                Log.e("Files", "FileName: " + files.get(i));
                String name_File=files.get(i).toString().split("unzipsurveyfolder/")[1];

//                String name_File = files.get(i).toString();

                Log.e("Files", "FileName: " + name_File);
                switch (name_File) {

                    case "mpczz.csv":
//
//                        CSVReaderMP csvreaderFeedermp = new CSVReaderMP(context, files.get(i).toString());
//                        try {
//                            //??STORING CSV FILE TO ARRAYLIST
//                            csvdata = csvreaderFeedermp.ReadCSV();
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//
//                        countSize = 0;
//                        databasehelper = new DB(SDActivity.getsActivity());
//                        SD = databasehelper.getWritableDatabase();
//                        SD.beginTransaction();
//                        for (int t = 0; t < csvdata.size(); t++) {
//
//                            databasehelper = new DB(SDActivity.getsActivity());
//                            databasehelper.insertCOMASTER(csvdata.get(t), SD);
//                            databasehelper.insertIntoTable(csvdata.get(t), SD,9);
////                            Log.e("CSVDATA", "DATA: " + csvdata.get(t));
//                            countSize++;
//                        }
//                        SD.setTransactionSuccessful();
//                        SD.endTransaction();
//                        SD.close();
//                        name_File="";
                        countSize = 0;
                        Log.d("Files", "FileName:" + files.get(i));
                        FileReader file = new FileReader(files.get(i));

                        BufferedReader buffer = new BufferedReader(file);

                        databasehelper = new DB(SDActivity.getsActivity());
                        SD = databasehelper.getWritableDatabase();
                        SD.beginTransaction();

                        while ((line = buffer.readLine()) != null) {
                            //                    System.out.println("dddddddd" + line);
                            if (line != null) {

                                int occurance = countOccurrences(line, '$');

                                countSize++;
                                //      System.out.println("am in if cls");
                                splitData = line.replace("'", "").split(Pattern.quote("$"));
                                databasehelper.insertIntoTable(splitData, SD, occurance);

                            }
                        }
                        break;
//                switch (name_File) {
//                    case "division.csv":
//
//                        FileReader file2 = new FileReader(files.get(i));
//
//                        BufferedReader buffer2 = new BufferedReader(file2);
//
//                        databasehelper = new DB(SDActivity.getsActivity());
//                        SD2 = databasehelper.getWritableDatabase();
//                        SD2.beginTransaction();
//
//                        while ((line = buffer2.readLine()) != null) {
//                            //                    System.out.println("dddddddd" + line);
//                            if (line != null) {
//
//                                splitData = line.replace("'", "").split(Pattern.quote("}"));
//                                databasehelper.insertBillDIVISIONMASTER(splitData, SD2);
//
//                            }
//                        }
//                        break;

                    case "section.csv":
                        countSize = 0;
                        Log.d("Files", "FileName:" + files.get(i));
                        FileReader filesec = new FileReader(files.get(i));

                        BufferedReader buffersec = new BufferedReader(filesec);

                        databasehelper = new DB(SDActivity.getsActivity());
                        SD2 = databasehelper.getWritableDatabase();
                        SD2.beginTransaction();

                        while ((line = buffersec.readLine()) != null) {
                            //                    System.out.println("dddddddd" + line);
                            if (line != null) {

                                splitData = line.replace("'", "").split(Pattern.quote("}"));

                                databasehelper.insertBillDCMASTER(splitData, SD2);

                            }
                        }
                        break;
//                    case "remark.csv":
//
//                        BufferedReader bufferremark = new BufferedReader(file);
//
//                        databasehelper = new DB(SDActivity.getsActivity());
//                        SD3 = databasehelper.getWritableDatabase();
//                        SD3.beginTransaction();
//
//                        while ((line = bufferremark.readLine()) != null) {
//                            //                    System.out.println("dddddddd" + line);
//                            if (line != null) {
//
//                                splitData = line.replace("'", "").split(Pattern.quote("}"));
//
//                                databasehelper.insertBillRemarkMASTER(splitData, SD3);
//
//                            }
//                        }
//                        break;
//                    default:
//
//                        System.out.println("am in defalu cls");
//                        BufferedReader buffer = new BufferedReader(file);
//
//                        databasehelper = new DB(SDActivity.getsActivity());
//                        SD3 = databasehelper.getWritableDatabase();
//                        SD3.beginTransaction();
//
//                        while ((line = buffer.readLine()) != null) {
//                            //                    System.out.println("dddddddd" + line);
//                            if (line != null) {
//
//                                int occurance = countOccurrences(line, '$');
//
//                                countSize++;
//                                //      System.out.println("am in if cls");
//                                splitData = line.replace("'", "").split(Pattern.quote("$"));
//                                databasehelper.insertIntoTable(splitData, SD3, occurance);
//
//                            }
//
//                            break;
//                        }
//
//                }}



////
//                    case "11kvfeeder.csv":
//
//                            CSVReader csvreaderFeeder = new CSVReader(context, files.get(i).toString());
//                        try {
//                            //??STORING CSV FILE TO ARRAYLIST
//                            csvdata = csvreaderFeeder.ReadCSV();
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//
//                        countSize = 0;
//                        databasehelper = new DB(SDActivity.getsActivity());
//                        SD = databasehelper.getWritableDatabase();
//                        SD.beginTransaction();
//                        for (int t = 0; t < csvdata.size(); t++) {
//
//                            databasehelper = new DB(SDActivity.getsActivity());
//                            databasehelper.insert11KVFEEDERMASTER(csvdata.get(t), SD);
////                            Log.e("CSVDATA", "DATA: " + csvdata.get(t));
//                            countSize++;
//                        }
//                        SD.setTransactionSuccessful();
//                        SD.endTransaction();
//                        SD.close();
//                        name_File="";
//
//                        break;
//                    case "33kvfeeder.csv":
//
//                            CSVReader csvreaderFeederthi = new CSVReader(context, files.get(i).toString());
//                        try {
//                            //??STORING CSV FILE TO ARRAYLIST
//                            csvdata = csvreaderFeederthi.ReadCSV();
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//
//                        countSize = 0;
//                        databasehelper = new DB(SDActivity.getsActivity());
//                        SD = databasehelper.getWritableDatabase();
//                        SD.beginTransaction();
//                        for (int t = 0; t < csvdata.size(); t++) {
//
//                            databasehelper = new DB(SDActivity.getsActivity());
//                            databasehelper.insert33KVFEEDERMASTER(csvdata.get(t), SD);
////                            Log.e("CSVDATA", "DATA: " + csvdata.get(t));
//                            countSize++;
//                        }
//                        SD.setTransactionSuccessful();
//                        SD.endTransaction();
//                        SD.close();
//                        name_File="";
//
//                        break;
//                    case "consumer.csv":
//
//                        CSVReader csvreaderConsumer = new CSVReader(context, files.get(i).toString());
//                        try {
//                            //??STORING CSV FILE TO ARRAYLIST
//                            csvdata = csvreaderConsumer.ReadCSV();
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//
//                        countSize = 0;
//                        databasehelper = new DB(SDActivity.getsActivity());
//                        SD = databasehelper.getWritableDatabase();
//                        SD.beginTransaction();
//                        for (int t = 0; t < csvdata.size(); t++) {
//
//                            databasehelper = new DB(SDActivity.getsActivity());
//                            databasehelper.insertCONSUMERSURVEYMASTER(csvdata.get(t), SD);
////                            Log.e("CSVDATA", "DATA: " + csvdata.get(t));
//                            countSize++;
//                        }
//                        SD.setTransactionSuccessful();
//                        SD.endTransaction();
//                        SD.close();
//                        name_File="";
//
//                        break;
//                    case "division.csv":
//
//                        CSVReader csvreader = new CSVReader(context, files.get(i).toString());
//                        try {
//                            //??STORING CSV FILE TO ARRAYLIST
//                            csvdata = csvreader.ReadCSV();
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//
//                        countSize = 0;
//                        databasehelper = new DB(SDActivity.getsActivity());
//                        SD = databasehelper.getWritableDatabase();
//                        SD.beginTransaction();
//                        for (int t = 0; t < csvdata.size(); t++) {
//
//                            databasehelper = new DB(SDActivity.getsActivity());
//                            databasehelper.insertDIVISIONMASTER(csvdata.get(t), SD);
////                            Log.e("CSVDATA", "DATA: " + csvdata.get(t));
//                            countSize++;
//                        }
//                        SD.setTransactionSuccessful();
//                        SD.endTransaction();
//                        SD.close();
//
//                        name_File="";
//                        break;
//                    case "dtr.csv":
//
//                        CSVReader csvreaderDTR = new CSVReader(context, files.get(i).toString());
//                        try {
//                            //??STORING CSV FILE TO ARRAYLIST
//                            csvdata = csvreaderDTR.ReadCSV();
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//
//                        countSize = 0;
//                        databasehelper = new DB(SDActivity.getsActivity());
//                        SD = databasehelper.getWritableDatabase();
//                        SD.beginTransaction();
//                        for (int t = 0; t < csvdata.size(); t++) {
//
//                            databasehelper = new DB(SDActivity.getsActivity());
//                            databasehelper.insertDTRMASTER(csvdata.get(t), SD);
////                            Log.e("CSVDATA", "DATA: " + csvdata.get(t));
//                            countSize++;
//                        }
//                        SD.setTransactionSuccessful();
//                        SD.endTransaction();
//                        SD.close();
//                        name_File="";
//
//                        break;
//                    case "metermfg.csv":
//
//                        CSVReader csvreaderMetMFG = new CSVReader(context, files.get(i).toString());
//                        try {
//                            //??STORING CSV FILE TO ARRAYLIST
//                            csvdata = csvreaderMetMFG.ReadCSV();
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//
//                        countSize = 0;
//                        databasehelper = new DB(SDActivity.getsActivity());
//                        SD = databasehelper.getWritableDatabase();
//                        SD.beginTransaction();
//                        for (int t = 0; t < csvdata.size(); t++) {
//
//                            databasehelper = new DB(SDActivity.getsActivity());
//                            databasehelper.insertMETERMFGMASTER(csvdata.get(t), SD);
////                            Log.e("CSVDATA", "DATA: " + csvdata.get(t));
//                            countSize++;
//                        }
//                        SD.setTransactionSuccessful();
//                        SD.endTransaction();
//                        SD.close();
//                        name_File="";
//
//                        break;
//                    case "pole.csv":
//
//                        CSVReader csvreaderPole = new CSVReader(context, files.get(i).toString());
//                        try {
//                            //??STORING CSV FILE TO ARRAYLIST
//                            csvdata = csvreaderPole.ReadCSV();
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//
//                        countSize = 0;
//                        databasehelper = new DB(SDActivity.getsActivity());
//                        SD = databasehelper.getWritableDatabase();
//                        SD.beginTransaction();
//                        for (int t = 0; t < csvdata.size(); t++) {
//
//                            databasehelper = new DB(SDActivity.getsActivity());
//                            databasehelper.insertPOLEMASTER(csvdata.get(t), SD);
////                            Log.e("CSVDATA", "DATA: " + csvdata.get(t));
//                            countSize++;
//                        }
//                        SD.setTransactionSuccessful();
//                        SD.endTransaction();
//                        SD.close();
//                        name_File="";
//
//                        break;
//                    case "section.csv":
//
//                        CSVReader csvreaderSec = new CSVReader(context, files.get(i).toString());
//                        try {
//                            //??STORING CSV FILE TO ARRAYLIST
//                            csvdata = csvreaderSec.ReadCSV();
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//
//                        countSize = 0;
//                        databasehelper = new DB(SDActivity.getsActivity());
//                        SD = databasehelper.getWritableDatabase();
//                        SD.beginTransaction();
//                        for (int t = 0; t < csvdata.size(); t++) {
//
//                            databasehelper = new DB(SDActivity.getsActivity());
//                            databasehelper.insertSECTIONMASTER(csvdata.get(t), SD);
////                            Log.e("CSVDATA", "DATA: " + csvdata.get(t));
//                            countSize++;
//                        }
//                        SD.setTransactionSuccessful();
//                        SD.endTransaction();
//                        SD.close();
//                        name_File="";
//
//                        break;
//                    case "subdivision.csv":
//
//                        CSVReader csvreaderSDiv = new CSVReader(context, files.get(i).toString());
//                        try {
//                            //??STORING CSV FILE TO ARRAYLIST
//                            csvdata = csvreaderSDiv.ReadCSV();
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//
//                        countSize = 0;
//                        databasehelper = new DB(SDActivity.getsActivity());
//                        SD = databasehelper.getWritableDatabase();
//                        SD.beginTransaction();
//                        for (int t = 0; t < csvdata.size(); t++) {
//
//                            databasehelper = new DB(SDActivity.getsActivity());
//                            databasehelper.insertSUBDIVMASTER(csvdata.get(t), SD);
////                            Log.e("CSVDATA", "DATA: " + csvdata.get(t));
//                            countSize++;
//                        }
//                        SD.setTransactionSuccessful();
//                        SD.endTransaction();
//                        SD.close();
//                        name_File="";
//
//                        break;
//                    case "substation.csv":
//
//                        CSVReader csvreaderSStation = new CSVReader(context, files.get(i).toString());
//                        try {
//                            //??STORING CSV FILE TO ARRAYLIST
//                            csvdata = csvreaderSStation.ReadCSV();
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//
//                        countSize = 0;
//                        databasehelper = new DB(SDActivity.getsActivity());
//                        SD = databasehelper.getWritableDatabase();
//                        SD.beginTransaction();
//                        for (int t = 0; t < csvdata.size(); t++) {
//
//                            databasehelper = new DB(SDActivity.getsActivity());
//                            databasehelper.insertSUBSTATIONMASTER(csvdata.get(t), SD);
////                            Log.e("CSVDATA", "DATA: " + csvdata.get(t));
//                            countSize++;
//                        }
//                        SD.setTransactionSuccessful();
//                        SD.endTransaction();
//                        SD.close();
//                        name_File="";
//
//                        break;
                }

//                String name_File= files.get(i).toString().split(".")[0];
//                String name_File1=name_File.split("/")[]

//                if (name_File.contains("11kvfeeder")) {
//
//                    CSVReader csvreader = new CSVReader(context, files.get(i).toString());
//                    try {
//                        //??STORING CSV FILE TO ARRAYLIST
//                        csvdata = csvreader.ReadCSV();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//                    countSize = 0;
//                    databasehelper = new DB(SDActivity.getsActivity());
//                    SD = databasehelper.getWritableDatabase();
//                    SD.beginTransaction();
//                    for (int t = 0; t < csvdata.size(); t++) {
//
//                        databasehelper = new DB(SDActivity.getsActivity());
//                        databasehelper.insert11KVFEEDERMASTER(csvdata.get(t), SD);
//                        Log.e("CSVDATA", "DATA: " + csvdata.get(t));
//                        countSize++;
//                    }
//                    SD.setTransactionSuccessful();
//                    SD.endTransaction();
//                    SD.close();
//
//                } else if (name_File.contains("33kvfeeder")) {
//                    CSVReader csvreader = new CSVReader(context, files.get(i).toString());
//                    try {
//                        //??STORING CSV FILE TO ARRAYLIST
//                        csvdata = csvreader.ReadCSV();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//                    countSize = 0;
//                    databasehelper = new DB(SDActivity.getsActivity());
//                    SD = databasehelper.getWritableDatabase();
//                    SD.beginTransaction();
//                    for (int t = 0; t < csvdata.size(); t++) {
//
//                        databasehelper = new DB(SDActivity.getsActivity());
//                        databasehelper.insert33KVFEEDERMASTER(csvdata.get(t), SD);
//                        Log.e("CSVDATA", "DATA: " + csvdata.get(t));
//                        countSize++;
//                    }
//                    SD.setTransactionSuccessful();
//                    SD.endTransaction();
//                    SD.close();
//                } else if (name_File.contains("consumer")) {
//                    CSVReader csvreader = new CSVReader(context, files.get(i).toString());
//                    try {
//                        //??STORING CSV FILE TO ARRAYLIST
//                        csvdata = csvreader.ReadCSV();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//                    countSize = 0;
//                    databasehelper = new DB(SDActivity.getsActivity());
//                    SD = databasehelper.getWritableDatabase();
//                    SD.beginTransaction();
//                    for (int t = 0; t < csvdata.size(); t++) {
//
//                        databasehelper = new DB(SDActivity.getsActivity());
//                        databasehelper.insertCONSUMERSURVEYMASTER(csvdata.get(t), SD);
//                        Log.e("CSVDATA", "DATA: " + csvdata.get(t));
//                        countSize++;
//                    }
//                    SD.setTransactionSuccessful();
//                    SD.endTransaction();
//                    SD.close();
//                } else
// if (name_File.contains("division")) {
//                    CSVReader csvreader = new CSVReader(context, files.get(i).toString());
//                    try {
//                        //??STORING CSV FILE TO ARRAYLIST
//                        csvdata = csvreader.ReadCSV();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//                    countSize = 0;
//                    databasehelper = new DB(SDActivity.getsActivity());
//                    SD = databasehelper.getWritableDatabase();
//                    SD.beginTransaction();
//                    for (int t = 0; t < csvdata.size(); t++) {
//
//                        databasehelper = new DB(SDActivity.getsActivity());
//                        databasehelper.insertDIVISIONMASTER(csvdata.get(t), SD);
//                        Log.e("CSVDATA", "DATA: " + csvdata.get(t));
//                        countSize++;
//                    }
//                    SD.setTransactionSuccessful();
//                    SD.endTransaction();
//                    SD.close();
//                }
//                else if (name_File.contains("dtr")) {
//                    CSVReader csvreader = new CSVReader(context, files.get(i).toString());
//                    try {
//                        //??STORING CSV FILE TO ARRAYLIST
//                        csvdata = csvreader.ReadCSV();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//                    countSize = 0;
//                    databasehelper = new DB(SDActivity.getsActivity());
//                    SD = databasehelper.getWritableDatabase();
//                    SD.beginTransaction();
//                    for (int t = 0; t < csvdata.size(); t++) {
//
//                        databasehelper = new DB(SDActivity.getsActivity());
//                        databasehelper.insertDTRMASTER(csvdata.get(t), SD);
//                        Log.e("CSVDATA", "DATA: " + csvdata.get(t));
//                        countSize++;
//                    }
//                    SD.setTransactionSuccessful();
//                    SD.endTransaction();
//                    SD.close();
//                } else if (name_File.contains("metermfg")) {
//                    CSVReader csvreader = new CSVReader(context, files.get(i).toString());
//                    try {
//                        //??STORING CSV FILE TO ARRAYLIST
//                        csvdata = csvreader.ReadCSV();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//                    countSize = 0;
//                    databasehelper = new DB(SDActivity.getsActivity());
//                    SD = databasehelper.getWritableDatabase();
//                    SD.beginTransaction();
//                    for (int t = 0; t < csvdata.size(); t++) {
//
//                        databasehelper = new DB(SDActivity.getsActivity());
//                        databasehelper.insertMETERMFGMASTER(csvdata.get(t), SD);
//                        Log.e("CSVDATA", "DATA: " + csvdata.get(t));
//                        countSize++;
//                    }
//                    SD.setTransactionSuccessful();
//                    SD.endTransaction();
//                    SD.close();
//                } else if (name_File.contains("section")) {
//                    CSVReader csvreader = new CSVReader(context, files.get(i).toString());
//                    try {
//                        //??STORING CSV FILE TO ARRAYLIST
//                        csvdata = csvreader.ReadCSV();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//                    countSize = 0;
//                    databasehelper = new DB(SDActivity.getsActivity());
//                    SD = databasehelper.getWritableDatabase();
//                    SD.beginTransaction();
//                    for (int t = 0; t < csvdata.size(); t++) {
//
//                        databasehelper = new DB(SDActivity.getsActivity());
//                        databasehelper.insertSECTIONMASTER(csvdata.get(t), SD);
//                        Log.e("CSVDATA", "DATA: " + csvdata.get(t));
//                        countSize++;
//                    }
//                    SD.setTransactionSuccessful();
//                    SD.endTransaction();
//                    SD.close();
//                } else if (name_File.contains("subdivision")) {
//                    CSVReader csvreader = new CSVReader(context, files.get(i).toString());
//                    try {
//                        //??STORING CSV FILE TO ARRAYLIST
//                        csvdata = csvreader.ReadCSV();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//                    countSize = 0;
//                    databasehelper = new DB(SDActivity.getsActivity());
//                    SD = databasehelper.getWritableDatabase();
//                    SD.beginTransaction();
//                    for (int t = 0; t < csvdata.size(); t++) {
//
//                        databasehelper = new DB(SDActivity.getsActivity());
//                        databasehelper.insertSUBDIVMASTER(csvdata.get(t), SD);
//                        Log.e("CSVDATA", "DATA: " + csvdata.get(t));
//                        countSize++;
//                    }
//                    SD.setTransactionSuccessful();
//                    SD.endTransaction();
//                    SD.close();
//                } else if (name_File.contains("pole")) {
//                    CSVReader csvreader = new CSVReader(context, files.get(i).toString());
//                    try {
//                        //??STORING CSV FILE TO ARRAYLIST
//                        csvdata = csvreader.ReadCSV();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//                    countSize = 0;
//                    databasehelper = new DB(SDActivity.getsActivity());
//                    SD = databasehelper.getWritableDatabase();
//                    SD.beginTransaction();
//                    for (int t = 0; t < csvdata.size(); t++) {
//
//                        databasehelper = new DB(SDActivity.getsActivity());
//                        databasehelper.insertPOLEMASTER(csvdata.get(t), SD);
//                        Log.e("CSVDATA", "DATA: " + csvdata.get(t));
//                        countSize++;
//                    }
//                    SD.setTransactionSuccessful();
//                    SD.endTransaction();
//                    SD.close();
//                }
            }
            SD.setTransactionSuccessful();
            SD.endTransaction();
            SD.close();
//            zin.close();
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
//        File prevFile = new File(unzipLocation);
//        DeleteRecursive(prevFile);
        mpd.dismiss();
    }

//    void DeleteRecursive(File dir) {
//        Log.d("DeleteRecursive", "DELETEPREVIOUS TOP" + dir.getPath());
//        if (dir.isDirectory()) {
//            String[] children = dir.list();
//            for (int i = 0; i < children.length; i++) {
//                File temp = new File(dir, children[i]);
//                if (temp.isDirectory()) {
//                    Log.d("DeleteRecursive", "Recursive Call" + temp.getPath());
//                    DeleteRecursive(temp);
//                } else {
//                    Log.d("DeleteRecursive", "Delete File" + temp.getPath());
//                    boolean b = temp.delete();
//                    if (b == false) {
//                        Log.d("DeleteRecursive", "DELETE FAIL");
//                    }
//                }
//            }
//
//        }
//        dir.delete();
//    }

    public static int countOccurrences(String haystack, char needle) {
        int count = 0;
        for (int i = 0; i < haystack.length(); i++) {
            if (haystack.charAt(i) == needle) {
                count++;
            }
        }
        return count;
    }
}