package com.sec.android.app.voicenote.service;

import com.sec.android.app.voicenote.common.util.SpeechTimeData;
import com.sec.android.app.voicenote.common.util.SpeechTimeDataTreeSet;
import com.sec.android.app.voicenote.provider.Log;
import java.util.TreeSet;

public class SkipSilenceTask {
    private static final int MUTE_MARGIN_END = 700;
    private static final int MUTE_MARGIN_SEEK = 450;
    private static final int MUTE_MARGIN_START = 500;
    private static final String TAG = "SkipSilenceTask";
    private final SpeechTimeDataTreeSet mAllPlaySections = new SpeechTimeDataTreeSet();
    private OnSkipSilenceTaskListener mListeners;
    private TreeSet<SpeechTimeData> mUpcomingPlaySections;

    public interface OnSkipSilenceTaskListener {
        void playComplete();

        void seekTo(int i);
    }

    public SkipSilenceTask(SpeechTimeDataTreeSet speechTimeDataTreeSet) {
        updatePlaySection(speechTimeDataTreeSet);
    }

    public void updatePlaySection(SpeechTimeDataTreeSet speechTimeDataTreeSet) {
        this.mAllPlaySections.clear();
        this.mAllPlaySections.addAll(speechTimeDataTreeSet);
    }

    public synchronized void refreshUpcomingSectionsList(int i) {
        Log.m26i(TAG, "CheckMuteSectionTask - refreshUpcomingSectionsList " + i);
        long j = (long) i;
        SpeechTimeData speechTimeData = new SpeechTimeData(j, 0);
        SpeechTimeData speechTimeData2 = (SpeechTimeData) this.mAllPlaySections.floor(speechTimeData);
        if (speechTimeData2 == null || speechTimeData2.mStartTime + ((long) speechTimeData2.mDuration) <= j) {
            this.mUpcomingPlaySections = (TreeSet) this.mAllPlaySections.tailSet(speechTimeData);
        } else {
            this.mUpcomingPlaySections = (TreeSet) this.mAllPlaySections.tailSet(speechTimeData2);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0070, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x00e2, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean checkMuteSection(int r14) {
        /*
            r13 = this;
            monitor-enter(r13)
            java.util.TreeSet<com.sec.android.app.voicenote.common.util.SpeechTimeData> r0 = r13.mUpcomingPlaySections     // Catch:{ all -> 0x00e3 }
            r1 = 1
            if (r0 != 0) goto L_0x000f
            java.lang.String r14 = "SkipSilenceTask"
            java.lang.String r0 = "mUpcomingPlaySections is null"
            com.sec.android.app.voicenote.provider.Log.m22e(r14, r0)     // Catch:{ all -> 0x00e3 }
            monitor-exit(r13)
            return r1
        L_0x000f:
            java.util.TreeSet<com.sec.android.app.voicenote.common.util.SpeechTimeData> r0 = r13.mUpcomingPlaySections     // Catch:{ all -> 0x00e3 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x00e3 }
            boolean r2 = r0.hasNext()     // Catch:{ all -> 0x00e3 }
            r3 = 0
            r4 = 0
            if (r2 == 0) goto L_0x0040
            java.lang.Object r2 = r0.next()     // Catch:{ ConcurrentModificationException -> 0x0024 }
            com.sec.android.app.voicenote.common.util.SpeechTimeData r2 = (com.sec.android.app.voicenote.common.util.SpeechTimeData) r2     // Catch:{ ConcurrentModificationException -> 0x0024 }
            goto L_0x0053
        L_0x0024:
            r2 = move-exception
            java.lang.String r5 = "SkipSilenceTask"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x00e3 }
            r6.<init>()     // Catch:{ all -> 0x00e3 }
            java.lang.String r7 = "ConcurrentModificationException exception:"
            r6.append(r7)     // Catch:{ all -> 0x00e3 }
            java.lang.String r2 = r2.getMessage()     // Catch:{ all -> 0x00e3 }
            r6.append(r2)     // Catch:{ all -> 0x00e3 }
            java.lang.String r2 = r6.toString()     // Catch:{ all -> 0x00e3 }
            com.sec.android.app.voicenote.provider.Log.m22e(r5, r2)     // Catch:{ all -> 0x00e3 }
            goto L_0x0052
        L_0x0040:
            com.sec.android.app.voicenote.service.SkipSilenceTask$OnSkipSilenceTaskListener r2 = r13.mListeners     // Catch:{ all -> 0x00e3 }
            if (r2 == 0) goto L_0x0052
            java.lang.String r14 = "SkipSilenceTask"
            java.lang.String r0 = "checkMuteSection - playComplete 1"
            com.sec.android.app.voicenote.provider.Log.m26i(r14, r0)     // Catch:{ all -> 0x00e3 }
            com.sec.android.app.voicenote.service.SkipSilenceTask$OnSkipSilenceTaskListener r14 = r13.mListeners     // Catch:{ all -> 0x00e3 }
            r14.playComplete()     // Catch:{ all -> 0x00e3 }
            monitor-exit(r13)
            return r4
        L_0x0052:
            r2 = r3
        L_0x0053:
            if (r2 != 0) goto L_0x0057
            monitor-exit(r13)
            return r1
        L_0x0057:
            long r5 = (long) r14
            long r7 = r2.mStartTime     // Catch:{ all -> 0x00e3 }
            r9 = 500(0x1f4, double:2.47E-321)
            long r7 = r7 - r9
            int r7 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r7 >= 0) goto L_0x0071
            com.sec.android.app.voicenote.service.SkipSilenceTask$OnSkipSilenceTaskListener r14 = r13.mListeners     // Catch:{ all -> 0x00e3 }
            if (r14 == 0) goto L_0x006f
            com.sec.android.app.voicenote.service.SkipSilenceTask$OnSkipSilenceTaskListener r14 = r13.mListeners     // Catch:{ all -> 0x00e3 }
            long r2 = r2.mStartTime     // Catch:{ all -> 0x00e3 }
            int r0 = (int) r2     // Catch:{ all -> 0x00e3 }
            int r0 = r0 + -450
            r14.seekTo(r0)     // Catch:{ all -> 0x00e3 }
        L_0x006f:
            monitor-exit(r13)
            return r1
        L_0x0071:
            long r7 = r2.mStartTime     // Catch:{ all -> 0x00e3 }
            int r11 = r2.mDuration     // Catch:{ all -> 0x00e3 }
            long r11 = (long) r11
            long r7 = r7 + r11
            r11 = 700(0x2bc, double:3.46E-321)
            long r7 = r7 + r11
            int r7 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r7 >= 0) goto L_0x0080
            monitor-exit(r13)
            return r1
        L_0x0080:
            boolean r7 = r0.hasNext()     // Catch:{ all -> 0x00e3 }
            if (r7 != 0) goto L_0x0094
            java.lang.String r14 = "SkipSilenceTask"
            java.lang.String r0 = "checkMuteSection - playComplete 2"
            com.sec.android.app.voicenote.provider.Log.m26i(r14, r0)     // Catch:{ all -> 0x00e3 }
            com.sec.android.app.voicenote.service.SkipSilenceTask$OnSkipSilenceTaskListener r14 = r13.mListeners     // Catch:{ all -> 0x00e3 }
            r14.playComplete()     // Catch:{ all -> 0x00e3 }
            monitor-exit(r13)
            return r4
        L_0x0094:
            boolean r7 = r0.hasNext()     // Catch:{ all -> 0x00e3 }
            if (r7 == 0) goto L_0x00b3
            long r7 = r2.mStartTime     // Catch:{ all -> 0x00e3 }
            int r11 = r2.mDuration     // Catch:{ all -> 0x00e3 }
            long r11 = (long) r11     // Catch:{ all -> 0x00e3 }
            long r7 = r7 + r11
            long r7 = r7 - r9
            int r7 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r7 <= 0) goto L_0x00b3
            boolean r2 = r0.hasNext()     // Catch:{ all -> 0x00e3 }
            if (r2 == 0) goto L_0x00b2
            java.lang.Object r2 = r0.next()     // Catch:{ all -> 0x00e3 }
            com.sec.android.app.voicenote.common.util.SpeechTimeData r2 = (com.sec.android.app.voicenote.common.util.SpeechTimeData) r2     // Catch:{ all -> 0x00e3 }
            goto L_0x0094
        L_0x00b2:
            r2 = r3
        L_0x00b3:
            if (r2 == 0) goto L_0x00cf
            long r3 = r2.mStartTime     // Catch:{ all -> 0x00e3 }
            long r3 = r3 - r9
            int r0 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r0 >= 0) goto L_0x00cb
            com.sec.android.app.voicenote.service.SkipSilenceTask$OnSkipSilenceTaskListener r14 = r13.mListeners     // Catch:{ all -> 0x00e3 }
            if (r14 == 0) goto L_0x00e1
            com.sec.android.app.voicenote.service.SkipSilenceTask$OnSkipSilenceTaskListener r14 = r13.mListeners     // Catch:{ all -> 0x00e3 }
            long r2 = r2.mStartTime     // Catch:{ all -> 0x00e3 }
            int r0 = (int) r2     // Catch:{ all -> 0x00e3 }
            int r0 = r0 + -450
            r14.seekTo(r0)     // Catch:{ all -> 0x00e3 }
            goto L_0x00e1
        L_0x00cb:
            r13.refreshUpcomingSectionsList(r14)     // Catch:{ all -> 0x00e3 }
            goto L_0x00e1
        L_0x00cf:
            com.sec.android.app.voicenote.service.SkipSilenceTask$OnSkipSilenceTaskListener r14 = r13.mListeners     // Catch:{ all -> 0x00e3 }
            if (r14 == 0) goto L_0x00e1
            java.lang.String r14 = "SkipSilenceTask"
            java.lang.String r0 = "checkMuteSection - playComplete 3"
            com.sec.android.app.voicenote.provider.Log.m26i(r14, r0)     // Catch:{ all -> 0x00e3 }
            com.sec.android.app.voicenote.service.SkipSilenceTask$OnSkipSilenceTaskListener r14 = r13.mListeners     // Catch:{ all -> 0x00e3 }
            r14.playComplete()     // Catch:{ all -> 0x00e3 }
            monitor-exit(r13)
            return r4
        L_0x00e1:
            monitor-exit(r13)
            return r1
        L_0x00e3:
            r14 = move-exception
            monitor-exit(r13)
            throw r14
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.SkipSilenceTask.checkMuteSection(int):boolean");
    }

    public final synchronized void setSkipSilenceTaskListener(OnSkipSilenceTaskListener onSkipSilenceTaskListener) {
        this.mListeners = onSkipSilenceTaskListener;
    }
}
