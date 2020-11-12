package com.sec.android.app.voicenote.provider;

public class SettingsParser {
    private static final String DISABLE = "Disable";
    public static final String Delay = "Delay";
    private static final String ENABLE = "Enable";
    private static final String TAG = "SettingsParser";
    public static final String TAG_LOG = "Log";
    private static long sDelay = -1;

    /* JADX WARNING: Removed duplicated region for block: B:45:0x00a9 A[SYNTHETIC, Splitter:B:45:0x00a9] */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00b3 A[SYNTHETIC, Splitter:B:50:0x00b3] */
    /* JADX WARNING: Removed duplicated region for block: B:64:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void checkSettingsFile() {
        /*
            java.lang.String r0 = "Settings file close fail !!"
            java.lang.String r1 = "SettingsParser"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.io.File r3 = android.os.Environment.getExternalStorageDirectory()
            java.lang.String r3 = r3.getPath()
            r2.append(r3)
            java.lang.String r3 = "/Voice Recorder/settings.ini"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.io.File r3 = new java.io.File
            r3.<init>(r2)
            boolean r3 = r3.exists()
            if (r3 != 0) goto L_0x0029
            return
        L_0x0029:
            r3 = 0
            java.io.BufferedReader r4 = new java.io.BufferedReader     // Catch:{ IOException -> 0x00a2 }
            java.io.FileReader r5 = new java.io.FileReader     // Catch:{ IOException -> 0x00a2 }
            r5.<init>(r2)     // Catch:{ IOException -> 0x00a2 }
            r4.<init>(r5)     // Catch:{ IOException -> 0x00a2 }
        L_0x0034:
            java.lang.String r2 = r4.readLine()     // Catch:{ IOException -> 0x009d, all -> 0x009b }
            if (r2 == 0) goto L_0x0094
            java.lang.String r3 = ":"
            java.lang.String[] r2 = r2.split(r3)     // Catch:{ IOException -> 0x009d, all -> 0x009b }
            int r3 = r2.length     // Catch:{ IOException -> 0x009d, all -> 0x009b }
            r5 = 1
            if (r3 <= r5) goto L_0x0034
            r3 = 0
            r6 = r2[r3]     // Catch:{ IOException -> 0x009d, all -> 0x009b }
            r7 = -1
            int r8 = r6.hashCode()     // Catch:{ IOException -> 0x009d, all -> 0x009b }
            r9 = 76580(0x12b24, float:1.07311E-40)
            if (r8 == r9) goto L_0x0061
            r9 = 65915235(0x3edc963, float:1.3975844E-36)
            if (r8 == r9) goto L_0x0057
            goto L_0x006a
        L_0x0057:
            java.lang.String r8 = "Delay"
            boolean r6 = r6.equals(r8)     // Catch:{ IOException -> 0x009d, all -> 0x009b }
            if (r6 == 0) goto L_0x006a
            r7 = r5
            goto L_0x006a
        L_0x0061:
            java.lang.String r8 = "Log"
            boolean r6 = r6.equals(r8)     // Catch:{ IOException -> 0x009d, all -> 0x009b }
            if (r6 == 0) goto L_0x006a
            r7 = r3
        L_0x006a:
            if (r7 == 0) goto L_0x0078
            if (r7 == r5) goto L_0x006f
            goto L_0x0034
        L_0x006f:
            r2 = r2[r5]     // Catch:{ IOException -> 0x009d, all -> 0x009b }
            long r2 = java.lang.Long.parseLong(r2)     // Catch:{ IOException -> 0x009d, all -> 0x009b }
            sDelay = r2     // Catch:{ IOException -> 0x009d, all -> 0x009b }
            goto L_0x0034
        L_0x0078:
            r6 = r2[r5]     // Catch:{ IOException -> 0x009d, all -> 0x009b }
            java.lang.String r7 = "Enable"
            boolean r6 = r6.equalsIgnoreCase(r7)     // Catch:{ IOException -> 0x009d, all -> 0x009b }
            if (r6 == 0) goto L_0x0086
            com.sec.android.app.voicenote.provider.Log.setMode(r5)     // Catch:{ IOException -> 0x009d, all -> 0x009b }
            goto L_0x0034
        L_0x0086:
            r2 = r2[r5]     // Catch:{ IOException -> 0x009d, all -> 0x009b }
            java.lang.String r5 = "Disable"
            boolean r2 = r2.equalsIgnoreCase(r5)     // Catch:{ IOException -> 0x009d, all -> 0x009b }
            if (r2 == 0) goto L_0x0034
            com.sec.android.app.voicenote.provider.Log.setMode(r3)     // Catch:{ IOException -> 0x009d, all -> 0x009b }
            goto L_0x0034
        L_0x0094:
            r4.close()     // Catch:{ IOException -> 0x009d, all -> 0x009b }
            r4.close()     // Catch:{ IOException -> 0x00ad }
            goto L_0x00b0
        L_0x009b:
            r2 = move-exception
            goto L_0x00b1
        L_0x009d:
            r3 = r4
            goto L_0x00a2
        L_0x009f:
            r2 = move-exception
            r4 = r3
            goto L_0x00b1
        L_0x00a2:
            java.lang.String r2 = "Settings file not exist !!"
            com.sec.android.app.voicenote.provider.Log.m26i(r1, r2)     // Catch:{ all -> 0x009f }
            if (r3 == 0) goto L_0x00b0
            r3.close()     // Catch:{ IOException -> 0x00ad }
            goto L_0x00b0
        L_0x00ad:
            com.sec.android.app.voicenote.provider.Log.m26i(r1, r0)
        L_0x00b0:
            return
        L_0x00b1:
            if (r4 == 0) goto L_0x00ba
            r4.close()     // Catch:{ IOException -> 0x00b7 }
            goto L_0x00ba
        L_0x00b7:
            com.sec.android.app.voicenote.provider.Log.m26i(r1, r0)
        L_0x00ba:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.provider.SettingsParser.checkSettingsFile():void");
    }
}
