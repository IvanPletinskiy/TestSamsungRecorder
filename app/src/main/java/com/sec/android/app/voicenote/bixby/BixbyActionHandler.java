package com.sec.android.app.voicenote.bixby;

import android.content.Context;
import android.os.Bundle;

import com.samsung.android.sdk.bixby2.action.ActionHandler;
import com.samsung.android.sdk.bixby2.action.ResponseCallback;
import com.sec.android.app.voicenote.bixby.action.GetRecordedFileCount;
import com.sec.android.app.voicenote.bixby.action.GetRecordingInfo;
import com.sec.android.app.voicenote.bixby.action.PlayRecordingFile;
import com.sec.android.app.voicenote.bixby.action.SpeechToTextInfoAction;
import com.sec.android.app.voicenote.bixby.action.StartRecordingAction;
import com.sec.android.app.voicenote.bixby.action.StartTncAction;
import com.sec.android.app.voicenote.bixby.constant.BixbyConstant;
import com.sec.android.app.voicenote.provider.Log;

import java.util.HashMap;

public class BixbyActionHandler extends ActionHandler {
    private static final String TAG = "BixbyActionHandler";

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void executeAction(android.content.Context r7, java.lang.String r8, android.os.Bundle r9, com.samsung.android.sdk.bixby2.action.ResponseCallback r10) {
        /*
            r6 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "executeAction - context/actionName/params : "
            r0.append(r1)
            r0.append(r7)
            java.lang.String r1 = "/"
            r0.append(r1)
            r0.append(r8)
            r0.append(r1)
            java.lang.String r1 = r9.toString()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "BixbyActionHandler"
            com.sec.android.app.voicenote.provider.Log.m26i(r1, r0)
            int r0 = r8.hashCode()
            r1 = 5
            r2 = 4
            r3 = 3
            r4 = 2
            r5 = 1
            switch(r0) {
                case -1631472886: goto L_0x0067;
                case -1625347077: goto L_0x005d;
                case -1598966521: goto L_0x0053;
                case -1349266015: goto L_0x0049;
                case -158618145: goto L_0x003f;
                case 1390714073: goto L_0x0035;
                default: goto L_0x0034;
            }
        L_0x0034:
            goto L_0x0071
        L_0x0035:
            java.lang.String r0 = "viv.voiceRecorderApp.StartTnc"
            boolean r8 = r8.equals(r0)
            if (r8 == 0) goto L_0x0071
            r8 = r4
            goto L_0x0072
        L_0x003f:
            java.lang.String r0 = "viv.voiceRecorderApp.GetRecordedFileCount"
            boolean r8 = r8.equals(r0)
            if (r8 == 0) goto L_0x0071
            r8 = r1
            goto L_0x0072
        L_0x0049:
            java.lang.String r0 = "viv.voiceRecorderApp.StartRecording"
            boolean r8 = r8.equals(r0)
            if (r8 == 0) goto L_0x0071
            r8 = 0
            goto L_0x0072
        L_0x0053:
            java.lang.String r0 = "viv.voiceRecorderApp.PlayRecordingFile"
            boolean r8 = r8.equals(r0)
            if (r8 == 0) goto L_0x0071
            r8 = r2
            goto L_0x0072
        L_0x005d:
            java.lang.String r0 = "viv.voiceRecorderApp.GetRecordingInfo"
            boolean r8 = r8.equals(r0)
            if (r8 == 0) goto L_0x0071
            r8 = r3
            goto L_0x0072
        L_0x0067:
            java.lang.String r0 = "viv.voiceRecorderApp.SpeechToTextInfo"
            boolean r8 = r8.equals(r0)
            if (r8 == 0) goto L_0x0071
            r8 = r5
            goto L_0x0072
        L_0x0071:
            r8 = -1
        L_0x0072:
            if (r8 == 0) goto L_0x0093
            if (r8 == r5) goto L_0x008f
            if (r8 == r4) goto L_0x008b
            if (r8 == r3) goto L_0x0087
            if (r8 == r2) goto L_0x0083
            if (r8 == r1) goto L_0x007f
            goto L_0x0096
        L_0x007f:
            r6.handleGetRecordedFileCount(r7, r9, r10)
            goto L_0x0096
        L_0x0083:
            r6.handlePlayRecordingFile(r7, r9, r10)
            goto L_0x0096
        L_0x0087:
            r6.handleGetRecordingInfo(r7, r9, r10)
            goto L_0x0096
        L_0x008b:
            r6.handleStartTncAction(r7, r9, r10)
            goto L_0x0096
        L_0x008f:
            r6.handleSpeechToTextInfoAction(r7, r9, r10)
            goto L_0x0096
        L_0x0093:
            r6.handleStartRecordingAction(r7, r9, r10)
        L_0x0096:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.bixby.BixbyActionHandler.executeAction(android.content.Context, java.lang.String, android.os.Bundle, com.samsung.android.sdk.bixby2.action.ResponseCallback):void");
    }

    private void handleGetRecordedFileCount(Context context, Bundle bundle, ResponseCallback responseCallback) {
        Log.m26i(TAG, "handleGetRecordedFileCount");
        new GetRecordedFileCount().executeAction(context, getParams(bundle, BixbyConstant.InputParameter.BIXBY_FILE_NAME), responseCallback);
    }

    private void handlePlayRecordingFile(Context context, Bundle bundle, ResponseCallback responseCallback) {
        Log.m26i(TAG, "handlePlayRecordingFile");
        new PlayRecordingFile().executeAction(context, (String) null, responseCallback);
    }

    private void handleStartRecordingAction(Context context, Bundle bundle, ResponseCallback responseCallback) {
        Log.m26i(TAG, "handleStartRecordingAction");
        new StartRecordingAction().executeAction(context, getParams(bundle, "recordingMode"), responseCallback);
    }

    private void handleSpeechToTextInfoAction(Context context, Bundle bundle, ResponseCallback responseCallback) {
        Log.m26i(TAG, "handleSpeechToTextInfoAction");
        new SpeechToTextInfoAction().executeAction(context, (String) null, responseCallback);
    }

    private void handleStartTncAction(Context context, Bundle bundle, ResponseCallback responseCallback) {
        Log.m26i(TAG, "handleStartTncAction");
        new StartTncAction().executeAction(context, (String) null, responseCallback);
    }

    private void handleGetRecordingInfo(Context context, Bundle bundle, ResponseCallback responseCallback) {
        Log.m26i(TAG, "handleGetRecordingInfo");
        new GetRecordingInfo().executeAction(context, (String) null, responseCallback);
    }

    private String getParams(Bundle bundle, String str) {
        Log.m26i(TAG, "getParams: " + str);
        HashMap hashMap = (HashMap) bundle.getSerializable(ActionHandler.PARAMS);
        if (hashMap == null || hashMap.isEmpty()) {
            throw new IllegalArgumentException("params/file name can't be null or empty");
        }
        String str2 = null;
//        for (String str3 : hashMap.keySet()) {
//            Log.m19d(TAG, "key - value: " + str3 + " - " + ((String) ((List) hashMap.get(str3)).get(0)));
//            if (str3.contains(str)) {
//                str2 = (String) ((List) hashMap.get(str3)).get(0);
//            }
//        }
        if (str2 != null || !str.equals("recordingMode")) {
            return str2;
        }
        throw new IllegalArgumentException("mandatory params(s) missing.");
    }
}
