package com.fedco.mbc.felhr.droidterm;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.fedco.mbc.R;
import com.fedco.mbc.felhr.droidterm.ActivityInterfaces.ISendCurrentInput;
import com.fedco.mbc.felhr.droidterm.DroidTermBuild.BUILD_TYPE;

public class InputFragment extends DialogFragment implements OnClickListener, ISendCurrentInput {
    public static final String CLASS_ID = InputFragment.class.getSimpleName();
    private static final String INPUT_MODE = "input_mode";
    private static final String INPUT_PREFERENCES = "input_preferences";
    private IInputFragmentCommunicator comm;
    private int inputButton;
    private RadioGroup inputGroup;
    private Button saveInputButton;
    private SharedPreferences sharedPreferences;

    public interface IInputFragmentCommunicator {
        void configInput(int i);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.input_dialog, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.comm = (IInputFragmentCommunicator) getActivity();
        this.inputGroup = (RadioGroup) getView().findViewById(R.id.radioGroup1);
        this.saveInputButton = (Button) getView().findViewById(R.id.buttonSaveInput);
        this.saveInputButton.setOnClickListener(this);
        this.sharedPreferences = getActivity().getSharedPreferences(INPUT_PREFERENCES, 0);
        int inputMode = this.sharedPreferences.getInt(INPUT_MODE, 0);
        this.inputButton = inputMode;
        setCheckedOption(inputMode);
        BUILD_TYPE build_type = DroidTermBuild.TYPE;
        BUILD_TYPE build_type2 = DroidTermBuild.TYPE;
        if (build_type == BUILD_TYPE.LITE) {
            //((AdView) getView().findViewById(R.id.adView_usb_input)).loadAd(new Builder().build());
        }
    }

    public void onStart() {
        super.onStart();
        if (this.inputButton > 2) {
            this.inputButton = 0;
            saveInputOption(0);
        }
        ((RadioButton) this.inputGroup.getChildAt(this.inputButton)).setChecked(true);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(1);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        return dialog;
    }

    public void onClick(View v) {
        if (this.inputGroup.getCheckedRadioButtonId() != -1) {
            switch (this.inputGroup.indexOfChild(this.inputGroup.findViewById(this.inputGroup.getCheckedRadioButtonId()))) {
                case 0:
                    saveInputOption(0);
                    this.comm.configInput(0);
                    return;
                case 1:
                    saveInputOption(1);
                    this.comm.configInput(1);
                    return;
                case 2:
                    saveInputOption(2);
                    this.comm.configInput(2);
                    return;
                default:
                    return;
            }
        }
    }

    public void sendInput(int number) {
        this.inputButton = number;
    }

    private void saveInputOption(int id) {
        Editor editor = this.sharedPreferences.edit();
        editor.putInt(INPUT_MODE, id);
        editor.commit();
    }

    private void setCheckedOption(int id) {
        switch (id) {
            case 0:
                ((RadioButton) this.inputGroup.getChildAt(id)).setChecked(true);
                return;
            case 1:
                ((RadioButton) this.inputGroup.getChildAt(id)).setChecked(true);
                return;
            case 2:
                ((RadioButton) this.inputGroup.getChildAt(id)).setChecked(true);
                return;
            default:
                return;
        }
    }
}
