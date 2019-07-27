package com.fedco.mbc.activitysurvey;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fedco.mbc.R;
import com.fedco.mbc.activity.CameraActivity;
import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.activity.MeterState;
import com.fedco.mbc.activity.Signature_Activity;
import com.fedco.mbc.activity.StartLocationAlert;
import com.fedco.mbc.activitymetering.Meteringtypes;
import com.fedco.mbc.activitymetering.Remarks;

import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.model.StructSurveyConsumerMaster;
import com.fedco.mbc.model.StructSurveyConsumerUpload;
import com.fedco.mbc.model.StructSurveyDTRUpload;
import com.fedco.mbc.model.StructSurveyPoleUpload;
import com.fedco.mbc.model.Structmeterupload;
import com.fedco.mbc.service.UploadService;
import com.fedco.mbc.sqlite.DB;
import com.fedco.mbc.utils.FileUpload;
import com.fedco.mbc.utils.GPSTracker;
import com.fedco.mbc.utils.HttpFileUpload;
import com.fedco.mbc.utils.UtilAppCommon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.fedco.mbc.activity.StartLocationAlert.REQUEST_CHECK_SETTINGS;

public class CameraView extends Activity {

    Button picBtn, nxtBtn;
    ImageView kwhImage, kvahImage;

    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    private File mFileTemp;
    String MRname;

    public int globalcount = 10;

    Context context;
    Logger Log;
    Boolean flagCam;
    String flagImage;
    private ProgressDialog progress;
    DB dbHelper, dbHelper2, dbHelper4;

    SQLiteDatabase  SD2, SD3;
    TextView kwhHint, mdHint ,lat_val,long_val;
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.bmp";
    public static final int REQUEST_CODE_GALLERY = 0x1;

    Button btn_bck;
    UtilAppCommon appCom;
    RelativeLayout back;

    String ZipSourcePath = Environment.getExternalStorageDirectory() + "/MBC/Images/"; //All image source path
    String ZipCopyPath = Environment.getExternalStorageDirectory() + "/MBC/MeterUpload/";
    String ZipImgLimitPathMeter = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesCS/"; //20 count image folder path
    String ZipImgCountPath = Environment.getExternalStorageDirectory() + "/MBC/LimitImagesCS"; //count zip folders path
    String ZipMetCSVPath = Environment.getExternalStorageDirectory() + "/MBC/CSCSV/"; //Single data CSV path
    //    public String ZipDesPath = Environment.getExternalStorageDirectory() + "/MBC/" + GSBilling.getInstance().getKEYNAME() + GSBilling.getInstance().captureDatetime() + ".zip"; //single CSV after zip Destination path
    public String ZipDesPath;
    //    String ZipDesPathdup = "/MBC/" + GSBilling.getInstance().getKEYNAME() + GSBilling.getInstance().captureDatetime(); // duplicate of zipDespath
    String ZipDesPathdup;
    GPSTracker gps, gps2;
    double latitude;
    double longitude;
    double latitude1 = 0.0;
    double longitude1 = 0.0;
    long gpstime;
    double altitude;
    double accuracy;
    UtilAppCommon comApp;
    SweetAlertDialog pDialog;
    SQLiteDatabase SD;
    DB databaseHelper;
    Context mContext;
    LocationManager locationManager;

    StartLocationAlert startLocationAlert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        nxtBtn = (Button) findViewById(R.id.ButtonNext);
        picBtn = (Button) findViewById(R.id.ButtonTakePic);
        nxtBtn.setVisibility(View.GONE);
        picBtn .setVisibility(View.GONE);

        kwhImage = (ImageView) findViewById(R.id.imageviewKWh);
        kvahImage = (ImageView) findViewById(R.id.imageviewKVAh);

        kwhHint = (TextView) findViewById(R.id.TextViewKWh);
        mdHint = (TextView) findViewById(R.id.TextViewwKVAh);
        lat_val = (TextView) findViewById(R.id.lat_val_txt);
        long_val = (TextView) findViewById(R.id.long_val_txt);

        databaseHelper = new DB(getApplicationContext());
        SD = databaseHelper.getWritableDatabase();
        comApp = new UtilAppCommon();
        dbHelper = new DB(getApplicationContext());
        MRname = comApp.UniqueCode(getApplicationContext());

        StructSurveyConsumerUpload.USER_ID =MRname;
        StructSurveyConsumerUpload.SURVEY_DT =getCurrentDateTime();

        ZipDesPath=Environment.getExternalStorageDirectory() + "/MBC/" + MRname + GSBilling.getInstance().captureDatetime() + ".zip"; //single CSV after zip Destination path
        ZipDesPathdup = "/MBC/" + MRname + GSBilling.getInstance().captureDatetime(); // duplicate of zipDespath


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        startLocationAlert = new StartLocationAlert(this);
        startLocationAlert.settingsrequest();

        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            this.sendBroadcast(poke);
        }

        btn_bck = (Button) findViewById(R.id.Buttonback);
        btn_bck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // finish activity
                overridePendingTransition(R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_right);
            }
        });
        nxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nxtBtn.setVisibility(View.GONE);
                picBtn.setVisibility(View.GONE);

                final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
                Date d = new Date();
                String sDate = sdf.format(d);

                StructSurveyConsumerUpload.SURVEY_DT = sDate;
                if(StructSurveyConsumerUpload.CON_MTR_IMAGE !=null && !StructSurveyConsumerUpload.CON_MTR_IMAGE.isEmpty()){

                    AlertUpload(mContext);

                }else{

                    Toast.makeText(getApplicationContext(), "Meter Image is Mandatory", Toast.LENGTH_SHORT).show();

                }

            }
        });


        picBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                nxtBtn.setVisibility(View.GONE);
                picBtn.setVisibility(View.GONE);

                final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
                Date d = new Date();
                String sDate = sdf.format(d);

                StructSurveyConsumerUpload.SURVEY_DT = sDate;
                if(StructSurveyConsumerUpload.CON_MTR_IMAGE !=null && !StructSurveyConsumerUpload.CON_MTR_IMAGE.isEmpty()){

                    Alert(mContext);
                }else{

                    Toast.makeText(getApplicationContext(), "Meter Image is Mandatory", Toast.LENGTH_SHORT).show();

                }

            }
        });


        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {

            mFileTemp = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);

        } else {

            mFileTemp = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
        }


        kwhImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
                flagImage = "kwh";
            }
        });
        kvahImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                takePicture();
                flagImage = "md";

            }
        });
    }

    private void takePicture() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            Uri mImageCaptureUri = null;
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                mImageCaptureUri = Uri.fromFile(mFileTemp);
            } else {
                /*
                 * The solution is taken from here: http://stackoverflow.com/questions/10042695/how-to-get-camera-result-as-a-uri-in-data-folder
	        	 */
//                mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        } catch (ActivityNotFoundException e) {

            Log.d(getApplicationContext(), "METERCAMVIEW", "cannot take picture", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        System.out.println("Request Code ____" + requestCode + "result Code ____" + requestCode);

        flagCam = true;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 2;
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[16 * 1024];

        Bitmap bitmap;

        switch (requestCode) {

            case REQUEST_CODE_TAKE_PICTURE:
                Log.v(getApplicationContext(), "", "AM in REQUEST_CODE_TAKE_PICTURE");

                if (flagCam) {
                    switch (flagImage) {
                        case "kwh":
                            bitmap = BitmapFactory.decodeFile(mFileTemp.getPath(), options);
                            File f = new File(Environment.getExternalStorageDirectory()
                                    + File.separator + "/MBC/Images/" + comApp.UniqueCode(getApplicationContext()) + "_" + StructSurveyConsumerUpload.Consumer_Number + "_MTR.jpg");
                            StructSurveyConsumerUpload.CON_MTR_IMAGE = comApp.UniqueCode(getApplicationContext()) + "_" +StructSurveyConsumerUpload.Consumer_Number + "_MTR.jpg";
                            try {
                                if (!f.exists()) {
//                                    f.mkdirs();
                                    f.createNewFile();
                                }
                            } catch (IOException e) {
                                Log.e(context, "Image Storage", "", e);
                            }
                            //write the bytes in file
                            FileOutputStream fo = null;
                            FileOutputStream os = null;
                            try {

                                os = new FileOutputStream(f);

                                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, os);
                                kwhImage.setImageBitmap(bitmap);
                                os.close();

                            } catch (FileNotFoundException e) {
                                Log.e(context, "FNF", "", e);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            break;
                        case "md":
//                            final BitmapFactory.Options options1 = new BitmapFactory.Options();
//                            options1.inJustDecodeBounds = true;
//                            options1.inSampleSize = 2;
//                            options1.inJustDecodeBounds = false;
//                            options1.inTempStorage = new byte[16 * 1024];
                            bitmap = BitmapFactory.decodeFile(mFileTemp.getPath(), options);
                            File file = new File(Environment.getExternalStorageDirectory()
                                    + File.separator + "/MBC/Images/" + comApp.UniqueCode(getApplicationContext()) + "_" + StructSurveyConsumerUpload.Consumer_Number+ "_PREM.jpg");
                            StructSurveyConsumerUpload.CON_PRE_IMAGE = comApp.UniqueCode(getApplicationContext()) + "_" +StructSurveyConsumerUpload.Consumer_Number+ "_PREM.jpg";
                            try {
                                if (!file.exists()) {
                                    file.createNewFile();
                                }
                            } catch (IOException e) {
                                Log.e(context, "Image Storage", "", e);
                            }
                            //write the bytes in file
                            FileOutputStream fokvah = null;
                            FileOutputStream oskvah = null;
                            try {

                                oskvah = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, oskvah);
//                                Toast.makeText(getApplicationContext(), "Image Saved Successfully..", Toast.LENGTH_LONG).show();
                                kvahImage.setImageBitmap(bitmap);
                                oskvah.close();

                            } catch (FileNotFoundException e) {
                                Log.e(context, "FNF", "", e);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
//                            nxtBtn.setVisibility(View.VISIBLE);
                            Log.e(getApplicationContext(),"CAMERA 1 "," "+latitude1);

                            pDialog = new SweetAlertDialog(CameraView.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#586ECA"));
                            pDialog.setTitleText("Capturing Location..");
                            pDialog.setCancelable(false);
                            pDialog.show();

                            if (isLocationEnabled(getApplicationContext())) {

                                //Check if Mobile Data is ON
                                if (isMobileDataEnabled()) {

                                    //Check if LAT & LONG is ON
                                    if (latitude1 == 0.00 && longitude1 == 0.00) {

                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                for (int i = 0; i <= 250; i++) {

                                                    GPSTracker gps3 = new GPSTracker(CameraView.this);

                                                    latitude1 = gps3.getLatitude();
                                                    longitude1 = gps3.getLongitude();
                                                    gpstime = gps3.getTime();
                                                    accuracy = gps3.getAccuracy();
                                                    altitude = gps3.getAltitude();

                                                    final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
                                                    Date d = new Date(gps3.getTime());
                                                    String sDate = sdf.format(d);

                                                    lat_val.setText(String.valueOf(latitude1));
                                                    long_val.setText(String.valueOf(longitude1));

                                                    StructSurveyConsumerUpload.USER_LONG = String.valueOf(longitude1);
                                                    StructSurveyConsumerUpload.USER_LAT  = String.valueOf(latitude1);
                                                    StructSurveyConsumerUpload.USER_GPS_DT   = sDate;
                                                    Log.e(getApplicationContext(),"CAMERA 10 "," "+latitude1);
                                                    Log.e(getApplicationContext(),"CAMERA 10"," "+longitude1);
                                                    Log.e(getApplicationContext(),"CAMERA 10"," "+sDate);
                                                    StructSurveyConsumerUpload.USER_ACCURACY = String.valueOf(accuracy);
                                                    StructSurveyConsumerUpload.USER_ALT      = String.valueOf(altitude);



                                                    if (latitude1 != 0.0) {
//                                                        nxtBtn.setVisibility(View.VISIBLE);
                                                        picBtn.setVisibility(View.VISIBLE);
                                                        pDialog.dismiss();
                                                        break; // A unlabeled break is enough. You don't need a labeled break here.
                                                    }

                                                }


                                            }
                                        }, 1 * 5 * 1000);

                                        pDialog.dismiss();

                                    }


                                } else {
                                    pDialog.dismiss();

                                    Intent intent = new Intent();
                                    intent.setComponent(new ComponentName(
                                            "com.android.settings",
                                            "com.android.settings.Settings$DataUsageSummaryActivity"));
                                    startActivity(intent);

                                }

                            } else {

                                startLocationAlert.settingsrequest();

                            }
                            break;
                    }


                } else {


                    bitmap = BitmapFactory.decodeFile(mFileTemp.getPath(), options);
                    File f = new File(Environment.getExternalStorageDirectory()
                            + File.separator + "/MBC/Images/" + comApp.UniqueCode(getApplicationContext()) + "_" +StructSurveyConsumerUpload.Consumer_Number + "_MTR.jpg");
                    StructSurveyConsumerUpload.CON_MTR_IMAGE = comApp.UniqueCode(getApplicationContext()) + "_" +StructSurveyConsumerUpload.Consumer_Number+ "_MTR.jpg";
                    Structmeterupload.IMAGE2 = "";
                    try {
                        f.createNewFile();
                    } catch (IOException e) {
                        Log.e(context, "Image Storage", "", e);
                    }
                    //write the bytes in file
                    FileOutputStream fo = null;
                    FileOutputStream os = null;
                    try {

                        os = new FileOutputStream(f);

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, os);
                        kwhImage.setImageBitmap(bitmap);
                        os.close();

                    } catch (FileNotFoundException e) {
                        Log.e(context, "FNF", "", e);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;

            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        //Check if LAT & LONG is ON

                        pDialog = new SweetAlertDialog(CameraView.this, SweetAlertDialog.PROGRESS_TYPE);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#586ECA"));
                        pDialog.setTitleText("Capturing Location..");
                        pDialog.setCancelable(false);
                        pDialog.show();

                        if (latitude1 == 0.00 && longitude1 == 0.00) {

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i <= 250; i++) {

                                        GPSTracker gps3 = new GPSTracker(CameraView.this);

                                        latitude1 = gps3.getLatitude();
                                        longitude1 = gps3.getLongitude();
                                        gpstime = gps3.getTime();
                                        accuracy = gps3.getAccuracy();
                                        altitude = gps3.getAltitude();

                                        final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
                                        Date d = new Date(gps3.getTime());
                                        String sDate = sdf.format(d);

                                        lat_val.setText(String.valueOf(latitude1));
                                        long_val.setText(String.valueOf(longitude1));

                                        StructSurveyConsumerUpload.USER_LONG = String.valueOf(longitude1);
                                        StructSurveyConsumerUpload.USER_LAT  = String.valueOf(latitude1);
                                        StructSurveyConsumerUpload.USER_GPS_DT   = sDate;
                                        Log.e(getApplicationContext(),"CAMERA 11 "," "+latitude1);
                                        Log.e(getApplicationContext(),"CAMERA 11" +
                                                ""," "+longitude1);
                                        Log.e(getApplicationContext(),"CAMERA 11"," "+sDate);
                                        StructSurveyConsumerUpload.USER_ACCURACY = String.valueOf(accuracy);
                                        StructSurveyConsumerUpload.USER_ALT      = String.valueOf(altitude);



                                        if (latitude1 != 0.0) {
                                            pDialog.dismiss();
//                                            nxtBtn.setVisibility(View.VISIBLE);
                                            picBtn.setVisibility(View.VISIBLE);
                                            break; // A unlabeled break is enough. You don't need a labeled break here.
                                        }

                                    }


                                }
                            }, 1 * 5 * 1000);



                        }

                        break;
                    case Activity.RESULT_CANCELED:

                        startLocationAlert.settingsrequest();//keep asking if imp or do whatever
                        break;
                }

                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getCurrentDateTime() {

        Calendar rightNow = Calendar.getInstance();
        long offset = rightNow.get(Calendar.ZONE_OFFSET) + rightNow.get(Calendar.DST_OFFSET);
        String sinceMidnight = Long.toString((rightNow.getTimeInMillis() + offset) % (24 * 60 * 60 * 1000));

        return sinceMidnight;
    }

    public void onBackPressed() {

        finish(); // finish activity
        this.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);

    }

    public boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    public Boolean isMobileDataEnabled() {
        Object connectivityService = getSystemService(CONNECTIVITY_SERVICE);
        ConnectivityManager cm = (ConnectivityManager) connectivityService;

        try {
            Class<?> c = Class.forName(cm.getClass().getName());
            Method m = c.getDeclaredMethod("getMobileDataEnabled");
            m.setAccessible(true);
            return (Boolean) m.invoke(cm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void Alert(final Context context) {

        dbHelper.insertSURVEYUPLOAD();    //Database Insert
        appCom.null_CONSUMER_Upload();

        new SweetAlertDialog(CameraView.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Stored")
                .setContentText(" Successfully ")
                .setConfirmText("OK")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        sDialog.cancel();

                        Intent intentUpload = new Intent(CameraView.this, UploadService.class);
                        startService(intentUpload);

                        Intent intent = new Intent(getApplicationContext(), SurveyDetails.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left);
                    }
                }).show();

//        new SweetAlertDialog(CameraView.this, SweetAlertDialog.WARNING_TYPE)
//                .setTitleText("Are you sure to Upload?")
//                .setContentText("the data will be sent to server")
//                .setCancelText("No")
//                .setConfirmText("Yes")
//                .showCancelButton(true)
//                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sDialog) {
//                        // reuse previous dialog instance, keep widget user state, reset them if you need
//                        sDialog.cancel();
//
//                        Toast.makeText(CameraView.this, "Internally Stored", Toast.LENGTH_SHORT).show();
//
//                        Intent intent = new Intent(getApplicationContext(), Meteringtypes.class);
////                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        // intent.putExtra("Position", 1);
//
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.anim_slide_in_left,
//                                R.anim.anim_slide_out_left);
//                    }
//                })
//                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sDialog) {
//
//                        sDialog.cancel();
//
//                        if(isMobileDataEnabled()){
//
//                            new TextFileMeterClass(CameraView.this).execute();
//
//                        }else{
//
//                            Toast.makeText(context, "Internally Stored Due to No-Connectivity", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(getApplicationContext(), Meteringtypes.class);
////                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            // intent.putExtra("Position", 1);
//
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.anim_slide_in_left,
//                                    R.anim.anim_slide_out_left);
//
//                        }
//
//
//                    }
//                })
//                .show();

    }

    public void AlertUpload(final Context context) {

        dbHelper.insertSURVEYUPLOAD();    //Database Insert
        appCom.null_CONSUMER_Upload();

        if(isMobileDataEnabled()){

            new TextFileMeterClass(CameraView.this).execute();

        }else{

            new SweetAlertDialog(CameraView.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Stored")
                    .setContentText(" Successfully ")
                    .setConfirmText("OK")
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {

                            sDialog.cancel();

                            Intent intent = new Intent(getApplicationContext(), SurveyDetails.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);
                        }
                    }).show();

        }

    }

    /*--------------------- Consumer Upload Text File Creation ----------------------------------*/
    private class TextFileMeterClass extends AsyncTask<String, Void, Void> {

        private final Context context;
        TextFileMeterClass(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {

            progress = new ProgressDialog(this.context);
            progress.setMessage("Uploading Please Wait..");
            progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                dbHelper2 = new DB(getApplicationContext());
                SD2 = dbHelper2.getWritableDatabase();

                String selquer = "SELECT * FROM TBL_CONSUMERSURVEY_UPOLOAD WHERE FLAG_UPLOAD='N'";//WHERE Upload_Flag='N'

                Cursor curMetselect = SD2.rawQuery(selquer, null);
                String arrStr[] = null;
                ArrayList<String> mylist = new ArrayList<String>();
                // Removefolder(ZipImgLimitPath);
                if (curMetselect != null && curMetselect.moveToFirst()) {
                    int counter = 0;
                    while (!curMetselect.isAfterLast()) {
                        counter++;
                        // String name = curBillselect.getString(0);

                        /*mylist.add(curMetselect.getString(0)  + "}" + curMetselect.getString(1)  + "}" + curMetselect.getString(2)  +
                                "}" + curMetselect.getString(3)  + "}" + curMetselect.getString(4)  + "}" + curMetselect.getString(5)  +
                                "}" + curMetselect.getString(6)  + "}" + curMetselect.getString(7)  + "}" + curMetselect.getString(8)  +
                                "}" + curMetselect.getString(9)  + "}" + curMetselect.getString(10) + "}" + curMetselect.getString(11) +
                                "}" + curMetselect.getString(12) + "}" + curMetselect.getString(13) + "}" + curMetselect.getString(14) +
                                "}" + curMetselect.getString(15) + "}" + curMetselect.getString(16) + "}" + curMetselect.getString(17) +
                                "}" + curMetselect.getString(18) + "}" + curMetselect.getString(19) + "}" + curMetselect.getString(20) +
                                "}" + curMetselect.getString(21) + "}" + curMetselect.getString(22) + "}" + curMetselect.getString(23) +
                                "}" + curMetselect.getString(24) + "}" + curMetselect.getString(25) + "}" + curMetselect.getString(26) +
                                "}" + curMetselect.getString(27) + "}" + curMetselect.getString(28) + "}" + curMetselect.getString(29) +
                                "}" + curMetselect.getString(30) + "}" + curMetselect.getString(31) + "}" + curMetselect.getString(32) +
                                "}" + curMetselect.getString(33) + "}" + curMetselect.getString(34) + "}" + curMetselect.getString(35) +
                                "}" + curMetselect.getString(36) + "}" + curMetselect.getString(37) + "}" + curMetselect.getString(38) +
                                "}" + curMetselect.getString(39) + "}" + curMetselect.getString(40) + "}" + curMetselect.getString(41) +
                                "}" + curMetselect.getString(42) + "}" + curMetselect.getString(43) + "}" + curMetselect.getString(44) +
                                "}" + curMetselect.getString(45) + "}" + curMetselect.getString(46) + "}" + curMetselect.getString(47) +
                                "}" + curMetselect.getString(48) + "}" + curMetselect.getString(49) + "}" + curMetselect.getString(50) +
                                "}" + curMetselect.getString(51) + "}" + curMetselect.getString(52) + "}" + curMetselect.getString(53) +
                                "}" + curMetselect.getString(54) + "}" + curMetselect.getString(55) + "}" + curMetselect.getString(56) +
                                "}" + curMetselect.getString(57) + "}" + curMetselect.getString(58) + "}" +curMetselect.getString(59) + "}" + curMetselect.getString(60) +
                                "}" + curMetselect.getString(61) + "}" + curMetselect.getString(62) + "}" + curMetselect.getString(63) +
                                "}" + curMetselect.getString(64) + "}" + curMetselect.getString(65) + "}" + curMetselect.getString(66) +
                                "}" + curMetselect.getString(67) + "}" + curMetselect.getString(68) + "}" + curMetselect.getString(69) +
                                "}" + curMetselect.getString(70) + "}" + curMetselect.getString(71) + "}" + curMetselect.getString(72)+
                                "}" + curMetselect.getString(73) + "}" + curMetselect.getString(74) + "}" + curMetselect.getString(75)+
                                "}" + curMetselect.getString(76)+
                                "}" + curMetselect.getString(77) + "}" + curMetselect.getString(78) + "}" + curMetselect.getString(79)+
                                "}" + curMetselect.getString(80) + "}" + curMetselect.getString(81)+ "}" + curMetselect.getString(82)+
                                "}" + curMetselect.getString(83) + "}" + curMetselect.getString(84)+ "}" + curMetselect.getString(85)+ "}" + curMetselect.getString(86)+
                                "}" + curMetselect.getString(87) + "}" + curMetselect.getString(88)+ "}" + curMetselect.getString(89)+ "}" + curMetselect.getString(90)+
                                "}" + curMetselect.getString(91) + "}" + curMetselect.getString(92)+ "}" + curMetselect.getString(93)+ "}"+"remark done");*/
                        mylist.add(curMetselect.getString(0)  + "}" + curMetselect.getString(1)  + "}" + curMetselect.getString(2)  +
                                "}" + curMetselect.getString(3)  + "}" + curMetselect.getString(4)  + "}" + curMetselect.getString(5)  +
                                "}" + curMetselect.getString(6)  + "}" + curMetselect.getString(7)  + "}" + curMetselect.getString(8)  +
                                "}" + curMetselect.getString(9)  + "}" + curMetselect.getString(10) + "}" + curMetselect.getString(11) +
                                "}" + curMetselect.getString(12) + "}" + curMetselect.getString(13) + "}" + curMetselect.getString(14) +
                                "}" + curMetselect.getString(15) + "}" + curMetselect.getString(16) + "}" + curMetselect.getString(17) +
                                "}" + curMetselect.getString(18) + "}" + curMetselect.getString(19) + "}" + curMetselect.getString(20) +
                                "}" + curMetselect.getString(21) + "}" + curMetselect.getString(22) + "}" + curMetselect.getString(23) +
                                "}" + curMetselect.getString(24) + "}" + curMetselect.getString(25) + "}" + curMetselect.getString(26) +
                                "}" + curMetselect.getString(27) + "}" + curMetselect.getString(28) + "}" + curMetselect.getString(29) +
                                "}" + curMetselect.getString(30) + "}" + curMetselect.getString(31) + "}" + curMetselect.getString(32) +
                                "}" + curMetselect.getString(33) + "}" + curMetselect.getString(34) + "}" + curMetselect.getString(35) +
                                "}" + curMetselect.getString(36) + "}" + curMetselect.getString(37) + "}" + curMetselect.getString(38) +
                                "}" + curMetselect.getString(39) + "}" + curMetselect.getString(40) + "}" + curMetselect.getString(41) +
                                "}" + curMetselect.getString(42) + "}" + curMetselect.getString(43) + "}" + curMetselect.getString(44) +
                                "}" + curMetselect.getString(45) + "}" + curMetselect.getString(46) + "}" + curMetselect.getString(47) +
                                "}" + curMetselect.getString(48) + "}" + curMetselect.getString(49) + "}" + curMetselect.getString(50) +
                                "}" + curMetselect.getString(51) + "}" + curMetselect.getString(52) + "}" + curMetselect.getString(53) +
                                "}" + curMetselect.getString(54) + "}" + curMetselect.getString(55) + "}" + curMetselect.getString(56) +
                                "}" + curMetselect.getString(57) + "}" + curMetselect.getString(58) + "}" + curMetselect.getString(59) +
                                "}" + curMetselect.getString(60) + "}" + curMetselect.getString(61) + "}" + curMetselect.getString(62) +
                                "}" + curMetselect.getString(63) + "}" + curMetselect.getString(64) + "}" + curMetselect.getString(65) +
                                "}" + curMetselect.getString(66) + "}" + curMetselect.getString(67) + "}" + curMetselect.getString(68) +
                                "}" + curMetselect.getString(69) + "}" + curMetselect.getString(70) + "}" + curMetselect.getString(71) +
                                "}" + curMetselect.getString(72) + "}" + curMetselect.getString(73) + "}" + curMetselect.getString(74) +
                                "}" + curMetselect.getString(75) + "}" + curMetselect.getString(76) + "}" + curMetselect.getString(77) +
                                "}" + curMetselect.getString(78) + "}" + curMetselect.getString(79) + "}" + curMetselect.getString(80) +
                                "}" + curMetselect.getString(81) + "}" + curMetselect.getString(82) + "}" + curMetselect.getString(83) +
                                "}" + curMetselect.getString(84) + "}" + curMetselect.getString(85) + "}" + curMetselect.getString(86) +
                                "}" + curMetselect.getString(87) + "}" + curMetselect.getString(88) + "}" + curMetselect.getString(89) +
                                "}" + curMetselect.getString(90) + "}" + curMetselect.getString(91) + "}" + curMetselect.getString(92) +
                                "}" + curMetselect.getString(93) + "}" + curMetselect.getString(94) + "}" +curMetselect.getString(76)  +
                                "}" + curMetselect.getString(96) + "}" + curMetselect.getString(97) + "}" + curMetselect.getString(98)+
                                "}" + curMetselect.getString(99)+"}" + curMetselect.getString(100)+"}" + curMetselect.getString(101)+
                                "}" + curMetselect.getString(102)+"}" + curMetselect.getString(103)+"}" + curMetselect.getString(104)+
                                "}" + curMetselect.getString(105)+"}" + curMetselect.getString(106)+"}" + curMetselect.getString(107)+
                                "}" + curMetselect.getString(108)+"}" + curMetselect.getString(109)+"}" + curMetselect.getString(110));

                        createfolder(ZipImgLimitPathMeter + "/" + MRname + GSBilling.getInstance().captureDate() + "_" + counter / globalcount);
                        moveFile(ZipSourcePath, curMetselect.getString(71), ZipImgLimitPathMeter + "/" + MRname + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");
                        moveFile(ZipSourcePath, curMetselect.getString(72), ZipImgLimitPathMeter + "/" + MRname + GSBilling.getInstance().captureDate() + "_" + counter / globalcount + "/");

                        curMetselect.moveToNext();

                    }
                    for (int i = 0; i <= counter / globalcount; i++) {

                        selectZipFolder(ZipImgLimitPathMeter + "/" + MRname + GSBilling.getInstance().captureDate() + "_" + i, ZipImgLimitPathMeter + "/" + MRname + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                    }
                    generateNoteMeterOnSD(getApplicationContext(), "consumer.csv", mylist);
                }

                CameraView.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progress.dismiss();
                        new PostMeterClass(CameraView.this).execute();
                    }
                });


            } catch (NullPointerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute() {
            progress.dismiss();
            // new PostClass(SDActivity.this).execute();
        }

    }

    /*--------------------- Consumer Uplaod Upload billing File  ----------------------------------*/
    public class PostMeterClass extends AsyncTask<String, Void, Boolean> {

        private final Context context;
        public String succsess = null;

        PostMeterClass(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Please Wait..");
            progress.show();

            zipFolder(ZipMetCSVPath, ZipDesPath);
            System.out.println("++++++++" + ZipDesPathdup);
            GSBilling.getInstance().setFinalZipName(ZipDesPathdup);

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip");
                HttpFileUpload hfu = new HttpFileUpload("http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles/", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
//                HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadMobileReadingFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                // HttpFileUpload hfu = new HttpFileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + GSBilling.getInstance().getFinalZipName(), ".zip");
                int status = hfu.Send_Now(fstrm);

                if (status != 200) {

                    CameraView.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();

                            new SweetAlertDialog(CameraView.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Stored")
                                    .setContentText(" Successfully ")
                                    .setConfirmText("OK")
                                    .showCancelButton(true)
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {

                                            sDialog.cancel();

                                            Intent intent = new Intent(getApplicationContext(), SurveyDetails.class);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.anim_slide_in_left,
                                                    R.anim.anim_slide_out_left);
                                        }
                                    }).show();


                        }
                    });

                } else {


                    File file = new File(ZipImgCountPath);
                    int filecount = CountRecursive(file);

                    for (int i = 0; i < filecount; i++) {

                        FileInputStream fstrm2 = null;
                        try {
                            fstrm2 = new FileInputStream(ZipImgLimitPathMeter + MRname + GSBilling.getInstance().captureDate() + "_" + i + ".zip");
                            FileUpload hfu2 = new FileUpload("http://enservmp.fedco.co.in/MPSurvey/api/UploadFile/UploadSurveyFiles", "" + ZipImgLimitPathMeter + MRname + GSBilling.getInstance().captureDate() + "_" + i, ".zip");
//                            FileUpload hfu2 = new FileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadMobileReadingFiles", "" + ZipImgLimitPathMeter + MRname + GSBilling.getInstance().captureDate() + "_" + i, ".zip");
                            // FileUpload hfu = new FileUpload("http://enserv.feedbackinfra.com/Webapi/api/UploadFile/UploadFiles", "" + ZipImgLimitPath + name + GSBilling.getInstance().captureDate() + "_" + i, ".zip");

                            int status2 = hfu2.Send_Now(fstrm2);
                            if (status2 != 200) {
                            } else {

                                String[] fileNames = new String[0];
                                File fileImage = new File(ZipImgLimitPathMeter + MRname + GSBilling.getInstance().captureDate() + "_" + i + "/");
                                if (fileImage.isDirectory()) {
                                    fileNames = fileImage.list();
                                }
                                int total = 0;
                                for (String fileName : fileNames) {
                                    if (fileName.contains("_MTR.jpg")) {

                                        dbHelper2 = new DB(getApplicationContext());
                                        SD2 = dbHelper2.getWritableDatabase();

                                        String updatequer = "UPDATE  TBL_CONSUMERSURVEY_UPOLOAD  SET FLAG_UPLOAD = 'Y' WHERE  Consumer_Number = '" + fileName.split("_")[1] + "';";

                                        Cursor update = SD2.rawQuery(updatequer, null);
                                        if (update != null && update.moveToFirst()) {
                                            android.util.Log.v("Update ", "Success");
                                        }
                                    }
                                }


                            }
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    }
                    CameraView.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();
                            // new UpdateUI().execute();
                            new SweetAlertDialog(CameraView.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Uploaded")
                                    .setContentText(" Successfully ")
                                    .setConfirmText("OK")
                                    .showCancelButton(true)
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {

                                            sDialog.cancel();

                                            Intent intent = new Intent(getApplicationContext(), SurveyDetails.class);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.anim_slide_in_left,
                                                    R.anim.anim_slide_out_left);
                                        }
                                    }).show();

                        }
                    });
                }


            } catch (IOException e) {
                e.printStackTrace();

            }
            return true;
        }

        protected void onPostExecute() {
            progress.dismiss();
//            File file = new File(ZipImgCountPath);
//            DeleteRecursive(file);
            new File(Environment.getExternalStorageDirectory().toString() + GSBilling.getInstance().getFinalZipName() + ".zip").delete();

        }

    }

    public void createfolder(String outputPath) {
        //create output directory if it doesn't exist
        File dir = new File(outputPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public int CountRecursive(File dir) {
        String[] fileNames;
        fileNames = dir.list();
        int total = 0;
        for (String fileName : fileNames) {
            if (fileName.contains(".zip")) {
                total++;
            }
        }
        Log.e(getApplicationContext(),"","1"+total);
        Log.e(getApplicationContext(),"","2"+fileNames.length);
        return total;
    }

    void DeleteRecursive(File dir) {

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                File temp = new File(dir, children[i]);
                if (temp.isDirectory()) {

                    DeleteRecursive(temp);

                } else {

                    boolean b = temp.delete();
                    if (b == false) {

                    }
                }
            }

        }
        dir.delete();
    }

    public static void zipFolder(String inputFolderPath, String outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outZipPath);
            ZipOutputStream zos = new ZipOutputStream(fos);
            File srcFile = new File(inputFolderPath);
            File[] files = srcFile.listFiles();
            android.util.Log.d("", "Zip directory: " + srcFile.getName());
            for (File file : files) {
                android.util.Log.d("", "Adding file: " + file.getName());
                byte[] buffer = new byte[1024];
                FileInputStream fis = new FileInputStream(file);
                zos.putNextEntry(new ZipEntry(file.getName()));
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

    public static void selectZipFolder(String inputFolderPath, String outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outZipPath);
            // GSBilling.getInstance().setFinalZipName();
            ZipOutputStream zos = new ZipOutputStream(fos);
            File srcFile = new File(inputFolderPath);
            File[] files = srcFile.listFiles();
            android.util.Log.d("", "Zip directory: " + srcFile.getName());
            for (File file : files) {
                android.util.Log.d("", "Adding file: " + file.getName());
                byte[] buffer = new byte[1024];
                FileInputStream fis = new FileInputStream(file);
                zos.putNextEntry(new ZipEntry(file.getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
        } catch (IOException ioe) {
            android.util.Log.e("", ioe.getMessage());
        }
    }

    public void generateNoteMeterOnSD(Context context, String sFileName, ArrayList sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "MBC/CSCSV/");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            int length = sBody.size();
            for (int i = 0; i < length; i++) {
                System.out.println("selqwer1234 " + sBody.get(i));

                writer.append(sBody.get(i).toString());
                writer.append("\n");
            }

            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void moveFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            ////  delete the original file
            // new File(inputPath + inputFile).delete();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
