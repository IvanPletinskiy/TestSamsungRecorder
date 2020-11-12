package com.samsung.android.scloud.oem.lib.backup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.samsung.android.scloud.oem.lib.LOG;
import com.samsung.android.scloud.oem.lib.bnr.BNRFile;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import java.util.ArrayList;
import java.util.List;

public class ReuseDBHelper extends SQLiteOpenHelper {
    private static ReuseDBHelper INSTANCE = null;
    private static String TAG = "ReuseDBHelper";
    private Context mContext = null;

    public static synchronized ReuseDBHelper getInstance(Context context) {
        ReuseDBHelper reuseDBHelper;
        synchronized (ReuseDBHelper.class) {
            if (INSTANCE == null) {
                INSTANCE = new ReuseDBHelper(context, "backup.db", (SQLiteDatabase.CursorFactory) null, 1);
            }
            reuseDBHelper = INSTANCE;
        }
        return reuseDBHelper;
    }

    public void onOpen(SQLiteDatabase sQLiteDatabase) {
        super.onOpen(sQLiteDatabase);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        LOG.m15i(TAG, "create TABLE if not exists~! ");
        sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS reuse_files(_id INTEGER PRIMARY KEY AUTOINCREMENT, sourcekey TEXT NOT NULL, path TEXT UNIQUE NOT NULL, checksum TEXT, offset INTEGER DEFAULT 0, start_key TEXT, next_key TEXT, size INTEGER DEFAULT 0, complete INTEGER DEFAULT 0 );");
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        if (i < i2) {
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS reuse_files");
            onCreate(sQLiteDatabase);
        }
    }

    public Cursor query(String[] strArr, String str, String[] strArr2, String str2, String str3, String str4) {
        return getReadableDatabase().query("reuse_files", strArr, str, strArr2, str2, str3, str4);
    }

    public int update(ContentValues contentValues, String str, String[] strArr) {
        return getWritableDatabase().update("reuse_files", contentValues, str, strArr);
    }

    private ReuseDBHelper(Context context, String str, SQLiteDatabase.CursorFactory cursorFactory, int i) {
        super(context, str, cursorFactory, i);
        this.mContext = context;
    }

    public long addReuseFile(String str, BNRFile bNRFile) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("sourcekey", str);
        contentValues.put(DialogFactory.BUNDLE_PATH, bNRFile.getPath());
        contentValues.put("complete", Integer.valueOf(bNRFile.isComplete() ? 1 : 0));
        if (!"".equals(bNRFile.getChecksum())) {
            contentValues.put("checksum", bNRFile.getChecksum());
        }
        if (!"".equals(bNRFile.getStartKey())) {
            contentValues.put("start_key", bNRFile.getStartKey());
        }
        if (!"".equals(bNRFile.getNextKey())) {
            contentValues.put("next_key", bNRFile.getNextKey());
        }
        if (bNRFile.getSize() > 0) {
            contentValues.put("size", Long.valueOf(bNRFile.getSize()));
        }
        if (bNRFile.getSize() > 0) {
            contentValues.put("offset", Long.valueOf(bNRFile.getSize()));
        }
        String str2 = TAG;
        LOG.m15i(str2, "addReuseFile, CV : " + contentValues.toString());
        return getWritableDatabase().insertWithOnConflict("reuse_files", DialogFactory.BUNDLE_PATH, contentValues, 5);
    }

    public ArrayList<String> getReusePathList(String str) {
        ArrayList<String> arrayList = new ArrayList<>();
        SQLiteDatabase writableDatabase = getWritableDatabase();
        String[] strArr = {DialogFactory.BUNDLE_PATH};
        Cursor query = writableDatabase.query("reuse_files", strArr, "sourcekey = '" + str + "'", (String[]) null, (String) null, (String) null, "_id ASC");
        while (query.moveToNext()) {
            arrayList.add(query.getString(query.getColumnIndex(DialogFactory.BUNDLE_PATH)));
        }
        String str2 = TAG;
        LOG.m12d(str2, "getReusePathList, pathList : " + arrayList.toString());
        query.close();
        return arrayList;
    }

    public void clearReuseFile(String str) {
        String str2 = TAG;
        LOG.m15i(str2, "clearRestoreFileDB() is called~!, " + str);
        SQLiteDatabase readableDatabase = getReadableDatabase();
        readableDatabase.delete("reuse_files", "sourcekey = '" + str + "'", (String[]) null);
    }

    public void removeReuseFile(List<String> list) {
        if (list.size() != 0) {
            String str = TAG;
            LOG.m15i(str, "removeReuseFile() is called~!, " + list.size());
            getReadableDatabase().delete("reuse_files", "path IN (?)", (String[]) list.toArray(new String[list.size()]));
        }
    }
}
