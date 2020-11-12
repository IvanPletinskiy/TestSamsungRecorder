package com.sec.android.app.voicenote.common.util.p006db;

import androidx.room.Dao;
import androidx.room.Query;
import com.sec.android.app.voicenote.data.trash.TrashInfo;
import java.util.List;

@Dao
/* renamed from: com.sec.android.app.voicenote.common.util.db.TrashDao */
public interface TrashDao extends BaseDao<TrashInfo> {
    @Query("DELETE FROM trash")
    void deleteAllData();

    @Query("DELETE FROM trash WHERE VOLUME_NAME = 'external_primary' OR VOLUME_NAME = :volumeName")
    void deleteAllData(String str);

    @Query("DELETE FROM trash WHERE _id = :id")
    int deleteDataWithID(int i);

    @Query("SELECT * FROM trash WHERE VOLUME_NAME = 'external_primary' ORDER BY DELETE_TIME DESC, DATE_TAKEN DESC")
    List<TrashInfo> getAllData();

    @Query("SELECT * FROM trash WHERE VOLUME_NAME = 'external_primary' OR VOLUME_NAME = :volumeName ORDER BY DELETE_TIME DESC, DATE_TAKEN DESC")
    List<TrashInfo> getAllData(String str);

    @Query("SELECT * FROM trash WHERE _id IN (:arrId)")
    List<TrashInfo> getDataWithListId(List<Long> list);

    @Query("SELECT COUNT(*) FROM trash WHERE VOLUME_NAME = 'external_primary'")
    int numberItem();

    @Query("SELECT COUNT(*) FROM trash WHERE VOLUME_NAME = 'external_primary' OR VOLUME_NAME = :volumeName")
    int numberItem(String str);

    @Query("SELECT * FROM trash WHERE PATH = :path")
    TrashInfo trashInfoWithPath(String str);

    @Query("SELECT * FROM trash WHERE PATH = :path")
    List<TrashInfo> trashListWithPath(String str);
}
