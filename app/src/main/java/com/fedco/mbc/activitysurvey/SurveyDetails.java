package com.fedco.mbc.activitysurvey;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.UtilAppCommon;

import java.sql.SQLException;


public class SurveyDetails extends AppCompatActivity {

    private Button btn_11kV_FeederSurvey,btn_DTR_Survey,btn_PoleSurvey,btn_ConsumerSurvey;
    Context context;
    String accountnum;


    SQLiteDatabase SD;
    DB dbHelper;

    UtilAppCommon uac;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_details);
        callView();
        uac=new UtilAppCommon();
        btn_11kV_FeederSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(getApplicationContext(), FeederSurvey_11kV.class));
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });
        btn_DTR_Survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(getApplicationContext(), DTR_Survey.class));
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });
        btn_PoleSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(getApplicationContext(), PoleSurvey.class));
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
//                Toast.makeText(getApplicationContext(),"Development is Under Progress ...",Toast.LENGTH_LONG).show();
            }
        });
        btn_ConsumerSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                /****DIALOG BOX INITIALIZATION PROVIDING A LAYOUT TO DIALOUGE****/
//                final Dialog dialogAccount   = new Dialog(SurveyDetails.this, R.style.DialogeAppTheme);
//                dialogAccount.setContentView(R.layout.custom_dialoge_search);
//                dialogAccount.setTitle("Account Search");
//                // set the custom dialog components - text, image and button
//                final EditText editTextAccno = (EditText) dialogAccount.findViewById(R.id.EditTextACC);
//                editTextAccno.setHint("Enter Account Number");
//                editTextAccno.setInputType(InputType.TYPE_CLASS_TEXT);
//                Button dialogButton = (Button) dialogAccount.findViewById(R.id.dialogButtonACCOK);
//                Button dHomeButton  = (Button) dialogAccount.findViewById(R.id.HomeButton);
//
//                dHomeButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        finish();
//                        overridePendingTransition(R.anim.anim_slide_in_right,
//                                R.anim.anim_slide_out_right);
//
//                    }
//                });
//                // if button is clicked, close the custom dialog
//                /****DIALOUGE BOX BUTTON INTIALISATION AND EVENT ADDING****/
//                dialogButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        try {
//                            accountnum = editTextAccno.getText().toString().trim();
//                        } catch (NullPointerException N) {
//                            N.printStackTrace();
//                        }
//
//                        if (accountnum.isEmpty()) {
////                            Toast.makeText(SurveyDetails.this, "Please Enter Account Number.", Toast.LENGTH_SHORT).show();
//
//                            Intent intent = new Intent();
//                            intent.putExtra("flow","SURVEY");
//                            intent.setComponent(new ComponentName(getApplicationContext(), ConsumerSurvey.class));
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                    R.anim.anim_slide_out_left);
//                        } else {
//
//                            Log.e( "METERTYPES", "Consumer Num : " + accountnum);
//
//                            dbHelper = new DB(getApplicationContext());
//                            SD = dbHelper.getWritableDatabase();
//
//                            String ret = "SELECT * FROM TBL_CONSUMERSURVEY_MASTER WHERE Consumer_Number ='" + accountnum+"'";
//                            Cursor curconsmasData = SD.rawQuery(ret, null);
//
//                            if (curconsmasData != null && curconsmasData.moveToFirst()) {
//
//                                uac = new UtilAppCommon();
//                                try {
//                                    uac.copyResultToCONSurveyMaster(curconsmasData);
//                                } catch (SQLException e) {
//                                    e.printStackTrace();
//                                }
//
//                                Intent intent = new Intent();
//                                intent.putExtra("flow","SURVEY");
//                                intent.setComponent(new ComponentName(getApplicationContext(), ConsumerSurvey.class));
//                                startActivity(intent);
//                                overridePendingTransition(R.anim.anim_slide_in_left,
//                                        R.anim.anim_slide_out_left);
//
//                            } else {
//                                dialogAccount.dismiss();
//
//                                Intent intent = new Intent();
//                                intent.putExtra("flow","SURVEY");
//                                intent.setComponent(new ComponentName(getApplicationContext(), ConsumerSurvey.class));
//                                startActivity(intent);
//                                overridePendingTransition(R.anim.anim_slide_in_left,
//                                        R.anim.anim_slide_out_left);
//
//                                Toast.makeText(SurveyDetails.this, "Account Number Not Found", Toast.LENGTH_SHORT).show();
//                            }
//
//                            // Intent intent=new Intent(getApplicationContext(),CameraActivity.class);
//                            // startActivity(intent);
//                        }
//
//                    }
//                });
//                dialogAccount.show();

                Intent intent = new Intent();
                intent.putExtra("flow","SURVEY");
                intent.setComponent(new ComponentName(getApplicationContext(), ConsumerSearch.class));
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);


//                Toast.makeText(getApplicationContext(),"Development is Under Progress ...",Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(getApplicationContext(),Survey.class));
        startActivity(intent);
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);
//        ShowAlertonBack();
    }
    private void callView(){
        btn_11kV_FeederSurvey = (Button) findViewById(R.id.button_11kv_feedersurvey);
        btn_DTR_Survey = (Button) findViewById(R.id.button_dtr_survey);
        btn_PoleSurvey = (Button) findViewById(R.id.button_pole_survey);
        btn_ConsumerSurvey = (Button) findViewById(R.id.button_consumer_survey);
    }
}
