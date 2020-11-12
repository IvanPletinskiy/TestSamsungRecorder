package com.sec.android.app.voicenote.provider;

import android.os.Build;

public final class Log {
    public static boolean ENG = "eng".equals(Build.TYPE);
    public static boolean SESSION = true;
    private static final String prefix = "VR#";

    /* renamed from: v */
    public static int m29v(String str, String str2) {
        if (!ENG) {
            return 0;
        }
        return android.util.Log.v(prefix + str, str2);
    }

    /* renamed from: v */
    public static int m31v(String str, String str2, Throwable th) {
        if (!ENG) {
            return 0;
        }
        return android.util.Log.v(prefix + str, str2, th);
    }

    /* renamed from: v */
    public static int m30v(String str, String str2, String str3) {
        if (!ENG) {
            return 0;
        }
        if (SESSION) {
            return android.util.Log.v(prefix + str + " : " + str3, str2);
        }
        return android.util.Log.v(prefix + str, str2);
    }

    /* renamed from: d */
    public static int m19d(String str, String str2) {
        if (!ENG) {
            return 0;
        }
        return android.util.Log.d(prefix + str, str2);
    }

    /* renamed from: d */
    public static int m21d(String str, String str2, Throwable th) {
        if (!ENG) {
            return 0;
        }
        return android.util.Log.d(prefix + str, str2, th);
    }

    /* renamed from: d */
    public static int m20d(String str, String str2, String str3) {
        if (!ENG) {
            return 0;
        }
        if (SESSION) {
            return android.util.Log.d(prefix + str + " : " + str3, str2);
        }
        return android.util.Log.d(prefix + str, str2);
    }

    /* renamed from: i */
    public static int m26i(String str, String str2) {
        return android.util.Log.i(prefix + str, str2);
    }

    /* renamed from: i */
    public static int m28i(String str, String str2, Throwable th) {
        return android.util.Log.i(prefix + str, str2, th);
    }

    /* renamed from: i */
    public static int m27i(String str, String str2, String str3) {
        if (SESSION) {
            return android.util.Log.i(prefix + str + " : " + str3, str2);
        }
        return android.util.Log.i(prefix + str, str2);
    }

    /* renamed from: w */
    public static int m32w(String str, String str2) {
        return android.util.Log.w(prefix + str, str2);
    }

    /* renamed from: w */
    public static int m34w(String str, String str2, Throwable th) {
        return android.util.Log.w(prefix + str, str2, th);
    }

    /* renamed from: w */
    public static int m35w(String str, Throwable th) {
        return android.util.Log.w(prefix + str, th);
    }

    /* renamed from: w */
    public static int m33w(String str, String str2, String str3) {
        if (SESSION) {
            return android.util.Log.w(prefix + str + " : " + str3, str2);
        }
        return android.util.Log.w(prefix + str, str2);
    }

    /* renamed from: e */
    public static int m22e(String str, String str2) {
        return android.util.Log.e(prefix + str, str2);
    }

    /* renamed from: e */
    public static int m24e(String str, String str2, Throwable th) {
        return android.util.Log.e(prefix + str, str2, th);
    }

    /* renamed from: e */
    public static int m23e(String str, String str2, String str3) {
        if (SESSION) {
            return android.util.Log.e(prefix + str + " : " + str3, str2);
        }
        return android.util.Log.e(prefix + str, str2);
    }

    /* renamed from: e */
    public static int m25e(String str, String str2, Throwable th, String str3) {
        if (SESSION) {
            return android.util.Log.e(prefix + str + " : " + str3, str2, th);
        }
        return android.util.Log.e(prefix + str, str2, th);
    }

    public static void setMode(boolean z) {
        android.util.Log.i("VR#Log", "setMode : " + z);
        ENG = z;
    }
}
