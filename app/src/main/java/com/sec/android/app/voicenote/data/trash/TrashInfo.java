package com.sec.android.app.voicenote.data.trash;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "trash")
public class TrashInfo {
    @ColumnInfo(name = "_id")
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int idFile;
    @ColumnInfo(name = "CATEGORY_ID")
    private int mCategoryId;
    @ColumnInfo(name = "CATEGORY_NAME")
    private String mCategoryName;
    @ColumnInfo(name = "DATE_MODIFIED")
    private long mDateModified;
    @ColumnInfo(name = "DATE_TAKEN")
    private long mDateTaken;
    @ColumnInfo(name = "DELETE_TIME")
    private long mDeleteTime;
    @ColumnInfo(name = "DURATION")
    private long mDuration;
    @ColumnInfo(name = "IS_MEMO")
    private int mIsMemo;
    @ColumnInfo(name = "MIME_TYPE")
    private String mMimeType;
    @ColumnInfo(name = "NAME")
    private String mName;
    @ColumnInfo(name = "PATH")
    private String mPath;
    @ColumnInfo(name = "RECORDING_MODE")
    private int mRecordingMode;
    @ColumnInfo(name = "RECORDING_TYPE")
    private int mRecordingType;
    @ColumnInfo(name = "RESTORE_PATH")
    private String mRestorePath;
    @ColumnInfo(name = "VOLUME_NAME")
    private String mVolumeName;
    @ColumnInfo(name = "YEAR_NAME")
    private String mYearName;

    @Ignore
    public TrashInfo() {
    }

    public TrashInfo(String str, String str2, String str3, int i, int i2, String str4, int i3, int i4, String str5, long j, String str6, String str7, long j2, long j3, long j4) {
        this.mName = str;
        this.mPath = str2;
        this.mRestorePath = str3;
        this.mIsMemo = i;
        this.mCategoryId = i2;
        this.mCategoryName = str4;
        this.mRecordingMode = i3;
        this.mRecordingType = i4;
        this.mVolumeName = str5;
        this.mDuration = j;
        this.mYearName = str6;
        this.mMimeType = str7;
        this.mDateTaken = j2;
        this.mDateModified = j3;
        this.mDeleteTime = j4;
    }

    @NonNull
    public Integer getIdFile() {
        return Integer.valueOf(this.idFile);
    }

    public void setIdFile(@NonNull Integer num) {
        this.idFile = num.intValue();
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String str) {
        this.mName = str;
    }

    public String getPath() {
        return this.mPath;
    }

    public void setPath(String str) {
        this.mPath = str;
    }

    public String getRestorePath() {
        return this.mRestorePath;
    }

    public void setRestorePath(String str) {
        this.mRestorePath = str;
    }

    public int getCategoryId() {
        return this.mCategoryId;
    }

    public void setCategoryId(int i) {
        this.mCategoryId = i;
    }

    public String getCategoryName() {
        return this.mCategoryName;
    }

    public void setCategoryName(String str) {
        this.mCategoryName = str;
    }

    public int getIsMemo() {
        return this.mIsMemo;
    }

    public void setIsMemo(int i) {
        this.mIsMemo = i;
    }

    public int getRecordingMode() {
        return this.mRecordingMode;
    }

    public void setRecordingMode(int i) {
        this.mRecordingMode = i;
    }

    public String getVolumeName() {
        return this.mVolumeName;
    }

    public void setVolumeName(String str) {
        this.mVolumeName = str;
    }

    public long getDuration() {
        return this.mDuration;
    }

    public void setDuration(long j) {
        this.mDuration = j;
    }

    public int getRecordingType() {
        return this.mRecordingType;
    }

    public void setRecordingType(int i) {
        this.mRecordingType = i;
    }

    public String getYearName() {
        return this.mYearName;
    }

    public void setYearName(String str) {
        this.mYearName = str;
    }

    public String getMimeType() {
        return this.mMimeType;
    }

    public void setMimeType(String str) {
        this.mMimeType = str;
    }

    public long getDateTaken() {
        return this.mDateTaken;
    }

    public void setDateTaken(long j) {
        this.mDateTaken = j;
    }

    public long getDateModified() {
        return this.mDateModified;
    }

    public void setDateModified(long j) {
        this.mDateModified = j;
    }

    public long getDeleteTime() {
        return this.mDeleteTime;
    }

    public void setDeleteTime(long j) {
        this.mDeleteTime = j;
    }
}
