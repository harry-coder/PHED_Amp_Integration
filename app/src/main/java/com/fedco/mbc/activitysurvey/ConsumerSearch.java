package com.fedco.mbc.activitysurvey;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.UtilAppCommon;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ConsumerSearch extends Activity {

    HashMap<String, String> hmconsmasData;
    static final ArrayList<HashMap<String, String>> allist = new ArrayList<HashMap<String, String>>();
    final Context context = this;
    UtilAppCommon uac;
    private Boolean exit = false;
    Button btnExit, button_Back, btn_Met_No, btn_Con_No, btn_Old_No;
    String accountnum;
    Logger Log;
    SQLiteDatabase SD;
    DB dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_report);


        /****BUTTON  INITIALISATION****/
        btn_Con_No = (Button) findViewById(R.id.ButtonConNo);
        btn_Met_No = (Button) findViewById(R.id.ButtonMetNo);
        btn_Old_No = (Button) findViewById(R.id.ButtonOldNo);
        button_Back = (Button) findViewById(R.id.Buttonback);
        button_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(getApplicationContext(), SurveyDetails.class));
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right);
            }
        });
        btn_Con_No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /****DIALOG BOX INITIALIZATION PROVIDING A LAYOUT TO DIALOUGE****/
                final Dialog dialogAccount   = new Dialog(ConsumerSearch.this, R.style.DialogeAppTheme);
                dialogAccount.setContentView(R.layout.custom_dialoge_search);
                dialogAccount.setTitle("Account Search");
                // set the custom dialog components - text, image and button
                final EditText editTextAccno = (EditText) dialogAccount.findViewById(R.id.EditTextACC);
                editTextAccno.setHint("Enter Account Number");
                editTextAccno.setInputType(InputType.TYPE_CLASS_TEXT);
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
//                            Toast.makeText(SurveyDetails.this, "Please Enter Account Number.", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent();
                            intent.putExtra("flow","SURVEY");
                            intent.setComponent(new ComponentName(getApplicationContext(), ConsumerSurvey.class));
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);
                        } else {

                            android.util.Log.e( "METERTYPES", "Consumer Num : " + accountnum);

                            dbHelper = new DB(getApplicationContext());
                            SD = dbHelper.getWritableDatabase();

                            String ret = "SELECT * FROM TBL_CONSUMERSURVEY_MASTER WHERE Consumer_Number ='" + accountnum+"'";
                            Cursor curconsmasData = SD.rawQuery(ret, null);

                            if (curconsmasData != null && curconsmasData.moveToFirst()) {

                                uac = new UtilAppCommon();
                                try {
                                    uac.copyResultToCONSurveyMaster(curconsmasData);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                Intent intent = new Intent();
                                intent.putExtra("flow","SURVEY");
                                intent.setComponent(new ComponentName(getApplicationContext(), ConsumerSurvey.class));
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left);

                            } else {
                                dialogAccount.dismiss();

                                Intent intent = new Intent();
                                intent.putExtra("flow","SURVEY");
                                intent.setComponent(new ComponentName(getApplicationContext(), ConsumerSurvey.class));
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left);

                                Toast.makeText(ConsumerSearch.this, "Account Number Not Found", Toast.LENGTH_SHORT).show();
                            }

                            // Intent intent=new Intent(getApplicationContext(),CameraActivity.class);
                            // startActivity(intent);
                        }

                    }
                });
                dialogAccount.show();


            }
        });
        btn_Met_No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /****DIALOG BOX INITIALIZATION PROVIDING A LAYOUT TO DIALOUGE****/
                final Dialog dialogAccount   = new Dialog(ConsumerSearch.this, R.style.DialogeAppTheme);
                dialogAccount.setContentView(R.layout.custom_dialoge_search);
                dialogAccount.setTitle("Meter Number Search");
                // set the custom dialog components - text, image and button
                final EditText editTextAccno = (EditText) dialogAccount.findViewById(R.id.EditTextACC);
                editTextAccno.setHint("Enter Meter Number");
                editTextAccno.setInputType(InputType.TYPE_CLASS_TEXT);
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
//                            Toast.makeText(SurveyDetails.this, "Please Enter Account Number.", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent();
                            intent.putExtra("flow","SURVEY");
                            intent.setComponent(new ComponentName(getApplicationContext(), ConsumerSurvey.class));
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);
                        } else {

                            android.util.Log.e( "METERTYPES", "Consumer Num : " + accountnum);

                            dbHelper = new DB(getApplicationContext());
                            SD = dbHelper.getWritableDatabase();

                            String ret = "SELECT * FROM TBL_CONSUMERSURVEY_MASTER WHERE Meter_S_No ='" + accountnum+"'";
                            Cursor curconsmasData = SD.rawQuery(ret, null);

                            if (curconsmasData != null && curconsmasData.moveToFirst()) {

                                uac = new UtilAppCommon();
                                try {
                                    uac.copyResultToCONSurveyMaster(curconsmasData);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                Intent intent = new Intent();
                                intent.putExtra("flow","SURVEY");
                                intent.setComponent(new ComponentName(getApplicationContext(), ConsumerSurvey.class));
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left);

                            } else {
                                dialogAccount.dismiss();

                                Intent intent = new Intent();
                                intent.putExtra("flow","SURVEY");
                                intent.setComponent(new ComponentName(getApplicationContext(), ConsumerSurvey.class));
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left);

                                Toast.makeText(ConsumerSearch.this, "Account Number Not Found", Toast.LENGTH_SHORT).show();
                            }

                            // Intent intent=new Intent(getApplicationContext(),CameraActivity.class);
                            // startActivity(intent);
                        }

                    }
                });
                dialogAccount.show();


            }
        });

        btn_Old_No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /****DIALOG BOX INITIALIZATION PROVIDING A LAYOUT TO DIALOUGE****/
                final Dialog dialogAccount   = new Dialog(ConsumerSearch.this, R.style.DialogeAppTheme);
                dialogAccount.setContentView(R.layout.custom_dialoge_search);
                dialogAccount.setTitle("Old Consumer Search");
                // set the custom dialog components - text, image and button
                final EditText editTextAccno = (EditText) dialogAccount.findViewById(R.id.EditTextACC);
                editTextAccno.setHint("Enter Old Consumer No");
                editTextAccno.setInputType(InputType.TYPE_CLASS_TEXT);
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
//                            Toast.makeText(SurveyDetails.this, "Please Enter Account Number.", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent();
                            intent.putExtra("flow","SURVEY");
                            intent.setComponent(new ComponentName(getApplicationContext(), ConsumerSurvey.class));
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);
                        } else {

                            android.util.Log.e( "METERTYPES", "Consumer Num : " + accountnum);

                            dbHelper = new DB(getApplicationContext());
                            SD = dbHelper.getWritableDatabase();

                            String ret = "SELECT * FROM TBL_CONSUMERSURVEY_MASTER WHERE Old_Consumer_Number ='" + accountnum+"'";
                            Cursor curconsmasData = SD.rawQuery(ret, null);

                            if (curconsmasData != null && curconsmasData.moveToFirst()) {

                                uac = new UtilAppCommon();
                                try {
                                    uac.copyResultToCONSurveyMaster(curconsmasData);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                Intent intent = new Intent();
                                intent.putExtra("flow","SURVEY");
                                intent.setComponent(new ComponentName(getApplicationContext(), ConsumerSurvey.class));
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left);

                            } else {
                                dialogAccount.dismiss();

                                Intent intent = new Intent();
                                intent.putExtra("flow","SURVEY");
                                intent.setComponent(new ComponentName(getApplicationContext(), ConsumerSurvey.class));
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left);

                                Toast.makeText(ConsumerSearch.this, "Account Number Not Found", Toast.LENGTH_SHORT).show();
                            }

                            // Intent intent=new Intent(getApplicationContext(),CameraActivity.class);
                            // startActivity(intent);
                        }

                    }
                });
                dialogAccount.show();

            }
        });

    }
}
