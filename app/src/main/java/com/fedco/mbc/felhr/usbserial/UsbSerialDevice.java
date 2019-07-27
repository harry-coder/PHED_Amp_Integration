package com.fedco.mbc.felhr.usbserial;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbRequest;
import android.os.Build.VERSION;
import com.fedco.mbc.felhr.deviceids.CH34xIds;
import com.fedco.mbc.felhr.deviceids.CP210xIds;
import com.fedco.mbc.felhr.deviceids.FTDISioIds;
import com.fedco.mbc.felhr.deviceids.PL2303Ids;
import com.fedco.mbc.felhr.usbserial.FTDISerialDevice.FTDIUtilities;
import com.fedco.mbc.felhr.usbserial.UsbSerialInterface.UsbReadCallback;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class UsbSerialDevice implements UsbSerialInterface {
    private static final String CLASS_ID = UsbSerialDevice.class.getSimpleName();
    protected static final int USB_TIMEOUT = 5000;
    private static boolean mr1Version;
    protected final UsbDeviceConnection connection;
    protected final UsbDevice device;
    protected ReadThread readThread;
    protected SerialBuffer serialBuffer = new SerialBuffer(mr1Version);
    protected WorkerThread workerThread;
    protected WriteThread writeThread;

    protected class ReadThread extends Thread {
        private UsbReadCallback callback;
        private UsbEndpoint inEndpoint;
        private AtomicBoolean working = new AtomicBoolean(true);

        public void setCallback(UsbReadCallback callback) {
            this.callback = callback;
        }

        public void run() {
            while (this.working.get()) {
                int numberBytes = UsbSerialDevice.this.connection.bulkTransfer(this.inEndpoint, UsbSerialDevice.this.serialBuffer.getBufferCompatible(), 16384, 0);
                if (numberBytes > 0) {
                    byte[] dataReceived = UsbSerialDevice.this.serialBuffer.getDataReceivedCompatible(numberBytes);
                    if (!UsbSerialDevice.this.isFTDIDevice()) {
                        this.callback.onReceivedData(dataReceived);
                    } else if (dataReceived.length > 2) {
                        this.callback.onReceivedData(FTDIUtilities.adaptArray(dataReceived));
                    }
                }
            }
        }

        public void setUsbEndpoint(UsbEndpoint inEndpoint) {
            this.inEndpoint = inEndpoint;
        }

        public void stopReadThread() {
            this.working.set(false);
        }
    }

    protected class WorkerThread extends Thread {
        private UsbReadCallback callback;
        private UsbRequest requestIN;
        private AtomicBoolean working = new AtomicBoolean(true);

        public void run() {
            while (this.working.get()) {
                UsbRequest request = UsbSerialDevice.this.connection.requestWait();
                if (request != null && request.getEndpoint().getType() == 2 && request.getEndpoint().getDirection() == 128) {
                    byte[] data = UsbSerialDevice.this.serialBuffer.getDataReceived();
                    if (!UsbSerialDevice.this.isFTDIDevice()) {
                        UsbSerialDevice.this.serialBuffer.clearReadBuffer();
                        onReceivedData(data);
                    } else if (data.length > 2) {
                        data = FTDIUtilities.adaptArray(data);
                        UsbSerialDevice.this.serialBuffer.clearReadBuffer();
                        onReceivedData(data);
                    }
                    this.requestIN.queue(UsbSerialDevice.this.serialBuffer.getReadBuffer(), 16384);
                }
            }
        }

        public void setCallback(UsbReadCallback callback) {
            this.callback = callback;
        }

        public void setUsbRequest(UsbRequest request) {
            this.requestIN = request;
        }

        public UsbRequest getUsbRequest() {
            return this.requestIN;
        }

        private void onReceivedData(byte[] data) {
            this.callback.onReceivedData(data);
        }

        public void stopWorkingThread() {
            this.working.set(false);
        }
    }

    protected class WriteThread extends Thread {
        private UsbEndpoint outEndpoint;
        private AtomicBoolean working = new AtomicBoolean(true);

        public void run() {
            while (this.working.get()) {
                byte[] data = UsbSerialDevice.this.serialBuffer.getWriteBuffer();
                UsbSerialDevice.this.connection.bulkTransfer(this.outEndpoint, data, data.length, 5000);
            }
        }

        public void setUsbEndpoint(UsbEndpoint outEndpoint) {
            this.outEndpoint = outEndpoint;
        }

        public void stopWriteThread() {
            this.working.set(false);
        }
    }

    public abstract void close();

    public abstract boolean open();

    public abstract void setBaudRate(int i);

    public abstract void setDataBits(int i);

    public abstract void setFlowControl(int i);

    public abstract void setParity(int i);

    public abstract void setStopBits(int i);

    static {
        if (VERSION.SDK_INT >= 17) {
            mr1Version = true;
        } else {
            mr1Version = false;
        }
    }

    public UsbSerialDevice(UsbDevice device, UsbDeviceConnection connection) {
        this.device = device;
        this.connection = connection;
    }

    public static UsbSerialDevice createUsbSerialDevice(UsbDevice device, UsbDeviceConnection connection) {
        return createUsbSerialDevice(device, connection, -1);
    }

    public static UsbSerialDevice createUsbSerialDevice(UsbDevice device, UsbDeviceConnection connection, int iface) {
        int vid = device.getVendorId();
        int pid = device.getProductId();
        if (FTDISioIds.isDeviceSupported(vid, pid)) {
            return new FTDISerialDevice(device, connection, iface);
        }
        if (CP210xIds.isDeviceSupported(vid, pid)) {
            return new CP2102SerialDevice(device, connection, iface);
        }
        if (PL2303Ids.isDeviceSupported(vid, pid)) {
            return new PL2303SerialDevice(device, connection, iface);
        }
        if (CH34xIds.isDeviceSupported(vid, pid)) {
            return new CH34xSerialDevice(device, connection, iface);
        }
        if (isCdcDevice(device)) {
            return new CDCSerialDevice(device, connection, iface);
        }
        return null;
    }

    public void write(byte[] buffer) {
        this.serialBuffer.putWriteBuffer(buffer);
    }

    public int read(UsbReadCallback mCallback) {
        if (mr1Version) {
            this.workerThread.setCallback(mCallback);
            this.workerThread.getUsbRequest().queue(this.serialBuffer.getReadBuffer(), 16384);
        } else {
            this.readThread.setCallback(mCallback);
            this.readThread.start();
        }
        return 0;
    }

    public void debug(boolean value) {
        if (this.serialBuffer != null) {
            this.serialBuffer.debug(value);
        }
    }

    private boolean isFTDIDevice() {
        return this instanceof FTDISerialDevice;
    }

    public static boolean isCdcDevice(UsbDevice device) {
        int iIndex = device.getInterfaceCount();
        for (int i = 0; i <= iIndex - 1; i++) {
            if (device.getInterface(i).getInterfaceClass() == 10) {
                return true;
            }
        }
        return false;
    }

    protected void setThreadsParams(UsbRequest request, UsbEndpoint endpoint) {
        if (mr1Version) {
            this.workerThread.setUsbRequest(request);
            this.writeThread.setUsbEndpoint(endpoint);
            return;
        }
        this.readThread.setUsbEndpoint(request.getEndpoint());
        this.writeThread.setUsbEndpoint(endpoint);
    }

    protected void killWorkingThread() {
        if (mr1Version && this.workerThread != null) {
            this.workerThread.stopWorkingThread();
            this.workerThread = null;
        } else if (!mr1Version && this.readThread != null) {
            this.readThread.stopReadThread();
            this.readThread = null;
        }
    }

    protected void restartWorkingThread() {
        if (mr1Version && this.workerThread == null) {
            this.workerThread = new WorkerThread();
            this.workerThread.start();
            do {
            } while (!this.workerThread.isAlive());
        } else if (!mr1Version && this.readThread == null) {
            this.readThread = new ReadThread();
            this.readThread.start();
            do {
            } while (!this.readThread.isAlive());
        }
    }

    protected void killWriteThread() {
        if (this.writeThread != null) {
            this.writeThread.stopWriteThread();
            this.writeThread = null;
            this.serialBuffer.resetWriteBuffer();
        }
    }

    protected void restartWriteThread() {
        if (this.writeThread == null) {
            this.writeThread = new WriteThread();
            this.writeThread.start();
            do {
            } while (!this.writeThread.isAlive());
        }
    }
}
