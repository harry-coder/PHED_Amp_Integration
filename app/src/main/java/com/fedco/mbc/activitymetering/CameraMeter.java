package com.fedco.mbc.activitymetering;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.activity.Home;
import com.fedco.mbc.activity.MeterState;
import com.fedco.mbc.bluetoothprinting.DuplicateBillPrint;

import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.model.Structbilling;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.model.Structmetering;
import com.fedco.mbc.model.Structmeterupload;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.UtilAppCommon;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class CameraMeter extends AppCompatActivity {

    TextView tvAcc, tvName, tvAdd, tvMn, tvTc, tvCl ,tvCltext,tvOCN,tvBM;
    ImageView imgWarnign;
    Button BtnContinue;
    final Context context = this;
    Logger Log;

    public static final String TAG = "ShootAndCropActivity";
    LinearLayout layoutConsDetail;

    SQLiteDatabase SD;
    DB dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        try {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
            getSupportActionBar().setLogo(R.mipmap.logo);
            setTitle("Consumer Details");
        } catch (NullPointerException npe) {
            Log.e(getApplicationContext(), "Billing Act", "ActionBar Throwing null Pointer", npe);
        }

        File folder = new File(Environment.getExternalStorageDirectory()
                + File.separator + "/MBC/Images/");
        if (!folder.exists()) {
            folder.mkdir();
        }

        UtilAppCommon uc = new UtilAppCommon();
        uc.princonmas();

        /*LOGGER ACTIVATED FOR THIS ACTIVITY*/
        tvAcc   = (TextView) findViewById(R.id.TextViewACCValue);
        tvName  = (TextView) findViewById(R.id.TextViewNameValue);
        tvAdd   = (TextView) findViewById(R.id.TextViewADDValue);
        tvMn    = (TextView) findViewById(R.id.TextViewMNValue);
        tvTc    = (TextView) findViewById(R.id.TextViewTCValue);
        tvCl    = (TextView) findViewById(R.id.TextViewTRRRValue);
        tvOCN   = (TextView) findViewById(R.id.TextViewOCNValue);
        tvBM    = (TextView) findViewById(R.id.TextViewBMValue);
        imgWarnign    = (ImageView) findViewById(R.id.imageWarning);
        layoutConsDetail = (LinearLayout) findViewById(R.id.LLconsumerdet);
        //  tvCltext    = (TextView) findViewById(R.id.TextViewCL);
        //  tvCl.setVisibility(View.GONE);
        //  tvCltext.setVisibility(View.GONE);

        BtnContinue = (Button) findViewById(R.id.ButtonContinue);

        tvAcc   .setText(" :  "+Structmetering.CONSUMERNO);
        tvName  .setText(" :  "+Structmetering.NAME);
        tvAdd   .setText(" :  "+Structmetering.ADDRESS );
        tvMn    .setText(" :  "+Structmetering.METERDEVICESERIALNO);
        tvTc    .setText(" :  "+Structmetering.TARIFFCODE);
        tvCl    .setText(" :  "+Structmetering.CYCLE);
        tvOCN   .setText(" :  "+Structmetering.OLDCONSUMERNO);
        tvBM    .setText(" :  "+Structmetering.BILLMONTH);
        // tvCl.setText("");

        imgWarnign.setVisibility(View.GONE);

        if(Structmetering.PREVPAYMENT.matches("N")){

            imgWarnign.setVisibility(View.VISIBLE);

        }

        /*CAPTURING TARRIF DETAILS FOR PERTICULAR USER*/
        dbHelper = new DB(getApplicationContext());
        SD = dbHelper.getWritableDatabase();

        uc.printarrif();

        BtnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String duplicateaccQuery = "select * from TBL_METERUPLOAD WHERE BILLMONTH ='" + Structmetering.BILLMONTH + "' and CONSUMERNO='" + (Structmetering.CONSUMERNO) + "'";
                final Cursor curdupacc = SD.rawQuery(duplicateaccQuery, null);

                /****GETTING DATA FROM CURSOR WHEN CURSOR IS NOT NULL AND ON THE FORST INDEX****/
                if (curdupacc != null && curdupacc.moveToFirst()) {


                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Meter Reading Already Done ")
                            .setContentText("for the billing month of "+Structmetering.BILLMONTH)
                            .setConfirmText("OK")
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    Intent intent = new Intent(getApplicationContext(), Home.class);
                                    // intent.putExtra("datalist", allist);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.anim_slide_in_left,
                                            R.anim.anim_slide_out_left);

                                }
                            })

                            .show();
                } else {

                    final Dialog dialog = new Dialog(context, android.R.style.Theme_DeviceDefault_Light);
                    dialog.setContentView(R.layout.custom_dialoge_metering);
                    dialog.setTitle("Contact Details");

                    // set the custom dialog components - text, image and button
                    final EditText editTextAccno = (EditText) dialog.findViewById(R.id.EditTextmobno);
                    final EditText editTextEmailid = (EditText) dialog.findViewById(R.id.EditTextemail);
                    final EditText editTextpan = (EditText) dialog.findViewById(R.id.EditTextPanno);

                    editTextAccno.setText(Structmetering.MOB_NO);
                    editTextEmailid.setText(Structmetering.EMAIL);
                    editTextpan.setText(Structmetering.PANNO);

                    final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                    final String pattern = "[A-Z]{5}\\d{4}[A-Z]{1}";



                    final Button dialogButtonContinue = (Button) dialog.findViewById(R.id.dialogButtonContinue);
                    Button dialogButtonSkip = (Button) dialog.findViewById(R.id.dialogButtonSkip);
                    /*// if button is clicked, close the custom dialog
                    *//*DIALOUGE BOX BUTTON INTIALISATION AND EVENT ADDING*/
                    dialogButtonContinue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            String email = editTextEmailid.getText().toString().trim();
                            String panno = editTextpan.getText().toString().trim();


                            /*------------------IF ALL ARE EMPTY---------------------*/
                            if (editTextAccno.getText().toString().length() == 0  && email.toString().length() == 0 &&  panno.toString().length() == 0) {

                                Structmeterupload.MOB_NO = "";
                                Structmeterupload.EMAIL = "";
                                Structmeterupload.PANNO = "";

                                Toast.makeText(CameraMeter.this, "No Data Found to Submit", Toast.LENGTH_SHORT).show();

                            }/*------------------IF MOBILE AVAILABLE---------------------*/
                            else if (editTextAccno.getText().toString().length() != 0  && email.toString().length() == 0 &&  panno.toString().length() == 0)
                            {
                                if (editTextAccno.getText().toString().length() < 10)
                                {
                                    editTextAccno.setError("Please Enter a valid Mobile no");
                                }
                                else
                                {
                                    Structmeterupload.MOB_NO = editTextAccno.getText().toString().trim();
                                    Structmeterupload.EMAIL = "";
                                    Structmeterupload.PANNO = "";

                                    Intent intent = new Intent(getApplicationContext(), MeterStateMtr.class);
                                    //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.anim_slide_in_left,
                                            R.anim.anim_slide_out_left);

                                    dialog.dismiss();

                                }

                            }/*------------------IF MOBILE & EMAIL AVAILABLE---------------------*/
                            else if (editTextAccno.getText().toString().length() != 0  && email.toString().length() != 0 &&  panno.toString().length() == 0)
                            {

                                if (editTextAccno.getText().toString().length() < 10)
                                {
                                    editTextAccno.setError("Please Enter a valid Mobile no");

                                }else if(!email.matches(emailPattern)){

                                    editTextEmailid.setError("Invalid Email id");

                                }
                                else
                                {

                                    Structmeterupload.MOB_NO = editTextAccno.getText().toString().trim();
                                    Structmeterupload.EMAIL = editTextEmailid.getText().toString().trim();
                                    Structmeterupload.PANNO = "";

                                    Intent intent = new Intent(getApplicationContext(), MeterStateMtr.class);
                                    //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.anim_slide_in_left,
                                            R.anim.anim_slide_out_left);

                                    dialog.dismiss();

                                }
                            } /*------------------IF MOBILE, EMAIL & PANNO AVAILABLE---------------------*/
                            else if (editTextAccno.getText().toString().length() != 0  && email.toString().length() != 0 &&  panno.toString().length() != 0)
                            {
                                if (editTextAccno.getText().toString().length() < 10)
                                {
                                    editTextAccno.setError("Please Enter a valid Mobile no");
                                }
                                else if (!email.matches(emailPattern))
                                {
                                    editTextEmailid.setError("Invalid Email id");
                                }
                                else if (!isValidPan(panno))
                                {
                                    editTextpan.setError("Invalid PAN No.");
                                }
                                else
                                {
                                    Structmeterupload.MOB_NO = editTextAccno.getText().toString().trim();
                                    Structmeterupload.EMAIL = editTextEmailid.getText().toString().trim();
                                    Structmeterupload.PANNO = editTextpan.getText().toString().trim();

                                    Intent intent = new Intent(getApplicationContext(), MeterStateMtr.class);
                                    //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.anim_slide_in_left,
                                            R.anim.anim_slide_out_left);

                                    dialog.dismiss();
                                }
                            }/*------------------IF EMAIL AVAILABLE---------------------*/
                            else if (editTextAccno.getText().toString().length() == 0  && email.toString().length() != 0 &&  panno.toString().length() == 0)
                            {
                                if (!email.matches(emailPattern))
                                {
                                    editTextEmailid.setError("Invalid Email id");
                                }
                                else
                                {
                                    Structmeterupload.MOB_NO = "";
                                    Structmeterupload.EMAIL = editTextEmailid.getText().toString().trim();
                                    Structmeterupload.PANNO = "";

                                    Intent intent = new Intent(getApplicationContext(), MeterStateMtr.class);
                                    //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.anim_slide_in_left,
                                            R.anim.anim_slide_out_left);

                                    dialog.dismiss();
                                }
                            }/*------------------IF PAN & EMAIL AVAILABLE---------------------*/
                            else if (editTextAccno.getText().toString().length() == 0  && email.toString().length() != 0 &&  panno.toString().length() != 0) {

                                if (!email.matches(emailPattern))
                                {
                                    editTextEmailid.setError("Invalid Email id");
                                }
                                else if (!isValidPan(panno))
                                {
                                    editTextpan.setError("Invalid PAN No.");
                                }
                                else
                                {
                                    Structmeterupload.MOB_NO = "";
                                    Structmeterupload.EMAIL = editTextEmailid.getText().toString().trim();
                                    Structmeterupload.PANNO = editTextpan.getText().toString().trim();

                                    Intent intent = new Intent(getApplicationContext(), MeterStateMtr.class);
                                    //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.anim_slide_in_left,
                                            R.anim.anim_slide_out_left);

                                    dialog.dismiss();

                                }
                            }/*------------------IF PAN AVAILABLE---------------------*/
                            else if (editTextAccno.getText().toString().length() == 0  && email.toString().length() == 0 &&  panno.toString().length() != 0)
                            {
                                if (!isValidPan(panno))
                                {
                                    editTextpan.setError("Invalid PAN No.");
                                }
                                else
                                {
                                    Structmeterupload.MOB_NO = "";
                                    Structmeterupload.EMAIL = "";
                                    Structmeterupload.PANNO =editTextpan.getText().toString().trim();

                                    Intent intent = new Intent(getApplicationContext(), MeterStateMtr.class);
                                    //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.anim_slide_in_left,
                                            R.anim.anim_slide_out_left);

                                    dialog.dismiss();
                                }
                            }



                        }
                    });
                    dialogButtonSkip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(getApplicationContext(), MeterStateMtr.class);
                            // Intent intent = new Intent(getApplicationContext(), MeteringReadinginput.class);
                            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            // intent.putExtra("IMAGE",bitmap);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);

                            dialog.dismiss();

                        }
                    });
                    dialog.show();

                }


            }
        });
    }
    public boolean isValidPan(String pan) {
        Pattern mPattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");

        Matcher mMatcher = mPattern.matcher(pan);
        return mMatcher.matches();
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

    public void onBackPressed() {

        finish(); // finish activity
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);

    }
}

