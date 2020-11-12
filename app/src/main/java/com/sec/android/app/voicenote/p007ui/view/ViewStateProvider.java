package com.sec.android.app.voicenote.p007ui.view;

import com.sec.android.app.voicenote.provider.Log;

/* renamed from: com.sec.android.app.voicenote.ui.view.ViewStateProvider */
public class ViewStateProvider {
    private static final String TAG = "ViewStateProvider";
    private static ViewStateProvider mInstance;
    private boolean mIsConvertAnimationRunning = false;
    private boolean mIsConvertSttHelpGuideShowing = false;

    private ViewStateProvider() {
        Log.m26i(TAG, "creator");
    }

    public boolean isConvertSttHelpGuideShowing() {
        return this.mIsConvertSttHelpGuideShowing;
    }

    public boolean isConvertAnimationRunning() {
        return this.mIsConvertAnimationRunning;
    }

    public void setConvertSttHelpGuideState(boolean z) {
        this.mIsConvertSttHelpGuideShowing = z;
    }

    public void setConvertAnimationState(boolean z) {
        this.mIsConvertAnimationRunning = z;
    }

    public static ViewStateProvider getInstance() {
        if (mInstance == null) {
            synchronized (ViewStateProvider.class) {
                if (mInstance == null) {
                    mInstance = new ViewStateProvider();
                }
            }
        }
        return mInstance;
    }
}
