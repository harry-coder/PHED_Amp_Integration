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

public class Report_Pole extends Activity {

    String division, dtrName, feederName, poleNo, prepoleNo;
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
            "DIVISION","FEEDER","DTR","POLE_CODE","PRE_POLE_NO" };

    GridView grid;
    Button btn_bck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_pole);


        gridView = (GridView) findViewById(R.id.gridView1);
//        c_count = (TextView) findViewById(R.id.text5);
//        t_count = (TextView) findViewById(R.id.text4);
        btn_bck = (Button) findViewById(R.id.Buttonback);
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

        ArrayAdapter adapter1 = new ArrayAdapter(this, R.layout.grid_view_header, letters);
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

//            String query = "select POLE_CODE,PRE_POLE_NO,FEEDER_CODE from TBL_POLE_UPLOAD where FLAG_UPLOAD ='Y'";
            String query = "select distinct t1.DIV_NAME, t2.FEEDER_NAME, t3.DTR_NAME,t4.POLE_CODE, t4.PRE_POLE_NO\n" +
                    "            from TBL_DIVISION_MASTER t1, TBL_11KVFEEDER_UPLOAD t2, TBL_DTR_UPLOAD t3, TBL_POLE_UPLOAD t4\n" +
                    "            where t1.DIVISION_CODE = t4.DIV_CODE and t2.FEEDER_CODE = t4.FEEDER_CODE and t3.DTR_CODE = t4.DT_CODE";
            System.out.println("query---" + query);
            //Cursor curQueryData = SD.rawQuery(ret, null);
            Cursor curQueryData = SD.rawQuery(query, null);

            Toast.makeText(getApplicationContext(), "Total data Found:" + curQueryData.getCount(), Toast.LENGTH_LONG).show();

            if (curQueryData.moveToFirst()) {
                do {

                    division = curQueryData.getString(0);
                    feederName = curQueryData.getString(1);
                    dtrName = curQueryData.getString(2);
                    poleNo= curQueryData.getString(3);
                    prepoleNo = curQueryData.getString(4);


                    //add in to array list
                    list.add(division);
                    list.add(feederName);
                    list.add(dtrName);
                    list.add(poleNo);
                    list.add(prepoleNo);

                    //gridView.setAdapter(adapter);
                } while (curQueryData.moveToNext());//Move the cursor to the next row.
            } else {
                Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
        }




    }

    public void onBackPressed() {
//        if (exit) {
        finish(); // finish activity
        overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);

    }
}  