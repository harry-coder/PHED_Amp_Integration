package com.fedco.mbc.felhr.usbserial;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbRequest;
import android.util.Log;

public class XdcVcpSerialDevice extends UsbSerialDevice {
    private static final String CLASS_ID = XdcVcpSerialDevice.class.getSimpleName();
    private static final int DEFAULT_BAUDRATE = 115200;
    private static final int XDCVCP_GET_LINE_CTL = 4;
    private static final int XDCVCP_IFC_ENABLE = 0;
    private static final int XDCVCP_LINE_CTL_DEFAULT = 2048;
    private static final int XDCVCP_MHS_ALL = 17;
    private static final int XDCVCP_MHS_DEFAULT = 0;
    private static final int XDCVCP_MHS_DTR = 1;
    private static final int XDCVCP_MHS_RTS = 16;
    private static final int XDCVCP_REQTYPE_DEVICE2HOST = 193;
    private static final int XDCVCP_REQTYPE_HOST2DEVICE = 65;
    private static final int XDCVCP_SET_BAUDDIV = 1;
    private static final int XDCVCP_SET_BAUDRATE = 30;
    private static final int XDCVCP_SET_CHARS = 25;
    private static final int XDCVCP_SET_FLOW = 19;
    private static final int XDCVCP_SET_LINE_CTL = 3;
    private static final int XDCVCP_SET_MHS = 7;
    private static final int XDCVCP_SET_XOFF = 10;
    private static final int XDCVCP_SET_XON = 9;
    private static final int XDCVCP_UART_DISABLE = 0;
    private static final int XDCVCP_UART_ENABLE = 1;
    private static final int XDCVCP_XOFF = 0;
    private static final int XDCVCP_XON = 0;
    private UsbEndpoint inEndpoint;
    private UsbInterface mInterface;
    private UsbEndpoint outEndpoint;
    private UsbRequest requestIN;

    public XdcVcpSerialDevice(UsbDevice device, UsbDeviceConnection connection) {
        this(device, connection, -1);
    }

    public XdcVcpSerialDevice(UsbDevice device, UsbDeviceConnection connection, int iface) {
        super(device, connection);
        if (iface < 0) {
            iface = 0;
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
                } else {
                    this.outEndpoint = endpoint;
                }
            }
            if (setControlCommand(0, 1, null) < 0) {
                return false;
            }
            setBaudRate(DEFAULT_BAUDRATE);
            if (setControlCommand(3, 2048, null) < 0) {
                return false;
            }
            setFlowControl(0);
            if (setControlCommand(7, 0, null) < 0) {
                return false;
            }
            this.requestIN = new UsbRequest();
            this.requestIN.initialize(this.connection, this.inEndpoint);
            setThreadsParams(this.requestIN, this.outEndpoint);
            return true;
        }
        Log.i(CLASS_ID, "Interface could not be claimed");
        return false;
    }

    public void close() {
        setControlCommand(0, 0, null);
        killWorkingThread();
        killWriteThread();
        this.connection.releaseInterface(this.mInterface);
    }

    public void setBaudRate(int baudRate) {
        setControlCommand(XDCVCP_SET_BAUDRATE, 0, new byte[]{(byte) (baudRate & 255), (byte) ((baudRate >> 8) & 255), (byte) ((baudRate >> 16) & 255), (byte) ((baudRate >> 24) & 255)});
    }

    public void setDataBits(int dataBits) {
        byte[] data = getCTL();
        switch (dataBits) {
            case 5:
                data[1] = (byte) 5;
                break;
            case 6:
                data[1] = (byte) 6;
                break;
            case 7:
                data[1] = (byte) 7;
                break;
            case 8:
                data[1] = (byte) 8;
                break;
            default:
                return;
        }
        setControlCommand(3, (byte) ((data[1] << 8) | (data[0] & 255)), null);
    }

    public void setStopBits(int stopBits) {
        byte[] data = getCTL();
        switch (stopBits) {
            case 1:
                data[0] = (byte) (data[0] & -2);
                data[0] = (byte) (data[0] & -3);
                break;
            case 2:
                data[0] = (byte) (data[0] & -2);
                data[0] = (byte) (data[0] | 2);
                break;
            case 3:
                data[0] = (byte) (data[0] | 1);
                data[0] = (byte) (data[0] & -3);
                break;
            default:
                return;
        }
        setControlCommand(3, (byte) ((data[1] << 8) | (data[0] & 255)), null);
    }

    public void setParity(int parity) {
        byte[] data = getCTL();
        switch (parity) {
            case 0:
                data[0] = (byte) (data[0] & -17);
                data[0] = (byte) (data[0] & -33);
                data[0] = (byte) (data[0] & -65);
                data[0] = (byte) (data[0] & -129);
                break;
            case 1:
                data[0] = (byte) (data[0] | 16);
                data[0] = (byte) (data[0] & -33);
                data[0] = (byte) (data[0] & -65);
                data[0] = (byte) (data[0] & -129);
                break;
            case 2:
                data[0] = (byte) (data[0] & -17);
                data[0] = (byte) (data[0] | 32);
                data[0] = (byte) (data[0] & -65);
                data[0] = (byte) (data[0] & -129);
                break;
            case 3:
                data[0] = (byte) (data[0] | 16);
                data[0] = (byte) (data[0] | 32);
                data[0] = (byte) (data[0] & -65);
                data[0] = (byte) (data[0] & -129);
                break;
            case 4:
                data[0] = (byte) (data[0] & -17);
                data[0] = (byte) (data[0] & -33);
                data[0] = (byte) (data[0] | 64);
                data[0] = (byte) (data[0] & -129);
                break;
            default:
                return;
        }
        setControlCommand(3, (byte) ((data[1] << 8) | (data[0] & 255)), null);
    }

    public void setFlowControl(int flowControl) {
        switch (flowControl) {
            case 0:
                byte[] dataOff = new byte[16];
                dataOff[0] = (byte) 1;
                dataOff[4] = (byte) 64;
                dataOff[9] = Byte.MIN_VALUE;
                dataOff[13] = (byte) 32;
                setControlCommand(19, 0, dataOff);
                return;
            case 1:
                byte[] dataRTSCTS = new byte[16];
                dataRTSCTS[0] = (byte) 9;
                dataRTSCTS[4] = Byte.MIN_VALUE;
                dataRTSCTS[9] = Byte.MIN_VALUE;
                dataRTSCTS[13] = (byte) 32;
                setControlCommand(19, 0, dataRTSCTS);
                return;
            case 2:
                byte[] dataDSRDTR = new byte[16];
                dataDSRDTR[0] = (byte) 18;
                dataDSRDTR[4] = (byte) 64;
                dataDSRDTR[9] = Byte.MIN_VALUE;
                dataDSRDTR[13] = (byte) 32;
                setControlCommand(19, 0, dataDSRDTR);
                return;
            case 3:
                byte[] dataXONXOFF = new byte[16];
                dataXONXOFF[0] = (byte) 1;
                dataXONXOFF[4] = (byte) 67;
                dataXONXOFF[9] = Byte.MIN_VALUE;
                dataXONXOFF[13] = (byte) 32;
                byte[] dataChars = new byte[6];
                dataChars[4] = (byte) 17;
                dataChars[5] = (byte) 19;
                setControlCommand(25, 0, dataChars);
                setControlCommand(19, 0, dataXONXOFF);
                return;
            default:
                return;
        }
    }

    private int setControlCommand(int request, int value, byte[] data) {
        int dataLength = 0;
        if (data != null) {
            dataLength = data.length;
        }
        int response = this.connection.controlTransfer(XDCVCP_REQTYPE_HOST2DEVICE, request, value, this.mInterface.getId(), data, dataLength, FTDISerialDevice.FTDI_BAUDRATE_600);
        Log.i(CLASS_ID, "Control Transfer Response: " + String.valueOf(response));
        return response;
    }

    private byte[] getCTL() {
        byte[] data = new byte[2];
        Log.i(CLASS_ID, "Control Transfer Response: " + String.valueOf(this.connection.controlTransfer(XDCVCP_REQTYPE_DEVICE2HOST, 4, 0, this.mInterface.getId(), data, data.length, FTDISerialDevice.FTDI_BAUDRATE_600)));
        return data;
    }
}
