package com.fedco.mbc.felhr.usbserial;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbRequest;
import android.util.Log;

public class CDCSerialDevice extends UsbSerialDevice {
    private static final int CDC_DEFAULT_CONTROL_LINE = 3;
    private static final byte[] CDC_DEFAULT_LINE_CODING;
    private static final int CDC_DISCONNECT_CONTROL_LINE = 2;
    private static final int CDC_GET_LINE_CODING = 33;
    private static final int CDC_REQTYPE_DEVICE2HOST = 161;
    private static final int CDC_REQTYPE_HOST2DEVICE = 33;
    private static final int CDC_SET_CONTROL_LINE_STATE = 34;
    private static final int CDC_SET_LINE_CODING = 32;
    private static final String CLASS_ID = CDCSerialDevice.class.getSimpleName();
    private UsbEndpoint inEndpoint;
    private UsbInterface mInterface;
    private UsbEndpoint outEndpoint;
    private UsbRequest requestIN;

    static {
        byte[] bArr = new byte[7];
        bArr[1] = (byte) -62;
        bArr[2] = (byte) 1;
        bArr[6] = (byte) 8;
        CDC_DEFAULT_LINE_CODING = bArr;
    }

    public CDCSerialDevice(UsbDevice device, UsbDeviceConnection connection) {
        this(device, connection, -1);
    }

    public CDCSerialDevice(UsbDevice device, UsbDeviceConnection connection, int iface) {
        super(device, connection);
        if (iface < 0) {
            iface = findFirstCDC(device);
        }
        this.mInterface = device.getInterface(iface);
    }

    public boolean open() {
        restartWorkingThread();
        restartWriteThread();
        if (this.connection.claimInterface(this.mInterface, true)) {
            Log.i(CLASS_ID, "Interface succesfully claimed");
            int numberEndpoints = this.mInterface.getEndpointCount();
            for (int i = 0; i <= numberEndpoints - 1; i++) {
                UsbEndpoint endpoint = this.mInterface.getEndpoint(i);
                if (endpoint.getType() == 2 && endpoint.getDirection() == 128) {
                    this.inEndpoint = endpoint;
                } else if (endpoint.getType() == 2 && endpoint.getDirection() == 0) {
                    this.outEndpoint = endpoint;
                }
            }
            if (this.outEndpoint == null || this.inEndpoint == null) {
                Log.i(CLASS_ID, "Interface does not have an IN or OUT interface");
                return false;
            }
            setControlCommand(32, 0, CDC_DEFAULT_LINE_CODING);
            setControlCommand(34, 3, null);
            this.requestIN = new UsbRequest();
            this.requestIN.initialize(this.connection, this.inEndpoint);
            setThreadsParams(this.requestIN, this.outEndpoint);
            return true;
        }
        Log.i(CLASS_ID, "Interface could not be claimed");
        return false;
    }

    public void close() {
        killWorkingThread();
        killWriteThread();
        this.connection.releaseInterface(this.mInterface);
    }

    public void setBaudRate(int baudRate) {
        byte[] data = getLineCoding();
        data[0] = (byte) (baudRate & 255);
        data[1] = (byte) ((baudRate >> 8) & 255);
        data[2] = (byte) ((baudRate >> 16) & 255);
        data[3] = (byte) ((baudRate >> 24) & 255);
        setControlCommand(32, 0, data);
    }

    public void setDataBits(int dataBits) {
        byte[] data = getLineCoding();
        switch (dataBits) {
            case 5:
                data[6] = (byte) 5;
                break;
            case 6:
                data[6] = (byte) 6;
                break;
            case 7:
                data[6] = (byte) 7;
                break;
            case 8:
                data[6] = (byte) 8;
                break;
            default:
                return;
        }
        setControlCommand(32, 0, data);
    }

    public void setStopBits(int stopBits) {
        byte[] data = getLineCoding();
        switch (stopBits) {
            case 1:
                data[4] = (byte) 0;
                break;
            case 2:
                data[4] = (byte) 2;
                break;
            case 3:
                data[4] = (byte) 1;
                break;
            default:
                return;
        }
        setControlCommand(32, 0, data);
    }

    public void setParity(int parity) {
        byte[] data = getLineCoding();
        switch (parity) {
            case 0:
                data[5] = (byte) 0;
                break;
            case 1:
                data[5] = (byte) 1;
                break;
            case 2:
                data[5] = (byte) 2;
                break;
            case 3:
                data[5] = (byte) 3;
                break;
            case 4:
                data[5] = (byte) 4;
                break;
            default:
                return;
        }
        setControlCommand(32, 0, data);
    }

    public void setFlowControl(int flowControl) {
    }

    private int setControlCommand(int request, int value, byte[] data) {
        int dataLength = 0;
        if (data != null) {
            dataLength = data.length;
        }
        int response = this.connection.controlTransfer(33, request, value, 0, data, dataLength, FTDISerialDevice.FTDI_BAUDRATE_600);
        Log.i(CLASS_ID, "Control Transfer Response: " + String.valueOf(response));
        return response;
    }

    private byte[] getLineCoding() {
        byte[] data = new byte[7];
        Log.i(CLASS_ID, "Control Transfer Response: " + String.valueOf(this.connection.controlTransfer(CDC_REQTYPE_DEVICE2HOST, 33, 0, 0, data, data.length, FTDISerialDevice.FTDI_BAUDRATE_600)));
        return data;
    }

    private static int findFirstCDC(UsbDevice device) {
        int interfaceCount = device.getInterfaceCount();
        for (int iIndex = 0; iIndex < interfaceCount; iIndex++) {
            if (device.getInterface(iIndex).getInterfaceClass() == 10) {
                return iIndex;
            }
        }
        Log.i(CLASS_ID, "There is no CDC class interface");
        return -1;
    }
}
