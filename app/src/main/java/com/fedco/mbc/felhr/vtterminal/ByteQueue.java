package com.fedco.mbc.felhr.vtterminal;

import java.util.Arrays;

class ByteQueue {
    private byte[] mBuffer;
    private int mHead;
    private int mStoredBytes;

    public ByteQueue(int size) {
        this.mBuffer = new byte[size];
    }

    public int getBytesAvailable() {
        int i;
        synchronized (this) {
            i = this.mStoredBytes;
        }
        return i;
    }

    public int read(byte[] buffer, int offset, int length) throws InterruptedException {
        boolean wasFull = false;
        if (length + offset > buffer.length) {
            throw new IllegalArgumentException("length + offset > buffer.length");
        } else if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        } else if (length == 0) {
            return 0;
        } else {
            int totalRead;
            synchronized (this) {
                while (this.mStoredBytes == 0) {
                    wait();
                }
                totalRead = 0;
                int bufferLength = this.mBuffer.length;
                if (bufferLength == this.mStoredBytes) {
                    wasFull = true;
                }
                while (length > 0 && this.mStoredBytes > 0) {
                    int bytesToCopy = Math.min(length, Math.min(bufferLength - this.mHead, this.mStoredBytes));
                    System.arraycopy(this.mBuffer, this.mHead, buffer, offset, bytesToCopy);
                    this.mHead += bytesToCopy;
                    if (this.mHead >= bufferLength) {
                        this.mHead = 0;
                    }
                    this.mStoredBytes -= bytesToCopy;
                    length -= bytesToCopy;
                    offset += bytesToCopy;
                    totalRead += bytesToCopy;
                }
                if (wasFull) {
                    notify();
                }
            }
            return totalRead;
        }
    }

    public void write(byte[] buffer, int offset, int length) throws InterruptedException {
        if (length + offset > buffer.length) {
            throw new IllegalArgumentException("length + offset > buffer.length");
        } else if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        } else if (length != 0) {
            synchronized (this) {
                int bufferLength = this.mBuffer.length;
                boolean wasEmpty = this.mStoredBytes == 0;
                while (length > 0) {
                    int oneRun;
                    while (bufferLength == this.mStoredBytes) {
                        wait();
                    }
                    int tail = this.mHead + this.mStoredBytes;
                    if (tail >= bufferLength) {
                        tail -= bufferLength;
                        oneRun = this.mHead - tail;
                    } else {
                        oneRun = bufferLength - tail;
                    }
                    int bytesToCopy = Math.min(oneRun, length);
                    System.arraycopy(buffer, offset, this.mBuffer, tail, bytesToCopy);
                    offset += bytesToCopy;
                    this.mStoredBytes += bytesToCopy;
                    length -= bytesToCopy;
                }
                if (wasEmpty) {
                    notify();
                }
            }
        }
    }

    public void resetBuffer() {
        Arrays.fill(this.mBuffer, (byte) 0);
        this.mHead = 0;
        this.mStoredBytes = 0;
    }
}
