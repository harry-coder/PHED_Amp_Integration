package com.fedco.mbc.felhr.vtterminal;

public interface IEmulatorView {
    void toggleKeyboard();

    void writeToUsb(byte[] bArr);
}
