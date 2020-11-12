package com.sec.android.app.voicenote.service;

import android.content.Intent;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import com.sec.android.app.voicenote.common.util.AndroidForWork;
import com.sec.android.app.voicenote.provider.CallRejectChecker;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;

public class TelephonyCallScreeningService extends CallScreeningService {
    private static final String TAG = "TelephonyCallScreeningService";

    public void onScreenCall(Call.Details details) {
        Log.m26i(TAG, "onScreenCall - Call screening service triggered");
        CallScreeningService.CallResponse.Builder builder = new CallScreeningService.CallResponse.Builder();
        if (CallRejectChecker.getInstance().getReject()) {
            rejectCall(builder);
            if (!AndroidForWork.getInstance().isAndroidForWorkMode(VoiceNoteApplication.getApplication())) {
                CallRejectChecker.getInstance().increaseRejectCallCount();
            }
        } else {
            doNothing(builder);
        }
        respondToCall(details, builder.build());
    }

    private void doNothing(CallScreeningService.CallResponse.Builder builder) {
        Log.m26i(TAG, "doNothing - send default response to Telecom");
        builder.setDisallowCall(false);
        builder.setRejectCall(false);
        builder.setSkipCallLog(false);
        builder.setSkipNotification(false);
    }

    private void rejectCall(CallScreeningService.CallResponse.Builder builder) {
        Log.m26i(TAG, "endCall - mRejectCall : " + CallRejectChecker.getInstance().getReject());
        builder.setDisallowCall(true);
        builder.setRejectCall(true);
        builder.setSkipCallLog(false);
        builder.setSkipNotification(false);
    }

    public boolean onUnbind(Intent intent) {
        Log.m26i(TAG, "onUnbind from Telecom");
        stopSelf();
        return super.onUnbind(intent);
    }

    public void onDestroy() {
        Log.m19d(TAG, "onDeytroy");
        super.onDestroy();
    }
}
