package com.fedco.mbc.felhr.droidterm.utilities;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import java.util.concurrent.atomic.AtomicBoolean;

public class SoftKeyboard {
    int[] coords = new int[2];
    private InputMethodManager im;
    private View layout;
    private int layoutBottom;
    private SoftKeyboardChangesThread softKeyboardThread;

    public interface SoftKeyboardChanged {
        void onSoftKeyboardHide();

        void onSoftKeyboardShow();
    }

    private class SoftKeyboardChangesThread extends Thread {
        private SoftKeyboardChanged mCallback;
        private AtomicBoolean started = new AtomicBoolean(true);

        public void setCallback(SoftKeyboardChanged mCallback) {
            this.mCallback = mCallback;
        }

        public void run() {
            while (this.started.get()) {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                int currentBottomLocation = SoftKeyboard.this.getLayoutCoordinates();
                while (currentBottomLocation == SoftKeyboard.this.layoutBottom && this.started.get()) {
                    currentBottomLocation = SoftKeyboard.this.getLayoutCoordinates();
                }
                if (this.started.get()) {
                    this.mCallback.onSoftKeyboardShow();
                }
                while (currentBottomLocation != SoftKeyboard.this.layoutBottom && this.started.get()) {
                    currentBottomLocation = SoftKeyboard.this.getLayoutCoordinates();
                }
                if (this.started.get()) {
                    this.mCallback.onSoftKeyboardHide();
                }
            }
        }

        public void keyboardOpened() {
            synchronized (this) {
                notify();
            }
        }

        public void closeThread() {
            synchronized (this) {
                this.started.set(false);
                notify();
            }
        }
    }

    public SoftKeyboard(View layout, InputMethodManager im) {
        this.layout = layout;
        this.im = im;
        this.softKeyboardThread = new SoftKeyboardChangesThread();
        this.softKeyboardThread.start();
    }

    public void openSoftKeyboard() {
        this.layoutBottom = getLayoutCoordinates();
        this.im.toggleSoftInput(0, 1);
        this.softKeyboardThread.keyboardOpened();
    }

    public void closeSoftKeyboard() {
        this.im.toggleSoftInput(0, 1);
    }

    public void setSoftKeyboardCallback(SoftKeyboardChanged mCallback) {
        this.softKeyboardThread.setCallback(mCallback);
    }

    public void unregisterSoftKeyboardCallback() {
        this.softKeyboardThread.closeThread();
    }

    private int getLayoutCoordinates() {
        this.layout.getLocationOnScreen(this.coords);
        return this.coords[1] + this.layout.getHeight();
    }
}
