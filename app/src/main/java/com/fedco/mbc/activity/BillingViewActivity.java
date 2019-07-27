package com.fedco.mbc.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.bluetoothprinting.GlobalPool;
import com.fedco.mbc.bluetoothprinting.SLPrintingMainActivity;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.model.Structbilling;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.GPSTracker;
import com.fedco.mbc.utils.HttpFileUpload;
import com.fedco.mbc.utils.URLS;
import com.fedco.mbc.utils.UtilAppCommon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.fedco.mbc.activity.StartLocationAlert.REQUEST_CHECK_SETTINGS;

public class BillingViewActivity extends AppCompatActivity implements LogoutListaner {

    TextView tvConno, tvBillbasis, tvBillno, tvBilldate, tvPayby, tvAmntpay, tvAftedd, tvConname, tvConadd, tvTarrifcat,
            tvLoad, tv_LoadUnit, tvPhase, tvArr, tvConunits, tvArr2, tvBillMonth, tvCurBill, tvTotalamnt, tvaftdd2;
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
    String DIRECTORY;
    String ZipSourcePath = Environment.getExternalStorageDirectory ( ) + "/MBC/Images/";
    String ZipCopyPath = Environment.getExternalStorageDirectory ( ) + "/MBC/Downloadsingular/";
    String Zip = Environment.getExternalStorageDirectory ( ) + "/Notes/billing.csv";
    String ZipDesPathdup;
    String signaturePathDes;
    String photoPathDes;
    String ZipDesPath;
    String print_type;

    float totlabill;

    //  Button bt_cancel, bt_submit;
    ArrayList <String> mylistimagename = new ArrayList <String> ( );

    ProgressDialog progress;
    String printer_catergory, printer_mfg, printer_roll, prev_pref, slot1;

    DB dbHelper4;
    SQLiteDatabase SD4;
    UtilAppCommon appcomUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        boolean isMeter = Home.isMeter;

        if (!isMeter) {
            setContentView ( R.layout.activity_billing_view );

        } else {
            setContentView ( R.layout.meter_read_summary );

        }

        ucom = new UtilAppCommon ( );
        appcomUtil = new UtilAppCommon ( );

        try {
            getSupportActionBar ( ).setHomeAsUpIndicator ( R.drawable.back );
            getSupportActionBar ( ).setLogo ( R.mipmap.logo );
            setTitle ( "SMARTPHED" );
        } catch (NullPointerException npe) {
            Log.e ( getApplicationContext ( ), "Billing Act", "ActionBar Throwing null Pointer", npe );
        }

//        TotalARR = Structconsmas.Pre_Financial_Yr_Arr + Structconsmas.Cur_Fiancial_Yr_Arr;
//        TotalARR = 0f;
        DIRECTORY = Environment.getExternalStorageDirectory ( ).getPath ( ) + "/MBC/Images/";

        ((GlobalPool) getApplication ( )).registerSessionListaner ( this );
        ((GlobalPool) getApplication ( )).startUserSession ( );
        ZipDesPath = Environment.getExternalStorageDirectory ( ) + "/MBC/" + appcomUtil.UniqueCode ( getApplicationContext ( ) ) + GSBilling.getInstance ( ).captureDatetime ( ) + ".zip";
        ZipDesPathdup = "/MBC/" + appcomUtil.UniqueCode ( getApplicationContext ( ) ) + GSBilling.getInstance ( ).captureDatetime ( );
        // Session class instance
        session = new SessionManager ( getApplicationContext ( ) );
        gps = new GPSTracker ( BillingViewActivity.this );

        latitude = gps.getLatitude ( );
        longitude = gps.getLongitude ( );
        gpstime = gps.getTime ( );
        accuracy = gps.getAccuracy ( );
        altitude = gps.getAltitude ( );

        locationManager = (LocationManager) getSystemService ( Context.LOCATION_SERVICE );
        startLocationAlert = new StartLocationAlert ( this );
        startLocationAlert.settingsrequest ( );

        final SimpleDateFormat sdf1 = new SimpleDateFormat ( "dd-MM-yyyy kk:mm:ss" );
        Date d = new Date ( gps.getTime ( ) );
        String sDate = sdf1.format ( d );

        Structbilling.User_Long = String.valueOf ( longitude );
        Structbilling.User_Lat = String.valueOf ( latitude );
        Structbilling.GPS_TIME = sDate;
        Structbilling.GPS_ACCURACY = String.valueOf ( accuracy );
        Structbilling.GPS_ALTITUDE = String.valueOf ( altitude );
        Structbilling.BAT_STATE = GSBilling.getInstance ( ).getBatStr ( );
        Structbilling.DSIG_STATE = GSBilling.getInstance ( ).getSignalStr ( );
//        Structbilling.VER_CODE = GSBilling.getInstance().getVerCode();
        // get user data from session
        HashMap <String, String> user = session.getUserDetails ( );

        // name
        String key = ucom.UniqueCode ( getApplicationContext ( ) );
        tvConno = (TextView) findViewById ( R.id.consumer_no );
        tvBillMonth = (TextView) findViewById ( R.id.billmonth );
        tvBillno = (TextView) findViewById ( R.id.bill_no );
        tvBilldate = (TextView) findViewById ( R.id.bill_date );
        tvPayby = (TextView) findViewById ( R.id.pay_by );
        tvAmntpay = (TextView) findViewById ( R.id.amount_pay );
        tvAftedd = (TextView) findViewById ( R.id.amount_due );
        tvConname = (TextView) findViewById ( R.id.consumer_name );
        tvConadd = (TextView) findViewById ( R.id.consumer_address );
        tvTarrifcat = (TextView) findViewById ( R.id.tariff );
        tvLoad = (TextView) findViewById ( R.id.load );
        tv_LoadUnit = (TextView) findViewById ( R.id.load_unit );
        tvPhase = (TextView) findViewById ( R.id.phase );
        tvArr = (TextView) findViewById ( R.id.arrear );
        tvConunits = (TextView) findViewById ( R.id.consumed_units );
        tvArr2 = (TextView) findViewById ( R.id.arre );
        // tvCurBill = (TextView) findViewById(R.id.curr);
        tvTotalamnt = (TextView) findViewById ( R.id.total );
        tvaftdd2 = (TextView) findViewById ( R.id.due );
        tvBillbasis = (TextView) findViewById ( R.id.bill_basis );

        btnPrint = (Button) findViewById ( R.id.print );

        if (isLocationEnabled ( getApplicationContext ( ) )) {
            btnPrint.setEnabled ( true );
        } else {
            btnPrint.setEnabled ( false );
        }


       /* bt_cancel = (Button) findViewById ( R.id.bt_cancel );
        bt_submit = (Button) findViewById ( R.id.bt_submit );

        bt_cancel.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent ( BillingViewActivity.this, Billing.class );
                GSBilling.getInstance ( ).setPrintSingle ( "<0x09>" );
                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity ( intent );
                overridePendingTransition ( R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left );


            }
        } );

        bt_submit.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {

                new SweetAlertDialog ( BillingViewActivity.this, SweetAlertDialog.WARNING_TYPE )
                        .setTitleText ( "Are you sure?" )
                        .setContentText ( "Your are about to submit the data!" )
                        .setConfirmText ( "Submit!" )
                        .setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                saveData ();

                                sDialog.dismissWithAnimation ( );

                            }
                        } ).setCancelText ( "Cancel" )
                        .setCancelClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation ( );
                            }
                        } )
                        .show ( );
            }
        } );*/

        Structbilling.Bill_No = key + "/" + ucom.findSequence ( getApplicationContext ( ), "BillNumber" );

//        String BilMonth1=Structconsmas.Bill_Mon.substring(0,4);
//        String BilMonth2=Structconsmas.Bill_Mon.substring(4,6);
//        String BilMonth3=Structconsmas.Bill_Mon.substring(6,8);
//
//        String DateBillmonth=BilMonth3+"-"+BilMonth2+"-"+BilMonth1;
//        Structbilling.Bill_Month=DateBillmonth;


        if (!isMeter) {
            tvConno.setText ( Structbilling.Cons_Number );
            tvBillMonth.setText ( Structconsmas.Bill_Mon );
            tvBillno.setText ( Structbilling.Bill_No );
            tvBilldate.setText ( Structbilling.Bill_Date );

            // Give here Whether it is read, overflow etc...
            tvPayby.setText ( Structconsmas.FIRST_CHQ_DUE_DATE );


            tvAmntpay.setText ( Structbilling.O_Total_Bill );
//        tvAmntpay.setText ("----");
            tvAftedd.setText ( String.format ( "%.2f", (Double.valueOf ( Structbilling.O_Total_Bill ) + (Double.valueOf ( Structbilling.O_Surcharge ))) ) );
//        tvAftedd.setText("----");
            tvConname.setText ( Structconsmas.Name );
            tvConadd.setText ( Structconsmas.H_NO + Structconsmas.MOH + Structconsmas.address1 + "" + Structconsmas.address2 );
            tvTarrifcat.setText ( Structbilling.Tariff_Code );
            tvLoad.setText ( Structconsmas.Load.toString ( ) );
            switch (Structconsmas.Load_Type) {
                case "1":
                    tv_LoadUnit.setText ( "Load In W" );
                    break;
                case "2":
                    tv_LoadUnit.setText ( "Load In KW" );
                    break;
                case "3":
                    tv_LoadUnit.setText ( "Load In HP" );
                    break;
                case "4":
                    tv_LoadUnit.setText ( "Load In KVA" );
                    break;
            }
            tvPhase.setText ( "1" );
            tvArr.setText ( Structbilling.O_Arrear_Demand.toString ( ) );
            tvConunits.setText ( String.valueOf ( Structbilling.Units_Consumed ) );
            tvArr2.setText ( Structbilling.O_Arrear_Demand.toString ( ) );

            float totlabill = Float.parseFloat ( Structbilling.O_Total_Bill ); //Structbilling.Amnt_Paidafter_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL;
            tvTotalamnt.setText ( "" + Structbilling.O_Total_Bill );
//        tvTotalamnt.setText("----");
            // tvTotalamnt.setText(Structbilling.Cur_Bill_Total.toString());
            // tvCurBill.setText(String.format("%.2f", Math.ceil(Structbilling.Amnt_bPaid_on_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL)));
            tvaftdd2.setText ( String.format ( "%.2f", totlabill ) );  //Math.ceil(Structbilling.Amnt_Paidafter_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL)));
//        tvaftdd2.setText("----");  //Math.ceil(Structbilling.Amnt_Paidafter_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL)));
//        tvaftdd2.setText(Math.ceil(Structbilling.Amnt_Paidafter_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL);  //;
            tvBillbasis.setText ( Structbilling.Bill_Basis );
        } else {

            System.out.println ( "This is the date " + Readinginput.readingDate );
            tvConno.setText ( Readinginput.consumerNo );
            tvBillMonth.setText ( Structconsmas.Bill_Mon );
            tvBillno.setText ( Structbilling.Bill_No );
            tvBilldate.setText ( Structbilling.Bill_Date );
//            tvBilldate.setText ( Structconsmas.CUR_READ_DATE );


            System.out.println ( "This is the reading date " + Structbilling.Bill_Date );
            // System.out.println ("This is the reading date "+Structconsmas.Bill_Date );
            tvPayby.setText ( MeterState.selectedValue );

            tvAmntpay.setText ( "" + Structbilling.Cur_Meter_Reading );
//        tvAmntpay.setText ("----");
            tvAftedd.setText ( Structbilling.MOB_NO );
//        tvAftedd.setText("----");
            tvConname.setText ( Structconsmas.Name );
            tvConadd.setText ( Structconsmas.H_NO + Structconsmas.MOH + Structconsmas.address1 + "" + Structconsmas.address2 );

            System.out.println ( "This is tariff Code " + Structbilling.Tariff_Code );
            tvTarrifcat.setText ( Readinginput.tariffCode );
            tvLoad.setText ( Structconsmas.Load.toString ( ) );
            switch (Structconsmas.Load_Type) {
                case "1":
                    tv_LoadUnit.setText ( "Load In W" );
                    break;
                case "2":
                    tv_LoadUnit.setText ( "Load In KW" );
                    break;
                case "3":
                    tv_LoadUnit.setText ( "Load In HP" );
                    break;
                case "4":
                    tv_LoadUnit.setText ( "Load In KVA" );
                    break;
            }
            tvPhase.setText ( "1" );
            tvArr.setText ( Structbilling.O_Arrear_Demand.toString ( ) );
            tvConunits.setText ( String.valueOf ( Structbilling.Units_Consumed ) );
            tvArr2.setText ( Structbilling.O_Arrear_Demand.toString ( ) );

            if (!TextUtils.isEmpty ( Structbilling.O_Total_Bill )) {
                totlabill = Float.parseFloat ( Structbilling.O_Total_Bill );
            } else {
                totlabill = 0;
            }
            //  float totlabill = Float.parseFloat ( Structbilling.O_Total_Bill ); //Structbilling.Amnt_Paidafter_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL;
            tvTotalamnt.setText ( "" + Structbilling.O_Total_Bill );

//        tvTotalamnt.setText("----");
            // tvTotalamnt.setText(Structbilling.Cur_Bill_Total.toString());
            // tvCurBill.setText(String.format("%.2f", Math.ceil(Structbilling.Amnt_bPaid_on_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL)));
            tvaftdd2.setText ( String.format ( "%.2f", totlabill ) );  //Math.ceil(Structbilling.Amnt_Paidafter_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL)));
//        tvaftdd2.setText("----");  //Math.ceil(Structbilling.Amnt_Paidafter_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL)));
//        tvaftdd2.setText(Math.ceil(Structbilling.Amnt_Paidafter_Rbt_Date - Structbilling.House_Lck_Adju_Amnt - Structconsmas.SD_Interest_chngto_SD_AVAIL);  //;
            tvBillbasis.setText ( Structbilling.Bill_Basis );

        }


        Intent intent = new Intent ( "android.location.GPS_ENABLED_CHANGE" );
        intent.putExtra ( "enabled", true );
        String provider = Settings.Secure.getString ( getContentResolver ( ), Settings.Secure.LOCATION_PROVIDERS_ALLOWED );

        if (!provider.contains ( "gps" )) { //if gps is disabled
            final Intent poke = new Intent ( );
            poke.setClassName ( "com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider" );
            poke.addCategory ( Intent.CATEGORY_ALTERNATIVE );
            poke.setData ( Uri.parse ( "3" ) );
            this.sendBroadcast ( poke );
        }
        btnPrint.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {

                btnPrint.setVisibility ( View.GONE );

                //Check if Location is ON
                if (isLocationEnabled ( getApplicationContext ( ) )) {

                    //Check if Mobile Data is ON
//                    if (isMobileDataEnabled()) {


                    //Check if LAT & LONG is ON
                    if (latitude1 == 0.00 && longitude1 == 0.00) {

                        Handler handler = new Handler ( );
                        handler.postDelayed ( new Runnable ( ) {
                            @Override
                            public void run() {
                                for (int i = 0; i <= 250; i++) {

                                    GPSTracker gps3 = new GPSTracker ( MeterState.getsActivity ( ) );

                                    latitude1 = gps3.getLatitude ( );
                                    longitude1 = gps3.getLongitude ( );
                                    gpstime = gps3.getTime ( );
                                    accuracy = gps3.getAccuracy ( );
                                    altitude = gps3.getAltitude ( );

                                    final SimpleDateFormat sdf = new SimpleDateFormat ( "dd-MM-yyyy kk:mm:ss" );
                                    Date d = new Date ( gps3.getTime ( ) );
                                    String sDate = sdf.format ( d );

                                    Structbilling.User_Long = String.valueOf ( longitude1 );
                                    Structbilling.User_Lat = String.valueOf ( latitude1 );
                                    Structbilling.GPS_TIME = sDate;
                                    Structbilling.GPS_ACCURACY = String.valueOf ( accuracy );
                                    Structbilling.GPS_ALTITUDE = String.valueOf ( altitude );


                                    if (latitude1 != 0.0) {
                                        break; // A unlabeled break is enough. You don't need a labeled break here.
                                    }

                                }
                                Intent intent = new Intent ( BillingViewActivity.this, Signature_Activity.class );
                                //                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity ( intent );
                                overridePendingTransition ( R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left );
                            }
                        }, 1 * 5 * 1000 );


                    } else {

                        Intent intent = new Intent ( BillingViewActivity.this, Signature_Activity.class );
                        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity ( intent );
                        overridePendingTransition ( R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left );

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
                    startLocationAlert.settingsrequest ( );
                }
            }

        } );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );
        switch (requestCode) {

            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:

                        btnPrint.setEnabled ( true );
                        btnPrint.setVisibility ( View.VISIBLE );
                        break;
                    case Activity.RESULT_CANCELED:
                        btnPrint.setVisibility ( View.VISIBLE );
                        startLocationAlert.settingsrequest ( );//keep asking if imp or do whatever
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
                locationMode = Settings.Secure.getInt ( context.getContentResolver ( ), Settings.Secure.LOCATION_MODE );

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace ( );
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString ( context.getContentResolver ( ), Settings.Secure.LOCATION_PROVIDERS_ALLOWED );
            return !TextUtils.isEmpty ( locationProviders );
        }


    }

    public Boolean isMobileDataEnabled() {
        Object connectivityService = getSystemService ( CONNECTIVITY_SERVICE );
        ConnectivityManager cm = (ConnectivityManager) connectivityService;

        try {
            Class <?> c = Class.forName ( cm.getClass ( ).getName ( ) );
            Method m = c.getDeclaredMethod ( "getMobileDataEnabled" );
            m.setAccessible ( true );
            return (Boolean) m.invoke ( cm );
        } catch (Exception e) {
            e.printStackTrace ( );
            return null;
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction ( );
        ((GlobalPool) getApplication ( )).onUserInteraction ( );
    }

    @Override
    public void userLogoutListaner() {
        finish ( );
        Intent intent = new Intent ( BillingViewActivity.this, MainActivity.class );
        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( intent );

    }


    public void onBackPressed() {

        finish ( ); // finish activity
        Intent intent = new Intent ( BillingViewActivity.this, BillingtypesActivity.class );
        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( intent );
        this.overridePendingTransition ( R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right );

    }


    public void saveData() {


        dbHelper = new DB ( this );
        SD = dbHelper.getWritableDatabase ( );

        String strPref = "SELECT PRINTER_PREF FROM USER_MASTER";
        android.util.Log.e ( "Sequence", "update " + strPref );

        Cursor getPref = SD.rawQuery ( strPref, null );

        if (getPref != null && getPref.moveToFirst ( )) {

            prev_pref = getPref.getString ( 0 );

            printer_catergory = prev_pref.split ( "_" )[0];
            printer_mfg = prev_pref.split ( "_" )[1];
            printer_roll = prev_pref.split ( "_" )[2];

            GSBilling.getInstance ( ).setPrinter_catergory ( printer_catergory );
            GSBilling.getInstance ( ).setPrinter_mfg ( printer_mfg );
            GSBilling.getInstance ( ).setPrinter_roll ( printer_roll );
        }

        if (Home.Mflag.equals ( "Y" )) {

            dbHelper4 = new DB ( getApplicationContext ( ) );
            SD4 = dbHelper4.getWritableDatabase ( );
//                    dbHelper4.insertSequence("DCNumber", dcSeq);
            //  dbHelper4.insertIntoBillingTable();
            dbHelper4.insertIntoMPBillingTable ( );
            new TextFileClass ( BillingViewActivity.this ).execute ( );
//                    dbHelper4.insertSequence("BillNumber", billseq);                    new TextFileClass(Signature_Activity.this).execute();

            Toast.makeText ( getApplicationContext ( ), "Welcome to metering", Toast.LENGTH_SHORT ).show ( );

        } else {

            if (prev_pref.equalsIgnoreCase ( "0_0_0" ) || prev_pref.equalsIgnoreCase ( "0_0_1" )) {//IMP_AMI
                Intent intent = new Intent ( this, com.fedco.mbc.amigos.MainActivity.class );
                GSBilling.getInstance ( ).setPrintSingle ( "<0x09>" );
                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity ( intent );
                overridePendingTransition ( R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left );
            } else if (prev_pref.equalsIgnoreCase ( "0_1_0" ) || prev_pref.equalsIgnoreCase ( "0_1_1" )) {//IMP_REG
                Toast.makeText ( this, "Under process", Toast.LENGTH_SHORT ).show ( );
            } else if (prev_pref.equalsIgnoreCase ( "0_2_0" ) || prev_pref.equalsIgnoreCase ( "0_2_1" )) {
//                    Intent intent = new Intent(Signature_Activity.this, SLPrintingMainActivity.class);
                Intent intent = new Intent ( this, SLPrintingMainActivity.class );
                GSBilling.getInstance ( ).setPrintSingle ( "" );
                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity ( intent );
                overridePendingTransition ( R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left );
            } else {
                Toast.makeText ( this, "Unable to find Printer", Toast.LENGTH_SHORT ).show ( );
            }
        }

        //Toast.makeText(getApplicationContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(Signature_Activity.this, MainActivity.class);
//                startActivity(intent);
        // Calling the same class
        //recreate();

    }

    private class TextFileClass extends AsyncTask <String, Void, Void> {

        private final Context context;

        public TextFileClass(Context c) {

            this.context = c;
//            this.error = status;
//            this.type = t;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog ( this.context );
            progress.setMessage ( "Please Wait.." );
            progress.show ( );
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                dbHelper2 = new DB ( getApplicationContext ( ) );
                SD2 = dbHelper2.getWritableDatabase ( );

                String selquer = "SELECT * FROM TBL_BILLING WHERE Upload_Flag='N' ";//WHERE Upload_Flag='N'
                Cursor curBillselect = SD2.rawQuery ( selquer, null );
                String arrStr[] = null;
                ArrayList <String> mylist = new ArrayList <String> ( );
                ArrayList <String> mylist1 = new ArrayList <String> ( );


                if (curBillselect != null && curBillselect.moveToFirst ( )) {
                    int i = 0;

//                    Log.e(getApplicationContext(), "SLPrintAct", "UpdateFLag to N : " + curBillselect.getString(0));

                    while (curBillselect.isAfterLast ( ) == false) {
                        Calendar c = Calendar.getInstance ( );
                        System.out.println ( "Current time => " + c.getTime ( ) );
                        i = i + 1;
                        SimpleDateFormat df = new SimpleDateFormat ( "dd-MMM-yy" );
                        String formattedDate = df.format ( c.getTime ( ) );
                        String column_24 = String.valueOf ( Double.valueOf ( curBillselect.getString ( 24 ) ) + Double.valueOf ( curBillselect.getString ( 31 ) ) );


                        mylist.add ( curBillselect.getString ( 0 ) + "}" + curBillselect.getString ( 1 ) + "}" + curBillselect.getString ( 2 ) + "}" +
                                curBillselect.getString ( 3 ) + "}" + curBillselect.getString ( 4 ) + "}" + curBillselect.getString ( 5 ) + "}" +
                                curBillselect.getString ( 6 ) + "}" + curBillselect.getString ( 7 ) + "}" + curBillselect.getString ( 8 ) + "}" +
                                curBillselect.getString ( 9 ) + "}" + curBillselect.getString ( 10 ) + "}" + curBillselect.getString ( 11 ) + "}" +
                                curBillselect.getString ( 12 ) + "}" + curBillselect.getString ( 13 ) + "}" + curBillselect.getString ( 14 ) + "}" +
                                curBillselect.getString ( 15 ) + "}" + curBillselect.getString ( 16 ) + "}" + curBillselect.getString ( 17 ) + "}" +
                                curBillselect.getString ( 18 ) + "}" + curBillselect.getString ( 19 ) + "}" + curBillselect.getString ( 20 ) + "}" +
                                curBillselect.getString ( 21 ) + "}" + curBillselect.getString ( 22 ) + "}" + curBillselect.getString ( 23 ) + "}" +
                                curBillselect.getString ( 24 ) + "}" + curBillselect.getString ( 25 ) + "}" + curBillselect.getString ( 26 ) + "}" +
                                curBillselect.getString ( 27 ) + "}" + curBillselect.getString ( 28 ) + "}" + curBillselect.getString ( 29 ) + "}" +
                                curBillselect.getString ( 30 ) + "}" + curBillselect.getString ( 31 ) + "}" + curBillselect.getString ( 32 ) + "}" +
                                curBillselect.getString ( 33 ) + "}" + curBillselect.getString ( 34 ) + "}" + curBillselect.getString ( 35 ) + "}" +
                                curBillselect.getString ( 36 ) + "}" + curBillselect.getString ( 37 ) + "}" + curBillselect.getString ( 38 ) + "}" +
                                curBillselect.getString ( 39 ) + "}" + curBillselect.getString ( 40 ) + "}" + curBillselect.getString ( 41 ) + "}" +
                                curBillselect.getString ( 42 ) + "}" + curBillselect.getString ( 43 ) + "}" + curBillselect.getString ( 44 ) + "}" +
                                curBillselect.getString ( 45 ) + "}" + curBillselect.getString ( 46 ) + "}" + curBillselect.getString ( 47 ) + "}" +
                                curBillselect.getString ( 48 ) + "}" + curBillselect.getString ( 49 ) + "}" + curBillselect.getString ( 50 ) + "}" +
                                curBillselect.getString ( 51 ) + "}" + curBillselect.getString ( 52 ) + "}" + curBillselect.getString ( 53 ) + "}" +
                                curBillselect.getString ( 54 ) + "}" + curBillselect.getString ( 55 ) + "}" + curBillselect.getString ( 56 ) + "}" +
                                curBillselect.getString ( 57 ) + "}" + curBillselect.getString ( 58 ) + "}" + curBillselect.getString ( 59 ) + "}" +
                                curBillselect.getString ( 60 ) + "}" + curBillselect.getString ( 61 ) + "}" + curBillselect.getString ( 62 ) + "}" +
                                curBillselect.getString ( 63 ) + "}" + curBillselect.getString ( 64 ) + "}" + curBillselect.getString ( 65 ) + "}" +
                                curBillselect.getString ( 66 ) + "}" + curBillselect.getString ( 67 ) + "}" + curBillselect.getString ( 68 ) + "}" +
                                curBillselect.getString ( 69 ) + "}" + curBillselect.getString ( 70 ) + "}" + curBillselect.getString ( 71 ) + "}" +
                                curBillselect.getString ( 72 ) + "}" + curBillselect.getString ( 73 ) + "}" + curBillselect.getString ( 74 ) + "}" +
                                curBillselect.getString ( 75 ) + "}" + curBillselect.getString ( 76 ) + "}" + curBillselect.getString ( 77 ) + "}" +
                                curBillselect.getString ( 78 ) + "}" + curBillselect.getString ( 79 ) + "}" + curBillselect.getString ( 80 ) + "}" +
                                curBillselect.getString ( 81 ) + "}" + curBillselect.getString ( 82 ) + "}" + curBillselect.getString ( 83 ) + "}" +
                                curBillselect.getString ( 84 ) + "}" + curBillselect.getString ( 85 ) + "}" + curBillselect.getString ( 86 ) + "}" +
                                curBillselect.getString ( 87 ) + "}" + curBillselect.getString ( 88 ) + "}" + curBillselect.getString ( 89 ) + "}" +
                                curBillselect.getString ( 90 ) + "}" + curBillselect.getString ( 91 ) + "}" + curBillselect.getString ( 92 ) + "}" +
                                curBillselect.getString ( 93 ) + "}" + curBillselect.getString ( 94 ) + "}" + curBillselect.getString ( 95 ) + "}" +
                                curBillselect.getString ( 96 ) + "}" + curBillselect.getString ( 97 ) + "}" + curBillselect.getString ( 98 ) + "}" +
                                curBillselect.getString ( 99 ) + "}" + curBillselect.getString ( 100 ) + "}" + curBillselect.getString ( 101 ) + "}" +
                                curBillselect.getString ( 102 ) + "}" + curBillselect.getString ( 103 ) + "}" + curBillselect.getString ( 104 ) + "}" +
                                curBillselect.getString ( 105 ) + "}" + curBillselect.getString ( 106 ) + "}" + curBillselect.getString ( 107 ) + "}" +
                                curBillselect.getString ( 108 ) + "}" + curBillselect.getString ( 109 ) + "}" + curBillselect.getString ( 110 ) + "}" +
                                curBillselect.getString ( 111 ) + "}" + curBillselect.getString ( 112 ) + "}" + curBillselect.getString ( 113 ) + "}" +
                                curBillselect.getString ( 114 ) + "}" + curBillselect.getString ( 115 ) + "}" + curBillselect.getString ( 116 ) + "}" +
                                curBillselect.getString ( 117 ) + "}" + curBillselect.getString ( 118 ) + "}" + curBillselect.getString ( 119 ) + "}" +
                                curBillselect.getString ( 120 ) + "}" + curBillselect.getString ( 121 ) + "}" + curBillselect.getString ( 122 ) + "}" +
                                curBillselect.getString ( 123 ) + "}" + curBillselect.getString ( 124 ) + "}" + curBillselect.getString ( 125 ) + "}" +
                                curBillselect.getString ( 126 ) + "}" + curBillselect.getString ( 127 ) + "}" + curBillselect.getString ( 128 ) + "}" +
                                curBillselect.getString ( 129 ) + "}" + curBillselect.getString ( 130 ) + "}" + curBillselect.getString ( 131 ) + "}" +
                                curBillselect.getString ( 132 ) + "}" + curBillselect.getString ( 133 ) + "}" + curBillselect.getString ( 134 ) + "}" +
                                curBillselect.getString ( 135 ) + "}" + curBillselect.getString ( 136 ) + "}" + curBillselect.getString ( 137 ) + "}" +
                                curBillselect.getString ( 138 ) + "}" + curBillselect.getString ( 139 ) + "}" + curBillselect.getString ( 140 ) + "}" +
                                curBillselect.getString ( 141 ) + "}" + curBillselect.getString ( 142 ) + "}" + curBillselect.getString ( 143 ) + "}" +
                                curBillselect.getString ( 144 ) + "}" + curBillselect.getString ( 145 ) + "}" + curBillselect.getString ( 146 ) + "}" +
                                curBillselect.getString ( 147 ) + "}" + curBillselect.getString ( 148 ) + "}" + curBillselect.getString ( 149 ) + "}" +
                                curBillselect.getString ( 150 ) + "}" + curBillselect.getString ( 151 ) + "}" + curBillselect.getString ( 152 ) + "}" +
                                curBillselect.getString ( 153 ) + "}" + curBillselect.getString ( 154 ) + "}" + curBillselect.getString ( 155 ) + "}" +
                                curBillselect.getString ( 156 ) + "}" + curBillselect.getString ( 157 ) + "}" + curBillselect.getString ( 158 ) + "}" +
                                curBillselect.getString ( 159 ) + "}" + curBillselect.getString ( 160 ) + "}" + curBillselect.getString ( 161 ) + "}" +
                                curBillselect.getString ( 162 ) + "}" + curBillselect.getString ( 163 ) + "}" + curBillselect.getString ( 164 ) + "}" +
                                curBillselect.getString ( 165 ) + "}" + curBillselect.getString ( 166 ) + "}" + curBillselect.getString ( 167 ) + "}" +
                                curBillselect.getString ( 168 ) + "}" + curBillselect.getString ( 169 ) + "}" + curBillselect.getString ( 170 ) + "}" +
                                curBillselect.getString ( 171 ) + "}" + curBillselect.getString ( 172 ) + "}" + curBillselect.getString ( 173 ) + "}" +
                                curBillselect.getString ( 174 ) + "}" + curBillselect.getString ( 175 ) + "}" + curBillselect.getString ( 176 ) + "}" +
                                curBillselect.getString ( 177 ) + "}" + curBillselect.getString ( 178 ) + "}" + curBillselect.getString ( 179 ) + "}" +
                                curBillselect.getString ( 180 ) + "}" + curBillselect.getString ( 181 ) + "}" + curBillselect.getString ( 182 ) + "}" +
                                curBillselect.getString ( 183 ) + "}" + curBillselect.getString ( 184 ) + "}" + curBillselect.getString ( 185 ) + "}" +
                                curBillselect.getString ( 186 ) + "}" + curBillselect.getString ( 187 ) + "}" + curBillselect.getString ( 188 ) + "}" +
                                curBillselect.getString ( 189 ) + "}" + curBillselect.getString ( 190 ) + "}" + curBillselect.getString ( 191 ) + "}" +
                                curBillselect.getString ( 192 ) + "}" + curBillselect.getString ( 193 ) + "}" + curBillselect.getString ( 194 ) + "}" +
                                curBillselect.getString ( 195 ) + "}" + curBillselect.getString ( 196 ) + "}" + curBillselect.getString ( 197 ) + "}" +
                                curBillselect.getString ( 198 ) + "}" + curBillselect.getString ( 199 ) + "}" + curBillselect.getString ( 200 ) + "}" +
                                curBillselect.getString ( 201 ) + "}" + curBillselect.getString ( 202 ) + "}" + curBillselect.getString ( 203 ) + "}" +
                                curBillselect.getString ( 204 ) + "}" + curBillselect.getString ( 205 ) + "}" + curBillselect.getString ( 206 ) + "}" +
                                curBillselect.getString ( 207 ) + "}" + curBillselect.getString ( 208 ) + "}" + curBillselect.getString ( 209 ) + "}" +
                                curBillselect.getString ( 210 ) + "}" + curBillselect.getString ( 211 ) + "}" + curBillselect.getString ( 212 ) + "}" +
                                curBillselect.getString ( 213 ) + "}" + curBillselect.getString ( 214 ) + "}" + curBillselect.getString ( 215 ) + "}" +
                                curBillselect.getString ( 216 ) );


                        mylist1.add ( curBillselect.getString ( 60 ) + "$" + curBillselect.getString ( 0 ) + "$" + curBillselect.getString ( 5 ) + "$" +
                                curBillselect.getString ( 61 ) + "$" + curBillselect.getString ( 11 ) + "$" + curBillselect.getString ( 62 ) + "$" +
                                curBillselect.getString ( 2 ) + "$" + i + "$" + curBillselect.getString ( 7 ) + "$" +
                                curBillselect.getString ( 212 ) + "$" + curBillselect.getString ( 1 ) + "$" + formattedDate + "$" +
                                curBillselect.getString ( 63 ) + "$" + curBillselect.getString ( 8 ) + "$" + curBillselect.getString ( 64 ) + "$" +
                                curBillselect.getString ( 65 ) + "$" + curBillselect.getString ( 66 ) + "$" + curBillselect.getString ( 39 ) + "$" +
                                curBillselect.getString ( 10 ) + "$" + curBillselect.getString ( 67 ) + "$" + curBillselect.getString ( 68 ) + "$" +
                                curBillselect.getString ( 69 ) + "$" + curBillselect.getString ( 70 ) + "$" + curBillselect.getString ( 71 ) + "$" +
                                curBillselect.getString ( 182 ) + "$" + curBillselect.getString ( 72 ) + "$" + curBillselect.getString ( 73 ) + "$" +
                                column_24 + "$" + curBillselect.getString ( 74 ) + "$" + "0" + "$" + curBillselect.getString ( 75 ) + "$" +
                                curBillselect.getString ( 76 ) + "$" + curBillselect.getString ( 27 ) + "$" + curBillselect.getString ( 77 ) + "$" +
                                curBillselect.getString ( 26 ) + "$" + curBillselect.getString ( 78 ) + "$" + curBillselect.getString ( 79 ) + "$" +
                                curBillselect.getString ( 80 ) + "$" + curBillselect.getString ( 81 ) + "$" + curBillselect.getString ( 82 ) + "$" +
                                curBillselect.getString ( 201 ) + "$" + curBillselect.getString ( 83 ) + "$" + curBillselect.getString ( 179 ) + "$" +
                                curBillselect.getString ( 85 ) + "$" + curBillselect.getString ( 86 ) + "$" + curBillselect.getString ( 169 ) + "$" +
                                curBillselect.getString ( 87 ) + "$" + curBillselect.getString ( 88 ) + "$" + curBillselect.getString ( 89 ) + "$" +
                                curBillselect.getString ( 90 ) + "$" + curBillselect.getString ( 91 ) + "$" + curBillselect.getString ( 92 ) + "$" +
                                curBillselect.getString ( 93 ) + "$" + curBillselect.getString ( 94 ) + "$" + curBillselect.getString ( 95 ) + "$" +
                                curBillselect.getString ( 96 ) + "$" + curBillselect.getString ( 97 ) + "$" + curBillselect.getString ( 98 ) + "$" +
                                curBillselect.getString ( 99 ) + "$" + curBillselect.getString ( 178 ) + "$0$" );

//                        mylistbck.add(curBillselect.getString(44)+ "|" +curBillselect.getString(50) );

                        moveFile ( ZipSourcePath, curBillselect.getString ( 48 ), ZipCopyPath );
                        moveFile ( ZipSourcePath, curBillselect.getString ( 49 ), ZipCopyPath );

                        curBillselect.moveToNext ( );
                    }

                    generateNoteOnSD ( getApplicationContext ( ), "Meter.csv", mylist );
//                    generateNoteOnSD(getApplicationContext(), "Meter1.csv", mylist1);
//                    generatebackupNoteOnSD(getApplicationContext(), "mbc_Ob.csv", mylist);

                }

                BillingViewActivity.this.runOnUiThread ( new Runnable ( ) {

                    @Override
                    public void run() {
                        progress.dismiss ( );
                        new PostClass ( BillingViewActivity.this ).execute ( );
                    }
                } );


            } catch (NullPointerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace ( );
            }
            return null;
        }

        protected void onPostExecute() {
            progress.dismiss ( );
//            new PostClass(Signature_Activity.this).execute();
        }

    }

    private class PostClass extends AsyncTask <String, Void, Boolean> {

        private final Context context;
        public String succsess = null;

        public PostClass(Context c) {

            this.context = c;

        }

        protected void onPreExecute() {
            progress = new ProgressDialog ( this.context );
            progress.setMessage ( "Sending Data..." );
            progress.show ( );

            dbHelper3 = new DB ( getApplicationContext ( ) );
            SD3 = dbHelper3.getWritableDatabase ( );
            String frt[] = new String[0];

            String serImgQwer = "Select User_Mtr_Img,User_Sig_Img from TBL_BILLING WHERE Upload_Flag='N'";
            Cursor curBillImg = SD3.rawQuery ( serImgQwer, null );
            if (curBillImg != null && curBillImg.moveToFirst ( )) {

//                Log.e(getApplicationContext(), "SLPrintAct", "Update Success");


                mylistimagename.add ( curBillImg.getString ( 0 ) );
                mylistimagename.add ( curBillImg.getString ( 1 ) );
//                Log.e(getApplicationContext(), "SLPrintAct", "mtr_img" + curBillImg.getString(0) + "sig_img" + curBillImg.getString(1));
            }

            ArrayList <String> stringArrayList = new ArrayList <String> ( );
            for (int j = 0; j < mylistimagename.size ( ); j++) {

                stringArrayList.add ( Environment.getExternalStorageDirectory ( ) + "/MBC/" + mylistimagename.get ( j ) ); //add to arraylist
            }
            String[] files = stringArrayList.toArray ( new String[stringArrayList.size ( )] );
            String[] file = {Zip, signaturePathDes, photoPathDes};

            zipFolder ( ZipCopyPath, ZipDesPath );
            GSBilling.getInstance ( ).setFinalZipName ( ZipDesPathdup );

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                // Set your file path here
//                System.out.println("FILENAME IS1 "+GSBilling.getInstance().getFinalZipName());
                FileInputStream fstrm = new FileInputStream ( Environment.getExternalStorageDirectory ( ).toString ( ) + GSBilling.getInstance ( ).getFinalZipName ( ) + ".zip" );
//                Log.e(getApplicationContext(), "SLPrintAct", "FILENAME IS12 " + fstrm);

                // Set your server page url (and the file title/description)

//                HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi_Testing/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                HttpFileUpload hfu = new HttpFileUpload ( URLS.DataComm.billUpload, "" + GSBilling.getInstance ( ).getFinalZipName ( ), ".zip" );
//                HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");

                // Log.e(getApplicationContext(), "http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles", "" + GSBilling.getInstance().getFinalZipName()+".zip");
//                Log.e(getApplicationContext(), "SLPrintAct", "going out " + GSBilling.getInstance().getFinalZipName() + ".zip");
                int status = hfu.Send_Now ( fstrm );
                if (status != 200) {
//                    succsess = "1";
                    BillingViewActivity.this.runOnUiThread ( new Runnable ( ) {

                        @Override
                        public void run() {
                            progress.dismiss ( );
                            Toast.makeText ( BillingViewActivity.this, "Internaly Stored Due to No Connectivity", Toast.LENGTH_LONG ).show ( );

                            Intent intent = new Intent ( BillingViewActivity.this, BillingtypesActivity.class );
                            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                            intent.putExtra ( "printtype", print_type );
                            startActivity ( intent );
                            overridePendingTransition ( R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left );

                        }
                    } );


                } else {
//                    succsess = "0";
                    dbHelper2 = new DB ( getApplicationContext ( ) );
                    SD2 = dbHelper2.getWritableDatabase ( );

//                        String updatequer = "UPDATE  TBL_BILLING  SET Upload_Flag = 'Y' WHERE  Cons_Number = '" + curBillselect.getString(0) + "' and  Bill_Month='" + curBillselect.getString(5) + "'";
                    String updatequer = "UPDATE  TBL_BILLING  SET Upload_Flag = 'Y'";
                    Cursor curBillupdate = SD2.rawQuery ( updatequer, null );
                    if (curBillupdate != null && curBillupdate.moveToFirst ( )) {
//                        Log.e(getApplicationContext(), "SLPrintAct", "Update Success");
                    }
                    BillingViewActivity.this.runOnUiThread ( new Runnable ( ) {

                        @Override
                        public void run() {
                            progress.dismiss ( );
                            Toast.makeText ( BillingViewActivity.this, " Successfully Uploaded to Server ", Toast.LENGTH_LONG ).show ( );
                            Intent intent = new Intent ( BillingViewActivity.this, BillingtypesActivity.class );
                            //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            //  intent.putExtra("printtype", print_type);
                            startActivity ( intent );
                            finish ( );
                            overridePendingTransition ( R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left );

                        }
                    } );


                }

//                return true;

            } catch (Exception e) {
                // Error: File not found
                succsess = "0";
                e.printStackTrace ( );
//                return  false;
            }

            return true;

        }

        protected void onPostExecute() {
            progress.dismiss ( );
            new File ( Environment.getExternalStorageDirectory ( ).toString ( ) + GSBilling.getInstance ( ).getFinalZipName ( ) + ".zip" ).delete ( );
            Intent intent = new Intent ( BillingViewActivity.this, BillingtypesActivity.class );
            startActivity ( intent );
            overridePendingTransition ( R.anim.anim_slide_in_left,
                    R.anim.anim_slide_out_left );

        }
    }

    public static void zipFolder(String inputFolderPath, String outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream ( outZipPath );
//            GSBilling.getInstance().setFinalZipName();
            ZipOutputStream zos = new ZipOutputStream ( fos );
            File srcFile = new File ( inputFolderPath );
            File[] files = srcFile.listFiles ( );
            android.util.Log.d ( "", "Zip directory: " + srcFile.getName ( ) );
            for (int i = 0; i < files.length; i++) {
                android.util.Log.d ( "", "Adding file: " + files[i].getName ( ) );
                byte[] buffer = new byte[1024];
                FileInputStream fis = new FileInputStream ( files[i] );
                zos.putNextEntry ( new ZipEntry ( files[i].getName ( ) ) );
                int length;
                while ((length = fis.read ( buffer )) > 0) {
                    zos.write ( buffer, 0, length );
                }
                zos.closeEntry ( );
                fis.close ( );
            }
            System.out.println ( "helloooo" + srcFile.delete ( ) );
            zos.close ( );
        } catch (IOException ioe) {
            android.util.Log.e ( "", ioe.getMessage ( ) );
        }
    }

    public void generateNoteOnSD(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File ( Environment.getExternalStorageDirectory ( ), "MBC/Downloadsingular/" );
            if (!root.exists ( )) {
                root.mkdirs ( );
            }
            File gpxfile = new File ( root, sFileName );
            FileWriter writer = new FileWriter ( gpxfile );
            int length = sBody.size ( );
            for (int i = 0; i < length; i++) {
                System.out.println ( "selqwer1234 " + sBody.get ( i ) );

                writer.append ( sBody.get ( i ).toString ( ) );
                writer.append ( "\n" );
            }

            writer.flush ( );
            writer.close ( );

        } catch (IOException e) {
            e.printStackTrace ( );
        }
    }

    public void generatebackupNoteOnSD(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File ( Environment.getExternalStorageDirectory ( ), "MBC/" );
            if (!root.exists ( )) {
                root.mkdirs ( );
            }
            File gpxfile = new File ( root, sFileName );
            FileWriter writer = new FileWriter ( gpxfile );
            int length = sBody.size ( );
            for (int i = 0; i < length; i++) {
                System.out.println ( "selqwer1234 " + sBody.get ( i ) );
                BufferedWriter writerbuf = new BufferedWriter ( new FileWriter ( gpxfile, true ) );
                writerbuf.write ( sBody.get ( i ).toString ( ) );
                writerbuf.close ( );
//                writer.append(sBody.get(i).toString());
//                writer.append("\n");
            }

            writer.flush ( );
            writer.close ( );

        } catch (IOException e) {
            e.printStackTrace ( );
        }
    }

    private void moveFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File ( outputPath );
            if (!dir.exists ( )) {
                dir.mkdirs ( );
            }


            in = new FileInputStream ( inputPath + inputFile );
            out = new FileOutputStream ( outputPath + inputFile );

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read ( buffer )) != -1) {
                out.write ( buffer, 0, read );
            }
            in.close ( );
            in = null;

            // write the output file
            out.flush ( );
            out.close ( );
            out = null;

////             delete the original file
//            new File(inputPath + inputFile).delete();


        } catch (FileNotFoundException e) {
            e.printStackTrace ( );
        } catch (IOException e) {
            e.printStackTrace ( );
        }
    }
}





