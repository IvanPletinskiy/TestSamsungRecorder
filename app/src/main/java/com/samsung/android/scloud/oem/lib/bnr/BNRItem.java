package com.samsung.android.scloud.oem.lib.bnr;

public class BNRItem {
    private String data;
    private String localId;
    private long size;
    private long timestamp;

    public BNRItem(String str, String str2, long j) {
        this.localId = str;
        this.data = str2;
        setTimestamp(j);
    }

    public long getTimeStamp() {
        return this.timestamp;
    }

    public void setTimestamp(long j) {
        int length = 13 - (j + "").length();
        if (length > 0) {
            j = (long) (((double) j) * Math.pow(10.0d, (double) length));
        }
        this.timestamp = j;
    }

    public String getLocalId() {
        return this.localId;
    }

    public String getData() {
        return this.data;
    }

    public long getSize() {
        return this.size;
    }
}
