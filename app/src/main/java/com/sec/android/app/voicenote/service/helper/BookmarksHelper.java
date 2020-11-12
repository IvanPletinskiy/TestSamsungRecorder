package com.sec.android.app.voicenote.service.helper;

import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.codec.M4aConsts;
import com.sec.android.app.voicenote.service.codec.M4aInfo;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookmarksHelper {
    private static final String STRING_CODING = "UTF-16BE";
    private static final String TAG = "BookmarksHelper";
    private static final String TEMP_NAME = "temp6546368.m4a";
    private int bookLength;
    private int bookmarksCount;
    private M4aInfo inf = null;
    private boolean invalidInit;
    private final byte[] newBKMK = {0, 0, 2, 100, 98, 107, 109, 107};
    private final byte[] newBNUM = {0, 0, 0, 8, 98, 110, 117, 109};
    private final byte[] shortBook = {0, 0, 119, -104, 98, 111, 111, 107};

    public BookmarksHelper(M4aInfo m4aInfo) {
        if (m4aInfo != null) {
            this.inf = m4aInfo;
            this.invalidInit = m4aInfo.usedToWrite;
            this.bookmarksCount = (m4aInfo.customAtomLength.get(M4aConsts.BOOKMARKS_NUMBER).intValue() - 8) / M4aConsts.BKMK_LENGTH;
            this.bookLength = 30616;
            return;
        }
        this.invalidInit = true;
    }

    public int getBookmarksCount() {
        return this.bookmarksCount;
    }

    /* JADX WARNING: type inference failed for: r4v0 */
    /* JADX WARNING: type inference failed for: r4v2 */
    /* JADX WARNING: type inference failed for: r4v29 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void overwrite(java.util.List<com.sec.android.app.voicenote.service.helper.Bookmark> r24) {
        /*
            r23 = this;
            r1 = r23
            r2 = r24
            boolean r3 = r1.invalidInit
            if (r3 == 0) goto L_0x0009
            return
        L_0x0009:
            com.sec.android.app.voicenote.service.codec.M4aInfo r3 = r1.inf
            r4 = 1
            r3.usedToWrite = r4
            byte[] r3 = r1.newBKMK
            java.nio.ByteBuffer r3 = java.nio.ByteBuffer.wrap(r3)
            r5 = 612(0x264, float:8.58E-43)
            r3.putInt(r5)
            r5 = 4
            java.nio.ByteBuffer r5 = java.nio.ByteBuffer.allocate(r5)
            com.sec.android.app.voicenote.service.codec.M4aInfo r7 = r1.inf     // Catch:{ FileNotFoundException -> 0x033a, IOException -> 0x032c, all -> 0x0325 }
            java.util.HashMap<java.lang.String, java.lang.Boolean> r7 = r7.hasCustomAtom     // Catch:{ FileNotFoundException -> 0x033a, IOException -> 0x032c, all -> 0x0325 }
            java.lang.String r8 = "book"
            java.lang.Object r7 = r7.get(r8)     // Catch:{ FileNotFoundException -> 0x033a, IOException -> 0x032c, all -> 0x0325 }
            java.lang.Boolean r7 = (java.lang.Boolean) r7     // Catch:{ FileNotFoundException -> 0x033a, IOException -> 0x032c, all -> 0x0325 }
            boolean r7 = r7.booleanValue()     // Catch:{ FileNotFoundException -> 0x033a, IOException -> 0x032c, all -> 0x0325 }
            r8 = 8
            r10 = 500(0x1f4, float:7.0E-43)
            r11 = 100
            r12 = 50
            r13 = 0
            if (r7 == 0) goto L_0x011d
            java.io.RandomAccessFile r4 = new java.io.RandomAccessFile     // Catch:{ FileNotFoundException -> 0x033a, IOException -> 0x032c, all -> 0x0325 }
            com.sec.android.app.voicenote.service.codec.M4aInfo r7 = r1.inf     // Catch:{ FileNotFoundException -> 0x033a, IOException -> 0x032c, all -> 0x0325 }
            java.lang.String r7 = r7.path     // Catch:{ FileNotFoundException -> 0x033a, IOException -> 0x032c, all -> 0x0325 }
            java.lang.String r14 = "rw"
            r4.<init>(r7, r14)     // Catch:{ FileNotFoundException -> 0x033a, IOException -> 0x032c, all -> 0x0325 }
            java.nio.channels.FileChannel r7 = r4.getChannel()     // Catch:{ FileNotFoundException -> 0x0119, IOException -> 0x0115, all -> 0x0111 }
            com.sec.android.app.voicenote.service.codec.M4aInfo r14 = r1.inf     // Catch:{ FileNotFoundException -> 0x010c, IOException -> 0x0107, all -> 0x0102 }
            java.util.HashMap<java.lang.String, java.lang.Long> r14 = r14.customAtomPosition     // Catch:{ FileNotFoundException -> 0x010c, IOException -> 0x0107, all -> 0x0102 }
            java.lang.String r15 = "bnum"
            java.lang.Object r14 = r14.get(r15)     // Catch:{ FileNotFoundException -> 0x010c, IOException -> 0x0107, all -> 0x0102 }
            java.lang.Long r14 = (java.lang.Long) r14     // Catch:{ FileNotFoundException -> 0x010c, IOException -> 0x0107, all -> 0x0102 }
            long r14 = r14.longValue()     // Catch:{ FileNotFoundException -> 0x010c, IOException -> 0x0107, all -> 0x0102 }
            long r14 = r14 + r8
            r7.position(r14)     // Catch:{ FileNotFoundException -> 0x010c, IOException -> 0x0107, all -> 0x0102 }
            monitor-enter(r24)     // Catch:{ FileNotFoundException -> 0x010c, IOException -> 0x0107, all -> 0x0102 }
            int r8 = r24.size()     // Catch:{ all -> 0x00fe }
            if (r8 < r12) goto L_0x0064
            r8 = r12
        L_0x0064:
            r9 = r13
        L_0x0065:
            if (r9 >= r8) goto L_0x00b4
            r3.rewind()     // Catch:{ all -> 0x00fe }
            r5.rewind()     // Catch:{ all -> 0x00fe }
            r7.write(r3)     // Catch:{ all -> 0x00fe }
            java.lang.Object r14 = r2.get(r9)     // Catch:{ all -> 0x00fe }
            com.sec.android.app.voicenote.service.helper.Bookmark r14 = (com.sec.android.app.voicenote.service.helper.Bookmark) r14     // Catch:{ all -> 0x00fe }
            int r14 = r14.getElapsed()     // Catch:{ all -> 0x00fe }
            r5.putInt(r14)     // Catch:{ all -> 0x00fe }
            r5.rewind()     // Catch:{ all -> 0x00fe }
            r7.write(r5)     // Catch:{ all -> 0x00fe }
            java.lang.Object r14 = r2.get(r9)     // Catch:{ all -> 0x00fe }
            com.sec.android.app.voicenote.service.helper.Bookmark r14 = (com.sec.android.app.voicenote.service.helper.Bookmark) r14     // Catch:{ all -> 0x00fe }
            java.lang.String r14 = r14.getTitle()     // Catch:{ all -> 0x00fe }
            java.lang.String r15 = "UTF-16BE"
            byte[] r14 = r1.strToByte(r14, r11, r15)     // Catch:{ all -> 0x00fe }
            java.nio.ByteBuffer r14 = java.nio.ByteBuffer.wrap(r14)     // Catch:{ all -> 0x00fe }
            r7.write(r14)     // Catch:{ all -> 0x00fe }
            java.lang.Object r14 = r2.get(r9)     // Catch:{ all -> 0x00fe }
            com.sec.android.app.voicenote.service.helper.Bookmark r14 = (com.sec.android.app.voicenote.service.helper.Bookmark) r14     // Catch:{ all -> 0x00fe }
            java.lang.String r14 = r14.getDescription()     // Catch:{ all -> 0x00fe }
            java.lang.String r15 = "UTF-16BE"
            byte[] r14 = r1.strToByte(r14, r10, r15)     // Catch:{ all -> 0x00fe }
            java.nio.ByteBuffer r14 = java.nio.ByteBuffer.wrap(r14)     // Catch:{ all -> 0x00fe }
            r7.write(r14)     // Catch:{ all -> 0x00fe }
            int r9 = r9 + 1
            goto L_0x0065
        L_0x00b4:
            r1.bookmarksCount = r8     // Catch:{ all -> 0x00fe }
            monitor-exit(r24)     // Catch:{ all -> 0x00fe }
            if (r8 >= r12) goto L_0x00f0
            int r12 = r12 - r8
            r2 = r13
        L_0x00bb:
            if (r2 >= r12) goto L_0x00f0
            r3.rewind()     // Catch:{ FileNotFoundException -> 0x010c, IOException -> 0x0107, all -> 0x0102 }
            r5.rewind()     // Catch:{ FileNotFoundException -> 0x010c, IOException -> 0x0107, all -> 0x0102 }
            r7.write(r3)     // Catch:{ FileNotFoundException -> 0x010c, IOException -> 0x0107, all -> 0x0102 }
            r5.putInt(r13)     // Catch:{ FileNotFoundException -> 0x010c, IOException -> 0x0107, all -> 0x0102 }
            r5.rewind()     // Catch:{ FileNotFoundException -> 0x010c, IOException -> 0x0107, all -> 0x0102 }
            r7.write(r5)     // Catch:{ FileNotFoundException -> 0x010c, IOException -> 0x0107, all -> 0x0102 }
            java.lang.String r8 = "empty title"
            java.lang.String r9 = "UTF-16BE"
            byte[] r8 = r1.strToByte(r8, r11, r9)     // Catch:{ FileNotFoundException -> 0x010c, IOException -> 0x0107, all -> 0x0102 }
            java.nio.ByteBuffer r8 = java.nio.ByteBuffer.wrap(r8)     // Catch:{ FileNotFoundException -> 0x010c, IOException -> 0x0107, all -> 0x0102 }
            r7.write(r8)     // Catch:{ FileNotFoundException -> 0x010c, IOException -> 0x0107, all -> 0x0102 }
            java.lang.String r8 = "empty description"
            java.lang.String r9 = "UTF-16BE"
            byte[] r8 = r1.strToByte(r8, r10, r9)     // Catch:{ FileNotFoundException -> 0x010c, IOException -> 0x0107, all -> 0x0102 }
            java.nio.ByteBuffer r8 = java.nio.ByteBuffer.wrap(r8)     // Catch:{ FileNotFoundException -> 0x010c, IOException -> 0x0107, all -> 0x0102 }
            r7.write(r8)     // Catch:{ FileNotFoundException -> 0x010c, IOException -> 0x0107, all -> 0x0102 }
            int r2 = r2 + 1
            goto L_0x00bb
        L_0x00f0:
            r1.updateBnum(r7)     // Catch:{ FileNotFoundException -> 0x010c, IOException -> 0x0107, all -> 0x0102 }
            r7.close()     // Catch:{ FileNotFoundException -> 0x010c, IOException -> 0x0107, all -> 0x0102 }
            r4.close()     // Catch:{ FileNotFoundException -> 0x010c, IOException -> 0x0107, all -> 0x0102 }
            r6 = r7
            r7 = 0
            r14 = 0
            goto L_0x0347
        L_0x00fe:
            r0 = move-exception
            r3 = r0
            monitor-exit(r24)     // Catch:{ all -> 0x00fe }
            throw r3     // Catch:{ FileNotFoundException -> 0x010c, IOException -> 0x0107, all -> 0x0102 }
        L_0x0102:
            r0 = move-exception
            r2 = r0
            r6 = r7
            goto L_0x0329
        L_0x0107:
            r0 = move-exception
            r2 = r0
            r6 = r7
            goto L_0x0330
        L_0x010c:
            r0 = move-exception
            r2 = r0
            r6 = r7
            goto L_0x033e
        L_0x0111:
            r0 = move-exception
            r2 = r0
            goto L_0x0328
        L_0x0115:
            r0 = move-exception
            r2 = r0
            goto L_0x032f
        L_0x0119:
            r0 = move-exception
            r2 = r0
            goto L_0x033d
        L_0x011d:
            com.sec.android.app.voicenote.service.codec.M4aInfo r7 = r1.inf     // Catch:{ FileNotFoundException -> 0x033a, IOException -> 0x032c, all -> 0x0325 }
            java.util.HashMap<java.lang.String, java.lang.Boolean> r7 = r7.hasCustomAtom     // Catch:{ FileNotFoundException -> 0x033a, IOException -> 0x032c, all -> 0x0325 }
            java.lang.String r14 = "book"
            java.lang.Boolean r4 = java.lang.Boolean.valueOf(r4)     // Catch:{ FileNotFoundException -> 0x033a, IOException -> 0x032c, all -> 0x0325 }
            r7.put(r14, r4)     // Catch:{ FileNotFoundException -> 0x033a, IOException -> 0x032c, all -> 0x0325 }
            java.io.RandomAccessFile r4 = new java.io.RandomAccessFile     // Catch:{ FileNotFoundException -> 0x033a, IOException -> 0x032c, all -> 0x0325 }
            com.sec.android.app.voicenote.service.codec.M4aInfo r7 = r1.inf     // Catch:{ FileNotFoundException -> 0x033a, IOException -> 0x032c, all -> 0x0325 }
            java.lang.String r7 = r7.path     // Catch:{ FileNotFoundException -> 0x033a, IOException -> 0x032c, all -> 0x0325 }
            java.lang.String r14 = "rw"
            r4.<init>(r7, r14)     // Catch:{ FileNotFoundException -> 0x033a, IOException -> 0x032c, all -> 0x0325 }
            java.io.RandomAccessFile r7 = new java.io.RandomAccessFile     // Catch:{ FileNotFoundException -> 0x0119, IOException -> 0x0115, all -> 0x0111 }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x0119, IOException -> 0x0115, all -> 0x0111 }
            r14.<init>()     // Catch:{ FileNotFoundException -> 0x0119, IOException -> 0x0115, all -> 0x0111 }
            java.io.File r15 = android.os.Environment.getExternalStorageDirectory()     // Catch:{ FileNotFoundException -> 0x0119, IOException -> 0x0115, all -> 0x0111 }
            java.lang.String r15 = r15.getAbsolutePath()     // Catch:{ FileNotFoundException -> 0x0119, IOException -> 0x0115, all -> 0x0111 }
            r14.append(r15)     // Catch:{ FileNotFoundException -> 0x0119, IOException -> 0x0115, all -> 0x0111 }
            r15 = 47
            r14.append(r15)     // Catch:{ FileNotFoundException -> 0x0119, IOException -> 0x0115, all -> 0x0111 }
            java.lang.String r6 = "temp6546368.m4a"
            r14.append(r6)     // Catch:{ FileNotFoundException -> 0x0119, IOException -> 0x0115, all -> 0x0111 }
            java.lang.String r6 = r14.toString()     // Catch:{ FileNotFoundException -> 0x0119, IOException -> 0x0115, all -> 0x0111 }
            java.lang.String r14 = "rw"
            r7.<init>(r6, r14)     // Catch:{ FileNotFoundException -> 0x0119, IOException -> 0x0115, all -> 0x0111 }
            java.nio.channels.FileChannel r6 = r4.getChannel()     // Catch:{ FileNotFoundException -> 0x0321, IOException -> 0x031d, all -> 0x0319 }
            java.nio.channels.FileChannel r14 = r7.getChannel()     // Catch:{ FileNotFoundException -> 0x0316, IOException -> 0x0313, all -> 0x0310 }
            com.sec.android.app.voicenote.service.codec.M4aInfo r15 = r1.inf     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            long r10 = r15.udtaPos     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            long r10 = r10 + r8
            r6.position(r10)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            long r10 = r6.size()     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r12 = 0
            int r10 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r10 != 0) goto L_0x0188
            java.lang.String r2 = "BookmarksHelper"
            java.lang.String r3 = "srcWrite.size() == 0 ! Aborting current operation"
            com.sec.android.app.voicenote.provider.Log.m22e(r2, r3)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r1.closeQuietly(r6)
            r1.closeQuietly(r14)
            r1.closeQuietly(r4)
            r1.closeQuietly(r7)
            return
        L_0x0188:
            r19 = 0
            long r21 = r6.size()     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r17 = r14
            r18 = r6
            long r10 = r17.transferFrom(r18, r19, r21)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            int r10 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r10 >= 0) goto L_0x01a1
            java.lang.String r10 = "BookmarksHelper"
            java.lang.String r11 = "dst.transferFrom() < 0"
            com.sec.android.app.voicenote.provider.Log.m22e(r10, r11)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
        L_0x01a1:
            com.sec.android.app.voicenote.service.codec.M4aInfo r10 = r1.inf     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            long r10 = r10.udtaPos     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            long r10 = r10 + r8
            r6.position(r10)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            byte[] r8 = r1.shortBook     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            java.nio.ByteBuffer r8 = java.nio.ByteBuffer.wrap(r8)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            com.sec.android.app.voicenote.service.codec.M4aInfo r9 = r1.inf     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            java.util.HashMap<java.lang.String, java.lang.Long> r9 = r9.customAtomPosition     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            java.lang.String r10 = "book"
            long r16 = r6.position()     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            java.lang.Long r11 = java.lang.Long.valueOf(r16)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r9.put(r10, r11)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            int r9 = r1.bookLength     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r8.putInt(r9)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r8.rewind()     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r6.write(r8)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            byte[] r8 = r1.newBNUM     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            java.nio.ByteBuffer r8 = java.nio.ByteBuffer.wrap(r8)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            com.sec.android.app.voicenote.service.codec.M4aInfo r9 = r1.inf     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            java.util.HashMap<java.lang.String, java.lang.Long> r9 = r9.customAtomPosition     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            java.lang.String r10 = "bnum"
            long r16 = r6.position()     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            java.lang.Long r11 = java.lang.Long.valueOf(r16)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r9.put(r10, r11)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r6.write(r8)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            long r8 = r6.position()     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r10 = 0
            r11 = 50
        L_0x01ec:
            if (r10 >= r11) goto L_0x022a
            r3.rewind()     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r5.rewind()     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r6.write(r3)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r15 = 0
            r5.putInt(r15)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r5.rewind()     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r6.write(r5)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            java.lang.String r11 = "empty title"
            java.lang.String r15 = "UTF-16BE"
            r12 = 100
            byte[] r11 = r1.strToByte(r11, r12, r15)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            java.nio.ByteBuffer r11 = java.nio.ByteBuffer.wrap(r11)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r6.write(r11)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            java.lang.String r11 = "empty description"
            java.lang.String r12 = "UTF-16BE"
            r13 = 500(0x1f4, float:7.0E-43)
            byte[] r11 = r1.strToByte(r11, r13, r12)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            java.nio.ByteBuffer r11 = java.nio.ByteBuffer.wrap(r11)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r6.write(r11)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            int r10 = r10 + 1
            r11 = 50
            r12 = 0
            goto L_0x01ec
        L_0x022a:
            r6.position(r8)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            monitor-enter(r24)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            int r8 = r24.size()     // Catch:{ all -> 0x0306 }
            r9 = 0
        L_0x0233:
            if (r9 >= r8) goto L_0x0286
            r3.rewind()     // Catch:{ all -> 0x0306 }
            r5.rewind()     // Catch:{ all -> 0x0306 }
            r6.write(r3)     // Catch:{ all -> 0x0306 }
            java.lang.Object r10 = r2.get(r9)     // Catch:{ all -> 0x0306 }
            com.sec.android.app.voicenote.service.helper.Bookmark r10 = (com.sec.android.app.voicenote.service.helper.Bookmark) r10     // Catch:{ all -> 0x0306 }
            int r10 = r10.getElapsed()     // Catch:{ all -> 0x0306 }
            r5.putInt(r10)     // Catch:{ all -> 0x0306 }
            r5.rewind()     // Catch:{ all -> 0x0306 }
            r6.write(r5)     // Catch:{ all -> 0x0306 }
            java.lang.Object r10 = r2.get(r9)     // Catch:{ all -> 0x0306 }
            com.sec.android.app.voicenote.service.helper.Bookmark r10 = (com.sec.android.app.voicenote.service.helper.Bookmark) r10     // Catch:{ all -> 0x0306 }
            java.lang.String r10 = r10.getTitle()     // Catch:{ all -> 0x0306 }
            java.lang.String r11 = "UTF-16BE"
            r12 = 100
            byte[] r10 = r1.strToByte(r10, r12, r11)     // Catch:{ all -> 0x0306 }
            java.nio.ByteBuffer r10 = java.nio.ByteBuffer.wrap(r10)     // Catch:{ all -> 0x0306 }
            r6.write(r10)     // Catch:{ all -> 0x0306 }
            java.lang.Object r10 = r2.get(r9)     // Catch:{ all -> 0x0306 }
            com.sec.android.app.voicenote.service.helper.Bookmark r10 = (com.sec.android.app.voicenote.service.helper.Bookmark) r10     // Catch:{ all -> 0x0306 }
            java.lang.String r10 = r10.getDescription()     // Catch:{ all -> 0x0306 }
            java.lang.String r11 = "UTF-16BE"
            r13 = 500(0x1f4, float:7.0E-43)
            byte[] r10 = r1.strToByte(r10, r13, r11)     // Catch:{ all -> 0x0306 }
            java.nio.ByteBuffer r10 = java.nio.ByteBuffer.wrap(r10)     // Catch:{ all -> 0x0306 }
            r6.write(r10)     // Catch:{ all -> 0x0306 }
            int r9 = r9 + 1
            goto L_0x0233
        L_0x0286:
            int r3 = r24.size()     // Catch:{ all -> 0x0306 }
            r1.bookmarksCount = r3     // Catch:{ all -> 0x0306 }
            monitor-exit(r24)     // Catch:{ all -> 0x0306 }
            com.sec.android.app.voicenote.service.codec.M4aInfo r2 = r1.inf     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            java.util.HashMap<java.lang.String, java.lang.Long> r2 = r2.customAtomPosition     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            java.lang.String r3 = "book"
            java.lang.Object r2 = r2.get(r3)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            java.lang.Long r2 = (java.lang.Long) r2     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            long r2 = r2.longValue()     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            int r5 = r1.bookLength     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            long r8 = (long) r5     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            long r2 = r2 + r8
            r6.position(r2)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r2 = 0
            r14.position(r2)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            long r19 = r6.position()     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            long r21 = r14.size()     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r17 = r6
            r18 = r14
            long r2 = r17.transferFrom(r18, r19, r21)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r8 = 0
            int r2 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r2 >= 0) goto L_0x02c6
            java.lang.String r2 = "BookmarksHelper"
            java.lang.String r3 = "srcWrite.transferFrom() < 0"
            com.sec.android.app.voicenote.provider.Log.m22e(r2, r3)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
        L_0x02c6:
            r1.updateOuterAtomsLengths(r6)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r1.updateBnum(r6)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r6.close()     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r4.close()     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r7.close()     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            java.io.File r2 = new java.io.File     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r3.<init>()     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            java.io.File r5 = android.os.Environment.getExternalStorageDirectory()     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            java.lang.String r5 = r5.getAbsolutePath()     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r3.append(r5)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r5 = 47
            r3.append(r5)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            java.lang.String r5 = "temp6546368.m4a"
            r3.append(r5)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            java.lang.String r3 = r3.toString()     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            r2.<init>(r3)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            boolean r2 = r2.delete()     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            if (r2 != 0) goto L_0x0347
            java.lang.String r2 = "BookmarksHelper"
            java.lang.String r3 = "delete temp file failed"
            com.sec.android.app.voicenote.provider.Log.m22e(r2, r3)     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
            goto L_0x0347
        L_0x0306:
            r0 = move-exception
            r3 = r0
            monitor-exit(r24)     // Catch:{ all -> 0x0306 }
            throw r3     // Catch:{ FileNotFoundException -> 0x030d, IOException -> 0x030a }
        L_0x030a:
            r0 = move-exception
            r2 = r0
            goto L_0x0332
        L_0x030d:
            r0 = move-exception
            r2 = r0
            goto L_0x0340
        L_0x0310:
            r0 = move-exception
            r2 = r0
            goto L_0x032a
        L_0x0313:
            r0 = move-exception
            r2 = r0
            goto L_0x0331
        L_0x0316:
            r0 = move-exception
            r2 = r0
            goto L_0x033f
        L_0x0319:
            r0 = move-exception
            r2 = r0
            r6 = 0
            goto L_0x032a
        L_0x031d:
            r0 = move-exception
            r2 = r0
            r6 = 0
            goto L_0x0331
        L_0x0321:
            r0 = move-exception
            r2 = r0
            r6 = 0
            goto L_0x033f
        L_0x0325:
            r0 = move-exception
            r2 = r0
            r4 = 0
        L_0x0328:
            r6 = 0
        L_0x0329:
            r7 = 0
        L_0x032a:
            r14 = 0
            goto L_0x035d
        L_0x032c:
            r0 = move-exception
            r2 = r0
            r4 = 0
        L_0x032f:
            r6 = 0
        L_0x0330:
            r7 = 0
        L_0x0331:
            r14 = 0
        L_0x0332:
            java.lang.String r3 = "BookmarksHelper"
            java.lang.String r5 = "IOException"
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r3, (java.lang.String) r5, (java.lang.Throwable) r2)     // Catch:{ all -> 0x035b }
            goto L_0x0347
        L_0x033a:
            r0 = move-exception
            r2 = r0
            r4 = 0
        L_0x033d:
            r6 = 0
        L_0x033e:
            r7 = 0
        L_0x033f:
            r14 = 0
        L_0x0340:
            java.lang.String r3 = "BookmarksHelper"
            java.lang.String r5 = "FileNotFoundException"
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r3, (java.lang.String) r5, (java.lang.Throwable) r2)     // Catch:{ all -> 0x035b }
        L_0x0347:
            r1.closeQuietly(r6)
            r1.closeQuietly(r14)
            r1.closeQuietly(r4)
            r1.closeQuietly(r7)
            java.lang.String r2 = "BookmarksHelper"
            java.lang.String r3 = "overwrite has ended"
            com.sec.android.app.voicenote.provider.Log.m26i(r2, r3)
            return
        L_0x035b:
            r0 = move-exception
            r2 = r0
        L_0x035d:
            r1.closeQuietly(r6)
            r1.closeQuietly(r14)
            r1.closeQuietly(r4)
            r1.closeQuietly(r7)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.helper.BookmarksHelper.overwrite(java.util.List):void");
    }

    public List<Bookmark> getAllBookmarks() {
        FileInputStream fileInputStream;
        FileInputStream fileInputStream2 = null;
        if (this.invalidInit) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        try {
            fileInputStream = new FileInputStream(new File(this.inf.path));
            try {
                FileChannel channel = fileInputStream.getChannel();
                ByteBuffer allocate = ByteBuffer.allocate(4);
                ByteBuffer allocate2 = ByteBuffer.allocate(100);
                ByteBuffer allocate3 = ByteBuffer.allocate(500);
                channel.position(this.inf.customAtomPosition.get(M4aConsts.BOOKMARKS_NUMBER).longValue() + 16);
                for (int i = 0; i < this.bookmarksCount; i++) {
                    if (channel.read(allocate) < 0) {
                        closeQuietly(fileInputStream);
                        return null;
                    }
                    allocate.rewind();
                    int i2 = allocate.getInt();
                    allocate.rewind();
                    if (channel.read(allocate2) < 0) {
                        closeQuietly(fileInputStream);
                        return null;
                    }
                    allocate2.rewind();
                    byte[] array = allocate2.array();
                    if (channel.read(allocate3) < 0) {
                        closeQuietly(fileInputStream);
                        return null;
                    }
                    allocate3.rewind();
                    byte[] array2 = allocate3.array();
                    arrayList.add(new Bookmark(i2, new String(array, STRING_CODING).trim(), new String(array2, STRING_CODING).trim(), true));
                    channel.position(channel.position() + 8);
                }
                fileInputStream.close();
                closeQuietly(fileInputStream);
            } catch (FileNotFoundException e) {
                e = e;
                fileInputStream2 = fileInputStream;
                Log.m24e(TAG, "FileNotFoundException", (Throwable) e);
                closeQuietly(fileInputStream2);
                Collections.sort(arrayList);
                return arrayList;
            } catch (IOException e2) {
//                e = e2;
                fileInputStream2 = fileInputStream;
//                Log.m24e(TAG, "IOException", (Throwable) e);
                closeQuietly(fileInputStream2);
                Collections.sort(arrayList);
                return arrayList;
            } catch (Throwable th) {
                th = th;
                closeQuietly(fileInputStream);
                throw th;
            }
        } catch (FileNotFoundException e3) {
//            e = e3;
//            Log.m24e(TAG, "FileNotFoundException", (Throwable) e);
            closeQuietly(fileInputStream2);
            Collections.sort(arrayList);
            return arrayList;
        } catch (IOException e4) {
//            e = e4;
//            Log.m24e(TAG, "IOException", (Throwable) e);
            closeQuietly(fileInputStream2);
            Collections.sort(arrayList);
            return arrayList;
        } catch (Throwable th2) {
//            th = th2;
            fileInputStream = fileInputStream2;
            closeQuietly(fileInputStream);
//            throw th;
        }
        Collections.sort(arrayList);
        return arrayList;
    }

    /* access modifiers changed from: protected */
    public void updateOuterAtomsLengths(FileChannel fileChannel) {
        try {
            ByteBuffer allocate = ByteBuffer.allocate(4);
            allocate.putInt(this.inf.fileMoovLength + this.bookLength);
            allocate.rewind();
            fileChannel.position(this.inf.moovPos);
            fileChannel.write(allocate);
            allocate.rewind();
            allocate.putInt(this.inf.fileUdtaLength + this.bookLength);
            allocate.rewind();
            fileChannel.position(this.inf.udtaPos);
            fileChannel.write(allocate);
            allocate.rewind();
        } catch (IOException e) {
            Log.m24e(TAG, "updateOuterAtomsLengths - Some other exception", (Throwable) e);
        }
    }

    private void updateBnum(FileChannel fileChannel) {
        try {
            ByteBuffer allocate = ByteBuffer.allocate(4);
            allocate.putInt((this.bookmarksCount * M4aConsts.BKMK_LENGTH) + 8);
            allocate.rewind();
            fileChannel.position(this.inf.customAtomPosition.get(M4aConsts.BOOKMARKS_NUMBER).longValue());
            fileChannel.write(allocate);
        } catch (IOException e) {
            Log.m24e(TAG, "IOException ", (Throwable) e);
        } catch (IllegalArgumentException e2) {
            Log.m24e(TAG, "IllegalArgumentException ", (Throwable) e2);
        }
    }

    private byte[] strToByte(String str, int i, String str2) {
        byte[] bArr = new byte[i];
        try {
            byte[] bytes = str.getBytes(str2);
            if (bytes.length > i) {
                System.arraycopy(bytes, 0, bArr, 0, i);
            } else {
                System.arraycopy(bytes, 0, bArr, 0, bytes.length);
            }
        } catch (UnsupportedEncodingException e) {
            Log.m24e(TAG, "UnsupportedEncodingException", (Throwable) e);
        }
        return bArr;
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
}
