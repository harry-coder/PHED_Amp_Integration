package com.fedco.mbc.activity;

/* printer Category -
* 0-Impact
* 1-Thermal
*  printer MFG-
* 0-amigos
* 1-Rego
* 2-Sil
*  printer Roll
* 0-White
* 1-Pre-print
* */


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.authentication.PrinterSessionManager;
import com.fedco.mbc.sqlite.DB;

public class PrintSelection extends AppCompatActivity {

    RadioGroup rg_imp, rg_therm;
    Switch switch_therm, switch_imp;
    CheckBox cb_therm, cb_imp;
    TextView txt_roll_therm, txt_roll_imp, txt_ther_desc, txt_imp_desc;
    RadioButton rb_amigos, rd_rego, rd_sil, rd_evo, rd_ana;
    LinearLayout ll_printer_imp_cat, ll_printer_imp_roll, ll_printer_ther_cat, ll_printer_ther_roll;

    PrinterSessionManager printsession;
    Button OK;

    DB dbHelper;
    SQLiteDatabase SD;

    String printer_catergory, printer_mfg, printer_roll = "-1", prev_pref;

    private void addView() {

        ll_printer_imp_cat = (LinearLayout) findViewById(R.id.LL_print_imp_cat);
        ll_printer_imp_roll = (LinearLayout) findViewById(R.id.LL_print_imp_roll);
        ll_printer_ther_cat = (LinearLayout) findViewById(R.id.LL_print_ther_cat);
        ll_printer_ther_roll = (LinearLayout) findViewById(R.id.LL_print_ther_roll);

        cb_therm = (CheckBox) findViewById(R.id.cb_ther);
        cb_imp = (CheckBox) findViewById(R.id.cb_imp);

        rg_imp = (RadioGroup) findViewById(R.id.radio_group_imp);
        rg_therm = (RadioGroup) findViewById(R.id.radio_group_therm);

        rb_amigos = (RadioButton) findViewById(R.id.rbtn_amigos);
        rd_rego = (RadioButton) findViewById(R.id.rbtn_rego);
        rd_sil = (RadioButton) findViewById(R.id.rbtn_sil);
        rd_evo = (RadioButton) findViewById(R.id.rb_evo);
        rd_ana = (RadioButton) findViewById(R.id.rb_ana);

        switch_therm = (Switch) findViewById(R.id.switch_roll_typ_therm);
        switch_imp = (Switch) findViewById(R.id.switch_roll_typ_imp);

        txt_roll_therm = (TextView) findViewById(R.id.TXT_ROLL_TYP_THERM);
        txt_roll_imp = (TextView) findViewById(R.id.TXT_ROLL_TYP_IMP);
        txt_ther_desc = (TextView) findViewById(R.id.TXT_THER);
        txt_imp_desc = (TextView) findViewById(R.id.TXT_IMP_VAL);

        OK = (Button) findViewById(R.id.ButtonOK);

        ll_printer_imp_cat.setVisibility(View.GONE);
        ll_printer_imp_roll.setVisibility(View.GONE);

        ll_printer_ther_cat.setEnabled(false);
        ll_printer_ther_roll.setEnabled(false);


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_selection);

        addView();


        dbHelper = new DB(getApplicationContext());
        SD = dbHelper.getWritableDatabase();

        String strPref = "SELECT PRINTER_PREF FROM USER_MASTER";
        Log.e("Sequence", "update " + strPref);

        Cursor getPref = SD.rawQuery(strPref, null);

        if (getPref != null && getPref.moveToFirst()) {

            prev_pref = getPref.getString(0);

        }

        autoCheck();

        cb_imp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    ll_printer_imp_cat.setVisibility(View.VISIBLE);
                    printer_catergory = "0";

                } else {

                    if (prev_pref != null && !prev_pref.isEmpty()) {

                        if (prev_pref.split("_")[0].equalsIgnoreCase("0")) {
                            cb_imp.setChecked(true);
                        }

                    } else {

                        ll_printer_imp_cat.setVisibility(View.GONE);
                        ll_printer_imp_roll.setVisibility(View.GONE);

                    }


                }
            }
        });

        rg_imp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                if (checkedId == -1) {

//                    ll_printer_imp_roll.setVisibility(View.GONE);

                } else {

//                    ll_printer_imp_roll.setVisibility(View.VISIBLE);

//                    int selectedId = rg_imp.getCheckedRadioButtonId();
//
//                    // find the radiobutton by returned id
//                    RadioButton radioButton = (RadioButton) findViewById(selectedId);
//                    if(radioButton.getText().toString().equalsIgnoreCase("Amigos")){
//                        printer_mfg ="0";
//                    }else if(radioButton.getText().toString().equalsIgnoreCase("Amigos")){
//                        printer_mfg ="1";
//                    }else if(radioButton.getText().toString().equalsIgnoreCase("Amigos")){
//                        printer_mfg ="2";
//                    }

                }

            }
        });

//        switch_imp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//                if (isChecked) {
//
//                    txt_roll_imp.setText("White roll");
//                    printer_roll="0";
//                } else {
//
//                    txt_roll_imp.setText("Pre printed roll");
//                    printer_roll="1";
//                }
//
//            }
//        });
        printer_roll = "1";

        printsession = new PrinterSessionManager(getApplicationContext());

//        rg = (RadioGroup) findViewById(R.id.radio_group);
//        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
//
//                if (checkedId == R.id.RBThermal) {
//                    Toast.makeText(getApplicationContext(), "Configuration is Under Process", Toast.LENGTH_LONG).show();
//                    /****DIALOG BOX INITIALIZATION PROVIDING A LAYOUT TO DIALOUGE****/
//                    final Dialog dialogAccount = new Dialog(PrintSelection.this, R.style.DialogeAppTheme);
////                dialogAccount.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
////                dialogAccount.getWindow().getAttributes().windowAnimations = R.anim.anim_slide_out_left;
//
//                    dialogAccount.setContentView(R.layout.radiobutton_dialog);
//                    dialogAccount.setTitle("Print Configuration");
//
////                    RadioGroup rg = (RadioGroup) dialogAccount.findViewById(R.id.radio_group);
//                    final RadioGroup rbEv = (RadioGroup) dialogAccount.findViewById(R.id.RBTherType);
//                    rbEv.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                        @Override
//                        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
//                            if (checkedId == R.id.RBTEvolute) {
//                                String printer_type = "1";//preprint
//                                String print_type = "0";//preprint
//                                System.out.println("in 1" + printer_type + "" + print_type);
//                                printsession.createLoginSession(printer_type, print_type);
//                            } else if (checkedId == R.id.RBTAnalogics) {
//                                String printer_type = "1";//preprint
//                                String print_type = "1";//preprint
//                                System.out.println("in 1" + printer_type + "" + print_type);
//                                printsession.createLoginSession(printer_type, print_type);
//                            }
//                        }
//                    });
//
//
////                    rbEv.setVisibility(View.GONE);
////                    rbAna.setVisibility(View.GONE);
//
////                    rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
////                        @Override
////                        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
////
////                            if (checkedId == R.id.RBThermal) {
////                                rbEv.setVisibility(View.VISIBLE);
////                                rbAna.setVisibility(View.GONE);
////                                Thermal_enable = "1";
////                                if(checkedId == R.id.RBTEvolute){
////                                    print_type = "1";//preprint
////                                    System.out.println("in 1"+Thermal_enable+""+print_type);
////                                    printsession.createLoginSession(Thermal_enable, print_type);
////                                }else {
////                                    print_type = "0";//preprint
////                                    System.out.println("in 1"+Thermal_enable+""+print_type);
////                                    printsession.createLoginSession(Thermal_enable, print_type);
////                                }
////
////                            } else if (checkedId == R.id.RBImpact) {
////                                rbAna.setVisibility(View.VISIBLE);
////                                rbEv.setVisibility(View.GONE);
////                                Thermal_enable = "0";
////                                if(checkedId == R.id.RBTpreprint){
////                                    print_type = "1";//preprint
////                                    System.out.println("in 0"+Thermal_enable+""+print_type);
////                                    printsession.createLoginSession(Thermal_enable, print_type);
////                                }else {
////                                    print_type = "0";//postprint
////                                    System.out.println("in 0"+Thermal_enable+""+print_type);
////                                    printsession.createLoginSession(Thermal_enable, print_type);
////                                }
////
////                            }
////
////                        }
////                    });
//                    Button dialogButton = (Button) dialogAccount.findViewById(R.id.dialogButtonOK);
//                    Button dialogButtoncan = (Button) dialogAccount.findViewById(R.id.dialogButtonCancel);
//
//                    /****DIALOUGE BOX BUTTON INTIALISATION AND EVENT ADDING****/
//                    dialogButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(getApplicationContext(), Billing.class);
//                            intent.putExtra("flowkey", "billing");
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                    R.anim.anim_slide_out_left);
//                            dialogAccount.dismiss();
//                        }
//                    });
////                    dialogAccount.show();
//                    /****DIALOUGE BOX BUTTON INTIALISATION AND EVENT ADDING****/
//                    dialogButtoncan.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            dialogAccount.dismiss();
//                        }
//                    });
//                    dialogAccount.show();
//                } else if (checkedId == R.id.RBImpact) {
//                    final Dialog dialogAccount = new Dialog(PrintSelection.this, R.style.DialogeAppTheme);
////                dialogAccount.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
////                dialogAccount.getWindow().getAttributes().windowAnimations = R.anim.anim_slide_out_left;
//
//                    dialogAccount.setContentView(R.layout.selectradbtn_dialog);
//                    dialogAccount.setTitle("Print Configuration");
//
//                    RadioGroup rg = (RadioGroup) dialogAccount.findViewById(R.id.radio_group);
////                    final RadioGroup rbEv=(RadioGroup)dialogAccount.findViewById(R.id.RBTherType);
//                    rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                        @Override
//                        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
//                            if (checkedId == R.id.RBTpreprint) {
//                                String printer_type = "0";//preprint
//                                String print_type = "0";//preprint
//                                System.out.println("in RBTPREPrint ----" + printer_type + "" + print_type);
//                                printsession.createLoginSession(printer_type, print_type);
//                            } else if (checkedId == R.id.RBTpostprint) {
//                                String printer_type = "0";//preprint
//                                String print_type = "1";//preprint
//                                System.out.println("in RBTPOSTPrint ----" + printer_type + "" + print_type);
//                                printsession.createLoginSession(printer_type, print_type);
//                            }
//                        }
//                    });
//
//
//                    Button dialogButton = (Button) dialogAccount.findViewById(R.id.printDROK);
//                    Button dialogButtoncan = (Button) dialogAccount.findViewById(R.id.printDRCANCEL);
//
//                    /****DIALOUGE BOX BUTTON INTIALISATION AND EVENT ADDING****/
//                    dialogButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
////                            printsession.createLoginSession(printer_type, print_type);
//                            Intent intent = new Intent(getApplicationContext(), Billing.class);
//                            intent.putExtra("flowkey", "billing");
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                    R.anim.anim_slide_out_left);
//                            dialogAccount.dismiss();
//                        }
//                    });
////                    dialogAccount.show();
//                    /****DIALOUGE BOX BUTTON INTIALISATION AND EVENT ADDING****/
//                    dialogButtoncan.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            dialogAccount.dismiss();
//                        }
//                    });
//                    dialogAccount.show();
//                }
//
//            }
//        });
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!cb_imp.isChecked()) {
                    Toast.makeText(PrintSelection.this, "Please select type", Toast.LENGTH_SHORT).show();
                } else if (rg_imp.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(PrintSelection.this, "Please select printer", Toast.LENGTH_SHORT).show();
                } else if (printer_roll.equalsIgnoreCase("-1")) {
                    Toast.makeText(PrintSelection.this, "Please select type of roll", Toast.LENGTH_SHORT).show();
                } else {

                    int selectedId = rg_imp.getCheckedRadioButtonId();

                    // find the radiobutton by returned id
                    RadioButton radioButton = (RadioButton) findViewById(selectedId);
                    if (radioButton.getText().toString().equalsIgnoreCase("Amigos")) {
                        printer_mfg = "0";
                    } else if (radioButton.getText().toString().equalsIgnoreCase("Rego")) {
                        printer_mfg = "1";
                    } else if (radioButton.getText().toString().equalsIgnoreCase("Softland")) {
                        printer_mfg = "2";
                    }

                    try {

                        SD.beginTransaction();


                        String str = "UPDATE USER_MASTER SET PRINTER_PREF ='" + printer_catergory + "_" + printer_mfg + "_" + printer_roll + "'";
                        Log.e("Sequence", "update " + str);

                        SD.execSQL(str);
                        SD.setTransactionSuccessful();

                    } catch (Exception ex) {

                        Log.e("", "insert into table operation", ex);

                    } finally {

                        SD.endTransaction();
                        SD.close();
                    }

                    if (GSBilling.getInstance().getAappFlow().equalsIgnoreCase("billing")) {
                        Intent intent = new Intent(getApplicationContext(), Billing.class);
                        intent.putExtra("flowkey", "billing");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left);

                    } else {
                        Intent intent = new Intent(getApplicationContext(), Collection.class);
                        intent.putExtra("flowkey", "billing");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left);

                    }
                }

            }
        });
    }

    private void autoCheck() {
        String printerType, printerMfg = null, printerRoll;
        String printerTypeCode = prev_pref.split("_")[0];
        String printerMfgCode = prev_pref.split("_")[1];
        String printerRollCode = prev_pref.split("_")[2];

        if (printerTypeCode.equalsIgnoreCase("0")) {
            printerType = "Imapct";
        } else {
            printerType = "Thermal";
        }

        if (printerRollCode.equalsIgnoreCase("0")) {
            printerRoll = "Pre print roll";
        } else {
            printerRoll = "White roll";
        }

        switch (printerMfgCode.toString()) {
            case "0":
                printerMfg = "Amigos";
                break;
            case "1":
                printerMfg = "Rego";
                break;
            case "2":
                printerMfg = "Softland";
                break;
        }

        String Prev_pref_text = printerMfg + " " + printerType + " " + printerRoll;
        txt_imp_desc.setText(Prev_pref_text);

//        if(prev_pref!=null && !prev_pref.isEmpty()) {
//
//            if (prev_pref.split("_")[0].equalsIgnoreCase("0")) {
//                cb_imp.setChecked(true);
//            }
//
//        }


    }

}
