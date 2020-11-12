package com.sec.android.app.voicenote.p007ui;

import com.sec.android.app.voicenote.service.Engine;

/* renamed from: com.sec.android.app.voicenote.ui.-$$Lambda$WaveFragment$2$c3e0KFt_O4u-Bc2ywDar-WsiRKE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$WaveFragment$2$c3e0KFt_O4uBc2ywDarWsiRKE implements Runnable {
    public static final /* synthetic */ $$Lambda$WaveFragment$2$c3e0KFt_O4uBc2ywDarWsiRKE INSTANCE = new $$Lambda$WaveFragment$2$c3e0KFt_O4uBc2ywDarWsiRKE();

    private /* synthetic */ $$Lambda$WaveFragment$2$c3e0KFt_O4uBc2ywDarWsiRKE() {
    }

    public final void run() {
        Engine.getInstance().doNextPlay();
    }
}
