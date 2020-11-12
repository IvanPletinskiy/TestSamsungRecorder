package com.sec.android.app.voicenote.common.util;

import android.app.Activity;
import android.content.res.Resources;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;

public class StatusBarHelper {
    private static final String TAG = "StatusBarHelper";

    public static boolean isIncludeStatusBarInMultiWindow(Activity activity) {
        if (!DisplayManager.isInMultiWindowMode(activity) || DisplayManager.getMultiwindowMode() != 2) {
            return false;
        }
        if ((DisplayManager.isDeviceOnLandscape() || VoiceNoteFeature.FLAG_IS_TABLET) && activity.getResources().getIdentifier("statusBarBackground", DialogFactory.BUNDLE_ID, "android") > 0) {
            return true;
        }
        return false;
    }

    public static int getStatusBarHeight(Activity activity) {
        Resources resources = activity.getResources();
        int identifier = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (identifier > 0) {
            int dimensionPixelSize = resources.getDimensionPixelSize(identifier);
            Log.m19d(TAG, "getStatusBarHeight - " + dimensionPixelSize);
            return dimensionPixelSize;
        }
        int ceil = (int) Math.ceil((double) (resources.getDisplayMetrics().density * 24.0f));
        Log.m19d(TAG, "getStatusBarHeight - default : " + ceil);
        return ceil;
    }
}
