package com.fedco.mbc.felhr.usbserial;

import java.io.OutputStream;

public class SerialOutputStream extends OutputStream {
    protected final UsbSerialInterface device;

    public SerialOutputStream(UsbSerialInterface device) {
        this.device = device;
    }

    public void write(int b) {
        this.device.write(new byte[]{(byte) b});
    }

    public void write(byte[] b) {
        this.device.write(b);
    }
}
