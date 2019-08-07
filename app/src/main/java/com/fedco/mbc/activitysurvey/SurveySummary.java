package com.fedco.mbc.activitysurvey;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fedco.mbc.Adapter.BillingSummery;
import com.fedco.mbc.R;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.sqlite.DB;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SurveySummary extends AppCompatActivity {

    public SweetAlertDialog sDialog;
    SQLiteDatabase SD;
    DB dbHelper;
    String strDate;
    private ArrayList<String> results = new ArrayList<String>();
    BillingSummery handler;
    HashMap<String, ArrayList> summerycol;
    ArrayList<String> li,list_DailySummary,list_CummulativeSummary,
            list_Col_DailySummary,list_ColCummulativeSummary;
    ListView lvItems , collvItems ,colCUMlist;
    Button btnDailySum,btnDailyCum ,btnDCSum,btnDCCum;
    Logger Log;
    /*CardView cvsum ,cvcum ,cvCsum ,cvCcum;
    TextView TVBCon,TVBUnit,TVBAmnt,TVBDate;*/

    static final String[] dailySummaryHeaders = new String[]{
            "Date" , "11kV Feeder" , "DTR", "Pole", "Consumer"
    };
    static final String[] cummulativeHeaders = new String[]{
             "11kV Feeder" , "DTR", "Pole", "Consumer"
    };
    GridView gv_DailySummaryHeader,gv_DailySummary,
            gv_CummulativeHeader,gv_CummulativeSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_survey_summary);

            btnDailySum=(Button)findViewById(R.id.btnDSR);
            btnDailyCum=(Button)findViewById(R.id.btnCR);

            gv_DailySummaryHeader = (GridView) findViewById(R.id.gridView_DS_Header);
            ArrayAdapter<String> headerAdapter = new ArrayAdapter<String>
                    (this,R.layout.grid_view_header,dailySummaryHeaders);
            gv_DailySummaryHeader.setAdapter(headerAdapter);

            btnDailySum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dbHelper = new DB(getApplicationContext());
                    SD = dbHelper.getWritableDatabase();
                    gv_DailySummary = (GridView) findViewById(R.id.gridView1);
                    list_DailySummary = new ArrayList<String>();

                    String dailySummary = "select date1,count(FEEDER_CODE),count(DTR_CODE),count(POLE_CODE),count(Consumer_Number) from (\n" +
                            "             select \n" +
                            "\t\t\t FEEDER_CODE,\n" +
                            "\t\t\t date(REPORT_DATE) as date1,\n" +
                            "\t\t\t  Null as DTR_CODE,\n" +
                            "\t\t\t  Null as POLE_CODE,\n" +
                            "\t\t\t  Null as Consumer_Number\n" +
                            " from TBL_11KVFEEDER_UPLOAD \n" +
                            "             union all  \n" +
                            "              select \n" +
                            "\t\t\t Null as FEEDER_CODE,\n" +
                            "\t\t\t  date(REPORT_DATE) as date1,\n" +
                            "\t\t\t  DTR_CODE,\n" +
                            "\t\t\t  Null as POLE_CODE,\n" +
                            "\t\t\t  Null as Consumer_Number\n" +
                            "from TBL_DTR_UPLOAD\n" +
                            "             union all  \n" +
                            "              select \n" +
                            "\t\t\t Null as FEEDER_CODE,\n" +
                            "\t\t\t  date(REPORT_DATE) as date1,\n" +
                            "\t\t\t Null as DTR_CODE,\n" +
                            "\t\t\t  POLE_CODE,\n" +
                            "\t\t\t Null as Consumer_Number\n" +
                            "from TBL_POLE_UPLOAD union all  \n" +
                            "              select \n" +
                            "\t\t\t Null as FEEDER_CODE,\n" +
                            "\t\t\t  date(REPORT_DATE) as date1,\n" +
                            "\t\t\t Null as  DTR_CODE,\n" +
                            "\t\t\t Null as POLE_CODE,\n" +
                            "\t\t\t Consumer_Number\n" +
                            "from TBL_CONSUMERSURVEY_UPOLOAD\n" +
                            "             )  A where date1<=date('now')\n" +
                            "             group by date1 order by date1 desc";
                    Cursor todoCursor = SD.rawQuery(dailySummary, null);
                    System.out.println("Select Result : " + dailySummary);

                    if (todoCursor!= null && todoCursor.moveToFirst()){
                        do{
                            String date = todoCursor.getString(0);
                            list_DailySummary.add(date);
                            String feeder = todoCursor.getString(1);
                            list_DailySummary.add(feeder);
                            String dtr = todoCursor.getString(2);
                            list_DailySummary.add(dtr);
                            String pole = todoCursor.getString(3);
                            list_DailySummary.add(pole);
                            String consumer = todoCursor.getString(4);
                            list_DailySummary.add(consumer);
                        }
                        while(todoCursor.moveToNext());

                        ArrayAdapter<String> ar_DailySummary = new ArrayAdapter<String>
                                (getApplicationContext(),R.layout.grid_view,list_DailySummary);

                        gv_DailySummary.setAdapter(ar_DailySummary);

                    }else{

                        Toast.makeText(SurveySummary.this, "No Data Found", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            gv_CummulativeHeader = (GridView) findViewById(R.id.gridView2);
            ArrayAdapter<String> cummulativeHeader = new ArrayAdapter<String>
                    (this,R.layout.grid_view_header,cummulativeHeaders);
            gv_CummulativeHeader.setAdapter(cummulativeHeader);

            btnDailyCum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dbHelper = new DB(getApplicationContext());
                    SD = dbHelper.getWritableDatabase();
                    gv_CummulativeSummary = (GridView) findViewById(R.id.gridView3) ;
                    list_CummulativeSummary = new ArrayList<String>();

                    String cummulativeSummary = "SELECT \n" +
                            "\t(SELECT count(*) FROM TBL_11KVFEEDER_UPLOAD WHERE date(REPORT_DATE)<=date('now')) AS feeder_count,\n" +
                            "\t(SELECT count(*) FROM TBL_DTR_UPLOAD WHERE date(REPORT_DATE)<=date('now')) AS dt_count,\n" +
                            "\t(SELECT count(*) FROM TBL_POLE_UPLOAD WHERE date(REPORT_DATE)<=date('now')) AS pole_count,\n" +
                            "\t(SELECT count(*) FROM TBL_CONSUMERSURVEY_UPOLOAD WHERE date(REPORT_DATE)<=date('now') ) AS consumer_count";
                    Cursor todoCursor = SD.rawQuery(cummulativeSummary, null);

                    if(todoCursor!= null && todoCursor.moveToFirst()){
                        do{

                            String cfeeder = todoCursor.getString(0);
                            list_CummulativeSummary.add(cfeeder);
                            String cdtr = todoCursor.getString(1);
                            list_CummulativeSummary.add(cdtr);
                            String cpole = todoCursor.getString(2);
                            list_CummulativeSummary.add(cpole);
                            String cconsumer = todoCursor.getString(3);
                            list_CummulativeSummary.add(cconsumer);
                        }
                        while(todoCursor.moveToNext());


                        ArrayAdapter<String> ad_CummulativeSummary = new ArrayAdapter<String>
                                (getApplicationContext(),R.layout.grid_view,list_CummulativeSummary);
                        gv_CummulativeSummary.setAdapter(ad_CummulativeSummary);


                    }else{

                        Toast.makeText(SurveySummary.this, "No Data Found", Toast.LENGTH_SHORT).show();
                    }

                }
            });

    }

    private void openAndQueryDatabase() {
        try {
            dbHelper = new DB(getApplicationContext());
            SD = dbHelper.getWritableDatabase();

            Cursor c = SD.rawQuery("SELECT STATUS FROM TBL_METERSTATUSCODE", null);

            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        String status = c.getString(c.getColumnIndex("STATUS"));
                        System.out.print("STATSUS IS " + status);

                        results.add(status);
                    } while (c.moveToNext());
                }
            }
        } catch (SQLiteException se) {
            Log.e(getApplicationContext(),"", "Could not create or Open the database");
        } finally {

            SD.close();
        }

    }
    class BillSumRep extends AsyncTask<String, Integer, String> {
        BillingSummery handler;
        // Show Progress bar before downloading Music
        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
//        pDialog = showDialog();
            //            pDialogFile= showDialogFile();
            sDialog = new SweetAlertDialog(SurveySummary.this, SweetAlertDialog.PROGRESS_TYPE);
            sDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            sDialog.setTitleText("Downloading File");
            sDialog.setCancelable(false);
            sDialog.show();
            Log.d(getApplicationContext(),"TAG", " is cancelled " + isCancelled());

            super.onPreExecute();

        }

        // Download Music File from Internet
        // This is run in a background thread
        @Override
        protected String doInBackground(String... f_url) {
            //            ---------------------------------------------------------------------by me
            dbHelper = new DB(getApplicationContext());
            SD = dbHelper.getWritableDatabase();
            // Find ListView to populate

            // Query for items from the database and get a cursor back
            Cursor todoCursor = SD.rawQuery("SELECT  * FROM TBL_BILLING", null);
            // Cursor todoCursor = SD.rawQuery("SELECT  count(*),Bill_Date,sum(Units_Consumed),sum(Cur_Bill_Total) from  TBL_BILLING GROUP BY BILL_DATE ORDER BY BILL_DATE", null);
            // TodoDatabaseHandler is a SQLiteOpenHelper class connecting to SQLite
            handler = new BillingSummery(getApplicationContext(),todoCursor,90);
            SurveySummary.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // Attach cursor adapter to the ListView
                    lvItems.setAdapter(handler);
                }
            });

            return null;
        }

        /**
         * Updating progress bar
         */// This is called from background thread but runs in UI
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
//        pDialog.setProgress(values);
            sDialog.setTitleText("progress " + values[0]);
        }

        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String file_url) {
            sDialog.dismiss();

            Toast.makeText(getApplicationContext(), "Downloaded Successfully . Reading starteds", Toast.LENGTH_LONG).show();

        }
    }
    class Dcs extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
//        pDialog = showDialog();
            //            pDialogFile= showDialogFile();
            sDialog = new SweetAlertDialog(SurveySummary.this, SweetAlertDialog.PROGRESS_TYPE);
            sDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            sDialog.setTitleText("Downloading File");
            sDialog.setCancelable(false);
            sDialog.show();
            Log.d(getApplicationContext(),"TAG", " is cancelled " + isCancelled());

            super.onPreExecute();

        }

        // Download Music File from Internet
        // This is run in a background thread
        @Override
        protected String doInBackground(String... f_url) {
            //            ---------------------------------------------------------------------by me
            dbHelper = new DB(getApplicationContext());
            SD = dbHelper.getWritableDatabase();

            /****RAW QUERY EXICUTION AND STORING IN CURSOR****/
            String countCheque = "SELECT COUNT(*) FROM TBL_COLMASTER_MP WHERE PAYMNT_MODE='Cheque'";
            String countTotCash = "select sum(AMOUNT) as AMOUNT, PAYMNT_MODE from TBL_COLMASTER_MP where COL_DATE= '" + strDate + "'  group by PAYMNT_MODE";
            String countTotCheque = "select sum(AMOUNT) as AMOUNT, PAYMNT_MODE from TBL_COLMASTER_MP where COL_DATE= '" + strDate + "'  and PAYMNT_MODE='Cheque' group by PAYMNT_MODE";
            String curTotAMNT = "select sum(Amount) as Amount, PAYMNT_MODE from TBL_COLMASTER_MP where COL_DATE ='" + strDate + "' ";

            Cursor curCountCheque = SD.rawQuery(countCheque, null);
            Cursor curCountTotCash = SD.rawQuery(countTotCash, null);
            Cursor curCountTotCheque = SD.rawQuery(countTotCheque, null);
            Cursor curCountTot = SD.rawQuery(curTotAMNT, null);

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
            super.onProgressUpdate(values);
//        pDialog.setProgress(values);
            sDialog.setTitleText("progress " + values[0]);
        }

        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String file_url) {
            sDialog.dismiss();

            Toast.makeText(getApplicationContext(), "Downloaded Successfully . Reading starteds", Toast.LENGTH_LONG).show();

        }
    }
}
