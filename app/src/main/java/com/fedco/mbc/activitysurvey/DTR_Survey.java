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
import android.os.Environment;
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
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.model.StructSurveyDTRUpload;
import com.fedco.mbc.sqlite.DB;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.fedco.mbc.activity.CameraActivity.TEMP_PHOTO_FILE_NAME;

//import feedback.nsc.exisitingconsumer.ExistingConsumer_Search;


public class DTR_Survey extends Activity {


    // Spinner cycle,route,ownerships,cons_type, department,ed_exception,meter_status;

    private Spinner spinner_Division, spinner_DC, spinner_Box, spinner_dtr_name,/*spinner_SubDivision,*/spinner_DTR_Status,
            spinner_11kVFeederName, spinner_MeterMake, spinner_DTR_Capacity, spinner_No_Of_LT, spinner_MeterCondition, spinner_HT;
    AutoCompleteTextView spinner_33_11kv_substation;
    HashMap<String, String> substation_sample;
    Cursor div_cursor, dc_Cursor, met_mfg_cursor, sec_cursor, cycle_cursor, feeder_cursor, dt_number_cursor, metermake_cursor, substation_cursor;
    String subdivsion, section, cycle;

    EditText et_DTR_Location, et_MeterNumber, et_Remarks, et_MeterReading;
    TextView tv_DTR_Number;
    Button button_Back, button_Continue, button_New;
    private File mFileTemp;
    CheckBox check_IRDA, check_Optical, check_LPR, check_NA, chk_AMR;
    Switch chk_seal, check_HT, chk_com;
    String meterComPort;
    TextView tv_box_seal_status, tv_HT;
    RelativeLayout back;
    RelativeLayout next;
    LinearLayout llSealstatus;
    RadioGroup radio_theft;
    String ht_consumer;

    String str_Division, str_33_11_substation, str_11kV_Feeder, str_MeterMake, str_DTR_Capacity, str_No_Of_LT, str_MeterCondition,
            str_DTR_Number, str_DTR_Location, str_MeterNumber, str_Remark, str_MeterReading,
            str_MeterBoxStatus, str_dt_number, str_DTR_name;
    String division, division_id,dc,dc_id, feeder, feeder_id, metermake, metermake_id, substation, substation_id, dt_number_id;
    String str_IRDA, str_Optical, str_LPR;

    ArrayList<String> div_list, divid_list, dc_list, dcid_list, feeder_list, feederid_list, metermake_list, metermakeid_list, substation_list, substationid_list,
            dt_number_list, dtnumber_id_list;
    ArrayAdapter<String> div_adapter, dc_adapter, feeder_adapter, metermake_adapter, substation_adapter, sdiv_adapter, dtNumber_adapter;
    DB dbHelper;
    SQLiteDatabase sb;

    boolean check = true;

    String str_MeterComPort;
    SQLiteDatabase SD;
    DB databaseHelper;

    final Context context = this;
    private String blockCharacterSet = "}{";

    String newPole_Flag = "N";

    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };
    InputFilter filterSpace = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (Character.isWhitespace(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dtr_survey);
        callView();

        divid_list = new ArrayList<String>();
        div_list = new ArrayList<String>();
        dc_list = new ArrayList<String>();
        dcid_list = new ArrayList<String>();
        feeder_list = new ArrayList<String>();
        feederid_list = new ArrayList<String>();
        metermake_list = new ArrayList<String>();
        metermakeid_list = new ArrayList<String>();
        substation_list = new ArrayList<String>();
        substationid_list = new ArrayList<String>();
        dt_number_list = new ArrayList<String>();
        dtnumber_id_list = new ArrayList<String>();
        substation_sample = new HashMap<String, String>();
        substation_sample.clear();
        dbHelper = new DB(getApplicationContext());
        SD = dbHelper.getWritableDatabase();
        String previousSummary = "SELECT DIV_CODE,FEEDER_CODE FROM TBL_DTR_UPLOAD ORDER BY REPORT_DATE DESC LIMIT 1";
        Cursor todoCursor = SD.rawQuery(previousSummary, null);
        if (todoCursor != null && todoCursor.moveToFirst()) {
            do {
                division_id = todoCursor.getString(0);
                feeder_id = todoCursor.getString(1);

            }
            while (todoCursor.moveToNext());
        }

//        check_IRDA.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (check_IRDA.isChecked()) {
//                    //Toast.makeText(DTR_Survey.this,"IRDA checkbox checked", Toast.LENGTH_SHORT).show();
//                    GSBilling.getInstance().setIrda("IRDA");
//                } else
//
//                {
//                    GSBilling.getInstance().setIrda("");
//                    //Toast.makeText(DTR_Survey.this,"IRDA checkbox Unchecked", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
//        check_Optical.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (check_Optical.isChecked()) {
//                    GSBilling.getInstance().setOptical("OPTICAL");
//                    //Toast.makeText(DTR_Survey.this,"Optical checkbox checked", Toast.LENGTH_SHORT).show();
//                } else
//
//                {
//                    GSBilling.getInstance().setOptical("");
//                    //Toast.makeText(DTR_Survey.this,"Optical checkbox Unchecked", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
//
//        check_LPR.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (check_LPR.isChecked()) {
//                    GSBilling.getInstance().setLpr("LPR");
//                    //Toast.makeText(DTR_Survey.this,"LPR checkbox checked", Toast.LENGTH_SHORT).show();
//                } else
//
//                {
//                    GSBilling.getInstance().setLpr("");
//                    // Toast.makeText(DTR_Survey.this,"LPR checkbox Unchecked", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });

        chk_com.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chk_com.isChecked()) {
                    check_IRDA.setVisibility(View.VISIBLE);
                    check_Optical.setVisibility(View.VISIBLE);
                    check_LPR.setVisibility(View.VISIBLE);
                    chk_AMR.setVisibility(View.VISIBLE);
                } else {
                    str_MeterComPort = "NA";
                    check_IRDA.setVisibility(View.GONE);
                    check_Optical.setVisibility(View.GONE);
                    check_LPR.setVisibility(View.GONE);
                    chk_AMR.setVisibility(View.GONE);

                    check_IRDA.setChecked(false);
                    check_Optical.setChecked(false);
                    check_LPR.setChecked(false);
                    chk_AMR.setChecked(false);
                }
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

//                  Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_LONG).show();

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

        check_HT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
//                    spinner_HT.setVisibility(View.VISIBLE);
//                    tv_HT.setVisibility(View.VISIBLE);

                    ht_consumer = "Y";

                } else {
//                    spinner_HT.setVisibility(View.GONE);
//                    tv_HT.setVisibility(View.GONE);

                    ht_consumer = "N";
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
//                Toast.makeText(getApplicationContext(),"hh",Toast.LENGTH_LONG).show();
                validateAll();

            }
        });
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
        }


        new Division(DTR_Survey.this).execute();
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
                    new DC(DTR_Survey.this).execute();
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
                dc    = dc_list.get(position).toString();
                dc_id = dcid_list.get(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        new Substaion_33_11KV(DTR_Survey.this).execute();
        spinner_33_11kv_substation.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                substation = substation_adapter.getItem(position);
                substation_id = substation_sample.get(substation_adapter.getItem(position));

                feeder_list.clear();
                feederid_list.clear();

                new Feeder(DTR_Survey.this).execute();
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
//                    new Feeder(DTR_Survey.this).execute();
//
//                } else {
//
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
        spinner_11kVFeederName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                feeder = feeder_list.get(position).toString();
                feeder_id = feederid_list.get(position).toString();

                if (position > 0) {

                    newPole_Flag = "N";
                    tv_DTR_Number.setVisibility(View.GONE);
                    spinner_dtr_name.setVisibility(View.VISIBLE);
                    dt_number_list.clear();
                    dtnumber_id_list.clear();
                    new DTR_Number(DTR_Survey.this).execute();

                } else {

                    spinner_dtr_name.setAdapter(null);
                    dt_number_list.clear();
                    dtnumber_id_list.clear();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        spinner_MeterCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (spinner_MeterCondition.getSelectedItem().toString().equalsIgnoreCase("NO METER")) {

                    et_MeterNumber.setEnabled(false);

                    check_IRDA.setEnabled(false);
                    check_Optical.setEnabled(false);
                    check_LPR.setEnabled(false);
                    check_NA.setEnabled(false);
                    spinner_Box.setEnabled(false);
                    chk_AMR.setEnabled(false);
                    chk_seal.setEnabled(false);
                    chk_com.setChecked(false);
                    chk_com.setEnabled(false);
                    et_MeterReading.setEnabled(false);
                    spinner_MeterMake.setEnabled(false);

                } else {
                    et_MeterNumber.setEnabled(true);
                    check_IRDA.setEnabled(true);
                    check_Optical.setEnabled(true);
                    check_LPR.setEnabled(true);
                    check_NA.setEnabled(true);
                    chk_AMR.setEnabled(true);
                    spinner_Box.setEnabled(true);
                    chk_seal.setEnabled(true);
                    chk_com.setChecked(true);
                    chk_com.setEnabled(true);
                    et_MeterReading.setEnabled(true);
                    spinner_MeterMake.setEnabled(true);
                }

//                  Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        new MeterMake(DTR_Survey.this).execute();
        spinner_MeterMake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                metermake = metermake_list.get(position).toString();
                metermake_id = metermakeid_list.get(position).toString();

//                  Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        button_New.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_DTR_Number.setVisibility(View.VISIBLE);
                tv_DTR_Number.setEnabled(true);
                spinner_dtr_name.setVisibility(View.GONE);
                newPole_Flag = "Y";
                try {

                    String total_c = "select DTR_CODE from TBL_DTR_UPLOAD WHERE FEEDER_CODE='" + feeder_id + "'";
                    Log.e("NEW DT ", "QWERY" + total_c);
                    Cursor cursor_total = SD.rawQuery(total_c, null);
                    //Toast.makeText(getApplicationContext(), "Pole Number:" + cursor_total.getCount(), Toast.LENGTH_LONG).show();
                    int polef = 1;
                    int DTRumber = polef + cursor_total.getCount();
                    tv_DTR_Number.setText("DT-" + DTRumber);

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    private void validateAll() {
        str_DTR_Number = null;
        str_DTR_Capacity = spinner_DTR_Capacity.getSelectedItem().toString();
        str_No_Of_LT = spinner_No_Of_LT.getSelectedItem().toString();
        str_MeterCondition = spinner_MeterCondition.getSelectedItem().toString();
        str_Remark = et_Remarks.getText().toString().trim();

        try{
            if (newPole_Flag.equalsIgnoreCase("Y")) {
                str_DTR_Number = tv_DTR_Number.getText().toString();
            } else {
                str_DTR_Number = spinner_dtr_name.getSelectedItem().toString();
            }
        }catch(Exception e){
            e.printStackTrace();
//            Toast.makeText(getApplicationContext(),"Please Select DTR Number",Toast.LENGTH_LONG).show();
        }
        try {
            if (spinner_Division.getSelectedItem().toString().trim().equals("Select Division Name")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Division Name")
                        .setConfirmText("OK!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        })
                        .show();
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
            }else if (spinner_33_11kv_substation.getText().toString().trim().equals("")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Sub-Station Name")
                        .setConfirmText("OK!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();
            } else if (spinner_11kVFeederName.getSelectedItem().toString().trim().equals("Select feeder Name")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Feeder Name")
                        .setConfirmText("OK!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();
            } else if (tv_DTR_Number.getText().toString().matches((String) "") && spinner_dtr_name.getSelectedItem().toString().equalsIgnoreCase("Select DTR Number")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select / Add DTR Number")
                        .setConfirmText("OK!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();
            } else if (et_DTR_Location.getText().toString().trim().equals("")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Enter DTR Name")
                        .setConfirmText("OK!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();
                et_DTR_Location.requestFocus();
            } else if (spinner_DTR_Status.getSelectedItem().toString().trim().equalsIgnoreCase("Select DTR Status")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select DTR Status")
                        .setConfirmText("OK!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();
                et_DTR_Location.requestFocus();
            } else if (spinner_MeterCondition.getSelectedItem().toString().equals("OK")) {
                validateMeterAndCheckBox();
            } else if (spinner_MeterCondition.getSelectedItem().toString().trim().equals("DEFECTIVE METER")) {
                validateMeterAndCheckBox();
            } else {
                str_Division = spinner_Division.getSelectedItem().toString().trim();
                str_33_11_substation = spinner_33_11kv_substation.getText().toString();
                str_11kV_Feeder = spinner_11kVFeederName.getSelectedItem().toString().trim();
//                if(spinner_dtr_name.getSelectedItem().toString().equalsIgnoreCase("Select DTR Number") && !tv_DTR_Number.getText().toString().equalsIgnoreCase(""))
//                {
//                    str_DTR_name=tv_DTR_Number.getText().toString();
//                }else if(!spinner_dtr_name.getSelectedItem().toString().equalsIgnoreCase("Select DTR Number") && tv_DTR_Number.getText().toString().equalsIgnoreCase(""))
//                {
//                    str_DTR_name=spinner_dtr_name.getSelectedItem().toString();
//                }
                str_DTR_name = tv_DTR_Number.getText().toString();
                str_DTR_Location = et_DTR_Location.getText().toString().trim();
                str_MeterNumber = et_MeterNumber.getText().toString().trim();
                str_MeterReading = et_MeterReading.getText().toString().trim();
                str_MeterBoxStatus = spinner_Box.getSelectedItem().toString().trim();
                getValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validateMeterAndCheckBox() {

        if (chk_com.isChecked()) {
            if (check_IRDA.isChecked() && !check_Optical.isChecked() && !check_LPR.isChecked() && !chk_AMR.isChecked()) {
                str_MeterComPort = String.valueOf("IRDA");

            } else if (!check_IRDA.isChecked() && check_Optical.isChecked() && !check_LPR.isChecked() && !chk_AMR.isChecked()) {
                str_MeterComPort = String.valueOf("OPTICAL");

            } else if (!check_IRDA.isChecked() && !check_Optical.isChecked() && check_LPR.isChecked() && !chk_AMR.isChecked()) {
                str_MeterComPort = String.valueOf("LPR");

            } else if (!check_IRDA.isChecked() && !check_Optical.isChecked() && !check_LPR.isChecked() && chk_AMR.isChecked()) {
                str_MeterComPort = String.valueOf("AMR");

            } else if (check_IRDA.isChecked() && check_Optical.isChecked() && !check_LPR.isChecked() && !chk_AMR.isChecked()) {
                str_MeterComPort = String.valueOf("IRDA_OPTICAL");

            } else if (check_IRDA.isChecked() && !check_Optical.isChecked() && check_LPR.isChecked() && !chk_AMR.isChecked()) {
                str_MeterComPort = String.valueOf("IRDA_LPR");

            } else if (check_IRDA.isChecked() && !check_Optical.isChecked() && !check_LPR.isChecked() && chk_AMR.isChecked()) {
                str_MeterComPort = String.valueOf("IRDA_AMR");

            } else if (!check_IRDA.isChecked() && check_Optical.isChecked() && check_LPR.isChecked()) {
                str_MeterComPort = String.valueOf("OPTICAL_LPR");

            } else if (!check_IRDA.isChecked() && check_Optical.isChecked() && !check_LPR.isChecked() && chk_AMR.isChecked()) {
                str_MeterComPort = String.valueOf("OPTICAL_AMR");

            } else if (!check_IRDA.isChecked() && !check_Optical.isChecked() && check_LPR.isChecked() && chk_AMR.isChecked()) {
                str_MeterComPort = String.valueOf("LPR_AMR");

            } else if (check_IRDA.isChecked() && check_Optical.isChecked() && !check_LPR.isChecked() && chk_AMR.isChecked()) {
                str_MeterComPort = String.valueOf("IRDA_OPTICAL_AMR");

            } else if (check_IRDA.isChecked() && check_Optical.isChecked() && check_LPR.isChecked() && !chk_AMR.isChecked()) {
                str_MeterComPort = String.valueOf("IRDA_OPTICAL_LPR");

            } else if (check_IRDA.isChecked() && !check_Optical.isChecked() && check_LPR.isChecked() && chk_AMR.isChecked()) {
                str_MeterComPort = String.valueOf("IRDA_AMR_LPR");

            } else if (!check_IRDA.isChecked() && check_Optical.isChecked() && check_LPR.isChecked() && chk_AMR.isChecked()) {
                str_MeterComPort = String.valueOf("OPTICAL_AMR_LPR");

            } else if (check_IRDA.isChecked() && check_Optical.isChecked() && check_LPR.isChecked() && chk_AMR.isChecked()) {
                str_MeterComPort = String.valueOf("IRDA_OPTICAL_LPR_AMR");

            } else {
                str_MeterComPort = "NoSelection";
            }
        } else {
            str_MeterComPort = "NA";
        }


        try {
            if (str_MeterComPort.equalsIgnoreCase("NoSelection")) {
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
                        }).show();
            } else if (spinner_MeterMake.getSelectedItem().toString().trim().equals("Select Meter Make")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Meter make")
                        .setConfirmText("OK!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();
            } else if (spinner_Box.getSelectedItem().toString().trim().equals("Select Box Status")) {
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
                        }).show();
            } else {
                str_MeterNumber = et_MeterNumber.getText().toString().trim();
                str_MeterReading = et_MeterReading.getText().toString().trim();
                str_MeterBoxStatus = spinner_Box.getSelectedItem().toString().trim();

                str_Division = spinner_Division.getSelectedItem().toString().trim();
                str_33_11_substation = spinner_33_11kv_substation.getText().toString();
                str_11kV_Feeder = spinner_11kVFeederName.getSelectedItem().toString().trim();
//                if(spinner_dtr_name.getSelectedItem().toString().equalsIgnoreCase("Select DTR Number") && !tv_DTR_Number.getText().toString().equalsIgnoreCase(""))
//                {
//                    str_DTR_name=tv_DTR_Number.getText().toString();
//                }else if(!spinner_dtr_name.getSelectedItem().toString().equalsIgnoreCase("Select DTR Number") && tv_DTR_Number.getText().toString().equalsIgnoreCase(""))
//                {
//                    str_DTR_name=spinner_dtr_name.getSelectedItem().toString();
//                }
                str_DTR_name = tv_DTR_Number.getText().toString();
                str_DTR_Location = et_DTR_Location.getText().toString().trim();
                str_MeterNumber = et_MeterNumber.getText().toString().trim();
                str_MeterReading = et_MeterReading.getText().toString().trim();

                getValueMeter();
                getValue();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getValue() {


        switch (radio_theft.getCheckedRadioButtonId()) {
            case R.id.radio_yes:
                StructSurveyDTRUpload.THEFT_PRONE = String.valueOf("Y");
                // do operations specific to this selection
                break;
            case R.id.radio_no:
                StructSurveyDTRUpload.THEFT_PRONE = String.valueOf("N");
                // do operations specific to this selection
                break;
        }

//        if(spinner_dtr_name.getSelectedItem().toString().equalsIgnoreCase("Select DTR Number")){
//            str_DTR_Number=tv_DTR_Number.getText().toString();
//        }else{
//            str_DTR_Number=spinner_dtr_name.getSelectedItem().toString();
//        }

        StructSurveyDTRUpload.MET_CONDITION = String.valueOf(str_MeterCondition);
        StructSurveyDTRUpload.DIV_CODE = String.valueOf(division_id);
        StructSurveyDTRUpload.DTR_NAME = String.valueOf(et_DTR_Location.getText());
        StructSurveyDTRUpload.FEEDER_CODE = String.valueOf(feeder_id);
        StructSurveyDTRUpload.DTR_CODE = String.valueOf(str_DTR_Number);
        StructSurveyDTRUpload.DTR_CODING = String.valueOf(str_DTR_Number);
        StructSurveyDTRUpload.DTR_STATUS = String.valueOf(str_DTR_Capacity);
        StructSurveyDTRUpload.HT_CONSUMERS = String.valueOf(ht_consumer);

        StructSurveyDTRUpload.LT_CIRCUIT = String.valueOf(str_No_Of_LT);

        StructSurveyDTRUpload.FLAG_UPDATE = String.valueOf('1');
        StructSurveyDTRUpload.FLAG_SOURCE = String.valueOf('1');
        StructSurveyDTRUpload.FLAG_UPLOAD = String.valueOf("N");
        StructSurveyDTRUpload.REPORT_DATE = String.valueOf(GSBilling.getInstance().captureDate1());
        StructSurveyDTRUpload.REMARKS = et_Remarks.getText().toString();
        StructSurveyDTRUpload.DTR_DC = String.valueOf(dc_id);
        StructSurveyDTRUpload.DTR_STS = String.valueOf(spinner_DTR_Status.getSelectedItem().toString());

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(getApplicationContext(), CameraViewDTR.class));
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);

    }

    private void getValueMeter() {

        StructSurveyDTRUpload.MET_MAKE = String.valueOf(metermake);
        StructSurveyDTRUpload.METER_NO = String.valueOf(str_MeterNumber);
        StructSurveyDTRUpload.MET_COM_PORT = str_MeterComPort;
        StructSurveyDTRUpload.MET_READ = String.valueOf(str_MeterReading);
        StructSurveyDTRUpload.MET_CONDITION = String.valueOf(str_MeterCondition);
        StructSurveyDTRUpload.METER_BOX_STATUS = String.valueOf(spinner_Box.getSelectedItem().toString());
        StructSurveyDTRUpload.SEAL_STATUS = String.valueOf(tv_box_seal_status.getText());

    }

    public void callView() {

        spinner_Division = (Spinner) findViewById(R.id.spr_division);
        spinner_DC = (Spinner) findViewById(R.id.spin_dc);
        spinner_33_11kv_substation = (AutoCompleteTextView) findViewById(R.id.spin_33_11kv_substation);
        spinner_33_11kv_substation.setThreshold(1);
        spinner_11kVFeederName = (Spinner) findViewById(R.id.spr_11kv_feedername);
        spinner_MeterMake = (Spinner) findViewById(R.id.spr_meter_make);
        spinner_DTR_Capacity = (Spinner) findViewById(R.id.spr_dtr_capacity);
        spinner_No_Of_LT = (Spinner) findViewById(R.id.spr_no_of_lt);
        spinner_MeterCondition = (Spinner) findViewById(R.id.spr_meter_condition);
        spinner_Box = (Spinner) findViewById(R.id.spr_box_status);
        spinner_dtr_name = (Spinner) findViewById(R.id.spin_dtr_number);
        spinner_DTR_Status = (Spinner) findViewById(R.id.spr_dtrStatus);
//      spinner_HT = (Spinner) findViewById(R.id.spr_no_of_HT);
        radio_theft = (RadioGroup) findViewById(R.id.mainRadio);

        tv_DTR_Number = (TextView) findViewById(R.id.tv_dtr_number1);
        et_DTR_Location = (EditText) findViewById(R.id.et_dtr_location);
        et_MeterNumber = (EditText) findViewById(R.id.et_meter_number);
        et_Remarks = (EditText) findViewById(R.id.et_remark);
        et_MeterReading = (EditText) findViewById(R.id.et_meter_reading);
        et_Remarks.setFilters(new InputFilter[]{filter});
        et_MeterNumber.setFilters(new InputFilter[]{filterSpace});

        back = (RelativeLayout) findViewById(R.id.relback);
        next = (RelativeLayout) findViewById(R.id.relButton);

        button_Back = (Button) findViewById(R.id.Buttonback);
        button_Continue = (Button) findViewById(R.id.ButtonContreading);
        button_New = (Button) findViewById(R.id.btn_new);

        check_IRDA = (CheckBox) findViewById(R.id.chk_irda);
        check_Optical = (CheckBox) findViewById(R.id.chk_optical);
        check_LPR = (CheckBox) findViewById(R.id.chk_lpr);
        check_NA = (CheckBox) findViewById(R.id.chk_na);
        check_HT = (Switch) findViewById(R.id.cb_no_of_ht);
        chk_AMR = (CheckBox) findViewById(R.id.chk_AMR);

        chk_com = (Switch) findViewById(R.id.cb_com_port);
        chk_seal = (Switch) findViewById(R.id.chk_seal);
        tv_box_seal_status = (TextView) findViewById(R.id.tv_box_seal_status);
        tv_HT = (TextView) findViewById(R.id.tv_HT_circuits);

        llSealstatus = (LinearLayout) findViewById(R.id.llSealstatus);
        llSealstatus.setVisibility(View.GONE);
        tv_DTR_Number.setVisibility(View.GONE);

        chk_com.setChecked(true);
//        spinner_HT.setVisibility(View.GONE);
//        tv_HT.setVisibility(View.GONE);
        int maxLength = 12;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        et_MeterNumber.setFilters(fArray);

        int maxLengthDTR = 50;
        InputFilter[] fArray2 = new InputFilter[1];
        fArray2[0] = new InputFilter.LengthFilter(maxLengthDTR);
        et_DTR_Location.setFilters(fArray2);

        dbHelper = new DB(getApplicationContext());
        sb = dbHelper.getWritableDatabase();

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
            pd = new ProgressDialog(DTR_Survey.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(DTR_Survey.this);
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
            pd = new ProgressDialog(DTR_Survey.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(DTR_Survey.this);
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

    //------------------Sub-Station--------------------------------//
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
            pd = new ProgressDialog(DTR_Survey.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(DTR_Survey.this);
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
                spinner_33_11kv_substation.setAdapter(substation_adapter);

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
            pd = new ProgressDialog(DTR_Survey.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(DTR_Survey.this);
            SD = databaseHelper.getReadableDatabase();

            try {
                String feederString = "select distinct FEEDER_CODE, FEEDER_NAME from TBL_11KVFEEDER_MASTER WHERE A33KVSUBSTATION_CODE='" + substation_id + "'";
                System.out.println("select distinct FEEDER_NAME from TBL_11KVFEEDER_MASTER WHERE FEEDER_CODE='" + substation_id + "'");
                feeder_cursor = SD.rawQuery(feederString, null);
                if (feeder_cursor != null && feeder_cursor.moveToFirst()) {
                    feederid_list.add("Select feeder Name");
                    feeder_list.add("Select feeder Name");
                    do {

                        String feeder_id = feeder_cursor.getString(0);
                        String feeder_name = feeder_cursor.getString(1);
                        Log.e("DTR ", "feeder_id:  " + feeder_id);
                        Log.e("DTR ", "feeder_name:  " + feeder_name);
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
                spinner_11kVFeederName.setAdapter(feeder_adapter);

                if (check) {
                    int position = feederid_list.indexOf(feeder_id);
                    spinner_11kVFeederName.setSelection(position);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //........... DTR...........................................//
    public class DTR_Number extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        Context _context;

        DTR_Number(Context ctx) {
            _context = ctx;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // ShowAlertagain();
            super.onPreExecute();
            pd = new ProgressDialog(DTR_Survey.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(DTR_Survey.this);
            SD = databaseHelper.getReadableDatabase();

            try {
                String poleString = "select distinct DTR_CODE,DTR_CODING from TBL_DTR_MASTER WHERE FEEDER_CODE ='" + feeder_id + "'";
                Log.e("DTR Number", "QWERY " + poleString + "'");
                dt_number_cursor = SD.rawQuery(poleString, null);
                if (dt_number_cursor != null && dt_number_cursor.moveToFirst()) {
                    dtnumber_id_list.add("Select DTR Number");
                    dt_number_list.add("Select DTR Number");
                    do {

                        String dt_id = dt_number_cursor.getString(0);
                        Log.e("DTR Number", "dt_code: : " + dt_id);
                        String dt_name = dt_number_cursor.getString(1);
                        Log.e("DTR Number", "dt_coding: " + dt_name);
                        dtnumber_id_list.add(dt_id);
                        dt_number_list.add(dt_id+" "+dt_name);

                    } while (dt_number_cursor.moveToNext());
                } else {
                    dtnumber_id_list.add("Select DTR Number");
                    dt_number_list.add("Select DTR Number");
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
                dtNumber_adapter = new ArrayAdapter<String>(
                        getApplicationContext(), R.layout.custom_spinner, R.id.textView1, dt_number_list);
                spinner_dtr_name.setAdapter(dtNumber_adapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //------------------Meter Make--------------------------------//
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
            pd = new ProgressDialog(DTR_Survey.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(DTR_Survey.this);
            SD = databaseHelper.getReadableDatabase();

            try {
                String poleString = "select distinct METERTYPESHORTCODE,METERTYPENAME from TBL_METER_MFG";
                //System.out.println("select distinct POLE_CODE,POLE_TYPE from TBL_POLE_MASTER WHERE FEEDER_CODE ='" + feeder_id + "'");
                metermake_cursor = SD.rawQuery(poleString, null);
                if (metermake_cursor != null && metermake_cursor.moveToFirst()) {
                    metermakeid_list.add("Select Meter Make");
                    metermake_list.add("Select Meter Make");
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
                spinner_MeterMake.setAdapter(metermake_adapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}