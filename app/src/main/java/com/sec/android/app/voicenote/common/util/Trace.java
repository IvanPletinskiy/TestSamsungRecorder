package com.sec.android.app.voicenote.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Trace {
    private static final long TAG = 8;
    private static Method sTraceBegin;
    private static Method sTraceEnd;

    public static void beginSection(String str) {
        try {
            getTraceBegin().invoke(android.os.Trace.class, new Object[]{Long.valueOf(TAG), str});
        } catch (IllegalAccessException | NullPointerException | InvocationTargetException unused) {
        }
    }

    public static void endSection() {
        try {
            getTraceEnd().invoke(android.os.Trace.class, new Object[]{Long.valueOf(TAG)});
        } catch (IllegalAccessException | NullPointerException | InvocationTargetException unused) {
        }
    }

    private static Method getTraceBegin() {
        if (sTraceBegin == null) {
            try {
                sTraceBegin = android.os.Trace.class.getMethod("traceBegin", new Class[]{Long.TYPE, String.class});
            } catch (NoSuchMethodException unused) {
            }
        }
        return sTraceBegin;
    }

    private static Method getTraceEnd() {
        if (sTraceEnd == null) {
            Class<android.os.Trace> cls = android.os.Trace.class;
            try {
                sTraceEnd = cls.getMethod("traceEnd", new Class[]{Long.TYPE});
            } catch (NoSuchMethodException unused) {
            }
        }
        return sTraceEnd;
    }
}
