package com.fedco.mbc.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.authentication.PrinterSessionManager;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.service.MyBroadcastReceiver;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.ConnectionDetector;
import com.fedco.mbc.utils.GPSTracker;
import com.fedco.mbc.utils.URLS;
import com.fedco.mbc.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.SYSTEM_ALERT_WINDOW;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_SETTINGS;

public class SplashActivity extends Activity {

    com.fedco.mbc.logging.Logger Log;
    Context context;
    Dialog dialog;

    ConnectionDetector connectionDetector;
    GPSTracker gps;
    TelephonyManager telephonyManager;
    String codeIMEI;
    SessionManager session;
    PrinterSessionManager prntsession;

    String Uid, Upass, LSTMR, LSTSESSIONID;

    String[] PERMISSIONS = {
            ACCESS_COARSE_LOCATION,
            ACCESS_FINE_LOCATION,
            ACCESS_NETWORK_STATE,
            INTERNET
    };

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    String[] PERMISSIONS2 = {CAMERA,
            ACCESS_COARSE_LOCATION,
            ACCESS_FINE_LOCATION,
            ACCESS_NETWORK_STATE,
            ACCESS_WIFI_STATE,
            WRITE_EXTERNAL_STORAGE,
            READ_EXTERNAL_STORAGE,
            READ_PHONE_STATE,
            BLUETOOTH,
            BLUETOOTH_ADMIN,
            WRITE_SETTINGS,
            INTERNET,
            SYSTEM_ALERT_WINDOW
    };
    int PERMISSION_ALL = 1;

    DB dbHelper ,databaseHelper;
    SQLiteDatabase SD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        session = new SessionManager(getApplicationContext());
        prntsession = new PrinterSessionManager(getApplicationContext());

        dbHelper = new DB(getApplicationContext());
        SD = dbHelper.getWritableDatabase();

        SharedPreferences prefsCol = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
        if (!prefsCol.getBoolean("mpcollection", false)) {
            // // run your one time code

            databaseHelper = new DB(getApplicationContext());
            SD = databaseHelper.getWritableDatabase();

            databaseHelper.alterScript("MBC_MP_COLLECTION.sql");
            databaseHelper.alterScript("MBC_MP_ALTER.sql");
            databaseHelper.close();

            SharedPreferences.Editor editor = prefsCol.edit();
            editor.putBoolean("mpcollection", true);
            editor.commit();
        }
        databaseHelper = new DB(getApplicationContext());
        SD = databaseHelper.getWritableDatabase();

        databaseHelper.alterScript("MBC_MP_ALTER1.4.2.sql");

        if (checkAndRequestPermissions()) {

            connectionDetector = new ConnectionDetector(SplashActivity.this);
//            if (connectionDetector.isConnectingToInternet()) {
            if (Utility.isConnectingToInternet()) {
                if (dbHelper.countUserData() == 0) {

                    Log.e(context, "Splash-connected", "no Licence");
                    new FetOnlineUserAuth().execute();

                } else {

                    Log.e(context, "Splash-connected", "Licence: " + session.retLicence());

                    SharedPreferences bulkuplod = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
                    if (!bulkuplod.getBoolean("bulkupload", false)) {
                        //  run your one time code

                        startBulkUpload();

                        SharedPreferences.Editor editor = bulkuplod.edit();
                        editor.putBoolean("bulkupload", true);
                        editor.commit();
                    }

                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);

                }
                //  if Internet Not Connected then
            } else {

                if (dbHelper.countUserData() == 0) {

                    Log.e(context, "Splash-not-connected", "noLice");
                    Toast.makeText(SplashActivity.this, "You Need Internet Connection", Toast.LENGTH_SHORT).show();

                } else {

                    Log.e(context, "Splash-not-connected ", "Licence: " + session.retLicence());
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);

                }

            }
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
//            Toast.makeText(getApplicationContext(), "NNNNNNNNNN", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {

        Log.v(getApplicationContext(), "SplashACT", "OnDestroy------");
        super.onDestroy();

    }

    public void onBackPressed() {

        android.util.Log.v("BackPressed", "OnDestroy------");
//        dialog.dismiss();
//        dismissProgressDialog();
        this.finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);

    }

    private boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int storageread = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int loc = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int loc2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int network = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NETWORK_STATE);
//        int wifi = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_WIFI_STATE);
        int phone = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
        int bluetooth = ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH);
//        int adminbluetooth = ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADMIN);
//        int write = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_SETTINGS);
        int internet = ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET);
//        int alert = ContextCompat.checkSelfPermission(this, android.Manifest.permission.SYSTEM_ALERT_WINDOW);


        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (network != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (storageread != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_NETWORK_STATE);
        }
//        if (wifi != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_WIFI_STATE);
//        }
        if (phone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (bluetooth != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.BLUETOOTH);
        }
//        if (adminbluetooth != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(android.Manifest.permission.BLUETOOTH_ADMIN);
//        }
//        if (write != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(android.Manifest.permission.WRITE_SETTINGS);
//        }
        if (internet != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.INTERNET);
        }
//        if (alert != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(android.Manifest.permission.SYSTEM_ALERT_WINDOW);
//        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(getApplicationContext(), "yes", "yes");
                    connectionDetector = new ConnectionDetector(SplashActivity.this);
                    if (Utility.isConnectingToInternet()) {
                        if (dbHelper.countUserData() == 0) {

                            Log.e(context, "Splash-connected", "no Licence");
                            new FetOnlineUserAuth().execute();

                        } else {

                            Log.e(context, "Splash-connected", "Licence: " + session.retLicence());

                            SharedPreferences bulkuplod = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
                            if (!bulkuplod.getBoolean("bulkupload", false)) {
                                //  run your one time code

                                startBulkUpload();

                                SharedPreferences.Editor editor = bulkuplod.edit();
                                editor.putBoolean("bulkupload", true);
                                editor.commit();
                            }

                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);

                        }
                        //  if Internet Not Connected then
                    } else {

                        if (dbHelper.countUserData() == 0) {

                            Log.e(context, "Splash-not-connected", "noLice");
                            Toast.makeText(SplashActivity.this, "You Need Internet Connection", Toast.LENGTH_SHORT).show();

                        } else {

                            Log.e(context, "Splash-not-connected ", "Licence: " + session.retLicence());
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);

                        }


                    }
                } else {
                    Log.d(getApplicationContext(), "yes", "no");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void startBulkUpload() {

        Intent intent = new Intent(getApplicationContext(), MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), 234324243, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                + (1000 * 60 * 60 * 24), 8000, pendingIntent);

    }

    /*---------------------------Store LAST MR Receipt ID------------------------*/
    public void caputeKey(String Key) {

        DB dbHelper = new DB(getApplicationContext());
        SQLiteDatabase SD = dbHelper.getWritableDatabase();

        String text = Key.trim();
        // String turnstring = text.substring(16, 21);
        String finalstr = text.replaceAll("^0+(?=\\d+$)", "");
        long l = Long.parseLong(finalstr);
        long recVal = l + 1;

        String str = "UPDATE TBL_SEQUENCE SET SEQ_VAL =" + recVal + " WHERE NAME='ReceiptNumber'";
        Log.e(context, "Sequence", "update " + str);

        SD.execSQL(str);
        SD.close();

    }

    public void caputeSessionKey(String Key) {

        DB dbHelper = new DB(getApplicationContext());
        SQLiteDatabase SD = dbHelper.getWritableDatabase();

        String text = Key.trim();
        // String turnstring = text.substring(16, 21);
        String finalstr = text.replaceAll("^0+(?=\\d+$).", "");
        long l = Long.parseLong(finalstr);
        long recVal = l ;

        String str = "UPDATE TBL_SEQUENCE SET SEQ_VAL =" + recVal + " WHERE NAME='SessionNo'";
        Log.e(context, "Sequence", "update " + str);

        SD.execSQL(str);
        SD.close();

    }

    public void continueSessionKey(String SessionKey,String MR_ID) {

        String start_date;
        String end_date;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        start_date                    = dateformat.format(c.getTime());
        end_date                    ="0";

        DB dbHelper = new DB(getApplicationContext());
        long l = Long.parseLong(SessionKey.trim());
        dbHelper.insertSession(l,MR_ID,start_date,end_date);


    }

    /*---------------------------Capture DeviceSpecified UID & PASS Online------------------------*/
    private class FetOnlineUserAuth extends AsyncTask<Void, Void, Wrapper> {

        @SuppressLint("MissingPermission")
        @Override
        protected Wrapper doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String id = null;
            String line;

            String forecastJsonStr = null;

            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            codeIMEI = telephonyManager.getDeviceId();

            System.out.println ("This is the code imei "+codeIMEI );
            GSBilling.getInstance().setIMEI(codeIMEI);

            try {
                URL url = new URL(URLS.UserAccess.licence  + codeIMEI);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                Wrapper wrap = new Wrapper();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                forecastJsonStr = buffer.toString();
                System.out.println ("This is the object "+ forecastJsonStr);

                JSONArray myListsAll = new JSONArray(forecastJsonStr);

                for (int i = 0; i < myListsAll.length(); i++) {
                    JSONObject jsonobject = (JSONObject) myListsAll.get(i);


                    wrap.uniqCode = jsonobject.optString("MR_LICENSE_KEY");
                    wrap.passCode = jsonobject.optString("MR_PASSWORD");
                    wrap.verCode = jsonobject.optDouble("CURVERSION");
                    LSTMR = jsonobject.optString("LSTMR");

                    String last_sessionID = jsonobject.optString("LSTSESSION");
                    LSTSESSIONID = last_sessionID.split("-")[1];

                    Log.v(context, "SplsAct", "LicenceKey/Uid " + wrap.uniqCode);
                    Log.v(context, "SplsAct", "Password " + wrap.passCode);

                }

                return wrap;

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {

                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Wrapper s) {
            super.onPostExecute(s);

            System.out.println ( "This is the wrapper "+s);
            if (s.uniqCode != null && !s.uniqCode.isEmpty()) {

                PackageInfo pInfo = null;
                try {
                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String version = pInfo.versionName;

                prntsession.createLoginSession("0", "0");

                // storing Licencekey to Long carried session
                session.storeLicence(s.uniqCode);

                GSBilling.getInstance().setMR_LICENSE_KEY(s.uniqCode);

                dbHelper = new DB(getApplicationContext());
                SD = dbHelper.getWritableDatabase();

                String deleteUser = " DELETE FROM USER_MASTER";
                SD.beginTransaction();
                SD.execSQL(deleteUser);
                SD.setTransactionSuccessful();
                SD.endTransaction();

                String insertUser = "INSERT INTO USER_MASTER ('ID','PASS',CUR_VER) VALUES ( '" + s.uniqCode + "','" + s.passCode + "'," + s.verCode + ")";
                SD.beginTransaction();
                SD.execSQL(insertUser);
                SD.setTransactionSuccessful();
                SD.endTransaction();

                new onlineAuthenticate().execute(s.uniqCode, s.passCode);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
                if (!prefs.getBoolean("firstTime", false)) {
                    // // run your one time code

                    caputeKey(LSTMR);
//                  caputeSessionKey(LSTSESSIONID);
                    caputeSessionKey(LSTSESSIONID);
                    continueSessionKey(LSTSESSIONID,s.uniqCode);

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("firstTime", true);
                    editor.commit();
                }

            } else {

                // Dialoge Interface Enabling
                dialog = new Dialog(SplashActivity.this, android.R.style.Theme_DeviceDefault_Light);
                dialog.setContentView(R.layout.custom_dialoge);
                dialog.setTitle("IMEI Confirmation");

                // set the custom dialog components - text, image and button
                EditText image = (EditText) dialog.findViewById(R.id.EditTextOTP);
                image.setText(codeIMEI);

                dialog.show();

                dialog.setOnKeyListener(new Dialog.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface arg0, int keyCode,
                                         KeyEvent event) {
                        // TODO Auto-generated method stub
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            finish();
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_right,
                                    R.anim.anim_slide_out_right);
                            dialog.dismiss();
                        }
                        return true;
                    }
                });


            }

        }
    }

    /*---------------------------Validate UID & PASS Online------------------------*/
    private class onlineAuthenticate extends AsyncTask<String, String, String> {
            SweetAlertDialog pDialog;
            JSONObject jsonSection;
            String MRNME, MRID;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String forecastJsonStr = null;

            try {

                //URL url = new URL(URLS.UserAccess.userAuthenticate + codeIMEI + "&Uid=" + params[0] + "&Pass=" +
                URL url = new URL(URLS.UserAccess.userAuthenticate + codeIMEI + "/" + params[0] + "/" +params[1]);

                Log.e(context, "MainAct", URLS.UserAccess.userAuthenticate  + codeIMEI + "/" + params[0] + "/" + params[1]);
                Log.e(context, "SplsAct", "para1 " + params[0]);
                Log.e(context, "SplsAct", "para1 " + params[1]);

                Uid = params[0];
                Upass = params[1];

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                String id = null;
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();

                return forecastJsonStr;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {

                Log.v(getApplicationContext(), "SplsAct : ", "Authenticate", e);
                return null;

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(getApplicationContext(), "PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            Log.e(context, "MainAct", "PostResult : " + s);

           /* JSONArray ja = null;
            try {
                ja = new JSONArray(s);
                for (int i = 0; i < ja.length(); i++) {

                    JSONObject jsonSection = ja.getJSONObject(i);

                    Log.e(context, "SplsAct", "MR_NAME" + jsonSection.getString("MR_NAME"));
                    Log.e(context, "SplsAct", "METER_READER_ID" + jsonSection.getString("METER_READER_ID"));

                    MRNME = jsonSection.getString("MR_NAME");
                    MRID = jsonSection.getString("METER_READER_ID");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

            if (s.toString().trim().equals("0")) {

                Toast.makeText(SplashActivity.this, "You Are Not Authenticated To Use This Application", Toast.LENGTH_SHORT).show();

            } else {

                Wrapper wrap;
                session.createLoginSession(Uid, Upass, GSBilling.getInstance().getMR_LICENSE_KEY());

                session.storeUid(Uid);
                session.storePassword(Upass);

                session.storeMRName(MRNME);
                session.storeMRID(MRID);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
                finish();
            }

            Log.e(getApplicationContext(), "json", s);

        }
    }

    /*---------------------------Validate VERSION Online------------------------*/
    private class onlineVersionAuthenticate extends AsyncTask<String, String, String> {
            SweetAlertDialog pDialog;
            JSONObject jsonSection;
            String MRNME, MRID;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                PackageInfo pInfo = SplashActivity.this.getPackageManager().getPackageInfo
                        (getPackageName(),
                        0);
                int version = pInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            pDialog = new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String forecastJsonStr = null;

            try {

                //URL url = new URL(URLS.UserAccess.userAuthenticate + codeIMEI + "&Uid=" + params[0] + "&Pass=" + params[1]);
                URL url = new URL(URLS.UserAccess.userAuthenticate + codeIMEI + "/" + params[0] + "/" + params[1]);

                Log.e(context, "MainAct", URLS.UserAccess.userAuthenticate  + codeIMEI + "/" + params[0] + "/" + params[1]);
                Log.e(context, "SplsAct", "para1 " + params[0]);
                Log.e(context, "SplsAct", "para1 " + params[1]);

                Uid = params[0];
                Upass = params[1];

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                String id = null;
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();

                return forecastJsonStr;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {

                Log.v(getApplicationContext(), "SplsAct : ", "Authenticate", e);
                return null;

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(getApplicationContext(), "PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            Log.e(context, "MainAct", "PostResult : " + s);

            JSONArray ja = null;
            try {
                ja = new JSONArray(s);
                for (int i = 0; i < ja.length(); i++) {

                    JSONObject jsonSection = ja.getJSONObject(i);

                    Log.e(context, "SplsAct", "MR_NAME" + jsonSection.getString("MR_NAME"));
                    Log.e(context, "SplsAct", "METER_READER_ID" + jsonSection.getString("METER_READER_ID"));

                    MRNME = jsonSection.getString("MR_NAME");
                    MRID = jsonSection.getString("METER_READER_ID");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (s.toString().trim().equals("0")) {

                Toast.makeText(SplashActivity.this, "You Are Not Authenticated To Use This Application", Toast.LENGTH_SHORT).show();

            } else {

                Wrapper wrap;
                session.createLoginSession(Uid, Upass, GSBilling.getInstance().getMR_LICENSE_KEY());

                session.storeUid(Uid);
                session.storePassword(Upass);

                session.storeMRName(MRNME);
                session.storeMRID(MRID);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
                finish();
            }

            Log.e(getApplicationContext(), "json", s);

        }
    }

    public class Wrapper {
        public String uniqCode;
        public String passCode;
        public String LSTMR;
        public Double verCode;
    }

}
