package com.fedco.mbc.felhr.vtterminal;

interface Screen {
    void blockCopy(int i, int i2, int i3, int i4, int i5, int i6);

    void blockSet(int i, int i2, int i3, int i4, int i5, int i6, int i7);

    String getTranscriptText();

    void resize(int i, int i2, int i3, int i4);

    void scroll(int i, int i2, int i3, int i4);

    void set(int i, int i2, byte b, int i3, int i4);

    void setLineWrap(int i);

    void setXPointer(int i);

    void setYPointer(int i);
}
