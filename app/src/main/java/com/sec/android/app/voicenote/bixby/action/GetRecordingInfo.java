package com.sec.android.app.voicenote.bixby.action;

import android.content.Context;
import com.samsung.android.sdk.bixby2.action.ResponseCallback;
import com.sec.android.app.voicenote.bixby.constant.BixbyConstant;
import com.sec.android.app.voicenote.provider.Log;
import java.util.Observable;
import java.util.Observer;
import org.json.JSONException;
import org.json.JSONObject;

public class GetRecordingInfo extends AbstractAction {
    private static final String TAG = "GetRecordingInfo";

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
        checkCondition(context);
        sendResponse(true, "no need");
    }

    /* access modifiers changed from: protected */
    public void sendResponse(boolean z, String str) {
        Log.m32w(TAG, "sendResponse - result : " + z + ", cause : " + str);
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(BixbyConstant.ResponseOutputParameter.SUPPORT_INTERVIEW_MODE, String.valueOf(isSupportInterviewMode()));
            jSONObject.put(BixbyConstant.ResponseOutputParameter.SUPPORT_SPEECH_TO_TEXT_MODE, String.valueOf(isSupportSpeechToTextMode()));
            jSONObject.put(BixbyConstant.ResponseOutputParameter.NETWORK_CONNECTION, String.valueOf(isNetworkConnected()));
        } catch (JSONException e) {
            Log.m26i(TAG, "sendResponse " + e.getMessage());
        }
        Log.m26i(TAG, "sendResponse response data " + jSONObject.toString());
        this.mResponseCallback.onComplete(jSONObject.toString());
    }
}
