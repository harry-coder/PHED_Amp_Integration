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
import com.fedco.mbc.activity.SDActivity;
import com.fedco.mbc.activity.StartLocationAlert;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.model.StructSurveyConsumerMaster;
import com.fedco.mbc.model.StructSurveyConsumerUpload;
import com.fedco.mbc.model.Structbilling;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.UtilAppCommon;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ConsumerSurvey extends Activity {
    public static final String TAG = ConsumerSurvey.class.getSimpleName();
    private EditText et_ConsumerNumber, et_ConsumerName, et_MeterReading, et_MeterNumber, et_Remarks, et_Address, et_ConsumerMobile,
            et_consumer_email, et_NeighbourMeterNo, et_Adhar, et_load;
    private Spinner spinner_Division, spinner_11kv_Feeder, spinner_MeterMake, spinner_MeterBoxStatus,
            spinner_ServiceCable, spinner_ActionRequired, spinner_ConsumerType, spinner_Feeder, spinner_DTR, spinner_Pole,
            spinner_MeterCondition, spinner_Premises, spinner_No_of_Rooms, spinner_No_of_AirConditioners, spinner_No_of_Coolers, spinner_Remarks,
            et_NeighbourConsNo, spinner_dc, spinner_MeterLOC, spinner_MeterCAP, spinner_MeterTYP, spinner_Caste, spinner_conn_type;
    private Button button_Add, button_Back, button_Continue;
    LinearLayout linear_connection, linear_load;
    AutoCompleteTextView spinner_33_11kv_Substation;
    final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    HashMap<String, String> substation_sample;
    HashMap<String, String> div_sample;
    HashMap<String, String> feeder_sample;
    HashMap<String, String> dt_sample;
    HashMap<String, String> pole_sample;
    LinearLayout tr_Feeder, tr_Dtr, tr_Pole;
    LinearLayout llSealstatus;
    TextView tv_box_seal_status, tv_MeterPhaseStatus, tv_NoOfRooms, tv_NoOfAC, tv_NoOfCoolers, tv_WaterPump, tv_WaterPumpStatus, tv_Sub_33kV, tv_Feeder, tv_Dtr, tv_Pole;
    boolean check = true;
    final Context context = this;
    private static ConsumerSurvey sActivity;

    public static ConsumerSurvey getsActivity() {
        return sActivity;
    }

    String str_ConsumerNumber, str_ConsumerName, str_ConsumerType, str_ConsumerAddress, str_ConsumerMobile, str_ConsumerEmail,
            str_MeterReading, str_MeterNumber, str_Substastion_33kV, str_FeederSpin, str_DTR_Spin, str_PoleSpin, str_MeterCondition, str_PremisesType,
            str_ServiceCable, str_ActionRequired, str_Remarks1, str_Remarks2, str_MeterMake, str_MeterBoxStatus,
            div_code, division_id, sub_div_code, sub_division_id, str_Substation, str_SubstationId, str_Feeder, feeder_id, str_DTR, dt_id,
            str_MeterMakeSpin, str_MeterComPort, str_CheckSeal, str_Pole, str_PoleValue, pole_id, metermake_id;

    ArrayList<String> consumerList, subdiv_list, subdivid_list, div_list, divid_list, substation_list, substationid_list, feeder_list, feederid_list, dt_list, dtid_list,
            pole_list, poleid_list, metermake_list, metermakeid_list, neighcon_list, neighconid_list, divcode_list;
    RelativeLayout back;
    RelativeLayout next;
    String FlowValue, IR_val, OPT_val, LPR_val, key;

    Cursor division_Cursor, substation_cursor, subdivision_cursor, feeder_cursor, dt_cursor, pole_cursor, metermake_cursor, neighcon_cursor;

    ArrayAdapter<String> div_adapter, subdiv_adapter, substation_adapter, feeder_adapter, dt_adapter, pole_adapter, metermake_adapter, neighcon_adapter;

    SQLiteDatabase SD;
    DB databaseHelper;
    CheckBox IR, OPT, LPR, NA;
    StartLocationAlert startLocationAlert;
    Switch chk_seal, chk_MeterPhase, chk_com, chk_WaterPump;
    private String blockCharacterSet = "}{";
    SessionManager session;
    UtilAppCommon appcomUtil;

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

    private String blockCharacterSetSpecial = "!@`#$%^&*()_-=+/~.<>?|{}:;'";

    private InputFilter filterSpecial = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSetSpecial.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consumer_survey);

        sActivity = this;
        callView();

        div_list = new ArrayList<String>();
        divid_list = new ArrayList<String>();
        substation_list = new ArrayList<String>();
        substationid_list = new ArrayList<String>();
        feeder_list = new ArrayList<String>();
        feederid_list = new ArrayList<String>();
        dt_list = new ArrayList<String>();
        dtid_list = new ArrayList<String>();
        pole_list = new ArrayList<String>();
        poleid_list = new ArrayList<String>();
        metermake_list = new ArrayList<String>();
        metermakeid_list = new ArrayList<String>();
        neighconid_list = new ArrayList<String>();
        neighcon_list = new ArrayList<String>();
        divcode_list = new ArrayList<String>();
        subdiv_list = new ArrayList<String>();
        subdivid_list = new ArrayList<String>();
        substation_sample = new HashMap<String, String>();
        div_sample = new HashMap<String, String>();
        feeder_sample = new HashMap<String, String>();
        dt_sample = new HashMap<String, String>();
        pole_sample = new HashMap<String, String>();
        substation_sample.clear();
        div_sample.clear();
        feeder_sample.clear();
        dt_sample.clear();
        pole_sample.clear();
        FlowValue = getIntent().getStringExtra("flow");

        if (FlowValue.equals("POLE")) {

            tv_Sub_33kV.setText(StructSurveyConsumerUpload.POLE_SUBSTATION_33kV);
            tv_Feeder.setText(StructSurveyConsumerUpload.POLE_FEEDER_11kV);
            tv_Dtr.setText(StructSurveyConsumerUpload.POLE_DTR);
            tv_Pole.setText(StructSurveyConsumerUpload.POLE_POLE);

            spinner_33_11kv_Substation.setVisibility(View.GONE);
            spinner_Feeder.setVisibility(View.GONE);
            spinner_DTR.setVisibility(View.GONE);
            spinner_Pole.setVisibility(View.GONE);

            tv_Sub_33kV.setVisibility(View.VISIBLE);
            tv_Feeder.setVisibility(View.VISIBLE);
            tv_Dtr.setVisibility(View.VISIBLE);
            tv_Pole.setVisibility(View.VISIBLE);

        } else {

            et_ConsumerNumber.setText(StructSurveyConsumerMaster.Consumer_Number);
            et_ConsumerName.setText(StructSurveyConsumerMaster.Name);
            et_MeterNumber.setText(StructSurveyConsumerMaster.Meter_S_No);

            if (StructSurveyConsumerMaster.address1 != null && !StructSurveyConsumerMaster.address1.isEmpty()) {
                et_Address.setText(StructSurveyConsumerMaster.address1 + StructSurveyConsumerMaster.address2);
            }

            et_ConsumerMobile.setText(StructSurveyConsumerMaster.CON_MOB_NO);
//            et_consumer_email.setText(StructSurveyConsumerMaster.CON_EMAIL_ID.trim());
            et_MeterReading.setText(StructSurveyConsumerMaster.Prev_Meter_Reading);
            div_code = StructSurveyConsumerMaster.Division_Code;
            sub_div_code = StructSurveyConsumerMaster.Sub_division_Code;

//            substation_sample.clear();

//            new Substaion_33_11KV(ConsumerSurvey.this).execute();
//            spinner_33_11kv_Substation.setText(substation_sample.get(StructSurveyConsumerMaster.ThiKVFEEDER_NAME));

        }

        databaseHelper = new DB(getApplicationContext());
        SD = databaseHelper.getWritableDatabase();

        String previousSummary = "SELECT Division_Code,Section_Code,THIKVFEEDER_NAME,ELEKVFEEDER_NAME,DTR_NAME,POLE_NAME FROM TBL_CONSUMERSURVEY_UPOLOAD ORDER BY REPORT_DATE DESC LIMIT 1";
        Cursor todoCursor = SD.rawQuery(previousSummary, null);
        if (todoCursor != null && todoCursor.moveToFirst()) {
            do {

                div_code = todoCursor.getString(0);
                sub_div_code = todoCursor.getString(1);
                str_Substation = todoCursor.getString(2);
                feeder_id = todoCursor.getString(3);
                dt_id = todoCursor.getString(4);
                pole_id = todoCursor.getString(5);
            }
            while (todoCursor.moveToNext());
        }


        appcomUtil = new UtilAppCommon();
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
//        key = session.retLicence();
        key = appcomUtil.UniqueCode(getApplicationContext());

//        StructSurveyConsumerUpload.SBM_No = key;
        StructSurveyConsumerUpload.Meter_Reader_Name = session.retMRName();
        StructSurveyConsumerUpload.Meter_Reader_ID = session.retMRID();

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
        button_Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                if (FlowValue.equals("POLE")) {

                    validatePoleAll();

//                    if (!et_consumer_email.getText().toString().equals("") && et_consumer_email.getText().toString().matches(emailPattern)) {
//                        getValuePole();
//                    }
                } else {
                    validateAll();

//                    if (!et_consumer_email.getText().toString().equals("") && et_consumer_email.getText().toString().matches(emailPattern)) {
//                        getValue();
//                    }
//                    else{
//                        Toast.makeText(getApplicationContext(),"Please Enter Valid Email ID",Toast.LENGTH_LONG).show();
//                    }
                }
            }
        });

        new Division(ConsumerSurvey.this).execute();
        spinner_Division.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                if (check) {
//                    int position2 = subdivid_list.indexOf(sub_div_code);
//                    spinner_dc.setSelection(position2);
//                }

                div_code = div_list.get(position).toString();
                division_id = divid_list.get(position).toString();

                if (position > 0) {
                    spinner_dc.setAdapter(null);
                    subdiv_list.clear();
                    subdivid_list.clear();
                    new Sub_Division(ConsumerSurvey.this).execute();
                } else {
                    spinner_dc.setAdapter(null);
                    subdiv_list.clear();
                    subdivid_list.clear();

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_dc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sub_div_code = subdiv_list.get(position).toString();
                sub_division_id = subdivid_list.get(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_ConsumerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (spinner_ConsumerType.getSelectedItem().toString().equalsIgnoreCase("Agriculture")) {

                    linear_connection.setVisibility(View.VISIBLE);
                    linear_load.setVisibility(View.VISIBLE);

                } else {
                    linear_connection.setVisibility(View.GONE);
                    linear_load.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        substation_sample.clear();

        new Substaion_33_11KV(ConsumerSurvey.this).execute();
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
                str_Substation = null;
                str_SubstationId = null;
//
//                if (check) {
//                    int position2 = feeder_list.indexOf(feeder_id);
//                    spinner_Feeder.setSelection(position2);
//                }
                str_Substation = substation_adapter.getItem(position);
                str_SubstationId = substation_sample.get(substation_adapter.getItem(position));

                feeder_list.clear();
                feederid_list.clear();

                new Feeder(ConsumerSurvey.this).execute();
            }
        });
//        spinner_33_11kv_Substation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                str_Substation   =null;
//                str_SubstationId =null;
//
//                str_Substation   = substation_list.get(position).toString();
//                str_SubstationId = substationid_list.get(position).toString();
//
//                if (position > 0) {
//
//                    feeder_list.clear();
//                    feederid_list.clear();
//                    divcode_list.clear();
//
//                    new Feeder(ConsumerSurvey.this).execute();
//
//                } else {
//                    spinner_Feeder.setAdapter(null);
//                    divcode_list.clear();
//                    feeder_list.clear();
//                    feederid_list.clear();
//
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

//        if (check) {
//
//
//            feeder_list.clear();
//            feederid_list.clear();
//            callFeeder();
////            new Feeder(ConsumerSurvey.this).execute();
//                }

        spinner_Feeder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                str_Feeder = null;
                feeder_id = null;
                div_code = null;

                str_Feeder = feeder_list.get(position).toString();
                feeder_id = feederid_list.get(position).toString();
                div_code = divcode_list.get(position).toString();

                if (check) {
                    int position2 = dt_list.indexOf(dt_id);
                    spinner_DTR.setSelection(position2);
                }

                if (position > 0) {
                    spinner_DTR.setAdapter(null);

                    dt_list.clear();
                    dtid_list.clear();

                    new DT(ConsumerSurvey.this).execute();

                } else {
                    spinner_DTR.setAdapter(null);
                    dt_list.clear();
                    dtid_list.clear();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        spinner_DTR.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                str_DTR = null;
                dt_id = null;

                str_DTR = dt_list.get(position).toString();
                dt_id = dtid_list.get(position).toString();

                if (position > 0) {
                    spinner_Pole.setAdapter(null);
                    pole_list.clear();
                    poleid_list.clear();
                    new POLE(ConsumerSurvey.this).execute();

                } else {
                    spinner_Pole.setAdapter(null);
                    pole_list.clear();
                    poleid_list.clear();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        spinner_Pole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                str_Pole = pole_list.get(position).toString();
                pole_id = poleid_list.get(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        new MeterMake(ConsumerSurvey.this).execute();
        spinner_MeterMake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                str_MeterMake = metermake_list.get(position).toString();
                metermake_id = metermakeid_list.get(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        new Neigh_Con(ConsumerSurvey.this).execute();
        spinner_MeterBoxStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (spinner_MeterBoxStatus.getSelectedItem().toString().equalsIgnoreCase("Yes")) {
                    llSealstatus.setVisibility(View.VISIBLE);
                } else {
                    llSealstatus.setVisibility(View.GONE);
                    tv_box_seal_status.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        spinner_Premises.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && position < 4) {
                    tv_NoOfRooms.setVisibility(View.VISIBLE);
                    spinner_No_of_Rooms.setVisibility(View.VISIBLE);
                    tv_NoOfAC.setVisibility(View.VISIBLE);
                    spinner_No_of_AirConditioners.setVisibility(View.VISIBLE);
                    tv_NoOfCoolers.setVisibility(View.VISIBLE);
                    spinner_No_of_Coolers.setVisibility(View.VISIBLE);
                    tv_WaterPump.setVisibility(View.VISIBLE);
                    tv_WaterPumpStatus.setVisibility(View.VISIBLE);
                    chk_WaterPump.setVisibility(View.VISIBLE);
                } else {
                    tv_NoOfRooms.setVisibility(View.GONE);
                    spinner_No_of_Rooms.setVisibility(View.GONE);
                    tv_NoOfAC.setVisibility(View.GONE);
                    spinner_No_of_AirConditioners.setVisibility(View.GONE);
                    tv_NoOfCoolers.setVisibility(View.GONE);
                    spinner_No_of_Coolers.setVisibility(View.GONE);
                    tv_WaterPump.setVisibility(View.GONE);
                    tv_WaterPumpStatus.setVisibility(View.GONE);
                    chk_WaterPump.setVisibility(View.GONE);
                    chk_WaterPump.setChecked(false);
                    tv_WaterPumpStatus.setText("NO");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_MeterCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                if (spinner_MeterCondition.getSelectedItem().toString().equalsIgnoreCase("OK")) {
                    spinner_ActionRequired.setSelection(0);
                } else if (spinner_MeterCondition.getSelectedItem().toString().equalsIgnoreCase("DEFECTIVE METER")) {
                    spinner_ActionRequired.setSelection(2);
                }

                if (spinner_MeterCondition.getSelectedItem().toString().equalsIgnoreCase("NO METER")) {
                    spinner_ActionRequired.setSelection(2);
                    et_MeterNumber.setEnabled(false);
                    IR.setEnabled(false);
                    OPT.setEnabled(false);
                    LPR.setEnabled(false);
                    NA.setEnabled(false);
                    et_MeterReading.setEnabled(false);
                    spinner_MeterMake.setEnabled(false);
                    spinner_ServiceCable.setEnabled(false);
                    chk_com.setChecked(false);
                    chk_com.setEnabled(false);
                    spinner_ActionRequired.setEnabled(false);
                    spinner_MeterBoxStatus.setEnabled(false);

                } else {
                    et_MeterNumber.setEnabled(true);
                    IR.setEnabled(true);
                    OPT.setEnabled(true);
                    LPR.setEnabled(true);
                    NA.setEnabled(true);
                    et_MeterReading.setEnabled(true);
                    chk_com.setChecked(true);
                    chk_com.setEnabled(true);
                    spinner_MeterMake.setEnabled(true);
                    spinner_ActionRequired.setSelection(0);
                    spinner_ServiceCable.setEnabled(true);
                    spinner_ActionRequired.setEnabled(true);
                    spinner_MeterBoxStatus.setEnabled(true);
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
        chk_MeterPhase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    tv_MeterPhaseStatus.setVisibility(View.VISIBLE);
                    tv_MeterPhaseStatus.setText("3-PH");
                } else {
                    tv_MeterPhaseStatus.setVisibility(View.VISIBLE);
                    tv_MeterPhaseStatus.setText("1-PH");
                }
            }
        });
        chk_WaterPump.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    tv_WaterPumpStatus.setVisibility(View.VISIBLE);
                    tv_WaterPumpStatus.setText("YES");
                } else {
                    tv_WaterPumpStatus.setVisibility(View.VISIBLE);
                    tv_WaterPumpStatus.setText("NO");
                }
            }
        });
        chk_com.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    IR.setVisibility(View.VISIBLE);
                    OPT.setVisibility(View.VISIBLE);
                    LPR.setVisibility(View.VISIBLE);

                } else {
                    IR.setVisibility(View.GONE);
                    OPT.setVisibility(View.GONE);
                    LPR.setVisibility(View.GONE);

                    IR.setChecked(false);
                    OPT.setChecked(false);
                    LPR.setChecked(false);

                }
            }
        });
    }

    private void callFeeder() {

        feeder_list.clear();
        feederid_list.clear();

        databaseHelper = new DB(ConsumerSurvey.this);
        SD = databaseHelper.getReadableDatabase();
        databaseHelper = new DB(ConsumerSurvey.this);
        SD = databaseHelper.getReadableDatabase();

        try {
            String feederString = "select distinct FEEDER_CODE,FEEDER_NAME, DIVISION_CODE from TBL_11KVFEEDER_MASTER WHERE A33KVSUBSTATION_CODE='" + str_SubstationId + "'";
            Log.e("CONSUMER ", "FEEDER :" + feederString);
            feeder_cursor = SD.rawQuery(feederString, null);
            if (feeder_cursor != null && feeder_cursor.moveToFirst()) {
                feederid_list.add("Select feeder Name");
                feeder_list.add("Select feeder Name");
                divcode_list.add("Select feeder Name");
                do {

                    String feeder_id = feeder_cursor.getString(0);
                    String feeder_name = feeder_cursor.getString(1);
                    String feeder_DIV = feeder_cursor.getString(2);
                    System.out.println("feeder_id:  " + feeder_id);
                    System.out.println("div id:  " + feeder_DIV);
                    feederid_list.add(feeder_id);
                    feeder_list.add(feeder_name);
                    divcode_list.add(feeder_DIV);
                    feeder_sample.put(feeder_id, feeder_name);

                } while (feeder_cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        feeder_adapter = new ArrayAdapter<String>(
                getApplicationContext(), R.layout.custom_spinner, R.id.textView1, feeder_list);
        spinner_Feeder.setAdapter(feeder_adapter);
//        if (check) {
//            int position = feeder_list.indexOf(feeder_id);
//            spinner_Feeder.setSelection(position);
//        }
    }

    private void callView() {

        linear_connection = (LinearLayout) findViewById(R.id.ll_conn);
        linear_load = (LinearLayout) findViewById(R.id.ll_load);
        button_Continue = (Button) findViewById(R.id.ButtonContreading);
        button_Back = (Button) findViewById(R.id.Buttonback);

        et_ConsumerNumber = (EditText) findViewById(R.id.et_consumer_number);
        et_ConsumerName = (EditText) findViewById(R.id.et_consumer_name);

        et_MeterNumber = (EditText) findViewById(R.id.et_meter_number);
        et_MeterReading = (EditText) findViewById(R.id.et_meter_reading);
        et_Remarks = (EditText) findViewById(R.id.et_remarks);
        et_Address = (EditText) findViewById(R.id.et_consumer_address);
        et_ConsumerMobile = (EditText) findViewById(R.id.et_consumer_mobile);
        et_consumer_email = (EditText) findViewById(R.id.et_consumer_email);
        et_NeighbourConsNo = (Spinner) findViewById(R.id.et_neighbour_con_no);
        et_NeighbourMeterNo = (EditText) findViewById(R.id.et_neighbour_met_no);
        et_Adhar = (EditText) findViewById(R.id.et_consumer_ADHAR);
        et_load = (EditText) findViewById(R.id.conn_load);
        et_Adhar = (EditText) findViewById(R.id.et_consumer_ADHAR);

        et_Remarks.setFilters(new InputFilter[]{filter});
        et_Address.setFilters(new InputFilter[]{filter});
        et_MeterNumber.setFilters(new InputFilter[]{filterSpace});
        et_ConsumerNumber.setFilters(new InputFilter[]{filterSpecial});
//        et_NeighbourConsNo.setFilters(new InputFilter[]{filterSpecial});

        tv_box_seal_status = (TextView) findViewById(R.id.tv_box_seal_status);
        tv_MeterPhaseStatus = (TextView) findViewById(R.id.tv_meter_phase_status);
        tv_NoOfRooms = (TextView) findViewById(R.id.tv_no_of_rooms);
        tv_NoOfRooms.setVisibility(View.GONE);
        tv_NoOfAC = (TextView) findViewById(R.id.tv_air_conditioner);
        tv_NoOfCoolers = (TextView) findViewById(R.id.tv_air_coolers);
        tv_NoOfCoolers.setVisibility(View.GONE);
        tv_WaterPump = (TextView) findViewById(R.id.tv_waterpump_text);
        tv_WaterPump.setVisibility(View.GONE);
        tv_WaterPumpStatus = (TextView) findViewById(R.id.tv_waterpump_status);
        tv_WaterPumpStatus.setVisibility(View.GONE);
        tv_Sub_33kV = (TextView) findViewById(R.id.tv_33_11kv_substation_value);
        tv_Feeder = (TextView) findViewById(R.id.tv_feeder_value);
        tv_Dtr = (TextView) findViewById(R.id.tv_DTR_value);
        tv_Pole = (TextView) findViewById(R.id.tv_pole_value);

//        tv_MeterPhaseStatus.setVisibility(View.GONE);

        spinner_Division = (Spinner) findViewById(R.id.spin_division_value);
        spinner_33_11kv_Substation = (AutoCompleteTextView) findViewById(R.id.spin_33_11kv_substation_value);
        spinner_33_11kv_Substation.setThreshold(1);
        spinner_Feeder = (Spinner) findViewById(R.id.spin_feeder_value);
        spinner_DTR = (Spinner) findViewById(R.id.spin_DTR_value);
        spinner_Pole = (Spinner) findViewById(R.id.spin_pole_value);
        spinner_ConsumerType = (Spinner) findViewById(R.id.spin_consumer_type);
        spinner_MeterMake = (Spinner) findViewById(R.id.spr_meter_make);
        spinner_MeterBoxStatus = (Spinner) findViewById(R.id.spr_box_status);
        spinner_ServiceCable = (Spinner) findViewById(R.id.spr_service_cable_status);
        spinner_ActionRequired = (Spinner) findViewById(R.id.spr_action_required);
        spinner_Remarks = (Spinner) findViewById(R.id.spinner_remarks2);
        spinner_MeterCondition = (Spinner) findViewById(R.id.spr_meter_condition);
        spinner_Premises = (Spinner) findViewById(R.id.spr_consumer_premise);
        spinner_No_of_Rooms = (Spinner) findViewById(R.id.spr_no_of_rooms);
        spinner_No_of_Rooms.setVisibility(View.GONE);
        spinner_No_of_AirConditioners = (Spinner) findViewById(R.id.spr_no_of_air_conditioner);
        spinner_No_of_AirConditioners.setVisibility(View.GONE);
        spinner_No_of_Coolers = (Spinner) findViewById(R.id.spr_no_of_coolers);
        spinner_No_of_Coolers.setVisibility(View.GONE);
        spinner_dc = (Spinner) findViewById(R.id.spin_sub_division_value);
        spinner_MeterCAP = (Spinner) findViewById(R.id.spin_meter_capacity);
        spinner_MeterLOC = (Spinner) findViewById(R.id.spin_meter_location);
        spinner_MeterTYP = (Spinner) findViewById(R.id.spin_meter_type);
        spinner_Caste = (Spinner) findViewById(R.id.spin_consumer_caste);
        spinner_conn_type = (Spinner) findViewById(R.id.spin_consumer_conn_type);

        tr_Feeder = (LinearLayout) findViewById(R.id.feeder_row);
        tr_Dtr = (LinearLayout) findViewById(R.id.fdtr_row);
        tr_Pole = (LinearLayout) findViewById(R.id.pole_row);

        back = (RelativeLayout) findViewById(R.id.relback);
        next = (RelativeLayout) findViewById(R.id.relButton);

        chk_seal = (Switch) findViewById(R.id.chk_seal);
        chk_WaterPump = (Switch) findViewById(R.id.chk_waterpump);
        chk_WaterPump.setVisibility(View.GONE);
        chk_MeterPhase = (Switch) findViewById(R.id.cb_1ph);
        chk_com = (Switch) findViewById(R.id.cb_com_port);

        IR = (CheckBox) findViewById(R.id.chk_irda);
        OPT = (CheckBox) findViewById(R.id.chk_optical);
        LPR = (CheckBox) findViewById(R.id.chk_lpr);
        NA = (CheckBox) findViewById(R.id.chk_na);

        llSealstatus = (LinearLayout) findViewById(R.id.llSealstatus);
        llSealstatus.setVisibility(View.GONE);
        chk_com.setChecked(true);

        int maxLength = 15;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        et_ConsumerNumber.setFilters(fArray);

        int maxLength2 = 500;
        InputFilter[] fArray2 = new InputFilter[1];
        fArray2[0] = new InputFilter.LengthFilter(maxLength2);
        et_Remarks.setFilters(fArray2);
    }

    private void getValue() {

        str_ConsumerNumber = et_ConsumerNumber.getText().toString().trim();
        str_ConsumerName = et_ConsumerName.getText().toString().trim();
        str_ConsumerAddress = et_Address.getText().toString().trim();
        str_ConsumerMobile = et_ConsumerMobile.getText().toString().trim();
        str_ConsumerEmail = et_consumer_email.getText().toString().trim();
        str_MeterNumber = et_MeterNumber.getText().toString().trim();
        str_MeterBoxStatus = spinner_MeterBoxStatus.getSelectedItem().toString().trim();

        StructSurveyConsumerUpload.Consumer_Number = et_ConsumerNumber.getText().toString().trim();
        StructSurveyConsumerUpload.Section_Code = sub_division_id;
        StructSurveyConsumerUpload.Section_Name = sub_div_code;
        StructSurveyConsumerUpload.Name = et_ConsumerName.getText().toString().trim();
        StructSurveyConsumerUpload.address1 = et_Address.getText().toString().trim();
        StructSurveyConsumerUpload.ELEKVFEEDER_NAME = str_Feeder;
        StructSurveyConsumerUpload.DTR_NAME = str_DTR;
        StructSurveyConsumerUpload.POLE_NAME = str_Pole;
        StructSurveyConsumerUpload.Division_Name = div_code;
        StructSurveyConsumerUpload.Division_Code = division_id;
        StructSurveyConsumerUpload.Category = spinner_ConsumerType.getSelectedItem().toString();
        StructSurveyConsumerUpload.CON_MOBILE = et_ConsumerMobile.getText().toString().trim();
        StructSurveyConsumerUpload.CON_EMAIL = et_consumer_email.getText().toString().trim();
        StructSurveyConsumerUpload.REMARK1 = str_Remarks1;
        StructSurveyConsumerUpload.REMARK2 = str_Remarks2;
        StructSurveyConsumerUpload.REPORT_DATE = String.valueOf(GSBilling.getInstance().captureDate1());
        StructSurveyConsumerUpload.NEIGH_CON = et_NeighbourConsNo.getSelectedItem().toString();
        StructSurveyConsumerUpload.PREMISES_TYPE = spinner_Premises.getSelectedItem().toString();
        StructSurveyConsumerUpload.THIKVFEEDER_NAME = str_Substastion_33kV;
        StructSurveyConsumerUpload.METER_COND = str_MeterCondition;
        StructSurveyConsumerUpload.Meter_Phase = tv_MeterPhaseStatus.getText().toString();
        StructSurveyConsumerUpload.ADHAR = et_Adhar.getText().toString();
        StructSurveyConsumerUpload.MET_CAP = spinner_MeterCAP.getSelectedItem().toString();
        StructSurveyConsumerUpload.MET_LOC = spinner_MeterLOC.getSelectedItem().toString();
        StructSurveyConsumerUpload.MET_TYP = spinner_MeterTYP.getSelectedItem().toString();
        StructSurveyConsumerUpload.CON_CASTE = spinner_Caste.getSelectedItem().toString();
        StructSurveyConsumerUpload.CON_CONN_TYPE = spinner_conn_type.getSelectedItem().toString();
        StructSurveyConsumerUpload.Load = et_load.getText().toString();
        StructSurveyConsumerUpload.NO_OF_ROOMS = spinner_No_of_Rooms.getSelectedItem().toString();
        StructSurveyConsumerUpload.NO_OF_AC = spinner_No_of_AirConditioners.getSelectedItem().toString();
        StructSurveyConsumerUpload.WATERPUMP_STS = tv_WaterPumpStatus.getText().toString();
        StructSurveyConsumerUpload.NO_OF_COOLERS = spinner_No_of_Coolers.getSelectedItem().toString();

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(getApplicationContext(), CameraView.class));
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
    }

    private void getValuePole() {

        str_ConsumerNumber = et_ConsumerNumber.getText().toString().trim();
        str_ConsumerName = et_ConsumerName.getText().toString().trim();
        str_ConsumerAddress = et_Address.getText().toString().trim();
        str_ConsumerMobile = et_ConsumerMobile.getText().toString().trim();
        str_ConsumerEmail = et_consumer_email.getText().toString().trim();
        str_MeterNumber = et_MeterNumber.getText().toString().trim();
        str_MeterBoxStatus = spinner_MeterBoxStatus.getSelectedItem().toString().trim();

        StructSurveyConsumerUpload.Consumer_Number = et_ConsumerNumber.getText().toString().trim();
        StructSurveyConsumerUpload.Section_Code = sub_division_id;
        StructSurveyConsumerUpload.Section_Name = sub_div_code;
        StructSurveyConsumerUpload.Name = et_ConsumerName.getText().toString().trim();
        StructSurveyConsumerUpload.address1 = et_Address.getText().toString().trim();
        StructSurveyConsumerUpload.ELEKVFEEDER_NAME = str_FeederSpin;
        StructSurveyConsumerUpload.DTR_NAME = str_DTR_Spin;
        StructSurveyConsumerUpload.POLE_NAME = str_PoleSpin;
        StructSurveyConsumerUpload.Division_Name = div_code;
        StructSurveyConsumerUpload.Division_Code = division_id;
        StructSurveyConsumerUpload.Category = spinner_ConsumerType.getSelectedItem().toString();
        StructSurveyConsumerUpload.CON_MOBILE = et_ConsumerMobile.getText().toString().trim();
        StructSurveyConsumerUpload.CON_EMAIL = et_consumer_email.getText().toString().trim();
        StructSurveyConsumerUpload.REMARK1 = str_Remarks1;
        StructSurveyConsumerUpload.REMARK2 = str_Remarks2;
        StructSurveyConsumerUpload.REPORT_DATE = String.valueOf(GSBilling.getInstance().captureDate1());
        StructSurveyConsumerUpload.NEIGH_CON = et_NeighbourConsNo.getSelectedItem().toString();
        StructSurveyConsumerUpload.PREMISES_TYPE = spinner_Premises.getSelectedItem().toString();
        StructSurveyConsumerUpload.THIKVFEEDER_NAME = str_Substastion_33kV;
        StructSurveyConsumerUpload.Meter_Phase = tv_MeterPhaseStatus.getText().toString();
        StructSurveyConsumerUpload.ADHAR = et_Adhar.getText().toString();
        StructSurveyConsumerUpload.MET_CAP = spinner_MeterCAP.getSelectedItem().toString();
        StructSurveyConsumerUpload.MET_LOC = spinner_MeterLOC.getSelectedItem().toString();
        StructSurveyConsumerUpload.MET_TYP = spinner_MeterTYP.getSelectedItem().toString();
        StructSurveyConsumerUpload.CON_CASTE = spinner_Caste.getSelectedItem().toString();
        StructSurveyConsumerUpload.CON_CONN_TYPE = spinner_conn_type.getSelectedItem().toString();
        StructSurveyConsumerUpload.CONN_LOAD = et_load.getText().toString();
        StructSurveyConsumerUpload.NO_OF_ROOMS = spinner_No_of_Rooms.getSelectedItem().toString();
        StructSurveyConsumerUpload.NO_OF_AC = spinner_No_of_AirConditioners.getSelectedItem().toString();
        StructSurveyConsumerUpload.WATERPUMP_STS = tv_WaterPumpStatus.getText().toString();
        StructSurveyConsumerUpload.NO_OF_COOLERS = spinner_No_of_Coolers.getSelectedItem().toString();

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(getApplicationContext(), CameraView.class));
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
    }

    private void getValueMeter() {

        str_MeterNumber = et_MeterNumber.getText().toString().trim();
        str_MeterBoxStatus = spinner_MeterBoxStatus.getSelectedItem().toString().trim();

        StructSurveyConsumerUpload.Meter_S_No = et_MeterNumber.getText().toString().trim();
        StructSurveyConsumerUpload.Meter_Type = str_MeterComPort;
        StructSurveyConsumerUpload.METER_MAKE = metermake_id;
        StructSurveyConsumerUpload.METER_COND = str_MeterCondition;
        StructSurveyConsumerUpload.METER_SCS = str_ServiceCable;
        StructSurveyConsumerUpload.METER_ARM = str_ActionRequired;
        StructSurveyConsumerUpload.METER_READ = et_MeterReading.getText().toString();
        StructSurveyConsumerUpload.MBS = str_MeterBoxStatus;
        StructSurveyConsumerUpload.MBSS = tv_box_seal_status.getText().toString();
    }

    private void validateAll() {
//        str_Substastion_33kV = null;
//        str_FeederSpin = null;
//        str_DTR_Spin = null;
//        str_PoleSpin = null;
//        str_PoleValue = null;

        if (spinner_33_11kv_Substation != null && spinner_33_11kv_Substation.getText() != null) {
            str_Substastion_33kV = (String) spinner_33_11kv_Substation.getText().toString();
        }
        if (spinner_Feeder != null && spinner_Feeder.getSelectedItem() != null) {
            str_FeederSpin = (String) spinner_Feeder.getSelectedItem();
        }
        if (spinner_DTR != null && spinner_DTR.getSelectedItem() != null) {
            str_DTR_Spin = (String) spinner_DTR.getSelectedItem();
        }
        if (spinner_Pole != null && spinner_Pole.getSelectedItem() != null) {
            str_PoleSpin = (String) spinner_Pole.getSelectedItem();
        }

//            str_Substastion_33kV =spinner_33_11kv_Substation.getSelectedItem().toString();
//            str_FeederSpin=spinner_Feeder.getSelectedItem().toString();
//            str_DTR_Spin=spinner_DTR.getSelectedItem().toString();
//            str_PoleSpin=spinner_Pole.getSelectedItem().toString();


        try {
//            str_PoleValue = spinner_Pole.getSelectedItem().toString();
            if (spinner_Division.getSelectedItem().toString().trim().equals("Select Division")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Division Name")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();

            } else if (spinner_dc.getSelectedItem().toString().trim().equals("Select DC Name")) {
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

            } else if (str_Substastion_33kV.equalsIgnoreCase("") || str_Substastion_33kV.equalsIgnoreCase("Select Sub-Station Name")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Sub Station Name")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();

            } else if (spinner_Feeder.getSelectedItem().toString().equals("Select feeder Name")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Feeder Name")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();

            } else if (spinner_DTR.getSelectedItem().toString().trim().equals("Select DTR Name") || spinner_DTR.getSelectedItem().toString().matches("")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select DTR")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();

            } else if (spinner_Pole.getSelectedItem().toString().equalsIgnoreCase("Select Pole Name") || spinner_Pole.getSelectedItem().toString().equalsIgnoreCase("")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Pole")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();

            } else if (et_ConsumerNumber.getText().toString().trim().equals("")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Enter Consumer Number / IVRS Id")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();
            } else if (et_ConsumerName.getText().toString().trim().equals("")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Enter Consumer Name")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();
            }
//            else if (spinner_Caste.getSelectedItem().toString().trim().equalsIgnoreCase("Select Consumer Caste")) {
//                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//                        .setTitleText("ALERT")
//                        .setContentText("Please Select Consumer Caste")
//                        .setConfirmText("OK")
//                        .showCancelButton(true)
//                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sDialog) {
//
//                                sDialog.cancel();
//                            }
//                        }).show();
//            }
            else if (spinner_MeterTYP.getSelectedItem().toString().trim().equalsIgnoreCase("Select Meter Type")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Meter Type")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();
            } else if (spinner_MeterCAP.getSelectedItem().toString().trim().equalsIgnoreCase("Select Meter Capacity")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Meter Capacity")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();
            } else if (spinner_MeterLOC.getSelectedItem().toString().trim().equalsIgnoreCase("Select Meter Location")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Meter Location")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();
            } else if (et_Address.getText().toString().trim().equals("")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Enter Consumer Address")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();
            } else if (et_ConsumerMobile.getText().toString().trim().equals("")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Enter Mobile Number")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();
            } else if (et_ConsumerMobile.getText().toString().length() < 10) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Mobile number can't be less than 10 digits")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();

                et_ConsumerMobile.requestFocus();
            }  else if (!et_consumer_email.getText().toString().equals("")) {
                if (!et_consumer_email.getText().toString().matches(emailPattern)) {
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("ALERT")
                            .setContentText("Enter valid Email")
                            .setConfirmText("OK")
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {

                                    sDialog.cancel();
                                }
                            }).show();

                    et_consumer_email.requestFocus();
                }
            }  else if (spinner_Premises.getSelectedItem().toString().equalsIgnoreCase("Select Premises Type")) {
//                Toast.makeText(getApplicationContext(), "Please Select Premises", Toast.LENGTH_LONG).show();
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Premises")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();
//                spinner_Premises.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                        if (position ==1 && position !=2 && position!=3){
//                            Toast.makeText(getApplicationContext(), "Please Select No Of Rooms", Toast.LENGTH_LONG).show();
//                        }else if (position ==2 && position !=1 && position!=3){
//                            Toast.makeText(getApplicationContext(), "Please Select No Of Rooms", Toast.LENGTH_LONG).show();
//                        }else if (position ==3 && position !=1 && position!=2){
//                            Toast.makeText(getApplicationContext(), "Please Select No Of Rooms", Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> parent) {
//
//                    }
//                });
//                spinner_No_of_Rooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                        if (position ==1 && position !=2 && position!=3){
//                            Toast.makeText(getApplicationContext(), "Please Select No Of AC", Toast.LENGTH_LONG).show();
//                        }else if (position ==2 && position !=1 && position!=3){
//                            Toast.makeText(getApplicationContext(), "Please Select No Of AC", Toast.LENGTH_LONG).show();
//                        }else if (position ==3 && position !=1 && position!=2){
//                            Toast.makeText(getApplicationContext(), "Please Select No Of AC", Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> parent) {
//
//                    }
//                });
//                if (spinner_No_of_Rooms.getSelectedItem().toString().equalsIgnoreCase("Select No Of Rooms")){
//                    Toast.makeText(getApplicationContext(), "Please Select No Of Rooms", Toast.LENGTH_LONG).show();
//                } else if (spinner_No_of_AirConditioners.getSelectedItem().toString().equalsIgnoreCase("Select No Of AC")){
//                    Toast.makeText(getApplicationContext(), "Please Select No Of AC", Toast.LENGTH_LONG).show();
//                }
//            }
//            else if (spinner_No_of_Rooms.getSelectedItem().toString().equals("Select No Of Rooms")) {
//                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//                        .setTitleText("ALERT")
//                        .setContentText("Please Select No Of Rooms")
//                        .setConfirmText("OK")
//                        .showCancelButton(true)
//                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sDialog) {
//                                sDialog.cancel();
//                            }
//                        }).show();
//            }
//            else if (spinner_No_of_AirConditioners.getSelectedItem().toString().equals("Select No Of AC")) {
//                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//                        .setTitleText("ALERT")
//                        .setContentText("Please Select No Of AC")
//                        .setConfirmText("OK")
//                        .showCancelButton(true)
//                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sDialog) {
//                                sDialog.cancel();
//                            }
//                        }).show();
            }
//            else if (spinner_No_of_Rooms.getSelectedItem().toString().equalsIgnoreCase("Select No Of Rooms")) {
//                Toast.makeText(getApplicationContext(), "Please Select No Of Rooms", Toast.LENGTH_LONG).show();
//            } else if (spinner_No_of_AirConditioners.getSelectedItem().toString().equalsIgnoreCase("Select No Of AC")) {
//                Toast.makeText(getApplicationContext(), "Please Select No Of AC", Toast.LENGTH_LONG).show();
//            }
            else if (spinner_MeterCondition.getSelectedItem().toString().trim().equals("OK")) {
                validateMeterAndCheckBox();
//                getValue();
            } else if (spinner_MeterCondition.getSelectedItem().toString().trim().equals("DEFECTIVE METER")) {
                validateMeterAndCheckBox();
//                getValue();
            } else {

                str_MeterCondition = spinner_MeterCondition.getSelectedItem().toString().trim();
                str_ServiceCable = spinner_ServiceCable.getSelectedItem().toString().trim();
                str_ActionRequired = spinner_ActionRequired.getSelectedItem().toString().trim();
                str_ConsumerType = spinner_ConsumerType.getSelectedItem().toString().trim();
                str_ConsumerEmail = et_consumer_email.getText().toString().trim();
                str_Remarks1 = spinner_Remarks.getSelectedItem().toString().trim();
                str_Remarks2 = et_Remarks.getText().toString().trim();

                getValue();

            }
        } catch (Exception e) {
            e.printStackTrace();
            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("ALERT")
                    .setContentText("Enter All Mandatory Fields")
                    .setConfirmText("OK")
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                        }
                    }).show();
        }
    }

    private void validatePoleAll() {
//        String premisesValue = spinner_Premises.getSelectedItem().toString().trim();
//        Log.e(TAG, "Premises Value" + premisesValue);
        if (FlowValue.equals("POLE")) {

            if (tv_Sub_33kV.getText().length() != 0) {
                str_Substastion_33kV = tv_Sub_33kV.getText().toString();
            }
            if (tv_Feeder.getText().length() != 0) {
                str_FeederSpin = tv_Feeder.getText().toString();
            }
            if (tv_Dtr.getText().length() != 0) {
                str_DTR_Spin = tv_Dtr.getText().toString();
            }
            if (tv_Pole.getText().length() != 0) {
                str_PoleSpin = tv_Pole.getText().toString();
            }
        }

        try {

            if (spinner_Division.getSelectedItem().toString().trim().equals("Select Division")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Division Name")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();

            } else if (spinner_dc.getSelectedItem().toString().trim().equals("Select DC Name")) {
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

            } else if (str_Substastion_33kV.equalsIgnoreCase("") || str_Substastion_33kV.equalsIgnoreCase("Select Sub-Station Name")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Sub Station Name")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();

            } else if (et_ConsumerNumber.getText().toString().trim().equals("")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Enter Consumer Number / IVRS Id")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();

                et_ConsumerNumber.requestFocus();
            }  else if (et_ConsumerName.getText().toString().trim().equals("")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Enter Consumer Name")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();

                et_ConsumerName.requestFocus();
            } else if (spinner_MeterTYP.getSelectedItem().toString().trim().equalsIgnoreCase("Select Meter Type")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Meter Type")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();
            } else if (spinner_MeterCAP.getSelectedItem().toString().trim().equalsIgnoreCase("Select Meter Capacity")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Meter Capacity")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();
            } else if (spinner_MeterLOC.getSelectedItem().toString().trim().equalsIgnoreCase("Select Meter Location")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Meter Location")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();
            } else if (et_Address.getText().toString().trim().equals("")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Enter Consumer Address")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        }).show();

                et_Address.requestFocus();
            } else if (et_ConsumerMobile.getText().toString().trim().equals("")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Enter Mobile Number")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();

                et_ConsumerMobile.requestFocus();
            } else if (et_ConsumerMobile.getText().toString().length() < 10) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Mobile number can't be less than 10 digits")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();

                et_ConsumerMobile.requestFocus();
            }  else if (!et_consumer_email.getText().toString().equals("")) {
                if (!et_consumer_email.getText().toString().matches(emailPattern)) {
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("ALERT")
                            .setContentText("Enter valid Email")
                            .setConfirmText("OK")
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {

                                    sDialog.cancel();
                                }
                            }).show();

                    et_consumer_email.requestFocus();
                }
            }
            else if (spinner_Premises.getSelectedItem().toString().trim().equalsIgnoreCase("Select Premises Type")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Premises")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();
//                if (spinner_No_of_Rooms.getSelectedItem().toString().equalsIgnoreCase("Select No Of Rooms")){
//                    Toast.makeText(getApplicationContext(), "Please Select No Of Rooms", Toast.LENGTH_LONG).show();
//                } else if (spinner_No_of_AirConditioners.getSelectedItem().toString().equalsIgnoreCase("Select No Of AC")){
//                    Toast.makeText(getApplicationContext(), "Please Select No Of AC", Toast.LENGTH_LONG).show();
//                }
            }
            else if (spinner_MeterCondition.getSelectedItem().toString().trim().equals("OK")) {
                validateMeterAndCheckBoxPole();
//                getValue();
            } else if (spinner_MeterCondition.getSelectedItem().toString().trim().equals("DEFECTIVE METER")) {
                validateMeterAndCheckBoxPole();
//                getValue();
            } else {

                str_MeterCondition = spinner_MeterCondition.getSelectedItem().toString().trim();
                str_ServiceCable = spinner_ServiceCable.getSelectedItem().toString().trim();
                str_ActionRequired = spinner_ActionRequired.getSelectedItem().toString().trim();
                str_ConsumerType = spinner_ConsumerType.getSelectedItem().toString().trim();
                str_ConsumerEmail = et_consumer_email.getText().toString().trim();
                str_Remarks1 = spinner_Remarks.getSelectedItem().toString().trim();
                str_Remarks2 = et_Remarks.getText().toString().trim();

                getValuePole();

            }
        } catch (Exception e) {
            e.printStackTrace();
//            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//                    .setTitleText("ALERT")
//                    .setContentText("Enter All Mandatory Fields")
//                    .setConfirmText("OK")
//                    .showCancelButton(true)
//                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                        @Override
//                        public void onClick(SweetAlertDialog sDialog) {
//                            sDialog.cancel();
//                        }
//                    }).show();
            Toast.makeText(getApplicationContext(), "Enter All Mandatory Fileds", Toast.LENGTH_LONG).show();
        }
    }

    private void validateMeterAndCheckBox() {

        if (chk_com.isChecked()) {
            if (IR.isChecked() && !OPT.isChecked() && !LPR.isChecked()) {
                str_MeterComPort = String.valueOf("IRDA");
                Log.e(TAG, "Com Port Value: " + str_MeterComPort);
            } else if (!IR.isChecked() && OPT.isChecked() && !LPR.isChecked()) {
                str_MeterComPort = String.valueOf("OPTICAL");
                Log.e(TAG, "Com Port Value: " + str_MeterComPort);
            } else if (!IR.isChecked() && !OPT.isChecked() && LPR.isChecked()) {
                str_MeterComPort = String.valueOf("LPR");
                Log.e(TAG, "Com Port Value: " + str_MeterComPort);
            } else if (!IR.isChecked() && !OPT.isChecked() && !LPR.isChecked()) {
                str_MeterComPort = String.valueOf("NA");
                Log.e(TAG, "Com Port Value: " + str_MeterComPort);
            } else if (IR.isChecked() && OPT.isChecked() && !LPR.isChecked()) {
                str_MeterComPort = String.valueOf("IRDA_OPTICAL");
                Log.e(TAG, "Com Port Value: " + str_MeterComPort);
            } else if (!IR.isChecked() && OPT.isChecked() && LPR.isChecked()) {
                str_MeterComPort = String.valueOf("OPTICAL_LPR");
                Log.e(TAG, "Com Port Value: " + str_MeterComPort);
            } else if (IR.isChecked() && !OPT.isChecked() && LPR.isChecked()) {
                str_MeterComPort = String.valueOf("IRDA_LPR");
                Log.e(TAG, "Com Port Value: " + str_MeterComPort);
            } else if (IR.isChecked() && OPT.isChecked() && LPR.isChecked()) {
                str_MeterComPort = String.valueOf("IRDA_OPTICAL_LPR");
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
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();

                et_MeterNumber.requestFocus();
            } else if (str_MeterComPort.matches((String)"NoSelection")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Cannot Check All Boxes of Com Port / Check Atleast One Comport")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        }).show();
            } else if (et_MeterReading.getText().toString().trim().equals("")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Enter Meter Reading")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();

                et_MeterReading.requestFocus();
            } else if (spinner_MeterMake.getSelectedItemPosition() == 0) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Meter Make")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();

            } else if (spinner_MeterBoxStatus.getSelectedItemPosition() == 0) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Meter Box Status")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();

            } else {

                str_MeterCondition = spinner_MeterCondition.getSelectedItem().toString().trim();
                str_ServiceCable = spinner_ServiceCable.getSelectedItem().toString().trim();
                str_ActionRequired = spinner_ActionRequired.getSelectedItem().toString().trim();
                str_ConsumerType = spinner_ConsumerType.getSelectedItem().toString().trim();
                str_ConsumerEmail = et_consumer_email.getText().toString().trim();
                str_Remarks1 = spinner_Remarks.getSelectedItem().toString().trim();
                str_Remarks2 = et_Remarks.getText().toString().trim();
                getValueMeter();
                getValue();
            }

        } catch (Exception e) {
            e.printStackTrace();
//            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//                    .setTitleText("ALERT")
//                    .setContentText("Enter All Mandatory Fields")
//                    .setConfirmText("OK")
//                    .showCancelButton(true)
//                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                        @Override
//                        public void onClick(SweetAlertDialog sDialog) {
//                            sDialog.cancel();
//                        }
//                    }).show();
            Toast.makeText(getApplicationContext(), "Enter All Mandatory Fields", Toast.LENGTH_LONG).show();
        }

    }

    private void validateMeterAndCheckBoxPole() {

        if (chk_com.isChecked()) {
            if (IR.isChecked() && !OPT.isChecked() && !LPR.isChecked()) {
                str_MeterComPort = String.valueOf("IRDA");
                Log.e(TAG, "Com Port Value: " + str_MeterComPort);
            } else if (!IR.isChecked() && OPT.isChecked() && !LPR.isChecked()) {
                str_MeterComPort = String.valueOf("OPTICAL");
                Log.e(TAG, "Com Port Value: " + str_MeterComPort);
            } else if (!IR.isChecked() && !OPT.isChecked() && LPR.isChecked()) {
                str_MeterComPort = String.valueOf("LPR");
                Log.e(TAG, "Com Port Value: " + str_MeterComPort);
            } else if (!IR.isChecked() && !OPT.isChecked() && !LPR.isChecked() && NA.isChecked()) {
                str_MeterComPort = String.valueOf("NA");
                Log.e(TAG, "Com Port Value: " + str_MeterComPort);
            } else if (IR.isChecked() && OPT.isChecked() && !LPR.isChecked()) {
                str_MeterComPort = String.valueOf("IRDA_OPTICAL");
                Log.e(TAG, "Com Port Value: " + str_MeterComPort);
            } else if (!IR.isChecked() && OPT.isChecked() && LPR.isChecked()) {
                str_MeterComPort = String.valueOf("OPTICAL_LPR");
                Log.e(TAG, "Com Port Value: " + str_MeterComPort);
            } else if (IR.isChecked() && !OPT.isChecked() && LPR.isChecked()) {
                str_MeterComPort = String.valueOf("IRDA_LPR");
                Log.e(TAG, "Com Port Value: " + str_MeterComPort);
            } else if (IR.isChecked() && OPT.isChecked() && LPR.isChecked()) {
                str_MeterComPort = String.valueOf("IRDA_OPTICAL_LPR");
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
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();

                et_MeterNumber.requestFocus();
            } else if (str_MeterComPort.equalsIgnoreCase("NoSelection")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Cannot Check All Boxes of Com Port")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        }).show();
            } else if (et_MeterReading.getText().toString().trim().equals("")) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Enter Meter Reading")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();

                et_MeterReading.requestFocus();
            } else if (spinner_MeterMake.getSelectedItemPosition() == 0) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Meter Make")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();

            } else if (spinner_MeterBoxStatus.getSelectedItemPosition() == 0) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText("Please Select Meter Box Status")
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();

            } else {

                str_MeterCondition = spinner_MeterCondition.getSelectedItem().toString().trim();
                str_ServiceCable = spinner_ServiceCable.getSelectedItem().toString().trim();
                str_ActionRequired = spinner_ActionRequired.getSelectedItem().toString().trim();
                str_ConsumerType = spinner_ConsumerType.getSelectedItem().toString().trim();
                str_ConsumerEmail = et_consumer_email.getText().toString().trim();
                str_Remarks1 = spinner_Remarks.getSelectedItem().toString().trim();
                str_Remarks2 = et_Remarks.getText().toString().trim();
                getValueMeter();
                getValuePole();
            }

        } catch (Exception e) {
            e.printStackTrace();
//            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//                    .setTitleText("ALERT")
//                    .setContentText("Enter All Mandatory Fields")
//                    .setConfirmText("OK")
//                    .showCancelButton(true)
//                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                        @Override
//                        public void onClick(SweetAlertDialog sDialog) {
//                            sDialog.cancel();
//                        }
//                    }).show();
            Toast.makeText(ConsumerSurvey.this, "Enter All Mandatory Fields...", Toast.LENGTH_SHORT).show();
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
            pd = new ProgressDialog(ConsumerSurvey.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(ConsumerSurvey.this);
            SD = databaseHelper.getReadableDatabase();

            try {

                String divString = "select distinct DIVISION_CODE,DIV_NAME from TBL_DIVISION_MASTER";
//                String divString = "select distinct DIVISION_CODE,DIV_NAME from TBL_DIVISION_MASTER where DIVISION_CODE in (select DIV_CODE from TBL_33KVFEEDER_MASTER where FEEDER_CODE='" + feeder_id + "')";
                System.out.println("nitin: " + divString);
                division_Cursor = SD.rawQuery(divString, null);
                if (division_Cursor != null && division_Cursor.moveToFirst()) {
                    divid_list.add("Select Division ");
                    div_list.add("Select Division ");
                    do {
                        String division_name = division_Cursor.getString(1);
                        System.out.println("division_name: " + division_name);
                        div_list.add(division_name);

                        String division_id = division_Cursor.getString(0);
                        System.out.println("division_id: : " + division_id);
                        divid_list.add(division_id);
                    } while (division_Cursor.moveToNext());
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
                div_adapter = new ArrayAdapter<String>(
                        getApplicationContext(), R.layout.custom_spinner, R.id.textView1, div_list);
                spinner_Division.setAdapter(div_adapter);
                if (check) {
                    int position = divid_list.indexOf(div_code);
                    spinner_Division.setSelection(position);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //...........Division Class...........................................//
    public class Sub_Division extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        Context _context;

        Sub_Division(Context ctx) {
            _context = ctx;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // ShowAlertagain();
            super.onPreExecute();
            pd = new ProgressDialog(ConsumerSurvey.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(ConsumerSurvey.this);
            SD = databaseHelper.getReadableDatabase();

            try {

                String divString = "select distinct SEC_CODE,SEC_NAME from TBL_SECTION_MASTER where CESU_DIV_CODE='" + division_id + "'";
//                String divString = "select distinct DIVISION_CODE,DIV_NAME from TBL_DIVISION_MASTER where DIVISION_CODE in (select DIV_CODE from TBL_33KVFEEDER_MASTER where FEEDER_CODE='" + feeder_id + "')";
                Log.e("CONSUMER", "SUB-DIV : " + divString);
                subdivision_cursor = SD.rawQuery(divString, null);
                if (subdivision_cursor != null && subdivision_cursor.moveToFirst()) {
                    subdivid_list.add("Select DC Name ");
                    subdiv_list.add("Select DC Name ");
                    do {
                        String sub_division_name = subdivision_cursor.getString(1);
                        Log.e("", "DC Name_name: " + sub_division_name);
                        subdiv_list.add(sub_division_name);

                        String sub_division_id = subdivision_cursor.getString(0);
                        Log.e("", "DC Name_id: : " + sub_division_id);
                        subdivid_list.add(sub_division_id);
                    } while (subdivision_cursor.moveToNext());
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
                subdiv_adapter = new ArrayAdapter<String>(
                        getApplicationContext(), R.layout.custom_spinner, R.id.textView1, subdiv_list);
                spinner_dc.setAdapter(subdiv_adapter);

                if (check) {
                    int position = subdivid_list.indexOf(sub_div_code);
                    spinner_dc.setSelection(position);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //........... Sub-Station...........................................//
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
            pd = new ProgressDialog(ConsumerSurvey.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(ConsumerSurvey.this);
            SD = databaseHelper.getReadableDatabase();

            try {
                String poleString = "select distinct SUBSTATION_CODE,SUBSTATION_NAME from TBL_SUB_STATION_MASTER";
                Log.e("CONSUMER ", "SUB-STATION :" + poleString);
                //System.out.println("select distinct POLE_CODE,POLE_TYPE from TBL_POLE_MASTER WHERE FEEDER_CODE ='" + feeder_id + "'");
                substation_cursor = SD.rawQuery(poleString, null);
                if (substation_cursor != null && substation_cursor.moveToFirst()) {
                    substationid_list.add("Select Sub-Station Name");
                    substation_list.add("Select Sub-Station Name");
                    do {

                        String substation_id = substation_cursor.getString(0);
                        System.out.println("SUBSTATION ID : " + substation_id);

                        String substation_name = substation_cursor.getString(1);
                        System.out.println("SUBSTATION NAME: " + substation_name);

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
                if (check) {
//                    String position = substation_sample.get(str_Substation);

                    spinner_33_11kv_Substation.setText(str_Substation);
                    callFeeder();

//                    feeder_list.clear();
//                    feederid_list.clear();


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
            pd = new ProgressDialog(ConsumerSurvey.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(ConsumerSurvey.this);
            SD = databaseHelper.getReadableDatabase();

            try {
                String feederString = "select distinct FEEDER_CODE,FEEDER_NAME, DIVISION_CODE from TBL_11KVFEEDER_MASTER WHERE A33KVSUBSTATION_CODE='" + str_SubstationId + "'";
                Log.e("CONSUMER ", "FEEDER :" + feederString);
                feeder_cursor = SD.rawQuery(feederString, null);
                if (feeder_cursor != null && feeder_cursor.moveToFirst()) {
                    feederid_list.add("Select feeder Name");
                    feeder_list.add("Select feeder Name");
                    divcode_list.add("Select feeder Name");
                    do {

                        String feeder_id = feeder_cursor.getString(0);
                        String feeder_name = feeder_cursor.getString(1);
                        String feeder_DIV = feeder_cursor.getString(2);
                        System.out.println("feeder_id:  " + feeder_id);
                        System.out.println("div id:  " + feeder_DIV);
                        feederid_list.add(feeder_id);
                        feeder_list.add(feeder_name);
                        divcode_list.add(feeder_DIV);
                        feeder_sample.put(feeder_id, feeder_name);

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
                spinner_Feeder.setAdapter(feeder_adapter);
                if (check) {
                    int position = feeder_list.indexOf(feeder_id);
                    spinner_Feeder.setSelection(position);
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
            pd = new ProgressDialog(ConsumerSurvey.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(ConsumerSurvey.this);
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
                        System.out.println("DTR_Name : " + dtr_id);
                        dtid_list.add(dtr_id);
                        dt_list.add(dtr_id + " " + dtr);
                        dt_sample.put(dtr_id, dtr);
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
                spinner_DTR.setAdapter(dt_adapter);
                if (check) {
                    int position = dt_list.indexOf(dt_id);
                    spinner_DTR.setSelection(position);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //...........Division Class...........................................//
    public class POLE extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        Context _context;

        POLE(Context ctx) {
            _context = ctx;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // ShowAlertagain();
            super.onPreExecute();
            pd = new ProgressDialog(ConsumerSurvey.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(ConsumerSurvey.this);
            SD = databaseHelper.getReadableDatabase();

            try {
                String poleString = "select distinct POLE_CODE,POLE_TYPE from TBL_POLE_MASTER WHERE DT_CODE ='" + dt_id + "'";
                // String poleString = "select distinct POLE_CODE,POLE_TYPE from TBL_POLE_UPLOAD  WHERE DT_CODE ='" + dt_id + "'";
                //System.out.println("select distinct POLE_CODE,POLE_TYPE from TBL_POLE_MASTER WHERE FEEDER_CODE ='" + feeder_id + "'");
                pole_cursor = SD.rawQuery(poleString, null);
                if (pole_cursor != null && pole_cursor.moveToFirst()) {
                    poleid_list.add("Select Pole Name");
                    pole_list.add("Select Pole Name");
                    do {

                        String pole_id = pole_cursor.getString(0);
                        String pole_name = pole_cursor.getString(1);

                        System.out.println("pole_id: : " + pole_id);
                        System.out.println("pole_name: " + pole_name);

                        poleid_list.add(pole_id);
                        pole_list.add(pole_name);
                        pole_sample.put(pole_id, pole_name);
                    } while (pole_cursor.moveToNext());
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
                pole_adapter = new ArrayAdapter<String>(
                        getApplicationContext(), R.layout.custom_spinner, R.id.textView1, poleid_list);
                spinner_Pole.setAdapter(pole_adapter);
                if (check) {
                    int position = poleid_list.indexOf(pole_id);
                    spinner_Pole.setSelection(position);
                }
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
            pd = new ProgressDialog(ConsumerSurvey.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(ConsumerSurvey.this);
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

    //------------------Neighbour Consumer--------------------------------//
    public class Neigh_Con extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        Context _context;

        Neigh_Con(Context ctx) {
            _context = ctx;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // ShowAlertagain();
            super.onPreExecute();
            pd = new ProgressDialog(ConsumerSurvey.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(ConsumerSurvey.this);
            SD = databaseHelper.getReadableDatabase();

            try {
                String neighString = "SELECT Consumer_Number FROM TBL_CONSUMERSURVEY_UPOLOAD ORDER BY REPORT_DATE DESC LIMIT 5";
                //System.out.println("select distinct POLE_CODE,POLE_TYPE from TBL_POLE_MASTER WHERE FEEDER_CODE ='" + feeder_id + "'");
                neighcon_cursor = SD.rawQuery(neighString, null);
                if (neighcon_cursor != null && neighcon_cursor.moveToFirst()) {
                    neighconid_list.add("Select Neighbour Consumer");
                    neighcon_list.add("Select Neighbour Consumer");
                    do {

                        String neigh_id = neighcon_cursor.getString(0);
                        System.out.println("meter_id: : " + neigh_id);

                        neighconid_list.add(neigh_id);
                        neighcon_list.add(neigh_id);

                    } while (neighcon_cursor.moveToNext());
                } else {
                    neighconid_list.add("No Neighbour Consumer");
                    neighcon_list.add("No Neighbour Consumer");
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
                neighcon_adapter = new ArrayAdapter<String>(
                        getApplicationContext(), R.layout.custom_spinner, R.id.textView1, neighconid_list);
                et_NeighbourConsNo.setAdapter(neighcon_adapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(getApplicationContext(), SurveyDetails.class));
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);

    }


}
