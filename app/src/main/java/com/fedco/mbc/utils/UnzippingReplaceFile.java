package com.fedco.mbc.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
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
public class UnzippingReplaceFile extends AsyncTask<String, Integer, String> {

    //        String zipFile = Environment.getExternalStorageDirectory() + "/MBC/BED.zip";
    String zipFile;
    //        String zipFile = Environment.getExternalStorageDirectory() + "/MBC/abc.zip";
    String unzipLocation = Environment.getExternalStorageDirectory() + "/unzippedfolder/";
    String testFileLocation = Environment.getExternalStorageDirectory() + "/MBC/testing.txt";
    String inputFolderPath = Environment.getExternalStorageDirectory() + "/unzipped/";
    String outZipPath = Environment.getExternalStorageDirectory() + "/unziptestnitin1.zip";

    int countSize;
    String _zipFile;
    String _location;
    String[] splitData;
    //    Decompress d;
    String line = "";

    Context context;

    List<File> files;

    DB databasehelper, dbHelper4;
    SQLiteDatabase DB, SD3;

    Button btnShowProgress;
    String zipfileName, name;

    // Progress Dialog
    private CustomDialog pDialog;
    private ProgressDialog mpd;

    UtilAppCommon uac;

    public UnzippingReplaceFile(String zipFileName) {
        this.zipfileName = zipFileName;
        this.zipFile = Environment.getExternalStorageDirectory() + "/MBC/" + zipFileName + ".zip";
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
        mpd = new ProgressDialog(SDActivity.getsActivity());
        mpd.setMessage("Please Wait for Few Seconds...");
        mpd.setCancelable(false);
        mpd.setCanceledOnTouchOutside(false);
        mpd.show();
        //            pDialogFile= showDialogFile();
        Log.d("TAG", " is cancelled " + isCancelled());
        File prevFile = new File(unzipLocation);
        DeleteRecursive(prevFile);
        super.onPreExecute();

    }

    // Download Music File from Internet
    // This is run in a background thread
    @Override
    protected String doInBackground(String... f_url) {
        int countingconsumers = 0;
        try {
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

            for (int i = 0; i < files.size(); i++) {

                countSize = 0;
                Log.d("Files", "FileName:" + files.get(i));
                FileReader file = new FileReader(files.get(i));

                String name_File = files.get(i).toString().split("unzippedfolder/")[1];

                Log.e("Files", "FileName: " + name_File);
                System.out.println("am in defalu cls");
//                --------------------------Normal
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
//                        }
//                        -----------------------------------------------Normal END
//
//                if(name_File.equalsIgnoreCase("division.csv")){
//                    BufferedReader bufferdiv = new BufferedReader(file);
//
//                    databasehelper = new DB(SDActivity.getsActivity());
//                    SD3 = databasehelper.getWritableDatabase();
//                    SD3.beginTransaction();
//
//                    while ((line = bufferdiv.readLine()) != null) {
//                        //                    System.out.println("dddddddd" + line);
//                        if (line != null) {
//
//                            splitData = line.replace("'", "").split(Pattern.quote("}"));
//                            databasehelper.insertBillDIVISIONMASTER(splitData, SD3);
//
//                        }
//                    }
//                }else if(name_File.equalsIgnoreCase("section.csv")){
//                    BufferedReader buffersec = new BufferedReader(file);
//
//                    databasehelper = new DB(SDActivity.getsActivity());
//                    SD3 = databasehelper.getWritableDatabase();
//                    SD3.beginTransaction();
//
//                    while ((line = buffersec.readLine()) != null) {
//                        //                    System.out.println("dddddddd" + line);
//                        if (line != null) {
//
//                            splitData = line.replace("'", "").split(Pattern.quote("}"));
//
//                            databasehelper.insertBillDCMASTER(splitData, SD3);
//
//                        }
//                    }
//                }else if(name_File.equalsIgnoreCase("remarks.csv")){
//                    BufferedReader bufferremark = new BufferedReader(file);
//
//                    databasehelper = new DB(SDActivity.getsActivity());
//                    SD3 = databasehelper.getWritableDatabase();
//                    SD3.beginTransaction();
//
//                    while ((line = bufferremark.readLine()) != null) {
//                        //                    System.out.println("dddddddd" + line);
//                        if (line != null) {
//
//                            splitData = line.replace("'", "").split(Pattern.quote("}"));
//
//                            databasehelper.insertBillRemarkMASTER(splitData, SD3);
//
//                        }
//                    }
//                }else{
//
//                }

                switch (name_File) {
                    case "division.csv":

                        BufferedReader bufferdiv = new BufferedReader(file);

                        databasehelper = new DB(SDActivity.getsActivity());
                        SD3 = databasehelper.getWritableDatabase();
                        SD3.beginTransaction();

                        while ((line = bufferdiv.readLine()) != null) {
                            //                    System.out.println("dddddddd" + line);
                            if (line != null) {

                                splitData = line.replace("'", "").split(Pattern.quote("}"));
                                databasehelper.insertBillDIVISIONMASTER(splitData, SD3);

                            }
                        }
                        break;
                    case "section.csv":

                        BufferedReader buffersec = new BufferedReader(file);

                        databasehelper = new DB(SDActivity.getsActivity());
                        SD3 = databasehelper.getWritableDatabase();
                        SD3.beginTransaction();

                        while ((line = buffersec.readLine()) != null) {
                            //                    System.out.println("dddddddd" + line);
                            if (line != null) {

                                splitData = line.replace("'", "").split(Pattern.quote("}"));

                                databasehelper.insertBillDCMASTER(splitData, SD3);

                            }
                        }
                        break;

                    case "metermfg.csv":

                        BufferedReader buffermtr = new BufferedReader(file);

                        databasehelper = new DB(SDActivity.getsActivity());
                        SD3 = databasehelper.getWritableDatabase();
                        SD3.beginTransaction();

                        while ((line = buffermtr.readLine()) != null) {
                            //                    System.out.println("dddddddd" + line);
                            if (line != null) {

                                splitData = line.replace("'", "").split(Pattern.quote("$"));

//                                databasehelper.insertBillTRIFFMASTER(splitData, SD3);
                                databasehelper.insertBillMFGMASTER(splitData, SD3);

                            }
                        }
                        break;
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
                    default:

                        System.out.println("am in defalu cls");
                        BufferedReader buffer = new BufferedReader(file);

                        databasehelper = new DB(SDActivity.getsActivity());
                        SD3 = databasehelper.getWritableDatabase();
                        SD3.beginTransaction();

                        while ((line = buffer.readLine()) != null) {
                            //                    System.out.println("dddddddd" + line);
                            if (line != null) {

                                int occurance = countOccurrences(line, '$');

                                countingconsumers++;
                                //      System.out.println("am in if cls");
                                splitData = line.replace("'", "").split(Pattern.quote("$"));
                                String[] processedRecord;

//                                if (splitData[1].equalsIgnoreCase("0")) {
//                                    splitData[1] = splitData[14];
//
//                                }
//                                databasehelper.insertIntoTable(splitData, SD3, occurance);
                                System.out.print("print cons query :" + splitData);
                                databasehelper.insertIntoTable(splitData, SD3, occurance);

                            }


                        }
                        break;
                }
                SD3.setTransactionSuccessful();
                SD3.endTransaction();
                SD3.close();
            }

            zin.close();
        } catch (Exception e) {
            Log.e("Decompress", "unzip", e);
        }
        return String.valueOf(countingconsumers);
    }

//    /**
//     * Updating progress bar
//     */// This is called from background thread but runs in UI
//    protected void onProgressUpdate(Integer... values) {
//        super.onProgressUpdate(values);
////        pDialog.setProgress(values);
//
//    }

    // This runs in UI when background thread finishes
    @Override
    protected void onPostExecute(String file_url) {

//        pDialog.dismiss();
        mpd.dismiss();
//        String countrecord=file_url+" Consumer Records ";
//        Toast.makeText(SDActivity.getsActivity(),countrecord+ "Downloaded Successfully", Toast.LENGTH_LONG).show();
        Toast.makeText(SDActivity.getsActivity(), "Downloaded Successfully", Toast.LENGTH_LONG).show();

      /*  if (checkFile(testFileLocation)) {
            databasehelper = new DB(SDActivity.getsActivity());
            SD3 = databasehelper.getWritableDatabase();

            uac = new UtilAppCommon();
            name = uac.UniqueCode(SDActivity.getsActivity());

            String strPref = "SELECT DISTINCT LOC_CD,Cycle,Bill_Mon FROM TBL_CONSMAST";

            Cursor getPref = SD3.rawQuery(strPref, null);
            if (getPref.moveToFirst()) {
                do {
                    new DownloadAsyncFileFromURL(SDActivity.getsActivity(), "Billed", getPref.getString(0), getPref.getString(1), getPref.getString(2), name).execute();

                } while (getPref.moveToNext());//Move the cursor to the next row.
            } else {
                Toast.makeText(SDActivity.getsActivity(), "No data found", Toast.LENGTH_LONG).show();
            }
        } else {

            Toast.makeText(SDActivity.getsActivity(), "Testing Mode Activated", Toast.LENGTH_LONG).show();

        }*/


    }

    public static int countOccurrences(String haystack, char needle) {
        int count = 0;
        for (int i = 0; i < haystack.length(); i++) {
            if (haystack.charAt(i) == needle) {
                count++;
            }
        }
        return count;
    }

    public static String[] zeroConsAcc(String[] record) {

        String[] processedRecord = record;

        if (record[1].equalsIgnoreCase("0")) {
            processedRecord[1] = record[14];

        }

        return processedRecord;
    }

    public boolean checkFile(String filePath) {


        File root = new File(filePath);
        if (!root.exists()) {
            return true;
        } else {
            return false;
        }
    }
}