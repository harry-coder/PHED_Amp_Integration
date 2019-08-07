package com.fedco.mbc.replacement;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.fedco.mbc.R;
import com.fedco.mbc.model.StructMeterReplacment;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.sqlite.DB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class FragmentWithTwoImages extends Fragment {
    private String title;
    private int imageMain;
    private int imageSecondary;

    TextView etintsallDate;

    Button btnInstallDate;
    private EditText et_meter_no, et_mtcseal, OSOBSValues, OSIBSValue, OSOSValue, OSMBRSValue, et_meter_body_seal, et_fr, et_meter_seal;
    private Spinner spin_meter_make_value, spin_meter_type, spin_meter_phase, spin_meter_capacity, spin_metre_brand;

    String str_meterno, str_mtcseal, str_OSOBSValues, str_OSIBSValue, str_OSOSValue, str_OSMBRSValue, str_meterbodyseal, str_fr, str_meterseal, str_metermakevalue, str_metertype, str_meterphase, str_metercapacity, str_meterbrand;

    DB databaseHelper;
    DB dbHelper, dbHelper2, dbHelper4;
    SQLiteDatabase SD, SD2, SD3;
    Cursor phase;

    Cursor division_Cursor, substation_cursor, subdivision_cursor, feeder_cursor, dt_cursor, pole_cursor, metermake_cursor, neighcon_cursor;
    ArrayList<String> consumerList, subdiv_list, subdivid_list, div_list, divid_list, substation_list, substationid_list, feeder_list, feederid_list, dt_list, dtid_list,
            pole_list, poleid_list, metermake_list, metermakeid_list, neighcon_list, neighconid_list, divcode_list;
    ArrayAdapter<String> div_adapter, subdiv_adapter, substation_adapter, feeder_adapter, dt_adapter, pole_adapter, metermake_adapter, neighcon_adapter;

    ArrayList<String> phase_code_list;
    ArrayAdapter<String> prePhasecode_adapter;


    public static FragmentWithTwoImages newInstance(String title, int resMainImage) {
        FragmentWithTwoImages fragment = new FragmentWithTwoImages();
        Bundle args = new Bundle();
        args.putInt("imageMain", resMainImage);
//        args.putInt("imageSecondary", resSecondaryImage);
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageMain = getArguments().getInt("imageMain", 0);
        imageSecondary = getArguments().getInt("imageSecondary", 0);
        title = getArguments().getString("title");

    }


    /*private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        *//**
         * Set Up Current Date Into dialog
         *//*
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        *//**
         * Set Call back to capture selected date
         *//*
        date.setCallBack(ondate);
        date.show(getFragmentManager(), "Date Picker");
    }*/

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            etintsallDate.setText(String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear + 1)
                    + "-" + String.valueOf(year));
            StructMeterReplacment.REPLACEMENT_DATE = String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear + 1)
                    + "-" + String.valueOf(year);
            Calendar c = Calendar.getInstance();
            SimpleDateFormat fds = new SimpleDateFormat("kk:mm:ss");
            StructMeterReplacment.REPLACEMENT_TIME = fds.format(c.getTime());

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two_images, container, false);
//        btnInstallDate = (Button) view.findViewById(R.id.BTNDATE);
//        etintsallDate = (TextView) view.findViewById(R.id.ETDate);
        et_meter_no = (EditText) view.findViewById(R.id.et_meter_no);
        et_meter_body_seal = (EditText) view.findViewById(R.id.et_meter_body_seal);
        et_fr = (EditText) view.findViewById(R.id.et_fr);
        et_meter_seal = (EditText) view.findViewById(R.id.et_meter_seal);
        OSIBSValue = (EditText) view.findViewById(R.id.OSIBSValue);
        OSMBRSValue = (EditText) view.findViewById(R.id.OSMBRSValue);
        OSOBSValues = (EditText) view.findViewById(R.id.OSOBSValues);
        OSOSValue = (EditText) view.findViewById(R.id.OSOSValue);

        spin_meter_make_value = (Spinner) view.findViewById(R.id.spin_meter_make_value);
        spin_meter_type = (Spinner) view.findViewById(R.id.spin_meter_type);
        spin_meter_phase = (Spinner) view.findViewById(R.id.spin_meter_phase);
        spin_meter_capacity = (Spinner) view.findViewById(R.id.spin_meter_capacity);
//        spin_metre_brand = (Spinner) view.findViewById(R.id.spin_metre_brand);

        metermakeid_list = new ArrayList<>();
        metermake_list = new ArrayList<>();

        new MeterMake(MainActivity.getsActivity()).execute();


//        btnInstallDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                showDatePicker();
//
//
//            }
//        });

        et_meter_no.addTextChangedListener(watch);
        et_meter_body_seal.addTextChangedListener(watch);
        et_fr.addTextChangedListener(watch);
        et_meter_seal.addTextChangedListener(watch);
        OSIBSValue.addTextChangedListener(watch);
        OSMBRSValue.addTextChangedListener(watch);
        OSOBSValues.addTextChangedListener(watch);
        OSOSValue.addTextChangedListener(watch);

        spin_meter_make_value.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub


                if (position > 0) {

                    StructMeterReplacment.OLD_METER_MAKE = spin_meter_make_value.getSelectedItem().toString();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        spin_meter_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub


                if (position > 0) {

                    StructMeterReplacment.OLD_METER_TYPE = spin_meter_type.getSelectedItem().toString();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        spin_meter_phase.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub


                if (position > 0) {

                    StructMeterReplacment.OLD_METER_PHASE = spin_meter_phase.getSelectedItem().toString();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        spin_meter_capacity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub


                if (position > 0) {

                    StructMeterReplacment.OLD_METER_CAPACITY = spin_meter_capacity.getSelectedItem().toString();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


        return view;
    }

    public class Pre_Phase_Code extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        Context _context;

        Pre_Phase_Code(Context ctx) {
            _context = ctx;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // ShowAlertagain();
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.getsActivity());
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(MainActivity.getsActivity());
            SD = databaseHelper.getReadableDatabase();

            try {
                String poleString = "SELECT  PHASE_CD FROM TBL_CONSMAST WHERE Consumer_Number ='" + Structconsmas.Consumer_Number + "'";
                //System.out.println("select distinct POLE_CODE,POLE_TYPE from TBL_POLE_MASTER WHERE FEEDER_CODE ='" + feeder_id + "'");
                phase = SD.rawQuery(poleString, null);

                if (phase != null && phase.moveToFirst()) {

                    phase_code_list.add("Select phase code");
                    phase_code_list.add("PC");
                    do {

                        String pre_phase_code = phase.getString(0);
                        System.out.println("pre phase code : " + pre_phase_code);

                        phase_code_list.add(pre_phase_code);

                    } while (phase.moveToNext());
                } else {

                    phase_code_list.add("Select pre phase code");
                    phase_code_list.add("PC");
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

                prePhasecode_adapter = new ArrayAdapter<String>(
                        MainActivity.getsActivity(), R.layout.custom_spinner, R.id.textView1, phase_code_list);

                spin_meter_phase.setAdapter(prePhasecode_adapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    TextWatcher watch = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int a, int b, int c) {
            // TODO Auto-generated method stub

//            output.setText(s);
//            if(a == 9){
//                Toast.makeText(getApplicationContext(), "Maximum Limit Reached", Toast.LENGTH_SHORT).show();
//            }
//            str_meterno = et_meter_no.getText().toString().trim();
//            str_OSIBSValue = OSIBSValue.getText().toString().trim();
//            str_OSMBRSValue = OSMBRSValue.getText().toString().trim();
//            str_OSOBSValues = OSOBSValues.getText().toString().trim();
//            str_OSOSValue = OSOSValue.getText().toString().trim();
//            str_meterbodyseal = et_meter_body_seal.getText().toString().trim();
//            str_fr = et_fr.getText().toString().trim();
//            str_meterseal = et_meter_seal.getText().toString().trim();
//            str_mtcseal = et_mtcseal.getText().toString().trim();

            StructMeterReplacment.OLD_METER_NO = et_meter_no.getText().toString().trim();
            StructMeterReplacment.OLD_METER_OUTERBOX_SEAL = OSOBSValues.getText().toString().trim();
            StructMeterReplacment.OLD_METER_INNERBOX_SEAL = OSIBSValue.getText().toString().trim();
            StructMeterReplacment.OLD_METER_OPTICAL_SEAL = OSOSValue.getText().toString().trim();
            StructMeterReplacment.OLD_METER_MDRESET_BUTTON_SEAL = OSMBRSValue.getText().toString().trim();
            StructMeterReplacment.OLD_METER_BODY_SEAL = et_meter_body_seal.getText().toString().trim();
            StructMeterReplacment.OLD_METER_FINAL_READING = et_fr.getText().toString().trim();


        }
    };


//    private void getValue() {
//
//
//        str_meterno = et_meter_no.getText().toString().trim();
//        str_OSIBSValue = OSIBSValue.getText().toString().trim();
//        str_OSMBRSValue = OSMBRSValue.getText().toString().trim();
//        str_OSOBSValues = OSOBSValues.getText().toString().trim();
//        str_OSOSValue = OSOSValue.getText().toString().trim();
//        str_meterbodyseal = et_meter_body_seal.getText().toString().trim();
//        str_fr = et_fr.getText().toString().trim();
//        str_meterseal = et_meter_seal.getText().toString().trim();
//      str_mtcseal = et_mtcseal.getText().toString().trim();
//
//
//
//
//        StructMeterReplacment.OLD_METER_NO = et_meter_no.getText().toString().trim();
//        StructMeterReplacment.OLD_METER_OUTERBOX_SEAL = OSOBSValues.getText().toString().trim();
//        StructMeterReplacment.OLD_METER_INNERBOX_SEAL = OSIBSValue.getText().toString().trim();
//        StructMeterReplacment.OLD_METER_OPTICAL_SEAL = OSOSValue.getText().toString().trim();
//        StructMeterReplacment.OLD_METER_MDRESET_BUTTON_SEAL = OSMBRSValue.getText().toString().trim();
//        StructMeterReplacment.OLD_METER_BODY_SEAL = et_meter_body_seal.getText().toString().trim();
//        StructMeterReplacment.OLD_METER_FINAL_READING = et_fr.getText().toString().trim();
//
//        StructMeterReplacment.OLD_METER_MAKE = spin_meter_make_value.getSelectedItem().toString();
//        StructMeterReplacment.OLD_METER_TYPE = spin_meter_type.getSelectedItem().toString();
//        StructMeterReplacment.OLD_METER_PHASE = spin_meter_phase.getSelectedItem().toString();
//        StructMeterReplacment.OLD_METER_CAPACITY = spin_meter_capacity.getSelectedItem().toString();
//
//
//    }

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
            pd = new ProgressDialog(MainActivity.getsActivity());
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(MainActivity.getsActivity());
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
                        MainActivity.getsActivity(), R.layout.custom_spinner, R.id.textView1, metermake_list);
                spin_meter_make_value.setAdapter(metermake_adapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}