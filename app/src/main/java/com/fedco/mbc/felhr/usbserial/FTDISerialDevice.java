package com.fedco.mbc.felhr.usbserial;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbRequest;
import android.util.Log;
import java.util.Arrays;

public class FTDISerialDevice extends UsbSerialDevice {
    private static final String CLASS_ID = FTDISerialDevice.class.getSimpleName();
    public static final int FTDI_BAUDRATE_115200 = 26;
    public static final int FTDI_BAUDRATE_1200 = 2500;
    public static final int FTDI_BAUDRATE_19200 = 32924;
    public static final int FTDI_BAUDRATE_230400 = 13;
    public static final int FTDI_BAUDRATE_2400 = 1250;
    public static final int FTDI_BAUDRATE_300 = 10000;
    public static final int FTDI_BAUDRATE_38400 = 49230;
    public static final int FTDI_BAUDRATE_460800 = 16390;
    public static final int FTDI_BAUDRATE_4800 = 625;
    public static final int FTDI_BAUDRATE_57600 = 52;
    public static final int FTDI_BAUDRATE_600 = 5000;
    public static final int FTDI_BAUDRATE_921600 = 32771;
    public static final int FTDI_BAUDRATE_9600 = 16696;
    private static final int FTDI_REQTYPE_HOST2DEVICE = 64;
    private static final int FTDI_SET_DATA_DEFAULT = 8;
    private static final int FTDI_SET_FLOW_CTRL_DEFAULT = 0;
    private static final int FTDI_SET_MODEM_CTRL_DEFAULT1 = 257;
    private static final int FTDI_SET_MODEM_CTRL_DEFAULT2 = 514;
    private static final int FTDI_SET_MODEM_CTRL_DEFAULT3 = 256;
    private static final int FTDI_SET_MODEM_CTRL_DEFAULT4 = 512;
    private static final int FTDI_SIO_MODEM_CTRL = 1;
    private static final int FTDI_SIO_RESET = 0;
    private static final int FTDI_SIO_SET_BAUD_RATE = 3;
    private static final int FTDI_SIO_SET_DATA = 4;
    private static final int FTDI_SIO_SET_FLOW_CTRL = 2;
    private int currentSioSetData;
    private UsbEndpoint inEndpoint;
    private UsbInterface mInterface;
    private UsbEndpoint outEndpoint;
    private UsbRequest requestIN;

    public static class FTDIUtilities {
        public static byte[] adaptArray(byte[] ftdiData) {
            int length = ftdiData.length;
            if (length <= 64) {
                return Arrays.copyOfRange(ftdiData, 2, length);
            }
            int n = 1;
            int p = 64;
            while (p < length) {
                n++;
                p = n * 64;
            }
            byte[] data = new byte[(length - (n * 2))];
            copyData(ftdiData, data);
            return data;
        }

        private static void copyData(byte[] src, byte[] dst) {
            int i = 0;
            int j = 0;
            while (i <= src.length - 1) {
                if (i == 0 || i == 1) {
                    i++;
                } else if (i % 64 != 0 || i < 64) {
                    dst[j] = src[i];
                    i++;
                    j++;
                } else {
                    i += 2;
                }
            }
        }
    }

    public FTDISerialDevice(UsbDevice device, UsbDeviceConnection connection) {
        this(device, connection, -1);
    }

    public FTDISerialDevice(UsbDevice device, UsbDeviceConnection connection, int iface) {
        super(device, connection);
        this.currentSioSetData = 0;
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
            if (setControlCommand(0, 0, 0, null) < 0 || setControlCommand(4, 8, 0, null) < 0) {
                return false;
            }
            this.currentSioSetData = 8;
            if (setControlCommand(1, 257, 0, null) < 0 || setControlCommand(1, FTDI_SET_MODEM_CTRL_DEFAULT2, 0, null) < 0 || setControlCommand(2, 0, 0, null) < 0 || setControlCommand(3, FTDI_BAUDRATE_9600, 0, null) < 0) {
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
        setControlCommand(1, 256, 0, null);
        setControlCommand(1, 512, 0, null);
        this.currentSioSetData = 0;
        killWorkingThread();
        killWriteThread();
        this.connection.releaseInterface(this.mInterface);
    }

    public void setBaudRate(int baudRate) {
        int value;
        if (baudRate >= 0 && baudRate <= 300) {
            value = 10000;
        } else if (baudRate > 300 && baudRate <= 600) {
            value = FTDI_BAUDRATE_600;
        } else if (baudRate > 600 && baudRate <= 1200) {
            value = FTDI_BAUDRATE_1200;
        } else if (baudRate > 1200 && baudRate <= 2400) {
            value = FTDI_BAUDRATE_2400;
        } else if (baudRate > 2400 && baudRate <= 4800) {
            value = FTDI_BAUDRATE_4800;
        } else if (baudRate > 4800 && baudRate <= 9600) {
            value = FTDI_BAUDRATE_9600;
        } else if (baudRate > 9600 && baudRate <= 19200) {
            value = FTDI_BAUDRATE_19200;
        } else if (baudRate > 19200 && baudRate <= 38400) {
            value = FTDI_BAUDRATE_38400;
        } else if (baudRate > 19200 && baudRate <= 57600) {
            value = 52;
        } else if (baudRate > 57600 && baudRate <= 115200) {
            value = 26;
        } else if (baudRate > 115200 && baudRate <= 230400) {
            value = 13;
        } else if (baudRate > 230400 && baudRate <= 460800) {
            value = FTDI_BAUDRATE_460800;
        } else if (baudRate > 460800 && baudRate <= 921600) {
            value = FTDI_BAUDRATE_921600;
        } else if (baudRate > 921600) {
            value = FTDI_BAUDRATE_921600;
        } else {
            value = FTDI_BAUDRATE_9600;
        }
        setControlCommand(3, value, 0, null);
    }

    public void setDataBits(int dataBits) {
        switch (dataBits) {
            case 5:
                this.currentSioSetData |= 1;
                this.currentSioSetData &= -3;
                this.currentSioSetData |= 4;
                this.currentSioSetData &= -9;
                setControlCommand(4, this.currentSioSetData, 0, null);
                return;
            case 6:
                this.currentSioSetData &= -2;
                this.currentSioSetData |= 2;
                this.currentSioSetData |= 4;
                this.currentSioSetData &= -9;
                setControlCommand(4, this.currentSioSetData, 0, null);
                return;
            case 7:
                this.currentSioSetData |= 1;
                this.currentSioSetData |= 2;
                this.currentSioSetData |= 4;
                this.currentSioSetData &= -9;
                setControlCommand(4, this.currentSioSetData, 0, null);
                return;
            case 8:
                this.currentSioSetData &= -2;
                this.currentSioSetData &= -3;
                this.currentSioSetData &= -5;
                this.currentSioSetData |= 8;
                setControlCommand(4, this.currentSioSetData, 0, null);
                return;
            default:
                this.currentSioSetData &= -2;
                this.currentSioSetData &= -3;
                this.currentSioSetData &= -5;
                this.currentSioSetData |= 8;
                setControlCommand(4, this.currentSioSetData, 0, null);
                return;
        }
    }

    public void setStopBits(int stopBits) {
        switch (stopBits) {
            case 1:
                this.currentSioSetData &= -2049;
                this.currentSioSetData &= -4097;
                this.currentSioSetData &= -8193;
                setControlCommand(4, this.currentSioSetData, 0, null);
                return;
            case 2:
                this.currentSioSetData &= -2049;
                this.currentSioSetData |= 4096;
                this.currentSioSetData &= -8193;
                setControlCommand(4, this.currentSioSetData, 0, null);
                return;
            case 3:
                this.currentSioSetData |= 2048;
                this.currentSioSetData &= -4097;
                this.currentSioSetData &= -8193;
                setControlCommand(4, this.currentSioSetData, 0, null);
                return;
            default:
                this.currentSioSetData &= -2049;
                this.currentSioSetData &= -4097;
                this.currentSioSetData &= -8193;
                setControlCommand(4, this.currentSioSetData, 0, null);
                return;
        }
    }

    public void setParity(int parity) {
        switch (parity) {
            case 0:
                this.currentSioSetData &= -257;
                this.currentSioSetData &= -513;
                this.currentSioSetData &= -1025;
                setControlCommand(4, this.currentSioSetData, 0, null);
                return;
            case 1:
                this.currentSioSetData |= 256;
                this.currentSioSetData &= -513;
                this.currentSioSetData &= -1025;
                setControlCommand(4, this.currentSioSetData, 0, null);
                return;
            case 2:
                this.currentSioSetData &= -257;
                this.currentSioSetData |= 512;
                this.currentSioSetData &= -1025;
                setControlCommand(4, this.currentSioSetData, 0, null);
                return;
            case 3:
                this.currentSioSetData |= 256;
                this.currentSioSetData |= 512;
                this.currentSioSetData &= -1025;
                setControlCommand(4, this.currentSioSetData, 0, null);
                return;
            case 4:
                this.currentSioSetData &= -257;
                this.currentSioSetData &= -513;
                this.currentSioSetData |= 1024;
                setControlCommand(4, this.currentSioSetData, 0, null);
                return;
            default:
                this.currentSioSetData &= -257;
                this.currentSioSetData &= -513;
                this.currentSioSetData &= -1025;
                setControlCommand(4, this.currentSioSetData, 0, null);
                return;
        }
    }

    public void setFlowControl(int flowControl) {
        switch (flowControl) {
            case 0:
                setControlCommand(2, 0, 0, null);
                return;
            case 1:
                setControlCommand(2, 0, 1, null);
                return;
            case 2:
                setControlCommand(2, 0, 2, null);
                return;
            case 3:
                setControlCommand(2, 4881, 4, null);
                return;
            default:
                setControlCommand(2, 0, 0, null);
                return;
        }
    }

    private int setControlCommand(int request, int value, int index, byte[] data) {
        int dataLength = 0;
        if (data != null) {
            dataLength = data.length;
        }
        int response = this.connection.controlTransfer(64, request, value, (this.mInterface.getId() + 1) + index, data, dataLength, FTDI_BAUDRATE_600);
        Log.i(CLASS_ID, "Control Transfer Response: " + String.valueOf(response));
        return response;
    }
}
