package com.fedco.mbc.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.bluetoothprinting.GlobalPool;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.UtilAppCommon;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Created by nitinb on 15-02-2016.
 */
public class ConsumerSearchByName_Activity extends Activity implements LogoutListaner{


    Button btn_search;
    EditText edt_cons_name;
    String str_cons_name;

    String consumer_name_next;

    String cnumber, name, meter, division, address, cycle, route,ivrs, subdivision ,section;

    private GridView gridView;
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;

    //DatabaseHandler handler;
    Logger Log;
    SQLiteDatabase SD;
    DB dbHelper;
    UtilAppCommon uac;
    Button btn_bck;
    static final String[] letters = new String[]{

            "Cust No","Old_Cust_No","Name","Meter No","Address", "Book No", "IBC", "BSC"};

    GridView grid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consumersearchbyname_activity);

        //GridView
        /* gridView=(GridView) findViewById(R.id.gridView1);*/

        edt_cons_name = (EditText) findViewById(R.id.edtTxtConName);

        grid = (GridView) findViewById(R.id.gridView);

        ArrayAdapter adapter1 = new ArrayAdapter(this,R.layout.grid_view_header, letters);
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
        ((GlobalPool)getApplication()).registerSessionListaner(this);
        ((GlobalPool)getApplication()).startUserSession();

        btn_search = (Button) findViewById(R.id.btnSearchConsName);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str_cons_name = edt_cons_name.getText().toString().trim();

                String ret = "select * from TBL_CONSMAST where Name like '%" + str_cons_name + "%'";
                System.out.println("query---" + ret);
                Cursor curSearchdata = SD.rawQuery(ret, null);
                System.out.println("query--- return12" + curSearchdata.moveToFirst());// && curconsmasData.moveToFirst()
                System.out.println("query--- return21" + curSearchdata != null);// && curconsmasData.moveToFirst()
                System.out.println("query--- return" + curSearchdata.getCount());

                if (curSearchdata != null && curSearchdata.moveToFirst()) {

                    System.out.println("query--- return" + curSearchdata.getCount());
                    uac = new UtilAppCommon();
                    try {
                        uac.copyResultsetToConmasClass(curSearchdata);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    //ArrayList
                    list = new ArrayList<String>();
                    gridView = (GridView) findViewById(R.id.gridView1);
                    adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.grid_view_billing, list);
                    gridView.setAdapter(adapter);

                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {

                            int posti = (position/8)*8;
                            String ConsNo =list.get(posti);
//                            Toast.makeText(getApplicationContext(), list.get(pos).toString(), Toast.LENGTH_SHORT).show();
//                          Toast.makeText(getApplicationContext(), v.getId(), Toast.LENGTH_SHORT).show();

//                            consumer_name_next = list.get(pos).toString();
//                            String ret1 = "select * from TBL_CONSMAST where Name = '" + consumer_name_next + "'";

                            String ret1 ="SELECT * FROM TBL_CONSMAST WHERE Consumer_Number ='"+ ConsNo + "'";
                            //String ret1="SELECT * FROM TBL_CONSMAST WHERE Name ="+consumer_name_next;
                            //String ret = "SELECT Consumer_Number FROM TBL_CONSMAST WHERE Name='SAPHALA PARIDA'";
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
//                                        intent.putExtra("datalist", allist);
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left);

                            } else {

                                Toast.makeText(getApplicationContext(), "No data found:", Toast.LENGTH_LONG).show();
                            }


                        }
                    });

                    dbHelper = new DB(getApplicationContext());
                    SD = dbHelper.getWritableDatabase();


                    try {

                        //Move the cursor to the first row.
                        // if(curSearchdata.moveToFirst())   {

                        if (curSearchdata != null && curSearchdata.moveToFirst()) {

                            do {
                                cnumber     = curSearchdata.getString(0);
                                ivrs        = curSearchdata.getString(103);
                                name        = curSearchdata.getString(2);
                                meter       = curSearchdata.getString(11);
                                address     = curSearchdata.getString(3)+""+curSearchdata.getString(4)+""+curSearchdata.getString(69)+""+curSearchdata.getString(70)+""+curSearchdata.getString(71);
                                cycle       = curSearchdata.getString(5);//group
                                route       = curSearchdata.getString(70);//RDG
                                division    = curSearchdata.getString(10);//DC

                                //add in to array list
                                list.add(cnumber);
                                list.add(ivrs);
                                list.add(name);
                                list.add(meter);
                                list.add(address);
                                list.add(cycle);
                                list.add(route);
                                list.add(division);
                                //gridView.setAdapter(adapter);
                            } while (curSearchdata.moveToNext());//Move the cursor to the next row.
                        }

                    } catch (Exception e) {

                    }
                }
            }
        });

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.home:
                overridePendingTransition(R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right);
                finish();

                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);

    }
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        ((GlobalPool)getApplication()).onUserInteraction();
    }

    @Override
    public void userLogoutListaner() {
        finish();
        Intent intent=new Intent(ConsumerSearchByName_Activity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

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

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Debug.stopMethodTracing();
        super.onDestroy();
    }
}