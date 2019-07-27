package com.fedco.mbc.activitymetering;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.model.Structmetering;
import com.fedco.mbc.model.Structmeterupload;

import java.util.ArrayList;
import java.util.List;

public class SealDetails extends Activity {

    Button btn_Next, btn_back;
    EditText etOobs, etOibs, etOos, etOmdbs, etNobs, etNibs, etNos, etNmdbs;
    EditText[] etArr;
    String[] strArray;
    List<String> someList;
    Boolean duplicateval;
    RelativeLayout layout_clickforward, layout_clickbackward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seal_details);

        etOobs  = (EditText) findViewById(R.id.OSOBSValues);
        etOibs  = (EditText) findViewById(R.id.OSIBSValue);
        etOos   = (EditText) findViewById(R.id.OSOSValue);
        etOmdbs = (EditText) findViewById(R.id.OSMBRSValue);
        etNobs  = (EditText) findViewById(R.id.NSOBSValue);
        etNibs  = (EditText) findViewById(R.id.NSIBSValue);
        etNos   = (EditText) findViewById(R.id.NSOSValue);
        etNmdbs = (EditText) findViewById(R.id.NSMBRSValue);


        layout_clickforward = (RelativeLayout) findViewById(R.id.relButton);
        layout_clickbackward = (RelativeLayout) findViewById(R.id.relback);

        etArr = new EditText[]{etOobs, etOibs, etOos, etOmdbs, etNobs, etNibs, etNos, etNmdbs};

        layout_clickbackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CurVoltageDetails.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //intent.putExtra("Position", 1);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right);
            }
        });
        layout_clickforward.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                someList = new ArrayList<String>();
                someList.clear();
                duplicateval = false;

                final boolean fieldsOK = validate(etArr);

                if (fieldsOK) { //if all fields are filled


                    someList.add(etNobs.getText().toString());
                    someList.add(etNibs.getText().toString());
                    someList.add(etNos.getText().toString());
                    someList.add(etNmdbs.getText().toString());
                    someList.add(etOobs.getText().toString());
                    someList.add(etOibs.getText().toString());
                    someList.add(etOos.getText().toString());
                    someList.add(etOmdbs.getText().toString());

                    for (int i = 0; i < someList.size() - 1; i++) {
                        for (int j = i + 1; j < someList.size(); j++) {
                            if ((someList.get(i).equals(someList.get((j)))) && (i != j)) {
// System.out.println("Duplicate Element is : "+strArray[j]);
                                duplicateval = true;
                            }
                        }
                    }

                    if (duplicateval) {

                        Toast.makeText(SealDetails.this, "You cannot enter duplicate value", Toast.LENGTH_SHORT).show();

                    } else {

                        Structmeterupload.OUTTERBOXSEAL = etNobs.getText().toString();
                        Structmeterupload.INNERBOXSEAL  = etNibs.getText().toString();
                        Structmeterupload.OPTICALSEAL   = etNos.getText().toString();
                        Structmeterupload.MDBUTTONSEAL  = etNmdbs.getText().toString();
                        Structmeterupload.OLDOUTTERBOXSEAL  = etOobs.getText().toString();
                        Structmeterupload.OLDINNERBOXSEAL   = etOibs.getText().toString();
                        Structmeterupload.OLDOPTICALSEAL    = etOos.getText().toString();
                        Structmeterupload.OLDMDBUTTONSEAL   = etOmdbs.getText().toString();

                        // Perform action on click
                        Intent intent = new Intent(getApplicationContext(), Remarks.class);
                        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("Flag", true);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left);

                    }


                } else {//if all fields are not filled


                    if(etNobs.getText().toString().length() != 0)
                    {
                        someList.add(etNobs.getText().toString());
                    }
                    if(etNibs.getText().toString().length()!= 0){
                        someList.add(etNibs.getText().toString());
                    }
                    if(etNos.getText().toString().length()!= 0){
                        someList.add(etNos.getText().toString());
                    }
                    if(etNmdbs.getText().toString().length()!= 0){
                        someList.add(etNmdbs.getText().toString());
                    }
                    if(etOobs.getText().toString().length()!= 0){
                        someList.add(etOobs.getText().toString());
                    }
                    if(etOibs.getText().toString().length()!= 0){
                        someList.add(etOibs.getText().toString());
                    }
                    if(etOos.getText().toString().length()!= 0){
                        someList.add(etOos.getText().toString());
                    }
                    if(etOmdbs.getText().toString().length()!= 0){
                        someList.add(etOmdbs.getText().toString());
                    }

//                    someList.add(etNobs.getText().toString());
//                    someList.add(etNibs.getText().toString());
//                    someList.add(etNos.getText().toString());
//                    someList.add(etNmdbs.getText().toString());
//                    someList.add(etOobs.getText().toString());
//                    someList.add(etOibs.getText().toString());
//                    someList.add(etOos.getText().toString());
//                    someList.add(etOmdbs.getText().toString());

                    for (int i = 0; i < someList.size() - 1; i++) {
                        for (int j = i + 1; j < someList.size(); j++) {
                            if ((someList.get(i).equals(someList.get((j)))) && (i != j)) {
                                System.out.println("Duplicate Element is : "+someList.get(j));
                                duplicateval = true;
                            }
                        }
                    }

                    if (duplicateval) {

                        Toast.makeText(SealDetails.this, "You cannot enter duplicate value", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                        if(etNobs.getText().toString().length()== 0){
                            Structmeterupload.OUTTERBOXSEAL = Structmetering.OUTTERBOXSEAL;
                        }else{
                            Structmeterupload.OUTTERBOXSEAL = etNobs.getText().toString();
                        }
                        if(etNibs.getText().toString().length()== 0)
                        {
                            Structmeterupload.INNERBOXSEAL = Structmetering.INNERBOXSEAL;
                        }else{
                            Structmeterupload.INNERBOXSEAL = etNibs.getText().toString();
                        }
                        if(etNos.getText().toString().length()== 0)
                        {
                            Structmeterupload.OPTICALSEAL = Structmetering.OPTICALSEAL;
                        }else{
                            Structmeterupload.OPTICALSEAL = etNos.getText().toString();
                        }
                        if(etNmdbs.getText().toString().length()== 0)
                        {
                            Structmeterupload.MDBUTTONSEAL = Structmetering.MDBUTTONSEAL;
                        }else{
                            Structmeterupload.MDBUTTONSEAL = etNmdbs.getText().toString();
                        }
                        if(etOobs.getText().toString().length()== 0)
                        {
                            Structmeterupload.OLDOUTTERBOXSEAL = " ";
                        }else{
                            Structmeterupload.OLDOUTTERBOXSEAL = etOobs.getText().toString();
                        }
                        if(etOibs.getText().toString().length()== 0)
                        {
                            Structmeterupload.OLDINNERBOXSEAL = " ";
                        }else{
                            Structmeterupload.OLDINNERBOXSEAL = etOibs.getText().toString();
                        }
                        if(etOos.getText().toString().length()== 0)
                        {
                            Structmeterupload.OLDOPTICALSEAL = " ";
                        }else{
                            Structmeterupload.OLDOPTICALSEAL = etOos.getText().toString();
                        }
                        if(etOmdbs.getText().toString().length()== 0)
                        {
                            Structmeterupload.OLDMDBUTTONSEAL = " ";
                        }else{
                            Structmeterupload.OLDMDBUTTONSEAL = etOmdbs.getText().toString();
                        }


                            // Perform action on click
                            Intent intent = new Intent(getApplicationContext(), Remarks.class);
                            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("Flag", true);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);



                    }

                }

            }
        });

    }

    private boolean validate(EditText[] fields) {
        for (int i = 0; i < fields.length; i++) {
            EditText currentField = fields[i];
            if (currentField.getText().toString().length() <= 0) {
                return false;
            }
        }
        return true;
    }
}
