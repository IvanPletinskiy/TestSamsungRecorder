package com.sec.android.app.voicenote.backup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.StorageProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

public class SmartSwitchReceiver extends BroadcastReceiver {
    private static final int ACTION_CANCEL = 2;
    private static final int ACTION_START = 0;
    private static final String BACKUP_FILENAME = "VoiceRecorder.json";
    private static final int ERR_INVALID_DATA = 3;
    private static final int ERR_PERMISSION = 4;
    private static final int ERR_STORAGE_FULL = 2;
    private static final int ERR_SUCCESS = 0;
    private static final int ERR_UNKNOWN = 1;
    private static final String REQUEST_BACKUP = "com.samsung.android.intent.action.REQUEST_BACKUP_VOICERECORDER";
    private static final String REQUEST_RESTORE = "com.samsung.android.intent.action.REQUEST_RESTORE_VOICERECORDER";
    private static final String RESPONSE_BACKUP = "com.samsung.android.intent.action.RESPONSE_BACKUP_VOICERECORDER";
    private static final String RESPONSE_RESTORE = "com.samsung.android.intent.action.RESPONSE_RESTORE_VOICERECORDER";
    private static final int RESULT_FAIL = 1;
    private static final int RESULT_OK = 0;
    private static final String TAG = "SmartSwitchReceiver";
    /* access modifiers changed from: private */
    public static final LinkedList<BackupRestoreTask> mPendingTasks = new LinkedList<>();
    private static final String[] permissions = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};

    private enum Operation {
        BACKUP,
        RESTORE
    }

    private static class BackupRestoreRequest {
        public int action;
        public Context context;
        public String key;
        public Operation operation;
        public String path;
        public String source;

        private BackupRestoreRequest() {
        }

        /* synthetic */ BackupRestoreRequest(C07071 r1) {
            this();
        }
    }

    private static class BackupRestoreResult {
        public Context context;
        public Operation operation;
        public int size;
        public String source;
        public int status;

        private BackupRestoreResult() {
        }

        /* synthetic */ BackupRestoreResult(C07071 r1) {
            this();
        }
    }

    public void onReceive(Context context, Intent intent) {
        Operation operation;
        Log.m26i(TAG, "onReceive SmartSwitch Intent");
        if (context == null || intent == null) {
            Log.m22e(TAG, "Invalid context or intent");
            return;
        }
        String action = intent.getAction();
        if (action == null) {
            Log.m22e(TAG, "Received intent with no action");
        } else if (action.equals(REQUEST_BACKUP) || action.equals(REQUEST_RESTORE)) {
            if (action.equals(REQUEST_BACKUP)) {
                operation = Operation.BACKUP;
            } else {
                operation = action.equals(REQUEST_RESTORE) ? Operation.RESTORE : null;
            }
            Operation operation2 = operation;
            String stringExtra = intent.getStringExtra("SAVE_PATH");
            int intExtra = intent.getIntExtra("ACTION", -1);
            String stringExtra2 = intent.getStringExtra("SESSION_KEY");
            String stringExtra3 = intent.getStringExtra("SOURCE");
            if (stringExtra == null || intExtra == -1 || stringExtra2 == null || stringExtra3 == null) {
                Log.m22e(TAG, "Received malformed request");
                sendResponse(context, operation2, 1, 1, 0, stringExtra3);
            } else if (!permissionCheck(context)) {
                sendResponse(context, operation2, 1, 4, 0, stringExtra3);
            } else {
                processRequest(context, operation2, stringExtra, intExtra, stringExtra2, stringExtra3);
            }
        } else {
            Log.m22e(TAG, "Received intent with unsupported action " + action);
        }
    }

    private boolean permissionCheck(Context context) {
        for (String checkSelfPermission : permissions) {
            if (context.checkSelfPermission(checkSelfPermission) != 0) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public static void sendResponse(Context context, Operation operation, int i, int i2, int i3, String str) {
        String str2 = operation == Operation.BACKUP ? RESPONSE_BACKUP : operation == Operation.RESTORE ? RESPONSE_RESTORE : null;
        Intent intent = new Intent(str2);
        intent.putExtra("RESULT", i);
        intent.putExtra("ERR_CODE", i2);
        intent.putExtra("REQ_SIZE", i3);
        intent.putExtra("SOURCE", str);
        Log.m26i(TAG, "sendResponse (Action:" + str2 + " RESULT:" + i + " ERR_CODE:" + i2 + " REQ_SIZE:" + i3 + " SOURCE:" + str + ")");
        context.sendBroadcast(intent);
    }

    private static void processRequest(Context context, Operation operation, String str, int i, String str2, String str3) {
        if (i == 0) {
            BackupRestoreRequest backupRestoreRequest = new BackupRestoreRequest((C07071) null);
            backupRestoreRequest.context = context;
            backupRestoreRequest.operation = operation;
            backupRestoreRequest.path = str;
            backupRestoreRequest.action = i;
            backupRestoreRequest.key = str2;
            backupRestoreRequest.source = str3;
            new BackupRestoreTask((C07071) null).execute(new BackupRestoreRequest[]{backupRestoreRequest});
        } else if (i == 2) {
            synchronized (mPendingTasks) {
                while (true) {
                    BackupRestoreTask poll = mPendingTasks.poll();
                    if (poll != null) {
                        poll.cancel(false);
                    }
                }
            }
        }
    }

    private static class BackupRestoreTask extends AsyncTask<BackupRestoreRequest, Void, BackupRestoreResult> {
        private BackupRestoreTask() {
        }

        /* synthetic */ BackupRestoreTask(C07071 r1) {
            this();
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            super.onPreExecute();
            synchronized (SmartSwitchReceiver.mPendingTasks) {
                SmartSwitchReceiver.mPendingTasks.add(this);
            }
        }

        /* access modifiers changed from: protected */
        public BackupRestoreResult doInBackground(BackupRestoreRequest[] backupRestoreRequestArr) {
            FileOutputStream fileOutputStream;
            FileInputStream fileInputStream;
            byte[] bArr = null;
            if (backupRestoreRequestArr == null || backupRestoreRequestArr.length == 0) {
                Log.m22e(SmartSwitchReceiver.TAG, "Invalid backup/restore request passed to AsyncTask");
                return null;
            }
            BackupRestoreRequest backupRestoreRequest = backupRestoreRequestArr[0];
            BackupRestoreResult backupRestoreResult = new BackupRestoreResult((C07071) null);
            backupRestoreResult.context = backupRestoreRequest.context;
            backupRestoreResult.operation = backupRestoreRequest.operation;
            backupRestoreResult.status = 0;
            backupRestoreResult.size = 0;
            backupRestoreResult.source = backupRestoreRequest.source;
            File file = new File(backupRestoreRequest.path + File.separator + SmartSwitchReceiver.BACKUP_FILENAME);
            Log.m26i(SmartSwitchReceiver.TAG, "Path: " + backupRestoreRequest.path + File.separator + SmartSwitchReceiver.BACKUP_FILENAME);
            try {
                int i = C07071.f101xefc90adb[backupRestoreRequest.operation.ordinal()];
                if (i == 1) {
                    String makeBackupDataJSon = BackupRestoreHelper.makeBackupDataJSon(backupRestoreRequest.context);
                    if (makeBackupDataJSon == null) {
                        Log.m22e(SmartSwitchReceiver.TAG, "Backup failed");
                        backupRestoreResult.status = 1;
                        return backupRestoreResult;
                    }
                    bArr = makeBackupDataJSon.getBytes("UTF-8");
                    backupRestoreResult.size = bArr.length;
                    long availableStorage = StorageProvider.getAvailableStorage(Environment.getExternalStorageDirectory().toString());
                    if (((long) bArr.length) > availableStorage) {
                        Log.m22e(SmartSwitchReceiver.TAG, "Insufficient storage available: required " + bArr.length + " bytes, available " + availableStorage + " bytes");
                        backupRestoreResult.status = 2;
                        return backupRestoreResult;
                    }
                    File file2 = new File(file.getParent());
                    if (!file2.exists() && !file2.mkdir()) {
                        Log.m22e(SmartSwitchReceiver.TAG, "Fail to create parent file!");
                    }
                    fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(bArr);
                    fileOutputStream.close();
                } else if (i == 2) {
                    if (!file.exists()) {
                        Log.m22e(SmartSwitchReceiver.TAG, "Cannot locate backup data");
                        backupRestoreResult.status = 3;
                        return backupRestoreResult;
                    }
                    byte[] bArr2 = new byte[((int) file.length())];
                    fileInputStream = new FileInputStream(file);
                    fileInputStream.read(bArr2);
                    BackupRestoreHelper.restoreJsonBackupData(backupRestoreRequest.context, new String(bArr2, "UTF-8"));
                    fileInputStream.close();
                }
                return backupRestoreResult;
            } catch (IOException e) {
                int length = bArr != null ? bArr.length : 0;
                long availableStorage2 = StorageProvider.getAvailableStorage(Environment.getExternalStorageDirectory().toString());
                if (((long) length) > availableStorage2) {
                    Log.m22e(SmartSwitchReceiver.TAG, "Insufficient storage available: required " + length + " bytes, available " + availableStorage2 + " bytes");
                    backupRestoreResult.size = length;
                    backupRestoreResult.status = 2;
                } else {
                    Log.m22e(SmartSwitchReceiver.TAG, "I/O exception occurred: " + e.getMessage());
                    backupRestoreResult.size = 0;
                    backupRestoreResult.status = 1;
                }
                return backupRestoreResult;
            } catch (Throwable th) {
//                fileInputStream.close();
                throw th;
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(BackupRestoreResult backupRestoreResult) {
            if (backupRestoreResult != null) {
                int i = backupRestoreResult.status == 0 ? 0 : 1;
                if (backupRestoreResult.operation == Operation.RESTORE && i == 0) {
                    Settings.setSettings(Settings.KEY_FIRST_LAUNCH, false);
                }
                SmartSwitchReceiver.sendResponse(backupRestoreResult.context, backupRestoreResult.operation, i, backupRestoreResult.status, backupRestoreResult.size, backupRestoreResult.source);
                synchronized (SmartSwitchReceiver.mPendingTasks) {
                    SmartSwitchReceiver.mPendingTasks.remove(this);
                }
                super.onPostExecute(backupRestoreResult);
            }
        }
    }

    /* renamed from: com.sec.android.app.voicenote.backup.SmartSwitchReceiver$1 */
    static /* synthetic */ class C07071 {

        /* renamed from: $SwitchMap$com$sec$android$app$voicenote$backup$SmartSwitchReceiver$Operation */
        static final /* synthetic */ int[] f101xefc90adb = new int[Operation.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|6) */
        /* JADX WARNING: Code restructure failed: missing block: B:7:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        static {
            /*
                com.sec.android.app.voicenote.backup.SmartSwitchReceiver$Operation[] r0 = com.sec.android.app.voicenote.backup.SmartSwitchReceiver.Operation.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                f101xefc90adb = r0
                int[] r0 = f101xefc90adb     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.sec.android.app.voicenote.backup.SmartSwitchReceiver$Operation r1 = com.sec.android.app.voicenote.backup.SmartSwitchReceiver.Operation.BACKUP     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = f101xefc90adb     // Catch:{ NoSuchFieldError -> 0x001f }
                com.sec.android.app.voicenote.backup.SmartSwitchReceiver$Operation r1 = com.sec.android.app.voicenote.backup.SmartSwitchReceiver.Operation.RESTORE     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                return
            */
//            throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.backup.SmartSwitchReceiver.C07071.<clinit>():void");
        }
    }
}
