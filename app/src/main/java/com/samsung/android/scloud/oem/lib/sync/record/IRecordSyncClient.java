package com.samsung.android.scloud.oem.lib.sync.record;

import java.util.List;
import org.json.JSONObject;

public interface IRecordSyncClient {
    void complete(String str, long j, boolean z);

    boolean createRecord(String str, JSONObject jSONObject);

    void deleteRecord(String str, long j);

    long getLastSyncTime();

    List<RecordItem> getLocalRecordList(String[] strArr, String str, String str2, boolean z);

    String getModifiedTimeName();

    JSONObject getRecord(String str, long j);

    boolean isColdStartable();

    boolean ready();

    void setLastSyncTime(long j);

    boolean updateRecord(String str, JSONObject jSONObject, long j);
}
