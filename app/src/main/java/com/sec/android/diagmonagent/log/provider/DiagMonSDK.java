package com.sec.android.diagmonagent.log.provider;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.samsung.context.sdk.samsunganalytics.BuildConfig;
import com.sec.android.diagmonagent.log.provider.utils.DiagMonUtil;
import com.sec.android.diagmonagent.log.provider.utils.Validator;
import java.lang.Thread;
import java.util.concurrent.TimeUnit;

public class DiagMonSDK {
    private static long MIN_WAITING_TIME = TimeUnit.HOURS.toMillis(6);
    private static String PREF_DIAGMON_CHECK = "diagmon_check";
    private static String PREF_DIAGMON_NAME = "diagmon_pref";
    private static String PREF_DIAGMON_TIMESTAMP = "diagmon_timestamp";
    /* access modifiers changed from: private */
    public static DiagMonProvider elp;
    /* access modifiers changed from: private */
    public static DiagMonSDK instance;
    /* access modifiers changed from: private */
    public static boolean isEnableDefaultConfig = false;
    private static boolean isEnableUncaughtExceptionLogging = false;
    /* access modifiers changed from: private */
    public static DiagMonConfig mConfig = null;
    private static Thread.UncaughtExceptionHandler originUncaughtExceptionHandler;
    /* access modifiers changed from: private */
    public static Bundle srObj;
    private static final Uri uri = Uri.parse("content://com.sec.android.log.diagmonagent/");

    public static String getSDKtype() {
        return "S";
    }

    /* access modifiers changed from: private */
    public static boolean eventReportViaCP(Context context, EventBuilder eventBuilder) {
        try {
            new Bundle();
            Bundle makeEventObjAsBundle = DiagMonUtil.makeEventObjAsBundle(context, mConfig, eventBuilder);
            if (makeEventObjAsBundle == null) {
                Log.w(DiagMonUtil.TAG, "No EventObject");
                return false;
            } else if (mConfig == null) {
                Log.w(DiagMonUtil.TAG, "No Configuration");
                Log.w(DiagMonUtil.TAG, "You have to set DiagMonConfiguration");
                return false;
            } else if (Validator.validateSrObj(mConfig.getContext(), srObj)) {
                Log.w(DiagMonUtil.TAG, "Invalid SR object");
                return false;
            } else if (Validator.validateErObj(mConfig.getContext(), makeEventObjAsBundle, srObj)) {
                Log.w(DiagMonUtil.TAG, "Invalid ER object");
                return false;
            } else {
                Log.d(DiagMonUtil.TAG, "Valid SR, ER object");
                Log.i(DiagMonUtil.TAG, "Report your logs");
                String str = DiagMonUtil.TAG;
                Log.i(str, "networkMode : " + eventBuilder.getNetworkMode());
                DiagMonUtil.printResultfromDMA(context.getContentResolver().call(uri, "event_report", "eventReport", makeEventObjAsBundle));
                String zipPath = eventBuilder.getZipPath();
                if (zipPath.isEmpty()) {
                    return true;
                }
                DiagMonUtil.removeZipFile(zipPath);
                return true;
            }
        } catch (Exception unused) {
            return false;
        }
    }

    /* access modifiers changed from: private */
    public static boolean eventReportViaBR(Context context, EventBuilder eventBuilder) {
        try {
            if (Validator.validateLegacyConfig(mConfig)) {
                Log.w(DiagMonUtil.TAG, "Invalid DiagMonConfiguration");
                return false;
            } else if (Validator.isValidLegacyEventBuilder(eventBuilder)) {
                Log.w(DiagMonUtil.TAG, "Invalid EventBuilder");
                return false;
            } else {
                Log.d(DiagMonUtil.TAG, "Valid EventBuilder");
                context.sendBroadcast(DiagMonUtil.makeEventobjAsIntent(context, mConfig, eventBuilder));
                Log.i(DiagMonUtil.TAG, "Report your logs");
                return true;
            }
        } catch (Exception unused) {
            return false;
        }
    }

    protected static class DiagMonHelper {
        /* JADX WARNING: Code restructure failed: missing block: B:22:0x00a8, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public static void setConfiguration(com.sec.android.diagmonagent.log.provider.DiagMonConfig r3) {
            /*
                java.lang.Class<com.sec.android.diagmonagent.log.provider.DiagMonSDK> r0 = com.sec.android.diagmonagent.log.provider.DiagMonSDK.class
                monitor-enter(r0)
                java.lang.String r1 = com.sec.android.diagmonagent.log.provider.utils.DiagMonUtil.TAG     // Catch:{ all -> 0x00a9 }
                java.lang.String r2 = "SetConfiguration"
                android.util.Log.i(r1, r2)     // Catch:{ all -> 0x00a9 }
                com.sec.android.diagmonagent.log.provider.DiagMonConfig unused = com.sec.android.diagmonagent.log.provider.DiagMonSDK.mConfig = r3     // Catch:{ all -> 0x00a9 }
                if (r3 != 0) goto L_0x0018
                java.lang.String r3 = com.sec.android.diagmonagent.log.provider.utils.DiagMonUtil.TAG     // Catch:{ all -> 0x00a9 }
                java.lang.String r1 = "DiagMonConfiguration is null"
                android.util.Log.w(r3, r1)     // Catch:{ all -> 0x00a9 }
                monitor-exit(r0)     // Catch:{ all -> 0x00a9 }
                return
            L_0x0018:
                com.sec.android.diagmonagent.log.provider.DiagMonConfig r3 = com.sec.android.diagmonagent.log.provider.DiagMonSDK.mConfig     // Catch:{ all -> 0x00a9 }
                android.content.Context r3 = r3.getContext()     // Catch:{ all -> 0x00a9 }
                int r3 = com.sec.android.diagmonagent.log.provider.utils.DiagMonUtil.checkDMA(r3)     // Catch:{ all -> 0x00a9 }
                r1 = 0
                if (r3 == 0) goto L_0x0096
                r2 = 1
                if (r3 == r2) goto L_0x0057
                r1 = 2
                if (r3 == r1) goto L_0x003c
                java.lang.String r3 = com.sec.android.diagmonagent.log.provider.utils.DiagMonUtil.TAG     // Catch:{ all -> 0x00a9 }
                java.lang.String r1 = "Exceptional case"
                android.util.Log.w(r3, r1)     // Catch:{ all -> 0x00a9 }
                java.lang.String r3 = com.sec.android.diagmonagent.log.provider.utils.DiagMonUtil.TAG     // Catch:{ all -> 0x00a9 }
                java.lang.String r1 = "SetConfiguration is aborted"
                android.util.Log.w(r3, r1)     // Catch:{ all -> 0x00a9 }
                goto L_0x00a7
            L_0x003c:
                android.os.Bundle r3 = new android.os.Bundle     // Catch:{ all -> 0x00a9 }
                r3.<init>()     // Catch:{ all -> 0x00a9 }
                android.os.Bundle unused = com.sec.android.diagmonagent.log.provider.DiagMonSDK.srObj = r3     // Catch:{ all -> 0x00a9 }
                com.sec.android.diagmonagent.log.provider.DiagMonConfig r3 = com.sec.android.diagmonagent.log.provider.DiagMonSDK.mConfig     // Catch:{ all -> 0x00a9 }
                android.os.Bundle r3 = com.sec.android.diagmonagent.log.provider.utils.DiagMonUtil.generateSRobj(r3)     // Catch:{ all -> 0x00a9 }
                android.os.Bundle unused = com.sec.android.diagmonagent.log.provider.DiagMonSDK.srObj = r3     // Catch:{ all -> 0x00a9 }
                android.os.Bundle r3 = com.sec.android.diagmonagent.log.provider.DiagMonSDK.srObj     // Catch:{ all -> 0x00a9 }
                com.sec.android.diagmonagent.log.provider.DiagMonSDK.sendSRObj(r3)     // Catch:{ all -> 0x00a9 }
                goto L_0x00a7
            L_0x0057:
                com.sec.android.diagmonagent.log.provider.DiagMonConfig r3 = com.sec.android.diagmonagent.log.provider.DiagMonSDK.mConfig     // Catch:{ all -> 0x00a9 }
                boolean r3 = com.sec.android.diagmonagent.log.provider.utils.Validator.validateLegacyConfig(r3)     // Catch:{ all -> 0x00a9 }
                if (r3 == 0) goto L_0x0073
                java.lang.String r3 = com.sec.android.diagmonagent.log.provider.utils.DiagMonUtil.TAG     // Catch:{ all -> 0x00a9 }
                java.lang.String r2 = "Invalid DiagMonConfiguration"
                android.util.Log.w(r3, r2)     // Catch:{ all -> 0x00a9 }
                java.lang.String r3 = com.sec.android.diagmonagent.log.provider.utils.DiagMonUtil.TAG     // Catch:{ all -> 0x00a9 }
                java.lang.String r2 = "SetConfiguration is aborted"
                android.util.Log.w(r3, r2)     // Catch:{ all -> 0x00a9 }
                com.sec.android.diagmonagent.log.provider.DiagMonSDK unused = com.sec.android.diagmonagent.log.provider.DiagMonSDK.instance = r1     // Catch:{ all -> 0x00a9 }
                goto L_0x00a7
            L_0x0073:
                com.sec.android.diagmonagent.log.provider.DiagMonSDK r3 = new com.sec.android.diagmonagent.log.provider.DiagMonSDK     // Catch:{ all -> 0x00a9 }
                r3.<init>()     // Catch:{ all -> 0x00a9 }
                com.sec.android.diagmonagent.log.provider.DiagMonSDK unused = com.sec.android.diagmonagent.log.provider.DiagMonSDK.instance = r3     // Catch:{ all -> 0x00a9 }
                com.sec.android.diagmonagent.log.provider.DiagMonProvider r3 = new com.sec.android.diagmonagent.log.provider.DiagMonProvider     // Catch:{ all -> 0x00a9 }
                r3.<init>()     // Catch:{ all -> 0x00a9 }
                com.sec.android.diagmonagent.log.provider.DiagMonProvider unused = com.sec.android.diagmonagent.log.provider.DiagMonSDK.elp = r3     // Catch:{ all -> 0x00a9 }
                com.sec.android.diagmonagent.log.provider.DiagMonProvider r3 = com.sec.android.diagmonagent.log.provider.DiagMonSDK.elp     // Catch:{ all -> 0x00a9 }
                com.sec.android.diagmonagent.log.provider.DiagMonConfig r1 = com.sec.android.diagmonagent.log.provider.DiagMonSDK.mConfig     // Catch:{ all -> 0x00a9 }
                r3.setConfiguration(r1)     // Catch:{ all -> 0x00a9 }
                java.lang.String r3 = com.sec.android.diagmonagent.log.provider.utils.DiagMonUtil.TAG     // Catch:{ all -> 0x00a9 }
                java.lang.String r1 = "Valid DiagMonConfiguration"
                android.util.Log.i(r3, r1)     // Catch:{ all -> 0x00a9 }
                goto L_0x00a7
            L_0x0096:
                java.lang.String r3 = com.sec.android.diagmonagent.log.provider.utils.DiagMonUtil.TAG     // Catch:{ all -> 0x00a9 }
                java.lang.String r2 = "Not installed DMA"
                android.util.Log.w(r3, r2)     // Catch:{ all -> 0x00a9 }
                java.lang.String r3 = com.sec.android.diagmonagent.log.provider.utils.DiagMonUtil.TAG     // Catch:{ all -> 0x00a9 }
                java.lang.String r2 = "SetConfiguration is aborted"
                android.util.Log.w(r3, r2)     // Catch:{ all -> 0x00a9 }
                com.sec.android.diagmonagent.log.provider.DiagMonSDK unused = com.sec.android.diagmonagent.log.provider.DiagMonSDK.instance = r1     // Catch:{ all -> 0x00a9 }
            L_0x00a7:
                monitor-exit(r0)     // Catch:{ all -> 0x00a9 }
                return
            L_0x00a9:
                r3 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x00a9 }
                throw r3
            */
            throw new UnsupportedOperationException("Method not decompiled: com.sec.android.diagmonagent.log.provider.DiagMonSDK.DiagMonHelper.setConfiguration(com.sec.android.diagmonagent.log.provider.DiagMonConfig):void");
        }

        public static void eventReport(Context context, DiagMonConfig diagMonConfig, EventBuilder eventBuilder) {
            DiagMonConfig unused = DiagMonSDK.mConfig = diagMonConfig;
            if (DiagMonUtil.checkDMA(context) == 0) {
                Log.w(DiagMonUtil.TAG, "not installed");
            } else if (DiagMonUtil.checkDMA(context) == 1) {
                Log.d(DiagMonUtil.TAG, "LEGACY DMA");
                setConfiguration(diagMonConfig);
                boolean unused2 = DiagMonSDK.eventReportViaBR(context, eventBuilder);
            } else if (DiagMonUtil.checkDMA(context) == 2) {
                Log.d(DiagMonUtil.TAG, "NEW DMA");
                Bundle unused3 = DiagMonSDK.srObj = new Bundle();
                Bundle unused4 = DiagMonSDK.srObj = DiagMonUtil.generateSRobj(DiagMonSDK.mConfig);
                if (DiagMonSDK.isEnableDefaultConfig) {
                    DiagMonSDK.sendSRObj(DiagMonSDK.srObj);
                }
                boolean unused5 = DiagMonSDK.eventReportViaCP(context, eventBuilder);
            } else {
                Log.d(DiagMonUtil.TAG, "Wrong Status");
            }
        }
    }

    public static boolean sendSRObj(Bundle bundle) {
        try {
            if (Validator.validateSrObj(mConfig.getContext(), bundle)) {
                Log.w(DiagMonUtil.TAG, "Invalid SR object");
                mConfig = null;
                return false;
            }
            Log.i(DiagMonUtil.TAG, "Valid SR object");
            Log.i(DiagMonUtil.TAG, "Request Service Registration");
            DiagMonUtil.printResultfromDMA(mConfig.getContext().getContentResolver().call(uri, "register_service", "registration", bundle));
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public static DiagMonProvider getElp() {
        try {
            return elp;
        } catch (Exception unused) {
            return null;
        }
    }

    public static DiagMonConfig getConfiguration() {
        try {
            return mConfig;
        } catch (Exception unused) {
            return null;
        }
    }

    public static String getSDKVersion() {
        try {
            return String.valueOf(BuildConfig.VERSION_CODE);
        } catch (Exception unused) {
            return "";
        }
    }

    public static void enableUncaughtExceptionLogging(Context context) {
        String str;
        boolean z;
        boolean defaultNetworkMode = false;
        try {
            if (isEnableUncaughtExceptionLogging) {
                Log.w(DiagMonUtil.TAG, "UncaughtExceptionLogging is already enabled");
            } else if (mConfig == null) {
                Log.w(DiagMonUtil.TAG, "UncaughtExceptionLogging Can't be enabled because Configuration is null");
            } else if (!mConfig.isCustomConfiguration() || !isEnableDefaultConfig) {
                String str2 = "D";
                if (isEnableDefaultConfig) {
                    defaultNetworkMode = mConfig.getDefaultNetworkMode();
                } else if (mConfig.isCustomConfiguration()) {
                    defaultNetworkMode = mConfig.isEnabledDefaultNetwork() ? mConfig.getDefaultNetworkMode() : true;
                    str2 = mConfig.getAgreeAsString();
                } else {
                    Log.i(DiagMonUtil.TAG, "value for uncaughtException will be default");
                    str = str2;
                    z = true;
                    isEnableUncaughtExceptionLogging = true;
                    originUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
                    Thread.setDefaultUncaughtExceptionHandler(new DiagMonLogger(context, originUncaughtExceptionHandler, mConfig, z, str));
                }
                z = defaultNetworkMode;
                str = str2;
                isEnableUncaughtExceptionLogging = true;
                originUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
                Thread.setDefaultUncaughtExceptionHandler(new DiagMonLogger(context, originUncaughtExceptionHandler, mConfig, z, str));
            } else {
                Log.w(DiagMonUtil.TAG, "UncaughtException Logging and SetConfiguration can't be used at the same time");
            }
        } catch (Exception e) {
            Log.e(DiagMonUtil.TAG, "failed to enableUncaughtExceptionLogging" + e);
        }
    }

    public static void setDefaultConfiguration(Context context, String str) {
        try {
            if (mConfig == null) {
                DiagMonConfig diagMonConfig = new DiagMonConfig(context);
                diagMonConfig.setServiceId(str);
                diagMonConfig.setAgree("D");
                mConfig = diagMonConfig;
                toggleConfigurationStatus(false);
            } else if (mConfig.isCustomConfiguration()) {
                Log.w(DiagMonUtil.TAG, "setDefaultConfiguration can't be used because CustomLogging is using");
            } else {
                Log.w(DiagMonUtil.TAG, "setDefaultConfiguration is already set");
            }
        } catch (Exception unused) {
        }
    }

    public static boolean isEnableDefaultConfiguration() {
        try {
            return isEnableDefaultConfig;
        } catch (Exception unused) {
            return false;
        }
    }

    protected static void toggleConfigurationStatus(boolean z) {
        DiagMonConfig diagMonConfig = mConfig;
        if (diagMonConfig == null) {
            Log.w(DiagMonUtil.TAG, "can't handle toggleConfigurationStatus");
        } else if (z) {
            isEnableDefaultConfig = false;
            diagMonConfig.setCustomConfigStatus(true);
            Log.d(DiagMonUtil.TAG, "Status is chaged to CustomLogging");
        } else {
            isEnableDefaultConfig = true;
            diagMonConfig.setCustomConfigStatus(false);
            Log.d(DiagMonUtil.TAG, "Status is chaged to UncaughtException");
        }
    }
}
