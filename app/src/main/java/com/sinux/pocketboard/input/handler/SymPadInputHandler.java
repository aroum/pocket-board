package com.sinux.pocketboard.input.handler;

import android.content.Context;
import android.media.AudioManager;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

import com.sinux.pocketboard.PocketBoardIME;

public class SymPadInputHandler extends ProxyInputHandler {

    private final PocketBoardIME pocketBoardIME;

    public SymPadInputHandler(PocketBoardIME pocketBoardIME) {
        super(pocketBoardIME);
        this.pocketBoardIME = pocketBoardIME;
    }

    @Override
    protected int translateShortPressKeyCode(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_U:
                return KeyEvent.KEYCODE_MOVE_HOME;
            case KeyEvent.KEYCODE_B:
                return KeyEvent.KEYCODE_MOVE_END;
            case KeyEvent.KEYCODE_O:
                return KeyEvent.KEYCODE_PAGE_UP;
            case KeyEvent.KEYCODE_M:
                return KeyEvent.KEYCODE_PAGE_DOWN;
            case KeyEvent.KEYCODE_H:
                return KeyEvent.KEYCODE_DPAD_LEFT;
            case KeyEvent.KEYCODE_J:
                return KeyEvent.KEYCODE_DPAD_DOWN;
            case KeyEvent.KEYCODE_K:
                return KeyEvent.KEYCODE_DPAD_UP;
            case KeyEvent.KEYCODE_L:
                return KeyEvent.KEYCODE_DPAD_RIGHT;
            case KeyEvent.KEYCODE_DEL:
                return KeyEvent.KEYCODE_DEL;
            case KeyEvent.KEYCODE_P:
                return KeyEvent.KEYCODE_FORWARD_DEL;
            case KeyEvent.KEYCODE_X:
                   return KeyEvent.KEYCODE_CUT;
            case KeyEvent.KEYCODE_C:
                   return KeyEvent.KEYCODE_COPY;
            case KeyEvent.KEYCODE_V:
                return KeyEvent.KEYCODE_PASTE;
            case KeyEvent.KEYCODE_SPACE:
                return KeyEvent.KEYCODE_SPACE;
            case KeyEvent.KEYCODE_ENTER:
                return KeyEvent.KEYCODE_ENTER;
            case KeyEvent.KEYCODE_I:
                return KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
        }

        return 0;
    }

    @Override
    protected int translateLongPressKeyCode(int keyCode) {
        switch (keyCode) {
            // V - Rewind, B - Fast forward (media navigation)
            case KeyEvent.KEYCODE_V:
                return KeyEvent.KEYCODE_MEDIA_REWIND;
            case KeyEvent.KEYCODE_B:
                return KeyEvent.KEYCODE_MEDIA_FAST_FORWARD;
        }

        return 0;
    }

    @Override
    protected void handleKeyPress(int translatedKeyCode, KeyEvent originalEvent,
                                  InputConnection inputConnection, boolean shiftEnabled, boolean altEnabled) {
        if (isPlaybackKey(translatedKeyCode)) {
            AudioManager audioManager = (AudioManager)pocketBoardIME.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                audioManager.dispatchMediaKeyEvent(createNewKeyEvent(translatedKeyCode, originalEvent, KeyEvent.ACTION_DOWN, InputDevice.SOURCE_KEYBOARD));
                audioManager.dispatchMediaKeyEvent(createNewKeyEvent(translatedKeyCode, originalEvent, KeyEvent.ACTION_UP, InputDevice.SOURCE_KEYBOARD));
            }
        } else {
            if (originalEvent.isShiftPressed()) {
                inputConnection.sendKeyEvent(createNewKeyEvent(KeyEvent.KEYCODE_SHIFT_LEFT, originalEvent, KeyEvent.ACTION_DOWN, InputDevice.SOURCE_KEYBOARD));
            }
            inputConnection.sendKeyEvent(createNewKeyEvent(translatedKeyCode, originalEvent, KeyEvent.ACTION_DOWN, InputDevice.SOURCE_DPAD));
            inputConnection.sendKeyEvent(createNewKeyEvent(translatedKeyCode, originalEvent, KeyEvent.ACTION_UP, InputDevice.SOURCE_DPAD));
            if (originalEvent.isShiftPressed()) {
                inputConnection.sendKeyEvent(createNewKeyEvent(KeyEvent.KEYCODE_SHIFT_LEFT, originalEvent, KeyEvent.ACTION_UP, InputDevice.SOURCE_KEYBOARD));
            }
        }
    }

    public static boolean isPlaybackKey(int keyCode) {
        switch (keyCode) {
            // There are many media keys, but we only need these
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
            case KeyEvent.KEYCODE_MEDIA_NEXT:
            case KeyEvent.KEYCODE_MEDIA_REWIND:
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                return true;
        }

        return false;
    }
}
