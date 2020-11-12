package com.sec.android.app.voicenote.service.helper;

import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.codec.M4aInfo;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class SmtaHelper {
    private static final int SMTA_SIZE = 26;
    private static final String TAG = "SmtaHelper";
    private static final String TEMP_NAME = "temp6546368.m4a";
    private final byte[] SAUT = {0, 0, 0, 14, 115, 97, 117, 116, 0, 0, 0, 1, 0, 0};
    private M4aInfo inf = null;
    private boolean invalidInit;
    private long mSmtaPos = -1;

    public SmtaHelper(M4aInfo m4aInfo) {
        if (m4aInfo != null) {
            this.inf = m4aInfo;
            this.invalidInit = m4aInfo.usedToWrite;
            this.mSmtaPos = m4aInfo.customAtomPosition.get("smta").longValue();
            return;
        }
        this.invalidInit = true;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v0, resolved type: java.nio.channels.FileChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v2, resolved type: java.nio.channels.FileChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v3, resolved type: java.nio.channels.FileChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v5, resolved type: java.nio.channels.FileChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v6, resolved type: java.nio.channels.FileChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v7, resolved type: java.nio.channels.FileChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v8, resolved type: java.nio.channels.FileChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v9, resolved type: java.nio.channels.FileChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v10, resolved type: java.nio.channels.FileChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v11, resolved type: java.nio.channels.FileChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v12, resolved type: java.nio.channels.FileChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v15, resolved type: java.nio.channels.FileChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v16, resolved type: java.nio.channels.FileChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v20, resolved type: java.io.RandomAccessFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v21, resolved type: java.nio.channels.FileChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v29, resolved type: java.nio.channels.FileChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v30, resolved type: java.nio.channels.FileChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v31, resolved type: java.nio.channels.FileChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v32, resolved type: java.nio.channels.FileChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v33, resolved type: java.nio.channels.FileChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v34, resolved type: java.nio.channels.FileChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v35, resolved type: java.nio.channels.FileChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v36, resolved type: java.nio.channels.FileChannel} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:105:0x023a=Splitter:B:105:0x023a, B:87:0x01ff=Splitter:B:87:0x01ff} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void overwrite(int r22) {
        /*
            r21 = this;
            r1 = r21
            r0 = r22
            monitor-enter(r21)
            boolean r2 = r1.invalidInit     // Catch:{ all -> 0x0247 }
            if (r2 == 0) goto L_0x000b
            monitor-exit(r21)
            return
        L_0x000b:
            com.sec.android.app.voicenote.service.codec.M4aInfo r2 = r1.inf     // Catch:{ all -> 0x0247 }
            r3 = 1
            r2.usedToWrite = r3     // Catch:{ all -> 0x0247 }
            byte[] r2 = r1.SAUT     // Catch:{ all -> 0x0247 }
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.wrap(r2)     // Catch:{ all -> 0x0247 }
            r4 = 13
            byte r5 = (byte) r0     // Catch:{ all -> 0x0247 }
            r2.put(r4, r5)     // Catch:{ all -> 0x0247 }
            r2.rewind()     // Catch:{ all -> 0x0247 }
            com.sec.android.app.voicenote.service.codec.M4aInfo r4 = r1.inf     // Catch:{ all -> 0x0247 }
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = r4.customAtomLength     // Catch:{ all -> 0x0247 }
            java.lang.String r5 = "smta"
            java.lang.Object r4 = r4.get(r5)     // Catch:{ all -> 0x0247 }
            java.lang.Integer r4 = (java.lang.Integer) r4     // Catch:{ all -> 0x0247 }
            int r4 = r4.intValue()     // Catch:{ all -> 0x0247 }
            java.lang.String r5 = "SmtaHelper"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0247 }
            r6.<init>()     // Catch:{ all -> 0x0247 }
            java.lang.String r7 = "overwrite - recordmode - mode : "
            r6.append(r7)     // Catch:{ all -> 0x0247 }
            r6.append(r0)     // Catch:{ all -> 0x0247 }
            java.lang.String r0 = " path : "
            r6.append(r0)     // Catch:{ all -> 0x0247 }
            com.sec.android.app.voicenote.service.codec.M4aInfo r0 = r1.inf     // Catch:{ all -> 0x0247 }
            java.lang.String r0 = r0.path     // Catch:{ all -> 0x0247 }
            r6.append(r0)     // Catch:{ all -> 0x0247 }
            java.lang.String r0 = " smta size : "
            r6.append(r0)     // Catch:{ all -> 0x0247 }
            r6.append(r4)     // Catch:{ all -> 0x0247 }
            java.lang.String r0 = r6.toString()     // Catch:{ all -> 0x0247 }
            com.sec.android.app.voicenote.provider.Log.m19d(r5, r0)     // Catch:{ all -> 0x0247 }
            r7 = 8
            r0 = 26
            r9 = 0
            if (r4 >= r0) goto L_0x01a4
            java.io.RandomAccessFile r10 = new java.io.RandomAccessFile     // Catch:{ FileNotFoundException -> 0x019e, IOException -> 0x0198, all -> 0x0192 }
            com.sec.android.app.voicenote.service.codec.M4aInfo r11 = r1.inf     // Catch:{ FileNotFoundException -> 0x019e, IOException -> 0x0198, all -> 0x0192 }
            java.lang.String r11 = r11.path     // Catch:{ FileNotFoundException -> 0x019e, IOException -> 0x0198, all -> 0x0192 }
            java.lang.String r12 = "rw"
            r10.<init>(r11, r12)     // Catch:{ FileNotFoundException -> 0x019e, IOException -> 0x0198, all -> 0x0192 }
            java.io.RandomAccessFile r11 = new java.io.RandomAccessFile     // Catch:{ FileNotFoundException -> 0x018d, IOException -> 0x0188, all -> 0x0185 }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x018d, IOException -> 0x0188, all -> 0x0185 }
            r12.<init>()     // Catch:{ FileNotFoundException -> 0x018d, IOException -> 0x0188, all -> 0x0185 }
            java.io.File r13 = android.os.Environment.getExternalStorageDirectory()     // Catch:{ FileNotFoundException -> 0x018d, IOException -> 0x0188, all -> 0x0185 }
            java.lang.String r13 = r13.getAbsolutePath()     // Catch:{ FileNotFoundException -> 0x018d, IOException -> 0x0188, all -> 0x0185 }
            r12.append(r13)     // Catch:{ FileNotFoundException -> 0x018d, IOException -> 0x0188, all -> 0x0185 }
            r13 = 47
            r12.append(r13)     // Catch:{ FileNotFoundException -> 0x018d, IOException -> 0x0188, all -> 0x0185 }
            java.lang.String r14 = "temp6546368.m4a"
            r12.append(r14)     // Catch:{ FileNotFoundException -> 0x018d, IOException -> 0x0188, all -> 0x0185 }
            java.lang.String r12 = r12.toString()     // Catch:{ FileNotFoundException -> 0x018d, IOException -> 0x0188, all -> 0x0185 }
            java.lang.String r14 = "rw"
            r11.<init>(r12, r14)     // Catch:{ FileNotFoundException -> 0x018d, IOException -> 0x0188, all -> 0x0185 }
            java.nio.channels.FileChannel r12 = r10.getChannel()     // Catch:{ FileNotFoundException -> 0x0181, IOException -> 0x017d, all -> 0x0179 }
            java.nio.channels.FileChannel r9 = r11.getChannel()     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            long r14 = r1.mSmtaPos     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            long r5 = (long) r4     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            long r14 = r14 + r5
            r12.position(r14)     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            r17 = 0
            long r19 = r12.size()     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            r15 = r9
            r16 = r12
            long r5 = r15.transferFrom(r16, r17, r19)     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            r14 = 0
            int r5 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r5 >= 0) goto L_0x00c7
            java.lang.String r0 = "SmtaHelper"
            java.lang.String r2 = "dst.transferFrom() < 0"
            com.sec.android.app.voicenote.provider.Log.m22e(r0, r2)     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            r1.closeQuietly(r12)     // Catch:{ all -> 0x0247 }
            r1.closeQuietly(r9)     // Catch:{ all -> 0x0247 }
            r1.closeQuietly(r10)     // Catch:{ all -> 0x0247 }
            r1.closeQuietly(r11)     // Catch:{ all -> 0x0247 }
            monitor-exit(r21)
            return
        L_0x00c7:
            long r5 = r1.mSmtaPos     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            long r5 = r5 + r7
            r7 = 4
            long r5 = r5 + r7
            r12.position(r5)     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            r12.write(r2)     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            r9.position(r14)     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            long r17 = r12.position()     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            long r19 = r9.size()     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            r5 = r14
            r15 = r12
            r16 = r9
            long r7 = r15.transferFrom(r16, r17, r19)     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            int r2 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r2 >= 0) goto L_0x00ff
            java.lang.String r0 = "SmtaHelper"
            java.lang.String r2 = "srcWrite.transferFrom() < 0"
            com.sec.android.app.voicenote.provider.Log.m22e(r0, r2)     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            r1.closeQuietly(r12)     // Catch:{ all -> 0x0247 }
            r1.closeQuietly(r9)     // Catch:{ all -> 0x0247 }
            r1.closeQuietly(r10)     // Catch:{ all -> 0x0247 }
            r1.closeQuietly(r11)     // Catch:{ all -> 0x0247 }
            monitor-exit(r21)
            return
        L_0x00ff:
            int r0 = r0 - r4
            r1.updateOuterAtomsLengths(r12, r0)     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            com.sec.android.app.voicenote.service.codec.M4aInfo r0 = r1.inf     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            java.util.HashMap<java.lang.String, java.lang.Boolean> r0 = r0.hasCustomAtom     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            java.lang.String r2 = "saut"
            java.lang.Boolean r3 = java.lang.Boolean.valueOf(r3)     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            r0.put(r2, r3)     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            com.sec.android.app.voicenote.service.codec.M4aInfo r0 = r1.inf     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            java.util.HashMap<java.lang.String, java.lang.Long> r0 = r0.customAtomPosition     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            java.lang.String r2 = "saut"
            long r3 = r12.position()     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            java.lang.Long r3 = java.lang.Long.valueOf(r3)     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            r0.put(r2, r3)     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            com.sec.android.app.voicenote.service.codec.M4aInfo r0 = r1.inf     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = r0.customAtomLength     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            java.lang.String r2 = "saut"
            byte[] r3 = r1.SAUT     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            int r3 = r3.length     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            r0.put(r2, r3)     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            r12.close()     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            r10.close()     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            r11.close()     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            java.io.File r0 = new java.io.File     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            r2.<init>()     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            java.io.File r3 = android.os.Environment.getExternalStorageDirectory()     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            java.lang.String r3 = r3.getAbsolutePath()     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            r2.append(r3)     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            r2.append(r13)     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            java.lang.String r3 = "temp6546368.m4a"
            r2.append(r3)     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            java.lang.String r2 = r2.toString()     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            r0.<init>(r2)     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            boolean r0 = r0.delete()     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
            if (r0 != 0) goto L_0x0168
            java.lang.String r0 = "SmtaHelper"
            java.lang.String r2 = "delete temp file failed"
            com.sec.android.app.voicenote.provider.Log.m22e(r0, r2)     // Catch:{ FileNotFoundException -> 0x0174, IOException -> 0x016f, all -> 0x016c }
        L_0x0168:
            r3 = r10
            r4 = r12
            goto L_0x01ff
        L_0x016c:
            r0 = move-exception
            goto L_0x023a
        L_0x016f:
            r0 = move-exception
            r2 = r9
            r9 = r12
            goto L_0x020c
        L_0x0174:
            r0 = move-exception
            r2 = r9
            r9 = r12
            goto L_0x021d
        L_0x0179:
            r0 = move-exception
            r12 = r9
            goto L_0x023a
        L_0x017d:
            r0 = move-exception
            r2 = r9
            goto L_0x020c
        L_0x0181:
            r0 = move-exception
            r2 = r9
            goto L_0x021d
        L_0x0185:
            r0 = move-exception
        L_0x0186:
            r11 = r9
            goto L_0x0195
        L_0x0188:
            r0 = move-exception
        L_0x0189:
            r2 = r9
            r11 = r2
            goto L_0x020c
        L_0x018d:
            r0 = move-exception
        L_0x018e:
            r2 = r9
            r11 = r2
            goto L_0x021d
        L_0x0192:
            r0 = move-exception
            r10 = r9
            r11 = r10
        L_0x0195:
            r12 = r11
            goto L_0x023a
        L_0x0198:
            r0 = move-exception
            r2 = r9
            r10 = r2
            r11 = r10
            goto L_0x020c
        L_0x019e:
            r0 = move-exception
            r2 = r9
            r10 = r2
            r11 = r10
            goto L_0x021d
        L_0x01a4:
            if (r4 != r0) goto L_0x01f5
            java.io.RandomAccessFile r3 = new java.io.RandomAccessFile     // Catch:{ FileNotFoundException -> 0x019e, IOException -> 0x0198, all -> 0x0192 }
            com.sec.android.app.voicenote.service.codec.M4aInfo r0 = r1.inf     // Catch:{ FileNotFoundException -> 0x019e, IOException -> 0x0198, all -> 0x0192 }
            java.lang.String r0 = r0.path     // Catch:{ FileNotFoundException -> 0x019e, IOException -> 0x0198, all -> 0x0192 }
            java.lang.String r4 = "rw"
            r3.<init>(r0, r4)     // Catch:{ FileNotFoundException -> 0x019e, IOException -> 0x0198, all -> 0x0192 }
            java.nio.channels.FileChannel r4 = r3.getChannel()     // Catch:{ FileNotFoundException -> 0x01f2, IOException -> 0x01ef, all -> 0x01ec }
            com.sec.android.app.voicenote.service.codec.M4aInfo r0 = r1.inf     // Catch:{ FileNotFoundException -> 0x01e6, IOException -> 0x01e0, all -> 0x01da }
            java.util.HashMap<java.lang.String, java.lang.Long> r0 = r0.customAtomPosition     // Catch:{ FileNotFoundException -> 0x01e6, IOException -> 0x01e0, all -> 0x01da }
            java.lang.String r5 = "smta"
            java.lang.Object r0 = r0.get(r5)     // Catch:{ FileNotFoundException -> 0x01e6, IOException -> 0x01e0, all -> 0x01da }
            java.lang.Long r0 = (java.lang.Long) r0     // Catch:{ FileNotFoundException -> 0x01e6, IOException -> 0x01e0, all -> 0x01da }
            long r5 = r0.longValue()     // Catch:{ FileNotFoundException -> 0x01e6, IOException -> 0x01e0, all -> 0x01da }
            long r5 = r5 + r7
            r7 = 4
            long r5 = r5 + r7
            r4.position(r5)     // Catch:{ FileNotFoundException -> 0x01e6, IOException -> 0x01e0, all -> 0x01da }
            r4.write(r2)     // Catch:{ FileNotFoundException -> 0x01e6, IOException -> 0x01e0, all -> 0x01da }
            r4.close()     // Catch:{ FileNotFoundException -> 0x01e6, IOException -> 0x01e0, all -> 0x01da }
            r4.close()     // Catch:{ FileNotFoundException -> 0x01e6, IOException -> 0x01e0, all -> 0x01da }
            r3.close()     // Catch:{ FileNotFoundException -> 0x01e6, IOException -> 0x01e0, all -> 0x01da }
            r11 = r9
            goto L_0x01ff
        L_0x01da:
            r0 = move-exception
            r10 = r3
            r12 = r4
            r11 = r9
            goto L_0x023a
        L_0x01e0:
            r0 = move-exception
            r10 = r3
            r2 = r9
            r11 = r2
            r9 = r4
            goto L_0x020c
        L_0x01e6:
            r0 = move-exception
            r10 = r3
            r2 = r9
            r11 = r2
            r9 = r4
            goto L_0x021d
        L_0x01ec:
            r0 = move-exception
            r10 = r3
            goto L_0x0186
        L_0x01ef:
            r0 = move-exception
            r10 = r3
            goto L_0x0189
        L_0x01f2:
            r0 = move-exception
            r10 = r3
            goto L_0x018e
        L_0x01f5:
            java.lang.String r0 = "SmtaHelper"
            java.lang.String r2 = "stma is unexpected size"
            com.sec.android.app.voicenote.provider.Log.m32w((java.lang.String) r0, (java.lang.String) r2)     // Catch:{ FileNotFoundException -> 0x019e, IOException -> 0x0198, all -> 0x0192 }
            r3 = r9
            r4 = r3
            r11 = r4
        L_0x01ff:
            r1.closeQuietly(r4)     // Catch:{ all -> 0x0247 }
            r1.closeQuietly(r9)     // Catch:{ all -> 0x0247 }
            r1.closeQuietly(r3)     // Catch:{ all -> 0x0247 }
        L_0x0208:
            r1.closeQuietly(r11)     // Catch:{ all -> 0x0247 }
            goto L_0x022e
        L_0x020c:
            java.lang.String r3 = "SmtaHelper"
            java.lang.String r4 = "IOException"
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r3, (java.lang.String) r4, (java.lang.Throwable) r0)     // Catch:{ all -> 0x0237 }
            r1.closeQuietly(r9)     // Catch:{ all -> 0x0247 }
            r1.closeQuietly(r2)     // Catch:{ all -> 0x0247 }
            r1.closeQuietly(r10)     // Catch:{ all -> 0x0247 }
            goto L_0x0208
        L_0x021d:
            java.lang.String r3 = "SmtaHelper"
            java.lang.String r4 = "FileNotFoundException"
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r3, (java.lang.String) r4, (java.lang.Throwable) r0)     // Catch:{ all -> 0x0237 }
            r1.closeQuietly(r9)     // Catch:{ all -> 0x0247 }
            r1.closeQuietly(r2)     // Catch:{ all -> 0x0247 }
            r1.closeQuietly(r10)     // Catch:{ all -> 0x0247 }
            goto L_0x0208
        L_0x022e:
            java.lang.String r0 = "SmtaHelper"
            java.lang.String r2 = "overwrite has ended"
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r2)     // Catch:{ all -> 0x0247 }
            monitor-exit(r21)
            return
        L_0x0237:
            r0 = move-exception
            r12 = r9
            r9 = r2
        L_0x023a:
            r1.closeQuietly(r12)     // Catch:{ all -> 0x0247 }
            r1.closeQuietly(r9)     // Catch:{ all -> 0x0247 }
            r1.closeQuietly(r10)     // Catch:{ all -> 0x0247 }
            r1.closeQuietly(r11)     // Catch:{ all -> 0x0247 }
            throw r0     // Catch:{ all -> 0x0247 }
        L_0x0247:
            r0 = move-exception
            monitor-exit(r21)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.helper.SmtaHelper.overwrite(int):void");
    }

    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                Log.m24e(TAG, "closeQuietly fail - class : " + closeable.getClass().getSimpleName(), (Throwable) e);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void updateOuterAtomsLengths(FileChannel fileChannel, int i) {
        try {
            ByteBuffer allocate = ByteBuffer.allocate(4);
            allocate.putInt(this.inf.fileMoovLength + i);
            allocate.rewind();
            fileChannel.position(this.inf.moovPos);
            fileChannel.write(allocate);
            allocate.rewind();
            allocate.putInt(this.inf.fileUdtaLength + i);
            allocate.rewind();
            fileChannel.position(this.inf.udtaPos);
            fileChannel.write(allocate);
            allocate.rewind();
            allocate.rewind();
            allocate.putInt(this.inf.customAtomLength.get("smta").intValue() + i);
            allocate.rewind();
            fileChannel.position(this.mSmtaPos);
            fileChannel.write(allocate);
            allocate.rewind();
        } catch (IOException e) {
            Log.m24e(TAG, "updateOuterAtomsLengths - Some other exception", (Throwable) e);
        }
    }
}
