package com.fedco.mbc.replacement;

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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.MeterChangeReportActivity;
import com.fedco.mbc.activity.NewConsumerReportActivity;
import com.fedco.mbc.activity.UnbilledConsumerReportActivity;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.DownloadAsyncFileFromURL;
import com.fedco.mbc.utils.UtilAppCommon;

import java.util.ArrayList;
import java.util.HashMap;

public class ReplacementReportsActivity extends Activity {

    HashMap<String, String> hmconsmasData;
    static final ArrayList<HashMap<String, String>> allist = new ArrayList<HashMap<String, String>>();
    final Context context = this;
    UtilAppCommon uac;
    private Boolean exit = false;
    Button btnExit, btnBack, btn_NewConsumer, btn_MeterChange, btn_UnbuildConsumer,
            btn_UnbuildConsumerSync;
    RelativeLayout layoutBack;
    String accountnum,name;
    Logger Log;
    SQLiteDatabase SD;
    DB dbHelper;
    private static ReplacementReportsActivity sActivity;
    public static ReplacementReportsActivity getsActivity() {
        return sActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporttypes);
        sActivity=this;

        /****BUTTON  INITIALISATION****/

        btn_NewConsumer = (Button) findViewById(R.id.ButtonNewC);
        btn_MeterChange = (Button) findViewById(R.id.ButtonMeterC);
        btn_UnbuildConsumer = (Button) findViewById(R.id.ButtonUnbuildC);
        btn_UnbuildConsumerSync = (Button) findViewById(R.id.ButtonSync);
        //btnExit =(Button)findViewById(R.id.ButtonExit);
//        btnBack =(Button)findViewById(R.id.ButtonBack);
        layoutBack =(RelativeLayout) findViewById(R.id.relback);
        btn_MeterChange.setVisibility(View.GONE);
        btn_NewConsumer.setVisibility(View.GONE);
        btn_UnbuildConsumerSync.setVisibility(View.GONE);
        btn_UnbuildConsumer.setText("  Replaced Consumer List");
        /****BUTTON  ACTION   EVENTS****/
        btn_NewConsumer.setText("Replaced Consumers List ");

        btn_NewConsumer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AllReplacedReport.class);
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
                Intent intent = new Intent(getApplicationContext(), UnreplacedConsumerReport.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });


        uac = new UtilAppCommon();
        name = uac.UniqueCode(getApplicationContext());


        btn_UnbuildConsumerSync.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {


                dbHelper = new DB(ReplacementReportsActivity.this);
                SD = dbHelper.getWritableDatabase();

                String strPref = "SELECT DISTINCT LOC_CD,Cycle,Bill_Mon FROM TBL_CONSMAST";

                try {
                    Cursor getPref = SD.rawQuery(strPref, null);
                    if (getPref.moveToFirst()) {

                        do {
                            if (isNetworkAvailable(getApplicationContext())) {

                                new DownloadAsyncFileFromURL(getApplicationContext(), "Billed", getPref.getString(0), getPref.getString(1), getPref.getString(2), name).execute();

                            } else {

                                Toast.makeText(getApplicationContext(), "Internet Connection Required", Toast.LENGTH_SHORT).show();

                            }

                        } while (getPref.moveToNext());//Move the cursor to the next row.
                    } else {
                        Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
                }

            }
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
