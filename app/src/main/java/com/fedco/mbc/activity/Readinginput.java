package com.fedco.mbc.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fedco.mbc.CustomClasses.DialogBox;
import com.fedco.mbc.R;
import com.fedco.mbc.billinglogic.CBillling;
import com.fedco.mbc.bluetoothprinting.GlobalPool;
import com.fedco.mbc.logging.Log4jHelper;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.meter_selection.StartMeterReading;
import com.fedco.mbc.meter_selection.fragment.BottomSheetFragment;
import com.fedco.mbc.model.Structbilling;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.model.Structtariff;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.GPSTracker;
import com.fedco.mbc.utils.HttpFileUpload;
import com.fedco.mbc.utils.InputFilterMinMax;
import com.fedco.mbc.utils.UtilAppCommon;
import com.fedco.mbc.utils.Utility;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.paperdb.Paper;

public class Readinginput extends AppCompatActivity implements StartMeterReading, LogoutListaner {
    private BottomSheetDialogFragment bottomSheetDialogFragment;
    Cursor curderivedmeterstatusData;
    SQLiteDatabase SD;
    DB dbHelper;
    Boolean flagCheck = false;
    Cursor dt_number_cursor, metermake_cursor;
    ArrayList <String> dtnumber_id_list, dt_number_list, metermakeid_list, metermake_list;
    ArrayAdapter dtNumber_adapter, metermake_adapter, meterStatusAdapter;

    Spinner pfUnit, mdUnits;

    String barcode_data;
    String data_validate;
    // barcode image
    Bitmap bitmap = null;
    String currentDateandTime;
    String curmeterreading;
    int curmeterstatus;
    UtilAppCommon ucom;
    CBillling uc;
    CBillling calBill;
    private static Readinginput RActivity;
    Logger Log;
    SweetAlertDialog sweetAlertDialog;

    public static Readinginput getRActivity() {
        return RActivity;
    }

    EditText etCR, etMD, etPF, tv_md_value;
    Spinner spinnerMeterMfg, spinnerReason;
    TextView tvName, tvDMS, tvMS, tv_meter_number;
    Button btnContread, btnTakeread, btn_send_log_to_server;
    String unitLoad;
    String unitMD;
    String selected_meter;

    private RecyclerView rv_meterImages;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    GPSTracker gps;
    double latitude;
    double longitude;

    double C_assesed_consumption;


    Spinner sp_reasons;
    // SQLiteDatabase SD;
    DB databaseHelper;
    UtilAppCommon appCom;
    //  CBillling calBill;

    // Cursor dt_number_cursor, metermake_cursor;
    String reason, reason_id;

    TextView tv_readingDate, tv_heading;

    Spinner sp_status;

    int meterStatus;

    private ArrayList <String> results = new ArrayList <String> ( );

    ArrayList <String> meterStatusList;

    public static String consumerNo, readingDate, tariffCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );

        setContentView ( R.layout.activity_readinginput );
        System.out.println ("Reading Input Activity" );
        configureLog4j ( );
        RActivity = this;
        getSupportActionBar ( ).setHomeAsUpIndicator ( R.drawable.back );
        getSupportActionBar ( ).setTitle ( "Reading Input" );
        System.out.print ( Structconsmas.Consumer_Number + "---" + Structconsmas.Old_Consumer_Number + "---" + Structconsmas.Name + "---" + Structconsmas.address1 + "---" + Structconsmas.address2 + "---" + Structconsmas.Cycle + "---" + Structconsmas.Electrical_Address + "---" + Structconsmas.Route_Number + "---" + Structconsmas.Division_Name + "---" + Structconsmas.Sub_division_Name + "---" + Structconsmas.Section_Name + "---" + Structconsmas.Meter_S_No + "---" + Structconsmas.Meter_Type + "---" + Structconsmas.Meter_Phase + "---" + Structconsmas.Multiply_Factor + "---" + Structconsmas.Meter_Ownership + "---" + Structconsmas.Meter_Digits + "---" + Structconsmas.Category + "---" + Structconsmas.Tariff_Code + "---" + Structconsmas.Load + "---" + Structconsmas.Load_Type + "---" + Structconsmas.ED_Exemption + "---" + Structconsmas.Prev_Meter_Reading + "---" + Structconsmas.Prev_Meter_Reading_Date + "---" + Structconsmas.Prev_Meter_Status + "---" + Structconsmas.Meter_Status_Count + "---" + Structconsmas.Consump_of_Old_Meter + "---" + Structconsmas.Meter_Chng_Code + "---" + Structconsmas.New_Meter_Init_Reading + "---" + Structconsmas.misc_charges + "---" + Structconsmas.Sundry_Allow_EC + "---" + Structconsmas.Sundry_Allow_ED + "---" + Structconsmas.Sundry_Allow_MR + "---" + Structconsmas.Sundry_Allow_DPS + "---" + Structconsmas.Sundry_Charge_EC + "---" + Structconsmas.Sundry_Charge_ED + "---" + Structconsmas.Sundry_Charte_MR + "---" + Structconsmas.Sundry_Charge_DPS + "---" + Structconsmas.Pro_Energy_Chrg + "---" + Structconsmas.Pro_Electricity_Duty + "---" + Structconsmas.Pro_Units_Billed + "---" + Structconsmas.Units_Billed_LM + "---" + Structconsmas.Avg_Units + "---" + Structconsmas.Load_Factor_Units + "---" + Structconsmas.Last_Pay_Date + "---" + Structconsmas.Last_Pay_Receipt_Book_No + "---" + Structconsmas.Last_Pay_Receipt_No + "---" + Structconsmas.Last_Total_Pay_Paid + "---" + Structconsmas.Pre_Financial_Yr_Arr + "---" + Structconsmas.Cur_Fiancial_Yr_Arr + "---" + Structconsmas.SD_Interest_chngto_SD_AVAIL + "---" + Structconsmas.Bill_Mon + "---" + Structconsmas.New_Consumer_Flag + "---" + Structconsmas.Cheque_Boune_Flag + "---" + Structconsmas.Last_Cheque_Bounce_Date + "---" + Structconsmas.Consumer_Class + "---" + Structconsmas.Court_Stay_Amount + "---" + Structconsmas.Installment_Flag + "---" + Structconsmas.Round_Amount + "---" + Structconsmas.Flag_For_Billing_or_Collection + "---" + Structconsmas.Meter_Rent + "---" + Structconsmas.Last_Recorded_Max_Demand + "---" + Structconsmas.Delay_Payment_Surcharge + "---" + Structconsmas.Meter_Reader_ID + "---" + Structconsmas.Meter_Reader_Name + "---" + Structconsmas.Division_Code + "---" + Structconsmas.Sub_division_Code + "---" + Structconsmas.Section_Code );

        consumerNo = Structconsmas.Consumer_Number;
        readingDate = Structconsmas.Prev_Meter_Reading_Date;
        tariffCode = Structconsmas.Tariff_Code;


        String curmeter = getIntent ( ).getStringExtra ( "Value" );

        curmeterstatus = GSBilling.getInstance ( ).getCurmeter ( );
        ((GlobalPool) getApplication ( )).registerSessionListaner ( this );
        ((GlobalPool) getApplication ( )).startUserSession ( );

        Logger.e ( getApplicationContext ( ), "MeterStatAct", "current meter status is : " + curmeterstatus + " posion is : " + curmeter );
        Logger.e ( getApplicationContext ( ), "MeterStatAct", "Previos meter status is : " + Structconsmas.Prev_Meter_Status );

        storeReasons ( );

        tv_readingDate = findViewById ( R.id.tv_readingDate );
        tv_heading = findViewById ( R.id.tv_heading );

        rv_meterImages = findViewById ( R.id.rv_meterImages );
        rv_meterImages.setLayoutManager ( new LinearLayoutManager ( this, LinearLayoutManager.HORIZONTAL, false ) );

      // if(Home.isMeter){
           rv_meterImages.setAdapter ( new ImageAdapter ( this, PictureActivity.filePaths ) );
       //}

        sp_status = findViewById ( R.id.sp_status );
        meterStatusList = new ArrayList <> ( );
        meterStatusList.add ( "Normal" );
        meterStatusList.add ( "Meter Faulty" );
        meterStatusList.add ( "Reading Not Taken" );
        meterStatusList.add ( "Meter Missing" );
        meterStatusList.add ( "Meter OverFlow" );
        meterStatusList.add ( "Not Traceable" );
        meterStatusList.add ( "Premise Locked" );

        meterStatusAdapter = new ArrayAdapter <String> (
                getApplicationContext ( ), R.layout.custom_spinner, R.id.textView1, meterStatusList );
        sp_status.setAdapter ( meterStatusAdapter );

//        tvDMS = (TextView) findViewById(R.id.TextViewDMSValues);
        tv_meter_number = findViewById ( R.id.tv_meter_number );
        tv_md_value = findViewById ( R.id.tv_md_value );
        tvName = findViewById ( R.id.TextViewRINameValue );
        tvMS = findViewById ( R.id.TextViewRIPsValues );
        etCR = findViewById ( R.id.TextViewRICrValue );
//        etMD = (EditText) findViewById(R.id.TextViewRIMdValue);
        etPF = findViewById ( R.id.TextViewRIPfValue );
        spinnerMeterMfg = findViewById ( R.id.spinnerMeterMfg );
        spinnerReason = findViewById ( R.id.TextViewRRemValue );
        tv_meter_number.setText ( Structconsmas.Meter_S_No );
        etPF.setFilters ( new InputFilter[]{new InputFilterMinMax ( 0.0f, 1f ), new InputFilter.LengthFilter ( 4 )} );
//        etPF.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(etPF,1,2)});
        etPF.setText ( "0.8" );

//        etPF.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(etPF,1,2)});
//        pfUnit  = (Spinner) findViewById(R.id.TextViewRIMdUnitValue); //TextViewRIMdUnitValue
        mdUnits = findViewById ( R.id.TextViewRIMdUnitValue );
        btnContread = findViewById ( R.id.ButtonContreading );
        btnTakeread = findViewById ( R.id.ButtonTakereading );
        btn_send_log_to_server = findViewById ( R.id.btn_send_log_to_server );

        spinnerMeterMfg.setVisibility ( View.INVISIBLE );
        btnTakeread.setVisibility ( View.GONE );

        tv_md_value.setInputType ( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED );
        etPF.setInputType ( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED );

        tvMS.setText ( curmeter );
        tvName.setText ( Structconsmas.Name );
//        tvDMS.setText(Structbilling.Bill_Basis);

        ucom = new UtilAppCommon ( );
        dbHelper = new DB ( getApplicationContext ( ) );
        SD = dbHelper.getWritableDatabase ( );


        sp_reasons = findViewById ( R.id.spr_reasons );


        metermakeid_list = new ArrayList <> ( );
        metermake_list = new ArrayList <> ( );


        tv_readingDate.setText ( Structbilling.Bill_Date );


        //  new MeterMake ( Readinginput.this ).execute ( );

        //   new MeterStatus ( Readinginput.this ).execute ( );

        SimpleDateFormat sdf = new SimpleDateFormat ( "yyyyMMdd" );
        currentDateandTime = sdf.format ( new Date ( ) );

        Logger.e ( getApplicationContext ( ), "MeterStatAct", "prev meter Date   " + Structconsmas.Prev_Meter_Reading_Date );
        Logger.e ( getApplicationContext ( ), "MeterStatAct", "current Date   " + currentDateandTime );

        String ret = "select DERIVEDMETERSTATUS from TBL_DERIVEDMETERSTATUSCODE Where PREVIOUSMETERSTATUSCODE='" + Structconsmas.Prev_Meter_Status.trim ( ) + "' and CURRENTMETERSTATUSCODE='" + curmeterstatus + "'";

        curderivedmeterstatusData = SD.rawQuery ( ret, null );
        if (curderivedmeterstatusData != null && curderivedmeterstatusData.moveToFirst ( )) {
            System.out.println ( "derived meterstatus is :" + curderivedmeterstatusData.getString ( 0 ) );
        }
        bottomSheetDialogFragment = new BottomSheetFragment ( );
        btnTakeread.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                bottomSheetDialogFragment.show ( getSupportFragmentManager ( ), bottomSheetDialogFragment.getTag ( ) );

            }
        } );
        btn_send_log_to_server.setOnClickListener ( new View.OnClickListener ( ) {

            @Override
            public void onClick(View v) {
                if (Utility.isConnectingToInternet ( )) {
                    new MakeZip ( ).execute ( );
                } else {
                    Toast.makeText ( getApplicationContext ( ), "Please Try With Working Internet..", Toast.LENGTH_SHORT ).show ( );
                }
            }
        } );


        sp_status.setOnItemSelectedListener ( new AdapterView.OnItemSelectedListener ( ) {
            @Override
            public void onItemSelected(AdapterView <?> adapterView, View view, int i, long l) {

                System.out.println ( "This is the position " + i );
                switch (i) {
                    case 0://NORAML ACT(1)//MP NOARMAL


                        Structbilling.Reasons = "";
                        // Intent intentnormal = new Intent ( getApplicationContext ( ), Readinginput.class );
                        //  intentnormal.putExtra("Value", selectedValue);
                        //intent.putExtra("Position", 1);
                        GSBilling.getInstance ( ).setCurmeter ( 1 );


                        // startActivity ( intentnormal );

                       /* overridePendingTransition ( R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left );
*/


                        setReasonAdapter ( "Normal", "NormalList" );


                        meterStatus = 1;

                        GSBilling.getInstance ().setNormalReason ( 0 );

                        break;
                    case 1://// MP PFL



                        GSBilling.getInstance ( ).setMaxDemand ( Double.parseDouble ( "0" ) );
                        GSBilling.getInstance ( ).setUnitMaxDemand ( "KW" );
                        GSBilling.getInstance ( ).setPowerFactor ( Double.parseDouble ( "0.8" ) );
                        Structbilling.Avrg_PF = String.valueOf ( GSBilling.getInstance ( ).getPowerFactor ( ) );


                        dbHelper = new DB ( getApplicationContext ( ) );
                        SD = dbHelper.getWritableDatabase ( );
                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        appCom = new UtilAppCommon ( );
                        String curmeterreading = "0";
//                String curmeterreading = Structconsmas.ACC_MIN_UNITS;
                        calBill = new CBillling ( );
                        Long dateDuration = null;
                        int calculatedunit = 0;

                        GSBilling.getInstance ( ).setCurmeter ( 2 );


                        calBill.curMeterRead = curmeterreading;
                        System.out.println ( "CUR READ DATE :" + Structconsmas.CUR_READ_DATE );

                        calBill.curMeterStatus = 2;
                        calBill.derivedMeterStatus = "2";

                        meterStatus = 2;

                        Paper.book ( "MeterFaulty" ).write ( "FaultyList", meterFaultList ( ) );

                        setReasonAdapter ( "MeterFaulty", "FaultyList" );

                        GSBilling.getInstance ().setNormalReason ( 1 );


//                Intent intent = new Intent(MeterState.this, BillingViewActivity.class);

                        break;


                    case 2:// MP ACCESSED UNIT


                        Structbilling.Reasons = "";
                        //  Intent intentoverflow = new Intent ( getApplicationContext ( ), Readinginput.class );
                        // intentoverflow.putExtra("Value", selectedValue);
                        //intent.putExtra("Position", 1);
                        //   GSBilling.getInstance ( ).setCurmeter ( 1 );
                        GSBilling.getInstance ( ).setCurmeter ( 3 );

                        //startActivity ( intentoverflow );
                        /*overridePendingTransition ( R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left );
*/
                        System.out.println ( "" + Structconsmas.Prev_Meter_Status );
//
                        meterStatus = 1;


                        setReasonAdapter ( "ReadingNotTaken", "ReadingNotList" );

                        GSBilling.getInstance ().setNormalReason ( 2 );

                        break;



                    case 3://METER DEFECTIVE/CHanged

                        //  GSBilling.getInstance ( ).setCurmeter ( 9 );
                        GSBilling.getInstance ( ).setCurmeter ( 4 );

                        GSBilling.getInstance ( ).setNormalReason ( 0 );

/*
                        calBill.curMeterRead = curmeterreading;
                        calBill.curMeterStatus = 4;
                        calBill.derivedMeterStatus = "4";
*/

                        Structbilling.Derived_mtr_status = "4";
                        Structbilling.Cur_Meter_Stat = 4;

                        GSBilling.getInstance ().setNormalReason ( 3 );

                        setReasonAdapter ( "MeterMissing", "MeterMissingList" );


                        break;

                    case 4://DIAL OVER ACT(2)


                        Logger.e ( getApplicationContext ( ), "MeterStatAct", "PrevMtrSt :" + Structconsmas.Prev_Meter_Status );
                        //  Intent intent = new Intent ( getApplicationContext ( ), Readinginput.class );
                        //  GSBilling.getInstance ( ).setCurmeter ( 1 );
                        GSBilling.getInstance ( ).setCurmeter ( 5 );

                        GSBilling.getInstance ( ).setMetOVERFLOW ( 1 );
                        /*startActivity ( intent );
                        overridePendingTransition ( R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left );
*/

                        meterStatus = 1;

                        GSBilling.getInstance ().setNormalReason ( 4 );

                        setReasonAdapter ( "MeterOverflow", "MeterOverflowList" );

                        break;
                    case 5:// MP ACCESSED UNIT


                        Structbilling.Reasons = "";
                        //  Intent intentoverflow = new Intent ( getApplicationContext ( ), Readinginput.class );
                        // intentoverflow.putExtra("Value", selectedValue);
                        //intent.putExtra("Position", 1);
                        //   GSBilling.getInstance ( ).setCurmeter ( 1 );
                        GSBilling.getInstance ( ).setCurmeter ( 6 );
                        //startActivity ( intentoverflow );
                        /*overridePendingTransition ( R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left );
*/
                        System.out.println ( "" + Structconsmas.Prev_Meter_Status );
//
                        meterStatus = 1;


                        setReasonAdapter ( "NotTraceable", "NotTraceableList" );

                        GSBilling.getInstance ().setNormalReason ( 5 );

                        break;


                    case 6:// MP ACCESSED UNIT


                        Structbilling.Reasons = "";
                        //  Intent intentoverflow = new Intent ( getApplicationContext ( ), Readinginput.class );
                        // intentoverflow.putExtra("Value", selectedValue);
                        //intent.putExtra("Position", 1);
                        GSBilling.getInstance ( ).setCurmeter ( 7 );
                        //startActivity ( intentoverflow );
                        /*overridePendingTransition ( R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left );
*/
                        System.out.println ( "" + Structconsmas.Prev_Meter_Status );
//
                        meterStatus = 1;


                        setReasonAdapter ( "PremiseLocked", "PremiseLockedList" );
                        GSBilling.getInstance ().setNormalReason ( 6 );


                        break;


                }


                if (i != 0) {

                    etCR.setVisibility ( View.GONE );

                } else {
                    etCR.setVisibility ( View.VISIBLE );

                }
            }

            @Override
            public void onNothingSelected(AdapterView <?> adapterView) {

            }


        } );


        btnContread.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                /****Chechiong if Edit Text is Empty ****/


                if (GSBilling.getInstance ( ).getMetOVERFLOW ( ) == 1) {

                    System.out.println ( "This is the reading in first if" );

//                    if (!etPF.getText().toString().equalsIgnoreCase("0")) {
                    if (etPF.getText ( ).toString ( ) != null && !etPF.getText ( ).toString ( ).isEmpty ( ) && !etPF.getText ( ).toString ( ).equalsIgnoreCase ( "0" ) && !etPF.getText ( ).toString ( ).equalsIgnoreCase ( "0.0" ) && !etPF.getText ( ).toString ( ).equalsIgnoreCase ( "0.00" )) {
                        GSBilling.getInstance ( ).setPowerFactor ( Double.parseDouble ( etPF.getText ( ).toString ( ) ) );
                        Structbilling.Avrg_PF = String.valueOf ( GSBilling.getInstance ( ).getPowerFactor ( ) );

                    } else {
                        GSBilling.getInstance ( ).setPowerFactor ( Double.parseDouble ( "0.8" ) );
                        Structbilling.Avrg_PF = String.valueOf ( GSBilling.getInstance ( ).getPowerFactor ( ) );
                    }

                    if (!mdUnits.getSelectedItem ( ).toString ( ).equalsIgnoreCase ( "Select Unit" )) {
                        GSBilling.getInstance ( ).setUnitMaxDemand ( mdUnits.getSelectedItem ( ).toString ( ) );
//                        Structbilling.MD_UNIT_CD=String.valueOf(GSBilling.getInstance().getUnitMaxDemand());
                    } else {

                        GSBilling.getInstance ( ).setUnitMaxDemand ( "KW" );
//                        Structbilling.MD_UNIT_CD=String.valueOf(GSBilling.getInstance().getUnitMaxDemand());
                    }
                    if (tv_md_value.getText ( ).toString ( ) != null && !tv_md_value.getText ( ).toString ( ).isEmpty ( )) {

                        GSBilling.getInstance ( ).setMaxDemand ( Double.parseDouble ( tv_md_value.getText ( ).toString ( ) ) );
                        Structbilling.md_input = String.valueOf ( GSBilling.getInstance ( ).getMaxDemand ( ) );
                        Structbilling.MDI = (float) GSBilling.getInstance ( ).getMaxDemand ( );

                    } else {

                        if (Structtariff.Tariff_URBAN.equalsIgnoreCase ( "IND" ) || Structtariff.Tariff_RURAL.equalsIgnoreCase ( "IND R" )) {

                            new SweetAlertDialog ( Readinginput.this, SweetAlertDialog.SUCCESS_TYPE )
                                    .setTitleText ( "MD value not entered" )
                                    .setContentText ( "Are you sure to continue" )
                                    .setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation ( );

                                        }
                                    } )
                                    .show ( );

                        }

                        GSBilling.getInstance ( ).setMaxDemand ( Double.parseDouble ( "0" ) );
                        Structbilling.md_input = String.valueOf ( GSBilling.getInstance ( ).getMaxDemand ( ) );
                        Structbilling.MDI = (float) GSBilling.getInstance ( ).getMaxDemand ( );

                    }

                    if (etCR.getText ( ).toString ( ).trim ( ) != null && !etCR.getText ( ).toString ( ).trim ( ).isEmpty ( )) {

                        String finadate;
                        dbHelper = new DB ( getApplicationContext ( ) );
                        SD = dbHelper.getWritableDatabase ( );

                        curmeterreading = etCR.getText ( ).toString ( ).trim ( );
                        calBill = new CBillling ( );
                        Long dateDuration = null;
                        int calculatedunit = 0;

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

                            finadate = date1 + "20" + date2;
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
//                        System.out.println("CUR READ DATE :" + Structconsmas.CUR_READ_DATE);

                        //09071194894
                        switch (GSBilling.getInstance ( ).getCurmeter ( )) {

                            case 1:

                                calBill.curMeterStatus = 1;
                                calBill.derivedMeterStatus = "1";

                                Structbilling.Derived_mtr_status = "1";
                                Structbilling.Cur_Meter_Stat = 1;

                                calculatedunit = calBill.Unitcalculation ( Structbilling.Derived_mtr_status, curmeterreading, Structbilling.Cur_Meter_Stat );
                                calBill.unit = calculatedunit;
                                if (validate3PhData ( )) {


                                    calBill.CalculateBill ( );
                                    ucom.copyResultsetToBillingClass ( calBill );


                                    if (ucom.consump_CHK ( )) {
                                        new SweetAlertDialog ( Readinginput.this, SweetAlertDialog.WARNING_TYPE )

                                                .setTitleText ( "Billing error" )
                                                .setContentText ( "Consumption is very high" )
                                                .setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        sDialog.dismissWithAnimation ( );

                                                       /* UtilAppCommon ucom = new UtilAppCommon ( );
                                                        ucom.nullyfimodelCon ( );
                                                        ucom.nullyfimodelBill ( );
*/
                                                        Intent intent = new Intent ( Readinginput.this, BillingtypesActivity.class );
                                                        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                                        startActivity ( intent );
                                                        overridePendingTransition ( R.anim.anim_slide_in_left,
                                                                R.anim.anim_slide_out_left );
                                                    }
                                                } )
                                                .show ( );
                                    } else {
                                        Intent intent = new Intent ( Readinginput.this, BillingViewActivity.class );
                                        GSBilling.getInstance ( ).setNormalReason ( 1 );
                                        startActivity ( intent );
                                        overridePendingTransition ( R.anim.anim_slide_in_left,
                                                R.anim.anim_slide_out_left );
                                    }
                                } else {
                                    new SweetAlertDialog ( Readinginput.getRActivity ( ), SweetAlertDialog.WARNING_TYPE )
                                            .setTitleText ( "Billing ERROR" )
                                            .setContentText ( data_validate )
                                            .setConfirmText ( "OK!" )
                                            .show ( );
                                }
                                break;
                        }
                    } else {
                        etCR.setError ( "Current Reading cannot be empty" );
                    }
                } else {

                    System.out.println ( "This is the reading in second if" );
                    System.out.println ( "Current meter " + GSBilling.getInstance ( ).getCurmeter ( ) );

                    if (etCR.getText ( ).toString ( ).matches ( "" ) && (GSBilling.getInstance ( ).getCurmeter ( ) == 1)) {
                        etCR.setError ( "Current Reading cannot be empty" );
                    } else {

//                        if (checkDemandBasedValidation().equalsIgnoreCase("NOK") && flagCheck) {

                        GSBilling.getInstance ( ).setConsumptionchkhigh ( "  " );


                        if (etPF.getText ( ).toString ( ) != null && !etPF.getText ( ).toString ( ).isEmpty ( ) && !etPF.getText ( ).toString ( ).equalsIgnoreCase ( "0" ) && !etPF.getText ( ).toString ( ).equalsIgnoreCase ( "0.0" ) && !etPF.getText ( ).toString ( ).equalsIgnoreCase ( "0.00" )) {
                            GSBilling.getInstance ( ).setPowerFactor ( Double.valueOf ( etPF.getText ( ).toString ( ) ) );
                            Structbilling.Avrg_PF = String.valueOf ( GSBilling.getInstance ( ).getPowerFactor ( ) );
                        } else {
                            GSBilling.getInstance ( ).setPowerFactor ( Double.parseDouble ( "0.8" ) );
                            Structbilling.Avrg_PF = String.valueOf ( GSBilling.getInstance ( ).getPowerFactor ( ) );
                        }

                        if (!mdUnits.getSelectedItem ( ).toString ( ).equalsIgnoreCase ( "Select Unit" )) {
                            GSBilling.getInstance ( ).setUnitMaxDemand ( mdUnits.getSelectedItem ( ).toString ( ) );
                        } else {
                            GSBilling.getInstance ( ).setUnitMaxDemand ( "KW" );
                        }

                        if (tv_md_value.getText ( ).toString ( ) != null && !tv_md_value.getText ( ).toString ( ).isEmpty ( )) {
                            GSBilling.getInstance ( ).setMaxDemand ( Double.parseDouble ( tv_md_value.getText ( ).toString ( ) ) );
                            Structbilling.md_input = String.valueOf ( GSBilling.getInstance ( ).getMaxDemand ( ) );
                            Structbilling.MDI = (float) GSBilling.getInstance ( ).getMaxDemand ( );
                        } else {
                            GSBilling.getInstance ( ).setMaxDemand ( Double.parseDouble ( "0" ) );
                            Structbilling.md_input = String.valueOf ( GSBilling.getInstance ( ).getMaxDemand ( ) );
                            Structbilling.MDI = (float) GSBilling.getInstance ( ).getMaxDemand ( );
                        }

                        String finadate;
                        dbHelper = new DB ( getApplicationContext ( ) );
                        SD = dbHelper.getWritableDatabase ( );
                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        curmeterreading = etCR.getText ( ).toString ( ).trim ( );
                        calBill = new CBillling ( );
                        Long dateDuration = null;
                        int calculatedunit = 0;

                        Date varDate = null;
                        SimpleDateFormat sdf = new SimpleDateFormat ( "dd-MM-yyyy" );
                        String DTime = sdf.format ( new Date ( ) );
                        SimpleDateFormat dateFormat = new SimpleDateFormat ( "dd-MMM-yyyy" );
                        try {

                            if (Structbilling.dateDuration == 0l) {
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
                        System.out.println ( "CUR READ DATE :" + Structconsmas.CUR_READ_DATE );

                       /* switch (GSBilling.getInstance ( ).getCurmeter ( )) {

                            case 1: //NORMAL CASE


                                System.out.println ( "Normal 1" );
                                if (Double.parseDouble ( curmeterreading ) < Structconsmas.Prev_Meter_Reading) {

                                    showWarningDialog ( "Reading should be Greater than Previous Meter Reading! " );


                                    calBill.curMeterStatus = meterStatus;
                                    calBill.derivedMeterStatus = ""+meterStatus;

                                    Structbilling.Derived_mtr_status = ""+meterStatus;
                                    Structbilling.Cur_Meter_Stat = meterStatus;

                                    System.out.println ("This is the meter Status "+meterStatus );

                                    calculatedunit = calBill.Unitcalculation ( Structbilling.Derived_mtr_status, curmeterreading, Structbilling.Cur_Meter_Stat );
                                    calBill.unit = calculatedunit;



                                    // dialogBox("Reading should be Greater than Previous Meter Reading!");


                                } else {

                                    calBill.curMeterStatus = 1;
                                    calBill.derivedMeterStatus = "1";

                                    Structbilling.Derived_mtr_status = "1";
                                    Structbilling.Cur_Meter_Stat = 1;

                                    calculatedunit = calBill.Unitcalculation ( Structbilling.Derived_mtr_status, curmeterreading, Structbilling.Cur_Meter_Stat );
                                    calBill.unit = calculatedunit;

                                    if (validate3PhData ( )) {
                                        calBill.CalculateBill ( );

                                        ucom.copyResultsetToBillingClass ( calBill );
                                        *//*
                                            commented on 08/12/2017 for demo purpose
                                         *//*
                                        if (ucom.consump_CHK ( )) {

                                            showWarningDialog ( "Consumption is very high" );
                                            //dialogBox("Consumption is very high");
                                        } else {


                                            // Intent intent = new Intent(Readinginput.this, ReasonActivity.class);
                                            Intent intent = new Intent ( Readinginput.this, BillingViewActivity.class );

                                            GSBilling.getInstance ( ).setNormalReason ( 1 );
                                            startActivity ( intent );
                                            overridePendingTransition ( R.anim.anim_slide_in_left,
                                                    R.anim.anim_slide_out_left );
//                                            }
                                        }
                                    } else {
                                        new SweetAlertDialog ( Readinginput.getRActivity ( ), SweetAlertDialog.WARNING_TYPE )
                                                .setTitleText ( "Billing ERROR" )
                                                .setContentText ( data_validate )
                                                .setConfirmText ( "OK!" )
                                                .show ( );
                                    }
                                }
                                break;
                            case 4://ASSESSED UNIT CASE
                            {
                                System.out.println ( "Assessed 1" );
                                calBill.curMeterStatus = 4;
                                calBill.derivedMeterStatus = "4";

                                Structbilling.Derived_mtr_status = "4";
                                Structbilling.Cur_Meter_Stat = 4;

                                calculatedunit = calBill.Unitcalculation ( Structbilling.Derived_mtr_status, curmeterreading, Structbilling.Cur_Meter_Stat );
                                calBill.unit = calculatedunit;

                                if (validate3PhData ( )) {

                                    calBill.CalculateBill ( );
                                    ucom.copyResultsetToBillingClass ( calBill );

                                    if (ucom.consump_CHK ( )) {
                                        showWarningDialog ( "Consumption is very high" );
                                        // dialogBox("Consumption is very high");
                                    } else {
                                        Intent intent = new Intent ( Readinginput.this, BillingViewActivity.class );

                                        GSBilling.getInstance ( ).setNormalReason ( 1 );
                                        startActivity ( intent );
                                        overridePendingTransition ( R.anim.anim_slide_in_left,
                                                R.anim.anim_slide_out_left );
                                    }
                                } else {

                                    new SweetAlertDialog ( Readinginput.getRActivity ( ), SweetAlertDialog.WARNING_TYPE )
                                            .setTitleText ( "Billing ERROR" )
                                            .setContentText ( data_validate )
                                            .setConfirmText ( "OK!" )
                                            .show ( );
                                }

                            }
                            break;
                            case 9://METER CHANGED  CASE
                            {
                                System.out.println ( "Meter Change 1" );
                                if (GSBilling.getInstance ( ).getMeterChange ( ).equalsIgnoreCase ( "DBCHNG" )) {

                                    if (Double.parseDouble ( curmeterreading ) < Structconsmas.Prev_Meter_Reading) {
                                        dialogBox ( "Reading should be Greater than Previous Meter Reading!" );
                                    } else {
                                        calBill.curMeterStatus = 9;
                                        calBill.derivedMeterStatus = "9";
                                        Structbilling.Derived_mtr_status = "9";
                                        Structbilling.Cur_Meter_Stat = 9;
                                        calculatedunit = calBill.Unitcalculation ( Structbilling.Derived_mtr_status, curmeterreading, Structbilling.Cur_Meter_Stat );
                                        calBill.unit = calculatedunit;
//
                                        if (validate3PhData ( )) {

                                            calBill.CalculateBill ( );

                                            ucom.copyResultsetToBillingClass ( calBill );

                                            if (ucom.consump_CHK ( )) {
                                                showWarningDialog ( "Consumption is very high" );
                                                // dialogBox("Consumption is very high");
                                            } else {
                                                Intent intent = new Intent ( Readinginput.this, BillingViewActivity.class );

                                                GSBilling.getInstance ( ).setNormalReason ( 1 );
                                                startActivity ( intent );
                                                overridePendingTransition ( R.anim.anim_slide_in_left,
                                                        R.anim.anim_slide_out_left );
                                            }
                                        } else {
                                            new SweetAlertDialog ( Readinginput.getRActivity ( ), SweetAlertDialog.WARNING_TYPE )
                                                    .setTitleText ( "Billing ERROR" )
                                                    .setContentText ( data_validate )
                                                    .setConfirmText ( "OK!" )
                                                    .show ( );
                                        }
                                    }
//                                }
                                } else {
                                    calBill.curMeterStatus = 9;
                                    calBill.derivedMeterStatus = "9";
//                                    Structbilling.Derived_mtr_status = "9";
                                    Structbilling.Derived_mtr_status = "9";
                                    Structbilling.Cur_Meter_Stat = 9;

                                    calculatedunit = calBill.Unitcalculation ( Structbilling.Derived_mtr_status, curmeterreading, Structbilling.Cur_Meter_Stat );
                                    calBill.unit = calculatedunit;

                                    if (validate3PhData ( )) {
                                        calBill.CalculateBill ( );

                                        ucom.copyResultsetToBillingClass ( calBill );

                                        if (ucom.consump_CHK ( )) {
                                            showWarningDialog ( "Consumption is very high" );
                                            // dialogBox("Consumption is very high");
                                        } else {
                                            Intent intent = new Intent ( Readinginput.this, BillingViewActivity.class );

                                            GSBilling.getInstance ( ).setNormalReason ( 1 );
                                            startActivity ( intent );
                                            overridePendingTransition ( R.anim.anim_slide_in_left,
                                                    R.anim.anim_slide_out_left );
                                        }
                                    } else {
                                        new SweetAlertDialog ( Readinginput.getRActivity ( ), SweetAlertDialog.WARNING_TYPE )
                                                .setTitleText ( "Billing ERROR" )
                                                .setContentText ( data_validate )
                                                .setConfirmText ( "OK!" )
                                                .show ( );
                                    }
                                }
                            }
                            break;
                        }*/


//                        else if (checkDemandBasedValidation().equalsIgnoreCase("NOK") && !flagCheck) {
                        /****DIALOG BOX INITIALIZATION PROVIDING A LAYOUT TO DIALOUGE****/
//                            final Dialog dialogAccount = new Dialog(Readinginput.this, R.style.Theme_AppCompat_Dialog);
//
//                            dialogAccount.setContentView(R.layout.custom_dialoge_warning_demand);
//                            dialogAccount.setTitle("Demand based consumer");
//
//                            // set the custom dialog components - text, image and button
//
//                            Button dialogButton = (Button) dialogAccount.findViewById(R.id.dialogButtonACCOK);
//                            GSBilling.getInstance().setConsumptionchkhigh("  ");
//                            flagCheck=true;
//                            // if button is clicked, close the custom dialog
//                            /****DIALOUGE BOX BUTTON INTIALISATION AND EVENT ADDING****/
//                            dialogButton.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    //---------------
//                                    dialogAccount.dismiss();
//                                    flagCheck=true;
//                                }
//                            });
//
//                            dialogAccount.setOnKeyListener(new Dialog.OnKeyListener() {
//
//                                @Override
//                                public boolean onKey(DialogInterface arg0, int keyCode,
//                                                     KeyEvent event) {
//                                    // TODO Auto-generated method stub
//                                    dialogAccount.dismiss();
//                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
//
//                                        dialogAccount.dismiss();
//                                    }
//                                    return true;
//                                }
//
//                            });
//                            dialogAccount.show();

//                        }
//                        else {
                        if (etPF.getText ( ).toString ( ) != null && !etPF.getText ( ).toString ( ).isEmpty ( ) && !etPF.getText ( ).toString ( ).equalsIgnoreCase ( "0" ) && !etPF.getText ( ).toString ( ).equalsIgnoreCase ( "0.0" ) && !etPF.getText ( ).toString ( ).equalsIgnoreCase ( "0.00" )) {
                            GSBilling.getInstance ( ).setPowerFactor ( Double.valueOf ( etPF.getText ( ).toString ( ) ) );
                            Structbilling.Avrg_PF = String.valueOf ( GSBilling.getInstance ( ).getPowerFactor ( ) );
                        } else {
                            GSBilling.getInstance ( ).setPowerFactor ( Double.parseDouble ( "0.8" ) );
                            Structbilling.Avrg_PF = String.valueOf ( GSBilling.getInstance ( ).getPowerFactor ( ) );
                        }
                        if (!mdUnits.getSelectedItem ( ).toString ( ).equalsIgnoreCase ( "Select Unit" )) {
                            GSBilling.getInstance ( ).setUnitMaxDemand ( mdUnits.getSelectedItem ( ).toString ( ) );
                        } else {
                            GSBilling.getInstance ( ).setUnitMaxDemand ( "KW" );
                        }
                        if (tv_md_value.getText ( ).toString ( ) != null && !tv_md_value.getText ( ).toString ( ).isEmpty ( )) {
                            GSBilling.getInstance ( ).setMaxDemand ( Double.parseDouble ( tv_md_value.getText ( ).toString ( ) ) );
                            Structbilling.md_input = String.valueOf ( GSBilling.getInstance ( ).getMaxDemand ( ) );
                            Structbilling.MDI = (float) GSBilling.getInstance ( ).getMaxDemand ( );
                        } else {
                            GSBilling.getInstance ( ).setMaxDemand ( Double.parseDouble ( "0" ) );
                            Structbilling.md_input = String.valueOf ( GSBilling.getInstance ( ).getMaxDemand ( ) );
                            Structbilling.MDI = (float) GSBilling.getInstance ( ).getMaxDemand ( );
                        }
                        String finadatee;
                        dbHelper = new DB ( getApplicationContext ( ) );
                        SD = dbHelper.getWritableDatabase ( );
                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        curmeterreading = etCR.getText ( ).toString ( ).trim ( );
                        calBill = new CBillling ( );
                        Long dateDurationn = null;
                        int calculatedunitt = 0;

                        Date varDatee = null;
                        SimpleDateFormat sdff = new SimpleDateFormat ( "dd-MM-yyyy" );
                        String DTimee = sdf.format ( new Date ( ) );
                        SimpleDateFormat dateFormatt = new SimpleDateFormat ( "dd-MMM-yyyy" );
                        try {

                            if (Structbilling.dateDuration == 0l) {
                                Structbilling.dateDuration = 30l;
                            }
                            dateDurationn = Structbilling.dateDuration;
                            calBill.totalDateduration = dateDuration;
                            System.out.println ( "DATE DURATION  :" + calBill.totalDateduration );
                            System.out.println ( "DATE DURATION 2  :" + dateDuration );
                        } catch (Exception e) {
                            // TODO: handle exception
                            e.printStackTrace ( );
                        }

                        calBill.curMeterRead = curmeterreading;
                        calBill.curMeterReadDate = DTime;
                        System.out.println ( "CUR READ DATE :" + Structconsmas.CUR_READ_DATE );
                        System.out.println ( "This is the readinf " + GSBilling.getInstance ( ).getCurmeter ( ) );

                        switch (GSBilling.getInstance ( ).getCurmeter ( )) {

                            case 1: //NORMAL CASE


                                System.out.println ( "Normal 2" );
                                if (Double.parseDouble ( curmeterreading ) < Structconsmas.Prev_Meter_Reading) {

                                    showWarningDialog ( "Reading should be Greater than Previous Meter Reading!" );

                                    calBill.curMeterStatus = meterStatus;
                                    calBill.derivedMeterStatus = "" + meterStatus;


                                    Structbilling.Derived_mtr_status = "" + meterStatus;
                                    Structbilling.Cur_Meter_Stat = meterStatus;

                                    System.out.println ( "This is the meter Status " + meterStatus );


                                    calculatedunit = calBill.Unitcalculation ( Structbilling.Derived_mtr_status, curmeterreading, Structbilling.Cur_Meter_Stat );
                                    calBill.unit = calculatedunit;


                                    System.out.println ( "This is the date getting " + Structconsmas.CUR_READ_DATE );

                                    calBill.CalculateBill ( );
                                    ucom.copyResultsetToBillingClass ( calBill );

                                    //  dialogBox("Reading should be Greater than Previous Meter Reading!");

                                } else {
                                    calBill.curMeterStatus = 1;
                                    calBill.derivedMeterStatus = "1";

                                    Structbilling.Derived_mtr_status = "1";
                                    Structbilling.Cur_Meter_Stat = 1;

                                    calculatedunit = calBill.Unitcalculation ( Structbilling.Derived_mtr_status, curmeterreading, Structbilling.Cur_Meter_Stat );
                                    calBill.unit = calculatedunit;

                                    if (validate3PhData ( )) {
                                        calBill.CalculateBill ( );
                                        ucom.copyResultsetToBillingClass ( calBill );

                                        if (ucom.consump_CHK ( )) {
                                            showWarningDialog ( "Consumption is very high" );
                                            // dialogBox("Consumption is very high");
                                        } else {
                                            Intent intent = new Intent ( Readinginput.this, BillingViewActivity.class );

                                            GSBilling.getInstance ( ).setNormalReason ( 1 );
                                            startActivity ( intent );
                                            overridePendingTransition ( R.anim.anim_slide_in_left,
                                                    R.anim.anim_slide_out_left );
                                        }
                                    } else {
                                        new SweetAlertDialog ( Readinginput.getRActivity ( ), SweetAlertDialog.WARNING_TYPE )
                                                .setTitleText ( "Billing ERROR" )
                                                .setContentText ( data_validate )
                                                .setConfirmText ( "OK!" )
                                                .show ( );
                                    }
                                }
                                break;


                            case 2://ASSESSED UNIT CASE
                            {

                                System.out.println ( "Assessed 2" );
                                calBill.curMeterStatus = 2;
                                calBill.derivedMeterStatus = "2";

                                Structbilling.Derived_mtr_status = "2";
                                Structbilling.Cur_Meter_Stat = 2;

                                // calculatedunit = calBill.Unitcalculation ( Structbilling.Derived_mtr_status, curmeterreading, Structbilling.Cur_Meter_Stat );
                                // calBill.unit = calculatedunit;
//                                if(higherConsumptionChk(calculatedunit)){
//                                    new SweetAlertDialog(Readinginput.getRActivity(), SweetAlertDialog.WARNING_TYPE)
//                                            .setTitleText("Billing ERROR")
//                                            .setContentText("Unit calculated is way too high!")
//                                            .setConfirmText("OK!")
//                                            .show();
//                                }else{
                                //  if (validate3PhData ( )) {

                                //      calBill.CalculateBill ( );
                                ucom.copyResultsetToBillingClass ( calBill );

                                //    if (ucom.consump_CHK ( )) {
                                //      showWarningDialog ( "Consumption is very high" );
                                // dialogBox("Consumption is very high");
                                //} else {
                                Intent intent = new Intent ( Readinginput.this, BillingViewActivity.class );

                                GSBilling.getInstance ( ).setNormalReason ( 1 );
                                startActivity ( intent );
                                overridePendingTransition ( R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left );
                                //}
                                /*} else {

                                    new SweetAlertDialog ( Readinginput.getRActivity ( ), SweetAlertDialog.WARNING_TYPE )
                                            .setTitleText ( "Billing ERROR" )
                                            .setContentText ( data_validate )
                                            .setConfirmText ( "OK!" )
                                            .show ( );
                                }*/
//                                }

                            }
                            case 3://ASSESSED UNIT CASE
                            {

                                System.out.println ( "Assessed 2" );
                                calBill.curMeterStatus = 3;
                                calBill.derivedMeterStatus = "3";

                                Structbilling.Derived_mtr_status = "3";
                                Structbilling.Cur_Meter_Stat = 3;

                                // calculatedunit = calBill.Unitcalculation ( Structbilling.Derived_mtr_status, curmeterreading, Structbilling.Cur_Meter_Stat );
                                // calBill.unit = calculatedunit;
//                                if(higherConsumptionChk(calculatedunit)){
//                                    new SweetAlertDialog(Readinginput.getRActivity(), SweetAlertDialog.WARNING_TYPE)
//                                            .setTitleText("Billing ERROR")
//                                            .setContentText("Unit calculated is way too high!")
//                                            .setConfirmText("OK!")
//                                            .show();
//                                }else{
                                //  if (validate3PhData ( )) {

                                //      calBill.CalculateBill ( );
                                ucom.copyResultsetToBillingClass ( calBill );

                                //    if (ucom.consump_CHK ( )) {
                                //      showWarningDialog ( "Consumption is very high" );
                                // dialogBox("Consumption is very high");
                                //} else {
                                Intent intent = new Intent ( Readinginput.this, BillingViewActivity.class );

                                GSBilling.getInstance ( ).setNormalReason ( 1 );
                                startActivity ( intent );
                                overridePendingTransition ( R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left );
                                //}
                                /*} else {

                                    new SweetAlertDialog ( Readinginput.getRActivity ( ), SweetAlertDialog.WARNING_TYPE )
                                            .setTitleText ( "Billing ERROR" )
                                            .setContentText ( data_validate )
                                            .setConfirmText ( "OK!" )
                                            .show ( );
                                }*/
//                                }

                            }
                            break;
                            case 4://ASSESSED UNIT CASE
                            {

                                System.out.println ( "Assessed 2" );
                                calBill.curMeterStatus = 4;
                                calBill.derivedMeterStatus = "4";

                                Structbilling.Derived_mtr_status = "4";
                                Structbilling.Cur_Meter_Stat = 4;

                                //  calculatedunit = calBill.Unitcalculation ( Structbilling.Derived_mtr_status, curmeterreading, Structbilling.Cur_Meter_Stat );
                                //calBill.unit = calculatedunit;
//                                if(higherConsumptionChk(calculatedunit)){
//                                    new SweetAlertDialog(Readinginput.getRActivity(), SweetAlertDialog.WARNING_TYPE)
//                                            .setTitleText("Billing ERROR")
//                                            .setContentText("Unit calculated is way too high!")
//                                            .setConfirmText("OK!")
//                                            .show();
//                                }else{
                                // if (validate3PhData ( )) {

                                //   calBill.CalculateBill ( );
                                ucom.copyResultsetToBillingClass ( calBill );

                                // if (ucom.consump_CHK ( )) {
                                //   showWarningDialog ( "Consumption is very high" );
                                // dialogBox("Consumption is very high");
                                //} else {
                                Intent intent = new Intent ( Readinginput.this, BillingViewActivity.class );

                                GSBilling.getInstance ( ).setNormalReason ( 1 );
                                startActivity ( intent );
                                overridePendingTransition ( R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left );
                                //}
                                /*} else {

                                    new SweetAlertDialog ( Readinginput.getRActivity ( ), SweetAlertDialog.WARNING_TYPE )
                                            .setTitleText ( "Billing ERROR" )
                                            .setContentText ( data_validate )
                                            .setConfirmText ( "OK!" )
                                            .show ( );
                                }*/
//                                }

                            }
                            break;

                            case 5://ASSESSED UNIT CASE
                            {

                                System.out.println ( "Assessed 2" );
                                calBill.curMeterStatus = 5;
                                calBill.derivedMeterStatus = "5";

                                Structbilling.Derived_mtr_status = "5";
                                Structbilling.Cur_Meter_Stat = 5;

                                // calculatedunit = calBill.Unitcalculation ( Structbilling.Derived_mtr_status, curmeterreading, Structbilling.Cur_Meter_Stat );
                                // calBill.unit = calculatedunit;
//                                if(higherConsumptionChk(calculatedunit)){
//                                    new SweetAlertDialog(Readinginput.getRActivity(), SweetAlertDialog.WARNING_TYPE)
//                                            .setTitleText("Billing ERROR")
//                                            .setContentText("Unit calculated is way too high!")
//                                            .setConfirmText("OK!")
//                                            .show();
//                                }else{
                                //  if (validate3PhData ( )) {

                                //      calBill.CalculateBill ( );
                                ucom.copyResultsetToBillingClass ( calBill );

                                //    if (ucom.consump_CHK ( )) {
                                //      showWarningDialog ( "Consumption is very high" );
                                // dialogBox("Consumption is very high");
                                //} else {
                                Intent intent = new Intent ( Readinginput.this, BillingViewActivity.class );

                                GSBilling.getInstance ( ).setNormalReason ( 1 );
                                startActivity ( intent );
                                overridePendingTransition ( R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left );
                                //}
                                /*} else {

                                    new SweetAlertDialog ( Readinginput.getRActivity ( ), SweetAlertDialog.WARNING_TYPE )
                                            .setTitleText ( "Billing ERROR" )
                                            .setContentText ( data_validate )
                                            .setConfirmText ( "OK!" )
                                            .show ( );
                                }*/
//                                }

                            }
                            case 6://ASSESSED UNIT CASE
                            {

                                System.out.println ( "Assessed 2" );
                                calBill.curMeterStatus = 6;
                                calBill.derivedMeterStatus = "6";

                                Structbilling.Derived_mtr_status = "6";
                                Structbilling.Cur_Meter_Stat = 6;

                                // calculatedunit = calBill.Unitcalculation ( Structbilling.Derived_mtr_status, curmeterreading, Structbilling.Cur_Meter_Stat );
                                // calBill.unit = calculatedunit;
//                                if(higherConsumptionChk(calculatedunit)){
//                                    new SweetAlertDialog(Readinginput.getRActivity(), SweetAlertDialog.WARNING_TYPE)
//                                            .setTitleText("Billing ERROR")
//                                            .setContentText("Unit calculated is way too high!")
//                                            .setConfirmText("OK!")
//                                            .show();
//                                }else{
                                //  if (validate3PhData ( )) {

                                //      calBill.CalculateBill ( );
                                ucom.copyResultsetToBillingClass ( calBill );

                                //    if (ucom.consump_CHK ( )) {
                                //      showWarningDialog ( "Consumption is very high" );
                                // dialogBox("Consumption is very high");
                                //} else {
                                Intent intent = new Intent ( Readinginput.this, BillingViewActivity.class );

                                GSBilling.getInstance ( ).setNormalReason ( 1 );
                                startActivity ( intent );
                                overridePendingTransition ( R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left );
                                //}
                                /*} else {

                                    new SweetAlertDialog ( Readinginput.getRActivity ( ), SweetAlertDialog.WARNING_TYPE )
                                            .setTitleText ( "Billing ERROR" )
                                            .setContentText ( data_validate )
                                            .setConfirmText ( "OK!" )
                                            .show ( );
                                }*/
//                                }

                            }
                            case 7://ASSESSED UNIT CASE
                            {

                                System.out.println ( "Assessed 2" );
                                calBill.curMeterStatus = 7;
                                calBill.derivedMeterStatus = "7";

                                Structbilling.Derived_mtr_status = "7";
                                Structbilling.Cur_Meter_Stat = 7;

                                // calculatedunit = calBill.Unitcalculation ( Structbilling.Derived_mtr_status, curmeterreading, Structbilling.Cur_Meter_Stat );
                                // calBill.unit = calculatedunit;
//                                if(higherConsumptionChk(calculatedunit)){
//                                    new SweetAlertDialog(Readinginput.getRActivity(), SweetAlertDialog.WARNING_TYPE)
//                                            .setTitleText("Billing ERROR")
//                                            .setContentText("Unit calculated is way too high!")
//                                            .setConfirmText("OK!")
//                                            .show();
//                                }else{
                                //  if (validate3PhData ( )) {

                                //      calBill.CalculateBill ( );
                                ucom.copyResultsetToBillingClass ( calBill );

                                //    if (ucom.consump_CHK ( )) {
                                //      showWarningDialog ( "Consumption is very high" );
                                // dialogBox("Consumption is very high");
                                //} else {
                                Intent intent = new Intent ( Readinginput.this, BillingViewActivity.class );

                                GSBilling.getInstance ( ).setNormalReason ( 1 );
                                startActivity ( intent );
                                overridePendingTransition ( R.anim.anim_slide_in_left,
                                        R.anim.anim_slide_out_left );
                                //}
                                /*} else {

                                    new SweetAlertDialog ( Readinginput.getRActivity ( ), SweetAlertDialog.WARNING_TYPE )
                                            .setTitleText ( "Billing ERROR" )
                                            .setContentText ( data_validate )
                                            .setConfirmText ( "OK!" )
                                            .show ( );
                                }*/
//                                }

                            }
                            /*case 9://METER CHANGED  CASE
                            {

                                System.out.println ( "Meter change 2" );
                                if (GSBilling.getInstance ( ).getMeterChange ( ).equalsIgnoreCase ( "DBCHNG" )) {

                                    if (Double.parseDouble ( curmeterreading ) < Structconsmas.Prev_Meter_Reading) {
                                        dialogBox ( "Reading should be Greater than Previous Meter Reading!" );
                                    } else {
                                        calBill.curMeterStatus = 9;
                                        calBill.derivedMeterStatus = "9";
                                        Structbilling.Derived_mtr_status = "9";
                                        Structbilling.Cur_Meter_Stat = 9;
                                        calculatedunit = calBill.Unitcalculation ( Structbilling.Derived_mtr_status, curmeterreading, Structbilling.Cur_Meter_Stat );
                                        calBill.unit = calculatedunit;
                                        if (validate3PhData ( )) {

                                            calBill.CalculateBill ( );

                                            ucom.copyResultsetToBillingClass ( calBill );

                                            if (ucom.consump_CHK ( )) {
                                                showWarningDialog ( "Consumption is very high" );
                                                // dialogBox("Consumption is very high");
                                            } else {
                                                Intent intent = new Intent ( Readinginput.this, BillingViewActivity.class );

                                                GSBilling.getInstance ( ).setNormalReason ( 1 );
                                                startActivity ( intent );
                                                overridePendingTransition ( R.anim.anim_slide_in_left,
                                                        R.anim.anim_slide_out_left );
                                            }
                                        } else {
                                            dialogBox ( "OK!" );
                                        }
                                    }
//                                }
                                } else {
                                    calBill.curMeterStatus = 9;
                                    calBill.derivedMeterStatus = "9";
                                    ;
                                    Structbilling.Derived_mtr_status = "9";
                                    Structbilling.Cur_Meter_Stat = 9;
                                    calculatedunit = calBill.Unitcalculation ( Structbilling.Derived_mtr_status, curmeterreading, Structbilling.Cur_Meter_Stat );
                                    calBill.unit = calculatedunit;
                                    if (validate3PhData ( )) {
                                        calBill.CalculateBill ( );

                                        ucom.copyResultsetToBillingClass ( calBill );

                                        if (ucom.consump_CHK ( )) {
                                            showWarningDialog ( "Consumption is very high" );
                                            // dialogBox("Consumption is very high");
                                        } else {
                                            Intent intent = new Intent ( Readinginput.this, BillingViewActivity.class );

                                            GSBilling.getInstance ( ).setNormalReason ( 1 );
                                            startActivity ( intent );
                                            overridePendingTransition ( R.anim.anim_slide_in_left,
                                                    R.anim.anim_slide_out_left );
                                        }
                                    } else {
                                        dialogBox ( "OK!" );
                                    }
                                }
                            }
                            System.out.println ( "This is reading " + GSBilling.getInstance ( ).getCurmeter ( ) );

                            break;*/
                        }
                    }
                }
            }

//
        } );


        sp_reasons.setOnItemSelectedListener ( new AdapterView.OnItemSelectedListener ( ) {
            @Override
            public void onItemSelected(AdapterView <?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub


                System.out.println ( "This is the reason " + parent.getItemAtPosition ( position ) );

                //   GSBilling.getInstance ( )

                Structbilling.Reasons = (String) parent.getItemAtPosition ( position );





               /* reason = metermake_list.get ( position );
                reason_id = metermakeid_list.get ( position );


//                if (reason.equalsIgnoreCase("Previous reading is higher than Current reading")) {
//                    Structbilling.Reasons = reason;
//
//                    if (et_act_reading.getText().toString() != null && !et_act_reading.getText().toString().isEmpty()) {
//                        Structbilling.ACTUAL_READING = et_act_reading.getText().toString();
//                    }
//
//
//
//
//                } else
                if (position > 0) {
                    Structbilling.Reasons = reason;

                    *//*if (et_act_reading.getText().toString() != null && !et_act_reading.getText().toString().isEmpty() || et_meter_no.getText().toString() != null && !et_meter_no.getText().toString().isEmpty() ) {
                        Structbilling.ACTUAL_READING = et_act_reading.getText().toString();
                        Structbilling.MTR_NO= et_meter_no.getText().toString();
                    }
*//*
                    if (reason.equalsIgnoreCase ( "Previous reading is higher than Current reading" )) {
                        GSBilling.getInstance ( ).setPowerFactor ( Double.parseDouble ( "0" ) );
                        GSBilling.getInstance ( ).setUnitMaxDemand ( "KW" );
                        GSBilling.getInstance ( ).setPowerFactor ( Double.parseDouble ( "0.8" ) );
                        Structbilling.Avrg_PF = String.valueOf ( GSBilling.getInstance ( ).getPowerFactor ( ) );

                        GSBilling.getInstance ( ).setCurmeter ( 4 );

                        databaseHelper = new DB ( getApplicationContext ( ) );
                        SD = databaseHelper.getWritableDatabase ( );
                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        appCom = new UtilAppCommon ( );
                        String curmeterreading = "0";
                        calBill = new CBillling ( );
                        Long dateDuration = null;
                        int calculatedunit = 0;

                        Date varDate = null;
                        SimpleDateFormat sdf = new SimpleDateFormat ( "dd-MM-yyyy" );
                        String DTime = sdf.format ( new Date ( ) );
                        SimpleDateFormat dateFormat = new SimpleDateFormat ( "dd-MMM-yyyy" );
                        try {
                            varDate = dateFormat.parse ( Structconsmas.Prev_Meter_Reading_Date );

                            String billISDate = Structconsmas.BILL_ISSUE_DATE.substring ( 0, 4 );
                            String billISDate2 = Structconsmas.BILL_ISSUE_DATE.substring ( 4, 6 );
                            String billISDate3 = Structconsmas.BILL_ISSUE_DATE.substring ( 6, 8 );

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
                        // System.out.println("CUR READ DATE :" + Structconsmas.CUR_READ_DATE);

                        calBill.curMeterStatus = 4;
                        calBill.derivedMeterStatus = "4";

                        Structbilling.Derived_mtr_status = "2";
                        Structbilling.Cur_Meter_Stat = 4;

                        calculatedunit = calBill.Unitcalculation ( Structbilling.Derived_mtr_status, curmeterreading, Structbilling.Cur_Meter_Stat );
                        calBill.unit = calculatedunit;
                        calBill.CalculateBill ( );

                        appCom.copyResultsetToBillingClass ( calBill );

                        if (appCom.consump_CHK ( )) {
                            new SweetAlertDialog ( Readinginput.this, SweetAlertDialog.WARNING_TYPE )

                                    .setTitleText ( "Billing error" )
                                    .setContentText ( "Consumption is very high" )
                                    .setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation ( );

                                            UtilAppCommon ucom = new UtilAppCommon ( );
                                            ucom.nullyfimodelCon ( );
                                            ucom.nullyfimodelBill ( );

//                                            Toast.makeText(ReasonActivity.this, "HY", Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(MeterState.this, BillingtypesActivity.class);
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                        startActivity(intent);
//                                        overridePendingTransition(R.anim.anim_slide_in_left,
//                                                R.anim.anim_slide_out_left);

                                        }
                                    } )
                                    .show ( );
                        } else {

                            Structbilling.Bill_Basis = "R";
                            Structbilling.BILL_TYP_CD = "4";

                            Structbilling.MTR_STAT_TYP = "56";
                            Structbilling.RDG_TYP_CD = "1";

                            Intent intent = new Intent ( );
                            intent.setComponent ( new ComponentName ( getApplicationContext ( ), BillingViewActivity
                                    .class ) );
                            startActivity ( intent );
                            overridePendingTransition ( R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left );

                        }
                    } *//*else {

                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(getApplicationContext(), BillingViewActivity
                                .class));
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left);

                    }*//*


                }
*/

            }

            @Override
            public void onNothingSelected(AdapterView <?> parent) {
                // TODO Auto-generated method stub
            }
        } );


   /*     tv_readingDate.setText ( Structbilling.Bill_Date );
        System.out.println ("This is mama Date "+Structbilling.Bill_Date );
  */

    }

    public void run() {
        Process.setThreadPriority ( Process.THREAD_PRIORITY_BACKGROUND );
    }

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;

        if (contentsToEncode == null) {
            return null;
        }

        Map <EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding ( contentsToEncode );

        if (encoding != null) {
            hints = new EnumMap <EncodeHintType, Object> ( EncodeHintType.class );
            hints.put ( EncodeHintType.CHARACTER_SET, encoding );
        }

        MultiFormatWriter writer = new MultiFormatWriter ( );
        BitMatrix result;

        try {
            result = writer.encode ( contentsToEncode, format, img_width, img_height, hints );
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }

        int width = result.getWidth ( );
        int height = result.getHeight ( );
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get ( x, y ) ? BLACK : WHITE;
            }
        }

        /*//16 bit bitmap creation
         * Bitmap bitmap16bit = Bitmap.createBitmap(80, 60, Bitmap.Config.RGB_565);
         **/
        Bitmap bitmap = Bitmap.createBitmap ( width, height,
                Bitmap.Config.ARGB_8888 );

        bitmap.setPixels ( pixels, 0, width, 0, 0, width, height );
        SaveImage ( bitmap );
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length ( ); i++) {
            if (contents.charAt ( i ) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory ( ).toString ( );
        File myDir = new File ( root + "/MBC/Images" );
        if (myDir.exists ( )) {

        } else {
            myDir.mkdirs ( );
        }

        Random generator = new Random ( );
        int n = 10000;
        n = generator.nextInt ( n );
        String fname = "Image.bmp";
        File file = new File ( myDir, fname );
        if (file.exists ( )) file.delete ( );
        try {
            FileOutputStream out = new FileOutputStream ( file );
            finalBitmap.compress ( Bitmap.CompressFormat.JPEG, 90, out );
            out.flush ( );
            out.close ( );
            Toast.makeText ( getApplicationContext ( ), "image created", Toast.LENGTH_LONG ).show ( );
        } catch (Exception e) {
            e.printStackTrace ( );
        }
    }

    public boolean validate3PhData() {

        String ConsumptionHigh = higherConsumptionChk ( );
        String LoadHigh = higherLoadChk ( );
        String MDHigh = higherMDChk ( );

        if (ConsumptionHigh.equalsIgnoreCase ( "0" ) && higherLoadChk ( ).equalsIgnoreCase ( "0" ) && higherMDChk ( ).equalsIgnoreCase ( "0" )) {
            data_validate = "";
            return true;
        } else if (ConsumptionHigh.equalsIgnoreCase ( "0" ) && !higherLoadChk ( ).equalsIgnoreCase ( "0" ) && higherMDChk ( ).equalsIgnoreCase ( "0" )) {
            data_validate = LoadHigh;
            return false;
        } else if (!ConsumptionHigh.equalsIgnoreCase ( "0" ) && higherLoadChk ( ).equalsIgnoreCase ( "0" ) && higherMDChk ( ).equalsIgnoreCase ( "0" )) {
            data_validate = ConsumptionHigh;
            return false;
        } else if (ConsumptionHigh.equalsIgnoreCase ( "0" ) && higherLoadChk ( ).equalsIgnoreCase ( "0" ) && !higherMDChk ( ).equalsIgnoreCase ( "0" )) {
            data_validate = MDHigh;
            return false;
        } else if (!ConsumptionHigh.equalsIgnoreCase ( "0" ) && !higherLoadChk ( ).equalsIgnoreCase ( "0" ) && higherMDChk ( ).equalsIgnoreCase ( "0" )) {
            data_validate = ConsumptionHigh + "\n" + LoadHigh;
            return false;
        } else if (!ConsumptionHigh.equalsIgnoreCase ( "0" ) && higherLoadChk ( ).equalsIgnoreCase ( "0" ) && !higherMDChk ( ).equalsIgnoreCase ( "0" )) {
            data_validate = ConsumptionHigh + "\n" + MDHigh;
            return false;
        } else if (ConsumptionHigh.equalsIgnoreCase ( "0" ) && !higherLoadChk ( ).equalsIgnoreCase ( "0" ) && !higherMDChk ( ).equalsIgnoreCase ( "0" )) {
            data_validate = LoadHigh + "\n" + MDHigh;
            return false;
        } else if (!ConsumptionHigh.equalsIgnoreCase ( "0" ) && !higherLoadChk ( ).equalsIgnoreCase ( "0" ) && !higherMDChk ( ).equalsIgnoreCase ( "0" )) {
            data_validate = ConsumptionHigh + "\n" + LoadHigh + "\n" + MDHigh;
            return false;
        }
        return true;
    }

    public boolean validateConsumptionHigh() {

        String Consumption2 = consumption2times ( );
        String Consumption3 = consumption3times ( );

        if (Consumption2.equalsIgnoreCase ( "0" ) && Consumption3.equalsIgnoreCase ( "0" )) {
            data_validate = "";
            return true;
        } else if (Consumption2.equalsIgnoreCase ( "0" ) && !Consumption3.equalsIgnoreCase ( "0" )) {
            data_validate = Consumption3;
            return false;
        } else if (!Consumption2.equalsIgnoreCase ( "0" ) && Consumption3.equalsIgnoreCase ( "0" )) {
            data_validate = Consumption2;
            return false;
        } else if (!Consumption2.equalsIgnoreCase ( "0" ) && !Consumption3.equalsIgnoreCase ( "0" )) {
            data_validate = Consumption2 + Consumption3;
            return false;
        }

        return true;
    }

    private String higherConsumptionChk() {
        int unit_calculated = calBill.unit;
//        if (unit_calculated > 10000 && (Structbilling.RDG_TYP_CD != "3" || Structbilling.RDG_TYP_CD != "4")) {
//            return "Meter Consumption is too high. Please check the Curr. reading entered";
//        }
        if (unit_calculated > 1000 && GSBilling.getInstance ( ).getMetOVERFLOW ( ) == 1) {
            return "Meter Consumption is too high. Please check the Curr. reading entered";
        }

        return "0";
    }

    private double consmuptionCalc(int calculatedUnit) {
        if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase ( "S" )) {

            C_assesed_consumption = (int) (Double.parseDouble ( Structconsmas.AVGUNITS1 ) + Double.parseDouble ( Structconsmas.AVGUNITS2 ) + Double.parseDouble ( Structconsmas.AVGUNITS3 )) / 3;
            C_assesed_consumption = Math.max ( C_assesed_consumption, Double.valueOf ( Structconsmas.AVGUNITS1 ) );


        } else {

            C_assesed_consumption = (int) (Double.parseDouble ( Structconsmas.PREV_AVG_UNIT )) + calculatedUnit;

        }

        return C_assesed_consumption;
    }

    private String consumption2times() {
        int unit_calculated = calBill.unit;
        consmuptionCalc ( unit_calculated );

        if (C_assesed_consumption > 50 && unit_calculated > 50) {
            if (((C_assesed_consumption * 2)) > unit_calculated) {
                return " - Consumption is 2 Times higher \n";//, "2 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
            }
        }

        return "0";
    }

    private String consumption3times() {
        int unit_calculated = calBill.unit;
        consmuptionCalc ( unit_calculated );

        if (C_assesed_consumption > 50 && unit_calculated > 50) {
            if (((C_assesed_consumption * 3)) > unit_calculated) {
                return " - Consumption is 3 Times higher \n";//, "2 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
            }
        }

        return "0";
    }

    private String higherLoadChk() {
        double load = 0;
        load = ucom.convertLoadToWatts ( );
        if (load > 10000) {
//            return "Load is very high";
            return "0";
        }

        return "0";
    }

    private String higherMDChk() {
        double load = 0;
        load = ucom.convertLoadToWatts ( );
        if (GSBilling.getInstance ( ).getMaxDemand ( ) > 30) {
            return "MD value is too high. Please check the MD entered";
        }

        return "0";
    }

    private static int doubleValueChk(String checkString) {

        int convertvalue = 0;
        double value = Double.parseDouble ( checkString );

        convertvalue = (int) value;

        return convertvalue;

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

    public void onBackPressed() {
//        if (exit) {
        GSBilling.getInstance ( ).clearData ( );
//        UtilAppCommon.nullyfimodelBill();
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

    private void configureLog4j() {
        String todayDate = new SimpleDateFormat ( "dd_MMM_yyyy" ).format ( Calendar.getInstance ( ).getTime ( ) );

        // set file name
        String fileName = Environment.getExternalStorageDirectory ( ) + "/MeterCommunications/Log_" + todayDate + ".txt";

        // set log line pattern
        String filePattern = "%d - [%c] - %p : %m%n";
        // set max. number of backed up log files
        int maxBackupSize = 10;
        // set max. size of log file
        long maxFileSize = 1024 * 1024;

        // configure
        Log4jHelper.Configure ( fileName, filePattern, maxBackupSize, maxFileSize );


    }

    // IR Reading....
    // Call Back method  to get the Message form other Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );
        etCR.setEnabled ( true );
        switch (GSBilling.getInstance ( ).getCodeMetre ( )) {
            /**
             * When meter not responding
             */
            case 0:
                Toast.makeText ( getApplicationContext ( ), data.getStringExtra ( "Data" ), Toast.LENGTH_SHORT ).show ( );
                etCR.setText ( "" );
                tv_meter_number.setText ( "" );
                tv_md_value.setText ( "0.0" );
                etCR.setEnabled ( true );
                Structbilling.PRNT_BAT_STAT = selected_meter + "_";
                break;
            /**
             * on meter success
             */
            case 1:
                etCR.setText ( String.valueOf ( data.getStringExtra ( "Data" ) ).split ( "\\." )[0].trim ( ) );
                tv_meter_number.setText ( data.getStringExtra ( "meter_number" ) );
                if (data.getStringExtra ( "MD" ) != null)
                    tv_md_value.setText ( data.getStringExtra ( "MD" ) );
                else
                    tv_md_value.setText ( "0.0" );
                etCR.setEnabled ( false );
                Structbilling.PRNT_BAT_STAT = selected_meter + "_" + tv_meter_number.getText ( ).toString ( ).trim ( );
                break;
        }
    }

    @Override
    public void onStartReading(String make, String model) {
        selected_meter = make + "_" + model;
        android.util.Log.e ( "Reading input", selected_meter );

        switch (selected_meter) {
            /**
             * GENUS
             */
            case "GENUS_OPTICAL": {
                GSBilling.getInstance ( ).setMeterCode ( 6 );
                Intent intent_genus_optical = new Intent ( Readinginput.this, com.fedco.mbc.felhr.droidterm.MainActivity.class );
                startActivityForResult ( intent_genus_optical, 2 );
            }
            break;

            case "GENUS_6LOWPAN": {
                GSBilling.getInstance ( ).setMeterCode ( 6 );
                Intent intent_genus_6lowpan = new Intent ( Readinginput.this, com.fedco.mbc.felhr.droidterm.MainActivity.class );
                startActivityForResult ( intent_genus_6lowpan, 2 );
            }
            break;

            case "GENUS_IRDA": {
                GSBilling.getInstance ( ).setMeterCode ( 4 );
                Intent intent_genus_irda = new Intent ( Readinginput.this, com.fedco.mbc.felhr.droidterm.MainActivity.class );
                startActivityForResult ( intent_genus_irda, 2 );
            }
            break;

            case "GENUS_DLMS": {
                GSBilling.getInstance ( ).setMeterCode ( 18 );
                Intent intent_genus_dlms = new Intent ( Readinginput.this, com.fedco.mbc.felhr.droidterm.MainActivity.class );
                startActivityForResult ( intent_genus_dlms, 2 );
            }
            break;

            /**
             * AVON
             */
            case "AVON_OPTICAL": {
                GSBilling.getInstance ( ).setMeterCode ( 14 );
                Intent intent_avon_optical = new Intent ( Readinginput.this, com.fedco.mbc.felhr.droidterm.MainActivity.class );
                startActivityForResult ( intent_avon_optical, 2 );
            }
            break;

            case "AVON_IRDA": {
                GSBilling.getInstance ( ).setMeterCode ( 12 );
                Intent intent_avon_irda = new Intent ( Readinginput.this, com.fedco.mbc.felhr.droidterm.MainActivity.class );
                startActivityForResult ( intent_avon_irda, 2 );
            }
            break;

            case "AVON_DLMS": {
                GSBilling.getInstance ( ).setMeterCode ( 20 );
                Intent intent_avon_dlms = new Intent ( Readinginput.this, com.fedco.mbc.felhr.droidterm.MainActivity.class );
                startActivityForResult ( intent_avon_dlms, 2 );
            }
            break;

            case "AVON_3PH": {
                GSBilling.getInstance ( ).setMeterCode ( 16 );
                Intent intent_avon_3ph = new Intent ( Readinginput.this, com.fedco.mbc.felhr.droidterm.MainActivity.class );
                startActivityForResult ( intent_avon_3ph, 2 );
            }
            break;

            /**
             *VISIONTEK
             */
            case "VISIONTEK_IRDA": {
                GSBilling.getInstance ( ).setMeterCode ( 10 );
                Intent intent_visiontek_irda = new Intent ( Readinginput.this, com.fedco.mbc.felhr.droidterm.MainActivity.class );
                startActivityForResult ( intent_visiontek_irda, 2 );
            }
            break;

            case "VISIONTEK_DLMS": {
                GSBilling.getInstance ( ).setMeterCode ( 21 );
                Intent intent_visiontek_dlms = new Intent ( Readinginput.this, com.fedco.mbc.felhr.droidterm.MainActivity.class );
                startActivityForResult ( intent_visiontek_dlms, 2 );
            }
            break;

            case "VISIONTEK_6LOWPAN": {
                GSBilling.getInstance ( ).setMeterCode ( 24 );
                Intent intent_visiontek_6lowpan = new Intent ( Readinginput.this, com.fedco.mbc.felhr.droidterm.MainActivity.class );
                startActivityForResult ( intent_visiontek_6lowpan, 2 );
            }
            break;

            /**
             * MONTEL
             */
            case "MONTEL_OPTICAL": {
                GSBilling.getInstance ( ).setMeterCode ( 11 );
                Intent intent_montel_irda = new Intent ( Readinginput.this, com.fedco.mbc.felhr.droidterm.MainActivity.class );
                startActivityForResult ( intent_montel_irda, 2 );// Activity is started with requestCode 2
            }

            case "MONTEL_IRDA": {
                GSBilling.getInstance ( ).setMeterCode ( 11 );
                Intent intent_montel_irda = new Intent ( Readinginput.this, com.fedco.mbc.felhr.droidterm.MainActivity.class );
                startActivityForResult ( intent_montel_irda, 2 );// Activity is started with requestCode 2

            }
            break;

            /**
             *BENTEK
             */
            case "BENTEK_OPTICAL": {
                GSBilling.getInstance ( ).setMeterCode ( 13 );
                Intent intentBentek = new Intent ( Readinginput.this, com.fedco.mbc.felhr.droidterm.MainActivity.class );
                startActivityForResult ( intentBentek, 2 );
            }

            break;

            /**
             *ALLIED
             */
            case "ALLIED_OPTICAL": {
                GSBilling.getInstance ( ).setMeterCode ( 15 );
                Intent intent_allied_opt = new Intent ( Readinginput.this, com.fedco.mbc.felhr.droidterm.MainActivity.class );
                startActivityForResult ( intent_allied_opt, 2 );
            }
            break;

            case "ALLIED_IR": {
                GSBilling.getInstance ( ).setMeterCode ( 1 );
                Intent intent_allied_ir = new Intent ( Readinginput.this, com.fedco.mbc.felhr.droidterm.MainActivity.class );
                startActivityForResult ( intent_allied_ir, 2 );
            }
            break;


            /**
             * LNG
             */
            case "LNG_DLMS": {
                GSBilling.getInstance ( ).setMeterCode ( 23 );
                Intent intent_landis_dlms = new Intent ( Readinginput.this, com.fedco.mbc.felhr.droidterm.MainActivity.class );
                startActivityForResult ( intent_landis_dlms, 2 );
            }
            break;

            /**
             * LNT
             */
            case "LNT_3PH OPTICAL": {
                GSBilling.getInstance ( ).setMeterCode ( 2 );
                Intent intentLNT = new Intent ( Readinginput.this, com.fedco.mbc.felhr.droidterm.MainActivity.class );
                startActivityForResult ( intentLNT, 2 );
            }

            break;

            case "LNT_DLMS": {
                GSBilling.getInstance ( ).setMeterCode ( 17 );
                Intent intent_lnt_dlms = new Intent ( Readinginput.this, com.fedco.mbc.felhr.droidterm.MainActivity.class );
                startActivityForResult ( intent_lnt_dlms, 2 );
            }
            break;

            /**
             * SECURE
             */
            case "SECURE_OPTICAL": {
                GSBilling.getInstance ( ).setMeterCode ( 3 );
                Intent intent_secure_optical = new Intent ( Readinginput.this, com.fedco.mbc.felhr.droidterm.MainActivity.class );
                startActivityForResult ( intent_secure_optical, 2 );
            }
            break;

            case "SECURE_DLMS": {
                GSBilling.getInstance ( ).setMeterCode ( 25 );
                Intent intent_secure_dlms = new Intent ( Readinginput.this, com.fedco.mbc.felhr.droidterm.MainActivity.class );
                startActivityForResult ( intent_secure_dlms, 2 );
            }
            break;

            /**
             * HPL
             */
            case "HPL_DLMS": {
                GSBilling.getInstance ( ).setMeterCode ( 22 );
                Intent intent_hpl_dlms = new Intent ( Readinginput.this, com.fedco.mbc.felhr.droidterm.MainActivity.class );
                startActivityForResult ( intent_hpl_dlms, 2 );
            }
            break;

            /**
             * EMCO
             */
            case "EMCO_DLMS": {
                GSBilling.getInstance ( ).setMeterCode ( 19 );
                Intent intent_emco_dlms = new Intent ( Readinginput.this, com.fedco.mbc.felhr.droidterm.MainActivity.class );
                startActivityForResult ( intent_emco_dlms, 2 );
            }
            break;

            default: {
                Toast.makeText ( RActivity, selected_meter + " not configurable yet try other option!", Toast.LENGTH_SHORT ).show ( );
            }
        }
    }

    class MakeZip extends AsyncTask <String, Void, Boolean> {
        private String sourcePath = Environment.getExternalStorageDirectory ( ) + "/MeterCommunications/";
        private String destinationPath = Environment.getExternalStorageDirectory ( ) + File.separator + "MeterCommunicationsZip" + "/" + "Meter_Log_" + (new SimpleDateFormat ( "dd_MMM_yyyy" ).format ( Calendar.getInstance ( ).getTime ( ) ) + ".zip");
        private ProgressDialog progressBar;

        public MakeZip() {
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute ( );
            progressBar = new ProgressDialog ( RActivity );
            progressBar.setCancelable ( false );
            progressBar.setMessage ( "Sending ZipFile to Server..." );
            progressBar.show ( );
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                zipFolder ( sourcePath, destinationPath );
                GSBilling.getInstance ( ).setUploadZipToServer ( destinationPath );
                FileInputStream fileInputStream = new FileInputStream ( GSBilling.getInstance ( ).getUploadZipToServer ( ) );
                HttpFileUpload httpFileUpload = new HttpFileUpload ( "http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles", "", destinationPath );
                int status = httpFileUpload.Send_Now ( fileInputStream );
                return status == 200;
            } catch (Exception e) {
                e.printStackTrace ( );
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean isFileUpload) {
            super.onPostExecute ( isFileUpload );
            progressBar.dismiss ( );
            if (isFileUpload) {
                Toast.makeText ( getApplicationContext ( ), "File uploaded successfully", Toast.LENGTH_SHORT ).show ( );
            } else {
                Toast.makeText ( getApplicationContext ( ), "Fail to uploaded file", Toast.LENGTH_SHORT ).show ( );
            }
        }
    }

//    public String checkDemandBasedValidation() {
//        String val = null;
//        if (Structtariff.MD_MFC_CMP_FLAG.equalsIgnoreCase("Y")) {
////            if  (tv_md_value.getText().toString().equalsIgnoreCase("") && !tv_md_value.getText().toString().isEmpty() ){
//            if  (tv_md_value.length()==0 || etPF.length()==0 ){
//                val = "NOK";
//            } else if(tv_md_value.getText().toString() != null && !tv_md_value.getText().toString().isEmpty() || etPF.getText().toString() != null && !etPF.getText().toString().isEmpty()){
//                val = "OK";
//            }else {
//                val = "NOK";
//            }
//        }else{
//            val="OK";
//        }
//
//        return val;
//    }

    public static void zipFolder(String inputFolderPath, String outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream ( outZipPath );
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
            System.out.println ( "hello" + srcFile.delete ( ) );
            zos.close ( );
        } catch (IOException ioe) {
            android.util.Log.e ( "", ioe.getMessage ( ) );
        }
    }

    //------------------Meter Make--------------------------------//


    public class MeterMake extends AsyncTask <String, String, String> {
        ProgressDialog pd;
        Context _context;

        MeterMake(Context ctx) {
            _context = ctx;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // ShowAlertagain();
            super.onPreExecute ( );
            pd = new ProgressDialog ( Readinginput.this );
            pd.setMessage ( "Please wait..." );
            pd.setCancelable ( false );
            pd.show ( );
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB ( Readinginput.this );
            SD = databaseHelper.getReadableDatabase ( );

            try {
//                String poleString = "SELECT SRNO,REMARK_DESC from TBL_REMARKS_MASTER WHERE BILL_BASIS='" + Structbilling.Bill_Basis + "'";
                String poleString = "SELECT SRNO,REMARK_DESC from TBL_REMARKS_MASTER WHERE BILL_BASIS='ACT'";
                //System.out.println("select distinct POLE_CODE,POLE_TYPE from TBL_POLE_MASTER WHERE FEEDER_CODE ='" + feeder_id + "'");
                metermake_cursor = SD.rawQuery ( poleString, null );
                if (metermake_cursor != null && metermake_cursor.moveToFirst ( )) {
                    metermakeid_list.add ( "Select Reason " );
                    metermake_list.add ( "Select Reason" );
                    do {

                        String metermake_id = metermake_cursor.getString ( 0 );
                        System.out.println ( "meter_id: : " + metermake_id );
                        String metermake_name = metermake_cursor.getString ( 1 );
                        System.out.println ( "mete_name: " + metermake_name );
                        metermakeid_list.add ( metermake_id );
                        metermake_list.add ( metermake_name );

                    } while (metermake_cursor.moveToNext ( ));
                }
            } catch (Exception e) {
                e.printStackTrace ( );
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute ( result );
            pd.hide ( );
            pd.dismiss ( );
            try {
               /* project_adapter=new ArrayAdapter<String>(SiteInspection_Activity2.this,android.R.layout.simple_spinner_item,project_name_list);
                project_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
                metermake_adapter = new ArrayAdapter <String> (
                        getApplicationContext ( ), R.layout.custom_spinner, R.id.textView1, metermake_list );
                sp_reasons.setAdapter ( metermake_adapter );

            } catch (Exception e) {
                e.printStackTrace ( );
            }
        }
    }

    public class MeterStatus extends AsyncTask <String, String, String> {
        Context _context;

        MeterStatus(Context ctx) {
            _context = ctx;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // ShowAlertagain();
            super.onPreExecute ( );

        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            try {
                dbHelper = new DB ( getApplicationContext ( ) );
                SD = dbHelper.getWritableDatabase ( );
                Cursor c = SD.rawQuery ( "SELECT STATUS FROM TBL_METERSTATUSCODE WHERE STATUS IN ('READ','ESTIMATE','OVERFLOW')", null );

                if (c != null) {
                    if (c.moveToFirst ( )) {
                        do {
                            String status = c.getString ( c.getColumnIndex ( "STATUS" ) );
                            Logger.e ( getApplicationContext ( ), "MeterStateAct", "STATSUS IS " + status );
//                        int age = c.getInt(c.getColumnIndex("Age"));

                            System.out.println ( "This is status1 " + status );
                            if (status.equalsIgnoreCase ( "overflow" )) {
                                status = "ROLLOVER";
                            }
                            results.add ( status );
                        } while (c.moveToNext ( ));
                    }
                }
            } catch (SQLiteException se) {
                Logger.e ( getApplicationContext ( ), "MeterStatAct", "Could not create or Open the database" );
            } finally {
//            if (newDB != null)
//                newDB.execSQL("DELETE FROM " + tableName);
                SD.close ( );
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute ( result );
            try {
                meterStatusAdapter = new ArrayAdapter <String> (
                        getApplicationContext ( ), R.layout.custom_spinner, R.id.textView1, results );
                sp_status.setAdapter ( meterStatusAdapter );

            } catch (Exception e) {
                e.printStackTrace ( );
            }
        }
    }


    /*
    public class MeterMake extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        Context _context;

        MeterMake(Context ctx) {
            _context = ctx;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // ShowAlertagain();
            super.onPreExecute();
            pd = new ProgressDialog(Readinginput.this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            dbHelper = new DB(Readinginput.this);
            SD = dbHelper.getReadableDatabase();

            try {
                String poleString = "SELECT SRNO,REMARK_DESC from TBL_REMARKS_MASTER WHERE BILL_BASIS='" + Structbilling.Bill_Basis + "'";
                //System.out.println("select distinct POLE_CODE,POLE_TYPE from TBL_POLE_MASTER WHERE FEEDER_CODE ='" + feeder_id + "'");
                metermake_cursor = SD.rawQuery(poleString, null);
                if (metermake_cursor != null && metermake_cursor.moveToFirst()) {
                    metermakeid_list.add("Select Reason ");
                    metermake_list.add("Select Reason");
                    do {

                        String metermake_id = metermake_cursor.getString(0);
                        System.out.println("meter_id: : " + metermake_id);
                        String metermake_name = metermake_cursor.getString(1);
                        System.out.println("mete_name: " + metermake_name);
                        metermakeid_list.add(metermake_id);
                        metermake_list.add(metermake_name);

                    } while (metermake_cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            pd.hide();
            pd.dismiss();
            try {
               *//* project_adapter=new ArrayAdapter<String>(SiteInspection_Activity2.this,android.R.layout.simple_spinner_item,project_name_list);
                project_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*//*
                metermake_adapter = new ArrayAdapter<String>(
                        getApplicationContext(), R.layout.custom_spinner, R.id.textView1, metermake_list);
                spinnerReason.setAdapter(metermake_adapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

  */
    @Override
    public void onUserInteraction() {
        super.onUserInteraction ( );
        ((GlobalPool) getApplication ( )).onUserInteraction ( );
    }

    @Override
    public void userLogoutListaner() {


        finish ( );
        Intent intent = new Intent ( Readinginput.this, MainActivity.class );
        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( intent );

    }

    public void dialogBox(String popmessage) {

        if (sweetAlertDialog == null) {
            sweetAlertDialog = new SweetAlertDialog ( Readinginput.this, SweetAlertDialog.WARNING_TYPE );
            sweetAlertDialog.setTitleText ( "Billing error" );
            sweetAlertDialog.setContentText ( popmessage );
            sweetAlertDialog.setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismissWithAnimation ( );

                }
            } );
            sweetAlertDialog.show ( );
        } else {
            sweetAlertDialog.dismiss ( );
            sweetAlertDialog = new SweetAlertDialog ( Readinginput.this, SweetAlertDialog.WARNING_TYPE );
            sweetAlertDialog.setTitleText ( "Billing error" );
            sweetAlertDialog.setContentText ( popmessage );
            sweetAlertDialog.setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismissWithAnimation ( );

                }
            } );
            sweetAlertDialog.show ( );

        }


    }


    public void showWarningDialog(String message) {


        sweetAlertDialog = new SweetAlertDialog ( Readinginput.this, SweetAlertDialog.WARNING_TYPE );
        sweetAlertDialog.setTitleText ( message );
        sweetAlertDialog.setContentText ( "Do You Want To Continue? " );
        sweetAlertDialog.setConfirmText ( "Ok" );
        sweetAlertDialog.setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
            @Override
            public void onClick(SweetAlertDialog sDialog) {

                Intent intent = new Intent ( Readinginput.this, BillingViewActivity.class );


                GSBilling.getInstance ( ).setNormalReason ( 1 );
                startActivity ( intent );
                overridePendingTransition ( R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left );

                sDialog.dismissWithAnimation ( );

            }
        } ).setCancelText ( "Cancel" ).setCancelClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation ( );
            }
        } );

        sweetAlertDialog.show ( );


    }


    public void storeReasons() {


        Paper.book ( "Normal" ).write ( "NormalList", normalList ( ) );
        Paper.book ( "MeterFaulty" ).write ( "FaultyList", meterFaultList ( ) );
        Paper.book ( "ReadingNotTaken" ).write ( "ReadingNotList", readingNotTakenList ( ) );
        Paper.book ( "MeterMissing" ).write ( "MeterMissingList", meterMissingList ( ) );
        Paper.book ( "MeterOverflow" ).write ( "MeterOverflowList", overflow ( ) );
        Paper.book ( "NotTraceable" ).write ( "NotTraceableList", notTracable ( ) );
        Paper.book ( "PremiseLocked" ).write ( "PremiseLockedList", premiseLocked ( ) );

    }



    public ArrayList<String> meterMissingList(){
        ArrayList <String> list = new ArrayList <> ( );
        list.add ( "Meter Missing" );
        return list;
    }

    public ArrayList<String> overflow(){
        ArrayList <String> list = new ArrayList <> ( );
        list.add ( "Overflow" );
        return list;
    }

    public ArrayList<String> notTracable(){
        ArrayList <String> list = new ArrayList <> ( );
        list.add ( "Service Missing" );
        list.add ( "No Trace Service" );
        return list;
    }
    public ArrayList<String> premiseLocked(){
        ArrayList <String> list = new ArrayList <> ( );
        list.add ( "Premise Locked" );
        return list;
    }

    public ArrayList <String> normalList() {
        ArrayList <String> list = new ArrayList <> ( );
        list.add ( "Meter Normal" );
        list.add ( "self Shifting" );
        list.add ( "Previous Reading Wrong" );
        list.add ( "Serv used for Commer" );
        list.add ( "Ext. Unauthorisedly" );
        list.add ( "Service open to Sky" );
        list.add ( "Serv no Wrongly Painted" );
        list.add ( "Disc showing consmp" );
        list.add ( "Theft of Energy" );
        return list;

    }

    public ArrayList <String> meterFaultList() {
        ArrayList <String> list = new ArrayList <> ( );
        list.add ( " Meter Cripping Backward" );
        list.add ( "Counter Defective" );
        list.add ( "Meter Burnt" );
        list.add ( "Meter Stopped" );
        list.add ( "Meter Noisy" );
        list.add ( "Meter Not Gearing" );
        list.add ( "Meter Glass Broken" );
        list.add ( "Meter Sticky" );
        list.add ( " Meter Damaged" );
        list.add ( "Meter Seal Broken" );
        list.add ( "Dirty Glass" );
        list.add ( "Reading not Visible" );
        list.add ( "ELECTRONIC METER-DISP.CUT" );
        list.add ( "ELECTRONIC METER-NO DISP." );

        return list;

    }

    public ArrayList <String> readingNotTakenList() {
        ArrayList <String> list = new ArrayList <> ( );
        list.add ( "Water Logging" );
        list.add ( "Meter fixed at Low Level" );
        list.add ( "Paper seal on Cupboard" );
        list.add ( "Meter Hanging" );
        list.add ( "Darkness" );
        list.add ( "Obstacle to take Reading" );
        list.add ( "Refused to obtain Reading" );
        list.add ( "Others No facility" );
        list.add ( "No Display as no Power" );
        list.add ( "Reading Not Taken  RNT" );


        return list;

    }

    public void setReasonAdapter(String bookName, String listName) {

        ArrayList <String> list = Paper.book ( bookName ).read ( listName );

        metermake_adapter = new ArrayAdapter <String> (
                getApplicationContext ( ), R.layout.custom_spinner, R.id.textView1, list );
        sp_reasons.setAdapter ( metermake_adapter );

    }

    class ImageAdapter extends RecyclerView.Adapter <ImageAdapter.ImageHolder> {

        LayoutInflater inflater;
        Context context;
        ArrayList <String> imageList;


        ImageAdapter(Context context, ArrayList <String> list) {
            inflater = LayoutInflater.from ( context );

            this.imageList = list;
            this.context = context;
        }

        @Override
        public ImageAdapter.ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate ( R.layout.image_container_single_item, parent, false );

            return new ImageAdapter.ImageHolder ( view );
        }

        @Override
        public void onBindViewHolder(ImageAdapter.ImageHolder holder, int position) {


            System.out.println ( "This is showed Images " + imageList.get ( position ) );
            holder.im_image.setImageBitmap ( BitmapFactory.decodeFile ( imageList.get ( (imageList.size ( ) - 1) - position ) ) );
            holder.tv_number.setText ( "" + position );


        }

        @Override
        public int getItemCount() {
            if (imageList.size ( ) > 3) {
                return 3;
            } else {
                return imageList.size ( );
            }

        }


        class ImageHolder extends RecyclerView.ViewHolder {
            TextView tv_number;
            CardView cv_container;
            ImageView im_image;


            ImageHolder(View itemView) {
                super ( itemView );
                tv_number = itemView.findViewById ( R.id.tv_number );
                cv_container = itemView.findViewById ( R.id.cv_container );
                im_image = itemView.findViewById ( R.id.im_image );

                cv_container.setOnClickListener ( new View.OnClickListener ( ) {
                    @Override
                    public void onClick(View view) {
                        showDialog ( Readinginput.this, imageList.get ( getAdapterPosition ( ) ) );
                    }
                } );


            }


        }
    }


    public void showDialog(Context context, String path) {


        DialogBox dialogBox = new DialogBox ( context );
        final Dialog showAadharDialog = dialogBox.setRequestedDialog ( false, R.layout.aadhar_dialog );

        ImageView im_closeDialog = showAadharDialog.findViewById ( R.id.im_cancel );
        ImageView im_showAadharImage = showAadharDialog.findViewById ( R.id.im_aadharImage );
        //  ProgressBar progressBar = showAadharDialog.findViewById ( R.id.pb_imageLoad );

        im_showAadharImage.setImageBitmap ( BitmapFactory.decodeFile ( path ) );

        im_closeDialog.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                showAadharDialog.dismiss ( );
            }
        } );

        showAadharDialog.show ( );


    }


}