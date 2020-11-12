package com.sec.android.app.voicenote.service;

import com.sec.android.app.voicenote.provider.Log;

public class ContentItem {
    private static final String TAG = "ContentItem";
    private int mDuration;
    private int mEndTime;
    private String mPath;
    private int mStartTime;

    public ContentItem(String str) {
        Log.m19d(TAG, "ContentItem - path : " + str);
        this.mPath = str;
        this.mDuration = -1;
        this.mEndTime = -1;
        this.mStartTime = -1;
    }

    public ContentItem(String str, int i) {
        Log.m19d(TAG, "ContentItem - path : " + str + " eTime : " + i);
        this.mPath = str;
        this.mStartTime = 0;
        this.mEndTime = i;
        this.mDuration = i;
    }

    public ContentItem(String str, int i, int i2) {
        Log.m19d(TAG, "ContentItem - path : " + str + " sTime : " + i + " eTime : " + i2);
        this.mPath = str;
        this.mStartTime = i;
        this.mEndTime = i2;
        this.mDuration = i2 - i;
    }

    public void replacePath(String str) {
        this.mPath = str;
    }

    public int getStartTime() {
        return this.mStartTime;
    }

    public void setStartTime(int i) {
        this.mStartTime = i;
    }

    public int getEndTime() {
        return this.mEndTime;
    }

    public void setEndTime(int i) {
        this.mEndTime = i;
    }

    public String getPath() {
        return this.mPath;
    }

    public void setPath(String str) {
        this.mPath = str;
    }

    public int getDuration() {
        return this.mDuration;
    }

    public void setDuration(int i) {
        this.mDuration = i;
    }
}
