package com.fedco.mbc.activitymetering;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.Collection;
import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.activity.Home;
import com.fedco.mbc.activity.PrintSelection;
import com.fedco.mbc.activity.SDActivity;
import com.fedco.mbc.authentication.PrinterSessionManager;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.model.Structmeterupload;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.HttpFileUpload;
import com.fedco.mbc.utils.UtilAppCommon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by abhisheka on 19-09-2016.
 */

public class Metering extends AppCompatActivity {
    Logger Log;
    final Context context = this;
    Button btnMetering, btnUBT, btnReport, btnSummery, btnBulkUpload;
    String pass;

    SessionManager session;
    PrinterSessionManager printsession;
    Handler handler;
    UtilAppCommon appCom;
    SQLiteDatabase SD,SD2;
    DB dbHelper,dbHelper2;
//    int pendingcount = 300;
    int pendingcount = 150;

    String ZipCopyPath = Environment.getExternalStorageDirectory() + "/MBC/BulkUpload/";
    public String ZipDesPath;
    String ZipDesPathdup;
    SweetAlertDialog sDialog;
    int meterPending;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metering);

        try {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
            getSupportActionBar().setLogo(R.mipmap.logo);
            setTitle("SMARTPHED");
        } catch (NullPointerException npe) {
            Log.e(getApplicationContext(), "Billing Act", "ActionBar Throwing null Pointer", npe);
        }

        session = new SessionManager(getApplicationContext());
        printsession = new PrinterSessionManager(getApplicationContext());
        appCom = new UtilAppCommon();

        btnMetering = (Button) findViewById(R.id.buttonMetering1);
        btnUBT = (Button) findViewById(R.id.buttonMetering2);
        btnReport = (Button) findViewById(R.id.buttonMetering3);
        btnSummery = (Button) findViewById(R.id.buttonMetering4);
        btnBulkUpload = (Button) findViewById(R.id.buttonMetering5);

        appCom.nullMeterUpload();
        appCom.nullMeterMaster();

        this.ZipDesPath = Environment.getExternalStorageDirectory() + "/MBC/" + appCom.UniqueCode(getApplicationContext()) + GSBilling.getInstance().captureDatetime() + ".zip";
        this.ZipDesPathdup = Environment.getExternalStorageDirectory() + "/MBC/" + appCom.UniqueCode(getApplicationContext()) + GSBilling.getInstance().captureDatetime() ;

        HashMap<String, String> user = session.getUserDetails();

        // name
        String name = user.get(SessionManager.KEY_NAME);
        // email
        String email = user.get(SessionManager.KEY_EMAIL);
        String Key = user.get(SessionManager.KEY_LICENCE);

        GSBilling.getInstance().setKEYNAME(name);

        session.checkLogin();

        btnMetering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper = new DB(getApplicationContext());
                SD = dbHelper.getWritableDatabase();

                Structmeterupload.IMAGE1 = null;
                Structmeterupload.IMAGE2 = null;

                String pending = "SELECT CONSUMERNO from 'TBL_METERUPLOAD' WHERE UPLOADFLAG='N'";
                final Cursor curPend = SD.rawQuery(pending, null);
                curPend.getCount();

                // if(curPend.getCount()>0){
                if (curPend.getCount() > pendingcount) {
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Records are Pending ?")
                            .setContentText("More Than " + curPend.getCount() + " records are pending for upload!")
                            .setCancelText("No,cancel plz!")
                            .setConfirmText("OK!")
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {

                                    Intent intent = new Intent(context, SDActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.anim_slide_in_left,
                                            R.anim.anim_slide_out_left);
                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.cancel();
                                }
                            })
                            .show();

                } else {

                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(getApplicationContext(), Meteringtypes.class));
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);
                }


            }
        });

        btnUBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /****DIALOG BOX INITIALIZATION PROVIDING A LAYOUT TO DIALOUGE****/
                final Dialog dialogAccount = new Dialog(context, R.style.DialogeAppTheme);

                dialogAccount.setContentView(R.layout.custom_dialoge_password);
                dialogAccount.setTitle("Account Search");

                final EditText editTextAccno = (EditText) dialogAccount.findViewById(R.id.EditTextACC);

                Button dHomeButton = (Button) dialogAccount.findViewById(R.id.HomeButton);
                dHomeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        finish();
                        overridePendingTransition(R.anim.anim_slide_in_right,
                                R.anim.anim_slide_out_right);

                    }
                });

                Button dialogButton = (Button) dialogAccount.findViewById(R.id.dialogButtonACCOK);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            pass = editTextAccno.getText().toString().trim();
                        } catch (NullPointerException N) {
                            N.printStackTrace();
                        }

                        if (pass.isEmpty()) {
                            Toast.makeText(context, "Please Enter Account Number.", Toast.LENGTH_LONG).show();
                        } else if (pass.equals("data@123")) {

                            Intent intent = new Intent(getApplicationContext(), SDActivity.class);
                            intent.putExtra("flowkey", "metering");
                            //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);

                        } else {
                            dialogAccount.dismiss();
                            Toast.makeText(context, " UnAuthorized Access ", Toast.LENGTH_LONG).show();
                        }

                    }
                });
                dialogAccount.show();
            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), SummeryFilter.class);
                intent.putExtra("Filter","Metering");
                //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);

            }
        });

        btnSummery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), SummeryFilter.class);
                intent.putExtra("Filter","Summery");
                //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);

            }
        });

        btnBulkUpload .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isMobileDataEnabled()){

                    checkPendingRecord();
                    if( meterPending > 0 ){
                        new  BulkTextFileCreate().execute();
                    }
                    else {
                        Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
                    }


                }else{

                    Toast.makeText(Metering.this, "Internally stored due to no connectivity", Toast.LENGTH_SHORT).show();


                }

            }
        });
    }

    public void checkPendingRecord(){

        dbHelper2 = new DB(getApplicationContext());
        SD2 = dbHelper2.getWritableDatabase();

//        String billpending = "SELECT Cons_Number from 'TBL_BILLING''";
//        String colpending  = "SELECT  CON_NO from 'TBL_COLMASTER_MP'";
        String metpending  = "SELECT  CONSUMERNO from 'TBL_METERUPLOAD'";

//        final Cursor curBillPend = SD2.rawQuery(billpending, null);
//        final Cursor curColPend  = SD2.rawQuery(colpending, null);
        final Cursor curMetPend  = SD2.rawQuery(metpending, null);

//        billingPending      = curBillPend.getCount();
//        collectionPending   = curColPend.getCount();
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

    public void onBackPressed() {

        Intent intent = new Intent(Metering.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.metering, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), "Configuration is Under Process", Toast.LENGTH_LONG).show();
                break;
            case R.id.previous:

                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are You Sure to Logout ?")
                        .setContentText("Cannot use this application Until Login")
                        .setCancelText("No,cancel plz!")
                        .setConfirmText("OK!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                LogoutUser();
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .show();

                break;
            case R.id.next:
                final Dialog dialogAccLogout = new Dialog(context, R.style.DialogeAppTheme);
                dialogAccLogout.setContentView(R.layout.custom_dialoge_password);

                // set the custom dialog components - text, image and button
                final EditText editTextAccLogout = (EditText) dialogAccLogout.findViewById(R.id.EditTextACC);
                Button dialogLogoutButton = (Button) dialogAccLogout.findViewById(R.id.dialogButtonACCOK);
                // if button is clicked, close the custom dialog
                /****DIALOUGE BOX BUTTON INTIALISATION AND EVENT ADDING****/
                dialogLogoutButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            pass = editTextAccLogout.getText().toString().trim();
                        } catch (NullPointerException N) {
                            N.printStackTrace();
                        }

                        if (pass.isEmpty()) {
                            Toast.makeText(context, "Please Enter Account Number.", Toast.LENGTH_LONG).show();
                        } else if (pass.equals("soubhagya")) {

                            printsession.logoutUser();
                            Intent intent = new Intent(context, PrintSelection.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);

                        } else {
                            dialogAccLogout.dismiss();
                            Toast.makeText(context, " UnAuthorized Access ", Toast.LENGTH_LONG).show();
                        }

                    }
                });
                dialogAccLogout.show();

                break;
            case android.R.id.home:

                finish();
                overridePendingTransition(R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right);

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /*--------------------- Billing Bulk Upload Text File Creation ----------------------------------*/
    private class BulkTextFileCreate extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            dbHelper2 = new DB(getApplicationContext());
            SD2 = dbHelper2.getWritableDatabase();

            String selquer = "SELECT * FROM TBL_METERUPLOAD";//WHERE Upload_Flag='N'

            Cursor curMetselect = SD2.rawQuery(selquer, null);
            String arrStr[] = null;
            ArrayList<String> mylist = new ArrayList<String>();

            if (curMetselect != null && curMetselect.moveToFirst()) {

                while (curMetselect.isAfterLast() == false) {

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

                    curMetselect.moveToNext();

                }
                generateNoteOnSD(getApplicationContext(), "metering.csv", mylist);
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            sDialog.dismiss();
            new BulkUpload().execute();


        }

        @Override
        protected void onPreExecute() {
            sDialog = new SweetAlertDialog(Metering.this, SweetAlertDialog.PROGRESS_TYPE);
            sDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            sDialog.setTitleText("Loading");
            sDialog.setCancelable(false);
            sDialog.show();

        }


    }

    /*--------------------- Billing Bulk Upload to Server ----------------------------------*/
    private class BulkUpload extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {


                FileInputStream fstrm = new FileInputStream( GSBilling.getInstance().getFinalZipName() + ".zip");
                HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi_Testing/api/UploadFile/UploadMobileReadingFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                Log.e(getApplicationContext(), "SLPrintAct", "going out " + GSBilling.getInstance().getFinalZipName() + ".zip");

                int status = hfu.Send_Now(fstrm);
                if (status != 200) {
//                    succsess = "1";
                    Metering.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            sDialog.dismiss();
                            Toast.makeText(Metering.this, "Unable to upload try again ", Toast.LENGTH_LONG).show();

                        }
                    });

                } else {

                    Metering.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            sDialog.dismiss();
                            Toast.makeText(Metering.this, "uploaded successfully", Toast.LENGTH_LONG).show();

                        }
                    });

                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            sDialog.dismiss();

            File prevFile = new File(ZipCopyPath);
            DeleteRecursive(prevFile);


        }

        @Override
        protected void onPreExecute() {
            sDialog = new SweetAlertDialog(Metering.this, SweetAlertDialog.PROGRESS_TYPE);
            sDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            sDialog.setTitleText("Loading");
            sDialog.setCancelable(false);
            sDialog.show();

            zipFolder(ZipCopyPath, ZipDesPath);
            GSBilling.getInstance().setFinalZipName(ZipDesPathdup);
        }


    }


    public void generateNoteOnSD(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "MBC/BulkUpload/");
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
//            GSBilling.getInstance().setFinalZipName();
            ZipOutputStream zos = new ZipOutputStream(fos);
            File srcFile = new File(inputFolderPath);
            File[] files = srcFile.listFiles();

            for (int i = 0; i < files.length; i++) {
                android.util.Log.d("", "Adding file: " + files[i].getName());
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
            System.out.println("helloooo" + srcFile.delete());
            zos.close();
        } catch (IOException ioe) {
            android.util.Log.e("", ioe.getMessage());
        }
    }


    public void LogoutUser() {

        SD = dbHelper.getWritableDatabase();
        String delStr = "DELETE FROM USER_MASTER";

        SD.execSQL(delStr);
        session.logoutUser();

        this.finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);
    }




}
