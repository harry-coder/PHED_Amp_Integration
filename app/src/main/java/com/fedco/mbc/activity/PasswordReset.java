package com.fedco.mbc.activity;

/* printer Category -
* 0-Impact
* 1-Thermal
*  printer MFG-
* 0-amigos
* 1-Rego
* 2-Sil
*  printer Roll
* 0-White
* 1-Pre-print
* */


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.text.Editable;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fedco.mbc.R;
import com.fedco.mbc.amigos.MainActivityCollectionPrint;
import com.fedco.mbc.authentication.PrinterSessionManager;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.model.Structcolmas;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.URLS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.lang.reflect.Method;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PasswordReset extends AppCompatActivity {
    SQLiteDatabase SD, SD4;
    DB dbHelper, dbHelper4;

    final Context context = this;
    JSONObject jsonObject = new JSONObject();
    SweetAlertDialog sDialog;
    EditText oldPassword,newPassword,confrmPassword;
    String _newPassword;
    String _confrmPasssword;
    String _oldPassword;
    SessionManager session;

    PrinterSessionManager printsession;
    Button OK;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_reset);
        oldPassword = (EditText) findViewById(R.id.oldpassword);
        newPassword = (EditText) findViewById(R.id.newpassword);
        confrmPassword = (EditText) findViewById(R.id.confirmpassword);
        OK = (Button) findViewById(R.id.ButtonOK);


        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _newPassword = newPassword.getText().toString().trim();
                _confrmPasssword = confrmPassword.getText().toString().trim();
                _oldPassword = oldPassword.getText().toString().trim();

                if (oldPassword.equals("")) {
                    oldPassword.setError("old password cannot be blank");
                } else if (_newPassword.equals("")) {
                    newPassword.setError("new password cannot be blank");
                } else if (_confrmPasssword.equals("")) {
                    confrmPassword.setError("confirm password cannot be blank");
                } else if (String.valueOf(_newPassword.toString()).equals(String.valueOf(_confrmPasssword))) {

                    try {
                       String str="";
                        dbHelper = new DB(getApplicationContext());
                        SD = dbHelper.getWritableDatabase();
                        String getmrid ="SELECT ID from USER_MASTER";
                        Cursor todoCursor = SD.rawQuery(getmrid, null);
                        if (todoCursor != null && todoCursor.moveToFirst()) {
                            do {
                                getmrid = todoCursor.getString(0);
                            }
                            while (todoCursor.moveToNext());

                        } else {
                            Toast.makeText(PasswordReset.this, "No data Found", Toast.LENGTH_SHORT).show();
                        }
                        jsonObject.put("UserId",getmrid);
                        jsonObject.put("Password", _oldPassword);
                        jsonObject.put("NewPassword", _newPassword);
                        try {
                            if (new CommonHttpConnection(getApplicationContext()).isConnectingToInternet()) {
                                new password().execute();
                            } else {
                                Toast.makeText(PasswordReset.this, "Internet connection required", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                } else {
                    confrmPassword.setError("Password Mismatch");
                }

//                    try {
//
//
//                    } catch (Exception ex) {
//
//                        Log.e("", "insert into table operation", ex);
//
//                    } finally {
//
//
//                    }
//
//                    if (GSBilling.getInstance().getAappFlow().equalsIgnoreCase("billing")) {
//                        Intent intent = new Intent(getApplicationContext(), Billing.class);
//                        intent.putExtra("flowkey", "billing");
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.anim_slide_in_left,
//                                R.anim.anim_slide_out_left);
//
//                    } else {
//                        Intent intent = new Intent(getApplicationContext(), Collection.class);
//                        intent.putExtra("flowkey", "billing");
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.anim_slide_in_left,
//                                R.anim.anim_slide_out_left);
//
//                    }
            }


        });
    }


    private class password extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(isMobileDataEnabled()) {
                showProgressDialog();
            }else {
                Toast.makeText(PasswordReset.this, "internet is required", Toast.LENGTH_SHORT).show();
            }

        }

        /**
         * getting Profile JSON
         * */
        protected String doInBackground(String... params) {

             BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;


            try {




                String url= URLS.VersionCode.changepassword ;

                // URL url = new URL(URLS.VersionCode.meterno + codeIMEI + "/" + params[0]+"/"+last_recept_no);

//                Log.e(context, "MainAct", URLS.VersionCode.meterno  + "/" + params[0]);
//                Log.e(context, "MainAct", "para1 " + params[0]);
                try {

                        forecastJsonStr= new CommonHttpConnection(getApplicationContext()).GetHttpConnection(URLS.UserAccess.checkConnection, 10000);
                        if(forecastJsonStr.equals("OK")) {
                            forecastJsonStr = new CommonHttpConnection(getApplicationContext()).PostHttpConnection(url,jsonObject, 10000);
                        }else {
                            return "Mobile data not available";
                        }


                }catch (Exception ex)
                {
                    return ex.toString();
                }

            } finally {

            }
//                return null;

            return forecastJsonStr;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String str="";
            String str1="";
            if (PasswordReset.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                return;
            }
            dismissProgressDialog();
            if(s.equals("USER NOT FOUND")){
                sDialog = new SweetAlertDialog(PasswordReset.this, SweetAlertDialog.ERROR_TYPE);
                sDialog.setTitleText("Password");
                sDialog.setContentText("Please verify Password");
                sDialog.show();
                sDialog.setCanceledOnTouchOutside(false);
                sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
//                        if (GSBilling.getInstance().getAappFlow().equalsIgnoreCase("billing")) {
//                            Intent intent = new Intent(getApplicationContext(), Billing.class);
//                            intent.putExtra("flowkey", "billing");
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                    R.anim.anim_slide_out_left);
//
//                        } else {
//                            Intent intent = new Intent(getApplicationContext(), Collection.class);
//                            intent.putExtra("flowkey", "billing");
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                    R.anim.anim_slide_out_left);
//
//                        }
                    }
                });
            }else {
                try {
                    JSONArray ja = null;
//              SBilling.getInstance().TokenNo= str;
                    ja = new JSONArray(s);
                    JSONObject jsonSection = ja.getJSONObject(0);
                    str = jsonSection.getString("UserId");
                    str1 = jsonSection.getString("NewPassword");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dbHelper = new DB(getApplicationContext());
                SD = dbHelper.getWritableDatabase();
                String getmrid = "UPDATE USER_MASTER set PASS ='" + str1 + "' where ID='" + str + "'";
                Cursor todoCursor = SD.rawQuery(getmrid, null);
                if (todoCursor != null && todoCursor.moveToFirst()) {
                    do {
                        getmrid = todoCursor.getString(0);
                    }
                    while (todoCursor.moveToNext());
                }
                oldPassword.setText("");
                newPassword.setText("");
                confrmPassword.setText("");
                sDialog = new SweetAlertDialog(PasswordReset.this, SweetAlertDialog.SUCCESS_TYPE);
                sDialog.setTitleText("Password");
                sDialog.setContentText("Successfully Updated");
                sDialog.show();
                sDialog.setCanceledOnTouchOutside(false);
                sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        if (GSBilling.getInstance().getAappFlow().equalsIgnoreCase("billing")) {
                            Intent intent = new Intent(getApplicationContext(), Billing.class);
                            intent.putExtra("flowkey", "billing");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);

                        } else {
                            Intent intent = new Intent(getApplicationContext(), Collection.class);
                            intent.putExtra("flowkey", "billing");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);

                        }
                    }
                });
//                Toast.makeText(getApplicationContext(),"Password updated successfully",Toast.LENGTH_LONG).show();
            }
//            Log.e(context, "MainAct", "PostString " + s);
//            if(!s.equals("null")&& !s.isEmpty()&& !s.equals("NULL")&&s.length()>15) {
//                if(s.equals("Mobile data not available")){
//                    Toast.makeText(PasswordReset.this, "Mobile data not available", Toast.LENGTH_SHORT).show();
//                }else if (s.equals("Consumer Not Found")|| s.equals("404 Not Found"))
//                {
////                  Toast.makeText(Collection.this, "consumer not found", Toast.LENGTH_SHORT).show();
//                    sDialog = new SweetAlertDialog(PasswordReset.this, SweetAlertDialog.ERROR_TYPE);
//                    sDialog.setTitleText("Error");
//                    sDialog.setContentText("consumer not found");
//                    sDialog.show();
//                    sDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                        @Override
//                        public void onClick(SweetAlertDialog sDialog) {
//                            sDialog.dismissWithAnimation();
//
//                        }
//                    });
//                }else if (s.contains("METER_INST_FLAG"))
//                {
//                    JSONArray ja = null;
//                    try {
//                        ja = new JSONArray(s);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }else {
//                Toast.makeText(PasswordReset.this, "InternetConnection Problem Try Again", Toast.LENGTH_SHORT).show();
//            }

        }

    }
    private void showProgressDialog() {
        if (sDialog == null) {
            sDialog = new SweetAlertDialog(PasswordReset.this, SweetAlertDialog.PROGRESS_TYPE);
            sDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            sDialog.setTitleText("Loading");
            sDialog.setCancelable(false);
            sDialog.show();
        }
        sDialog.show();
    }
    private void dismissProgressDialog() {
        if (sDialog != null && sDialog.isShowing() ) {
            sDialog.dismiss();
//            dialogAccount.dismiss();
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

    }

//    private void autoCheck() {
//        String printerType, printerMfg = null, printerRoll;
//        String printerTypeCode = prev_pref.split("_")[0];
//        String printerMfgCode = prev_pref.split("_")[1];
//        String printerRollCode = prev_pref.split("_")[2];
//
//        if (printerTypeCode.equalsIgnoreCase("0")) {
//            printerType = "Imapct";
//        } else {
//            printerType = "Thermal";
//        }
//
//        if (printerRollCode.equalsIgnoreCase("0")) {
//            printerRoll = "Pre print roll";
//        } else {
//            printerRoll = "White roll";
//        }
//
//        switch (printerMfgCode.toString()) {
//            case "0":
//                printerMfg = "Amigos";
//                break;
//            case "1":
//                printerMfg = "Rego";
//                break;
//            case "2":
//                printerMfg = "Softland";
//                break;
//        }
//
//        String Prev_pref_text = printerMfg + " " + printerType + " " + printerRoll;
//        txt_imp_desc.setText(Prev_pref_text);
//
////        if(prev_pref!=null && !prev_pref.isEmpty()) {
////
////            if (prev_pref.split("_")[0].equalsIgnoreCase("0")) {
////                cb_imp.setChecked(true);
////            }
////
////        }
//
//
//    }


