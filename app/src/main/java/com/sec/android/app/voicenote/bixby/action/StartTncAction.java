package com.sec.android.app.voicenote.bixby.action;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import com.samsung.android.sdk.bixby2.action.ResponseCallback;
import com.sec.android.app.voicenote.activity.WebTosActivity;
import com.sec.android.app.voicenote.bixby.constant.BixbyConstant;
import com.sec.android.app.voicenote.provider.Log;
import java.util.Observable;
import java.util.Observer;
import org.json.JSONException;
import org.json.JSONObject;

public class StartTncAction extends AbstractAction {
    private static final String TAG = "StartTncAction";

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
        startTnCActivity(context);
    }

    /* access modifiers changed from: protected */
    public void sendResponse(boolean z, String str) {
        Log.m32w(TAG, "sendResponse - result : " + z + ", cause : " + str);
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(BixbyConstant.ResponseOutputParameter.TNC, String.valueOf(z));
            jSONObject.put(BixbyConstant.ResponseOutputParameter.CAUSE, str);
        } catch (JSONException e) {
            Log.m26i(TAG, "sendResponse " + e.getMessage());
        }
        Log.m26i(TAG, "sendResponse response result " + jSONObject.toString());
        this.mResponseCallback.onComplete(jSONObject.toString());
    }

    private void startTnCActivity(Context context) {
        Log.m26i(TAG, "startTnCActivity");
        try {
            context.startActivity(new Intent(context, WebTosActivity.class));
        } catch (ActivityNotFoundException e) {
            Log.m24e(TAG, "ActivityNotFoundException", (Throwable) e);
            sendResponse(false, "ActivityNotFoundException");
        }
        sendResponse(true, "Done");
    }
}
