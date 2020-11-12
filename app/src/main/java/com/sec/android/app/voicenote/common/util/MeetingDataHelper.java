package com.sec.android.app.voicenote.common.util;

import com.sec.android.app.voicenote.service.codec.M4aConsts;
import com.sec.android.app.voicenote.service.codec.M4aInfo;
import com.sec.android.app.voicenote.service.codec.M4aSerializableAtomHelper;
import java.nio.ByteBuffer;

public class MeetingDataHelper extends M4aSerializableAtomHelper {
    private final byte[] newMETD = {0, 0, 0, 0, 109, 101, 116, 100};

    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.sec.android.app.voicenote.common.util.MeetingData loadData(java.lang.String r3) {
        /*
            if (r3 == 0) goto L_0x0036
            boolean r0 = com.sec.android.app.voicenote.service.codec.M4aInfo.isM4A(r3)
            if (r0 == 0) goto L_0x0036
            java.lang.Object r0 = com.sec.android.app.voicenote.service.codec.M4aConsts.FILE_LOCK
            monitor-enter(r0)
            com.sec.android.app.voicenote.service.codec.M4aReader r1 = new com.sec.android.app.voicenote.service.codec.M4aReader     // Catch:{ all -> 0x0033 }
            r1.<init>(r3)     // Catch:{ all -> 0x0033 }
            com.sec.android.app.voicenote.service.codec.M4aInfo r3 = r1.readFile()     // Catch:{ all -> 0x0033 }
            if (r3 == 0) goto L_0x0031
            java.util.HashMap<java.lang.String, java.lang.Boolean> r1 = r3.hasCustomAtom     // Catch:{ all -> 0x0033 }
            java.lang.String r2 = "metd"
            java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x0033 }
            java.lang.Boolean r1 = (java.lang.Boolean) r1     // Catch:{ all -> 0x0033 }
            boolean r1 = r1.booleanValue()     // Catch:{ all -> 0x0033 }
            if (r1 == 0) goto L_0x0031
            com.sec.android.app.voicenote.common.util.MeetingDataHelper r1 = new com.sec.android.app.voicenote.common.util.MeetingDataHelper     // Catch:{ all -> 0x0033 }
            r1.<init>(r3)     // Catch:{ all -> 0x0033 }
            com.sec.android.app.voicenote.common.util.MeetingData r3 = r1.read()     // Catch:{ all -> 0x0033 }
            monitor-exit(r0)     // Catch:{ all -> 0x0033 }
            return r3
        L_0x0031:
            monitor-exit(r0)     // Catch:{ all -> 0x0033 }
            goto L_0x0036
        L_0x0033:
            r3 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0033 }
            throw r3
        L_0x0036:
            r3 = 0
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.common.util.MeetingDataHelper.loadData(java.lang.String):com.sec.android.app.voicenote.common.util.MeetingData");
    }

    public MeetingDataHelper(M4aInfo m4aInfo) {
        super(m4aInfo);
    }

    public void overwrite(MeetingData meetingData) {
        long j;
        ByteBuffer wrap = ByteBuffer.wrap(this.newMETD);
        if (this.inf.hasCustomAtom.get(M4aConsts.METD).booleanValue()) {
            j = this.inf.customAtomPosition.get(M4aConsts.METD).longValue();
        } else {
            j = this.inf.udtaPos + 8;
        }
        if (overwriteAtom(meetingData, j, wrap)) {
            this.inf.hasCustomAtom.put(M4aConsts.METD, true);
            this.inf.customAtomPosition.put(M4aConsts.METD, Long.valueOf(j));
        }
    }

    public MeetingData read() {
        M4aInfo m4aInfo = this.inf;
        if (m4aInfo == null || !m4aInfo.hasCustomAtom.get(M4aConsts.METD).booleanValue()) {
            return null;
        }
        return (MeetingData) readAtom(this.inf.customAtomPosition.get(M4aConsts.METD).longValue());
    }

    public void remove() {
        if (this.inf.hasCustomAtom.get(M4aConsts.METD).booleanValue()) {
            removeAtom(this.inf.customAtomPosition.get(M4aConsts.METD).longValue());
            this.inf.hasCustomAtom.put(M4aConsts.METD, false);
        }
    }
}
