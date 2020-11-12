package com.sec.android.app.voicenote.common.util;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recent_search")
public class LabelHistorySearchInfo {
    @ColumnInfo(name = "LABEL")
    @NonNull
    @PrimaryKey
    private String mLabel;
    @ColumnInfo(name = "TIME")
    private long mTime;

    public LabelHistorySearchInfo(@NonNull String str, long j) {
        this.mLabel = str;
        this.mTime = j;
    }

    @NonNull
    public String getLabel() {
        return this.mLabel;
    }

    public long getTime() {
        return this.mTime;
    }
}
