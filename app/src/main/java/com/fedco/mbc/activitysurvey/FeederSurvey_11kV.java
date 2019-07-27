package com.fedco.mbc.activitysurvey;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.model.StructSurvey11KVUpload;
import com.fedco.mbc.model.StructSurveyConsumerMaster;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.GPSTracker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class FeederSurvey_11kV extends Activity {

    public static final String TAG = FeederSurvey_11kV.class.getSimpleName();

    private Spinner spinner_Division, spinner_DC, spinner_11kVFeederName, spinner_TapPoint,
            spinner_Box, spinner_MeterStatus, spinner_Metermake;
    ArrayList<String> div_list, divid_list, dc_list, dcid_list, feeder_list, feederid_list, tapPoint_List, tapPoint_Id_List, metermake_list, metermakeid_list, substationid_list, substation_list;
    ArrayAdapter<String> div_adapter, dc_adapter, feeder_adapter, tapPoint_adapter, metermake_adapter, substation_adapter;
    Cursor div_cursor, dc_Cursor, feeder_cursor, tapPointCursor, metermake_cursor, substation_cursor;
    AutoCompleteTextView spinner_33_11kv_Substation;
    CheckBox check_IRDA, check_Optical, check_LPR, check_NA;
    Switch chk_seal, chk_com;
    HashMap<String, String> substation_sample;
    final Context context = this;
    String meterNumber;
    TextView tv_box_seal_status, TV_FEEDER_NAME, tv_TapPoint;
    private EditText et_11kVFeederName, et_FeederLocation, et_MeterNumber, et_MeterReading,
            et_33_11kv_Substation, et_remark_33_11kv, et_mf_value, et_ctr_value, et_ptr_value/*,et_MeterMake*/;

    String str_Division, str_33_11kvSubstation, str_11kVFeederName, str_TapPoint, str_Remarks,
            str_FeederLocation, str_MeterNumber, str_MeterComPort, str_MeterBoxStatus, str_MeterMake, str_MeterStatus;
    String division, division_id, dc, dc_id, substation_id, substation, feeder, feeder_id, metermake, metermake_id;
    String str_IRDA, str_Optical, str_LPR;
    String newTapPointFlag = "N";

    boolean check = true;
    int tapPointNumber;

    Button button_Continue, button_Back, button_New;
    RelativeLayout back;
    RelativeLayout next;
    LinearLayout llSealstatus;
    String meterComPort;
    DB databaseHelper;
    SQLiteDatabase SD;
    private String blockCharacterSet = "}{";
    double latitude1 = 0.0;
    double longitude1 = 0.0;
    long gpstime;
    double altitude;
    double accuracy;
    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feeder_survey_11kv);
        callView();

        divid_list = new ArrayList<String>();
        div_list = new ArrayList<String>();

        dc_list = new ArrayList<String>();
        dcid_list = new ArrayList<String>();

        substation_list = new ArrayList<String>();
        substationid_list = new ArrayList<String>();

        feeder_list = new ArrayList<String>();
        feederid_list = new ArrayList<String>();

        tapPoint_List = new ArrayList<String>();
        tapPoint_Id_List = new ArrayList<String>();

        metermake_list = new ArrayList<String>();
        metermakeid_list = new ArrayList<String>();
        substation_sample = new HashMap<String, String>();
        substation_sample.clear();

        databaseHelper = new DB(getApplicationContext());
        SD = databaseHelper.getWritableDatabase();

        String previousSummary = "SELECT FEEDER_CODE,DIVISION_CODE FROM TBL_11KVFEEDER_UPLOAD ORDER BY REPORT_DATE DESC LIMIT 1";
        Cursor todoCursor = SD.rawQuery(previousSummary, null);
        if (todoCursor != null && todoCursor.moveToFirst()) {
            do {
                feeder_id = todoCursor.getString(0);
                division_id = todoCursor.getString(1);
//                dc_id = todoCursor.getString(2);
            }
            while (todoCursor.moveToNext());
        }
        chk_com.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chk_com.isChecked()) {
                    check_IRDA.setVisibility(View.VISIBLE);
                    check_Optical.setVisibility(View.VISIBLE);
                    check_LPR.setVisibility(View.VISIBLE);
                } else {
                    str_MeterComPort = "NA";
                    check_IRDA.setVisibility(View.GONE);
                    check_Optical.setVisibility(View.GONE);
                    check_LPR.setVisibility(View.GONE);
                    check_IRDA.setChecked(false);
                    check_Optical.setChecked(false);
                    check_LPR.setChecked(false);
                }
            }
        });

        new Division(FeederSurvey_11kV.this).execute();
        spinner_Division.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                division = div_list.get(position).toString();
                division_id = divid_list.get(position).toString();

                if (position > 0) {
                    spinner_DC.setAdapter(null);
                    dc_list.clear();
                    dcid_list.clear();
                    new DC(FeederSurvey_11kV.this).execute();
                } else {
                    spinner_DC.setAdapter(null);
                    dc_list.clear();
                    dcid_list.clear();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        spinner_DC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // TODO Auto-generated method stub
                dc = dc_list.get(position).toString();
                dc_id = dcid_list.get(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        new Substaion_33_11KV(FeederSurvey_11kV.this).execute();
        spinner_33_11kv_Substation.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//                Toast.makeText(FeederSurvey_11kV.this,
//                        substation_adapter.getItem(position).toString(),
//                        Toast.LENGTH_SHORT).show();
//               System.out.println("ITEM : "+substation_adapter.getItem(position));
//                System.out.println("ITEM ID : "+ substation_adapter.getItemId(position));
//                System.out.println("ITEM Position : "+ substation_adapter.getPosition(substation_adapter.getItem(position)));
//                System.out.println("array  ID : "+  substation_list.get(substation_adapter.getPosition(substation_adapter.getItem(position))));
//                System.out.println("HASH  ID : "+  substation_sample.get(substation_adapter.getItem(position)));
//                System.out.println("HASH  ID : "+  substation_sample.get(substation_adapter.getItem(position)));
                substation = substation_adapter.getItem(position);
                substation_id = substation_sample.get(substation_adapter.getItem(position));

                feeder_list.clear();
                feederid_list.clear();

                new Feeder(FeederSurvey_11kV.this).execute();
            }
        });
//        spinner_33_11kv_Substation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view,
//                                       int position, long id) {
//                // TODO Auto-generated method stub
//                substation = substation_list.get(position).toString();
//                substation_id = substationid_list.get(position).toString();
//Log.e(""," hhhhhhhhhhh "+position);
//                if (position > 0) {
//                    Log.e(""," hhhhhhh@@@@hhhh "+position);
//                    feeder_list.clear();
//                    feederid_list.clear();
//                    new Feeder(FeederSurvey_11kV.this).execute();
//
//                } else {
//                    Log.e(""," hhhhhhhhhh%%%%%h "+position);
//                    spinner_11kVFeederName.setAdapter(null);
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
        new MeterMake(FeederSurvey_11kV.this).execute();

        spinner_Metermake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                metermake = metermake_list.get(position).toString();
                metermake_id = metermakeid_list.get(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        spinner_11kVFeederName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                feeder = feeder_list.get(position).toString();
                feeder_id = feederid_list.get(position).toString();

                if (position > 0) {

                    newTapPointFlag = "N";
                    tv_TapPoint.setVisibility(View.GONE);
                    spinner_TapPoint.setVisibility(View.VISIBLE);
                    tapPoint_List.clear();
                    tapPoint_Id_List.clear();
                    new TapPoint(FeederSurvey_11kV.this).execute();

                } else {

                    spinner_TapPoint.setAdapter(null);
                    tapPoint_List.clear();
                    tapPoint_Id_List.clear();
                }
                try {
                    if (spinner_11kVFeederName.getSelectedItem().toString().equalsIgnoreCase("OTHERS")) {

                        et_11kVFeederName.setVisibility(View.VISIBLE);
                        TV_FEEDER_NAME.setVisibility(View.VISIBLE);

                    } else {
                        et_11kVFeederName.setVisibility(View.GONE);
                        TV_FEEDER_NAME.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Please Select 11Kv Feeder Name", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        button_New.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_TapPoint.setVisibility(View.VISIBLE);
                tv_TapPoint.setEnabled(true);
                spinner_TapPoint.setVisibility(View.GONE);

                newTapPointFlag = "Y";

                String total_c = "select distinct TAP_POINT from TBL_11KVFEEDER_MASTER WHERE FEEDER_CODE='" + feeder_id + "'";  // query
                Cursor cursor_total = SD.rawQuery(total_c, null);
                //Toast.makeText(getApplicationContext(), "Pole Number:" + cursor_total.getCount(), Toast.LENGTH_LONG).show();
                int firstTapPoint = 1;
                tapPointNumber = firstTapPoint + cursor_total.getCount();
                tv_TapPoint.setText("T" + tapPointNumber);

                spinner_TapPoint.setAdapter(null);

//                pole_number_list.clear();
//                new Pre_Pole_Number(PoleSurvey.this).execute();

            }
        });
        spinner_MeterStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (spinner_MeterStatus.getSelectedItem().toString().equalsIgnoreCase("NO METER")) {

                    et_MeterNumber.setEnabled(false);
                    check_IRDA.setEnabled(false);
                    check_Optical.setEnabled(false);
                    check_LPR.setEnabled(false);
                    check_NA.setEnabled(false);
                    spinner_Box.setEnabled(false);
                    chk_seal.setEnabled(false);
                    spinner_Metermake.setEnabled(false);
                    chk_com.setChecked(false);
                    chk_com.setEnabled(false);

                } else if (spinner_MeterStatus.getSelectedItem().toString().equalsIgnoreCase("DEFECTIVE METER")) {

                    et_MeterNumber.setEnabled(false);
                    check_IRDA.setEnabled(false);
                    check_Optical.setEnabled(false);
                    check_LPR.setEnabled(false);
                    check_NA.setEnabled(false);
                    spinner_Box.setEnabled(false);
                    chk_seal.setEnabled(false);
//                    spinner_Metermake.setEnabled(false);
                    chk_com.setChecked(false);
                    chk_com.setEnabled(false);

                } else {
                    et_MeterNumber.setEnabled(true);
                    check_IRDA.setEnabled(true);
                    check_Optical.setEnabled(true);
                    check_LPR.setEnabled(true);
                    check_NA.setEnabled(true);
                    spinner_Box.setEnabled(true);
                    chk_seal.setEnabled(true);
                    chk_com.setChecked(true);
                    chk_com.setEnabled(true);
                    spinner_Metermake.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        spinner_Box.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (spinner_Box.getSelectedItem().toString().equalsIgnoreCase("Yes")) {

                    llSealstatus.setVisibility(View.VISIBLE);

                } else {
                    llSealstatus.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        chk_seal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    tv_box_seal_status.setText("Sealed");
                } else {
                    tv_box_seal_status.setText("Open");
                }
            }
        });

        button_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(getApplicationContext(), SurveyDetails.class));
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right);

            }
        });
        button_Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateAll();
            }
        });

    }

    private void getValue() {

        StructSurvey11KVUpload.MET_CONDITION = String.valueOf(spinner_MeterStatus.getSelectedItem().toString());
        StructSurvey11KVUpload.DIVISION_CODE = String.valueOf(division_id);
        StructSurvey11KVUpload.A33KVFEEDER_CODE = String.valueOf(feeder_id);
        StructSurvey11KVUpload.FEEDER_CODE = String.valueOf(feeder_id);

        StructSurvey11KVUpload.A33KVSUBSTATION_CODE = String.valueOf(substation_id);
        StructSurvey11KVUpload.FEEDER_NAME = String.valueOf(str_11kVFeederName);
        // StructSurveyDTRUpload.USER_ID   = String.valueOf(str_FeederLocation);
        // StructSurvey11KVUpload.MET_READ = String.valueOf(str_MeterReading);
        StructSurvey11KVUpload.REPORT_DATE = String.valueOf(GSBilling.getInstance().captureDate1());
        StructSurvey11KVUpload.FLAG_UPDATE = String.valueOf('1');
        StructSurvey11KVUpload.FLAG_SOURCE = String.valueOf('1');
        StructSurvey11KVUpload.FLAG_UPLOAD = String.valueOf("N");
        StructSurvey11KVUpload.REMARK = str_Remarks;
        StructSurvey11KVUpload.CTR = et_ctr_value.getText().toString();
        StructSurvey11KVUpload.PTR = et_ptr_value.getText().toString();
        StructSurvey11KVUpload.MF = et_mf_value.getText().toString();
        StructSurvey11KVUpload.DC = String.valueOf(dc_id);
//        StructSurvey11KVUpload.TAP_POINT = String.valueOf(tv_TapPoint.getText().toString());
        StructSurvey11KVUpload.TAP_POINT = String.valueOf(str_TapPoint);

        et_11kVFeederName.setVisibility(View.GONE);

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(getApplicationContext(), CameraViewFeeder.class));
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
    }

    private void getMeterValue() {

        StructSurvey11KVUpload.MET_MAKE = String.valueOf(spinner_Metermake.getSelectedItem().toString());
        StructSurvey11KVUpload.MET_COM_PORT = str_MeterComPort;
        StructSurvey11KVUpload.MET_CONDITION = String.valueOf(spinner_MeterStatus.getSelectedItem().toString());
        StructSurvey11KVUpload.METER_BOX_STATUS = String.valueOf(spinner_Box.getSelectedItem().toString());
        StructSurvey11KVUpload.SEAL_STATUS = String.valueOf(tv_box_seal_status.getText());
        StructSurvey11KVUpload.METER_NO = String.valueOf(str_MeterNumber);

    }

    private void validateAll() {
        try {
            //Check if LAT & LONG is ON
            if (latitude1 == 0.00 && longitude1 == 0.00) {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i <= 100; i++) {

                            GPSTracker gps3 = new GPSTracker(FeederSurvey_11kV.this);

                            latitude1 = gps3.getLatitude();
                            longitude1 = gps3.getLongitude();
                            gpstime = gps3.getTime();
                            accuracy = gps3.getAccuracy();
                            altitude = gps3.getAltitude();

                            final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
                            Date d = new Date(gps3.getTime());
                            String sDate = sdf.format(d);

                            Log.e("LOCAITON","LL"+String.valueOf(longitude1));
                            Log.e("LOCAITON","LL"+String.valueOf(latitude1));

                            StructSurvey11KVUpload.TAP_POINT_LONG   = String.valueOf(longitude1);
                            StructSurvey11KVUpload.TAP_POINT_LAT      = String.valueOf(latitude1);


                            if (latitude1 != 0.0) {

                                break; // A unlabeled break is enough. You don't need a labeled break here.
                            }

                        }



                    }
                }, 1 * 5 * 1000);



            }
            if (newTapPointFlag.equalsIgnoreCase("Y")) {
                str_TapPoint = tv_TapPoint.getText().toString().trim();

            } else {
                str_TapPoint = spinner_TapPoint.getSelectedItem().toString().trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            if (spinner_Division.getSelectedItem().toString().trim().equals("Select Division")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Division")
                        .setConfirmText("OK!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();

//            } else if (spinner_33_11kv_Substation.getSelectedItem().toString().trim().equals("Select Sub-Station Name")) {
            } else if (spinner_DC.getSelectedItem().toString().trim().equals("Select DC Name")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select DC Name")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();
            } else if (spinner_33_11kv_Substation.getText().toString().trim().equalsIgnoreCase("Select Sub-Station Name")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Select SubStation Name")
                        .setConfirmText("OK!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        })
                        .show();
                spinner_33_11kv_Substation.requestFocus();
            }
            else if (spinner_33_11kv_Substation.getText().toString().trim().equalsIgnoreCase("")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("ENter SubStation Name")
                        .setConfirmText("OK!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        })
                        .show();
                spinner_33_11kv_Substation.requestFocus();
            }else if (spinner_11kVFeederName.getSelectedItem().toString().trim().equalsIgnoreCase("Select Feeder Name")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Select 11kV Feeder Name")
                        .setConfirmText("OK!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        })
                        .show();
            }  else if (spinner_11kVFeederName.getSelectedItem().toString().trim().equals("OTHERS") && et_11kVFeederName.getText().toString().trim().equals("")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Add 11kV Feeder Name")
                        .setConfirmText("OK!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .show();
            }
            else if (tv_TapPoint.getText().toString().matches((String) "") && spinner_TapPoint.getSelectedItem().toString().equals("Select Tap Point") /*|| */) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Tap Point")
                        .setConfirmText("OK!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .show();
            } else if (spinner_MeterStatus.getSelectedItem().toString().trim().equals("OK")) {
                validateMeterAndCheckBox();
            }
//            else if (et_mf_value.getText().toString().trim().equals("")) {
//                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//                        .setTitleText("ALERT")
//                        .setContentText("Please Select MF")
//                        .setConfirmText("OK!")
//                        .showCancelButton(true)
//                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sDialog) {
//
//                                sDialog.cancel();
//                            }
//                        })
//                        .show();
//            }

            else {
                str_Division = spinner_Division.getSelectedItem().toString().trim();
//                str_33_11kvSubstation = spinner_33_11kv_Substation.getSelectedItem().toString().trim();
                str_33_11kvSubstation = spinner_33_11kv_Substation.getText().toString().trim();

                if (spinner_11kVFeederName.getSelectedItem().toString().equalsIgnoreCase("OTHERS")) {
                    str_11kVFeederName = et_11kVFeederName.getText().toString();
                    String total_c = "select  A33KVFEEDER_CODE from TBL_11KVFEEDER_MASTER where A33KVFEEDER_CODE like '%OTH%'";
                    Log.e("NEW DT ", "QWERY" + total_c);
                    Cursor cursor_total = SD.rawQuery(total_c, null);
                    //Toast.makeText(getApplicationContext(), "Pole Number:" + cursor_total.getCount(), Toast.LENGTH_LONG).show();
                    int polef = 1;
                    int FeederCOde = polef + cursor_total.getCount();
                    feeder_id = "OTH-" + FeederCOde;

                } else {
                    str_11kVFeederName = spinner_11kVFeederName.getSelectedItem().toString();
                }
                str_Remarks = et_remark_33_11kv.getText().toString().trim();
                getValue();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validateMeterAndCheckBox() {
        if (chk_com.isChecked()) {
            if (check_IRDA.isChecked() && !check_Optical.isChecked() && !check_LPR.isChecked()) {
                str_MeterComPort = String.valueOf("AMR");
                Log.e(TAG, "Com Port Value: " + str_MeterComPort);
            } else if (!check_IRDA.isChecked() && check_Optical.isChecked() && !check_LPR.isChecked()) {
                str_MeterComPort = String.valueOf("OPTICAL");
                Log.e(TAG, "Com Port Value: " + str_MeterComPort);
            } else if (!check_IRDA.isChecked() && !check_Optical.isChecked() && check_LPR.isChecked()) {
                str_MeterComPort = String.valueOf("LPR");
                Log.e(TAG, "Com Port Value: " + str_MeterComPort);
            } else if (!check_IRDA.isChecked() && !check_Optical.isChecked() && !check_LPR.isChecked() && check_NA.isChecked()) {
                str_MeterComPort = String.valueOf("NA");
                Log.e(TAG, "Com Port Value: " + str_MeterComPort);
            } else if (check_IRDA.isChecked() && check_Optical.isChecked() && !check_LPR.isChecked()) {
                str_MeterComPort = String.valueOf("AMR_OPTICAL");
                Log.e(TAG, "Com Port Value: " + str_MeterComPort);
            } else if (!check_IRDA.isChecked() && check_Optical.isChecked() && check_LPR.isChecked()) {
                str_MeterComPort = String.valueOf("OPTICAL_LPR");
                Log.e(TAG, "Com Port Value: " + str_MeterComPort);
            } else if (check_IRDA.isChecked() && !check_Optical.isChecked() && check_LPR.isChecked()) {
                str_MeterComPort = String.valueOf("AMR_LPR");
                Log.e(TAG, "Com Port Value: " + str_MeterComPort);
            } else if (check_IRDA.isChecked() && check_Optical.isChecked() && check_LPR.isChecked()) {
                str_MeterComPort = String.valueOf("AMR_OPTICAL_LPR");
                Log.e(TAG, "Com Port Value: " + str_MeterComPort);
            } else {
                str_MeterComPort = "NoSelection";
            }

        } else {
            str_MeterComPort = "NA";
        }
        try {

            if (et_MeterNumber.getText().toString().trim().equals("")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Enter Meter Number")
                        .setConfirmText("OK!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        })
                        .show();

            } else if (str_MeterComPort.equalsIgnoreCase("NoSelection")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Meter Com Port")
                        .setConfirmText("OK!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        })
                        .show();
            } else if (spinner_Box.getSelectedItem().toString().equalsIgnoreCase("Select Box Status")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Meter Box Status")
                        .setConfirmText("OK!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        })
                        .show();
            }
//            else if (!check_IRDA.isChecked() && !check_Optical.isChecked() && !check_LPR.isChecked() && !check_NA.isChecked()) {
//                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//                        .setTitleText("ALERT")
//                        .setContentText("Please Select Meter Com Port")
//                        .setConfirmText("OK!")
//                        .showCancelButton(true)
//                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sDialog) {
//
//                                sDialog.cancel();
//                            }
//                        })
//                        .show();
//            } else if (check_IRDA.isChecked() && check_Optical.isChecked() && check_LPR.isChecked() && check_NA.isChecked()) {
//                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//                        .setTitleText("ALERT")
//                        .setContentText("Cannot Check All Values")
//                        .setConfirmText("OK!")
//                        .showCancelButton(true)
//                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sDialog) {
//                                sDialog.cancel();
//                            }
//                        }).show();
//            } else if (spinner_Box.getSelectedItem().toString().trim().equals("Select Box Status")) {
//                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//                        .setTitleText("ALERT")
//                        .setContentText("Please Select Meter Box Status")
//                        .setConfirmText("OK!")
//                        .showCancelButton(true)
//                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sDialog) {
//
//                                sDialog.cancel();
//                            }
//                        })
//                        .show();
//
//            }
            else if (spinner_Metermake.getSelectedItem().toString().trim().equals("Select Meter Make")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Meter Make")
                        .setConfirmText("OK!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        })
                        .show();
            } else if (et_mf_value.getText().toString().trim().equals("")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Enter MF Value")
                        .setConfirmText("OK!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        })
                        .show();
            }else if (et_ctr_value.getText().toString().trim().equals("")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Enter CTR Value")
                        .setConfirmText("OK!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        })
                        .show();
            }else if (et_ptr_value.getText().toString().trim().equals("")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Enter PTR Value")
                        .setConfirmText("OK!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        })
                        .show();
            } else {

                str_MeterNumber = et_MeterNumber.getText().toString().trim();
                str_MeterBoxStatus = spinner_Box.getSelectedItem().toString().trim();
                str_MeterMake = spinner_Metermake.getSelectedItem().toString().trim();

                str_Division = spinner_Division.getSelectedItem().toString().trim();
//                str_33_11kvSubstation = spinner_33_11kv_Substation.getSelectedItem().toString().trim();
                str_33_11kvSubstation = spinner_33_11kv_Substation.getText().toString().trim();

                if (spinner_11kVFeederName.getSelectedItem().toString().equalsIgnoreCase("OTHERS")) {
                    str_11kVFeederName = et_11kVFeederName.getText().toString();
                    String total_c = "select  A33KVFEEDER_CODE from TBL_11KVFEEDER_MASTER where A33KVFEEDER_CODE like '%OTH%'";
                    Log.e("NEW DT ", "QWERY" + total_c);
                    Cursor cursor_total = SD.rawQuery(total_c, null);
                    //Toast.makeText(getApplicationContext(), "Pole Number:" + cursor_total.getCount(), Toast.LENGTH_LONG).show();
                    int polef = 1;
                    int FeederCOde = polef + cursor_total.getCount();
                    feeder_id = "OTH-" + FeederCOde;

                } else {
                    str_11kVFeederName = spinner_11kVFeederName.getSelectedItem().toString();
                }

                str_Remarks = et_remark_33_11kv.getText().toString().trim();
                getMeterValue();
                getValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callView() {

        spinner_Division = (Spinner) findViewById(R.id.spr_division);
        spinner_DC = (Spinner) findViewById(R.id.spin_dc);
        spinner_33_11kv_Substation = (AutoCompleteTextView) findViewById(R.id.spinner_33_11kv_substation);
        spinner_33_11kv_Substation.setThreshold(0);
        spinner_11kVFeederName = (Spinner) findViewById(R.id.spr_11kv_feedername);
        spinner_TapPoint = (Spinner) findViewById(R.id.spin_tap_point);
        spinner_Metermake = (Spinner) findViewById(R.id.spr_meter_make);

        //spinner_MeterComPort = (Spinner) findViewById(R.id.spr_meter_com_port);
        spinner_MeterStatus = (Spinner) findViewById(R.id.spr_meter_status);
        spinner_Box = (Spinner) findViewById(R.id.spr_box_status);

        et_11kVFeederName = (EditText) findViewById(R.id.et_11kv_feedername);
        et_FeederLocation = (EditText) findViewById(R.id.et_feeder_location);
        et_MeterNumber = (EditText) findViewById(R.id.et_meter_number);
        et_MeterReading = (EditText) findViewById(R.id.et_meter_reading);
        et_remark_33_11kv = (EditText) findViewById(R.id.TextViewRIPfValue);
        et_mf_value = (EditText) findViewById(R.id.et_mf_reading);
        et_ctr_value = (EditText) findViewById(R.id.et_ct_reading);
        et_ptr_value = (EditText) findViewById(R.id.et_pt_reading);

        tv_box_seal_status = (TextView) findViewById(R.id.tv_box_seal_status);
        TV_FEEDER_NAME = (TextView) findViewById(R.id.tv_11kv_feedername);
        tv_TapPoint = (TextView) findViewById(R.id.tv_tap_point);
        tv_TapPoint.setVisibility(View.GONE);

        et_remark_33_11kv.setFilters(new InputFilter[]{filter});

        check_IRDA = (CheckBox) findViewById(R.id.chk_amr);
        check_Optical = (CheckBox) findViewById(R.id.chk_optical);
        check_LPR = (CheckBox) findViewById(R.id.chk_lpr);
        check_NA = (CheckBox) findViewById(R.id.chk_na);
        chk_seal = (Switch) findViewById(R.id.chk_seal);
        chk_com = (Switch) findViewById(R.id.cb_com_port);

        button_Back = (Button) findViewById(R.id.Buttonback);
        button_Continue = (Button) findViewById(R.id.ButtonContreading);
        button_New = (Button) findViewById(R.id.btn_new);

        llSealstatus = (LinearLayout) findViewById(R.id.llSealstatus);
        llSealstatus.setVisibility(View.GONE);
        et_11kVFeederName.setVisibility(View.GONE);
        TV_FEEDER_NAME.setVisibility(View.GONE);

        chk_com.setChecked(true);

        int maxLength = 12;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        et_MeterNumber.setFilters(fArray);
       /* back= (RelativeLayout) findViewById(R.id.relback);
        next= (RelativeLayout) findViewById(R.id.relButton);*/
    }

    //........... Division Class..........................................//
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
            pd = new ProgressDialog(FeederSurvey_11kV.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(FeederSurvey_11kV.this);
            SD = databaseHelper.getReadableDatabase();

            try {

                String divString = "select distinct DIVISION_CODE,DIV_NAME from TBL_DIVISION_MASTER";
//                String divString = "select distinct DIVISION_CODE,DIV_NAME from TBL_DIVISION_MASTER where DIVISION_CODE in (select DIV_CODE from TBL_33KVFEEDER_MASTER where FEEDER_CODE='" + feeder_id + "')";
                System.out.println("nitin: " + divString);
                div_cursor = SD.rawQuery(divString, null);
                if (div_cursor != null && div_cursor.moveToFirst()) {
                    divid_list.add("Select Division ");
                    div_list.add("Select Division ");
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
                spinner_Division.setAdapter(div_adapter);
                if (check) {
                    int position = divid_list.indexOf(division_id);
                    spinner_Division.setSelection(position);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //-----------DC---------//
    public class DC extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        Context _context;

        DC(Context ctx) {
            _context = ctx;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // ShowAlertagain();
            super.onPreExecute();
            pd = new ProgressDialog(FeederSurvey_11kV.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(FeederSurvey_11kV.this);
            SD = databaseHelper.getReadableDatabase();

            try {

                String divString = "select distinct SEC_CODE,SEC_NAME from TBL_SECTION_MASTER where CESU_DIV_CODE='" + division_id + "'";
//                String divString = "select distinct DIVISION_CODE,DIV_NAME from TBL_DIVISION_MASTER where DIVISION_CODE in (select DIV_CODE from TBL_33KVFEEDER_MASTER where FEEDER_CODE='" + feeder_id + "')";
                Log.e("CONSUMER", "DC : " + divString);
                dc_Cursor = SD.rawQuery(divString, null);
                if (dc_Cursor != null && dc_Cursor.moveToFirst()) {
                    dcid_list.add("Select DC Name ");
                    dc_list.add("Select DC Name ");
                    do {
                        String dc_name = dc_Cursor.getString(1);
                        Log.e("", "DC Name_name: " + dc_name);
                        dc_list.add(dc_name);

                        String dc_id = dc_Cursor.getString(0);
                        Log.e("", "DC Name_id: : " + dc_id);
                        dcid_list.add(dc_id);
                    } while (dc_Cursor.moveToNext());
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
//                project_adapter=new ArrayAdapter<String>(ConsumerSurvey.this,android.R.layout.simple_spinner_item,project_name_list);
//                project_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dc_adapter = new ArrayAdapter<String>(
                        getApplicationContext(), R.layout.custom_spinner, R.id.textView1, dc_list);
                spinner_DC.setAdapter(dc_adapter);

//                if (check) {
//                    int position = dcid_list.indexOf(dc_id);
//                    spinner_DC.setSelection(position);
//                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //----------- Sub-Station --------------------------------//
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
            pd = new ProgressDialog(FeederSurvey_11kV.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(FeederSurvey_11kV.this);
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
                        System.out.println(": 33/11kV Sub Station id: " + substation_id);
                        String substation_name = substation_cursor.getString(1);
                        System.out.println("33/11kV Sub Station name: " + substation_name);
                        substationid_list.add(substation_id);
                        substation_list.add(substation_name);
                        substation_sample.put(substation_name, substation_id);
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
                spinner_33_11kv_Substation.setAdapter(substation_adapter);

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
            pd = new ProgressDialog(FeederSurvey_11kV.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(FeederSurvey_11kV.this);
            SD = databaseHelper.getReadableDatabase();

            try {
//
                String feederString = "select distinct FEEDER_CODE, FEEDER_NAME,METERNUMBER from TBL_11KVFEEDER_MASTER WHERE A33KVSUBSTATION_CODE='" + substation_id + "'";
//                String feederString = "select distinct FEEDER_CODE, FEEDER_NAME from TBL_33KVFEEDER_MASTER ";//WHERE SUBSTATION_CODE = '" + substation_id + "'";
                //System.out.println("select distinct FEEDER_NAME from TBL_11KVFEEDER_MASTER WHERE DIVISION_CODE='" + division_id + "'");
                System.out.println(feederString);


                feeder_cursor = SD.rawQuery(feederString, null);
                if (feeder_cursor != null && feeder_cursor.moveToFirst()) {
                    feederid_list.add("Select Feeder Name");
                    feeder_list.add("Select Feeder Name");
                    do {

                        String feeder_id = feeder_cursor.getString(0);
                        String feeder_name = feeder_cursor.getString(1);
                        meterNumber = feeder_cursor.getString(2);
                        System.out.println("feeder_id:  " + feeder_id);
                        feederid_list.add(feeder_id);
                        feeder_list.add(feeder_name);

                    } while (feeder_cursor.moveToNext());
                } else {
                    feederid_list.add("Select Feeder Name");
                    feeder_list.add("Select Feeder Name");
                }
                feederid_list.add("OTHERS");
                feeder_list.add("OTHERS");
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
                spinner_11kVFeederName.setAdapter(feeder_adapter);
                et_MeterNumber.setText(meterNumber);
                if (check) {
                    int position = feederid_list.indexOf(feeder_id);
                    spinner_11kVFeederName.setSelection(position);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //........... TapPoint...........................................//
    public class TapPoint extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        Context _context;

        TapPoint(Context ctx) {
            _context = ctx;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // ShowAlertagain();
            super.onPreExecute();
            pd = new ProgressDialog(FeederSurvey_11kV.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(FeederSurvey_11kV.this);
            SD = databaseHelper.getReadableDatabase();

            try {
                String tapPointQuery = "select distinct FEEDER_CODE,TAP_POINT from TBL_11KVFEEDER_MASTER WHERE FEEDER_CODE ='" + feeder_id + "'"; // wrong query
                Log.e("Tap Point", "Query " + tapPointQuery);
                tapPointCursor = SD.rawQuery(tapPointQuery, null);
                if (tapPointCursor != null && tapPointCursor.moveToFirst()) {
                    tapPoint_Id_List.add("Select Tap Point");
                    tapPoint_List.add("Select Tap Point");
                    do {
                        String tapPoint_id = tapPointCursor.getString(0);
                        Log.e("Tap Point ID", "tapPoint_id: : " + tapPoint_id);
                        String tapPointName = tapPointCursor.getString(1);
                        Log.e("tapPointName", "tapPointName: " + tapPointName);
                        tapPoint_Id_List.add(tapPoint_id);
                        tapPoint_List.add(tapPointName);

                    } while (tapPointCursor.moveToNext());
                } else {
                    tapPoint_Id_List.add("Select Tap Point");
                    tapPoint_List.add("Select Tap Point");
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
                tapPoint_adapter = new ArrayAdapter<String>(
                        getApplicationContext(), R.layout.custom_spinner, R.id.textView1, tapPoint_List);
                spinner_TapPoint.setAdapter(tapPoint_adapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //------------Meter Make--------------------------------//
    public class MeterMake extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        Context _context;

        MeterMake(Context ctx) {
            _context = ctx;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // ShowAlertagain();
            super.onPreExecute();
            pd = new ProgressDialog(FeederSurvey_11kV.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(FeederSurvey_11kV.this);
            SD = databaseHelper.getReadableDatabase();

            try {
                String poleString = "select distinct METERTYPESHORTCODE,METERTYPENAME from TBL_METER_MFG";
                //System.out.println("select distinct POLE_CODE,POLE_TYPE from TBL_POLE_MASTER WHERE FEEDER_CODE ='" + feeder_id + "'");
                metermake_cursor = SD.rawQuery(poleString, null);
                if (metermake_cursor != null && metermake_cursor.moveToFirst()) {
                    metermakeid_list.add("Select Meter Make ");
                    metermake_list.add("Select Meter Make ");
                    do {

                        String metermake_id = metermake_cursor.getString(0);
                        System.out.println("meter_id: : " + metermake_id);
                        String metermake_name = metermake_cursor.getString(1);
                        System.out.println("mete_name: " + metermake_name);
                        metermakeid_list.add(metermake_id);
                        metermake_list.add(metermake_name);

                    } while (metermake_cursor.moveToNext());
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
                metermake_adapter = new ArrayAdapter<String>(
                        getApplicationContext(), R.layout.custom_spinner, R.id.textView1, metermake_list);
                spinner_Metermake.setAdapter(metermake_adapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}