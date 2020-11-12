package com.samsung.android.scloud.oem.lib.bnr;

public class BNRFile {
    private String checksum = "";
    private String fileKey = "";
    private String hash = "";

    /* renamed from: id */
    private long f82id = 0;
    private boolean isComplete;
    private boolean isExternal;
    private String nextKey = "";
    private long offset = 0;
    private String path;
    private long size;
    private String startKey = "";
    private long timestamp;

    public BNRFile() {
    }

    public BNRFile(String str, String str2, String str3, boolean z) {
        this.path = str;
        this.startKey = str2;
        this.nextKey = str3;
        this.isComplete = z;
    }

    public BNRFile(String str, String str2, String str3, String str4, boolean z) {
        this.path = str;
        this.checksum = str2;
        this.startKey = str3;
        this.nextKey = str4;
        this.isComplete = z;
    }

    public long getTimeStamp() {
        return this.timestamp;
    }

    public String getPath() {
        return this.path;
    }

    public boolean getisExternal() {
        return this.isExternal;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long j) {
        this.size = j;
    }

    public String getStartKey() {
        return this.startKey;
    }

    public String getNextKey() {
        return this.nextKey;
    }

    public String getChecksum() {
        return this.checksum;
    }

    public void setOffset(long j) {
        this.offset = j;
    }

    public boolean isComplete() {
        return this.isComplete;
    }
}
