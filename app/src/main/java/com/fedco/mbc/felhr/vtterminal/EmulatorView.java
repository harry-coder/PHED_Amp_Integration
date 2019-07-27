package com.fedco.mbc.felhr.vtterminal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
//import com.felhr.droidterm.C0107R;
import com.fedco.mbc.felhr.constant.Command;
import com.fedco.mbc.felhr.droidterm.TerminalFragment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class EmulatorView extends View implements OnGestureListener {
    private static final int TRANSCRIPT_ROWS = 500;
    public static final int UPDATE = 1;
    private int mBackground;
    private Paint mBackgroundPaint;
    private ByteQueue mByteQueue;
    private int mCharacterHeight;
    private int mCharacterWidth;
    private Runnable mCheckSize;
    private int mColumns;
    private Paint mCursorPaint;
    private TerminalEmulator mEmulator;
    private String mFileNameLog;
    private int mForeground;
    private GestureDetector mGestureDetector;
    private final Handler mHandler;
    private int mHeight;
    private TermKeyListener mKeyListener;
    private boolean mKnownSize;
    private int mLeftColumn;
    private Date mOldTimeLog;
    private byte[] mReceiveBuffer;
    private boolean mRecording;
    private int mRows;
    private float mScrollRemainder;
    private TextRenderer mTextRenderer;
    private int mTextSize;
    private int mTopRow;
    private TranscriptScreen mTranscriptScreen;
    private int mVisibleColumns;
    private int mWidth;
    private TerminalFragment terminalFragment;
    private ViewGroup terminalLayout;

    class C01101 implements Runnable {
        C01101() {
        }

        public void run() {
            EmulatorView.this.updateSize();
            EmulatorView.this.mHandler.postDelayed(this, 1000);
        }
    }

    class C01112 extends Handler {
        C01112() {
        }

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                EmulatorView.this.update();
            }
        }
    }

    public EmulatorView(Context context) {
        super(context);
        this.mOldTimeLog = new Date();
        this.mRecording = false;
        this.mCheckSize = new C01101();
        this.mHandler = new C01112();
        commonConstructor(context);
    }

    public void onResume() {
        updateSize();
        this.mHandler.postDelayed(this.mCheckSize, 1000);
    }

    public void onPause() {
        this.mHandler.removeCallbacks(this.mCheckSize);
    }

    public void register(TermKeyListener listener) {
        this.mKeyListener = listener;
    }

    public void setColors(int foreground, int background) {
        this.mForeground = foreground;
        this.mBackground = background;
        updateText();
    }

    public String getTranscriptText() {
        return this.mEmulator.getTranscriptText();
    }

    public void resetTerminal() {
        this.mEmulator.reset();
        invalidate();
    }

    public void resetHardTerminal() {
        this.mTranscriptScreen.resetTranscript();
        this.mByteQueue.resetBuffer();
        this.mEmulator.reset();
        invalidate();
    }

    public boolean onCheckIsTextEditor() {
        return true;
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.inputType = 0;
        return new BaseInputConnection(this, false) {
            private final String KEYCODE_CHARS = "\u0000\u0000\u0000\u0000\u0000\u0000\u00000123456789*#\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000abcdefghijklmnopqrstuvwxyz,.\u0000\u0000\u0000\u0000\t \u0000\u0000\u0000\r`-=[]\\;'/@\u0000\u0000\u0000+";

            public boolean beginBatchEdit() {
                return true;
            }

            public boolean clearMetaKeyStates(int states) {
                return true;
            }

            public boolean commitCompletion(CompletionInfo text) {
                return true;
            }

            public boolean commitText(CharSequence text, int newCursorPosition) {
                sendText(text);
                return true;
            }

            public boolean deleteSurroundingText(int leftLength, int rightLength) {
                return true;
            }

            public boolean endBatchEdit() {
                return true;
            }

            public boolean finishComposingText() {
                return true;
            }

            public int getCursorCapsMode(int reqModes) {
                return 0;
            }

            public ExtractedText getExtractedText(ExtractedTextRequest request, int flags) {
                return null;
            }

            public CharSequence getTextAfterCursor(int n, int flags) {
                return null;
            }

            public CharSequence getTextBeforeCursor(int n, int flags) {
                return null;
            }

            public boolean performEditorAction(int actionCode) {
                if (actionCode != 0) {
                    return false;
                }
                sendText("\n");
                return true;
            }

            public boolean performContextMenuAction(int id) {
                return true;
            }

            public boolean performPrivateCommand(String action, Bundle data) {
                return true;
            }

            public boolean sendKeyEvent(KeyEvent event) {
                if (event.getAction() == 0) {
                    int keyCode = event.getKeyCode();
                    if (keyCode >= 0 && keyCode < "\u0000\u0000\u0000\u0000\u0000\u0000\u00000123456789*#\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000abcdefghijklmnopqrstuvwxyz,.\u0000\u0000\u0000\u0000\t \u0000\u0000\u0000\r`-=[]\\;'/@\u0000\u0000\u0000+".length()) {
                        char c = "\u0000\u0000\u0000\u0000\u0000\u0000\u00000123456789*#\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000abcdefghijklmnopqrstuvwxyz,.\u0000\u0000\u0000\u0000\t \u0000\u0000\u0000\r`-=[]\\;'/@\u0000\u0000\u0000+".charAt(keyCode);
                        if (c <= '\u0000') {
                            switch (keyCode) {
                                case 19:
                                case 20 /*20*/:
                                case 21 /*21*/:
                                case 22 /*22*/:
                                    super.sendKeyEvent(event);
                                    break;
                                default:
                                    break;
                            }
                        }
                        sendChar(c);
                    }
                }
                return true;
            }

            public boolean setComposingText(CharSequence text, int newCursorPosition) {
                return true;
            }

            public boolean setSelection(int start, int end) {
                return true;
            }

            private void sendChar(int c) {
                try {
                    mapAndSend(c);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            private void sendText(CharSequence text) {
                int n = text.length();
                int i = 0;
                while (i < n) {
                    try {
                        mapAndSend(text.toString().codePointAt(i));
                        i++;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }

            private void mapAndSend(int c) throws IOException {
                EmulatorView.this.terminalFragment.writeWithEncoding(EmulatorView.this.mKeyListener.mapControlChar(c));
            }
        };
    }

    public void write(byte[] buffer, int length) {
        try {
            this.mByteQueue.write(buffer, 0, length);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        this.mHandler.sendMessage(this.mHandler.obtainMessage(1));
    }

    public boolean getKeypadApplicationMode() {
        return this.mEmulator.getKeypadApplicationMode();
    }

    public EmulatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmulatorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mOldTimeLog = new Date();
        this.mRecording = false;
        this.mCheckSize = new C01101();
        this.mHandler = new C01112();
//        TypedArray a = context.obtainStyledAttributes(R.styleable.EmulatorView);
//        initializeScrollbars(a);
//        a.recycle();
        commonConstructor(context);
    }

    private void commonConstructor(Context context) {
        this.mTextRenderer = null;
        this.mCursorPaint = new Paint();
        this.mCursorPaint.setARGB(255, 128, 128, 128);
        this.mBackgroundPaint = new Paint();
        this.mTopRow = 0;
        this.mLeftColumn = 0;
        this.mGestureDetector = new GestureDetector(context, this, null);
        this.mGestureDetector.setIsLongpressEnabled(true);
        setVerticalScrollBarEnabled(true);
    }

    protected int computeVerticalScrollRange() {
        if (this.mTranscriptScreen == null) {
            return 0;
        }
        return this.mTranscriptScreen.getActiveRows();
    }

    protected int computeVerticalScrollExtent() {
        return this.mRows;
    }

    protected int computeVerticalScrollOffset() {
        if (this.mTranscriptScreen == null) {
            return 0;
        }
        return (this.mTranscriptScreen.getActiveRows() + this.mTopRow) - this.mRows;
    }

    public void initialize(TerminalFragment terminalFragment) {
        this.terminalFragment = terminalFragment;
        this.mTextSize = 15;
        this.mForeground = -1;
        this.mBackground = -16777216;
        updateText();
        this.mReceiveBuffer = new byte[131072];
        this.mByteQueue = new ByteQueue(131072);
    }

    public void setLayout(ViewGroup viewGroup) {
        this.terminalLayout = viewGroup;
    }

    public void append(byte[] buffer, int base, int length) {
        this.mEmulator.append(buffer, base, length);
        ensureCursorVisible();
        invalidate();
    }

    public void page(int delta) {
        this.mTopRow = Math.min(0, Math.max(-this.mTranscriptScreen.getActiveTranscriptRows(), this.mTopRow + (this.mRows * delta)));
        invalidate();
    }

    public void pageHorizontal(int deltaColumns) {
        this.mLeftColumn = Math.max(0, Math.min(this.mLeftColumn + deltaColumns, this.mColumns - this.mVisibleColumns));
        invalidate();
    }

    public void setTextSize(int fontSize) {
        this.mTextSize = fontSize;
        updateText();
    }

    public boolean onSingleTapUp(MotionEvent e) {
        this.terminalFragment.toggleKeyboard();
        return true;
    }

    public void onLongPress(MotionEvent e) {
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        distanceY += this.mScrollRemainder;
        int deltaRows = (int) (distanceY / ((float) this.mCharacterHeight));
        this.mScrollRemainder = distanceY - ((float) (this.mCharacterHeight * deltaRows));
        this.mTopRow = Math.min(0, Math.max(-this.mTranscriptScreen.getActiveTranscriptRows(), this.mTopRow + deltaRows));
        awakenScrollBars();
        invalidate();
        return true;
    }

    public void onSingleTapConfirmed(MotionEvent e) {
    }

    public boolean onJumpTapDown(MotionEvent e1, MotionEvent e2) {
        this.mTopRow = 0;
        invalidate();
        return true;
    }

    public boolean onJumpTapUp(MotionEvent e1, MotionEvent e2) {
        this.mTopRow = -this.mTranscriptScreen.getActiveTranscriptRows();
        invalidate();
        return true;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        this.mScrollRemainder = 0.0f;
        onScroll(e1, e2, 2.0f * velocityX, -2.0f * velocityY);
        return true;
    }

    public void onShowPress(MotionEvent e) {
    }

    public boolean onDown(MotionEvent e) {
        this.mScrollRemainder = 0.0f;
        return true;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        return this.mGestureDetector.onTouchEvent(ev);
    }

    private void updateText() {
        if (this.mTextSize > 0) {
            this.mTextRenderer = new PaintRenderer(this.mTextSize, this.mForeground, this.mBackground);
        }
        this.mBackgroundPaint.setColor(this.mBackground);
        this.mCharacterWidth = this.mTextRenderer.getCharacterWidth();
        this.mCharacterHeight = this.mTextRenderer.getCharacterHeight();
        if (this.mKnownSize) {
            updateSize();
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (!this.mKnownSize) {
            this.mKnownSize = true;
        }
        updateSize();
    }

    private void updateSize(int w, int h) {
        if (w > 0 && h > 0) {
            this.mColumns = w / this.mCharacterWidth;
            this.mRows = h / this.mCharacterHeight;
            if (this.mTranscriptScreen != null) {
                this.mEmulator.updateSize(this.mColumns, this.mRows);
            } else {
                this.mTranscriptScreen = new TranscriptScreen(this.mColumns, TRANSCRIPT_ROWS, this.mRows, 0, 7);
                this.mEmulator = new TerminalEmulator(this.mTranscriptScreen, this.mColumns, this.mRows);
            }
            this.mTopRow = 0;
            this.mLeftColumn = 0;
            layout(0, 0, w, h);
            invalidate();
        }
    }

    public void updateSize() {
        if (this.terminalFragment != null && this.mKnownSize) {
            Rect visibleRect = new Rect();
            this.terminalLayout.getDrawingRect(visibleRect);
            int w = visibleRect.width();
            int h = (visibleRect.height() + 0) - 2;
            if (w != this.mWidth || h != this.mHeight) {
                this.mWidth = w;
                this.mHeight = h;
                updateSize(w, h);
            }
        }
    }

    private void update() {
        try {
            int bytesRead = this.mByteQueue.read(this.mReceiveBuffer, 0, Math.min(this.mByteQueue.getBytesAvailable(), this.mReceiveBuffer.length));
            String stringRead = new String(this.mReceiveBuffer, 0, bytesRead);
            append(this.mReceiveBuffer, 0, bytesRead);
            if (this.mRecording) {
                writeLog(stringRead);
            }
        } catch (InterruptedException e) {
        }
    }

    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        if (this.mCharacterWidth != 0) {
            canvas.drawRect(0.0f, 0.0f, (float) w, (float) h, this.mBackgroundPaint);
            this.mVisibleColumns = w / this.mCharacterWidth;
            float x = (float) ((-this.mLeftColumn) * this.mCharacterWidth);
            float y = (float) this.mCharacterHeight;
            int endLine = this.mTopRow + this.mRows;
            int cx = this.mEmulator.getCursorCol();
            int cy = this.mEmulator.getCursorRow();
            for (int i = this.mTopRow; i < endLine; i++) {
                int cursorX = -1;
                if (i == cy) {
                    cursorX = cx;
                }
                this.mTranscriptScreen.drawText(i, canvas, x, y, this.mTextRenderer, cursorX);
                y += (float) this.mCharacterHeight;
            }
        }
    }

    private void ensureCursorVisible() {
        this.mTopRow = 0;
        if (this.mVisibleColumns > 0) {
            int cx = this.mEmulator.getCursorCol();
            int visibleCursorX = this.mEmulator.getCursorCol() - this.mLeftColumn;
            if (visibleCursorX < 0) {
                this.mLeftColumn = cx;
            } else if (visibleCursorX >= this.mVisibleColumns) {
                this.mLeftColumn = (cx - this.mVisibleColumns) + 1;
            }
        }
    }

    public void setFileNameLog(String fileNameLog) {
        this.mFileNameLog = fileNameLog;
    }

    public void startRecording() {
        this.mRecording = true;
    }

    public void stopRecording() {
        this.mRecording = false;
    }

    public boolean writeLog(String buffer) {
        String state = Environment.getExternalStorageState();
        File logFile = new File(this.mFileNameLog);
        if ("mounted".equals(state)) {
            try {
                FileOutputStream f = new FileOutputStream(logFile, true);
                PrintWriter pw = new PrintWriter(f);
                pw.print(buffer);
                pw.flush();
                pw.close();
                f.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return true;
            }
        } else if ("mounted_ro".equals(state)) {
            stopRecording();
            return false;
        } else {
            stopRecording();
            return false;
        }
    }

    public void setIncomingEoL_0D(int eol) {
        if (this.mEmulator != null) {
            this.mEmulator.setIncomingEoL_0D(eol);
        }
    }

    public void setIncomingEoL_0A(int eol) {
        if (this.mEmulator != null) {
            this.mEmulator.setIncomingEoL_0A(eol);
        }
    }

    public void setEOL(int mode) {
        if (mode == 0) {
            this.mKeyListener.setEOL(0);
        } else if (mode == 1) {
            this.mKeyListener.setEOL(1);
        } else {
            this.mKeyListener.setEOL(2);
        }
    }
}
