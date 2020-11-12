package com.sec.android.app.voicenote.bixby.action;

import android.content.Context;
import com.samsung.android.sdk.bixby2.action.ResponseCallback;
import com.sec.android.app.voicenote.provider.Log;
import java.util.Observable;
import java.util.Observer;

public class PlayRecordingFile extends AbstractAction {
    private static final String TAG = "PlayRecordingFile";

    /* access modifiers changed from: protected */
    public void addObserver(Observer observer) {
        super.addObserver(observer);
    }

    /* access modifiers changed from: protected */
    public void deleteObserver(Observer observer) {
        super.deleteObserver(observer);
    }

    public void executeAction(Context context, String str, ResponseCallback responseCallback) {
        Log.m26i(TAG, "executeAction");
    }

    /* access modifiers changed from: protected */
    public void sendResponse(boolean z, String str) {
        Log.m26i(TAG, "sendResponse");
    }

    public void update(Observable observable, Object obj) {
        Log.m26i(TAG, "update");
    }
}
