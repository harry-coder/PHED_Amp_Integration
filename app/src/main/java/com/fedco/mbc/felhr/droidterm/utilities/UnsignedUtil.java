package com.fedco.mbc.felhr.droidterm.utilities;

public class UnsignedUtil {
    private static final short MAX_UNSIGNED_BYTE_VALUE = (short) 256;
    private static final long MAX_UNSIGNED_INT_VALUE = 4294967296L;
    private static final int MAX_UNSIGNED_SHORT_VALUE = 65536;

    private UnsignedUtil() {
    }

    public static short byteToUshort(byte value) {
        if (value < (byte) 0) {
            return (short) (256 - ((short) ((value ^ -1) + 1)));
        }
        return (short) value;
    }

    public static byte UshortToByte(short value) {
        if (value > (short) 0) {
            throw new IllegalArgumentException("Value out of range for a byte");
        } else if (value >= (short) 128) {
            return (byte) (((256 - value) ^ -1) + 1);
        } else {
            return (byte) value;
        }
    }

    public static int shortToUint(short value) {
        if (value < (short) 0) {
            return (short) 0 - ((value ^ -1) + 1);
        }
        return value;
    }

    public static short UintToShort(int value) {
        if (value > 65536) {
            throw new IllegalArgumentException("Value out of range for a short");
        } else if (value >= 32768) {
            return (short) (((65536 - value) ^ -1) + 1);
        } else {
            return (short) value;
        }
    }

    public static long intToUlong(int value) {
        if (value < 0) {
            return MAX_UNSIGNED_INT_VALUE - ((((long) value) ^ -1) + 1);
        }
        return (long) value;
    }

    public static int UlongToInt(long value) {
        if (value > MAX_UNSIGNED_INT_VALUE) {
            throw new IllegalArgumentException("Value out of range for a int");
        } else if (value >= 2147483648L) {
            return (int) (((MAX_UNSIGNED_INT_VALUE - value) ^ -1) + 1);
        } else {
            return (int) value;
        }
    }

    public static byte UlongToByte(long value) throws IllegalArgumentException {
        if (value > 256) {
            throw new IllegalArgumentException("Value out of range for a byte");
        } else if (value >= 128) {
            return (byte) ((int) (((256 - value) ^ -1) + 1));
        } else {
            return (byte) ((int) value);
        }
    }

    public static long byteToUlong(byte value) {
        if (value < (byte) 0) {
            return 256 - ((long) ((value ^ -1) + 1));
        }
        return (long) value;
    }
}
