package com.sec.android.app.voicenote.common.util;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

public class VRUtil {
    public static final String TAG = "VRUtil";

    public static boolean isTalkBackOn(Context context) {
        if (context == null) {
            return false;
        }
        ContentResolver contentResolver = context.getContentResolver();
        String string = Settings.Secure.getString(contentResolver, "enabled_accessibility_services");
        if (Settings.Secure.getInt(contentResolver, "accessibility_enabled", 0) != 1 || string == null || !string.matches("(?i).*talkback.*")) {
            return false;
        }
        return true;
    }
}
