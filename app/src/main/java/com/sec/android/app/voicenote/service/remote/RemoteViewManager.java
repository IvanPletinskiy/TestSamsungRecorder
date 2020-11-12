package com.sec.android.app.voicenote.service.remote;

import android.content.Context;
import android.content.Intent;

import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.MetadataRepository;
import com.sec.android.app.voicenote.service.VoiceNoteService;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;

//import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class RemoteViewManager implements Engine.OnEngineListener {
    private static final String TAG = "RemoteViewManager";
    private static RemoteViewManager mInstance;
    /* access modifiers changed from: private */
    public boolean isRegisteredNFCTouch = false;
    private AbsRemoteViewManager mAbsRemoteViewManager;
    /* access modifiers changed from: private */
    public Context mContext = null;
    private boolean mCoverInitializeState = false;
//    private ScoverManager.StateListener mCoverListener;
    private int mCurrentTime = 0;
    private int mDisplayTime = -1;
    private int mDisplayedRemoteType = 1;
    private boolean mIsEngineUpdateForNoti = false;
    private boolean mIsUpdate = true;
//    private ScoverManager.NfcLedCoverTouchListener mNfcTouch;
    private OnRecordChangedListener mOnRecordChangedListener;
    private int mPlayerState = 1;
    private int mRecorderState = 1;
    private int mRemoteViewState = 0;
//    private Scover mScover;
//    public ScoverManager mScoverManager;
    public boolean mSupportCover = true;

    public static class ManagerType {
        public static final int COVER = 0;
        public static final int NOTIFICATION = 1;
    }

    public interface OnRecordChangedListener {
        void finishPlayer();

        void onRecordDone(long j);
    }

    public static class RemoteViewState {
        public static final int EDIT = 4;
        public static final int NONE = 0;
        public static final int PAUSE = 3;
        public static final int PLAY = 2;
        public static final int RECORD = 1;
        public static final int TRANSLATE = 5;
    }

    public static class RemoteViewType {
        public static final int HIDE = 1;
        public static final int ON_CLEAR_COVER = 5;
        public static final int ON_LED_COVER = 7;
        public static final int ON_SVIEW_COVER = 4;
        public static final int ON_UNSUPPORT_COVER = 6;
    }

    public int getRemoteViewState(int i, int i2, int i3) {
        switch (i) {
            case 1:
            case 8:
            case 11:
                break;
            case 2:
                if (i3 != 1) {
                    return 2;
                }
                return i2 != 1 ? 1 : 0;
            case 3:
            case 4:
            case 7:
                return 2;
            case 6:
                return 4;
            case 12:
                return 5;
            default:
                return 0;
        }
        return 0;
    }

    private RemoteViewManager() {
        Log.m26i(TAG, "RemoteViewManager creator !!");
    }

    public static RemoteViewManager getInstance() {
        if (mInstance == null) {
            mInstance = new RemoteViewManager();
        }
        return mInstance;
    }

    public void setContext(Context context) {
        this.mContext = context;
        this.mCoverInitializeState = true;
        initCover();
    }

    public void registerRecordChangedListener(OnRecordChangedListener onRecordChangedListener) {
        this.mOnRecordChangedListener = onRecordChangedListener;
        initCover();
    }

    public void unregisterRecordChangedListener() {
        if (this.mOnRecordChangedListener != null) {
            this.mOnRecordChangedListener = null;
            if (!this.mCoverInitializeState) {
                release();
            }
        }
    }

    public boolean isCoverClosed() {
////        ScoverManager scoverManager;
////        ScoverState coverState;
//        if (!this.mSupportCover || (scoverManager = this.mScoverManager) == null || (coverState = scoverManager.getCoverState()) == null || coverState.getSwitchState()) {
//            return false;
//        }
        return true;
    }

    public void registerNFCTouchCoverListener() {
        Log.m26i(TAG, "registerNFCTouchCoverListener");
//        if (this.mScoverManager == null) {
//            Log.m26i(TAG, "registerNFCTouchCoverListener: mScoverManager is NULL");
//            return;
//        }
//        if (this.mNfcTouch == null) {
//            this.mNfcTouch = new ScoverManager.NfcLedCoverTouchListener() {
//                public void onCoverTapMid() {
//                    super.onCoverTapMid();
//                    if (RemoteViewManager.this.mContext == null) {
//                        Log.m22e(RemoteViewManager.TAG, "onCoverTapMid - context is null");
//                        return;
//                    }
//                    int playerState = Engine.getInstance().getPlayerState();
//                    int recorderState = Engine.getInstance().getRecorderState();
//                    Log.m26i(RemoteViewManager.TAG, "onCoverTapMid - playState : " + playerState + " - recordState : " + recorderState);
//                    Intent intent = new Intent("com.samsung.cover.REMOTEVIEWS_UPDATE");
//                    if (playerState == 3) {
//                        LocalBroadcastManager.getInstance(RemoteViewManager.this.mContext).sendBroadcast(new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_PLAY_PAUSE));
//                        intent.putExtra("voice_recorder_status", 3);
//                    } else if (recorderState == 2) {
//                        LocalBroadcastManager.getInstance(RemoteViewManager.this.mContext).sendBroadcast(new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_REC_PAUSE));
//                        intent.putExtra("voice_recorder_status", 3);
//                    } else if (recorderState == 3) {
//                        LocalBroadcastManager.getInstance(RemoteViewManager.this.mContext).sendBroadcast(new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_REC_RESUME));
//                        intent.putExtra("voice_recorder_status", 1);
//                    } else if (playerState == 4) {
//                        LocalBroadcastManager.getInstance(RemoteViewManager.this.mContext).sendBroadcast(new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_PLAY));
//                        intent.putExtra("voice_recorder_status", 2);
//                    }
//                    RemoteViewManager.this.mContext.sendBroadcastAsUser(intent, AndroidForWork.OWNER);
//                }
//            };
//        }
//        try {
//            this.mScoverManager.registerNfcTouchListener(7, this.mNfcTouch);
//            this.isRegisteredNFCTouch = true;
//        } catch (SsdkUnsupportedException e) {
//            Log.m22e(TAG, "registerNfcTouchListener - " + e.toString());
//        }
    }

    public void unregisterNFCTouchCoverListener() {
//        try {
//            this.mScoverManager.unregisterNfcTouchListener(this.mNfcTouch);
//            this.isRegisteredNFCTouch = false;
//        } catch (SsdkUnsupportedException e) {
//            Log.m22e(TAG, "unregisterNFCTouchCoverListener - " + e.toString());
//        }
//        this.mNfcTouch = null;
    }

    public void release() {
//        if (this.mSupportCover) {
//            this.mScoverManager.unregisterListener(this.mCoverListener);
//            unregisterNFCTouchCoverListener();
//            this.mScoverManager = null;
//            this.mCoverListener = null;
//        }
//        NotiRemoteViewManager.release();
//        mInstance = null;
//        this.mContext = null;
//        this.mScover = null;
        Engine.getInstance().unregisterListener(this);
    }

    private void initCover() {
//        if (!this.mCoverInitializeState || this.mOnRecordChangedListener == null) {
//            this.mScover = new Scover();
//            if (this.mContext == null) {
//                Log.m32w(TAG, "initCover - context is null");
//                this.mContext = VoiceNoteApplication.getApplication().getApplicationContext();
//            }
//            try {
//                this.mScover.initialize(this.mContext);
//            } catch (IllegalArgumentException unused) {
//                Log.m22e(TAG, "IllegalArgumentException : context is null");
//                this.mSupportCover = false;
//            } catch (SsdkUnsupportedException unused2) {
//                Log.m22e(TAG, "SsdkUnsupportedException : not supported device");
//                this.mSupportCover = false;
//            }
//            Engine.getInstance().registerListener(this);
//            if (this.mSupportCover) {
//                this.mScoverManager = new ScoverManager(this.mContext);
//                this.mCoverListener = new ScoverManager.StateListener() {
//                    public void onCoverStateChanged(ScoverState scoverState) {
//                        boolean switchState = scoverState.getSwitchState();
//                        Log.m19d(RemoteViewManager.TAG, "onCoverStateChanged : " + switchState);
//                        if (scoverState.getType() == 7) {
//                            CoverRemoteViewManager instance = CoverRemoteViewManager.getInstance();
//                            if (!scoverState.getAttachState()) {
//                                instance.setRunningInBackground(false);
//                                RemoteViewManager.this.unregisterNFCTouchCoverListener();
//                            } else if (!RemoteViewManager.this.isRegisteredNFCTouch) {
//                                RemoteViewManager.this.registerNFCTouchCoverListener();
//                            }
//                            if (switchState) {
//                                instance.setRunningInBackground(false);
//                            } else if (VoiceNoteService.Helper.connectionCount() == 0) {
//                                instance.setRunningInBackground(true);
//                            } else {
//                                instance.setRunningInBackground(false);
//                            }
//                        }
//                        RemoteViewManager.this.doWorkWithCover(switchState);
//                    }
//                };
//                this.mScoverManager.registerListener(this.mCoverListener);
//            }
//        }
    }

    /* access modifiers changed from: private */
    public void doWorkWithCover(boolean z) {
        OnRecordChangedListener onRecordChangedListener;
        if (!z && Engine.getInstance().isSimpleRecorderMode()) {
            if (Engine.getInstance().getRecorderState() != 1) {
                Engine.getInstance().setSimpleModeItem(Engine.getInstance().stopRecord(true, true));
                CursorProvider.getInstance().resetCurrentPlayingItemPosition();
            }
            if (Engine.getInstance().getPlayerState() != 1) {
                Engine.getInstance().stopPlay();
            }
            long simpleModeItem = Engine.getInstance().getSimpleModeItem();
            if (simpleModeItem >= 0 && (onRecordChangedListener = this.mOnRecordChangedListener) != null) {
                onRecordChangedListener.onRecordDone(simpleModeItem);
            }
        } else if (z || !Engine.getInstance().isSimplePlayerMode()) {
            int attachedCoverType = getAttachedCoverType();
            if (attachedCoverType == 1) {
                displayNotificationOnCover(z);
                this.mDisplayedRemoteType = 4;
            } else if (attachedCoverType == 7) {
                displayNotificationOnCover(z);
                this.mDisplayedRemoteType = 7;
            } else if (attachedCoverType != 8) {
                displayNotificationOnCover(z);
                this.mDisplayedRemoteType = 6;
            } else {
                displayNotificationOnCover(z);
                this.mDisplayedRemoteType = 5;
            }
        } else {
            OnRecordChangedListener onRecordChangedListener2 = this.mOnRecordChangedListener;
            if (onRecordChangedListener2 != null) {
                onRecordChangedListener2.finishPlayer();
            }
        }
    }

    public int getDisplayedRemoteType() {
        return this.mDisplayedRemoteType;
    }

    public int getAttachedCoverType() {
//        ScoverManager scoverManager;
//        ScoverState coverState;
//        if (!this.mSupportCover || (scoverManager = this.mScoverManager) == null || (coverState = scoverManager.getCoverState()) == null) {
//            return 2;
//        }
//        return coverState.getType();
        return 0;
    }

    private void displayNotificationOnCover(boolean z) {
        Context context = this.mContext;
        if (context == null) {
            Log.m26i(TAG, "displayNotificationOnCover: mContext is NULL");
        } else if (!z) {
            context.sendBroadcast(new Intent("com.sec.android.app.voicenote.cover_close"));
        } else {
            context.sendBroadcast(new Intent(VoiceNoteService.VOICENOTE_COVER_OPEN));
        }
    }

    public void enableUpdate(boolean z) {
        this.mIsUpdate = z;
        if (this.mRecorderState == 2) {
            update(2, 1);
        }
    }

    public static boolean isRunning() {
        return NotiRemoteViewManager.isRunning();
    }

    public void enableEngineUpdateForNoti(boolean z) {
        this.mIsEngineUpdateForNoti = z;
    }

    public void onEngineUpdate(int i, int i2, int i3) {
        int scene = VoiceNoteApplication.getScene();
        if (i == 1010) {
            this.mRecorderState = i2;
            this.mRemoteViewState = getRemoteViewState(scene, this.mRecorderState, this.mPlayerState);
            if (this.mIsEngineUpdateForNoti) {
                int i4 = this.mRecorderState;
                if (i4 == 3 || i4 == 2 || i4 == 4) {
                    start(1);
                } else {
                    stop(1);
                }
            }
            start(0);
        } else if (i == 1011) {
            this.mCurrentTime = i2 / 1000;
            if (this.mIsEngineUpdateForNoti) {
                int i5 = this.mDisplayTime;
                if (i5 == -1 || i5 != this.mCurrentTime) {
                    this.mDisplayTime = this.mCurrentTime;
                    update(2, 1);
                }
            }
        } else if (i == 1021) {
            int maxDurationRecord = Engine.getInstance().getMaxDurationRecord(MetadataRepository.getInstance().getRecordMode());
            if (maxDurationRecord != -1) {
                int i6 = maxDurationRecord / 1000;
                this.mDisplayTime = i6;
                this.mCurrentTime = i6;
                update(2, 1);
            }
        } else if (i == 2010) {
            this.mPlayerState = i2;
            if (this.mIsEngineUpdateForNoti) {
                this.mRemoteViewState = getRemoteViewState(scene, this.mRecorderState, this.mPlayerState);
                int i7 = this.mRemoteViewState;
                if (i7 == 2 || i7 == 1 || i7 == 5) {
                    if (this.mPlayerState == 5) {
                        hide(1);
                        this.mPlayerState = 1;
                    } else {
                        start(1);
                    }
                }
            }
            this.mRemoteViewState = getRemoteViewState(scene, 1, this.mPlayerState);
            start(0);
        } else if (i == 2012) {
            this.mRemoteViewState = getRemoteViewState(scene, 1, this.mPlayerState);
            if (this.mRemoteViewState == 5 || this.mPlayerState == 3) {
                this.mCurrentTime = i2 / 1000;
                if (this.mIsEngineUpdateForNoti) {
                    int i8 = this.mDisplayTime;
                    if (i8 == -1 || i8 != this.mCurrentTime) {
                        this.mDisplayTime = this.mCurrentTime;
                        update(2, 1);
                    }
                }
            } else if (this.mRecorderState == 4) {
                this.mCurrentTime = i2 / 1000;
                if (this.mIsEngineUpdateForNoti) {
                    int i9 = this.mDisplayTime;
                    if (i9 == -1 || i9 != this.mCurrentTime) {
                        this.mDisplayTime = this.mCurrentTime;
                        update(2, 1);
                    }
                }
            }
        } else if (i == 2017) {
            this.mPlayerState = 4;
            this.mRemoteViewState = getRemoteViewState(scene, 1, this.mPlayerState);
            start(0);
        }
    }

    public int getDisplayTime() {
        return this.mDisplayTime;
    }

    public int getCurrentTime() {
        return this.mCurrentTime;
    }

    public boolean getIsEnableUpdate() {
        return this.mIsUpdate;
    }

    public void setRemoteViewState(int i) {
        this.mRemoteViewState = getRemoteViewState(i, -1, -1);
    }

    public int getCoverWindowWidth() {
//        ScoverState coverState;
//        ScoverManager scoverManager = this.mScoverManager;
//        if (scoverManager == null || (coverState = scoverManager.getCoverState()) == null) {
//            return 0;
//        }
//        return coverState.getWindowWidth();
        return 0;
    }

    public int getCoverWindowHeight() {
//        ScoverState coverState;
//        ScoverManager scoverManager = this.mScoverManager;
//        if (scoverManager == null || (coverState = scoverManager.getCoverState()) == null) {
//            return 0;
//        }
//        return coverState.getWindowHeight();
        return 0;
    }

    public void start(int i) {
        this.mAbsRemoteViewManager = ManagerFactory.createRemoteViewManager(i, this.mContext);
        int i2 = this.mRemoteViewState;
        if (i2 != 0) {
            if (i2 != 1) {
                if (i2 == 2) {
                    this.mAbsRemoteViewManager.start(this.mRecorderState, this.mPlayerState, i2);
                    return;
                } else if (i2 != 4) {
                    if (i2 == 5) {
                        this.mAbsRemoteViewManager.start(this.mRecorderState, this.mPlayerState, i2);
                        return;
                    }
                    return;
                }
            }
            this.mAbsRemoteViewManager.start(this.mRecorderState, this.mPlayerState, this.mRemoteViewState);
            return;
        }
        this.mAbsRemoteViewManager.start(this.mRecorderState, this.mPlayerState, 0);
    }

    public void stop(int i) {
        this.mAbsRemoteViewManager = ManagerFactory.createRemoteViewManager(i, this.mContext);
        this.mAbsRemoteViewManager.stop(this.mRemoteViewState);
    }

    public void show(int i) {
        this.mRemoteViewState = getRemoteViewState(VoiceNoteApplication.getScene(), this.mRecorderState, this.mPlayerState);
        this.mAbsRemoteViewManager = ManagerFactory.createRemoteViewManager(i, this.mContext);
        int i2 = this.mRemoteViewState;
        if (i2 == 1 || i2 == 4) {
            this.mAbsRemoteViewManager.show(this.mRecorderState, this.mPlayerState, this.mRemoteViewState);
        } else if (i2 == 2) {
            this.mAbsRemoteViewManager.show(this.mRecorderState, this.mPlayerState, i2);
        } else if (i2 == 5 && Engine.getInstance().getTranslationState() != 1) {
            this.mAbsRemoteViewManager.show(this.mRecorderState, this.mPlayerState, this.mRemoteViewState);
        }
    }

    public void update(int i, int i2) {
        this.mRemoteViewState = getRemoteViewState(VoiceNoteApplication.getScene(), this.mRecorderState, this.mPlayerState);
        this.mAbsRemoteViewManager = ManagerFactory.createRemoteViewManager(i2, this.mContext);
        int i3 = this.mRemoteViewState;
        if (i3 != 1) {
            if (i3 == 2) {
                this.mAbsRemoteViewManager.update(this.mRecorderState, this.mPlayerState, i3, i);
                return;
            } else if (i3 != 4) {
                if (i3 == 5) {
                    this.mAbsRemoteViewManager.update(this.mRecorderState, this.mPlayerState, i3, i);
                    return;
                }
                return;
            }
        }
        this.mAbsRemoteViewManager.update(this.mRecorderState, this.mPlayerState, this.mRemoteViewState, i);
    }

    public void hide(int i) {
        this.mRemoteViewState = getRemoteViewState(VoiceNoteApplication.getScene(), this.mRecorderState, this.mPlayerState);
        this.mAbsRemoteViewManager = ManagerFactory.createRemoteViewManager(i, this.mContext);
        this.mAbsRemoteViewManager.hide(this.mRemoteViewState);
    }

    private static class ManagerFactory {
        private ManagerFactory() {
        }

        /* access modifiers changed from: private */
        public static AbsRemoteViewManager createRemoteViewManager(int i, Context context) {
            if (i == 0) {
                CoverRemoteViewManager instance = CoverRemoteViewManager.getInstance();
                CoverRemoteViewManager.getInstance().setContext(context);
                return instance;
            } else if (i != 1) {
                NotiRemoteViewManager instance2 = NotiRemoteViewManager.getInstance();
                NotiRemoteViewManager.getInstance().setContext(context);
                return instance2;
            } else {
                NotiRemoteViewManager instance3 = NotiRemoteViewManager.getInstance();
                NotiRemoteViewManager.getInstance().setContext(context);
                return instance3;
            }
        }
    }
}
