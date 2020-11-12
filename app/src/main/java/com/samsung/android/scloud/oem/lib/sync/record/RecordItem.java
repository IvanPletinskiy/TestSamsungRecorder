package com.samsung.android.scloud.oem.lib.sync.record;

import java.util.UUID;

public class RecordItem {
    private boolean deleted;
    private boolean isNew = false;
    private long localRecordId;
    private String serverRecordId;
    private String tableName;
    private long timeStamp;

    public RecordItem(long j, String str, long j2, boolean z, String str2) {
        this.localRecordId = j;
        if (str == null) {
            this.isNew = true;
            this.serverRecordId = generateServerRecordId();
        } else {
            this.serverRecordId = str;
        }
        this.timeStamp = j2;
        this.deleted = z;
        this.tableName = str2;
    }

    public long getLocalRecordId() {
        return this.localRecordId;
    }

    public String getServerRecordId() {
        return this.serverRecordId;
    }

    public String getTableName() {
        return this.tableName;
    }

    public long getTimestamp() {
        return this.timeStamp;
    }

    public boolean isDeleted() {
        return this.deleted;
    }

    private String generateServerRecordId() {
        return UUID.randomUUID().toString();
    }
}
