package com.fedco.mbc.activity;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.fedco.mbc.R;
import com.fedco.mbc.billinglogic.CBillling;
import com.fedco.mbc.bluetoothprinting.GlobalPool;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.model.Structbilling;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.model.Structtariff;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.GPSTracker;
import com.fedco.mbc.utils.UtilAppCommon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MeterState extends ListActivity implements LogoutListaner {

    SQLiteDatabase SD;
    DB dbHelper;
    ArrayList<String> values;
    private ArrayList<String> results = new ArrayList<String>();
    GPSTracker gps;
    double latitude;
    double longitude;
    String curmeterreading, curDateTime;
    int curMeterStat;
    UtilAppCommon appCom;
    CBillling calBill;
    Logger Log;
    EditText et_Remark;
    private static MeterState sActivity;
    public static MeterState getsActivity() {
        return sActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_state);
        setTitle("READING STATUS");
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        this.curDateTime = sdf.format(new Date());
        sActivity = this;
        et_Remark=(EditText)findViewById(R.id.et_remark) ;
        GSBilling.getInstance().clearData();
        GSBilling.getInstance().setMeterChange("");
        GSBilling.getInstance().setConsumptionchkhigh("");

//        if(Structtariff.TARIFF_CODE.equalsIgnoreCase("110") ||Structtariff.TARIFF_CODE.equalsIgnoreCase("111") ||Structtariff.TARIFF_CODE.equalsIgnoreCase("113")  ){
//
//            dbHelper = new DB(getApplicationContext());
//            SD = dbHelper.getWritableDatabase();
//            appCom=  new UtilAppCommon();
//
//            String curmeterreading = "0";
//
//            calBill = new CBillling();
//
//            Long dateDuration = null;
//            int calculatedunit = 0;
//
//            Date varDate = null;
//            SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
//            String DTime = sdf2.format(new Date());
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
//
//            try {
//                varDate = dateFormat.parse(Structconsmas.Prev_Meter_Reading_Date);
//                dateFormat = new SimpleDateFormat("dd-MM-yyyy");//22-04-0017
//
//                String date1 = dateFormat.format(varDate).substring(0, 6);
//                String date2 = dateFormat.format(varDate).substring(8, 10);
//
//                String finadate = date1 + "20" + date2;
//                if(Structbilling.dateDuration == 0){
//                    Structbilling.dateDuration=30l;
//                }
//                dateDuration = Structbilling.dateDuration;
//
//                calBill.totalDateduration = dateDuration;
//
//            } catch (Exception e) {
//                // TODO: handle exception
//                e.printStackTrace();
//            }
//
//            calBill.curMeterRead = curmeterreading;
//            calBill.curMeterReadDate =DTime;
//
//            calBill.curMeterStatus = 0;
//            calBill.derivedMeterStatus = "0";
//
//            Structbilling.Derived_mtr_status = "0";
//            Structbilling.Cur_Meter_Stat = 0;
//
//            calculatedunit = calBill.Unitcalculation(Structbilling.Derived_mtr_status, "0", Integer.parseInt(Structconsmas.RDG_TYP_CD));
//            calBill.unit = calculatedunit;
//            calBill.CalculateBill();
//
//            appCom.copyResultsetToBillingClass(calBill);
//            GSBilling.getInstance().setCurmeter(0);
//
//            if (appCom.consump_CHK()) {
//                new SweetAlertDialog(MeterState.this, SweetAlertDialog.WARNING_TYPE)
//
//                        .setTitleText("Billing error")
//                        .setContentText("Consumption is very high")
//                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sDialog) {
//                                sDialog.dismissWithAnimation();
//
//                                UtilAppCommon ucom = new UtilAppCommon();
//                                ucom.nullyfimodelCon();
//                                ucom.nullyfimodelBill();
//
//
//                                Intent intent = new Intent(MeterState.this, BillingtypesActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//                                startActivity(intent);
//                                overridePendingTransition(R.anim.anim_slide_in_left,
//                                        R.anim.anim_slide_out_left);
//                            }
//                        })
//                        .show();
//            } else {
//                Intent intent = new Intent(MeterState.this, ReasonActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.anim_slide_in_left,
//                        R.anim.anim_slide_out_left);
//
//            }
////            Intent intent = new Intent(MeterState.this, BillingViewActivity.class);
//
//        }
        ((GlobalPool)getApplication()).registerSessionListaner(this);
        ((GlobalPool)getApplication()).startUserSession();
        openAndQueryDatabase();

        displayResultList();

    }

    private void displayResultList() {

        setListAdapter(new ArrayAdapter<String>(this,
                R.layout.list_view, results));

    }

    private void openAndQueryDatabase() {
        try {
            dbHelper = new DB(getApplicationContext());
            SD = dbHelper.getWritableDatabase();
            Cursor c = SD.rawQuery("SELECT STATUS FROM TBL_METERSTATUSCODE WHERE STATUS IN ('READ','ESTIMATE','OVERFLOW')", null);

            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        String status = c.getString(c.getColumnIndex("STATUS"));
                        Log.e(getApplicationContext(), "MeterStateAct", "STATSUS IS " + status);
//                        int age = c.getInt(c.getColumnIndex("Age"));
                        results.add(status);
                    } while (c.moveToNext());
                }
            }
        } catch (SQLiteException se) {
            Log.e(getApplicationContext(), "MeterStatAct", "Could not create or Open the database");
        } finally {
//            if (newDB != null)
//                newDB.execSQL("DELETE FROM " + tableName);
            SD.close();
        }

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);

        // ListView Clicked item index
        int itemPosition = position;

        // ListView Clicked item value
        String itemValue = (String) l.getItemAtPosition(position);

        Log.e(getApplicationContext(), "MeterStatAct", "POSION OF VALUE ::" + itemValue);
        Log.e(getApplicationContext(), "MeterStatAct", "POSION OF VALUE ::" + position);
        Log.e(getApplicationContext(), "MeterStatAct", "POSION OF VALUE ::" + Structconsmas.Prev_Meter_Status);
        //actual
        boolean normal = Structconsmas.Prev_Meter_Status.equals(new String("1"));
        boolean estimate = Structconsmas.Prev_Meter_Status.equals(new String("2"));
        boolean direct = Structconsmas.Prev_Meter_Status.equals(new String("3"));
        //houselocked
        boolean overflow = Structconsmas.Prev_Meter_Status.equals(new String("4"));
        boolean meterreplacement = Structconsmas.Prev_Meter_Status.equals(new String("5"));
        //average
        boolean negread = Structconsmas.Prev_Meter_Status.equals(new String("6"));
        boolean mtrdef = Structconsmas.Prev_Meter_Status.equals(new String("7"));
        boolean sitntrace = Structconsmas.Prev_Meter_Status.equals(new String("9"));
        //nometer
        boolean nomtr = Structconsmas.Prev_Meter_Status.equals(new String("8"));

        dbHelper = new DB(getApplicationContext());
        SD = dbHelper.getWritableDatabase();
        GSBilling.getInstance().setCurmeter(0);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        switch (itemPosition) {
            case 0://NORAML ACT(1)//MP NOARMAL

                if (et_Remark.getText().toString().matches("")){
                    Structbilling.Remarks="";
                }else{
                    String remark=et_Remark.getText().toString().replace("\n","");
                    Structbilling.Remarks=remark;
                }

                Structbilling.Reasons="";
                Intent intentnormal = new Intent(getApplicationContext(), Readinginput.class);
                intentnormal.putExtra("Value", itemValue);
                //intent.putExtra("Position", 1);
                GSBilling.getInstance().setCurmeter(1);
                startActivity(intentnormal);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);

                System.out.println("" + Structconsmas.Prev_Meter_Status);

                break;
            case 1://// MP PFL
            {

                GSBilling.getInstance().setMaxDemand(Double.parseDouble("0"));
                GSBilling.getInstance().setUnitMaxDemand("KW");
                GSBilling.getInstance().setPowerFactor(Double.parseDouble("0.8"));
                Structbilling.Avrg_PF = String.valueOf(GSBilling.getInstance().getPowerFactor());


                dbHelper = new DB(getApplicationContext());
                SD = dbHelper.getWritableDatabase();
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                appCom=  new UtilAppCommon();
                String curmeterreading = "0";
//                String curmeterreading = Structconsmas.ACC_MIN_UNITS;
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
                    if(Structbilling.dateDuration == 0){
                        Structbilling.dateDuration=30l;
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
                calBill.curMeterReadDate =DTime;
//                System.out.println("CUR READ DATE :" + Structconsmas.CUR_READ_DATE);

                calBill.curMeterStatus = 2;
                calBill.derivedMeterStatus = "2";

                Structbilling.Derived_mtr_status = "2";
                Structbilling.Cur_Meter_Stat = 2;

                calculatedunit = calBill.Unitcalculation(Structbilling.Derived_mtr_status, curmeterreading, Structbilling.Cur_Meter_Stat);
                calBill.unit = calculatedunit;
                calBill.CalculateBill();

                appCom.copyResultsetToBillingClass(calBill);
                GSBilling.getInstance().setCurmeter(2);

                if (appCom.consump_CHK()) {
                    new SweetAlertDialog(MeterState.this, SweetAlertDialog.WARNING_TYPE)

                            .setTitleText("Billing error")
                            .setContentText("Consumption is very high")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();

                                    UtilAppCommon ucom = new UtilAppCommon();
                                    ucom.nullyfimodelCon();
                                    ucom.nullyfimodelBill();

                                    Intent intent = new Intent(MeterState.this, BillingtypesActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                    startActivity(intent);
                                    overridePendingTransition(R.anim.anim_slide_in_left,
                                            R.anim.anim_slide_out_left);
                                }
                            })
                            .show();
                } else {
                    Intent intent = new Intent(MeterState.this, ReasonActivity.class);
                    GSBilling.getInstance().setNormalReason(0);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);
                }

//                Intent intent = new Intent(MeterState.this, BillingViewActivity.class);

                break;
            }

            case 2:// MP ACCESSED UNIT
            {
                if (et_Remark.getText().toString().matches("")){
                    Structbilling.Remarks="";
                }else{
                    String remark=et_Remark.getText().toString().replace("\n","");
                    Structbilling.Remarks=remark;
                }

                Structbilling.Reasons="";
                Intent intentoverflow = new Intent(getApplicationContext(), Readinginput.class);
                intentoverflow.putExtra("Value", itemValue);
                //intent.putExtra("Position", 1);
                GSBilling.getInstance().setCurmeter(1);
                startActivity(intentoverflow);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);

                System.out.println("" + Structconsmas.Prev_Meter_Status);
//                GSBilling.getInstance().setPowerFactor(Double.parseDouble("0"));
//                GSBilling.getInstance().setUnitMaxDemand("KW");
//                GSBilling.getInstance().setPowerFactor(Double.parseDouble("0.8"));
//                Structbilling.Avrg_PF = String.valueOf(GSBilling.getInstance().getPowerFactor());
//
//                GSBilling.getInstance().setCurmeter(4);
//
//                dbHelper = new DB(getApplicationContext());
//                SD = dbHelper.getWritableDatabase();
//                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                appCom=  new UtilAppCommon();
//                String curmeterreading = "0";
//                calBill = new CBillling();
//                Long dateDuration = null;
//                int calculatedunit = 0;
//
//                Date varDate = null;
//                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//                String DTime = sdf.format(new Date());
//                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
//                try {
//                    varDate = dateFormat.parse(Structconsmas.Prev_Meter_Reading_Date);
//
//                    String billISDate = Structconsmas.BILL_ISSUE_DATE.substring(0, 4);
//                    String billISDate2 = Structconsmas.BILL_ISSUE_DATE.substring(4, 6);
//                    String billISDate3 = Structconsmas.BILL_ISSUE_DATE.substring(6, 8);
//
//                    dateFormat = new SimpleDateFormat("dd-MM-yyyy");//22-04-0017
//                    System.out.println("Date :" + dateFormat.format(varDate));
//                    String date1 = dateFormat.format(varDate).substring(0, 6);
//                    String date2 = dateFormat.format(varDate).substring(8, 10);
//
//                    String finadate = date1 + "20" + date2;
//                    System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDA :" + finadate);
//
//                    if(Structbilling.dateDuration == 0){
//                        Structbilling.dateDuration=30l;
//                    }
//                    dateDuration = Structbilling.dateDuration;
//                    calBill.totalDateduration = dateDuration;
//                    System.out.println("DATE DURATION  :" + calBill.totalDateduration);
//                    System.out.println("DATE DURATION 2  :" + dateDuration);
//                } catch (Exception e) {
//                    // TODO: handle exception
//                    e.printStackTrace();
//                }
//
//                calBill.curMeterRead = curmeterreading;
//                calBill.curMeterReadDate = DTime;
////                System.out.println("CUR READ DATE :" + Structconsmas.CUR_READ_DATE);
//
//                calBill.curMeterStatus = 4;
//                calBill.derivedMeterStatus = "4";
//
//                Structbilling.Derived_mtr_status = "4";
//                Structbilling.Cur_Meter_Stat = 4;
//
//                calculatedunit = calBill.Unitcalculation(Structbilling.Derived_mtr_status, curmeterreading, Structbilling.Cur_Meter_Stat);
//                calBill.unit = calculatedunit;
//                calBill.CalculateBill();
//
//                appCom.copyResultsetToBillingClass(calBill);
//
//                if (appCom.consump_CHK()) {
//                    new SweetAlertDialog(MeterState.this, SweetAlertDialog.WARNING_TYPE)
//
//                            .setTitleText("Billing error")
//                            .setContentText("Consumption is very high")
//                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                @Override
//                                public void onClick(SweetAlertDialog sDialog) {
//                                    sDialog.dismissWithAnimation();
//
//                                    UtilAppCommon ucom = new UtilAppCommon();
//                                    ucom.nullyfimodelCon();
//                                    ucom.nullyfimodelBill();
//
//                                    Intent intent = new Intent(MeterState.this, BillingtypesActivity.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(intent);
//                                    overridePendingTransition(R.anim.anim_slide_in_left,
//                                            R.anim.anim_slide_out_left);
//
//                                }
//                            })
//                            .show();
//                } else {
//                    Intent intent = new Intent(MeterState.this, Readinginput.class);
//                    GSBilling.getInstance().setNormalReason(0);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.anim_slide_in_left,
//                            R.anim.anim_slide_out_left);
//
//                }

//                Intent intent = new Intent(MeterState.this, BillingViewActivity.class);

//                -----------------------------------WITH RAEDING
//                if (et_Remark.getText().toString().matches("")){
//                    Structbilling.Remarks="";
//                }else{
//                    String remark=et_Remark.getText().toString().replace("\n","");
//                    Structbilling.Remarks=remark;
//                }
//
//                Log.e(getApplicationContext(), "MeterStatAct", "PrevMtrSt :" + Structconsmas.Prev_Meter_Status);
//                Intent intent = new Intent(getApplicationContext(), Readinginput.class);
//                GSBilling.getInstance().setCurmeter(4);
//
//                startActivity(intent);
//                overridePendingTransition(R.anim.anim_slide_in_left,
//                        R.anim.anim_slide_out_left);


            }
            break;

            case 4://DIAL OVER ACT(2)

                if (et_Remark.getText().toString().matches("")){
                    Structbilling.Remarks="";
                }else{
                    String remark=et_Remark.getText().toString().replace("\n","");
                    Structbilling.Remarks=remark;
                }

                Log.e(getApplicationContext(), "MeterStatAct", "PrevMtrSt :" + Structconsmas.Prev_Meter_Status);
                Intent intent = new Intent(getApplicationContext(), Readinginput.class);
                GSBilling.getInstance().setCurmeter(1);
                GSBilling.getInstance().setMetOVERFLOW(1);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);

                break;
            case 3://METER DEFECTIVE/CHanged



                if (et_Remark.getText().toString().matches("")){
                    Structbilling.Remarks="";
                }else{
                    String remark=et_Remark.getText().toString().replace("\n","");
                    Structbilling.Remarks=remark;
                }

//                        -----------------------------------------------
                /****DIALOG BOX INITIALIZATION PROVIDING A LAYOUT TO DIALOUGE****/
                final Dialog dialogAccount = new Dialog(MeterState.this, R.style.DialogeAppTheme);
                dialogAccount.setContentView(R.layout.custom_dialoge_meterchange);
                dialogAccount.setTitle("Account Search");

                // set the custom dialog components - text, image and button
                final CheckBox checkMCDB = (CheckBox) dialogAccount.findViewById(R.id.checkMCDB);
                final CheckBox checkMCNDB = (CheckBox) dialogAccount.findViewById(R.id.checkMCNDB);
                final TextView tv_met_ser = (TextView) dialogAccount.findViewById(R.id.met_Ser);

                Button dialogButton = (Button) dialogAccount.findViewById(R.id.dialogButtonACCOK);
                Button dHomeButton = (Button) dialogAccount.findViewById(R.id.HomeButton);

                tv_met_ser.setText("Meter Serial Number \n"+Structconsmas.Meter_S_No);

                dHomeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        finish();
                        overridePendingTransition(R.anim.anim_slide_in_right,
                                R.anim.anim_slide_out_right);

                    }
                });
                // if button is clicked, close the custom dialog
                /****DIALOUGE BOX BUTTON INTIALISATION AND EVENT ADDING****/
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(checkMCDB.isChecked()){
                            GSBilling.getInstance().setMeterChange("DBCHNG");
                            Intent intentMC = new Intent(MeterState.this, Readinginput.class);
                            GSBilling.getInstance().setCurmeter(9);
                            GSBilling.getInstance().setDbNotChng(0);
                            //  GSBilling.getInstance().setMetOVERFLOW(1);
                            startActivity(intentMC);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);

                        }else if(checkMCNDB.isChecked()){
                            GSBilling.getInstance().setPowerFactor(Double.parseDouble("0"));
                            GSBilling.getInstance().setUnitMaxDemand("KW");
                            GSBilling.getInstance().setPowerFactor(Double.parseDouble("0.8"));
                            GSBilling.getInstance().setDbNotChng(1);
                            Structbilling.Avrg_PF = String.valueOf(GSBilling.getInstance().getPowerFactor());

                            GSBilling.getInstance().setCurmeter(4);

                            dbHelper = new DB(getApplicationContext());
                            SD = dbHelper.getWritableDatabase();
                            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            appCom=  new UtilAppCommon();
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

                                if(Structbilling.dateDuration == 0){
                                    Structbilling.dateDuration=30l;
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
//                System.out.println("CUR READ DATE :" + Structconsmas.CUR_READ_DATE);

                            calBill.curMeterStatus = 4;
                            calBill.derivedMeterStatus = "4";

                            Structbilling.Derived_mtr_status = "4";
                            Structbilling.Cur_Meter_Stat = 4;

                            calculatedunit = calBill.Unitcalculation(Structbilling.Derived_mtr_status, curmeterreading, Structbilling.Cur_Meter_Stat);
                            calBill.unit = calculatedunit;
                            calBill.CalculateBill();

                            appCom.copyResultsetToBillingClass(calBill);

                            if (appCom.consump_CHK()) {
                                new SweetAlertDialog(MeterState.this, SweetAlertDialog.WARNING_TYPE)

                                        .setTitleText("Billing error")
                                        .setContentText("Consumption is very high")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();

                                                UtilAppCommon ucom = new UtilAppCommon();
                                                ucom.nullyfimodelCon();
                                                ucom.nullyfimodelBill();

                                                Intent intent = new Intent(MeterState.this, BillingtypesActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.anim_slide_in_left,
                                                        R.anim.anim_slide_out_left);

                                            }
                                        })
                                        .show();
                            } else {
                                Intent intent = new Intent(MeterState.this, ReasonActivity.class);
                                GSBilling.getInstance().setNormalReason(0);
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left);

                            }


//                                    Intent intentMC = new Intent(MeterState.this, Readinginput.class);
//                                    GSBilling.getInstance().setCurmeter(9);
//                                    // GSBilling.getInstance().setMetOVERFLOW(1);
//                                    startActivity(intentMC);
//                                    overridePendingTransition(R.anim.anim_slide_in_left,
//                                            R.anim.anim_slide_out_left);

                        }else if(checkMCDB.isChecked() && checkMCNDB.isChecked()){
                            new SweetAlertDialog(MeterState.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Warning")
                                    .setContentText("Select only one value ")
                                    .setConfirmText("OK, Got it!")
                                    .show();
                        }else {
                            new SweetAlertDialog(MeterState.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Warning")
                                    .setContentText("Select at-least one value ")
                                    .setConfirmText("OK, Got it!")
                                    .show();
                        }

                    }
                });
                dialogAccount.show();


//                        ------------------------------------------------

                break;

        }

        Log.e(getApplicationContext(), "MeterStatAct", "Click : \n  Position :" + itemPosition + "  \n  ListItem : " + itemValue);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.anim_slide_in_right,
                    R.anim.anim_slide_out_right);
            return true;
        }
        return false;
    }
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        ((GlobalPool)getApplication()).onUserInteraction();
    }

    @Override
    public void userLogoutListaner() {
        finish();
        Intent intent=new Intent(MeterState.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    public void onBackPressed() {
//        if (exit) {
        finish(); // finish activity
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);
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