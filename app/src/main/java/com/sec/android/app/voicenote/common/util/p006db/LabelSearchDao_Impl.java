package com.sec.android.app.voicenote.common.util.p006db;

import android.database.Cursor;

import com.sec.android.app.voicenote.common.util.LabelHistorySearchInfo;

import java.util.ArrayList;
import java.util.List;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.sqlite.db.SupportSQLiteStatement;

/* renamed from: com.sec.android.app.voicenote.common.util.db.LabelSearchDao_Impl */
public final class LabelSearchDao_Impl implements LabelSearchDao {
    private final RoomDatabase __db;
    private final EntityDeletionOrUpdateAdapter __deletionAdapterOfLabelHistorySearchInfo;
    private final EntityInsertionAdapter __insertionAdapterOfLabelHistorySearchInfo;
    private final EntityInsertionAdapter __insertionAdapterOfLabelHistorySearchInfo_1;
    private final SharedSQLiteStatement __preparedStmtOfDeleteAllData;
    private final SharedSQLiteStatement __preparedStmtOfDeleteLabelWithName;
    private final EntityDeletionOrUpdateAdapter __updateAdapterOfLabelHistorySearchInfo;

    public LabelSearchDao_Impl(RoomDatabase roomDatabase) {
        this.__db = roomDatabase;
        this.__insertionAdapterOfLabelHistorySearchInfo = new EntityInsertionAdapter<LabelHistorySearchInfo>(roomDatabase) {
            public String createQuery() {
                return "INSERT OR REPLACE INTO `recent_search`(`LABEL`,`TIME`) VALUES (?,?)";
            }

            public void bind(SupportSQLiteStatement supportSQLiteStatement, LabelHistorySearchInfo labelHistorySearchInfo) {
                if (labelHistorySearchInfo.getLabel() == null) {
                    supportSQLiteStatement.bindNull(1);
                } else {
                    supportSQLiteStatement.bindString(1, labelHistorySearchInfo.getLabel());
                }
                supportSQLiteStatement.bindLong(2, labelHistorySearchInfo.getTime());
            }
        };
        this.__insertionAdapterOfLabelHistorySearchInfo_1 = new EntityInsertionAdapter<LabelHistorySearchInfo>(roomDatabase) {
            public String createQuery() {
                return "INSERT OR IGNORE INTO `recent_search`(`LABEL`,`TIME`) VALUES (?,?)";
            }

            public void bind(SupportSQLiteStatement supportSQLiteStatement, LabelHistorySearchInfo labelHistorySearchInfo) {
                if (labelHistorySearchInfo.getLabel() == null) {
                    supportSQLiteStatement.bindNull(1);
                } else {
                    supportSQLiteStatement.bindString(1, labelHistorySearchInfo.getLabel());
                }
                supportSQLiteStatement.bindLong(2, labelHistorySearchInfo.getTime());
            }
        };
        this.__deletionAdapterOfLabelHistorySearchInfo = new EntityDeletionOrUpdateAdapter<LabelHistorySearchInfo>(roomDatabase) {
            public String createQuery() {
                return "DELETE FROM `recent_search` WHERE `LABEL` = ?";
            }

            public void bind(SupportSQLiteStatement supportSQLiteStatement, LabelHistorySearchInfo labelHistorySearchInfo) {
                if (labelHistorySearchInfo.getLabel() == null) {
                    supportSQLiteStatement.bindNull(1);
                } else {
                    supportSQLiteStatement.bindString(1, labelHistorySearchInfo.getLabel());
                }
            }
        };
        this.__updateAdapterOfLabelHistorySearchInfo = new EntityDeletionOrUpdateAdapter<LabelHistorySearchInfo>(roomDatabase) {
            public String createQuery() {
                return "UPDATE OR REPLACE `recent_search` SET `LABEL` = ?,`TIME` = ? WHERE `LABEL` = ?";
            }

            public void bind(SupportSQLiteStatement supportSQLiteStatement, LabelHistorySearchInfo labelHistorySearchInfo) {
                if (labelHistorySearchInfo.getLabel() == null) {
                    supportSQLiteStatement.bindNull(1);
                } else {
                    supportSQLiteStatement.bindString(1, labelHistorySearchInfo.getLabel());
                }
                supportSQLiteStatement.bindLong(2, labelHistorySearchInfo.getTime());
                if (labelHistorySearchInfo.getLabel() == null) {
                    supportSQLiteStatement.bindNull(3);
                } else {
                    supportSQLiteStatement.bindString(3, labelHistorySearchInfo.getLabel());
                }
            }
        };
        this.__preparedStmtOfDeleteAllData = new SharedSQLiteStatement(roomDatabase) {
            public String createQuery() {
                return "DELETE FROM recent_search";
            }
        };
        this.__preparedStmtOfDeleteLabelWithName = new SharedSQLiteStatement(roomDatabase) {
            public String createQuery() {
                return "DELETE FROM recent_search WHERE LABEL == ?";
            }
        };
    }

    public long insertReplace(LabelHistorySearchInfo labelHistorySearchInfo) {
        this.__db.beginTransaction();
        try {
            long insertAndReturnId = this.__insertionAdapterOfLabelHistorySearchInfo.insertAndReturnId(labelHistorySearchInfo);
            this.__db.setTransactionSuccessful();
            return insertAndReturnId;
        } finally {
            this.__db.endTransaction();
        }
    }

    public void insertIgnore(LabelHistorySearchInfo labelHistorySearchInfo) {
        this.__db.beginTransaction();
        try {
            this.__insertionAdapterOfLabelHistorySearchInfo_1.insert(labelHistorySearchInfo);
            this.__db.setTransactionSuccessful();
        } finally {
            this.__db.endTransaction();
        }
    }

    public long[] insertListReplace(List<LabelHistorySearchInfo> list) {
        this.__db.beginTransaction();
        try {
            long[] insertAndReturnIdsArray = this.__insertionAdapterOfLabelHistorySearchInfo.insertAndReturnIdsArray(list);
            this.__db.setTransactionSuccessful();
            return insertAndReturnIdsArray;
        } finally {
            this.__db.endTransaction();
        }
    }

    public void insertListIgnore(List<LabelHistorySearchInfo> list) {
        this.__db.beginTransaction();
        try {
            this.__insertionAdapterOfLabelHistorySearchInfo_1.insert(list);
            this.__db.setTransactionSuccessful();
        } finally {
            this.__db.endTransaction();
        }
    }

    public void delete(LabelHistorySearchInfo labelHistorySearchInfo) {
        this.__db.beginTransaction();
        try {
            this.__deletionAdapterOfLabelHistorySearchInfo.handle(labelHistorySearchInfo);
            this.__db.setTransactionSuccessful();
        } finally {
            this.__db.endTransaction();
        }
    }

    public void deleteList(List<LabelHistorySearchInfo> list) {
        this.__db.beginTransaction();
        try {
            this.__deletionAdapterOfLabelHistorySearchInfo.handleMultiple(list);
            this.__db.setTransactionSuccessful();
        } finally {
            this.__db.endTransaction();
        }
    }

    public int updateReplace(LabelHistorySearchInfo labelHistorySearchInfo) {
        this.__db.beginTransaction();
        try {
            int handle = this.__updateAdapterOfLabelHistorySearchInfo.handle(labelHistorySearchInfo) + 0;
            this.__db.setTransactionSuccessful();
            return handle;
        } finally {
            this.__db.endTransaction();
        }
    }

    public int updateIgnore(LabelHistorySearchInfo labelHistorySearchInfo) {
        this.__db.beginTransaction();
        try {
            int handle = this.__updateAdapterOfLabelHistorySearchInfo.handle(labelHistorySearchInfo) + 0;
            this.__db.setTransactionSuccessful();
            return handle;
        } finally {
            this.__db.endTransaction();
        }
    }

    public int updateListReplace(List<LabelHistorySearchInfo> list) {
        this.__db.beginTransaction();
        try {
            int handleMultiple = this.__updateAdapterOfLabelHistorySearchInfo.handleMultiple(list) + 0;
            this.__db.setTransactionSuccessful();
            return handleMultiple;
        } finally {
            this.__db.endTransaction();
        }
    }

    public void deleteAllData() {
        SupportSQLiteStatement acquire = this.__preparedStmtOfDeleteAllData.acquire();
        this.__db.beginTransaction();
        try {
            acquire.executeUpdateDelete();
            this.__db.setTransactionSuccessful();
        } finally {
            this.__db.endTransaction();
            this.__preparedStmtOfDeleteAllData.release(acquire);
        }
    }

    public int deleteLabelWithName(String str) {
        SupportSQLiteStatement acquire = this.__preparedStmtOfDeleteLabelWithName.acquire();
        this.__db.beginTransaction();
        if (str == null) {
            try {
                acquire.bindNull(1);
            } catch (Throwable th) {
                this.__db.endTransaction();
                this.__preparedStmtOfDeleteLabelWithName.release(acquire);
                throw th;
            }
        } else {
            acquire.bindString(1, str);
        }
        int executeUpdateDelete = acquire.executeUpdateDelete();
        this.__db.setTransactionSuccessful();
        this.__db.endTransaction();
        this.__preparedStmtOfDeleteLabelWithName.release(acquire);
        return executeUpdateDelete;
    }

    public List<LabelHistorySearchInfo> getAllData() {
        RoomSQLiteQuery acquire = RoomSQLiteQuery.acquire("SELECT * FROM recent_search ORDER BY TIME DESC", 0);
        Cursor query = this.__db.query(acquire);
        try {
            int columnIndexOrThrow = query.getColumnIndexOrThrow("LABEL");
            int columnIndexOrThrow2 = query.getColumnIndexOrThrow("TIME");
            ArrayList arrayList = new ArrayList(query.getCount());
            while (query.moveToNext()) {
                arrayList.add(new LabelHistorySearchInfo(query.getString(columnIndexOrThrow), query.getLong(columnIndexOrThrow2)));
            }
            return arrayList;
        } finally {
            query.close();
            acquire.release();
        }
    }
}
