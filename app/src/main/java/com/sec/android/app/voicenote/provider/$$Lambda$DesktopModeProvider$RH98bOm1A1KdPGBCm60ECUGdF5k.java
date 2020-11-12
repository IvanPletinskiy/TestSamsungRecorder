package com.sec.android.app.voicenote.provider;

import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

/* renamed from: com.sec.android.app.voicenote.provider.-$$Lambda$DesktopModeProvider$RH98bOm1A1KdPGBCm60ECUGdF5k  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$DesktopModeProvider$RH98bOm1A1KdPGBCm60ECUGdF5k implements Runnable {
    public static final /* synthetic */ $$Lambda$DesktopModeProvider$RH98bOm1A1KdPGBCm60ECUGdF5k INSTANCE = new $$Lambda$DesktopModeProvider$RH98bOm1A1KdPGBCm60ECUGdF5k();

    private /* synthetic */ $$Lambda$DesktopModeProvider$RH98bOm1A1KdPGBCm60ECUGdF5k() {
    }

    public final void run() {
        VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.RECORD_STOP_BY_DEX_CONNECT));
    }
}
