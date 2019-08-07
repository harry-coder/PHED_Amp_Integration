package com.fedco.mbc.bluetoothprinting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;

import android.text.format.DateFormat;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.BillingtypesActivity;
import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.hasnain.BlutoothPrinterActivity;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.model.Structbilling;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.model.Structtariff;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.AndroidBmpUtil;
import com.fedco.mbc.utils.HttpFileUpload;
import com.fedco.mbc.utils.URLS;
import com.fedco.mbc.utils.UtilAppCommon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SLPrintingMainActivity extends AppCompatActivity {
    private Activity mActivity;

    String myCmd = "<0x09>", selectedImagePath, strMeterMake, strMeterOwner;
    String print_type;
    String Hour, Minute;
    int HOURS, MIN, AP;
    String strtime;
    String key;
    String strdcSeq;
    Long billseq;
    Long dcSeq;
    SweetAlertDialog sDialog;

    Logger Log;
    SessionManager session;
    SQLiteDatabase SD, SD2, SD3, SD4;
    DB dbHelper, dbHelper2, dbHelper3, dbHelper4;
    UtilAppCommon appcomUtil;

    private ProgressDialog progress;
    int Cur_Meter_Reading;
    int Prev_Meter_Reading;
    String Bill_Basis;
    String Reading_type_code;

    ArrayList<String> mylistimagename = new ArrayList<String>();

    String ZipSourcePath = Environment.getExternalStorageDirectory() + "/MBC/Images/";
    String ZipCopyPath = Environment.getExternalStorageDirectory() + "/MBC/Downloadsingular/";
    String ZipDeletPath2 = Environment.getExternalStorageDirectory() + "/MBC/Downloadsingular/";
    String Zip = Environment.getExternalStorageDirectory() + "/Notes/billing.csv";
    String ZipDesPathdup;
    String signaturePathDes;
    String photoPathDes;
    String ZipDesPath;
    String imgSrcPathDes;
    String imgSrcPath;
    private static SimpleDateFormat simpleDateFormat;

    private static Date connvertedDate;
    private static DateFormat dateFormat;
    private static String actualDate;
    String newDate;
    String mdValue;
    String pfValue, billTime;
    Float miscValue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = this;

        dbHelper = new DB(getApplicationContext());
        SD = dbHelper.getWritableDatabase();
        appcomUtil = new UtilAppCommon();
        Calendar c = Calendar.getInstance();

        //-------------not used
        Structconsmas.Meter_Type = "00";
        Structconsmas.Meter_Ownership = "A";
        //----------
        imgSrcPath = Environment.getExternalStorageDirectory()
                + File.separator + "/MBC/Images/" + appcomUtil.UniqueCode(getApplicationContext()) + "_" + Structconsmas.LOC_CD + "_" + Structconsmas.MAIN_CONS_LNK_NO + "_" + appcomUtil.billMonthConvert(Structconsmas.Bill_Mon) + "_mtr.jpg";
        imgSrcPathDes = Environment.getExternalStorageDirectory()
                + File.separator + "/MBC/Images/" + appcomUtil.UniqueCode(getApplicationContext()) + "_mtr.bmp";
        ZipDesPath = Environment.getExternalStorageDirectory() + "/MBC/" + appcomUtil.UniqueCode(getApplicationContext()) + GSBilling.getInstance().captureDatetime() + ".zip";
        ZipDesPathdup = "/MBC/" + appcomUtil.UniqueCode(getApplicationContext()) + GSBilling.getInstance().captureDatetime();
        signaturePathDes = Environment.getExternalStorageDirectory() + "/MBC" + "/" + appcomUtil.UniqueCode(getApplicationContext()) + "_" + Structconsmas.LOC_CD + "_" + Structconsmas.MAIN_CONS_LNK_NO + "_" + appcomUtil.billMonthConvert(Structconsmas.Bill_Mon) + "_sig.jpg";
        photoPathDes = Environment.getExternalStorageDirectory() + "/MBC/" + appcomUtil.UniqueCode(getApplicationContext()) + "_" + Structconsmas.LOC_CD + "_" + Structconsmas.MAIN_CONS_LNK_NO + "_" +appcomUtil.billMonthConvert(Structconsmas.Bill_Mon) + "_mtr.jpg";

        File dir2 = new File(ZipDeletPath2);
        DeleteRecursive(dir2);

        SimpleDateFormat fds = new SimpleDateFormat("kk:mm");
        strtime = fds.format(c.getTime());

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
//        key = session.retLicence();
        key = appcomUtil.UniqueCode(getApplicationContext());

        Structbilling.SBM_No = key;
        Structbilling.Meter_Reader_Name = session.retMRName();
        Structbilling.Meter_Reader_ID = session.retMRID();

        try {
            AndroidBmpUtil.convertJpeg2Bmp(getApplicationContext(), imgSrcPath, imgSrcPathDes);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        switch (Structconsmas.Meter_Type.trim()) {
            case "03":
                System.out.println("in if3" + strMeterMake);
                this.strMeterMake = "1ph Static";
                break;
            case "01":
                System.out.println("in if1" + strMeterMake);
                this.strMeterMake = "1ph EM";
                break;
            case "28":
                System.out.println("in if28" + strMeterMake);
                this.strMeterMake = "LT 1 Ph AMR/AMI";
                break;
            default:
                System.out.println("in ELSE" + strMeterMake);
                System.out.println("in default" + Structconsmas.Meter_Type);
                this.strMeterMake = "";
                break;
        }

        switch (Structconsmas.Meter_Ownership) {
            case "D":
                System.out.println("in if3" + strMeterOwner);
                this.strMeterOwner = "DF";
                break;
            case "G":
                System.out.println("in if1" + strMeterOwner);
                this.strMeterOwner = "CESU";
                break;
            case "C":
                System.out.println("in if28" + strMeterMake);
                this.strMeterOwner = "CONSUMER";
                break;
            default:
                System.out.println("in ELSE" + strMeterMake);
                this.strMeterOwner = "";
                break;
        }


        switch (Structconsmas.PICK_REGION) {
            case "10"://bhopal
                Log.e(getApplicationContext(), "SLPrintAct", "Bhopal ");
                new PrintProcessBHO().execute();
                break;
            case "11"://jabalpur

                Log.e(getApplicationContext(), "SLPrintAct", "Jabalpur ");
                new PrintProcessJBL().execute();
                break;
            case "12"://indore
//                new PrintProcessBHO().execute();
                Log.e(getApplicationContext(), "SLPrintAct", "Indore ");
                new PrintProcess().execute();
                break;

            default:
                new PrintProcess().execute();

                break;
        }

        Prev_Meter_Reading =Structconsmas.Prev_Meter_Reading;
        Cur_Meter_Reading = Structbilling.Cur_Meter_Reading;
        Bill_Basis = Structbilling.Bill_Basis;
        Reading_type_code = RDG_TYP_CD_Print();


        if(Structbilling.Reasons != null && !Structbilling.Reasons.isEmpty()){

            if(Structbilling.Reasons.equalsIgnoreCase("Previous reading is higher than Current reading")){
                Prev_Meter_Reading = Structconsmas.Prev_Meter_Reading;
                Cur_Meter_Reading = Structconsmas.Prev_Meter_Reading;
                Bill_Basis = "ACT";
                Reading_type_code = "NORMAL";

            }

        }



    }

    public void onBackPressed() {

        Intent intent = new Intent(SLPrintingMainActivity.this, BillingtypesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);

    }

    /*--------------------- Billing Masters Download Initiation ----------------------------------*/
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

            if (Structbilling.MDI != null) {
                mdValue = Structbilling.MDI + " " + GSBilling.getInstance().getUnitMaxDemand();
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
            String address = Structconsmas.address1 + Structconsmas.address2 + Structconsmas.MOH;
            String subadd1;
            String subadd2;
            String PrinDate;

            PrinDate = Structbilling.Cur_Meter_Reading_Date.substring(0, 6) + Structbilling.Cur_Meter_Reading_Date.substring(9);

            if (address.length() < 23) {
                address = Structconsmas.address1 + Structconsmas.address2 + "\n";
            } else {

                if (address.length() >= 46) {
                    subadd1 = address.substring(0, 46);
                } else {
                    subadd1 = address;
                }

                address = subadd1;

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

            dbHelper4 = new DB(getApplicationContext());
            SD4 = dbHelper4.getWritableDatabase();

            //  dbHelper4.insertIntoBillingTable();
            dbHelper4.insertIntoMPBillingTable();
            dbHelper4.insertSequence("BillNumber", billseq);

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
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.Total_Energy_Charg))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_FCA))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Structbilling.Electricity_Duty_Charges)))) + "\n" +
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
                        "<0x09>" + "         " + (String.format("%1$4s", meterReaderName)) + " " + (String.format("%1$6s", Structbilling.SBM_No)) + " " + Structbilling.VER_CODE + "\n" +
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
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.Total_Energy_Charg))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_FCA))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Structbilling.Electricity_Duty_Charges)))) + "\n" +
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
                        "<0x09>" + "         " + (String.format("%1$4s", meterReaderName)) + " " + (String.format("%1$6s", Structbilling.SBM_No)) + " " + Structbilling.VER_CODE + "\n" +
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
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.Total_Energy_Charg))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_FCA))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Structbilling.Electricity_Duty_Charges)))) + "\n" +
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
                        "<0x09>" + "         " + (String.format("%1$4s", meterReaderName)) + " " + (String.format("%1$6s", Structbilling.SBM_No)) + " " + Structbilling.VER_CODE + "\n" +
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
            }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            sDialog = new SweetAlertDialog(SLPrintingMainActivity.this, SweetAlertDialog.SUCCESS_TYPE);
            sDialog.setTitleText("Bluetooth Printer");
            sDialog.setContentText("Successfully Printed");

            sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismissWithAnimation();

                    UtilAppCommon ucom = new UtilAppCommon();
                    ucom.nullyfimodelCon();
                    ucom.nullyfimodelBill();

                    if (isMobileDataEnabled()) {

                        new TextFileClass(SLPrintingMainActivity.this).execute();

                    } else {

                        Toast.makeText(SLPrintingMainActivity.this, "Internally stored due to no connectivity", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(SLPrintingMainActivity.this, BillingtypesActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("printtype", print_type);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left);
                    }
                }
            });
            sDialog.show();

//            sDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {


        }


    }

    /*--------------------- Billing Masters Download Initiation ----------------------------------*/
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

            if (Structbilling.MDI != null) {
                mdValue = Structbilling.MDI + " " + GSBilling.getInstance().getUnitMaxDemand();
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
            String address = Structconsmas.address1 + Structconsmas.address2 + Structconsmas.MOH;
            String subadd1;
            String subadd2;

            if (address.length() < 23) {
                address = Structconsmas.address1 + Structconsmas.address2 + Structconsmas.MOH+"\n";
            } else {

                if (address.length() >= 46) {
                    subadd1 = address.substring(0, 46);
                } else {
                    subadd1 = address;
                }

                address = subadd1;

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

            dbHelper4 = new DB(getApplicationContext());
            SD4 = dbHelper4.getWritableDatabase();

            //  dbHelper4.insertIntoBillingTable();
            dbHelper4.insertIntoMPBillingTable();
            dbHelper4.insertSequence("BillNumber", billseq);
            String PrinDate;

            PrinDate = Structbilling.Cur_Meter_Reading_Date.substring(0, 6) + Structbilling.Cur_Meter_Reading_Date.substring(9);


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
                            /* ............................................................................................. */
                        "<0x09>" + " " + (String.format("%1$8s", printtariif())) + " " + getLoadType() + " " + (String.format("%1$8s", Structconsmas.Load)) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$8s", Structconsmas.TOT_SD_HELD)) + "\n" +
                        "<0x09>" + "       " + (String.format("%1$8s", Structconsmas.Meter_S_No)) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "    " + (String.format("%1$8s", Structconsmas.MF)) + " / " + Structconsmas.PHASE_CD + "\n" + "\n" +
                            /* .............................................................................................. */
                        "<0x09>" + "            " + (String.format("%1$4s", Structbilling.Cur_Meter_Reading_Date)) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", Cur_Meter_Reading)) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", Prev_Meter_Reading)) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "     " + (String.format("%1$8s", mdValue)) + "      " + Structbilling.Avrg_PF + "\n" +
                        "<0x09>" + "     " + (String.format("%1$8s", dotSeparate(Structbilling.Billed_Units) + "/" + dotSeparate(Structbilling.O_BilledUnit_Actual))) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", Bill_Basis)) + "\n" + "      " + "\n" +
                            /* .............................................................................................. */
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.MIN_CHRG))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.FIXED_CHARGE))))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.Total_Energy_Charg))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_FCA))))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                            /* .............................................................................................. */
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Structbilling.Electricity_Duty_Charges)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Structbilling.Meter_Rent)))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structconsmas.Sundry_Allow_EC))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_OverdrwalPenalty))))) + "\n" +//penall adhibhar
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Float.valueOf(Structbilling.O_PFPenalty) + Float.valueOf(Structbilling.O_welding_charges)))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(UtilAppCommon.doublecheck(Structconsmas.SD_BILLED)))))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(0))))) + "\n" +
                            /* .................................................................................. */
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structconsmas.SD_INSTT_AMT) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_LockCreditAmount) * -1f)))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Employee_Incentive) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(0) * -1f)))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(0) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Incentives) * -1f)))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Subsidy) * -1f)))) + "\n" +
                        /* .................................................................................. */
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Bill) - Float.valueOf(Structbilling.O_Arrear_Demand) + Float.valueOf(Structconsmas.Sundry_Allow_EC))))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structconsmas.Cur_Fiancial_Yr_Arr) + Float.valueOf(printSurcharge()))))) + "\n" +
                        "<0x09>" + "              " + (String.format("%1$6s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "               " + (String.format("%1$6s", Structbilling.O_Surcharge)) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$6s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" +   // doubt
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "              " + (String.format("%1$6s", Structconsmas.FIRST_CHQ_DUE_DATE)) + "\n" +
                        "<0x09>" + "              " + (String.format("%1$6s", Structconsmas.FIRST_CASH_DUE_DATE)) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        /* .................................................................................. */
                        "<0x09>" + "         " + (String.format("%1$4s", meterReaderName)) + " " + (String.format("%1$6s", Structbilling.SBM_No)) + "\n" +
                        "<0x09>" + "                 " + Structbilling.VER_CODE + "\n" +
                        "<0x09>" + "            " + "\n" +
                        /* .................................................................................. */
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
                mSendMessage("<0x09>" + "    " + "\n" +
                        "<0x09>" + "    " + "\n" +
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
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + " " + (String.format("%1$6s", address)) + "\n" +
                        "<0x09>" + /* ............................................................................................. */
                        "<0x09>" + " " + (String.format("%1$8s", printtariif())) + " " + getLoadType() + " " + (String.format("%1$8s", Structconsmas.Load)) + "\n" +
                        "<0x09>" + "      " + (String.format("%1$8s", Structconsmas.TOT_SD_HELD)) + "\n" +
                        "<0x09>" + "       " + (String.format("%1$8s", Structconsmas.Meter_S_No)) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "    " + (String.format("%1$8s", Structconsmas.MF)) + " / " + Structconsmas.PHASE_CD + "\n" + "\n" +
                        "<0x09>" + /* .............................................................................................. */
                        "<0x09>" + "            " + (String.format("%1$4s", Structbilling.Cur_Meter_Reading_Date)) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", Cur_Meter_Reading)) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", Prev_Meter_Reading)) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "     " + (String.format("%1$8s", mdValue)) + "      " + Structbilling.Avrg_PF + "\n" +
                        "<0x09>" + "     " + (String.format("%1$8s", dotSeparate(Structbilling.Billed_Units) + "/" + dotSeparate(Structbilling.O_BilledUnit_Actual))) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$8s", Bill_Basis)) + "\n" + "      " + "\n" +
                        "<0x09>" + /* .............................................................................................. */
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.MIN_CHRG))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.FIXED_CHARGE))))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.Total_Energy_Charg))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_FCA))))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + /* .............................................................................................. */
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Structbilling.Electricity_Duty_Charges)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Structbilling.Meter_Rent)))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structconsmas.Sundry_Allow_EC))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_OverdrwalPenalty))))) + "\n" +//penall adhibhar
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Float.valueOf(Structbilling.O_PFPenalty) + Float.valueOf(Structbilling.O_welding_charges)))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(UtilAppCommon.doublecheck(Structconsmas.SD_BILLED)))))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(0))))) + "\n" +
                        "<0x09>" + /* .................................................................................. */
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structconsmas.SD_INSTT_AMT) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_LockCreditAmount) * -1f)))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Employee_Incentive) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(0) * -1f)))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(0) * -1f)))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Incentives) * -1f)))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Subsidy) * -1f)))) + "\n" +
                        /* .................................................................................. */
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_Total_Bill) - Float.valueOf(Structbilling.O_Arrear_Demand) + Float.valueOf(Structconsmas.Sundry_Allow_EC))))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structconsmas.Cur_Fiancial_Yr_Arr) + Float.valueOf(printSurcharge()))))) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$6s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "               " + (String.format("%1$6s", Structbilling.O_Surcharge)) + "\n" +
                        "<0x09>" + "               " + (String.format("%1$6s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" +   // doubt
                        "<0x09>" + "      " + "\n" +
                        "<0x09>" + "              " + (String.format("%1$6s", Structconsmas.FIRST_CHQ_DUE_DATE)) + "\n" +
                        "<0x09>" + "              " + (String.format("%1$6s", Structconsmas.FIRST_CASH_DUE_DATE)) + "\n" +
                        "<0x09>" + "      " + "\n" +
                        /* .................................................................................. */
                        "         " + (String.format("%1$4s", meterReaderName)) + " " + (String.format("%1$6s", Structbilling.SBM_No)) + "\n" +
                        "                 " + Structbilling.VER_CODE + "\n" +
                        "            " + "\n" +
                        /* .................................................................................. */
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
            sDialog = new SweetAlertDialog(SLPrintingMainActivity.this, SweetAlertDialog.SUCCESS_TYPE);
            sDialog.setTitleText("Bluetooth Printer");
            sDialog.setContentText("Successfully Printed");

            sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismissWithAnimation();

                    UtilAppCommon ucom = new UtilAppCommon();
                    ucom.nullyfimodelCon();
                    ucom.nullyfimodelBill();

                    if (isMobileDataEnabled()) {

                        new TextFileClass(SLPrintingMainActivity.this).execute();

                    } else {

                        Toast.makeText(SLPrintingMainActivity.this, "Internally stored due to no connectivity", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(SLPrintingMainActivity.this, BillingtypesActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("printtype", print_type);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left);
                    }
                }
            });
            sDialog.show();

        }

        @Override
        protected void onPreExecute() {


        }


    }

    /*--------------------- Billing Masters Download Initiation ----------------------------------*/
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

            if (Structbilling.MDI != null) {
                mdValue = Structbilling.MDI + " " + GSBilling.getInstance().getUnitMaxDemand();
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
                consumerName = fixedLengthString(Structconsmas.Name, 43);
                consumerName = consumerName.substring(0, 42);
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
            String PrinDate;

            PrinDate = Structbilling.Cur_Meter_Reading_Date.substring(0, 6) + Structbilling.Cur_Meter_Reading_Date.substring(9);

            String sectionName = Structconsmas.Section_Name;
            if (sectionName.length() >= 19) {
                sectionName = Structconsmas.Section_Name.substring(0, 19);
            } else {
                sectionName = Structconsmas.Section_Name;
            }

            Calendar c1 = Calendar.getInstance();
            SimpleDateFormat fds2 = new SimpleDateFormat("kk:mm");
            billTime = fds2.format(c1.getTime());

            dbHelper4 = new DB(getApplicationContext());
            SD4 = dbHelper4.getWritableDatabase();

            //  dbHelper4.insertIntoBillingTable();
            dbHelper4.insertIntoMPBillingTable();
            dbHelper4.insertSequence("BillNumber", billseq);

            String print_content = "";

            if (Structconsmas.Name.length() < 23 && address.length() < 23) {

                mSendMessage( "<0x09>" + "    " + "\n" +
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
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.Total_Energy_Charg))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_FCA))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Structbilling.Electricity_Duty_Charges)))) + "\n" +
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
                        "<0x09>" + "         " + (String.format("%1$4s", meterReaderName)) + " " + (String.format("%1$6s", Structbilling.SBM_No)) + " " + Structbilling.VER_CODE + "\n" +
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
                        "<0x09>" + "        " + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                        "<0x09>" + "        " + (String.format("%1$8s", appcomUtil.IVRS_NO_PRINT())) + "\n" +
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
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.Total_Energy_Charg))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_FCA))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Structbilling.Electricity_Duty_Charges)))) + "\n" +
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
                        "<0x09>" + "              " + (String.format("%1$10s", Structbilling.O_Surcharge)) + "\n" +
                        "<0x10>" + "              " + (String.format("%1$10s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" +  // doubt
                        "<0x09>" + "              " + (String.format("%1$6s", Structconsmas.FIRST_CHQ_DUE_DATE)) + "\n" +
                        "<0x09>" + "              " + (String.format("%1$6s", Structconsmas.FIRST_CASH_DUE_DATE)) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "         " + (String.format("%1$4s", meterReaderName)) + " " + (String.format("%1$6s", Structbilling.SBM_No)) + " " + Structbilling.VER_CODE + "\n" +
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
                        "<0x09>" + "        " + (String.format("%1$9s", Structconsmas.Consumer_Number)) + "\n" +
                        "<0x09>" + "        " + (String.format("%1$8s", appcomUtil.IVRS_NO_PRINT())) + "\n" +
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
                        "<0x09>" + "  " + "\n" +
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
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.Total_Energy_Charg))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Structbilling.O_FCA))))) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", Structbilling.Electricity_Duty_Charges)))) + "\n" +
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
                        "<0x09>" + "            " + (String.format("%1$10s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", Structbilling.O_Surcharge)) + "\n" +
                        "<0x09>" + "             " + (String.format("%1$10s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" +  // doubt
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "              " + (String.format("%1$6s", Structconsmas.FIRST_CHQ_DUE_DATE)) + "\n" +
                        "<0x09>" + "              " + (String.format("%1$6s", Structconsmas.FIRST_CASH_DUE_DATE)) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "         " + (String.format("%1$4s", meterReaderName)) + " " + (String.format("%1$6s", Structbilling.SBM_No)) + " " + Structbilling.VER_CODE + "\n" +
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
                        "<0x09>" + "              " + (String.format("%1$6s", (String.format("%.2f", Float.valueOf(Math.round(Double.valueOf(Structbilling.O_Total_Bill))))))) + "\n" +
                        "<0x09>" + " " + "\n" +
                        "<0x09>" + "              " + (String.format("%1$6s", (String.format("%.2f", (float) Math.round((float) (Math.round(Double.valueOf(Structbilling.O_Total_Bill)) + Double.valueOf(Structbilling.O_Surcharge))))))) + "\n" + // doubt
                        "<0x09>" + " " + "\n" +
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
            return print_content;
        }

        @Override
        protected void onPostExecute(String result) {
//            Intent intent = new Intent(SLPrintingMainActivity.this, BlutoothPrinterActivity.class);
//            intent.putExtra("result", result);
//            startActivity(intent);
//            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);

            sDialog = new SweetAlertDialog(SLPrintingMainActivity.this, SweetAlertDialog.SUCCESS_TYPE);
            sDialog.setTitleText("Bluetooth Printer");
            sDialog.setContentText("Successfully Printed");

            sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismissWithAnimation();

                    UtilAppCommon ucom = new UtilAppCommon();
                    ucom.nullyfimodelCon();
                    ucom.nullyfimodelBill();

                    if (isMobileDataEnabled()) {

                        new TextFileClass(SLPrintingMainActivity.this).execute();

                    } else {

                        Toast.makeText(SLPrintingMainActivity.this, "Internally stored due to no connectivity", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SLPrintingMainActivity.this, BillingtypesActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("printtype", print_type);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
                    }
                }
            });
            sDialog.show();
        }

        @Override
        protected void onPreExecute() {


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

        dbHelper = new DB(SLPrintingMainActivity.this);
        SD = dbHelper.getWritableDatabase();

        String strPref = "SELECT STATUS FROM TBL_METERSTATUSCODE_MP WHERE BILL_BASIS='" + Structbilling.Bill_Basis + "'";


        Cursor getPref = SD.rawQuery(strPref, null);

        if (getPref != null && getPref.moveToFirst()) {

            return getPref.getString(0);

        } else {
            return "NO METER";
        }

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

    /*   This method ENters the Messegae to be printed  AsynTask operation */
    public class EnterTextAsyc extends AsyncTask<Integer, Integer, Integer> {
        /* displays the progress dialog until background task is completed*/
        private SimpleDateFormat simpleDateFormat;

        private Date connvertedDate;
        private DateFormat dateFormat;
        private String actualDate;

        @Override
        protected void onPreExecute() {

        }

        /* Task of EnterTextAsyc performing in the background*/
        @Override
        protected Integer doInBackground(Integer... params) {
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
            Float obj  = new Float(GSBilling.getInstance().getSalb1unit());
            Float obj1 = new Float(GSBilling.getInstance().getSalb2unit());
            Float obj2 = new Float(GSBilling.getInstance().getSalb3unit());
            Float obj3 = new Float(GSBilling.getInstance().getSalb4unit());

            int slab1unit = obj.intValue();
            int slab2unit = obj1.intValue();
            int slab3unit = obj2.intValue();
            int slab4unit = obj3.intValue();

//            float Arr = Structconsmas.Pre_Financial_Yr_Arr + Structconsmas.Cur_Fiancial_Yr_Arr;
            Structbilling.DCNumber = "";

//            Structbilling.DSIG_STATE = "50";

            billseq = appcomUtil.findSequence(getApplicationContext(), "BillNumber");

            UtilAppCommon uc = new UtilAppCommon();
            uc.print_Bill();
            uc.princonmas();

//            double totAMount = (Math.ceil(Structbilling.Amnt_bPaid_on_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL));

            //      if (Arr > 5000 && totAMount > 5000) {

            dcSeq = appcomUtil.findSequence(getApplicationContext(), "DCNumber");
            strdcSeq = String.format("%05d", dcSeq);
            Structbilling.DCNumber = key + "/DN" + dcSeq;

            /// paral billing
            if (Structbilling.md_input != null && !Structbilling.md_input.isEmpty()) {
                mdValue = Structbilling.md_input + " " + GSBilling.getInstance().getUnitMaxDemand();
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

            dbHelper.insertSequence("DCNumber", dcSeq);

            SLPrintingMainActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
//                    progress.dismiss();

                    ArrayList<String> mylistbck = new ArrayList<String>();
                    ArrayList<String> listBckBill = new ArrayList<String>();

                    dbHelper4 = new DB(getApplicationContext());
                    SD4 = dbHelper4.getWritableDatabase();

                    //  dbHelper4.insertIntoBillingTable();
                    dbHelper4.insertIntoMPBillingTable();
                    dbHelper4.insertSequence("BillNumber", billseq);

                }
            });
            return 0;
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

        public String padLeft(String s, int n) {
            if (s.length() < n) {
                return String.format("%1$" + n + "s", s);
            } else return s;
        }

        /* This displays the status messages of EnterTextAsyc in the dialog box */
        @Override
        protected void onPostExecute(Integer result) {

            showdialog();
            super.onPostExecute(result);
        }
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

    /*  To show response messages  */
    public void showdialog() {

        new SweetAlertDialog(SLPrintingMainActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Bluetooth Printer")
                .setContentText("Successfully Printed")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();

                        UtilAppCommon ucom = new UtilAppCommon();
                        ucom.nullyfimodelCon();
                        ucom.nullyfimodelBill();

                        if (isMobileDataEnabled()) {

                            new TextFileClass(SLPrintingMainActivity.this).execute();

                        } else {

                            Toast.makeText(SLPrintingMainActivity.this, "Internally stored due to no connectivity", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(SLPrintingMainActivity.this, BillingtypesActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("printtype", print_type);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);
                        }
                    }
                })
                .show();
    }

    public void mSendMessage(String message) {
//        // TODO Auto-generated method stub
        Log.e(getApplicationContext(), "SLPrintAct", "MESSAGE " + message);
        Log.e(getApplicationContext(), "SLPrintAct", "MESSAGE " + message);
        Intent myIntent = new Intent();
        myIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        myIntent.putExtra("message", message);
        myIntent.setAction("com.softland.iprintMarvel.Bluetooth.message");
        myIntent.setType("*/*");
        sendBroadcast(myIntent);

//        dbHelper.insertIntoBillingTable();
//        dbHelper.insertSequence("BillNumber", billseq);

//        SILPrintIntegration integration = new SILPrintIntegration(message.replace("<0x09>", "").replace("<0x10>", ""));
//        integration.startPrinter();
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

    private String dotSeparate(String value) {

        if (value.contains(".")) {
            String[] parts = value.split("\\."); // escape .
            String part1 = parts[0];
            return part1;
        }
        return value;
    }

    private class TextFileClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        public TextFileClass(Context c) {

            this.context = c;
//            this.error = status;
//            this.type = t;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Please Wait..");
            progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                dbHelper2 = new DB(getApplicationContext());
                SD2 = dbHelper2.getWritableDatabase();

                String selquer = "SELECT * FROM TBL_BILLING WHERE Upload_Flag='N' ";//WHERE Upload_Flag='N'
                Cursor curBillselect = SD2.rawQuery(selquer, null);
                String arrStr[] = null;
                ArrayList<String> mylist = new ArrayList<String>();
                ArrayList<String> mylist1 = new ArrayList<String>();


                if (curBillselect != null && curBillselect.moveToFirst()) {
                    int i = 0;

                    Log.e(getApplicationContext(), "SLPrintAct", "UpdateFLag to N : " + curBillselect.getString(0));

                    while (curBillselect.isAfterLast() == false) {
                        Calendar c = Calendar.getInstance();
                        System.out.println("Current time => " + c.getTime());
                        i = i + 1;
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yy");
                        String formattedDate = df.format(c.getTime());
                        String column_24 = String.valueOf(Double.valueOf(curBillselect.getString(24)) + Double.valueOf(curBillselect.getString(31)));

//                        mylist.add(curBillselect.getString(0) + "}" + curBillselect.getString(1) + "}" + curBillselect.getString(2) +
//                                "}" + curBillselect.getString(3) + "}" + curBillselect.getString(4) + "}" + curBillselect.getString(5) +
//                                "}" + curBillselect.getString(6) + "}" + curBillselect.getString(7) + "}" + curBillselect.getString(8) +
//                                "}" + curBillselect.getString(9) + "}" + curBillselect.getString(10) + "}" + curBillselect.getString(11) +
//                                "}" + curBillselect.getString(12) + "}" + curBillselect.getString(13) + "}" + curBillselect.getString(14) +
//                                "}" + curBillselect.getString(15) + "}" + curBillselect.getString(16) + "}" + curBillselect.getString(17) +
//                                "}" + curBillselect.getString(18) + "}" + curBillselect.getString(19) + "}" + curBillselect.getString(20) +
//                                "}" + curBillselect.getString(21) + "}" + curBillselect.getString(22) + "}" + curBillselect.getString(23) +
//                                "}" + curBillselect.getString(24) + "}" + curBillselect.getString(25) + "}" + curBillselect.getString(26) +
//                                "}" + curBillselect.getString(27) + "}" + curBillselect.getString(28) + "}" + curBillselect.getString(29) +
//                                "}" + curBillselect.getString(30) + "}" + curBillselect.getString(31) + "}" + curBillselect.getString(32) +
//                                "}" + curBillselect.getString(33) + "}" + curBillselect.getString(34) + "}" + curBillselect.getString(35) +
//                                "}" + curBillselect.getString(36) + "}" + curBillselect.getString(37) + "}" + curBillselect.getString(38) +
//                                "}" + curBillselect.getString(39) + "}" + curBillselect.getString(40) + "}" + curBillselect.getString(41) +
//                                "}" + curBillselect.getString(42) + "}" + curBillselect.getString(43) + "}" + curBillselect.getString(44) +
//                                "}" + curBillselect.getString(46) + "}" + curBillselect.getString(47) + "}" + curBillselect.getString(48) +
//                                "}" + curBillselect.getString(49) + "}" + curBillselect.getString(51) + "}" + curBillselect.getString(52) +
//                                "}" + curBillselect.getString(53) + "}" + curBillselect.getString(54) + "}" + curBillselect.getString(55) +
//                                "}" + curBillselect.getString(56) + "}" + curBillselect.getString(57) + "}" + curBillselect.getString(58));

                        mylist.add(curBillselect.getString(0) + "}" + curBillselect.getString(1) + "}" + curBillselect.getString(2) + "}" +
                                curBillselect.getString(3) + "}" + curBillselect.getString(4) + "}" + curBillselect.getString(5) + "}" +
                                curBillselect.getString(6) + "}" + curBillselect.getString(7) + "}" + curBillselect.getString(8) + "}" +
                                curBillselect.getString(9) + "}" + curBillselect.getString(10) + "}" + curBillselect.getString(11) + "}" +
                                curBillselect.getString(12) + "}" + curBillselect.getString(13) + "}" + curBillselect.getString(14) + "}" +
                                curBillselect.getString(15) + "}" + curBillselect.getString(16) + "}" + curBillselect.getString(17) + "}" +
                                curBillselect.getString(18) + "}" + curBillselect.getString(19) + "}" + curBillselect.getString(20) + "}" +
                                curBillselect.getString(21) + "}" + curBillselect.getString(22) + "}" + curBillselect.getString(23) + "}" +
                                curBillselect.getString(24) + "}" + curBillselect.getString(25) + "}" + curBillselect.getString(26) + "}" +
                                curBillselect.getString(27) + "}" + curBillselect.getString(28) + "}" + curBillselect.getString(29) + "}" +
                                curBillselect.getString(30) + "}" + curBillselect.getString(31) + "}" + curBillselect.getString(32) + "}" +
                                curBillselect.getString(33) + "}" + curBillselect.getString(34) + "}" + curBillselect.getString(35) + "}" +
                                curBillselect.getString(36) + "}" + curBillselect.getString(37) + "}" + curBillselect.getString(38) + "}" +
                                curBillselect.getString(39) + "}" + curBillselect.getString(40) + "}" + curBillselect.getString(41) + "}" +
                                curBillselect.getString(42) + "}" + curBillselect.getString(43) + "}" + curBillselect.getString(44) + "}" +
                                curBillselect.getString(45) + "}" + curBillselect.getString(46) + "}" + curBillselect.getString(47) + "}" +
                                curBillselect.getString(48) + "}" + curBillselect.getString(49) + "}" + curBillselect.getString(50) + "}" +
                                curBillselect.getString(51) + "}" + curBillselect.getString(52) + "}" + curBillselect.getString(53) + "}" +
                                curBillselect.getString(54) + "}" + curBillselect.getString(55) + "}" + curBillselect.getString(56) + "}" +
                                curBillselect.getString(57) + "}" + curBillselect.getString(58) + "}" + curBillselect.getString(59) + "}" +
                                curBillselect.getString(60) + "}" + curBillselect.getString(61) + "}" + curBillselect.getString(62) + "}" +
                                curBillselect.getString(63) + "}" + curBillselect.getString(64) + "}" + curBillselect.getString(65) + "}" +
                                curBillselect.getString(66) + "}" + curBillselect.getString(67) + "}" + curBillselect.getString(68) + "}" +
                                curBillselect.getString(69) + "}" + curBillselect.getString(70) + "}" + curBillselect.getString(71) + "}" +
                                curBillselect.getString(72) + "}" + curBillselect.getString(73) + "}" + curBillselect.getString(74) + "}" +
                                curBillselect.getString(75) + "}" + curBillselect.getString(76) + "}" + curBillselect.getString(77) + "}" +
                                curBillselect.getString(78) + "}" + curBillselect.getString(79) + "}" + curBillselect.getString(80) + "}" +
                                curBillselect.getString(81) + "}" + curBillselect.getString(82) + "}" + curBillselect.getString(83) + "}" +
                                curBillselect.getString(84) + "}" + curBillselect.getString(85) + "}" + curBillselect.getString(86) + "}" +
                                curBillselect.getString(87) + "}" + curBillselect.getString(88) + "}" + curBillselect.getString(89) + "}" +
                                curBillselect.getString(90) + "}" + curBillselect.getString(91) + "}" + curBillselect.getString(92) + "}" +
                                curBillselect.getString(93) + "}" + curBillselect.getString(94) + "}" + curBillselect.getString(95) + "}" +
                                curBillselect.getString(96) + "}" + curBillselect.getString(97) + "}" + curBillselect.getString(98) + "}" +
                                curBillselect.getString(99) + "}" + curBillselect.getString(100) + "}" + curBillselect.getString(101) + "}" +
                                curBillselect.getString(102) + "}" + curBillselect.getString(103) + "}" + curBillselect.getString(104) + "}" +
                                curBillselect.getString(105) + "}" + curBillselect.getString(106) + "}" + curBillselect.getString(107) + "}" +
                                curBillselect.getString(108) + "}" + curBillselect.getString(109) + "}" + curBillselect.getString(110) + "}" +
                                curBillselect.getString(111) + "}" + curBillselect.getString(112) + "}" + curBillselect.getString(113) + "}" +
                                curBillselect.getString(114) + "}" + curBillselect.getString(115) + "}" + curBillselect.getString(116) + "}" +
                                curBillselect.getString(117) + "}" + curBillselect.getString(118) + "}" + curBillselect.getString(119) + "}" +
                                curBillselect.getString(120) + "}" + curBillselect.getString(121) + "}" + curBillselect.getString(122) + "}" +
                                curBillselect.getString(123) + "}" + curBillselect.getString(124) + "}" + curBillselect.getString(125) + "}" +
                                curBillselect.getString(126) + "}" + curBillselect.getString(127) + "}" + curBillselect.getString(128) + "}" +
                                curBillselect.getString(129) + "}" + curBillselect.getString(130) + "}" + curBillselect.getString(131) + "}" +
                                curBillselect.getString(132) + "}" + curBillselect.getString(133) + "}" + curBillselect.getString(134) + "}" +
                                curBillselect.getString(135) + "}" + curBillselect.getString(136) + "}" + curBillselect.getString(137) + "}" +
                                curBillselect.getString(138) + "}" + curBillselect.getString(139) + "}" + curBillselect.getString(140) + "}" +
                                curBillselect.getString(141) + "}" + curBillselect.getString(142) + "}" + curBillselect.getString(143) + "}" +
                                curBillselect.getString(144) + "}" + curBillselect.getString(145) + "}" + curBillselect.getString(146) + "}" +
                                curBillselect.getString(147) + "}" + curBillselect.getString(148) + "}" + curBillselect.getString(149) + "}" +
                                curBillselect.getString(150) + "}" + curBillselect.getString(151) + "}" + curBillselect.getString(152) + "}" +
                                curBillselect.getString(153) + "}" + curBillselect.getString(154) + "}" + curBillselect.getString(155) + "}" +
                                curBillselect.getString(156) + "}" + curBillselect.getString(157) + "}" + curBillselect.getString(158) + "}" +
                                curBillselect.getString(159) + "}" + curBillselect.getString(160) + "}" + curBillselect.getString(161) + "}" +
                                curBillselect.getString(162) + "}" + curBillselect.getString(163) + "}" + curBillselect.getString(164) + "}" +
                                curBillselect.getString(165) + "}" + curBillselect.getString(166) + "}" + curBillselect.getString(167) + "}" +
                                curBillselect.getString(168) + "}" + curBillselect.getString(169) + "}" + curBillselect.getString(170) + "}" +
                                curBillselect.getString(171) + "}" + curBillselect.getString(172) + "}" + curBillselect.getString(173) + "}" +
                                curBillselect.getString(174) + "}" + curBillselect.getString(175) + "}" + curBillselect.getString(176) + "}" +
                                curBillselect.getString(177) + "}" + curBillselect.getString(178) + "}" + curBillselect.getString(179) + "}" +
                                curBillselect.getString(180) + "}" + curBillselect.getString(181) + "}" + curBillselect.getString(182) + "}" +
                                curBillselect.getString(183) + "}" + curBillselect.getString(184) + "}" + curBillselect.getString(185) + "}" +
                                curBillselect.getString(186) + "}" + curBillselect.getString(187) + "}" + curBillselect.getString(188) + "}" +
                                curBillselect.getString(189) + "}" + curBillselect.getString(190) + "}" + curBillselect.getString(191) + "}" +
                                curBillselect.getString(192) + "}" + curBillselect.getString(193) + "}" + curBillselect.getString(194) + "}" +
                                curBillselect.getString(195) + "}" + curBillselect.getString(196) + "}" + curBillselect.getString(197) + "}" +
                                curBillselect.getString(198) + "}" + curBillselect.getString(199) + "}" + curBillselect.getString(200) + "}" +
                                curBillselect.getString(201) + "}" + curBillselect.getString(202) + "}" + curBillselect.getString(203) + "}" +
                                curBillselect.getString(204) + "}" + curBillselect.getString(205) + "}" + curBillselect.getString(206) + "}" +
                                curBillselect.getString(207) + "}" + curBillselect.getString(208) + "}" + curBillselect.getString(209) + "}" +
                                curBillselect.getString(210) + "}" + curBillselect.getString(211)+ "}" + curBillselect.getString(212)+ "}" +
                                curBillselect.getString(213)+ "}" + curBillselect.getString(214)+ "}" + curBillselect.getString(215)+ "}"+
                                curBillselect.getString(216));

                        mylist1.add(curBillselect.getString(60) + "$" + curBillselect.getString(0) + "$" + curBillselect.getString(5) + "$" +
                                curBillselect.getString(61) + "$" + curBillselect.getString(11) + "$" + curBillselect.getString(62) + "$" +
                                curBillselect.getString(2) + "$" + i + "$" + curBillselect.getString(7) + "$" +
                                curBillselect.getString(212) + "$" + curBillselect.getString(1) + "$" + formattedDate + "$" +
                                curBillselect.getString(63) + "$" + curBillselect.getString(8) + "$" + curBillselect.getString(64) + "$" +
                                curBillselect.getString(65) + "$" + curBillselect.getString(66) + "$" + curBillselect.getString(39) + "$" +
                                curBillselect.getString(10) + "$" + curBillselect.getString(67) + "$" + curBillselect.getString(68) + "$" +
                                curBillselect.getString(69) + "$" + curBillselect.getString(70) + "$" + curBillselect.getString(71) + "$" +
                                curBillselect.getString(182) + "$" + curBillselect.getString(72) + "$" + curBillselect.getString(73) + "$" +
                                column_24 + "$" + curBillselect.getString(74) + "$" + "0" + "$" + curBillselect.getString(75) + "$" +
                                curBillselect.getString(76) + "$" + curBillselect.getString(27) + "$" + curBillselect.getString(77) + "$" +
                                curBillselect.getString(26) + "$" + curBillselect.getString(78) + "$" + curBillselect.getString(79) + "$" +
                                curBillselect.getString(80) + "$" + curBillselect.getString(81) + "$" + curBillselect.getString(82) + "$" +
                                curBillselect.getString(201) + "$" + curBillselect.getString(83) + "$" + curBillselect.getString(179) + "$" +
                                curBillselect.getString(85) + "$" + curBillselect.getString(86) + "$" + curBillselect.getString(169) + "$" +
                                curBillselect.getString(87) + "$" + curBillselect.getString(88) + "$" + curBillselect.getString(89) + "$" +
                                curBillselect.getString(90) + "$" + curBillselect.getString(91) + "$" + curBillselect.getString(92) + "$" +
                                curBillselect.getString(93) + "$" + curBillselect.getString(94) + "$" + curBillselect.getString(95) + "$" +
                                curBillselect.getString(96) + "$" + curBillselect.getString(97) + "$" + curBillselect.getString(98) + "$" +
                                curBillselect.getString(99) + "$" + curBillselect.getString(178) + "$0$");

//                        mylistbck.add(curBillselect.getString(44)+ "|" +curBillselect.getString(50) );

                        moveFile(ZipSourcePath, curBillselect.getString(48), ZipCopyPath);
                        moveFile(ZipSourcePath, curBillselect.getString(49), ZipCopyPath);

                        curBillselect.moveToNext();
                    }

                    generateNoteOnSD(getApplicationContext(), "billing.csv", mylist);
                    generateNoteOnSD(getApplicationContext(), "billing1.csv", mylist1);
//                    generatebackupNoteOnSD(getApplicationContext(), "mbc_Ob.csv", mylist);

                }

                SLPrintingMainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progress.dismiss();
                        new PostClass(SLPrintingMainActivity.this).execute();
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
//            new PostClass(SDActivity.this).execute();
        }

    }

    private class PostClass extends AsyncTask<String, Void, Boolean> {

        private final Context context;
        public String succsess = null;

        public PostClass(Context c) {

            this.context = c;

        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Sending Data...");
            progress.show();

            dbHelper3 = new DB(getApplicationContext());
            SD3 = dbHelper3.getWritableDatabase();
            String frt[] = new String[0];

            String serImgQwer = "Select User_Mtr_Img,User_Sig_Img from TBL_BILLING WHERE Upload_Flag='N'";
            Cursor curBillImg = SD3.rawQuery(serImgQwer, null);
            if (curBillImg != null && curBillImg.moveToFirst()) {

                Log.e(getApplicationContext(), "SLPrintAct", "Update Success");


                mylistimagename.add(curBillImg.getString(0));
                mylistimagename.add(curBillImg.getString(1));
                Log.e(getApplicationContext(), "SLPrintAct", "mtr_img" + curBillImg.getString(0) + "sig_img" + curBillImg.getString(1));
            }

            ArrayList<String> stringArrayList = new ArrayList<String>();
            for (int j = 0; j < mylistimagename.size(); j++) {

                stringArrayList.add(Environment.getExternalStorageDirectory() + "/MBC/" + mylistimagename.get(j)); //add to arraylist
            }
            String[] files = stringArrayList.toArray(new String[stringArrayList.size()]);
            String[] file = {Zip, signaturePathDes, photoPathDes};

            zipFolder(ZipCopyPath, ZipDesPath);
            GSBilling.getInstance().setFinalZipName(ZipDesPathdup);

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                // Set your file path here
//                System.out.println("FILENAME IS1 "+GSBilling.getInstance().getFinalZipName());
                FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip");
                Log.e(getApplicationContext(), "SLPrintAct", "FILENAME IS12 " + fstrm);

                // Set your server page url (and the file title/description)

//                HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi_Testing/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                HttpFileUpload hfu = new HttpFileUpload(URLS.DataComm.billUpload, "" + GSBilling.getInstance()
                        .getFinalZipName(), ".zip");
//                HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");

                // Log.e(getApplicationContext(), "http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles", "" + GSBilling.getInstance().getFinalZipName()+".zip");
                Log.e(getApplicationContext(), "SLPrintAct", "going out " + GSBilling.getInstance().getFinalZipName() + ".zip");
                int status = hfu.Send_Now(fstrm);
                if (status != 200) {
//                    succsess = "1";
                    SLPrintingMainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();
                            Toast.makeText(SLPrintingMainActivity.this, "Internaly Stored Due to No Connectivity", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(SLPrintingMainActivity.this, BillingtypesActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("printtype", print_type);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);

                        }
                    });

                } else {
//                    succsess = "0";
                    dbHelper2 = new DB(getApplicationContext());
                    SD2 = dbHelper2.getWritableDatabase();

//                        String updatequer = "UPDATE  TBL_BILLING  SET Upload_Flag = 'Y' WHERE  Cons_Number = '" + curBillselect.getString(0) + "' and  Bill_Month='" + curBillselect.getString(5) + "'";
                    String updatequer = "UPDATE  TBL_BILLING  SET Upload_Flag = 'Y'";
                    Cursor curBillupdate = SD2.rawQuery(updatequer, null);
                    if (curBillupdate != null && curBillupdate.moveToFirst()) {
                        Log.e(getApplicationContext(), "SLPrintAct", "Update Success");
                    }

                    SLPrintingMainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();
                            Toast.makeText(SLPrintingMainActivity.this, " Successfully Uploaded to Server ", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SLPrintingMainActivity.this, BillingtypesActivity.class);
                            //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            //  intent.putExtra("printtype", print_type);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);

                        }
                    });
                }

//                return true;

            } catch (Exception e) {
                // Error: File not found
                succsess = "0";
                e.printStackTrace();
//                return  false;
            }

            return true;

        }

        protected void onPostExecute() {
            progress.dismiss();

            new File(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip").delete();
            Intent intent = new Intent(SLPrintingMainActivity.this, BillingtypesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("printtype", print_type);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_in_left,
                    R.anim.anim_slide_out_left);


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

////             delete the original file
//            new File(inputPath + inputFile).delete();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteFile(String inputPath, String inputFile) {
        try {
            // delete the original file
            new File(inputPath + inputFile).delete();


        } catch (Exception e) {
            Log.e(getApplicationContext(), "SLPrintAct", "tag" + e.getMessage());
        }
    }

    public void generateLastREC(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "mrcn/last/");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            int length = sBody.size();
            for (int i = 0; i < length; i++) {
//                System.out.println("selqwer1234 " + sBody.get(i));

                writer.append(sBody.get(i).toString());
                writer.append("\n");
            }

            writer.flush();
            writer.close();
            System.out.println("success file");

//                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateNoteOnSD(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "MBC/Downloadsingular/");
            if (!root.exists()) {
                root.mkdirs();
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

    public void generatebackupNoteOnSD(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "MBC/");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            int length = sBody.size();
            for (int i = 0; i < length; i++) {
                System.out.println("selqwer1234 " + sBody.get(i));
                BufferedWriter writerbuf = new BufferedWriter(new FileWriter(gpxfile, true));
                writerbuf.write(sBody.get(i).toString());
                writerbuf.close();
//                writer.append(sBody.get(i).toString());
//                writer.append("\n");
            }

            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    void DeleteRecursive(File dir) {
        Log.e(getApplicationContext(), "SLPrintAct", "DeleteRecursive DELETEPREVIOUS TOP" + dir.getPath());
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                File temp = new File(dir, children[i]);
                if (temp.isDirectory()) {
                    Log.e(getApplicationContext(), "SLPrintAct", "DeleteRecursive Recursive Call" + temp.getPath());
                    DeleteRecursive(temp);
                } else {
                    Log.e(getApplicationContext(), "SLPrintAct", "DeleteRecursive Delete File" + temp.getPath());
                    boolean b = temp.delete();
                    if (b == false) {
                        Log.e(getApplicationContext(), "SLPrintAct", "DeleteRecursive DELETE FAIL");
                    }
                }
            }
        }
        dir.delete();
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

    public static void zipFolder(String inputFolderPath, String outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outZipPath);
//            GSBilling.getInstance().setFinalZipName();
            ZipOutputStream zos = new ZipOutputStream(fos);
            File srcFile = new File(inputFolderPath);
            File[] files = srcFile.listFiles();
            android.util.Log.d("", "Zip directory: " + srcFile.getName());
            for (int i = 0; i < files.length; i++) {
                android.util.Log.d("", "Adding file: " + files[i].getName());
                byte[] buffer = new byte[1024];
                FileInputStream fis = new FileInputStream(files[i]);
                zos.putNextEntry(new ZipEntry(files[i].getName()));
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

}

