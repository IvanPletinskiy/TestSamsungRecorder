package com.sec.android.app.voicenote.provider;

import android.content.Context;


public class UPSMProvider {
    public static final String EMERGENCY_STATE_CHANGED = "com.samsung.intent.action.EMERGENCY_STATE_CHANGED";
    private static final String TAG = "UPSMProvider";
    private static volatile UPSMProvider mInstance;
    private Context mAppContext;

    public boolean supportMaxMode() {
        return true;
    }

    private UPSMProvider() {
        Log.m19d(TAG, "UPSMProvider creator !!");
    }

    public static UPSMProvider getInstance() {
        if (mInstance == null) {
            synchronized (UPSMProvider.class) {
                if (mInstance == null) {
                    mInstance = new UPSMProvider();
                }
            }
        }
        return mInstance;
    }

    public void setApplicationContext(Context context) {
        this.mAppContext = context;
    }

    public boolean isUltraPowerSavingMode() {
        Context context = this.mAppContext;
//        return context != null && SemEmergencyManager.isEmergencyMode(context) && SemEmergencyManager.getInstance(this.mAppContext).checkModeType(512);
        return false;
    }
}
