package com.samsung.context.sdk.samsunganalytics;

import android.app.Application;
import com.samsung.context.sdk.samsunganalytics.internal.Tracker;
import com.samsung.context.sdk.samsunganalytics.internal.policy.Validation;
import com.samsung.context.sdk.samsunganalytics.internal.util.Debug;
import com.samsung.context.sdk.samsunganalytics.internal.util.Utils;
import java.util.Map;
import java.util.Set;

public class SamsungAnalytics {
    private static SamsungAnalytics instance;
    private Tracker tracker = null;

    private SamsungAnalytics(Application application, Configuration configuration) {
        if (!Validation.isValidConfig(application, configuration)) {
            return;
        }
        if (configuration.isEnableUseInAppLogging() || Validation.isLoggingEnableDevice(application)) {
            this.tracker = new Tracker(application, configuration);
        }
    }

    private static SamsungAnalytics getInstanceAndConfig(Application application, Configuration configuration) {
        SamsungAnalytics samsungAnalytics = instance;
        if (samsungAnalytics == null || samsungAnalytics.tracker == null) {
            synchronized (SamsungAnalytics.class) {
                instance = new SamsungAnalytics(application, configuration);
            }
        }
        return instance;
    }

    public static void setConfiguration(Application application, Configuration configuration) {
        getInstanceAndConfig(application, configuration);
    }

    public static SamsungAnalytics getInstance() {
        if (instance == null) {
            Utils.throwException("call after setConfiguration() method");
            if (!Utils.isEngBin()) {
                return getInstanceAndConfig((Application) null, (Configuration) null);
            }
        }
        return instance;
    }

    public int sendLog(Map<String, String> map) {
        try {
            return this.tracker.sendLog(map, false);
        } catch (NullPointerException unused) {
            return -100;
        }
    }

    public void registerSettingPref(Map<String, Set<String>> map) {
        try {
            this.tracker.registerSettingPref(map);
        } catch (NullPointerException e) {
            Debug.LogException(SamsungAnalytics.class, e);
        }
    }
}
