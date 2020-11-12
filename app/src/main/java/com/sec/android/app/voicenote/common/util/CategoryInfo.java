package com.sec.android.app.voicenote.common.util;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "labels")
public class CategoryInfo implements Cloneable {
    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    private Integer idCategory;
    @ColumnInfo(name = "POSITION")
    private Integer mPosition;
    @ColumnInfo(name = "TITLE")
    private String mTitle;

    @Ignore
    public CategoryInfo(String str) {
        this.mTitle = str;
    }

    @Ignore
    public CategoryInfo(String str, int i) {
        this.mTitle = str;
        this.mPosition = Integer.valueOf(i);
    }

    @Ignore
    public CategoryInfo(int i, String str, int i2) {
        this.idCategory = Integer.valueOf(i);
        this.mTitle = str;
        this.mPosition = Integer.valueOf(i2);
    }

    public CategoryInfo() {
    }

    public Integer getIdCategory() {
        return this.idCategory;
    }

    public void setIdCategory(int i) {
        this.idCategory = Integer.valueOf(i);
    }

    public String getTitle() {
        return this.mTitle;
    }

    public void setTitle(String str) {
        this.mTitle = str;
    }

    public Integer getPosition() {
        return this.mPosition;
    }

    public void setPosition(int i) {
        this.mPosition = Integer.valueOf(i);
    }

    public CategoryInfo clone() {
        try {
            return (CategoryInfo) super.clone();
        } catch (CloneNotSupportedException unused) {
            return new CategoryInfo(this.idCategory.intValue(), this.mTitle, this.mPosition.intValue());
        }
    }
}
