package com.sec.android.app.voicenote.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.UpdateProvider;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

public class PackageReplacedReceiver extends BroadcastReceiver implements UpdateProvider.StubListener {
    private static final String PACKAGE_REPLACED_ACTION = "android.intent.action.MY_PACKAGE_REPLACED";
    private static final String TAG = "PackageReplacedReceiver";

    public void onReceive(Context context, Intent intent) {
        Log.m19d(TAG, "onReceive");
        if (intent != null && intent.getAction().equalsIgnoreCase(PACKAGE_REPLACED_ACTION)) {
            UpdateProvider.getInstance().checkUpdate(this);
        }
    }

    public void onUpdateCheckFail(UpdateProvider.StubData stubData) {
        Log.m19d(TAG, "onUpdateCheckFail");
        Settings.setSettings(Settings.KEY_UPDATE_CHECK_RESULT_CODE, UpdateProvider.StubCodes.UPDATE_CHECK_FAIL);
        clearUpdateChecker();
    }

    public void onNoMatchingApplication(UpdateProvider.StubData stubData) {
        Log.m19d(TAG, "onNoMatchingApplication");
        Settings.setSettings(Settings.KEY_UPDATE_CHECK_RESULT_CODE, "0");
        clearUpdateChecker();
    }

    public void onUpdateNotNecessary(UpdateProvider.StubData stubData) {
        Log.m19d(TAG, "onUpdateNotNecessary");
        Settings.setSettings(Settings.KEY_UPDATE_CHECK_RESULT_CODE, "1");
        clearUpdateChecker();
    }

    public void onUpdateAvailable(UpdateProvider.StubData stubData) {
        Log.m19d(TAG, "onUpdateAvailable");
    }

    private void clearUpdateChecker() {
        Log.m19d(TAG, "clearUpdateChecker");
        VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.INVALIDATE_MENU));
        Settings.setSettings(Settings.KEY_UPDATE_CHECK_FROM_GALAXY_APPS, false);
    }
}
