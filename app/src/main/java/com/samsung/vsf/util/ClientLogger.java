package com.samsung.vsf.util;

import android.os.Build;
import com.samsung.vsf.SpeechRecognizer;
import org.json.JSONObject;

public class ClientLogger {
    private static JSONObject open_log;
    private static JSONObject prepare_log;

    public static String getOpenLogger(SpeechRecognizer.Config config) {
        if (open_log == null) {
            open_log = new JSONObject();
            try {
                open_log.put("CLIENTLOG_MODEL", Build.MODEL);
                JSONObject jSONObject = open_log;
                jSONObject.put("CLIENTLOG_OS", "Android_" + Build.VERSION.SDK_INT);
                open_log.put("CLIENTLOG_APP_VERSION", "20191001.1.35");
                open_log.put("CLIENTLOG_CLIENT_TYPE", config.getSDKClient());
            } catch (Exception unused) {
            }
        }
        return open_log.toString();
    }

    public static String getPrepareLogger(long j) {
        if (prepare_log == null) {
            prepare_log = new JSONObject();
        }
        try {
            prepare_log.put("CLIENTLOG_OPEN_LATENCY", j);
        } catch (Exception unused) {
        }
        return prepare_log.toString();
    }
}
