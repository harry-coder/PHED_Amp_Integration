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
import com.fedco.mbc.felhr.droidterm.ActivityInterfaces.IOnDeviceViewer;
import com.fedco.mbc.felhr.droidterm.DroidTermBuild.BUILD_TYPE;
import com.fedco.mbc.felhr.usbviewerdata.UsbViewerData;
import com.fedco.mbc.felhr.usbviewerdata.UsbViewerData.Endpoint;
import com.fedco.mbc.felhr.usbviewerdata.UsbViewerData.Interface;
import java.util.List;

public class UsbViewerDialogFragment extends DialogFragment implements IOnDeviceViewer, OnClickListener {
    public static final String CLASS_ID = UsbViewerDialogFragment.class.getSimpleName();
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private Button backButton;
    private UsbViewerCommunicator comm;
    private TextView usbViewer;

    public interface UsbViewerCommunicator {
        void backToMainMenuFromViewer();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.usbviewer_dialog_layout, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.comm = (MainActivity) getActivity();
        this.usbViewer = (TextView) getView().findViewById(R.id.usbViewer);
        this.backButton = (Button) getView().findViewById(R.id.button_backMenu_viewer);
        this.backButton.setOnClickListener(this);
        if (DroidTermBuild.TYPE == BUILD_TYPE.LITE) {
            //((AdView) getView().findViewById(C0107R.id.adView_usb_viewer)).loadAd(new Builder().build());
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
        this.comm.backToMainMenuFromViewer();
    }

    public void sendDeviceData(UsbViewerData usbData) {
        if (usbData.vendorName.equals("None")) {
            this.usbViewer.setText("Vendor: Unknown vendor" + LINE_SEPARATOR);
        } else {
            this.usbViewer.setText("Vendor: " + usbData.vendorName + LINE_SEPARATOR);
        }
        this.usbViewer.append("Vendor Id: " + usbData.vendorId + LINE_SEPARATOR);
        if (usbData.productName.equals("None")) {
            this.usbViewer.append("Product: Unknown product" + LINE_SEPARATOR);
        } else {
            this.usbViewer.append("Product: " + usbData.productName + LINE_SEPARATOR);
        }
        this.usbViewer.append("Product Id: " + usbData.productId + LINE_SEPARATOR);
        this.usbViewer.append(LINE_SEPARATOR);
        List<Interface> interfaces = usbData.interfaces;
        int countInterfaces = interfaces.size();
        int i = 1;
        for (Interface iface : interfaces) {
            this.usbViewer.append("Interface (" + String.valueOf(i) + "/" + String.valueOf(countInterfaces) + ")" + LINE_SEPARATOR);
            this.usbViewer.append("Class: " + iface.getClassInterface() + LINE_SEPARATOR);
            this.usbViewer.append(LINE_SEPARATOR);
            List<Endpoint> endpoints = iface.endpoints;
            int countEndpoints = endpoints.size();
            int j = 1;
            for (Endpoint endpoint : endpoints) {
                this.usbViewer.append("    Endpoint: (" + String.valueOf(j) + "/" + String.valueOf(countEndpoints) + ")" + LINE_SEPARATOR);
                this.usbViewer.append(LINE_SEPARATOR);
                this.usbViewer.append("    Attributes: " + endpoint.attribute + LINE_SEPARATOR);
                this.usbViewer.append("    Type: " + endpoint.type + LINE_SEPARATOR);
                this.usbViewer.append("    Interval: " + endpoint.interval + LINE_SEPARATOR);
                this.usbViewer.append("    Packet Size: " + endpoint.packSize + LINE_SEPARATOR);
                this.usbViewer.append(LINE_SEPARATOR);
                j++;
            }
            i++;
        }
    }
}
