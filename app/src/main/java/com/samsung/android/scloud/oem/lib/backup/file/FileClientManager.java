package com.samsung.android.scloud.oem.lib.backup.file;

import android.content.Context;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import com.samsung.android.scloud.oem.lib.LOG;
import com.samsung.android.scloud.oem.lib.backup.BackupMetaManager;
import com.samsung.android.scloud.oem.lib.backup.IBackupClient;
import com.samsung.android.scloud.oem.lib.common.IClientHelper;
import com.samsung.android.scloud.oem.lib.common.IServiceHandler;
import com.samsung.android.scloud.oem.lib.utils.FileTool;
import com.samsung.android.scloud.oem.lib.utils.HashUtil;
import com.samsung.android.scloud.oem.lib.utils.SCloudUtil;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class FileClientManager extends IClientHelper {
    /* access modifiers changed from: private */
    public static final String TAG = "FileClientManager";
    private final IBackupClient backupClient;
    /* access modifiers changed from: private */
    public final ArrayList<String> needToBeProcessedFileList = new ArrayList<>();
    /* access modifiers changed from: private */
    public final ArrayList<String> processedKeyList = new ArrayList<>();
    private final Map<String, IServiceHandler> serviceHandlerMap = new HashMap();

    public FileClientManager(IBackupClient iBackupClient) {
        this.backupClient = iBackupClient;
        this.serviceHandlerMap.put("backupPrepare", new IServiceHandler() {
            public Bundle handleServiceAction(final Context context, Object obj, String str, Bundle bundle) {
                String access$000 = FileClientManager.TAG;
                LOG.m15i(access$000, "[" + str + "] BACKUP_PREPARE");
                final Bundle bundle2 = new Bundle();
                final IFileClient iFileClient = (IFileClient) obj;
                BackupMetaManager.getInstance(context).setCanceled(str, false);
                iFileClient.initialize(context, new IBackupClient.ResultListener() {
                    public void onError(int i) {
                        bundle2.putInt("reason_code", i);
                    }

                    public void onSuccess() {
                        bundle2.putBoolean("is_success", iFileClient.isBackupPrepare(context));
                    }
                });
                return bundle2;
            }
        });
        this.serviceHandlerMap.put("getFileMeta", new IServiceHandler() {
            /* JADX WARNING: Removed duplicated region for block: B:20:0x007c  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public android.os.Bundle handleServiceAction(android.content.Context r7, java.lang.Object r8, java.lang.String r9, android.os.Bundle r10) {
                /*
                    r6 = this;
                    java.lang.String r0 = com.samsung.android.scloud.oem.lib.backup.file.FileClientManager.TAG
                    java.lang.StringBuilder r1 = new java.lang.StringBuilder
                    r1.<init>()
                    java.lang.String r2 = "["
                    r1.append(r2)
                    r1.append(r9)
                    java.lang.String r3 = "] GET_FILE_META"
                    r1.append(r3)
                    java.lang.String r1 = r1.toString()
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r0, r1)
                    java.lang.String r0 = "meta_pfd"
                    android.os.Parcelable r10 = r10.getParcelable(r0)
                    android.os.ParcelFileDescriptor r10 = (android.os.ParcelFileDescriptor) r10
                    android.os.Bundle r0 = new android.os.Bundle
                    r0.<init>()
                    if (r10 != 0) goto L_0x0048
                    java.lang.String r7 = com.samsung.android.scloud.oem.lib.backup.file.FileClientManager.TAG
                    java.lang.StringBuilder r8 = new java.lang.StringBuilder
                    r8.<init>()
                    r8.append(r2)
                    r8.append(r9)
                    java.lang.String r9 = "] GET_FILE_META: meta_pfd is null"
                    r8.append(r9)
                    java.lang.String r8 = r8.toString()
                    com.samsung.android.scloud.oem.lib.LOG.m15i(r7, r8)
                    return r0
                L_0x0048:
                    r1 = 0
                    com.samsung.android.scloud.oem.lib.backup.file.FileClientHelper r2 = new com.samsung.android.scloud.oem.lib.backup.file.FileClientHelper     // Catch:{ all -> 0x0079 }
                    android.util.JsonWriter r3 = new android.util.JsonWriter     // Catch:{ all -> 0x0079 }
                    java.io.FileWriter r4 = new java.io.FileWriter     // Catch:{ all -> 0x0079 }
                    java.io.FileDescriptor r5 = r10.getFileDescriptor()     // Catch:{ all -> 0x0079 }
                    r4.<init>(r5)     // Catch:{ all -> 0x0079 }
                    r3.<init>(r4)     // Catch:{ all -> 0x0079 }
                    r2.<init>(r7, r9, r3)     // Catch:{ all -> 0x0079 }
                    r2.open()     // Catch:{ all -> 0x0076 }
                    com.samsung.android.scloud.oem.lib.backup.file.IFileClient r8 = (com.samsung.android.scloud.oem.lib.backup.file.IFileClient) r8     // Catch:{ all -> 0x0076 }
                    boolean r7 = r8.backupFileMetaAndRecord(r7, r2)     // Catch:{ all -> 0x0076 }
                    r2.release()
                    r10.close()     // Catch:{ IOException -> 0x006c }
                    goto L_0x0070
                L_0x006c:
                    r8 = move-exception
                    r8.printStackTrace()
                L_0x0070:
                    java.lang.String r8 = "is_success"
                    r0.putBoolean(r8, r7)
                    return r0
                L_0x0076:
                    r7 = move-exception
                    r1 = r2
                    goto L_0x007a
                L_0x0079:
                    r7 = move-exception
                L_0x007a:
                    if (r1 == 0) goto L_0x007f
                    r1.release()
                L_0x007f:
                    r10.close()     // Catch:{ IOException -> 0x0083 }
                    goto L_0x0087
                L_0x0083:
                    r8 = move-exception
                    r8.printStackTrace()
                L_0x0087:
                    throw r7
                */
                throw new UnsupportedOperationException("Method not decompiled: com.samsung.android.scloud.oem.lib.backup.file.FileClientManager.C06052.handleServiceAction(android.content.Context, java.lang.Object, java.lang.String, android.os.Bundle):android.os.Bundle");
            }
        });
        this.serviceHandlerMap.put("backupComplete", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                boolean z = bundle.getBoolean("is_success");
                String access$000 = FileClientManager.TAG;
                LOG.m15i(access$000, "[" + str + "] BACKUP_COMPLETE: " + z);
                BackupMetaManager.getInstance(context).setCanceled(str, false);
                if (z) {
                    ((IBackupClient) obj).backupCompleted(context);
                    BackupMetaManager.getInstance(context).setLastBackupTime(str, System.currentTimeMillis());
                    return null;
                }
                ((IBackupClient) obj).backupFailed(context);
                return null;
            }
        });
        this.serviceHandlerMap.put("restorePrepare", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                String access$000 = FileClientManager.TAG;
                LOG.m15i(access$000, "[" + str + "] RESTORE_PREPARE");
                Bundle bundle2 = new Bundle();
                IFileClient iFileClient = (IFileClient) obj;
                final Bundle bundle3 = bundle2;
                final Context context2 = context;
                final String str2 = str;
                final IFileClient iFileClient2 = iFileClient;
                final Bundle bundle4 = bundle;
                iFileClient.initialize(context, new IBackupClient.ResultListener() {
                    public void onError(int i) {
                        bundle3.putInt("reason_code", i);
                    }

                    public void onSuccess() {
                        FileClientManager.this.processedKeyList.clear();
                        FileClientManager.this.needToBeProcessedFileList.clear();
                        BackupMetaManager.getInstance(context2).setCanceled(str2, false);
                        bundle3.putBoolean("is_success", iFileClient2.isRestorePrepare(context2, bundle4));
                    }
                });
                return bundle2;
            }
        });
        this.serviceHandlerMap.put("transactionBegin", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                boolean z;
                String access$000 = FileClientManager.TAG;
                LOG.m15i(access$000, "[" + str + "] TRANSACTION_BEGIN");
                Bundle bundle2 = new Bundle();
                try {
                    String string = bundle.getString("record");
                    z = ((IFileClient) obj).transactionBegin(string != null ? new JSONObject(string) : null, SCloudUtil.makeSHA1Hash(bundle.getString(DialogFactory.BUNDLE_ID)));
                } catch (UnsupportedEncodingException | NoSuchAlgorithmException | JSONException e) {
                    String access$0002 = FileClientManager.TAG;
                    LOG.m14e(access$0002, "[" + str + "] TRANSACTION_BEGIN: Exception", e);
                    z = false;
                }
                bundle2.putBoolean("is_success", z);
                return bundle2;
            }
        });
        this.serviceHandlerMap.put("getFileList", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                String access$000 = FileClientManager.TAG;
                LOG.m15i(access$000, "[" + str + "] GET_FILE_LIST");
                Bundle bundle2 = new Bundle();
                ArrayList<String> fileList = ((IFileClient) obj).getFileList(context);
                if (fileList != null) {
                    String access$0002 = FileClientManager.TAG;
                    LOG.m15i(access$0002, "[" + str + "] GET_FILE_LIST " + fileList.size() + " " + FileClientManager.this.getSize(fileList));
                    bundle2.putStringArrayList("path_list", fileList);
                    bundle2.putBoolean("is_success", true);
                }
                return bundle2;
            }
        });
        this.serviceHandlerMap.put("getLargeFileList", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                ParcelFileDescriptor parcelFileDescriptor = (ParcelFileDescriptor) bundle.getParcelable("pfd");
                boolean z = parcelFileDescriptor != null && FileClientManager.this.writeObject(parcelFileDescriptor, ((IFileClient) obj).getFileList(context));
                String access$000 = FileClientManager.TAG;
                LOG.m15i(access$000, "[" + str + "] GET_LARGE_FILE_LIST " + z);
                return FileClientManager.this.getResult(z);
            }
        });
        this.serviceHandlerMap.put("getLargeHashList", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                ParcelFileDescriptor parcelFileDescriptor = (ParcelFileDescriptor) bundle.getParcelable("pfd");
                boolean z = parcelFileDescriptor != null && FileClientManager.this.writeObject(parcelFileDescriptor, FileClientManager.this.getLocalHashList(((IFileClient) obj).getFileList(context)));
                String access$000 = FileClientManager.TAG;
                LOG.m15i(access$000, "[" + str + "] GET_LARGE_HASH_LIST " + z);
                return FileClientManager.this.getResult(z);
            }
        });
        this.serviceHandlerMap.put("restoreFile", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                String restoreFilePath;
                String access$000 = FileClientManager.TAG;
                LOG.m15i(access$000, "[" + str + "] RESTORE_FILE");
                Bundle bundle2 = new Bundle();
                String string = bundle.getString(DialogFactory.BUNDLE_PATH);
                if (!(string == null || (restoreFilePath = ((IFileClient) obj).getRestoreFilePath(context, string)) == null)) {
                    String access$0002 = FileClientManager.TAG;
                    LOG.m15i(access$0002, "[" + str + "] RESTORE_FILE: path: " + restoreFilePath);
                    bundle2.putParcelable("file_descriptor", FileTool.openFile(restoreFilePath));
                    FileClientManager.this.needToBeProcessedFileList.add(restoreFilePath);
                    bundle2.putBoolean("is_success", true);
                }
                return bundle2;
            }
        });
        this.serviceHandlerMap.put("transactionEnd", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                String access$000 = FileClientManager.TAG;
                LOG.m15i(access$000, "[" + str + "] TRANSACTION_END");
                Bundle bundle2 = new Bundle();
                try {
                    String string = bundle.getString("record");
                    if (((IFileClient) obj).transactionEnd(string != null ? new JSONObject(string) : null, SCloudUtil.makeSHA1Hash(bundle.getString(DialogFactory.BUNDLE_ID)))) {
                        FileClientManager.this.processedKeyList.addAll(FileClientManager.this.needToBeProcessedFileList);
                        bundle2.putBoolean("is_success", true);
                    }
                    FileClientManager.this.needToBeProcessedFileList.clear();
                } catch (UnsupportedEncodingException | NoSuchAlgorithmException | JSONException e) {
                    String access$0002 = FileClientManager.TAG;
                    LOG.m14e(access$0002, "[" + str + "] TRANSACTION_END: Exception", e);
                }
                return bundle2;
            }
        });
        this.serviceHandlerMap.put("restoreComplete", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                boolean z = bundle.getBoolean("is_success");
                Bundle bundle2 = new Bundle();
                BackupMetaManager.getInstance(context).setCanceled(str, false);
                if (z) {
                    ((IBackupClient) obj).restoreCompleted(context, FileClientManager.this.processedKeyList);
                } else {
                    ((IBackupClient) obj).restoreFailed(context, FileClientManager.this.processedKeyList);
                }
                FileClientManager.this.processedKeyList.clear();
                return bundle2;
            }
        });
        this.serviceHandlerMap.put("deleteRestoreFile", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                ArrayList<String> stringArrayList = bundle.getStringArrayList("path_list");
                if (stringArrayList != null) {
                    String access$000 = FileClientManager.TAG;
                    LOG.m15i(access$000, "[" + str + "] DELETE_RESTORE_FILE: " + stringArrayList.size());
                    Iterator<String> it = stringArrayList.iterator();
                    while (it.hasNext()) {
                        new File(context.getFilesDir(), it.next()).delete();
                    }
                }
                return new Bundle();
            }
        });
        this.serviceHandlerMap.put("checkAndUpdateReuseDB", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                String access$000 = FileClientManager.TAG;
                LOG.m15i(access$000, "[" + str + "] CHECK_AND_UPDATE_REUSE_DB");
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("is_success", true);
                return bundle2;
            }
        });
        this.serviceHandlerMap.put("completeFile", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                String access$000 = FileClientManager.TAG;
                LOG.m15i(access$000, "[" + str + "] COMPLETE_FILE");
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("is_success", true);
                return bundle2;
            }
        });
        this.serviceHandlerMap.put("clearReuseFileDB", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                String access$000 = FileClientManager.TAG;
                LOG.m15i(access$000, "[" + str + "] CLEAR_REUSE_FILE_DB");
                return new Bundle();
            }
        });
        this.serviceHandlerMap.put("requestCancel", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                String access$000 = FileClientManager.TAG;
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
    public long getSize(ArrayList<String> arrayList) {
        Iterator<String> it = arrayList.iterator();
        long j = 0;
        while (it.hasNext()) {
            j += (long) it.next().length();
        }
        return j;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001a, code lost:
        r6 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001b, code lost:
        r2 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x001f, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0020, code lost:
        r3 = r2;
        r2 = r6;
        r6 = r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean writeObject(android.os.ParcelFileDescriptor r5, java.util.List<java.lang.String> r6) {
        /*
            r4 = this;
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x0045 }
            java.io.FileDescriptor r5 = r5.getFileDescriptor()     // Catch:{ IOException -> 0x0045 }
            r0.<init>(r5)     // Catch:{ IOException -> 0x0045 }
            r5 = 0
            java.io.ObjectOutputStream r1 = new java.io.ObjectOutputStream     // Catch:{ Throwable -> 0x0034 }
            r1.<init>(r0)     // Catch:{ Throwable -> 0x0034 }
            r1.writeObject(r6)     // Catch:{ Throwable -> 0x001d, all -> 0x001a }
            r1.close()     // Catch:{ Throwable -> 0x0034 }
            r0.close()     // Catch:{ IOException -> 0x0045 }
            r5 = 1
            return r5
        L_0x001a:
            r6 = move-exception
            r2 = r5
            goto L_0x0023
        L_0x001d:
            r6 = move-exception
            throw r6     // Catch:{ all -> 0x001f }
        L_0x001f:
            r2 = move-exception
            r3 = r2
            r2 = r6
            r6 = r3
        L_0x0023:
            if (r2 == 0) goto L_0x002e
            r1.close()     // Catch:{ Throwable -> 0x0029 }
            goto L_0x0031
        L_0x0029:
            r1 = move-exception
            r2.addSuppressed(r1)     // Catch:{ Throwable -> 0x0034 }
            goto L_0x0031
        L_0x002e:
            r1.close()     // Catch:{ Throwable -> 0x0034 }
        L_0x0031:
            throw r6     // Catch:{ Throwable -> 0x0034 }
        L_0x0032:
            r6 = move-exception
            goto L_0x0036
        L_0x0034:
            r5 = move-exception
            throw r5     // Catch:{ all -> 0x0032 }
        L_0x0036:
            if (r5 == 0) goto L_0x0041
            r0.close()     // Catch:{ Throwable -> 0x003c }
            goto L_0x0044
        L_0x003c:
            r0 = move-exception
            r5.addSuppressed(r0)     // Catch:{ IOException -> 0x0045 }
            goto L_0x0044
        L_0x0041:
            r0.close()     // Catch:{ IOException -> 0x0045 }
        L_0x0044:
            throw r6     // Catch:{ IOException -> 0x0045 }
        L_0x0045:
            r5 = move-exception
            r5.printStackTrace()
            r5 = 0
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.samsung.android.scloud.oem.lib.backup.file.FileClientManager.writeObject(android.os.ParcelFileDescriptor, java.util.List):boolean");
    }

    /* access modifiers changed from: private */
    public List<String> getLocalHashList(List<String> list) {
        ArrayList arrayList = new ArrayList();
        if (list != null) {
            for (String file : list) {
                try {
                    arrayList.add(HashUtil.getFileSHA256(new File(file)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    public Bundle getResult(boolean z) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("is_success", z);
        return bundle;
    }
}
