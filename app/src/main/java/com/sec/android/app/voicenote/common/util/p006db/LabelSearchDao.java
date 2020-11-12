package com.sec.android.app.voicenote.common.util.p006db;

import androidx.room.Dao;
import androidx.room.Query;
import com.sec.android.app.voicenote.common.util.LabelHistorySearchInfo;
import java.util.List;

@Dao
/* renamed from: com.sec.android.app.voicenote.common.util.db.LabelSearchDao */
public interface LabelSearchDao extends BaseDao<LabelHistorySearchInfo> {
    @Query("DELETE FROM recent_search")
    void deleteAllData();

    @Query("DELETE FROM recent_search WHERE LABEL == :label")
    int deleteLabelWithName(String str);

    @Query("SELECT * FROM recent_search ORDER BY TIME DESC")
    List<LabelHistorySearchInfo> getAllData();
}
