package com.fedco.mbc.activitymetering;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.PrintSelection;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.UtilAppCommon;

import java.sql.SQLException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class NSCReport extends Activity {

    String cnumber, name, meter, division, address, cycle, route, subdivision, section,oldconno,meterdigit,mf;
    String consumer_name_next;
    TextView c_count, t_count;
    Integer totalconsumer;

    private GridView gridView;
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;
    //DatabaseHandler handler;
    SQLiteDatabase SD;
    DB dbHelper,databaseHelper;
    UtilAppCommon uac;
    Logger Log;

    Spinner sec_spin, cycle_spin;
    ArrayList<String> div_list, sdiv_list, sec_list, cycle_list;
    ArrayAdapter<String> div_adapter, sdiv_adapter, sec_adapter, cycle_adapter;
    Cursor div_cursor, sdiv_cursor, sec_cursor, cycle_cursor;

    static final String[] letters = new String[]{

            "CONSUMERNO", "NAME", "METERNO", "OLDCONSUMERNO","METERDIGIT","ADDRESS", "CYCLE", "ROUTE", "DIVISION", "SUBDIVISION", "SECTION","MF"};

    GridView grid;
    Button btn_bck ,btn_filter;
    String divisionpre,subdivisionpre,sectionpre,cyclepre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meternscreport);


        gridView = (GridView) findViewById(R.id.gridView1);
        c_count = (TextView) findViewById(R.id.text5);
        t_count = (TextView) findViewById(R.id.text4);
        btn_bck =(Button)findViewById(R.id.Buttonback);
        sec_spin = (Spinner) findViewById(R.id.section);
        cycle_spin = (Spinner) findViewById(R.id.cycle);
        btn_filter = (Button) findViewById(R.id.btn_filter);

        dbHelper = new DB(getApplicationContext());
        SD = dbHelper.getWritableDatabase();

        divisionpre = getIntent().getStringExtra("division");
        subdivisionpre = getIntent().getStringExtra("subdivsion");
//        sectionpre = getIntent().getStringExtra("section");
//        cyclepre = getIntent().getStringExtra("cycle");

        // for showing header value
        grid = (GridView) findViewById(R.id.gridView);

        ArrayAdapter adapter1 = new ArrayAdapter(this, R.layout.grid_text_custom, letters);
        grid.setAdapter(adapter1);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Toast.makeText(getApplicationContext(), ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
                list.clear();

                listSort(SD,((TextView) v).getText());
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
                String ret1 = "select * from TBL_METERMASTER where NAME = '" + consumer_name_next + "'";

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
        btn_bck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // finish activity
                overridePendingTransition(R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right);
            }
        });
        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sectionpre= sec_spin.getSelectedItem().toString();
                cyclepre= cycle_spin.getSelectedItem().toString();

                if(divisionpre.length() !=0 && subdivisionpre.length() !=0 && sectionpre.length() ==14 && cyclepre.length() == 12)
                {
                    String superQwery="SELECT CONSUMERNO from TBL_METERMASTER WHERE DIVISION='"+divisionpre+"' and SUBDIVISION='"+subdivisionpre+"'";
                    String childQwery="SELECT * FROM TBL_METERMASTER WHERE NSC ='N' AND DIVISION='"+divisionpre+"' and SUBDIVISION='"+subdivisionpre+"'";
                    filterSearch(SD,superQwery,childQwery);
                }
                else if(divisionpre.length()!=0 && subdivisionpre.length()!=0 && sectionpre.length()!=14 && cyclepre.length() == 12)
                {
                    String superQwery="SELECT CONSUMERNO from TBL_METERMASTER WHERE DIVISION='"+divisionpre+"' and SUBDIVISION='"+subdivisionpre+"' and SECTION='"+sectionpre+"'";
                    String childQwery="SELECT * FROM TBL_METERMASTER WHERE NSC ='N' AND DIVISION='"+divisionpre+"' and SUBDIVISION='"+subdivisionpre+"' and SECTION='"+sectionpre+"'";
                    filterSearch(SD,superQwery,childQwery);
                }
                else if(divisionpre.length()!=0 && subdivisionpre.length()!=0 && sectionpre.length()==14 && cyclepre.length() != 12)
                {
                    String superQwery="SELECT CONSUMERNO from TBL_METERMASTER WHERE DIVISION='"+divisionpre+"' and SUBDIVISION='"+subdivisionpre+"' and SECTION='"+sectionpre+"' and CYCLE='"+cyclepre+"'";
                    String childQwery="SELECT * FROM TBL_METERMASTER WHERE NSC ='N' AND DIVISION='"+divisionpre+"' and SUBDIVISION='"+subdivisionpre+"' and CYCLE='"+cyclepre+"'";
                    filterSearch(SD,superQwery,childQwery);
                }
                else if(divisionpre.length()!=0 && subdivisionpre.length()!=0 && sectionpre.length()!=14 && cyclepre.length() != 12)
                {
                    String superQwery="SELECT CONSUMERNO from TBL_METERMASTER WHERE DIVISION='"+divisionpre+"' and SUBDIVISION='"+subdivisionpre+"' and SECTION='"+sectionpre+"' and CYCLE='"+cyclepre+"'";
                    String childQwery="SELECT * FROM TBL_METERMASTER WHERE NSC ='N' AND DIVISION='"+divisionpre+"' and SUBDIVISION='"+subdivisionpre+"' and SECTION='"+sectionpre+"' and CYCLE='"+cyclepre+"'";
                    section.length();
                    filterSearch(SD,superQwery,childQwery);
                }
                else
                {
                    Toast.makeText(NSCReport.this, "Please Check the filteration ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {

            String total_c = "select CONSUMERNO from TBL_METERMASTER";
            Cursor cursor_total = SD.rawQuery(total_c, null);
            Toast.makeText(getApplicationContext(), "Total Consumer:" + cursor_total.getCount(), Toast.LENGTH_LONG).show();
            t_count.setText("Consumers : " + cursor_total.getCount());
            //for holding retrieve data from query and store in the form of rows
            String ret = "select * from TBL_METERMASTER where NSC ='N' and DIVISION='"+divisionpre+"' and SUBDIVISION='"+subdivisionpre+"' and SECTION='"+sectionpre+"' and CYCLE='"+cyclepre+"'";
            System.out.println("query---" + ret);
            //Cursor curSearchdata = SD.rawQuery(ret, null);
            Cursor curSearchdata = SD.rawQuery(ret, null);

            Toast.makeText(getApplicationContext(), "Total data Found:" + curSearchdata.getCount(), Toast.LENGTH_LONG).show();
            c_count.setText("New Consumers : " + curSearchdata.getCount());


            if (curSearchdata.moveToFirst()) {

                do {
                    cnumber=curSearchdata.getString(0);
                    name=curSearchdata.getString(4);
                    meter=curSearchdata.getString(3);
                    oldconno=curSearchdata.getString(1);
                    meterdigit=curSearchdata.getString(50);
                    address=curSearchdata.getString(5);
                    cycle=curSearchdata.getString(6);
                    route=curSearchdata.getString(7);
                    division=curSearchdata.getString(8);
                    subdivision=curSearchdata.getString(9);
                    section=curSearchdata.getString(10);
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

        sec_list = new ArrayList<String>();
        cycle_list = new ArrayList<String>();

        new Section(NSCReport.this).execute();
        new Cycle(NSCReport.this).execute();
        sec_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                section = sec_list.get(position).toString();

//                  Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_LONG).show();
                if (position > 0) {

//                    cycle_spin.setAdapter(null);
//                    cycle_list.clear();
//
//                    new Cycle(NSCReport.this).execute();

                } else {
//
//                    cycle_spin.setAdapter(null);
//                    cycle_list.clear();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


    }

    public void listSort(SQLiteDatabase SD ,CharSequence Filter){
        try {
            list.clear();

            String strWhereCondition=" ";

            if(sectionpre!=null)
                strWhereCondition = " and SECTION ='"+sectionpre+"' ";
            if(cyclepre!=null)
                strWhereCondition += " and CYCLE ='"+cyclepre+"' ";

            String total_c = "select CONSUMERNO from TBL_METERMASTER WHERE DIVISION='"+divisionpre+"' and SUBDIVISION='"+subdivisionpre+"'"+strWhereCondition+"' ORDER BY "+Filter+" ASC";
            Cursor cursor_total = SD.rawQuery(total_c, null);
            Toast.makeText(getApplicationContext(), "Total Consumer:" + cursor_total.getCount(), Toast.LENGTH_LONG).show();
            t_count.setText("Consumers : " + cursor_total.getCount());
            //for holding retrieve data from query and store in the form of rows
            String ret = "select * from TBL_METERMASTER where NSC ='N' and DIVISION='"+divisionpre+"' and SUBDIVISION='"+subdivisionpre+"'"+strWhereCondition+"' ORDER BY "+Filter+" ASC";
            System.out.println("query---" + ret);
            //Cursor curSearchdata = SD.rawQuery(ret, null);
            Cursor curSearchdata = SD.rawQuery(ret, null);

            Toast.makeText(getApplicationContext(), "Total data Found:" + curSearchdata.getCount(), Toast.LENGTH_LONG).show();
            c_count.setText("New Consumers : " + curSearchdata.getCount());


            if (curSearchdata.moveToFirst()) {

                do {
                    cnumber = curSearchdata.getString(0);
                    name = curSearchdata.getString(4);
                    meter = curSearchdata.getString(3);
                    address = curSearchdata.getString(5);
                    cycle = curSearchdata.getString(6);
                    route = curSearchdata.getString(7);
                    division = curSearchdata.getString(8);
                    subdivision = curSearchdata.getString(9);
                    section = curSearchdata.getString(10);

                    //add in to array list
                    list.add(cnumber);
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

                adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.grid_view, list);
                gridView.setAdapter(adapter);

            } else {
                Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void filterSearch(SQLiteDatabase SD,String superQwer, String childQwer ){
        try {
list.clear();
//            String total_c = "select CONSUMERNO from TBL_METERMASTER WHERE DIVISION='"+divisionpre+"' and SUBDIVISION='"+subdivisionpre+"' and SECTION='"+sectionpre+"' and CYCLE='"+cyclepre+"'";
            Cursor cursor_total = SD.rawQuery(superQwer, null);
            Toast.makeText(getApplicationContext(), "Total Consumer:" + cursor_total.getCount(), Toast.LENGTH_LONG).show();
            t_count.setText("Consumers : " + cursor_total.getCount());
            //for holding retrieve data from query and store in the form of rows
//            String ret = "SELECT * FROM TBL_METERMASTER WHERE CONSUMERNO NOT IN(SELECT CONSUMERNO  FROM TBL_METERUPLOAD) and DIVISION='"+divisionpre+"' and SUBDIVISION='"+subdivisionpre+"' ORDER BY "+Filter+" ASC";
//            String ret = "SELECT * FROM TBL_METERMASTER WHERE NSC ='N' and DIVISION='"+divisionpre+"' and SUBDIVISION='"+subdivisionpre+"' and SECTION='"+sectionpre+"' and CYCLE='"+cyclepre+"'";
//            System.out.println("query---" + ret);
            //Cursor curSearchdata = SD.rawQuery(ret, null);
            Cursor curSearchdata = SD.rawQuery(childQwer, null);

            Toast.makeText(getApplicationContext(), "Total data Found:" + curSearchdata.getCount(), Toast.LENGTH_LONG).show();
            c_count.setText("New Consumers : " + curSearchdata.getCount());


            if (curSearchdata.moveToFirst()) {

                do {
                    cnumber=curSearchdata.getString(0);
                    name=curSearchdata.getString(4);
                    meter=curSearchdata.getString(3);
                    oldconno=curSearchdata.getString(1);
                    meterdigit=curSearchdata.getString(50);
                    address=curSearchdata.getString(5);
                    cycle=curSearchdata.getString(6);
                    route=curSearchdata.getString(7);
                    division=curSearchdata.getString(8);
                    subdivision=curSearchdata.getString(9);
                    section=curSearchdata.getString(10);
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

                adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.grid_view, list);
                gridView.setAdapter(adapter);
            } else {
                Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.metering, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:

                finish();
                overridePendingTransition(R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right);

                return true;

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

    //...........Section  Class...........................................//
    public class Section extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        Context _context;

        Section(Context ctx) {
            _context = ctx;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // ShowAlertagain();
            super.onPreExecute();
            pd = new ProgressDialog(NSCReport.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(NSCReport.this);
            SD = databaseHelper.getReadableDatabase();

            try {
                String secString = "select distinct SECTION from TBL_METERMASTER WHERE SUBDIVISION='" + subdivisionpre + "'";
                System.out.println("select distinct SECTION from TBL_METERMASTER WHERE SUBDIVISION='" + subdivisionpre + "'");
                sec_cursor = SD.rawQuery(secString, null);
                if (sec_cursor != null && sec_cursor.moveToFirst()) {
                    sec_list.add("Select Section");
                    do {

                        String section_id = sec_cursor.getString(0);
                        System.out.println("hdhd" + section_id);
                        sec_list.add(section_id);

                    } while (sec_cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            pd.hide();
            pd.dismiss();
            try {
               /* project_adapter=new ArrayAdapter<String>(SiteInspection_Activity2.this,android.R.layout.simple_spinner_item,project_name_list);
                project_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
                sec_adapter = new ArrayAdapter<String>(
                        getApplicationContext(), R.layout.custom_filter, R.id.textViewfilter, sec_list);
                sec_spin.setAdapter(sec_adapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //...........Cycle Class...........................................//
    public class Cycle extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        Context _context;

        Cycle(Context ctx) {
            _context = ctx;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // ShowAlertagain();
            super.onPreExecute();
            pd = new ProgressDialog(NSCReport.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(NSCReport.this);
            SD = databaseHelper.getReadableDatabase();

            try {
                String cycleString = "select distinct CYCLE from TBL_METERMASTER ORDER BY CYCLE ASC";
                System.out.println("select distinct CYCLE from TBL_METERMASTER ORDER BY CYCLE ASC" );
                cycle_cursor = SD.rawQuery(cycleString, null);
                if (cycle_cursor != null && cycle_cursor.moveToFirst()) {
                    cycle_list.add("Select Cycle");
                    do {

                        String cycle_id = cycle_cursor.getString(0);
                        System.out.println("hdhd" + cycle_id);
                        cycle_list.add(cycle_id);

                    } while (cycle_cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            pd.hide();
            pd.dismiss();
            try {
               /* project_adapter=new ArrayAdapter<String>(SiteInspection_Activity2.this,android.R.layout.simple_spinner_item,project_name_list);
                project_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
                cycle_adapter = new ArrayAdapter<String>(
                        getApplicationContext(), R.layout.custom_filter, R.id.textViewfilter, cycle_list);
                cycle_spin.setAdapter(cycle_adapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}  