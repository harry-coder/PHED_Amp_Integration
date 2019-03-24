package com.fedco.mbc.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.CustomClasses.DuplicateReceiptPrint;
import com.fedco.mbc.CustomClasses.LastPrintEnergyPojo;
import com.fedco.mbc.CustomClasses.Summery_Pojo;
import com.fedco.mbc.CustomClasses.TotalRecordsPojo;
import com.fedco.mbc.R;
import com.fedco.mbc.amigos.DuplicateCollectionPrint;
import com.fedco.mbc.amigos.MainActivityCollectionPrint;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.model.Structcolmas;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.URLS;
import com.fedco.mbc.utils.UtilAppCommon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.paperdb.Paper;

import static android.app.AlertDialog.Builder;

public class Phed_summary_report extends Activity {
    SQLiteDatabase SD;
    GridView gridView, gridView1, gridView3, gridView4;
    String cnumber, name, meter, division, address, mnl_recpt, Amount, date;

    static final String[] gridHeader1 = new String[]{"Date", "Type", "MeterNo", "ManualReceipt", "CustomerNo", "Name", "Amount"};
    // static final String[] gridHeader1 = new String[]{"Date","ManualReceipt","CustomerNo","Name","Amount"};

    static final String[] gridHeader2 = new String[]{"Date", "Cash", "Amount", "Cheque/POS", "Amount", "Total", "Amount"};
    DB dbHelper;
    private ArrayList <String> list, list2;

    private ArrayList <String> listDummy;
    final Calendar myCalendar = Calendar.getInstance ( );
    EditText edfrom, edTo;
    Button datefilter;
    private ArrayAdapter <String> adapter, adapter2;
    FloatingActionButton floatingActionButton;
    String paymentType, prev_pref;
    TextView txt1, txt2, todayrecpt, todayamt, totalrecpt, totalamt;
    UtilAppCommon uac;
    Logger Log;
    final DecimalFormat numberFormat = new DecimalFormat ( "########0.00" );

    ProgressDialog progressDialog;

    ArrayList <Summery_Pojo> summaryList;
    ArrayList <TotalRecordsPojo> totalRecordsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.phed_summary_report );
        gridView = findViewById ( R.id.gridView );
        gridView1 = findViewById ( R.id.gridView1 );
//        gridView3 = (GridView) findViewById(R.id.gridView3);
//        gridView4 = (GridView) findViewById(R.id.gridView4);
        txt1 = findViewById ( R.id.txtvalue );
//        txt2=(TextView)findViewById(R.id.txtvalue1);
        txt1.setVisibility ( View.GONE );
        todayrecpt = findViewById ( R.id.txtrept );
        todayamt = findViewById ( R.id.tktamt1 );
        totalrecpt = findViewById ( R.id.txtrecpt3 );
        totalamt = findViewById ( R.id.txtamt4 );


        summaryList = new ArrayList <> ( );
        totalRecordsList = new ArrayList <> ( );
        datefilter = findViewById ( R.id.button2 );
        deletCollectionReport ( );
//        txt2.setVisibility(View.GONE);
        //floatingActionButton=(FloatingActionButton)findViewById(R.id.fab);
        Button dHomeButton = findViewById ( R.id.HomeButton );

        progressDialog = new ProgressDialog ( this );


        dHomeButton.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {

                finish ( );
                overridePendingTransition ( R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right );

            }
        } );


        // insertIntotbl_colmaster_mp ();

        listDummy = new ArrayList <> ( );

        paymentType = GSBilling.getInstance ( ).getPayType ( );
        uac = new UtilAppCommon ( );
        dbHelper = new DB ( getApplicationContext ( ) );
        SD = dbHelper.getWritableDatabase ( );

        /*   insertIntotbl_colmaster_mp ( );
         */
         /*for(int i=0;i<100;i++){

            insertIntotbl_colmaster_mp ();
        }*/


//        Cursor cursor = SD.rawQuery("Select TBL_COLLECTION.METER_INST_FLAG as MeterNo,TBL_COLLECTION.CON_NO AS ConsumerNo,TBL_COLLECTION.CON_NAME as ConsumerName,TBL_COLLECTION.SECTION as Section,SUM(TBL_COLMASTER_MP.Amount)Amount from TBL_COLMASTER_MP join TBL_COLLECTION where TBL_COLMASTER_MP.CON_NO=TBL_COLLECTION.CON_NO GROUP BY TBL_COLMASTER_MP.CON_NO", null);
        //susil query   //Cursor cursor = SD.rawQuery("SELECT col_date, SUM(cashcount)as cashcount,SUM(cashcol)as cashcol,SUM( chqcount) as chqcount, SUM(chqcol) as chqcol, SUM(cashcount )+ SUM(chqcount) as  total, SUM(cashcol )+ SUM(chqcol)  as amount FROM  (SELECT  count(*)  AS  cashcount,SUM(0) as chqcount,col_date, SUM(amount) AS cashcol,SUM(0) as chqcol  FROM ( SELECT a.col_date,  a.amount AS amount fROM  tbl_colmaster_mp a  where pymnt_type = 'C'  )  GROUP BY col_date   union all  SELECT SUM(0) as cashcount , COUNT(*) AS chqcount,col_date,SUM(0) as cashcol, SUM(amount) AS chqcol  FROM   ( SELECT a.col_date,  a.amount AS amount FROM tbl_colmaster_mp a  where pymnt_type  != 'C')   GROUP BY col_date) GROUP BY col_date  ",null);


        Cursor cursor = SD.rawQuery ( "SELECT  col_date,CON_NO,CONS_NAME, amount , cast(MAN_RECP_NO as int) as MAN_RECP_NO,METER_NO FROM   tbl_colmaster_mp   where pymnt_type = 'C'  union all   SELECT  col_date,CON_NO,CONS_NAME,amount,cast(MAN_RECP_NO as int) as MAN_RECP_NO,METER_NO   FROM   tbl_colmaster_mp   where pymnt_type  != 'C' order by  MAN_RECP_NO   DESC ", null );
        // Cursor cursor = SD.rawQuery("SELECT  col_date,CON_NO,CONS_NAME, amount ,MAN_RECP_NO  FROM   tbl_colmaster_mp   where pymnt_type = 'C'  union all   SELECT  col_date,CON_NO,CONS_NAME,amount,MAN_RECP_NO  FROM   tbl_colmaster_mp   where pymnt_type  != 'C' order by  col_date,MAN_RECP_NO  DESC ",null);

        Cursor cursorreport = SD.rawQuery ( "select sum(todaytotalrecpt) as todaytotalrecpt,ifnull(round(sum(todaytotalamount),2),0) as todaytotalamount,sum(monthlytotalrecpt) as monthlytotalrecpt,ifnull(round(sum(monthlytotalamount),2),0) as monthlytotalamount from ( select  * from ( SELECT count(*) as todaytotalrecpt,sum(0) as todaytotalamount ,sum(0) as monthlytotalrecpt  ,sum(0) as  monthlytotalamount   FROM  tbl_colmaster_mp where DATE(substr(col_date,7,4)||'-'||substr(col_date,4,2)||'-'||substr(col_date,1,2))  between  strftime('%Y-%m-%d', date('now'))  and strftime('%Y-%m-%d', date('now')) union  SELECT sum(0) as todaytotalrecpt , sum(amount) as todaytotalamount,sum(0) as monthlytotalrecpt  ,sum(0) as  monthlytotalamount FROM  tbl_colmaster_mp where DATE(substr(col_date,7,4)||'-'||substr(col_date,4,2)||'-'||substr(col_date,1,2))  between  strftime('%Y-%m-%d', date('now'))  and strftime('%Y-%m-%d', date('now')) union SELECT sum(0) as todaytotalrecpt ,sum(0) as todaytotalamount,count(*) as monthlytotalrecpt ,sum(0) as  monthlytotalamount FROM tbl_colmaster_mp WHERE    DATE(substr(col_date,7,4)||'-'||substr(col_date,4,2)||'-'||substr(col_date,1,2)) BETWEEN datetime('now', 'start of month') AND datetime('now', 'localtime') union SELECT sum(0) as todaytotalrecpt ,sum(0) as todaytotalamount,sum(0) as monthlytotalrecpt ,sum(amount) as monthlytotalamount  FROM tbl_colmaster_mp WHERE    DATE(substr(col_date,7,4)||'-'||substr(col_date,4,2)||'-'||substr(col_date,1,2))  BETWEEN datetime('now', 'start of month') AND datetime('now', 'localtime') ))  ", null );

        ArrayAdapter gridheaderadapter1 = new ArrayAdapter ( this, R.layout.grid_view_header, gridHeader1 );
        //  ArrayAdapter gridheaderadapter2 = new ArrayAdapter(this, R.layout.grid_view_header, gridHeader2);
/*
        TotalRecordsPojo recordsPojo=totalRecordsList.get ( 0 );
        todayrecpt.setText ( recordsPojo.getTodayTotalReceipt () );
        todayamt.setText ( MainActivityCollectionPrint.internationAnotation ( recordsPojo.getTodayTotalAmount () ) );
        totalrecpt.setText ( recordsPojo.getMonthlyTotalReceipt () );
        totalamt.setText ( MainActivityCollectionPrint.internationAnotation ( recordsPojo.getMonthlyTotalAmount () ) );
*/


      /*  try {
            String _todayrecpt = null, _todayamount = null, _monthlyrecpt = null, _monthlyamount = null;
            if (cursorreport.getCount ( ) > 0 && cursorreport.moveToFirst ( )) {

                do {
                    _todayrecpt = cursorreport.getString ( 0 );
                    _todayamount = numberFormat.format ( Double.parseDouble ( cursorreport.getString ( 1 ).toString ( ) ) );
                    _monthlyrecpt = cursorreport.getString ( 2 );
                    _monthlyamount = numberFormat.format ( Double.parseDouble ( cursorreport.getString ( 3 ) ) );


                } while (cursorreport.moveToNext ( ));
            }
            todayrecpt.setText ( _todayrecpt );
            todayamt.setText ( MainActivityCollectionPrint.internationAnotation ( _todayamount ) );
            totalrecpt.setText ( _monthlyrecpt );
            totalamt.setText ( MainActivityCollectionPrint.internationAnotation ( _monthlyamount ) );

        } catch (Exception e) {
            e.getStackTrace ( );
        }*/


        gridView.setAdapter ( gridheaderadapter1 );
//        gridView3.setAdapter(gridheaderadapter2);


        list = new ArrayList <String> ( );
        try {

         /*   if (cursor.getCount ( ) > 0 && cursor.moveToFirst ( )) {

                do {


                    date = cursor.getString ( 0 );


                    cnumber = cursor.getString ( 1 );
                    name = cursor.getString ( 2 );
                    mnl_recpt = cursor.getString ( 4 );
                    Amount = cursor.getString ( 3 );

                    String meterNumber = cursor.getString ( 5 );

                    System.out.println ( "This is the meter number " + meterNumber );

                    //curSearchdata.getString(3)+""+curSearchdata.getString(4)+""+curSearchdata.getString(69)+""+curSearchdata.getString(70)+""+curSearchdata.getString(71);


                    if(!date.equalsIgnoreCase ( "null" )){
                        System.out.println ("date "+date );
                        list.add ( date );

                        list.add ( meterNumber );

                        list.add ( mnl_recpt );
                        list.add ( cnumber );
                        list.add ( name );
                        list.add ( MainActivityCollectionPrint.internationAnotation ( Amount ) );



                    }


                    System.out.println ("Date "+date );
                    System.out.println ("meter "+meterNumber );
                    System.out.println ("receipt "+mnl_recpt );
                    System.out.println ("number "+cnumber );
                    System.out.println ("name "+name );
                    System.out.println ("amount "+Amount );

                    *//*for(int i=0;i<100;i++){

                        listDummy.add ( date );

                        listDummy.add ( meterNumber );

                        listDummy.add ( mnl_recpt );
                        listDummy.add ( cnumber );
                        listDummy.add ( name );
                        listDummy.add ( MainActivityCollectionPrint.internationAnotation ( Amount ) );

                    }*//*


                } while (cursor.moveToNext ( ));
                */


/*
            for(int i=0;i<summaryList.size ();i++){

                Summery_Pojo summery_pojo=summaryList.get ( i );

                list.add ( summery_pojo.getDate () );
                list.add ( summery_pojo.getMeterNo ( ) );
                list.add ( summery_pojo.getManualReceipt ( ) );
                list.add ( summery_pojo.getCustomerNo ( ) );
                list.add ( summery_pojo.getName ( ) );
                list.add ( summery_pojo.getAmount ( ) );



            }

            if (summaryList.size ( ) != 0) {

                Summery_Pojo summery_pojo = summaryList.get ( 0 );
                Summery_Pojo summery1_pojo = summaryList.get ( 1 );

                list.add ( summery_pojo.getDate ( ) );
                list.add ( summery_pojo.getMeterNo ( ) );
                list.add ( summery_pojo.getManualReceipt ( ) );
                list.add ( summery_pojo.getCustomerNo ( ) );
                list.add ( summery_pojo.getName ( ) );
                list.add ( summery_pojo.getAmount ( ) );


                list.add ( summery1_pojo.getDate ( ) );
                list.add ( summery1_pojo.getMeterNo ( ) );
                list.add ( summery1_pojo.getManualReceipt ( ) );
                list.add ( summery1_pojo.getCustomerNo ( ) );
                list.add ( summery1_pojo.getName ( ) );
                list.add ( summery1_pojo.getAmount ( ) );


                adapter = new ArrayAdapter <String> ( getApplicationContext ( ), R.layout.grid_view_billing, list );
                gridView1.setAdapter ( adapter );
            } else {
                txt1.setVisibility ( View.VISIBLE );
                txt1.setText ( "Data not found!" );
            }*/
        } catch (Exception ex) {
            ex.getStackTrace ( );
            //Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        //dbHelper = new DB(getApplicationContext());
        //SD = dbHelper.getWritableDatabase();
//        Cursor cursor = SD.rawQuery("Select TBL_COLLECTION.METER_INST_FLAG as MeterNo,TBL_COLLECTION.CON_NO AS ConsumerNo,TBL_COLLECTION.CON_NAME as ConsumerName,TBL_COLLECTION.SECTION as Section,SUM(TBL_COLMASTER_MP.Amount)Amount from TBL_COLMASTER_MP join TBL_COLLECTION where TBL_COLMASTER_MP.CON_NO=TBL_COLLECTION.CON_NO GROUP BY TBL_COLMASTER_MP.CON_NO", null);
        //susil query   //Cursor cursor = SD.rawQuery("SELECT col_date, SUM(cashcount)as cashcount,SUM(cashcol)as cashcol,SUM( chqcount) as chqcount, SUM(chqcol) as chqcol, SUM(cashcount )+ SUM(chqcount) as  total, SUM(cashcol )+ SUM(chqcol)  as amount FROM  (SELECT  count(*)  AS  cashcount,SUM(0) as chqcount,col_date, SUM(amount) AS cashcol,SUM(0) as chqcol  FROM ( SELECT a.col_date,  a.amount AS amount fROM  tbl_colmaster_mp a  where pymnt_type = 'C'  )  GROUP BY col_date   union all  SELECT SUM(0) as cashcount , COUNT(*) AS chqcount,col_date,SUM(0) as cashcol, SUM(amount) AS chqcol  FROM   ( SELECT a.col_date,  a.amount AS amount FROM tbl_colmaster_mp a  where pymnt_type  != 'C')   GROUP BY col_date) GROUP BY col_date  ",null);
       /* Cursor cursor1 = SD.rawQuery("SELECT max(col_date) AS col_date,sum(cashcount)cashcount, sum(cashcol) as cashcol, sum(chqcount) as chqcount, sum(chqcol)as chqcol, sum(cashcount) + sum(chqcount) total, sum(cashcol) + sum(chqcol) amount FROM (  SELECT COUNT(*) AS cashcount, SUM(0) AS chqcount, col_date, SUM(amount) AS cashcol, SUM(0) AS chqcol  FROM ( SELECT a.col_date, a.amount AS amount FROM  tbl_colmaster_mp a WHERE a. pymnt_type = 'C'  ) group  by  col_date UNION ALL SELECT SUM(0) AS cashcount, COUNT(*) AS chqcount,  col_date, SUM(0) AS cashcol, SUM(amount) AS chqcol FROM ( SELECT a.col_date, a.amount AS amount FROM tbl_colmaster_mp a WHERE a.pymnt_type != 'C'   ) group  by  col_date )  ",null);
        list2=new ArrayList<>();
        String _date,cash,_Amount,_pos,Total,Amount2,Amount3;
        try {
            if (cursor1.getCount()>0 && cursor1.moveToFirst()) {
                if(cursor1.getString(0)!=null) {
                    do {
                        _date = cursor1.getString(0);
                        cash = cursor1.getString(1);
                        _Amount = cursor1.getString(2);
                        _pos = cursor1.getString(3);
                        Total = cursor1.getString(4);
                        Amount2 = cursor1.getString(5);
                        Amount3 = cursor1.getString(6);
                        //curSearchdata.getString(3)+""+curSearchdata.getString(4)+""+curSearchdata.getString(69)+""+curSearchdata.getString(70)+""+curSearchdata.getString(71);
                        list2.add(_date);
                        list2.add(cash);
                        list2.add(_Amount);
                        list2.add(_pos);
                        list2.add(Total);
                        list2.add(Amount2);
                        list2.add(Amount3);

                    } while (cursor1.moveToNext());
                    adapter2 = new ArrayAdapter<String>(getApplicationContext(), R.layout.grid_view_billing, list2);
                    gridView4.setAdapter(adapter2);
                }else
                {
                    txt2.setVisibility(View.VISIBLE);
                    txt2.setText("Data not found!");
                }
            }else
            {
                txt2.setVisibility(View.VISIBLE);
                txt2.setText("Data not found!");
                //Toast.makeText(getApplicationContext(), "No data found" , Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
        }*/


        gridView1.setOnItemClickListener ( new AdapterView.OnItemClickListener ( ) {
            @Override
            public void onItemClick(AdapterView <?> adapterView, View view, int position, long l) {
                try {
                    // final String manualrecptno = list.get ( position ).toString ( );

                    final String manualrecptno = list.get ( position );

                    new SpecificReceiptPrint ( ).execute ( manualrecptno );

                   /* System.out.println ( "Ya it clicked " + position );
                    String _date = null, _time = null, _recpt = null, _mtrno = null, _consumeNo = null, _consumeName = null, _Tokenno = null, _Amount = null, _mnl_recpt = null, _address = null;
                    if (true) {
                        final String consNumber = list.get ( position ).toString ( );
                        final String manualrecptno = list.get ( position ).toString ( );

                        System.out.println ( "This is the manual receipt " + manualrecptno );

// /
//       Cursor cursor = SD.rawQuery("Select TBL_COLLECTION.METER_INST_FLAG as MeterNo,TBL_COLLECTION.CON_NO AS ConsumerNo,TBL_COLLECTION.CON_NAME as ConsumerName,TBL_COLLECTION.SECTION as Section,SUM(TBL_COLMASTER_MP.Amount)Amount from TBL_COLMASTER_MP join TBL_COLLECTION where TBL_COLMASTER_MP.CON_NO=TBL_COLLECTION.CON_NO and TBL_COLMASTER_MP.CON_NO='"+consNumber+"' GROUP BY TBL_COLMASTER_MP.CON_NO", null);
                        Cursor cursor = SD.rawQuery ( "SELECT  col_date,CON_NO,CONS_NAME, amount ,ADDRESS,TOKEN_NO,METER_NO,PAYMENT_PURPOSE,UNITSACTAL,RECIP_NO,MR_NAME,COL_TIME,PYMNT_TYPE,TARIFFCODE,TARIFF_RATE,TARIFF_INDEX,INCIDENT_TYPE,IBC,BSC FROM   tbl_colmaster_mp   where  MAN_RECP_NO ='" + manualrecptno + "'", null );
                        if (cursor != null && cursor.moveToFirst ( )) {

                            do {
                                _date = cursor.getString ( 0 );
                                _consumeNo = cursor.getString ( 1 );
                                _consumeName = cursor.getString ( 2 );
                                _Amount = cursor.getString ( 3 );
                                _address = cursor.getString ( 4 );
                                _Tokenno = cursor.getString ( 5 );
                                _mtrno = cursor.getString ( 6 );
                                _recpt = cursor.getString ( 9 );
                                _time = cursor.getString ( 11 );

                                Structcolmas.CON_NO = cursor.getString ( 1 );
                                Structcolmas.METER_NO = cursor.getString ( 6 );
                                Structcolmas.CONS_NAME = cursor.getString ( 2 );
                                Structcolmas.ADDRESS = cursor.getString ( 4 );
                                Structcolmas.CON_TYPE = cursor.getString ( 7 );
                                Structcolmas.AMOUNT = cursor.getString ( 3 );
                                Structcolmas.UNITSACTAL = cursor.getString ( 8 );
                                Structcolmas.RECEIPT_NO = cursor.getString ( 9 );
                                Structcolmas.MR_NAME = cursor.getString ( 10 );
                                Structcolmas.COL_DATE = cursor.getString ( 0 );
                                Structcolmas.COL_TIME = cursor.getString ( 11 );
                                Structcolmas.PYMNT_MODE = cursor.getString ( 12 );
                                Structcolmas.TOKEN_NO = cursor.getString ( 5 );
                                Structcolmas.TARIFFCODE = cursor.getString ( 13 );
                                Structcolmas.TARIFF_RATE = cursor.getString ( 14 );
                                Structcolmas.TARIFF_INDEX = cursor.getString ( 15 );
                                Structcolmas.INCIDENT_TYPE = cursor.getString ( 16 );
                                Structcolmas.IBC = cursor.getString ( 17 );
                                Structcolmas.BSC = cursor.getString ( 18 );

                            } while (cursor.moveToNext ( ));
                            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext ( ).getSystemService ( Context.LAYOUT_INFLATER_SERVICE );
                            View customView = null;
                            int screenSize = getResources ( ).getConfiguration ( ).screenLayout &
                                    Configuration.SCREENLAYOUT_SIZE_MASK;

                            switch (screenSize) {
                                case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                                    customView = layoutInflater.inflate ( R.layout.collection_summary_dialog_large, null );
                                    break;
                                case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                                    customView = layoutInflater.inflate ( R.layout.collection_summary_dialog, null );
                                    break;
                                case Configuration.SCREENLAYOUT_SIZE_LARGE:
                                    customView = layoutInflater.inflate ( R.layout.collection_summary_dialog_large, null );
                                    break;
                                case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                                    customView = layoutInflater.inflate ( R.layout.collection_summary_dialog, null );
                                    break;
                                case Configuration.SCREENLAYOUT_SIZE_SMALL:
                                    customView = layoutInflater.inflate ( R.layout.collection_summary_dialog, null );
                                    break;
                                default:
                                    customView = layoutInflater.inflate ( R.layout.collection_summary_dialog, null );
                            }

                            TextView txtmeterNo = customView.findViewById ( R.id.tktmtrno );
                            TextView txtcusumerName = customView.findViewById ( R.id.txtconsname );
                            TextView txttktno = customView.findViewById ( R.id.tktno );
                            TextView token = customView.findViewById ( R.id.token );
                            final TextView txtconsNo = customView.findViewById ( R.id.txtconsno );
                            TextView txtamount = customView.findViewById ( R.id.cn );
                            TextView datetime = customView.findViewById ( R.id.datentime );
                            TextView txtaddress = customView.findViewById ( R.id.address );
                            Button _print = customView.findViewById ( R.id.print );

                            txtamount.setText ( _Amount );

                            txtconsNo.setText ( _consumeNo );
                            txtmeterNo.setText ( _mtrno );
                            txtcusumerName.setText ( _consumeName );
                            txttktno.setText ( _recpt );
                            token.setText ( _Tokenno );

                            txtamount.setText ( _Amount );

                            txtaddress.setText ( _address );
                            datetime.setText ( _date + "/" + _time );
                            Builder alert = new Builder ( Phed_summary_report.this );
                            // this is set the view from XML inside AlertDialog
                            alert.setView ( customView );
                            final AlertDialog dialog = alert.create ( );
                            dialog.show ( );
                            _print.setOnClickListener ( new View.OnClickListener ( ) {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss ( );
//                                    _duplicatePrint(manualrecptno);
                                    Intent intent = new Intent ( Phed_summary_report.this, DuplicateCollectionPrint.class );
                                    intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                    startActivity ( intent );
                                    overridePendingTransition ( R.anim.anim_slide_in_left,
                                            R.anim.anim_slide_out_left );
                                }
                            } );
                        }

                    }*/


                } catch (Exception ex) {
                    ex.getStackTrace ( );
                }
            }
        } );

//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View customView = layoutInflater.inflate(R.layout.date_selection_dialog, null);
//                AlertDialog.Builder alert = new AlertDialog.Builder(Phed_summary_report.this);
//                // this is set the view from XML inside AlertDialog
//                edfrom=(EditText)customView.findViewById(R.id.edtFrom);
//                edTo=(EditText)customView.findViewById(R.id.edTo);
//
//                edfrom.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        // TODO Auto-generated method stub
//                        datepicker("From");
//                    }
//                });
//                edTo.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        datepicker("To");
//                    }
//                });
//                alert.setView(customView);
//                AlertDialog dialog = alert.create();
//                dialog.setButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        gridShorting();
//                    }
//                });
//                dialog.show();
//            }
//        });


        datefilter.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {


                datepicker ( "From" );


               /* LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext ( ).getSystemService ( Context.LAYOUT_INFLATER_SERVICE );
                View customView = layoutInflater.inflate ( R.layout.date_selection_dialog, null );
                Builder alert = new Builder ( Phed_summary_report.this );
                // this is set the view from XML inside AlertDialog
                edfrom = customView.findViewById ( R.id.edtFrom );
                edTo = customView.findViewById ( R.id.edTo );

                edfrom.setOnClickListener ( new View.OnClickListener ( ) {
                    @Override
                    public void onClick(View view) {
                        // TODO Auto-generated method stub
                        datepicker ( "From" );
                    }
                } );
                edTo.setOnClickListener ( new View.OnClickListener ( ) {
                    @Override
                    public void onClick(View view) {
                        datepicker ( "To" );
                    }
                } );
                alert.setView ( customView );
                AlertDialog dialog = alert.create ( );
//                Builder(this,getResources().getColor("));
                dialog.setButton ( "OK", new DialogInterface.OnClickListener ( ) {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        if (edfrom.getText ( ).toString ( ).equals ( "" ) || edTo.getText ( ).toString ( ).equals ( "" )) {
                            Toast.makeText ( getApplicationContext ( ), "please select date", Toast.LENGTH_LONG ).show ( );
                        } else {
                            try {
                                String date1 = edfrom.getText ( ).toString ( );
                                String date2 = edTo.getText ( ).toString ( );
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat ( "dd/MM/yyyy" );
                                Date dt1 = simpleDateFormat.parse ( date1 );
                                Date dt2 = simpleDateFormat.parse ( date2 );
                                if (dt1.after ( dt2 )) {
                                    Toast.makeText ( getApplicationContext ( ), "Date from is must greater than to date", Toast.LENGTH_LONG ).show ( );
                                } else {
                                    gridShorting ( );
                                }
                            } catch (Exception ex) {

                            }


                        }

                    }
                } );
                dialog.show ( );
*/
            }
        } );


        requestServerSummary ( );

    }

    public void requestServerSummary() {


        DateFormat dateFormat = new SimpleDateFormat ( "dd-MM-yyyy", Locale.getDefault ( ) );
        Date date = new Date ( );

        new SummaryRequest ( ).execute ( dateFormat.format ( date ) );


    }

    public void datepicker(final String editValue) {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener ( ) {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set ( Calendar.YEAR, year );
                myCalendar.set ( Calendar.MONTH, monthOfYear );
                myCalendar.set ( Calendar.DAY_OF_MONTH, dayOfMonth );
                String myFormat = "dd-MM-yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat ( myFormat, Locale.US );

                String date = sdf.format ( myCalendar.getTime ( ) );

                new SummaryRequest ( ).execute ( date );
                System.out.println ( "This is the date selected " + date );


                /*
                if (editValue.equals ( "From" ))
                    edfrom.setText ( sdf.format ( myCalendar.getTime ( ) ) );
                else
                    edTo.setText ( sdf.format ( myCalendar.getTime ( ) ) );*/
            }

        };
        new DatePickerDialog ( Phed_summary_report.this, date, myCalendar
                .get ( Calendar.YEAR ), myCalendar.get ( Calendar.MONTH ),
                myCalendar.get ( Calendar.DAY_OF_MONTH ) ).show ( );
    }

    public void gridShorting() {
        list = new ArrayList <> ( );
        String[] _fromstring = edfrom.getText ( ).toString ( ).split ( "/" );
        String[] _tostr = edTo.getText ( ).toString ( ).split ( "/" );
        Long _from = Long.parseLong ( _fromstring[2] + _fromstring[1] + _fromstring[0] );
        Long _to = Long.parseLong ( _tostr[2] + _tostr[1] + _tostr[0] );
        Cursor cursor = SD.rawQuery ( "SELECT  col_date,CON_NO,CONS_NAME, amount ,MAN_RECP_NO,METER_NO  FROM   tbl_colmaster_mp   where pymnt_type = 'C'  union all   SELECT  col_date,CON_NO,CONS_NAME,amount,MAN_RECP_NO,METER_NO  FROM   tbl_colmaster_mp   where pymnt_type  != 'C' order by MAN_RECP_NO DESC", null );
        if (cursor != null && cursor.moveToFirst ( )) {
            do {
                String[] _colldate = cursor.getString ( 0 ).toString ( ).split ( "-" );
                Long _collection = Long.parseLong ( _colldate[2] + _colldate[1] + _colldate[0] );
                try {
                    if (_collection >= _from && _collection <= _to) {
                        txt1.setVisibility ( View.INVISIBLE );
                        date = cursor.getString ( 0 );
                        cnumber = cursor.getString ( 1 );
                        name = cursor.getString ( 2 );
                        mnl_recpt = cursor.getString ( 4 );
                        Amount = cursor.getString ( 3 );

                        String meterNumber = cursor.getString ( 5 );

                        System.out.println ( "This is the meter number " + meterNumber );
                        //curSearchdata.getString(3)+""+curSearchdata.getString(4)+""+curSearchdata.getString(69)+""+curSearchdata.getString(70)+""+curSearchdata.getString(71);
                        list.add ( date );
                        list.add ( meterNumber );
                        list.add ( mnl_recpt );
                        list.add ( cnumber );
                        list.add ( name );
                        list.add ( Amount );
                    } else {
                        txt1.setVisibility ( View.VISIBLE );
                        txt1.setText ( "Data not found!" );
                    }
                } catch (Exception e) {
                    e.printStackTrace ( );
                }
            } while (cursor.moveToNext ( ));
            adapter = new ArrayAdapter <String> ( getApplicationContext ( ), R.layout.grid_view_billing, list );
            gridView1.setAdapter ( adapter );
        }

    }

    //    public void deletCollectionReport(){
//        dbHelper = new DB(getApplicationContext());
//        SD = dbHelper.getWritableDatabase();
//        Cursor cursordelete = SD.rawQuery("delete  from tbl_colmaster_mp where  (SELECT strftime('%m',max(DATE ( substr(col_date,7,4)|| '-' || substr(col_date,4,2)|| '-' || substr(col_date,1,2) ))) - strftime('%m',min(DATE ( substr(col_date,7,4) || '-' || substr(col_date,4,2) || '-' || substr(col_date,1,2) ))) AS diff_in_sec FROM tbl_colmaster_mp) >=3 ",null);
//
//    }


    public void deletCollectionReport() {
        dbHelper = new DB ( getApplicationContext ( ) );
        SD = dbHelper.getWritableDatabase ( );
        int count = 0;
        Cursor cursor1 = SD.rawQuery ( "SELECT strftime('%m',max(DATE ( substr(col_date,7,4)|| '-' || substr(col_date,4,2)|| '-' || substr(col_date,1,2) ))) - strftime('%m',min(DATE ( substr(col_date,7,4) || '-' || substr(col_date,4,2) || '-' || substr(col_date,1,2) ))) AS diff_in_sec FROM tbl_colmaster_mp", null );
        if (cursor1.getCount ( ) > 0) {
            if (cursor1 != null && cursor1.moveToFirst ( ) && cursor1.getString ( 0 ) != null) {
                do {
                    count = Integer.parseInt ( cursor1.getString ( 0 ) );
                    Logger.e ( getApplicationContext ( ), "Month count number", String.valueOf ( count ) );
                } while (cursor1.moveToNext ( ));
            }
            if (count >= 1) {
                Cursor cursordelete = SD.rawQuery ( "delete  from tbl_colmaster_mp where  (SELECT strftime('%m',max(DATE ( substr(col_date,7,4)|| '-' || substr(col_date,4,2)|| '-' || substr(col_date,1,2) ))) - strftime('%m',min(DATE ( substr(col_date,7,4) || '-' || substr(col_date,4,2) || '-' || substr(col_date,1,2) ))) AS diff_in_sec FROM tbl_colmaster_mp) >=1 ", null );
            }
        } else {
            txt1.setVisibility ( View.VISIBLE );
            txt1.setText ( "Data not found!" );

        }
    }


    private class SummaryRequest extends AsyncTask <String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute ( );
            runOnUiThread ( new Runnable ( ) {
                @Override
                public void run() {
                    progressDialog.setMessage ( "Loading Summary" );
                    progressDialog.show ( );

                }
            } );
        }

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;


            try {

                System.out.println ( "This is the id " + MainActivity.MRID );

                String userId;

                if(Paper.book (  ).exist ( "userId" )){
                    userId=Paper.book ().read ( "userId" );
                }
                else {
                    userId=MainActivity.MRID;
                }
                //URL url = new URL(URLS.UserAccess.userAuthenticate + codeIMEI + "&Uid=" + params[0] + "&Pass=" + params[1]);
                //URL url = new URL ( URLS.CollectionData.collectionSummary + "27-02-2019" + "/" + MainActivity.MR_id );
                URL url = new URL ( URLS.CollectionData.collectionSummary + params[0] + "/" + "PH" + userId );


                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection ( );
                urlConnection.setRequestMethod ( "GET" );
                urlConnection.connect ( );

                // Read the input stream into a String
                String id = null;
                InputStream inputStream = urlConnection.getInputStream ( );
                StringBuffer buffer = new StringBuffer ( );
                if (inputStream == null) {
                    // Nothing to do.
                    return "123";
                }
                reader = new BufferedReader ( new InputStreamReader ( inputStream ) );

                String line;
                while ((line = reader.readLine ( )) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append ( line + "\n" );
                }

                if (buffer.length ( ) == 0) {
                    // Stream was empty.  No point in parsing.
                    return "123";
                }
                forecastJsonStr = buffer.toString ( );

                return forecastJsonStr;
            } catch (MalformedURLException e) {
                e.printStackTrace ( );
            } catch (ProtocolException e) {
                e.printStackTrace ( );
            } catch (IOException e) {
                Log.v ( getApplicationContext ( ), "MainAct : ", "Authenticate", e );
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect ( );
                }
                if (reader != null) {
                    try {
                        reader.close ( );
                    } catch (final IOException e) {
                        Log.e ( getApplicationContext ( ), "PlaceholderFragment", "Error closing stream", e );
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute ( s );

            totalRecordsList.clear ( );
            summaryList.clear ( );
            list.clear ( );


            System.out.println ( "This is the response " + s );


            if (s != null && !s.isEmpty ( ) && !s.equals ( " " )) {

                JSONArray ja = null;
                try {
                    ja = new JSONArray ( s );

                    TotalRecordsPojo totalRecords = new TotalRecordsPojo ( );
                    JSONObject summaryObject = ja.getJSONObject ( 0 );
                    totalRecords.setTodayTotalReceipt ( summaryObject.getString ( "todaytotalrecpt" ) );
                    totalRecords.setTodayTotalAmount ( summaryObject.getString ( "todaytotalamount" ) );
                    totalRecords.setMonthlyTotalReceipt ( summaryObject.getString ( "monthlytotalrecpt" ) );
                    totalRecords.setMonthlyTotalAmount ( summaryObject.getString ( "monthlytotalamount" ) );

                    totalRecordsList.add ( totalRecords );
                    JSONArray summaryArray = summaryObject.getJSONArray ( "DETAILS" );
                    for (int j = 0; j < summaryArray.length ( ); j++) {

                        JSONObject summaryDetailsObject = summaryArray.getJSONObject ( j );
                        Summery_Pojo summery_pojo = new Summery_Pojo ( );

                        summery_pojo.setDate ( summaryDetailsObject.getString ( "col_date" ) );
                        summery_pojo.setCustomerNo ( summaryDetailsObject.getString ( "CON_NO" ) );
                        summery_pojo.setName ( summaryDetailsObject.getString ( "CONS_NAME" ) );
                        summery_pojo.setAmount ( summaryDetailsObject.getString ( "amount" ) );
                        summery_pojo.setManualReceipt ( summaryDetailsObject.getString ( "MAN_RECP_NO" ) );
                        summery_pojo.setMeterNo ( summaryDetailsObject.getString ( "METERNO" ) );
                        summery_pojo.setType ( summaryDetailsObject.getString ( "TYPE" ) );


                        summaryList.add ( summery_pojo );

                    }


                } catch (JSONException e) {
                    e.printStackTrace ( );
                }

                /*if (s.trim ( ).equals ( "0" )) {


                    showAuthenticateDialog ( );

                    // Toast.makeText(MainActivity.this, "You Are Not Authenticated To Use This Application", Toast.LENGTH_SHORT).show();


                } */


                setTotalRecords ( );
                setSummaryList ( );

            } else {
                Toast.makeText ( Phed_summary_report.this, "Something went wrong try again!!", Toast.LENGTH_SHORT ).show ( );
                // System.out.println ("This is inside offline" );
                // new offlineAuthenticate().execute(uId, uPass);
            }
        }


    }


    public void setTotalRecords() {

        progressDialog.dismiss ( );


        TotalRecordsPojo recordsPojo = totalRecordsList.get ( 0 );
        todayrecpt.setText ( recordsPojo.getTodayTotalReceipt ( ) );
        todayamt.setText ( MainActivityCollectionPrint.internationAnotation ( recordsPojo.getTodayTotalAmount ( ) ) );
        totalrecpt.setText ( recordsPojo.getMonthlyTotalReceipt ( ) );
        totalamt.setText ( MainActivityCollectionPrint.internationAnotation ( recordsPojo.getMonthlyTotalAmount ( ) ) );


    }

    public void setSummaryList() {

        System.out.println ( "This is called with size " + summaryList.size ( ) );
        if (summaryList.size ( ) != 0) {
            gridView1.setVisibility ( View.VISIBLE );
            txt1.setVisibility ( View.GONE );

            for (int i = 0; i < summaryList.size ( ); i++) {

                Summery_Pojo summery_pojo = summaryList.get ( i );

                list.add ( summery_pojo.getDate ( ) );
                list.add ( summery_pojo.getType ( ) );
                list.add ( summery_pojo.getMeterNo ( ) );
                list.add ( summery_pojo.getManualReceipt ( ) );
                list.add ( summery_pojo.getCustomerNo ( ) );
                list.add ( summery_pojo.getName ( ) );
                list.add ( MainActivityCollectionPrint.internationAnotation ( summery_pojo.getAmount ( ) ) );
            }
        } else {
            gridView1.setVisibility ( View.GONE );
            txt1.setVisibility ( View.VISIBLE );
            txt1.setText ( "Data not found!" );
        }

        adapter = new ArrayAdapter <String> ( getApplicationContext ( ), R.layout.grid_view_billing, list );
        adapter.notifyDataSetInvalidated ( );
        //  gridView1.invalidateViews();

        refresh ( );
        //   adapter.notifyDataSetChanged ();

        gridView1.setAdapter ( adapter );


    }

    public void refresh() {
        new Handler ( ).post ( new Runnable ( ) {
            @Override
            public void run() {
                adapter.notifyDataSetChanged ( );
                gridView.invalidate ( );
            }
        } );

    }


    private class SpecificReceiptPrint extends AsyncTask <String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute ( );
            runOnUiThread ( new Runnable ( ) {
                @Override
                public void run() {
                    progressDialog.setMessage ( "Gathering Data" );
                    progressDialog.show ( );

                }
            } );
        }

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;


            try {


                //URL url = new URL(URLS.UserAccess.userAuthenticate + codeIMEI + "&Uid=" + params[0] + "&Pass=" + params[1]);
                //URL url = new URL ( URLS.CollectionData.collectionSummary + "27-02-2019" + "/" + MainActivity.MR_id );

                URL url = new URL ( URLS.CollectionData.collectionLastReceipt + params[0] );


                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection ( );
                urlConnection.setRequestMethod ( "GET" );
                urlConnection.connect ( );

                // Read the input stream into a String
                String id = null;
                InputStream inputStream = urlConnection.getInputStream ( );
                StringBuffer buffer = new StringBuffer ( );
                if (inputStream == null) {
                    // Nothing to do.
                    return "123";
                }
                reader = new BufferedReader ( new InputStreamReader ( inputStream ) );

                String line;
                while ((line = reader.readLine ( )) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append ( line + "\n" );
                }

                if (buffer.length ( ) == 0) {
                    // Stream was empty.  No point in parsing.
                    return "123";
                }
                forecastJsonStr = buffer.toString ( );

                return forecastJsonStr;
            } catch (MalformedURLException e) {
                e.printStackTrace ( );
            } catch (ProtocolException e) {
                e.printStackTrace ( );
            } catch (IOException e) {
                Log.v ( getApplicationContext ( ), "MainAct : ", "Authenticate", e );
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect ( );
                }
                if (reader != null) {
                    try {
                        reader.close ( );
                    } catch (final IOException e) {
                        Log.e ( getApplicationContext ( ), "PlaceholderFragment", "Error closing stream", e );
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute ( s );


            System.out.println ( "This is the response " + s );

            String noResponse = "NO DATA FOUND";

            if (s != null && !s.contains ( noResponse )) {

                System.out.println ( s.equalsIgnoreCase ( noResponse ) );


                System.out.println ( "Inside this response " + s );

                JSONArray ja = null;
                try {

                    ja = new JSONArray ( s );

                    JSONObject lastReceiptObject = ja.getJSONObject ( 0 );

                    String amount = lastReceiptObject.getString ( "AMOUNT" );
                    String conNo = lastReceiptObject.getString ( "CUSTOMER_NO" );
                    String meterNo = lastReceiptObject.getString ( "METER_NO" );
                    String consumerName = lastReceiptObject.getString ( "CONS_NAME" );
                    String receiptNo = lastReceiptObject.getString ( "RECEIPTNUMBER" );
                    String tokenNo = lastReceiptObject.getString ( "TOKENDESC" );
                    String date = lastReceiptObject.getString ( "PAYMENTDATETIME" );
                    String address = lastReceiptObject.getString ( "ADDRESS" );


                    DuplicateReceiptPrint.account = lastReceiptObject.getString ( "CUSTOMER_NO" );
                    DuplicateReceiptPrint.meterNo = lastReceiptObject.getString ( "METER_NO" );
                    DuplicateReceiptPrint.eReceipt = lastReceiptObject.getString ( "RECEIPTNUMBER" );
                    DuplicateReceiptPrint.dateTime = lastReceiptObject.getString ( "PAYMENTDATETIME" );
                    DuplicateReceiptPrint.token = lastReceiptObject.getString ( "TOKENDESC" );
                    DuplicateReceiptPrint.units = lastReceiptObject.getString ( "UNITSACTUAL" );

                    // tariffRate=lastReceiptObject.getString ( "TARIFF" );

                    DuplicateReceiptPrint.customerName = lastReceiptObject.getString ( "CONS_NAME" );
                    DuplicateReceiptPrint.address = lastReceiptObject.getString ( "ADDRESS" );

                    // DuplicateReceiptPrint.account = lastReceiptObject.getString ( "PAYMENT_PURPOSE" );

                    DuplicateReceiptPrint.handledBy = lastReceiptObject.getString ( "MR_NAME" );
                    DuplicateReceiptPrint.lPaymentType = lastReceiptObject.getString ( "PYMNT_TYPE" );
                    DuplicateReceiptPrint.tariffCode = lastReceiptObject.getString ( "TARIFFCODE" );
                    DuplicateReceiptPrint.tariffRate = lastReceiptObject.getString ( "TARIFF_RATE" );
                    DuplicateReceiptPrint.tariffIndex = lastReceiptObject.getString ( "TARIFF_INDEX" );
                    DuplicateReceiptPrint.ibc = lastReceiptObject.getString ( "IBC" );
                    DuplicateReceiptPrint.bsc = lastReceiptObject.getString ( "BSC" );
                    DuplicateReceiptPrint.lastReceiptAmount = lastReceiptObject.getString ( "AMOUNT" );

                    DuplicateReceiptPrint.incidentType = lastReceiptObject.getString ( "INCIDENT_TYPE" );

                    DuplicateReceiptPrint.conType = lastReceiptObject.getString ( "CON_TYPE" );
                    DuplicateReceiptPrint.bankName = lastReceiptObject.getString ( "BANKNAME" );
                    DuplicateReceiptPrint.chequeNo = lastReceiptObject.getString ( "CHEQUENO" );
                    DuplicateReceiptPrint.chequeDate = lastReceiptObject.getString ( "CHEQUEDATE" );

                    JSONArray receiptEnergyDetails = lastReceiptObject.getJSONArray ( "DETAILS" );
                    for (int i = 0; i < receiptEnergyDetails.length ( ); i++) {

                        LastPrintEnergyPojo lastPrintEnergyPojo = new LastPrintEnergyPojo ( );
                        JSONObject energyObject = receiptEnergyDetails.getJSONObject ( i );
                        lastPrintEnergyPojo.setHead ( energyObject.getString ( "HEAD" ) );
                        lastPrintEnergyPojo.setAmount ( energyObject.getString ( "AMOUNT" ) );

                        DuplicateReceiptPrint.itemList.add ( lastPrintEnergyPojo );

                    }

                    // dayEndReport=new DayEndReport ();

                    showPrintDialog ( amount, conNo, meterNo, consumerName, receiptNo, tokenNo, date, address );

                } catch (JSONException e) {
                    e.printStackTrace ( );
                }

                //     GSBilling.getInstance ( ).setPayType ( "CHEQUE" );


                //  System.out.println ("This is the cash "+dayEndReport.getTotalAmount () );
  /*Intent intent = new Intent ( Collection.this, DuplicateCollectionPrint.class );
                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity ( intent );
                overridePendingTransition ( R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left );
*/



                   /* if (s.trim ( ).equals ( "0" )) {


                    showAuthenticateDialog ( );

                    // Toast.makeText(MainActivity.this, "You Are Not Authenticated To Use This Application", Toast.LENGTH_SHORT).show();


                }
*/


                progressDialog.dismiss ( );
                //  setTotalRecords ( );
                //setSummaryList ( );

            } else {

                //    Toast.makeText ( Phed_summary_report.this, "Something went wrong try again!!", Toast.LENGTH_SHORT ).show ( );
                progressDialog.dismiss ( );
                // System.out.println ("This is inside offline" );
                // new offlineAuthenticate().execute(uId, uPass);
            }
        }


        public void showPrintDialog(String amount, String consNo, String meterNo, String consumerName, String receiptNo, String tokenNo, String date, String address) {

            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext ( ).getSystemService ( Context.LAYOUT_INFLATER_SERVICE );
            View customView = null;
            int screenSize = getResources ( ).getConfiguration ( ).screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK;

            switch (screenSize) {
                case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                    customView = layoutInflater.inflate ( R.layout.collection_summary_dialog_large, null );
                    break;
                case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                    customView = layoutInflater.inflate ( R.layout.collection_summary_dialog, null );
                    break;
                case Configuration.SCREENLAYOUT_SIZE_LARGE:
                    customView = layoutInflater.inflate ( R.layout.collection_summary_dialog_large, null );
                    break;
                case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                    customView = layoutInflater.inflate ( R.layout.collection_summary_dialog, null );
                    break;
                case Configuration.SCREENLAYOUT_SIZE_SMALL:
                    customView = layoutInflater.inflate ( R.layout.collection_summary_dialog, null );
                    break;
                default:
                    customView = layoutInflater.inflate ( R.layout.collection_summary_dialog, null );
            }

            TextView txtmeterNo = customView.findViewById ( R.id.tktmtrno );
            TextView txtcusumerName = customView.findViewById ( R.id.txtconsname );
            TextView txttktno = customView.findViewById ( R.id.tktno );
            TextView token = customView.findViewById ( R.id.token );
            final TextView txtconsNo = customView.findViewById ( R.id.txtconsno );
            TextView txtamount = customView.findViewById ( R.id.cn );
            TextView datetime = customView.findViewById ( R.id.datentime );
            TextView txtaddress = customView.findViewById ( R.id.address );
            Button _print = customView.findViewById ( R.id.print );

            txtamount.setText ( amount );

            txtconsNo.setText ( consNo );
            txtmeterNo.setText ( meterNo );
            txtcusumerName.setText ( consumerName );
            txttktno.setText ( receiptNo );
            token.setText ( tokenNo );

            txtamount.setText ( amount );

            txtaddress.setText ( address );
            //  datetime.setText ( _date + "/" + _time );
            datetime.setText ( date );


            Builder alert = new Builder ( Phed_summary_report.this );
            // this is set the view from XML inside AlertDialog
            alert.setView ( customView );
            final AlertDialog dialog = alert.create ( );
            dialog.show ( );
            _print.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View view) {
                    dialog.dismiss ( );
//                                    _duplicatePrint(manualrecptno);
                    Intent intent = new Intent ( Phed_summary_report.this, DuplicateCollectionPrint.class );
                    intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity ( intent );
                    overridePendingTransition ( R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left );
                }
            } );
        }
    }

}


