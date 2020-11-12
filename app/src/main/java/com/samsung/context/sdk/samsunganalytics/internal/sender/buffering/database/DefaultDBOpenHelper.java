package com.samsung.context.sdk.samsunganalytics.internal.sender.buffering.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.samsung.context.sdk.samsunganalytics.DBOpenHelper;

public class DefaultDBOpenHelper extends SQLiteOpenHelper implements DBOpenHelper {
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
    }

    public DefaultDBOpenHelper(Context context) {
        super(context, "SamsungAnalytics.db", (SQLiteDatabase.CursorFactory) null, 1);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS logs_v2 (_id INTEGER PRIMARY KEY AUTOINCREMENT, timestamp INTEGER, logtype TEXT, data TEXT)");
    }

    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }
}
