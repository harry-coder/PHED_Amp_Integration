package com.fedco.mbc.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Debug;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.DownloadAsyncFileFromURL;
import com.fedco.mbc.utils.UtilAppCommon;

import java.util.ArrayList;
import java.util.HashMap;

public class ReporttypesActivity extends Activity {

    HashMap<String, String> hmconsmasData;
    static final ArrayList<HashMap<String, String>> allist = new ArrayList<HashMap<String, String>>();
    final Context context = this;
    UtilAppCommon uac;
    private Boolean exit = false;
    Button btnExit, btnBack, btn_NewConsumer, btn_MeterChange, btn_UnbuildConsumer, btn_UnbuildConsumerSync,btn_MDCustomer,btn_NonMDCustomer;
    String accountnum, name;
    Logger Log;
    SQLiteDatabase SD;
    DB dbHelper;
    private static ReporttypesActivity sActivity;

    public static ReporttypesActivity getsActivity() {
        return sActivity;
    }

    Button bt_meteredList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporttypes);
        sActivity = this;

        /****BUTTON  INITIALISATION****/

        btn_NewConsumer = (Button) findViewById(R.id.ButtonNewC);
        btn_MeterChange = (Button) findViewById(R.id.ButtonMeterC);
        btn_UnbuildConsumer = (Button) findViewById(R.id.ButtonUnbuildC);
        btn_UnbuildConsumerSync = (Button) findViewById(R.id.ButtonSync);
        //btnExit =(Button)findViewById(R.id.ButtonExit);
        btn_MDCustomer = (Button) findViewById(R.id.ButtonMD);
        btn_NonMDCustomer = (Button) findViewById(R.id.ButtonUNMD);
        btnBack = (Button) findViewById(R.id.Buttonback);
        bt_meteredList=findViewById ( R.id.bt_meteredList );

        if(Home.isMeter){

            btn_UnbuildConsumer.setText("  Reading Not Taken Customers");
            bt_meteredList.setText ( "Reading Taken Customers" );

        }
        else {
            btn_UnbuildConsumer.setText("  UnMetered Customer");

            bt_meteredList.setText ( "Metered Customer" );
        }


        /****BUTTON  ACTION   EVENTS****/

        btn_NewConsumer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewConsumerReportActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });
        btn_MeterChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MeterChangeReportActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });
        btn_UnbuildConsumer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UnbilledConsumerReportActivity.class);
                intent.putExtra("ReportFlag","UNBILLED");
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });
        btn_MDCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UnbilledConsumerReportActivity.class);
                intent.putExtra("ReportFlag","MDCustomer");
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });
        bt_meteredList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UnbilledConsumerReportActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("ReportFlag","MeteredCustomer");
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });
        btn_NonMDCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UnbilledConsumerReportActivity.class);
                intent.putExtra("ReportFlag","NONMDCustomer");
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right);
            }
        });


        uac = new UtilAppCommon();
        name = uac.UniqueCode(getApplicationContext());

        btn_UnbuildConsumerSync.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                dbHelper = new DB(ReporttypesActivity.this);
                SD = dbHelper.getWritableDatabase();

                String strPref = "SELECT DISTINCT LOC_CD,Cycle,Bill_Mon FROM TBL_CONSMAST";

                Cursor getPref = SD.rawQuery(strPref, null);
                if (getPref != null ) {
                    if  (getPref.moveToFirst()) {
                        do {
                            if (isNetworkAvailable(getApplicationContext())) {

                                android.util.Log.e("IN SYNC ", " CHK " + getPref.getString(0) + getPref.getString(1) + getPref.getString(2) + name);
                                new DownloadAsyncFileFromURL(getApplicationContext(), "Billed", getPref.getString(0), getPref.getString(1), getPref.getString(2), name).execute();

                            }else{
                                Toast.makeText(getApplicationContext(), "Internet Connection Required", Toast.LENGTH_SHORT).show();
                            }

                        }while (getPref.moveToNext());
                    }
                }

////                for (int i = 0; i <= getPref.getCount(); i++) {
//                    if (getPref != null && getPref.moveToFirst()) {
//
//                        if (isNetworkAvailable(getApplicationContext())) {
//
//
////                            new DownloadAsyncFileFromURL(getApplicationContext(), "Billed", getPref.getString(0), getPref.getString(1), getPref.getString(2), name).execute();
//                            android.util.Log.e("IN SYNC ", " CHK " + getPref.getString(0) + getPref.getString(1) + getPref.getString(2) + name);
//
//
//                        } else {
//
//                            Toast.makeText(getApplicationContext(), "Internet Connection Required", Toast.LENGTH_SHORT).show();
//
//                        }
//
//                    }
            }
//                if(getPref.moveToFirst())
//                {
//
//                    do
//                    {
//                        if (isNetworkAvailable(getApplicationContext())) {
//
//                            new DownloadAsyncFileFromURL(getApplicationContext(),"Billed", getPref.getString(0),getPref.getString(1),getPref.getString(2),name).execute();
//
//                            Log.e(getApplicationContext(),"LOOOOOPING"," SYNC"+getPref.getString(0));
//                            Log.e(getApplicationContext(),"LOOOOOPING"," SYNC"+getPref.getString(1));
//                            Log.e(getApplicationContext(),"LOOOOOPING"," SYNC"+getPref.getString(2));
//
//                        } else {
//
//                            Toast.makeText(getApplicationContext(), "Internet Connection Required", Toast.LENGTH_SHORT).show();
//
//                        }
//
//                    }while(getPref.moveToNext());//Move the cursor to the next row.
//                }
//                else
//                {
//                    Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
//                }

//            }
        });


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.home:

                finish();
                overridePendingTransition(R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right);
                return true;


            default:
                break;
        }

        return super.onOptionsItemSelected(item);

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

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Debug.stopMethodTracing();
        super.onDestroy();
    }

}
