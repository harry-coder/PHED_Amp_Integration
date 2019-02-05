package com.fedco.mbc.felhr.droidterm;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.fedco.mbc.R;
import com.fedco.mbc.felhr.log.LogFile;
import java.util.ArrayList;
import java.util.List;

public class FileBrowserDialogFragment extends DialogFragment implements OnClickListener, OnItemClickListener {
    public static final String CLASS_ID = FileBrowserDialogFragment.class.getSimpleName();
    private FileAdapter adapter;
    private Button button;
    private IFileBrowserCommunicator comm;
    private Context context;
    private GridView gridView;
    private List<LogFile> logFiles;
    private Object obj;

    class C01041 extends Thread {
        C01041() {
        }

        public void run() {
            FileBrowserDialogFragment.this.logFiles = LogFile.getAllFiles();
            synchronized (FileBrowserDialogFragment.this.obj) {
                FileBrowserDialogFragment.this.obj.notify();
            }
        }
    }

    private class FileAdapter extends ArrayAdapter<LogFile> {
        public FileAdapter(Context context, List<LogFile> files) {
            super(context, R.layout.filebrowser_item, files);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View square = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.filebrowser_item, null, false);
            TextView textView = (TextView) square.findViewById(R.id.textViewFile);
            LogFile log = (LogFile) getItem(position);
            if (log != null) {
                textView.setText(log.getName());
            }
            return square;
        }
    }

    public interface IFileBrowserCommunicator {
        void backToMainMenuFromBrowser();

        void goToLogViewer(String str);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.filebrowser_layout, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.comm = (IFileBrowserCommunicator) getActivity();
        this.obj = new Object();
        this.context = getActivity();
        this.gridView = (GridView) getView().findViewById(R.id.gridView1);
        this.button = (Button) getView().findViewById(R.id.button_back_filebrowser);
        this.button.setOnClickListener(this);
        this.gridView.setOnItemClickListener(this);
        new C01041().start();
        populateGrid();
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(1);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        return dialog;
    }

    public void onClick(View v) {
        this.comm.backToMainMenuFromBrowser();
    }

    public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
        this.comm.goToLogViewer(LogFile.getFileContent(((TextView) arg1.findViewById(R.id.textViewFile)).getText().toString()));
    }

    private void populateGrid() {
        synchronized (this.obj) {
            try {
                this.obj.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (this.logFiles != null) {
            this.adapter = new FileAdapter(this.context, this.logFiles);
        } else {
            this.logFiles = new ArrayList();
            this.adapter = new FileAdapter(this.context, this.logFiles);
        }
        this.gridView.setAdapter(this.adapter);
    }
}
