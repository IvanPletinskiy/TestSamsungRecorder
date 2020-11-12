package com.samsung.android.scloud.oem.lib.backup.record;

import android.content.Context;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.JsonReader;
import com.samsung.android.scloud.oem.lib.LOG;
import com.samsung.android.scloud.oem.lib.backup.BackupMetaManager;
import com.samsung.android.scloud.oem.lib.backup.IBackupClient;
import com.samsung.android.scloud.oem.lib.backup.ReuseDBHelper;
import com.samsung.android.scloud.oem.lib.bnr.BNRFile;
import com.samsung.android.scloud.oem.lib.common.IClientHelper;
import com.samsung.android.scloud.oem.lib.common.IServiceHandler;
import com.samsung.android.scloud.oem.lib.utils.SCloudParser;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RecordClientManager extends IClientHelper {
    /* access modifiers changed from: private */
    public static final String TAG = "RecordClientManager";
    private final IBackupClient backupClient;
    private String dataDirectory;
    /* access modifiers changed from: private */
    public final Map<String, List<ParcelFileDescriptor>> pfdMap = new HashMap();
    /* access modifiers changed from: private */
    public final Map<String, List<String>> processedKeyMap = new HashMap();
    private final Map<String, IServiceHandler> serviceHandlerMap = new HashMap();

    public RecordClientManager(IBackupClient iBackupClient) {
        this.backupClient = iBackupClient;
        this.serviceHandlerMap.put("getKeyAndDate", new IServiceHandler() {
            /* JADX WARNING: Removed duplicated region for block: B:23:0x009b  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public android.os.Bundle handleServiceAction(android.content.Context r7, java.lang.Object r8, java.lang.String r9, android.os.Bundle r10) {
                /*
                    r6 = this;
                    java.lang.String r0 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.TAG
                    java.lang.StringBuilder r1 = new java.lang.StringBuilder
                    r1.<init>()
                    java.lang.String r2 = "["
                    r1.append(r2)
                    r1.append(r9)
                    java.lang.String r3 = "] GET_KEY_AND_DATE"
                    r1.append(r3)
                    java.lang.String r1 = r1.toString()
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r0, r1)
                    java.lang.String r0 = "file_descriptor"
                    android.os.Parcelable r10 = r10.getParcelable(r0)
                    android.os.ParcelFileDescriptor r10 = (android.os.ParcelFileDescriptor) r10
                    android.os.Bundle r0 = new android.os.Bundle
                    r0.<init>()
                    if (r10 != 0) goto L_0x0048
                    java.lang.String r7 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.TAG
                    java.lang.StringBuilder r8 = new java.lang.StringBuilder
                    r8.<init>()
                    r8.append(r2)
                    r8.append(r9)
                    java.lang.String r9 = "] pfd is null"
                    r8.append(r9)
                    java.lang.String r8 = r8.toString()
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r7, r8)
                    return r0
                L_0x0048:
                    com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager r1 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.this
                    java.util.Map r1 = r1.pfdMap
                    java.lang.Object r1 = r1.get(r9)
                    java.util.List r1 = (java.util.List) r1
                    if (r1 != 0) goto L_0x005b
                    java.util.ArrayList r1 = new java.util.ArrayList
                    r1.<init>()
                L_0x005b:
                    r1.add(r10)
                    com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager r2 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.this
                    java.util.Map r2 = r2.pfdMap
                    r2.put(r9, r1)
                    r1 = 0
                    com.samsung.android.scloud.oem.lib.backup.record.RecordClientHelper r2 = new com.samsung.android.scloud.oem.lib.backup.record.RecordClientHelper     // Catch:{ all -> 0x0098 }
                    android.util.JsonWriter r3 = new android.util.JsonWriter     // Catch:{ all -> 0x0098 }
                    java.io.FileWriter r4 = new java.io.FileWriter     // Catch:{ all -> 0x0098 }
                    java.io.FileDescriptor r5 = r10.getFileDescriptor()     // Catch:{ all -> 0x0098 }
                    r4.<init>(r5)     // Catch:{ all -> 0x0098 }
                    r3.<init>(r4)     // Catch:{ all -> 0x0098 }
                    r2.<init>(r7, r9, r3)     // Catch:{ all -> 0x0098 }
                    r2.open()     // Catch:{ all -> 0x0095 }
                    com.samsung.android.scloud.oem.lib.backup.record.IRecordClient r8 = (com.samsung.android.scloud.oem.lib.backup.record.IRecordClient) r8     // Catch:{ all -> 0x0095 }
                    boolean r7 = r8.addKeyAndDate(r7, r2)     // Catch:{ all -> 0x0095 }
                    r2.release()
                    r10.close()     // Catch:{ IOException -> 0x008b }
                    goto L_0x008f
                L_0x008b:
                    r8 = move-exception
                    r8.printStackTrace()
                L_0x008f:
                    java.lang.String r8 = "is_success"
                    r0.putBoolean(r8, r7)
                    return r0
                L_0x0095:
                    r7 = move-exception
                    r1 = r2
                    goto L_0x0099
                L_0x0098:
                    r7 = move-exception
                L_0x0099:
                    if (r1 == 0) goto L_0x009e
                    r1.release()
                L_0x009e:
                    r10.close()     // Catch:{ IOException -> 0x00a2 }
                    goto L_0x00a6
                L_0x00a2:
                    r8 = move-exception
                    r8.printStackTrace()
                L_0x00a6:
                    throw r7
                */
                throw new UnsupportedOperationException("Method not decompiled: com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.C06141.handleServiceAction(android.content.Context, java.lang.Object, java.lang.String, android.os.Bundle):android.os.Bundle");
            }
        });
        this.serviceHandlerMap.put("getRecord", new IServiceHandler() {
            /* JADX WARNING: Removed duplicated region for block: B:28:0x010b  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public android.os.Bundle handleServiceAction(android.content.Context r21, java.lang.Object r22, java.lang.String r23, android.os.Bundle r24) {
                /*
                    r20 = this;
                    r7 = r20
                    r0 = r23
                    r1 = r24
                    java.lang.String r2 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.TAG
                    java.lang.StringBuilder r3 = new java.lang.StringBuilder
                    r3.<init>()
                    java.lang.String r15 = "["
                    r3.append(r15)
                    r3.append(r0)
                    java.lang.String r4 = "] GET_RECORD"
                    r3.append(r4)
                    java.lang.String r3 = r3.toString()
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r2, r3)
                    android.os.Bundle r14 = new android.os.Bundle
                    r14.<init>()
                    r2 = 0
                    java.lang.String r12 = "is_success"
                    r14.putBoolean(r12, r2)
                    java.lang.String r3 = "record_pfd"
                    android.os.Parcelable r3 = r1.getParcelable(r3)
                    r13 = r3
                    android.os.ParcelFileDescriptor r13 = (android.os.ParcelFileDescriptor) r13
                    java.lang.String r3 = "idlist_file"
                    android.os.Parcelable r3 = r1.getParcelable(r3)
                    android.os.ParcelFileDescriptor r3 = (android.os.ParcelFileDescriptor) r3
                    if (r13 == 0) goto L_0x0118
                    if (r3 != 0) goto L_0x0045
                    goto L_0x0118
                L_0x0045:
                    com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager r4 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.this
                    java.util.Map r4 = r4.pfdMap
                    java.lang.Object r4 = r4.get(r0)
                    java.util.List r4 = (java.util.List) r4
                    if (r4 != 0) goto L_0x0058
                    java.util.ArrayList r4 = new java.util.ArrayList
                    r4.<init>()
                L_0x0058:
                    r4.add(r13)
                    r4.add(r3)
                    com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager r5 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.this
                    java.util.Map r5 = r5.pfdMap
                    r5.put(r0, r4)
                    com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager r4 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.this
                    android.util.JsonReader r5 = new android.util.JsonReader
                    java.io.FileReader r6 = new java.io.FileReader
                    java.io.FileDescriptor r3 = r3.getFileDescriptor()
                    r6.<init>(r3)
                    r5.<init>(r6)
                    java.util.ArrayList r11 = r4.getListFromJsonFile(r5)
                    java.lang.String r3 = "observing_uri"
                    java.lang.String r1 = r1.getString(r3)
                    android.net.Uri r5 = android.net.Uri.parse(r1)
                    r1 = 1
                    long[] r3 = new long[r1]
                    r8 = 0
                    r3[r2] = r8
                    r16 = 0
                    com.samsung.android.scloud.oem.lib.backup.record.RecordClientHelper r10 = new com.samsung.android.scloud.oem.lib.backup.record.RecordClientHelper     // Catch:{ all -> 0x0106 }
                    android.util.JsonWriter r9 = new android.util.JsonWriter     // Catch:{ all -> 0x0106 }
                    java.io.FileWriter r1 = new java.io.FileWriter     // Catch:{ all -> 0x0106 }
                    java.io.FileDescriptor r2 = r13.getFileDescriptor()     // Catch:{ all -> 0x0106 }
                    r1.<init>(r2)     // Catch:{ all -> 0x0106 }
                    r9.<init>(r1)     // Catch:{ all -> 0x0106 }
                    int r1 = r11.size()     // Catch:{ all -> 0x0106 }
                    long r1 = (long) r1     // Catch:{ all -> 0x0106 }
                    com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager$2$1 r17 = new com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager$2$1     // Catch:{ all -> 0x0106 }
                    r18 = r1
                    r1 = r17
                    r2 = r20
                    r4 = r23
                    r6 = r21
                    r1.<init>(r3, r4, r5, r6)     // Catch:{ all -> 0x0106 }
                    r8 = r10
                    r1 = r9
                    r9 = r21
                    r2 = r10
                    r10 = r23
                    r3 = r11
                    r11 = r1
                    r4 = r12
                    r1 = r13
                    r12 = r18
                    r5 = r14
                    r14 = r17
                    r8.<init>(r9, r10, r11, r12, r14)     // Catch:{ all -> 0x0104 }
                    r2.open()     // Catch:{ all -> 0x0100 }
                    r6 = r22
                    com.samsung.android.scloud.oem.lib.backup.record.IRecordClient r6 = (com.samsung.android.scloud.oem.lib.backup.record.IRecordClient) r6     // Catch:{ all -> 0x0100 }
                    r8 = r21
                    boolean r3 = r6.backupRecord(r8, r2, r3)     // Catch:{ all -> 0x0100 }
                    java.lang.String r6 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.TAG     // Catch:{ all -> 0x0100 }
                    java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0100 }
                    r8.<init>()     // Catch:{ all -> 0x0100 }
                    r8.append(r15)     // Catch:{ all -> 0x0100 }
                    r8.append(r0)     // Catch:{ all -> 0x0100 }
                    java.lang.String r0 = "] backupRecord: onCompleted: "
                    r8.append(r0)     // Catch:{ all -> 0x0100 }
                    r8.append(r3)     // Catch:{ all -> 0x0100 }
                    java.lang.String r0 = r8.toString()     // Catch:{ all -> 0x0100 }
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r6, r0)     // Catch:{ all -> 0x0100 }
                    r2.release()
                    r1.close()     // Catch:{ IOException -> 0x00f7 }
                    goto L_0x00fc
                L_0x00f7:
                    r0 = move-exception
                    r1 = r0
                    r1.printStackTrace()
                L_0x00fc:
                    r5.putBoolean(r4, r3)
                    return r5
                L_0x0100:
                    r0 = move-exception
                    r16 = r2
                    goto L_0x0108
                L_0x0104:
                    r0 = move-exception
                    goto L_0x0108
                L_0x0106:
                    r0 = move-exception
                    r1 = r13
                L_0x0108:
                    r2 = r0
                    if (r16 == 0) goto L_0x010e
                    r16.release()
                L_0x010e:
                    r1.close()     // Catch:{ IOException -> 0x0112 }
                    goto L_0x0117
                L_0x0112:
                    r0 = move-exception
                    r1 = r0
                    r1.printStackTrace()
                L_0x0117:
                    throw r2
                L_0x0118:
                    r5 = r14
                    java.lang.String r1 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.TAG
                    java.lang.StringBuilder r2 = new java.lang.StringBuilder
                    r2.<init>()
                    r2.append(r15)
                    r2.append(r0)
                    java.lang.String r0 = "] pfd is null or uploadList is null"
                    r2.append(r0)
                    java.lang.String r0 = r2.toString()
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r1, r0)
                    return r5
                */
                throw new UnsupportedOperationException("Method not decompiled: com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.C06192.handleServiceAction(android.content.Context, java.lang.Object, java.lang.String, android.os.Bundle):android.os.Bundle");
            }
        });
        this.serviceHandlerMap.put("putRecord", new IServiceHandler() {
            /* JADX WARNING: Code restructure failed: missing block: B:102:0x02b5, code lost:
                r19.release();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:106:0x02c6, code lost:
                r0 = new java.util.ArrayList();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:47:0x01a8, code lost:
                r0 = th;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:48:0x01aa, code lost:
                r0 = e;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:60:0x01d0, code lost:
                r0 = e;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:61:0x01d1, code lost:
                r10 = "] PUT_RECORD: result: ";
                r20 = false;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:62:0x01d5, code lost:
                r0 = th;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:63:0x01d6, code lost:
                r13 = r8;
                r8 = r9;
                r9 = "[";
                r10 = "] PUT_RECORD: result: ";
             */
            /* JADX WARNING: Code restructure failed: missing block: B:76:?, code lost:
                ((android.util.JsonReader) r2.next()).close();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:77:0x0219, code lost:
                r0 = com.sec.android.app.voicenote.common.util.DeviceInfo.STR_TRUE;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:78:0x021c, code lost:
                r0 = move-exception;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:79:0x021d, code lost:
                r0 = android.util.Log.getStackTraceString(r0);
             */
            /* JADX WARNING: Code restructure failed: missing block: B:80:0x0222, code lost:
                com.samsung.android.scloud.oem.lib.LOG.m15i(com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.access$000(), r9 + r15 + r10 + r0);
             */
            /* JADX WARNING: Code restructure failed: missing block: B:82:0x0241, code lost:
                r19.release();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:86:0x0252, code lost:
                r0 = new java.util.ArrayList();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:96:?, code lost:
                ((android.util.JsonReader) r2.next()).close();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:97:0x028d, code lost:
                r0 = com.sec.android.app.voicenote.common.util.DeviceInfo.STR_TRUE;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:98:0x0290, code lost:
                r0 = move-exception;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:99:0x0291, code lost:
                r0 = android.util.Log.getStackTraceString(r0);
             */
            /* JADX WARNING: Failed to process nested try/catch */
            /* JADX WARNING: Removed duplicated region for block: B:102:0x02b5  */
            /* JADX WARNING: Removed duplicated region for block: B:106:0x02c6 A[Catch:{ FileNotFoundException -> 0x02e8 }] */
            /* JADX WARNING: Removed duplicated region for block: B:47:0x01a8 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:24:0x0111] */
            /* JADX WARNING: Removed duplicated region for block: B:62:0x01d5 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:5:0x0083] */
            /* JADX WARNING: Removed duplicated region for block: B:74:0x0210  */
            /* JADX WARNING: Removed duplicated region for block: B:82:0x0241  */
            /* JADX WARNING: Removed duplicated region for block: B:86:0x0252 A[Catch:{ FileNotFoundException -> 0x01a0 }] */
            /* JADX WARNING: Removed duplicated region for block: B:94:0x0284  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public android.os.Bundle handleServiceAction(android.content.Context r25, java.lang.Object r26, java.lang.String r27, android.os.Bundle r28) {
                /*
                    r24 = this;
                    r7 = r24
                    r0 = r25
                    r15 = r27
                    r1 = r28
                    java.lang.String r14 = "] PUT_RECORD: result: "
                    java.lang.String r16 = "true"
                    java.lang.String r2 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.TAG
                    java.lang.StringBuilder r3 = new java.lang.StringBuilder
                    r3.<init>()
                    java.lang.String r12 = "["
                    r3.append(r12)
                    r3.append(r15)
                    java.lang.String r4 = "] PUT_RECORD"
                    r3.append(r4)
                    java.lang.String r3 = r3.toString()
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r2, r3)
                    android.os.Bundle r13 = new android.os.Bundle
                    r13.<init>()
                    java.lang.String r11 = "is_success"
                    r10 = 0
                    r13.putBoolean(r11, r10)
                    java.lang.String r2 = "path_list"
                    java.util.ArrayList r2 = r1.getStringArrayList(r2)
                    java.lang.String r3 = "observing_uri"
                    java.lang.String r3 = r1.getString(r3)
                    r4 = 0
                    java.lang.String r6 = "total"
                    long r17 = r1.getLong(r6, r4)
                    if (r2 == 0) goto L_0x02ed
                    int r1 = (r17 > r4 ? 1 : (r17 == r4 ? 0 : -1))
                    if (r1 != 0) goto L_0x0050
                    goto L_0x02ed
                L_0x0050:
                    java.util.ArrayList r9 = new java.util.ArrayList
                    r9.<init>()
                    android.net.Uri r6 = android.net.Uri.parse(r3)
                    java.io.File r8 = new java.io.File
                    com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager r1 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.this
                    java.io.File r1 = r1.getFileDirectory(r0)
                    java.lang.StringBuilder r3 = new java.lang.StringBuilder
                    r3.<init>()
                    java.lang.String r4 = "BACKUP_"
                    r3.append(r4)
                    r3.append(r15)
                    java.lang.String r4 = "_RestoredID"
                    r3.append(r4)
                    java.lang.String r3 = r3.toString()
                    r8.<init>(r1, r3)
                    r1 = 1
                    long[] r3 = new long[r1]
                    r4 = 0
                    r3[r10] = r4
                    r19 = 0
                    java.util.Iterator r1 = r2.iterator()     // Catch:{ Exception -> 0x01dd, all -> 0x01d5 }
                L_0x0087:
                    boolean r2 = r1.hasNext()     // Catch:{ Exception -> 0x01dd, all -> 0x01d5 }
                    if (r2 == 0) goto L_0x00c6
                    java.lang.Object r2 = r1.next()     // Catch:{ Exception -> 0x00b9, all -> 0x00b1 }
                    java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x00b9, all -> 0x00b1 }
                    android.util.JsonReader r4 = new android.util.JsonReader     // Catch:{ Exception -> 0x00b9, all -> 0x00b1 }
                    java.io.FileReader r5 = new java.io.FileReader     // Catch:{ Exception -> 0x00b9, all -> 0x00b1 }
                    java.io.File r10 = new java.io.File     // Catch:{ Exception -> 0x00b9, all -> 0x00b1 }
                    r28 = r1
                    com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager r1 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.this     // Catch:{ Exception -> 0x00b9, all -> 0x00b1 }
                    java.io.File r1 = r1.getFileDirectory(r0)     // Catch:{ Exception -> 0x00b9, all -> 0x00b1 }
                    r10.<init>(r1, r2)     // Catch:{ Exception -> 0x00b9, all -> 0x00b1 }
                    r5.<init>(r10)     // Catch:{ Exception -> 0x00b9, all -> 0x00b1 }
                    r4.<init>(r5)     // Catch:{ Exception -> 0x00b9, all -> 0x00b1 }
                    r9.add(r4)     // Catch:{ Exception -> 0x00b9, all -> 0x00b1 }
                    r1 = r28
                    r10 = 0
                    goto L_0x0087
                L_0x00b1:
                    r0 = move-exception
                    r1 = r0
                    r13 = r8
                    r8 = r9
                    r9 = r12
                    r10 = r14
                    goto L_0x027a
                L_0x00b9:
                    r0 = move-exception
                    r10 = r14
                    r1 = 0
                    r23 = r13
                    r13 = r8
                    r8 = r9
                    r9 = r12
                    r12 = r11
                    r11 = r23
                    goto L_0x01eb
                L_0x00c6:
                    com.samsung.android.scloud.oem.lib.backup.record.RecordClientHelper r21 = new com.samsung.android.scloud.oem.lib.backup.record.RecordClientHelper     // Catch:{ Exception -> 0x01d0, all -> 0x01d5 }
                    android.util.JsonWriter r10 = new android.util.JsonWriter     // Catch:{ Exception -> 0x01d0, all -> 0x01d5 }
                    java.io.FileWriter r1 = new java.io.FileWriter     // Catch:{ Exception -> 0x01d0, all -> 0x01d5 }
                    r1.<init>(r8)     // Catch:{ Exception -> 0x01d0, all -> 0x01d5 }
                    r10.<init>(r1)     // Catch:{ Exception -> 0x01d0, all -> 0x01d5 }
                    com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager$3$1 r22 = new com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager$3$1     // Catch:{ Exception -> 0x01d0, all -> 0x01d5 }
                    r1 = r22
                    r2 = r24
                    r4 = r27
                    r5 = r6
                    r6 = r25
                    r1.<init>(r3, r4, r5, r6)     // Catch:{ Exception -> 0x01d0, all -> 0x01d5 }
                    r6 = r8
                    r8 = r21
                    r4 = r9
                    r9 = r25
                    r1 = r10
                    r20 = 0
                    r10 = r27
                    r5 = r11
                    r11 = r1
                    r2 = r12
                    r3 = r13
                    r12 = r17
                    r1 = r14
                    r14 = r22
                    r8.<init>(r9, r10, r11, r12, r14)     // Catch:{ Exception -> 0x01c8, all -> 0x01c2 }
                    r21.open()     // Catch:{ Exception -> 0x01b6, all -> 0x01ac }
                    com.samsung.android.scloud.oem.lib.backup.record.RecordReader r8 = new com.samsung.android.scloud.oem.lib.backup.record.RecordReader     // Catch:{ Exception -> 0x01b6, all -> 0x01ac }
                    r8.<init>(r4, r15)     // Catch:{ Exception -> 0x01b6, all -> 0x01ac }
                    r9 = r26
                    com.samsung.android.scloud.oem.lib.backup.record.IRecordClient r9 = (com.samsung.android.scloud.oem.lib.backup.record.IRecordClient) r9     // Catch:{ Exception -> 0x01b6, all -> 0x01ac }
                    r10 = r1
                    r1 = r9
                    r9 = r2
                    r2 = r25
                    r11 = r3
                    r3 = r8
                    r8 = r4
                    r12 = r5
                    r4 = r17
                    r13 = r6
                    r6 = r21
                    boolean r1 = r1.restoreRecord(r2, r3, r4, r6)     // Catch:{ Exception -> 0x01aa, all -> 0x01a8 }
                    java.lang.String r0 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.TAG     // Catch:{ Exception -> 0x01a6, all -> 0x01a8 }
                    java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01a6, all -> 0x01a8 }
                    r2.<init>()     // Catch:{ Exception -> 0x01a6, all -> 0x01a8 }
                    r2.append(r9)     // Catch:{ Exception -> 0x01a6, all -> 0x01a8 }
                    r2.append(r15)     // Catch:{ Exception -> 0x01a6, all -> 0x01a8 }
                    java.lang.String r3 = "] restoreRecord: onCompleted: "
                    r2.append(r3)     // Catch:{ Exception -> 0x01a6, all -> 0x01a8 }
                    r2.append(r1)     // Catch:{ Exception -> 0x01a6, all -> 0x01a8 }
                    java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x01a6, all -> 0x01a8 }
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r0, r2)     // Catch:{ Exception -> 0x01a6, all -> 0x01a8 }
                    java.util.Iterator r2 = r8.iterator()
                L_0x0137:
                    boolean r0 = r2.hasNext()
                    if (r0 == 0) goto L_0x016c
                    java.lang.Object r0 = r2.next()
                    android.util.JsonReader r0 = (android.util.JsonReader) r0
                    r0.close()     // Catch:{ Exception -> 0x0149 }
                    r0 = r16
                    goto L_0x014f
                L_0x0149:
                    r0 = move-exception
                    r3 = r0
                    java.lang.String r0 = android.util.Log.getStackTraceString(r3)
                L_0x014f:
                    java.lang.String r3 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.TAG
                    java.lang.StringBuilder r4 = new java.lang.StringBuilder
                    r4.<init>()
                    r4.append(r9)
                    r4.append(r15)
                    r4.append(r10)
                    r4.append(r0)
                    java.lang.String r0 = r4.toString()
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r3, r0)
                    goto L_0x0137
                L_0x016c:
                    r21.release()
                    com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager r0 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.this     // Catch:{ FileNotFoundException -> 0x01a0 }
                    java.util.Map r0 = r0.processedKeyMap     // Catch:{ FileNotFoundException -> 0x01a0 }
                    java.lang.Object r0 = r0.get(r15)     // Catch:{ FileNotFoundException -> 0x01a0 }
                    java.util.List r0 = (java.util.List) r0     // Catch:{ FileNotFoundException -> 0x01a0 }
                    if (r0 != 0) goto L_0x0182
                    java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ FileNotFoundException -> 0x01a0 }
                    r0.<init>()     // Catch:{ FileNotFoundException -> 0x01a0 }
                L_0x0182:
                    com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager r2 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.this     // Catch:{ FileNotFoundException -> 0x01a0 }
                    android.util.JsonReader r3 = new android.util.JsonReader     // Catch:{ FileNotFoundException -> 0x01a0 }
                    java.io.FileReader r4 = new java.io.FileReader     // Catch:{ FileNotFoundException -> 0x01a0 }
                    r4.<init>(r13)     // Catch:{ FileNotFoundException -> 0x01a0 }
                    r3.<init>(r4)     // Catch:{ FileNotFoundException -> 0x01a0 }
                    java.util.ArrayList r2 = r2.getListFromJsonFile(r3)     // Catch:{ FileNotFoundException -> 0x01a0 }
                    r0.addAll(r2)     // Catch:{ FileNotFoundException -> 0x01a0 }
                    com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager r2 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.this     // Catch:{ FileNotFoundException -> 0x01a0 }
                    java.util.Map r2 = r2.processedKeyMap     // Catch:{ FileNotFoundException -> 0x01a0 }
                    r2.put(r15, r0)     // Catch:{ FileNotFoundException -> 0x01a0 }
                    goto L_0x0273
                L_0x01a0:
                    r0 = move-exception
                    r0.printStackTrace()
                    goto L_0x0273
                L_0x01a6:
                    r0 = move-exception
                    goto L_0x01bf
                L_0x01a8:
                    r0 = move-exception
                    goto L_0x01b1
                L_0x01aa:
                    r0 = move-exception
                    goto L_0x01bd
                L_0x01ac:
                    r0 = move-exception
                    r10 = r1
                    r9 = r2
                    r8 = r4
                    r13 = r6
                L_0x01b1:
                    r1 = r0
                    r19 = r21
                    goto L_0x027a
                L_0x01b6:
                    r0 = move-exception
                    r10 = r1
                    r9 = r2
                    r11 = r3
                    r8 = r4
                    r12 = r5
                    r13 = r6
                L_0x01bd:
                    r1 = r20
                L_0x01bf:
                    r19 = r21
                    goto L_0x01eb
                L_0x01c2:
                    r0 = move-exception
                    r10 = r1
                    r9 = r2
                    r8 = r4
                    r13 = r6
                    goto L_0x01da
                L_0x01c8:
                    r0 = move-exception
                    r10 = r1
                    r9 = r2
                    r11 = r3
                    r8 = r4
                    r12 = r5
                    r13 = r6
                    goto L_0x01e9
                L_0x01d0:
                    r0 = move-exception
                    r10 = r14
                    r20 = 0
                    goto L_0x01e1
                L_0x01d5:
                    r0 = move-exception
                    r13 = r8
                    r8 = r9
                    r9 = r12
                    r10 = r14
                L_0x01da:
                    r1 = r0
                    goto L_0x027a
                L_0x01dd:
                    r0 = move-exception
                    r20 = r10
                    r10 = r14
                L_0x01e1:
                    r23 = r13
                    r13 = r8
                    r8 = r9
                    r9 = r12
                    r12 = r11
                    r11 = r23
                L_0x01e9:
                    r1 = r20
                L_0x01eb:
                    java.lang.String r2 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.TAG     // Catch:{ all -> 0x0277 }
                    java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0277 }
                    r3.<init>()     // Catch:{ all -> 0x0277 }
                    r3.append(r9)     // Catch:{ all -> 0x0277 }
                    r3.append(r15)     // Catch:{ all -> 0x0277 }
                    java.lang.String r4 = "] Exception"
                    r3.append(r4)     // Catch:{ all -> 0x0277 }
                    java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0277 }
                    com.samsung.android.scloud.oem.lib.LOG.m14e(r2, r3, r0)     // Catch:{ all -> 0x0277 }
                    java.util.Iterator r2 = r8.iterator()
                L_0x020a:
                    boolean r0 = r2.hasNext()
                    if (r0 == 0) goto L_0x023f
                    java.lang.Object r0 = r2.next()
                    android.util.JsonReader r0 = (android.util.JsonReader) r0
                    r0.close()     // Catch:{ Exception -> 0x021c }
                    r0 = r16
                    goto L_0x0222
                L_0x021c:
                    r0 = move-exception
                    r3 = r0
                    java.lang.String r0 = android.util.Log.getStackTraceString(r3)
                L_0x0222:
                    java.lang.String r3 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.TAG
                    java.lang.StringBuilder r4 = new java.lang.StringBuilder
                    r4.<init>()
                    r4.append(r9)
                    r4.append(r15)
                    r4.append(r10)
                    r4.append(r0)
                    java.lang.String r0 = r4.toString()
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r3, r0)
                    goto L_0x020a
                L_0x023f:
                    if (r19 == 0) goto L_0x0244
                    r19.release()
                L_0x0244:
                    com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager r0 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.this     // Catch:{ FileNotFoundException -> 0x01a0 }
                    java.util.Map r0 = r0.processedKeyMap     // Catch:{ FileNotFoundException -> 0x01a0 }
                    java.lang.Object r0 = r0.get(r15)     // Catch:{ FileNotFoundException -> 0x01a0 }
                    java.util.List r0 = (java.util.List) r0     // Catch:{ FileNotFoundException -> 0x01a0 }
                    if (r0 != 0) goto L_0x0257
                    java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ FileNotFoundException -> 0x01a0 }
                    r0.<init>()     // Catch:{ FileNotFoundException -> 0x01a0 }
                L_0x0257:
                    com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager r2 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.this     // Catch:{ FileNotFoundException -> 0x01a0 }
                    android.util.JsonReader r3 = new android.util.JsonReader     // Catch:{ FileNotFoundException -> 0x01a0 }
                    java.io.FileReader r4 = new java.io.FileReader     // Catch:{ FileNotFoundException -> 0x01a0 }
                    r4.<init>(r13)     // Catch:{ FileNotFoundException -> 0x01a0 }
                    r3.<init>(r4)     // Catch:{ FileNotFoundException -> 0x01a0 }
                    java.util.ArrayList r2 = r2.getListFromJsonFile(r3)     // Catch:{ FileNotFoundException -> 0x01a0 }
                    r0.addAll(r2)     // Catch:{ FileNotFoundException -> 0x01a0 }
                    com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager r2 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.this     // Catch:{ FileNotFoundException -> 0x01a0 }
                    java.util.Map r2 = r2.processedKeyMap     // Catch:{ FileNotFoundException -> 0x01a0 }
                    r2.put(r15, r0)     // Catch:{ FileNotFoundException -> 0x01a0 }
                L_0x0273:
                    r11.putBoolean(r12, r1)
                    return r11
                L_0x0277:
                    r0 = move-exception
                    goto L_0x01da
                L_0x027a:
                    java.util.Iterator r2 = r8.iterator()
                L_0x027e:
                    boolean r0 = r2.hasNext()
                    if (r0 == 0) goto L_0x02b3
                    java.lang.Object r0 = r2.next()
                    android.util.JsonReader r0 = (android.util.JsonReader) r0
                    r0.close()     // Catch:{ Exception -> 0x0290 }
                    r0 = r16
                    goto L_0x0296
                L_0x0290:
                    r0 = move-exception
                    r3 = r0
                    java.lang.String r0 = android.util.Log.getStackTraceString(r3)
                L_0x0296:
                    java.lang.String r3 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.TAG
                    java.lang.StringBuilder r4 = new java.lang.StringBuilder
                    r4.<init>()
                    r4.append(r9)
                    r4.append(r15)
                    r4.append(r10)
                    r4.append(r0)
                    java.lang.String r0 = r4.toString()
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r3, r0)
                    goto L_0x027e
                L_0x02b3:
                    if (r19 == 0) goto L_0x02b8
                    r19.release()
                L_0x02b8:
                    com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager r0 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.this     // Catch:{ FileNotFoundException -> 0x02e8 }
                    java.util.Map r0 = r0.processedKeyMap     // Catch:{ FileNotFoundException -> 0x02e8 }
                    java.lang.Object r0 = r0.get(r15)     // Catch:{ FileNotFoundException -> 0x02e8 }
                    java.util.List r0 = (java.util.List) r0     // Catch:{ FileNotFoundException -> 0x02e8 }
                    if (r0 != 0) goto L_0x02cb
                    java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ FileNotFoundException -> 0x02e8 }
                    r0.<init>()     // Catch:{ FileNotFoundException -> 0x02e8 }
                L_0x02cb:
                    com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager r2 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.this     // Catch:{ FileNotFoundException -> 0x02e8 }
                    android.util.JsonReader r3 = new android.util.JsonReader     // Catch:{ FileNotFoundException -> 0x02e8 }
                    java.io.FileReader r4 = new java.io.FileReader     // Catch:{ FileNotFoundException -> 0x02e8 }
                    r4.<init>(r13)     // Catch:{ FileNotFoundException -> 0x02e8 }
                    r3.<init>(r4)     // Catch:{ FileNotFoundException -> 0x02e8 }
                    java.util.ArrayList r2 = r2.getListFromJsonFile(r3)     // Catch:{ FileNotFoundException -> 0x02e8 }
                    r0.addAll(r2)     // Catch:{ FileNotFoundException -> 0x02e8 }
                    com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager r2 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.this     // Catch:{ FileNotFoundException -> 0x02e8 }
                    java.util.Map r2 = r2.processedKeyMap     // Catch:{ FileNotFoundException -> 0x02e8 }
                    r2.put(r15, r0)     // Catch:{ FileNotFoundException -> 0x02e8 }
                    goto L_0x02ec
                L_0x02e8:
                    r0 = move-exception
                    r0.printStackTrace()
                L_0x02ec:
                    throw r1
                L_0x02ed:
                    r9 = r12
                    r11 = r13
                    java.lang.String r0 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.TAG
                    java.lang.StringBuilder r1 = new java.lang.StringBuilder
                    r1.<init>()
                    r1.append(r9)
                    r1.append(r15)
                    java.lang.String r2 = "] PUT_RECORD: pathList is null or totalCount is zero"
                    r1.append(r2)
                    java.lang.String r1 = r1.toString()
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r0, r1)
                    return r11
                */
                throw new UnsupportedOperationException("Method not decompiled: com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.C06213.handleServiceAction(android.content.Context, java.lang.Object, java.lang.String, android.os.Bundle):android.os.Bundle");
            }
        });
        this.serviceHandlerMap.put("backupPrepare", new IServiceHandler() {
            public Bundle handleServiceAction(final Context context, Object obj, String str, Bundle bundle) {
                String access$000 = RecordClientManager.TAG;
                LOG.m15i(access$000, "[" + str + "] BACKUP_PREPARE");
                final Bundle bundle2 = new Bundle();
                final IRecordClient iRecordClient = (IRecordClient) obj;
                BackupMetaManager.getInstance(context).setCanceled(str, false);
                RecordClientManager.this.pfdMap.put(str, new ArrayList());
                iRecordClient.initialize(context, new IBackupClient.ResultListener() {
                    public void onError(int i) {
                        bundle2.putInt("reason_code", i);
                    }

                    public void onSuccess() {
                        bundle2.putBoolean("is_success", iRecordClient.isBackupPrepare(context));
                    }
                });
                return bundle2;
            }
        });
        this.serviceHandlerMap.put("backupComplete", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                Bundle bundle2 = new Bundle();
                boolean z = bundle.getBoolean("is_success");
                BackupMetaManager.getInstance(context).setCanceled(str, false);
                List<ParcelFileDescriptor> list = (List) RecordClientManager.this.pfdMap.get(str);
                if (list != null) {
                    for (ParcelFileDescriptor parcelFileDescriptor : list) {
                        if (parcelFileDescriptor != null) {
                            try {
                                parcelFileDescriptor.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (z) {
                    BackupMetaManager.getInstance(context).setLastBackupTime(str, System.currentTimeMillis());
                    ((IBackupClient) obj).backupCompleted(context);
                } else {
                    ((IBackupClient) obj).backupFailed(context);
                }
                String access$000 = RecordClientManager.TAG;
                LOG.m15i(access$000, "[" + str + "] BACKUP_COMPLETE");
                return bundle2;
            }
        });
        this.serviceHandlerMap.put("restorePrepare", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                String access$000 = RecordClientManager.TAG;
                LOG.m15i(access$000, "[" + str + "] RESTORE_PREPARE");
                Bundle bundle2 = new Bundle();
                BackupMetaManager.getInstance(context).setCanceled(str, false);
                RecordClientManager.this.pfdMap.put(str, new ArrayList());
                RecordClientManager.this.processedKeyMap.put(str, new ArrayList());
                final Bundle bundle3 = bundle2;
                final Object obj2 = obj;
                final Context context2 = context;
                final Bundle bundle4 = bundle;
                ((IRecordClient) obj).initialize(context, new IBackupClient.ResultListener() {
                    public void onError(int i) {
                        bundle3.putInt("reason_code", i);
                    }

                    public void onSuccess() {
                        bundle3.putBoolean("is_success", ((IBackupClient) obj2).isRestorePrepare(context2, bundle4));
                    }
                });
                return bundle2;
            }
        });
        this.serviceHandlerMap.put("restoreComplete", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                boolean z = bundle.getBoolean("is_success");
                Bundle bundle2 = new Bundle();
                List list = (List) RecordClientManager.this.processedKeyMap.get(str);
                if (list == null) {
                    list = new ArrayList();
                }
                String access$000 = RecordClientManager.TAG;
                LOG.m15i(access$000, "[" + str + "] RESTORE_COMPLETE: isSuccess: " + z + ", processedKeyListSize: " + list.size());
                BackupMetaManager.getInstance(context).setCanceled(str, false);
                List<ParcelFileDescriptor> list2 = (List) RecordClientManager.this.pfdMap.get(str);
                if (list2 != null) {
                    for (ParcelFileDescriptor parcelFileDescriptor : list2) {
                        if (parcelFileDescriptor != null) {
                            try {
                                parcelFileDescriptor.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (z) {
                    Iterator<String> it = ReuseDBHelper.getInstance(context).getReusePathList(str).iterator();
                    while (it.hasNext()) {
                        new File(RecordClientManager.this.getFileDirectory(context), it.next()).delete();
                    }
                    ReuseDBHelper.getInstance(context).clearReuseFile(str);
                    ((IBackupClient) obj).restoreCompleted(context, (ArrayList) list);
                } else {
                    ((IBackupClient) obj).restoreFailed(context, (ArrayList) list);
                }
                return bundle2;
            }
        });
        this.serviceHandlerMap.put("deleteRestoreFile", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                ArrayList<String> stringArrayList = bundle.getStringArrayList("path_list");
                if (stringArrayList != null) {
                    String access$000 = RecordClientManager.TAG;
                    LOG.m15i(access$000, "[" + str + "] DELETE_RESTORE_FILE: " + stringArrayList.size());
                    Iterator<String> it = stringArrayList.iterator();
                    while (it.hasNext()) {
                        new File(context.getFilesDir(), it.next()).delete();
                    }
                }
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("is_success", true);
                return bundle2;
            }
        });
        this.serviceHandlerMap.put("completeFile", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                String access$000 = RecordClientManager.TAG;
                LOG.m15i(access$000, "[" + str + "] COMPLETE_FILE");
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("is_success", true);
                String string = bundle.getString(DialogFactory.BUNDLE_PATH);
                if (string == null) {
                    bundle2.putBoolean("is_success", false);
                    return bundle2;
                }
                File file = new File(context.getFilesDir(), string);
                BNRFile bNRFile = new BNRFile(string, bundle.getString("checksum"), bundle.getString("startKey"), bundle.getString("nextKey"), bundle.getBoolean("complete"));
                bNRFile.setSize(file.length());
                bNRFile.setOffset(file.length());
                if (ReuseDBHelper.getInstance(context).addReuseFile(str, bNRFile) == -1) {
                    bundle2.putBoolean("is_success", false);
                }
                return bundle2;
            }
        });
        this.serviceHandlerMap.put("restoreFile", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                LOG.m15i(RecordClientManager.TAG, "[" + str + "] RESTORE_FILE");
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("is_success", true);
                String string = bundle.getString(DialogFactory.BUNDLE_PATH, (String) null);
                String string2 = bundle.getString("startKey", "0");
                String string3 = bundle.getString("nextKey", "0");
                boolean z = bundle.getBoolean("complete", false);
                if (string == null) {
                    string = str + "_" + "restoreitem" + "_" + string2;
                }
                try {
                    ParcelFileDescriptor open = ParcelFileDescriptor.open(new File(RecordClientManager.this.getFileDirectory(context), string), 939524096);
                    bundle2.putString(DialogFactory.BUNDLE_PATH, string);
                    bundle2.putParcelable("file_descriptor", open);
                    ReuseDBHelper.getInstance(context).addReuseFile(str, new BNRFile(string, string2, string3, z));
                    List list = (List) RecordClientManager.this.pfdMap.get(str);
                    if (list == null) {
                        list = new ArrayList();
                    }
                    list.add(open);
                    RecordClientManager.this.pfdMap.put(str, list);
                    return bundle2;
                } catch (FileNotFoundException unused) {
                    bundle2.putBoolean("is_success", false);
                    return bundle2;
                }
            }
        });
        this.serviceHandlerMap.put("checkAndUpdateReuseDB", new IServiceHandler() {
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v10, resolved type: int} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v13, resolved type: com.samsung.android.scloud.oem.lib.backup.ReuseDBHelper} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v15, resolved type: com.samsung.android.scloud.oem.lib.backup.ReuseDBHelper} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v17, resolved type: com.samsung.android.scloud.oem.lib.backup.ReuseDBHelper} */
            /* JADX WARNING: Multi-variable type inference failed */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public android.os.Bundle handleServiceAction(android.content.Context r24, java.lang.Object r25, java.lang.String r26, android.os.Bundle r27) {
                /*
                    r23 = this;
                    r1 = r26
                    java.lang.String r2 = "'"
                    java.lang.String r3 = "path = '"
                    java.lang.String r4 = "offset"
                    java.lang.String r5 = "complete"
                    java.lang.String r0 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.TAG
                    java.lang.StringBuilder r6 = new java.lang.StringBuilder
                    r6.<init>()
                    java.lang.String r7 = "["
                    r6.append(r7)
                    r6.append(r1)
                    java.lang.String r8 = "] CHECK_AND_UPDATE_REUSE_DB"
                    r6.append(r8)
                    java.lang.String r6 = r6.toString()
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r0, r6)
                    android.os.Bundle r6 = new android.os.Bundle
                    r6.<init>()
                    r8 = 1
                    java.lang.String r0 = "is_success"
                    r6.putBoolean(r0, r8)
                    java.util.ArrayList r9 = new java.util.ArrayList
                    r9.<init>()
                    com.samsung.android.scloud.oem.lib.backup.ReuseDBHelper r15 = com.samsung.android.scloud.oem.lib.backup.ReuseDBHelper.getInstance(r24)
                    java.lang.String[] r13 = new java.lang.String[r8]
                    r17 = 0
                    java.lang.Integer r14 = java.lang.Integer.valueOf(r17)
                    r13[r17] = r1
                    r11 = 0
                    java.lang.String r12 = "sourcekey = ?"
                    r0 = 0
                    r16 = 0
                    r18 = 0
                    r10 = r15
                    r19 = r14
                    r14 = r0
                    r20 = r15
                    r15 = r16
                    r16 = r18
                    android.database.Cursor r10 = r10.query(r11, r12, r13, r14, r15, r16)
                L_0x005b:
                    if (r10 == 0) goto L_0x01a3
                    boolean r0 = r10.moveToNext()     // Catch:{ all -> 0x019c }
                    if (r0 == 0) goto L_0x01a3
                    java.lang.String r0 = "path"
                    int r0 = r10.getColumnIndex(r0)     // Catch:{ all -> 0x019c }
                    java.lang.String r11 = r10.getString(r0)     // Catch:{ all -> 0x019c }
                    java.lang.String r0 = "checksum"
                    int r0 = r10.getColumnIndex(r0)     // Catch:{ all -> 0x019c }
                    java.lang.String r0 = r10.getString(r0)     // Catch:{ all -> 0x019c }
                    int r12 = r10.getColumnIndex(r5)     // Catch:{ all -> 0x019c }
                    int r12 = r10.getInt(r12)     // Catch:{ all -> 0x019c }
                    if (r12 != r8) goto L_0x0083
                    r12 = r8
                    goto L_0x0085
                L_0x0083:
                    r12 = r17
                L_0x0085:
                    r13 = 0
                    java.io.File r14 = new java.io.File     // Catch:{ FileNotFoundException -> 0x0148 }
                    java.io.File r15 = r24.getFilesDir()     // Catch:{ FileNotFoundException -> 0x0148 }
                    r14.<init>(r15, r11)     // Catch:{ FileNotFoundException -> 0x0148 }
                    boolean r15 = r14.isFile()     // Catch:{ FileNotFoundException -> 0x0148 }
                    if (r15 == 0) goto L_0x0111
                    long r15 = r14.length()     // Catch:{ FileNotFoundException -> 0x0148 }
                    r21 = 0
                    int r15 = (r15 > r21 ? 1 : (r15 == r21 ? 0 : -1))
                    if (r15 != 0) goto L_0x00a1
                    goto L_0x0111
                L_0x00a1:
                    if (r12 == 0) goto L_0x00e3
                    java.io.FileInputStream r12 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x0148 }
                    r12.<init>(r14)     // Catch:{ FileNotFoundException -> 0x0148 }
                    java.lang.String r12 = com.samsung.android.scloud.oem.lib.utils.HashUtil.getMD5HashString(r12)     // Catch:{ FileNotFoundException -> 0x0148 }
                    boolean r0 = r12.equals(r0)     // Catch:{ FileNotFoundException -> 0x0148 }
                    if (r0 != 0) goto L_0x00dd
                    r14.delete()     // Catch:{ FileNotFoundException -> 0x0148 }
                    android.content.ContentValues r0 = new android.content.ContentValues     // Catch:{ FileNotFoundException -> 0x0148 }
                    r0.<init>()     // Catch:{ FileNotFoundException -> 0x0148 }
                    r12 = r19
                    r0.put(r4, r12)     // Catch:{ FileNotFoundException -> 0x00db }
                    r0.put(r5, r12)     // Catch:{ FileNotFoundException -> 0x00db }
                    java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x00db }
                    r14.<init>()     // Catch:{ FileNotFoundException -> 0x00db }
                    r14.append(r3)     // Catch:{ FileNotFoundException -> 0x00db }
                    r14.append(r11)     // Catch:{ FileNotFoundException -> 0x00db }
                    r14.append(r2)     // Catch:{ FileNotFoundException -> 0x00db }
                    java.lang.String r14 = r14.toString()     // Catch:{ FileNotFoundException -> 0x00db }
                    r15 = r20
                    r15.update(r0, r14, r13)     // Catch:{ FileNotFoundException -> 0x0146 }
                    goto L_0x0195
                L_0x00db:
                    r0 = move-exception
                    goto L_0x014b
                L_0x00dd:
                    r12 = r19
                    r15 = r20
                    goto L_0x0195
                L_0x00e3:
                    r12 = r19
                    r15 = r20
                    android.content.ContentValues r0 = new android.content.ContentValues     // Catch:{ FileNotFoundException -> 0x0146 }
                    r0.<init>()     // Catch:{ FileNotFoundException -> 0x0146 }
                    long r18 = r14.length()     // Catch:{ FileNotFoundException -> 0x0146 }
                    java.lang.Long r14 = java.lang.Long.valueOf(r18)     // Catch:{ FileNotFoundException -> 0x0146 }
                    r0.put(r4, r14)     // Catch:{ FileNotFoundException -> 0x0146 }
                    r0.put(r5, r12)     // Catch:{ FileNotFoundException -> 0x0146 }
                    java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x0146 }
                    r14.<init>()     // Catch:{ FileNotFoundException -> 0x0146 }
                    r14.append(r3)     // Catch:{ FileNotFoundException -> 0x0146 }
                    r14.append(r11)     // Catch:{ FileNotFoundException -> 0x0146 }
                    r14.append(r2)     // Catch:{ FileNotFoundException -> 0x0146 }
                    java.lang.String r14 = r14.toString()     // Catch:{ FileNotFoundException -> 0x0146 }
                    r15.update(r0, r14, r13)     // Catch:{ FileNotFoundException -> 0x0146 }
                    goto L_0x0195
                L_0x0111:
                    r12 = r19
                    r15 = r20
                    r9.add(r11)     // Catch:{ FileNotFoundException -> 0x0146 }
                    android.content.ContentValues r0 = new android.content.ContentValues     // Catch:{ FileNotFoundException -> 0x0146 }
                    r0.<init>()     // Catch:{ FileNotFoundException -> 0x0146 }
                    long r18 = r14.length()     // Catch:{ FileNotFoundException -> 0x0146 }
                    java.lang.Long r14 = java.lang.Long.valueOf(r18)     // Catch:{ FileNotFoundException -> 0x0146 }
                    r0.put(r4, r14)     // Catch:{ FileNotFoundException -> 0x0146 }
                    r0.put(r5, r12)     // Catch:{ FileNotFoundException -> 0x0146 }
                    java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x0146 }
                    r14.<init>()     // Catch:{ FileNotFoundException -> 0x0146 }
                    r14.append(r3)     // Catch:{ FileNotFoundException -> 0x0146 }
                    r14.append(r11)     // Catch:{ FileNotFoundException -> 0x0146 }
                    r14.append(r2)     // Catch:{ FileNotFoundException -> 0x0146 }
                    java.lang.String r14 = r14.toString()     // Catch:{ FileNotFoundException -> 0x0146 }
                    r15.update(r0, r14, r13)     // Catch:{ FileNotFoundException -> 0x0146 }
                    r19 = r12
                    r20 = r15
                    goto L_0x005b
                L_0x0146:
                    r0 = move-exception
                    goto L_0x014d
                L_0x0148:
                    r0 = move-exception
                    r12 = r19
                L_0x014b:
                    r15 = r20
                L_0x014d:
                    java.lang.String r14 = com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.TAG     // Catch:{ all -> 0x019c }
                    java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x019c }
                    r8.<init>()     // Catch:{ all -> 0x019c }
                    r8.append(r7)     // Catch:{ all -> 0x019c }
                    r8.append(r1)     // Catch:{ all -> 0x019c }
                    java.lang.String r13 = "] FileNotFoundException"
                    r8.append(r13)     // Catch:{ all -> 0x019c }
                    java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x019c }
                    com.samsung.android.scloud.oem.lib.LOG.m14e(r14, r8, r0)     // Catch:{ all -> 0x019c }
                    java.io.File r0 = new java.io.File     // Catch:{ all -> 0x019c }
                    java.io.File r8 = r24.getFilesDir()     // Catch:{ all -> 0x019c }
                    r0.<init>(r8, r11)     // Catch:{ all -> 0x019c }
                    r0.delete()     // Catch:{ all -> 0x019c }
                    android.content.ContentValues r0 = new android.content.ContentValues     // Catch:{ all -> 0x019c }
                    r0.<init>()     // Catch:{ all -> 0x019c }
                    r0.put(r4, r12)     // Catch:{ all -> 0x019c }
                    r0.put(r5, r12)     // Catch:{ all -> 0x019c }
                    java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x019c }
                    r8.<init>()     // Catch:{ all -> 0x019c }
                    r8.append(r3)     // Catch:{ all -> 0x019c }
                    r8.append(r11)     // Catch:{ all -> 0x019c }
                    r8.append(r2)     // Catch:{ all -> 0x019c }
                    java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x019c }
                    r11 = 0
                    r15.update(r0, r8, r11)     // Catch:{ all -> 0x019c }
                L_0x0195:
                    r19 = r12
                    r20 = r15
                    r8 = 1
                    goto L_0x005b
                L_0x019c:
                    r0 = move-exception
                    if (r10 == 0) goto L_0x01a2
                    r10.close()
                L_0x01a2:
                    throw r0
                L_0x01a3:
                    r15 = r20
                    if (r10 == 0) goto L_0x01aa
                    r10.close()
                L_0x01aa:
                    int r0 = r9.size()
                    if (r0 <= 0) goto L_0x01b3
                    r15.removeReuseFile(r9)
                L_0x01b3:
                    return r6
                */
                throw new UnsupportedOperationException("Method not decompiled: com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager.C061611.handleServiceAction(android.content.Context, java.lang.Object, java.lang.String, android.os.Bundle):android.os.Bundle");
            }
        });
        this.serviceHandlerMap.put("clearReuseFileDB", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                String access$000 = RecordClientManager.TAG;
                LOG.m15i(access$000, "[" + str + "] CLEAR_REUSE_FILE_DB");
                Bundle bundle2 = new Bundle();
                Iterator<String> it = ReuseDBHelper.getInstance(context).getReusePathList(str).iterator();
                while (it.hasNext()) {
                    new File(RecordClientManager.this.getFileDirectory(context), it.next()).delete();
                }
                ReuseDBHelper.getInstance(context).clearReuseFile(str);
                bundle2.putBoolean("is_success", true);
                return bundle2;
            }
        });
        this.serviceHandlerMap.put("requestCancel", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                String access$000 = RecordClientManager.TAG;
                LOG.m15i(access$000, "[" + str + "] REQUEST_CANCEL");
                Bundle bundle2 = new Bundle();
                BackupMetaManager.getInstance(context).setCanceled(str, true);
                return bundle2;
            }
        });
    }

    public IServiceHandler getServiceHandler(String str) {
        return this.serviceHandlerMap.get(str);
    }

    public Object getClient(String str) {
        return this.backupClient;
    }

    /* access modifiers changed from: private */
    public ArrayList<String> getListFromJsonFile(JsonReader jsonReader) {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                arrayList.add(SCloudParser.toString(jsonReader));
            }
            try {
                jsonReader.endArray();
                jsonReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e2) {
            e2.printStackTrace();
            try {
                jsonReader.endArray();
                jsonReader.close();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        } catch (Throwable th) {
            try {
                jsonReader.endArray();
                jsonReader.close();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
            throw th;
        }
        return arrayList;
    }

    public void setDataDirectory(String str) {
        this.dataDirectory = str;
    }

    /* access modifiers changed from: private */
    public File getFileDirectory(Context context) {
        String str = this.dataDirectory;
        if (str == null) {
            return context.getFilesDir();
        }
        File file = new File(str);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }
}
