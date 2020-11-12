package com.sec.android.app.voicenote.common.util;

import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflector {
    private static final String TAG = "Reflector";

    public static Class<?> getClass(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    public static Method getMethod(Class<?> cls, String str) {
        Method[] methods;
        if (!(cls == null || (methods = cls.getMethods()) == null || str == null)) {
            for (Method method : methods) {
                if (str.equals(method.getName())) {
                    return method;
                }
            }
        }
        com.sec.android.app.voicenote.provider.Log.m32w(TAG, str + " NoSuchMethod");
        return null;
    }

    public static Method getDeclaredMethod(Class<?> cls, String str) {
        Method[] declaredMethods;
        if (!(cls == null || (declaredMethods = cls.getDeclaredMethods()) == null || str == null)) {
            for (Method method : declaredMethods) {
                if (str.equals(method.getName())) {
                    return method;
                }
            }
        }
        com.sec.android.app.voicenote.provider.Log.m32w(TAG, str + " NoSuchMethod");
        return null;
    }

    public static <T> Method getMethod(Class<T> cls, String str, Class<?>... clsArr) {
        try {
            return cls.getMethod(str, clsArr);
        } catch (NoSuchMethodException unused) {
            com.sec.android.app.voicenote.provider.Log.m19d(TAG, str + " NoSuchMethodException");
            return null;
        }
    }

    public static Object invoke(Object obj, Method method, Object... objArr) {
        if (method == null) {
            com.sec.android.app.voicenote.provider.Log.m19d(TAG, "method is null");
            return null;
        }
        try {
            Object invoke = method.invoke(obj, objArr);
            com.sec.android.app.voicenote.provider.Log.m19d(TAG, method.getName() + " is called");
            return invoke;
        } catch (InvocationTargetException unused) {
            com.sec.android.app.voicenote.provider.Log.m19d(TAG, method.getName() + " InvocationTargetException");
            return null;
        } catch (IllegalAccessException unused2) {
            com.sec.android.app.voicenote.provider.Log.m19d(TAG, method.getName() + " IllegalAccessException");
            return null;
        }
    }
}
