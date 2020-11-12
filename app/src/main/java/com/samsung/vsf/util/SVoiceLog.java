package com.samsung.vsf.util;

import android.os.Build;
import android.util.Log;

public class SVoiceLog {
    private static boolean enableLogging = false;
    private static SVoiceLogWriter logWriterThread = null;
    private static boolean m_isEnabled = true;
    private static boolean m_isEnabled_versbose = "eng".equalsIgnoreCase(Build.TYPE);

    public static void debug(String str, String str2) {
        if (m_isEnabled) {
            Log.d(str + ":" + getThread(), " : VSP::" + str2);
        }
    }

    public static void warn(String str, String str2) {
        Log.w(str + ":" + getThread(), " : VSP::" + str2);
    }

    public static void error(String str, String str2) {
        Log.e(str + ":" + getThread(), " : VSP::" + str2);
    }

    public static void info(String str, String str2) {
        if (m_isEnabled) {
            Log.i(str + ":" + getThread(), " : VSP::" + str2);
        }
    }

    public static void verbose(String str, String str2) {
        if (m_isEnabled_versbose) {
            Log.v(str + ":" + getThread(), " : VSP::" + str2);
        }
    }

    private static String getThread() {
        return Thread.currentThread().getName();
    }
}
