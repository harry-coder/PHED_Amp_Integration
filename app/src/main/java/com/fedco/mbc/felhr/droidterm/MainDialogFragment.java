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
import com.fedco.mbc.felhr.droidterm.DroidTermBuild.BUILD_TYPE;

public class MainDialogFragment extends DialogFragment implements OnClickListener {
    public static final String CLASS_ID = MainDialogFragment.class.getSimpleName();
    private Button aboutButton;
    private IActivityCommunicator comm;
    private Button getProButton;
    private Button logViewerButton;
    private Button usbButton;
    private Button usbViewerButton;

    public interface IActivityCommunicator {
        void getButtonPressed(int i);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_dialog_layout, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.comm = (MainActivity) getActivity();
        this.usbButton = (Button) getView().findViewById(R.id.button_usb);
        this.usbViewerButton = (Button) getView().findViewById(R.id.button_viewer);
        this.logViewerButton = (Button) getView().findViewById(R.id.button_log_viewer);
        this.aboutButton = (Button) getView().findViewById(R.id.button_about);
        this.usbButton.setOnClickListener(this);
        this.usbViewerButton.setOnClickListener(this);
        this.logViewerButton.setOnClickListener(this);
        this.aboutButton.setOnClickListener(this);
        if (DroidTermBuild.TYPE == BUILD_TYPE.LITE) {
            this.getProButton = (Button) getView().findViewById(R.id.button_droidterm_pro);
            this.getProButton.setOnClickListener(this);
//            ((AdView) getView().findViewById(R.id.adView_main_dialog)).loadAd(new Builder().build());
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(1);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        return dialog;
    }

    public void onStart() {
        super.onStart();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_usb:
                this.comm.getButtonPressed(R.id.button_usb);
                return;
            case R.id.button_viewer:
                this.comm.getButtonPressed(R.id.button_viewer);
                return;
            case R.id.button_log_viewer:
                this.comm.getButtonPressed(R.id.button_log_viewer);
                return;
            case R.id.button_droidterm_pro:
                this.comm.getButtonPressed(R.id.button_droidterm_pro);
                return;
            case R.id.button_about:
                this.comm.getButtonPressed(R.id.button_about);
                return;
            default:
                return;
        }
    }
}
