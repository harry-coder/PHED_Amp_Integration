package com.fedco.mbc.felhr.droidterm;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.fedco.mbc.R;
import com.fedco.mbc.felhr.droidterm.ActivityInterfaces.INoUsbDialogCommunicator;
import com.fedco.mbc.felhr.droidterm.DroidTermBuild.BUILD_TYPE;

public class UsbNotCompatibleFragment extends DialogFragment implements INoUsbDialogCommunicator, OnClickListener {
    public static final String CLASS_ID = UsbNotCompatibleFragment.class.getSimpleName();
    private INotUsbCommunicator comm;
    private Button displayButton;
    private String message;

    public interface INotUsbCommunicator {
        void getButtonPressed();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.usb_not_compatible_layout, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.comm = (MainActivity) getActivity();
        this.displayButton = (Button) getView().findViewById(R.id.no_usb_message);
        this.displayButton.setOnClickListener(this);
        if (this.message != null) {
            this.displayButton.setText(this.message);
        }
        if (DroidTermBuild.TYPE == BUILD_TYPE.LITE) {
           //((AdView) getView().findViewById(C0107R.id.adView_usb_not_compatible)).loadAd(new Builder().build());
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(1);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        return dialog;
    }

    public void sendMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void onClick(View v) {
        this.comm.getButtonPressed();
    }
}
