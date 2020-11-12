package com.samsung.android.scloud.oem.lib.sync.file;

import android.content.Context;
import android.os.Bundle;
import com.samsung.android.scloud.oem.lib.LOG;
import com.samsung.android.scloud.oem.lib.common.IClientHelper;
import com.samsung.android.scloud.oem.lib.common.IServiceHandler;
import java.util.HashMap;
import java.util.Map;

public class FileSyncManager extends IClientHelper {
    private static final Map<String, IServiceHandler> SERVICE_HANDLER_MAP = new HashMap();
    private final IFileSyncClient syncClient;

    static {
        SERVICE_HANDLER_MAP.put("isColdStartable", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                LOG.m15i("FileSyncManager", "IS_COLD_STARTABLE : " + str);
                boolean isColdStartable = ((IFileSyncClient) obj).isColdStartable(context);
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("is_cold_startable", isColdStartable);
                return bundle2;
            }
        });
        SERVICE_HANDLER_MAP.put("prepare", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                LOG.m15i("FileSyncManager", "PREPARE To Sync : " + str);
                String[] stringArray = bundle.getStringArray("sync_key");
                long[] longArray = bundle.getLongArray("timestamp");
                String[] stringArray2 = bundle.getStringArray("tag");
                String string = bundle.getString("account_name");
                String string2 = bundle.getString("account_type");
                boolean z = bundle.getBoolean("is_cold_start", false);
                IFileSyncClient iFileSyncClient = (IFileSyncClient) obj;
                iFileSyncClient.prepareToSync(context);
                SyncItem localItems = iFileSyncClient.getLocalItems(context, stringArray, longArray, stringArray2, string2, string, z);
                new Bundle();
                localItems.getSyncItemCount();
                throw null;
            }
        });
        SERVICE_HANDLER_MAP.put("getAttachmentInfo", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                int i = bundle.getInt("data_version");
                LOG.m15i("FileSyncManager", "GET_ATTACHMENT_INFO : " + str + ", v : " + i);
                FileInfo attachmentFileInfo = ((IFileSyncClient) obj).getAttachmentFileInfo(context, i, bundle.getString("local_id"));
                new Bundle();
                attachmentFileInfo.getFileInfoCount();
                throw null;
            }
        });
        SERVICE_HANDLER_MAP.put("upload", new IServiceHandler() {
            /* JADX WARNING: Removed duplicated region for block: B:25:0x008a A[SYNTHETIC, Splitter:B:25:0x008a] */
            /* JADX WARNING: Removed duplicated region for block: B:31:0x0095 A[SYNTHETIC, Splitter:B:31:0x0095] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public android.os.Bundle handleServiceAction(android.content.Context r9, java.lang.Object r10, java.lang.String r11, android.os.Bundle r12) {
                /*
                    r8 = this;
                    java.lang.String r0 = "data_version"
                    int r3 = r12.getInt(r0)
                    java.lang.StringBuilder r0 = new java.lang.StringBuilder
                    r0.<init>()
                    java.lang.String r1 = "UPLOAD : "
                    r0.append(r1)
                    r0.append(r11)
                    java.lang.String r11 = ", v : "
                    r0.append(r11)
                    r0.append(r3)
                    java.lang.String r11 = r0.toString()
                    java.lang.String r0 = "FileSyncManager"
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r0, r11)
                    java.lang.String r11 = "local_id"
                    java.lang.String r4 = r12.getString(r11)
                    java.lang.String r11 = "upload_file_list"
                    java.lang.String[] r5 = r12.getStringArray(r11)
                    java.util.HashMap r7 = new java.util.HashMap
                    r7.<init>()
                    r1 = r10
                    com.samsung.android.scloud.oem.lib.sync.file.IFileSyncClient r1 = (com.samsung.android.scloud.oem.lib.sync.file.IFileSyncClient) r1
                    r2 = r9
                    r6 = r7
                    java.lang.String r9 = r1.getLocalChange(r2, r3, r4, r5, r6)
                    android.os.Bundle r10 = new android.os.Bundle
                    r10.<init>()
                    java.lang.String r1 = "is_success"
                    r2 = 0
                    if (r9 == 0) goto L_0x006d
                    java.lang.String r3 = "content_sync_file"
                    android.os.Parcelable r12 = r12.getParcelable(r3)     // Catch:{ Exception -> 0x006b }
                    android.os.ParcelFileDescriptor r12 = (android.os.ParcelFileDescriptor) r12     // Catch:{ Exception -> 0x006b }
                    java.io.FileWriter r3 = new java.io.FileWriter     // Catch:{ Exception -> 0x006b }
                    java.io.FileDescriptor r12 = r12.getFileDescriptor()     // Catch:{ Exception -> 0x006b }
                    r3.<init>(r12)     // Catch:{ Exception -> 0x006b }
                    r3.write(r9)     // Catch:{ Exception -> 0x0066, all -> 0x0063 }
                    java.lang.String r9 = "write content Str : content.sync"
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r0, r9)     // Catch:{ Exception -> 0x0066, all -> 0x0063 }
                    r2 = r3
                    goto L_0x0072
                L_0x0063:
                    r9 = move-exception
                    r2 = r3
                    goto L_0x0093
                L_0x0066:
                    r9 = move-exception
                    r2 = r3
                    goto L_0x007f
                L_0x0069:
                    r9 = move-exception
                    goto L_0x0093
                L_0x006b:
                    r9 = move-exception
                    goto L_0x007f
                L_0x006d:
                    java.lang.String r9 = "content is null : content.sync"
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r0, r9)     // Catch:{ Exception -> 0x006b }
                L_0x0072:
                    r9 = 1
                    r10.putBoolean(r1, r9)     // Catch:{ Exception -> 0x006b }
                    r10.putSerializable(r11, r7)     // Catch:{ Exception -> 0x006b }
                    if (r2 == 0) goto L_0x0092
                    r2.close()     // Catch:{ Exception -> 0x008e }
                    goto L_0x0092
                L_0x007f:
                    java.lang.String r11 = "getLocalChange err "
                    com.samsung.android.scloud.oem.lib.LOG.m14e(r0, r11, r9)     // Catch:{ all -> 0x0069 }
                    r9 = 0
                    r10.putBoolean(r1, r9)     // Catch:{ all -> 0x0069 }
                    if (r2 == 0) goto L_0x0092
                    r2.close()     // Catch:{ Exception -> 0x008e }
                    goto L_0x0092
                L_0x008e:
                    r9 = move-exception
                    r9.printStackTrace()
                L_0x0092:
                    return r10
                L_0x0093:
                    if (r2 == 0) goto L_0x009d
                    r2.close()     // Catch:{ Exception -> 0x0099 }
                    goto L_0x009d
                L_0x0099:
                    r10 = move-exception
                    r10.printStackTrace()
                L_0x009d:
                    throw r9
                */
                throw new UnsupportedOperationException("Method not decompiled: com.samsung.android.scloud.oem.lib.sync.file.FileSyncManager.C06534.handleServiceAction(android.content.Context, java.lang.Object, java.lang.String, android.os.Bundle):android.os.Bundle");
            }
        });
        SERVICE_HANDLER_MAP.put("download", new IServiceHandler() {
            /* JADX WARNING: Removed duplicated region for block: B:36:0x00a9  */
            /* JADX WARNING: Removed duplicated region for block: B:39:0x00be A[Catch:{ UnsupportedOperationException -> 0x00d2 }] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public android.os.Bundle handleServiceAction(android.content.Context r12, java.lang.Object r13, java.lang.String r14, android.os.Bundle r15) {
                /*
                    r11 = this;
                    java.lang.String r0 = "data_version"
                    int r3 = r15.getInt(r0)
                    java.lang.StringBuilder r0 = new java.lang.StringBuilder
                    r0.<init>()
                    java.lang.String r1 = "DOWNLOAD : "
                    r0.append(r1)
                    r0.append(r14)
                    java.lang.String r14 = ", v : "
                    r0.append(r14)
                    r0.append(r3)
                    java.lang.String r14 = r0.toString()
                    java.lang.String r0 = "FileSyncManager"
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r0, r14)
                    java.lang.String r14 = "local_id"
                    java.lang.String r4 = r15.getString(r14)
                    java.lang.String r1 = "sync_key"
                    java.lang.String r5 = r15.getString(r1)
                    java.lang.String r1 = "download_file_list"
                    boolean r2 = r15.containsKey(r1)
                    if (r2 == 0) goto L_0x003f
                    java.io.Serializable r1 = r15.getSerializable(r1)
                    java.util.HashMap r1 = (java.util.HashMap) r1
                    goto L_0x0040
                L_0x003f:
                    r1 = 0
                L_0x0040:
                    r9 = r1
                    java.lang.String r1 = "deleted_file_list"
                    java.lang.String[] r10 = r15.getStringArray(r1)
                    java.lang.String r1 = "content_sync_file"
                    android.os.Parcelable r1 = r15.getParcelable(r1)
                    android.os.ParcelFileDescriptor r1 = (android.os.ParcelFileDescriptor) r1
                    r6 = 0
                    java.lang.String r2 = "timestamp"
                    long r6 = r15.getLong(r2, r6)
                    java.lang.Long r15 = java.lang.Long.valueOf(r6)
                    java.lang.StringBuilder r2 = new java.lang.StringBuilder
                    r2.<init>()
                    java.io.BufferedReader r6 = new java.io.BufferedReader     // Catch:{ Exception -> 0x009c }
                    java.io.FileReader r7 = new java.io.FileReader     // Catch:{ Exception -> 0x009c }
                    java.io.FileDescriptor r1 = r1.getFileDescriptor()     // Catch:{ Exception -> 0x009c }
                    r7.<init>(r1)     // Catch:{ Exception -> 0x009c }
                    r6.<init>(r7)     // Catch:{ Exception -> 0x009c }
                L_0x006e:
                    java.lang.String r1 = r6.readLine()     // Catch:{ IOException -> 0x0083 }
                    if (r1 == 0) goto L_0x0078
                    r2.append(r1)     // Catch:{ IOException -> 0x0083 }
                    goto L_0x006e
                L_0x0078:
                    r6.close()     // Catch:{ IOException -> 0x007c }
                    goto L_0x008d
                L_0x007c:
                    r1 = move-exception
                L_0x007d:
                    r1.printStackTrace()     // Catch:{ Exception -> 0x009c }
                    goto L_0x008d
                L_0x0081:
                    r1 = move-exception
                    goto L_0x0093
                L_0x0083:
                    r1 = move-exception
                    r1.printStackTrace()     // Catch:{ all -> 0x0081 }
                    r6.close()     // Catch:{ IOException -> 0x008b }
                    goto L_0x008d
                L_0x008b:
                    r1 = move-exception
                    goto L_0x007d
                L_0x008d:
                    java.lang.String r1 = "read content file complete : content.sync"
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r0, r1)     // Catch:{ Exception -> 0x009c }
                    goto L_0x00a2
                L_0x0093:
                    r6.close()     // Catch:{ IOException -> 0x0097 }
                    goto L_0x009b
                L_0x0097:
                    r6 = move-exception
                    r6.printStackTrace()     // Catch:{ Exception -> 0x009c }
                L_0x009b:
                    throw r1     // Catch:{ Exception -> 0x009c }
                L_0x009c:
                    r1 = move-exception
                    java.lang.String r6 = "read content file err. FILE : content.sync"
                    com.samsung.android.scloud.oem.lib.LOG.m14e(r0, r6, r1)
                L_0x00a2:
                    android.os.Bundle r0 = new android.os.Bundle
                    r0.<init>()
                    if (r4 != 0) goto L_0x00be
                    r1 = r13
                    com.samsung.android.scloud.oem.lib.sync.file.IFileSyncClient r1 = (com.samsung.android.scloud.oem.lib.sync.file.IFileSyncClient) r1     // Catch:{ UnsupportedOperationException -> 0x00d2 }
                    long r6 = r15.longValue()     // Catch:{ UnsupportedOperationException -> 0x00d2 }
                    java.lang.String r13 = r2.toString()     // Catch:{ UnsupportedOperationException -> 0x00d2 }
                    r2 = r12
                    r4 = r5
                    r5 = r6
                    r7 = r13
                    r8 = r9
                    java.lang.String r12 = r1.createLocal(r2, r3, r4, r5, r7, r8)     // Catch:{ UnsupportedOperationException -> 0x00d2 }
                    goto L_0x00ce
                L_0x00be:
                    r1 = r13
                    com.samsung.android.scloud.oem.lib.sync.file.IFileSyncClient r1 = (com.samsung.android.scloud.oem.lib.sync.file.IFileSyncClient) r1     // Catch:{ UnsupportedOperationException -> 0x00d2 }
                    long r6 = r15.longValue()     // Catch:{ UnsupportedOperationException -> 0x00d2 }
                    java.lang.String r8 = r2.toString()     // Catch:{ UnsupportedOperationException -> 0x00d2 }
                    r2 = r12
                    java.lang.String r12 = r1.updateLocal(r2, r3, r4, r5, r6, r8, r9, r10)     // Catch:{ UnsupportedOperationException -> 0x00d2 }
                L_0x00ce:
                    r0.putString(r14, r12)     // Catch:{ UnsupportedOperationException -> 0x00d2 }
                    goto L_0x00e5
                L_0x00d2:
                    r12 = move-exception
                    java.lang.String r13 = r12.getMessage()
                    java.lang.String r14 = "FAIL_CORRUPTED_FILE"
                    boolean r13 = r14.equals(r13)
                    if (r13 == 0) goto L_0x00e6
                    r12 = 1
                    java.lang.String r13 = "need_recover"
                    r0.putBoolean(r13, r12)
                L_0x00e5:
                    return r0
                L_0x00e6:
                    throw r12
                */
                throw new UnsupportedOperationException("Method not decompiled: com.samsung.android.scloud.oem.lib.sync.file.FileSyncManager.C06545.handleServiceAction(android.content.Context, java.lang.Object, java.lang.String, android.os.Bundle):android.os.Bundle");
            }
        });
        SERVICE_HANDLER_MAP.put("deleteItem", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                LOG.m15i("FileSyncManager", "DELETE : " + str);
                boolean deleteLocal = ((IFileSyncClient) obj).deleteLocal(context, bundle.getString("local_id"));
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("is_success", deleteLocal);
                return bundle2;
            }
        });
        SERVICE_HANDLER_MAP.put("complete", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                LOG.m15i("FileSyncManager", "COMPLETE : " + str);
                boolean isComplete = ((IFileSyncClient) obj).isComplete(context, bundle.getString("local_id"), bundle.getString("sync_key"), bundle.getLong("timestamp"), bundle.getInt("rcode"));
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("is_success", isComplete);
                return bundle2;
            }
        });
    }

    public FileSyncManager(IFileSyncClient iFileSyncClient) {
        this.syncClient = iFileSyncClient;
    }

    public IServiceHandler getServiceHandler(String str) {
        return SERVICE_HANDLER_MAP.get(str);
    }

    public Object getClient(String str) {
        return this.syncClient;
    }
}
