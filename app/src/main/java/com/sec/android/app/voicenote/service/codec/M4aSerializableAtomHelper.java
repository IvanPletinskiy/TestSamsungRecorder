package com.sec.android.app.voicenote.service.codec;

import android.os.Environment;
import com.sec.android.app.voicenote.provider.Log;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public abstract class M4aSerializableAtomHelper {
    private static final String TAG = "M4aSerializableAtom";
    private static final String TEMP_NAME = ".temp3223293.m4a";
    protected M4aInfo inf = null;
    private boolean invalidInit;
    private int newAtomLength = 0;
    private int oldAtomLength = 0;

    public interface ObjectConstructorStub {
        Object newInstance(Object obj);
    }

    public M4aSerializableAtomHelper(M4aInfo m4aInfo) {
        if (m4aInfo != null) {
            this.inf = m4aInfo;
            this.invalidInit = m4aInfo.usedToWrite;
            return;
        }
        this.invalidInit = true;
    }

    public static void closeQuietly(AutoCloseable autoCloseable) {
        if (autoCloseable != null) {
            try {
                autoCloseable.close();
            } catch (Exception unused) {
                Log.m22e(TAG, " close fail : " + autoCloseable.getClass().getSimpleName());
            }
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x005a  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0060  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean overwriteAtom(java.io.Serializable r25, long r26, java.nio.ByteBuffer r28) {
        /*
            r24 = this;
            r1 = r24
            r2 = r26
            r4 = r28
            java.lang.String r5 = ".temp3223293.m4a"
            java.lang.String r6 = "rw"
            boolean r0 = r1.invalidInit
            r7 = 0
            java.lang.String r8 = "M4aSerializableAtom"
            if (r0 == 0) goto L_0x0017
            java.lang.String r0 = "overwriteAtom() invalid init false"
            com.sec.android.app.voicenote.provider.Log.m29v(r8, r0)
            return r7
        L_0x0017:
            com.sec.android.app.voicenote.service.codec.M4aInfo r0 = r1.inf
            r9 = 1
            r0.usedToWrite = r9
            java.io.ByteArrayOutputStream r10 = new java.io.ByteArrayOutputStream
            r10.<init>()
            r11 = 4
            java.nio.ByteBuffer r12 = java.nio.ByteBuffer.allocate(r11)
            r0 = 8
            r1.newAtomLength = r0
            java.io.ObjectOutputStream r14 = new java.io.ObjectOutputStream     // Catch:{ IOException -> 0x0048, all -> 0x0044 }
            r14.<init>(r10)     // Catch:{ IOException -> 0x0048, all -> 0x0044 }
            r0 = r25
            r14.writeObject(r0)     // Catch:{ IOException -> 0x0042 }
            byte[] r0 = r10.toByteArray()     // Catch:{ IOException -> 0x0042 }
            closeQuietly(r10)
            closeQuietly(r14)
            goto L_0x0058
        L_0x003f:
            r0 = move-exception
            goto L_0x0235
        L_0x0042:
            r0 = move-exception
            goto L_0x004a
        L_0x0044:
            r0 = move-exception
            r14 = 0
            goto L_0x0235
        L_0x0048:
            r0 = move-exception
            r14 = 0
        L_0x004a:
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x003f }
            com.sec.android.app.voicenote.provider.Log.m22e(r8, r0)     // Catch:{ all -> 0x003f }
            closeQuietly(r10)
            closeQuietly(r14)
            r0 = 0
        L_0x0058:
            if (r0 != 0) goto L_0x0060
            java.lang.String r0 = "overwriteAtom() data bytes is null"
            com.sec.android.app.voicenote.provider.Log.m29v(r8, r0)
            return r7
        L_0x0060:
            int r10 = r1.newAtomLength
            int r14 = r0.length
            int r10 = r10 + r14
            r1.newAtomLength = r10
            java.nio.ByteBuffer r10 = java.nio.ByteBuffer.wrap(r0)
            java.io.RandomAccessFile r14 = new java.io.RandomAccessFile     // Catch:{ IOException -> 0x01fd, all -> 0x01f6 }
            com.sec.android.app.voicenote.service.codec.M4aInfo r0 = r1.inf     // Catch:{ IOException -> 0x01fd, all -> 0x01f6 }
            java.lang.String r0 = r0.path     // Catch:{ IOException -> 0x01fd, all -> 0x01f6 }
            r14.<init>(r0, r6)     // Catch:{ IOException -> 0x01fd, all -> 0x01f6 }
            java.io.RandomAccessFile r15 = new java.io.RandomAccessFile     // Catch:{ IOException -> 0x01f3, all -> 0x01f0 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01f3, all -> 0x01f0 }
            r0.<init>()     // Catch:{ IOException -> 0x01f3, all -> 0x01f0 }
            java.io.File r16 = android.os.Environment.getExternalStorageDirectory()     // Catch:{ IOException -> 0x01f3, all -> 0x01f0 }
            java.lang.String r13 = r16.getAbsolutePath()     // Catch:{ IOException -> 0x01f3, all -> 0x01f0 }
            r0.append(r13)     // Catch:{ IOException -> 0x01f3, all -> 0x01f0 }
            r13 = 47
            r0.append(r13)     // Catch:{ IOException -> 0x01f3, all -> 0x01f0 }
            r0.append(r5)     // Catch:{ IOException -> 0x01f3, all -> 0x01f0 }
            java.lang.String r0 = r0.toString()     // Catch:{ IOException -> 0x01f3, all -> 0x01f0 }
            r15.<init>(r0, r6)     // Catch:{ IOException -> 0x01f3, all -> 0x01f0 }
            java.nio.channels.FileChannel r6 = r14.getChannel()     // Catch:{ IOException -> 0x01ed, all -> 0x01ea }
            java.nio.channels.FileChannel r16 = r15.getChannel()     // Catch:{ IOException -> 0x01e7, all -> 0x01e5 }
            int r0 = r6.read(r12, r2)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            if (r0 >= 0) goto L_0x00b4
            java.lang.String r0 = "overwriteAtom() readSize is under 0"
            com.sec.android.app.voicenote.provider.Log.m22e(r8, r0)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            closeQuietly(r6)
            closeQuietly(r16)
            closeQuietly(r14)
            closeQuietly(r15)
            return r7
        L_0x00b4:
            r12.rewind()     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            int r0 = r12.getInt()     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r1.oldAtomLength = r0     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r12.rewind()     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r17 = 4
            r25 = r10
            long r9 = r2 + r17
            int r0 = r6.read(r12, r9)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            if (r0 >= 0) goto L_0x00de
            java.lang.String r0 = "overwriteAtom() readSize2 is under 0"
            com.sec.android.app.voicenote.provider.Log.m22e(r8, r0)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            closeQuietly(r6)
            closeQuietly(r16)
            closeQuietly(r14)
            closeQuietly(r15)
            return r7
        L_0x00de:
            byte[] r0 = new byte[r11]     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            byte[] r9 = new byte[r11]     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r4.position(r11)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r12.rewind()     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r10 = r7
        L_0x00e9:
            if (r10 >= r11) goto L_0x0105
            byte r17 = r12.get()     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r0[r10] = r17     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            byte r17 = r28.get()     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r9[r10] = r17     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            byte r11 = r0[r10]     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            byte r13 = r9[r10]     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            if (r11 == r13) goto L_0x00ff
            r0 = r7
            goto L_0x0106
        L_0x00ff:
            int r10 = r10 + 1
            r11 = 4
            r13 = 47
            goto L_0x00e9
        L_0x0105:
            r0 = 1
        L_0x0106:
            r9 = 0
            if (r0 == 0) goto L_0x013b
            int r0 = r1.oldAtomLength     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            long r11 = (long) r0     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            long r11 = r11 + r2
            r6.position(r11)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r20 = 0
            long r22 = r6.size()     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r18 = r16
            r19 = r6
            long r11 = r18.transferFrom(r19, r20, r22)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            int r0 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r0 >= 0) goto L_0x0137
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r0.<init>()     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            java.lang.String r13 = "overwriteAtom - dst.transferFrom : "
            r0.append(r13)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r0.append(r11)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            java.lang.String r0 = r0.toString()     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            com.sec.android.app.voicenote.provider.Log.m22e(r8, r0)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
        L_0x0137:
            r6.position(r2)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            goto L_0x0172
        L_0x013b:
            r1.oldAtomLength = r7     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r6.position(r2)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r20 = 0
            long r22 = r6.size()     // Catch:{ IllegalArgumentException -> 0x016a }
            r18 = r16
            r19 = r6
            long r11 = r18.transferFrom(r19, r20, r22)     // Catch:{ IllegalArgumentException -> 0x016a }
            int r0 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r0 >= 0) goto L_0x0166
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ IllegalArgumentException -> 0x016a }
            r0.<init>()     // Catch:{ IllegalArgumentException -> 0x016a }
            java.lang.String r13 = "overwriteAtom - transferFromSize : "
            r0.append(r13)     // Catch:{ IllegalArgumentException -> 0x016a }
            r0.append(r11)     // Catch:{ IllegalArgumentException -> 0x016a }
            java.lang.String r0 = r0.toString()     // Catch:{ IllegalArgumentException -> 0x016a }
            com.sec.android.app.voicenote.provider.Log.m22e(r8, r0)     // Catch:{ IllegalArgumentException -> 0x016a }
        L_0x0166:
            r6.position(r2)     // Catch:{ IllegalArgumentException -> 0x016a }
            goto L_0x0172
        L_0x016a:
            r0 = move-exception
            java.lang.String r0 = r0.toString()     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            com.sec.android.app.voicenote.provider.Log.m22e(r8, r0)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
        L_0x0172:
            r28.rewind()     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            int r0 = r1.newAtomLength     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r4.putInt(r0)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r28.rewind()     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r6.write(r4)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r2 = r25
            r6.write(r2)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            long r20 = r6.position()     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            long r22 = r16.size()     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r18 = r6
            r19 = r16
            long r2 = r18.transferFrom(r19, r20, r22)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            int r0 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r0 >= 0) goto L_0x01ad
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r0.<init>()     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            java.lang.String r4 = "overwriteAtom - srcWrite.transferFrom : "
            r0.append(r4)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r0.append(r2)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            java.lang.String r0 = r0.toString()     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            com.sec.android.app.voicenote.provider.Log.m22e(r8, r0)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
        L_0x01ad:
            r1.updateOuterAtomsLengths(r6)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            java.io.File r0 = new java.io.File     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r2.<init>()     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            java.io.File r3 = android.os.Environment.getExternalStorageDirectory()     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            java.lang.String r3 = r3.getAbsolutePath()     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r2.append(r3)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r3 = 47
            r2.append(r3)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r2.append(r5)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            java.lang.String r2 = r2.toString()     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            r0.<init>(r2)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            boolean r0 = r0.delete()     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
            if (r0 != 0) goto L_0x01dc
            java.lang.String r0 = "FAIL toDelete.delete"
            com.sec.android.app.voicenote.provider.Log.m22e(r8, r0)     // Catch:{ IOException -> 0x01e2, all -> 0x01e0 }
        L_0x01dc:
            closeQuietly(r6)
            goto L_0x0212
        L_0x01e0:
            r0 = move-exception
            goto L_0x0228
        L_0x01e2:
            r0 = move-exception
            r13 = r6
            goto L_0x0203
        L_0x01e5:
            r0 = move-exception
            goto L_0x01fa
        L_0x01e7:
            r0 = move-exception
            r13 = r6
            goto L_0x0201
        L_0x01ea:
            r0 = move-exception
            r6 = 0
            goto L_0x01fa
        L_0x01ed:
            r0 = move-exception
            r13 = 0
            goto L_0x0201
        L_0x01f0:
            r0 = move-exception
            r6 = 0
            goto L_0x01f9
        L_0x01f3:
            r0 = move-exception
            r13 = 0
            goto L_0x0200
        L_0x01f6:
            r0 = move-exception
            r6 = 0
            r14 = 0
        L_0x01f9:
            r15 = 0
        L_0x01fa:
            r16 = 0
            goto L_0x0228
        L_0x01fd:
            r0 = move-exception
            r13 = 0
            r14 = 0
        L_0x0200:
            r15 = 0
        L_0x0201:
            r16 = 0
        L_0x0203:
            java.lang.String r2 = "Error writing atom to file"
            com.sec.android.app.voicenote.provider.Log.m22e(r8, r2)     // Catch:{ all -> 0x0226 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0226 }
            com.sec.android.app.voicenote.provider.Log.m22e(r8, r0)     // Catch:{ all -> 0x0226 }
            closeQuietly(r13)
        L_0x0212:
            closeQuietly(r16)
            closeQuietly(r14)
            closeQuietly(r15)
            java.lang.String r0 = "overwriteAtom has ended"
            com.sec.android.app.voicenote.provider.Log.m26i(r8, r0)
            com.sec.android.app.voicenote.service.codec.M4aInfo r0 = r1.inf
            r0.usedToWrite = r7
            r2 = 1
            return r2
        L_0x0226:
            r0 = move-exception
            r6 = r13
        L_0x0228:
            closeQuietly(r6)
            closeQuietly(r16)
            closeQuietly(r14)
            closeQuietly(r15)
            throw r0
        L_0x0235:
            closeQuietly(r10)
            closeQuietly(r14)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.codec.M4aSerializableAtomHelper.overwriteAtom(java.io.Serializable, long, java.nio.ByteBuffer):boolean");
    }

    /* access modifiers changed from: protected */
    public final Serializable readAtom(long j) {
        FileChannel fileChannel;
        RandomAccessFile randomAccessFile;
        ObjectInputStream objectInputStream;
        Serializable serializable = null;
        if (this.invalidInit) {
            return null;
        }
        try {
            randomAccessFile = new RandomAccessFile(this.inf.path, "r");
            try {
                fileChannel = randomAccessFile.getChannel();
                try {
                    ByteBuffer allocate = ByteBuffer.allocate(4);
                    fileChannel.position(j);
                    if (fileChannel.read(allocate) < 0) {
                        Log.m22e(TAG, "read() countRead is under 0");
                    } else {
                        allocate.rewind();
                        ByteBuffer allocate2 = ByteBuffer.allocate(allocate.getInt() - 8);
                        fileChannel.position(j + 8);
                        if (fileChannel.read(allocate2) < 0) {
                            Log.m22e(TAG, "read2() countRead is under 0");
                        } else {
                            allocate2.rewind();
                            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(allocate2.array());
                            try {
                                objectInputStream = new ObjectInputStream(byteArrayInputStream);
                                try {
                                    Serializable serializable2 = (Serializable) objectInputStream.readObject();
                                    closeQuietly(byteArrayInputStream);
                                    closeQuietly(objectInputStream);
                                    serializable = serializable2;
                                } catch (ClassNotFoundException e) {
                                    e = e;
                                    try {
                                        Log.m22e(TAG, e.toString());
                                        closeQuietly(byteArrayInputStream);
                                        closeQuietly(objectInputStream);
                                        closeQuietly(fileChannel);
                                        closeQuietly(randomAccessFile);
                                        return serializable;
                                    } catch (Throwable th) {
                                        th = th;
                                        closeQuietly(byteArrayInputStream);
                                        closeQuietly(objectInputStream);
                                        throw th;
                                    }
                                }
                            } catch (ClassNotFoundException e2) {
//                                e = e2;
                                objectInputStream = null;
//                                Log.m22e(TAG, e.toString());
                                closeQuietly(byteArrayInputStream);
                                closeQuietly(objectInputStream);
                                closeQuietly(fileChannel);
                                closeQuietly(randomAccessFile);
                                return serializable;
                            } catch (Throwable th2) {
//                                th = th2;
                                objectInputStream = null;
                                closeQuietly(byteArrayInputStream);
                                closeQuietly(objectInputStream);
//                                throw th;
                            }
                            closeQuietly(fileChannel);
                            closeQuietly(randomAccessFile);
                            return serializable;
                        }
                    }
                    closeQuietly(fileChannel);
                    closeQuietly(randomAccessFile);
                    return null;
                } catch (IOException e3) {
//                    e = e3;
                    try {
                        Log.m22e(TAG, "Error reading data from file");
//                        Log.m22e(TAG, e.toString());
                        closeQuietly(fileChannel);
                        closeQuietly(randomAccessFile);
                        return serializable;
                    } catch (Throwable th3) {
//                        th = th3;
                        closeQuietly(fileChannel);
                        closeQuietly(randomAccessFile);
//                        throw th;
                    }
                }
//            } catch (IOException e4) {
////                e = e4;
//                fileChannel = null;
//                Log.m22e(TAG, "Error reading data from file");
////                Log.m22e(TAG, e.toString());
//                closeQuietly(fileChannel);
//                closeQuietly(randomAccessFile);
//                return serializable;
//            } catch (Throwable th4) {
            } catch (Throwable th4) {
//                th = th4;
                fileChannel = null;
                closeQuietly(fileChannel);
                closeQuietly(randomAccessFile);
//                throw th;
            }
        } catch (IOException e5) {
//            e = e5;
            randomAccessFile = null;
            fileChannel = null;
            Log.m22e(TAG, "Error reading data from file");
//            Log.m22e(TAG, e.toString());
            closeQuietly(fileChannel);
            closeQuietly(randomAccessFile);
            return serializable;
        } catch (Throwable th5) {
//            th = th5;
            randomAccessFile = null;
            fileChannel = null;
            closeQuietly(fileChannel);
            closeQuietly(randomAccessFile);
//            throw th;
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public final void removeAtom(long j) {
        RandomAccessFile randomAccessFile;
        RandomAccessFile randomAccessFile2;
        FileChannel fileChannel = null;
        FileChannel fileChannel2 = null;
        long j2 = j;
        if (!this.invalidInit) {
            ByteBuffer allocate = ByteBuffer.allocate(4);
            FileChannel fileChannel3 = null;
            try {
                randomAccessFile2 = new RandomAccessFile(this.inf.path, "rw");
                try {
                    randomAccessFile = new RandomAccessFile(Environment.getExternalStorageDirectory().getAbsolutePath() + '/' + TEMP_NAME, "rw");
                    try {
                        fileChannel = randomAccessFile2.getChannel();
//                    } catch (IOException e) {
//                        e = e;
//                        fileChannel2 = null;
//                        try {
//                            Log.m22e(TAG, "Error removing atom from file");
//                            Log.m22e(TAG, e.toString());
//                            closeQuietly(fileChannel3);
//                            closeQuietly(fileChannel2);
//                            closeQuietly(randomAccessFile2);
//                            closeQuietly(randomAccessFile);
//                        } catch (Throwable th) {
//                            th = th;
//                            fileChannel = fileChannel3;
//                            fileChannel3 = fileChannel2;
//                            closeQuietly(fileChannel);
//                            closeQuietly(fileChannel3);
//                            closeQuietly(randomAccessFile2);
//                            closeQuietly(randomAccessFile);
//                            throw th;
//                        }
//                    } catch (Throwable th2) {
                    } catch (Throwable th2) {
//                        th = th2;
                        fileChannel = null;
                        closeQuietly(fileChannel);
                        closeQuietly(fileChannel3);
                        closeQuietly(randomAccessFile2);
                        closeQuietly(randomAccessFile);
//                        throw th;
                    }
                } catch (IOException e2) {
//                    e = e2;
                    fileChannel2 = null;
                    randomAccessFile = null;
                    Log.m22e(TAG, "Error removing atom from file");
//                    Log.m22e(TAG, e.toString());
                    closeQuietly(fileChannel3);
                    closeQuietly(fileChannel2);
                    closeQuietly(randomAccessFile2);
                    closeQuietly(randomAccessFile);
                } catch (Throwable th3) {
//                    th = th3;
                    fileChannel = null;
                    randomAccessFile = null;
                    closeQuietly(fileChannel);
                    closeQuietly(fileChannel3);
                    closeQuietly(randomAccessFile2);
                    closeQuietly(randomAccessFile);
//                    throw th;
                }
                try {
                    FileChannel channel = randomAccessFile.getChannel();
                    if (fileChannel.read(allocate, j2) < 0) {
                        Log.m22e(TAG, "removeAtom() readSize is under 0");
                    }
                    allocate.rewind();
                    this.oldAtomLength = allocate.getInt();
                    fileChannel.position(((long) this.oldAtomLength) + j2);
                    long transferFrom = channel.transferFrom(fileChannel, 0, fileChannel.size());
                    if (transferFrom < 0) {
                        Log.m22e(TAG, "overwriteAtom - dst.transferFrom : " + transferFrom);
                    }
                    fileChannel.position(j2);
                    long transferFrom2 = fileChannel.transferFrom(channel, fileChannel.position(), channel.size());
                    if (transferFrom2 < 0) {
                        Log.m22e(TAG, "overwriteAtom - srcWrite.transferFrom : " + transferFrom2);
                    }
                    fileChannel.truncate(fileChannel.position() + channel.size());
                    this.newAtomLength = 0;
                    updateOuterAtomsLengths(fileChannel);
                    this.inf.usedToWrite = true;
                    this.invalidInit = true;
                    if (!new File(Environment.getExternalStorageDirectory().getAbsolutePath() + '/' + TEMP_NAME).delete()) {
                        Log.m22e(TAG, "FAIL toDelete.delete");
                    }
                    closeQuietly(fileChannel);
                    closeQuietly(channel);
                } catch (IOException e3) {
//                    e = e3;
                    fileChannel2 = null;
                    fileChannel3 = fileChannel;
                    Log.m22e(TAG, "Error removing atom from file");
//                    Log.m22e(TAG, e.toString());
                    closeQuietly(fileChannel3);
                    closeQuietly(fileChannel2);
                    closeQuietly(randomAccessFile2);
                    closeQuietly(randomAccessFile);
                } catch (Throwable th4) {
//                    th = th4;
                    closeQuietly(fileChannel);
                    closeQuietly(fileChannel3);
                    closeQuietly(randomAccessFile2);
                    closeQuietly(randomAccessFile);
//                    throw th;
                }
            } catch (IOException e4) {
//                e = e4;
                fileChannel2 = null;
                randomAccessFile2 = null;
                randomAccessFile = null;
                Log.m22e(TAG, "Error removing atom from file");
//                Log.m22e(TAG, e.toString());
                closeQuietly(fileChannel3);
                closeQuietly(fileChannel2);
                closeQuietly(randomAccessFile2);
                closeQuietly(randomAccessFile);
            } catch (Throwable th5) {
//                th = th5;
                fileChannel = null;
                randomAccessFile2 = null;
                randomAccessFile = null;
                closeQuietly(fileChannel);
                closeQuietly(fileChannel3);
                closeQuietly(randomAccessFile2);
                closeQuietly(randomAccessFile);
//                throw th;
            }
            closeQuietly(randomAccessFile2);
            closeQuietly(randomAccessFile);
        }
    }

    private void updateOuterAtomsLengths(FileChannel fileChannel) {
        ByteBuffer allocate = ByteBuffer.allocate(4);
        M4aInfo m4aInfo = this.inf;
        int i = m4aInfo.fileUdtaLength;
        int i2 = (i - this.oldAtomLength) + this.newAtomLength;
        int i3 = (m4aInfo.fileMoovLength - i) + i2;
        m4aInfo.fileUdtaLength = i2;
        m4aInfo.fileMoovLength = i3;
        try {
            long position = fileChannel.position();
            allocate.putInt(i2);
            allocate.rewind();
            fileChannel.position(this.inf.udtaPos);
            fileChannel.write(allocate);
            allocate.rewind();
            allocate.putInt(i3);
            allocate.rewind();
            fileChannel.position(this.inf.moovPos);
            fileChannel.write(allocate);
            allocate.rewind();
            fileChannel.position(position);
        } catch (IOException e) {
            Log.m22e(TAG, e.toString());
        }
    }
}
