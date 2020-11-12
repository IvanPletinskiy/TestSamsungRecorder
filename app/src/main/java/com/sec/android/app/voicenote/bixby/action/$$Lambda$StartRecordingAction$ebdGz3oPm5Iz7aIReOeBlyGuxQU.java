package com.sec.android.app.voicenote.bixby.action;

import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

/* renamed from: com.sec.android.app.voicenote.bixby.action.-$$Lambda$StartRecordingAction$ebdGz3oPm5Iz7aIReOeBlyGuxQU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$StartRecordingAction$ebdGz3oPm5Iz7aIReOeBlyGuxQU implements Runnable {
    public static final /* synthetic */ $$Lambda$StartRecordingAction$ebdGz3oPm5Iz7aIReOeBlyGuxQU INSTANCE = new $$Lambda$StartRecordingAction$ebdGz3oPm5Iz7aIReOeBlyGuxQU();

    private /* synthetic */ $$Lambda$StartRecordingAction$ebdGz3oPm5Iz7aIReOeBlyGuxQU() {
    }

    public final void run() {
        VoiceNoteObservable.getInstance().notifyObservers(4);
    }
}
