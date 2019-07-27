package com.fedco.mbc.bluetoothprinting;

/**
 * Created by soubhagyarm on 20-06-2016.
 */

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.BillingtypesActivity;
import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.model.Structbilling;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.model.Structtariff;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.UtilAppCommon;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DuplicateBillPrint extends AppCompatActivity {

    private Activity mActivity;

    Button btnPrint, btnSettings, btnBack, btnimagepath;
    EditText edtText;
    Dialog myDialog;
    RadioButton rdbtn_Normal, rdbtn_DoubleHeight, rdbtn_DoubleWidth, rdbtn_Double, rdbtn_LandScape, rdbtn_BarCode, rdbtn_Arabic, rbtn_BarCode, rdBtn_Image;
    String myCmd = "<0x09>", selectedImagePath, strMeterMake, strMeterOwner;
    String print_type;
    TextView imagepath;
    File mSdcard, path;
    int HOURS, MIN, AP;
    public static ProgressDialog prgDialog;
    private static final int SELECT_PICTURE = 1;
    SessionManager session;

    String Hour, Minute;
    SQLiteDatabase SD, SD2, SD3, SD4;
    DB dbHelper, dbHelper2, dbHelper3, dbHelper4;
    UtilAppCommon appcomUtil;

    private static Date connvertedDate;
    private static DateFormat dateFormat;
    private static String actualDate;
    String newDate;
    String mdValue;
    String pfValue, billTime;
    Float miscValue;
    String strtime;
    String key;
    String strdcSeq;
    Long billseq;
    Long dcSeq;
    SweetAlertDialog sDialog;
    int Cur_Meter_Reading;
    int Prev_Meter_Reading;
    String Bill_Basis;
    String Reading_type_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        System.out.println("INSIDE DUPLICATE BILLLING ");
        System.out.println("capture name2 " + GSBilling.getInstance().getKEYNAME());

        Calendar c = Calendar.getInstance();
        HOURS = c.get(Calendar.HOUR);
        MIN = c.get(Calendar.MINUTE);
        AP = c.get(Calendar.AM_PM);

        if (HOURS < 10) {
            Hour = "0" + HOURS;
        }
        if (MIN <= 10) {
            Minute = "0" + MIN;
        }

        print_type = GSBilling.getInstance().getPrinttype();

        System.out.println("Duplicate Billing Meter TYpe" + Structconsmas.Meter_Type);

        switch (Structconsmas.Meter_Type.trim()) {
            case "03":
                this.strMeterMake = "1ph Static";
                System.out.println("in if3" + strMeterMake);
                break;
            case "01":
                this.strMeterMake = "1ph EM";
                System.out.println("in if1" + strMeterMake);
                break;
            case "28":
                this.strMeterMake = "LT 1 Ph AMR/AMI";
                System.out.println("in if28" + strMeterMake);
                break;
            default:
                System.out.println("in ELSE" + strMeterMake);
                System.out.println("in default" + Structconsmas.Meter_Type);
                this.strMeterMake = "";
                break;
        }


        switch (Structconsmas.Meter_Ownership) {
            case "D":
                this.strMeterOwner = "DF";
                System.out.println("in if3" + strMeterOwner);
                break;
            case "G":
                this.strMeterOwner = "CESU";
                System.out.println("in if1" + strMeterOwner);
                break;
            case "C":
                this.strMeterOwner = "CONSUMER";
                System.out.println("in if28" + strMeterMake);
                break;
            default:
                this.strMeterOwner = "";
                System.out.println("in ELSE" + strMeterMake);
                break;
        }

        System.out.println("CALLING ASYNTASK IN DUPLICATE ");
        switch (Structconsmas.PICK_REGION) {
            case "10"://bhopal
                new PrintProcessBHO().execute();
                break;
            case "11"://jabalpur
                new PrintProcessJBL().execute();
                break;
            case "12"://indore
                new PrintProcess().execute();
                break;
            default:
                new PrintProcess().execute();
                break;
        }
    }

    public void onBackPressed() {

        UtilAppCommon ucom = new UtilAppCommon();
        UtilAppCommon.nullyfimodelCon();
        UtilAppCommon.nullyfimodelBill();

        Intent intent = new Intent(DuplicateBillPrint.this, BillingtypesActivity.class);
        //        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //finish();
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
    }

    private class PrintProcess extends AsyncTask<String, Void, String> {
        /* displays the progress dialog until background task is completed*/
        private SimpleDateFormat simpleDateFormat;

        private Date connvertedDate;
        private DateFormat dateFormat;
        private String actualDate;

        @Override
        protected String doInBackground(String... params) {
            appcomUtil = new UtilAppCommon();
            actualDate = Structconsmas.Bill_Mon;
            simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = null;
            String newDate = null;

            try {
                date = simpleDateFormat.parse(actualDate);
                SimpleDateFormat postFormater = new SimpleDateFormat("dd-MMM-yyyy");
                newDate = postFormater.format(date).trim().substring(3);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Structbilling.DCNumber = "";
            billseq = appcomUtil.findSequence(getApplicationContext(), "BillNumber");

            UtilAppCommon uc = new UtilAppCommon();
            uc.print_Bill();
            uc.princonmas();

            dcSeq = appcomUtil.findSequence(getApplicationContext(), "DCNumber");
            strdcSeq = String.format("%05d", dcSeq);
            Structbilling.DCNumber = key + "/DN" + dcSeq;

            if (Structbilling.md_input != null) {
                mdValue = Structbilling.md_input + " " + "KW";
            } else {
                mdValue = "0" + " " + GSBilling.getInstance().getUnitMaxDemand();
            }


            if (GSBilling.getInstance().getPowerFactor() == 0) {
                pfValue = "0";
            } else {
                pfValue = String.valueOf(GSBilling.getInstance().getPowerFactor());
            }

            if (Structconsmas.misc_charges != null) {
                miscValue = Structconsmas.misc_charges;
            } else {
                miscValue = 0.0f;
            }

            String printtariif = (Structconsmas.Tariff_Code + "-" + (((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.Tariff_URBAN : Structtariff.Tariff_RURAL)));
            String consumerName = Structconsmas.Name;

            String meterReaderName = Structbilling.Meter_Reader_Name;
            if (Structbilling.Meter_Reader_Name.length() < 20) {
                meterReaderName = Structbilling.Meter_Reader_Name;
            } else {
                meterReaderName = Structbilling.Meter_Reader_Name.substring(0, 18);
            }
            String feederDesc = Structconsmas.FDR_SHRT_DESC;
            if (Structconsmas.FDR_SHRT_DESC.length() < 13) {
                feederDesc = Structconsmas.FDR_SHRT_DESC;
            } else {
                feederDesc = Structconsmas.FDR_SHRT_DESC.substring(0, 12);
            }
            String address = Structconsmas.address1 + Structconsmas.address2 +Structconsmas.MOH;
            String subadd1;
            String subadd2;
            String PrinDate;

            PrinDate = Structbilling.Cur_Meter_Reading_Date.substring(0, 6) + Structbilling.Cur_Meter_Reading_Date.substring(9);

            if (address.length() < 24) {
                address = Structconsmas.address1 + Structconsmas.address2 + "\n";
            } else {

//            if(Structconsmas.address1.length() > 23){
//                subadd1 = Structconsmas.address1.substring(0,22);
//            }else{
                subadd1 = Structconsmas.address1;
//            }

                if (Structconsmas.address2.length() > 23) {
                    subadd2 = Structconsmas.address2.substring(0, 16);
                } else {
                    subadd2 = Structconsmas.address2;
                }
                address = subadd1 + subadd2;
            }
            String sectionName = Structconsmas.Section_Name;
            if (sectionName.length() >= 19) {
                sectionName = Structconsmas.Section_Name.substring(0, 19);
            } else {
                sectionName = Structconsmas.Section_Name;
            }

            Calendar c1 = Calendar.getInstance();
            SimpleDateFormat fds2 = new SimpleDateFormat("kk:mm");
            billTime = fds2.format(c1.getTime());

            Prev_Meter_Reading = Structconsmas.Prev_Meter_Reading;
            Cur_Meter_Reading = Structbilling.Cur_Meter_Reading;
            Bill_Basis = Structbilling.Bill_Basis;
            Reading_type_code = RDG_TYP_CD_Print();

            if (Structbilling.Reasons != null && !Structbilling.Reasons.isEmpty()) {
                    if (Structbilling.Reasons.equalsIgnoreCase("Previous reading is higher than Current reading")) {
                        Prev_Meter_Reading = Structconsmas.Prev_Meter_Reading;
                        Cur_Meter_Reading = Structconsmas.Prev_Meter_Reading;
                        Bill_Basis = "ACT";
                        Reading_type_code = "NORMAL";

                }
            }

//            String printtariif = (Structconsmas.Tariff_Code + "-" + (((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.Tariff_URBAN : Structtariff.Tariff_RURAL)));

//            mSendMessage(
//                    "<0x09>     MADHYA PRADESH" + "\n" +
//                            "<0x09>     M.P.M.K.V.V.C.L" + "\n" +
//                            "<0x09>    ELECTRICITY BILL" + "\n" +
//                            "<0x09>-----------------------" + "\n" +
//                            "<0x09> CUSTOMER CARE NUMBERS" + "\n" +
//                            "<0x09>-----------------------" + "\n" +
//                            "<0x09>    1800-233-1912" + "\n" +
//                            //"<0x10> 0755-2551222" + "\n" +
//                            "<0x09>-----------------------" + "\n" +
//
//                            "<0x09>BILL MONTH : " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" + //201706
//                            "<0x09>BILL DATE  : " + (String.format("%1$6s", Structbilling.Bill_Date)) + "\n" +
//                            "<0x09>BILL NO    : " + (String.format("%1$6s", Structbilling.Bill_No)) + "\n" +
//                            "<0x09>BILL PERIOD: " + (String.format("%1$6s", Structbilling.Bill_Period)) + "\n" +
//                            //  "<0x09>MACHINE NO : " + (String.format("%1$6s",Structbilling.SBM_No)) + "\n" +
//                            //"<0x09>MR NAME: " + "\n" +
//                            //"<0x09>" + (String.format("%1$6s",Structbilling.Meter_Reader_Name)) + "\n" +
//                            //"<0x09>MR ID       : " + (String.format("%1$4s",Structbilling.Meter_Reader_ID)) + "\n" +
//
//                            "<0x09>-----------------------" + "\n" +
//
//                            "<0x09>DIVISION   : " + (String.format("%1$6s", "BHOPAL CITY")) + "\n" +
//                            "<0x09>DC NAME    : " + (String.format("%1$6s", sectionName)) + "\n" +
//                            "<0x09>GROUP/DAIRY NO     : " + (String.format("%1$8s", Structconsmas.Cycle)) + "/" + (String.format("%1$8s", Structconsmas.Route_Number)) + "\n" +
//                            "<0x09>FDR DESC  : " + (String.format("%1$6s", Structconsmas.FDR_SHRT_DESC)) + "\n" +
//                            "<0x09>POLE ID   : " + (String.format("%1$9s", Structconsmas.POLE_ID)) + "\n" +
//                            "<0x09>ACC ID    : " + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
//                            "<0x09>IVRS/SC NO: " + (String.format("%1$8s", Structconsmas.LOC_CD + "-" + Structconsmas.Consumer_Number)) + "\n" +
////                    "<0x09>SUB-DIV    : " + Structconsmas.Sub_division_Name + "\n" +
////                    "<0x09>SECTION    : " + Structconsmas.Section_Name + "\n" +
//                            "<0x09>NAME      : " + (String.format("%1$6s", Structconsmas.Name)) + "\n" +
//                            "<0x10>ADDRESS   : " + "\n" +
//                            "<0x09>" + (String.format("%1$6s", Structconsmas.address1 + Structconsmas.address2)) + "\n" +
//                            "<0x09>-----------------------" + "\n" +
//
////                            "<0x09>TARIFF CATG  : " + (String.format("%1$8s",Structconsmas.Tariff_Code)) + "\n" +
//                            "<0x09>TARIFF CATG  : " + (String.format("%1$8s", printtariif)) + "\n" +
//                            "<0x09>LOAD         : " + (String.format("%1$8s", Structconsmas.Load)) + getLoadType() + "\n" +
//                            "<0x09>SECURITY DEPS: " + (String.format("%1$8s", Structconsmas.TOT_SD_HELD)) + "\n" +
//                            //   "<0x09>DAIRY NO     : " + (String.format("%1$8s",Structconsmas.Route_Number)) + "\n" +
//                            "<0x09>MTR SL NO    : " + (String.format("%1$8s", Structconsmas.Meter_S_No)) + "\n" +
////                        "<0x09>MTR OWNER    : " + (String.format("%1$6s",Structconsmas.Meter_Ownership)) + "\n" +
////                        "<0x09>MAKE         : "  + "\n" +
//                            "<0x09>MF           : " + (String.format("%1$8s", Structconsmas.MF)) + "\n" +
//                            "<0x09>PHASE        : " + (String.format("%1$8s", Structconsmas.PHASE_CD)) + "\n" +
//                            "<0x09>---------------------" + "\n" +
//                            "<0x09>READING DATE :" + (String.format("%1$4s", Structbilling.Cur_Meter_Reading_Date)) + "\n" +
//                            // "<0x09>PREV AVG UNIT: " + (String.format("%1$8s",Structconsmas.PREV_AVG_UNIT)) + "\n" +
//                            "<0x09>READING STS  : " + (String.format("%1$8s", Structbilling.Bill_Basis)) + "\n" +
////                    "<0x09>PRES.BL DATE :" + Structbilling.Bill_Date + "\n" +
////                    "<0x09>PREV.BL DATE :" + Structconsmas.Prev_Meter_Reading_Date + "\n" +
//                            "<0x09>PRES READING : " + (String.format("%1$8s", Structbilling.Cur_Meter_Reading)) + "\n" +
//                            "<0x09>PREV READING : " + (String.format("%1$8s", Structconsmas.Prev_Meter_Reading)) + "\n" +
//                            "<0x09>MD           : " + (String.format("%1$8s", mdValue)) + "\n" +
//                            "<0x09>PF           : " + (String.format("%1$8s", pfValue)) + "\n" +
//                            //  "<0x09>AVG UNITS    : " + (String.format("%1$8s",Structbilling.Units_Consumed)) + "\n" +
//                            "<0x09>MTR CONSP    : " + (String.format("%1$8s", Structbilling.MTR_CONSMP)) + "\n" +
//                            "<0x09>UNIT BILLED  : " + (String.format("%1$8s", Structbilling.O_BilledUnit_Actual)) + "\n" +
//                            "<0x09>BILL TYPE    : " + (String.format("%1$8s", Structbilling.Bill_Basis)) + "\n" +
//                            "<0x09>---------------------" + "\n" +
//                            "<0x09>MIN CHARG    : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.MIN_CHRG))))) + "\n" +
//                            "<0x09>FIXED CHARG  : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.FIXED_CHARGE))))) + "\n" +
////                    "<0x09>Current Bill   " + "\n" +
////                    "<0x09>---------------" + "\n" +
////                    "<0x09>Current D.P.S :  " + (String.format("%.2f", Structconsmas.Delay_Payment_Surcharge)) + "\n" +
////                    "<0x09>Fix/Mis Chg   :  " + (String.format("%.2f", Structbilling.Monthly_Min_Charg_DC)) + "\n" +
//                            // "<0x09>EC1          : " + (String.format("%1$8s",(String.format("%.2f", Structbilling.Slab_1_EC)))) + "\n" +
//                            //"<0x09>EC2          : " + (String.format("%1$8s",(String.format("%.2f", Structbilling.Slab_2_EC)))) + "\n" +
//                            //"<0x09>EC3          : " + (String.format("%1$8s",(String.format("%.2f", Structbilling.Slab_3_EC)))) + "\n" +
//                            //"<0x09>EC4          : " + (String.format("%1$8s",(String.format("%.2f", Structbilling.Slab_4_EC)))) + "\n" +
//                            "<0x09>ENERGY CHRG  : " + (String.format("%1$8s", (String.format("%.2f", Structbilling.Total_Energy_Charg)))) + "\n" +
//                            "<0x09>FCA CHARG    : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_FCA))))) + "\n" +
//                            "<0x09>---------------------" + "\n" +
////                        "<0x09>FIX  CHARGE   :  " + (String.format("%1$6s",Structbilling.O_Total_Fixed_Charges)) + "\n" +
//                            "<0x09>DUTY CHARGE  : " + (String.format("%1$8s", (String.format("%.2f", Structbilling.Electricity_Duty_Charges)))) + "\n" +
//                            "<0x09>METER RENT   : " + (String.format("%1$8s", (String.format("%.2f", Structbilling.Meter_Rent)))) + "\n" +
//                            "<0x09>ADJ AMT      : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structconsmas.Sundry_Allow_EC))))) + "\n" +
//                            "<0x09>PANEL CHRG   : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_OverdrwalPenalty))))) + "\n" +
//                            "<0x09>P.F CHRG     : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_PFPenalty))))) + "\n" +
////                            "<0x09>P.F CHRG     : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf("0"))))) + "\n" + //JUGAD
//                            "<0x09>TOD CHRG     : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(0))))) + "\n" +
//                            "<0x09>SD BILLED    : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(UtilAppCommon.doublecheck(Structconsmas.SD_BILLED)))))) + "\n" +
//                            "<0x09>OTHER CHRG   : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_welding_charges))))) + "\n" +
//                            "<0x09>------------------" + "\n" +
//                            "<0x09>SD INT       : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf("-"+Structconsmas.SD_INSTT_AMT))))) + "\n" +
//                            "<0x09>LOCK CR      : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_LockCreditAmount))))) + "\n" +
//                            "<0x09>EMP. REBATE  : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_Employee_Incentive))))) + "\n" +
//                            "<0x09>INCENTIVES   : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf("-"+Structbilling.Prinatable_Total_Incentives))))) + "\n" +
//                            "<0x09>SUBSIDY      : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Subsidy))))) + "\n" +
//                            "<0x09>------------------" + "\n" +
//                            "<0x09>CUR BILL AMT : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_Current_Demand))))) + "\n" +
//                            "<0x09>PREV ARREARS : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_Arrear_Demand))))) + "\n" +
//                            "<0x09>SURCHARGE ARS: " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structconsmas.SURCHARGE_ARREARS))))) + "\n" +
//                            "<0x09>PREV BILL DIFF. AMT.: " + "\n" + (String.format("%1$8s", "0.00")) + "\n" +                                                                // No diff Amount
//
//                            // "<0x09>MSC CHARG    : " + (String.format("%1$8s",(String.format("%.2f", miscValue)))) + "\n" +
//                            //"<0x09>INCENTIVES/PENALTIES : "  + "\n" + //+ (String.format("%1$6s",(String.format("%.2f", Float.valueOf(Structbilling.O_PFPenalty) + Float.valueOf(Structbilling.O_OverdrwalPenalty) -Float.valueOf(Structbilling.O_Total_Incentives))))) + "\n" +                  // doubt
//                            //"<0x09> " + (String.format("%1$6s",(String.format("%.2f", Float.valueOf(Structbilling.O_PFPenalty) + Float.valueOf(Structbilling.O_OverdrwalPenalty) - Float.valueOf(Structbilling.O_Total_Incentives))))) + "\n" +                  // doubt
//                            // doubt
//                            //    "<0x09>SD ARREARS   : " + (String.format("%1$8s",(String.format("%.2f", Float.valueOf(Structconsmas.SD_ARREAR))))) + "\n" +
//
//
//                            "<0x09>ROUND AMT    : " + (String.format("%1$8s", Structbilling.Round_Amnt)) + "\n" +
////                    "<0x09>Upt(" + Structbilling.Due_Date + "):" + (String.format("%.2f", Structbilling.Amnt_bPaid_on_Rbt_Date)) + "\n" +
////                    "<0x09>Aft(" + Structbilling.Due_Date.trim() + "):" + (String.format("%.2f", Structbilling.Amnt_Paidafter_Rbt_Date)) + "\n" +
//                            //  "<0x09>TOT AMT      : " + (String.format("%1$8s",(String.format("%.2f", Float.valueOf(Math.round(Double.parseDouble(Structbilling.O_Total_Bill))))))) + "\n" +
//                            "<0x10>PAYABLE BY DUE DT:" + "\n" +
//                            "<0x09>" + (String.format("%1$6s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
//
//                            "<0x09>DELAY PAY SURCHARG : " + "\n" +
//                            "<0x09>" + (String.format("%1$6s", Structbilling.O_Surcharge)) + "\n" +
//                            "<0x09>PAYABLE AFTER DUE DT:" + "\n" +
//                            "<0x09>" + (String.format("%1$6s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" +  // doubt
//                            "<0x09>CHEQUE DUE DT      :" + "\n" +
//                            "<0x09>" + (String.format("%1$6s", Structconsmas.FIRST_CHQ_DUE_DATE)) + "\n" +
//                            "<0x09>CASH DUE DT        :" + "\n" +
//                            "<0x09>" + (String.format("%1$6s", Structconsmas.FIRST_CASH_DUE_DATE)) + "\n" +
//                            "<0x09>....................." + "\n" +
//                            "<0x09>MACHINE NO : " + (String.format("%1$6s", Structbilling.SBM_No)) + "\n" +
//                            "<0x09>MR NAME: " + "\n" +
//                            "<0x09>" + (String.format("%1$6s", Structbilling.Meter_Reader_Name)) + "\n" +
//                            "<0x09>MR ID       : " + (String.format("%1$4s", Structbilling.Meter_Reader_ID)) + "\n" +
//
//                            "<0x09>" + " " + "\n" +
//                            "<0x09>" + " " + "\n" +
//                            "<0x09>" + " " + "\n" +
//                            "<0x09>" + " " + "\n" +
//                            "<0x09>" + " " + "\n" +
//                            "<0x09>" + " " + "\n" +
//                            "<0x10>          RECEIPT        " + "\n" +
//                            "<0x09>BILL MONTH : " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" +
//                            "<0x09>BILL DATE  : " + (String.format("%1$6s", Structbilling.Bill_Date)) + "\n" +
//                            "<0x09>BILL NO    : " + (String.format("%1$6s", Structbilling.Bill_No)) + "\n" +
//                            "<0x09>DIVISION   : " + (String.format("%1$6s", "BURHANPUR")) + "\n" +
//                            "<0x09>DC NAME    : " + (String.format("%1$6s", sectionName)) + "\n" +
//                            "<0x09>GROUP/DAIRY NO: " + (String.format("%1$8s", Structconsmas.Cycle)) + "/" + (String.format("%1$8s", Structconsmas.Route_Number)) + "\n" +
//                            "<0x09>ACC ID/SC NO: " + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
//                            "<0x09>IVRS    NO  : " + (String.format("%1$8s", Structconsmas.LOC_CD + "-" + Structconsmas.Consumer_Number)) + "\n" +
////                    "<0x09>SUB-DIV    : " + Structconsmas.Sub_division_Name + "\n" +
////                    "<0x09>SECTION    : " + Structconsmas.Section_Name + "\n" +
//                            "<0x09>CONS NAME        : " + (String.format("%1$6s", Structconsmas.Name)) + "\n" +
//                            "<0x09>PAYABLE BY DUE DT:" + "\n" +
//                            "<0x09>" + (String.format("%1$6s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
//                            "<0x09>PAYABLE AFTER DUE DT:" + "\n" +
//                            "<0x09>" + (String.format("%1$6s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" + // doubt
//                            "<0x09>" + " " + "\n" +
//                            "<0x09>" + " " + "\n" +
//                            "<0x09>" + " " + "\n" +
//                            "<0x09>" + " " + "\n");     // doubt
            if (Structconsmas.Name.length() < 23 && address.length() < 23) {
                mSendMessage("<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +   // To MAtch With Amigos Printer
                        "<0x09>" + "     " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" + //2041706
                        "<0x09>" + "      " + (String.format("%1$6s", PrinDate)) + "  " + billTime + "\n" +
                        "<0x09>" + "      " + (String.format("%1$6s", Structbilling.Bill_No)) + "\n" +
                        "<0x09>" + "  " + (String.format("%1$6s", Structbilling.Bill_Period)) + "\n" +
                        "<0x09>" + "  " + "\n" +
                        "<0x09>" + "  " + "\n" +
                        "<0x09>" + "    " + (String.format("%1$6s", Structconsmas.DIV_NAME)) + "\n" +
                        "<0x09>" + "    " + (String.format("%1$6s", sectionName)) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$8s", Structconsmas.Cycle)) + "/" + Structconsmas.Route_Number + "\n" +
                        "<0x09>" + "     " + (String.format("%1$6s", feederDesc)) + "\n" +
                        "<0x09>" + "        " + (String.format("%1$9s", Structconsmas.POLE_ID)) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                        "<0x09>" + "       " + (String.format("%1$8s", appcomUtil.IVRS_NO_PRINT())) + "\n" +
                        "<0x09>" + " " + (String.format("%1$6s", consumerName)) + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + " " + (String.format("%1$6s", address)) + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "   " + (String.format("%1$8s", printtariif())) + "\n" +
                        "<0x09>" + "   " + (String.format("%1$8s", Structconsmas.Load)) + getLoadType() + "\n" +
                        "<0x09>" + "      " + (String.format("%1$8s", Structconsmas.TOT_SD_HELD)) + "\n" +
                        "<0x09>" + "       " + (String.format("%1$8s", Structconsmas.Meter_S_No)) + "\n" +
                        "<0x09>" + "    " + (String.format("%1$8s", Structconsmas.MF)) + " / " + Structconsmas.PHASE_CD + "\n" +
                        "<0x09>" + "            " + (String.format("%1$4s", Structbilling.Cur_Meter_Reading_Date)) + "\n" +
                        "<0x09>" + "   " + (String.format("%1$8s", Reading_type_code)) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", Cur_Meter_Reading)) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", Prev_Meter_Reading)) + "\n" +
                        "<0x09>" + "    " + "\n" + // To Match with Amigos Printer
                        "<0x09>" + "     " + (String.format("%1$8s", mdValue)) + "      " + Structbilling.Avrg_PF + "\n" +
                        "<0x09>" + "           " + (String.format("%1$8s", dotSeparate(Structbilling.Billed_Units) + "/" + dotSeparate(Structbilling.O_BilledUnit_Actual))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", Bill_Basis)) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.MIN_CHRG))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.FIXED_CHARGE))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_FCA))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Structbilling.Meter_Rent)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structconsmas.Sundry_Allow_EC))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_OverdrwalPenalty))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_PFPenalty))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_welding_charges))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(UtilAppCommon.doublecheck(Structconsmas.SD_BILLED)))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(0))))) + "\n" +
                        "<0x09>" + "    " + "\n" + // To Match with Amigos Printer
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structconsmas.SD_INSTT_AMT) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_LockCreditAmount) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Employee_Incentive) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Incentives) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Subsidy) * -1f)))) + "\n" +
                        "<0x09>" + "    " + "\n" + // To Match with Amigos Printer
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Bill) - Float.valueOf(Structbilling.O_Arrear_Demand) + Float.valueOf(Structconsmas.Sundry_Allow_EC))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structconsmas.Cur_Fiancial_Yr_Arr))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(printSurcharge()))))) + "\n" +
                        "<0x10>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", Structbilling.O_Surcharge)) + "\n" +
                        "<0x10>" + "             " + (String.format("%1$10s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" +  // doubt
                        "<0x09>" + "              " + (String.format("%1$6s", Structconsmas.FIRST_CHQ_DUE_DATE)) + "\n" +
                        "<0x09>" + "              " + (String.format("%1$6s", Structconsmas.FIRST_CASH_DUE_DATE)) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "         " + (String.format("%1$4s", meterReaderName)) + " " + (String.format("%1$6s", Structbilling.SBM_No)) + " " + Structbilling.VER_CODE + " " + (String.format("%1$6s", "Bill")) + "\n" +
                        "<0x09>" + "    " + "\n" + // To Match with Amigos Printer
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$6s", Structconsmas.DIV_NAME)) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$6s", sectionName)) + "\n" +
                        "<0x09>" + "        " + (String.format("%1$8s", Structconsmas.Cycle)) + "/" + Structconsmas.Route_Number + "\n" +
                        "<0x09>" + "        " + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                        "<0x09>" + "        " + (String.format("%1$8s", appcomUtil.IVRS_NO_PRINT())) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "  " + (String.format("%1$6s", consumerName)) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x10>" + "               " + (String.format("%1$6s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                        "<0x10>" + "               " + (String.format("%1$6s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" + // doubt
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" + // To match with Amigos printer
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n");
            } else if (Structconsmas.Name.length() < 23 && address.length() > 23) {
                mSendMessage("<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" + // to match with amigos printer
                        "<0x09>" + "      " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" + //201706
                        "<0x09>" + "      " + (String.format("%1$6s", PrinDate)) + "  " + billTime + "\n" +
                        "<0x09>" + "      " + (String.format("%1$6s", Structbilling.Bill_No)) + "\n" +
                        "<0x09>" + "  " + (String.format("%1$6s", Structbilling.Bill_Period)) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "    " + (String.format("%1$6s", Structconsmas.DIV_NAME)) + "\n" +
                        "<0x09>" + "     " + (String.format("%1$6s", sectionName)) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$8s", Structconsmas.Cycle)) + "/" + Structconsmas.Route_Number + "\n" +
                        "<0x09>" + "     " + (String.format("%1$6s", feederDesc)) + "\n" +
                        "<0x09>" + "        " + (String.format("%1$9s", Structconsmas.POLE_ID)) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                        "<0x09>" + "       " + (String.format("%1$8s", appcomUtil.IVRS_NO_PRINT())) + "\n" +
                        "<0x09>" + " " + "\n" + // To Match with Amigos printer
                        "<0x09>" + "  " + (String.format("%1$6s", consumerName)) + "\n" +
                        "<0x09>" + "  " + (String.format("%1$6s", address)) + "\n" +
                        "<0x09>" + " " + "\n" + // To Match with Amigos printer
                        "<0x09>" + "   " + (String.format("%1$8s", printtariif())) + "\n" +
                        "<0x09>" + "   " + (String.format("%1$8s", Structconsmas.Load)) + getLoadType() + "\n" +
                        "<0x09>" + "      " + (String.format("%1$8s", Structconsmas.TOT_SD_HELD)) + "\n" +
                        "<0x09>" + "       " + (String.format("%1$8s", Structconsmas.Meter_S_No)) + "\n" +
                        "<0x09>" + "    " + (String.format("%1$8s", Structconsmas.MF)) + " / " + Structconsmas.PHASE_CD + "\n" +
                        "<0x09>" + "            " + (String.format("%1$4s", Structbilling.Cur_Meter_Reading_Date)) + "\n" +
                        "<0x09>" + "   " + (String.format("%1$8s", Reading_type_code)) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", Cur_Meter_Reading)) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", Prev_Meter_Reading)) + "\n" +
                        "<0x09>" + " " + "\n" + // To Match with Amigos printer
                        "<0x09>" + "     " + (String.format("%1$8s", mdValue)) + "      " + Structbilling.Avrg_PF + "\n" +
                        "<0x09>" + "           " + (String.format("%1$8s", dotSeparate(Structbilling.Billed_Units) + "/" + dotSeparate(Structbilling.O_BilledUnit_Actual))) + "\n" +
                        "<0x09>" + "            " + (String.format("%1$10s", Bill_Basis)) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.MIN_CHRG))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.FIXED_CHARGE))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_FCA))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Structbilling.Meter_Rent)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structconsmas.Sundry_Allow_EC))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_OverdrwalPenalty))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_PFPenalty))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_welding_charges))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(UtilAppCommon.doublecheck(Structconsmas.SD_BILLED)))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(0))))) + "\n" +
                        "<0x09>" + " " + "\n" +  // To Match With Amigos Printer
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structconsmas.SD_INSTT_AMT) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_LockCreditAmount) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Employee_Incentive) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Incentives) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Subsidy) * -1f)))) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Bill) - Float.valueOf(Structbilling.O_Arrear_Demand) + Float.valueOf(Structconsmas.Sundry_Allow_EC))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structconsmas.Cur_Fiancial_Yr_Arr))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(printSurcharge()))))) + "\n" +
                        "<0x10>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
//                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", Structbilling.O_Surcharge)) + "\n" +
                        "<0x10>" + "             " + (String.format("%1$10s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" +  // doubt
                        "<0x09>" + "              " + (String.format("%1$6s", Structconsmas.FIRST_CHQ_DUE_DATE)) + "\n" +
                        "<0x09>" + "              " + (String.format("%1$6s", Structconsmas.FIRST_CASH_DUE_DATE)) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "         " + (String.format("%1$4s", meterReaderName)) + " " + (String.format("%1$6s", Structbilling.SBM_No)) + " " + Structbilling.VER_CODE + " " + (String.format("%1$6s", "Bill")) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$6s", Structconsmas.DIV_NAME)) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$6s", sectionName)) + "\n" +
                        "<0x09>" + "        " + (String.format("%1$8s", Structconsmas.Cycle)) + "/" + Structconsmas.Route_Number + "\n" +
                        "<0x09>" + "        " + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                        "<0x09>" + "        " + (String.format("%1$8s", appcomUtil.IVRS_NO_PRINT())) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "   " + (String.format("%1$6s", consumerName)) + "\n" +
                        "<0x10>" + "               " + (String.format("%1$6s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" + /*" " + "\n" +*/
                        "<0x10>" + "               " + (String.format("%1$6s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" + // doubt
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" + // To match with Amigos printer
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n");
            } else {
                mSendMessage("<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" + // to match with amigos printer
                        "<0x09>" + "      " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" + //201706
                        "<0x09>" + "      " + (String.format("%1$6s", PrinDate)) + "  " + billTime + "\n" +
                        "<0x09>" + "      " + (String.format("%1$6s", Structbilling.Bill_No)) + "\n" +
                        "<0x09>" + "  " + (String.format("%1$6s", Structbilling.Bill_Period)) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "    " + (String.format("%1$6s", Structconsmas.DIV_NAME)) + "\n" +
                        "<0x09>" + "     " + (String.format("%1$6s", sectionName)) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$8s", Structconsmas.Cycle)) + "/" + Structconsmas.Route_Number + "\n" +
                        "<0x09>" + "     " + (String.format("%1$6s", feederDesc)) + "\n" +
                        "<0x09>" + "        " + (String.format("%1$9s", Structconsmas.POLE_ID)) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                        "<0x09>" + "       " + (String.format("%1$8s", appcomUtil.IVRS_NO_PRINT())) + "\n" +
//                        "<0x09>" + " " + "\n" + // To Match with Amigos printer
                        "<0x09>" + "  " + (String.format("%1$6s", consumerName)) + "\n" +
                        "<0x09>" + "  " + (String.format("%1$6s", address)) + "\n" +
                        "<0x09>" + " " + "\n" + // To Match with Amigos printer
                        "<0x09>" + "   " + (String.format("%1$8s", printtariif())) + "\n" +
                        "<0x09>" + "   " + (String.format("%1$8s", Structconsmas.Load)) + getLoadType() + "\n" +
                        "<0x09>" + "      " + (String.format("%1$8s", Structconsmas.TOT_SD_HELD)) + "\n" +
                        "<0x09>" + "       " + (String.format("%1$8s", Structconsmas.Meter_S_No)) + "\n" +
                        "<0x09>" + "    " + (String.format("%1$8s", Structconsmas.MF)) + " / " + Structconsmas.PHASE_CD + "\n" +
                        "<0x09>" + "            " + (String.format("%1$4s", Structbilling.Cur_Meter_Reading_Date)) + "\n" +
                        "<0x09>" + "   " + (String.format("%1$8s", Reading_type_code)) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", Cur_Meter_Reading)) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", Prev_Meter_Reading)) + "\n" +
                        "<0x09>" + " " + "\n" + // To Match with Amigos printer
                        "<0x09>" + "     " + (String.format("%1$8s", mdValue)) + "      " + Structbilling.Avrg_PF + "\n" +
                        "<0x09>" + "           " + (String.format("%1$8s", dotSeparate(Structbilling.Billed_Units) + "/" + dotSeparate(Structbilling.O_BilledUnit_Actual))) + "\n" +
                        "<0x09>" + "            " + (String.format("%1$10s", Bill_Basis)) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.MIN_CHRG))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.FIXED_CHARGE))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_FCA))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Structbilling.Meter_Rent)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structconsmas.Sundry_Allow_EC))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_OverdrwalPenalty))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_PFPenalty))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_welding_charges))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(UtilAppCommon.doublecheck(Structconsmas.SD_BILLED)))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(0))))) + "\n" +
                        "<0x09>" + " " + "\n" +  // To Match With Amigos Printer
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structconsmas.SD_INSTT_AMT) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_LockCreditAmount) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Employee_Incentive) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Incentives) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Subsidy) * -1f)))) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Bill) - Float.valueOf(Structbilling.O_Arrear_Demand) + Float.valueOf(Structconsmas.Sundry_Allow_EC))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structconsmas.Cur_Fiancial_Yr_Arr))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(printSurcharge()))))) + "\n" +
                        "<0x10>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
//                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", Structbilling.O_Surcharge)) + "\n" +
                        "<0x10>" + "             " + (String.format("%1$10s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" +  // doubt
                        "<0x09>" + "              " + (String.format("%1$6s", Structconsmas.FIRST_CHQ_DUE_DATE)) + "\n" +
                        "<0x09>" + "              " + (String.format("%1$6s", Structconsmas.FIRST_CASH_DUE_DATE)) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "         " + (String.format("%1$4s", meterReaderName)) + " " + (String.format("%1$6s", Structbilling.SBM_No)) + " " + Structbilling.VER_CODE + " " + (String.format("%1$6s", "Bill")) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$6s", Structconsmas.DIV_NAME)) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$6s", sectionName)) + "\n" +
                        "<0x09>" + "        " + (String.format("%1$8s", Structconsmas.Cycle)) + "/" + Structconsmas.Route_Number + "\n" +
                        "<0x09>" + "        " + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                        "<0x09>" + "        " + (String.format("%1$8s", appcomUtil.IVRS_NO_PRINT())) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "   " + (String.format("%1$6s", consumerName)) + "\n" +
                        "<0x10>" + "               " + (String.format("%1$6s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" + /*" " + "\n" +*/
                        "<0x10>" + "               " + (String.format("%1$6s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" + // doubt
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" + // To match with Amigos printer
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n");
            }
//            dbHelper.insertSequence("DCNumber", dcSeq);
//            }
//            else
//            {
////                sendMessage("<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + "           " + Structbilling.Bill_Month + "\n" +
////                        "<0x09>" + "       " + Structbilling.Bill_Date + "  " + strtime +
////                        "<0x09>" + "       " + Structbilling.Bill_No + "    " + "1.0" +
////                        "<0x09>" + "              " + Structbilling.Bill_Period + "\n" +
////                        "<0x09>" + "           " + Structconsmas.Division_Name + "\n" +
////                        "<0x09>" + "           " + Structconsmas.Sub_division_Name + "\n" +
////                        "<0x09>" + "           " + Structconsmas.Section_Name + "\n" +
////                        "<0x09>" + "           " + Structbilling.SBM_No + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + "    " + Structbilling.Meter_Reader_Name.trim() + "\n" +
////                        "<0x09>" + "           " + Structbilling.Meter_Reader_ID.trim() + "\n" +
////                        "<0x09>" + "      " + Structconsmas.Electrical_Address + "\n" +
////                        "<0x09>" + "        " + Structbilling.Cons_Number + "\n" +
////                        "<0x09>" + "        " + Structconsmas.Old_Consumer_Number + "\n" +
////                        "<0x09>" + "  " + Structconsmas.Name.trim() + "\n" +
////                        "<0x09>" + "        " + Structconsmas.address1.trim() + Structconsmas.address2.trim() + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + "       " + Structconsmas.Tariff_Code + "\n" +
////                        "<0x09>" + "        " + Structconsmas.Category + "        " + Structconsmas.Load + "\n" +
//////                            "<0x09>" + "\n" +
////                        "<0x09>" + "          " + Structconsmas.Cycle + "\n" +
////                        "<0x09>" + "          " + Structconsmas.Route_Number + "\n" +
////                        "<0x09>" + "          " + Structconsmas.Meter_S_No + "\n" +
////                        "<0x09>" + "          " + strMeterOwner + "\n" +
////                        "<0x09>" + "          " + strMeterMake + "\n" +
////                        "<0x09>" + "        " + Structconsmas.Multiply_Factor + "             " + "1" + "\n" +
////                        "<0x09>" + "           " + Structbilling.Avrg_Units_Billed + "\n" +
////                        "<0x09>" + " " + "      \n" +
////                        "<0x09>" + "     " + Structbilling.Cur_Meter_Reading + " " + Structbilling.Bill_Date + " " + Structbilling.Cur_Meter_Stat + "\n" +
////                        "<0x09>" + "     " + Structconsmas.Prev_Meter_Reading + " " + Structconsmas.Prev_Meter_Reading_Date + " " + Structconsmas.Prev_Meter_Status +
////                        "<0x09>" + "    " + " 0 " + "            " + " 0 " + "\n" +
////                        "<0x09>" + "          " + Structbilling.Units_Consumed + "\n" +
////                        "<0x09>" + "          " + Structbilling.Bill_Basis + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Monthly_Min_Charg_DC))) + "\n" +
////                        "<0x09>" + "      " + String.format("%5.5s", slab1unit) + " =" + (String.format("%1$6s", String.format("%.2f", Structbilling.Slab_1_EC))) + "\n" +
////                        "<0x09>" + "      " + String.format("%5.5s", slab2unit) + " =" + (String.format("%1$6s", String.format("%.2f", Structbilling.Slab_2_EC))) + "\n" +
////                        "<0x09>" + "      " + String.format("%5.5s", slab3unit) + " =" + (String.format("%1$6s", String.format("%.2f", Structbilling.Slab_3_EC))) + "\n" +
////                        "<0x09>" + "      " + String.format("%5.5s", slab4unit) + " =" + (String.format("%1$6s", String.format("%.2f", Structbilling.Slab_4_EC))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Total_Energy_Charg))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Electricity_Duty_Charges))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structconsmas.Meter_Rent))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", "0.00")) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structconsmas.Delay_Payment_Surcharge))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", "0.00")) + "\n" +
//////                            "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structconsmas.SD_Interest_chngto_SD_AVAIL))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Cur_Bill_Total))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structconsmas.Pre_Financial_Yr_Arr))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structconsmas.Cur_Fiancial_Yr_Arr))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", "0.00")) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.House_Lck_Adju_Amnt))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structconsmas.SD_Interest_chngto_SD_AVAIL))) + "\n" +
//////                                "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Cur_Bill_Total))) + "\n" +
//////                                "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Cur_Bill_Total))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", "0.00")) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Amnt_Paidafter_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Rbt_Amnt))) + "\n" +
////                        "<0x10>" + "             " + (String.format("%1$6s", String.format("%.2f", Math.ceil(Structbilling.Amnt_bPaid_on_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL)))) + "\n" +
////                        "<0x09>" + "             " + Structbilling.Due_Date +
////                        "<0x10>" + "             " + (String.format("%1$6s", String.format("%.2f", Math.ceil(Structbilling.Amnt_Paidafter_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL)))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", "0.00")) + "\n" +
////                        "<0x09>" + " " + "\n" +
//////                            "<0x09>" + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + "     " + Structconsmas.Last_Pay_Receipt_Book_No + "\n" +
////                        "<0x09>" + "     " + Structconsmas.Last_Pay_Receipt_No + "\n" +
////                        "<0x09>" + "     " + Structconsmas.Last_Total_Pay_Paid + "    " + Structconsmas.Last_Pay_Date + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
//////                            "<0x80>" +imgSrcPathDes+ "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + "  " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
//////                            "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n");
//                mSendMessage("<0x9>BILL MONTH :" + (String.format("%1$6s", Structconsmas.Bill_Mon)) + "\n" +
//                        "<0x09>BILL DATE: " + (String.format("%1$6s", Structbilling.Bill_Date)) + "\n" +
//                        "<0x09>BILL NO:" + (String.format("%1$6s",Structbilling.Bill_No)) + "\n" +
//                        "<0x09>BILL PERIOD:" + (String.format("%1$6s",Structbilling.Bill_Period)) + "\n" +
//                        "<0x09>DIVISION:" + (String.format("%1$6s",Structconsmas.Division_Name)) + "\n" +
//                        "<0x10>DC NAME:" + (String.format("%1$6s",Structconsmas.Section_Name)) + "\n" +
//                        "<0x10>MACHINE NO:" + (String.format("%1$6s",Structbilling.SBM_No)) + "\n" +
//                        "<0x09>------------------------" + "\n" +
//                        "<0x09>METER READER NAME" + (String.format("%1$6s",Structbilling.Meter_Reader_Name)) +"\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>METER READER ID: " + (String.format("%1$6s",Structbilling.Meter_Reader_ID)) + "\n" +
//                        "<0x09>POLE ID : " + (String.format("%1$6s",Structconsmas.POLE_ID)) + "\n" +
//                        "<0x09>ACC ID   : " + (String.format("%1$6s",Structconsmas.Consumer_Number)) + "\n" +
//                        "<0x09>IVRS/SC NO: " + (String.format("%1$6s",Structconsmas.Consumer_Number + "" + Structconsmas.LOC_CD)) + "\n" +
////                    "<0x09>SUB-DIV    : " + Structconsmas.Sub_division_Name + "\n" +
////                    "<0x09>SECTION    : " + Structconsmas.Section_Name + "\n" +
//                        "<0x09>NAME :  " + (String.format("%1$6s",Structconsmas.Name)) + "\n" +
//                        "<0x10>ADDRESS   : " + "\n" +
//                        "<0x09>" + (String.format("%1$6s",Structconsmas.address1 + Structconsmas.address2)) + "\n" +
//                        "<0x09>TARIFF CATG:  " + (String.format("%1$6s",Structconsmas.Tariff_Code)) + "\n" +
//
//                        "<0x09>C.LOAD     : " + (String.format("%1$6s",Structconsmas.Load)) + "\n" +
//                        "<0x09>GROUP NO     : " + (String.format("%1$6s",Structconsmas.Cycle)) + "\n" +
//                        "<0x09>DAIRY NO     : " + (String.format("%1$6s",Structconsmas.Route_Number)) + "\n" +
//                        "<0x09>MTR SL NO    : " + (String.format("%1$6s",Structconsmas.Meter_S_No)) + "\n" +
//                        "<0x09>MTR OWNER    : " + (String.format("%1$6s",Structconsmas.Meter_Ownership)) + "\n" +
//                        "<0x09>MAKE         : "  + "\n" +
//                        "<0x09>MF           : " + (String.format("%1$6s",Structconsmas.Multiply_Factor)) + "<0x09>      PHASE: " + (String.format("%1$6s",Structconsmas.PHASE_CD)) + "\n" +
//                        "<0x09>---------------------" + "\n" +
//                        "<0x09>RDG DATE     :" + (String.format("%1$6s",Structbilling.Cur_Meter_Reading_Date)) + "\n" + "<0x09>      STS: " + (String.format("%1$6s",Structbilling.Bill_Basis)) + "\n" +
//                        "<0x09>" + "\n" +
////                    "<0x09>PRES.BL DATE :" + Structbilling.Bill_Date + "\n" +
////                    "<0x09>PREV.BL DATE :" + Structconsmas.Prev_Meter_Reading_Date + "\n" +
//                        "<0x09>PRES.READING :  " + (String.format("%1$6s",Structbilling.Cur_Meter_Reading)) + "\n" +
//                        "<0x09>PREV.READING :  " + (String.format("%1$6s",Structconsmas.Prev_Meter_Reading)) + "\n" +
//                        "<0x09>MD           :  " + (String.format("%1$6s",Structbilling.md_input)) + "\n" + "<0x09>      PF: " + (String.format("%1$6s",Structbilling.Avrg_PF)) + "\n" +
//                        "<0x09>AVG UNITS    :  " + (String.format("%1$6s",Structbilling.Units_Consumed)) + "\n" +
//                        "<0x09>UNIT BILLED  :  " + (String.format("%1$6s",Structbilling.Billed_Units)) + "\n" +
//                        "<0x09>BILL TYPE    : " + "\n" +                                // NO BILL TYPE
//                        "<0x09>---------------------" + "\n" +
//                        "<0x09>FIXED CHARGE :  " + (String.format("%1$6s",Structbilling.FIXED_CHARGE)) + "\n" +
//                        "<0x09>MIN CHARGE   :  " + (String.format("%1$6s",Structbilling.MIN_CHRG)) + "\n" +
////                    "<0x09>Current Bill   " + "\n" +
////                    "<0x09>---------------" + "\n" +
////                    "<0x09>Current D.P.S :  " + (String.format("%.2f", Structconsmas.Delay_Payment_Surcharge)) + "\n" +
////                    "<0x09>Fix/Mis Chg   :  " + (String.format("%.2f", Structbilling.Monthly_Min_Charg_DC)) + "\n" +
//                        "<0x09>EC1           :  " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Slab_1_EC)))) + "\n" +
//                        "<0x09>EC2           :  " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Slab_2_EC)))) + "\n" +
//                        "<0x09>EC3           :  " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Slab_3_EC)))) + "\n" +
//                        "<0x09>EC4           :  " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Slab_4_EC)))) + "\n" +
//                        "<0x09>Energy Chg    :  " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Total_Energy_Charg)))) + "\n" +
//                        "<0x09>FCA CHARGE    :  " + (String.format("%1$6s",Structbilling.O_Total_Fixed_Charges)) + "\n" +
//                        "<0x09>DUTY CHARGE   :  " + (String.format("%1$6s",Structbilling.O_ElectricityDutyCharges)) + "\n" +
//                        "<0x09>METER RENT    :  " + (String.format("%1$6s",Structbilling.O_MeterRent)) + "\n" +
//                        "<0x09>SURCHARGES    :  " + (String.format("%1$6s",Structbilling.O_Surcharge)) + "\n" +
//                        "<0x09>MSC CHARG     :  " + (String.format("%1$6s",(String.format("%.2f", Structconsmas.misc_charges)))) + "\n" +
//                        "<0x09>INCENTIVES/PENALITIES :" + (String.format("%1$6s",Structbilling.O_Total_Incentives)) + "\n" +                  // doubt
//                        "<0x09>" + "\n" +
//                        "<0x10>CUR BILL AMT  :  " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Cur_Bill_Total)))) + "\n" +
//                        "<0x09>SD BILLED     :  " + (String.format("%1$6s",Structconsmas.SD_BILLED))+ "\n" +
//                        "<0x09>SD ARREARS    :  " + (String.format("%1$6s",Structconsmas.SD_ARREAR)) + "\n" +
//                        "<0x09>PRE ARREARS   :" + (String.format("%1$6s",Structconsmas.Cur_Fiancial_Yr_Arr)) + "\n" +
//                        "<0x09>ADJ AMT       :" + (String.format("%1$6s",(String.format("%.2f", Structconsmas.Sundry_Allow_EC)))) + "\n" +
//                        "<0x09>SD INT        :" + (String.format("%1$6s",Structconsmas.SD_INSTT_AMT)) + "\n" +
//                        "<0x09>DIFF AMT      : " + (String.format("%1$6s","0.00")) + "\n" +                                                                // No diff Amount
//                        "<0x09>ROUND AMT     :" + (String.format("%1$6s",Structbilling.Round_Amnt)) + "\n" +
////                    "<0x09>Upt(" + Structbilling.Due_Date + "):" + (String.format("%.2f", Structbilling.Amnt_bPaid_on_Rbt_Date)) + "\n" +
////                    "<0x09>Aft(" + Structbilling.Due_Date.trim() + "):" + (String.format("%.2f", Structbilling.Amnt_Paidafter_Rbt_Date)) + "\n" +
//                        "<0x09>TOT AMT       :" + (String.format("%1$6s",Structbilling.O_Total_Bill)) + "\n" +
//                        "<0x09>DPS AMT       :" + (String.format("%1$6s",Structbilling.Delay_Pay_Surcharge)) + "\n" +
//                        "<0x09>....................." + "\n" +
//                        "<0x10>PAY BL BY DUE DT   :" + (String.format("%1$6s",(String.format("%.2f",(Double.valueOf(Structbilling.O_Total_Bill) - Double.valueOf(Structbilling.O_Surcharge)))))) + "\n" +  // doubt
//                        "<0x10>PAY BL AFTER DUE DT:" + (String.format("%1$6s",Structbilling.O_Total_Bill)) + "\n" +
//                        "<0x10>CHQ/ONLINE DUE DT  :" + (String.format("%1$6s",Structconsmas.FIRST_CHQ_DUE_DATE)) + "\n" +
//                        "<0x10>CASH DUE DT        :" + (String.format("%1$6s",Structconsmas.FIRST_CASH_DUE_DATE)) +"\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>" + "\n" +
//                        "<0x10>          RECEIPT        " + "\n" +
//                        "<0x09>BILL MON :" +  (String.format("%1$6s",Structconsmas.Bill_Mon)) + "\n" +
//                        "<0x09>DIV      :" +  (String.format("%1$6s",Structconsmas.Division_Name)) +"\n" +
//                        "<0x09>ACC ID   :" + (String.format("%1$6s",Structconsmas.Consumer_Number)) + "\n" +
//                        "<0x09>DIVISION :" + (String.format("%1$6s",Structconsmas.Division_Name)) + "\n" +
//                        "<0x09>BILL DATE :" + (String.format("%1$6s",Structbilling.Bill_Date)) + "\n" +
//                        "<0x09>IVRS/SC NO :" + (String.format("%1$6s",Structconsmas.Consumer_Number + "" + Structconsmas.LOC_CD)) + "\n" +
//                        "<0x10>TOTAL      :" + (String.format("%1$6s",(String.format("%.2f", Structbilling.Cur_Bill_Total)))) + "\n");     // doubt
//            }
//            dbHelper.insertSequence("DCNumber", dcSeq);
//            }
//            else
//            {
////                sendMessage("<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + "           " + Structbilling.Bill_Month + "\n" +
////                        "<0x09>" + "       " + Structbilling.Bill_Date + "  " + strtime +
////                        "<0x09>" + "       " + Structbilling.Bill_No + "    " + "1.0" +
////                        "<0x09>" + "              " + Structbilling.Bill_Period + "\n" +
////                        "<0x09>" + "           " + Structconsmas.Division_Name + "\n" +
////                        "<0x09>" + "           " + Structconsmas.Sub_division_Name + "\n" +
////                        "<0x09>" + "           " + Structconsmas.Section_Name + "\n" +
////                        "<0x09>" + "           " + Structbilling.SBM_No + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + "    " + Structbilling.Meter_Reader_Name.trim() + "\n" +
////                        "<0x09>" + "           " + Structbilling.Meter_Reader_ID.trim() + "\n" +
////                        "<0x09>" + "      " + Structconsmas.Electrical_Address + "\n" +
////                        "<0x09>" + "        " + Structbilling.Cons_Number + "\n" +
////                        "<0x09>" + "        " + Structconsmas.Old_Consumer_Number + "\n" +
////                        "<0x09>" + "  " + Structconsmas.Name.trim() + "\n" +
////                        "<0x09>" + "        " + Structconsmas.address1.trim() + Structconsmas.address2.trim() + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + "       " + Structconsmas.Tariff_Code + "\n" +
////                        "<0x09>" + "        " + Structconsmas.Category + "        " + Structconsmas.Load + "\n" +
//////                            "<0x09>" + "\n" +
////                        "<0x09>" + "          " + Structconsmas.Cycle + "\n" +
////                        "<0x09>" + "          " + Structconsmas.Route_Number + "\n" +
////                        "<0x09>" + "          " + Structconsmas.Meter_S_No + "\n" +
////                        "<0x09>" + "          " + strMeterOwner + "\n" +
////                        "<0x09>" + "          " + strMeterMake + "\n" +
////                        "<0x09>" + "        " + Structconsmas.Multiply_Factor + "             " + "1" + "\n" +
////                        "<0x09>" + "           " + Structbilling.Avrg_Units_Billed + "\n" +
////                        "<0x09>" + " " + "      \n" +
////                        "<0x09>" + "     " + Structbilling.Cur_Meter_Reading + " " + Structbilling.Bill_Date + " " + Structbilling.Cur_Meter_Stat + "\n" +
////                        "<0x09>" + "     " + Structconsmas.Prev_Meter_Reading + " " + Structconsmas.Prev_Meter_Reading_Date + " " + Structconsmas.Prev_Meter_Status +
////                        "<0x09>" + "    " + " 0 " + "            " + " 0 " + "\n" +
////                        "<0x09>" + "          " + Structbilling.Units_Consumed + "\n" +
////                        "<0x09>" + "          " + Structbilling.Bill_Basis + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Monthly_Min_Charg_DC))) + "\n" +
////                        "<0x09>" + "      " + String.format("%5.5s", slab1unit) + " =" + (String.format("%1$6s", String.format("%.2f", Structbilling.Slab_1_EC))) + "\n" +
////                        "<0x09>" + "      " + String.format("%5.5s", slab2unit) + " =" + (String.format("%1$6s", String.format("%.2f", Structbilling.Slab_2_EC))) + "\n" +
////                        "<0x09>" + "      " + String.format("%5.5s", slab3unit) + " =" + (String.format("%1$6s", String.format("%.2f", Structbilling.Slab_3_EC))) + "\n" +
////                        "<0x09>" + "      " + String.format("%5.5s", slab4unit) + " =" + (String.format("%1$6s", String.format("%.2f", Structbilling.Slab_4_EC))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Total_Energy_Charg))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Electricity_Duty_Charges))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structconsmas.Meter_Rent))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", "0.00")) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structconsmas.Delay_Payment_Surcharge))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", "0.00")) + "\n" +
//////                            "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structconsmas.SD_Interest_chngto_SD_AVAIL))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Cur_Bill_Total))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structconsmas.Pre_Financial_Yr_Arr))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structconsmas.Cur_Fiancial_Yr_Arr))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", "0.00")) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.House_Lck_Adju_Amnt))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structconsmas.SD_Interest_chngto_SD_AVAIL))) + "\n" +
//////                                "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Cur_Bill_Total))) + "\n" +
//////                                "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Cur_Bill_Total))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", "0.00")) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Amnt_Paidafter_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", String.format("%.2f", Structbilling.Rbt_Amnt))) + "\n" +
////                        "<0x10>" + "             " + (String.format("%1$6s", String.format("%.2f", Math.ceil(Structbilling.Amnt_bPaid_on_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL)))) + "\n" +
////                        "<0x09>" + "             " + Structbilling.Due_Date +
////                        "<0x10>" + "             " + (String.format("%1$6s", String.format("%.2f", Math.ceil(Structbilling.Amnt_Paidafter_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL)))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$6s", "0.00")) + "\n" +
////                        "<0x09>" + " " + "\n" +
//////                            "<0x09>" + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + "     " + Structconsmas.Last_Pay_Receipt_Book_No + "\n" +
////                        "<0x09>" + "     " + Structconsmas.Last_Pay_Receipt_No + "\n" +
////                        "<0x09>" + "     " + Structconsmas.Last_Total_Pay_Paid + "    " + Structconsmas.Last_Pay_Date + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
//////                            "<0x80>" +imgSrcPathDes+ "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + "  " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
//////                            "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n");
//                mSendMessage("<0x9>BILL MONTH :" + (String.format("%1$6s", Structconsmas.Bill_Mon)) + "\n" +
//                        "<0x09>BILL DATE: " + (String.format("%1$6s", Structbilling.Bill_Date)) + "\n" +
//                        "<0x09>BILL NO:" + (String.format("%1$6s",Structbilling.Bill_No)) + "\n" +
//                        "<0x09>BILL PERIOD:" + (String.format("%1$6s",Structbilling.Bill_Period)) + "\n" +
//                        "<0x09>DIVISION:" + (String.format("%1$6s",Structconsmas.Division_Name)) + "\n" +
//                        "<0x10>DC NAME:" + (String.format("%1$6s",Structconsmas.Section_Name)) + "\n" +
//                        "<0x10>MACHINE NO:" + (String.format("%1$6s",Structbilling.SBM_No)) + "\n" +
//                        "<0x09>------------------------" + "\n" +
//                        "<0x09>METER READER NAME" + (String.format("%1$6s",Structbilling.Meter_Reader_Name)) +"\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>METER READER ID: " + (String.format("%1$6s",Structbilling.Meter_Reader_ID)) + "\n" +
//                        "<0x09>POLE ID : " + (String.format("%1$6s",Structconsmas.POLE_ID)) + "\n" +
//                        "<0x09>ACC ID   : " + (String.format("%1$6s",Structconsmas.Consumer_Number)) + "\n" +
//                        "<0x09>IVRS/SC NO: " + (String.format("%1$6s",Structconsmas.Consumer_Number + "" + Structconsmas.LOC_CD)) + "\n" +
////                    "<0x09>SUB-DIV    : " + Structconsmas.Sub_division_Name + "\n" +
////                    "<0x09>SECTION    : " + Structconsmas.Section_Name + "\n" +
//                        "<0x09>NAME :  " + (String.format("%1$6s",Structconsmas.Name)) + "\n" +
//                        "<0x10>ADDRESS   : " + "\n" +
//                        "<0x09>" + (String.format("%1$6s",Structconsmas.address1 + Structconsmas.address2)) + "\n" +
//                        "<0x09>TARIFF CATG:  " + (String.format("%1$6s",Structconsmas.Tariff_Code)) + "\n" +
//
//                        "<0x09>C.LOAD     : " + (String.format("%1$6s",Structconsmas.Load)) + "\n" +
//                        "<0x09>GROUP NO     : " + (String.format("%1$6s",Structconsmas.Cycle)) + "\n" +
//                        "<0x09>DAIRY NO     : " + (String.format("%1$6s",Structconsmas.Route_Number)) + "\n" +
//                        "<0x09>MTR SL NO    : " + (String.format("%1$6s",Structconsmas.Meter_S_No)) + "\n" +
//                        "<0x09>MTR OWNER    : " + (String.format("%1$6s",Structconsmas.Meter_Ownership)) + "\n" +
//                        "<0x09>MAKE         : "  + "\n" +
//                        "<0x09>MF           : " + (String.format("%1$6s",Structconsmas.Multiply_Factor)) + "<0x09>      PHASE: " + (String.format("%1$6s",Structconsmas.PHASE_CD)) + "\n" +
//                        "<0x09>---------------------" + "\n" +
//                        "<0x09>RDG DATE     :" + (String.format("%1$6s",Structbilling.Cur_Meter_Reading_Date)) + "\n" + "<0x09>      STS: " + (String.format("%1$6s",Structbilling.Bill_Basis)) + "\n" +
//                        "<0x09>" + "\n" +
////                    "<0x09>PRES.BL DATE :" + Structbilling.Bill_Date + "\n" +
////                    "<0x09>PREV.BL DATE :" + Structconsmas.Prev_Meter_Reading_Date + "\n" +
//                        "<0x09>PRES.READING :  " + (String.format("%1$6s",Structbilling.Cur_Meter_Reading)) + "\n" +
//                        "<0x09>PREV.READING :  " + (String.format("%1$6s",Structconsmas.Prev_Meter_Reading)) + "\n" +
//                        "<0x09>MD           :  " + (String.format("%1$6s",Structbilling.md_input)) + "\n" + "<0x09>      PF: " + (String.format("%1$6s",Structbilling.Avrg_PF)) + "\n" +
//                        "<0x09>AVG UNITS    :  " + (String.format("%1$6s",Structbilling.Units_Consumed)) + "\n" +
//                        "<0x09>UNIT BILLED  :  " + (String.format("%1$6s",Structbilling.Billed_Units)) + "\n" +
//                        "<0x09>BILL TYPE    : " + "\n" +                                // NO BILL TYPE
//                        "<0x09>---------------------" + "\n" +
//                        "<0x09>FIXED CHARGE :  " + (String.format("%1$6s",Structbilling.FIXED_CHARGE)) + "\n" +
//                        "<0x09>MIN CHARGE   :  " + (String.format("%1$6s",Structbilling.MIN_CHRG)) + "\n" +
////                    "<0x09>Current Bill   " + "\n" +
////                    "<0x09>---------------" + "\n" +
////                    "<0x09>Current D.P.S :  " + (String.format("%.2f", Structconsmas.Delay_Payment_Surcharge)) + "\n" +
////                    "<0x09>Fix/Mis Chg   :  " + (String.format("%.2f", Structbilling.Monthly_Min_Charg_DC)) + "\n" +
//                        "<0x09>EC1           :  " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Slab_1_EC)))) + "\n" +
//                        "<0x09>EC2           :  " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Slab_2_EC)))) + "\n" +
//                        "<0x09>EC3           :  " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Slab_3_EC)))) + "\n" +
//                        "<0x09>EC4           :  " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Slab_4_EC)))) + "\n" +
//                        "<0x09>Energy Chg    :  " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Total_Energy_Charg)))) + "\n" +
//                        "<0x09>FCA CHARGE    :  " + (String.format("%1$6s",Structbilling.O_Total_Fixed_Charges)) + "\n" +
//                        "<0x09>DUTY CHARGE   :  " + (String.format("%1$6s",Structbilling.O_ElectricityDutyCharges)) + "\n" +
//                        "<0x09>METER RENT    :  " + (String.format("%1$6s",Structbilling.O_MeterRent)) + "\n" +
//                        "<0x09>SURCHARGES    :  " + (String.format("%1$6s",Structbilling.O_Surcharge)) + "\n" +
//                        "<0x09>MSC CHARG     :  " + (String.format("%1$6s",(String.format("%.2f", Structconsmas.misc_charges)))) + "\n" +
//                        "<0x09>INCENTIVES/PENALITIES :" + (String.format("%1$6s",Structbilling.O_Total_Incentives)) + "\n" +                  // doubt
//                        "<0x09>" + "\n" +
//                        "<0x10>CUR BILL AMT  :  " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Cur_Bill_Total)))) + "\n" +
//                        "<0x09>SD BILLED     :  " + (String.format("%1$6s",Structconsmas.SD_BILLED))+ "\n" +
//                        "<0x09>SD ARREARS    :  " + (String.format("%1$6s",Structconsmas.SD_ARREAR)) + "\n" +
//                        "<0x09>PRE ARREARS   :" + (String.format("%1$6s",Structconsmas.Cur_Fiancial_Yr_Arr)) + "\n" +
//                        "<0x09>ADJ AMT       :" + (String.format("%1$6s",(String.format("%.2f", Structconsmas.Sundry_Allow_EC)))) + "\n" +
//                        "<0x09>SD INT        :" + (String.format("%1$6s",Structconsmas.SD_INSTT_AMT)) + "\n" +
//                        "<0x09>DIFF AMT      : " + (String.format("%1$6s","0.00")) + "\n" +                                                                // No diff Amount
//                        "<0x09>ROUND AMT     :" + (String.format("%1$6s",Structbilling.Round_Amnt)) + "\n" +
////                    "<0x09>Upt(" + Structbilling.Due_Date + "):" + (String.format("%.2f", Structbilling.Amnt_bPaid_on_Rbt_Date)) + "\n" +
////                    "<0x09>Aft(" + Structbilling.Due_Date.trim() + "):" + (String.format("%.2f", Structbilling.Amnt_Paidafter_Rbt_Date)) + "\n" +
//                        "<0x09>TOT AMT       :" + (String.format("%1$6s",Structbilling.O_Total_Bill)) + "\n" +
//                        "<0x09>DPS AMT       :" + (String.format("%1$6s",Structbilling.Delay_Pay_Surcharge)) + "\n" +
//                        "<0x09>....................." + "\n" +
//                        "<0x10>PAY BL BY DUE DT   :" + (String.format("%1$6s",(String.format("%.2f",(Double.valueOf(Structbilling.O_Total_Bill) - Double.valueOf(Structbilling.O_Surcharge)))))) + "\n" +  // doubt
//                        "<0x10>PAY BL AFTER DUE DT:" + (String.format("%1$6s",Structbilling.O_Total_Bill)) + "\n" +
//                        "<0x10>CHQ/ONLINE DUE DT  :" + (String.format("%1$6s",Structconsmas.FIRST_CHQ_DUE_DATE)) + "\n" +
//                        "<0x10>CASH DUE DT        :" + (String.format("%1$6s",Structconsmas.FIRST_CASH_DUE_DATE)) +"\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>" + "\n" +
//                        "<0x09>" + "\n" +
//                        "<0x10>          RECEIPT        " + "\n" +
//                        "<0x09>BILL MON :" +  (String.format("%1$6s",Structconsmas.Bill_Mon)) + "\n" +
//                        "<0x09>DIV      :" +  (String.format("%1$6s",Structconsmas.Division_Name)) +"\n" +
//                        "<0x09>ACC ID   :" + (String.format("%1$6s",Structconsmas.Consumer_Number)) + "\n" +
//                        "<0x09>DIVISION :" + (String.format("%1$6s",Structconsmas.Division_Name)) + "\n" +
//                        "<0x09>BILL DATE :" + (String.format("%1$6s",Structbilling.Bill_Date)) + "\n" +
//                        "<0x09>IVRS/SC NO :" + (String.format("%1$6s",Structconsmas.Consumer_Number + "" + Structconsmas.LOC_CD)) + "\n" +
//                        "<0x10>TOTAL      :" + (String.format("%1$6s",(String.format("%.2f", Structbilling.Cur_Bill_Total)))) + "\n");     // doubt
//            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            sDialog = new SweetAlertDialog(DuplicateBillPrint.this, SweetAlertDialog.SUCCESS_TYPE);
            sDialog.setTitleText("Bluetooth Printer");
            sDialog.setContentText("Successfully Printed");
            sDialog.show();
            sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismissWithAnimation();

                    UtilAppCommon ucom = new UtilAppCommon();
                    ucom.nullyfimodelCon();
                    ucom.nullyfimodelBill();

//                        Toast.makeText(DuplicateBillPrint.this, "Internally stored due to no connectivity", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(DuplicateBillPrint.this, BillingtypesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("printtype", print_type);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);
//                    }
                }
            });


//            sDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {


        }

    }/*--------------------- INDORE Bill Print ----------------------------------*/

    private class PrintProcessBHO extends AsyncTask<String, Void, String> {
        /* displays the progress dialog until background task is completed*/
        private SimpleDateFormat simpleDateFormat;

        private Date connvertedDate;
        private DateFormat dateFormat;
        private String actualDate;

        public String fixedLengthString(String str, int leng) {
            for (int i = str.length(); i <= leng; i++)
                str += " ";
            return str;
        }


        @Override
        protected String doInBackground(String... params) {
            appcomUtil = new UtilAppCommon();
            actualDate = Structconsmas.Bill_Mon;
            simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = null;
            String newDate = null;

            try {
                date = simpleDateFormat.parse(actualDate);
                SimpleDateFormat postFormater = new SimpleDateFormat("dd-MMM-yyyy");
                newDate = postFormater.format(date).trim().substring(3);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Structbilling.DCNumber = "";
            billseq = appcomUtil.findSequence(getApplicationContext(), "BillNumber");

            UtilAppCommon uc = new UtilAppCommon();
            uc.print_Bill();
            uc.princonmas();

            dcSeq = appcomUtil.findSequence(getApplicationContext(), "DCNumber");
            strdcSeq = String.format("%05d", dcSeq);
            Structbilling.DCNumber = key + "/DN" + dcSeq;

            if (Structbilling.md_input != null) {
                mdValue = Structbilling.md_input + " " + "KW";
            } else {
                mdValue = "0" + " " + GSBilling.getInstance().getUnitMaxDemand();
            }

            if (GSBilling.getInstance().getPowerFactor() == 0) {
                pfValue = "0";
            } else {
                pfValue = String.valueOf(GSBilling.getInstance().getPowerFactor());
            }

            if (Structconsmas.misc_charges != null) {
                miscValue = Structconsmas.misc_charges;
            } else {
                miscValue = 0.0f;
            }

            String printtariif = (Structconsmas.Tariff_Code + "-" + (((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.Tariff_URBAN : Structtariff.Tariff_RURAL)));
            String consumerName = Structconsmas.Name;
            if (Structconsmas.Name.length() < 22) {
                //   consumerName = Structconsmas.Name + "\n";
                consumerName = fixedLengthString(consumerName, 43);// + "\n";
            } else {
                consumerName = fixedLengthString(consumerName, 43);// + "\n";
                consumerName = consumerName.substring(0, 41);
            }
            Structconsmas.Name = consumerName;
            String meterReaderName = Structbilling.Meter_Reader_Name;
            if (Structbilling.Meter_Reader_Name.length() < 20) {
                meterReaderName = Structbilling.Meter_Reader_Name;
            } else {
                meterReaderName = Structbilling.Meter_Reader_Name.substring(0, 18);
            }
            String feederDesc = Structconsmas.FDR_SHRT_DESC;
            if (Structconsmas.FDR_SHRT_DESC.length() < 13) {
                feederDesc = Structconsmas.FDR_SHRT_DESC;
            } else {
                feederDesc = Structconsmas.FDR_SHRT_DESC.substring(0, 12);
            }
            String address = Structconsmas.address1 + Structconsmas.address2 +Structconsmas.MOH;
            String subadd1;
            String subadd2;
            String PrinDate;

            PrinDate = Structbilling.Cur_Meter_Reading_Date.substring(0, 6) + Structbilling.Cur_Meter_Reading_Date.substring(9);

            if (address.length() < 22) {
                address = fixedLengthString(Structconsmas.address1 + Structconsmas.address2, 42);// + "\n";
            } else {
                if (address.length() >= 45) {
                    subadd1 = address.substring(0, 42);
                } else {
                    subadd1 = address;
                }

                address = fixedLengthString(subadd1, 41);

            }
            String sectionName = Structconsmas.Section_Name;
            if (sectionName.length() >= 19) {
                sectionName = Structconsmas.Section_Name.substring(0, 19);
            } else {
                sectionName = Structconsmas.Section_Name;
            }


            Prev_Meter_Reading =Structconsmas.Prev_Meter_Reading;
            Cur_Meter_Reading = Structbilling.Cur_Meter_Reading;
            Bill_Basis = Structbilling.Bill_Basis;
            Reading_type_code = RDG_TYP_CD_Print();

            if(Structbilling.Reasons != null && !Structbilling.Reasons .isEmpty()){

                if(Structbilling.Reasons.equalsIgnoreCase("Previous reading is higher than Current reading")){
                    Prev_Meter_Reading = Structconsmas.Prev_Meter_Reading;
                    Cur_Meter_Reading = Structconsmas.Prev_Meter_Reading;
                    Bill_Basis = "ACT";
                    Reading_type_code = "NORMAL";

                }

            }

            Calendar c1 = Calendar.getInstance();
            SimpleDateFormat fds2 = new SimpleDateFormat("kk:mm");
            billTime = fds2.format(c1.getTime());


            if (Structconsmas.Name.length() < 23 && address.length() < 23) {
                mSendMessage("<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "     " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" + //201706
                        "<0x09>" + "     " + (String.format("%1$6s", PrinDate)) + "  " + billTime + "\n" +
                        "<0x09>" + "     " + (String.format("%1$6s", Structbilling.Bill_No)) + "\n" +
                        "<0x09>" + "  " + (String.format("%1$6s", Structbilling.Bill_Period)) + "\n" +
                        "<0x09>" + "  " + "\n" +
                        "<0x09>" + "  " + "\n" +
                        "<0x09>" + "    " + (String.format("%1$6s", Structconsmas.DIV_NAME)) + "\n" +
                        "<0x09>" + "     " + (String.format("%1$6s", sectionName)) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$8s", Structconsmas.Cycle)) + "/" + Structconsmas.Route_Number + "\n" +
                        "<0x09>" + "     " + (String.format("%1$6s", feederDesc)) + "\n" +
                        "<0x09>" + "        " + (String.format("%1$9s", Structconsmas.POLE_ID)) + "\n" +
                        "<0x09>" + "       " + (String.format("%1$8s", appcomUtil.IVRS_NO_PRINT())) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                        "<0x09>" + "  " + "\n" +
                        "<0x09>" + "  " + (String.format("%1$6s", consumerName)) + "\n" +
                        "<0x09>" + "  " + (String.format("%1$6s", address)) + "\n" +
                        "<0x09>" + "    " + (String.format("%1$8s", printtariif())) + "\n" +
                        "<0x09>" + "   " + (String.format("%1$8s", Structconsmas.Load)) + getLoadType() + "\n" +
                        "<0x09>" + "      " + (String.format("%1$8s", Structconsmas.TOT_SD_HELD)) + "\n" +
                        "<0x09>" + "       " + (String.format("%1$8s", Structconsmas.Meter_S_No)) + "\n" +
                        "<0x09>" + "    " + (String.format("%1$8s", Structconsmas.MF)) + " / " + Structconsmas.PHASE_CD + "\n" +
                        "<0x09>" + "            " + (String.format("%1$4s", Structbilling.Cur_Meter_Reading_Date)) + "\n" +
                        "<0x09>" + "   " + (String.format("%1$8s", Reading_type_code)) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", Cur_Meter_Reading)) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", Prev_Meter_Reading)) + "\n" +
                        "<0x09>" + "  " + "\n" +
                        "<0x09>" + "     " + (String.format("%1$8s", mdValue)) + "      " + Structbilling.Avrg_PF + "\n" +
                        "<0x09>" + "           " + (String.format("%1$8s", dotSeparate(Structbilling.Billed_Units) + "/" + dotSeparate(Structbilling.O_BilledUnit_Actual))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", Bill_Basis)) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.MIN_CHRG))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.FIXED_CHARGE))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_FCA))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Structbilling.Meter_Rent)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structconsmas.Sundry_Allow_EC))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_OverdrwalPenalty))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_PFPenalty))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_welding_charges))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(UtilAppCommon.doublecheck(Structconsmas.SD_BILLED)))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(0))))) + "\n" +
                        "<0x09>" + "  " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structconsmas.SD_INSTT_AMT) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_LockCreditAmount) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Employee_Incentive) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Incentives) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Subsidy) * -1f)))) + "\n" +
//                        "<0x09>" + "  " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Bill) - Float.valueOf(Structbilling.O_Arrear_Demand) + Float.valueOf(Structconsmas.Sundry_Allow_EC))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structconsmas.Cur_Fiancial_Yr_Arr))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(printSurcharge()))))) + "\n" +
                        "<0x10>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", Structbilling.O_Surcharge)) + "\n" +
                        "<0x10>" + "             " + (String.format("%1$10s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" +  // doubt
                        "<0x09>" + "              " + (String.format("%1$6s", Structconsmas.FIRST_CHQ_DUE_DATE)) + "\n" +
                        "<0x09>" + "              " + (String.format("%1$6s", Structconsmas.FIRST_CASH_DUE_DATE)) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "         " + (String.format("%1$4s", meterReaderName)) + " " + (String.format("%1$6s", Structbilling.SBM_No)) + " " + Structbilling.VER_CODE + " " + (String.format("%1$6s", "Bill")) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$6s", Structconsmas.DIV_NAME)) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$6s", sectionName)) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "        " + (String.format("%1$8s", Structconsmas.Cycle)) + "/" + Structconsmas.Route_Number + "\n" +
                        "<0x09>" + "        " + (String.format("%1$8s", appcomUtil.IVRS_NO_PRINT())) + "\n" +
                        "<0x09>" + "        " + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "    " + (String.format("%1$6s", consumerName)) + "\n" +
                        "<0x10>" + "               " + (String.format("%1$6s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                        "<0x10>" + "               " + (String.format("%1$6s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" + // doubt
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n");

            } else if (Structconsmas.Name.length() < 23 && address.length() > 23) {
                mSendMessage("<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "     " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" + //201706
                        "<0x09>" + "     " + (String.format("%1$6s", PrinDate)) + "  " + billTime + "\n" +
                        "<0x09>" + "     " + (String.format("%1$6s", Structbilling.Bill_No)) + "\n" +
                        "<0x09>" + "  " + (String.format("%1$6s", Structbilling.Bill_Period)) + "\n" +
                        "<0x09>" + "  " + "\n" +
                        "<0x09>" + "  " + "\n" +
                        "<0x09>" + "    " + (String.format("%1$6s", Structconsmas.DIV_NAME)) + "\n" +
                        "<0x09>" + "     " + (String.format("%1$6s", sectionName)) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$8s", Structconsmas.Cycle)) + "/" + Structconsmas.Route_Number + "\n" +
                        "<0x09>" + "     " + (String.format("%1$6s", feederDesc)) + "\n" +
                        "<0x09>" + "        " + (String.format("%1$9s", Structconsmas.POLE_ID)) + "\n" +
                        "<0x09>" + "       " + (String.format("%1$8s", appcomUtil.IVRS_NO_PRINT())) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                        "<0x09>" + "  " + "\n" +
                        "<0x09>" + "  " + (String.format("%1$6s", consumerName)) + "\n" +
                        "<0x09>" + "  " + (String.format("%1$6s", address)) + "\n" +
                        "<0x09>" + "    " + (String.format("%1$8s", printtariif())) + "\n" +
                        "<0x09>" + "   " + (String.format("%1$8s", Structconsmas.Load)) + getLoadType() + "\n" +
                        "<0x09>" + "      " + (String.format("%1$8s", Structconsmas.TOT_SD_HELD)) + "\n" +
                        "<0x09>" + "       " + (String.format("%1$8s", Structconsmas.Meter_S_No)) + "\n" +
                        "<0x09>" + "    " + (String.format("%1$8s", Structconsmas.MF)) + " / " + Structconsmas.PHASE_CD + "\n" +
                        "<0x09>" + "            " + (String.format("%1$4s", Structbilling.Cur_Meter_Reading_Date)) + "\n" +
                        "<0x09>" + "   " + (String.format("%1$8s", Reading_type_code)) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", Cur_Meter_Reading)) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", Prev_Meter_Reading)) + "\n" +
                        "<0x09>" + "  " + "\n" +
                        "<0x09>" + "     " + (String.format("%1$8s", mdValue)) + "      " + Structbilling.Avrg_PF + "\n" +
                        "<0x09>" + "           " + (String.format("%1$8s", dotSeparate(Structbilling.Billed_Units) + "/" + dotSeparate(Structbilling.O_BilledUnit_Actual))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", Bill_Basis)) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.MIN_CHRG))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.FIXED_CHARGE))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_FCA))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Structbilling.Meter_Rent)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structconsmas.Sundry_Allow_EC))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_OverdrwalPenalty))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_PFPenalty))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_welding_charges))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(UtilAppCommon.doublecheck(Structconsmas.SD_BILLED)))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(0))))) + "\n" +
                        "<0x09>" + "  " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structconsmas.SD_INSTT_AMT) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_LockCreditAmount) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Employee_Incentive) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Incentives) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Subsidy) * -1f)))) + "\n" +
//                        "<0x09>" + "  " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Bill) - Float.valueOf(Structbilling.O_Arrear_Demand) + Float.valueOf(Structconsmas.Sundry_Allow_EC))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structconsmas.Cur_Fiancial_Yr_Arr))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(printSurcharge()))))) + "\n" +
                        "<0x10>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                        "<0x09>" + "  " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", Structbilling.O_Surcharge)) + "\n" +
                        "<0x10>" + "             " + (String.format("%1$10s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" +  // doubt
                        "<0x09>" + "              " + (String.format("%1$6s", Structconsmas.FIRST_CHQ_DUE_DATE)) + "\n" +
                        "<0x09>" + "              " + (String.format("%1$6s", Structconsmas.FIRST_CASH_DUE_DATE)) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "         " + (String.format("%1$4s", meterReaderName)) + " " + (String.format("%1$6s", Structbilling.SBM_No)) + " " + Structbilling.VER_CODE + " " + (String.format("%1$6s", "Bill")) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$6s", Structconsmas.DIV_NAME)) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$6s", sectionName)) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "        " + (String.format("%1$8s", Structconsmas.Cycle)) + "/" + Structconsmas.Route_Number + "\n" +
                        "<0x09>" + "        " + (String.format("%1$8s", appcomUtil.IVRS_NO_PRINT())) + "\n" +
                        "<0x09>" + "        " + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "    " + (String.format("%1$6s", consumerName)) + "\n" +
                        "<0x10>" + "               " + (String.format("%1$6s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                        "<0x10>" + "               " + (String.format("%1$6s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" + // doubt
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n");
            } else {
                mSendMessage("<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "     " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" + //201706
                        "<0x09>" + "     " + (String.format("%1$6s", PrinDate)) + "  " + billTime + "\n" +
                        "<0x09>" + "     " + (String.format("%1$6s", Structbilling.Bill_No)) + "\n" +
                        "<0x09>" + "  " + (String.format("%1$6s", Structbilling.Bill_Period)) + "\n" +
                        "<0x09>" + "  " + "\n" +
                        "<0x09>" + "  " + "\n" +
                        "<0x09>" + "    " + (String.format("%1$6s", Structconsmas.DIV_NAME)) + "\n" +
                        "<0x09>" + "     " + (String.format("%1$6s", sectionName)) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$8s", Structconsmas.Cycle)) + "/" + Structconsmas.Route_Number + "\n" +
                        "<0x09>" + "     " + (String.format("%1$6s", feederDesc)) + "\n" +
                        "<0x09>" + "        " + (String.format("%1$9s", Structconsmas.POLE_ID)) + "\n" +
                        "<0x09>" + "       " + (String.format("%1$8s", appcomUtil.IVRS_NO_PRINT())) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                        "<0x09>" + "  " + (String.format("%1$6s", consumerName)) + "\n" +
                        "<0x09>" + "  " + (String.format("%1$6s", address)) + "\n" +
                        "<0x09>" + "    " + (String.format("%1$8s", printtariif())) + "\n" +
                        "<0x09>" + "   " + (String.format("%1$8s", Structconsmas.Load)) + getLoadType() + "\n" +
                        "<0x09>" + "      " + (String.format("%1$8s", Structconsmas.TOT_SD_HELD)) + "\n" +
                        "<0x09>" + "       " + (String.format("%1$8s", Structconsmas.Meter_S_No)) + "\n" +
                        "<0x09>" + "    " + (String.format("%1$8s", Structconsmas.MF)) + " / " + Structconsmas.PHASE_CD + "\n" +
                        "<0x09>" + "            " + (String.format("%1$4s", Structbilling.Cur_Meter_Reading_Date)) + "\n" +
                        "<0x09>" + "   " + (String.format("%1$8s", Reading_type_code)) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", Cur_Meter_Reading)) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", Prev_Meter_Reading)) + "\n" +
                        "<0x09>" + "  " + "\n" +
                        "<0x09>" + "     " + (String.format("%1$8s", mdValue)) + "      " + Structbilling.Avrg_PF + "\n" +
                        "<0x09>" + "           " + (String.format("%1$8s", dotSeparate(Structbilling.Billed_Units) + "/" + dotSeparate(Structbilling.O_BilledUnit_Actual))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", Bill_Basis)) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.MIN_CHRG))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.FIXED_CHARGE))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_FCA))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Structbilling.Meter_Rent)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structconsmas.Sundry_Allow_EC))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_OverdrwalPenalty))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_PFPenalty))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_welding_charges))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(UtilAppCommon.doublecheck(Structconsmas.SD_BILLED)))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(0))))) + "\n" +
                        "<0x09>" + "  " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structconsmas.SD_INSTT_AMT) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_LockCreditAmount) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Employee_Incentive) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Incentives) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Subsidy) * -1f)))) + "\n" +
//                        "<0x09>" + "  " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Bill) - Float.valueOf(Structbilling.O_Arrear_Demand) + Float.valueOf(Structconsmas.Sundry_Allow_EC))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structconsmas.Cur_Fiancial_Yr_Arr))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(printSurcharge()))))) + "\n" +
                        "<0x10>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                        "<0x09>" + "  " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", Structbilling.O_Surcharge)) + "\n" +
                        "<0x10>" + "             " + (String.format("%1$10s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" +  // doubt
                        "<0x09>" + "              " + (String.format("%1$6s", Structconsmas.FIRST_CHQ_DUE_DATE)) + "\n" +
                        "<0x09>" + "              " + (String.format("%1$6s", Structconsmas.FIRST_CASH_DUE_DATE)) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "         " + (String.format("%1$4s", meterReaderName)) + " " + (String.format("%1$6s", Structbilling.SBM_No)) + " " + Structbilling.VER_CODE + " " + (String.format("%1$6s", "Bill")) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$6s", Structconsmas.DIV_NAME)) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$6s", sectionName)) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "        " + (String.format("%1$8s", Structconsmas.Cycle)) + "/" + Structconsmas.Route_Number + "\n" +
                        "<0x09>" + "        " + (String.format("%1$8s", appcomUtil.IVRS_NO_PRINT())) + "\n" +
                        "<0x09>" + "        " + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "    " + (String.format("%1$6s", consumerName)) + "\n" +
                        "<0x10>" + "               " + (String.format("%1$6s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                        "<0x10>" + "               " + (String.format("%1$6s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" + // doubt
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n");
            }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            sDialog = new SweetAlertDialog(DuplicateBillPrint.this, SweetAlertDialog.SUCCESS_TYPE);
            sDialog.setTitleText("Bluetooth Printer");
            sDialog.setContentText("Successfully Printed");
            sDialog.show();
            sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismissWithAnimation();

                    UtilAppCommon ucom = new UtilAppCommon();
                    ucom.nullyfimodelCon();
                    ucom.nullyfimodelBill();


                    Intent intent = new Intent(DuplicateBillPrint.this, BillingtypesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("printtype", print_type);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);
                }
            });


        }

        @Override
        protected void onPreExecute() {


        }


    }/*--------------------- BHOPAL Bill Print ----------------------------------*/

    private class PrintProcessJBL extends AsyncTask<String, Void, String> {
        /* displays the progress dialog until background task is completed*/
        private SimpleDateFormat simpleDateFormat;

        private Date connvertedDate;
        private DateFormat dateFormat;
        private String actualDate;

        @Override
        protected String doInBackground(String... params) {
            appcomUtil = new UtilAppCommon();
            actualDate = Structconsmas.Bill_Mon;
            simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = null;
            String newDate = null;

            try {
                date = simpleDateFormat.parse(actualDate);
                SimpleDateFormat postFormater = new SimpleDateFormat("dd-MMM-yyyy");
                newDate = postFormater.format(date).trim().substring(3);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Structbilling.DCNumber = "";
            billseq = appcomUtil.findSequence(getApplicationContext(), "BillNumber");

            UtilAppCommon uc = new UtilAppCommon();
            uc.print_Bill();
            uc.princonmas();

            dcSeq = appcomUtil.findSequence(getApplicationContext(), "DCNumber");
            strdcSeq = String.format("%05d", dcSeq);
            Structbilling.DCNumber = key + "/DN" + dcSeq;

            if (Structbilling.md_input != null) {
                mdValue = Structbilling.md_input + " " + "KW";
            } else {
                mdValue = "0" + " " + GSBilling.getInstance().getUnitMaxDemand();
            }

            if (GSBilling.getInstance().getPowerFactor() == 0) {
                pfValue = "0";
            } else {
                pfValue = String.valueOf(GSBilling.getInstance().getPowerFactor());
            }

            if (Structconsmas.misc_charges != null) {
                miscValue = Structconsmas.misc_charges;
            } else {
                miscValue = 0.0f;
            }

            String printtariif = (Structconsmas.Tariff_Code + "-" + (((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.Tariff_URBAN : Structtariff.Tariff_RURAL)));
            String consumerName = Structconsmas.Name;

            String meterReaderName = Structbilling.Meter_Reader_Name;
            if (Structbilling.Meter_Reader_Name.length() < 20) {
                meterReaderName = Structbilling.Meter_Reader_Name;
            } else {
                meterReaderName = Structbilling.Meter_Reader_Name.substring(0, 18);
            }
            String feederDesc = Structconsmas.FDR_SHRT_DESC;
            if (Structconsmas.FDR_SHRT_DESC.length() < 13) {
                feederDesc = Structconsmas.FDR_SHRT_DESC;
            } else {
                feederDesc = Structconsmas.FDR_SHRT_DESC.substring(0, 12);
            }
            String address = Structconsmas.address1 + Structconsmas.address2 +Structconsmas.MOH;
            if (address.length() < 25) {
                address = Structconsmas.address1 + Structconsmas.address2 + "\n";
            } else {
                address = Structconsmas.address1 + Structconsmas.address2;
            }
            String sectionName = Structconsmas.Section_Name;
            if (sectionName.length() >= 19) {
                sectionName = Structconsmas.Section_Name.substring(0, 19);
            } else {
                sectionName = Structconsmas.Section_Name;
            }
            String PrinDate;

            PrinDate = Structbilling.Cur_Meter_Reading_Date.substring(0, 6) + Structbilling.Cur_Meter_Reading_Date.substring(9);
            Prev_Meter_Reading =Structconsmas.Prev_Meter_Reading;
            Cur_Meter_Reading = Structbilling.Cur_Meter_Reading;
            Bill_Basis = Structbilling.Bill_Basis;
            Reading_type_code = RDG_TYP_CD_Print();

            if(Structbilling.Reasons != null && !Structbilling.Reasons .isEmpty()){

                if(Structbilling.Reasons.equalsIgnoreCase("Previous reading is higher than Current reading")){
                    Prev_Meter_Reading = Structconsmas.Prev_Meter_Reading;
                    Cur_Meter_Reading = Structconsmas.Prev_Meter_Reading;
                    Bill_Basis = "ACT";
                    Reading_type_code = "NORMAL";

                }

            }

            Calendar c1 = Calendar.getInstance();
            SimpleDateFormat fds2 = new SimpleDateFormat("kk:mm");
            billTime = fds2.format(c1.getTime());
//            String printtariif = (Structconsmas.Tariff_Code + "-" + (((Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) ? Structtariff.Tariff_URBAN : Structtariff.Tariff_RURAL)));

//            mSendMessage(
//                    "<0x09>     MADHYA PRADESH" + "\n" +
//                            "<0x09>     M.P.M.K.V.V.C.L" + "\n" +
//                            "<0x09>    ELECTRICITY BILL" + "\n" +
//                            "<0x09>-----------------------" + "\n" +
//                            "<0x09> CUSTOMER CARE NUMBERS" + "\n" +
//                            "<0x09>-----------------------" + "\n" +
//                            "<0x09>    1800-233-1912" + "\n" +
//                            //"<0x10> 0755-2551222" + "\n" +
//                            "<0x09>-----------------------" + "\n" +
//
//                            "<0x09>BILL MONTH : " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" + //201706
//                            "<0x09>BILL DATE  : " + (String.format("%1$6s", Structbilling.Bill_Date)) + "\n" +
//                            "<0x09>BILL NO    : " + (String.format("%1$6s", Structbilling.Bill_No)) + "\n" +
//                            "<0x09>BILL PERIOD: " + (String.format("%1$6s", Structbilling.Bill_Period)) + "\n" +
//                            //  "<0x09>MACHINE NO : " + (String.format("%1$6s",Structbilling.SBM_No)) + "\n" +
//                            //"<0x09>MR NAME: " + "\n" +
//                            //"<0x09>" + (String.format("%1$6s",Structbilling.Meter_Reader_Name)) + "\n" +
//                            //"<0x09>MR ID       : " + (String.format("%1$4s",Structbilling.Meter_Reader_ID)) + "\n" +
//
//                            "<0x09>-----------------------" + "\n" +
//
//                            "<0x09>DIVISION   : " + (String.format("%1$6s", "BHOPAL CITY")) + "\n" +
//                            "<0x09>DC NAME    : " + (String.format("%1$6s", sectionName)) + "\n" +
//                            "<0x09>GROUP/DAIRY NO     : " + (String.format("%1$8s", Structconsmas.Cycle)) + "/" + (String.format("%1$8s", Structconsmas.Route_Number)) + "\n" +
//                            "<0x09>FDR DESC  : " + (String.format("%1$6s", Structconsmas.FDR_SHRT_DESC)) + "\n" +
//                            "<0x09>POLE ID   : " + (String.format("%1$9s", Structconsmas.POLE_ID)) + "\n" +
//                            "<0x09>ACC ID    : " + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
//                            "<0x09>IVRS/SC NO: " + (String.format("%1$8s", Structconsmas.LOC_CD + "-" + Structconsmas.Consumer_Number)) + "\n" +
////                    "<0x09>SUB-DIV    : " + Structconsmas.Sub_division_Name + "\n" +
////                    "<0x09>SECTION    : " + Structconsmas.Section_Name + "\n" +
//                            "<0x09>NAME      : " + (String.format("%1$6s", Structconsmas.Name)) + "\n" +
//                            "<0x10>ADDRESS   : " + "\n" +
//                            "<0x09>" + (String.format("%1$6s", Structconsmas.address1 + Structconsmas.address2)) + "\n" +
//                            "<0x09>-----------------------" + "\n" +
//
////                            "<0x09>TARIFF CATG  : " + (String.format("%1$8s",Structconsmas.Tariff_Code)) + "\n" +
//                            "<0x09>TARIFF CATG  : " + (String.format("%1$8s", printtariif)) + "\n" +
//                            "<0x09>LOAD         : " + (String.format("%1$8s", Structconsmas.Load)) + getLoadType() + "\n" +
//                            "<0x09>SECURITY DEPS: " + (String.format("%1$8s", Structconsmas.TOT_SD_HELD)) + "\n" +
//                            //   "<0x09>DAIRY NO     : " + (String.format("%1$8s",Structconsmas.Route_Number)) + "\n" +
//                            "<0x09>MTR SL NO    : " + (String.format("%1$8s", Structconsmas.Meter_S_No)) + "\n" +
////                        "<0x09>MTR OWNER    : " + (String.format("%1$6s",Structconsmas.Meter_Ownership)) + "\n" +
////                        "<0x09>MAKE         : "  + "\n" +
//                            "<0x09>MF           : " + (String.format("%1$8s", Structconsmas.MF)) + "\n" +
//                            "<0x09>PHASE        : " + (String.format("%1$8s", Structconsmas.PHASE_CD)) + "\n" +
//                            "<0x09>---------------------" + "\n" +
//                            "<0x09>READING DATE :" + (String.format("%1$4s", Structbilling.Cur_Meter_Reading_Date)) + "\n" +
//                            // "<0x09>PREV AVG UNIT: " + (String.format("%1$8s",Structconsmas.PREV_AVG_UNIT)) + "\n" +
//                            "<0x09>READING STS  : " + (String.format("%1$8s", Structbilling.Bill_Basis)) + "\n" +
////                    "<0x09>PRES.BL DATE :" + Structbilling.Bill_Date + "\n" +
////                    "<0x09>PREV.BL DATE :" + Structconsmas.Prev_Meter_Reading_Date + "\n" +
//                            "<0x09>PRES READING : " + (String.format("%1$8s", Structbilling.Cur_Meter_Reading)) + "\n" +
//                            "<0x09>PREV READING : " + (String.format("%1$8s", Structconsmas.Prev_Meter_Reading)) + "\n" +
//                            "<0x09>MD           : " + (String.format("%1$8s", mdValue)) + "\n" +
//                            "<0x09>PF           : " + (String.format("%1$8s", pfValue)) + "\n" +
//                            //  "<0x09>AVG UNITS    : " + (String.format("%1$8s",Structbilling.Units_Consumed)) + "\n" +
//                            "<0x09>MTR CONSP    : " + (String.format("%1$8s", Structbilling.MTR_CONSMP)) + "\n" +
//                            "<0x09>UNIT BILLED  : " + (String.format("%1$8s", Structbilling.O_BilledUnit_Actual)) + "\n" +
//                            "<0x09>BILL TYPE    : " + (String.format("%1$8s", Structbilling.Bill_Basis)) + "\n" +
//                            "<0x09>---------------------" + "\n" +
//                            "<0x09>MIN CHARG    : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.MIN_CHRG))))) + "\n" +
//                            "<0x09>FIXED CHARG  : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.FIXED_CHARGE))))) + "\n" +
////                    "<0x09>Current Bill   " + "\n" +
////                    "<0x09>---------------" + "\n" +
////                    "<0x09>Current D.P.S :  " + (String.format("%.2f", Structconsmas.Delay_Payment_Surcharge)) + "\n" +
////                    "<0x09>Fix/Mis Chg   :  " + (String.format("%.2f", Structbilling.Monthly_Min_Charg_DC)) + "\n" +
//                            // "<0x09>EC1          : " + (String.format("%1$8s",(String.format("%.2f", Structbilling.Slab_1_EC)))) + "\n" +
//                            //"<0x09>EC2          : " + (String.format("%1$8s",(String.format("%.2f", Structbilling.Slab_2_EC)))) + "\n" +
//                            //"<0x09>EC3          : " + (String.format("%1$8s",(String.format("%.2f", Structbilling.Slab_3_EC)))) + "\n" +
//                            //"<0x09>EC4          : " + (String.format("%1$8s",(String.format("%.2f", Structbilling.Slab_4_EC)))) + "\n" +
//                            "<0x09>ENERGY CHRG  : " + (String.format("%1$8s", (String.format("%.2f", Structbilling.Total_Energy_Charg)))) + "\n" +
//                            "<0x09>FCA CHARG    : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_FCA))))) + "\n" +
//                            "<0x09>---------------------" + "\n" +
////                        "<0x09>FIX  CHARGE   :  " + (String.format("%1$6s",Structbilling.O_Total_Fixed_Charges)) + "\n" +
//                            "<0x09>DUTY CHARGE  : " + (String.format("%1$8s", (String.format("%.2f", Structbilling.Electricity_Duty_Charges)))) + "\n" +
//                            "<0x09>METER RENT   : " + (String.format("%1$8s", (String.format("%.2f", Structbilling.Meter_Rent)))) + "\n" +
//                            "<0x09>ADJ AMT      : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structconsmas.Sundry_Allow_EC))))) + "\n" +
//                            "<0x09>PANEL CHRG   : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_OverdrwalPenalty))))) + "\n" +
//                            "<0x09>P.F CHRG     : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_PFPenalty))))) + "\n" +
////                            "<0x09>P.F CHRG     : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf("0"))))) + "\n" + //JUGAD
//                            "<0x09>TOD CHRG     : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(0))))) + "\n" +
//                            "<0x09>SD BILLED    : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(UtilAppCommon.doublecheck(Structconsmas.SD_BILLED)))))) + "\n" +
//                            "<0x09>OTHER CHRG   : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_welding_charges))))) + "\n" +
//                            "<0x09>------------------" + "\n" +
//                            "<0x09>SD INT       : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf("-"+Structconsmas.SD_INSTT_AMT))))) + "\n" +
//                            "<0x09>LOCK CR      : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_LockCreditAmount))))) + "\n" +
//                            "<0x09>EMP. REBATE  : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_Employee_Incentive))))) + "\n" +
//                            "<0x09>INCENTIVES   : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf("-"+Structbilling.Prinatable_Total_Incentives))))) + "\n" +
//                            "<0x09>SUBSIDY      : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Subsidy))))) + "\n" +
//                            "<0x09>------------------" + "\n" +
//                            "<0x09>CUR BILL AMT : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_Current_Demand))))) + "\n" +
//                            "<0x09>PREV ARREARS : " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_Arrear_Demand))))) + "\n" +
//                            "<0x09>SURCHARGE ARS: " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structconsmas.SURCHARGE_ARREARS))))) + "\n" +
//                            "<0x09>PREV BILL DIFF. AMT.: " + "\n" + (String.format("%1$8s", "0.00")) + "\n" +                                                                // No diff Amount
//
//                            // "<0x09>MSC CHARG    : " + (String.format("%1$8s",(String.format("%.2f", miscValue)))) + "\n" +
//                            //"<0x09>INCENTIVES/PENALTIES : "  + "\n" + //+ (String.format("%1$6s",(String.format("%.2f", Float.valueOf(Structbilling.O_PFPenalty) + Float.valueOf(Structbilling.O_OverdrwalPenalty) -Float.valueOf(Structbilling.O_Total_Incentives))))) + "\n" +                  // doubt
//                            //"<0x09> " + (String.format("%1$6s",(String.format("%.2f", Float.valueOf(Structbilling.O_PFPenalty) + Float.valueOf(Structbilling.O_OverdrwalPenalty) - Float.valueOf(Structbilling.O_Total_Incentives))))) + "\n" +                  // doubt
//                            // doubt
//                            //    "<0x09>SD ARREARS   : " + (String.format("%1$8s",(String.format("%.2f", Float.valueOf(Structconsmas.SD_ARREAR))))) + "\n" +
//
//
//                            "<0x09>ROUND AMT    : " + (String.format("%1$8s", Structbilling.Round_Amnt)) + "\n" +
////                    "<0x09>Upt(" + Structbilling.Due_Date + "):" + (String.format("%.2f", Structbilling.Amnt_bPaid_on_Rbt_Date)) + "\n" +
////                    "<0x09>Aft(" + Structbilling.Due_Date.trim() + "):" + (String.format("%.2f", Structbilling.Amnt_Paidafter_Rbt_Date)) + "\n" +
//                            //  "<0x09>TOT AMT      : " + (String.format("%1$8s",(String.format("%.2f", Float.valueOf(Math.round(Double.parseDouble(Structbilling.O_Total_Bill))))))) + "\n" +
//                            "<0x10>PAYABLE BY DUE DT:" + "\n" +
//                            "<0x09>" + (String.format("%1$6s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
//
//                            "<0x09>DELAY PAY SURCHARG : " + "\n" +
//                            "<0x09>" + (String.format("%1$6s", Structbilling.O_Surcharge)) + "\n" +
//                            "<0x09>PAYABLE AFTER DUE DT:" + "\n" +
//                            "<0x09>" + (String.format("%1$6s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" +  // doubt
//                            "<0x09>CHEQUE DUE DT      :" + "\n" +
//                            "<0x09>" + (String.format("%1$6s", Structconsmas.FIRST_CHQ_DUE_DATE)) + "\n" +
//                            "<0x09>CASH DUE DT        :" + "\n" +
//                            "<0x09>" + (String.format("%1$6s", Structconsmas.FIRST_CASH_DUE_DATE)) + "\n" +
//                            "<0x09>....................." + "\n" +
//                            "<0x09>MACHINE NO : " + (String.format("%1$6s", Structbilling.SBM_No)) + "\n" +
//                            "<0x09>MR NAME: " + "\n" +
//                            "<0x09>" + (String.format("%1$6s", Structbilling.Meter_Reader_Name)) + "\n" +
//                            "<0x09>MR ID       : " + (String.format("%1$4s", Structbilling.Meter_Reader_ID)) + "\n" +
//
//                            "<0x09>" + " " + "\n" +
//                            "<0x09>" + " " + "\n" +
//                            "<0x09>" + " " + "\n" +
//                            "<0x09>" + " " + "\n" +
//                            "<0x09>" + " " + "\n" +
//                            "<0x09>" + " " + "\n" +
//                            "<0x10>          RECEIPT        " + "\n" +
//                            "<0x09>BILL MONTH : " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" +
//                            "<0x09>BILL DATE  : " + (String.format("%1$6s", Structbilling.Bill_Date)) + "\n" +
//                            "<0x09>BILL NO    : " + (String.format("%1$6s", Structbilling.Bill_No)) + "\n" +
//                            "<0x09>DIVISION   : " + (String.format("%1$6s", "BURHANPUR")) + "\n" +
//                            "<0x09>DC NAME    : " + (String.format("%1$6s", sectionName)) + "\n" +
//                            "<0x09>GROUP/DAIRY NO: " + (String.format("%1$8s", Structconsmas.Cycle)) + "/" + (String.format("%1$8s", Structconsmas.Route_Number)) + "\n" +
//                            "<0x09>ACC ID/SC NO: " + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
//                            "<0x09>IVRS    NO  : " + (String.format("%1$8s", Structconsmas.LOC_CD + "-" + Structconsmas.Consumer_Number)) + "\n" +
////                    "<0x09>SUB-DIV    : " + Structconsmas.Sub_division_Name + "\n" +
////                    "<0x09>SECTION    : " + Structconsmas.Section_Name + "\n" +
//                            "<0x09>CONS NAME        : " + (String.format("%1$6s", Structconsmas.Name)) + "\n" +
//                            "<0x09>PAYABLE BY DUE DT:" + "\n" +
//                            "<0x09>" + (String.format("%1$6s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
//                            "<0x09>PAYABLE AFTER DUE DT:" + "\n" +
//                            "<0x09>" + (String.format("%1$6s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" + // doubt
//                            "<0x09>" + " " + "\n" +
//                            "<0x09>" + " " + "\n" +
//                            "<0x09>" + " " + "\n" +
//                            "<0x09>" + " " + "\n");     // doubt
            if (Structconsmas.Name.length() < 23) {
                mSendMessage("<0x09>" + "     " + "\n" +
                        "<0x09>" + "     " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "     " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" + //201706
                        "<0x09>" + "     " + (String.format("%1$6s", PrinDate)) + "  " + billTime + "\n" +
                        "<0x09>" + "     " + (String.format("%1$6s", Structbilling.Bill_No)) + "\n" +
                        "<0x09>" + "     " + "\n" +
                        "<0x09>" + "    " + (String.format("%1$6s", Structconsmas.DIV_NAME)) + "\n" +
                        "<0x09>" + "   " + (String.format("%1$6s", sectionName)) + "\n" +
                        "<0x09>" + "       " + (String.format("%1$8s", appcomUtil.IVRS_NO_PRINT())) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "  " + (String.format("%1$6s", Structconsmas.Name)) + "\n" +
                        "<0x09>" + "      " + "\n" + "      " + "\n" +
                        "<0x09>" + " " + (String.format("%1$6s", address)) + "\n" +
                        "<0x09>" +         /* ............................................................................................. */
                        "<0x09>" + " " + (String.format("%1$8s", printtariif())) + " " + getLoadType() + " " + (String.format("%1$8s", Structconsmas.Load)) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$8s", Structconsmas.TOT_SD_HELD)) + "\n" +
                        "<0x09>" + "       " + (String.format("%1$8s", Structconsmas.Meter_S_No)) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "    " + (String.format("%1$8s", Structconsmas.MF)) + " / " + Structconsmas.PHASE_CD + "\n" + "\n" +
                        "<0x09>" +         /* .............................................................................................. */
                        "<0x09>" + "            " + (String.format("%1$4s", Structbilling.Cur_Meter_Reading_Date)) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", Cur_Meter_Reading)) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", Prev_Meter_Reading)) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "     " + (String.format("%1$8s", mdValue)) + "      " + Structbilling.Avrg_PF + "\n" +
                        "<0x09>" + "     " + (String.format("%1$8s", dotSeparate(Structbilling.Billed_Units) + "/" + dotSeparate(Structbilling.O_BilledUnit_Actual))) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", Bill_Basis)) + "\n" + "      " + "\n" +
                        "<0x09>" +         /* .............................................................................................. */
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.MIN_CHRG))))) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.FIXED_CHARGE))))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_FCA))))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" +         /* .............................................................................................. */
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Structbilling.Meter_Rent)))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "              " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structconsmas.Sundry_Allow_EC))))) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_OverdrwalPenalty))))) + "\n" +//penall adhibhar
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Float.valueOf(Structbilling.O_PFPenalty) + Float.valueOf(Structbilling.O_welding_charges)))))) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(UtilAppCommon.doublecheck(Structconsmas.SD_BILLED)))))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(0))))) + "\n" +
                        "<0x09>" +         /* .................................................................................. */
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structconsmas.SD_INSTT_AMT) * -1f)))) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_LockCreditAmount) * -1f)))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_Employee_Incentive) * -1f)))) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(0) * -1f)))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(0) * -1f)))) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Incentives) * -1f)))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Subsidy) * -1f)))) + "\n" +
                        "<0x09>" +     /* .................................................................................. */
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Bill) - Float.valueOf(Structbilling.O_Arrear_Demand) + Float.valueOf(Structconsmas.Sundry_Allow_EC))))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structconsmas.Cur_Fiancial_Yr_Arr))))) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$6s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "               " + (String.format("%1$6s", Structbilling.O_Surcharge)) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$6s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" +   // doubt
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "              " + (String.format("%1$6s", Structconsmas.FIRST_CHQ_DUE_DATE)) + "\n" +
                        "<0x09>" + "              " + (String.format("%1$6s", Structconsmas.FIRST_CASH_DUE_DATE)) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" +     /* .................................................................................. */
                        "<0x09>" + "         " + (String.format("%1$4s", meterReaderName)) + " " + (String.format("%1$6s", Structbilling.SBM_No)) + " bill" + "\n" +
                        "                 " + Structbilling.VER_CODE + "\n" +
                        "            " + "\n" +
                        "<0x09>" +     /* .................................................................................. */
                        "<0x09>" + "             " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$6s", Structconsmas.DIV_NAME)) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$6s", sectionName)) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "        " + (String.format("%1$8s", Structconsmas.Cycle)) + "/" + Structconsmas.Route_Number + "\n" +
                        "<0x09>" + "        " + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                        "<0x09>" + "        " + (String.format("%1$8s", appcomUtil.IVRS_NO_PRINT())) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n");
            } else {
                mSendMessage("<0x09>" + "     " + "\n" +
                        "<0x09>" + "     " + "\n" +
                        "<0x09>" + "     " + "\n" +
                        "<0x09>" + "     " + "\n" +
                        "<0x09>" + "     " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
                        "<0x09>" + "     " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" + //201706
                        "<0x09>" + "     " + (String.format("%1$6s", PrinDate)) + "  " + billTime + "\n" +
                        "<0x09>" + "     " + (String.format("%1$6s", Structbilling.Bill_No)) + "\n" +
                        "<0x09>" + "    " + (String.format("%1$6s", Structconsmas.DIV_NAME)) + "\n" +
                        "<0x09>" + "   " + (String.format("%1$6s", sectionName)) + "\n" +
                        "<0x09>" + "       " + (String.format("%1$8s", appcomUtil.IVRS_NO_PRINT())) + "\n" +
                        "<0x09>" + "  " + (String.format("%1$6s", Structconsmas.Name)) + "\n" +
                        "<0x09>" + " " + (String.format("%1$6s", address)) + "\n" +
                        "<0x09>" + " " + (String.format("%1$8s", printtariif())) + " " + getLoadType() + " " + (String.format("%1$8s", Structconsmas.Load)) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$8s", Structconsmas.TOT_SD_HELD)) + "\n" +
                        "<0x09>" + "       " + (String.format("%1$8s", Structconsmas.Meter_S_No)) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "    " + (String.format("%1$8s", Structconsmas.MF)) + " / " + Structconsmas.PHASE_CD + "\n" + "\n" +
                        "<0x09>" +         /* .............................................................................................. */
                        "<0x09>" + "            " + (String.format("%1$4s", Structbilling.Cur_Meter_Reading_Date)) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", Cur_Meter_Reading)) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", Prev_Meter_Reading)) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "     " + (String.format("%1$8s", mdValue)) + "      " + Structbilling.Avrg_PF + "\n" +
                        "<0x09>" + "     " + (String.format("%1$8s", dotSeparate(Structbilling.Billed_Units) + "/" + dotSeparate(Structbilling.O_BilledUnit_Actual))) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", Bill_Basis)) + "\n" + "      " + "\n" +
                        "<0x09>" +         /* .............................................................................................. */
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.MIN_CHRG))))) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.FIXED_CHARGE))))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_TotalEnergyCharge))))) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_FCA))))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" +         /* .............................................................................................. */
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_ElectricityDutyCharges))))) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Structbilling.Meter_Rent)))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "              " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structconsmas.Sundry_Allow_EC))))) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_OverdrwalPenalty))))) + "\n" +//penall adhibhar
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Float.valueOf(Structbilling.O_PFPenalty) + Float.valueOf(Structbilling.O_welding_charges)))))) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(UtilAppCommon.doublecheck(Structconsmas.SD_BILLED)))))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(0))))) + "\n" +
                        "<0x09>" +         /* .................................................................................. */
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structconsmas.SD_INSTT_AMT) * -1f)))) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_LockCreditAmount) * -1f)))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_Employee_Incentive) * -1f)))) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(0) * -1f)))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(0) * -1f)))) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Incentives) * -1f)))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Subsidy) * -1f)))) + "\n" +
                        "<0x09>" +     /* .................................................................................. */
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Bill) - Float.valueOf(Structbilling.O_Arrear_Demand) + Float.valueOf(Structconsmas.Sundry_Allow_EC))))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", (String.format("%.2f", Float.valueOf(Structconsmas.Cur_Fiancial_Yr_Arr))))) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$6s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "               " + (String.format("%1$6s", Structbilling.O_Surcharge)) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$6s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" +   // doubt
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "              " + (String.format("%1$6s", Structconsmas.FIRST_CHQ_DUE_DATE)) + "\n" +
                        "<0x09>" + "              " + (String.format("%1$6s", Structconsmas.FIRST_CASH_DUE_DATE)) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" +     /* .................................................................................. */
                        "<0x09>" + "         " + (String.format("%1$4s", meterReaderName)) + " " + (String.format("%1$6s", Structbilling.SBM_No)) + " bill" + "\n" +
                        "                 " + Structbilling.VER_CODE + "\n" +
                        "            " + "\n" +
                        "<0x09>" +     /* .................................................................................. */
                        "<0x09>" + "             " + (String.format("%1$6s", getBillMonth(Structconsmas.Bill_Mon))) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$6s", Structconsmas.DIV_NAME)) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$6s", sectionName)) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "        " + (String.format("%1$8s", Structconsmas.Cycle)) + "/" + Structconsmas.Route_Number + "\n" +
                        "<0x09>" + "        " + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                        "<0x09>" + "        " + (String.format("%1$8s", appcomUtil.IVRS_NO_PRINT())) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n" +
                        "<0x09>" + " " + " " + "\n");
            }


            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            sDialog = new SweetAlertDialog(DuplicateBillPrint.this, SweetAlertDialog.SUCCESS_TYPE);
            sDialog.setTitleText("Bluetooth Printer");
            sDialog.setContentText("Successfully Printed");
            sDialog.show();
            sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismissWithAnimation();

                    UtilAppCommon ucom = new UtilAppCommon();
                    ucom.nullyfimodelCon();
                    ucom.nullyfimodelBill();


                    Intent intent = new Intent(DuplicateBillPrint.this, BillingtypesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("printtype", print_type);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);
                }
            });


        }

        @Override
        protected void onPreExecute() {


        }


    }/*--------------------- JBLPUR Bill Print ----------------------------------*/

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        dismissProgressDialog();
        Debug.stopMethodTracing();
        super.onDestroy();
    }

    private void dismissProgressDialog() {
        if (sDialog != null && sDialog.isShowing()) {
            sDialog.dismiss();
        }
    }

    private String printSurcharge() {

        if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
            return "0";
        } else {
            return Structconsmas.SURCHARGE_ARREARS;
        }

    }

    private String RDG_TYP_CD_Print() {

        dbHelper = new DB(DuplicateBillPrint.this);
        SD = dbHelper.getWritableDatabase();

        String strPref = "SELECT STATUS FROM TBL_METERSTATUSCODE_MP WHERE BILL_BASIS='" + Structbilling.Bill_Basis + "'";


        Cursor getPref = SD.rawQuery(strPref, null);

        if (getPref != null && getPref.moveToFirst()) {

            return getPref.getString(0);

        } else {
            return "NO METER";
        }

    }

    /*  To show response messages  */
    public void showdialog() {

        new SweetAlertDialog(DuplicateBillPrint.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Bluetooth Printer")
                .setContentText("Successfully Printed")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

                    }
                })
                .show();


    }

    private String printtariif() {
        String st1 = null;
        String printtariif = null;
        if (Structconsmas.URBAN_FLG.equalsIgnoreCase("U")) {
            st1 = Structtariff.Tariff_URBAN;
        } else {
            st1 = Structtariff.Tariff_RURAL;
        }

        return Structconsmas.Tariff_Code + "-" + st1;
    }

    public String getLoadType() {

        //UtilAppCommon uc = new UtilAppCommon();

        switch (Structconsmas.Load_Type) {
            case "W":
            case "1":
                return " W";
            case "KW":
            case "2"://kw
                return " KW";
            //return uc.ConvertMD(Structconsmas.Load, "KW", "HP");//load,string units,string target units
            case "BHP":
            case "3"://hp'HP
                return " HP";
            //               return uc.ConvertMD(Structconsmas.Load, "HP", "HP");
            case "KVA":
            case "4"://KVA
                return " KVA";
            //return uc.ConvertMD(Structconsmas.Load, "KVA", "KVA");
        }
        return "W";
    }

    public String getBillMonth(String month) {

        switch (month.substring(4, 6)) {
            case "01":
                return "Jan-" + month.substring(2, 4);
            case "02":
                return "Feb-" + month.substring(2, 4);
            case "03":
                return "Mar-" + month.substring(2, 4);
            case "04":
                return "Apr-" + month.substring(2, 4);
            case "05":
                return "May-" + month.substring(2, 4);
            case "06":
                return "Jun-" + month.substring(2, 4);
            case "07":
                return "Jul-" + month.substring(2, 4);
            case "08":
                return "Aug-" + month.substring(2, 4);
            case "09":
                return "Sep-" + month.substring(2, 4);
            case "10":
                return "Oct-" + month.substring(2, 4);
            case "11":
                return "Nov-" + month.substring(2, 4);
            case "12":
                return "Dec-" + month.substring(2, 4);
        }
        return month;
    }

    private String dotSeparate(String value) {

        if (value.contains(".")) {
            String[] parts = value.split("\\."); // escape .
            String part1 = parts[0];
            return part1;
        }
        return value;
    }

    private void sendMessage(String message) {
        // TODO Auto-generated method stub

        Log.e("", "MESSAGE " + message);

        Intent myIntent = new Intent();
        myIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        myIntent.putExtra("message", message);
        myIntent.setAction("com.softland.iprintMarvel.Bluetooth.message");
        myIntent.setType("*/*");
        sendBroadcast(myIntent);

        UtilAppCommon ucom = new UtilAppCommon();
        ucom.nullyfimodelCon();
        ucom.nullyfimodelBill();

    }

    public void mSendMessage(String message) {
        // TODO Auto-generated method stub
        Log.e("", "MESSAGE " + message);
        Intent myIntent = new Intent();
        myIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        myIntent.putExtra("message", message);
        myIntent.setAction("com.softland.iprintMarvel.Bluetooth.message");
        myIntent.setType("*/*");
        sendBroadcast(myIntent);

//        dbHelper.insertIntoBillingTable();
//        dbHelper.insertSequence("BillNumber", billseq);
//        SILPrintIntegration integration = new SILPrintIntegration(message.replace("<0x09>","").replace("<0x10>",""));
//        integration.startPrinter();
    }
}