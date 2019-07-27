package com.fedco.mbc.activity;

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
import com.fedco.mbc.bluetoothprinting.GlobalPool;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.UtilAppCommon;

import java.sql.SQLException;
import java.util.ArrayList;

public class SearchCustByBookId extends Activity implements LogoutListaner {

    Button btn_search;
    EditText edt_cons_address;
    String str_cons_address;

    String consumer_address_next;

    String cnumber, name, meter, division, address, cycle, route, ivrs, subdivision, section;


    private GridView gridView;
    private ArrayList <String> list;
    private ArrayAdapter <String> adapter;
    //DatabaseHandler handler;

    SQLiteDatabase SD;
    DB dbHelper;
    UtilAppCommon uac;
    Logger Log;
    static final String[] letters = new String[]{

            "Cust No", "OLD_Csut_No", "Name", "Meter No", "Address", "Book No", "IBC", "BSC"};

    GridView grid;
    Button btn_bck;
    TextView tv_Header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_search_cust_by_book_id );

        edt_cons_address = findViewById ( R.id.edtTxtConName );
        tv_Header = findViewById ( R.id.TVHeader );
        edt_cons_address.setHint ( "Enter Book Id" );
        grid = findViewById ( R.id.gridView );
        gridView = findViewById ( R.id.gridView1 );
        tv_Header.setText ( "Search By Book Id" );
        ArrayAdapter adapter1 = new ArrayAdapter ( this, R.layout.grid_view_header, letters );
        grid.setAdapter ( adapter1 );
        grid.setOnItemClickListener ( new AdapterView.OnItemClickListener ( ) {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Toast.makeText ( getApplicationContext ( ), ((TextView) v).getText ( ), Toast.LENGTH_SHORT ).show ( );
            }
        } );


        dbHelper = new DB ( getApplicationContext ( ) );
        SD = dbHelper.getWritableDatabase ( );

        btn_bck = findViewById ( R.id.Buttonback );
        btn_bck.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                finish ( ); // finish activity
                overridePendingTransition ( R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right );
            }
        } );
        ((GlobalPool) getApplication ( )).registerSessionListaner ( this );
        ((GlobalPool) getApplication ( )).startUserSession ( );
        btn_search = findViewById ( R.id.btnSearchConsName );
        btn_search.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {

                str_cons_address = edt_cons_address.getText ( ).toString ( ).trim ( );

//                String ret = "select * from TBL_CONSMAST where Meter_S_No like '%" + str_cons_address + "%'";
                String ret = "select * from TBL_CONSMAST where Cycle like '%" + str_cons_address + "%'";
                System.out.println ( "query---" + ret );
                Cursor curSearchdata = SD.rawQuery ( ret, null );
                System.out.println ( "query--- return12" + curSearchdata.moveToFirst ( ) );// && curconsmasData.moveToFirst()
                System.out.println ( "query--- return21" + curSearchdata != null );// && curconsmasData.moveToFirst()
                System.out.println ( "query--- return" + curSearchdata.getCount ( ) );
                if (curSearchdata != null && curSearchdata.moveToFirst ( )) {

                    System.out.println ( "query--- return" + curSearchdata.getCount ( ) );
                    uac = new UtilAppCommon ( );
                    try {
                        UtilAppCommon.copyResultsetToConmasClass ( curSearchdata );
                    } catch (SQLException e) {
                        e.printStackTrace ( );
                    }

                 /*   t_number.setText(Structconsmas.Consumer_Number);
                    t_name.setText(Structconsmas.Name);
*/
                    //ArrayList
                    list = new ArrayList <String> ( );

                    adapter = new ArrayAdapter <String> ( getApplicationContext ( ), R.layout.grid_view_billing, list );
                    gridView.setAdapter ( adapter );

                    gridView.setOnItemClickListener ( new AdapterView.OnItemClickListener ( ) {
                        @Override
                        public void onItemClick(AdapterView <?> arg0, View v, int position, long arg3) {

                            Toast.makeText ( getApplicationContext ( ), list.get ( position ).toString ( ), Toast.LENGTH_SHORT ).show ( );

                          /*  Intent intent=new Intent(getApplicationContext(),CameraActivity.class);
//                                        intent.putExtra("datalist", allist);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);*/
                            consumer_address_next = list.get ( position ).toString ( );
                            String ret1 = "select * from TBL_CONSMAST where MAIN_CONS_LNK_NO = '" + consumer_address_next + "'";
                            //String ret1="SELECT * FROM TBL_CONSMAST WHERE Name ="+consumer_name_next;
                            //String ret = "SELECT Consumer_Number FROM TBL_CONSMAST WHERE Name='SAPHALA PARIDA'";
                            Cursor curconsmasData1 = SD.rawQuery ( ret1, null );

                            /****GETTING DATA FROM CURSOR WHEN CURSOR IS NOT NULL AND ON THE FORST INDEX****/
                            if (curconsmasData1 != null && curconsmasData1.moveToFirst ( )) {

                                uac = new UtilAppCommon ( );
                                try {
                                    UtilAppCommon.copyResultsetToConmasClass ( curconsmasData1 );
                                } catch (SQLException e) {
                                    e.printStackTrace ( );
                                }
                                /****INITIALISATION OF INTENT AND ADDING SENDING DATA TO NEXT SCREEN , STARING INTENT****/

                                Intent intent = new Intent ( getApplicationContext ( ), CameraActivity.class );
//                                        intent.putExtra("datalist", allist);
                                startActivity ( intent );
                                overridePendingTransition ( R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left );
                                //String temp_address = c.getString(3);
                                //System.out.println(temp_address+" result of select Query");
                            }


                        }
                    } );

        /*String number, name, meter;
        name="";
        number="";
        meter="";*/
                    dbHelper = new DB ( getApplicationContext ( ) );
                    SD = dbHelper.getWritableDatabase ( );


                    try {

                        //Move the cursor to the first row.
                        // if(curSearchdata.moveToFirst())   {

                        if (curSearchdata != null && curSearchdata.moveToFirst ( )) {

                /*  list.add("Consumer No.");
                  list.add("Name");
                  list.add("Meter No.");
                  list.add("Address");
                  list.add("Cycle");
                  list.add("Route");
                  list.add("Division");
                  list.add("Subdivision");*/
                            do {

                   /* name=c.getString(c.getColumnIndex("name"));
                    number=c.getString(c.getColumnIndex("roll"));
                    meter=c.getString(c.getColumnIndex("course"));*/
                                cnumber = curSearchdata.getString ( 0 );
                                ivrs = curSearchdata.getString ( 103 );
                                name = curSearchdata.getString ( 2 );
                                meter = curSearchdata.getString ( 11 );
                                address = curSearchdata.getString ( 3 ) + "" + curSearchdata.getString ( 4 ) + "" + curSearchdata.getString ( 69 ) + "" + curSearchdata.getString ( 70 ) + "" + curSearchdata.getString ( 71 );
                                cycle = curSearchdata.getString ( 5 );//group
                                route = curSearchdata.getString ( 70 );//RDG
                                division = curSearchdata.getString ( 10 );//DC

                                //add in to array list
                                list.add ( cnumber );
                                list.add ( ivrs );
                                list.add ( name );
                                list.add ( meter );
                                list.add ( address );
                                list.add ( cycle );
                                list.add ( route );
                                list.add ( division );
                                //gridView.setAdapter(adapter);
                            }
                            while (curSearchdata.moveToNext ( ));//Move the cursor to the next row.
                        } else {
                            Toast.makeText ( getApplicationContext ( ), "No data found", Toast.LENGTH_LONG ).show ( );
                        }
                    } catch (Exception e) {
                        Toast.makeText ( getApplicationContext ( ), "No data found" + e.getMessage ( ), Toast.LENGTH_LONG ).show ( );
                    }
                } else {
                    list = new ArrayList <String> ( );
                    adapter = new ArrayAdapter <String> ( getApplicationContext ( ), R.layout.grid_view_billing, list );
                    gridView.setAdapter ( adapter );
                    Toast.makeText ( getApplicationContext ( ), "No data found", Toast.LENGTH_LONG ).show ( );
                }
            }
        } );


        //handler.close();
        //Toast.makeText(getBaseContext(),"Name: "+name+"Roll No: "+roll+"Course: "+course , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction ( );
        ((GlobalPool) getApplication ( )).onUserInteraction ( );
    }

    @Override
    public void userLogoutListaner() {
        finish ( );
        Intent intent = new Intent ( this, MainActivity.class );
        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( intent );

    }

    public void onBackPressed() {
//        if (exit) {
        finish ( ); // finish activity
        this.overridePendingTransition ( R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right );
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
