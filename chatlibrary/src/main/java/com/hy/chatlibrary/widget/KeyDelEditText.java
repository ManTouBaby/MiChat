package com.hy.chatlibrary.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

/**
 * @author:MtBaby
 * @date:2020/05/29 12:04
 * @desc:
 */
public class KeyDelEditText extends AppCompatEditText {

    private OnKeyListener mKeyListener;

    public KeyDelEditText(Context context) {
        super(context);
    }

    public KeyDelEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyDelEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setOnKeyListener(OnKeyListener l) {
        mKeyListener = l;
        super.setOnKeyListener(l);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new InnerInputConnection(super.onCreateInputConnection(outAttrs), true);
    }

    private class InnerInputConnection extends InputConnectionWrapper {

        public InnerInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            boolean ret = false;
            if (beforeLength == 1 && afterLength == 0 && mKeyListener != null) {
                ret = mKeyListener.onKey(KeyDelEditText.this, KeyEvent.KEYCODE_DEL, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            }
            return ret || super.deleteSurroundingText(beforeLength, afterLength);
        }
    }

}
