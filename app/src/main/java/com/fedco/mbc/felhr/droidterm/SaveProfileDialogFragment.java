package com.fedco.mbc.felhr.droidterm;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.fedco.mbc.R;
import com.fedco.mbc.felhr.droidterm.DroidTermBuild.BUILD_TYPE;

public class SaveProfileDialogFragment extends DialogFragment implements OnClickListener {
    public static final String CLASS_ID = SaveProfileDialogFragment.class.getSimpleName();
    private Button backButton;
    private ISaveProfileCommunicator comm;
    private EditText profileName;
    private Button saveProfileButton;

    public interface ISaveProfileCommunicator {
        void back();

        void saveProfile(String str);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.save_config_layout, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.comm = (ISaveProfileCommunicator) getActivity();
        this.profileName = (EditText) getView().findViewById(R.id.editTextProfileName);
        this.backButton = (Button) getView().findViewById(R.id.buttonBack);
        this.saveProfileButton = (Button) getView().findViewById(R.id.buttonSaveProfile);
        this.backButton.setOnClickListener(this);
        this.saveProfileButton.setOnClickListener(this);
        if (DroidTermBuild.TYPE == BUILD_TYPE.LITE) {
           // ((AdView) getView().findViewById(R.id.adView_usb_save_config)).loadAd(new Builder().build());
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
            case R.id.buttonSaveProfile:
                this.comm.saveProfile(this.profileName.getText().toString());
                return;
            case R.id.buttonBack:
                this.comm.back();
                return;
            default:
                return;
        }
    }
}
