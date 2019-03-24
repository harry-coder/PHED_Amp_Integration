package com.fedco.mbc.activity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.BuildConfig;
import com.fedco.mbc.CustomClasses.TextDrawable;
import com.fedco.mbc.R;
import com.fedco.mbc.amigos.DuplicateAmigoPrinting;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.billinglogic.CBillling;
import com.fedco.mbc.bluetoothprinting.DuplicateBillPrint;
import com.fedco.mbc.bluetoothprinting.GlobalPool;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.model.Structbilling;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.model.Structtariff;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.GPSTracker;
import com.fedco.mbc.utils.UtilAppCommon;
import com.fedco.mbc.utils.Utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CameraActivity extends AppCompatActivity implements LogoutListaner {

    TextView tvAcc, tvName, tvAdd, tvMn, tvTc, tvCl, tvOCN, tvBM, tvIVRS;
    Button BtnContinue;
    final Context context = this;
    Logger Log;
    public static final int MEDIA_TYPE_IMAGE = 1;
    Date cashDate;
    public static final String TAG = "ShootAndCropActivity";
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.bmp";
    public static final int REQUEST_CODE_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;

    private File mFileTemp;
    SQLiteDatabase SD, SD3, SD5;
    DB dbHelper, dbHelper2, dbHelper5;
    UtilAppCommon uac;
    UtilAppCommon appCom, appCom1;
    CBillling calBill;
    SessionManager session;
    String currentDateandTime, previous_date;
    ImageView imgWarnign;
    SimpleDateFormat comparingDate;
    Date TarifToDate;
    Date dateToday;
    Date prevMtrDate;
    Cursor curSearchdata;
    String tariffEffetiveDate;
    String load_unit;
    String tarrif_Cate;
    Boolean NoTariffFlag = false;

    SweetAlertDialog sDialog;
    String printer_catergory, printer_mfg, printer_roll, prev_pref, slot1;


    EditText et_mobileNo,et_mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_demo );

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder ( );
        StrictMode.setVmPolicy ( builder.build ( ) );

        try {
            getSupportActionBar ( ).setHomeAsUpIndicator ( R.drawable.back );
            getSupportActionBar ( ).setLogo ( R.mipmap.logo );
            setTitle ( "SMARTPHED" );
        } catch (NullPointerException npe) {
            Logger.e ( getApplicationContext ( ), "Billing Act", "ActionBar Throwing null Pointer", npe );
        }

        File folder = new File ( Environment.getExternalStorageDirectory ( )
                + File.separator + "/MBC/Images/" );
        if (!folder.exists ( )) {
            folder.mkdir ( );
        }

        ((GlobalPool) getApplication ( )).registerSessionListaner ( this );
        ((GlobalPool) getApplication ( )).startUserSession ( );

        UtilAppCommon uc = new UtilAppCommon ( );
        UtilAppCommon uac = new UtilAppCommon ( );
        session = new SessionManager ( getApplicationContext ( ) );
        uc.princonmas ( );
        String Datetime;
        //	CBillling cb=new CBillling();
        Calendar c = Calendar.getInstance ( );

        SimpleDateFormat month_date = new SimpleDateFormat ( "MMMM-yy" );
        final String month_name = month_date.format ( c.getTime ( ) );

        comparingDate = new SimpleDateFormat ( "dd-MM-yyyy" );
        String currentDateTime = comparingDate.format ( new Date ( ) );
        try {
            dateToday = comparingDate.parse ( currentDateTime );
        } catch (ParseException e) {
            e.printStackTrace ( );
        }

        /**LOGGER ACTIVATED FOR THIS ACTIVITY**/
        tvAcc = (TextView) findViewById ( R.id.TextViewACCValue );
        tvName = (TextView) findViewById ( R.id.TextViewNameValue );
        tvAdd = (TextView) findViewById ( R.id.TextViewADDValue );
        tvMn = (TextView) findViewById ( R.id.TextViewMNValue );
        tvTc = (TextView) findViewById ( R.id.TextViewTCValue );
        tvCl = (TextView) findViewById ( R.id.TextViewTRRRValue );
        tvOCN = (TextView) findViewById ( R.id.TextViewOCNValue );
        tvBM = (TextView) findViewById ( R.id.TextViewBMValue );
        tvIVRS = (TextView) findViewById ( R.id.TextViewIVRSValue );

        BtnContinue = (Button) findViewById ( R.id.ButtonContinue );
        imgWarnign = (ImageView) findViewById ( R.id.imageWarning );

        System.out.println ( "Location code: " + Structconsmas.LOC_CD );
        System.out.println ( "Consumer Number: " + Structconsmas.Consumer_Number );

        String IVRS_NO = Structconsmas.Consumer_Number;

        et_mail= (EditText) findViewById ( R.id.EditTextemail );
        et_mobileNo= (EditText) findViewById ( R.id.EditTextmobno );
        String code = "0 ";
        et_mobileNo.setCompoundDrawablesWithIntrinsicBounds ( new TextDrawable ( code ), null, null, null );
        et_mobileNo.setCompoundDrawablePadding ( code.length ( ) * 10 );


        tvAcc.setText ( " :  " + Structconsmas.Consumer_Number );
        tvIVRS.setText ( " :  " + IVRS_NO );
        tvName.setText ( " :  " + Structconsmas.Name );
        tvAdd.setText ( " :  " + Structconsmas.address1 + Structconsmas.address2 + Structconsmas.H_NO + Structconsmas.MOH + Structconsmas.CITY );
        tvMn.setText ( " :  " + Structconsmas.Meter_S_No );
        tvTc.setText ( " :  " + Structconsmas.Tariff_Code );
        // tvCl.setText(Float.toString(Structconsmas.Load));
        tvCl.setText ( " :  " + Structconsmas.Load + " " + LoadConvert ( ) );
        tvOCN.setText ( " :  " + Structconsmas.Old_Consumer_Number );
        tvBM.setText ( " :  " + Structconsmas.Bill_Mon );

        imgWarnign.setVisibility ( View.GONE );
        String state = Environment.getExternalStorageState ( );
        if (Environment.MEDIA_MOUNTED.equals ( state )) {
            mFileTemp = new File ( Environment.getExternalStorageDirectory ( ), TEMP_PHOTO_FILE_NAME );
        } else {
            mFileTemp = new File ( getFilesDir ( ), TEMP_PHOTO_FILE_NAME );
        }
        dbHelper = new DB ( getApplicationContext ( ) );
        SD = dbHelper.getWritableDatabase ( );

        // ----------------------------------------------------------------------------------------- MPMBCApp
        try {

            //for holding retrieve data from query and store in the form of rows
            String ret = "SELECT MAX(EFFECTIVE_DATE) FROM TBL_TARRIF_PHED DESC LIMIT 1";
            System.out.println ( "query---" + ret );
            curSearchdata = SD.rawQuery ( ret, null );
            Toast.makeText ( getApplicationContext ( ), "Total data Found:" + curSearchdata.getCount ( ), Toast.LENGTH_LONG ).show ( );

            if (curSearchdata.moveToFirst ( )) {

                do {

                    tariffEffetiveDate = curSearchdata.getString ( 0 );
                    String _date = curSearchdata.getString ( 0 ).replace ( "/", "-" );
                    try {
                        //data base tarrif todate
                        TarifToDate = comparingDate.parse ( _date );
                    } catch (ParseException e) {
                        e.printStackTrace ( );
                    }
                } while (curSearchdata.moveToNext ( ));//Move the cursor to the next row.
            } else {
                Toast.makeText ( getApplicationContext ( ), "No data found", Toast.LENGTH_LONG ).show ( );
            }
        } catch (Exception e) {
            Toast.makeText ( getApplicationContext ( ), "No data found" + e.getMessage ( ), Toast.LENGTH_LONG ).show ( );
        }


        dbHelper5 = new DB ( getApplicationContext ( ) );
        SD5 = dbHelper5.getWritableDatabase ( );
        if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase ( "S" )) {
            getRMScodefromSybase ( Structconsmas.Tariff_Code.trim ( ), dbHelper5, SD5 );
        }

        dbHelper5 = null;
        SD5.close ( );

        System.out.println ( "DATA to day: " + dateToday );
        System.out.println ( "DATA tarrif date: " + TarifToDate );

        String agreecultural_Consumer = agricultChk ( );

        if (agreecultural_Consumer.equalsIgnoreCase ( "Agricultural Consumer" )) {
            BtnContinue.setEnabled ( false );
            new SweetAlertDialog ( Readinginput.getRActivity ( ), SweetAlertDialog.WARNING_TYPE )
                    .setTitleText ( "Billing ERROR" )
                    .setContentText ( "Agricultural Consumer Found!" )
                    .setConfirmText ( "OK!" )
                    .show ( );

        } else {


            /*------------FOR MP------------------*/
//
////            switch (dateToday.compareTo(TarifToDate)) {
////                case -1:
////                    System.out.println("today is sooner than questionDate");
////
////                    try {
////
////                        String newTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim() + "' AND TARIFF_TO_DATE='" + tariffEffetiveDate + "'";
////                        if (Structconsmas.SYSTEM_FLAG != null && !Structconsmas.SYSTEM_FLAG.isEmpty()) { // null check empty chk
////                            if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
////                                newTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + getRMScodefromSybase(Structconsmas.Tariff_Code.trim()) + "' AND TARIFF_TO_DATE='" + tariffEffetiveDate + "'";
////                            }
////
////                        }
////
////                        System.out.println("query---" + newTarrifQwery);
////                        Cursor curnewTarrif = SD.rawQuery(newTarrifQwery, null);
////
////                        if (curnewTarrif.moveToFirst()) {
////
////                            do {
////
////                                uac = new UtilAppCommon();
////                                try {
////                                    uac.copyResultsetToTarrifClass(curnewTarrif);
////                                } catch (SQLException e) {
////                                    e.printStackTrace();
////                                }
////                            } while (curnewTarrif.moveToNext());//Move the cursor to the next row.
////                        } else {
////                            Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
////                            NoTariffFlag = true;
////                        }
////                    } catch (Exception e) {
////                        Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
////                    }
////
////                    break;
////                case 0:
////                    System.out.println("today and questionDate are equal");
////
////                    try {
////
////                        dbHelper2 = new DB(getApplicationContext());
////                        SD3 = dbHelper2.getWritableDatabase();
////                        String newTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim() + "' AND TARIFF_TO_DATE='" + tariffEffetiveDate + "'";
////                        if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
////                            newTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + getRMScodefromSybase(Structconsmas.Tariff_Code.trim()) + "' AND TARIFF_TO_DATE='" + tariffEffetiveDate + "'";
////                        }
////                        System.out.println("query---" + newTarrifQwery);
////                        Cursor curnewTarrif = SD3.rawQuery(newTarrifQwery, null);
////
////                        if (curnewTarrif.moveToFirst()) {
////
////                            do {
////
////                                uac = new UtilAppCommon();
////                                try {
////                                    uac.copyResultsetToTarrifClass(curnewTarrif);
////                                } catch (SQLException e) {
////                                    e.printStackTrace();
////                                }
////                            } while (curnewTarrif.moveToNext());//Move the cursor to the next row.
////                        } else {
////                            Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
////                            NoTariffFlag = true;
////                        }
////                    } catch (Exception e) {
////                        Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
////                    }
////                    try {
////
////                        String oldTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim() + "' AND TARIFF_TO_DATE!='" + tariffEffetiveDate + "'";
////                        System.out.println("query---" + oldTarrifQwery);
////                        Cursor curOLDTarrif = SD.rawQuery(oldTarrifQwery, null);
////
////                        if (curOLDTarrif.moveToFirst()) {
////
////                            do {
////
////                                System.out.println("DATA 1 : " + curOLDTarrif.getString(0));
////
////                                uac = new UtilAppCommon();
////                                try {
////                                    uac.copyResultsetToOLDTarrifClass(curOLDTarrif);
////                                } catch (SQLException e) {
////                                    e.printStackTrace();
////                                }
////                            } while (curOLDTarrif.moveToNext());//Move the cursor to the next row.
////                        } else {
////                            Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
////                        }
////                    } catch (Exception e) {
////                        Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
////                    }
////                    break;
////                case 1:
////                    System.out.println("today is later than questionDate");
////
////                    try {
////
////                        String newTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim() + "' AND TARIFF_TO_DATE='" + tariffEffetiveDate + "'";
////                        System.out.println("query---" + newTarrifQwery);
////                        Cursor curnewTarrif = SD.rawQuery(newTarrifQwery, null);
////
////                        if (curnewTarrif.moveToFirst()) {
////
////                            do {
////                                uac = new UtilAppCommon();
////                                try {
////                                    uac.copyResultsetToTarrifClass(curnewTarrif);
////                                } catch (SQLException e) {
////                                    e.printStackTrace();
////                                }
////                            } while (curnewTarrif.moveToNext());//Move the cursor to the next row.
////                        } else {
////                            Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
////                            NoTariffFlag = true;
////                        }
////                    } catch (Exception e) {
////                        Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
////                    }
////                    try {
////
////                        String oldTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim() + "' AND TARIFF_TO_DATE!='" + tariffEffetiveDate + "'";
////                        System.out.println("query---" + oldTarrifQwery);
////                        Cursor curOLDTarrif = SD.rawQuery(oldTarrifQwery, null);
////
////                        if (curOLDTarrif.moveToFirst()) {
////
////                            do {
////
////                                System.out.println("DATA 1 : " + curOLDTarrif.getString(0));
////
////                                uac = new UtilAppCommon();
////                                try {
////                                    uac.copyResultsetToOLDTarrifClass(curOLDTarrif);
////                                } catch (SQLException e) {
////                                    e.printStackTrace();
////                                }
////                            } while (curOLDTarrif.moveToNext());//Move the cursor to the next row.
////                        } else {
////                            Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
////                            NoTariffFlag = true;
////                        }
////                    } catch (Exception e) {
////                        Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
////                    }
////
////                    break;
////                default:
////                    System.out.println("Invalid results from date comparison");
////                    break;
////            }
//            ////////////////////////////////////////////////////////////////////
//            prevMtrDate = uac.dateConvert("MMM", Structconsmas.Prev_Meter_Reading_Date);
//
//            //TarifToDate =01-05-2018 && dateToday = 28-04-2018
//
//            if (prevMtrDate.before(TarifToDate) && dateToday.after(TarifToDate)) {
//                System.out.println("today is later than questionDate");
//
//                try {
//
//                    dbHelper2 = new DB(getApplicationContext());
//                    SD3 = dbHelper2.getWritableDatabase();
//                    String newTarrifQwery = "SELECT * FROM TBL_TARRIF_PHED where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim() + "' AND EFFECTIVE_DATE='" + tariffEffetiveDate + "'";
//                    if (Structconsmas.SYSTEM_FLAG != null && !Structconsmas.SYSTEM_FLAG.isEmpty()) { // null check empty chk
//                        if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
//                            newTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" +
//                                    Structconsmas.Tariff_Code.trim() + "' AND " +
//                                    "EFFECTIVE_DATE ='" + tariffEffetiveDate + "'";
//                        }
//
//                    }
//                    System.out.println("query---" + newTarrifQwery);
//                    Cursor curnewTarrif = SD3.rawQuery(newTarrifQwery, null);
//
//                    if (curnewTarrif.moveToFirst()) {
//
//                        do {
//
//                            System.out.println("DATA 1 : " + curnewTarrif.getString(0));
//
//                            uac = new UtilAppCommon();
//                            try {
//                                uac.copyResultsetToTarrifClass(curnewTarrif);
//
//                            } catch (SQLException e) {
//                                e.printStackTrace();
//                            }
//                        } while (curnewTarrif.moveToNext());//Move the cursor to the next row.
//                    } else {
//                        Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
//                    }
//                } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//                try {
//                    dbHelper2 = new DB(getApplicationContext());
//                    SD3 = dbHelper2.getWritableDatabase();
//                    String oldTarrifQwery = "SELECT * FROM TBL_TARRIF_PHED where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim() + "' AND EFFECTIVE_DATE!='" + tariffEffetiveDate + "'";
//                    if (Structconsmas.SYSTEM_FLAG != null && !Structconsmas.SYSTEM_FLAG.isEmpty()) { // null check empty chk
//                        if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
//                            // oldTarrifQwery= "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" +
//                            //       getRMScodefromSybase(Structconsmas.Tariff_Code.trim(),dbHelper2,SD3) + "' AND " +
//                            //     "EFFECTIVE_DATE !='" + tariffEffetiveDate + "'";
//
//                            oldTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" +
//                                    Structconsmas.Tariff_Code.trim() + "' AND " +
//                                    "EFFECTIVE_DATE !='" + tariffEffetiveDate + "'";
//                        }
//
//                    }
//                    System.out.println("query---" + oldTarrifQwery);
//                    Cursor curOLDTarrif = SD3.rawQuery(oldTarrifQwery, null);
//
//                    if (curOLDTarrif.moveToFirst()) {
//
//                        do {
//
//                            System.out.println("DATA 1 : " + curOLDTarrif.getString(0));
//
//                            uac = new UtilAppCommon();
//                            try {
//                                uac.copyResultsetToOLDTarrifClass(curOLDTarrif);
//                            } catch (SQLException e) {
//                                e.printStackTrace();
//                            }
//                        } while (curOLDTarrif.moveToNext());//Move the cursor to the next row.
//                    } else {
//                        Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
//                    }
//                } catch (Exception e) {
////                Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//
//            } else if (prevMtrDate.before(TarifToDate) && dateToday.before(TarifToDate)) {
//                System.out.println("today and questionDate are equal");
//
////            try {
////
////                dbHelper2 = new DB(getApplicationContext());
////                SD3 = dbHelper2.getWritableDatabase();
////                String newTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim() + "' AND EFFECTIVE_DATE='" + tariffEffetiveDate + "'";
////                if(Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S"))
////                {
////                    newTarrifQwery= "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + getRMScodefromSybase(Structconsmas.Tariff_Code.trim()) + "' AND EFFECTIVE_DATE='" + tariffEffetiveDate + "'";
////                }
////                System.out.println("query---" + newTarrifQwery);
////                Cursor curnewTarrif = SD3.rawQuery(newTarrifQwery, null);
////
////                if (curnewTarrif.moveToFirst()) {
////
////                    do {
////
////                        System.out.println("TARIFF DATA 5 : " + curnewTarrif.getString(0));
////                        System.out.println("TARIFF DATA 6 : " + curnewTarrif.getString(1));
////                        System.out.println("TARIFF DATA 7 : " + curnewTarrif.getString(2));
////                        System.out.println("TARIFF DATA 8 : " + curnewTarrif.getString(3));
////
////                        uac = new UtilAppCommon();
////                        try {
////                            uac.copyResultsetToTarrifClass(curnewTarrif);
////                        } catch (SQLException e) {
////                            e.printStackTrace();
////                        }
////                    } while (curnewTarrif.moveToNext());//Move the cursor to the next row.
////                } else {
////                    Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
////                }
////            } catch (Exception e) {
////                Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
////            }
//
//                dbHelper2 = new DB(getApplicationContext());
//                SD3 = dbHelper2.getWritableDatabase();
//                try {
//
//                    String oldTarrifQwery = "SELECT * FROM TBL_TARRIF_PHED where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim() + "' AND EFFECTIVE_DATE!='" + tariffEffetiveDate + "'";
//                    if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
//                        oldTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" +
//                                Structconsmas.Tariff_Code.trim() + "' AND EFFECTIVE_DATE!='" + tariffEffetiveDate + "'";
//                    }
//                    System.out.println("query---" + oldTarrifQwery);
//                    Cursor curOLDTarrif = SD3.rawQuery(oldTarrifQwery, null);
//
//                    if (curOLDTarrif.moveToFirst()) {
//
//                        do {
//
//                            System.out.println("DATA 1 : " + curOLDTarrif.getString(0));
//
//                            uac = new UtilAppCommon();
//                            try {
//                                uac.copyResultsetToOLDTarrifClass(curOLDTarrif);
//                                uac.nullyfimodelNewTarif();
//                            } catch (SQLException e) {
//                                e.printStackTrace();
//                            }
//                        } while (curOLDTarrif.moveToNext());//Move the cursor to the next row.
//                    } else {
//                        Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
//                    }
//                } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//            } else if (prevMtrDate.after(TarifToDate) && dateToday.after(TarifToDate)) {
//                System.out.println("today is sooner than questionDate");
//
//                dbHelper2 = new DB(getApplicationContext());
//                SD3 = dbHelper2.getWritableDatabase();
//                try {
//
//                    String newTarrifQwery = "SELECT * FROM TBL_TARRIF_PHED where TARIFF_CODE='" +
//                            Structconsmas.Tariff_Code.trim() + "' AND EFFECTIVE_DATE='" + tariffEffetiveDate + "'";
//                    if (Structconsmas.SYSTEM_FLAG != null && !Structconsmas.SYSTEM_FLAG.isEmpty()) { // null check empty chk
//                        if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
//                            newTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim() + "' AND EFFECTIVE_DATE='" + tariffEffetiveDate + "'";
//                        }
//
//                    }
//
//                    System.out.println("query---" + newTarrifQwery);
//                    dbHelper2 = new DB(getApplicationContext());
//                    SD3 = dbHelper2.getWritableDatabase();
//
//                    Cursor curnewTarrif = SD3.rawQuery(newTarrifQwery, null);
//
//                    if (curnewTarrif.moveToFirst()) {
//
//                        do {
//
//                            System.out.println("TARIFF DATA 1 : " + curnewTarrif.getString(0));
//                            System.out.println("TARIFF DATA 2 : " + curnewTarrif.getString(1));
//                            System.out.println("TARIFF DATA 3 : " + curnewTarrif.getString(2));
//                            System.out.println("TARIFF DATA 4 : " + curnewTarrif.getString(3));
//
//                            uac = new UtilAppCommon();
//                            try {
//                                uac.copyResultsetToTarrifClass(curnewTarrif);
//                                uac.nullyfimodelOldTarif();
//                            } catch (SQLException e) {
//                                e.printStackTrace();
//                            }
//                        } while (curnewTarrif.moveToNext());
//                        //Move the cursor to the next row.
//                    } else {
//                        Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//
//            } else if ((prevMtrDate.before(TarifToDate) && dateToday.equals(TarifToDate))
//                    ||
//                    (prevMtrDate.after(TarifToDate) && dateToday.equals(TarifToDate))) {
//                System.out.println("today is later than questionDate");
//
//                try {
//
//                    dbHelper2 = new DB(getApplicationContext());
//                    SD3 = dbHelper2.getWritableDatabase();
//                    String newTarrifQwery = "SELECT * FROM TBL_TARRIF_PHED where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim() + "' AND EFFECTIVE_DATE='" + tariffEffetiveDate + "'";
//                    if (Structconsmas.SYSTEM_FLAG != null && !Structconsmas.SYSTEM_FLAG.isEmpty()) { // null check empty chk
//                        if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
//                            newTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim() + "' AND EFFECTIVE_DATE='" + tariffEffetiveDate + "'";
//                        }
//
//                    }
//                    System.out.println("query---" + newTarrifQwery);
//                    Cursor curnewTarrif = SD3.rawQuery(newTarrifQwery, null);
//
//                    if (curnewTarrif.moveToFirst()) {
//
//                        do {
//
//                            System.out.println("DATA 1 : " + curnewTarrif.getString(0));
//
//                            uac = new UtilAppCommon();
//                            try {
//                                uac.copyResultsetToTarrifClass(curnewTarrif);
//
//                            } catch (SQLException e) {
//                                e.printStackTrace();
//                            }
//                        } while (curnewTarrif.moveToNext());//Move the cursor to the next row.
//                    } else {
//                        Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
//                    }
//                } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//                try {
//                    dbHelper2 = new DB(getApplicationContext());
//                    SD3 = dbHelper2.getWritableDatabase();
//                    String oldTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim() + "' AND EFFECTIVE_DATE!='" + tariffEffetiveDate + "'";
//                    if (Structconsmas.SYSTEM_FLAG != null && !Structconsmas.SYSTEM_FLAG.isEmpty()) { // null check empty chk
//                        if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
//                            oldTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" +
//                                    Structconsmas.Tariff_Code.trim() + "' AND " +
//                                    "EFFECTIVE_DATE !='" + tariffEffetiveDate + "'";
//                        }
//
//                    }
//                    System.out.println("query---" + oldTarrifQwery);
//                    Cursor curOLDTarrif = SD3.rawQuery(oldTarrifQwery, null);
//
//                    if (curOLDTarrif.moveToFirst()) {
//
//                        do {
//
//                            System.out.println("DATA 1 : " + curOLDTarrif.getString(0));
//
//                            uac = new UtilAppCommon();
//                            try {
//                                uac.copyResultsetToOLDTarrifClass(curOLDTarrif);
//                            } catch (SQLException e) {
//                                e.printStackTrace();
//                            }
//                        } while (curOLDTarrif.moveToNext());//Move the cursor to the next row.
//                    } else {
//                        Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
//                    }
//                } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), "No data found" + e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//
//            }
//
//
//            System.out.println("Previous MET READ DATE :" + Structconsmas.Prev_Meter_Reading_Date);
//            System.out.println("Tariff to DATE :" + Structtariff.TARIFF_TO_DATE);
//
//            Date varDate = null;
//            Date varDate2 = null;
//            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//            String DTime = sdf.format(new Date());
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
//            try {
//
//                if (Structconsmas.Prev_Meter_Reading_Date.isEmpty() || Structconsmas.Prev_Meter_Reading_Date.equalsIgnoreCase("0")) {
//
//                    varDate = dateFormat.parse(Structconsmas.BILL_ISSUE_DATE);
//                    varDate2 = dateFormat.parse(Structconsmas.BILL_ISSUE_DATE);
//
//                    String billISDate = Structconsmas.BILL_ISSUE_DATE.substring(0, 4);
//                    String billISDate2 = Structconsmas.BILL_ISSUE_DATE.substring(4, 6);
//                    String billISDate3 = Structconsmas.BILL_ISSUE_DATE.substring(6, 8);
//
//                    System.out.println("BILLISSUE DATE 1 :" + billISDate);
//                    System.out.println("BILLISSUEDATE  2 :" + billISDate2);
//                    System.out.println("BILLISSUEDATE  3 :" + billISDate3);
//
//
//                    dateFormat = new SimpleDateFormat("dd-MM-yyyy");//22-04-0017
//                    System.out.println("Date PMRD: " + dateFormat.format(varDate));
//                    System.out.println("Date PMRD: " + dateFormat.format(varDate2));
//
//                    String date1 = dateFormat.format(varDate).substring(0, 6);
//                    String date2 = dateFormat.format(varDate).substring(8, 10);
//
//                    String date3 = dateFormat.format(varDate2).substring(0, 6);
//                    String date4 = dateFormat.format(varDate2).substring(8, 10);
//
//                    String finadate = date1 + "20" + date2;
//                    try {
//                        SimpleDateFormat format = new SimpleDateFormat("dd-mm-yyyy");
//
////                   Date varDate2 = format.parse(Structconsmas.BILL_ISSUE_DATE);
//                        String date = Structconsmas.BILL_ISSUE_DATE;
//
//
//                        Date d = format.parse(finadate);
//                        Date dateBefore = new Date(d.getTime() - 5 * 24 * 3600 * 1000l);
//                        System.out.print("************** " + format.format(dateBefore)); // print 15-10-2015
//                    } catch (ParseException pe) {
//                        pe.printStackTrace();
//                    }
//                    String finadate2 = date3 + "20" + date4;
//                    System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDA :" + finadate);
//
//                    if (Structtariff.OLD_EFFECTIVE_DATE != null) {
//                        Structbilling.OLD_dateDuration = printDifference(finadate, Structtariff.OLD_TARIFF_TO_DATE);//24-03-2017 ,01-04-2099 ---diff is 29958 days
//                        if (Structtariff.EFFECTIVE_DATE.equalsIgnoreCase("0")) {
//                            Structbilling.NEW_dateDuration = 0l;
//                        } else {
//
//                            if(prevMtrDate.before(TarifToDate) || prevMtrDate.equals(TarifToDate)){
//                                Structbilling.NEW_dateDuration = printDifference(finadate,
//                                        finadate2);
//                            }else{
//                                Structbilling.NEW_dateDuration = printDifference(Structtariff
//                                                .EFFECTIVE_DATE,
//                                        finadate2);
//                            }
//
//                            if (Structbilling.OLD_dateDuration > 0l) {
//                                Structbilling.NEW_dateDuration += 1l;
//                            }
//
//                        }
//                        // 01-04-2017 31 days
//                        if (Structbilling.NEW_dateDuration < 0l) {
//                            Structbilling.NEW_dateDuration = 0l;
//                            Structbilling.OLD_dateDuration = 0l;
//                        } else if (Structbilling.OLD_dateDuration < 0l) {
//                            Structbilling.OLD_dateDuration = 0l;
//                        }
//
//
//                    } else {
//
//                        Structbilling.OLD_dateDuration = 0l;
//                        Structbilling.NEW_dateDuration = printDifference(finadate, finadate2);//01-03-2017, 01-04-2017 31 days
//                        if (Structbilling.NEW_dateDuration < 0l) {
//                            Structbilling.NEW_dateDuration = 0l;
//
//                        } else if (Structbilling.OLD_dateDuration < 0l) {
//                            Structbilling.OLD_dateDuration = 0l;
//                        }
//
//                    }
//
//                    Structbilling.dateDuration = Structbilling.OLD_dateDuration + Structbilling.NEW_dateDuration;
//
//                } else {
//                    varDate = dateFormat.parse(Structconsmas.Prev_Meter_Reading_Date);
//                    varDate2 = dateFormat.parse(Structconsmas.BILL_ISSUE_DATE);
//
//                    String billISDate = Structconsmas.BILL_ISSUE_DATE.substring(0, 4);
//                    String billISDate2 = Structconsmas.BILL_ISSUE_DATE.substring(4, 6);
//                    String billISDate3 = Structconsmas.BILL_ISSUE_DATE.substring(6, 8);
//
//                    dateFormat = new SimpleDateFormat("dd-MM-yyyy");//22-04-0017
//
//                    String date1 = dateFormat.format(varDate).substring(0, 6);
//                    String date2 = dateFormat.format(varDate).substring(8, 10);
//
//                    String date3 = dateFormat.format(varDate2).substring(0, 6);
//                    String date4 = dateFormat.format(varDate2).substring(8, 10);
//
//                    String finadate = date1 + "20" + date2;
//                    String finadate2 = DTime;
//
//
//                    if (Structtariff.OLD_EFFECTIVE_DATE != null) {
//                        Structbilling.OLD_dateDuration = printDifference(finadate, Structtariff.OLD_TARIFF_TO_DATE);//24-03-2017 ,01-04-2099 ---diff is 29958 days
//                        if (Structtariff.EFFECTIVE_DATE.equalsIgnoreCase("0")) {
//                            Structbilling.NEW_dateDuration = 0l;
//                        } else {
//                            if(prevMtrDate.before(TarifToDate) || prevMtrDate.equals(TarifToDate)){
//                                Structbilling.NEW_dateDuration = printDifference(finadate,
//                                        finadate2);
//                            }else{
//                                Structbilling.NEW_dateDuration = printDifference(Structtariff
//                                                .EFFECTIVE_DATE,
//                                        finadate2);
//                            }
//
//                            if (Structbilling.OLD_dateDuration > 0l) {
//                                Structbilling.NEW_dateDuration += 1l;
//                            }
//                        }
//
//                        if (Structbilling.NEW_dateDuration < 0l) {
//                            Structbilling.NEW_dateDuration = 0l;
//
//                        } else if (Structbilling.OLD_dateDuration < 0l) {
//                            Structbilling.OLD_dateDuration = 0l;
//                        }
//
//                    } else {
//                        Structbilling.OLD_dateDuration = 0l;
//                        Structbilling.NEW_dateDuration = printDifference(finadate, Structconsmas.CUR_READ_DATE);//01-03-2017, 01-04-2017 31 days
//                        if (Structbilling.NEW_dateDuration < 0l) {
//                            Structbilling.NEW_dateDuration = 0l;
//                        } else if (Structbilling.OLD_dateDuration < 0l) {
//                            Structbilling.OLD_dateDuration = 0l;
//                        }
//
//                    }
//
//                }
//                Structbilling.dateDuration = Structbilling.OLD_dateDuration + Structbilling.NEW_dateDuration;
//
//            } catch (Exception e) {
//                // TODO: handle exception
//                e.printStackTrace();
//            }
//
//            String state = Environment.getExternalStorageState();
//            if (Environment.MEDIA_MOUNTED.equals(state)) {
//                mFileTemp = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
//            } else {
//                mFileTemp = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
//            }


            ////////////////////////////////////////////////////


            dbHelper2 = new DB ( getApplicationContext ( ) );
            SD3 = dbHelper2.getWritableDatabase ( );
            try {
                Date varDate = null;
                Date varDate2 = null;

                String newTarrifQwery = "SELECT * FROM TBL_TARRIF_PHED where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim ( ) + "'";
                System.out.println ( "query---" + newTarrifQwery );
                Cursor curnewTarrif = SD3.rawQuery ( newTarrifQwery, null );

                int l = curnewTarrif.getCount ( );
                if (curnewTarrif.moveToFirst ( )) {

                    do {

                        uac = new UtilAppCommon ( );
                        try {
                            UtilAppCommon.copyResultsetToTarrifClass ( curnewTarrif );
                        } catch (SQLException e) {
                            e.printStackTrace ( );
                        }
                    } while (curnewTarrif.moveToNext ( ));//Move the cursor to the next row.
                } else {
                    Toast.makeText ( getApplicationContext ( ), "No data found", Toast.LENGTH_LONG ).show ( );
//                            NoTariffFlag = true;
                }


                prevMtrDate = uac.dateConvert ( "MMM", Structconsmas.Prev_Meter_Reading_Date );

                SimpleDateFormat sdf = new SimpleDateFormat ( "dd-MM-yyyy" );
                String DTime = sdf.format ( new Date ( ) );

                SimpleDateFormat dateFormat = new SimpleDateFormat ( "dd-MMM-yyyy" );

                String billISDate = Structconsmas.Prev_Meter_Reading_Date.substring ( 0, 7 );
                String billISDate2 = Structconsmas.Prev_Meter_Reading_Date.substring ( 7, 9 );

                String prevMTRDate = billISDate + "20" + billISDate2;
                Date PRVMTR = dateFormat.parse ( prevMTRDate );

                dateFormat = new SimpleDateFormat ( "dd-MM-yyyy" );//22-04-0017
                String PRV_DATE = sdf.format ( PRVMTR );
                String finadate2 = DTime;

//                String date1 = dateFormat.format(varDate).substring(0, 6);//12-06-
//                String date2 = dateFormat.format(varDate).substring(8, 10);//18
//
////                String date3 = dateFormat.format(varDate2).substring(0, 6);
////                String date4 = dateFormat.format(varDate2).substring(8, 10);
//
//                String finadate = date1 + "20" + date2;
////                String finadate2 = DTime;
//
////                Date varDate = dateFormat.parse(Structconsmas.Prev_Meter_Reading_Date);
////                String finadate = String.valueOf(varDate);
                Structbilling.dateDuration = printDifference ( PRV_DATE, DTime );
            } catch (ParseException e) {
                e.printStackTrace ( );
            }


//                Structbilling.dateDuration = Structbilling.OLD_dateDuration + Structbilling.NEW_dateDuration;
//                Structbilling.dateDuration = printDifference(finadate,DTime);


        }

        final UtilAppCommon finalUac = uac;
//        if(finalUac.convertLoadToWatts()> 10000d){
//
//
//            new SweetAlertDialog(CameraActivity.this, SweetAlertDialog.WARNING_TYPE)
//                    .setTitleText("Load is above 10KW")
//                    .setContentText("You are prohibited to bill the consumer")
//                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                        @Override
//                        public void onClick(SweetAlertDialog sDialog) {
//
//                            sDialog.dismissWithAnimation();
//                            finalUac.nullyfimodelBill();
//
//                            Intent intent = new Intent(getApplicationContext(), Billing.class);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                    R.anim.anim_slide_out_left);
//
////                            dialogAccount.dismiss();
//                        }
//                    })
//                    .show();

//            /****DIALOG BOX INITIALIZATION PROVIDING A LAYOUT TO DIALOUGE****/
//            final Dialog dialogAccount = new Dialog(Signature_Activity.this, R.style.DialogeAppTheme);
//
//            dialogAccount.setContentView(R.layout.custom_dialoge_warning);
//            dialogAccount.setTitle("Account Search");
//
//            // set the custom dialog components - text, image and button
//
//            Button dialogButton = (Button) dialogAccount.findViewById(R.id.dialogButtonACCOK);
//            GSBilling.getInstance().setConsumptionchkhigh("  ");
//            // if button is clicked, close the custom dialog
//            /****DIALOUGE BOX BUTTON INTIALISATION AND EVENT ADDING****/
//            dialogButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    GSBilling.getInstance().setConsumptionchkhigh("**");
//                    appCommon = new UtilAppCommon();
//                    session = new SessionManager(getApplicationContext());
//                    key = appCommon.UniqueCode(getApplicationContext());
//
//                    Structbilling.Cumul_Pro_Elec_Duty=10000f;
//                    Structbilling.SBM_No = key;
//                    Structbilling.Meter_Reader_Name = session.retMRName();
//                    Structbilling.Meter_Reader_ID = session.retMRID();
//
//                    //-------------not used
//                    Structconsmas.Meter_Type = "00";
//                    Structconsmas.Meter_Ownership = "A";
//                    //----------
//                    billseq = appCommon.findSequence(getApplicationContext(), "BillNumber");
//                    dbHelper = new DB(getApplicationContext());
//                    SD = dbHelper.getWritableDatabase();
//                    //  dbHelper4.insertIntoBillingTable();
//                    dbHelper.insertIntoMPBillingTable();
//                    dbHelper.insertSequence("BillNumber", billseq);
//
//                    Toast.makeText(Signature_Activity.this, "Data Stored", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(getApplicationContext(), Billing.class);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.anim_slide_in_left,
//                            R.anim.anim_slide_out_left);
//
//                            dialogAccount.dismiss();
//
//
//                }
//            });
//            dialogAccount.show();

//        }

//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
//        try {
//            String date1 = Structconsmas.FIRST_CASH_DUE_DATE.substring(0, 7);
//            String date2 = Structconsmas.FIRST_CASH_DUE_DATE.substring(7, 9);
//            String finadate = date1 + "20" + date2;
//            cashDate = dateFormat.parse(finadate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

//        if (dateCompare(dateToday, cashDate).equalsIgnoreCase("later")) {
//
//
//            new SweetAlertDialog(CameraActivity.this, SweetAlertDialog.WARNING_TYPE)
//                    .setTitleText("Due Date is Over")
//                    .setContentText("You are prohibited to bill the consumer")
//                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                        @Override
//                        public void onClick(SweetAlertDialog sDialog) {
//
//                            sDialog.dismissWithAnimation();
//                            finalUac.nullyfimodelBill();
//
//                            Intent intent = new Intent(getApplicationContext(), Billing.class);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                    R.anim.anim_slide_out_left);
//
////                            dialogAccount.dismiss();
//                        }
//                    })
//                    .show();
//        }

        BtnContinue.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {


                String duplicateaccQuery = "select * from TBL_BILLING WHERE Bill_Month ='" + Structconsmas.Bill_Mon + "' and Cons_Number='" + (Structconsmas.Consumer_Number) + "'";

                final Cursor curdupacc = SD.rawQuery ( duplicateaccQuery, null );
                Logger.e ( getApplicationContext ( ), "CamAct", "Duplicate Query " + duplicateaccQuery );
                if (curdupacc != null && curdupacc.moveToFirst ( )) {


                    if (Home.isMeter) {
                        sDialog = new SweetAlertDialog ( context, SweetAlertDialog.WARNING_TYPE );
                        sDialog.setTitleText ( "Meter Reading ?" );
                        sDialog.setContentText ( "Meter Reading Already Completed" );
                        sDialog.setConfirmText ( "OK" ).setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation ( );

                                Intent intent = new Intent ( CameraActivity.this, Billing.class );
                                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                startActivity ( intent );
                                overridePendingTransition ( R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left );


                            }
                        } );
                        sDialog.show ( );
                    } else {

                        sDialog = new SweetAlertDialog ( context, SweetAlertDialog.WARNING_TYPE );
                        sDialog.setTitleText ( "Print Duplicate BIll ?" );
                        sDialog.setContentText ( "Cannot add Current Meter Reading!" );
                        sDialog.setCancelText ( "No,cancel plz!" );
                        sDialog.setConfirmText ( "Yes,print!" );
                        sDialog.showCancelButton ( true );
                        sDialog.setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                try {

                                    UtilAppCommon.duplicateBill ( curdupacc );
                                    duplicatePrintTariff ( dbHelper, SD );

                                } catch (SQLException e) {
                                    e.printStackTrace ( );
                                }

                                if (Structbilling.unit125Logic != null && Structbilling.unit125Logic.equalsIgnoreCase ( "1.25" ) || Structbilling.unit125Logic.equalsIgnoreCase ( "10000" )) {
                                    Toast.makeText ( context, "Can not print duplicate bill", Toast.LENGTH_SHORT ).show ( );
                                    sDialog.cancel ( );
                                } else {
                                    String strPref = "SELECT PRINTER_PREF FROM USER_MASTER";

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
                                    String dcCode = "SELECT SEC_NAME FROM TBL_BILLING_DC_MASTER WHERE RMS_DC_CODE='" + Structconsmas.LOC_CD + "' OR CCNB_DC_CODE='" + Structconsmas.LOC_CD + "'";
                                    android.util.Log.e ( "Sequence", "update " + strPref );

                                    Cursor curDcCode = SD.rawQuery ( dcCode, null );

                                    if (curDcCode != null && curDcCode.moveToFirst ( )) {

                                        Structconsmas.DC_NAME = curDcCode.getString ( 0 );

                                    }

                                    String divCode = "SELECT  DIV_NAME,DISPLAY_CODE  FROM  TBL_BILLING_DIV_MASTER";// WHERE RMS_DC_CODE='"+Structconsmas.LOC_CD+"' OR CCNB_DC_CODE='"+Structconsmas.LOC_CD+"'";
                                    android.util.Log.e ( "Sequence", "update " + strPref );

                                    Cursor curDivCode = SD.rawQuery ( divCode, null );

                                    if (curDivCode != null && curDivCode.moveToFirst ( )) {

                                        Structconsmas.DIV_NAME = curDivCode.getString ( 0 );
                                        Structconsmas.PICK_REGION = curDivCode.getString ( 1 );

                                    }
                                    if (!unitPercentageChk ( ).equalsIgnoreCase ( "0" )) {
                                        GSBilling.getInstance ( ).setConsumptionchkhigh ( "**" );
                                    } else {
                                        GSBilling.getInstance ( ).setConsumptionchkhigh ( "  " );
                                    }

                                    if (prev_pref.equalsIgnoreCase ( "0_0_0" ) || prev_pref.equalsIgnoreCase ( "0_0_1" )) {//IMP_AMI

                                        Intent intent = new Intent ( CameraActivity.this, DuplicateAmigoPrinting.class );
                                        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                        startActivity ( intent );
                                        overridePendingTransition ( R.anim.anim_slide_in_left,
                                                R.anim.anim_slide_out_left );

                                    } else if (prev_pref.equalsIgnoreCase ( "0_1_0" ) || prev_pref.equalsIgnoreCase ( "0_1_1" )) {//IMP_REG
                                        Toast.makeText ( CameraActivity.this, "Under process", Toast.LENGTH_SHORT ).show ( );
                                    } else if (prev_pref.equalsIgnoreCase ( "0_2_0" ) || prev_pref.equalsIgnoreCase ( "0_2_1" )) {
                                        Intent intent = new Intent ( context, DuplicateBillPrint.class );
                                        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                        startActivity ( intent );
                                        overridePendingTransition ( R.anim.anim_slide_in_left,
                                                R.anim.anim_slide_out_left );
                                    } else {
                                        Toast.makeText ( CameraActivity.this, "Unable to find Printer", Toast.LENGTH_SHORT ).show ( );
                                    }
                                }
                            }
                        } )
                                .setCancelClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.cancel ( );
                                    }
                                } );
                        sDialog.show ( );
                    }
                } else {


                    try {

//                        if (Structbilling.dateDuration <= 15l) {

//                            new SweetAlertDialog(CameraActivity.this, SweetAlertDialog.WARNING_TYPE)
//                                    .setTitleText("Billing ERROR")
//                                    .setContentText("Minimum billing date not matched")
//                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                        @Override
//                                        public void onClick(SweetAlertDialog sDialog) {
//                                            sDialog.dismissWithAnimation();
//
//                                            UtilAppCommon ucom = new UtilAppCommon();
//                                            ucom.nullyfimodelCon();
//                                            ucom.nullyfimodelBill();
//
//                                            Intent intent = new Intent(context, Home.class);
//                                            startActivity(intent);
//                                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                                    R.anim.anim_slide_out_left);
//
//                                        }
//                                    })
//                                    .show();
//                        } else {

                        Date varDate = null;
                        SimpleDateFormat sdf = new SimpleDateFormat ( "dd-MM-yyyy" );
                        String DTime = sdf.format ( new Date ( ) );
                        SimpleDateFormat dateFormat = new SimpleDateFormat ( "dd-MMM-yyyy" );
                        varDate = dateFormat.parse ( Structconsmas.Prev_Meter_Reading_Date );

                        dateFormat = new SimpleDateFormat ( "dd-MM-yyyy" );//22-04-0017
                        System.out.println ( "Date PMRD: " + dateFormat.format ( varDate ) );

                        String date1_1 = dateFormat.format ( varDate ).substring ( 0, 6 );
                        String date2_1 = dateFormat.format ( varDate ).substring ( 8, 10 );

                        currentDateandTime = sdf.format ( new Date ( ) );
                        previous_date = date1_1 + "20" + date2_1;

                        Date date1 = sdf.parse ( currentDateandTime );
                        Date date2 = sdf.parse ( previous_date );

                        if (date2.after ( date1 )) {

                            new SweetAlertDialog ( context, SweetAlertDialog.WARNING_TYPE )
                                    .setTitleText ( "Back date Billing is Not Possible ?" )
                                    .setContentText ( "Please check your date!" )
                                    .setCancelText ( "No,cancel plz!" )
                                    .setConfirmText ( "OK!" )
                                    .showCancelButton ( true )
                                    .setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {

                                            Intent intent = new Intent ( context, Home.class );
                                            startActivity ( intent );
                                            overridePendingTransition ( R.anim.anim_slide_in_left,
                                                    R.anim.anim_slide_out_left );
                                        }
                                    } )
                                    .setCancelClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.cancel ( );
                                        }
                                    } )
                                    .show ( );

                        } else {
//                            tarrifCalculate();

                            if (NoTariffFlag) {
                                new SweetAlertDialog ( CameraActivity.this, SweetAlertDialog.WARNING_TYPE )
                                        .setTitleText ( "Consumer category not found" )
                                        .setContentText ( "Please contact administator" )
                                        .setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation ( );

                                                UtilAppCommon ucom = new UtilAppCommon ( );
                                                UtilAppCommon.nullyfimodelCon ( );
                                                UtilAppCommon.nullyfimodelBill ( );

                                                Intent intent = new Intent ( CameraActivity.this, Home.class );
                                                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                                startActivity ( intent );
                                                overridePendingTransition ( R.anim.anim_slide_in_left,
                                                        R.anim.anim_slide_out_left );


                                            }
                                        } )
                                        .show ( );
                            } else {
                                String consCHK = "select BILLED_FLAG from TBL_CONSMAST WHERE Bill_Mon ='" + Structconsmas.Bill_Mon + "' and Consumer_Number='" + (Structconsmas.Consumer_Number) + "'";

                                final Cursor curDUPchk = SD.rawQuery ( consCHK, null );
                                Logger.e ( getApplicationContext ( ), "CamAct", "Duplicate Query " + duplicateaccQuery );
                                if (curDUPchk != null && curDUPchk.moveToFirst ( )) {

                                    if (curDUPchk.getString ( 0 ).equalsIgnoreCase ( "Y" )) {

                                        new SweetAlertDialog ( CameraActivity.this, SweetAlertDialog.WARNING_TYPE )
                                                .setTitleText ( "Consumer Already Billed" )
                                                .setContentText ( "Unable To Bill Again" )
                                                .setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        sDialog.dismissWithAnimation ( );

                                                        UtilAppCommon ucom = new UtilAppCommon ( );
                                                        UtilAppCommon.nullyfimodelCon ( );
                                                        UtilAppCommon.nullyfimodelBill ( );

                                                        Intent intent = new Intent ( CameraActivity.this, Home.class );
                                                        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                                        startActivity ( intent );
                                                        overridePendingTransition ( R.anim.anim_slide_in_left,
                                                                R.anim.anim_slide_out_left );

                                                    }
                                                } )

                                                .show ( );

                                    } else {
                                        //final Dialog dialog = new Dialog ( context, android.R.style.Theme_DeviceDefault_Light );
                                       /* final Dialog dialog = new Dialog ( context );

                                        dialog.setContentView ( R.layout.custom_dialoge_mobileno );
                                        dialog.setTitle ( "Mobile Number Confirmation" );
                                        // set the custom dialog components - text, image and button
                                        final EditText editTextAccno = dialog.findViewById ( R.id.EditTextmobno );
                                        final EditText editTextEmailid = dialog.findViewById ( R.id.EditTextemail );

                                        String code = "0+ ";
                                        editTextAccno.setCompoundDrawablesWithIntrinsicBounds ( new TextDrawable ( code ), null, null, null );
                                        editTextAccno.setCompoundDrawablePadding ( code.length ( ) * 10 );


                                        Button dialogButtonContinue = dialog.findViewById ( R.id.dialogButtonContinue );
                                        Button dialogButtonSkip = dialog.findViewById ( R.id.dialogButtonSkip );
*/
                                        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                                        // if button is clicked, close the custom dialog
                                        /*DIALOUGE BOX BUTTON INTIALISATION AND EVENT ADDING*/
                                       /* dialogButtonContinue.setOnClickListener ( new View.OnClickListener ( ) {
                                            @Override
                                            public void onClick(View v) {
*/
                                        String email = et_mail.getText ( ).toString ( ).trim ( );
                                        String mobile = et_mobileNo.getText ( ).toString ( );

                                        if (TextUtils.isEmpty ( mobile )) {
                                            et_mobileNo.setError ( "Enter Mobile No." );
                                            et_mobileNo.requestFocus ();
                                        } else if (mobile.length ( ) != 10) {
                                            et_mobileNo.setError ( "Invalid Mobile No." );
                                            et_mobileNo.requestFocus ();
                                        }
                                        else if(mobile.charAt ( 0 )=='0'){

                                            et_mobileNo.setError ( "Invalid No." );
                                            et_mobileNo.requestFocus ();

                                        }
                                        else if (!TextUtils.isEmpty ( email )) {
                                            if (!email.matches ( emailPattern )) {
                                                et_mail.setError ( "Invalid Email id" );
                                                et_mail.requestFocus ();
                                            }
                                        } else {


                                            Structbilling.MOB_NO = "0" + et_mobileNo.getText ( ).toString ( ).trim ( );
                                            Structbilling.EMAIL_ID = email;
                                            getCCnBcodefromLOC_CD ( );

                                            startActivity ( new Intent ( CameraActivity.this,PictureActivity.class ) );
                                           // takePicture ( );
                                          //  dialog.dismiss ( );

                                        }


                                              /*  if (editTextAccno.getText().toString().length() != 0 && email.toString().length() == 0) {
                                                    if (editTextAccno.getText().toString().length() < 10) {
                                                        editTextAccno.setError("Please Enter a valid Mobile no");

                                                    } else {

                                                        Structbilling.MOB_NO = editTextAccno.getText().toString().trim();
                                                        getCCnBcodefromLOC_CD();
                                                        takePicture();
                                                        dialog.dismiss();

                                                    }
                                                } else if (editTextAccno.getText().toString().length() != 0 && email.toString().length() != 0) {
                                                    if (editTextAccno.getText().toString().length() < 10) {
                                                        editTextAccno.setError("Please Enter a valid Mobile no");

                                                    } else if (!email.matches(emailPattern)) {

                                                        editTextEmailid.setError("Invalid Email id");

                                                    } else {

                                                        Structbilling.MOB_NO = editTextAccno.getText().toString().trim();
                                                        Structbilling.EMAIL_ID = email;
                                                        getCCnBcodefromLOC_CD();
                                                        takePicture();
                                                        dialog.dismiss();

                                                    }
                                                } else if (editTextAccno.getText().toString().length() == 0 && email.toString().length() != 0) {
                                                    if (!email.matches(emailPattern)) {

                                                        editTextEmailid.setError("Invalid Email id");

                                                    } else {

                                                        Structbilling.MOB_NO = editTextAccno.getText().toString().trim();
                                                        Structbilling.EMAIL_ID = email;
                                                        getCCnBcodefromLOC_CD();
                                                        takePicture();
                                                        dialog.dismiss();

                                                    }
                                                } else {


                                                    Structbilling.MOB_NO = editTextAccno.getText().toString().trim();
                                                    Structbilling.EMAIL_ID = email;
                                                    getCCnBcodefromLOC_CD();
                                                    takePicture();
                                                    dialog.dismiss();

                                                }*/
//
//                                                    if (editTextAccno.getText().toString().length() == 0) {
//                                                        editTextAccno.setError("Can Not be Empty");
//                                                    } else if (editTextAccno.getText().toString().length() < 10) {
//                                                        editTextAccno.setError("Please Enter a valid Mobile no");
//                                                    } else {
//                                                        Structbilling.MOB_NO = editTextAccno.getText().toString().trim();
//                                                        getCCnBcodefromLOC_CD();
//                                                        takePicture();
//                                                        dialog.dismiss();
//                                                    }

                                  //  }
                                    //   } );


                                       /* dialogButtonSkip.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                getCCnBcodefromLOC_CD();
                                                takePicture();
                                                dialog.dismiss();
                                            }
                                        });*/
                                    //   dialog.show ( );
                                }


                                } else {
                                    final Dialog dialog = new Dialog ( context, android.R.style.Theme_DeviceDefault_Light );
                                    dialog.setContentView ( R.layout.custom_dialoge_mobileno );
                                    dialog.setTitle ( "Mobile Number Confirmation" );
                                    // set the custom dialog components - text, image and button
                                    final EditText editTextAccno = dialog.findViewById ( R.id.EditTextmobno );
                                    Button dialogButtonContinue = dialog.findViewById ( R.id.dialogButtonContinue );
                                    Button dialogButtonSkip = dialog.findViewById ( R.id.dialogButtonSkip );
                                    // if button is clicked, close the custom dialog
                                    /*DIALOUGE BOX BUTTON INTIALISATION AND EVENT ADDING*/
                                    dialogButtonContinue.setOnClickListener ( new View.OnClickListener ( ) {
                                        @Override
                                        public void onClick(View v) {
                                            if (editTextAccno.getText ( ).toString ( ).length ( ) == 0) {
                                                editTextAccno.setError ( "Cannot be Empty" );
                                            } else if (editTextAccno.getText ( ).toString ( ).length ( ) < 10) {
                                                editTextAccno.setError ( "Please Enter a valid Mobile no" );
                                            } else {
                                                Structbilling.MOB_NO = editTextAccno.getText ( ).toString ( ).trim ( );
                                                getCCnBcodefromLOC_CD ( );
                                                takePicture ( );
                                                dialog.dismiss ( );
                                            }
                                        }
                                    } );
                                    dialogButtonSkip.setOnClickListener ( new View.OnClickListener ( ) {
                                        @Override
                                        public void onClick(View v) {
                                            getCCnBcodefromLOC_CD ( );
                                            takePicture ( );
                                            dialog.dismiss ( );
                                        }
                                    } );
                                    dialog.show ( );
                                }


                            }


                        }


                    } catch (ParseException ex) {
                        ex.printStackTrace ( );
                    }

                }

//
//                //MP
//                tarrifCalculate();
//                final Dialog dialog = new Dialog(context, android.R.style.Theme_DeviceDefault_Light);
//                dialog.setContentView(R.layout.custom_dialoge_mobileno);
//                dialog.setTitle("Mobile Number Confirmation");
//                // set the custom dialog components - text, image and button
//                final EditText editTextAccno = (EditText) dialog.findViewById(R.id.EditTextmobno);
//                Button dialogButtonContinue = (Button) dialog.findViewById(R.id.dialogButtonContinue);
//                Button dialogButtonSkip = (Button) dialog.findViewById(R.id.dialogButtonSkip);
//                // if button is clicked, close the custom dialog
//                            /*DIALOUGE BOX BUTTON INTIALISATION AND EVENT ADDING*/
//                dialogButtonContinue.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (editTextAccno.getText().toString().length() == 0) {
//                            editTextAccno.setError("Can Not be Empty");
//                        } else if (editTextAccno.getText().toString().length() < 10) {
//                            editTextAccno.setError("Please Enter a valid Mobile no");
//                        } else {
//                            Structbilling.MOB_NO = editTextAccno.getText().toString().trim();
//                            takePicture();
//                            dialog.dismiss();
//                        }
//                    }
//                });
//                dialogButtonSkip.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        takePicture();
//                        dialog.dismiss();
//                    }
//                });
//                dialog.show();
            }
        } );

    }


    private String agricultChk() {

        if (Structtariff.TARIFF_CODE == "341" || Structtariff.TARIFF_CODE == "351" ||
                Structtariff.TARIFF_CODE == "352" || Structtariff.TARIFF_CODE == "353") {
            return "Agricultural Consumer";
        }

        return "0";
    }

    private String unmeteredChk() {

        if (Structtariff.TARIFF_CODE.equalsIgnoreCase ( "311" ) || Structtariff.TARIFF_CODE.equalsIgnoreCase ( "312" ) ||
                Structtariff.TARIFF_CODE.equalsIgnoreCase ( "313" ) || Structtariff.TARIFF_CODE.equalsIgnoreCase ( "314" ) ||
                Structtariff.TARIFF_CODE.equalsIgnoreCase ( "110" ) || Structtariff.TARIFF_CODE.equalsIgnoreCase ( "111" ) ||
                Structtariff.TARIFF_CODE.equalsIgnoreCase ( "113" ) || Structtariff.TARIFF_CODE.equalsIgnoreCase ( "916" )) {
            return "Unmetered Consumer";
        }

        return "0";
    }

    public String getRMScodefromSybase(String Sybasecode, DB dbHelper2, SQLiteDatabase SD2) {

        String SybaseQuery = "SELECT RMS_CODE FROM TBL_TARIFF_SYBASE where SYBASE_CODE='" + Sybasecode + "'";
        System.out.println ( "query---" + SybaseQuery );

        Cursor curSybaseTariff = SD2.rawQuery ( SybaseQuery, null );
        if (curSybaseTariff.moveToFirst ( )) {
            Structconsmas.Tariff_Code = curSybaseTariff.getString ( 0 );
            return curSybaseTariff.getString ( 0 );
        }
        return Sybasecode;
    }

    public void getCCnBcodefromLOC_CD() {
        dbHelper = new DB ( getApplicationContext ( ) );
        SD = dbHelper.getWritableDatabase ( );
//        String dcQuery = "SELECT * FROM TBL_BILLING_DC_MASTER where SEC_CODE='" + Structconsmas.LOC_CD + "'";
        String dcQuery = "SELECT * FROM TBL_BILLING_DC_MASTER WHERE  RMS_DC_CODE='" + Structconsmas.LOC_CD + "' OR  CCNB_DC_CODE='" + Structconsmas.LOC_CD + "'";
        System.out.println ( "query---" + dcQuery );

        Cursor curCCnB = SD.rawQuery ( dcQuery, null );
        if (curCCnB.moveToFirst ( )) {
            UtilAppCommon ucom = new UtilAppCommon ( );

            try {
                UtilAppCommon.copyResultToDCMaster ( curCCnB );
            } catch (SQLException e) {
                e.printStackTrace ( );
            }
        }

    }

    private void tarrifCalculate() {

        //	CBillling cb=new CBillling();
        Calendar c = Calendar.getInstance ( );

        SimpleDateFormat month_date = new SimpleDateFormat ( "MMMM-yy" );
        final String month_name = month_date.format ( c.getTime ( ) );

        comparingDate = new SimpleDateFormat ( "dd-MM-yyyy" );
        String currentDateTime = comparingDate.format ( new Date ( ) );

        try {
            dateToday = comparingDate.parse ( currentDateTime );
        } catch (ParseException e) {
            e.printStackTrace ( );
        }

        try {

            //for holding retrieve data from query and store in the form of rows
            String ret = "SELECT  DISTINCT  TARIFF_TO_DATE  FROM TBL_TARRIF_MP DESC LIMIT 1";
            System.out.println ( "query---" + ret );
            curSearchdata = SD.rawQuery ( ret, null );
            Toast.makeText ( getApplicationContext ( ), "Total data Found:" + curSearchdata.getCount ( ), Toast.LENGTH_LONG ).show ( );

            if (curSearchdata.moveToFirst ( )) {

                do {

                    System.out.println ( "DATA 1 : " + curSearchdata.getString ( 0 ) );

                    tariffEffetiveDate = curSearchdata.getString ( 0 );
                    try {
                        //data base tarrif todate
                        TarifToDate = comparingDate.parse ( curSearchdata.getString ( 0 ) );
                    } catch (ParseException e) {
                        e.printStackTrace ( );
                    }
                } while (curSearchdata.moveToNext ( ));//Move the cursor to the next row.
            } else {
                Toast.makeText ( getApplicationContext ( ), "No data found", Toast.LENGTH_LONG ).show ( );
            }
        } catch (Exception e) {
            Toast.makeText ( getApplicationContext ( ), "No data found" + e.getMessage ( ), Toast.LENGTH_LONG ).show ( );
        }


        System.out.println ( "Current Date : " + dateToday );
        System.out.println ( "Distinct Tariff To Date: " + TarifToDate );

        switch (dateToday.compareTo ( TarifToDate )) {
            case -1:
                System.out.println ( "today is sooner than questionDate" );

                try {

                    String newTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim ( ) + "' AND TARIFF_TO_DATE='" + tariffEffetiveDate + "'";
                    System.out.println ( "query---" + newTarrifQwery );
                    Cursor curnewTarrif = SD.rawQuery ( newTarrifQwery, null );

                    if (curnewTarrif.moveToFirst ( )) {

                        do {

                            System.out.println ( "TARIFF DATA 1 : " + curnewTarrif.getString ( 0 ) );
                            System.out.println ( "TARIFF DATA 2 : " + curnewTarrif.getString ( 1 ) );
                            System.out.println ( "TARIFF DATA 3 : " + curnewTarrif.getString ( 2 ) );
                            System.out.println ( "TARIFF DATA 4 : " + curnewTarrif.getString ( 3 ) );

                            uac = new UtilAppCommon ( );
                            try {
                                UtilAppCommon.copyResultsetToTarrifClass ( curnewTarrif );
                            } catch (SQLException e) {
                                e.printStackTrace ( );
                            }
                        } while (curnewTarrif.moveToNext ( ));//Move the cursor to the next row.
                    } else {
                        Toast.makeText ( getApplicationContext ( ), "No data found", Toast.LENGTH_LONG ).show ( );
                    }
                } catch (Exception e) {
                    Toast.makeText ( getApplicationContext ( ), "No data found" + e.getMessage ( ), Toast.LENGTH_LONG ).show ( );
                }

                break;
            case 0:
                System.out.println ( "today and questionDate are equal" );

                try {

                    String newTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim ( ) + "' AND TARIFF_TO_DATE='" + tariffEffetiveDate + "'";
                    System.out.println ( "query---" + newTarrifQwery );
                    Cursor curnewTarrif = SD.rawQuery ( newTarrifQwery, null );

                    if (curnewTarrif.moveToFirst ( )) {

                        do {

                            System.out.println ( "TARIFF DATA 5 : " + curnewTarrif.getString ( 0 ) );
                            System.out.println ( "TARIFF DATA 6 : " + curnewTarrif.getString ( 1 ) );
                            System.out.println ( "TARIFF DATA 7 : " + curnewTarrif.getString ( 2 ) );
                            System.out.println ( "TARIFF DATA 8 : " + curnewTarrif.getString ( 3 ) );

                            uac = new UtilAppCommon ( );
                            try {
                                UtilAppCommon.copyResultsetToTarrifClass ( curnewTarrif );
                            } catch (SQLException e) {
                                e.printStackTrace ( );
                            }
                        } while (curnewTarrif.moveToNext ( ));//Move the cursor to the next row.
                    } else {
                        Toast.makeText ( getApplicationContext ( ), "No data found", Toast.LENGTH_LONG ).show ( );
                    }
                } catch (Exception e) {
                    Toast.makeText ( getApplicationContext ( ), "No data found" + e.getMessage ( ), Toast.LENGTH_LONG ).show ( );
                }
                try {

                    String oldTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim ( ) + "' AND TARIFF_TO_DATE!='" + tariffEffetiveDate + "'";
                    System.out.println ( "query---" + oldTarrifQwery );
                    Cursor curOLDTarrif = SD.rawQuery ( oldTarrifQwery, null );

                    if (curOLDTarrif.moveToFirst ( )) {

                        do {

                            System.out.println ( "DATA 1 : " + curOLDTarrif.getString ( 0 ) );

                            uac = new UtilAppCommon ( );
                            try {
                                UtilAppCommon.copyResultsetToOLDTarrifClass ( curOLDTarrif );
                            } catch (SQLException e) {
                                e.printStackTrace ( );
                            }
                        } while (curOLDTarrif.moveToNext ( ));//Move the cursor to the next row.
                    } else {
                        Toast.makeText ( getApplicationContext ( ), "No data found", Toast.LENGTH_LONG ).show ( );
                    }
                } catch (Exception e) {
                    Toast.makeText ( getApplicationContext ( ), "No data found" + e.getMessage ( ), Toast.LENGTH_LONG ).show ( );
                }
                break;
            case 1:
                System.out.println ( "today is later than questionDate" );

                try {

                    String newTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim ( ) + "' AND TARIFF_TO_DATE='" + tariffEffetiveDate + "'";
                    System.out.println ( "query---" + newTarrifQwery );
                    Cursor curnewTarrif = SD.rawQuery ( newTarrifQwery, null );

                    if (curnewTarrif.moveToFirst ( )) {

                        do {

                            System.out.println ( "DATA 1 : " + curnewTarrif.getString ( 0 ) );

                            uac = new UtilAppCommon ( );
                            try {
                                UtilAppCommon.copyResultsetToTarrifClass ( curnewTarrif );
                            } catch (SQLException e) {
                                e.printStackTrace ( );
                            }
                        } while (curnewTarrif.moveToNext ( ));//Move the cursor to the next row.
                    } else {
                        Toast.makeText ( getApplicationContext ( ), "No data found", Toast.LENGTH_LONG ).show ( );
                    }
                } catch (Exception e) {
                    Toast.makeText ( getApplicationContext ( ), "No data found" + e.getMessage ( ), Toast.LENGTH_LONG ).show ( );
                }
                try {

                    String oldTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim ( ) + "' AND TARIFF_TO_DATE!='" + tariffEffetiveDate + "'";
                    System.out.println ( "query---" + oldTarrifQwery );
                    Cursor curOLDTarrif = SD.rawQuery ( oldTarrifQwery, null );

                    if (curOLDTarrif.moveToFirst ( )) {

                        do {

                            System.out.println ( "DATA 1 : " + curOLDTarrif.getString ( 0 ) );

                            uac = new UtilAppCommon ( );
                            try {
                                UtilAppCommon.copyResultsetToOLDTarrifClass ( curOLDTarrif );
                            } catch (SQLException e) {
                                e.printStackTrace ( );
                            }
                        } while (curOLDTarrif.moveToNext ( ));//Move the cursor to the next row.
                    } else {
                        Toast.makeText ( getApplicationContext ( ), "No data found", Toast.LENGTH_LONG ).show ( );
                    }
                } catch (Exception e) {
                    Toast.makeText ( getApplicationContext ( ), "No data found" + e.getMessage ( ), Toast.LENGTH_LONG ).show ( );
                }

                break;
            default:
                System.out.println ( "Invalid results from date comparison" );
                break;
        }

        System.out.println ( "Previous MET READ DATE :" + Structconsmas.Prev_Meter_Reading_Date );
        System.out.println ( "Tariff to DATE :" + Structtariff.TARIFF_TO_DATE );

        Date varDate = null;
        Date varDate2 = null;
        SimpleDateFormat sdf = new SimpleDateFormat ( "dd-MM-yyyy" );
        String DTime = sdf.format ( new Date ( ) );
        SimpleDateFormat dateFormat = new SimpleDateFormat ( "dd-MMM-yyyy" );
        try {

            if (Structconsmas.Prev_Meter_Reading_Date.isEmpty ( )) {

                varDate = dateFormat.parse ( Structconsmas.BILL_ISSUE_DATE );
                varDate2 = dateFormat.parse ( Structconsmas.BILL_ISSUE_DATE );

                dateFormat = new SimpleDateFormat ( "dd-MM-yyyy" );//22-04-0017

                String date1 = dateFormat.format ( varDate ).substring ( 0, 6 );
                String date2 = dateFormat.format ( varDate ).substring ( 8, 10 );

                String date3 = dateFormat.format ( varDate2 ).substring ( 0, 6 );
                String date4 = dateFormat.format ( varDate2 ).substring ( 8, 10 );

                String finadate = date1 + "20" + date2;
                try {
                    SimpleDateFormat format = new SimpleDateFormat ( "dd-mm-yyyy" );
                    // Date varDate2 = format.parse(Structconsmas.BILL_ISSUE_DATE);
                    String date = Structconsmas.BILL_ISSUE_DATE;

                    Date d = format.parse ( finadate );
                    Date dateBefore = new Date ( d.getTime ( ) - 5 * 24 * 3600 * 1000l );
                    System.out.print ( "************** " + format.format ( dateBefore ) ); // print 15-10-2015

                } catch (ParseException pe) {
                    pe.printStackTrace ( );
                }
                String finadate2 = date3 + "20" + date4;

                if (Structtariff.OLD_EFFECTIVE_DATE != null) {
                    Structbilling.OLD_dateDuration = printDifference ( finadate, Structtariff.OLD_TARIFF_TO_DATE );//24-03-2017 ,01-04-2099 ---diff is 29958 days
                    // Structbilling.OLD_dateDuration= printDifference(billISDate3+"-"+billISDate2+"-"+billISDate, Structtariff.EFFECTIVE_DATE);//01-03-2017, 01-04-2017 31 days
                    Structbilling.dateDuration = printDifference ( Structtariff.EFFECTIVE_DATE, finadate2 );//01-03-2017, 01-04-2017 31 days
                } else {

                    Structbilling.OLD_dateDuration = 0l;
                    Structbilling.dateDuration = printDifference ( finadate, finadate2 );//01-03-2017, 01-04-2017 31 days

                }

            } else {

                varDate = dateFormat.parse ( Structconsmas.Prev_Meter_Reading_Date );
                varDate2 = dateFormat.parse ( Structconsmas.BILL_ISSUE_DATE );

                dateFormat = new SimpleDateFormat ( "dd-MM-yyyy" );//22-04-0017

                String date1 = dateFormat.format ( varDate ).substring ( 0, 6 );
                String date2 = dateFormat.format ( varDate ).substring ( 8, 10 );

                String date3 = dateFormat.format ( varDate2 ).substring ( 0, 6 );
                String date4 = dateFormat.format ( varDate2 ).substring ( 8, 10 );

                String finadate = date1 + "20" + date2;
                String finadate2 = date3 + "20" + date4;

                if (Structtariff.OLD_EFFECTIVE_DATE != null) {
                    Structbilling.OLD_dateDuration = printDifference ( finadate, Structtariff.OLD_TARIFF_TO_DATE );//24-03-2017 ,01-04-2099 ---diff is 29958 days
                    // Structbilling.OLD_dateDuration= printDifference(billISDate3+"-"+billISDate2+"-"+billISDate, Structtariff.EFFECTIVE_DATE);//01-03-2017, 01-04-2017 31 days
                    Structbilling.dateDuration = printDifference ( Structtariff.EFFECTIVE_DATE, finadate2 );//01-03-2017, 01-04-2017 31 days
                } else {

                    Structbilling.OLD_dateDuration = 0l;
                    Structbilling.dateDuration = printDifference ( finadate, finadate2 );//01-03-2017, 01-04-2017 31 days

                }

            }


        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace ( );
        }
    }

    public Long printDifference(String str1, String str2) {


        System.out.println ( "print diff startDate nnnnnnnnn : " + str1 );
        System.out.println ( "print diff nnnnnnnnn2: " + str2 );

        SimpleDateFormat dates = new SimpleDateFormat ( "dd-MM-yyyy" );

        //Setting dates
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = dates.parse ( String.valueOf ( str1 ) );
            endDate = dates.parse ( String.valueOf ( str2 ) );
        } catch (ParseException e) {
            e.printStackTrace ( );
        }


        //milliseconds
        long different = endDate.getTime ( ) - startDate.getTime ( );

        System.out.println ( "startDate : " + startDate );
        System.out.println ( "endDate : " + endDate );
        System.out.println ( "different : " + different );

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf (
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds );

        return elapsedDays;
    }

    private void takePicture() {

        Intent intent = new Intent ( MediaStore.ACTION_IMAGE_CAPTURE );
        int currentVer = android.os.Build.VERSION.SDK_INT;
        try {
            Uri mImageCaptureUri = null;
            String state = Environment.getExternalStorageState ( );
            if (Environment.MEDIA_MOUNTED.equals ( state )) {
                if (currentVer < 24)
                    mImageCaptureUri = Uri.fromFile ( mFileTemp );
                else {
                    intent.addFlags ( Intent.FLAG_GRANT_READ_URI_PERMISSION );
                    mImageCaptureUri = FileProvider.getUriForFile ( CameraActivity.this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            mFileTemp );
                }
            } else {
                /*
                 * The solution is taken from here: http://stackoverflow.com/questions/10042695/how-to-get-camera-result-as-a-uri-in-data-folder
                 */
//                mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;

            }
            intent.putExtra ( MediaStore.EXTRA_OUTPUT, mImageCaptureUri );
            intent.putExtra ( "return-data", true );
            startActivityForResult ( intent, REQUEST_CODE_TAKE_PICTURE );
        } catch (ActivityNotFoundException e) {

            Logger.d ( getApplicationContext ( ), TAG, "cannot take picture", e );
        }
    }

//    private void startCropImage() {
//        Log.v(getApplicationContext(), "", "~~ am in StartCorpImage ~~");
//        Intent intent = new Intent(getApplicationContext(), CropImage.class);
//        intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
//        intent.putExtra(CropImage.SCALE, true);
//
//        intent.putExtra(CropImage.ASPECT_X, 3);
//        intent.putExtra(CropImage.ASPECT_Y, 2);
//        Log.v(getApplicationContext(), "", "~~ Leaving StartCorpImage ~~");
//        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
//    }

    @Override
    protected void onResume() {
        super.onResume ( );
        appCom1 = new UtilAppCommon ( );
        appCom1.getRegion ( getApplicationContext ( ) );
        getCCnBcodefromLOC_CD ( );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println ( "Request Code ____" + requestCode + "result Code ____" + requestCode );
        if (resultCode != RESULT_OK) {

            return;
        }

        Bitmap bitmap;
        UtilAppCommon uac = new UtilAppCommon ( );
        switch (requestCode) {

//            case REQUEST_CODE_GALLERY:
//
//                try {
//
//                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
//                    FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
//                    copyStream(inputStream, fileOutputStream);
//                    fileOutputStream.close();
//                    inputStream.close();
//
//                    startCropImage();
//
//                } catch (Exception e) {
//
//                    Log.e(TAG, "Error while creating temp file", e);
//                }
//
//                break;

            case REQUEST_CODE_TAKE_PICTURE:

                Logger.v ( getApplicationContext ( ), "", "AM in REQUEST_CODE_TAKE_PICTURE" );
//                startCropImage();// call this function when you need to enable the cropping functionality

//                REplaced from cropping case

                GPSTracker gps = new GPSTracker ( CameraActivity.this );
                Logger.v ( getApplicationContext ( ), "", "AM in REQUEST_CODE_CROP_IMAGE" );
//                String path = data.getStringExtra(CropImage.IMAGE_PATH);
//                System.out.println("IMNAGE PATH IS ");
//                if (path == null) {
//
//                    return;
//                }


                bitmap = BitmapFactory.decodeFile ( mFileTemp.getPath ( ) );
                Logger.v ( getApplicationContext ( ), "", "Leaving REQUEST_CODE_CROP_IMAGE" );
                System.out.println ( "SUCCESSFULLY GETTING IMAGE" + bitmap );
//                open b2 for image printing and resizing
//                Bitmap b2 = getResizedBitmap(bitmap, 240, 384);
//                creating black and white image
//                Bitmap b2 = getResizedBitmap(createBlackAndWhite(mFileTemp), 120, 128);
                // we save the file, at least until we have made use of it
                ByteArrayOutputStream outStream = new ByteArrayOutputStream ( );
                System.out.println ( "output is " + outStream );
//                b2.compress(Bitmap.CompressFormat.JPEG, 70, outStream);
////                ------------------------------------------------
//                Bitmap dest = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system
//
//                Canvas cs = new Canvas(dest);
//                Paint tPaint = new Paint();
//                tPaint.setTextSize(35);
//                tPaint.setColor(Color.BLUE);
//                tPaint.setStyle(Paint.Style.FILL);
//                float height = tPaint.measureText("yY");
//                cs.drawText(dateTime, 20f, height+15f, tPaint);
//                ------------------------------------------------

                ////////////////////////////

                Bitmap drawableBitmap = bitmap.copy ( Bitmap.Config.ARGB_8888, true );

                drawableBitmap = Utility.rotateImage ( drawableBitmap, 90 );
                Canvas canvas = new Canvas ( drawableBitmap );

                Paint paint = new Paint ( );
                paint.setAntiAlias ( true );
                paint.setColor ( Color.RED );
                paint.setStyle ( Paint.Style.FILL );
                paint.clearShadowLayer ( );

                paint.setTextSize ( 50 );
                String lat_long_on_image = gps.getLatitude ( ) + " : " + gps.getLongitude ( );
//                String lat_long_on_image = "10.23" + " : " + "11.21";
                canvas.drawText ( lat_long_on_image, 120, drawableBitmap.getHeight ( ) - 250, paint );

                paint.setTextSize ( 50 );
                canvas.drawText ( Utility.getCurrentTime ( ), 120, drawableBitmap.getHeight ( ) - 150,
                        paint );
                //////////////////////////////
                drawableBitmap = getResizedBitmap ( drawableBitmap, 1024, 1024 );
                drawableBitmap.compress ( Bitmap.CompressFormat.JPEG, 50, outStream );
//                final Canvas canvas = mSurfaceHolder.lockCanvas(null);
//                final Canvas c = new Canvas(bitmap); // creates a new canvas with your image is painted background
//                c.drawColor(0, PorterDuff.Mode.CLEAR); // this makes your whole Canvas transparent

//                canvas.drawColor(Color.WHITE);  // this makes it all white on another canvas
//                canvas.drawBitmap (bitmap, 0,  0,null); // this draws your bitmap on another canvas

                File f = new File ( Environment.getExternalStorageDirectory ( )
                        + File.separator + "/MBC/Images/" + uac.UniqueCode ( getApplicationContext
                        ( ) ) + "_" + Structconsmas.LOC_CD + "_" + Structconsmas.MAIN_CONS_LNK_NO +
                        "_" + uac.billMonthConvert ( Structconsmas.Bill_Mon ) + "_mtr.jpg" );
                Structbilling.User_Mtr_Img = uac.UniqueCode ( getApplicationContext ( ) ) + "_" +
                        Structconsmas.LOC_CD + "_" + Structconsmas.MAIN_CONS_LNK_NO + "_" + uac.billMonthConvert ( Structconsmas.Bill_Mon ) + "_mtr.jpg";

                try {
                    f.createNewFile ( );
                } catch (IOException e) {
                    Logger.e ( context, "Image Storage", "", e );
                }


                //write the bytes in file
                FileOutputStream fo = null;
                FileOutputStream os = null;
                try {

                    os = new FileOutputStream ( f );
                    //compress to specified format (PNG), quality - which is ignored for PNG, and out stream
//                    b2.compress(Bitmap.CompressFormat.JPEG, 20, os);
                    drawableBitmap.compress ( Bitmap.CompressFormat.JPEG, 20, os );
                    os.close ( );

                } catch (FileNotFoundException e) {
                    Logger.e ( context, "FNF", "", e );
                } catch (IOException e) {
                    e.printStackTrace ( );
                }

                //----------------------------------------------------------------------------------------------------------------------------

//                File file = new File (Environment.getExternalStorageDirectory()
//                        + File.separator + "/MBC/Images/yyy.jpg");// + uac.UniqueCode
//                        //(getApplicationContext()) + "_" + Structconsmas.LOC_CD + "_" +
//                    //Structconsmas.MAIN_CONS_LNK_NO + "_mtr.jpg");

//                File file = new File (Environment.getExternalStorageDirectory()
//                        + File.separator + "/MBC/Images/" + Structbilling.User_Mtr_Img);

//                File file = new File(Environment.getExternalStorageDirectory()
//                        + File.separator + "/MBC/Images/" + Structbilling.User_Mtr_Img);
//
//                InputStream inputStream = null;
//                try {
//                    inputStream = new FileInputStream(file);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                Bitmap loadedBitmap = BitmapFactory.decodeStream(inputStream);
//                Bitmap drawableBitmap = loadedBitmap.copy(Bitmap.Config.ARGB_8888, true);
//
//                drawableBitmap = Utility.rotateImage(drawableBitmap, 90);
//                Canvas canvas = new Canvas(drawableBitmap);
//
//                Paint paint = new Paint();
//                paint.setAntiAlias(true);
//                paint.setColor(Color.RED);
//                paint.setStyle(Paint.Style.FILL);
//
//                paint.setTextSize(30);
////                String lat_long_on_image = gpsTracker.getCurrentLatitude() + " : " + gpsTracker.getCurrentLongitude();
//                String lat_long_on_image = "10.23" + " : " + "11.21";
//                canvas.drawText(lat_long_on_image, 150, drawableBitmap.getHeight() - 250, paint);
//
//                paint.setTextSize(30);
//                canvas.drawText(Utility.getCurrentTime(), 150, drawableBitmap.getHeight() - 150, paint);
//
////              mImageView.setImageBitmap(drawableBitmap);
//                Utility.saveBitmap(file, loadedBitmap);
////              btnNext.setVisibility(View.VISIBLE);
//--------------------------------------------------------------------------------------------------------------------


//                try {
//                    fo.write(outStream.toByteArray());
//                } catch (IOException e) {
//                    Log.e(context, "IOE", "", e);
//                }
//                // remember close de FileOutput
//                try {
//                    fo.close();
//                } catch (IOException e) {
//                    Log.e(context, "IOE", "", e);
//                }
                // UNMETERED CONSUMER HANDLEING
                String unmetered_consuemr = unmeteredChk ( );
                if (unmetered_consuemr.equalsIgnoreCase ( "Unmetered Consumer" )) {

                    dbHelper = new DB ( getApplicationContext ( ) );
                    SD = dbHelper.getWritableDatabase ( );
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    appCom = new UtilAppCommon ( );
                    String curmeterreading = "0";
                    calBill = new CBillling ( );
                    Long dateDuration = null;
                    int calculatedunit = 0;


                    GSBilling.getInstance ( ).setPowerFactor ( Double.parseDouble ( "0" ) );
                    GSBilling.getInstance ( ).setUnitMaxDemand ( "KW" );
                    GSBilling.getInstance ( ).setPowerFactor ( Double.parseDouble ( "0.8" ) );

                    Date varDate = null;
                    SimpleDateFormat sdf = new SimpleDateFormat ( "dd-MM-yyyy" );
                    String DTime = sdf.format ( new Date ( ) );
                    SimpleDateFormat dateFormat = new SimpleDateFormat ( "dd-MMM-yyyy" );
                    try {
                        varDate = dateFormat.parse ( Structconsmas.Prev_Meter_Reading_Date );

                        dateFormat = new SimpleDateFormat ( "dd-MM-yyyy" );//22-04-0017
                        System.out.println ( "Date :" + dateFormat.format ( varDate ) );
                        String date1 = dateFormat.format ( varDate ).substring ( 0, 6 );
                        String date2 = dateFormat.format ( varDate ).substring ( 8, 10 );

                        String finadate = date1 + "20" + date2;
                        System.out.println ( "DDDDDDDDDDDDDDDDDDDDDDDDDA :" + finadate );
                        if (Structbilling.dateDuration == 0) {
                            Structbilling.dateDuration = 30l;
                        }
                        dateDuration = Structbilling.dateDuration;
                        calBill.totalDateduration = dateDuration;
                        System.out.println ( "DATE DURATION  :" + calBill.totalDateduration );
                        System.out.println ( "DATE DURATION 2  :" + dateDuration );
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace ( );
                    }

                    calBill.curMeterRead = curmeterreading;
                    calBill.curMeterReadDate = DTime;
//                System.out.println("CUR READ DATE :" + Structconsmas.CUR_READ_DATE);

                    calBill.curMeterStatus = 0;
                    calBill.derivedMeterStatus = "0";

                    Structbilling.Derived_mtr_status = "0";
                    Structbilling.Cur_Meter_Stat = 0;

                    calculatedunit = calBill.Unitcalculation ( Structbilling.Derived_mtr_status, curmeterreading, Structbilling.Cur_Meter_Stat );
                    calBill.unit = calculatedunit;
                    calBill.CalculateBill ( );

                    appCom.copyResultsetToBillingClass ( calBill );
                    GSBilling.getInstance ( ).setCurmeter ( 0 );

                    Intent intent = new Intent ( CameraActivity.this, BillingViewActivity.class );
                    startActivity ( intent );
                    overridePendingTransition ( R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left );

                } else {

                    Intent intent = new Intent ( getApplicationContext ( ), Readinginput.class );
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.putExtra("IMAGE",bitmap);
                    startActivity ( intent );
                    overridePendingTransition ( R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left );

                }


                break;
//            case REQUEST_CODE_CROP_IMAGE:
//                Log.v(getApplicationContext(), "", "AM in REQUEST_CODE_CROP_IMAGE");
//                String path = data.getStringExtra(CropImage.IMAGE_PATH);
//                System.out.println("IMNAGE PATH IS ");
//                if (path == null) {
//
//                    return;
//                }
//
//                bitmap = BitmapFactory.decodeFile(mFileTemp.getPath());
//                Log.v(getApplicationContext(), "", "Leaving REQUEST_CODE_CROP_IMAGE");
//                System.out.println("SUCCESSFULLY GETTING IMAGE" + bitmap);
////                open b2 for image printing and resizing
////                Bitmap b2 = getResizedBitmap(bitmap, 240, 384);
////                creating black and white image
////                Bitmap b2 = getResizedBitmap(createBlackAndWhite(mFileTemp), 120, 128);
//                // we save the file, at least until we have made use of it
//                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//                System.out.println("output is " + outStream);
////                b2.compress(Bitmap.CompressFormat.JPEG, 70, outStream);
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
//                File f = new File(Environment.getExternalStorageDirectory()
//                        + File.separator + "/MBC/Images/" + GSBilling.getInstance().getKEYNAME() + "_" + Structconsmas.Consumer_Number + "_mtr.jpg");
//                Structbilling.User_Mtr_Img = GSBilling.getInstance().getKEYNAME() + "_" + Structconsmas.Consumer_Number + "_mtr.jpg";
//                try {
//                    f.createNewFile();
//                } catch (IOException e) {
//                    Log.e(context, "Image Storage", "", e);
//                }
//                //write the bytes in file
//                FileOutputStream fo = null;
//                FileOutputStream os = null;
//                try {
//
//                    os = new FileOutputStream(f);
//                    //compress to specified format (PNG), quality - which is ignored for PNG, and out stream
////                    b2.compress(Bitmap.CompressFormat.JPEG, 20, os);
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, os);
//                    os.close();
//
//                } catch (FileNotFoundException e) {
//                    Log.e(context, "FNF", "", e);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
////                try {
////                    fo.write(outStream.toByteArray());
////                } catch (IOException e) {
////                    Log.e(context, "IOE", "", e);
////                }
////                // remember close de FileOutput
////                try {
////                    fo.close();
////                } catch (IOException e) {
////                    Log.e(context, "IOE", "", e);
////                }
//                Intent intent = new Intent(getApplicationContext(), MeterState.class);
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                intent.putExtra("IMAGE",bitmap);
//                startActivity(intent);
//                overridePendingTransition(R.anim.anim_slide_in_left,
//                        R.anim.anim_slide_out_left);

//                break;
        }
        super.onActivityResult ( requestCode, resultCode, data );
    }

    //    public static Bitmap createBlackAndWhite (File filenew){
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ALPHA_8;
//        Bitmap bitmap = BitmapFactory.decodeFile(filenew.getAbsolutePath(), options);
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        // create output bitmap
//        Bitmap bmOut = Bitmap.createBitmap(width, height, bitmap.getConfig());
//        // color information
//        int A, R, G, B;
//        int pixel;
//
//        // scan through all pixels
//        for (int x = 0; x < width; ++x) {
//            for (int y = 0; y < height; ++y) {
//                // get pixel color
//                pixel = bitmap.getPixel(x, y);
//                A = Color.alpha(pixel);
//                R = Color.red(pixel);
//                G = Color.green(pixel);
//                B = Color.blue(pixel);
//                int gray = (int) (0.2989 * R + 0.5870 * G + 0.1140 * B);
//
//                // use 128 as threshold, above -> white, below -> black
//                if (gray > 128)
//                    gray = 255;
//                else
//                    gray = 0;
//                // set new pixel color to output bitmap
//                bmOut.setPixel(x, y, Color.argb(A, gray, gray, gray));
//            }
//        }
//        return bmOut;
//    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

        int width = bm.getWidth ( );
        int height = bm.getHeight ( );

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // create a matrix for the manipulation
        Matrix matrix = new Matrix ( );

        // resize the bit map
        matrix.postScale ( scaleWidth, scaleHeight );

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap ( bm, 0, 0, width, height, matrix, false );

        return resizedBitmap;

    }

    private Bitmap timestampItAndSave(Bitmap toEdit) {
        Bitmap dest = Bitmap.createBitmap ( toEdit.getWidth ( ), toEdit.getHeight ( ), Bitmap.Config.ARGB_8888 );

        SimpleDateFormat sdf = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss" );
        String dateTime = sdf.format ( Calendar.getInstance ( ).getTime ( ) ); // reading local time in the system

        Canvas cs = new Canvas ( dest );
        Paint tPaint = new Paint ( );
        tPaint.setTextSize ( 35 );
        tPaint.setColor ( Color.BLUE );
        tPaint.setStyle ( Paint.Style.FILL );
        float height = tPaint.measureText ( "yY" );
        cs.drawText ( dateTime, 20f, height + 15f, tPaint );
        try {
            dest.compress ( Bitmap.CompressFormat.JPEG, 100, new FileOutputStream ( new File
                    ( Environment.getExternalStorageDirectory ( ) + "/MBC/Images/" + Structbilling.User_Mtr_Img ) ) );
        } catch (FileNotFoundException e) {
            e.printStackTrace ( );
            return null;
        }
        return dest;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId ( ) == android.R.id.home) {
            finish ( );
            overridePendingTransition ( R.anim.anim_slide_in_right,
                    R.anim.anim_slide_out_right );
            return true;
        }
        return false;
    }

    private String LoadConvert() {
        switch (Structconsmas.Load_Type) {
            case "W":
                load_unit = "W";
                break;
            case "1":
                load_unit = "W";
                break;
            // return Structconsmas.Load;
            case "KW":
                load_unit = "KW";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "2":
                load_unit = "KW";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "BHP":
                load_unit = "HP";
//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");
                break;
            case "3":
                load_unit = "HP";
//                return uac.ConvertMD(Structconsmas.Load, "HP", "W");
                break;
            case "KVA":
                load_unit = "KVA";
//                return uac.ConvertMD(Structconsmas.Load, "KVA", "W");
                break;
            case "4":
                load_unit = "KVA";

                break;
        }
        return load_unit;
    }

    private String TariffConvert() {
        switch (Structconsmas.Tariff_Code) {
            case "101":
                tarrif_Cate = "LV-1 Dom Cons with meter";
                break;
            case "102":
                tarrif_Cate = "LV-2.2 Non Domestic consumers";
                break;
            // return Structconsmas.Load;
            case "113":
                tarrif_Cate = "LV-1.2 Single Point Unmetered Benef Rural";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "201":
                tarrif_Cate = "LV-2.2 X-Ray Plant";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "204":
                tarrif_Cate = "LV-2.1 NON DOM GOVT SCHOOL";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "205":
                tarrif_Cate = "LV-2.2 Non Dom DemandBased";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "206":
                tarrif_Cate = "LV-2.1 Non Dom Dem Based";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "303":
                tarrif_Cate = "Govt Sch LV - 5.1d DTR Agr Metered consumers";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "305":
                tarrif_Cate = "LV - 5.1 Agri Metered consumers";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "306":
                tarrif_Cate = "LV - 5.1 Agr Meter Benef upto 5 HP";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "311":
                tarrif_Cate = "LV-5.4 Agr without meter upto 3 HP";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "312":
                tarrif_Cate = "LV-5.4 Agr without meter >3 to 5 HP";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "313":
                tarrif_Cate = "LV-5.4 Agr without meter 5 to 10 HP";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "314":
                tarrif_Cate = "LV-5.4 Agr without meter > 10 HP";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "319":
                tarrif_Cate = "Agr FLAT BENF Up-to 3HP";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "320":
                tarrif_Cate = "Agr FLAT BENF 3-5 HP";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "331":
                tarrif_Cate = "Utthan Yojna upto 3HP MTRD";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "332":
                tarrif_Cate = "Utthan Yojna 3-5 HP MTR";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "333":
                tarrif_Cate = "Utthan Yojna 5-10 HP MTR";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "334":
                tarrif_Cate = "Utthan Yojna 10-20 HP MTR";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "341":
                tarrif_Cate = "LV-5.2 Oth Agri use- horticulture";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;

            case "351":
                tarrif_Cate = "LV-5.3 Other Agriculture";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "352":
                tarrif_Cate = "LV-5.3 Oth Agri Related Power Loom";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "353":
                tarrif_Cate = "LV-5.3 Oth Agri Related Demad Based loom";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "550":
                tarrif_Cate = "4.1a-IndDem Load<=25 (power loom)";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "551":
                tarrif_Cate = "LV-4.1a Indust Dem Base 0-25";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "561":
                tarrif_Cate = "HPLV-4.1a Ind Dem 25-75HP";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "571":
                tarrif_Cate = "LV-4.1a Ind Dem 75-100HP";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "581":
                tarrif_Cate = "LV-4.1a Ind Dem >100 HP";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "591":
                tarrif_Cate = "4.1a IndDem Load>100 N";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "599":
                tarrif_Cate = "CD<=100HP 4.1B-IndDem";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "611":
                tarrif_Cate = "CD<=150(TEMPORARY) LV-3.2a PUBLIC PUR LGT";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "612":
                tarrif_Cate = "MUNSPAL LV-3.2b PUBLIC PUR LGT NGR";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "613":
                tarrif_Cate = "PNCHT LV-3.2c PUBLIC PUR LGT GRM";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "811":
                tarrif_Cate = "PNCHLV-3.1a W/W AND CREM";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "812":
                tarrif_Cate = "MUNSPALLV-3.1b W/W AND CREM NGR";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "813":
                tarrif_Cate = "PNCH LV-3.1c W/W AND CREM GRM";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "901":
                tarrif_Cate = "PNCH LV-1.2 TEMP-Dom House Const";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "902":
                tarrif_Cate = "LV-2.2 TEMP-Non Domestic Mela";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "903":
                tarrif_Cate = "LV-1.2 TEMP-Dom Marriage/Social";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "904":
                tarrif_Cate = "LV-2.3 TEMP-Non Domestic Marriage";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "914":
                tarrif_Cate = "LV-3D PUB WTR WORKS TEMP MNS CRP";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "915":
                tarrif_Cate = "LV-5c TEMP-AGR METER";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "916":
                tarrif_Cate = "LV-5c.TEMP-AGR-FLAT";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "917":
                tarrif_Cate = "LV-5.2c TEMP-OTHER AGR METER";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "924":
                tarrif_Cate = "LV-3D PUB WATER WORK";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "104":
                tarrif_Cate = "Domestic DTR METERED";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "203":
                tarrif_Cate = "LV-2.2  X-RAY Plant Non Dom Demand Based";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "504":
                tarrif_Cate = "IND LT Non-Seasonal upto 10 HP Flour Mills";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "507":
                tarrif_Cate = "IND LT Non-Seasonal upto 10 HP Flour Mills";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
            case "804":
                tarrif_Cate = "IND LT Non-Seasonal upto 10 HP Flour Mills";
//                return uac.ConvertMD(Structconsmas.Load, "KW", "W");
                break;
        }
        return tarrif_Cate;
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

    @Override
    public void onUserInteraction() {
        super.onUserInteraction ( );
        ((GlobalPool) getApplication ( )).onUserInteraction ( );
    }

    @Override
    public void userLogoutListaner() {
        finish ( );
        Intent intent = new Intent ( CameraActivity.this, MainActivity.class );
        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( intent );

    }

    public void duplicatePrintTariff(DB dbHelper, SQLiteDatabase SD) {
        try {

            String newTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" + Structconsmas.Tariff_Code.trim ( ) + "' LIMIT 1";
            if (Structconsmas.SYSTEM_FLAG != null && !Structconsmas.SYSTEM_FLAG.isEmpty ( )) { // null check empty chk
                if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase ( "S" )) {
                    newTarrifQwery = "SELECT * FROM TBL_TARRIF_MP where TARIFF_CODE='" +
                            getRMScodefromSybase ( Structconsmas.Tariff_Code.trim ( ), dbHelper, SD ) +
                            "' AND TARIFF_TO_DATE='" + tariffEffetiveDate + "'";
                }

            }

            System.out.println ( "query---" + newTarrifQwery );
            Cursor curnewTarrif = SD.rawQuery ( newTarrifQwery, null );

            if (curnewTarrif.moveToFirst ( )) {

                do {

                    System.out.println ( "TARIFF DATA 1 : " + curnewTarrif.getString ( 0 ) );
                    System.out.println ( "TARIFF DATA 2 : " + curnewTarrif.getString ( 1 ) );
                    System.out.println ( "TARIFF DATA 3 : " + curnewTarrif.getString ( 2 ) );
                    System.out.println ( "TARIFF DATA 4 : " + curnewTarrif.getString ( 3 ) );

                    uac = new UtilAppCommon ( );
                    try {
                        UtilAppCommon.copyResultsetToTarrifClass ( curnewTarrif );
                    } catch (SQLException e) {
                        e.printStackTrace ( );
                    }
                } while (curnewTarrif.moveToNext ( ));//Move the cursor to the next row.
            } else {
                Toast.makeText ( getApplicationContext ( ), "No data found", Toast.LENGTH_LONG ).show ( );
                NoTariffFlag = true;
            }
        } catch (Exception e) {
            Toast.makeText ( getApplicationContext ( ), "No data found" + e.getMessage ( ), Toast.LENGTH_LONG ).show ( );
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        dismissProgressDialog ( );
        Debug.stopMethodTracing ( );
        super.onDestroy ( );
    }

    private void dismissProgressDialog() {
        if (sDialog != null && sDialog.isShowing ( )) {
            sDialog.dismiss ( );
        }
    }

    private String dateCompare(Date date2Day, Date dateToComapre) {
        switch (date2Day.compareTo ( dateToComapre )) {
            case -1:
                System.out.println ( "today is sooner than questionDate" );
                return "sooner";
            case 0:
                System.out.println ( "today and questionDate are equal" );
                return "equal";
            case 1:
                System.out.println ( "today is later than questionDate" );
                return "later";
        }

        return "sooner";
    }

    private String unitPercentageChk() {

        double avg_Units = 0;
        double unit_calculated = Double.parseDouble ( Structbilling.O_BilledUnit_Actual );

        if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase ( "S" )) {
            avg_Units = (Double.parseDouble ( Structconsmas.AVGUNITS1 ) + Double.parseDouble ( Structconsmas.AVGUNITS2 ) + Double.parseDouble ( Structconsmas.AVGUNITS3 ));
        } else {
            avg_Units = (Double.parseDouble ( Structconsmas.PREV_AVG_UNIT ));
        }
//        if (unit_calculated > 50) {
//
//
//            if (((Double.parseDouble(Structbilling.O_BilledUnit_Actual))) > (avg_Units * 1.5)) {
//                return " - Consumption is 1.5 Times higher \n";//, "2 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
//            }
//
//
//        }

        return "0";
    }

}

