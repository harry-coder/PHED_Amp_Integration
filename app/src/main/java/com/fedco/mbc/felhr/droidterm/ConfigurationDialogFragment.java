package com.fedco.mbc.felhr.droidterm;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.fedco.mbc.R;
import com.fedco.mbc.felhr.connectivityservices.UsbService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigurationDialogFragment extends DialogFragment implements OnClickListener {
    private static final int BAUD_RATE_115200 = 115200;
    private static final int BAUD_RATE_1200 = 1200;
    private static final int BAUD_RATE_19200 = 19200;
    private static final int BAUD_RATE_230400 = 230400;
    private static final int BAUD_RATE_2400 = 2400;
    private static final int BAUD_RATE_300 = 300;
    private static final int BAUD_RATE_38400 = 38400;
    private static final int BAUD_RATE_460800 = 460800;
    private static final int BAUD_RATE_4800 = 4800;
    private static final int BAUD_RATE_57600 = 57600;
    private static final int BAUD_RATE_600 = 600;
    private static final int BAUD_RATE_921600 = 921600;
    private static final int BAUD_RATE_9600 = 9600;
    private static final String BAUD_RATE_HEADER = "Baud Rate";
    public static final String CLASS_ID = ConfigurationDialogFragment.class.getSimpleName();
    private static final int DATA_BITS_5 = 5;
    private static final int DATA_BITS_6 = 6;
    private static final int DATA_BITS_7 = 7;
    private static final int DATA_BITS_8 = 8;
    private static final String DATA_BITS_HEADER = "Data bits";
    private static final String FLOW_HEADER = "Flow";
    private static final String FLOW_NONE = "None";
    private static final String PARITY_EVEN = "Parity Even";
    private static final String PARITY_HEADER = "Parity";
    private static final String PARITY_MARK = "Parity Mark";
    private static final String PARITY_NONE = "Parity None";
    private static final String PARITY_ODD = "Parity Odd";
    private static final String PARITY_SPACE = "Parity Space";
    private static final String STOP_BITS_1 = "1";
    private static final String STOP_BITS_15 = "1.5";
    private static final String STOP_BITS_2 = "2";
    private static final String STOP_BITS_HEADER = "Stop bits";
    private ExpandableListAdapter adapter;
    private List<String> baudRateValues;
    private int[] checkedCheckBoxes = new int[]{5, 3, 0, 0, 0};
    private IConfigurationCommunicator comm;
    private Button connectButton;
    private List<String> dataBitsValues;
    private ExpandableListView expandableList;
    private List<String> flowValues;
    private List<String> headerValues;
    private HashMap<String, List<String>> listSections;
    private List<String> parityValues;
    private List<String> stopBitsValues;

    private class ExpandableListAdapter extends BaseExpandableListAdapter implements OnClickListener {
        private Context context;
        private HashMap<String, List<String>> dataChild;
        private List<String> dataHeader;

        public ExpandableListAdapter(Context context, List<String> dataHeader, HashMap<String, List<String>> dataChild) {
            this.context = context;
            this.dataHeader = dataHeader;
            this.dataChild = dataChild;
        }

        public Object getChild(int arg0, int arg1) {
            return ((List) this.dataChild.get(this.dataHeader.get(arg0))).get(arg1);
        }

        public long getChildId(int arg0, int arg1) {
            return (long) arg1;
        }

        public View getChildView(int arg0, int arg1, boolean arg2, View arg3, ViewGroup arg4) {
            String childText = (String) getChild(arg0, arg1);
            if (arg3 == null) {
                arg3 = ((LayoutInflater) this.context.getSystemService(context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item, null);
            }
            RadioButton buttonText = (RadioButton) arg3.findViewById(R.id.radioButton1);
            switch (arg0) {
                case 0:
                    buttonText.setChecked(arg1 == ConfigurationDialogFragment.this.checkedCheckBoxes[0]);
                    break;
                case 1:
                    buttonText.setChecked(arg1 == ConfigurationDialogFragment.this.checkedCheckBoxes[1]);
                    break;
                case 2:
                    buttonText.setChecked(arg1 == ConfigurationDialogFragment.this.checkedCheckBoxes[2]);
                    break;
                case 3:
                    buttonText.setChecked(arg1 == ConfigurationDialogFragment.this.checkedCheckBoxes[3]);
                    break;
                case 4:
                    buttonText.setChecked(arg1 == ConfigurationDialogFragment.this.checkedCheckBoxes[4]);
                    break;
            }
            buttonText.setTag(new int[]{arg0, arg1});
            buttonText.setText(childText);
            buttonText.setOnClickListener(this);
            return arg3;
        }

        public int getChildrenCount(int arg0) {
            return ((List) this.dataChild.get(this.dataHeader.get(arg0))).size();
        }

        public Object getGroup(int arg0) {
            return this.dataHeader.get(arg0);
        }

        public int getGroupCount() {
            return this.dataHeader.size();
        }

        public long getGroupId(int arg0) {
            return (long) arg0;
        }

        public View getGroupView(int arg0, boolean arg1, View arg2, ViewGroup arg3) {
            String headerTitle = (String) getGroup(arg0);
            if (arg2 == null) {
                arg2 = ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_header, null);
            }
            ((TextView) arg2.findViewById(R.id.textViewHeader)).setText(headerTitle);
            return arg2;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isChildSelectable(int arg0, int arg1) {
            return true;
        }

        public void onClick(View v) {
            int[] pos = (int[]) v.getTag();
            ConfigurationDialogFragment.this.checkedCheckBoxes[pos[0]] = pos[1];
            notifyDataSetInvalidated();
        }
    }

    public interface IConfigurationCommunicator {
        void sendConfiguration(Bundle bundle);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.configuration_dialog, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.comm = (MainActivity) getActivity();
        this.expandableList = (ExpandableListView) getView().findViewById(R.id.expandableListView1);
        this.connectButton = (Button) getView().findViewById(R.id.connectButton);
        populateLists();
        this.adapter = new ExpandableListAdapter(getActivity(), this.headerValues, this.listSections);
        this.expandableList.setAdapter(this.adapter);
        this.connectButton.setOnClickListener(this);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(1);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        return dialog;
    }

    private void populateLists() {
        this.listSections = new HashMap(5);
        this.baudRateValues = new ArrayList(13);
        this.baudRateValues.add(String.valueOf(BAUD_RATE_300));
        this.baudRateValues.add(String.valueOf(BAUD_RATE_600));
        this.baudRateValues.add(String.valueOf(BAUD_RATE_1200));
        this.baudRateValues.add(String.valueOf(BAUD_RATE_2400));
        this.baudRateValues.add(String.valueOf(BAUD_RATE_4800));
        this.baudRateValues.add(String.valueOf(BAUD_RATE_9600));
        this.baudRateValues.add(String.valueOf(BAUD_RATE_19200));
        this.baudRateValues.add(String.valueOf(BAUD_RATE_38400));
        this.baudRateValues.add(String.valueOf(BAUD_RATE_57600));
        this.baudRateValues.add(String.valueOf(BAUD_RATE_115200));
        this.baudRateValues.add(String.valueOf(BAUD_RATE_230400));
        this.baudRateValues.add(String.valueOf(BAUD_RATE_460800));
        this.baudRateValues.add(String.valueOf(BAUD_RATE_921600));
        this.dataBitsValues = new ArrayList(4);
        this.dataBitsValues.add(String.valueOf(5));
        this.dataBitsValues.add(String.valueOf(6));
        this.dataBitsValues.add(String.valueOf(7));
        this.dataBitsValues.add(String.valueOf(8));
        this.stopBitsValues = new ArrayList(3);
        this.stopBitsValues.add(STOP_BITS_1);
        this.stopBitsValues.add(STOP_BITS_15);
        this.stopBitsValues.add(STOP_BITS_2);
        this.parityValues = new ArrayList(5);
        this.parityValues.add(PARITY_NONE);
        this.parityValues.add(PARITY_ODD);
        this.parityValues.add(PARITY_EVEN);
        this.parityValues.add(PARITY_MARK);
        this.parityValues.add(PARITY_SPACE);
        this.flowValues = new ArrayList();
        this.flowValues.add(FLOW_NONE);
        this.listSections.put(BAUD_RATE_HEADER, this.baudRateValues);
        this.listSections.put(DATA_BITS_HEADER, this.dataBitsValues);
        this.listSections.put(STOP_BITS_HEADER, this.stopBitsValues);
        this.listSections.put(PARITY_HEADER, this.parityValues);
        this.listSections.put(FLOW_HEADER, this.flowValues);
        this.headerValues = new ArrayList(5);
        this.headerValues.add(BAUD_RATE_HEADER);
        this.headerValues.add(DATA_BITS_HEADER);
        this.headerValues.add(STOP_BITS_HEADER);
        this.headerValues.add(PARITY_HEADER);
        this.headerValues.add(FLOW_HEADER);
    }

    private void getUserConfig(Bundle bundle) {
        for (int i = 0; i <= this.checkedCheckBoxes.length - 1; i++) {
            int value = this.checkedCheckBoxes[i];
            switch (i) {
                case 0:
                    switch (value) {
                        case 0:
                            bundle.putInt(UsbService.CONFIG_BAUDRATE, BAUD_RATE_300);
                            break;
                        case 1:
                            bundle.putInt(UsbService.CONFIG_BAUDRATE, BAUD_RATE_600);
                            break;
                        case 2:
                            bundle.putInt(UsbService.CONFIG_BAUDRATE, BAUD_RATE_1200);
                            break;
                        case 3:
                            bundle.putInt(UsbService.CONFIG_BAUDRATE, BAUD_RATE_2400);
                            break;
                        case 4:
                            bundle.putInt(UsbService.CONFIG_BAUDRATE, BAUD_RATE_4800);
                            break;
                        case 5:
                            bundle.putInt(UsbService.CONFIG_BAUDRATE, BAUD_RATE_9600);
                            break;
                        case 6:
                            bundle.putInt(UsbService.CONFIG_BAUDRATE, BAUD_RATE_19200);
                            break;
                        case 7:
                            bundle.putInt(UsbService.CONFIG_BAUDRATE, BAUD_RATE_38400);
                            break;
                        case 8:
                            bundle.putInt(UsbService.CONFIG_BAUDRATE, BAUD_RATE_57600);
                            break;
                        case 9:
                            bundle.putInt(UsbService.CONFIG_BAUDRATE, BAUD_RATE_115200);
                            break;
                        case 10:
                            bundle.putInt(UsbService.CONFIG_BAUDRATE, BAUD_RATE_230400);
                            break;
                        case 11:
                            bundle.putInt(UsbService.CONFIG_BAUDRATE, BAUD_RATE_460800);
                            break;
                        case 12 /*12*/:
                            bundle.putInt(UsbService.CONFIG_BAUDRATE, BAUD_RATE_921600);
                            break;
                        default:
                            break;
                    }
                case 1:
                    switch (value) {
                        case 0:
                            bundle.putInt(UsbService.CONFIG_DATA_BITS, 5);
                            break;
                        case 1:
                            bundle.putInt(UsbService.CONFIG_DATA_BITS, 6);
                            break;
                        case 2:
                            bundle.putInt(UsbService.CONFIG_DATA_BITS, 7);
                            break;
                        case 3:
                            bundle.putInt(UsbService.CONFIG_DATA_BITS, 8);
                            break;
                        default:
                            break;
                    }
                case 2:
                    switch (value) {
                        case 0:
                            bundle.putInt(UsbService.CONFIG_STOP_BITS, 1);
                            break;
                        case 1:
                            bundle.putInt(UsbService.CONFIG_STOP_BITS, 3);
                            break;
                        case 2:
                            bundle.putInt(UsbService.CONFIG_STOP_BITS, 2);
                            break;
                        default:
                            break;
                    }
                case 3:
                    switch (value) {
                        case 0:
                            bundle.putInt(UsbService.CONFIG_PARITY, 0);
                            break;
                        case 1:
                            bundle.putInt(UsbService.CONFIG_PARITY, 1);
                            break;
                        case 2:
                            bundle.putInt(UsbService.CONFIG_PARITY, 2);
                            break;
                        case 3:
                            bundle.putInt(UsbService.CONFIG_PARITY, 3);
                            break;
                        case 4:
                            bundle.putInt(UsbService.CONFIG_PARITY, 4);
                            break;
                        default:
                            break;
                    }
                case 4:
                    switch (value) {
                        case 0:
                            bundle.putInt(UsbService.CONFIG_FLOW, 0);
                            break;
                        default:
                            break;
                    }
                default:
                    break;
            }
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.connectButton) {
            Bundle configBundle = new Bundle();
            getUserConfig(configBundle);
            this.comm.sendConfiguration(configBundle);
        }
    }
}
