package com.samsung.context.sdk.samsunganalytics.internal.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.provider.Settings;
import com.samsung.context.sdk.samsunganalytics.AnalyticsException;
import com.samsung.context.sdk.samsunganalytics.Configuration;
import com.samsung.context.sdk.samsunganalytics.internal.executor.SingleThreadExecutor;
import com.samsung.context.sdk.samsunganalytics.internal.sender.LogType;
import com.samsung.context.sdk.samsunganalytics.internal.setting.BuildClient;

public class Utils {

    /* renamed from: br */
    private static BroadcastReceiver f84br;

    public static boolean isEngBin() {
        return Build.TYPE.equals("eng");
    }

    public static void throwException(String str) {
        if (!isEngBin()) {
            Debug.LogE(str);
            return;
        }
        throw new AnalyticsException(str);
    }

    public static long getDaysAgo(int i) {
        return Long.valueOf(System.currentTimeMillis()).longValue() - (((long) i) * 86400000);
    }

    public static boolean compareDays(int i, Long l) {
        return Long.valueOf(System.currentTimeMillis()).longValue() > l.longValue() + (((long) i) * 86400000);
    }

    public static boolean compareHours(int i, Long l) {
        return Long.valueOf(System.currentTimeMillis()).longValue() > l.longValue() + (((long) i) * 3600000);
    }

    public static LogType getTypeForServer(String str) {
        return "dl".equals(str) ? LogType.DEVICE : LogType.UIX;
    }

    public static boolean isDiagnosticAgree(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "samsung_errorlog_agree", 0) == 1;
    }

    public static void sendSettings(Context context, Configuration configuration) {
        SingleThreadExecutor.getInstance().execute(new BuildClient(context, configuration));
    }

    public static void registerReceiver(Context context, final Configuration configuration) {
        Debug.LogENG("register BR ");
        if (f84br == null) {
            f84br = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("receive BR ");
                    sb.append(intent != null ? intent.getAction() : "null");
                    Debug.LogENG(sb.toString());
                    if (intent != null && "android.intent.action.ACTION_POWER_CONNECTED".equals(intent.getAction())) {
                        Utils.sendSettings(context, configuration);
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED");
            context.registerReceiver(f84br, intentFilter);
            return;
        }
        Debug.LogENG("BR is already registered");
    }
}
