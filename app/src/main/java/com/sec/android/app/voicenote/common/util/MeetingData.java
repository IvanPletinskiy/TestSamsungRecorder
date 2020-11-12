package com.sec.android.app.voicenote.common.util;

import com.sec.android.app.voicenote.provider.Log;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class MeetingData implements Serializable {
    public static final int MAX_PERSON_COUNT = 8;
    private static final int MIN_SPEECH_TIME = 300;
    private static final String TAG = "MeetingData";
    private static final long serialVersionUID = 3392250230415484145L;
    private int mRecordMode;
    private ArrayList<PersonalSpeechTimeData> mSpeechTimeData = new ArrayList<>();

    public MeetingData(int i) {
        this.mRecordMode = i;
    }

    public int getRecordMode() {
        return this.mRecordMode;
    }

    public int trimData(int i, int i2) {
        float[] fArr;
        int i3;
        float f;
        ArrayList arrayList;
        float[] fArr2;
        int i4;
        int i5 = i;
        int i6 = i2;
        float[] degrees = getDegrees();
        ArrayList arrayList2 = new ArrayList();
        int length = degrees.length;
        int i7 = 0;
        while (i7 < length) {
            float f2 = degrees[i7];
            TreeSet<SpeechTimeData> personalSpeechTimeData = getPersonalSpeechTimeData(f2);
            if (personalSpeechTimeData == null) {
                fArr = degrees;
                i3 = i7;
            } else {
                TreeSet treeSet = new TreeSet();
                Iterator<SpeechTimeData> it = personalSpeechTimeData.iterator();
                while (it.hasNext()) {
                    SpeechTimeData next = it.next();
                    long j = next.mStartTime;
                    long j2 = (long) i5;
                    if (j >= j2) {
                        i4 = i7;
                        long j3 = (long) i6;
                        if (j < j3) {
                            fArr2 = degrees;
                            arrayList = arrayList2;
                            if (((long) next.mDuration) + j <= j3) {
                                next.mStartTime = j - j2;
                                treeSet.add(next);
                            } else {
                                next.mDuration = (int) (j3 - j);
                                next.mStartTime = j - j2;
                                treeSet.add(next);
                            }
                            f = f2;
                            i7 = i4;
                            degrees = fArr2;
                            arrayList2 = arrayList;
                            f2 = f;
                        } else {
                            fArr2 = degrees;
                            arrayList = arrayList2;
                        }
                    } else {
                        fArr2 = degrees;
                        arrayList = arrayList2;
                        i4 = i7;
                    }
                    long j4 = next.mStartTime;
                    if (j4 <= j2) {
                        int i8 = next.mDuration;
                        if (((long) i8) + j4 > j2) {
                            f = f2;
                            if (((long) i8) + j4 <= ((long) i6)) {
                                next.mDuration = (int) (((long) i8) - (j2 - j4));
                                next.mStartTime = 0;
                                treeSet.add(next);
                            } else {
                                next.mDuration = i6 - i5;
                                next.mStartTime = 0;
                                treeSet.add(next);
                            }
                            i7 = i4;
                            degrees = fArr2;
                            arrayList2 = arrayList;
                            f2 = f;
                        }
                    }
                    f = f2;
                    i7 = i4;
                    degrees = fArr2;
                    arrayList2 = arrayList;
                    f2 = f;
                }
                fArr = degrees;
                ArrayList arrayList3 = arrayList2;
                i3 = i7;
                float f3 = f2;
                if (!treeSet.isEmpty()) {
                    float f4 = f3;
                    PersonalSpeechTimeData personalSpeechTimeData2 = new PersonalSpeechTimeData((float) ((int) f4), treeSet, getTitle(f4));
                    arrayList2 = arrayList3;
                    arrayList2.add(personalSpeechTimeData2);
                } else {
                    arrayList2 = arrayList3;
                }
            }
            i7 = i3 + 1;
            degrees = fArr;
        }
        if (arrayList2.isEmpty()) {
            return 0;
        }
        setPersonalSpeechTimeData(arrayList2);
        return arrayList2.size();
    }

    public int deleteData(int i, int i2) {
        int i3;
        float[] fArr;
        int i4;
        float f;
        int i5;
        float[] fArr2;
        int i6;
        int i7 = i;
        int i8 = i2;
        float[] degrees = getDegrees();
        ArrayList arrayList = new ArrayList();
        int length = degrees.length;
        int i9 = 0;
        while (i9 < length) {
            float f2 = degrees[i9];
            TreeSet<SpeechTimeData> personalSpeechTimeData = getPersonalSpeechTimeData(f2);
            if (personalSpeechTimeData == null) {
                fArr = degrees;
                i4 = length;
                i3 = i9;
            } else {
                TreeSet treeSet = new TreeSet();
                Iterator<SpeechTimeData> it = personalSpeechTimeData.iterator();
                while (it.hasNext()) {
                    SpeechTimeData next = it.next();
                    long j = next.mStartTime;
                    long j2 = (long) i7;
                    if (j < j2) {
                        i6 = length;
                        if (((long) next.mDuration) + j > j2) {
                            next.mDuration = (int) (j2 - j);
                        }
                        if (next.mDuration > MIN_SPEECH_TIME) {
                            treeSet.add(next);
                        }
                        fArr2 = degrees;
                        i5 = i9;
                        f = f2;
                    } else {
                        i6 = length;
                        long j3 = (long) i8;
                        if (j < j3) {
                            fArr2 = degrees;
                            int i10 = next.mDuration;
                            i5 = i9;
                            f = f2;
                            if (((long) i10) + j > j3) {
                                next.mDuration = (int) ((j + ((long) i10)) - j3);
                                next.mStartTime = j2;
                                if (next.mDuration > MIN_SPEECH_TIME) {
                                    treeSet.add(next);
                                }
                            }
                        } else {
                            fArr2 = degrees;
                            i5 = i9;
                            f = f2;
                            next.mStartTime = j - ((long) (i8 - i7));
                            treeSet.add(next);
                        }
                    }
                    length = i6;
                    degrees = fArr2;
                    i9 = i5;
                    f2 = f;
                }
                fArr = degrees;
                i4 = length;
                i3 = i9;
                float f3 = f2;
                if (!treeSet.isEmpty()) {
                    float f4 = f3;
                    arrayList.add(new PersonalSpeechTimeData((float) ((int) f4), treeSet, getTitle(f4)));
                }
            }
            i9 = i3 + 1;
            length = i4;
            degrees = fArr;
        }
        if (arrayList.isEmpty()) {
            return 0;
        }
        setPersonalSpeechTimeData(arrayList);
        return arrayList.size();
    }

    public int getNumberOfPerson() {
        return this.mSpeechTimeData.size();
    }

    public float[] getDegrees() {
        int size = this.mSpeechTimeData.size();
        float[] fArr = new float[size];
        for (int i = 0; i < size; i++) {
            fArr[i] = this.mSpeechTimeData.get(i).mDegree;
        }
        return fArr;
    }

    public TreeSet<SpeechTimeData> getPersonalSpeechTimeData(float f) {
        Iterator<PersonalSpeechTimeData> it = this.mSpeechTimeData.iterator();
        while (it.hasNext()) {
            PersonalSpeechTimeData next = it.next();
            if (next.mDegree == f) {
                return next.mDataList;
            }
        }
        return null;
    }

    public void addPersonalSpeechTimeData(float f, ArrayList<SpeechTimeData> arrayList, String str) {
        if (this.mSpeechTimeData.size() >= 8) {
            Log.m29v(TAG, "addPersonalSpeechTieData : can not add new person any more");
            return;
        }
        this.mSpeechTimeData.add(new PersonalSpeechTimeData(f, new TreeSet(arrayList), str));
    }

    public void setEnablePerson(float f, boolean z) {
        Iterator<PersonalSpeechTimeData> it = this.mSpeechTimeData.iterator();
        while (it.hasNext()) {
            PersonalSpeechTimeData next = it.next();
            if (next.mDegree == f) {
                next.mEnable = z;
                return;
            }
        }
    }

    public boolean getEnablePerson(float f) {
        Iterator<PersonalSpeechTimeData> it = this.mSpeechTimeData.iterator();
        while (it.hasNext()) {
            PersonalSpeechTimeData next = it.next();
            if (next.mDegree == f) {
                return next.mEnable;
            }
        }
        return false;
    }

    public void setTitle(float f, String str) {
        Iterator<PersonalSpeechTimeData> it = this.mSpeechTimeData.iterator();
        while (it.hasNext()) {
            PersonalSpeechTimeData next = it.next();
            if (next.mDegree == f) {
                next.mTitle = str;
                return;
            }
        }
    }

    public String getTitle(float f) {
        Iterator<PersonalSpeechTimeData> it = this.mSpeechTimeData.iterator();
        while (it.hasNext()) {
            PersonalSpeechTimeData next = it.next();
            if (next.mDegree == f) {
                return next.mTitle;
            }
        }
        return null;
    }

    public void setPersonalSpeechTimeData(ArrayList<PersonalSpeechTimeData> arrayList) {
        this.mSpeechTimeData = arrayList;
    }
}
