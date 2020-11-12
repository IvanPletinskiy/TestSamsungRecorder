package com.sec.android.app.voicenote.common.util;

import java.io.Serializable;
import java.util.Iterator;
import java.util.TreeSet;

public class PersonalSpeechTimeData implements Serializable {
    private static final long serialVersionUID = 3392250230415484146L;
    public TreeSet<SpeechTimeData> mDataList;
    public float mDegree;
    public boolean mEnable = true;
    public String mTitle;

    public PersonalSpeechTimeData(float f, TreeSet<SpeechTimeData> treeSet, String str) {
        this.mDegree = f;
        this.mDataList = treeSet;
        this.mTitle = str;
    }

    public boolean isEnabled() {
        return this.mEnable;
    }

    public void add(SpeechTimeData speechTimeData, float f) {
        Degree degree = new Degree(f);
        Degree degree2 = new Degree(this.mDegree);
        float value = degree.difference(degree2).getValue();
        Iterator<SpeechTimeData> it = this.mDataList.iterator();
        int i = 0;
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            SpeechTimeData next = it.next();
            i += next.mDuration;
            int checkOverlap = checkOverlap(next, speechTimeData);
            if (checkOverlap > 0) {
                if (checkOverlap == 1) {
                    return;
                }
                if (checkOverlap == 2) {
                    next.mStartTime = speechTimeData.mStartTime;
                    return;
                } else if (checkOverlap == 3) {
                    next.mDuration = (int) ((speechTimeData.mStartTime + ((long) speechTimeData.mDuration)) - next.mStartTime);
                    return;
                } else if (checkOverlap == 4) {
                    next.mStartTime = speechTimeData.mStartTime;
                    next.mDuration = speechTimeData.mDuration;
                    return;
                }
            }
        }
        int i2 = speechTimeData.mDuration;
        degree2.add(value * (((float) i2) / ((float) (i + i2))));
        this.mDataList.add(speechTimeData);
    }

    private int checkOverlap(SpeechTimeData speechTimeData, SpeechTimeData speechTimeData2) {
        long j = speechTimeData.mStartTime;
        long j2 = ((long) speechTimeData.mDuration) + j;
        long j3 = speechTimeData2.mStartTime;
        long j4 = ((long) speechTimeData2.mDuration) + j3;
        int i = (j > j3 ? 1 : (j == j3 ? 0 : -1));
        if (i <= 0 && j2 >= j4) {
            return 1;
        }
        if (i >= 0 && j <= j4 && j2 >= j4) {
            return 2;
        }
        if (i > 0 || j2 < j3 || j2 > j4) {
            return (i < 0 || j2 > j4) ? 0 : 4;
        }
        return 3;
    }
}
