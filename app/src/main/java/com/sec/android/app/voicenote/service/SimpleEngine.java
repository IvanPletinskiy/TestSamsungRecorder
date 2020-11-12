package com.sec.android.app.voicenote.service;

import android.content.ContentValues;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.FileObserver;
import android.os.Handler;
import android.widget.Toast;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.provider.CallRejectChecker;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.DBProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.PhoneStateProvider;
import com.sec.android.app.voicenote.provider.PrivateModeProvider;
import com.sec.android.app.voicenote.provider.SimpleStorageProvider;
import com.sec.android.app.voicenote.provider.StorageProvider;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.service.AudioFormat;
import com.sec.android.app.voicenote.service.SimpleEngineManager;
import com.sec.android.app.voicenote.service.SimplePlayer;
import com.sec.android.app.voicenote.service.SimpleRecorder;
import com.sec.android.app.voicenote.service.codec.M4aInfo;
import com.sec.android.app.voicenote.uicore.SimpleMediaSessionManager;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public class SimpleEngine implements SimplePlayer.OnPlayerListener, SimpleRecorder.OnRecorderListener {
    public static final int INFO_CURRENT_TIME = 101;
    public static final int INFO_ENGINEFOCUS_LOSS = 105;
    public static final int INFO_ENGINE_STATE = 100;
    public static final int INFO_REPEAT_TIME = 104;
    public static final int INFO_SAVED_ID = 103;
    public static final int INFO_TRIM_TIME = 102;
    /* access modifiers changed from: private */
    public static String TAG = "SimpleEngine";
    SimpleEngineManager.OnEngineFocusChangeListener engineFocusChangeListener = new SimpleEngineManager.OnEngineFocusChangeListener() {
        public void onEngineFocusChange(int i) {
            long j;
            if (i == -1) {
                Log.m23e(SimpleEngine.TAG, "ENGINEFOCUS_LOSS ", SimpleEngine.this.mSession);
                boolean z = SimpleEngine.this.mSimpleMetadata.getRecordMode() == 4;
                if (SimpleEngine.this.mOriginalFilePath != null) {
                    String name = new File(SimpleEngine.this.mOriginalFilePath).getName();
                    SimpleEngine.this.setUserSettingName(name.substring(0, name.lastIndexOf(46)));
                }
                if (SimpleEngine.this.getRecorderState() != 1) {
                    j = SimpleEngine.this.stopRecord(true, z);
                } else {
                    j = SimpleEngine.this.getSimpleModeItem();
                }
                if (j != -2) {
                    SimpleEngine.this.notifyObservers(105, -1, -1);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public Context mAppContext;
    private AudioFormat mAudioFormat = null;
    private long mCategoryID = 0;
    private final Stack<ContentItem> mContentItemStack = new Stack<>();
    private int mCurrentTime = 0;
    private FileEventObserver mFileObserver = null;
    private String mLastSavedFilePath = null;
    private final ArrayList<WeakReference<OnSimpleEngineListener>> mListeners = new ArrayList<>();
    /* access modifiers changed from: private */
    public String mOriginalFilePath = null;
    /* access modifiers changed from: private */
    public SimplePlayer mPayer;
    private SimpleRecorder mRecorder;
    private int mScene = 0;
    private boolean mScreenOff = false;
    /* access modifiers changed from: private */
    public String mSession;
    private boolean mShowToast = false;
    private int mSimpleEngineState = 0;
    /* access modifiers changed from: private */
    public SimpleMetadataRepository mSimpleMetadata;
    private long mSimpleModeItemId;
    private boolean mSimplePlayerMode = false;
    private boolean mSimpleRecorderMode = false;
    private String mUserSettingName = null;

    public interface OnSimpleEngineListener {
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
        public static final int NETWORK_NOT_CONNECTED = -106;
        public static final int NOT_ENOUGH_STORAGE = -107;

        /* renamed from: OK */
        public static final int f107OK = 0;
        public static final int OVERWRITE_FAIL = -117;
        public static final int PLAY_DURING_CALL = -103;
        public static final int PLAY_DURING_INCOMING_CALLS = -122;
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

    public static class SimpleEngineState {
        public static final int EDITING = 1;
        public static final int IDLE = 0;
        public static final int SAVING = 1;
    }

    public SimpleEngine(Context context, String str) {
        this.mAppContext = context;
        this.mRecorder = new SimpleRecorder(context, str);
        this.mPayer = new SimplePlayer(context, str);
        this.mRecorder.registerListener(this);
        this.mPayer.registerListener(this);
        this.mSession = str;
        this.mSimpleMetadata = SimpleMetadataRepositoryManager.getInstance().getMetadataRepository(this.mSession);
    }

    public boolean requestEngineFocus() {
        return SimpleEngineManager.getInstance().requestEngineFocus(this.mSession, this.engineFocusChangeListener) == 1;
    }

    public void onDestroy() {
        Log.m27i(TAG, "onDestroy ", this.mSession);
        this.mRecorder.unregisterListener(this);
        this.mRecorder.onDestroy();
        this.mPayer.unregisterListener(this);
        this.mPayer.onDestroy();
        unregisterAllListener();
    }

    private class FileEventObserver extends FileObserver {
        private final Handler mFileEventHandler = new Handler(SimpleEngine.this.mAppContext.getMainLooper());
        private final Runnable mFileEventRunnable = new Runnable() {
            public void run() {
                String path = SimpleEngine.this.mPayer.getPath();
                File file = new File(path);
                if (path.isEmpty() || FileEventObserver.this.mPath == null) {
                    Log.m29v(SimpleEngine.TAG, "path is empty");
                } else if (path.equals(FileEventObserver.this.mPath) && !file.exists()) {
                    Log.m29v(SimpleEngine.TAG, "run - stop play and close play scene");
                    SimpleEngine.this.stopPlay();
                    SimpleEngine.this.notifyObservers(2010, 5, -1);
                }
            }
        };
        /* access modifiers changed from: private */
        public String mPath = null;

        public FileEventObserver(String str, int i) {
            super(str, i);
        }

        public void onEvent(int i, String str) {
            Log.m26i(SimpleEngine.TAG, "onEvent - playing file is something changed");
            this.mFileEventHandler.post(this.mFileEventRunnable);
        }

        public void setPath(String str) {
            this.mPath = str;
        }
    }

    public void onPlayerUpdate(int i, int i2, int i3) {
        notifyObservers(i, i2, i3);
        if (i != 2010) {
            if (i == 2012) {
                this.mCurrentTime = i2;
            }
        } else if (i2 == 1) {
            SimpleMediaSessionManager.getInstance().updateMediaSessionState(0, 0, 0.0f, this.mSession);
        } else if (i2 == 3) {
            SimpleMediaSessionManager.getInstance().updateMediaSessionState(3, 0, 0.0f, this.mSession);
        } else if (i2 == 4) {
            SimpleMediaSessionManager.getInstance().updateMediaSessionState(2, 0, 0.0f, this.mSession);
        } else if (i2 == 5) {
            SimpleMediaSessionManager.getInstance().updateMediaSessionState(0, 0, 0.0f, this.mSession);
        }
    }

    public void onRecorderUpdate(int i, int i2, int i3) {
        notifyObservers(i, i2, i3);
        if (i != 1010) {
            if (i != 1011) {
                switch (i) {
                    case 1020:
                        String str = TAG;
                        Log.m23e(str, "onInfo - INFO_AUDIOFOCUS_LOSS : extra = " + i2, this.mSession);
                        if (i2 == 1006) {
                            Log.m20d(TAG, "INFO_AUDIOFOCUS_LOSS : cancel record", this.mSession);
                            return;
                        }
                        String str2 = this.mOriginalFilePath;
                        if (str2 != null) {
                            String name = new File(str2).getName();
                            setUserSettingName(name.substring(0, name.lastIndexOf(46)));
                        }
                        long stopRecord = stopRecord(true, true);
                        if (stopRecord != -2) {
                            notifyObservers(103, (int) stopRecord, -1);
                            return;
                        }
                        return;
                    case 1021:
                        String str3 = TAG;
                        Log.m23e(str3, "onInfo - INFO_MAX_DURATION_REACHED : extra = " + i2, this.mSession);
                        long stopRecord2 = stopRecord(true, true);
                        if (stopRecord2 != -2) {
                            notifyObservers(103, (int) stopRecord2, -1);
                            return;
                        }
                        return;
                    case 1022:
                        String str4 = TAG;
                        Log.m23e(str4, "onInfo - INFO_MAX_FILESIZE_REACHED : extra = " + i2, this.mSession);
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
            SimpleMediaSessionManager.getInstance().updateMediaSessionState(0, 0, 0.0f, this.mSession);
        } else if (i2 == 2) {
            this.mSimpleMetadata.rename((String) null, getRecentFilePath());
            SimpleMediaSessionManager.getInstance().updateMediaSessionState(3, 0, 0.0f, this.mSession);
        } else if (i2 == 3) {
            SimpleMediaSessionManager.getInstance().updateMediaSessionState(2, 0, 0.0f, this.mSession);
        } else if (i2 == 4) {
            SimpleMediaSessionManager.getInstance().updateMediaSessionState(0, 0, 0.0f, this.mSession);
        }
    }

    public void setSimpleRecorderMode(boolean z) {
        this.mSimpleRecorderMode = z;
    }

    public boolean isSimpleRecorderMode() {
        return this.mSimpleRecorderMode;
    }

    public final void registerListener(OnSimpleEngineListener onSimpleEngineListener) {
        if (onSimpleEngineListener != null && !containsListener(onSimpleEngineListener)) {
            this.mListeners.add(new WeakReference(onSimpleEngineListener));
            if (this.mPayer.getPlayerState() != 1) {
                onSimpleEngineListener.onEngineUpdate(2010, this.mPayer.getPlayerState(), -1);
                onSimpleEngineListener.onEngineUpdate(2012, this.mCurrentTime, -1);
            } else if (this.mRecorder.getRecorderState() != 1) {
                onSimpleEngineListener.onEngineUpdate(1010, this.mRecorder.getRecorderState(), -1);
                onSimpleEngineListener.onEngineUpdate(1011, this.mCurrentTime, -1);
            }
        }
    }

    public final void unregisterListener(OnSimpleEngineListener onSimpleEngineListener) {
        if (onSimpleEngineListener != null && containsListener(onSimpleEngineListener)) {
            removeListener(onSimpleEngineListener);
        }
    }

    private boolean containsListener(OnSimpleEngineListener onSimpleEngineListener) {
        ArrayList<WeakReference<OnSimpleEngineListener>> arrayList = this.mListeners;
        if (!(arrayList == null || onSimpleEngineListener == null)) {
            Iterator<WeakReference<OnSimpleEngineListener>> it = arrayList.iterator();
            while (it.hasNext()) {
                if (onSimpleEngineListener.equals(it.next().get())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void removeListener(OnSimpleEngineListener onSimpleEngineListener) {
        ArrayList<WeakReference<OnSimpleEngineListener>> arrayList = this.mListeners;
        if (arrayList != null && onSimpleEngineListener != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                WeakReference weakReference = this.mListeners.get(size);
                if (weakReference.get() == null || ((OnSimpleEngineListener) weakReference.get()).equals(onSimpleEngineListener)) {
                    this.mListeners.remove(weakReference);
                }
            }
        }
    }

    public void setAudioFormat(AudioFormat audioFormat) {
        this.mAudioFormat = audioFormat;
        this.mRecorder.setAudioFormat(audioFormat);
    }

    public AudioFormat getAudioFormat() {
        return this.mAudioFormat;
    }

    public void setUserSettingName(String str) {
        this.mUserSettingName = str;
        String str2 = TAG;
        Log.m26i(str2, "setUserSettingName - name : " + this.mUserSettingName);
    }

    public void setCategoryID(long j) {
        this.mCategoryID = j;
        String str = TAG;
        Log.m26i(str, "setCategoryID - id : " + j);
    }

    public String getUserSettingName() {
        String str = TAG;
        Log.m26i(str, "getUserSettingName - name : " + this.mUserSettingName);
        return this.mUserSettingName;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0085, code lost:
        return r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int startRecord(com.sec.android.app.voicenote.service.AudioFormat r5) {
        /*
            r4 = this;
            monitor-enter(r4)
            java.lang.String r0 = TAG     // Catch:{ all -> 0x008a }
            java.lang.String r1 = "startRecord"
            java.lang.String r2 = r4.mSession     // Catch:{ all -> 0x008a }
            com.sec.android.app.voicenote.provider.Log.m27i((java.lang.String) r0, (java.lang.String) r1, (java.lang.String) r2)     // Catch:{ all -> 0x008a }
            int r0 = r4.getSimpleEngineState()     // Catch:{ all -> 0x008a }
            if (r0 == 0) goto L_0x0014
            r5 = -119(0xffffffffffffff89, float:NaN)
            monitor-exit(r4)
            return r5
        L_0x0014:
            com.sec.android.app.voicenote.service.SimpleRecorder r0 = r4.mRecorder     // Catch:{ all -> 0x008a }
            int r0 = r0.getRecorderState()     // Catch:{ all -> 0x008a }
            r1 = 1
            if (r0 == r1) goto L_0x002a
            java.lang.String r5 = TAG     // Catch:{ all -> 0x008a }
            java.lang.String r0 = "startRecord - it is already recording state"
            java.lang.String r1 = r4.mSession     // Catch:{ all -> 0x008a }
            com.sec.android.app.voicenote.provider.Log.m33w((java.lang.String) r5, (java.lang.String) r0, (java.lang.String) r1)     // Catch:{ all -> 0x008a }
            r5 = -108(0xffffffffffffff94, float:NaN)
            monitor-exit(r4)
            return r5
        L_0x002a:
            com.sec.android.app.voicenote.provider.PhoneStateProvider r0 = com.sec.android.app.voicenote.provider.PhoneStateProvider.getInstance()     // Catch:{ all -> 0x008a }
            android.content.Context r1 = r4.mAppContext     // Catch:{ all -> 0x008a }
            boolean r0 = r0.isCallIdle(r1)     // Catch:{ all -> 0x008a }
            if (r0 == 0) goto L_0x0086
            com.sec.android.app.voicenote.service.SimpleMetadataRepository r0 = r4.mSimpleMetadata     // Catch:{ all -> 0x008a }
            int r0 = r0.getRecordMode()     // Catch:{ all -> 0x008a }
            r1 = 4
            if (r0 != r1) goto L_0x004b
            android.content.Context r2 = r4.mAppContext     // Catch:{ all -> 0x008a }
            boolean r2 = com.sec.android.app.voicenote.provider.Network.isNetworkConnected(r2)     // Catch:{ all -> 0x008a }
            if (r2 != 0) goto L_0x004b
            r5 = -106(0xffffffffffffff96, float:NaN)
            monitor-exit(r4)
            return r5
        L_0x004b:
            if (r0 != r1) goto L_0x0053
            r1 = 2
            r4.setCategoryID(r1)     // Catch:{ all -> 0x008a }
            goto L_0x0058
        L_0x0053:
            r1 = 0
            r4.setCategoryID(r1)     // Catch:{ all -> 0x008a }
        L_0x0058:
            if (r5 != 0) goto L_0x005f
            com.sec.android.app.voicenote.service.AudioFormat r5 = new com.sec.android.app.voicenote.service.AudioFormat     // Catch:{ all -> 0x008a }
            r5.<init>(r0)     // Catch:{ all -> 0x008a }
        L_0x005f:
            r4.mAudioFormat = r5     // Catch:{ all -> 0x008a }
            r0 = 0
            r4.mLastSavedFilePath = r0     // Catch:{ all -> 0x008a }
            com.sec.android.app.voicenote.service.AudioFormat r0 = r4.mAudioFormat     // Catch:{ all -> 0x008a }
            java.lang.String r0 = r0.getExtension()     // Catch:{ all -> 0x008a }
            java.lang.String r0 = com.sec.android.app.voicenote.provider.SimpleStorageProvider.createTempFile(r0)     // Catch:{ all -> 0x008a }
            com.sec.android.app.voicenote.service.ContentItem r1 = new com.sec.android.app.voicenote.service.ContentItem     // Catch:{ all -> 0x008a }
            int r2 = r4.mCurrentTime     // Catch:{ all -> 0x008a }
            r3 = 0
            r1.<init>(r0, r2, r3)     // Catch:{ all -> 0x008a }
            r4.pushContentItem(r1)     // Catch:{ all -> 0x008a }
            com.sec.android.app.voicenote.service.SimpleRecorder r1 = r4.mRecorder     // Catch:{ all -> 0x008a }
            int r5 = r1.startRecord(r0, r5)     // Catch:{ all -> 0x008a }
            if (r5 == 0) goto L_0x0084
            r4.popContentItem()     // Catch:{ all -> 0x008a }
        L_0x0084:
            monitor-exit(r4)
            return r5
        L_0x0086:
            r5 = -102(0xffffffffffffff9a, float:NaN)
            monitor-exit(r4)
            return r5
        L_0x008a:
            r5 = move-exception
            monitor-exit(r4)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.SimpleEngine.startRecord(com.sec.android.app.voicenote.service.AudioFormat):int");
    }

    public synchronized boolean pauseRecord() {
        Log.m27i(TAG, "pauseRecord", this.mSession);
        if (!isSaveEnable()) {
            Log.m27i(TAG, "pauseRecord can not pause", this.mSession);
            return false;
        }
        return this.mRecorder.pauseRecord();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00be, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int resumeRecord() {
        /*
            r6 = this;
            monitor-enter(r6)
            java.lang.String r0 = TAG     // Catch:{ all -> 0x00bf }
            java.lang.String r1 = "resumeRecord"
            java.lang.String r2 = r6.mSession     // Catch:{ all -> 0x00bf }
            com.sec.android.app.voicenote.provider.Log.m27i((java.lang.String) r0, (java.lang.String) r1, (java.lang.String) r2)     // Catch:{ all -> 0x00bf }
            int r0 = r6.getSimpleEngineState()     // Catch:{ all -> 0x00bf }
            if (r0 == 0) goto L_0x0014
            r0 = -119(0xffffffffffffff89, float:NaN)
            monitor-exit(r6)
            return r0
        L_0x0014:
            com.sec.android.app.voicenote.provider.PhoneStateProvider r0 = com.sec.android.app.voicenote.provider.PhoneStateProvider.getInstance()     // Catch:{ all -> 0x00bf }
            android.content.Context r1 = r6.mAppContext     // Catch:{ all -> 0x00bf }
            boolean r0 = r0.isCallIdle(r1)     // Catch:{ all -> 0x00bf }
            if (r0 == 0) goto L_0x00bb
            com.sec.android.app.voicenote.service.SimpleMetadataRepository r0 = r6.mSimpleMetadata     // Catch:{ all -> 0x00bf }
            int r0 = r0.getRecordMode()     // Catch:{ all -> 0x00bf }
            com.sec.android.app.voicenote.service.SimpleEngineManager r1 = com.sec.android.app.voicenote.service.SimpleEngineManager.getInstance()     // Catch:{ all -> 0x00bf }
            boolean r1 = r1.isWiredHeadSetConnected()     // Catch:{ all -> 0x00bf }
            r2 = 1
            r3 = 4
            if (r1 == 0) goto L_0x004c
            r1 = 2
            if (r0 == r1) goto L_0x0048
            if (r0 == r3) goto L_0x0044
            android.content.Context r1 = r6.mAppContext     // Catch:{ all -> 0x00bf }
            r4 = 2131755439(0x7f1001af, float:1.9141757E38)
            android.widget.Toast r1 = android.widget.Toast.makeText(r1, r4, r2)     // Catch:{ all -> 0x00bf }
            r1.show()     // Catch:{ all -> 0x00bf }
            goto L_0x004c
        L_0x0044:
            r0 = -105(0xffffffffffffff97, float:NaN)
            monitor-exit(r6)
            return r0
        L_0x0048:
            r0 = -104(0xffffffffffffff98, float:NaN)
            monitor-exit(r6)
            return r0
        L_0x004c:
            if (r0 != r3) goto L_0x005a
            android.content.Context r1 = r6.mAppContext     // Catch:{ all -> 0x00bf }
            boolean r1 = com.sec.android.app.voicenote.provider.Network.isNetworkConnected(r1)     // Catch:{ all -> 0x00bf }
            if (r1 != 0) goto L_0x005a
            r0 = -106(0xffffffffffffff96, float:NaN)
            monitor-exit(r6)
            return r0
        L_0x005a:
            java.lang.String r1 = r6.getRecentFilePath()     // Catch:{ all -> 0x00bf }
            com.sec.android.app.voicenote.service.SimplePlayer r4 = r6.mPayer     // Catch:{ all -> 0x00bf }
            r4.stopPlay()     // Catch:{ all -> 0x00bf }
            com.sec.android.app.voicenote.service.SimpleMetadataRepository r4 = r6.mSimpleMetadata     // Catch:{ all -> 0x00bf }
            java.lang.String r4 = r4.getPath()     // Catch:{ all -> 0x00bf }
            if (r4 == 0) goto L_0x0071
            boolean r4 = r4.isEmpty()     // Catch:{ all -> 0x00bf }
            if (r4 == 0) goto L_0x0083
        L_0x0071:
            com.sec.android.app.voicenote.service.SimpleMetadataRepository r4 = r6.mSimpleMetadata     // Catch:{ all -> 0x00bf }
            r4.read(r1)     // Catch:{ all -> 0x00bf }
            com.sec.android.app.voicenote.service.SimpleMetadataRepository r4 = r6.mSimpleMetadata     // Catch:{ all -> 0x00bf }
            com.sec.android.app.voicenote.provider.DBProvider r5 = com.sec.android.app.voicenote.provider.DBProvider.getInstance()     // Catch:{ all -> 0x00bf }
            int r5 = r5.getRecordModeByPath(r1)     // Catch:{ all -> 0x00bf }
            r4.setRecordMode(r5)     // Catch:{ all -> 0x00bf }
        L_0x0083:
            com.sec.android.app.voicenote.service.SimpleRecorder r4 = r6.mRecorder     // Catch:{ all -> 0x00bf }
            int r4 = r4.getRecorderState()     // Catch:{ all -> 0x00bf }
            if (r4 == r3) goto L_0x008d
            if (r4 != r2) goto L_0x00ad
        L_0x008d:
            com.sec.android.app.voicenote.service.AudioFormat r2 = r6.mAudioFormat     // Catch:{ all -> 0x00bf }
            if (r2 != 0) goto L_0x0098
            com.sec.android.app.voicenote.service.AudioFormat r2 = new com.sec.android.app.voicenote.service.AudioFormat     // Catch:{ all -> 0x00bf }
            r2.<init>(r0)     // Catch:{ all -> 0x00bf }
            r6.mAudioFormat = r2     // Catch:{ all -> 0x00bf }
        L_0x0098:
            com.sec.android.app.voicenote.service.AudioFormat r0 = r6.mAudioFormat     // Catch:{ all -> 0x00bf }
            java.lang.String r0 = r0.getExtension()     // Catch:{ all -> 0x00bf }
            java.lang.String r1 = com.sec.android.app.voicenote.provider.SimpleStorageProvider.createTempFile(r1, r0)     // Catch:{ all -> 0x00bf }
            com.sec.android.app.voicenote.service.ContentItem r0 = new com.sec.android.app.voicenote.service.ContentItem     // Catch:{ all -> 0x00bf }
            int r2 = r6.mCurrentTime     // Catch:{ all -> 0x00bf }
            r3 = 0
            r0.<init>(r1, r2, r3)     // Catch:{ all -> 0x00bf }
            r6.pushContentItem(r0)     // Catch:{ all -> 0x00bf }
        L_0x00ad:
            com.sec.android.app.voicenote.service.SimpleRecorder r0 = r6.mRecorder     // Catch:{ all -> 0x00bf }
            int r2 = r6.mCurrentTime     // Catch:{ all -> 0x00bf }
            int r0 = r0.resumeRecord(r1, r2)     // Catch:{ all -> 0x00bf }
            if (r0 == 0) goto L_0x00bd
            r6.popContentItem()     // Catch:{ all -> 0x00bf }
            goto L_0x00bd
        L_0x00bb:
            r0 = -102(0xffffffffffffff9a, float:NaN)
        L_0x00bd:
            monitor-exit(r6)
            return r0
        L_0x00bf:
            r0 = move-exception
            monitor-exit(r6)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.SimpleEngine.resumeRecord():int");
    }

    public int getRecorderState() {
        return this.mRecorder.getRecorderState();
    }

    public long stopRecord(boolean z, boolean z2) {
        String str = TAG;
        Log.m27i(str, "stopRecord - newName : " + z + " showToast : " + z2, this.mSession);
        if (getSimpleEngineState() != 0) {
            return -119;
        }
        if (this.mRecorder.getRecorderState() == 1 || isSaveEnable()) {
            setSimpleEngineState(1);
            CallRejectChecker.getInstance().setReject(false);
            this.mRecorder.initPhoneStateListener();
            this.mShowToast = z2;
            if (this.mRecorder.getRecorderState() == 2 || this.mRecorder.getRecorderState() == 3) {
                ContentItem peekContentItem = peekContentItem();
                if (peekContentItem == null) {
                    if (this.mAudioFormat == null) {
                        this.mAudioFormat = new AudioFormat(this.mRecorder.getRecordMode());
                    }
                    peekContentItem = new ContentItem(SimpleStorageProvider.createTempFile(this.mAudioFormat.getExtension()));
                    pushContentItem(peekContentItem);
                }
                if (!this.mRecorder.saveFile(peekContentItem)) {
                    popContentItem();
                }
            }
            this.mRecorder.stopSTT();
            long saveFile = saveFile(z);
            if (saveFile < 0) {
                Toast.makeText(this.mAppContext, C0690R.string.recording_failed, 1).show();
                notifyObservers(103, (int) saveFile, -1);
            }
            setSimpleEngineState(0);
            return saveFile;
        }
        Log.m27i(TAG, "Can not stopRecord", this.mSession);
        return -119;
    }

    private long saveFile(boolean z) {
        int i;
        long j;
//        File file;
        File file = null;
        long j2;
        String str;
        String str2;
        String str3;
        boolean z2 = z;
        boolean z3 = this.mShowToast;
        if (getContentItemCount() < 1) {
            Log.m22e(TAG, "saveFile fail ");
            return -1;
        }
        String recentFilePath = getRecentFilePath();
        File file2 = new File(recentFilePath);
        if (!file2.exists() || file2.isDirectory()) {
            Log.m23e(TAG, "Abnormal hiddenPath - path : " + recentFilePath, this.mSession);
            return -1;
        }
        Log.m27i(TAG, "saveFile - saveNewFile : " + z2, this.mSession);
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(recentFilePath);
        String extractMetadata = mediaMetadataRetriever.extractMetadata(9);
        mediaMetadataRetriever.release();
        long parseLong = extractMetadata != null ? Long.parseLong(extractMetadata) : 0;
        int recordMode = this.mSimpleMetadata.getRecordMode();
        if (recordMode == 5 || recordMode == 6) {
            recordMode = 1;
        }
        if (M4aInfo.isM4A(recentFilePath)) {
            this.mSimpleMetadata.write(recentFilePath);
            this.mSimpleMetadata.close();
        }
        String extension = this.mAudioFormat.getExtension();
        String str4 = this.mOriginalFilePath;
        if (str4 != null && !str4.endsWith(extension)) {
            int length = this.mOriginalFilePath.length();
            extension = this.mOriginalFilePath.substring(length - 4, length);
        }
        Log.m26i(TAG, "saveFile - mRecordMode : " + recordMode);
        Log.m26i(TAG, "saveFile - getExtension : " + extension);
        Log.m26i(TAG, "saveFile - mOriginalFilePath : " + this.mOriginalFilePath);
        long j3 = this.mCategoryID;
        setCategoryID(0);
        boolean z4 = z3;
        if (z2 || (str3 = this.mOriginalFilePath) == null) {
            j = j3;
            Log.m26i(TAG, "saveFile save to new file !!!");
            String str5 = this.mUserSettingName;
            if (str5 == null || str5.isEmpty()) {
                str = DBProvider.getInstance().createNewSimpleFileName(recordMode);
            } else {
                str = this.mUserSettingName;
                setUserSettingName((String) null);
            }
            if (this.mOriginalFilePath != null) {
                StringBuilder sb = new StringBuilder();
                i = recordMode;
                String str6 = this.mOriginalFilePath;
                sb.append(str6.substring(0, str6.lastIndexOf(47)));
                sb.append('/');
                sb.append(str);
                sb.append(extension);
                str2 = sb.toString();
            } else {
                i = recordMode;
                if (!PrivateModeProvider.isPrivateBoxMode() || !PrivateModeProvider.isPrivateMode()) {
                    str2 = SimpleStorageProvider.getVoiceRecorderPath() + '/' + str + extension;
                } else {
                    str2 = PrivateModeProvider.getPrivateStorageRoot(this.mAppContext) + '/' + str + extension;
                }
            }
            Log.m20d(TAG, "saveFile - newFilePath : " + str2, this.mSession);
            File file3 = new File(str2);
            if (!SimpleStorageProvider.isExistFile(file2)) {
                Log.m23e(TAG, "cancel recording while save by Hidden file doesn't exist", this.mSession);
                cancelRecord();
                return -1;
            }
            if (SimpleStorageProvider.isExistFile(file3)) {
                Log.m23e(TAG, "rename saving file while save by saving file already exist", this.mSession);
                str2 = DBProvider.getInstance().createNewSimpleFilePath(str2);
                Log.m26i(TAG, "saveFile - new newFilePath : " + str2);
                file3 = new File(str2);
                if (SimpleStorageProvider.isExistFile(file3)) {
                    Log.m20d(TAG, "saveFile - newFilePath : " + str2, this.mSession);
                    if (!file3.delete()) {
                        Log.m23e(TAG, "Fail to delete saveFile !!", this.mSession);
                    }
                }
            }
            boolean rename = PrivateModeProvider.rename(this.mAppContext, recentFilePath, str2);
            Log.m19d(TAG, "move result : " + rename);
            if (!rename) {
                Log.m22e(TAG, "cancel recording while save by can not rename file");
                cancelRecord();
                return -1;
            }
        } else {
            if (!recentFilePath.equals(str3)) {
                j = j3;
                Log.m26i(TAG, "saveFile save to new file2 !!!");
                file = new File(this.mOriginalFilePath);
                setUserSettingName((String) null);
                if (!SimpleStorageProvider.isExistFile(file2)) {
                    Log.m22e(TAG, "cancel recording while save by Hidden file doesn't exist");
                    cancelRecord();
                    return -1;
                }
                if (SimpleStorageProvider.isExistFile(file)) {
                    Log.m22e(TAG, "rename saving file while save by saving file already exist");
                    if (!file.delete()) {
                        Log.m22e(TAG, "Fail to delete saveFile !!");
                        return -1;
                    }
                }
                boolean rename2 = PrivateModeProvider.rename(this.mAppContext, recentFilePath, this.mOriginalFilePath);
                Log.m19d(TAG, "move result : " + rename2);
                if (!rename2) {
                    Log.m22e(TAG, "cancel recording while save by can not rename file");
                    cancelRecord();
                }
            } else {
                j = j3;
                Log.m26i(TAG, "saveFile save to original file !!!");
                file = new File(recentFilePath);
                setUserSettingName((String) null);
            }
            i = recordMode;
        }
        String substring = file.getName().substring(0, file.getName().lastIndexOf(46));
        this.mLastSavedFilePath = file.getPath();
        Log.m26i(TAG, "saveFile name : " + substring);
        String convertToSDCardReadOnlyPath = StorageProvider.convertToSDCardReadOnlyPath(file.getPath());
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", substring);
        contentValues.put("mime_type", this.mAudioFormat.getMimeType());
        contentValues.put("_data", convertToSDCardReadOnlyPath);
        contentValues.put("duration", Long.valueOf(parseLong));
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
        contentValues.put(DialogFactory.BUNDLE_LABEL_ID, Long.valueOf(j));
        if (AudioFormat.ExtType.EXT_3GA.equals(extension)) {
            contentValues.put("is_music", 0);
        }
        if (z || this.mOriginalFilePath == null) {
            Uri insertDB = DBProvider.getInstance().insertDB(convertToSDCardReadOnlyPath, contentValues);
            if (insertDB != null) {
                Log.m29v(TAG, "insertDB success : " + convertToSDCardReadOnlyPath);
                long idByPath = DBProvider.getInstance().getIdByPath(convertToSDCardReadOnlyPath);
                SurveyLogProvider.insertRecordingLog(insertDB.toString(), (int) parseLong);
                j2 = idByPath;
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
        SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_SAVE, -1);
        this.mRecorder.cancelRecord();
        this.mPayer.stopPlay(false);
        String str7 = TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("saveFile : showToast : ");
        boolean z5 = z4;
        sb2.append(z5);
        Log.m26i(str7, sb2.toString());
        if (z5) {
            Context context = this.mAppContext;
            Toast.makeText(context, context.getString(C0690R.string.filename_has_been_saved, new Object[]{substring}), 0).show();
        }
        if (VoiceNoteFeature.isGateEnabled()) {
            android.util.Log.i("GATE", "<GATE-M> AUDIO_RECORDED: " + file.getPath() + " </GATE-M>");
        }
        return j2;
    }

    public synchronized boolean cancelRecord() {
        Log.m27i(TAG, "cancelRecord", this.mSession);
        setUserSettingName((String) null);
        setOriginalFilePath((String) null);
        CallRejectChecker.getInstance().setReject(false);
        this.mRecorder.initPhoneStateListener();
        return this.mRecorder.cancelRecord();
    }

    public void setCurrentTime(int i) {
        String str = TAG;
        Log.m27i(str, "setCurrentTime - time : " + i, this.mSession);
        this.mCurrentTime = i;
        this.mRecorder.setCurrentTime(i);
        if (this.mPayer.getPlayerState() != 1) {
            this.mPayer.seekTo(i);
        }
    }

    public void setCurrentTime(int i, boolean z) {
        String str = TAG;
        Log.m27i(str, " setCurrentTime - time : " + i, this.mSession);
        this.mCurrentTime = i;
        if (z) {
            notifyObservers(101, i, -1);
        }
    }

    public void setSimpleModeItem(long j) {
        this.mSimpleModeItemId = j;
    }

    public long getSimpleModeItem() {
        return this.mSimpleModeItemId;
    }

    public boolean isSaveEnable() {
        return this.mRecorder.isSaveEnable();
    }

    public void setOriginalFilePath(String str) {
        this.mOriginalFilePath = str;
        String str2 = TAG;
        Log.m19d(str2, "setOriginalFilePath - path : " + this.mOriginalFilePath);
    }

    public String getOriginalFilePath() {
        String str = TAG;
        Log.m19d(str, "getOriginalFilePath - path : " + this.mOriginalFilePath);
        return this.mOriginalFilePath;
    }

    public void setScreenOff(boolean z) {
        this.mScreenOff = z;
    }

    public boolean getScreenOff() {
        return this.mScreenOff;
    }

    public void setSimpleEngineState(int i) {
        String str = TAG;
        Log.m26i(str, "setEngineState - state : " + i);
        this.mSimpleEngineState = i;
    }

    public int getSimpleEngineState() {
        String str = TAG;
        Log.m26i(str, "getEngineState - state : " + this.mSimpleEngineState);
        return this.mSimpleEngineState;
    }

    public String getLastSavedFilePath() {
        return this.mLastSavedFilePath;
    }

    public int startPlay(String str) {
        return initPlay(str, true);
    }

    public int initPlay(String str, boolean z) {
        return initPlay(str, DBProvider.getInstance().getIdByPath(str), z);
    }

    public int startPlay(long j) {
        return initPlay(j, true);
    }

    public int initPlay(long j, boolean z) {
        return initPlay(DBProvider.getInstance().getPathById(j), j, z);
    }

    public int initPlay(String str, long j, boolean z) {
        String str2 = TAG;
        Log.m27i(str2, "initPlay - id : " + j + " play : " + z, this.mSession);
        if (getSimpleEngineState() != 0) {
            return -119;
        }
        if (str == null || str.isEmpty()) {
            String str3 = TAG;
            Log.m23e(str3, "initPlay - path is abnormal : " + str, this.mSession);
            return -115;
        } else if (!PhoneStateProvider.getInstance().isCallIdle(this.mAppContext)) {
            return -103;
        } else {
            if (getContentItemCount() == 0) {
                File file = new File(str);
                if (!file.exists() || file.isDirectory()) {
                    String str4 = TAG;
                    Log.m22e(str4, "initPlay file is not exist or directory - path : " + file.getPath());
                    return -115;
                }
                AudioFormat audioFormat = new AudioFormat(CursorProvider.getInstance().getRecordMode(j));
                audioFormat.setMimeType(CursorProvider.getInstance().getMimeType(j));
                setOriginalFilePath(str);
                String str5 = TAG;
                Log.m26i(str5, "initPlay set new AudioFormat extension : " + audioFormat.getExtension());
                setAudioFormat(audioFormat);
                pushContentItem(new ContentItem(str, 0, (int) CursorProvider.getInstance().getDuration(j)));
            }
            if (!this.mPayer.initPlay(str, j, z)) {
                return -115;
            }
            this.mFileObserver = new FileEventObserver(str, 2564);
            this.mFileObserver.setPath(str);
            this.mFileObserver.startWatching();
            if (VoiceNoteFeature.isGateEnabled()) {
                android.util.Log.i("GATE", "<GATE-M> AUDIO_PLAYING : " + str + " </GATE-M>");
            }
            return 0;
        }
    }

    public boolean initPlay() {
        Log.m27i(TAG, "stopPlay", this.mSession);
        FileEventObserver fileEventObserver = this.mFileObserver;
        if (fileEventObserver != null) {
            fileEventObserver.stopWatching();
        }
        return this.mPayer.initPlay();
    }

    public boolean stopPlay() {
        Log.m27i(TAG, "stopPlay", this.mSession);
        FileEventObserver fileEventObserver = this.mFileObserver;
        if (fileEventObserver != null) {
            fileEventObserver.stopWatching();
        }
        return this.mPayer.stopPlay();
    }

    public boolean stopPlay(boolean z) {
        String str = TAG;
        Log.m27i(str, "stopPlay - updateMetadata : " + z, this.mSession);
        FileEventObserver fileEventObserver = this.mFileObserver;
        if (fileEventObserver != null) {
            fileEventObserver.stopWatching();
        }
        return this.mPayer.stopPlay(z);
    }

    public boolean pausePlay() {
        Log.m27i(TAG, "pausePlay", this.mSession);
        return this.mPayer.pausePlay();
    }

    public int resumePlay() {
        return resumePlay(true);
    }

    public int resumePlay(boolean z) {
        String str = TAG;
        Log.m27i(str, "resumePlay - mCurrentTime : " + this.mCurrentTime, this.mSession);
        if (getSimpleEngineState() != 0) {
            return -119;
        }
        if (getContentItemCount() == 0) {
            Log.m22e(TAG, "resumePlay No files exist");
            return -115;
        } else if (!PhoneStateProvider.getInstance().isCallIdle(this.mAppContext)) {
            return -103;
        } else {
            int duration = getDuration();
            String path = this.mPayer.getPath();
            String recentFilePath = getRecentFilePath();
            if (recentFilePath.equals(path)) {
                String str2 = TAG;
                Log.m27i(str2, "resumePlay - resumePlay duration : " + duration, this.mSession);
                int i = this.mCurrentTime;
                if (i + 100 <= duration) {
                    seekTo(i);
                } else if (i + 100 > duration) {
                    setCurrentTime(0);
                }
                return this.mPayer.resumePlay();
            }
            Log.m27i(TAG, "resumePlay - startPlay", this.mSession);
            int initPlay = initPlay(recentFilePath, z);
            int i2 = this.mCurrentTime;
            if (i2 + 100 > duration) {
                return initPlay;
            }
            seekTo(i2);
            return initPlay;
        }
    }

    public void seekTo(int i) {
        this.mPayer.seekTo(i);
    }

    public int getDuration() {
        if (this.mPayer.getPlayerState() != 1) {
            return this.mPayer.getDuration();
        }
        int recentFileDuration = getRecentFileDuration();
        int i = this.mCurrentTime;
        if (recentFileDuration > i) {
            i = getRecentFileDuration();
        }
        return getMaxDuration(i);
    }

    public void skipInterval(int i) {
        String str = TAG;
        Log.m27i(str, "skipInterval : " + i, this.mSession);
        this.mPayer.skipInterval(i);
    }

    public int getPlayerState() {
        return this.mPayer.getPlayerState();
    }

    public int getCurrentTime() {
        return this.mCurrentTime;
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

    public int getRecentFileDuration() {
        if (this.mContentItemStack.size() < 1) {
            return 0;
        }
        int duration = this.mContentItemStack.peek().getDuration();
        String str = TAG;
        Log.m26i(str, "getRecentFileDuration duration : " + duration);
        return duration;
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
                        ((OnSimpleEngineListener) weakReference.get()).onEngineUpdate(i, i2, i3);
                    }
                    size--;
                } else {
                    return;
                }
            } catch (IndexOutOfBoundsException e) {
                Log.m25e(TAG, "IndexOutOfBoundsException !", e, this.mSession);
            } catch (NullPointerException e2) {
                Log.m25e(TAG, "NullPointerException !", e2, this.mSession);
            }
        }
    }

    public synchronized int getContentItemCount() {
        String str = TAG;
        Log.m26i(str, "getContentItemCount size : " + this.mContentItemStack.size());
        return this.mContentItemStack.size();
    }

    private synchronized void pushContentItem(ContentItem contentItem) {
        if (contentItem != null) {
            String str = TAG;
            Log.m19d(str, "pushContentItem sTime : " + contentItem.getStartTime() + " eTime : " + contentItem.getEndTime() + " dTime : " + contentItem.getDuration() + " path : " + contentItem.getPath() + " stack size : " + this.mContentItemStack.size());
            this.mContentItemStack.push(contentItem);
        }
    }

    private synchronized ContentItem popContentItem() {
        if (getContentItemCount() <= 0) {
            return null;
        }
        ContentItem pop = this.mContentItemStack.pop();
        String str = TAG;
        Log.m19d(str, "popContentItem sTime : " + pop.getStartTime() + " eTime : " + pop.getEndTime() + " dTime : " + pop.getDuration() + " path : " + pop.getPath() + " stack size : " + this.mContentItemStack.size());
        return pop;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(3:19|20|32) */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        com.sec.android.app.voicenote.provider.Log.m22e(TAG, "clearContentItem() mPath is null");
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x008f */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void clearContentItem() {
        /*
            r5 = this;
            monitor-enter(r5)
            java.lang.String r0 = TAG     // Catch:{ all -> 0x009e }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x009e }
            r1.<init>()     // Catch:{ all -> 0x009e }
            java.lang.String r2 = "clearContentItem - size : "
            r1.append(r2)     // Catch:{ all -> 0x009e }
            java.util.Stack<com.sec.android.app.voicenote.service.ContentItem> r2 = r5.mContentItemStack     // Catch:{ all -> 0x009e }
            int r2 = r2.size()     // Catch:{ all -> 0x009e }
            r1.append(r2)     // Catch:{ all -> 0x009e }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x009e }
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r1)     // Catch:{ all -> 0x009e }
            android.content.Context r0 = r5.mAppContext     // Catch:{ all -> 0x009e }
            boolean r0 = com.sec.android.app.voicenote.provider.PermissionProvider.checkSavingEnable(r0)     // Catch:{ all -> 0x009e }
            if (r0 != 0) goto L_0x002e
            java.lang.String r0 = TAG     // Catch:{ all -> 0x009e }
            java.lang.String r1 = "cancel clearContentItem. Permission error"
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r1)     // Catch:{ all -> 0x009e }
            monitor-exit(r5)
            return
        L_0x002e:
            java.util.Stack<com.sec.android.app.voicenote.service.ContentItem> r0 = r5.mContentItemStack     // Catch:{ all -> 0x009e }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x009e }
        L_0x0034:
            boolean r1 = r0.hasNext()     // Catch:{ all -> 0x009e }
            if (r1 == 0) goto L_0x0097
            java.lang.Object r1 = r0.next()     // Catch:{ all -> 0x009e }
            com.sec.android.app.voicenote.service.ContentItem r1 = (com.sec.android.app.voicenote.service.ContentItem) r1     // Catch:{ all -> 0x009e }
            java.lang.String r2 = r1.getPath()     // Catch:{ NullPointerException -> 0x008f }
            boolean r2 = com.sec.android.app.voicenote.provider.SimpleStorageProvider.isTempFile(r2)     // Catch:{ NullPointerException -> 0x008f }
            if (r2 == 0) goto L_0x0034
            java.io.File r2 = new java.io.File     // Catch:{ NullPointerException -> 0x008f }
            java.lang.String r3 = r1.getPath()     // Catch:{ NullPointerException -> 0x008f }
            r2.<init>(r3)     // Catch:{ NullPointerException -> 0x008f }
            boolean r2 = r2.delete()     // Catch:{ NullPointerException -> 0x008f }
            if (r2 != 0) goto L_0x0074
            java.lang.String r2 = TAG     // Catch:{ NullPointerException -> 0x008f }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ NullPointerException -> 0x008f }
            r3.<init>()     // Catch:{ NullPointerException -> 0x008f }
            java.lang.String r4 = "Delete fail - path : "
            r3.append(r4)     // Catch:{ NullPointerException -> 0x008f }
            java.lang.String r1 = r1.getPath()     // Catch:{ NullPointerException -> 0x008f }
            r3.append(r1)     // Catch:{ NullPointerException -> 0x008f }
            java.lang.String r1 = r3.toString()     // Catch:{ NullPointerException -> 0x008f }
            com.sec.android.app.voicenote.provider.Log.m22e(r2, r1)     // Catch:{ NullPointerException -> 0x008f }
            goto L_0x0034
        L_0x0074:
            java.lang.String r2 = TAG     // Catch:{ NullPointerException -> 0x008f }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ NullPointerException -> 0x008f }
            r3.<init>()     // Catch:{ NullPointerException -> 0x008f }
            java.lang.String r4 = "Delete - path : "
            r3.append(r4)     // Catch:{ NullPointerException -> 0x008f }
            java.lang.String r1 = r1.getPath()     // Catch:{ NullPointerException -> 0x008f }
            r3.append(r1)     // Catch:{ NullPointerException -> 0x008f }
            java.lang.String r1 = r3.toString()     // Catch:{ NullPointerException -> 0x008f }
            com.sec.android.app.voicenote.provider.Log.m26i(r2, r1)     // Catch:{ NullPointerException -> 0x008f }
            goto L_0x0034
        L_0x008f:
            java.lang.String r1 = TAG     // Catch:{ all -> 0x009e }
            java.lang.String r2 = "clearContentItem() mPath is null"
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r2)     // Catch:{ all -> 0x009e }
            goto L_0x0034
        L_0x0097:
            java.util.Stack<com.sec.android.app.voicenote.service.ContentItem> r0 = r5.mContentItemStack     // Catch:{ all -> 0x009e }
            r0.clear()     // Catch:{ all -> 0x009e }
            monitor-exit(r5)
            return
        L_0x009e:
            r0 = move-exception
            monitor-exit(r5)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.SimpleEngine.clearContentItem():void");
    }

    public synchronized ContentItem peekContentItem() {
        if (getContentItemCount() <= 0) {
            return null;
        }
        ContentItem peek = this.mContentItemStack.peek();
        String str = TAG;
        Log.m26i(str, "peekContentItem sTime : " + peek.getStartTime() + " eTime : " + peek.getEndTime() + " dTime : " + peek.getDuration() + " path : " + peek.getPath() + " stack size : " + this.mContentItemStack.size());
        return peek;
    }

    public synchronized ContentItem getContentItem(int i) {
        if (getContentItemCount() <= i) {
            return null;
        }
        ContentItem contentItem = (ContentItem) this.mContentItemStack.get(i);
        String str = TAG;
        Log.m26i(str, "getContentItem sTime : " + contentItem.getStartTime() + " eTime : " + contentItem.getEndTime() + " dTime : " + contentItem.getDuration() + " path : " + contentItem.getPath() + " stack size : " + this.mContentItemStack.size());
        return contentItem;
    }

    public String getRecentFilePath() {
        if (this.mContentItemStack.size() < 1) {
            return "";
        }
        String path = this.mContentItemStack.peek().getPath();
        String str = TAG;
        Log.m26i(str, "getRecentFilePath path : " + path);
        return path;
    }

    public void disableSystemSound() {
        this.mRecorder.disableSystemSound();
    }

    public String getPath() {
        return this.mPayer.getPath();
    }

    public int getRepeatMode() {
        return this.mPayer.getRepeatMode();
    }

    public int[] getRepeatPosition() {
        return this.mPayer.getRepeatPosition();
    }

    public void setRepeatTime(int i, int i2) {
        this.mPayer.setRepeatTime(i, i2);
        notifyObservers(104, -1, -1);
    }

    public void setMute(boolean z, boolean z2) {
        this.mPayer.setMute(z, z2);
    }

    public int getSkipSilenceMode() {
        return this.mPayer.getSkipSilenceMode();
    }

    public int setRepeatMode(int i) {
        return this.mPayer.setRepeatMode(i);
    }

    public float getPlaySpeed() {
        return this.mPayer.getPlaySpeed();
    }

    public void setSimplePlayerMode(boolean z) {
        this.mSimplePlayerMode = z;
    }

    public boolean isSimplePlayerMode() {
        return this.mSimplePlayerMode;
    }

    public float setPlaySpeed(float f) {
        return this.mPayer.setPlaySpeed(f);
    }

    public int setSkipSilenceMode(int i) {
        return this.mPayer.enableSkipSilenceMode(i);
    }

    public void setScene(int i) {
        this.mScene = i;
    }

    public int getScene() {
        return this.mScene;
    }

    public boolean isEditSaveEnable() {
        return SimpleStorageProvider.isTempFile(getRecentFilePath());
    }

    private void unregisterAllListener() {
        this.mListeners.clear();
    }
}
