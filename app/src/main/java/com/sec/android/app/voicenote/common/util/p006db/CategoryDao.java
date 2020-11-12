package com.sec.android.app.voicenote.common.util.p006db;

import androidx.room.Dao;
import androidx.room.Query;
import com.sec.android.app.voicenote.common.util.CategoryInfo;
import java.util.List;

@Dao
/* renamed from: com.sec.android.app.voicenote.common.util.db.CategoryDao */
public interface CategoryDao extends BaseDao<CategoryInfo> {
    @Query("SELECT * FROM labels WHERE _id > :idLabel AND TITLE == :title COLLATE NOCASE")
    List<CategoryInfo> checkIsSameTitle(int i, String str);

    @Query("DELETE FROM labels")
    void deleteAllData();

    @Query("DELETE FROM labels WHERE _id = :id")
    int deleteDataWithID(int i);

    @Query("SELECT * FROM labels")
    List<CategoryInfo> getAllData();

    @Query("SELECT * FROM labels WHERE _id = :idLabel")
    CategoryInfo getCategoryFromId(int i);

    @Query("SELECT * FROM labels WHERE TITLE LIKE :title")
    CategoryInfo getCategoryFromTitle(String str);

    @Query("UPDATE labels SET TITLE =:title WHERE _id = :id")
    int upDateCategory(String str, int i);
}
