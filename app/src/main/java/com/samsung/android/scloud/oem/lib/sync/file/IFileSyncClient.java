package com.samsung.android.scloud.oem.lib.sync.file;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import java.util.HashMap;

public interface IFileSyncClient {
    String createLocal(Context context, int i, String str, long j, String str2, HashMap<String, ParcelFileDescriptor> hashMap);

    boolean deleteLocal(Context context, String str);

    FileInfo getAttachmentFileInfo(Context context, int i, String str);

    String getLocalChange(Context context, int i, String str, String[] strArr, HashMap<String, ParcelFileDescriptor> hashMap);

    SyncItem getLocalItems(Context context, String[] strArr, long[] jArr, String[] strArr2, String str, String str2, boolean z);

    boolean isColdStartable(Context context);

    boolean isComplete(Context context, String str, String str2, long j, int i);

    boolean prepareToSync(Context context);

    String updateLocal(Context context, int i, String str, String str2, long j, String str3, HashMap<String, ParcelFileDescriptor> hashMap, String[] strArr);
}
