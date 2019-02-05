package com.fedco.mbc.felhr.droidterm;

import android.content.Context;
import android.os.Bundle;
import com.fedco.mbc.felhr.usbviewerdata.UsbViewerData;

public class ActivityInterfaces {

    public interface INoUsbDialogCommunicator {
        void sendMessage(String str);
    }

    public interface IOnDeviceDiscovered {
        void sendDevice(String str, String str2);

        void sendDiscoveredDevices(Context context, Bundle bundle);
    }

    public interface IOnDeviceViewer {
        void sendDeviceData(UsbViewerData usbViewerData);
    }

    public interface IOnToggleKeyboard {
        void requestFocus();
    }

    public interface ISendCurrentInput {
        void sendInput(int i);
    }

    public interface ISendLogData {
        void sendLogData(String str);
    }
}
