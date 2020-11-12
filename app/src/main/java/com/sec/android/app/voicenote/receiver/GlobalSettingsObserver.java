package com.sec.android.app.voicenote.receiver;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

public class GlobalSettingsObserver extends ContentObserver {
    private static final String TAG = "GlobalSettingsObserver";

    public GlobalSettingsObserver(Handler handler) {
        super(handler);
    }

    public void onChange(boolean z, Uri uri) {
        String uri2 = uri.toString();
        if (uri2.contains("navigation_bar")) {
            Log.m19d(TAG, "onChange:" + uri2);
            VoiceNoteObservable.getInstance().notifyObservers(20);
        }
    }
}
