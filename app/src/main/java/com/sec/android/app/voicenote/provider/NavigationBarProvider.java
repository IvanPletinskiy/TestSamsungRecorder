package com.sec.android.app.voicenote.provider;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.KeyCharacterMap;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowInsets;
import com.sec.android.app.voicenote.uicore.Observable;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

public class NavigationBarProvider {
    private static final String TAG = "NavigationBarProvider";
    private static volatile NavigationBarProvider mInstance;
    private Context mAppContext = null;
    private Handler mHandler = null;
    private Boolean mIsNavigationBarVisible;
    private Boolean mSupportSoftNavigationBar;

    private NavigationBarProvider() {
    }

    public static NavigationBarProvider getInstance() {
        if (mInstance == null) {
            synchronized (NavigationBarProvider.class) {
                if (mInstance == null) {
                    mInstance = new NavigationBarProvider();
                }
            }
        }
        return mInstance;
    }

    public void onDestroy() {
        this.mIsNavigationBarVisible = false;
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeMessages(0);
            this.mHandler = null;
        }
    }

    public void setApplicationContext(Context context) {
        this.mAppContext = context;
    }

    public int getNavigationBarHeight(Activity activity, boolean z) {
        int i;
        if (!isDeviceSupportSoftNavigationBar()) {
            return 0;
        }
        if (!z && (!isNavigationBarEnabled() || HWKeyboardProvider.isHWKeyboard(this.mAppContext))) {
            return 0;
        }
        if (isFullScreenGesture()) {
            i = getNavigationGestureHeight(activity);
        } else {
            i = getNavigationNormalHeight();
        }
        Log.m26i(TAG, "getNavigationBarHeight: " + i);
        return i;
    }

    public int getNavigationGestureHeight(Activity activity) {
        WindowInsets rootWindowInsets = activity.getWindow().getDecorView().getRootWindowInsets();
        if (rootWindowInsets != null) {
            return rootWindowInsets.getStableInsetBottom();
        }
        return 0;
    }

    public int getNavigationNormalHeight() {
        Resources resources = this.mAppContext.getResources();
        int identifier = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (identifier > 0) {
            return resources.getDimensionPixelSize(identifier);
        }
        return 0;
    }

    public boolean isFullScreenGesture() {
        return Settings.Global.getInt(this.mAppContext.getContentResolver(), "navigation_bar_gesture_while_hidden", 1) == 1;
    }

    public boolean isDeviceSupportSoftNavigationBar() {
        if (this.mSupportSoftNavigationBar == null) {
            Resources resources = this.mAppContext.getResources();
            int identifier = resources.getIdentifier("config_showNavigationBar", "bool", "android");
            boolean z = false;
            boolean z2 = identifier > 0 ? resources.getBoolean(identifier) : false;
            if (!z2) {
                boolean hasPermanentMenuKey = ViewConfiguration.get(this.mAppContext).hasPermanentMenuKey();
                boolean deviceHasKey = KeyCharacterMap.deviceHasKey(4);
                if (!hasPermanentMenuKey && !deviceHasKey) {
                    z = true;
                }
                z2 = z;
            }
            Log.m19d(TAG, "isDeviceSupportSoftNavigationBar: " + z2);
            this.mSupportSoftNavigationBar = Boolean.valueOf(z2);
        }
        return this.mSupportSoftNavigationBar.booleanValue();
    }

    public boolean isNavigationBarEnabled() {
        this.mIsNavigationBarVisible = Boolean.valueOf(isSoftNavigationBarEnabled());
        return this.mIsNavigationBarVisible.booleanValue();
    }

    private boolean isSoftNavigationBarEnabled() {
        if (isDeviceSupportSoftNavigationBar() && Settings.Global.getInt(this.mAppContext.getContentResolver(), "navigationbar_hide_bar_enabled", 0) != 1) {
            return true;
        }
        return false;
    }

    public boolean isNavigationBarChanged() {
        boolean isSoftNavigationBarEnabled = isSoftNavigationBarEnabled();
        if (this.mIsNavigationBarVisible.booleanValue() == isSoftNavigationBarEnabled) {
            return false;
        }
        this.mIsNavigationBarVisible = Boolean.valueOf(isSoftNavigationBarEnabled);
        return true;
    }

    public void setOnSystemUiVisibilityChangeListener(Activity activity) {
        activity.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            public final void onSystemUiVisibilityChange(int i) {
                NavigationBarProvider.this.mo12971x252ad695(i);
            }
        });
    }

    /* renamed from: lambda$setOnSystemUiVisibilityChangeListener$0$NavigationBarProvider */
    public /* synthetic */ void mo12971x252ad695(int i) {
        if (isNavigationBarChanged()) {
            if (this.mHandler == null) {
                initHandler();
            }
            this.mHandler.removeMessages(0);
            this.mHandler.sendEmptyMessageDelayed(0, 50);
        }
    }

    private void initHandler() {
        this.mHandler = new Handler() {
            public void handleMessage(Message message) {
                VoiceNoteObservable.getInstance().notifyObservers(15);
                Observable.getInstance().notifyObservers(VoiceNoteApplication.getSimpleActivitySession(), 15);
            }
        };
    }
}
