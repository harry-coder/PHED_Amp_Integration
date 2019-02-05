package com.fedco.mbc.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.amigos.DuplicateCollectionPrint;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.bluetoothprinting.GlobalPool;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.model.Structcollection;
import com.fedco.mbc.model.Structcolmas;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.GPSTracker;
import com.fedco.mbc.utils.UtilAppCommon;
import com.fedco.mbc.utils.Utility;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.fedco.mbc.activity.StartLocationAlert.REQUEST_CHECK_SETTINGS;

/**
 * Created by soubhagyarm on 17-05-2016.
 */
public class CollectionView extends AppCompatActivity implements LogoutListaner {

    TextView tvsession, tvivrs, tvConno, tvPayby, tvAmntpay, tvAftedd, tvConname, tvminAmnt, tvCurBill, tvTotalamnt, tvaftdd2,
            tvDiv, tvSubdiv, tvSec, tvRcpt,tvphone,tvaddress,tvmeterno,tvfactortype,tvfactoramt,txtfactortype,txtfactoramount;
    Button btnPrint;
    Logger Log;
    Float TotalARR;
    String Thermal_enable, strtime, strDate, key;
    SessionManager session;
    GPSTracker gps, gps2;
    double latitude;
    double longitude;
    long gpstime, ser_key, session_key;
    double altitude;
    double accuracy;
    Location location;
    double minPay;
    double m_SurCharge;
    double latitude1 = 0.0;
    double longitude1 = 0.0;
    public static String reciept;

    LocationManager locationManager;
    StartLocationAlert startLocationAlert;
    Context mContext;
    UtilAppCommon comApp;
    SQLiteDatabase sqLiteDatabaseCol;
    DB dbHelperCol;
    View rowView;
    LinearLayout mContainerView;
    TextView txt1,txt2,valtxt1,valtxt2;
    private View mExclusiveEmptyView;
    HashMap<String,String> hashMap=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_col_view);

        try {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
            getSupportActionBar().setLogo(R.mipmap.logo);
            setTitle("SMARTPHED");
        } catch (NullPointerException npe) {
            Log.e(getApplicationContext(), "Billing Act", "ActionBar Throwing null Pointer", npe);
        }
        ((GlobalPool)getApplication()).registerSessionListaner(this);
        ((GlobalPool)getApplication()).startUserSession();

        session = new SessionManager(getApplicationContext());
        gps = new GPSTracker(CollectionView.this);

        mContainerView = (LinearLayout) findViewById(R.id.parentview);
        Intent intent1 = getIntent();
        hashMap= (HashMap<String, String>) intent1.getSerializableExtra("HeaderDeatils");
        // String json=


            inflateEditRow();


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

        Structcolmas.USER_LONG = String.valueOf(longitude);
        Structcolmas.USER_LAT = String.valueOf(latitude);
        Structcolmas.GPS_TIME = sDate;
        Structcolmas.GPS_ACCURACY = String.valueOf(accuracy);
        Structcolmas.GPS_ALTITUDE = String.valueOf(altitude);
        Structcolmas.BAT_STATE = GSBilling.getInstance().getBatStr();
        Structcolmas.SIG_STATE = GSBilling.getInstance().getSignalStr();


//        Structcolmas.VER_CODE = GSBilling.getInstance().getVerCode();


        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

//        this.minPay = Float.parseFloat(Structcollection.CUR_TOT_BILL);//* Float.parseFloat(Structcollection.MCP);
        tvConno = (TextView) findViewById(R.id.consumer_no);

        tvPayby = (TextView) findViewById(R.id.bill_no);
        tvAmntpay = (TextView) findViewById(R.id.amount_pay);
        tvAftedd = (TextView) findViewById(R.id.amount_due);
        tvConname = (TextView) findViewById(R.id.consumer_name);
//        this.m_SurCharge = Math.ceil(Double.valueOf(Structcollection.AMNT_AFT_DUE_DATE)) - Math.ceil(Double.valueOf(Structcollection.AMNT_BFR_RBT_DATE));
//        tvaftdd2 = (TextView) findViewById(R.id.due);
//        tvminAmnt = (TextView) findViewById(R.id.mintotal);

        tvDiv = (TextView) findViewById(R.id.consumer_div);
        tvSubdiv = (TextView) findViewById(R.id.consumer_sdiv);
        tvSec = (TextView) findViewById(R.id.consumer_sec);
        tvphone = (TextView) findViewById(R.id.consumer_ivrs_val);
        tvaddress = (TextView) findViewById(R.id.consumer_addr);
//        tvRcpt = (TextView) findViewById(R.id.receipt_no);
        tvivrs = (TextView) findViewById(R.id.consumer_ivrs_val);
//        tvmeterno = (TextView) findViewById(R.id.session_name);
        tvfactortype =(TextView)findViewById(R.id.factortype);
        tvfactoramt =(TextView)findViewById(R.id.factoramt);
        txtfactortype =(TextView)findViewById(R.id.factor_type);
        txtfactoramount =(TextView)findViewById(R.id.factor_amt);






        try {

            Log.e(getApplicationContext(), "ColView", "inview " + Structcollection.CON_NO + Structcollection.AMNT_BFR_RBT_DATE + Structcollection.AMNT_AFT_RBT_DATE + Structcollection.AMNT_AFT_DUE_DATE + Structcollection.CON_NAME + Structcollection.AMNT_AFT_DUE_DATE);
//            tvConno.setText(Structcollection.CON_NO);
            tvConno.setText(GSBilling.getInstance().ConsumerNO);
//            tvPayby.setText(Structcollection.RBT_DATE);
            tvPayby.setText(GSBilling.getInstance().MeterNo);
            tvAmntpay.setText(GSBilling.getInstance().ARREARS);
            tvAftedd.setText( GSBilling.getInstance().CON_TYPE.toUpperCase());
            tvConname.setText(GSBilling.getInstance().CONS_NAME.toUpperCase());
            tvaddress.setText(GSBilling.getInstance().Addresses.toUpperCase());
            tvphone.setText(GSBilling.getInstance().MOBILENO);
//            tvaftdd2.setText("" + Math.ceil(Double.valueOf(Structcollection.AMNT_AFT_DUE_DATE)));
            tvDiv.setText( GSBilling.getInstance().BSC.toUpperCase());
//            tvSubdiv.setText(Structcollection.SUB_DIV);
            tvSec.setText(GSBilling.getInstance().IBC.toUpperCase());
//            tvmeterno.setText(GSBilling.getInstance().MeterNo);


            tvfactortype.setText(GSBilling.getInstance().FACTOR.toUpperCase());
            tvfactoramt.setText(GSBilling.getInstance().FACTORAMOUNT);
//            tvivrs.setText(Structcollection.IVRS_NO);
//            tvsession.setText("" + GSBilling.getInstance().getSessionKey());
            if(GSBilling.getInstance().CON_TYPE.equals("PREPAID")){
                tvfactoramt.setVisibility(View.VISIBLE);
                tvfactortype.setVisibility(View.VISIBLE);
                txtfactortype.setVisibility(View.VISIBLE);
                txtfactoramount.setVisibility(View.VISIBLE);
            }else{
                tvfactoramt.setVisibility(View.GONE);
                tvfactortype.setVisibility(View.GONE);
                txtfactortype.setVisibility(View.GONE);
                txtfactoramount.setVisibility(View.GONE);
            }

        } catch (NullPointerException n) {
            Toast.makeText(CollectionView.this, "Download collection file", Toast.LENGTH_SHORT).show();
        }

        comApp = new UtilAppCommon();
        ser_key = comApp.findSequence(getApplicationContext(), "ReceiptNumber");

        key = comApp.UniqueCode(getApplicationContext());
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat fds = new SimpleDateFormat("kk:mm");
        strDate = sdf.format(cal.getTime());
        strtime = fds.format(cal.getTime());

        btnPrint = (Button) findViewById(R.id.print);
//        tvRcpt.setText(key + "/" + strDate.replace("-", "") + "/" + String.format("%05d", ser_key));
//        reciept = (String) tvRcpt.getText();

        /*
        * commented on 04/02/2018
        * */

//        if  today is greater than  Rebate Date then this.minPay = Float.parseFloat(Structcollection.CUR_TOT_BILL) + this.m_SurCharge;
//        if (strDate.compareTo(Structcollection.RBT_DATE) > 0) {
//            this.minPay = Float.parseFloat(Structcollection.CUR_TOT_BILL) + this.m_SurCharge;
//        } else {
//            this.minPay = Float.parseFloat(Structcollection.CUR_TOT_BILL);
//        }
        this.minPay = 0.0f;
        if (isLocationEnabled(getApplicationContext())) {
            btnPrint.setEnabled(true);
            btnPrint.setVisibility(View.VISIBLE);
        } else {
            btnPrint.setEnabled(false);
        }


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
                String collected_cons_number = "";
                dbHelperCol = new DB(getApplicationContext());
                sqLiteDatabaseCol = dbHelperCol.getWritableDatabase();

                String consumerChk = "SELECT CON_NO FROM TBL_COLMASTER_MP";
                Cursor todoCursor = sqLiteDatabaseCol.rawQuery(consumerChk, null);
                System.out.println("Select Result : " + consumerChk);

                if (todoCursor != null && todoCursor.moveToFirst()) {
                    do {
                        collected_cons_number = todoCursor.getString(0);
                    } while (todoCursor.moveToNext());
                } else {

                }
//                if (gps.canGetLocation()) {
                if (isLocationEnabled(getApplicationContext())) {

//                    if (isMobileDataEnabled()) {
                    try {
                        if (new CommonHttpConnection(getApplicationContext()).isConnectingToInternet()) {

                            if (latitude1 == 0.00 && longitude1 == 0.00) {
    //                                if (Structcolmas.USER_LONG.trim() .equals("0.0") && Structcolmas.USER_LAT.trim() .equals("0.0") ) {

                                // Execute some code after 2 seconds have passed
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        for (int i = 0; i <= 250; i++) {

                                            GPSTracker gps3 = new GPSTracker(CollectionView.this);
    //                                                gps2.onLocationChanged(location);
                                            latitude1 = gps3.getLatitude();
                                            longitude1 = gps3.getLongitude();
                                            gpstime = gps3.getTime();
                                            accuracy = gps3.getAccuracy();
                                            altitude = gps3.getAltitude();

                                            final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
                                            Date d = new Date(gps3.getTime());
                                            String sDate = sdf.format(d);

                                            Structcolmas.USER_LONG = String.valueOf(longitude1);
                                            Structcolmas.USER_LAT = String.valueOf(latitude1);
                                            Structcolmas.GPS_TIME = sDate;
                                            Structcolmas.GPS_ACCURACY = String.valueOf(accuracy);
                                            Structcolmas.GPS_ALTITUDE = String.valueOf(altitude);
                                            Structcolmas.INSTA_FLAG = "1";

                                            if (latitude1 != 0.0) {
                                                break; // A unlabeled break is enough. You don't need a labeled break here.
                                            }

                                        }
    //                            start coding here
    //                                    String consumerChk = "SELECT CON_NO FROM TBL_COLMASTER_MP";
    //                                    Cursor todoCursor = sqLiteDatabaseCol.rawQuery(consumerChk, null);
    //                                    System.out.println("Select Result : " + consumerChk);

    //                                    if (todoCursor != null && todoCursor.moveToFirst()) {
    //                                        do {
    //                                            String collected_cons_number = todoCursor.getString(0);

    //                                            dsfdsjafjsd
    //                                        }
    //                                        while (todoCursor.moveToNext());
    //                                    } else {
    //                                        Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
    //                                    }

    //                                    Intent intent = new Intent(CollectionView.this, PaymentType.class);
    //                                    intent.putExtra("MINPAY", minPay);
    //                                    //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    //                                    startActivity(intent);
    //                                    overridePendingTransition(R.anim.anim_slide_in_left,
    //                                            R.anim.anim_slide_out_left);

                                    }
                                }, 1 * 5 * 1000);

    //                            start coding here


    //                            if (todoCursor != null && todoCursor.moveToFirst()) {
    //                                do {
    //                                    collected_cons_number = todoCursor.getString(0);
                                if (tvConno.getText().toString().equalsIgnoreCase(collected_cons_number)) {
                                    showAlert();
                                } else {
                                    Intent intent = new Intent(CollectionView.this, PaymentType.class);
                                    intent.putExtra("MINPAY", minPay);
                                    //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.anim_slide_in_left,
                                            R.anim.anim_slide_out_left);
                                }
    //                                            dsfdsjafjsd
    //                                }
    //                                while (todoCursor.moveToNext());
    //                            } else {
    //                                Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
    //                            }

                            } else {
                                Structcolmas.INSTA_FLAG = "1";
                                if (tvConno.getText().toString().equalsIgnoreCase(collected_cons_number)) {
                                    showAlert();
                                } else {
                                    Intent intent = new Intent(CollectionView.this, PaymentType.class);
                                    intent.putExtra("MINPAY", minPay);
                                    //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.anim_slide_in_left,
                                            R.anim.anim_slide_out_left);
                                }
    //                            Intent intent = new Intent(CollectionView.this, PaymentType.class);
    //                            intent.putExtra("MINPAY", minPay);
    //                            //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    //                            startActivity(intent);
    //                            overridePendingTransition(R.anim.anim_slide_in_left,
    //                                    R.anim.anim_slide_out_left);

                            }

                        } else {
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName(
                                    "com.android.settings",
                                    "com.android.settings.Settings$DataUsageSummaryActivity"));
                            startActivity(intent);
                            System.out.println("NOT YUP");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
//                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);

                    startLocationAlert.settingsrequest();
                }

            }

        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        btnPrint.setVisibility(View.VISIBLE);
    }

    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        System.out.println("RESULT CODE   " + requestCode + " REQUEST  " + resultCode);
//        if (resultCode == 0) {
//            switch (requestCode) {
//                case 1:
//
//                    Intent intent = new Intent(getApplicationContext(), Collection.class);
//                    //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.anim_slide_in_left,
//                            R.anim.anim_slide_out_left);
//                    break;
//            }
//        }
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
//                        startLocationUpdates();
                        btnPrint.setEnabled(true);
                        btnPrint.setVisibility(View.VISIBLE);
                        break;
                    case Activity.RESULT_CANCELED:
                        btnPrint.setEnabled(true);
                        btnPrint.setVisibility(View.VISIBLE);
                        startLocationAlert.settingsrequest();//keep asking if imp or do whatever
                        break;
                }

                break;
//            case REQUEST_CHECK_SETTINGS2:
////                            gps2= new GPSTracker(BillingViewActivity.this);
//                btnPrint.setEnabled(true);
////                -----------------------
//
//                    if (latitude1==0.0 && longitude1==0.0 ) {
//
//                        for(int i =0 ; i<=250;i++){
//
//                            gps2= new GPSTracker(BillingViewActivity.this);
//                            //                            gps2.onLocationChanged(location);
//                            latitude1 = gps2.getLatitude();
//                            longitude1 = gps2.getLongitude();
//                            gpstime = gps2.getTime();
//                            accuracy = gps2.getAccuracy();
//                            altitude = gps2.getAltitude();
//
//                            final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
//                            Date d = new Date(gps2.getTime());
//                            String sDate = sdf.format(d);
//
//                            Structbilling.User_Long = String.valueOf(longitude1);
//                            Structbilling.User_Lat= String.valueOf(latitude1);
//                            Structbilling.GPS_TIME = sDate;
//                            Structbilling.GPS_ACCURACY = String.valueOf(accuracy);
//                            Structbilling.GPS_ALTITUDE = String.valueOf(altitude);
//                            Structbilling.BAT_STATE = GSBilling.getInstance().getBatStr();
//                            Structbilling.DSIG_STATE = GSBilling.getInstance().getSignalStr();
//                            Structbilling.VER_CODE = GSBilling.getInstance().getVerCode();
//
//                            if(latitude1!= 0.0){
//                                break; // A unlabeled break is enough. You don't need a labeled break here.
//                            }
//
//                        }
//
//                        Intent intent = new Intent(BillingViewActivity.this, Signature_Activity.class);
//                        //                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.anim_slide_in_left,
//                                R.anim.anim_slide_out_left);
//
//                    } else {
//
//                        gps2= new GPSTracker(BillingViewActivity.this);
//                        //                            gps2.onLocationChanged(location);
//                        latitude1 = gps2.getLatitude();
//                        longitude1 = gps2.getLongitude();
//                        gpstime = gps2.getTime();
//                        accuracy = gps2.getAccuracy();
//                        altitude = gps2.getAltitude();
//
//                        final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
//                        Date d = new Date(gps2.getTime());
//                        String sDate = sdf.format(d);
//
//                        Structbilling.User_Long = String.valueOf(longitude1);
//                        Structbilling.User_Lat= String.valueOf(latitude1);
//                        Structbilling.GPS_TIME = sDate;
//                        Structbilling.GPS_ACCURACY = String.valueOf(accuracy);
//                        Structbilling.GPS_ALTITUDE = String.valueOf(altitude);
//                        Structbilling.BAT_STATE = GSBilling.getInstance().getBatStr();
//                        Structbilling.DSIG_STATE = GSBilling.getInstance().getSignalStr();
//                        Structbilling.VER_CODE = GSBilling.getInstance().getVerCode();
//
//
//                        Intent intent = new Intent(BillingViewActivity.this, Signature_Activity.class);
//                        //                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.anim_slide_in_left,
//                                R.anim.anim_slide_out_left);
//
//                    }
//
////                ---------------------------
////                            System.out.println("success");
////                            Intent intent = new Intent(getApplicationContext(), BillingViewActivity.class);
////                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                            startActivity(intent);
////                            overridePendingTransition(R.anim.anim_slide_in_left,
////                                    R.anim.anim_slide_out_left);
//                            break;

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

    public void onBackPressed() {
        // if (exit) {
        finish(); // finish activity
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);
    }


    @Override
    protected void onStart() {
        super.onStart ( );
        checkBlueToothConnectivity ();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(CollectionView.this);
        dialog.setTitle("");
        dialog.setMessage("Already collected for this consumer. Do you want to collect again? ");
        dialog.setCancelable(false);
        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
//                btpObject.stopBluetoothService();
                Intent intent = new Intent(CollectionView.this, PaymentType.class);
//                Intent intent = new Intent(CollectionView.this, DuplicateCollectionPrint.class);
                intent.putExtra("MINPAY", minPay);
                //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });
        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                btnPrint.setVisibility(View.VISIBLE);
                dialogInterface.cancel();
            }
        });
        dialog.create().show();

    }
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        ((GlobalPool)getApplication()).onUserInteraction();
    }

    @Override
    public void userLogoutListaner() {
        finish();
        Intent intent=new Intent(CollectionView.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    private void inflateEditRow() {

        try {
            if (!hashMap.isEmpty()) {
                Iterator<Map.Entry<String, String>> it = hashMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> pair = (Map.Entry<String, String>) it.next();
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    rowView = inflater.inflate(R.layout.table_row_layout, null);
        /*final ImageButton deleteButton = (ImageButton) rowView
                .findViewById(R.id.buttonDelete);*/
                    txt1 = (TextView) rowView.findViewById(R.id.txt1);
                    txt2 = (TextView) rowView.findViewById(R.id.txt2);
                    txt1.setId(mContainerView.getChildCount());
                    txt2.setId(mContainerView.getChildCount());
                    txt1.setText(pair.getKey());
                    txt2.setText(pair.getValue());
                    mExclusiveEmptyView = rowView;
                    mContainerView.addView(rowView, mContainerView.getChildCount() - 1);
                    //   System.out.println(pair.getKey() + " = " + pair.getValue());
                }

            }
        }catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),Toast.LENGTH_LONG).show();
        }
    }


    public void checkBlueToothConnectivity(){

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //return mBluetoothAdapter.isEnabled();

        if(mBluetoothAdapter==null){
            return;
        }
        else if(mBluetoothAdapter.getState()==BluetoothAdapter.STATE_ON){
            Toast.makeText ( this, "Bluetooth is connected!!", Toast.LENGTH_SHORT ).show ( );
        }
        else {

            mBluetoothAdapter.enable ();

            Toast.makeText ( this, "Bluetooth Turned ON", Toast.LENGTH_SHORT ).show ( );
        }
    }
}
