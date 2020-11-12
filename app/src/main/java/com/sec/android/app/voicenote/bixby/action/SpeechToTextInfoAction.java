package com.sec.android.app.voicenote.bixby.action;

import android.content.Context;
import com.samsung.android.sdk.bixby2.action.ResponseCallback;
import com.sec.android.app.voicenote.bixby.constant.BixbyConstant;
import com.sec.android.app.voicenote.provider.Log;
import java.util.Observable;
import java.util.Observer;
import org.json.JSONException;
import org.json.JSONObject;

public class SpeechToTextInfoAction extends AbstractAction {
    private static final String TAG = "SpeechToTextInfoAction";

    public void update(Observable observable, Object obj) {
    }

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
        this.mResponseCallback = responseCallback;
        setRecordingMode(str);
        checkCondition(context);
        if (!isSupportSpeechToTextMode()) {
            sendResponse(false, "speech to text mode is not supported");
        } else if (!isNetworkConnected()) {
            sendResponse(false, "network is not connected");
        } else if (isChinaModel()) {
            sendResponse(true, "china model");
        } else if (isTnCAgreed()) {
            sendResponse(true, "TnC agreed");
        } else {
            sendResponse(false, "TnC is not agreed");
        }
    }

    /* access modifiers changed from: protected */
    public void sendResponse(boolean z, String str) {
        Log.m32w(TAG, "sendResponse - result : " + z + ", cause : " + str);
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("recordingMode", getRecordingMode());
            jSONObject.put("status", String.valueOf(z));
            jSONObject.put(BixbyConstant.ResponseOutputParameter.NETWORK_CONNECTION, String.valueOf(isNetworkConnected()));
            jSONObject.put(BixbyConstant.ResponseOutputParameter.CHINA_MODEL, String.valueOf(isChinaModel()));
            jSONObject.put(BixbyConstant.ResponseOutputParameter.TNC, String.valueOf(isTnCAgreed()));
        } catch (JSONException e) {
            Log.m26i(TAG, "sendResponse " + e.getMessage());
        }
        Log.m26i(TAG, "sendResponse response result " + jSONObject.toString());
        this.mResponseCallback.onComplete(jSONObject.toString());
    }
}
