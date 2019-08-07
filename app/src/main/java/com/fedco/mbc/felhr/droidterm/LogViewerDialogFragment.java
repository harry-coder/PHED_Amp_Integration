package com.fedco.mbc.felhr.droidterm;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fedco.mbc.R;
import com.fedco.mbc.felhr.droidterm.ActivityInterfaces.ISendLogData;

public class LogViewerDialogFragment extends DialogFragment implements OnClickListener, ISendLogData {
    public static final String CLASS_ID = LogViewerDialogFragment.class.getSimpleName();
    private Button backButton;
    private ILogViewerCommunicator comm;
    private TextView logViewer;

    public interface ILogViewerCommunicator {
        void backToFileBrowser();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.logviewer_dialog_layout, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.comm = (ILogViewerCommunicator) getActivity();
        this.logViewer = (TextView) getView().findViewById(R.id.usbLogViewer);
        this.backButton = (Button) getView().findViewById(R.id.button_backFileBrowser_viewer);
        this.backButton.setOnClickListener(this);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(1);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        return dialog;
    }

    public void onClick(View v) {
        this.comm.backToFileBrowser();
    }

    public void sendLogData(String data) {
        this.logViewer.setText(data);
    }
}
