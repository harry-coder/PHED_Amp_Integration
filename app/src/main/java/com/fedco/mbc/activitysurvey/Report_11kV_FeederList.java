package com.fedco.mbc.activitysurvey;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.CameraActivity;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.UtilAppCommon;

import java.sql.SQLException;
import java.util.ArrayList;

public class Report_11kV_FeederList extends Activity {

    String division, feederName_11kV, feederName_33kV, meterNumber, meterComPort, meterMake;
    public static final String TAG = Report_11kV_FeederList.class.getSimpleName();
//    String consumer_name_next;
//    TextView c_count, t_count;
//    Integer totalconsumer;

    private GridView gridView;
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;
    //DatabaseHandler handler;
    SQLiteDatabase SD;
    DB dbHelper;
    UtilAppCommon uac;
    Logger Log;
    static final String[] letters = new String[]{

            "Division", "11kV Feeder Name", "33kV SubStation Name", "Meter Com Port", "Meter Make"};

    GridView grid;
    Button btn_bck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_11kv_feeder);

        gridView = (GridView) findViewById(R.id.gridView1);

        btn_bck =(Button)findViewById(R.id.Buttonback);
        btn_bck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // finish activity
                overridePendingTransition(R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right);
            }
        });
        dbHelper = new DB(getApplicationContext());
        SD = dbHelper.getWritableDatabase();


        // for showing header value
        grid = (GridView) findViewById(R.id.gridView);

        ArrayAdapter adapter1 = new ArrayAdapter(this,  R.layout.grid_view_header, letters);
        grid.setAdapter(adapter1);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Toast.makeText(getApplicationContext(), ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
            }
        });


        //ArrayList
        list = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.grid_view, list);
        gridView.setAdapter(adapter);


        try {

//            String total_c = "select Consumer_Number from TBL_CONSMAST";
//            Cursor cursor_total = SD.rawQuery(total_c, null);
//            Toast.makeText(getApplicationContext(), "Total Consumer:" + cursor_total.getCount(), Toast.LENGTH_LONG).show();
//            t_count.setText("Total Consumer is: " + cursor_total.getCount());
              //for holding retrieve data from query and store in the form of rows
//            String ret = "select * from TBL_CONSMAST where New_Consumer_Flag ='Y'";
//            String query = "select t1.DIV_NAME,t2.FEEDER_NAME,t2.A33KVSUBSTATION_CODE,t2.METERNUMBER,t2.MET_COM_PORT,t2.MET_MAKE from TBL_DIVISION_MASTER t1,TBL_11KVFEEDER_UPLOAD t2 , TBL_33KVFEEDER_MASTER t3 where t1.DIVISION_CODE = t2.DIVISION_CODE and t2.A33KVFEEDER_CODE = t3.FEEDER_CODE";
//            String query = "select  t2.DIV_NAME,t1.FEEDER_NAME ,t1.A33KVSUBSTATION_CODE,t1.MET_COM_PORT,t1.MET_MAKE  from TBL_11KVFEEDER_UPLOAD t1,TBL_DIVISION_MASTER t2 where t1.DIVISION_CODE=t2.DIVISION_CODE";
            String query = "select  t2.DIV_NAME,t1.FEEDER_NAME ,t1.A33KVSUBSTATION_CODE,t1.MET_COM_PORT,t1.MET_MAKE  from TBL_11KVFEEDER_UPLOAD t1\n" +
                    "left join TBL_DIVISION_MASTER t2 on t1.DIVISION_CODE=t2.DIVISION_CODE";
            System.out.println("query---" + query);
            //Cursor curSearchdata = SD.rawQuery(ret, null);
            Cursor curQuery = SD.rawQuery(query, null);
            // Cursor c=handler.DisplayData();
            // Move the cursor to the first row.
            // totalconsumer=curSearchdata.getCount();

            Toast.makeText(getApplicationContext(), "Total data Found:" + curQuery.getCount(), Toast.LENGTH_LONG).show();
//            c_count.setText("New Consumer is: " + curSearchdata.getCount());

            if (curQuery.moveToFirst()) {
                do {
                    division = curQuery.getString(0);
                    feederName_11kV = curQuery.getString(1);
                    feederName_33kV = curQuery.getString(2);
                    // meterNumber = curQuery.getString(3);
                    meterComPort = curQuery.getString(3);
                    meterMake = curQuery.getString(4);

                    //add in to array list
                    list.add(division);
                    list.add(feederName_11kV);
                    list.add(feederName_33kV);
                    // list.add(meterNumber);
                    list.add(meterComPort);
                    list.add(meterMake);
                    //gridView.setAdapter(adapter);
                } while (curQuery.moveToNext());//Move the cursor to the next row.
            } else {
                Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
        }

      /* //------------- this part is used for count consumer---------------//

       String retcount = "select * from TBL_CONSMAST where Name = '" + consumer_name_next + "'";

       Cursor cursorcount = SD.rawQuery(retcount, null);

       *//****GETTING DATA FROM CURSOR WHEN CURSOR IS NOT NULL AND ON THE FORST INDEX****//*
       if (cursorcount != null && cursorcount.moveToFirst()) {

           uac = new UtilAppCommon();
           try {
               uac.copyResultsetToConmasClass(cursorcount);
           } catch (SQLException e) {
               e.printStackTrace();
           }
           *//****INITIALISATION OF INTENT AND ADDING SENDING DATA TO NEXT SCREEN , STARING INTENT****//*

           Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
//                                        intent.putExtra("datalist", allist);
           startActivity(intent);
           overridePendingTransition(R.anim.anim_slide_in_left,
                   R.anim.anim_slide_out_left);
           //String temp_address = c.getString(3);
           //System.out.println(temp_address+" result of select Query");
       }
*/
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
}  