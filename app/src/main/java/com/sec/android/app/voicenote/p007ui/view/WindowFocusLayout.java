package com.sec.android.app.voicenote.p007ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/* renamed from: com.sec.android.app.voicenote.ui.view.WindowFocusLayout */
public class WindowFocusLayout extends LinearLayout {
    private OnWindowFocusChangeListener mWindowFocusChangeListener;

    /* renamed from: com.sec.android.app.voicenote.ui.view.WindowFocusLayout$OnWindowFocusChangeListener */
    public interface OnWindowFocusChangeListener {
        void onWindowFocusChanged(boolean z);
    }

    public WindowFocusLayout(Context context) {
        super(context);
    }

    public WindowFocusLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public WindowFocusLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        OnWindowFocusChangeListener onWindowFocusChangeListener = this.mWindowFocusChangeListener;
        if (onWindowFocusChangeListener != null) {
            onWindowFocusChangeListener.onWindowFocusChanged(z);
        }
    }

    public void setOnWindowFocusChangeListener(OnWindowFocusChangeListener onWindowFocusChangeListener) {
        this.mWindowFocusChangeListener = onWindowFocusChangeListener;
    }
}
