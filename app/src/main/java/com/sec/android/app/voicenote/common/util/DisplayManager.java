package com.sec.android.app.voicenote.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.NavigationBarProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import java.util.List;

public class DisplayManager {
    public static final int LANDSCAPE = 1;
    public static final int LANDSCAPE_MULTIWINDOW = 3;
    public static final int MODE_FREEFORM = 1;
    public static final int MODE_NONE = 0;
    public static final int MODE_PICTURE_IN_PICTURE = 4;
    public static final int MODE_SPLIT_SCREEN = 2;
    public static final int PORTRAIT = 0;
    public static final int PORTRAIT_MULTIWINDOW = 2;
    private static final String TAG = "DeviceInfoDisplayManager";
    private static Point sCurrentScreenSize;
    private static Point sFullScreenSize;
    private static int sPreviousDeviceOrientation;
    private static Point sPreviousWindowSize;
    private static int sRotationBasedOnInflatedLayout;

    public static void logDisplayInfo(Context context, Configuration configuration) {
        if (DeviceInfo.isEngBinary()) {
            Activity activity = (Activity) context;
            getCurrentScreenWidth(activity);
            getCurrentScreenHeight(activity);
            getFullScreenWidth();
            getFullScreenHeight();
            getMultiwindowMode();
            isDeviceOnLandscape();
            isCurrentWindowOnLandscape(activity);
            StatusBarHelper.getStatusBarHeight(activity);
            getActionBarHeight(activity);
            int navigationBarHeight = NavigationBarProvider.getInstance().getNavigationBarHeight(activity, false);
            Log.m19d(TAG, "getNavigationBarHeight = " + navigationBarHeight);
            Log.m19d(TAG, "isDeX_inExternalMonitor = " + isInDeXExternalMonitor(activity));
        }
    }

    private static void refreshDisplayInfo(Activity activity) {
        Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
        if (sCurrentScreenSize == null) {
            sCurrentScreenSize = new Point();
        }
        defaultDisplay.getSize(sCurrentScreenSize);
    }

    public static int getCurrentScreenWidth(Activity activity) {
        refreshDisplayInfo(activity);
        Log.m19d(TAG, "getCurrentScreenWidth: " + sCurrentScreenSize.x);
        return sCurrentScreenSize.x;
    }

    public static int getCurrentScreenHeight(Activity activity) {
        refreshDisplayInfo(activity);
        Log.m19d(TAG, "getCurrentScreenHeight: " + sCurrentScreenSize.y);
        return sCurrentScreenSize.y;
    }

    public static int getMultiWindowCurrentAppHeight(Activity activity) {
        int currentScreenHeight = getCurrentScreenHeight(activity);
        if (!isInMultiWindowMode(activity)) {
            return currentScreenHeight;
        }
        if (getMultiwindowMode() == 1 || isInDeXExternalMonitor(activity)) {
            currentScreenHeight -= activity.getResources().getDimensionPixelSize(C0690R.dimen.multi_popup_caption_height);
        }
        return StatusBarHelper.isIncludeStatusBarInMultiWindow(activity) ? currentScreenHeight - StatusBarHelper.getStatusBarHeight(activity) : currentScreenHeight;
    }

    public static Point getCurrentScreenSize(Activity activity) {
        refreshDisplayInfo(activity);
        Log.m19d(TAG, "getCurrentScreenSize: " + sCurrentScreenSize);
        return sCurrentScreenSize;
    }

    private static void initFullScreenSizePoint() {
        if (sFullScreenSize == null) {
            sFullScreenSize = new Point();
        }
        ((WindowManager) VoiceNoteApplication.getApplication().getSystemService("window")).getDefaultDisplay().getSize(sFullScreenSize);
        Log.m19d(TAG, "initFullScreenSizePoint: " + sFullScreenSize.x + " " + sFullScreenSize.y);
    }

    public static int getFullScreenWidth() {
        initFullScreenSizePoint();
        Log.m19d(TAG, "getFullScreenWidth: " + sFullScreenSize.x);
        return sFullScreenSize.x;
    }

    public static int getFullScreenHeight() {
        initFullScreenSizePoint();
        Log.m19d(TAG, "getFullScreenHeight: " + sFullScreenSize.y);
        return sFullScreenSize.y;
    }

    public static int getMultiwindowMode() {
//        int mode = new SemMultiWindowManager().getMode();
//        if (mode == 1) {
//            Log.m19d(TAG, "getMultiwindowMode: FREEFORM");
//        } else if (mode != 2) {
//            Log.m19d(TAG, "getMultiwindowMode: mode = " + mode);
//        } else {
//            Log.m19d(TAG, "getMultiwindowMode: SPLIT SCREEN");
//        }
//        return mode;
        return 0;
    }

    public static boolean isMultiWindowVerticalSplitMode(Activity activity) {
        return VoiceNoteFeature.FLAG_IS_WINNER() && !isDeviceOnLandscape() && getMultiwindowMode() == 2 && getCurrentScreenWidth(activity) != getFullScreenWidth() && ((double) getMultiWindowCurrentAppHeight(activity)) > ((double) getFullScreenHeight()) * 0.9d;
    }

    public static boolean isInMultiWindowMode(Activity activity) {
        return activity.isInMultiWindowMode();
    }

    public static boolean isDeviceOnLandscape() {
        initFullScreenSizePoint();
        Point point = sFullScreenSize;
        boolean z = point.x > point.y;
        Log.m19d(TAG, "isDeviceOnLandscape = " + z);
        return z;
    }

    public static boolean isInDeXExternalMonitor(Activity activity) {
        return activity.getResources().getBoolean(C0690R.bool.dex_in_external_monitor);
    }

    public static boolean isCurrentWindowOnLandscape(Activity activity) {
        boolean z = activity.getResources().getBoolean(C0690R.bool.is_landscape);
        Log.m19d(TAG, "_VR in isCurrentWindowOnLandscape =" + z);
        return z;
    }

    public static int getActionBarHeight(Activity activity) {
        TypedValue typedValue = new TypedValue();
        if (activity.getTheme().resolveAttribute(16843499, typedValue, true)) {
            int complexToDimensionPixelSize = TypedValue.complexToDimensionPixelSize(typedValue.data, activity.getResources().getDisplayMetrics());
            Log.m19d(TAG, "getActionBarHeight : " + complexToDimensionPixelSize);
            return complexToDimensionPixelSize;
        }
        int ceil = (int) Math.ceil((double) (activity.getResources().getDisplayMetrics().density * 56.0f));
        Log.m19d(TAG, "getActionBarHeight - default :" + ceil);
        return ceil;
    }

    public static int identifyOrientationForInflatingLayout(Activity activity) {
//        int isDeviceOnLandscape = isDeviceOnLandscape();
//        if (isInMultiWindowMode(activity)) {
//            if (isDeviceOnLandscape != 0) {
//                int minWidthLandscapeWindow = getMinWidthLandscapeWindow(activity);
//                Log.m19d(TAG, "Landscape multi window min width : " + minWidthLandscapeWindow);
//                if (getCurrentScreenWidth(activity) >= minWidthLandscapeWindow) {
//                    isDeviceOnLandscape = 3;
//                }
//            }
//            isDeviceOnLandscape = 2;
//        }
//        convertOrientation("IDENTIFY", isDeviceOnLandscape);
//        return isDeviceOnLandscape;
        return 0;
    }

    public static void convertOrientation(String str, int i) {
        if (i == 0) {
            Log.m19d(TAG, str + "_VR in PORTRAIT");
        } else if (i == 1) {
            Log.m19d(TAG, str + "_VR in LANDSCAPE");
        } else if (i == 2) {
            Log.m19d(TAG, str + "_VR in PORTRAIT_MULTIWINDOW");
        } else if (i != 3) {
            Log.m19d(TAG, str + "VR_convertOrientation : " + i);
        } else {
            Log.m19d(TAG, str + "_VR in LANDSCAPE_MULTIWINDOW");
        }
    }

    public static void setVROrientation(int i) {
        convertOrientation("setVROrientation", i);
        sRotationBasedOnInflatedLayout = i;
    }

    public static int getVROrientation() {
        return sRotationBasedOnInflatedLayout;
    }

    public static void updateWindowSize(Activity activity) {
        Point currentScreenSize = getCurrentScreenSize(activity);
        Log.m19d(TAG, "updatePreviousWindowSize: " + currentScreenSize);
        if (sPreviousWindowSize == null) {
            sPreviousWindowSize = new Point();
        }
        Point point = sPreviousWindowSize;
        point.x = currentScreenSize.x;
        point.y = currentScreenSize.y;
    }

    public static boolean isMultiWindowSizeChanged(Activity activity) {
        boolean z = true;
        if (sPreviousWindowSize == null) {
            return true;
        }
        if (getCurrentScreenWidth(activity) == sPreviousWindowSize.x && getCurrentScreenHeight(activity) == sPreviousWindowSize.y) {
            z = false;
        }
        Log.m19d(TAG, "isMultiWindowSizeChanged : " + z + " - PreviousWindow : " + sPreviousWindowSize);
        return z;
    }

    public static void updateDeviceOrientation() {
        sPreviousDeviceOrientation = getDeviceOrientation();
    }

    private static int getDeviceOrientation() {
        return isDeviceOnLandscape() ? 1 : 0;
    }

    public static boolean isDeviceOrientationChanged() {
        boolean z = sPreviousDeviceOrientation != getDeviceOrientation();
        Log.m19d(TAG, "isDeviceOrientationChanged : " + z);
        return z;
    }

    public static boolean windowWidthReachThreshold(Activity activity) {
        if (sPreviousWindowSize == null) {
            updateWindowSize(activity);
            return false;
        }
        int minWidthLandscapeWindow = getMinWidthLandscapeWindow(activity);
        boolean z = sPreviousWindowSize.x < minWidthLandscapeWindow && getCurrentScreenWidth(activity) >= minWidthLandscapeWindow;
        boolean z2 = sPreviousWindowSize.x >= minWidthLandscapeWindow && getCurrentScreenWidth(activity) < minWidthLandscapeWindow;
        StringBuilder sb = new StringBuilder();
        sb.append("windowWidthReachThreshold + ");
        sb.append(z || z2);
        Log.m19d(TAG, sb.toString());
        if (z || z2) {
            return true;
        }
        return false;
    }

    private static int getMinWidthLandscapeWindow(Activity activity) {
        int fullScreenWidth = (getFullScreenWidth() > getFullScreenHeight() ? getFullScreenWidth() : getFullScreenHeight()) / 2;
        int dimensionPixelSize = activity.getResources().getDimensionPixelSize(C0690R.dimen.multi_landscape_window_min_width);
        return dimensionPixelSize > fullScreenWidth ? dimensionPixelSize : fullScreenWidth;
    }

    public static boolean smallHalfScreen(Activity activity) {
        return getCurrentScreenHeight(activity) < getFullScreenHeight() / 2;
    }

    public static boolean isDarkMode(Context context) {
        return (context.getResources().getConfiguration().uiMode & 48) == 32;
    }

    public static void setSystemGestureExclusionRects(View view, List<Rect> list) {
        if (Build.VERSION.SDK_INT >= 29) {
            view.setSystemGestureExclusionRects(list);
        }
    }
}
