package com.fedco.mbc.activitymetering;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Debug;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.BillingViewActivity;
import com.fedco.mbc.activity.BillingtypesActivity;
import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.billinglogic.CBillling;
import com.fedco.mbc.bluetoothprinting.DuplicateBillPrint;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.model.Structbilling;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.model.Structmetering;
import com.fedco.mbc.model.Structmeterupload;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.GPSTracker;
import com.fedco.mbc.utils.UtilAppCommon;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MeterStateMtr extends ListActivity {

    SQLiteDatabase SD;
    DB dbHelper;

    ArrayList<String> values;
    private ArrayList<String> results = new ArrayList<String>();

    GPSTracker gps;
    double latitude;
    double longitude;

    String curmeterreading, curDateTime;
    int curMeterStat;

    UtilAppCommon appCom;
    CBillling calBill;
    Logger Log;
    com.splunk.mint.Logger LogMint;

    Button btn_bck;
    SweetAlertDialog sAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metering_meter_state);
        setTitle("Meter Status");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        this.curDateTime = sdf.format(new Date());

        openAndQueryDatabase();
        displayResultList();
        btn_bck =(Button)findViewById(R.id.Buttonback);
        btn_bck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // finish activity
                overridePendingTransition(R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right);
            }
        });
    }

    private void displayResultList() {
        setListAdapter(new ArrayAdapter<String>(this,
                R.layout.list_view, results));

    }

    private void openAndQueryDatabase() {
        try {

            dbHelper = new DB(getApplicationContext());
            SD = dbHelper.getWritableDatabase();

            Cursor c = SD.rawQuery("SELECT STATUS FROM TBL_METERSTATUSCODE WHERE STATE_CODE IN ('1','5','7','8')", null);

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
            Log.e(getApplicationContext(), getClass().getSimpleName(), "Could not create or Open the database");
        } finally {

            SD.close();

        }

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);

        // ListView Clicked item index
        int itemPosition = position;

        // ListView Clicked item value
        String itemValue = (String) l.getItemAtPosition(position);

        //actual
        boolean normal = Structmetering.CURRENTMETERSTATUS.equals(new String("1"));
        boolean dialover = Structmetering.CURRENTMETERSTATUS.equals(new String("2"));
        boolean meterchange = Structmetering.CURRENTMETERSTATUS.equals(new String("3"));

        //houselocked
        boolean readingnot = Structmetering.CURRENTMETERSTATUS.equals(new String("4"));
        boolean Premlock = Structmetering.CURRENTMETERSTATUS.equals(new String("5"));

        //average
        boolean negread = Structmetering.CURRENTMETERSTATUS.equals(new String("6"));
        boolean mtrdef = Structmetering.CURRENTMETERSTATUS.equals(new String("7"));
        boolean sitntrace = Structmetering.CURRENTMETERSTATUS.equals(new String("9"));

        //nometer
        boolean nomtr = Structmetering.CURRENTMETERSTATUS.equals(new String("8"));

        dbHelper = new DB(getApplicationContext());
        SD = dbHelper.getWritableDatabase();

        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        switch (itemPosition) {
            case 0://NORAML ACT(1)

                if (mtrdef || nomtr) {

                    new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Warning")
                            .setContentText("You are not allowed to declare meter status as 'Actual' ")
                            .setConfirmText("OK, Got it!")
                            .show();

                } else {

                    System.out.println("IN CASE 0000000000000000 : METERSTATE  5");

                    String ACTQwer = "select DERIVEDMETERSTATUS from TBL_DERIVEDMETERSTATUSCODE Where PREVIOUSMETERSTATUSCODE='" + Structmetering.CURRENTMETERSTATUS.trim() + "' and CURRENTMETERSTATUSCODE='1'";
                    System.out.println("Query is meter State :" + ACTQwer);

                    Structmeterupload.CURRENTMETERSTATUS="1";
                    Structmeterupload.BILL_BASIS = "ACT";
                    Structmeterupload.FRESHDFFLAG = "N";

                    Cursor curDerMetStaACT = SD.rawQuery(ACTQwer, null);
                    if (curDerMetStaACT != null && curDerMetStaACT.moveToFirst()) {
                        System.out.println("derived meterstatus is meterstate  :" + curDerMetStaACT.getString(0));
                    }

                    Intent intent = new Intent(getApplicationContext(), MeterCameraView.class);
                    intent.putExtra("CamFlag",true);
                    //intent.putExtra("Position", 1);
                    GSBilling.getInstance().setCurmeter3ph(curDerMetStaACT.getString(0));
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);


                }


                break;
            case 1://Premisses LOcked HLK(5)

            {
                System.out.println("IN CASE 0000000000000000 : METERSTATE  5");

                String HLKQwer = "select DERIVEDMETERSTATUS from TBL_DERIVEDMETERSTATUSCODE Where PREVIOUSMETERSTATUSCODE='" + Structmetering.CURRENTMETERSTATUS.trim() + "' and CURRENTMETERSTATUSCODE='5'";
                System.out.println("Query is meter State :" + HLKQwer);

                Structmeterupload.CURRENTMETERSTATUS="5";
                Structmeterupload.BILL_BASIS = "HLK";
                Structmeterupload.FRESHDFFLAG = "N";

                Cursor curDerMetStaHLK = SD.rawQuery(HLKQwer, null);
                if (curDerMetStaHLK != null && curDerMetStaHLK.moveToFirst()) {
                    System.out.println("derived meterstatus is meterstate  :" + curDerMetStaHLK.getString(0));
                }

                Intent intent = new Intent(getApplicationContext(), MeterCameraView.class);
                intent.putExtra("CamFlag",false);

                //intent.putExtra("Position", 1);
                GSBilling.getInstance().setCurmeter3ph(curDerMetStaHLK.getString(0));

                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);


                break;
            }

            case 2://Meter Defective AVG (7)
                System.out.println("IN CASE 0000000000000000 : METERSTATE  5");

                String DiffQwer = "select DERIVEDMETERSTATUS from TBL_DERIVEDMETERSTATUSCODE Where PREVIOUSMETERSTATUSCODE='" + Structmetering.CURRENTMETERSTATUS.trim() + "' and CURRENTMETERSTATUSCODE='7'";
                System.out.println("Query is meter State :" + DiffQwer);

                Structmeterupload.CURRENTMETERSTATUS="7";
                Structmeterupload.BILL_BASIS = "AVG";
                Structmeterupload.FRESHDFFLAG = "Y";

                final Cursor curDerMetStaDiff = SD.rawQuery(DiffQwer, null);
                if (curDerMetStaDiff != null && curDerMetStaDiff.moveToFirst()) {
                    System.out.println("derived meterstatus is meterstate  :" + curDerMetStaDiff.getString(0));
                }

                if (normal || dialover || meterchange || readingnot || Premlock || nomtr ) {

                    sAlert = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
                    sAlert.setTitleText("Are you sure !")
                            .setContentText("current meter status will be Defective ")
                            .setCancelText("No")
                            .setConfirmText("Yes")
                            .showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    // reuse previous dialog instance, keep widget user state, reset them if you need
                                    sDialog.cancel();

                                }
                            })
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {

                                    Intent intent = new Intent(getApplicationContext(), MeterCameraView.class);

                                    intent.putExtra("CamFlag",false);
                                    //intent.putExtra("Position", 1);
                                    GSBilling.getInstance().setCurmeter3ph(curDerMetStaDiff.getString(0));
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.anim_slide_in_left,
                                            R.anim.anim_slide_out_left);

                                }
                            })
                            .show();

                } else {


                    if(mtrdef){
                        Intent intent = new Intent(getApplicationContext(), MeterCameraView.class);
                        intent.putExtra("CamFlag",false);

                        //intent.putExtra("Position", 1);
                        GSBilling.getInstance().setCurmeter3ph(curDerMetStaDiff.getString(0));

                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left);

                    }else{

                        Intent intent = new Intent(getApplicationContext(), MeterCameraView.class);
                        intent.putExtra("CamFlag",true);

                        //intent.putExtra("Position", 1);
                        GSBilling.getInstance().setCurmeter3ph(curDerMetStaDiff.getString(0));

                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left);
                    }

                }

                break;
            case 3://No meter NM (8)
                System.out.println("IN CASE 0000000000000000 : METERSTATE  5");

                String NoMetQwer = "select DERIVEDMETERSTATUS from TBL_DERIVEDMETERSTATUSCODE Where PREVIOUSMETERSTATUSCODE='" + Structmetering.CURRENTMETERSTATUS.trim() + "' and CURRENTMETERSTATUSCODE='8'";
                System.out.println("Query is meter State :" + NoMetQwer);

                Structmeterupload.CURRENTMETERSTATUS="8";
                Structmeterupload.BILL_BASIS = "NM";
                Structmeterupload.FRESHDFFLAG = "N";

                Cursor curDerMetStaNM = SD.rawQuery(NoMetQwer, null);
                if (curDerMetStaNM != null && curDerMetStaNM.moveToFirst()) {
                    System.out.println("derived meterstatus is meterstate  :" + curDerMetStaNM.getString(0));
                }

                GSBilling.getInstance().setCurmeter3ph(curDerMetStaNM.getString(0));
                if (normal || dialover || meterchange || readingnot || Premlock || mtrdef ) {

                    new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Warning")
                            .setContentText("You are not allowed to declare meter status as 'No Meter' ")
                            .setConfirmText("OK, Got it!")
                            .show();

                } else {



                    Intent intent = new Intent(getApplicationContext(), MeterCameraView.class);
                    intent.putExtra("CamFlag",false);
                    //intent.putExtra("Position", 1);

                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);
                }
                break;
        }

        Log.e(getApplicationContext(), "MeterStatAct", "Click : \n  Position :" + itemPosition + "  \n  ListItem : " + itemValue);


    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        //    sAlert.dismiss();
        Debug.stopMethodTracing();
        super.onDestroy();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.anim_slide_in_right,
                    R.anim.anim_slide_out_right);
            return true;
        }
        return false;
    }

    public void onBackPressed() {

        finish(); // finish activity
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);

    }
}