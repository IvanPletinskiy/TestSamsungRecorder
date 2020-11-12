package com.sec.android.app.voicenote.provider;

import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

/* renamed from: com.sec.android.app.voicenote.provider.-$$Lambda$SmartTipsProvider$qI8m4-4K4dWm69zPoCMwknMwHKQ  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$SmartTipsProvider$qI8m44K4dWm69zPoCMwknMwHKQ implements Runnable {
    public static final /* synthetic */ $$Lambda$SmartTipsProvider$qI8m44K4dWm69zPoCMwknMwHKQ INSTANCE = new $$Lambda$SmartTipsProvider$qI8m44K4dWm69zPoCMwknMwHKQ();

    private /* synthetic */ $$Lambda$SmartTipsProvider$qI8m44K4dWm69zPoCMwknMwHKQ() {
    }

    public final void run() {
        VoiceNoteObservable.getInstance().notifyObservers(16);
    }
}
