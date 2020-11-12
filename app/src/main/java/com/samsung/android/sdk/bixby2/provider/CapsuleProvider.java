package com.samsung.android.sdk.bixby2.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.samsung.android.sdk.bixby2.LogUtil;
import com.samsung.android.sdk.bixby2.Sbixby;
import com.samsung.android.sdk.bixby2.action.ActionHandler;
import com.samsung.android.sdk.bixby2.action.ResponseCallback;
import com.samsung.android.sdk.bixby2.receiver.ApplicationTriggerReceiver;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class CapsuleProvider extends ContentProvider {
    /* access modifiers changed from: private */
    public static final String TAG = (CapsuleProvider.class.getSimpleName() + "_" + "1.0.12");
    private static Map<String, ActionHandler> actionMap = new HashMap();
    private static String mActionId = null;
    private static Signature mBixbyAgentSignature = new Signature(Base64.decode("MIIE1DCCA7ygAwIBAgIJANIJlaecDarWMA0GCSqGSIb3DQEBBQUAMIGiMQswCQYDVQQGEwJLUjEUMBIGA1UECBMLU291dGggS29yZWExEzARBgNVBAcTClN1d29uIENpdHkxHDAaBgNVBAoTE1NhbXN1bmcgQ29ycG9yYXRpb24xDDAKBgNVBAsTA0RNQzEVMBMGA1UEAxMMU2Ftc3VuZyBDZXJ0MSUwIwYJKoZIhvcNAQkBFhZhbmRyb2lkLm9zQHNhbXN1bmcuY29tMB4XDTExMDYyMjEyMjUxMloXDTM4MTEwNzEyMjUxMlowgaIxCzAJBgNVBAYTAktSMRQwEgYDVQQIEwtTb3V0aCBLb3JlYTETMBEGA1UEBxMKU3V3b24gQ2l0eTEcMBoGA1UEChMTU2Ftc3VuZyBDb3Jwb3JhdGlvbjEMMAoGA1UECxMDRE1DMRUwEwYDVQQDEwxTYW1zdW5nIENlcnQxJTAjBgkqhkiG9w0BCQEWFmFuZHJvaWQub3NAc2Ftc3VuZy5jb20wggEgMA0GCSqGSIb3DQEBAQUAA4IBDQAwggEIAoIBAQDJhjhKPh8vsgZnDnjvIyIVwNJvRaInKNuZpE2hHDWsM6cf4HHEotaCWptMiLMz7ZbzxebGZtYPPulMSQiFq8+NxmD3B6q8d+rT4tDYrugQjBXNJg8uhQQsKNLyktqjxtoMe/I5HbeEGq3o/fDJ0N7893Ek5tLeCp4NLadGw2cOT/zchbcBu0dEhhuW/3MR2jYDxaEDNuVf+jS0NT7tyF9RAV4VGMZ+MJ45+HY5/xeBB/EJzRhBGmB38mlktuY/inC5YZ2wQwajI8Gh0jr4Z+GfFPVw/+Vz0OOgwrMGMqrsMXM4CZS+HjQeOpC9LkthVIH0bbOeqDgWRI7DX+sXNcHzAgEDo4IBCzCCAQcwHQYDVR0OBBYEFJMsOvcLYnoMdhC1oOdCfWz66j8eMIHXBgNVHSMEgc8wgcyAFJMsOvcLYnoMdhC1oOdCfWz66j8eoYGopIGlMIGiMQswCQYDVQQGEwJLUjEUMBIGA1UECBMLU291dGggS29yZWExEzARBgNVBAcTClN1d29uIENpdHkxHDAaBgNVBAoTE1NhbXN1bmcgQ29ycG9yYXRpb24xDDAKBgNVBAsTA0RNQzEVMBMGA1UEAxMMU2Ftc3VuZyBDZXJ0MSUwIwYJKoZIhvcNAQkBFhZhbmRyb2lkLm9zQHNhbXN1bmcuY29tggkA0gmVp5wNqtYwDAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQUFAAOCAQEAMpYB/kDgNqSobMXUndjBtUFZmOcmN1OLDUMDaaxRUw9jqs6MAZoaZmFqLxuyxfq9bzEyYfOA40cWI/BT2ePFP1/W0ZZdewAOTcJEwbJ+L+mjI/8Hf1LEZ16GJHqoARhxN+MMm78BxWekKZ20vwslt9cQenuB7hAvcv9HlQFk4mdS4RTEL4udKkLnMIiX7GQOoZJO0Tq76dEgkSti9JJkk6htuUwLRvRMYWHVjC9kgWSJDFEt+yjULIVb9HDb7i2raWDK0E6B9xUl3tRs3Q81n5nEYNufAH2WzoO0shisLYLEjxJgjUaXM/BaM3VZRmnMv4pJVUTWxXAek2nAjIEBWA==", 0));
    private static boolean mIsAppInitialized = false;
    private static final boolean mIsUserBuild = "user".equals(Build.TYPE);
    /* access modifiers changed from: private */
    public static boolean mWaitForHandler = false;
    private static Object sWaitLock = new Object();
    /* access modifiers changed from: private */
    public Object sActionExecutionLock = new Object();

    public int delete(@NonNull Uri uri, @Nullable String str, @Nullable String[] strArr) {
        return 0;
    }

    public String getType(@NonNull Uri uri) {
        return "actionUri";
    }

    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    public Cursor query(@NonNull Uri uri, @Nullable String[] strArr, @Nullable String str, @Nullable String[] strArr2, @Nullable String str2) {
        return null;
    }

    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String str, @Nullable String[] strArr) {
        return 0;
    }

    public static void setAppInitialized(boolean z) {
        synchronized (sWaitLock) {
            if (!mIsAppInitialized && z) {
                mIsAppInitialized = z;
                LogUtil.m18i(TAG, "releasing initialize wait lock.");
                sWaitLock.notify();
            }
        }
        new Timer().schedule(new TimerTask() {
            public void run() {
                boolean unused = CapsuleProvider.mWaitForHandler = false;
            }
        }, 3000);
    }

    public static void addActionHandler(String str, ActionHandler actionHandler) {
        synchronized (sWaitLock) {
            if (actionMap.get(str) == null) {
                actionMap.put(str, actionHandler);
                if (mActionId != null && mActionId.equals(str)) {
                    String str2 = TAG;
                    LogUtil.m18i(str2, "handler added: " + str);
                    sWaitLock.notify();
                }
            }
        }
    }

    private boolean isCallerAllowed() {
        if (!mIsUserBuild) {
            return true;
        }
        int callingUid = Binder.getCallingUid();
        PackageManager packageManager = getContext().getPackageManager();
        String[] packagesForUid = packageManager.getPackagesForUid(callingUid);
        if (packagesForUid == null) {
            LogUtil.m17e(TAG, "packages is null");
            return false;
        }
        for (String str : packagesForUid) {
            if ("com.samsung.android.bixby.agent".equals(str)) {
                try {
                    Signature[] signatureArr = packageManager.getPackageInfo(str, 64).signatures;
                    if (signatureArr != null && signatureArr.length > 0 && mBixbyAgentSignature.equals(signatureArr[0])) {
                        return true;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        LogUtil.m17e(TAG, "Not allowed to access capsule provider. package (s): " + Arrays.toString(packagesForUid));
        return false;
    }

    public boolean onCreate() {
        LogUtil.m18i(TAG, "onCreate");
        mWaitForHandler = true;
        return true;
    }

    private void executeProcessTriggerReceiver() {
        if (getContext() != null) {
            ApplicationTriggerReceiver applicationTriggerReceiver = new ApplicationTriggerReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.samsung.android.sdk.bixby2.ACTION_APPLICATION_TRIGGER");
            getContext().registerReceiver(applicationTriggerReceiver, intentFilter);
            LogUtil.m18i(TAG, "ApplicationTriggerReceiver registered");
            Intent intent = new Intent();
            intent.setAction("com.samsung.android.sdk.bixby2.ACTION_APPLICATION_TRIGGER");
            intent.addFlags(268435456);
            getContext().sendBroadcast(intent);
        }
    }

    private void waitForAppInitialization() {
        synchronized (sWaitLock) {
            if (!mIsAppInitialized) {
                try {
                    sWaitLock.wait(5000);
                } catch (InterruptedException e) {
                    LogUtil.m17e(TAG, "interrupted exception");
                    e.printStackTrace();
                }
            }
        }
    }

    public Bundle call(@NonNull String str, @Nullable String str2, @Nullable Bundle bundle) {
        LogUtil.m18i(TAG, "call()");
        String str3 = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("call(): method --> ");
        sb.append(str);
        sb.append(" args --> ");
        sb.append(str2);
        sb.append(" extras --> ");
        sb.append(bundle != null ? bundle.toString() : null);
        LogUtil.m16d(str3, sb.toString());
        if (!isCallerAllowed()) {
            throw new SecurityException("not allowed to access capsule provider.");
        } else if (!TextUtils.isEmpty(str)) {
            if (!mIsAppInitialized) {
                executeProcessTriggerReceiver();
            }
            waitForAppInitialization();
            if (!mIsAppInitialized) {
                LogUtil.m17e(TAG, "App initialization error.");
                return updateStatus(-1, "Initialization Failure..");
            } else if (str.equals("getAppContext")) {
                Sbixby.getInstance();
                String appState = Sbixby.getStateHandler().getAppState(getContext());
                if (appState == null) {
                    return null;
                }
                Bundle bundle2 = new Bundle();
                bundle2.putString("appContext", appState);
                return bundle2;
            } else if (bundle != null) {
                return executeAction(str, bundle);
            } else {
                throw new IllegalArgumentException("action params are EMPTY.");
            }
        } else {
            throw new IllegalArgumentException("method is null or empty. pass valid action name.");
        }
    }

    private Bundle updateStatus(int i, String str) {
        Bundle bundle = new Bundle();
        bundle.putInt("status_code", i);
        if (TextUtils.isEmpty(str) && i == -1) {
            str = "Failed to execute action.";
            LogUtil.m17e(TAG, str);
        }
        bundle.putString("status_message", str);
        return bundle;
    }

    private ActionHandler getActionHandler(String str) throws InterruptedException {
        ActionHandler actionHandler = actionMap.get(str);
        synchronized (sWaitLock) {
            if (actionHandler == null) {
                if (mWaitForHandler) {
                    mActionId = str;
                    sWaitLock.wait(3000);
                    actionHandler = actionMap.get(str);
                }
            }
        }
        return actionHandler;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0076, code lost:
        return updateStatus(-1, "action execution timed out");
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized android.os.Bundle executeAction(java.lang.String r11, android.os.Bundle r12) {
        /*
            r10 = this;
            monitor-enter(r10)
            r0 = -1
            java.lang.String r1 = TAG     // Catch:{ Exception -> 0x008b }
            java.lang.String r2 = "executeAction()"
            com.samsung.android.sdk.bixby2.LogUtil.m18i(r1, r2)     // Catch:{ Exception -> 0x008b }
            com.samsung.android.sdk.bixby2.action.ActionHandler r5 = r10.getActionHandler(r11)     // Catch:{ Exception -> 0x008b }
            if (r5 != 0) goto L_0x001f
            java.lang.String r11 = TAG     // Catch:{ Exception -> 0x008b }
            java.lang.String r12 = "Handler not found!!.."
            com.samsung.android.sdk.bixby2.LogUtil.m17e(r11, r12)     // Catch:{ Exception -> 0x008b }
            r11 = -2
            java.lang.String r12 = "Action handler not found"
            android.os.Bundle r11 = r10.updateStatus(r11, r12)     // Catch:{ Exception -> 0x008b }
            monitor-exit(r10)
            return r11
        L_0x001f:
            if (r12 == 0) goto L_0x007a
            java.lang.String r1 = "actionType"
            boolean r1 = r12.containsKey(r1)     // Catch:{ Exception -> 0x008b }
            if (r1 != 0) goto L_0x002a
            goto L_0x007a
        L_0x002a:
            com.samsung.android.sdk.bixby2.provider.CapsuleProvider$CapsuleResponseCallback r1 = new com.samsung.android.sdk.bixby2.provider.CapsuleProvider$CapsuleResponseCallback     // Catch:{ Exception -> 0x008b }
            r1.<init>()     // Catch:{ Exception -> 0x008b }
            java.lang.Thread r2 = new java.lang.Thread     // Catch:{ Exception -> 0x008b }
            com.samsung.android.sdk.bixby2.provider.CapsuleProvider$2 r9 = new com.samsung.android.sdk.bixby2.provider.CapsuleProvider$2     // Catch:{ Exception -> 0x008b }
            r3 = r9
            r4 = r10
            r6 = r11
            r7 = r12
            r8 = r1
            r3.<init>(r5, r6, r7, r8)     // Catch:{ Exception -> 0x008b }
            r2.<init>(r9)     // Catch:{ Exception -> 0x008b }
            r2.start()     // Catch:{ Exception -> 0x008b }
            java.lang.Object r11 = r10.sActionExecutionLock     // Catch:{ Exception -> 0x008b }
            monitor-enter(r11)     // Catch:{ Exception -> 0x008b }
            boolean r12 = r1.actionExecuted     // Catch:{ all -> 0x0077 }
            if (r12 != 0) goto L_0x0051
            java.lang.Object r12 = r10.sActionExecutionLock     // Catch:{ all -> 0x0077 }
            r3 = 30000(0x7530, double:1.4822E-319)
            r12.wait(r3)     // Catch:{ all -> 0x0077 }
        L_0x0051:
            boolean r12 = r1.actionExecuted     // Catch:{ all -> 0x0077 }
            if (r12 == 0) goto L_0x0060
            android.os.Bundle r12 = r1.getResultBundle()     // Catch:{ all -> 0x0077 }
            if (r12 == 0) goto L_0x006e
            monitor-exit(r11)     // Catch:{ all -> 0x0077 }
            monitor-exit(r10)
            return r12
        L_0x0060:
            java.lang.String r12 = TAG     // Catch:{ all -> 0x0077 }
            java.lang.String r3 = "timeout occurred.."
            com.samsung.android.sdk.bixby2.LogUtil.m17e(r12, r3)     // Catch:{ all -> 0x0077 }
            r12 = 1
            r1.setActionTimedOut(r12)     // Catch:{ all -> 0x0077 }
            r2.interrupt()     // Catch:{ all -> 0x0077 }
        L_0x006e:
            monitor-exit(r11)     // Catch:{ all -> 0x0077 }
            java.lang.String r11 = "action execution timed out"
            android.os.Bundle r11 = r10.updateStatus(r0, r11)     // Catch:{ all -> 0x0089 }
            monitor-exit(r10)
            return r11
        L_0x0077:
            r12 = move-exception
            monitor-exit(r11)     // Catch:{ all -> 0x0077 }
            throw r12     // Catch:{ Exception -> 0x008b }
        L_0x007a:
            java.lang.String r11 = TAG     // Catch:{ Exception -> 0x008b }
            java.lang.String r12 = "params missing"
            com.samsung.android.sdk.bixby2.LogUtil.m17e(r11, r12)     // Catch:{ Exception -> 0x008b }
            java.lang.String r11 = "params missing.."
            android.os.Bundle r11 = r10.updateStatus(r0, r11)     // Catch:{ Exception -> 0x008b }
            monitor-exit(r10)
            return r11
        L_0x0089:
            r11 = move-exception
            goto L_0x00b3
        L_0x008b:
            r11 = move-exception
            java.lang.String r12 = TAG     // Catch:{ all -> 0x0089 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0089 }
            r1.<init>()     // Catch:{ all -> 0x0089 }
            java.lang.String r2 = "Unable to execute action."
            r1.append(r2)     // Catch:{ all -> 0x0089 }
            java.lang.String r2 = r11.toString()     // Catch:{ all -> 0x0089 }
            r1.append(r2)     // Catch:{ all -> 0x0089 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0089 }
            com.samsung.android.sdk.bixby2.LogUtil.m17e(r12, r1)     // Catch:{ all -> 0x0089 }
            r11.printStackTrace()     // Catch:{ all -> 0x0089 }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x0089 }
            android.os.Bundle r11 = r10.updateStatus(r0, r11)     // Catch:{ all -> 0x0089 }
            monitor-exit(r10)
            return r11
        L_0x00b3:
            monitor-exit(r10)
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.samsung.android.sdk.bixby2.provider.CapsuleProvider.executeAction(java.lang.String, android.os.Bundle):android.os.Bundle");
    }

    private class CapsuleResponseCallback implements ResponseCallback {
        /* access modifiers changed from: private */
        public boolean actionExecuted;
        private boolean actionTimedOut;
        private Bundle resultBundle;

        public CapsuleResponseCallback() {
            this.resultBundle = new Bundle();
            this.actionExecuted = false;
            this.actionTimedOut = false;
            this.actionExecuted = false;
            this.actionTimedOut = false;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:15:0x005b, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onComplete(java.lang.String r5) {
            /*
                r4 = this;
                java.lang.String r0 = com.samsung.android.sdk.bixby2.provider.CapsuleProvider.TAG
                java.lang.String r1 = "onComplete()"
                com.samsung.android.sdk.bixby2.LogUtil.m18i(r0, r1)
                com.samsung.android.sdk.bixby2.provider.CapsuleProvider r0 = com.samsung.android.sdk.bixby2.provider.CapsuleProvider.this
                java.lang.Object r0 = r0.sActionExecutionLock
                monitor-enter(r0)
                boolean r1 = r4.actionTimedOut     // Catch:{ all -> 0x005c }
                if (r1 == 0) goto L_0x0016
                monitor-exit(r0)     // Catch:{ all -> 0x005c }
                return
            L_0x0016:
                boolean r1 = r4.actionExecuted     // Catch:{ all -> 0x005c }
                if (r1 != 0) goto L_0x005a
                java.lang.String r1 = com.samsung.android.sdk.bixby2.provider.CapsuleProvider.TAG     // Catch:{ all -> 0x005c }
                java.lang.String r2 = "Action Execution Success"
                com.samsung.android.sdk.bixby2.LogUtil.m18i(r1, r2)     // Catch:{ all -> 0x005c }
                android.os.Bundle r1 = r4.resultBundle     // Catch:{ all -> 0x005c }
                java.lang.String r2 = "status_code"
                r3 = 0
                r1.putInt(r2, r3)     // Catch:{ all -> 0x005c }
                android.os.Bundle r1 = r4.resultBundle     // Catch:{ all -> 0x005c }
                java.lang.String r2 = "result"
                r1.putString(r2, r5)     // Catch:{ all -> 0x005c }
                java.lang.String r1 = com.samsung.android.sdk.bixby2.provider.CapsuleProvider.TAG     // Catch:{ all -> 0x005c }
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x005c }
                r2.<init>()     // Catch:{ all -> 0x005c }
                java.lang.String r3 = "action result: "
                r2.append(r3)     // Catch:{ all -> 0x005c }
                if (r5 == 0) goto L_0x0043
                goto L_0x0044
            L_0x0043:
                r5 = 0
            L_0x0044:
                r2.append(r5)     // Catch:{ all -> 0x005c }
                java.lang.String r5 = r2.toString()     // Catch:{ all -> 0x005c }
                com.samsung.android.sdk.bixby2.LogUtil.m16d(r1, r5)     // Catch:{ all -> 0x005c }
                r5 = 1
                r4.actionExecuted = r5     // Catch:{ all -> 0x005c }
                com.samsung.android.sdk.bixby2.provider.CapsuleProvider r5 = com.samsung.android.sdk.bixby2.provider.CapsuleProvider.this     // Catch:{ all -> 0x005c }
                java.lang.Object r5 = r5.sActionExecutionLock     // Catch:{ all -> 0x005c }
                r5.notify()     // Catch:{ all -> 0x005c }
            L_0x005a:
                monitor-exit(r0)     // Catch:{ all -> 0x005c }
                return
            L_0x005c:
                r5 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x005c }
                throw r5
            */
            throw new UnsupportedOperationException("Method not decompiled: com.samsung.android.sdk.bixby2.provider.CapsuleProvider.CapsuleResponseCallback.onComplete(java.lang.String):void");
        }

        public Bundle getResultBundle() {
            return this.resultBundle;
        }

        public void setActionTimedOut(boolean z) {
            this.actionTimedOut = z;
        }
    }
}
