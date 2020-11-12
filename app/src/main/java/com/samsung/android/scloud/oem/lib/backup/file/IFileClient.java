package com.samsung.android.scloud.oem.lib.backup.file;

import android.content.Context;
import com.samsung.android.scloud.oem.lib.backup.IBackupClient;
import java.util.ArrayList;
import org.json.JSONObject;

public interface IFileClient extends IBackupClient {
    boolean backupFileMetaAndRecord(Context context, FileClientHelper fileClientHelper);

    ArrayList<String> getFileList(Context context);

    String getRestoreFilePath(Context context, String str);

    boolean transactionBegin(JSONObject jSONObject, String str);

    boolean transactionEnd(JSONObject jSONObject, String str);
}
