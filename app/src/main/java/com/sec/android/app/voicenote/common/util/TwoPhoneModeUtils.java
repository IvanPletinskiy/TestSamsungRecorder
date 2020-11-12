package com.sec.android.app.voicenote.common.util;

import android.content.Context;
import android.provider.Settings;

public class TwoPhoneModeUtils {
    private static final String TAG = "TwoPhoneModeUtils";
    public static final int TWO_PHONE_USING_MODE_B = 10;
    private static TwoPhoneModeUtils mInstance;

    private TwoPhoneModeUtils() {
    }

    public static TwoPhoneModeUtils getInstance() {
        if (mInstance == null) {
            mInstance = new TwoPhoneModeUtils();
        }
        return mInstance;
    }

    public boolean IsTwoPhoneMode(Context context) {
        boolean z = false;
        int i = Settings.Global.getInt(context.getContentResolver(), "two_register", 0);
        int i2 = Settings.Global.getInt(context.getContentResolver(), "two_account", 0);
//        int semGetMyUserId = UserHandle.semGetMyUserId();
//        if (i == 1 && i2 == 1 && semGetMyUserId == 10) {
//            z = true;
//        }
//        Log.m26i(TAG, "isEnabled: " + i + " - kt_two_phone_Account: " + i2 + " - userId: " + semGetMyUserId + " - IsTwoPhoneMode: " + z);
        return z;
    }
}
