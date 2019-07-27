package com.fedco.mbc.activitymetering;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.Billing;
import com.fedco.mbc.activity.BillingtypesActivity;
import com.fedco.mbc.activity.CameraActivity;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.UtilAppCommon;

import java.sql.SQLException;

/**
 * Created by abhisheka on 19-09-2016.
 */

public class Meteringtypes extends AppCompatActivity {

    Button btnExit, btnBack, btnAccountNo, btnOldAccountNo, btnMeterNo, btnName, btnAddress, btnMobileNo, btnRecheckRequest, btnRoute;
    String accountnum;
    final Context context = this;

    SQLiteDatabase SD;
    DB dbHelper;

    UtilAppCommon uac;
    UtilAppCommon UtilAppCommon;

    Logger Log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteringtypes);
        setTitle("Consumer Search");

        //----Buttons Initilization----//
        btnAccountNo        = (Button) findViewById(R.id.accountno);
        btnOldAccountNo     = (Button) findViewById(R.id.oldaccountno);
        btnMeterNo          = (Button) findViewById(R.id.meterno);
        btnName             = (Button) findViewById(R.id.name);
        btnAddress          = (Button) findViewById(R.id.address);
        btnMobileNo         = (Button) findViewById(R.id.mobileno);
        btnRecheckRequest   = (Button) findViewById(R.id.recheckrequest);
        btnRoute            = (Button) findViewById(R.id.routesequence);

        btnMobileNo.setEnabled(false);
        btnRecheckRequest.setEnabled(false);
        btnRoute.setEnabled(false);

        UtilAppCommon=new UtilAppCommon();

        UtilAppCommon.nullMeterUpload();
        UtilAppCommon.nullMeterMaster();

        btnAccountNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /****DIALOG BOX INITIALIZATION PROVIDING A LAYOUT TO DIALOUGE****/
                final Dialog dialogAccount   = new Dialog(context, R.style.DialogeAppTheme);
                dialogAccount.setContentView(R.layout.custom_dialoge_search);
                dialogAccount.setTitle("Account Search");
                // set the custom dialog components - text, image and button
                final EditText editTextAccno = (EditText) dialogAccount.findViewById(R.id.EditTextACC);
                editTextAccno.setHint("Enter Account Number");
                editTextAccno.setInputType(InputType.TYPE_CLASS_NUMBER);
                Button dialogButton = (Button) dialogAccount.findViewById(R.id.dialogButtonACCOK);
                Button dHomeButton  = (Button) dialogAccount.findViewById(R.id.HomeButton);

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
                        try {
                            accountnum = editTextAccno.getText().toString().trim();
                        } catch (NullPointerException N) {
                            N.printStackTrace();
                        }

                        if (accountnum.isEmpty()) {
                            Toast.makeText(context, "Please Enter Account Number.", Toast.LENGTH_SHORT).show();
                        } else {

                            Log.e(getApplicationContext(), "METERTYPES", "Consumer Num : " + accountnum);

                            dbHelper = new DB(getApplicationContext());
                            SD = dbHelper.getWritableDatabase();

                            String ret = "SELECT * FROM TBL_METERMASTER WHERE CONSUMERNO =" + accountnum;
                            Cursor curconsmasData = SD.rawQuery(ret, null);

                            if (curconsmasData != null && curconsmasData.moveToFirst()) {

                                uac = new UtilAppCommon();
                                try {
                                    uac.copyResultToMeterMaster(curconsmasData);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                Intent intent = new Intent(getApplicationContext(), CameraMeter.class);
                                // intent.putExtra("datalist", allist);
                                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left);

                            } else {
                                dialogAccount.dismiss();
                                Toast.makeText(context, "Account Number Not Found", Toast.LENGTH_SHORT).show();
                            }

                            // Intent intent=new Intent(getApplicationContext(),CameraActivity.class);
                            // startActivity(intent);
                        }

                    }
                });
                dialogAccount.show();
            }
        });

        btnOldAccountNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialogAccount = new Dialog(context, R.style.DialogeAppTheme);
                dialogAccount.getWindow().getAttributes().windowAnimations = R.anim.anim_slide_out_left;
                dialogAccount.setContentView(R.layout.custom_dialoge_search);
                dialogAccount.setTitle("OLD Account Search");

                // set the custom dialog components - text, image and button
                final EditText editTextAccno = (EditText) dialogAccount.findViewById(R.id.EditTextACC);
                editTextAccno.setHint("Enter Old Account Number");
                Button dialogButton = (Button) dialogAccount.findViewById(R.id.dialogButtonACCOK);
                Button dHomeButton  = (Button) dialogAccount.findViewById(R.id.HomeButton);

                dHomeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        finish();
                        overridePendingTransition(R.anim.anim_slide_in_right,
                                R.anim.anim_slide_out_right);

                    }
                });

                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            accountnum = editTextAccno.getText().toString().trim();
                        } catch (NullPointerException N) {
                            N.printStackTrace();
                        }

                        if (accountnum.isEmpty()) {
                            Toast.makeText(context, "Please Enter Old-Account Number.", Toast.LENGTH_SHORT).show();
                        } else {

                            Log.e(getApplicationContext(), "BillTypeAct", "Account no : " + accountnum);

                            dbHelper = new DB(getApplicationContext());
                            SD = dbHelper.getWritableDatabase();

                            String ret = "SELECT * FROM TBL_METERMASTER WHERE OLDCONSUMERNO ='" + accountnum + "'";
                            // String ret = "SELECT Consumer_Number FROM TBL_CONSMAST WHERE Name='SAPHALA PARIDA'";
                            Cursor curconsmasData = SD.rawQuery(ret, null);
                            if (curconsmasData != null && curconsmasData.moveToFirst()) {

                                uac = new UtilAppCommon();
                                try {
                                    uac.copyResultToMeterMaster(curconsmasData);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                /****INITIALISATION OF INTENT AND ADDING SENDING DATA TO NEXT SCREEN , STARING INTENT****/
                                Intent intent = new Intent(getApplicationContext(), CameraMeter.class);
                                //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                //  intent.putExtra("datalist", allist);
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left);

                            } else {
                                dialogAccount.dismiss();
                                Toast.makeText(context, "Old-Account Number Not Found", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }
                });
                dialogAccount.show();
            }
        });

        btnMeterNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /****DIALOG BOX INITIALIZATION PROVIDING A LAYOUT TO DIALOUGE****/
                final Dialog dialogAccount = new Dialog(context, R.style.DialogeAppTheme);
                dialogAccount.getWindow().getAttributes().windowAnimations = R.anim.anim_slide_out_left;
                dialogAccount.setContentView(R.layout.custom_dialoge_search);
                dialogAccount.setTitle("Meter Number Search");

                // set the custom dialog components - text, image and button
                final EditText editTextAccno = (EditText) dialogAccount.findViewById(R.id.EditTextACC);
                editTextAccno.setHint("Enter Meter Number");
                Button dialogButton = (Button) dialogAccount.findViewById(R.id.dialogButtonACCOK);
                Button dHomeButton = (Button) dialogAccount.findViewById(R.id.HomeButton);

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
                        try {
                            accountnum = editTextAccno.getText().toString().trim();
                        } catch (NullPointerException N) {
                            N.printStackTrace();
                        }

                        if (accountnum.isEmpty()) {
                            Toast.makeText(context, "Please Enter Meter Number.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(getApplicationContext(), "BillTypeAct", "Account no : " + accountnum);

                            dbHelper = new DB(getApplicationContext());
                            SD = dbHelper.getWritableDatabase();

                            /****RAW QUERY EXICUTION AND STORING IN CURSOR****/
                            String ret = "SELECT * FROM TBL_METERMASTER WHERE METERDEVICESERIALNO ='" + accountnum + "'";
                            // String ret = "SELECT Consumer_Number FROM TBL_CONSMAST WHERE Name='SAPHALA PARIDA'";
                            Cursor curconsmasData = SD.rawQuery(ret, null);

                            /****GETTING DATA FROM CURSOR WHEN CURSOR IS NOT NULL AND ON THE FORST INDEX****/
                            if (curconsmasData != null && curconsmasData.moveToFirst()) {

                                uac = new UtilAppCommon();

                                try {
                                    uac.copyResultToMeterMaster(curconsmasData);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                /****INITIALISATION OF INTENT AND ADDING SENDING DATA TO NEXT SCREEN , STARING INTENT****/
                                Intent intent = new Intent(getApplicationContext(), CameraMeter.class);
                                //  intent.putExtra("datalist", allist);
                                //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left);

                            } else {
                                dialogAccount.dismiss();
                                Toast.makeText(context, "Meter Number Not Found", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }
                });
                dialogAccount.show();
            }
        });

        btnName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MtrConsSrcByName.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });

        btnAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MtrConsSrcByAddress.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });

        btnRecheckRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NMActivity.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);

            }
        });

    }

    public void onBackPressed() {

        Intent intent = new Intent(Meteringtypes.this, Metering.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);

    }

}
