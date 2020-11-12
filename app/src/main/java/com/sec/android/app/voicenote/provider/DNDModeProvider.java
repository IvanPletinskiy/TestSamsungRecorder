package com.sec.android.app.voicenote.provider;

import android.content.Context;
import android.provider.Settings;

public class DNDModeProvider {
    private static final int DND_MODE_OFF = 0;
    private static final String TAG = "DNDModeProvider";

    private DNDModeProvider() {
    }

    public static boolean getDoNotDisturb(Context context) {
        try {
            int i = Settings.Global.getInt(context.getContentResolver(), "zen_mode", 0);
            Log.m19d(TAG, "DNDMode : " + i);
            if (i != 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.m24e(TAG, "It failed to get DND value", (Throwable) e);
            return false;
        }
    }
}
