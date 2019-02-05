package com.fedco.mbc.felhr.usbserial;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbRequest;
import android.util.Log;

public class CH34xSerialDevice extends UsbSerialDevice {
    private static final int CH341_NBREAK_BITS_REG1 = 1;
    private static final int CH341_NBREAK_BITS_REG2 = 64;
    private static final int CH341_REG_BREAK1 = 5;
    private static final int CH341_REG_BREAK2 = 24;
    private static final int CH341_REQ_READ_REG = 149;
    private static final int CH341_REQ_WRITE_REG = 154;
    private static final int CH34X_115200_1 = 52227;
    private static final int CH34X_115200_2 = 8;
    private static final int CH34X_19200_1 = 55554;
    private static final int CH34X_19200_2 = 13;
    private static final int CH34X_2400_1 = 55553;
    private static final int CH34X_2400_2 = 56;
    private static final int CH34X_38400_1 = 25603;
    private static final int CH34X_38400_2 = 10;
    private static final int CH34X_4800_1 = 25602;
    private static final int CH34X_4800_2 = 31;
    private static final int CH34X_9600_1 = 45570;
    private static final int CH34X_9600_2 = 19;
    private static final String CLASS_ID = CH34xSerialDevice.class.getSimpleName();
    private static final int DEFAULT_BAUDRATE = 9600;
    private static final int REQTYPE_HOST_FROM_DEVICE = 192;
    private static final int REQTYPE_HOST_TO_DEVICE = 65;
    private boolean dtr = false;
    private UsbEndpoint inEndpoint;
    private UsbInterface mInterface;
    private UsbEndpoint outEndpoint;
    private UsbRequest requestIN;
    private boolean rts = false;

    public CH34xSerialDevice(UsbDevice device, UsbDeviceConnection connection) {
        super(device, connection);
    }

    public CH34xSerialDevice(UsbDevice device, UsbDeviceConnection connection, int iface) {
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
                } else if (endpoint.getType() == 2 && endpoint.getDirection() == 0) {
                    this.outEndpoint = endpoint;
                }
            }
            if (init() != 0) {
                return false;
            }
            setBaudRate(DEFAULT_BAUDRATE);
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
        if (baudRate <= 2400) {
            if (setControlCommandOut(CH341_REQ_WRITE_REG, 4882, CH34X_2400_1, null) < 0) {
                Log.i(CLASS_ID, "Error setting baudRate");
            } else if (setControlCommandOut(CH341_REQ_WRITE_REG, 3884, CH34X_2400_2, null) < 0) {
                Log.i(CLASS_ID, "Error setting baudRate");
            } else {
                Log.i(CLASS_ID, "BaudRate set correctly");
            }
        } else if (baudRate <= 2400 || baudRate > 4800) {
            if (baudRate <= 4800 || baudRate > DEFAULT_BAUDRATE) {
                if (baudRate <= DEFAULT_BAUDRATE || baudRate > 19200) {
                    if (baudRate <= 19200 || baudRate > 38400) {
                        if (baudRate <= 38400) {
                            return;
                        }
                        if (setControlCommandOut(CH341_REQ_WRITE_REG, 4882, CH34X_115200_1, null) < 0) {
                            Log.i(CLASS_ID, "Error setting baudRate");
                        } else if (setControlCommandOut(CH341_REQ_WRITE_REG, 3884, 8, null) < 0) {
                            Log.i(CLASS_ID, "Error setting baudRate");
                        } else {
                            Log.i(CLASS_ID, "BaudRate set correctly");
                        }
                    } else if (setControlCommandOut(CH341_REQ_WRITE_REG, 4882, CH34X_38400_1, null) < 0) {
                        Log.i(CLASS_ID, "Error setting baudRate");
                    } else if (setControlCommandOut(CH341_REQ_WRITE_REG, 3884, 10, null) < 0) {
                        Log.i(CLASS_ID, "Error setting baudRate");
                    } else {
                        Log.i(CLASS_ID, "BaudRate set correctly");
                    }
                } else if (setControlCommandOut(CH341_REQ_WRITE_REG, 4882, CH34X_19200_1, null) < 0) {
                    Log.i(CLASS_ID, "Error setting baudRate");
                } else if (setControlCommandOut(CH341_REQ_WRITE_REG, 3884, 13, null) < 0) {
                    Log.i(CLASS_ID, "Error setting baudRate");
                } else {
                    Log.i(CLASS_ID, "BaudRate set correctly");
                }
            } else if (setControlCommandOut(CH341_REQ_WRITE_REG, 4882, CH34X_9600_1, null) < 0) {
                Log.i(CLASS_ID, "Error setting baudRate");
            } else if (setControlCommandOut(CH341_REQ_WRITE_REG, 3884, 19, null) < 0) {
                Log.i(CLASS_ID, "Error setting baudRate");
            } else {
                Log.i(CLASS_ID, "BaudRate set correctly");
            }
        } else if (setControlCommandOut(CH341_REQ_WRITE_REG, 4882, CH34X_4800_1, null) < 0) {
            Log.i(CLASS_ID, "Error setting baudRate");
        } else if (setControlCommandOut(CH341_REQ_WRITE_REG, 3884, CH34X_4800_2, null) < 0) {
            Log.i(CLASS_ID, "Error setting baudRate");
        } else {
            Log.i(CLASS_ID, "BaudRate set correctly");
        }
    }

    public void setDataBits(int dataBits) {
    }

    public void setStopBits(int stopBits) {
    }

    public void setParity(int parity) {
    }

    public void setFlowControl(int flowControl) {
    }

    private int init() {
        int[] iArr = new int[2];
        iArr[0] = -1;
        if (checkState("init #1", 95, 0, iArr) == -1) {
            return -1;
        }
        if (setControlCommandOut(161, 0, 0, null) < 0) {
            Log.i(CLASS_ID, "init failed! #2");
            return -1;
        }
        setBaudRate(DEFAULT_BAUDRATE);
        iArr = new int[2];
        iArr[0] = -1;
        if (checkState("init #4", CH341_REQ_READ_REG, 9496, iArr) == -1) {
            return -1;
        }
        if (setControlCommandOut(CH341_REQ_WRITE_REG, 9496, 80, null) < 0) {
            Log.i(CLASS_ID, "init failed! #5");
            return -1;
        } else if (checkState("init #6", CH341_REQ_READ_REG, 1798, new int[]{255, 238}) == -1) {
            return -1;
        } else {
            if (setControlCommandOut(161, 20511, 55562, null) < 0) {
                Log.i(CLASS_ID, "init failed! #7");
                return -1;
            }
            setBaudRate(DEFAULT_BAUDRATE);
            if (writeHandshakeByte() == -1 || checkState("init #10", CH341_REQ_READ_REG, 1798, new int[]{-1, 238}) == -1) {
                return -1;
            }
            return 0;
        }
    }

    private int checkState(String msg, int request, int value, int[] expected) {
        byte[] buffer = new byte[expected.length];
        int ret = setControlCommandIn(request, value, 0, buffer);
        if (ret != expected.length) {
            Log.i(CLASS_ID, "Expected " + expected.length + " bytes, but get " + ret + " [" + msg + "]");
            return -1;
        }
        for (int i = 0; i < expected.length; i++) {
            if (expected[i] != -1) {
                int current = buffer[i] & 255;
                if (expected[i] != current) {
                    Log.i(CLASS_ID, "Expected 0x" + Integer.toHexString(expected[i]) + " bytes, but get 0x" + Integer.toHexString(current) + " [" + msg + "]");
                    return -1;
                }
            }
        }
        return 0;
    }

    private int writeHandshakeByte() {
        int i = 64;
        int i2 = 32;
        if (setControlCommandOut(164, ((this.dtr ? 32 : 0) | (this.rts ? 64 : 0)) ^ -1, 0, null) < 0) {
            Log.i(CLASS_ID, "Faild to set handshake byte");
            return -1;
        }
        if (!this.dtr) {
            i2 = 0;
        }
        if (!this.rts) {
            i = 0;
        }
        return setControlCommandOut(164, (i2 | i) ^ -1, 0, null) > 0 ? 0 : 0;
    }

    private int setControlCommandOut(int request, int value, int index, byte[] data) {
        int dataLength = 0;
        if (data != null) {
            dataLength = data.length;
        }
        int response = this.connection.controlTransfer(REQTYPE_HOST_TO_DEVICE, request, value, index, data, dataLength, FTDISerialDevice.FTDI_BAUDRATE_600);
        Log.i(CLASS_ID, "Control Transfer Response: " + String.valueOf(response));
        return response;
    }

    private int setControlCommandIn(int request, int value, int index, byte[] data) {
        int dataLength = 0;
        if (data != null) {
            dataLength = data.length;
        }
        int response = this.connection.controlTransfer(REQTYPE_HOST_FROM_DEVICE, request, value, index, data, dataLength, FTDISerialDevice.FTDI_BAUDRATE_600);
        Log.i(CLASS_ID, "Control Transfer Response: " + String.valueOf(response));
        return response;
    }
}
