package com.fedco.mbc.activitymetering;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.sqlite.DB;

import java.util.ArrayList;

public class SummeryFilter extends Activity {

    Spinner div_spin, subdiv_spin, sec_spin, cycle_spin;
    ArrayList<String> div_list, sdiv_list, sec_list, cycle_list;
    ArrayAdapter<String> div_adapter, sdiv_adapter, sec_adapter, cycle_adapter;
    Cursor div_cursor, sdiv_cursor, sec_cursor, cycle_cursor;
    String division, subdivsion, section, cycle;
    Button btn_submit;
    String filterFlag;

    SQLiteDatabase SD;
    DB databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summery_filter);

        div_spin = (Spinner) findViewById(R.id.division);
        subdiv_spin = (Spinner) findViewById(R.id.subdivision);
        sec_spin = (Spinner) findViewById(R.id.section);
        cycle_spin = (Spinner) findViewById(R.id.cycle);

        div_list = new ArrayList<String>();
        sdiv_list = new ArrayList<String>();
        sec_list = new ArrayList<String>();
        cycle_list = new ArrayList<String>();

        filterFlag = getIntent().getExtras().getString("Filter");

        new Division(SummeryFilter.this).execute();

        div_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                division = div_list.get(position).toString();

//                  Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_LONG).show();
                if (position > 0) {

                    new Subdivision(SummeryFilter.this).execute();

                } else {

                    subdiv_spin.setAdapter(null);
                    sdiv_list.clear();

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        subdiv_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                subdivsion = sdiv_list.get(position).toString();

//                  Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_LONG).show();
                if (position > 0) {

                    sec_spin.setAdapter(null);
                    sec_list.clear();

                    new Section(SummeryFilter.this).execute();

                } else {

                    sec_spin.setAdapter(null);
                    sec_list.clear();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        sec_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                section = sec_list.get(position).toString();

//                  Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_LONG).show();
                if (position > 0) {

                    cycle_spin.setAdapter(null);
                    cycle_list.clear();

                    new Cycle(SummeryFilter.this).execute();

                } else {

                    cycle_spin.setAdapter(null);
                    cycle_list.clear();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

//        cycle_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view,
//                                       int position, long id) {
//                // TODO Auto-generated method stub
//
//
////                  Toast.makeText(getApplicationContext(),""+position,Toast.LENGTH_LONG).show();
//                if (position > 0) {
//
//                    cycle_spin.setAdapter(null);
//                    cycle_list.clear();
//
//                    new Cycle(SummeryFilter.this).execute();
//
//                } else {
//
//                    cycle_spin.setAdapter(null);
//                    cycle_list.clear();
//
//                }
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // TODO Auto-generated method stub
//            }
//        });

        btn_submit = (Button) findViewById(R.id.btn_sumSubmit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                cycle = cycle_spin.getSelectedItem().toString();

                if (division.length()==20) {

                    Toast.makeText(SummeryFilter.this, "Please select Divison", Toast.LENGTH_SHORT).show();

                } else if (subdivsion.length()==24) {

                    Toast.makeText(SummeryFilter.this, "Please select Sub-Division", Toast.LENGTH_SHORT).show();

                }
                else {

                    if(filterFlag.matches("Metering")){

                        Intent intent = new Intent(getApplicationContext(), Report.class);
                        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("division", division);
                        intent.putExtra("subdivsion", subdivsion);
                        intent.putExtra("section", section);
                        intent.putExtra("cycle", cycle);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left);

                    }else{

                        Intent intent = new Intent(getApplicationContext(), MeterSummery.class);
                        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("division", division);
                        intent.putExtra("subdivsion", subdivsion);
                        intent.putExtra("section", section);
                        intent.putExtra("cycle", cycle);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left);

                    }


                }

            }
        });
    }

    //...........Division Class...........................................//
    public class Division extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        Context _context;

        Division(Context ctx) {
            _context = ctx;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // ShowAlertagain();
            super.onPreExecute();
            pd = new ProgressDialog(SummeryFilter.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(SummeryFilter.this);
            SD = databaseHelper.getReadableDatabase();

            try {
                String divString = "select distinct DIVISION from TBL_METERMASTER";
                div_cursor = SD.rawQuery(divString, null);
                if (div_cursor != null && div_cursor.moveToFirst()) {
                    div_list.add("Select Division Name");
                    do {

                        String division_id = div_cursor.getString(0);
                        div_list.add(division_id);

                    } while (div_cursor.moveToNext());
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
                div_adapter = new ArrayAdapter<String>(
                        getApplicationContext(), R.layout.custom_spinner, R.id.textView1, div_list);
                div_spin.setAdapter(div_adapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //...........Sub-Division Class...........................................//
    public class Subdivision extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        Context _context;

        Subdivision(Context ctx) {
            _context = ctx;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // ShowAlertagain();
            super.onPreExecute();
            pd = new ProgressDialog(SummeryFilter.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(SummeryFilter.this);
            SD = databaseHelper.getReadableDatabase();

            try {
                String sdivString = "select distinct SUBDIVISION from TBL_METERMASTER WHERE DIVISION='" + division + "'";
                System.out.println("select distinct SUBDIVISION from TBL_METERMASTER WHERE DIVISION='" + division + "'");
                sdiv_cursor = SD.rawQuery(sdivString, null);
                if (sdiv_cursor != null && sdiv_cursor.moveToFirst()) {
                    sdiv_list.add("Select Sub-Division Name");
                    do {

                        String subdivision_id = sdiv_cursor.getString(0);
                        System.out.println("hdhd" + subdivision_id);
                        sdiv_list.add(subdivision_id);

                    } while (sdiv_cursor.moveToNext());
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
                sdiv_adapter = new ArrayAdapter<String>(
                        getApplicationContext(), R.layout.custom_spinner, R.id.textView1, sdiv_list);
                subdiv_spin.setAdapter(sdiv_adapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            pd = new ProgressDialog(SummeryFilter.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(SummeryFilter.this);
            SD = databaseHelper.getReadableDatabase();

            try {
                String secString = "select distinct SECTION from TBL_METERMASTER WHERE SUBDIVISION='" + subdivsion + "'";
                System.out.println("select distinct SECTION from TBL_METERMASTER WHERE SUBDIVISION='" + subdivsion + "'");
                sec_cursor = SD.rawQuery(secString, null);
                if (sec_cursor != null && sec_cursor.moveToFirst()) {
                    sec_list.add("Select Section Name");
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
                        getApplicationContext(), R.layout.custom_spinner, R.id.textView1, sec_list);
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
            pd = new ProgressDialog(SummeryFilter.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB(SummeryFilter.this);
            SD = databaseHelper.getReadableDatabase();

            try {
                String cycleString = "select distinct CYCLE from TBL_METERMASTER WHERE SECTION='" + section + "'";
                System.out.println("select distinct CYCLE from TBL_METERMASTER WHERE SECTION='" + section + "'");
                cycle_cursor = SD.rawQuery(cycleString, null);
                if (cycle_cursor != null && cycle_cursor.moveToFirst()) {
                    cycle_list.add("Select Cycle Name");
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
                        getApplicationContext(), R.layout.custom_spinner, R.id.textView1, cycle_list);
                cycle_spin.setAdapter(cycle_adapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
