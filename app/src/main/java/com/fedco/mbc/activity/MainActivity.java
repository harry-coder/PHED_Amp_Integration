package com.fedco.mbc.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.model.Structbilling;
import com.fedco.mbc.model.Structcolmas;
import com.fedco.mbc.model.Structmeterupload;
import com.fedco.mbc.service.CaptureService;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.GPSTracker;
import com.fedco.mbc.utils.URLS;
import com.fedco.mbc.utils.UtilAppCommon;
import com.splunk.mint.Mint;

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
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends RootActivity  {

    Button btnExit, btnLogin;
    EditText etdName, etdPass;
    private Boolean exit = false;
    final Context context = this;
    private static final int REQUEST_CHECK_SETTINGS = 1;
    public static String MR_id;

    SessionManager session;
    SweetAlertDialog sDialog,bDialog;
    com.fedco.mbc.logging.Logger Log;
    GPSTracker gps;
    Double latitude;
    Double longitude;
    TelephonyManager telephonyManager;
    String codeIMEI;
    public String uId, uPass, lastSessionID;
    int sessionList = 0;
    LocationManager locationManager;
    String currentVersion;
    int appversionCode;

    DB databaseHelper;
    SQLiteDatabase SD;
    Activity mcontext;
    final static int REQUEST_LOCATION = 199;


    Activity mContext;
    StartLocationAlert startLocationAlert;
    UtilAppCommon ucom;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the application environment
        // TODO: Update with your API key

        Mint.initAndStartSession(MainActivity.this, "7c741295");
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        gps = new GPSTracker(MainActivity.this);


        session = new SessionManager(getApplicationContext());
        databaseHelper = new DB(context);
        SD = databaseHelper.getReadableDatabase();


        etdName = (EditText) findViewById(R.id.EditTextEmpID);
        etdPass = (EditText) findViewById(R.id.EditTextPassword);
        btnExit = (Button) findViewById(R.id.ButtonExit);
        btnLogin = (Button) findViewById(R.id.ButtonLogin);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        codeIMEI = telephonyManager.getDeviceId();
        Structbilling.Reasons = "0";

        //code change by manas  20.06.2018

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String version = pInfo.versionName;
        Structbilling.VER_CODE = version;
        Structcolmas.VER_CODE = version;
        Structmeterupload.VER_CODE = version;
        appversionCode = pInfo.versionCode;

        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

//        new GetVersionCode().execute();

        //------------------------------
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.exit(0);

            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uId = etdName.getText().toString().trim();


                uPass = etdPass.getText().toString().trim();

                startService(v);

                if (uId.trim().length() > 0 && uPass.trim().length() > 0) {

                    if (gps.canGetLocation()) {

                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();

                        Log.e(context, "MainAct", "Latitude" + latitude);
                        Log.e(context, "MainAct", "Longitude" + longitude);

                        if (isNetworkAvailable(getApplicationContext())) {

                            Log.e(context, "MainAct", "Online Authentication");
                            new onlineAuthenticate().execute(uId, uPass);

                        } else {

                            Log.e(context, "MainAct", "Offline Authentication");
                            new offlineAuthenticate().execute(uId, uPass);

                        }

                    } else {

                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);

                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Invalid User Credentials", Toast.LENGTH_LONG).show();
                }

            }
        });
//        sessionKeyAdd();
//        addIfSession();
    }


    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(context, "RESULT CODE :" + requestCode, " REQUEST :" + resultCode);
        if (resultCode == 0) {
            switch (requestCode) {
                case 1:
                    Log.e(context, "MainAct", "Offline Authentication");
                    Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);
                    break;
            }
        }

    }

    private class onlineAuthenticate extends AsyncTask<String, String, String> {
        SweetAlertDialog pDialog;
        JSONObject jsonSection;
        String MRNME, MRID;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();

        }

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;


            try {

                //URL url = new URL(URLS.UserAccess.userAuthenticate + codeIMEI + "&Uid=" + params[0] + "&Pass=" + params[1]);
                URL url = new URL(URLS.UserAccess.userAuthenticate + codeIMEI + "/" + params[0] + "/" + params[1]);

                Log.e(context, "MainAct", URLS.UserAccess.userAuthenticate  + codeIMEI + "/" + params[0] + "/" + params[1]);
                Log.e(context, "MainAct", "para1 " + params[0]);
                Log.e(context, "MainAct", "para1 " + params[1]);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                String id = null;
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return "123";
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
                    return "123";
                }
                forecastJsonStr = buffer.toString();

                return forecastJsonStr;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.v(getApplicationContext(), "MainAct : ", "Authenticate", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
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
            if (MainActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                return;
            }
            dismissProgressDialog();
            Log.e(context, "MainAct", "PostString " + s);

            if (s != null && !s.isEmpty()&& !s.equals(" ")) {

                JSONArray ja = null;
                try {
                    ja = new JSONArray(s);
                    for (int i = 0; i < ja.length(); i++) {

                        JSONObject jsonSection = ja.getJSONObject(i);

                        Log.e(context, "MainAct", "MR_NAME" + jsonSection.getString("MR_NAME"));
                        Log.e(context, "MainAct", "METER_READER_ID" + jsonSection.getString("METER_READER_ID"));

                        MRNME = jsonSection.getString("MR_NAME");
                        MRID = jsonSection.getString("METER_READER_ID");
                        GSBilling.getInstance().Agent = jsonSection.getString("AGENTFLAG");
                        GSBilling.getInstance().Wallet = jsonSection.getString("WALLETBALANCE");
//                        MR_id = jsonSection.getString("METER_READER_ID");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (s.toString().trim().equals("0")) {

                    Toast.makeText(MainActivity.this, "You Are Not Authenticated To Use This Application", Toast.LENGTH_SHORT).show();

                } else if (s.toString().trim().equals("123")) {

                    new offlineAuthenticate().execute(uId, uPass);

                } else {

//                    session.createLoginSession(uId, uPass, GSBilling.getInstance().getMR_LICENSE_KEY());
//
//                    session.storeUid(uId);
//                    session.storePassword(uPass);
//
//                    session.storeMRName(MRNME);
//                    session.storeMRID(MRID);

                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);
                    finish();
                }

                Log.e(getApplicationContext(), "json", s);

            } else {
                new offlineAuthenticate().execute(uId, uPass);
            }

        }
    }

    private class offlineAuthenticate extends AsyncTask<String, String, String> {
        SweetAlertDialog pDialog;
        String MRNME, MRID, getUID, getPass, getResponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();

        }

        @Override
        protected String doInBackground(String... params) {

            // getUID="select * from USER_MASTER limit 1 ";
            getUID = "SELECT EXISTS(SELECT 1  FROM USER_MASTER WHERE ID='" + params[0] + "' AND PASS='" + params[1] + "')";
            Cursor curGetUser = SD.rawQuery(getUID, null);

            if (curGetUser != null && curGetUser.moveToFirst()) {
                Log.e(context, "MainAct", "USER_ID" + curGetUser.getString(0));
                getResponse = curGetUser.getString(0);
                //  Log.e(context,"MainAct","USER_Pass"+curGetUser.getString(1));
            }


            return getResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (MainActivity.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                return;
            }
            dismissProgressDialog();

            if (getResponse.equals("1")) {

                Intent intent = new Intent(getApplicationContext(), Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);

            } else {
                Toast.makeText(MainActivity.this, "You Are Not Authenticated To Use This Application", Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onBackPressed() {
        if (exit) {
//            bDialog.setCancelable(false);
            finish(); // finish activity
        } else {

            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }

    private void showProgressDialog() {
        if (sDialog == null) {
            sDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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
    protected void onDestroy() {
        // TODO Auto-generated method stub
        dismissProgressDialog();
        Debug.stopMethodTracing();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        new GetVersionCode().execute();

    }

    // Method to start the service
    public void startService(View view) {
        startService(new Intent(getBaseContext(), CaptureService.class));
    }

    public void exit() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void sessionKeyAdd() {

        databaseHelper = new DB(context);
        SD = databaseHelper.getReadableDatabase();

        String chkSession = "SELECT COUNT(*) FROM TBL_SESSION_MASTER";
        Cursor curChkSession = SD.rawQuery(chkSession, null);
        if (curChkSession != null && curChkSession.moveToFirst()) {
            sessionList = 0;//Integer.parseInt(curChkSession.getString(0));
        }
    }

    public void addIfSession() {
        ucom = new UtilAppCommon();

        if (sessionList <= 0) {
//            continueSessionKey("100000",ucom.UniqueCode(getApplicationContext()));
            String last_session_id = "0";//String.valueOf(GSBilling.getInstance().getSessionKey());
//            continueSessionKey(last_session_id, ucom.UniqueCode(getApplicationContext()));
        } else {
            Toast.makeText(context, "Session Continued", Toast.LENGTH_SHORT).show();
        }

    }

    public void continueSessionKey(String SessionKey, String MR_ID) {
        String start_date;
        String end_date;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        start_date = dateformat.format(c.getTime());
        end_date = "0";
        DB dbHelper = new DB(getApplicationContext());
        long l = Long.parseLong(SessionKey.trim());
        dbHelper.insertSession(l, MR_ID, start_date, end_date);
    }

    private class GetVersionCode extends AsyncTask<Void, String, String> {
        int CURVERSION,STABLEVERSION;

        @Override

        protected String doInBackground(Void... voids) {

            String newVersion = null;

            ///get version code
            // Document doc = null;
//            try {
//                doc = Jsoup.connect("https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName()).get();
////                Elements data = doc.select(".file-list .mdl-menu__item");
//                Elements element = doc.getElementsContainingOwnText("version code");
//                if (element.size() > 0) {
//                    System.out.println("full text : " + element.get(0).text());
//                    Pattern pattern = Pattern.compile("(.*)\\s+\\((\\d+)\\)");
//                    Matcher matcher = pattern.matcher(element.get(0).text());
//                    if (matcher.find()) {
//                        System.out.println("version name : " + matcher.group(1));
//                        System.out.println("version code : " + matcher.group(2));
//                    }
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }




            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;


            try {
                PackageInfo pinfo = new PackageInfo();
                String Strurl=URLS.VersionCode.versioncode+ pinfo.versionCode+"";
                URL url = new URL(Strurl);
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                String id = null;
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return "123";
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
                    return "123";
                }
                forecastJsonStr = buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.v(getApplicationContext(), "MainAct : ", "Authenticate", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
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
            return forecastJsonStr;






        }


        @Override

        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            bDialog = new SweetAlertDialog(MainActivity.this,SweetAlertDialog.WARNING_TYPE);

            try {
                if(onlineVersion!=null && onlineVersion.length()==43) {
                    JSONArray jsonArray = new JSONArray(onlineVersion);
                    JSONObject jsnobject = jsonArray.getJSONObject(0);
                    CURVERSION = jsnobject.getInt("CURVERSION");
                    STABLEVERSION = jsnobject.getInt("STABLEVERSION");
                    if (appversionCode!=CURVERSION) {

                        if(appversionCode<STABLEVERSION)
                        {
                            bDialog
                                    .setTitleText("Updated app available!")
                                    .setContentText("you need to update app")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();

                                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                            try {
//                                        Toast.makeText(getApplicationContext(), "App is in BETA version cannot update", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" +  MainActivity.this.getPackageName()  + "&hl=en")));
                                            } catch (ActivityNotFoundException anfe) {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" +  MainActivity.this.getPackageName()  + "&hl=en")));
                                            }
                                        }
                                    })
                                    .show();
                            bDialog.setOnKeyListener(new Dialog.OnKeyListener() {

                                @Override
                                public boolean onKey(DialogInterface arg0, int keyCode,
                                                     KeyEvent event) {
                                    // TODO Auto-generated method stub
                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                                        finish();
                                        bDialog.dismiss();
                                    }
                                    return true;
                                }
                            });
                        }


                    }
                }else{
                    Log.e(getApplicationContext(),"please check","webservice or internet connection not working");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
