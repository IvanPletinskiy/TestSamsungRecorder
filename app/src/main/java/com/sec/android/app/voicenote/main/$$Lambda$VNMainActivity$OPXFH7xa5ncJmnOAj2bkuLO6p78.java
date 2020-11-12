package com.sec.android.app.voicenote.main;

import com.sec.android.app.voicenote.provider.CursorProvider;

/* renamed from: com.sec.android.app.voicenote.main.-$$Lambda$VNMainActivity$OPXFH7xa5ncJmnOAj2bkuLO6p78  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$VNMainActivity$OPXFH7xa5ncJmnOAj2bkuLO6p78 implements Runnable {
    public static final /* synthetic */ $$Lambda$VNMainActivity$OPXFH7xa5ncJmnOAj2bkuLO6p78 INSTANCE = new $$Lambda$VNMainActivity$OPXFH7xa5ncJmnOAj2bkuLO6p78();

    private /* synthetic */ $$Lambda$VNMainActivity$OPXFH7xa5ncJmnOAj2bkuLO6p78() {
    }

    public final void run() {
        CursorProvider.getInstance().loadRecordFileCountInBG();
    }
}
