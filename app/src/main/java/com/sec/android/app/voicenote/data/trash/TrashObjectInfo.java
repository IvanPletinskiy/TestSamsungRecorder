package com.sec.android.app.voicenote.data.trash;

public class TrashObjectInfo {

    /* renamed from: id */
    private long f104id;
    private String path;
    private int status;
    private TrashInfo trashInfo;

    public TrashObjectInfo(long j, TrashInfo trashInfo2, int i) {
        this.trashInfo = trashInfo2;
        this.f104id = j;
        this.status = i;
    }

    public TrashObjectInfo(long j, String str, int i) {
        this.path = str;
        this.f104id = j;
        this.status = i;
    }

    public long getId() {
        return this.f104id;
    }

    public void setId(long j) {
        this.f104id = j;
    }

    public TrashInfo getTrashInfo() {
        return this.trashInfo;
    }

    public void setTrashInfo(TrashInfo trashInfo2) {
        this.trashInfo = trashInfo2;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String str) {
        this.path = str;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int i) {
        this.status = i;
    }
}
