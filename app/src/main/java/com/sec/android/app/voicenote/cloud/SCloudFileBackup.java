package com.sec.android.app.voicenote.cloud;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import com.samsung.android.scloud.oem.lib.backup.IBackupClient;
import com.samsung.android.scloud.oem.lib.backup.file.FileClientHelper;
import com.samsung.android.scloud.oem.lib.backup.file.IFileClient;
import com.sec.android.app.voicenote.common.util.CategoryRepository;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.PermissionProvider;
import com.sec.android.app.voicenote.provider.StorageProvider;
import java.io.File;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

public class SCloudFileBackup implements IFileClient {
    private static final String[] BACKUP_PROJECTION = {CategoryRepository.LabelColumn.f102ID, "date_modified", "recordingtype", "recording_mode", "_data", "datetaken"};
    private static final int IDX_DATA = 4;
    private static final int IDX_DATA_TAKEN = 5;
    private static final int IDX_DATEMODIFIED = 1;
    private static final int IDX_ID = 0;
    private static final int IDX_RECORDINGMODE = 3;
    private static final int IDX_RECORDINGTYPE = 2;
    private static final String TAG = "SCloudFileBackup";
    private RestoreItem mRestoredItem;
    private ArrayList<RestoreItem> mRestoredList = new ArrayList<>();
    private String mTransactionKey;

    private static class RestoreItem {
        /* access modifiers changed from: private */
        public long mDataTaken;
        /* access modifiers changed from: private */
        public final String mFileName;
        /* access modifiers changed from: private */
        public final String mFullPath;
        /* access modifiers changed from: private */
        public String mRecordingMode;
        /* access modifiers changed from: private */
        public String mRecordingType;

        RestoreItem(String str, String str2) {
            this.mFullPath = str;
            this.mFileName = str2;
        }

        /* access modifiers changed from: private */
        public RestoreItem setRecordingType(String str) {
            this.mRecordingType = str;
            return this;
        }

        /* access modifiers changed from: private */
        public RestoreItem setRecordingMode(String str) {
            this.mRecordingMode = str;
            return this;
        }

        /* access modifiers changed from: private */
        public RestoreItem setDataTaken(long j) {
            this.mDataTaken = j;
            return this;
        }
    }

    public void initialize(Context context, IBackupClient.ResultListener resultListener) {
        if (PermissionProvider.checkSavingEnable(context)) {
            resultListener.onSuccess();
        } else {
            Log.m26i(TAG, "initialize. Permission error");
            resultListener.onError(326);
        }
        StorageProvider.initSDCardWritableDirPath(context);
    }

    public boolean isBackupPrepare(Context context) {
        Log.m19d(TAG, "isBackupPrepare");
        return true;
    }

    public boolean backupFileMetaAndRecord(Context context, FileClientHelper fileClientHelper) {
        Log.m19d(TAG, "backupFileMetaAndRecord");
        if (context == null) {
            Log.m22e(TAG, "backupFileMetaAndRecord context is null");
            return false;
        }
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, BACKUP_PROJECTION, CursorProvider.getInstance().getBackupListQuery().toString(), (String[]) null, "datetaken DESC");
            if (cursor != null) {
                if (cursor.getCount() != 0) {
                    ArrayList arrayList = new ArrayList();
                    while (cursor.moveToNext()) {
                        String string = cursor.getString(cursor.getColumnIndex(BACKUP_PROJECTION[0]));
                        String string2 = cursor.getString(cursor.getColumnIndex(BACKUP_PROJECTION[1]));
                        JSONObject jSONObject = new JSONObject();
                        jSONObject.put(BACKUP_PROJECTION[2], cursor.getInt(cursor.getColumnIndex(BACKUP_PROJECTION[2])));
                        jSONObject.put(BACKUP_PROJECTION[3], cursor.getInt(cursor.getColumnIndex(BACKUP_PROJECTION[3])));
                        jSONObject.put(BACKUP_PROJECTION[5], cursor.getLong(cursor.getColumnIndex(BACKUP_PROJECTION[5])));
                        arrayList.add(new File(StorageProvider.convertToSDCardWritablePath(cursor.getString(cursor.getColumnIndex(BACKUP_PROJECTION[4])))));
                        fileClientHelper.addFileMetaAndRecord(string, string2, jSONObject, arrayList);
                        arrayList.clear();
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                    return true;
                }
            }
            Log.m22e(TAG, "backupFileMetaAndRecord cursor is null or empty");
            if (cursor != null) {
                cursor.close();
            }
            return true;
        } catch (JSONException e) {
            Log.m22e(TAG, "backupFileMetaAndRecord - JSONException : " + e);
            if (cursor != null) {
                cursor.close();
            }
            return false;
        } catch (SQLiteException e2) {
            Log.m22e(TAG, "backupFileMetaAndRecord - SQLiteException : " + e2);
            if (cursor != null) {
                cursor.close();
            }
            return false;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    public void backupCompleted(Context context) {
        Log.m26i(TAG, "backupCompleted");
    }

    public void backupFailed(Context context) {
        Log.m22e(TAG, "backupFailed");
    }

    public boolean isRestorePrepare(Context context, Bundle bundle) {
        Log.m19d(TAG, "isRestorePrepare");
        return true;
    }

    public ArrayList<String> getFileList(Context context) {
        Log.m19d(TAG, "getFileList");
        ArrayList<String> arrayList = new ArrayList<>();
        if (context == null) {
            Log.m22e(TAG, "getFileList context is null");
            return arrayList;
        }
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_data"}, CursorProvider.getInstance().getBackupListQuery().toString(), (String[]) null, "datetaken DESC");
            if (cursor != null) {
                if (cursor.getCount() != 0) {
                    while (cursor.moveToNext()) {
                        arrayList.add(StorageProvider.convertToSDCardWritablePath(cursor.getString(cursor.getColumnIndex("_data"))));
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                    return arrayList;
                }
            }
            Log.m22e(TAG, "getFileList cursor is null or empty");
            if (cursor != null) {
                cursor.close();
            }
            return arrayList;
        } catch (SQLiteException e) {
            Log.m22e(TAG, "getFileList - SQLiteException : " + e);
            if (cursor != null) {
                cursor.close();
            }
            return arrayList;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    public boolean transactionBegin(JSONObject jSONObject, String str) {
        Log.m19d(TAG, "transactionBegin");
        this.mTransactionKey = str;
        return true;
    }

    public String getRestoreFilePath(Context context, String str) {
        Log.m19d(TAG, "getRestoreFilePath");
        String str2 = Environment.getExternalStorageDirectory() + "/Voice Recorder/";
        File file = new File(str2);
        if (!file.exists() && !file.mkdir()) {
            Log.m22e(TAG, "restoreFile mkdir failed");
        }
        String substring = str.substring(str.lastIndexOf("/") + 1, str.length());
        boolean z = false;
        String substring2 = substring.substring(0, substring.lastIndexOf("."));
        String substring3 = substring.substring(substring.lastIndexOf("."), substring.length());
        String str3 = str2 + substring;
        Log.m19d(TAG, "restoreFile titleName = " + substring2);
        File file2 = new File(str3);
        while (file2.exists() && !z) {
            Log.m22e(TAG, "restoreFile has same FileName");
            substring2 = substring2 + "_1";
            str3 = str2 + substring2 + substring3;
            if (!new File(str3).exists()) {
                z = true;
            }
        }
        this.mRestoredItem = new RestoreItem(str3, substring2);
        return str3;
    }

    public boolean transactionEnd(JSONObject jSONObject, String str) {
        Log.m19d(TAG, "transactionEnd");
        if (jSONObject == null) {
            Log.m22e(TAG, "transactionEnd jsonObject is null");
            return true;
        }
        if (str.isEmpty() || !str.equals(this.mTransactionKey)) {
            Log.m22e(TAG, "transactionEnd transactionKey is empty or mismatch");
        } else {
            try {
                String obj = jSONObject.get(BACKUP_PROJECTION[2]).toString();
                String obj2 = jSONObject.get(BACKUP_PROJECTION[3]).toString();
                long j = jSONObject.getLong(BACKUP_PROJECTION[5]);
                RestoreItem restoreItem = this.mRestoredItem;
                RestoreItem unused = restoreItem.setRecordingType(obj);
                RestoreItem unused2 = restoreItem.setRecordingMode(obj2);
                RestoreItem unused3 = restoreItem.setDataTaken(j);
                this.mRestoredList.add(this.mRestoredItem);
            } catch (JSONException e) {
                Log.m22e(TAG, "transactionEnd - JSONException : " + e);
            }
        }
        return true;
    }

    public void restoreCompleted(Context context, ArrayList<String> arrayList) {
        StringBuilder sb = new StringBuilder();
        sb.append("restoreCompleted : ");
        ArrayList<RestoreItem> arrayList2 = this.mRestoredList;
        sb.append(arrayList2 != null ? arrayList2.size() : 0);
        Log.m26i(TAG, sb.toString());
        ArrayList<RestoreItem> arrayList3 = this.mRestoredList;
        if (arrayList3 != null && arrayList3.size() != 0) {
            insertRecord(context);
        }
    }

    public void restoreFailed(Context context, ArrayList<String> arrayList) {
        Log.m22e(TAG, "restoreFailed");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:27:0x005b, code lost:
        if (r5 == null) goto L_0x005e;
     */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0125 A[Catch:{ all -> 0x0122 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized void insertRecord(android.content.Context r11) {
        /*
            r10 = this;
            monitor-enter(r10)
            java.lang.String r0 = "SCloudFileBackup"
            java.lang.String r1 = "insertRecord"
            com.sec.android.app.voicenote.provider.Log.m19d(r0, r1)     // Catch:{ all -> 0x0149 }
            java.util.ArrayList<com.sec.android.app.voicenote.cloud.SCloudFileBackup$RestoreItem> r0 = r10.mRestoredList     // Catch:{ all -> 0x0149 }
            int r0 = r0.size()     // Catch:{ all -> 0x0149 }
            android.content.ContentValues[] r0 = new android.content.ContentValues[r0]     // Catch:{ all -> 0x0149 }
            java.util.ArrayList<com.sec.android.app.voicenote.cloud.SCloudFileBackup$RestoreItem> r1 = r10.mRestoredList     // Catch:{ all -> 0x0149 }
            java.util.Iterator r1 = r1.iterator()     // Catch:{ all -> 0x0149 }
        L_0x0016:
            boolean r2 = r1.hasNext()     // Catch:{ all -> 0x0149 }
            if (r2 == 0) goto L_0x0129
            java.lang.Object r2 = r1.next()     // Catch:{ all -> 0x0149 }
            com.sec.android.app.voicenote.cloud.SCloudFileBackup$RestoreItem r2 = (com.sec.android.app.voicenote.cloud.SCloudFileBackup.RestoreItem) r2     // Catch:{ all -> 0x0149 }
            java.io.File r3 = new java.io.File     // Catch:{ all -> 0x0149 }
            java.lang.String r4 = r2.mFullPath     // Catch:{ all -> 0x0149 }
            r3.<init>(r4)     // Catch:{ all -> 0x0149 }
            r4 = 0
            android.media.MediaMetadataRetriever r5 = new android.media.MediaMetadataRetriever     // Catch:{ IllegalArgumentException -> 0x0051, all -> 0x004d }
            r5.<init>()     // Catch:{ IllegalArgumentException -> 0x0051, all -> 0x004d }
            java.lang.String r6 = r2.mFullPath     // Catch:{ IllegalArgumentException -> 0x004a }
            r5.setDataSource(r6)     // Catch:{ IllegalArgumentException -> 0x004a }
            r6 = 9
            java.lang.String r6 = r5.extractMetadata(r6)     // Catch:{ IllegalArgumentException -> 0x004a }
            r7 = 12
            java.lang.String r4 = r5.extractMetadata(r7)     // Catch:{ IllegalArgumentException -> 0x0048 }
        L_0x0044:
            r5.release()     // Catch:{ all -> 0x0149 }
            goto L_0x005e
        L_0x0048:
            r7 = move-exception
            goto L_0x0054
        L_0x004a:
            r7 = move-exception
            r6 = r4
            goto L_0x0054
        L_0x004d:
            r11 = move-exception
            r5 = r4
            goto L_0x0123
        L_0x0051:
            r7 = move-exception
            r5 = r4
            r6 = r5
        L_0x0054:
            java.lang.String r8 = "SCloudFileBackup"
            java.lang.String r9 = "IllegalArgumentException"
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r8, (java.lang.String) r9, (java.lang.Throwable) r7)     // Catch:{ all -> 0x0122 }
            if (r5 == 0) goto L_0x005e
            goto L_0x0044
        L_0x005e:
            r7 = 0
            if (r6 == 0) goto L_0x0066
            long r7 = java.lang.Long.parseLong(r6)     // Catch:{ all -> 0x0149 }
        L_0x0066:
            android.content.ContentValues r5 = new android.content.ContentValues     // Catch:{ all -> 0x0149 }
            r5.<init>()     // Catch:{ all -> 0x0149 }
            java.lang.String r6 = "title"
            java.lang.String r9 = r2.mFileName     // Catch:{ all -> 0x0149 }
            r5.put(r6, r9)     // Catch:{ all -> 0x0149 }
            java.lang.String r6 = "mime_type"
            r5.put(r6, r4)     // Catch:{ all -> 0x0149 }
            java.lang.String r4 = "_data"
            java.lang.String r6 = r3.getAbsolutePath()     // Catch:{ all -> 0x0149 }
            java.lang.String r6 = com.sec.android.app.voicenote.provider.StorageProvider.convertToSDCardReadOnlyPath(r6)     // Catch:{ all -> 0x0149 }
            r5.put(r4, r6)     // Catch:{ all -> 0x0149 }
            java.lang.String r4 = "duration"
            java.lang.Long r6 = java.lang.Long.valueOf(r7)     // Catch:{ all -> 0x0149 }
            r5.put(r4, r6)     // Catch:{ all -> 0x0149 }
            java.lang.String r4 = "_size"
            long r6 = r3.length()     // Catch:{ all -> 0x0149 }
            java.lang.Long r6 = java.lang.Long.valueOf(r6)     // Catch:{ all -> 0x0149 }
            r5.put(r4, r6)     // Catch:{ all -> 0x0149 }
            java.lang.String r4 = "datetaken"
            long r6 = r2.mDataTaken     // Catch:{ all -> 0x0149 }
            java.lang.Long r6 = java.lang.Long.valueOf(r6)     // Catch:{ all -> 0x0149 }
            r5.put(r4, r6)     // Catch:{ all -> 0x0149 }
            java.lang.String r4 = "date_modified"
            long r6 = r3.lastModified()     // Catch:{ all -> 0x0149 }
            r8 = 1000(0x3e8, double:4.94E-321)
            long r6 = r6 / r8
            java.lang.Long r3 = java.lang.Long.valueOf(r6)     // Catch:{ all -> 0x0149 }
            r5.put(r4, r3)     // Catch:{ all -> 0x0149 }
            java.lang.String r3 = "track"
            r4 = 0
            java.lang.Integer r6 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x0149 }
            r5.put(r3, r6)     // Catch:{ all -> 0x0149 }
            java.lang.String r3 = "is_ringtone"
            java.lang.Integer r6 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x0149 }
            r5.put(r3, r6)     // Catch:{ all -> 0x0149 }
            java.lang.String r3 = "is_alarm"
            java.lang.Integer r6 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x0149 }
            r5.put(r3, r6)     // Catch:{ all -> 0x0149 }
            java.lang.String r3 = "is_notification"
            java.lang.Integer r6 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x0149 }
            r5.put(r3, r6)     // Catch:{ all -> 0x0149 }
            java.lang.String r3 = "album"
            java.lang.String r6 = "Sounds"
            r5.put(r3, r6)     // Catch:{ all -> 0x0149 }
            java.lang.String r3 = "is_drm"
            java.lang.Integer r6 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x0149 }
            r5.put(r3, r6)     // Catch:{ all -> 0x0149 }
            java.lang.String[] r3 = BACKUP_PROJECTION     // Catch:{ all -> 0x0149 }
            r6 = 2
            r3 = r3[r6]     // Catch:{ all -> 0x0149 }
            java.lang.String r6 = r2.mRecordingType     // Catch:{ all -> 0x0149 }
            r5.put(r3, r6)     // Catch:{ all -> 0x0149 }
            java.lang.String r3 = "is_memo"
            java.lang.Integer r6 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x0149 }
            r5.put(r3, r6)     // Catch:{ all -> 0x0149 }
            java.lang.String[] r3 = BACKUP_PROJECTION     // Catch:{ all -> 0x0149 }
            r6 = 3
            r3 = r3[r6]     // Catch:{ all -> 0x0149 }
            java.lang.String r6 = r2.mRecordingMode     // Catch:{ all -> 0x0149 }
            r5.put(r3, r6)     // Catch:{ all -> 0x0149 }
            java.lang.String r3 = "is_music"
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x0149 }
            r5.put(r3, r4)     // Catch:{ all -> 0x0149 }
            java.util.ArrayList<com.sec.android.app.voicenote.cloud.SCloudFileBackup$RestoreItem> r3 = r10.mRestoredList     // Catch:{ all -> 0x0149 }
            int r2 = r3.indexOf(r2)     // Catch:{ all -> 0x0149 }
            r0[r2] = r5     // Catch:{ all -> 0x0149 }
            goto L_0x0016
        L_0x0122:
            r11 = move-exception
        L_0x0123:
            if (r5 == 0) goto L_0x0128
            r5.release()     // Catch:{ all -> 0x0149 }
        L_0x0128:
            throw r11     // Catch:{ all -> 0x0149 }
        L_0x0129:
            android.content.ContentResolver r11 = r11.getContentResolver()     // Catch:{ all -> 0x0149 }
            android.net.Uri r1 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI     // Catch:{ all -> 0x0149 }
            int r11 = r11.bulkInsert(r1, r0)     // Catch:{ all -> 0x0149 }
            java.util.ArrayList<com.sec.android.app.voicenote.cloud.SCloudFileBackup$RestoreItem> r0 = r10.mRestoredList     // Catch:{ all -> 0x0149 }
            int r0 = r0.size()     // Catch:{ all -> 0x0149 }
            if (r11 == r0) goto L_0x0142
            java.lang.String r11 = "SCloudFileBackup"
            java.lang.String r0 = "insertRecord fail to insert DB"
            com.sec.android.app.voicenote.provider.Log.m22e(r11, r0)     // Catch:{ all -> 0x0149 }
        L_0x0142:
            java.util.ArrayList<com.sec.android.app.voicenote.cloud.SCloudFileBackup$RestoreItem> r11 = r10.mRestoredList     // Catch:{ all -> 0x0149 }
            r11.clear()     // Catch:{ all -> 0x0149 }
            monitor-exit(r10)
            return
        L_0x0149:
            r11 = move-exception
            monitor-exit(r10)
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.cloud.SCloudFileBackup.insertRecord(android.content.Context):void");
    }
}
