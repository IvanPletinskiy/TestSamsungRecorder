package com.sec.android.app.voicenote.common.util;

import java.util.Comparator;
import java.util.TreeSet;

public class SpeechTimeDataTreeSet extends TreeSet<SpeechTimeData> {
    private static final long serialVersionUID = 1;
    private static Comparator<SpeechTimeData> speechTimesComparator = $$Lambda$SpeechTimeDataTreeSet$kGI41xTBcTdfln5tX9xTTUOAi10.INSTANCE;

    public SpeechTimeDataTreeSet() {
        super(speechTimesComparator);
    }

    /* JADX WARNING: type inference failed for: r1v2, types: [java.util.NavigableSet] */
    /* JADX WARNING: type inference failed for: r1v3, types: [java.util.NavigableSet] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0070  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0079  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0085  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0091  */
    /* JADX WARNING: Removed duplicated region for block: B:28:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean add(com.sec.android.app.voicenote.common.util.SpeechTimeData r18) {
        /*
            r17 = this;
            r0 = r17
            com.sec.android.app.voicenote.common.util.SpeechTimeData r1 = new com.sec.android.app.voicenote.common.util.SpeechTimeData
            r2 = r18
            r1.<init>(r2)
            java.lang.Object r2 = r0.floor(r1)
            com.sec.android.app.voicenote.common.util.SpeechTimeData r2 = (com.sec.android.app.voicenote.common.util.SpeechTimeData) r2
            com.sec.android.app.voicenote.common.util.SpeechTimeData r3 = new com.sec.android.app.voicenote.common.util.SpeechTimeData
            long r4 = r1.mStartTime
            int r6 = r1.mDuration
            long r6 = (long) r6
            long r4 = r4 + r6
            r6 = 0
            r3.<init>(r4, r6)
            java.lang.Object r3 = r0.floor(r3)
            com.sec.android.app.voicenote.common.util.SpeechTimeData r3 = (com.sec.android.app.voicenote.common.util.SpeechTimeData) r3
            r4 = 1
            if (r2 == 0) goto L_0x0034
            long r7 = r2.mStartTime
            int r5 = r2.mDuration
            long r9 = (long) r5
            long r7 = r7 + r9
            long r9 = r1.mStartTime
            int r5 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r5 >= 0) goto L_0x0031
            goto L_0x0034
        L_0x0031:
            r5 = r2
            r7 = r6
            goto L_0x0036
        L_0x0034:
            r5 = r1
            r7 = r4
        L_0x0036:
            if (r3 == 0) goto L_0x0054
            long r8 = r3.mStartTime
            int r10 = r3.mDuration
            long r11 = (long) r10
            long r11 = r11 + r8
            long r13 = r1.mStartTime
            int r15 = r1.mDuration
            r16 = r7
            long r6 = (long) r15
            long r13 = r13 + r6
            int r6 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r6 >= 0) goto L_0x004b
            goto L_0x0056
        L_0x004b:
            long r6 = (long) r10
            long r8 = r8 + r6
            long r6 = r5.mStartTime
            long r8 = r8 - r6
            int r6 = (int) r8
            r5.mDuration = r6
            goto L_0x0062
        L_0x0054:
            r16 = r7
        L_0x0056:
            long r6 = r1.mStartTime
            int r8 = r1.mDuration
            long r8 = (long) r8
            long r6 = r6 + r8
            long r8 = r5.mStartTime
            long r6 = r6 - r8
            int r6 = (int) r6
            r5.mDuration = r6
        L_0x0062:
            r6 = 0
            if (r2 != 0) goto L_0x006e
            if (r3 != 0) goto L_0x006e
            java.util.TreeSet r6 = new java.util.TreeSet
            r6.<init>()
            r7 = 0
            goto L_0x0083
        L_0x006e:
            if (r2 != 0) goto L_0x0079
            r7 = 0
            java.util.NavigableSet r1 = r0.subSet(r1, r7, r3, r4)
            r6 = r1
            java.util.TreeSet r6 = (java.util.TreeSet) r6
            goto L_0x0083
        L_0x0079:
            r7 = 0
            if (r3 == 0) goto L_0x0083
            java.util.NavigableSet r1 = r0.subSet(r2, r7, r3, r4)
            r6 = r1
            java.util.TreeSet r6 = (java.util.TreeSet) r6
        L_0x0083:
            if (r6 == 0) goto L_0x0088
            r6.clear()
        L_0x0088:
            if (r16 == 0) goto L_0x0091
            boolean r1 = super.add(r5)
            if (r1 == 0) goto L_0x0091
            goto L_0x0092
        L_0x0091:
            r4 = r7
        L_0x0092:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.common.util.SpeechTimeDataTreeSet.add(com.sec.android.app.voicenote.common.util.SpeechTimeData):boolean");
    }
}
