package com.sec.android.app.voicenote.service;

import android.content.Context;
import com.sec.android.app.voicenote.common.util.MeetingData;
import com.sec.android.app.voicenote.common.util.SpeechTimeData;
import com.sec.android.app.voicenote.common.util.SpeechTimeDataTreeSet;
import com.sec.android.app.voicenote.common.util.TextData;
import com.sec.android.app.voicenote.common.util.VoiceRecorderData;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.AudioFormat;
import com.sec.android.app.voicenote.service.codec.M4aInfo;
import com.sec.android.app.voicenote.service.codec.M4aReader;
import com.sec.android.app.voicenote.service.helper.Bookmark;
import com.sec.android.app.voicenote.service.helper.SttHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

public class SimpleMetadataRepository {
    private static final String BPS_128K = "128000";
    private static final String BPS_256K = "256000";
    private static final String BPS_64K = "64000";
    private static final int NONE = -1;
    private static final String TAG = "SimpleMetadataRepository";
    private static volatile SimpleMetadataRepository mInstance;
    private int[] mAMRWaveData = null;
    private final List<Integer> mAmplitudeCollector = new ArrayList();
    private ArrayList<Bookmark> mBookmarkList = new ArrayList<>();
    private String mCategoryName = null;
    private int mChCount = -1;
    private ArrayList<TextData> mDisplayedSttData;
    private long mDuration = 0;
    private boolean mIsDataChanged = false;
    private int mLastAddedBookmarkTime = -1;
    private int mLastRemovedBookmarkTime = 0;
    private final ArrayList<OnVoiceMetadataListener> mListeners = new ArrayList<>();
    private MeetingData mMeetingData = new MeetingData(2);
    private String mPath;
    private SpeechTimeDataTreeSet mPlaySection = null;
    private int mRecQuality = -1;
    private int mRecordMode = 0;
    private ArrayList<TextData> mSttData = null;
    private VoiceRecorderData mVoiceRecorderData = new VoiceRecorderData();
    private int[] mWaveData = null;
    private boolean mWaveMakerIsWorking = false;

    public interface OnVoiceMetadataListener {
        void onWaveMakerFinished(int i, int i2);
    }

    public SimpleMetadataRepository(Context context, String str) {
        initialize();
    }

    public boolean rename(String str, String str2) {
        if (str2 == null) {
            return false;
        }
        Log.m29v(TAG, "rename - oldPath : " + str + " newPath : " + str2);
        setPath(str2);
        return true;
    }

    private synchronized boolean containsListener(OnVoiceMetadataListener onVoiceMetadataListener) {
        if (onVoiceMetadataListener == null) {
            return false;
        }
        Iterator<OnVoiceMetadataListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            if (onVoiceMetadataListener.equals(it.next())) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0033, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void registerListener(com.sec.android.app.voicenote.service.SimpleMetadataRepository.OnVoiceMetadataListener r4) {
        /*
            r3 = this;
            monitor-enter(r3)
            if (r4 == 0) goto L_0x0032
            boolean r0 = r3.containsListener(r4)     // Catch:{ all -> 0x002f }
            if (r0 == 0) goto L_0x000a
            goto L_0x0032
        L_0x000a:
            java.lang.String r0 = "SimpleMetadataRepository"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x002f }
            r1.<init>()     // Catch:{ all -> 0x002f }
            java.lang.String r2 = "registerListener : "
            r1.append(r2)     // Catch:{ all -> 0x002f }
            java.lang.Class r2 = r4.getClass()     // Catch:{ all -> 0x002f }
            java.lang.String r2 = r2.getSimpleName()     // Catch:{ all -> 0x002f }
            r1.append(r2)     // Catch:{ all -> 0x002f }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x002f }
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r1)     // Catch:{ all -> 0x002f }
            java.util.ArrayList<com.sec.android.app.voicenote.service.SimpleMetadataRepository$OnVoiceMetadataListener> r0 = r3.mListeners     // Catch:{ all -> 0x002f }
            r0.add(r4)     // Catch:{ all -> 0x002f }
            monitor-exit(r3)
            return
        L_0x002f:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        L_0x0032:
            monitor-exit(r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.SimpleMetadataRepository.registerListener(com.sec.android.app.voicenote.service.SimpleMetadataRepository$OnVoiceMetadataListener):void");
    }

    private synchronized void unregisterAllListener() {
        Log.m26i(TAG, "unregisterAllListener");
        this.mListeners.clear();
    }

    private synchronized void notifyObservers(int i, int i2) {
        Log.m29v(TAG, "notifyObservers()");
        Iterator<OnVoiceMetadataListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            OnVoiceMetadataListener next = it.next();
            if (next != null) {
                next.onWaveMakerFinished(i, i2);
            } else {
                it.remove();
            }
        }
    }

    public void setPath(String str) {
        Log.m29v(TAG, "setPath - path : " + str);
        this.mPath = str;
    }

    public String getPath() {
        Log.m19d(TAG, "getPath - title : " + getTitle(this.mPath));
        return this.mPath;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0084, code lost:
        if (r7 != null) goto L_0x0068;
     */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x0326  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean read(java.lang.String r18) {
        /*
            r17 = this;
            r1 = r17
            r2 = r18
            r3 = 0
            java.lang.String r4 = "SimpleMetadataRepository"
            if (r2 == 0) goto L_0x0330
            boolean r0 = r18.isEmpty()
            if (r0 == 0) goto L_0x0011
            goto L_0x0330
        L_0x0011:
            java.io.File r0 = new java.io.File
            r0.<init>(r2)
            boolean r5 = r0.exists()
            if (r5 == 0) goto L_0x032a
            boolean r0 = r0.isDirectory()
            if (r0 == 0) goto L_0x0024
            goto L_0x032a
        L_0x0024:
            r1.mPath = r2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = "read from - "
            r0.append(r5)
            java.lang.String r5 = r1.mPath
            java.lang.String r5 = r1.getTitle((java.lang.String) r5)
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r4, r0)
            com.sec.android.app.voicenote.service.codec.M4aReader r0 = new com.sec.android.app.voicenote.service.codec.M4aReader
            java.lang.String r5 = r1.mPath
            r0.<init>(r5)
            com.sec.android.app.voicenote.service.codec.M4aInfo r5 = r0.readFile()
            r6 = 0
            android.media.MediaMetadataRetriever r7 = new android.media.MediaMetadataRetriever     // Catch:{ Exception -> 0x007b, all -> 0x0077 }
            r7.<init>()     // Catch:{ Exception -> 0x007b, all -> 0x0077 }
            java.lang.String r0 = r1.mPath     // Catch:{ Exception -> 0x0074 }
            r7.setDataSource(r0)     // Catch:{ Exception -> 0x0074 }
            r0 = 9
            java.lang.String r8 = r7.extractMetadata(r0)     // Catch:{ Exception -> 0x0074 }
            r0 = 1022(0x3fe, float:1.432E-42)
            java.lang.String r9 = r7.extractMetadata(r0)     // Catch:{ Exception -> 0x006e }
            r0 = 20
            java.lang.String r6 = r7.extractMetadata(r0)     // Catch:{ Exception -> 0x006c }
        L_0x0068:
            r7.release()
            goto L_0x0087
        L_0x006c:
            r0 = move-exception
            goto L_0x007f
        L_0x006e:
            r0 = move-exception
            r9 = r6
            goto L_0x007f
        L_0x0071:
            r0 = move-exception
            goto L_0x0324
        L_0x0074:
            r0 = move-exception
            r8 = r6
            goto L_0x007e
        L_0x0077:
            r0 = move-exception
            r7 = r6
            goto L_0x0324
        L_0x007b:
            r0 = move-exception
            r7 = r6
            r8 = r7
        L_0x007e:
            r9 = r8
        L_0x007f:
            java.lang.String r10 = "Exception"
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r4, (java.lang.String) r10, (java.lang.Throwable) r0)     // Catch:{ all -> 0x0071 }
            if (r7 == 0) goto L_0x0087
            goto L_0x0068
        L_0x0087:
            if (r8 != 0) goto L_0x008a
            return r3
        L_0x008a:
            android.media.MediaExtractor r7 = new android.media.MediaExtractor
            r7.<init>()
            java.lang.String r0 = r1.mPath     // Catch:{ IOException -> 0x00ab }
            if (r0 == 0) goto L_0x00b3
            java.lang.String r0 = r1.mPath     // Catch:{ IOException -> 0x00ab }
            r7.setDataSource(r0)     // Catch:{ IOException -> 0x00ab }
            int r0 = r7.getTrackCount()     // Catch:{ IOException -> 0x00ab }
            if (r0 <= 0) goto L_0x00b3
            android.media.MediaFormat r0 = r7.getTrackFormat(r3)     // Catch:{ IOException -> 0x00ab }
            java.lang.String r10 = "channel-count"
            int r0 = r0.getInteger(r10)     // Catch:{ IOException -> 0x00ab }
            r1.mChCount = r0     // Catch:{ IOException -> 0x00ab }
            goto L_0x00b3
        L_0x00ab:
            r0 = move-exception
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m22e(r4, r0)
        L_0x00b3:
            r7.release()
            long r7 = java.lang.Long.parseLong(r8)
            r1.mDuration = r7
            java.lang.String r0 = ".amr"
            boolean r0 = r2.contains(r0)
            r7 = 1
            if (r0 != 0) goto L_0x00d7
            java.lang.String r0 = ".3ga"
            boolean r0 = r2.contains(r0)
            if (r0 == 0) goto L_0x00ce
            goto L_0x00d7
        L_0x00ce:
            if (r9 == 0) goto L_0x00d9
            int r0 = java.lang.Integer.parseInt(r9)
            r1.mRecordMode = r0
            goto L_0x00d9
        L_0x00d7:
            r1.mRecordMode = r7
        L_0x00d9:
            int r0 = r1.mRecordMode
            r8 = 4
            if (r0 != 0) goto L_0x00f5
            if (r5 == 0) goto L_0x00f3
            java.util.HashMap<java.lang.String, java.lang.Boolean> r0 = r5.hasCustomAtom
            java.lang.String r9 = "sttd"
            java.lang.Object r0 = r0.get(r9)
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            if (r0 == 0) goto L_0x00f3
            r1.mRecordMode = r8
            goto L_0x00f5
        L_0x00f3:
            r1.mRecordMode = r7
        L_0x00f5:
            int r0 = r1.getRecQualityMode(r6)
            r1.mRecQuality = r0
            com.sec.android.app.voicenote.service.helper.BookmarksHelper r0 = new com.sec.android.app.voicenote.service.helper.BookmarksHelper
            r0.<init>(r5)
            java.util.ArrayList<com.sec.android.app.voicenote.service.helper.Bookmark> r6 = r1.mBookmarkList
            r6.clear()
            java.util.List r6 = r0.getAllBookmarks()
            int r0 = r0.getBookmarksCount()
            if (r0 <= 0) goto L_0x0116
            if (r6 == 0) goto L_0x0116
            java.util.ArrayList<com.sec.android.app.voicenote.service.helper.Bookmark> r0 = r1.mBookmarkList
            r0.addAll(r6)
        L_0x0116:
            com.sec.android.app.voicenote.common.util.MeetingDataHelper r0 = new com.sec.android.app.voicenote.common.util.MeetingDataHelper
            r0.<init>(r5)
            com.sec.android.app.voicenote.common.util.MeetingData r0 = r0.read()
            r1.mMeetingData = r0
            com.sec.android.app.voicenote.common.util.MeetingData r0 = r1.mMeetingData
            com.sec.android.app.voicenote.common.util.SpeechTimeDataTreeSet r0 = r1.parseMeetingData(r0)
            r1.mPlaySection = r0
            com.sec.android.app.voicenote.service.helper.SttHelper r0 = new com.sec.android.app.voicenote.service.helper.SttHelper
            r0.<init>(r5)
            java.util.ArrayList r0 = r0.read()
            r1.mSttData = r0
            com.sec.android.app.voicenote.service.helper.VoiceRecorderDataHelper r0 = new com.sec.android.app.voicenote.service.helper.VoiceRecorderDataHelper
            r0.<init>(r5)
            com.sec.android.app.voicenote.common.util.VoiceRecorderData r0 = r0.read()
            r1.mVoiceRecorderData = r0
            com.sec.android.app.voicenote.common.util.VoiceRecorderData r0 = r1.mVoiceRecorderData
            if (r0 == 0) goto L_0x017e
            java.lang.String r0 = "read - metadata version : 1"
            com.sec.android.app.voicenote.provider.Log.m26i(r4, r0)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r6 = "read - effect name : "
            r0.append(r6)
            com.sec.android.app.voicenote.common.util.VoiceRecorderData r6 = r1.mVoiceRecorderData
            java.lang.String r6 = r6.mEffectName
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r4, r0)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r6 = "read - category name : "
            r0.append(r6)
            com.sec.android.app.voicenote.common.util.VoiceRecorderData r6 = r1.mVoiceRecorderData
            java.lang.String r6 = r6.mCategoryName
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r4, r0)
            com.sec.android.app.voicenote.common.util.VoiceRecorderData r0 = r1.mVoiceRecorderData
            java.lang.String r0 = r0.mCategoryName
            r1.mCategoryName = r0
        L_0x017e:
            com.sec.android.app.voicenote.service.helper.WaveHelper r0 = new com.sec.android.app.voicenote.service.helper.WaveHelper
            r0.<init>(r5)
            int[] r0 = r0.read()
            r1.mWaveData = r0
            int[] r0 = r1.mWaveData
            if (r0 == 0) goto L_0x0267
            int r0 = r0.length
            if (r0 <= 0) goto L_0x0267
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = "read - wave version : "
            r0.append(r5)
            int[] r5 = r1.mWaveData
            r5 = r5[r3]
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r4, r0)
            int[] r0 = r1.mWaveData
            r5 = r0[r3]
            if (r5 != 0) goto L_0x0267
            int r0 = r0.length
            long r5 = r1.mDuration
            float r5 = (float) r5
            r6 = 1065353216(0x3f800000, float:1.0)
            float r5 = r5 * r6
            float r6 = (float) r0
            float r5 = r5 / r6
            r9 = 1108082688(0x420c0000, float:35.0)
            float r10 = r9 / r5
            float r6 = r6 * r5
            float r6 = r6 / r9
            double r11 = (double) r6
            double r11 = java.lang.Math.ceil(r11)
            int r6 = (int) r11
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r11 = "wave NEED to CONVERT "
            r9.append(r11)
            r9.append(r5)
            java.lang.String r5 = "ms to "
            r9.append(r5)
            r5 = 35
            r9.append(r5)
            java.lang.String r5 = "ms"
            r9.append(r5)
            java.lang.String r5 = r9.toString()
            com.sec.android.app.voicenote.provider.Log.m32w((java.lang.String) r4, (java.lang.String) r5)
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r9 = "wave CONVERT - duration : "
            r5.append(r9)
            long r11 = r1.mDuration
            r5.append(r11)
            java.lang.String r9 = " oldSize : "
            r5.append(r9)
            r5.append(r0)
            java.lang.String r9 = " newSize : "
            r5.append(r9)
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r4, r5)
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r9 = "wave CONVERT - numOfWave : "
            r5.append(r9)
            r5.append(r10)
            java.lang.String r5 = r5.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r4, r5)
            int[] r5 = new int[r6]
            r9 = r3
        L_0x0223:
            r11 = 2
            if (r9 >= r6) goto L_0x0253
            float r12 = (float) r9
            float r12 = r12 * r10
            int r12 = (int) r12
            int r13 = r9 + 1
            float r14 = (float) r13
            float r14 = r14 * r10
            int r14 = (int) r14
            int r15 = r0 + -1
            if (r14 <= r15) goto L_0x0233
            r14 = r15
        L_0x0233:
            r16 = r3
            r15 = r12
        L_0x0236:
            if (r15 > r14) goto L_0x0242
            int[] r8 = r1.mWaveData
            r8 = r8[r15]
            int r16 = r16 + r8
            int r15 = r15 + 1
            r8 = 4
            goto L_0x0236
        L_0x0242:
            int r14 = r14 - r12
            int r14 = r14 + r7
            int r16 = r16 / r14
            int r16 = r16 / 2
            r5[r9] = r16
            r8 = r5[r9]
            if (r8 >= 0) goto L_0x0250
            r5[r9] = r3
        L_0x0250:
            r9 = r13
            r8 = 4
            goto L_0x0223
        L_0x0253:
            r5[r3] = r11
            r1.mWaveData = r5
            java.lang.String r0 = "wave - CONVERT COMPLETE"
            com.sec.android.app.voicenote.provider.Log.m26i(r4, r0)
            int r0 = r1.mRecordMode
            if (r0 != r11) goto L_0x0267
            com.sec.android.app.voicenote.common.util.MeetingData r0 = r1.mMeetingData
            if (r0 == 0) goto L_0x0267
            r1.convertInterviewWaveData(r3)
        L_0x0267:
            boolean r0 = isAMR(r18)
            if (r0 == 0) goto L_0x0271
            int[] r0 = r1.mAMRWaveData
            r1.mWaveData = r0
        L_0x0271:
            int[] r0 = r1.mWaveData
            if (r0 != 0) goto L_0x028f
            com.sec.android.app.voicenote.service.helper.WaveMaker r0 = new com.sec.android.app.voicenote.service.helper.WaveMaker
            r0.<init>()
            com.sec.android.app.voicenote.service.-$$Lambda$SimpleMetadataRepository$BNh5CWW_qIXfRbCL30N_UreToik r2 = new com.sec.android.app.voicenote.service.-$$Lambda$SimpleMetadataRepository$BNh5CWW_qIXfRbCL30N_UreToik
            r2.<init>()
            r0.registerListener(r2)
            r1.mWaveMakerIsWorking = r7
            long r2 = r1.mDuration
            float r2 = (float) r2
            r0.setDuration(r2)
            java.lang.String r2 = r1.mPath
            r0.decode(r2)
        L_0x028f:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "read - duration : "
            r0.append(r2)
            long r2 = r1.mDuration
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r4, r0)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "read - mRecordMode : "
            r0.append(r2)
            int r2 = r1.mRecordMode
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r4, r0)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "read - bookmark size : "
            r0.append(r2)
            java.util.ArrayList<com.sec.android.app.voicenote.service.helper.Bookmark> r2 = r1.mBookmarkList
            int r2 = r2.size()
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r4, r0)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "read - stt data : "
            r0.append(r2)
            java.util.ArrayList<com.sec.android.app.voicenote.common.util.TextData> r2 = r1.mSttData
            java.lang.String r3 = "NOT exist"
            if (r2 == 0) goto L_0x02ee
            int r2 = r2.size()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            goto L_0x02ef
        L_0x02ee:
            r2 = r3
        L_0x02ef:
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r4, r0)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "read - wave size : "
            r0.append(r2)
            int[] r2 = r1.mWaveData
            if (r2 == 0) goto L_0x030c
            int r2 = r2.length
            java.lang.Integer r3 = java.lang.Integer.valueOf(r2)
        L_0x030c:
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r4, r0)
            java.util.ArrayList<com.sec.android.app.voicenote.common.util.TextData> r0 = r1.mSttData
            if (r0 == 0) goto L_0x0323
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0323
            r0 = 4
            r1.mRecordMode = r0
        L_0x0323:
            return r7
        L_0x0324:
            if (r7 == 0) goto L_0x0329
            r7.release()
        L_0x0329:
            throw r0
        L_0x032a:
            java.lang.String r0 = "read - file is not exist or directory"
            com.sec.android.app.voicenote.provider.Log.m22e(r4, r0)
            return r3
        L_0x0330:
            java.lang.String r0 = "read - file is not exist"
            com.sec.android.app.voicenote.provider.Log.m22e(r4, r0)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.SimpleMetadataRepository.read(java.lang.String):boolean");
    }

    public /* synthetic */ void lambda$read$0$SimpleMetadataRepository(String str, int[] iArr) {
        Log.m19d(TAG, "WaveMaker path:" + str + ", size:" + iArr.length);
        if (str.equals(this.mPath)) {
            this.mWaveData = iArr;
            if (this.mRecordMode == 2 && this.mMeetingData != null) {
                convertInterviewWaveData(true);
            }
            this.mWaveMakerIsWorking = false;
            notifyObservers(0, this.mWaveData.length);
            this.mIsDataChanged = true;
        }
        unregisterAllListener();
    }

    public synchronized void initialize() {
        Log.m26i(TAG, "initialize");
        this.mBookmarkList.clear();
        this.mAmplitudeCollector.clear();
        clearSttData();
        this.mWaveData = null;
        this.mMeetingData = null;
        this.mPlaySection = null;
        this.mPath = null;
        this.mRecordMode = 0;
        this.mWaveMakerIsWorking = false;
        this.mDuration = 0;
        this.mIsDataChanged = false;
    }

    public String getTitle() {
        Log.m26i(TAG, "updateTitle");
        String str = this.mPath;
        if (str == null) {
            return null;
        }
        String[] split = str.split("/");
        String str2 = split[split.length - 1];
        if (str2 != null) {
            return str2.lastIndexOf(46) > 0 ? str2.substring(0, str2.lastIndexOf(46)) : str2;
        }
        Log.m26i(TAG, "updateTitle - filename is null");
        return null;
    }

    public String getTitle(float f) {
        MeetingData meetingData = this.mMeetingData;
        if (meetingData == null) {
            Log.m26i(TAG, "getTitle - degree : " + f + " mMeetingData is null");
            return "";
        }
        String title = meetingData.getTitle(f);
        Log.m26i(TAG, "getTitle - degree : " + f + " title : " + title);
        return title;
    }

    public boolean isWaveMakerWorking() {
        return this.mWaveMakerIsWorking;
    }

    private void createMeetingData() {
        SpeechTime speechTime = new SpeechTime();
        SpeechTime speechTime2 = new SpeechTime();
        speechTime.init(1);
        int i = 2;
        speechTime2.init(2);
        speechTime.setRealTimeMode(false);
        speechTime2.setRealTimeMode(false);
        if (this.mRecordMode == 2) {
            int i2 = 0;
            for (int i3 : this.mWaveData) {
                speechTime.calc(i2, i3 >> 16);
                speechTime2.calc(i2, 65535 & i3);
                i2 += 35;
                long j = (long) i2;
                speechTime.setElapsedTime(j);
                speechTime2.setElapsedTime(j);
            }
            speechTime.lastCalc(i2);
            speechTime2.lastCalc(i2);
            MeetingData meetingData = new MeetingData(this.mRecordMode);
            if (!speechTime.getSpeechData().isEmpty()) {
                meetingData.addPersonalSpeechTimeData(0.0f, speechTime.getSpeechData(), "Voice " + 1);
                Log.m29v(TAG, "createMeetingData - up is added");
            } else {
                i = 1;
            }
            if (!speechTime2.getSpeechData().isEmpty()) {
                meetingData.addPersonalSpeechTimeData(180.0f, speechTime2.getSpeechData(), "Voice " + i);
                Log.m29v(TAG, "createMeetingData - down is added");
            }
            if (meetingData.getNumberOfPerson() > 0) {
                setMeetingData(meetingData);
                Log.m29v(TAG, "createMeetingData - data is exist");
            } else {
                setMeetingData((MeetingData) null);
                Log.m29v(TAG, "createMeetingData - data is empty");
            }
            this.mIsDataChanged = true;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0039, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void write(java.lang.String r6) {
        /*
            r5 = this;
            monitor-enter(r5)
            if (r6 != 0) goto L_0x000c
            java.lang.String r6 = "SimpleMetadataRepository"
            java.lang.String r0 = "write - path is null"
            com.sec.android.app.voicenote.provider.Log.m22e(r6, r0)     // Catch:{ all -> 0x01aa }
            monitor-exit(r5)
            return
        L_0x000c:
            java.io.File r0 = new java.io.File     // Catch:{ all -> 0x01aa }
            r0.<init>(r6)     // Catch:{ all -> 0x01aa }
            boolean r0 = r0.exists()     // Catch:{ all -> 0x01aa }
            if (r0 != 0) goto L_0x003a
            java.lang.String r0 = "SimpleMetadataRepository"
            java.lang.String r1 = "write - file not exist"
            com.sec.android.app.voicenote.provider.Log.m22e(r0, r1)     // Catch:{ all -> 0x01aa }
            boolean r0 = com.sec.android.app.voicenote.provider.Log.ENG     // Catch:{ all -> 0x01aa }
            if (r0 == 0) goto L_0x0038
            java.lang.String r0 = "SimpleMetadataRepository"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x01aa }
            r1.<init>()     // Catch:{ all -> 0x01aa }
            java.lang.String r2 = "write - file not exist - path : "
            r1.append(r2)     // Catch:{ all -> 0x01aa }
            r1.append(r6)     // Catch:{ all -> 0x01aa }
            java.lang.String r6 = r1.toString()     // Catch:{ all -> 0x01aa }
            com.sec.android.app.voicenote.provider.Log.m22e(r0, r6)     // Catch:{ all -> 0x01aa }
        L_0x0038:
            monitor-exit(r5)
            return
        L_0x003a:
            java.lang.String r0 = "SimpleMetadataRepository"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x01aa }
            r1.<init>()     // Catch:{ all -> 0x01aa }
            java.lang.String r2 = "write to - "
            r1.append(r2)     // Catch:{ all -> 0x01aa }
            java.lang.String r2 = r5.getTitle((java.lang.String) r6)     // Catch:{ all -> 0x01aa }
            r1.append(r2)     // Catch:{ all -> 0x01aa }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x01aa }
            com.sec.android.app.voicenote.provider.Log.m19d(r0, r1)     // Catch:{ all -> 0x01aa }
            boolean r0 = r5.mIsDataChanged     // Catch:{ all -> 0x01aa }
            if (r0 == 0) goto L_0x01a6
            java.lang.String r0 = "SimpleMetadataRepository"
            java.lang.String r1 = "write - need to be updated"
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r1)     // Catch:{ all -> 0x01aa }
            com.sec.android.app.voicenote.service.codec.M4aReader r0 = new com.sec.android.app.voicenote.service.codec.M4aReader     // Catch:{ all -> 0x01aa }
            r0.<init>(r6)     // Catch:{ all -> 0x01aa }
            java.util.ArrayList<com.sec.android.app.voicenote.service.helper.Bookmark> r1 = r5.mBookmarkList     // Catch:{ all -> 0x01aa }
            r2 = 0
            if (r1 == 0) goto L_0x00b3
            java.lang.String r1 = "SimpleMetadataRepository"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x01aa }
            r3.<init>()     // Catch:{ all -> 0x01aa }
            java.lang.String r4 = "write bookmark info - size : "
            r3.append(r4)     // Catch:{ all -> 0x01aa }
            java.util.ArrayList<com.sec.android.app.voicenote.service.helper.Bookmark> r4 = r5.mBookmarkList     // Catch:{ all -> 0x01aa }
            int r4 = r4.size()     // Catch:{ all -> 0x01aa }
            r3.append(r4)     // Catch:{ all -> 0x01aa }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x01aa }
            com.sec.android.app.voicenote.provider.Log.m26i(r1, r3)     // Catch:{ all -> 0x01aa }
            com.sec.android.app.voicenote.service.codec.M4aInfo r1 = r0.readFile()     // Catch:{ all -> 0x01aa }
            if (r1 != 0) goto L_0x0094
            java.lang.String r6 = "SimpleMetadataRepository"
            java.lang.String r0 = "write info is null !!!"
            com.sec.android.app.voicenote.provider.Log.m26i(r6, r0)     // Catch:{ all -> 0x01aa }
            monitor-exit(r5)
            return
        L_0x0094:
            com.sec.android.app.voicenote.service.helper.BookmarksHelper r3 = new com.sec.android.app.voicenote.service.helper.BookmarksHelper     // Catch:{ all -> 0x01aa }
            r3.<init>(r1)     // Catch:{ all -> 0x01aa }
            java.util.ArrayList<com.sec.android.app.voicenote.service.helper.Bookmark> r1 = r5.mBookmarkList     // Catch:{ all -> 0x01aa }
            java.util.Collections.sort(r1)     // Catch:{ all -> 0x01aa }
            java.util.ArrayList<com.sec.android.app.voicenote.service.helper.Bookmark> r1 = r5.mBookmarkList     // Catch:{ all -> 0x01aa }
            r3.overwrite(r1)     // Catch:{ all -> 0x01aa }
            com.sec.android.app.voicenote.service.BookmarkHolder r1 = com.sec.android.app.voicenote.service.BookmarkHolder.getInstance()     // Catch:{ all -> 0x01aa }
            int r3 = r3.getBookmarksCount()     // Catch:{ all -> 0x01aa }
            if (r3 <= 0) goto L_0x00af
            r3 = 1
            goto L_0x00b0
        L_0x00af:
            r3 = r2
        L_0x00b0:
            r1.set(r6, r3)     // Catch:{ all -> 0x01aa }
        L_0x00b3:
            int[] r1 = r5.mWaveData     // Catch:{ all -> 0x01aa }
            if (r1 == 0) goto L_0x00ec
            java.lang.String r1 = "SimpleMetadataRepository"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x01aa }
            r3.<init>()     // Catch:{ all -> 0x01aa }
            java.lang.String r4 = "write amplitude data length : "
            r3.append(r4)     // Catch:{ all -> 0x01aa }
            int[] r4 = r5.mWaveData     // Catch:{ all -> 0x01aa }
            int r4 = r4.length     // Catch:{ all -> 0x01aa }
            r3.append(r4)     // Catch:{ all -> 0x01aa }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x01aa }
            com.sec.android.app.voicenote.provider.Log.m26i(r1, r3)     // Catch:{ all -> 0x01aa }
            com.sec.android.app.voicenote.service.codec.M4aInfo r1 = r0.readFile()     // Catch:{ all -> 0x01aa }
            if (r1 != 0) goto L_0x00df
            java.lang.String r6 = "SimpleMetadataRepository"
            java.lang.String r0 = "write info is null !!!"
            com.sec.android.app.voicenote.provider.Log.m26i(r6, r0)     // Catch:{ all -> 0x01aa }
            monitor-exit(r5)
            return
        L_0x00df:
            r5.createMeetingData()     // Catch:{ all -> 0x01aa }
            com.sec.android.app.voicenote.service.helper.WaveHelper r3 = new com.sec.android.app.voicenote.service.helper.WaveHelper     // Catch:{ all -> 0x01aa }
            r3.<init>(r1)     // Catch:{ all -> 0x01aa }
            int[] r1 = r5.mWaveData     // Catch:{ all -> 0x01aa }
            r3.overwrite(r1)     // Catch:{ all -> 0x01aa }
        L_0x00ec:
            com.sec.android.app.voicenote.common.util.MeetingData r1 = r5.mMeetingData     // Catch:{ all -> 0x01aa }
            if (r1 == 0) goto L_0x0110
            java.lang.String r1 = "SimpleMetadataRepository"
            java.lang.String r3 = "write meeting data"
            com.sec.android.app.voicenote.provider.Log.m26i(r1, r3)     // Catch:{ all -> 0x01aa }
            com.sec.android.app.voicenote.service.codec.M4aInfo r1 = r0.readFile()     // Catch:{ all -> 0x01aa }
            if (r1 != 0) goto L_0x0106
            java.lang.String r6 = "SimpleMetadataRepository"
            java.lang.String r0 = "write info is null !!!"
            com.sec.android.app.voicenote.provider.Log.m26i(r6, r0)     // Catch:{ all -> 0x01aa }
            monitor-exit(r5)
            return
        L_0x0106:
            com.sec.android.app.voicenote.common.util.MeetingDataHelper r3 = new com.sec.android.app.voicenote.common.util.MeetingDataHelper     // Catch:{ all -> 0x01aa }
            r3.<init>(r1)     // Catch:{ all -> 0x01aa }
            com.sec.android.app.voicenote.common.util.MeetingData r1 = r5.mMeetingData     // Catch:{ all -> 0x01aa }
            r3.overwrite(r1)     // Catch:{ all -> 0x01aa }
        L_0x0110:
            r1 = 0
            java.util.ArrayList<com.sec.android.app.voicenote.common.util.TextData> r3 = r5.mSttData     // Catch:{ all -> 0x01aa }
            if (r3 != 0) goto L_0x0121
            java.util.ArrayList<com.sec.android.app.voicenote.common.util.TextData> r3 = r5.mDisplayedSttData     // Catch:{ all -> 0x01aa }
            if (r3 == 0) goto L_0x0121
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x01aa }
            java.util.ArrayList<com.sec.android.app.voicenote.common.util.TextData> r3 = r5.mDisplayedSttData     // Catch:{ all -> 0x01aa }
            r1.<init>(r3)     // Catch:{ all -> 0x01aa }
            goto L_0x012c
        L_0x0121:
            java.util.ArrayList<com.sec.android.app.voicenote.common.util.TextData> r3 = r5.mSttData     // Catch:{ all -> 0x01aa }
            if (r3 == 0) goto L_0x012c
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x01aa }
            java.util.ArrayList<com.sec.android.app.voicenote.common.util.TextData> r3 = r5.mSttData     // Catch:{ all -> 0x01aa }
            r1.<init>(r3)     // Catch:{ all -> 0x01aa }
        L_0x012c:
            if (r1 == 0) goto L_0x014c
            java.lang.String r3 = "SimpleMetadataRepository"
            java.lang.String r4 = "write stt data"
            com.sec.android.app.voicenote.provider.Log.m26i(r3, r4)     // Catch:{ all -> 0x01aa }
            com.sec.android.app.voicenote.service.codec.M4aInfo r3 = r0.readFile()     // Catch:{ all -> 0x01aa }
            if (r3 != 0) goto L_0x0144
            java.lang.String r6 = "SimpleMetadataRepository"
            java.lang.String r0 = "write info is null !!!"
            com.sec.android.app.voicenote.provider.Log.m26i(r6, r0)     // Catch:{ all -> 0x01aa }
            monitor-exit(r5)
            return
        L_0x0144:
            com.sec.android.app.voicenote.service.helper.SttHelper r4 = new com.sec.android.app.voicenote.service.helper.SttHelper     // Catch:{ all -> 0x01aa }
            r4.<init>(r3)     // Catch:{ all -> 0x01aa }
            r4.overwrite(r1)     // Catch:{ all -> 0x01aa }
        L_0x014c:
            int r1 = r5.mRecordMode     // Catch:{ all -> 0x01aa }
            if (r1 == 0) goto L_0x0181
            java.lang.String r1 = "SimpleMetadataRepository"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x01aa }
            r3.<init>()     // Catch:{ all -> 0x01aa }
            java.lang.String r4 = "write recordmode data : "
            r3.append(r4)     // Catch:{ all -> 0x01aa }
            int r4 = r5.mRecordMode     // Catch:{ all -> 0x01aa }
            r3.append(r4)     // Catch:{ all -> 0x01aa }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x01aa }
            com.sec.android.app.voicenote.provider.Log.m26i(r1, r3)     // Catch:{ all -> 0x01aa }
            com.sec.android.app.voicenote.service.codec.M4aInfo r1 = r0.readFile()     // Catch:{ all -> 0x01aa }
            if (r1 != 0) goto L_0x0177
            java.lang.String r6 = "SimpleMetadataRepository"
            java.lang.String r0 = "write info is null !!!"
            com.sec.android.app.voicenote.provider.Log.m26i(r6, r0)     // Catch:{ all -> 0x01aa }
            monitor-exit(r5)
            return
        L_0x0177:
            com.sec.android.app.voicenote.service.helper.SmtaHelper r3 = new com.sec.android.app.voicenote.service.helper.SmtaHelper     // Catch:{ all -> 0x01aa }
            r3.<init>(r1)     // Catch:{ all -> 0x01aa }
            int r1 = r5.mRecordMode     // Catch:{ all -> 0x01aa }
            r3.overwrite(r1)     // Catch:{ all -> 0x01aa }
        L_0x0181:
            com.sec.android.app.voicenote.service.codec.M4aInfo r0 = r0.readFile()     // Catch:{ all -> 0x01aa }
            if (r0 != 0) goto L_0x0190
            java.lang.String r6 = "SimpleMetadataRepository"
            java.lang.String r0 = "write info is null !!!"
            com.sec.android.app.voicenote.provider.Log.m26i(r6, r0)     // Catch:{ all -> 0x01aa }
            monitor-exit(r5)
            return
        L_0x0190:
            com.sec.android.app.voicenote.common.util.VoiceRecorderData r1 = r5.mVoiceRecorderData     // Catch:{ all -> 0x01aa }
            if (r1 == 0) goto L_0x01a4
            com.sec.android.app.voicenote.service.helper.VoiceRecorderDataHelper r1 = new com.sec.android.app.voicenote.service.helper.VoiceRecorderDataHelper     // Catch:{ all -> 0x01aa }
            r1.<init>(r0)     // Catch:{ all -> 0x01aa }
            com.sec.android.app.voicenote.common.util.VoiceRecorderData r0 = r5.mVoiceRecorderData     // Catch:{ all -> 0x01aa }
            java.lang.String r3 = r5.mCategoryName     // Catch:{ all -> 0x01aa }
            r0.mCategoryName = r3     // Catch:{ all -> 0x01aa }
            com.sec.android.app.voicenote.common.util.VoiceRecorderData r0 = r5.mVoiceRecorderData     // Catch:{ all -> 0x01aa }
            r1.overwrite(r0)     // Catch:{ all -> 0x01aa }
        L_0x01a4:
            r5.mIsDataChanged = r2     // Catch:{ all -> 0x01aa }
        L_0x01a6:
            r5.mPath = r6     // Catch:{ all -> 0x01aa }
            monitor-exit(r5)
            return
        L_0x01aa:
            r6 = move-exception
            monitor-exit(r5)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.SimpleMetadataRepository.write(java.lang.String):void");
    }

    public void setDisplayedSttData(ArrayList<TextData> arrayList) {
        Log.m26i(TAG, "setDisplayedSttData");
        if (this.mDisplayedSttData == null) {
            this.mDisplayedSttData = new ArrayList<>();
        }
        if (arrayList != null) {
            this.mDisplayedSttData.clear();
            this.mDisplayedSttData.addAll(arrayList);
        }
    }

    public void close() {
        Log.m26i(TAG, "close !!");
        initialize();
    }

    /* access modifiers changed from: package-private */
    public void addAmplitudeData(int i) {
        if (i == -1) {
            return;
        }
        if (this.mAmplitudeCollector.isEmpty()) {
            this.mAmplitudeCollector.add(2);
        } else {
            this.mAmplitudeCollector.add(Integer.valueOf(i));
        }
    }

    public int[] getWaveData() {
        return this.mWaveData;
    }

    public static boolean isAMR(String str) {
        return str != null && str.toLowerCase(Locale.US).endsWith(AudioFormat.ExtType.EXT_AMR);
    }

    public int getWaveDataSize() {
        int[] iArr = this.mWaveData;
        if (iArr == null) {
            return 0;
        }
        return iArr.length;
    }

    public long getDuration() {
        return this.mDuration;
    }

    public int[] getOverWriteWaveData(int i, int i2) {
        int[] iArr;
        Log.m26i(TAG, "getOverWriteWaveData - recordStartTime : " + i + " recordEndTime : " + i2);
        if (this.mWaveData == null) {
            return null;
        }
        Log.m26i(TAG, "        mWaveData.length:" + this.mWaveData.length + " mAmplitudeCollector.size:" + getAmplitudeCollectorSize());
        int[] iArr2 = this.mWaveData;
        int i3 = i2 / 35;
        if (iArr2.length < i3) {
            iArr = Arrays.copyOf(iArr2, i3);
        } else {
            iArr = Arrays.copyOf(iArr2, iArr2.length);
        }
        int i4 = i / 35;
        Iterator<Integer> it = this.mAmplitudeCollector.iterator();
        while (it.hasNext() && i4 < iArr.length) {
            iArr[i4] = it.next().intValue();
            i4++;
        }
        return iArr;
    }

    /* access modifiers changed from: package-private */
    public void initAmplitude() {
        Log.m26i(TAG, "initAmplitude");
        this.mAmplitudeCollector.clear();
    }

    /* access modifiers changed from: package-private */
    public void stopAmplitude(int i, int i2) {
        int i3;
        Log.m26i(TAG, "stopAmplitude - recordStartTime : " + i + " recordEndTime : " + i2);
        if (i < 0 || i > i2) {
            Log.m22e(TAG, "  input arguments invalid");
            return;
        }
        int[] iArr = this.mWaveData;
        if (iArr == null || iArr.length == 0) {
            this.mWaveData = new int[this.mAmplitudeCollector.size()];
            i3 = 0;
        } else {
            int i4 = i2 / 35;
            if (iArr.length < i4) {
                this.mWaveData = Arrays.copyOf(iArr, i4);
            }
            i3 = i / 35;
        }
        for (Integer intValue : this.mAmplitudeCollector) {
            int[] iArr2 = this.mWaveData;
            if (i3 >= iArr2.length) {
                break;
            }
            iArr2[i3] = intValue.intValue();
            i3++;
        }
        if (isAMR(this.mPath)) {
            this.mAMRWaveData = this.mWaveData;
        }
        Log.m26i(TAG, "              - mWaveData.length : " + this.mWaveData.length + " i : " + i3);
        initAmplitude();
        this.mIsDataChanged = true;
    }

    public void setRecordMode(int i) {
        Log.m26i(TAG, "setRecordMode : " + i);
        this.mRecordMode = i;
    }

    public int getRecordMode() {
        Log.m26i(TAG, "getRecordMode : " + this.mRecordMode);
        return this.mRecordMode;
    }

    /* access modifiers changed from: package-private */
    public void setRecQuality(int i) {
        Log.m26i(TAG, "setRecQuality : " + i);
        this.mRecQuality = i;
    }

    /* access modifiers changed from: package-private */
    public int getRecQuality() {
        Log.m26i(TAG, "getRecQuality : " + this.mRecQuality);
        return this.mRecQuality;
    }

    /* access modifiers changed from: package-private */
    public void setRecChCount(int i) {
        Log.m26i(TAG, "setRecChCount : " + i);
        this.mChCount = i;
    }

    /* access modifiers changed from: package-private */
    public int getRecChCount() {
        Log.m26i(TAG, "getRecChCount : " + this.mChCount);
        return this.mChCount;
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x0043  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x004c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getRecQualityMode(java.lang.String r7) {
        /*
            r6 = this;
            r0 = -1
            if (r7 != 0) goto L_0x000b
            java.lang.String r7 = "SimpleMetadataRepository"
            java.lang.String r1 = "getRecQualityMode str is null"
            com.sec.android.app.voicenote.provider.Log.m19d(r7, r1)
            return r0
        L_0x000b:
            int r1 = r7.hashCode()
            r2 = 51466930(0x31152b2, float:4.27066E-37)
            r3 = 0
            r4 = 2
            r5 = 1
            if (r1 == r2) goto L_0x0036
            r2 = 1450720409(0x56783c99, float:6.8234787E13)
            if (r1 == r2) goto L_0x002c
            r2 = 1482060541(0x585672fd, float:9.4315744E14)
            if (r1 == r2) goto L_0x0022
            goto L_0x0040
        L_0x0022:
            java.lang.String r1 = "256000"
            boolean r7 = r7.equals(r1)
            if (r7 == 0) goto L_0x0040
            r7 = r4
            goto L_0x0041
        L_0x002c:
            java.lang.String r1 = "128000"
            boolean r7 = r7.equals(r1)
            if (r7 == 0) goto L_0x0040
            r7 = r5
            goto L_0x0041
        L_0x0036:
            java.lang.String r1 = "64000"
            boolean r7 = r7.equals(r1)
            if (r7 == 0) goto L_0x0040
            r7 = r3
            goto L_0x0041
        L_0x0040:
            r7 = r0
        L_0x0041:
            if (r7 == 0) goto L_0x004c
            if (r7 == r5) goto L_0x004a
            if (r7 == r4) goto L_0x0048
            goto L_0x004d
        L_0x0048:
            r0 = r4
            goto L_0x004d
        L_0x004a:
            r0 = r5
            goto L_0x004d
        L_0x004c:
            r0 = r3
        L_0x004d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.SimpleMetadataRepository.getRecQualityMode(java.lang.String):int");
    }

    /* access modifiers changed from: package-private */
    public SpeechTimeDataTreeSet getPlaySection() {
        return this.mPlaySection;
    }

    public void enablePersonal(float f, boolean z) {
        if (this.mMeetingData != null) {
            Log.m29v(TAG, "enablePersonal - degree : " + f + " enable : " + z);
            this.mMeetingData.setEnablePerson(f, z);
            this.mPlaySection = parseMeetingData(this.mMeetingData);
        }
    }

    public boolean isEnabledPerson(float f) {
        MeetingData meetingData = this.mMeetingData;
        return meetingData != null && meetingData.getEnablePerson(f);
    }

    public boolean isExistedPerson(float f) {
        MeetingData meetingData = this.mMeetingData;
        if (meetingData == null) {
            return false;
        }
        for (float f2 : meetingData.getDegrees()) {
            if (f2 == f) {
                return true;
            }
        }
        return false;
    }

    private SpeechTimeDataTreeSet parseMeetingData(MeetingData meetingData) {
        TreeSet<SpeechTimeData> personalSpeechTimeData;
        if (meetingData == null) {
            Log.m26i(TAG, "meetingData is null !!");
            return null;
        }
        SpeechTimeDataTreeSet speechTimeDataTreeSet = new SpeechTimeDataTreeSet();
        int numberOfPerson = meetingData.getNumberOfPerson();
        for (int i = 0; i < numberOfPerson; i++) {
            float f = meetingData.getDegrees()[i];
            if (meetingData.getEnablePerson(f) && (personalSpeechTimeData = meetingData.getPersonalSpeechTimeData(f)) != null) {
                speechTimeDataTreeSet.addAll(personalSpeechTimeData);
            }
        }
        return speechTimeDataTreeSet;
    }

    private void convertInterviewWaveData(boolean z) {
        Log.m32w(TAG, "wave NEED to CONVERT INTERVIEW - fromWaveMaker : " + z);
        TreeSet<SpeechTimeData> personalSpeechTimeData = this.mMeetingData.getPersonalSpeechTimeData(0.0f);
        if (personalSpeechTimeData != null) {
            Iterator<SpeechTimeData> it = personalSpeechTimeData.iterator();
            while (it.hasNext()) {
                SpeechTimeData next = it.next();
                Log.m19d(TAG, "wave MeetingData up " + next.mStartTime + '~' + (next.mStartTime + ((long) next.mDuration)));
                long j = next.mStartTime;
                int i = (int) (j / 35);
                int i2 = (int) ((j + ((long) next.mDuration)) / 35);
                if (i < 0) {
                    i = 0;
                }
                if (i2 < 0) {
                    i2 = 0;
                }
                if (i2 > this.mWaveData.length) {
                    Log.m32w(TAG, "convertInterviewWaveData - speech data size " + i2 + "is lager than wave data size " + this.mWaveData.length);
                    i2 = this.mWaveData.length;
                }
                while (i < i2) {
                    if (z) {
                        int[] iArr = this.mWaveData;
                        iArr[i] = (iArr[i] / 2) << 16;
                    } else {
                        int[] iArr2 = this.mWaveData;
                        iArr2[i] = iArr2[i] << 16;
                    }
                    i++;
                }
            }
        }
        TreeSet<SpeechTimeData> personalSpeechTimeData2 = this.mMeetingData.getPersonalSpeechTimeData(180.0f);
        if (personalSpeechTimeData2 != null) {
            Iterator<SpeechTimeData> it2 = personalSpeechTimeData2.iterator();
            while (it2.hasNext()) {
                SpeechTimeData next2 = it2.next();
                Log.m19d(TAG, "wave MeetingData down " + next2.mStartTime + '~' + (next2.mStartTime + ((long) next2.mDuration)));
                long j2 = next2.mStartTime;
                int i3 = (int) (j2 / 35);
                int i4 = (int) ((j2 + ((long) next2.mDuration)) / 35);
                if (i3 < 0) {
                    i3 = 0;
                }
                if (i4 < 0) {
                    i4 = 0;
                }
                if (i4 > this.mWaveData.length) {
                    Log.m32w(TAG, "convertInterviewWaveData - speech data size " + i4 + "is lager than wave data size " + this.mWaveData.length);
                    i4 = this.mWaveData.length;
                }
                while (i3 < i4) {
                    int[] iArr3 = this.mWaveData;
                    if ((iArr3[i3] >> 16) > 0) {
                        iArr3[i3] = (iArr3[i3] << 16) + iArr3[i3];
                    }
                    i3++;
                }
            }
        }
        this.mIsDataChanged = true;
        Log.m26i(TAG, "wave INTERVIEW COMPLETE");
    }

    public void trim(int i, int i2) {
        Log.m26i(TAG, "trim - fromTime : " + i + " toTime : " + i2);
        if (this.mBookmarkList != null) {
            trimBookmark(i, i2);
        }
        MeetingData meetingData = this.mMeetingData;
        if (meetingData != null) {
            meetingData.trimData(i, i2);
        }
        if (this.mSttData != null) {
            trimStt(i, i2);
        }
        int[] iArr = this.mWaveData;
        if (iArr == null) {
            Log.m22e(TAG, "mWaveData is null !!!");
            return;
        }
        int i3 = i / 35;
        int i4 = i2 / 35;
        try {
            int i5 = iArr[0];
            this.mWaveData = Arrays.copyOfRange(iArr, i3, i4);
            this.mWaveData[0] = i5;
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.m24e(TAG, "ArrayIndexOutOfBoundsException", (Throwable) e);
            Log.m26i(TAG, "mWaveData size : " + this.mWaveData.length + " fromIndex : " + i3 + " toIndex : " + i4);
        }
        this.mIsDataChanged = true;
    }

    public void delete(int i, int i2) {
        Log.m26i(TAG, "delete - fromTime : " + i + " toTime : " + i2);
        if (this.mBookmarkList != null) {
            deleteBookmark(i, i2);
        }
        MeetingData meetingData = this.mMeetingData;
        if (meetingData != null) {
            meetingData.deleteData(i, i2);
        }
        if (this.mSttData != null) {
            deleteStt(i, i2);
        }
        int[] iArr = this.mWaveData;
        if (iArr == null) {
            Log.m22e(TAG, "mWaveData is null !!!");
            return;
        }
        int i3 = i / 35;
        int i4 = i2 / 35;
        if (i4 > iArr.length) {
            i4 = iArr.length;
        }
        try {
            int i5 = this.mWaveData[0];
            int[] iArr2 = new int[((this.mWaveData.length - i4) + i3)];
            System.arraycopy(this.mWaveData, 0, iArr2, 0, i3);
            System.arraycopy(this.mWaveData, i4, iArr2, i3, this.mWaveData.length - i4);
            this.mWaveData = iArr2;
            this.mWaveData[0] = i5;
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.m24e(TAG, "ArrayIndexOutOfBoundsException", (Throwable) e);
            Log.m26i(TAG, "mWaveData size : " + this.mWaveData.length + " fromIndex : " + i3 + " toIndex : " + i4);
        }
        this.mIsDataChanged = true;
    }

    /* access modifiers changed from: package-private */
    public void trimBookmark(int i, int i2) {
        ArrayList<Bookmark> arrayList = new ArrayList<>();
        Iterator<Bookmark> it = this.mBookmarkList.iterator();
        while (it.hasNext()) {
            Bookmark next = it.next();
            int elapsed = next.getElapsed();
            if (elapsed >= i && elapsed <= i2) {
                next.setElapsed(elapsed - i);
                arrayList.add(next);
            }
        }
        this.mBookmarkList.clear();
        this.mBookmarkList = arrayList;
        this.mIsDataChanged = true;
    }

    private void deleteBookmark(int i, int i2) {
        ArrayList<Bookmark> arrayList = new ArrayList<>();
        Iterator<Bookmark> it = this.mBookmarkList.iterator();
        while (it.hasNext()) {
            Bookmark next = it.next();
            int elapsed = next.getElapsed();
            if (elapsed <= i) {
                next.setElapsed(elapsed);
                arrayList.add(next);
            } else if (elapsed >= i2) {
                next.setElapsed((elapsed + i) - i2);
                arrayList.add(next);
            }
        }
        this.mBookmarkList.clear();
        this.mBookmarkList = arrayList;
        this.mIsDataChanged = true;
    }

    private void trimStt(int i, int i2) {
        ArrayList<TextData> arrayList = new ArrayList<>();
        ArrayList<TextData> arrayList2 = this.mSttData;
        if (arrayList2 != null) {
            Iterator<TextData> it = arrayList2.iterator();
            while (it.hasNext()) {
                TextData next = it.next();
                long j = next.timeStamp;
                long j2 = (long) i;
                if (j >= j2 && next.duration + j < ((long) i2)) {
                    long j3 = j - j2;
                    next.elapsedTime = j3;
                    next.timeStamp = j3;
                    arrayList.add(next);
                    Log.m29v(TAG, "trim-add : " + next.timeStamp + ' ' + next.mText[0]);
                }
            }
        }
        this.mSttData = arrayList;
        this.mIsDataChanged = true;
    }

    private void deleteStt(int i, int i2) {
        int i3 = i;
        int i4 = i2;
        ArrayList<TextData> arrayList = new ArrayList<>();
        ArrayList<TextData> arrayList2 = this.mSttData;
        if (arrayList2 != null) {
            Iterator<TextData> it = arrayList2.iterator();
            while (it.hasNext()) {
                TextData next = it.next();
                long j = next.timeStamp;
                if (j <= ((long) i3)) {
                    arrayList.add(next);
                    Log.m29v(TAG, "delete-add : " + next.timeStamp + ' ' + next.mText[0]);
                }
                if (next.duration + j > ((long) i4)) {
                    long j2 = j - ((long) (i4 - i3));
                    next.elapsedTime = j2;
                    next.timeStamp = j2;
                    arrayList.add(next);
                    Log.m29v(TAG, "delete-add : " + next.timeStamp + ' ' + next.mText[0]);
                }
            }
        }
        this.mSttData = arrayList;
        this.mIsDataChanged = true;
    }

    public ArrayList<Bookmark> getBookmarkList() {
        return this.mBookmarkList;
    }

    public int getBookmarkCount() {
        return this.mBookmarkList.size();
    }

    public boolean removeRoughBookmark(int i) {
        long currentTimeMillis = System.currentTimeMillis();
        for (int i2 = 0; i2 < this.mBookmarkList.size(); i2++) {
            Bookmark bookmark = this.mBookmarkList.get(i2);
            int elapsed = bookmark.getElapsed() - i;
            long j = 1000;
            try {
                String description = bookmark.getDescription();
                if (!"".equals(description)) {
                    j = currentTimeMillis - Long.valueOf(description).longValue();
                }
            } catch (NumberFormatException e) {
                Log.m24e(TAG, "NumberFormatException", (Throwable) e);
            }
            if (j >= 500 && ((bookmark.getElapsed() == 0 && elapsed == 0) || (elapsed > 0 && elapsed <= 60))) {
                this.mBookmarkList.remove(bookmark);
                this.mLastRemovedBookmarkTime = bookmark.getElapsed();
                Log.m29v(TAG, "removeRoughBookmark - bookmark removed : " + this.mLastRemovedBookmarkTime);
                this.mIsDataChanged = true;
                return true;
            }
        }
        return false;
    }

    public void resetLastAddedBookmarkTime() {
        Log.m26i(TAG, "resetLastAddedBookmarkTime - time :" + this.mLastRemovedBookmarkTime);
        this.mLastAddedBookmarkTime = -1;
    }

    public int getLastAddedBookmarkTime() {
        return this.mLastAddedBookmarkTime;
    }

    public int getLastRemovedBookmarkTime() {
        Log.m26i(TAG, "getLastRemovedBookmarkTime - time :" + this.mLastRemovedBookmarkTime);
        return this.mLastRemovedBookmarkTime;
    }

    public int[] getAmplitudeCollector() {
        int[] iArr = new int[this.mAmplitudeCollector.size()];
        int i = 0;
        for (Integer intValue : this.mAmplitudeCollector) {
            iArr[i] = intValue.intValue();
            i++;
        }
        return iArr;
    }

    public int getAmplitudeCollectorSize() {
        return this.mAmplitudeCollector.size();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x004d, code lost:
        if (r10 > ((long) r1)) goto L_0x0052;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean addSilenceSttData(int r18, int r19) {
        /*
            r17 = this;
            r0 = r17
            r1 = r19
            java.util.ArrayList<com.sec.android.app.voicenote.common.util.TextData> r2 = r0.mSttData
            r3 = 0
            if (r2 != 0) goto L_0x000a
            return r3
        L_0x000a:
            int r2 = r2.size()
            if (r2 <= 0) goto L_0x00c2
            java.util.ArrayList<com.sec.android.app.voicenote.common.util.TextData> r4 = r0.mSttData
            r5 = 1
            int r2 = r2 - r5
            java.lang.Object r2 = r4.get(r2)
            com.sec.android.app.voicenote.common.util.TextData r2 = (com.sec.android.app.voicenote.common.util.TextData) r2
            long r6 = r2.timeStamp
            long r8 = r2.duration
            long r6 = r6 + r8
            r2 = r18
            long r8 = (long) r2
            int r2 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r2 >= 0) goto L_0x00c2
            java.util.ArrayList<com.sec.android.app.voicenote.common.util.TextData> r2 = r0.mSttData
            java.util.Iterator r2 = r2.iterator()
            r4 = r3
        L_0x002d:
            boolean r6 = r2.hasNext()
            if (r6 == 0) goto L_0x00bf
            java.lang.Object r6 = r2.next()
            com.sec.android.app.voicenote.common.util.TextData r6 = (com.sec.android.app.voicenote.common.util.TextData) r6
            long r10 = r6.timeStamp
            long r12 = r6.duration
            long r12 = r12 + r10
            int r7 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            java.lang.String r14 = " endTime : "
            java.lang.String r15 = " startTime : "
            java.lang.String r5 = "SimpleMetadataRepository"
            if (r7 > 0) goto L_0x0050
            r16 = r4
            long r3 = (long) r1
            int r3 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
            if (r3 <= 0) goto L_0x005b
            goto L_0x0052
        L_0x0050:
            r16 = r4
        L_0x0052:
            int r3 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
            if (r3 > 0) goto L_0x008e
            long r3 = (long) r1
            int r3 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
            if (r3 > 0) goto L_0x008e
        L_0x005b:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "      silence : <"
            r3.append(r4)
            java.lang.String[] r4 = r6.mText
            r6 = 0
            r4 = r4[r6]
            r3.append(r4)
            r4 = 62
            r3.append(r4)
            r3.append(r15)
            r3.append(r10)
            r3.append(r14)
            r3.append(r12)
            java.lang.String r3 = r3.toString()
            com.sec.android.app.voicenote.provider.Log.m29v(r5, r3)
            r2.remove()
            r3 = 1
            r0.mIsDataChanged = r3
            r4 = r3
            r6 = 0
            goto L_0x00bb
        L_0x008e:
            r3 = 1
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r3 = "nonsilence : <"
            r4.append(r3)
            java.lang.String[] r3 = r6.mText
            r6 = 0
            r3 = r3[r6]
            r4.append(r3)
            r3 = 62
            r4.append(r3)
            r4.append(r15)
            r4.append(r10)
            r4.append(r14)
            r4.append(r12)
            java.lang.String r3 = r4.toString()
            com.sec.android.app.voicenote.provider.Log.m29v(r5, r3)
            r4 = r16
        L_0x00bb:
            r3 = r6
            r5 = 1
            goto L_0x002d
        L_0x00bf:
            r16 = r4
            goto L_0x00c5
        L_0x00c2:
            r6 = r3
            r16 = r6
        L_0x00c5:
            return r16
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.SimpleMetadataRepository.addSilenceSttData(int, int):boolean");
    }

    public void addSttData(ArrayList<TextData> arrayList) {
        Iterator<TextData> it;
        ArrayList<TextData> arrayList2 = arrayList;
        Log.m26i(TAG, "addSttData()");
        int size = arrayList.size();
        if (size != 0) {
            if (this.mSttData == null) {
                this.mSttData = new ArrayList<>();
            }
            Iterator<TextData> it2 = arrayList.iterator();
            while (it2.hasNext()) {
                TextData next = it2.next();
                long j = next.timeStamp;
                Log.m29v(TAG, "      new : <" + next.mText[0] + '>' + " startTime : " + j + " endTime : " + (next.duration + j));
            }
            int size2 = this.mSttData.size();
            if (size2 > 0) {
                long j2 = arrayList2.get(0).timeStamp;
                TextData textData = arrayList2.get(size - 1);
                long j3 = textData.timeStamp + textData.duration;
                TextData textData2 = this.mSttData.get(size2 - 1);
                long j4 = textData2.timeStamp + textData2.duration;
                Log.m19d(TAG, "addSttData() : originalSttDataCount = " + size2);
                Log.m19d(TAG, "addSttData() : originalSttEndTime = " + j4);
                Log.m19d(TAG, "addSttData() : newSttDataCount = " + size);
                Log.m19d(TAG, "addSttData() : newSttDataStartTime = " + j2);
                Log.m19d(TAG, "addSttData() : newSttDataEndTime = " + j3);
                if (j2 < j4) {
                    Iterator<TextData> it3 = this.mSttData.iterator();
                    while (it3.hasNext()) {
                        TextData next2 = it3.next();
                        long j5 = next2.timeStamp;
                        long j6 = next2.duration + j5;
                        if ((j2 > j6 || j6 > j3) && ((j2 > j5 || j5 > j3) && (j5 > j2 || j3 > j6))) {
                            it = it3;
                            Log.m29v(TAG, "remain : <" + next2.mText[0] + '>' + " startTime : " + j5 + " endTime : " + j6);
                        } else {
                            it3.remove();
                            StringBuilder sb = new StringBuilder();
                            it = it3;
                            sb.append("      remove : <");
                            sb.append(next2.mText[0]);
                            sb.append('>');
                            sb.append(" startTime : ");
                            sb.append(j5);
                            sb.append(" endTime : ");
                            sb.append(j6);
                            Log.m29v(TAG, sb.toString());
                        }
                        it3 = it;
                    }
                }
            }
            this.mSttData.addAll(arrayList2);
            Collections.sort(this.mSttData);
            Iterator<TextData> it4 = this.mSttData.iterator();
            while (it4.hasNext()) {
                TextData next3 = it4.next();
                long j7 = next3.timeStamp;
                Log.m29v(TAG, "Total : <" + next3.mText[0] + '>' + " startTime : " + j7 + " endTime : " + (next3.duration + j7));
            }
            this.mIsDataChanged = true;
        }
    }

    public void setSttData(ArrayList<TextData> arrayList) {
        TextData textData;
        Log.m26i(TAG, "setSttData()");
        if (arrayList != null) {
            if (this.mSttData == null) {
                this.mSttData = new ArrayList<>();
            }
            this.mSttData.clear();
            Iterator<TextData> it = arrayList.iterator();
            while (it.hasNext()) {
                TextData next = it.next();
                TextData textData2 = new TextData();
                try {
                    textData = (TextData) next.clone();
                } catch (CloneNotSupportedException unused) {
                    Log.m22e(TAG, "setSttData() : clone fail");
                    textData = textData2;
                }
                this.mSttData.add(textData);
            }
            Collections.sort(this.mSttData);
            this.mIsDataChanged = true;
        }
    }

    public void clearSttData() {
        Log.m26i(TAG, "clearSttData");
        ArrayList<TextData> arrayList = this.mSttData;
        if (arrayList != null) {
            arrayList.clear();
            this.mSttData = null;
        }
        ArrayList<TextData> arrayList2 = this.mDisplayedSttData;
        if (arrayList2 != null) {
            arrayList2.clear();
            this.mDisplayedSttData = null;
        }
    }

    public ArrayList<TextData> getSttData() {
        return this.mSttData;
    }

    public void writeSttDataInFile(String str, ArrayList<TextData> arrayList) {
        Log.m26i(TAG, "writeSttDataInFile()");
        M4aInfo readFile = new M4aReader(str).readFile();
        if (readFile == null) {
            Log.m22e(TAG, "write info is null !!!");
            return;
        }
        if (arrayList != null) {
            StringBuilder sb = new StringBuilder();
            Iterator<TextData> it = arrayList.iterator();
            while (it.hasNext()) {
                sb.append(it.next().mText[0]);
            }
            if (Log.ENG) {
                android.util.Log.v("ASRTest", "TEST_PLATFORM: RESULTS: result: " + sb);
            }
        } else {
            android.util.Log.v("ASRTest", "TEST_PLATFORM: RESULTS: result:");
        }
        new SttHelper(readFile).overwrite(arrayList);
    }

    private void setMeetingData(MeetingData meetingData) {
        Log.m26i(TAG, "setMeetingData");
        this.mMeetingData = meetingData;
    }

    private String getTitle(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        return str.substring(str.lastIndexOf(47) + 1, str.lastIndexOf(46));
    }
}
