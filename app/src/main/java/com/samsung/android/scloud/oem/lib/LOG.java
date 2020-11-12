package com.samsung.android.scloud.oem.lib;

import android.os.Build;
import android.util.Log;
import java.util.Locale;

public class LOG {
    private static boolean enabled = "eng".equals(Build.TYPE);

    /* renamed from: i */
    public static void m15i(String str, String str2) {
        if (str2 != null) {
            Log.i("[PDLIB]" + str, str2);
        }
    }

    /* renamed from: d */
    public static void m12d(String str, String str2) {
        if (enabled && str2 != null) {
            Log.d("[PDLIB]" + str, str2);
        }
    }

    /* renamed from: e */
    public static void m14e(String str, String str2, Throwable th) {
        Locale locale = Locale.US;
        Object[] objArr = new Object[2];
        objArr[0] = str2;
        objArr[1] = th == null ? "" : Log.getStackTraceString(th);
        String format = String.format(locale, "%s %s", objArr);
        Log.e("[PDLIB]SCLOUD_ERR-" + str, format);
    }

    /* renamed from: e */
    public static void m13e(String str, String str2) {
        m14e(str, str2, (Throwable) null);
    }
}
