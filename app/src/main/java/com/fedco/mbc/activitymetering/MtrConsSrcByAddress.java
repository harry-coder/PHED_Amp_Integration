package com.fedco.mbc.activitymetering;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.UtilAppCommon;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Created by nitinb on 15-02-2016.
 */
public class MtrConsSrcByAddress extends Activity {


    Button btn_search;
    EditText edt_cons_address;
    String str_cons_address;

    String consumer_address_next;

    String cnumber, name, meter, division, address, cycle, route, subdivision, section,oldconno,meterdigit,mf;


    private GridView gridView;
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;
    //DatabaseHandler handler;

    SQLiteDatabase SD;
    DB dbHelper;
    UtilAppCommon uac;
    Logger Log;
    static final String[] letters = new String[]{

            "CONSUMERNO", "NAME", "METERNO", "OLDCONSUMERNO","METERDIGIT","ADDRESS", "CYCLE", "ROUTE", "DIVISION", "SUBDIVISION", "SECTION","MF"};

    GridView grid;
    Button btn_bck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metersearchbyaddress);

        //GridView
       /* gridView=(GridView) findViewById(R.id.gridView1);*/

        edt_cons_address = (EditText) findViewById(R.id.edtTxtConName);

        grid = (GridView) findViewById(R.id.gridView);

        ArrayAdapter adapter1 = new ArrayAdapter(this, R.layout.grid_text_custom, letters);
        grid.setAdapter(adapter1);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Toast.makeText(getApplicationContext(), ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
            }
        });


        dbHelper = new DB(getApplicationContext());
        SD = dbHelper.getWritableDatabase();

        btn_bck =(Button)findViewById(R.id.Buttonback);
        btn_bck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // finish activity
                overridePendingTransition(R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right);
            }
        });


        btn_search = (Button) findViewById(R.id.btnSearchConsName);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str_cons_address = edt_cons_address.getText().toString().trim();

                String ret = "select * from TBL_METERMASTER where ADDRESS like '%" + str_cons_address + "%'";
                System.out.println("query---" + ret);
                Cursor curSearchdata = SD.rawQuery(ret, null);

                if (curSearchdata != null && curSearchdata.moveToFirst()) {

                    uac = new UtilAppCommon();
                    try {
                        uac.copyResultToMeterMaster(curSearchdata);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                 /*   t_number.setText(Structconsmas.Consumer_Number);
                    t_name.setText(Structconsmas.Name);
*/
                    //ArrayList
                    list = new ArrayList<String>();
                    gridView = (GridView) findViewById(R.id.gridView1);
                    adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.grid_view, list);
                    gridView.setAdapter(adapter);

                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {

                            Toast.makeText(getApplicationContext(), list.get(position).toString(), Toast.LENGTH_SHORT).show();

                          /*  Intent intent=new Intent(getApplicationContext(),CameraActivity.class);
//                                        intent.putExtra("datalist", allist);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);*/
                            consumer_address_next = list.get(position).toString();
                            String ret1 = "select * from TBL_METERMASTER where ADDRESS = '" + consumer_address_next + "'";
                            //String ret1="SELECT * FROM TBL_CONSMAST WHERE Name ="+consumer_name_next;
                            //String ret = "SELECT Consumer_Number FROM TBL_CONSMAST WHERE Name='SAPHALA PARIDA'";
                            Cursor curconsmasData1 = SD.rawQuery(ret1, null);

                            /****GETTING DATA FROM CURSOR WHEN CURSOR IS NOT NULL AND ON THE FORST INDEX****/
                            if (curconsmasData1 != null && curconsmasData1.moveToFirst()) {

                                uac = new UtilAppCommon();
                                try {
                                    uac.copyResultToMeterMaster(curconsmasData1);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                /****INITIALISATION OF INTENT AND ADDING SENDING DATA TO NEXT SCREEN , STARING INTENT****/

                                Intent intent = new Intent(getApplicationContext(), CameraMeter.class);
//                                        intent.putExtra("datalist", allist);
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left);
                                //String temp_address = c.getString(3);
                                //System.out.println(temp_address+" result of select Query");
                            }


                        }
                    });


                    dbHelper = new DB(getApplicationContext());
                    SD = dbHelper.getWritableDatabase();


                    try {



                        if (curSearchdata != null && curSearchdata.moveToFirst()) {

                            do {

                                cnumber = curSearchdata.getString(0);
                                name = curSearchdata.getString(4);
                                meter = curSearchdata.getString(3);
                                oldconno=curSearchdata.getString(1);
                                meterdigit=curSearchdata.getString(50);
                                address = curSearchdata.getString(5);
                                cycle = curSearchdata.getString(6);
                                route = curSearchdata.getString(7);
                                division = curSearchdata.getString(8);
                                subdivision = curSearchdata.getString(9);
                                section = curSearchdata.getString(10);
                                mf=curSearchdata.getString(45);

                                //add in to array list
                                list.add(cnumber);
                                list.add(name);
                                list.add(meter);
                                list.add(oldconno);
                                list.add(meterdigit);
                                list.add(address);
                                list.add(cycle);
                                list.add(route);
                                list.add(division);
                                list.add(subdivision);
                                list.add(section);
                                list.add(mf);
                                //gridView.setAdapter(adapter);
                            } while (curSearchdata.moveToNext());//Move the cursor to the next row.
                        } else {
                            Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        //handler.close();
        //Toast.makeText(getBaseContext(),"Name: "+name+"Roll No: "+roll+"Course: "+course , Toast.LENGTH_LONG).show();
    }
    public void onBackPressed() {
//        if (exit) {
        finish(); // finish activity
        this.overridePendingTransition(R.anim.anim_slide_in_right,
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
