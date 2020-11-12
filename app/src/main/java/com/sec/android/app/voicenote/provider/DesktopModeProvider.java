package com.sec.android.app.voicenote.provider;

import android.content.Context;
import android.content.Intent;

import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.VoiceNoteService;
import com.sec.android.app.voicenote.service.remote.CoverRemoteViewManager;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;

import java.lang.reflect.Method;

public class DesktopModeProvider {
    private static final int DISPLAY_TYPE_DUAL = 102;
    private static final String TAG = "DesktopModeProvider";
    private static Context mAppContext;
//    private static SemDesktopModeManager.DesktopModeListener mDesktopModeListener;
//    private static SemDesktopModeManager mDesktopModeManager;
    private static volatile DesktopModeProvider mInstance;
    private Method mGetDisplayType;
    private boolean mIsRecordInDualView = false;
    private int mSemDesktopModeState = 0;

    private DesktopModeProvider() {
        Log.m19d(TAG, "DesktopModeProvider creator !!");
        mAppContext = VoiceNoteApplication.getApplication();
    }

    public static DesktopModeProvider getInstance() {
        if (mInstance == null) {
            synchronized (DesktopModeProvider.class) {
                if (mInstance == null) {
                    mInstance = new DesktopModeProvider();
                }
            }
        }
        return mInstance;
    }

    public void setApplicationContext(Context context) {
        mAppContext = context;
    }

    public static boolean isDesktopMode() {
        boolean z = false;
        if (!VoiceNoteFeature.FLAG_SUPPORT_KNOX_DESKTOP) {
            return false;
        }
//        mDesktopModeManager = (SemDesktopModeManager) mAppContext.getSystemService(SemDesktopModeManager.class);
//        SemDesktopModeManager semDesktopModeManager = mDesktopModeManager;
//        if (semDesktopModeManager != null && semDesktopModeManager.getDesktopModeState().enabled == 4) {
//            z = true;
//        }
//        Log.m19d(TAG, "isDesktopMode - isDesktop : " + z);
        return z;
    }

    public void checkDualView() {
//        if (mDesktopModeManager != null) {
//            try {
//                if (this.mGetDisplayType == null) {
//                    this.mGetDisplayType = Reflector.getMethod(SemDesktopModeState.class, "getDisplayType");
//                }
//                boolean z = false;
//                Object invoke = Reflector.invoke(mDesktopModeManager.getDesktopModeState(), this.mGetDisplayType, new Object[0]);
//                if (invoke instanceof Integer) {
//                    if (((Integer) invoke).intValue() == 102) {
//                        z = true;
//                    }
//                    this.mIsRecordInDualView = z;
//                    return;
//                }
//                this.mIsRecordInDualView = false;
//            } catch (NoClassDefFoundError e) {
//                Log.m24e(TAG, "NoClassDefFoundError:", (Throwable) e);
//            } catch (NoSuchMethodError e2) {
//                Log.m24e(TAG, "NoSuchMethodError:", (Throwable) e2);
//            }
//        }
    }

    public boolean isRecordeDualview() {
        return this.mIsRecordInDualView;
    }

    public boolean isLoadingDesktopMode() {
        return this.mSemDesktopModeState >= 10;
    }

    public void registerListener() {
        Log.m19d(TAG, "registerListener");
//        if (VoiceNoteFeature.FLAG_SUPPORT_KNOX_DESKTOP) {
//            mDesktopModeManager = (SemDesktopModeManager) mAppContext.getSystemService(SemDesktopModeManager.class);
//            SemDesktopModeManager semDesktopModeManager = mDesktopModeManager;
//            if (semDesktopModeManager != null) {
//                mDesktopModeListener = new SemDesktopModeManager.DesktopModeListener() {
//                    public final void onDesktopModeStateChanged(SemDesktopModeState semDesktopModeState) {
//                        DesktopModeProvider.this.lambda$registerListener$0$DesktopModeProvider(semDesktopModeState);
//                    }
//                };
//                semDesktopModeManager.registerListener(mDesktopModeListener);
//            }
//        }
    }

//    public /* synthetic */ void lambda$registerListener$0$DesktopModeProvider(SemDesktopModeState semDesktopModeState) {
//        int recorderState;
//        this.mSemDesktopModeState = semDesktopModeState.state;
//        Log.m26i(TAG, "onDesktopModeStateChanged " + semDesktopModeState.toString());
//        if (semDesktopModeState.state == 40) {
//            int recorderState2 = Engine.getInstance().getRecorderState();
//            if (recorderState2 == 2 || recorderState2 == 3) {
//                stopRecordingForDex();
//                return;
//            }
//            SimpleEngine activeEngine = SimpleEngineManager.getInstance().getActiveEngine();
//            if (activeEngine == null || !((recorderState = activeEngine.getRecorderState()) == 2 || recorderState == 3)) {
//                VoiceNoteObservable.getInstance().notifyObservers(18);
//                Observable.getInstance().notifyObservers(VoiceNoteApplication.getSimpleActivitySession(), 18);
//                return;
//            }
//            stopRecordingForDexOnSimpleMode();
//        }
//    }

    public void unregisterListener() {
//        SemDesktopModeManager semDesktopModeManager;
//        Log.m26i(TAG, "unregisterListener");
//        if (VoiceNoteFeature.FLAG_SUPPORT_KNOX_DESKTOP && (semDesktopModeManager = mDesktopModeManager) != null) {
//            semDesktopModeManager.unregisterListener(mDesktopModeListener);
//        }
    }

    private void stopRecordingForDex() {
        int recorderState = Engine.getInstance().getRecorderState();
        CoverRemoteViewManager.getInstance().hide(1);
        if (recorderState != 2 && recorderState != 3) {
            return;
        }
        if (VoiceNoteService.Helper.connectionCount() == 0) {
            Intent intent = new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_SAVE);
            intent.putExtra("desktopMode_changed", true);
            mAppContext.sendBroadcast(intent);
            VoiceNoteApplication.saveEvent(3);
            return;
        }
        VoiceNoteApplication.getApplication().getTopActivity().runOnUiThread($$Lambda$DesktopModeProvider$RH98bOm1A1KdPGBCm60ECUGdF5k.INSTANCE);
    }

    private void stopRecordingForDexOnSimpleMode() {
        VoiceNoteApplication.getApplication().getTopActivity().runOnUiThread($$Lambda$DesktopModeProvider$AxKmLHpxn41muiOIoXycsX4SHXE.INSTANCE);
    }
}
