package com.fedco.mbc.activitymetering;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.MeterChangeReportActivity;
import com.fedco.mbc.activity.NewConsumerReportActivity;
import com.fedco.mbc.activity.PrintSelection;
import com.fedco.mbc.activity.UnbilledConsumerReportActivity;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.UtilAppCommon;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Report extends Activity {

    HashMap<String, String> hmconsmasData;
    static final ArrayList<HashMap<String, String>> allist = new ArrayList<HashMap<String, String>>();
    final Context context = this;
    UtilAppCommon uac;
    private Boolean exit = false;
    Button btnExit, btnBack, btn_NewConsumer, btn_MeterChange, btn_UnbuildConsumer;
    String accountnum;
    Logger Log;
    SQLiteDatabase SD;
    DB dbHelper;
    String divisionpre,subdivisionpre,sectionpre,cyclepre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporttypes);
//        try {
//            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
//            getSupportActionBar().setLogo(R.mipmap.logo);
//            setTitle("Reports");
//        } catch (NullPointerException npe) {
//            Log.e(getApplicationContext(), "Billing Act", "ActionBar Throwing null Pointer", npe);
//        }

        /****BUTTON  INITIALISATION****/
        btn_NewConsumer = (Button) findViewById(R.id.ButtonNewC);
        btn_MeterChange = (Button) findViewById(R.id.ButtonMeterC);
        btn_UnbuildConsumer = (Button) findViewById(R.id.ButtonUnbuildC);
        //btnExit =(Button)findViewById(R.id.ButtonExit);
        btnBack =(Button)findViewById(R.id.Buttonback);

        divisionpre = getIntent().getStringExtra("division");
        subdivisionpre = getIntent().getStringExtra("subdivsion");
        sectionpre = getIntent().getStringExtra("section");
        cyclepre = getIntent().getStringExtra("cycle");

        /****BUTTON  ACTION   EVENTS****/
        btn_NewConsumer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NSCReport.class);

                intent.putExtra("division", divisionpre);
                intent.putExtra("subdivsion", subdivisionpre);
                intent.putExtra("section", sectionpre);
                intent.putExtra("cycle", cyclepre);

//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });
        btn_MeterChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MCReport.class);

                intent.putExtra("division", divisionpre);
                intent.putExtra("subdivsion", subdivisionpre);
                intent.putExtra("section", sectionpre);
                intent.putExtra("cycle", cyclepre);

//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });
        btn_UnbuildConsumer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UnMeterReport.class);

                intent.putExtra("division", divisionpre);
                intent.putExtra("subdivsion", subdivisionpre);
                intent.putExtra("section", sectionpre);
                intent.putExtra("cycle", cyclepre);

//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // finish activity
                overridePendingTransition(R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right);
            }
        });


    }


    public void onBackPressed() {
//        if (exit) {
        finish(); // finish activity
        overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Debug.stopMethodTracing();
        super.onDestroy();
    }

}
