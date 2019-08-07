package com.fedco.mbc.felhr.vtterminal;

import androidx.core.internal.view.SupportMenu;
import androidx.core.view.InputDeviceCompat;


abstract class BaseTextRenderer implements TextRenderer {
    protected static final int mCursorPaint = -8355712;
    protected int[] mBackPaint = new int[]{-16777216, -3407872, -16724992, -3355648, -16777012, -65332, -16724788, -1};
    protected int[] mForePaint = new int[]{-16777216, SupportMenu.CATEGORY_MASK, -16711936, InputDeviceCompat.SOURCE_ANY, -16776961, -65281, -16711681, -1};

    public BaseTextRenderer(int forePaintColor, int backPaintColor) {
        this.mForePaint[7] = forePaintColor;
        this.mBackPaint[0] = backPaintColor;
    }
}
