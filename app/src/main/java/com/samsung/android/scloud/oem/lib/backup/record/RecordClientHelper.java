package com.samsung.android.scloud.oem.lib.backup.record;

import android.content.Context;
import android.util.JsonWriter;
import com.samsung.android.scloud.oem.lib.LOG;
import com.samsung.android.scloud.oem.lib.backup.IBackupClient;
import java.io.IOException;

public class RecordClientHelper {
    private static String TAG = "RecordClientHelper";
    private Context context = null;
    private JsonWriter jsonWriter = null;
    private IBackupClient.BackupProgressListener listener = null;
    private String sourceKey = null;
    private long totalCount;

    public RecordClientHelper(Context context2, String str, JsonWriter jsonWriter2, long j, IBackupClient.BackupProgressListener backupProgressListener) {
        this.jsonWriter = jsonWriter2;
        this.listener = backupProgressListener;
        this.totalCount = j;
        this.sourceKey = str;
        this.context = context2;
    }

    public RecordClientHelper(Context context2, String str, JsonWriter jsonWriter2) {
        this.jsonWriter = jsonWriter2;
        this.sourceKey = str;
        this.context = context2;
    }

    /* access modifiers changed from: protected */
    public void open() {
        String str = TAG;
        LOG.m12d(str, "[" + this.sourceKey + "] open");
        JsonWriter jsonWriter2 = this.jsonWriter;
        if (jsonWriter2 != null) {
            try {
                jsonWriter2.beginArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void release() {
        String str = TAG;
        LOG.m12d(str, "[" + this.sourceKey + "] release");
        try {
            if (this.jsonWriter != null) {
                this.jsonWriter.endArray();
                this.jsonWriter.flush();
                this.jsonWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
