package com.fedco.mbc.replacement;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.BillingtypesActivity;
import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.activity.Home;
import com.fedco.mbc.activity.PrintSelection;
import com.fedco.mbc.activity.ReporttypesActivity;
import com.fedco.mbc.activity.SDActivity;
import com.fedco.mbc.activity.Summery;
import com.fedco.mbc.authentication.PrinterSessionManager;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.UtilAppCommon;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

//import com.fedco.mbc.authentication.PrintSessionManager;

public class Replacement extends AppCompatActivity {

    private Boolean exit = false;
    Logger Log;
    final Context context = this;
    Button btnBilling, btnUBT, btnReport, btnSummery;
    public String Thermal_enable, print_type;
    String pass;

    // Session Manager Class
    SessionManager session;
    PrinterSessionManager printsession;
    Handler handler;
    UtilAppCommon appCommon;

    SQLiteDatabase SD, SD3;
    DB databasehelper, dbHelper4;
    // int pendingcount = 300;
    int pendingcount =150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        try {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
            getSupportActionBar().setLogo(R.mipmap.logo);
            setTitle("MPMBCApp");
        } catch (NullPointerException npe) {
            Log.e(getApplicationContext(), "Replacement Act", "ActionBar Throwing null Pointer", npe);
        }

        session = new SessionManager(getApplicationContext());
        printsession = new PrinterSessionManager(getApplicationContext());
        databasehelper = new DB(context);
        appCommon=new UtilAppCommon();

        btnBilling = (Button) findViewById(R.id.buttonBilling1);
        btnUBT = (Button) findViewById(R.id.buttonBilling2);
        btnReport = (Button) findViewById(R.id.buttonBilling3);
        btnSummery = (Button) findViewById(R.id.buttonBilling4);
        HashMap<String, String> user = session.getUserDetails();
        // name
        String name = session.retLicence();
        // email
        String email = user.get(SessionManager.KEY_EMAIL);
        String Key = user.get(SessionManager.KEY_LICENCE);

        btnBilling.setText("    Replacement");

        GSBilling.getInstance().setKEYNAME(appCommon.UniqueCode(getApplicationContext()));

        session.checkLogin();

        Log.e(getApplicationContext(), "BillAct", "getting name : " + name);
        Log.e(getApplicationContext(), "BillAct", "getting Pass : " + email);
        Log.e(getApplicationContext(), "BillAct", "getting Key  : " + Key);


        btnBilling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UtilAppCommon ucom = new UtilAppCommon();
                ucom.nullyfimodelCon();
                ucom.nullyfimodelBill();

                Intent intent = new Intent(context, ReplacementtypesActivity.class);
                System.out.println(GSBilling.getInstance().getPrintertype());
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);

////                Intent serviceIntent = new Intent(Replacement.this, BackgroundService.class);
////                startService(serviceIntent);
//
//                HashMap<String, String> printuser = printsession.getUserDetails();
//                String printype = printuser.get(printsession.KEY_EMAIL);
//                String print = printuser.get(printsession.KEY_NAME);
//
//                GSBilling.getInstance().setPrintertype(printype);
//                GSBilling.getInstance().setPrinttype(print);
//
//                Log.e(getApplicationContext(), "BillAct", "getting print  : " + print);
//                Log.e(getApplicationContext(), "BillAct", "getting printer: " + printype);
//
//                try {
//                    if (GSBilling.getInstance().getPrintertype().equals(null) && GSBilling.getInstance().getPrintertype().equals(GSBilling.getInstance().getKEYNAME())) {
//
//                        GSBilling.getInstance().clearData();
//                        Toast.makeText(Replacement.this, "Please configure the Printer ", Toast.LENGTH_SHORT).show();
//
//                    } else {
//
//                        if (!GSBilling.getInstance().getPrintertype().equals("0") && !GSBilling.getInstance().getPrintertype().equals("1")) {
//
//                            Toast.makeText(Replacement.this, "Please configure the Printer ", Toast.LENGTH_SHORT).show();
//
//                        } else {
//                            dbHelper4 = new DB(getApplicationContext());
//                            SD3 = dbHelper4.getWritableDatabase();
//                            String pending = "SELECT Cons_Number from 'TBL_BILLING' WHERE Upload_Flag='N'";
////                            String pending = "SELECT Cons_Number from 'TBL_BILLING' WHERE Upload_Flag='N'";
//                            final Cursor curPend = SD3.rawQuery(pending, null);
//                            curPend.getCount();
//                            // if(curPend.getCount()>0){
//                            //  if (curPend.getCount() > pendingcount) {
////                                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
////                                        .setTitleText("Records are Pending ?")
////                                        .setContentText("More Than " + curPend.getCount() + " records are pending for upload!")
////                                        .setCancelText("No,cancel plz!")
////                                        .setConfirmText("OK!")
////                                        .showCancelButton(true)
////                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
////                                            @Override
////                                            public void onClick(SweetAlertDialog sDialog) {
////
////                                                Intent intent = new Intent(context, SDActivity.class);
////                                                startActivity(intent);
////                                                overridePendingTransition(R.anim.anim_slide_in_left,
////                                                        R.anim.anim_slide_out_left);
////                                            }
////                                        })
////                                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
////                                            @Override
////                                            public void onClick(SweetAlertDialog sDialog) {
////                                                sDialog.cancel();
////                                            }
////                                        })
////                                        .show();
////
////                            } else {
////
////                                Intent intent = new Intent(context, BillingtypesActivity.class);
////                                System.out.println(GSBilling.getInstance().getPrintertype());
////                                startActivity(intent);
////                                overridePendingTransition(R.anim.anim_slide_in_left,
////                                        R.anim.anim_slide_out_left);
////                            }
//                            Intent intent = new Intent(context, ReplacementtypesActivity.class);
//                            System.out.println(GSBilling.getInstance().getPrintertype());
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                    R.anim.anim_slide_out_left);
//                        }
//                    }
//                } catch (NullPointerException n) {
//                    Toast.makeText(Replacement.this, "Please configure the Printer ", Toast.LENGTH_SHORT).show();
//                }

            }
        });

        btnUBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                            intent.putExtra("flowkey", "replacement");
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
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), ReplacementReportsActivity.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);

            }
        });
        btnSummery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Summery.class);
                intent.putExtra("FLAG", "Replacement");
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);

            }
        });
    }

    public void onBackPressed() {

        Intent intent = new Intent(Replacement.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Debug.stopMethodTracing();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.billing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), "Configuration is Under Process", Toast.LENGTH_LONG).show();
                break;
            case R.id.previous:

                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are You Sure to Logout ?")
                        .setContentText("Cannot use this application Until Login")
                        .setCancelText("No,cancel plz!")
                        .setConfirmText("OK!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                LogoutUser();
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .show();

                break;
            case R.id.next:
//                final Dialog dialogAccLogout = new Dialog(context, R.style.DialogeAppTheme);
//                dialogAccLogout.setContentView(R.layout.custom_dialoge_password);
//
//                // set the custom dialog components - text, image and button
//                final EditText editTextAccLogout = (EditText) dialogAccLogout.findViewById(R.id.EditTextACC);
//                Button dialogLogoutButton = (Button) dialogAccLogout.findViewById(R.id.dialogButtonACCOK);
//                // if button is clicked, close the custom dialog
//                /****DIALOUGE BOX BUTTON INTIALISATION AND EVENT ADDING****/
//                dialogLogoutButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        try {
//                            pass = editTextAccLogout.getText().toString().trim();
//                        } catch (NullPointerException N) {
//                            N.printStackTrace();
//                        }
//
//                        if (pass.isEmpty()) {
//                            Toast.makeText(context, "Please Enter Account Number.", Toast.LENGTH_LONG).show();
//                        } else if (pass.equals("soubhagya")) {
//
////                            printsession.logoutUser();
//                            Intent intent = new Intent(context, PrintSelection.class);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                    R.anim.anim_slide_out_left);
//
//                        } else {
//                            dialogAccLogout.dismiss();
//                            Toast.makeText(context, " UnAuthorized Access ", Toast.LENGTH_LONG).show();
//                        }
//
//                    }
//                });
//                dialogAccLogout.show();

                GSBilling.getInstance().setAappFlow("billing");
                Intent intent = new Intent(context, PrintSelection.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
                break;
            case android.R.id.home:

                finish();
                overridePendingTransition(R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right);

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void LogoutUser() {

        SD = databasehelper.getWritableDatabase();
        String delStr = "DELETE FROM USER_MASTER";
        SD.execSQL(delStr);
        session.logoutUser();
        this.finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);
    }

    public void onMenuItemClick(View clickedView, int position) {
        switch (position) {
            case 1:
                //  ShowAlert();
                break;

        }
    }

}
