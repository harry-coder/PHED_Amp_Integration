package com.fedco.mbc.testing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.billinglogic.CBillling;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.model.Structbilling;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.model.Structtariff;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.UtilAppCommon;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class TestMainActivity extends Activity {

    final Context context = this;
    UtilAppCommon uac;
    DB dbHelper, dbHelper2,dbHelper4;
    SQLiteDatabase SD, SD2, SD3,SD4;
    SimpleDateFormat comparingDate;
    Date TarifToDate;
    Date dateToday;
    Cursor curSearchdata;
    String tariffEffetiveDate;
    CBillling calBill;
    UtilAppCommon appCom;
    private ProgressDialog progress;
    String finadate = null;
    UtilAppCommon appcomUtil;
    SessionManager session;
    String key;
    Logger Log;
    Long billseq;
    Long dcSeq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main);

        dbHelper = new DB(getApplicationContext());
        SD = dbHelper.getWritableDatabase();
        appcomUtil=new UtilAppCommon();

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
//        key = session.retLicence();
        key = appcomUtil.UniqueCode(getApplicationContext());

        Structbilling.SBM_No = key;
        Structbilling.Meter_Reader_Name = session.retMRName();
        Structbilling.Meter_Reader_ID = session.retMRID();

        String ret = "SELECT * FROM TBL_CONSMAST WHERE Consumer_Number='5860'";
//    String ret = "SELECT * FROM TBL_CONSMAST";
//        String ret = "SELECT * FROM TBL_CONSMAST WHERE Consumer_Number in('7006','7067','7080')";

        Cursor curconsmasData = SD.rawQuery(ret, null);

        if (curconsmasData != null && curconsmasData.moveToFirst()) {

            do {

                System.out.println("DATAATTATATATATATAT "+curconsmasData.getString(0));
                System.out.println("DATAATTATATATATATAT "+curconsmasData.getString(1));
                System.out.println("DATAATTATATATATATAT "+curconsmasData.getString(2));
                System.out.println("DATAATTATATATATATAT "+curconsmasData.getString(3));

                uac = new UtilAppCommon();
                try {

                    uac.copyResultsetToConmasClass(curconsmasData);
                    uac.princonmas();
                    tarrifCalculate();
                    billingCalculate();
                    storeData();

                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }


            } while (curconsmasData.moveToNext());

//
        }
        new LongOperationSurvey().execute();
    }

    public String getRMScodefromSybase(String Sybasecode)
    {
        dbHelper = new DB(getApplicationContext());
        SD4 = dbHelper.getWritableDatabase();
        String SybaseQuery = "SELECT RMS_CODE FROM TBL_TARIFF_SYBASE where SYBASE_CODE='" + Sybasecode + "'";
        System.out.println("query---" + SybaseQuery);

        Cursor curSybaseTariff = SD4.rawQuery(SybaseQuery, null);
        if (curSybaseTariff.moveToFirst()) {
            Structconsmas.Tariff_Code=curSybaseTariff.getString(0);
            return curSybaseTariff.getString(0);
        }
        return Sybasecode;
    }
    private void tarrifCalculate() {
        dbHelper = new DB(getApplicationContext());
        SD = dbHelper.getWritableDatabase();
        appcomUtil=new UtilAppCommon();
        //	CBillling cb=new CBillling();
        Calendar c = Calendar.getInstance();

        SimpleDateFormat month_date = new SimpleDateFormat("MMMM-yy");
        final String month_name = month_date.format(c.getTime());

        comparingDate = new SimpleDateFormat("dd-MM-yyyy");
        String currentDateTime = comparingDate.format(new Date());
        try {
            dateToday = comparingDate.parse(currentDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {

            //for holding retrieve data from query and store in the form of rows
            String ret = "SELECT  DISTINCT  TARIFF_TO_DATE  FROM TBL_TARRIF_MP DESC LIMIT 1";
            System.out.println("query---" + ret);
//            if (SD.isOpen()){
//                dbHelper.close();
//            }
            curSearchdata = SD.rawQuery(ret, null);
            Toast.makeText(getApplicationContext(), "Total data Found:" + curSearchdata.getCount(), Toast.LENGTH_LONG).show();

            if (curSearchdata.moveToFirst()) {

                do {

                    System.out.println("DATA 1 : " + curSearchdata.getString(0));

                    tariffEffetiveDate = curSearchdata.getString(0);
                    try {
                        //data base tarrif todate
                        TarifToDate = comparingDate.parse(curSearchdata.getString(0));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } while (curSearchdata.moveToNext());//Move the cursor to the next row.
            } else {
                Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
        }


        System.out.println("Current Date : " + dateToday);
        System.out.println("Distinct Tariff To Date: " + TarifToDate);

        switch (dateToday.compareTo(TarifToDate)) {
            case -1:
                System.out.println("today is sooner than questionDate");

                try {


                    String newTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim() + "' AND TARIFF_TO_DATE='" + tariffEffetiveDate + "'";
                    if(Structconsmas.SYSTEM_FLAG != null && !Structconsmas.SYSTEM_FLAG.isEmpty()){ // null check empty chk
                        if(Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S"))
                        {
                            newTarrifQwery= "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + getRMScodefromSybase(Structconsmas.Tariff_Code.trim()) + "' AND TARIFF_TO_DATE='" + tariffEffetiveDate + "'";
                        }

                    }

                    System.out.println("query---" + newTarrifQwery);
                    dbHelper2 = new DB(getApplicationContext());
                    SD3 = dbHelper2.getWritableDatabase();

                    Cursor curnewTarrif = SD3.rawQuery(newTarrifQwery, null);

                    if (curnewTarrif.moveToFirst()) {

                        do {

                            System.out.println("TARIFF DATA 1 : " + curnewTarrif.getString(0));
                            System.out.println("TARIFF DATA 2 : " + curnewTarrif.getString(1));
                            System.out.println("TARIFF DATA 3 : " + curnewTarrif.getString(2));
                            System.out.println("TARIFF DATA 4 : " + curnewTarrif.getString(3));

                            uac = new UtilAppCommon();
                            try {
                                uac.copyResultsetToTarrifClass(curnewTarrif);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        } while (curnewTarrif.moveToNext());
                        //Move the cursor to the next row.
                    } else {
                        Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();//
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                break;
            case 0:
                System.out.println("today and questionDate are equal");

                try {

                    dbHelper2 = new DB(getApplicationContext());
                    SD3 = dbHelper2.getWritableDatabase();
                    String newTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim() + "' AND TARIFF_TO_DATE='" + tariffEffetiveDate + "'";
                    if(Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S"))
                    {
                        newTarrifQwery= "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + getRMScodefromSybase(Structconsmas.Tariff_Code.trim()) + "' AND TARIFF_TO_DATE='" + tariffEffetiveDate + "'";
                    }
                    System.out.println("query---" + newTarrifQwery);
                    Cursor curnewTarrif = SD3.rawQuery(newTarrifQwery, null);

                    if (curnewTarrif.moveToFirst()) {

                        do {

                            System.out.println("TARIFF DATA 5 : " + curnewTarrif.getString(0));
                            System.out.println("TARIFF DATA 6 : " + curnewTarrif.getString(1));
                            System.out.println("TARIFF DATA 7 : " + curnewTarrif.getString(2));
                            System.out.println("TARIFF DATA 8 : " + curnewTarrif.getString(3));

                            uac = new UtilAppCommon();
                            try {
                                uac.copyResultsetToTarrifClass(curnewTarrif);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        } while (curnewTarrif.moveToNext());//Move the cursor to the next row.
                    } else {
                        Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                try {

                    String oldTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim() + "' AND TARIFF_TO_DATE!='" + tariffEffetiveDate + "'";
                    if(Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S"))
                    {
                        oldTarrifQwery= "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + getRMScodefromSybase(Structconsmas.Tariff_Code.trim()) + "' AND TARIFF_TO_DATE='" + tariffEffetiveDate + "'";
                    }
                    System.out.println("query---" + oldTarrifQwery);
                    Cursor curOLDTarrif = SD3.rawQuery(oldTarrifQwery, null);

                    if (curOLDTarrif.moveToFirst()) {

                        do {

                            System.out.println("DATA 1 : " + curOLDTarrif.getString(0));

                            uac = new UtilAppCommon();
                            try {
                                uac.copyResultsetToOLDTarrifClass(curOLDTarrif);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        } while (curOLDTarrif.moveToNext());//Move the cursor to the next row.
                    } else {
                        Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case 1:
                System.out.println("today is later than questionDate");

                try {

                    dbHelper2 = new DB(getApplicationContext());
                    SD3 = dbHelper2.getWritableDatabase();
                    String newTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim() + "' AND TARIFF_TO_DATE='" + tariffEffetiveDate + "'";
                    System.out.println("query---" + newTarrifQwery);
                    Cursor curnewTarrif = SD3.rawQuery(newTarrifQwery, null);

                    if (curnewTarrif.moveToFirst()) {

                        do {

                            System.out.println("DATA 1 : " + curnewTarrif.getString(0));

                            uac = new UtilAppCommon();
                            try {
                                uac.copyResultsetToTarrifClass(curnewTarrif);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        } while (curnewTarrif.moveToNext());//Move the cursor to the next row.
                    } else {
                        Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                try {

                    String oldTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim() + "' AND TARIFF_TO_DATE!='" + tariffEffetiveDate + "'";
                    System.out.println("query---" + oldTarrifQwery);
                    Cursor curOLDTarrif = SD3.rawQuery(oldTarrifQwery, null);

                    if (curOLDTarrif.moveToFirst()) {

                        do {

                            System.out.println("DATA 1 : " + curOLDTarrif.getString(0));

                            uac = new UtilAppCommon();
                            try {
                                uac.copyResultsetToOLDTarrifClass(curOLDTarrif);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        } while (curOLDTarrif.moveToNext());//Move the cursor to the next row.
                    } else {
                        Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                break;
            default:
                System.out.println("Invalid results from date comparison");
                break;
        }

        System.out.println("Previous MET READ DATE :" + Structconsmas.Prev_Meter_Reading_Date);
        System.out.println("Tariff to DATE :" + Structtariff.TARIFF_TO_DATE);

        Date varDate = null;
        Date varDate2 = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String DTime = sdf.format(new Date());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        try {

            if(Structconsmas.Prev_Meter_Reading_Date.isEmpty()){
                varDate = dateFormat.parse(Structconsmas.BILL_ISSUE_DATE);
                varDate2 = dateFormat.parse(Structconsmas.BILL_ISSUE_DATE);

                String billISDate = Structconsmas.BILL_ISSUE_DATE.substring(0, 4);
                String billISDate2 = Structconsmas.BILL_ISSUE_DATE.substring(4, 6);
                String billISDate3 = Structconsmas.BILL_ISSUE_DATE.substring(6, 8);

                System.out.println("BILLISSUE DATE 1 :" + billISDate);
                System.out.println("BILLISSUEDATE  2 :" + billISDate2);
                System.out.println("BILLISSUEDATE  3 :" + billISDate3);


                dateFormat = new SimpleDateFormat("dd-MM-yyyy");//22-04-0017
                System.out.println("Date PMRD: " + dateFormat.format(varDate));
                System.out.println("Date PMRD: " + dateFormat.format(varDate2));

                String date1 = dateFormat.format(varDate).substring(0, 6);
                String date2 = dateFormat.format(varDate).substring(8, 10);

                String date3 = dateFormat.format(varDate2).substring(0, 6);
                String date4 = dateFormat.format(varDate2).substring(8, 10);

                String finadate = date1 + "20" + date2;
                try {
                    SimpleDateFormat format = new SimpleDateFormat("dd-mm-yyyy");

//                   Date varDate2 = format.parse(Structconsmas.BILL_ISSUE_DATE);
                    String date = Structconsmas.BILL_ISSUE_DATE;

                    Date d = format.parse(finadate);
                    Date dateBefore = new Date(d.getTime() - 5 * 24 * 3600 * 1000l );
                    System.out.print("************** "+format.format(dateBefore)); // print 15-10-2015
                }
                catch(ParseException pe) {
                    pe.printStackTrace();
                }
                String finadate2 = date3 + "20" + date4;
                System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDA :" + finadate);

                if (Structtariff.OLD_EFFECTIVE_DATE != null) {
                    Structbilling.OLD_dateDuration = printDifference(finadate, Structtariff.OLD_TARIFF_TO_DATE);//24-03-2017 ,01-04-2099 ---diff is 29958 days
//            Structbilling.OLD_dateDuration= printDifference(billISDate3+"-"+billISDate2+"-"+billISDate, Structtariff.EFFECTIVE_DATE);//01-03-2017, 01-04-2017 31 days
                    Structbilling.dateDuration = printDifference(Structtariff.EFFECTIVE_DATE, finadate2);//01-03-2017, 01-04-2017 31 days
                } else {
                    Structbilling.OLD_dateDuration = 0l;
                    Structbilling.dateDuration = printDifference(finadate, finadate2);//01-03-2017, 01-04-2017 31 days

                }

            }else{
                varDate = dateFormat.parse(Structconsmas.Prev_Meter_Reading_Date);
                varDate2 = dateFormat.parse(Structconsmas.BILL_ISSUE_DATE);

                String billISDate = Structconsmas.BILL_ISSUE_DATE.substring(0, 4);
                String billISDate2 = Structconsmas.BILL_ISSUE_DATE.substring(4, 6);
                String billISDate3 = Structconsmas.BILL_ISSUE_DATE.substring(6, 8);

                System.out.println("BILLISSUE DATE 1 :" + billISDate);
                System.out.println("BILLISSUEDATE  2 :" + billISDate2);
                System.out.println("BILLISSUEDATE  3 :" + billISDate3);


                dateFormat = new SimpleDateFormat("dd-MM-yyyy");//22-04-0017
                System.out.println("Date PMRD: " + dateFormat.format(varDate));
                System.out.println("Date PMRD: " + dateFormat.format(varDate2));

                String date1 = dateFormat.format(varDate).substring(0, 6);
                String date2 = dateFormat.format(varDate).substring(8, 10);

                String date3 = dateFormat.format(varDate2).substring(0, 6);
                String date4 = dateFormat.format(varDate2).substring(8, 10);

                String finadate = date1 + "20" + date2;
                String finadate2 = date3 + "20" + date4;
                System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDA :" + finadate);



                if (Structtariff.OLD_EFFECTIVE_DATE != null) {
                    Structbilling.OLD_dateDuration = printDifference(finadate, Structtariff.OLD_TARIFF_TO_DATE);//24-03-2017 ,01-04-2099 ---diff is 29958 days
//            Structbilling.OLD_dateDuration= printDifference(billISDate3+"-"+billISDate2+"-"+billISDate, Structtariff.EFFECTIVE_DATE);//01-03-2017, 01-04-2017 31 days
                    Structbilling.dateDuration = printDifference(Structtariff.EFFECTIVE_DATE, finadate2);//01-03-2017, 01-04-2017 31 days
                } else {
                    Structbilling.OLD_dateDuration = 0l;
                    Structbilling.dateDuration = printDifference(finadate, finadate2);//01-03-2017, 01-04-2017 31 days

                }

            }


        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }


    private void billingCalculate() {

        dbHelper = new DB(getApplicationContext());
        SD = dbHelper.getWritableDatabase();
        appcomUtil=new UtilAppCommon();
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


        String curmeterreading = Structconsmas.CUR_READING;
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

            finadate = date1 + "20" + date2;
            System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDA :" + finadate);

//            printDifference(finadate, Structtariff.TARIFF_TO_DATE);
//            printDifference(billISDate3+"-"+billISDate2+"-"+billISDate, Structtariff.EFFECTIVE_DATE);
//            dateDuration = printDifference(finadate, Structconsmas.CUR_READ_DATE);
            dateDuration = Structbilling.dateDuration;
            calBill.totalDateduration = dateDuration;
            System.out.println("DATE DURATION  :" + calBill.totalDateduration);
            System.out.println("DATE DURATION 2  :" + dateDuration);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

//        String cnvrtddate = calBill.convyyyymmdd(Structconsmas.Prev_Meter_Reading_Date);
//        dateDuration = calBill.DateDifference(cnvrtddate, Structconsmas.CUR_READ_DATE);


        calBill.curMeterRead = curmeterreading;
        calBill.curMeterReadDate = Structconsmas.CUR_READ_DATE;
        System.out.println("CUR READ DATE :" + Structconsmas.CUR_READ_DATE);

        calBill.curMeterStatus = Integer.parseInt(Structconsmas.CUR_MET_STATUS);
        calBill.derivedMeterStatus = Structconsmas.CUR_MET_STATUS;

        Structbilling.Derived_mtr_status = Structconsmas.CUR_MET_STATUS;
        Structbilling.Cur_Meter_Stat = Integer.parseInt(Structconsmas.CUR_MET_STATUS);

        switch (Structconsmas.CUR_MET_STATUS) {
            case "0"://Premisses LOcked HLK(5)//MP NORMAL IS 1

            {
                calculatedunit = calBill.Unitcalculation("0", curmeterreading, 0);

                break;
            }
            case "1"://Premisses LOcked HLK(5)//MP NORMAL IS 1

            {
                calculatedunit = calBill.Unitcalculation(Structconsmas.CUR_MET_STATUS, curmeterreading, Integer.parseInt(Structconsmas.CUR_MET_STATUS));

                break;
            }

            case "2"://Premisses LOcked HLK(5)//MP NORMAL IS 1

            {
                calculatedunit = calBill.Unitcalculation(Structconsmas.CUR_MET_STATUS, curmeterreading, Integer.parseInt(Structconsmas.CUR_MET_STATUS));


                break;
            }
            case "3"://Premisses LOcked HLK(5)//MP NORMAL IS 1

            {
                calculatedunit = calBill.Unitcalculation(Structconsmas.CUR_MET_STATUS, curmeterreading, Integer.parseInt(Structconsmas.CUR_MET_STATUS));


                break;
            }

            case "4"://Premisses LOcked HLK(5)//MP NORMAL IS 1

            {
                calculatedunit = calBill.Unitcalculation(Structconsmas.CUR_MET_STATUS, curmeterreading, Integer.parseInt(Structconsmas.CUR_MET_STATUS));


                break;
            }

            case "9"://Premisses LOcked HLK(5)//MP NORMAL IS 1

            {
                calculatedunit = calBill.Unitcalculation(Structconsmas.CUR_MET_STATUS, curmeterreading, Integer.parseInt(Structconsmas.CUR_MET_STATUS));


                break;
            }
        }
        calBill.unit = calculatedunit;
        calBill.CalculateBill();

        uac.copyResultsetToBillingClass(calBill);

    }


    public Long printDifference(String str1, String str2) {


        System.out.println("print diff startDate nnnnnnnnn : " + str1);
        System.out.println("print diff nnnnnnnnn2: " + str2);

        SimpleDateFormat dates = new SimpleDateFormat("dd-MM-yyyy");

        //Setting dates
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = dates.parse(String.valueOf(str1));
            endDate = dates.parse(String.valueOf(str2));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);

        return elapsedDays;
    }


    private void storeData() {
//        dbHelper = new DB(getApplicationContext());
//        SD = dbHelper.getWritableDatabase();
//
        appcomUtil=new UtilAppCommon();
        dbHelper.insertIntoMPBillingTable();
//        EnterTextAsyc asynctask = new EnterTextAsyc();
//        asynctask.execute(0);


    }



    /*--------------------- Survey Masters Download Initiation ----------------------------*/
    private class LongOperationSurvey extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

//            String delPrev = "DELETE FROM TBL_SURVEYMASTER";
//
//            SD3.beginTransaction();
//            SD3.execSQL(delPrev);
//            SD3.setTransactionSuccessful();
//            SD3.endTransaction();
//            SD3.close();
            try {

                dbHelper2 = new DB(getApplicationContext());
                SD2 = dbHelper2.getWritableDatabase();

                String selquer = "SELECT * FROM TBL_BILLING ";//WHERE Upload_Flag='N'
                Cursor curBillselect = SD2.rawQuery(selquer, null);
                String arrStr[] = null;
                ArrayList<String> mylist = new ArrayList<String>();



                if (curBillselect != null && curBillselect.moveToFirst()) {

                    while (curBillselect.isAfterLast() == false) {
                        String name = curBillselect.getString(0);

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
                                curBillselect.getString(210) + "}" + curBillselect.getString(211) );



                        curBillselect.moveToNext();
                    }

                    generateNoteOnSD(getApplicationContext(), "mpBillling.csv", mylist);

                }


            } catch (NullPointerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            //  SD.close();

            new LongOperationSurvey2().execute();
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {


        }


    }


    /*--------------------- Survey Masters Download Initiation ----------------------------*/
    private class LongOperationSurvey2 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try {

                dbHelper2 = new DB(getApplicationContext());
                SD2 = dbHelper2.getWritableDatabase();

                String selquer = "SELECT * FROM TBL_BILLING ";//WHERE Upload_Flag='N'
                Cursor curBillselect = SD2.rawQuery(selquer, null);
                String arrStr[] = null;
                ArrayList<String> mylist = new ArrayList<String>();
                ArrayList<String> mylist1 = new ArrayList<String>();


                if (curBillselect != null && curBillselect.moveToFirst()) {
                    int i=0;
                    while (curBillselect.isAfterLast() == false) {
                        String name = curBillselect.getString(0);
                        Calendar c = Calendar.getInstance();
                        System.out.println("Current time => " + c.getTime());
                        i=i+1;
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yy");
                        String formattedDate = df.format(c.getTime());
                        mylist1.add(curBillselect.getString(60)+ "$" + curBillselect.getString(0) + "$" + curBillselect.getString(5) + "$" +
                                curBillselect.getString(61)+ "$" + curBillselect.getString(11)+ "$" + curBillselect.getString(62)+ "$" +
                                curBillselect.getString(2) + "$" + i + "$" + curBillselect.getString(7) + "$" +
                                curBillselect.getString(212)+ "$" + curBillselect.getString(1) + "$" + formattedDate + "$" +
                                curBillselect.getString(63)+ "$" + curBillselect.getString(8) + "$" + curBillselect.getString(64)+ "$" +
                                curBillselect.getString(65)+ "$" + curBillselect.getString(66)+ "$" + curBillselect.getString(39)+ "$" +
                                curBillselect.getString(10)+ "$" + curBillselect.getString(67)+ "$" + curBillselect.getString(68)+ "$" +
                                curBillselect.getString(69)+ "$" + curBillselect.getString(70)+ "$" + curBillselect.getString(71)+ "$" +
                                curBillselect.getString(182) + "$" + curBillselect.getString(72)+ "$" + curBillselect.getString(73)+ "$" +
                                curBillselect.getString(24)+ "$" + curBillselect.getString(74)+ "$" + "0"+ "$" + curBillselect.getString(75)+ "$" +
                                curBillselect.getString(76)+ "$" + curBillselect.getString(27)+ "$" + curBillselect.getString(77)+ "$" +
                                curBillselect.getString(26)+ "$" + curBillselect.getString(78)+ "$" + curBillselect.getString(79)+ "$" +
                                curBillselect.getString(80)+ "$" + curBillselect.getString(81)+ "$" + curBillselect.getString(82)+ "$" +
                                curBillselect.getString(201) + "$" + curBillselect.getString(83)+ "$" + curBillselect.getString(84)+ "$" +
                                curBillselect.getString(85)+ "$" + curBillselect.getString(86)+ "$" + curBillselect.getString(169) + "$" +
                                curBillselect.getString(87)+ "$" + curBillselect.getString(88)+ "$" + curBillselect.getString(89)+ "$" +
                                curBillselect.getString(90)+ "$" + curBillselect.getString(91)+ "$" + curBillselect.getString(92)+ "$" +
                                curBillselect.getString(93)+ "$" + curBillselect.getString(94)+ "$" + curBillselect.getString(95)+ "$" +
                                curBillselect.getString(96)+ "$" + curBillselect.getString(97)+ "$" + curBillselect.getString(98)+ "$" +
                                curBillselect.getString(99)+ "$" + curBillselect.getString(178) + "$0$" );


                        curBillselect.moveToNext();
                    }


                    generateNoteOnSD(getApplicationContext(), "mpBillling1.csv", mylist1);
                }


            } catch (NullPointerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            dbHelper = new DB(getApplicationContext());
            SD = dbHelper.getWritableDatabase();

            String del_Prev_Pole_Data = "DELETE FROM TBL_BILLING";

            SD.beginTransaction();
            SD.execSQL(del_Prev_Pole_Data);
            SD.setTransactionSuccessful();
            SD.endTransaction();
            //  SD.close();


            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {


        }


    }

    public void generateNoteOnSD(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "MPTesting/");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            int length = sBody.size();
            for (int i = 0; i < length; i++) {
                System.out.println("$ s eparated " + sBody.get(i));

                writer.append(sBody.get(i).toString());
                writer.append("\n");
            }

            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    public class EnterTextAsyc extends AsyncTask<Integer, Integer, Integer> {
//        /* displays the progress dialog until background task is completed*/
//        @Override
//        protected void onPreExecute() {
//
//        }
//
//        /* Task of EnterTextAsyc performing in the background*/
//        @Override
//        protected Integer doInBackground(Integer... params) {
////            slPrintingMainActivity = new SLPrintingMainActivity();
//            appcomUtil = new UtilAppCommon();
////            if (print_type.equals("1")) {
////            slPrintingMainActivity.sendMessage("<0x9>CON CODE :" + Structbilling.Cons_Number + "\n" +
////                    "<0x9>B. ID    : 0001 " + "\n" +
////                    "<0x09>BILL DATE: " + Structbilling.Bill_Date + "\n" +
////                    "<0x09>Upt(" + Structbilling.Due_Date + "):" + (String.format("%.2f", Structbilling.Cur_Bill_Total)) + "\n" +
////                    "<0x09>By(" + Structbilling.Due_Date + "):" + (String.format("%.2f", +Structbilling.Amnt_bPaid_on_Rbt_Date)) + "\n" +
////                    "<0x09>Aft(" + Structbilling.Due_Date.trim() + "):" + (String.format("%.2f", Structbilling.Amnt_Paidafter_Rbt_Date)) + "\n" +
////                    "<0x10>  CESU ELECTRICITY BILL" + "\n" +
////                    "<0x10>           2016" + "\n" +
////                    "<0x09>------------------------" + "\n" +
////                    "<0x09>B. ID      : 0001" + "\n" +
////                    "<0x09>DATE & TIME: " + Structbilling.Bill_Date + "\n" +
////                    "<0x09>BILL MONTHS: " + Structbilling.Bill_Month + "\n" +
////                    "<0x09>DUE DATE   : " + Structbilling.Due_Date + "\n" +
////                    "<0x09>DIVISION   : " + Structconsmas.Division_Name + "\n" +
////                    "<0x09>SUB-DIV    : " + Structconsmas.Sub_division_Name + "\n" +
////                    "<0x09>SECTION    : " + Structconsmas.Section_Name + "\n" +
////                    "<0x09>NAME :  " + Structconsmas.Name + "\n" +
////                    "<0x10>ADDRESS   : " + "\n" +
////                    "<0x09>" + Structconsmas.address1 + Structconsmas.address2 +
////                    "<0x09>TARIFF CATG:  " + Structconsmas.Tariff_Code + "\n" +
////                    "<0x09>PHASE      : " + "1" + "\n" +
////                    "<0x09>C.LOAD     : " + Structconsmas.Load + "\n" +
////                    "<0x09>MF         : " + Structconsmas.Multiply_Factor + "\n" +
////                    "<0x09>---------------------" + "\n" +
////                    "<0x09>PRES.BL DATE :" + Structbilling.Bill_Date + "\n" +
////                    "<0x09>PREV.BL DATE :" + Structconsmas.Prev_Meter_Reading_Date + "\n" +
////                    "<0x09>PRES.READING :  " + Structbilling.Cur_Meter_Reading + "\n" +
////                    "<0x09>PREV.READING :  " + Structconsmas.Prev_Meter_Reading + "\n" +
////                    "<0x09>PRES.STATUS  :  " + Structbilling.Cur_Meter_Stat + "\n" +
////                    "<0x09>C.UNITS      :  " + Structbilling.Units_Consumed + "\n" +
////                    "<0x09>C.MONTHS     :  " + Structbilling.Bill_Period + "\n" +
////                    "<0x10>ARREARS : " + "\n" +
////                    "<0x09>PREV FY ARR   :  " + (String.format("%.2f", Structconsmas.Pre_Financial_Yr_Arr)) + "\n" +
////                    "<0x09>CURR FY ARR   :  " + (String.format("%.2f", Structconsmas.Cur_Fiancial_Yr_Arr)) + "\n" +
////                    "<0x09>Current Bill   " + "\n" +
////                    "<0x09>---------------" + "\n" +
////                    "<0x09>Current D.P.S :  " + (String.format("%.2f", Structconsmas.Delay_Payment_Surcharge)) + "\n" +
////                    "<0x09>Fix/Mis Chg   :  " + (String.format("%.2f", Structbilling.Monthly_Min_Charg_DC)) + "\n" +
////                    "<0x09>EC1           :  " + (String.format("%.2f", Structbilling.Slab_1_EC)) + "\n" +
////                    "<0x09>EC2           :  " + (String.format("%.2f", Structbilling.Slab_2_EC)) + "\n" +
////                    "<0x09>EC3           :  " + (String.format("%.2f", Structbilling.Slab_3_EC)) + "\n" +
////                    "<0x09>EC4           :  " + (String.format("%.2f", Structbilling.Slab_4_EC)) + "\n" +
////                    "<0x09>Energy Chg    :  " + (String.format("%.2f", Structbilling.Slab_1_EC)) + "\n" +
////                    "<0x09>ED            :  " + (String.format("%.2f", Structbilling.Electricity_Duty_Charges)) + "\n" +
////                    "<0x09>METER RENT    :  " + (String.format("%.2f", Structconsmas.Meter_Rent)) + "\n" +
////                    "<0x09>FPPCA Chg     :  0.00" + "\n" +
////                    "<0x09>Shunt Chg     :  0.00" + "\n" +
////                    "<0x09>Other Chg     :  0.00" + "\n" +
////                    "<0x09>              --------" + "\n" +
////                    "<0x09>Sub Total     :  " + (String.format("%.2f", Structbilling.Cur_Bill_Total)) + "\n" +
////                    "<0x09>InstlonSD     :  " + (String.format("%.2f", Structconsmas.SD_Interest_chngto_SD_AVAIL)) + "\n" +
////                    "<0x09>              --------" + "\n" +
////                    "<0x09>Gross Total   :  " + (String.format("%.2f", Structbilling.Cur_Bill_Total)) + "\n" +
////                    "<0x09>Rebate        :  " + (String.format("%.2f", Structbilling.Rbt_Amnt)) + "\n" +
////                    "<0x10>AMOUNT PAYABLE" + "\n" +
////                    "<0x10>--------------" + "\n" +
////                    "<0x09>Upt(" + Structbilling.Due_Date + "):" + (String.format("%.2f", Structbilling.Amnt_bPaid_on_Rbt_Date)) + "\n" +
////                    "<0x09>Aft(" + Structbilling.Due_Date.trim() + "):" + (String.format("%.2f", Structbilling.Amnt_Paidafter_Rbt_Date)) + "\n" +
////                    "<0x09>---------------------" + "\n" +
////                    "<0x09>L Paid Amt  :" + Structconsmas.Last_Total_Pay_Paid + "\n" +
////                    "<0x09>Rec No.     :" + Structconsmas.Last_Pay_Receipt_No + "\n" +
////                    "<0x09>L Paid Date :" + Structconsmas.Last_Pay_Date + "\n" +
////                    "<0x09>\nHELPLINE NO:18003456198" + "\n" +
////                    "<0x09>Bill Generated By :" + "\n" +
////                    "<0x09>Feedback Infra Pvt. Ltd" + "\n");
//            mSendMessage("<0x09>BILL MONTH :" + (String.format("%1$6s", Structconsmas.Bill_Mon)) + "\n" +
//                    "<0x09>BILL DATE: " + (String.format("%1$6s", Structbilling.Bill_Date)) + "\n" +
//                    "<0x09>BILL NO  : " + (String.format("%1$6s",Structbilling.Bill_No)) + "\n" +
//                    "<0x09>BILL PERIOD:" + (String.format("%1$6s",Structbilling.Bill_Period)) + "\n" +
//                    "<0x09>DIVISION:" + (String.format("%1$6s",Structconsmas.Division_Name)) + "\n" +
//                    "<0x10>DC NAME:" + (String.format("%1$6s",Structconsmas.Section_Name)) + "\n" +
//                    "<0x10>MACHINE NO:" + (String.format("%1$6s",Structbilling.SBM_No)) + "\n" +
//                    "<0x09>------------------------" + "\n" +
//                    "<0x09>METER READER NAME" + (String.format("%1$6s",Structbilling.Meter_Reader_Name)) +"\n" +
//                    "<0x09>" + "\n" +
//                    "<0x09>METER READER ID: " + (String.format("%1$6s",Structbilling.Meter_Reader_ID)) + "\n" +
//                    "<0x09>POLE ID : " + (String.format("%1$6s",Structconsmas.POLE_ID)) + "\n" +
//                    "<0x09>ACC ID   : " + (String.format("%1$6s",Structconsmas.Consumer_Number)) + "\n" +
//                    "<0x09>IVRS/SC NO: " + (String.format("%1$6s",Structconsmas.Consumer_Number + "" + Structconsmas.LOC_CD)) + "\n" +
////                    "<0x09>SUB-DIV    : " + Structconsmas.Sub_division_Name + "\n" +
////                    "<0x09>SECTION    : " + Structconsmas.Section_Name + "\n" +
//                    "<0x09>NAME : " + (String.format("%1$6s",Structconsmas.Name)) + "\n" +
//                    "<0x10>ADDRESS   : " + "\n" +
//                    "<0x09>" + (String.format("%1$6s",Structconsmas.address1 + Structconsmas.address2)) + "\n" +
//                    "<0x09>TARIFF CATG:  " + (String.format("%1$6s",Structconsmas.Tariff_Code)) + "\n" +
//
//                    "<0x09>C.LOAD     : " + (String.format("%1$6s",Structconsmas.Load)) + "\n" +
//                    "<0x09>GROUP NO     : " + (String.format("%1$6s",Structconsmas.Cycle)) + "\n" +
//                    "<0x09>DAIRY NO     : " + (String.format("%1$6s",Structconsmas.Route_Number)) + "\n" +
//                    "<0x09>MTR SL NO    : " + (String.format("%1$6s",Structconsmas.Meter_S_No)) + "\n" +
//                    "<0x09>MTR OWNER    : " + (String.format("%1$6s",Structconsmas.Meter_Ownership)) + "\n" +
//                    "<0x09>MAKE         : "  + "\n" +
//                    "<0x09>MF           : " + (String.format("%1$6s",Structconsmas.Multiply_Factor)) + "<0x09>      PHASE: " + (String.format("%1$6s",Structconsmas.PHASE_CD)) + "\n" +
//                    "<0x09>---------------------" + "\n" +
//                    "<0x09>RDG DATE     : " + (String.format("%1$6s",Structbilling.Cur_Meter_Reading_Date)) + "\n" + "<0x09>      STS: " + (String.format("%1$6s",Structbilling.Bill_Basis)) + "\n" +
//                    "<0x09>" + "\n" +
////                    "<0x09>PRES.BL DATE :" + Structbilling.Bill_Date + "\n" +
////                    "<0x09>PREV.BL DATE :" + Structconsmas.Prev_Meter_Reading_Date + "\n" +
//                    "<0x09>PRES.READING : " + (String.format("%1$6s",Structbilling.Cur_Meter_Reading)) + "\n" +
//                    "<0x09>PREV.READING : " + (String.format("%1$6s",Structconsmas.Prev_Meter_Reading)) + "\n" +
//                    "<0x09>MD           : " + (String.format("%1$6s",Structbilling.md_input)) + "\n" + "<0x09>      PF: " + (String.format("%1$6s",Structbilling.Avrg_PF)) + "\n" +
//                    "<0x09>AVG UNITS    : " + (String.format("%1$6s",Structbilling.Units_Consumed)) + "\n" +
//                    "<0x09>UNIT BILLED  : " + (String.format("%1$6s",Structbilling.Billed_Units)) + "\n" +
//                    "<0x09>BILL TYPE    : " + "\n" +                                // NO BILL TYPE
//                    "<0x09>---------------------" + "\n" +
//                    "<0x09>FIXED CHARGE : " + (String.format("%1$6s",(String.format("%.2f",Float.valueOf(Structbilling.FIXED_CHARGE))))) + "\n" +
//                    "<0x09>MIN CHARGE   : " + (String.format("%1$6s",(String.format("%.2f",Float.valueOf(Structbilling.MIN_CHRG))))) + "\n" +
////                    "<0x09>Current Bill   " + "\n" +
////                    "<0x09>---------------" + "\n" +
////                    "<0x09>Current D.P.S :  " + (String.format("%.2f", Structconsmas.Delay_Payment_Surcharge)) + "\n" +
////                    "<0x09>Fix/Mis Chg   :  " + (String.format("%.2f", Structbilling.Monthly_Min_Charg_DC)) + "\n" +
//                    "<0x09>EC1          : " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Slab_1_EC)))) + "\n" +
//                    "<0x09>EC2          : " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Slab_2_EC)))) + "\n" +
//                    "<0x09>EC3          : " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Slab_3_EC)))) + "\n" +
//                    "<0x09>EC4          : " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Slab_4_EC)))) + "\n" +
//                    "<0x09>Energy Chg   : " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Total_Energy_Charg)))) + "\n" +
//                    "<0x09>FCA CHARGE   : " + (String.format("%1$6s",(String.format("%.2f", Float.valueOf(Structbilling.O_FCA))))) + "\n" +
////                        "<0x09>FIX  CHARGE   :  " + (String.format("%1$6s",Structbilling.O_Total_Fixed_Charges)) + "\n" +
//                    "<0x09>DUTY CHARGE  : " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Electricity_Duty_Charges)))) + "\n" +
//                    "<0x09>METER RENT   : " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Meter_Rent)))) + "\n" +
//                    "<0x09>SURCHARGES   : " + (String.format("%1$6s",(String.format("%.2f", Float.valueOf(Structbilling.O_Surcharge))))) + "\n" +
//                    "<0x09>MSC CHARG    : " + (String.format("%1$6s",(String.format("%.2f", Structconsmas.misc_charges)))) + "\n" +
//                    "<0x09>INCENTIVES/PENALITIES : " + (String.format("%1$6s",(String.format("%.2f", Float.valueOf(Structbilling.O_Total_Incentives))))) + "\n" +                  // doubt
//                    "<0x09>" + "\n" +
//                    "<0x10>CUR BILL AMT : " + (String.format("%1$6s",(String.format("%.2f", Structbilling.Cur_Bill_Total)))) + "\n" +
//                    "<0x09>SD BILLED    : " + (String.format("%1$6s",(String.format("%.2f", Float.valueOf(Structconsmas.SD_BILLED)))))+ "\n" +
//                    "<0x09>SD ARREARS   : " + (String.format("%1$6s",(String.format("%.2f", Float.valueOf(Structconsmas.SD_ARREAR))))) + "\n" +
//                    "<0x09>PRE ARREARS  : " + (String.format("%1$6s",(String.format("%.2f", Structconsmas.Cur_Fiancial_Yr_Arr)))) + "\n" +
//                    "<0x09>ADJ AMT      : " + (String.format("%1$6s",(String.format("%.2f", Structconsmas.Sundry_Allow_EC)))) + "\n" +
//                    "<0x09>SD INT       : " + (String.format("%1$6s",(String.format("%.2f", Float.valueOf(Structconsmas.SD_INSTT_AMT))))) + "\n" +
//                    "<0x09>DIFF AMT     : " + (String.format("%1$6s","0.00")) + "\n" +                                                                // No diff Amount
//                    "<0x09>ROUND AMT    : " + (String.format("%1$6s",Structbilling.Round_Amnt)) + "\n" +
////                    "<0x09>Upt(" + Structbilling.Due_Date + "):" + (String.format("%.2f", Structbilling.Amnt_bPaid_on_Rbt_Date)) + "\n" +
////                    "<0x09>Aft(" + Structbilling.Due_Date.trim() + "):" + (String.format("%.2f", Structbilling.Amnt_Paidafter_Rbt_Date)) + "\n" +
//                    "<0x09>TOT AMT      : " + (String.format("%1$6s",(String.format("%.2f", Float.valueOf(Structbilling.O_Total_Bill))))) + "\n" +
//                    "<0x09>DPS AMT      : " + (String.format("%1$6s",Structbilling.Delay_Pay_Surcharge)) + "\n" +
//                    "<0x09>....................." + "\n" +
//                    "<0x09>PAY BL BY DUE DT   :" + (String.format("%1$6s",(String.format("%.2f", (Double.valueOf(Structbilling.O_Total_Bill) - Double.valueOf(Structbilling.O_Surcharge)))))) + "\n" +  // doubt
//                    "<0x09>PAY BL AFTER DUE DT:" + (String.format("%1$6s",(String.format("%.2f",Float.valueOf(Structbilling.O_Total_Bill))))) + "\n" +
//                    "<0x09>CHQ/ONLINE DUE DT  :" + (String.format("%1$6s",Structconsmas.FIRST_CHQ_DUE_DATE)) + "\n" +
//                    "<0x09>CASH DUE DT        :" + (String.format("%1$6s",Structconsmas.FIRST_CASH_DUE_DATE)) +"\n" +
//                    "<0x09>" + "\n" +
//                    "<0x09>" + "\n" +
//                    "<0x09>" + "\n" +
//                    "<0x09>" + "\n" +
//                    "<0x09>" + "\n" +
//                    "<0x09>" + "\n" +
//                    "<0x09>" + "\n" +
//                    "<0x09>" + "\n" +
//                    "<0x09>" + "\n" +
//                    "<0x10>          RECEIPT        " + "\n" +
//                    "<0x09>BILL MON :" +  (String.format("%1$6s",Structconsmas.Bill_Mon)) + "\n" +
//                    "<0x09>DIV      :" +  (String.format("%1$6s",Structconsmas.Division_Name)) +"\n" +
//                    "<0x09>ACC ID   :" +  (String.format("%1$6s",Structconsmas.Consumer_Number)) + "\n" +
//                    "<0x09>DIVISION :" +  (String.format("%1$6s",Structconsmas.Division_Name)) + "\n" +
//                    "<0x09>BILL DATE :" + (String.format("%1$6s",Structbilling.Bill_Date)) + "\n" +
//                    "<0x09>IVRS/SC NO:" + (String.format("%1$6s",Structconsmas.Consumer_Number + "" + Structconsmas.LOC_CD)) + "\n" +
//                    "<0x10>TOTAL     :" + (String.format("%1$6s",(String.format("%.2f", Structbilling.Cur_Bill_Total)))) + "\n");     // doubt
////            dbHelper.insertSequence("DCNumber", dcSeq);
////
////            }
//// else {
//
//            Float obj = new Float(GSBilling.getInstance().getSalb1unit());
//            Float obj1 = new Float(GSBilling.getInstance().getSalb2unit());
//            Float obj2 = new Float(GSBilling.getInstance().getSalb3unit());
//            Float obj3 = new Float(GSBilling.getInstance().getSalb4unit());
//
//            int slab1unit = obj.intValue();
//            int slab2unit = obj1.intValue();
//            int slab3unit = obj2.intValue();
//            int slab4unit = obj3.intValue();
//
//            float Arr = Structconsmas.Pre_Financial_Yr_Arr + Structconsmas.Cur_Fiancial_Yr_Arr;
//            Structbilling.DCNumber = "";
//
////            Structbilling.DSIG_STATE = "50";
//
//            billseq = appcomUtil.findSequence(getApplicationContext(), "BillNumber");
//
//            UtilAppCommon uc = new UtilAppCommon();
//            uc.print_Bill();
//            uc.princonmas();
//
//            double totAMount = (Math.ceil(Structbilling.Amnt_bPaid_on_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL));
//
////            if (Arr > 5000 && totAMount > 5000) {
////
////                dcSeq=appcomUtil.findSequence(getApplicationContext(), "DCNumber");
////                strdcSeq =String.format("%05d", dcSeq);
////                Structbilling.DCNumber = key + "/DN" + dcSeq;
////
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
////                        "<0x09>" + "        " + Structconsmas.Category + "       " + Structconsmas.Load + "\n" +
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
////                        "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structbilling.Monthly_Min_Charg_DC))) + "\n" +
////                        "<0x09>" + "      " + String.format("%5.5s", slab1unit) + " =" + (String.format("%1$10s", String.format("%.2f", Structbilling.Slab_1_EC))) + "\n" +
////                        "<0x09>" + "      " + String.format("%5.5s", slab2unit) + " =" + (String.format("%1$10s", String.format("%.2f", Structbilling.Slab_2_EC))) + "\n" +
////                        "<0x09>" + "      " + String.format("%5.5s", slab3unit) + " =" + (String.format("%1$10s", String.format("%.2f", Structbilling.Slab_3_EC))) + "\n" +
////                        "<0x09>" + "      " + String.format("%5.5s", slab4unit) + " =" + (String.format("%1$10s", String.format("%.2f", Structbilling.Slab_4_EC))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structbilling.Total_Energy_Charg))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structbilling.Electricity_Duty_Charges))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structconsmas.Meter_Rent))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", "0.00")) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structconsmas.Delay_Payment_Surcharge))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", "0.00")) + "\n" +
////
////
//////                            "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structconsmas.SD_Interest_chngto_SD_AVAIL))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structbilling.Cur_Bill_Total))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structconsmas.Pre_Financial_Yr_Arr))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structconsmas.Cur_Fiancial_Yr_Arr))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", "0.00")) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structbilling.House_Lck_Adju_Amnt))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structconsmas.SD_Interest_chngto_SD_AVAIL))) + "\n" +
//////                                "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structbilling.Cur_Bill_Total))) + "\n" +
//////                                "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structbilling.Cur_Bill_Total))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", "0.00")) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structbilling.Amnt_Paidafter_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structbilling.Rbt_Amnt))) + "\n" +
////                        "<0x10>" + "             " + (String.format("%1$10s", String.format("%.2f", Math.ceil(Structbilling.Amnt_bPaid_on_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL)))) + "\n" +
////                        "<0x09>" + "             " + Structbilling.Due_Date +
////                        "<0x10>" + "             " + (String.format("%1$10s", String.format("%.2f", Math.ceil(Structbilling.Amnt_Paidafter_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL)))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", "0.00")) + "\n" +
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
////                        "<0x09>" + "     " + Structbilling.DCNumber + " " + Structbilling.Bill_Date +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + "  " + Structbilling.Cons_Number +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + "          " + (Structconsmas.Pre_Financial_Yr_Arr + Structconsmas.Cur_Fiancial_Yr_Arr) +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + "     " + Structbilling.Bill_Month + "\n" +
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
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n" +
////                        "<0x09>" + " " + "\n");
////
//////                    }
////                dbHelper.insertSequence("DCNumber", dcSeq);
////            }
////            else
////            {
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
////                        "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structbilling.Monthly_Min_Charg_DC))) + "\n" +
////                        "<0x09>" + "      " + String.format("%5.5s", slab1unit) + " =" + (String.format("%1$10s", String.format("%.2f", Structbilling.Slab_1_EC))) + "\n" +
////                        "<0x09>" + "      " + String.format("%5.5s", slab2unit) + " =" + (String.format("%1$10s", String.format("%.2f", Structbilling.Slab_2_EC))) + "\n" +
////                        "<0x09>" + "      " + String.format("%5.5s", slab3unit) + " =" + (String.format("%1$10s", String.format("%.2f", Structbilling.Slab_3_EC))) + "\n" +
////                        "<0x09>" + "      " + String.format("%5.5s", slab4unit) + " =" + (String.format("%1$10s", String.format("%.2f", Structbilling.Slab_4_EC))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structbilling.Total_Energy_Charg))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structbilling.Electricity_Duty_Charges))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structconsmas.Meter_Rent))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", "0.00")) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structconsmas.Delay_Payment_Surcharge))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", "0.00")) + "\n" +
//////                            "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structconsmas.SD_Interest_chngto_SD_AVAIL))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structbilling.Cur_Bill_Total))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structconsmas.Pre_Financial_Yr_Arr))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structconsmas.Cur_Fiancial_Yr_Arr))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", "0.00")) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structbilling.House_Lck_Adju_Amnt))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structconsmas.SD_Interest_chngto_SD_AVAIL))) + "\n" +
//////                                "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structbilling.Cur_Bill_Total))) + "\n" +
//////                                "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structbilling.Cur_Bill_Total))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", "0.00")) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structbilling.Amnt_Paidafter_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", String.format("%.2f", Structbilling.Rbt_Amnt))) + "\n" +
////                        "<0x10>" + "             " + (String.format("%1$10s", String.format("%.2f", Math.ceil(Structbilling.Amnt_bPaid_on_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL)))) + "\n" +
////                        "<0x09>" + "             " + Structbilling.Due_Date +
////                        "<0x10>" + "             " + (String.format("%1$10s", String.format("%.2f", Math.ceil(Structbilling.Amnt_Paidafter_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL)))) + "\n" +
////                        "<0x09>" + "             " + (String.format("%1$10s", "0.00")) + "\n" +
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
////            }
//
//            TestMainActivity.this.runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
////                    progress.dismiss();
//                    ArrayList<String> mylistbck = new ArrayList<String>();
//                    ArrayList<String> listBckBill = new ArrayList<String>();
//
//                    dbHelper4 = new DB(getApplicationContext());
//                    SD4 = dbHelper4.getWritableDatabase();
//
////                    dbHelper4.insertIntoBillingTable();
//                    dbHelper4.insertIntoMPBillingTable();
//                    dbHelper4.insertSequence("BillNumber", billseq);
//
//                    dbHelper4 = new DB(getApplicationContext());
//                    SD4 = dbHelper4.getWritableDatabase();
//
//                    String selquer = "SELECT * FROM TBL_BILLING WHERE Upload_Flag='N' ";//WHERE Upload_Flag='N'
//                    Cursor curBillselect = SD4.rawQuery(selquer, null);
//                    String arrStr[] = null;
//
//
//
//                    if (curBillselect != null && curBillselect.moveToFirst()) {
//                        Log.e(getApplicationContext(), "SLPrintAct", "UpdateFLag to N : " + curBillselect.getString(0));
//
//                        while (curBillselect.isAfterLast() == false) {
//                            String name = curBillselect.getString(0);
//
//                            listBckBill.add(curBillselect.getString(0) + "}" + curBillselect.getString(1) + "}" + curBillselect.getString(2) +
//                                    "}" + curBillselect.getString(3) + "}" + curBillselect.getString(4) + "}" + curBillselect.getString(5) +
//                                    "}" + curBillselect.getString(6) + "}" + curBillselect.getString(7) + "}" + curBillselect.getString(8) +
//                                    "}" + curBillselect.getString(9) + "}" + curBillselect.getString(10) + "}" + curBillselect.getString(11) +
//                                    "}" + curBillselect.getString(12) + "}" + curBillselect.getString(13) + "}" + curBillselect.getString(14) +
//                                    "}" + curBillselect.getString(15) + "}" + curBillselect.getString(16) + "}" + curBillselect.getString(17) +
//                                    "}" + curBillselect.getString(18) + "}" + curBillselect.getString(19) + "}" + curBillselect.getString(20) +
//                                    "}" + curBillselect.getString(21) + "}" + curBillselect.getString(22) + "}" + curBillselect.getString(23) +
//                                    "}" + curBillselect.getString(24) + "}" + curBillselect.getString(25) + "}" + curBillselect.getString(26) +
//                                    "}" + curBillselect.getString(27) + "}" + curBillselect.getString(28) + "}" + curBillselect.getString(29) +
//                                    "}" + curBillselect.getString(30) + "}" + curBillselect.getString(31) + "}" + curBillselect.getString(32) +
//                                    "}" + curBillselect.getString(33) + "}" + curBillselect.getString(34) + "}" + curBillselect.getString(35) +
//                                    "}" + curBillselect.getString(36) + "}" + curBillselect.getString(37) + "}" + curBillselect.getString(38) +
//                                    "}" + curBillselect.getString(39) + "}" + curBillselect.getString(40) + "}" + curBillselect.getString(41) +
//                                    "}" + curBillselect.getString(42) + "}" + curBillselect.getString(43) + "}" + curBillselect.getString(44) +
//                                    "}" + curBillselect.getString(46) + "}" + curBillselect.getString(47) + "}" + curBillselect.getString(48) +
//                                    "}" + curBillselect.getString(49) + "}" + curBillselect.getString(51) + "}" + curBillselect.getString(52) +
//                                    "}" + curBillselect.getString(53) + "}" + curBillselect.getString(54) + "}" + curBillselect.getString(55) +
//                                    "}" + curBillselect.getString(56) + "}" + curBillselect.getString(57) + "}" + curBillselect.getString(58));
//
//
//                            curBillselect.moveToNext();
//                        }
//
////                        generatebackupNoteOnSD(getApplicationContext(), "mbc_Ob.txt", listBckBill);
//                    }
//
//                    mylistbck.add(billseq+ "|" +dcSeq );
////                    generateLastREC(getApplicationContext(), "REC_Bill.txt", mylistbck);
//
//                }
//            });
//            return 0;
//        }
//
//        public String padLeft(String s, int n) {
//            if (s.length() < n) {
//                return String.format("%1$" + n + "s", s);
//            } else return s;
//        }
//
//        /* This displays the status messages of EnterTextAsyc in the dialog box */
//        @Override
//        protected void onPostExecute(Integer result) {
//
////            showdialog();
//            super.onPostExecute(result);
//        }
//    }
//    public void mSendMessage(String message) {
//        // TODO Auto-generated method stub
//        Log.e(getApplicationContext(), "SLPrintAct", "MESSAGE " + message);
//        Intent myIntent = new Intent();
//        myIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
//        myIntent.putExtra("message", message);
//        myIntent.setAction("com.softland.iprintMarvel.Bluetooth.message");
//        myIntent.setType("*/*");
//        sendBroadcast(myIntent);
//
////        dbHelper.insertIntoBillingTable();
////        dbHelper.insertSequence("BillNumber", billseq);
//
//
//
//    }

}