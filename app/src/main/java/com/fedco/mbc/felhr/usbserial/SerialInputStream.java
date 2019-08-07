package com.fedco.mbc.felhr.usbserial;

import com.fedco.mbc.felhr.usbserial.UsbSerialInterface.UsbReadCallback;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;

public class SerialInputStream extends InputStream implements UsbReadCallback {
    protected ArrayBlockingQueue data = new ArrayBlockingQueue(256);
    protected final UsbSerialInterface device;
    protected volatile boolean is_open;

    public SerialInputStream(UsbSerialInterface device) {
        this.device = device;
        this.is_open = true;
        device.read(this);
    }

    public int read() {
        while (this.is_open) {
            try {
                return ((Integer) this.data.take()).intValue();
            } catch (InterruptedException e) {
            }
        }
        return -1;
    }

    public void close() {
        this.is_open = false;
        try {
            this.data.put(Integer.valueOf(-1));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onReceivedData(byte[] new_data) {
        for (byte b : new_data) {
            try {
                this.data.put(Integer.valueOf(b & 255));
            } catch (InterruptedException e) {
            }
        }
    }
}
