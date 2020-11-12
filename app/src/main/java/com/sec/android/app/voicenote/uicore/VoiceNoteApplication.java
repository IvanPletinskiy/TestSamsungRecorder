package com.sec.android.app.voicenote.uicore;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
//import com.samsung.android.feature.SemFloatingFeature;
import com.sec.android.app.voicenote.bixby.BixbyExecutor;
import com.sec.android.app.voicenote.common.util.Trace;
import com.sec.android.app.voicenote.common.util.TrashHelper;
import com.sec.android.app.voicenote.main.VNMainActivity;
import com.sec.android.app.voicenote.p007ui.animation.AnimationFactory;
import com.sec.android.app.voicenote.p007ui.pager.PagerNormalFragment;
import com.sec.android.app.voicenote.provider.AssistantProvider;
import com.sec.android.app.voicenote.provider.ContactUsProvider;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.DBProvider;
import com.sec.android.app.voicenote.provider.DesktopModeProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.GdprProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.NavigationBarProvider;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.SettingsParser;
import com.sec.android.app.voicenote.provider.SimpleStorageProvider;
import com.sec.android.app.voicenote.provider.StorageProvider;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.provider.UPSMProvider;
import com.sec.android.app.voicenote.provider.UpdateProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.provider.WaveProvider;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.SimpleEngineManager;
import com.sec.android.app.voicenote.service.SimpleMetadataRepositoryManager;
import com.sec.android.diagmonagent.log.provider.DiagMonSDK;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;

public class VoiceNoteApplication extends Application {
    private static final String DIAGMON_SERVICE_ID = "vruy5va1ta";
    private static final String TAG = "VoiceNoteApplication";
    private static VoiceNoteApplication mInstance;
    private static int mSavedEvent;
    private static int mScene;
    private static AtomicInteger sessionCounter;
    /* access modifiers changed from: private */
    public int mActivityCnt = 0;
    /* access modifiers changed from: private */
    public Activity mTopActivity = null;

    static /* synthetic */ int access$008(VoiceNoteApplication voiceNoteApplication) {
        int i = voiceNoteApplication.mActivityCnt;
        voiceNoteApplication.mActivityCnt = i + 1;
        return i;
    }

    static /* synthetic */ int access$010(VoiceNoteApplication voiceNoteApplication) {
        int i = voiceNoteApplication.mActivityCnt;
        voiceNoteApplication.mActivityCnt = i - 1;
        return i;
    }

    public void onCreate() {
        Log.m26i(TAG, "onCreate !!");
        Trace.beginSection("VNApp.onCreate");
        super.onCreate();
        mInstance = this;
        Settings.setApplicationContext(new WeakReference(this));
        NavigationBarProvider.getInstance().setApplicationContext(this);
        Engine.getInstance().setApplicationContext(this);
        SimpleEngineManager.getInstance().setApplicationContext(this);
        UpdateProvider.getInstance().setApplicationContext(this);
        DesktopModeProvider.getInstance().setApplicationContext(this);
        StorageProvider.setApplicationContext(this);
        SimpleStorageProvider.setApplicationContext(this);
        SimpleMetadataRepositoryManager.getInstance().setApplicationContext(this);
        new Thread(new Runnable() {
            public final void run() {
                VoiceNoteApplication.this.lambda$onCreate$0$VoiceNoteApplication();
            }
        }).start();
        MediaSessionManager.getInstance().setApplicationContext(this);
        SimpleMediaSessionManager.getInstance().setApplicationContext(this);
        mSavedEvent = 2;
        if (isSupportDeviceCog()) {
            registerBixbyExecutor();
        }
        registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            public void onActivityCreated(Activity activity, Bundle bundle) {
            }

            public void onActivityDestroyed(Activity activity) {
            }

            public void onActivityPaused(Activity activity) {
            }

            public void onActivityResumed(Activity activity) {
            }

            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            }

            public void onActivityStarted(Activity activity) {
                VoiceNoteApplication.access$008(VoiceNoteApplication.this);
                Activity unused = VoiceNoteApplication.this.mTopActivity = activity;
            }

            public void onActivityStopped(Activity activity) {
                VoiceNoteApplication.access$010(VoiceNoteApplication.this);
                if (VoiceNoteApplication.this.mActivityCnt == 0) {
                    Activity unused = VoiceNoteApplication.this.mTopActivity = null;
                }
            }
        });
        sessionCounter = new AtomicInteger();
        Trace.endSection();
    }

    public void onTerminate() {
        Log.m26i(TAG, "onTerminate !!");
        mSavedEvent = 2;
        mInstance = null;
        super.onTerminate();
    }

    public static void saveScene(int i) {
        Log.m26i(TAG, "saveScene - scene : " + i);
        mScene = i;
    }

    public static int getScene() {
        return mScene;
    }

    public static void saveEvent(int i) {
        Log.m26i(TAG, "saveEvent in - event : " + i);
        if (!Engine.getInstance().isSimpleRecorderMode()) {
            mSavedEvent = convertEvent(i);
            Log.m26i(TAG, "saveEvent convert - event : " + mSavedEvent);
        }
    }

    public static int convertEvent(int i) {
        Log.m26i(TAG, "convertEvent - event : " + i + "scene : " + mScene);
        int i2 = mScene;
        if (i2 == 3) {
            switch (i) {
                case Event.PLAY_PAUSE:
                    return Event.MINI_PLAY_PAUSE;
                case Event.PLAY_RESUME:
                    return Event.MINI_PLAY_RESUME;
                case Event.PLAY_NEXT:
                    return Event.MINI_PLAY_NEXT;
                case Event.PLAY_PREV:
                    return Event.MINI_PLAY_PREV;
                default:
                    return i;
            }
        } else if (i2 != 15) {
            if (i2 != 6) {
                if (i2 == 7) {
                    switch (i) {
                        case Event.PLAY_PAUSE:
                            return Event.SEARCH_PLAY_PAUSE;
                        case Event.PLAY_RESUME:
                            return Event.SEARCH_PLAY_RESUME;
                        case Event.PLAY_NEXT:
                        case Event.PLAY_PREV:
                            return Event.SEARCH_PLAY_START;
                        default:
                            return i;
                    }
                } else if (i2 != 8) {
                    return i;
                } else {
                    if (i == 2002) {
                        return 1008;
                    }
                    if (i != 2003) {
                        return i;
                    }
                    return 1007;
                }
            } else if (i == 1002) {
                return Event.EDIT_RECORD_PAUSE;
            } else {
                if (i == 1003) {
                    return Event.EDIT_RECORD;
                }
                if (i != 2002) {
                    return i != 2003 ? i : Event.EDIT_PLAY_START;
                }
                return Event.EDIT_PLAY_PAUSE;
            }
        } else if (i != 2002) {
            return i != 2003 ? i : Event.TRASH_MINI_PLAY_RESUME;
        } else {
            return Event.TRASH_MINI_PLAY_PAUSE;
        }
    }

    public static int restoreEvent() {
        Log.m26i(TAG, "restoreEvent - event : " + mSavedEvent);
        int i = mSavedEvent;
        mSavedEvent = 2;
        return i;
    }

    public static VoiceNoteApplication getApplication() {
        return mInstance;
    }

    public Activity getTopActivity() {
        return this.mTopActivity;
    }

    private void registerBixbyExecutor() {
        new BixbyExecutor(this).addActionHandler();
    }

    public static String getApkInfo() {
        return "apk version : (Version : " + getApkVersionCode() + " : " + getApkVersionName() + ")";
    }

    public static String getApkName() {
        return mInstance.getPackageName();
    }

    public static String getApkVersionCode() {
        try {
            PackageManager packageManager = mInstance.getPackageManager();
            if (packageManager != null) {
                return String.valueOf(packageManager.getPackageInfo(mInstance.getPackageName(), 0).versionCode);
            }
            return "";
        } catch (PackageManager.NameNotFoundException e) {
            Log.m22e(TAG, "PackageManager.NameNotFoundException : " + e);
            return "";
        } catch (NullPointerException e2) {
            Log.m22e(TAG, "NullPointerException : " + e2);
            return "";
        }
    }

    public static String getApkVersionName() {
        try {
            PackageManager packageManager = mInstance.getPackageManager();
            if (packageManager == null) {
                return "";
            }
            PackageInfo packageInfo = packageManager.getPackageInfo(mInstance.getPackageName(), 0);
            if (packageInfo.versionName != null) {
                return packageInfo.versionName;
            }
            return "";
        } catch (PackageManager.NameNotFoundException e) {
            Log.m22e(TAG, "PackageManager.NameNotFoundException : " + e);
            return "";
        } catch (NullPointerException e2) {
            Log.m22e(TAG, "NullPointerException : " + e2);
            return "";
        }
    }

    public static synchronized String createNewSession() {
        String str;
        synchronized (VoiceNoteApplication.class) {
            str = "session#" + sessionCounter.incrementAndGet();
        }
        return str;
    }

    public static String getSimpleActivitySession() {
        return "session#" + sessionCounter.get();
    }

    /* access modifiers changed from: private */
    /* renamed from: initBG */
    public void lambda$onCreate$0$VoiceNoteApplication() {
        Log.m26i(TAG, "initialize in background");
        Trace.beginSection("VNApp.initBG");
        VoiceNoteFeature.init();
        VNMainActivity.init();
        PagerNormalFragment.init();
        DBProvider.getInstance().setApplicationContext(this);
        CursorProvider.getInstance().setApplicationContext(this);
        AnimationFactory.setApplicationContext(this);
        WaveProvider.getInstance().setApplicationContext(this);
        TrashHelper.getInstance().setApplicationContext(this);
        Trace.beginSection("VNApp.initBG2nd");
        GdprProvider.getInstance().setApplicationContext(this);
        GdprProvider.getInstance().isGdprCountry();
        ContactUsProvider.getInstance().setApplicationContext(this);
        ContactUsProvider.getInstance().isSupportedContactUs();
        NavigationBarProvider.getInstance().isDeviceSupportSoftNavigationBar();
        Trace.endSection();
        Trace.beginSection("VNApp.initBG3rd");
        UPSMProvider.getInstance().setApplicationContext(this);
        AssistantProvider.getInstance().setApplicationContext(this);
        SurveyLogProvider.setApplicationContext(this);
        SettingsParser.checkSettingsFile();
        Trace.endSection();
        if (!isTestRun()) {
            Trace.beginSection("VNApp.initBG4");
            SALogProvider.setAppContext(new WeakReference(mInstance));
            SALogProvider.setConfig(mInstance);
            SALogProvider.registerStatus();
            DiagMonSDK.setDefaultConfiguration(this, DIAGMON_SERVICE_ID);
            DiagMonSDK.enableUncaughtExceptionLogging(this);
            Trace.endSection();
        } else {
            Log.m26i(TAG, "Can not initialize Samsung Analytics. FINGERPRINT : " + Build.FINGERPRINT);
        }
        Trace.endSection();
    }

    private boolean isSupportDeviceCog() {
//        try {
//            return SemFloatingFeature.getInstance().getBoolean("SEC_FLOATING_FEATURE_COMMON_SUPPORT_BIXBY", false);
//        } catch (Exception unused) {
//            return false;
//        }
        return false;
    }

    private boolean isTestRun() {
        return "robolectric".equals(Build.FINGERPRINT);
    }
}
