package com.fedco.mbc.activitymetering;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.model.Structmetering;
import com.fedco.mbc.model.Structmeterupload;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.DecimalDigitsInputFilter;
import com.fedco.mbc.utils.UtilAppCommon;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MeteringReadinginput extends Activity {


    SQLiteDatabase SD;
    DB dbHelper;

    String currentDateandTime;
    String curmeterstatus;

    UtilAppCommon ucom;
    private static MeteringReadinginput RActivity;
    Logger Log;
    com.splunk.mint.Logger LogMint;
    private String curMF;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public static MeteringReadinginput getRActivity() {
        return RActivity;
    }

    Spinner spinMDUnit, spinOMDUnit;
    EditText etCR, etMD, etPF, etKVAh, etKVARh, etOKWh, etOMD, etCMD;
    TextView tvName, tvDMS, tvMS;
    Button btnContread,btn_back;
    RelativeLayout layout_clickforward,layout_clickbackward;
    String curKWh, curKVAh, curKVARh, curOKWh, curOMD, curMD ,cumMD;
    Double consumpKWh, consumpKVAh, consumpKVARh, consumpOKWh, consumpOMD, consumpMD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteringreadinginput);

        RActivity = this;

        curmeterstatus = GSBilling.getInstance().getCurmeter3ph();

        tvName = (TextView) findViewById(R.id.TextViewRINameValue);
        tvMS = (TextView) findViewById(R.id.TextViewRIPsValues);
        etCR = (EditText) findViewById(R.id.TextViewRICrValue);
        etMD = (EditText) findViewById(R.id.TextViewRIMdValue);
        etPF = (EditText) findViewById(R.id.TextViewRIPfValue);
        etKVAh = (EditText) findViewById(R.id.TextViewRIPKAHVal);
        etKVARh = (EditText) findViewById(R.id.TextViewRIPKARHVal);
        etOKWh = (EditText) findViewById(R.id.TextViewRIOFFKWHVal);
        etOMD = (EditText) findViewById(R.id.TextViewRIOFFMDVal);
        etCMD = (EditText) findViewById(R.id.TextViewRICMDValue);
        btnContread = (Button) findViewById(R.id.ButtonContreading);
        spinMDUnit = (Spinner) findViewById(R.id.TextViewRIMDUnitVal);
        spinOMDUnit = (Spinner) findViewById(R.id.TextViewRIOFFMDUnitVal);
        layout_clickforward = (RelativeLayout) findViewById(R.id.relButton);
        layout_clickbackward = (RelativeLayout) findViewById(R.id.relback);

        // etCR.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(etCR,Integer.parseInt(Structmetering.METERDIGIT),2)});
        // etKVAh.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(etKVAh,Integer.parseInt(Structmetering.METERDIGIT),2)});
        // etKVARh.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(etKVARh,Integer.parseInt(Structmetering.METERDIGIT),2)});

        etCR.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(etCR,7,2)});
        etKVAh.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(etKVAh,7,2)});
        etKVARh.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(etKVARh,7,2)});

        etMD.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(etMD,4,3)});


        // tvMS.setText(curmeterstatus);
        tvMS.setText(Structmeterupload.BILL_BASIS);
        tvName.setText(Structmetering.NAME);

        dbHelper = new DB(getApplicationContext());
        SD = dbHelper.getWritableDatabase();
        ucom = new UtilAppCommon();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        currentDateandTime = sdf.format(new Date());

        btn_back = (Button) findViewById(R.id.Buttonback);
        layout_clickbackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Meteringtypes.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //intent.putExtra("Position", 1);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right);
            }
        });

        layout_clickforward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double KVAh = ParseDouble(Structmetering.NORMALKVAH);
                double OffPeakKWh = ParseDouble(Structmetering.OFFPEAKKVAH);
                double Kwh = ParseDouble(Structmetering.NORMALKWH);

                if (KVAh > 0.0 && etKVAh.getText().length() == 0) {
                    etKVAh.setError("KVAh cannot be empty");
                } else if (OffPeakKWh > 0.0 && etOKWh.getText().length() == 0) {
                    etOKWh.setError("OffPeak KWh cannot be empty");
                }  else if (Kwh > 0.0 && etCR.getText().length() == 0) {
                    etCR.setError("KWh cannot be empty");
                } else {

                    curKWh = etCR.getText().toString();         // current KWh
                    curKVAh = etKVAh.getText().toString();      // current KVAh
                    curKVARh = etKVARh.getText().toString();    // cur KVARh
                    curMD = etMD.getText().toString();          // cur MD
                    curOKWh = etOKWh.getText().toString();      // cur Offpeak KWh
                    curOMD = etOMD.getText().toString();        // cur Offpeak MD
                    cumMD = etCMD.getText().toString();         // cur Offpeak MD

                    consumpCal();
                    validate3PhData();

                }
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

    }

    public boolean validate3PhData() {

        String kwhReturn = checkKWh();
        String kvarhReturn = checkKVARh();
        String kvahReturn = checkKVAh();

        String OffpeakkwhReturn = checkOKWh();
        String powerFactorReturn = CheckPowerFactor();
        String kwhkvahConsumptionReturn = CheckKWHKVAHConsumption();
        String mdcdConsumptionReturn = CheckMDConsumptionWithCD();
        String KVAhConsumpReturn = checkConsumpKVAh();

        String kwhConReturn = consumpValKWh();
        String mdConReturn  = consumpValMD();
        String okwhCalReturn = consumpValOKWh();
        String normalOPeakKWh = CheckOffpeaknormalKWH();
        String comapreOMDwithMD = CheckOffpeakMDcomparMD();
        // String consumpOMD = consumpValOMD();



        // if (kwhReturn.equals(" ") && kvahReturn.equals(" ") && kvarhReturn.equals(" ") && kwhConReturn.equals(" ") && mdConReturn.equals(" ") && okwhCalReturn.equals(" ") && powerFactorReturn.equals(" ") && kwhkvahConsumptionReturn.equals(" ") && mdcdConsumptionReturn.equals(" ") && normalOPeakKWh.equals(" ") && consumpOMD.equals(" ") && comapreOMDwithMD.equals(" ")&& KVAhConsumpReturn.equals(" ")) {
        if (OffpeakkwhReturn.equals(" ") && kwhReturn.equals(" ") && kvahReturn.equals(" ") && kvarhReturn.equals(" ") && kwhConReturn.equals(" ") && mdConReturn.equals(" ") && okwhCalReturn.equals(" ") && powerFactorReturn.equals(" ") && kwhkvahConsumptionReturn.equals(" ") && mdcdConsumptionReturn.equals(" ") && normalOPeakKWh.equals(" ")  && comapreOMDwithMD.equals(" ")&& KVAhConsumpReturn.equals(" ")) {

            Structmeterupload.NORMALKWH = curKWh;
            Structmeterupload.NORMALKVAH = curKVAh;
            Structmeterupload.NORMALKVARH = curKVARh;
            Structmeterupload.NORMALMD = curMD;
            Structmeterupload.OFFPEAKKWH = curOKWh;

            Structmeterupload.CUMULATIVEMD = cumMD;
            Structmeterupload.OFFPEAKMD = curOMD;
            Structmeterupload.NORMALMDUNIT = spinMDUnit.getSelectedItem().toString();
            Structmeterupload.OFFPEAKMDUNIT = spinOMDUnit.getSelectedItem().toString();

            Intent intent = new Intent(getApplicationContext(), CurVoltageDetails.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // intent.putExtra("Position", 1);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_in_left,
                    R.anim.anim_slide_out_left);

        } else {

            final Dialog dialogAccount = new Dialog(MeteringReadinginput.this, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth);

            dialogAccount.setContentView(R.layout.custom_dialoge_validate);
            dialogAccount.setTitle("Warning");
            dialogAccount.show();

            final TextView editTextAccno = (TextView) dialogAccount.findViewById(R.id.TEXTTTT);
            // editTextAccno.setText(kwhReturn + "\n" + kvarhReturn + "\n" + kvahReturn + "\n" + kwhConReturn + "\n" + mdConReturn + "\n" + okwhCalReturn + "\n" +  powerFactorReturn + "\n" +  kwhkvahConsumptionReturn+ "\n" +  mdcdConsumptionReturn+ "\n" +  normalOPeakKWh+ "\n" +  consumpOMD+ "\n" +  comapreOMDwithMD+ "\n" +  KVAhConsumpReturn);
            // editTextAccno.setText(OffpeakkwhReturn + "\n" + kwhReturn + "\n" + kvarhReturn + "\n" + kvahReturn + "\n" + kwhConReturn + "\n" + mdConReturn + "\n" + okwhCalReturn + "\n" +  powerFactorReturn + "\n" +  kwhkvahConsumptionReturn+ "\n" +  mdcdConsumptionReturn+ "\n" +  normalOPeakKWh+ "\n" +  comapreOMDwithMD+ "\n" +  KVAhConsumpReturn);
            editTextAccno.setText(OffpeakkwhReturn + kwhReturn +kvarhReturn + kvahReturn +  kwhConReturn + mdConReturn + okwhCalReturn + powerFactorReturn + kwhkvahConsumptionReturn+ mdcdConsumptionReturn+ normalOPeakKWh+ comapreOMDwithMD+ KVAhConsumpReturn);

            Button dHomeButton = (Button) dialogAccount.findViewById(R.id.dialogButtonContinue);
            dHomeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialogAccount.dismiss();

                    Structmeterupload.NORMALKWH = curKWh;
                    Structmeterupload.NORMALKVAH = curKVAh;
                    Structmeterupload.NORMALKVARH = curKVARh;
                    Structmeterupload.NORMALMD = curMD;
                    Structmeterupload.OFFPEAKKWH = curOKWh;

                    Structmeterupload.CUMULATIVEMD = cumMD;
                    Structmeterupload.OFFPEAKMD = curOMD;
                    Structmeterupload.NORMALMDUNIT = spinMDUnit.getSelectedItem().toString();
                    Structmeterupload.OFFPEAKMDUNIT = spinOMDUnit.getSelectedItem().toString();

                    Intent intent = new Intent(getApplicationContext(), CurVoltageDetails.class);
                    // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    // intent.putExtra("Position", 1);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);

                }
            });

            Button dialogButton = (Button) dialogAccount.findViewById(R.id.dialogButtonSkip);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialogAccount.dismiss();

                }
            });

        }

        return true;
    }


    /*---------- OVARALL CONSUMPTION CALCULATION ----------*/
    public void consumpCal() {

        double prevKWh = ParseDouble(Structmetering.NORMALKWH);
        double curKWhchk = ParseDouble(curKWh);
        double prevKVAh = ParseDouble(Structmetering.NORMALKVAH);
        double dobcurKVAh = ParseDouble(curKVAh);

        consumpKWh  = 0.0;
        consumpKVAh = 0.0;
        consumpOKWh = 0.0;

        if (prevKWh < curKWhchk)
        {
            consumpKWh = (ParseDouble(curKWh) - ParseDouble(Structmetering.NORMALKWH)) * ParseDouble(Structmetering.MF);
        }

        consumpMD = ParseDouble(curMD) * ParseDouble(Structmetering.MF);
        consumpOMD = ParseDouble(curOMD) * ParseDouble(Structmetering.MF);

        if (ParseDouble(Structmetering.OFFPEAKKWH)  < ParseDouble(curOKWh))
        {
            consumpOKWh = (ParseDouble(curOKWh) - ParseDouble(Structmetering.OFFPEAKKWH)) * ParseDouble(Structmetering.MF);
        }

        if (prevKVAh < dobcurKVAh)
        {
            consumpKVAh = (ParseDouble(curKVAh) - ParseDouble(Structmetering.NORMALKVAH) ) * ParseDouble(Structmetering.MF);
        }

        if (consumpKWh < 0)
        {
            consumpKWh = 0.0;
        }

        if (consumpOKWh < 0)
        {
            consumpOKWh = 0.0;
        }

        Structmeterupload.CONSUMPOKWH   = consumpOKWh.toString();
        Structmeterupload.CONSUMPKWH    = consumpKWh.toString();
        Structmeterupload.CONSUMPMD     = consumpMD.toString();
        Structmeterupload.CONSUMPKVAH   = consumpKVAh.toString();
        Structmeterupload.POWERFACTOR   = "0";

        if (consumpKWh>0)
            Structmeterupload.POWERFACTOR = String.valueOf(consumpKVAh/consumpKWh);

    }


    /*----------KWH VALUE CHECK ----------*/
    public String checkOKWh() {

        double prevOKWh = ParseDouble(Structmetering.OFFPEAKKWH);
        double curOKWhchk = ParseDouble(curOKWh);

        if (prevOKWh > curOKWhchk) {

//            return "- Previous KWh is Higher than Current KWh";
            return " - Current Off Peak KWh is Lower than Previous Off Peak KWh \n";

        } else {
            return " ";
        }

    }

    public String checkKWh() {

        double prevKWh = ParseDouble(Structmetering.NORMALKWH);
        double curKWhchk = ParseDouble(curKWh);

        if (prevKWh > curKWhchk) {

//            return "- Previous KWh is Higher than Current KWh";
            return " - Current KWh is Lower than Previous KWh \n";

        } else {
            return " ";
        }

    }


    public String checkKVAh() {
        double prevKVAh = ParseDouble(Structmetering.NORMALKVAH);
        double dobcurKVAh = ParseDouble(curKVAh);
        double ConKVAh3 = ParseDouble(Structmetering.KVAH3CON);
        double ConKVAhLastMon = ParseDouble(Structmetering.KVAHLASTMONCON);

        if (prevKVAh > dobcurKVAh) {

//            return "- Previous KVAh is Higher than Current KVAh";
            return " - Current KVAh is Lower than Previous KVAh \n";

        }

        return " ";
    }

    /*---------- KVAH Consumption VALUE CHECK ----------*/
    public String checkConsumpKVAh() {

        double prevKVAh = ParseDouble(Structmetering.NORMALKVAH);
        double dobcurKVAh = ParseDouble(curKVAh);
        double ConKVAh3 = ParseDouble(Structmetering.KVAH3CON);
        double ConKVAhLastMon = ParseDouble(Structmetering.KVAHLASTMONCON);


//        --------------------------------------twio function

        if (((((consumpKVAh) * 1) / 3) > ConKVAh3) && ((consumpKVAh * 1 > 1000) && (consumpKVAh * 1 < 10000)))
        {
            return " - KVAh consumption is 3 Times higher \n";//, "3 Times", (((kvahcons) * m_multiplicationFactor)), m_kVAhAvgConsumption, file_name, "Cumulative Kvah", "Consumption");
        }
        else
        {
            if (((((consumpKVAh) * 1) / 2) > ConKVAh3) && ((consumpKVAh * 1 > 10000)))
            {
                return " - KVAh consumption is 2 Times higher \n";//, "2 Times", (((kvahcons) * m_multiplicationFactor)), m_kVAhAvgConsumption, file_name, "Cumulative Kvah", "Consumption");
            }
        }

        if (((((consumpKVAh) * 1)) < ConKVAh3 / 3) && ((consumpKVAh * 1 > 0) && (consumpKVAh * 1 < 10000)))
        {
            return " - KVAh consumption is 3 Times lower \n";//, "3 Times", (((kvahcons) * m_multiplicationFactor)), m_kVAhAvgConsumption, file_name, "Cumulative Kvah", "Consumption");
        }
        else
        {
            if (((((consumpKVAh) * 1)) < ConKVAh3 / 2) && ((consumpKVAh * 1 > 10000)))
            {
                return " - KVAh consumption is 2 Times lower \n";//, "2 Times", (((kvahcons) * m_multiplicationFactor)), m_kVAhAvgConsumption, file_name, "Cumulative Kvah", "Consumption");
            }
        }
        return " ";

    }


    /*----------KVARH VALUE CHECK ----------*/
    public String checkKVARh() {

        double prevKVARh = ParseDouble(Structmetering.NORMALKVARH);
        double curKVARhchk = ParseDouble(curKVARh);


        if (prevKVARh > curKVARhchk) {

//            return "- Previous KVARh is Higher than Current KVARh";
            return " - Current KVARh is Lower than Previous KVARh \n";

        } else {
            return " ";
        }

    }



    /*---------- KWH CONSUMPTION CHECK ----------*/
    public String consumpValKWh() {

        double ConKwh3 = ParseDouble(Structmetering.KWH3CON); // last 3 month consumption
        double ConKwhLastMon = ParseDouble(Structmetering.KWHLASTMONCON); // last month consumption

        if ((((consumpKWh) / 3) > ConKwh3) && ((consumpKWh > 1000) && (consumpKWh < 10000)))
        {
            return " - KWh consumption is 3 Times higher \n";
        }
        else
        {
            if (((consumpKWh / 2) > ConKwh3) && ((consumpKWh > 10000)))
            {
                if ( Structmeterupload.CURRENTMETERSTATUS != "2")
                {
                    return " - KWh consumption is 2 Times higher \n";//, "2 Times", (((kwhcons) * m_multiplicationFactor)), m_kwhAVGConsumption, file_name, "Cumulative KWh", "Consumption");
                }
                else
                {
                    return " - For round off ,KWh consumption is 2 Times higher \n";//, "2 Times", (((kwhcons) * m_multiplicationFactor)), m_kwhAVGConsumption, file_name, "Cumulative KWh", "Consumption");
                }

            }
        }
        if (((consumpKWh) < (ConKwh3 / 3)) && ((consumpKWh > 0) && (consumpKWh< 10000)))
        {
            return " - KWh consumption is 3 Times Lower \n";//, "3 Times", (((kwhcons) * m_multiplicationFactor)), m_kwhAVGConsumption, file_name, "Cumulative KWh", "Consumption");
        }
        else
        {
            if (((consumpKWh) < (ConKwh3 / 2)) && ((consumpKWh > 10000)))
            {
                return " - KWh consumption is 2 Times Lower \n";//, "2 Times", (((kwhcons) * m_multiplicationFactor)), m_kwhAVGConsumption, file_name, "Cumulative KWh", "Consumption");
            }
        }

        return " ";
    }

    /*---------- KWH AND KVAH CONSUMPTION CHECK ----------*/
    public String CheckKWHKVAHConsumption() {

        double ConKwh3 = ParseDouble(Structmetering.KWH3CON);
        double ConKwhLastMon = ParseDouble(Structmetering.KWHLASTMONCON);


        if (ParseDouble(Structmetering.NORMALKVAH) > 0)
        {
            if ((consumpKWh) > 0 && (consumpKVAh) > 0)
            {
                if ((consumpKWh) > (consumpKVAh))
                {
                    return " - Cumulative KWh Consumption > Cumulative kVAh Consumption \n";//, "", kwhconsmf, kvahconsmf, file_name, "Cumulative KWh", "Consumption");
                }
            }
        }
        return " ";
    }



    /*---------- MD CONVERSION ----------*/
    public double ConvertMD(double MDvalue,String units,String target_units)
    {
        if (target_units == "KW")
        {
            if (units=="KVA")
            {
                return MDvalue / 1.1;
            }
            else if (units == "HP")
            {
                return MDvalue * 0.746;
            }

        }
        if (target_units == "KVA")
        {
            if (units == "KW")
            {
                return MDvalue * 1.1;
            }
            else if (units == "HP")
            {
                return (MDvalue * 0.746 )* 1.1;
            }

        }
        if (target_units == "HP")
        {
            if (units == "KW")
            {
                return MDvalue / 0.746;
            }
            else if (units == "KVA")
            {
                return MDvalue / ( 0.746 * 1.1);
            }

        }
        return MDvalue;
    }


    /*---------- MD CONSUMPTION CHECK ----------*/
    public String consumpValMD() {

        double ConMD3 = ParseDouble(Structmetering.MD3CON);//last 3 month
        double ConMDLastMon = ParseDouble(Structmetering.MDLASTMONCON);//last monrth
        // double Contract_demand = ConvertMD(ParseDouble(Structmetering.LOAD), Structmetering.LOADUNITS,"KVA");

        // if (((consumpMD * 1)) > Contract_demand  )
        // {
        //     return "- MD Consumption>CD";//, " ", double.Parse(values[17]) * 1, r.Contract_demand, file_name, "Cumulative MD", "Maximum Demand");
        // }
        double md_tmpValue;
        md_tmpValue = ConvertMD((consumpMD * 1), Structmeterupload.NORMALMDUNIT, "KVA");
        if (md_tmpValue >= 0 && md_tmpValue < 110)
        {
            if ((((consumpMD) * 1) / 3) > ConMD3)
            {
                return " - MD Consumption is 3 Times higher \n";//, "3 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
            }
        }

        if (md_tmpValue >= 0 && md_tmpValue < 110)
        {
            if ((consumpMD * 1) < (ConMD3 / 3))
            {
                return " - MD Consumption is 3 Times Lower \n";//, "3 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
            }
        }
        if (md_tmpValue >= 110)
        {
            if (((consumpMD * 1) / 2) > ConMD3)
            {
                return " - MD Consumption is 2 Times higher \n";//, "2 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
            }
        }

        if (md_tmpValue >= 110)
        {
            if ((consumpMD * 1) < (ConMD3 / 2))
            {
                return " - MD Consumption is 2 Times Lower \n";//, "2 Times";//, double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
            }
        }
        return " ";
    }

    /*---------- MD CONSUMPTION CHECK ----------*/
    public String consumpValOMD() {

        // double ConOMD = ParseDouble(Structmetering.OFFPEAK3CON);
        double ConOMD = ParseDouble(Structmetering.OFFPEAKKWH3CON);
        double prevConOMD = ParseDouble(Structmetering.OFFPEAKKWHLASTMONCON);
        //  double Contract_demand = ConvertMD(ParseDouble(Structmetering.LOAD), Structmetering.LOADUNITS,"KVA");

        //  if (((consumpMD * 1)) > Contract_demand  )
        //  {
        //      return "- MD Consumption>CD";//, " ", double.Parse(values[17]) * 1, r.Contract_demand, file_name, "Cumulative MD", "Maximum Demand");
        //  }
        double md_tmpValue;
        md_tmpValue = ConvertMD((consumpOMD * 1), Structmeterupload.NORMALMDUNIT, "KVA");
        if (md_tmpValue >= 0 && md_tmpValue < 110)
        {
            if ((((consumpOMD) * 1) / 3) > ConOMD)
            {
                return " - Offpeak MD Consumption is 3 Times higher \n";//, "3 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
            }
        }

        if (md_tmpValue >= 0 && md_tmpValue < 110)
        {
            if ((consumpOMD * 1) < (ConOMD / 3))
            {
                return " - Offpeak MD Consumption is 3 Times Lower \n";//, "3 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
            }
        }
        if (md_tmpValue >= 110)
        {
            if (((consumpOMD * 1) / 2) > ConOMD)
            {
                return " - Offpeak MD Consumption is 2 Times higher \n";//, "2 Times", double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
            }
        }

        if (md_tmpValue >= 110)
        {
            if ((consumpOMD * 1) < (ConOMD / 2))
            {
                return " - Offpeak MD Consumption is 2 Times Lower \n";//, "2 Times";//, double.Parse(values[17]) * m_multiplicationFactor, m_MDAvgConsumption, file_name, "Cumulative MD", "Maximum Demand");
            }
        }
        return " ";
    }

   /*---------- MD CONSUMPTION CHECK WITH CD ----------*/
    public String CheckMDConsumptionWithCD() {

        double ConMD3 = ParseDouble(Structmetering.MD3CON);
        double ConMD6 = ParseDouble(Structmetering.MDLASTMONCON);


        double Contract_demand = ConvertMD(ParseDouble(Structmetering.LOAD), Structmetering.LOADUNITS,"KVA");

        if (((consumpMD * 1)) > Contract_demand  )
        {
            return " - MD Consumption > CD \n";//, " ", double.Parse(values[17]) * 1, r.Contract_demand, file_name, "Cumulative MD", "Maximum Demand");
        }

        return " ";
    }


   /*---------- POWER FACTOR CHECK -----------*/
    public String CheckPowerFactor() {

        //  double ConMD3 = ParseDouble(Structmetering.MD3CON);
        // double ConMD6 = ParseDouble(Structmetering.MD6CON);


        if (Structmetering.CYCLE == "01")
        {
            if (ParseDouble( Structmeterupload.POWERFACTOR) < 0.7)
            {
                return " - Power Factor < 0.7 \n";//, "", r.PF, m_pf, file_name, "Power Factor", "Power Factor");
            }
            if (ParseDouble( Structmeterupload.POWERFACTOR) > 1)
            {
                return " - Power Factor > 1 \n";//, "", r.PF, m_pf, file_name, "Power Factor", "Power Factor");
            }
        }

        return " ";
    }


    /*---------- OFFPEAK CONSUMPTION CHECK -----------*///
    public String consumpValOKWh() {

        double ConOKWh3 = ParseDouble(Structmetering.OFFPEAKKWH3CON);
        double ConOKWhLastMon = ParseDouble(Structmetering.OFFPEAKKWHLASTMONCON);



        if ((((consumpOKWh) / 3) > ConOKWh3) && ((consumpOKWh > 0) && (consumpOKWh < 10000)))
        {
            return " - Offpeak KWh consumption is 3 Times higher \n";
        }
        else
        {
            if (((consumpOKWh / 2) > ConOKWh3) && ((consumpOKWh > 10000)))
            {
                if ( Structmeterupload.CURRENTMETERSTATUS != "2")
                {
                    return " - Offpeak KWh consumption is 2 Times higher \n";//, "2 Times", (((kwhcons) * m_multiplicationFactor)), m_kwhAVGConsumption, file_name, "Cumulative KWh", "Consumption");
                }
                else
                {
                    return " - For round off ,Offpeak KWh consumption is 2 Times higher \n";//, "2 Times", (((kwhcons) * m_multiplicationFactor)), m_kwhAVGConsumption, file_name, "Cumulative KWh", "Consumption");
                }

            }
        }
        if (((consumpOKWh) < (ConOKWh3 / 3)) && ((consumpOKWh > 0) && (consumpOKWh< 10000)))
        {
            return " - Offpeak KWh consumption is 3 Times Lower \n";//, "3 Times", (((kwhcons) * m_multiplicationFactor)), m_kwhAVGConsumption, file_name, "Cumulative KWh", "Consumption");
        }
        else
        {
            if (((consumpOKWh) < (ConOKWh3 / 2)) && ((consumpOKWh > 10000)))
            {
                return " - Offpeak KWh consumption is 2 Times Lower \n";//, "2 Times", (((kwhcons) * m_multiplicationFactor)), m_kwhAVGConsumption, file_name, "Cumulative KWh", "Consumption");
            }
        }

        //        if ((consumpKWh) < (consumpOKWh))
        //        {
        //            return "- Off Peak kwh Consumption > Cumulative KWh Consumption";//, "", OFFPEAKkwhconsmf, kwhconsmf, file_name, "Off Peak kwh", "Consumption");
        //        }
        return " ";

    }

    public String CheckOffpeaknormalKWH(){

        if ((consumpKWh) < (consumpOKWh))
        {
            return " - Off Peak kwh Consumption > Cumulative KWh Consumption \n";//, "", OFFPEAKkwhconsmf, kwhconsmf, file_name, "Off Peak kwh", "Consumption");
        }
        return " ";
    }

    public String CheckOffpeakMDcomparMD(){

        if ((consumpMD) < (consumpOMD))
        {
            return " - Off Peak MD Consumption > Cumulative MD Consumption \n";//, "", OFFPEAKkwhconsmf, kwhconsmf, file_name, "Off Peak kwh", "Consumption");
        }
        return " ";
    }


    /* ------ Parsing Bi-Pass --------- */
    double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch(Exception e) {
                return 0;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return 0;
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
        //        if (exit) {
        finish(); // finish activity
        Intent intent = new Intent(MeteringReadinginput.this, Meteringtypes.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("MeteringReadinginput Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
