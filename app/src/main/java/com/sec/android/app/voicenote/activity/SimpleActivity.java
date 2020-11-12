package com.sec.android.app.voicenote.activity;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.Manifest;
import com.sec.android.app.voicenote.p007ui.SimpleControlButtonFragment;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.DBProvider;
import com.sec.android.app.voicenote.provider.DesktopModeProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.HWKeyboardProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.NFCProvider;
import com.sec.android.app.voicenote.provider.NavigationBarProvider;
import com.sec.android.app.voicenote.provider.PermissionProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.SimpleStorageProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.receiver.AudioDeviceReceiver;
import com.sec.android.app.voicenote.receiver.VoiceNoteIntentReceiver;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.SimpleEngine;
import com.sec.android.app.voicenote.service.SimpleEngineManager;
import com.sec.android.app.voicenote.service.SimpleMetadataRepositoryManager;
import com.sec.android.app.voicenote.service.VoiceNoteService;
import com.sec.android.app.voicenote.service.remote.RemoteViewManager;
import com.sec.android.app.voicenote.uicore.Observable;
import com.sec.android.app.voicenote.uicore.Observer;
import com.sec.android.app.voicenote.uicore.SimpleFragmentController;
import com.sec.android.app.voicenote.uicore.SimpleMediaSessionManager;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import java.util.ArrayList;

public class SimpleActivity extends AppCompatActivity implements Observer, SimpleControlButtonFragment.OnRecordResultListener, RemoteViewManager.OnRecordChangedListener, SimpleEngine.OnSimpleEngineListener, SimpleFragmentController.OnSceneChangeListener, DialogFactory.DialogResultListener, CursorProvider.OnSimpleCursorChangeListener {
    public static final String BACKGROUND_VOICENOTE_ACCEPT_CALL = "com.samsung.telecom.IncomingCallAnsweredDuringRecord";
    public static final String LAUNCH_INFO = "simple";
    private static final String TAG = "SimpleActivity";
    public static final String VOICENOTE_COVER_CLOSE = "com.sec.android.app.voicenote.cover_close";
    public static final String VOICENOTE_SD_UNMOUNT = "com.sec.android.app.voicenote.sd_unmount";
    private boolean isOldSession = false;
    private boolean isResumed = false;
    private AudioDeviceReceiver mAudioDeviceReceiver = null;
    private BroadcastReceiver mBroadcastReceiver;
    private String mCurrentName;
    private int mCurrentScene = 1;
    private long mCurrentTime = 0;
    private Handler mEngineEventHandler = null;
    private SimpleFragmentController mFragmentController;
    public boolean mIsChooseSttLanguage = false;
    public boolean mIsChooseWebTos = false;
    public boolean mIsEnableNavigationBar = false;
    /* access modifiers changed from: private */
    public boolean mIsPermissionCheckDone = false;
    private LaunchMode mLaunchMode = LaunchMode.INIT;
    private Observable mObservable;
    private long mPlayId;
    private long mRecordedId = -1;
    /* access modifiers changed from: private */
    public String mSession = null;
    /* access modifiers changed from: private */
    public SimpleEngine mSimpleEngine = null;
    /* access modifiers changed from: private */
    public final Handler mStartHandler = new Handler(new Handler.Callback() {
        public boolean handleMessage(Message message) {
            Log.m26i(SimpleActivity.TAG, "handleMessage - Retry to start simple mode");
            SimpleActivity.this.mStartHandler.removeMessages(0);
            if (SimpleActivity.this.mSimpleEngine == null || SimpleActivity.this.mSimpleEngine.getSimpleEngineState() == 1) {
                SimpleActivity.this.mStartHandler.sendEmptyMessageDelayed(0, 500);
            } else {
                SimpleActivity.this.runLaunchMode();
                SimpleActivity.this.setupActionBar();
            }
            return false;
        }
    });
    private Toolbar mToolbar;
    private VoiceNoteIntentReceiver mVoiceNoteIntentReceiver = null;
    private View seekLayout = null;

    public enum LaunchMode {
        INIT,
        ATTACH,
        SFINDER,
        VOICELABEL,
        SPEECHTOTEXT
    }

    public void update(Observable observable, Object obj) {
        int intValue = ((Integer) obj).intValue();
        if (intValue != 18) {
            if (intValue == 979) {
                this.mIsChooseWebTos = true;
            } else if (intValue != 980) {
                switch (intValue) {
                    case Event.SIMPLE_RECORD_START /*50002*/:
                        Log.m26i(TAG, "Event.SIMPLE_RECORD_START : session : " + this.mSession);
                        if (this.mSimpleEngine.getLastSavedFilePath() == null) {
                            if (this.mLaunchMode == LaunchMode.SPEECHTOTEXT) {
                                this.mCurrentName = DBProvider.getInstance().createNewSimpleFileName(4);
                            } else {
                                this.mCurrentName = DBProvider.getInstance().createNewSimpleFileName(1);
                            }
                            setupActionBar();
                            return;
                        }
                        return;
                    case Event.SIMPLE_RECORD_STOP /*50003*/:
                        Log.m26i(TAG, "Event.SIMPLE_RECORD_STOP : session : " + this.mSession);
                        this.mPlayId = this.mSimpleEngine.getSimpleModeItem();
                        CursorProvider.getInstance().registerSimpleCursorChangeListener(this);
                        CursorProvider.getInstance().loadSimple(getSupportLoaderManager(), this.mPlayId);
                        return;
                    default:
                        return;
                }
            } else {
                this.mIsChooseSttLanguage = true;
            }
        } else if (this.isResumed) {
            refresh();
        }
    }

    public void onDialogResult(DialogFragment dialogFragment, Bundle bundle) {
        if (bundle != null) {
            int i = bundle.getInt(DialogFactory.BUNDLE_REQUEST_CODE, -1);
            int i2 = bundle.getInt("result_code");
            Log.m26i(TAG, "onDialogResult requestCode : " + i + " resultCode : " + i2);
            if (i == 14 && i2 == -1) {
                startNFCWritingActivity(this.mRecordedId);
            }
        }
    }

    public void notifyDataSetChanged() {
        Log.m26i(TAG, "notifyDataSetChanged !! session : " + this.mSession);
        if (!SimpleStorageProvider.isExistFile(DBProvider.getInstance().getPathById(this.mPlayId))) {
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        Log.m26i(TAG, "onCreate");
        super.onCreate(bundle);
        getWindow().requestFeature(8);
        setVolumeControlStream(3);
        CursorProvider.getInstance().resetSearchTag();
        SimpleMediaSessionManager.getInstance().createMediaSession();
        RemoteViewManager.getInstance().registerRecordChangedListener(this);
        DesktopModeProvider.getInstance().registerListener();
        registerBroadcastReceiver(true);
        if (getIntent() != null) {
            parseIntent(getIntent());
        }
        if (this.mLaunchMode == LaunchMode.INIT) {
            finish();
        }
        finishNormalMode();
        setContentView((int) C0690R.layout.activity_simple);
        this.mToolbar = (Toolbar) findViewById(C0690R.C0693id.toolbar);
        setSupportActionBar(this.mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(C0690R.C0692drawable.actionbar_background_non_divider, (Resources.Theme) null));
        }
        setSimpleMode();
        this.seekLayout = findViewById(C0690R.C0693id.simple_multi_seekbar);
        Log.m26i(TAG, "savedInstanceState  " + bundle);
        if (bundle != null) {
            this.mSession = bundle.getString("session-id");
            int i = bundle.getInt("current-event");
            this.mCurrentScene = bundle.getInt("current-scene");
            this.mSimpleEngine = SimpleEngineManager.getInstance().getEngine(this.mSession);
            this.mFragmentController = new SimpleFragmentController(this.mSession, i, this.mCurrentScene, this.mLaunchMode, this);
            this.mCurrentName = bundle.getString("current-name");
            this.isOldSession = bundle.getBoolean("previous-session-valid");
            Log.m26i(TAG, "onCreate  OldSession - reused" + this.mSession);
        } else {
            this.mSession = VoiceNoteApplication.createNewSession();
            this.mSimpleEngine = SimpleEngineManager.getInstance().getEngine(this.mSession);
            this.mFragmentController = new SimpleFragmentController(this.mSession, Event.SIMPLE_RECORD_OPEN, 0, this.mLaunchMode, this);
            this.isOldSession = false;
            this.mSimpleEngine.setSimpleModeItem(-1);
            if (this.mLaunchMode == LaunchMode.SPEECHTOTEXT) {
                this.mCurrentName = DBProvider.getInstance().createNewSimpleFileName(4);
            } else {
                this.mCurrentName = DBProvider.getInstance().createNewSimpleFileName(1);
            }
            Log.m26i(TAG, "onCreate  new session created " + this.mSession);
        }
        this.mSimpleEngine.registerListener(this);
        this.mVoiceNoteIntentReceiver = new VoiceNoteIntentReceiver(this);
        this.mVoiceNoteIntentReceiver.registerListener();
        this.mAudioDeviceReceiver = new AudioDeviceReceiver(this);
        this.mAudioDeviceReceiver.registerListener();
        if (this.mLaunchMode != LaunchMode.SFINDER) {
            this.mSimpleEngine.setSimpleRecorderMode(true);
        }
        this.mFragmentController.registerSceneChangeListener(this);
        this.mObservable = Observable.getInstance();
        this.mObservable.addObserver(this.mSession, this);
        this.mEngineEventHandler = new Handler(new Handler.Callback() {
            public final boolean handleMessage(Message message) {
                return SimpleActivity.this.lambda$onCreate$0$SimpleActivity(message);
            }
        });
        NavigationBarProvider instance = NavigationBarProvider.getInstance();
        if (instance.isDeviceSupportSoftNavigationBar()) {
            this.mIsEnableNavigationBar = instance.isNavigationBarEnabled();
            instance.setOnSystemUiVisibilityChangeListener(this);
        }
        if (this.mLaunchMode == LaunchMode.SFINDER) {
            CursorProvider.getInstance().registerSimpleCursorChangeListener(this);
            CursorProvider.getInstance().loadSimple(getSupportLoaderManager(), this.mPlayId);
        }
    }

    public /* synthetic */ boolean lambda$onCreate$0$SimpleActivity(Message message) {
        if (!isDestroyed() && message.what == 103) {
            Log.m26i(TAG, "Engine.INFO_SAVED_ID - id : " + message.arg1);
            this.mSimpleEngine.unregisterListener(this);
            runLaunchMode();
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putString("session-id", this.mSession);
        bundle.putInt("current-event", this.mFragmentController.getCurrentEvent());
        bundle.putInt("current-scene", this.mFragmentController.getCurrentScene());
        bundle.putString("current-name", this.mCurrentName);
        bundle.putBoolean("previous-session-valid", this.isOldSession);
        super.onSaveInstanceState(bundle);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        ArrayList arrayList;
        SimpleEngine simpleEngine;
        Log.m27i(TAG, "onResume", this.mSession);
        super.onResume();
        boolean z = true;
        this.isResumed = true;
        if (this.mLaunchMode == LaunchMode.VOICELABEL) {
            arrayList = new ArrayList();
            arrayList.add(5);
        } else {
            arrayList = null;
            z = false;
        }
        this.mIsChooseSttLanguage = false;
        this.mIsChooseWebTos = false;
        runLaunchMode();
        this.mIsPermissionCheckDone = PermissionProvider.checkPermission(this, arrayList, z);
        if (this.mIsPermissionCheckDone && (simpleEngine = this.mSimpleEngine) != null && simpleEngine.getSimpleEngineState() == 0) {
            setupActionBar();
        }
        if (HWKeyboardProvider.isHWKeyboard(this)) {
            this.mObservable.notifyObservers(this.mSession, 11);
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        Log.m27i(TAG, "onPause", this.mSession);
        this.isResumed = false;
        super.onPause();
        Log.m26i(TAG, VoiceNoteApplication.getApkInfo());
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        Log.m27i(TAG, "onStop", this.mSession);
        super.onStop();
        if (DialogFactory.isDialogVisible(getSupportFragmentManager(), DialogFactory.PERMISSION_DIALOG)) {
            DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.PERMISSION_DIALOG);
        }
        if (!this.mIsChooseSttLanguage && !this.mIsChooseWebTos && this.mIsPermissionCheckDone && !isChangingConfigurations()) {
            saveSimpleRecording();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        Log.m27i(TAG, "onDestroy", this.mSession);
        RemoteViewManager.getInstance().unregisterRecordChangedListener();
        registerBroadcastReceiver(false);
        this.mSimpleEngine.unregisterListener(this);
        SimpleFragmentController simpleFragmentController = this.mFragmentController;
        if (simpleFragmentController != null) {
            simpleFragmentController.unregisterSceneChangeListener(this);
            this.mFragmentController.onDestroy();
            this.mFragmentController = null;
        }
        Observable observable = this.mObservable;
        if (observable != null) {
            observable.deleteObserver(this.mSession, this);
            this.mObservable = null;
        }
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
        CursorProvider.getInstance().unregisterSimpleCursorChangeListener(this);
        DesktopModeProvider.getInstance().unregisterListener();
        super.onDestroy();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        Log.m26i(TAG, "onConfigurationChanged : " + HWKeyboardProvider.isHWKeyboard(this));
        if (HWKeyboardProvider.isDeviceHasHardKeyboard(this)) {
            HWKeyboardProvider.setDeviceHasHardkeyboard(true);
        }
        if (HWKeyboardProvider.getDeviceHasHardkeyboard()) {
            getWindow().setAttributes(getWindow().getAttributes());
        } else if (HWKeyboardProvider.isHWKeyboard(this)) {
            this.mObservable.notifyObservers(this.mSession, 11);
        } else {
            this.mObservable.notifyObservers(this.mSession, 12);
        }
        if (!isInMultiWindow()) {
            handleConfigChangeForFullMode();
        }
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        int i2;
        Log.m27i(TAG, "onKeyDown - keyCode : " + i + " - scene : " + this.mCurrentScene, this.mSession);
        if (VoiceNoteFeature.FLAG_SUPPORT_BLE_SPEN_AIR_ACTION && i == 130 && ((i2 = this.mCurrentScene) == 1 || i2 == 2)) {
            if (System.currentTimeMillis() - this.mCurrentTime < 1000) {
                Log.m32w(TAG, "onKeyDown break - less than 1s from last SPEN control stop");
            } else if (this.mSimpleEngine.getRecorderState() == 1) {
                this.mObservable.notifyObservers(this.mSession, Integer.valueOf(Event.BLE_SPEN_RECORD_START));
            } else {
                this.mCurrentTime = System.currentTimeMillis();
                this.mObservable.notifyObservers(this.mSession, Integer.valueOf(Event.BLE_SPEN_RECORD_PAUSE_RESUME));
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    public void onBackPressed() {
        Log.m26i(TAG, "onBackPressed");
        if (!isDestroyed()) {
            onRecordResult(Event.SIMPLE_MODE_CANCEL, -1);
            super.onBackPressed();
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.m26i(TAG, "onPrepareOptionsMenu");
        menu.clear();
        return super.onPrepareOptionsMenu(menu);
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        Log.m26i(TAG, "onRequestPermissionsResult - requestCode : " + i);
        if (strArr.length == 0 || iArr.length == 0) {
            Log.m26i(TAG, "onRequestPermissionsResult - permissions or grantResults size is zero");
        } else if (i != 1) {
            super.onRequestPermissionsResult(i, strArr, iArr);
        } else {
            boolean z = false;
            Settings.setSettings(Settings.KEY_FORCE_SYSTEM_PERMISSION_DIALOG, false);
            if (this.mLaunchMode == LaunchMode.VOICELABEL) {
                Settings.setSettings(Settings.KEY_FORCE_SYSTEM_PERMISSION_DIALOG_PHONE, false);
            }
            this.mIsPermissionCheckDone = true;
            int length = iArr.length;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    break;
                } else if (iArr[i2] != 0) {
                    SimpleEngineManager.getInstance().deleteEngine(this.mSession);
                    finish();
                    z = true;
                    break;
                } else {
                    i2++;
                }
            }
            if (!z && PermissionProvider.checkStoragePermission(this, 1, C0690R.string.app_name)) {
                runLaunchMode();
                setupActionBar();
            }
        }
    }

    /* access modifiers changed from: private */
    public void setupActionBar() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar == null) {
            return;
        }
        if (this.mLaunchMode == LaunchMode.SFINDER) {
            supportActionBar.setTitle((CharSequence) DBProvider.getInstance().getFileName(this.mPlayId));
        } else {
            supportActionBar.setTitle((CharSequence) this.mCurrentName);
        }
    }

    public void onRecordResult(int i, long j) {
        StringBuilder sb = new StringBuilder();
        sb.append("onRecordResult: ");
        sb.append(i);
        sb.append(", ");
        sb.append(j);
        int i2 = (j > 0 ? 1 : (j == 0 ? 0 : -1));
        sb.append(i2 < 0 ? " not attached." : "");
        sb.append(" session :");
        sb.append(this.mSession);
        Log.m26i(TAG, sb.toString());
        if (i == 967) {
            setupActionBar();
        } else if (i == 982) {
            cancelRecording();
            finishPlaying();
            SimpleEngineManager.getInstance().deleteEngine(this.mSession);
            finish();
        } else if (i == 983 && i2 >= 0) {
            this.mRecordedId = j;
            LaunchMode launchMode = this.mLaunchMode;
            if (launchMode == LaunchMode.ATTACH) {
                finishPlaying();
                Uri contentURI = DBProvider.getInstance().getContentURI(this.mRecordedId);
                Intent intent = new Intent();
                intent.setData(contentURI);
                intent.setFlags(1);
                setResult(-1, intent);
                this.mSimpleEngine.setSimpleModeItem(-1);
                SimpleEngineManager.getInstance().deleteEngine(this.mSession);
                finish();
            } else if (launchMode == LaunchMode.VOICELABEL) {
                startNFCWritingActivity(this.mRecordedId);
            } else if (launchMode == LaunchMode.SPEECHTOTEXT) {
                saveSimpleRecording();
            }
        }
    }

    public void onRecordDone(long j) {
        this.mSimpleEngine.setSimpleModeItem(-1);
        StringBuilder sb = new StringBuilder();
        sb.append("onRecordDone: ");
        sb.append(j);
        int i = (j > 0 ? 1 : (j == 0 ? 0 : -1));
        sb.append(i < 0 ? " not attached." : "");
        Log.m26i(TAG, sb.toString());
        finishPlaying();
        if (i >= 0) {
            if (this.mLaunchMode == LaunchMode.ATTACH) {
                Uri contentURI = DBProvider.getInstance().getContentURI(j);
                Intent intent = new Intent();
                intent.setData(contentURI);
                intent.setFlags(1);
                setResult(-1, intent);
            }
            SimpleEngineManager.getInstance().deleteEngine(this.mSession);
            finish();
        }
    }

    public void finishPlayer() {
        finishPlaying();
        SimpleEngineManager.getInstance().deleteEngine(this.mSession);
        finish();
    }

    private void parseIntent(Intent intent) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            char c = 65535;
            switch (action.hashCode()) {
                case -2025987874:
                    if (action.equals("voicenote.intent.action.accessibility")) {
                        c = 2;
                        break;
                    }
                    break;
                case -109726588:
                    if (action.equals("voicenote.intent.action.SPEECH_TO_TEXT")) {
                        c = 3;
                        break;
                    }
                    break;
                case 244611860:
                    if (action.equals("voicenote.intent.action.suggest")) {
                        c = 0;
                        break;
                    }
                    break;
                case 289773812:
                    if (action.equals("android.provider.MediaStore.RECORD_SOUND")) {
                        c = 1;
                        break;
                    }
                    break;
            }
            if (c != 0) {
                if (c == 1) {
                    this.mLaunchMode = LaunchMode.ATTACH;
                } else if (c == 2) {
                    this.mLaunchMode = LaunchMode.VOICELABEL;
                } else if (c == 3) {
                    this.mLaunchMode = LaunchMode.SPEECHTOTEXT;
                }
            } else if ((intent.getFlags() & 1048576) == 0) {
                String stringExtra = intent.getStringExtra("intent_extra_data_key");
                if (stringExtra != null) {
                    this.mPlayId = Long.parseLong(stringExtra);
                    this.mLaunchMode = LaunchMode.SFINDER;
                }
            } else {
                return;
            }
            ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setDisplayOptions(8);
                supportActionBar.setTitle((CharSequence) "");
            }
            Log.m26i(TAG, "parseIntent() : mLaunchMode = " + this.mLaunchMode);
        }
    }

    private void setSimpleMode() {
        Log.m26i(TAG, "setSimpleMode() : mLaunchMode = " + this.mLaunchMode);
        if (this.mLaunchMode == LaunchMode.SPEECHTOTEXT) {
            Settings.setSettings(Settings.KEY_SIMPLE_RECORD_MODE, 4);
        } else {
            Settings.setSettings(Settings.KEY_SIMPLE_RECORD_MODE, 1);
        }
    }

    /* access modifiers changed from: private */
    public void runLaunchMode() {
        if (!this.isOldSession && this.mSimpleEngine.getRecorderState() == 1 && this.mSimpleEngine.getPlayerState() == 1) {
            SimpleMetadataRepositoryManager.getInstance().getMetadataRepository(this.mSession).setRecordMode(1);
            Settings.setSettings(Settings.KEY_SIMPLE_RECORD_MODE, 1);
            Log.m26i(TAG, "runLaunchMode() : mLaunchMode = " + this.mLaunchMode);
            int i = C07043.f98x6e2b7812[this.mLaunchMode.ordinal()];
            if (i == 1) {
                String pathById = DBProvider.getInstance().getPathById(this.mPlayId);
                if (!SimpleStorageProvider.isExistFile(pathById)) {
                    Toast.makeText(this, C0690R.string.this_file_does_not_exist, 1).show();
                    SimpleEngineManager.getInstance().deleteEngine(this.mSession);
                    finish();
                    return;
                }
                this.mSimpleEngine.setSimplePlayerMode(true);
                int initPlay = this.mSimpleEngine.initPlay(pathById, this.mPlayId, true);
                if (initPlay == -119) {
                    Log.m26i(TAG, "runLaunchMode SimpleEngine is busy now !!");
                    this.mSimpleEngine.registerListener(this);
                } else if (initPlay == -103) {
                    Toast.makeText(this, C0690R.string.no_play_during_call, 0).show();
                    SimpleEngineManager.getInstance().deleteEngine(this.mSession);
                    finish();
                } else if (initPlay == 0) {
                    this.mObservable.notifyObservers(this.mSession, Integer.valueOf(Event.SIMPLE_PLAY_OPEN));
                }
            } else if (i == 2) {
                checkAttachAvailable();
                this.mSimpleEngine.setSimpleRecorderMode(true);
                this.mObservable.notifyObservers(this.mSession, Integer.valueOf(Event.SIMPLE_RECORD_OPEN));
                SimpleMetadataRepositoryManager.getInstance().getMetadataRepository(this.mSession).setRecordMode(5);
            } else if (i == 3) {
                Settings.setSettings(Settings.KEY_SIMPLE_RECORD_MODE, 4);
                SimpleMetadataRepositoryManager.getInstance().getMetadataRepository(this.mSession).setRecordMode(4);
                this.mSimpleEngine.setSimpleRecorderMode(true);
                this.mObservable.notifyObservers(this.mSession, Integer.valueOf(Event.SIMPLE_RECORD_OPEN));
            } else if (i == 4) {
                this.mSimpleEngine.setSimpleRecorderMode(true);
                this.mObservable.notifyObservers(this.mSession, Integer.valueOf(Event.SIMPLE_RECORD_OPEN));
            }
            if (this.mCurrentName == null) {
                if (this.mLaunchMode == LaunchMode.SPEECHTOTEXT) {
                    this.mCurrentName = DBProvider.getInstance().createNewSimpleFileName(4);
                } else {
                    this.mCurrentName = DBProvider.getInstance().createNewSimpleFileName(1);
                }
            }
            this.isOldSession = true;
            return;
        }
        Log.m27i(TAG, "runLaunchMode() : same mSession- no need to relaunch again", this.mSession);
    }

    /* renamed from: com.sec.android.app.voicenote.activity.SimpleActivity$3 */
    static /* synthetic */ class C07043 {

        /* renamed from: $SwitchMap$com$sec$android$app$voicenote$activity$SimpleActivity$LaunchMode */
        static final /* synthetic */ int[] f98x6e2b7812 = new int[LaunchMode.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(10:0|1|2|3|4|5|6|7|8|10) */
        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|(3:7|8|10)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x002a */
        static {
            /*
                com.sec.android.app.voicenote.activity.SimpleActivity$LaunchMode[] r0 = com.sec.android.app.voicenote.activity.SimpleActivity.LaunchMode.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                f98x6e2b7812 = r0
                int[] r0 = f98x6e2b7812     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.sec.android.app.voicenote.activity.SimpleActivity$LaunchMode r1 = com.sec.android.app.voicenote.activity.SimpleActivity.LaunchMode.SFINDER     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = f98x6e2b7812     // Catch:{ NoSuchFieldError -> 0x001f }
                com.sec.android.app.voicenote.activity.SimpleActivity$LaunchMode r1 = com.sec.android.app.voicenote.activity.SimpleActivity.LaunchMode.ATTACH     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = f98x6e2b7812     // Catch:{ NoSuchFieldError -> 0x002a }
                com.sec.android.app.voicenote.activity.SimpleActivity$LaunchMode r1 = com.sec.android.app.voicenote.activity.SimpleActivity.LaunchMode.SPEECHTOTEXT     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                int[] r0 = f98x6e2b7812     // Catch:{ NoSuchFieldError -> 0x0035 }
                com.sec.android.app.voicenote.activity.SimpleActivity$LaunchMode r1 = com.sec.android.app.voicenote.activity.SimpleActivity.LaunchMode.VOICELABEL     // Catch:{ NoSuchFieldError -> 0x0035 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0035 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0035 }
            L_0x0035:
                return
            */
//            throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.activity.SimpleActivity.C07043.<clinit>():void");
        }
    }

    private void checkAttachAvailable() {
        if (getIntent().getLongExtra("android.provider.MediaStore.extra.MAX_BYTES", 10247680) < 10240) {
            Toast.makeText(this, C0690R.string.exceed_message_size_limitation, 0).show();
            SimpleEngineManager.getInstance().deleteEngine(this.mSession);
            finish();
        }
    }

    public void finishNormalMode() {
        Log.m27i(TAG, "finishNormalMode", this.mSession);
        if (Engine.getInstance().getRecorderState() != 1) {
            sendBroadcast(new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_SAVE));
        }
        if (Engine.getInstance().getTranslationState() != 1) {
            sendBroadcast(new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_TRANSLATION_SAVE));
        } else if (Engine.getInstance().getPlayerState() != 1) {
            sendBroadcast(new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_PLAY_STOP));
        }
        sendBroadcast(new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_HIDE_NOTIFICATION));
    }

    private void finishPlaying() {
        SimpleEngine simpleEngine = this.mSimpleEngine;
        if (simpleEngine != null && simpleEngine.getPlayerState() != 1) {
            this.mSimpleEngine.stopPlay();
            this.mSimpleEngine.setOriginalFilePath((String) null);
            SimpleMetadataRepositoryManager.getInstance().deleteMetadataRepository(this.mSession);
            this.mSimpleEngine.clearContentItem();
            this.mSimpleEngine.setSimplePlayerMode(false);
        }
    }

    private void cancelRecording() {
        SimpleEngine simpleEngine = this.mSimpleEngine;
        if (simpleEngine != null) {
            if (simpleEngine.getRecorderState() != 1) {
                this.mSimpleEngine.cancelRecord();
            }
            this.mSimpleEngine.setUserSettingName((String) null);
        }
    }

    /* access modifiers changed from: private */
    public void saveSimpleRecording() {
        long j;
        SimpleEngine simpleEngine = this.mSimpleEngine;
        if (simpleEngine != null) {
            if (simpleEngine.getRecorderState() != 1) {
                long stopRecord = this.mSimpleEngine.stopRecord(true, true);
                if (stopRecord == -119) {
                    this.mSimpleEngine.cancelRecord();
                }
                this.mSimpleEngine.setSimpleModeItem(stopRecord);
            }
            j = this.mSimpleEngine.getSimpleModeItem();
        } else {
            j = 0;
        }
        if (this.mLaunchMode != LaunchMode.ATTACH || j < 0) {
            onRecordResult(Event.SIMPLE_MODE_CANCEL, j);
        } else {
            onRecordResult(Event.SIMPLE_MODE_DONE, j);
        }
        SimpleEngine simpleEngine2 = this.mSimpleEngine;
        if (simpleEngine2 != null) {
            simpleEngine2.setUserSettingName((String) null);
        }
    }

    private void startNFCWritingActivity(long j) {
        Log.m26i(TAG, "startNFCWritingActivity()");
        String currentLabelInfo = NFCProvider.getCurrentLabelInfo(this, j);
        if (NFCProvider.isNFCEnabled(this)) {
            Log.m26i(TAG, "NFC is enabled");
            Intent intent = new Intent(this, NFCWritingActivity.class);
            intent.setFlags(134217728);
            intent.putExtra(NFCWritingActivity.TAG_LABEL_INFO, currentLabelInfo);
            intent.putExtra(NFCWritingActivity.FROM_LAUNCH_INFO, LAUNCH_INFO);
            Settings.setSettings(Settings.KEY_NFC_LABEL_INFO, currentLabelInfo);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.m24e(TAG, "ActivityNotFoundException", (Throwable) e);
            }
        } else {
            Log.m26i(TAG, "NFC isn't enabled and do show dialog");
            Bundle bundle = new Bundle();
            bundle.putInt(DialogFactory.BUNDLE_REQUEST_CODE, 14);
            DialogFactory.show(getSupportFragmentManager(), DialogFactory.ENABLE_NFC_DIALOG, bundle, this);
        }
    }

    private void registerBroadcastReceiver(boolean z) {
        if (z) {
            Log.m26i(TAG, "register broadcastReceiver");
            if (this.mBroadcastReceiver == null) {
                this.mBroadcastReceiver = new BroadcastReceiver() {
                    /* JADX WARNING: Removed duplicated region for block: B:17:0x0052  */
                    /* JADX WARNING: Removed duplicated region for block: B:34:0x00f5  */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void onReceive(android.content.Context r6, android.content.Intent r7) {
                        /*
                            r5 = this;
                            java.lang.String r6 = r7.getAction()
                            java.lang.StringBuilder r0 = new java.lang.StringBuilder
                            r0.<init>()
                            java.lang.String r1 = "onReceive action : "
                            r0.append(r1)
                            r0.append(r6)
                            java.lang.String r0 = r0.toString()
                            java.lang.String r1 = "SimpleActivity"
                            com.sec.android.app.voicenote.provider.Log.m26i(r1, r0)
                            int r0 = r6.hashCode()
                            r1 = -531434542(0xffffffffe052f3d2, float:-6.0802896E19)
                            r2 = 2
                            r3 = 0
                            r4 = 1
                            if (r0 == r1) goto L_0x0045
                            r1 = -368883504(0xffffffffea0348d0, float:-3.9678282E25)
                            if (r0 == r1) goto L_0x003b
                            r1 = -225542389(0xfffffffff28e7f0b, float:-5.6448585E30)
                            if (r0 == r1) goto L_0x0031
                            goto L_0x004f
                        L_0x0031:
                            java.lang.String r0 = "com.samsung.telecom.IncomingCallAnsweredDuringRecord"
                            boolean r6 = r6.equals(r0)
                            if (r6 == 0) goto L_0x004f
                            r6 = r3
                            goto L_0x0050
                        L_0x003b:
                            java.lang.String r0 = "com.sec.android.app.voicenote.cover_close"
                            boolean r6 = r6.equals(r0)
                            if (r6 == 0) goto L_0x004f
                            r6 = r2
                            goto L_0x0050
                        L_0x0045:
                            java.lang.String r0 = "com.sec.android.app.voicenote.sd_unmount"
                            boolean r6 = r6.equals(r0)
                            if (r6 == 0) goto L_0x004f
                            r6 = r4
                            goto L_0x0050
                        L_0x004f:
                            r6 = -1
                        L_0x0050:
                            if (r6 == 0) goto L_0x00f5
                            if (r6 == r4) goto L_0x005f
                            if (r6 == r2) goto L_0x0058
                            goto L_0x0102
                        L_0x0058:
                            com.sec.android.app.voicenote.activity.SimpleActivity r6 = com.sec.android.app.voicenote.activity.SimpleActivity.this
                            r6.saveSimpleRecording()
                            goto L_0x0102
                        L_0x005f:
                            com.sec.android.app.voicenote.activity.SimpleActivity r6 = com.sec.android.app.voicenote.activity.SimpleActivity.this
                            com.sec.android.app.voicenote.service.SimpleEngine r6 = r6.mSimpleEngine
                            java.lang.String r6 = r6.getRecentFilePath()
                            java.lang.String r0 = "tag_path"
                            java.lang.String r7 = r7.getStringExtra(r0)
                            if (r7 == 0) goto L_0x00e7
                            boolean r0 = r6.isEmpty()
                            if (r0 != 0) goto L_0x00e7
                            boolean r6 = r6.startsWith(r7)
                            if (r6 == 0) goto L_0x00e7
                            com.sec.android.app.voicenote.activity.SimpleActivity r6 = com.sec.android.app.voicenote.activity.SimpleActivity.this
                            com.sec.android.app.voicenote.service.SimpleEngine r6 = r6.mSimpleEngine
                            int r6 = r6.getRecorderState()
                            if (r6 == r4) goto L_0x00b1
                            com.sec.android.app.voicenote.activity.SimpleActivity r6 = com.sec.android.app.voicenote.activity.SimpleActivity.this
                            com.sec.android.app.voicenote.service.SimpleEngine r6 = r6.mSimpleEngine
                            r6.cancelRecord()
                            com.sec.android.app.voicenote.activity.SimpleActivity r6 = com.sec.android.app.voicenote.activity.SimpleActivity.this
                            r7 = 2131755421(0x7f10019d, float:1.914172E38)
                            android.widget.Toast r6 = android.widget.Toast.makeText(r6, r7, r4)
                            r6.show()
                            com.sec.android.app.voicenote.service.SimpleEngineManager r6 = com.sec.android.app.voicenote.service.SimpleEngineManager.getInstance()
                            com.sec.android.app.voicenote.activity.SimpleActivity r7 = com.sec.android.app.voicenote.activity.SimpleActivity.this
                            java.lang.String r7 = r7.mSession
                            r6.deleteEngine(r7)
                            com.sec.android.app.voicenote.activity.SimpleActivity r6 = com.sec.android.app.voicenote.activity.SimpleActivity.this
                            r6.finish()
                            goto L_0x00e7
                        L_0x00b1:
                            com.sec.android.app.voicenote.activity.SimpleActivity r6 = com.sec.android.app.voicenote.activity.SimpleActivity.this
                            com.sec.android.app.voicenote.service.SimpleEngine r6 = r6.mSimpleEngine
                            int r6 = r6.getPlayerState()
                            if (r6 == r4) goto L_0x00e2
                            com.sec.android.app.voicenote.activity.SimpleActivity r6 = com.sec.android.app.voicenote.activity.SimpleActivity.this
                            com.sec.android.app.voicenote.service.SimpleEngine r6 = r6.mSimpleEngine
                            r6.cancelRecord()
                            com.sec.android.app.voicenote.activity.SimpleActivity r6 = com.sec.android.app.voicenote.activity.SimpleActivity.this
                            com.sec.android.app.voicenote.service.SimpleEngine r6 = r6.mSimpleEngine
                            r6.stopPlay(r3)
                            com.sec.android.app.voicenote.service.SimpleEngineManager r6 = com.sec.android.app.voicenote.service.SimpleEngineManager.getInstance()
                            com.sec.android.app.voicenote.activity.SimpleActivity r7 = com.sec.android.app.voicenote.activity.SimpleActivity.this
                            java.lang.String r7 = r7.mSession
                            r6.deleteEngine(r7)
                            com.sec.android.app.voicenote.activity.SimpleActivity r6 = com.sec.android.app.voicenote.activity.SimpleActivity.this
                            r6.finish()
                            goto L_0x00e7
                        L_0x00e2:
                            com.sec.android.app.voicenote.activity.SimpleActivity r6 = com.sec.android.app.voicenote.activity.SimpleActivity.this
                            r6.setupActionBar()
                        L_0x00e7:
                            java.lang.String r6 = "storage"
                            com.sec.android.app.voicenote.provider.Settings.setSettings((java.lang.String) r6, (int) r3)
                            com.sec.android.app.voicenote.provider.SimpleStorageProvider.resetSDCardWritableDir()
                            java.lang.String r6 = "sdcard_previous_state"
                            com.sec.android.app.voicenote.provider.Settings.setSettings((java.lang.String) r6, (int) r3)
                            goto L_0x0102
                        L_0x00f5:
                            com.sec.android.app.voicenote.activity.SimpleActivity r6 = com.sec.android.app.voicenote.activity.SimpleActivity.this
                            boolean r6 = r6.mIsPermissionCheckDone
                            if (r6 == 0) goto L_0x0102
                            com.sec.android.app.voicenote.activity.SimpleActivity r6 = com.sec.android.app.voicenote.activity.SimpleActivity.this
                            r6.saveSimpleRecording()
                        L_0x0102:
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.activity.SimpleActivity.C07032.onReceive(android.content.Context, android.content.Intent):void");
                    }
                };
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("com.samsung.telecom.IncomingCallAnsweredDuringRecord");
                intentFilter.addAction("com.sec.android.app.voicenote.sd_unmount");
                intentFilter.addAction("com.sec.android.app.voicenote.cover_close");
//                LocalBroadcastManager.getInstance(this).registerReceiver(this.mBroadcastReceiver, intentFilter);
                registerReceiver(this.mBroadcastReceiver, intentFilter, Manifest.permission.Controller, (Handler) null);
                return;
            }
            return;
        }
        Log.m26i(TAG, "unregister broadcastReceiver");
        if (this.mBroadcastReceiver != null) {
//            LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mBroadcastReceiver);
            unregisterReceiver(this.mBroadcastReceiver);
            this.mBroadcastReceiver = null;
        }
    }

    public void onEngineUpdate(int i, int i2, int i3) {
        Handler handler = this.mEngineEventHandler;
        if (handler != null) {
            handler.sendMessage(handler.obtainMessage(i, i2, i3));
        }
    }

    public void onSceneChange(int i) {
        this.mCurrentScene = i;
        if (isInMultiWindow() || DesktopModeProvider.isDesktopMode()) {
            int i2 = this.mCurrentScene;
            if (i2 == 1 || i2 == 2) {
                this.seekLayout.setVisibility(8);
            } else if (i2 != 4) {
                this.seekLayout.setVisibility(8);
            } else {
                this.seekLayout.setVisibility(0);
            }
        }
    }

    private void handleConfigChangeForFullMode() {
        Log.m26i(TAG, "handleConfigChangeForFullMode");
        setContentView((int) C0690R.layout.activity_simple);
        setSupportActionBar((Toolbar) findViewById(C0690R.C0693id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(C0690R.C0692drawable.actionbar_background_non_divider, (Resources.Theme) null));
        }
        setupActionBar();
        SimpleFragmentController simpleFragmentController = this.mFragmentController;
        if (simpleFragmentController != null) {
            simpleFragmentController.updateScene();
        }
    }

    private boolean isInMultiWindow() {
        return !isDestroyed() && isInMultiWindowMode();
    }

    private void refresh() {
        Log.m29v(TAG, "refresh");
        startActivity(new Intent(this, SimpleActivity.class));
    }

    public SimpleFragmentController getCurrentSimpleFragmentController() {
        return this.mFragmentController;
    }
}
