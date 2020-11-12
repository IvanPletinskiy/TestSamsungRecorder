package com.sec.android.app.voicenote.p007ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.common.util.VRUtil;
import com.sec.android.app.voicenote.p007ui.ControlButtonFactory;
import com.sec.android.app.voicenote.p007ui.animation.AnimationFactory;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.p007ui.view.TrimPopup;
import com.sec.android.app.voicenote.p007ui.view.ViewStateProvider;
import com.sec.android.app.voicenote.provider.AssistantProvider;
import com.sec.android.app.voicenote.provider.CallRejectChecker;
import com.sec.android.app.voicenote.provider.ContextMenuProvider;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.DBProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.GdprProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.MouseKeyboardProvider;
import com.sec.android.app.voicenote.provider.Network;
import com.sec.android.app.voicenote.provider.PermissionProvider;
import com.sec.android.app.voicenote.provider.PhoneStateProvider;
import com.sec.android.app.voicenote.provider.RecognizerDBProvider;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.SecureFolderProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.StorageProvider;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.provider.UPSMProvider;
import com.sec.android.app.voicenote.provider.ViewProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.service.AudioFormat;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.SimpleEngineManager;
import com.sec.android.app.voicenote.uicore.FragmentController;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.util.ArrayList;
import java.util.Locale;

/* renamed from: com.sec.android.app.voicenote.ui.ControlButtonFragment */
public class ControlButtonFragment extends AbsFragment implements FragmentController.OnSceneChangeListener, Engine.OnEngineListener, DialogFactory.DialogResultListener {
    private static final int DELAY_DEFAULT_BUTTON = 350;
    private static final int DELAY_RECORD_STOP_BUTTON = 1200;
    private static final int SKIP_INTERVAL_1SEC = 1000;
    private static final int SKIP_INTERVAL_3SEC = 3000;
    private static final float SKIP_INTERVAL_LIMIT_RATIO = 0.4f;
    private static final int SKIP_INTERVAL_NEXT = 901;
    private static final int SKIP_INTERVAL_PREV = 900;
    private static final int SKIP_INTERVAL_STANDARD_DURATION = 15000;
    private static final String TAG = "ControlButtonFragment";
    private static ControlButtonFactory.AbsButton mCurrentButton;
    private AccessibilityManager mAccessibilityManager;
    private AccessibilityManager.AccessibilityStateChangeListener mAccessibilityStateChangeListener;
    private ControlButtonFactory mButtonFactory;
    private View mContainerView;
    private Handler mEngineEventHandler = null;
    /* access modifiers changed from: private */
    public boolean mIsFirstTime = true;
    private boolean mIsSaveAfterEdit = false;
    private boolean mIsShowingGuide = false;
    private boolean mIsTrimInProgress = false;
    private View mRootViewTmp;
    private int mScene = 0;
    /* access modifiers changed from: private */
    public boolean mShouldResumeForSkipInterval = false;
    /* access modifiers changed from: private */
    public Handler mSkipIntervalEventHandler = new Handler(new Handler.Callback() {
        public boolean handleMessage(Message message) {
            int i = message.what;
            if (i == ControlButtonFragment.SKIP_INTERVAL_PREV) {
                Engine.getInstance().skipInterval(ControlButtonFragment.this.mSkippedTime);
            } else if (i == ControlButtonFragment.SKIP_INTERVAL_NEXT) {
                int duration = Engine.getInstance().getDuration();
                int currentTime = Engine.getInstance().getCurrentTime();
                if (Engine.getInstance().getRepeatMode() == 4) {
                    int[] repeatPosition = Engine.getInstance().getRepeatPosition();
                    duration = Math.max(repeatPosition[0], repeatPosition[1]);
                }
                if (duration <= ControlButtonFragment.this.mSkippedTime + currentTime) {
                    int unused = ControlButtonFragment.this.mSkippedTime = duration - currentTime;
                    if (Engine.getInstance().getRepeatMode() != 4) {
                        boolean unused2 = ControlButtonFragment.this.mShouldResumeForSkipInterval = false;
                    }
                    Engine.getInstance().skipInterval(ControlButtonFragment.this.mSkippedTime);
                    ControlButtonFragment.this.stopSkipInterval();
                    return false;
                }
                Engine.getInstance().skipInterval(ControlButtonFragment.this.mSkippedTime);
            }
            if (Math.abs(ControlButtonFragment.this.mSkippedTime) <= ControlButtonFragment.this.mSkipIntervalLimit) {
                ControlButtonFragment controlButtonFragment = ControlButtonFragment.this;
                int unused3 = controlButtonFragment.mSkippedTime = controlButtonFragment.mSkippedTime + ControlButtonFragment.this.mSkipIntervalValue;
            }
            ControlButtonFragment.this.mSkipIntervalEventHandler.removeMessages(message.what);
            ControlButtonFragment.this.mSkipIntervalEventHandler.sendEmptyMessageDelayed(message.what, 200);
            return false;
        }
    });
    /* access modifiers changed from: private */
    public int mSkipIntervalLimit;
    /* access modifiers changed from: private */
    public int mSkipIntervalValue = SKIP_INTERVAL_3SEC;
    /* access modifiers changed from: private */
    public int mSkippedTime;
    private TrimPopup mTrimPopup;

    static /* synthetic */ boolean lambda$showConvertSTTGuide$31(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ boolean lambda$showOverwriteGuide$29(View view, MotionEvent motionEvent) {
        return true;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.m19d(TAG, "onCreate");
        this.mEngineEventHandler = new Handler(new Handler.Callback() {
            public final boolean handleMessage(Message message) {
                return ControlButtonFragment.this.lambda$onCreate$0$ControlButtonFragment(message);
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00be, code lost:
        if (r15 != 4) goto L_0x0277;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0158, code lost:
        if (r15 != 18) goto L_0x0277;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ boolean lambda$onCreate$0$ControlButtonFragment(android.os.Message r15) {
        /*
            r14 = this;
            androidx.fragment.app.FragmentActivity r0 = r14.getActivity()
            r1 = 0
            if (r0 == 0) goto L_0x0277
            boolean r0 = r14.isAdded()
            if (r0 == 0) goto L_0x0277
            boolean r0 = r14.isRemoving()
            if (r0 == 0) goto L_0x0015
            goto L_0x0277
        L_0x0015:
            int r0 = r14.mScene
            r2 = 2010(0x7da, float:2.817E-42)
            r3 = 1001(0x3e9, float:1.403E-42)
            r4 = 4
            r5 = 3
            r6 = 1
            if (r0 != r5) goto L_0x003f
            int r0 = r15.what
            if (r0 != r2) goto L_0x003e
            int r15 = r15.arg1
            if (r15 == r4) goto L_0x0035
            if (r15 != r6) goto L_0x002b
            goto L_0x0035
        L_0x002b:
            com.sec.android.app.voicenote.ui.ControlButtonFactory r15 = r14.mButtonFactory
            android.view.View r15 = r15.getView(r3)
            r14.enableButton(r15, r1)
            goto L_0x003e
        L_0x0035:
            com.sec.android.app.voicenote.ui.ControlButtonFactory r15 = r14.mButtonFactory
            android.view.View r15 = r15.getView(r3)
            r14.enableButton(r15, r6)
        L_0x003e:
            return r1
        L_0x003f:
            int r7 = r15.what
            r8 = 101(0x65, float:1.42E-43)
            r9 = 5004(0x138c, float:7.012E-42)
            r10 = 12
            r11 = 6
            if (r7 == r8) goto L_0x023b
            r0 = 1010(0x3f2, float:1.415E-42)
            java.lang.String r8 = " arg : "
            r12 = 2
            java.lang.String r13 = "ControlButtonFragment"
            if (r7 == r0) goto L_0x0199
            r0 = 3010(0xbc2, float:4.218E-42)
            if (r7 == r0) goto L_0x012a
            r0 = 2002(0x7d2, float:2.805E-42)
            r3 = 8
            if (r7 == r2) goto L_0x009a
            r15 = 2011(0x7db, float:2.818E-42)
            if (r7 == r15) goto L_0x0063
            goto L_0x0277
        L_0x0063:
            java.lang.String r15 = "onPlayerUpdate - Player.INFO_PLAY_COMPLETE"
            com.sec.android.app.voicenote.provider.Log.m19d(r13, r15)
            int r15 = r14.mScene
            if (r15 == r11) goto L_0x008f
            if (r15 == r3) goto L_0x0084
            if (r15 == r10) goto L_0x0079
            java.lang.Integer r15 = java.lang.Integer.valueOf(r0)
            r14.onUpdate(r15)
            goto L_0x0277
        L_0x0079:
            r15 = 7002(0x1b5a, float:9.812E-42)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            r14.onUpdate(r15)
            goto L_0x0277
        L_0x0084:
            r15 = 1008(0x3f0, float:1.413E-42)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            r14.onUpdate(r15)
            goto L_0x0277
        L_0x008f:
            r15 = 5002(0x138a, float:7.009E-42)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            r14.onUpdate(r15)
            goto L_0x0277
        L_0x009a:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r7 = "onPlayerUpdate - status : "
            r2.append(r7)
            int r7 = r15.what
            r2.append(r7)
            r2.append(r8)
            int r7 = r15.arg1
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r13, r2)
            int r15 = r15.arg1
            if (r15 == r12) goto L_0x00f8
            if (r15 == r5) goto L_0x00c2
            if (r15 == r4) goto L_0x00f8
            goto L_0x0277
        L_0x00c2:
            int r15 = r14.mScene
            if (r15 == r12) goto L_0x0277
            if (r15 == r11) goto L_0x00ed
            if (r15 == r3) goto L_0x00e2
            if (r15 == r10) goto L_0x00d7
            r15 = 2003(0x7d3, float:2.807E-42)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            r14.onUpdate(r15)
            goto L_0x0277
        L_0x00d7:
            r15 = 7003(0x1b5b, float:9.813E-42)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            r14.onUpdate(r15)
            goto L_0x0277
        L_0x00e2:
            r15 = 1007(0x3ef, float:1.411E-42)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            r14.onUpdate(r15)
            goto L_0x0277
        L_0x00ed:
            r15 = 5003(0x138b, float:7.01E-42)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            r14.onUpdate(r15)
            goto L_0x0277
        L_0x00f8:
            int r15 = r14.mScene
            if (r15 == r12) goto L_0x0277
            if (r15 == r11) goto L_0x0116
            if (r15 == r3) goto L_0x010b
            if (r15 == r10) goto L_0x0277
            java.lang.Integer r15 = java.lang.Integer.valueOf(r0)
            r14.onUpdate(r15)
            goto L_0x0277
        L_0x010b:
            r15 = 1008(0x3f0, float:1.413E-42)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            r14.onUpdate(r15)
            goto L_0x0277
        L_0x0116:
            com.sec.android.app.voicenote.ui.ControlButtonFactory r15 = r14.mButtonFactory
            android.view.View r15 = r15.getView(r9)
            r14.enableButton(r15, r6)
            r15 = 5002(0x138a, float:7.009E-42)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            r14.onUpdate(r15)
            goto L_0x0277
        L_0x012a:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "onEditorState - status : "
            r0.append(r2)
            int r2 = r15.what
            r0.append(r2)
            r0.append(r8)
            int r2 = r15.arg1
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r13, r0)
            int r15 = r15.arg1
            if (r15 == 0) goto L_0x0188
            if (r15 == r6) goto L_0x015c
            if (r15 == r5) goto L_0x0188
            if (r15 == r4) goto L_0x015c
            r0 = 17
            if (r15 == r0) goto L_0x0188
            r0 = 18
            if (r15 == r0) goto L_0x015c
            goto L_0x0277
        L_0x015c:
            int r15 = r14.mScene
            if (r15 != r11) goto L_0x0277
            r14.mIsTrimInProgress = r1
            r15 = 5
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            r14.onUpdate(r15)
            boolean r15 = r14.mIsSaveAfterEdit
            if (r15 == 0) goto L_0x0277
            androidx.fragment.app.FragmentManager r15 = r14.getFragmentManager()
            java.lang.String r0 = "EditSaveDialog"
            boolean r15 = com.sec.android.app.voicenote.p007ui.dialog.DialogFactory.isDialogVisible(r15, r0)
            if (r15 != 0) goto L_0x0184
            androidx.fragment.app.FragmentManager r15 = r14.getFragmentManager()
            r0 = 0
            java.lang.String r2 = "EditSaveDialog"
            com.sec.android.app.voicenote.p007ui.dialog.DialogFactory.show(r15, r2, r0)
        L_0x0184:
            r14.mIsSaveAfterEdit = r1
            goto L_0x0277
        L_0x0188:
            int r15 = r14.mScene
            if (r15 != r11) goto L_0x0277
            r14.mIsTrimInProgress = r6
            r15 = 5006(0x138e, float:7.015E-42)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            r14.onUpdate(r15)
            goto L_0x0277
        L_0x0199:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "onRecorderUpdate - status : "
            r0.append(r2)
            int r2 = r15.what
            r0.append(r2)
            r0.append(r8)
            int r2 = r15.arg1
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r13, r0)
            int r15 = r15.arg1
            if (r15 == r12) goto L_0x0227
            r0 = 5005(0x138d, float:7.013E-42)
            if (r15 == r5) goto L_0x01fd
            if (r15 == r4) goto L_0x01c3
            goto L_0x0277
        L_0x01c3:
            int r15 = r14.mScene
            if (r15 == r11) goto L_0x01e7
            java.lang.String r15 = "record_mode"
            int r15 = com.sec.android.app.voicenote.provider.Settings.getIntSettings(r15, r6)
            if (r15 == r4) goto L_0x0277
            com.sec.android.app.voicenote.ui.ControlButtonFactory r15 = r14.mButtonFactory
            r0 = 1007(0x3ef, float:1.411E-42)
            android.view.View r15 = r15.getView(r0)
            r14.enableButton(r15, r6)
            com.sec.android.app.voicenote.ui.ControlButtonFactory r15 = r14.mButtonFactory
            r0 = 1004(0x3ec, float:1.407E-42)
            android.view.View r15 = r15.getView(r0)
            r14.enableButton(r15, r6)
            goto L_0x0277
        L_0x01e7:
            com.sec.android.app.voicenote.ui.ControlButtonFactory r15 = r14.mButtonFactory
            android.view.View r15 = r15.getView(r0)
            r14.enableButton(r15, r6)
            boolean r15 = r14.mIsTrimInProgress
            if (r15 != 0) goto L_0x0277
            java.lang.Integer r15 = java.lang.Integer.valueOf(r0)
            r14.onUpdate(r15)
            goto L_0x0277
        L_0x01fd:
            int r15 = r14.mScene
            if (r15 == r11) goto L_0x020b
            r15 = 1002(0x3ea, float:1.404E-42)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            r14.onUpdate(r15)
            goto L_0x021b
        L_0x020b:
            com.sec.android.app.voicenote.ui.ControlButtonFactory r15 = r14.mButtonFactory
            android.view.View r15 = r15.getView(r9)
            r14.enableButton(r15, r6)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r0)
            r14.onUpdate(r15)
        L_0x021b:
            com.sec.android.app.voicenote.ui.ControlButtonFactory r15 = r14.mButtonFactory
            r0 = 1004(0x3ec, float:1.407E-42)
            android.view.View r15 = r15.getView(r0)
            r14.enableButton(r15, r6)
            goto L_0x0277
        L_0x0227:
            int r15 = r14.mScene
            if (r15 == r11) goto L_0x0233
            java.lang.Integer r15 = java.lang.Integer.valueOf(r3)
            r14.onUpdate(r15)
            goto L_0x0277
        L_0x0233:
            java.lang.Integer r15 = java.lang.Integer.valueOf(r9)
            r14.onUpdate(r15)
            goto L_0x0277
        L_0x023b:
            if (r0 != r11) goto L_0x0254
            com.sec.android.app.voicenote.service.Engine r15 = com.sec.android.app.voicenote.service.Engine.getInstance()
            int r15 = r15.getPlayerState()
            if (r15 == r5) goto L_0x0254
            boolean r15 = r14.mIsTrimInProgress
            if (r15 != 0) goto L_0x0254
            com.sec.android.app.voicenote.ui.ControlButtonFactory r15 = r14.mButtonFactory
            android.view.View r15 = r15.getView(r9)
            r14.enableButton(r15, r6)
        L_0x0254:
            com.sec.android.app.voicenote.ui.view.ViewStateProvider r15 = com.sec.android.app.voicenote.p007ui.view.ViewStateProvider.getInstance()
            boolean r15 = r15.isConvertAnimationRunning()
            if (r15 != 0) goto L_0x0277
            int r15 = r14.mScene
            if (r15 != r10) goto L_0x0277
            com.sec.android.app.voicenote.service.Engine r15 = com.sec.android.app.voicenote.service.Engine.getInstance()
            int r15 = r15.getTranslationState()
            if (r15 != r6) goto L_0x0277
            com.sec.android.app.voicenote.ui.ControlButtonFactory r15 = r14.mButtonFactory
            r0 = 7001(0x1b59, float:9.81E-42)
            android.view.View r15 = r15.getView(r0)
            r14.enableButton(r15, r6)
        L_0x0277:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.ControlButtonFragment.lambda$onCreate$0$ControlButtonFragment(android.os.Message):boolean");
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view;
        Log.m26i(TAG, "onCreateView");
        if (DisplayManager.getVROrientation() == 1 || DisplayManager.getVROrientation() == 3) {
            Log.m19d(TAG, "Inflating Layout LAND");
            view = layoutInflater.inflate(C0690R.layout.fragment_controlbutton_land, viewGroup, false);
        } else {
            Log.m19d(TAG, "Inflating Layout PORT");
            view = layoutInflater.inflate(C0690R.layout.fragment_controlbutton, viewGroup, false);
        }
        this.mButtonFactory = new ControlButtonFactory(view);
        this.mContainerView = view;
        initAccessibilityFocus(view);
        View view2 = this.mButtonFactory.getView(1001);
        if (view2 != null) {
            view2.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ControlButtonFragment.this.lambda$onCreateView$1$ControlButtonFragment(view);
                }
            });
        }
        View view3 = this.mButtonFactory.getView(1002);
        if (view3 != null) {
//            view3.semSetHoverPopupType(1);
            view3.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ControlButtonFragment.this.lambda$onCreateView$2$ControlButtonFragment(view);
                }
            });
        }
        if (this.mIsShowingGuide) {
            showOverwriteGuide();
        }
        View view4 = this.mButtonFactory.getView(1003);
        if (view4 != null) {
//            view4.semSetHoverPopupType(1);
            view4.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ControlButtonFragment.this.lambda$onCreateView$3$ControlButtonFragment(view);
                }
            });
        }
        View view5 = this.mButtonFactory.getView(1004);
        if (view5 != null) {
//            view5.semSetHoverPopupType(1);
            view5.setContentDescription(AssistantProvider.getInstance().getButtonDescriptionForTalkback(C0690R.string.stop));
            view5.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ControlButtonFragment.this.lambda$onCreateView$4$ControlButtonFragment(view);
                }
            });
        }
        View view6 = this.mButtonFactory.getView(1007);
        if (view6 != null) {
//            view6.semSetHoverPopupType(1);
            view6.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ControlButtonFragment.this.lambda$onCreateView$5$ControlButtonFragment(view);
                }
            });
        }
        View view7 = this.mButtonFactory.getView(1008);
        if (view7 != null) {
//            view7.semSetHoverPopupType(1);
            view7.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ControlButtonFragment.this.lambda$onCreateView$6$ControlButtonFragment(view);
                }
            });
        }
        View view8 = this.mButtonFactory.getView(Event.PLAY_START);
        if (view8 != null) {
//            view8.semSetHoverPopupType(1);
            view8.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ControlButtonFragment.this.lambda$onCreateView$7$ControlButtonFragment(view);
                }
            });
        }
        View view9 = this.mButtonFactory.getView(Event.PLAY_PAUSE);
        if (view9 != null) {
//            view9.semSetHoverPopupType(1);
            view9.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ControlButtonFragment.this.lambda$onCreateView$8$ControlButtonFragment(view);
                }
            });
        }
        View view10 = this.mButtonFactory.getView(Event.PLAY_RW);
        if (view10 != null) {
//            view10.semSetHoverPopupType(1);
            view10.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ControlButtonFragment.this.lambda$onCreateView$9$ControlButtonFragment(view);
                }
            });
            view10.setOnLongClickListener(new View.OnLongClickListener() {
                public final boolean onLongClick(View view) {
                    return ControlButtonFragment.this.lambda$onCreateView$10$ControlButtonFragment(view);
                }
            });
            view10.setOnKeyListener(new View.OnKeyListener() {
                public final boolean onKey(View view, int i, KeyEvent keyEvent) {
                    return ControlButtonFragment.this.lambda$onCreateView$11$ControlButtonFragment(view, i, keyEvent);
                }
            });
            view10.setOnTouchListener(new View.OnTouchListener() {
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    return ControlButtonFragment.this.lambda$onCreateView$12$ControlButtonFragment(view, motionEvent);
                }
            });
        }
        View view11 = this.mButtonFactory.getView(Event.PLAY_FF);
        if (view11 != null) {
//            view11.semSetHoverPopupType(1);
            view11.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ControlButtonFragment.this.lambda$onCreateView$13$ControlButtonFragment(view);
                }
            });
            view11.setOnLongClickListener(new View.OnLongClickListener() {
                public final boolean onLongClick(View view) {
                    return ControlButtonFragment.this.lambda$onCreateView$14$ControlButtonFragment(view);
                }
            });
            view11.setOnKeyListener(new View.OnKeyListener() {
                public final boolean onKey(View view, int i, KeyEvent keyEvent) {
                    return ControlButtonFragment.this.lambda$onCreateView$15$ControlButtonFragment(view, i, keyEvent);
                }
            });
            view11.setOnTouchListener(new View.OnTouchListener() {
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    return ControlButtonFragment.this.lambda$onCreateView$16$ControlButtonFragment(view, motionEvent);
                }
            });
        }
        View view12 = this.mButtonFactory.getView(Event.EDIT_PLAY_START);
        if (view12 != null) {
//            view12.semSetHoverPopupType(1);
            view12.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ControlButtonFragment.this.lambda$onCreateView$17$ControlButtonFragment(view);
                }
            });
        }
        View view13 = this.mButtonFactory.getView(Event.EDIT_PLAY_PAUSE);
        if (view13 != null) {
//            view13.semSetHoverPopupType(1);
            view13.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ControlButtonFragment.this.lambda$onCreateView$18$ControlButtonFragment(view);
                }
            });
        }
        View view14 = this.mButtonFactory.getView(Event.EDIT_RECORD);
        if (view14 != null) {
//            view14.semSetHoverPopupType(1);
            view14.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ControlButtonFragment.this.lambda$onCreateView$19$ControlButtonFragment(view);
                }
            });
        }
        View view15 = this.mButtonFactory.getView(Event.EDIT_RECORD_PAUSE);
        if (view15 != null) {
//            view15.semSetHoverPopupType(1);
            view15.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ControlButtonFragment.this.lambda$onCreateView$20$ControlButtonFragment(view);
                }
            });
        }
        View view16 = this.mButtonFactory.getView(Event.EDIT_TRIM);
        if (view16 != null) {
            if (Settings.isEnabledShowButtonBG()) {
                ((ImageButton) view.findViewById(C0690R.C0693id.controlbutton_edit_trim_button)).setBackgroundResource(C0690R.C0692drawable.voice_note_btn_shape_drawable);
            }
//            view16.semSetHoverPopupType(1);
            view16.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ControlButtonFragment.this.lambda$onCreateView$21$ControlButtonFragment(view);
                }
            });
        }
        View view17 = this.mButtonFactory.getView(Event.TRANSLATION_START);
        if (view17 != null) {
            view17.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ControlButtonFragment.this.lambda$onCreateView$22$ControlButtonFragment(view);
                }
            });
        }
        View view18 = this.mButtonFactory.getView(Event.TRANSLATION_PAUSE);
        if (view18 != null) {
            view18.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ControlButtonFragment.this.lambda$onCreateView$23$ControlButtonFragment(view);
                }
            });
        }
        View view19 = this.mButtonFactory.getView(Event.TRANSLATION_RESUME);
        if (view19 != null) {
            view19.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ControlButtonFragment.this.lambda$onCreateView$24$ControlButtonFragment(view);
                }
            });
        }
        View view20 = this.mButtonFactory.getView(Event.TRANSLATION_SAVE);
        if (view20 != null) {
            view20.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ControlButtonFragment.this.lambda$onCreateView$25$ControlButtonFragment(view);
                }
            });
        }
        int i = this.mStartingEvent;
        if (i == 11 || i == 12 || i == 21) {
            int i2 = this.mScene;
            if (i2 == 1) {
                mCurrentButton = getButtons(4);
                this.mStartingEvent = 4;
            } else if (i2 == 2) {
                mCurrentButton = getButtons(3);
                this.mStartingEvent = 3;
            }
        } else {
            mCurrentButton = getButtons(1);
        }
        MouseKeyboardProvider.getInstance().mouseClickInteraction(getActivity(), this, this.mContainerView);
        onUpdate(Integer.valueOf(this.mStartingEvent));
        return view;
    }

    public /* synthetic */ void lambda$onCreateView$1$ControlButtonFragment(View view) {
        int playerState;
        Log.m26i(TAG, "Event.RECORD_START");
        SimpleEngineManager.getInstance().finishActiveSession();
        if (hasPostEvent(Event.BLOCK_CONTROL_BUTTONS)) {
            Log.m26i(TAG, "DISABLE_CONTROL_BUTTONS event posted ");
            backToIdleControlButton();
        } else if (!PermissionProvider.checkRecordPermission((AppCompatActivity) getActivity(), 2, C0690R.string.record)) {
            backToIdleControlButton();
        } else if (Engine.getInstance().getRecorderState() != 1) {
            Log.m32w(TAG, "Skip Event.RECORD_START - it is already recording state");
            backToIdleControlButton();
        } else if (Settings.getIntSettings("record_mode", 1) != 4 || !UPSMProvider.getInstance().isUltraPowerSavingMode()) {
            enableButtonDelayed(view);
            enableButtonDelayed(3, (int) DELAY_RECORD_STOP_BUTTON);
            enableButtonDelayed(4, (int) DELAY_RECORD_STOP_BUTTON);
            Engine.getInstance().clearContentItem();
            int intSettings = Settings.getIntSettings("record_mode", 1);
            if (intSettings == 4 && !Network.isNetworkConnected(getActivity())) {
                if (!DialogFactory.isDialogVisible(getFragmentManager(), DialogFactory.NETWORK_NOT_CONNECTED)) {
                    DialogFactory.show(getFragmentManager(), DialogFactory.NETWORK_NOT_CONNECTED, (Bundle) null);
                }
                backToIdleControlButton();
            } else if (intSettings == 4 && RecognizerDBProvider.getTOSAcceptedState() != 1) {
                if (!DialogFactory.isDialogVisible(getFragmentManager(), DialogFactory.PRIVACY_POLICY_DIALOG)) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(DialogFactory.BUNDLE_GDPR_COUNTRY, GdprProvider.getInstance().isGdprCountry());
                    DialogFactory.show(getFragmentManager(), DialogFactory.PRIVACY_POLICY_DIALOG, bundle);
                }
                backToIdleControlButton();
            } else if (needRecognizerTOS()) {
                backToIdleControlButton();
            } else {
                if (this.mScene == 3 && ((playerState = Engine.getInstance().getPlayerState()) == 3 || playerState == 4)) {
                    Engine.getInstance().stopPlay();
                }
                Engine.getInstance().setOriginalFilePath((String) null);
                boolean z = false;
                boolean z2 = this.mStartingEvent == 1991 || (Engine.getInstance().isBluetoothSCOConnected() && !Engine.getInstance().isWiredHeadSetConnected());
                Engine.getInstance().setRecordByBluetoothSCO(z2);
                Engine instance = Engine.getInstance();
                if (z2 && intSettings == 1) {
                    z = true;
                }
                int startRecord = instance.startRecord(new AudioFormat(intSettings, z));
                if (startRecord < 0) {
                    errorHandler(startRecord);
                    backToIdleControlButton();
                    return;
                }
                if (intSettings == 1) {
                    SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_ready_common), getActivity().getResources().getString(C0690R.string.event_standard_rec), "1");
                } else if (intSettings == 2) {
                    SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_ready_common), getActivity().getResources().getString(C0690R.string.event_standard_rec), "2");
                } else if (intSettings != 4) {
                    SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_ready_common), getActivity().getResources().getString(C0690R.string.event_standard_rec), "1");
                } else {
                    SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_ready_common), getActivity().getResources().getString(C0690R.string.event_standard_rec), SALogProvider.QUALITY_LOW);
                }
                postEvent(1001);
            }
        } else {
            Log.m26i(TAG, " Ultra Power saving Mode enabled can not record  ");
            backToIdleControlButton();
        }
    }

    public /* synthetic */ void lambda$onCreateView$2$ControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.RECORD_PAUSE");
        enableButtonDelayed(view);
        if (Engine.getInstance().pauseRecord()) {
            SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_recording_comm), getActivity().getResources().getString(C0690R.string.event_pause));
            postEvent(1002);
            if (Settings.getBooleanSettings(Settings.KEY_HELP_SHOW_OVERWRITE_GUIDE, true)) {
                View view2 = this.mRootViewTmp;
                if (view2 != null && ((ViewGroup) view2).getChildCount() >= 1) {
                    ((ViewGroup) this.mRootViewTmp).getChildAt(0).setImportantForAccessibility(4);
                    ((ViewGroup) ((ViewGroup) this.mRootViewTmp).getChildAt(0)).setDescendantFocusability(393216);
                }
                showOverwriteGuide();
                this.mIsShowingGuide = true;
            }
        }
    }

    public /* synthetic */ void lambda$onCreateView$3$ControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.RECORD_RESUME");
        enableButtonDelayed(view);
        if (PermissionProvider.checkRecordPermission((AppCompatActivity) getActivity(), 2, C0690R.string.record)) {
            int resumeRecord = Engine.getInstance().resumeRecord();
            if (resumeRecord < 0) {
                errorHandler(resumeRecord);
                return;
            }
            SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_recording_comm), getActivity().getResources().getString(C0690R.string.event_resume));
            postEvent(1003);
        }
    }

    public /* synthetic */ void lambda$onCreateView$4$ControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.RECORD_STOP");
        if (Engine.getInstance().isSaveEnable()) {
            if (Engine.getInstance().getPlayerState() == 3) {
                Engine.getInstance().pausePlay();
                postEvent(1008);
            } else {
                Engine.getInstance().pauseRecord();
                postEvent(1002);
            }
            SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_recording_comm), getActivity().getResources().getString(C0690R.string.event_stop));
            saveFileNameDialog();
        }
    }

    public /* synthetic */ void lambda$onCreateView$5$ControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.RECORD_PLAY_START");
        enableButtonDelayed(view);
        enableButtonDelayed(1003, (int) DELAY_DEFAULT_BUTTON);
        CursorProvider.getInstance().resetCurrentPlayingItemPosition();
        int resumePlay = Engine.getInstance().resumePlay();
        if (resumePlay < 0) {
            errorHandler(resumePlay);
            return;
        }
        SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_recording_comm), getActivity().getResources().getString(C0690R.string.event_play));
        postEvent(1007);
    }

    public /* synthetic */ void lambda$onCreateView$6$ControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.RECORD_PLAY_PAUSE");
        enableButtonDelayed(view);
        enableButtonDelayed(1003, (int) DELAY_DEFAULT_BUTTON);
        Engine.getInstance().pausePlay();
        CursorProvider.getInstance().resetCurrentPlayingItemPosition();
        postEvent(1008);
    }

    public /* synthetic */ void lambda$onCreateView$7$ControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.PLAY_START");
        enableButtonDelayed(view);
        boolean z = Engine.getInstance().getPlayerState() == 4;
        int resumePlay = Engine.getInstance().resumePlay();
        if (resumePlay == -103) {
            Toast.makeText(getActivity(), C0690R.string.no_play_during_call, 0).show();
        } else if (resumePlay == 0 && this.mScene == 4) {
            if (z) {
                postEvent(Event.PLAY_RESUME);
            } else {
                postEvent(Event.PLAY_START);
            }
            SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_player_comm), getActivity().getResources().getString(C0690R.string.event_player_play));
        }
    }

    public /* synthetic */ void lambda$onCreateView$8$ControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.PLAY_PAUSE");
        enableButtonDelayed(view);
        Engine.getInstance().pausePlay();
        if (this.mScene == 4) {
            postEvent(Event.PLAY_PAUSE);
            SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_player_comm), getActivity().getResources().getString(C0690R.string.event_player_pause));
        }
    }

    public /* synthetic */ void lambda$onCreateView$9$ControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.PLAY_RW");
        if (view.isEnabled()) {
            enableButtonDelayed(view);
            setSkipIntervalValue(SKIP_INTERVAL_PREV);
            Engine.getInstance().skipInterval(this.mSkipIntervalValue);
            SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_SEEK, 1);
            SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_player_comm), getActivity().getResources().getString(C0690R.string.event_player_rewind));
        }
    }

    public /* synthetic */ boolean lambda$onCreateView$10$ControlButtonFragment(View view) {
        Log.m26i(TAG, "onLongClick Event.PLAY_RW");
        stopSkipInterval();
        if (Engine.getInstance().getPlayerState() == 3) {
            this.mShouldResumeForSkipInterval = true;
            Engine.getInstance().pausePlay();
        }
        setSkipIntervalValue(SKIP_INTERVAL_PREV);
        this.mSkipIntervalEventHandler.sendEmptyMessageDelayed(SKIP_INTERVAL_PREV, 50);
        return true;
    }

    public /* synthetic */ boolean lambda$onCreateView$11$ControlButtonFragment(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() == 1 && keyEvent.getKeyCode() == 66) {
            stopSkipInterval();
            if (this.mShouldResumeForSkipInterval) {
                this.mShouldResumeForSkipInterval = false;
                if (Engine.getInstance().resumePlay() == 0) {
                    postEvent(Event.PLAY_RESUME);
                }
            }
        }
        return false;
    }

    public /* synthetic */ boolean lambda$onCreateView$12$ControlButtonFragment(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 1) {
            stopSkipInterval();
            if (this.mShouldResumeForSkipInterval) {
                this.mShouldResumeForSkipInterval = false;
                if (Engine.getInstance().resumePlay() == 0) {
                    postEvent(Event.PLAY_RESUME);
                }
            }
        }
        return false;
    }

    public /* synthetic */ void lambda$onCreateView$13$ControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.PLAY_FF");
        if (view.isEnabled()) {
            enableButtonDelayed(view);
            setSkipIntervalValue(SKIP_INTERVAL_NEXT);
            Engine.getInstance().skipInterval(this.mSkipIntervalValue);
            SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_SEEK, 1);
            SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_player_comm), getActivity().getResources().getString(C0690R.string.event_player_ff));
        }
    }

    public /* synthetic */ boolean lambda$onCreateView$14$ControlButtonFragment(View view) {
        Log.m26i(TAG, "onLongClick Event.PLAY_FF");
        stopSkipInterval();
        if (Engine.getInstance().getPlayerState() == 3) {
            this.mShouldResumeForSkipInterval = true;
            Engine.getInstance().pausePlay();
        }
        setSkipIntervalValue(SKIP_INTERVAL_NEXT);
        this.mSkipIntervalEventHandler.sendEmptyMessageDelayed(SKIP_INTERVAL_NEXT, 50);
        return true;
    }

    public /* synthetic */ boolean lambda$onCreateView$15$ControlButtonFragment(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() == 1 && keyEvent.getKeyCode() == 66) {
            stopSkipInterval();
            if (this.mShouldResumeForSkipInterval) {
                this.mShouldResumeForSkipInterval = false;
                if (Engine.getInstance().resumePlay() == 0) {
                    postEvent(Event.PLAY_RESUME);
                }
            }
        }
        return false;
    }

    public /* synthetic */ boolean lambda$onCreateView$16$ControlButtonFragment(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 1) {
            stopSkipInterval();
            if (this.mShouldResumeForSkipInterval) {
                this.mShouldResumeForSkipInterval = false;
                if (Engine.getInstance().resumePlay() == 0) {
                    postEvent(Event.PLAY_RESUME);
                }
            }
        }
        return false;
    }

    public /* synthetic */ void lambda$onCreateView$17$ControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.EDIT_PLAY_START");
        enableButtonDelayed(view);
        enableButtonDelayed((int) Event.EDIT_RECORD, (int) DELAY_RECORD_STOP_BUTTON);
        boolean z = Engine.getInstance().getPlayerState() == 4;
        int resumePlay = Engine.getInstance().resumePlay();
        if (resumePlay < 0) {
            errorHandler(resumePlay);
            return;
        }
        if (z) {
            postEvent(Event.EDIT_PLAY_RESUME);
        } else {
            postEvent(Event.EDIT_PLAY_START);
            if (VoiceNoteFeature.isGateEnabled()) {
                android.util.Log.i("GATE", "<GATE-M> AUDIO_PLAYING </GATE-M>");
            }
        }
        SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_edit_comm), getActivity().getResources().getString(C0690R.string.event_edit_play));
    }

    public /* synthetic */ void lambda$onCreateView$18$ControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.EDIT_PLAY_PAUSE");
        enableButtonDelayed(view);
        Engine.getInstance().pausePlay();
        postEvent(Event.EDIT_PLAY_PAUSE);
        SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_edit_comm), getActivity().getResources().getString(C0690R.string.event_edit_play_pause));
    }

    public /* synthetic */ void lambda$onCreateView$19$ControlButtonFragment(View view) {
        int i;
        Log.m26i(TAG, "Event.EDIT_RECORD");
        enableButtonDelayed(view);
        if (this.mScene == 6) {
            i = Settings.getIntSettings(Settings.KEY_PLAY_MODE, 1);
        } else {
            i = Settings.getIntSettings("record_mode", 1);
        }
        if (i == 4 && UPSMProvider.getInstance().isUltraPowerSavingMode()) {
            Log.m26i(TAG, " Ultra Power saving Mode enabled can not record  ");
            if (UPSMProvider.getInstance().supportMaxMode()) {
                Toast.makeText(getActivity(), getActivity().getString(C0690R.string.max_power_mode_error_msg, new Object[]{getActivity().getString(C0690R.string.speech_to_text_mode)}), 0).show();
                return;
            }
            Toast.makeText(getActivity(), C0690R.string.ups_mode_error_msg, 0).show();
        } else if (PermissionProvider.checkRecordPermission((AppCompatActivity) getActivity(), 3, C0690R.string.record)) {
            if (i != 4 || Network.isNetworkConnected(getActivity())) {
                if (i != 4 || RecognizerDBProvider.getTOSAcceptedState() == 1) {
                    int resumeRecord = Engine.getInstance().resumeRecord();
                    if (resumeRecord < 0) {
                        errorHandler(resumeRecord);
                        return;
                    }
                    postEvent(Event.EDIT_RECORD);
                    SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_edit_comm), getActivity().getResources().getString(C0690R.string.event_edit_rec));
                } else if (!DialogFactory.isDialogVisible(getFragmentManager(), DialogFactory.PRIVACY_POLICY_DIALOG)) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(DialogFactory.BUNDLE_GDPR_COUNTRY, GdprProvider.getInstance().isGdprCountry());
                    DialogFactory.show(getFragmentManager(), DialogFactory.PRIVACY_POLICY_DIALOG, bundle);
                }
            } else if (!DialogFactory.isDialogVisible(getFragmentManager(), DialogFactory.NETWORK_NOT_CONNECTED)) {
                DialogFactory.show(getFragmentManager(), DialogFactory.NETWORK_NOT_CONNECTED, (Bundle) null);
            }
        }
    }

    public /* synthetic */ void lambda$onCreateView$20$ControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.EDIT_RECORD_PAUSE");
        enableButtonDelayed(view);
        if (Engine.getInstance().pauseRecord()) {
            postEvent(Event.EDIT_RECORD_PAUSE);
            SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_edit_comm), getActivity().getResources().getString(C0690R.string.event_edit_rec_pause));
        }
    }

    public /* synthetic */ void lambda$onCreateView$21$ControlButtonFragment(View view) {
        TrimPopup trimPopup = this.mTrimPopup;
        if (trimPopup != null) {
            trimPopup.dismiss(true);
        }
        this.mTrimPopup = TrimPopup.getInstance(getContext(), this.mButtonFactory.getView(Event.EDIT_TRIM));
        this.mTrimPopup.show();
    }

    public /* synthetic */ void lambda$onCreateView$22$ControlButtonFragment(View view) {
        if (!view.isClickable()) {
            Log.m26i(TAG, "Convert button is disable");
            return;
        }
        Log.m26i(TAG, "Event.TRANSLATION_START");
        SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_ready_convert_stt), getActivity().getResources().getString(C0690R.string.event_convert_start));
        if (Network.isNetworkConnected(getContext())) {
            enableButtonDelayed(view);
            if (RecognizerDBProvider.getTOSAcceptedState() != 1) {
                if (!DialogFactory.isDialogVisible(getFragmentManager(), DialogFactory.PRIVACY_POLICY_DIALOG)) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(DialogFactory.BUNDLE_GDPR_COUNTRY, GdprProvider.getInstance().isGdprCountry());
                    DialogFactory.show(getFragmentManager(), DialogFactory.PRIVACY_POLICY_DIALOG, bundle);
                }
            } else if (!needRecognizerTOS()) {
                int startTranslation = Engine.getInstance().startTranslation();
                if (startTranslation < 0) {
                    errorHandler(startTranslation);
                    return;
                }
                FragmentController.getInstance().setNeedUpdateLayout(true);
                postEvent(Event.TRANSLATION_START);
            }
        } else if (!DialogFactory.isDialogVisible(getFragmentManager(), DialogFactory.NETWORK_NOT_CONNECTED)) {
            DialogFactory.show(getFragmentManager(), DialogFactory.NETWORK_NOT_CONNECTED, (Bundle) null);
        }
    }

    public /* synthetic */ void lambda$onCreateView$23$ControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.TRANSLATION_PAUSE");
        SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_convert_stt_progress), getActivity().getResources().getString(C0690R.string.event_convert_pause));
        if (Engine.getInstance().isSaveTranslatable()) {
            postEvent(Event.TRANSLATION_PAUSE);
            Engine.getInstance().pauseTranslation(false);
        }
    }

    public /* synthetic */ void lambda$onCreateView$24$ControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.TRANSLATION_RESUME");
        SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_convert_stt_progress), getActivity().getResources().getString(C0690R.string.event_convert_resume));
        if (Network.isNetworkConnected(getContext())) {
            int resumeTranslation = Engine.getInstance().resumeTranslation();
            if (resumeTranslation < 0) {
                errorHandler(resumeTranslation);
            } else {
                postEvent(Event.TRANSLATION_RESUME);
            }
        } else if (!DialogFactory.isDialogVisible(getFragmentManager(), DialogFactory.NETWORK_NOT_CONNECTED)) {
            DialogFactory.show(getFragmentManager(), DialogFactory.NETWORK_NOT_CONNECTED, (Bundle) null);
        }
    }

    public /* synthetic */ void lambda$onCreateView$25$ControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.TRANSLATION_SAVE");
        SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_convert_stt_progress), getActivity().getResources().getString(C0690R.string.event_convert_stop));
        if (Engine.getInstance().isSaveTranslatable()) {
            if (Engine.getInstance().getTranslationState() == 2 && Engine.getInstance().getPlayerState() == 3) {
                Engine.getInstance().pauseTranslation(false);
            }
            postEvent(Event.TRANSLATION_PAUSE);
            saveConvertSTT();
        }
    }

    private void initAccessibilityFocus(View view) {
        final View view2 = this.mButtonFactory.getView(1002);
        final View view3 = this.mButtonFactory.getView(1003);
        if (view2 != null && view3 != null) {
            FrameLayout frameLayout = (FrameLayout) view.findViewById(C0690R.C0693id.controlbutton_record_layout);
            if (VRUtil.isTalkBackOn(getContext())) {
                view2.setContentDescription((CharSequence) null);
                view2.setImportantForAccessibility(2);
                view3.setContentDescription(AssistantProvider.getInstance().getContentDesToButton(getResources().getString(C0690R.string.record_tts)));
            }
            frameLayout.setAccessibilityDelegate(new View.AccessibilityDelegate() {
                public void onPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
                    super.onPopulateAccessibilityEvent(view, accessibilityEvent);
                    if (accessibilityEvent.getEventType() == 32768) {
                        if (!ControlButtonFragment.this.mIsFirstTime) {
                            view2.setContentDescription(AssistantProvider.getInstance().getContentDesToButton(ControlButtonFragment.this.getResources().getString(C0690R.string.pause)));
                        }
                        boolean unused = ControlButtonFragment.this.mIsFirstTime = false;
                    }
                }
            });
            this.mAccessibilityManager = (AccessibilityManager) getActivity().getApplicationContext().getSystemService("accessibility");
            this.mAccessibilityStateChangeListener = new AccessibilityManager.AccessibilityStateChangeListener() {


                public final void onAccessibilityStateChanged(boolean z) {
                    ControlButtonFragment.this.lambda$initAccessibilityFocus$26$ControlButtonFragment(view2, view3, z);
                }
            };
            this.mAccessibilityManager.addAccessibilityStateChangeListener(this.mAccessibilityStateChangeListener);
        }
    }

    public /* synthetic */ void lambda$initAccessibilityFocus$26$ControlButtonFragment(View view, View view2, boolean z) {
        if (!z || !VRUtil.isTalkBackOn(getContext())) {
            view.setContentDescription(getResources().getString(C0690R.string.pause));
            view2.setContentDescription(getResources().getString(C0690R.string.resume));
            return;
        }
        view.setContentDescription((CharSequence) null);
        view.setImportantForAccessibility(2);
        view2.setContentDescription(AssistantProvider.getInstance().getContentDesToButton(getResources().getString(C0690R.string.record_tts)));
    }

    private void saveAsNewName(Bundle bundle, boolean z) {
        Log.m26i(TAG, "saveAsNewName");
        String string = bundle.getString(DialogFactory.BUNDLE_NAME);
        long j = bundle.getLong(DialogFactory.BUNDLE_LABEL_ID, 0);
        if (string != null && !string.isEmpty()) {
            Engine.getInstance().setUserSettingName(string);
            Engine.getInstance().setCategoryID(j);
            if (PermissionProvider.checkPermission((AppCompatActivity) getActivity(), (ArrayList<Integer>) null, false)) {
                long stopRecord = Engine.getInstance().stopRecord(true, z);
                if (stopRecord >= 0 || stopRecord == -2) {
                    CursorProvider.getInstance().resetCurrentPlayingItemPosition();
                    int intSettings = Settings.getIntSettings("record_mode", 1);
                    Settings.setSettings(Settings.KEY_LIST_MODE, 0);
                    if (intSettings == 4) {
                        postEvent(Event.RECORD_STOP_DELAYED);
                    } else {
                        postEvent(1004);
                    }
                }
            } else {
                Log.m26i(TAG, "Event.RECORD_STOP show permission dialog. Is this possible??");
            }
        }
    }

    private void saveFileNameDialog() {
        if (!DialogFactory.isDialogVisible(getFragmentManager(), DialogFactory.RENAME_DIALOG)) {
            Log.m26i(TAG, " saveFileNameDialog");
            String createNewFileName = DBProvider.getInstance().createNewFileName(0);
            int intSettings = Settings.getIntSettings("record_mode", 1);
            Bundle bundle = new Bundle();
            bundle.putString(DialogFactory.BUNDLE_NAME, createNewFileName);
            bundle.putInt("record_mode", intSettings);
            bundle.putInt(DialogFactory.BUNDLE_REQUEST_CODE, 11);
            DialogFactory.show(getFragmentManager(), DialogFactory.RENAME_DIALOG, bundle, this);
        }
    }

    private void saveConvertSTT() {
        if (!DialogFactory.isDialogVisible(getFragmentManager(), DialogFactory.RENAME_DIALOG)) {
            Log.m26i(TAG, " saveConvertSTT");
            Bundle bundle = new Bundle();
            bundle.putString(DialogFactory.BUNDLE_PATH, Engine.getInstance().getPath());
            bundle.putInt("record_mode", 4);
            bundle.putInt(DialogFactory.BUNDLE_REQUEST_CODE, 17);
            DialogFactory.show(getFragmentManager(), DialogFactory.RENAME_DIALOG, bundle, this);
        }
    }

    public void onDialogResult(DialogFragment dialogFragment, Bundle bundle) {
        if (bundle != null) {
            int i = bundle.getInt(DialogFactory.BUNDLE_REQUEST_CODE);
            int i2 = bundle.getInt("result_code");
            Log.m26i(TAG, "onDialogResult - requestCode : " + i + " result : " + i2);
            if (i != 8) {
                if (i == 11 && i2 == -1) {
                    saveAsNewName(bundle, false);
                }
            } else if (i2 == -2) {
                postEvent(Event.OPEN_FULL_PLAYER);
            }
        }
    }

    public void onViewCreated(View view, Bundle bundle) {
        Log.m26i(TAG, "onViewCreated");
        super.onViewCreated(view, bundle);
        Engine.getInstance().registerListener(this);
        FragmentController.getInstance().registerSceneChangeListener(this);
    }

    public void onResume() {
        Log.m26i(TAG, "onResume");
        super.onResume();
        if (Engine.getInstance().getRecorderState() == 2 && PermissionProvider.isCallRejectEnable(getContext()) && !CallRejectChecker.getInstance().getReject()) {
            CallRejectChecker.getInstance().setReject(true);
        }
        if (Settings.getBooleanSettings(Settings.KEY_HELP_SHOW_OVERWRITE_GUIDE, true)) {
            this.mRootViewTmp = getActivity().getWindow().getDecorView();
        }
        if (this.mIsTrimInProgress) {
            onUpdate(Integer.valueOf(Event.EDIT_TRIM));
        }
    }

    public void onPause() {
        Log.m26i(TAG, "onPause");
        this.mRootViewTmp = null;
        this.mSkipIntervalEventHandler.removeMessages(SKIP_INTERVAL_PREV);
        this.mSkipIntervalEventHandler.removeMessages(SKIP_INTERVAL_NEXT);
        super.onPause();
    }

    public void onStart() {
        Log.m26i(TAG, "onStart");
        super.onStart();
        int i = this.mScene;
        if (i == 1) {
            postEvent(4);
        } else if (i == 2) {
            postEvent(3);
        } else if (i != 3) {
            if (i == 4) {
                initSkipIntervalView();
            } else if (i == 12) {
                enableButton(this.mButtonFactory.getView(Event.TRANSLATION_START), true);
                if (Engine.getInstance().getTranslationState() == 1) {
                    onUpdate(17);
                }
                if (Engine.getInstance().getTranslationState() == 3) {
                    onUpdate(Integer.valueOf(Event.TRANSLATION_PAUSE));
                }
            }
        } else if (Engine.getInstance().getPlayerState() == 4) {
            enableButton(this.mButtonFactory.getView(1001), true);
        } else {
            enableButton(this.mButtonFactory.getView(1001), false);
        }
    }

    public void onStop() {
        Log.m26i(TAG, "onStop");
        ControlButtonFactory.AbsButton buttons = getButtons(1);
        if (!(mCurrentButton == null || buttons == null)) {
            for (int i = 0; i <= 4; i++) {
                AnimationFactory.changeButton(mCurrentButton.get(i), buttons.get(i), false);
            }
        }
        this.mCurrentEvent = 1;
        mCurrentButton = buttons;
        super.onStop();
    }

    public void onDestroyView() {
        Log.m26i(TAG, "onDestroyView");
        if (this.mContainerView != null) {
            MouseKeyboardProvider.getInstance().destroyMouseClickInteraction(this, this.mContainerView);
            this.mContainerView = null;
        }
        FragmentController.getInstance().unregisterSceneChangeListener(this);
        Engine.getInstance().unregisterListener(this);
        TrimPopup trimPopup = this.mTrimPopup;
        if (trimPopup != null) {
            trimPopup.dismiss(true);
            this.mTrimPopup = null;
        }
        this.mAccessibilityManager.removeAccessibilityStateChangeListener(this.mAccessibilityStateChangeListener);
        super.onDestroyView();
    }

    public void onEngineUpdate(int i, int i2, int i3) {
        Handler handler = this.mEngineEventHandler;
        if (handler != null) {
            handler.sendMessage(handler.obtainMessage(i, i2, i3));
        }
    }

    public void onDestroy() {
        Log.m26i(TAG, "onDestroy");
        this.mEngineEventHandler = null;
        ControlButtonFactory.AbsButton absButton = mCurrentButton;
        if (absButton != null) {
            absButton.removeAll();
            mCurrentButton = null;
        }
        this.mButtonFactory.clear();
        super.onDestroy();
    }

    public void onUpdate(Object obj) {
        int intValue = ((Integer) obj).intValue();
        if (isAdded() && !Engine.getInstance().isSimpleRecorderMode() && intValue != 1005) {
            Log.m19d(TAG, "onUpdate uiEvent : " + intValue + " mCurrentEvent : " + this.mCurrentEvent);
            if (intValue == 981 || intValue == 19) {
                Log.m26i(TAG, "onUpdate - do not update button");
                return;
            }
            if (intValue == 975) {
                initSkipIntervalView();
                intValue = (Engine.getInstance().getPlayerState() == 4 || Engine.getInstance().getPlayerState() == 2) ? 2002 : 2001;
            }
            ControlButtonFactory.AbsButton buttons = getButtons(this.mCurrentEvent);
            ControlButtonFactory.AbsButton buttons2 = getButtons(intValue);
            if (buttons == null || buttons2 == null) {
                Log.m32w(TAG, "onUpdate - button is null !!");
                return;
            }
            Log.m19d(TAG, "onUpdate oldButton : " + buttons.getClass().getSimpleName() + " newButton : " + buttons2.getClass().getSimpleName());
            boolean z = (this.mCurrentEvent == 1 && mCurrentButton.size() == 0) ? false : true;
            for (int i = 0; i <= 4; i++) {
                AnimationFactory.changeButton(buttons.get(i), buttons2.get(i), z);
            }
            int i2 = this.mCurrentEvent;
            this.mCurrentEvent = intValue;
            this.mStartingEvent = intValue;
            mCurrentButton = buttons2;
            View view = getView();
            if (intValue == 15) {
                TrimPopup trimPopup = this.mTrimPopup;
                if (trimPopup != null && trimPopup.isShowing()) {
                    this.mTrimPopup.dismiss();
                    this.mTrimPopup.show();
                }
            } else if (intValue == 17) {
                if (!ViewStateProvider.getInstance().isConvertAnimationRunning()) {
                    enableButton(this.mButtonFactory.getView(Event.TRANSLATION_START), true);
                }
                if (i2 == 2002 && VoiceNoteFeature.FLAG_SUPPORT_DATA_CHECK_POPUP && Settings.getBooleanSettings(Settings.KEY_DATA_CHECK_SHOW_AGAIN, true) && !DialogFactory.isDialogVisible(getFragmentManager(), DialogFactory.DATA_CHECK_DIALOG)) {
                    Log.m26i(TAG, "showDataCheckDialog module: 3");
                    Bundle bundle = new Bundle();
                    bundle.putInt(DialogFactory.BUNDLE_REQUEST_CODE, 8);
                    bundle.putInt(DialogFactory.BUNDLE_DATA_CHECK_MODULE, 3);
                    DialogFactory.show(getFragmentManager(), DialogFactory.DATA_CHECK_DIALOG, bundle, this);
                }
            } else if (intValue != 1009) {
                if (!(intValue == 2001 || intValue == 2003)) {
                    if (intValue == 5004) {
                        View view2 = this.mButtonFactory.getView(Event.EDIT_TRIM);
                        if (view2 != null) {
                            enableButton(view2, false);
                            return;
                        }
                        return;
                    } else if (intValue != 5012) {
                        if (intValue != 5998) {
                            if (intValue != 8002) {
                                if (intValue != 40998) {
                                    if (intValue == 969) {
                                        this.mButtonFactory.blockAllButton(false);
                                        return;
                                    } else if (intValue == 970) {
                                        this.mButtonFactory.blockAllButton(true);
                                        return;
                                    } else if (!(intValue == 2005 || intValue == 2006)) {
                                        if (intValue == 7008 || intValue == 7009) {
                                            performClick(Event.TRANSLATION_START);
                                            return;
                                        }
                                        switch (intValue) {
                                            case 1001:
                                            case 1002:
                                            case 1003:
                                                if (view != null) {
                                                    view.findViewById(C0690R.C0693id.controlbutton_record_layout).sendAccessibilityEvent(8);
                                                    return;
                                                }
                                                return;
                                            default:
                                                switch (intValue) {
                                                    case Event.RECORD_STOP_BY_DEX_CONNECT:
                                                        int i3 = this.mScene;
                                                        if (i3 == 8) {
                                                            if (!DialogFactory.isDialogVisible(getFragmentManager(), DialogFactory.RENAME_DIALOG)) {
                                                                performClick(1004);
                                                                return;
                                                            }
                                                            return;
                                                        } else if (i3 == 6 && !DialogFactory.isDialogVisible(getFragmentManager(), DialogFactory.EDIT_SAVE_DIALOG)) {
                                                            DialogFactory.show(getFragmentManager(), DialogFactory.EDIT_SAVE_DIALOG, (Bundle) null);
                                                            if (Engine.getInstance().pauseRecord()) {
                                                                VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.EDIT_RECORD_PAUSE));
                                                                return;
                                                            }
                                                            return;
                                                        } else {
                                                            return;
                                                        }
                                                    case Event.RECORD_BY_LEVEL_ACTIVEKEY:
                                                        int recorderState = Engine.getInstance().getRecorderState();
                                                        if (recorderState == 1) {
                                                            performClick(1009);
                                                            return;
                                                        } else if (recorderState != 2) {
                                                            performClick(1003);
                                                            return;
                                                        } else if (Engine.getInstance().stopRecord(true, true) > 0) {
                                                            postEvent(1004);
                                                            return;
                                                        } else {
                                                            return;
                                                        }
                                                    case Event.RECORD_START_BY_TASK_EDGE:
                                                    case Event.RECORD_START_BY_SVOICE:
                                                    case Event.RECORD_RESUME_BY_PERMISSION:
                                                    case Event.RECORD_START_BY_PERMISSION:
                                                        break;
                                                    default:
                                                        return;
                                                }
                                        }
                                    }
                                } else if (Engine.getInstance().isTrimEnable() || Engine.getInstance().isDeleteEnable()) {
                                    performClick(Event.EDIT_TRIM);
                                    return;
                                } else {
                                    return;
                                }
                            } else if (Engine.getInstance().getRecorderState() == 2) {
                                performClick(1002);
                                return;
                            } else {
                                performClick(1003);
                                return;
                            }
                        }
                        performClick(1009);
                        return;
                    } else {
                        this.mIsSaveAfterEdit = true;
                        return;
                    }
                }
                initSkipIntervalView();
            } else if (performClick(1001)) {
                VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.BIXBY_START_RECORDING_RESULT_SUCCESS));
            } else {
                VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.BIXBY_START_RECORDING_RESULT_FAIL));
            }
        }
    }

    private boolean performClick(int i) {
        View view = this.mButtonFactory.getView(i);
        return view != null && view.performClick();
    }

    private ControlButtonFactory.AbsButton getButtons(int i) {
        ControlButtonFactory.AbsButton buttons = this.mButtonFactory.getButtons(i);
        return buttons == null ? mCurrentButton : buttons;
    }

    private void enableButtonDelayed(int i, int i2) {
        enableButtonDelayed(this.mButtonFactory.getView(i), i2);
    }

    private void enableButtonDelayed(View view) {
        enableButtonDelayed(view, (int) DELAY_DEFAULT_BUTTON);
    }

    private void enableButtonDelayed(final View view, int i) {
        if (view != null) {
            Log.m26i(TAG, "enableButtonDelayed button : " + view.getContentDescription());
            view.setEnabled(false);
            view.postDelayed(new Runnable() {
                private final /* synthetic */ View f$1;

                {
                    this.f$1 = view;
                }

                public final void run() {
                    ControlButtonFragment.this.lambda$enableButtonDelayed$27$ControlButtonFragment(this.f$1);
                }
            }, (long) i);
        }
    }

    public /* synthetic */ void lambda$enableButtonDelayed$27$ControlButtonFragment(View view) {
        enableButton(view, true);
    }

    private void enableButton(View view, boolean z) {
        if (view != null) {
            Log.m26i(TAG, "enableButton button : " + view.getContentDescription() + " enabled : " + z);
            switch (view.getId()) {
                case C0690R.C0693id.controlbutton_edit_play:
                case C0690R.C0693id.controlbutton_pre_play:
                    if (!z || Engine.getInstance().getRecorderState() == 2) {
                        this.mButtonFactory.enableButton(view, false);
                        return;
                    } else {
                        this.mButtonFactory.enableButton(view, true);
                        return;
                    }
                case C0690R.C0693id.controlbutton_edit_record_start:
                    if (!z || !Engine.getInstance().isEditRecordable()) {
                        this.mButtonFactory.enableButton(view, false);
                        return;
                    } else {
                        this.mButtonFactory.enableButton(view, true);
                        return;
                    }
                case C0690R.C0693id.controlbutton_edit_trim_button:
                    if (!z || (!Engine.getInstance().isTrimEnable() && !Engine.getInstance().isDeleteEnable())) {
                        view.setAlpha(0.2f);
                        view.setEnabled(false);
                        view.setFocusable(false);
                        return;
                    }
                    view.setAlpha(1.0f);
                    view.setEnabled(true);
                    view.setFocusable(true);
                    return;
                case C0690R.C0693id.controlbutton_record_resume:
                case C0690R.C0693id.controlbutton_record_start:
                    if (z && Engine.getInstance().getPlayerState() == 3) {
                        z = false;
                    }
                    if (z) {
                        this.mButtonFactory.enableButton(view, true);
                        return;
                    } else {
                        this.mButtonFactory.enableButton(view, false);
                        return;
                    }
                case C0690R.C0693id.controlbutton_translation_start:
                    if (!z || !Engine.getInstance().isTranslateable()) {
                        this.mButtonFactory.enableButton(view, false);
                        return;
                    } else {
                        this.mButtonFactory.enableButton(view, true);
                        return;
                    }
                default:
                    if (z) {
                        this.mButtonFactory.enableButton(view, true);
                        return;
                    } else {
                        this.mButtonFactory.enableButton(view, false);
                        return;
                    }
            }
        }
    }

    private void initSkipIntervalView() {
        int i = Engine.getInstance().getDuration() > SKIP_INTERVAL_STANDARD_DURATION ? SKIP_INTERVAL_3SEC : 1000;
        int i2 = i / 1000;
        String string = getString(C0690R.string.second, String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(i2)}));
        TextView textView = (TextView) this.mButtonFactory.getView(Event.PLAY_RW);
        if (textView != null) {
            textView.setText('-' + string);
            if (i == 1000) {
                textView.setContentDescription(getResources().getString(C0690R.string.string_interval_1sec_rewind));
            } else {
                textView.setContentDescription(getResources().getString(C0690R.string.string_interval_3sec_rewind, new Object[]{Integer.valueOf(i2)}));
            }
        }
        TextView textView2 = (TextView) this.mButtonFactory.getView(Event.PLAY_FF);
        if (textView2 != null) {
            textView2.setText('+' + string);
            if (i == 1000) {
                textView2.setContentDescription(getResources().getString(C0690R.string.string_interval_1sec_forward));
            } else {
                textView2.setContentDescription(getResources().getString(C0690R.string.string_interval_3sec_forward, new Object[]{Integer.valueOf(i2)}));
            }
        }
    }

    private void setSkipIntervalValue(int i) {
        Log.m26i(TAG, "setSkipIntervalValue - event : " + i);
        int duration = Engine.getInstance().getDuration();
        if (duration > SKIP_INTERVAL_STANDARD_DURATION) {
            this.mSkipIntervalValue = SKIP_INTERVAL_3SEC;
        } else {
            this.mSkipIntervalValue = 1000;
        }
        if (i == SKIP_INTERVAL_PREV) {
            this.mSkipIntervalValue *= -1;
        }
        this.mSkipIntervalLimit = (int) (((float) duration) * SKIP_INTERVAL_LIMIT_RATIO);
    }

    /* access modifiers changed from: private */
    public void stopSkipInterval() {
        this.mSkippedTime = 0;
        this.mSkipIntervalEventHandler.removeMessages(SKIP_INTERVAL_PREV);
        this.mSkipIntervalEventHandler.removeMessages(SKIP_INTERVAL_NEXT);
    }

    public void onSceneChange(int i) {
        if (this.mScene == 2 && i == 3) {
            Log.m29v(TAG, "onSceneChange - list -> mini play");
            enableButton(this.mButtonFactory.getView(1001), false);
        } else if (i == 2 && this.mScene == 3) {
            Log.m29v(TAG, "onSceneChange - mini play -> list");
            enableButton(this.mButtonFactory.getView(1001), true);
        } else if (i == 12 && Settings.getBooleanSettings(Settings.KEY_HELP_SHOW_CONVERT_STT_GUIDE, true)) {
            showConvertSTTGuide();
        }
        TrimPopup trimPopup = this.mTrimPopup;
        if (trimPopup != null) {
            trimPopup.dismiss(true);
            this.mTrimPopup = null;
        }
        this.mScene = i;
    }

    private boolean needRecognizerTOS() {
        int i;
        if (this.mScene == 6) {
            i = Settings.getIntSettings(Settings.KEY_PLAY_MODE, 1);
        } else {
            i = Settings.getIntSettings("record_mode", 1);
        }
        Log.m26i(TAG, "needRecognizerTOS() : recordMode = " + i);
        if (i == 4 || this.mScene == 12) {
            if (UPSMProvider.getInstance().isUltraPowerSavingMode()) {
                return true;
            }
            if (SecureFolderProvider.isSecureFolderSupported()) {
                SecureFolderProvider.getKnoxMenuList(getActivity());
                if (SecureFolderProvider.isInsideSecureFolder()) {
                    Toast.makeText(getActivity(), C0690R.string.memo_recording_error_secure_folder, 0).show();
                    return true;
                }
            }
            int tOSAcceptedState = RecognizerDBProvider.getTOSAcceptedState();
            if (tOSAcceptedState != 1) {
                displayRecognizerTOS(tOSAcceptedState);
                return true;
            }
        }
        return false;
    }

    private void displayRecognizerTOS(int i) {
        Log.m26i(TAG, "displayRecognizerTOS() : tosResult = " + i);
        if (i == 0 && !DialogFactory.isDialogVisible(getFragmentManager(), DialogFactory.PRIVACY_POLICY_DIALOG)) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(DialogFactory.BUNDLE_GDPR_COUNTRY, GdprProvider.getInstance().isGdprCountry());
            DialogFactory.show(getFragmentManager(), DialogFactory.PRIVACY_POLICY_DIALOG, bundle);
        }
    }

    private void showOverwriteGuide() {
        Log.m26i(TAG, "showOverwriteGuide");
        FragmentActivity activity = getActivity();
        if (activity != null) {
            final Window window = activity.getWindow();
            final View findViewById = window.getDecorView().findViewById(C0690R.C0693id.main_activity_root_view);
//            findViewById.getRootView().semSetRoundedCorners(0);
            final int statusBarColor = window.getStatusBarColor();
            final int navigationBarColor = window.getNavigationBarColor();
            window.setStatusBarColor(activity.getResources().getColor(C0690R.C0691color.mode_help_bg, (Resources.Theme) null));
            window.setNavigationBarColor(activity.getResources().getColor(C0690R.C0691color.mode_help_bg, (Resources.Theme) null));
            window.addFlags(2);
            if (findViewById.findViewById(C0690R.C0693id.help_overwrite) == null) {
                Log.m26i(TAG, "showOverwriteGuide add view");
                ViewGroup viewGroup = (ViewGroup) findViewById;
                final View inflate = activity.getLayoutInflater().inflate(C0690R.layout.help_overwrite_guide, viewGroup, false);
                viewGroup.addView(inflate);
                Button button = (Button) inflate.findViewById(C0690R.C0693id.help_overwrite_ok_button);
                ViewProvider.setMaxFontSize(activity, (TextView) inflate.findViewById(C0690R.C0693id.overwrite_description_top));
                ViewProvider.setMaxFontSize(activity, (TextView) inflate.findViewById(C0690R.C0693id.overwrite_description_bottom));
                ViewProvider.setMaxFontSize(activity, button);
                button.requestFocus();
                button.setOnClickListener(new View.OnClickListener() {
                    private final /* synthetic */ Window f$1;
                    private final /* synthetic */ int f$2;
                    private final /* synthetic */ int f$3;
                    private final /* synthetic */ View f$4;
                    private final /* synthetic */ View f$5;

                    {
                        this.f$1 = window;
                        this.f$2 = statusBarColor;
                        this.f$3 = navigationBarColor;
                        this.f$4 = findViewById;
                        this.f$5 = inflate;
                    }

                    public final void onClick(View view) {
                        ControlButtonFragment.this.lambda$showOverwriteGuide$28$ControlButtonFragment(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, view);
                    }
                });
                inflate.setOnTouchListener($$Lambda$ControlButtonFragment$Q4ElUvC9POXwo2trAYYFC38_mp4.INSTANCE);
            }
        }
    }

    public /* synthetic */ void lambda$showOverwriteGuide$28$ControlButtonFragment(Window window, int i, int i2, View view, View view2, View view3) {
        window.setStatusBarColor(i);
        window.setNavigationBarColor(i2);
        window.addFlags(1);
//        view.getRootView().semSetRoundedCorners(12);
        ((ViewGroup) view).removeView(view2);
        View view4 = this.mRootViewTmp;
        if (view4 != null && ((ViewGroup) view4).getChildCount() >= 1) {
            ((ViewGroup) this.mRootViewTmp).getChildAt(0).setImportantForAccessibility(1);
            ((ViewGroup) ((ViewGroup) this.mRootViewTmp).getChildAt(0)).setDescendantFocusability(262144);
        }
        Settings.setSettings(Settings.KEY_HELP_SHOW_OVERWRITE_GUIDE, false);
        this.mIsShowingGuide = false;
    }

    private void errorHandler(int i) {
        if (i < -100) {
            Log.m26i(TAG, "errorHandler - errorCode : " + i);
            switch (i) {
                case -121:
                    Toast.makeText(getActivity(), C0690R.string.low_battery_msg, 0).show();
                    return;
                case -120:
                    Toast.makeText(getActivity(), C0690R.string.recording_now, 0).show();
                    Log.m22e(TAG, "ANOTHER_RECORDER_ALREADY_RUNNING !!!!");
                    return;
                case -119:
                    Toast.makeText(getActivity(), C0690R.string.please_wait, 1).show();
                    Log.m22e(TAG, "Engine BUSY !!!!");
                    return;
                case -118:
                    Toast.makeText(getActivity(), C0690R.string.stack_size_error, 0).show();
                    Log.m22e(TAG, "STACK_SIZE_ERROR !!!!");
                    return;
                case -117:
                    Toast.makeText(getActivity(), C0690R.string.overwrite_failed, 0).show();
                    Log.m22e(TAG, "overwrite - OVERWRITE_FAIL !!!!");
                    return;
                case -116:
                    Toast.makeText(getActivity(), C0690R.string.trim_failed, 0).show();
                    Log.m22e(TAG, "trim - TRIM_FAIL !!!!");
                    return;
                case -115:
                    Toast.makeText(getActivity(), C0690R.string.playback_failed_msg, 0).show();
                    Log.m22e(TAG, "startPlay - PLAY_FAIL !!!!");
                    return;
                case -114:
                    Toast.makeText(getActivity(), C0690R.string.recording_failed, 0).show();
                    Log.m22e(TAG, "startRecord - RECORD_FAIL !!!!");
                    return;
                case -111:
                    Toast.makeText(getActivity(), C0690R.string.trim_failed, 1).show();
                    Log.m22e(TAG, "Can not trim !!!!");
                    return;
                case -109:
                    Toast.makeText(getActivity(), C0690R.string.recording_failed, 0).show();
                    Log.m22e(TAG, "startRecord - REQUEST_AUDIO_FOCUS_FAIL !!!!");
                    return;
                case -107:
                    if (StorageProvider.alternativeStorageRecordEnable()) {
                        DialogFactory.show(getFragmentManager(), DialogFactory.STORAGE_CHANGE_DIALOG, (Bundle) null);
                        return;
                    } else {
                        DialogFactory.show(getFragmentManager(), DialogFactory.STORAGE_FULL_DIALOG, (Bundle) null);
                        return;
                    }
                case -106:
                    DialogFactory.show(getFragmentManager(), DialogFactory.NETWORK_NOT_CONNECTED, (Bundle) null);
                    return;
                case -105:
                case -104:
                    if (!DialogFactory.isDialogVisible(getFragmentManager(), DialogFactory.MODE_NOT_SUPPORTED)) {
                        DialogFactory.show(getFragmentManager(), DialogFactory.MODE_NOT_SUPPORTED, (Bundle) null);
                        return;
                    }
                    return;
                case -103:
                    Toast.makeText(getActivity(), C0690R.string.no_play_during_call, 0).show();
                    return;
                case -102:
                    if (PhoneStateProvider.getInstance().isDuringCall(getActivity())) {
                        Toast.makeText(getActivity(), C0690R.string.no_rec_during_call, 0).show();
                        return;
                    } else {
                        Toast.makeText(getActivity(), C0690R.string.no_rec_during_incoming_calls, 0).show();
                        return;
                    }
                case -101:
                    Toast.makeText(getActivity(), C0690R.string.recording_failed, 0).show();
                    Log.m22e(TAG, "startRecord - start failed !!!!");
                    return;
                default:
                    return;
            }
        }
    }

    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        ContextMenuProvider.getInstance().createContextMenu(getActivity(), contextMenu, this.mScene, view, this);
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        ContextMenuProvider.getInstance().contextItemSelected((AppCompatActivity) getActivity(), menuItem, this.mScene, this);
        return false;
    }

    private void backToIdleControlButton() {
        Log.m26i(TAG, "backToIdleControlButton");
        if (FragmentController.getInstance().getCurrentEvent() == 1009 || FragmentController.getInstance().getCurrentEvent() == 1991) {
            postEvent(1010);
        }
    }

    private void showConvertSTTGuide() {
        Log.m26i(TAG, "showConvertSTTGuide");
        FragmentActivity activity = getActivity();
        if (activity != null) {
            final Window window = activity.getWindow();
            final View findViewById = window.getDecorView().findViewById(C0690R.C0693id.main_activity_root_view);
//            findViewById.getRootView().semSetRoundedCorners(0);
            Log.m26i(TAG, "showConvertSTTGuide add view");
            final int statusBarColor = window.getStatusBarColor();
            final int navigationBarColor = window.getNavigationBarColor();
            window.setStatusBarColor(activity.getResources().getColor(C0690R.C0691color.mode_help_bg, (Resources.Theme) null));
            window.setNavigationBarColor(activity.getResources().getColor(C0690R.C0691color.mode_help_bg, (Resources.Theme) null));
            window.addFlags(2);
            if (findViewById.findViewById(C0690R.C0693id.help_convert_stt_parent) == null) {
                ViewGroup viewGroup = (ViewGroup) findViewById;
                final View inflate = activity.getLayoutInflater().inflate(C0690R.layout.help_convert_stt_guide, viewGroup, false);
                viewGroup.addView(inflate);
                Button button = (Button) inflate.findViewById(C0690R.C0693id.help_convert_stt_ok_button);
                ViewProvider.setMaxFontSize(activity, (TextView) inflate.findViewById(C0690R.C0693id.help_convert_stt_seekbar_description));
                ViewProvider.setMaxFontSize(activity, button);
                button.requestFocus();
                ViewStateProvider.getInstance().setConvertSttHelpGuideState(false);
                button.setOnClickListener(new View.OnClickListener() {
                    private final /* synthetic */ Window f$1;
                    private final /* synthetic */ int f$2;
                    private final /* synthetic */ int f$3;
                    private final /* synthetic */ View f$4;
                    private final /* synthetic */ View f$5;

                    {
                        this.f$1 = window;
                        this.f$2 = statusBarColor;
                        this.f$3 = navigationBarColor;
                        this.f$4 = findViewById;
                        this.f$5 = inflate;
                    }

                    public final void onClick(View view) {
                        ControlButtonFragment.this.lambda$showConvertSTTGuide$30$ControlButtonFragment(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, view);
                    }
                });
                inflate.setOnTouchListener($$Lambda$ControlButtonFragment$kSMfVv5IraC2xTg9UhmU7xzwbnI.INSTANCE);
            }
        }
    }

    public /* synthetic */ void lambda$showConvertSTTGuide$30$ControlButtonFragment(Window window, int i, int i2, View view, View view2, View view3) {
        window.setStatusBarColor(i);
        window.setNavigationBarColor(i2);
        window.addFlags(1);
//        view.getRootView().semSetRoundedCorners(12);
        ((ViewGroup) view).removeView(view2);
        View view4 = this.mRootViewTmp;
        if (view4 != null && ((ViewGroup) view4).getChildCount() >= 1) {
            ((ViewGroup) this.mRootViewTmp).getChildAt(0).setImportantForAccessibility(1);
            ((ViewGroup) ((ViewGroup) this.mRootViewTmp).getChildAt(0)).setDescendantFocusability(262144);
        }
        Settings.setSettings(Settings.KEY_HELP_SHOW_CONVERT_STT_GUIDE, false);
    }
}
