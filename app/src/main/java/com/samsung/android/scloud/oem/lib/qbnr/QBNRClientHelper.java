package com.samsung.android.scloud.oem.lib.qbnr;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;

import com.samsung.android.scloud.oem.lib.LOG;
import com.samsung.android.scloud.oem.lib.common.IClientHelper;
import com.samsung.android.scloud.oem.lib.common.IServiceHandler;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;

import java.util.HashMap;
import java.util.Map;

import androidx.core.app.NotificationCompat;

public class QBNRClientHelper extends IClientHelper {
    /* access modifiers changed from: private */
    public static final String TAG = "QBNRClientHelper";
    private final ISCloudQBNRClient backupClient;
    /* access modifiers changed from: private */
    public boolean mIsFinished;
    /* access modifiers changed from: private */
    public boolean mIsSuccess;
    /* access modifiers changed from: private */
    public long mProcNow;
    /* access modifiers changed from: private */
    public long mProcTotal;
    private final Map<String, IServiceHandler> serviceHandlerMap = new HashMap();

    public QBNRClientHelper(ISCloudQBNRClient iSCloudQBNRClient) {
        this.backupClient = iSCloudQBNRClient;
        this.serviceHandlerMap.put("getClientInfo", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                String access$000 = QBNRClientHelper.TAG;
                LOG.m15i(access$000, "[" + str + "] GET_CLIENT_INFO , " + str);
                ISCloudQBNRClient iSCloudQBNRClient = (ISCloudQBNRClient) obj;
                boolean isSupportBackup = iSCloudQBNRClient.isSupportBackup(context);
                boolean isEnableBackup = iSCloudQBNRClient.isEnableBackup(context);
                String label = iSCloudQBNRClient.getLabel(context);
                String description = iSCloudQBNRClient.getDescription(context);
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("support_backup", isSupportBackup);
                bundle2.putString(DialogFactory.BUNDLE_NAME, str);
                bundle2.putBoolean("is_enable_backup", isEnableBackup);
                bundle2.putString("label", label);
                bundle2.putString("description", description);
                String access$0002 = QBNRClientHelper.TAG;
                LOG.m12d(access$0002, "[" + str + "] GET_CLIENT_INFO, " + str + ", " + label);
                return bundle2;
            }
        });
        this.serviceHandlerMap.put("backup", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                final Uri parse = Uri.parse(bundle.getString("observing_uri"));
                final ParcelFileDescriptor parcelFileDescriptor = (ParcelFileDescriptor) bundle.getParcelable("file");
                QBNRClientHelper.this.init();
                final Object obj2 = obj;
                final Context context2 = context;
                final String str2 = str;
//                C06441 r1 = new Runnable() {
//                    public void run() {
//                        ((ISCloudQBNRClient) obj2).backup(context2, parcelFileDescriptor, new ISCloudQBNRClient.QuickBackupListener() {
//                        });
//                    }
//                };
//                new Thread(r1, "BACKUP_" + str).start();
                return null;
            }
        });
        this.serviceHandlerMap.put("restore", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                final Uri parse = Uri.parse(bundle.getString("observing_uri"));
                final ParcelFileDescriptor parcelFileDescriptor = (ParcelFileDescriptor) bundle.getParcelable("file");
                QBNRClientHelper.this.init();
                final Object obj2 = obj;
                final Context context2 = context;
                final String str2 = str;
//                C06471 r1 = new Runnable() {
//                    public void run() {
//                        ((ISCloudQBNRClient) obj2).restore(context2, parcelFileDescriptor, new ISCloudQBNRClient.QuickBackupListener() {
//                        });
//                    }
//                };
//                new Thread(r1, "RESTORE_" + str).start();
                return null;
            }
        });
        this.serviceHandlerMap.put("get_status", new IServiceHandler() {
            public Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle) {
                String access$000 = QBNRClientHelper.TAG;
                LOG.m15i(access$000, "[" + str + "] GET_STATUS: is_finished: " + QBNRClientHelper.this.mIsFinished + ", is_success: " + QBNRClientHelper.this.mIsSuccess + ", proc: " + QBNRClientHelper.this.mProcNow + ", total: " + QBNRClientHelper.this.mProcTotal);
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("is_finished", QBNRClientHelper.this.mIsFinished);
                bundle2.putBoolean("is_success", QBNRClientHelper.this.mIsSuccess);
                if (!QBNRClientHelper.this.mIsFinished) {
                    long j = 0;
                    if (QBNRClientHelper.this.mProcTotal != 0) {
                        j = (QBNRClientHelper.this.mProcNow * 100) / QBNRClientHelper.this.mProcTotal;
                    }
                    bundle2.putInt(NotificationCompat.CATEGORY_PROGRESS, (int) j);
                }
                return bundle2;
            }
        });
    }

    public IServiceHandler getServiceHandler(String str) {
        return this.serviceHandlerMap.get(str);
    }

    public Object getClient(String str) {
        return this.backupClient;
    }

    /* access modifiers changed from: private */
    public void init() {
        this.mProcNow = 0;
        this.mProcTotal = 0;
        this.mIsFinished = false;
        this.mIsSuccess = false;
    }
}
