package com.fedco.mbc.felhr.usbserial;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class SerialBuffer {
    public static final int DEFAULT_READ_BUFFER_SIZE = 16384;
    public static final int DEFAULT_WRITE_BUFFER_SIZE = 16384;
    private boolean debugging = false;
    private ByteBuffer readBuffer;
    private byte[] readBuffer_compatible;
    private SynchronizedBuffer writeBuffer = new SynchronizedBuffer();

    private class SynchronizedBuffer {
        private byte[] buffer = new byte[16384];
        private int position = -1;

        public synchronized void put(byte[] src) {
            if (this.position == -1) {
                this.position = 0;
            }
            if (SerialBuffer.this.debugging) {
                UsbSerialDebugger.printLogPut(src, true);
            }
            if (this.position + src.length > 16383) {
                if (this.position < 16384) {
                    System.arraycopy(src, 0, this.buffer, this.position, 16384 - this.position);
                }
                this.position = 16384;
                notify();
            } else {
                System.arraycopy(src, 0, this.buffer, this.position, src.length);
                this.position += src.length;
                notify();
            }
        }

        public synchronized byte[] get() {
            byte[] dst;
            if (this.position == -1) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            dst = Arrays.copyOfRange(this.buffer, 0, this.position);
            if (SerialBuffer.this.debugging) {
                UsbSerialDebugger.printLogGet(dst, true);
            }
            this.position = -1;
            return dst;
        }

        public synchronized void reset() {
            this.position = -1;
        }
    }

    public SerialBuffer(boolean version) {
        if (version) {
            this.readBuffer = ByteBuffer.allocate(16384);
        } else {
            this.readBuffer_compatible = new byte[16384];
        }
    }

    public void debug(boolean value) {
        this.debugging = value;
    }

    public void putReadBuffer(ByteBuffer data) {
        synchronized (this) {
            try {
                this.readBuffer.put(data);
            } catch (BufferOverflowException e) {
            }
        }
    }

    public ByteBuffer getReadBuffer() {
        ByteBuffer byteBuffer;
        synchronized (this) {
            byteBuffer = this.readBuffer;
        }
        return byteBuffer;
    }

    public byte[] getDataReceived() {
        byte[] dst;
        synchronized (this) {
            dst = new byte[this.readBuffer.position()];
            this.readBuffer.position(0);
            this.readBuffer.get(dst, 0, dst.length);
            if (this.debugging) {
                UsbSerialDebugger.printReadLogGet(dst, true);
            }
        }
        return dst;
    }

    public void clearReadBuffer() {
        synchronized (this) {
            this.readBuffer.clear();
        }
    }

    public byte[] getWriteBuffer() {
        return this.writeBuffer.get();
    }

    public void putWriteBuffer(byte[] data) {
        this.writeBuffer.put(data);
    }

    public void resetWriteBuffer() {
        this.writeBuffer.reset();
    }

    public byte[] getBufferCompatible() {
        return this.readBuffer_compatible;
    }

    public byte[] getDataReceivedCompatible(int numberBytes) {
        return Arrays.copyOfRange(this.readBuffer_compatible, 0, numberBytes);
    }
}
