package com.fedco.mbc.felhr.vtterminal;

import android.graphics.Canvas;

interface TextRenderer {
    void drawTextRun(Canvas canvas, float f, float f2, int i, char[] cArr, int i2, int i3, boolean z, int i4, int i5);

    int getCharacterHeight();

    int getCharacterWidth();
}
