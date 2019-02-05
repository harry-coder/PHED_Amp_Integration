package com.fedco.mbc.felhr.vtterminal;

import android.support.v4.media.TransportMediator;
import android.view.KeyEvent;

public class TermKeyListener {
    private static final int CR = 13;
    private static final int LF = 10;
    private static final int LFCR = 2573;
    private int eol = 10;
    private ModifierKey mAltKey = new ModifierKey();
    private ModifierKey mCapKey = new ModifierKey();
    private ModifierKey mControlKey = new ModifierKey();

    private class ModifierKey {
        private static final int LOCKED = 4;
        private static final int PRESSED = 1;
        private static final int RELEASED = 2;
        private static final int UNPRESSED = 0;
        private static final int USED = 3;
        private int mState = 0;

        public void onPress() {
            switch (this.mState) {
                case 1:
                case 3:
                    return;
                case 2:
                    this.mState = 4;
                    return;
                case 4:
                    this.mState = 0;
                    return;
                default:
                    this.mState = 1;
                    return;
            }
        }

        public void onRelease() {
            switch (this.mState) {
                case 1:
                    this.mState = 2;
                    return;
                case 3:
                    this.mState = 0;
                    return;
                default:
                    return;
            }
        }

        public void adjustAfterKeypress() {
            switch (this.mState) {
                case 1:
                    this.mState = 3;
                    return;
                case 2:
                    this.mState = 0;
                    return;
                default:
                    return;
            }
        }

        public boolean isActive() {
            return this.mState != 0;
        }
    }

    public void setEOL(int mode) {
        if (mode == 0 || mode != 1) {
        }
    }

    public void handleControlKey(boolean down) {
        if (down) {
            this.mControlKey.onPress();
        } else {
            this.mControlKey.onRelease();
        }
    }

    public int mapControlChar(int ch) {
        int result = ch;
        if (this.mControlKey.isActive()) {
            if (result >= 97 && result <= 122) {
                result = (char) ((result - 97) + 1);
            } else if (result == 32) {
                result = 0;
            } else if (result == 91 || result == 49) {
                result = 27;
            } else if (result == 92 || result == 46) {
                result = 28;
            } else if (result == 93 || result == 48) {
                result = 29;
            } else if (result == 94 || result == 54) {
                result = 30;
            } else if (result == 95 || result == 53) {
                result = 31;
            }
        }
        if (result > -1) {
            this.mAltKey.adjustAfterKeypress();
            this.mCapKey.adjustAfterKeypress();
            this.mControlKey.adjustAfterKeypress();
        }
        return result;
    }

    public int keyDown(int keyCode, KeyEvent event) {
        int i = 0;
        int result = -1;
        switch (keyCode) {
            case 57:
            case 58:
                this.mAltKey.onPress();
                break;
            case 59:
            case 60:
                this.mCapKey.onPress();
                break;
            case 66:
                result = 13;
                break;
            case 67:
                result = TransportMediator.KEYCODE_MEDIA_PAUSE;
                break;
            default:
                int i2;
                if (this.mCapKey.isActive()) {
                    i2 = 1;
                } else {
                    i2 = 0;
                }
                if (this.mAltKey.isActive()) {
                    i = 2;
                }
                result = event.getUnicodeChar(i2 | i);
                break;
        }
        return mapControlChar(result);
    }

    public void keyUp(int keyCode) {
        switch (keyCode) {
            case 57:
            case 58:
                this.mAltKey.onRelease();
                return;
            case 59:
            case 60:
                this.mCapKey.onRelease();
                return;
            default:
                return;
        }
    }
}
