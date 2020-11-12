package com.samsung.android.scloud.oem.lib.backup;

import android.content.Context;
import android.os.Bundle;
import java.util.ArrayList;

public interface IBackupClient {

    public interface BackupProgressListener {
    }

    public interface ResultListener {
        void onError(int i);

        void onSuccess();
    }

    void backupCompleted(Context context);

    void backupFailed(Context context);

    void initialize(Context context, ResultListener resultListener);

    boolean isBackupPrepare(Context context);

    boolean isRestorePrepare(Context context, Bundle bundle);

    void restoreCompleted(Context context, ArrayList<String> arrayList);

    void restoreFailed(Context context, ArrayList<String> arrayList);
}
