package com.sec.android.app.voicenote.common.util;

import android.app.ActivityManager;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;

public class BackgroundRestrictHelper {
    private static final String TAG = "BackgroundRestrictHelper";

    public static boolean isVoiceRecorderAddedToSleepingApp() {
        ActivityManager activityManager = (ActivityManager) VoiceNoteApplication.getApplication().getSystemService("activity");
        if (activityManager != null) {
            boolean isBackgroundRestricted = activityManager.isBackgroundRestricted();
            Log.m26i(TAG, "VoiceRecorder added to Sleeping App: " + isBackgroundRestricted);
            return isBackgroundRestricted;
        }
        Log.m26i(TAG, "Can not get Activity Manager");
        return false;
    }
}
