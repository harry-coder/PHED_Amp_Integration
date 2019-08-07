package com.fedco.mbc.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fedco.mbc.R;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.UtilAppCommon;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MeterReading extends AppCompatActivity {

    Logger Log;
    Button btnMetering, btnUBT, btnReport, btnSummery, btnLRP, btnHelp, btnExit, btnBack;
    String pass;
    Context context;
    DB dbHelper4;
    SQLiteDatabase SD3;
    int pendingcount=300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);
        try {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
            getSupportActionBar().setLogo(R.mipmap.logo);
            setTitle("PHEDApp");
        } catch (NullPointerException npe) {
            Log.e(getApplicationContext(), "Billing Act", "ActionBar Throwing null Pointer", npe);
        }
        btnMetering = (Button) findViewById(R.id.buttonBilling1);
        btnMetering.setText("    METER READING");
        btnMetering.setTextSize(15.5f);

        btnUBT = (Button) findViewById(R.id.buttonBilling2);
        btnReport = (Button) findViewById(R.id.buttonBilling3);
        btnSummery = (Button) findViewById(R.id.buttonBilling4);

        btnMetering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                UtilAppCommon ucom = new UtilAppCommon();
//                ucom.nullyfimodelCon();
//                ucom.nullyfimodelBill();
//
//                try {
//
//                            dbHelper4 = new DB(getApplicationContext());
//                            SD3 = dbHelper4.getWritableDatabase();
//                            String pending = "SELECT Cons_Number from 'TBL_BILLING' WHERE Upload_Flag='N'";
//                            final Cursor curPend = SD3.rawQuery(pending, null);
//                            curPend.getCount();
//                            // if(curPend.getCount()>0){
//                            if (curPend.getCount() > pendingcount) {
//                                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//                                        .setTitleText("Records are Pending ?")
//                                        .setContentText("More Than " + curPend.getCount() + " records are pending for upload!")
//                                        .setCancelText("No,cancel plz!")
//                                        .setConfirmText("OK!")
//                                        .showCancelButton(true)
//                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                            @Override
//                                            public void onClick(SweetAlertDialog sDialog) {
//
//                                                Intent intent = new Intent(context, SDActivity.class);
//                                                startActivity(intent);
//                                                overridePendingTransition(R.anim.anim_slide_in_left,
//                                                        R.anim.anim_slide_out_left);
//                                            }
//                                        })
//                                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                            @Override
//                                            public void onClick(SweetAlertDialog sDialog) {
//                                                sDialog.cancel();
//                                            }
//                                        })
//                                        .show();
//
//                            } else {
//
//                                Intent intent = new Intent(context, BillingtypesActivity.class);
//                                System.out.println(GSBilling.getInstance().getPrintertype());
//                                startActivity(intent);
//                                overridePendingTransition(R.anim.anim_slide_in_left,
//                                        R.anim.anim_slide_out_left);
//                            }
//
//
//                } catch (NullPointerException n) {
//                    Toast.makeText(MeterReading.this, "Please configure the Printer ", Toast.LENGTH_SHORT).show();
//                }
                Intent intent = new Intent(getApplicationContext(), BillingtypesActivity.class);

                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });

        btnUBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /****DIALOG BOX INITIALIZATION PROVIDING A LAYOUT TO DIALOUGE****/
                final Dialog dialogAccount = new Dialog(context, R.style.DialogeAppTheme);

                dialogAccount.setContentView(R.layout.custom_dialoge_password);
                dialogAccount.setTitle("Account Search");

                final EditText editTextAccno = (EditText) dialogAccount.findViewById(R.id.EditTextACC);

                Button dHomeButton = (Button) dialogAccount.findViewById(R.id.HomeButton);
                dHomeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        finish();
                        overridePendingTransition(R.anim.anim_slide_in_right,
                                R.anim.anim_slide_out_right);

                    }
                });

                Button dialogButton = (Button) dialogAccount.findViewById(R.id.dialogButtonACCOK);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            pass = editTextAccno.getText().toString().trim();
                        } catch (NullPointerException N) {
                            N.printStackTrace();
                        }

                        if (pass.isEmpty()) {
                            Toast.makeText(context, "Please Enter Account Number.", Toast.LENGTH_LONG).show();
                        } else if (pass.equals("data@123")) {

                            Intent intent = new Intent(getApplicationContext(), SDActivity.class);
                            intent.putExtra("flowkey", "metering");
                            //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);

                        } else {
                            dialogAccount.dismiss();
                            Toast.makeText(context, " UnAuthorized Access ", Toast.LENGTH_LONG).show();
                        }

                    }
                });
                dialogAccount.show();

            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Work under progress.", Toast.LENGTH_LONG).show();
            }
        });

        btnSummery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Work under progress.", Toast.LENGTH_LONG).show();
            }
        });


    }
}
