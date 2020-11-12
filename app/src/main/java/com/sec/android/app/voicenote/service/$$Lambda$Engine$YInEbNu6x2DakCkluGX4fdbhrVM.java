package com.sec.android.app.voicenote.service;

import java.io.File;
import java.io.FilenameFilter;

/* renamed from: com.sec.android.app.voicenote.service.-$$Lambda$Engine$YInEbNu6x2DakCkluGX4fdbhrVM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$Engine$YInEbNu6x2DakCkluGX4fdbhrVM implements FilenameFilter {
    public static final /* synthetic */ $$Lambda$Engine$YInEbNu6x2DakCkluGX4fdbhrVM INSTANCE = new $$Lambda$Engine$YInEbNu6x2DakCkluGX4fdbhrVM();

    private /* synthetic */ $$Lambda$Engine$YInEbNu6x2DakCkluGX4fdbhrVM() {
    }

    public final boolean accept(File file, String str) {
        return Engine.lambda$restoreTempFile$0(file, str);
    }
}
