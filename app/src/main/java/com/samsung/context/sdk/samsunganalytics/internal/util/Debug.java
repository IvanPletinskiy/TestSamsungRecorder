package com.samsung.context.sdk.samsunganalytics.internal.util;

import android.util.Log;

public class Debug {
    public static void LogENG(String str) {
        if (Utils.isEngBin()) {
            Log.d("SamsungAnalytics605015", "[ENG ONLY] " + str);
        }
    }

    public static void LogD(String str) {
        Log.d("SamsungAnalytics605015", str);
    }

    public static void LogD(String str, String str2) {
        LogD("[" + str + "] " + str2);
    }

    public static void LogE(String str) {
        Log.e("SamsungAnalytics605015", str);
    }

    public static void LogException(Class cls, Exception exc) {
        if (exc != null) {
            Log.w("SamsungAnalytics605015", "[" + cls.getSimpleName() + "] " + exc.getClass().getSimpleName() + " " + exc.getMessage());
        }
    }
}
