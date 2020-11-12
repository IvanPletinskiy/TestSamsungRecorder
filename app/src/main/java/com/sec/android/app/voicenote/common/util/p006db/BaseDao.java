package com.sec.android.app.voicenote.common.util.p006db;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;
import java.util.List;

/* renamed from: com.sec.android.app.voicenote.common.util.db.BaseDao */
public interface BaseDao<T> {
    @Delete
    void delete(T t);

    @Delete
    void deleteList(List<T> list);

    @Insert(onConflict = 5)
    void insertIgnore(T t);

    @Insert(onConflict = 5)
    void insertListIgnore(List<T> list);

    @Insert(onConflict = 1)
    long[] insertListReplace(List<T> list);

    @Insert(onConflict = 1)
    long insertReplace(T t);

    @Update(onConflict = 5)
    int updateIgnore(T t);

    @Update(onConflict = 1)
    int updateListReplace(List<T> list);

    @Update(onConflict = 1)
    int updateReplace(T t);
}
