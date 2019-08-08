package com.fedco.mbc.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.fedco.mbc.CustomClasses.ReceiptModel;
import com.fedco.mbc.R;
import com.fedco.mbc.amigos.MainActivityCollectionPrint;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.bluetoothprinting.GlobalPool;
import com.fedco.mbc.model.Structcollection;
import com.fedco.mbc.model.Structcolmas;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.CardUtils;
import com.fedco.mbc.utils.CommonFunctionClass;
import com.fedco.mbc.utils.DecimalDigitsInputFilter;
import com.fedco.mbc.utils.GPSTracker;
import com.fedco.mbc.utils.HttpFileUpload;
import com.fedco.mbc.utils.URLS;
import com.fedco.mbc.utils.UtilAppCommon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.net.ssl.HttpsURLConnection;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PaymentType extends AppCompatActivity implements LogoutListaner {

    static final int DATE_PICKER_ID = 1111;
    static final int DATE_PICKER_ID1 = 2222;
    int year, month, day;
    int _tempCount = 0;

    String str_currentdate, paymentmode = "C", str_bank_name, str_bank_name_id;

    EditText etCash, etBankName, etChekNo, etMobNo, etDD, etdemanddraft;
    Spinner spinner_BankName, spinner_indcident;
    TextView etChekDate;
    Button btnDate, btnPrint;
    RadioGroup rgPaymentType;
    Integer minPay = 1;
    Date date1, date2, date3, date4;
    SessionManager session;
    LinearLayout lloption;
    GPSTracker gps;
    double latitude;
    double longitude;
    UtilAppCommon utillComn;
    Calendar c;
    Date dateCurrent;
    Date dateLast;
    DB dbHelper, databaseHelper;
    SQLiteDatabase SD;
    String printer_catergory, printer_mfg, printer_roll, prev_pref, slot1;
    public String typePay;

    Cursor bank_Name;
    ArrayList <String> bank_name_id_list, bank_name_list;
    ArrayAdapter bank_name_adapter;
    private int al_banksize;
    private int maxLength = 6;
    private int maxLengthCash = 6;
    private int maxLengthCheque = 11;
    private int maxLengthpos = 11;
    private long maxnumber = 9999999999l;
    SweetAlertDialog sDialog;
    private ProgressDialog progress;
    String ZipCopyPath = Environment.getExternalStorageDirectory ( ) + "/MBC/Downloadsingular/";
    String ZipDesPath;
    String ZipDesPathdup;
    SQLiteDatabase SD2, SD3, SD4;
    DB dbHelper2, dbHelper3, dbHelper4;
    UtilAppCommon comApp;
    String key;
    RadioButton rdcash, rdcheque, repos;
    final DecimalFormat numberFormat = new DecimalFormat ( "########0.00" );
    private static final String[] paths = {"Bill", "Bypass", "Conn Fee (Zone)", "Loss Of Revenue", "Reconnection Fee", "Security Deposit", "Meter Replacement Payment"};
    private static final String[] path = {"Bill", "Arrears", "Burnt Meter Replacement Fee", "Bypass", "Conn Fee (JEA)", "Conn Fee (Zone)", "Fixed Fee Correction", "Loss Of Revenue", "Meter Maintenance Fee Correction", "Preload", "Reconnection Fee", "Final Bill", "Security Deposit", "Meter Replacement Payment"};
    String strDate, strtime, strBillMonth;

    String amount;
    String transactionStatus ;
    String transactionReason ;
    String pan ;
    String rrn ;
    String terminalId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_payment_type );

       // savedInstanceState.setClassLoader(getClass().getClassLoader());

        session = new SessionManager ( getApplicationContext ( ) );
        utillComn = new UtilAppCommon ( );
        GSBilling.getInstance ( ).setPayType ( "CASH" );

        ((GlobalPool) getApplication ( )).registerSessionListaner ( this );
        ((GlobalPool) getApplication ( )).startUserSession ( );

        key = GSBilling.getInstance ( ).MRID;
        typePay = GSBilling.getInstance ( ).CON_TYPE;

        this.ZipDesPathdup = "/MBC/" + key + "_col_" + GSBilling.getInstance ( ).captureDatetime ( );
        this.ZipDesPath = Environment.getExternalStorageDirectory ( ) + "/MBC/" + key + "_col_" + GSBilling.getInstance ( ).captureDatetime ( ) + ".zip";

        HashMap <String, String> user = session.getUserDetails ( );
        rdcash = findViewById ( R.id.RBCash );
        rdcheque = findViewById ( R.id.RBChe );
        spinner_indcident = findViewById ( R.id.indcident );

        lloption = findViewById ( R.id.lloptional );
        etCash = findViewById ( R.id.ETAmount );
//        etCash.setText(etCash.toString().replaceAll("^0+(?=\\d+$)",""));
        etBankName = findViewById ( R.id.ETBank );
        etChekNo = findViewById ( R.id.ETChNo );
        etMobNo = findViewById ( R.id.ETMobile );
        etDD = findViewById ( R.id.ETDDNo );
        etdemanddraft = findViewById ( R.id.ETDemadraftNo );
        etChekDate = findViewById ( R.id.ETDate );
        btnDate = findViewById ( R.id.BTNDATE );
        btnPrint = findViewById ( R.id.BTNPrint );
        spinner_BankName = findViewById ( R.id.SPINNERBank );
//
        if (GSBilling.getInstance ( ).CON_TYPE.equalsIgnoreCase ( "PREPAID" )) {
            rdcheque.setVisibility ( View.GONE );
//            spinner_indcident.setVisibility(View.GONE);
            ArrayAdapter <String> adapter1 = new ArrayAdapter <String> ( PaymentType.this,
                    android.R.layout.simple_spinner_item, path );
            adapter1.setDropDownViewResource ( android.R.layout.simple_spinner_dropdown_item );
            spinner_indcident.setAdapter ( adapter1 );
            spinner_indcident.setAdapter ( adapter1 );
        } else {
            ArrayAdapter <String> adapter = new ArrayAdapter <String> ( PaymentType.this,
                    android.R.layout.simple_spinner_item, paths );
            adapter.setDropDownViewResource ( android.R.layout.simple_spinner_dropdown_item );
            spinner_indcident.setAdapter ( adapter );
        }
//        minPay = getIntent().getFloatExtra("MINPAY", 00.0f);
        // lloption.setVisibility(View.GONE);
        etCash.setVisibility ( View.VISIBLE );
        etBankName.setVisibility ( View.GONE );
        etChekNo.setVisibility ( View.GONE );
        etDD.setVisibility ( View.GONE );
        etdemanddraft.setVisibility ( View.GONE );
        etMobNo.setVisibility ( View.VISIBLE );
        etChekDate.setVisibility ( View.GONE );
        btnDate.setVisibility ( View.GONE );
        spinner_BankName.setVisibility ( View.GONE );
        etCash.setFilters ( new InputFilter[]{new DecimalDigitsInputFilter ( etCash, 8, 2 )} );
        etCash.setInputType ( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL );

        //-------------for Phed process-------------------//
        String strmob = GSBilling.getInstance ( ).MOBILENO;
//        if(GSBilling.getInstance().MOBILENO.equals(" ")||GSBilling.getInstance().MOBILENO.isEmpty()||GSBilling.getInstance().MOBILENO.equalsIgnoreCase("null")){
//            etMobNo.setText("");
//        }else {
//            etMobNo.setText(GSBilling.getInstance().MOBILENO);
//        }
        Calendar cal = Calendar.getInstance ( );
        SimpleDateFormat sdf = new SimpleDateFormat ( "dd-MM-yyyy" );
        SimpleDateFormat fds = new SimpleDateFormat ( "kk:mm" );
        SimpleDateFormat formatter = new SimpleDateFormat ( "dd MMMM yyyy" );
        String currentDateTimeString = DateFormat.getDateTimeInstance ( ).format ( new Date ( ) );
        strDate = sdf.format ( cal.getTime ( ) );
        strtime = fds.format ( cal.getTime ( ) );

        Date date = new Date ( );
        String billMonth = formatter.format ( date );
        String[] str = billMonth.split ( " " );
        String month = str[1].substring ( 0, 3 );
        String year = str[2].substring ( 2 );
        strBillMonth = month + "-" + year;

        etCash.addTextChangedListener ( new TextWatcher ( ) {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String enteredString = s.toString ( );
                if (enteredString.startsWith ( "0" )) {
                    Toast.makeText ( PaymentType.this, "should not starts with zero(0)", Toast.LENGTH_SHORT ).show ( );
                    if (enteredString.length ( ) > 0) {

                        etCash.setText ( enteredString.substring ( 1 ) );

                    } else {
                        etCash.setText ( "" );
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        } );

        GSBilling.getInstance ( ).ColDate = strDate;
        Structcolmas.COL_TIME = strtime;

        // etCash.setHint(String.format("%.2f", minPay));
        rgPaymentType = findViewById ( R.id.RGType );
        rgPaymentType.setOnCheckedChangeListener (
                new RadioGroup.OnCheckedChangeListener ( ) {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if (checkedId == R.id.RBCash) {
                            paymentmode = "C";
                            GSBilling.getInstance ( ).setPayType ( "CASH" );
                            lloption.setVisibility ( View.VISIBLE );
                            etCash.setVisibility ( View.VISIBLE );
//                            etCash.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthCash)});
//                            etCash.setInputType(InputType.TYPE_CLASS_NUMBER);
                            etCash.setFilters ( new InputFilter[]{new DecimalDigitsInputFilter ( etCash, 8, 2 )} );
                            etCash.setInputType ( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL );
                            etMobNo.setVisibility ( View.VISIBLE );
                            etCash.setText ( "" );
//                            etMobNo.setText("");
                            // etCash.setHint(String.format("%.2f", minPay));
                            etBankName.setVisibility ( View.GONE );
                            spinner_BankName.setVisibility ( View.GONE );
                            etChekNo.setVisibility ( View.GONE );
                            etDD.setVisibility ( View.GONE );
                            etdemanddraft.setVisibility ( View.GONE );
                            etChekDate.setVisibility ( View.GONE );
                            btnDate.setVisibility ( View.GONE );
//                            if(etMobNo.getText().toString().equals("")||etMobNo.getText().toString().isEmpty()){
                            etMobNo.setText ( "" );
//                            }else{
//                                etMobNo.setText(GSBilling.getInstance().MOBILENO);
//                            }

                        } else if (checkedId == R.id.RBChe) {
                            paymentmode = "Q";
                            GSBilling.getInstance ( ).setPayType ( "CHEQUE" );
                            lloption.setVisibility ( View.VISIBLE );
                            etCash.setVisibility ( View.VISIBLE );
//                            etCash.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthCheque)});
                            etCash.setFilters ( new InputFilter[]{new DecimalDigitsInputFilter ( etCash, 8, 2 )} );
                            etCash.setInputType ( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL );
                            etMobNo.setVisibility ( View.VISIBLE );
                            // etCash.setHint(String.format("%.2f", minPay));
//                            etBankName.setVisibility(View.VISIBLE);
                            spinner_BankName.setVisibility ( View.VISIBLE );
                            etChekNo.setVisibility ( View.VISIBLE );
                            etChekDate.setVisibility ( View.VISIBLE );
                            btnDate.setVisibility ( View.VISIBLE );
                            etDD.setVisibility ( View.GONE );
                            etdemanddraft.setVisibility ( View.GONE );
                            etCash.setText ( "" );
                            etMobNo.setText ( "" );
                            etBankName.setText ( "" );
                            etChekNo.setText ( "" );
//                            if(etMobNo.getText().toString().equals("")||etMobNo.getText().toString().isEmpty()){
//                                etMobNo.setText("");
//                            }else{
//                                etMobNo.setText(GSBilling.getInstance().MOBILENO);
//                            }
//                            etMobNo.setText(GSBilling.getInstance().MOBILENO);
                        } else if (checkedId == R.id.RBDD) {
                            paymentmode = "P";
                            GSBilling.getInstance ( ).setPayType ( "POS" );
                            lloption.setVisibility ( View.VISIBLE );
                            etCash.setVisibility ( View.VISIBLE );
                            etCash.setFilters ( new InputFilter[]{new DecimalDigitsInputFilter ( etCash, 8, 2 )} );
                            etCash.setInputType ( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL );
                            etMobNo.setVisibility ( View.VISIBLE );
                            etDD.setVisibility ( View.VISIBLE );
                            // etCash.setHint(String.format("%.2f", minPay));
                            etBankName.setVisibility ( View.GONE );
                            spinner_BankName.setVisibility ( View.GONE );
                            etChekNo.setVisibility ( View.GONE );
                            etdemanddraft.setVisibility ( View.GONE );
                            etChekDate.setVisibility ( View.GONE );
                            btnDate.setVisibility ( View.GONE );
                            etCash.setText ( "" );
                            etMobNo.setText ( "" );
                            etBankName.setText ( "" );
                            etDD.setText ( "" );
//                            if(etMobNo.getText().toString().equals("")||etMobNo.getText().toString().isEmpty()){
//                                etMobNo.setText("");
//                            }else{
//                                etMobNo.setText(GSBilling.getInstance().MOBILENO);
//                            }
//                            etMobNo.setText(GSBilling.getInstance().MOBILENO);
                        } else if (checkedId == R.id.RBDemanddraft) {
                            paymentmode = "D";
                            GSBilling.getInstance ( ).setPayType ( "DD" );
                            lloption.setVisibility ( View.VISIBLE );
                            etCash.setVisibility ( View.VISIBLE );
                            etCash.setFilters ( new InputFilter[]{new DecimalDigitsInputFilter ( etCash, 8, 2 )} );
                            etCash.setInputType ( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL );
                            etMobNo.setVisibility ( View.VISIBLE );
                            etDD.setVisibility ( View.GONE );
                            etdemanddraft.setVisibility ( View.VISIBLE );
                            // etCash.setHint(String.format("%.2f", minPay));
//                            etBankName.setVisibility(View.VISIBLE);
                            spinner_BankName.setVisibility ( View.VISIBLE );
                            etChekNo.setVisibility ( View.GONE );
                            etChekDate.setVisibility ( View.VISIBLE );
                            btnDate.setVisibility ( View.VISIBLE );
                            etCash.setText ( "" );
                            etMobNo.setText ( "" );
                            etBankName.setText ( "" );
                            etDD.setText ( "" );
//                            if(etMobNo.getText().toString().equals("")||etMobNo.getText().toString().isEmpty()){
//                                etMobNo.setText("");
//                            }else{
//                                etMobNo.setText(GSBilling.getInstance().MOBILENO);
//                            }
                        }

                    }


                }
        );

//        bank_name_list.clear();
//        bank_name_id_list.clear();

        new BankName ( PaymentType.this ).execute ( );
        spinner_BankName.setOnItemSelectedListener ( new AdapterView.OnItemSelectedListener ( ) {
            @Override
            public void onItemSelected(AdapterView <?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                str_bank_name = bank_name_list.get ( position );
                str_bank_name_id = bank_name_id_list.get ( position );
                al_banksize = bank_name_list.size ( );

                if (spinner_BankName.getSelectedItem ( ).toString ( ).equalsIgnoreCase ( "Others" )) {

                    etBankName.setVisibility ( View.VISIBLE );

                } else if (al_banksize > 1 && (bank_name_list.size ( ) - 1) < al_banksize) {
                    etBankName.setVisibility ( View.GONE );
                } else {
                    etBankName.setVisibility ( View.GONE );
                }
            }

            @Override
            public void onNothingSelected(AdapterView <?> parent) {
                // TODO Auto-generated method stub
            }
        } );

        btnDate.setOnClickListener ( new View.OnClickListener ( ) {

            @Override
            public void onClick(View v) {

                // On button click show datepicker dialog

                if (paymentmode == "Q") {
                    showDialog ( DATE_PICKER_ID );
                } else {
                    showDialog ( DATE_PICKER_ID1 );
                }

            }

        } );
        btnPrint.setOnClickListener ( new View.OnClickListener ( ) {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {




//                if (etMobNo.getText().toString().length() == 0) {
//                    etMobNo.setText("0");
////                    etMobNo.setError("Can Not be Empty");
////                    Toast.makeText(PaymentType.this, "Mobile Number Cannot Be Empty", Toast.LENGTH_SHORT).show();
//                }
                    if (etCash.getText ( ).toString ( ).length ( ) == 0) {
                        etCash.setError ( "Cash Field Cannot be empty" );
                        Toast.makeText ( PaymentType.this, "Cash Field Cannot be empty", Toast.LENGTH_SHORT ).show ( );
                    }
//                else if (etChekNo.getText().toString().length() == 0 && etChekNo.getText().toString().length() < 6) {
//                    etChekNo.setError("Cheque No Can not be Empty");
//                    Toast.makeText(PaymentType.this, "Cheque No Can not be Empty", Toast.LENGTH_SHORT).show();
//                }
//             else if (etdemanddraft.getText().toString().length() == 0 && etdemanddraft.getText().toString().length() < 26) {
//                    etdemanddraft.setError("DD  No Cannot be Empty");
//                    Toast.makeText(getApplicationContext(), "DD  No Cannot be Empty", Toast.LENGTH_SHORT).show();
//                }
//             else if (etDD.getText().toString().length() == 0 && etDD.getText().toString().length() < 26) {
//                    etDD.setError("pos receipt No Cannot be Empty");
//                }
//                else if (( etMobNo.getText().toString().length()==0 || etMobNo.getText().toString().length() < 11)|| (etMobNo.getText().toString()).equals("99999999999") ||  (etMobNo.getText().toString()).equals("09999999999") ||  (etMobNo.getText().toString()).equals("00999999999")||  (etMobNo.getText().toString()).equals("00000000000")) {
//                    etMobNo.setError("Please Enter a valid Mobile no");
//                    Toast.makeText(PaymentType.this, "Please Enter a Valid Mobile Number", Toast.LENGTH_SHORT).show();
//                }
                    else if (spinner_BankName.equals ( "" ) || spinner_BankName.equals ( "null" )) {
                        Toast.makeText ( PaymentType.this, "Please download master bank", Toast.LENGTH_SHORT ).show ( );
                    } else {
//                    CommonFunctionClass commonFunctionClass=new CommonFunctionClass();
//                    LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    View customView =null;
//                    customView = layoutInflater.inflate(R.layout.number_dialogue, null);
//                    TextView txtamount = (TextView) customView.findViewById(R.id.amounttxt);
//                    Button btnno =(Button)customView.findViewById(R.id.btn_no);
//                    Button btnyes =(Button)customView.findViewById(R.id.btn_yes);
//                    txtamount.setText(commonFunctionClass.convert(Long.parseLong(etCash.getText().toString())).toString());
//                    AlertDialog.Builder alert = new AlertDialog.Builder(PaymentType.this);
//                    // this is set the view from XML inside AlertDialog
//                    alert.setView(customView);
//                    final AlertDialog dialog = alert.create();
//                    dialog.show();
//
//                    btnno.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            dialog.cancel();
//                        }
//                    });
//                    btnyes.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//
//                        }
//                    });
                        Structcolmas.MOB_NO = etMobNo.getText ( ).toString ( ).trim ( );
//                    Structcolmas.MOB_NO = GSBilling.getInstance().MOBILENO.trim();

                        if (!Structcolmas.SESSION_KEY.isEmpty ( ) && Structcolmas.SESSION_KEY != null) {
                            Structcolmas.SESSION_KEY = Structcolmas.SESSION_KEY;
                        } else {
                            UtilAppCommon appCommon = new UtilAppCommon ( );
                            Structcolmas.SESSION_KEY = String.valueOf ( appCommon.findSequence ( getApplicationContext
                                    ( ), "SessionNo" ) );
                        }

                        dbHelper = new DB ( PaymentType.this );
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

                        try {
                            String Datetime;
                            Float cashET = Float.valueOf ( etCash.getText ( ).toString ( ) );
                            //	CBillling cb=new CBillling();
                            Calendar c = Calendar.getInstance ( );
                            SimpleDateFormat dateformat = new SimpleDateFormat ( "HHmmss" );
                            SimpleDateFormat month_date = new SimpleDateFormat ( "MMMM-yy" );
                            SimpleDateFormat date = new SimpleDateFormat ( "dd-MM-yyyy" );
                            String month_name = month_date.format ( c.getTime ( ) );
                            String dateee = date.format ( c.getTime ( ) );
                            Datetime = dateformat.format ( c.getTime ( ) );
                            Structcolmas.COL_DATE = Datetime;
                            Structcolmas.PYMNT_MODE = paymentmode;
                            Structcolmas.MR_ID = "";
                            Structcolmas.MAIN_LINK_CONS_NO = Structcollection.IVRS_NO;
                            Structcolmas.LOC_CD = Structcollection.LOC_CD;

                            switch (paymentmode) {

                                case "C": {
                                    if (etCash.getText ( ).toString ( ).equals ( null )) {
                                        etCash.setError ( "Cash Field Cannot Be Empty" );
                                        Toast.makeText ( PaymentType.this, "Cash Field Cannot Be Empty", Toast.LENGTH_SHORT ).show ( );
                                    } else if ((etMobNo.getText ( ).toString ( ).length ( ) == 0 || etMobNo.getText ( ).toString ( ).length ( ) < 11) || (etMobNo.getText ( ).toString ( )).equals ( "99999999999" ) || (etMobNo.getText ( ).toString ( )).equals ( "09999999999" ) || (etMobNo.getText ( ).toString ( )).equals ( "00999999999" ) || (etMobNo.getText ( ).toString ( )).equals ( "00000000000" )) {
                                        etMobNo.setError ( "Please Enter a Valid Mobile no" );
                                        Toast.makeText ( PaymentType.this, "Please Enter a Valid Mobile Number", Toast.LENGTH_SHORT ).show ( );
                                    } else {
                                        if ((cashET > Float.parseFloat ( GSBilling.getInstance ( ).Wallet )) && GSBilling.getInstance ( ).Agent.equalsIgnoreCase ( "1" )) {
//                                        etCash.setError("Minimum Collection Amount cannot be NGN.0");
//                                        Toast.makeText(PaymentType.this, "Minimum Collection Amount cannot be NGN.0", Toast.LENGTH_SHORT).show();
                                            sDialog = new SweetAlertDialog ( PaymentType.this, SweetAlertDialog.ERROR_TYPE );
                                            sDialog.setTitleText ( "Error" );
                                            sDialog.setContentText ( "wallet amount is less for collection" );
                                            sDialog.show ( );
                                            sDialog.setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation ( );
//                                                Intent intent = new Intent(PaymentType.this, Collection.class);
//                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                                startActivity(intent);
//                                                overridePendingTransition(R.anim.anim_slide_in_left,
//                                                        R.anim.anim_slide_out_left);

                                                }
                                            } );
                                        } else {
                                            Structcolmas.AMOUNT = numberFormat.format ( Double.parseDouble ( etCash.getText ( ).toString ( ).replaceAll ( "^0+(?=\\d+$)", "" ) ) );

                                            System.out.println ( "This is the amount stored " + Structcolmas.AMOUNT );
                                            Structcolmas.CHEQUE_NO = "";
                                            Structcolmas.CH_DATE = "";
                                            Structcolmas.BANK_NAME = "";
//                                        etCash.setText(etCash.toString().replaceAll("^0+(?=\\d+$)",""));
                                            String str = spinner_indcident.getSelectedItem ( ).toString ( );
                                            if (str.equals ( "Arrears" )) {

                                                GSBilling.getInstance ( ).Payment_type = "1";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Arrears";

                                            }
                                            if (str.equals ( "Burnt Meter Replacement Fee" )) {

                                                GSBilling.getInstance ( ).Payment_type = "2";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Burnt Meter Replacement Fee";

                                            }


                                            if (str.equals ( "Bypass" )) {

                                                GSBilling.getInstance ( ).Payment_type = "3";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Bypass";

                                            }
                                            if (str.equals ( "Conn Fee (JEA)" )) {

                                                GSBilling.getInstance ( ).Payment_type = "4";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Conn Fee (JEA)";

                                            }
                                            if (str.equals ( "Conn Fee (Zone)" )) {

                                                GSBilling.getInstance ( ).Payment_type = "5";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Conn Fee (Zone)";

                                            }
                                            if (str.equals ( "Fixed Fee Correction" )) {

                                                GSBilling.getInstance ( ).Payment_type = "6";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Fixed Fee Correction";

                                            }


                                            if (str.equals ( "Loss Of Revenue" )) {

                                                GSBilling.getInstance ( ).Payment_type = "7";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Loss Of Revenue";

                                            }
                                            if (str.equals ( "Meter Maintenance Fee Correction" )) {

                                                GSBilling.getInstance ( ).Payment_type = "8";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Meter Maintenance Fee Correction";

                                            }
                                            if (str.equals ( "Preload" )) {

                                                GSBilling.getInstance ( ).Payment_type = "9";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Preload";

                                            }
                                            if (str.equals ( "Reconnection Fee" )) {

                                                GSBilling.getInstance ( ).Payment_type = "10";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Reconnection Fee";

                                            }
                                            if (str.equals ( "Final Bill" )) {

                                                GSBilling.getInstance ( ).Payment_type = "11";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Final Bill";

                                            }
                                            if (str.equals ( "Security Deposit" )) {

                                                GSBilling.getInstance ( ).Payment_type = "12";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Security Deposit";

                                            }
                                            if (str.equals ( "Meter Replacement Payment" )) {

                                                GSBilling.getInstance ( ).Payment_type = "13";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Meter Replacement Payment";

                                            }
                                            if (str.equals ( "Bill" )) {

                                                GSBilling.getInstance ( ).Payment_type = "14";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Bill";

                                            }
//                                        GSBilling.getInstance().Payment_type =spinner_indcident.getSelectedItem().toString();
//                                        etMobNo.setText(GSBilling.getInstance().MOBILENO);
//                                        GSBilling.getInstance().Payment_type = Integer.toString( spinner_indcident.getItemAtPosition());
                                            // Structcolmas.PYMNT_MODE = "Cash";
                                            String typePay = "CASH";
                                            CommonFunctionClass commonFunctionClass = new CommonFunctionClass ( );
                                            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext ( ).getSystemService ( Context.LAYOUT_INFLATER_SERVICE );
                                            View customView = null;
                                            int screenSize = getResources ( ).getConfiguration ( ).screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
                                            switch (screenSize) {
                                                case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                                                    customView = layoutInflater.inflate ( R.layout.number_dialogue, null );
                                                    break;
                                                case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                                                    customView = layoutInflater.inflate ( R.layout.number_dialogue, null );
                                                    break;
                                                case Configuration.SCREENLAYOUT_SIZE_LARGE:
                                                    customView = layoutInflater.inflate ( R.layout.number_dialogue, null );
                                                    break;
                                                case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                                                    customView = layoutInflater.inflate ( R.layout.number_dialogue, null );
                                                    break;
                                                case Configuration.SCREENLAYOUT_SIZE_SMALL:
                                                    customView = layoutInflater.inflate ( R.layout.number_dialogue, null );
                                                    break;
                                                default:
                                                    customView = layoutInflater.inflate ( R.layout.number_dialogue, null );
                                            }
//                    customView = layoutInflater.inflate(R.layout.number_dialogue, null);
                                            TextView txtamount = customView.findViewById ( R.id.amounttxt );
                                            TextView txtview = customView.findViewById ( R.id.txt_amount );
                                            Button btnno = customView.findViewById ( R.id.btn_no );
                                            Button btnyes = customView.findViewById ( R.id.btn_yes );
                                            double Number = Double.parseDouble ( etCash.getText ( ).toString ( ) );
//                                        CommonFunctionClass obj = new NumberToWord();

                                            String Num = numberFormat.format ( Number );
                                            String wordFormat = "";
                                            if (Num.contains ( "." )) {
                                                String[] arr = Num.split ( "\\." );
                                                Long[] intArr = new Long[2];
                                                intArr[0] = Long.parseLong ( arr[0] ); // 1
                                                intArr[1] = Long.parseLong ( arr[1] );
                                                String str1 = commonFunctionClass.convert ( Integer.parseInt ( String.valueOf ( intArr[0] ) ) );
                                                String str2 = commonFunctionClass.convert ( Integer.parseInt ( String.valueOf ( intArr[1] ) ) );
                                                wordFormat = str1 + "  Naira and " + str2 + " Kobo only";
                                            }
                                            txtamount.setText ( wordFormat );
                                            txtview.setText ( Num );
                                            AlertDialog.Builder alert = new AlertDialog.Builder ( PaymentType.this );
                                            // this is set the view from XML inside AlertDialog
                                            alert.setView ( customView );
                                            final AlertDialog dialog = alert.create ( );
                                            dialog.show ( );

                                            btnno.setOnClickListener ( new View.OnClickListener ( ) {
                                                @Override
                                                public void onClick(View v) {

                                                    dialog.cancel ( );
                                                }
                                            } );
                                            btnyes.setOnClickListener ( new View.OnClickListener ( ) {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.cancel ( );
                                                    makeCardPayment ( etCash.getText().toString ());
                                                 //   new TextFileClass ( PaymentType.this ).execute ( );

                                                }
                                            } );

//                                        new TextFileClass(PaymentType.this).execute();
//                                        if (prev_pref.equalsIgnoreCase("0_0_0") || prev_pref.equalsIgnoreCase("0_0_1")) {//IMP_AMI
//
//                                            Intent intent = new Intent(PaymentType.this, MainActivityCollectionPrint.class);
//                                            GSBilling.getInstance().setPayType("CASH");
////                                            intent.putExtra("payType",typePay);
//                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                            startActivity(intent);
//                                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                                    R.anim.anim_slide_out_left);
//
//                                        } else if (prev_pref.equalsIgnoreCase("0_1_0") || prev_pref.equalsIgnoreCase("0_1_1")) {//IMP_REG
//
//                                            Toast.makeText(PaymentType.this, "Under process", Toast.LENGTH_SHORT).show();
//
//                                        } else if (prev_pref.equalsIgnoreCase("0_2_0") || prev_pref.equalsIgnoreCase("0_2_1")) {
//
//                                            Intent intent = new Intent(PaymentType.this, CollPrintActivity.class);
//                                            GSBilling.getInstance().setPayType("CASH");
////                                            intent.putExtra("payType",typePay);
//                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                            startActivity(intent);
//                                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                                    R.anim.anim_slide_out_left);
//                                        } else {
//                                            Toast.makeText(PaymentType.this, "Unable to find Printer", Toast.LENGTH_SHORT).show();
//                                        }
                                        }
                                    }
                                }
                                break;
                                case "Q": {
                                    if (etCash.getText ( ).toString ( ).equalsIgnoreCase ( "" )) {
                                        etCash.setError ( "Amount Can Not Be Empty" );
                                        Toast.makeText ( PaymentType.this, "Amount Can not be Empty", Toast.LENGTH_SHORT ).show ( );
                                    } else if (etChekNo.getText ( ).toString ( ).length ( ) == 0 && etChekNo.getText ( ).toString ( ).length ( ) < 6) {
                                        etChekNo.setError ( "Cheque No Can Not Be Empty" );
                                        Toast.makeText ( PaymentType.this, "Cheque No Can not be Empty", Toast.LENGTH_SHORT ).show ( );
                                    } else if (spinner_BankName.getAdapter ( ).getCount ( ) < 1) {
                                        Toast.makeText ( PaymentType.this, "please download master bank details", Toast.LENGTH_SHORT ).show ( );
                                    } else if (spinner_BankName.getSelectedItem ( ).toString ( ).equalsIgnoreCase ( "Select Bank Name" )) {
                                        Toast.makeText ( PaymentType.this, "Please Select Bank Name", Toast.LENGTH_SHORT ).show ( );
                                    } else if (str_bank_name.length ( ) == 0) {
                                        Toast.makeText ( getApplicationContext ( ), "Bank Name Can not be Empty", Toast.LENGTH_SHORT ).show ( );
//                                    etBankName.setError("Bank Name Can not be Empty");
                                    } else if (al_banksize > 1 && al_banksize < (bank_name_list.size ( ) - 1)) {
                                        etBankName.setVisibility ( View.VISIBLE );
                                    } else if (etChekDate.getText ( ).toString ( ).length ( ) == 0) {
                                        etChekDate.setError ( "Choose a Proper Date" );
                                        Toast.makeText ( getApplicationContext ( ), "Choose a Proper Date", Toast.LENGTH_SHORT ).show ( );
                                    } else if ((etMobNo.getText ( ).toString ( ).length ( ) == 0 || etMobNo.getText ( ).toString ( ).length ( ) < 11) || (etMobNo.getText ( ).toString ( )).equals ( "99999999999" ) || (etMobNo.getText ( ).toString ( )).equals ( "09999999999" ) || (etMobNo.getText ( ).toString ( )).equals ( "00999999999" ) || (etMobNo.getText ( ).toString ( )).equals ( "00000000000" )) {
                                        etMobNo.setError ( "Please Enter a valid Mobile No" );
                                        Toast.makeText ( PaymentType.this, "Please Enter a Valid Mobile Number", Toast.LENGTH_SHORT ).show ( );
                                    } else {
                                        if ((cashET > Float.parseFloat ( GSBilling.getInstance ( ).Wallet )) && (GSBilling.getInstance ( ).Agent.equalsIgnoreCase ( "1" ))) {
//                                        etCash.setError("Minimum Collection Amount cannot be NGN.0");
//                                        Toast.makeText(getApplicationContext(), "Minimum Collection Amount cannot be NGN.0", Toast.LENGTH_SHORT).show();
//                                        etCash.requestFocus();
                                            sDialog = new SweetAlertDialog ( PaymentType.this, SweetAlertDialog.ERROR_TYPE );
                                            sDialog.setTitleText ( "Error" );
                                            sDialog.setContentText ( "wallet amount is less for collection" );
                                            sDialog.show ( );
                                            sDialog.setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation ( );
                                                    Intent intent = new Intent ( PaymentType.this, Collection.class );
                                                    intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                                    startActivity ( intent );
                                                    overridePendingTransition ( R.anim.anim_slide_in_left,
                                                            R.anim.anim_slide_out_left );

                                                }
                                            } );
                                        } else {
                                            if (etBankName.getText ( ).toString ( ) != null && !etBankName.getText ( ).toString ( ).isEmpty ( )) {
                                                str_bank_name = etBankName.getText ( ).toString ( );
                                            }
                                            Structcolmas.AMOUNT = numberFormat.format ( Double.parseDouble ( etCash.getText ( ).toString ( ).replaceAll ( "^0+(?=\\d+$)", "" ) ) );
                                            Structcolmas.CHEQUE_NO = etChekNo.getText ( ).toString ( );
                                            Structcolmas.CH_DATE = etChekDate.getText ( ).toString ( );
                                            Structcolmas.BANK_NAME = str_bank_name;
                                            String str = spinner_indcident.getSelectedItem ( ).toString ( );
                                            if (str.equals ( "Arrears" )) {

                                                GSBilling.getInstance ( ).Payment_type = "1";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Arrears";

                                            }
                                            if (str.equals ( "Burnt Meter Replacement Fee" )) {

                                                GSBilling.getInstance ( ).Payment_type = "2";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Burnt Meter Replacement Fee";

                                            }


                                            if (str.equals ( "Bypass" )) {

                                                GSBilling.getInstance ( ).Payment_type = "3";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Bypass";

                                            }
                                            if (str.equals ( "Conn Fee (JEA)" )) {

                                                GSBilling.getInstance ( ).Payment_type = "4";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Conn Fee (JEA)";

                                            }
                                            if (str.equals ( "Conn Fee (Zone)" )) {

                                                GSBilling.getInstance ( ).Payment_type = "5";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Conn Fee (Zone)";

                                            }
                                            if (str.equals ( "Fixed Fee Correction" )) {

                                                GSBilling.getInstance ( ).Payment_type = "6";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Fixed Fee Correction";

                                            }


                                            if (str.equals ( "Loss Of Revenue" )) {

                                                GSBilling.getInstance ( ).Payment_type = "7";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Loss Of Revenue";

                                            }
                                            if (str.equals ( "Meter Maintenance Fee Correction" )) {

                                                GSBilling.getInstance ( ).Payment_type = "8";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Meter Maintenance Fee Correction";

                                            }
                                            if (str.equals ( "Preload" )) {

                                                GSBilling.getInstance ( ).Payment_type = "9";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Preload";

                                            }
                                            if (str.equals ( "Reconnection Fee" )) {

                                                GSBilling.getInstance ( ).Payment_type = "10";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Reconnection Fee";

                                            }
                                            if (str.equals ( "Final Bill" )) {

                                                GSBilling.getInstance ( ).Payment_type = "11";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Final Bill";

                                            }
                                            if (str.equals ( "Security Deposit" )) {

                                                GSBilling.getInstance ( ).Payment_type = "12";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Security Deposit";

                                            }
                                            if (str.equals ( "Meter Replacement Payment" )) {

                                                GSBilling.getInstance ( ).Payment_type = "13";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Meter Replacement Payment";

                                            }
                                            if (str.equals ( "Bill" )) {

                                                GSBilling.getInstance ( ).Payment_type = "14";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Bill";

                                            }
//                                        GSBilling.getInstance().Payment_type =spinner_indcident.getSelectedItem().toString();
//                                        etMobNo.setText(GSBilling.getInstance().MOBILENO);
                                            // Structcolmas.PYMNT_MODE = "Cheque";
                                            String typePay = "CHEQUE";
                                            CommonFunctionClass commonFunctionClass = new CommonFunctionClass ( );
                                            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext ( ).getSystemService ( Context.LAYOUT_INFLATER_SERVICE );
                                            View customView = null;
                                            int screenSize = getResources ( ).getConfiguration ( ).screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
                                            switch (screenSize) {
                                                case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                                                    customView = layoutInflater.inflate ( R.layout.number_dialogue, null );
                                                    break;
                                                case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                                                    customView = layoutInflater.inflate ( R.layout.number_dialogue, null );
                                                    break;
                                                case Configuration.SCREENLAYOUT_SIZE_LARGE:
                                                    customView = layoutInflater.inflate ( R.layout.number_dialogue, null );
                                                    break;
                                                case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                                                    customView = layoutInflater.inflate ( R.layout.number_dialogue, null );
                                                    break;
                                                case Configuration.SCREENLAYOUT_SIZE_SMALL:
                                                    customView = layoutInflater.inflate ( R.layout.number_dialogue, null );
                                                    break;
                                                default:
                                                    customView = layoutInflater.inflate ( R.layout.number_dialogue, null );
                                            }
//                                        customView = layoutInflater.inflate(R.layout.number_dialogue, null);
                                            TextView txtamount = customView.findViewById ( R.id.amounttxt );
                                            Button btnno = customView.findViewById ( R.id.btn_no );
                                            TextView txtview = customView.findViewById ( R.id.txt_amount );
                                            Button btnyes = customView.findViewById ( R.id.btn_yes );
                                            double Number = Double.parseDouble ( etCash.getText ( ).toString ( ) );
//                                        CommonFunctionClass obj = new NumberToWord();

                                            String Num = numberFormat.format ( Number );
                                            String wordFormat = "";
                                            if (Num.contains ( "." )) {
                                                String[] arr = Num.split ( "\\." );
                                                Long[] intArr = new Long[2];
                                                intArr[0] = Long.parseLong ( arr[0] ); // 1
                                                intArr[1] = Long.parseLong ( arr[1] );
                                                String str1 = commonFunctionClass.convert ( Integer.parseInt ( String.valueOf ( intArr[0] ) ) );
                                                String str2 = commonFunctionClass.convert ( Integer.parseInt ( String.valueOf ( intArr[1] ) ) );
                                                wordFormat = str1 + "  Naira and " + str2 + " Kobo only";
                                            }
                                            txtamount.setText ( wordFormat );
                                            txtview.setText ( Num );
//                                        txtamount.setText(commonFunctionClass.convert(Integer.parseInt(etCash.getText().toString())).toString());
                                            AlertDialog.Builder alert = new AlertDialog.Builder ( PaymentType.this );
                                            // this is set the view from XML inside AlertDialog
                                            alert.setView ( customView );
                                            final AlertDialog dialog = alert.create ( );
                                            dialog.show ( );

                                            btnno.setOnClickListener ( new View.OnClickListener ( ) {
                                                @Override
                                                public void onClick(View v) {

                                                    dialog.cancel ( );
                                                }
                                            } );
                                            btnyes.setOnClickListener ( new View.OnClickListener ( ) {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.cancel ( );
                                                    makeCardPayment ( etCash.getText().toString ());
                                                   // new TextFileClass ( PaymentType.this ).execute ( );

                                                }
                                            } );
//                                        new TextFileClass(PaymentType.this).execute();
//                                        if (prev_pref.equalsIgnoreCase("0_0_0") || prev_pref.equalsIgnoreCase("0_0_1")) {//IMP_AMI
//
//                                            Intent intent = new Intent(PaymentType.this, MainActivityCollectionPrint.class);
////                                             intent.putExtra(" ",typePay);
//                                            GSBilling.getInstance().setPayType("CHEQUE");
//                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                            startActivity(intent);
//                                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                                    R.anim.anim_slide_out_left);
//
//                                        } else if (prev_pref.equalsIgnoreCase("0_1_0") || prev_pref.equalsIgnoreCase("0_1_1")) {//IMP_REG
//
//                                            Toast.makeText(PaymentType.this, "Under process", Toast.LENGTH_SHORT).show();
//
//                                        } else if (prev_pref.equalsIgnoreCase("0_2_0") || prev_pref.equalsIgnoreCase("0_2_1")) {
//
//                                            Intent intent = new Intent(PaymentType.this, CollPrintActivity.class);
//                                            GSBilling.getInstance().setPayType("CHEQUE");
////                                             intent.putExtra("payType",typePay);
//                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                            startActivity(intent);
//                                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                                    R.anim.anim_slide_out_left);
//
//                                        } else {
//                                            Toast.makeText(PaymentType.this, "Unable to find Printer", Toast.LENGTH_SHORT).show();
//                                        }
                                        }
                                    }
                                }
                                break;
                                case "D": {
                                    if (etCash.getText ( ).toString ( ).equalsIgnoreCase ( "" )) {
                                        etCash.setError ( "Amount Can Not Be Empty" );
                                        Toast.makeText ( getApplicationContext ( ), "Amount Can not be Empty", Toast.LENGTH_SHORT ).show ( );
                                    } else if (etdemanddraft.getText ( ).toString ( ).length ( ) == 0 && etdemanddraft.getText ( ).toString ( ).length ( ) < 26) {
                                        etDD.setError ( "DD  No Cannot Be Empty" );
                                        Toast.makeText ( getApplicationContext ( ), "DD  No Cannot Be Empty", Toast.LENGTH_SHORT ).show ( );
                                    } else if (spinner_BankName.getSelectedItem ( ).toString ( ).equalsIgnoreCase ( "Select Bank Name" )) {
                                        Toast.makeText ( getApplicationContext ( ), "Please Select Bank Name", Toast.LENGTH_SHORT ).show ( );
                                    } else if (str_bank_name.length ( ) == 0) {
                                        Toast.makeText ( getApplicationContext ( ), "Bank Name Can Not Be Empty", Toast.LENGTH_SHORT ).show ( );
//                                    etBankName.setError("Bank Name Can not be Empty");
                                    } else if (al_banksize > 1 && al_banksize < (bank_name_list.size ( ) - 1)) {
                                        etBankName.setVisibility ( View.VISIBLE );
                                    } else if (etChekDate.getText ( ).toString ( ).length ( ) == 0) {
                                        etChekDate.setError ( "Choose a Proper Date" );
                                        Toast.makeText ( getApplicationContext ( ), "Choose a Proper Date", Toast.LENGTH_SHORT ).show ( );
                                    } else if ((etMobNo.getText ( ).toString ( ).length ( ) == 0 || etMobNo.getText ( ).toString ( ).length ( ) < 11) || (etMobNo.getText ( ).toString ( )).equals ( "99999999999" ) || (etMobNo.getText ( ).toString ( )).equals ( "09999999999" ) || (etMobNo.getText ( ).toString ( )).equals ( "00999999999" ) || (etMobNo.getText ( ).toString ( )).equals ( "00000000000" )) {
                                        etMobNo.setError ( "Please Enter a valid Mobile No" );
                                        Toast.makeText ( PaymentType.this, "Please Enter a Valid Mobile Number", Toast.LENGTH_SHORT ).show ( );
                                    } else {
                                        if ((cashET > Float.parseFloat ( GSBilling.getInstance ( ).Wallet )) && (GSBilling.getInstance ( ).Agent.equalsIgnoreCase ( "1" ))) {
//                                        etCash.setError("Minimum Collection Amount cannot be NGN.0");
//                                        Toast.makeText(getApplicationContext(), "Minimum Collection Amount cannot be NGN.0", Toast.LENGTH_SHORT).show();
                                            sDialog = new SweetAlertDialog ( PaymentType.this, SweetAlertDialog.ERROR_TYPE );
                                            sDialog.setTitleText ( "Error" );
                                            sDialog.setContentText ( "wallet amount is less for collection" );
                                            sDialog.show ( );
                                            sDialog.setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation ( );
                                                    Intent intent = new Intent ( PaymentType.this, Collection.class );
                                                    intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                                    startActivity ( intent );
                                                    overridePendingTransition ( R.anim.anim_slide_in_left,
                                                            R.anim.anim_slide_out_left );

                                                }
                                            } );
                                        } else {
//                                        if (etBankName.getText().toString() != null && !etBankName.getText().toString().isEmpty()) {
//                                            str_bank_name = etBankName.getText().toString();
//                                        }
                                            Structcolmas.AMOUNT = numberFormat.format ( Double.parseDouble ( etCash.getText ( ).toString ( ).replaceAll ( "^0+(?=\\d+$)", "" ) ) );
                                            Structcolmas.CHEQUE_NO = etdemanddraft.getText ( ).toString ( );
                                            Structcolmas.CH_DATE = etChekDate.getText ( ).toString ( );
                                            Structcolmas.BANK_NAME = str_bank_name;
                                            String str = spinner_indcident.getSelectedItem ( ).toString ( );
                                            if (str.equals ( "Arrears" )) {

                                                GSBilling.getInstance ( ).Payment_type = "1";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Arrears";

                                            }
                                            if (str.equals ( "Burnt Meter Replacement Fee" )) {

                                                GSBilling.getInstance ( ).Payment_type = "2";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Burnt Meter Replacement Fee";

                                            }


                                            if (str.equals ( "Bypass" )) {

                                                GSBilling.getInstance ( ).Payment_type = "3";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Bypass";

                                            }
                                            if (str.equals ( "Conn Fee (JEA)" )) {

                                                GSBilling.getInstance ( ).Payment_type = "4";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Conn Fee (JEA)";

                                            }
                                            if (str.equals ( "Conn Fee (Zone)" )) {

                                                GSBilling.getInstance ( ).Payment_type = "5";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Conn Fee (Zone)";

                                            }
                                            if (str.equals ( "Fixed Fee Correction" )) {

                                                GSBilling.getInstance ( ).Payment_type = "6";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Fixed Fee Correction";

                                            }


                                            if (str.equals ( "Loss Of Revenue" )) {

                                                GSBilling.getInstance ( ).Payment_type = "7";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Loss Of Revenue";

                                            }
                                            if (str.equals ( "Meter Maintenance Fee Correction" )) {

                                                GSBilling.getInstance ( ).Payment_type = "8";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Meter Maintenance Fee Correction";

                                            }
                                            if (str.equals ( "Preload" )) {

                                                GSBilling.getInstance ( ).Payment_type = "9";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Preload";

                                            }
                                            if (str.equals ( "Reconnection Fee" )) {

                                                GSBilling.getInstance ( ).Payment_type = "10";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Reconnection Fee";

                                            }
                                            if (str.equals ( "Final Bill" )) {

                                                GSBilling.getInstance ( ).Payment_type = "11";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Final Bill";

                                            }
                                            if (str.equals ( "Security Deposit" )) {

                                                GSBilling.getInstance ( ).Payment_type = "12";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Security Deposit";

                                            }
                                            if (str.equals ( "Meter Replacement Payment" )) {

                                                GSBilling.getInstance ( ).Payment_type = "13";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Meter Replacement Payment";

                                            }
                                            if (str.equals ( "Bill" )) {

                                                GSBilling.getInstance ( ).Payment_type = "14";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Bill";

                                            }

//                                        GSBilling.getInstance().Payment_type =spinner_indcident.getSelectedItem().toString();
//                                        etMobNo.setText(GSBilling.getInstance().MOBILENO);
                                            String typePay = "DD";
                                            CommonFunctionClass commonFunctionClass = new CommonFunctionClass ( );
                                            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext ( ).getSystemService ( Context.LAYOUT_INFLATER_SERVICE );
                                            View customView = null;
                                            int screenSize = getResources ( ).getConfiguration ( ).screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
                                            switch (screenSize) {
                                                case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                                                    customView = layoutInflater.inflate ( R.layout.number_dialogue, null );
                                                    break;
                                                case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                                                    customView = layoutInflater.inflate ( R.layout.number_dialogue, null );
                                                    break;
                                                case Configuration.SCREENLAYOUT_SIZE_LARGE:
                                                    customView = layoutInflater.inflate ( R.layout.number_dialogue, null );
                                                    break;
                                                case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                                                    customView = layoutInflater.inflate ( R.layout.number_dialogue, null );
                                                    break;
                                                case Configuration.SCREENLAYOUT_SIZE_SMALL:
                                                    customView = layoutInflater.inflate ( R.layout.number_dialogue, null );
                                                    break;
                                                default:
                                                    customView = layoutInflater.inflate ( R.layout.number_dialogue, null );
                                            }
//                                        customView = layoutInflater.inflate(R.layout.number_dialogue, null);
                                            TextView txtamount = customView.findViewById ( R.id.amounttxt );
                                            TextView txtview = customView.findViewById ( R.id.txt_amount );
                                            Button btnno = customView.findViewById ( R.id.btn_no );
                                            Button btnyes = customView.findViewById ( R.id.btn_yes );
                                            double Number = Double.parseDouble ( etCash.getText ( ).toString ( ) );
//                                        CommonFunctionClass obj = new NumberToWord();

                                            String Num = numberFormat.format ( Number );
                                            String wordFormat = "";
                                            if (Num.contains ( "." )) {
                                                String[] arr = Num.split ( "\\." );
                                                Long[] intArr = new Long[2];
                                                intArr[0] = Long.parseLong ( arr[0] ); // 1
                                                intArr[1] = Long.parseLong ( arr[1] );
                                                String str1 = commonFunctionClass.convert ( Integer.parseInt ( String.valueOf ( intArr[0] ) ) );
                                                String str2 = commonFunctionClass.convert ( Integer.parseInt ( String.valueOf ( intArr[1] ) ) );
                                                wordFormat = str1 + "  Naira and " + str2 + " Kobo only";
                                            }
                                            txtamount.setText ( wordFormat );
                                            txtview.setText ( Num );
//                                        txtamount.setText(commonFunctionClass.convert(Integer.parseInt(etCash.getText().toString())).toString());
                                            AlertDialog.Builder alert = new AlertDialog.Builder ( PaymentType.this );
                                            // this is set the view from XML inside AlertDialog
                                            alert.setView ( customView );
                                            final AlertDialog dialog = alert.create ( );
                                            dialog.show ( );

                                            btnno.setOnClickListener ( new View.OnClickListener ( ) {
                                                @Override
                                                public void onClick(View v) {

                                                    dialog.cancel ( );
                                                }
                                            } );
                                            btnyes.setOnClickListener ( new View.OnClickListener ( ) {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.cancel ( );

                                                    makeCardPayment ( etCash.getText().toString ());
                                                    //new TextFileClass ( PaymentType.this ).execute ( );

                                                }
                                            } );

//                                        new TextFileClass(PaymentType.this).execute();
//                                        if (prev_pref.equalsIgnoreCase("0_0_0") || prev_pref.equalsIgnoreCase("0_0_1")) {//IMP_AMI
//
//                                            Intent intent = new Intent(PaymentType.this, MainActivityCollectionPrint.class);
//                                            GSBilling.getInstance().setPayType("pos");
////                                            intent.putExtra("payType",typePay);
//                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                            startActivity(intent);
//                                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                                    R.anim.anim_slide_out_left);
//
//                                        } else if (prev_pref.equalsIgnoreCase("0_1_0") || prev_pref.equalsIgnoreCase("0_1_1")) {//IMP_REG
//
//                                            Toast.makeText(PaymentType.this, "Under process", Toast.LENGTH_SHORT).show();
//
//                                        } else if (prev_pref.equalsIgnoreCase("0_2_0") || prev_pref.equalsIgnoreCase("0_2_1")) {
//
//                                            Intent intent = new Intent(PaymentType.this, CollPrintActivity.class);
//                                            GSBilling.getInstance().setPayType("pos");
////                                            intent.putExtra("payType",typePay);
//                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                            startActivity(intent);
//                                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                                    R.anim.anim_slide_out_left);
//
//                                        } else {
//                                            Toast.makeText(PaymentType.this, "Unable to find Printer", Toast.LENGTH_SHORT).show();
//                                        }
                                        }
                                    }
                                }
                                break;
                                case "P": {
                                    if (etCash.getText ( ).toString ( ).equalsIgnoreCase ( "" )) {
                                        etCash.setError ( "Amount Can Not Be Empty" );
                                        Toast.makeText ( getApplicationContext ( ), "Amount Can Not Be Empty", Toast.LENGTH_SHORT ).show ( );
                                    } else if (etDD.getText ( ).toString ( ).length ( ) == 0 && etDD.getText ( ).toString ( ).length ( ) < 26) {
                                        etDD.setError ( "pos receipt No Cannot be Empty" );
                                        Toast.makeText ( getApplicationContext ( ), "pos receipt No Cannot Be Empty", Toast.LENGTH_SHORT ).show ( );
//                                } else if (spinner_BankName.getSelectedItem().toString().equalsIgnoreCase("Select Bank Name")) {
//                                    Toast.makeText(getApplicationContext(), "Please Select Bank Name", Toast.LENGTH_SHORT).show();
//                                } else if (str_bank_name.toString().length() == 0) {
//                                    Toast.makeText(getApplicationContext(), "Bank Name Can not be Empty", Toast.LENGTH_SHORT).show();
////                                    etBankName.setError("Bank Name Can not be Empty");
//                                } else if (al_banksize > 1 && al_banksize < (bank_name_list.size() - 1)) {
//                                    etBankName.setVisibility(View.VISIBLE);
//                                } else if (etChekDate.getText().toString().length() == 0) {
//                                    etChekDate.setError("Choose a Proper Date");
//                                    Toast.makeText(getApplicationContext(), "Choose a Proper Date", Toast.LENGTH_SHORT).show();
                                    } else if ((etMobNo.getText ( ).toString ( ).length ( ) == 0 || etMobNo.getText ( ).toString ( ).length ( ) < 11) || (etMobNo.getText ( ).toString ( )).equals ( "99999999999" ) || (etMobNo.getText ( ).toString ( )).equals ( "09999999999" ) || (etMobNo.getText ( ).toString ( )).equals ( "00999999999" ) || (etMobNo.getText ( ).toString ( )).equals ( "00000000000" )) {
                                        etMobNo.setError ( "Please Enter a valid Mobile No" );
                                        Toast.makeText ( PaymentType.this, "Please Enter a Valid Mobile Number", Toast.LENGTH_SHORT ).show ( );
                                    } else {
                                        if ((cashET > Float.parseFloat ( GSBilling.getInstance ( ).Wallet )) && (GSBilling.getInstance ( ).Agent.equalsIgnoreCase ( "1" ))) {
//                                        etCash.setError("Minimum Collection Amount cannot be NGN.0");
//                                        Toast.makeText(getApplicationContext(), "Minimum Collection Amount cannot be NGN.0", Toast.LENGTH_SHORT).show();
                                            sDialog = new SweetAlertDialog ( PaymentType.this, SweetAlertDialog.ERROR_TYPE );
                                            sDialog.setTitleText ( "Error" );
                                            sDialog.setContentText ( "Wallet amount is less for collection" );
                                            sDialog.show ( );
                                            sDialog.setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation ( );
                                                    Intent intent = new Intent ( PaymentType.this, Collection.class );
                                                    intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                                    startActivity ( intent );
                                                    overridePendingTransition ( R.anim.anim_slide_in_left,
                                                            R.anim.anim_slide_out_left );

                                                }
                                            } );
                                        } else {
//                                        if (etBankName.getText().toString() != null && !etBankName.getText().toString().isEmpty()) {
//                                            str_bank_name = etBankName.getText().toString();
//                                        }
                                            Structcolmas.AMOUNT = numberFormat.format ( Double.parseDouble ( etCash.getText ( ).toString ( ).replaceAll ( "^0+(?=\\d+$)", "" ) ) );
                                            Structcolmas.CHEQUE_NO = etDD.getText ( ).toString ( );
                                            Structcolmas.CH_DATE = etChekDate.getText ( ).toString ( );
                                            Structcolmas.BANK_NAME = str_bank_name;
                                            String str = spinner_indcident.getSelectedItem ( ).toString ( );
                                            if (str.equals ( "Arrears" )) {

                                                GSBilling.getInstance ( ).Payment_type = "1";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Arrears";

                                            }
                                            if (str.equals ( "Burnt Meter Replacement Fee" )) {

                                                GSBilling.getInstance ( ).Payment_type = "2";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Burnt Meter Replacement Fee";

                                            }


                                            if (str.equals ( "Bypass" )) {

                                                GSBilling.getInstance ( ).Payment_type = "3";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Bypass";

                                            }
                                            if (str.equals ( "Conn Fee (JEA)" )) {

                                                GSBilling.getInstance ( ).Payment_type = "4";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Conn Fee (JEA)";

                                            }
                                            if (str.equals ( "Conn Fee (Zone)" )) {

                                                GSBilling.getInstance ( ).Payment_type = "5";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Conn Fee (Zone)";

                                            }
                                            if (str.equals ( "Fixed Fee Correction" )) {

                                                GSBilling.getInstance ( ).Payment_type = "6";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Fixed Fee Correction";

                                            }


                                            if (str.equals ( "Loss Of Revenue" )) {

                                                GSBilling.getInstance ( ).Payment_type = "7";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Loss Of Revenue";

                                            }
                                            if (str.equals ( "Meter Maintenance Fee Correction" )) {

                                                GSBilling.getInstance ( ).Payment_type = "8";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Meter Maintenance Fee Correction";

                                            }
                                            if (str.equals ( "Preload" )) {

                                                GSBilling.getInstance ( ).Payment_type = "9";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Preload";

                                            }
                                            if (str.equals ( "Reconnection Fee" )) {

                                                GSBilling.getInstance ( ).Payment_type = "10";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Reconnection Fee";

                                            }
                                            if (str.equals ( "Final Bill" )) {

                                                GSBilling.getInstance ( ).Payment_type = "11";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Final Bill";

                                            }
                                            if (str.equals ( "Security Deposit" )) {

                                                GSBilling.getInstance ( ).Payment_type = "12";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Security Deposit";

                                            }
                                            if (str.equals ( "Meter Replacement Payment" )) {

                                                GSBilling.getInstance ( ).Payment_type = "13";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Meter Replacement Payment";

                                            }
                                            if (str.equals ( "Bill" )) {

                                                GSBilling.getInstance ( ).Payment_type = "14";
                                                GSBilling.getInstance ( ).INCIDENT_TYPE = "Bill";

                                            }

//                                        GSBilling.getInstance().Payment_type =spinner_indcident.getSelectedItem().toString();
//                                        etMobNo.setText(GSBilling.getInstance().MOBILENO);
                                            String typePay = "POS";
                                            CommonFunctionClass commonFunctionClass = new CommonFunctionClass ( );
                                            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext ( ).getSystemService ( Context.LAYOUT_INFLATER_SERVICE );
                                            View customView = null;
                                            int screenSize = getResources ( ).getConfiguration ( ).screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
                                            switch (screenSize) {
                                                case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                                                    customView = layoutInflater.inflate ( R.layout.number_dialogue, null );
                                                    break;
                                                case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                                                    customView = layoutInflater.inflate ( R.layout.number_dialogue, null );
                                                    break;
                                                case Configuration.SCREENLAYOUT_SIZE_LARGE:
                                                    customView = layoutInflater.inflate ( R.layout.number_dialogue, null );
                                                    break;
                                                case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                                                    customView = layoutInflater.inflate ( R.layout.number_dialogue, null );
                                                    break;
                                                case Configuration.SCREENLAYOUT_SIZE_SMALL:
                                                    customView = layoutInflater.inflate ( R.layout.number_dialogue, null );
                                                    break;
                                                default:
                                                    customView = layoutInflater.inflate ( R.layout.number_dialogue, null );
                                            }
//                                        customView = layoutInflater.inflate(R.layout.number_dialogue, null);
                                            TextView txtamount = customView.findViewById ( R.id.amounttxt );
                                            TextView txtview = customView.findViewById ( R.id.txt_amount );
                                            Button btnno = customView.findViewById ( R.id.btn_no );
                                            Button btnyes = customView.findViewById ( R.id.btn_yes );
                                            double Number = Double.parseDouble ( etCash.getText ( ).toString ( ) );
//                                        CommonFunctionClass obj = new NumberToWord();

                                            String Num = numberFormat.format ( Number );
                                            String wordFormat = "";
                                            if (Num.contains ( "." )) {
                                                String[] arr = Num.split ( "\\." );
                                                Long[] intArr = new Long[2];
                                                intArr[0] = Long.parseLong ( arr[0] ); // 1
                                                intArr[1] = Long.parseLong ( arr[1] );
                                                String str1 = commonFunctionClass.convert ( Integer.parseInt ( String.valueOf ( intArr[0] ) ) );
                                                String str2 = commonFunctionClass.convert ( Integer.parseInt ( String.valueOf ( intArr[1] ) ) );
                                                wordFormat = str1 + "  Naira and " + str2 + " Kobo only";
                                            }
                                            txtamount.setText ( wordFormat );
                                            txtview.setText ( Num );
//                                        txtamount.setText(commonFunctionClass.convert(Integer.parseInt(etCash.getText().toString())).toString());
                                            AlertDialog.Builder alert = new AlertDialog.Builder ( PaymentType.this );
                                            // this is set the view from XML inside AlertDialog
                                            alert.setView ( customView );
                                            final AlertDialog dialog = alert.create ( );
                                            dialog.show ( );

                                            btnno.setOnClickListener ( new View.OnClickListener ( ) {
                                                @Override
                                                public void onClick(View v) {

                                                    dialog.cancel ( );
                                                }
                                            } );
                                            btnyes.setOnClickListener ( new View.OnClickListener ( ) {
                                                @Override
                                                public void onClick(View v) {

                                                    dialog.cancel ( );

                                                    makeCardPayment ( etCash.getText().toString ());
                                                   // new TextFileClass ( PaymentType.this ).execute ( );

                                                }
                                            } );

//                                        new TextFileClass(PaymentType.this).execute();
//                                        if (prev_pref.equalsIgnoreCase("0_0_0") || prev_pref.equalsIgnoreCase("0_0_1")) {//IMP_AMI
//
//                                            Intent intent = new Intent(PaymentType.this, MainActivityCollectionPrint.class);
//                                            GSBilling.getInstance().setPayType("pos");
////                                            intent.putExtra("payType",typePay);
//                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                            startActivity(intent);
//                                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                                    R.anim.anim_slide_out_left);
//
//                                        } else if (prev_pref.equalsIgnoreCase("0_1_0") || prev_pref.equalsIgnoreCase("0_1_1")) {//IMP_REG
//
//                                            Toast.makeText(PaymentType.this, "Under process", Toast.LENGTH_SHORT).show();
//
//                                        } else if (prev_pref.equalsIgnoreCase("0_2_0") || prev_pref.equalsIgnoreCase("0_2_1")) {
//
//                                            Intent intent = new Intent(PaymentType.this, CollPrintActivity.class);
//                                            GSBilling.getInstance().setPayType("pos");
////                                            intent.putExtra("payType",typePay);
//                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                            startActivity(intent);
//                                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                                    R.anim.anim_slide_out_left);
//
//                                        } else {
//                                            Toast.makeText(PaymentType.this, "Unable to find Printer", Toast.LENGTH_SHORT).show();
//                                        }
                                        }
                                    }
                                    break;
                                }
                                default:
                                    Toast.makeText ( PaymentType.this, "Select Payment Type ", Toast.LENGTH_SHORT ).show ( );
                            }

                        } catch (NumberFormatException e) {
                            Toast.makeText ( PaymentType.this, "All Fields Mandatory", Toast.LENGTH_SHORT ).show ( );
                        }

//                    etMobNo.setText(GSBilling.getInstance().MOBILENO);

                    }
//                Toast.makeText(PaymentType.this, "Payment Type is : " + GSBilling.getInstance().getPayType(), Toast.LENGTH_SHORT).show();
                }


        } );

    }

    public void makeCardPayment(String amount){

        DecimalFormat decimalFormat=new DecimalFormat ( "#.00" );
       String collectionAmount= decimalFormat.format ( Double.parseDouble ( amount ) );




      //  System.out.println ("Amount "+collectionAmount+"0" );

      //  System.out.println ( xx );

        String additionalAmount="0.0";
        String transactionId=System.currentTimeMillis ()+""+MainActivity.MRID;

        System.out.println ("Transaction id "+transactionId );

       // 2677
        Intent intent=new Intent (  );
        intent.putExtra( CardUtils.TRANSACTION_AMOUNT,collectionAmount);
        intent.putExtra(CardUtils.TRANSACTION_ADDITIONAL_AMOUNT,additionalAmount);
        intent.putExtra(CardUtils.TRANSACTION_ID,transactionId);

        intent.setComponent ( new ComponentName(CardUtils.PACKAGE_NAME, CardUtils.CLASS_DETAILS));


        if (getPackageManager ().resolveActivity(intent, 0) != null) {
            startActivityForResult(intent ,CardUtils.REQUESTCODE);
        } else {
            Toast.makeText(this, "You Currently do not an App to back this action", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_CANCELED){

            return;
        } else if (resultCode == Activity.RESULT_OK && requestCode == CardUtils.REQUESTCODE){
            if (data != null) {

                System.out.println ("RequestCode "+requestCode );
                System.out.println ("ResultCode "+resultCode );
                Toast.makeText ( PaymentType.this, "RequestCode "+requestCode+ " " +resultCode, Toast.LENGTH_SHORT ).show ( );



                 amount= data.getStringExtra(CardUtils.AMOUNT);
                 transactionStatus =data.getStringExtra(CardUtils.TRANSACTION_STATUS);
                 transactionReason = data.getStringExtra(CardUtils.TRANSACTION_STATUS_REASON);
                 pan = data.getStringExtra(CardUtils.PAN);
                 rrn = data.getStringExtra(CardUtils.RRN);
                 terminalId = data.getStringExtra(CardUtils.TERMINAL_ID);

                System.out.println ("Amount "+amount );
                System.out.println ("trans1 "+transactionStatus );
                System.out.println ("trans2 "+transactionReason );

                if(transactionStatus.equalsIgnoreCase ( "approved" )){
                     new TextFileClass ( PaymentType.this ).execute ( );
                }
                else{
                    Toast.makeText ( PaymentType.this, ""+transactionStatus, Toast.LENGTH_SHORT ).show ( );
                }


            }

        }


    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID: {

                // open datepicker dialog.
                // set date picker for current date
                // add pickerListener listner to date picker
                if (paymentmode == "Q") {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat ( "yyyy-MM-dd" );
                        SimpleDateFormat sdf2 = new SimpleDateFormat ( "dd-MM-yyyy" );
                        c = Calendar.getInstance ( );
                        dateCurrent = sdf.parse ( sdf.format ( c.getTime ( ) ) );
                        dateLast = sdf2.parse ( LastDayofMonth ( ) );
                    } catch (Exception e) {

                    }

                    Date today = new Date ( );
                    c.setTime ( today );
                    c.add ( Calendar.MONTH, -0 );
                    c.add ( Calendar.DATE, -175 );// Subtract 6 months
                    System.out.println ( "MIN " + c.getTime ( ).getTime ( ) );
                    long minDate = c.getTime ( ).getTime ( ); // Twice!

                    Calendar c2 = Calendar.getInstance ( );
                    c2.setTime ( dateLast );
                    c2.add ( Calendar.DAY_OF_MONTH, -18 );
                    c2.add ( Calendar.DATE, 180 );// Subtract 6 months
                    long maxDate = c2.getTime ( ).getTime ( ); // Twice!

                    Calendar c3 = Calendar.getInstance ( );
                    int mYear = c3.get ( Calendar.YEAR );
                    int mMonth = c3.get ( Calendar.MONTH );
                    int mDay = c3.get ( Calendar.DAY_OF_MONTH );

                    DatePickerDialog pickerDialog = new DatePickerDialog ( this, pickerListener, mYear, mMonth, mDay );

                    pickerDialog.getDatePicker ( ).setMaxDate ( maxDate );
                    pickerDialog.getDatePicker ( ).setMinDate ( minDate );
                    return pickerDialog;
                } else {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat ( "yyyy-MM-dd" );
                        SimpleDateFormat sdf2 = new SimpleDateFormat ( "dd-MM-yyyy" );
                        c = Calendar.getInstance ( );
                        dateCurrent = sdf.parse ( sdf.format ( c.getTime ( ) ) );
                        dateLast = sdf2.parse ( LastDayofMonth ( ) );
                    } catch (Exception e) {

                    }
                    Date today = new Date ( );
                    c.setTime ( today );
                    c.add ( Calendar.MONTH, -0 );
                    c.add ( Calendar.DATE, -30 );// Subtract 6 months
                    System.out.println ( "MIN " + c.getTime ( ).getTime ( ) );
                    long minDate = c.getTime ( ).getTime ( ); // Twice!

                    Calendar c2 = Calendar.getInstance ( );
                    c2.setTime ( dateLast );
                    c2.add ( Calendar.DAY_OF_MONTH, -2 );
                    c2.add ( Calendar.DATE, 0 );// Subtract 6 months
                    long maxDate = c2.getTime ( ).getTime ( ); // Twice!

                    Calendar c3 = Calendar.getInstance ( );
                    int mYear = c3.get ( Calendar.YEAR );
                    int mMonth = c3.get ( Calendar.MONTH );
                    int mDay = c3.get ( Calendar.DAY_OF_MONTH );

                    DatePickerDialog pickerDialog = new DatePickerDialog ( this, pickerListener, mYear, mMonth, mDay );

                    pickerDialog.getDatePicker ( ).setMaxDate ( maxDate );
                    pickerDialog.getDatePicker ( ).setMinDate ( minDate );
                    return pickerDialog;

                }
            }
            default: {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat ( "yyyy-MM-dd" );
                    SimpleDateFormat sdf2 = new SimpleDateFormat ( "dd-MM-yyyy" );
                    c = Calendar.getInstance ( );
                    dateCurrent = sdf.parse ( sdf.format ( c.getTime ( ) ) );
                    dateLast = sdf2.parse ( LastDayofMonth ( ) );
                } catch (Exception e) {

                }
                Date today = new Date ( );
                c.setTime ( today );
                c.add ( Calendar.MONTH, -0 );
                c.add ( Calendar.DATE, -30 );// Subtract 6 months
                System.out.println ( "MIN " + c.getTime ( ).getTime ( ) );
                long minDate = c.getTime ( ).getTime ( ); // Twice!

                Calendar c2 = Calendar.getInstance ( );
//                c2.setTime(dateLast);
                c2.setTime ( today );
                c2.add ( Calendar.DAY_OF_MONTH, -0 );
                c2.add ( Calendar.DATE, 0 );// Subtract 6 months
                long maxDate = c2.getTime ( ).getTime ( ); // Twice!

                Calendar c3 = Calendar.getInstance ( );
                int mYear = c3.get ( Calendar.YEAR );
                int mMonth = c3.get ( Calendar.MONTH );
                int mDay = c3.get ( Calendar.DAY_OF_MONTH );

                DatePickerDialog pickerDialog = new DatePickerDialog ( this, pickerListener, mYear, mMonth, mDay );

                pickerDialog.getDatePicker ( ).setMaxDate ( maxDate );
                pickerDialog.getDatePicker ( ).setMinDate ( minDate );
                return pickerDialog;

            }


        }
//        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener ( ) {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            //String date_value = selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear;
            String date_value = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
            System.out.println ( "Pre date is " + date_value );
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            Calendar c = Calendar.getInstance ( );

            //String date_valuenow = c.get(Calendar.DAY_OF_MONTH) + "-" + ((c.get(Calendar.MONTH)) + 1) + "-" + c.get(Calendar.YEAR);
            String date_valuenow = c.get ( Calendar.YEAR ) + "-" + ((c.get ( Calendar.MONTH )) + 1) + "-" + c.get ( Calendar.DAY_OF_MONTH );
            try {

                SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy-MM-dd" );

                date1 = formatter.parse ( date_value );
                date2 = formatter.parse ( date_valuenow );

                SimpleDateFormat format = new SimpleDateFormat ( "dd-MM-yyyy" );
                SimpleDateFormat formatMN = new SimpleDateFormat ( "hh.mm" );

                String formattedDate = format.format ( date1 );
//                String formattedDate = format.format(date2);
                String formateed = formatMN.format ( date1 );

                etChekDate.setText ( formattedDate );

            } catch (ParseException e1) {
                e1.printStackTrace ( );
            }

        }
    };

    public String LastDayofMonth() {
        String date;
        SimpleDateFormat dateFormat = new SimpleDateFormat ( "dd-MM-yyyy" );
        Calendar c = Calendar.getInstance ( );
        c.set ( Calendar.DAY_OF_MONTH, c.getActualMaximum ( Calendar.DAY_OF_MONTH ) );
        String format2 = dateFormat.format ( c.getTime ( ) );
        // System.out.println("Before date last day" + Calendar.getInstance().getActualMaximum(Calendar.DATE));
        return format2;
    }

    private void showProgressDialog() {
        if (sDialog == null) {
            sDialog = new SweetAlertDialog ( PaymentType.this, SweetAlertDialog.PROGRESS_TYPE );
            sDialog.getProgressHelper ( ).setBarColor ( Color.parseColor ( "#A5DC86" ) );
            sDialog.setTitleText ( "Loading" );
            sDialog.setCancelable ( false );
            sDialog.show ( );
        }
        sDialog.show ( );
    }

    private void dismissProgressDialog() {
        if (sDialog != null && sDialog.isShowing ( )) {
            sDialog.dismiss ( );
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
        Intent intent = new Intent ( PaymentType.this, MainActivity.class );
        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( intent );
    }

    //------------------Meter Make--------------------------------//
    public class BankName extends AsyncTask <String, String, String> {
        ProgressDialog pd;
        Context _context;

        BankName(Context ctx) {
            _context = ctx;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // ShowAlertagain();
            super.onPreExecute ( );
            pd = new ProgressDialog ( PaymentType.this );
            pd.setMessage ( "Please wait..." );
            pd.setCancelable ( false );
            pd.show ( );
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseHelper = new DB ( PaymentType.this );
            SD = databaseHelper.getReadableDatabase ( );

            try {
//                String poleString = "SELECT BRANCH_CODE,BRANCH_NAME from TBL_BANK_MASTER WHERE DIV_CODE='" + Structcollection.DIV_CODE + "'";
                String poleString = "SELECT BRANCH_NAME,BANK_NAME,BRANCH_CODE from TBL_BANK_MASTER order by BANK_NAME asc  ";
                //System.out.println("select distinct POLE_CODE,POLE_TYPE from TBL_POLE_MASTER WHERE FEEDER_CODE ='" + feeder_id + "'");
                bank_Name = SD.rawQuery ( poleString, null );
                bank_name_id_list = new ArrayList <String> ( );
                bank_name_list = new ArrayList <String> ( );
                if (bank_Name != null && bank_Name.moveToFirst ( ) && bank_Name.getCount ( ) > 0) {
                    bank_name_id_list.add ( "Select Bank Name " );
                    bank_name_list.add ( "Select Bank Name" );

                    do {

                        String bank_name_id = bank_Name.getString ( 0 );
                        System.out.println ( "meter_id: : " + bank_name_id );
                        String bank_name = bank_Name.getString ( 1 );
                        System.out.println ( "mete_name: " + bank_name );
                        bank_name_id_list.add ( bank_name_id );
                        bank_name_list.add ( bank_name );


                        System.out.println ( "This is bank name " + bank_name );
                    } while (bank_Name.moveToNext ( ));

                    bank_name_id_list.add ( "100" );
                    bank_name_list.add ( "Others" );

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
                bank_name_adapter = new ArrayAdapter <String> (
                        getApplicationContext ( ), R.layout.custom_spinner, R.id.textView1, bank_name_list );
                spinner_BankName.setAdapter ( bank_name_adapter );

            } catch (Exception e) {
                e.printStackTrace ( );
            }
        }
    }


    private class TextFileClass extends AsyncTask <String, Void, Void> {

        private final Context context;

        public TextFileClass(Context c) {

            this.context = c;

        }

        protected void onPreExecute() {
            progress = new ProgressDialog ( this.context );
            progress.setMessage ( "Loading" );
            progress.show ( );
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
//                dbHelper2 = new DB(getApplicationContext());
//                SD2 = dbHelper2.getWritableDatabase();
//
//                String selquer = "SELECT * FROM TBL_COLMASTER_MP WHERE Upload_Flag='N' ";//WHERE Upload_Flag='N'
//                Cursor curColselect = SD2.rawQuery(selquer, null);

                String[] arrStr = null;
                ArrayList <String> mylist = new ArrayList <String> ( );


                mylist.add ( (GSBilling.getInstance ( ).MRID) + "}" + (GSBilling.getInstance ( ).MRNAME) + "}" + (GSBilling.getInstance ( ).MRID) + "}"
                        + (GSBilling.getInstance ( ).ConsumerNO) + "}" + (GSBilling.getInstance ( ).ColDate) + "}" + (Structcolmas.COL_TIME) + "}" +
                        (Structcolmas.AMOUNT) + "}" + "}" + (Structcolmas.CHEQUE_NO) + "}" + (Structcolmas.CH_DATE) + "}" + Structcolmas.BANK_NAME + "}" +
                        (Structcolmas.MAN_BOOK_NO) + "}" + (Structcolmas.MAN_RECP_NO) + "}" + (Structcolmas.PYMNT_MODE) + "}" +
                        (Structcolmas.INSTA_FLAG) + "}" + (Structcolmas.USER_LONG) + "}" + (Structcolmas.USER_LAT) + "}" +
                        (Structcolmas.BAT_STATE) + "}" + (Structcolmas.SIG_STATE) + "}" + (Structcolmas.MOB_NO) + "}" +
                        (Structcolmas.GPS_TIME) + "}" + (Structcolmas.PRNT_BAT_STAT) + "}" + (Structcolmas.VER_CODE) + "}" +
                        (Structcolmas.GPS_ALTITUDE) + "}" + (Structcolmas.GPS_ACCURACY) + "}" + (Structcolmas.TOTAL_CONSUMPTION) + "}" +
                        (Structcolmas.APP1_CONSUMPTION) + "}" + (Structcolmas.APP1_NAME) + "}" + (Structcolmas.APP2_CONSUMPTION) + "}" +
                        (Structcolmas.APP2_NAME) + "}" + (Structcolmas.APP3_CONSUMPTION) + "}" + (Structcolmas.APP3_NAME) + "}" +
                        (Structcolmas.SESSION_KEY) + "}" + (GSBilling.getInstance ( ).ConsumerNO) + "}" + (GSBilling.getInstance ( ).SEC_CODE) + "}" +
                        (GSBilling.getInstance ( ).MeterNo) + "}" + (GSBilling.getInstance ( ).Payment_type) );
//                        mylist.add(curColselect.getString(1) + "}" + curColselect.getString(2) + "}" + curColselect.getString(3) +
//                                "}" + curColselect.getString(4) + "}" + curColselect.getString(5) + "}" + curColselect.getString(6) +
//                                "}" + curColselect.getString(10) + "}" + curColselect.getString(7) + "}" + curColselect.getString(8) +
//                                "}" + curColselect.getString(9) + "}" + curColselect.getString(11) + "}" + curColselect.getString(12) +
//                                "}" + curColselect.getString(13) + "}" + curColselect.getString(14) + "}" + curColselect.getString(15) +
//                                "}" + curColselect.getString(18) + "}" + curColselect.getString(19) + "}" + curColselect.getString(20) +
//                                "}" + curColselect.getString(21) + "}" + curColselect.getString(22) + "}" + curColselect.getString(23) +
//                                "}" + curColselect.getString(24) + "}" + curColselect.getString(25) + "}" + curColselect.getString(26) +
//                                "}" + curColselect.getString(27) + "}" + curColselect.getString(28) + "}" + curColselect.getString(29) +
//                                "}" + curColselect.getString(30) + "}" + curColselect.getString(31) + "}" + curColselect.getString(32) +
//                                "}" + curColselect.getString(33) + "}" + curColselect.getString(34) + "}" + curColselect.getString(35) +
//                                "}" + curColselect.getString(37) + "}" + curColselect.getString(38) + "}" + curColselect.getString(39) + "}");
//
                generateNoteOnSD ( getApplicationContext ( ), "colmobile.csv", mylist );


//                if (curColselect != null && curColselect.moveToFirst()) {
//
//                    while (curColselect.isAfterLast() == false) {
//                        String name = curColselect.getString(0);
//
//                        mylist.add(curColselect.getString(1) + "}" + curColselect.getString(2) + "}" + curColselect.getString(3) +
//                                "}" + curColselect.getString(4) + "}" + curColselect.getString(5) + "}" + curColselect.getString(6) +
//                                "}" + curColselect.getString(10) + "}" + curColselect.getString(7) + "}" + curColselect.getString(8) +
//                                "}" + curColselect.getString(9) + "}" + curColselect.getString(11) + "}" + curColselect.getString(12) +
//                                "}" + curColselect.getString(13) + "}" + curColselect.getString(14) + "}" + curColselect.getString(15) +
//                                "}" + curColselect.getString(18) + "}" + curColselect.getString(19) + "}" + curColselect.getString(20) +
//                                "}" + curColselect.getString(21) + "}" + curColselect.getString(22) + "}" + curColselect.getString(23) +
//                                "}" + curColselect.getString(24) + "}" + curColselect.getString(25) + "}" + curColselect.getString(26) +
//                                "}" + curColselect.getString(27) + "}" + curColselect.getString(28) + "}" + curColselect.getString(29) +
//                                "}" + curColselect.getString(30) + "}" + curColselect.getString(31) + "}" + curColselect.getString(32) +
//                                "}" + curColselect.getString(33) + "}" + curColselect.getString(34) + "}" + curColselect.getString(35) +
//                                "}" + curColselect.getString(37) + "}" + curColselect.getString(38) + "}" + curColselect.getString(39) + "}");
//                        // mylist2.add(curColselect.getString(7));
//
//
//                        curColselect.moveToNext();
//                    }
//                    generateNoteOnSD(getApplicationContext(), "colmobile.csv", mylist);
//
//                }

                PaymentType.this.runOnUiThread ( new Runnable ( ) {

                    @Override
                    public void run() {
                        progress.dismiss ( );
                        new PostClass ( PaymentType.this ).execute ( );
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
            ++_tempCount;

//            dbHelper3 = new DB(getApplicationContext());
//            SD3 = dbHelper3.getWritableDatabase();
//            String frt[] = new String[0];
//
//            String serImgQwer = "Select User_Mtr_Img,User_Sig_Img from TBL_BILLING WHERE Upload_Flag='N'";
//            Cursor curBillImg = SD3.rawQuery(serImgQwer, null);
//            if (curBillImg != null && curBillImg.moveToFirst()) {
//
//                Log.e(getApplicationContext(), "SLPrintAct", "Update Success");
//
//                mylistimagename.add(curBillImg.getString(0));
//                mylistimagename.add(curBillImg.getString(1));
//                Log.e(getApplicationContext(), "SLPrintAct", "mtr_img" + curBillImg.getString(0) + "sig_img" + curBillImg.getString(1));
//            }

//            ArrayList<String> stringArrayList = new ArrayList<String>();
//            for (int j = 0; j < mylistimagename.size(); j++) {
//
//                stringArrayList.add(Environment.getExternalStorageDirectory() + "/MBC/" + mylistimagename.get(j)); //add to arraylist
//            }
            //String[] files = stringArrayList.toArray(new String[stringArrayList.size()]);
            //String[] file = {Zip, signaturePathDes, photoPathDes};

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
//                HttpFileUpload hfu = new HttpFileUpload("http://enservtest.fedco.co.in/MPSurvey/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                HttpFileUpload hfu = new HttpFileUpload("http://10.10.24.91:8080/Collection/UploadPrePaidFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                //  HttpFileUpload hfu = new HttpFileUpload("https://dlenhance.phed.com.ng/dlenhanceapi/Collection/UploadPrePaidFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                //  HttpFileUpload hfu = new HttpFileUpload("http://dlenhance.phed.com.ng/dlenhanceapi/Collection/UploadPrePaidFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                HttpFileUpload hfu = new HttpFileUpload ( "https://dlenhanceuat.phed.com.ng/dlenhanceapi/Collection/UploadPrePaidFiles", "" + GSBilling.getInstance ( ).getFinalZipName ( ), ".zip" );
//                HttpFileUpload hfu = new HttpFileUpload("http://phedtest.fedco.co.in/phedapi/Collection/UploadPrePaidFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");

                // Log.e(getApplicationContext(), "http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles", "" + GSBilling.getInstance().getFinalZipName()+".zip");
//                Log.e(getApplicationContext(), "SLPrintAct", "going out " + GSBilling.getInstance().getFinalZipName() + ".zip");
                int status = hfu.Send_Now ( fstrm );
                System.out.println ( "This is the status " + status );
                if (status != 200) {
//                    succsess = "1";
                    PaymentType.this.runOnUiThread ( new Runnable ( ) {

                        @Override
                        public void run() {
                            progress.dismiss ( );

                            if (typePay.equalsIgnoreCase ( "Prepaid" )) {

                                if (_tempCount <= 3) {
                                    sDialog = new SweetAlertDialog ( PaymentType.this, SweetAlertDialog.ERROR_TYPE );
                                    sDialog.setTitleText ( "Error" );
                                    sDialog.setContentText ( "Not able to reach Server, Please try again" );
                                    sDialog.show ( );
                                    sDialog.showCancelButton ( true );
                                    sDialog.setCancelText ( "Cancel" );
                                    sDialog.setConfirmText ( "Retry" );
                                    sDialog.setCanceledOnTouchOutside ( false );
                                    sDialog.setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation ( );
                                            PaymentType.this.runOnUiThread ( new Runnable ( ) {

                                                @Override
                                                public void run() {
                                                    progress.dismiss ( );
                                                    new PostClass ( PaymentType.this ).execute ( );
                                                }
                                            } );


                                        }
                                    } );
                                    sDialog.setCancelClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation ( );

                                        }
                                    } );

                                } else {
                                    sDialog = new SweetAlertDialog ( PaymentType.this, SweetAlertDialog.ERROR_TYPE );
                                    sDialog.setTitleText ( "Error" );
                                    sDialog.setContentText ( "Token Not Generated" );
                                    sDialog.show ( );
                                    sDialog.setCanceledOnTouchOutside ( false );
                                    sDialog.setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation ( );
                                            Intent intent = new Intent ( PaymentType.this, Collection.class );
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            intent.putExtra("printtype", print_type);
                                            startActivity ( intent );
                                            overridePendingTransition ( R.anim.anim_slide_in_left,
                                                    R.anim.anim_slide_out_left );

                                        }
                                    } );

                                }
//                            Toast.makeText(PaymentType.this, "Token not generated", Toast.LENGTH_LONG).show();

                            } else {
                                if (_tempCount <= 3) {
                                    sDialog = new SweetAlertDialog ( PaymentType.this, SweetAlertDialog.ERROR_TYPE );
                                    sDialog.setTitleText ( "Error" );
                                    sDialog.setContentText ( "Not able to reach Server, Please try again" );
                                    sDialog.show ( );
                                    sDialog.showCancelButton ( true );
                                    sDialog.setCancelText ( "Cancel" );
                                    sDialog.setConfirmText ( "Retry" );
                                    sDialog.setCanceledOnTouchOutside ( false );
                                    sDialog.setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation ( );
                                            PaymentType.this.runOnUiThread ( new Runnable ( ) {

                                                @Override
                                                public void run() {
                                                    progress.dismiss ( );
                                                    new PostClass ( PaymentType.this ).execute ( );
                                                }
                                            } );


                                        }
                                    } );
                                    sDialog.setCancelClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation ( );

                                        }
                                    } );
                                } else {

                                    sDialog = new SweetAlertDialog ( PaymentType.this, SweetAlertDialog.ERROR_TYPE );
                                    sDialog.setTitleText ( "Error" );
                                    sDialog.setContentText ( "Receipt Not Generated" );
                                    sDialog.show ( );
                                    sDialog.setCanceledOnTouchOutside ( false );
                                    sDialog.setConfirmClickListener ( new SweetAlertDialog.OnSweetClickListener ( ) {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation ( );
                                            Intent intent = new Intent ( PaymentType.this, Collection.class );
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            intent.putExtra("printtype", print_type);
                                            startActivity ( intent );
                                            overridePendingTransition ( R.anim.anim_slide_in_left,
                                                    R.anim.anim_slide_out_left );

                                        }
                                    } );

                                }
//                            Intent intent = new Intent(PaymentType.this, Collection.class);
////                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                            intent.putExtra("printtype", print_type);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                    R.anim.anim_slide_out_left);
                            }
                        }
                    } );

                } else {
//                    succsess = "0";


                    String s = hfu.responseReturn ( );
//                    String s1=hfu.responseReturn();
                    if (s != null && !s.isEmpty ( )) {
                        dbHelper = new DB ( PaymentType.this );
                        SD = dbHelper.getWritableDatabase ( );
                        try {
                            JSONArray rootJSON = new JSONArray ( s );


                            for (int i = 0; i < rootJSON.length ( ); i++) {
                                JSONObject jsonobject = rootJSON.getJSONObject ( i );

                                System.out.println ( "This is the json Payout Response " + jsonobject );
                                String ReceiptNo = jsonobject.getString ( "ReceiptNo" );
                                String tokenDec = jsonobject.getString ( "tokenDec" );
                                String ManualReceiptNo = jsonobject.getString ( "ManualReceiptNo" );
                                String PaymentDate = jsonobject.getString ( "PaymentDate" );
                                String unitsActual = jsonobject.getString ( "unitsActual" );
                                String Paymenttime = jsonobject.getString ( "PaymentTime" );

                                String walletbalance = jsonobject.getString ( "walletbalance" );

                                System.out.println ( "This is the wallet balance " + walletbalance );

                                String PayDtl = jsonobject.getString ( "PayDtl" );
                                String tariffIndex = jsonobject.getString ( "TARIFF_INDEX" );

                                JSONArray innerJSON = new JSONArray ( PayDtl );
                                for (int j = 0; j < innerJSON.length ( ); j++) {
                                    JSONObject jsonobj =
                                            innerJSON.getJSONObject ( j );
                                    String Query = "insert into PaymentDetails values('" + ReceiptNo + "','" + tokenDec + "','" + unitsActual + "','" + ManualReceiptNo + "','" + PaymentDate + "','" + jsonobj.getString ( "HEAED" ) + "','" + jsonobj.getString ( "AMOUNT" ) + "')";
                                    SD.execSQL ( Query );
                                }


                                GSBilling.getInstance ( ).TokenNo = tokenDec;
                                GSBilling.getInstance ( ).RecieptNo = ReceiptNo;
                                GSBilling.getInstance ( ).MANRECP_NO = ManualReceiptNo;
                                GSBilling.getInstance ( ).punit = unitsActual;


                                GSBilling.getInstance ( ).Serverdate = PaymentDate;
                                GSBilling.getInstance ( ).Servertime = Paymenttime;


                                GSBilling.getInstance ( ).setWallet ( walletbalance );

                                GSBilling.getInstance ( ).TARIFF_INDEX = tariffIndex;

                                JSONObject jsonObject = new JSONObject ( );
                                jsonObject.put ( "ReceiptNo", GSBilling.getInstance ( ).RecieptNo );

                                //post response to server
                                String str = new CommonHttpConnection ( getApplicationContext ( ) ).PostHttpConnection ( URLS.UserAccess.Confirmation, jsonObject, 10000 );
//                               String str = confirmCall(GSBilling.getInstance().MANRECP_NO);

//
                            }
                        } catch (JSONException e) {
                            e.printStackTrace ( );
                        }

//                        JSONArray ja = null;
////                     String str = s.replaceAll("\"\\[", "[");
////                        String str=  s.replace("\""," ");
//                        try {
////                            GSBilling.getInstance().TokenNo= str;
//                            ja = new JSONArray(s);
//                            for (int i = 0; i < ja.length(); i++) {
//
//                                JSONObject jsonSection = ja.getJSONObject(i);

//                                Log.e(context, "MainAct", "METERNO" + jsonSection.getString("METER_INST_FLAG"));
//                                Log.e(context, "MainAct", "ARREARS" + jsonSection.getString("ARREAR"));

//                                GSBilling.getInstance().TokenNo= jsonSection.getString("tokenDec");
//                                GSBilling.getInstance().RecieptNo = jsonSection.getString("ReceiptNo");
//                                GSBilling.getInstance().MANRECP_NO = jsonSection.getString("ManualReceiptNo");
//                                GSBilling.getInstance().punit = jsonSection.getString("unitsActual");
//                                GSBilling.getInstance().Serverdate=  jsonSection.getString("PaymentDate");
//                                GSBilling.getInstance().Servertime=  jsonSection.getString("PaymentTime");
//                                GSBilling.getInstance().setWallet( jsonSection.getString("walletbalance"));

//                                System.out.println("token  :" + GSBilling.getInstance().TokenNo);
//                                System.out.println("receiptNO  :" + GSBilling.getInstance().RecieptNo);
//                                System.out.println("MANRECP_NO  :" + GSBilling.getInstance().MANRECP_NO);

//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }


                    }

//                    dbHelper2 = new DB(getApplicationContext());
//                    SD2 = dbHelper2.getWritableDatabase();

//                        String updatequer = "UPDATE  TBL_BILLING  SET Upload_Flag = 'Y' WHERE  Cons_Number = '" + curBillselect.getString(0) + "' and  Bill_Month='" + curBillselect.getString(5) + "'";
//                    String updatequer = "UPDATE  TBL_COLMASTER_MP  SET Upload_Flag = 'Y',TOKEN_NO='"+GSBilling.getInstance().TokenNo+"'";
//                    Cursor curBillupdate = SD2.rawQuery(updatequer, null);
//                    if (curBillupdate != null && curBillupdate.moveToFirst()) {
////                        Log.d(this, "SLPrintAct", "Update Success");
//                    }
                    progress.dismiss ( );
                    Intent intent = new Intent ( getApplicationContext ( ), MainActivityCollectionPrint.class );
                    intent.putExtra ( CardUtils.PAN ,pan);
                    intent.putExtra ( CardUtils.RRN ,rrn);
                    intent.putExtra ( CardUtils.AMOUNT,amount );
                    intent.putExtra ( CardUtils.TERMINAL_ID,terminalId );
                    intent.putExtra ( CardUtils.TRANSACTION_STATUS, transactionStatus);
                    startActivity ( intent );
                    overridePendingTransition ( R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left );


//                    PaymentType.this.runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            progress.dismiss();
//                            Toast.makeText(PaymentType.this, " Successfully Uploaded to Server ", Toast.LENGTH_LONG).show();
//                            Intent intent = new Intent(PaymentType.this, CollectiontypesActivity.class);
//                            //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            //  intent.putExtra("printtype", print_type);
//                            startActivity(intent);
//                            finish();
//                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                    R.anim.anim_slide_out_left);
//
//                        }
//                    });
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

    public String confirmCall(String recpt) {
        URL url = null;
        try {
            url = new URL ( "http://10.10.13.103:8080/Collection/PaymentConfirm" );
        } catch (MalformedURLException e) {
            e.printStackTrace ( );
        }

        HttpsURLConnection conn = null;
        try {
            conn = (HttpsURLConnection) url.openConnection ( );
        } catch (IOException e) {
            e.printStackTrace ( );
        }
        conn.setReadTimeout ( 10000 );
        conn.setConnectTimeout ( 15000 );
        try {
            conn.setRequestMethod ( "POST" );
        } catch (ProtocolException e) {
            e.printStackTrace ( );
        }
        conn.setDoInput ( true );
        conn.setDoOutput ( true );

        Uri.Builder builder = new Uri.Builder ( );
        builder.appendQueryParameter ( "receiptNo", recpt );
        String query = builder.build ( ).getEncodedQuery ( );
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter ( new OutputStreamWriter ( conn.getOutputStream ( ), StandardCharsets.UTF_8 ) );
        } catch (IOException e) {
            e.printStackTrace ( );
        }
        try {
            writer.write ( query );
            writer.flush ( );
            writer.close ( );
        } catch (IOException e) {
            e.printStackTrace ( );
        }


        int response = 0;
        try {
            response = conn.getResponseCode ( );
        } catch (IOException e) {
            e.printStackTrace ( );
        }
        if (response == HttpURLConnection.HTTP_OK) {
            try {
                InputStream bis = new BufferedInputStream ( conn.getInputStream ( ) );
                return "OK";
            } catch (IOException e) {
                e.printStackTrace ( );
            }
            //bis is your json do whatever you want with it
        }
        conn.disconnect ( );
        return null;
    }


}
