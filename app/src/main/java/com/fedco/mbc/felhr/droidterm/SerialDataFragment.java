package com.fedco.mbc.felhr.droidterm;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fedco.mbc.R;
import com.fedco.mbc.felhr.droidterm.DroidTermBuild.BUILD_TYPE;

public class SerialDataFragment extends Fragment {
    private final int LIGHT_DELAY = 100;
    private Bundle savedData;
    private TextView usbDevice;

    class C01081 implements Runnable {
        C01081() {
        }

        public void run() {
            SerialDataFragment.this.usbDevice.setBackgroundResource(R.drawable.device_shape);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.serial_info, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.usbDevice = (TextView) getView().findViewById(R.id.usb_device_text);
        if (savedInstanceState != null) {
            this.savedData = savedInstanceState;
        }
        if (DroidTermBuild.TYPE == BUILD_TYPE.LITE) {
            //((AdView) getView().findViewById(C0107R.id.adView_usb_device_info)).loadAd(new Builder().build());
        }
    }

    public void onStart() {
        super.onStart();
        if (this.savedData != null) {
            setUsbDevice(this.savedData.getString(MainActivity.DEVICE_NAME_KEY));
            this.savedData = null;
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(MainActivity.DEVICE_NAME_KEY, getUsbDevice());
        super.onSaveInstanceState(savedInstanceState);
    }

    public void setUsbDevice(String text) {
        this.usbDevice.setText(text);
    }

    public void lightIndicator() {
        this.usbDevice.setBackgroundResource(R.drawable.device_shape_2);
        new Handler().postDelayed(new C01081(), 100);
    }

    public String getUsbDevice() {
        return String.valueOf(this.usbDevice.getText());
    }

    public void reset() {
    }

    public void requestFocus() {
    }

    public boolean isEchoOn() {
        return false;
    }
}
