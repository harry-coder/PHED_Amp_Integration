package com.fedco.mbc.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * Created by soubhagyarm on 01-08-2016.
 */
class myPhoneStateListener extends PhoneStateListener {

    TelephonyManager TelephonManager;
    myPhoneStateListener pslistener;
    int SignalStrength = 0;
    public int signalStr;
    public String batteryLev;

    @Override
    public void onSignalStrengthsChanged(android.telephony.SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        SignalStrength = signalStrength.getGsmSignalStrength();
        SignalStrength = (2 * SignalStrength) - 113; // -> dBm
        this.signalStr=SignalStrength;

    }


}
