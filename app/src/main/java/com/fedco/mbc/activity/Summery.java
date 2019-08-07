package com.fedco.mbc.activity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fedco.mbc.Adapter.BillingSummery;
import com.fedco.mbc.R;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.sqlite.DB;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Summery extends AppCompatActivity {

    //TextView tvCash, tvCheque, tvNoCheque, tvNoDD, tvTotAmnt;
    public SweetAlertDialog sDialog;
    SQLiteDatabase SD;
    DB dbHelper;
    String strDate;
    private ArrayList <String> results = new ArrayList <String> ( );
    BillingSummery handler;
    HashMap <String, ArrayList> summerycol;
    ArrayList <String> li, list_DailySummary, list_CummulativeSummary,
            list_Col_DailySummary, list_ColCummulativeSummary, list_CummulativeRepSummary;
    ListView lvItems, collvItems, colCUMlist;
    Button btnDailySum, btnDailyCum, btnDCSum, btnDCCum, btnDCSsr;
    Logger Log;
    LinearLayout linearLayout_SSR;
    /*CardView cvsum ,cvcum ,cvCsum ,cvCcum;
    TextView TVBCon,TVBUnit,TVBAmnt,TVBDate;*/

    static String[] dailySummaryHeaders;
    static String[] cummulativeHeaders;

    TextView tv_readTakenCustomer, tv_readNotTakenCustomer;


   /* static final String[] dailySummaryHeaders = new String[]{
            "Date", "Read Customer", "Consumption", "Billed Amount"
    };
   */
   /* static final String[] dailySummaryHeaders = new String[]{
            "Date", "BilledBilled Customer", "Billed Units", "Billed Amount"
    };*/

    /* static final String[] cummulativeHeaders = new String[]{
             "Latest Billing Date", "Billed Customer", "Billed Units", "Billed Amount"
     };
    */
    static final String[] col_SummaryHeaders = new String[]{
            "Date", "Cash", "Amount", "Cheque/POS", "Amount", "Total", "Amount"/*, "Current Amount*", "Arrear  Amount*"*/
    };
    static final String[] dailySummaryHeadersReplace = new String[]{
            "Date", "Replaced Customer"
    };
    static final String[] cummulativeHeadersReplace = new String[]{
            "Date", "Replaced Consumer"
    };

    GridView gv_DailySummaryHeader, gv_DailySummary,
            gv_CummulativeHeader, gv_CummulativeSummary,
            gv_CollectionDailySummaryHeader, gv_CollectionDailySummary,
            gv_CollectionCummulativeHeader, gv_CollectionCummulativeSummary, gv_CollectionDailySummaryHeaderSSR,
            gv_DailySummaryHeaderReplace, gv_DailySummaryReplace,
            gv_CummulativeHeaderReplace, gv_CummulativeSummaryReplace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        String flag = getIntent ( ).getStringExtra ( "FLAG" );

        /*-------------BILLING SUMMERY REPORT----------*/
        if (flag.equals ( "Billing" )) {

            if (Home.isMeter) {
                setContentView ( R.layout.meter_reading_report );


                tv_readTakenCustomer = (TextView) findViewById ( R.id.tv_readCustomer );
                tv_readNotTakenCustomer = (TextView) findViewById ( R.id.tv_notReadCustomer );

                tv_readTakenCustomer.setText ("Total Read Customer :"+ UnbilledConsumerReportActivity.readCustomer );
                tv_readNotTakenCustomer.setText ("Total Not Read Customer :"+ UnbilledConsumerReportActivity.notReadCustomer );


            } else {
                setContentView ( R.layout.activity_summery );

            }

            btnDailySum = (Button) findViewById ( R.id.btnDSR );
            btnDailyCum = (Button) findViewById ( R.id.btnCR );
            gv_DailySummaryHeader = (GridView) findViewById ( R.id.gridView_DS_Header );
            gv_CummulativeHeader = (GridView) findViewById ( R.id.gridView2 );

            gv_CummulativeSummary = (GridView) findViewById ( R.id.gridView3 );
            gv_DailySummary = (GridView) findViewById ( R.id.gridView1 );


            if (Home.isMeter) {
                dailySummaryHeaders = new String[]{
                        "Date", "Read Customer", "Consumption"
                };

                cummulativeHeaders = new String[]{
                        "Latest Read Date", "Read Customer", "Consumption"
                };





/*     notReadCustomer               gv_DailySummaryHeader.setNumColumns ( 3 );
                gv_CummulativeHeader.setNumColumns ( 3 );
                gv_CummulativeSummary.setNumColumns ( 3 );
                gv_DailySummary.setNumColumns ( 3 );*/

            } else {
                dailySummaryHeaders = new String[]{
                        "Date", "Billed Customer", "Consumption", "Billed Amount"
                };

                cummulativeHeaders = new String[]{
                        "Latest Billing Date", "Billed Customer", "Consumption", "Billed Amount"
                };
               /* gv_DailySummaryHeader.setNumColumns ( 4 );
                gv_CummulativeHeader.setNumColumns ( 4 );
                gv_CummulativeSummary.setNumColumns ( 4 );
                gv_DailySummary.setNumColumns ( 4 );*/
            }
           /* dailySummaryHeaders=new String[]{
                    "Date", "Read Customer", "Consumption", "Billed Amount"
            };


            cummulativeHeaders= new String[]{
                    "Latest Billing Date", "Billed Customer", "Billed Units", "Billed Amount"
            };*/

            ArrayAdapter <String> headerAdapter = new ArrayAdapter <String>
                    ( this, R.layout.grid_view_header, dailySummaryHeaders );
            gv_DailySummaryHeader.setAdapter ( headerAdapter );


            btnDailySum.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View view) {

                    dbHelper = new DB ( getApplicationContext ( ) );
                    SD = dbHelper.getWritableDatabase ( );
                    list_DailySummary = new ArrayList <String> ( );

//                    String dailySummary = "SELECT  count(*),Bill_Date,sum(Units_Consumed),sum(Cur_Bill_Total) from  TBL_BILLING GROUP BY BILL_DATE ORDER BY BILL_DATE DESC";
                    String dailySummary = "SELECT  count(*),Bill_Date,sum(Units_Consumed),sum(Cur_Bill_Total) from  (SELECT DISTINCT Cons_Number,BILL_DATE,Units_Consumed,Cur_Bill_Total FROM TBL_BILLING) GROUP BY BILL_DATE ORDER BY BILL_DATE DESC";
                    Cursor todoCursor = SD.rawQuery ( dailySummary, null );
                    System.out.println ( "Select Result : " + dailySummary );


                    if (todoCursor != null && todoCursor.moveToFirst ( )) {
                        do {
                            String billDate = todoCursor.getString ( 1 );
                            list_DailySummary.add ( billDate );
                            String billedConsumers = todoCursor.getString ( 0 );
                            list_DailySummary.add ( billedConsumers );
                            String billedUnits = todoCursor.getString ( 2 );
                            list_DailySummary.add ( billedUnits );

                            if (!Home.isMeter) {
                                String billedAmount = todoCursor.getString ( 3 );
                                list_DailySummary.add ( billedAmount );
                            }

                        }
                        while (todoCursor.moveToNext ( ));

                        ArrayAdapter <String> ar_DailySummary = new ArrayAdapter <String>
                                ( getApplicationContext ( ), R.layout.grid_view_billing, list_DailySummary );

                        gv_DailySummary.setAdapter ( ar_DailySummary );

                    } else {

                        Toast.makeText ( Summery.this, "No Data Found", Toast.LENGTH_SHORT ).show ( );
                    }

                }
            } );

            ArrayAdapter <String> cummulativeHeader = new ArrayAdapter <String>
                    ( this, R.layout.grid_view_header, cummulativeHeaders );
            gv_CummulativeHeader.setAdapter ( cummulativeHeader );

            btnDailyCum.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View view) {

                    dbHelper = new DB ( getApplicationContext ( ) );
                    SD = dbHelper.getWritableDatabase ( );
                    list_CummulativeSummary = new ArrayList <String> ( );

//                    String cummulativeSummary = "SELECT  count(*),sum(Units_Consumed),sum(Cur_Bill_Total) ,Bill_date from  TBL_BILLING";
                    String cummulativeSummary = "SELECT  count(*),Bill_Date,sum(Units_Consumed),sum(Cur_Bill_Total) from  (SELECT DISTINCT Cons_Number,BILL_DATE,Units_Consumed,Cur_Bill_Total FROM TBL_BILLING) ";
                    Cursor todoCursor = SD.rawQuery ( cummulativeSummary, null );

                    if (todoCursor != null && todoCursor.moveToFirst ( )) {
                        do {
                            if (todoCursor.getString ( 1 ) != null && !todoCursor.getString ( 1 ).isEmpty
                                    ( )) {
                                String billDate = todoCursor.getString ( 1 );
                                list_CummulativeSummary.add ( billDate );
                                String billedConsumers = todoCursor.getString ( 0 );
                                list_CummulativeSummary.add ( billedConsumers );
                                String billedUnits = todoCursor.getString ( 2 );
                                list_CummulativeSummary.add ( billedUnits );

                                if (!Home.isMeter) {
                                    String billedAmount = todoCursor.getString ( 3 );
                                    list_CummulativeSummary.add ( billedAmount );

                                }

                            } else {

                                Toast.makeText ( Summery.this, "No Data Found", Toast.LENGTH_SHORT ).show ( );
                            }

                        }
                        while (todoCursor.moveToNext ( ));


                        ArrayAdapter <String> ad_CummulativeSummary = new ArrayAdapter <String>
                                ( getApplicationContext ( ), R.layout.grid_view_billing, list_CummulativeSummary );
                        gv_CummulativeSummary.setAdapter ( ad_CummulativeSummary );


                    } else {

                        Toast.makeText ( Summery.this, "No Data Found", Toast.LENGTH_SHORT ).show ( );
                    }

                }
            } );

        } else if (flag.equals ( "Collection" )) {

            /*-------------COLLECTION SUMMERY REPORT----------*/
            setContentView ( R.layout.activity_summery2 );

            btnDCSum = (Button) findViewById ( R.id.btnDCR );
            btnDCCum = (Button) findViewById ( R.id.btnCCR );
            btnDCSsr = (Button) findViewById ( R.id.btnSSR );
            linearLayout_SSR = (LinearLayout) findViewById ( R.id.ll_ssr );
            linearLayout_SSR.setVisibility ( View.INVISIBLE );

            gv_CollectionDailySummaryHeader = (GridView) findViewById ( R.id.gridView5 );
//            gv_CollectionDailySummaryHeaderSSR = (GridView) findViewById(R.id.gridViewSSR1);

            ArrayAdapter <String> ad_ColSummaryHeader = new ArrayAdapter <String>
                    ( this, R.layout.grid_text_custom, col_SummaryHeaders );
            gv_CollectionDailySummaryHeader.setAdapter ( ad_ColSummaryHeader );

            btnDCSum.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View view) {

                    dbHelper = new DB ( getApplicationContext ( ) );
                    SD = dbHelper.getWritableDatabase ( );
                    gv_CollectionDailySummary = (GridView) findViewById ( R.id.gridView6 );
                    list_Col_DailySummary = new ArrayList <String> ( );

                    // Find ListView to populate
                    // Query for items from the database and get a cursor back
                    // Cursor todoCursor = SD.rawQuery("SELECT  * FROM TBL_BILLING", null);
//                            String collectionSum="select  A.cnt cashcount,A.amt as cashcol,A.col_date,B.cnt as chqcount,B.amt as chqcol,B.col_date from (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt FROM  TBL_COLMASTER_MP where pymnt_type='C' group by pymnt_type,col_date order by col_date) A  left join  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt FROM  TBL_COLMASTER_MP where pymnt_type='Q' group by pymnt_type,col_date order by col_date) B on A.col_date=B.col_date";
//                    String collectionSum="select distinct col_date,cashcount,cashcol,chqcount ,chqcol ,cashcount+chqcount TOTAL,cashcol+chqcol AMOUNT from (select  ifnull(A.cnt,0) cashcount,ifnull(A.amt,0) as cashcol,case when A.col_date is null then B.col_date else A.col_date end as col_date,ifnull(B.cnt,0) as chqcount,ifnull(B.amt,0) as chqcol,case when B.col_date is null then A.col_date else B.col_date end as col_date from (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt FROM  TBL_COLMASTER_MP where pymnt_type='C' group by pymnt_type) A  LEFT join  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt FROM  TBL_COLMASTER_MP where pymnt_type='Q' group by pymnt_type) B on A.col_date=B.col_date UNION ALL select  ifnull(B.cnt,0) cashcount,ifnull(B.amt,0) as cashcol,case when B.col_date is null then A.col_date else B.col_date end as col_date,ifnull(A.cnt,0) as chqcount,ifnull(A.amt,0) as chqcol,case when A.col_date is null then B.col_date else A.col_date end as col_date from (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt FROM  TBL_COLMASTER_MP where pymnt_type='Q' group by pymnt_type) A  LEFT join  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt FROM  TBL_COLMASTER_MP where pymnt_type='C' group by pymnt_type) B on A.col_date=B.col_date ) order by col_date desc ";
//                    String collectionSum="select distinct col_date,cashcount,cashcol,chqcount ,chqcol ,cashcount+chqcount TOTAL,cashcol+chqcol AMOUNT,CURRENT_AMOUNT,ARREAR_AMOUNT from (select  ifnull(A.CURRENT_AMOUNT,0) CURRENT_AMOUNT,ifnull(A.ARREAR_AMOUNT,0) ARREAR_AMOUNT,ifnull(A.cnt,0) cashcount,ifnull(A.amt,0) as cashcol,case when A.col_date is null then B.col_date else A.col_date end as col_date, ifnull(B.cnt,0) as chqcount,ifnull(B.amt,0) as chqcol,case when B.col_date is null then A.col_date else B.col_date end as col_date from (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(B.CUR_TOT_BILL),CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS CURRENT_AMOUNT,A.AMOUNT-CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS ARREAR_AMOUNT,A.AMOUNT as amount FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO) where pymnt_type='C' group by pymnt_type) A  LEFT join  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM   (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(B.CUR_TOT_BILL),CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS CURRENT_AMOUNT,A.AMOUNT-CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS ARREAR_AMOUNT,A.AMOUNT as amount FROM  TBL_COLMASTER_MP A,tbl_collection B  where  A.CON_NO=B.CON_NO) where pymnt_type='Q' group by pymnt_type) B   on A.col_date=B.col_date UNION ALL select  ifnull(A.CURRENT_AMOUNT,0) CURRENT_AMOUNT,ifnull(A.ARREAR_AMOUNT,0) ARREAR_AMOUNT,ifnull(B.cnt,0) cashcount,ifnull(B.amt,0) as cashcol,case when B.col_date is null then A.col_date else B.col_date end as col_date, ifnull(A.cnt,0) as chqcount,ifnull(A.amt,0) as chqcol,case when A.col_date is null then B.col_date else A.col_date end as col_date from  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM  (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(B.CUR_TOT_BILL),CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS CURRENT_AMOUNT,A.AMOUNT-CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS ARREAR_AMOUNT,A.AMOUNT as amount FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO ) where pymnt_type='Q' group by pymnt_type) A LEFT join  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM   (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(B.CUR_TOT_BILL),CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS CURRENT_AMOUNT,A.AMOUNT-CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS ARREAR_AMOUNT,A.AMOUNT as amount FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO) where pymnt_type='C' group by pymnt_type) B on A.col_date=B.col_date ) order by col_date desc ";
                    /*
                       commented on 12/2//2018 in chattisgarh
                    * */
//                    String collectionSum = "select  col_date,cashcount,cashcol,chqcount ,chqcol ,TOTAL,AMOUNT,SUM(CURRENT_AMOUNT),SUM(ARREAR_AMOUNT) FROM (select distinct col_date,cashcount,cashcol,chqcount ,chqcol ,cashcount+chqcount TOTAL,cashcol+chqcol AMOUNT,CURRENT_AMOUNT,ARREAR_AMOUNT from (select  ifnull(A1.CURRENT_AMOUNT,0) CURRENT_AMOUNT,ifnull(A1.ARREAR_AMOUNT,0) ARREAR_AMOUNT,ifnull(A1.cnt,0) cashcount,ifnull(A1.amt,0) as cashcol, case when A1.col_date is null then B1.col_date else A1.col_date end as col_date, ifnull(B1.cnt,0) as chqcount,ifnull(B1.amt,0) as chqcol,case when B1.col_date is null then A1.col_date else B1.col_date end as col_date  from  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM  (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(B.CUR_TOT_BILL),CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS CURRENT_AMOUNT,A.AMOUNT-CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS ARREAR_AMOUNT,A.AMOUNT as amount FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO) where pymnt_type='C' group by pymnt_type,col_date) A1  LEFT join  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT   FROM   (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(B.CUR_TOT_BILL),CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS CURRENT_AMOUNT,A.AMOUNT-CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS ARREAR_AMOUNT,A.AMOUNT as amount FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO) where pymnt_type!='C' group by pymnt_type,col_date) B1   on A1.col_date=B1.col_date UNION ALL select  ifnull(A2.CURRENT_AMOUNT,0) CURRENT_AMOUNT,ifnull(A2.ARREAR_AMOUNT,0) ARREAR_AMOUNT,ifnull(B2.cnt,0) cashcount, ifnull(B2.amt,0) as cashcol,case when B2.col_date is null then A2.col_date else B2.col_date end as col_date, ifnull(A2.cnt,0) as chqcount,ifnull(A2.amt,0) as chqcol,case when A2.col_date is null then B2.col_date else A2.col_date end as col_date from  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM  (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(B.CUR_TOT_BILL),CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS CURRENT_AMOUNT,A.AMOUNT-CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS ARREAR_AMOUNT,A.AMOUNT as amount FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO ) where pymnt_type!='C' group by pymnt_type,col_date) A2   LEFT join  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM   (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(B.CUR_TOT_BILL),CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS CURRENT_AMOUNT,A.AMOUNT-CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS ARREAR_AMOUNT,A.AMOUNT as amount FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO) where pymnt_type='C' group by pymnt_type,col_date)   B2 on A2.col_date=B2.col_date ) order by col_date desc  ) GROUP BY  col_date,cashcount,cashcol,chqcount ,chqcol ,TOTAL,AMOUNT  order by col_date desc";

//                    String collectionSum = "select  col_date,cashcount,cashcol,chqcount ,chqcol ,TOTAL,AMOUNT,SUM(CURRENT_AMOUNT),SUM(ARREAR_AMOUNT) FROM (select distinct col_date,cashcount,cashcol,chqcount ,chqcol ,cashcount+chqcount TOTAL,cashcol+chqcol AMOUNT,CURRENT_AMOUNT,ARREAR_AMOUNT from (select  ifnull(A1.CURRENT_AMOUNT,0) CURRENT_AMOUNT,ifnull(A1.ARREAR_AMOUNT,0) ARREAR_AMOUNT,ifnull(A1.cnt,0) cashcount,ifnull(A1.amt,0) as cashcol, case when A1.col_date is null then B1.col_date else A1.col_date end as col_date, ifnull(B1.cnt,0) as chqcount,ifnull(B1.amt,0) as chqcol,case when B1.col_date is null then A1.col_date else B1.col_date end as col_date  from  (select count(*)  as cnt,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM  (select A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(B.CUR_TOT_BILL),CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS CURRENT_AMOUNT,A.AMOUNT-CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS ARREAR_AMOUNT,A.AMOUNT as amount FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO and pymnt_type='C') group by col_date) A1  LEFT join  (select count(*)  as cnt,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT   FROM   (select A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(B.CUR_TOT_BILL),CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS CURRENT_AMOUNT,A.AMOUNT-CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS ARREAR_AMOUNT,A.AMOUNT as amount FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO and pymnt_type!='C') group by col_date) B1   on A1.col_date=B1.col_date UNION ALL select  ifnull(A2.CURRENT_AMOUNT,0) CURRENT_AMOUNT,ifnull(A2.ARREAR_AMOUNT,0) ARREAR_AMOUNT,ifnull(B2.cnt,0) cashcount, ifnull(B2.amt,0) as cashcol,case when B2.col_date is null then A2.col_date else B2.col_date end as col_date, ifnull(A2.cnt,0) as chqcount,ifnull(A2.amt,0) as chqcol,case when A2.col_date is null then B2.col_date else A2.col_date end as col_date from  (select count(*)  as cnt,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM  (select A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(B.CUR_TOT_BILL),CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS CURRENT_AMOUNT,A.AMOUNT-CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS ARREAR_AMOUNT,A.AMOUNT as amount FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO and pymnt_type!='C') group by col_date) A2   LEFT join  (select count(*)  as cnt,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM   (select A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(B.CUR_TOT_BILL),CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS CURRENT_AMOUNT,A.AMOUNT-CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS ARREAR_AMOUNT,A.AMOUNT as amount FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO and pymnt_type='C')  group by col_date)   B2 on A2.col_date=B2.col_date ) order by col_date desc  ) GROUP BY  col_date,cashcount,cashcol,chqcount ,chqcol ,TOTAL,AMOUNT  order by col_date desc";
                    // sushil old  String collectionSum = "select  col_date,cashcount,cashcol,chqcount ,chqcol ,TOTAL,AMOUNT,SUM(CURRENT_AMOUNT),SUM(ARREAR_AMOUNT) FROM (select distinct col_date,cashcount,cashcol,chqcount ,chqcol ,cashcount+chqcount TOTAL,cashcol+chqcol AMOUNT,CURRENT_AMOUNT,ARREAR_AMOUNT from (select  ifnull(A1.CURRENT_AMOUNT,0) CURRENT_AMOUNT,ifnull(A1.ARREAR_AMOUNT,0) ARREAR_AMOUNT,ifnull(A1.cnt,0) cashcount,ifnull(A1.amt,0) as cashcol, case when A1.col_date is null then B1.col_date else A1.col_date end as col_date, ifnull(B1.cnt,0) as chqcount,ifnull(B1.amt,0) as chqcol, case when B1.col_date is null then A1.col_date else B1.col_date end as col_date   from  (select count(*)  as cnt,col_date ,sum(amount) as amt, sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT  FROM  (select A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE, ROUND(B.CUR_TOT_BILL),CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS CURRENT_AMOUNT, A.AMOUNT-CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS ARREAR_AMOUNT,A.AMOUNT as amount  FROM  TBL_COLMASTER_MP A left join tbl_collection B on  A.CON_NO=B.CON_NO and pymnt_type='C') group by col_date) A1  LEFT join  (select count(*)  as cnt, col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT   FROM  (select A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(B.CUR_TOT_BILL),CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS CURRENT_AMOUNT,A.AMOUNT-CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT, ROUND(B.CUR_TOT_BILL)) END AS ARREAR_AMOUNT,A.AMOUNT as amount  FROM  TBL_COLMASTER_MP A left join tbl_collection B on  A.CON_NO=B.CON_NO and pymnt_type!='C') group by col_date) B1  on A1.col_date=B1.col_date UNION ALL  select  ifnull(A2.CURRENT_AMOUNT,0) CURRENT_AMOUNT,ifnull(A2.ARREAR_AMOUNT,0) ARREAR_AMOUNT,ifnull(B2.cnt,0) cashcount, ifnull(B2.amt,0) as cashcol,case when B2.col_date is null then A2.col_date else B2.col_date end as col_date, ifnull(A2.cnt,0)  as chqcount,ifnull(A2.amt,0) as chqcol,case when A2.col_date is null then B2.col_date else A2.col_date end as col_date  from  (select count(*)  as cnt, col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM  (select A.col_date , round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(B.CUR_TOT_BILL),CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS CURRENT_AMOUNT,A.AMOUNT-CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS ARREAR_AMOUNT,A.AMOUNT as amount  FROM  TBL_COLMASTER_MP A left join tbl_collection B on A.CON_NO=B.CON_NO and pymnt_type!='C') group by col_date) A2  LEFT join  (select count(*)  as cnt,col_date ,sum(amount) as amt, sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT  FROM   (select A.col_date ,round(B.AMNT_AFT_RBT_DATE), B.AMNT_BFR_RBT_DATE,ROUND(B.CUR_TOT_BILL),CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))  END AS CURRENT_AMOUNT,A.AMOUNT-CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS ARREAR_AMOUNT, A.AMOUNT as amount FROM  TBL_COLMASTER_MP A left join tbl_collection B on A.CON_NO=B.CON_NO and pymnt_type='C')   group by col_date)  B2 on A2.col_date=B2.col_date ) order by col_date desc  ) GROUP BY  col_date,cashcount,cashcol,chqcount ,chqcol ,TOTAL,AMOUNT  order by col_date desc";
                    String collectionSum = "SELECT col_date, SUM(cashcount)as cashcount,SUM(cashcol)as cashcol,SUM( chqcount) as chqcount, SUM(chqcol) as chqcol, SUM(cashcount )+ SUM(chqcount) as  total, SUM(cashcol )+ SUM(chqcol)  as amount FROM  (SELECT  count(*)  AS  cashcount,SUM(0) as chqcount,col_date, SUM(amount) AS cashcol,SUM(0) as chqcol  FROM ( SELECT a.col_date,  a.amount AS amount fROM  tbl_colmaster_mp a  where pymnt_type = 'C'  )  GROUP BY col_date   union all  SELECT SUM(0) as cashcount , COUNT(*) AS chqcount,col_date,SUM(0) as cashcol, SUM(amount) AS chqcol  FROM   ( SELECT a.col_date,  a.amount AS amount FROM tbl_colmaster_mp a  where pymnt_type  != 'C')   GROUP BY col_date) GROUP BY col_date  ";
//                    String collectionSum = "SELECT max(col_date) AS col_date,sum(cashcount)cashcount, sum(cashcol) as cashcol, sum(chqcount) as chqcount, sum(chqcol)as chqcol, sum(cashcount) + sum(chqcount) total, sum(cashcol) + sum(chqcol) amount FROM (  SELECT COUNT(*) AS cashcount, SUM(0) AS chqcount, col_date, SUM(amount) AS cashcol, SUM(0) AS chqcol  FROM ( SELECT a.col_date, a.amount AS amount FROM  tbl_colmaster_mp a WHERE a. pymnt_type = 'C'  ) group  by  col_date UNION ALL SELECT SUM(0) AS cashcount, COUNT(*) AS chqcount,  col_date, SUM(0) AS cashcol, SUM(amount) AS chqcol FROM ( SELECT a.col_date, a.amount AS amount FROM tbl_colmaster_mp a WHERE a.pymnt_type != 'C'   ) group  by  col_date ) ";

//                    String collectionSum="select  col_date,cashcount,cashcol,chqcount ,chqcol ,TOTAL,AMOUNT,SUM(CURRENT_AMOUNT),SUM(ARREAR_AMOUNT) FROM (select distinct col_date,cashcount,cashcol,chqcount ,chqcol ,cashcount+chqcount TOTAL,cashcol+chqcol AMOUNT,CURRENT_AMOUNT,ARREAR_AMOUNT from (select  ifnull(A1.CURRENT_AMOUNT,0) CURRENT_AMOUNT,ifnull(A1.ARREAR_AMOUNT,0) ARREAR_AMOUNT,ifnull(A1.cnt,0) cashcount,ifnull(A1.amt,0) as cashcol, ( CASE WHEN A1.col_date is null then B1.col_date else A1.col_date END) as col_date, ifnull(B1.cnt,0) as chqcount,ifnull(B1.amt,0) as chqcol,( CASE WHEN B1.col_date is null then A1.col_date else B1.col_date END) as col_date  from  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM  (SELECT  pymnt_type,col_date,CURRENT_AMOUNT,AMOUNT,AMOUNT-CURRENT_AMOUNT AS arrear_amount FROM (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(cast(B.CUR_TOT_BILL as real)),( CASE WHEN MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real)))<0 THEN 0 ELSE  MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real))) END) AS CURRENT_AMOUNT,cast(A.AMOUNT as real) AS AMOUNT FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO) ) where pymnt_type='C' group by pymnt_type,col_date) A1  LEFT join  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT   FROM   (SELECT  pymnt_type,col_date,CURRENT_AMOUNT,AMOUNT,AMOUNT-CURRENT_AMOUNT AS arrear_amount FROM (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(cast(B.CUR_TOT_BILL as real)),( CASE WHEN MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real)))<0 THEN 0 ELSE  MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real))) END) AS CURRENT_AMOUNT,cast(A.AMOUNT as real) AS AMOUNT FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO)) where pymnt_type='Q' group by pymnt_type,col_date) B1   on A1.col_date=B1.col_date UNION ALL select  ifnull(A2.CURRENT_AMOUNT,0) CURRENT_AMOUNT,ifnull(A2.ARREAR_AMOUNT,0) ARREAR_AMOUNT,ifnull(B2.cnt,0) cashcount, ifnull(B2.amt,0) as cashcol,( CASE WHEN B2.col_date is null then A2.col_date else B2.col_date END) as col_date, ifnull(A2.cnt,0) as chqcount,ifnull(A2.amt,0) as chqcol,( CASE WHEN A2.col_date is null then B2.col_date else A2.col_date END) as col_date from  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM  (SELECT  pymnt_type,col_date,CURRENT_AMOUNT,AMOUNT,AMOUNT-CURRENT_AMOUNT AS arrear_amount FROM (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(cast(B.CUR_TOT_BILL as real)),( CASE WHEN MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real)))<0 THEN 0 ELSE  MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real))) END) AS CURRENT_AMOUNT,cast(A.AMOUNT as real) AS AMOUNT FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO) ) where pymnt_type='Q' group by pymnt_type,col_date) A2   LEFT join  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM   (SELECT  pymnt_type,col_date,CURRENT_AMOUNT,AMOUNT,AMOUNT-CURRENT_AMOUNT AS arrear_amount FROM (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(cast(B.CUR_TOT_BILL as real)),( CASE WHEN MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real)))<0 THEN 0 ELSE  MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real))) END) AS CURRENT_AMOUNT,cast(A.AMOUNT as real) AS AMOUNT FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO)) where pymnt_type='C' group by pymnt_type,col_date)   B2 on A2.col_date=B2.col_date ) order by col_date desc  ) GROUP BY  col_date,cashcount,cashcol,chqcount ,chqcol ,TOTAL,AMOUNT  order by col_date desc";
                    Cursor todoCursor = SD.rawQuery ( collectionSum, null );
                    if (todoCursor != null && todoCursor.moveToFirst ( )) {
                        do {
                            String zero = todoCursor.getString ( 0 );
                            list_Col_DailySummary.add ( zero );

                            String one = todoCursor.getString ( 1 );
                            list_Col_DailySummary.add ( one );

                            String two = todoCursor.getString ( 2 );
                            list_Col_DailySummary.add ( two );

                            String three = todoCursor.getString ( 3 );
                            list_Col_DailySummary.add ( three );

                            String four = todoCursor.getString ( 4 );
                            list_Col_DailySummary.add ( four );

                            String five = todoCursor.getString ( 5 );
                            list_Col_DailySummary.add ( five );

                            String six = todoCursor.getString ( 6 );
                            list_Col_DailySummary.add ( six );
//                            String seven = todoCursor.getString(7);
//                            list_Col_DailySummary.add(seven);
//
//                            String eight = todoCursor.getString(8);
//                            list_Col_DailySummary.add(eight);
                        }
                        while (todoCursor.moveToNext ( ));
                        ArrayAdapter <String> ad_ColDailySummary = new ArrayAdapter <String>
                                ( getApplicationContext ( ), R.layout.grid_view, list_Col_DailySummary );
                        gv_CollectionDailySummary.setAdapter ( ad_ColDailySummary );

                    } else {
                        Toast.makeText ( Summery.this, "No data Found", Toast.LENGTH_SHORT ).show ( );
                    }
//                    System.out.println("" + list_Col_DailySummary);


//                            if (todoCursor.moveToFirst()){
//                                do{
//                                    String data = todoCursor.getString(0);
//                                    System.out.println("DDDD "+ todoCursor.getString(0)+ todoCursor.getString(1)+ todoCursor.getString(2));
//
//                                    // do what ever you want here
//                                }while(todoCursor.moveToNext());
//                            }
                    // Create the Adapter
//                            ColSummery colListAdapter=new ColSummery(Summery.this,todoCursor);

                    // Set The Adapter to ListView
//                            collvItems.setAdapter(colListAdapter);

                }
            } );

            gv_CollectionDailySummaryHeaderSSR = (GridView) findViewById ( R.id.gridView5 );

            ArrayAdapter <String> ad_ColSummaryHeaderSSR = new ArrayAdapter <String>
                    ( this, R.layout.grid_text_custom, col_SummaryHeaders );
            gv_CollectionDailySummaryHeaderSSR.setAdapter ( ad_ColSummaryHeaderSSR );

            btnDCSsr.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View view) {

                    dbHelper = new DB ( getApplicationContext ( ) );
                    SD = dbHelper.getWritableDatabase ( );
                    gv_CollectionDailySummary = (GridView) findViewById ( R.id.gridView6 );
                    list_Col_DailySummary = new ArrayList <String> ( );
                    // Find ListView to populate
                    // Query for items from the database and get a cursor back
                    // Cursor todoCursor = SD.rawQuery("SELECT  * FROM TBL_BILLING", null);
//                            String collectionSum="select  A.cnt cashcount,A.amt as cashcol,A.col_date,B.cnt as chqcount,B.amt as chqcol,B.col_date from (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt FROM  TBL_COLMASTER_MP where pymnt_type='C' group by pymnt_type,col_date order by col_date) A  left join  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt FROM  TBL_COLMASTER_MP where pymnt_type='Q' group by pymnt_type,col_date order by col_date) B on A.col_date=B.col_date";
//                    String collectionSum="select distinct col_date,cashcount,cashcol,chqcount ,chqcol ,cashcount+chqcount TOTAL,cashcol+chqcol AMOUNT from (select  ifnull(A.cnt,0) cashcount,ifnull(A.amt,0) as cashcol,case when A.col_date is null then B.col_date else A.col_date end as col_date,ifnull(B.cnt,0) as chqcount,ifnull(B.amt,0) as chqcol,case when B.col_date is null then A.col_date else B.col_date end as col_date from (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt FROM  TBL_COLMASTER_MP where pymnt_type='C' group by pymnt_type) A  LEFT join  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt FROM  TBL_COLMASTER_MP where pymnt_type='Q' group by pymnt_type) B on A.col_date=B.col_date UNION ALL select  ifnull(B.cnt,0) cashcount,ifnull(B.amt,0) as cashcol,case when B.col_date is null then A.col_date else B.col_date end as col_date,ifnull(A.cnt,0) as chqcount,ifnull(A.amt,0) as chqcol,case when A.col_date is null then B.col_date else A.col_date end as col_date from (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt FROM  TBL_COLMASTER_MP where pymnt_type='Q' group by pymnt_type) A  LEFT join  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt FROM  TBL_COLMASTER_MP where pymnt_type='C' group by pymnt_type) B on A.col_date=B.col_date ) order by col_date desc ";
//                    String collectionSum="select distinct col_date,cashcount,cashcol,chqcount ,chqcol ,cashcount+chqcount TOTAL,cashcol+chqcol AMOUNT,CURRENT_AMOUNT,ARREAR_AMOUNT from (select  ifnull(A.CURRENT_AMOUNT,0) CURRENT_AMOUNT,ifnull(A.ARREAR_AMOUNT,0) ARREAR_AMOUNT,ifnull(A.cnt,0) cashcount,ifnull(A.amt,0) as cashcol,case when A.col_date is null then B.col_date else A.col_date end as col_date, ifnull(B.cnt,0) as chqcount,ifnull(B.amt,0) as chqcol,case when B.col_date is null then A.col_date else B.col_date end as col_date from (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(B.CUR_TOT_BILL),CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS CURRENT_AMOUNT,A.AMOUNT-CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS ARREAR_AMOUNT,A.AMOUNT as amount FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO) where pymnt_type='C' group by pymnt_type) A  LEFT join  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM   (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(B.CUR_TOT_BILL),CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS CURRENT_AMOUNT,A.AMOUNT-CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS ARREAR_AMOUNT,A.AMOUNT as amount FROM  TBL_COLMASTER_MP A,tbl_collection B  where  A.CON_NO=B.CON_NO) where pymnt_type='Q' group by pymnt_type) B   on A.col_date=B.col_date UNION ALL select  ifnull(A.CURRENT_AMOUNT,0) CURRENT_AMOUNT,ifnull(A.ARREAR_AMOUNT,0) ARREAR_AMOUNT,ifnull(B.cnt,0) cashcount,ifnull(B.amt,0) as cashcol,case when B.col_date is null then A.col_date else B.col_date end as col_date, ifnull(A.cnt,0) as chqcount,ifnull(A.amt,0) as chqcol,case when A.col_date is null then B.col_date else A.col_date end as col_date from  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM  (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(B.CUR_TOT_BILL),CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS CURRENT_AMOUNT,A.AMOUNT-CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS ARREAR_AMOUNT,A.AMOUNT as amount FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO ) where pymnt_type='Q' group by pymnt_type) A LEFT join  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM   (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(B.CUR_TOT_BILL),CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS CURRENT_AMOUNT,A.AMOUNT-CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS ARREAR_AMOUNT,A.AMOUNT as amount FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO) where pymnt_type='C' group by pymnt_type) B on A.col_date=B.col_date ) order by col_date desc ";

                    //String collectionSum="select  col_date,cashcount,cashcol,chqcount ,chqcol ,TOTAL,AMOUNT,SUM(CURRENT_AMOUNT),SUM(ARREAR_AMOUNT) FROM (select distinct col_date,cashcount,cashcol,chqcount ,chqcol ,cashcount+chqcount TOTAL,cashcol+chqcol AMOUNT,CURRENT_AMOUNT,ARREAR_AMOUNT from (select  ifnull(A1.CURRENT_AMOUNT,0) CURRENT_AMOUNT,ifnull(A1.ARREAR_AMOUNT,0) ARREAR_AMOUNT,ifnull(A1.cnt,0) cashcount,ifnull(A1.amt,0) as cashcol, case when A1.col_date is null then B1.col_date else A1.col_date end as col_date, ifnull(B1.cnt,0) as chqcount,ifnull(B1.amt,0) as chqcol,case when B1.col_date is null then A1.col_date else B1.col_date end as col_date  from  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM  (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(B.CUR_TOT_BILL),CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS CURRENT_AMOUNT,A.AMOUNT-CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS ARREAR_AMOUNT,A.AMOUNT as amount FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO) where pymnt_type='C' group by pymnt_type,col_date) A1  LEFT join  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT   FROM   (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(B.CUR_TOT_BILL),CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS CURRENT_AMOUNT,A.AMOUNT-CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS ARREAR_AMOUNT,A.AMOUNT as amount FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO) where pymnt_type='Q' group by pymnt_type,col_date) B1   on A1.col_date=B1.col_date UNION ALL select  ifnull(A2.CURRENT_AMOUNT,0) CURRENT_AMOUNT,ifnull(A2.ARREAR_AMOUNT,0) ARREAR_AMOUNT,ifnull(B2.cnt,0) cashcount, ifnull(B2.amt,0) as cashcol,case when B2.col_date is null then A2.col_date else B2.col_date end as col_date, ifnull(A2.cnt,0) as chqcount,ifnull(A2.amt,0) as chqcol,case when A2.col_date is null then B2.col_date else A2.col_date end as col_date from  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM  (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(B.CUR_TOT_BILL),CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS CURRENT_AMOUNT,A.AMOUNT-CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS ARREAR_AMOUNT,A.AMOUNT as amount FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO ) where pymnt_type='Q' group by pymnt_type,col_date) A2   LEFT join  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM   (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(B.CUR_TOT_BILL),CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS CURRENT_AMOUNT,A.AMOUNT-CASE WHEN MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL))<0 THEN 0 ELSE  MIN(A.AMOUNT,ROUND(B.CUR_TOT_BILL)) END AS ARREAR_AMOUNT,A.AMOUNT as amount FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO) where pymnt_type='C' group by pymnt_type,col_date)   B2 on A2.col_date=B2.col_date ) order by col_date desc  ) GROUP BY  col_date,cashcount,cashcol,chqcount ,chqcol ,TOTAL,AMOUNT  order by col_date desc";

                    String collectionSum = "select  col_date,cashcount,cashcol,chqcount ,chqcol ,TOTAL,AMOUNT,SUM(CURRENT_AMOUNT),SUM(ARREAR_AMOUNT) FROM (select distinct col_date,cashcount,cashcol,chqcount ,chqcol ,cashcount+chqcount TOTAL,cashcol+chqcol AMOUNT,CURRENT_AMOUNT,ARREAR_AMOUNT from (select  ifnull(A1.CURRENT_AMOUNT,0) CURRENT_AMOUNT,ifnull(A1.ARREAR_AMOUNT,0) ARREAR_AMOUNT,ifnull(A1.cnt,0) cashcount,ifnull(A1.amt,0) as cashcol, ( CASE WHEN A1.col_date is null then B1.col_date else A1.col_date END) as col_date, ifnull(B1.cnt,0) as chqcount,ifnull(B1.amt,0) as chqcol,( CASE WHEN B1.col_date is null then A1.col_date else B1.col_date END) as col_date  from  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM  (SELECT  pymnt_type,col_date,CURRENT_AMOUNT,AMOUNT,AMOUNT-CURRENT_AMOUNT AS arrear_amount FROM (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(cast(B.CUR_TOT_BILL as real)),( CASE WHEN MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real)))<0 THEN 0 ELSE  MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real))) END) AS CURRENT_AMOUNT,cast(A.AMOUNT as real) AS AMOUNT FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO) ) where pymnt_type='C' group by pymnt_type,col_date) A1  LEFT join  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT   FROM   (SELECT  pymnt_type,col_date,CURRENT_AMOUNT,AMOUNT,AMOUNT-CURRENT_AMOUNT AS arrear_amount FROM (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(cast(B.CUR_TOT_BILL as real)),( CASE WHEN MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real)))<0 THEN 0 ELSE  MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real))) END) AS CURRENT_AMOUNT,cast(A.AMOUNT as real) AS AMOUNT FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO)) where pymnt_type!='C' group by pymnt_type,col_date) B1   on A1.col_date=B1.col_date UNION ALL select  ifnull(A2.CURRENT_AMOUNT,0) CURRENT_AMOUNT,ifnull(A2.ARREAR_AMOUNT,0) ARREAR_AMOUNT,ifnull(B2.cnt,0) cashcount, ifnull(B2.amt,0) as cashcol,( CASE WHEN B2.col_date is null then A2.col_date else B2.col_date END) as col_date, ifnull(A2.cnt,0) as chqcount,ifnull(A2.amt,0) as chqcol,( CASE WHEN A2.col_date is null then B2.col_date else A2.col_date END) as col_date from  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM  (SELECT  pymnt_type,col_date,CURRENT_AMOUNT,AMOUNT,AMOUNT-CURRENT_AMOUNT AS arrear_amount FROM (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(cast(B.CUR_TOT_BILL as real)),( CASE WHEN MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real)))<0 THEN 0 ELSE  MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real))) END) AS CURRENT_AMOUNT,cast(A.AMOUNT as real) AS AMOUNT FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO) ) where pymnt_type!='C' group by pymnt_type,col_date) A2   LEFT join  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM   (SELECT  pymnt_type,col_date,CURRENT_AMOUNT,AMOUNT,AMOUNT-CURRENT_AMOUNT AS arrear_amount FROM (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(cast(B.CUR_TOT_BILL as real)),( CASE WHEN MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real)))<0 THEN 0 ELSE  MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real))) END) AS CURRENT_AMOUNT,cast(A.AMOUNT as real) AS AMOUNT FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO)) where pymnt_type='C' group by pymnt_type,col_date)   B2 on A2.col_date=B2.col_date ) order by col_date desc  ) GROUP BY  col_date,cashcount,cashcol,chqcount ,chqcol ,TOTAL,AMOUNT  order by col_date desc";
//                    String collectionSum = "SELECT max(col_date) AS col_date,sum(cashcount)cashcount, sum(cashcol) as cashcol, sum(chqcount) as chqcount, sum(chqcol)as chqcol, sum(cashcount) + sum(chqcount) total, sum(cashcol) + sum(chqcol) amount FROM (  SELECT COUNT(*) AS cashcount, SUM(0) AS chqcount, col_date, SUM(amount) AS cashcol, SUM(0) AS chqcol  FROM ( SELECT a.col_date, a.amount AS amount FROM  tbl_colmaster_mp a WHERE a. pymnt_type = 'C'  ) group  by  col_date UNION ALL SELECT SUM(0) AS cashcount, COUNT(*) AS chqcount,  col_date, SUM(0) AS cashcol, SUM(amount) AS chqcol FROM ( SELECT a.col_date, a.amount AS amount FROM tbl_colmaster_mp a WHERE a.pymnt_type != 'C'   ) group  by  col_date ) ";

                    Cursor todoCursor = SD.rawQuery ( collectionSum, null );
                    if (todoCursor != null && todoCursor.moveToFirst ( )) {
                        do {
                            String zero = todoCursor.getString ( 0 );
                            list_Col_DailySummary.add ( zero );

                            String one = todoCursor.getString ( 1 );
                            list_Col_DailySummary.add ( one );

                            String two = todoCursor.getString ( 2 );
                            list_Col_DailySummary.add ( two );

                            String three = todoCursor.getString ( 3 );
                            list_Col_DailySummary.add ( three );

                            String four = todoCursor.getString ( 4 );
                            list_Col_DailySummary.add ( four );

                            String five = todoCursor.getString ( 5 );
                            list_Col_DailySummary.add ( five );

                            String six = todoCursor.getString ( 6 );
                            list_Col_DailySummary.add ( six );

                            String seven = todoCursor.getString ( 7 );
                            list_Col_DailySummary.add ( seven );

                            String eight = todoCursor.getString ( 8 );
                            list_Col_DailySummary.add ( eight );

                        }
                        while (todoCursor.moveToNext ( ));
                        ArrayAdapter <String> ad_ColDailySummary = new ArrayAdapter <String>
                                ( getApplicationContext ( ), R.layout.grid_view, list_Col_DailySummary );
                        gv_CollectionDailySummary.setAdapter ( ad_ColDailySummary );
                    } else {
                        Toast.makeText ( Summery.this, "No data Found", Toast.LENGTH_SHORT ).show ( );
                    }
//                    System.out.println("" + list_Col_DailySummary);


//                            if (todoCursor.moveToFirst()){
//                                do{
//                                    String data = todoCursor.getString(0);
//                                    System.out.println("DDDD "+ todoCursor.getString(0)+ todoCursor.getString(1)+ todoCursor.getString(2));
//
//                                    // do what ever you want here
//                                }while(todoCursor.moveToNext());
//                            }
                    // Create the Adapter
//                            ColSummery colListAdapter=new ColSummery(Summery.this,todoCursor);

                    // Set The Adapter to ListView
//                            collvItems.setAdapter(colListAdapter);

                }
            } );
            gv_CollectionCummulativeHeader = (GridView) findViewById ( R.id.gridView7 );
            ArrayAdapter <String> ad_ColCummulativeSummaryHeader = new ArrayAdapter <String>
                    ( this, R.layout.grid_text_custom, col_SummaryHeaders );
            gv_CollectionCummulativeHeader.setAdapter ( ad_ColCummulativeSummaryHeader );

            btnDCCum.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View view) {
//                    cvCcum.setVisibility(View.VISIBLE);

                    dbHelper = new DB ( getApplicationContext ( ) );
                    SD = dbHelper.getWritableDatabase ( );
                    gv_CollectionCummulativeSummary = (GridView) findViewById ( R.id.gridView8 );
                    list_ColCummulativeSummary = new ArrayList <String> ( );
                    // Find ListView to populate
                    // Query for items from the database and get a cursor back
                    // Cursor todoCursor = SD.rawQuery("SELECT  * FROM TBL_BILLING", null);
//                    String collectionSum="select col_date, sum(cashcount),sum(cashcol),sum(chqcount) ,sum(chqcol) ,sum(TOTAL) TOTAL,sum(AMOUNT) AMOUNT from (select distinct col_date,cashcount,cashcol,chqcount ,chqcol , cashcount+chqcount TOTAL,cashcol+chqcol AMOUNT  from (select distinct ifnull(A.cnt,0) cashcount,ifnull(A.amt,0) as cashcol,case when A.col_date is null then B.col_date else A.col_date end as col_date,ifnull(B.cnt,0) as chqcount,ifnull(B.amt,0) as chqcol,case when B.col_date is null then A.col_date else B.col_date end as col_date from (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt FROM  TBL_COLMASTER_MP where pymnt_type='C' group by pymnt_type) A  LEFT join  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt FROM  TBL_COLMASTER_MP where pymnt_type='Q' group by pymnt_type) B on A.col_date=B.col_date UNION ALL select  ifnull(B.cnt,0) cashcount,ifnull(B.amt,0) as cashcol,case when B.col_date is null then A.col_date else B.col_date end as col_date,ifnull(A.cnt,0) as chqcount,ifnull(A.amt,0) as chqcol,case when A.col_date is null then B.col_date else A.col_date end as col_date from (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt FROM  TBL_COLMASTER_MP where pymnt_type='Q' group by pymnt_type) A  LEFT join  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt FROM  TBL_COLMASTER_MP where pymnt_type='C' group by pymnt_type) B on A.col_date=B.col_date))";

                    /*
                    Commented On 12/2/2018 in chattisgarh
                    * */
//                    String collectionSum = "SELECT col_date,SUM(cashcount),SUM(cashcol),SUM(chqcount ),SUM(chqcol) ,SUM(TOTAL),SUM(AMOUNT),SUM(CURRENT_AMOUNT),SUM(ARREAR_AMOUNT) FROM (select  col_date,cashcount,cashcol,chqcount ,chqcol ,TOTAL,AMOUNT,SUM(CURRENT_AMOUNT) AS CURRENT_AMOUNT,SUM(ARREAR_AMOUNT) AS ARREAR_AMOUNT  FROM (select distinct col_date,cashcount,cashcol,chqcount ,chqcol ,cashcount+chqcount TOTAL,cashcol+chqcol AMOUNT,CURRENT_AMOUNT,ARREAR_AMOUNT from (select  ifnull(A1.CURRENT_AMOUNT,0) CURRENT_AMOUNT,ifnull(A1.ARREAR_AMOUNT,0) ARREAR_AMOUNT,ifnull(A1.cnt,0) cashcount,ifnull(A1.amt,0) as cashcol, ( CASE WHEN A1.col_date is null then B1.col_date else A1.col_date END) as col_date, ifnull(B1.cnt,0) as chqcount,ifnull(B1.amt,0) as chqcol,( CASE WHEN B1.col_date is null then A1.col_date else B1.col_date END) as col_date  from  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM  (SELECT  pymnt_type,col_date,CURRENT_AMOUNT,AMOUNT,AMOUNT-CURRENT_AMOUNT AS arrear_amount FROM (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(cast(B.CUR_TOT_BILL as real)),( CASE WHEN MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real)))<0 THEN 0 ELSE  MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real))) END) AS CURRENT_AMOUNT,cast(A.AMOUNT as real) AS AMOUNT FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO) ) where pymnt_type='C' group by pymnt_type,col_date) A1  LEFT join  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT   FROM   (SELECT  pymnt_type,col_date,CURRENT_AMOUNT,AMOUNT,AMOUNT-CURRENT_AMOUNT AS arrear_amount FROM (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(cast(B.CUR_TOT_BILL as real)),( CASE WHEN MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real)))<0 THEN 0 ELSE  MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real))) END) AS CURRENT_AMOUNT,cast(A.AMOUNT as real) AS AMOUNT FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO)) where pymnt_type!='C' group by pymnt_type,col_date) B1   on A1.col_date=B1.col_date UNION ALL select  ifnull(A2.CURRENT_AMOUNT,0) CURRENT_AMOUNT,ifnull(A2.ARREAR_AMOUNT,0) ARREAR_AMOUNT,ifnull(B2.cnt,0) cashcount, ifnull(B2.amt,0) as cashcol,( CASE WHEN B2.col_date is null then A2.col_date else B2.col_date END) as col_date, ifnull(A2.cnt,0) as chqcount,ifnull(A2.amt,0) as chqcol,( CASE WHEN A2.col_date is null then B2.col_date else A2.col_date END) as col_date from  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM  (SELECT  pymnt_type,col_date,CURRENT_AMOUNT,AMOUNT,AMOUNT-CURRENT_AMOUNT AS arrear_amount FROM (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(cast(B.CUR_TOT_BILL as real)),( CASE WHEN MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real)))<0 THEN 0 ELSE  MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real))) END) AS CURRENT_AMOUNT,cast(A.AMOUNT as real) AS AMOUNT FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO) ) where pymnt_type!='C' group by pymnt_type,col_date) A2   LEFT join  (select count(*)  as cnt,pymnt_type,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM   (SELECT  pymnt_type,col_date,CURRENT_AMOUNT,AMOUNT,AMOUNT-CURRENT_AMOUNT AS arrear_amount FROM (select a.pymnt_type as pymnt_type,A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(cast(B.CUR_TOT_BILL as real)),( CASE WHEN MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real)))<0 THEN 0 ELSE  MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real))) END) AS CURRENT_AMOUNT,cast(A.AMOUNT as real) AS AMOUNT FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO)) where pymnt_type='C' group by pymnt_type,col_date)   B2 on A2.col_date=B2.col_date ) order by col_date desc  ) GROUP BY  col_date,cashcount,cashcol,chqcount ,chqcol ,TOTAL,AMOUNT  order by col_date Asc)";

//                    String collectionSum = "SELECT col_date,SUM(cashcount),SUM(cashcol),SUM(chqcount ),SUM(chqcol) ,SUM(TOTAL),SUM(AMOUNT),SUM(CURRENT_AMOUNT),SUM(ARREAR_AMOUNT) FROM (select  col_date,cashcount,cashcol,chqcount ,chqcol ,TOTAL,AMOUNT,SUM(CURRENT_AMOUNT) AS CURRENT_AMOUNT,SUM(ARREAR_AMOUNT) AS ARREAR_AMOUNT  FROM (select distinct col_date,cashcount,cashcol,chqcount ,chqcol ,cashcount+chqcount TOTAL,cashcol+chqcol AMOUNT,CURRENT_AMOUNT,ARREAR_AMOUNT from (select  ifnull(A1.CURRENT_AMOUNT,0) CURRENT_AMOUNT,ifnull(A1.ARREAR_AMOUNT,0) ARREAR_AMOUNT,ifnull(A1.cnt,0) cashcount,ifnull(A1.amt,0) as cashcol, ( CASE WHEN A1.col_date is null then B1.col_date else A1.col_date END) as col_date, ifnull(B1.cnt,0) as chqcount,ifnull(B1.amt,0) as chqcol,( CASE WHEN B1.col_date is null then A1.col_date else B1.col_date END) as col_date  from  (select count(*)  as cnt,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM  (SELECT  col_date,CURRENT_AMOUNT,AMOUNT,AMOUNT-CURRENT_AMOUNT AS arrear_amount FROM (select A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(cast(B.CUR_TOT_BILL as real)),( CASE WHEN MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real)))<0 THEN 0 ELSE  MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real))) END) AS CURRENT_AMOUNT,cast(A.AMOUNT as real) AS AMOUNT FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO and pymnt_type='C') ) group by col_date) A1  LEFT join  (select count(*)  as cnt,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT   FROM   (SELECT  col_date,CURRENT_AMOUNT,AMOUNT,AMOUNT-CURRENT_AMOUNT AS arrear_amount FROM (select A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(cast(B.CUR_TOT_BILL as real)),( CASE WHEN MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real)))<0 THEN 0 ELSE  MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real))) END) AS CURRENT_AMOUNT,cast(A.AMOUNT as real) AS AMOUNT FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO and pymnt_type!='C')) group by col_date) B1   on A1.col_date=B1.col_date UNION ALL select  ifnull(A2.CURRENT_AMOUNT,0) CURRENT_AMOUNT,ifnull(A2.ARREAR_AMOUNT,0) ARREAR_AMOUNT,ifnull(B2.cnt,0) cashcount, ifnull(B2.amt,0) as cashcol,( CASE WHEN B2.col_date is null then A2.col_date else B2.col_date END) as col_date, ifnull(A2.cnt,0) as chqcount,ifnull(A2.amt,0) as chqcol,( CASE WHEN A2.col_date is null then B2.col_date else A2.col_date END) as col_date from  (select count(*)  as cnt,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM  (SELECT  col_date,CURRENT_AMOUNT,AMOUNT,AMOUNT-CURRENT_AMOUNT AS arrear_amount FROM (select A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(cast(B.CUR_TOT_BILL as real)),( CASE WHEN MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real)))<0 THEN 0 ELSE  MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real))) END) AS CURRENT_AMOUNT,cast(A.AMOUNT as real) AS AMOUNT FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO and pymnt_type!='C') ) group by col_date) A2   LEFT join  (select count(*)  as cnt,col_date ,sum(amount) as amt,sum(CURRENT_AMOUNT) as CURRENT_AMOUNT,sum(ARREAR_AMOUNT) as ARREAR_AMOUNT FROM   (SELECT  col_date,CURRENT_AMOUNT,AMOUNT,AMOUNT-CURRENT_AMOUNT AS arrear_amount FROM (select A.col_date ,round(B.AMNT_AFT_RBT_DATE),B.AMNT_BFR_RBT_DATE,ROUND(cast(B.CUR_TOT_BILL as real)),( CASE WHEN MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real)))<0 THEN 0 ELSE  MIN(cast(cast(A.AMOUNT as real) as real),ROUND(cast(B.CUR_TOT_BILL as real))) END) AS CURRENT_AMOUNT,cast(A.AMOUNT as real) AS AMOUNT FROM  TBL_COLMASTER_MP A,tbl_collection B where  A.CON_NO=B.CON_NO and pymnt_type='C')) group by col_date)   B2 on A2.col_date=B2.col_date ) order by col_date desc  ) GROUP BY  col_date,cashcount,cashcol,chqcount ,chqcol ,TOTAL,AMOUNT  order by col_date Asc)";
                    String collectionSum = "SELECT max(col_date) AS col_date,sum(cashcount)cashcount, sum(cashcol) as cashcol, sum(chqcount) as chqcount, sum(chqcol)as chqcol, sum(cashcount) + sum(chqcount) total, sum(cashcol) + sum(chqcol) amount FROM (  SELECT COUNT(*) AS cashcount, SUM(0) AS chqcount, col_date, SUM(amount) AS cashcol, SUM(0) AS chqcol  FROM ( SELECT a.col_date, a.amount AS amount FROM  tbl_colmaster_mp a WHERE a. pymnt_type = 'C'  ) group  by  col_date UNION ALL SELECT SUM(0) AS cashcount, COUNT(*) AS chqcount,  col_date, SUM(0) AS cashcol, SUM(amount) AS chqcol FROM ( SELECT a.col_date, a.amount AS amount FROM tbl_colmaster_mp a WHERE a.pymnt_type != 'C'   ) group  by  col_date ) ";

                    Cursor todoCursor = SD.rawQuery ( collectionSum, null );
                    if (todoCursor != null && todoCursor.moveToFirst ( )) {
                        do {

                            if (todoCursor.getString ( 1 ) != null && !todoCursor.getString ( 1 ).isEmpty
                                    ( )) {
                                String zero = todoCursor.getString ( 0 );
                                list_ColCummulativeSummary.add ( zero ); // date

                                String one = todoCursor.getString ( 1 );
                                list_ColCummulativeSummary.add ( one ); // Cash

                                String two = todoCursor.getString ( 2 );
                                list_ColCummulativeSummary.add ( two ); // Amount

                                String three = todoCursor.getString ( 3 );
                                list_ColCummulativeSummary.add ( three ); // Cheque / DD

                                String four = todoCursor.getString ( 4 );
                                BigDecimal bigDecimal = new BigDecimal ( four );
                                @SuppressLint("DefaultLocale")
                                String finalStr = String.format ( "%.0f", bigDecimal );
                                list_ColCummulativeSummary.add ( finalStr ); // Amount

                                String five = todoCursor.getString ( 5 );
                                list_ColCummulativeSummary.add ( five ); // Total

                                String six = todoCursor.getString ( 6 );
                                BigDecimal bigDecimal1 = new BigDecimal ( six );
                                @SuppressLint("DefaultLocale")
                                String finalStr1 = String.format ( "%.0f", bigDecimal1 );
                                list_ColCummulativeSummary.add ( finalStr1 ); // Amount
                            } else {
                                Toast.makeText ( Summery.this, "No Data Found", Toast.LENGTH_SHORT ).show ( );
                            }

//                            String seven = todoCursor.getString(7);
//                            list_ColCummulativeSummary.add(seven);
//
//
//                            String eight = todoCursor.getString(8);
//                            list_ColCummulativeSummary.add(eight);
                        }
                        while (todoCursor.moveToNext ( ));
                        ArrayAdapter <String> ad_ColCummulativeSummary = new ArrayAdapter <String>
                                ( getApplicationContext ( ), R.layout.grid_view, list_ColCummulativeSummary );
                        gv_CollectionCummulativeSummary.setAdapter ( ad_ColCummulativeSummary );
                    } else {
                        Toast.makeText ( Summery.this, "No data Found", Toast.LENGTH_SHORT ).show ( );
                    }
//                    System.out.println("" + list_ColCummulativeSummary);

                    // Create the Adapter
                    // ColSummery CumAda=new ColSummery(Summery.this,todoCursor);

                    // Set The Adapter to ListView
//                    colCUMlist.setAdapter(CumAda);
//                    colCUMlist.setAdapter(CumAda);
                }
            } );
        } else {


            setContentView ( R.layout.activity_summery_replace );
            btnDailySum = (Button) findViewById ( R.id.btnDSR );
            btnDailyCum = (Button) findViewById ( R.id.btnCR );
            gv_DailySummaryHeaderReplace = (GridView) findViewById ( R.id.gridView_DS_Header );
            ArrayAdapter <String> headerAdapter = new ArrayAdapter <String>
                    ( this, R.layout.grid_view_header, dailySummaryHeadersReplace );
            gv_DailySummaryHeaderReplace.setAdapter ( headerAdapter );

            btnDailySum.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View view) {
                    dbHelper = new DB ( getApplicationContext ( ) );
                    SD = dbHelper.getWritableDatabase ( );
                    gv_DailySummary = (GridView) findViewById ( R.id.gridView1 );
                    list_DailySummary = new ArrayList <String> ( );
                    String dailySummary = "SELECT  count(*),REPLACEMENT_DATE from  (SELECT DISTINCT CONSUMERNO,REPLACEMENT_DATE FROM TBL_METER_REPLACEMENT) GROUP BY REPLACEMENT_DATE ORDER BY REPLACEMENT_DATE DESC";
                    Cursor todoCursor = SD.rawQuery ( dailySummary, null );
                    System.out.println ( "Select Result : " + dailySummary );

                    if (todoCursor != null && todoCursor.moveToFirst ( )) {
                        do {
                            String billDate = todoCursor.getString ( 1 );
                            list_DailySummary.add ( billDate );
                            String billedConsumers = todoCursor.getString ( 0 );
                            list_DailySummary.add ( billedConsumers );
//                            String billedUnits = todoCursor.getString(2);
//                            list_DailySummary.add(billedUnits);
//                            String billedAmount = todoCursor.getString(3);
//                            list_DailySummary.add(billedAmount);
                        }
                        while (todoCursor.moveToNext ( ));
                        ArrayAdapter <String> ar_DailySummary = new ArrayAdapter <String>
                                ( getApplicationContext ( ), R.layout.grid_view_replace, list_DailySummary );
                        gv_DailySummary.setAdapter ( ar_DailySummary );

                    } else {
                        Toast.makeText ( Summery.this, "No Data Found", Toast.LENGTH_SHORT ).show ( );
                    }
                }
            } );
            gv_CummulativeHeaderReplace = (GridView) findViewById ( R.id.gridView2 );
            ArrayAdapter <String> cummulativeHeader = new ArrayAdapter <String>
                    ( this, R.layout.grid_view_header, cummulativeHeadersReplace );

            gv_CummulativeHeaderReplace.setAdapter ( cummulativeHeader );

            list_CummulativeRepSummary = new ArrayList <String> ( );

            btnDailyCum.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View view) {
                    dbHelper = new DB ( getApplicationContext ( ) );
                    SD = dbHelper.getWritableDatabase ( );
                    gv_DailySummary = (GridView) findViewById ( R.id.gridView1 );

                    list_DailySummary = new ArrayList <String> ( );
                    String dailySummary = "SELECT  count(*),REPLACEMENT_DATE from  (SELECT DISTINCT CONSUMERNO,REPLACEMENT_DATE FROM TBL_METER_REPLACEMENT)";
                    Cursor todoCursor = SD.rawQuery ( dailySummary, null );
                    System.out.println ( "Select Result : " + dailySummary );

                    if (todoCursor != null && todoCursor.moveToFirst ( )) {
                        do {
                            if (todoCursor.getString ( 1 ) != null && !todoCursor.getString ( 1 ).isEmpty
                                    ( )) {
                                String billDate = todoCursor.getString ( 1 );
                                list_DailySummary.add ( billDate );
                                String billedConsumers = todoCursor.getString ( 0 );
                                list_DailySummary.add ( billedConsumers );
                            } else {
                                Toast.makeText ( Summery.this, "No Data Found", Toast.LENGTH_SHORT ).show ( );
                            }

//                            String billedUnits = todoCursor.getString(2);
//                            list_DailySummary.add(billedUnits);
//                            String billedAmount = todoCursor.getString(3);
//                            list_DailySummary.add(billedAmount);

                        }
                        while (todoCursor.moveToNext ( ));
                        ArrayAdapter <String> ar_DailySummary = new ArrayAdapter <String>
                                ( getApplicationContext ( ), R.layout.grid_view_replace, list_DailySummary );
                        gv_DailySummary.setAdapter ( ar_DailySummary );

                    } else {
                        Toast.makeText ( Summery.this, "No Data Found", Toast.LENGTH_SHORT ).show ( );
                    }
                }
            } );
           /*
            btnDailyCum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dbHelper = new DB(getApplicationContext());
                    SD = dbHelper.getWritableDatabase();
                    gv_CummulativeSummary = (GridView) findViewById(R.id.gridView3);

                    String cummulativeSummary = "SELECT  count(*),REPLACEMENT_DATE from  (SELECT DISTINCT CONSUMERNO,REPLACEMENT_DATE FROM TBL_METER_REPLACEMENT)";
                    Cursor todoCursor = SD.rawQuery(cummulativeSummary, null);

                    if (todoCursor != null && todoCursor.moveToFirst()) {
                        do {
                            String billDate = todoCursor.getString(1);
                            list_CummulativeRepSummary.add(billDate);
                            String billedConsumers = todoCursor.getString(0);
                            list_CummulativeRepSummary.add(billedConsumers);

                        }
                        while (todoCursor.moveToNext());

                        ArrayAdapter<String> ad_CummulativeSummary = new ArrayAdapter<String>
                                (getApplicationContext(), R.layout.grid_view_replace, list_CummulativeRepSummary);
                        gv_CummulativeSummary.setAdapter(ad_CummulativeSummary);

                    } else {

                        Toast.makeText(Summery.this, "No Data Found", Toast.LENGTH_SHORT).show();
                    }

                }
            });*/
        }

//        Calendar cal = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//        strDate = sdf.format(cal.getTime());
//
//
//
//
//        final ListView list=(ListView) findViewById(R.id.listView1);
/////////////////////////////
//        summerycol = new HashMap<>();
//
//        for(int i=0 ;i<= 50 ; i++) {
////            li.clear();
////            summerycol.clear();
//            li=new ArrayList<String>();
//            li.add("List 1");
//            li.add("List 2");
//            li.add("List 3");
//            li.add("List 4");
//            li.add("List 5");
//            li.add("List 6");
//            li.add("List 7");
//            summerycol.put("key"+i, li);
////            summerycol.put("List 1"+i, li);
////            summerycol.put("key", li);
////            System.out.println("aaaa "+summerycol);
////            ColSummery customAdapter = new ColSummery(this, summerycol);
////        list.setAdapter(customAdapter);
////            System.out.println("aaaa "+li);
//        }
//
//        System.out.println("bbb "+summerycol);
////        System.out.println("aaaa "+li);
//        ColSummery customAdapter = new ColSummery(this, summerycol);
//        list.setAdapter(customAdapter);
//
/////////////////////////////
//
//        System.out.println("Current date in String Format: " + strDate);
//
////        new Dcs().execute();
//
////        tvCash = (TextView) findViewById(R.id.tvcash);
////        tvCheque = (TextView) findViewById(R.id.tvcheque);
////        tvNoCheque = (TextView) findViewById(R.id.tvnoche);
////        tvNoDD = (TextView) findViewById(R.id.tvnodd);
////        tvTotAmnt = (TextView) findViewById(R.id.tvtotamnt);
////
////        Button dialogButton = (Button) findViewById(R.id.print);
////        // if button is clicked, close the custom dialog
////
////        /****DIALOUGE BOX BUTTON INTIALISATION AND EVENT ADDING****/
////        dialogButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////
////
////            }
////        });

    }

    private void openAndQueryDatabase() {
        try {
            dbHelper = new DB ( getApplicationContext ( ) );
            SD = dbHelper.getWritableDatabase ( );
//            DBHelper dbHelper = new DBHelper(this.getApplicationContext());
//            newDB = dbHelper.getWritableDatabase();
            Cursor c = SD.rawQuery ( "SELECT STATUS FROM TBL_METERSTATUSCODE_MP", null );

            if (c != null) {
                if (c.moveToFirst ( )) {
                    do {
                        String status = c.getString ( c.getColumnIndex ( "STATUS" ) );
                        System.out.print ( "STATSUS IS " + status );
//                        int age = c.getInt(c.getColumnIndex("Age"));
                        results.add ( status );
                    } while (c.moveToNext ( ));
                }
            }
        } catch (SQLiteException se) {
            Log.e ( getApplicationContext ( ), "", "Could not create or Open the database" );
        } finally {
//            if (newDB != null)
//                newDB.execSQL("DELETE FROM " + tableName);
            SD.close ( );
        }

    }

    class BillSumRep extends AsyncTask <String, Integer, String> {
        BillingSummery handler;

        // Show Progress bar before downloading Music
        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
//        pDialog = showDialog();
            //            pDialogFile= showDialogFile();
            sDialog = new SweetAlertDialog ( Summery.this, SweetAlertDialog.PROGRESS_TYPE );
            sDialog.getProgressHelper ( ).setBarColor ( Color.parseColor ( "#A5DC86" ) );
            sDialog.setTitleText ( "Downloading File" );
            sDialog.setCancelable ( false );
            sDialog.show ( );
            Log.d ( getApplicationContext ( ), "TAG", " is cancelled " + isCancelled ( ) );

            super.onPreExecute ( );

        }

        // Download Music File from Internet
        // This is run in a background thread
        @Override
        protected String doInBackground(String... f_url) {
            //            ---------------------------------------------------------------------by me
            dbHelper = new DB ( getApplicationContext ( ) );
            SD = dbHelper.getWritableDatabase ( );
            // Find ListView to populate

            // Query for items from the database and get a cursor back
            Cursor todoCursor = SD.rawQuery ( "SELECT  * FROM TBL_BILLING", null );
//            Cursor todoCursor = SD.rawQuery("SELECT  count(*),Bill_Date,sum(Units_Consumed),sum(Cur_Bill_Total) from  TBL_BILLING GROUP BY BILL_DATE ORDER BY BILL_DATE", null);
            // TodoDatabaseHandler is a SQLiteOpenHelper class connecting to SQLite
            handler = new BillingSummery ( getApplicationContext ( ), todoCursor, 90 );
            Summery.this.runOnUiThread ( new Runnable ( ) {

                @Override
                public void run() {
                    // Attach cursor adapter to the ListView
                    lvItems.setAdapter ( handler );
                }
            } );

            return null;
        }

        /**
         * Updating progress bar
         */// This is called from background thread but runs in UI
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate ( values );
//        pDialog.setProgress(values);
            sDialog.setTitleText ( "progress " + values[0] );
        }

        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String file_url) {
            sDialog.dismiss ( );

            Toast.makeText ( getApplicationContext ( ), "Downloaded Successfully . Reading starteds", Toast.LENGTH_LONG ).show ( );

        }
    }

    class Dcs extends AsyncTask <String, Integer, String> {

        // Show Progress bar before downloading Music
        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
//        pDialog = showDialog();
            //            pDialogFile= showDialogFile();
            sDialog = new SweetAlertDialog ( Summery.this, SweetAlertDialog.PROGRESS_TYPE );
            sDialog.getProgressHelper ( ).setBarColor ( Color.parseColor ( "#A5DC86" ) );
            sDialog.setTitleText ( "Downloading File" );
            sDialog.setCancelable ( false );
            sDialog.show ( );
            Log.d ( getApplicationContext ( ), "TAG", " is cancelled " + isCancelled ( ) );

            super.onPreExecute ( );

        }

        // Download Music File from Internet
        // This is run in a background thread
        @Override
        protected String doInBackground(String... f_url) {
            //            ---------------------------------------------------------------------by me
            dbHelper = new DB ( getApplicationContext ( ) );
            SD = dbHelper.getWritableDatabase ( );

            /****RAW QUERY EXICUTION AND STORING IN CURSOR****/
            String countCheque = "SELECT COUNT(*) FROM TBL_COLMASTER_MP WHERE PAYMNT_MODE='Cheque'";
            String countTotCash = "select sum(AMOUNT) as AMOUNT, PAYMNT_MODE from TBL_COLMASTER_MP where COL_DATE= '" + strDate + "'  group by PAYMNT_MODE";
            String countTotCheque = "select sum(AMOUNT) as AMOUNT, PAYMNT_MODE from TBL_COLMASTER_MP where COL_DATE= '" + strDate + "'  and PAYMNT_MODE='Cheque' group by PAYMNT_MODE";
            String curTotAMNT = "select sum(Amount) as Amount, PAYMNT_MODE from TBL_COLMASTER_MP where COL_DATE ='" + strDate + "' ";

            Cursor curCountCheque = SD.rawQuery ( countCheque, null );
            Cursor curCountTotCash = SD.rawQuery ( countTotCash, null );
            Cursor curCountTotCheque = SD.rawQuery ( countTotCheque, null );
            Cursor curCountTot = SD.rawQuery ( curTotAMNT, null );

            /****GETTING DATA FROM CURSOR WHEN CURSOR IS NOT NULL AND ON THE FORST INDEX****/
            /*if (curCountCheque != null && curCountCheque.moveToFirst()) {
                tvNoCheque.setText(curCountCheque.getString(0));
            }
            if (curCountTotCash != null && curCountTotCash.moveToFirst()) {
                tvCash.setText(curCountTotCash.getString(0));
            }
            if (curCountTotCheque != null && curCountTotCheque.moveToFirst()) {
                tvCheque.setText(curCountTotCheque.getString(0));
            }
            if (curCountTot != null && curCountTot.moveToFirst()) {
                System.out.print("couttotal " + curCountTot.getString(0));
                tvTotAmnt.setText(curCountTot.getString(0));

            }*/

            return null;
        }

        /**
         * Updating progress bar
         */// This is called from background thread but runs in UI
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate ( values );
//        pDialog.setProgress(values);
            sDialog.setTitleText ( "progress " + values[0] );
        }

        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String file_url) {
            sDialog.dismiss ( );

            Toast.makeText ( getApplicationContext ( ), "Downloaded Successfully . Reading starteds", Toast.LENGTH_LONG ).show ( );

        }
    }
}
