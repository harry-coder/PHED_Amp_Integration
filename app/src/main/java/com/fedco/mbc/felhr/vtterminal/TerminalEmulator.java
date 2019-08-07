package com.fedco.mbc.felhr.vtterminal;

import android.annotation.SuppressLint;
import android.util.Log;
import com.fedco.mbc.felhr.encodings.EncodingBuffer;
import com.fedco.mbc.felhr.usbserial.FTDISerialDevice;
//import com.google.ads.AdSize;

public class TerminalEmulator {
    public static final boolean DEBUG = true;
    private static final int ESC = 1;
    private static final int ESC_LEFT_SQUARE_BRACKET = 5;
    private static final int ESC_LEFT_SQUARE_BRACKET_QUESTION_MARK = 6;
    private static final int ESC_NONE = 0;
    private static final int ESC_POUND = 2;
    private static final int ESC_SELECT_LEFT_PAREN = 3;
    private static final int ESC_SELECT_RIGHT_PAREN = 4;
    private static final int K_132_COLUMN_MODE_MASK = 8;
    private static final int K_ORIGIN_MODE_MASK = 64;
    private static final int K_WRAPAROUND_MODE_MASK = 128;
    public static final boolean LOG_CHARACTERS_FLAG = false;
    private static final String LOG_TAG = "DroidTerm Terminal Emulator";
    public static final boolean LOG_UNKNOWN_ESCAPE_SEQUENCES = false;
    private static final int MAX_ESCAPE_PARAMETERS = 16;
    private boolean mAboutToAutoWrap;
    private boolean mAlternateCharSet;
    private int mArgIndex;
    private int[] mArgs = new int[16];
    private boolean mAutomaticNewlineMode;
    private int mBackColor;
    private int mBottomMargin;
    private int mColumns;
    private boolean mContinueSequence;
    private int mCursorCol;
    private int mCursorRow;
    private int mDecFlags;
    private int mEscapeState;
    private int mForeColor;
    private int mIncomingEoL_0A = 10;
    private int mIncomingEoL_0A0D = 2573;
    private int mIncomingEoL_0D = 13;
    private boolean mInsertMode;
    private boolean mInverseColors;
    private int mProcessedCharCount;
    private int mRows;
    private int mSavedCursorCol;
    private int mSavedCursorRow;
    private int mSavedDecFlags;
    private Screen mScreen;
    private boolean[] mTabStop;
    private int mTopMargin;
    private boolean mbKeypadApplicationMode;
    private boolean variableChar;

    public TerminalEmulator(Screen screen, int columns, int rows) {
        this.mScreen = screen;
        this.mRows = rows;
        this.mColumns = columns;
        this.mTabStop = new boolean[this.mColumns];
        this.variableChar = false;
        reset();
    }

    public void updateSize(int columns, int rows) {
        if (this.mRows != rows || this.mColumns != columns) {
            String transcriptText = this.mScreen.getTranscriptText();
            this.mScreen.resize(columns, rows, this.mForeColor, this.mBackColor);
            if (this.mRows != rows) {
                this.mRows = rows;
                this.mTopMargin = 0;
                this.mBottomMargin = this.mRows;
            }
            if (this.mColumns != columns) {
                int oldColumns = this.mColumns;
                this.mColumns = columns;
                boolean[] oldTabStop = this.mTabStop;
                this.mTabStop = new boolean[this.mColumns];
                System.arraycopy(oldTabStop, 0, this.mTabStop, 0, Math.min(oldColumns, columns));
                while (this.mCursorCol >= columns) {
                    this.mCursorCol -= columns;
                    this.mCursorRow = Math.min(this.mBottomMargin - 1, this.mCursorRow + 1);
                }
            }
            this.mCursorRow = 0;
            this.mCursorCol = 0;
            this.mAboutToAutoWrap = false;
            int end = transcriptText.length() - 1;
            while (end >= 0 && transcriptText.charAt(end) == '\n') {
                end--;
            }
            for (int i = 0; i <= end; i++) {
                byte c = (byte) transcriptText.charAt(i);
                if (c == (byte) 10) {
                    setCursorCol(0);
                    doLinefeed();
                } else {
                    emit(c);
                }
            }
        }
    }

    public final int getCursorRow() {
        return this.mCursorRow;
    }

    public final int getCursorCol() {
        return this.mCursorCol;
    }

    public final boolean getKeypadApplicationMode() {
        return this.mbKeypadApplicationMode;
    }

    private void setDefaultTabStops() {
        int i = 0;
        while (i < this.mColumns) {
            boolean[] zArr = this.mTabStop;
            boolean z = (i & 7) == 0 && i != 0;
            zArr[i] = z;
            i++;
        }
    }

    private void handleEndOfLineChars(int incomingEoL) {
        if (incomingEoL == 3338) {
            process((byte) 13);
            process((byte) 10);
        } else if (incomingEoL != 0) {
            process((byte) incomingEoL);
        }
    }

    @SuppressLint("LongLogTag")
    public void append(byte[] buffer, int base, int length) {
        for (int i = 0; i < length; i++) {
            byte b = buffer[base + i];
            if (b == (byte) 13) {
                try {
                    handleEndOfLineChars(this.mIncomingEoL_0D);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Exception while processing character " + Integer.toString(this.mProcessedCharCount) + " code " + Integer.toString(b), e);
                }
            } else if (b == (byte) 10) {
                handleEndOfLineChars(this.mIncomingEoL_0A);
            } else {
                process(b);
            }
            this.mProcessedCharCount++;
        }
    }

    private void process(byte b) {
        switch (b) {
            case (byte) -101:
                startEscapeSequence(5);
                return;
            case (byte) 0:
            case (byte) 7:
                return;
            case (byte) 8:
                setCursorCol(Math.max(0, this.mCursorCol - 1));
                return;
            case (byte) 9:
                setCursorCol(nextTabStop(this.mCursorCol));
                return;
            case (byte) 10:
            case (byte) 11:
            case 12 /*12*/:
                doLinefeed();
                return;
            case (byte) 13:
                setCursorCol(0);
                return;
            case (byte) 14:
                setAltCharSet(true);
                return;
            case (byte) 15:
                setAltCharSet(false);
                return;
            case 24 /*24*/:
            case FTDISerialDevice.FTDI_BAUDRATE_115200 /*26*/:
                if (this.mEscapeState != 0) {
                    this.mEscapeState = 0;
                    emit(Byte.MAX_VALUE);
                    return;
                }
                return;
            case (byte) 27:
                startEscapeSequence(1);
                return;
            default:
                this.mContinueSequence = false;
                switch (this.mEscapeState) {
                    case 0:
                        emit(b);
                        break;
                    case 1:
                        doEsc(b);
                        break;
                    case 2:
                        doEscPound(b);
                        break;
                    case 3:
                        doEscSelectLeftParen(b);
                        break;
                    case 4:
                        doEscSelectRightParen(b);
                        break;
                    case 5:
                        doEscLeftSquareBracket(b);
                        break;
                    case 6:
                        doEscLSBQuest(b);
                        break;
                    default:
                        unknownSequence(b);
                        break;
                }
                if (!this.mContinueSequence) {
                    this.mEscapeState = 0;
                    return;
                }
                return;
        }
    }

    private void setAltCharSet(boolean alternateCharSet) {
        this.mAlternateCharSet = alternateCharSet;
    }

    private int nextTabStop(int cursorCol) {
        for (int i = cursorCol; i < this.mColumns; i++) {
            if (this.mTabStop[i]) {
                return i;
            }
        }
        return this.mColumns - 1;
    }

    private void doEscLSBQuest(byte b) {
        int mask = getDecFlagsMask(getArg0(0));
        switch (b) {
            case (byte) 104:
                this.mDecFlags |= mask;
                break;
            case (byte) 108:
                this.mDecFlags &= mask ^ -1;
                break;
            case (byte) 114:
                this.mDecFlags = (this.mDecFlags & (mask ^ -1)) | (this.mSavedDecFlags & mask);
                break;
            case (byte) 115:
                this.mSavedDecFlags = (this.mSavedDecFlags & (mask ^ -1)) | (this.mDecFlags & mask);
                break;
            default:
                parseArg(b);
                break;
        }
        if ((mask & 8) != 0) {
            blockClear(0, 0, this.mColumns, this.mRows);
            setCursorRowCol(0, 0);
        }
        if ((mask & 64) != 0) {
            setCursorPosition(0, 0);
        }
    }

    private int getDecFlagsMask(int argument) {
        if (argument < 1 || argument > 9) {
            return 0;
        }
        return 1 << argument;
    }

    private void startEscapeSequence(int escapeState) {
        this.mEscapeState = escapeState;
        this.mArgIndex = 0;
        for (int j = 0; j < 16; j++) {
            this.mArgs[j] = -1;
        }
    }

    private void doLinefeed() {
        int newCursorRow = this.mCursorRow + 1;
        if (newCursorRow >= this.mBottomMargin) {
            scroll();
            newCursorRow = this.mBottomMargin - 1;
        }
        setCursorRow(newCursorRow);
    }

    private void continueSequence() {
        this.mContinueSequence = true;
    }

    private void continueSequence(int state) {
        this.mEscapeState = state;
        this.mContinueSequence = true;
    }

    private void doEscSelectLeftParen(byte b) {
        doSelectCharSet(true, b);
    }

    private void doEscSelectRightParen(byte b) {
        doSelectCharSet(false, b);
    }

    private void doSelectCharSet(boolean isG0CharSet, byte b) {
        switch (b) {
            case (byte) 48:
            case (byte) 49:
//            case AdSize.PORTRAIT_AD_HEIGHT /*50*/:
            case (byte) 65:
            case (byte) 66:
                return;
            default:
                unknownSequence(b);
                return;
        }
    }

    private void doEscPound(byte b) {
        switch (b) {
            case (byte) 56:
                this.mScreen.blockSet(0, 0, this.mColumns, this.mRows, 69, getForeColor(), getBackColor());
                return;
            default:
                unknownSequence(b);
                return;
        }
    }

    private void doEsc(byte b) {
        switch (b) {
            case 35 /*35*/:
                continueSequence(2);
                return;
            case 40 /*40*/:
                continueSequence(3);
                return;
            case 41 /*41*/:
                continueSequence(4);
                return;
            case (byte) 48:
                unimplementedSequence(b);
                return;
            case (byte) 55:
                this.mSavedCursorRow = this.mCursorRow;
                this.mSavedCursorCol = this.mCursorCol;
                return;
            case (byte) 56:
                setCursorRowCol(this.mSavedCursorRow, this.mSavedCursorCol);
                return;
            case (byte) 61:
                this.mbKeypadApplicationMode = true;
                return;
            case (byte) 62:
                this.mbKeypadApplicationMode = false;
                return;
            case (byte) 68:
                doLinefeed();
                return;
            case (byte) 69:
                setCursorCol(0);
                doLinefeed();
                return;
            case (byte) 70:
                setCursorRowCol(0, this.mBottomMargin - 1);
                return;
            case (byte) 72:
                this.mTabStop[this.mCursorCol] = true;
                return;
            case (byte) 77:
                if (this.mCursorRow == 0) {
                    this.mScreen.blockCopy(0, this.mTopMargin + 1, this.mColumns, this.mBottomMargin - (this.mTopMargin + 1), 0, this.mTopMargin);
                    blockClear(0, this.mBottomMargin - 1, this.mColumns);
                    return;
                }
                this.mCursorRow--;
                return;
            case (byte) 78:
                unimplementedSequence(b);
                return;
            case (byte) 80:
                unimplementedSequence(b);
                return;
//            case AdSize.LARGE_AD_HEIGHT /*90*/:
//                sendDeviceAttributes();
//                return;
            case (byte) 91:
                continueSequence(5);
                return;
            default:
                unknownSequence(b);
                return;
        }
    }

    private void doEscLeftSquareBracket(byte b) {
        int charsAfterCursor;
        int linesAfterCursor;
        switch (b) {
            case (byte) 63:
                continueSequence(6);
                return;
            case (byte) 64:
                charsAfterCursor = this.mColumns - this.mCursorCol;
                int charsToInsert = Math.min(getArg0(1), charsAfterCursor);
                this.mScreen.blockCopy(this.mCursorCol, this.mCursorRow, charsAfterCursor - charsToInsert, 1, this.mCursorCol + charsToInsert, this.mCursorRow);
                blockClear(this.mCursorCol, this.mCursorRow, charsToInsert);
                return;
            case (byte) 65:
                setCursorRow(Math.max(this.mTopMargin, this.mCursorRow - getArg0(1)));
                return;
            case (byte) 66:
                setCursorRow(Math.min(this.mBottomMargin - 1, this.mCursorRow + getArg0(1)));
                return;
            case (byte) 67:
                setCursorCol(Math.min(this.mColumns - 1, this.mCursorCol + getArg0(1)));
                return;
            case (byte) 68:
                setCursorCol(Math.max(0, this.mCursorCol - getArg0(1)));
                return;
            case (byte) 71:
                setCursorCol(Math.min(Math.max(1, getArg0(1)), this.mColumns) - 1);
                return;
            case (byte) 72:
                setHorizontalVerticalPosition();
                return;
            case (byte) 74:
                switch (getArg0(0)) {
                    case 0:
                        blockClear(this.mCursorCol, this.mCursorRow, this.mColumns - this.mCursorCol);
                        blockClear(0, this.mCursorRow + 1, this.mColumns, this.mBottomMargin - (this.mCursorRow + 1));
                        return;
                    case 1:
                        blockClear(0, this.mTopMargin, this.mColumns, this.mCursorRow - this.mTopMargin);
                        blockClear(0, this.mCursorRow, this.mCursorCol + 1);
                        return;
                    case 2:
                        blockClear(0, this.mTopMargin, this.mColumns, this.mBottomMargin - this.mTopMargin);
                        setCursorRowCol(0, 0);
                        return;
                    default:
                        unknownSequence(b);
                        return;
                }
            case (byte) 75:
                switch (getArg0(0)) {
                    case 0:
                        blockClear(this.mCursorCol, this.mCursorRow, this.mColumns - this.mCursorCol);
                        return;
                    case 1:
                        blockClear(0, this.mCursorRow, this.mCursorCol + 1);
                        return;
                    case 2:
                        blockClear(0, this.mCursorRow, this.mColumns);
                        return;
                    default:
                        unknownSequence(b);
                        return;
                }
            case (byte) 76:
                linesAfterCursor = this.mBottomMargin - this.mCursorRow;
                int linesToInsert = Math.min(getArg0(1), linesAfterCursor);
                this.mScreen.blockCopy(0, this.mCursorRow, this.mColumns, linesAfterCursor - linesToInsert, 0, this.mCursorRow + linesToInsert);
                blockClear(0, this.mCursorRow, this.mColumns, linesToInsert);
                return;
            case (byte) 77:
                linesAfterCursor = this.mBottomMargin - this.mCursorRow;
                int linesToDelete = Math.min(getArg0(1), linesAfterCursor);
                int linesToMove = linesAfterCursor - linesToDelete;
                this.mScreen.blockCopy(0, this.mCursorRow + linesToDelete, this.mColumns, linesToMove, 0, this.mCursorRow);
                blockClear(0, this.mCursorRow + linesToMove, this.mColumns, linesToDelete);
                return;
            case (byte) 80:
                charsAfterCursor = this.mColumns - this.mCursorCol;
                int charsToDelete = Math.min(getArg0(1), charsAfterCursor);
                int charsToMove = charsAfterCursor - charsToDelete;
                this.mScreen.blockCopy(this.mCursorCol + charsToDelete, this.mCursorRow, charsToMove, 1, this.mCursorCol, this.mCursorRow);
                blockClear(this.mCursorCol + charsToMove, this.mCursorRow, charsToDelete);
                return;
            case (byte) 84:
                unimplementedSequence(b);
                return;
            case (byte) 99:
                sendDeviceAttributes();
                return;
            case (byte) 100:
                setCursorRow(Math.min(Math.max(1, getArg0(1)), this.mRows) - 1);
                return;
            case (byte) 102:
                setHorizontalVerticalPosition();
                return;
            case (byte) 103:
                switch (getArg0(0)) {
                    case 0:
                        this.mTabStop[this.mCursorCol] = false;
                        return;
                    case 3:
                        for (int i = 0; i < this.mColumns; i++) {
                            this.mTabStop[i] = false;
                        }
                        return;
                    default:
                        return;
                }
            case (byte) 104:
                doSetMode(true);
                return;
            case (byte) 108:
                doSetMode(false);
                return;
            case (byte) 109:
                selectGraphicRendition();
                return;
            case (byte) 114:
                int top = Math.max(0, Math.min(getArg0(1) - 1, this.mRows - 2));
                int bottom = Math.max(top + 2, Math.min(getArg1(this.mRows), this.mRows));
                this.mTopMargin = top;
                this.mBottomMargin = bottom;
                setCursorRowCol(this.mTopMargin, 0);
                return;
            default:
                parseArg(b);
                return;
        }
    }

    private void selectGraphicRendition() {
        for (int i = 0; i <= this.mArgIndex; i++) {
            int code = this.mArgs[i];
            if (code < 0) {
                if (this.mArgIndex > 0) {
                } else {
                    code = 0;
                }
            }
            if (code == 0) {
                this.mInverseColors = false;
                this.mForeColor = 7;
                this.mBackColor = 0;
            } else if (code == 1) {
                this.mForeColor |= 8;
            } else if (code == 4) {
                this.mBackColor |= 8;
            } else if (code == 7) {
                this.mInverseColors = true;
            } else if (code >= 30 && code <= 37) {
                this.mForeColor = (this.mForeColor & 8) | (code - 30);
            } else if (code >= 40 && code <= 47) {
                this.mBackColor = (this.mBackColor & 8) | (code - 40);
            }
        }
    }

    private void blockClear(int sx, int sy, int w) {
        blockClear(sx, sy, w, 1);
    }

    private void blockClear(int sx, int sy, int w, int h) {
        this.mScreen.blockSet(sx, sy, w, h, 32, getForeColor(), getBackColor());
    }

    private int getForeColor() {
        return this.mInverseColors ? (this.mBackColor & 7) | (this.mForeColor & 8) : this.mForeColor;
    }

    private int getBackColor() {
        return this.mInverseColors ? (this.mForeColor & 7) | (this.mBackColor & 8) : this.mBackColor;
    }

    private void doSetMode(boolean newValue) {
        int modeBit = getArg0(0);
        switch (modeBit) {
            case 4:
                this.mInsertMode = newValue;
                return;
            case 20 /*20*/:
                this.mAutomaticNewlineMode = newValue;
                return;
            default:
                unknownParameter(modeBit);
                return;
        }
    }

    private void setHorizontalVerticalPosition() {
        setCursorPosition(getArg1(1) - 1, getArg0(1) - 1);
    }

    private void setCursorPosition(int x, int y) {
        int effectiveTopMargin = 0;
        int effectiveBottomMargin = this.mRows;
        if ((this.mDecFlags & 64) != 0) {
            effectiveTopMargin = this.mTopMargin;
            effectiveBottomMargin = this.mBottomMargin;
        }
        setCursorRowCol(Math.max(effectiveTopMargin, Math.min(effectiveTopMargin + y, effectiveBottomMargin - 1)), Math.max(0, Math.min(x, this.mColumns - 1)));
    }

    private void sendDeviceAttributes() {
        write(new byte[]{(byte) 27, (byte) 91, (byte) 63, (byte) 49, (byte) 59, (byte) 50, (byte) 99});
    }

    private void write(byte[] data) {
    }

    private void scroll() {
        this.mScreen.scroll(this.mTopMargin, this.mBottomMargin, getForeColor(), getBackColor());
    }

    private void parseArg(byte b) {
        if (b >= (byte) 48 && b <= (byte) 57) {
            if (this.mArgIndex < this.mArgs.length) {
                int value;
                int oldValue = this.mArgs[this.mArgIndex];
                int thisDigit = b - 48;
                if (oldValue >= 0) {
                    value = (oldValue * 10) + thisDigit;
                } else {
                    value = thisDigit;
                }
                this.mArgs[this.mArgIndex] = value;
            }
            continueSequence();
        } else if (b == (byte) 59) {
            if (this.mArgIndex < this.mArgs.length) {
                this.mArgIndex++;
            }
            continueSequence();
        } else {
            unknownSequence(b);
        }
    }

    private int getArg0(int defaultValue) {
        return getArg(0, defaultValue);
    }

    private int getArg1(int defaultValue) {
        return getArg(1, defaultValue);
    }

    private int getArg(int index, int defaultValue) {
        int result = this.mArgs[index];
        if (result < 0) {
            return defaultValue;
        }
        return result;
    }

    private void unimplementedSequence(byte b) {
        finishSequence();
    }

    private void unknownSequence(byte b) {
        finishSequence();
    }

    private void unknownParameter(int parameter) {
    }

    private void logError(String errorType, byte b) {
    }

    private void logError(String error) {
        finishSequence();
    }

    private void finishSequence() {
        this.mEscapeState = 0;
    }

    private boolean autoWrapEnabled() {
        return true;
    }

    private void emit(byte b) {
        boolean autoWrap = autoWrapEnabled();
        boolean eol = false;
        if (autoWrap && this.mCursorCol == this.mColumns - 1 && this.mAboutToAutoWrap && (!this.variableChar || b > (byte) 0)) {
            if (b < (byte) 0 || !EncodingBuffer.isNonAsciiHeader(b)) {
                eol = true;
            }
            this.mScreen.setLineWrap(this.mCursorRow);
            this.mCursorCol = 0;
            if (this.mCursorRow + 1 < this.mBottomMargin) {
                this.mCursorRow++;
            } else {
                scroll();
            }
        }
        if (this.mInsertMode) {
            int destCol = this.mCursorCol + 1;
            if (destCol < this.mColumns && (b > (byte) 0 || EncodingBuffer.isNonAsciiHeader(b))) {
                this.mScreen.blockCopy(this.mCursorCol, this.mCursorRow, this.mColumns - destCol, 1, destCol, this.mCursorRow);
            }
        }
        this.mScreen.set(this.mCursorCol, this.mCursorRow, b, getForeColor(), getBackColor());
        if (autoWrap && (b > (byte) 0 || EncodingBuffer.isNonAsciiHeader(b))) {
            this.mAboutToAutoWrap = this.mCursorCol == this.mColumns + -1;
        }
        if ((b > (byte) 0 || EncodingBuffer.isNonAsciiHeader(b)) && !eol) {
            this.mCursorCol = Math.min(this.mCursorCol + 1, this.mColumns - 1);
            this.variableChar = true;
        } else if (eol) {
            this.mCursorCol = 1;
        } else {
            this.variableChar = false;
        }
    }

    private void setCursorRow(int row) {
        this.mCursorRow = row;
        this.mScreen.setYPointer(row);
        this.mAboutToAutoWrap = false;
    }

    private void setCursorCol(int col) {
        this.mCursorCol = col;
        this.mScreen.setXPointer(col);
        this.mAboutToAutoWrap = false;
    }

    private void setCursorRowCol(int row, int col) {
        this.mCursorRow = Math.min(row, this.mRows - 1);
        this.mCursorCol = Math.min(col, this.mColumns - 1);
        this.mScreen.setYPointer(this.mCursorRow);
        this.mScreen.setXPointer(this.mCursorCol);
        this.mAboutToAutoWrap = false;
    }

    public void reset() {
        this.mCursorRow = 0;
        this.mCursorCol = 0;
        this.mArgIndex = 0;
        this.mContinueSequence = false;
        this.mEscapeState = 0;
        this.mSavedCursorRow = 0;
        this.mSavedCursorCol = 0;
        this.mDecFlags = 0;
        this.mSavedDecFlags = 0;
        this.mInsertMode = false;
        this.mAutomaticNewlineMode = false;
        this.mTopMargin = 0;
        this.mBottomMargin = this.mRows;
        this.mAboutToAutoWrap = false;
        this.mForeColor = 7;
        this.mBackColor = 0;
        this.mInverseColors = false;
        this.mbKeypadApplicationMode = false;
        this.mAlternateCharSet = false;
        setDefaultTabStops();
        blockClear(0, 0, this.mColumns, this.mRows);
    }

    public String getTranscriptText() {
        return this.mScreen.getTranscriptText();
    }

    public void setIncomingEoL_0D(int eol) {
        this.mIncomingEoL_0D = eol;
    }

    public void setIncomingEoL_0A(int eol) {
        this.mIncomingEoL_0A = eol;
    }
}
