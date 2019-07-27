package com.fedco.mbc.felhr.encodings;

import android.support.v4.media.TransportMediator;
import android.support.v4.view.MotionEventCompat;

abstract class SingleByteCharset {
    private int[] charMap;

    public abstract int isCharPresent(int i);

    public abstract int lookup(byte b);

    public abstract byte[] toUTF8(byte[] bArr);

    public SingleByteCharset(int[] charMap) {
        this.charMap = charMap;
    }

    public byte[] convertToUTF8(int codepoint) {
        if (codepoint <= TransportMediator.KEYCODE_MEDIA_PAUSE) {
            return new byte[]{(byte) codepoint};
        }
        byte[] utf8Data;
        int utf8Value;
        int i;
        byte low;
        if (codepoint <= 2047) {
            utf8Data = new byte[2];
            utf8Value = (((((codepoint & 63) & -65) | 128) & -8193) | 16384) | 32768;
            for (i = 6; i <= 10; i++) {
                if (((codepoint >> i) & 1) == 1) {
                    utf8Value |= 1 << (i + 2);
                }
            }
            low = (byte) (utf8Value & 255);
            utf8Data[0] = (byte) ((MotionEventCompat.ACTION_POINTER_INDEX_MASK & utf8Value) >> 8);
            utf8Data[1] = low;
        } else {
            utf8Data = new byte[3];
            utf8Value = ((((((((codepoint & 63) & -65) | 128) & -16385) | 32768) & -1048577) | 2097152) | 4194304) | 8388608;
            i = 6;
            while (i <= 15) {
                if (((codepoint >> i) & 1) == 1 && i < 12) {
                    utf8Value |= 1 << (i + 2);
                } else if (((codepoint >> i) & 1) == 1 && i >= 12) {
                    utf8Value |= 1 << (i + 4);
                }
                i++;
            }
            low = (byte) (utf8Value & 255);
            byte mid = (byte) ((MotionEventCompat.ACTION_POINTER_INDEX_MASK & utf8Value) >> 8);
            utf8Data[0] = (byte) ((16711680 & utf8Value) >> 16);
            utf8Data[1] = mid;
            utf8Data[2] = low;
        }
        return utf8Data;
    }
}
