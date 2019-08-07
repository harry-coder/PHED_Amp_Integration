package com.fedco.mbc.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fedco.mbc.R;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.UtilAppCommon;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by abhisheka on 02-08-2016.
 */

public class UnCollectedConsumerReportActivity extends AppCompatActivity {
    String cnumber, name, meter, division, address, cycle, route, subdivision,section;
    String consumer_name_next;
    TextView c_count, t_count;
    Logger Log;
    private GridView gridView_header;
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;

    SQLiteDatabase SD;
    DB dbHelper;
    UtilAppCommon uac;
    static final String[] letters = new String[]{

            "Cons No","OLD Cons No","Name","IBC","BSC"};

    GridView grid;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uncollected_consumer_report);

        try {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
            getSupportActionBar().setLogo(R.mipmap.logo);
            setTitle("SMARTPHED");
        } catch (NullPointerException npe) {
            Log.e(getApplicationContext(), "Billing Act", "ActionBar Throwing null Pointer", npe);
        }
        //GridView
        gridView_header = (GridView) findViewById(R.id.gridView_header);
        c_count = (TextView) findViewById(R.id.text1);
        t_count = (TextView) findViewById(R.id.text2);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, letters);
        gridView_header.setAdapter(adapter);

        /*list = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.grid_view, list);
        gridView.setAdapter(adapter);*/

        dbHelper = new DB(getApplicationContext());
        SD = dbHelper.getWritableDatabase();


        list = new ArrayList<String>();
        grid = (GridView) findViewById(R.id.gridView2);
        ArrayAdapter adapter1 = new ArrayAdapter(getApplicationContext(), R.layout.grid_view, list);
        grid.setAdapter(adapter1);

//        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView parent, View v, int position, long id) {
//                Toast.makeText(getApplicationContext(), ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
//            }
//        });

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {

                Toast.makeText(getApplicationContext(), list.get(position).toString(), Toast.LENGTH_SHORT).show();

                consumer_name_next = list.get(position).toString();
                String ret1 = "select * from TBL_COLLECTION where CON_NO = '" + consumer_name_next + "'";
                //String ret1="SELECT * FROM TBL_CONSMAST WHERE Name ="+consumer_name_next;
                //String ret = "SELECT Consumer_Number FROM TBL_CONSMAST WHERE Name='SAPHALA PARIDA'";
                Cursor curconsmasData1 = SD.rawQuery(ret1, null);

                /****GETTING DATA FROM CURSOR WHEN CURSOR IS NOT NULL AND ON THE FORST INDEX****/
                if (curconsmasData1 != null && curconsmasData1.moveToFirst()) {

                    uac = new UtilAppCommon();
                    try {
                        uac.copyResultsetToCollClass(curconsmasData1);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    /****INITIALISATION OF INTENT AND ADDING SENDING DATA TO NEXT SCREEN , STARING INTENT****/

                    Intent intent = new Intent(getApplicationContext(), CollectionView.class);
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

            String total_c = "select CON_NO from TBL_COLLECTION";
            Cursor cursor_total = SD.rawQuery(total_c, null);
            Toast.makeText(getApplicationContext(), "Total Consumer:" + cursor_total.getCount(), Toast.LENGTH_LONG).show();
            t_count.setText("Total Consumer is: " + cursor_total.getCount());


            //for holding retrieve data from query and store in the form of rows
            String ret = "SELECT * FROM TBL_COLLECTION WHERE CON_NO NOT IN(SELECT CON_NO  FROM TBL_COLMASTER_MP)";
            System.out.println("query---" + ret);
            Cursor curSearchdata = SD.rawQuery(ret, null);
            Toast.makeText(getApplicationContext(), "Total data Found:" + curSearchdata.getCount(), Toast.LENGTH_LONG).show();
            c_count.setText("UnCollected Consumer is: " + curSearchdata.getCount());
            if (curSearchdata.moveToFirst()) {


                do {
                    cnumber = curSearchdata.getString(3);
                    list.add(cnumber);

                    subdivision = curSearchdata.getString(17);//IVRS
                    list.add(subdivision);

                    name = curSearchdata.getString(4);
                    list.add(name);

                    division = curSearchdata.getString(0);
                    list.add(division);

                    section = curSearchdata.getString(2);
                    list.add(section);

                } while (curSearchdata.moveToNext());//Move the cursor to the next row.
            } else {
                Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void onBackPressed() {

        finish(); // finish activity
        overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);
    }

}
