package com.fedco.mbc.felhr.vtterminal;

import android.graphics.Canvas;
import com.fedco.mbc.felhr.droidterm.utilities.UnsignedUtil;
import com.fedco.mbc.felhr.encodings.EncodingBuffer;
import com.fedco.mbc.felhr.usbserial.UsbSerialDebugger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class TranscriptScreen implements Screen {
    private int colorEncoded;
    private int iBackColor;
    private int iColumns;
    private int iForeColor;
    private int iScreenRows;
    private int iTotalRows;
    private int mActiveRows;
    private int mActiveTranscriptRows;
    private int mColumns;
    private int[] mData;
    private int mHead;
    private boolean[] mLineWrap;
    private char[] mRowBuffer;
    private int mScreenRows;
    private int mTotalRows;
    private EncodingBuffer utf8Buffer;
    private int xPointer;
    private int yPointer;

    public TranscriptScreen(int columns, int totalRows, int screenRows, int foreColor, int backColor) {
        init(columns, totalRows, screenRows, foreColor, backColor);
    }

    private void init(int columns, int totalRows, int screenRows, int foreColor, int backColor) {
        this.iColumns = columns;
        this.iTotalRows = totalRows;
        this.iScreenRows = screenRows;
        this.iForeColor = foreColor;
        this.iBackColor = backColor;
        this.mColumns = columns;
        this.mTotalRows = totalRows;
        this.mActiveTranscriptRows = 0;
        this.mHead = 0;
        this.mActiveRows = screenRows;
        this.mScreenRows = screenRows;
        this.mData = new int[(columns * totalRows)];
        this.utf8Buffer = new EncodingBuffer(true);
        this.xPointer = 0;
        this.yPointer = 0;
        blockSet(0, 0, this.mColumns, this.mScreenRows, 32, foreColor, backColor);
        this.mRowBuffer = new char[columns];
        this.mLineWrap = new boolean[totalRows];
        consistencyCheck();
    }

    public void resetTranscript() {
        int totalSize = this.mColumns * this.mTotalRows;
        this.mHead = 0;
        this.mActiveTranscriptRows = 0;
        this.mActiveRows = this.iScreenRows;
        this.mData = new int[totalSize];
        this.mRowBuffer = new char[this.mColumns];
        this.mLineWrap = new boolean[this.mTotalRows];
        this.xPointer = 0;
        this.yPointer = 0;
    }

    private int externalToInternalRow(int row) {
        if (row >= (-this.mActiveTranscriptRows) && row < this.mScreenRows) {
            return row >= 0 ? row : this.mScreenRows + (((this.mHead + this.mActiveTranscriptRows) + row) % this.mActiveTranscriptRows);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private int getOffset(int externalLine) {
        return externalToInternalRow(externalLine) * this.mColumns;
    }

    private int getOffset(int x, int y) {
        return getOffset(y) + x;
    }

    public void setLineWrap(int row) {
        this.mLineWrap[externalToInternalRow(row)] = true;
    }

    public void set(int x, int y, byte b, int foreColor, int backColor) {
        this.utf8Buffer.putByte(new byte[]{b});
        if (!this.utf8Buffer.isRemainingBytes()) {
            byte[] charUtf8 = this.utf8Buffer.getUTF8Char();
            int singleChar = 0;
            if (charUtf8.length == 1) {
                singleChar = charUtf8[0];
            } else if (charUtf8.length == 2) {
                singleChar = (UnsignedUtil.byteToUshort(charUtf8[0]) << 8) + UnsignedUtil.byteToUshort(charUtf8[1]);
            } else if (charUtf8.length == 3) {
                singleChar = ((UnsignedUtil.byteToUshort(charUtf8[0]) << 16) + (UnsignedUtil.byteToUshort(charUtf8[1]) << 8)) + UnsignedUtil.byteToUshort(charUtf8[2]);
            } else if (charUtf8.length == 4) {
                singleChar = (((UnsignedUtil.byteToUshort(charUtf8[0]) << 24) + (UnsignedUtil.byteToUshort(charUtf8[1]) << 16)) + (UnsignedUtil.byteToUshort(charUtf8[2]) << 8)) + UnsignedUtil.byteToUshort(charUtf8[3]);
            }
            this.mData[getOffset(this.xPointer, this.yPointer)] = encode(singleChar, foreColor, backColor);
            int i = this.xPointer + 1;
            this.xPointer = i;
            if (i > this.mColumns - 1) {
                this.xPointer = 0;
                if (this.yPointer < this.mScreenRows - 1) {
                    this.yPointer++;
                }
            }
        }
    }

    private int encode(int b, int foreColor, int backColor) {
        this.colorEncoded = (foreColor << 12) | (backColor << 8);
        return b;
    }

    public void scroll(int topMargin, int bottomMargin, int foreColor, int backColor) {
        if (topMargin > bottomMargin - 2 || topMargin > this.mScreenRows - 2 || bottomMargin > this.mScreenRows) {
            throw new IllegalArgumentException();
        }
        consistencyCheck();
        int expansionRows = Math.min(1, this.mTotalRows - this.mActiveRows);
        int rollRows = 1 - expansionRows;
        this.mActiveRows += expansionRows;
        this.mActiveTranscriptRows += expansionRows;
        if (this.mActiveTranscriptRows > 0) {
            this.mHead = (this.mHead + rollRows) % this.mActiveTranscriptRows;
        }
        consistencyCheck();
        int topOffset = getOffset(topMargin);
        int destOffset = getOffset(-1);
        System.arraycopy(this.mData, topOffset, this.mData, destOffset, this.mColumns);
        int topLine = externalToInternalRow(topMargin);
        System.arraycopy(this.mLineWrap, topLine, this.mLineWrap, externalToInternalRow(-1), 1);
        System.arraycopy(this.mData, this.mColumns + topOffset, this.mData, topOffset, ((bottomMargin - topMargin) - 1) * this.mColumns);
        System.arraycopy(this.mLineWrap, topLine + 1, this.mLineWrap, topLine, (bottomMargin - topMargin) - 1);
        blockSet(0, bottomMargin - 1, this.mColumns, 1, 32, foreColor, backColor);
        this.mLineWrap[externalToInternalRow(bottomMargin - 1)] = false;
    }

    private void consistencyCheck() {
        checkPositive(this.mColumns);
        checkPositive(this.mTotalRows);
        checkRange(0, this.mActiveTranscriptRows, this.mTotalRows);
        if (this.mActiveTranscriptRows == 0) {
            checkEqual(this.mHead, 0);
        } else {
            checkRange(0, this.mHead, this.mActiveTranscriptRows - 1);
        }
        checkEqual(this.mScreenRows + this.mActiveTranscriptRows, this.mActiveRows);
        checkRange(0, this.mScreenRows, this.mTotalRows);
        checkEqual(this.mTotalRows, this.mLineWrap.length);
        checkEqual(this.mTotalRows * this.mColumns, this.mData.length);
        checkEqual(this.mColumns, this.mRowBuffer.length);
    }

    private void checkPositive(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("checkPositive " + n);
        }
    }

    private void checkRange(int a, int b, int c) {
        if (a > b || b > c) {
            throw new IllegalArgumentException("checkRange " + a + " <= " + b + " <= " + c);
        }
    }

    private void checkEqual(int a, int b) {
        if (a != b) {
            throw new IllegalArgumentException("checkEqual " + a + " == " + b);
        }
    }

    public void blockCopy(int sx, int sy, int w, int h, int dx, int dy) {
        if (sx < 0 || sx + w > this.mColumns || sy < 0 || sy + h > this.mScreenRows || dx < 0 || dx + w > this.mColumns || dy < 0 || dy + h > this.mScreenRows) {
            throw new IllegalArgumentException();
        } else if (sy <= dy) {
            for (int y = 0; y < h; y++) {
                System.arraycopy(this.mData, getOffset(sx, sy + y), this.mData, getOffset(dx, dy + y), w);
            }
        } else {
            for (int y = 0; y < h; y++) {
                int y2 = h - (y + 1);
                System.arraycopy(this.mData, getOffset(sx, sy + y2), this.mData, getOffset(dx, dy + y2), w);
            }
        }
    }

    public void blockSet(int sx, int sy, int w, int h, int val, int foreColor, int backColor) {
        if (sx < 0 || sx + w > this.mColumns || sy < 0 || sy + h > this.mScreenRows) {
            throw new IllegalArgumentException();
        }
        int[] data = this.mData;
        int encodedVal = encode(val, foreColor, backColor);
        for (int y = 0; y < h; y++) {
            int offset = getOffset(sx, sy + y);
            for (int x = 0; x < w; x++) {
                data[offset + x] = encodedVal;
            }
        }
    }

    public final void drawText(int row, Canvas canvas, float x, float y, TextRenderer renderer, int cx) {
        if (row >= (-this.mActiveTranscriptRows) && row < this.mScreenRows) {
            int offset = getOffset(row);
            char[] rowBuffer = this.mRowBuffer;
            int[] data = this.mData;
            int columns = this.mColumns;
            int lastColors = 0;
            int lastRunStart = -1;
            for (int i = 0; i < columns; i++) {
                int c = data[offset + i];
                int colors = this.colorEncoded;
                if (cx == i) {
                    colors |= 65536;
                }
                rowBuffer[i] = getCharFromUTF8(c);
                if (colors != lastColors) {
                    if (lastRunStart >= 0) {
                        renderer.drawTextRun(canvas, x, y, lastRunStart, rowBuffer, lastRunStart, i - lastRunStart, (65536 & lastColors) != 0, (lastColors >> 12) & 15, (lastColors >> 8) & 15);
                    }
                    lastColors = colors;
                    lastRunStart = i;
                }
            }
            if (lastRunStart >= 0) {
                renderer.drawTextRun(canvas, x, y, lastRunStart, rowBuffer, lastRunStart, columns - lastRunStart, (65536 & lastColors) != 0, (lastColors >> 12) & 15, (lastColors >> 8) & 15);
            }
        }
    }

    private char getCharFromUTF8(int c) {
        ByteBuffer buffer;
        CharsetDecoder utf8Decoder = Charset.forName(UsbSerialDebugger.ENCODING).newDecoder();
        byte b1 = (byte) (c & 255);
        byte b2 = (byte) (c >> 8);
        byte b3 = (byte) (c >> 16);
        if (b3 == (byte) 0 && b2 == (byte) 0) {
            buffer = ByteBuffer.allocate(1);
            buffer.put(b1);
        } else if (b3 == (byte) 0 && b2 != (byte) 0) {
            buffer = ByteBuffer.allocate(2);
            buffer.put(b2);
            buffer.put(b1);
        } else if (b3 == (byte) 0 || b2 == (byte) 0) {
            buffer = ByteBuffer.allocate(1);
            buffer.put(b1);
        } else {
            buffer = ByteBuffer.allocate(3);
            buffer.put(b3);
            buffer.put(b2);
            buffer.put(b1);
        }
        buffer.rewind();
        CharBuffer charBuffer = CharBuffer.allocate(1);
        utf8Decoder.decode(buffer, charBuffer, true);
        return charBuffer.get(0);
    }

    public int getActiveRows() {
        return this.mActiveRows;
    }

    public int getActiveTranscriptRows() {
        return this.mActiveTranscriptRows;
    }

    public String getTranscriptText() {
        return internalGetTranscriptText(true);
    }

    private String internalGetTranscriptText(boolean stripColors) {
        StringBuilder builder = new StringBuilder();
        char[] rowBuffer = this.mRowBuffer;
        int[] data = this.mData;
        int columns = this.mColumns;
        for (int row = -this.mActiveTranscriptRows; row < this.mScreenRows; row++) {
            int offset = getOffset(row);
            int lastPrintingChar = -1;
            for (int column = 0; column < columns; column++) {
                char charDecoded = getCharFromUTF8(data[offset + column]);
                if (' ' != charDecoded) {
                    lastPrintingChar = column;
                }
                rowBuffer[column] = charDecoded;
            }
            if (this.mLineWrap[externalToInternalRow(row)]) {
                builder.append(rowBuffer, 0, columns);
            } else {
                builder.append(rowBuffer, 0, lastPrintingChar + 1);
            }
        }
        return builder.toString();
    }

    public void resize(int columns, int rows, int foreColor, int backColor) {
        init(columns, this.mTotalRows, rows, foreColor, backColor);
    }

    public void setXPointer(int x) {
        this.xPointer = x;
    }

    public void setYPointer(int y) {
        this.yPointer = y;
    }
}
