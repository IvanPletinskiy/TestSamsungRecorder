package com.sec.android.app.voicenote.uicore;

import android.content.res.Resources;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.activity.SimpleActivity;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.common.util.StatusBarHelper;
import com.sec.android.app.voicenote.p007ui.AbsSimpleFragment;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.provider.HWKeyboardProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.NavigationBarProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.provider.WaveProvider;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SimpleFragmentController implements Observer {
    /* access modifiers changed from: private */
    private static final String CONTROLBUTTON = "ControlButton";
    private static final SparseArray<AbsScene> EVENT_SCENE_TABLE = new SparseArray<>();
    private static final String INFO = "Info";
    private static final String MULTI_CONTROLBUTTON = "Multi_ControlButton";
    private static final String MULTI_INFO = "Multi_Info";
    private static final String MULTI_SEEKBAR = "Multi_Seekbar";
    private static final String MULTI_STT = "Multi_Stt";
    private static final String MULTI_TOOLBAR = "Multi_Toolbar";
    private static final HashMap<String, Integer> SIMPLE_FRAGMENT_LAYOUT_TABLE = new HashMap<>();
    private static final String STT = "Stt";
    private static final String TAG = "SimpleFragmentController";
    private static final String TOOLBAR = "Toolbar";
    private static final String WAVE = "Wave";
    private AbsScene PLAY_SCENE = null;
    private AbsScene RECORD_SCENE = null;
    private FragmentFactory fragmentFactory;
    /* access modifiers changed from: private */
    public AppCompatActivity mActivity;
    private int mCurrentEvent;
    private int mCurrentScene;
    /* access modifiers changed from: private */
    public SimpleActivity.LaunchMode mLaunchMode;
    private final ArrayList<WeakReference<OnSceneChangeListener>> mListeners = new ArrayList<>();
    private Observable mObservable = Observable.getInstance();
    /* access modifiers changed from: private */
    public String mSession;
    public static final String[] ALL_TAG = {INFO, "Wave", CONTROLBUTTON, TOOLBAR, STT, MULTI_INFO, MULTI_SEEKBAR, MULTI_CONTROLBUTTON, MULTI_TOOLBAR, MULTI_STT};

    public interface OnSceneChangeListener {
        void onSceneChange(int i);
    }

    public static class Scene {
        public static final int Empty = 0;
        public static final int MultiWindowPlay = 4;
        public static final int MultiWindowRecord = 2;
        public static final int Play = 3;
        public static final int Record = 1;
    }

    private boolean isIncludeSimpleToolbar(int i) {
        return (i == 50001 || i == 50002 || i == 50003 || i == 50004 || i == 50005) ? false : true;
    }

    public SimpleFragmentController(String str, int i, int i2, SimpleActivity.LaunchMode launchMode, AppCompatActivity appCompatActivity) {
        Log.m26i(TAG, "create SimpleFragmentController session:" + str + " currentScene: " + i2);
        this.mSession = str;
        this.mActivity = appCompatActivity;
        this.mCurrentEvent = i;
        this.mCurrentScene = i2;
        this.mLaunchMode = launchMode;
        this.mObservable.addObserver(this.mSession, this);
        this.fragmentFactory = new FragmentFactory();
        SIMPLE_FRAGMENT_LAYOUT_TABLE.put(INFO, Integer.valueOf(C0690R.C0693id.simple_info));
        SIMPLE_FRAGMENT_LAYOUT_TABLE.put("Wave", Integer.valueOf(C0690R.C0693id.simple_wave));
        SIMPLE_FRAGMENT_LAYOUT_TABLE.put(STT, Integer.valueOf(C0690R.C0693id.simple_stt));
        SIMPLE_FRAGMENT_LAYOUT_TABLE.put(TOOLBAR, Integer.valueOf(C0690R.C0693id.simple_toolbar));
        SIMPLE_FRAGMENT_LAYOUT_TABLE.put(CONTROLBUTTON, Integer.valueOf(C0690R.C0693id.simple_controlbutton));
        SIMPLE_FRAGMENT_LAYOUT_TABLE.put(MULTI_INFO, Integer.valueOf(C0690R.C0693id.simple_multi_info));
        SIMPLE_FRAGMENT_LAYOUT_TABLE.put(MULTI_SEEKBAR, Integer.valueOf(C0690R.C0693id.simple_multi_seekbar));
        SIMPLE_FRAGMENT_LAYOUT_TABLE.put(MULTI_CONTROLBUTTON, Integer.valueOf(C0690R.C0693id.simple_multi_controlbutton));
        SIMPLE_FRAGMENT_LAYOUT_TABLE.put(MULTI_STT, Integer.valueOf(C0690R.C0693id.simple_multi_stt));
        updateScene();
    }

    /* JADX WARNING: Removed duplicated region for block: B:8:0x002c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateEventSceneTable() {
        /*
            r3 = this;
            java.lang.String r0 = r3.mSession
            java.lang.String r1 = "SimpleFragmentController"
            java.lang.String r2 = "updateEventSceneTable"
            com.sec.android.app.voicenote.provider.Log.m27i((java.lang.String) r1, (java.lang.String) r2, (java.lang.String) r0)
            boolean r0 = r3.isInMultiWindow()
            if (r0 != 0) goto L_0x001f
            com.sec.android.app.voicenote.provider.DesktopModeProvider.getInstance()
            boolean r0 = com.sec.android.app.voicenote.provider.DesktopModeProvider.isDesktopMode()
            if (r0 == 0) goto L_0x0019
            goto L_0x001f
        L_0x0019:
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$FullScreenRecordScene r0 = new com.sec.android.app.voicenote.uicore.SimpleFragmentController$FullScreenRecordScene
            r0.<init>()
            goto L_0x0024
        L_0x001f:
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$MultiWindowRecordScene r0 = new com.sec.android.app.voicenote.uicore.SimpleFragmentController$MultiWindowRecordScene
            r0.<init>()
        L_0x0024:
            r3.RECORD_SCENE = r0
            boolean r0 = r3.isInMultiWindow()
            if (r0 != 0) goto L_0x003c
            com.sec.android.app.voicenote.provider.DesktopModeProvider.getInstance()
            boolean r0 = com.sec.android.app.voicenote.provider.DesktopModeProvider.isDesktopMode()
            if (r0 == 0) goto L_0x0036
            goto L_0x003c
        L_0x0036:
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$FullScreenPlayScene r0 = new com.sec.android.app.voicenote.uicore.SimpleFragmentController$FullScreenPlayScene
            r0.<init>()
            goto L_0x0041
        L_0x003c:
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$MultiWindowPlayScene r0 = new com.sec.android.app.voicenote.uicore.SimpleFragmentController$MultiWindowPlayScene
            r0.<init>()
        L_0x0041:
            r3.PLAY_SCENE = r0
            r3.reorganizeScene()
            android.util.SparseArray<com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene> r0 = EVENT_SCENE_TABLE
            r1 = 50001(0xc351, float:7.0066E-41)
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene r2 = r3.RECORD_SCENE
            r0.put(r1, r2)
            android.util.SparseArray<com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene> r0 = EVENT_SCENE_TABLE
            r1 = 50002(0xc352, float:7.0068E-41)
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene r2 = r3.RECORD_SCENE
            r0.put(r1, r2)
            android.util.SparseArray<com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene> r0 = EVENT_SCENE_TABLE
            r1 = 50003(0xc353, float:7.0069E-41)
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene r2 = r3.PLAY_SCENE
            r0.put(r1, r2)
            android.util.SparseArray<com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene> r0 = EVENT_SCENE_TABLE
            r1 = 50006(0xc356, float:7.0073E-41)
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene r2 = r3.PLAY_SCENE
            r0.put(r1, r2)
            android.util.SparseArray<com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene> r0 = EVENT_SCENE_TABLE
            r1 = 50007(0xc357, float:7.0075E-41)
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene r2 = r3.PLAY_SCENE
            r0.put(r1, r2)
            android.util.SparseArray<com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene> r0 = EVENT_SCENE_TABLE
            r1 = 50008(0xc358, float:7.0076E-41)
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene r2 = r3.PLAY_SCENE
            r0.put(r1, r2)
            android.util.SparseArray<com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene> r0 = EVENT_SCENE_TABLE
            r1 = 50009(0xc359, float:7.0078E-41)
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene r2 = r3.PLAY_SCENE
            r0.put(r1, r2)
            android.util.SparseArray<com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene> r0 = EVENT_SCENE_TABLE
            r1 = 50011(0xc35b, float:7.008E-41)
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene r2 = r3.PLAY_SCENE
            r0.put(r1, r2)
            android.util.SparseArray<com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene> r0 = EVENT_SCENE_TABLE
            r1 = 50012(0xc35c, float:7.0082E-41)
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene r2 = r3.PLAY_SCENE
            r0.put(r1, r2)
            android.util.SparseArray<com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene> r0 = EVENT_SCENE_TABLE
            r1 = 50004(0xc354, float:7.007E-41)
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene r2 = r3.PLAY_SCENE
            r0.put(r1, r2)
            android.util.SparseArray<com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene> r0 = EVENT_SCENE_TABLE
            r1 = 50005(0xc355, float:7.0072E-41)
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene r2 = r3.PLAY_SCENE
            r0.put(r1, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.uicore.SimpleFragmentController.updateEventSceneTable():void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:27:0x0090  */
    /* JADX WARNING: Removed duplicated region for block: B:30:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateScene() {
        /*
            r6 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "updateScene - "
            r0.append(r1)
            int r1 = r6.mCurrentScene
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "SimpleFragmentController"
            com.sec.android.app.voicenote.provider.Log.m26i(r1, r0)
            r6.updateEventSceneTable()
            int r0 = r6.mCurrentScene
            if (r0 == 0) goto L_0x0095
            boolean r0 = r6.isInMultiWindow()
            r1 = 4
            r2 = 3
            r3 = 2
            r4 = 1
            r5 = 0
            if (r0 != 0) goto L_0x0060
            com.sec.android.app.voicenote.provider.DesktopModeProvider.getInstance()
            boolean r0 = com.sec.android.app.voicenote.provider.DesktopModeProvider.isDesktopMode()
            if (r0 == 0) goto L_0x0034
            goto L_0x0060
        L_0x0034:
            int r0 = r6.mCurrentScene
            if (r0 == r4) goto L_0x0058
            if (r0 == r3) goto L_0x0050
            if (r0 == r2) goto L_0x0048
            if (r0 == r1) goto L_0x0040
        L_0x003e:
            r0 = r5
            goto L_0x008a
        L_0x0040:
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$MultiWindowPlayScene r5 = new com.sec.android.app.voicenote.uicore.SimpleFragmentController$MultiWindowPlayScene
            r5.<init>()
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene r0 = r6.PLAY_SCENE
            goto L_0x008a
        L_0x0048:
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$FullScreenPlayScene r5 = new com.sec.android.app.voicenote.uicore.SimpleFragmentController$FullScreenPlayScene
            r5.<init>()
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene r0 = r6.PLAY_SCENE
            goto L_0x008a
        L_0x0050:
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$MultiWindowRecordScene r5 = new com.sec.android.app.voicenote.uicore.SimpleFragmentController$MultiWindowRecordScene
            r5.<init>()
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene r0 = r6.RECORD_SCENE
            goto L_0x008a
        L_0x0058:
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$FullScreenRecordScene r5 = new com.sec.android.app.voicenote.uicore.SimpleFragmentController$FullScreenRecordScene
            r5.<init>()
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene r0 = r6.RECORD_SCENE
            goto L_0x008a
        L_0x0060:
            int r0 = r6.mCurrentScene
            if (r0 == r4) goto L_0x0083
            if (r0 == r3) goto L_0x007b
            if (r0 == r2) goto L_0x0073
            if (r0 == r1) goto L_0x006b
            goto L_0x003e
        L_0x006b:
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$MultiWindowPlayScene r5 = new com.sec.android.app.voicenote.uicore.SimpleFragmentController$MultiWindowPlayScene
            r5.<init>()
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene r0 = r6.PLAY_SCENE
            goto L_0x008a
        L_0x0073:
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$FullScreenPlayScene r5 = new com.sec.android.app.voicenote.uicore.SimpleFragmentController$FullScreenPlayScene
            r5.<init>()
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene r0 = r6.PLAY_SCENE
            goto L_0x008a
        L_0x007b:
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$MultiWindowRecordScene r5 = new com.sec.android.app.voicenote.uicore.SimpleFragmentController$MultiWindowRecordScene
            r5.<init>()
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene r0 = r6.RECORD_SCENE
            goto L_0x008a
        L_0x0083:
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$FullScreenRecordScene r5 = new com.sec.android.app.voicenote.uicore.SimpleFragmentController$FullScreenRecordScene
            r5.<init>()
            com.sec.android.app.voicenote.uicore.SimpleFragmentController$AbsScene r0 = r6.RECORD_SCENE
        L_0x008a:
            boolean r1 = r5.equals(r0)
            if (r1 != 0) goto L_0x0095
            int r1 = r6.mCurrentEvent
            r6.sceneChangeTransaction(r0, r5, r1)
        L_0x0095:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.uicore.SimpleFragmentController.updateScene():void");
    }

    public int getCurrentEvent() {
        return this.mCurrentEvent;
    }

    public int getCurrentScene() {
        return this.mCurrentScene;
    }

    private void addFragment(String str, int i, int i2, int i3) {
        AbsSimpleFragment create = this.fragmentFactory.create(str);
        if (create == null) {
            Log.m22e(TAG, "addFragment - tag name : " + str + " is null");
            return;
        }
        create.setEvent(i);
        FragmentTransaction beginTransaction = this.mActivity.getSupportFragmentManager().beginTransaction();
        beginTransaction.setCustomAnimations(i2, i3);
        beginTransaction.replace(getContainerViewId(str), create, str);
        beginTransaction.commitAllowingStateLoss();
    }

    private void removeFragment(String str, int i, int i2) {
        Log.m26i(TAG, "removeFragment - tag: " + str);
        AbsSimpleFragment absSimpleFragment = this.fragmentFactory.get(str);
        if (absSimpleFragment == null) {
            Log.m22e(TAG, "removeFragment - tag name : " + str + " is null");
            return;
        }
        FragmentTransaction beginTransaction = this.mActivity.getSupportFragmentManager().beginTransaction();
        beginTransaction.setCustomAnimations(i, i2);
        beginTransaction.remove(absSimpleFragment);
        beginTransaction.commitAllowingStateLoss();
        this.fragmentFactory.remove(str);
    }

    private void refreshFragment(String str, int i, int i2) {
        Log.m26i(TAG, "refreshFragment - tag: " + str);
        AbsSimpleFragment absSimpleFragment = this.fragmentFactory.get(str);
        if (absSimpleFragment == null) {
            Log.m22e(TAG, "refreshFragment - tag name : " + str + " is null");
            return;
        }
        FragmentTransaction beginTransaction = this.mActivity.getSupportFragmentManager().beginTransaction();
        beginTransaction.setCustomAnimations(i, i2);
        beginTransaction.detach(absSimpleFragment);
        beginTransaction.attach(absSimpleFragment);
        beginTransaction.commitAllowingStateLoss();
    }

    private void updateFragment(String str, int i) {
        Log.m26i(TAG, "updateFragment : " + str);
        AbsSimpleFragment absSimpleFragment = this.fragmentFactory.get(str);
        if (absSimpleFragment == null) {
            addFragment(str, i, 0, 0);
        } else {
            absSimpleFragment.onUpdate(Integer.valueOf(i));
        }
    }

    private int getContainerViewId(String str) {
        return SIMPLE_FRAGMENT_LAYOUT_TABLE.get(str).intValue();
    }

    private List<String> getNewFragment(String[] strArr, String[] strArr2) {
        boolean z;
        ArrayList arrayList = new ArrayList();
        if (!(strArr2 == null || strArr2.length == 0)) {
            if (strArr == null || strArr.length == 0) {
                Collections.addAll(arrayList, strArr2);
            } else {
                for (String str : strArr2) {
                    int length = strArr.length;
                    int i = 0;
                    while (true) {
                        if (i >= length) {
                            z = true;
                            break;
                        } else if (str.equals(strArr[i])) {
                            z = false;
                            break;
                        } else {
                            i++;
                        }
                    }
                    if (z) {
                        arrayList.add(str);
                    } else if (this.mActivity.getSupportFragmentManager().findFragmentByTag(str) == null) {
                        Log.m22e(TAG, str + " is not old fragment but not exist !!");
                        arrayList.add(str);
                    }
                }
                return arrayList;
            }
        }
        return arrayList;
    }

    private List<String> getOldFragment(String[] strArr, String[] strArr2) {
        boolean z;
        ArrayList arrayList = new ArrayList();
        if (!(strArr == null || strArr.length == 0)) {
            if (strArr2 == null || strArr2.length == 0) {
                Collections.addAll(arrayList, strArr);
            } else {
                for (String str : strArr) {
                    int length = strArr2.length;
                    int i = 0;
                    while (true) {
                        if (i >= length) {
                            z = true;
                            break;
                        } else if (str.equals(strArr2[i])) {
                            z = false;
                            break;
                        } else {
                            i++;
                        }
                    }
                    if (z) {
                        arrayList.add(str);
                    }
                }
                return arrayList;
            }
        }
        return arrayList;
    }

    private List<String> getReuseFragment(String[] strArr, String[] strArr2) {
        ArrayList arrayList = new ArrayList();
        if (!(strArr == null || strArr.length == 0 || strArr2 == null || strArr2.length == 0)) {
            FragmentManager supportFragmentManager = this.mActivity.getSupportFragmentManager();
            for (String str : strArr) {
                for (String str2 : strArr2) {
                    if (str.equals(str2) && supportFragmentManager.findFragmentByTag(str2) != null) {
                        arrayList.add(str);
                    }
                }
            }
        }
        return arrayList;
    }

    public void update(Observable observable, Object obj) {
        AppCompatActivity appCompatActivity;
        int intValue = ((Integer) obj).intValue();
        if (intValue == 2) {
            intValue = this.mCurrentEvent;
        }
        updateEventSceneTable();
        if (intValue == 1 || (appCompatActivity = this.mActivity) == null || appCompatActivity.isDestroyed()) {
            Log.m26i(TAG, "update just update mCurrentEvent Event : " + intValue);
            if (EVENT_SCENE_TABLE.get(intValue, (AbsScene) null) != null) {
                this.mCurrentEvent = intValue;
                return;
            }
            return;
        }
        if (intValue == 11 || intValue == 12) {
            updateViewChange();
        } else if (intValue == 998) {
            Log.m26i(TAG, "update hide dialog !!!");
            DialogFactory.clearTopDialog(this.mActivity.getSupportFragmentManager());
            return;
        }
        Log.m27i(TAG, "update - current event : " + this.mCurrentEvent + " new event : " + intValue, this.mSession);
        if (EVENT_SCENE_TABLE.get(intValue, (AbsScene) null) != null) {
            sceneChangeTransaction(EVENT_SCENE_TABLE.get(intValue), EVENT_SCENE_TABLE.get(this.mCurrentEvent), intValue);
            return;
        }
        String[] tags = EVENT_SCENE_TABLE.get(this.mCurrentEvent).getTags();
        if (tags != null) {
            for (String str : tags) {
                if (CONTROLBUTTON.equals(str)) {
                    updateControlButtonLayout(true);
                }
                updateFragment(str, intValue);
            }
        }
        if ((intValue == 11 || intValue == 12) && tags != null) {
            for (String str2 : tags) {
                if (TOOLBAR.equals(str2)) {
                    refreshFragment(str2, 0, 0);
                }
                if (CONTROLBUTTON.equals(str2)) {
                    refreshFragment(str2, C0690R.animator.ani_wave_fragment_show, C0690R.animator.ani_wave_fragment_hide);
                }
                if (STT.equals(str2)) {
                    refreshFragment(str2, 0, 0);
                }
                if (INFO.equals(str2)) {
                    refreshFragment(str2, 0, 0);
                }
                if ("Wave".equals(str2)) {
                    refreshFragment(str2, 0, 0);
                }
            }
        }
    }

    private void updateViewChange() {
        Log.m26i(TAG, "updateViewChange");
        if (DisplayManager.isInMultiWindowMode(this.mActivity)) {
            return;
        }
        if (DisplayManager.isDeviceOnLandscape()) {
            updateSimpleLayoutLandScape();
        } else {
            updateSimpleLayout();
        }
    }

    private void updateSimpleLayout() {
        int i;
        int i2;
        int i3;
        int i4;
        Log.m26i(TAG, "updateSimpleLayout");
        int i5 = this.mCurrentScene;
        if (i5 == 3 || i5 == 1 || (i4 = this.mCurrentEvent) == 2005 || i4 == 2006) {
            int intSettings = Settings.getIntSettings(Settings.KEY_SIMPLE_RECORD_MODE, -1);
            int intSettings2 = Settings.getIntSettings(Settings.KEY_SIMPLE_PLAY_MODE, -1);
            boolean isHWKeyboard = HWKeyboardProvider.isHWKeyboard(this.mActivity);
            Log.m19d(TAG, "updateSimpleLayout - recordMode: " + intSettings + " - playMode: " + intSettings2 + " - scene: " + this.mCurrentScene);
            int i6 = this.mActivity.getResources().getDisplayMetrics().heightPixels;
            int i7 = this.mActivity.getResources().getDisplayMetrics().widthPixels;
            int statusBarHeight = (i6 - getStatusBarHeight()) - getActionBarHeight();
            int dimensionPixelSize = this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.main_controlbutton_margin_top) + this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.main_controlbutton_height);
            int dimensionPixelSize2 = this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.controlbutton_big_height);
            int dimensionPixelSize3 = this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.main_controlbutton_margin_left);
            if (VoiceNoteFeature.FLAG_IS_TABLET) {
                i = (((i7 - (dimensionPixelSize2 * 3)) - (dimensionPixelSize3 * 2)) / 4) + dimensionPixelSize3;
            } else {
                i = this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.multi_window_control_button_margin_left_right_max);
            }
            if (this.mCurrentScene == 3) {
                dimensionPixelSize += this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.main_toolbar_height);
            }
            if (statusBarHeight <= 0) {
                Log.m26i(TAG, "updateSimpleLayout - mainViewHeight = 0 ");
                return;
            }
            int dimensionPixelSize4 = ((statusBarHeight * 20) / 100) - this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.wave_bookmark_top_margin);
            int i8 = ((isHWKeyboard ? 1 : 2) * statusBarHeight) / 100;
            int dimensionPixelSize5 = this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.main_toolbar_height);
            int dimensionPixelSize6 = this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.main_toolbar_margin);
            int dimensionPixelSize7 = this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.main_toolbar_margin_bottom);
            if (VoiceNoteFeature.FLAG_IS_TABLET) {
                dimensionPixelSize6 = i - this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.multi_window_toolbar_margin_offset);
            }
            int i9 = (int) (((((this.mCurrentScene == 1 && intSettings == 4) || (this.mCurrentScene == 3 && intSettings2 == 4)) ? 21.5f : 35.0f) * ((float) statusBarHeight)) / 100.0f);
            int i10 = dimensionPixelSize6;
            int i11 = dimensionPixelSize7;
            WaveProvider.getInstance().setWaveHeight(i9, (i9 - this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.wave_time_text_height)) - this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.wave_bookmark_top_margin), true);
            int dimensionPixelSize8 = i9 + this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.voice_note_trim_handler_red_height);
            if (intSettings == 4 || intSettings2 == 4) {
                int dimensionPixelSize9 = this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.multi_window_stt_margin_top);
                i3 = (((((statusBarHeight - dimensionPixelSize4) - i8) - dimensionPixelSize8) - dimensionPixelSize9) - dimensionPixelSize) - getRecordButtonMarginBottom();
                i2 = dimensionPixelSize9;
            } else {
                i3 = 0;
                i2 = 0;
            }
            FrameLayout frameLayout = (FrameLayout) this.mActivity.getWindow().getDecorView().findViewById(C0690R.C0693id.simple_info);
            if (frameLayout != null) {
                Log.m19d(TAG, "updateSimpleLayout - infoViewActualHeight " + dimensionPixelSize4);
                updateViewHeight(frameLayout, dimensionPixelSize4);
            }
            FrameLayout frameLayout2 = (FrameLayout) this.mActivity.getWindow().getDecorView().findViewById(C0690R.C0693id.simple_wave);
            if (frameLayout2 != null) {
                Log.m19d(TAG, "updateSimpleLayout - waveViewActualHeight " + dimensionPixelSize8);
                updateViewHeight(frameLayout2, dimensionPixelSize8);
            }
            FrameLayout frameLayout3 = (FrameLayout) this.mActivity.getWindow().getDecorView().findViewById(C0690R.C0693id.simple_controlbutton);
            if (frameLayout3 != null) {
                Log.m19d(TAG, "updateSimpleLayout - controlbuttonView " + dimensionPixelSize2);
                updateViewHeight(frameLayout3, dimensionPixelSize2, 0, 0, i, i);
            }
            FrameLayout frameLayout4 = (FrameLayout) this.mActivity.getWindow().getDecorView().findViewById(C0690R.C0693id.simple_toolbar);
            if (frameLayout4 != null) {
                updateViewHeight(frameLayout4, dimensionPixelSize5, 0, i11, i10, i10);
            }
            FrameLayout frameLayout5 = (FrameLayout) this.mActivity.getWindow().getDecorView().findViewById(C0690R.C0693id.simple_stt);
            if (frameLayout5.getHeight() != i3) {
                Log.m19d(TAG, "updateSimpleLayout - sttViewActualHeight " + i3);
                updateViewHeight(frameLayout5, i3, i2);
                return;
            }
            return;
        }
        Log.m19d(TAG, "updateSimpleLayout - do not update");
    }

    private void updateSimpleLayoutLandScape() {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        Log.m26i(TAG, "updateSimpleLayoutLandScape");
        int i8 = this.mCurrentScene;
        if (i8 == 3 || i8 == 1 || (i7 = this.mCurrentEvent) == 2005 || i7 == 2006) {
            int intSettings = Settings.getIntSettings(Settings.KEY_SIMPLE_RECORD_MODE, -1);
            int intSettings2 = Settings.getIntSettings(Settings.KEY_SIMPLE_PLAY_MODE, -1);
            Log.m26i(TAG, "updateSimpleLayoutLandScape - recordMode: " + intSettings + " - playMode: " + intSettings2 + " - scene: " + this.mCurrentScene);
            int i9 = this.mActivity.getResources().getDisplayMetrics().heightPixels;
            int i10 = this.mActivity.getResources().getDisplayMetrics().widthPixels;
            int statusBarHeight = i9 - StatusBarHelper.getStatusBarHeight(this.mActivity);
            int actionBarHeight = statusBarHeight - getActionBarHeight();
            if (actionBarHeight <= 0) {
                Log.m26i(TAG, "updateSimpleLayoutLandScape - mainViewHeight = 0 ");
                return;
            }
            int dimensionPixelSize = this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.main_controlbutton_height);
            int dimensionPixelSize2 = this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.main_controlbutton_margin_top);
            int dimensionPixelSize3 = this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.main_controlbutton_margin_bottom);
            updateSimpleControlMargin();
            int dimensionPixelSize4 = this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.multi_window_stt_margin_top);
            int i11 = (statusBarHeight * 51) / 100;
            int i12 = 0;
            if (4 == intSettings || 4 == intSettings2) {
                i11 = (statusBarHeight * 40) / 100;
                i = ((((actionBarHeight - i11) - dimensionPixelSize) - dimensionPixelSize2) - dimensionPixelSize3) - (dimensionPixelSize4 * 2);
            } else {
                i = 0;
            }
            WaveProvider.getInstance().setWaveHeight(i11, (i11 - this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.wave_time_text_height)) - this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.wave_bookmark_top_margin), true);
            int dimensionPixelSize5 = i11 + this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.voice_note_trim_handler_red_height);
            int dimensionPixelSize6 = this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.multi_window_toolbar_margin_offset);
            if (isIncludeSimpleToolbar(this.mCurrentEvent)) {
                i12 = i10 / 2;
                i2 = dimensionPixelSize6 * 2;
                i3 = dimensionPixelSize;
            } else {
                i3 = 0;
                i2 = 0;
            }
            int i13 = i10 - i12;
            int i14 = i12 - (i2 * 2);
            if (isIncludeSimpleToolbar(this.mCurrentEvent)) {
                i4 = dimensionPixelSize6 * 3;
            } else {
                i4 = (i10 * 2) / 7;
            }
            FrameLayout frameLayout = (FrameLayout) this.mActivity.getWindow().getDecorView().findViewById(C0690R.C0693id.simple_toolbar);
            if (frameLayout != null) {
                Log.m19d(TAG, "updateSimpleLayoutLandScape - toolbarViewActualHeight " + i3);
                i5 = dimensionPixelSize4;
                i6 = dimensionPixelSize5;
                updateViewHeight(frameLayout, i3, i14, 0, 0, i2, i2);
            } else {
                i5 = dimensionPixelSize4;
                i6 = dimensionPixelSize5;
            }
            FrameLayout frameLayout2 = (FrameLayout) this.mActivity.getWindow().getDecorView().findViewById(C0690R.C0693id.simple_controlbutton);
            if (frameLayout2 != null) {
                updateViewHeight(frameLayout2, dimensionPixelSize, i13, 0, 0, i4, i4);
                Log.m19d(TAG, "updateSimpleLayoutLandScape - mainControlButton: height = " + dimensionPixelSize + " - margin: " + dimensionPixelSize3);
            }
            FrameLayout frameLayout3 = (FrameLayout) this.mActivity.getWindow().getDecorView().findViewById(C0690R.C0693id.simple_info);
            if (frameLayout3 != null) {
                Log.m19d(TAG, "updateSimpleLayoutLandScape - infoViewActualHeight " + i11);
                updateViewHeight(frameLayout3, i11);
            }
            FrameLayout frameLayout4 = (FrameLayout) this.mActivity.getWindow().getDecorView().findViewById(C0690R.C0693id.simple_wave);
            if (frameLayout4 != null) {
                Log.m19d(TAG, "updateSimpleLayoutLandScape - waveViewActualHeight " + i6);
                updateViewHeight(frameLayout4, i6);
            }
            FrameLayout frameLayout5 = (FrameLayout) this.mActivity.getWindow().getDecorView().findViewById(C0690R.C0693id.simple_stt);
            if (frameLayout5 != null) {
                Log.m19d(TAG, "updateSimpleLayoutLandScape - sttViewActualHeight " + i);
                updateViewHeight(frameLayout5, i, i5);
                return;
            }
            return;
        }
        Log.m19d(TAG, "updateSimpleLayoutLandScape - do not update");
    }

    private void updateSimpleControlMargin() {
        Log.m19d(TAG, "updateMainControlMargin");
        int recordButtonMarginBottom = getRecordButtonMarginBottom();
        RelativeLayout relativeLayout = (RelativeLayout) this.mActivity.getWindow().getDecorView().findViewById(C0690R.C0693id.control_button_layout);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) relativeLayout.getLayoutParams();
        layoutParams.bottomMargin = recordButtonMarginBottom;
        relativeLayout.setLayoutParams(layoutParams);
    }

    private int getRecordButtonMarginBottom() {
        if (this.mCurrentEvent == 17) {
            return 0;
        }
        if (DisplayManager.isInMultiWindowMode(this.mActivity)) {
            return this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.main_controlbutton_multi_window_margin_bottom);
        }
        int dimensionPixelSize = this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.main_controlbutton_margin_bottom);
        if (DisplayManager.isDeviceOnLandscape() && NavigationBarProvider.getInstance().isNavigationBarEnabled() && NavigationBarProvider.getInstance().isFullScreenGesture()) {
            return dimensionPixelSize - NavigationBarProvider.getInstance().getNavigationGestureHeight(this.mActivity);
        }
        if (this.mActivity.isInMultiWindowMode() || DisplayManager.isDeviceOnLandscape() || !NavigationBarProvider.getInstance().isNavigationBarEnabled() || HWKeyboardProvider.isHWKeyboard(this.mActivity) || !NavigationBarProvider.getInstance().isFullScreenGesture()) {
            return dimensionPixelSize;
        }
        return dimensionPixelSize + (NavigationBarProvider.getInstance().getNavigationNormalHeight() - NavigationBarProvider.getInstance().getNavigationGestureHeight(this.mActivity));
    }

    private void sceneChangeTransaction(AbsScene absScene, AbsScene absScene2, int i) {
        Log.m26i(TAG, "sceneChangeTransaction - newScene: " + absScene.getScene() + " - oldScene: " + absScene2.getScene() + " - newEvent: " + i);
        String[] tags = absScene2.getTags();
        String[] tags2 = absScene.getTags();
        List<String> newFragment = getNewFragment(tags, tags2);
        List<String> oldFragment = getOldFragment(tags, tags2);
        List<String> reuseFragment = getReuseFragment(tags, tags2);
        for (String next : oldFragment) {
            Log.m30v(TAG, "removeFragment : " + next, this.mSession);
            if (CONTROLBUTTON.equals(next)) {
                updateControlButtonLayout(false);
                removeFragment(next, 0, 0);
            } else {
                removeFragment(next, 0, 0);
            }
        }
        for (String next2 : newFragment) {
            Log.m30v(TAG, "addFragment : " + next2, this.mSession);
            if (CONTROLBUTTON.equals(next2)) {
                updateControlButtonLayout(true);
                addFragment(next2, i, 0, 0);
            }
            addFragment(next2, i, 0, 0);
        }
        for (String next3 : reuseFragment) {
            if (CONTROLBUTTON.equals(next3)) {
                updateControlButtonLayout(true);
            }
            updateFragment(next3, i);
        }
        if (absScene.getScene() != absScene2.getScene()) {
            notifyObservers(absScene.getScene());
        }
        this.mCurrentEvent = i;
        if (EVENT_SCENE_TABLE.get(i) != null) {
            this.mCurrentScene = EVENT_SCENE_TABLE.get(i).getScene();
        }
        if (this.mCurrentScene == 4) {
            setSeekbarVisibility(true);
        } else {
            setSeekbarVisibility(false);
        }
        if (!DisplayManager.isInMultiWindowMode(this.mActivity) || this.mLaunchMode != SimpleActivity.LaunchMode.SPEECHTOTEXT) {
            setMultiSttVisibility(false);
        } else {
            setMultiSttVisibility(true);
        }
        updateViewChange();
    }

    public void onDestroy() {
        Log.m27i(TAG, "onDestroy ", this.mSession);
        if (this.mActivity != null) {
            this.fragmentFactory.removeAll();
        }
        Observable observable = this.mObservable;
        if (observable != null) {
            observable.deleteObserver(this.mSession, this);
            this.mObservable = null;
        }
        this.mActivity = null;
        this.mLaunchMode = null;
        unregisterAllSceneChangeListener();
    }

    private class FragmentFactory {
        private final String TAG = "FragmentFactory";
        private final HashMap<String, AbsSimpleFragment> mMap = new HashMap<>();

        public FragmentFactory() {
            if (SimpleFragmentController.this.mActivity != null) {
                FragmentManager supportFragmentManager = SimpleFragmentController.this.mActivity.getSupportFragmentManager();
                for (String str : SimpleFragmentController.ALL_TAG) {
                    AbsSimpleFragment absSimpleFragment = (AbsSimpleFragment) supportFragmentManager.findFragmentByTag(str);
                    if (absSimpleFragment != null) {
                        Log.m29v("FragmentFactory", "FragmentFactory() : fragment :" + str);
                        this.mMap.put(str, absSimpleFragment);
                    }
                }
            }
        }

        public AbsSimpleFragment get(String str) {
            return this.mMap.get(str);
        }

        private void put(String str, AbsSimpleFragment absSimpleFragment) {
            if (absSimpleFragment != null) {
                this.mMap.put(str, absSimpleFragment);
            }
        }

        public void remove(String str) {
            this.mMap.remove(str);
        }

        public void removeAll() {
            this.mMap.clear();
        }

        public AbsSimpleFragment create(String str) {
            if (get(str) == null) {
                return createFragment(str);
            }
            return get(str);
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private com.sec.android.app.voicenote.p007ui.AbsSimpleFragment createFragment(java.lang.String r4) {
            /*
                r3 = this;
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r1 = "createFragment : "
                r0.append(r1)
                r0.append(r4)
                java.lang.String r0 = r0.toString()
                com.sec.android.app.voicenote.uicore.SimpleFragmentController r1 = com.sec.android.app.voicenote.uicore.SimpleFragmentController.this
                java.lang.String r1 = r1.mSession
                java.lang.String r2 = "FragmentFactory"
                com.sec.android.app.voicenote.provider.Log.m30v((java.lang.String) r2, (java.lang.String) r0, (java.lang.String) r1)
                int r0 = r4.hashCode()
                switch(r0) {
                    case -1053166540: goto L_0x0081;
                    case -463747543: goto L_0x0077;
                    case -121801387: goto L_0x006d;
                    case 83475: goto L_0x0063;
                    case 2283726: goto L_0x0059;
                    case 2688793: goto L_0x004f;
                    case 524559195: goto L_0x0044;
                    case 797320685: goto L_0x003a;
                    case 1061258805: goto L_0x002f;
                    case 1197567695: goto L_0x0025;
                    default: goto L_0x0023;
                }
            L_0x0023:
                goto L_0x008b
            L_0x0025:
                java.lang.String r0 = "ControlButton"
                boolean r0 = r4.equals(r0)
                if (r0 == 0) goto L_0x008b
                r0 = 6
                goto L_0x008c
            L_0x002f:
                java.lang.String r0 = "Multi_Toolbar"
                boolean r0 = r4.equals(r0)
                if (r0 == 0) goto L_0x008b
                r0 = 9
                goto L_0x008c
            L_0x003a:
                java.lang.String r0 = "Multi_Stt"
                boolean r0 = r4.equals(r0)
                if (r0 == 0) goto L_0x008b
                r0 = 5
                goto L_0x008c
            L_0x0044:
                java.lang.String r0 = "Toolbar"
                boolean r0 = r4.equals(r0)
                if (r0 == 0) goto L_0x008b
                r0 = 8
                goto L_0x008c
            L_0x004f:
                java.lang.String r0 = "Wave"
                boolean r0 = r4.equals(r0)
                if (r0 == 0) goto L_0x008b
                r0 = 2
                goto L_0x008c
            L_0x0059:
                java.lang.String r0 = "Info"
                boolean r0 = r4.equals(r0)
                if (r0 == 0) goto L_0x008b
                r0 = 0
                goto L_0x008c
            L_0x0063:
                java.lang.String r0 = "Stt"
                boolean r0 = r4.equals(r0)
                if (r0 == 0) goto L_0x008b
                r0 = 4
                goto L_0x008c
            L_0x006d:
                java.lang.String r0 = "Multi_Seekbar"
                boolean r0 = r4.equals(r0)
                if (r0 == 0) goto L_0x008b
                r0 = 3
                goto L_0x008c
            L_0x0077:
                java.lang.String r0 = "Multi_ControlButton"
                boolean r0 = r4.equals(r0)
                if (r0 == 0) goto L_0x008b
                r0 = 7
                goto L_0x008c
            L_0x0081:
                java.lang.String r0 = "Multi_Info"
                boolean r0 = r4.equals(r0)
                if (r0 == 0) goto L_0x008b
                r0 = 1
                goto L_0x008c
            L_0x008b:
                r0 = -1
            L_0x008c:
                switch(r0) {
                    case 0: goto L_0x00af;
                    case 1: goto L_0x00af;
                    case 2: goto L_0x00a9;
                    case 3: goto L_0x00a3;
                    case 4: goto L_0x009d;
                    case 5: goto L_0x009d;
                    case 6: goto L_0x0097;
                    case 7: goto L_0x0097;
                    case 8: goto L_0x0091;
                    case 9: goto L_0x0091;
                    default: goto L_0x008f;
                }
            L_0x008f:
                r0 = 0
                goto L_0x00b4
            L_0x0091:
                com.sec.android.app.voicenote.ui.SimpleToolbarFragment r0 = new com.sec.android.app.voicenote.ui.SimpleToolbarFragment
                r0.<init>()
                goto L_0x00b4
            L_0x0097:
                com.sec.android.app.voicenote.ui.SimpleControlButtonFragment r0 = new com.sec.android.app.voicenote.ui.SimpleControlButtonFragment
                r0.<init>()
                goto L_0x00b4
            L_0x009d:
                com.sec.android.app.voicenote.ui.SimpleSttFragment r0 = new com.sec.android.app.voicenote.ui.SimpleSttFragment
                r0.<init>()
                goto L_0x00b4
            L_0x00a3:
                com.sec.android.app.voicenote.ui.SimpleSeekFragment r0 = new com.sec.android.app.voicenote.ui.SimpleSeekFragment
                r0.<init>()
                goto L_0x00b4
            L_0x00a9:
                com.sec.android.app.voicenote.ui.SimpleWaveFragment r0 = new com.sec.android.app.voicenote.ui.SimpleWaveFragment
                r0.<init>()
                goto L_0x00b4
            L_0x00af:
                com.sec.android.app.voicenote.ui.SimpleInfoFragment r0 = new com.sec.android.app.voicenote.ui.SimpleInfoFragment
                r0.<init>()
            L_0x00b4:
                if (r0 == 0) goto L_0x00cb
                com.sec.android.app.voicenote.uicore.SimpleFragmentController r1 = com.sec.android.app.voicenote.uicore.SimpleFragmentController.this
                java.lang.String r1 = r1.mSession
                r0.setSession(r1)
                com.sec.android.app.voicenote.uicore.SimpleFragmentController r1 = com.sec.android.app.voicenote.uicore.SimpleFragmentController.this
                com.sec.android.app.voicenote.activity.SimpleActivity$LaunchMode r1 = r1.mLaunchMode
                r0.setmLaunchMode(r1)
                r3.put(r4, r0)
            L_0x00cb:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.uicore.SimpleFragmentController.FragmentFactory.createFragment(java.lang.String):com.sec.android.app.voicenote.ui.AbsSimpleFragment");
        }
    }

    private boolean containsListener(OnSceneChangeListener onSceneChangeListener) {
        ArrayList<WeakReference<OnSceneChangeListener>> arrayList = this.mListeners;
        if (!(arrayList == null || onSceneChangeListener == null)) {
            Iterator<WeakReference<OnSceneChangeListener>> it = arrayList.iterator();
            while (it.hasNext()) {
                WeakReference next = it.next();
                if (next != null && next.get() != null && ((OnSceneChangeListener) next.get()).equals(onSceneChangeListener)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void removeListener(OnSceneChangeListener onSceneChangeListener) {
        ArrayList<WeakReference<OnSceneChangeListener>> arrayList = this.mListeners;
        if (arrayList != null && onSceneChangeListener != null) {
            synchronized (arrayList) {
                for (int size = this.mListeners.size() - 1; size >= 0; size--) {
                    WeakReference weakReference = this.mListeners.get(size);
                    if (weakReference.get() == null || ((OnSceneChangeListener) weakReference.get()).equals(onSceneChangeListener)) {
                        this.mListeners.remove(weakReference);
                    }
                }
            }
        }
    }

    public final void registerSceneChangeListener(OnSceneChangeListener onSceneChangeListener) {
        if (onSceneChangeListener != null && !containsListener(onSceneChangeListener)) {
            this.mListeners.add(new WeakReference(onSceneChangeListener));
            Log.m27i(TAG, "registerSceneChangeListener mCurrentEvent : " + this.mCurrentEvent, this.mSession);
            onSceneChangeListener.onSceneChange(EVENT_SCENE_TABLE.get(this.mCurrentEvent).getScene());
        }
    }

    public final void unregisterSceneChangeListener(OnSceneChangeListener onSceneChangeListener) {
        if (onSceneChangeListener != null && containsListener(onSceneChangeListener)) {
            removeListener(onSceneChangeListener);
        }
    }

    private void unregisterAllSceneChangeListener() {
        synchronized (this.mListeners) {
            this.mListeners.clear();
        }
    }

    private void notifyObservers(int i) {
        Log.m27i(TAG, "notifyObservers scene : " + i, this.mSession);
        for (int size = this.mListeners.size() + -1; size >= 0; size--) {
            WeakReference weakReference = this.mListeners.get(size);
            if (weakReference.get() == null) {
                this.mListeners.remove(weakReference);
            } else {
                ((OnSceneChangeListener) weakReference.get()).onSceneChange(i);
            }
        }
    }

    private void reorganizeScene() {
        Log.m26i(TAG, "reorganizeScene");
        if (this.PLAY_SCENE instanceof FullScreenPlayScene) {
            if (Settings.getIntSettings(Settings.KEY_SIMPLE_PLAY_MODE, 1) == 4) {
                if (!this.PLAY_SCENE.contains(STT)) {
                    this.PLAY_SCENE.addTag(STT);
                }
            } else if (this.PLAY_SCENE.contains(STT)) {
                this.PLAY_SCENE.removeTag(STT);
            }
            if (this.mLaunchMode == SimpleActivity.LaunchMode.SFINDER) {
                if (!this.PLAY_SCENE.contains(TOOLBAR)) {
                    this.PLAY_SCENE.addTag(TOOLBAR);
                }
            } else if (this.PLAY_SCENE.contains(TOOLBAR)) {
                this.PLAY_SCENE.removeTag(TOOLBAR);
            }
        }
    }

    private void updateViewHeight(View view, int i) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        layoutParams.height = i;
        view.setLayoutParams(layoutParams);
    }

    private void updateViewHeight(View view, int i, int i2, int i3, int i4, int i5, int i6) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        layoutParams.height = i;
        layoutParams.width = i2;
        layoutParams.topMargin = i3;
        layoutParams.bottomMargin = i4;
        layoutParams.leftMargin = i5;
        layoutParams.rightMargin = i6;
        view.setLayoutParams(layoutParams);
    }

    private void updateViewHeight(View view, int i, int i2) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        layoutParams.height = i;
        layoutParams.topMargin = i2;
        view.setLayoutParams(layoutParams);
    }

    private void updateViewHeight(View view, int i, int i2, int i3, int i4, int i5) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        layoutParams.height = i;
        layoutParams.topMargin = i2;
        layoutParams.bottomMargin = i3;
        layoutParams.leftMargin = i4;
        layoutParams.rightMargin = i5;
        view.setLayoutParams(layoutParams);
    }

    private int getStatusBarHeight() {
        Resources resources = this.mActivity.getResources();
        int identifier = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (identifier > 0) {
            return resources.getDimensionPixelSize(identifier);
        }
        Log.m19d(TAG, "getStatusBarHeight - default");
        return (int) Math.ceil((double) (resources.getDisplayMetrics().density * 24.0f));
    }

    private int getActionBarHeight() {
        TypedValue typedValue = new TypedValue();
        if (this.mActivity.getTheme().resolveAttribute(16843499, typedValue, true)) {
            return TypedValue.complexToDimensionPixelSize(typedValue.data, this.mActivity.getResources().getDisplayMetrics());
        }
        Log.m19d(TAG, "getActionBarHeight - default");
        return (int) Math.ceil((double) (this.mActivity.getResources().getDisplayMetrics().density * 56.0f));
    }

    private void updateControlButtonLayout(boolean z) {
        Log.m27i(TAG, "updateControlButtonLayout - show : " + z, this.mSession);
        FrameLayout frameLayout = (FrameLayout) this.mActivity.getWindow().getDecorView().findViewById(C0690R.C0693id.main_controlbutton);
        if (frameLayout == null) {
            return;
        }
        if (z) {
            if (frameLayout.getVisibility() != 0) {
                frameLayout.setVisibility(0);
            }
        } else if (frameLayout.getVisibility() != 8) {
            frameLayout.setVisibility(8);
        }
    }

    private void setSeekbarVisibility(boolean z) {
        Log.m26i(TAG, "setSeekbarVisibility : " + z);
        FrameLayout frameLayout = (FrameLayout) this.mActivity.getWindow().getDecorView().findViewById(C0690R.C0693id.simple_multi_seekbar);
        if (frameLayout == null) {
            return;
        }
        if (z) {
            if (frameLayout.getVisibility() != 0) {
                frameLayout.setVisibility(0);
            }
        } else if (frameLayout.getVisibility() != 8) {
            frameLayout.setVisibility(8);
        }
    }

    private void setMultiSttVisibility(boolean z) {
        Log.m26i(TAG, "setMultiSttVisibility : " + z);
        FrameLayout frameLayout = (FrameLayout) this.mActivity.getWindow().getDecorView().findViewById(C0690R.C0693id.simple_multi_stt);
        if (frameLayout == null) {
            return;
        }
        if (z) {
            if (frameLayout.getVisibility() != 0) {
                frameLayout.setVisibility(0);
            }
        } else if (frameLayout.getVisibility() != 8) {
            frameLayout.setVisibility(8);
        }
    }

    private class FullScreenRecordScene extends RecordScene {
        FullScreenRecordScene() {
            super();
            this.mScene = 1;
            this.mTags.add(SimpleFragmentController.INFO);
            this.mTags.add("Wave");
            if (SimpleFragmentController.this.mLaunchMode == SimpleActivity.LaunchMode.SPEECHTOTEXT) {
                if (!this.mTags.contains(SimpleFragmentController.STT)) {
                    this.mTags.add(SimpleFragmentController.STT);
                }
            } else if (this.mTags.contains(SimpleFragmentController.STT)) {
                this.mTags.remove(SimpleFragmentController.STT);
            }
            this.mTags.add(SimpleFragmentController.CONTROLBUTTON);
        }
    }

    private class FullScreenPlayScene extends PlayScene {
        FullScreenPlayScene() {
            super();
            this.mScene = 3;
            this.mTags.add(SimpleFragmentController.INFO);
            this.mTags.add("Wave");
            this.mTags.add(SimpleFragmentController.CONTROLBUTTON);
        }
    }

    private class MultiWindowRecordScene extends RecordScene {
        MultiWindowRecordScene() {
            super();
            this.mScene = 2;
            this.mTags.add(SimpleFragmentController.MULTI_INFO);
            if (SimpleFragmentController.this.mLaunchMode == SimpleActivity.LaunchMode.SPEECHTOTEXT) {
                if (!this.mTags.contains(SimpleFragmentController.MULTI_STT)) {
                    this.mTags.add(SimpleFragmentController.MULTI_STT);
                }
            } else if (this.mTags.contains(SimpleFragmentController.MULTI_STT)) {
                this.mTags.remove(SimpleFragmentController.MULTI_STT);
            }
            this.mTags.add(SimpleFragmentController.MULTI_CONTROLBUTTON);
        }
    }

    private class MultiWindowPlayScene extends PlayScene {
        MultiWindowPlayScene() {
            super();
            this.mScene = 4;
            this.mTags.add(SimpleFragmentController.MULTI_INFO);
            this.mTags.add(SimpleFragmentController.MULTI_SEEKBAR);
            this.mTags.add(SimpleFragmentController.MULTI_CONTROLBUTTON);
            if (SimpleFragmentController.this.mLaunchMode == SimpleActivity.LaunchMode.SPEECHTOTEXT) {
                if (!this.mTags.contains(SimpleFragmentController.MULTI_STT)) {
                    this.mTags.add(SimpleFragmentController.MULTI_STT);
                }
            } else if (this.mTags.contains(SimpleFragmentController.MULTI_STT)) {
                this.mTags.remove(SimpleFragmentController.MULTI_STT);
            }
        }
    }

    private class PlayScene extends AbsScene {
        private PlayScene() {
            super();
        }
    }

    private class RecordScene extends AbsScene {
        private RecordScene() {
            super();
        }
    }

    private class AbsScene {
        int mScene;
        final ArrayList<String> mTags;

        private AbsScene() {
            this.mScene = 0;
            this.mTags = new ArrayList<>();
        }

        public int getScene() {
            return this.mScene;
        }

        public String[] getTags() {
            int size = this.mTags.size();
            if (size == 0) {
                return null;
            }
            return (String[]) this.mTags.toArray(new String[size]);
        }

        public void addTag(String str) {
            this.mTags.add(str);
        }

        public boolean contains(String str) {
            return this.mTags.contains(str);
        }

        public void removeTag(String str) {
            this.mTags.remove(str);
        }
    }

    private boolean isInMultiWindow() {
        AppCompatActivity appCompatActivity = this.mActivity;
        return appCompatActivity != null && !appCompatActivity.isDestroyed() && this.mActivity.isInMultiWindowMode();
    }
}
