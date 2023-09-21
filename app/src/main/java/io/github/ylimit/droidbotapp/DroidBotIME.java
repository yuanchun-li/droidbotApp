package io.github.ylimit.droidbotapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.InputMethodService;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;


public class DroidBotIME extends InputMethodService {
    private static final String TAG = "DroidBotIME";

//    @Override
//    public boolean onEvaluateFullscreenMode() {
//        return false;
//    }
//
//    @Override
//    public boolean onEvaluateInputViewShown() {
//        return false;
//    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    @Override public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    private String IME_INPUT_TEXT = "DROIDBOT_INPUT_TEXT";

    private final int INPUT_MODE_SET = 0;
    private final int INPUT_MODE_APPEND = 1;

    private BroadcastReceiver mReceiver = null;

    @Override
    public View onCreateInputView() {
        if (mReceiver == null) {
            IntentFilter filter = new IntentFilter(IME_INPUT_TEXT);
            mReceiver = new InputReceiver();
            registerReceiver(mReceiver, filter);
        }

        return null;
    }

    public void onDestroy() {
        if (mReceiver != null)
            unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    class InputReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(IME_INPUT_TEXT)) {
                InputConnection ic = getCurrentInputConnection();
                if (ic == null) return;

                String text = intent.getStringExtra("text").replace("--", " ");
                int mode = intent.getIntExtra("mode", INPUT_MODE_SET);
                if (mode == INPUT_MODE_SET) {
                    ic.deleteSurroundingText(10000, 10000);
                }
                ic.commitText(text, 1);

            }
        }
    }
}