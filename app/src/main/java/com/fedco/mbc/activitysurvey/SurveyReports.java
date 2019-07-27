package com.fedco.mbc.activitysurvey;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Debug;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.GPSTracker;
import com.fedco.mbc.utils.UtilAppCommon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SurveyReports extends Activity {

    HashMap<String, String> hmconsmasData;
    static final ArrayList<HashMap<String, String>> allist = new ArrayList<HashMap<String, String>>();
    final Context context = this;
    UtilAppCommon uac;
    private Boolean exit = false;
    Button btnExit, btnBack, btn_11kVFeederList, btn_DTR_List, btn_Pole_List, btn_Consumer_List;
    String accountnum;
    Logger Log;
    SQLiteDatabase SD;
    DB dbHelper;
    ArrayList<String> li, list_DailySummary, list_CummulativeSummary;
    GridView gv_DailySummaryHeader, gv_DailySummary,
            gv_CummulativeHeader, gv_CummulativeSummary;
    static final String[] dailySummaryHeaders = new String[]{
            "Date", "11kV Feeder", "11kV Feeder Pending", "DTR", "DTR Pending", "Pole", "Pole Pending", "Consumer", "Consumer Pending"
    };

    Button dHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_reports);

        /****BUTTON  INITIALISATION****/

        btn_11kVFeederList = (Button) findViewById(R.id.btn_11kv_feederlist);
        btn_DTR_List = (Button) findViewById(R.id.btn_dtr_list);
        btn_Pole_List = (Button) findViewById(R.id.btn_pole_list);
        btn_Consumer_List = (Button) findViewById(R.id.btn_consumer_list);
        dHomeButton = (Button) findViewById(R.id.Buttonback);
        dHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                overridePendingTransition(R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right);

            }
        });
        /****BUTTON  ACTION   EVENTS****/

        btn_11kVFeederList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Report_11kV_FeederList.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });
        btn_DTR_List.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Report_DTR.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });
        btn_Pole_List.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Report_Pole.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });
        btn_Consumer_List.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Report_Consumer.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });

        GPSTracker gps3 = new GPSTracker(SurveyReports.this);
        final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date d = new Date(gps3.getTime());
        String sDate = sdf.format(d);

        gv_DailySummaryHeader = (GridView) findViewById(R.id.gridView);
        ArrayAdapter<String> headerAdapter = new ArrayAdapter<String>
                (this, R.layout.grid_view_header, dailySummaryHeaders);
        gv_DailySummaryHeader.setAdapter(headerAdapter);

        dbHelper = new DB(getApplicationContext());
        SD = dbHelper.getWritableDatabase();
        gv_DailySummary = (GridView) findViewById(R.id.gridView1);
        list_DailySummary = new ArrayList<String>();

        String dailySummary = "SELECT (SELECT count(*) FROM TBL_11KVFEEDER_UPLOAD WHERE date(REPORT_DATE)<=date('now')) AS feeder_count,(SELECT count(*) FROM TBL_11KVFEEDER_UPLOAD WHERE FLAG_UPLOAD='N'  and date(REPORT_DATE)<=date('now')) AS feeder_unupload,(SELECT count(*) FROM TBL_DTR_UPLOAD WHERE date(REPORT_DATE)<=date('now')) AS dt_count,(SELECT count(*) FROM TBL_DTR_UPLOAD WHERE FLAG_UPLOAD='N' and date(REPORT_DATE)<=date('now')) AS dt_unupload,(SELECT count(*) FROM TBL_POLE_UPLOAD WHERE date(REPORT_DATE)<=date('now')) AS pole_count,(SELECT count(*) FROM TBL_POLE_UPLOAD WHERE  FLAG_UPLOAD='N' and date(REPORT_DATE)<=date('now')) AS pole_unupload,(SELECT count(*) FROM TBL_CONSUMERSURVEY_UPOLOAD WHERE date(REPORT_DATE)<=date('now')) AS consumer_count,(SELECT count(*) FROM TBL_CONSUMERSURVEY_UPOLOAD WHERE  FLAG_UPLOAD='N' and date(REPORT_DATE)<=date('now')) AS Con_unupload";
        Cursor todoCursor = SD.rawQuery(dailySummary, null);
        System.out.println("Select Result : " + dailySummary);

        if (todoCursor != null && todoCursor.moveToFirst()) {
            do {

                list_DailySummary.add(sDate);
                list_DailySummary.add(todoCursor.getString(0));
                list_DailySummary.add(todoCursor.getString(1));
                list_DailySummary.add(todoCursor.getString(2));
                list_DailySummary.add(todoCursor.getString(3));
                list_DailySummary.add(todoCursor.getString(4));
                list_DailySummary.add(todoCursor.getString(5));
                list_DailySummary.add(todoCursor.getString(6));
                list_DailySummary.add(todoCursor.getString(7));

            }
            while (todoCursor.moveToNext());

            ArrayAdapter<String> ar_DailySummary = new ArrayAdapter<String>
                    (getApplicationContext(), R.layout.grid_view, list_DailySummary);

            gv_DailySummary.setAdapter(ar_DailySummary);

        } else {

            Toast.makeText(SurveyReports.this, "No Data Found", Toast.LENGTH_SHORT).show();
        }

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

        finish(); // finish activity
        overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Debug.stopMethodTracing();
        super.onDestroy();
    }

}
