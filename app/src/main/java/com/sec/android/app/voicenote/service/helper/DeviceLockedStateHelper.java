package com.sec.android.app.voicenote.service.helper;

import android.content.Context;
import android.content.Intent;

import com.sec.android.app.voicenote.common.util.KeyguardManagerHelper;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.VoiceNoteService;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceLockedStateHelper {
    private static final int PERIOD_TIME_TO_CHECK_LOCKED_STATE = 300;
    private static final String TAG = "DeviceLockedStateHelper";
    private Context mContext;
    /* access modifiers changed from: private */
    public boolean mIsKeyguardLockedBySecure;
    /* access modifiers changed from: private */
//    public LocalBroadcastManager mLocalBroadcastManager;
    private Timer mTimer;

    public DeviceLockedStateHelper(Context context) {
        this.mContext = context;
    }

    public void startListenLockedStateChange() {
        Log.m19d(TAG, "startListenLockedStateChange");
//        if (this.mLocalBroadcastManager == null) {
//            this.mLocalBroadcastManager = LocalBroadcastManager.getInstance(this.mContext);
//        }
        new Thread(new Runnable() {
            public final void run() {
                DeviceLockedStateHelper.this.lambda$startListenLockedStateChange$0$DeviceLockedStateHelper();
            }
        }).start();
    }

    public /* synthetic */ void lambda$startListenLockedStateChange$0$DeviceLockedStateHelper() {
        this.mIsKeyguardLockedBySecure = KeyguardManagerHelper.isKeyguardLockedBySecure();
        if (this.mTimer == null) {
            this.mTimer = new Timer();
        }
        this.mTimer.schedule(new TimerTask() {
            public void run() {
                Intent intent;
                if (DeviceLockedStateHelper.this.mIsKeyguardLockedBySecure != KeyguardManagerHelper.isKeyguardLockedBySecure()) {
                    DeviceLockedStateHelper deviceLockedStateHelper = DeviceLockedStateHelper.this;
                    boolean unused = deviceLockedStateHelper.mIsKeyguardLockedBySecure = !deviceLockedStateHelper.mIsKeyguardLockedBySecure;
                    Log.m19d(DeviceLockedStateHelper.TAG, "locked state changed - isLocked = " + DeviceLockedStateHelper.this.mIsKeyguardLockedBySecure);
                    if (DeviceLockedStateHelper.this.mIsKeyguardLockedBySecure) {
                        intent = new Intent(VoiceNoteService.VOICENOTE_DEVICE_LOCKED);
                    } else {
                        intent = new Intent(VoiceNoteService.VOICENOTE_DEVICE_UNLOCKED);
                    }
//                    if (DeviceLockedStateHelper.this.mLocalBroadcastManager != null) {
//                        DeviceLockedStateHelper.this.mLocalBroadcastManager.sendBroadcast(intent);
//                    }
                }
            }
        }, 0, 300);
    }

    public void stopListenLockedStateChange() {
        Log.m19d(TAG, "stopListenLockedStateChange");
        Timer timer = this.mTimer;
        if (timer != null) {
            timer.cancel();
            this.mTimer = null;
        }
//        this.mLocalBroadcastManager = null;
    }
}
