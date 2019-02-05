package com.fedco.mbc.activitysurvey;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.activity.StartLocationAlert;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.model.StructSurveyConsumerUpload;
import com.fedco.mbc.model.StructSurveyPoleUpload;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.GPSTracker;
import com.fedco.mbc.utils.HttpFileUpload;
import com.fedco.mbc.utils.UtilAppCommon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.fedco.mbc.activity.StartLocationAlert.REQUEST_CHECK_SETTINGS;

public class PoleSurvey extends Activity {

    Spinner div_spin,  feeder_spin, dt_spin, spinner_PoleType, et_PoleType, spinner_pole_number, spinner_PrePoleNumber,spinner_poleLOC;
    AutoCompleteTextView spinner_33_11kv_substation;
    HashMap<String,String> substation_sample;
    ArrayList<String> div_list, divid_list, feeder_list, dt_list, dtid_list, feederid_list,
            substationid_list, substation_list, polenumber_id_list, pole_number_list;
    ArrayAdapter<String> div_adapter, feeder_adapter, dt_adapter, substation_adapter, poleNumber_adapter, prePoleNumber_adapter;
    Cursor div_cursor, feeder_cursor, dt_cursor, substation_cursor, pole_no_cursor;
    String division, division_id, feeder, feeder_id, dt, dt_id, sDate;
    Button button_Save, button_New;
    String filterFlag;
    GPSTracker gps, gps2;
    double latitude;
    double longitude;
    double latitude1 = 0.00;
    double longitude1 = 0.00;
    long gpstime;
    double altitude;
    double accuracy;
    LocationManager locationManager;
    Context mContext;
    StartLocationAlert startLocationAlert;
    Switch cb_Comp_Pole;
    Switch cb_Cut_Pole;
    Switch cb_conducter;
    DB databaseHelper;
    String cb_pole_check, cb_pole_cut_check;
    Logger Log;
    DB dbHelper, dbHelper2, dbHelper4;
    SweetAlertDialog pDialog;
    UtilAppCommon appCom;
    SessionManager sessionManager;
    public int globalcount = 10;
    private ProgressDialog progress;

    SQLiteDatabase SD, SD2, SD3;
    String MRname;
    UtilAppCommon comApp;

    boolean check = true;

    String ZipSourcePath = Environment.getExternalStorageDirectory() + "/MBC/Images/"; //All image source path
    String ZipCopyPath = Environment.getExternalStorageDirectory() + "/MBC/MeterUpload/";
    String ZipImgLimitPathMeter = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesMeter/"; //20 count image folder path
    String ZipImgCountPath = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesMeter"; //count zip folders path
    String ZipMetCSVPath = Environment.getExternalStorageDirectory() + "/MBC/MetCSV/"; //Single data CSV path
    //    public String ZipDesPath = Environment.getExternalStorageDirectory() + "/MBC/" + GSBilling.getInstance().getKEYNAME() + GSBilling.getInstance().captureDatetime() + ".zip"; //single CSV after zip Destination path
    public String ZipDesPath;
    //    String ZipDesPathdup = "/MBC/" + GSBilling.getInstance().getKEYNAME() + GSBilling.getInstance().captureDatetime(); // duplicate of zipDespath
    String ZipDesPathdup;
    SweetAlertDialog captureDia;
    final Context context = this;

    String str_DTR_Number_Name, str_PoleNumber,str_POLE_Number, str_PrePoleNumber, str_PoleType, substation, substation_id;
    private EditText et_DTR_Number_Name ,et_LT_Number_Con;
    int poleNumber;
    TextView tv_PoleNumber,tv_conductor;
    private Button button_AddConsumer,/*button_Save,*/ /*button_Continue,*/
            button_Back;

    GridView gridView;
    private ArrayAdapter<String> con_adapter;

    static final String[] letters = new String[]{

            "Cons No", "Name", "Address", "Type", "Meter No", "Communication", "Status"};

    GridView grid;
    String newPole_Flag = "N";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pole_survey);

        dbHelper = new DB(getApplicationContext());

        button_AddConsumer = (Button) findViewById(R.id.btn_Pole_add_con);
        button_Back = (Button) findViewById(R.id.Buttonback);
        button_New = (Button) findViewById(R.id.btn_new);

        div_spin = (Spinner) findViewById(R.id.spr_division);
        spinner_33_11kv_substation = (AutoCompleteTextView) findViewById(R.id.spin_33_11kv_substation);
        spinner_33_11kv_substation.setThreshold(1);
        feeder_spin = (Spinner) findViewById(R.id.spr_feeder);
        dt_spin = (Spinner) findViewById(R.id.spr_dtr);
        tv_PoleNumber = (TextView) findViewById(R.id.tv_polenumber);
        tv_conductor = (TextView) findViewById(R.id.tv_pole_conductor_status);
        spinner_PrePoleNumber = (Spinner) findViewById(R.id.spr_pre_pole);
        spinner_PoleType = (Spinner) findViewById(R.id.spr_poletype);
        spinner_poleLOC= (Spinner) findViewById(R.id.spr_pole_location);
        cb_Comp_Pole = (Switch) findViewById(R.id.cb_com_pole);
        cb_Cut_Pole = (Switch) findViewById(R.id.cb_cut_pole);
        cb_conducter = (Switch) findViewById(R.id.cb_pole_Conductor);
        tv_conductor.setVisibility(View.GONE);
        tv_conductor.setText("");

        spinner_pole_number = (Spinner) findViewById(R.id.spin_pole_num);
        button_Save = (Button) findViewById(R.id.btn_save);
        tv_PoleNumber.setVisibility(View.GONE);

        comApp = new UtilAppCommon();
        dbHelper = new DB(getApplicationContext());
        MRname = comApp.UniqueCode(getApplicationContext());

        ZipDesPath = Environment.getExternalStorageDirectory() + "/MBC/" + MRname + GSBilling.getInstance().captureDatetime() + ".zip"; //single CSV after zip Destination path
        ZipDesPathdup = "/MBC/" + MRname + GSBilling.getInstance().captureDatetime(); // duplicate of zipDespath

        et_DTR_Number_Name = (EditText) findViewById(R.id.et_dtr_number_name);
        et_LT_Number_Con = (EditText) findViewById(R.id.et_lt_number_con);
        et_DTR_Number_Name.setVisibility(View.GONE);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        startLocationAlert = new StartLocationAlert(this);
        startLocationAlert.settingsrequest();
        sessionManager = new SessionManager(getApplicationContext());

        divid_list = new ArrayList<String>();
        div_list = new ArrayList<String>();
        feeder_list = new ArrayList<String>();
        feederid_list = new ArrayList<String>();
        dt_list = new ArrayList<String>();
        dtid_list = new ArrayList<String>();
        substation_list = new ArrayList<String>();
        substationid_list = new ArrayList<String>();
        polenumber_id_list = new ArrayList<String>();
        pole_number_list = new ArrayList<String>();
        substation_sample = new HashMap<String, String>();
        substation_sample.clear();
        dbHelper = new DB(getApplicationContext());
        SD = dbHelper.getWritableDatabase();

        cb_pole_check = "N";
        cb_pole_cut_check = "N";

        String previousSummary = "SELECT DIV_CODE,FEEDER_CODE,DT_CODE,PRE_POLE_NO FROM TBL_POLE_UPLOAD ORDER BY REPORT_DATE DESC LIMIT 1";
        Cursor todoCursor = SD.rawQuery(previousSummary, null);
        if (todoCursor != null && todoCursor.moveToFirst()) {
            do {

                division_id = todoCursor.getString(0);
                feeder_id = todoCursor.getString(1);
                dt_id = todoCursor.getString(2);

            }
            while (todoCursor.moveToNext());
        }
        cb_conducter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    tv_conductor.setVisibility(View.VISIBLE);
                    tv_conductor.setText("LT ABC");
                } else {
                    tv_conductor.setVisibility(View.VISIBLE);
                    tv_conductor.setText("OH Line");
                }
            }
        });
        new Division(PoleSurvey.this).execute();
        div_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                division = div_list.get(position).toString();
                division_id = divid_list.get(position).toString();

//                  Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_LONG).show();
//                if (position > 0) {
//                } else {
//
//                    feeder_spin.setAdapter(null);
//                    feeder_list.clear();
//
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        new Substaion_33_11KV(PoleSurvey.this).execute();
        spinner_33_11kv_substation.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                substation = substation_adapter.getItem(position);
                substation_id = substation_sample.get(substation_adapter.getItem(position));

                feeder_list.clear();
                feederid_list.clear();

                new Feeder(PoleSurvey.this).execute();
            }
        });
//        spinner_33_11kv_substation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view,
//                                       int position, long id) {
//                // TODO Auto-generated method stub
//                substation = substation_list.get(position).toString();
//                substation_id = substationid_list.get(position).toString();
//
////                  Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_LONG).show();
//                if (position > 0) {
//                    feeder_list.clear();
//                    feederid_list.clear();
//
//                    new Feeder(PoleSurvey.this).execute();
//
//                } else {
//
//                    feeder_spin.setAdapter(null);
//                    feeder_list.clear();
//                    feederid_list.clear();
//
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // TODO Auto-generated method stub
//            }
//        });

        feeder_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                feeder = feeder_list.get(position).toString();
                feeder_id = feederid_list.get(position).toString();

//                  Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_LONG).show();
                if (position > 0) {

                    dt_list.clear();
                    dtid_list.clear();

                    new DT(PoleSurvey.this).execute();

                } else {

                    dt_spin.setAdapter(null);
                    dt_list.clear();
                    dtid_list.clear();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        cb_Comp_Pole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_Comp_Pole.isChecked()) {

                    cb_pole_check = "Y";
                } else {
                    cb_pole_check = "N";
                }

            }
        });

        cb_Cut_Pole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_Comp_Pole.isChecked()) {

                    cb_pole_cut_check = "Y";
                } else {
                    cb_pole_cut_check = "N";
                }

            }
        });

        dt_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO  Auto-generated method stub
                dt = dt_list.get(position).toString();
                dt_id = dtid_list.get(position).toString();

                if (position > 0) {
                    spinner_PrePoleNumber.setAdapter(null);
                    spinner_pole_number.setAdapter(null);
                    polenumber_id_list.clear();
                    pole_number_list.clear();
                    newPole_Flag="N";
                    spinner_pole_number.setVisibility(View.VISIBLE);
                    tv_PoleNumber.setVisibility(View.GONE);

                    try {
                        new Pole_Number(PoleSurvey.this).execute();

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    newPole_Flag="N";
                    spinner_PrePoleNumber.setAdapter(null);
                    spinner_pole_number.setAdapter(null);
                    polenumber_id_list.clear();
                    pole_number_list.clear();
                }
//                  Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

//        spinner_pole_number.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view,
//                                       int position, long id) {
//                // TODO  Auto-generated method stub
//                dt = dt_list.get(position).toString();
//                dt_id = dtid_list.get(position).toString();
//
//                if (position > 0) {
//                    spinner_PrePoleNumber.setAdapter(null);
//                    spinner_pole_number.setAdapter(null);
//                    polenumber_id_list.clear();
//                    pole_number_list.clear();
//                    try {
//
//                        String total_c = "select SURVEY_DT from TBL_POLE_UPLOAD WHERE DT_CODE='" + dt_id + "'";
//                        Cursor cursor_total = SD.rawQuery(total_c, null);
//                        //Toast.makeText(getApplicationContext(), "Pole Number:" + cursor_total.getCount(), Toast.LENGTH_LONG).show();
//                        int polef = 1;
//                        poleNumber = polef + cursor_total.getCount();
//                        tv_PoleNumber.setText(" " + poleNumber);
//
//                        new Pole_Number(PoleSurvey.this).execute();
//
//                    } catch (Exception e) {
//                        Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                } else {
//                    spinner_PrePoleNumber.setAdapter(null);
//                    spinner_pole_number.setAdapter(null);
//                    polenumber_id_list.clear();
//                    pole_number_list.clear();
//                }
////                  Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // TODO Auto-generated method stub
//            }
//        });

        button_New.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_PoleNumber.setVisibility(View.VISIBLE);
                spinner_pole_number.setVisibility(View.GONE);

                newPole_Flag="Y";

                String total_c = "select distinct POLE_CODE from TBL_POLE_UPLOAD WHERE DT_CODE='" + dt_id + "'";
                Cursor cursor_total = SD.rawQuery(total_c, null);
                //Toast.makeText(getApplicationContext(), "Pole Number:" + cursor_total.getCount(), Toast.LENGTH_LONG).show();
                int polef = 1;
                poleNumber = polef + cursor_total.getCount();
                tv_PoleNumber.setText("P" + poleNumber);

                spinner_PrePoleNumber.setAdapter(null);
                pole_number_list.clear();
                new Pre_Pole_Number(PoleSurvey.this).execute();

            }
        });

        button_Save = (Button) findViewById(R.id.btn_save);
        button_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                button_Save.setVisibility(View.GONE);
                str_POLE_Number=null;
                str_PoleType = spinner_PoleType.getSelectedItem().toString().trim();
                StructSurveyPoleUpload.REPORT_DATE = String.valueOf(GSBilling.getInstance().captureDate1());

                if(newPole_Flag.equalsIgnoreCase("Y") ){
                    str_POLE_Number=tv_PoleNumber.getText().toString();
                }else{
                    str_POLE_Number=spinner_pole_number.getSelectedItem().toString();
                }
                String prePole = "";

                try {

                    if (div_spin.getSelectedItem().toString().trim().equals("Select Division Name")) {
                        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("ALERT")
                                .setContentText("Please Select Division Name")
                                .setConfirmText("OK!")
                                .showCancelButton(true)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        button_Save.setVisibility(View.VISIBLE);
                                        sDialog.cancel();
                                    }
                                }).show();
                    } else if (spinner_33_11kv_substation.getText().toString().trim().equals("Select Sub-Station Name")) {
                        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("ALERT")
                                .setContentText("Please Select Sub Station Name")
                                .setConfirmText("OK!")
                                .showCancelButton(true)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        button_Save.setVisibility(View.VISIBLE);
                                        sDialog.cancel();
                                    }
                                }).show();
                    } else if (feeder_spin.getSelectedItem().toString().trim().equals("Select feeder Name")) {
                        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("ALERT")
                                .setContentText("Please Select Feeder Name")
                                .setConfirmText("OK!")
                                .showCancelButton(true)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        button_Save.setVisibility(View.VISIBLE);
                                        sDialog.cancel();
                                    }
                                }).show();
                    } else if (dt_spin.getSelectedItem().toString().trim().equals("Select DTR Name")) {
                        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("ALERT")
                                .setContentText("Please Select DTR Name")
                                .setConfirmText("OK!")
                                .showCancelButton(true)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        button_Save.setVisibility(View.VISIBLE);
                                        sDialog.cancel();
                                    }
                                }).show();
                    } else if (str_POLE_Number.toString().trim().equalsIgnoreCase("Select Pole Number")) {
                        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("ALERT")
                                .setContentText("Enter Pole Number")
                                .setConfirmText("OK!")
                                .showCancelButton(true)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        button_Save.setVisibility(View.VISIBLE);
                                        sDialog.cancel();
                                    }
                                }).show();
                    } else if (spinner_PrePoleNumber.getSelectedItem().toString().trim().equalsIgnoreCase("Select Pre-Pole Number")) {
                        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("ALERT")
                                .setContentText("Enter PrePole Number")
                                .setConfirmText("OK!")
                                .showCancelButton(true)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        button_Save.setVisibility(View.VISIBLE);
                                        sDialog.cancel();
                                    }
                                }).show();

                    } else {
                        division = div_spin.getSelectedItem().toString().trim();
                        substation = spinner_33_11kv_substation.getText().toString().trim();
                        feeder = feeder_spin.getSelectedItem().toString().trim();
                        dt = dt_spin.getSelectedItem().toString().trim();
                        str_PoleNumber = tv_PoleNumber.getText().toString().trim();
                        str_PrePoleNumber = spinner_PrePoleNumber.getSelectedItem().toString().trim();

                        pDialog = new SweetAlertDialog(PoleSurvey.this, SweetAlertDialog.PROGRESS_TYPE);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#586ECA"));
                        pDialog.setTitleText("Capturing Location..");
                        pDialog.setCancelable(false);
                        pDialog.show();

                        if (isLocationEnabled(getApplicationContext())) {      // Location Enable Check

                            if (isMobileDataEnabled()) {                       // Mobile Data Enable Check

                                if (latitude1 == 0.00) {                       // Location 0.00 Enable Check

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            for (int i = 0; i <= 50; i++) {     // Force Location Capture

                                                GPSTracker gps3 = new GPSTracker(PoleSurvey.this);
                                                if (gps3.canGetLocation()) {

                                                    latitude1 = gps3.getLatitude();
                                                    longitude1 = gps3.getLongitude();
                                                    gpstime = gps3.getTime();
                                                    accuracy = gps3.getAccuracy();
                                                    altitude = gps3.getAltitude();

                                                    System.out.println("LAT FOR " + latitude1);
                                                    System.out.println("LONG FOR " + longitude1);

                                                    final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
                                                    Date d = new Date(gps3.getTime());
                                                    sDate = sdf.format(d);

                                                    StructSurveyPoleUpload.USER_LAT = String.valueOf(latitude1);
                                                    StructSurveyPoleUpload.USER_LONG = String.valueOf(longitude1);
                                                    StructSurveyPoleUpload.USER_ACCURACY = String.valueOf(accuracy);
                                                    StructSurveyPoleUpload.USER_ALT = String.valueOf(altitude);
                                                    StructSurveyPoleUpload.USER_GPS_DT = sDate;
                                                    StructSurveyPoleUpload.VER_CODE = GSBilling.getInstance().getVerCode();
                                                    StructSurveyPoleUpload.BAT_STR = GSBilling.getInstance().getBatStr();


                                                }

                                                if (latitude1 != 0.0) {
                                                    pDialog.dismiss();
                                                    break; // A unlabeled break is enough. You don't need a labeled break here.
                                                }

                                            }

                                   /* str_DTR_Number_Name = et_DTR_Number_Name.getText().toString().trim();
                                    str_PoleNumber=et_PoleNumber.getText().toString().trim();
                                    str_PrePoleNumber = et_PrePoleNumber.getText().toString().trim();
                                    str_PoleType = spinner_PoleType.getSelectedItem().toString().trim();*/

                                            StructSurveyPoleUpload.DIV_CODE = String.valueOf(division_id);
                                            StructSurveyPoleUpload.FEEDER_CODE = String.valueOf(feeder_id);
                                            StructSurveyPoleUpload.DTR_CODE = String.valueOf(dt_id);
                                            StructSurveyPoleUpload.POLE_CODE = str_POLE_Number;
                                            StructSurveyPoleUpload.PRE_POLE_NO = str_PrePoleNumber;
                                            StructSurveyPoleUpload.POLE_TYPE = String.valueOf(str_PoleType);
                                            StructSurveyPoleUpload.USER_ID = String.valueOf(MRname);
                                            StructSurveyPoleUpload.FLAG_UPDATE = String.valueOf('1');
                                            StructSurveyPoleUpload.FLAG_SOURCE = String.valueOf('1');
                                            StructSurveyPoleUpload.FLAG_UPLOAD = String.valueOf("N");
                                            StructSurveyPoleUpload.SURVEY_DT = String.valueOf(sDate);
                                            StructSurveyPoleUpload.COMP_POLE = String.valueOf(cb_pole_check);
                                            StructSurveyPoleUpload.CUT_POLE = String.valueOf(cb_pole_cut_check);
                                            StructSurveyPoleUpload.CONDUCTER = String.valueOf(tv_conductor);
                                            StructSurveyPoleUpload.POLELOC = String.valueOf(spinner_poleLOC.getSelectedItem());
                                            StructSurveyPoleUpload.POLE_CON_COUNT = String.valueOf(et_LT_Number_Con.getText());
                                            pDialog.dismiss();

                                            Alert(mContext);
                                        }
                                    }, 1 * 5 * 1000);

                                } else {
                                    StructSurveyPoleUpload.DIV_CODE = String.valueOf(division);
                                    StructSurveyPoleUpload.FEEDER_CODE = String.valueOf(feeder);
                                    StructSurveyPoleUpload.DTR_CODE = String.valueOf(dt);
                                    StructSurveyPoleUpload.POLE_CODE = str_POLE_Number;
                                    StructSurveyPoleUpload.PRE_POLE_NO = str_PrePoleNumber;
                                    StructSurveyPoleUpload.POLE_TYPE = String.valueOf(str_PoleType);
                                    StructSurveyPoleUpload.USER_ID = String.valueOf(MRname);
                                    StructSurveyPoleUpload.FLAG_UPDATE = String.valueOf(altitude);
                                    StructSurveyPoleUpload.FLAG_SOURCE = String.valueOf(longitude1);
                                    StructSurveyPoleUpload.FLAG_UPLOAD = String.valueOf(latitude1);
                                    StructSurveyPoleUpload.SURVEY_DT = String.valueOf(sDate);
                                    StructSurveyPoleUpload.COMP_POLE = String.valueOf(cb_pole_check);
                                    StructSurveyPoleUpload.CUT_POLE = String.valueOf(cb_pole_cut_check);
                                    StructSurveyPoleUpload.CONDUCTER = String.valueOf(tv_conductor);
                                    StructSurveyPoleUpload.POLELOC = String.valueOf(spinner_poleLOC.getSelectedItem());
                                    StructSurveyPoleUpload.POLE_CON_COUNT = String.valueOf(et_LT_Number_Con.getText());
                                    // Structmeterupload.CUMULATIVEMD  = Structmetering.CUMULATIVEMD;
                                    pDialog.dismiss();
                                    Alert(mContext);

                                }


                            } else {

                                Intent intent = new Intent();
                                intent.setComponent(new ComponentName(
                                        "com.android.settings",
                                        "com.android.settings.Settings$DataUsageSummaryActivity"));
                                startActivity(intent);

                            }

                        } else {
                            // can't get location
                            // GPS or Network is not enabled
                            // Ask user to enable GPS/network in settings
     //                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 2);
                            pDialog.dismiss();
                            startLocationAlert.settingsrequest();
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Enter All Mandatory Fields", Toast.LENGTH_SHORT).show();
                }

            }
        });


        button_AddConsumer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                button_Save.setVisibility(View.GONE);
                button_AddConsumer.setVisibility(View.GONE);

                str_PoleType = spinner_PoleType.getSelectedItem().toString().trim();
                StructSurveyPoleUpload.REPORT_DATE = String.valueOf(GSBilling.getInstance().captureDate1());
                if(newPole_Flag.equalsIgnoreCase("Y")){
                    str_POLE_Number=tv_PoleNumber.getText().toString();
                }else{
                    str_POLE_Number=spinner_pole_number.getSelectedItem().toString();
                }
                String prePole = "";

                try {

                    if (div_spin.getSelectedItem().toString().trim().equals("Select Division Name")) {
                        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("ALERT")
                                .setContentText("Please Select Division Name")
                                .setConfirmText("OK!")
                                .showCancelButton(true)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        button_Save.setVisibility(View.VISIBLE);
                                        button_AddConsumer.setVisibility(View.VISIBLE);
                                        sDialog.cancel();
                                    }
                                }).show();
                    } else if (spinner_33_11kv_substation.getText().toString().trim().equals("Select Sub-Station Name")) {
                        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("ALERT")
                                .setContentText("Please Select Sub Station Name")
                                .setConfirmText("OK!")
                                .showCancelButton(true)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        button_Save.setVisibility(View.VISIBLE);
                                        button_AddConsumer.setVisibility(View.VISIBLE);
                                        sDialog.cancel();
                                    }
                                }).show();
                    } else if (feeder_spin.getSelectedItem().toString().trim().equals("Select feeder Name")) {
                        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("ALERT")
                                .setContentText("Please Select Feeder Name")
                                .setConfirmText("OK!")
                                .showCancelButton(true)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        button_Save.setVisibility(View.VISIBLE);
                                        button_AddConsumer.setVisibility(View.VISIBLE);
                                        sDialog.cancel();
                                    }
                                }).show();
                    } else if (dt_spin.getSelectedItem().toString().trim().equals("Select DTR Name")) {
                        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("ALERT")
                                .setContentText("Please Select DTR Name")
                                .setConfirmText("OK!")
                                .showCancelButton(true)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        button_Save.setVisibility(View.VISIBLE);
                                        button_AddConsumer.setVisibility(View.VISIBLE);
                                        sDialog.cancel();
                                    }
                                }).show();
                    } else if (str_POLE_Number.toString().trim().equalsIgnoreCase("Select Pole Number")) {
                        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("ALERT")
                                .setContentText("Enter Pole Number")
                                .setConfirmText("OK!")
                                .showCancelButton(true)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        button_Save.setVisibility(View.VISIBLE);
                                        button_AddConsumer.setVisibility(View.VISIBLE);
                                        sDialog.cancel();
                                    }
                                }).show();
                    } else if (spinner_PrePoleNumber.getSelectedItem().toString().trim().equalsIgnoreCase("Select Pre-Pole Number")) {
                        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("ALERT")
                                .setContentText("Enter PrePole Number")
                                .setConfirmText("OK!")
                                .showCancelButton(true)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        button_Save.setVisibility(View.VISIBLE);
                                        button_AddConsumer.setVisibility(View.VISIBLE);
                                        sDialog.cancel();
                                    }
                                }).show();
                    } else {
                        division = div_spin.getSelectedItem().toString().trim();
                        substation = spinner_33_11kv_substation.getText().toString().trim();
                        feeder = feeder_spin.getSelectedItem().toString().trim();
                        dt = dt_spin.getSelectedItem().toString().trim();
                        str_PoleNumber = tv_PoleNumber.getText().toString().trim();
                        str_PrePoleNumber = spinner_PrePoleNumber.getSelectedItem().toString().trim();

                        StructSurveyConsumerUpload.POLE_SUBSTATION_33kV=substation;
                        StructSurveyConsumerUpload.POLE_FEEDER_11kV=feeder;
                        StructSurveyConsumerUpload.POLE_DTR=dt;
                        StructSurveyConsumerUpload.POLE_POLE=str_POLE_Number;

                        pDialog = new SweetAlertDialog(PoleSurvey.this, SweetAlertDialog.PROGRESS_TYPE);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#586ECA"));
                        pDialog.setTitleText("Capturing Location..");
                        pDialog.setCancelable(false);
                        pDialog.show();

                        if (isLocationEnabled(getApplicationContext())) {      // Location Enable Check

                            if (isMobileDataEnabled()) {                       // Mobile Data Enable Check

                                if (latitude1 == 0.00) {                       // Location 0.00 Enable Check

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            for (int i = 0; i <= 50; i++) {     // Force Location Capture

                                                GPSTracker gps3 = new GPSTracker(PoleSurvey.this);
                                                if (gps3.canGetLocation()) {

                                                    latitude1 = gps3.getLatitude();
                                                    longitude1 = gps3.getLongitude();
                                                    gpstime = gps3.getTime();
                                                    accuracy = gps3.getAccuracy();
                                                    altitude = gps3.getAltitude();

                                                    System.out.println("LAT FOR " + latitude1);
                                                    System.out.println("LONG FOR " + longitude1);

                                                    final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
                                                    Date d = new Date(gps3.getTime());
                                                    sDate = sdf.format(d);

                                                    StructSurveyPoleUpload.USER_LAT = String.valueOf(latitude1);
                                                    StructSurveyPoleUpload.USER_LONG = String.valueOf(longitude1);
                                                    StructSurveyPoleUpload.USER_ACCURACY = String.valueOf(accuracy);
                                                    StructSurveyPoleUpload.USER_ALT = String.valueOf(altitude);
                                                    StructSurveyPoleUpload.USER_GPS_DT = sDate;
                                                    StructSurveyPoleUpload.VER_CODE = GSBilling.getInstance().getVerCode();
                                                    StructSurveyPoleUpload.BAT_STR = GSBilling.getInstance().getBatStr();

                                                }

                                                if (latitude1 != 0.0) {
                                                    pDialog.dismiss();
                                                    break; // A unlabeled break is enough. You don't need a labeled break here.
                                                }

                                            }

                                            StructSurveyPoleUpload.DIV_CODE = String.valueOf(division_id);
                                            StructSurveyPoleUpload.FEEDER_CODE = String.valueOf(feeder_id);
                                            StructSurveyPoleUpload.DTR_CODE = String.valueOf(dt_id);
                                            StructSurveyPoleUpload.POLE_CODE = str_POLE_Number;
                                            StructSurveyPoleUpload.PRE_POLE_NO = str_PrePoleNumber;
                                            StructSurveyPoleUpload.POLE_TYPE = String.valueOf(str_PoleType);
                                            StructSurveyPoleUpload.USER_ID = String.valueOf(MRname);
                                            StructSurveyPoleUpload.FLAG_UPDATE = String.valueOf('1');
                                            StructSurveyPoleUpload.FLAG_SOURCE = String.valueOf('1');
                                            StructSurveyPoleUpload.FLAG_UPLOAD = String.valueOf("N");
                                            StructSurveyPoleUpload.SURVEY_DT = String.valueOf(sDate);
                                            StructSurveyPoleUpload.COMP_POLE = String.valueOf(cb_pole_check);
                                            StructSurveyPoleUpload.CUT_POLE = String.valueOf(cb_pole_cut_check);
                                            StructSurveyPoleUpload.CONDUCTER = String.valueOf(tv_conductor);
                                            StructSurveyPoleUpload.POLELOC = String.valueOf(spinner_poleLOC.getSelectedItem());
                                            StructSurveyPoleUpload.POLE_CON_COUNT = String.valueOf(et_LT_Number_Con.getText());
                                            pDialog.dismiss();
                                            getValue();
//                                            dbHelper.insertPOLEUPLOAD();    //Database Insert
//                                            dbHelper.insertPOLEUPLOADUPDATE();    //Database Insert
                                        }
                                    }, 1 * 5 * 1000);

                                } else {
                                    StructSurveyPoleUpload.DIV_CODE = String.valueOf(division);
                                    StructSurveyPoleUpload.FEEDER_CODE = String.valueOf(feeder);
                                    StructSurveyPoleUpload.DTR_CODE = String.valueOf(dt);
                                    StructSurveyPoleUpload.POLE_CODE = str_POLE_Number;
                                    StructSurveyPoleUpload.PRE_POLE_NO = str_PrePoleNumber;
                                    StructSurveyPoleUpload.POLE_TYPE = String.valueOf(str_PoleType);
                                    StructSurveyPoleUpload.USER_ID = String.valueOf(MRname);
                                    StructSurveyPoleUpload.FLAG_UPDATE = String.valueOf(altitude);
                                    StructSurveyPoleUpload.FLAG_SOURCE = String.valueOf(longitude1);
                                    StructSurveyPoleUpload.FLAG_UPLOAD = String.valueOf(latitude1);
                                    StructSurveyPoleUpload.SURVEY_DT = String.valueOf(sDate);
                                    StructSurveyPoleUpload.COMP_POLE = String.valueOf(cb_pole_check);
                                    StructSurveyPoleUpload.CUT_POLE = String.valueOf(cb_pole_cut_check);
                                    StructSurveyPoleUpload.CONDUCTER = String.valueOf(tv_conductor);
                                    StructSurveyPoleUpload.POLELOC = String.valueOf(spinner_poleLOC.getSelectedItem());
                                    StructSurveyPoleUpload.POLE_CON_COUNT = String.valueOf(et_LT_Number_Con.getText());
                                    // Structmeterupload.CUMULATIVEMD  = Structmetering.CUMULATIVEMD;
                                    pDialog.dismiss();
                                    getValue();
//                                    dbHelper.insertPOLEUPLOAD();    //Database Insert
//                                    dbHelper.insertPOLEUPLOADUPDATE();    //Database Insert

                                }


                            } else {

                                button_AddConsumer.setVisibility(View.VISIBLE);
                                Intent intent = new Intent();
                                intent.setComponent(new ComponentName(
                                        "com.android.settings",
                                        "com.android.settings.Settings$DataUsageSummaryActivity"));
                                startActivity(intent);

                            }

                        } else {
                            // can't get location
                            // GPS or Network is not enabled
                            // Ask user to enable GPS/network in settings
                            //                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 2);
                            pDialog.dismiss();
                            startLocationAlert.settingsrequest();
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Enter All Mandatory Fields", Toast.LENGTH_SHORT).show();
                }


            }
        });
        button_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(getApplicationContext(), SurveyDetails.class));
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });


       /* ArrayAdapter adapter1 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, letters);
        grid.setAdapter(adapter1);*/
    }

    private void getValue() {
        str_POLE_Number=null;
        if(spinner_pole_number.getSelectedItem().toString().equalsIgnoreCase("Select Pole Number")){
            str_POLE_Number=tv_PoleNumber.getText().toString();
        }else{
            str_POLE_Number=spinner_pole_number.getSelectedItem().toString();
        }
        StructSurveyPoleUpload.DIV_CODE = String.valueOf(division_id);
        StructSurveyPoleUpload.FEEDER_CODE = String.valueOf(feeder_id);
        StructSurveyPoleUpload.DTR_CODE = String.valueOf(dt_id);
        StructSurveyPoleUpload.POLE_CODE = String.valueOf(str_POLE_Number);
        StructSurveyPoleUpload.PRE_POLE_NO = String.valueOf(str_PrePoleNumber);
        StructSurveyPoleUpload.POLE_TYPE = String.valueOf(spinner_PoleType.getSelectedItem().toString().trim());
        StructSurveyPoleUpload.USER_ID = String.valueOf(MRname);
        StructSurveyPoleUpload.FLAG_UPDATE = String.valueOf('1');
        StructSurveyPoleUpload.FLAG_SOURCE = String.valueOf('1');
        StructSurveyPoleUpload.FLAG_UPLOAD = String.valueOf("N");
        StructSurveyPoleUpload.SURVEY_DT = String.valueOf(sDate);
        StructSurveyPoleUpload.REPORT_DATE = String.valueOf(GSBilling.getInstance().captureDate1());
        StructSurveyPoleUpload.COMP_POLE = String.valueOf(cb_pole_check);
        StructSurveyPoleUpload.CUT_POLE = String.valueOf(cb_pole_cut_check);
        StructSurveyPoleUpload.CONDUCTER = String.valueOf(tv_conductor);
        StructSurveyPoleUpload.POLELOC = String.valueOf(spinner_poleLOC.getSelectedItem());
        //Alert(mContext);

        dbHelper.insertPOLEUPLOAD();    //Database Insert
        dbHelper.insertPOLEUPLOADUPDATE();    //Database Insert

        Intent intent = new Intent();
        intent.putExtra("flow", "POLE");
        intent.setComponent(new ComponentName(getApplicationContext(), ConsumerSurvey.class));
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
    }

    public void onBackPressed() {

        finish(); // finish activity
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {

                    case Activity.RESULT_OK:
                        // startLocationUpdates();
                        button_AddConsumer.setVisibility(View.GONE);
                        button_Save.setVisibility(View.GONE);
                        break;
                    case Activity.RESULT_CANCELED:
                        startLocationAlert.settingsrequest();//keep asking if imp or do whatever
                        break;
                }

                break;


        }
    }

    public boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }


    public void Alert(final Context context) {
        //btn_Next.setVisibility(View.VISIBLE);
        dbHelper.insertPOLEUPLOAD();    //Database Insert
        dbHelper.insertPOLEUPLOADUPDATE();    //Database Insert

        appCom.null_POLE_Upload();

        if (isMobileDataEnabled()) {

            new TextFileMeterClass(PoleSurvey.this).execute();

        } else {

            new SweetAlertDialog(PoleSurvey.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Stored")
                    .setContentText(" Successfully ")
                    .setConfirmText("OK")
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {

                            sDialog.cancel();

                            Intent intent = new Intent(getApplicationContext(), SurveyDetails.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);
                        }
                    }).show();

        }

//        new SweetAlertDialog(PoleSurvey.this, SweetAlertDialog.WARNING_TYPE)
//                .setTitleText("Are you sure to Upload?")
//                .setContentText("the data will be sent to server")
//                .setCancelText("No")
//                .setConfirmText("Yes")
//                .showCancelButton(true)
//                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sDialog) {
//                        // reuse previous dialog instance, keep widget user state, reset them if you need
//                        sDialog.cancel();
//
//                        Toast.makeText(PoleSurvey.this, "Internally Stored", Toast.LENGTH_SHORT).show();
//
//                        Intent intent = new Intent(getApplicationContext(), SurveyDetails.class);
////                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        // intent.putExtra("Position", 1);
//
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.anim_slide_in_left,
//                                R.anim.anim_slide_out_left);
//                    }
//                })
//                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sDialog) {
//
//                        sDialog.cancel();
//
//                        if(isMobileDataEnabled()){
//
//                            new TextFileMeterClass(PoleSurvey.this).execute();
//
//                        }else{
//
//                            Toast.makeText(context, "Internally Stored Due to No-Connectivity", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(getApplicationContext(), SurveyDetails.class);
////                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            // intent.putExtra("Position", 1);
//
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                    R.anim.anim_slide_out_left);
//
//                        }
//
//
//                    }
//                })
//                .show();

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

    //...........Division Class...........................................//
    public class Division extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        Context _context;

        Division(Context ctx) {
            _context = ctx;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // ShowAlertagain();
            super.onPreExecute();
            pd = new ProgressDialog(PoleSurvey.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(PoleSurvey.this);
            SD = databaseHelper.getReadableDatabase();

            try {
                String divString = "select distinct DIVISION_CODE,DIV_NAME from TBL_DIVISION_MASTER";
                div_cursor = SD.rawQuery(divString, null);
                if (div_cursor != null && div_cursor.moveToFirst()) {
                    divid_list.add("Select Division Name");
                    div_list.add("Select Division Name");
                    do {

                        String division_id = div_cursor.getString(0);
                        System.out.println("division_id: : " + division_id);
                        String division_name = div_cursor.getString(1);
                        System.out.println("division_name: " + division_name);
                        divid_list.add(division_id);
                        div_list.add(division_name);

                    } while (div_cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            pd.hide();
            pd.dismiss();
            try {
               /* project_adapter=new ArrayAdapter<String>(SiteInspection_Activity2.this,android.R.layout.simple_spinner_item,project_name_list);
                project_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
                div_adapter = new ArrayAdapter<String>(
                        getApplicationContext(), R.layout.custom_spinner, R.id.textView1, div_list);
                div_spin.setAdapter(div_adapter);
                if (check) {
                    int position = divid_list.indexOf(division_id);
                    div_spin.setSelection(position);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //........... Feeder Class...........................................//
    public class Feeder extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        Context _context;

        Feeder(Context ctx) {
            _context = ctx;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // ShowAlertagain();
            super.onPreExecute();
            pd = new ProgressDialog(PoleSurvey.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(PoleSurvey.this);
            SD = databaseHelper.getReadableDatabase();

            try {
//                String feederString = "select distinct FEEDER_CODE, FEEDER_NAME from TBL_11KVFEEDER_MASTER WHERE DIVISION_CODE='" + division_id + "'";
                String feederString = "select distinct FEEDER_CODE, FEEDER_NAME from TBL_11KVFEEDER_MASTER WHERE A33KVSUBSTATION_CODE='" + substation_id + "'";

                System.out.println("select distinct FEEDER_CODE, FEEDER_NAME from TBL_11KVFEEDER_MASTER WHERE FEEDER_CODE='" + substation_id + "'");
                feeder_cursor = SD.rawQuery(feederString, null);
                if (feeder_cursor != null && feeder_cursor.moveToFirst()) {
                    feederid_list.add("Select feeder Name");
                    feeder_list.add("Select feeder Name");
                    do {

                        String feeder_id = feeder_cursor.getString(0);
                        String feeder_name = feeder_cursor.getString(1);
                        System.out.println("feeder_id:  " + feeder_id);
                        feederid_list.add(feeder_id);
                        feeder_list.add(feeder_name);

                    } while (feeder_cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            pd.hide();
            pd.dismiss();
            try {
               /* project_adapter=new ArrayAdapter<String>(SiteInspection_Activity2.this,android.R.layout.simple_spinner_item,project_name_list);
                project_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
                feeder_adapter = new ArrayAdapter<String>(
                        getApplicationContext(), R.layout.custom_spinner, R.id.textView1, feeder_list);
                feeder_spin.setAdapter(feeder_adapter);
                if (check) {
                    int position = feederid_list.indexOf(feeder_id);
                    feeder_spin.setSelection(position);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //...........Pole Class...........................................//
    public class Pole_Number extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        Context _context;

        Pole_Number(Context ctx) {
            _context = ctx;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // ShowAlertagain();
            super.onPreExecute();
            pd = new ProgressDialog(PoleSurvey.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(PoleSurvey.this);
            SD = databaseHelper.getReadableDatabase();

            try {
                String poleString = "select distinct POLE_CODE,PRE_POLE_NO from TBL_POLE_MASTER WHERE DT_CODE ='" + dt_id + "'";
                //System.out.println("select distinct POLE_CODE,POLE_TYPE from TBL_POLE_MASTER WHERE FEEDER_CODE ='" + feeder_id + "'");
                pole_no_cursor = SD.rawQuery(poleString, null);

                if (pole_no_cursor != null && pole_no_cursor.moveToFirst()) {
                    polenumber_id_list.add("Select Pole Number");
                    pole_number_list.add("Select Pre-Pole Number");

                    do {

                        String pole_no = pole_no_cursor.getString(0);
                        System.out.println("Pole _ CODE : : " + pole_no);
                        String pre_pole_no = pole_no_cursor.getString(1);
                        System.out.println("Pre_Pole_No : " + pre_pole_no);
                        polenumber_id_list.add(pole_no);
                        pole_number_list.add(pre_pole_no);

                    } while (pole_no_cursor.moveToNext());
                } else {
                    polenumber_id_list.add("Select Pole Number");
                    pole_number_list.add("Select Pre-Pole Number");
                    pole_number_list.add("P0");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            pd.hide();
            pd.dismiss();
            try {
               /* project_adapter=new ArrayAdapter<String>(SiteInspection_Activity2.this,android.R.layout.simple_spinner_item,project_name_list);
                project_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
                poleNumber_adapter = new ArrayAdapter<String>(
                        getApplicationContext(), R.layout.custom_spinner, R.id.textView1, polenumber_id_list);
                prePoleNumber_adapter = new ArrayAdapter<String>(
                        getApplicationContext(), R.layout.custom_spinner, R.id.textView1, pole_number_list);
                spinner_pole_number.setAdapter(poleNumber_adapter);
                spinner_PrePoleNumber.setAdapter(prePoleNumber_adapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //...........Pre-Pole Class...........................................//
    public class Pre_Pole_Number extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        Context _context;

        Pre_Pole_Number(Context ctx) {
            _context = ctx;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // ShowAlertagain();
            super.onPreExecute();
            pd = new ProgressDialog(PoleSurvey.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(PoleSurvey.this);
            SD = databaseHelper.getReadableDatabase();

            try {
                String poleString = "select distinct POLE_CODE from TBL_POLE_MASTER WHERE DT_CODE ='" + dt_id + "'";
                //System.out.println("select distinct POLE_CODE,POLE_TYPE from TBL_POLE_MASTER WHERE FEEDER_CODE ='" + feeder_id + "'");
                pole_no_cursor = SD.rawQuery(poleString, null);

                if (pole_no_cursor != null && pole_no_cursor.moveToFirst()) {

                    pole_number_list.add("Select Pre-Pole Number");
                    pole_number_list.add("P0");
                    do {

                        String pre_pole_no = pole_no_cursor.getString(0);
                        System.out.println("Pre_Pole_No : " + pre_pole_no);

                        pole_number_list.add(pre_pole_no);

                    } while (pole_no_cursor.moveToNext());
                } else {

                    pole_number_list.add("Select Pre-Pole Number");
                    pole_number_list.add("P0");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            pd.hide();
            pd.dismiss();
            try {
               /* project_adapter=new ArrayAdapter<String>(SiteInspection_Activity2.this,android.R.layout.simple_spinner_item,project_name_list);
                project_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

                prePoleNumber_adapter = new ArrayAdapter<String>(
                        getApplicationContext(), R.layout.custom_spinner, R.id.textView1, pole_number_list);

                spinner_PrePoleNumber.setAdapter(prePoleNumber_adapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //...........Sub-Statsion Class...........................................//
    public class Substaion_33_11KV extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        Context _context;

        Substaion_33_11KV(Context ctx) {
            _context = ctx;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // ShowAlertagain();
            super.onPreExecute();
            pd = new ProgressDialog(PoleSurvey.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(PoleSurvey.this);
            SD = databaseHelper.getReadableDatabase();

            try {
                String poleString = "select distinct SUBSTATION_CODE,SUBSTATION_NAME from TBL_SUB_STATION_MASTER";
                //System.out.println("select distinct POLE_CODE,POLE_TYPE from TBL_POLE_MASTER WHERE FEEDER_CODE ='" + feeder_id + "'");
                substation_cursor = SD.rawQuery(poleString, null);
                if (substation_cursor != null && substation_cursor.moveToFirst()) {
                    substationid_list.add("Select Sub-Station Name");
                    substation_list.add("Select Sub-Station Name");
                    do {

                        String substation_id = substation_cursor.getString(0);
                        System.out.println("meter_id: : " + substation_id);
                        String substation_name = substation_cursor.getString(1);
                        System.out.println("mete_name: " + substation_name);
                        substationid_list.add(substation_id);
                        substation_list.add(substation_name);
                        substation_sample.put(substation_name,substation_id);

                    } while (substation_cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            pd.hide();
            pd.dismiss();
            try {
               /* project_adapter=new ArrayAdapter<String>(SiteInspection_Activity2.this,android.R.layout.simple_spinner_item,project_name_list);
                project_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
                substation_adapter = new ArrayAdapter<String>(
                        getApplicationContext(), R.layout.custom_spinner, R.id.textView1, substation_list);
                spinner_33_11kv_substation.setAdapter(substation_adapter);
                if (check) {
                    int position = substationid_list.indexOf(substation_id);
                    spinner_33_11kv_substation.setSelection(position);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //........... DTR  Class...........................................//
    public class DT extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        Context _context;

        DT(Context ctx) {
            _context = ctx;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // ShowAlertagain();
            super.onPreExecute();
            pd = new ProgressDialog(PoleSurvey.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(PoleSurvey.this);
            SD = databaseHelper.getReadableDatabase();

            try {
                String secString = "select distinct DTR_CODE,DTR_NAME from TBL_DTR_MASTER WHERE FEEDER_CODE ='" + feeder_id + "' ";
                System.out.println("select distinct DTR_CODE,DTR_NAME from TBL_DTR_MASTER WHERE FEEDER_CODE ='" + feeder_id + "'");
                dt_cursor = SD.rawQuery(secString, null);
                if (dt_cursor != null && dt_cursor.moveToFirst()) {
                    dtid_list.add("Select DTR Name");
                    dt_list.add("Select DTR Name");
                    do {

                        String dtr_id = dt_cursor.getString(0);
                        String dtr = dt_cursor.getString(1);
                        System.out.println("DTR_Id : " + dtr_id);
                        System.out.println("DTR_Name : " + dtr);
                        dtid_list.add(dtr_id);
                        dt_list.add(dtr_id+" "+dtr);

                    } while (dt_cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            pd.hide();
            pd.dismiss();
            try {
               /* project_adapter=new ArrayAdapter<String>(SiteInspection_Activity2.this,android.R.layout.simple_spinner_item,project_name_list);
                project_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
                dt_adapter = new ArrayAdapter<String>(
                        getApplicationContext(), R.layout.custom_spinner, R.id.textView1, dt_list);
                dt_spin.setAdapter(dt_adapter);

//                if (check) {
//                    int position = dtid_list.indexOf(dt_id);
//                    dt_spin.setSelection(position);
//                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*--------------------- Meter Reading Uplaod Text File Creation ----------------------------------*/
    private class TextFileMeterClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        TextFileMeterClass(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {

            progress = new ProgressDialog(this.context);
            progress.setMessage("Uploading Please Wait..");
            progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                dbHelper2 = new DB(getApplicationContext());
                SD2 = dbHelper2.getWritableDatabase();

                String selquer = "SELECT * FROM TBL_POLE_UPLOAD WHERE FLAG_UPLOAD='N' ";//

                Cursor curMetselect = SD2.rawQuery(selquer, null);
                String arrStr[] = null;
                ArrayList<String> mylist = new ArrayList<String>();
                // Removefolder(ZipImgLimitPath);
                if (curMetselect != null && curMetselect.moveToFirst()) {
                    int counter = 0;
                    while (!curMetselect.isAfterLast()) {
                        counter++;
                        // String name = curBillselect.getString(0);

                        mylist.add(curMetselect.getString(0)     + "}" + curMetselect.getString(1) + "}" + curMetselect.getString(2) +
                                "}" + curMetselect.getString(3)  + "}" + curMetselect.getString(4) + "}" + curMetselect.getString(5) +
                                "}" + curMetselect.getString(6)  + "}" + curMetselect.getString(7) + "}" + curMetselect.getString(8) +
                                "}" + curMetselect.getString(9)  + "}" + curMetselect.getString(10) + "}" + curMetselect.getString(11) +
                                "}" + curMetselect.getString(12) + "}" + curMetselect.getString(13) + "}" + curMetselect.getString(14) +
                                "}" + curMetselect.getString(15) + "}" + curMetselect.getString(16) + "}" + curMetselect.getString(17)+
                                "}" + curMetselect.getString(7) + "}" + curMetselect.getString(19) + "}" + curMetselect.getString(20)+
                                "}" + curMetselect.getString(21)+"}" + curMetselect.getString(22)+"}" + curMetselect.getString(23)+
                                "}" + curMetselect.getString(24));

                        createfolder(ZipImgLimitPathMeter + "/" + MRname + GSBilling.getInstance().captureDate() + "_" + counter / globalcount);
//                        moveFile(ZipSourcePath, curMetselect.getString(60), ZipImgLimitPathMeter + "/" + MRname + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");
//                        moveFile(ZipSourcePath, curMetselect.getString(61), ZipImgLimitPathMeter + "/" + MRname + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");

                        curMetselect.moveToNext();

                    }
                    for (int i = 0; i <= counter / globalcount; i++) {

                        selectZipFolder(ZipImgLimitPathMeter + "/" + MRname + GSBilling.getInstance().captureDate() + "_" + i, ZipImgLimitPathMeter + "/" + MRname + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                    }
                    generateNoteMeterOnSD(getApplicationContext(), "pole.csv", mylist);
                }

                PoleSurvey.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progress.dismiss();
                        new PostMeterClass(PoleSurvey.this).execute();
                    }
                });


            } catch (NullPointerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute() {
            progress.dismiss();
            // new PostClass(SDActivity.this).execute();
        }

    }

    /*--------------------- Meter Reading Uplaod Upload billing File  ----------------------------------*/
    public class PostMeterClass extends AsyncTask<String, Void, Boolean> {

        private final Context context;
        public String succsess = null;

        PostMeterClass(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Please Wait..");
            progress.show();

            zipFolder(ZipMetCSVPath, ZipDesPath);
            System.out.println("++++++++" + ZipDesPathdup);
            GSBilling.getInstance().setFinalZipName(ZipDesPathdup);

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip");
                HttpFileUpload hfu = new HttpFileUpload("http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadMobileReadingFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                // HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");

                int status = hfu.Send_Now(fstrm);

                if (status != 200) {

                    PoleSurvey.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();
                            new SweetAlertDialog(PoleSurvey.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Stored")
                                    .setContentText(" Successfully ")
                                    .setConfirmText("OK")
                                    .showCancelButton(true)
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {

                                            sDialog.cancel();

                                            Intent intent = new Intent(getApplicationContext(), SurveyDetails.class);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.anim_slide_in_left,
                                                    R.anim.anim_slide_out_left);
                                        }
                                    }).show();

//                            Toast.makeText(PoleSurvey.this, "Internaly Stored Due to No Connectivity", Toast.LENGTH_LONG).show();
//                            Intent intent = new Intent(getApplicationContext(), SurveyDetails.class);
////                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            // intent.putExtra("Position", 1);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                    R.anim.anim_slide_out_left);
                        }
                    });

                } else {


                    dbHelper2 = new DB(getApplicationContext());
                    SD2 = dbHelper2.getWritableDatabase();

                    String updatequer = "UPDATE  TBL_POLE_UPLOAD  SET FLAG_UPLOAD = 'Y'";
                    Cursor curBillupdate = SD2.rawQuery(updatequer, null);
                    if (curBillupdate != null && curBillupdate.moveToFirst()) {
                        android.util.Log.v("Update ", "Success");
                    }

                    PoleSurvey.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();
                            // new UpdateUI().execute();
//                            Toast.makeText(PoleSurvey.this, "Successfully Uploaded to Server", Toast.LENGTH_LONG).show();
                            new SweetAlertDialog(PoleSurvey.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Uploaded")
                                    .setContentText(" Successfully ")
                                    .setConfirmText("OK")
                                    .showCancelButton(true)
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {

                                            sDialog.cancel();

                                            Intent intent = new Intent(getApplicationContext(), SurveyDetails.class);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.anim_slide_in_left,
                                                    R.anim.anim_slide_out_left);
                                        }
                                    }).show();

//                            Intent intent = new Intent(getApplicationContext(), SurveyDetails.class);
////                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            // intent.putExtra("Position", 1);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                    R.anim.anim_slide_out_left);

                        }
                    });
                }


            } catch (IOException e) {
                e.printStackTrace();

            }
            return true;
        }

        protected void onPostExecute() {
            progress.dismiss();
//            File file = new File(ZipImgCountPath);
//            DeleteRecursive(file);
            new File(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip").delete();

        }

    }

    public void createfolder(String outputPath) {
        //create output directory if it doesn't exist
        File dir = new File(outputPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public int CountRecursive(File dir) {
        String[] fileNames;
        fileNames = dir.list();
        int total = 0;
        for (String fileName : fileNames) {
            if (fileName.contains(".zip")) {
                total++;
            }
        }
        Log.e(getApplicationContext(), "", "1" + total);
        Log.e(getApplicationContext(), "", "2" + fileNames.length);
        return total;
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
            ZipOutputStream zos = new ZipOutputStream(fos);
            File srcFile = new File(inputFolderPath);
            File[] files = srcFile.listFiles();
            android.util.Log.d("", "Zip directory: " + srcFile.getName());
            for (File file : files) {
                android.util.Log.d("", "Adding file: " + file.getName());
                byte[] buffer = new byte[1024];
                FileInputStream fis = new FileInputStream(file);
                zos.putNextEntry(new ZipEntry(file.getName()));
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

    public static void selectZipFolder(String inputFolderPath, String outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outZipPath);
            // GSBilling.getInstance().setFinalZipName();
            ZipOutputStream zos = new ZipOutputStream(fos);
            File srcFile = new File(inputFolderPath);
            File[] files = srcFile.listFiles();
            android.util.Log.d("", "Zip directory: " + srcFile.getName());
            for (File file : files) {
                android.util.Log.d("", "Adding file: " + file.getName());
                byte[] buffer = new byte[1024];
                FileInputStream fis = new FileInputStream(file);
                zos.putNextEntry(new ZipEntry(file.getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
        } catch (IOException ioe) {
            android.util.Log.e("", ioe.getMessage());
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

        } catch (IOException e) {
            e.printStackTrace();
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

            ////  delete the original file
            // new File(inputPath + inputFile).delete();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
