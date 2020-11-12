package com.sec.vsg.voiceframework.process;

import android.util.Log;

public class ProcessLOGS {
    public static void info(String str, String str2) {
        Log.i(str, str2);
    }

    public static void error(String str, String str2) {
        Log.e(str, str2);
    }

    public static void warn(String str, String str2) {
        Log.w(str, str2);
    }

    public static void debug(String str, String str2) {
        Log.d(str, str2);
    }
}
