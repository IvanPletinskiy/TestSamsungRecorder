package com.sec.android.app.voicenote.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

public class ShareTaskReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        int scene = VoiceNoteApplication.getScene();
        if (scene == 5) {
            VoiceNoteObservable.getInstance().notifyObservers(7);
        } else if (scene == 10) {
            VoiceNoteObservable.getInstance().notifyObservers(14);
        }
    }
}
