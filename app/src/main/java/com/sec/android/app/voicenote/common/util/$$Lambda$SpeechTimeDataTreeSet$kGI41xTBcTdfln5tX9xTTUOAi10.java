package com.sec.android.app.voicenote.common.util;

import java.util.Comparator;

/* renamed from: com.sec.android.app.voicenote.common.util.-$$Lambda$SpeechTimeDataTreeSet$kGI41xTBcTdfln5tX9xTTUOAi10  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$SpeechTimeDataTreeSet$kGI41xTBcTdfln5tX9xTTUOAi10 implements Comparator {
    public static final /* synthetic */ $$Lambda$SpeechTimeDataTreeSet$kGI41xTBcTdfln5tX9xTTUOAi10 INSTANCE = new $$Lambda$SpeechTimeDataTreeSet$kGI41xTBcTdfln5tX9xTTUOAi10();

    private /* synthetic */ $$Lambda$SpeechTimeDataTreeSet$kGI41xTBcTdfln5tX9xTTUOAi10() {
    }

    public final int compare(Object obj, Object obj2) {
        return Long.compare(((SpeechTimeData) obj).mStartTime, ((SpeechTimeData) obj2).mStartTime);
    }
}
