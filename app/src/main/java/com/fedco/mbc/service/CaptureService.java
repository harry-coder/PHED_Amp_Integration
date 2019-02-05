package com.fedco.mbc.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.fedco.mbc.activity.GSBilling;
import com.fedco.mbc.logging.Logger;
import com.fedco.mbc.model.StructSurvey11KVUpload;
import com.fedco.mbc.model.StructSurveyConsumerUpload;
import com.fedco.mbc.model.StructSurveyDTRUpload;
import com.fedco.mbc.model.StructSurveyPoleUpload;
import com.fedco.mbc.model.Structbilling;
import com.fedco.mbc.model.Structcolmas;
import com.fedco.mbc.model.Structmeterupload;
import com.fedco.mbc.utils.GPSTracker;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by soubhagyarm on 27-02-2016.
 */

/**
 * Service is declaired to operate on background
 */
public class CaptureService extends Service {
    GPSTracker gps;
    int level;
    int SignalStrength = 0;
    myPhoneStateListener pslistener;
    TelephonyManager TelephonManager;
    Context context;
    Logger Log;
    PackageInfo pInfo = null;
    Handler mHandler;

    /**
     * Binder is involked for service binding
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Intializing the objects on creation of Service
     */
    @Override
    public void onCreate() {
        super.onCreate();

        //GPSTracker object involked

        pslistener = new myPhoneStateListener();

        TelephonManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        TelephonManager.listen(pslistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        SignalStrength = pslistener.SignalStrength;
        //Broadcast receiver involked
        this.registerReceiver(this.mBatInfoReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
//        String version = pInfo.versionName;

    }

    /**
     * Broadcast receiver is initalized and capturing battery level
     */
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {

            level = intent.getIntExtra("level", 0);
//            Log.e(context, "test", String.valueOf(level) + "%");

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /**
         * Thread Initialized to sync all outputs within perticular time interval
         */

        gps = new GPSTracker(CaptureService.this);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    startJob();
                    try {
                        Thread.sleep(1 * 30 * 1000);
                        if (gps.canGetLocation()) {

//                            Log.e(context, "CaptureService ", "Longitude : " + gps.getLongitude());
//                            Log.e(context, "CaptureService ", "Latitude  : " + gps.getLatitude());
//                            Log.e(context, "CaptureService ", "Accuracy  : " + gps.getAccuracy());
//                            Log.e(context, "CaptureService ", "Altitude  : " + gps.getAltitude());
//                            Log.e(context, "CaptureService ", "Time      : " + gps.getTime());
//                            Log.e(context, "CaptureService ", "Battery   : " + String.valueOf(level));
//                            Log.e(context, "CaptureService ", "Signal    : " + pslistener.SignalStrength);
//                            Log.e(context, "CaptureService ", "-----------------------------------------");

                            final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");
                            Date d = new Date(gps.getTime());
                            String sDate = sdf.format(d);

                            GSBilling.getInstance().setSignalStr("" + pslistener.SignalStrength);
                            GSBilling.getInstance().setBatStr(String.valueOf(level));
                            GSBilling.getInstance().setVerCode(pInfo.versionName);

                            Structbilling.BAT_STATE  = String.valueOf(level);
                            Structbilling.DSIG_STATE = "" + pslistener.SignalStrength;
                            Structbilling.VER_CODE   = pInfo.versionName;

                            Structcolmas.BAT_STATE   = String.valueOf(level);
                            Structcolmas.SIG_STATE   = "" + pslistener.SignalStrength;
                            Structcolmas.VER_CODE    = pInfo.versionName;

                            Structmeterupload.BATERY_STAT   = String.valueOf(level);
                            Structmeterupload.SIG_STRENGTH  = "" + pslistener.SignalStrength;
                            Structmeterupload.VER_CODE      = pInfo.versionName;

                            StructSurveyConsumerUpload.BAT_STR= String.valueOf(level);
                            StructSurveyConsumerUpload.SIGNAL_STR= "" + pslistener.SignalStrength;
                            StructSurveyConsumerUpload.VER_CODE   = pInfo.versionName;

                            StructSurveyDTRUpload. BAT_STR            = String.valueOf(level);
                            StructSurveyDTRUpload. SIGNAL_STR            ="" + pslistener.SignalStrength;
                            StructSurveyDTRUpload.VER_CODE= pInfo.versionName;

                            StructSurvey11KVUpload.BAT_STR              = String.valueOf(level);
                            StructSurvey11KVUpload.SIGNAL_STR         = "" + pslistener.SignalStrength;
                            StructSurvey11KVUpload. VER_CODE          = pInfo.versionName;

                            StructSurveyPoleUpload.BAT_STR        = String.valueOf(level);
                            StructSurveyPoleUpload.SIGNAL_STR     = "" + pslistener.SignalStrength;
                            StructSurveyPoleUpload.VER_CODE       = pInfo.versionName;

                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        t.start();
        return START_STICKY;
    }

    private void startJob() {
        //do job here


    }
}