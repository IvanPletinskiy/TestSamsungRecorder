package com.sec.android.app.voicenote.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;

public class GalaxyAppsReceiver extends BroadcastReceiver {
    private static final String TAG = "GalaxyAppsReceiver";
    private static final String UPDATE_ACTION = "com.sec.android.app.samsungapps.UPDATE_EXISTS";

    public void onReceive(Context context, Intent intent) {
        Log.m26i(TAG, "onReceive");
        if (intent != null && intent.getAction().equalsIgnoreCase(UPDATE_ACTION)) {
            Log.m19d(TAG, "Installed Version name : " + VoiceNoteApplication.getApkVersionName());
            Log.m19d(TAG, "Installed Version code : " + VoiceNoteApplication.getApkVersionCode());
            Log.m19d(TAG, "New version name : " + intent.getStringExtra("version"));
            Log.m19d(TAG, "New version code : " + intent.getStringExtra("versionCode"));
            Settings.setSettings(Settings.KEY_UPDATE_CHECK_FROM_GALAXY_APPS, true);
        }
    }
}
