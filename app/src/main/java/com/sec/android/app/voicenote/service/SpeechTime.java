package com.sec.android.app.voicenote.service;

import com.sec.android.app.voicenote.common.util.SpeechTimeData;
import com.sec.android.app.voicenote.provider.Log;
import java.util.ArrayList;

public class SpeechTime {
    public static final float DEGREE_INTERVIEWEE = 0.0f;
    public static final float DEGREE_INTERVIEWER = 180.0f;
    private static final int MAX_SPEECH_INTERVAL_TIME = 1000;
    private static final int MIN_SPEECH_TIME = 300;
    public static final int MODE_INTERVIEWEE = 1;
    public static final int MODE_INTERVIEWER = 2;
    public static final int MODE_MEETING = 3;
    static final String TAG = "SpeechTime";
    private static final int THRESHOLD_INTERVIEW = 100;
    private static final int THRESHOLD_MEETING = 1000;
    private int[] mAmplitudeArray = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int mAmplitudeArrayIndex = 0;
    private long mElapsedTime = 0;
    private boolean mIsRealtime = false;
    private long mLastEndOfSpeechTime = 0;
    private long mLastStartOfSpeechTime = 0;
    private ArrayList<SpeechTimeData> mSpeechTimeDataArray = new ArrayList<>();
    private int mSpeechTimeDataArrayIndex = 0;
    private long mStartOfSpeechTime = 0;
    private int mThreshold = 0;

    public synchronized void init(int i) {
        this.mSpeechTimeDataArray.clear();
        this.mSpeechTimeDataArrayIndex = 0;
        for (int i2 = 0; i2 < this.mAmplitudeArray.length; i2++) {
            this.mAmplitudeArray[i2] = 0;
        }
        if (i != 1) {
            if (i != 2) {
                if (i == 3) {
                    this.mThreshold = 1000;
                } else {
                    Log.m22e(TAG, "abnormal mode !!");
                }
            }
        }
        this.mThreshold = 100;
    }

    public void setRealTimeMode(boolean z) {
        this.mIsRealtime = z;
    }

    public void setElapsedTime(long j) {
        this.mElapsedTime = j;
    }

    public long getCurrentTime() {
        return this.mIsRealtime ? System.currentTimeMillis() : this.mElapsedTime;
    }

    public ArrayList<SpeechTimeData> getSpeechData() {
        return this.mSpeechTimeDataArray;
    }

    public synchronized void calc(int i, int i2) {
        this.mAmplitudeArrayIndex %= this.mAmplitudeArray.length;
        int[] iArr = this.mAmplitudeArray;
        int i3 = this.mAmplitudeArrayIndex;
        this.mAmplitudeArrayIndex = i3 + 1;
        iArr[i3] = i2;
        int i4 = 0;
        for (int i5 : this.mAmplitudeArray) {
            i4 += i5;
        }
        if (i4 / this.mAmplitudeArray.length > this.mThreshold) {
            if (this.mStartOfSpeechTime == 0) {
                this.mStartOfSpeechTime = getCurrentTime();
            }
        } else if (this.mStartOfSpeechTime > 0) {
            speechDetected(i);
        }
    }

    public synchronized void lastCalc(int i) {
        int i2 = 0;
        for (int i3 : this.mAmplitudeArray) {
            i2 += i3;
        }
        if (i2 / this.mAmplitudeArray.length > this.mThreshold && this.mStartOfSpeechTime > 0) {
            speechDetected(i);
        }
        this.mAmplitudeArrayIndex = 0;
    }

    private void speechDetected(int i) {
        int i2;
        long currentTime = getCurrentTime();
        long j = this.mStartOfSpeechTime;
        int i3 = (int) (currentTime - j);
        if (i3 <= MIN_SPEECH_TIME) {
            return;
        }
        if (j - this.mLastEndOfSpeechTime > 1000 || (i2 = this.mSpeechTimeDataArrayIndex) == 0) {
            this.mLastStartOfSpeechTime = this.mStartOfSpeechTime;
            this.mLastEndOfSpeechTime = currentTime;
            this.mStartOfSpeechTime = 0;
            long j2 = (long) (i - i3);
            ArrayList<SpeechTimeData> arrayList = this.mSpeechTimeDataArray;
            int i4 = this.mSpeechTimeDataArrayIndex;
            this.mSpeechTimeDataArrayIndex = i4 + 1;
            arrayList.add(i4, new SpeechTimeData(j2, i3));
            Log.m26i(TAG, "voice is detected!! rec time - " + j2 + " duration - " + i3);
            return;
        }
        this.mLastEndOfSpeechTime = currentTime;
        this.mStartOfSpeechTime = 0;
        this.mSpeechTimeDataArray.get(i2 - 1).mDuration = (int) (currentTime - this.mLastStartOfSpeechTime);
    }

    public boolean isMute(long j) {
        return this.mStartOfSpeechTime == 0 && System.currentTimeMillis() - this.mLastEndOfSpeechTime > j;
    }
}
