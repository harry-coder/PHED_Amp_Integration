package com.fedco.mbc.activitymetering;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.model.Structmeterupload;
import com.fedco.mbc.utils.DecimalDigitsInputFilter;

import static com.fedco.mbc.model.Structmeterupload.METERCURY;
import static com.fedco.mbc.model.Structmeterupload.METERVOLTB;
import static com.fedco.mbc.model.Structmeterupload.METERVOLTR;
import static com.fedco.mbc.model.Structmeterupload.TONGUECURY;
import static com.fedco.mbc.model.Structmeterupload.TONGUEVOLTB;
import static com.fedco.mbc.model.Structmeterupload.TONGUEVOLTR;

public class CurVoltageDetails extends Activity {

    Button btn_Next ,btn_back;
    EditText etVMR,etVTR,etVMY,etVTY,etVMB,etVTB,etCMR,etCTR,etCMY,etCTY,etCMB,etCTB;
    EditText[] etArr;
    RelativeLayout layout_clickforward,layout_clickbackward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cur_volatge_read);

        etVMR = (EditText)findViewById(R.id.VMRValues) ;
        etVTR = (EditText)findViewById(R.id.VTRValue)  ;
        etVMY = (EditText)findViewById(R.id.VMYValue)  ;
        etVTY = (EditText)findViewById(R.id.VTYValue)  ;
        etVMB = (EditText)findViewById(R.id.VMBValue)  ;
        etVTB = (EditText)findViewById(R.id.VTBValue)  ;
        etCMR = (EditText)findViewById(R.id.CMRValues) ;
        etCTR = (EditText)findViewById(R.id.CTRValue)  ;
        etCMY = (EditText)findViewById(R.id.CMYValue)  ;
        etCTY = (EditText)findViewById(R.id.CTYValue)  ;
        etCMB = (EditText)findViewById(R.id.CMBValue)  ;
        etCTB = (EditText)findViewById(R.id.CTBValue)  ;
        layout_clickforward = (RelativeLayout) findViewById(R.id.relButton);
        layout_clickbackward = (RelativeLayout) findViewById(R.id.relback);

        etVMR.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(etVMR,3,2)});
        etVTR.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(etVTR,3,2)});
        etVMY.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(etVMY,3,2)});
        etVTY.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(etVTY,3,2)});
        etVMB.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(etVMB,3,2)});
        etVTB.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(etVTB,3,2)});
        etCMR.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(etCMR,3,2)});
        etCTR.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(etCTR,3,2)});
        etCMY.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(etCMY,3,2)});
        etCTY.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(etCTY,3,2)});
        etCMB.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(etCMB,3,2)});
        etCTB.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(etCTB,3,2)});

        etArr=new EditText[]{ etVMR,etVMY,etVMB,etCMR,etCMY,etCMB };


        btn_back = (Button) findViewById(R.id.Buttonback);
        layout_clickbackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MeteringReadinginput.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //intent.putExtra("Position", 1);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right);
            }
        });

        layout_clickforward.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                final boolean fieldsOK=validate(etArr);
                if(fieldsOK){

                    Structmeterupload.METERVOLTR    = etVMR.getText().toString();
                    Structmeterupload.METERVOLTY    = etVMY.getText().toString();
                    Structmeterupload.METERVOLTB    = etVMB.getText().toString();
                    Structmeterupload.METERCURR     = etCMR.getText().toString();
                    Structmeterupload.METERCURY     = etCMY.getText().toString();
                    Structmeterupload.METERCURB     = etCMB.getText().toString();
                    Structmeterupload.TONGUEVOLTR   = etVTR.getText().toString();
                    Structmeterupload.TONGUEVOLTY   = etVTY.getText().toString();
                    Structmeterupload.TONGUEVOLTB   = etVTB.getText().toString();
                    Structmeterupload.TONGUECURR    = etCTR.getText().toString();
                    Structmeterupload.TONGUECURY    = etCTY.getText().toString();
                    Structmeterupload.TONGUECURB    = etCTB.getText().toString();

                    Intent intent = new Intent(getApplicationContext(), SealDetails.class);
                    // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    //  intent.putExtra("Position", 1);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);

                }else{

                    // Structmeterupload.METERVOLTR    = "0.00";
                    // Structmeterupload.METERVOLTY    = "0.00";
                    // Structmeterupload.METERVOLTB    = "0.00";
                    // Structmeterupload.METERCURR     = "0.00";
                    // Structmeterupload.METERCURY     = "0.00";
                    // Structmeterupload.METERCURB     = "0.00";
                    Structmeterupload.TONGUEVOLTR   = "0.00";
                    Structmeterupload.TONGUEVOLTY   = "0.00";
                    Structmeterupload.TONGUEVOLTB   = "0.00";
                    Structmeterupload.TONGUECURR    = "0.00";
                    Structmeterupload.TONGUECURY    = "0.00";
                    Structmeterupload.TONGUECURB    = "0.00";

                    Toast.makeText(CurVoltageDetails.this, "Please Enter the Meter Current & Voltage ", Toast.LENGTH_SHORT).show();
                    //  Intent intent = new Intent(getApplicationContext(), SealDetails.class);
                    //  // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    //  // intent.putExtra("Position", 1);
                    //  startActivity(intent);
                    //  overridePendingTransition(R.anim.anim_slide_in_left,
                    //          R.anim.anim_slide_out_left);
                }


            }
        });

    }
    private boolean validate(EditText[] fields){
        for(int i=0; i<fields.length; i++){
            EditText currentField=fields[i];
            if(currentField.getText().toString().length()<=0){
                return false;
            }
        }
        return true;
    }
}
