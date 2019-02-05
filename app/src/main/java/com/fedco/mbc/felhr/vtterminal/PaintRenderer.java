package com.fedco.mbc.felhr.vtterminal;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

class PaintRenderer extends BaseTextRenderer {
    private static final char[] EXAMPLE_CHAR = new char[]{'X'};
    private int mCharAscent;
    private int mCharDescent;
    private int mCharHeight;
    private int mCharWidth;
    private Paint mTextPaint = new Paint();

    public PaintRenderer(int fontSize, int forePaintColor, int backPaintColor) {
        super(forePaintColor, backPaintColor);
        this.mTextPaint.setTypeface(Typeface.MONOSPACE);
        this.mTextPaint.setAntiAlias(true);
        this.mTextPaint.setTextSize((float) fontSize);
        this.mCharHeight = (int) Math.ceil((double) this.mTextPaint.getFontSpacing());
        this.mCharAscent = (int) Math.ceil((double) this.mTextPaint.ascent());
        this.mCharDescent = this.mCharHeight + this.mCharAscent;
        this.mCharWidth = (int) this.mTextPaint.measureText(EXAMPLE_CHAR, 0, 1);
    }

    public void drawTextRun(Canvas canvas, float x, float y, int lineOffset, char[] text, int index, int count, boolean cursor, int foreColor, int backColor) {
        if (cursor) {
            this.mTextPaint.setColor(-8355712);
        } else {
            this.mTextPaint.setColor(this.mBackPaint[backColor & 7]);
        }
        float left = x + ((float) (this.mCharWidth * lineOffset));
        canvas.drawRect(left, y + ((float) this.mCharAscent), left + ((float) (this.mCharWidth * count)), y + ((float) this.mCharDescent), this.mTextPaint);
        boolean bold = (foreColor & 8) != 0;
        boolean underline = (backColor & 8) != 0;
        if (bold) {
            this.mTextPaint.setFakeBoldText(true);
        }
        if (underline) {
            this.mTextPaint.setUnderlineText(true);
        }
        this.mTextPaint.setColor(this.mForePaint[foreColor & 7]);
        canvas.drawText(text, index, count, left, y, this.mTextPaint);
        if (bold) {
            this.mTextPaint.setFakeBoldText(false);
        }
        if (underline) {
            this.mTextPaint.setUnderlineText(false);
        }
    }

    public int getCharacterHeight() {
        return this.mCharHeight;
    }

    public int getCharacterWidth() {
        return this.mCharWidth;
    }
}
