package com.samsung.android.scloud.oem.lib.utils;

import android.os.ParcelFileDescriptor;
import com.samsung.android.scloud.oem.lib.LOG;
import java.io.File;
import java.io.FileNotFoundException;

public final class FileTool {
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0091 A[Catch:{ IOException -> 0x009f }] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0096 A[Catch:{ IOException -> 0x009f }] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x009b A[Catch:{ IOException -> 0x009f }] */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x00af A[Catch:{ IOException -> 0x00bd }] */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x00b4 A[Catch:{ IOException -> 0x00bd }] */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x00b9 A[Catch:{ IOException -> 0x00bd }] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x00ca A[Catch:{ IOException -> 0x00d8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00cf A[Catch:{ IOException -> 0x00d8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00d4 A[Catch:{ IOException -> 0x00d8 }] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:54:0x00a6=Splitter:B:54:0x00a6, B:39:0x0088=Splitter:B:39:0x0088} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean fileCopy(java.lang.String r8, java.lang.String r9) {
        /*
            java.lang.String r0 = "fileCopy() failed"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "fileCopy(), from : "
            r1.append(r2)
            r1.append(r8)
            java.lang.String r2 = " , to : "
            r1.append(r2)
            r1.append(r9)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "FileTool"
            com.samsung.android.scloud.oem.lib.LOG.m15i(r2, r1)
            java.io.File r1 = new java.io.File
            r1.<init>(r8)
            boolean r8 = r1.isFile()
            r3 = 0
            if (r8 == 0) goto L_0x00e7
            java.io.File r8 = new java.io.File
            r8.<init>(r9)
            boolean r9 = r8.exists()
            if (r9 == 0) goto L_0x003a
            r8.delete()
        L_0x003a:
            boolean r9 = r1.renameTo(r8)
            r4 = 1
            if (r9 != 0) goto L_0x00dd
            r9 = 1024(0x400, float:1.435E-42)
            byte[] r9 = new byte[r9]
            r5 = 0
            java.io.FileInputStream r6 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x00a4, IOException -> 0x0086, all -> 0x0082 }
            r6.<init>(r1)     // Catch:{ FileNotFoundException -> 0x00a4, IOException -> 0x0086, all -> 0x0082 }
            java.io.FileOutputStream r7 = new java.io.FileOutputStream     // Catch:{ FileNotFoundException -> 0x007e, IOException -> 0x007a, all -> 0x0077 }
            r7.<init>(r8)     // Catch:{ FileNotFoundException -> 0x007e, IOException -> 0x007a, all -> 0x0077 }
        L_0x0050:
            int r8 = r9.length     // Catch:{ FileNotFoundException -> 0x0075, IOException -> 0x0073, all -> 0x0071 }
            int r8 = r6.read(r9, r3, r8)     // Catch:{ FileNotFoundException -> 0x0075, IOException -> 0x0073, all -> 0x0071 }
            r5 = -1
            if (r8 == r5) goto L_0x005c
            r7.write(r9, r3, r8)     // Catch:{ FileNotFoundException -> 0x0075, IOException -> 0x0073, all -> 0x0071 }
            goto L_0x0050
        L_0x005c:
            boolean r8 = r1.exists()     // Catch:{ IOException -> 0x006c }
            if (r8 == 0) goto L_0x0065
            r1.delete()     // Catch:{ IOException -> 0x006c }
        L_0x0065:
            r6.close()     // Catch:{ IOException -> 0x006c }
            r7.close()     // Catch:{ IOException -> 0x006c }
            goto L_0x0070
        L_0x006c:
            r8 = move-exception
            r8.printStackTrace()
        L_0x0070:
            return r4
        L_0x0071:
            r8 = move-exception
            goto L_0x00c4
        L_0x0073:
            r8 = move-exception
            goto L_0x007c
        L_0x0075:
            r8 = move-exception
            goto L_0x0080
        L_0x0077:
            r8 = move-exception
            r7 = r5
            goto L_0x00c4
        L_0x007a:
            r8 = move-exception
            r7 = r5
        L_0x007c:
            r5 = r6
            goto L_0x0088
        L_0x007e:
            r8 = move-exception
            r7 = r5
        L_0x0080:
            r5 = r6
            goto L_0x00a6
        L_0x0082:
            r8 = move-exception
            r6 = r5
            r7 = r6
            goto L_0x00c4
        L_0x0086:
            r8 = move-exception
            r7 = r5
        L_0x0088:
            com.samsung.android.scloud.oem.lib.LOG.m14e(r2, r0, r8)     // Catch:{ all -> 0x00c2 }
            boolean r8 = r1.exists()     // Catch:{ IOException -> 0x009f }
            if (r8 == 0) goto L_0x0094
            r1.delete()     // Catch:{ IOException -> 0x009f }
        L_0x0094:
            if (r5 == 0) goto L_0x0099
            r5.close()     // Catch:{ IOException -> 0x009f }
        L_0x0099:
            if (r7 == 0) goto L_0x00a3
            r7.close()     // Catch:{ IOException -> 0x009f }
            goto L_0x00a3
        L_0x009f:
            r8 = move-exception
            r8.printStackTrace()
        L_0x00a3:
            return r3
        L_0x00a4:
            r8 = move-exception
            r7 = r5
        L_0x00a6:
            com.samsung.android.scloud.oem.lib.LOG.m14e(r2, r0, r8)     // Catch:{ all -> 0x00c2 }
            boolean r8 = r1.exists()     // Catch:{ IOException -> 0x00bd }
            if (r8 == 0) goto L_0x00b2
            r1.delete()     // Catch:{ IOException -> 0x00bd }
        L_0x00b2:
            if (r5 == 0) goto L_0x00b7
            r5.close()     // Catch:{ IOException -> 0x00bd }
        L_0x00b7:
            if (r7 == 0) goto L_0x00c1
            r7.close()     // Catch:{ IOException -> 0x00bd }
            goto L_0x00c1
        L_0x00bd:
            r8 = move-exception
            r8.printStackTrace()
        L_0x00c1:
            return r3
        L_0x00c2:
            r8 = move-exception
            r6 = r5
        L_0x00c4:
            boolean r9 = r1.exists()     // Catch:{ IOException -> 0x00d8 }
            if (r9 == 0) goto L_0x00cd
            r1.delete()     // Catch:{ IOException -> 0x00d8 }
        L_0x00cd:
            if (r6 == 0) goto L_0x00d2
            r6.close()     // Catch:{ IOException -> 0x00d8 }
        L_0x00d2:
            if (r7 == 0) goto L_0x00dc
            r7.close()     // Catch:{ IOException -> 0x00d8 }
            goto L_0x00dc
        L_0x00d8:
            r9 = move-exception
            r9.printStackTrace()
        L_0x00dc:
            throw r8
        L_0x00dd:
            boolean r8 = r1.exists()
            if (r8 == 0) goto L_0x00e6
            r1.delete()
        L_0x00e6:
            return r4
        L_0x00e7:
            java.lang.String r8 = "oldFile is null or not file~!"
            com.samsung.android.scloud.oem.lib.LOG.m15i(r2, r8)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.samsung.android.scloud.oem.lib.utils.FileTool.fileCopy(java.lang.String, java.lang.String):boolean");
    }

    public static ParcelFileDescriptor openFile(String str) {
        LOG.m15i("FileTool", "openFile !!  path : " + str);
        String[] split = str.split("/");
        String str2 = split[split.length + -1];
        LOG.m15i("FileTool", "filename !!  uri : " + str2);
        File file = new File(str);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        try {
            return ParcelFileDescriptor.open(file, 939524096);
        } catch (FileNotFoundException e) {
            LOG.m14e("FileTool", "Unable to open file " + str, e);
            return null;
        }
    }
}
