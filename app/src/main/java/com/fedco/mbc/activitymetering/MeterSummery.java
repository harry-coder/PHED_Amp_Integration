package com.fedco.mbc.activitymetering;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.fedco.mbc.Adapter.BillingSummery;
import com.fedco.mbc.R;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.sqlite.DB;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MeterSummery extends AppCompatActivity {

    public SweetAlertDialog sDialog;
    SQLiteDatabase SD;
    DB dbHelper;
    String strDate;
    private ArrayList<String> results = new ArrayList<String>();
    BillingSummery handler;
    String divisionpre,subdivisionpre,sectionpre,cyclepre;

    ArrayList<String> li, list_DailySummary, list_CummulativeSummary;
    ListView lvItems, collvItems, colCUMlist;
    Button btnDailySum, btnDailyCum, btnDCSum, btnDCCum;
    Logger Log;

    static final String[] dailySummaryHeaders = new String[]{
            "Date", "Total Consumer", "ACT Count", "HLK Count", "AVG Count"
    };
    static final String[] cummulativeHeaders = new String[]{
            "Date", "Total Consumer", "ACT Count",  "HLK Count", "AVG Count"
    };
    GridView gv_DailySummaryHeader1,gv_DailySummaryHeader, gv_DailySummary,
            gv_CummulativeHeader, gv_CummulativeSummary  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summerymetering);

        btnDailySum = (Button) findViewById(R.id.btnDSR);
        btnDailyCum = (Button) findViewById(R.id.btnCR);

        gv_DailySummaryHeader = (GridView) findViewById(R.id.gridView_DS_Header);
        ArrayAdapter<String> headerAdapter = new ArrayAdapter<String>
                (this, R.layout.grid_text_custom, dailySummaryHeaders);
        gv_DailySummaryHeader.setAdapter(headerAdapter);

//        gv_DailySummaryHeader1 = (GridView) findViewById(R.id.gridView_DS_Header_HEader);
//        ArrayAdapter<String> headerAdapter1 = new ArrayAdapter<String>
//                (this, R.layout.grid_text_custom, dailySummaryHeaders1);
//        gv_DailySummaryHeader1.setAdapter(headerAdapter1);

        divisionpre = getIntent().getStringExtra("division");
        subdivisionpre = getIntent().getStringExtra("subdivsion");
      //  sectionpre = getIntent().getStringExtra("section");
      //  cyclepre = getIntent().getStringExtra("cycle");

        btnDailySum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dbHelper = new DB(getApplicationContext());
                SD = dbHelper.getWritableDatabase();

                gv_DailySummary = (GridView) findViewById(R.id.gridView1);
                list_DailySummary = new ArrayList<String>();
                String strWhereCondition=" ";
                if(divisionpre!=null)
                    strWhereCondition = "WHERE  S11.DIVISION ='"+divisionpre+"' ";
                if(subdivisionpre!=null)
                    strWhereCondition += " and S11.SUBDIVISION ='"+subdivisionpre+"' ";
                if(sectionpre!=null)
                    strWhereCondition += " and S11.SECTION ='"+sectionpre+"' ";
                if(cyclepre!=null)
                    strWhereCondition += " and S11.CYCLE ='"+cyclepre+"' ";
                // String dailySummary = "SELECT  count(*),Bill_Date,sum(Units_Consumed),sum(Cur_Bill_Total) from  TBL_BILLING GROUP BY BILL_DATE ORDER BY BILL_DATE DESC";
//                String dailySummary = "select S1.METERREADDATE,S1.DIVISION,S1.SUBDIVISION,S1.SECTION,S1.CYCLE,ifnull(S3.cnt,0) ACT_COUNT,ifnull(S3.consumption,0) ACT_CONSUMPTION,ifnull(S4.cnt,0) AVG_COUNT,ifnull(S4.consumption,0) AVG_CONSUMPTION,ifnull(S5.cnt,0) NM_COUNT,ifnull(S5.consumption,0) NM_CONSUMPTION,ifnull(S6.cnt,0) HLK_COUNT,ifnull(S6.consumption,0) HLK_CONSUMPTION,ifnull(S3.cnt,0)+ifnull(S4.cnt,0)+ifnull(S5.cnt,0)+ifnull(S6.cnt,0) Total_Count,ifnull(S3.consumption,0)+ifnull(S4.consumption,0)+ifnull(S5.consumption,0)+ifnull(S6.consumption,0)   Total_Consumption from tbl_meterupload S1 left outer join (select  a.METERREADDATE,count(*) cnt,sum(CONSUMPKWH) consumption,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS,a.CYCLE from tbl_meterupload a,tbl_meterstatuscode b where trim(a.currentmeterstatus)=trim(b.state_id) group by a.METERREADDATE,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS) s3 on (s1.METERREADDATE=s3.METERREADDATE and S1.DIVISION = S3.DIVISION and S1.SUBDIVISION =S3.SUBDIVISION and S1.SECTION = S3.SECTION  and S1.CYCLE=S3.CYCLE and s3.BILL_BASIS='ACT') left outer join (select  a.METERREADDATE,count(*) cnt,sum(CONSUMPKWH) consumption,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS,a.CYCLE  from tbl_meterupload a,tbl_meterstatuscode b where trim(a.currentmeterstatus)=trim(b.state_id) group by a.METERREADDATE,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS) s4 on  (s1.METERREADDATE=s4.METERREADDATE and S1.DIVISION = S4.DIVISION and S1.SUBDIVISION =S4.SUBDIVISION and S1.SECTION = S4.SECTION  and S1.CYCLE=S4.CYCLE and s4.BILL_BASIS='AVG')  left outer join (select  a.METERREADDATE,count(*) cnt,sum(CONSUMPKWH) consumption,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS,a.CYCLE  from tbl_meterupload a,tbl_meterstatuscode b where trim(a.currentmeterstatus)=trim(b.state_id) group by a.METERREADDATE,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS) s5 on (s1.METERREADDATE=s5.METERREADDATE and S1.DIVISION = S5.DIVISION and S1.SUBDIVISION =S5.SUBDIVISION and S1.SECTION = S5.SECTION  and S1.CYCLE=S5.CYCLE and s5.BILL_BASIS='NM') left outer join (select  a.METERREADDATE,count(*) cnt,sum(CONSUMPKWH) consumption,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS,a.CYCLE  from tbl_meterupload a,tbl_meterstatuscode b where trim(a.currentmeterstatus)=trim(b.state_id) group by a.METERREADDATE,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS) s6 on (s1.METERREADDATE=s6.METERREADDATE and S1.DIVISION = S6.DIVISION and S1.SUBDIVISION =S6.SUBDIVISION and S1.SECTION = S6.SECTION  and S1.CYCLE=S5.CYCLE and  s6.BILL_BASIS='HLK')   "+ strWhereCondition +" group by S1.METERREADDATE";//,S1.DIVISION,S1.SUBDIVISION,S1.SECTION,S1.CYCLE";
               // String dailySummary = "select S1.METERREADDATE,S1.DIVISION,S1.SUBDIVISION,S1.SECTION,S1.CYCLE,ifnull(S3.cnt,0) ACT_COUNT,ifnull(S3.consumption,0) ACT_CONSUMPTION,ifnull(S4.cnt,0) AVG_COUNT,ifnull(S4.consumption,0) AVG_CONSUMPTION,ifnull(S5.cnt,0) NM_COUNT,ifnull(S5.consumption,0) NM_CONSUMPTION,ifnull(S6.cnt,0) HLK_COUNT,ifnull(S6.consumption,0) HLK_CONSUMPTION,ifnull(S3.cnt,0)+ifnull(S4.cnt,0)+ifnull(S5.cnt,0)+ifnull(S6.cnt,0) Total_Count,ifnull(S3.consumption,0)+ifnull(S4.consumption,0)+ifnull(S5.consumption,0)+ifnull(S6.consumption,0)   Total_Consumption from tbl_meterupload S1 left outer join (select  a.METERREADDATE,count(*) cnt,sum(CONSUMPKWH) consumption,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS,a.CYCLE from tbl_meterupload a,tbl_meterstatuscode b where trim(a.currentmeterstatus)=trim(b.state_id) group by a.METERREADDATE,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS) s3 on (s1.METERREADDATE=s3.METERREADDATE and S1.DIVISION = S3.DIVISION and S1.SUBDIVISION =S3.SUBDIVISION and S1.SECTION = S3.SECTION  and S1.CYCLE=S3.CYCLE and s3.BILL_BASIS='ACT') left outer join (select  a.METERREADDATE,count(*) cnt,sum(CONSUMPKWH) consumption,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS,a.CYCLE  from tbl_meterupload a,tbl_meterstatuscode b where trim(a.currentmeterstatus)=trim(b.state_id) group by a.METERREADDATE,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS) s4 on  (s1.METERREADDATE=s4.METERREADDATE and S1.DIVISION = S4.DIVISION and S1.SUBDIVISION =S4.SUBDIVISION and S1.SECTION = S4.SECTION  and S1.CYCLE=S4.CYCLE and s4.BILL_BASIS='AVG')  left outer join (select  a.METERREADDATE,count(*) cnt,sum(CONSUMPKWH) consumption,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS,a.CYCLE  from tbl_meterupload a,tbl_meterstatuscode b where trim(a.currentmeterstatus)=trim(b.state_id) group by a.METERREADDATE,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS) s5 on (s1.METERREADDATE=s5.METERREADDATE and S1.DIVISION = S5.DIVISION and S1.SUBDIVISION =S5.SUBDIVISION and S1.SECTION = S5.SECTION  and S1.CYCLE=S5.CYCLE and s5.BILL_BASIS='NM') left outer join (select  a.METERREADDATE,count(*) cnt,sum(CONSUMPKWH) consumption,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS,a.CYCLE  from tbl_meterupload a,tbl_meterstatuscode b where trim(a.currentmeterstatus)=trim(b.state_id) group by a.METERREADDATE,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS) s6 on (s1.METERREADDATE=s6.METERREADDATE and S1.DIVISION = S6.DIVISION and S1.SUBDIVISION =S6.SUBDIVISION and S1.SECTION = S6.SECTION  and S1.CYCLE=S5.CYCLE and  s6.BILL_BASIS='HLK') "+ strWhereCondition+" group by S1.METERREADDATE ";//,S1.DIVISION,S1.SUBDIVISION,S1.SECTION,S1.CYCLE";
                String dailySummary = " select SUBSTR(METERREADDATE,1,10),sum(ACT_COUNT),sum(ACT_CONSUMPTION),sum(AVG_COUNT),sum(AVG_CONSUMPTION),sum(NM_COUNT),sum(NM_CONSUMPTION),sum(HLK_COUNT),sum(HLK_CONSUMPTION),sum(Total_Count),sum(Total_Consumption) from  (select S1.METERREADDATE,S1.DIVISION,S1.SUBDIVISION,S1.SECTION,S1.CYCLE,ifnull(S3.cnt,0) ACT_COUNT,ifnull(S3.consumption,0) ACT_CONSUMPTION,ifnull(S4.cnt,0)+ifnull(S5.cnt,0) AVG_COUNT,ifnull(S4.consumption,0) AVG_CONSUMPTION,ifnull(S5.cnt,0) NM_COUNT,ifnull(S5.consumption,0) NM_CONSUMPTION,ifnull(S6.cnt,0) HLK_COUNT,ifnull(S6.consumption,0) HLK_CONSUMPTION,ifnull(S3.cnt,0)+ifnull(S4.cnt,0)+ifnull(S5.cnt,0)+ifnull(S6.cnt,0) Total_Count,ifnull(S3.consumption,0)+ifnull(S4.consumption,0)+ifnull(S5.consumption,0)+ifnull(S6.consumption,0)   Total_Consumption from tbl_meterupload S1 left outer join (select  a.METERREADDATE,count(*) cnt,sum(CONSUMPKWH) consumption,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS,a.CYCLE from tbl_meterupload a,tbl_meterstatuscode b where trim(a.currentmeterstatus)=trim(b.state_id) group by a.METERREADDATE,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS) s3 on (s1.METERREADDATE=s3.METERREADDATE and S1.DIVISION = S3.DIVISION and S1.SUBDIVISION =S3.SUBDIVISION and S1.SECTION = S3.SECTION  and S1.CYCLE=S3.CYCLE and s3.BILL_BASIS='ACT') left outer join (select  a.METERREADDATE,count(*) cnt,sum(CONSUMPKWH) consumption,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS,a.CYCLE  from tbl_meterupload a,tbl_meterstatuscode b where trim(a.currentmeterstatus)=trim(b.state_id) group by a.METERREADDATE,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS) s4 on  (s1.METERREADDATE=s4.METERREADDATE and S1.DIVISION = S4.DIVISION and S1.SUBDIVISION =S4.SUBDIVISION and S1.SECTION = S4.SECTION  and S1.CYCLE=S4.CYCLE and s4.BILL_BASIS='AVG') left outer join (select  a.METERREADDATE,count(*) cnt,sum(CONSUMPKWH) consumption,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS,a.CYCLE  from tbl_meterupload a,tbl_meterstatuscode b where trim(a.currentmeterstatus)=trim(b.state_id) group by a.METERREADDATE,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS) s5 on (s1.METERREADDATE=s5.METERREADDATE and S1.DIVISION = S5.DIVISION and S1.SUBDIVISION =S5.SUBDIVISION and S1.SECTION = S5.SECTION  and S1.CYCLE=S5.CYCLE and s5.BILL_BASIS='NM') left outer join (select  a.METERREADDATE,count(*) cnt,sum(CONSUMPKWH) consumption,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS,a.CYCLE  from tbl_meterupload a,tbl_meterstatuscode b where trim(a.currentmeterstatus)=trim(b.state_id) group by a.METERREADDATE,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS) s6 on (s1.METERREADDATE=s6.METERREADDATE and S1.DIVISION = S6.DIVISION and S1.SUBDIVISION =S6.SUBDIVISION and S1.SECTION = S6.SECTION  and S1.CYCLE=S5.CYCLE and  s6.BILL_BASIS='HLK')   where Total_Count>0 group by S1.METERREADDATE,S1.DIVISION,S1.SUBDIVISION,S1.SECTION,S1.CYCLE) S11 "+ strWhereCondition + " group by SUBSTR(METERREADDATE,1,10)" ;
                Cursor todoCursor = SD.rawQuery(dailySummary, null);
                System.out.println("Select Result : " + dailySummary);

                if (todoCursor != null && todoCursor.moveToFirst()) {
                    do {
                        String billDate = todoCursor.getString(0);
                        list_DailySummary.add(billDate);

                        String billedConsumers = todoCursor.getString(9);
                        list_DailySummary.add(billedConsumers);
//                        String billedUnits = todoCursor.getString(14);
//                        list_DailySummary.add(billedUnits);

                        String billedActualCount = todoCursor.getString(1);
                        list_DailySummary.add(billedActualCount);
//                        String billedActualUnit = todoCursor.getString(2);
//                        list_DailySummary.add(billedActualUnit);

                        String billedAvCount = todoCursor.getString(7);
                        list_DailySummary.add(billedAvCount);
//                        String billedAvUnit = todoCursor.getString(8);
//                        list_DailySummary.add(billedAvUnit);

                        String billedHlkCount = todoCursor.getString(3);
                        list_DailySummary.add(billedHlkCount);
//                        String billedHlkUnit = todoCursor.getString(12);
//                        list_DailySummary.add(billedHlkUnit);
                    }
                    while (todoCursor.moveToNext());

                    ArrayAdapter<String> ar_DailySummary = new ArrayAdapter<String>
                            (getApplicationContext(), R.layout.grid_view, list_DailySummary);

                    gv_DailySummary.setAdapter(ar_DailySummary);

                } else {

                    Toast.makeText(MeterSummery.this, "No Data Found", Toast.LENGTH_SHORT).show();
                }

            }
        });

        gv_CummulativeHeader = (GridView) findViewById(R.id.gridView2);
        ArrayAdapter<String> cummulativeHeader = new ArrayAdapter<String>
                (this, R.layout.grid_text_custom, cummulativeHeaders);
        gv_CummulativeHeader.setAdapter(cummulativeHeader);

        btnDailyCum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dbHelper = new DB(getApplicationContext());
                SD = dbHelper.getWritableDatabase();
                gv_CummulativeSummary = (GridView) findViewById(R.id.gridView3);
                list_CummulativeSummary = new ArrayList<String>();


                String strWhereCondition=" ";
                if(divisionpre!=null)
                    strWhereCondition = " where S11.DIVISION ='"+divisionpre+"' ";
                if(subdivisionpre!=null)
                    strWhereCondition += " and S11.SUBDIVISION ='"+subdivisionpre+"' ";
                if(sectionpre!=null)
                    strWhereCondition += " and S11.SECTION ='"+sectionpre+"' ";
                if(cyclepre!=null)
                    strWhereCondition += " and S11.CYCLE ='"+cyclepre+"' ";


//                String cummulativeSummary = "select S1.METERREADDATE, S1.DIVISION,S1.SUBDIVISION,S1.SECTION,S1.CYCLE,ifnull(S3.cnt,0) ACT_COUNT,ifnull(S3.consumption,0) ACT_CONSUMPTION,ifnull(S4.cnt,0) AVG_COUNT,ifnull(S4.consumption,0) AVG_CONSUMPTION,ifnull(S5.cnt,0) NM_COUNT,ifnull(S5.consumption,0) NM_CONSUMPTION,ifnull(S6.cnt,0) HLK_COUNT,ifnull(S6.consumption,0) HLK_CONSUMPTION, ifnull(S3.cnt,0)+ifnull(S4.cnt,0)+ifnull(S5.cnt,0)+ifnull(S6.cnt,0) Total_Count,ifnull(S3.consumption,0)+ifnull(S4.consumption,0)+ifnull(S5.consumption,0)+ifnull(S6.consumption,0)   Total_Consumption from tbl_meterupload S1 left outer join (select  a.METERREADDATE,count(*) cnt,sum(CONSUMPKWH) consumption,a.DIVISION,a.SUBDIVISION,a.SECTION,a.CYCLE,b.BILL_BASIS from tbl_meterupload a,tbl_meterstatuscode b where trim(a.currentmeterstatus)=trim(b.state_id) group by a.METERREADDATE,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS) s3 on ( S1.DIVISION = S3.DIVISION and S1.SUBDIVISION =S3.SUBDIVISION and S1.SECTION = S3.SECTION  and S1.CYCLE=S3.CYCLE and s3.BILL_BASIS='ACT') left outer join (select  a.METERREADDATE,count(*) cnt,sum(CONSUMPKWH) consumption,a.DIVISION,a.SUBDIVISION,a.SECTION,a.CYCLE,b.BILL_BASIS from tbl_meterupload a,tbl_meterstatuscode b where trim(a.currentmeterstatus)=trim(b.state_id) group by a.METERREADDATE,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS) s4 on (S1.DIVISION = S4.DIVISION and S1.SUBDIVISION =S4.SUBDIVISION and S1.SECTION = S4.SECTION  and S1.CYCLE=S4.CYCLE and s4.BILL_BASIS='AVG') left outer join (select  a.METERREADDATE,count(*) cnt,sum(CONSUMPKWH) consumption,a.DIVISION,a.SUBDIVISION,a.SECTION,a.CYCLE,b.BILL_BASIS from tbl_meterupload a,tbl_meterstatuscode b where trim(a.currentmeterstatus)=trim(b.state_id)  group by a.METERREADDATE,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS) s5 on   ( S1.DIVISION = S5.DIVISION and S1.SUBDIVISION =S5.SUBDIVISION and S1.SECTION = S5.SECTION  and S1.CYCLE=S5.CYCLE and s5.BILL_BASIS='NM')  left outer join (select  a.METERREADDATE,count(*) cnt,sum(CONSUMPKWH) consumption,a.DIVISION,a.SUBDIVISION,a.SECTION,a.CYCLE,b.BILL_BASIS from tbl_meterupload a,tbl_meterstatuscode b where trim(a.currentmeterstatus)=trim(b.state_id)  group by a.METERREADDATE,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS) s6 on ( S1.DIVISION = S6.DIVISION and S1.SUBDIVISION =S6.SUBDIVISION and S1.SECTION = S6.SECTION and S1.CYCLE=S6.CYCLE and  s6.BILL_BASIS='HLK')  " + strWhereCondition ;//+"  group by S1.DIVISION,S1.SUBDIVISION,S1.SECTION,S1.CYCLE";
//                String cummulativeSummary = "select METERREADDATE,sum(ACT_COUNT),sum(ACT_CONSUMPTION),sum(AVG_COUNT),sum(NM_COUNT+HLK_COUNT),sum(HLK_COUNT),sum(HLK_CONSUMPTION),sum(Total_Count),sum(Total_Consumption) from (select S1.METERREADDATE,S1.DIVISION,S1.SUBDIVISION,S1.SECTION,S1.CYCLE,ifnull(S3.cnt,0) ACT_COUNT,ifnull(S3.consumption,0) ACT_CONSUMPTION,ifnull(S4.cnt,0) AVG_COUNT,ifnull(S4.consumption,0) AVG_CONSUMPTION,ifnull(S5.cnt,0) NM_COUNT,ifnull(S5.consumption,0) NM_CONSUMPTION,ifnull(S6.cnt,0) HLK_COUNT,ifnull(S6.consumption,0) HLK_CONSUMPTION,ifnull(S3.cnt,0)+ifnull(S4.cnt,0)+ifnull(S5.cnt,0)+ifnull(S6.cnt,0) Total_Count,ifnull(S3.consumption,0)+ifnull(S4.consumption,0)+ifnull(S5.consumption,0)+ifnull(S6.consumption,0)   Total_Consumption from tbl_meterupload S1 left outer join (select  a.METERREADDATE,count(*) cnt,sum(CONSUMPKWH) consumption,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS,a.CYCLE from tbl_meterupload a,tbl_meterstatuscode b where trim(a.currentmeterstatus)=trim(b.state_id) group by a.METERREADDATE,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS) s3 on (s1.METERREADDATE=s3.METERREADDATE and S1.DIVISION = S3.DIVISION and S1.SUBDIVISION =S3.SUBDIVISION and S1.SECTION = S3.SECTION  and S1.CYCLE=S3.CYCLE and s3.BILL_BASIS='ACT') left outer join (select  a.METERREADDATE,count(*) cnt,sum(CONSUMPKWH) consumption,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS,a.CYCLE  from tbl_meterupload a,tbl_meterstatuscode b where trim(a.currentmeterstatus)=trim(b.state_id) group by a.METERREADDATE,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS) s4 on  (s1.METERREADDATE=s4.METERREADDATE and S1.DIVISION = S4.DIVISION and S1.SUBDIVISION =S4.SUBDIVISION and S1.SECTION = S4.SECTION  and S1.CYCLE=S4.CYCLE and s4.BILL_BASIS='AVG')  left outer join (select  a.METERREADDATE,count(*) cnt,sum(CONSUMPKWH) consumption,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS,a.CYCLE  from tbl_meterupload a,tbl_meterstatuscode b where trim(a.currentmeterstatus)=trim(b.state_id) group by a.METERREADDATE,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS) s5 on (s1.METERREADDATE=s5.METERREADDATE and S1.DIVISION = S5.DIVISION and S1.SUBDIVISION =S5.SUBDIVISION and S1.SECTION = S5.SECTION  and S1.CYCLE=S5.CYCLE and s5.BILL_BASIS='NM') left outer join (select  a.METERREADDATE,count(*) cnt,sum(CONSUMPKWH) consumption,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS,a.CYCLE  from tbl_meterupload a,tbl_meterstatuscode b where trim(a.currentmeterstatus)=trim(b.state_id) group by a.METERREADDATE,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS) s6 on (s1.METERREADDATE=s6.METERREADDATE and S1.DIVISION = S6.DIVISION and S1.SUBDIVISION =S6.SUBDIVISION and S1.SECTION = S6.SECTION  and S1.CYCLE=S5.CYCLE and  s6.BILL_BASIS='HLK')   where Total_Count>0 group by S1.METERREADDATE,S1.DIVISION,S1.SUBDIVISION,S1.SECTION,S1.CYCLE) S11"+ strWhereCondition ;
                String cummulativeSummary = "select SUBSTR(METERREADDATE,1,10),sum(ACT_COUNT),sum(ACT_CONSUMPTION),sum(AVG_COUNT),sum(AVG_CONSUMPTION),sum(NM_COUNT),sum(NM_CONSUMPTION),sum(HLK_COUNT),sum(HLK_CONSUMPTION),sum(Total_Count),sum(Total_Consumption) from  (select S1.METERREADDATE,S1.DIVISION,S1.SUBDIVISION,S1.SECTION,S1.CYCLE,ifnull(S3.cnt,0) ACT_COUNT,ifnull(S3.consumption,0) ACT_CONSUMPTION,ifnull(S4.cnt,0)+ifnull(S5.cnt,0) AVG_COUNT,ifnull(S4.consumption,0) AVG_CONSUMPTION,ifnull(S5.cnt,0) NM_COUNT,ifnull(S5.consumption,0) NM_CONSUMPTION,ifnull(S6.cnt,0) HLK_COUNT,ifnull(S6.consumption,0) HLK_CONSUMPTION,ifnull(S3.cnt,0)+ifnull(S4.cnt,0)+ifnull(S5.cnt,0)+ifnull(S6.cnt,0) Total_Count,ifnull(S3.consumption,0)+ifnull(S4.consumption,0)+ifnull(S5.consumption,0)+ifnull(S6.consumption,0)   Total_Consumption from tbl_meterupload S1 left outer join (select  a.METERREADDATE,count(*) cnt,sum(CONSUMPKWH) consumption,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS,a.CYCLE from tbl_meterupload a,tbl_meterstatuscode b where trim(a.currentmeterstatus)=trim(b.state_id) group by a.METERREADDATE,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS) s3 on (s1.METERREADDATE=s3.METERREADDATE and S1.DIVISION = S3.DIVISION and S1.SUBDIVISION =S3.SUBDIVISION and S1.SECTION = S3.SECTION  and S1.CYCLE=S3.CYCLE and s3.BILL_BASIS='ACT') left outer join (select  a.METERREADDATE,count(*) cnt,sum(CONSUMPKWH) consumption,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS,a.CYCLE  from tbl_meterupload a,tbl_meterstatuscode b where trim(a.currentmeterstatus)=trim(b.state_id) group by a.METERREADDATE,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS) s4 on  (s1.METERREADDATE=s4.METERREADDATE and S1.DIVISION = S4.DIVISION and S1.SUBDIVISION =S4.SUBDIVISION and S1.SECTION = S4.SECTION  and S1.CYCLE=S4.CYCLE and s4.BILL_BASIS='AVG') left outer join (select  a.METERREADDATE,count(*) cnt,sum(CONSUMPKWH) consumption,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS,a.CYCLE  from tbl_meterupload a,tbl_meterstatuscode b where trim(a.currentmeterstatus)=trim(b.state_id) group by a.METERREADDATE,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS) s5 on (s1.METERREADDATE=s5.METERREADDATE and S1.DIVISION = S5.DIVISION and S1.SUBDIVISION =S5.SUBDIVISION and S1.SECTION = S5.SECTION  and S1.CYCLE=S5.CYCLE and s5.BILL_BASIS='NM') left outer join (select  a.METERREADDATE,count(*) cnt,sum(CONSUMPKWH) consumption,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS,a.CYCLE  from tbl_meterupload a,tbl_meterstatuscode b where trim(a.currentmeterstatus)=trim(b.state_id) group by a.METERREADDATE,a.DIVISION,a.SUBDIVISION,a.SECTION,b.BILL_BASIS) s6 on (s1.METERREADDATE=s6.METERREADDATE and S1.DIVISION = S6.DIVISION and S1.SUBDIVISION =S6.SUBDIVISION and S1.SECTION = S6.SECTION  and S1.CYCLE=S5.CYCLE and  s6.BILL_BASIS='HLK')   where Total_Count>0 group by S1.METERREADDATE,S1.DIVISION,S1.SUBDIVISION,S1.SECTION,S1.CYCLE) S11 "+ strWhereCondition ;
                Cursor todoCursor = SD.rawQuery(cummulativeSummary, null);

                if (todoCursor != null && todoCursor.moveToFirst()) {
                    do {
                        String billDate = todoCursor.getString(0);
                        list_CummulativeSummary.add(billDate);

                        String billedConsumersCount = todoCursor.getString(9);
                        list_CummulativeSummary.add(billedConsumersCount);
//                        String billedConsumersUnit = todoCursor.getString(10);
//                        list_CummulativeSummary.add(billedConsumersUnit);
                        String billActualCount = todoCursor.getString(1);
                        list_CummulativeSummary.add(billActualCount);
//                        String billActualUnit = todoCursor.getString(2);
//                        list_CummulativeSummary.add(billActualUnit);
                        String billAvgCount = todoCursor.getString(7);
                        list_CummulativeSummary.add(billAvgCount);
//                        String billAvgUnit = todoCursor.getString(4);
//                        list_CummulativeSummary.add(billAvgUnit);
                        String billedHlkCount = todoCursor.getString(3);
                        list_CummulativeSummary.add(billedHlkCount);
//                        String billedHlkUnit = todoCursor.getString(6);
//                        list_CummulativeSummary.add(billedHlkUnit);
                    }
                    while (todoCursor.moveToNext());

                    ArrayAdapter<String> ad_CummulativeSummary = new ArrayAdapter<String>
                            (getApplicationContext(), R.layout.grid_view, list_CummulativeSummary);
                    gv_CummulativeSummary.setAdapter(ad_CummulativeSummary);


                } else {

                    Toast.makeText(MeterSummery.this, "No Data Found", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


}