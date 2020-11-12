package com.sec.android.app.voicenote.service.helper;

import com.sec.android.app.voicenote.service.codec.M4aConsts;
import com.sec.android.app.voicenote.service.codec.M4aInfo;
import com.sec.android.app.voicenote.service.codec.M4aSerializableAtomHelper;

public class WaveHelper extends M4aSerializableAtomHelper {
    private static final String TAG = "WaveHelper";

    public WaveHelper(M4aInfo m4aInfo) {
        super(m4aInfo);
    }

    public int[] read() {
        M4aInfo m4aInfo = this.inf;
        if (m4aInfo == null || !m4aInfo.hasCustomAtom.get(M4aConsts.AMPL).booleanValue()) {
            return null;
        }
        return (int[]) readAtom(this.inf.customAtomPosition.get(M4aConsts.AMPL).longValue());
    }

    /* JADX WARNING: type inference failed for: r8v0, types: [int[], java.io.Serializable] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void overwrite(int[] r8) {
        /*
            r7 = this;
            com.sec.android.app.voicenote.service.codec.M4aInfo r0 = r7.inf
            if (r0 != 0) goto L_0x000c
            java.lang.String r8 = "WaveHelper"
            java.lang.String r0 = "overwrite() inf is NULL"
            com.sec.android.app.voicenote.provider.Log.m22e(r8, r0)
            return
        L_0x000c:
            r0 = 8
            byte[] r0 = new byte[r0]
            r0 = {0, 0, 0, 0, 97, 109, 112, 108} // fill-array
            java.nio.ByteBuffer r0 = java.nio.ByteBuffer.wrap(r0)
            com.sec.android.app.voicenote.service.codec.M4aInfo r1 = r7.inf
            java.util.HashMap<java.lang.String, java.lang.Boolean> r1 = r1.hasCustomAtom
            java.lang.String r2 = "ampl"
            java.lang.Object r1 = r1.get(r2)
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            if (r1 == 0) goto L_0x0038
            com.sec.android.app.voicenote.service.codec.M4aInfo r1 = r7.inf
            java.util.HashMap<java.lang.String, java.lang.Long> r1 = r1.customAtomPosition
            java.lang.Object r1 = r1.get(r2)
            java.lang.Long r1 = (java.lang.Long) r1
            long r3 = r1.longValue()
            goto L_0x003f
        L_0x0038:
            com.sec.android.app.voicenote.service.codec.M4aInfo r1 = r7.inf
            long r3 = r1.udtaPos
            r5 = 8
            long r3 = r3 + r5
        L_0x003f:
            boolean r8 = r7.overwriteAtom(r8, r3, r0)
            if (r8 == 0) goto L_0x005c
            com.sec.android.app.voicenote.service.codec.M4aInfo r8 = r7.inf
            java.util.HashMap<java.lang.String, java.lang.Boolean> r8 = r8.hasCustomAtom
            r0 = 1
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r0)
            r8.put(r2, r0)
            com.sec.android.app.voicenote.service.codec.M4aInfo r8 = r7.inf
            java.util.HashMap<java.lang.String, java.lang.Long> r8 = r8.customAtomPosition
            java.lang.Long r0 = java.lang.Long.valueOf(r3)
            r8.put(r2, r0)
        L_0x005c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.helper.WaveHelper.overwrite(int[]):void");
    }
}
