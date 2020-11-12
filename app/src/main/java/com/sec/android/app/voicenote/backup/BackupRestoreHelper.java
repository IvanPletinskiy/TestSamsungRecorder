package com.sec.android.app.voicenote.backup;

import android.content.ContentValues;
import android.content.Context;
import android.provider.MediaStore;
import android.util.SparseIntArray;
import com.sec.android.app.voicenote.common.util.CategoryRepository;
import com.sec.android.app.voicenote.common.util.DataRepository;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Settings;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BackupRestoreHelper {
    private static final String[] BACKUP_COLUMN_PROJECTION = {CategoryRepository.LabelColumn.f102ID, "_size", "duration", "datetaken", DialogFactory.BUNDLE_LABEL_ID};
    private static final String BACKUP_KEY_CALL_REJECT = "key_call_reject";
    private static final String BACKUP_KEY_CATEGORIES_TABLE = "key_categories_table";
    private static final String BACKUP_KEY_MEDIA_ITEM = "key_media_item";
    private static final String BACKUP_KEY_PLAY_CONTINUOUSLY = "key_play_continuously";
    private static final String BACKUP_KEY_REC_QUALITY = "key_rec_quality";
    private static final String BACKUP_KEY_REC_STEREO = "key_rec_stereo";
    private static final String BACKUP_KEY_SETTINGS = "key_settings";
    private static final int INDEX_DATETAKEN = 2;
    private static final int INDEX_DURATION = 1;
    private static final int INDEX_ID = 4;
    private static final int INDEX_LABEL_ID = 3;
    private static final int INDEX_SIZE = 0;
    private static final String TAG = "BackupRestoreHelper";
    private static SparseIntArray mLabelIdMap;
    private static Map<String, Integer> mReceiverCategory;
    private static Map<String, Integer> mSenderCategory;

    public static String makeBackupDataJSon(Context context) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(BACKUP_KEY_SETTINGS, makeBackupSetting(context));
            jSONObject.put(BACKUP_KEY_CATEGORIES_TABLE, makeBackupCategoryTable(context));
            jSONObject.put(BACKUP_KEY_MEDIA_ITEM, makeBackupMediaItem(context));
        } catch (JSONException e) {
            Log.m22e(TAG, "make backup data failed: " + e.toString());
        }
        return jSONObject.toString();
    }

    private static JSONObject makeBackupSetting(Context context) {
        JSONObject jSONObject = new JSONObject();
        try {
            Settings.setApplicationContext(new WeakReference(context));
            boolean booleanSettings = Settings.getBooleanSettings(Settings.KEY_REC_CALL_REJECT, false);
            jSONObject.put(BACKUP_KEY_CALL_REJECT, booleanSettings);
            Log.m26i(TAG, "backup BACKUP_KEY_CALL_REJECT: " + booleanSettings);
            boolean booleanSettings2 = Settings.getBooleanSettings(Settings.KEY_PLAY_CONTINUOUSLY, false);
            jSONObject.put(BACKUP_KEY_PLAY_CONTINUOUSLY, booleanSettings2);
            Log.m26i(TAG, "backup BACKUP_KEY_PLAY_CONTINUOUSLY: " + booleanSettings2);
            int intSettings = Settings.getIntSettings(Settings.KEY_REC_QUALITY, 1);
            jSONObject.put(BACKUP_KEY_REC_QUALITY, intSettings);
            Log.m26i(TAG, "backup BACKUP_KEY_REC_QUALITY: " + intSettings);
            boolean booleanSettings3 = Settings.getBooleanSettings(Settings.KEY_REC_STEREO, false);
            jSONObject.put(BACKUP_KEY_REC_STEREO, booleanSettings3);
            Log.m26i(TAG, "backup BACKUP_KEY_REC_STEREO: " + booleanSettings3);
        } catch (Exception e) {
            Log.m22e(TAG, "make backup setting json failed: " + e.toString());
        }
        return jSONObject;
    }

    private static JSONObject makeBackupCategoryTable(Context context) {
        JSONObject jSONObject = new JSONObject();
        try {
            mSenderCategory = DataRepository.getInstance().getCategoryRepository().getAllUserCategory();
            for (Map.Entry next : mSenderCategory.entrySet()) {
                jSONObject.put((String) next.getKey(), next.getValue());
            }
        } catch (Exception e) {
            Log.m22e(TAG, "make backup categories json failed: " + e.toString());
        }
        return jSONObject;
    }

    private static JSONObject makeBackupMediaItem(Context context) {
        JSONObject jSONObject = new JSONObject();
        try {
            Iterator<String[]> it = getUserCategoryMediaItems(context).iterator();
            while (it.hasNext()) {
                String[] next = it.next();
                jSONObject.put(next[4], new JSONArray(new String[]{next[0], next[1], next[2], next[3]}));
            }
        } catch (Exception e) {
            Log.m22e(TAG, "make backup media item json failed: " + e.toString());
        }
        return jSONObject;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0077, code lost:
        if (r8.isClosed() == false) goto L_0x008e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x008c, code lost:
        if (r8.isClosed() == false) goto L_0x008e;
     */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0088  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.util.ArrayList<java.lang.String[]> getUserCategoryMediaItems(android.content.Context r8) {
        /*
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            android.content.ContentResolver r1 = r8.getContentResolver()
            android.net.Uri r2 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            java.lang.String[] r3 = BACKUP_COLUMN_PROJECTION
            java.lang.String r4 = "label_id >= '100'"
            r5 = 0
            r6 = 0
            android.database.Cursor r8 = r1.query(r2, r3, r4, r5, r6)
            if (r8 == 0) goto L_0x0086
            r8.moveToFirst()     // Catch:{ Exception -> 0x006a }
        L_0x001a:
            java.lang.String r1 = "_size"
            int r1 = r8.getColumnIndex(r1)     // Catch:{ Exception -> 0x006a }
            java.lang.String r1 = r8.getString(r1)     // Catch:{ Exception -> 0x006a }
            java.lang.String r2 = "duration"
            int r2 = r8.getColumnIndex(r2)     // Catch:{ Exception -> 0x006a }
            java.lang.String r2 = r8.getString(r2)     // Catch:{ Exception -> 0x006a }
            java.lang.String r3 = "datetaken"
            int r3 = r8.getColumnIndex(r3)     // Catch:{ Exception -> 0x006a }
            java.lang.String r3 = r8.getString(r3)     // Catch:{ Exception -> 0x006a }
            java.lang.String r4 = "label_id"
            int r4 = r8.getColumnIndex(r4)     // Catch:{ Exception -> 0x006a }
            java.lang.String r4 = r8.getString(r4)     // Catch:{ Exception -> 0x006a }
            java.lang.String r5 = "_id"
            int r5 = r8.getColumnIndex(r5)     // Catch:{ Exception -> 0x006a }
            java.lang.String r5 = r8.getString(r5)     // Catch:{ Exception -> 0x006a }
            r6 = 5
            java.lang.String[] r6 = new java.lang.String[r6]     // Catch:{ Exception -> 0x006a }
            r7 = 0
            r6[r7] = r1     // Catch:{ Exception -> 0x006a }
            r1 = 1
            r6[r1] = r2     // Catch:{ Exception -> 0x006a }
            r1 = 2
            r6[r1] = r3     // Catch:{ Exception -> 0x006a }
            r1 = 3
            r6[r1] = r4     // Catch:{ Exception -> 0x006a }
            r1 = 4
            r6[r1] = r5     // Catch:{ Exception -> 0x006a }
            r0.add(r6)     // Catch:{ Exception -> 0x006a }
            boolean r1 = r8.moveToNext()     // Catch:{ Exception -> 0x006a }
            if (r1 != 0) goto L_0x001a
            goto L_0x0086
        L_0x0068:
            r0 = move-exception
            goto L_0x007a
        L_0x006a:
            java.lang.String r1 = "BackupRestoreHelper"
            java.lang.String r2 = "fail to get user items"
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r2)     // Catch:{ all -> 0x0068 }
            if (r8 == 0) goto L_0x0091
            boolean r1 = r8.isClosed()
            if (r1 != 0) goto L_0x0091
            goto L_0x008e
        L_0x007a:
            if (r8 == 0) goto L_0x0085
            boolean r1 = r8.isClosed()
            if (r1 != 0) goto L_0x0085
            r8.close()
        L_0x0085:
            throw r0
        L_0x0086:
            if (r8 == 0) goto L_0x0091
            boolean r1 = r8.isClosed()
            if (r1 != 0) goto L_0x0091
        L_0x008e:
            r8.close()
        L_0x0091:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.backup.BackupRestoreHelper.getUserCategoryMediaItems(android.content.Context):java.util.ArrayList");
    }

    public static void restoreJsonBackupData(Context context, String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            restoreBackupSetting(context, jSONObject.getJSONObject(BACKUP_KEY_SETTINGS));
            restoreBackupCategoryTable(context, jSONObject.getJSONObject(BACKUP_KEY_CATEGORIES_TABLE));
            restoreBackupMediaItem(context, jSONObject.getJSONObject(BACKUP_KEY_MEDIA_ITEM));
        } catch (JSONException e) {
            Log.m22e(TAG, "restore backup json failed: " + e.toString());
        }
    }

    private static void restoreBackupSetting(Context context, JSONObject jSONObject) {
        try {
            Settings.setApplicationContext(new WeakReference(context));
            boolean z = jSONObject.getBoolean(BACKUP_KEY_CALL_REJECT);
            Settings.setSettings(Settings.KEY_REC_CALL_REJECT, z);
            Log.m26i(TAG, "restore BACKUP_KEY_CALL_REJECT: " + z);
            boolean z2 = jSONObject.getBoolean(BACKUP_KEY_PLAY_CONTINUOUSLY);
            Settings.setSettings(Settings.KEY_PLAY_CONTINUOUSLY, z2);
            Log.m26i(TAG, "restore BACKUP_KEY_PLAY_CONTINUOUSLY: " + z2);
            int i = jSONObject.getInt(BACKUP_KEY_REC_QUALITY);
            Settings.setSettings(Settings.KEY_REC_QUALITY, i);
            Log.m26i(TAG, "restore BACKUP_KEY_REC_QUALITY: " + i);
            boolean z3 = jSONObject.getBoolean(BACKUP_KEY_REC_STEREO);
            Settings.setSettings(Settings.KEY_REC_STEREO, z3);
            Log.m26i(TAG, "restore BACKUP_KEY_REC_STEREO: " + z3);
        } catch (Exception e) {
            Log.m22e(TAG, "restore backup setting failed: " + e.toString());
        }
    }

    private static void restoreBackupCategoryTable(Context context, JSONObject jSONObject) {
        try {
            mSenderCategory = new HashMap();
            Iterator<String> keys = jSONObject.keys();
            while (keys.hasNext()) {
                String next = keys.next();
                mSenderCategory.put(next, Integer.valueOf(jSONObject.getInt(next)));
            }
            mReceiverCategory = DataRepository.getInstance().getCategoryRepository().getAllUserCategory();
            insertSenderCategoryToReceiver();
        } catch (JSONException e) {
            Log.m22e(TAG, "restore backup category failed: " + e.toString());
        }
    }

    private static void insertSenderCategoryToReceiver() {
        mLabelIdMap = new SparseIntArray();
        int maxCategoryPos = CursorProvider.getInstance().getMaxCategoryPos();
        for (Map.Entry next : mSenderCategory.entrySet()) {
            String str = (String) next.getKey();
            Integer num = (Integer) next.getValue();
            Integer num2 = mReceiverCategory.get(str);
            if (num2 == null) {
                maxCategoryPos++;
                num2 = Integer.valueOf(insertColumn(str, maxCategoryPos));
            }
            mLabelIdMap.put(num.intValue(), num2.intValue());
            Log.m26i(TAG, "insertSenderCategoryToReceiver (title:" + str + " LabelId:" + num + " => " + num2 + ")");
        }
    }

    private static int insertColumn(String str, int i) {
        return (int) DataRepository.getInstance().getCategoryRepository().insertColumnFromBackup(str, i);
    }

    private static void restoreBackupMediaItem(Context context, JSONObject jSONObject) {
        try {
            Iterator<String> keys = jSONObject.keys();
            while (keys.hasNext()) {
                JSONArray jSONArray = jSONObject.getJSONArray(keys.next());
                updateLabelId(context, jSONArray.getString(0), jSONArray.getString(1), jSONArray.getString(2), jSONArray.getString(3));
            }
        } catch (JSONException e) {
            Log.m22e(TAG, "restore backup category failed: " + e.toString());
        }
    }

    private static void updateLabelId(Context context, String str, String str2, String str3, String str4) {
        int i = mLabelIdMap.get(Integer.parseInt(str4));
        ContentValues contentValues = new ContentValues();
        contentValues.put(DialogFactory.BUNDLE_LABEL_ID, Integer.valueOf(i));
        int update = context.getContentResolver().update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues, "_size == ? and duration == ? and datetaken == ? and label_id == ?", new String[]{str, str2, str3, str4});
        Log.m26i(TAG, "updateLabelID (size:" + str + " duration:" + str2 + " date:" + str3 + " updatedCount:" + update + " labelID:" + str4 + " => " + i + ")");
    }
}
