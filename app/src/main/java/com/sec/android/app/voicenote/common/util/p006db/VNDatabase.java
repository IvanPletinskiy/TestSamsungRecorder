package com.sec.android.app.voicenote.common.util.p006db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.sec.android.app.voicenote.common.util.CategoryInfo;
import com.sec.android.app.voicenote.common.util.CategoryRepository;
import com.sec.android.app.voicenote.common.util.LabelHistorySearchInfo;
import com.sec.android.app.voicenote.data.trash.TrashInfo;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.DBProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Settings;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {CategoryInfo.class, LabelHistorySearchInfo.class, TrashInfo.class}, exportSchema = false, version = 8)
/* renamed from: com.sec.android.app.voicenote.common.util.db.VNDatabase */
public abstract class VNDatabase extends RoomDatabase {
    private static final int CURRENT_VERSION = 8;
    private static final String DATABASE_NAME = "label.db";
    private static final String LABEL_SEARCH = "LABEL";
    public static final String POSITION = "POSITION";
    private static final String TAG = "VNDatabase";
    private static final String TIME_SEARCH = "TIME";
    private static final String TITLE_CATEGORY = "TITLE";
    private static final String TRASH_CATEGORY_ID = "CATEGORY_ID";
    private static final String TRASH_CATEGORY_NAME = "CATEGORY_NAME";
    private static final String TRASH_DATE_MODIFIED = "DATE_MODIFIED";
    private static final String TRASH_DATE_TAKEN = "DATE_TAKEN";
    private static final String TRASH_DELETE_TIME = "DELETE_TIME";
    private static final String TRASH_DURATION = "DURATION";
    private static final String TRASH_IS_MEMO = "IS_MEMO";
    private static final String TRASH_MIME_TYPE = "MIME_TYPE";
    private static final String TRASH_NAME = "NAME";
    private static final String TRASH_PATH = "PATH";
    private static final String TRASH_RECORDING_MODE = "RECORDING_MODE";
    private static final String TRASH_RECORDING_TYPE = "RECORDING_TYPE";
    private static final String TRASH_RESTORE_PATH = "RESTORE_PATH";
    private static final String TRASH_VOLUME_NAME = "VOLUME_NAME";
    private static final String TRASH_YEAR_NAME = "YEAR_NAME";
    private static final String _ID_CATEGORY = "_id";
    private static final String _LABELS_CATEGORY_TABLE_NAME = "labels";
    private static final String _LABELS_SEARCH_TABLE_NAME = "recent_search";
    private static final String _LABELS_TRASH_TABLE_NAME = "trash";
    private static final String _TRASH_ID = "_id";
    private static List<String> mPrevPositionData = new ArrayList();
    private static List<String> mPrevTitleData = new ArrayList();
    private static VNDatabase sInstance;

    public abstract CategoryDao mCategoryDao();

    public abstract LabelSearchDao mLabelSearchDao();

    public abstract TrashDao mTrashDao();

    public static VNDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (VNDatabase.class) {
                if (sInstance == null) {
                    sInstance = buildDatabase(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private static VNDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(context, VNDatabase.class, DATABASE_NAME).allowMainThreadQueries().addCallback(new RoomDatabase.Callback() {
            public void onCreate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
                super.onCreate(supportSQLiteDatabase);
                Log.m26i(VNDatabase.TAG, "onCreate db");
                VNDatabase.initDataCategory(supportSQLiteDatabase);
            }

            public void onOpen(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
                super.onOpen(supportSQLiteDatabase);
            }
        }).addMigrations(getListMigration(context)).build();
    }

    /* access modifiers changed from: private */
    public static void initDataCategory(SupportSQLiteDatabase supportSQLiteDatabase) {
        Log.m26i(TAG, "initDataCategory");
        supportSQLiteDatabase.execSQL("INSERT INTO labels VALUES(" + 0 + ", 'None', 0)");
        supportSQLiteDatabase.execSQL("INSERT INTO labels VALUES(" + 1 + ", 'Interview', 1)");
        supportSQLiteDatabase.execSQL("INSERT INTO labels VALUES(" + 2 + ", 'Speech-to-text', 2)");
        supportSQLiteDatabase.execSQL("INSERT INTO labels VALUES(" + 3 + ", 'Call History', 3)");
        Settings.setSettings(Settings.KEY_CATEGORY_LABEL_ID, 0);
    }

    private static Migration[] getListMigration(Context context) {
        Migration[] migrationArr = new Migration[7];
        for (int i = 1; i < 8; i++) {
            migrationArr[i - 1] = getMigrationWithVersion(context, i, 8);
        }
        return migrationArr;
    }

    private static Migration getMigrationWithVersion(final Context context, int i, int i2) {
        return new Migration(i, i2) {
            public void migrate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
                Log.m26i(VNDatabase.TAG, "migrate database - startVersion : " + this.startVersion + ", endVersion: " + this.endVersion);
                if (this.startVersion <= 7) {
                    VNDatabase.createTableTrash(supportSQLiteDatabase);
                    if (this.startVersion != 7) {
                        VNDatabase.createTableRecentSearch(supportSQLiteDatabase);
                        if (this.startVersion != 6) {
                            int access$300 = VNDatabase.savePrevData(supportSQLiteDatabase, 4);
                            supportSQLiteDatabase.execSQL("DROP TABLE IF EXISTS labels");
                            VNDatabase.createTableCategory(supportSQLiteDatabase);
                            VNDatabase.initDataCategory(supportSQLiteDatabase);
                            VNDatabase.insertPrevData(supportSQLiteDatabase, access$300);
                            VNDatabase.updateLabelIdInMediaDB(context);
                        }
                    }
                }
            }
        };
    }

    /* access modifiers changed from: private */
    public static void updateLabelIdInMediaDB(Context context) {
        Log.m19d(TAG, " updateLabelIdInMediaDB");
        Cursor query = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{DialogFactory.BUNDLE_LABEL_ID}, "label_id >= 4", (String[]) null, (String) null);
        if (query != null && query.moveToFirst()) {
            for (int i = 0; i < query.getCount(); i++) {
                int i2 = query.getInt(query.getColumnIndex(DialogFactory.BUNDLE_LABEL_ID));
                DBProvider.getInstance().updateCategoryID(context, i2, (i2 + 100) - 4);
                query.moveToNext();
            }
        }
        if (query != null) {
            query.close();
        }
    }

    /* access modifiers changed from: private */
    public static void insertPrevData(SupportSQLiteDatabase supportSQLiteDatabase, int i) {
        Log.m26i(TAG, " insertPrevData");
        for (int i2 = 0; i2 < i; i2++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CategoryRepository.LabelColumn.f102ID, Integer.valueOf(i2 + 100));
            contentValues.put("TITLE", mPrevTitleData.get(i2));
            contentValues.put("POSITION", Integer.valueOf(Integer.parseInt(mPrevPositionData.get(i2)) + 1));
            android.util.Log.d(TAG, "insertPrevData111: " + contentValues);
            long insert = supportSQLiteDatabase.insert("labels", 1, contentValues);
            if (insert <= 0) {
                Log.m22e(TAG, " error insertPrevData : " + insert);
            }
        }
    }

    /* access modifiers changed from: private */
    public static void createTableCategory(SupportSQLiteDatabase supportSQLiteDatabase) {
        supportSQLiteDatabase.execSQL("CREATE TABLE labels (_id INTEGER PRIMARY KEY AUTOINCREMENT, TITLE TEXT, POSITION INTEGER);");
    }

    /* access modifiers changed from: private */
    public static int savePrevData(SupportSQLiteDatabase supportSQLiteDatabase, int i) {
        Log.m26i(TAG, " savePrevData");
        Cursor customCategoryCursor = CursorProvider.getInstance().getCustomCategoryCursor(supportSQLiteDatabase, i);
        int i2 = 0;
        if (customCategoryCursor != null) {
            int count = customCategoryCursor.getCount();
            customCategoryCursor.moveToFirst();
            while (i2 < count) {
                mPrevTitleData.add(i2, customCategoryCursor.getString(customCategoryCursor.getColumnIndex("TITLE")));
                mPrevPositionData.add(i2, customCategoryCursor.getString(customCategoryCursor.getColumnIndex("POSITION")));
                customCategoryCursor.moveToNext();
                i2++;
            }
            i2 = count;
        }
        if (customCategoryCursor != null && !customCategoryCursor.isClosed()) {
            customCategoryCursor.close();
        }
        return i2;
    }

    /* access modifiers changed from: private */
    public static void createTableRecentSearch(SupportSQLiteDatabase supportSQLiteDatabase) {
        supportSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS recent_search (LABEL TEXT PRIMARY KEY NOT NULL, TIME INTEGER NOT NULL);");
    }

    /* access modifiers changed from: private */
    public static void createTableTrash(SupportSQLiteDatabase supportSQLiteDatabase) {
        supportSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS trash (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, NAME TEXT, PATH TEXT UNIQUE, RESTORE_PATH TEXT, CATEGORY_ID INTEGER NOT NULL, CATEGORY_NAME TEXT, IS_MEMO INTEGER NOT NULL, RECORDING_MODE INTEGER NOT NULL, RECORDING_TYPE INTEGER NOT NULL, VOLUME_NAME TEXT, DURATION INTEGER NOT NULL, YEAR_NAME TEXT, MIME_TYPE TEXT, DATE_TAKEN INTEGER NOT NULL, DATE_MODIFIED INTEGER NOT NULL, DELETE_TIME INTEGER NOT NULL);");
    }
}
