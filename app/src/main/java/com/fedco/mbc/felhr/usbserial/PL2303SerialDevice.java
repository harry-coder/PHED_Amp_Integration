package com.fedco.mbc.felhr.usbserial;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbRequest;
import android.util.Log;

public class PL2303SerialDevice extends UsbSerialDevice {
    private static final String CLASS_ID = PL2303SerialDevice.class.getSimpleName();
    private static final int PL2303_REQTYPE_DEVICE2HOST_VENDOR = 192;
    private static final int PL2303_REQTYPE_HOST2DEVICE = 33;
    private static final int PL2303_REQTYPE_HOST2DEVICE_VENDOR = 64;
    private static final int PL2303_SET_CONTROL_REQUEST = 34;
    private static final int PL2303_SET_LINE_CODING = 32;
    private static final int PL2303_VENDOR_WRITE_REQUEST = 1;
    private byte[] defaultSetLine;
    private UsbEndpoint inEndpoint;
    private UsbInterface mInterface;
    private UsbEndpoint outEndpoint;
    private UsbRequest requestIN;

    public PL2303SerialDevice(UsbDevice device, UsbDeviceConnection connection) {
        this(device, connection, -1);
    }

    public PL2303SerialDevice(UsbDevice device, UsbDeviceConnection connection, int iface) {
        super(device, connection);
        byte[] bArr = new byte[7];
        bArr[0] = Byte.MIN_VALUE;
        bArr[1] = (byte) 37;
        bArr[6] = (byte) 8;
        this.defaultSetLine = bArr;
        if (iface > 1) {
            throw new IllegalArgumentException("Multi-interface PL2303 devices not supported!");
        }
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
                } else if (endpoint.getType() == 2 && endpoint.getDirection() == 0) {
                    this.outEndpoint = endpoint;
                }
            }
            byte[] buf = new byte[1];
            if (setControlCommand(PL2303_REQTYPE_DEVICE2HOST_VENDOR, 1, 33924, 0, buf) < 0) {
                return false;
            }
            if (setControlCommand(64, 1, 1028, 0, null) < 0) {
                return false;
            }
            if (setControlCommand(PL2303_REQTYPE_DEVICE2HOST_VENDOR, 1, 33924, 0, buf) < 0) {
                return false;
            }
            if (setControlCommand(PL2303_REQTYPE_DEVICE2HOST_VENDOR, 1, 33667, 0, buf) < 0) {
                return false;
            }
            if (setControlCommand(PL2303_REQTYPE_DEVICE2HOST_VENDOR, 1, 33924, 0, buf) < 0) {
                return false;
            }
            if (setControlCommand(64, 1, 1028, 1, null) < 0) {
                return false;
            }
            if (setControlCommand(PL2303_REQTYPE_DEVICE2HOST_VENDOR, 1, 33924, 0, buf) < 0) {
                return false;
            }
            if (setControlCommand(PL2303_REQTYPE_DEVICE2HOST_VENDOR, 1, 33667, 0, buf) < 0) {
                return false;
            }
            if (setControlCommand(64, 1, 0, 1, null) < 0) {
                return false;
            }
            if (setControlCommand(64, 1, 1, 0, null) < 0) {
                return false;
            }
            if (setControlCommand(64, 1, 2, 68, null) < 0) {
                return false;
            }
            if (setControlCommand(33, 34, 3, 0, null) < 0) {
                return false;
            }
            if (setControlCommand(33, 32, 0, 0, this.defaultSetLine) < 0) {
                return false;
            }
            if (setControlCommand(64, 1, 1285, 4881, null) < 0) {
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
        killWorkingThread();
        killWriteThread();
        this.connection.releaseInterface(this.mInterface);
    }

    public void setBaudRate(int baudRate) {
        byte[] tempBuffer = new byte[]{(byte) (baudRate & 255), (byte) ((baudRate >> 8) & 255), (byte) ((baudRate >> 16) & 255), (byte) ((baudRate >> 24) & 255)};
        if (tempBuffer[0] != this.defaultSetLine[0] || tempBuffer[1] != this.defaultSetLine[1] || tempBuffer[2] != this.defaultSetLine[2] || tempBuffer[3] != this.defaultSetLine[3]) {
            this.defaultSetLine[0] = tempBuffer[0];
            this.defaultSetLine[1] = tempBuffer[1];
            this.defaultSetLine[2] = tempBuffer[2];
            this.defaultSetLine[3] = tempBuffer[3];
            setControlCommand(33, 32, 0, 0, this.defaultSetLine);
        }
    }

    public void setDataBits(int dataBits) {
        switch (dataBits) {
            case 5:
                if (this.defaultSetLine[6] != (byte) 5) {
                    this.defaultSetLine[6] = (byte) 5;
                    setControlCommand(33, 32, 0, 0, this.defaultSetLine);
                    return;
                }
                return;
            case 6:
                if (this.defaultSetLine[6] != (byte) 6) {
                    this.defaultSetLine[6] = (byte) 6;
                    setControlCommand(33, 32, 0, 0, this.defaultSetLine);
                    return;
                }
                return;
            case 7:
                if (this.defaultSetLine[6] != (byte) 7) {
                    this.defaultSetLine[6] = (byte) 7;
                    setControlCommand(33, 32, 0, 0, this.defaultSetLine);
                    return;
                }
                return;
            case 8:
                if (this.defaultSetLine[6] != (byte) 8) {
                    this.defaultSetLine[6] = (byte) 8;
                    setControlCommand(33, 32, 0, 0, this.defaultSetLine);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void setStopBits(int stopBits) {
        switch (stopBits) {
            case 1:
                if (this.defaultSetLine[4] != (byte) 0) {
                    this.defaultSetLine[4] = (byte) 0;
                    setControlCommand(33, 32, 0, 0, this.defaultSetLine);
                    return;
                }
                return;
            case 2:
                if (this.defaultSetLine[4] != (byte) 2) {
                    this.defaultSetLine[4] = (byte) 2;
                    setControlCommand(33, 32, 0, 0, this.defaultSetLine);
                    return;
                }
                return;
            case 3:
                if (this.defaultSetLine[4] != (byte) 1) {
                    this.defaultSetLine[4] = (byte) 1;
                    setControlCommand(33, 32, 0, 0, this.defaultSetLine);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void setParity(int parity) {
        switch (parity) {
            case 0:
                if (this.defaultSetLine[5] != (byte) 0) {
                    this.defaultSetLine[5] = (byte) 0;
                    setControlCommand(33, 32, 0, 0, this.defaultSetLine);
                    return;
                }
                return;
            case 1:
                if (this.defaultSetLine[5] != (byte) 1) {
                    this.defaultSetLine[5] = (byte) 1;
                    setControlCommand(33, 32, 0, 0, this.defaultSetLine);
                    return;
                }
                return;
            case 2:
                if (this.defaultSetLine[5] != (byte) 2) {
                    this.defaultSetLine[5] = (byte) 2;
                    setControlCommand(33, 32, 0, 0, this.defaultSetLine);
                    return;
                }
                return;
            case 3:
                if (this.defaultSetLine[5] != (byte) 3) {
                    this.defaultSetLine[5] = (byte) 3;
                    setControlCommand(33, 32, 0, 0, this.defaultSetLine);
                    return;
                }
                return;
            case 4:
                if (this.defaultSetLine[5] != (byte) 4) {
                    this.defaultSetLine[5] = (byte) 4;
                    setControlCommand(33, 32, 0, 0, this.defaultSetLine);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void setFlowControl(int flowControl) {
    }

    private int setControlCommand(int reqType, int request, int value, int index, byte[] data) {
        int dataLength = 0;
        if (data != null) {
            dataLength = data.length;
        }
        int response = this.connection.controlTransfer(reqType, request, value, index, data, dataLength, FTDISerialDevice.FTDI_BAUDRATE_600);
        Log.i(CLASS_ID, "Control Transfer Response: " + String.valueOf(response));
        return response;
    }
}
