package com.samsung.android.scloud.oem.lib.bnr;

import android.content.Context;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import com.samsung.android.scloud.oem.lib.LOG;
import com.samsung.android.scloud.oem.lib.backup.BackupMetaManager;
import com.samsung.android.scloud.oem.lib.common.IClientHelper;
import com.samsung.android.scloud.oem.lib.common.IServiceHandler;
import com.samsung.android.scloud.oem.lib.utils.FileTool;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BNRClientHelper extends IClientHelper {
    /* access modifiers changed from: private */
    public static final String TAG = "BNRClientHelper";
    /* access modifiers changed from: private */
    public String OPERATION = "";
    private ISCloudBNRClient backupClient;
    /* access modifiers changed from: private */
    public final List<String> downloadFileList = new ArrayList();
    /* access modifiers changed from: private */
    public final List<String> processedKeyList = new ArrayList();
    /* access modifiers changed from: private */
    public final List<String> restoreFileList = new ArrayList();
    private final Map<String, IServiceHandler> serviceHandlerMap = new HashMap();

    public BNRClientHelper(ISCloudBNRClient iSCloudBNRClient) {
        this.backupClient = iSCloudBNRClient;
        this.serviceHandlerMap.put("getClientInfo", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                Bundle bundle2 = new Bundle();
                ISCloudBNRClient iSCloudBNRClient = (ISCloudBNRClient) obj;
                boolean isSupportBackup = iSCloudBNRClient.isSupportBackup(context);
                boolean isEnableBackup = iSCloudBNRClient.isEnableBackup(context);
                String access$000 = BNRClientHelper.TAG;
                LOG.m15i(access$000, "[" + str + "] GET_CLIENT_INFO, " + str);
                String label = iSCloudBNRClient.getLabel(context);
                String description = iSCloudBNRClient.getDescription(context);
                bundle2.putBoolean("support_backup", isSupportBackup);
                bundle2.putString(DialogFactory.BUNDLE_NAME, str);
                bundle2.putBoolean("is_enable_backup", isEnableBackup);
                bundle2.putString("label", label);
                bundle2.putString("description", description);
                return bundle2;
            }
        });
        this.serviceHandlerMap.put("backupPrepare", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                String access$000 = BNRClientHelper.TAG;
                LOG.m15i(access$000, "[" + str + "] BACKUP_PREPARE");
                String unused = BNRClientHelper.this.OPERATION = "backup";
                BNRClientHelper.this.clearData();
                Bundle bundle2 = new Bundle();
                boolean backupPrepare = ((ISCloudBNRClient) obj).backupPrepare(context);
                String access$0002 = BNRClientHelper.TAG;
                LOG.m15i(access$0002, "[" + str + "] BACKUP_PREPARE, result: " + backupPrepare);
                bundle2.putBoolean("is_success", backupPrepare);
                return bundle2;
            }
        });
        this.serviceHandlerMap.put("getItemKey", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                String str2 = str;
                Bundle bundle2 = bundle;
                Bundle bundle3 = new Bundle();
                int i = bundle2.getInt("start");
                int i2 = bundle2.getInt("max_count");
                String access$000 = BNRClientHelper.TAG;
                LOG.m15i(access$000, "[" + str2 + "] GET_ITEM_KEY, start: " + i + ", max: " + i2);
                HashMap<String, Long> itemKey = ((ISCloudBNRClient) obj).getItemKey(context, i, i2);
                if (itemKey == null) {
                    String access$0002 = BNRClientHelper.TAG;
                    LOG.m15i(access$0002, "[" + str2 + "] GET_ITEM_KEY, nothing to backup");
                    bundle3.putBoolean("is_continue", false);
                    bundle3.putBoolean("is_success", true);
                } else if (itemKey.size() == 0) {
                    String access$0003 = BNRClientHelper.TAG;
                    LOG.m15i(access$0003, "[" + str2 + "] GET_ITEM_KEY, value is incorrect, return err");
                    bundle3.putBoolean("is_success", false);
                } else {
                    String access$0004 = BNRClientHelper.TAG;
                    LOG.m15i(access$0004, "[" + str2 + "] GET_ITEM_KEY, count: " + itemKey.size());
                    String[] strArr = new String[itemKey.size()];
                    long[] jArr = new long[itemKey.size()];
                    int i3 = 0;
                    for (Map.Entry next : itemKey.entrySet()) {
                        String access$0005 = BNRClientHelper.TAG;
                        LOG.m12d(access$0005, "[" + str2 + "] GET_ITEM_KEY, item: " + ((String) next.getKey()) + ", " + next.getValue());
                        strArr[i3] = (String) next.getKey();
                        jArr[i3] = ((Long) next.getValue()).longValue();
                        i3++;
                    }
                    bundle3.putBoolean("is_continue", itemKey.size() >= i2);
                    bundle3.putStringArray("local_id", strArr);
                    bundle3.putLongArray("timestamp", jArr);
                    bundle3.putBoolean("is_success", true);
                }
                return bundle3;
            }
        });
        this.serviceHandlerMap.put("getFileMeta", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                String str2 = str;
                Bundle bundle2 = bundle;
                Bundle bundle3 = new Bundle();
                int i = bundle2.getInt("start");
                int i2 = bundle2.getInt("max_count");
                String access$000 = BNRClientHelper.TAG;
                StringBuilder sb = new StringBuilder();
                String str3 = "[";
                sb.append(str3);
                sb.append(str2);
                sb.append("] GET_FILE_META, start: ");
                sb.append(i);
                sb.append(", max: ");
                sb.append(i2);
                LOG.m15i(access$000, sb.toString());
                ArrayList<BNRFile> fileMeta = ((ISCloudBNRClient) obj).getFileMeta(context, i, i2);
                String str4 = "is_success";
                if (fileMeta == null) {
                    LOG.m15i(BNRClientHelper.TAG, str3 + str2 + "] GET_FILE_META, nothing to backup");
                    bundle3.putBoolean("is_continue", false);
                    bundle3.putBoolean(str4, true);
                } else if (fileMeta.size() == 0) {
                    LOG.m15i(BNRClientHelper.TAG, str3 + str2 + "] GET_FILE_META, value is incorrect, return err");
                    bundle3.putBoolean(str4, false);
                } else {
                    LOG.m15i(BNRClientHelper.TAG, str3 + str2 + "] GET_FILE_META, count: " + fileMeta.size());
                    String[] strArr = new String[fileMeta.size()];
                    long[] jArr = new long[fileMeta.size()];
                    boolean[] zArr = new boolean[fileMeta.size()];
                    long[] jArr2 = new long[fileMeta.size()];
                    Iterator<BNRFile> it = fileMeta.iterator();
                    int i3 = 0;
                    while (it.hasNext()) {
                        BNRFile next = it.next();
                        LOG.m12d(BNRClientHelper.TAG, str3 + str2 + "] GET_FILE_META, " + next.getPath() + ", " + next.getSize() + ", " + next.getisExternal() + ", " + next.getTimeStamp());
                        strArr[i3] = next.getPath();
                        jArr[i3] = next.getSize();
                        zArr[i3] = next.getisExternal();
                        jArr2[i3] = next.getTimeStamp();
                        str4 = str4;
                        i3++;
                        str3 = str3;
                        str2 = str;
                    }
                    String str5 = str4;
                    bundle3.putBoolean("is_continue", fileMeta.size() >= i2);
                    bundle3.putStringArray(DialogFactory.BUNDLE_PATH, strArr);
                    bundle3.putLongArray("size", jArr);
                    bundle3.putBooleanArray("external", zArr);
                    bundle3.putLongArray("timestamp", jArr2);
                    bundle3.putBoolean(str5, true);
                }
                return bundle3;
            }
        });
        this.serviceHandlerMap.put("backupItem", new IServiceHandler() {
            /* JADX WARNING: Removed duplicated region for block: B:46:0x022a A[SYNTHETIC, Splitter:B:46:0x022a] */
            /* JADX WARNING: Removed duplicated region for block: B:53:0x0238 A[SYNTHETIC, Splitter:B:53:0x0238] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public android.os.Bundle handleServiceAction(android.content.Context r26, java.lang.Object r27, java.lang.String r28, android.os.Bundle r29) {
                /*
                    r25 = this;
                    r1 = r28
                    r0 = r29
                    java.lang.String r2 = "timestamp"
                    java.lang.String r3 = "value"
                    java.lang.String r4 = "key"
                    java.lang.String r5 = ", "
                    java.lang.String r6 = "] BACKUP_ITEM, item: "
                    android.os.Bundle r7 = new android.os.Bundle
                    r7.<init>()
                    java.lang.String r8 = "to_upload_list"
                    java.util.ArrayList r8 = r0.getStringArrayList(r8)
                    java.lang.String r9 = "file_descriptor"
                    android.os.Parcelable r9 = r0.getParcelable(r9)
                    android.os.ParcelFileDescriptor r9 = (android.os.ParcelFileDescriptor) r9
                    java.lang.String r10 = "is_success"
                    r11 = 0
                    java.lang.String r12 = "["
                    if (r9 != 0) goto L_0x0047
                    java.lang.String r0 = com.samsung.android.scloud.oem.lib.bnr.BNRClientHelper.TAG
                    java.lang.StringBuilder r2 = new java.lang.StringBuilder
                    r2.<init>()
                    r2.append(r12)
                    r2.append(r1)
                    java.lang.String r1 = "] BACKUP_ITEM, pfd is null"
                    r2.append(r1)
                    java.lang.String r1 = r2.toString()
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r0, r1)
                    r7.putBoolean(r10, r11)
                    return r7
                L_0x0047:
                    java.lang.String r13 = "max_size"
                    long r13 = r0.getLong(r13)
                    if (r8 == 0) goto L_0x0071
                    java.lang.String r0 = com.samsung.android.scloud.oem.lib.bnr.BNRClientHelper.TAG
                    java.lang.StringBuilder r15 = new java.lang.StringBuilder
                    r15.<init>()
                    r15.append(r12)
                    r15.append(r1)
                    java.lang.String r11 = "] BACKUP_ITEM, toUploadList: "
                    r15.append(r11)
                    int r11 = r8.size()
                    r15.append(r11)
                    java.lang.String r11 = r15.toString()
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r0, r11)
                L_0x0071:
                    r0 = r27
                    com.samsung.android.scloud.oem.lib.bnr.ISCloudBNRClient r0 = (com.samsung.android.scloud.oem.lib.bnr.ISCloudBNRClient) r0
                    r11 = r26
                    java.util.ArrayList r0 = r0.backupItem(r11, r8)
                    if (r0 == 0) goto L_0x0244
                    int r8 = r0.size()
                    if (r8 != 0) goto L_0x0085
                    goto L_0x0244
                L_0x0085:
                    java.lang.String r8 = com.samsung.android.scloud.oem.lib.bnr.BNRClientHelper.TAG
                    java.lang.StringBuilder r11 = new java.lang.StringBuilder
                    r11.<init>()
                    r11.append(r12)
                    r11.append(r1)
                    java.lang.String r15 = "] BACKUP_ITEM, count: "
                    r11.append(r15)
                    int r15 = r0.size()
                    r11.append(r15)
                    java.lang.String r11 = r11.toString()
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r8, r11)
                    r8 = 0
                    java.io.FileWriter r11 = new java.io.FileWriter     // Catch:{ IOException -> 0x0206, JSONException -> 0x0204 }
                    java.io.FileDescriptor r15 = r9.getFileDescriptor()     // Catch:{ IOException -> 0x0206, JSONException -> 0x0204 }
                    r11.<init>(r15)     // Catch:{ IOException -> 0x0206, JSONException -> 0x0204 }
                    int r8 = r0.size()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    java.lang.String[] r8 = new java.lang.String[r8]     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r11.write(r12)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r15 = 0
                    java.lang.Object r16 = r0.get(r15)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r17 = r16
                    com.samsung.android.scloud.oem.lib.bnr.BNRItem r17 = (com.samsung.android.scloud.oem.lib.bnr.BNRItem) r17     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    java.lang.String r16 = r17.getLocalId()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r8[r15] = r16     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    long r18 = r17.getSize()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    org.json.JSONObject r15 = new org.json.JSONObject     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r15.<init>()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r26 = r8
                    java.lang.String r8 = com.samsung.android.scloud.oem.lib.bnr.BNRClientHelper.TAG     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r20 = r13
                    java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r13.<init>()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r13.append(r12)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r13.append(r1)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r13.append(r6)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    java.lang.String r14 = r17.getLocalId()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r13.append(r14)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r13.append(r5)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r14 = r5
                    r22 = r6
                    long r5 = r17.getTimeStamp()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r13.append(r5)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    java.lang.String r5 = r13.toString()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    com.samsung.android.scloud.oem.lib.LOG.m12d(r8, r5)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    java.lang.String r5 = r17.getLocalId()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r15.put(r4, r5)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    java.lang.String r5 = r17.getData()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r15.put(r3, r5)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    long r5 = r17.getTimeStamp()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r15.put(r2, r5)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    java.lang.String r5 = r15.toString()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r11.write(r5)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    int r5 = r0.size()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r6 = 1
                    if (r5 <= r6) goto L_0x01de
                    r5 = r6
                L_0x0127:
                    int r8 = r0.size()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    if (r5 >= r8) goto L_0x01de
                    java.lang.Object r8 = r0.get(r5)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    com.samsung.android.scloud.oem.lib.bnr.BNRItem r8 = (com.samsung.android.scloud.oem.lib.bnr.BNRItem) r8     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    if (r8 != 0) goto L_0x016b
                    java.lang.String r0 = com.samsung.android.scloud.oem.lib.bnr.BNRClientHelper.TAG     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r2.<init>()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r2.append(r12)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r2.append(r1)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    java.lang.String r3 = "] BACKUP_ITEM, item is incorrect: "
                    r2.append(r3)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r2.append(r5)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    java.lang.String r3 = ", return err"
                    r2.append(r3)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    java.lang.String r2 = r2.toString()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r0, r2)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r11.close()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r2 = 0
                    r7.putBoolean(r10, r2)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r11.close()     // Catch:{ IOException -> 0x0166 }
                    r9.close()     // Catch:{ IOException -> 0x0166 }
                    goto L_0x016a
                L_0x0166:
                    r0 = move-exception
                    r0.printStackTrace()
                L_0x016a:
                    return r7
                L_0x016b:
                    long r23 = r8.getSize()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    long r23 = r18 + r23
                    int r13 = (r23 > r20 ? 1 : (r23 == r20 ? 0 : -1))
                    if (r13 < 0) goto L_0x0176
                    goto L_0x01de
                L_0x0176:
                    java.lang.String r13 = r8.getLocalId()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r26[r5] = r13     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    long r23 = r8.getSize()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    long r18 = r18 + r23
                    java.lang.String r13 = ","
                    r11.write(r13)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    java.lang.String r13 = com.samsung.android.scloud.oem.lib.bnr.BNRClientHelper.TAG     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r6.<init>()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r6.append(r12)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r6.append(r1)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r29 = r0
                    r0 = r22
                    r6.append(r0)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r22 = r0
                    java.lang.String r0 = r8.getLocalId()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r6.append(r0)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r0 = r14
                    r6.append(r0)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r14 = r0
                    long r0 = r8.getTimeStamp()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r6.append(r0)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    java.lang.String r0 = r6.toString()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    com.samsung.android.scloud.oem.lib.LOG.m12d(r13, r0)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    java.lang.String r0 = r8.getLocalId()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r15.put(r4, r0)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    java.lang.String r0 = r8.getData()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r15.put(r3, r0)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    long r0 = r8.getTimeStamp()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r15.put(r2, r0)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    java.lang.String r0 = r15.toString()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r11.write(r0)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    int r5 = r5 + 1
                    r1 = r28
                    r0 = r29
                    r6 = 1
                    goto L_0x0127
                L_0x01de:
                    java.lang.String r0 = "]"
                    r11.write(r0)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r11.flush()     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r0 = 1
                    r7.putBoolean(r10, r0)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    java.lang.String r0 = "local_id"
                    r1 = r26
                    r7.putStringArray(r0, r1)     // Catch:{ IOException -> 0x01fd, JSONException -> 0x01fb, all -> 0x01f8 }
                    r11.close()     // Catch:{ IOException -> 0x0231 }
                    r9.close()     // Catch:{ IOException -> 0x0231 }
                    goto L_0x0235
                L_0x01f8:
                    r0 = move-exception
                    r1 = r0
                    goto L_0x0236
                L_0x01fb:
                    r0 = move-exception
                    goto L_0x01fe
                L_0x01fd:
                    r0 = move-exception
                L_0x01fe:
                    r8 = r11
                    goto L_0x0207
                L_0x0200:
                    r0 = move-exception
                    r1 = r0
                    r11 = r8
                    goto L_0x0236
                L_0x0204:
                    r0 = move-exception
                    goto L_0x0207
                L_0x0206:
                    r0 = move-exception
                L_0x0207:
                    java.lang.String r1 = com.samsung.android.scloud.oem.lib.bnr.BNRClientHelper.TAG     // Catch:{ all -> 0x0200 }
                    java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0200 }
                    r2.<init>()     // Catch:{ all -> 0x0200 }
                    r2.append(r12)     // Catch:{ all -> 0x0200 }
                    r3 = r28
                    r2.append(r3)     // Catch:{ all -> 0x0200 }
                    java.lang.String r3 = "] Exception"
                    r2.append(r3)     // Catch:{ all -> 0x0200 }
                    java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0200 }
                    com.samsung.android.scloud.oem.lib.LOG.m14e(r1, r2, r0)     // Catch:{ all -> 0x0200 }
                    r1 = 0
                    r7.putBoolean(r10, r1)     // Catch:{ all -> 0x0200 }
                    if (r8 == 0) goto L_0x022d
                    r8.close()     // Catch:{ IOException -> 0x0231 }
                L_0x022d:
                    r9.close()     // Catch:{ IOException -> 0x0231 }
                    goto L_0x0235
                L_0x0231:
                    r0 = move-exception
                    r0.printStackTrace()
                L_0x0235:
                    return r7
                L_0x0236:
                    if (r11 == 0) goto L_0x023b
                    r11.close()     // Catch:{ IOException -> 0x023f }
                L_0x023b:
                    r9.close()     // Catch:{ IOException -> 0x023f }
                    goto L_0x0243
                L_0x023f:
                    r0 = move-exception
                    r0.printStackTrace()
                L_0x0243:
                    throw r1
                L_0x0244:
                    r3 = r1
                    java.lang.String r0 = com.samsung.android.scloud.oem.lib.bnr.BNRClientHelper.TAG
                    java.lang.StringBuilder r1 = new java.lang.StringBuilder
                    r1.<init>()
                    r1.append(r12)
                    r1.append(r3)
                    java.lang.String r2 = "] BACKUP_ITEM, value is incorrect, return err"
                    r1.append(r2)
                    java.lang.String r1 = r1.toString()
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r0, r1)
                    r1 = 0
                    r7.putBoolean(r10, r1)
                    return r7
                */
                throw new UnsupportedOperationException("Method not decompiled: com.samsung.android.scloud.oem.lib.bnr.BNRClientHelper.C06375.handleServiceAction(android.content.Context, java.lang.Object, java.lang.String, android.os.Bundle):android.os.Bundle");
            }
        });
        this.serviceHandlerMap.put("getFilePath", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                String access$000 = BNRClientHelper.TAG;
                LOG.m15i(access$000, "[" + str + "] GET_FILE_PATH, " + BNRClientHelper.this.OPERATION);
                String string = bundle.getString(DialogFactory.BUNDLE_PATH);
                boolean z = bundle.getBoolean("external");
                Bundle bundle2 = new Bundle();
                String access$0002 = BNRClientHelper.TAG;
                LOG.m12d(access$0002, "[" + str + "] GET_FILE_PATH, " + string + ", " + z);
                String filePath = ((ISCloudBNRClient) obj).getFilePath(context, string, z, BNRClientHelper.this.OPERATION);
                if (filePath != null) {
                    String access$0003 = BNRClientHelper.TAG;
                    LOG.m12d(access$0003, "[" + str + "] GET_FILE_PATH, return path : " + filePath);
                    bundle2.putBoolean("is_success", true);
                    bundle2.putString("real_path", filePath);
                } else {
                    String access$0004 = BNRClientHelper.TAG;
                    LOG.m15i(access$0004, "[" + str + "] GET_FILE_PATH, value is incorrect, return err");
                    bundle2.putBoolean("is_success", false);
                }
                return bundle2;
            }
        });
        this.serviceHandlerMap.put("backupComplete", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                boolean z = bundle.getBoolean("is_success");
                Bundle bundle2 = new Bundle();
                String access$000 = BNRClientHelper.TAG;
                LOG.m15i(access$000, "[" + str + "] BACKUP_COMPLETE, " + z);
                boolean backupComplete = ((ISCloudBNRClient) obj).backupComplete(context, z);
                if (backupComplete && z) {
                    BackupMetaManager.getInstance(context).setLastBackupTime(str, System.currentTimeMillis());
                }
                String access$0002 = BNRClientHelper.TAG;
                LOG.m15i(access$0002, "[" + str + "] BACKUP_COMPLETE, return: " + backupComplete);
                bundle2.putBoolean("is_success", backupComplete);
                return bundle2;
            }
        });
        this.serviceHandlerMap.put("restorePrepare", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                String access$000 = BNRClientHelper.TAG;
                LOG.m15i(access$000, "[" + str + "] RESTORE_PREPARE");
                String unused = BNRClientHelper.this.OPERATION = "restore";
                BNRClientHelper.this.clearRestoredData(context, obj);
                Bundle bundle2 = new Bundle();
                boolean restorePrepare = ((ISCloudBNRClient) obj).restorePrepare(context, bundle);
                String access$0002 = BNRClientHelper.TAG;
                LOG.m15i(access$0002, "[" + str + "] RESTORE_PREPARE, return: " + restorePrepare);
                bundle2.putBoolean("is_success", restorePrepare);
                return bundle2;
            }
        });
        this.serviceHandlerMap.put("restoreItem", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                Bundle bundle2 = new Bundle();
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                String access$000 = BNRClientHelper.TAG;
                LOG.m15i(access$000, "[" + str + "] RESTORE_ITEM, count: " + arrayList.size());
                BNRClientHelper.convertToBNRItems((ParcelFileDescriptor) bundle.getParcelable("file_descriptor"), arrayList);
                boolean restoreItem = ((ISCloudBNRClient) obj).restoreItem(context, arrayList, arrayList2);
                String access$0002 = BNRClientHelper.TAG;
                LOG.m15i(access$0002, "[" + str + "] RESTORE_ITEM, return: " + arrayList2.size() + ", " + restoreItem);
                if (arrayList2.size() > 0) {
                    Iterator it = arrayList2.iterator();
                    while (it.hasNext()) {
                        BNRClientHelper.this.addToList(0, (String) it.next());
                    }
                }
                bundle2.putBoolean("is_success", restoreItem);
                bundle2.putStringArray("inserted_id_list", (String[]) arrayList2.toArray(new String[arrayList2.size()]));
                return bundle2;
            }
        });
        this.serviceHandlerMap.put("restoreFile", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                Bundle bundle2 = new Bundle();
                BNRClientHelper bNRClientHelper = BNRClientHelper.this;
                bNRClientHelper.addToList(1, bundle.getString(DialogFactory.BUNDLE_PATH) + "_scloud_dwnload");
                String access$000 = BNRClientHelper.TAG;
                LOG.m12d(access$000, "[" + str + "] RESTORE_FILE, " + bundle.getString(DialogFactory.BUNDLE_PATH));
                String string = bundle.getString(DialogFactory.BUNDLE_PATH);
                if (!FileTool.fileCopy(string, bundle.getString(DialogFactory.BUNDLE_PATH) + "_scloud_origin")) {
                    bundle2.putBoolean("is_success", false);
                    return bundle2;
                }
                BNRClientHelper bNRClientHelper2 = BNRClientHelper.this;
                bNRClientHelper2.addToList(2, bundle.getString(DialogFactory.BUNDLE_PATH) + "_scloud_origin");
                if (!FileTool.fileCopy(bundle.getString(DialogFactory.BUNDLE_PATH) + "_scloud_dwnload", bundle.getString(DialogFactory.BUNDLE_PATH))) {
                    bundle2.putBoolean("is_success", false);
                    return bundle2;
                }
                bundle2.putBoolean("is_success", true);
                return bundle2;
            }
        });
        this.serviceHandlerMap.put("restoreComplete", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                boolean z = bundle.getBoolean("is_success");
                String access$000 = BNRClientHelper.TAG;
                LOG.m15i(access$000, "[" + str + "] RESTORE_COMPLETE, " + z);
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("is_success", true);
                if (z) {
                    String access$0002 = BNRClientHelper.TAG;
                    LOG.m15i(access$0002, "[" + str + "] RESTORE_COMPLETE, restoredKeyList size : " + BNRClientHelper.this.processedKeyList.size());
                    if (BNRClientHelper.this.processedKeyList.size() >= 0) {
                        if (!((ISCloudBNRClient) obj).restoreComplete(context, (String[]) BNRClientHelper.this.processedKeyList.toArray(new String[BNRClientHelper.this.processedKeyList.size()]))) {
                            String access$0003 = BNRClientHelper.TAG;
                            LOG.m15i(access$0003, "[" + str + "] RESTORE_COMPLETE, restoreComplete() return false ");
                            BNRClientHelper.this.clearRestoredData(context, obj);
                            bundle2.putBoolean("is_success", false);
                            return bundle2;
                        }
                        BNRClientHelper.this.processedKeyList.clear();
                    }
                    if (BNRClientHelper.this.restoreFileList.size() > 0) {
                        for (String str2 : BNRClientHelper.this.restoreFileList) {
                            File file = new File(str2);
                            if (file.exists()) {
                                String access$0004 = BNRClientHelper.TAG;
                                LOG.m15i(access$0004, "[" + str + "] clearPreRestoredData() delete, name : " + str2 + ", deleted : " + file.delete());
                            }
                        }
                    }
                    if (BNRClientHelper.this.downloadFileList.size() > 0) {
                        for (String str3 : BNRClientHelper.this.downloadFileList) {
                            File file2 = new File(str3);
                            if (file2.exists()) {
                                String access$0005 = BNRClientHelper.TAG;
                                LOG.m15i(access$0005, "[" + str + "] clearPreRestoredData() delete, name : " + str3 + ", deleted : " + file2.delete());
                            }
                        }
                        BNRClientHelper.this.downloadFileList.clear();
                    }
                } else {
                    BNRClientHelper.this.clearRestoredData(context, obj);
                    bundle2.putBoolean("is_success", true);
                }
                return bundle2;
            }
        });
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0082 A[SYNTHETIC, Splitter:B:20:0x0082] */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x008d A[SYNTHETIC, Splitter:B:25:0x008d] */
    /* JADX WARNING: Removed duplicated region for block: B:31:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void convertToBNRItems(android.os.ParcelFileDescriptor r8, java.util.List<com.samsung.android.scloud.oem.lib.bnr.BNRItem> r9) {
        /*
            r0 = 0
            java.io.FileInputStream r1 = new java.io.FileInputStream     // Catch:{ IOException -> 0x007c, JSONException -> 0x007a }
            java.io.FileDescriptor r2 = r8.getFileDescriptor()     // Catch:{ IOException -> 0x007c, JSONException -> 0x007a }
            r1.<init>(r2)     // Catch:{ IOException -> 0x007c, JSONException -> 0x007a }
            long r2 = r8.getStatSize()     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            int r8 = (int) r2     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            byte[] r8 = new byte[r8]     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            r1.read(r8)     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            java.lang.String r0 = new java.lang.String     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            r0.<init>(r8)     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            org.json.JSONArray r8 = new org.json.JSONArray     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            r8.<init>(r0)     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            r0 = 0
        L_0x001f:
            int r2 = r8.length()     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            if (r0 >= r2) goto L_0x006c
            org.json.JSONObject r2 = r8.optJSONObject(r0)     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            com.samsung.android.scloud.oem.lib.bnr.BNRItem r3 = new com.samsung.android.scloud.oem.lib.bnr.BNRItem     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            java.lang.String r4 = "key"
            java.lang.String r4 = r2.optString(r4)     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            java.lang.String r5 = "value"
            java.lang.String r5 = r2.optString(r5)     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            java.lang.String r6 = "timestamp"
            long r6 = r2.optLong(r6)     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            r3.<init>(r4, r5, r6)     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            java.lang.String r2 = TAG     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            r4.<init>()     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            java.lang.String r5 = "convertToBNRItems: "
            r4.append(r5)     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            java.lang.String r5 = r3.getLocalId()     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            r4.append(r5)     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            java.lang.String r5 = ", "
            r4.append(r5)     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            long r5 = r3.getTimeStamp()     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            r4.append(r5)     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            java.lang.String r4 = r4.toString()     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            com.samsung.android.scloud.oem.lib.LOG.m12d(r2, r4)     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            r9.add(r3)     // Catch:{ IOException -> 0x0074, JSONException -> 0x0072, all -> 0x0070 }
            int r0 = r0 + 1
            goto L_0x001f
        L_0x006c:
            r1.close()     // Catch:{ IOException -> 0x0086 }
            goto L_0x008a
        L_0x0070:
            r8 = move-exception
            goto L_0x008b
        L_0x0072:
            r8 = move-exception
            goto L_0x0075
        L_0x0074:
            r8 = move-exception
        L_0x0075:
            r0 = r1
            goto L_0x007d
        L_0x0077:
            r8 = move-exception
            r1 = r0
            goto L_0x008b
        L_0x007a:
            r8 = move-exception
            goto L_0x007d
        L_0x007c:
            r8 = move-exception
        L_0x007d:
            r8.printStackTrace()     // Catch:{ all -> 0x0077 }
            if (r0 == 0) goto L_0x008a
            r0.close()     // Catch:{ IOException -> 0x0086 }
            goto L_0x008a
        L_0x0086:
            r8 = move-exception
            r8.printStackTrace()
        L_0x008a:
            return
        L_0x008b:
            if (r1 == 0) goto L_0x0095
            r1.close()     // Catch:{ IOException -> 0x0091 }
            goto L_0x0095
        L_0x0091:
            r9 = move-exception
            r9.printStackTrace()
        L_0x0095:
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.samsung.android.scloud.oem.lib.bnr.BNRClientHelper.convertToBNRItems(android.os.ParcelFileDescriptor, java.util.List):void");
    }

    /* access modifiers changed from: private */
    public void clearData() {
        this.processedKeyList.clear();
        this.restoreFileList.clear();
        this.downloadFileList.clear();
    }

    /* access modifiers changed from: private */
    public void clearRestoredData(Context context, Object obj) {
        if (this.processedKeyList.size() > 0) {
            String str = TAG;
            LOG.m15i(str, "remove restored data in previous failed restoring.. - " + this.processedKeyList.size());
            List<String> list = this.processedKeyList;
            ((ISCloudBNRClient) obj).clearRestoreData(context, (String[]) list.toArray(new String[list.size()]));
            this.processedKeyList.clear();
        }
        if (this.restoreFileList.size() > 0) {
            String str2 = TAG;
            LOG.m15i(str2, "remove restored files in previous failed restoring.. - " + this.restoreFileList.size());
            for (String next : this.restoreFileList) {
                FileTool.fileCopy(next + "_scloud_origin", next);
            }
            this.restoreFileList.clear();
        }
        if (this.downloadFileList.size() > 0) {
            for (String next2 : this.downloadFileList) {
                File file = new File(next2);
                if (file.exists()) {
                    String str3 = TAG;
                    LOG.m15i(str3, "clearPreRestoredData() delete, name : " + next2 + ", deleted : " + file.delete());
                }
            }
            this.downloadFileList.clear();
        }
    }

    /* access modifiers changed from: private */
    public void addToList(int i, String str) {
        if (i == 0) {
            this.processedKeyList.add(str);
        } else if (i == 1) {
            this.downloadFileList.add(str);
        } else if (i == 2) {
            this.restoreFileList.add(str);
        }
    }

    public IServiceHandler getServiceHandler(String str) {
        return this.serviceHandlerMap.get(str);
    }

    public Object getClient(String str) {
        return this.backupClient;
    }
}
