package com.sec.android.app.voicenote.provider;

import android.content.Context;

public class HWKeyboardProvider {
    private static final String TAG = "HWKeyboardProvider";
    private static boolean mDeviceHasHardkeyboard = false;

    public static boolean isHWKeyboard(Context context) {
//        if (context == null) {
//            Log.e(TAG, "context is null");
//            return false;
//        } else if (context.getResources().getConfiguration().semMobileKeyboardCovered == 1) {
//            return true;
//        } else {
//            return false;
//        }
        return false;
    }

    public static boolean isDeviceHasHardKeyboard(Context context) {
        if (context == null) {
//            Log.m22e(TAG, "context is null");
            return false;
        } else if (context.getResources().getConfiguration().hardKeyboardHidden == 1) {
            return true;
        } else {
            return false;
        }
    }

    public static void setDeviceHasHardkeyboard(boolean z) {
        mDeviceHasHardkeyboard = z;
    }

    public static boolean getDeviceHasHardkeyboard() {
        return mDeviceHasHardkeyboard;
    }
}
