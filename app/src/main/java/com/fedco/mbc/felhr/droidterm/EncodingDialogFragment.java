package com.fedco.mbc.felhr.droidterm;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class EncodingDialogFragment extends DialogFragment implements OnClickListener {
    public static final String CLASS_ID = EncodingDialogFragment.class.getSimpleName();

    public interface IEncodingFragmentCommunicator {
        void configEncoding(int i);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return null;
    }

    private void saveInputOption(int id) {
    }

    private void setCheckedOption(int id) {
    }

    public void onClick(View v) {
    }
}
