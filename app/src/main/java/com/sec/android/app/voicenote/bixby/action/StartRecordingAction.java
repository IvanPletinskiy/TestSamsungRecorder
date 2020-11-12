package com.sec.android.app.voicenote.bixby.action;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.sec.android.app.voicenote.activity.WebTosActivity;
import com.sec.android.app.voicenote.bixby.constant.BixbyConstant;
import com.sec.android.app.voicenote.common.util.DataRepository;
import com.sec.android.app.voicenote.main.VNMainActivity;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.util.Observable;
import java.util.Observer;
import org.json.JSONException;
import org.json.JSONObject;

public class StartRecordingAction extends AbstractAction {
    private static final String TAG = "StartRecordingAction";

    public void update(Observable observable, Object obj) {
        int intValue = ((Integer) obj).intValue();
        Log.m26i(TAG, "update " + intValue);
        switch (intValue) {
            case Event.BIXBY_START_RECORDING_RESULT_FAIL /*29996*/:
                sendResponse(false, "BIXBY_START_RECORDING_RESULT_FAIL");
                return;
            case Event.BIXBY_START_RECORDING_RESULT_SUCCESS /*29997*/:
                sendResponse(true, "BIXBY_START_RECORDING_RESULT_SUCCESS");
                return;
            case Event.BIXBY_READY_TO_START_RECORDING /*29998*/:
                if (isNeedDataCheckDialog()) {
                    sendResponse(true, "BIXBY_SHOW_DATA_CHECK_DIALOG");
                    return;
                } else {
                    executeStartRecording();
                    return;
                }
            default:
                return;
        }
    }

    /* access modifiers changed from: protected */
    public void addObserver(Observer observer) {
        super.addObserver(observer);
    }

    /* access modifiers changed from: protected */
    public void deleteObserver(Observer observer) {
        super.deleteObserver(observer);
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x004b  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00a9  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void executeAction(android.content.Context r6, java.lang.String r7, com.samsung.android.sdk.bixby2.action.ResponseCallback r8) {
        /*
            r5 = this;
            java.lang.String r0 = "StartRecordingAction"
            java.lang.String r1 = "executeAction"
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r1)
            r5.mResponseCallback = r8
            r5.setRecordingMode(r7)
            r5.checkCondition(r6)
            java.lang.String r7 = r5.getRecordingMode()
            int r8 = r7.hashCode()
            r1 = 503107969(0x1dfcd181, float:6.6920467E-21)
            r2 = 2
            r3 = 1
            r4 = 0
            if (r8 == r1) goto L_0x003e
            r1 = 1182472020(0x467b1754, float:16069.832)
            if (r8 == r1) goto L_0x0034
            r1 = 1312628413(0x4e3d1ebd, float:7.9322707E8)
            if (r8 == r1) goto L_0x002a
            goto L_0x0048
        L_0x002a:
            java.lang.String r8 = "standard"
            boolean r7 = r7.equals(r8)
            if (r7 == 0) goto L_0x0048
            r7 = r4
            goto L_0x0049
        L_0x0034:
            java.lang.String r8 = "speech-to-text"
            boolean r7 = r7.equals(r8)
            if (r7 == 0) goto L_0x0048
            r7 = r2
            goto L_0x0049
        L_0x003e:
            java.lang.String r8 = "interview"
            boolean r7 = r7.equals(r8)
            if (r7 == 0) goto L_0x0048
            r7 = r3
            goto L_0x0049
        L_0x0048:
            r7 = -1
        L_0x0049:
            if (r7 == 0) goto L_0x00a9
            if (r7 == r3) goto L_0x0094
            if (r7 == r2) goto L_0x0050
            goto L_0x00b1
        L_0x0050:
            java.lang.String r7 = "executeAction, speech to text mode"
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r7)
            boolean r7 = r5.isSupportSpeechToTextMode()
            if (r7 != 0) goto L_0x0061
            java.lang.String r6 = "speech to text mode is not supported"
            r5.sendResponse(r4, r6)
            goto L_0x00b1
        L_0x0061:
            boolean r7 = r5.isNetworkConnected()
            if (r7 != 0) goto L_0x006d
            java.lang.String r6 = "network is not connected"
            r5.sendResponse(r4, r6)
            goto L_0x00b1
        L_0x006d:
            boolean r7 = r5.isChinaModel()
            if (r7 == 0) goto L_0x007c
            java.lang.String r7 = "executeAction, china model"
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r7)
            r5.prepareStartRecording(r6)
            goto L_0x00b1
        L_0x007c:
            boolean r7 = r5.isTnCAgreed()
            if (r7 == 0) goto L_0x008b
            java.lang.String r7 = "executeAction, TnC agreed"
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r7)
            r5.prepareStartRecording(r6)
            goto L_0x00b1
        L_0x008b:
            java.lang.String r7 = "executeAction, TnC is not agreed"
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r7)
            r5.startTnCActivity(r6)
            goto L_0x00b1
        L_0x0094:
            java.lang.String r7 = "executeAction, interview mode"
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r7)
            boolean r7 = r5.isSupportInterviewMode()
            if (r7 != 0) goto L_0x00a5
            java.lang.String r6 = "interview mode is not supported"
            r5.sendResponse(r4, r6)
            goto L_0x00b1
        L_0x00a5:
            r5.prepareStartRecording(r6)
            goto L_0x00b1
        L_0x00a9:
            java.lang.String r7 = "executeAction, standard mode"
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r7)
            r5.prepareStartRecording(r6)
        L_0x00b1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.bixby.action.StartRecordingAction.executeAction(android.content.Context, java.lang.String, com.samsung.android.sdk.bixby2.action.ResponseCallback):void");
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
        deleteObserver(this);
    }

    private void prepareStartRecording(Context context) {
        Log.m26i(TAG, "prepareStartRecording");
        addObserver(this);
        cleanUpCurrentState();
        setRecordingModeSettings();
        startMainActivity(context);
    }

    private void executeStartRecording() {
        Log.m26i(TAG, "executeStartRecording");
        VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.BIXBY_START_RECORDING));
    }

    private void cleanUpCurrentState() {
        Log.m26i(TAG, "cleanUpCurrentState");
        if (Engine.getInstance().getPlayerState() == 1 && Engine.getInstance().getRecorderState() == 1) {
            Activity topActivity = VoiceNoteApplication.getApplication().getTopActivity();
            if (topActivity == null) {
                Log.m26i(TAG, "cleanUpCurrentState - topActivity is null, open main");
                VoiceNoteObservable.getInstance().notifyObservers(4);
            } else if (!topActivity.getClass().getSimpleName().equals(VNMainActivity.class.getSimpleName()) || VoiceNoteApplication.getScene() != 1) {
                Log.m26i(TAG, "cleanUpCurrentState - not on main, to open main");
                topActivity.runOnUiThread($$Lambda$StartRecordingAction$ebdGz3oPm5Iz7aIReOeBlyGuxQU.INSTANCE);
            }
            CursorProvider.getInstance().resetSearchTag();
            DataRepository.getInstance().getCategoryRepository().setCurrentCategoryID(-1);
            return;
        }
        Log.m22e(TAG, "cleanUpCurrentState - player or recorder is not idle");
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0042  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0050  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setRecordingModeSettings() {
        /*
            r5 = this;
            java.lang.String r0 = "StartRecordingAction"
            java.lang.String r1 = "setRecordingModeSettings"
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r1)
            java.lang.String r0 = r5.mRecordingMode
            int r1 = r0.hashCode()
            r2 = 503107969(0x1dfcd181, float:6.6920467E-21)
            r3 = 2
            r4 = 1
            if (r1 == r2) goto L_0x0033
            r2 = 1182472020(0x467b1754, float:16069.832)
            if (r1 == r2) goto L_0x0029
            r2 = 1312628413(0x4e3d1ebd, float:7.9322707E8)
            if (r1 == r2) goto L_0x001f
            goto L_0x003d
        L_0x001f:
            java.lang.String r1 = "standard"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x003d
            r0 = 0
            goto L_0x003e
        L_0x0029:
            java.lang.String r1 = "speech-to-text"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x003d
            r0 = r3
            goto L_0x003e
        L_0x0033:
            java.lang.String r1 = "interview"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x003d
            r0 = r4
            goto L_0x003e
        L_0x003d:
            r0 = -1
        L_0x003e:
            java.lang.String r1 = "record_mode"
            if (r0 == 0) goto L_0x0050
            if (r0 == r4) goto L_0x004c
            if (r0 == r3) goto L_0x0047
            goto L_0x0053
        L_0x0047:
            r0 = 4
            com.sec.android.app.voicenote.provider.Settings.setSettings((java.lang.String) r1, (int) r0)
            goto L_0x0053
        L_0x004c:
            com.sec.android.app.voicenote.provider.Settings.setSettings((java.lang.String) r1, (int) r3)
            goto L_0x0053
        L_0x0050:
            com.sec.android.app.voicenote.provider.Settings.setSettings((java.lang.String) r1, (int) r4)
        L_0x0053:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.bixby.action.StartRecordingAction.setRecordingModeSettings():void");
    }

    private void startMainActivity(Context context) {
        Log.m26i(TAG, "startMainActivity");
        Intent intent = new Intent(context, VNMainActivity.class);
        intent.setAction("android.intent.action.MAIN");
        intent.addFlags(335544320);
        context.startActivity(intent);
    }

    private void startTnCActivity(Context context) {
        Log.m26i(TAG, "startTnCActivity");
        context.startActivity(new Intent(context, WebTosActivity.class));
    }
}
