package com.sec.android.app.voicenote.bixby.action;

import android.content.Context;
import com.samsung.android.sdk.bixby2.action.ResponseCallback;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Network;
import com.sec.android.app.voicenote.provider.RecognizerDBProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.util.Observer;

public abstract class AbstractAction implements Observer {
    private static final String TAG = "AbstractAction";
    protected boolean mIsChinaModel;
    protected boolean mIsNetworkConnected;
    protected boolean mIsSupportInterviewMode;
    protected boolean mIsSupportSpeechToTextMode;
    protected boolean mIsTnCAgreed;
    protected final VoiceNoteObservable mObservable = VoiceNoteObservable.getInstance();
    protected String mRecordingMode;
    protected ResponseCallback mResponseCallback;

    public abstract void executeAction(Context context, String str, ResponseCallback responseCallback);

    /* access modifiers changed from: protected */
    public abstract void sendResponse(boolean z, String str);

    /* access modifiers changed from: protected */
    public void addObserver(Observer observer) {
        this.mObservable.addObserver(observer);
    }

    /* access modifiers changed from: protected */
    public void deleteObserver(Observer observer) {
        this.mObservable.deleteObserver(observer);
    }

    /* access modifiers changed from: protected */
    public void setRecordingMode(String str) {
        this.mRecordingMode = str;
    }

    /* access modifiers changed from: protected */
    public String getRecordingMode() {
        return this.mRecordingMode;
    }

    /* access modifiers changed from: protected */
    public void checkCondition(Context context) {
        this.mIsNetworkConnected = Network.isNetworkConnected(context);
        this.mIsChinaModel = VoiceNoteFeature.FLAG_SUPPORT_DATA_CHECK_POPUP;
        boolean z = true;
        if (RecognizerDBProvider.getTOSAcceptedState() != 1) {
            z = false;
        }
        this.mIsTnCAgreed = z;
        this.mIsSupportInterviewMode = VoiceNoteFeature.FLAG_SUPPORT_INTERVIEW;
        this.mIsSupportSpeechToTextMode = VoiceNoteFeature.FLAG_SUPPORT_VOICE_MEMO(context);
        Log.m26i(TAG, "checkCondition - isNetworkConnected : " + this.mIsNetworkConnected);
        Log.m26i(TAG, "checkCondition - isChinaModel : " + this.mIsChinaModel);
        Log.m26i(TAG, "checkCondition - isTnCAgreed : " + this.mIsTnCAgreed);
        Log.m26i(TAG, "checkCondition - isSupportInterviewMode : " + this.mIsSupportInterviewMode);
        Log.m26i(TAG, "checkCondition - isSupportSpeechToTextMode : " + this.mIsSupportSpeechToTextMode);
    }

    /* access modifiers changed from: protected */
    public boolean isNetworkConnected() {
        return this.mIsNetworkConnected;
    }

    /* access modifiers changed from: protected */
    public boolean isChinaModel() {
        return this.mIsChinaModel;
    }

    /* access modifiers changed from: protected */
    public boolean isTnCAgreed() {
        return this.mIsTnCAgreed;
    }

    /* access modifiers changed from: protected */
    public boolean isSupportInterviewMode() {
        return this.mIsSupportInterviewMode;
    }

    /* access modifiers changed from: protected */
    public boolean isSupportSpeechToTextMode() {
        return this.mIsSupportSpeechToTextMode;
    }

    /* access modifiers changed from: protected */
    public boolean isNeedDataCheckDialog() {
        if (!isChinaModel() || !Settings.getBooleanSettings(Settings.KEY_DATA_CHECK_SHOW_AGAIN, true)) {
            return false;
        }
        return true;
    }
}
