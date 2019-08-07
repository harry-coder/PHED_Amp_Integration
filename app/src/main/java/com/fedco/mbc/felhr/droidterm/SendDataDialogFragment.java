package com.fedco.mbc.felhr.droidterm;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.fedco.mbc.R;
import com.fedco.mbc.felhr.droidterm.DroidTermBuild.BUILD_TYPE;

public class SendDataDialogFragment extends DialogFragment implements OnClickListener {
    public static final String CLASS_ID = SendDataDialogFragment.class.getSimpleName();
    private Button backButton;
    private ISendDataCommunicator comm;
    private Button sendButton;
    private EditText text;

    public interface ISendDataCommunicator {
        void backToTerminal();

        void sendData(byte[] bArr);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.send_data_layout, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.comm = (ISendDataCommunicator) getActivity();
        this.text = (EditText) getView().findViewById(R.id.editTextSend);
        this.backButton = (Button) getView().findViewById(R.id.button_backMenu_terminal);
        this.sendButton = (Button) getView().findViewById(R.id.button_send_bulk);
        this.backButton.setOnClickListener(this);
        this.sendButton.setOnClickListener(this);
        this.text.setMaxLines(2);
        this.text.setVerticalScrollBarEnabled(true);
        this.text.setMovementMethod(new ScrollingMovementMethod());
        if (DroidTermBuild.TYPE == BUILD_TYPE.LITE) {
            //((AdView) getView().findViewById(R.id.adView_usb_send_data)).loadAd(new Builder().build());
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(1);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        return dialog;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_send_bulk:
                this.comm.sendData(this.text.getText().toString().getBytes());
                return;
            case R.id.button_backMenu_terminal:
                this.comm.backToTerminal();
                return;
            default:
                return;
        }
    }
}
