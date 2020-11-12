package com.sec.android.app.voicenote.service.helper;

import com.sec.android.app.voicenote.common.util.TextData;
import com.sec.android.app.voicenote.service.codec.M4aConsts;
import com.sec.android.app.voicenote.service.codec.M4aInfo;
import com.sec.android.app.voicenote.service.codec.M4aSerializableAtomHelper;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class SttHelper extends M4aSerializableAtomHelper {
    private static final String TAG = "SttHelper";
    private final byte[] newSTTD = {0, 0, 0, 0, 115, 116, 116, 100};

    public static final class DataType {
        public static final int NORMAL = 0;
    }

    public SttHelper(M4aInfo m4aInfo) {
        super(m4aInfo);
    }

    private void overwrite(ArrayList<TextData> arrayList, boolean z) {
        long j;
        ByteBuffer wrap = ByteBuffer.wrap(this.newSTTD);
        if (this.inf.hasCustomAtom.get(M4aConsts.STTD).booleanValue()) {
            j = this.inf.customAtomPosition.get(M4aConsts.STTD).longValue();
        } else {
            j = this.inf.udtaPos + 8;
        }
        if (overwriteAtom(arrayList, j, wrap)) {
            if (z) {
                exportToFile(this.inf.path, arrayList);
            }
            this.inf.hasCustomAtom.put(M4aConsts.STTD, true);
            this.inf.customAtomPosition.put(M4aConsts.STTD, Long.valueOf(j));
        }
    }

    public void overwrite(ArrayList<TextData> arrayList) {
        overwrite(arrayList, true);
    }

    public ArrayList<TextData> read() {
        M4aInfo m4aInfo = this.inf;
        if (m4aInfo == null || !m4aInfo.hasCustomAtom.get(M4aConsts.STTD).booleanValue()) {
            return null;
        }
        return (ArrayList) readAtom(this.inf.customAtomPosition.get(M4aConsts.STTD).longValue());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00a9, code lost:
        if (r2 != null) goto L_0x00ab;
     */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00a2 A[Catch:{ FileNotFoundException -> 0x00a3, IOException -> 0x009a, all -> 0x0097 }] */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00ba A[SYNTHETIC, Splitter:B:52:0x00ba] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void exportToFile(java.lang.String r8, java.util.ArrayList<com.sec.android.app.voicenote.common.util.TextData> r9) {
        /*
            r7 = this;
            java.lang.String r0 = "Error exporting stt data to text file"
            java.lang.String r1 = "SttHelper"
            if (r8 != 0) goto L_0x000c
            java.lang.String r8 = "exportToFile() path is null"
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r8)
            return
        L_0x000c:
            r2 = 46
            int r2 = r8.lastIndexOf(r2)
            r3 = 0
            java.lang.String r8 = r8.substring(r3, r2)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r8)
            java.lang.String r8 = "_memo.txt"
            r2.append(r8)
            java.lang.String r8 = r2.toString()
            r2 = 0
            java.io.File r4 = new java.io.File
            r4.<init>(r8)
            boolean r8 = r4.exists()
            if (r8 == 0) goto L_0x003f
            boolean r8 = r4.delete()
            if (r8 != 0) goto L_0x003f
            java.lang.String r8 = "delete failed"
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r8)
        L_0x003f:
            if (r9 == 0) goto L_0x00c2
            boolean r8 = r9.isEmpty()
            if (r8 == 0) goto L_0x0049
            goto L_0x00c2
        L_0x0049:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            boolean r5 = r4.createNewFile()     // Catch:{ FileNotFoundException -> 0x00a3, IOException -> 0x009a }
            if (r5 != 0) goto L_0x005a
            java.lang.String r8 = "createNewFile failed"
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r8)     // Catch:{ FileNotFoundException -> 0x00a3, IOException -> 0x009a }
            return
        L_0x005a:
            java.io.FileOutputStream r5 = new java.io.FileOutputStream     // Catch:{ FileNotFoundException -> 0x00a3, IOException -> 0x009a }
            r5.<init>(r4)     // Catch:{ FileNotFoundException -> 0x00a3, IOException -> 0x009a }
            int r2 = r9.size()     // Catch:{ FileNotFoundException -> 0x0094, IOException -> 0x0091, all -> 0x008f }
            r4 = r3
        L_0x0064:
            if (r4 >= r2) goto L_0x0080
            java.lang.Object r6 = r9.get(r4)     // Catch:{ FileNotFoundException -> 0x0094, IOException -> 0x0091, all -> 0x008f }
            com.sec.android.app.voicenote.common.util.TextData r6 = (com.sec.android.app.voicenote.common.util.TextData) r6     // Catch:{ FileNotFoundException -> 0x0094, IOException -> 0x0091, all -> 0x008f }
            int r6 = r6.dataType     // Catch:{ FileNotFoundException -> 0x0094, IOException -> 0x0091, all -> 0x008f }
            if (r6 != 0) goto L_0x007d
            java.lang.Object r6 = r9.get(r4)     // Catch:{ FileNotFoundException -> 0x0094, IOException -> 0x0091, all -> 0x008f }
            com.sec.android.app.voicenote.common.util.TextData r6 = (com.sec.android.app.voicenote.common.util.TextData) r6     // Catch:{ FileNotFoundException -> 0x0094, IOException -> 0x0091, all -> 0x008f }
            java.lang.String[] r6 = r6.mText     // Catch:{ FileNotFoundException -> 0x0094, IOException -> 0x0091, all -> 0x008f }
            r6 = r6[r3]     // Catch:{ FileNotFoundException -> 0x0094, IOException -> 0x0091, all -> 0x008f }
            r8.append(r6)     // Catch:{ FileNotFoundException -> 0x0094, IOException -> 0x0091, all -> 0x008f }
        L_0x007d:
            int r4 = r4 + 1
            goto L_0x0064
        L_0x0080:
            java.lang.String r8 = r8.toString()     // Catch:{ FileNotFoundException -> 0x0094, IOException -> 0x0091, all -> 0x008f }
            byte[] r8 = r8.getBytes()     // Catch:{ FileNotFoundException -> 0x0094, IOException -> 0x0091, all -> 0x008f }
            r5.write(r8)     // Catch:{ FileNotFoundException -> 0x0094, IOException -> 0x0091, all -> 0x008f }
            r5.close()     // Catch:{ Exception -> 0x00af }
            goto L_0x00b2
        L_0x008f:
            r8 = move-exception
            goto L_0x00b8
        L_0x0091:
            r8 = move-exception
            r2 = r5
            goto L_0x009b
        L_0x0094:
            r8 = move-exception
            r2 = r5
            goto L_0x00a4
        L_0x0097:
            r8 = move-exception
            r5 = r2
            goto L_0x00b8
        L_0x009a:
            r8 = move-exception
        L_0x009b:
            java.lang.String r9 = "IOException"
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r1, (java.lang.String) r9, (java.lang.Throwable) r8)     // Catch:{ all -> 0x0097 }
            if (r2 == 0) goto L_0x00b2
            goto L_0x00ab
        L_0x00a3:
            r8 = move-exception
        L_0x00a4:
            java.lang.String r9 = "FileNotFoundException"
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r1, (java.lang.String) r9, (java.lang.Throwable) r8)     // Catch:{ all -> 0x0097 }
            if (r2 == 0) goto L_0x00b2
        L_0x00ab:
            r2.close()     // Catch:{ Exception -> 0x00af }
            goto L_0x00b2
        L_0x00af:
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r0)
        L_0x00b2:
            java.lang.String r8 = "exportToFile() x"
            com.sec.android.app.voicenote.provider.Log.m29v(r1, r8)
            return
        L_0x00b8:
            if (r5 == 0) goto L_0x00c1
            r5.close()     // Catch:{ Exception -> 0x00be }
            goto L_0x00c1
        L_0x00be:
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r0)
        L_0x00c1:
            throw r8
        L_0x00c2:
            java.lang.String r8 = "exportToFile() list is null or empty"
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r8)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.helper.SttHelper.exportToFile(java.lang.String, java.util.ArrayList):void");
    }
}
