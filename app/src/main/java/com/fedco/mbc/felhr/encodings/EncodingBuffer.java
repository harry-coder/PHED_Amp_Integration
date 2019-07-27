package com.fedco.mbc.felhr.encodings;


import android.util.Log;
import com.fedco.mbc.felhr.droidterm.utilities.HexData;
import java.nio.ByteBuffer;

public class EncodingBuffer {
    private static final String CLASS_ID = EncodingBuffer.class.getSimpleName();
    private static final int MAX_LENGTH = 16384;
    private int counter = 0;
    private boolean isRemainingUTF16 = false;
    private int remainingBytes = 0;
    private ByteBuffer utf16Buffer = ByteBuffer.allocate(16384);
    private boolean utf8;
    private ByteBuffer utf8Buffer = ByteBuffer.allocate(16384);

    public EncodingBuffer(boolean utf8) {
        this.utf8 = utf8;
    }

    public void putByte(byte[] data) {
        int i = 0;
        int length;
        byte b;
        if (this.utf8) {
            Log.i("Encoding", "Encoding Buffer: " + HexData.hexToString(data));
            length = data.length;
            while (i < length) {
                b = data[i];
                if ( isUTF8Trail(b)) {
                    this.utf8Buffer.put(b);
                    this.counter++;
                } else {
                    this.utf8Buffer.put(new byte[]{(byte) -17, (byte) -65, (byte) -67});
                    this.counter += 3;
                }
                i++;
            }
            return;
        }
        for (byte b2 : data) {
            this.utf16Buffer.put(b2);
            this.counter++;
        }
        if (data.length % 2 == 0) {
            this.isRemainingUTF16 = false;
        } else {
            this.isRemainingUTF16 = true;
        }
    }

    public boolean isRemainingBytes() {
        return this.remainingBytes != 0;
    }

    public boolean isRemainingUTF16Bytes() {
        return this.isRemainingUTF16;
    }

    public byte[] getUTF8Char() {
        byte[] bufferUTF8 = new byte[this.counter];
        while (true) {
            int i = this.counter - 1;
            this.counter = i;
            if (i >= 0) {
                bufferUTF8[this.counter] = this.utf8Buffer.get(this.counter);
            } else {
                this.counter = 0;
                this.utf8Buffer.clear();
                return bufferUTF8;
            }
        }
    }

    public byte[] getUTF16Char() {
        byte[] bufferUTF16 = new byte[this.counter];
        while (true) {
            int i = this.counter - 1;
            this.counter = i;
            if (i >= 0) {
                bufferUTF16[this.counter] = this.utf16Buffer.get(this.counter);
            } else {
                this.counter = 0;
                this.utf16Buffer.clear();
                return bufferUTF16;
            }
        }
    }

    public static boolean isNonAsciiHeader(byte b) {
        if ((b & -32) == -64 || (b & -16) == -32 || (b & -8) == -16) {
            return true;
        }
        return false;
    }

    public static int utfLenghtByte(byte b) {
//        if ((b | TransportMediator.KEYCODE_MEDIA_PAUSE) == TransportMediator.KEYCODE_MEDIA_PAUSE) {
//            return 1;
//        }
        if ((b & -32) == -64) {
            return 2;
        }
        if ((b & -16) == -32) {
            return 3;
        }
        if ((b & -8) == -16) {
            return 4;
        }
        return 0;
    }

//    private boolean isUTF8Header(byte b) {
//        if ((b | TransportMediator.KEYCODE_MEDIA_PAUSE) == TransportMediator.KEYCODE_MEDIA_PAUSE) {
//            this.remainingBytes = 0;
//            return true;
//        } else
//if ((b & -32) == -64) {
//            this.remainingBytes = 1;
//            return true;
//        } else if ((b & -16) == -32) {
//            this.remainingBytes = 2;
//            return true;
//        } else if ((b & -8) != -16) {
//            return false;
//        } else {
//            this.remainingBytes = 3;
//            return true;
//        }
//    }

    private boolean isUTF8Trail(byte b) {
        if ((b & -64) != -128) {
            return false;
        }
        this.remainingBytes--;
        return true;
    }
}
