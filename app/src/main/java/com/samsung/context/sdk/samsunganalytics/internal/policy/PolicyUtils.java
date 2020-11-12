package com.samsung.context.sdk.samsunganalytics.internal.policy;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.text.TextUtils;
import com.samsung.context.sdk.samsunganalytics.BuildConfig;
import com.samsung.context.sdk.samsunganalytics.Configuration;
import com.samsung.context.sdk.samsunganalytics.internal.Callback;
import com.samsung.context.sdk.samsunganalytics.internal.connection.API;
import com.samsung.context.sdk.samsunganalytics.internal.device.DeviceInfo;
import com.samsung.context.sdk.samsunganalytics.internal.executor.Executor;
import com.samsung.context.sdk.samsunganalytics.internal.util.Debug;
import com.samsung.context.sdk.samsunganalytics.internal.util.Preferences;
import com.samsung.context.sdk.samsunganalytics.internal.util.Utils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PolicyUtils {
    private static int senderType = -1;

    public static int getRemainingQuota(Context context, int i) {
        int i2;
        SharedPreferences preferences = Preferences.getPreferences(context);
        int i3 = 0;
        if (i == 1) {
            i2 = preferences.getInt("dq-w", 0);
            i3 = preferences.getInt("wifi_used", 0);
        } else if (i == 0) {
            i2 = preferences.getInt("dq-3g", 0);
            i3 = preferences.getInt("data_used", 0);
        } else {
            i2 = 0;
        }
        return i2 - i3;
    }

    public static int hasQuota(Context context, int i, int i2) {
        int i3;
        int i4;
        int i5;
        SharedPreferences preferences = Preferences.getPreferences(context);
        if (i == 1) {
            i3 = preferences.getInt("dq-w", 0);
            i5 = preferences.getInt("wifi_used", 0);
            i4 = preferences.getInt("oq-w", 0);
        } else if (i == 0) {
            i3 = preferences.getInt("dq-3g", 0);
            i5 = preferences.getInt("data_used", 0);
            i4 = preferences.getInt("oq-3g", 0);
        } else {
            i4 = 0;
            i3 = 0;
            i5 = 0;
        }
        Debug.LogENG("Quota : " + i3 + "/ Uploaded : " + i5 + "/ limit : " + i4 + "/ size : " + i2);
        if (i3 < i5 + i2) {
            Debug.LogD("DLS Sender", "send result fail : Over daily quota");
            return -1;
        } else if (i4 >= i2) {
            return 0;
        } else {
            Debug.LogD("DLS Sender", "send result fail : Over once quota");
            return -11;
        }
    }

    public static boolean isPolicyExpired(Context context) {
        SharedPreferences preferences = Preferences.getPreferences(context);
        if (Utils.compareDays(1, Long.valueOf(preferences.getLong("quota_reset_date", 0)))) {
            resetQuota(preferences);
        }
        return Utils.compareDays(preferences.getInt("rint", 1), Long.valueOf(preferences.getLong("policy_received_date", 0)));
    }

    public static void resetQuota(SharedPreferences sharedPreferences) {
        sharedPreferences.edit().putLong("quota_reset_date", System.currentTimeMillis()).putInt("data_used", 0).putInt("wifi_used", 0).apply();
    }

    public static Map<String, String> makePolicyParam(Context context, DeviceInfo deviceInfo, Configuration configuration) {
        HashMap hashMap = new HashMap();
        hashMap.put("pkn", context.getPackageName());
        hashMap.put("dm", deviceInfo.getDeviceModel());
        if (!TextUtils.isEmpty(deviceInfo.getMcc())) {
            hashMap.put("mcc", deviceInfo.getMcc());
        }
        if (!TextUtils.isEmpty(deviceInfo.getMnc())) {
            hashMap.put("mnc", deviceInfo.getMnc());
        }
        hashMap.put("uv", configuration.getVersion());
        hashMap.put("sv", BuildConfig.VERSION_NAME);
        hashMap.put("did", configuration.getDeviceId());
        hashMap.put("tid", configuration.getTrackingId());
        String format = SimpleDateFormat.getTimeInstance(2, Locale.US).format(new Date());
        hashMap.put("ts", format);
        hashMap.put("hc", Validation.sha256(configuration.getTrackingId() + format + Validation.SALT));
        String csc = getCSC();
        if (!TextUtils.isEmpty(csc)) {
            hashMap.put("csc", csc);
        }
        return hashMap;
    }

    public static void getPolicy(Context context, Configuration configuration, Executor executor, DeviceInfo deviceInfo, Callback callback) {
        executor.execute(makeGetPolicyClient(context, configuration, deviceInfo, callback));
    }

    public static void getPolicy(Context context, Configuration configuration, Executor executor, DeviceInfo deviceInfo) {
        executor.execute(makeGetPolicyClient(context, configuration, deviceInfo, (Callback) null));
    }

    public static GetPolicyClient makeGetPolicyClient(Context context, Configuration configuration, DeviceInfo deviceInfo, Callback callback) {
        GetPolicyClient getPolicyClient = new GetPolicyClient(API.GET_POLICY, makePolicyParam(context, deviceInfo, configuration), Preferences.getPreferences(context), callback);
        Debug.LogENG("trid: " + configuration.getTrackingId().substring(0, 7) + ", uv: " + configuration.getVersion());
        return getPolicyClient;
    }

    public static void useQuota(Context context, int i, int i2) {
        SharedPreferences preferences = Preferences.getPreferences(context);
        if (i == 1) {
            preferences.edit().putInt("wifi_used", preferences.getInt("wifi_used", 0) + i2).apply();
        } else if (i == 0) {
            preferences.edit().putInt("data_used", Preferences.getPreferences(context).getInt("data_used", 0) + i2).apply();
        }
    }

    public static int setSenderType(Context context, Configuration configuration) {
        if (senderType == -1) {
            try {
                int i = 0;
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo("com.sec.android.diagmonagent", 0);
                Debug.LogD("Validation", "dma pkg:" + packageInfo.versionCode);
                if (packageInfo.versionCode < 540000000) {
                    if (!configuration.isEnableUseInAppLogging()) {
                        i = 1;
                    }
                    senderType = i;
                } else if (packageInfo.versionCode >= 600000000) {
                    senderType = 3;
                } else {
                    senderType = 2;
                }
            } catch (Exception e) {
                senderType = configuration.isEnableUseInAppLogging() ^ true ? 1 : 0;
                Debug.LogD("DMA not found" + e.getMessage());
            }
        }
        return senderType;
    }

    public static int getSenderType() {
        return senderType;
    }

    public static String getCSC() {
        try {
            return (String) Class.forName("android.os.SystemProperties").getMethod("get", new Class[]{String.class}).invoke((Object) null, new Object[]{"ro.csc.sales_code"});
        } catch (Exception unused) {
            return null;
        }
    }
}
