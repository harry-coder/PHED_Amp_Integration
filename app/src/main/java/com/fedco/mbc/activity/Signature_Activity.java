package com.fedco.mbc.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.amigos.MainActivity;
import com.fedco.mbc.authentication.PrinterSessionManager;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.bluetoothprinting.GlobalPool;
import com.fedco.mbc.bluetoothprinting.SLPrintingMainActivity;
import com.fedco.mbc.model.StructSurveySecMaster;
import com.fedco.mbc.model.Structbilling;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.model.Structtariff;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.HttpFileUpload;
import com.fedco.mbc.utils.URLS;
import com.fedco.mbc.utils.UtilAppCommon;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by nitinb on 17-02-2016.
 */
public class Signature_Activity extends Activity implements LogoutListaner {

    Button submit, clear;
    GestureOverlayView gestureView;
    String print_type;
    SessionManager session;
    PrinterSessionManager printerSession;
    HashMap <String, String> user;
    String printer, printerID;
    String LicKey, key;
    Long billseq;
    UtilAppCommon appCommon;
    DB dbHelper;
    SQLiteDatabase SD;
    String printer_catergory, printer_mfg, printer_roll, prev_pref, slot1;
    Button mClear, mGetSign, mCancel;
    File file;
    LinearLayout mContent;
    private ProgressDialog progress;
    SweetAlertDialog sDialog;
    View view;
    Long dcSeq;
    signature mSignature;
    Bitmap bitmap;
    boolean enbleButton = false;
    TextView mTextView;
    SQLiteDatabase SD2, SD3, SD4;
    DB dbHelper2, dbHelper3, dbHelper4;
    // Creating Separate Directory for saving Generated Images
    String DIRECTORY;//= Environment.getExternalStorageDirectory().getPath() + "/MBC/Images/";
    String pic_name;// = UtilAppCommon.UniqueCode(getApplicationContext()) + "_" + Structconsmas.LOC_CD+"_"+Structconsmas.MAIN_CONS_LNK_NO + "_sig";
    String StoredPath;
    String ZipSourcePath = Environment.getExternalStorageDirectory ( ) + "/MBC/Images/";
    String ZipCopyPath = Environment.getExternalStorageDirectory ( ) + "/MBC/Downloadsingular/";
    String ZipDeletPath2 = Environment.getExternalStorageDirectory ( ) + "/MBC/Downloadsingular/";
    String Zip = Environment.getExternalStorageDirectory ( ) + "/Notes/billing.csv";
    String ZipDesPathdup;
    String signaturePathDes;
    String photoPathDes;
    String ZipDesPath;
    String imgSrcPathDes;
    String imgSrcPath;
    ArrayList <String> mylistimagename = new ArrayList <String> ( );
    UtilAppCommon appcomUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        // TODO Auto-generated method stub
        super.onCreate ( savedInstanceState );
        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        setContentView ( R.layout.signature );
//----------------------------
        DIRECTORY = Environment.getExternalStorageDirectory ( ).getPath ( ) + "/MBC/Images/";
        mContent = (LinearLayout) findViewById ( R.id.canvasLayout );
        mSignature = new signature ( getApplicationContext ( ), null );
        mSignature.setBackgroundColor ( Color.WHITE );
        // Dynamically generating Layout through java code
        mContent.addView ( mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT );
        mClear = (Button) findViewById ( R.id.clear );
        mGetSign = (Button) findViewById ( R.id.getsign );
        mTextView = (TextView) findViewById ( R.id.textView_code );

        mGetSign.setEnabled ( true );
        mCancel = (Button) findViewById ( R.id.cancel );
        view = mContent;
        appcomUtil = new UtilAppCommon ( );
        mGetSign.setOnClickListener ( onButtonClick );
        mClear.setOnClickListener ( onButtonClick );
        mCancel.setOnClickListener ( onButtonClick );

        ZipDesPath = Environment.getExternalStorageDirectory ( ) + "/MBC/" + appcomUtil.UniqueCode ( getApplicationContext ( ) ) + GSBilling.getInstance ( ).captureDatetime ( ) + ".zip";
        ZipDesPathdup = "/MBC/" + appcomUtil.UniqueCode ( getApplicationContext ( ) ) + GSBilling.getInstance ( ).captureDatetime ( );
//        mTextView.setText("Signature is Compulsory" + "\n" + "Please sign here...");

//-----------------------------
//
//        gestureView = (GestureOverlayView) findViewById(R.id.signaturePad);
//        submit = (Button) findViewById(R.id.getsign);
//        clear = (Button) findViewById(R.id.clear);

        ((GlobalPool) getApplication ( )).registerSessionListaner ( this );
        ((GlobalPool) getApplication ( )).startUserSession ( );


        printerSession = new PrinterSessionManager ( getApplicationContext ( ) );
        session = new SessionManager ( getApplicationContext ( ) );
        appCommon = new UtilAppCommon ( );

        printerID = printerSession.retPrinterID ( );
        printer = printerSession.retPrintID ( );

        LicKey = session.retLicence ( );

        Structbilling.SBM_No = LicKey;
        Structbilling.Meter_Reader_Name = session.retMRName ( );
        Structbilling.Meter_Reader_ID = session.retMRID ( );
//        Structbilling.Rbt_Date =
//        Structbilling.Due_Date =

        dbHelper = new DB ( Signature_Activity.this );
        SD = dbHelper.getWritableDatabase ( );

        String strPref = "SELECT PRINTER_PREF FROM USER_MASTER";
        Log.e ( "Sequence", "update " + strPref );

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
        Log.e ( "Sequence", "update " + strPref );

        Cursor curDcCode = SD.rawQuery ( dcCode, null );

        if (curDcCode != null && curDcCode.moveToFirst ( )) {

            Structconsmas.DC_NAME = curDcCode.getString ( 0 );

        }

        String divCode = "SELECT DIV_NAME,DISPLAY_CODE FROM TBL_BILLING_DIV_MASTER";// WHERE RMS_DC_CODE='"+Structconsmas.LOC_CD+"' OR CCNB_DC_CODE='"+Structconsmas.LOC_CD+"'";
//        Log.e("Sequence", "update " + strPref);

        dbHelper = new DB ( Signature_Activity.this );
        SD = dbHelper.getWritableDatabase ( );
        Cursor curDivCode = SD.rawQuery ( divCode, null );

        if (curDivCode != null && curDivCode.moveToFirst ( )) {

            Structconsmas.DIV_NAME = curDivCode.getString ( 0 );
            Structconsmas.PICK_REGION = curDivCode.getString ( 1 );

        }


//        clear.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Log.v("log_tag", "Panel Cleared");
//                //                mSignature.clear();
//                gestureView.clear(true);
//                //                submit.setEnabled(false);
//            }
//        });


        if (!unitPercentageChk ( ).equalsIgnoreCase ( "0" ) && Structbilling.Cur_Meter_Stat == 1) {

            /****DIALOG BOX INITIALIZATION PROVIDING A LAYOUT TO DIALOUGE****/
            final Dialog dialogAccount = new Dialog ( Signature_Activity.this, R.style.DialogeAppTheme );

            dialogAccount.setContentView ( R.layout.custom_dialoge_warning );
            dialogAccount.setTitle ( "Account Search" );

            // set the custom dialog components - text, image and button

            Button dialogButton = (Button) dialogAccount.findViewById ( R.id.dialogButtonACCOK );
            GSBilling.getInstance ( ).setConsumptionchkhigh ( "  " );
            // if button is clicked, close the custom dialog
            /****DIALOUGE BOX BUTTON INTIALISATION AND EVENT ADDING****/
            dialogButton.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {

                    GSBilling.getInstance ( ).setConsumptionchkhigh ( "**" );

                    appCommon = new UtilAppCommon ( );
                    session = new SessionManager ( getApplicationContext ( ) );
                    key = appCommon.UniqueCode ( getApplicationContext ( ) );

                    Structbilling.SBM_No = key;
                    Structbilling.Meter_Reader_Name = session.retMRName ( );
                    Structbilling.Meter_Reader_ID = session.retMRID ( );


                    //-------------not used
                    Structconsmas.Meter_Type = "00";
                    Structconsmas.Meter_Ownership = "A";

                    //----------
                    Structbilling.Cumul_Pro_Elec_Duty = 1.25f;
                    billseq = appCommon.findSequence ( getApplicationContext ( ), "BillNumber" );
                    dbHelper = new DB ( getApplicationContext ( ) );
                    SD = dbHelper.getWritableDatabase ( );

                    //  dbHelper4.insertIntoBillingTable();
                    dbHelper.insertIntoMPBillingTable ( );
                    dbHelper.insertSequence ( "BillNumber", billseq );

                    Toast.makeText ( Signature_Activity.this, "Data Stored", Toast.LENGTH_SHORT ).show ( );

                    dialogAccount.dismiss ( );
//
                    appCommon.nullyfimodelBill ( );
                    Intent intent = new Intent ( getApplicationContext ( ), Billing.class );
                    startActivity ( intent );
                    overridePendingTransition ( R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left );

                }
            } );

            dialogAccount.setOnKeyListener ( new Dialog.OnKeyListener ( ) {
                @Override
                public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                    // TODO Auto-generated method stub
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        Intent intent = new Intent ( Signature_Activity.this, BillingtypesActivity.class );
                        GSBilling.getInstance ( ).setPrintSingle ( "<0x09>" );
                        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                        startActivity ( intent );
                        overridePendingTransition ( R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left );
                        dialogAccount.dismiss ( );
                    }
                    return true;
                }

            } );
            dialogAccount.show ( );

        }
        if (appCommon.convertLoadToWatts ( ) > 10000d) {

            new SweetAlertDialog ( Signature_Activity.this, SweetAlertDialog.WARNING_TYPE )
                    .setTitleText ( "Load is above 10KW" )
                    .setContentText ( "You are prohibited to bill the consumer" )
                    .setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation ( );
//                            appCommon.nullyfimodelBill();
//                            Intent intent = new Intent(getApplicationContext(), Billing.class);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                    R.anim.anim_slide_out_left);

                            GSBilling.getInstance ( ).setConsumptionchkhigh ( "**" );
                            appCommon = new UtilAppCommon ( );
                            session = new SessionManager ( getApplicationContext ( ) );
                            key = appCommon.UniqueCode ( getApplicationContext ( ) );

                            Structbilling.Cumul_Pro_Elec_Duty = 10000f;
                            Structbilling.SBM_No = key;
                            Structbilling.Meter_Reader_Name = session.retMRName ( );
                            Structbilling.Meter_Reader_ID = session.retMRID ( );

                            //-------------not used
                            Structconsmas.Meter_Type = "00";
                            Structconsmas.Meter_Ownership = "A";
                            //----------

                            billseq = appCommon.findSequence ( getApplicationContext ( ), "BillNumber" );
                            dbHelper = new DB ( getApplicationContext ( ) );
                            SD = dbHelper.getWritableDatabase ( );
                            //  dbHelper4.insertIntoBillingTable();
                            dbHelper.insertIntoMPBillingTable ( );
                            dbHelper.insertSequence ( "BillNumber", billseq );

                            appCommon.nullyfimodelBill ( );
                            Toast.makeText ( Signature_Activity.this, "Data Stored", Toast.LENGTH_SHORT ).show ( );
                            Intent intent = new Intent ( getApplicationContext ( ), Billing.class );
                            startActivity ( intent );
                            overridePendingTransition ( R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left );
//                            dialogAccount.dismiss();
                        }
                    } )
                    .show ( );


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
//                    Structbilling.Cumul_Pro_Elec_Duty=10000f;//                    Structbilling.SBM_No = key;
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

        }

//        submit.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Log.v("log_tag", "Panel Saved");
//                saveSig(v);
////                  --------------------------------------------------------------------------------------------------
//                submit.setVisibility(View.GONE);
//
//                if(prev_pref.equalsIgnoreCase("0_0_0") || prev_pref.equalsIgnoreCase("0_0_1")){//IMP_AMI
//                    Intent intent = new Intent(Signature_Activity.this, com.fedco.mbc.amigos.MainActivity.class);
//                    GSBilling.getInstance().setPrintSingle("<0x09>");//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.anim_slide_in_left,
//                            R.anim.anim_slide_out_left);
//                }else if(prev_pref.equalsIgnoreCase("0_1_0") || prev_pref.equalsIgnoreCase("0_1_1")){//IMP_REG
//                    Toast.makeText(Signature_Activity.this, "Under process", Toast.LENGTH_SHORT).show();
//                }else if(prev_pref.equalsIgnoreCase("0_2_0") || prev_pref.equalsIgnoreCase("0_2_1")){
////                    Intent intent = new Intent(Signature_Activity.this, SLPrintingMainActivity.class);
//                    Intent intent = new Intent(Signature_Activity.this,SLPrintingMainActivity.class);
//                    GSBilling.getInstance().setPrintSingle("");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.anim_slide_in_left,
//                            R.anim.anim_slide_out_left);
//                }else{
//                    Toast.makeText(Signature_Activity.this, "Unable to find Printer", Toast.LENGTH_SHORT).show();
//                }
//
//
////                    ---------------------------------------------------------------------------------------------
////                if (printer.equals("1")) {
////
////                    Intent intent = new Intent(Signature_Activity.this, PrintingMainActivity.class);
////                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                    startActivity(intent);
////                    overridePendingTransition(R.anim.anim_slide_in_left,
////                            R.anim.anim_slide_out_left);
////                } else {
////
////                    Intent intent = new Intent(Signature_Activity.this, SLPrintingMainActivity.class);
////                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                    intent.putExtra("printtype", print_type);
////                    startActivity(intent);
////                    overridePendingTransition(R.anim.anim_slide_in_left,
////                            R.anim.anim_slide_out_left);
////
////
////                }
//                //                Intent intent = new Intent(getApplicationContext(), PrintingMainActivity.class);
//                //                startActivity(intent);
//                //                overridePendingTransition(R.anim.anim_slide_in_right,
//                //                        R.anim.anim_slide_out_right);
//                //                Toast.makeText(getApplicationContext(), "Signature Saved", Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    Button.OnClickListener onButtonClick = new Button.OnClickListener ( ) {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            UtilAppCommon ucom = new UtilAppCommon ( );

            if (v == mClear) {
                Log.v ( "log_tag", "Panel Cleared" );
                mSignature.clear ( );
//                mGetSign.setEnabled(false);
//                if (enbleButton) {
//                    Toast.makeText(Signature_Activity.this, "Signature is compulsory", Toast.LENGTH_SHORT).show();
//                }
//                mTextView.setText("Signature is Compulsory" + "\n" + "Please sign here...");

            } else if (v == mGetSign) {
                Log.v ( "log_tag", "Panel Saved" );

                view.setDrawingCacheEnabled ( true );
                pic_name = ucom.UniqueCode ( getApplicationContext ( ) ) + "_" + Structconsmas.LOC_CD
                        + "_" + Structconsmas.MAIN_CONS_LNK_NO + "_" + ucom.billMonthConvert ( Structconsmas.Bill_Mon ) + "_sig";
                Structbilling.User_Sig_Img = ucom.UniqueCode ( getApplicationContext ( ) ) + "_" + Structconsmas.LOC_CD + "_" + Structconsmas.MAIN_CONS_LNK_NO + "_" + ucom.billMonthConvert ( Structconsmas.Bill_Mon ) + "_sig.jpg";
                StoredPath = DIRECTORY + pic_name + ".jpg";
                mSignature.save ( view, StoredPath );
                if (Home.Mflag.equals ( "Y" )) {

                    dbHelper4 = new DB ( getApplicationContext ( ) );
                    SD4 = dbHelper4.getWritableDatabase ( );
//                    dbHelper4.insertSequence("DCNumber", dcSeq);
                    //  dbHelper4.insertIntoBillingTable();
                    dbHelper4.insertIntoMPBillingTable ( );
//                    dbHelper4.insertSequence("BillNumber", billseq);                    new TextFileClass(Signature_Activity.this).execute();

                    Toast.makeText ( getApplicationContext ( ), "Welcome to metering", Toast.LENGTH_SHORT ).show ( );

                } else {

                    if (prev_pref.equalsIgnoreCase ( "0_0_0" ) || prev_pref.equalsIgnoreCase ( "0_0_1" )) {//IMP_AMI
                        Intent intent = new Intent ( Signature_Activity.this, com.fedco.mbc.amigos.MainActivity.class );
                        GSBilling.getInstance ( ).setPrintSingle ( "<0x09>" );
                        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                        startActivity ( intent );
                        overridePendingTransition ( R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left );
                    } else if (prev_pref.equalsIgnoreCase ( "0_1_0" ) || prev_pref.equalsIgnoreCase ( "0_1_1" )) {//IMP_REG
                        Toast.makeText ( Signature_Activity.this, "Under process", Toast.LENGTH_SHORT ).show ( );
                    } else if (prev_pref.equalsIgnoreCase ( "0_2_0" ) || prev_pref.equalsIgnoreCase ( "0_2_1" )) {
//                    Intent intent = new Intent(Signature_Activity.this, SLPrintingMainActivity.class);
                        Intent intent = new Intent ( Signature_Activity.this, SLPrintingMainActivity.class );
                        GSBilling.getInstance ( ).setPrintSingle ( "" );
                        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                        startActivity ( intent );
                        overridePendingTransition ( R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left );
                    } else {
                        Toast.makeText ( Signature_Activity.this, "Unable to find Printer", Toast.LENGTH_SHORT ).show ( );
                    }
                }

                //Toast.makeText(getApplicationContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(Signature_Activity.this, MainActivity.class);
//                startActivity(intent);
                // Calling the same class
                //recreate();

            } else if (v == mCancel) {
                Log.v ( "log_tag", "Panel Canceled" );
                // Calling the BillDetailsActivity

                Intent intent = new Intent ( Signature_Activity.this, Billing.class );
                GSBilling.getInstance ( ).setPrintSingle ( "<0x09>" );
                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity ( intent );
                overridePendingTransition ( R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left );

//                Intent intent = new Intent(SignatureActivity.this, Home_Activity.class);
//                startActivity(intent);
            }
        }
    };

    // previous to 19 03 2018
//    private String unitPercentageChk() {
//
//        double avg_Units = 0;
//        double unit_calculated = Double.valueOf(Structbilling.O_BilledUnit_Actual);
//
//        // 190318
//
//        long m_noofdays = Structbilling.dateDuration;
//
//        if (m_noofdays < 30l) {
//            m_noofdays = 30l;
//
//        }
//
//       if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {
//            avg_Units =(Double.parseDouble(Structconsmas.AVGUNITS1) + Double.parseDouble(Structconsmas.AVGUNITS2) + Double.parseDouble(Structconsmas.AVGUNITS3));
//        } else {
//            avg_Units =(Double.parseDouble(Structconsmas.PREV_AVG_UNIT));
//        }
//        avg_Units = (Double.parseDouble(Structbilling.O_Total_Bill) + Double.parseDouble(Structbilling.O_Arrear_Demand) + Structconsmas.Sundry_Allow_EC);
//
//
////      if (unit_calculated > 50) {
//        if (unit_calculated > 10) {// no use
//
//        if (Structconsmas.PICK_REGION.equalsIgnoreCase("10")) {
////                if ((Double.valueOf(Structbilling.O_BilledUnit_Actual)/Double.valueOf(m_noofdays) ) > ((avg_Units/30d) *Double.parseDouble(Structtariff.BPL_VALIDATION))) {
//            if ((Double.valueOf(Structconsmas.LOAD_SHED_HRS) / Double.valueOf(m_noofdays)) > (
//                    (avg_Units / 30d) * Double.parseDouble(Structtariff.BPL_VALIDATION))) {
//                return " - Consumption is 150 Times higher \n";//, "2 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
//            }
//
//
//        } else if (Structconsmas.PICK_REGION.equalsIgnoreCase("11")) {
////                if ((Double.valueOf(Structbilling.O_BilledUnit_Actual)/Double.valueOf(m_noofdays) ) > ((avg_Units/30d) *Double.parseDouble(Structtariff.IND_VALIDATION))) {
//            if ((Double.valueOf(Structconsmas.LOAD_SHED_HRS) / Double.valueOf(m_noofdays)) > ((avg_Units / 30d) * Double.parseDouble(Structtariff.IND_VALIDATION))) {
//                return " - Consumption is 150 Times higher \n";//, "2 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
//            }
//
//
//        } else if (Structconsmas.PICK_REGION.equalsIgnoreCase("12")) {
////                if ((Double.valueOf(Structbilling.O_BilledUnit_Actual)/Double.valueOf(m_noofdays) ) > ((avg_Units/30d) *Double.parseDouble(Structtariff.JBP_VALIDATION))) {
//            if ((Double.valueOf(Structconsmas.LOAD_SHED_HRS) / Double.valueOf(m_noofdays)) > ((avg_Units / 30d) * Double.parseDouble(Structtariff.JBP_VALIDATION))) {
//                return " - Consumption is 150 Times higher \n";//, "2 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
//            }
//        }
//
////            if ((Double.valueOf(Structbilling.O_BilledUnit_Actual)/Double.valueOf(m_noofdays) ) > ((avg_Units/30d) *1.25d)) {
////                return " - Consumption is 150 Times higher \n";//, "2 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
////            }
//
//        }
//
//        return "0";
//    }

    private String unitPercentageChk() {

        double avg_Units = 0;
        double unit_calculated = Double.valueOf ( Structbilling.O_BilledUnit_Actual );

//        long m_noofdays = Structbilling.OLD_dateDuration+Structbilling.NEW_dateDuration;
        long m_noofdays = Structbilling.dateDuration;

        if (m_noofdays < 30l) {
            m_noofdays = 30l;

        }

//        if (Structconsmas.SYSTEM_FLAG.equalsIgnoreCase("S")) {

      /*     Float.valueOf(Structbilling.O_Total_Bill) - Float.valueOf(Structbilling
            .O_Arrear_Demand) + Float.valueOf(Structconsmas.Sundry_Allow_EC))))
      */

//        } else {
//            avg_Units =(Double.parseDouble(Structconsmas.PREV_AVG_UNIT));
//        }


//      if (unit_calculated > 50) {


        if (Structbilling.Cumul_Units > 0) {
            avg_Units = Double.valueOf ( Structbilling.Cur_Bill_Total ) - Structbilling.saral_current_demand;
        } else {
            avg_Units = Double.valueOf ( Structbilling.Cur_Bill_Total );
        }

        if (Structconsmas.LOAD_SHED_HRS.equalsIgnoreCase ( "0" ) || Double.parseDouble ( Structconsmas
                .LOAD_SHED_HRS ) < 0.0) {
            return "0";
        } else {
//            if (StructSurveySecMaster.DISPLAY_CODE.equalsIgnoreCase("S")) {
//                avg_Units = Double.valueOf(Structbilling.O_BilledUnit_Actual);
//                if (unit_calculated > 10) {
//                /*
//               // no use in RMS & CCnB
//                    if (Structconsmas.PICK_REGION.equalsIgnoreCase("10")) {
//
//                        if ((Double.valueOf(Structbilling.O_BilledUnit_Actual) / Double.valueOf
//                                (m_noofdays)) * Double.parseDouble(Structtariff.BPL_VALIDATION) < ((avg_Units / 30d))) {
//                            return " - Consumption is 150 Times higher \n";//, "2 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
//                        }
//
//                    } else if (Structconsmas.PICK_REGION.equalsIgnoreCase("11")) {
//                        if ((Double.valueOf(Structbilling.O_BilledUnit_Actual) / Double.valueOf
//                                (m_noofdays))* Double.parseDouble(Structtariff.IND_VALIDATION) < (
//                                (avg_Units / 30d))) {
//                            return " - Consumption is 150 Times higher \n";//, "2 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
//                        }
//
//                    } else if (Structconsmas.PICK_REGION.equalsIgnoreCase("12")) {
//                        if ((Double.valueOf(Structbilling.O_BilledUnit_Actual) / Double.valueOf
//                                (m_noofdays)) * Double.parseDouble(Structtariff.JBP_VALIDATION) < (
//                                (avg_Units / 30d))) {
//                            return " - Consumption is 150 Times higher \n";//, "2 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
//                        }
//                    }
//                    */
//
//                /////
//                    if (Structconsmas.PICK_REGION.equalsIgnoreCase("10")) {
////                if ((Double.valueOf(Structbilling.O_BilledUnit_Actual)/Double.valueOf(m_noofdays) ) > ((avg_Units/30d) *Double.parseDouble(Structtariff.BPL_VALIDATION))) {
//                        if ((Double.valueOf(Structconsmas.LOAD_SHED_HRS) /30d ) *
//                                Double.parseDouble(Structtariff.BPL_VALIDATION) < (
//                                (avg_Units / Double.valueOf(m_noofdays)))) {
//                            return " - Consumption is 150 Times higher \n";//, "2 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
//                        }
//
//                    } else if (Structconsmas.PICK_REGION.equalsIgnoreCase("11")) {
////                if ((Double.valueOf(Structbilling.O_BilledUnit_Actual)/Double.valueOf(m_noofdays) ) > ((avg_Units/30d) *Double.parseDouble(Structtariff.IND_VALIDATION))) {
//                        if ((Double.valueOf(Structconsmas.LOAD_SHED_HRS) / 30d)*
//                                Double.parseDouble(Structtariff.JBP_VALIDATION) < ((avg_Units / Double.valueOf(m_noofdays)) )) {
//                            return " - Consumption is 150 Times higher \n";//, "2 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
//                        }
//
//                    } else if (Structconsmas.PICK_REGION.equalsIgnoreCase("12")) {
////                if ((Double.valueOf(Structbilling.O_BilledUnit_Actual)/Double.valueOf(m_noofdays) ) > ((avg_Units/30d) *Double.parseDouble(Structtariff.JBP_VALIDATION))) {
//                        if ((Double.valueOf(Structconsmas.LOAD_SHED_HRS) / 30d)*
//                                Double.parseDouble(Structtariff.IND_VALIDATION) < ((avg_Units / Double.valueOf(m_noofdays)) )) {
//                            return " - Consumption is 150 Times higher \n";//, "2 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
//                        }
//                    }
//                    ////
//                   /* if(Structconsmas.PICK_REGION.equalsIgnoreCase("10")){
//                        if ((Double.valueOf(Structbilling.O_BilledUnit_Actual)/Double.valueOf(m_noofdays) ) > ((avg_Units/30d) *Double.parseDouble(Structtariff.BPL_VALIDATION))) {
//                            return " - Consumption is 150 Times higher \n";//, "2 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
//                        }
//                    }else if(Structconsmas.PICK_REGION.equalsIgnoreCase("11")){
//                        if ((Double.valueOf(Structbilling.O_BilledUnit_Actual)/Double.valueOf(m_noofdays) ) > ((avg_Units/30d) *Double.parseDouble(Structtariff.IND_VALIDATION))) {
//                            return " - Consumption is 150 Times higher \n";//, "2 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
//                        }
//                    }else if(Structconsmas.PICK_REGION.equalsIgnoreCase("12")){
//                        if ((Double.valueOf(Structbilling.O_BilledUnit_Actual)/Double.valueOf(m_noofdays) ) > ((avg_Units/30d) *Double.parseDouble(Structtariff.JBP_VALIDATION))) {
//                            return " - Consumption is 150 Times higher \n";//, "2 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
//                        }
//                    }
//                    */
//
//                }
//            } else {
//                if (Structconsmas.PICK_REGION.equalsIgnoreCase("10")) {
////                if ((Double.valueOf(Structbilling.O_BilledUnit_Actual)/Double.valueOf(m_noofdays) ) > ((avg_Units/30d) *Double.parseDouble(Structtariff.BPL_VALIDATION))) {
//                    if ((Double.valueOf(Structconsmas.LOAD_SHED_HRS) /30d ) *
//                            Double.parseDouble(Structtariff.BPL_VALIDATION) < (
//                            (avg_Units / Double.valueOf(m_noofdays)))) {
//                        return " - Consumption is 150 Times higher \n";//, "2 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
//                    }
//
//                } else if (Structconsmas.PICK_REGION.equalsIgnoreCase("11")) {
////                if ((Double.valueOf(Structbilling.O_BilledUnit_Actual)/Double.valueOf(m_noofdays) ) > ((avg_Units/30d) *Double.parseDouble(Structtariff.IND_VALIDATION))) {
//                    if ((Double.valueOf(Structconsmas.LOAD_SHED_HRS) / 30d)*
//                            Double.parseDouble(Structtariff.JBP_VALIDATION) < ((avg_Units / Double.valueOf(m_noofdays)) )) {
//                        return " - Consumption is 150 Times higher \n";//, "2 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
//                    }
//
//                } else if (Structconsmas.PICK_REGION.equalsIgnoreCase("12")) {
////                if ((Double.valueOf(Structbilling.O_BilledUnit_Actual)/Double.valueOf(m_noofdays) ) > ((avg_Units/30d) *Double.parseDouble(Structtariff.JBP_VALIDATION))) {
//                    if ((Double.valueOf(Structconsmas.LOAD_SHED_HRS) / 30d)*
//                            Double.parseDouble(Structtariff.IND_VALIDATION) < ((avg_Units / Double.valueOf(m_noofdays)) )) {
//                        return " - Consumption is 150 Times higher \n";//, "2 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
//                    }
//                }
//
//            }

        }

//            if ((Double.valueOf(Structbilling.O_BilledUnit_Actual)/Double.valueOf(m_noofdays) ) > ((avg_Units/30d) *1.25d)) {
//                return " - Consumption is 150 Times higher \n";//, "2 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
//            }

        return "0";
    }

    public void saveSig(View view) {
        try {
            //            GestureOverlayView gestureView = (GestureOverlayView) findViewById(R.id.signaturePad);
            gestureView.setDrawingCacheEnabled ( true );
            Bitmap bm = Bitmap.createBitmap ( gestureView.getDrawingCache ( ) );

            Bitmap scaledbm = getResizedBitmap ( bm, 80, 384 );

            //  File f = new File(Environment.getExternalStorageDirectory()
            //          + File.separator + "/MBC/Images/" + GSBilling.getInstance().getKEYNAME() + "_" + Structconsmas.Consumer_Number + "_sig.jpg");
            //  Structbilling.User_Sig_Img = GSBilling.getInstance().getKEYNAME() + "_" + Structconsmas.Consumer_Number + "_sig.jpg";

            File f = new File ( Environment.getExternalStorageDirectory ( )
                    + File.separator + "/MBC/Images/" + appCommon.UniqueCode ( getApplicationContext ( ) ) + "_" + Structconsmas.LOC_CD + "_" + Structconsmas.MAIN_CONS_LNK_NO + "_sig.jpg" );
            Structbilling.User_Sig_Img = appCommon.UniqueCode ( getApplicationContext ( ) ) + "_" + Structconsmas.LOC_CD + "_" + Structconsmas.MAIN_CONS_LNK_NO + "_sig.jpg";

            if (f.exists ( )) {
                f.delete ( );
            } else {
                f.createNewFile ( );
            }

            FileOutputStream os = new FileOutputStream ( f );
            os = new FileOutputStream ( f );
            //compress to specified format (PNG), quality - which is ignored for PNG, and out stream
            scaledbm.compress ( Bitmap.CompressFormat.JPEG, 20, os );
            os.close ( );
        } catch (Exception e) {
            Log.v ( "Gestures", e.getMessage ( ) );
            e.printStackTrace ( );
        }
    }

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

    public class signature extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint ( );
        private Path path = new Path ( );

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF ( );

        public signature(Context context, AttributeSet attrs) {
            super ( context, attrs );
            paint.setAntiAlias ( true );
            paint.setColor ( Color.rgb ( 18, 138, 128 ) );
            paint.setStyle ( Paint.Style.STROKE );
            paint.setStrokeJoin ( Paint.Join.ROUND );
            paint.setStrokeWidth ( STROKE_WIDTH );
        }

        public void save(View v, String StoredPath) {
            Log.v ( "log_tag", "Width: " + v.getWidth ( ) );
            Log.v ( "log_tag", "Height: " + v.getHeight ( ) );
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap ( mContent.getWidth ( ), mContent.getHeight ( ), Bitmap.Config.RGB_565 );
            }
            Canvas canvas = new Canvas ( bitmap );
            try {
                // Output the file
                FileOutputStream mFileOutStream = new FileOutputStream ( StoredPath );
                v.draw ( canvas );

                // Convert the output file to Image such as .png
                getResizedBitmap ( bitmap, 280, 280 ).compress ( Bitmap.CompressFormat.JPEG, 50, mFileOutStream );
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, mFileOutStream);

                String bitmapstring;
                ByteArrayOutputStream bytes = new ByteArrayOutputStream ( );
                bitmap.compress ( Bitmap.CompressFormat.JPEG, 30, bytes );
                byte[] byteArray = bytes.toByteArray ( );
                bitmapstring = Base64.encodeToString ( byteArray, Base64.DEFAULT );

//
//                Intent intent = new Intent(SignatureActivity.this, Home_Activity.class);
//                intent.putExtra("imagePath", StoredPath);
//                startActivity(intent);
//                finish();
                mFileOutStream.flush ( );
                mFileOutStream.close ( );

            } catch (Exception e) {
                Log.v ( "log_tag", e.toString ( ) );
            }

        }

        public void clear() {
            path.reset ( );
            invalidate ( );
            mGetSign.setEnabled ( true );
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath ( path, paint );
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX ( );
            float eventY = event.getY ( );
            mGetSign.setEnabled ( true );
            mTextView.setText ( "Please Sign Here..." );
            enbleButton = true;
            switch (event.getAction ( )) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo ( eventX, eventY );
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect ( eventX, eventY );
                    int historySize = event.getHistorySize ( );
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX ( i );
                        float historicalY = event.getHistoricalY ( i );
                        expandDirtyRect ( historicalX, historicalY );
                        path.lineTo ( historicalX, historicalY );
                    }
                    path.lineTo ( eventX, eventY );
                    break;

                default:
                    debug ( "Ignored touch event: " + event.toString ( ) );
                    return false;
            }

            invalidate ( (int) (dirtyRect.left - HALF_STROKE_WIDTH), (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH) );
            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {

            Log.v ( "log_tag", string );

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min ( lastTouchX, eventX );
            dirtyRect.right = Math.max ( lastTouchX, eventX );
            dirtyRect.top = Math.min ( lastTouchY, eventY );
            dirtyRect.bottom = Math.max ( lastTouchY, eventY );
        }
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
                                curBillselect.getString ( 99 ) + "}" + curBillselect.getString ( 100 ) + "}" + curBillselect.getString ( 101 ) + "}" + curBillselect.getString ( 102 ) + "}" + curBillselect.getString ( 103 ) + "}" + curBillselect.getString ( 104 ) + "}" + curBillselect.getString ( 105 ) + "}" + curBillselect.getString ( 106 ) + "}" + curBillselect.getString ( 107 ) + "}" + curBillselect.getString ( 108 ) + "}" + curBillselect.getString ( 109 ) + "}" + curBillselect.getString ( 110 ) + "}" + curBillselect.getString ( 111 ) + "}" + curBillselect.getString ( 112 ) + "}" + curBillselect.getString ( 113 ) + "}" + curBillselect.getString ( 114 ) + "}" + curBillselect.getString ( 115 ) + "}" + curBillselect.getString ( 116 ) + "}" + curBillselect.getString ( 117 ) + "}" + curBillselect.getString ( 118 ) + "}" + curBillselect.getString ( 119 ) + "}" + curBillselect.getString ( 120 ) + "}" + curBillselect.getString ( 121 ) + "}" + curBillselect.getString ( 122 ) + "}" + curBillselect.getString ( 123 ) + "}" + curBillselect.getString ( 124 ) + "}" + curBillselect.getString ( 125 ) + "}" + curBillselect.getString ( 126 ) + "}" + curBillselect.getString ( 127 ) + "}" + curBillselect.getString ( 128 ) + "}" + curBillselect.getString ( 129 ) + "}" + curBillselect.getString ( 130 ) + "}" + curBillselect.getString ( 131 ) + "}" + curBillselect.getString ( 132 ) + "}" + curBillselect.getString ( 133 ) + "}" + curBillselect.getString ( 134 ) + "}" + curBillselect.getString ( 135 ) + "}" + curBillselect.getString ( 136 ) + "}" + curBillselect.getString ( 137 ) + "}" + curBillselect.getString ( 138 ) + "}" + curBillselect.getString ( 139 ) + "}" + curBillselect.getString ( 140 ) + "}" + curBillselect.getString ( 141 ) + "}" + curBillselect.getString ( 142 ) + "}" + curBillselect.getString ( 143 ) + "}" + curBillselect.getString ( 144 ) + "}" + curBillselect.getString ( 145 ) + "}" + curBillselect.getString ( 146 ) + "}" + curBillselect.getString ( 147 ) + "}" + curBillselect.getString ( 148 ) + "}" + curBillselect.getString ( 149 ) + "}" + curBillselect.getString ( 150 ) + "}" + curBillselect.getString ( 151 ) + "}" + curBillselect.getString ( 152 ) + "}" + curBillselect.getString ( 153 ) + "}" + curBillselect.getString ( 154 ) + "}" + curBillselect.getString ( 155 ) + "}" + curBillselect.getString ( 156 ) + "}" + curBillselect.getString ( 157 ) + "}" + curBillselect.getString ( 158 ) + "}" + curBillselect.getString ( 159 ) + "}" + curBillselect.getString ( 160 ) + "}" + curBillselect.getString ( 161 ) + "}" + curBillselect.getString ( 162 ) + "}" + curBillselect.getString ( 163 ) + "}" + curBillselect.getString ( 164 ) + "}" + curBillselect.getString ( 165 ) + "}" + curBillselect.getString ( 166 ) + "}" + curBillselect.getString ( 167 ) + "}" + curBillselect.getString ( 168 ) + "}" + curBillselect.getString ( 169 ) + "}" + curBillselect.getString ( 170 ) + "}" + curBillselect.getString ( 171 ) + "}" + curBillselect.getString ( 172 ) + "}" + curBillselect.getString ( 173 ) + "}" + curBillselect.getString ( 174 ) + "}" + curBillselect.getString ( 175 ) + "}" + curBillselect.getString ( 176 ) + "}" + curBillselect.getString ( 177 ) + "}" + curBillselect.getString ( 178 ) + "}" + curBillselect.getString ( 179 ) + "}" + curBillselect.getString ( 180 ) + "}" + curBillselect.getString ( 181 ) + "}" + curBillselect.getString ( 182 ) + "}" + curBillselect.getString ( 183 ) + "}" + curBillselect.getString ( 184 ) + "}" + curBillselect.getString ( 185 ) + "}" + curBillselect.getString ( 186 ) + "}" + curBillselect.getString ( 187 ) + "}" + curBillselect.getString ( 188 ) + "}" + curBillselect.getString ( 189 ) + "}" + curBillselect.getString ( 190 ) + "}" + curBillselect.getString ( 191 ) + "}" + curBillselect.getString ( 192 ) + "}" + curBillselect.getString ( 193 ) + "}" + curBillselect.getString ( 194 ) + "}" + curBillselect.getString ( 195 ) + "}" + curBillselect.getString ( 196 ) + "}" + curBillselect.getString ( 197 ) + "}" + curBillselect.getString ( 198 ) + "}" + curBillselect.getString ( 199 ) + "}" + curBillselect.getString ( 200 ) + "}" + curBillselect.getString ( 201 ) + "}" + curBillselect.getString ( 202 ) + "}" + curBillselect.getString ( 203 ) + "}" + curBillselect.getString ( 204 ) + "}" + curBillselect.getString ( 205 ) + "}" + curBillselect.getString ( 206 ) + "}" + curBillselect.getString ( 207 ) + "}" + curBillselect.getString ( 208 ) + "}" + curBillselect.getString ( 209 ) + "}" + curBillselect.getString ( 210 ) + "}" + curBillselect.getString ( 211 ) + "}" + curBillselect.getString ( 212 ) + "}" +
                                curBillselect.getString ( 213 ) + "}" + curBillselect.getString ( 214 ) + "}" + curBillselect.getString ( 215 ) + "}" +
                                curBillselect.getString ( 216 ) );


                        mylist1.add ( curBillselect.getString ( 60 ) + "$" + curBillselect.getString ( 0 ) + "$" + curBillselect.getString ( 5 ) + "$" + curBillselect.getString ( 61 ) + "$" + curBillselect.getString ( 11 ) + "$" + curBillselect.getString ( 62 ) + "$" +
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
                                curBillselect.getString ( 201 ) + "$" + curBillselect.getString ( 83 ) + "$" + curBillselect.getString ( 179 ) + "$" + curBillselect.getString ( 85 ) + "$" + curBillselect.getString ( 86 ) + "$" + curBillselect.getString ( 169 ) + "$" +
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

                Signature_Activity.this.runOnUiThread ( new Runnable ( ) {

                    @Override
                    public void run() {
                        progress.dismiss ( );
                        new PostClass ( Signature_Activity.this ).execute ( );
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

    private void deleteFile(String inputPath, String inputFile) {
        try {
            // delete the original file
            new File ( inputPath + inputFile ).delete ( );


        } catch (Exception e) {
//            Log.e(getApplicationContext(), "SLPrintAct", "tag" + e.getMessage());
        }
    }

    public void generateLastREC(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File ( Environment.getExternalStorageDirectory ( ), "mrcn/last/" );
            if (!root.exists ( )) {
                root.mkdirs ( );
            }
            File gpxfile = new File ( root, sFileName );
            FileWriter writer = new FileWriter ( gpxfile );
            int length = sBody.size ( );
            for (int i = 0; i < length; i++) {
//                System.out.println("selqwer1234 " + sBody.get(i));

                writer.append ( sBody.get ( i ).toString ( ) );
                writer.append ( "\n" );
            }

            writer.flush ( );
            writer.close ( );
            System.out.println ( "success file" );

//                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace ( );
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
//                Log.e(getApplicationContext(), "SLPrintAct", "mtr_img" + curBillImg.getString(0) + "sig_img" + curBillImg.getString(1));            }

                ArrayList <String> stringArrayList = new ArrayList <String> ( );
                for (int j = 0; j < mylistimagename.size ( ); j++) {

                    stringArrayList.add ( Environment.getExternalStorageDirectory ( ) + "/MBC/" + mylistimagename.get ( j ) ); //add to arraylist
                }
                String[] files = stringArrayList.toArray ( new String[stringArrayList.size ( )] );
                String[] file = {Zip, signaturePathDes, photoPathDes};

                zipFolder ( ZipCopyPath, ZipDesPath );
                GSBilling.getInstance ( ).setFinalZipName ( ZipDesPathdup );

            }
        }

            @Override
            protected Boolean doInBackground (String...params){
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
                        Signature_Activity.this.runOnUiThread ( new Runnable ( ) {

                            @Override
                            public void run() {
                                progress.dismiss ( );
                                Toast.makeText ( Signature_Activity.this, "Internaly Stored Due to No Connectivity", Toast.LENGTH_LONG ).show ( );
                                Intent intent = new Intent ( Signature_Activity.this, BillingtypesActivity.class );
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
                        Signature_Activity.this.runOnUiThread ( new Runnable ( ) {

                            @Override
                            public void run() {
                                progress.dismiss ( );
                                Toast.makeText ( Signature_Activity.this, " Successfully Uploaded to Server ", Toast.LENGTH_LONG ).show ( );
                                Intent intent = new Intent ( Signature_Activity.this, BillingtypesActivity.class );
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

            protected void onPostExecute () {
                progress.dismiss ( );
                new File ( Environment.getExternalStorageDirectory ( ).toString ( ) + GSBilling.getInstance ( ).getFinalZipName ( ) + ".zip" ).delete ( );
                Intent intent = new Intent ( Signature_Activity.this, BillingtypesActivity.class );
                startActivity ( intent );
                overridePendingTransition ( R.anim.anim_slide_in_left, R.anim.anim_slide_out_left );

            }
        }



        private void dismissProgressDialog() {
            if (sDialog != null && sDialog.isShowing()) {
                sDialog.dismiss();
            }
        }
        public static void zipFolder(String inputFolderPath, String outZipPath) {
            try {
                FileOutputStream fos = new FileOutputStream(outZipPath);//            GSBilling.getInstance().setFinalZipName();
                ZipOutputStream zos = new ZipOutputStream(fos);
                File srcFile = new File(inputFolderPath);
                File[] files = srcFile.listFiles();
                android.util.Log.d("", "Zip directory: " + srcFile.getName());
                for (int i = 0; i < files.length; i++) {
                    android.util.Log.d("", "Adding file: " + files[i].getName());
                    byte[] buffer = new byte[1024];
                    FileInputStream fis = new FileInputStream(files[i]);                zos.putNextEntry(new ZipEntry(files[i].getName()));
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                    fis.close();
                }
                System.out.println("helloooo" + srcFile.delete());
                zos.close();
            } catch (IOException ioe) {
                android.util.Log.e("", ioe.getMessage());
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
            Intent intent=new Intent(Signature_Activity.this, com.fedco.mbc.activity.MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }

    }

