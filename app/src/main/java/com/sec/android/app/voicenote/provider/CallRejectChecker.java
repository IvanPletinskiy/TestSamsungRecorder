package com.sec.android.app.voicenote.provider;

public class CallRejectChecker {
    private static final String TAG = "CallRejectChecker";
    private static volatile CallRejectChecker mInstance;
    private boolean mEnableReject = false;

    private CallRejectChecker() {
        Log.m26i(TAG, "CallRejectChecker creator !!");
    }

    public static CallRejectChecker getInstance() {
        if (mInstance == null) {
            synchronized (CallRejectChecker.class) {
                if (mInstance == null) {
                    mInstance = new CallRejectChecker();
                }
            }
        }
        return mInstance;
    }

    public void setReject(boolean z) {
        if (Settings.getBooleanSettings(Settings.KEY_REC_CALL_REJECT)) {
            this.mEnableReject = z;
            Log.m26i(TAG, "setReject : " + z);
            return;
        }
        Log.m26i(TAG, "setReject : false");
        this.mEnableReject = false;
    }

    public boolean getReject() {
        return this.mEnableReject;
    }

    public void increaseRejectCallCount() {
        int rejectCallCount = getRejectCallCount() + 1;
        Settings.setSettings(Settings.KEY_CALL_REJECT_COUNT, rejectCallCount);
        Log.m26i(TAG, "increaseRejectCallCount : " + rejectCallCount);
    }

    public int getRejectCallCount() {
        return Settings.getIntSettings(Settings.KEY_CALL_REJECT_COUNT, 0);
    }

    public void resetRejectCallCount() {
        Settings.setSettings(Settings.KEY_CALL_REJECT_COUNT, 0);
    }
}
