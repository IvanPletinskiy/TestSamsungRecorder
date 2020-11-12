package com.sec.android.app.voicenote.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.sec.android.app.voicenote.common.util.DataRepository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class CategoryDBProvider extends ContentProvider {
    private static final String TAG = "CategoryDBProvider";
    static final String _TABLENAME = "labels";
    private SupportSQLiteDatabase mDB;

    public int delete(@NonNull Uri uri, String str, String[] strArr) {
        return 0;
    }

    @Nullable
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        return null;
    }

    public boolean onCreate() {
        return false;
    }

    public int update(@NonNull Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }

    public Cursor query(@NonNull Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        Log.m19d(TAG, "query Category DB");
        if (this.mDB == null) {
            this.mDB = DataRepository.getInstance().getVNDatabase().getOpenHelper().getWritableDatabase();
        }
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        sQLiteQueryBuilder.setTables("labels");
        Cursor query = this.mDB.query(sQLiteQueryBuilder.buildQuery(strArr, str, (String) null, (String) null, str2, (String) null), (Object[]) strArr2);
        if (!(query == null || getContext() == null)) {
            query.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return query;
    }
}
