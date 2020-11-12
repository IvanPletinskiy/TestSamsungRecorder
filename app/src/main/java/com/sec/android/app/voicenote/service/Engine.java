package com.sec.android.app.voicenote.service;

import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.FileObserver;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.provider.CallRejectChecker;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.DBProvider;
import com.sec.android.app.voicenote.provider.DesktopModeProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Network;
import com.sec.android.app.voicenote.provider.PermissionProvider;
import com.sec.android.app.voicenote.provider.PhoneStateProvider;
import com.sec.android.app.voicenote.provider.PrivateModeProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.StorageProvider;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.service.AudioFormat;
import com.sec.android.app.voicenote.service.Editor;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.Player;
import com.sec.android.app.voicenote.service.Recorder;
import com.sec.android.app.voicenote.service.VoiceNoteService;
import com.sec.android.app.voicenote.service.codec.M4aInfo;
import com.sec.android.app.voicenote.service.decoder.Decoder;
import com.sec.android.app.voicenote.service.helper.BluetoothHelper;
import com.sec.android.app.voicenote.uicore.MediaSessionManager;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import static com.sec.android.app.voicenote.service.Engine.ReturnCodes.CAN_NOT_START_RECORD_WHILE_RECORDING;

public class Engine implements Editor.OnEditorListener, Player.OnPlayerListener, Recorder.OnRecorderListener {
    public static final int INFO_CURRENT_TIME = 101;
    public static final int INFO_PROGRESS_DIALOG = 105;
    public static final int INFO_REPEAT_TIME = 104;
    public static final int INFO_SAVED_ID = 103;
    public static final int INFO_TRIM_TIME = 102;
    private static final String TAG = "Engine";
    private static Engine mInstance;
    private boolean isOpenedSpecialApp = false;
    /* access modifiers changed from: private */
    public Context mAppContext = null;
    private AudioFormat mAudioFormat = null;
    private long mCategoryID = 0;
    private final Stack<ContentItem> mContentItemStack = new Stack<>();
    private int mCurrentTime = 0;
    private int mEngineState = 0;
    private FileEventObserver mFileObserver = null;
    private boolean mIsBluetoothHeadSetConnected = false;
    private boolean mIsBluetoothSCOConnected = false;
    private boolean mIsNeedExternalMicAlert = false;
    private boolean mIsNeedReleaseMediaSession = false;
    private boolean mIsRecordByBluetoothSCO = false;
    private boolean mIsRestoreTempFile = false;
    private boolean mIsShowingToastAfterSaveTranslationFile = false;
    private boolean mIsWiredHeadSetConnected = false;
    private String mLastSavedFilePath = null;
    private final ArrayList<WeakReference<OnEngineListener>> mListeners = new ArrayList<>();
    private String mOriginalFilePath = null;
    private final int[] mOverwriteTime = new int[2];
    /* access modifiers changed from: private */
    public int mScene = 0;
    private boolean mScreenOff = false;
    private boolean mShowToast = false;
    private long mSimpleModeItemId;
    private boolean mSimplePlayerMode = false;
    private boolean mSimpleRecorderMode = false;
    private final int[] mTrimTime = new int[2];
    private String mUserSettingName = null;

    public static class EngineState {
        public static final int EDITING = 2;
        public static final int IDLE = 0;
        public static final int PROGRESSING = 3;
        public static final int SAVING = 1;
    }

    public interface OnEngineListener {
        void onEngineUpdate(int i, int i2, int i3);
    }

    public static class ReturnCodes {
        public static final int ANOTHER_RECORDER_ALREADY_RUNNING = -120;
        public static final int BUSY = -119;
        public static final int CAN_NOT_RESUME_RECORD_WHILE_IDLE = -110;
        public static final int CAN_NOT_START_DELETE = -113;
        public static final int CAN_NOT_START_OVERWRITE = -112;
        public static final int CAN_NOT_START_RECORD_WHILE_RECORDING = -108;
        public static final int CAN_NOT_START_TRIM = -111;
        public static final int ERROR_BASE = -100;
        public static final int INTERVIEW_MODE_NOT_SUPPORTED = -104;
        public static final int LOW_BATTERY = -121;
        public static final int MICROPHONE_RESTRICTED = -124;
        public static final int NETWORK_NOT_CONNECTED = -106;
        public static final int NOT_ENOUGH_STORAGE = -107;

        /* renamed from: OK */
        public static final int f106OK = 0;
        public static final int OVERWRITE_FAIL = -117;
        public static final int PLAY_DURING_CALL = -103;
        public static final int PLAY_DURING_INCOMING_CALLS = -122;
        public static final int PLAY_DURING_RECORDING = -123;
        public static final int PLAY_FAIL = -115;
        public static final int RECORD_DURING_CALL = -102;
        public static final int RECORD_FAIL = -114;
        public static final int REQUEST_AUDIO_FOCUS_FAIL = -109;
        public static final int STACK_SIZE_ERROR = -118;
        public static final int TRIM_FAIL = -116;
        public static final int UNKNOWN = -101;
        public static final int VOICEMEMO_MODE_NOT_SUPPORTED = -105;
        public static final int WAIT = -2;
    }

    public boolean isBluetoothSCOConnected() {
        return false;
    }

    public boolean isRecordByBluetoothSCO() {
        return false;
    }

    private class FileEventObserver extends FileObserver {
        private final Handler mFileEventHandler;
        private final Runnable mFileEventRunnable;
        private String mPath;

        public /* synthetic */ void lambda$new$0$Engine$FileEventObserver() {
            String str;
            String path = Player.getInstance().getPath();
            File file = new File(path);
            if (path.isEmpty() || (str = this.mPath) == null) {
                Log.m29v(Engine.TAG, "path is empty");
            } else if (path.equals(str) && !file.exists()) {
                Log.m29v(Engine.TAG, "run - stop play and close play scene");
                if (Engine.this.mScene == 12) {
                    Engine.this.cancelTranslation(true);
                }
                Engine.this.stopPlay();
                Engine.this.notifyObservers(2010, 5, -1);
            }
        }

        private FileEventObserver(String str, int i) {
            super(str, i);
            this.mFileEventHandler = new Handler(Engine.this.mAppContext.getMainLooper());
            this.mPath = null;
            this.mFileEventRunnable = new Runnable() {
                public final void run() {
                    Engine.FileEventObserver.this.lambda$new$0$Engine$FileEventObserver();
                }
            };
        }

        public void onEvent(int i, String str) {
            Log.m26i(Engine.TAG, "onEvent - playing file is something changed");
            this.mFileEventHandler.post(this.mFileEventRunnable);
        }

        public void setPath(String str) {
            this.mPath = str;
        }
    }

    public static Engine getInstance() {
        if (mInstance == null) {
            synchronized (Engine.class) {
                if (mInstance == null) {
                    mInstance = new Engine();
                }
            }
        }
        return mInstance;
    }

    public void setApplicationContext(Context context) {
        this.mAppContext = context;
        Recorder.getInstance().setApplicationContext(context);
        Player.getInstance().setApplicationContext(context);
    }

    private Engine() {
        Recorder.getInstance().registerListener(this);
        Player.getInstance().registerListener(this);
        Editor.getInstance().registerListener(this);
        resetOverwriteTime();
        resetTrimTime();
    }

    public void removeUnableContentItems() {
        deleteContentItemTempFile(popContentItem());
        ContentItem peekContentItem = peekContentItem();
        Recorder.getInstance().setRecordStartTime(peekContentItem.getStartTime());
        Recorder.getInstance().setRecordEndTime(peekContentItem.getEndTime());
    }

    public synchronized int getContentItemCount() {
        Log.m26i(TAG, "getContentItemCount size : " + this.mContentItemStack.size());
        return this.mContentItemStack.size();
    }

    private synchronized void pushContentItem(ContentItem contentItem) {
        if (contentItem != null) {
            Log.m19d(TAG, "pushContentItem sTime : " + contentItem.getStartTime() + " eTime : " + contentItem.getEndTime() + " dTime : " + contentItem.getDuration() + " title : " + getTitle(contentItem.getPath()) + " stack size : " + this.mContentItemStack.size());
            this.mContentItemStack.push(contentItem);
        }
    }

    private synchronized ContentItem popContentItem() {
        if (getContentItemCount() <= 0) {
            return null;
        }
        ContentItem pop = this.mContentItemStack.pop();
        Log.m19d(TAG, "popContentItem sTime : " + pop.getStartTime() + " eTime : " + pop.getEndTime() + " dTime : " + pop.getDuration() + " title : " + getTitle(pop.getPath()) + " stack size : " + this.mContentItemStack.size());
        return pop;
    }

    public synchronized ContentItem peekContentItem() {
        if (getContentItemCount() <= 0) {
            return null;
        }
        ContentItem peek = this.mContentItemStack.peek();
        Log.m19d(TAG, "peekContentItem sTime : " + peek.getStartTime() + " eTime : " + peek.getEndTime() + " dTime : " + peek.getDuration() + " path : " + peek.getPath() + " stack size : " + this.mContentItemStack.size());
        return peek;
    }

    /* access modifiers changed from: package-private */
    public synchronized ContentItem getContentItem(int i) {
        if (getContentItemCount() <= i) {
            return null;
        }
        ContentItem contentItem = (ContentItem) this.mContentItemStack.get(i);
        Log.m19d(TAG, "getContentItem sTime : " + contentItem.getStartTime() + " eTime : " + contentItem.getEndTime() + " dTime : " + contentItem.getDuration() + " path : " + contentItem.getPath() + " stack size : " + this.mContentItemStack.size());
        return contentItem;
    }

    public String getRecentFilePath() {
        if (this.mContentItemStack.size() < 1) {
            return "";
        }
        String path = this.mContentItemStack.peek().getPath();
        Log.m19d(TAG, "getRecentFilePath path : " + path);
        return path;
    }

    /* access modifiers changed from: package-private */
    public int getRecentFileDuration() {
        if (this.mContentItemStack.size() < 1) {
            return 0;
        }
        int duration = this.mContentItemStack.peek().getDuration();
        Log.m26i(TAG, "getRecentFileDuration duration : " + duration);
        return duration;
    }

    public boolean restoreTempFile() {
        Log.m19d(TAG, "restoreTempFile");
        this.mIsRestoreTempFile = false;
        File file = new File(StorageProvider.getRestoreTempFilePath());
        if (!file.isDirectory() || !file.exists()) {
            Log.m19d(TAG, "restoreTempFile : temp folder is not exist");
            return false;
        }
        File[] listFiles = file.listFiles($$Lambda$Engine$YInEbNu6x2DakCkluGX4fdbhrVM.INSTANCE);
        Log.m19d(TAG, "restoreTempFile : temp folder is exist");
        if (listFiles == null || listFiles.length <= 0) {
            clearContentItem();
            return false;
        }
        saveTempFile(listFiles[listFiles.length - 1].getPath());
        if (listFiles.length > 1) {
            try {
                Thread.sleep(1000);
                saveTempFile(listFiles[listFiles.length - 2].getPath());
            } catch (InterruptedException e) {
                Log.m24e(TAG, "InterruptedException !", (Throwable) e);
            }
        }
        clearContentItem();
        this.mIsRestoreTempFile = true;
        return true;
    }

    static /* synthetic */ boolean lambda$restoreTempFile$0(File file, String str) {
        return str.endsWith(AudioFormat.ExtType.EXT_M4A) || str.endsWith(AudioFormat.ExtType.EXT_AMR);
    }

    public boolean isRestoreTempFile() {
        return this.mIsRestoreTempFile;
    }

    private synchronized int getMaxDuration(int i) {
        Iterator it = this.mContentItemStack.iterator();
        while (it.hasNext()) {
            ContentItem contentItem = (ContentItem) it.next();
            if (contentItem.getEndTime() > i) {
                i = contentItem.getEndTime();
            }
        }
        return i;
    }

    private void deleteContentItemTempFile(ContentItem contentItem) {
        if (contentItem == null) {
            Log.m22e(TAG, "deleteContentItemTempFile. contentItem is null!!");
            return;
        }
        Log.m19d(TAG, "deleteContentItemTempFile : " + contentItem.getPath());
        if (!PermissionProvider.checkSavingEnable(this.mAppContext)) {
            Log.m26i(TAG, "deleteContentItemTempFile. Permission error");
            return;
        }
        try {
            if (!StorageProvider.isTempFile(contentItem.getPath())) {
                return;
            }
            if (!new File(contentItem.getPath()).delete()) {
                Log.m22e(TAG, "Delete fail");
                if (Log.ENG) {
                    Log.m22e(TAG, "Delete fail - path : " + contentItem.getPath());
                    return;
                }
                return;
            }
            Log.m19d(TAG, "Delete - path : " + contentItem.getPath());
        } catch (NullPointerException unused) {
            Log.m22e(TAG, "deleteContentItemTempFile() Path is null");
        }
    }

    public synchronized void clearContentItem() {
        Log.m26i(TAG, "clearContentItem - size : " + this.mContentItemStack.size());
        if (!PermissionProvider.checkSavingEnable(this.mAppContext)) {
            Log.m26i(TAG, "cancel clearContentItem. Permission error");
            return;
        }
        Iterator it = this.mContentItemStack.iterator();
        while (it.hasNext()) {
            ContentItem contentItem = (ContentItem) it.next();
            try {
                if (StorageProvider.isTempFile(contentItem.getPath())) {
                    if (!new File(contentItem.getPath()).delete()) {
                        Log.m22e(TAG, "Delete fail");
                        if (Log.ENG) {
                            Log.m22e(TAG, "Delete fail - path : " + contentItem.getPath());
                        }
                    } else {
                        Log.m19d(TAG, "Delete - path : " + contentItem.getPath());
                    }
                }
            } catch (NullPointerException unused) {
                Log.m22e(TAG, "clearContentItem() mPath is null");
            }
        }
        this.mContentItemStack.clear();
    }

    public void onDestroy() {
        Log.m26i(TAG, "onDestroy ");
        Recorder.getInstance().unregisterListener(this);
        Player.getInstance().unregisterListener(this);
        Editor.getInstance().unregisterListener(this);
        unregisterAllListener();
    }

    public void onEditorUpdate(int i, int i2) {
        Log.m26i(TAG, "onEditorUpdate - status : " + i + " arg : " + i2);
        if (i == 3010) {
            if (i2 == 0) {
                Log.m26i(TAG, "Editor.OVERWRITE_START");
            } else if (i2 == 1) {
                Log.m26i(TAG, "Editor.OVERWRITE_COMPLETE");
                ContentItem popContentItem = popContentItem();
                deleteContentItemTempFile(popContentItem());
                deleteContentItemTempFile(popContentItem());
                pushContentItem(popContentItem);
                setEngineState(0);
            } else if (i2 != 2) {
                if (i2 != 3) {
                    if (i2 == 4) {
                        trimCompleteFile();
                    } else if (i2 != 5) {
                        switch (i2) {
                            case 10:
                                Log.m26i(TAG, "Editor.TRIM_AFTER_OVERWRITE");
                                ContentItem popContentItem2 = popContentItem();
                                if (popContentItem2 == null) {
                                    Log.m22e(TAG, "reserveItem is null");
                                    break;
                                } else {
                                    deleteContentItemTempFile(popContentItem());
                                    deleteContentItemTempFile(popContentItem());
                                    pushContentItem(popContentItem2);
                                    String path = popContentItem2.getPath();
                                    String createTempFile = StorageProvider.createTempFile(path, path.substring(path.lastIndexOf(46)));
                                    int[] iArr = this.mTrimTime;
                                    pushContentItem(new ContentItem(createTempFile, 0, iArr[1] - iArr[0]));
                                    Editor instance = Editor.getInstance();
                                    int[] iArr2 = this.mTrimTime;
                                    instance.trim(path, createTempFile, iArr2[0], iArr2[1]);
                                    break;
                                }
                            case 11:
                            case 13:
                                Log.m26i(TAG, "Editor.SAVE_AFTER_OVERWRITE or SAVE_AFTER_TRIM");
                                long saveFile = saveFile(false);
                                setEngineState(0);
                                notifyObservers(103, (int) saveFile, -1);
                                if (saveFile == -1) {
                                    Toast.makeText(this.mAppContext, C0690R.string.recording_failed, 1).show();
                                }
                                resetTrimTime();
                                break;
                            case 12:
                            case 14:
                                Log.m26i(TAG, "Editor.SAVE_AS_NEW_AFTER_OVERWRITE or SAVE_AS_NEW_AFTER_TRIM");
                                long saveFile2 = saveFile(true);
                                setEngineState(0);
                                notifyObservers(103, (int) saveFile2, -1);
                                if (saveFile2 == -1) {
                                    Toast.makeText(this.mAppContext, C0690R.string.recording_failed, 1).show();
                                    break;
                                }
                                break;
                            case 15:
                                Log.m26i(TAG, "Editor.PLAY_AFTER_OVERWRITE");
                                ContentItem popContentItem3 = popContentItem();
                                deleteContentItemTempFile(popContentItem());
                                deleteContentItemTempFile(popContentItem());
                                pushContentItem(popContentItem3);
                                setEngineState(0);
                                resumePlay();
                                break;
                            case 16:
                                Log.m26i(TAG, "Editor.DELETE_AFTER_OVERWRITE");
                                ContentItem popContentItem4 = popContentItem();
                                if (popContentItem4 == null) {
                                    Log.m22e(TAG, "reserveItem is null");
                                    break;
                                } else {
                                    deleteContentItemTempFile(popContentItem());
                                    deleteContentItemTempFile(popContentItem());
                                    pushContentItem(popContentItem4);
                                    String path2 = popContentItem4.getPath();
                                    String createTempFile2 = StorageProvider.createTempFile(path2, path2.substring(path2.lastIndexOf(46)));
                                    int[] iArr3 = this.mTrimTime;
                                    pushContentItem(new ContentItem(createTempFile2, 0, iArr3[1] - iArr3[0]));
                                    Editor instance2 = Editor.getInstance();
                                    int[] iArr4 = this.mTrimTime;
                                    instance2.delete(path2, createTempFile2, iArr4[0], iArr4[1]);
                                    break;
                                }
                            case 17:
                                break;
                            case 18:
                                Editor.getInstance().setTranslationFile(false);
                                trimCompleteFile();
                                break;
                            case 19:
                                Editor.getInstance().setTranslationFile(false);
                                trimErrorFile();
                                break;
                        }
                    } else {
                        trimErrorFile();
                    }
                }
                Log.m26i(TAG, "Editor.TRIM_START");
            } else {
                Log.m26i(TAG, "Editor.OVERWRITE_ERROR");
                popContentItem();
                Recorder.getInstance().cancelRecord();
                notifyObservers(103, -1, -1);
                Toast.makeText(this.mAppContext, C0690R.string.recording_failed, 1).show();
                setEngineState(0);
            }
        }
        notifyObservers(i, i2, -1);
    }

    public void onPlayerUpdate(int i, int i2, int i3) {
        notifyObservers(i, i2, i3);
        if (i == 2012) {
            this.mCurrentTime = i2;
        }
    }

    public void onRecorderUpdate(int i, int i2, int i3) {
        notifyObservers(i, i2, i3);
        if (i != 1010) {
            if (i != 1011) {
                switch (i) {
                    case 1020:
                        Log.m22e(TAG, "onInfo - INFO_AUDIOFOCUS_LOSS : extra = " + i2);
                        if (i2 == 1006) {
                            Log.m19d(TAG, "INFO_AUDIOFOCUS_LOSS : cancel record");
                            cancelRecord();
                            if (VoiceNoteService.Helper.connectionCount() != 0 && this.mScene == 8) {
                                VoiceNoteObservable.getInstance().notifyObservers(3);
                                return;
                            }
                            return;
                        }
                        String str = this.mOriginalFilePath;
                        if (str != null) {
                            String name = new File(str).getName();
                            setUserSettingName(name.substring(0, name.lastIndexOf(46)));
                        }
                        long stopRecord = stopRecord(true, true);
                        if (stopRecord != -2) {
                            notifyObservers(103, (int) stopRecord, -1);
                        }
                        if (VoiceNoteService.Helper.connectionCount() != 0 && this.mScene == 8) {
                            VoiceNoteObservable.getInstance().notifyObservers(3);
                            return;
                        }
                        return;
                    case 1021:
                        Log.m22e(TAG, "onInfo - INFO_MAX_DURATION_REACHED : extra = " + i2);
                        if (this.mScene == 6) {
                            pauseRecord();
                            return;
                        }
                        long stopRecord2 = stopRecord(true, true);
                        if (stopRecord2 != -2) {
                            notifyObservers(103, (int) stopRecord2, -1);
                            Settings.setSettings(Settings.KEY_LIST_MODE, 0);
                            return;
                        }
                        return;
                    case 1022:
                        Log.m22e(TAG, "onInfo - INFO_MAX_FILESIZE_REACHED : extra = " + i2);
                        if (this.mScene == 6) {
                            pauseRecord();
                            return;
                        }
                        long stopRecord3 = stopRecord(true, true);
                        if (stopRecord3 != -2) {
                            notifyObservers(103, (int) stopRecord3, -1);
                            return;
                        }
                        return;
                    default:
                        return;
                }
            } else {
                this.mCurrentTime = i2;
            }
        } else if (i2 == 1) {
            clearContentItem();
        } else if (i2 == 2) {
            MetadataRepository.getInstance().rename((String) null, getRecentFilePath());
        }
    }

    public int getRecorderState() {
        return Recorder.getInstance().getRecorderState();
    }

    public int getRecordMode() {
        return Recorder.getInstance().getRecordMode();
    }

    public boolean isAutoResumeRecording() {
        return Recorder.getInstance().isAutoResumeRecording();
    }

    public void setAutoResumeRecording(boolean z) {
        Recorder.getInstance().setAutoResumeRecording(z);
    }

    public int getPlayerState() {
        return Player.getInstance().getPlayerState();
    }

    public void setVolume(float f, float f2) {
        Player.getInstance().setVolume(f, f2);
    }

    public int getEditorState() {
        return Editor.getInstance().getEditorState();
    }

    public void setOriginalFilePath(String str) {
        this.mOriginalFilePath = str;
        Log.m19d(TAG, "setOriginalFilePath - path : " + this.mOriginalFilePath);
    }

    public String getOriginalFilePath() {
        Log.m19d(TAG, "getOriginalFilePath - path : " + this.mOriginalFilePath);
        return this.mOriginalFilePath;
    }

    public void setUserSettingName(String str) {
        this.mUserSettingName = str;
        Log.m19d(TAG, "setUserSettingName - name : " + this.mUserSettingName);
    }

    public void setCategoryID(long j) {
        this.mCategoryID = j;
        Log.m26i(TAG, "setCategoryID - id : " + j);
    }

    public String getUserSettingName() {
        Log.m19d(TAG, "getUserSettingName - name : " + this.mUserSettingName);
        return this.mUserSettingName;
    }

    private void setAudioFormat(AudioFormat audioFormat) {
        this.mAudioFormat = audioFormat;
        Recorder.getInstance().setAudioFormat(audioFormat);
    }

    public AudioFormat getAudioFormat() {
        return this.mAudioFormat;
    }

    public void setCurrentTime(int i) {
        Log.m26i(TAG, "setCurrentTime - time : " + i);
        this.mCurrentTime = i;
        Recorder.getInstance().setCurrentTime(i);
        if (Player.getInstance().getPlayerState() != 1) {
            Player.getInstance().seekTo(i);
        }
    }

    public int getCurrentTime() {
        return this.mCurrentTime;
    }

    public String getLastSavedFilePath() {
        return this.mLastSavedFilePath;
    }

    public String getLastSavedFileName() {
        return getTitle(this.mLastSavedFilePath);
    }

    public String getCurrentFileName() {
        return getTitle(this.mOriginalFilePath);
    }

    public void setSimpleModeItem(long j) {
        this.mSimpleModeItemId = j;
    }

    public long getSimpleModeItem() {
        return this.mSimpleModeItemId;
    }

    public void setScene(int i) {
        this.mScene = i;
    }

    public int getScene() {
        return this.mScene;
    }

    /* access modifiers changed from: package-private */
    public void setScreenOff(boolean z) {
        this.mScreenOff = z;
    }

    public boolean getScreenOff() {
        return this.mScreenOff;
    }

    public synchronized int startRecord(AudioFormat audioFormat) {
        Log.m26i(TAG, "startRecord");
        if (getEngineState() != 0) {
            return ReturnCodes.BUSY;
        }
        if (Recorder.getInstance().getRecorderState() != 1) {
            Log.m32w(TAG, "startRecord - it is already recording state");
            return CAN_NOT_START_RECORD_WHILE_RECORDING;
        } else if (!PhoneStateProvider.getInstance().isCallIdle(this.mAppContext)) {
            return -102;
        } else {
            MetadataRepository.getInstance().initialize();
            MetadataRepository.getInstance().setRecChCount(-1);
            int intSettings = Settings.getIntSettings("record_mode", 1);
            setAutoResumeRecording(false);
            Log.m26i(TAG, "startRecord: recordMode = " + intSettings);
            if (isWiredHeadSetConnected() || isBluetoothSCOConnected()) {
                if (intSettings == 2) {
                    return -104;
                }
                if (intSettings == 4) {
                    return -105;
                }
                showNotificationAlert();
                this.mIsNeedExternalMicAlert = false;
            }
            if (intSettings == 4 && !Network.isNetworkConnected(this.mAppContext)) {
                return -106;
            }
            if (intSettings == 2) {
                setCategoryID(1);
            } else if (intSettings == 1) {
                setCategoryID(0);
            } else if (intSettings == 4) {
                setCategoryID(2);
            }
            if (audioFormat == null) {
                audioFormat = new AudioFormat(intSettings);
            }
            this.mAudioFormat = audioFormat;
            this.mLastSavedFilePath = null;
            String createTempFile = StorageProvider.createTempFile(this.mAudioFormat.getExtension());
            pushContentItem(new ContentItem(createTempFile, this.mCurrentTime, 0));
            getInstance().resetOverwriteTime();
            int startRecord = Recorder.getInstance().startRecord(createTempFile, audioFormat);
            if (startRecord != 0) {
                Settings.setSettings("record_mode", intSettings);
                popContentItem();
            }
            Recorder.getInstance().setOverwriteByDrag(false);
            return startRecord;
        }
    }

    public long stopRecord(boolean z, boolean z2) {
        Log.m26i(TAG, "stopRecord - newName : " + z + " showToast : " + z2);
        if (getEngineState() != 0) {
            return ReturnCodes.BUSY;
        }
        if (Recorder.getInstance().getRecorderState() == 1 || isSaveEnable()) {
            setEngineState(1);
            CallRejectChecker.getInstance().setReject(false);
            Recorder.getInstance().initPhoneStateListener();
            this.mShowToast = z2;
            if (Recorder.getInstance().getRecorderState() == 2 || Recorder.getInstance().getRecorderState() == 3) {
                ContentItem peekContentItem = peekContentItem();
                if (peekContentItem == null) {
                    if (this.mAudioFormat == null) {
                        this.mAudioFormat = new AudioFormat(Recorder.getInstance().getRecordMode());
                    }
                    peekContentItem = new ContentItem(StorageProvider.createTempFile(this.mAudioFormat.getExtension()));
                    pushContentItem(peekContentItem);
                }
                if (!Recorder.getInstance().saveFile(peekContentItem)) {
                    popContentItem();
                }
            }
            Recorder.getInstance().stopSTT();
            if (this.mContentItemStack.size() > 1) {
                Log.m29v(TAG, "stopRecord - save after overwrite ");
                setEngineState(0);
                if (z) {
                    startOverwrite(12);
                    return -2;
                }
                startOverwrite(11);
                return -2;
            } else if (this.mContentItemStack.isEmpty()) {
                setEngineState(0);
                return -114;
            } else {
                resetTrimTime();
                long saveFile = saveFile(z);
                if (saveFile < 0) {
                    Toast.makeText(this.mAppContext, C0690R.string.recording_failed, 1).show();
                    notifyObservers(103, (int) saveFile, -1);
                }
                setEngineState(0);
                setAutoResumeRecording(false);
                return saveFile;
            }
        } else {
            Log.m26i(TAG, "Can not stopRecord");
            return ReturnCodes.BUSY;
        }
    }

    /* JADX INFO: finally extract failed */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x008f  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00ad  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00b6  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00b9  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00e8  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00ed  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0133  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x013c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private long saveTempFile(java.lang.String r22) {
        /*
            r21 = this;
            r1 = r21
            r2 = r22
            r4 = 0
            java.lang.Integer r5 = java.lang.Integer.valueOf(r4)
            r6 = r4
            r0 = 1
        L_0x000b:
            r7 = 5
            java.lang.String r8 = "Engine"
            if (r0 > r7) goto L_0x0037
            com.sec.android.app.voicenote.service.Player r6 = com.sec.android.app.voicenote.service.Player.getInstance()
            boolean r6 = r6.isValidMediaFile(r2)
            if (r6 == 0) goto L_0x002f
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r9 = "check isValidMediaFile count "
            r7.append(r9)
            r7.append(r0)
            java.lang.String r0 = r7.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r8, r0)
            goto L_0x0037
        L_0x002f:
            r7 = 50
            android.os.SystemClock.sleep(r7)
            int r0 = r0 + 1
            goto L_0x000b
        L_0x0037:
            r9 = 0
            if (r6 != 0) goto L_0x0059
            java.lang.String r0 = "saveTempFile skip : invalid Media File"
            com.sec.android.app.voicenote.provider.Log.m22e(r8, r0)
            boolean r0 = com.sec.android.app.voicenote.provider.Log.ENG
            if (r0 == 0) goto L_0x0058
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "saveTempFile skip : invalid Media File :"
            r0.append(r3)
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m22e(r8, r0)
        L_0x0058:
            return r9
        L_0x0059:
            java.io.File r6 = new java.io.File
            r6.<init>(r2)
            java.lang.String r0 = "saveTempFile"
            com.sec.android.app.voicenote.provider.Log.m26i(r8, r0)
            android.media.MediaMetadataRetriever r7 = new android.media.MediaMetadataRetriever
            r7.<init>()
            r7.setDataSource(r2)     // Catch:{ Exception -> 0x0080 }
            r0 = 9
            java.lang.String r12 = r7.extractMetadata(r0)     // Catch:{ Exception -> 0x0080 }
            r0 = 1022(0x3fe, float:1.432E-42)
            java.lang.String r0 = r7.extractMetadata(r0)     // Catch:{ Exception -> 0x007b }
            r7.release()
            goto L_0x008d
        L_0x007b:
            r0 = move-exception
            goto L_0x0082
        L_0x007d:
            r0 = move-exception
            goto L_0x02dd
        L_0x0080:
            r0 = move-exception
            r12 = 0
        L_0x0082:
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x007d }
            com.sec.android.app.voicenote.provider.Log.m22e(r8, r0)     // Catch:{ all -> 0x007d }
            r7.release()
            r0 = 0
        L_0x008d:
            if (r12 == 0) goto L_0x0093
            long r9 = java.lang.Long.parseLong(r12)
        L_0x0093:
            com.sec.android.app.voicenote.service.codec.M4aReader r7 = new com.sec.android.app.voicenote.service.codec.M4aReader
            r7.<init>(r2)
            com.sec.android.app.voicenote.service.codec.M4aInfo r7 = r7.readFile()
            java.lang.String r12 = ".amr"
            boolean r13 = r2.contains(r12)
            if (r13 != 0) goto L_0x00b6
            java.lang.String r13 = ".3ga"
            boolean r13 = r2.contains(r13)
            if (r13 == 0) goto L_0x00ad
            goto L_0x00b6
        L_0x00ad:
            if (r0 == 0) goto L_0x00b4
            int r0 = java.lang.Integer.parseInt(r0)
            goto L_0x00b7
        L_0x00b4:
            r0 = r4
            goto L_0x00b7
        L_0x00b6:
            r0 = 1
        L_0x00b7:
            if (r0 != 0) goto L_0x00ce
            if (r7 == 0) goto L_0x00cd
            java.util.HashMap<java.lang.String, java.lang.Boolean> r0 = r7.hasCustomAtom
            java.lang.String r7 = "sttd"
            java.lang.Object r0 = r0.get(r7)
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            if (r0 == 0) goto L_0x00cd
            r0 = 4
            goto L_0x00ce
        L_0x00cd:
            r0 = 1
        L_0x00ce:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r13 = "saveTempFile recordMode = "
            r7.append(r13)
            r7.append(r0)
            java.lang.String r7 = r7.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r8, r7)
            boolean r7 = com.sec.android.app.voicenote.service.codec.M4aInfo.isM4A(r22)
            if (r7 == 0) goto L_0x00ed
            java.lang.String r12 = ".m4a"
            java.lang.String r7 = "audio/mp4"
            goto L_0x00ef
        L_0x00ed:
            java.lang.String r7 = "audio/amr"
        L_0x00ef:
            com.sec.android.app.voicenote.provider.DBProvider r13 = com.sec.android.app.voicenote.provider.DBProvider.getInstance()
            java.lang.String r13 = r13.createNewFileName(r0)
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            java.lang.String r15 = com.sec.android.app.voicenote.provider.StorageProvider.getVoiceRecorderPath()
            r14.append(r15)
            r15 = 47
            r14.append(r15)
            r14.append(r13)
            r14.append(r12)
            java.lang.String r12 = r14.toString()
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "saveTempFile : - newFilePath : "
            r13.append(r14)
            r13.append(r12)
            java.lang.String r13 = r13.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r8, r13)
            java.io.File r13 = new java.io.File
            r13.<init>(r12)
            boolean r6 = com.sec.android.app.voicenote.provider.StorageProvider.isExistFile((java.io.File) r6)
            r15 = -1
            if (r6 != 0) goto L_0x013c
            java.lang.String r0 = "saveTempFile : cancel restore file while save by Hidden file doesn't exist"
            com.sec.android.app.voicenote.provider.Log.m22e(r8, r0)
            r21.clearContentItem()
            return r15
        L_0x013c:
            boolean r6 = com.sec.android.app.voicenote.provider.StorageProvider.isExistFile((java.io.File) r13)
            java.lang.String r11 = "saveTempFile : Fail to delete saveFile !!"
            if (r6 == 0) goto L_0x018b
            java.lang.String r6 = "saveTempFile : rename saving file while save by saving file already exist"
            com.sec.android.app.voicenote.provider.Log.m22e(r8, r6)
            com.sec.android.app.voicenote.provider.DBProvider r6 = com.sec.android.app.voicenote.provider.DBProvider.getInstance()
            java.lang.String r12 = r6.createNewFilePath(r12)
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r13 = "saveTempFile : - new newFilePath : "
            r6.append(r13)
            r6.append(r12)
            java.lang.String r6 = r6.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r8, r6)
            java.io.File r13 = new java.io.File
            r13.<init>(r12)
            boolean r6 = com.sec.android.app.voicenote.provider.StorageProvider.isExistFile((java.io.File) r13)
            if (r6 == 0) goto L_0x018b
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r14)
            r6.append(r12)
            java.lang.String r6 = r6.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r8, r6)
            boolean r6 = r13.delete()
            if (r6 != 0) goto L_0x018b
            com.sec.android.app.voicenote.provider.Log.m22e(r8, r11)
        L_0x018b:
            android.content.Context r6 = r1.mAppContext
            boolean r2 = com.sec.android.app.voicenote.provider.PrivateModeProvider.rename(r6, r2, r12)
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r14 = "saveTempFile : move result : "
            r6.append(r14)
            r6.append(r2)
            java.lang.String r6 = r6.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r8, r6)
            if (r2 != 0) goto L_0x01b0
            java.lang.String r0 = "saveTempFile : cancel restore by can not rename file"
            com.sec.android.app.voicenote.provider.Log.m22e(r8, r0)
            r21.clearContentItem()
            return r15
        L_0x01b0:
            com.sec.android.app.voicenote.service.MetadataRepository r2 = com.sec.android.app.voicenote.service.MetadataRepository.getInstance()
            r2.updateAmpTask(r12)
            java.lang.String r2 = r13.getName()
            java.lang.String r6 = r13.getName()
            r12 = 46
            int r6 = r6.lastIndexOf(r12)
            java.lang.String r2 = r2.substring(r4, r6)
            java.lang.String r6 = r13.getPath()
            r1.mLastSavedFilePath = r6
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r12 = "saveTempFile : name : "
            r6.append(r12)
            r6.append(r2)
            java.lang.String r6 = r6.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r8, r6)
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r12 = "saveTempFile : addTime : "
            r6.append(r12)
            long r17 = r13.lastModified()
            r19 = 1000(0x3e8, double:4.94E-321)
            long r3 = r17 / r19
            r6.append(r3)
            java.lang.String r3 = r6.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r8, r3)
            java.lang.String r3 = r13.getPath()
            java.lang.String r3 = com.sec.android.app.voicenote.provider.StorageProvider.convertToSDCardReadOnlyPath(r3)
            android.content.ContentValues r4 = new android.content.ContentValues
            r4.<init>()
            java.lang.String r6 = "title"
            r4.put(r6, r2)
            java.lang.String r6 = "mime_type"
            r4.put(r6, r7)
            java.lang.String r6 = "_data"
            r4.put(r6, r3)
            java.lang.Long r6 = java.lang.Long.valueOf(r9)
            java.lang.String r7 = "duration"
            r4.put(r7, r6)
            long r6 = r13.length()
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            java.lang.String r7 = "_size"
            r4.put(r7, r6)
            long r6 = r13.lastModified()
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            java.lang.String r7 = "datetaken"
            r4.put(r7, r6)
            long r6 = r13.lastModified()
            long r6 = r6 / r19
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            java.lang.String r7 = "date_modified"
            r4.put(r7, r6)
            java.lang.String r6 = "track"
            r4.put(r6, r5)
            java.lang.String r6 = "is_ringtone"
            r4.put(r6, r5)
            java.lang.String r6 = "is_alarm"
            r4.put(r6, r5)
            java.lang.String r6 = "is_notification"
            r4.put(r6, r5)
            java.lang.String r6 = "album"
            java.lang.String r7 = "Sounds"
            r4.put(r6, r7)
            java.lang.String r6 = "is_drm"
            r4.put(r6, r5)
            r6 = 1
            java.lang.Integer r7 = java.lang.Integer.valueOf(r6)
            java.lang.String r6 = "recordingtype"
            r4.put(r6, r7)
            java.lang.String r6 = "is_memo"
            r4.put(r6, r5)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            java.lang.String r6 = "recording_mode"
            r4.put(r6, r0)
            java.lang.String r0 = "label_id"
            r4.put(r0, r5)
            com.sec.android.app.voicenote.provider.DBProvider r0 = com.sec.android.app.voicenote.provider.DBProvider.getInstance()
            android.net.Uri r0 = r0.insertDB(r3, r4)
            if (r0 == 0) goto L_0x02ce
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "saveTempFile : insertDB success : "
            r0.append(r4)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m29v(r8, r0)
            com.sec.android.app.voicenote.provider.DBProvider r0 = com.sec.android.app.voicenote.provider.DBProvider.getInstance()
            long r3 = r0.getIdByPath(r3)
            r5 = 0
            r1.setOriginalFilePath(r5)
            r5 = 0
            r1.setCurrentTime(r5)
            android.content.Context r0 = r1.mAppContext
            r6 = 2131755447(0x7f1001b7, float:1.9141774E38)
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]
            r8[r5] = r2
            java.lang.String r2 = r0.getString(r6, r8)
            android.widget.Toast r0 = android.widget.Toast.makeText(r0, r2, r7)
            r0.show()
            return r3
        L_0x02ce:
            java.lang.String r0 = "saveTempFile : cancel restore while save by Content Resolver insert failed"
            com.sec.android.app.voicenote.provider.Log.m22e(r8, r0)
            boolean r0 = r13.delete()
            if (r0 != 0) goto L_0x02dc
            com.sec.android.app.voicenote.provider.Log.m22e(r8, r11)
        L_0x02dc:
            return r15
        L_0x02dd:
            r7.release()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.Engine.saveTempFile(java.lang.String):long");
    }

    /* JADX INFO: finally extract failed */
    private long saveFile(boolean z) {
        String str;
        long j;
        int i;
        File file;
        long j2;
        String str2;
        String str3;
        String str4;
        boolean z2 = z;
        boolean z3 = this.mShowToast;
        if (getContentItemCount() < 1) {
            Log.m22e(TAG, "saveFile fail ");
            return -1;
        }
        String recentFilePath = getRecentFilePath();
        File file2 = new File(recentFilePath);
        if (!file2.exists() || file2.isDirectory()) {
            Log.m22e(TAG, "Abnormal hiddenPath");
            if (!Log.ENG) {
                return -1;
            }
            Log.m22e(TAG, "Abnormal hiddenPath - path : " + recentFilePath);
            return -1;
        }
        Log.m19d(TAG, "saveFile - saveNewFile : " + z2);
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mediaMetadataRetriever.setDataSource(recentFilePath);
            str = mediaMetadataRetriever.extractMetadata(9);
            mediaMetadataRetriever.release();
        } catch (Exception e) {
            Log.m22e(TAG, e.toString());
            mediaMetadataRetriever.release();
            str = null;
        } catch (Throwable th) {
            mediaMetadataRetriever.release();
            throw th;
        }
        long parseLong = str != null ? Long.parseLong(str) : 0;
        int recordMode = MetadataRepository.getInstance().getRecordMode();
        if (recordMode == 0) {
            if (this.mOriginalFilePath == null) {
                recordMode = Settings.getIntSettings("record_mode", 1);
            } else {
                recordMode = Settings.getIntSettings(Settings.KEY_PLAY_MODE, 1);
            }
        }
        if (recordMode == 5 || recordMode == 6) {
            recordMode = 1;
        }
        if (M4aInfo.isM4A(recentFilePath)) {
            MetadataRepository instance = MetadataRepository.getInstance();
            instance.write(recentFilePath);
            instance.close();
        }
        String extension = this.mAudioFormat.getExtension();
        String str5 = this.mOriginalFilePath;
        if (str5 != null && !str5.endsWith(extension)) {
            int length = this.mOriginalFilePath.length();
            extension = this.mOriginalFilePath.substring(length - 4, length);
        }
        Log.m26i(TAG, "saveFile - mRecordMode : " + recordMode);
        Log.m26i(TAG, "saveFile - getExtension : " + extension);
        Log.m19d(TAG, "saveFile - mOriginalFilePath : " + this.mOriginalFilePath);
        Log.m19d(TAG, "saveFile - mUserSettingName : " + this.mUserSettingName);
        Log.m19d(TAG, "saveFile - getMimeType : " + this.mAudioFormat.getMimeType());
        long j3 = this.mCategoryID;
        Log.m19d(TAG, "categoryID = " + j3);
        setCategoryID(0);
        boolean z4 = z3;
        long j4 = j3;
        if (z2 || (str4 = this.mOriginalFilePath) == null) {
            Log.m26i(TAG, "saveFile save to new file !!!");
            String str6 = this.mUserSettingName;
            if (str6 == null || str6.isEmpty()) {
                str2 = DBProvider.getInstance().createNewFileName(recordMode);
            } else {
                str2 = this.mUserSettingName;
                setUserSettingName((String) null);
            }
            i = recordMode;
            if (this.mOriginalFilePath != null) {
                StringBuilder sb = new StringBuilder();
                String str7 = this.mOriginalFilePath;
                j = parseLong;
                sb.append(str7.substring(0, str7.lastIndexOf(47)));
                sb.append('/');
                sb.append(str2);
                sb.append(extension);
                str3 = sb.toString();
            } else {
                j = parseLong;
                if (!PrivateModeProvider.isPrivateBoxMode() || !PrivateModeProvider.isPrivateMode()) {
                    str3 = StorageProvider.getVoiceRecorderPath() + '/' + str2 + extension;
                } else {
                    str3 = PrivateModeProvider.getPrivateStorageRoot(this.mAppContext) + '/' + str2 + extension;
                }
            }
            Log.m19d(TAG, "saveFile - newFileTitle : " + getTitle(str3));
            File file3 = new File(str3);
            if (!StorageProvider.isExistFile(file2)) {
                Log.m22e(TAG, "cancel recording while save by Hidden file doesn't exist");
                cancelRecord();
                clearContentItem();
                return -1;
            }
            if (StorageProvider.isExistFile(file3)) {
                Log.m22e(TAG, "rename saving file while save by saving file already exist");
                str3 = DBProvider.getInstance().createNewFilePath(str3);
                Log.m19d(TAG, "saveFile - new newFileTitle : " + getTitle(str3));
                file3 = new File(str3);
                if (StorageProvider.isExistFile(file3)) {
                    Log.m19d(TAG, "saveFile - newFilePath : " + str3);
                    if (!file3.delete()) {
                        Log.m22e(TAG, "Fail to delete saveFile !!");
                    }
                }
            }
            file = file3;
            boolean rename = PrivateModeProvider.rename(this.mAppContext, recentFilePath, str3);
            Log.m19d(TAG, "move result : " + rename);
            if (!rename) {
                Log.m22e(TAG, "cancel recording while save by can not rename file");
                cancelRecord();
                clearContentItem();
                return -1;
            }
        } else {
            if (!recentFilePath.equals(str4)) {
                Log.m26i(TAG, "saveFile save to new file2 !!!");
                file = new File(this.mOriginalFilePath);
                setUserSettingName((String) null);
                if (!StorageProvider.isExistFile(file2)) {
                    Log.m22e(TAG, "cancel recording while save by Hidden file doesn't exist");
                    cancelRecord();
                    clearContentItem();
                    return -1;
                }
                if (StorageProvider.isExistFile(file)) {
                    Log.m22e(TAG, "rename saving file while save by saving file already exist");
                    if (!file.delete()) {
                        Log.m22e(TAG, "Fail to delete saveFile !!");
                        clearContentItem();
                        return -1;
                    }
                }
                boolean rename2 = PrivateModeProvider.rename(this.mAppContext, recentFilePath, this.mOriginalFilePath);
                Log.m19d(TAG, "move result : " + rename2);
                if (!rename2) {
                    Log.m22e(TAG, "cancel recording while save by can not rename file");
                    cancelRecord();
                    clearContentItem();
                    return -1;
                }
            } else {
                Log.m26i(TAG, "saveFile save to original file !!!");
                file = new File(recentFilePath);
                setUserSettingName((String) null);
            }
            i = recordMode;
            j = parseLong;
        }
        String substring = file.getName().substring(0, file.getName().lastIndexOf(46));
        this.mLastSavedFilePath = file.getPath();
        Log.m19d(TAG, "saveFile name : " + substring);
        String convertToSDCardReadOnlyPath = StorageProvider.convertToSDCardReadOnlyPath(file.getPath());
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", substring);
        contentValues.put("mime_type", this.mAudioFormat.getMimeType());
        contentValues.put("_data", convertToSDCardReadOnlyPath);
        contentValues.put("duration", Long.valueOf(j));
        contentValues.put("_size", Long.valueOf(file.length()));
        if (z || this.mOriginalFilePath == null) {
            contentValues.put("datetaken", Long.valueOf(file.lastModified()));
        }
        contentValues.put("date_modified", Long.valueOf(file.lastModified() / 1000));
        contentValues.put("track", 0);
        contentValues.put("is_ringtone", 0);
        contentValues.put("is_alarm", 0);
        contentValues.put("is_notification", 0);
        contentValues.put("album", "Sounds");
        contentValues.put("is_drm", 0);
        contentValues.put("recordingtype", 1);
        contentValues.put("is_memo", 0);
        contentValues.put("recording_mode", Integer.valueOf(i));
        contentValues.put(DialogFactory.BUNDLE_LABEL_ID, Long.valueOf(j4));
        if (AudioFormat.ExtType.EXT_3GA.equals(extension)) {
            contentValues.put("is_music", 0);
        }
        if (z || this.mOriginalFilePath == null) {
            Uri insertDB = DBProvider.getInstance().insertDB(convertToSDCardReadOnlyPath, contentValues);
            if (insertDB != null) {
                Log.m29v(TAG, "insertDB success : " + convertToSDCardReadOnlyPath);
                j2 = DBProvider.getInstance().getIdByPath(convertToSDCardReadOnlyPath);
                SurveyLogProvider.insertRecordingLog(insertDB.toString(), (int) j);
            } else {
                Log.m22e(TAG, "cancel recording while save by Content Resolver insert failed");
                cancelRecord();
                if (file.delete()) {
                    return -1;
                }
                Log.m22e(TAG, "Fail to delete saveFile !!");
                return -1;
            }
        } else if (DBProvider.getInstance().updateDB(convertToSDCardReadOnlyPath, contentValues)) {
            Log.m29v(TAG, "updateDB success : " + convertToSDCardReadOnlyPath);
            j2 = DBProvider.getInstance().getIdByPath(convertToSDCardReadOnlyPath);
        } else {
            Log.m22e(TAG, "cancel recording while save by Content Resolver update failed");
            cancelRecord();
            return -1;
        }
        setOriginalFilePath((String) null);
        setCurrentTime(0);
        clearContentItem();
        SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_SAVE, -1);
        Recorder.getInstance().cancelRecord();
        Player.getInstance().stopPlay(false);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("saveFile : showToast : ");
        boolean z5 = z4;
        sb2.append(z5);
        Log.m26i(TAG, sb2.toString());
        if (z5) {
            Context context = this.mAppContext;
            Toast.makeText(context, context.getString(C0690R.string.filename_has_been_saved, new Object[]{substring}), 0).show();
        }
        if (VoiceNoteFeature.isGateEnabled()) {
            android.util.Log.i("GATE", "<GATE-M> AUDIO_RECORDED </GATE-M>");
        }
        return j2;
    }

    public synchronized boolean cancelRecord() {
        Log.m26i(TAG, "cancelRecord");
        setUserSettingName((String) null);
        setOriginalFilePath((String) null);
        CallRejectChecker.getInstance().setReject(false);
        Recorder.getInstance().initPhoneStateListener();
        clearContentItem();
        resetTrimTime();
        resetOverwriteTime();
        setCategoryID(0);
        return Recorder.getInstance().cancelRecord();
    }

    public synchronized boolean pauseRecord() {
        Log.m26i(TAG, "pauseRecord");
        if (!isSaveEnable()) {
            Log.m26i(TAG, "pauseRecord can not pause");
            return false;
        }
        CallRejectChecker.getInstance().setReject(false);
        return Recorder.getInstance().pauseRecord();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:62:0x0100, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int resumeRecord() {
        /*
            r8 = this;
            monitor-enter(r8)
            java.lang.String r0 = "Engine"
            java.lang.String r1 = "resumeRecord"
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r1)     // Catch:{ all -> 0x0101 }
            int r0 = r8.getEngineState()     // Catch:{ all -> 0x0101 }
            if (r0 == 0) goto L_0x0012
            r0 = ReturnCodes.BUSY(0xffffffffffffff89, float:NaN)
            monitor-exit(r8)
            return r0
        L_0x0012:
            com.sec.android.app.voicenote.provider.PhoneStateProvider r0 = com.sec.android.app.voicenote.provider.PhoneStateProvider.getInstance()     // Catch:{ all -> 0x0101 }
            android.content.Context r1 = r8.mAppContext     // Catch:{ all -> 0x0101 }
            boolean r0 = r0.isCallIdle(r1)     // Catch:{ all -> 0x0101 }
            if (r0 == 0) goto L_0x00fd
            com.sec.android.app.voicenote.service.MetadataRepository r0 = com.sec.android.app.voicenote.service.MetadataRepository.getInstance()     // Catch:{ all -> 0x0101 }
            int r0 = r0.getRecordMode()     // Catch:{ all -> 0x0101 }
            r1 = 1
            if (r0 != 0) goto L_0x002f
            java.lang.String r0 = "record_mode"
            int r0 = com.sec.android.app.voicenote.provider.Settings.getIntSettings(r0, r1)     // Catch:{ all -> 0x0101 }
        L_0x002f:
            boolean r2 = r8.isWiredHeadSetConnected()     // Catch:{ all -> 0x0101 }
            r3 = 2
            r4 = 0
            r5 = 4
            if (r2 != 0) goto L_0x003e
            boolean r2 = r8.isBluetoothSCOConnected()     // Catch:{ all -> 0x0101 }
            if (r2 == 0) goto L_0x004b
        L_0x003e:
            if (r0 == r3) goto L_0x00f9
            if (r0 == r5) goto L_0x00f5
            boolean r2 = r8.mIsNeedExternalMicAlert     // Catch:{ all -> 0x0101 }
            if (r2 == 0) goto L_0x004b
            r8.showNotificationAlert()     // Catch:{ all -> 0x0101 }
            r8.mIsNeedExternalMicAlert = r4     // Catch:{ all -> 0x0101 }
        L_0x004b:
            java.lang.String r2 = "Engine"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0101 }
            r6.<init>()     // Catch:{ all -> 0x0101 }
            java.lang.String r7 = "resumeRecord: recordMode = "
            r6.append(r7)     // Catch:{ all -> 0x0101 }
            r6.append(r0)     // Catch:{ all -> 0x0101 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0101 }
            com.sec.android.app.voicenote.provider.Log.m26i(r2, r6)     // Catch:{ all -> 0x0101 }
            if (r0 != r5) goto L_0x006f
            android.content.Context r2 = r8.mAppContext     // Catch:{ all -> 0x0101 }
            boolean r2 = com.sec.android.app.voicenote.provider.Network.isNetworkConnected(r2)     // Catch:{ all -> 0x0101 }
            if (r2 != 0) goto L_0x006f
            r0 = -106(0xffffffffffffff96, float:NaN)
            monitor-exit(r8)
            return r0
        L_0x006f:
            if (r0 != r3) goto L_0x0077
            r2 = 1
            r8.setCategoryID(r2)     // Catch:{ all -> 0x0101 }
            goto L_0x0086
        L_0x0077:
            if (r0 != r1) goto L_0x007f
            r2 = 0
            r8.setCategoryID(r2)     // Catch:{ all -> 0x0101 }
            goto L_0x0086
        L_0x007f:
            if (r0 != r5) goto L_0x0086
            r2 = 2
            r8.setCategoryID(r2)     // Catch:{ all -> 0x0101 }
        L_0x0086:
            java.lang.String r2 = r8.getRecentFilePath()     // Catch:{ all -> 0x0101 }
            com.sec.android.app.voicenote.service.MetadataRepository r3 = com.sec.android.app.voicenote.service.MetadataRepository.getInstance()     // Catch:{ all -> 0x0101 }
            java.lang.String r6 = r3.getPath()     // Catch:{ all -> 0x0101 }
            if (r6 == 0) goto L_0x009a
            boolean r6 = r6.isEmpty()     // Catch:{ all -> 0x0101 }
            if (r6 == 0) goto L_0x00a8
        L_0x009a:
            r3.read(r2)     // Catch:{ all -> 0x0101 }
            com.sec.android.app.voicenote.provider.DBProvider r6 = com.sec.android.app.voicenote.provider.DBProvider.getInstance()     // Catch:{ all -> 0x0101 }
            int r6 = r6.getRecordModeByPath(r2)     // Catch:{ all -> 0x0101 }
            r3.setRecordMode(r6)     // Catch:{ all -> 0x0101 }
        L_0x00a8:
            com.sec.android.app.voicenote.service.Recorder r3 = com.sec.android.app.voicenote.service.Recorder.getInstance()     // Catch:{ all -> 0x0101 }
            int r3 = r3.getRecorderState()     // Catch:{ all -> 0x0101 }
            if (r3 == r5) goto L_0x00b4
            if (r3 != r1) goto L_0x00d3
        L_0x00b4:
            com.sec.android.app.voicenote.service.AudioFormat r3 = r8.mAudioFormat     // Catch:{ all -> 0x0101 }
            if (r3 != 0) goto L_0x00bf
            com.sec.android.app.voicenote.service.AudioFormat r3 = new com.sec.android.app.voicenote.service.AudioFormat     // Catch:{ all -> 0x0101 }
            r3.<init>(r0)     // Catch:{ all -> 0x0101 }
            r8.mAudioFormat = r3     // Catch:{ all -> 0x0101 }
        L_0x00bf:
            com.sec.android.app.voicenote.service.AudioFormat r0 = r8.mAudioFormat     // Catch:{ all -> 0x0101 }
            java.lang.String r0 = r0.getExtension()     // Catch:{ all -> 0x0101 }
            java.lang.String r2 = com.sec.android.app.voicenote.provider.StorageProvider.createTempFile(r2, r0)     // Catch:{ all -> 0x0101 }
            com.sec.android.app.voicenote.service.ContentItem r0 = new com.sec.android.app.voicenote.service.ContentItem     // Catch:{ all -> 0x0101 }
            int r3 = r8.mCurrentTime     // Catch:{ all -> 0x0101 }
            r0.<init>(r2, r3, r4)     // Catch:{ all -> 0x0101 }
            r8.pushContentItem(r0)     // Catch:{ all -> 0x0101 }
        L_0x00d3:
            com.sec.android.app.voicenote.service.Recorder r0 = com.sec.android.app.voicenote.service.Recorder.getInstance()     // Catch:{ all -> 0x0101 }
            int r3 = r8.mCurrentTime     // Catch:{ all -> 0x0101 }
            int r0 = r0.resumeRecord(r2, r3)     // Catch:{ all -> 0x0101 }
            if (r0 == 0) goto L_0x00e3
            r8.popContentItem()     // Catch:{ all -> 0x0101 }
            goto L_0x00ff
        L_0x00e3:
            com.sec.android.app.voicenote.provider.CallRejectChecker r2 = com.sec.android.app.voicenote.provider.CallRejectChecker.getInstance()     // Catch:{ all -> 0x0101 }
            r2.setReject(r1)     // Catch:{ all -> 0x0101 }
            r8.setAutoResumeRecording(r4)     // Catch:{ all -> 0x0101 }
            com.sec.android.app.voicenote.service.Player r1 = com.sec.android.app.voicenote.service.Player.getInstance()     // Catch:{ all -> 0x0101 }
            r1.stopPlay()     // Catch:{ all -> 0x0101 }
            goto L_0x00ff
        L_0x00f5:
            r0 = -105(0xffffffffffffff97, float:NaN)
            monitor-exit(r8)
            return r0
        L_0x00f9:
            r0 = -104(0xffffffffffffff98, float:NaN)
            monitor-exit(r8)
            return r0
        L_0x00fd:
            r0 = -102(0xffffffffffffff9a, float:NaN)
        L_0x00ff:
            monitor-exit(r8)
            return r0
        L_0x0101:
            r0 = move-exception
            monitor-exit(r8)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.Engine.resumeRecord():int");
    }

    public int startPlay(String str) {
        return startPlay(str, true);
    }

    public int startPlay(String str, boolean z) {
        return startPlay(str, DBProvider.getInstance().getIdByPath(str), z);
    }

    public int startPlay(long j) {
        return startPlay(j, true);
    }

    public int startPlay(long j, boolean z) {
        return startPlay(DBProvider.getInstance().getPathById(j), j, z);
    }

    public int startPlay(String str, long j, boolean z) {
        Log.m26i(TAG, "startPlay - id : " + j + " play : " + z);
        if (getEngineState() != 0) {
            return ReturnCodes.BUSY;
        }
        if (str == null || str.isEmpty()) {
            Log.m22e(TAG, "startPlay - path is abnormal");
            if (Log.ENG) {
                Log.m22e(TAG, "startPlay - path is abnormal : " + str);
            }
            return -115;
        } else if (!PhoneStateProvider.getInstance().isCallIdle(this.mAppContext)) {
            return -103;
        } else {
            if (startOverwrite(15) == 0) {
                Log.m19d(TAG, "startPlay - startOverwrite done");
                return 0;
            }
            if (getContentItemCount() == 0) {
                File file = new File(str);
                if (!file.exists() || file.isDirectory()) {
                    Log.m22e(TAG, "startPlay file is not exist or directory");
                    if (Log.ENG) {
                        Log.m22e(TAG, "startPlay file is not exist or directory - path : " + file.getPath());
                    }
                    return -115;
                }
                AudioFormat audioFormat = new AudioFormat(CursorProvider.getInstance().getRecordMode(j));
                String mimeType = CursorProvider.getInstance().getMimeType(j);
                if (mimeType == null) {
                    mimeType = DBProvider.getInstance().getContentMimeType(j);
                }
                audioFormat.setMimeType(mimeType);
                setOriginalFilePath(str);
                Log.m26i(TAG, "startPlay set new AudioFormat extension : " + audioFormat.getExtension());
                setAudioFormat(audioFormat);
                pushContentItem(new ContentItem(str, 0, (int) CursorProvider.getInstance().getDuration(j)));
            }
            if (this.mScene != 6) {
                resetOverwriteTime();
            }
            if (!Player.getInstance().startPlay(str, j, z)) {
                return -115;
            }
            this.mFileObserver = new FileEventObserver(str, 2564);
            this.mFileObserver.setPath(str);
            this.mFileObserver.startWatching();
            if (VoiceNoteFeature.isGateEnabled()) {
                android.util.Log.i("GATE", "<GATE-M> AUDIO_PLAYING </GATE-M>");
            }
            if (this.mScene == 8) {
                Recorder.getInstance().setOverwriteByDrag(true);
            }
            return 0;
        }
    }

    public boolean pausePlay() {
        Log.m26i(TAG, "pausePlay");
        return Player.getInstance().pausePlay();
    }

    public void setPausedByCall(boolean z) {
        Player.getInstance().setPausedByCall(z);
    }

    public int resumePlay() {
        return resumePlay(true);
    }

    public int resumePlay(boolean z) {
        Log.m26i(TAG, "resumePlay - mCurrentTime : " + this.mCurrentTime);
        if (getEngineState() != 0) {
            return ReturnCodes.BUSY;
        }
        if (getContentItemCount() == 0) {
            Log.m22e(TAG, "resumePlay No files exist");
            return -115;
        } else if (!PhoneStateProvider.getInstance().isCallIdle(this.mAppContext)) {
            return -103;
        } else {
            if (startOverwrite(15) == 0) {
                Log.m19d(TAG, "resumePlay - startOverwrite done");
                return 0;
            } else if (VoiceNoteApplication.getScene() != 12 || Network.isNetworkConnected(this.mAppContext)) {
                int duration = getDuration();
                String recentFilePath = getRecentFilePath();
                if (recentFilePath.equals(Player.getInstance().getPath())) {
                    Log.m26i(TAG, "resumePlay - resumePlay duration : " + duration);
                    if (getTranslationState() == 1) {
                        int[] iArr = this.mTrimTime;
                        if (iArr[1] <= 0 || this.mCurrentTime + 100 <= iArr[1]) {
                            int i = this.mCurrentTime;
                            if (i + 100 <= duration) {
                                seekTo(i);
                            } else if (i + 100 > duration) {
                                setCurrentTime(0);
                            }
                        } else {
                            setCurrentTime(iArr[0]);
                        }
                    }
                    return Player.getInstance().resumePlay();
                }
                Log.m26i(TAG, "resumePlay - startPlay");
                if (this.mCurrentTime + 100 > duration) {
                    setCurrentTime(0);
                }
                int i2 = this.mCurrentTime;
                int startPlay = startPlay(recentFilePath, z);
                int[] iArr2 = this.mTrimTime;
                if (iArr2[1] > 0 && this.mCurrentTime + 100 > iArr2[1]) {
                    setCurrentTime(iArr2[0]);
                    return startPlay;
                } else if (this.mCurrentTime + 100 > duration) {
                    return startPlay;
                } else {
                    seekTo(i2);
                    return startPlay;
                }
            } else {
                Log.m19d(TAG, "resumePlay - network is disconnected, so stt can't be translated");
                return -106;
            }
        }
    }

    public void skipInterval(int i) {
        Log.m26i(TAG, "skipInterval : " + i);
        Player.getInstance().skipInterval(i);
    }

    /* access modifiers changed from: package-private */
    public boolean initPlay() {
        Log.m26i(TAG, "stopPlay");
        FileEventObserver fileEventObserver = this.mFileObserver;
        if (fileEventObserver != null) {
            fileEventObserver.stopWatching();
        }
        return Player.getInstance().initPlay();
    }

    public boolean stopPlay() {
        Log.m26i(TAG, "stopPlay");
        FileEventObserver fileEventObserver = this.mFileObserver;
        if (fileEventObserver != null) {
            fileEventObserver.stopWatching();
        }
        return Player.getInstance().stopPlay();
    }

    public void doNextPlay() {
        Player.getInstance().lambda$onCompletion$0$Player();
    }

    public boolean stopPlay(boolean z) {
        Log.m26i(TAG, "stopPlay - updateMetadata : " + z);
        FileEventObserver fileEventObserver = this.mFileObserver;
        if (fileEventObserver != null) {
            fileEventObserver.stopWatching();
        }
        return Player.getInstance().stopPlay(z);
    }

    public int getDuration() {
        if (Player.getInstance().getPlayerState() != 1) {
            return Player.getInstance().getDuration();
        }
        int recentFileDuration = getRecentFileDuration();
        int i = this.mCurrentTime;
        if (recentFileDuration > i) {
            i = getRecentFileDuration();
        }
        return getMaxDuration(i);
    }

    public int getRecordingDuration() {
        return Recorder.getInstance().getDuration();
    }

    public String getPath() {
        return Player.getInstance().getPath();
    }

    public void seekTo(int i) {
        Player.getInstance().seekTo(i);
    }

    public long getID() {
        return Player.getInstance().getID();
    }

    public void renamePath(String str) {
        ContentItem popContentItem = popContentItem();
        if (popContentItem != null) {
            popContentItem.replacePath(str);
            pushContentItem(popContentItem);
            if (this.mOriginalFilePath != null) {
                setOriginalFilePath(str);
            }
            Player.getInstance().renamePath(str);
            this.mFileObserver.setPath(str);
        }
    }

    public int getRepeatMode() {
        return Player.getInstance().getRepeatMode();
    }

    public int[] getRepeatPosition() {
        return Player.getInstance().getRepeatPosition();
    }

    public float getPlaySpeed() {
        return Player.getInstance().getPlaySpeed();
    }

    public int getSkipSilenceMode() {
        return Player.getInstance().getSkipSilenceMode();
    }

    public void updateTrack() {
        Player.getInstance().updateTrack();
    }

    public int setRepeatMode(int i) {
        return Player.getInstance().setRepeatMode(i);
    }

    public int setDCRepeatMode(int i, int i2) {
        return Player.getInstance().setDCRepeatMode(i, i2);
    }

    public float setPlaySpeed(float f) {
        return Player.getInstance().setPlaySpeed(f);
    }

    public int setSkipSilenceMode(int i) {
        return Player.getInstance().enableSkipSilenceMode(i);
    }

    public void resetPauseNotByUser() {
        Player.getInstance().resetPauseNotByUser();
    }

    public void setMonoMode(boolean z) {
        Player.getInstance().setMonoMode(z);
    }

    public void setMute(boolean z, boolean z2) {
        Player.getInstance().setMute(z, z2);
    }

    /* access modifiers changed from: package-private */
    public void enableSystemSound() {
        Recorder.getInstance().enableSystemSound();
    }

    public void setRepeatTime(int i, int i2) {
        Player.getInstance().setRepeatTime(i, i2);
        notifyObservers(104, -1, -1);
    }

    private boolean containsListener(OnEngineListener onEngineListener) {
        ArrayList<WeakReference<OnEngineListener>> arrayList = this.mListeners;
        if (!(arrayList == null || onEngineListener == null)) {
            Iterator<WeakReference<OnEngineListener>> it = arrayList.iterator();
            while (it.hasNext()) {
                if (onEngineListener.equals(it.next().get())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void removeListener(OnEngineListener onEngineListener) {
        ArrayList<WeakReference<OnEngineListener>> arrayList = this.mListeners;
        if (arrayList != null && onEngineListener != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                WeakReference weakReference = this.mListeners.get(size);
                if (weakReference.get() == null || ((OnEngineListener) weakReference.get()).equals(onEngineListener)) {
                    this.mListeners.remove(weakReference);
                }
            }
        }
    }

    public final void registerListener(OnEngineListener onEngineListener) {
        if (onEngineListener != null && !containsListener(onEngineListener)) {
            this.mListeners.add(new WeakReference(onEngineListener));
            if (Player.getInstance().getPlayerState() != 1) {
                onEngineListener.onEngineUpdate(2010, Player.getInstance().getPlayerState(), -1);
                onEngineListener.onEngineUpdate(2012, this.mCurrentTime, -1);
            } else if (Recorder.getInstance().getRecorderState() != 1) {
                onEngineListener.onEngineUpdate(1010, Recorder.getInstance().getRecorderState(), -1);
                onEngineListener.onEngineUpdate(1011, this.mCurrentTime, -1);
            }
        }
    }

    public final void unregisterListener(OnEngineListener onEngineListener) {
        if (onEngineListener != null && containsListener(onEngineListener)) {
            removeListener(onEngineListener);
        }
    }

    /* access modifiers changed from: private */
    public void notifyObservers(int i, int i2, int i3) {
        int size = this.mListeners.size() - 1;
        while (size >= 0) {
            try {
                WeakReference weakReference = this.mListeners.get(size);
                if (weakReference != null) {
                    if (weakReference.get() == null) {
                        this.mListeners.remove(weakReference);
                    } else {
                        ((OnEngineListener) weakReference.get()).onEngineUpdate(i, i2, i3);
                    }
                    size--;
                } else {
                    return;
                }
            } catch (IndexOutOfBoundsException e) {
                Log.m24e(TAG, "IndexOutOfBoundsException !", (Throwable) e);
            } catch (NullPointerException e2) {
                Log.m24e(TAG, "NullPointerException !", (Throwable) e2);
            }
        }
    }

    private void unregisterAllListener() {
        this.mListeners.clear();
    }

    public void setCurrentTime(int i, boolean z) {
        this.mCurrentTime = i;
        if (z) {
            notifyObservers(101, i, -1);
        }
    }

    private static class Position {
        static final int END = 1;
        static final int START = 0;

        private Position() {
        }
    }

    public void resetTrimTime() {
        Log.m19d(TAG, "resetTrimTime");
        int[] iArr = this.mTrimTime;
        iArr[0] = -1;
        iArr[1] = -1;
    }

    public void setTrimStartTime(int i) {
        setTrimStartTime(i, true);
    }

    public void setTrimStartTime(int i, boolean z) {
        this.mTrimTime[0] = i;
        if (z) {
            notifyObservers(102, -1, -1);
        }
    }

    public void setTrimEndTime(int i) {
        setTrimEndTime(i, true);
    }

    /* access modifiers changed from: package-private */
    public void setTrimEndTime(int i, boolean z) {
        this.mTrimTime[1] = i;
        if (z) {
            notifyObservers(102, -1, -1);
        }
    }

    public int getTrimStartTime() {
        return this.mTrimTime[0];
    }

    public int getTrimEndTime() {
        return this.mTrimTime[1];
    }

    public void resetOverwriteTime() {
        Log.m19d(TAG, "resetOverwriteTime");
        int[] iArr = this.mOverwriteTime;
        iArr[0] = -1;
        iArr[1] = -1;
    }

    /* access modifiers changed from: package-private */
    public void setOverwriteStartTime(int i) {
        Log.m19d(TAG, "setOverwriteStartTime - start : " + i);
        this.mOverwriteTime[0] = i;
    }

    /* access modifiers changed from: package-private */
    public void setOverwriteEndTime(int i) {
        Log.m19d(TAG, "setOverwriteEndTime - end : " + i);
        this.mOverwriteTime[1] = i;
    }

    public int getOverwriteStartTime() {
        return this.mOverwriteTime[0];
    }

    public int getOverwriteEndTime() {
        return this.mOverwriteTime[1];
    }

    public boolean isTrimEnable() {
        int duration = getDuration();
        int[] iArr = this.mTrimTime;
        if (iArr[0] == -1 || iArr[1] == -1) {
            Log.m26i(TAG, "isTrimEnable FALSE - START : " + this.mTrimTime[0] + " END : " + this.mTrimTime[1] + " duration :" + duration);
            return false;
        } else if (iArr[0] >= 100 || duration - iArr[1] >= 100) {
            int[] iArr2 = this.mTrimTime;
            if (iArr2[1] - iArr2[0] < 1000) {
                Log.m26i(TAG, "isTrimEnable FALSE - TRIM_MIN_INTERVAL : 1000");
                return false;
            } else if (getRecorderState() == 2) {
                Log.m26i(TAG, "isTrimEnable FALSE - RECORDING");
                return false;
            } else if (PhoneStateProvider.getInstance().isDuringCall(this.mAppContext)) {
                Log.m26i(TAG, "isTrimEnable FALSE - isDuringCall");
                return false;
            } else {
                Log.m19d(TAG, "isTrimEnable TRUE - START : " + this.mTrimTime[0] + " END : " + this.mTrimTime[1] + " duration :" + duration);
                return true;
            }
        } else {
            Log.m26i(TAG, "isTrimEnable FALSE - START : " + this.mTrimTime[0] + " END : " + this.mTrimTime[1] + " duration :" + duration);
            return false;
        }
    }

    public boolean isDeleteEnable() {
        int duration = getDuration();
        int[] iArr = this.mTrimTime;
        if (iArr[0] == -1 || iArr[1] == -1) {
            Log.m26i(TAG, "isDeleteEnable FALSE - START : " + this.mTrimTime[0] + " END : " + this.mTrimTime[1] + " duration :" + duration);
            return false;
        } else if (iArr[0] >= 100 || duration - iArr[1] >= 100) {
            int[] iArr2 = this.mTrimTime;
            if ((iArr2[0] + duration) - iArr2[1] < 1000) {
                Log.m26i(TAG, "isDeleteEnable FALSE - TRIM_MIN_INTERVAL : 1000");
                return false;
            } else if (getRecorderState() == 2) {
                Log.m26i(TAG, "isDeleteEnable FALSE - RECORDING");
                return false;
            } else if (PhoneStateProvider.getInstance().isDuringCall(this.mAppContext)) {
                Log.m26i(TAG, "isDeleteEnable FALSE - isDuringCall");
                return false;
            } else {
                Log.m19d(TAG, "isDeleteEnable TRUE - START : " + this.mTrimTime[0] + " END : " + this.mTrimTime[1] + " duration :" + duration);
                return true;
            }
        } else {
            Log.m26i(TAG, "isDeleteEnable FALSE - START : " + this.mTrimTime[0] + " END : " + this.mTrimTime[1] + " duration :" + duration);
            return false;
        }
    }

    public boolean isEditRecordable() {
        if (getAudioFormat() == null) {
            Log.m32w(TAG, "isEditRecordable - Audio Format obj is null");
            return false;
        }
        boolean z = getAudioFormat().getMaxDuration() - getCurrentTime() > 1000;
        if (getPlayerState() == 3) {
            z = false;
        }
        int recordMode = MetadataRepository.getInstance().getRecordMode();
        if (recordMode == 2 && (!VoiceNoteFeature.FLAG_SUPPORT_INTERVIEW || DesktopModeProvider.isDesktopMode())) {
            z = false;
        }
        if (recordMode == 4 && (!VoiceNoteFeature.FLAG_SUPPORT_VOICE_MEMO(this.mAppContext) || DesktopModeProvider.isDesktopMode())) {
            z = false;
        }
        if (recordMode != 4 || ((long) getDuration()) <= 600050) {
            return z;
        }
        return false;
    }

    public boolean isSaveTranslatable() {
        return getCurrentTime() - getTrimStartTime() > 1000;
    }

    public boolean isTranslateable() {
        if (getAudioFormat() == null) {
            return false;
        }
        boolean z = getDuration() - getCurrentTime() > 1000;
        if (DesktopModeProvider.isDesktopMode()) {
            return false;
        }
        return z;
    }

    public boolean isEditSaveEnable() {
        if (getEngineState() == 0 && StorageProvider.isTempFile(getRecentFilePath()) && StorageProvider.isExistFile(getOriginalFilePath())) {
            return true;
        }
        return false;
    }

    public int startTrim() {
        Log.m26i(TAG, "startTrim");
        if (getEngineState() != 0) {
            return ReturnCodes.BUSY;
        }
        if (!isTrimEnable()) {
            Log.m22e(TAG, "Trim disabled");
            return -111;
        }
        if (Recorder.getInstance().getRecorderState() == 3 || Recorder.getInstance().getRecorderState() == 2) {
            ContentItem peekContentItem = peekContentItem();
            if (peekContentItem == null) {
                Log.m22e(TAG, "start startTrim but stack is empty !!!");
                if (this.mAudioFormat == null) {
                    this.mAudioFormat = new AudioFormat(Recorder.getInstance().getRecordMode());
                }
                peekContentItem = new ContentItem(StorageProvider.createTempFile(this.mAudioFormat.getExtension()));
                pushContentItem(peekContentItem);
            }
            if (!Recorder.getInstance().saveFile(peekContentItem)) {
                popContentItem();
            }
        }
        if (Player.getInstance().getPlayerState() != 1) {
            Player.getInstance().stopPlay();
        }
        if (getContentItemCount() >= 2) {
            startOverwrite(10);
            return -2;
        } else if (getContentItemCount() <= 0) {
            return -118;
        } else {
            setEngineState(2);
            Editor.getInstance().registerListener(this);
            ContentItem peekContentItem2 = peekContentItem();
            if (peekContentItem2 == null) {
                return -116;
            }
            String path = peekContentItem2.getPath();
            String createTempFile = StorageProvider.createTempFile(path, path.substring(path.lastIndexOf(46)));
            int[] iArr = this.mTrimTime;
            pushContentItem(new ContentItem(createTempFile, 0, iArr[1] - iArr[0]));
            Editor instance = Editor.getInstance();
            int[] iArr2 = this.mTrimTime;
            instance.trim(path, createTempFile, iArr2[0], iArr2[1]);
            return -2;
        }
    }

    public int startDelete() {
        Log.m26i(TAG, "startDelete");
        if (getEngineState() != 0) {
            return ReturnCodes.BUSY;
        }
        if (!isDeleteEnable()) {
            Log.m22e(TAG, "Delete disabled");
            return -113;
        }
        if (Recorder.getInstance().getRecorderState() == 3 || Recorder.getInstance().getRecorderState() == 2) {
            ContentItem peekContentItem = peekContentItem();
            if (peekContentItem == null) {
                Log.m22e(TAG, "start startTrim but stack is empty !!!");
                if (this.mAudioFormat == null) {
                    this.mAudioFormat = new AudioFormat(Recorder.getInstance().getRecordMode());
                }
                peekContentItem = new ContentItem(StorageProvider.createTempFile(this.mAudioFormat.getExtension()));
                pushContentItem(peekContentItem);
            }
            if (!Recorder.getInstance().saveFile(peekContentItem)) {
                popContentItem();
            }
        }
        if (Player.getInstance().getPlayerState() != 1) {
            Player.getInstance().stopPlay();
        }
        if (getContentItemCount() >= 2) {
            startOverwrite(16);
            return -2;
        } else if (getContentItemCount() <= 0) {
            return -118;
        } else {
            setEngineState(2);
            Editor.getInstance().registerListener(this);
            ContentItem peekContentItem2 = peekContentItem();
            if (peekContentItem2 == null) {
                return -116;
            }
            String path = peekContentItem2.getPath();
            String createTempFile = StorageProvider.createTempFile(path, path.substring(path.lastIndexOf(46)));
            int endTime = peekContentItem2.getEndTime();
            int[] iArr = this.mTrimTime;
            pushContentItem(new ContentItem(createTempFile, 0, endTime - (iArr[1] - iArr[0])));
            Editor instance = Editor.getInstance();
            int[] iArr2 = this.mTrimTime;
            instance.delete(path, createTempFile, iArr2[0], iArr2[1]);
            return -2;
        }
    }

    public int startOverwrite(int i) {
        Log.m26i(TAG, "startOverwrite - event : " + i);
        if (getEngineState() != 0) {
            return ReturnCodes.BUSY;
        }
        Recorder instance = Recorder.getInstance();
        if (instance.getRecorderState() == 3 || instance.getRecorderState() == 2) { // if paused or stopped
            ContentItem peekContentItem = peekContentItem();
            if (peekContentItem == null) {
                Log.m22e(TAG, "start overwrite but stack is empty !!!");
                if (this.mAudioFormat == null) {
                    this.mAudioFormat = new AudioFormat(instance.getRecordMode());
                }
                peekContentItem = new ContentItem(StorageProvider.createTempFile(this.mAudioFormat.getExtension()));
                pushContentItem(peekContentItem);
            }
            if (!instance.saveFile(peekContentItem)) {
                popContentItem();
            }
        }
        if (getContentItemCount() >= 2) {
            setEngineState(2);
            ContentItem popContentItem = popContentItem();
            ContentItem peekContentItem2 = peekContentItem();
            pushContentItem(popContentItem);
            if (peekContentItem2 == null || popContentItem == null) {
                return 0;
            }
            String path = peekContentItem2.getPath();
            ContentItem contentItem = new ContentItem(StorageProvider.createTempFile(path, path.substring(path.lastIndexOf(46))));
            contentItem.setStartTime(popContentItem.getStartTime() < peekContentItem2.getStartTime() ? popContentItem.getStartTime() : peekContentItem2.getStartTime());
            contentItem.setEndTime(popContentItem.getEndTime() > peekContentItem2.getEndTime() ? popContentItem.getEndTime() : peekContentItem2.getEndTime());
            contentItem.setDuration(contentItem.getEndTime() - contentItem.getStartTime());
            pushContentItem(contentItem);
            Log.m19d(TAG, "startOverwrite originalItem  mStartTime : " + peekContentItem2.getStartTime() + " mEndTime : " + peekContentItem2.getEndTime() + " path : " + peekContentItem2.getPath());
            Log.m19d(TAG, "startOverwrite overwriteItem mStartTime : " + popContentItem.getStartTime() + " mEndTime : " + popContentItem.getEndTime() + " path : " + popContentItem.getPath());
            Log.m19d(TAG, "startOverwrite outputItem    mStartTime : " + contentItem.getStartTime() + " mEndTime : " + contentItem.getEndTime() + " path : " + contentItem.getPath());
            Editor.getInstance().registerListener(this);
            Editor.getInstance().overwrite(peekContentItem2.getPath(), popContentItem.getPath(), contentItem.getPath(), popContentItem.getStartTime(), popContentItem.getEndTime(), i);
            return 0;
        } else if (Player.getInstance().getPlayerState() == 3 || Player.getInstance().getPlayerState() == 4) {
            Log.m32w(TAG, "SKIP to write metadata while Playing : " + Player.getInstance().getPlayerState());
            return -112;
        } else {
            MetadataRepository instance2 = MetadataRepository.getInstance();
            instance2.rename((String) null, getRecentFilePath());
            instance2.write(getRecentFilePath());
            return -112;
        }
    }

    public boolean isSaveEnable() {
        boolean isSaveEnable = Recorder.getInstance().isSaveEnable();
        if (!isSaveEnable && isOpenedSpecialApp() && getContentItemCount() == 1) {
            ContentItem peekContentItem = peekContentItem();
            if (peekContentItem.getEndTime() - peekContentItem.getStartTime() >= 1000) {
                return true;
            }
        }
        return isSaveEnable;
    }

    public int getMaxDurationRecord(int i) {
        return Recorder.getInstance().getMaxDuration(i);
    }

    public boolean isWiredHeadSetConnected() {
        return this.mIsWiredHeadSetConnected;
    }

    public void setWiredHeadSetConnected(boolean z) {
        this.mIsWiredHeadSetConnected = z;
    }

    public void setExternalMicAlert() {
        this.mIsNeedExternalMicAlert = this.mIsWiredHeadSetConnected || this.mIsBluetoothSCOConnected;
    }

    public boolean isBluetoothHeadSetConnected() {
        return this.mIsBluetoothHeadSetConnected;
    }

    public void setBluetoothHeadSetConnected(boolean z) {
        this.mIsBluetoothHeadSetConnected = z;
    }

    public void setBluetoothSCOConnected(boolean z) {
        this.mIsBluetoothSCOConnected = z;
    }

    public void setRecordByBluetoothSCO(boolean z) {
        this.mIsRecordByBluetoothSCO = z;
    }

    public void setSimpleRecorderMode(boolean z) {
        this.mSimpleRecorderMode = z;
    }

    public boolean isSimpleRecorderMode() {
        return this.mSimpleRecorderMode;
    }

    private void setEngineState(int i) {
        Log.m26i(TAG, "setEngineState - state : " + i);
        this.mEngineState = i;
    }

    public int getEngineState() {
        Log.m26i(TAG, "getEngineState - state : " + this.mEngineState);
        return this.mEngineState;
    }

    public void setSimplePlayerMode(boolean z) {
        this.mSimplePlayerMode = z;
    }

    public boolean isSimplePlayerMode() {
        return this.mSimplePlayerMode;
    }

    public boolean isRunningSwitchSkipMuted() {
        return Player.getInstance().isIsRunningSwitchSkipMuted();
    }

    public void setPointerIcon(int i) {
        notifyObservers(105, i, -1);
    }

    private String getTitle(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        return str.substring(str.lastIndexOf(47) + 1, str.lastIndexOf(46));
    }

    public int startTranslation() {
        Log.m26i(TAG, "startTranslation");
        getInstance().setTrimStartTime(getInstance().getCurrentTime(), false);
        MediaSessionManager.getInstance().setIsTranslation(true);
        int resumePlay = getInstance().resumePlay();
        if (resumePlay < 0) {
            return resumePlay;
        }
        Decoder.getInstance().setMediaPath(CursorProvider.getInstance().getPath(getInstance().getID()));
        Decoder.getInstance().setStartTime((long) getInstance().getCurrentTime());
        Decoder.getInstance().start(this.mAppContext);
        return 0;
    }

    public void pauseTranslation(boolean z) {
        Log.m26i(TAG, "pauseTranslation");
        Decoder.getInstance().pause(z);
        pausePlay();
    }

    public int resumeTranslation() {
        Log.m26i(TAG, "resumeTranslation");
        Decoder.getInstance().setStartTime((long) getCurrentTime());
        int resumePlay = resumePlay();
        if (resumePlay >= 0) {
            Decoder.getInstance().resume();
        }
        return resumePlay;
    }

    public void stopTranslation(boolean z) {
        Log.m26i(TAG, "stopTranslation");
        this.mIsShowingToastAfterSaveTranslationFile = z;
        getInstance().setTrimEndTime(getInstance().getCurrentTime(), false);
        MediaSessionManager.getInstance().setIsTranslation(false);
        String saveTranslationFile = saveTranslationFile();
        Decoder.getInstance().stop();
        stopPlay(false);
        Player.getInstance().renamePath(saveTranslationFile);
    }

    public void cancelTranslation(boolean z) {
        Log.m26i(TAG, "cancelTranslation");
        getInstance().setTrimStartTime(0, false);
        MediaSessionManager.getInstance().setIsTranslation(false);
        MetadataRepository.getInstance().clearSttData();
        Decoder.getInstance().cancel();
        if (z) {
            stopPlay(false);
            return;
        }
        if (getPlayerState() == 3) {
            pausePlay();
        }
        setCurrentTime(0);
    }

    public int getTranslationState() {
        return Decoder.getInstance().getTranslationState();
    }

    public boolean isTranslationComplete() {
        return Decoder.getInstance().isComplete();
    }

    public String saveTranslationFile() {
        String str;
        String str2 = this.mUserSettingName;
        if (str2 == null || str2.isEmpty()) {
            Context context = this.mAppContext;
            str = getTitle(this.mOriginalFilePath) + "_" + (context != null ? context.getString(C0690R.string.prefix_voicememo).toLowerCase() : "memo");
        } else {
            str = this.mUserSettingName;
            setUserSettingName((String) null);
        }
        if (DBProvider.getInstance().isSameFileInLibrary(str)) {
            Log.m26i(TAG, "rename saving file while save by translated file already exist");
            str = DBProvider.getInstance().createNewTitle(str);
        }
        int lastIndexOf = this.mOriginalFilePath.lastIndexOf(46);
        String str3 = this.mOriginalFilePath;
        String substring = str3.substring(lastIndexOf, str3.length());
        String str4 = this.mOriginalFilePath;
        String str5 = str4.substring(0, str4.lastIndexOf(47)) + '/' + str + substring;
        MetadataRepository instance = MetadataRepository.getInstance();
        instance.setRecordMode(4);
        instance.setDataChanged(true);
        setEngineState(2);
        Editor.getInstance().registerListener(this);
        Editor.getInstance().setTranslationFile(true);
        int[] iArr = this.mTrimTime;
        pushContentItem(new ContentItem(str5, 0, iArr[1] - iArr[0]));
        Editor instance2 = Editor.getInstance();
        String str6 = this.mOriginalFilePath;
        int[] iArr2 = this.mTrimTime;
        instance2.trim(str6, str5, iArr2[0], iArr2[1]);
        this.mLastSavedFilePath = str5;
        setOriginalFilePath(str5);
        return str5;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x001a, code lost:
        r0 = r0 + 1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void saveTranslationAfterTrim() {
        /*
            r5 = this;
            java.lang.String r0 = "Engine"
            java.lang.String r1 = "saveTranslationAfterTrim"
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r1)
            java.lang.String r0 = r5.mLastSavedFilePath
            r1 = 47
            int r0 = r0.lastIndexOf(r1)
            java.lang.String r1 = r5.mLastSavedFilePath
            r2 = 46
            int r1 = r1.lastIndexOf(r2)
            r2 = 1
            if (r0 < 0) goto L_0x002c
            int r0 = r0 + r2
            if (r0 >= r1) goto L_0x002c
            java.lang.String r3 = r5.mLastSavedFilePath
            int r3 = r3.length()
            if (r1 >= r3) goto L_0x002c
            java.lang.String r3 = r5.mLastSavedFilePath
            java.lang.String r0 = r3.substring(r0, r1)
            goto L_0x002e
        L_0x002c:
            java.lang.String r0 = ""
        L_0x002e:
            java.lang.String r3 = r5.mLastSavedFilePath
            int r4 = r3.length()
            java.lang.String r1 = r3.substring(r1, r4)
            java.lang.String r3 = r5.mLastSavedFilePath
            r4 = 4
            r5.saveFileToMediaDB(r3, r4, r1, r0)
            boolean r1 = r5.mIsShowingToastAfterSaveTranslationFile
            if (r1 == 0) goto L_0x0059
            android.content.Context r1 = r5.mAppContext
            r3 = 2131755291(0x7f10011b, float:1.9141457E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r0
            java.lang.String r0 = r1.getString(r3, r2)
            android.widget.Toast r0 = android.widget.Toast.makeText(r1, r0, r4)
            r0.show()
            r5.mIsShowingToastAfterSaveTranslationFile = r4
        L_0x0059:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.Engine.saveTranslationAfterTrim():void");
    }

    private void saveFileToMediaDB(String str, int i, String str2, String str3) {
        Log.m26i(TAG, "saveFileToMediaDB");
        File file = new File(str);
        if (!file.exists()) {
            Log.m22e(TAG, "saveFileToMediaDB - file is not exists");
            return;
        }
        long j = this.mCategoryID;
        setCategoryID(0);
        Log.m19d(TAG, "saveFileToMediaDB - mRecordMode : " + i);
        Log.m19d(TAG, "saveFileToMediaDB - getExtension : " + str2);
        Log.m19d(TAG, "saveFileToMediaDB - getMimeType : " + this.mAudioFormat.getMimeType());
        Log.m19d(TAG, "saveFileToMediaDB - getDuration : " + getInstance().getRecentFileDuration());
        String convertToSDCardReadOnlyPath = StorageProvider.convertToSDCardReadOnlyPath(file.getPath());
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", str3);
        contentValues.put("mime_type", this.mAudioFormat.getMimeType());
        contentValues.put("_data", convertToSDCardReadOnlyPath);
        contentValues.put("duration", Integer.valueOf(getInstance().getRecentFileDuration()));
        contentValues.put("_size", Long.valueOf(file.length()));
        contentValues.put("datetaken", Long.valueOf(file.lastModified()));
        contentValues.put("date_modified", Long.valueOf(file.lastModified() / 1000));
        contentValues.put("track", 0);
        contentValues.put("is_ringtone", 0);
        contentValues.put("is_alarm", 0);
        contentValues.put("is_notification", 0);
        contentValues.put("album", "Sounds");
        contentValues.put("is_drm", 0);
        contentValues.put("recordingtype", 1);
        contentValues.put("is_memo", 0);
        contentValues.put("recording_mode", Integer.valueOf(i));
        contentValues.put(DialogFactory.BUNDLE_LABEL_ID, Long.valueOf(j));
        if (AudioFormat.ExtType.EXT_3GA.equals(str2)) {
            contentValues.put("is_music", 0);
        }
        if (DBProvider.getInstance().insertDB(convertToSDCardReadOnlyPath, contentValues) != null) {
            Log.m29v(TAG, "saveFileToMediaDB - insertDB success : " + convertToSDCardReadOnlyPath);
            return;
        }
        Log.m22e(TAG, "saveFileToMediaDB - insert failed");
    }

    public void updateLastWord() {
        notifyObservers(Editor.INFO_EDITOR_STATE, 20, -1);
    }

    public void saveBookmarkBeforeTranslation() {
        Log.m26i(TAG, "saveBookmarkBeforeTranslation");
        MetadataRepository instance = MetadataRepository.getInstance();
        instance.write(instance.getPath());
    }

    public boolean isShowFilePlayingWhenSearch() {
        ArrayList<Long> currentSearchListIds;
        if (Player.getInstance().getID() != -1 && !CursorProvider.getInstance().getRecordingSearchTag().isEmpty() && (currentSearchListIds = CursorProvider.getInstance().getCurrentSearchListIds()) != null && !currentSearchListIds.isEmpty()) {
            for (int i = 0; i < currentSearchListIds.size(); i++) {
                if (currentSearchListIds.get(i).longValue() == Player.getInstance().getID()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isEndPlay() {
        Log.m26i(TAG, "isEndPlay - current time: " + getCurrentTime());
        return getCurrentTime() == getDuration();
    }

    public boolean isSupportBlockCall() {
        Context context = this.mAppContext;
        if (context == null) {
            Log.m22e(TAG, "isSupportBlockCall - mAppContext is NULL");
            return false;
        }
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
            if (telephonyManager == null || !telephonyManager.isVoiceCapable()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            Log.m24e(TAG, "isSupportBlockCall - has exception: ", (Throwable) e);
            return false;
        }
    }

    private void trimCompleteFile() {
        Log.m26i(TAG, "Editor.TRIM_COMPLETE");
        ContentItem popContentItem = popContentItem();
        deleteContentItemTempFile(popContentItem());
        pushContentItem(popContentItem);
        resetTrimTime();
        setEngineState(0);
        if (VoiceNoteApplication.getScene() == 12) {
            saveTranslationAfterTrim();
            clearContentItem();
            String path = Player.getInstance().getPath();
            this.mFileObserver = new FileEventObserver(path, 2564);
            this.mFileObserver.setPath(path);
            this.mFileObserver.startWatching();
        }
    }

    private void trimErrorFile() {
        Log.m26i(TAG, "Editor.TRIM_ERROR");
        resetTrimTime();
        popContentItem();
        setEngineState(0);
    }

    public void setPlayWithReceiver(boolean z) {
        Player.getInstance().setPlayWithReceiver(z);
    }

    public boolean isPlayWithReceiver() {
        return Player.getInstance().isPlayWithReceiver();
    }

    public void releasePreferredDevice() {
        Player.getInstance().releasePreferredDevice();
    }

    public boolean ismIsNeedReleaseMediaSession() {
        return this.mIsNeedReleaseMediaSession;
    }

    public void setmIsNeedReleaseMediaSession(boolean z) {
        this.mIsNeedReleaseMediaSession = z;
    }

    public void setStopPlayWithFadeOut(boolean z) {
        Player.getInstance().setStopPlayWithFadeOut(z);
    }

    public void setResumeRecordByCall(boolean z) {
        Recorder.getInstance().setResumeRecordByCall(z);
    }

    public boolean isResumeRecordByCall() {
        return Recorder.getInstance().isResumeRecordByCall();
    }

    private void showNotificationAlert() {
        Log.m19d(TAG, "showNotificationAlert - wired-headset bluetooth: " + isWiredHeadSetConnected() + " - " + isBluetoothSCOConnected());
        if (isWiredHeadSetConnected()) {
            String string = this.mAppContext.getString(C0690R.string.external_mic);
            Context context = this.mAppContext;
            Toast.makeText(context, context.getString(C0690R.string.recording_alert, new Object[]{string}), 1).show();
        } else if (!isBluetoothSCOConnected()) {
        } else {
            if (!isRecordForStereoOn() || getInstance().getRecorderState() != 3) {
                BluetoothDevice currentBtDevice = BluetoothHelper.getInstance().getCurrentBtDevice();
                if (currentBtDevice != null) {
                    String bluetoothName = BluetoothHelper.getInstance().getBluetoothName(currentBtDevice.getAddress());
                    if (bluetoothName == null) {
                        bluetoothName = this.mAppContext.getResources().getString(C0690R.string.external_mic);
                    }
                    Context context2 = this.mAppContext;
                    Toast.makeText(context2, context2.getString(C0690R.string.recording_alert, new Object[]{bluetoothName}), 1).show();
                    return;
                }
                return;
            }
            Log.m19d(TAG, "showNotificationAlert - record by bluetooth with StereoOn");
            String string2 = this.mAppContext.getString(C0690R.string.build_in_mic);
            Context context3 = this.mAppContext;
            Toast.makeText(context3, context3.getString(C0690R.string.recording_alert, new Object[]{string2}), 1).show();
        }
    }

    public boolean isRecordForStereoOn() {
        return Recorder.getInstance().isRecordForStereoOn();
    }

    public void setOpenedSpecialApp(boolean z) {
        this.isOpenedSpecialApp = z;
    }

    public boolean isOpenedSpecialApp() {
        return this.isOpenedSpecialApp;
    }
}
