package com.fedco.mbc.felhr.usbserial;

public interface UsbSerialInterface {
    public static final int DATA_BITS_5 = 5;
    public static final int DATA_BITS_6 = 6;
    public static final int DATA_BITS_7 = 7;
    public static final int DATA_BITS_8 = 8;
    public static final int FLOW_CONTROL_DSR_DTR = 2;
    public static final int FLOW_CONTROL_OFF = 0;
    public static final int FLOW_CONTROL_RTS_CTS = 1;
    public static final int FLOW_CONTROL_XON_XOFF = 3;
    public static final int PARITY_EVEN = 2;
    public static final int PARITY_MARK = 3;
    public static final int PARITY_NONE = 0;
    public static final int PARITY_ODD = 1;
    public static final int PARITY_SPACE = 4;
    public static final int STOP_BITS_1 = 1;
    public static final int STOP_BITS_15 = 3;
    public static final int STOP_BITS_2 = 2;

    public interface UsbReadCallback {
        void onReceivedData(byte[] bArr);
    }

    void close();

    boolean open();

    int read(UsbReadCallback usbReadCallback);

    void setBaudRate(int i);

    void setDataBits(int i);

    void setFlowControl(int i);

    void setParity(int i);

    void setStopBits(int i);

    void write(byte[] bArr);
}
