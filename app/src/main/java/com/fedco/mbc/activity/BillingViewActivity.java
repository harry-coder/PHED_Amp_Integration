package com.fedco.mbc.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fedco.mbc.R;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.bluetoothprinting.GlobalPool;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.model.Structbilling;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.GPSTracker;
import com.fedco.mbc.utils.UtilAppCommon;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.fedco.mbc.activity.StartLocationAlert.REQUEST_CHECK_SETTINGS;

public class BillingViewActivity extends AppCompatActivity implements LogoutListaner {

    TextView tvConno, tvBillbasis, tvBillno, tvBilldate, tvPayby, tvAmntpay, tvAftedd, tvConname, tvConadd, tvTarrifcat,
            tvLoad,tv_LoadUnit, tvPhase, tvArr, tvConunits, tvArr2, tvBillMonth, tvCurBill, tvTotalamnt, tvaftdd2;
    Button btnPrint;
    Logger Log;
    Float TotalARR;
    String Thermal_enable;
    UtilAppCommon ucom;
    SQLiteDatabase SD, SD2, SD3;
    DB dbHelper, dbHelper2, dbHelper3;
    SessionManager session;
    GPSTracker gps, gps2;
    double latitude;
    double longitude;
    double latitude1 = 0.0;
    double longitude1 = 0.0;
    long gpstime;
    double altitude;
    double accuracy;
    Location location;
    protected static final int REQUEST_CHECK_SETTINGS2 = 2;
    LocationManager locationManager;
    Activity mContext;
    StartLocationAlert startLocationAlert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_view);
        ucom = new UtilAppCommon();

        try {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
            getSupportActionBar().setLogo(R.mipmap.logo);
            setTitle("SMARTPHED");
        } catch (NullPointerException npe) {
            Log.e(getApplicationContext(), "Billing Act", "ActionBar Throwing null Pointer", npe);
        }

//        TotalARR = Structconsmas.Pre_Financial_Yr_Arr + Structconsmas.Cur_Fiancial_Yr_Arr;
//        TotalARR = 0f;

        ((GlobalPool)getApplication()).registerSessionListaner(this);
        ((GlobalPool)getApplication()).startUserSession();
        // Session class instance
        session = new SessionManager(getApplicationContext());
        gps = new GPSTracker(BillingViewActivity.this);

        latitude = gps.getLatitude();
        longitude = gps.getLongitude();
        gpstime = gps.getTime();
        accuracy = gps.getAccuracy();
        altitude = gps.getAltitude();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        startLocationAlert = new StartLocationAlert(this);
        startLocationAlert.settingsrequest();

        final SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
        Date d = new Date(gps.getTime());
        String sDate = sdf1.format(d);

        Structbilling.User_Long = String.valueOf(longitude);
        Structbilling.User_Lat = String.valueOf(latitude);
        Structbilling.GPS_TIME = sDate;
        Structbilling.GPS_ACCURACY = String.valueOf(accuracy);
        Structbilling.GPS_ALTITUDE = String.valueOf(altitude);
        Structbilling.BAT_STATE = GSBilling.getInstance().getBatStr();
        Structbilling.DSIG_STATE = GSBilling.getInstance().getSignalStr();
//        Structbilling.VER_CODE = GSBilling.getInstance().getVerCode();
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        String key  = ucom.UniqueCode(getApplicationContext());
        tvConno     = (TextView) findViewById(R.id.consumer_no);
        tvBillMonth = (TextView) findViewById(R.id.billmonth);
        tvBillno    = (TextView) findViewById(R.id.bill_no);
        tvBilldate  = (TextView) findViewById(R.id.bill_date);
        tvPayby     = (TextView) findViewById(R.id.pay_by);
        tvAmntpay   = (TextView) findViewById(R.id.amount_pay);
        tvAftedd    = (TextView) findViewById(R.id.amount_due);
        tvConname   = (TextView) findViewById(R.id.consumer_name);
        tvConadd    = (TextView) findViewById(R.id.consumer_address);
        tvTarrifcat = (TextView) findViewById(R.id.tariff);
        tvLoad      = (TextView) findViewById(R.id.load);
        tv_LoadUnit = (TextView) findViewById(R.id.load_unit);
        tvPhase     = (TextView) findViewById(R.id.phase);
        tvArr       = (TextView) findViewById(R.id.arrear);
        tvConunits  = (TextView) findViewById(R.id.consumed_units);
        tvArr2      = (TextView) findViewById(R.id.arre);
        // tvCurBill = (TextView) findViewById(R.id.curr);
        tvTotalamnt = (TextView) findViewById(R.id.total);
        tvaftdd2    = (TextView) findViewById(R.id.due);
        tvBillbasis = (TextView) findViewById(R.id.bill_basis);

        btnPrint = (Button) findViewById(R.id.print);

        if (isLocationEnabled(getApplicationContext())) {
            btnPrint.setEnabled(true);
        } else {
            btnPrint.setEnabled(false);
        }


        Structbilling.Bill_No = key + "/" + ucom.findSequence(getApplicationContext(), "BillNumber");

//        String BilMonth1=Structconsmas.Bill_Mon.substring(0,4);
//        String BilMonth2=Structconsmas.Bill_Mon.substring(4,6);
//        String BilMonth3=Structconsmas.Bill_Mon.substring(6,8);
//
//        String DateBillmonth=BilMonth3+"-"+BilMonth2+"-"+BilMonth1;
//        Structbilling.Bill_Month=DateBillmonth;

        tvConno.setText(Structbilling.Cons_Number);
        tvBillMonth.setText(Structconsmas.Bill_Mon);
        tvBillno.setText(Structbilling.Bill_No);
        tvBilldate.setText(Structbilling.Bill_Date);
        tvPayby.setText(Structconsmas.FIRST_CHQ_DUE_DATE);
        tvAmntpay.setText (Structbilling.O_Total_Bill);
//        tvAmntpay.setText ("----");
        tvAftedd.setText(String.format("%.2f", (Double.valueOf(Structbilling.O_Total_Bill) + (Double.valueOf(Structbilling.O_Surcharge)))));
//        tvAftedd.setText("----");
        tvConname.setText(Structconsmas.Name);
        tvConadd.setText(Structconsmas.H_NO + Structconsmas.MOH + Structconsmas.address1 + "" + Structconsmas.address2);
        tvTarrifcat.setText(Structbilling.Tariff_Code);
        tvLoad.setText(Structconsmas.Load.toString());
        switch(Structconsmas.Load_Type){
            case "1":
                tv_LoadUnit.setText("Load In W");
                break;
            case "2":
                tv_LoadUnit.setText("Load In KW");
                break;
            case "3":
                tv_LoadUnit.setText("Load In HP");
                break;
            case "4":
                tv_LoadUnit.setText("Load In KVA");
                break;
        }
        tvPhase.setText("1");
        tvArr.setText(Structbilling.O_Arrear_Demand.toString());
        tvConunits.setText(String.valueOf(Structbilling.Units_Consumed));
        tvArr2.setText(Structbilling.O_Arrear_Demand.toString());

        float totlabill = Float.parseFloat(Structbilling.O_Total_Bill); //Structbilling.Amnt_Paidafter_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL;
        tvTotalamnt.setText("" + Structbilling.O_Total_Bill);
//        tvTotalamnt.setText("----");
        // tvTotalamnt.setText(Structbilling.Cur_Bill_Total.toString());
        // tvCurBill.setText(String.format("%.2f", Math.ceil(Structbilling.Amnt_bPaid_on_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL)));
        tvaftdd2.setText(String.format("%.2f", totlabill));  //Math.ceil(Structbilling.Amnt_Paidafter_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL)));
//        tvaftdd2.setText("----");  //Math.ceil(Structbilling.Amnt_Paidafter_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL)));
//        tvaftdd2.setText(Math.ceil(Structbilling.Amnt_Paidafter_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL);  //;
        tvBillbasis.setText(Structbilling.Bill_Basis);

        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            this.sendBroadcast(poke);
        }
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnPrint.setVisibility(View.GONE);

                //Check if Location is ON
                if (isLocationEnabled(getApplicationContext())) {

                    //Check if Mobile Data is ON
//                    if (isMobileDataEnabled()) {


                    //Check if LAT & LONG is ON
                    if (latitude1 == 0.00 && longitude1 == 0.00) {

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i <= 250; i++) {

                                    GPSTracker gps3 = new GPSTracker(MeterState.getsActivity());

                                    latitude1 = gps3.getLatitude();
                                    longitude1 = gps3.getLongitude();
                                    gpstime = gps3.getTime();
                                    accuracy = gps3.getAccuracy();
                                    altitude = gps3.getAltitude();

                                    final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
                                    Date d = new Date(gps3.getTime());
                                    String sDate = sdf.format(d);

                                    Structbilling.User_Long = String.valueOf(longitude1);
                                    Structbilling.User_Lat = String.valueOf(latitude1);
                                    Structbilling.GPS_TIME = sDate;
                                    Structbilling.GPS_ACCURACY = String.valueOf(accuracy);
                                    Structbilling.GPS_ALTITUDE = String.valueOf(altitude);


                                    if (latitude1 != 0.0) {
                                        break; // A unlabeled break is enough. You don't need a labeled break here.
                                    }

                                }
                                Intent intent = new Intent(BillingViewActivity.this, Signature_Activity.class);
                                //                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left);
                            }
                        }, 1 * 5 * 1000);



                    } else {

                        Intent intent = new Intent(BillingViewActivity.this, Signature_Activity.class);
                        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left);

                    }


//                    } else {
//
//                        Intent intent = new Intent();
//                        intent.setComponent(new ComponentName(
//                                "com.android.settings",
//                                "com.android.settings.Settings$DataUsageSummaryActivity"));
//                        startActivity(intent);
//
//                    }

                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    // startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 2);
                    startLocationAlert.settingsrequest();
                }
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:

                        btnPrint.setEnabled(true);
                        btnPrint.setVisibility(View.VISIBLE);
                        break;
                    case Activity.RESULT_CANCELED:
                        btnPrint.setVisibility(View.VISIBLE);
                        startLocationAlert.settingsrequest();//keep asking if imp or do whatever
                        break;
                }

                break;


        }
    }

    public boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    public Boolean isMobileDataEnabled() {
        Object connectivityService = getSystemService(CONNECTIVITY_SERVICE);
        ConnectivityManager cm = (ConnectivityManager) connectivityService;

        try {
            Class<?> c = Class.forName(cm.getClass().getName());
            Method m = c.getDeclaredMethod("getMobileDataEnabled");
            m.setAccessible(true);
            return (Boolean) m.invoke(cm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        ((GlobalPool)getApplication()).onUserInteraction();
    }

    @Override
    public void userLogoutListaner() {
        finish();
        Intent intent=new Intent(BillingViewActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }


    public void onBackPressed() {

        finish(); // finish activity
        Intent intent = new Intent(BillingViewActivity.this, BillingtypesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);

    }
}
