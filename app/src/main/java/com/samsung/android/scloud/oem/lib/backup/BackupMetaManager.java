package com.samsung.android.scloud.oem.lib.backup;

import android.content.Context;
import android.content.SharedPreferences;
import com.samsung.android.scloud.oem.lib.LOG;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BackupMetaManager {
    private static final String TAG = "BackupMetaManager";
    private static BackupMetaManager mMetaManager;
    private SharedPreferences backupMeta = null;
    private Map<String, Boolean> cancelMap = new ConcurrentHashMap();

    public static synchronized BackupMetaManager getInstance(Context context) {
        BackupMetaManager backupMetaManager;
        synchronized (BackupMetaManager.class) {
            if (mMetaManager == null) {
                mMetaManager = new BackupMetaManager(context);
            }
            backupMetaManager = mMetaManager;
        }
        return backupMetaManager;
    }

    private BackupMetaManager(Context context) {
        this.backupMeta = context.getSharedPreferences("BackupMeta", 0);
    }

    public void setCanceled(String str, boolean z) {
        this.cancelMap.put(str, Boolean.valueOf(z));
    }

    @Deprecated
    public void setLastBackupTime(String str, long j) {
        String str2 = TAG;
        LOG.m15i(str2, "[" + str + "] setLastBackupTime: " + j);
        SharedPreferences.Editor edit = this.backupMeta.edit();
        edit.putLong(str + "_" + "LAST_BACKUP_TIME", j).apply();
    }
}
