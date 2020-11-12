package com.sec.android.app.voicenote.provider;

import com.sec.android.app.voicenote.uicore.Observable;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;

/* renamed from: com.sec.android.app.voicenote.provider.-$$Lambda$DesktopModeProvider$AxKmLHpxn41muiOIoXycsX4SHXE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$DesktopModeProvider$AxKmLHpxn41muiOIoXycsX4SHXE implements Runnable {
    public static final /* synthetic */ $$Lambda$DesktopModeProvider$AxKmLHpxn41muiOIoXycsX4SHXE INSTANCE = new $$Lambda$DesktopModeProvider$AxKmLHpxn41muiOIoXycsX4SHXE();

    private /* synthetic */ $$Lambda$DesktopModeProvider$AxKmLHpxn41muiOIoXycsX4SHXE() {
    }

    public final void run() {
        Observable.getInstance().notifyObservers(VoiceNoteApplication.getSimpleActivitySession(), Integer.valueOf(Event.SIMPLE_MODE_DONE));
    }
}
