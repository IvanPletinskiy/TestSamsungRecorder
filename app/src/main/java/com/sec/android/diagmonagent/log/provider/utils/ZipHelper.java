package com.sec.android.diagmonagent.log.provider.utils;

public class ZipHelper {
    /* JADX WARNING: Removed duplicated region for block: B:26:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0051  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0056  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String zip(java.lang.String r5, java.lang.String r6) throws java.lang.Exception {
        /*
            java.io.File r0 = new java.io.File
            r0.<init>(r5)
            boolean r1 = r0.isFile()
            if (r1 != 0) goto L_0x001a
            boolean r1 = r0.isDirectory()
            if (r1 == 0) goto L_0x0012
            goto L_0x001a
        L_0x0012:
            java.lang.Exception r5 = new java.lang.Exception
            java.lang.String r6 = "not found"
            r5.<init>(r6)
            throw r5
        L_0x001a:
            r1 = 0
            java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch:{ all -> 0x0047 }
            r2.<init>(r6)     // Catch:{ all -> 0x0047 }
            java.io.BufferedOutputStream r3 = new java.io.BufferedOutputStream     // Catch:{ all -> 0x0044 }
            r3.<init>(r2)     // Catch:{ all -> 0x0044 }
            java.util.zip.ZipOutputStream r4 = new java.util.zip.ZipOutputStream     // Catch:{ all -> 0x0042 }
            r4.<init>(r3)     // Catch:{ all -> 0x0042 }
            r1 = 8
            r4.setLevel(r1)     // Catch:{ all -> 0x003f }
            zipEntry(r0, r5, r4)     // Catch:{ all -> 0x003f }
            r4.finish()     // Catch:{ all -> 0x003f }
            r4.close()
            r3.close()
            r2.close()
            return r6
        L_0x003f:
            r5 = move-exception
            r1 = r4
            goto L_0x004a
        L_0x0042:
            r5 = move-exception
            goto L_0x004a
        L_0x0044:
            r5 = move-exception
            r3 = r1
            goto L_0x004a
        L_0x0047:
            r5 = move-exception
            r2 = r1
            r3 = r2
        L_0x004a:
            if (r1 == 0) goto L_0x004f
            r1.close()
        L_0x004f:
            if (r3 == 0) goto L_0x0054
            r3.close()
        L_0x0054:
            if (r2 == 0) goto L_0x0059
            r2.close()
        L_0x0059:
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.diagmonagent.log.provider.utils.ZipHelper.zip(java.lang.String, java.lang.String):java.lang.String");
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x007a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void zipEntry(java.io.File r4, java.lang.String r5, java.util.zip.ZipOutputStream r6) throws java.lang.Exception {
        /*
            boolean r0 = r4.isDirectory()
            r1 = 0
            if (r0 == 0) goto L_0x0023
            java.lang.String r0 = r4.getName()
            java.lang.String r2 = ".metadata"
            boolean r0 = r0.equalsIgnoreCase(r2)
            if (r0 == 0) goto L_0x0014
            return
        L_0x0014:
            java.io.File[] r4 = r4.listFiles()
        L_0x0018:
            int r0 = r4.length
            if (r1 >= r0) goto L_0x0073
            r0 = r4[r1]
            zipEntry(r0, r5, r6)
            int r1 = r1 + 1
            goto L_0x0018
        L_0x0023:
            r5 = 0
            java.lang.String r0 = r4.getPath()     // Catch:{ all -> 0x0077 }
            java.lang.String r2 = com.sec.android.diagmonagent.log.provider.utils.DiagMonUtil.TAG     // Catch:{ all -> 0x0077 }
            android.util.Log.d(r2, r0)     // Catch:{ all -> 0x0077 }
            java.util.StringTokenizer r2 = new java.util.StringTokenizer     // Catch:{ all -> 0x0077 }
            java.lang.String r3 = "/"
            r2.<init>(r0, r3)     // Catch:{ all -> 0x0077 }
            int r0 = r2.countTokens()     // Catch:{ all -> 0x0077 }
            java.lang.String r3 = r2.toString()     // Catch:{ all -> 0x0077 }
        L_0x003c:
            if (r0 == 0) goto L_0x0045
            int r0 = r0 + -1
            java.lang.String r3 = r2.nextToken()     // Catch:{ all -> 0x0077 }
            goto L_0x003c
        L_0x0045:
            java.io.BufferedInputStream r0 = new java.io.BufferedInputStream     // Catch:{ all -> 0x0077 }
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ all -> 0x0077 }
            r2.<init>(r4)     // Catch:{ all -> 0x0077 }
            r0.<init>(r2)     // Catch:{ all -> 0x0077 }
            java.util.zip.ZipEntry r5 = new java.util.zip.ZipEntry     // Catch:{ all -> 0x0074 }
            r5.<init>(r3)     // Catch:{ all -> 0x0074 }
            long r2 = r4.lastModified()     // Catch:{ all -> 0x0074 }
            r5.setTime(r2)     // Catch:{ all -> 0x0074 }
            r6.putNextEntry(r5)     // Catch:{ all -> 0x0074 }
            r4 = 2048(0x800, float:2.87E-42)
            byte[] r5 = new byte[r4]     // Catch:{ all -> 0x0074 }
        L_0x0062:
            int r2 = r0.read(r5, r1, r4)     // Catch:{ all -> 0x0074 }
            r3 = -1
            if (r2 == r3) goto L_0x006d
            r6.write(r5, r1, r2)     // Catch:{ all -> 0x0074 }
            goto L_0x0062
        L_0x006d:
            r6.closeEntry()     // Catch:{ all -> 0x0074 }
            r0.close()
        L_0x0073:
            return
        L_0x0074:
            r4 = move-exception
            r5 = r0
            goto L_0x0078
        L_0x0077:
            r4 = move-exception
        L_0x0078:
            if (r5 == 0) goto L_0x007d
            r5.close()
        L_0x007d:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.diagmonagent.log.provider.utils.ZipHelper.zipEntry(java.io.File, java.lang.String, java.util.zip.ZipOutputStream):void");
    }
}
