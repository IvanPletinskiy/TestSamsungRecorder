package com.sec.android.app.voicenote.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import android.provider.Settings;
import com.sec.android.app.voicenote.provider.Log;

public class DemoResetReceiver extends BroadcastReceiver {
    private static final String TAG = "DemoResetReceiver";
    private static final String VOICENOTE_DEMO_RESET_STARTED = "com.samsung.sea.rm.DEMO_RESET_STARTED";

    public static boolean isDemoDevice(Context context) {
        return isLDUModel() || isShopDemo(context);
    }

    public static boolean isLDUModel() {
//        String salesCode = SemSystemProperties.getSalesCode();
//        Log.m19d(TAG, "salesCode  = " + salesCode);
//        return "PAP".equals(salesCode) || "FOP".equals(salesCode) || "LDU".equals(salesCode);
        return false;
    }

    public static boolean isShopDemo(Context context) {
        boolean z = false;
        if (Settings.Secure.getInt(context.getContentResolver(), "shopdemo", 0) == 1) {
            z = true;
        }
        Log.m19d(TAG, "isShopDemo  = " + z);
        return z;
    }

    public void onReceive(Context context, Intent intent) {
        Log.m26i(TAG, "onReceive");
        if (Build.VERSION.SDK_INT < 29) {
            Log.m26i(TAG, "allow from Q OS");
        } else if (!isDemoDevice(context)) {
            Log.m22e(TAG, "Not demo device!");
        } else if (intent != null) {
            String action = intent.getAction();
            if (action == null || action.isEmpty() || !action.equalsIgnoreCase(VOICENOTE_DEMO_RESET_STARTED)) {
                Log.m22e(TAG, "action is null or empty");
            } else {
                ((ActivityManager) context.getSystemService("activity")).clearApplicationUserData();
            }
        }
    }
}
