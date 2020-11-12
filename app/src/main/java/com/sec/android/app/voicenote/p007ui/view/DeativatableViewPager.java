package com.sec.android.app.voicenote.p007ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import androidx.viewpager.widget.ViewPager;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;

/* renamed from: com.sec.android.app.voicenote.ui.view.DeativatableViewPager */
public class DeativatableViewPager extends ViewPager {
    private String TAG = "DeativatableViewPager";
    private boolean mIsPagingDisabled = false;

    public DeativatableViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DeativatableViewPager(Context context) {
        super(context);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return !this.mIsPagingDisabled && super.onTouchEvent(motionEvent);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!this.mIsPagingDisabled) {
            try {
                return super.onInterceptTouchEvent(motionEvent);
            } catch (Exception e) {
                Log.m22e(this.TAG, e.toString());
            }
        }
        return false;
    }

    public void setPagingEnable(boolean z) {
        this.mIsPagingDisabled = !z;
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        Log.m26i(this.TAG, "dispatchKeyEvent");
        if (!this.mIsPagingDisabled) {
            return super.dispatchKeyEvent(keyEvent);
        }
        int keyCode = keyEvent.getKeyCode();
        if (keyCode == 21 || keyCode == 22) {
            if (VoiceNoteApplication.getScene() == 10 || VoiceNoteApplication.getScene() == 5 || VoiceNoteApplication.getScene() == 9) {
                return false;
            }
            if (VoiceNoteApplication.getScene() == 3 || VoiceNoteApplication.getScene() == 7) {
                return super.dispatchKeyEvent(keyEvent);
            }
        } else if (keyCode != 61) {
            return super.dispatchKeyEvent(keyEvent);
        }
        return super.dispatchKeyEvent(new KeyEvent(keyEvent.getAction(), keyEvent.hasModifiers(1) ? 19 : 20));
    }
}
