package com.fedco.mbc.activitymetering;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.authentication.SessionManager;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.CustomDialog;
import com.fedco.mbc.utils.DownloadCollectionFileFromURL;
import com.fedco.mbc.utils.DownloadFileFromURL;
import com.fedco.mbc.utils.DownloadMeterFileFromURL;
import com.fedco.mbc.utils.DownloadNMFileFromURL;
import com.fedco.mbc.utils.FileUpload;
import com.fedco.mbc.utils.HttpFileUpload;
import com.fedco.mbc.utils.UnzippingMeterFile;
import com.fedco.mbc.utils.UtilAppCommon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by nitinb on 30-12-2015.o
 */
public class NMActivity extends Activity {

    Button btn_upload,btn_download;
    TextView tv_pending1,tv_pending2;
    String keymap, name;
    TelephonyManager telephonyManager;
    String codeIMEI;
    SessionManager session;
    UtilAppCommon comApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sd);

        btn_upload=(Button)findViewById(R.id.btnProgressBarupload);
        btn_download=(Button)findViewById(R.id.btnProgressBar);

        tv_pending1=(TextView) findViewById(R.id.tvPend);
        tv_pending2=(TextView)findViewById(R.id.tvColPend);

        btn_upload.setVisibility(View.GONE);
        tv_pending1.setVisibility(View.GONE);
        tv_pending2.setVisibility(View.GONE);

        comApp  =   new UtilAppCommon();

        telephonyManager    = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        codeIMEI            = telephonyManager.getDeviceId();


        name        = comApp.UniqueCode(getApplicationContext());

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadNMFileFromURL(name,codeIMEI).execute();
            }
        });



    }

}
