package com.sec.android.app.voicenote.service.codec;

import com.sec.android.app.voicenote.service.AudioFormat;
import java.util.HashMap;
import java.util.Locale;

public class M4aInfo {
    private static final String TAG = "M4aInfo";
    public HashMap<String, Integer> customAtomLength = new HashMap<>();
    public HashMap<String, Long> customAtomPosition = new HashMap<>();
    public int fileMoovLength = -1;
    public int fileUdtaLength = -1;
    public HashMap<String, Boolean> hasCustomAtom = new HashMap<>();
    public long moovPos = -1;
    public String path;
    public long udtaPos = -1;
    public boolean usedToWrite = false;

    public M4aInfo() {
        this.customAtomPosition.put(M4aConsts.STTD, -1L);
        this.customAtomPosition.put(M4aConsts.METD, -1L);
        this.customAtomPosition.put(M4aConsts.BOOK, -1L);
        this.customAtomPosition.put(M4aConsts.BOOKMARKS_NUMBER, -1L);
        this.customAtomPosition.put(M4aConsts.AMPL, -1L);
        this.customAtomPosition.put("smta", -1L);
        this.customAtomPosition.put(M4aConsts.SAUT, -1L);
        this.customAtomPosition.put(M4aConsts.VRDT, -1L);
        this.hasCustomAtom.put(M4aConsts.STTD, false);
        this.hasCustomAtom.put(M4aConsts.METD, false);
        this.hasCustomAtom.put(M4aConsts.BOOK, false);
        this.hasCustomAtom.put(M4aConsts.BOOKMARKS_NUMBER, false);
        this.hasCustomAtom.put(M4aConsts.AMPL, false);
        this.hasCustomAtom.put(M4aConsts.SAUT, false);
        this.hasCustomAtom.put(M4aConsts.VRDT, false);
        this.customAtomLength.put(M4aConsts.STTD, 0);
        this.customAtomLength.put(M4aConsts.METD, 0);
        this.customAtomLength.put(M4aConsts.BOOK, 0);
        this.customAtomLength.put(M4aConsts.BOOKMARKS_NUMBER, 0);
        this.customAtomLength.put(M4aConsts.AMPL, 0);
        this.customAtomLength.put("smta", 0);
        this.customAtomLength.put(M4aConsts.SAUT, 0);
        this.customAtomLength.put(M4aConsts.VRDT, 0);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0049, code lost:
        r12 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x004a, code lost:
        r2 = r12;
        r12 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:?, code lost:
        r5.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0068, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0069, code lost:
        com.sec.android.app.voicenote.provider.Log.m24e(TAG, "IOException", (java.lang.Throwable) r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x003a, code lost:
        r12 = th;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0059 A[SYNTHETIC, Splitter:B:26:0x0059] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0064 A[SYNTHETIC, Splitter:B:32:0x0064] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x003a A[ExcHandler: all (th java.lang.Throwable), Splitter:B:3:0x0011] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static long getMediaDuration(java.lang.String r12) {
        /*
            java.lang.String r0 = "IOException"
            java.lang.String r1 = "M4aInfo"
            java.io.File r2 = new java.io.File
            r2.<init>(r12)
            r3 = 1
            r12 = 0
            java.io.FileInputStream r5 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x0051 }
            r5.<init>(r2)     // Catch:{ FileNotFoundException -> 0x0051 }
            java.io.FileDescriptor r7 = r5.getFD()     // Catch:{ IOException -> 0x003c, all -> 0x003a }
            android.media.MediaExtractor r12 = new android.media.MediaExtractor     // Catch:{ IOException -> 0x003c, all -> 0x003a }
            r12.<init>()     // Catch:{ IOException -> 0x003c, all -> 0x003a }
            r8 = 0
            long r10 = r2.length()     // Catch:{ IOException -> 0x003c, all -> 0x003a }
            r6 = r12
            r6.setDataSource(r7, r8, r10)     // Catch:{ IOException -> 0x003c, all -> 0x003a }
            int r2 = r12.getTrackCount()     // Catch:{ IOException -> 0x003c, all -> 0x003a }
            if (r2 <= 0) goto L_0x0036
            r2 = 0
            android.media.MediaFormat r2 = r12.getTrackFormat(r2)     // Catch:{ IOException -> 0x003c, all -> 0x003a }
            java.lang.String r6 = "durationUs"
            long r2 = r2.getLong(r6)     // Catch:{ IOException -> 0x003c, all -> 0x003a }
            r3 = r2
        L_0x0036:
            r12.release()     // Catch:{ IOException -> 0x003c, all -> 0x003a }
            goto L_0x0040
        L_0x003a:
            r12 = move-exception
            goto L_0x0062
        L_0x003c:
            r12 = move-exception
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r1, (java.lang.String) r0, (java.lang.Throwable) r12)     // Catch:{ FileNotFoundException -> 0x0049, all -> 0x003a }
        L_0x0040:
            r5.close()     // Catch:{ IOException -> 0x0044 }
            goto L_0x0048
        L_0x0044:
            r12 = move-exception
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r1, (java.lang.String) r0, (java.lang.Throwable) r12)
        L_0x0048:
            return r3
        L_0x0049:
            r12 = move-exception
            r2 = r12
            r12 = r5
            goto L_0x0052
        L_0x004d:
            r2 = move-exception
            r5 = r12
            r12 = r2
            goto L_0x0062
        L_0x0051:
            r2 = move-exception
        L_0x0052:
            java.lang.String r5 = "FileNotFoundException"
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r1, (java.lang.String) r5, (java.lang.Throwable) r2)     // Catch:{ all -> 0x004d }
            if (r12 == 0) goto L_0x0061
            r12.close()     // Catch:{ IOException -> 0x005d }
            goto L_0x0061
        L_0x005d:
            r12 = move-exception
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r1, (java.lang.String) r0, (java.lang.Throwable) r12)
        L_0x0061:
            return r3
        L_0x0062:
            if (r5 == 0) goto L_0x006c
            r5.close()     // Catch:{ IOException -> 0x0068 }
            goto L_0x006c
        L_0x0068:
            r2 = move-exception
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r1, (java.lang.String) r0, (java.lang.Throwable) r2)
        L_0x006c:
            throw r12
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.codec.M4aInfo.getMediaDuration(java.lang.String):long");
    }

    public static boolean isM4A(String str) {
        return str != null && str.toLowerCase(Locale.US).endsWith(AudioFormat.ExtType.EXT_M4A);
    }
}
