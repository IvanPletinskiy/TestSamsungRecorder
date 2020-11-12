package com.sec.android.app.voicenote.provider;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.samsung.context.sdk.samsunganalytics.Configuration;
import com.samsung.context.sdk.samsunganalytics.LogBuilders$EventBuilder;
import com.samsung.context.sdk.samsunganalytics.LogBuilders$ScreenViewBuilder;
import com.samsung.context.sdk.samsunganalytics.LogBuilders$SettingBuilder;
import com.samsung.context.sdk.samsunganalytics.LogBuilders$SettingPrefBuilder;
import com.samsung.context.sdk.samsunganalytics.SamsungAnalytics;
import java.lang.ref.WeakReference;
import java.util.HashMap;

public class SALogProvider {
    public static final String KEY_SA_BLOCK_CALL_WHILE_RECORDING_TYPE = "5404";
    public static final String KEY_SA_PLAY_CONTINUOUSLY_TYPE = "5406";
    private static final String KEY_SA_PREFERENCES = "com.sec.android.app.voicenote_sa_preferences";
    private static final String KEY_SA_PREFERENCES_INITIALIZED = "sa_pref_initialized";
    public static final String KEY_SA_RECORDING_QUALITY_TYPE = "5402";
    public static final String KEY_SA_RECORD_AUDIO_IN_STEREO_TYPE = "5408";
    public static final String KEY_SA_STORAGE_LOCATION_TYPE = "5405";
    public static final String KEY_SA_USER_CATEGORY_STATUS = "5219";
    public static final String OFF = "0";

    /* renamed from: ON */
    public static final String f105ON = "1";
    public static final String QUALITY_HIGH = "1";
    public static final String QUALITY_LOW = "3";
    public static final String QUALITY_MID = "2";
    public static final String QUALITY_MMS = "4";
    public static final String STORAGE_DEVICE = "1";
    public static final String STORAGE_SD = "2";
    private static final String TAG = "SALogProvider";
    private static final String TRACKING_ID = "430-399-9953102";
    private static final String VERSION = "2.4";
    private static WeakReference<Context> mAppContext = null;
    private static boolean mIsConfigurationCalled = false;

    public static void setAppContext(WeakReference<Context> weakReference) {
        mAppContext = weakReference;
    }

    public static void setConfig(Application application) {
        Log.m19d(TAG, "set SALog Config TrackingId = 430-399-9953102 , Version = 2.4");
        Configuration configuration = new Configuration();
        configuration.setTrackingId(TRACKING_ID);
        configuration.setVersion(VERSION);
        configuration.enableAutoDeviceId();
        SamsungAnalytics.setConfiguration(application, configuration);
        initStatus();
        mIsConfigurationCalled = true;
    }

    public static void insertSALog(String str) {
        if (mIsConfigurationCalled) {
            Log.m19d(TAG, "set SALog ScreenID = " + str);
            SamsungAnalytics.getInstance().sendLog(((LogBuilders$ScreenViewBuilder) new LogBuilders$ScreenViewBuilder().setScreenView(str)).build());
        }
    }

    public static void insertSALog(String str, String str2, String str3, long j) {
        if (mIsConfigurationCalled) {
            Log.m19d(TAG, "set SALog ScreenID = " + str + " , event = " + str2 + " , detail = " + str3 + " , value = " + j);
            HashMap hashMap = new HashMap();
            hashMap.put("det", str3);
            SamsungAnalytics instance = SamsungAnalytics.getInstance();
            instance.sendLog(((LogBuilders$ScreenViewBuilder) new LogBuilders$ScreenViewBuilder().setScreenView(str)).build());
            LogBuilders$EventBuilder logBuilders$EventBuilder = (LogBuilders$EventBuilder) new LogBuilders$EventBuilder().setScreenView(str);
            logBuilders$EventBuilder.setEventName(str2);
            LogBuilders$EventBuilder logBuilders$EventBuilder2 = (LogBuilders$EventBuilder) logBuilders$EventBuilder.setDimension(hashMap);
            logBuilders$EventBuilder2.setEventValue(j);
            instance.sendLog(logBuilders$EventBuilder2.build());
        }
    }

    public static void insertSALog(String str, String str2, long j) {
        if (mIsConfigurationCalled) {
            Log.m19d(TAG, "set SALog ScreenID = " + str + " , event = " + str2 + " , value = " + j);
            SamsungAnalytics instance = SamsungAnalytics.getInstance();
            instance.sendLog(((LogBuilders$ScreenViewBuilder) new LogBuilders$ScreenViewBuilder().setScreenView(str)).build());
            LogBuilders$EventBuilder logBuilders$EventBuilder = (LogBuilders$EventBuilder) new LogBuilders$EventBuilder().setScreenView(str);
            logBuilders$EventBuilder.setEventName(str2);
            logBuilders$EventBuilder.setEventValue(j);
            instance.sendLog(logBuilders$EventBuilder.build());
        }
    }

    public static void insertSALog(String str, String str2, String str3) {
        if (mIsConfigurationCalled) {
            Log.m19d(TAG, "set SALog ScreenID = " + str + " , event = " + str2 + " , detail = " + str3);
            HashMap hashMap = new HashMap();
            hashMap.put("det", str3);
            SamsungAnalytics instance = SamsungAnalytics.getInstance();
            instance.sendLog(((LogBuilders$ScreenViewBuilder) new LogBuilders$ScreenViewBuilder().setScreenView(str)).build());
            LogBuilders$EventBuilder logBuilders$EventBuilder = (LogBuilders$EventBuilder) new LogBuilders$EventBuilder().setScreenView(str);
            logBuilders$EventBuilder.setEventName(str2);
            instance.sendLog(((LogBuilders$EventBuilder) logBuilders$EventBuilder.setDimension(hashMap)).build());
        }
    }

    public static void insertSALog(String str, String str2) {
        if (mIsConfigurationCalled) {
            Log.m19d(TAG, "set SALog ScreenID = " + str + " , event = " + str2);
            SamsungAnalytics instance = SamsungAnalytics.getInstance();
            instance.sendLog(((LogBuilders$ScreenViewBuilder) new LogBuilders$ScreenViewBuilder().setScreenView(str)).build());
            LogBuilders$EventBuilder logBuilders$EventBuilder = (LogBuilders$EventBuilder) new LogBuilders$EventBuilder().setScreenView(str);
            logBuilders$EventBuilder.setEventName(str2);
            instance.sendLog(logBuilders$EventBuilder.build());
        }
    }

    private static void initStatus() {
        WeakReference<Context> weakReference = mAppContext;
        if (weakReference != null && weakReference.get() != null) {
            SharedPreferences sharedPreferences = ((Context) mAppContext.get()).getSharedPreferences(KEY_SA_PREFERENCES, 0);
            if (!sharedPreferences.contains(KEY_SA_PREFERENCES_INITIALIZED)) {
                Log.m19d(TAG, "Initialize status preferences");
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putBoolean(KEY_SA_PREFERENCES_INITIALIZED, true);
                if (!sharedPreferences.contains(KEY_SA_USER_CATEGORY_STATUS)) {
                    edit.putInt(KEY_SA_USER_CATEGORY_STATUS, 0);
                }
                int intSettings = Settings.getIntSettings(Settings.KEY_REC_QUALITY, 1);
                if (intSettings == 0) {
                    edit.putString(KEY_SA_RECORDING_QUALITY_TYPE, QUALITY_LOW);
                } else if (intSettings == 1) {
                    edit.putString(KEY_SA_RECORDING_QUALITY_TYPE, "2");
                } else if (intSettings == 2) {
                    edit.putString(KEY_SA_RECORDING_QUALITY_TYPE, "1");
                } else if (intSettings != 3) {
                    edit.putString(KEY_SA_RECORDING_QUALITY_TYPE, "2");
                } else {
                    edit.putString(KEY_SA_RECORDING_QUALITY_TYPE, QUALITY_MMS);
                }
                if (Settings.getBooleanSettings(Settings.KEY_REC_CALL_REJECT, false)) {
                    edit.putString(KEY_SA_BLOCK_CALL_WHILE_RECORDING_TYPE, "1");
                } else {
                    edit.putString(KEY_SA_BLOCK_CALL_WHILE_RECORDING_TYPE, "0");
                }
                if (Settings.getBooleanSettings(Settings.KEY_PLAY_CONTINUOUSLY, false)) {
                    edit.putString(KEY_SA_PLAY_CONTINUOUSLY_TYPE, "1");
                } else {
                    edit.putString(KEY_SA_PLAY_CONTINUOUSLY_TYPE, "0");
                }
                if (Settings.getIntSettings(Settings.KEY_STORAGE, 0) == 0) {
                    edit.putString(KEY_SA_STORAGE_LOCATION_TYPE, "1");
                } else {
                    edit.putString(KEY_SA_STORAGE_LOCATION_TYPE, "2");
                }
                if (Settings.getBooleanSettings(Settings.KEY_REC_STEREO, false)) {
                    edit.putString(KEY_SA_RECORD_AUDIO_IN_STEREO_TYPE, "1");
                } else {
                    edit.putString(KEY_SA_RECORD_AUDIO_IN_STEREO_TYPE, "0");
                }
                edit.apply();
            }
        }
    }

    public static void registerStatus() {
        if (mIsConfigurationCalled) {
            Log.m19d(TAG, "registerStatus");
            LogBuilders$SettingPrefBuilder logBuilders$SettingPrefBuilder = new LogBuilders$SettingPrefBuilder();
            logBuilders$SettingPrefBuilder.addKey(KEY_SA_PREFERENCES, KEY_SA_USER_CATEGORY_STATUS);
            logBuilders$SettingPrefBuilder.addKey(KEY_SA_PREFERENCES, KEY_SA_RECORDING_QUALITY_TYPE);
            logBuilders$SettingPrefBuilder.addKey(KEY_SA_PREFERENCES, KEY_SA_BLOCK_CALL_WHILE_RECORDING_TYPE);
            logBuilders$SettingPrefBuilder.addKey(KEY_SA_PREFERENCES, KEY_SA_STORAGE_LOCATION_TYPE);
            logBuilders$SettingPrefBuilder.addKey(KEY_SA_PREFERENCES, KEY_SA_RECORD_AUDIO_IN_STEREO_TYPE);
            logBuilders$SettingPrefBuilder.addKey(KEY_SA_PREFERENCES, KEY_SA_PLAY_CONTINUOUSLY_TYPE);
            SamsungAnalytics.getInstance().registerSettingPref(logBuilders$SettingPrefBuilder.build());
        }
    }

    public static void insertStatusLog(String str, String str2) {
        if (mIsConfigurationCalled) {
            Log.m19d(TAG, "insertStatusLog key = " + str + " , value = " + str2);
            WeakReference<Context> weakReference = mAppContext;
            if (!(weakReference == null || weakReference.get() == null)) {
                SharedPreferences.Editor edit = ((Context) mAppContext.get()).getSharedPreferences(KEY_SA_PREFERENCES, 0).edit();
                edit.putString(str, str2);
                edit.apply();
            }
            LogBuilders$SettingBuilder logBuilders$SettingBuilder = new LogBuilders$SettingBuilder();
            logBuilders$SettingBuilder.set(str, str2);
            SamsungAnalytics.getInstance().sendLog(logBuilders$SettingBuilder.build());
        }
    }

    public static void insertStatusLog(String str, int i) {
        if (mIsConfigurationCalled) {
            Log.m19d(TAG, "insertStatusLog key = " + str + " , value = " + i);
            WeakReference<Context> weakReference = mAppContext;
            if (!(weakReference == null || weakReference.get() == null)) {
                SharedPreferences.Editor edit = ((Context) mAppContext.get()).getSharedPreferences(KEY_SA_PREFERENCES, 0).edit();
                edit.putInt(str, i);
                edit.apply();
            }
            LogBuilders$SettingBuilder logBuilders$SettingBuilder = new LogBuilders$SettingBuilder();
            logBuilders$SettingBuilder.set(str, i);
            SamsungAnalytics.getInstance().sendLog(logBuilders$SettingBuilder.build());
        }
    }
}
