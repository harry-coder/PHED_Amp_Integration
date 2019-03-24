package com.fedco.mbc.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.bluetoothprinting.DuplicateBillPrint;
import com.fedco.mbc.bluetoothprinting.PrintingMainActivity;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.UtilAppCommon;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DuplicateActivity extends AppCompatActivity {

    Button btnSubmit;
    EditText etAccno;

    SQLiteDatabase SD;
    DB dbHelper;
    UtilAppCommon uac;

    final Context context = this;

    String accountno;
    String Datetime;
    Logger Log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duplicate);

        try {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
            getSupportActionBar().setLogo(R.mipmap.logo);
            setTitle("MBC");
        } catch (NullPointerException npe) {
            Log.e(getApplicationContext(), "Billing Act", "ActionBar Throwing null Pointer", npe);
        }
        btnSubmit = (Button) findViewById(R.id.ButtonACCOK);
        etAccno = (EditText) findViewById(R.id.EditTextACC);

        dbHelper = new DB(getApplicationContext());
        SD = dbHelper.getWritableDatabase();

        Calendar c = Calendar.getInstance();

        SimpleDateFormat month_date = new SimpleDateFormat("MMM-yy");
        final String month_name = month_date.format(c.getTime());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etAccno.getText().toString().trim().isEmpty()) {
                    etAccno.setError("can not be blank");
                }
                accountno = etAccno.getText().toString().trim();
                /****RAW QUERY EXECUTIONS AND STORING IN CURSOR****/
                String ret = "SELECT * FROM TBL_CONSMAST WHERE Consumer_Number =" + accountno;
                //String ret = "SELECT Consumer_Number FROM TBL_CONSMAST WHERE Name='SAPHALA PARIDA'";
                Cursor curconsmasData = SD.rawQuery(ret, null);

                /****GETTING DATA FROM CURSOR WHEN CURSOR IS NOT NULL AND ON THE FiRST INDEX****/
                if (curconsmasData != null && curconsmasData.moveToFirst()) {

                    uac = new UtilAppCommon();
                    try {
                        uac.copyResultsetToConmasClass(curconsmasData);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                String duplicateaccQuery = "select * from TBL_BILLING WHERE Bill_Month ='" + month_name + "' and Cons_Number='" + accountno + "'";

                final Cursor curdupacc = SD.rawQuery(duplicateaccQuery, null);
                System.out.println("DELETE SUICCESSqqqq" + duplicateaccQuery);
                System.out.println("DELETE SUICCESSssss" + curdupacc != null);
                System.out.println("DELETE SUICCESSmmmm" + curdupacc.moveToFirst());
                /****GETTING DATA FROM CURSOR WHEN CURSOR IS NOT NULL AND ON THE FORST INDEX****/
                if (curdupacc != null && curdupacc.moveToFirst()) {
                    System.out.println("am in if of " + curdupacc.getString(0));
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Print Duplicate BIll ?")
                            .setContentText("cant add Current Meter Reading!")
                            .setCancelText("No,cancel plz!")
                            .setConfirmText("Yes,print!")
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    try {
                                        uac.duplicateBill(curdupacc);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    System.out.println("xamera" + GSBilling.getInstance().getPrintertype());
                                    if (GSBilling.getInstance().getPrintertype().equals("1")) {

                                        Intent intent = new Intent(DuplicateActivity.this, PrintingMainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.anim_slide_in_left,
                                                R.anim.anim_slide_out_left);
                                    } else {
                                        Intent intent = new Intent(context, DuplicateBillPrint.class);
//                                                                         intent.putExtra("printtype",print_type);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.anim_slide_in_left,
                                                R.anim.anim_slide_out_left);
                                    }
                                    sDialog.cancel();
                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.cancel();
                                }
                            })
                            .show();
                } else {
                    Toast.makeText(context, "Account number not yet billed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void onBackPressed() {
        //        if (exit) {
        finish(); // finish activity
        overridePendingTransition(R.anim.anim_slide_in_right,
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

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Debug.stopMethodTracing();
        super.onDestroy();
    }
}
