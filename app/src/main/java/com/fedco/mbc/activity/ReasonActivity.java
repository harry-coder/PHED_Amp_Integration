package com.fedco.mbc.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.billinglogic.CBillling;
import com.fedco.mbc.model.Structbilling;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.UtilAppCommon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ReasonActivity extends Activity {

    SQLiteDatabase SD;
    DB databaseHelper;
    UtilAppCommon appCom;
    CBillling calBill;

    Cursor dt_number_cursor, metermake_cursor;
    ArrayList<String> dtnumber_id_list, dt_number_list, metermakeid_list, metermake_list;
    ArrayAdapter dtNumber_adapter, metermake_adapter;

    Spinner spinner_reason_name;
    TextView textview_dtr_name;
    EditText et_act_reading,et_meter_no;
    LinearLayout ll_reading,ll_reading2;

    String reason, reason_id;

    Button btn_OK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reason);

        spinner_reason_name = (Spinner) findViewById(R.id.spr_reasons);
        textview_dtr_name = (TextView) findViewById(R.id.tv_reasons);
        et_act_reading = (EditText) findViewById(R.id.et_reading);
        et_meter_no = (EditText) findViewById(R.id.et_mtrno);
        ll_reading = (LinearLayout) findViewById(R.id.ll_reading);
        ll_reading2 = (LinearLayout) findViewById(R.id.ll_reading2);

        dtnumber_id_list = new ArrayList<String>();
        dt_number_list = new ArrayList<String>();

        metermakeid_list = new ArrayList<String>();
        metermake_list = new ArrayList<String>();

        databaseHelper = new DB(getApplicationContext());
        SD = databaseHelper.getWritableDatabase();

        if (Structbilling.Bill_Basis.equalsIgnoreCase("E") || GSBilling.getInstance().getNormalReason()==1) {
            ll_reading.setVisibility(View.GONE);
        }
        if(GSBilling.getInstance().getDbNotChng()== 1){
            ll_reading2.setVisibility(View.VISIBLE);
        }

        new MeterMake(ReasonActivity.this).execute();

        spinner_reason_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                reason = metermake_list.get ( position );
                reason_id = metermakeid_list.get ( position );


//                if (reason.equalsIgnoreCase("Previous reading is higher than Current reading")) {
//                    Structbilling.Reasons = reason;
//
//                    if (et_act_reading.getText().toString() != null && !et_act_reading.getText().toString().isEmpty()) {
//                        Structbilling.ACTUAL_READING = et_act_reading.getText().toString();
//                    }
//
//
//
//
//                } else
                if (position > 0) {
                    Structbilling.Reasons = reason;

                    if (et_act_reading.getText().toString() != null && !et_act_reading.getText().toString().isEmpty() || et_meter_no.getText().toString() != null && !et_meter_no.getText().toString().isEmpty() ) {
                        Structbilling.ACTUAL_READING = et_act_reading.getText().toString();
                        Structbilling.MTR_NO= et_meter_no.getText().toString();
                    }

                    if (reason.equalsIgnoreCase("Previous reading is higher than Current reading")) {
                        GSBilling.getInstance().setPowerFactor(Double.parseDouble("0"));
                        GSBilling.getInstance().setUnitMaxDemand("KW");
                        GSBilling.getInstance().setPowerFactor(Double.parseDouble("0.8"));
                        Structbilling.Avrg_PF = String.valueOf(GSBilling.getInstance().getPowerFactor());

                        GSBilling.getInstance().setCurmeter(4);

                        databaseHelper = new DB(getApplicationContext());
                        SD = databaseHelper.getWritableDatabase();
                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        appCom = new UtilAppCommon();
                        String curmeterreading = "0";
                        calBill = new CBillling();
                        Long dateDuration = null;
                        int calculatedunit = 0;

                        Date varDate = null;
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        String DTime = sdf.format(new Date());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                        try {
                            varDate = dateFormat.parse(Structconsmas.Prev_Meter_Reading_Date);

                            String billISDate = Structconsmas.BILL_ISSUE_DATE.substring(0, 4);
                            String billISDate2 = Structconsmas.BILL_ISSUE_DATE.substring(4, 6);
                            String billISDate3 = Structconsmas.BILL_ISSUE_DATE.substring(6, 8);

                            dateFormat = new SimpleDateFormat("dd-MM-yyyy");//22-04-0017
                            System.out.println("Date :" + dateFormat.format(varDate));
                            String date1 = dateFormat.format(varDate).substring(0, 6);
                            String date2 = dateFormat.format(varDate).substring(8, 10);

                            String finadate = date1 + "20" + date2;
                            System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDA :" + finadate);

                            if (Structbilling.dateDuration == 0) {
                                Structbilling.dateDuration = 30l;
                            }
                            dateDuration = Structbilling.dateDuration;
                            calBill.totalDateduration = dateDuration;
                            System.out.println("DATE DURATION  :" + calBill.totalDateduration);
                            System.out.println("DATE DURATION 2  :" + dateDuration);
                        } catch (Exception e) {
                            // TODO: handle exception
                            e.printStackTrace();
                        }

                        calBill.curMeterRead = curmeterreading;
                        calBill.curMeterReadDate = DTime;
                        // System.out.println("CUR READ DATE :" + Structconsmas.CUR_READ_DATE);

                        calBill.curMeterStatus = 4;
                        calBill.derivedMeterStatus = "4";

                        Structbilling.Derived_mtr_status = "2";
                        Structbilling.Cur_Meter_Stat = 4;

                        calculatedunit = calBill.Unitcalculation(Structbilling.Derived_mtr_status, curmeterreading, Structbilling.Cur_Meter_Stat);
                        calBill.unit = calculatedunit;
                        calBill.CalculateBill();

                        appCom.copyResultsetToBillingClass(calBill);

                        if (appCom.consump_CHK()) {
                            new SweetAlertDialog(ReasonActivity.this, SweetAlertDialog.WARNING_TYPE)

                                    .setTitleText("Billing error")
                                    .setContentText("Consumption is very high")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();

                                            UtilAppCommon ucom = new UtilAppCommon();
                                            ucom.nullyfimodelCon();
                                            ucom.nullyfimodelBill();

//                                            Toast.makeText(ReasonActivity.this, "HY", Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(MeterState.this, BillingtypesActivity.class);
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                        startActivity(intent);
//                                        overridePendingTransition(R.anim.anim_slide_in_left,
//                                                R.anim.anim_slide_out_left);

                                        }
                                    })
                                    .show();
                        } else {

                            Structbilling.Bill_Basis = "R";
                            Structbilling.BILL_TYP_CD = "4";

                            Structbilling.MTR_STAT_TYP = "56";
                            Structbilling.RDG_TYP_CD = "1";

                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName(getApplicationContext(), BillingViewActivity
                                    .class));
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);

                        }
                    } else {

                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(getApplicationContext(), BillingViewActivity
                                .class));
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left);

                    }


                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

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
            pd = new ProgressDialog(ReasonActivity.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(ReasonActivity.this);
            SD = databaseHelper.getReadableDatabase();

            try {
//                String poleString = "SELECT SRNO,REMARK_DESC from TBL_REMARKS_MASTER WHERE BILL_BASIS='" + Structbilling.Bill_Basis + "'";
                String poleString = "SELECT SRNO,REMARK_DESC from TBL_REMARKS_MASTER WHERE BILL_BASIS='ACT'";
                //System.out.println("select distinct POLE_CODE,POLE_TYPE from TBL_POLE_MASTER WHERE FEEDER_CODE ='" + feeder_id + "'");
                metermake_cursor = SD.rawQuery(poleString, null);
                if (metermake_cursor != null && metermake_cursor.moveToFirst()) {
                    metermakeid_list.add("Select Reason ");
                    metermake_list.add("Select Reason");
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
                spinner_reason_name.setAdapter(metermake_adapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onBackPressed() {
//        if (exit) {
        GSBilling.getInstance().clearData();
        finish(); // finish activity
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);
//        UtilAppCommon.nullyfimodelBill();
//        Intent intent = new Intent(getApplicationContext(), MeterState.class);
////         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        overridePendingTransition(R.anim.anim_slide_in_left,
//                R.anim.anim_slide_out_left);

//        finish(); // finish activity
//        this.overridePendingTransition(R.anim.anim_slide_in_right,
//                R.anim.anim_slide_out_right);
//        overridePendingTransition(R.anim.anim_slide_in_right,
//                R.anim.anim_slide_out_right);
//
//        Debug.stopMethodTracing();
//        super.onDestroy();
//        } else {
//            Toast.makeText(this, "Press Back again to Exit.",Toast.LENGTH_SHORT).show();
//            exit = true;
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    exit = false;
//                }
//            }, 3 * 1000);
//        }


    }
}
