package com.samsung.android.scloud.oem.lib.backup.record;

import android.content.Context;
import com.samsung.android.scloud.oem.lib.backup.IBackupClient;
import java.util.ArrayList;

public interface IRecordClient extends IBackupClient {
    boolean addKeyAndDate(Context context, RecordClientHelper recordClientHelper);

    boolean backupRecord(Context context, RecordClientHelper recordClientHelper, ArrayList<String> arrayList);

    boolean restoreRecord(Context context, RecordReader recordReader, long j, RecordClientHelper recordClientHelper);
}
