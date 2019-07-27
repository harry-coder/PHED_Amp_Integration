package com.fedco.mbc.replacement;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.fedco.mbc.R;
import com.fedco.mbc.model.StructMeterReplacment;
import com.fedco.mbc.model.Structconsmas;
import com.fedco.mbc.utils.UtilAppCommon;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class MainActivity extends AppCompatActivity {

    TextView tvAcc, tvName, tvMn, tvIVRS;
    Button BtnContinue;
    InputMethodManager imm;

    FragmentPagerAdapter adapterViewPager;
    private static MainActivity sActivity;

    public static MainActivity getsActivity() {
        return sActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wizard);

        sActivity = this;
        setTitle("Meter Replacement");

        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        vpPager.setPageTransformer(true, new RotateUpTransformer());
        // vpPager.setPadding(5,5,5,5);

        tvAcc = (TextView) findViewById(R.id.TextViewACCValue);
        tvName = (TextView) findViewById(R.id.TextViewNameValue);
        tvMn = (TextView) findViewById(R.id.TextViewMNValue);
        tvIVRS = (TextView) findViewById(R.id.TextViewIVRSValue);

        BtnContinue = (Button) findViewById(R.id.ButtonContinue);

        String IVRS_NO = Structconsmas.LOC_CD + "" + Structconsmas.Consumer_Number;

        tvAcc.setText (" :  " + Structconsmas.Consumer_Number);
        tvIVRS.setText(" :  " + IVRS_NO);
        tvName.setText(" :  " + Structconsmas.Name);
        tvMn.setText  (" :  " + Structconsmas.Meter_S_No);

        StructMeterReplacment.CONSUMERNO = Structconsmas.Consumer_Number;
        StructMeterReplacment.IVRS_NO = IVRS_NO;
        StructMeterReplacment.LOCCD = Structconsmas.LOC_CD;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {


            if (valueCheckNew().equalsIgnoreCase("0")) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat fds = new SimpleDateFormat("kk:mm:ss");
                SimpleDateFormat fds1 = new SimpleDateFormat("dd-MM-yyyy");
                StructMeterReplacment.REPLACEMENT_DATE = fds1.format(c.getTime());
                StructMeterReplacment.REPLACEMENT_TIME = fds.format(c.getTime());

                Intent intent = new Intent();
                intent.setComponent(new ComponentName(getApplicationContext(), MeterView.class));
                startActivity(intent);

            } else {

                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("ALERT")
                        .setContentText(valueCheckNew())
                        .setConfirmText("OK")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.cancel();
                            }
                        }).show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @NonNull
    private String valueCheckNew() {

        if (!nullValueCheck(StructMeterReplacment.OLD_METER_NO, "Old Meter Number").equalsIgnoreCase("0")) {

            return " Old Meter Number is Empty";

        } else if (!nullValueCheck(StructMeterReplacment.OLD_METER_MAKE, "Old Meter Make").equalsIgnoreCase("0")) {

            return "Old Meter Make is Empty";

        } else if (!nullValueCheck(StructMeterReplacment.OLD_METER_TYPE, "Old Meter Type").equalsIgnoreCase("0")) {

            return "Old Meter Type is Empty";

        } else if (!nullValueCheck(StructMeterReplacment.OLD_METER_PHASE, "Old Meter Phase").equalsIgnoreCase("0")) {

            return " Old Meter Phase is Empty";

        } else if (!nullValueCheck(StructMeterReplacment.OLD_METER_CAPACITY, "Old Meter Capacity").equalsIgnoreCase("0")) {

            return "Old Meter Capacity is Empty";

        } else if (!nullValueCheck(StructMeterReplacment.OLD_METER_FINAL_READING, "Old Meter Optical Seal").equalsIgnoreCase("0")) {

            return " Old Meter Final Reading is Empty";

        } else if (!nullValueCheck(StructMeterReplacment.OLD_METER_OUTERBOX_SEAL, "Old Meter Outer Box Seal").equalsIgnoreCase("0")) {

            return " Old Meter OutterBox Seal is Empty";

        } else if (!nullValueCheck(StructMeterReplacment.OLD_METER_INNERBOX_SEAL, "Old Meter Inner Box Seal").equalsIgnoreCase("0")) {

            return " Old Meter InnerBox Seal is Empty";

        } else if (!nullValueCheck(StructMeterReplacment.OLD_METER_OPTICAL_SEAL, "Old Meter Optical Seal").equalsIgnoreCase("0")) {

            return " Old Meter Optical Seal is Empty";

        } else if (!nullValueCheck(StructMeterReplacment.OLD_METER_MDRESET_BUTTON_SEAL, "Old Meter Optical Seal").equalsIgnoreCase("0")) {

            return "Old Meter MD Seal is Empty";

        } else if (!nullValueCheck(StructMeterReplacment.OLD_METER_BODY_SEAL, "Old Meter Optical Seal").equalsIgnoreCase("0")) {

            return "Old Meter Body Seal is Empty";

        } else if (!nullValueCheck(StructMeterReplacment.NEW_METER_NO, "New Meter Number").equalsIgnoreCase("0")) {

            return "New Meter Number is Empty";

        } else if (!nullValueCheck(StructMeterReplacment.NEW_METER_MAKE, "New Meter Make").equalsIgnoreCase("0")) {

            return "New Meter Make is Empty";


        } else if (!nullValueCheck(StructMeterReplacment.NEW_METER_TYPE, "New Meter Type").equalsIgnoreCase("0")) {

            return "New Meter Type is Empty";


        } else if (!nullValueCheck(StructMeterReplacment.NEW_METER_PHASE, "New Meter Phase").equalsIgnoreCase("0")) {

            return " New Meter Phase is Empty";


        } else if (!nullValueCheck(StructMeterReplacment.NEW_METER_CAPACITY, "New Meter Capacity").equalsIgnoreCase("0")) {

            return "New Meter Capacity is Empty";

        } else if (!nullValueCheck(StructMeterReplacment.NEW_METER_FINAL_READING, "New Meter Optical Seal").equalsIgnoreCase("0")) {

            return "New Meter Initial Reading is Empty";


        } else if (!nullValueCheck(StructMeterReplacment.NEW_METER_OUTERBOX_SEAL, "New Meter Outer Box Seal").equalsIgnoreCase("0")) {

            return " New Meter OutterBox Seal is Empty";


        } else if (!nullValueCheck(StructMeterReplacment.NEW_METER_INNERBOX_SEAL, "New Meter Inner Box Seal").equalsIgnoreCase("0")) {

            return " New Meter InnerBox Seal is Empty";


        } else if (!nullValueCheck(StructMeterReplacment.NEW_METER_OPTICAL_SEAL, "New Meter Optical Seal").equalsIgnoreCase("0")) {

            return "New Meter Optical Seal is Empty";


        } else if (!nullValueCheck(StructMeterReplacment.NEW_METER_MDRESET_BUTTON_SEAL, "New Meter Optical Seal").equalsIgnoreCase("0")) {

            return "New Meter MD Seal is Empty";


        } else if (!nullValueCheck(StructMeterReplacment.NEW_METER_BODY_SEAL, "New Meter Optical Seal").equalsIgnoreCase("0")) {

            return " New Meter Body Seal is Empty";




        } else {
            return "0";
        }

    }

    private String nullValueCheck(String para, String referance) {

        if (para != null && !para.isEmpty()) {

            return "0";

        } else {

            return referance;
        }

    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return FragmentWithTwoImages.newInstance("Fragment 2", R.drawable.replaceidle);
                case 1:
                    return FragmentWithOneImage.newInstance("Fragment 1", R.drawable.replaceidle);

            }
            return FragmentWithOneImage.newInstance("Fragment 1", R.drawable.replaceidle);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "OLD";
                case 1:
                    return "NEW";
            }

            return "Tab " + position;
        }

    }
    @Override
    public void onBackPressed() {

        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("All Data will be")
                .setContentText(" erased ")
                .setConfirmText("OK")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        MainActivity.super.onBackPressed();

                        sDialog.cancel();
                        UtilAppCommon.null_Replacment();
                        UtilAppCommon.null_Replacment();
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(getApplicationContext(), Replacement.class));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left);


                    }
                }).show();



    }

}

