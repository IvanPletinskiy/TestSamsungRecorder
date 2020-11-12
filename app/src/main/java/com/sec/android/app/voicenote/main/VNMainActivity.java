package com.sec.android.app.voicenote.main;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.bixby.constant.BixbyConstant;
import com.sec.android.app.voicenote.common.util.AndroidForWork;
import com.sec.android.app.voicenote.common.util.BackgroundRestrictHelper;
import com.sec.android.app.voicenote.common.util.DeviceInfo;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.common.util.KeyguardManagerHelper;
import com.sec.android.app.voicenote.common.util.Trace;
import com.sec.android.app.voicenote.common.util.TrashHelper;
import com.sec.android.app.voicenote.common.util.VNMediaScanner;
import com.sec.android.app.voicenote.main.VNMainActivity;
import com.sec.android.app.voicenote.p007ui.actionbar.MainActionbar;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.p007ui.pager.ModePager;
import com.sec.android.app.voicenote.provider.CallRejectChecker;
import com.sec.android.app.voicenote.provider.CheckedItemProvider;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.DBProvider;
import com.sec.android.app.voicenote.provider.DesktopModeProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.HWKeyboardProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.MouseKeyboardProvider;
import com.sec.android.app.voicenote.provider.NavigationBarProvider;
import com.sec.android.app.voicenote.provider.Network;
import com.sec.android.app.voicenote.provider.PermissionProvider;
import com.sec.android.app.voicenote.provider.PhoneStateProvider;
import com.sec.android.app.voicenote.provider.PrivateModeProvider;
import com.sec.android.app.voicenote.provider.SecureFolderProvider;

import com.sec.android.app.voicenote.provider.StorageProvider;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.provider.TypefaceProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.receiver.GlobalSettingsObserver;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.IVoiceNoteService;
import com.sec.android.app.voicenote.service.IVoiceNoteServiceCallback;
import com.sec.android.app.voicenote.service.VoiceNoteService;
import com.sec.android.app.voicenote.service.decoder.Decoder;
import com.sec.android.app.voicenote.service.helper.BixbyHelper;
import com.sec.android.app.voicenote.service.helper.BluetoothHelper;
import com.sec.android.app.voicenote.service.remote.RemoteViewManager;
import com.sec.android.app.voicenote.uicore.FragmentController;
import com.sec.android.app.voicenote.uicore.MediaSessionManager;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Has LAUNCHER intent filter
 */

public class VNMainActivity extends AppCompatActivity implements Observer, FragmentController.OnSceneChangeListener {
    private static final String BUNDLE_SCENE = "scene";
    private static final int CHECK_SDCARD_DO_NOTHING = 0;
    private static final int CHECK_SDCARD_HIDE_DIALOG = 2;
    private static final int CHECK_SDCARD_SHOW_DIALOG = 1;
    private static final String EDGE_INTENT_RECORD_START = "voicenote.intent.action.edge_start_record";
    private static final String LAYOUT_DIRECTION = "layout_direction";
    private static final String LEVEL_ACTIVE_KEY_INTENT = "voicenote.intent.action.level_activekey";
    public static final int MESSAGE_BACK_CLICKABLE = 6;
    public static final int MESSAGE_FROM_BIXBY = 5;
    public static final int MESSAGE_FROM_LEVEL_ACTIVEKEY = 4;
    public static final int MESSAGE_FROM_SVOICE = 2;
    public static final int MESSAGE_FROM_TASK_EDGE = 3;
    public static final int MESSAGE_SPEN_RECORD_PAUSE_RESUME = 7;
    public static final int MESSAGE_SPEN_RECORD_START = 8;
    public static final int MESSAGE_TIMER = 1;
    private static final int NOT_CHECK_SDCARD = -1;
    private static final String PRIVATE_INTENT_ACTION = "voicenote.intent.action.privatebox";
    private static final String TAG = "VNMainActivity";
    private MainActionbar mActionbar = null;
    /* access modifiers changed from: private */
    public boolean mBackKeyLock = false;
    private int mCheckSDCardAction = -1;
    /* access modifiers changed from: private */
    public boolean mClickableBackKey = true;
    private long mCurrentTime = 0;
    private boolean mFinish = false;
    private FragmentController mFragmentController;
    private boolean mFromActionSearch = true;
    private boolean mFromEdge = false;
    private GlobalSettingsObserver mGlobalSettingObserver;
    private Handler mHandler = new MainHandler(this);
    /* access modifiers changed from: private */
    public IVoiceNoteService mIService = null;
    /* access modifiers changed from: private */
    public final IVoiceNoteServiceCallback mIServiceCallback = new IVoiceNoteServiceCallback.Stub() {
        public void messageCallback(int i, int i2) {
            Log.m26i(VNMainActivity.TAG, "message from service - msg : " + i + " arg : " + i2);
            if (VNMainActivity.this.mObservable == null) {
                Log.m22e(VNMainActivity.TAG, "IVoiceNoteServiceCallback - mObservable is NULL");
            } else if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
                VNMainActivity.this.serviceCallBackMsgHandler(i, i2);
            } else {
                Log.m26i(VNMainActivity.TAG, "change running thread to main UI thread");
//                VNMainActivity.this.runOnUiThread(new Runnable(i, i2) {
//                    private final /* synthetic */ int f$1;
//                    private final /* synthetic */ int f$2;
//
//                    {
//                        this.f$1 = r2;
//                        this.f$2 = r3;
//                    }
//
//                    public final void run() {
//                        VNMainActivity.C07372.this.lambda$messageCallback$0$VNMainActivity$2(this.f$1, this.f$2);
//                    }
//                });
            }
        }

        public /* synthetic */ void lambda$messageCallback$0$VNMainActivity$2(int i, int i2) {
            VNMainActivity.this.serviceCallBackMsgHandler(i, i2);
        }
    };
    private final ServiceConnection mIServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.m26i(VNMainActivity.TAG, "onServiceConnected");
            Trace.beginSection("VNActivity.onServiceConnected");
            IVoiceNoteService unused = VNMainActivity.this.mIService = IVoiceNoteService.Stub.asInterface(iBinder);
            if (VNMainActivity.this.mIService == null) {
                Log.m32w(VNMainActivity.TAG, "onServiceConnected - mIService is null");
                return;
            }
            try {
                VNMainActivity.this.mIService.hideNotification();
                VNMainActivity.this.mIService.registerCallback(VNMainActivity.this.mIServiceCallback);
                if (!Engine.getInstance().isWiredHeadSetConnected() && !Engine.getInstance().isBluetoothSCOConnected()) {
                    DialogFactory.clearDialogByTag(VNMainActivity.this.getSupportFragmentManager(), DialogFactory.MODE_NOT_SUPPORTED);
                }
            } catch (RemoteException e) {
                Log.m24e(VNMainActivity.TAG, "hideNotification RemoteException - ", (Throwable) e);
            }
            Trace.endSection();
        }

        public void onServiceDisconnected(ComponentName componentName) {
            Log.m26i(VNMainActivity.TAG, "onServiceDisconnected");
            try {
                VNMainActivity.this.mIService.unregisterCallback(VNMainActivity.this.mIServiceCallback);
            } catch (RemoteException e) {
                Log.m24e(VNMainActivity.TAG, "unregisterCallback RemoteException - ", (Throwable) e);
            }
            IVoiceNoteService unused = VNMainActivity.this.mIService = null;
        }
    };
    public boolean mIsEnableNavigationBar = false;
    private boolean mIsHWKeyboardChecked = false;
    private boolean mIsResumed = false;
    private Menu mMenu = null;
    private NavigationBarProvider mNavigationbarProvider;
    /* access modifiers changed from: private */
    public VoiceNoteObservable mObservable;
    private DialogFragment mSDDialog = null;
    private int mScene = 1;

    public static void init() {
    }

    private static class MainHandler extends Handler {
        WeakReference<VNMainActivity> mWeakRef;

        MainHandler(VNMainActivity vNMainActivity) {
            this.mWeakRef = new WeakReference<>(vNMainActivity);
        }

        public void handleMessage(Message message) {
            VNMainActivity vNMainActivity = (VNMainActivity) this.mWeakRef.get();
            if (vNMainActivity != null) {
                switch (message.what) {
                    case 1:
                        boolean unused = vNMainActivity.mBackKeyLock = false;
                        break;
                    case 2:
                        Engine.getInstance().cancelRecord();
                        Engine.getInstance().stopPlay();
                        vNMainActivity.mObservable.notifyObservers(Integer.valueOf(Event.RECORD_START_BY_SVOICE));
                        break;
                    case 3:
                        vNMainActivity.mObservable.notifyObservers(Integer.valueOf(Event.RECORD_START_BY_TASK_EDGE));
                        break;
                    case 4:
                        vNMainActivity.mObservable.notifyObservers(Integer.valueOf(Event.RECORD_BY_LEVEL_ACTIVEKEY));
                        break;
                    case 5:
                        if (vNMainActivity.isActivityResumed()) {
//                            if (Settings.getStringSettings(Settings.KEY_BIXBY_START_DATA).equals(BixbyConstant.BixbyStartMode.BIXBY_START_RECORD)) {
//                                vNMainActivity.mObservable.notifyObservers(Integer.valueOf(Event.BIXBY_START_RECORDING));
//                                break;
//                            }
                        } else {
                            removeMessages(5);
                            sendEmptyMessageDelayed(5, 50);
                            break;
                        }
                        break;
                    case 6:
                        boolean unused2 = vNMainActivity.mClickableBackKey = true;
                        break;
                    case 7:
                        vNMainActivity.mObservable.notifyObservers(Integer.valueOf(Event.BLE_SPEN_RECORD_PAUSE_RESUME));
                        break;
                    case 8:
                        vNMainActivity.mObservable.notifyObservers(Integer.valueOf(Event.BLE_SPEN_RECORD_START));
                        break;
                }
                super.handleMessage(message);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        Log.m26i(TAG, "onCreate - hash code : " + hashCode());
        Trace.beginSection("VNActivity.onCreate");
        getWindow().requestFeature(8);
        super.onCreate(bundle);
        initRecordingFileNumber();
        initBatchForOnCreate();
        if (getResources().getConfiguration().orientation == 2 && DisplayManager.getMultiwindowMode() != 2) {
            getWindow().setFlags(1024, 1024);
        }
        Trace.beginSection("VNActivity.setContentView");
        int identifyOrientationForInflatingLayout = DisplayManager.identifyOrientationForInflatingLayout(this);
        if (identifyOrientationForInflatingLayout == 1 || identifyOrientationForInflatingLayout == 3) {
            Log.m19d(TAG, "Inflating Activity Layout LAND");
            setContentView((int) C0690R.layout.activity_main_land);
        } else {
            Log.m19d(TAG, "Inflating Activity Layout PORT");
            setContentView((int) C0690R.layout.activity_main);
        }
        DisplayManager.setVROrientation(identifyOrientationForInflatingLayout);
        Trace.endSection();
        setVolumeControlStream(3);
        ModePager.getInstance().setContext(this);
        if (bundle == null) {
            this.mActionbar = new MainActionbar(this);
        } else {
            Log.m32w(TAG, "onCreate - has previous data");
            this.mScene = bundle.getInt("scene");
            if (!FragmentController.hasInstance()) {
                Log.m32w(TAG, "onCreate - fragment controller lost all fragment data !!");
                int i = this.mScene;
                if (i == 5 || i == 10) {
                    DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.RENAME_DIALOG);
                    DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.DELETE_DIALOG);
                }
                this.mScene = 1;
                FragmentController.removeAllFragments(this);
            }
            int i2 = this.mScene;
            if (i2 == 5 || i2 == 10 || i2 == 7) {
                this.mActionbar = new MainActionbar(this, this.mScene, bundle);
            } else {
                this.mActionbar = new MainActionbar(this, i2, (Bundle) null);
            }
        }
        this.mFragmentController = FragmentController.getInstance();
        this.mFragmentController.setContext(this);
        this.mFragmentController.registerSceneChangeListener(this);
        this.mFragmentController.setNeedUpdateLayout(true);
        this.mGlobalSettingObserver = new GlobalSettingsObserver(new Handler(Looper.getMainLooper()));
        getContentResolver().registerContentObserver(Settings.Global.CONTENT_URI, true, this.mGlobalSettingObserver);
        this.mObservable = VoiceNoteObservable.getInstance();
        this.mObservable.addObserver(this);
        this.mObservable.notifyObservers(Integer.valueOf(VoiceNoteApplication.restoreEvent()));
        Intent intent = getIntent();
        if (fromEdge(intent)) {
            this.mFromEdge = true;
        } else if (fromLevelFlex(intent)) {
            handleLevelFlexIntent(intent.getExtras());
        } else if (fromBixby(intent)) {
            handleBixbyIntent(intent.getExtras());
            intent.removeExtra(BixbyConstant.BixbyStartMode.BIXBY_START_DATA);
        }
        if (BackgroundRestrictHelper.isVoiceRecorderAddedToSleepingApp() && !VoiceNoteFeature.isChina() && !DialogFactory.isDialogVisible(getSupportFragmentManager(), DialogFactory.SLEEPING_APP_WARNING)) {
            DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.SLEEPING_APP_WARNING);
            DialogFactory.show(getSupportFragmentManager(), DialogFactory.SLEEPING_APP_WARNING, (Bundle) null);
        }
        this.mNavigationbarProvider = NavigationBarProvider.getInstance();
        if (this.mNavigationbarProvider.isDeviceSupportSoftNavigationBar()) {
            this.mIsEnableNavigationBar = this.mNavigationbarProvider.isNavigationBarEnabled();
            this.mNavigationbarProvider.setOnSystemUiVisibilityChangeListener(this);
        }
        CursorProvider.getInstance().registerContentObservers();
        if (isInMultiWindowMode()) {
            DisplayManager.updateWindowSize(this);
            DisplayManager.updateDeviceOrientation();
        }
        Trace.endSection();
        DeviceInfo.logDeviceInfo(this, getResources().getConfiguration());
        DisplayManager.logDisplayInfo(this, getResources().getConfiguration());
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.m26i(TAG, "onNewIntent");
        setIntent(intent);
        if (fromActionSearch(intent)) {
            this.mFromActionSearch = true;
            this.mObservable.notifyObservers(Integer.valueOf(Event.SEARCH_VOICE_INPUT));
        } else if (fromEdge(intent)) {
            this.mFromEdge = true;
        } else if (fromLevelFlex(intent)) {
            handleLevelFlexIntent(intent.getExtras());
        } else if (fromBixby(intent)) {
            handleBixbyIntent(intent.getExtras());
        }
        if (BackgroundRestrictHelper.isVoiceRecorderAddedToSleepingApp() && !VoiceNoteFeature.isChina() && !DialogFactory.isDialogVisible(getSupportFragmentManager(), DialogFactory.SLEEPING_APP_WARNING)) {
            DialogFactory.show(getSupportFragmentManager(), DialogFactory.SLEEPING_APP_WARNING, (Bundle) null);
        }
        ModePager.getInstance().setContext(this);
    }

    /* access modifiers changed from: protected */
    public void onRestart() {
        Log.m26i(TAG, "onRestart");
        super.onRestart();
        VoiceNoteObservable voiceNoteObservable = this.mObservable;
        if (voiceNoteObservable != null) {
            voiceNoteObservable.notifyObservers(Integer.valueOf(VoiceNoteApplication.restoreEvent()));
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        Log.m26i(TAG, "onStart - hash code : " + hashCode());
        Trace.beginSection("VNActivity.onStart");
        super.onStart();
        if (this.mNavigationbarProvider.isDeviceSupportSoftNavigationBar() && this.mNavigationbarProvider.isNavigationBarChanged()) {
            this.mObservable.notifyObservers(15);
        }
        if (!this.mFinish) {
            VoiceNoteService.Helper.bindToService(this, this.mIServiceConnection);
            Trace.endSection();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        int i;
        Handler handler;
        Log.m26i(TAG, "onResume");
        Trace.beginSection("VNActivity.onResume");
        super.onResume();
        if (!this.mFinish) {
            this.mIsResumed = true;
            this.mBackKeyLock = false;
            this.mClickableBackKey = true;
            if (PermissionProvider.isStorageAccessEnable(this) && com.sec.android.app.voicenote.provider.Settings.getBooleanSettings(com.sec.android.app.voicenote.provider.Settings.KEY_FIRST_LAUNCH, true)) {
                if (VoiceNoteFeature.FLAG_SUPPORT_DATA_CHECK_POPUP && !DialogFactory.isDialogVisible(getSupportFragmentManager(), DialogFactory.DATA_CHECK_DIALOG)) {
                    Log.m26i(TAG, "showDataCheckDialog module: 1");
                    Bundle bundle = new Bundle();
                    bundle.putInt(DialogFactory.BUNDLE_REQUEST_CODE, 8);
                    DialogFactory.show(getSupportFragmentManager(), DialogFactory.DATA_CHECK_DIALOG, bundle, (DialogFactory.DialogResultListener) null);
                }
                DBProvider.getInstance().initCategoryID();
            }
            if (PermissionProvider.checkPermission(this, (ArrayList<Integer>) null, false)) {
                if (DialogFactory.isDialogVisible(getSupportFragmentManager(), DialogFactory.PERMISSION_DIALOG)) {
                    DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.PERMISSION_DIALOG);
                }
                if (!startRecordingFromSvoice() && Engine.getInstance().getRecorderState() == 1) {
                    if (this.mCheckSDCardAction == -1) {
                        this.mCheckSDCardAction = checkSDCard();
                        Log.m26i(TAG, "SDCard checked in FG Main Thread: " + this.mCheckSDCardAction);
                        handleSDCardDialog(this.mCheckSDCardAction);
                    } else {
                        Log.m26i(TAG, "SDCard checked in BG");
                        handleSDCardDialog(this.mCheckSDCardAction);
                    }
                }
                startListFromPrivateBox();
                if (!this.mFromActionSearch) {
                    this.mFromActionSearch = false;
                    CursorProvider.getInstance().reload(getSupportLoaderManager());
                }
                if (checkClearTempFiles() && Engine.getInstance().restoreTempFile()) {
                    this.mObservable.notifyObservers(2);
                }
                if (this.mFromEdge) {
                    if (Engine.getInstance().getRecorderState() == 1 && Engine.getInstance().getPlayerState() == 1 && (handler = this.mHandler) != null) {
                        handler.removeMessages(0);
                        if (VoiceNoteApplication.getScene() == 2) {
                            this.mObservable.notifyObservers(4);
                        }
                        if (!DialogFactory.isDialogVisible(getSupportFragmentManager(), DialogFactory.SD_CARD_SELECT_DIALOG)) {
                            this.mHandler.sendEmptyMessageDelayed(3, 400);
                        }
                    }
                    this.mFromEdge = false;
                }
            }
            if (HWKeyboardProvider.isHWKeyboard(this)) {
                if (!this.mIsHWKeyboardChecked) {
                    this.mObservable.notifyObservers(11);
                }
                this.mIsHWKeyboardChecked = true;
            } else {
                this.mIsHWKeyboardChecked = false;
            }
            if (HWKeyboardProvider.isDeviceHasHardKeyboard(this)) {
                HWKeyboardProvider.setDeviceHasHardkeyboard(true);
            }
            this.mActionbar.onResume();
            if (Network.isNetworkConnected(getApplicationContext()) && DialogFactory.isDialogVisible(getSupportFragmentManager(), DialogFactory.NETWORK_NOT_CONNECTED)) {
                DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.NETWORK_NOT_CONNECTED);
            }
            if (!Network.isNetworkConnected(getApplicationContext()) && (((i = this.mScene) == 2 || i == 4 || i == 1) && DialogFactory.isDialogVisible(getSupportFragmentManager(), DialogFactory.NETWORK_NOT_CONNECTED))) {
                DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.NETWORK_NOT_CONNECTED);
            }
            if (!BackgroundRestrictHelper.isVoiceRecorderAddedToSleepingApp() && DialogFactory.isDialogVisible(getSupportFragmentManager(), DialogFactory.SLEEPING_APP_WARNING)) {
                DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.SLEEPING_APP_WARNING);
            }
            if (this.mFragmentController.isNeedUpdateLayout()) {
                this.mFragmentController.setNeedUpdateLayout(false);
            }
            this.mObservable.notifyObservers(Integer.valueOf(Event.BIXBY_READY_TO_START_RECORDING));
            Trace.endSection();
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        Log.m26i(TAG, "onPause isFinishing : " + isFinishing());
        this.mIsResumed = false;
        super.onPause();
//        ((InputMethodManager) getApplicationContext().getSystemService("input_method")).semForceHideSoftInput();
        if (!"mounted".equals(StorageProvider.getExternalStorageStateSd())) {
            com.sec.android.app.voicenote.provider.Settings.setSettings(com.sec.android.app.voicenote.provider.Settings.KEY_SDCARD_PREVIOUS_STATE, 0);
            com.sec.android.app.voicenote.provider.Settings.setSettings(com.sec.android.app.voicenote.provider.Settings.KEY_STORAGE, 0);
        }
    }

    /* access modifiers changed from: protected */
    public void onPostResume() {
        Trace.beginSection("VNActivity.onPostResume");
        super.onPostResume();
        Trace.endSection();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        Log.m26i(TAG, "onStop");
        boolean z = false;
        this.mBackKeyLock = false;
        this.mClickableBackKey = true;
        int playerState = Engine.getInstance().getPlayerState();
        boolean z2 = Engine.getInstance().getRecorderState() == 1 && (playerState == 3 || playerState == 4);
        if (!KeyguardManagerHelper.isKeyguardLockedBySecure() || !z2) {
            z = true;
        }
        if (DialogFactory.isDialogVisible(getSupportFragmentManager(), DialogFactory.PERMISSION_DIALOG)) {
            DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.PERMISSION_DIALOG);
        }
        try {
            if (this.mIService != null) {
                if (z) {
                    this.mIService.showNotification();
                }
                this.mIService.unregisterCallback(this.mIServiceCallback);
                this.mIService = null;
            }
        } catch (RemoteException e) {
            Log.m24e(TAG, "showNotification RemoteException - ", (Throwable) e);
        }
        VoiceNoteService.Helper.unbindFromService(this);
        if (this.mScene == 8 && playerState == 4) {
            VoiceNoteApplication.saveEvent(Event.PLAY_PAUSE);
        }
        if (checkClearTempFiles()) {
            StorageProvider.clearTempFiles();
        }
        super.onStop();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        Log.m26i(TAG, "onDestroy - hash code : " + hashCode());
        Log.m26i(TAG, VoiceNoteApplication.getApkInfo());
        Intent intent = getIntent();
        if (intent != null) {
            intent.setAction("");
        }
        MainActionbar mainActionbar = this.mActionbar;
        if (mainActionbar != null) {
            mainActionbar.onDestroy();
            this.mActionbar = null;
        }
        NavigationBarProvider navigationBarProvider = this.mNavigationbarProvider;
        if (navigationBarProvider != null) {
            navigationBarProvider.onDestroy();
        }
        if (!isChangingConfigurations()) {
            DialogFactory.clearAllDialog(getSupportFragmentManager());
        }
        FragmentController fragmentController = this.mFragmentController;
        if (fragmentController != null) {
            fragmentController.unregisterSceneChangeListener(this);
            this.mFragmentController.onDestroy();
            this.mFragmentController = null;
        }
        this.mObservable.notifyObservers(23);
        VoiceNoteObservable voiceNoteObservable = this.mObservable;
        if (voiceNoteObservable != null) {
            voiceNoteObservable.deleteObserver(this);
            this.mObservable = null;
        }
        if (this.mGlobalSettingObserver != null) {
            getContentResolver().unregisterContentObserver(this.mGlobalSettingObserver);
            this.mGlobalSettingObserver = null;
        }
        ModePager.getInstance().onDestroy(hashCode());
        BluetoothHelper.getInstance().onDestroy();
        TypefaceProvider.clearAll();
        CursorProvider.getInstance().unregisterContentObservers();
        this.mCheckSDCardAction = -1;
        clearMenu();
        TrashHelper.getInstance().onDestroy(isChangingConfigurations());
        super.onDestroy();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        Log.m26i(TAG, "onConfigurationChanged : " + HWKeyboardProvider.isHWKeyboard(this));
        if (isInMultiWindowMode()) {
            if (DisplayManager.isDeviceOrientationChanged()) {
                Log.m26i(TAG, "onConfigurationChanged : DeviceOrientationChanged - ReCREATE");
                recreate();
            } else if (DisplayManager.isMultiWindowSizeChanged(this)) {
                if (DisplayManager.windowWidthReachThreshold(this)) {
                    recreate();
                } else {
                    Log.m19d(TAG, "onConfigurationChanged : MAIN_MULTIWINDOW_SIZE_CHANGE");
                    this.mObservable.notifyObservers(21);
                }
                DisplayManager.updateWindowSize(this);
            }
        } else if (!VoiceNoteFeature.FLAG_IS_WINNER()) {
            if (HWKeyboardProvider.isDeviceHasHardKeyboard(this)) {
                HWKeyboardProvider.setDeviceHasHardkeyboard(true);
            }
            if (HWKeyboardProvider.getDeviceHasHardkeyboard()) {
                getWindow().setAttributes(getWindow().getAttributes());
            } else if (HWKeyboardProvider.isHWKeyboard(this)) {
                this.mObservable.notifyObservers(11);
            } else if (this.mIsHWKeyboardChecked) {
                this.mObservable.notifyObservers(12);
            }
        }
    }

    private void initBatchForOnCreate() {
        new Thread(new Runnable() {
            public final void run() {
                VNMainActivity.this.lambda$initBatchForOnCreate$0$VNMainActivity();
            }
        }).start();
        if (DesktopModeProvider.isDesktopMode() && com.sec.android.app.voicenote.provider.Settings.getIntSettings("record_mode", 1) != 6) {
            com.sec.android.app.voicenote.provider.Settings.setSettings("record_mode", 1);
        }
        checkRecQuality();
        if (VoiceNoteService.Helper.isConnectedContext(this)) {
            Log.m26i(TAG, "finish activity !!!");
            this.mFinish = true;
            finish();
        }
    }

    public /* synthetic */ void lambda$initBatchForOnCreate$0$VNMainActivity() {
        this.mCheckSDCardAction = checkSDCard();
        Log.m26i(TAG, "CheckedSDCardInBG: " + this.mCheckSDCardAction);
        TrashHelper.getInstance().checkFileInTrashToDelete();
    }

    private void initRecordingFileNumber() {
        new Thread($$Lambda$VNMainActivity$OPXFH7xa5ncJmnOAj2bkuLO6p78.INSTANCE).start();
    }

    public void update(Observable observable, Object obj) {
        int intValue = ((Integer) obj).intValue();
        Log.m26i(TAG, "update - data : " + intValue);
        if (Event.isConvertibleEvent(intValue)) {
            this.mBackKeyLock = true;
            Handler handler = this.mHandler;
            if (handler != null) {
                handler.removeMessages(0);
                this.mHandler.sendEmptyMessageDelayed(1, 700);
            }
        }
        switch (intValue) {
            case 4:
                if (DialogFactory.isDialogVisible(getSupportFragmentManager(), DialogFactory.TRANSLATION_CANCEL_DIALOG)) {
                    DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.TRANSLATION_CANCEL_DIALOG);
                    return;
                }
                return;
            case 11:
            case 12:
            case 21:
                ModePager.getInstance().updateTabSelected();
                return;
            case 18:
                if (this.mIsResumed) {
                    refresh();
                    return;
                }
                return;
            case Event.TRASH_DESELECT_ALL:
            case Event.TRASH_SELECT_ALL:
            case Event.DESELECT_ALL:
            case Event.SELECT_ALL:
            case Event.SELECT:
            case Event.DELETE_COMPLETE:
                invalidateOptionsMenu();
                return;
            case Event.HIDE_EDIT_PROGRESS_DIALOG:
                DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.EDIT_PROGRESS_DIALOG);
                return;
            case 1001:
            case Event.EDIT_RECORD:
                if (VoiceNoteFeature.FLAG_SUPPORT_SHOW_REJECT_CALL_NUMBER) {
                    CallRejectChecker.getInstance().resetRejectCallCount();
                    return;
                }
                return;
            case 1004:
            case 1006:
                if (SecureFolderProvider.isSecureFolderSupported()) {
                    SecureFolderProvider.getKnoxMenuList(this);
                    if (SecureFolderProvider.isInsideSecureFolder()) {
                        return;
                    }
                }
                if (VoiceNoteFeature.FLAG_SUPPORT_SHOW_REJECT_CALL_NUMBER && CallRejectChecker.getInstance().getRejectCallCount() != 0 && !DialogFactory.isDialogVisible(getSupportFragmentManager(), DialogFactory.REJECT_CALL_INFO_DIALOG)) {
                    DialogFactory.show(getSupportFragmentManager(), DialogFactory.REJECT_CALL_INFO_DIALOG, (Bundle) null);
                }
                if (com.sec.android.app.voicenote.provider.Settings.getStringSettings(com.sec.android.app.voicenote.provider.Settings.KEY_BIXBY_START_DATA) != null) {
                    com.sec.android.app.voicenote.provider.Settings.setSettings(com.sec.android.app.voicenote.provider.Settings.KEY_BIXBY_START_DATA, (String) null);
                    return;
                }
                return;
            case Event.RECORD_RELEASE_MEDIASESSION:
                if (Engine.getInstance().ismIsNeedReleaseMediaSession()) {
                    MediaSessionManager.getInstance().createMediaSession();
                    Engine.getInstance().setmIsNeedReleaseMediaSession(false);
                    return;
                }
                return;
            case Event.TRANSLATION_START:
                startCover(this.mScene);
                return;
            case Event.TRANSLATION_NETWORK_DIALOG:
                if (!DialogFactory.isDialogVisible(getSupportFragmentManager(), DialogFactory.NETWORK_NOT_CONNECTED)) {
                    DialogFactory.show(getSupportFragmentManager(), DialogFactory.NETWORK_NOT_CONNECTED, (Bundle) null);
                    return;
                }
                return;
            case Event.TRANSLATION_FILE_PLAY:
                DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.RENAME_DIALOG);
                DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.TRANSLATION_CANCEL_DIALOG);
                if (PhoneStateProvider.getInstance().isCallIdle(this)) {
                    Engine.getInstance().startPlay(Engine.getInstance().getLastSavedFilePath());
                    this.mObservable.notifyObservers(Integer.valueOf(Event.PLAY_START));
                    return;
                }
                this.mObservable.notifyObservers(3);
                return;
            case Event.BIXBY_START_PLAYING:
                BixbyHelper.getInstance().startPlayTask();
                return;
            default:
                return;
        }
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        Log.m26i(TAG, "dispatchKeyEvent");
        if (DesktopModeProvider.isDesktopMode()) {
            MouseKeyboardProvider.getInstance().keyEventInteraction(this, keyEvent, this.mScene);
        }
        if (keyEvent.getKeyCode() != 84 && (!keyEvent.isCtrlPressed() || keyEvent.getKeyCode() != 34)) {
            return super.dispatchKeyEvent(keyEvent);
        }
        if (this.mScene == 7 || this.mObservable == null || CursorProvider.getInstance().getItemCount() <= 0) {
            return true;
        }
        this.mObservable.notifyObservers(Integer.valueOf(Event.START_SEARCH));
        SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_SEARCH, -1);
        return true;
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        Log.m26i(TAG, "onKeyDown - keyCode : " + i + " - scene : " + this.mScene);
        if (VoiceNoteFeature.FLAG_SUPPORT_BLE_SPEN_AIR_ACTION && i == 130) {
            int i2 = this.mScene;
            if (i2 != 1) {
                if (i2 == 8) {
                    if (this.mHandler.hasMessages(7)) {
                        this.mHandler.removeMessages(7);
                    } else {
                        this.mHandler.sendEmptyMessageDelayed(7, 300);
                    }
                }
            } else if (this.mHandler.hasMessages(8)) {
                this.mHandler.removeMessages(8);
            } else {
                this.mHandler.sendEmptyMessageDelayed(8, 300);
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    public void onBackPressed() {
        Log.m26i(TAG, "onBackPressed");
        if (!isDestroyed() && !isFinishing()) {
            if (this.mBackKeyLock) {
                Log.m29v(TAG, "back key is not allowed");
            } else if (!this.mClickableBackKey) {
                Log.m29v(TAG, "back key is pressed so fast (< 100ms)");
            } else {
                this.mClickableBackKey = false;
                Handler handler = this.mHandler;
                if (handler != null) {
                    handler.removeMessages(6);
                    this.mHandler.sendEmptyMessageDelayed(6, 100);
                }
                MainActionbar mainActionbar = this.mActionbar;
                if (mainActionbar == null || !mainActionbar.onBackKeyPressed()) {
                    FragmentController fragmentController = this.mFragmentController;
                    if (fragmentController == null || !fragmentController.onBackKeyPressed()) {
                        super.onBackPressed();
                    }
                }
            }
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.m26i(TAG, "onPrepareOptionsMenu");
        Trace.beginSection("VNActivity.onPrepareOptionsMenu");
        this.mMenu = menu;
        MainActionbar mainActionbar = this.mActionbar;
        if (mainActionbar != null) {
            mainActionbar.prepareMenu(menu, this.mScene, this);
        }
        boolean onPrepareOptionsMenu = super.onPrepareOptionsMenu(menu);
        Trace.endSection();
        return onPrepareOptionsMenu;
    }

    public void invalidateOptionsMenu() {
        Log.m26i(TAG, "invalidateOptionsMenu");
        MainActionbar mainActionbar = this.mActionbar;
        if (mainActionbar != null) {
            mainActionbar.invalidateOptionMenu();
        }
        super.invalidateOptionsMenu();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        Log.m26i(TAG, "onOptionsItemSelected : " + menuItem.getTitle());
        MainActionbar mainActionbar = this.mActionbar;
        if (mainActionbar != null) {
            mainActionbar.selectOption(menuItem.getItemId(), this);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onSceneChange(int i) {
        Log.m26i(TAG, "onSceneChange - scene : " + i);
        this.mScene = i;
        VoiceNoteApplication.saveScene(i);
        Engine.getInstance().resetPauseNotByUser();
        int i2 = this.mScene;
        if (i2 == 2) {
            DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.MODE_NOT_SUPPORTED);
        } else if (i2 == 4) {
            startCover(i2);
        } else if (i2 == 12) {
            RemoteViewManager.getInstance().hide(0);
        }
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt("scene", this.mScene);
        bundle.putInt(LAYOUT_DIRECTION, getResources().getConfiguration().getLayoutDirection());
        this.mActionbar.onSaveInstanceState(bundle);
        super.onSaveInstanceState(bundle);
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        Log.m26i(TAG, "onRequestPermissionsResult - requestCode : " + i);
        if (strArr.length == 0 || iArr.length == 0) {
            Log.m26i(TAG, "onRequestPermissionsResult - permissions or grantResults size is zero");
        } else if (i == 1) {
            com.sec.android.app.voicenote.provider.Settings.setSettings(com.sec.android.app.voicenote.provider.Settings.KEY_FORCE_SYSTEM_PERMISSION_DIALOG, false);
            int length = iArr.length;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    break;
                } else if (iArr[i2] != 0) {
                    Engine.getInstance().cancelRecord();
                    Engine.getInstance().stopPlay(false);
                    VoiceNoteApplication.saveEvent(4);
                    VoiceNoteApplication.saveScene(0);
                    VoiceNoteObservable.getInstance().notifyObservers(4);
                    finish();
                    break;
                } else {
                    i2++;
                }
            }
            if (PermissionProvider.isStorageAccessEnable(this) && com.sec.android.app.voicenote.provider.Settings.getBooleanSettings(com.sec.android.app.voicenote.provider.Settings.KEY_FIRST_LAUNCH, true)) {
                DBProvider.getInstance().initCategoryID();
            }
            this.mObservable.notifyObservers(2);
            handleBixbyAfterGrantPermission();
        } else if (i == 2) {
            com.sec.android.app.voicenote.provider.Settings.setSettings(com.sec.android.app.voicenote.provider.Settings.KEY_FORCE_SYSTEM_PERMISSION_DIALOG, false);
            if (iArr[0] != 0) {
                return;
            }
            if (Engine.getInstance().getRecorderState() == 3) {
                this.mObservable.notifyObservers(Integer.valueOf(Event.RECORD_RESUME_BY_PERMISSION));
            } else {
                this.mObservable.notifyObservers(Integer.valueOf(Event.RECORD_START_BY_PERMISSION));
            }
        } else if (i == 3) {
            com.sec.android.app.voicenote.provider.Settings.setSettings(com.sec.android.app.voicenote.provider.Settings.KEY_FORCE_SYSTEM_PERMISSION_DIALOG, false);
            if (iArr[0] == 0) {
                this.mObservable.notifyObservers(Integer.valueOf(Event.EDIT_RECORD_BY_PERMISSION));
            }
        } else if (i == 4) {
            com.sec.android.app.voicenote.provider.Settings.setSettings(com.sec.android.app.voicenote.provider.Settings.KEY_FORCE_SYSTEM_PERMISSION_DIALOG_PHONE, false);
            if (iArr[0] == 0) {
                com.sec.android.app.voicenote.provider.Settings.setSettings(com.sec.android.app.voicenote.provider.Settings.KEY_REC_CALL_REJECT, true);
                CallRejectChecker.getInstance().setReject(true);
                this.mObservable.notifyObservers(Integer.valueOf(Event.RECORD_CALL_REJECT));
                SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_REJECT_CALL, -1);
                return;
            }
            CallRejectChecker.getInstance().setReject(false);
            this.mObservable.notifyObservers(Integer.valueOf(Event.RECORD_CALL_ALLOW));
        } else if (i != 5) {
            super.onRequestPermissionsResult(i, strArr, iArr);
        } else {
            com.sec.android.app.voicenote.provider.Settings.setSettings(com.sec.android.app.voicenote.provider.Settings.KEY_FORCE_SYSTEM_PERMISSION_DIALOG_PHONE, false);
            if (iArr[0] == 0) {
                this.mActionbar.selectOption(C0690R.C0693id.option_write_to_nfc_tag, this);
            }
        }
    }

    private void handleBixbyAfterGrantPermission() {
        Handler handler;
        String stringSettings = com.sec.android.app.voicenote.provider.Settings.getStringSettings(com.sec.android.app.voicenote.provider.Settings.KEY_BIXBY_START_DATA);
        if (stringSettings != null) {
            char c = 65535;
            int hashCode = stringSettings.hashCode();
            if (hashCode != -141876981) {
                if (hashCode == 1421026926 && stringSettings.equals(BixbyConstant.BixbyStartMode.BIXBY_START_PLAY)) {
                    c = 0;
                }
            } else if (stringSettings.equals(BixbyConstant.BixbyStartMode.BIXBY_START_RECORD)) {
                c = 1;
            }
            if (c == 0) {
                String stringSettings2 = com.sec.android.app.voicenote.provider.Settings.getStringSettings(BixbyConstant.InputParameter.FILE_NAME_ID);
                Log.m26i(TAG, "handleBixbyIntent - file name id" + stringSettings2);
                try {
                    BixbyHelper.getInstance().setBixbyPlayHelperContext(getApplicationContext(), Long.parseLong(stringSettings2));
                    this.mObservable.notifyObservers(Integer.valueOf(Event.BIXBY_START_PLAYING));
                } catch (NumberFormatException e) {
                    Log.m24e(TAG, "NumberFormatException", (Throwable) e);
                }
            } else if (c == 1 && (handler = this.mHandler) != null) {
                handler.removeMessages(5);
                this.mHandler.sendEmptyMessageDelayed(5, 50);
            }
        }
    }

    /* access modifiers changed from: private */
    public void serviceCallBackMsgHandler(int i, int i2) {
        int i3;
        String path;
        int i4 = i;
        int i5 = i2;
        if (i4 == 1) {
            if (this.mScene == 6) {
                i3 = com.sec.android.app.voicenote.provider.Settings.getIntSettings(com.sec.android.app.voicenote.provider.Settings.KEY_PLAY_MODE, 1);
            } else {
                i3 = com.sec.android.app.voicenote.provider.Settings.getIntSettings("record_mode", 1);
            }
            if (i3 != 4 || RemoteViewManager.isRunning()) {
                this.mObservable.notifyObservers(1004);
            } else {
                this.mObservable.notifyObservers(Integer.valueOf(Event.RECORD_STOP_DELAYED));
            }
            DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.EDIT_SAVE_DIALOG);
            DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.EDIT_CANCEL_DIALOG);
            DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.RECORD_CANCEL_DIALOG);
            DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.RENAME_DIALOG);
            DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.CATEGORY_RENAME);
            DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.MODE_NOT_SUPPORTED);
        } else if (i4 == 2) {
            this.mObservable.notifyObservers(1006);
            DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.EDIT_SAVE_DIALOG);
            DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.EDIT_CANCEL_DIALOG);
            DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.RECORD_CANCEL_DIALOG);
            DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.RENAME_DIALOG);
            DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.CATEGORY_RENAME);
            DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.MODE_NOT_SUPPORTED);
        } else if (i4 == 3) {
            int i6 = this.mScene;
            if (i6 != 6) {
                if (i6 == 8) {
                    this.mObservable.notifyObservers(1002);
                }
            } else if (i5 == 1021 || i5 == 1022) {
                this.mActionbar.selectOption(C0690R.C0693id.option_edit_save, this);
            } else {
                this.mObservable.notifyObservers(Integer.valueOf(Event.EDIT_RECORD_PAUSE));
            }
        } else if (i4 == 4) {
            int i7 = this.mScene;
            if (i7 == 6) {
                this.mObservable.notifyObservers(Integer.valueOf(Event.EDIT_RECORD));
            } else if (i7 == 8) {
                this.mObservable.notifyObservers(1003);
            }
        } else if (i4 != 41) {
            if (i4 == 51) {
                this.mObservable.notifyObservers(Integer.valueOf(Event.HIDE_DIALOG));
            } else if (i4 != 63) {
                switch (i4) {
                    case 11:
                        int i8 = this.mScene;
                        if (i8 == 13 || i8 == 15) {
                            this.mObservable.notifyObservers(Integer.valueOf(Event.OPEN_TRASH));
                            return;
                        } else {
                            this.mObservable.notifyObservers(3);
                            return;
                        }
                    case 12:
                        int i9 = this.mScene;
                        if (i9 == 3) {
                            this.mObservable.notifyObservers(Integer.valueOf(Event.MINI_PLAY_NEXT));
                            return;
                        } else if (i9 == 4) {
                            this.mObservable.notifyObservers(Integer.valueOf(Event.PLAY_NEXT));
                            this.mObservable.notifyObservers(Integer.valueOf(Event.UPDATE_FILE_NAME));
                            return;
                        } else if (i9 == 7) {
                            this.mObservable.notifyObservers(2);
                            return;
                        } else {
                            return;
                        }
                    case 13:
                        int i10 = this.mScene;
                        if (i10 == 3) {
                            this.mObservable.notifyObservers(Integer.valueOf(Event.MINI_PLAY_PREV));
                            return;
                        } else if (i10 == 4) {
                            this.mObservable.notifyObservers(Integer.valueOf(Event.PLAY_PREV));
                            this.mObservable.notifyObservers(Integer.valueOf(Event.UPDATE_FILE_NAME));
                            return;
                        } else if (i10 == 7) {
                            this.mObservable.notifyObservers(2);
                            return;
                        } else {
                            return;
                        }
                    case 14:
                        if (Decoder.getInstance().getTranslationState() == 1) {
                            this.mObservable.notifyObservers(Integer.valueOf(VoiceNoteApplication.convertEvent(Event.PLAY_PAUSE)));
                            return;
                        } else {
                            this.mObservable.notifyObservers(Integer.valueOf(Event.TRANSLATION_PAUSE));
                            return;
                        }
                    case 15:
                        if (Decoder.getInstance().getTranslationState() == 1) {
                            this.mObservable.notifyObservers(Integer.valueOf(VoiceNoteApplication.convertEvent(Event.PLAY_PAUSE)));
                            return;
                        } else {
                            this.mObservable.notifyObservers(Integer.valueOf(Event.TRANSLATION_PAUSE));
                            return;
                        }
                    case 16:
                        if (Decoder.getInstance().getTranslationState() == 1) {
                            this.mObservable.notifyObservers(Integer.valueOf(VoiceNoteApplication.convertEvent(Event.PLAY_RESUME)));
                            return;
                        } else {
                            this.mObservable.notifyObservers(Integer.valueOf(Event.TRANSLATION_RESUME));
                            return;
                        }
                    default:
                        switch (i4) {
                            case 31:
                                Log.m19d(TAG, "SD_MOUNT ");
                                if ("mounted".equals(StorageProvider.getExternalStorageStateSd()) && this.mIsResumed && !AndroidForWork.getInstance().isAndroidForWorkMode(this)) {
                                    new VNMediaScanner(this).startScan(StorageProvider.getRootPath(1));
                                    if (!DialogFactory.isDialogVisible(getSupportFragmentManager(), DialogFactory.SD_CARD_SELECT_DIALOG)) {
                                        this.mSDDialog = DialogFactory.show(getSupportFragmentManager(), DialogFactory.SD_CARD_SELECT_DIALOG, (Bundle) null);
                                        return;
                                    }
                                    return;
                                }
                                return;
                            case 32:
                                Log.m19d(TAG, "SD_UNMOUNT arg = " + i5);
                                DialogFragment dialogFragment = this.mSDDialog;
                                if (dialogFragment != null) {
                                    dialogFragment.dismissAllowingStateLoss();
                                    this.mSDDialog = null;
                                }
                                DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.SD_CARD_SELECT_DIALOG);
                                int scene = VoiceNoteApplication.getScene();
                                if (CheckedItemProvider.getCheckedItems().size() == 0 && DialogFactory.isDialogVisible(getSupportFragmentManager(), DialogFactory.RENAME_DIALOG)) {
                                    DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.RENAME_DIALOG);
                                }
                                if (scene == 4) {
                                    String path2 = Engine.getInstance().getPath();
                                    if (path2 != null && path2.isEmpty()) {
                                        DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.RENAME_DIALOG);
                                        DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.DETAIL_DIALOG);
                                    }
                                } else if (scene == 5 || scene == 10) {
                                    ArrayList<Long> checkedItems = CheckedItemProvider.getCheckedItems();
                                    if (checkedItems.size() == 1 && (path = CursorProvider.getInstance().getPath(checkedItems.get(0).longValue())) != null && !StorageProvider.isExistFile(path)) {
                                        DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.RENAME_DIALOG);
                                    }
                                }
                                if (i5 != -1) {
                                    DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.DELETE_DIALOG);
                                    if (i5 == 1006) {
                                        DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.RENAME_DIALOG);
                                    }
                                    this.mObservable.notifyObservers(Integer.valueOf(i2));
                                    return;
                                }
                                return;
                            case 33:
                                if (this.mScene == 12) {
                                    DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.NETWORK_NOT_CONNECTED);
                                    new Handler().postDelayed(new Runnable() {
                                        public final void run() {
                                            VNMainActivity.this.lambda$serviceCallBackMsgHandler$2$VNMainActivity();
                                        }
                                    }, (long) i5);
                                    return;
                                }
                                return;
                            default:
                                return;
                        }
                }
            } else {
                this.mObservable.notifyObservers(Integer.valueOf(Event.TRANSLATION_CANCEL));
                DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.EDIT_SAVE_DIALOG);
                DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.EDIT_CANCEL_DIALOG);
                DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.RECORD_CANCEL_DIALOG);
                DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.RENAME_DIALOG);
                DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.CATEGORY_RENAME);
                DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.MODE_NOT_SUPPORTED);
                DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.TRANSLATION_CANCEL_DIALOG);
            }
        } else if (i5 == 1) {
            int intSettings = com.sec.android.app.voicenote.provider.Settings.getIntSettings("record_mode", 1);
            int i11 = this.mScene;
            if (i11 == 4 || i11 == 6) {
                intSettings = com.sec.android.app.voicenote.provider.Settings.getIntSettings(com.sec.android.app.voicenote.provider.Settings.KEY_PLAY_MODE, 1);
            }
            if (DialogFactory.isDialogVisible(getSupportFragmentManager(), DialogFactory.MODE_NOT_SUPPORTED)) {
                Log.m29v(TAG, "Dialog is showing - don't show more");
            } else if (intSettings != 2) {
                if (intSettings == 4 && this.mIsResumed) {
                    DialogFactory.show(getSupportFragmentManager(), DialogFactory.MODE_NOT_SUPPORTED, (Bundle) null);
                }
            } else if (this.mIsResumed) {
                DialogFactory.show(getSupportFragmentManager(), DialogFactory.MODE_NOT_SUPPORTED, (Bundle) null);
            }
        } else {
            DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.MODE_NOT_SUPPORTED);
        }
    }

    public /* synthetic */ void lambda$serviceCallBackMsgHandler$2$VNMainActivity() {
        if (Engine.getInstance().resumeTranslation() == 0) {
            this.mObservable.notifyObservers(Integer.valueOf(Event.TRANSLATION_RESUME));
        }
    }

    /* access modifiers changed from: package-private */
    public boolean startRecordingFromSvoice() {
        Intent intent = getIntent();
        if (intent == null) {
            return false;
        }
        int scene = VoiceNoteApplication.getScene();
        if (scene == 1 || scene == 2) {
            boolean booleanExtra = intent.getBooleanExtra("android.intent.action.RUN", false);
            if (booleanExtra) {
                Handler handler = this.mHandler;
                if (handler != null) {
                    handler.removeMessages(0);
                    this.mHandler.sendEmptyMessageDelayed(2, 300);
                }
                intent.putExtra("android.intent.action.RUN", false);
            }
            return booleanExtra;
        }
        intent.putExtra("android.intent.action.RUN", false);
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0092, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0096, code lost:
        throw r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int checkSDCard() {
        /*
            r7 = this;
            java.lang.String r0 = "sdcard_previous_state"
            java.lang.String r1 = "VNMainActivity"
            java.lang.String r2 = "checkSDCard"
            com.sec.android.app.voicenote.provider.Log.m26i(r1, r2)
            java.lang.String r2 = "VNActivity.checkSDCard"
            com.sec.android.app.voicenote.common.util.Trace.beginSection(r2)
            java.lang.String r2 = "VNActivity.SDCard1"
            com.sec.android.app.voicenote.common.util.Trace.beginSection(r2)     // Catch:{ all -> 0x0092 }
            boolean r2 = com.sec.android.app.voicenote.provider.SecureFolderProvider.isSecureFolderSupported()     // Catch:{ all -> 0x0092 }
            r3 = 0
            if (r2 == 0) goto L_0x002a
            com.sec.android.app.voicenote.provider.SecureFolderProvider.getKnoxMenuList(r7)     // Catch:{ all -> 0x0092 }
            boolean r2 = com.sec.android.app.voicenote.provider.SecureFolderProvider.isInsideSecureFolder()     // Catch:{ all -> 0x0092 }
            if (r2 == 0) goto L_0x002a
            com.sec.android.app.voicenote.common.util.Trace.endSection()     // Catch:{ all -> 0x0097 }
            com.sec.android.app.voicenote.common.util.Trace.endSection()
            return r3
        L_0x002a:
            com.sec.android.app.voicenote.common.util.Trace.endSection()     // Catch:{ all -> 0x0097 }
            java.lang.String r2 = "VNActivity.SDCard2"
            com.sec.android.app.voicenote.common.util.Trace.beginSection(r2)     // Catch:{ all -> 0x0097 }
            java.lang.String r2 = com.sec.android.app.voicenote.provider.StorageProvider.getExternalStorageStateSd()     // Catch:{ all -> 0x0097 }
            com.sec.android.app.voicenote.common.util.Trace.endSection()     // Catch:{ all -> 0x0097 }
            java.lang.String r4 = "VNActivity.SDCard3"
            com.sec.android.app.voicenote.common.util.Trace.beginSection(r4)     // Catch:{ all -> 0x008d }
            boolean r4 = com.sec.android.app.voicenote.provider.StorageProvider.isSdCardWriteRestricted(r7)     // Catch:{ all -> 0x008d }
            com.sec.android.app.voicenote.common.util.AndroidForWork r5 = com.sec.android.app.voicenote.common.util.AndroidForWork.getInstance()     // Catch:{ all -> 0x008d }
            boolean r5 = r5.isAndroidForWorkMode(r7)     // Catch:{ all -> 0x008d }
            int r6 = com.sec.android.app.voicenote.provider.Settings.getIntSettings(r0, r3)     // Catch:{ all -> 0x008d }
            if (r6 != 0) goto L_0x0069
            java.lang.String r6 = "mounted"
            boolean r6 = r6.equals(r2)     // Catch:{ all -> 0x008d }
            if (r6 == 0) goto L_0x0069
            if (r4 != 0) goto L_0x0069
            if (r5 != 0) goto L_0x0069
            java.lang.String r0 = "SD Card Mounted !!"
            com.sec.android.app.voicenote.provider.Log.m19d(r1, r0)     // Catch:{ all -> 0x008d }
            r0 = 1
            com.sec.android.app.voicenote.common.util.Trace.endSection()     // Catch:{ all -> 0x0097 }
            com.sec.android.app.voicenote.common.util.Trace.endSection()
            return r0
        L_0x0069:
            java.lang.String r1 = "unmounted"
            boolean r1 = r1.equals(r2)     // Catch:{ all -> 0x008d }
            if (r1 != 0) goto L_0x007d
            if (r4 != 0) goto L_0x007d
            if (r5 == 0) goto L_0x0076
            goto L_0x007d
        L_0x0076:
            com.sec.android.app.voicenote.common.util.Trace.endSection()     // Catch:{ all -> 0x0097 }
            com.sec.android.app.voicenote.common.util.Trace.endSection()
            return r3
        L_0x007d:
            java.lang.String r1 = "storage"
            com.sec.android.app.voicenote.provider.Settings.setSettings((java.lang.String) r1, (int) r3)     // Catch:{ all -> 0x008d }
            com.sec.android.app.voicenote.provider.Settings.setSettings((java.lang.String) r0, (int) r3)     // Catch:{ all -> 0x008d }
            r0 = 2
            com.sec.android.app.voicenote.common.util.Trace.endSection()     // Catch:{ all -> 0x0097 }
            com.sec.android.app.voicenote.common.util.Trace.endSection()
            return r0
        L_0x008d:
            r0 = move-exception
            com.sec.android.app.voicenote.common.util.Trace.endSection()     // Catch:{ all -> 0x0097 }
            throw r0     // Catch:{ all -> 0x0097 }
        L_0x0092:
            r0 = move-exception
            com.sec.android.app.voicenote.common.util.Trace.endSection()     // Catch:{ all -> 0x0097 }
            throw r0     // Catch:{ all -> 0x0097 }
        L_0x0097:
            r0 = move-exception
            com.sec.android.app.voicenote.common.util.Trace.endSection()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.main.VNMainActivity.checkSDCard():int");
    }

    /* access modifiers changed from: package-private */
    public void startListFromPrivateBox() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        if (PRIVATE_INTENT_ACTION.equals(intent.getAction())) {
            PrivateModeProvider.setPrivateBoxMode(true);
            if (Engine.getInstance().getRecorderState() == 1 && Engine.getInstance().getPlayerState() == 1 && this.mScene == 1) {
                this.mObservable.notifyObservers(3);
                return;
            }
            return;
        }
        PrivateModeProvider.setPrivateBoxMode(false);
    }

    private boolean checkClearTempFiles() {
        if (Engine.getInstance().getRecorderState() == 1 && Engine.getInstance().getPlayerState() == 1 && Engine.getInstance().getEditorState() == 1 && this.mScene != 6 && PermissionProvider.checkSavingEnable(this)) {
            return true;
        }
        return false;
    }

    private void checkRecQuality() {
        if (com.sec.android.app.voicenote.provider.Settings.getIntSettings("record_mode", 1) == 6) {
            com.sec.android.app.voicenote.provider.Settings.setSettings("record_mode", 1);
            com.sec.android.app.voicenote.provider.Settings.setSettings(com.sec.android.app.voicenote.provider.Settings.KEY_REC_QUALITY, 1);
        }
    }

    private boolean fromEdge(Intent intent) {
        return intent != null && EDGE_INTENT_RECORD_START.equals(intent.getAction());
    }

    private boolean fromActionSearch(Intent intent) {
        return intent != null && "android.intent.action.SEARCH".equals(intent.getAction());
    }

    private boolean fromBixby(Intent intent) {
        Bundle extras;
        String string;
        if (intent == null || (extras = intent.getExtras()) == null || (string = extras.getString(BixbyConstant.BixbyStartMode.BIXBY_START_DATA)) == null || (!string.equals(BixbyConstant.BixbyStartMode.BIXBY_START_PLAY) && !string.equals(BixbyConstant.BixbyStartMode.BIXBY_START_RECORD))) {
            return false;
        }
        return true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x00b5  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00c8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void handleBixbyIntent(android.os.Bundle r7) {
        /*
            r6 = this;
            java.lang.String r0 = "VNMainActivity"
            java.lang.String r1 = "handleBixbyIntent"
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r1)
            int r1 = com.sec.android.app.voicenote.uicore.VoiceNoteApplication.getScene()
            r2 = 6
            if (r1 != r2) goto L_0x0014
            java.lang.String r7 = "handleBixbyIntent - Scene.Edit"
            com.sec.android.app.voicenote.provider.Log.m32w((java.lang.String) r0, (java.lang.String) r7)
            return
        L_0x0014:
            com.sec.android.app.voicenote.service.Engine r1 = com.sec.android.app.voicenote.service.Engine.getInstance()
            int r1 = r1.getRecorderState()
            r2 = 1
            if (r1 == r2) goto L_0x0025
            java.lang.String r7 = "handleBixbyIntent - Recording is not idle"
            com.sec.android.app.voicenote.provider.Log.m32w((java.lang.String) r0, (java.lang.String) r7)
            return
        L_0x0025:
            com.sec.android.app.voicenote.service.Engine r1 = com.sec.android.app.voicenote.service.Engine.getInstance()
            int r1 = r1.getPlayerState()
            r3 = 3
            if (r1 == r2) goto L_0x005c
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r4 = "handleBixbyIntent - getPlayerState : "
            r1.append(r4)
            com.sec.android.app.voicenote.service.Engine r4 = com.sec.android.app.voicenote.service.Engine.getInstance()
            int r4 = r4.getPlayerState()
            r1.append(r4)
            java.lang.String r1 = r1.toString()
            com.sec.android.app.voicenote.provider.Log.m32w((java.lang.String) r0, (java.lang.String) r1)
            com.sec.android.app.voicenote.service.Engine r1 = com.sec.android.app.voicenote.service.Engine.getInstance()
            r1.stopPlay()
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r1 = r6.mObservable
            java.lang.Integer r4 = java.lang.Integer.valueOf(r3)
            r1.notifyObservers(r4)
        L_0x005c:
            int r1 = r6.mScene
            r4 = 2
            r5 = 4
            if (r1 == r4) goto L_0x0066
            if (r1 == r3) goto L_0x0066
            if (r1 != r5) goto L_0x006f
        L_0x0066:
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r1 = r6.mObservable
            java.lang.Integer r3 = java.lang.Integer.valueOf(r5)
            r1.notifyObservers(r3)
        L_0x006f:
            java.lang.String r1 = "bixbyStartData"
            java.lang.String r1 = r7.getString(r1)
            java.lang.String r3 = "bixby_start_data"
            com.sec.android.app.voicenote.provider.Settings.setSettings((java.lang.String) r3, (java.lang.String) r1)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "handleBixbyIntent - startData: "
            r3.append(r4)
            r3.append(r1)
            java.lang.String r3 = r3.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r3)
            r3 = -1
            int r4 = r1.hashCode()
            r5 = -141876981(0xfffffffff78b210b, float:-5.643746E33)
            if (r4 == r5) goto L_0x00a8
            r5 = 1421026926(0x54b3266e, float:6.1555511E12)
            if (r4 == r5) goto L_0x009e
            goto L_0x00b2
        L_0x009e:
            java.lang.String r4 = "bixbyStartPlay"
            boolean r1 = r1.equals(r4)
            if (r1 == 0) goto L_0x00b2
            r1 = 0
            goto L_0x00b3
        L_0x00a8:
            java.lang.String r4 = "bixbyStartRecord"
            boolean r1 = r1.equals(r4)
            if (r1 == 0) goto L_0x00b2
            r1 = r2
            goto L_0x00b3
        L_0x00b2:
            r1 = r3
        L_0x00b3:
            if (r1 == 0) goto L_0x00c8
            if (r1 == r2) goto L_0x00b8
            goto L_0x0103
        L_0x00b8:
            android.os.Handler r7 = r6.mHandler
            if (r7 == 0) goto L_0x0103
            r0 = 5
            r7.removeMessages(r0)
            android.os.Handler r7 = r6.mHandler
            r1 = 100
            r7.sendEmptyMessageDelayed(r0, r1)
            goto L_0x0103
        L_0x00c8:
            java.lang.String r1 = "fileNameID"
            java.lang.String r7 = r7.getString(r1)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "handleBixbyIntent - file name id"
            r1.append(r2)
            r1.append(r7)
            java.lang.String r1 = r1.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r1)
            com.sec.android.app.voicenote.service.helper.BixbyHelper r1 = com.sec.android.app.voicenote.service.helper.BixbyHelper.getInstance()     // Catch:{ NumberFormatException -> 0x00fd }
            android.content.Context r2 = r6.getApplicationContext()     // Catch:{ NumberFormatException -> 0x00fd }
            long r3 = java.lang.Long.parseLong(r7)     // Catch:{ NumberFormatException -> 0x00fd }
            r1.setBixbyPlayHelperContext(r2, r3)     // Catch:{ NumberFormatException -> 0x00fd }
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r7 = r6.mObservable     // Catch:{ NumberFormatException -> 0x00fd }
            r1 = 29995(0x752b, float:4.2032E-41)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ NumberFormatException -> 0x00fd }
            r7.notifyObservers(r1)     // Catch:{ NumberFormatException -> 0x00fd }
            goto L_0x0103
        L_0x00fd:
            r7 = move-exception
            java.lang.String r1 = "NumberFormatException"
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r0, (java.lang.String) r1, (java.lang.Throwable) r7)
        L_0x0103:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.main.VNMainActivity.handleBixbyIntent(android.os.Bundle):void");
    }

    private boolean fromLevelFlex(Intent intent) {
        return intent != null && LEVEL_ACTIVE_KEY_INTENT.equals(intent.getAction());
    }

    private void handleLevelFlexIntent(Bundle bundle) {
        if (VoiceNoteApplication.getScene() == 6) {
            Log.m32w(TAG, "handleLevelFlexIntent - Scene.Edit");
            return;
        }
        Log.m26i(TAG, "handleLevelFlexIntent");
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeMessages(4);
            if (Engine.getInstance().getPlayerState() != 1) {
                Log.m32w(TAG, "handleLevelFlexIntent - getPlayerState : " + Engine.getInstance().getPlayerState());
                Engine.getInstance().stopPlay();
                this.mObservable.notifyObservers(3);
            }
            if (bundle != null) {
                int i = bundle.getInt("recording_mode", 1);
                Log.m26i(TAG, "handleLevelFlexIntent getExtras recording_mode : " + i);
                if (!VoiceNoteFeature.FLAG_SUPPORT_INTERVIEW || i != 2) {
                    com.sec.android.app.voicenote.provider.Settings.setSettings("record_mode", 1);
                } else {
                    com.sec.android.app.voicenote.provider.Settings.setSettings("record_mode", 2);
                }
            } else {
                Log.m26i(TAG, "handleLevelFlexIntent RecordMode.NORMAL");
                com.sec.android.app.voicenote.provider.Settings.setSettings("record_mode", 1);
            }
            int i2 = this.mScene;
            if (i2 == 2 || i2 == 3 || i2 == 4) {
                this.mObservable.notifyObservers(4);
            }
            Engine.getInstance().setmIsNeedReleaseMediaSession(true);
            BluetoothHelper.getInstance().setApplicationContext(getApplicationContext());
            this.mHandler.sendEmptyMessageDelayed(4, 100);
        }
    }

    public boolean isActivityResumed() {
        Log.m19d(TAG, "isActivityResumed = " + this.mIsResumed);
        return this.mIsResumed;
    }

    private void startCover(int i) {
        RemoteViewManager.getInstance().setRemoteViewState(i);
        RemoteViewManager.getInstance().start(0);
    }

    private void refresh() {
        Log.m29v(TAG, "refresh");
        startActivity(new Intent(this, VNMainActivity.class));
    }

    private void handleSDCardDialog(int i) {
        if (i != 1) {
            if (i == 2) {
                DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.SD_CARD_SELECT_DIALOG);
            }
        } else if (!DialogFactory.isDialogWithTagVisible(getSupportFragmentManager(), DialogFactory.SD_CARD_SELECT_DIALOG)) {
            this.mSDDialog = DialogFactory.show(getSupportFragmentManager(), DialogFactory.SD_CARD_SELECT_DIALOG, (Bundle) null);
        }
        this.mCheckSDCardAction = -1;
    }

    private void clearMenu() {
        if (this.mMenu != null) {
            this.mMenu = null;
        }
    }
}
