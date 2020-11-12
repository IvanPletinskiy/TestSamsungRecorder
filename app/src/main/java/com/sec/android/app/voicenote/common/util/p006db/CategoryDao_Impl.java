package com.sec.android.app.voicenote.common.util.p006db;

import android.database.Cursor;

import com.sec.android.app.voicenote.common.util.CategoryInfo;
import com.sec.android.app.voicenote.common.util.CategoryRepository;

import java.util.ArrayList;
import java.util.List;

import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.sqlite.db.SupportSQLiteStatement;

/* renamed from: com.sec.android.app.voicenote.common.util.db.CategoryDao_Impl */
public final class CategoryDao_Impl implements CategoryDao {
    private final RoomDatabase __db;
    private final EntityDeletionOrUpdateAdapter __deletionAdapterOfCategoryInfo;
    private final EntityInsertionAdapter __insertionAdapterOfCategoryInfo;
    private final EntityInsertionAdapter __insertionAdapterOfCategoryInfo_1;
    private final SharedSQLiteStatement __preparedStmtOfDeleteAllData;
    private final SharedSQLiteStatement __preparedStmtOfDeleteDataWithID;
    private final SharedSQLiteStatement __preparedStmtOfUpDateCategory;
    private final EntityDeletionOrUpdateAdapter __updateAdapterOfCategoryInfo;

    public CategoryDao_Impl(RoomDatabase roomDatabase) {
        this.__db = roomDatabase;
        this.__insertionAdapterOfCategoryInfo = new EntityInsertionAdapter<CategoryInfo>(roomDatabase) {
            public String createQuery() {
                return "INSERT OR REPLACE INTO `labels`(`_id`,`TITLE`,`POSITION`) VALUES (?,?,?)";
            }

            public void bind(SupportSQLiteStatement supportSQLiteStatement, CategoryInfo categoryInfo) {
                if (categoryInfo.getIdCategory() == null) {
                    supportSQLiteStatement.bindNull(1);
                } else {
                    supportSQLiteStatement.bindLong(1, (long) categoryInfo.getIdCategory().intValue());
                }
                if (categoryInfo.getTitle() == null) {
                    supportSQLiteStatement.bindNull(2);
                } else {
                    supportSQLiteStatement.bindString(2, categoryInfo.getTitle());
                }
                if (categoryInfo.getPosition() == null) {
                    supportSQLiteStatement.bindNull(3);
                } else {
                    supportSQLiteStatement.bindLong(3, (long) categoryInfo.getPosition().intValue());
                }
            }
        };
        this.__insertionAdapterOfCategoryInfo_1 = new EntityInsertionAdapter<CategoryInfo>(roomDatabase) {
            public String createQuery() {
                return "INSERT OR IGNORE INTO `labels`(`_id`,`TITLE`,`POSITION`) VALUES (?,?,?)";
            }

            public void bind(SupportSQLiteStatement supportSQLiteStatement, CategoryInfo categoryInfo) {
                if (categoryInfo.getIdCategory() == null) {
                    supportSQLiteStatement.bindNull(1);
                } else {
                    supportSQLiteStatement.bindLong(1, (long) categoryInfo.getIdCategory().intValue());
                }
                if (categoryInfo.getTitle() == null) {
                    supportSQLiteStatement.bindNull(2);
                } else {
                    supportSQLiteStatement.bindString(2, categoryInfo.getTitle());
                }
                if (categoryInfo.getPosition() == null) {
                    supportSQLiteStatement.bindNull(3);
                } else {
                    supportSQLiteStatement.bindLong(3, (long) categoryInfo.getPosition().intValue());
                }
            }
        };
        this.__deletionAdapterOfCategoryInfo = new EntityDeletionOrUpdateAdapter<CategoryInfo>(roomDatabase) {
            public String createQuery() {
                return "DELETE FROM `labels` WHERE `_id` = ?";
            }

            public void bind(SupportSQLiteStatement supportSQLiteStatement, CategoryInfo categoryInfo) {
                if (categoryInfo.getIdCategory() == null) {
                    supportSQLiteStatement.bindNull(1);
                } else {
                    supportSQLiteStatement.bindLong(1, (long) categoryInfo.getIdCategory().intValue());
                }
            }
        };
        this.__updateAdapterOfCategoryInfo = new EntityDeletionOrUpdateAdapter<CategoryInfo>(roomDatabase) {
            public String createQuery() {
                return "UPDATE OR REPLACE `labels` SET `_id` = ?,`TITLE` = ?,`POSITION` = ? WHERE `_id` = ?";
            }

            public void bind(SupportSQLiteStatement supportSQLiteStatement, CategoryInfo categoryInfo) {
                if (categoryInfo.getIdCategory() == null) {
                    supportSQLiteStatement.bindNull(1);
                } else {
                    supportSQLiteStatement.bindLong(1, (long) categoryInfo.getIdCategory().intValue());
                }
                if (categoryInfo.getTitle() == null) {
                    supportSQLiteStatement.bindNull(2);
                } else {
                    supportSQLiteStatement.bindString(2, categoryInfo.getTitle());
                }
                if (categoryInfo.getPosition() == null) {
                    supportSQLiteStatement.bindNull(3);
                } else {
                    supportSQLiteStatement.bindLong(3, (long) categoryInfo.getPosition().intValue());
                }
                if (categoryInfo.getIdCategory() == null) {
                    supportSQLiteStatement.bindNull(4);
                } else {
                    supportSQLiteStatement.bindLong(4, (long) categoryInfo.getIdCategory().intValue());
                }
            }
        };
        this.__preparedStmtOfDeleteAllData = new SharedSQLiteStatement(roomDatabase) {
            public String createQuery() {
                return "DELETE FROM labels";
            }
        };
        this.__preparedStmtOfDeleteDataWithID = new SharedSQLiteStatement(roomDatabase) {
            public String createQuery() {
                return "DELETE FROM labels WHERE _id = ?";
            }
        };
        this.__preparedStmtOfUpDateCategory = new SharedSQLiteStatement(roomDatabase) {
            public String createQuery() {
                return "UPDATE labels SET TITLE =? WHERE _id = ?";
            }
        };
    }

    public long insertReplace(CategoryInfo categoryInfo) {
        this.__db.beginTransaction();
        try {
            long insertAndReturnId = this.__insertionAdapterOfCategoryInfo.insertAndReturnId(categoryInfo);
            this.__db.setTransactionSuccessful();
            return insertAndReturnId;
        } finally {
            this.__db.endTransaction();
        }
    }

    public void insertIgnore(CategoryInfo categoryInfo) {
        this.__db.beginTransaction();
        try {
            this.__insertionAdapterOfCategoryInfo_1.insert(categoryInfo);
            this.__db.setTransactionSuccessful();
        } finally {
            this.__db.endTransaction();
        }
    }

    public long[] insertListReplace(List<CategoryInfo> list) {
        this.__db.beginTransaction();
        try {
            long[] insertAndReturnIdsArray = this.__insertionAdapterOfCategoryInfo.insertAndReturnIdsArray(list);
            this.__db.setTransactionSuccessful();
            return insertAndReturnIdsArray;
        } finally {
            this.__db.endTransaction();
        }
    }

    public void insertListIgnore(List<CategoryInfo> list) {
        this.__db.beginTransaction();
        try {
            this.__insertionAdapterOfCategoryInfo_1.insert(list);
            this.__db.setTransactionSuccessful();
        } finally {
            this.__db.endTransaction();
        }
    }

    public void delete(CategoryInfo categoryInfo) {
        this.__db.beginTransaction();
        try {
            this.__deletionAdapterOfCategoryInfo.handle(categoryInfo);
            this.__db.setTransactionSuccessful();
        } finally {
            this.__db.endTransaction();
        }
    }

    public void deleteList(List<CategoryInfo> list) {
        this.__db.beginTransaction();
        try {
            this.__deletionAdapterOfCategoryInfo.handleMultiple(list);
            this.__db.setTransactionSuccessful();
        } finally {
            this.__db.endTransaction();
        }
    }

    public int updateReplace(CategoryInfo categoryInfo) {
        this.__db.beginTransaction();
        try {
            int handle = this.__updateAdapterOfCategoryInfo.handle(categoryInfo) + 0;
            this.__db.setTransactionSuccessful();
            return handle;
        } finally {
            this.__db.endTransaction();
        }
    }

    public int updateIgnore(CategoryInfo categoryInfo) {
        this.__db.beginTransaction();
        try {
            int handle = this.__updateAdapterOfCategoryInfo.handle(categoryInfo) + 0;
            this.__db.setTransactionSuccessful();
            return handle;
        } finally {
            this.__db.endTransaction();
        }
    }

    public int updateListReplace(List<CategoryInfo> list) {
        this.__db.beginTransaction();
        try {
            int handleMultiple = this.__updateAdapterOfCategoryInfo.handleMultiple(list) + 0;
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

    public int deleteDataWithID(int i) {
        SupportSQLiteStatement acquire = this.__preparedStmtOfDeleteDataWithID.acquire();
        this.__db.beginTransaction();
        try {
            acquire.bindLong(1, (long) i);
            int executeUpdateDelete = acquire.executeUpdateDelete();
            this.__db.setTransactionSuccessful();
            return executeUpdateDelete;
        } finally {
            this.__db.endTransaction();
            this.__preparedStmtOfDeleteDataWithID.release(acquire);
        }
    }

    public int upDateCategory(String str, int i) {
        SupportSQLiteStatement acquire = this.__preparedStmtOfUpDateCategory.acquire();
        this.__db.beginTransaction();
        if (str == null) {
            try {
                acquire.bindNull(1);
            } catch (Throwable th) {
                this.__db.endTransaction();
                this.__preparedStmtOfUpDateCategory.release(acquire);
                throw th;
            }
        } else {
            acquire.bindString(1, str);
        }
        acquire.bindLong(2, (long) i);
        int executeUpdateDelete = acquire.executeUpdateDelete();
        this.__db.setTransactionSuccessful();
        this.__db.endTransaction();
        this.__preparedStmtOfUpDateCategory.release(acquire);
        return executeUpdateDelete;
    }

    public List<CategoryInfo> getAllData() {
        Integer num;
        RoomSQLiteQuery acquire = RoomSQLiteQuery.acquire("SELECT * FROM labels", 0);
        Cursor query = this.__db.query(acquire);
        try {
            int columnIndexOrThrow = query.getColumnIndexOrThrow(CategoryRepository.LabelColumn.f102ID);
            int columnIndexOrThrow2 = query.getColumnIndexOrThrow(CategoryRepository.LabelColumn.TITLE);
            int columnIndexOrThrow3 = query.getColumnIndexOrThrow("POSITION");
            ArrayList arrayList = new ArrayList(query.getCount());
            while (query.moveToNext()) {
                CategoryInfo categoryInfo = new CategoryInfo();
                Integer num2 = null;
                if (query.isNull(columnIndexOrThrow)) {
                    num = null;
                } else {
                    num = Integer.valueOf(query.getInt(columnIndexOrThrow));
                }
                categoryInfo.setIdCategory(num.intValue());
                categoryInfo.setTitle(query.getString(columnIndexOrThrow2));
                if (!query.isNull(columnIndexOrThrow3)) {
                    num2 = Integer.valueOf(query.getInt(columnIndexOrThrow3));
                }
                categoryInfo.setPosition(num2.intValue());
                arrayList.add(categoryInfo);
            }
            return arrayList;
        } finally {
            query.close();
            acquire.release();
        }
    }

    public CategoryInfo getCategoryFromTitle(String str) {
        CategoryInfo categoryInfo;
        Integer num;
        RoomSQLiteQuery acquire = RoomSQLiteQuery.acquire("SELECT * FROM labels WHERE TITLE LIKE ?", 1);
        if (str == null) {
            acquire.bindNull(1);
        } else {
            acquire.bindString(1, str);
        }
        Cursor query = this.__db.query(acquire);
        try {
            int columnIndexOrThrow = query.getColumnIndexOrThrow(CategoryRepository.LabelColumn.f102ID);
            int columnIndexOrThrow2 = query.getColumnIndexOrThrow(CategoryRepository.LabelColumn.TITLE);
            int columnIndexOrThrow3 = query.getColumnIndexOrThrow("POSITION");
            Integer num2 = null;
            if (query.moveToFirst()) {
                categoryInfo = new CategoryInfo();
                if (query.isNull(columnIndexOrThrow)) {
                    num = null;
                } else {
                    num = Integer.valueOf(query.getInt(columnIndexOrThrow));
                }
                categoryInfo.setIdCategory(num.intValue());
                categoryInfo.setTitle(query.getString(columnIndexOrThrow2));
                if (!query.isNull(columnIndexOrThrow3)) {
                    num2 = Integer.valueOf(query.getInt(columnIndexOrThrow3));
                }
                categoryInfo.setPosition(num2.intValue());
            } else {
                categoryInfo = null;
            }
            return categoryInfo;
        } finally {
            query.close();
            acquire.release();
        }
    }

    public CategoryInfo getCategoryFromId(int i) {
        CategoryInfo categoryInfo;
        Integer num;
        RoomSQLiteQuery acquire = RoomSQLiteQuery.acquire("SELECT * FROM labels WHERE _id = ?", 1);
        acquire.bindLong(1, (long) i);
        Cursor query = this.__db.query(acquire);
        try {
            int columnIndexOrThrow = query.getColumnIndexOrThrow(CategoryRepository.LabelColumn.f102ID);
            int columnIndexOrThrow2 = query.getColumnIndexOrThrow(CategoryRepository.LabelColumn.TITLE);
            int columnIndexOrThrow3 = query.getColumnIndexOrThrow("POSITION");
            Integer num2 = null;
            if (query.moveToFirst()) {
                categoryInfo = new CategoryInfo();
                if (query.isNull(columnIndexOrThrow)) {
                    num = null;
                } else {
                    num = Integer.valueOf(query.getInt(columnIndexOrThrow));
                }
                categoryInfo.setIdCategory(num.intValue());
                categoryInfo.setTitle(query.getString(columnIndexOrThrow2));
                if (!query.isNull(columnIndexOrThrow3)) {
                    num2 = Integer.valueOf(query.getInt(columnIndexOrThrow3));
                }
                categoryInfo.setPosition(num2.intValue());
            } else {
                categoryInfo = null;
            }
            return categoryInfo;
        } finally {
            query.close();
            acquire.release();
        }
    }

    public List<CategoryInfo> checkIsSameTitle(int i, String str) {
        Integer num;
        RoomSQLiteQuery acquire = RoomSQLiteQuery.acquire("SELECT * FROM labels WHERE _id > ? AND TITLE == ? COLLATE NOCASE", 2);
        acquire.bindLong(1, (long) i);
        if (str == null) {
            acquire.bindNull(2);
        } else {
            acquire.bindString(2, str);
        }
        Cursor query = this.__db.query(acquire);
        try {
            int columnIndexOrThrow = query.getColumnIndexOrThrow(CategoryRepository.LabelColumn.f102ID);
            int columnIndexOrThrow2 = query.getColumnIndexOrThrow(CategoryRepository.LabelColumn.TITLE);
            int columnIndexOrThrow3 = query.getColumnIndexOrThrow("POSITION");
            ArrayList arrayList = new ArrayList(query.getCount());
            while (query.moveToNext()) {
                CategoryInfo categoryInfo = new CategoryInfo();
                Integer num2 = null;
                if (query.isNull(columnIndexOrThrow)) {
                    num = null;
                } else {
                    num = Integer.valueOf(query.getInt(columnIndexOrThrow));
                }
                categoryInfo.setIdCategory(num.intValue());
                categoryInfo.setTitle(query.getString(columnIndexOrThrow2));
                if (!query.isNull(columnIndexOrThrow3)) {
                    num2 = Integer.valueOf(query.getInt(columnIndexOrThrow3));
                }
                categoryInfo.setPosition(num2.intValue());
                arrayList.add(categoryInfo);
            }
            return arrayList;
        } finally {
            query.close();
            acquire.release();
        }
    }
}
