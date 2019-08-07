package com.fedco.mbc.bluetoothprinting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.Collection;
import com.fedco.mbc.model.Structcollection;
import com.fedco.mbc.model.Structcolmas;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.Words;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DuplicateCollPrint extends AppCompatActivity {

    public static ProgressDialog prgDialog;
    String convertedNumber, typePay,strBillMonth;
    SQLiteDatabase SD, SD2, SD3;
    DB dbHelper, dbHelper2, dbHelper3;
    String strDate,strtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat fds = new SimpleDateFormat("kk:mm");
        strDate = sdf.format(cal.getTime());
        strtime = fds.format(cal.getTime());

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        String billMonth = formatter.format(date);
        String str[] = billMonth.split(" ");
        String month = str[1].substring(0, 3);
        String year = str[2].substring(2);
        strBillMonth = month + "-" + year;

        dbHelper    = new DB(getApplicationContext());
        SD          = dbHelper.getWritableDatabase();
        try {
            convertedNumber = Words.convertNumberToWords(Integer.valueOf(Structcolmas.AMOUNT));
        } catch (Exception e) {

        }
        if (Structcolmas.PYMNT_MODE.equals("C")) {
            typePay = "CASH";
        } else {
            typePay = "CHEQUE";
        }

        String divCode = "SELECT DIV_NAME,DISPLAY_CODE FROM TBL_BILLING_DIV_MASTER";

        Cursor curDivCode = SD.rawQuery(divCode,null);

        if(curDivCode!=null && curDivCode.moveToFirst()){

            Structconsmas.DIV_NAME=curDivCode.getString(0);
            Structconsmas.PICK_REGION=curDivCode.getString(1);

        }

        EnterTextAsyc asynctask = new EnterTextAsyc();
        asynctask.execute(0);
    }

    /*   This method shows the EnterTextAsyc  AsynTask operation */
    public class EnterTextAsyc extends AsyncTask<Integer, Integer, Integer> {
        /* displays the progress dialog until background task is completed*/
        @Override
        protected void onPreExecute() {
            // ProgressDialog(context, "Please Wait....");
//            prgDialog = new ProgressDialog(DuplicateCollPrint.this);
//            prgDialog.setMessage("Please Wait ....");
//            prgDialog.setIndeterminate(true);
//            prgDialog.setCancelable(false);
//            prgDialog.show();
//            Structconsmas.PICK_REGION= "12";

        }

        /* Task of EnterTextAsyc performing in the background*/
        @Override
        protected Integer doInBackground(Integer... params) {

            switch (Structconsmas.PICK_REGION) {

                case "10"://bhopal
                    if (typePay.equalsIgnoreCase("CASH")) {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "------ cut here ------" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + Structcollection.BILL_MON + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    } else if (typePay.equalsIgnoreCase("CHEQUE")) {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "CHEQUE DATE :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "CHEQUE NO.  :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "------ cut here ------" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + Structcollection.BILL_MON + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "CHEQUE DATE :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "CHEQUE NO.  :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    } else if (typePay.equalsIgnoreCase("DD")) {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "DD DATE     :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "DD NO.      :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "------ cut here ------" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + Structcollection.BILL_MON + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "DD DATE     :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "DD NO.      :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    } else {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "------ cut here ------" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    }
                    break;
                case "11"://jabalpur
                    if (typePay.equalsIgnoreCase("CASH")) {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       JABALPUR       " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "------ cut here ------" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       JABALPUR       " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    } else if (typePay.equalsIgnoreCase("CHEQUE")) {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       JABALPUR       " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "CHEQUE DATE :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "CHEQUE NO.  :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "------ cut here ------" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       JABALPUR       " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "CHEQUE DATE :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "CHEQUE NO.  :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    } else if (typePay.equalsIgnoreCase("DD")) {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       JABALPUR       " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "DD DATE     :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "DD NO.      :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "------ cut here ------" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       JABALPUR       " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "DD DATE     :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "DD NO.      :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    } else {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       JABALPUR       " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "------ cut here ------" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       JABALPUR       " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    }

                    break;
                case "12"://indore
                    if (typePay.equalsIgnoreCase("CASH")) {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       INDORE         " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +

                                        "<0x09>" + "------ cut here ------" + "\n" +

                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       INDORE         " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    }
                    if (typePay.equalsIgnoreCase("CHEQUE")) {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       INDORE         " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "CHEQUE DATE :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "CHEQUE NO.  :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +

                                        "<0x09>" + "------ cut here ------" + "\n" +

                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       INDORE         " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "CHEQUE DATE :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "CHEQUE NO.  :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    } else if (typePay.equalsIgnoreCase("DD")) {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       JABALPUR       " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "DD DATE     :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "DD NO.      :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +

                                        "<0x09>" + "------ cut here ------" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +

                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.M.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       JABALPUR       " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + Structcollection.BILL_MON + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "DD DATE     :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "DD NO.      :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    } else {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       INDORE         " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +

                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "------ cut here ------" + "\n" +

                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "       INDORE         " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    }
                    break;
                default:
                    if (typePay.equalsIgnoreCase("CASH")) {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "------ cut here ------" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +

                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +

                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "CHECK DATE  :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "CHECK NO.   :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    } else if (typePay.equalsIgnoreCase("CHEQUE")) {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "CHEQUE DATE :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "CHEQUE NO.  :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "------ cut here ------" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +

                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "CHEQUE DATE :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "CHEQUE NO.  :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    } else if (typePay.equalsIgnoreCase("DD")) {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + strBillMonth + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "DD DATE     :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "DD NO.      :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +

                                        "<0x09>" + "------ cut here ------" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +

                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                       "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + Structcollection.BILL_MON + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "DD DATE     :" + Structcolmas.CH_DATE + "\n" +
                                        "<0x09>" + "DD NO.      :" + Structcolmas.CHEQUE_NO + "\n" +
                                        "<0x09>" + "BANK NAME   :" + Structcolmas.BANK_NAME + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    } else {
                        sendMessage(
                                "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "RECEIPT(CONSUMER COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + Structcollection.BILL_MON + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + "  Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +

                                        "<0x09>" + "------ cut here ------" + "\n" +

                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "    MADHYA PRADESH    " + "\n" +
                                        "<0x09>" + " " + "    M.P.P.K.V.V.C.L   " + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + "" + "RECEIPT(DUPLICATE COPY)" + "\n" +
                                        "<0x09>" + " " + "**********************" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "MONTH  :" + Structcollection.BILL_MON + "\n" +
                                        "<0x09>" + "MR NO. :" + " " + "\n" +
                                        "<0x09>" + " " + Structcolmas.RECEIPT_NO + "\n" +
                                        "<0x09>" + "DATE        :" + strDate + "\n" +
                                        "<0x09>" + "TIME        :" + strtime + "\n" +
                                        "<0x09>" + "DIVISION    :" + "\n" +
                                        "<0x09>" + " " + Structcollection.DIV + "\n" +
                                        "<0x09>" + "DC          :" + "\n" +
                                        "<0x09>" + " " + Structcollection.SECTION + "\n" +
                                        "<0x09>" + "CONS NO.    :" + Structcollection.CON_NO + "\n" +
                                        "<0x09>" + "IVRS NO.    :" + Structcollection.IVRS_NO + "\n" +
                                        "<0x09>" + "CONSMER NAME:" + Structcollection.CON_NAME + "\n" +
                                        "<0x09>" + "AMOUNT      :" + Structcolmas.AMOUNT + "\n" +
                                        "<0x09>" + " (" + convertedNumber + " Rupees Only )" + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + "PAYMENT TYPE:" + typePay + "\n" +
                                        "<0x09>" + "READER ID   :" + Structcolmas.MR_ID + "\n" +
                                        "<0x09>" + "READER NAME :" + Structcolmas.MR_NAME + "\n" +
                                        "<0x09>" + "DEVICE ID   :" + Structcolmas.DEV_ID + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n" +
                                        "<0x09>" + " " + "     " + "\n");
                    }
                    break;
            }
            return 0;
        }

        public String padLeft(String s, int n) {
            if (s.length() < n) {
                return String.format("%1$" + n + "s", s);
            } else return s;
        }

        /* This displays the status messages of EnterTextAsyc in the dialog box */
        @Override
        protected void onPostExecute(Integer result) {
            //dlgPg.dismiss();
//            prgDialog.dismiss();
//            prgDialog.hide();
            showdialog();

            super.onPostExecute(result);
        }
    }

    /*  To show response messages  */
    public void showdialog() {

        new SweetAlertDialog(DuplicateCollPrint.this, SweetAlertDialog.SUCCESS_TYPE)
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

    private void sendMessage(String message) {
        // TODO Auto-generated method stub

        Log.e("", "MESSAGE " + message);

        Intent myIntent = new Intent();
        myIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        myIntent.putExtra("message", message);
        myIntent.setAction("com.softland.iprintMarvel.Bluetooth.message");
        myIntent.setType("*/*");
        sendBroadcast(myIntent);
    }

    public void onBackPressed() {

        Intent intent = new Intent(DuplicateCollPrint.this, Collection.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);

    }


}
