package com.sec.android.app.voicenote.common.util;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

public class ThreadUtil {
    private static final String TAG = "ThreadUtil";

    @NonNull
    public static Handler getMainThreadHandler() {
        return new Handler(Looper.getMainLooper());
    }

    public static void postOnUiThread(Runnable runnable) {
        getMainThreadHandler().post(runnable);
    }

    public static void postOnUiThreadDelayed(Runnable runnable, long j) {
        getMainThreadHandler().postDelayed(runnable, j);
    }
}
