package com.sec.android.app.voicenote.p007ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.activity.SimpleActivity;
import com.sec.android.app.voicenote.common.util.VRUtil;
import com.sec.android.app.voicenote.p007ui.animation.AnimationFactory;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.provider.AssistantProvider;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.DBProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.GdprProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Network;
import com.sec.android.app.voicenote.provider.PermissionProvider;
import com.sec.android.app.voicenote.provider.PhoneStateProvider;
import com.sec.android.app.voicenote.provider.RecognizerDBProvider;
import com.sec.android.app.voicenote.provider.SecureFolderProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.StorageProvider;
import com.sec.android.app.voicenote.provider.UPSMProvider;
import com.sec.android.app.voicenote.service.AudioFormat;
import com.sec.android.app.voicenote.service.SimpleEngine;
import com.sec.android.app.voicenote.service.SimpleEngineManager;
import java.util.Locale;

/* renamed from: com.sec.android.app.voicenote.ui.SimpleControlButtonFragment */
public class SimpleControlButtonFragment extends AbsSimpleFragment implements SimpleEngine.OnSimpleEngineListener, DialogFactory.DialogResultListener {
    private static final int DEFAULT_BUTTON_DELAY = 350;
    private static final int DELAY_RECORD_STOP_BUTTON = 1200;
    private static final int SKIP_INTERVAL_1SEC = 1000;
    private static final int SKIP_INTERVAL_3SEC = 3000;
    private static final float SKIP_INTERVAL_LIMIT_RATIO = 0.4f;
    private static final int SKIP_INTERVAL_NEXT = 901;
    private static final int SKIP_INTERVAL_PREV = 900;
    private static final int SKIP_INTERVAL_STANDARD_DURATION = 15000;
    private static final String TAG = "SimpleControlButtonFragment";
    private AccessibilityManager mAccessibilityManager;
    private AccessibilityManager.AccessibilityStateChangeListener mAccessibilityStateChangeListener;
    private final SparseArray<AbsButton> mButtonFactory = new SparseArray<>();
    private AbsButton mCurrentButton;
    private int mCurrentEvent = 1;
    private Handler mEngineEventHandler = null;
    /* access modifiers changed from: private */
    public final Handler mEventHandler = new Handler(new Handler.Callback() {
        public boolean handleMessage(Message message) {
            int i = message.what;
            if (i == SimpleControlButtonFragment.SKIP_INTERVAL_PREV || i == SimpleControlButtonFragment.SKIP_INTERVAL_NEXT) {
                SimpleControlButtonFragment simpleControlButtonFragment = SimpleControlButtonFragment.this;
                simpleControlButtonFragment.mSimpleEngine.skipInterval(simpleControlButtonFragment.mSkippedTime);
            }
            if (Math.abs(SimpleControlButtonFragment.this.mSkippedTime) <= SimpleControlButtonFragment.this.mSkipIntervalLimit) {
                SimpleControlButtonFragment simpleControlButtonFragment2 = SimpleControlButtonFragment.this;
                int unused = simpleControlButtonFragment2.mSkippedTime = simpleControlButtonFragment2.mSkippedTime + SimpleControlButtonFragment.this.mSkipIntervalValue;
            }
            SimpleControlButtonFragment.this.mEventHandler.removeMessages(message.what);
            SimpleControlButtonFragment.this.mEventHandler.sendEmptyMessageDelayed(message.what, 200);
            return false;
        }
    });
    /* access modifiers changed from: private */
    public boolean mIsFirstTime = true;
    private boolean mIsRecorded = false;
    private OnRecordResultListener mOnRecordResultListener;
    /* access modifiers changed from: private */
    public int mSkipIntervalLimit;
    /* access modifiers changed from: private */
    public int mSkipIntervalValue = SKIP_INTERVAL_3SEC;
    /* access modifiers changed from: private */
    public int mSkippedTime;
    /* access modifiers changed from: private */
    public final SparseArray<View> mViewFactory = new SparseArray<>();

    /* renamed from: com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$OnRecordResultListener */
    public interface OnRecordResultListener {
        void onRecordResult(int i, long j);
    }

    /* renamed from: com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$POSITION */
    private static class POSITION {
        static final int CENTER = 2;
        static final int CENTER_END = 3;
        static final int CENTER_START = 1;
        static final int END = 4;
        static final int START = 0;

        private POSITION() {
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mEngineEventHandler = new Handler(new Handler.Callback() {
            public final boolean handleMessage(Message message) {
                return SimpleControlButtonFragment.this.lambda$onCreate$0$SimpleControlButtonFragment(message);
            }
        });
    }

    public /* synthetic */ boolean lambda$onCreate$0$SimpleControlButtonFragment(Message message) {
        SimpleEngine simpleEngine;
        if (getActivity() != null && isAdded() && !isRemoving() && (simpleEngine = this.mSimpleEngine) != null) {
            int i = message.what;
            if (i != 105) {
                if (i == 1010) {
                    Log.m19d(TAG, "onRecorderUpdate - status : " + message.what + " arg : " + message.arg1);
                    int i2 = message.arg1;
                    if (i2 == 2) {
                        onUpdate(Integer.valueOf(Event.SIMPLE_RECORD_START));
                    } else if (i2 == 3 && this.mSimpleEngine.getRecorderState() != 1) {
                        stopSimpleRecording(this.mSimpleEngine.stopRecord(true, false));
                    }
                } else if (i == 1024) {
                    Toast.makeText(getActivity(), C0690R.string.call_accept_info, 1).show();
                } else if (i == 2010) {
                    Log.m19d(TAG, "onPlayerUpdate - status : " + message.what + " arg : " + message.arg1);
                    int i3 = message.arg1;
                    if (i3 != 2) {
                        if (i3 != 3) {
                            if (i3 == 4) {
                                if (this.mSimpleEngine.isSimpleRecorderMode()) {
                                    onUpdate(Integer.valueOf(Event.SIMPLE_RECORD_PLAY_PAUSE));
                                } else {
                                    onUpdate(Integer.valueOf(Event.SIMPLE_PLAY_PAUSE));
                                }
                            }
                        } else if (this.mSimpleEngine.isSimpleRecorderMode()) {
                            onUpdate(Integer.valueOf(Event.SIMPLE_RECORD_PLAY_START));
                        } else {
                            onUpdate(Integer.valueOf(Event.SIMPLE_PLAY_RESUME));
                        }
                    } else if (this.mSimpleEngine.isSimpleRecorderMode()) {
                        onUpdate(Integer.valueOf(Event.SIMPLE_RECORD_PLAY_PAUSE));
                    }
                } else if (i != 2011) {
                    switch (i) {
                        case 1020:
                            break;
                        case 1021:
                        case 1022:
                            stopSimpleRecording(DBProvider.getInstance().getIdByPath(this.mSimpleEngine.getLastSavedFilePath()));
                            break;
                    }
                } else if (simpleEngine.isSimpleRecorderMode()) {
                    onUpdate(Integer.valueOf(Event.SIMPLE_RECORD_PLAY_PAUSE));
                } else {
                    onUpdate(Integer.valueOf(Event.SIMPLE_PLAY_PAUSE));
                }
            }
            long idByPath = DBProvider.getInstance().getIdByPath(this.mSimpleEngine.getLastSavedFilePath());
            this.mSimpleEngine.setSimpleModeItem(idByPath);
            if (idByPath >= 0) {
                getActivity().invalidateOptionsMenu();
                onUpdate(Integer.valueOf(Event.SIMPLE_RECORD_STOP));
            }
            CursorProvider.getInstance().resetCurrentPlayingItemPosition();
            this.mOnRecordResultListener.onRecordResult(Event.SIMPLE_MODE_DONE, idByPath);
        }
        return false;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SimpleActivity) {
            this.mOnRecordResultListener = (OnRecordResultListener) context;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x013e  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0150  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0162  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0174  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0186  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0198  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x01aa  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x01cf  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x01f4  */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x012c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View onCreateView(android.view.LayoutInflater r12, android.view.ViewGroup r13, android.os.Bundle r14) {
        /*
            r11 = this;
            boolean r14 = r11.isInMultiWindow()
            r0 = 0
            if (r14 != 0) goto L_0x0019
            com.sec.android.app.voicenote.provider.DesktopModeProvider.getInstance()
            boolean r14 = com.sec.android.app.voicenote.provider.DesktopModeProvider.isDesktopMode()
            if (r14 == 0) goto L_0x0011
            goto L_0x0019
        L_0x0011:
            r14 = 2131492941(0x7f0c004d, float:1.8609348E38)
            android.view.View r12 = r12.inflate(r14, r13, r0)
            goto L_0x0020
        L_0x0019:
            r14 = 2131492964(0x7f0c0064, float:1.8609395E38)
            android.view.View r12 = r12.inflate(r14, r13, r0)
        L_0x0020:
            android.util.SparseArray<android.view.View> r13 = r11.mViewFactory
            r13.clear()
            android.util.SparseArray<android.view.View> r13 = r11.mViewFactory
            r14 = 2131296802(0x7f090222, float:1.821153E38)
            android.view.View r14 = r12.findViewById(r14)
            r0 = 982(0x3d6, float:1.376E-42)
            r13.put(r0, r14)
            android.util.SparseArray<android.view.View> r13 = r11.mViewFactory
            r14 = 2131296815(0x7f09022f, float:1.8211557E38)
            android.view.View r14 = r12.findViewById(r14)
            r1 = 50002(0xc352, float:7.0068E-41)
            r13.put(r1, r14)
            android.util.SparseArray<android.view.View> r13 = r11.mViewFactory
            r14 = 2131296816(0x7f090230, float:1.821156E38)
            android.view.View r14 = r12.findViewById(r14)
            r2 = 50003(0xc353, float:7.0069E-41)
            r13.put(r2, r14)
            android.util.SparseArray<android.view.View> r13 = r11.mViewFactory
            r14 = 2131296814(0x7f09022e, float:1.8211555E38)
            android.view.View r14 = r12.findViewById(r14)
            r3 = 50004(0xc354, float:7.007E-41)
            r13.put(r3, r14)
            android.util.SparseArray<android.view.View> r13 = r11.mViewFactory
            r14 = 2131296813(0x7f09022d, float:1.8211553E38)
            android.view.View r14 = r12.findViewById(r14)
            r4 = 50005(0xc355, float:7.0072E-41)
            r13.put(r4, r14)
            android.util.SparseArray<android.view.View> r13 = r11.mViewFactory
            r14 = 2131296812(0x7f09022c, float:1.8211551E38)
            android.view.View r14 = r12.findViewById(r14)
            r5 = 50007(0xc357, float:7.0075E-41)
            r13.put(r5, r14)
            android.util.SparseArray<android.view.View> r13 = r11.mViewFactory
            r14 = 2131296810(0x7f09022a, float:1.8211547E38)
            android.view.View r14 = r12.findViewById(r14)
            r6 = 50008(0xc358, float:7.0076E-41)
            r13.put(r6, r14)
            android.util.SparseArray<android.view.View> r13 = r11.mViewFactory
            r14 = 2131296811(0x7f09022b, float:1.821155E38)
            android.view.View r14 = r12.findViewById(r14)
            r7 = 50013(0xc35d, float:7.0083E-41)
            r13.put(r7, r14)
            android.util.SparseArray<android.view.View> r13 = r11.mViewFactory
            r14 = 2131296809(0x7f090229, float:1.8211545E38)
            android.view.View r14 = r12.findViewById(r14)
            r8 = 50014(0xc35e, float:7.0085E-41)
            r13.put(r8, r14)
            android.util.SparseArray<android.view.View> r13 = r11.mViewFactory
            r14 = 2131296803(0x7f090223, float:1.8211533E38)
            android.view.View r14 = r12.findViewById(r14)
            r9 = 983(0x3d7, float:1.377E-42)
            r13.put(r9, r14)
            android.util.SparseArray<com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$AbsButton> r13 = r11.mButtonFactory
            r13.clear()
            android.util.SparseArray<com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$AbsButton> r13 = r11.mButtonFactory
            r14 = 50001(0xc351, float:7.0066E-41)
            com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$RecordReadyButton r10 = new com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$RecordReadyButton
            r10.<init>()
            r13.put(r14, r10)
            android.util.SparseArray<com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$AbsButton> r13 = r11.mButtonFactory
            com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$RecordButton r14 = new com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$RecordButton
            r14.<init>()
            r13.put(r1, r14)
            android.util.SparseArray<com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$AbsButton> r13 = r11.mButtonFactory
            com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$RecordPlayButton r14 = new com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$RecordPlayButton
            r14.<init>()
            r13.put(r2, r14)
            android.util.SparseArray<com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$AbsButton> r13 = r11.mButtonFactory
            com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$RecordPlayPauseButton r14 = new com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$RecordPlayPauseButton
            r14.<init>()
            r13.put(r3, r14)
            android.util.SparseArray<com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$AbsButton> r13 = r11.mButtonFactory
            com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$RecordPlayButton r14 = new com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$RecordPlayButton
            r14.<init>()
            r13.put(r4, r14)
            android.util.SparseArray<com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$AbsButton> r13 = r11.mButtonFactory
            com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$PlayButton r14 = new com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$PlayButton
            r14.<init>()
            r13.put(r5, r14)
            android.util.SparseArray<com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$AbsButton> r13 = r11.mButtonFactory
            com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$PlayPauseButton r14 = new com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$PlayPauseButton
            r14.<init>()
            r13.put(r6, r14)
            android.util.SparseArray<com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$AbsButton> r13 = r11.mButtonFactory
            r14 = 50009(0xc359, float:7.0078E-41)
            com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$PlayButton r10 = new com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$PlayButton
            r10.<init>()
            r13.put(r14, r10)
            android.util.SparseArray<com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$AbsButton> r13 = r11.mButtonFactory
            com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$EmptyButton r14 = new com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$EmptyButton
            r14.<init>()
            r10 = 1
            r13.put(r10, r14)
            r11.initAccessibilityFocus(r12)
            android.util.SparseArray<android.view.View> r13 = r11.mViewFactory
            java.lang.Object r13 = r13.get(r0)
            android.view.View r13 = (android.view.View) r13
            if (r13 == 0) goto L_0x0134
            com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$bDDg6R4XX6z-_54DsKmxTeffRWo r14 = new com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$bDDg6R4XX6z-_54DsKmxTeffRWo
            r14.<init>()
            r13.setOnClickListener(r14)
        L_0x0134:
            android.util.SparseArray<android.view.View> r13 = r11.mViewFactory
            java.lang.Object r13 = r13.get(r1)
            android.view.View r13 = (android.view.View) r13
            if (r13 == 0) goto L_0x0146
            com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$oRytbPCux0tinbhaok1Gy24D790 r14 = new com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$oRytbPCux0tinbhaok1Gy24D790
            r14.<init>()
            r13.setOnClickListener(r14)
        L_0x0146:
            android.util.SparseArray<android.view.View> r13 = r11.mViewFactory
            java.lang.Object r13 = r13.get(r2)
            android.view.View r13 = (android.view.View) r13
            if (r13 == 0) goto L_0x0158
            com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$U3eMHYJuJU0UqSL1J_w1hzkU9Wk r14 = new com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$U3eMHYJuJU0UqSL1J_w1hzkU9Wk
            r14.<init>()
            r13.setOnClickListener(r14)
        L_0x0158:
            android.util.SparseArray<android.view.View> r13 = r11.mViewFactory
            java.lang.Object r13 = r13.get(r3)
            android.view.View r13 = (android.view.View) r13
            if (r13 == 0) goto L_0x016a
            com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$RAtPjtwFes2jSp8JpMnDajVGPIE r14 = new com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$RAtPjtwFes2jSp8JpMnDajVGPIE
            r14.<init>()
            r13.setOnClickListener(r14)
        L_0x016a:
            android.util.SparseArray<android.view.View> r13 = r11.mViewFactory
            java.lang.Object r13 = r13.get(r4)
            android.view.View r13 = (android.view.View) r13
            if (r13 == 0) goto L_0x017c
            com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$XyNVm-L3q9qQ09hm3p4-PFUttcs r14 = new com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$XyNVm-L3q9qQ09hm3p4-PFUttcs
            r14.<init>()
            r13.setOnClickListener(r14)
        L_0x017c:
            android.util.SparseArray<android.view.View> r13 = r11.mViewFactory
            java.lang.Object r13 = r13.get(r5)
            android.view.View r13 = (android.view.View) r13
            if (r13 == 0) goto L_0x018e
            com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$Rrt1t_3Jk2u_1fxYWxavzqrhl6o r14 = new com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$Rrt1t_3Jk2u_1fxYWxavzqrhl6o
            r14.<init>()
            r13.setOnClickListener(r14)
        L_0x018e:
            android.util.SparseArray<android.view.View> r13 = r11.mViewFactory
            java.lang.Object r13 = r13.get(r6)
            android.view.View r13 = (android.view.View) r13
            if (r13 == 0) goto L_0x01a0
            com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$yOpKKDj3sA0pYbuue9nczfyPmVo r14 = new com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$yOpKKDj3sA0pYbuue9nczfyPmVo
            r14.<init>()
            r13.setOnClickListener(r14)
        L_0x01a0:
            android.util.SparseArray<android.view.View> r13 = r11.mViewFactory
            java.lang.Object r13 = r13.get(r7)
            android.view.View r13 = (android.view.View) r13
            if (r13 == 0) goto L_0x01c5
            r13.semSetHoverPopupType(r10)
            com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$GlXAr-cmYHdC4_Sq31R96tx7Nrs r14 = new com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$GlXAr-cmYHdC4_Sq31R96tx7Nrs
            r14.<init>()
            r13.setOnClickListener(r14)
            com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$1VC4C4sX57Y-I47dGUQM4R99tpM r14 = new com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$1VC4C4sX57Y-I47dGUQM4R99tpM
            r14.<init>()
            r13.setOnLongClickListener(r14)
            com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$0pHD7DO2TrHRGe-YU4Dif7OqLiE r14 = new com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$0pHD7DO2TrHRGe-YU4Dif7OqLiE
            r14.<init>()
            r13.setOnTouchListener(r14)
        L_0x01c5:
            android.util.SparseArray<android.view.View> r13 = r11.mViewFactory
            java.lang.Object r13 = r13.get(r8)
            android.view.View r13 = (android.view.View) r13
            if (r13 == 0) goto L_0x01ea
            r13.semSetHoverPopupType(r10)
            com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$EhbAIWwBRDXavFCVfLRjpTjbMZE r14 = new com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$EhbAIWwBRDXavFCVfLRjpTjbMZE
            r14.<init>()
            r13.setOnClickListener(r14)
            com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$3Ak82LS_tiNRe2sHZOsup6kE0KQ r14 = new com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$3Ak82LS_tiNRe2sHZOsup6kE0KQ
            r14.<init>()
            r13.setOnLongClickListener(r14)
            com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$VK-5MYHPv20q9TsHJ9bEWEB3R80 r14 = new com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$VK-5MYHPv20q9TsHJ9bEWEB3R80
            r14.<init>()
            r13.setOnTouchListener(r14)
        L_0x01ea:
            android.util.SparseArray<android.view.View> r13 = r11.mViewFactory
            java.lang.Object r13 = r13.get(r9)
            android.view.View r13 = (android.view.View) r13
            if (r13 == 0) goto L_0x0211
            boolean r14 = com.sec.android.app.voicenote.provider.Settings.isEnabledShowButtonBG()
            if (r14 == 0) goto L_0x0209
            r14 = 2131296804(0x7f090224, float:1.8211535E38)
            android.view.View r14 = r12.findViewById(r14)
            android.widget.TextView r14 = (android.widget.TextView) r14
            r0 = 2131231117(0x7f08018d, float:1.8078306E38)
            r14.setBackgroundResource(r0)
        L_0x0209:
            com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$kn6_R2fbzBlvpQaYW4XrevkOg1Y r14 = new com.sec.android.app.voicenote.ui.-$$Lambda$SimpleControlButtonFragment$kn6_R2fbzBlvpQaYW4XrevkOg1Y
            r14.<init>()
            r13.setOnClickListener(r14)
        L_0x0211:
            com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$AbsButton r13 = r11.getButtons(r10)
            r11.mCurrentButton = r13
            androidx.fragment.app.FragmentActivity r13 = r11.getActivity()
            boolean r14 = r13 instanceof com.sec.android.app.voicenote.activity.SimpleActivity
            if (r14 == 0) goto L_0x022d
            com.sec.android.app.voicenote.activity.SimpleActivity r13 = (com.sec.android.app.voicenote.activity.SimpleActivity) r13
            com.sec.android.app.voicenote.uicore.SimpleFragmentController r13 = r13.getCurrentSimpleFragmentController()
            if (r13 == 0) goto L_0x022d
            int r13 = r13.getCurrentEvent()
            r11.mStartingEvent = r13
        L_0x022d:
            int r13 = r11.mStartingEvent
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            r11.onUpdate(r13)
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.SimpleControlButtonFragment.onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle):android.view.View");
    }

    public /* synthetic */ void lambda$onCreateView$1$SimpleControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.SIMPLE_MODE_CANCEL");
        this.mOnRecordResultListener.onRecordResult(Event.SIMPLE_MODE_CANCEL, -1);
    }

    public /* synthetic */ void lambda$onCreateView$3$SimpleControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.SIMPLE_RECORD_START");
        if (PermissionProvider.checkRecordPermission((AppCompatActivity) getActivity(), 2, C0690R.string.record)) {
            if (this.mSimpleEngine.getRecorderState() != 1) {
                if (this.mSimpleEngine.isSaveEnable()) {
                    ((SimpleActivity) getActivity()).finishNormalMode();
                } else {
                    return;
                }
            }
            if (this.mSimpleEngine.requestEngineFocus()) {
                setButtonDelay(view, DEFAULT_BUTTON_DELAY);
                this.mSimpleEngine.clearContentItem();
                int recordMode = this.mSimpleMetadata.getRecordMode();
                if (recordMode != 4 || Network.isNetworkConnected(getActivity())) {
                    if (recordMode != 4 || !SimpleEngineManager.getInstance().isWiredHeadSetConnected()) {
                        if (recordMode != 4 || RecognizerDBProvider.getTOSAcceptedState() == 1) {
                            if (!needRecognizerTOS()) {
                                if (recordMode == 5) {
                                    String stringExtra = getActivity().getIntent().getStringExtra("mime_type");
                                    long longExtra = getActivity().getIntent().getLongExtra("android.provider.MediaStore.extra.MAX_BYTES", 10247680);
                                    Log.m26i(TAG, "Record start with mime type: " + stringExtra + ", max size: " + longExtra);
                                    this.mSimpleEngine.setAudioFormat(new AudioFormat(stringExtra, longExtra));
                                } else {
                                    this.mSimpleEngine.setAudioFormat(new AudioFormat(recordMode));
                                }
                                view.post(new Runnable() {
                                    public final void run() {
                                        SimpleControlButtonFragment.this.lambda$null$2$SimpleControlButtonFragment();
                                    }
                                });
                            }
                        } else if (!DialogFactory.isDialogVisible(getFragmentManager(), DialogFactory.PRIVACY_POLICY_DIALOG)) {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(DialogFactory.BUNDLE_GDPR_COUNTRY, GdprProvider.getInstance().isGdprCountry());
                            DialogFactory.show(getFragmentManager(), DialogFactory.PRIVACY_POLICY_DIALOG, bundle);
                        }
                    } else if (!DialogFactory.isDialogVisible(getFragmentManager(), DialogFactory.MODE_NOT_SUPPORTED)) {
                        DialogFactory.show(getFragmentManager(), DialogFactory.MODE_NOT_SUPPORTED, (Bundle) null);
                    }
                } else if (!DialogFactory.isDialogVisible(getFragmentManager(), DialogFactory.NETWORK_NOT_CONNECTED)) {
                    DialogFactory.show(getFragmentManager(), DialogFactory.NETWORK_NOT_CONNECTED, (Bundle) null);
                }
            } else if (!SimpleEngineManager.getInstance().isAnyRecordingActive()) {
                Log.m19d(TAG, "Other recording cannot be stopped within 1 second of duration time!!!");
            } else {
                handleResultCode(-101);
            }
        }
    }

    public /* synthetic */ void lambda$null$2$SimpleControlButtonFragment() {
        SimpleEngine simpleEngine = this.mSimpleEngine;
        handleResultCode(simpleEngine.startRecord(simpleEngine.getAudioFormat()));
    }

    public /* synthetic */ void lambda$onCreateView$4$SimpleControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.SIMPLE_RECORD_STOP");
        setButtonDelay(view, DELAY_RECORD_STOP_BUTTON);
        if (this.mSimpleEngine.getRecorderState() != 1) {
            stopSimpleRecording(this.mSimpleEngine.stopRecord(true, false));
        }
    }

    public /* synthetic */ void lambda$onCreateView$5$SimpleControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.SIMPLE_RECORD_PLAY_START");
        setButtonDelay(view, DEFAULT_BUTTON_DELAY);
        if (this.mSimpleEngine.resumePlay() == 0) {
            postEvent(Event.SIMPLE_RECORD_PLAY_START);
            return;
        }
        int initPlay = this.mSimpleEngine.initPlay(this.mSimpleEngine.getSimpleModeItem(), true);
        if (initPlay == -103) {
            Toast.makeText(getActivity(), C0690R.string.no_play_during_call, 0).show();
        } else if (initPlay == 0) {
            postEvent(Event.SIMPLE_RECORD_PLAY_START);
        }
    }

    public /* synthetic */ void lambda$onCreateView$6$SimpleControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.SIMPLE_RECORD_PLAY_PAUSE");
        setButtonDelay(view, DEFAULT_BUTTON_DELAY);
        this.mSimpleEngine.pausePlay();
        postEvent(Event.SIMPLE_RECORD_PLAY_PAUSE);
    }

    public /* synthetic */ void lambda$onCreateView$7$SimpleControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.SIMPLE_PLAY_START");
        setButtonDelay(view, DEFAULT_BUTTON_DELAY);
        int resumePlay = this.mSimpleEngine.resumePlay();
        boolean z = false;
        if (resumePlay == -103) {
            Toast.makeText(getActivity(), C0690R.string.no_play_during_call, 0).show();
        } else if (resumePlay == 0) {
            if (this.mSimpleEngine.getPlayerState() == 4) {
                z = true;
            }
            if (z) {
                postEvent(Event.SIMPLE_PLAY_RESUME);
            } else {
                postEvent(Event.SIMPLE_PLAY_START);
            }
        }
    }

    public /* synthetic */ void lambda$onCreateView$8$SimpleControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.SIMPLE_PLAY_PAUSE");
        setButtonDelay(view, DEFAULT_BUTTON_DELAY);
        this.mSimpleEngine.pausePlay();
        onUpdate(Integer.valueOf(Event.SIMPLE_PLAY_PAUSE));
    }

    public /* synthetic */ void lambda$onCreateView$9$SimpleControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.SIMPLE_PLAY_RW");
        setButtonDelay(view, DEFAULT_BUTTON_DELAY);
        setSkipIntervalValue(SKIP_INTERVAL_PREV);
        this.mSimpleEngine.skipInterval(this.mSkipIntervalValue);
    }

    public /* synthetic */ boolean lambda$onCreateView$10$SimpleControlButtonFragment(View view) {
        stopSkipInterval();
        setSkipIntervalValue(SKIP_INTERVAL_PREV);
        this.mEventHandler.sendEmptyMessageDelayed(SKIP_INTERVAL_PREV, 50);
        return true;
    }

    public /* synthetic */ boolean lambda$onCreateView$11$SimpleControlButtonFragment(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() != 1) {
            return false;
        }
        stopSkipInterval();
        return false;
    }

    public /* synthetic */ void lambda$onCreateView$12$SimpleControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.SIMPLE_PLAY_FF");
        setButtonDelay(view, DEFAULT_BUTTON_DELAY);
        setSkipIntervalValue(SKIP_INTERVAL_NEXT);
        this.mSimpleEngine.skipInterval(this.mSkipIntervalValue);
    }

    public /* synthetic */ boolean lambda$onCreateView$13$SimpleControlButtonFragment(View view) {
        stopSkipInterval();
        setSkipIntervalValue(SKIP_INTERVAL_NEXT);
        this.mEventHandler.sendEmptyMessageDelayed(SKIP_INTERVAL_NEXT, 50);
        return true;
    }

    public /* synthetic */ boolean lambda$onCreateView$14$SimpleControlButtonFragment(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() != 1) {
            return false;
        }
        stopSkipInterval();
        return false;
    }

    public /* synthetic */ void lambda$onCreateView$15$SimpleControlButtonFragment(View view) {
        Log.m26i(TAG, "Event.SIMPLE_MODE_DONE");
        setButtonDelay(view, DEFAULT_BUTTON_DELAY);
        if (SimpleEngineManager.getInstance().getEngine(this.mSession).getRecorderState() == 1 || SimpleEngineManager.getInstance().getEngine(this.mSession).isSaveEnable()) {
            if (!SimpleEngineManager.getInstance().isEngineFocus(this.mSession)) {
                this.mOnRecordResultListener.onRecordResult(Event.SIMPLE_MODE_DONE, Long.valueOf(DBProvider.getInstance().getIdByPath(this.mSimpleEngine.getLastSavedFilePath())).longValue());
            }
            SimpleEngineManager.getInstance().abandonEngineFocus(this.mSession);
        }
    }

    private void initAccessibilityFocus(View view) {
        final View view2 = this.mViewFactory.get(Event.SIMPLE_RECORD_STOP);
        FrameLayout frameLayout = (FrameLayout) view.findViewById(C0690R.C0693id.simple_control_button_layout);
        if (VRUtil.isTalkBackOn(getContext()) && view2 != null) {
            view2.setContentDescription((CharSequence) null);
            view2.setImportantForAccessibility(2);
        }
        frameLayout.setAccessibilityDelegate(new View.AccessibilityDelegate() {
            public void onPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
                super.onPopulateAccessibilityEvent(view, accessibilityEvent);
                if (accessibilityEvent.getEventType() == 32768) {
                    if (!SimpleControlButtonFragment.this.mIsFirstTime) {
                        view2.setContentDescription(AssistantProvider.getInstance().getContentDesToButton(SimpleControlButtonFragment.this.getResources().getString(C0690R.string.stop)));
                    }
                    boolean unused = SimpleControlButtonFragment.this.mIsFirstTime = false;
                }
            }
        });
        this.mAccessibilityManager = (AccessibilityManager) getActivity().getApplicationContext().getSystemService("accessibility");
//        this.mAccessibilityStateChangeListener = new AccessibilityManager.AccessibilityStateChangeListener(view2) {
//            private final /* synthetic */ View f$1;
//
//            {
//                this.f$1 = r2;
//            }
//
//            public final void onAccessibilityStateChanged(boolean z) {
//                SimpleControlButtonFragment.this.lambda$initAccessibilityFocus$16$SimpleControlButtonFragment(this.f$1, z);
//            }
//        };
        this.mAccessibilityManager.addAccessibilityStateChangeListener(this.mAccessibilityStateChangeListener);
    }

    public /* synthetic */ void lambda$initAccessibilityFocus$16$SimpleControlButtonFragment(View view, boolean z) {
        if (!z || !VRUtil.isTalkBackOn(getContext())) {
            view.setContentDescription(getResources().getString(C0690R.string.stop));
            return;
        }
        view.setContentDescription((CharSequence) null);
        view.setImportantForAccessibility(2);
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        initSkipIntervalView();
        this.mSimpleEngine.registerListener(this);
    }

    public void onResume() {
        postEvent(15);
        super.onResume();
    }

    private void setButtonDelay(View view, int i) {
        if (view != null) {
            view.setEnabled(false);
//            view.postDelayed(new Runnable(view) {
//                private final /* synthetic */ View f$0;
//
//                {
//                    this.f$0 = r1;
//                }
//
//                public final void run() {
//                    this.f$0.setEnabled(true);
//                }
//            }, (long) i);
        }
    }

    private void setSkipIntervalValue(int i) {
        int duration = this.mSimpleEngine.getDuration();
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

    private void stopSkipInterval() {
        this.mSkippedTime = 0;
        this.mEventHandler.removeMessages(SKIP_INTERVAL_PREV);
        this.mEventHandler.removeMessages(SKIP_INTERVAL_NEXT);
    }

    private void initSkipIntervalView() {
        int i = this.mSimpleEngine.getDuration() > SKIP_INTERVAL_STANDARD_DURATION ? SKIP_INTERVAL_3SEC : 1000;
        int i2 = i / 1000;
        String string = getString(C0690R.string.second, String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(i2)}));
        TextView textView = (TextView) this.mViewFactory.get(Event.SIMPLE_PLAY_RW);
        if (textView != null) {
            textView.setText('-' + string);
            if (i == 1000) {
                textView.setContentDescription(getResources().getString(C0690R.string.string_interval_1sec_rewind));
            } else {
                textView.setContentDescription(getResources().getString(C0690R.string.string_interval_3sec_rewind, new Object[]{Integer.valueOf(i2)}));
            }
        }
        TextView textView2 = (TextView) this.mViewFactory.get(Event.SIMPLE_PLAY_FF);
        if (textView2 != null) {
            textView2.setText('+' + string);
            if (i == 1000) {
                textView2.setContentDescription(getResources().getString(C0690R.string.string_interval_1sec_forward));
            } else {
                textView2.setContentDescription(getResources().getString(C0690R.string.string_interval_3sec_forward, new Object[]{Integer.valueOf(i2)}));
            }
        }
    }

    public void onPause() {
        Log.m26i(TAG, "onPause");
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(128);
        }
        this.mIsRecorded = false;
        super.onPause();
    }

    public void onDestroyView() {
        Log.m26i(TAG, "onDestroyView");
        this.mSimpleEngine.unregisterListener(this);
        this.mAccessibilityManager.removeAccessibilityStateChangeListener(this.mAccessibilityStateChangeListener);
        super.onDestroyView();
    }

    public void onUpdate(Object obj) {
        if (isAdded()) {
            int intValue = ((Integer) obj).intValue();
            Log.m20d(TAG, "onUpdate uiEvent : " + intValue + " mCurrentEvent : " + this.mCurrentEvent, this.mSession);
            AbsButton buttons = getButtons(this.mCurrentEvent);
            AbsButton buttons2 = getButtons(intValue);
            boolean z = this.mCurrentEvent != 1 || !(this.mCurrentButton instanceof EmptyButton);
            for (int i = 0; i <= 4; i++) {
                AnimationFactory.changeButton(buttons.get(i), buttons2.get(i), z);
            }
            this.mCurrentEvent = intValue;
            setCurrentButton(buttons2);
            SimpleEngine simpleEngine = this.mSimpleEngine;
            if (simpleEngine == null) {
                Log.m22e(TAG, "mSimpleEngine is null");
            } else if (intValue == 983) {
                performClick(intValue);
            } else if (intValue == 50002) {
                View view = getView();
                if (view != null) {
                    view.findViewById(C0690R.C0693id.simple_control_button_layout).sendAccessibilityEvent(8);
                }
            } else if (intValue == 50007) {
                initSkipIntervalView();
            } else if (intValue != 8001) {
                if (intValue == 8002 && simpleEngine.getRecorderState() == 2) {
                    performClick(Event.SIMPLE_RECORD_STOP);
                }
            } else if (simpleEngine.getRecorderState() == 1) {
                performClick(Event.SIMPLE_RECORD_START);
            }
        }
    }

    private boolean needRecognizerTOS() {
        int recordMode = this.mSimpleMetadata.getRecordMode();
        Log.m26i(TAG, "needRecognizerTOS() : recordMode = " + recordMode);
        if (recordMode == 4) {
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
        if (i != -1 && i == 0 && !DialogFactory.isDialogVisible(getFragmentManager(), DialogFactory.PRIVACY_POLICY_DIALOG)) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(DialogFactory.BUNDLE_GDPR_COUNTRY, GdprProvider.getInstance().isGdprCountry());
            DialogFactory.show(getFragmentManager(), DialogFactory.PRIVACY_POLICY_DIALOG, bundle);
        }
    }

    public void onEngineUpdate(int i, int i2, int i3) {
        Handler handler = this.mEngineEventHandler;
        if (handler != null) {
            handler.sendMessage(handler.obtainMessage(i, i2, i3));
        }
    }

    public void onDestroy() {
        this.mEngineEventHandler = null;
        super.onDestroy();
    }

    private void setCurrentButton(AbsButton absButton) {
        this.mCurrentButton = absButton;
    }

    private AbsButton getButtons(int i) {
//        AbsButton absButton = this.mButtonFactory.get(i, (Object) null);
        AbsButton absButton = this.mButtonFactory.get(i, null);
        return absButton == null ? this.mCurrentButton : absButton;
    }

    /* renamed from: com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$RecordReadyButton */
    private class RecordReadyButton extends AbsButton {
        public RecordReadyButton() {
            super();
            this.mViewHolder.put(2, SimpleControlButtonFragment.this.mViewFactory.get(Event.SIMPLE_RECORD_START));
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$RecordButton */
    private class RecordButton extends AbsButton {
        public RecordButton() {
            super();
            this.mViewHolder.put(0, SimpleControlButtonFragment.this.mViewFactory.get(Event.SIMPLE_MODE_CANCEL));
            this.mViewHolder.put(2, SimpleControlButtonFragment.this.mViewFactory.get(Event.SIMPLE_RECORD_STOP));
            this.mViewHolder.put(4, SimpleControlButtonFragment.this.mViewFactory.get(Event.SIMPLE_MODE_DONE));
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$RecordPlayButton */
    private class RecordPlayButton extends AbsButton {
        public RecordPlayButton() {
            super();
            this.mViewHolder.put(0, SimpleControlButtonFragment.this.mViewFactory.get(Event.SIMPLE_MODE_CANCEL));
            this.mViewHolder.put(2, SimpleControlButtonFragment.this.mViewFactory.get(Event.SIMPLE_RECORD_PLAY_START));
            this.mViewHolder.put(4, SimpleControlButtonFragment.this.mViewFactory.get(Event.SIMPLE_MODE_DONE));
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$RecordPlayPauseButton */
    private class RecordPlayPauseButton extends AbsButton {
        public RecordPlayPauseButton() {
            super();
            this.mViewHolder.put(0, SimpleControlButtonFragment.this.mViewFactory.get(Event.SIMPLE_MODE_CANCEL));
            this.mViewHolder.put(2, SimpleControlButtonFragment.this.mViewFactory.get(Event.SIMPLE_RECORD_PLAY_PAUSE));
            this.mViewHolder.put(4, SimpleControlButtonFragment.this.mViewFactory.get(Event.SIMPLE_MODE_DONE));
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$PlayButton */
    private class PlayButton extends AbsButton {
        public PlayButton() {
            super();
            this.mViewHolder.put(1, SimpleControlButtonFragment.this.mViewFactory.get(Event.SIMPLE_PLAY_RW));
            this.mViewHolder.put(2, SimpleControlButtonFragment.this.mViewFactory.get(Event.SIMPLE_PLAY_PAUSE));
            this.mViewHolder.put(3, SimpleControlButtonFragment.this.mViewFactory.get(Event.SIMPLE_PLAY_FF));
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$PlayPauseButton */
    private class PlayPauseButton extends AbsButton {
        public PlayPauseButton() {
            super();
            this.mViewHolder.put(1, SimpleControlButtonFragment.this.mViewFactory.get(Event.SIMPLE_PLAY_RW));
            this.mViewHolder.put(2, SimpleControlButtonFragment.this.mViewFactory.get(Event.SIMPLE_PLAY_START));
            this.mViewHolder.put(3, SimpleControlButtonFragment.this.mViewFactory.get(Event.SIMPLE_PLAY_FF));
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$EmptyButton */
    private class EmptyButton extends AbsButton {
        public EmptyButton() {
            super();
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.SimpleControlButtonFragment$AbsButton */
    private abstract class AbsButton {
        final SparseArray<View> mViewHolder = new SparseArray<>();

        public AbsButton() {
            this.mViewHolder.clear();
        }

        public View get(int i) {
            return this.mViewHolder.get(i);
        }

        public void remove(int i) {
            this.mViewHolder.remove(i);
        }

        public void remove(View view) {
            int indexOfValue = this.mViewHolder.indexOfValue(view);
            if (indexOfValue != -1) {
                this.mViewHolder.removeAt(indexOfValue);
            }
        }

        public void add(int i, int i2) {
            if (this.mViewHolder.get(i2) != SimpleControlButtonFragment.this.mViewFactory.get(i)) {
                this.mViewHolder.put(4, SimpleControlButtonFragment.this.mViewFactory.get(i));
            }
        }
    }

    private void stopSimpleRecording(long j) {
        getActivity().getWindow().clearFlags(128);
        this.mSimpleEngine.setSimpleModeItem(j);
        if (j >= 0) {
            this.mSimpleEngine.initPlay(j, false);
            if (getActivity() != null) {
                getActivity().invalidateOptionsMenu();
            }
            int recordMode = this.mSimpleMetadata.getRecordMode();
            if (recordMode == 4) {
                Settings.setSettings(Settings.KEY_SIMPLE_PLAY_MODE, recordMode);
            } else {
                Settings.setSettings(Settings.KEY_SIMPLE_PLAY_MODE, 1);
            }
            postEvent(Event.SIMPLE_RECORD_STOP);
        }
    }

    public void onDialogResult(DialogFragment dialogFragment, Bundle bundle) {
        int i = bundle.getInt(DialogFactory.BUNDLE_REQUEST_CODE, -1);
        int i2 = bundle.getInt("result_code", -1);
        if (i == 12) {
            this.mOnRecordResultListener.onRecordResult(Event.CHANGE_STORAGE, (long) i2);
        }
    }

    private boolean performClick(int i) {
        View view = this.mViewFactory.get(i);
        return view != null && view.performClick();
    }

    private boolean isInMultiWindow() {
        return !getActivity().isDestroyed() && getActivity().isInMultiWindowMode();
    }

    private void handleResultCode(int i) {
        if (i == -121) {
            Toast.makeText(getActivity(), C0690R.string.low_battery_msg, 0).show();
        } else if (i == -120) {
            Toast.makeText(getActivity(), C0690R.string.recording_now, 0).show();
            Log.m22e(TAG, "ANOTHER_RECORDER_ALREADY_RUNNING !!!!");
        } else if (i != -107) {
            if (i == 0) {
                this.mIsRecorded = true;
                getActivity().invalidateOptionsMenu();
                getActivity().getWindow().addFlags(128);
                postEvent(Event.SIMPLE_RECORD_START);
            } else if (i != -102) {
                if (i != -101) {
                    Toast.makeText(getActivity(), C0690R.string.recording_failed, 0).show();
                    Log.m32w(TAG, "startRecord - exceptional case " + i);
                    return;
                }
                Toast.makeText(getActivity(), C0690R.string.recording_failed, 0).show();
                Log.m32w(TAG, "startRecord - failed !!!!");
            } else if (PhoneStateProvider.getInstance().isDuringCall(getActivity())) {
                Toast.makeText(getActivity(), C0690R.string.no_rec_during_call, 0).show();
            } else {
                Toast.makeText(getActivity(), C0690R.string.no_rec_during_incoming_calls, 0).show();
            }
        } else if (StorageProvider.alternativeStorageRecordEnable()) {
            DialogFactory.show(getFragmentManager(), DialogFactory.STORAGE_CHANGE_DIALOG, (Bundle) null, this);
        } else {
            DialogFactory.show(getFragmentManager(), DialogFactory.STORAGE_FULL_DIALOG, (Bundle) null);
        }
    }
}
