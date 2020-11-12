package com.sec.android.app.voicenote.uicore;

import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import java.util.Observable;

public class VoiceNoteObservable extends Observable {
    private static final String TAG = "VoiceNoteObservable";
    private static VoiceNoteObservable mVoiceNoteObservable;

    private VoiceNoteObservable() {
        Log.m26i(TAG, "VoiceNoteObservable creator !!");
    }

    public static VoiceNoteObservable getInstance() {
        if (mVoiceNoteObservable == null) {
            mVoiceNoteObservable = new VoiceNoteObservable();
        }
        return mVoiceNoteObservable;
    }

    public void notifyObservers(Object obj) {
        Log.m26i(TAG, "notifyObservers - data : " + obj + " name : " + Event.getEventName(((Integer) obj).intValue()));
        setChanged();
        super.notifyObservers(obj);
    }
}
