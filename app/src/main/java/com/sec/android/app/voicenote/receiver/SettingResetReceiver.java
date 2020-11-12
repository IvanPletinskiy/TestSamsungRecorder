package com.sec.android.app.voicenote.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Settings;

public class SettingResetReceiver extends BroadcastReceiver {
    private static final String TAG = "SettingResetReceiver";

    public void onReceive(Context context, Intent intent) {
        Log.m19d(TAG, "onReceive : " + intent);
        SharedPreferences.Editor edit = context.getSharedPreferences(Settings.KEY_PREFERENCES, 0).edit();
        edit.clear();
        edit.apply();
    }
}
