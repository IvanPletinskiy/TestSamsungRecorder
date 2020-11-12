package com.sec.android.app.voicenote.service;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.Vibrator;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.Manifest;
import com.sec.android.app.voicenote.common.util.DeviceInfo;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.common.util.KeyguardManagerHelper;
import com.sec.android.app.voicenote.common.util.Trace;
import com.sec.android.app.voicenote.common.util.VRUtil;
import com.sec.android.app.voicenote.provider.CallRejectChecker;
import com.sec.android.app.voicenote.provider.DesktopModeProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.PhoneStateProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.StorageProvider;
import com.sec.android.app.voicenote.provider.ViewProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.receiver.AudioDeviceReceiver;
import com.sec.android.app.voicenote.receiver.VoiceNoteIntentReceiver;
import com.sec.android.app.voicenote.service.AudioFormat;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.IVoiceNoteService;
import com.sec.android.app.voicenote.service.helper.DeviceLockedStateHelper;
import com.sec.android.app.voicenote.service.recognizer.VoiceWorker;
import com.sec.android.app.voicenote.service.remote.RemoteViewManager;
import com.sec.android.app.voicenote.uicore.MediaSessionManager;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.Hashtable;

public class VoiceNoteService extends Service implements Engine.OnEngineListener {
    public static final String BACKGROUND_VOICENOTE_ACCEPT_CALL = "com.samsung.telecom.IncomingCallAnsweredDuringRecord";
    public static final String BACKGROUND_VOICENOTE_CANCEL = "com.sec.android.app.voicenote.rec_cancel";
    public static final String BACKGROUND_VOICENOTE_CANCEL_DIALOG = "com.sec.android.app.voicenote.rec_cancel.dialog";
    public static final String BACKGROUND_VOICENOTE_CANCEL_DIALOG_CLOSE = "com.sec.android.app.voicenote.rec_cancel.dialog_close";
    public static final String BACKGROUND_VOICENOTE_CANCEL_KEYGUARD = "com.sec.android.app.voicenote.rec_cancel.keyguard";
    public static final String BACKGROUND_VOICENOTE_EDIT_CANCEL_KEYGUARD = "com.sec.android.app.voicenote.edit_cancel.keyguard";
    public static final String BACKGROUND_VOICENOTE_HIDE_NOTIFICATION = "com.sec.android.app.voicenote.hide_notification";
    public static final String BACKGROUND_VOICENOTE_NO_ACTION = "com.sec.android.app.voicenote.no_action";
    public static final String BACKGROUND_VOICENOTE_PLAY = "com.sec.android.app.voicenote.play";
    public static final String BACKGROUND_VOICENOTE_PLAY_NEXT = "com.sec.android.app.voicenote.play_next";
    public static final String BACKGROUND_VOICENOTE_PLAY_PAUSE = "com.sec.android.app.voicenote.play_pause";
    public static final String BACKGROUND_VOICENOTE_PLAY_PREV = "com.sec.android.app.voicenote.play_prev";
    public static final String BACKGROUND_VOICENOTE_PLAY_STOP = "com.sec.android.app.voicenote.play_stop";
    public static final String BACKGROUND_VOICENOTE_PLAY_STOP_QUIT = "com.sec.android.app.voicenote.play_stop.quit";
    public static final String BACKGROUND_VOICENOTE_PLAY_TOGGLE = "com.sec.android.app.voicenote.play_toggle";
    public static final String BACKGROUND_VOICENOTE_QUICK_PANEL_HIDE = "com.sec.android.app.voicenote.quick_panel_hide";
    public static final String BACKGROUND_VOICENOTE_QUICK_PANEL_SHOW = "com.sec.android.app.voicenote.quick_panel_show";
    public static final String BACKGROUND_VOICENOTE_REC_NEW = "com.sec.android.app.voicenote.rec_new";
    public static final String BACKGROUND_VOICENOTE_REC_PAUSE = "com.sec.android.app.voicenote.rec_pause";
    public static final String BACKGROUND_VOICENOTE_REC_PLAY_TOGGLE = "com.sec.android.app.voicenote.rec_play_toggle";
    public static final String BACKGROUND_VOICENOTE_REC_RESUME = "com.sec.android.app.voicenote.rec_resume";
    public static final String BACKGROUND_VOICENOTE_SAVE = "com.sec.android.app.voicenote.noti_rec_save";
    public static final String BACKGROUND_VOICENOTE_SAVE_BY_OTHER_APP = "com.sec.android.app.voicenote.rec_save";
    public static final String BACKGROUND_VOICENOTE_STANDBY = "com.sec.android.app.voicenote.standby";
    public static final String BACKGROUND_VOICENOTE_TRANSLATION_CANCEL_KEYGUARD = "com.sec.android.app.voicenote.translation_cancel.keyguard";
    public static final String BACKGROUND_VOICENOTE_TRANSLATION_FILE_PLAY = "com.sec.android.app.voicenote.translation_file_play";
    public static final String BACKGROUND_VOICENOTE_TRANSLATION_SAVE = "com.sec.android.app.voicenote.translation_save";
    public static final String BACKGROUND_VOICENOTE_TRANSLATION_TOGGLE = "com.sec.android.app.voicenote.translation_toggle";
    public static final String BACKGROUND_VOICENOTE_UPDATE_NOTIFICATION = "com.sec.android.app.voicenote.update_notification";
    public static final int INFO_CLEAR_DIALOG = 51;
    public static final int INFO_MINI_PLAY_PAUSE = 21;
    public static final int INFO_MINI_PLAY_RESUME = 22;
    public static final int INFO_MODE_NOT_SUPPORTED = 41;
    public static final int INFO_PLAY_COMPLETE = 14;
    public static final int INFO_PLAY_NEXT_FILE = 12;
    public static final int INFO_PLAY_PAUSE = 15;
    public static final int INFO_PLAY_PREV_FILE = 13;
    public static final int INFO_PLAY_RESUME = 16;
    public static final int INFO_PLAY_STOP_BACKGROUND = 11;
    public static final int INFO_RECORD_CANCEL_BACKGROUND = 2;
    public static final int INFO_RECORD_PAUSE_BACKGROUND = 3;
    public static final int INFO_RECORD_RESUME_BACKGROUND = 4;
    public static final int INFO_RECORD_STOP_BACKGROUND = 1;
    public static final int INFO_TRANSLATION_CANCEL_BACKGROUND = 63;
    public static final int INFO_TRANSLATION_PAUSE = 61;
    public static final int INFO_TRANSLATION_RESUME = 62;
    public static final int NETWORK_OFF = 34;
    public static final int NETWORK_ON = 33;
    public static final String PACKAGE_VOICENOTE = "com.sec.android.app.voicenote";
    public static final int SD_MOUNT = 31;
    public static final int SD_UNMOUNT = 32;
    private static final String TAG = "VoiceNoteService";
    private static final long THRESHOLD_CAMERA_OPEN_STOP_RECORDING_IMMEDIATELY = 1000;
    public static final String VOICENOTE_CAMERA_START = "com.sec.android.app.camera.ACTION_CAMERA_START";
    public static final String VOICENOTE_CAMERA_STOP = "com.sec.android.app.camera.ACTION_CAMERA_STOP";
    public static final String VOICENOTE_CLEAR_DIALOG = "com.sec.android.app.voicenote.clear_dialog";
    public static final String VOICENOTE_COVER_CLOSE = "com.sec.android.app.voicenote.cover_close";
    public static final String VOICENOTE_COVER_OPEN = "com.sec.android.app.voicenote.cover_open";
    public static final String VOICENOTE_DEVICE_LOCKED = "com.sec.android.app.voicenote.device_locked";
    public static final String VOICENOTE_DEVICE_STORAGE_LOW = "com.sec.android.app.voicenote.low_storage";
    public static final String VOICENOTE_DEVICE_UNLOCKED = "com.sec.android.app.voicenote.device_unlocked";
    public static final String VOICENOTE_HIDE_MODE_NOT_SUPPORTED = "com.sec.android.app.voicenote.hide_mode_not_supported";
    public static final String VOICENOTE_NETWORK_OFF = "com.sec.android.app.voicenote.network_off";
    public static final String VOICENOTE_NETWORK_ON = "com.sec.android.app.voicenote.network_on";
    public static final String VOICENOTE_SCREEN_OFF = "com.sec.android.app.voicenote.screen_off";
    public static final String VOICENOTE_SCREEN_ON = "com.sec.android.app.voicenote.screen_on";
    public static final String VOICENOTE_SD_MOUNT = "com.sec.android.app.voicenote.sd_mount";
    public static final String VOICENOTE_SD_UNMOUNT = "com.sec.android.app.voicenote.sd_unmount";
    public static final String VOICENOTE_SHOW_MODE_NOT_SUPPORTED = "com.sec.android.app.voicenote.show_mode_not_supported";
    private AudioDeviceReceiver mAudioDeviceReceiver = null;
    private IVoiceNoteService.Stub mBinder = null;
    private BroadcastReceiver mBroadcastReceiverNotification;
    private BroadcastReceiver mBroadcastReceiverNotificationCamera;
    private RemoteCallbackList<IVoiceNoteServiceCallback> mCallbacks = null;
    /* access modifiers changed from: private */
    public CameraActionHandler mCameraActionHandler = null;
    private AlertDialog mCancelDialog = null;
    private Dialog mCancelDialogOnCover = null;
    private int mCompleteCount = 0;
    private long mCurrentTime = 0;
    private DeviceLockedStateHelper mDeviceLockedStateHelper = null;
//    private SemWindowManager.FoldStateListener mFoldStateListener;
    /* access modifiers changed from: private */
    public boolean mIsDelayingWhenSpecialAppClosed;
    /* access modifiers changed from: private */
    public boolean mIsOpenedSpecialApp = false;
    /* access modifiers changed from: private */
    public long mLastCameraEvent = 0;
    private long mLastUpdateLogTime = 0;
    private boolean mNextPrevEnable = true;
    private AlertDialog mRejectCallInfoDialog = null;
    /* access modifiers changed from: private */
    public int mRemoteType = 1;
    private AlertDialog mTranslationCancelDialog = null;
    private VoiceNoteIntentReceiver mVoiceNoteIntentReceiver = null;
    /* access modifiers changed from: private */
    public AlertDialog mWarningMuteDetectedDialog = null;

    public static class UpdateType {
        public static final int HIDE_NOTIFICATION = 5;
        public static final int UPDATE_DEVICE_LOCKED = 11;
        public static final int UPDATE_DEVICE_UNLOCKED = 12;
        public static final int UPDATE_DURATION = 2;
        public static final int UPDATE_LANGUAGE = 7;
        public static final int UPDATE_NETWORK_OFF = 8;
        public static final int UPDATE_NOTIFICATION = 6;
        public static final int UPDATE_TITLE = 1;
        public static final int UPDATE_TRANSLATION_COMPLETE = 10;
        public static final int UPDATE_TRANSLATION_CONVERTING = 9;
    }

    public void onEngineUpdate(int i, int i2, int i3) {
        this.mCurrentTime = System.currentTimeMillis();
        if (this.mCurrentTime - this.mLastUpdateLogTime > THRESHOLD_CAMERA_OPEN_STOP_RECORDING_IMMEDIATELY) {
            Log.m26i(TAG, "onEngineUpdate - status : " + i + " arg1 : " + i2);
            this.mLastUpdateLogTime = this.mCurrentTime;
        }
        if (i == 1015) {
            sendMessageCallback(16, -1);
        } else if (i == 1016) {
            sendMessageCallback(15, -1);
        } else if (i != 2010) {
            if (i != 2011) {
                if (i == 2016) {
                    if (Helper.connectionCount() == 0) {
                        VoiceNoteApplication.saveEvent(Event.EDIT_PLAY_PAUSE);
                    }
                    sendMessageCallback(14, -1);
                } else if (i != 3010) {
                    switch (i) {
                        case 1020:
                            if (i2 == 1006) {
                                Log.m19d(TAG, "onEngineUpdate - cancel record");
                                Engine.getInstance().cancelRecord();
                                VoiceNoteApplication.saveEvent(1006);
                                sendMessageCallback(2, -1);
                                return;
                            }
                            sendMessageCallback(1, -1);
                            if (Helper.connectionCount() == 0) {
                                VoiceNoteApplication.saveEvent(3);
                                return;
                            }
                            return;
                        case 1021:
                        case 1022:
                            if (i2 == 1006) {
                                Log.m19d(TAG, "onEngineUpdate - cancel record");
                                Engine.getInstance().cancelRecord();
                                VoiceNoteApplication.saveEvent(1006);
                                sendMessageCallback(2, -1);
                                return;
                            } else if (VoiceNoteApplication.getScene() == 6) {
                                sendMessageCallback(3, i);
                                if (Helper.connectionCount() == 0) {
                                    VoiceNoteApplication.saveEvent(Event.EDIT_RECORD_PAUSE);
                                    return;
                                }
                                return;
                            } else {
                                sendMessageCallback(1, -1);
                                if (Helper.connectionCount() == 0) {
                                    VoiceNoteApplication.saveEvent(3);
                                    return;
                                }
                                return;
                            }
                        default:
                            switch (i) {
                                case 1024:
                                    Toast.makeText(this, C0690R.string.call_accept_info, 1).show();
                                    return;
                                case 1025:
                                    showMuteDetectedDialog(false);
                                    return;
                                case SimpleRecorder.INFO_NO_SOUND_DETECT_VIBRATE /*1026*/:
                                    showMuteDetectedDialog(true);
                                    return;
                                default:
                                    return;
                            }
                    }
                } else if (i2 != 2) {
                    if (i2 == 18) {
                        this.mCompleteCount++;
                        if (this.mCompleteCount != 1 || !VoiceWorker.getInstance().hasSttFragmentListener()) {
                            Log.m26i(TAG, "onEngineUpdate - has no sttFragment listener");
                            saveFileAfterWriteSttData();
                        }
                    } else if (i2 == 20) {
                        if (this.mCompleteCount != 0 || !VoiceWorker.getInstance().hasSttFragmentListener()) {
                            saveFileAfterWriteSttData();
                            return;
                        }
                        Log.m26i(TAG, "onEngineUpdate - SAVE_AFTER_WRITE_STT_DATA : Completed 1st");
                        this.mCompleteCount++;
                    }
                } else if (VoiceNoteApplication.getScene() == 6 && !StorageProvider.isExistFile(Engine.getInstance().getOriginalFilePath())) {
                    Log.m22e(TAG, "OVERWRITE_ERROR - OriginalFile is not exist.");
                    if (Helper.connectionCount() == 0) {
                        VoiceNoteApplication.saveEvent(3);
                    } else {
                        VoiceNoteObservable.getInstance().notifyObservers(3);
                    }
                    hideNotification();
                }
            } else if (VoiceNoteApplication.getScene() != 12) {
                if (Helper.connectionCount() == 0) {
                    VoiceNoteApplication.saveEvent(Event.PLAY_PAUSE);
                }
                sendMessageCallback(14, -1);
            } else if (Helper.connectionCount() == 0) {
                hideTranslationCancelDialog();
                saveBackgroundTranslationFile();
            } else {
                Engine.getInstance().pauseTranslation(true);
                VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.TRANSLATION_PAUSE));
            }
        } else if (i2 != 4) {
            if (i2 == 5) {
                if (i3 == -1007) {
                    Toast.makeText(this, getString(C0690R.string.file_corrupt_or_not_supported, new Object[]{Engine.getInstance().getCurrentFileName()}), 0).show();
                    new Handler().postDelayed(new Runnable() {
                        public final void run() {
                            VoiceNoteService.this.lambda$onEngineUpdate$0$VoiceNoteService();
                        }
                    }, 700);
                } else if (i3 == -1) {
                    sendMessageCallback(11, -1);
                    if (Helper.connectionCount() == 0) {
                        VoiceNoteApplication.saveEvent(3);
                    }
                }
            }
        } else if (Helper.connectionCount() != 0) {
            sendMessageCallback(15, -1);
        } else if (VoiceNoteApplication.getScene() == 12) {
            VoiceNoteApplication.saveEvent(Event.TRANSLATION_PAUSE);
        } else {
            VoiceNoteApplication.saveEvent(Event.PLAY_PAUSE);
        }
    }

    public /* synthetic */ void lambda$onEngineUpdate$0$VoiceNoteService() {
        sendMessageCallback(11, -1);
        if (Helper.connectionCount() == 0) {
            VoiceNoteApplication.saveEvent(3);
        }
    }

    public IBinder onBind(Intent intent) {
        Log.m26i(TAG, "onBind");
        return this.mBinder;
    }

    public boolean onUnbind(Intent intent) {
        Log.m26i(TAG, "onUnbind");
        if (Helper.connectionCount() == 0 && !RemoteViewManager.isRunning()) {
            Log.m26i(TAG, "onUnbind connectionCount is zero");
            if (Engine.getInstance().getRecorderState() == 2) {
                Log.m32w(TAG, "onUnbind - SKIP to stopSelf by RecorderState.RECORDING");
                showNotification();
            } else {
                stopSelf();
            }
        }
        return super.onUnbind(intent);
    }

    public void onCreate() {
        super.onCreate();
        Trace.beginSection("VNService.onCreate");
        Log.m26i(TAG, "onCreate");
        this.mCallbacks = new RemoteCallbackList<>();
        this.mBinder = new IVoiceNoteServiceStub(this, this.mCallbacks);
        this.mVoiceNoteIntentReceiver = new VoiceNoteIntentReceiver(this);
        this.mVoiceNoteIntentReceiver.registerListener();
        this.mAudioDeviceReceiver = new AudioDeviceReceiver(this);
        this.mAudioDeviceReceiver.registerListener();
        registerBroadcastReceiverNotification(true);
        registerBroadcastCamera(true);
        MediaSessionManager.getInstance().createMediaSession();
        RemoteViewManager.getInstance().setContext(this);
        Engine.getInstance().registerListener(this);
        DesktopModeProvider.getInstance().registerListener();
        new Thread(new Runnable() {
            public final void run() {
                VoiceNoteService.this.lambda$onCreate$1$VoiceNoteService();
            }
        }).start();
        if (Engine.getInstance().getPlayerState() == 1 && Engine.getInstance().getRecorderState() == 1) {
            hideNotification();
            hideCover();
            Engine.getInstance().enableSystemSound();
        }
        this.mCameraActionHandler = new CameraActionHandler();
        Trace.endSection();
    }

    public /* synthetic */ void lambda$onCreate$1$VoiceNoteService() {
        setCallRejectingServiceEnabled(true, false);
        if (this.mDeviceLockedStateHelper == null) {
            this.mDeviceLockedStateHelper = new DeviceLockedStateHelper(this);
        }
        this.mDeviceLockedStateHelper.startListenLockedStateChange();
        if (VoiceNoteFeature.FLAG_IS_BLOOM()) {
            registerFoldStateListener();
        }
        getRecorderFileCount();
    }

    private void getRecorderFileCount() {
        File[] listFiles;
        Log.m26i(TAG, "getRecorderFileCount");
        File file = new File(StorageProvider.getVoiceRecorderPath(2));
        if (file.exists() && file.isDirectory() && (listFiles = file.listFiles()) != null && listFiles.length > 0) {
            int i = 0;
            int i2 = 0;
            int i3 = 0;
            int i4 = 0;
            for (File name : listFiles) {
                String name2 = name.getName();
                if (name2.endsWith(AudioFormat.ExtType.EXT_M4A)) {
                    i++;
                } else if (name2.endsWith(AudioFormat.ExtType.EXT_AMR)) {
                    i2++;
                } else if (name2.endsWith(AudioFormat.ExtType.EXT_3GA)) {
                    i3++;
                } else if (name2.endsWith(".txt")) {
                    i4++;
                }
            }
            Log.m26i(TAG, "File count in path: " + StorageProvider.getVoiceRecorderPath(2) + " - m4a: " + i + " - amr: " + i2 + " - 3ga: " + i3 + " - txt: " + i4 + " - all: " + listFiles.length);
        }
    }

    private void registerFoldStateListener() {
//        this.mFoldStateListener = new SemWindowManager.FoldStateListener() {
//            public void onFoldStateChanged(boolean z) {
//                Log.m26i(VoiceNoteService.TAG, "onFoldStateChanged isFolded = " + z);
//                if (!z) {
//                    int playerState = Engine.getInstance().getPlayerState();
//                    boolean z2 = false;
//                    boolean z3 = Engine.getInstance().getRecorderState() == 1 && (playerState == 3 || playerState == 4);
//                    if (!KeyguardManagerHelper.isKeyguardLockedBySecure() || !z3) {
//                        z2 = true;
//                    }
//                    if (z2) {
//                        VoiceNoteService.this.showNotification();
//                    }
//                }
//            }
//
//            public void onTableModeChanged(boolean z) {
//                Log.m26i(VoiceNoteService.TAG, "onTableModeChanged isTableMode = " + z);
//            }
//        };
//        SemWindowManager.getInstance().registerFoldStateListener(this.mFoldStateListener, (Handler) null);
    }

    private void unregisterFoldStateListener() {
//        SemWindowManager.getInstance().unregisterFoldStateListener(this.mFoldStateListener);
    }

    public void onDestroy() {
        Log.m26i(TAG, "onDestroy");
        MediaSessionManager.getInstance().destroyMediaSession();
        RemoteViewManager.getInstance().release();
        VoiceNoteIntentReceiver voiceNoteIntentReceiver = this.mVoiceNoteIntentReceiver;
        if (voiceNoteIntentReceiver != null) {
            voiceNoteIntentReceiver.unregisterListener();
            this.mVoiceNoteIntentReceiver = null;
        }
        AudioDeviceReceiver audioDeviceReceiver = this.mAudioDeviceReceiver;
        if (audioDeviceReceiver != null) {
            audioDeviceReceiver.unregisterListener();
            this.mAudioDeviceReceiver = null;
        }
        CameraActionHandler cameraActionHandler = this.mCameraActionHandler;
        if (cameraActionHandler != null) {
            cameraActionHandler.removeMessages(1);
            this.mCameraActionHandler.removeMessages(2);
            this.mCameraActionHandler = null;
        }
//        if (VoiceNoteFeature.FLAG_IS_BLOOM() && this.mFoldStateListener != null) {
//            unregisterFoldStateListener();
//        }
        Engine.getInstance().setWiredHeadSetConnected(false);
        Engine.getInstance().setBluetoothHeadSetConnected(false);
        this.mCallbacks = null;
        Engine.getInstance().unregisterListener(this);
        registerBroadcastReceiverNotification(false);
        registerBroadcastCamera(false);
        DesktopModeProvider.getInstance().unregisterListener();
        hideNotification();
        DeviceLockedStateHelper deviceLockedStateHelper = this.mDeviceLockedStateHelper;
        if (deviceLockedStateHelper != null) {
            deviceLockedStateHelper.stopListenLockedStateChange();
            this.mDeviceLockedStateHelper = null;
        }
        setCallRejectingServiceEnabled(false, false);
        super.onDestroy();
    }

    /* access modifiers changed from: private */
    public void showRecordCancelDialog(boolean z) {
        if (this.mCancelDialog == null) {
            int i = z ? C0690R.string.edit_title : C0690R.string.stop_recording_message;
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, C0690R.style.DialogForService));
            builder.setMessage(getString(i));
            builder.setPositiveButton(C0690R.string.save, new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    VoiceNoteService.this.lambda$showRecordCancelDialog$2$VoiceNoteService(dialogInterface, i);
                }
            });
            builder.setNegativeButton(C0690R.string.discard, new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    VoiceNoteService.this.lambda$showRecordCancelDialog$3$VoiceNoteService(dialogInterface, i);
                }
            });
            builder.setNeutralButton(C0690R.string.cancel, (DialogInterface.OnClickListener) null);
            this.mCancelDialog = builder.create();
            this.mCancelDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    VoiceNoteService.this.lambda$showRecordCancelDialog$4$VoiceNoteService(dialogInterface);
                }
            });
            this.mCancelDialog.setCanceledOnTouchOutside(true);
            this.mCancelDialog.getWindow().setType(2014);
            this.mCancelDialog.show();
            this.mCancelDialog.getButton(-1).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
            this.mCancelDialog.getButton(-2).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
            this.mCancelDialog.getButton(-3).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
        }
    }

    public /* synthetic */ void lambda$showRecordCancelDialog$2$VoiceNoteService(DialogInterface dialogInterface, int i) {
        Log.m29v(TAG, "save recording in quickpanel while recording or pause by stop recording dialog");
        String originalFilePath = Engine.getInstance().getOriginalFilePath();
        Log.m19d(TAG, "SAVE - originalName : " + originalFilePath + " scene : " + VoiceNoteApplication.getScene());
        hideNotification();
        if (originalFilePath != null && !originalFilePath.isEmpty() && VoiceNoteApplication.getScene() == 6) {
            String name = new File(originalFilePath).getName();
            Engine.getInstance().setUserSettingName(name.substring(0, name.lastIndexOf(46)));
        }
        Engine.getInstance().stopRecord(true, true);
        sendMessageCallback(1, -1);
        VoiceNoteApplication.saveEvent(3);
        if (VoiceNoteFeature.FLAG_SUPPORT_SHOW_REJECT_CALL_NUMBER && CallRejectChecker.getInstance().getRejectCallCount() != 0) {
            showRejectCallInfoDialog();
        }
        stopSelf();
    }

    public /* synthetic */ void lambda$showRecordCancelDialog$3$VoiceNoteService(DialogInterface dialogInterface, int i) {
        Log.m29v(TAG, "cancel recording in quickpanel while recording or pause by stop recording dialog");
        Engine.getInstance().cancelRecord();
        sendMessageCallback(2, -1);
        if (Helper.connectionCount() == 0) {
            VoiceNoteApplication.saveScene(1);
        }
        VoiceNoteApplication.saveEvent(4);
        hideNotification();
        if (VoiceNoteFeature.FLAG_SUPPORT_SHOW_REJECT_CALL_NUMBER && CallRejectChecker.getInstance().getRejectCallCount() != 0) {
            showRejectCallInfoDialog();
        }
        stopSelf();
        Toast.makeText(this, C0690R.string.recording_discarded, 0).show();
    }

    public /* synthetic */ void lambda$showRecordCancelDialog$4$VoiceNoteService(DialogInterface dialogInterface) {
        this.mCancelDialog = null;
    }

    /* access modifiers changed from: private */
    public void showRecordCancelDialogOnCover(boolean z) {
        hideRecordCancelDialog();
        this.mCancelDialogOnCover = new Dialog(new ContextThemeWrapper(this, C0690R.style.DialogForService));
        this.mCancelDialogOnCover.requestWindowFeature(1);
        this.mCancelDialogOnCover.getWindow().setType(2099);
        this.mCancelDialogOnCover.setCancelable(true);
        this.mCancelDialogOnCover.setContentView(C0690R.layout.dialog_recording_cancel_on_cover);
        this.mCancelDialogOnCover.getWindow().setBackgroundDrawableResource(C0690R.C0691color.main_window_bg);
        if (this.mRemoteType == 4) {
            this.mCancelDialogOnCover.getWindow().setType(2099);
            this.mCancelDialogOnCover.getWindow().setLayout(RemoteViewManager.getInstance().getCoverWindowWidth(), RemoteViewManager.getInstance().getCoverWindowHeight());
            this.mCancelDialogOnCover.getWindow().getDecorView().setPadding(getResources().getDimensionPixelSize(C0690R.dimen.cover_dialog_start_padding), 0, getResources().getDimensionPixelSize(C0690R.dimen.cover_dialog_start_padding), 0);
            this.mCancelDialogOnCover.getWindow().clearFlags(2);
            WindowManager.LayoutParams attributes = this.mCancelDialogOnCover.getWindow().getAttributes();
            attributes.windowAnimations = 0;
            this.mCancelDialogOnCover.getWindow().setAttributes(attributes);
            this.mCancelDialogOnCover.getWindow().addFlags(524288);
        }
        Button button = (Button) this.mCancelDialogOnCover.getWindow().getDecorView().findViewById(C0690R.C0693id.cover_canceldialog_ok);
        Button button2 = (Button) this.mCancelDialogOnCover.getWindow().getDecorView().findViewById(C0690R.C0693id.cover_canceldialog_cancel);
        button2.setBackgroundResource(C0690R.C0692drawable.voice_ripple_cover_dialog_btn);
        button2.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                VoiceNoteService.this.lambda$showRecordCancelDialogOnCover$5$VoiceNoteService(view);
            }
        });
        button2.setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
        if (VoiceNoteApplication.getScene() == 12) {
            ((TextView) this.mCancelDialogOnCover.getWindow().getDecorView().findViewById(C0690R.C0693id.cover_canceldialog_content)).setText(getString(C0690R.string.stt_translation_cancel_popup_title));
            Button button3 = (Button) this.mCancelDialogOnCover.getWindow().getDecorView().findViewById(C0690R.C0693id.cover_canceldialog_discard);
            button3.setVisibility(0);
            button3.setBackgroundResource(C0690R.C0692drawable.voice_ripple_cover_dialog_btn);
            button3.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    VoiceNoteService.this.lambda$showRecordCancelDialogOnCover$6$VoiceNoteService(view);
                }
            });
            button3.setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
            button.setText(getString(C0690R.string.save));
        }
        button.setBackgroundResource(C0690R.C0692drawable.voice_ripple_cover_dialog_btn);
        button.setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
        button.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                VoiceNoteService.this.lambda$showRecordCancelDialogOnCover$7$VoiceNoteService(view);
            }
        });
        this.mCancelDialogOnCover.show();
    }

    public /* synthetic */ void lambda$showRecordCancelDialogOnCover$5$VoiceNoteService(View view) {
        hideRecordCancelDialog();
    }

    public /* synthetic */ void lambda$showRecordCancelDialogOnCover$6$VoiceNoteService(View view) {
        Log.m29v(TAG, "discard translation in quickpanel");
        Engine.getInstance().cancelTranslation(true);
        sendMessageCallback(63, -1);
        if (Helper.connectionCount() == 0) {
            VoiceNoteApplication.saveScene(1);
            VoiceNoteApplication.saveEvent(4);
        } else {
            VoiceNoteObservable.getInstance().notifyObservers(4);
        }
        hideNotification();
        hideCover();
    }

    public /* synthetic */ void lambda$showRecordCancelDialogOnCover$7$VoiceNoteService(View view) {
        int i;
        if (VoiceNoteApplication.getScene() == 12) {
            Log.m29v(TAG, "save translation in quickpanel");
            String path = Engine.getInstance().getPath();
            String str = null;
            if (path != null) {
                int lastIndexOf = path.lastIndexOf(47);
                int lastIndexOf2 = path.lastIndexOf(46);
                if (lastIndexOf >= 0 && (i = lastIndexOf + 1) < lastIndexOf2 && lastIndexOf2 < path.length()) {
                    str = path.substring(i, lastIndexOf2);
                }
            }
            if (path == null || str == null) {
                Log.m22e(TAG, "save translation in quickpanel - path null");
            } else {
                String lowerCase = getString(C0690R.string.prefix_voicememo).toLowerCase();
                Engine.getInstance().setUserSettingName(str + "_" + lowerCase);
                Engine.getInstance().setCategoryID(2);
                Engine.getInstance().stopTranslation(true);
            }
            hideNotification();
            hideCover();
            return;
        }
        Log.m29v(TAG, "cancel recording in quickpanel while recording or pause by stop recording dialog");
        Engine.getInstance().cancelRecord();
        sendMessageCallback(2, -1);
        hideNotification();
        VoiceNoteApplication.saveEvent(4);
        if (VoiceNoteFeature.FLAG_SUPPORT_SHOW_REJECT_CALL_NUMBER && CallRejectChecker.getInstance().getRejectCallCount() != 0) {
            showRejectCallInfoDialog();
        }
    }

    /* access modifiers changed from: private */
    public void hideRecordCancelDialog() {
        AlertDialog alertDialog = this.mCancelDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mCancelDialog = null;
        }
        Dialog dialog = this.mCancelDialogOnCover;
        if (dialog != null) {
            dialog.dismiss();
            this.mCancelDialogOnCover = null;
        }
    }

    /* access modifiers changed from: private */
    public void showRejectCallInfoDialog() {
        if (this.mRejectCallInfoDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, C0690R.style.DialogForService));
            int rejectCallCount = CallRejectChecker.getInstance().getRejectCallCount();
            builder.setTitle(getResources().getQuantityString(C0690R.plurals.call_blocked_title, rejectCallCount, new Object[]{Integer.valueOf(rejectCallCount)}));
            if (rejectCallCount > 1) {
                builder.setMessage(getString(C0690R.string.more_calls_missed_record));
            } else {
                builder.setMessage(getString(C0690R.string.a_call_missed_record));
            }
            builder.setPositiveButton(C0690R.string.category_call_history, new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    VoiceNoteService.this.lambda$showRejectCallInfoDialog$8$VoiceNoteService(dialogInterface, i);
                }
            });
            builder.setNegativeButton(C0690R.string.cancel, $$Lambda$VoiceNoteService$PG2Ds_9rwgQvIKuBYoPGupVVxU.INSTANCE);
            this.mRejectCallInfoDialog = builder.create();
            this.mRejectCallInfoDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                public final void onShow(DialogInterface dialogInterface) {
                    VoiceNoteService.this.lambda$showRejectCallInfoDialog$10$VoiceNoteService(dialogInterface);
                }
            });
            this.mRejectCallInfoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    VoiceNoteService.this.lambda$showRejectCallInfoDialog$11$VoiceNoteService(dialogInterface);
                }
            });
            this.mRejectCallInfoDialog.setCanceledOnTouchOutside(true);
            this.mRejectCallInfoDialog.getWindow().setType(2014);
            this.mRejectCallInfoDialog.show();
        }
    }

    public /* synthetic */ void lambda$showRejectCallInfoDialog$8$VoiceNoteService(DialogInterface dialogInterface, int i) {
        Intent intent = new Intent("com.android.phone.action.RECENT_CALLS");
        intent.setFlags(268435456);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.m24e(TAG, "ActivityNotFoundException", (Throwable) e);
        }
    }

    static /* synthetic */ void lambda$showRejectCallInfoDialog$9(DialogInterface dialogInterface, int i) {
        Log.m29v(TAG, "Cancel");
        dialogInterface.dismiss();
    }

    public /* synthetic */ void lambda$showRejectCallInfoDialog$10$VoiceNoteService(DialogInterface dialogInterface) {
        AlertDialog alertDialog = this.mRejectCallInfoDialog;
        if (alertDialog != null) {
            alertDialog.getButton(-1).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
            this.mRejectCallInfoDialog.getButton(-2).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
        }
    }

    public /* synthetic */ void lambda$showRejectCallInfoDialog$11$VoiceNoteService(DialogInterface dialogInterface) {
        this.mRejectCallInfoDialog = null;
    }

    private void showNetworkNotConnectedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, C0690R.style.DialogForService));
        builder.setTitle(C0690R.string.no_network_connection);
        builder.setMessage(getString(VoiceNoteFeature.FLAG_SUPPORT_CHINA_WLAN ? C0690R.string.no_network_connection_mgs_for_chn : C0690R.string.no_network_connection_mgs));
        builder.setPositiveButton(C0690R.string.f92ok, $$Lambda$VoiceNoteService$lG9OKuxvsgoFkUsubi1OCg4dn1E.INSTANCE);
        AlertDialog create = builder.create();
        create.getWindow().setType(2014);
        create.show();
    }

    /* access modifiers changed from: private */
    public void showTranslationCancelDialog() {
        if (this.mTranslationCancelDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, C0690R.style.DialogForService));
            builder.setMessage(getString(C0690R.string.stt_translation_cancel_popup_title));
            builder.setPositiveButton(C0690R.string.save, new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    VoiceNoteService.this.lambda$showTranslationCancelDialog$13$VoiceNoteService(dialogInterface, i);
                }
            });
            builder.setNegativeButton(C0690R.string.discard, new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    VoiceNoteService.this.lambda$showTranslationCancelDialog$14$VoiceNoteService(dialogInterface, i);
                }
            });
            builder.setNeutralButton(C0690R.string.cancel, (DialogInterface.OnClickListener) null);
            this.mTranslationCancelDialog = builder.create();
            this.mTranslationCancelDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    VoiceNoteService.this.lambda$showTranslationCancelDialog$15$VoiceNoteService(dialogInterface);
                }
            });
            this.mTranslationCancelDialog.setCanceledOnTouchOutside(true);
            this.mTranslationCancelDialog.getWindow().setType(2014);
            this.mTranslationCancelDialog.show();
            this.mTranslationCancelDialog.getButton(-1).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
            this.mTranslationCancelDialog.getButton(-2).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
            this.mTranslationCancelDialog.getButton(-3).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
        }
    }

    public /* synthetic */ void lambda$showTranslationCancelDialog$13$VoiceNoteService(DialogInterface dialogInterface, int i) {
        Log.m29v(TAG, "save translation in quickpanel");
        saveBackgroundTranslationFile();
    }

    public /* synthetic */ void lambda$showTranslationCancelDialog$14$VoiceNoteService(DialogInterface dialogInterface, int i) {
        Log.m29v(TAG, "cancel translation in quickpanel");
        Engine.getInstance().cancelTranslation(true);
        sendMessageCallback(63, -1);
        if (Helper.connectionCount() == 0) {
            VoiceNoteApplication.saveScene(1);
        }
        VoiceNoteApplication.saveEvent(4);
        hideNotification();
        Toast.makeText(this, C0690R.string.stt_translation_discard, 0).show();
    }

    public /* synthetic */ void lambda$showTranslationCancelDialog$15$VoiceNoteService(DialogInterface dialogInterface) {
        this.mTranslationCancelDialog = null;
    }

    /* access modifiers changed from: private */
    public void hideTranslationCancelDialog() {
        AlertDialog alertDialog = this.mTranslationCancelDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mTranslationCancelDialog = null;
        }
    }

    private static class IVoiceNoteServiceStub extends IVoiceNoteService.Stub {
        private final WeakReference<RemoteCallbackList<IVoiceNoteServiceCallback>> mRemoteCallbackList;
        private final WeakReference<VoiceNoteService> mVoiceNoteService;

        public IVoiceNoteServiceStub(VoiceNoteService voiceNoteService, RemoteCallbackList<IVoiceNoteServiceCallback> remoteCallbackList) {
            this.mVoiceNoteService = new WeakReference<>(voiceNoteService);
            this.mRemoteCallbackList = new WeakReference<>(remoteCallbackList);
        }

        public void showNotification() {
            ((VoiceNoteService) this.mVoiceNoteService.get()).showNotification();
        }

        public void hideNotification() {
            if (!RemoteViewManager.getInstance().isCoverClosed()) {
                ((VoiceNoteService) this.mVoiceNoteService.get()).hideNotification();
            }
        }

        public void registerCallback(IVoiceNoteServiceCallback iVoiceNoteServiceCallback) {
            if (iVoiceNoteServiceCallback != null) {
                ((RemoteCallbackList) this.mRemoteCallbackList.get()).register(iVoiceNoteServiceCallback);
            }
        }

        public void unregisterCallback(IVoiceNoteServiceCallback iVoiceNoteServiceCallback) {
            if (iVoiceNoteServiceCallback != null) {
                ((RemoteCallbackList) this.mRemoteCallbackList.get()).unregister(iVoiceNoteServiceCallback);
            }
        }
    }

    /* access modifiers changed from: private */
    public void showNotification() {
        RemoteViewManager.getInstance().enableEngineUpdateForNoti(true);
        RemoteViewManager.getInstance().show(1);
    }

    /* access modifiers changed from: private */
    public void hideNotification() {
        hideRecordCancelDialog();
        RemoteViewManager.getInstance().enableEngineUpdateForNoti(false);
        RemoteViewManager.getInstance().hide(1);
    }

    /* access modifiers changed from: private */
    public void hideCover() {
        RemoteViewManager.getInstance().hide(0);
    }

    /* access modifiers changed from: private */
    public void updateNotification(int i) {
        RemoteViewManager.getInstance().update(i, 1);
    }

    /* access modifiers changed from: private */
    public void enableNextPrevNotification(boolean z) {
        Log.m26i(TAG, "enableNextPrevNotification enable : " + z);
        this.mNextPrevEnable = z;
    }

    /* access modifiers changed from: private */
    public boolean isEnableNextPrevNotification() {
        if (VoiceNoteApplication.getScene() == 12) {
            Log.m26i(TAG, "cannot next or prev while translating");
            return false;
        } else if (!this.mNextPrevEnable) {
            Log.m26i(TAG, "isEnableNextPrevNotification false");
            return false;
        } else {
            MetadataRepository instance = MetadataRepository.getInstance();
            StringBuilder sb = new StringBuilder();
            sb.append("isEnableNextPrevNotification : ");
            sb.append(!instance.isWaveMakerWorking());
            Log.m26i(TAG, sb.toString());
            return !instance.isWaveMakerWorking();
        }
    }

    /* access modifiers changed from: private */
    public void updateCover(int i) {
        RemoteViewManager.getInstance().update(i, 0);
    }

    private void registerBroadcastReceiverNotification(boolean z) {
        Trace.beginSection("VNService.registerBRNoti");
        if (z) {
            Log.m19d(TAG, "register notification broadcastReceiver");
            if (this.mBroadcastReceiverNotification == null) {
                this.mBroadcastReceiverNotification = new BroadcastReceiver() {
                    /* JADX WARNING: Can't fix incorrect switch cases order */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void onReceive(android.content.Context r17, android.content.Intent r18) {
                        /*
                            r16 = this;
                            r1 = r16
                            r0 = r17
                            r2 = r18
                            java.lang.String r3 = r18.getAction()
                            int r4 = com.sec.android.app.voicenote.uicore.VoiceNoteApplication.getScene()
                            com.sec.android.app.voicenote.service.Engine r5 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            int r5 = r5.getPlayerState()
                            com.sec.android.app.voicenote.service.Engine r6 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            int r6 = r6.getRecorderState()
                            com.sec.android.app.voicenote.service.Engine r7 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            int r7 = r7.getTranslationState()
                            java.lang.StringBuilder r8 = new java.lang.StringBuilder
                            r8.<init>()
                            java.lang.String r9 = "onReceive action : "
                            r8.append(r9)
                            r8.append(r3)
                            java.lang.String r9 = ", scene : "
                            r8.append(r9)
                            r8.append(r4)
                            java.lang.String r9 = ", playerState : "
                            r8.append(r9)
                            r8.append(r5)
                            java.lang.String r9 = ", recorderState : "
                            r8.append(r9)
                            r8.append(r6)
                            java.lang.String r8 = r8.toString()
                            java.lang.String r9 = "VoiceNoteService"
                            com.sec.android.app.voicenote.provider.Log.m19d(r9, r8)
                            int r8 = r3.hashCode()
                            r10 = 11
                            r13 = 12
                            r15 = 4
                            r14 = 3
                            java.lang.Integer r11 = java.lang.Integer.valueOf(r14)
                            r12 = 1
                            switch(r8) {
                                case -2137363543: goto L_0x022b;
                                case -1985692204: goto L_0x0220;
                                case -1704065700: goto L_0x0216;
                                case -1603896272: goto L_0x020b;
                                case -1542700612: goto L_0x0200;
                                case -1535995408: goto L_0x01f5;
                                case -937130977: goto L_0x01eb;
                                case -805631362: goto L_0x01e0;
                                case -805559874: goto L_0x01d5;
                                case -805468275: goto L_0x01c9;
                                case -775515408: goto L_0x01be;
                                case -748185462: goto L_0x01b2;
                                case -726587789: goto L_0x01a6;
                                case -531434542: goto L_0x019a;
                                case -403228793: goto L_0x018e;
                                case -368883504: goto L_0x0182;
                                case -363757972: goto L_0x0177;
                                case -291969012: goto L_0x016b;
                                case -290110936: goto L_0x015f;
                                case -253775764: goto L_0x0153;
                                case -250783709: goto L_0x0147;
                                case -225542389: goto L_0x013c;
                                case -138968048: goto L_0x0130;
                                case 127008882: goto L_0x0124;
                                case 158859398: goto L_0x0118;
                                case 256480794: goto L_0x010c;
                                case 389861195: goto L_0x0100;
                                case 640733419: goto L_0x00f4;
                                case 796956619: goto L_0x00e8;
                                case 1174747211: goto L_0x00dd;
                                case 1197161458: goto L_0x00d1;
                                case 1284510676: goto L_0x00c5;
                                case 1416118785: goto L_0x00b9;
                                case 1605609282: goto L_0x00ad;
                                case 1605633767: goto L_0x00a2;
                                case 1636202285: goto L_0x0097;
                                case 1691664745: goto L_0x008b;
                                case 1803776853: goto L_0x0080;
                                case 1818822974: goto L_0x0074;
                                case 1855771272: goto L_0x0068;
                                default: goto L_0x0066;
                            }
                        L_0x0066:
                            goto L_0x0235
                        L_0x0068:
                            java.lang.String r8 = "com.sec.android.app.voicenote.show_mode_not_supported"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 31
                            goto L_0x0236
                        L_0x0074:
                            java.lang.String r8 = "com.sec.android.app.voicenote.network_off"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 36
                            goto L_0x0236
                        L_0x0080:
                            java.lang.String r8 = "com.sec.android.app.voicenote.noti_rec_save"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 5
                            goto L_0x0236
                        L_0x008b:
                            java.lang.String r8 = "com.sec.android.app.voicenote.translation_file_play"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 18
                            goto L_0x0236
                        L_0x0097:
                            java.lang.String r8 = "com.sec.android.app.voicenote.rec_cancel.dialog"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 6
                            goto L_0x0236
                        L_0x00a2:
                            java.lang.String r8 = "com.sec.android.app.voicenote.rec_pause"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 0
                            goto L_0x0236
                        L_0x00ad:
                            java.lang.String r8 = "com.sec.android.app.voicenote.translation_toggle"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 16
                            goto L_0x0236
                        L_0x00b9:
                            java.lang.String r8 = "com.sec.android.app.voicenote.update_notification"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 20
                            goto L_0x0236
                        L_0x00c5:
                            java.lang.String r8 = "com.sec.android.app.voicenote.no_action"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 22
                            goto L_0x0236
                        L_0x00d1:
                            java.lang.String r8 = "com.sec.android.app.voicenote.screen_on"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 23
                            goto L_0x0236
                        L_0x00dd:
                            java.lang.String r8 = "com.sec.android.app.voicenote.rec_cancel.keyguard"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 7
                            goto L_0x0236
                        L_0x00e8:
                            java.lang.String r8 = "com.sec.android.app.voicenote.play_pause"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 9
                            goto L_0x0236
                        L_0x00f4:
                            java.lang.String r8 = "com.sec.android.app.voicenote.translation_save"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 17
                            goto L_0x0236
                        L_0x0100:
                            java.lang.String r8 = "com.sec.android.app.voicenote.sd_mount"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 29
                            goto L_0x0236
                        L_0x010c:
                            java.lang.String r8 = "com.sec.android.app.voicenote.clear_dialog"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 33
                            goto L_0x0236
                        L_0x0118:
                            java.lang.String r8 = "android.intent.action.CONFIGURATION_CHANGED"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 34
                            goto L_0x0236
                        L_0x0124:
                            java.lang.String r8 = "com.sec.android.app.voicenote.cover_open"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 26
                            goto L_0x0236
                        L_0x0130:
                            java.lang.String r8 = "com.sec.android.app.voicenote.play_stop.quit"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 15
                            goto L_0x0236
                        L_0x013c:
                            java.lang.String r8 = "com.samsung.telecom.IncomingCallAnsweredDuringRecord"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = r14
                            goto L_0x0236
                        L_0x0147:
                            java.lang.String r8 = "com.sec.android.app.voicenote.hide_mode_not_supported"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 32
                            goto L_0x0236
                        L_0x0153:
                            java.lang.String r8 = "com.sec.android.app.voicenote.translation_cancel.keyguard"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 19
                            goto L_0x0236
                        L_0x015f:
                            java.lang.String r8 = "com.sec.android.app.voicenote.hide_notification"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 21
                            goto L_0x0236
                        L_0x016b:
                            java.lang.String r8 = "com.sec.android.app.voicenote.device_unlocked"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 39
                            goto L_0x0236
                        L_0x0177:
                            java.lang.String r8 = "com.sec.android.app.voicenote.rec_save"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = r15
                            goto L_0x0236
                        L_0x0182:
                            java.lang.String r8 = "com.sec.android.app.voicenote.cover_close"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 25
                            goto L_0x0236
                        L_0x018e:
                            java.lang.String r8 = "android.intent.action.CLOSE_SYSTEM_DIALOGS"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 28
                            goto L_0x0236
                        L_0x019a:
                            java.lang.String r8 = "com.sec.android.app.voicenote.sd_unmount"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 30
                            goto L_0x0236
                        L_0x01a6:
                            java.lang.String r8 = "com.sec.android.app.voicenote.device_locked"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 38
                            goto L_0x0236
                        L_0x01b2:
                            java.lang.String r8 = "com.sec.android.app.voicenote.quick_panel_show"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 27
                            goto L_0x0236
                        L_0x01be:
                            java.lang.String r8 = "com.sec.android.app.voicenote.rec_play_toggle"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = r10
                            goto L_0x0236
                        L_0x01c9:
                            java.lang.String r8 = "com.sec.android.app.voicenote.play_stop"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 10
                            goto L_0x0236
                        L_0x01d5:
                            java.lang.String r8 = "com.sec.android.app.voicenote.play_prev"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 13
                            goto L_0x0236
                        L_0x01e0:
                            java.lang.String r8 = "com.sec.android.app.voicenote.play_next"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 14
                            goto L_0x0236
                        L_0x01eb:
                            java.lang.String r8 = "com.sec.android.app.voicenote.play_toggle"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = r13
                            goto L_0x0236
                        L_0x01f5:
                            java.lang.String r8 = "com.sec.android.app.voicenote.low_storage"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 37
                            goto L_0x0236
                        L_0x0200:
                            java.lang.String r8 = "com.sec.android.app.voicenote.screen_off"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 24
                            goto L_0x0236
                        L_0x020b:
                            java.lang.String r8 = "com.sec.android.app.voicenote.network_on"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 35
                            goto L_0x0236
                        L_0x0216:
                            java.lang.String r8 = "com.sec.android.app.voicenote.rec_resume"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = r12
                            goto L_0x0236
                        L_0x0220:
                            java.lang.String r8 = "com.sec.android.app.voicenote.play"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 8
                            goto L_0x0236
                        L_0x022b:
                            java.lang.String r8 = "com.sec.android.app.voicenote.rec_cancel"
                            boolean r3 = r3.equals(r8)
                            if (r3 == 0) goto L_0x0235
                            r3 = 2
                            goto L_0x0236
                        L_0x0235:
                            r3 = -1
                        L_0x0236:
                            r8 = 2131755363(0x7f100163, float:1.9141603E38)
                            switch(r3) {
                                case 0: goto L_0x0c09;
                                case 1: goto L_0x0c03;
                                case 2: goto L_0x0be8;
                                case 3: goto L_0x0be2;
                                case 4: goto L_0x0b96;
                                case 5: goto L_0x0a48;
                                case 6: goto L_0x0a41;
                                case 7: goto L_0x0a28;
                                case 8: goto L_0x09f5;
                                case 9: goto L_0x09ad;
                                case 10: goto L_0x096b;
                                case 11: goto L_0x0918;
                                case 12: goto L_0x08ba;
                                case 13: goto L_0x07e9;
                                case 14: goto L_0x071b;
                                case 15: goto L_0x06d1;
                                case 16: goto L_0x063c;
                                case 17: goto L_0x05d7;
                                case 18: goto L_0x0593;
                                case 19: goto L_0x057b;
                                case 20: goto L_0x056d;
                                case 21: goto L_0x0566;
                                case 22: goto L_0x055f;
                                case 23: goto L_0x054e;
                                case 24: goto L_0x0533;
                                case 25: goto L_0x04e8;
                                case 26: goto L_0x04c5;
                                case 27: goto L_0x0c30;
                                case 28: goto L_0x04be;
                                case 29: goto L_0x04ad;
                                case 30: goto L_0x0406;
                                case 31: goto L_0x0387;
                                case 32: goto L_0x0372;
                                case 33: goto L_0x035d;
                                case 34: goto L_0x0329;
                                case 35: goto L_0x02e7;
                                case 36: goto L_0x027d;
                                case 37: goto L_0x0274;
                                case 38: goto L_0x025c;
                                case 39: goto L_0x023e;
                                default: goto L_0x023c;
                            }
                        L_0x023c:
                            goto L_0x0c30
                        L_0x023e:
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x0c30
                            if (r6 != r12) goto L_0x0251
                            if (r5 == r14) goto L_0x024a
                            if (r5 != r15) goto L_0x0251
                        L_0x024a:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.showNotification()
                            goto L_0x0c30
                        L_0x0251:
                            if (r6 == r14) goto L_0x0255
                            if (r6 != r15) goto L_0x0c30
                        L_0x0255:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.updateNotification(r13)
                            goto L_0x0c30
                        L_0x025c:
                            if (r6 != r12) goto L_0x0269
                            if (r5 == r14) goto L_0x0262
                            if (r5 != r15) goto L_0x0269
                        L_0x0262:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.hideNotification()
                            goto L_0x0c30
                        L_0x0269:
                            if (r6 == r14) goto L_0x026d
                            if (r6 != r15) goto L_0x0c30
                        L_0x026d:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.updateNotification(r10)
                            goto L_0x0c30
                        L_0x0274:
                            com.sec.android.app.voicenote.service.Recorder r0 = com.sec.android.app.voicenote.service.Recorder.getInstance()
                            r0.initProgressCheckFullStorage()
                            goto L_0x0c30
                        L_0x027d:
                            java.lang.String r0 = "VOICENOTE_NETWORK_OFF"
                            com.sec.android.app.voicenote.provider.Log.m19d(r9, r0)
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            int r0 = r0.getCurrentTime()
                            com.sec.android.app.voicenote.service.Engine r2 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            int r2 = r2.getDuration()
                            if (r0 != r2) goto L_0x0295
                            return
                        L_0x0295:
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x02b6
                            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = com.sec.android.app.voicenote.uicore.VoiceNoteObservable.getInstance()
                            r2 = 7002(0x1b5a, float:9.812E-42)
                            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                            r0.notifyObservers(r2)
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r2 = 0
                            r0.pauseTranslation(r2)
                            r0 = 7006(0x1b5e, float:9.817E-42)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r0)
                            goto L_0x02de
                        L_0x02b6:
                            int r0 = com.sec.android.app.voicenote.uicore.VoiceNoteApplication.getScene()
                            if (r0 != r13) goto L_0x02de
                            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = com.sec.android.app.voicenote.uicore.VoiceNoteObservable.getInstance()
                            r2 = 7006(0x1b5e, float:9.817E-42)
                            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                            r0.notifyObservers(r2)
                            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = com.sec.android.app.voicenote.uicore.VoiceNoteObservable.getInstance()
                            r2 = 7002(0x1b5a, float:9.812E-42)
                            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                            r0.notifyObservers(r2)
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r2 = 0
                            r0.pauseTranslation(r2)
                        L_0x02de:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 8
                            r0.updateNotification(r2)
                            goto L_0x0c30
                        L_0x02e7:
                            java.lang.String r0 = "VOICENOTE_NETWORK_ON"
                            com.sec.android.app.voicenote.provider.Log.m19d(r9, r0)
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            int r0 = r0.getCurrentTime()
                            com.sec.android.app.voicenote.service.Engine r2 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            int r2 = r2.getDuration()
                            if (r0 != r2) goto L_0x02ff
                            return
                        L_0x02ff:
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x031e
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            int r0 = r0.resumeTranslation()
                            if (r0 != 0) goto L_0x0c30
                            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = com.sec.android.app.voicenote.uicore.VoiceNoteObservable.getInstance()
                            r2 = 7003(0x1b5b, float:9.813E-42)
                            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                            r0.notifyObservers(r2)
                            goto L_0x0c30
                        L_0x031e:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 33
                            r3 = 1000(0x3e8, float:1.401E-42)
                            r0.sendMessageCallback(r2, r3)
                            goto L_0x0c30
                        L_0x0329:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 7
                            r0.updateNotification(r2)
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            android.app.AlertDialog r0 = r0.mWarningMuteDetectedDialog
                            if (r0 == 0) goto L_0x034e
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            android.app.AlertDialog r0 = r0.mWarningMuteDetectedDialog
                            boolean r0 = r0.isShowing()
                            if (r0 == 0) goto L_0x034e
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.dismissMuteDetectedDialog()
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 0
                            r0.showMuteDetectedDialog(r2)
                        L_0x034e:
                            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = com.sec.android.app.voicenote.uicore.VoiceNoteObservable.getInstance()
                            r2 = 19
                            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                            r0.notifyObservers(r2)
                            goto L_0x0c30
                        L_0x035d:
                            java.lang.String r0 = "VOICENOTE_CLEAR_DIALOG"
                            com.sec.android.app.voicenote.provider.Log.m19d(r9, r0)
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 <= 0) goto L_0x0c30
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 51
                            r3 = 0
                            r0.sendMessageCallback(r2, r3)
                            goto L_0x0c30
                        L_0x0372:
                            r3 = 0
                            java.lang.String r0 = "VOICENOTE_HIDE_MODE_NOT_SUPPORTED"
                            com.sec.android.app.voicenote.provider.Log.m19d(r9, r0)
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 <= 0) goto L_0x0c30
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 41
                            r0.sendMessageCallback(r2, r3)
                            goto L_0x0c30
                        L_0x0387:
                            java.lang.String r0 = "VOICENOTE_SHOW_MODE_NOT_SUPPORTED"
                            com.sec.android.app.voicenote.provider.Log.m19d(r9, r0)
                            java.lang.String r0 = "record_mode"
                            int r0 = com.sec.android.app.voicenote.provider.Settings.getIntSettings(r0, r12)
                            if (r4 == r15) goto L_0x0397
                            r2 = 6
                            if (r4 != r2) goto L_0x039d
                        L_0x0397:
                            java.lang.String r0 = "play_mode"
                            int r0 = com.sec.android.app.voicenote.provider.Settings.getIntSettings(r0, r12)
                        L_0x039d:
                            int r2 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r2 != 0) goto L_0x03fd
                            if (r6 == r12) goto L_0x0c30
                            r2 = 2
                            if (r0 == r2) goto L_0x03d4
                            if (r0 == r15) goto L_0x03ac
                            goto L_0x0c30
                        L_0x03ac:
                            r0 = 8
                            if (r4 != r0) goto L_0x0c30
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r3 = 2131755552(0x7f100220, float:1.9141987E38)
                            java.lang.String r0 = r0.getString(r3)
                            com.sec.android.app.voicenote.service.VoiceNoteService r3 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r4 = 2131755328(0x7f100140, float:1.9141532E38)
                            java.lang.Object[] r2 = new java.lang.Object[r2]
                            r5 = 0
                            r2[r5] = r0
                            r2[r12] = r0
                            java.lang.String r0 = r3.getString(r4, r2)
                            com.sec.android.app.voicenote.service.VoiceNoteService r2 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            android.widget.Toast r0 = android.widget.Toast.makeText(r2, r0, r12)
                            r0.show()
                            goto L_0x0c30
                        L_0x03d4:
                            r0 = 8
                            if (r4 != r0) goto L_0x0c30
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 2131755309(0x7f10012d, float:1.9141494E38)
                            java.lang.String r0 = r0.getString(r2)
                            com.sec.android.app.voicenote.service.VoiceNoteService r2 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r3 = 2131755328(0x7f100140, float:1.9141532E38)
                            r4 = 2
                            java.lang.Object[] r4 = new java.lang.Object[r4]
                            r5 = 0
                            r4[r5] = r0
                            r4[r12] = r0
                            java.lang.String r0 = r2.getString(r3, r4)
                            com.sec.android.app.voicenote.service.VoiceNoteService r2 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            android.widget.Toast r0 = android.widget.Toast.makeText(r2, r0, r12)
                            r0.show()
                            goto L_0x0c30
                        L_0x03fd:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 41
                            r0.sendMessageCallback(r2, r12)
                            goto L_0x0c30
                        L_0x0406:
                            java.lang.String r0 = "VOICENOTE_SD_UNMOUNT"
                            com.sec.android.app.voicenote.provider.Log.m19d(r9, r0)
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            java.lang.String r0 = r0.getRecentFilePath()
                            java.lang.String r3 = "tag_path"
                            java.lang.String r2 = r2.getStringExtra(r3)
                            if (r0 == 0) goto L_0x0495
                            if (r2 == 0) goto L_0x0495
                            boolean r0 = r0.startsWith(r2)
                            if (r0 == 0) goto L_0x0495
                            java.lang.String r0 = "VOICENOTE_SD_UNMOUNT Working volume has been removed !!"
                            com.sec.android.app.voicenote.provider.Log.m19d(r9, r0)
                            if (r6 == r12) goto L_0x0470
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r0.cancelRecord()
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            int r0 = r0.getPlayerState()
                            if (r0 == r12) goto L_0x0443
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r2 = 0
                            r0.stopPlay(r2)
                        L_0x0443:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 2131755421(0x7f10019d, float:1.914172E38)
                            android.widget.Toast r0 = android.widget.Toast.makeText(r0, r2, r12)
                            r0.show()
                            r2 = 2
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r2)
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x046d
                            int r0 = com.sec.android.app.voicenote.uicore.VoiceNoteApplication.getScene()
                            r3 = 6
                            if (r0 != r3) goto L_0x0467
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveScene(r2)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r14)
                            goto L_0x046d
                        L_0x0467:
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveScene(r12)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r15)
                        L_0x046d:
                            r14 = 1006(0x3ee, float:1.41E-42)
                            goto L_0x0496
                        L_0x0470:
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            int r0 = r0.getPlayerState()
                            if (r0 == r12) goto L_0x0495
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r2 = 0
                            r0.stopPlay(r2)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r14)
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x0496
                            r2 = 2
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveScene(r2)
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.hideNotification()
                            goto L_0x0496
                        L_0x0495:
                            r14 = -1
                        L_0x0496:
                            java.lang.String r0 = "storage"
                            r2 = 0
                            com.sec.android.app.voicenote.provider.Settings.setSettings((java.lang.String) r0, (int) r2)
                            com.sec.android.app.voicenote.provider.StorageProvider.resetSDCardWritableDir()
                            java.lang.String r0 = "sdcard_previous_state"
                            com.sec.android.app.voicenote.provider.Settings.setSettings((java.lang.String) r0, (int) r2)
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 32
                            r0.sendMessageCallback(r2, r14)
                            goto L_0x0c30
                        L_0x04ad:
                            java.lang.String r0 = "VOICENOTE_SD_MOUNT"
                            com.sec.android.app.voicenote.provider.Log.m19d(r9, r0)
                            if (r6 != r12) goto L_0x0c30
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 31
                            r3 = -1
                            r0.sendMessageCallback(r2, r3)
                            goto L_0x0c30
                        L_0x04be:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.hideRecordCancelDialog()
                            goto L_0x0c30
                        L_0x04c5:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.hideRecordCancelDialog()
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            int unused = r0.mRemoteType = r12
                            boolean r0 = com.sec.android.app.voicenote.service.remote.RemoteViewManager.isRunning()
                            if (r0 == 0) goto L_0x0c30
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 <= 0) goto L_0x0c30
                            boolean r0 = com.sec.android.app.voicenote.common.util.KeyguardManagerHelper.isKeyguardLockedBySecure()
                            if (r0 != 0) goto L_0x0c30
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.hideNotification()
                            goto L_0x0c30
                        L_0x04e8:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.hideRecordCancelDialog()
                            if (r4 != r13) goto L_0x04ff
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            int r0 = r0.getTranslationState()
                            if (r0 != r12) goto L_0x04ff
                            java.lang.String r0 = "translator state is idle - no need show cover"
                            com.sec.android.app.voicenote.provider.Log.m26i(r9, r0)
                            return
                        L_0x04ff:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            com.sec.android.app.voicenote.service.remote.RemoteViewManager r2 = com.sec.android.app.voicenote.service.remote.RemoteViewManager.getInstance()
                            int r2 = r2.getDisplayedRemoteType()
                            int unused = r0.mRemoteType = r2
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 2
                            r0.updateCover(r2)
                            boolean r0 = com.sec.android.app.voicenote.common.util.KeyguardManagerHelper.isKeyguardLockedBySecure()
                            if (r0 != 0) goto L_0x051d
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.showNotification()
                        L_0x051d:
                            r0 = 6
                            if (r4 != r0) goto L_0x0c30
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            int r0 = r0.getPlayerState()
                            if (r0 != r14) goto L_0x0c30
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r0.pausePlay()
                            goto L_0x0c30
                        L_0x0533:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.hideRecordCancelDialog()
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.hideTranslationCancelDialog()
                            com.sec.android.app.voicenote.service.remote.RemoteViewManager r0 = com.sec.android.app.voicenote.service.remote.RemoteViewManager.getInstance()
                            r2 = 0
                            r0.enableUpdate(r2)
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r0.setScreenOff(r12)
                            goto L_0x0c30
                        L_0x054e:
                            r2 = 0
                            com.sec.android.app.voicenote.service.remote.RemoteViewManager r0 = com.sec.android.app.voicenote.service.remote.RemoteViewManager.getInstance()
                            r0.enableUpdate(r12)
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r0.setScreenOff(r2)
                            goto L_0x0c30
                        L_0x055f:
                            java.lang.String r0 = "VOICENOTE_NO_ACTION"
                            com.sec.android.app.voicenote.provider.Log.m19d(r9, r0)
                            goto L_0x0c30
                        L_0x0566:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.hideNotification()
                            goto L_0x0c30
                        L_0x056d:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            java.lang.String r3 = "type"
                            r4 = 6
                            int r2 = r2.getIntExtra(r3, r4)
                            r0.updateNotification(r2)
                            goto L_0x0c30
                        L_0x057b:
                            com.sec.android.app.voicenote.service.remote.RemoteViewManager r0 = com.sec.android.app.voicenote.service.remote.RemoteViewManager.getInstance()
                            boolean r0 = r0.isCoverClosed()
                            if (r0 == 0) goto L_0x058c
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.showRecordCancelDialogOnCover(r12)
                            goto L_0x0c30
                        L_0x058c:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.showTranslationCancelDialog()
                            goto L_0x0c30
                        L_0x0593:
                            int r2 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r2 != 0) goto L_0x05be
                            r2 = 7007(0x1b5f, float:9.819E-42)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r2)
                            com.sec.android.app.voicenote.service.VoiceNoteService r2 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2.hideNotification()
                            android.content.Intent r2 = new android.content.Intent
                            java.lang.String r3 = "android.intent.action.CLOSE_SYSTEM_DIALOGS"
                            r2.<init>(r3)
                            r0.sendBroadcast(r2)
                            android.content.Intent r2 = new android.content.Intent
                            java.lang.Class<com.sec.android.app.voicenote.main.VNMainActivity> r3 = com.sec.android.app.voicenote.main.VNMainActivity.class
                            r2.<init>(r0, r3)
                            r3 = 268435456(0x10000000, float:2.5243549E-29)
                            r2.setFlags(r3)
                            r0.startActivity(r2)
                            goto L_0x0c30
                        L_0x05be:
                            com.sec.android.app.voicenote.service.remote.RemoteViewManager r0 = com.sec.android.app.voicenote.service.remote.RemoteViewManager.getInstance()
                            boolean r0 = r0.isCoverClosed()
                            if (r0 == 0) goto L_0x0c30
                            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = com.sec.android.app.voicenote.uicore.VoiceNoteObservable.getInstance()
                            r2 = 7007(0x1b5f, float:9.819E-42)
                            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                            r0.notifyObservers(r2)
                            goto L_0x0c30
                        L_0x05d7:
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            int r0 = r0.getPlayerState()
                            if (r0 != r14) goto L_0x05e8
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r0.pausePlay()
                        L_0x05e8:
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            boolean r0 = r0.isSaveTranslatable()
                            if (r0 == 0) goto L_0x05f8
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.saveBackgroundTranslationFile()
                            goto L_0x062b
                        L_0x05f8:
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x0609
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r0.cancelTranslation(r12)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r14)
                            goto L_0x062b
                        L_0x0609:
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r2 = 0
                            r0.cancelTranslation(r2)
                            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = com.sec.android.app.voicenote.uicore.VoiceNoteObservable.getInstance()
                            r2 = 7005(0x1b5d, float:9.816E-42)
                            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                            r0.notifyObservers(r2)
                            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = com.sec.android.app.voicenote.uicore.VoiceNoteObservable.getInstance()
                            r2 = 17
                            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                            r0.notifyObservers(r2)
                        L_0x062b:
                            com.sec.android.app.voicenote.service.remote.RemoteViewManager r0 = com.sec.android.app.voicenote.service.remote.RemoteViewManager.getInstance()
                            boolean r0 = r0.isCoverClosed()
                            if (r0 == 0) goto L_0x0c30
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.hideCover()
                            goto L_0x0c30
                        L_0x063c:
                            com.sec.android.app.voicenote.provider.PhoneStateProvider r0 = com.sec.android.app.voicenote.provider.PhoneStateProvider.getInstance()
                            com.sec.android.app.voicenote.service.VoiceNoteService r2 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            boolean r0 = r0.isDuringCall(r2)
                            if (r0 == 0) goto L_0x0653
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 0
                            android.widget.Toast r0 = android.widget.Toast.makeText(r0, r8, r2)
                            r0.show()
                            return
                        L_0x0653:
                            r2 = 0
                            if (r5 != r14) goto L_0x067c
                            r3 = 2
                            if (r7 != r3) goto L_0x067c
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r0.pauseTranslation(r2)
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x066d
                            r0 = 7002(0x1b5a, float:9.812E-42)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r0)
                            goto L_0x0c30
                        L_0x066d:
                            r0 = 7002(0x1b5a, float:9.812E-42)
                            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r2 = com.sec.android.app.voicenote.uicore.VoiceNoteObservable.getInstance()
                            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                            r2.notifyObservers(r0)
                            goto L_0x0c30
                        L_0x067c:
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            boolean r0 = r0.isEndPlay()
                            if (r0 == 0) goto L_0x068c
                            java.lang.String r0 = "BACKGROUND_VOICENOTE_TRANSLATION_TOGGLE - play to end"
                            com.sec.android.app.voicenote.provider.Log.m26i(r9, r0)
                            return
                        L_0x068c:
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            int r0 = r0.resumeTranslation()
                            r2 = -103(0xffffffffffffff99, float:NaN)
                            if (r0 == r2) goto L_0x06c5
                            if (r0 == 0) goto L_0x069c
                            goto L_0x0c30
                        L_0x069c:
                            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = com.sec.android.app.voicenote.uicore.VoiceNoteObservable.getInstance()
                            r2 = 7003(0x1b5b, float:9.813E-42)
                            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                            r0.notifyObservers(r2)
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x06b6
                            r0 = 7003(0x1b5b, float:9.813E-42)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r0)
                            goto L_0x0c30
                        L_0x06b6:
                            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = com.sec.android.app.voicenote.uicore.VoiceNoteObservable.getInstance()
                            r2 = 7003(0x1b5b, float:9.813E-42)
                            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                            r0.notifyObservers(r2)
                            goto L_0x0c30
                        L_0x06c5:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 0
                            android.widget.Toast r0 = android.widget.Toast.makeText(r0, r8, r2)
                            r0.show()
                            goto L_0x0c30
                        L_0x06d1:
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            int r0 = r0.getCurrentTime()
                            int r0 = r0 + 500
                            com.sec.android.app.voicenote.service.Engine r2 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            int r2 = r2.getDuration()
                            if (r0 >= r2) goto L_0x06ec
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r0.setStopPlayWithFadeOut(r12)
                        L_0x06ec:
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r0.stopPlay()
                            com.sec.android.app.voicenote.provider.CursorProvider r0 = com.sec.android.app.voicenote.provider.CursorProvider.getInstance()
                            r0.resetSearchTag()
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.hideNotification()
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = -1
                            r0.sendMessageCallback(r10, r2)
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x0712
                            r2 = 2
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveScene(r2)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r14)
                        L_0x0712:
                            com.sec.android.app.voicenote.service.MetadataRepository r0 = com.sec.android.app.voicenote.service.MetadataRepository.getInstance()
                            r0.close()
                            goto L_0x0c30
                        L_0x071b:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            boolean r0 = r0.isEnableNextPrevNotification()
                            if (r0 != 0) goto L_0x0724
                            return
                        L_0x0724:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 0
                            r0.enableNextPrevNotification(r2)
                            com.sec.android.app.voicenote.provider.PhoneStateProvider r0 = com.sec.android.app.voicenote.provider.PhoneStateProvider.getInstance()
                            com.sec.android.app.voicenote.service.VoiceNoteService r3 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            boolean r0 = r0.isCallIdle(r3)
                            if (r0 != 0) goto L_0x0741
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            android.widget.Toast r0 = android.widget.Toast.makeText(r0, r8, r2)
                            r0.show()
                            goto L_0x07d8
                        L_0x0741:
                            com.sec.android.app.voicenote.provider.CursorProvider r0 = com.sec.android.app.voicenote.provider.CursorProvider.getInstance()
                            java.lang.String r0 = r0.getNextFilePath()
                            if (r0 == 0) goto L_0x07d8
                            com.sec.android.app.voicenote.service.Engine r2 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r2.clearContentItem()
                            com.sec.android.app.voicenote.service.Engine r2 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r2.initPlay()
                            com.sec.android.app.voicenote.service.Engine r2 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            int r0 = r2.startPlay((java.lang.String) r0)
                            r2 = -103(0xffffffffffffff99, float:NaN)
                            if (r0 == r2) goto L_0x07ce
                            if (r0 == 0) goto L_0x0768
                            goto L_0x07d8
                        L_0x0768:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.updateNotification(r12)
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.updateCover(r12)
                            com.sec.android.app.voicenote.provider.CursorProvider r0 = com.sec.android.app.voicenote.provider.CursorProvider.getInstance()
                            r0.moveToNextPosition()
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 51
                            r3 = -1
                            r0.sendMessageCallback(r2, r3)
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x07c7
                            if (r4 != r15) goto L_0x079c
                            r0 = 2005(0x7d5, float:2.81E-42)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r0)
                            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = com.sec.android.app.voicenote.uicore.VoiceNoteObservable.getInstance()
                            r2 = 2005(0x7d5, float:2.81E-42)
                            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                            r0.notifyObservers(r2)
                            goto L_0x07d8
                        L_0x079c:
                            if (r4 != r14) goto L_0x07b1
                            r0 = 3004(0xbbc, float:4.21E-42)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r0)
                            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = com.sec.android.app.voicenote.uicore.VoiceNoteObservable.getInstance()
                            r2 = 3004(0xbbc, float:4.21E-42)
                            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                            r0.notifyObservers(r2)
                            goto L_0x07d8
                        L_0x07b1:
                            r0 = 7
                            if (r4 != r0) goto L_0x07d8
                            r0 = 6009(0x1779, float:8.42E-42)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r0)
                            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = com.sec.android.app.voicenote.uicore.VoiceNoteObservable.getInstance()
                            r2 = 6009(0x1779, float:8.42E-42)
                            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                            r0.notifyObservers(r2)
                            goto L_0x07d8
                        L_0x07c7:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = -1
                            r0.sendMessageCallback(r13, r2)
                            goto L_0x07d8
                        L_0x07ce:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 0
                            android.widget.Toast r0 = android.widget.Toast.makeText(r0, r8, r2)
                            r0.show()
                        L_0x07d8:
                            android.os.Handler r0 = new android.os.Handler
                            r0.<init>()
                            com.sec.android.app.voicenote.service.-$$Lambda$VoiceNoteService$2$vcAXmK4NVwvlFEfAFw5myar99Jg r2 = new com.sec.android.app.voicenote.service.-$$Lambda$VoiceNoteService$2$vcAXmK4NVwvlFEfAFw5myar99Jg
                            r2.<init>()
                            r3 = 300(0x12c, double:1.48E-321)
                            r0.postDelayed(r2, r3)
                            goto L_0x0c30
                        L_0x07e9:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            boolean r0 = r0.isEnableNextPrevNotification()
                            if (r0 != 0) goto L_0x07f2
                            return
                        L_0x07f2:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 0
                            r0.enableNextPrevNotification(r2)
                            com.sec.android.app.voicenote.provider.PhoneStateProvider r0 = com.sec.android.app.voicenote.provider.PhoneStateProvider.getInstance()
                            com.sec.android.app.voicenote.service.VoiceNoteService r3 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            boolean r0 = r0.isCallIdle(r3)
                            if (r0 != 0) goto L_0x080f
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            android.widget.Toast r0 = android.widget.Toast.makeText(r0, r8, r2)
                            r0.show()
                            goto L_0x08a9
                        L_0x080f:
                            com.sec.android.app.voicenote.provider.CursorProvider r0 = com.sec.android.app.voicenote.provider.CursorProvider.getInstance()
                            java.lang.String r0 = r0.getPrevFilePath()
                            if (r0 == 0) goto L_0x08a9
                            com.sec.android.app.voicenote.service.Engine r2 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r2.clearContentItem()
                            com.sec.android.app.voicenote.service.Engine r2 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r2.initPlay()
                            com.sec.android.app.voicenote.service.Engine r2 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            int r0 = r2.startPlay((java.lang.String) r0)
                            r2 = -103(0xffffffffffffff99, float:NaN)
                            if (r0 == r2) goto L_0x089f
                            if (r0 == 0) goto L_0x0837
                            goto L_0x08a9
                        L_0x0837:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.updateNotification(r12)
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.updateCover(r12)
                            com.sec.android.app.voicenote.provider.CursorProvider r0 = com.sec.android.app.voicenote.provider.CursorProvider.getInstance()
                            r0.moveToPrevPosition()
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 51
                            r3 = -1
                            r0.sendMessageCallback(r2, r3)
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x0896
                            if (r4 != r15) goto L_0x086b
                            r0 = 2006(0x7d6, float:2.811E-42)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r0)
                            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = com.sec.android.app.voicenote.uicore.VoiceNoteObservable.getInstance()
                            r2 = 2006(0x7d6, float:2.811E-42)
                            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                            r0.notifyObservers(r2)
                            goto L_0x08a9
                        L_0x086b:
                            if (r4 != r14) goto L_0x0880
                            r0 = 3005(0xbbd, float:4.211E-42)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r0)
                            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = com.sec.android.app.voicenote.uicore.VoiceNoteObservable.getInstance()
                            r2 = 3005(0xbbd, float:4.211E-42)
                            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                            r0.notifyObservers(r2)
                            goto L_0x08a9
                        L_0x0880:
                            r0 = 7
                            if (r4 != r0) goto L_0x08a9
                            r0 = 6010(0x177a, float:8.422E-42)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r0)
                            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = com.sec.android.app.voicenote.uicore.VoiceNoteObservable.getInstance()
                            r2 = 6010(0x177a, float:8.422E-42)
                            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                            r0.notifyObservers(r2)
                            goto L_0x08a9
                        L_0x0896:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 13
                            r3 = -1
                            r0.sendMessageCallback(r2, r3)
                            goto L_0x08a9
                        L_0x089f:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 0
                            android.widget.Toast r0 = android.widget.Toast.makeText(r0, r8, r2)
                            r0.show()
                        L_0x08a9:
                            android.os.Handler r0 = new android.os.Handler
                            r0.<init>()
                            com.sec.android.app.voicenote.service.-$$Lambda$VoiceNoteService$2$6i3guhlCMZ3Q1O9gPMv1Z75w7Ns r2 = new com.sec.android.app.voicenote.service.-$$Lambda$VoiceNoteService$2$6i3guhlCMZ3Q1O9gPMv1Z75w7Ns
                            r2.<init>()
                            r3 = 300(0x12c, double:1.48E-321)
                            r0.postDelayed(r2, r3)
                            goto L_0x0c30
                        L_0x08ba:
                            if (r5 != r14) goto L_0x08da
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r0.pausePlay()
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x08d0
                            r0 = 2002(0x7d2, float:2.805E-42)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r0)
                            goto L_0x0c30
                        L_0x08d0:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 15
                            r3 = -1
                            r0.sendMessageCallback(r2, r3)
                            goto L_0x0c30
                        L_0x08da:
                            if (r5 != r15) goto L_0x0c30
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            boolean r0 = r0.mIsDelayingWhenSpecialAppClosed
                            if (r0 == 0) goto L_0x08e5
                            return
                        L_0x08e5:
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            int r0 = r0.resumePlay()
                            r2 = -103(0xffffffffffffff99, float:NaN)
                            if (r0 == r2) goto L_0x090c
                            if (r0 == 0) goto L_0x08f5
                            goto L_0x0c30
                        L_0x08f5:
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x0902
                            r0 = 2003(0x7d3, float:2.807E-42)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r0)
                            goto L_0x0c30
                        L_0x0902:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 16
                            r3 = -1
                            r0.sendMessageCallback(r2, r3)
                            goto L_0x0c30
                        L_0x090c:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 0
                            android.widget.Toast r0 = android.widget.Toast.makeText(r0, r8, r2)
                            r0.show()
                            goto L_0x0c30
                        L_0x0918:
                            if (r5 != r14) goto L_0x0938
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r0.pausePlay()
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x092e
                            r0 = 2002(0x7d2, float:2.805E-42)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r0)
                            goto L_0x0c30
                        L_0x092e:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 15
                            r3 = -1
                            r0.sendMessageCallback(r2, r3)
                            goto L_0x0c30
                        L_0x0938:
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            int r0 = r0.resumePlay()
                            r2 = -103(0xffffffffffffff99, float:NaN)
                            if (r0 == r2) goto L_0x095f
                            if (r0 == 0) goto L_0x0948
                            goto L_0x0c30
                        L_0x0948:
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x0955
                            r0 = 2003(0x7d3, float:2.807E-42)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r0)
                            goto L_0x0c30
                        L_0x0955:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 16
                            r3 = -1
                            r0.sendMessageCallback(r2, r3)
                            goto L_0x0c30
                        L_0x095f:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 0
                            android.widget.Toast r0 = android.widget.Toast.makeText(r0, r8, r2)
                            r0.show()
                            goto L_0x0c30
                        L_0x096b:
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            int r0 = r0.getPlayerState()
                            if (r0 == r12) goto L_0x0c30
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r0.stopPlay()
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 5
                            r0.updateCover(r2)
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.hideNotification()
                            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = com.sec.android.app.voicenote.uicore.VoiceNoteObservable.getInstance()
                            r2 = 998(0x3e6, float:1.398E-42)
                            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                            r0.notifyObservers(r2)
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x099e
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r14)
                            goto L_0x09a4
                        L_0x099e:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = -1
                            r0.sendMessageCallback(r10, r2)
                        L_0x09a4:
                            com.sec.android.app.voicenote.service.MetadataRepository r0 = com.sec.android.app.voicenote.service.MetadataRepository.getInstance()
                            r0.close()
                            goto L_0x0c30
                        L_0x09ad:
                            int r0 = com.sec.android.app.voicenote.uicore.VoiceNoteApplication.getScene()
                            if (r0 != r13) goto L_0x09d7
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r2 = 0
                            r0.pauseTranslation(r2)
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x09c8
                            r0 = 7002(0x1b5a, float:9.812E-42)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r0)
                            goto L_0x0c30
                        L_0x09c8:
                            r0 = 7002(0x1b5a, float:9.812E-42)
                            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r2 = com.sec.android.app.voicenote.uicore.VoiceNoteObservable.getInstance()
                            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                            r2.notifyObservers(r0)
                            goto L_0x0c30
                        L_0x09d7:
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r0.pausePlay()
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x09eb
                            r0 = 2002(0x7d2, float:2.805E-42)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r0)
                            goto L_0x0c30
                        L_0x09eb:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 15
                            r3 = -1
                            r0.sendMessageCallback(r2, r3)
                            goto L_0x0c30
                        L_0x09f5:
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            int r0 = r0.resumePlay()
                            r2 = -103(0xffffffffffffff99, float:NaN)
                            if (r0 == r2) goto L_0x0a1c
                            if (r0 == 0) goto L_0x0a05
                            goto L_0x0c30
                        L_0x0a05:
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x0a12
                            r0 = 2003(0x7d3, float:2.807E-42)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r0)
                            goto L_0x0c30
                        L_0x0a12:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 16
                            r3 = -1
                            r0.sendMessageCallback(r2, r3)
                            goto L_0x0c30
                        L_0x0a1c:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 0
                            android.widget.Toast r0 = android.widget.Toast.makeText(r0, r8, r2)
                            r0.show()
                            goto L_0x0c30
                        L_0x0a28:
                            r2 = 0
                            com.sec.android.app.voicenote.service.remote.RemoteViewManager r0 = com.sec.android.app.voicenote.service.remote.RemoteViewManager.getInstance()
                            boolean r0 = r0.isCoverClosed()
                            if (r0 == 0) goto L_0x0a3a
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.showRecordCancelDialogOnCover(r2)
                            goto L_0x0c30
                        L_0x0a3a:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.showRecordCancelDialog(r2)
                            goto L_0x0c30
                        L_0x0a41:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.hideRecordCancelDialog()
                            goto L_0x0c30
                        L_0x0a48:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            boolean r0 = com.sec.android.app.voicenote.provider.PermissionProvider.checkSavingEnable(r0)
                            if (r0 != 0) goto L_0x0a79
                            java.lang.String r0 = "just stop recording because permission error"
                            com.sec.android.app.voicenote.provider.Log.m26i(r9, r0)
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r0.cancelRecord()
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x0a66
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r15)
                            goto L_0x0a6d
                        L_0x0a66:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 2
                            r3 = -1
                            r0.sendMessageCallback(r2, r3)
                        L_0x0a6d:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.hideNotification()
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.stopSelf()
                            goto L_0x0c30
                        L_0x0a79:
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            int r0 = r0.getEngineState()
                            if (r0 == 0) goto L_0x0a85
                            goto L_0x0c30
                        L_0x0a85:
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            boolean r0 = r0.isSimpleRecorderMode()
                            if (r0 != 0) goto L_0x0c30
                            if (r6 == r12) goto L_0x0c30
                        L_0x0a91:
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            int r0 = r0.getContentItemCount()
                            if (r0 <= r12) goto L_0x0aad
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            boolean r0 = r0.isSaveEnable()
                            if (r0 != 0) goto L_0x0aad
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r0.removeUnableContentItems()
                            goto L_0x0a91
                        L_0x0aad:
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            boolean r0 = r0.isSaveEnable()
                            if (r0 != 0) goto L_0x0ae4
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r0.cancelRecord()
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.stopSelf()
                            java.lang.String r0 = "desktopMode_changed"
                            r3 = 0
                            boolean r0 = r2.getBooleanExtra(r0, r3)
                            if (r0 == 0) goto L_0x0ad2
                            java.lang.String r0 = "Intent from DesktopModeProvider - SKIP to sendMessageCallback"
                            com.sec.android.app.voicenote.provider.Log.m26i(r9, r0)
                            goto L_0x0ad9
                        L_0x0ad2:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 2
                            r3 = -1
                            r0.sendMessageCallback(r2, r3)
                        L_0x0ad9:
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x0c30
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r15)
                            goto L_0x0c30
                        L_0x0ae4:
                            boolean r0 = com.sec.android.app.voicenote.provider.VoiceNoteFeature.FLAG_SUPPORT_SHOW_REJECT_CALL_NUMBER
                            if (r0 == 0) goto L_0x0afd
                            com.sec.android.app.voicenote.provider.CallRejectChecker r0 = com.sec.android.app.voicenote.provider.CallRejectChecker.getInstance()
                            int r0 = r0.getRejectCallCount()
                            if (r0 == 0) goto L_0x0afd
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x0afd
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.showRejectCallInfoDialog()
                        L_0x0afd:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.hideNotification()
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            java.lang.String r0 = r0.getOriginalFilePath()
                            java.lang.StringBuilder r3 = new java.lang.StringBuilder
                            r3.<init>()
                            java.lang.String r5 = "SAVE - originalName : "
                            r3.append(r5)
                            r3.append(r0)
                            java.lang.String r5 = " scene : "
                            r3.append(r5)
                            r3.append(r4)
                            java.lang.String r3 = r3.toString()
                            com.sec.android.app.voicenote.provider.Log.m19d(r9, r3)
                            if (r0 == 0) goto L_0x0b4c
                            boolean r3 = r0.isEmpty()
                            if (r3 != 0) goto L_0x0b4c
                            r3 = 6
                            if (r4 != r3) goto L_0x0b4c
                            java.io.File r3 = new java.io.File
                            r3.<init>(r0)
                            java.lang.String r0 = r3.getName()
                            com.sec.android.app.voicenote.service.Engine r3 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r4 = 46
                            int r4 = r0.lastIndexOf(r4)
                            r5 = 0
                            java.lang.String r0 = r0.substring(r5, r4)
                            r3.setUserSettingName(r0)
                        L_0x0b4c:
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            long r3 = r0.stopRecord(r12, r12)
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.stopSelf()
                            r5 = 0
                            int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                            if (r0 >= 0) goto L_0x0b68
                            r5 = -2
                            int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                            if (r0 != 0) goto L_0x0b66
                            goto L_0x0b68
                        L_0x0b66:
                            r3 = 0
                            goto L_0x0b6e
                        L_0x0b68:
                            java.lang.String r0 = "list_mode"
                            r3 = 0
                            com.sec.android.app.voicenote.provider.Settings.setSettings((java.lang.String) r0, (int) r3)
                        L_0x0b6e:
                            java.lang.String r0 = "desktopMode_changed"
                            boolean r0 = r2.getBooleanExtra(r0, r3)
                            if (r0 == 0) goto L_0x0b7c
                            java.lang.String r0 = "Intent from DesktopModeProvider - SKIP to sendMessageCallback"
                            com.sec.android.app.voicenote.provider.Log.m26i(r9, r0)
                            goto L_0x0b82
                        L_0x0b7c:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = -1
                            r0.sendMessageCallback(r12, r2)
                        L_0x0b82:
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x0b8d
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r14)
                            goto L_0x0c30
                        L_0x0b8d:
                            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = com.sec.android.app.voicenote.uicore.VoiceNoteObservable.getInstance()
                            r0.notifyObservers(r11)
                            goto L_0x0c30
                        L_0x0b96:
                            r2 = 50
                            java.lang.Thread.sleep(r2)     // Catch:{ InterruptedException -> 0x0b9c }
                            goto L_0x0ba5
                        L_0x0b9c:
                            r0 = move-exception
                            r2 = r0
                            java.lang.String r0 = r2.toString()
                            com.sec.android.app.voicenote.provider.Log.m22e(r9, r0)
                        L_0x0ba5:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            android.content.Context r2 = r0.getApplicationContext()
                            boolean r0 = r0.isRestrictedByPolicy(r2)
                            if (r0 == 0) goto L_0x0bda
                            com.sec.android.app.voicenote.service.Recorder r0 = com.sec.android.app.voicenote.service.Recorder.getInstance()
                            int r0 = r0.getRecorderState()
                            r2 = 2
                            if (r0 != r2) goto L_0x0c30
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r0.stopRecord(r12, r12)
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.stopSelf()
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x0bd2
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r14)
                            goto L_0x0c30
                        L_0x0bd2:
                            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = com.sec.android.app.voicenote.uicore.VoiceNoteObservable.getInstance()
                            r0.notifyObservers(r11)
                            goto L_0x0c30
                        L_0x0bda:
                            if (r0 != 0) goto L_0x0c30
                            java.lang.String r0 = "Don't save when opened special app"
                            com.sec.android.app.voicenote.provider.Log.m26i(r9, r0)
                            goto L_0x0c30
                        L_0x0be2:
                            java.lang.String r0 = "FLAG_KEEP_RECORDING_WHEN_ACCEPT_CALL"
                            com.sec.android.app.voicenote.provider.Log.m26i(r9, r0)
                            goto L_0x0c30
                        L_0x0be8:
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r0.cancelRecord()
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x0bfb
                            r0 = 1006(0x3ee, float:1.41E-42)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r0)
                            goto L_0x0c30
                        L_0x0bfb:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = 2
                            r3 = -1
                            r0.sendMessageCallback(r2, r3)
                            goto L_0x0c30
                        L_0x0c03:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r0.backgroundVoiceNoteRecResume()
                            goto L_0x0c30
                        L_0x0c09:
                            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                            r0.pauseRecord()
                            int r0 = com.sec.android.app.voicenote.service.VoiceNoteService.Helper.connectionCount()
                            if (r0 != 0) goto L_0x0c2a
                            r0 = 6
                            if (r4 == r0) goto L_0x0c24
                            r0 = 8
                            if (r4 == r0) goto L_0x0c1e
                            goto L_0x0c30
                        L_0x0c1e:
                            r0 = 1002(0x3ea, float:1.404E-42)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r0)
                            goto L_0x0c30
                        L_0x0c24:
                            r0 = 5005(0x138d, float:7.013E-42)
                            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r0)
                            goto L_0x0c30
                        L_0x0c2a:
                            com.sec.android.app.voicenote.service.VoiceNoteService r0 = com.sec.android.app.voicenote.service.VoiceNoteService.this
                            r2 = -1
                            r0.sendMessageCallback(r14, r2)
                        L_0x0c30:
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.VoiceNoteService.C07752.onReceive(android.content.Context, android.content.Intent):void");
                    }

                    public /* synthetic */ void lambda$onReceive$0$VoiceNoteService$2() {
                        VoiceNoteService.this.enableNextPrevNotification(true);
                    }

                    public /* synthetic */ void lambda$onReceive$1$VoiceNoteService$2() {
                        VoiceNoteService.this.enableNextPrevNotification(true);
                    }
                };
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(BACKGROUND_VOICENOTE_REC_PAUSE);
                intentFilter.addAction(BACKGROUND_VOICENOTE_REC_RESUME);
                intentFilter.addAction(BACKGROUND_VOICENOTE_REC_PLAY_TOGGLE);
                intentFilter.addAction(BACKGROUND_VOICENOTE_SAVE);
                intentFilter.addAction(BACKGROUND_VOICENOTE_SAVE_BY_OTHER_APP);
                intentFilter.addAction(BACKGROUND_VOICENOTE_PLAY);
                intentFilter.addAction(BACKGROUND_VOICENOTE_PLAY_PAUSE);
                intentFilter.addAction(BACKGROUND_VOICENOTE_PLAY_STOP);
                intentFilter.addAction(BACKGROUND_VOICENOTE_PLAY_TOGGLE);
                intentFilter.addAction(BACKGROUND_VOICENOTE_PLAY_PREV);
                intentFilter.addAction(BACKGROUND_VOICENOTE_PLAY_NEXT);
                intentFilter.addAction(BACKGROUND_VOICENOTE_PLAY_STOP_QUIT);
                intentFilter.addAction(BACKGROUND_VOICENOTE_REC_NEW);
                intentFilter.addAction(BACKGROUND_VOICENOTE_STANDBY);
                intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
                intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
                intentFilter.addAction(BACKGROUND_VOICENOTE_CANCEL);
                intentFilter.addAction(BACKGROUND_VOICENOTE_CANCEL_DIALOG);
                intentFilter.addAction(BACKGROUND_VOICENOTE_CANCEL_DIALOG_CLOSE);
                intentFilter.addAction(BACKGROUND_VOICENOTE_CANCEL_KEYGUARD);
                intentFilter.addAction("com.samsung.telecom.IncomingCallAnsweredDuringRecord");
                intentFilter.addAction(BACKGROUND_VOICENOTE_QUICK_PANEL_SHOW);
                intentFilter.addAction(BACKGROUND_VOICENOTE_QUICK_PANEL_HIDE);
                intentFilter.addAction(BACKGROUND_VOICENOTE_UPDATE_NOTIFICATION);
                intentFilter.addAction(BACKGROUND_VOICENOTE_HIDE_NOTIFICATION);
                intentFilter.addAction(BACKGROUND_VOICENOTE_TRANSLATION_TOGGLE);
                intentFilter.addAction(BACKGROUND_VOICENOTE_TRANSLATION_SAVE);
                intentFilter.addAction(BACKGROUND_VOICENOTE_TRANSLATION_FILE_PLAY);
                intentFilter.addAction(BACKGROUND_VOICENOTE_TRANSLATION_CANCEL_KEYGUARD);
                intentFilter.addAction(BACKGROUND_VOICENOTE_NO_ACTION);
                intentFilter.addAction(VOICENOTE_SCREEN_OFF);
                intentFilter.addAction(VOICENOTE_SCREEN_ON);
                intentFilter.addAction("com.sec.android.app.voicenote.cover_close");
                intentFilter.addAction(VOICENOTE_COVER_OPEN);
                intentFilter.addAction(VOICENOTE_SD_MOUNT);
                intentFilter.addAction("com.sec.android.app.voicenote.sd_unmount");
                intentFilter.addAction(VOICENOTE_SHOW_MODE_NOT_SUPPORTED);
                intentFilter.addAction(VOICENOTE_HIDE_MODE_NOT_SUPPORTED);
                intentFilter.addAction(VOICENOTE_NETWORK_ON);
                intentFilter.addAction(VOICENOTE_NETWORK_OFF);
                intentFilter.addAction(VOICENOTE_DEVICE_STORAGE_LOW);
                intentFilter.addAction(VOICENOTE_DEVICE_LOCKED);
                intentFilter.addAction(VOICENOTE_DEVICE_UNLOCKED);
//                LocalBroadcastManager.getInstance(this).registerReceiver(this.mBroadcastReceiverNotification, intentFilter);
                registerReceiver(this.mBroadcastReceiverNotification, intentFilter, Manifest.permission.Controller, (Handler) null);
            } else {
                return;
            }
        } else {
            Log.m26i(TAG, "unregister notification broadcastReceiver");
            if (this.mBroadcastReceiverNotification != null) {
//                LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mBroadcastReceiverNotification);
                unregisterReceiver(this.mBroadcastReceiverNotification);
                this.mBroadcastReceiverNotification = null;
            }
        }
        Trace.endSection();
    }

    private void registerBroadcastCamera(boolean z) {
        if (z) {
            if (this.mBroadcastReceiverNotificationCamera == null) {
                this.mBroadcastReceiverNotificationCamera = new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        long currentTimeMillis = System.currentTimeMillis();
                        int i = currentTimeMillis - VoiceNoteService.this.mLastCameraEvent > VoiceNoteService.THRESHOLD_CAMERA_OPEN_STOP_RECORDING_IMMEDIATELY ? 0 : 1000;
                        long unused = VoiceNoteService.this.mLastCameraEvent = currentTimeMillis;
                        String action = intent.getAction();
                        int scene = VoiceNoteApplication.getScene();
                        Log.m26i(VoiceNoteService.TAG, "onReceive Camera action : " + action + ", scene : " + scene + ", delayHandleCameraStart = " + i);
                        char c = 65535;
                        int hashCode = action.hashCode();
                        if (hashCode != -166366242) {
                            if (hashCode == 1795749094 && action.equals(VoiceNoteService.VOICENOTE_CAMERA_STOP)) {
                                c = 1;
                            }
                        } else if (action.equals(VoiceNoteService.VOICENOTE_CAMERA_START)) {
                            c = 0;
                        }
                        if (c != 0) {
                            if (c == 1) {
                                if (scene == 8 || scene == 6) {
                                    boolean unused2 = VoiceNoteService.this.mIsDelayingWhenSpecialAppClosed = true;
                                    VoiceNoteService.this.mCameraActionHandler.removeMessages(1);
                                    VoiceNoteService.this.mCameraActionHandler.removeMessages(2);
                                    VoiceNoteService.this.mCameraActionHandler.sendEmptyMessageDelayed(2, VoiceNoteService.THRESHOLD_CAMERA_OPEN_STOP_RECORDING_IMMEDIATELY);
                                    return;
                                }
                                Log.m26i(VoiceNoteService.TAG, "break VOICENOTE_CAMERA_STOP by scene");
                            }
                        } else if (scene == 8 || scene == 6) {
                            VoiceNoteService.this.mCameraActionHandler.removeMessages(1);
                            VoiceNoteService.this.mCameraActionHandler.removeMessages(2);
                            Message message = new Message();
                            message.what = 1;
                            message.arg1 = scene;
                            VoiceNoteService.this.mCameraActionHandler.sendMessageDelayed(message, (long) i);
                        } else {
                            Log.m26i(VoiceNoteService.TAG, "break VOICENOTE_CAMERA_START by scene");
                        }
                    }
                };
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(VOICENOTE_CAMERA_START);
                intentFilter.addAction(VOICENOTE_CAMERA_STOP);
                registerReceiver(this.mBroadcastReceiverNotificationCamera, intentFilter);
//                LocalBroadcastManager.getInstance(this).registerReceiver(this.mBroadcastReceiverNotificationCamera, intentFilter);
            }
        } else if (this.mBroadcastReceiverNotificationCamera != null) {
//            LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mBroadcastReceiverNotificationCamera);
            unregisterReceiver(this.mBroadcastReceiverNotificationCamera);
            this.mBroadcastReceiverNotificationCamera = null;
        }
    }

    public static class Helper {
        private static final String TAG = "Helper";
        private static Hashtable<Context, ServiceBinder> mConnectionMap = new Hashtable<>();

        public static boolean bindToService(Context context, ServiceConnection serviceConnection) {
            Log.m26i(TAG, "bindToService");
            try {
                Trace.beginSection("VNService.bindToService");
                if (context.startService(new Intent(context, VoiceNoteService.class)) != null) {
                    ServiceBinder serviceBinder = new ServiceBinder(serviceConnection);
                    mConnectionMap.put(context, serviceBinder);
                    Log.m29v(TAG, "bindToService - connection size : " + mConnectionMap.size());
                    return context.bindService(new Intent().setClass(context, VoiceNoteService.class), serviceBinder, 0);
                }
                Trace.endSection();
                return false;
            } finally {
                Trace.endSection();
            }
        }

        public static void unbindFromService(Context context) {
            Log.m26i(TAG, "unbindFromService");
            ServiceBinder remove = mConnectionMap.remove(context);
            if (remove != null) {
                context.unbindService(remove);
            }
        }

        public static int connectionCount() {
            if (mConnectionMap == null) {
                Log.m29v(TAG, "connectionCount - connectionMap is null");
                return 0;
            }
            Log.m29v(TAG, "connectionCount - connectionMap size : " + mConnectionMap.size());
            return mConnectionMap.size();
        }

        public static boolean isConnectedContext(Context context) {
            Enumeration<Context> keys = mConnectionMap.keys();
            while (keys.hasMoreElements()) {
                if (keys.nextElement().getClass().getSimpleName().equals(context.getClass().getSimpleName())) {
                    return true;
                }
            }
            return false;
        }

        private static class ServiceBinder implements ServiceConnection {
            ServiceConnection mServiceConnection;

            ServiceBinder(ServiceConnection serviceConnection) {
                this.mServiceConnection = serviceConnection;
            }

            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Trace.beginSection("VNService.onServiceConnected");
                ServiceConnection serviceConnection = this.mServiceConnection;
                if (serviceConnection != null) {
                    serviceConnection.onServiceConnected(componentName, iBinder);
                }
                Trace.endSection();
            }

            public void onServiceDisconnected(ComponentName componentName) {
                ServiceConnection serviceConnection = this.mServiceConnection;
                if (serviceConnection != null) {
                    serviceConnection.onServiceDisconnected(componentName);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void sendMessageCallback(int i, int i2) {
        String str;
        String str2;
        synchronized (this.mCallbacks) {
            try {
                int beginBroadcast = this.mCallbacks.beginBroadcast();
                for (int i3 = 0; i3 < beginBroadcast; i3++) {
                    this.mCallbacks.getBroadcastItem(i3).messageCallback(i, i2);
                }
                try {
                    this.mCallbacks.finishBroadcast();
                } catch (IllegalStateException e) {
                    e = e;
                    str = TAG;
                    str2 = "IllegalStateException";
                }
            } catch (RemoteException e2) {
                try {
                    Log.m24e(TAG, "RemoteException", (Throwable) e2);
                    try {
                        this.mCallbacks.finishBroadcast();
                    } catch (IllegalStateException e3) {
//                        e = e3;
                        str = TAG;
                        str2 = "IllegalStateException";
                    }
                } catch (Throwable th) {
                    try {
                        this.mCallbacks.finishBroadcast();
                    } catch (IllegalStateException e4) {
                        Log.m24e(TAG, "IllegalStateException", (Throwable) e4);
                    }
                    throw th;
                }
            }
        }
//        Log.m24e(str, str2, (Throwable) e);
    }

    /* access modifiers changed from: package-private */
    public void showMuteDetectedDialog(boolean z) {
        Log.m26i(TAG, "showMuteDetectedDialog - withVibration : " + z);
        if (this.mWarningMuteDetectedDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, C0690R.style.DialogForService));
            AudioManager audioManager = (AudioManager) getSystemService("audio");
            View inflate = View.inflate(this, C0690R.layout.dialog_no_sound, (ViewGroup) null);
            ViewProvider.setMaxFontSize(getBaseContext(), (TextView) inflate.findViewById(C0690R.C0693id.mute_no_sound_detected));
            ViewProvider.setMaxFontSize(getBaseContext(), (TextView) inflate.findViewById(C0690R.C0693id.mute_detected_text));
            boolean isTalkBackOn = VRUtil.isTalkBackOn(getApplicationContext());
            inflate.setOnTouchListener(new View.OnTouchListener() {
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    return VoiceNoteService.this.lambda$showMuteDetectedDialog$16$VoiceNoteService(view, motionEvent);
                }
            });
            builder.setOnKeyListener($$Lambda$VoiceNoteService$zoXtENLpTFTNWL5jX3JvDDCWJM.INSTANCE);
            if (Settings.getIntSettings("record_mode", 1) == 2) {
                ((TextView) inflate.findViewById(C0690R.C0693id.mute_detected_text)).setText(C0690R.string.mute_detected_popup_text_2);
                ImageView imageView = (ImageView) inflate.findViewById(C0690R.C0693id.mute_detected_img);
                if (VoiceNoteFeature.FLAG_IS_FOLDER_PHONE(this)) {
                    imageView.setImageResource(C0690R.C0692drawable.voice_recorder_popup_no_sound_detected_for_folder_phone);
                } else if (VoiceNoteFeature.FLAG_IS_TABLET) {
                    imageView.setImageResource(C0690R.C0692drawable.ic_voice_recorder_help_no_sound_detected_tablet);
                } else {
                    imageView.setImageResource(C0690R.C0692drawable.voice_recorder_help_no_sound_detected);
                }
            }
            builder.setCancelable(true);
            builder.setView(inflate);
            this.mWarningMuteDetectedDialog = builder.create();
            this.mWarningMuteDetectedDialog.getWindow().setType(2009);
            this.mWarningMuteDetectedDialog.getWindow().addFlags(2097152);
            if (!isTalkBackOn) {
                inflate.postDelayed(new Runnable() {
                    public final void run() {
                        VoiceNoteService.this.lambda$showMuteDetectedDialog$18$VoiceNoteService();
                    }
                }, 6000);
            }
            this.mWarningMuteDetectedDialog.setCanceledOnTouchOutside(true);
            this.mWarningMuteDetectedDialog.show();
            if (DisplayManager.isDeviceOnLandscape()) {
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(this.mWarningMuteDetectedDialog.getWindow().getAttributes());
                layoutParams.width = (DisplayManager.getFullScreenWidth() * 60) / 100;
                layoutParams.height = -2;
                this.mWarningMuteDetectedDialog.getWindow().setAttributes(layoutParams);
            }
            if (z && audioManager.getRingerMode() != 0) {
                Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService("vibrator");
                vibrator.cancel();
//                vibrator.semVibrate(50028, -1, (AudioAttributes) null, Vibrator.SemMagnitudeTypes.TYPE_NOTIFICATION);
            }
        }
    }

    public /* synthetic */ boolean lambda$showMuteDetectedDialog$16$VoiceNoteService(View view, MotionEvent motionEvent) {
        dismissMuteDetectedDialog();
        return false;
    }

    public /* synthetic */ void lambda$showMuteDetectedDialog$18$VoiceNoteService() {
        Log.m26i(TAG, "Runnable called ");
        dismissMuteDetectedDialog();
    }

    /* access modifiers changed from: package-private */
    public void dismissMuteDetectedDialog() {
        Log.m26i(TAG, "dismissMuteDetectedDialog");
        AlertDialog alertDialog = this.mWarningMuteDetectedDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mWarningMuteDetectedDialog = null;
        }
    }

    /* access modifiers changed from: private */
    public void saveBackgroundTranslationFile() {
        Log.m26i(TAG, "saveBackgroundTranslationFile");
        Engine.getInstance().setCategoryID(2);
        Engine.getInstance().stopTranslation(true);
        RemoteViewManager.getInstance().enableUpdate(true);
        updateNotification(9);
        if (RemoteViewManager.getInstance().isCoverClosed()) {
            hideCover();
        }
    }

    private void saveFileAfterWriteSttData() {
        Log.m19d(TAG, "onEngineUpdate - complete converting and update notification title");
        if (Helper.connectionCount() == 0) {
            RemoteViewManager.getInstance().enableUpdate(true);
            updateNotification(10);
            VoiceNoteApplication.saveEvent(3);
        } else {
            if (RemoteViewManager.getInstance().isCoverClosed()) {
                RemoteViewManager.getInstance().enableUpdate(true);
                updateNotification(10);
            }
            VoiceNoteObservable.getInstance().notifyObservers(3);
        }
        this.mCompleteCount = 0;
    }

    /* access modifiers changed from: private */
    public void pauseRecordByOpenOtherApp(int i) {
        Log.m26i(TAG, "pauseRecordByOpenOtherApp - scene : " + i);
        if (!Engine.getInstance().pauseRecord()) {
            return;
        }
        if (i == 8) {
            Toast.makeText(getApplicationContext(), C0690R.string.recording_pause, 0).show();
            Engine.getInstance().setAutoResumeRecording(true);
            VoiceNoteObservable.getInstance().notifyObservers(1002);
            Engine.getInstance().startOverwrite(-1);
            return;
        }
        Engine.getInstance().setAutoResumeRecording(false);
        VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.EDIT_RECORD_PAUSE));
    }

    /* access modifiers changed from: private */
    public void backgroundVoiceNoteRecResume() {
        int scene = VoiceNoteApplication.getScene();
        int resumeRecord = Engine.getInstance().resumeRecord();
        if (resumeRecord == -120) {
            Toast.makeText(this, C0690R.string.recording_now, 0).show();
        } else if (resumeRecord != -102) {
            if (resumeRecord != 0) {
                switch (resumeRecord) {
                    case -106:
                        showNetworkNotConnectedDialog();
                        return;
                    case -105:
                        String string = getString(C0690R.string.speech_to_text_mode);
                        Toast.makeText(this, getString(C0690R.string.mode_is_not_available, new Object[]{string, string}), 0).show();
                        return;
                    case -104:
                        String string2 = getString(C0690R.string.interview_mode);
                        Toast.makeText(this, getString(C0690R.string.mode_is_not_available, new Object[]{string2, string2}), 0).show();
                        return;
                    default:
                        return;
                }
            } else if (Helper.connectionCount() != 0) {
                sendMessageCallback(4, -1);
            } else if (scene == 6) {
                VoiceNoteApplication.saveEvent(Event.EDIT_RECORD);
            } else if (scene == 8) {
                VoiceNoteApplication.saveEvent(1003);
            }
        } else if (PhoneStateProvider.getInstance().isDuringCall(this)) {
            Toast.makeText(this, C0690R.string.no_rec_during_call, 0).show();
        } else {
            Toast.makeText(this, C0690R.string.no_rec_during_incoming_calls, 0).show();
        }
    }

    private class CameraActionHandler extends Handler {
        static final int CAMERA_START = 1;
        static final int CAMERA_STOP = 2;

        private CameraActionHandler() {
        }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            int recorderState = Engine.getInstance().getRecorderState();
            Log.m26i(VoiceNoteService.TAG, " CameraActionHandler - what : " + message.what + " - mIsOpenedSpecialApp : " + VoiceNoteService.this.mIsOpenedSpecialApp + " - recorderState : " + recorderState);
            int i = message.what;
            if (i != 1) {
                if (i == 2) {
                    boolean isAutoResumeRecording = Engine.getInstance().isAutoResumeRecording();
                    Engine.getInstance().setAutoResumeRecording(false);
                    if (!isAutoResumeRecording || Engine.getInstance().getPlayerState() != 1) {
                        Log.m26i(VoiceNoteService.TAG, "Don't auto resume recording");
                    } else if (!PhoneStateProvider.getInstance().isDuringCall(VoiceNoteService.this) && VoiceNoteService.this.mIsOpenedSpecialApp) {
                        boolean unused = VoiceNoteService.this.mIsOpenedSpecialApp = false;
                        Engine.getInstance().setOpenedSpecialApp(VoiceNoteService.this.mIsOpenedSpecialApp);
                        Toast.makeText(VoiceNoteService.this.getApplicationContext(), C0690R.string.recording_resume, 0).show();
                        VoiceNoteService.this.backgroundVoiceNoteRecResume();
                        boolean unused2 = VoiceNoteService.this.mIsDelayingWhenSpecialAppClosed = false;
                    }
                }
            } else if (!VoiceNoteService.this.mIsOpenedSpecialApp || recorderState == 2) {
                boolean unused3 = VoiceNoteService.this.mIsOpenedSpecialApp = true;
                Engine.getInstance().setOpenedSpecialApp(VoiceNoteService.this.mIsOpenedSpecialApp);
                VoiceNoteService.this.pauseRecordByOpenOtherApp(message.arg1);
            }
        }
    }

    public static void setCallRejectingServiceEnabled(boolean z, boolean z2) {
        Log.m19d(TAG, "setServiceEnabled START = " + z);
        VoiceNoteApplication application = VoiceNoteApplication.getApplication();
        PackageManager packageManager = application.getPackageManager();
        int i = z ? 1 : 2;
        if (z2) {
            packageManager.setComponentEnabledSetting(new ComponentName(application, TelephonyCallScreeningService.class), i, 1);
            if (VoiceNoteApplication.getScene() == 8) {
                CallRejectChecker.getInstance().setReject(z);
            }
            if (z) {
                VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.RECORD_CALL_REJECT));
            } else {
                VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.RECORD_CALL_ALLOW));
            }
            Log.m19d(TAG, "setServiceEnabled END - from Settings");
        } else if (Settings.getBooleanSettings(Settings.KEY_REC_CALL_REJECT) && VoiceNoteApplication.getScene() != 6 && !Engine.getInstance().isSimpleRecorderMode()) {
            packageManager.setComponentEnabledSetting(new ComponentName(application, TelephonyCallScreeningService.class), i, 1);
            Log.m19d(TAG, "setServiceEnabled END");
        }
    }

    /* access modifiers changed from: private */
    public boolean isRestrictedByPolicy(Context context) {
        Cursor query;
        Uri parse = Uri.parse("content://com.sec.knox.provider/RestrictionPolicy2");
        if (context == null || (query = context.getContentResolver().query(parse, (String[]) null, "isMicrophoneEnabled", new String[]{DeviceInfo.STR_TRUE}, (String) null)) == null) {
            return false;
        }
        try {
            query.moveToFirst();
            if (query.getString(query.getColumnIndex("isMicrophoneEnabled")).equals("false")) {
                Log.m26i(TAG, "isRestrictedByPolicy - Microphone is disabled.");
                return true;
            }
            query.close();
            return false;
        } finally {
            query.close();
        }
    }
}
