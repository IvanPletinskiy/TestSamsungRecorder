package com.samsung.android.scloud.oem.lib.sync.record;

import android.content.Context;
import android.os.Bundle;
import com.samsung.android.scloud.oem.lib.LOG;
import com.samsung.android.scloud.oem.lib.common.IClientHelper;
import com.samsung.android.scloud.oem.lib.common.IServiceHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordSyncManager extends IClientHelper {
    private static final Map<String, IServiceHandler> SERVICE_HANDLER_MAP = new HashMap();
    private final IRecordSyncClient syncClient;

    static {
        SERVICE_HANDLER_MAP.put("isSyncable", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                LOG.m15i("RecordSyncManager", "IsSyncable : " + str);
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("is_syncable", true);
                return bundle2;
            }
        });
        SERVICE_HANDLER_MAP.put("isColdStartable", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                LOG.m15i("RecordSyncManager", "IS_COLD_STARTABLE : " + str);
                boolean isColdStartable = ((IRecordSyncClient) obj).isColdStartable();
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("is_cold_startable", isColdStartable);
                return bundle2;
            }
        });
        SERVICE_HANDLER_MAP.put("lastSyncTime", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                LOG.m15i("RecordSyncManager", "LAST_SYNC_TIME : " + str + ", extras : " + bundle);
                IRecordSyncClient iRecordSyncClient = (IRecordSyncClient) obj;
                if (bundle == null || !bundle.containsKey("last_sync_time")) {
                    long lastSyncTime = iRecordSyncClient.getLastSyncTime();
                    Bundle bundle2 = new Bundle();
                    bundle2.putLong("last_sync_time", lastSyncTime);
                    LOG.m15i("RecordSyncManager", "getLastSyncTime - name : " + str + ", val : " + lastSyncTime);
                    return bundle2;
                }
                long j = bundle.getLong("last_sync_time");
                iRecordSyncClient.setLastSyncTime(j);
                LOG.m15i("RecordSyncManager", "setLastSyncTime - name : " + str + ", val : " + j);
                return null;
            }
        });
        SERVICE_HANDLER_MAP.put("ready", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                List<RecordItem> localRecordList;
                LOG.m15i("RecordSyncManager", "READY : " + str);
                String[] stringArray = bundle.getStringArray("server_id");
                String string = bundle.getString("account_name");
                String string2 = bundle.getString("account_type");
                boolean z = bundle.getBoolean("is_cold_start");
                Bundle bundle2 = new Bundle();
                IRecordSyncClient iRecordSyncClient = (IRecordSyncClient) obj;
                boolean ready = iRecordSyncClient.ready();
                if (ready && (localRecordList = iRecordSyncClient.getLocalRecordList(stringArray, string2, string, z)) != null) {
                    int size = localRecordList.size();
                    long[] jArr = new long[size];
                    long[] jArr2 = new long[size];
                    boolean[] zArr = new boolean[size];
                    String[] strArr = new String[size];
                    String[] strArr2 = new String[size];
                    for (int i = 0; i < size; i++) {
                        RecordItem recordItem = localRecordList.get(i);
                        jArr[i] = recordItem.getLocalRecordId();
                        strArr[i] = recordItem.getServerRecordId();
                        jArr2[i] = recordItem.getTimestamp();
                        zArr[i] = recordItem.isDeleted();
                        strArr2[i] = recordItem.getTableName();
                    }
                    bundle2.putLongArray("local_id", jArr);
                    bundle2.putStringArray("server_id", strArr);
                    bundle2.putLongArray("timestamp", jArr2);
                    bundle2.putBooleanArray("deleted", zArr);
                    bundle2.putStringArray("table_name", strArr2);
                }
                bundle2.putBoolean("is_success", ready);
                return bundle2;
            }
        });
        SERVICE_HANDLER_MAP.put("getLocalFiles", new IServiceHandler() {
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v18, resolved type: android.os.Bundle} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v21, resolved type: java.lang.String} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v22, resolved type: com.samsung.android.scloud.oem.lib.sync.record.RecordItem} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v34, resolved type: java.lang.String} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v31, resolved type: long} */
            /* JADX WARNING: Multi-variable type inference failed */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public android.os.Bundle handleServiceAction(android.content.Context r31, java.lang.Object r32, java.lang.String r33, android.os.Bundle r34) {
                /*
                    r30 = this;
                    r0 = r33
                    r1 = r34
                    java.lang.String r2 = "--"
                    java.lang.String r3 = "\r\n"
                    java.lang.StringBuilder r4 = new java.lang.StringBuilder
                    r4.<init>()
                    java.lang.String r5 = "GET_LOCAL_FILES : "
                    r4.append(r5)
                    r4.append(r0)
                    java.lang.String r4 = r4.toString()
                    java.lang.String r5 = "RecordSyncManager"
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r5, r4)
                    java.lang.String r4 = "local_id"
                    long[] r4 = r1.getLongArray(r4)
                    java.lang.String r6 = "server_id"
                    java.lang.String[] r6 = r1.getStringArray(r6)
                    java.lang.String r7 = "deleted"
                    boolean[] r7 = r1.getBooleanArray(r7)
                    java.lang.String r8 = "table_name"
                    java.lang.String[] r8 = r1.getStringArray(r8)
                    java.lang.String r9 = "cid_table_index"
                    java.lang.String[] r1 = r1.getStringArray(r9)
                    android.os.Bundle r9 = new android.os.Bundle
                    r9.<init>()
                    java.lang.String r10 = "is_success"
                    r11 = 0
                    if (r1 != 0) goto L_0x004f
                    java.lang.String r0 = "index is null..."
                    com.samsung.android.scloud.oem.lib.LOG.m13e(r5, r0)
                    r9.putBoolean(r10, r11)
                    return r9
                L_0x004f:
                    java.util.HashMap r12 = new java.util.HashMap
                    r12.<init>()
                    int r13 = r1.length
                    java.lang.String[] r13 = new java.lang.String[r13]
                    int r14 = r1.length
                    java.lang.String[] r14 = new java.lang.String[r14]
                    int r15 = r1.length
                    java.lang.String[] r15 = new java.lang.String[r15]
                    java.util.HashMap r11 = new java.util.HashMap
                    r11.<init>()
                    r16 = r3
                    java.util.ArrayList r3 = new java.util.ArrayList
                    r3.<init>()
                    r17 = r2
                    r2 = r32
                    com.samsung.android.scloud.oem.lib.sync.record.IRecordSyncClient r2 = (com.samsung.android.scloud.oem.lib.sync.record.IRecordSyncClient) r2
                    if (r4 == 0) goto L_0x00a6
                    r18 = r11
                    r0 = 0
                L_0x0074:
                    int r11 = r4.length
                    if (r0 >= r11) goto L_0x00a8
                    r27 = r12
                    r11 = r4[r0]
                    boolean r25 = r7[r0]
                    r28 = r4
                    r4 = r8[r0]
                    if (r25 == 0) goto L_0x0089
                    r2.deleteRecord(r4, r11)
                    r29 = r7
                    goto L_0x009d
                L_0x0089:
                    r29 = r7
                    com.samsung.android.scloud.oem.lib.sync.record.RecordItem r7 = new com.samsung.android.scloud.oem.lib.sync.record.RecordItem
                    r22 = r6[r0]
                    r23 = 0
                    r19 = r7
                    r20 = r11
                    r26 = r4
                    r19.<init>(r20, r22, r23, r25, r26)
                    r3.add(r7)
                L_0x009d:
                    int r0 = r0 + 1
                    r12 = r27
                    r4 = r28
                    r7 = r29
                    goto L_0x0074
                L_0x00a6:
                    r18 = r11
                L_0x00a8:
                    r27 = r12
                    r0 = 0
                L_0x00ab:
                    int r4 = r1.length
                    r11 = 1
                    if (r0 >= r4) goto L_0x0123
                    r4 = r1[r0]
                    java.lang.String r6 = ":"
                    java.lang.String[] r4 = r4.split(r6)
                    int r6 = r4.length
                    r7 = 3
                    if (r6 >= r7) goto L_0x00d6
                    java.lang.StringBuilder r2 = new java.lang.StringBuilder
                    r2.<init>()
                    java.lang.String r3 = "index format is wrong : "
                    r2.append(r3)
                    r0 = r1[r0]
                    r2.append(r0)
                    java.lang.String r0 = r2.toString()
                    com.samsung.android.scloud.oem.lib.LOG.m13e(r5, r0)
                    r6 = 0
                    r9.putBoolean(r10, r6)
                    return r9
                L_0x00d6:
                    r6 = 0
                    r7 = r4[r6]
                    r13[r0] = r7
                    r7 = r4[r11]
                    r14[r0] = r7
                    r7 = 2
                    r4 = r4[r7]
                    r15[r0] = r4
                    r4 = r14[r0]
                    java.lang.Integer r7 = java.lang.Integer.valueOf(r0)
                    r8 = r27
                    r8.put(r4, r7)
                    java.util.ArrayList r4 = new java.util.ArrayList
                    r4.<init>()
                    r7 = r6
                L_0x00f5:
                    int r11 = r3.size()
                    if (r7 >= r11) goto L_0x0117
                    r11 = r14[r0]
                    java.lang.Object r12 = r3.get(r7)
                    com.samsung.android.scloud.oem.lib.sync.record.RecordItem r12 = (com.samsung.android.scloud.oem.lib.sync.record.RecordItem) r12
                    java.lang.String r12 = r12.getTableName()
                    boolean r11 = r11.equals(r12)
                    if (r11 == 0) goto L_0x0114
                    java.lang.Object r11 = r3.get(r7)
                    r4.add(r11)
                L_0x0114:
                    int r7 = r7 + 1
                    goto L_0x00f5
                L_0x0117:
                    r7 = r14[r0]
                    r12 = r18
                    r12.put(r7, r4)
                    int r0 = r0 + 1
                    r27 = r8
                    goto L_0x00ab
                L_0x0123:
                    r12 = r18
                    r8 = r27
                    r6 = 0
                    java.lang.StringBuilder r0 = new java.lang.StringBuilder
                    r0.<init>()
                    java.lang.String r1 = "sync_toUploadFile_"
                    r0.append(r1)
                    r1 = r33
                    r0.append(r1)
                    java.lang.String r3 = r0.toString()
                    java.lang.StringBuilder r0 = new java.lang.StringBuilder
                    r0.<init>()
                    java.lang.String r4 = "sync_toDownloadFile_"
                    r0.append(r4)
                    r0.append(r1)
                    java.lang.String r1 = r0.toString()
                    java.io.File r4 = new java.io.File
                    java.io.File r0 = r31.getFilesDir()
                    r4.<init>(r0, r3)
                    java.io.File r0 = new java.io.File
                    java.io.File r7 = r31.getFilesDir()
                    r0.<init>(r7, r1)
                    boolean r7 = r4.exists()     // Catch:{ IOException -> 0x0177 }
                    if (r7 == 0) goto L_0x0167
                    r4.delete()     // Catch:{ IOException -> 0x0177 }
                L_0x0167:
                    boolean r7 = r0.exists()     // Catch:{ IOException -> 0x0177 }
                    if (r7 == 0) goto L_0x0170
                    r0.delete()     // Catch:{ IOException -> 0x0177 }
                L_0x0170:
                    r4.createNewFile()     // Catch:{ IOException -> 0x0177 }
                    r0.createNewFile()     // Catch:{ IOException -> 0x0177 }
                    goto L_0x017b
                L_0x0177:
                    r0 = move-exception
                    r0.printStackTrace()
                L_0x017b:
                    int r0 = r12.size()
                    if (r0 <= 0) goto L_0x032f
                    java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    r0.<init>()     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    java.io.FileWriter r7 = new java.io.FileWriter     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    r7.<init>(r4)     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    java.util.Set r4 = r12.keySet()     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    java.util.Iterator r4 = r4.iterator()     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                L_0x0193:
                    boolean r14 = r4.hasNext()     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    if (r14 == 0) goto L_0x0301
                    java.lang.Object r14 = r4.next()     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    java.lang.String r14 = (java.lang.String) r14     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    r6.<init>()     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    java.lang.String r11 = "table : "
                    r6.append(r11)     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    r6.append(r14)     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    java.lang.String r6 = r6.toString()     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r5, r6)     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    java.lang.Object r6 = r12.get(r14)     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    java.util.List r6 = (java.util.List) r6     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    int r6 = r6.size()     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    r11.<init>()     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    r31 = r4
                    java.lang.String r4 = "total upload size... "
                    r11.append(r4)     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    r11.append(r6)     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    java.lang.String r4 = r11.toString()     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    com.samsung.android.scloud.oem.lib.LOG.m12d(r5, r4)     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    r4 = 0
                L_0x01d4:
                    if (r6 <= 0) goto L_0x02f9
                    org.json.JSONArray r11 = new org.json.JSONArray     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    r11.<init>()     // Catch:{ JSONException -> 0x0321, IOException -> 0x0314 }
                    r18 = r10
                    org.json.JSONObject r10 = new org.json.JSONObject     // Catch:{ JSONException -> 0x02f1, IOException -> 0x02e9 }
                    r10.<init>()     // Catch:{ JSONException -> 0x02f1, IOException -> 0x02e9 }
                    r33 = r1
                    r1 = 500(0x1f4, float:7.0E-43)
                    if (r6 <= r1) goto L_0x01e9
                    goto L_0x01ea
                L_0x01e9:
                    r1 = r6
                L_0x01ea:
                    r19 = r3
                    java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x02e7, IOException -> 0x02e5 }
                    r3.<init>()     // Catch:{ JSONException -> 0x02e7, IOException -> 0x02e5 }
                    r20 = r9
                    java.lang.String r9 = "now upload size... "
                    r3.append(r9)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    r3.append(r1)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    java.lang.String r3 = r3.toString()     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    com.samsung.android.scloud.oem.lib.LOG.m12d(r5, r3)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    int r3 = r4 * 500
                    r9 = r3
                    r21 = r6
                L_0x0207:
                    int r6 = r3 + r1
                    if (r9 >= r6) goto L_0x021b
                    java.lang.Object r6 = r12.get(r14)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    java.util.List r6 = (java.util.List) r6     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    java.lang.Object r6 = r6.get(r9)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    r0.add(r6)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    int r9 = r9 + 1
                    goto L_0x0207
                L_0x021b:
                    java.util.Iterator r3 = r0.iterator()     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                L_0x021f:
                    boolean r6 = r3.hasNext()     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    if (r6 == 0) goto L_0x024c
                    java.lang.Object r6 = r3.next()     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    com.samsung.android.scloud.oem.lib.sync.record.RecordItem r6 = (com.samsung.android.scloud.oem.lib.sync.record.RecordItem) r6     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    java.lang.String r9 = r6.getTableName()     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    r22 = r0
                    r23 = r1
                    long r0 = r6.getLocalRecordId()     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    org.json.JSONObject r0 = r2.getRecord(r9, r0)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    java.lang.String r1 = "record_id"
                    java.lang.String r6 = r6.getServerRecordId()     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    r0.put(r1, r6)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    r11.put(r0)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    r0 = r22
                    r1 = r23
                    goto L_0x021f
                L_0x024c:
                    r22 = r0
                    r23 = r1
                    java.lang.String r0 = "records"
                    r10.put(r0, r11)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    r0.<init>()     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    java.lang.String r1 = "payload : "
                    r0.append(r1)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    java.lang.String r1 = r10.toString()     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    r0.append(r1)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    java.lang.String r0 = r0.toString()     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    com.samsung.android.scloud.oem.lib.LOG.m12d(r5, r0)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    r1 = r17
                    java.io.Writer r0 = r7.append(r1)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    java.lang.String r3 = "1QAZXSW2"
                    java.io.Writer r0 = r0.append(r3)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    r3 = r16
                    r0.append(r3)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    java.lang.String r0 = "cid:"
                    java.io.Writer r0 = r7.append(r0)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    java.lang.Object r6 = r8.get(r14)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    java.lang.Integer r6 = (java.lang.Integer) r6     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    int r6 = r6.intValue()     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    r6 = r13[r6]     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    java.io.Writer r0 = r0.append(r6)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    r0.append(r3)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    java.lang.String r0 = "tableName:"
                    java.io.Writer r0 = r7.append(r0)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    java.io.Writer r0 = r0.append(r14)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    r0.append(r3)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    java.lang.String r0 = "tableVersion:"
                    java.io.Writer r0 = r7.append(r0)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    java.lang.Object r6 = r8.get(r14)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    java.lang.Integer r6 = (java.lang.Integer) r6     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    int r6 = r6.intValue()     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    r6 = r15[r6]     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    java.io.Writer r0 = r0.append(r6)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    r0.append(r3)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    java.lang.String r0 = r10.toString()     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    r7.write(r0)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    r7.append(r3)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    java.io.Writer r0 = r7.append(r1)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    r0.append(r3)     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    int r4 = r4 + 1
                    int r6 = r21 - r23
                    r22.clear()     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    r17 = r1
                    r16 = r3
                    r10 = r18
                    r3 = r19
                    r9 = r20
                    r0 = r22
                    r1 = r33
                    goto L_0x01d4
                L_0x02e5:
                    r0 = move-exception
                    goto L_0x02ee
                L_0x02e7:
                    r0 = move-exception
                    goto L_0x02f6
                L_0x02e9:
                    r0 = move-exception
                    r33 = r1
                    r19 = r3
                L_0x02ee:
                    r20 = r9
                    goto L_0x031d
                L_0x02f1:
                    r0 = move-exception
                    r33 = r1
                    r19 = r3
                L_0x02f6:
                    r20 = r9
                    goto L_0x032a
                L_0x02f9:
                    r19 = r3
                    r4 = r31
                    r6 = 0
                    r11 = 1
                    goto L_0x0193
                L_0x0301:
                    r33 = r1
                    r19 = r3
                    r20 = r9
                    r18 = r10
                    r7.flush()     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    r7.close()     // Catch:{ JSONException -> 0x0312, IOException -> 0x0310 }
                    goto L_0x0337
                L_0x0310:
                    r0 = move-exception
                    goto L_0x031d
                L_0x0312:
                    r0 = move-exception
                    goto L_0x032a
                L_0x0314:
                    r0 = move-exception
                    r33 = r1
                    r19 = r3
                    r20 = r9
                    r18 = r10
                L_0x031d:
                    r0.printStackTrace()
                    goto L_0x032d
                L_0x0321:
                    r0 = move-exception
                    r33 = r1
                    r19 = r3
                    r20 = r9
                    r18 = r10
                L_0x032a:
                    r0.printStackTrace()
                L_0x032d:
                    r0 = 0
                    goto L_0x0338
                L_0x032f:
                    r33 = r1
                    r19 = r3
                    r20 = r9
                    r18 = r10
                L_0x0337:
                    r0 = 1
                L_0x0338:
                    java.lang.String r1 = "upload_file_path"
                    r3 = r19
                    r2 = r20
                    r2.putString(r1, r3)
                    java.lang.String r1 = "download_file_path"
                    r3 = r33
                    r2.putString(r1, r3)
                    r1 = r18
                    r2.putBoolean(r1, r0)
                    return r2
                */
                throw new UnsupportedOperationException("Method not decompiled: com.samsung.android.scloud.oem.lib.sync.record.RecordSyncManager.C06615.handleServiceAction(android.content.Context, java.lang.Object, java.lang.String, android.os.Bundle):android.os.Bundle");
            }
        });
        SERVICE_HANDLER_MAP.put("fileWriteDone", new IServiceHandler() {
            /* JADX WARNING: Code restructure failed: missing block: B:103:0x019f, code lost:
                r0 = e;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:104:0x01a0, code lost:
                r10 = r20;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:105:0x01a3, code lost:
                r0 = e;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:106:0x01a4, code lost:
                r10 = r20;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:107:0x01a7, code lost:
                r0 = e;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:108:0x01a8, code lost:
                r10 = r20;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:115:0x01bd, code lost:
                r0 = move-exception;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:116:0x01be, code lost:
                r1 = r0;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:117:0x01c1, code lost:
                r0 = e;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:118:0x01c2, code lost:
                r20 = r10;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:119:0x01c4, code lost:
                r9 = r11;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:120:0x01c6, code lost:
                r0 = e;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:121:0x01c7, code lost:
                r20 = r10;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:122:0x01c9, code lost:
                r9 = r11;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:123:0x01cb, code lost:
                r0 = e;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:124:0x01cc, code lost:
                r20 = r10;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:125:0x01ce, code lost:
                r9 = r11;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:167:?, code lost:
                r11.close();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:168:0x0226, code lost:
                r0 = move-exception;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:169:0x0227, code lost:
                r0.printStackTrace();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:42:?, code lost:
                r6.putBoolean("is_success", false);
             */
            /* JADX WARNING: Code restructure failed: missing block: B:44:?, code lost:
                r11.close();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:45:0x00f4, code lost:
                r0 = move-exception;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:46:0x00f5, code lost:
                r0.printStackTrace();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:48:0x00fa, code lost:
                r0 = e;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:49:0x00fb, code lost:
                r10 = r20;
                r8 = false;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:50:0x0100, code lost:
                r0 = e;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:51:0x0101, code lost:
                r10 = r20;
                r8 = false;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:52:0x0106, code lost:
                r0 = e;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:53:0x0107, code lost:
                r10 = r20;
                r8 = false;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:55:0x0114, code lost:
                r0 = e;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:56:0x0115, code lost:
                r10 = r20;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:57:0x0118, code lost:
                r0 = e;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:58:0x0119, code lost:
                r10 = r20;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:59:0x011c, code lost:
                r0 = e;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:60:0x011d, code lost:
                r10 = r20;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:70:0x0135, code lost:
                r20 = r10;
                r8 = false;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:72:?, code lost:
                r6.putBoolean("is_success", false);
             */
            /* JADX WARNING: Code restructure failed: missing block: B:74:?, code lost:
                r11.close();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:75:0x013f, code lost:
                r0 = move-exception;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:76:0x0140, code lost:
                r0.printStackTrace();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:78:0x0145, code lost:
                r0 = e;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:79:0x0146, code lost:
                r20 = r10;
                r8 = false;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:80:0x014b, code lost:
                r0 = e;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:81:0x014c, code lost:
                r20 = r10;
                r8 = false;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:82:0x0151, code lost:
                r0 = e;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:83:0x0152, code lost:
                r20 = r10;
                r8 = false;
             */
            /* JADX WARNING: Failed to process nested try/catch */
            /* JADX WARNING: Removed duplicated region for block: B:115:0x01bd A[ExcHandler: all (r0v22 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:14:0x006f] */
            /* JADX WARNING: Removed duplicated region for block: B:139:0x01e7 A[SYNTHETIC, Splitter:B:139:0x01e7] */
            /* JADX WARNING: Removed duplicated region for block: B:146:0x01f2 A[SYNTHETIC, Splitter:B:146:0x01f2] */
            /* JADX WARNING: Removed duplicated region for block: B:153:0x01fd A[SYNTHETIC, Splitter:B:153:0x01fd] */
            /* JADX WARNING: Removed duplicated region for block: B:158:0x0208  */
            /* JADX WARNING: Removed duplicated region for block: B:166:0x0222 A[SYNTHETIC, Splitter:B:166:0x0222] */
            /* JADX WARNING: Unknown top exception splitter block from list: {B:136:0x01e2=Splitter:B:136:0x01e2, B:150:0x01f8=Splitter:B:150:0x01f8, B:143:0x01ed=Splitter:B:143:0x01ed} */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public android.os.Bundle handleServiceAction(android.content.Context r19, java.lang.Object r20, java.lang.String r21, android.os.Bundle r22) {
                /*
                    r18 = this;
                    r0 = r22
                    java.lang.StringBuilder r1 = new java.lang.StringBuilder
                    r1.<init>()
                    java.lang.String r2 = "FILE_WRITE_DONE : "
                    r1.append(r2)
                    r2 = r21
                    r1.append(r2)
                    java.lang.String r1 = r1.toString()
                    java.lang.String r2 = "RecordSyncManager"
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r2, r1)
                    java.lang.String r1 = "local_id"
                    long[] r1 = r0.getLongArray(r1)
                    java.lang.String r3 = "server_id"
                    java.lang.String[] r3 = r0.getStringArray(r3)
                    java.lang.String r4 = "table_name"
                    java.lang.String[] r4 = r0.getStringArray(r4)
                    java.lang.String r5 = "download_file_path"
                    java.lang.String r5 = r0.getString(r5)
                    android.os.Bundle r6 = new android.os.Bundle
                    r6.<init>()
                    java.util.HashMap r0 = new java.util.HashMap
                    r0.<init>()
                    java.lang.String r7 = "is_success"
                    r8 = 0
                    if (r5 != 0) goto L_0x0045
                    r6.putBoolean(r7, r8)
                    return r6
                L_0x0045:
                    if (r3 == 0) goto L_0x0057
                    r9 = r8
                L_0x0048:
                    int r10 = r3.length
                    if (r9 >= r10) goto L_0x0057
                    r10 = r3[r9]
                    java.lang.Integer r11 = java.lang.Integer.valueOf(r9)
                    r0.put(r10, r11)
                    int r9 = r9 + 1
                    goto L_0x0048
                L_0x0057:
                    r3 = r20
                    com.samsung.android.scloud.oem.lib.sync.record.IRecordSyncClient r3 = (com.samsung.android.scloud.oem.lib.sync.record.IRecordSyncClient) r3
                    r9 = 0
                    java.io.File r10 = new java.io.File     // Catch:{ FileNotFoundException -> 0x01f6, IOException -> 0x01eb, JSONException -> 0x01e0 }
                    java.io.File r11 = r19.getFilesDir()     // Catch:{ FileNotFoundException -> 0x01f6, IOException -> 0x01eb, JSONException -> 0x01e0 }
                    r10.<init>(r11, r5)     // Catch:{ FileNotFoundException -> 0x01f6, IOException -> 0x01eb, JSONException -> 0x01e0 }
                    java.io.BufferedReader r11 = new java.io.BufferedReader     // Catch:{ FileNotFoundException -> 0x01d8, IOException -> 0x01d4, JSONException -> 0x01d0 }
                    java.io.FileReader r12 = new java.io.FileReader     // Catch:{ FileNotFoundException -> 0x01d8, IOException -> 0x01d4, JSONException -> 0x01d0 }
                    r12.<init>(r10)     // Catch:{ FileNotFoundException -> 0x01d8, IOException -> 0x01d4, JSONException -> 0x01d0 }
                    r11.<init>(r12)     // Catch:{ FileNotFoundException -> 0x01d8, IOException -> 0x01d4, JSONException -> 0x01d0 }
                    java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x01cb, IOException -> 0x01c6, JSONException -> 0x01c1, all -> 0x01bd }
                    r9.<init>()     // Catch:{ FileNotFoundException -> 0x01cb, IOException -> 0x01c6, JSONException -> 0x01c1, all -> 0x01bd }
                    r12 = 1
                L_0x0075:
                    java.lang.String r13 = r11.readLine()     // Catch:{ FileNotFoundException -> 0x01cb, IOException -> 0x01c6, JSONException -> 0x01c1, all -> 0x01bd }
                    if (r13 == 0) goto L_0x01ab
                    java.lang.String r14 = "--"
                    boolean r14 = r13.equals(r14)     // Catch:{ FileNotFoundException -> 0x01cb, IOException -> 0x01c6, JSONException -> 0x01c1, all -> 0x01bd }
                    if (r14 == 0) goto L_0x015a
                    org.json.JSONObject r13 = new org.json.JSONObject     // Catch:{ FileNotFoundException -> 0x01cb, IOException -> 0x01c6, JSONException -> 0x01c1, all -> 0x01bd }
                    java.lang.String r14 = r9.toString()     // Catch:{ FileNotFoundException -> 0x01cb, IOException -> 0x01c6, JSONException -> 0x01c1, all -> 0x01bd }
                    r13.<init>(r14)     // Catch:{ FileNotFoundException -> 0x01cb, IOException -> 0x01c6, JSONException -> 0x01c1, all -> 0x01bd }
                    java.lang.String r14 = "records"
                    org.json.JSONArray r13 = r13.getJSONArray(r14)     // Catch:{ FileNotFoundException -> 0x01cb, IOException -> 0x01c6, JSONException -> 0x01c1, all -> 0x01bd }
                    r14 = r12
                    r12 = r8
                L_0x0094:
                    int r15 = r13.length()     // Catch:{ FileNotFoundException -> 0x01cb, IOException -> 0x01c6, JSONException -> 0x01c1, all -> 0x01bd }
                    if (r12 >= r15) goto L_0x0157
                    org.json.JSONObject r14 = r13.getJSONObject(r12)     // Catch:{ FileNotFoundException -> 0x01cb, IOException -> 0x01c6, JSONException -> 0x01c1, all -> 0x01bd }
                    java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x01cb, IOException -> 0x01c6, JSONException -> 0x01c1, all -> 0x01bd }
                    r15.<init>()     // Catch:{ FileNotFoundException -> 0x01cb, IOException -> 0x01c6, JSONException -> 0x01c1, all -> 0x01bd }
                    java.lang.String r8 = "object : "
                    r15.append(r8)     // Catch:{ FileNotFoundException -> 0x0151, IOException -> 0x014b, JSONException -> 0x0145, all -> 0x01bd }
                    java.lang.String r8 = r14.toString()     // Catch:{ FileNotFoundException -> 0x0151, IOException -> 0x014b, JSONException -> 0x0145, all -> 0x01bd }
                    r15.append(r8)     // Catch:{ FileNotFoundException -> 0x0151, IOException -> 0x014b, JSONException -> 0x0145, all -> 0x01bd }
                    java.lang.String r8 = r15.toString()     // Catch:{ FileNotFoundException -> 0x0151, IOException -> 0x014b, JSONException -> 0x0145, all -> 0x01bd }
                    com.samsung.android.scloud.oem.lib.LOG.m12d(r2, r8)     // Catch:{ FileNotFoundException -> 0x0151, IOException -> 0x014b, JSONException -> 0x0145, all -> 0x01bd }
                    java.lang.String r8 = "record_id"
                    java.lang.String r8 = r14.optString(r8)     // Catch:{ FileNotFoundException -> 0x0151, IOException -> 0x014b, JSONException -> 0x0145, all -> 0x01bd }
                    boolean r15 = android.text.TextUtils.isEmpty(r8)     // Catch:{ FileNotFoundException -> 0x0151, IOException -> 0x014b, JSONException -> 0x0145, all -> 0x01bd }
                    if (r15 != 0) goto L_0x0135
                    java.lang.Object r15 = r0.get(r8)     // Catch:{ FileNotFoundException -> 0x012e, IOException -> 0x0127, JSONException -> 0x0120, all -> 0x01bd }
                    java.lang.Integer r15 = (java.lang.Integer) r15     // Catch:{ FileNotFoundException -> 0x012e, IOException -> 0x0127, JSONException -> 0x0120, all -> 0x01bd }
                    int r15 = r15.intValue()     // Catch:{ FileNotFoundException -> 0x012e, IOException -> 0x0127, JSONException -> 0x0120, all -> 0x01bd }
                    r22 = r9
                    r20 = r10
                    r9 = r1[r15]     // Catch:{ FileNotFoundException -> 0x011c, IOException -> 0x0118, JSONException -> 0x0114, all -> 0x01bd }
                    r15 = r4[r15]     // Catch:{ FileNotFoundException -> 0x011c, IOException -> 0x0118, JSONException -> 0x0114, all -> 0x01bd }
                    boolean r8 = r0.containsKey(r8)     // Catch:{ FileNotFoundException -> 0x011c, IOException -> 0x0118, JSONException -> 0x0114, all -> 0x01bd }
                    if (r8 == 0) goto L_0x00e5
                    r16 = 0
                    int r8 = (r9 > r16 ? 1 : (r9 == r16 ? 0 : -1))
                    if (r8 <= 0) goto L_0x00e5
                    boolean r8 = r3.updateRecord(r15, r14, r9)     // Catch:{ FileNotFoundException -> 0x011c, IOException -> 0x0118, JSONException -> 0x0114, all -> 0x01bd }
                    goto L_0x00e9
                L_0x00e5:
                    boolean r8 = r3.createRecord(r15, r14)     // Catch:{ FileNotFoundException -> 0x011c, IOException -> 0x0118, JSONException -> 0x0114, all -> 0x01bd }
                L_0x00e9:
                    r14 = r8
                    if (r14 != 0) goto L_0x010c
                    r1 = 0
                    r6.putBoolean(r7, r1)     // Catch:{ FileNotFoundException -> 0x0106, IOException -> 0x0100, JSONException -> 0x00fa, all -> 0x01bd }
                    r11.close()     // Catch:{ IOException -> 0x00f4 }
                    goto L_0x00f9
                L_0x00f4:
                    r0 = move-exception
                    r1 = r0
                    r1.printStackTrace()
                L_0x00f9:
                    return r6
                L_0x00fa:
                    r0 = move-exception
                    r10 = r20
                    r8 = r1
                    goto L_0x01c4
                L_0x0100:
                    r0 = move-exception
                    r10 = r20
                    r8 = r1
                    goto L_0x01c9
                L_0x0106:
                    r0 = move-exception
                    r10 = r20
                    r8 = r1
                    goto L_0x01ce
                L_0x010c:
                    int r12 = r12 + 1
                    r10 = r20
                    r9 = r22
                    r8 = 0
                    goto L_0x0094
                L_0x0114:
                    r0 = move-exception
                    r10 = r20
                    goto L_0x0123
                L_0x0118:
                    r0 = move-exception
                    r10 = r20
                    goto L_0x012a
                L_0x011c:
                    r0 = move-exception
                    r10 = r20
                    goto L_0x0131
                L_0x0120:
                    r0 = move-exception
                    r20 = r10
                L_0x0123:
                    r9 = r11
                    r8 = 0
                    goto L_0x01e2
                L_0x0127:
                    r0 = move-exception
                    r20 = r10
                L_0x012a:
                    r9 = r11
                    r8 = 0
                    goto L_0x01ed
                L_0x012e:
                    r0 = move-exception
                    r20 = r10
                L_0x0131:
                    r9 = r11
                    r8 = 0
                    goto L_0x01f8
                L_0x0135:
                    r20 = r10
                    r8 = 0
                    r6.putBoolean(r7, r8)     // Catch:{ FileNotFoundException -> 0x01a7, IOException -> 0x01a3, JSONException -> 0x019f, all -> 0x01bd }
                    r11.close()     // Catch:{ IOException -> 0x013f }
                    goto L_0x0144
                L_0x013f:
                    r0 = move-exception
                    r1 = r0
                    r1.printStackTrace()
                L_0x0144:
                    return r6
                L_0x0145:
                    r0 = move-exception
                    r20 = r10
                    r8 = 0
                    goto L_0x01c4
                L_0x014b:
                    r0 = move-exception
                    r20 = r10
                    r8 = 0
                    goto L_0x01c9
                L_0x0151:
                    r0 = move-exception
                    r20 = r10
                    r8 = 0
                    goto L_0x01ce
                L_0x0157:
                    r12 = r14
                    goto L_0x0075
                L_0x015a:
                    r22 = r9
                    r20 = r10
                    java.lang.String r9 = "cid:"
                    boolean r9 = r13.contains(r9)     // Catch:{ FileNotFoundException -> 0x01a7, IOException -> 0x01a3, JSONException -> 0x019f, all -> 0x01bd }
                    if (r9 != 0) goto L_0x0196
                    java.lang.String r9 = "tableName:"
                    boolean r9 = r13.contains(r9)     // Catch:{ FileNotFoundException -> 0x01a7, IOException -> 0x01a3, JSONException -> 0x019f, all -> 0x01bd }
                    if (r9 != 0) goto L_0x0196
                    java.lang.String r9 = "tableVersion:"
                    boolean r9 = r13.contains(r9)     // Catch:{ FileNotFoundException -> 0x01a7, IOException -> 0x01a3, JSONException -> 0x019f, all -> 0x01bd }
                    if (r9 == 0) goto L_0x0177
                    goto L_0x0196
                L_0x0177:
                    java.lang.String r9 = "--1QAZXSW2"
                    boolean r9 = r13.equals(r9)     // Catch:{ FileNotFoundException -> 0x01a7, IOException -> 0x01a3, JSONException -> 0x019f, all -> 0x01bd }
                    if (r9 != 0) goto L_0x018e
                    java.lang.String r9 = "\r\n"
                    boolean r9 = r13.equals(r9)     // Catch:{ FileNotFoundException -> 0x01a7, IOException -> 0x01a3, JSONException -> 0x019f, all -> 0x01bd }
                    if (r9 == 0) goto L_0x0188
                    goto L_0x018e
                L_0x0188:
                    r9 = r22
                    r9.append(r13)     // Catch:{ FileNotFoundException -> 0x01a7, IOException -> 0x01a3, JSONException -> 0x019f, all -> 0x01bd }
                    goto L_0x019b
                L_0x018e:
                    r9 = r22
                    java.lang.String r10 = "Start Boundary : 1QAZXSW2"
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r2, r10)     // Catch:{ FileNotFoundException -> 0x01a7, IOException -> 0x01a3, JSONException -> 0x019f, all -> 0x01bd }
                    goto L_0x019b
                L_0x0196:
                    r9 = r22
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r2, r13)     // Catch:{ FileNotFoundException -> 0x01a7, IOException -> 0x01a3, JSONException -> 0x019f, all -> 0x01bd }
                L_0x019b:
                    r10 = r20
                    goto L_0x0075
                L_0x019f:
                    r0 = move-exception
                    r10 = r20
                    goto L_0x01c4
                L_0x01a3:
                    r0 = move-exception
                    r10 = r20
                    goto L_0x01c9
                L_0x01a7:
                    r0 = move-exception
                    r10 = r20
                    goto L_0x01ce
                L_0x01ab:
                    r20 = r10
                    r11.close()     // Catch:{ IOException -> 0x01b5 }
                    r10 = r20
                    r8 = r12
                    goto L_0x0206
                L_0x01b5:
                    r0 = move-exception
                    r1 = r0
                    r1.printStackTrace()
                    r10 = r20
                    goto L_0x0206
                L_0x01bd:
                    r0 = move-exception
                    r1 = r0
                    goto L_0x0220
                L_0x01c1:
                    r0 = move-exception
                    r20 = r10
                L_0x01c4:
                    r9 = r11
                    goto L_0x01e2
                L_0x01c6:
                    r0 = move-exception
                    r20 = r10
                L_0x01c9:
                    r9 = r11
                    goto L_0x01ed
                L_0x01cb:
                    r0 = move-exception
                    r20 = r10
                L_0x01ce:
                    r9 = r11
                    goto L_0x01f8
                L_0x01d0:
                    r0 = move-exception
                    r20 = r10
                    goto L_0x01e2
                L_0x01d4:
                    r0 = move-exception
                    r20 = r10
                    goto L_0x01ed
                L_0x01d8:
                    r0 = move-exception
                    r20 = r10
                    goto L_0x01f8
                L_0x01dc:
                    r0 = move-exception
                    r1 = r0
                    r11 = r9
                    goto L_0x0220
                L_0x01e0:
                    r0 = move-exception
                    r10 = r9
                L_0x01e2:
                    r0.printStackTrace()     // Catch:{ all -> 0x01dc }
                    if (r9 == 0) goto L_0x0206
                    r9.close()     // Catch:{ IOException -> 0x0201 }
                    goto L_0x0206
                L_0x01eb:
                    r0 = move-exception
                    r10 = r9
                L_0x01ed:
                    r0.printStackTrace()     // Catch:{ all -> 0x01dc }
                    if (r9 == 0) goto L_0x0206
                    r9.close()     // Catch:{ IOException -> 0x0201 }
                    goto L_0x0206
                L_0x01f6:
                    r0 = move-exception
                    r10 = r9
                L_0x01f8:
                    r0.printStackTrace()     // Catch:{ all -> 0x01dc }
                    if (r9 == 0) goto L_0x0206
                    r9.close()     // Catch:{ IOException -> 0x0201 }
                    goto L_0x0206
                L_0x0201:
                    r0 = move-exception
                    r1 = r0
                    r1.printStackTrace()
                L_0x0206:
                    if (r10 == 0) goto L_0x020b
                    r10.delete()
                L_0x020b:
                    java.io.File r0 = new java.io.File     // Catch:{ IOException -> 0x0218 }
                    java.io.File r1 = r19.getFilesDir()     // Catch:{ IOException -> 0x0218 }
                    r0.<init>(r1, r5)     // Catch:{ IOException -> 0x0218 }
                    r0.createNewFile()     // Catch:{ IOException -> 0x0218 }
                    goto L_0x021c
                L_0x0218:
                    r0 = move-exception
                    r0.printStackTrace()
                L_0x021c:
                    r6.putBoolean(r7, r8)
                    return r6
                L_0x0220:
                    if (r11 == 0) goto L_0x022b
                    r11.close()     // Catch:{ IOException -> 0x0226 }
                    goto L_0x022b
                L_0x0226:
                    r0 = move-exception
                    r2 = r0
                    r2.printStackTrace()
                L_0x022b:
                    throw r1
                */
                throw new UnsupportedOperationException("Method not decompiled: com.samsung.android.scloud.oem.lib.sync.record.RecordSyncManager.C06626.handleServiceAction(android.content.Context, java.lang.Object, java.lang.String, android.os.Bundle):android.os.Bundle");
            }
        });
        SERVICE_HANDLER_MAP.put("complete", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                boolean z;
                LOG.m15i("RecordSyncManager", "COMPLETE : " + str);
                long[] longArray = bundle.getLongArray("local_id");
                String[] stringArray = bundle.getStringArray("table_name");
                int i = bundle.getInt("rcode");
                if (i != 301) {
                    LOG.m12d("RecordSyncManager", "upload not success, rCode : " + i);
                    z = false;
                } else {
                    z = true;
                }
                IRecordSyncClient iRecordSyncClient = (IRecordSyncClient) obj;
                if (longArray == null) {
                    return null;
                }
                for (int i2 = 0; i2 < longArray.length; i2++) {
                    iRecordSyncClient.complete(stringArray[i2], longArray[i2], z);
                }
                return null;
            }
        });
        SERVICE_HANDLER_MAP.put("getLocalInfo", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                LOG.m15i("RecordSyncManager", "GET_LOCAL_INFO : " + str);
                Bundle bundle2 = new Bundle();
                String modifiedTimeName = ((IRecordSyncClient) obj).getModifiedTimeName();
                if (modifiedTimeName == null) {
                    bundle2.putBoolean("is_success", false);
                    return bundle2;
                }
                LOG.m12d("RecordSyncManager", "modifiedTimeName : " + modifiedTimeName);
                bundle2.putString("modified_time_name", modifiedTimeName);
                bundle2.putBoolean("is_success", true);
                return bundle2;
            }
        });
    }

    public RecordSyncManager(IRecordSyncClient iRecordSyncClient) {
        this.syncClient = iRecordSyncClient;
    }

    public IServiceHandler getServiceHandler(String str) {
        return SERVICE_HANDLER_MAP.get(str);
    }

    public Object getClient(String str) {
        return this.syncClient;
    }
}
