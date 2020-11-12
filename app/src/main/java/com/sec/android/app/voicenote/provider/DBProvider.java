package com.sec.android.app.voicenote.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.LongSparseArray;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.CategoryRepository;
import com.sec.android.app.voicenote.common.util.DataRepository;
import com.sec.android.app.voicenote.data.trash.TrashInfo;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.service.MetadataRepository;
import com.sec.android.app.voicenote.service.codec.M4aConsts;
import com.sec.android.app.voicenote.service.codec.M4aInfo;
import com.sec.android.app.voicenote.service.codec.M4aReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DBProvider {
    private static final String TAG = "DBProvider";
    private static DBProvider mInstance;
    /* access modifiers changed from: private */
    public Context mAppContext = null;

    private DBProvider() {
    }

    public static DBProvider getInstance() {
        if (mInstance == null) {
            mInstance = new DBProvider();
        }
        return mInstance;
    }

    public void setApplicationContext(Context context) {
        this.mAppContext = context;
    }

    public boolean updateDB(String str, ContentValues contentValues) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String externalSDStorageFsUuid = StorageProvider.getExternalSDStorageFsUuid(this.mAppContext);
        if (externalSDStorageFsUuid != null) {
            String lowerCase = str.toLowerCase();
            if (lowerCase.contains("storage/" + externalSDStorageFsUuid.toLowerCase() + "/")) {
                uri = Uri.parse("content://media/" + externalSDStorageFsUuid.toLowerCase() + "/audio/media");
            }
        }
        ContentResolver contentResolver = this.mAppContext.getContentResolver();
        StringBuilder sb = new StringBuilder();
        sb.append("_data=\"");
        sb.append(str);
        sb.append('\"');
        return contentResolver.update(uri, contentValues, sb.toString(), (String[]) null) > 0;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: android.net.Uri} */
    /* JADX WARNING: type inference failed for: r1v0 */
    /* JADX WARNING: type inference failed for: r1v1, types: [android.database.Cursor] */
    /* JADX WARNING: type inference failed for: r1v3 */
    /* JADX WARNING: type inference failed for: r1v4 */
    /* JADX WARNING: type inference failed for: r1v5 */
    /* JADX WARNING: type inference failed for: r1v6 */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.net.Uri insertDB(java.lang.String r11, android.content.ContentValues r12) {
        /*
            r10 = this;
            android.content.Context r0 = r10.mAppContext
            boolean r0 = com.sec.android.app.voicenote.provider.PermissionProvider.isStorageAccessEnable(r0)
            r1 = 0
            java.lang.String r2 = "DBProvider"
            if (r0 != 0) goto L_0x0011
            java.lang.String r11 = "insertDB permission fail !!"
            com.sec.android.app.voicenote.provider.Log.m26i(r2, r11)
            return r1
        L_0x0011:
            java.lang.String r0 = "_id"
            java.lang.String[] r5 = new java.lang.String[]{r0}
            android.net.Uri r0 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            android.content.Context r3 = r10.mAppContext     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            java.lang.String r3 = com.sec.android.app.voicenote.provider.StorageProvider.getExternalSDStorageFsUuid(r3)     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            if (r3 == 0) goto L_0x0063
            java.lang.String r4 = r11.toLowerCase()     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            r6.<init>()     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            java.lang.String r7 = "storage/"
            r6.append(r7)     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            java.lang.String r7 = r3.toLowerCase()     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            r6.append(r7)     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            java.lang.String r7 = "/"
            r6.append(r7)     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            boolean r4 = r4.contains(r6)     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            if (r4 == 0) goto L_0x0063
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            r0.<init>()     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            java.lang.String r4 = "content://media/"
            r0.append(r4)     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            java.lang.String r3 = r3.toLowerCase()     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            r0.append(r3)     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            java.lang.String r3 = "/audio/media"
            r0.append(r3)     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            android.net.Uri r0 = android.net.Uri.parse(r0)     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
        L_0x0063:
            android.content.Context r3 = r10.mAppContext     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            android.content.ContentResolver r3 = r3.getContentResolver()     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            r4.<init>()     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            java.lang.String r6 = "_data=\""
            r4.append(r6)     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            r4.append(r11)     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            r11 = 34
            r4.append(r11)     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            java.lang.String r6 = r4.toString()     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            r7 = 0
            r8 = 0
            r4 = r0
            android.database.Cursor r11 = r3.query(r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x00e7, all -> 0x00e5 }
            if (r11 == 0) goto L_0x00d9
            int r3 = r11.getCount()     // Catch:{ Exception -> 0x00d4, all -> 0x00d0 }
            if (r3 != 0) goto L_0x009f
            java.lang.String r3 = "saved file is not inserted to DB yet"
            com.sec.android.app.voicenote.provider.Log.m29v(r2, r3)     // Catch:{ Exception -> 0x00d4, all -> 0x00d0 }
            android.content.Context r3 = r10.mAppContext     // Catch:{ Exception -> 0x00d4, all -> 0x00d0 }
            android.content.ContentResolver r3 = r3.getContentResolver()     // Catch:{ Exception -> 0x00d4, all -> 0x00d0 }
            android.net.Uri r12 = r3.insert(r0, r12)     // Catch:{ Exception -> 0x00d4, all -> 0x00d0 }
        L_0x009d:
            r1 = r12
            goto L_0x00d9
        L_0x009f:
            java.lang.String r12 = "saved file is inserted to DB already"
            com.sec.android.app.voicenote.provider.Log.m32w((java.lang.String) r2, (java.lang.String) r12)     // Catch:{ Exception -> 0x00d4, all -> 0x00d0 }
            r11.moveToFirst()     // Catch:{ Exception -> 0x00d4, all -> 0x00d0 }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d4, all -> 0x00d0 }
            r12.<init>()     // Catch:{ Exception -> 0x00d4, all -> 0x00d0 }
            java.lang.String r0 = "content://media"
            r12.append(r0)     // Catch:{ Exception -> 0x00d4, all -> 0x00d0 }
            android.net.Uri r0 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x00d4, all -> 0x00d0 }
            java.lang.String r0 = r0.getPath()     // Catch:{ Exception -> 0x00d4, all -> 0x00d0 }
            r12.append(r0)     // Catch:{ Exception -> 0x00d4, all -> 0x00d0 }
            r0 = 47
            r12.append(r0)     // Catch:{ Exception -> 0x00d4, all -> 0x00d0 }
            r0 = 0
            long r3 = r11.getLong(r0)     // Catch:{ Exception -> 0x00d4, all -> 0x00d0 }
            r12.append(r3)     // Catch:{ Exception -> 0x00d4, all -> 0x00d0 }
            java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x00d4, all -> 0x00d0 }
            android.net.Uri r12 = android.net.Uri.parse(r12)     // Catch:{ Exception -> 0x00d4, all -> 0x00d0 }
            goto L_0x009d
        L_0x00d0:
            r12 = move-exception
            r1 = r11
            r11 = r12
            goto L_0x010f
        L_0x00d4:
            r12 = move-exception
            r9 = r12
            r12 = r11
            r11 = r9
            goto L_0x00e9
        L_0x00d9:
            if (r11 == 0) goto L_0x010c
            boolean r12 = r11.isClosed()
            if (r12 != 0) goto L_0x010c
            r11.close()
            goto L_0x010c
        L_0x00e5:
            r11 = move-exception
            goto L_0x010f
        L_0x00e7:
            r11 = move-exception
            r12 = r1
        L_0x00e9:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x010d }
            r0.<init>()     // Catch:{ all -> 0x010d }
            java.lang.String r3 = "Exception : "
            r0.append(r3)     // Catch:{ all -> 0x010d }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x010d }
            r0.append(r11)     // Catch:{ all -> 0x010d }
            java.lang.String r11 = r0.toString()     // Catch:{ all -> 0x010d }
            com.sec.android.app.voicenote.provider.Log.m22e(r2, r11)     // Catch:{ all -> 0x010d }
            if (r12 == 0) goto L_0x010c
            boolean r11 = r12.isClosed()
            if (r11 != 0) goto L_0x010c
            r12.close()
        L_0x010c:
            return r1
        L_0x010d:
            r11 = move-exception
            r1 = r12
        L_0x010f:
            if (r1 == 0) goto L_0x011a
            boolean r12 = r1.isClosed()
            if (r12 != 0) goto L_0x011a
            r1.close()
        L_0x011a:
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.provider.DBProvider.insertDB(java.lang.String, android.content.ContentValues):android.net.Uri");
    }

    public String createNewFileName(int i) {
        String str;
        Context context = this.mAppContext;
        if (context == null || !PermissionProvider.isStorageAccessEnable(context)) {
            return null;
        }
        if (i == 0) {
            i = MetadataRepository.getInstance().getRecordMode();
        }
        if (i == 2) {
            str = this.mAppContext.getResources().getString(C0690R.string.interview_mode);
        } else if (i != 4) {
            str = this.mAppContext.getResources().getString(C0690R.string.voice);
        } else {
            str = this.mAppContext.getResources().getString(C0690R.string.prefix_voicememo);
        }
        String findFileName = findFileName(str + ' ', findFileIndexByPrefix(str));
        if (Settings.getIntSettings(Settings.KEY_STORAGE, 0) != 1) {
            return findFileName;
        }
        return findFileName + "_sd";
    }

    public String createNewSimpleFileName(int i) {
        String str;
        Context context = this.mAppContext;
        if (context == null || !PermissionProvider.isStorageAccessEnable(context)) {
            return null;
        }
        if (i != 4) {
            str = this.mAppContext.getResources().getString(C0690R.string.voice);
        } else {
            str = this.mAppContext.getResources().getString(C0690R.string.prefix_voicememo);
        }
        String findFileName = findFileName(str + ' ', findSimpleFileIndexByPrefix(str));
        if (Settings.getIntSettings(Settings.KEY_STORAGE, 0) != 1) {
            return findFileName;
        }
        return findFileName + "_sd";
    }

    /* JADX WARNING: Code restructure failed: missing block: B:1:0x0020, code lost:
        if (r0 == 0) goto L_0x0022;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0022, code lost:
        r0 = r0 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:4:0x002f, code lost:
        if (r0 >= Long.MAX_VALUE) goto L_0x0049;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0047, code lost:
        if (isSameFileInLibrary(java.lang.String.format(java.util.Locale.getDefault(), "%s %d", new java.lang.Object[]{r12, java.lang.Long.valueOf(r0)})) != false) goto L_0x0022;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x005b, code lost:
        return java.lang.String.format(java.util.Locale.getDefault(), "%s %d", new java.lang.Object[]{r12, java.lang.Long.valueOf(r0)});
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String createNewTitle(java.lang.String r12) {
        /*
            r11 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "createNewTitle origin name : "
            r0.append(r1)
            r0.append(r12)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "DBProvider"
            com.sec.android.app.voicenote.provider.Log.m19d(r1, r0)
            long r0 = r11.findFileIndexByPrefix(r12)
            r2 = 0
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            r3 = 1
            if (r2 != 0) goto L_0x0023
        L_0x0022:
            long r0 = r0 + r3
        L_0x0023:
            r5 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            int r2 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            r5 = 1
            r6 = 0
            r7 = 2
            java.lang.String r8 = "%s %d"
            if (r2 >= 0) goto L_0x0049
            java.util.Locale r2 = java.util.Locale.getDefault()
            java.lang.Object[] r9 = new java.lang.Object[r7]
            r9[r6] = r12
            java.lang.Long r10 = java.lang.Long.valueOf(r0)
            r9[r5] = r10
            java.lang.String r2 = java.lang.String.format(r2, r8, r9)
            boolean r2 = r11.isSameFileInLibrary(r2)
            if (r2 != 0) goto L_0x0022
        L_0x0049:
            java.util.Locale r2 = java.util.Locale.getDefault()
            java.lang.Object[] r3 = new java.lang.Object[r7]
            r3[r6] = r12
            java.lang.Long r12 = java.lang.Long.valueOf(r0)
            r3[r5] = r12
            java.lang.String r12 = java.lang.String.format(r2, r8, r3)
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.provider.DBProvider.createNewTitle(java.lang.String):java.lang.String");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0079, code lost:
        if (r6 == 0) goto L_0x007b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x007b, code lost:
        r6 = r6 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0084, code lost:
        if (r6 >= Long.MAX_VALUE) goto L_0x00a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x009e, code lost:
        if (isSameFileInLibrary(java.lang.String.format(java.util.Locale.getDefault(), "%s-%d", new java.lang.Object[]{r1, java.lang.Long.valueOf(r6)})) != false) goto L_0x007b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00c3, code lost:
        return java.lang.String.format(java.util.Locale.getDefault(), "%s%s-%d%s", new java.lang.Object[]{r14.substring(0, r0 + 1), r1, java.lang.Long.valueOf(r6), r14.substring(r2)});
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String createNewFilePath(java.lang.String r14) {
        /*
            r13 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "createNewFilePath prefix : "
            r0.append(r1)
            r0.append(r14)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "DBProvider"
            com.sec.android.app.voicenote.provider.Log.m19d(r1, r0)
            r0 = 47
            int r0 = r14.lastIndexOf(r0)
            r1 = 45
            int r1 = r14.lastIndexOf(r1)
            r2 = 46
            int r2 = r14.lastIndexOf(r2)
            r3 = -1
            r4 = 0
            r5 = 1
            if (r0 == r3) goto L_0x002f
            r6 = r5
            goto L_0x0030
        L_0x002f:
            r6 = r4
        L_0x0030:
            if (r1 == r3) goto L_0x0042
            int r7 = r1 + 1
            if (r2 <= r7) goto L_0x0042
            java.lang.String r7 = r14.substring(r7, r2)
            java.lang.String r8 = "\\d{3,4}"
            boolean r7 = r7.matches(r8)
            r7 = r7 ^ r5
            goto L_0x0043
        L_0x0042:
            r7 = r5
        L_0x0043:
            if (r2 == r3) goto L_0x0047
            r3 = r5
            goto L_0x0048
        L_0x0047:
            r3 = r4
        L_0x0048:
            if (r6 == 0) goto L_0x005c
            if (r3 == 0) goto L_0x005c
            if (r7 == 0) goto L_0x0055
            int r1 = r0 + 1
            java.lang.String r1 = r14.substring(r1, r2)
            goto L_0x005e
        L_0x0055:
            int r3 = r0 + 1
            java.lang.String r1 = r14.substring(r3, r1)
            goto L_0x005e
        L_0x005c:
            java.lang.String r1 = ""
        L_0x005e:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r1)
            java.lang.String r6 = "-"
            r3.append(r6)
            java.lang.String r3 = r3.toString()
            long r6 = r13.findFileIndexByPrefix(r3)
            r8 = 0
            int r3 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            r8 = 1
            if (r3 != 0) goto L_0x007c
        L_0x007b:
            long r6 = r6 + r8
        L_0x007c:
            r10 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            int r3 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            r10 = 2
            if (r3 >= 0) goto L_0x00a0
            java.util.Locale r3 = java.util.Locale.getDefault()
            java.lang.Object[] r11 = new java.lang.Object[r10]
            r11[r4] = r1
            java.lang.Long r12 = java.lang.Long.valueOf(r6)
            r11[r5] = r12
            java.lang.String r12 = "%s-%d"
            java.lang.String r3 = java.lang.String.format(r3, r12, r11)
            boolean r3 = r13.isSameFileInLibrary(r3)
            if (r3 != 0) goto L_0x007b
        L_0x00a0:
            java.util.Locale r3 = java.util.Locale.getDefault()
            r8 = 4
            java.lang.Object[] r8 = new java.lang.Object[r8]
            int r0 = r0 + r5
            java.lang.String r0 = r14.substring(r4, r0)
            r8[r4] = r0
            r8[r5] = r1
            java.lang.Long r0 = java.lang.Long.valueOf(r6)
            r8[r10] = r0
            r0 = 3
            java.lang.String r14 = r14.substring(r2)
            r8[r0] = r14
            java.lang.String r14 = "%s%s-%d%s"
            java.lang.String r14 = java.lang.String.format(r3, r14, r8)
            return r14
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.provider.DBProvider.createNewFilePath(java.lang.String):java.lang.String");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0068, code lost:
        if (r6 == 0) goto L_0x006a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x006a, code lost:
        r6 = r6 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0073, code lost:
        if (r6 >= Long.MAX_VALUE) goto L_0x008f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x008d, code lost:
        if (isSameFileInLibrary(java.lang.String.format(java.util.Locale.getDefault(), "%s-%d", new java.lang.Object[]{r1, java.lang.Long.valueOf(r6)})) != false) goto L_0x006a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00b2, code lost:
        return java.lang.String.format(java.util.Locale.getDefault(), "%s%s-%d%s", new java.lang.Object[]{r14.substring(0, r0 + 1), r1, java.lang.Long.valueOf(r6), r14.substring(r2)});
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String createNewSimpleFilePath(java.lang.String r14) {
        /*
            r13 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "createNewFilePath prefix : "
            r0.append(r1)
            r0.append(r14)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "DBProvider"
            com.sec.android.app.voicenote.provider.Log.m19d(r1, r0)
            r0 = 47
            int r0 = r14.lastIndexOf(r0)
            r1 = 45
            int r1 = r14.lastIndexOf(r1)
            r2 = 46
            int r2 = r14.lastIndexOf(r2)
            r3 = -1
            r4 = 0
            r5 = 1
            if (r0 == r3) goto L_0x002f
            r6 = r5
            goto L_0x0030
        L_0x002f:
            r6 = r4
        L_0x0030:
            if (r1 == r3) goto L_0x0042
            int r7 = r1 + 1
            if (r2 <= r7) goto L_0x0042
            java.lang.String r7 = r14.substring(r7, r2)
            java.lang.String r8 = "\\d{3,4}"
            boolean r7 = r7.matches(r8)
            r7 = r7 ^ r5
            goto L_0x0043
        L_0x0042:
            r7 = r5
        L_0x0043:
            if (r2 == r3) goto L_0x0047
            r3 = r5
            goto L_0x0048
        L_0x0047:
            r3 = r4
        L_0x0048:
            if (r6 == 0) goto L_0x005c
            if (r3 == 0) goto L_0x005c
            if (r7 == 0) goto L_0x0055
            int r1 = r0 + 1
            java.lang.String r1 = r14.substring(r1, r2)
            goto L_0x005e
        L_0x0055:
            int r3 = r0 + 1
            java.lang.String r1 = r14.substring(r3, r1)
            goto L_0x005e
        L_0x005c:
            java.lang.String r1 = ""
        L_0x005e:
            long r6 = r13.findSimpleFileIndexByPrefix(r1)
            r8 = 0
            int r3 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            r8 = 1
            if (r3 != 0) goto L_0x006b
        L_0x006a:
            long r6 = r6 + r8
        L_0x006b:
            r10 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            int r3 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            r10 = 2
            if (r3 >= 0) goto L_0x008f
            java.util.Locale r3 = java.util.Locale.getDefault()
            java.lang.Object[] r11 = new java.lang.Object[r10]
            r11[r4] = r1
            java.lang.Long r12 = java.lang.Long.valueOf(r6)
            r11[r5] = r12
            java.lang.String r12 = "%s-%d"
            java.lang.String r3 = java.lang.String.format(r3, r12, r11)
            boolean r3 = r13.isSameFileInLibrary(r3)
            if (r3 != 0) goto L_0x006a
        L_0x008f:
            java.util.Locale r3 = java.util.Locale.getDefault()
            r8 = 4
            java.lang.Object[] r8 = new java.lang.Object[r8]
            int r0 = r0 + r5
            java.lang.String r0 = r14.substring(r4, r0)
            r8[r4] = r0
            r8[r5] = r1
            java.lang.Long r0 = java.lang.Long.valueOf(r6)
            r8[r10] = r0
            r0 = 3
            java.lang.String r14 = r14.substring(r2)
            r8[r0] = r14
            java.lang.String r14 = "%s%s-%d%s"
            java.lang.String r14 = java.lang.String.format(r3, r14, r8)
            return r14
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.provider.DBProvider.createNewSimpleFilePath(java.lang.String):java.lang.String");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:64:0x019d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x019e, code lost:
        r17 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x01a1, code lost:
        r0 = th;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x012d A[Catch:{ Exception -> 0x019d, all -> 0x01a1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x01a1 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:8:0x007c] */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0130 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private long findFileIndexByPrefix(java.lang.String r24) {
        /*
            r23 = this;
            r1 = r23
            r2 = r24
            java.lang.String r3 = "_memo"
            java.lang.String r4 = "_sd"
            java.lang.String r5 = "-"
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r6 = "prefix = "
            r0.append(r6)
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.lang.String r6 = "DBProvider"
            com.sec.android.app.voicenote.provider.Log.m19d(r6, r0)
            if (r2 == 0) goto L_0x01e0
            android.content.Context r0 = r1.mAppContext
            boolean r0 = com.sec.android.app.voicenote.provider.PermissionProvider.isStorageAccessEnable(r0)
            if (r0 != 0) goto L_0x002c
            goto L_0x01e0
        L_0x002c:
            java.lang.String r9 = "title"
            java.lang.String[] r12 = new java.lang.String[]{r9}
            java.lang.String r13 = "title like ? AND (_data LIKE '%.amr' or (_data LIKE '%.m4a' and recordingtype == '1'))"
            r0 = 2
            java.lang.String[] r14 = new java.lang.String[r0]
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r2)
            java.lang.String r10 = " %"
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            r15 = 0
            r14[r15] = r0
            java.util.Locale r0 = java.util.Locale.US
            r11 = 1
            java.lang.Object[] r10 = new java.lang.Object[r11]
            int r16 = r24.length()
            int r16 = r16 + 1
            java.lang.Integer r16 = java.lang.Integer.valueOf(r16)
            r10[r15] = r16
            java.lang.String r15 = "%d"
            java.lang.String r0 = java.lang.String.format(r0, r15, r10)
            r14[r11] = r0
            java.lang.String r15 = "substr(title, ?, 1000) ASC"
            r17 = 0
            r18 = 1
            android.content.Context r0 = r1.mAppContext     // Catch:{ Exception -> 0x01bc }
            android.content.ContentResolver r10 = r0.getContentResolver()     // Catch:{ Exception -> 0x01bc }
            android.net.Uri r0 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x01bc }
            r20 = r11
            r11 = r0
            r7 = 0
            android.database.Cursor r8 = r10.query(r11, r12, r13, r14, r15)     // Catch:{ Exception -> 0x01bc }
            if (r8 == 0) goto L_0x01a7
            boolean r0 = r8.isClosed()     // Catch:{ Exception -> 0x01a3, all -> 0x01a1 }
            if (r0 != 0) goto L_0x01a7
            int r0 = r8.getCount()     // Catch:{ Exception -> 0x01a3, all -> 0x01a1 }
            if (r0 != 0) goto L_0x008a
            goto L_0x01a7
        L_0x008a:
            java.lang.String r0 = ""
            r8.moveToFirst()     // Catch:{ Exception -> 0x01a3, all -> 0x01a1 }
            r10 = r0
            r11 = 0
            r21 = 0
        L_0x0094:
            boolean r0 = r8.isAfterLast()     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            if (r0 != 0) goto L_0x0135
            int r0 = r8.getColumnIndex(r9)     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            java.lang.String r13 = r8.getString(r0)     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            android.content.Context r0 = r1.mAppContext     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            r14 = 2131755634(0x7f100272, float:1.9142153E38)
            java.lang.String r0 = r0.getString(r14)     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            boolean r0 = r2.equals(r0)     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            if (r0 != 0) goto L_0x00e5
            android.content.Context r0 = r1.mAppContext     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            r14 = 2131755309(0x7f10012d, float:1.9141494E38)
            java.lang.String r0 = r0.getString(r14)     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            boolean r0 = r2.equals(r0)     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            if (r0 != 0) goto L_0x00e5
            android.content.Context r0 = r1.mAppContext     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            r14 = 2131755413(0x7f100195, float:1.9141705E38)
            java.lang.String r0 = r0.getString(r14)     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            boolean r0 = r2.equals(r0)     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            if (r0 == 0) goto L_0x00dc
            goto L_0x00e5
        L_0x00dc:
            int r0 = r24.length()     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            java.lang.String r0 = r13.substring(r0)     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            goto L_0x0119
        L_0x00e5:
            int r0 = r24.length()     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            int r0 = r0 + 1
            java.lang.String r0 = r13.substring(r0)     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            boolean r14 = r0.contains(r5)     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            if (r14 == 0) goto L_0x00fd
            int r14 = r0.indexOf(r5)     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            java.lang.String r0 = r0.substring(r7, r14)     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
        L_0x00fd:
            boolean r14 = r0.contains(r4)     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            if (r14 == 0) goto L_0x010b
            int r14 = r0.indexOf(r4)     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            java.lang.String r0 = r0.substring(r7, r14)     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
        L_0x010b:
            boolean r14 = r0.contains(r3)     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            if (r14 == 0) goto L_0x0119
            int r14 = r0.indexOf(r3)     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            java.lang.String r0 = r0.substring(r7, r14)     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
        L_0x0119:
            long r11 = java.lang.Long.parseLong(r0)     // Catch:{ NumberFormatException -> 0x011e }
            goto L_0x0127
        L_0x011e:
            r0 = move-exception
            r14 = r0
            java.lang.String r0 = r14.toString()     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            com.sec.android.app.voicenote.provider.Log.m22e(r6, r0)     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
        L_0x0127:
            long r14 = r21 + r18
            int r0 = (r11 > r14 ? 1 : (r11 == r14 ? 0 : -1))
            if (r0 != 0) goto L_0x0130
            r21 = r11
            r10 = r13
        L_0x0130:
            r8.moveToNext()     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            goto L_0x0094
        L_0x0135:
            r3 = 9223372036854775707(0x7fffffffffffff9b, double:NaN)
            int r0 = (r21 > r3 ? 1 : (r21 == r3 ? 0 : -1))
            if (r0 < 0) goto L_0x0191
            int r0 = r24.length()     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            int r3 = r24.length()     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            int r3 = r3 + 3
            java.lang.String r0 = r10.substring(r0, r3)     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
            java.lang.Long r0 = java.lang.Long.valueOf(r0)     // Catch:{ NumberFormatException -> 0x018b }
            long r3 = r0.longValue()     // Catch:{ NumberFormatException -> 0x018b }
        L_0x0154:
            long r3 = r3 + r18
            r9 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            int r0 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r0 >= 0) goto L_0x0191
            java.lang.String r0 = r1.findFileName(r2, r3)     // Catch:{ NumberFormatException -> 0x018b }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ NumberFormatException -> 0x018b }
            r5.<init>()     // Catch:{ NumberFormatException -> 0x018b }
            java.lang.String r7 = com.sec.android.app.voicenote.provider.StorageProvider.getVoiceRecorderPath()     // Catch:{ NumberFormatException -> 0x018b }
            r5.append(r7)     // Catch:{ NumberFormatException -> 0x018b }
            r7 = 47
            r5.append(r7)     // Catch:{ NumberFormatException -> 0x018b }
            r5.append(r0)     // Catch:{ NumberFormatException -> 0x018b }
            java.lang.String r0 = ".m4a"
            r5.append(r0)     // Catch:{ NumberFormatException -> 0x018b }
            java.lang.String r0 = r5.toString()     // Catch:{ NumberFormatException -> 0x018b }
            boolean r0 = com.sec.android.app.voicenote.provider.StorageProvider.isExistFile((java.lang.String) r0)     // Catch:{ NumberFormatException -> 0x018b }
            if (r0 != 0) goto L_0x0154
            long r3 = r3 - r18
            r21 = r3
            goto L_0x0191
        L_0x018b:
            r0 = move-exception
            java.lang.String r2 = "NumberFormatException: "
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r6, (java.lang.String) r2, (java.lang.Throwable) r0)     // Catch:{ Exception -> 0x019d, all -> 0x01a1 }
        L_0x0191:
            if (r8 == 0) goto L_0x01d1
            boolean r0 = r8.isClosed()
            if (r0 != 0) goto L_0x01d1
            r8.close()
            goto L_0x01d1
        L_0x019d:
            r0 = move-exception
            r17 = r8
            goto L_0x01bf
        L_0x01a1:
            r0 = move-exception
            goto L_0x01d4
        L_0x01a3:
            r0 = move-exception
            r17 = r8
            goto L_0x01bd
        L_0x01a7:
            if (r8 == 0) goto L_0x01ac
            r8.close()     // Catch:{ Exception -> 0x01a3, all -> 0x01a1 }
        L_0x01ac:
            if (r8 == 0) goto L_0x01b7
            boolean r0 = r8.isClosed()
            if (r0 != 0) goto L_0x01b7
            r8.close()
        L_0x01b7:
            return r18
        L_0x01b8:
            r0 = move-exception
            r8 = r17
            goto L_0x01d4
        L_0x01bc:
            r0 = move-exception
        L_0x01bd:
            r21 = 0
        L_0x01bf:
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x01b8 }
            com.sec.android.app.voicenote.provider.Log.m22e(r6, r0)     // Catch:{ all -> 0x01b8 }
            if (r17 == 0) goto L_0x01d1
            boolean r0 = r17.isClosed()
            if (r0 != 0) goto L_0x01d1
            r17.close()
        L_0x01d1:
            long r21 = r21 + r18
            return r21
        L_0x01d4:
            if (r8 == 0) goto L_0x01df
            boolean r2 = r8.isClosed()
            if (r2 != 0) goto L_0x01df
            r8.close()
        L_0x01df:
            throw r0
        L_0x01e0:
            r2 = 0
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.provider.DBProvider.findFileIndexByPrefix(java.lang.String):long");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0132, code lost:
        if (r6.isClosed() == false) goto L_0x0134;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0134, code lost:
        r6.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x015a, code lost:
        if (r6.isClosed() == false) goto L_0x0134;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private long findSimpleFileIndexByPrefix(java.lang.String r17) {
        /*
            r16 = this;
            r1 = r16
            r0 = r17
            java.lang.String r2 = "DBProvider"
            r3 = 1
            if (r0 == 0) goto L_0x016b
            android.content.Context r5 = r1.mAppContext
            boolean r5 = com.sec.android.app.voicenote.provider.PermissionProvider.isStorageAccessEnable(r5)
            if (r5 != 0) goto L_0x0014
            goto L_0x016b
        L_0x0014:
            java.lang.String r5 = "title"
            java.lang.String r6 = "cast(substr(title, ?, 1000) as INTEGER) idx"
            java.lang.String[] r9 = new java.lang.String[]{r5, r6}
            java.lang.String r6 = "substr(title, ?, 1000) idx"
            java.lang.String[] r12 = new java.lang.String[]{r5, r6}
            java.lang.String r13 = "title like ? AND (_data LIKE '%.amr' or (_data LIKE '%.m4a' and recordingtype == '1'))"
            r6 = 2
            java.lang.String[] r14 = new java.lang.String[r6]
            java.util.Locale r6 = java.util.Locale.US
            r8 = 1
            java.lang.Object[] r7 = new java.lang.Object[r8]
            int r10 = r17.length()
            int r10 = r10 + r8
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r11 = 0
            r7[r11] = r10
            java.lang.String r10 = "%d"
            java.lang.String r6 = java.lang.String.format(r6, r10, r7)
            r14[r11] = r6
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r0)
            java.lang.String r7 = "_%"
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r14[r8] = r6
            java.lang.String r15 = "idx desc limit 1"
            r6 = 0
            android.content.Context r7 = r1.mAppContext     // Catch:{ Exception -> 0x014b }
            android.content.res.Resources r7 = r7.getResources()     // Catch:{ Exception -> 0x014b }
            android.content.res.Configuration r7 = r7.getConfiguration()     // Catch:{ Exception -> 0x014b }
            int r7 = r7.getLayoutDirection()     // Catch:{ Exception -> 0x014b }
            if (r7 != r8) goto L_0x0074
            android.content.Context r7 = r1.mAppContext     // Catch:{ Exception -> 0x014b }
            android.content.ContentResolver r10 = r7.getContentResolver()     // Catch:{ Exception -> 0x014b }
            android.net.Uri r11 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x014b }
            android.database.Cursor r6 = r10.query(r11, r12, r13, r14, r15)     // Catch:{ Exception -> 0x014b }
            r13 = r8
            goto L_0x0086
        L_0x0074:
            android.content.Context r7 = r1.mAppContext     // Catch:{ Exception -> 0x014b }
            android.content.ContentResolver r7 = r7.getContentResolver()     // Catch:{ Exception -> 0x014b }
            android.net.Uri r10 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x014b }
            r12 = r8
            r8 = r10
            r10 = r13
            r11 = r14
            r13 = r12
            r12 = r15
            android.database.Cursor r6 = r7.query(r8, r9, r10, r11, r12)     // Catch:{ Exception -> 0x014b }
        L_0x0086:
            if (r6 == 0) goto L_0x0138
            boolean r7 = r6.isClosed()     // Catch:{ Exception -> 0x014b }
            if (r7 != 0) goto L_0x0138
            int r7 = r6.getCount()     // Catch:{ Exception -> 0x014b }
            if (r7 != 0) goto L_0x0096
            goto L_0x0138
        L_0x0096:
            r6.moveToFirst()     // Catch:{ Exception -> 0x014b }
            android.content.Context r7 = r1.mAppContext     // Catch:{ Exception -> 0x014b }
            android.content.res.Resources r7 = r7.getResources()     // Catch:{ Exception -> 0x014b }
            android.content.res.Configuration r7 = r7.getConfiguration()     // Catch:{ Exception -> 0x014b }
            int r7 = r7.getLayoutDirection()     // Catch:{ Exception -> 0x014b }
            java.lang.String r8 = "idx"
            if (r7 != r13) goto L_0x00c0
            int r7 = r6.getColumnIndex(r8)     // Catch:{ Exception -> 0x014b }
            java.lang.String r7 = r6.getString(r7)     // Catch:{ Exception -> 0x014b }
            java.lang.String r7 = r7.trim()     // Catch:{ Exception -> 0x014b }
            java.lang.String r7 = arabicToDecimal(r7)     // Catch:{ Exception -> 0x014b }
            long r7 = java.lang.Long.parseLong(r7)     // Catch:{ Exception -> 0x014b }
            goto L_0x00c8
        L_0x00c0:
            int r7 = r6.getColumnIndex(r8)     // Catch:{ Exception -> 0x014b }
            long r7 = r6.getLong(r7)     // Catch:{ Exception -> 0x014b }
        L_0x00c8:
            r9 = 9223372036854775707(0x7fffffffffffff9b, double:NaN)
            int r9 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r9 < 0) goto L_0x012c
            int r5 = r6.getColumnIndex(r5)     // Catch:{ Exception -> 0x012a }
            java.lang.String r5 = r6.getString(r5)     // Catch:{ Exception -> 0x012a }
            int r9 = r17.length()     // Catch:{ Exception -> 0x012a }
            int r10 = r17.length()     // Catch:{ Exception -> 0x012a }
            int r10 = r10 + 3
            java.lang.String r5 = r5.substring(r9, r10)     // Catch:{ Exception -> 0x012a }
            java.lang.Long r5 = java.lang.Long.valueOf(r5)     // Catch:{ NumberFormatException -> 0x0123 }
            long r9 = r5.longValue()     // Catch:{ NumberFormatException -> 0x0123 }
        L_0x00ef:
            long r9 = r9 + r3
            r11 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            int r5 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r5 >= 0) goto L_0x012c
            java.lang.String r5 = r1.findFileName(r0, r9)     // Catch:{ NumberFormatException -> 0x0123 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ NumberFormatException -> 0x0123 }
            r11.<init>()     // Catch:{ NumberFormatException -> 0x0123 }
            java.lang.String r12 = com.sec.android.app.voicenote.provider.SimpleStorageProvider.getVoiceRecorderPath()     // Catch:{ NumberFormatException -> 0x0123 }
            r11.append(r12)     // Catch:{ NumberFormatException -> 0x0123 }
            r12 = 47
            r11.append(r12)     // Catch:{ NumberFormatException -> 0x0123 }
            r11.append(r5)     // Catch:{ NumberFormatException -> 0x0123 }
            java.lang.String r5 = ".m4a"
            r11.append(r5)     // Catch:{ NumberFormatException -> 0x0123 }
            java.lang.String r5 = r11.toString()     // Catch:{ NumberFormatException -> 0x0123 }
            boolean r5 = com.sec.android.app.voicenote.provider.SimpleStorageProvider.isExistFile((java.lang.String) r5)     // Catch:{ NumberFormatException -> 0x0123 }
            if (r5 != 0) goto L_0x00ef
            long r9 = r9 - r3
            r7 = r9
            goto L_0x012c
        L_0x0123:
            r0 = move-exception
            java.lang.String r5 = "Test!! NumberFormatException"
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r2, (java.lang.String) r5, (java.lang.Throwable) r0)     // Catch:{ Exception -> 0x012a }
            goto L_0x012c
        L_0x012a:
            r0 = move-exception
            goto L_0x014d
        L_0x012c:
            if (r6 == 0) goto L_0x015d
            boolean r0 = r6.isClosed()
            if (r0 != 0) goto L_0x015d
        L_0x0134:
            r6.close()
            goto L_0x015d
        L_0x0138:
            if (r6 == 0) goto L_0x013d
            r6.close()     // Catch:{ Exception -> 0x014b }
        L_0x013d:
            if (r6 == 0) goto L_0x0148
            boolean r0 = r6.isClosed()
            if (r0 != 0) goto L_0x0148
            r6.close()
        L_0x0148:
            return r3
        L_0x0149:
            r0 = move-exception
            goto L_0x015f
        L_0x014b:
            r0 = move-exception
            r7 = r3
        L_0x014d:
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0149 }
            com.sec.android.app.voicenote.provider.Log.m22e(r2, r0)     // Catch:{ all -> 0x0149 }
            if (r6 == 0) goto L_0x015d
            boolean r0 = r6.isClosed()
            if (r0 != 0) goto L_0x015d
            goto L_0x0134
        L_0x015d:
            long r7 = r7 + r3
            return r7
        L_0x015f:
            if (r6 == 0) goto L_0x016a
            boolean r2 = r6.isClosed()
            if (r2 != 0) goto L_0x016a
            r6.close()
        L_0x016a:
            throw r0
        L_0x016b:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.provider.DBProvider.findSimpleFileIndexByPrefix(java.lang.String):long");
    }

    private static String arabicToDecimal(String str) {
        int i = 0;
        char[] cArr = new char[str.length()];
        for (int i2 = 0; i2 < str.length(); i2++) {
            char charAt = str.charAt(i2);
            if (charAt < 1632 || charAt > 1641) {
                if (charAt >= 1776 && charAt <= 1785) {
                    i = charAt - 1728;
                }
                cArr[i2] = charAt;
            } else {
                i = charAt - 1584;
            }
            charAt = (char) i;
            cArr[i2] = charAt;
        }
        return new String(cArr);
    }

    private String findFileName(String str, long j) {
        return String.format(Locale.getDefault(), "%1$s%2$03d", new Object[]{str, Long.valueOf(j)});
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0043, code lost:
        if (r0 == null) goto L_0x0046;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0046, code lost:
        return r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0033, code lost:
        if (r0 != null) goto L_0x0035;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0035, code lost:
        r0.close();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isSameFileInLibrary(java.lang.String r8) {
        /*
            r7 = this;
            com.sec.android.app.voicenote.provider.CursorProvider r0 = com.sec.android.app.voicenote.provider.CursorProvider.getInstance()
            java.lang.StringBuilder r0 = r0.getAllFilesQuery()
            java.lang.String r1 = " and (title == '"
            r0.append(r1)
            r0.append(r8)
            java.lang.String r8 = "' COLLATE NOCASE)"
            r0.append(r8)
            java.lang.String r4 = r0.toString()
            r8 = 0
            r0 = 0
            android.content.Context r1 = r7.mAppContext     // Catch:{ Exception -> 0x003b }
            android.content.ContentResolver r1 = r1.getContentResolver()     // Catch:{ Exception -> 0x003b }
            android.net.Uri r2 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x003b }
            r3 = 0
            r5 = 0
            r6 = 0
            android.database.Cursor r0 = r1.query(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x003b }
            if (r0 == 0) goto L_0x0033
            int r1 = r0.getCount()     // Catch:{ Exception -> 0x003b }
            if (r1 <= 0) goto L_0x0033
            r8 = 1
        L_0x0033:
            if (r0 == 0) goto L_0x0046
        L_0x0035:
            r0.close()
            goto L_0x0046
        L_0x0039:
            r8 = move-exception
            goto L_0x0047
        L_0x003b:
            r1 = move-exception
            java.lang.String r2 = "DBProvider"
            java.lang.String r3 = "Exception "
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r2, (java.lang.String) r3, (java.lang.Throwable) r1)     // Catch:{ all -> 0x0039 }
            if (r0 == 0) goto L_0x0046
            goto L_0x0035
        L_0x0046:
            return r8
        L_0x0047:
            if (r0 == 0) goto L_0x004c
            r0.close()
        L_0x004c:
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.provider.DBProvider.isSameFileInLibrary(java.lang.String):boolean");
    }

    public String getPathById(long j) {
        String str = null;
        if (!PermissionProvider.isStorageAccessEnable(this.mAppContext)) {
            return null;
        }
        Cursor query = this.mAppContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, (String[]) null, "_id=" + j, (String[]) null, (String) null);
        if (query != null) {
            if (query.moveToFirst()) {
                str = StorageProvider.convertToSDCardWritablePath(query.getString(query.getColumnIndex("_data")));
            }
            query.close();
        }
        Log.m19d(TAG, "getPathById - id : " + j + " title : " + str);
        return str;
    }

    public long getIdByPath(String str) {
        String convertToSDCardReadOnlyPath = StorageProvider.convertToSDCardReadOnlyPath(str);
        long j = -1;
        if (convertToSDCardReadOnlyPath != null && PermissionProvider.isStorageAccessEnable(this.mAppContext)) {
            Cursor query = this.mAppContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, (String[]) null, "_data = ?", new String[]{convertToSDCardReadOnlyPath}, (String) null);
            if (query != null) {
                if (query.moveToFirst()) {
                    j = query.getLong(query.getColumnIndex(CategoryRepository.LabelColumn.f102ID));
                }
                query.close();
            }
            Log.m19d(TAG, "getIdByPath - path : " + convertToSDCardReadOnlyPath + " id : " + j);
        }
        return j;
    }

    public long getLabelIdByPath(String str) {
        Cursor query;
        Throwable th = null;
        long j = -1;
        if (str != null && PermissionProvider.isStorageAccessEnable(this.mAppContext)) {
            String convertToSDCardReadOnlyPath = StorageProvider.convertToSDCardReadOnlyPath(str);
            try {
                query = this.mAppContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{DialogFactory.BUNDLE_LABEL_ID}, "_data = ?", new String[]{convertToSDCardReadOnlyPath}, (String) null);
                if (query != null) {
                    if (query.moveToFirst()) {
                        j = query.getLong(query.getColumnIndex(DialogFactory.BUNDLE_LABEL_ID));
                    }
                }
                if (query != null) {
                    query.close();
                }
            } catch (Exception e) {
                Log.m22e(TAG, "getLabelIdByPath - Exception : " + e.toString());
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            Log.m19d(TAG, "getLabelIdByPath - path : " + convertToSDCardReadOnlyPath + " labelID : " + j);
        }
        return j;
//        throw th;
    }

    public int getRecordModeByPath(String str) {
        M4aInfo readFile;
        int i = 1;
        if (str == null || str.isEmpty() || !PermissionProvider.isStorageAccessEnable(this.mAppContext)) {
            return 1;
        }
        Cursor query = this.mAppContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"recording_mode"}, "_data = ?", new String[]{StorageProvider.convertToSDCardReadOnlyPath(str)}, (String) null);
        if (query != null) {
            if (query.moveToFirst()) {
                i = query.getInt(query.getColumnIndex("recording_mode"));
            }
            query.close();
        }
        if (i != 0 || (readFile = new M4aReader(str).readFile()) == null || !readFile.hasCustomAtom.get(M4aConsts.STTD).booleanValue()) {
            return i;
        }
        updateRecordingModeInMediaDB(getIdByPath(StorageProvider.convertToSDCardReadOnlyPath(str)), 4);
        return 4;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0068, code lost:
        if (r3.isClosed() == false) goto L_0x007f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x007d, code lost:
        if (r3.isClosed() == false) goto L_0x007f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x007f, code lost:
        r3.close();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isCallHistoryFile(long r12) {
        /*
            r11 = this;
            java.lang.String r0 = "label_id"
            java.lang.String r1 = "recorded_number"
            r2 = 0
            r3 = 0
            android.content.Context r4 = r11.mAppContext     // Catch:{ Exception -> 0x006d }
            android.content.ContentResolver r5 = r4.getContentResolver()     // Catch:{ Exception -> 0x006d }
            android.net.Uri r6 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x006d }
            java.lang.String[] r7 = new java.lang.String[]{r1, r0}     // Catch:{ Exception -> 0x006d }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x006d }
            r4.<init>()     // Catch:{ Exception -> 0x006d }
            java.lang.String r8 = "_id = "
            r4.append(r8)     // Catch:{ Exception -> 0x006d }
            r4.append(r12)     // Catch:{ Exception -> 0x006d }
            java.lang.String r8 = r4.toString()     // Catch:{ Exception -> 0x006d }
            r9 = 0
            r10 = 0
            android.database.Cursor r3 = r5.query(r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x006d }
            if (r3 == 0) goto L_0x0062
            r3.moveToFirst()     // Catch:{ Exception -> 0x006d }
            int r12 = r3.getColumnIndex(r1)     // Catch:{ Exception -> 0x006d }
            java.lang.String r12 = r3.getString(r12)     // Catch:{ Exception -> 0x006d }
            int r13 = r3.getColumnIndex(r0)     // Catch:{ Exception -> 0x006d }
            int r13 = r3.getInt(r13)     // Catch:{ Exception -> 0x006d }
            r0 = 1
            if (r12 == 0) goto L_0x0051
            if (r13 != 0) goto L_0x0044
            goto L_0x0045
        L_0x0044:
            r0 = r2
        L_0x0045:
            if (r3 == 0) goto L_0x0050
            boolean r12 = r3.isClosed()
            if (r12 != 0) goto L_0x0050
            r3.close()
        L_0x0050:
            return r0
        L_0x0051:
            r12 = 3
            if (r13 != r12) goto L_0x0055
            goto L_0x0056
        L_0x0055:
            r0 = r2
        L_0x0056:
            if (r3 == 0) goto L_0x0061
            boolean r12 = r3.isClosed()
            if (r12 != 0) goto L_0x0061
            r3.close()
        L_0x0061:
            return r0
        L_0x0062:
            if (r3 == 0) goto L_0x0082
            boolean r12 = r3.isClosed()
            if (r12 != 0) goto L_0x0082
            goto L_0x007f
        L_0x006b:
            r12 = move-exception
            goto L_0x0083
        L_0x006d:
            r12 = move-exception
            java.lang.String r13 = "DBProvider"
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x006b }
            com.sec.android.app.voicenote.provider.Log.m22e(r13, r12)     // Catch:{ all -> 0x006b }
            if (r3 == 0) goto L_0x0082
            boolean r12 = r3.isClosed()
            if (r12 != 0) goto L_0x0082
        L_0x007f:
            r3.close()
        L_0x0082:
            return r2
        L_0x0083:
            if (r3 == 0) goto L_0x008e
            boolean r13 = r3.isClosed()
            if (r13 != 0) goto L_0x008e
            r3.close()
        L_0x008e:
            throw r12
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.provider.DBProvider.isCallHistoryFile(long):boolean");
    }

    public Uri getContentURI(long j) {
        Cursor cursor;
        int i = 0;
        try {
            cursor = this.mAppContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, (String[]) null, "_id=" + j, (String[]) null, (String) null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        i = cursor.getInt(cursor.getColumnIndex(CategoryRepository.LabelColumn.f102ID));
                    }
                    cursor.close();
                } catch (Exception unused) {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                    return null;
                }
            }
            return Uri.parse("content://media/external/audio/media/" + i);
        } catch (Exception unused2) {
            cursor = null;
            cursor.close();
            return null;
        }
    }

    public String getFileName(long j) {
        Cursor query = this.mAppContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, (String[]) null, "_id=" + j, (String[]) null, (String) null);
        String str = null;
        if (query != null) {
            if (query.moveToFirst()) {
                str = query.getString(query.getColumnIndex("title"));
            }
            query.close();
        }
        return str;
    }

    public int getRecordModeById(long j) {
        int i = 1;
        if (!PermissionProvider.isStorageAccessEnable(this.mAppContext)) {
            return 1;
        }
        Cursor query = this.mAppContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"recording_mode"}, "_id=" + j, (String[]) null, (String) null);
        if (query != null) {
            if (query.moveToFirst()) {
                i = query.getInt(query.getColumnIndex("recording_mode"));
            }
            query.close();
        }
        return i;
    }

    public long getFileDuration(long j) {
        Cursor query = this.mAppContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, (String[]) null, "_id=" + j, (String[]) null, (String) null);
        long j2 = 0;
        if (query != null) {
            if (query.moveToFirst()) {
                j2 = query.getLong(query.getColumnIndex("duration"));
            }
            query.close();
        }
        return j2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0046, code lost:
        if (r11 != null) goto L_0x0035;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0049, code lost:
        return r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0033, code lost:
        if (r11 != null) goto L_0x0035;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0035, code lost:
        r11.close();
     */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x004d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getContentMimeType(long r10) {
        /*
            r9 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "_id="
            r0.append(r1)
            r0.append(r10)
            java.lang.String r5 = r0.toString()
            r10 = 0
            android.content.Context r11 = r9.mAppContext     // Catch:{ Exception -> 0x003e, all -> 0x0039 }
            android.content.ContentResolver r2 = r11.getContentResolver()     // Catch:{ Exception -> 0x003e, all -> 0x0039 }
            android.net.Uri r3 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x003e, all -> 0x0039 }
            r4 = 0
            r6 = 0
            r7 = 0
            android.database.Cursor r11 = r2.query(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x003e, all -> 0x0039 }
            if (r11 == 0) goto L_0x0033
            boolean r0 = r11.moveToFirst()     // Catch:{ Exception -> 0x003f }
            if (r0 == 0) goto L_0x0033
            java.lang.String r0 = "mime_type"
            int r0 = r11.getColumnIndex(r0)     // Catch:{ Exception -> 0x003f }
            java.lang.String r10 = r11.getString(r0)     // Catch:{ Exception -> 0x003f }
        L_0x0033:
            if (r11 == 0) goto L_0x0049
        L_0x0035:
            r11.close()
            goto L_0x0049
        L_0x0039:
            r11 = move-exception
            r8 = r11
            r11 = r10
            r10 = r8
            goto L_0x004b
        L_0x003e:
            r11 = r10
        L_0x003f:
            java.lang.String r0 = "DBProvider"
            java.lang.String r1 = "mimetype is null"
            com.sec.android.app.voicenote.provider.Log.m22e(r0, r1)     // Catch:{ all -> 0x004a }
            if (r11 == 0) goto L_0x0049
            goto L_0x0035
        L_0x0049:
            return r10
        L_0x004a:
            r10 = move-exception
        L_0x004b:
            if (r11 == 0) goto L_0x0050
            r11.close()
        L_0x0050:
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.provider.DBProvider.getContentMimeType(long):java.lang.String");
    }

    public void updateDateTakenInMediaDB(Context context, long j, long j2) {
        Log.m26i(TAG, "updateDateTakenInMediaDB - date : " + j2);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("datetaken", Long.valueOf(j2));
            context.getContentResolver().update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues, "_id == ?", new String[]{Long.toString(j)});
        } catch (Exception e) {
            Log.m24e(TAG, "updateDateTakenInMediaDB - fail to update date_taken", (Throwable) e);
        }
    }

    /* access modifiers changed from: package-private */
    public void updateRecordingModeInMediaDB(long j, int i) {
        Log.m26i(TAG, "updateRecordingModeInMediaDB - mode : " + i);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("recording_mode", Integer.valueOf(i));
            this.mAppContext.getContentResolver().update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues, "_id == ?", new String[]{Long.toString(j)});
        } catch (Exception unused) {
            Log.m22e(TAG, "updateRecordingModeInMediaDB - fail to update recording mode");
        }
    }

    public Uri getContentURIFromFiles(String str) {
        Cursor cursor;
        if (str != null && PermissionProvider.isStorageAccessEnable(this.mAppContext)) {
            String convertToSDCardReadOnlyPath = StorageProvider.convertToSDCardReadOnlyPath(str);
            long j = -1;
            try {
                cursor = this.mAppContext.getContentResolver().query(MediaStore.Files.getContentUri("external"), (String[]) null, "_data = ?", new String[]{convertToSDCardReadOnlyPath}, (String) null);
                if (cursor != null) {
                    try {
                        if (cursor.moveToFirst()) {
                            j = cursor.getLong(cursor.getColumnIndex(CategoryRepository.LabelColumn.f102ID));
                        }
                        cursor.close();
                    } catch (Exception unused) {
                        if (cursor != null && !cursor.isClosed()) {
                            cursor.close();
                        }
                        return null;
                    }
                }
                Uri withAppendedId = ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), j);
                Log.m19d(TAG, "getContentURIFromFiles - path : " + convertToSDCardReadOnlyPath + " contentUri : " + withAppendedId);
                return withAppendedId;
            } catch (Exception unused2) {
                cursor = null;
                cursor.close();
                return null;
            }
        }
        return null;
    }

    public long getContentExistCheckFromFiles(String str) {
        long j = -1;
        if (str != null && PermissionProvider.isStorageAccessEnable(this.mAppContext)) {
            Cursor cursor = null;
            try {
                Cursor query = this.mAppContext.getContentResolver().query(MediaStore.Files.getContentUri("external"), (String[]) null, "_data = ?", new String[]{StorageProvider.convertToSDCardReadOnlyPath(str)}, (String) null);
                if (query != null) {
                    long j2 = query.moveToFirst() ? query.getLong(query.getColumnIndex(CategoryRepository.LabelColumn.f102ID)) : -1;
                    query.close();
                    j = j2;
                }
                Log.m19d(TAG, "getContentURICheckFromFiles - id : " + j);
                return j;
            } catch (Exception unused) {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        }
        return -1;
    }

    public LongSparseArray<String> getListPathByIds(List<Long> list) {
        Cursor cursor;
        if (list == null) {
            return null;
        }
        LongSparseArray<String> longSparseArray = new LongSparseArray<>();
        String str = "_id IN " + list.toString().replace("[", "(").replace("]", ")");
        Log.m19d(TAG, str);
        try {
            cursor = this.mAppContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, (String[]) null, str, (String[]) null, (String) null);
            while (cursor != null) {
                try {
                    if (!cursor.moveToNext()) {
                        break;
                    }
                    longSparseArray.put(cursor.getLong(cursor.getColumnIndex(CategoryRepository.LabelColumn.f102ID)), StorageProvider.convertToSDCardWritablePath(cursor.getString(cursor.getColumnIndex("_data"))));
                } catch (Exception e) {
                    e = e;
                    cursor.close();
                    Log.m22e(TAG, e.toString());
                    return null;
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            return longSparseArray;
        } catch (Exception e2) {
//            e = e2;
            cursor = null;
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
//            Log.m22e(TAG, e.toString());
            return null;
        }
    }

    public LongSparseArray<TrashInfo> getListInfoByIds(List<Long> list) {
        Cursor cursor;
        if (list == null) {
            return null;
        }
        LongSparseArray<TrashInfo> longSparseArray = new LongSparseArray<>();
        String str = "_id IN " + list.toString().replace("[", "(").replace("]", ")");
        Log.m19d(TAG, str);
        long currentTimeMillis = System.currentTimeMillis();
        try {
            cursor = this.mAppContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, (String[]) null, str, (String[]) null, (String) null);
            while (cursor != null) {
                try {
                    if (!cursor.moveToNext()) {
                        break;
                    }
                    long j = cursor.getLong(cursor.getColumnIndex(CategoryRepository.LabelColumn.f102ID));
                    String string = cursor.getString(cursor.getColumnIndex("_data"));
                    String string2 = cursor.getString(cursor.getColumnIndex("title"));
                    long j2 = cursor.getLong(cursor.getColumnIndex("duration"));
                    String string3 = cursor.getString(cursor.getColumnIndex("volume_name"));
                    int i = cursor.getInt(cursor.getColumnIndex("recordingtype"));
                    int i2 = cursor.getInt(cursor.getColumnIndex("recording_mode"));
                    int i3 = cursor.getInt(cursor.getColumnIndex(DialogFactory.BUNDLE_LABEL_ID));
                    if (cursor.getString(cursor.getColumnIndex("recorded_number")) != null && i3 <= 0) {
                        i3 = 3;
                    }
                    int i4 = i3;
                    longSparseArray.put(j, new TrashInfo(string2, "", string, 0, i4, DataRepository.getInstance().getCategoryRepository().getLabelTitle(i4, this.mAppContext), i2, i, string3, j2, cursor.getString(cursor.getColumnIndex(NFCProvider.NFC_DB_KEY)), cursor.getString(cursor.getColumnIndex("mime_type")), cursor.getLong(cursor.getColumnIndex("datetaken")), cursor.getLong(cursor.getColumnIndex("date_modified")), currentTimeMillis));
                } catch (Exception e) {
                    e = e;
                    cursor.close();
                    Log.m22e(TAG, e.getMessage());
                    return null;
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            return longSparseArray;
        } catch (Exception e2) {
//            e = e2;
            cursor = null;
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
//            Log.m22e(TAG, e.getMessage());
            return null;
        }
    }

    public boolean updateCategoryIdFileFromDB(long j, long j2) {
        Log.m29v(TAG, "updateCategoryFileFromDB - id : " + j);
        String str = "_id=" + j;
        ContentValues contentValues = new ContentValues();
        contentValues.put(DialogFactory.BUNDLE_LABEL_ID, Long.valueOf(j2));
        Cursor cursor = null;
        boolean z = false;
        try {
            Cursor cursor2 = this.mAppContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, (String[]) null, str, (String[]) null, (String) null);
            if (cursor2 != null) {
                try {
                    if (cursor2.moveToFirst()) {
                        this.mAppContext.getContentResolver().update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues, "_id=" + j, (String[]) null);
                        z = true;
                    }
                } catch (NullPointerException unused) {
                    cursor = cursor2;
                    Log.m29v(TAG, "file is not exist to be updated");
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                    return false;
                } catch (SQLiteConstraintException unused2) {
                    cursor = cursor2;
                    try {
                        Log.m29v(TAG, "constraint failed.");
                        if (cursor != null && !cursor.isClosed()) {
                            cursor.close();
                        }
                        return false;
                    } catch (Throwable unused3) {
                        cursor2 = cursor;
                        if (cursor2 != null && !cursor2.isClosed()) {
                            cursor2.close();
                        }
                        return false;
                    }
                } catch (Throwable unused4) {
                    cursor2.close();
                    return false;
                }
            }
            if (cursor2 != null && !cursor2.isClosed()) {
                cursor2.close();
            }
            return z;
        } catch (NullPointerException unused5) {
            Log.m29v(TAG, "file is not exist to be updated");
            cursor.close();
            return false;
        } catch (SQLiteConstraintException unused6) {
            Log.m29v(TAG, "constraint failed.");
            cursor.close();
            return false;
        }
    }

    public List<Long> getFilesInCategory(long j) {
        Cursor query;
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        if (j == 0) {
            sb.append("(label_id = ");
            sb.append(j);
            sb.append(" and recorded_number is null)");
            sb.append(" or label_id = ");
            sb.append(-2);
        } else if (j == 3) {
            sb.append("label_id = ");
            sb.append(j);
            sb.append(" or (label_id = ");
            sb.append(0);
            sb.append(" and recorded_number is not null)");
        } else {
            sb.append("label_id = ");
            sb.append(j);
        }
        String sb2 = sb.toString();
        Log.m19d(TAG, "getFilesInCategory with selection query = " + sb2);
        try {
            query = this.mAppContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, (String[]) null, sb.toString(), (String[]) null, (String) null);
            if (query != null) {
                query.moveToFirst();
                int columnIndex = query.getColumnIndex(CategoryRepository.LabelColumn.f102ID);
                while (!query.isAfterLast()) {
                    arrayList.add(Long.valueOf(query.getLong(columnIndex)));
                    query.moveToNext();
                }
            }
            if (query != null) {
                query.close();
            }
        } catch (SQLiteException | UnsupportedOperationException e) {
            Log.m22e(TAG, "getFilesInCategory - SQLiteException :" + e);
        } catch (Throwable th) {
//            r1.addSuppressed(th);
        }
        return arrayList;
//        throw th;
    }

    public void updateFileFromDB(long j, long j2, File file) {
        ContentValues contentValues = new ContentValues();
        String name = file.getName();
        String substring = name.substring(0, name.lastIndexOf(46));
        Log.m29v(TAG, "updateFileFromDB - title : " + substring + "  id:  " + j);
        String externalSDStorageFsUuid = StorageProvider.getExternalSDStorageFsUuid(this.mAppContext);
        String convertToSDCardReadOnlyPath = StorageProvider.convertToSDCardReadOnlyPath(file.getAbsolutePath());
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        if (externalSDStorageFsUuid != null) {
            String lowerCase = convertToSDCardReadOnlyPath.toLowerCase();
            if (lowerCase.contains("storage/" + externalSDStorageFsUuid.toLowerCase() + "/")) {
                uri = Uri.parse("content://media/" + externalSDStorageFsUuid.toLowerCase() + "/audio/media");
            }
        }
        contentValues.put("_data", convertToSDCardReadOnlyPath);
        contentValues.put("title", substring);
        contentValues.put(DialogFactory.BUNDLE_LABEL_ID, Long.valueOf(j2));
        contentValues.put("_display_name", name);
        try {
            ContentResolver contentResolver = this.mAppContext.getContentResolver();
            contentResolver.update(uri, contentValues, "_id=" + j, (String[]) null);
        } catch (NullPointerException unused) {
            Log.m29v(TAG, "file is not exist to be updated");
        } catch (SQLiteConstraintException unused2) {
            Log.m29v(TAG, "constraint failed.");
        }
    }

    public void initCategoryID() {
        Log.m26i(TAG, "initCategoryID");
        new initCategoryIDTask().execute(new Long[0]);
    }

    private class initCategoryIDTask extends AsyncTask<Long, Integer, Boolean> {
        private initCategoryIDTask() {
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(Long... lArr) {
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put(DialogFactory.BUNDLE_LABEL_ID, 0);
                DBProvider.this.mAppContext.getContentResolver().update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues, "label_id > 3 AND recordingtype == '1'", (String[]) null);
                return true;
            } catch (Exception e) {
                Log.m24e(DBProvider.TAG, "Fail to update label", (Throwable) e);
                return false;
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            if (bool.booleanValue()) {
                Settings.setSettings(Settings.KEY_FIRST_LAUNCH, false);
            }
        }
    }

    public void updateCategoryID(Context context, int i, int i2) {
        Log.m26i(TAG, "updateCategoryID oldCategoryID : " + i + " newCategoryID : " + i2);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DialogFactory.BUNDLE_LABEL_ID, Integer.valueOf(i2));
            context.getContentResolver().update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues, "label_id == ? AND (_data LIKE '%.amr' or (_data LIKE '%.m4a' and recordingtype == '1'))", new String[]{Integer.toString(i)});
        } catch (Exception e) {
            Log.m24e(TAG, "Fail to update category id", (Throwable) e);
        }
    }
}
