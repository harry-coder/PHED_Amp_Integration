package com.fedco.mbc.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.UtilAppCommon;

import java.sql.SQLException;
import java.util.ArrayList;

public class NewConsumerReportActivity extends Activity {

    String cnumber, name, meter, division, address, cycle, route, subdivision,section,ivrs;
    String consumer_name_next;
    TextView c_count, t_count;
    Integer totalconsumer;

    private GridView gridView;
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;
    //DatabaseHandler handler;
    SQLiteDatabase SD;
    DB dbHelper;
    UtilAppCommon uac;
    Logger Log;
    static final String[] letters = new String[]{

            "Cust No","Old_Cust No", "Name", "Meter No", "Address", "Book No", "IBC", "BSC"};

    GridView grid;
    Button btn_bck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newconsumerreport);


        gridView = (GridView) findViewById(R.id.gridView1);
        c_count = (TextView) findViewById(R.id.text5);
        t_count = (TextView) findViewById(R.id.text4);
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

        ArrayAdapter adapter1 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, letters);
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
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {

                Toast.makeText(getApplicationContext(), list.get(position).toString(), Toast.LENGTH_SHORT).show();


                consumer_name_next = list.get(position).toString();
                String ret1 = "select * from TBL_CONSMAST where Name = '" + consumer_name_next + "'";

                Cursor curconsmasData1 = SD.rawQuery(ret1, null);

                /****GETTING DATA FROM CURSOR WHEN CURSOR IS NOT NULL AND ON THE FORST INDEX****/
                if (curconsmasData1 != null && curconsmasData1.moveToFirst()) {

                    uac = new UtilAppCommon();
                    try {
                        uac.copyResultsetToConmasClass(curconsmasData1);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    /****INITIALISATION OF INTENT AND ADDING SENDING DATA TO NEXT SCREEN , STARING INTENT****/

                    Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                        intent.putExtra("datalist", allist);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);
                    //String temp_address = c.getString(3);
                    //System.out.println(temp_address+" result of select Query");
                }


            }
        });


        try {

            String total_c = "select Consumer_Number from TBL_CONSMAST";
            Cursor cursor_total = SD.rawQuery(total_c, null);
            Toast.makeText(getApplicationContext(), "Total Consumer:" + cursor_total.getCount(), Toast.LENGTH_LONG).show();
            t_count.setText("Total Consumer is: " + cursor_total.getCount());
            //for holding retrieve data from query and store in the form of rows
            String ret = "select * from TBL_CONSMAST where New_Consumer_Flag ='Y'";
            System.out.println("query---" + ret);
            //Cursor curSearchdata = SD.rawQuery(ret, null);
            Cursor curSearchdata = SD.rawQuery(ret, null);
            // Cursor c=handler.DisplayData();
            //Move the cursor to the first row.
            //totalconsumer=curSearchdata.getCount();

            Toast.makeText(getApplicationContext(), "Total data Found:" + curSearchdata.getCount(), Toast.LENGTH_LONG).show();
            c_count.setText("New Consumer is: " + curSearchdata.getCount());


            if (curSearchdata.moveToFirst()) {
          /*list.add("Consumer No.");
          list.add("Name");
          list.add("Meter No.");
          list.add("Address");
          list.add("Cycle");
          list.add("Route");
          list.add("Division");
          list.add("Subdivision");*/

                do {
                    cnumber = curSearchdata.getString(0);
                    ivrs    = curSearchdata.getString(103);//IVRS
                    name = curSearchdata.getString(2);
                    meter = curSearchdata.getString(11);
                    address = curSearchdata.getString(3);
                    cycle = curSearchdata.getString(5);
                    route = curSearchdata.getString(70);
                    division = curSearchdata.getString(8);
                    subdivision = curSearchdata.getString(9);
                    section = curSearchdata.getString(10);

                    //add in to array list
                    list.add(cnumber);
                    list.add(ivrs);
                    list.add(name);
                    list.add(meter);
                    list.add(address);
                    list.add(cycle);
                    list.add(route);
                    list.add(division);
                    list.add(subdivision);
                    list.add(section);
                    //gridView.setAdapter(adapter);
                } while (curSearchdata.moveToNext());//Move the cursor to the next row.
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