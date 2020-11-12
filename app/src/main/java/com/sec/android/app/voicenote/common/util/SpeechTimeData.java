package com.sec.android.app.voicenote.common.util;

import androidx.annotation.NonNull;
import java.io.Serializable;

public class SpeechTimeData implements Serializable, Comparable<SpeechTimeData> {
    private static final long serialVersionUID = 3392250230415484147L;
    public int mDuration;
    public long mStartTime;

    public SpeechTimeData(long j, int i) {
        this.mStartTime = j;
        this.mDuration = i;
    }

    public SpeechTimeData(SpeechTimeData speechTimeData) {
        this.mStartTime = speechTimeData.mStartTime;
        this.mDuration = speechTimeData.mDuration;
    }

    public int compareTo(@NonNull SpeechTimeData speechTimeData) {
        return Long.compare(this.mStartTime, speechTimeData.mStartTime);
    }
}
