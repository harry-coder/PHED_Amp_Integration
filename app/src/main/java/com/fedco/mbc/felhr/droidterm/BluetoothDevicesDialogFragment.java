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
import android.widget.ListView;
import android.widget.TextView;

import com.fedco.mbc.R;
import com.fedco.mbc.felhr.droidterm.ActivityInterfaces.IOnDeviceDiscovered;
import java.util.ArrayList;
import java.util.List;

public class BluetoothDevicesDialogFragment extends DialogFragment implements OnItemClickListener, IOnDeviceDiscovered, OnClickListener {
    public static final String CLASS_ID = BluetoothDevicesDialogFragment.class.getSimpleName();
    private Button backButton;
    private BtAdapter btAdapter;
    private IBluetoothDevicesCommunicator comm;
    private ListView devicesList;

    private class BluetoothInfoDevice {
        public String deviceAddress;
        public String deviceName;

        public BluetoothInfoDevice(String name, String address) {
            this.deviceName = name;
            this.deviceAddress = address;
        }
    }

    private class BtAdapter extends ArrayAdapter<BluetoothInfoDevice> {
        public BtAdapter(Context context) {
            super(context, R.layout.bluetooth_list_item);
        }

        public BtAdapter(Context context, List<BluetoothInfoDevice> devices) {
            super(context, R.layout.bluetooth_list_item, devices);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.bluetooth_list_item, parent, false);
            TextView name = (TextView) rowView.findViewById(R.id.textViewName);
            TextView address = (TextView) rowView.findViewById(R.id.textViewAddress);
            BluetoothInfoDevice device = (BluetoothInfoDevice) getItem(position);
            if (device != null) {
                name.append(device.deviceName);
                address.append(device.deviceAddress);
            }
            return rowView;
        }
    }

    public interface IBluetoothDevicesCommunicator {
        void backToMainMenu();

        void connect(String str);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bluetooth_list_dialog_layout, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.comm = (IBluetoothDevicesCommunicator) getActivity();
        this.devicesList = (ListView) getView().findViewById(R.id.listView1);
        this.backButton = (Button) getView().findViewById(R.id.button_backMenu);
        if (this.btAdapter == null) {
            this.btAdapter = new BtAdapter(getActivity());
        }
        this.devicesList.setAdapter(this.btAdapter);
        this.devicesList.setOnItemClickListener(this);
        this.backButton.setOnClickListener(this);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(1);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        return dialog;
    }

    public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
        this.comm.connect(((BluetoothInfoDevice) this.btAdapter.getItem(arg2)).deviceAddress);
    }

    public void sendDevice(String deviceName, String addressDevice) {
        BluetoothInfoDevice device = new BluetoothInfoDevice(deviceName, addressDevice);
        if (device.deviceName == null) {
            device.deviceName = "";
        }
        this.btAdapter.add(device);
        this.btAdapter.notifyDataSetChanged();
    }

    public void sendDiscoveredDevices(Context context, Bundle data) {
        if (this.btAdapter == null) {
            List<BluetoothInfoDevice> devices = new ArrayList();
            for (String key : data.keySet()) {
                BluetoothInfoDevice device = new BluetoothInfoDevice(data.getString(key), key);
                if (device.deviceName == null) {
                    device.deviceName = "";
                }
                devices.add(device);
            }
            this.btAdapter = new BtAdapter(context, devices);
        }
    }

    public void onClick(View arg0) {
        this.comm.backToMainMenu();
    }
}
