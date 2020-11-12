package com.sec.android.app.voicenote.uicore;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.common.util.Trace;
import com.sec.android.app.voicenote.p007ui.AbsFragment;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.HWKeyboardProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.NavigationBarProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.MetadataRepository;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Observer;

public class FragmentController implements Observer {
    private static final String BOOKMARK = "Bookmark";
    private static final String CONTROLBUTTON = "ControlButton";
    private static final AbsScene EDIT_SCENE = new EditScene();
    private static final AbsScene EMPTY_SCENE = new EmptyScene();
    private static final SparseArray<AbsScene> EVENT_SCENE_TABLE = new SparseArray<>();
    private static final HashMap<String, Integer> FRAGMENT_LAYOUT_TABLE = new HashMap<>();
    private static final String IDLEBUTTON = "IdleControlButton";
    private static final String INFO = "Info";
    private static final String LIST = "List";
    private static final AbsScene LIST_SCENE = new ListScene();
    private static final AbsScene MAIN_SCENE = new MainScene();
    private static final AbsScene MINI_PLAY_SCENE = new MiniPlayScene();
    private static final int MSG_ADD_FRAGMENT_CONTROLBUTTON = 1;
    private static final AbsScene PLAY_SCENE = new PlayScene();
    private static final AbsScene PRERECORD_SCENE = new PreRecordScene();
    private static final AbsScene PRIVATE_SELECT_SCENE = new PrivateSelectScene();
    private static final AbsScene RECORD_SCENE = new RecordScene();
    private static final String SEARCH = "Search";
    private static final AbsScene SEARCH_SCENE = new SearchScene();
    private static final AbsScene SEARCH_SELECT_SCENE = new SearchSelectScene();
    private static final AbsScene SELECT_SCENE = new SelectScene();
    private static final String STT = "Stt";
    private static final AbsScene STT_TRANSLATION_SCENE = new TranslationScene();
    private static final String TAG = "FragmentController";
    private static final String TAG_END_ANIMATION = "endAnimation";
    private static final String TAG_EVENT = "event";
    private static final String TAG_START_ANIMATION = "startAnimation";
    private static final String TOOLBAR = "Toolbar";
    private static final String TRASH = "Trash";
    private static final AbsScene TRASH_MINI_PLAY_SCENE = new TrashMiniPlayScene();
    private static final AbsScene TRASH_SCENE = new TrashScene();
    private static final AbsScene TRASH_SELECT_SCENE = new TrashSelectScene();
    private static final String WAVE = "Wave";
    private static volatile FragmentController mInstance = null;
    private boolean isNeedUpdateLayout = false;
    private AppCompatActivity mActivity = null;
    private int mCurrentEvent = 4;
    private int mCurrentScene = 0;
    @SuppressLint({"HandlerLeak"})
    private Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            Log.m19d(FragmentController.TAG, "handleMessage what : " + message.what);
            if (message.what == 1) {
                FragmentController.this.updateControlButtonLayout(true);
                Bundle data = message.getData();
                FragmentController.this.addFragment(FragmentController.CONTROLBUTTON, data.getInt("event"), data.getInt(FragmentController.TAG_START_ANIMATION), data.getInt(FragmentController.TAG_END_ANIMATION));
            }
        }
    };
    private boolean mIsFromMain = false;
    private final ArrayList<WeakReference<OnSceneChangeListener>> mListeners = new ArrayList<>();
    private final VoiceNoteObservable mObservable = VoiceNoteObservable.getInstance();
    private int mOldPlayMode = -1;
    private int mPreviouScene = 0;

    public interface OnSceneChangeListener {
        void onSceneChange(int i);
    }

    public static class Scene {
        public static final int Edit = 6;
        public static final int Empty = 0;
        public static final int List = 2;
        public static final int Main = 1;
        public static final int MiniPlay = 3;
        public static final int Play = 4;
        public static final int PreRecord = 11;
        public static final int PrivateSelect = 9;
        public static final int Record = 8;
        public static final int Search = 7;
        public static final int SearchSelect = 10;
        public static final int Select = 5;
        public static final int Stt_Translation = 12;
        public static final int Trash = 13;
        public static final int TrashMiniPlay = 15;
        public static final int TrashSelect = 14;
    }

    private boolean isSttViewLayoutVisible(int i, int i2, int i3) {
        return (i == 4 && i2 == 4) || (i == 8 && i3 == 4) || i == 12 || (i == 6 && i2 == 4);
    }

    public static FragmentController getInstance() {
        if (mInstance == null) {
            synchronized (FragmentController.class) {
                if (mInstance == null) {
                    mInstance = new FragmentController();
                }
            }
        }
        return mInstance;
    }

    public static boolean hasInstance() {
        Log.m19d(TAG, "hasInstance - " + mInstance);
        return mInstance != null;
    }

    public void setContext(AppCompatActivity appCompatActivity) {
        this.mActivity = appCompatActivity;
    }

    private FragmentController() {
        Log.m26i(TAG, "create FragmentController");
        EVENT_SCENE_TABLE.put(1, EMPTY_SCENE);
        EVENT_SCENE_TABLE.put(3, LIST_SCENE);
        EVENT_SCENE_TABLE.put(4, MAIN_SCENE);
        EVENT_SCENE_TABLE.put(Event.OPEN_FULL_PLAYER, PLAY_SCENE);
        EVENT_SCENE_TABLE.put(1009, PRERECORD_SCENE);
        EVENT_SCENE_TABLE.put(Event.RECORD_BY_LEVEL_ACTIVEKEY, PRERECORD_SCENE);
        EVENT_SCENE_TABLE.put(1001, RECORD_SCENE);
        EVENT_SCENE_TABLE.put(1002, RECORD_SCENE);
        EVENT_SCENE_TABLE.put(1003, RECORD_SCENE);
        EVENT_SCENE_TABLE.put(1004, LIST_SCENE);
        EVENT_SCENE_TABLE.put(1007, RECORD_SCENE);
        EVENT_SCENE_TABLE.put(1008, RECORD_SCENE);
        EVENT_SCENE_TABLE.put(1006, MAIN_SCENE);
        EVENT_SCENE_TABLE.put(Event.CHANGE_MODE, MAIN_SCENE);
        EVENT_SCENE_TABLE.put(1010, MAIN_SCENE);
        EVENT_SCENE_TABLE.put(Event.PLAY_START, PLAY_SCENE);
        EVENT_SCENE_TABLE.put(Event.PLAY_PAUSE, PLAY_SCENE);
        EVENT_SCENE_TABLE.put(Event.PLAY_RESUME, PLAY_SCENE);
        EVENT_SCENE_TABLE.put(Event.PLAY_PAUSE, PLAY_SCENE);
        EVENT_SCENE_TABLE.put(Event.PLAY_NEXT, PLAY_SCENE);
        EVENT_SCENE_TABLE.put(Event.PLAY_PREV, PLAY_SCENE);
        EVENT_SCENE_TABLE.put(Event.PLAY_STOP, PLAY_SCENE);
        EVENT_SCENE_TABLE.put(6, SELECT_SCENE);
        EVENT_SCENE_TABLE.put(13, SEARCH_SELECT_SCENE);
        EVENT_SCENE_TABLE.put(10, PRIVATE_SELECT_SCENE);
        EVENT_SCENE_TABLE.put(7, LIST_SCENE);
        EVENT_SCENE_TABLE.put(14, SEARCH_SCENE);
        EVENT_SCENE_TABLE.put(Event.MINI_PLAY_START, MINI_PLAY_SCENE);
        EVENT_SCENE_TABLE.put(Event.MINI_PLAY_PAUSE, MINI_PLAY_SCENE);
        EVENT_SCENE_TABLE.put(Event.MINI_PLAY_RESUME, MINI_PLAY_SCENE);
        EVENT_SCENE_TABLE.put(Event.MINI_PLAY_NEXT, MINI_PLAY_SCENE);
        EVENT_SCENE_TABLE.put(Event.MINI_PLAY_PREV, MINI_PLAY_SCENE);
        EVENT_SCENE_TABLE.put(5, EDIT_SCENE);
        EVENT_SCENE_TABLE.put(Event.EDIT_PLAY_START, EDIT_SCENE);
        EVENT_SCENE_TABLE.put(Event.EDIT_PLAY_PAUSE, EDIT_SCENE);
        EVENT_SCENE_TABLE.put(Event.EDIT_PLAY_RESUME, EDIT_SCENE);
        EVENT_SCENE_TABLE.put(Event.EDIT_RECORD, EDIT_SCENE);
        EVENT_SCENE_TABLE.put(Event.EDIT_RECORD_PAUSE, EDIT_SCENE);
        EVENT_SCENE_TABLE.put(Event.EDIT_RECORD_SAVE, EDIT_SCENE);
        EVENT_SCENE_TABLE.put(Event.START_SEARCH, SEARCH_SCENE);
        EVENT_SCENE_TABLE.put(Event.SEARCH_PLAY_START, SEARCH_SCENE);
        EVENT_SCENE_TABLE.put(Event.SEARCH_PLAY_PAUSE, SEARCH_SCENE);
        EVENT_SCENE_TABLE.put(Event.SEARCH_PLAY_RESUME, SEARCH_SCENE);
        EVENT_SCENE_TABLE.put(Event.SEARCH_PLAY_STOP, SEARCH_SCENE);
        EVENT_SCENE_TABLE.put(Event.SEARCH_MINI_PLAY_NEXT, SEARCH_SCENE);
        EVENT_SCENE_TABLE.put(17, STT_TRANSLATION_SCENE);
        EVENT_SCENE_TABLE.put(Event.TRANSLATION_START, STT_TRANSLATION_SCENE);
        EVENT_SCENE_TABLE.put(Event.TRANSLATION_PAUSE, STT_TRANSLATION_SCENE);
        EVENT_SCENE_TABLE.put(Event.TRANSLATION_RESUME, STT_TRANSLATION_SCENE);
        EVENT_SCENE_TABLE.put(Event.TRANSLATION_SAVE, STT_TRANSLATION_SCENE);
        EVENT_SCENE_TABLE.put(Event.TRANSLATION_CANCEL, STT_TRANSLATION_SCENE);
        EVENT_SCENE_TABLE.put(Event.OPEN_TRASH, TRASH_SCENE);
        EVENT_SCENE_TABLE.put(Event.TRASH_SELECT, TRASH_SELECT_SCENE);
        EVENT_SCENE_TABLE.put(Event.TRASH_DESELECT, TRASH_SCENE);
        EVENT_SCENE_TABLE.put(Event.TRASH_MINI_PLAY_START, TRASH_MINI_PLAY_SCENE);
        EVENT_SCENE_TABLE.put(Event.TRASH_MINI_PLAY_PAUSE, TRASH_MINI_PLAY_SCENE);
        EVENT_SCENE_TABLE.put(Event.TRASH_MINI_PLAY_RESUME, TRASH_MINI_PLAY_SCENE);
        FRAGMENT_LAYOUT_TABLE.put(INFO, Integer.valueOf(C0690R.C0693id.main_info));
        FRAGMENT_LAYOUT_TABLE.put("Wave", Integer.valueOf(C0690R.C0693id.main_wave));
        FRAGMENT_LAYOUT_TABLE.put(BOOKMARK, Integer.valueOf(C0690R.C0693id.main_bookmark));
        FRAGMENT_LAYOUT_TABLE.put(STT, Integer.valueOf(C0690R.C0693id.main_stt));
        FRAGMENT_LAYOUT_TABLE.put(CONTROLBUTTON, Integer.valueOf(C0690R.C0693id.main_controlbutton));
        HashMap<String, Integer> hashMap = FRAGMENT_LAYOUT_TABLE;
        Integer valueOf = Integer.valueOf(C0690R.C0693id.main_list);
        hashMap.put(LIST, valueOf);
        FRAGMENT_LAYOUT_TABLE.put(TOOLBAR, Integer.valueOf(C0690R.C0693id.main_toolbar));
        FRAGMENT_LAYOUT_TABLE.put(IDLEBUTTON, Integer.valueOf(C0690R.C0693id.main_idle_controlbutton));
        FRAGMENT_LAYOUT_TABLE.put(SEARCH, valueOf);
        FRAGMENT_LAYOUT_TABLE.put(TRASH, valueOf);
        this.mObservable.addObserver(this);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00f2, code lost:
        r0 = com.sec.android.app.voicenote.service.Engine.getInstance().isShowFilePlayingWhenSearch();
        removeTrashOfPlayer();
        update(r13.mObservable, java.lang.Integer.valueOf(com.sec.android.app.voicenote.provider.Event.SEARCH_PLAY_STOP));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x0108, code lost:
        if (r0 != false) goto L_0x026d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x010a, code lost:
        update(r13.mObservable, java.lang.Integer.valueOf(com.sec.android.app.voicenote.provider.Event.STOP_SEARCH));
        update(r13.mObservable, 3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x012c, code lost:
        update(r13.mObservable, java.lang.Integer.valueOf(com.sec.android.app.voicenote.provider.Event.STOP_SEARCH));
        update(r13.mObservable, 3);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onBackKeyPressed() {
        /*
            r13 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "onBackKeyPressed - current event : "
            r0.append(r1)
            int r1 = r13.mCurrentEvent
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "FragmentController"
            com.sec.android.app.voicenote.provider.Log.m19d(r1, r0)
            androidx.appcompat.app.AppCompatActivity r0 = r13.mActivity
            r2 = 0
            if (r0 != 0) goto L_0x0023
            java.lang.String r0 = "onBackKeyPressed mActivity is NULL"
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r0)
            return r2
        L_0x0023:
            com.sec.android.app.voicenote.provider.PhoneStateProvider r0 = com.sec.android.app.voicenote.provider.PhoneStateProvider.getInstance()
            androidx.appcompat.app.AppCompatActivity r3 = r13.mActivity
            android.content.Context r3 = r3.getApplicationContext()
            boolean r0 = r0.isCallIdle(r3)
            r3 = 6
            r4 = 1
            if (r0 != 0) goto L_0x003f
            int r0 = r13.mCurrentScene
            if (r0 != r3) goto L_0x003f
            java.lang.String r0 = "onBackKeyPressed During the call"
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r0)
            return r4
        L_0x003f:
            androidx.appcompat.app.AppCompatActivity r0 = r13.mActivity
            androidx.fragment.app.FragmentManager r0 = r0.getSupportFragmentManager()
            if (r0 != 0) goto L_0x004d
            java.lang.String r0 = "onBackKeyPressed FragmentManager is NULL"
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r0)
            return r2
        L_0x004d:
            boolean r5 = r13.isBackPossible()
            if (r5 == 0) goto L_0x026e
            com.sec.android.app.voicenote.service.helper.BixbyHelper r5 = com.sec.android.app.voicenote.service.helper.BixbyHelper.getInstance()
            boolean r5 = r5.isBackPosible()
            if (r5 != 0) goto L_0x005f
            goto L_0x026e
        L_0x005f:
            boolean r5 = com.sec.android.app.voicenote.p007ui.dialog.DialogFactory.clearTopDialog(r0)
            if (r5 == 0) goto L_0x0066
            return r4
        L_0x0066:
            boolean r5 = r13.isGuideExist()
            if (r5 == 0) goto L_0x006d
            return r4
        L_0x006d:
            int r5 = r13.mCurrentEvent
            r6 = 5
            r7 = 0
            r8 = 975(0x3cf, float:1.366E-42)
            if (r5 == r6) goto L_0x0205
            r6 = 7
            r9 = 3
            if (r5 == r3) goto L_0x01f2
            r3 = 4
            if (r5 == r6) goto L_0x01cc
            r10 = 14
            r11 = 13
            if (r5 == r11) goto L_0x01b6
            if (r5 == r10) goto L_0x01ab
            r10 = 946(0x3b2, float:1.326E-42)
            r12 = 947(0x3b3, float:1.327E-42)
            if (r5 == r10) goto L_0x0195
            if (r5 == r12) goto L_0x0175
            r10 = 1007(0x3ef, float:1.411E-42)
            if (r5 == r10) goto L_0x0164
            r10 = 1008(0x3f0, float:1.413E-42)
            if (r5 == r10) goto L_0x0164
            r10 = 2005(0x7d5, float:2.81E-42)
            if (r5 == r10) goto L_0x0156
            r10 = 2006(0x7d6, float:2.811E-42)
            if (r5 == r10) goto L_0x0156
            r10 = 991(0x3df, float:1.389E-42)
            switch(r5) {
                case 3: goto L_0x01cc;
                case 10: goto L_0x01f2;
                case 17: goto L_0x014b;
                case 943: goto L_0x0140;
                case 975: goto L_0x0156;
                case 992: goto L_0x012c;
                case 3001: goto L_0x0156;
                case 3002: goto L_0x0156;
                case 3003: goto L_0x0156;
                case 3004: goto L_0x0156;
                case 3005: goto L_0x0156;
                case 3006: goto L_0x011e;
                case 3007: goto L_0x011e;
                case 3008: goto L_0x011e;
                case 5012: goto L_0x0205;
                case 6009: goto L_0x00f2;
                case 7001: goto L_0x00b7;
                case 7002: goto L_0x00b7;
                case 7003: goto L_0x00b7;
                default: goto L_0x00a1;
            }
        L_0x00a1:
            switch(r5) {
                case 1001: goto L_0x0164;
                case 1002: goto L_0x0164;
                case 1003: goto L_0x0164;
                case 1004: goto L_0x01cc;
                default: goto L_0x00a4;
            }
        L_0x00a4:
            switch(r5) {
                case 2001: goto L_0x0156;
                case 2002: goto L_0x0156;
                case 2003: goto L_0x0156;
                default: goto L_0x00a7;
            }
        L_0x00a7:
            switch(r5) {
                case 5001: goto L_0x0205;
                case 5002: goto L_0x0205;
                case 5003: goto L_0x0205;
                case 5004: goto L_0x0205;
                case 5005: goto L_0x0205;
                default: goto L_0x00aa;
            }
        L_0x00aa:
            switch(r5) {
                case 6001: goto L_0x00f2;
                case 6002: goto L_0x00f2;
                case 6003: goto L_0x00f2;
                case 6004: goto L_0x012c;
                default: goto L_0x00ad;
            }
        L_0x00ad:
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = r13.mObservable
            java.lang.Integer r1 = java.lang.Integer.valueOf(r3)
            r13.update(r0, r1)
            return r2
        L_0x00b7:
            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
            boolean r0 = r0.isSaveTranslatable()
            if (r0 == 0) goto L_0x026d
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = r13.mObservable
            r1 = 7002(0x1b5a, float:9.812E-42)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r13.update(r0, r1)
            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
            r0.pauseTranslation(r2)
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            com.sec.android.app.voicenote.service.Engine r1 = com.sec.android.app.voicenote.service.Engine.getInstance()
            java.lang.String r1 = r1.getPath()
            java.lang.String r2 = "path"
            r0.putString(r2, r1)
            androidx.appcompat.app.AppCompatActivity r1 = r13.mActivity
            androidx.fragment.app.FragmentManager r1 = r1.getSupportFragmentManager()
            java.lang.String r2 = "TranslationCancelDialog"
            com.sec.android.app.voicenote.p007ui.dialog.DialogFactory.show(r1, r2, r0)
            goto L_0x026d
        L_0x00f2:
            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
            boolean r0 = r0.isShowFilePlayingWhenSearch()
            r13.removeTrashOfPlayer()
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r1 = r13.mObservable
            r2 = 6004(0x1774, float:8.413E-42)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r13.update(r1, r2)
            if (r0 != 0) goto L_0x026d
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = r13.mObservable
            java.lang.Integer r1 = java.lang.Integer.valueOf(r10)
            r13.update(r0, r1)
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = r13.mObservable
            java.lang.Integer r1 = java.lang.Integer.valueOf(r9)
            r13.update(r0, r1)
            goto L_0x026d
        L_0x011e:
            r13.removeTrashOfPlayer()
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = r13.mObservable
            java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
            r13.update(r0, r1)
            goto L_0x026d
        L_0x012c:
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = r13.mObservable
            java.lang.Integer r1 = java.lang.Integer.valueOf(r10)
            r13.update(r0, r1)
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = r13.mObservable
            java.lang.Integer r1 = java.lang.Integer.valueOf(r9)
            r13.update(r0, r1)
            goto L_0x026d
        L_0x0140:
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = r13.mObservable
            java.lang.Integer r1 = java.lang.Integer.valueOf(r9)
            r13.update(r0, r1)
            goto L_0x026d
        L_0x014b:
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = r13.mObservable
            java.lang.Integer r1 = java.lang.Integer.valueOf(r8)
            r13.update(r0, r1)
            goto L_0x026d
        L_0x0156:
            r13.removeTrashOfPlayer()
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = r13.mObservable
            java.lang.Integer r1 = java.lang.Integer.valueOf(r9)
            r13.update(r0, r1)
            goto L_0x026d
        L_0x0164:
            com.sec.android.app.voicenote.service.Engine r1 = com.sec.android.app.voicenote.service.Engine.getInstance()
            boolean r1 = r1.isSaveEnable()
            if (r1 == 0) goto L_0x026d
            java.lang.String r1 = "RecordCancelDialog"
            com.sec.android.app.voicenote.p007ui.dialog.DialogFactory.show(r0, r1, r7)
            goto L_0x026d
        L_0x0175:
            int r0 = r13.mCurrentScene
            if (r0 != r11) goto L_0x018a
            boolean r0 = r13.mIsFromMain
            if (r0 == 0) goto L_0x018a
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = r13.mObservable
            java.lang.Integer r1 = java.lang.Integer.valueOf(r3)
            r13.update(r0, r1)
            r13.mIsFromMain = r2
            goto L_0x026d
        L_0x018a:
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = r13.mObservable
            java.lang.Integer r1 = java.lang.Integer.valueOf(r9)
            r13.update(r0, r1)
            goto L_0x026d
        L_0x0195:
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = r13.mObservable
            r1 = 943(0x3af, float:1.321E-42)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r13.update(r0, r1)
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = r13.mObservable
            java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
            r13.update(r0, r1)
            goto L_0x026d
        L_0x01ab:
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = r13.mObservable
            java.lang.Integer r1 = java.lang.Integer.valueOf(r9)
            r13.update(r0, r1)
            goto L_0x026d
        L_0x01b6:
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = r13.mObservable
            java.lang.Integer r1 = java.lang.Integer.valueOf(r10)
            r13.update(r0, r1)
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = r13.mObservable
            r1 = 992(0x3e0, float:1.39E-42)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r13.update(r0, r1)
            goto L_0x026d
        L_0x01cc:
            com.sec.android.app.voicenote.common.util.DataRepository r0 = com.sec.android.app.voicenote.common.util.DataRepository.getInstance()
            com.sec.android.app.voicenote.common.util.CategoryRepository r0 = r0.getCategoryRepository()
            boolean r0 = r0.isChildList()
            if (r0 == 0) goto L_0x01e7
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = r13.mObservable
            r1 = 960(0x3c0, float:1.345E-42)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r0.notifyObservers(r1)
            goto L_0x026d
        L_0x01e7:
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = r13.mObservable
            java.lang.Integer r1 = java.lang.Integer.valueOf(r3)
            r13.update(r0, r1)
            goto L_0x026d
        L_0x01f2:
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = r13.mObservable
            java.lang.Integer r1 = java.lang.Integer.valueOf(r6)
            r13.update(r0, r1)
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = r13.mObservable
            java.lang.Integer r1 = java.lang.Integer.valueOf(r9)
            r13.update(r0, r1)
            goto L_0x026d
        L_0x0205:
            com.sec.android.app.voicenote.service.Engine r3 = com.sec.android.app.voicenote.service.Engine.getInstance()
            int r3 = r3.getEngineState()
            r5 = 2
            if (r3 != r5) goto L_0x0222
            androidx.appcompat.app.AppCompatActivity r0 = r13.mActivity
            r3 = 2131755411(0x7f100193, float:1.91417E38)
            android.widget.Toast r0 = android.widget.Toast.makeText(r0, r3, r2)
            r0.show()
            java.lang.String r0 = "Engine BUSY !!!!"
            com.sec.android.app.voicenote.provider.Log.m32w((java.lang.String) r1, (java.lang.String) r0)
            goto L_0x026d
        L_0x0222:
            java.io.File r1 = new java.io.File
            com.sec.android.app.voicenote.service.Engine r3 = com.sec.android.app.voicenote.service.Engine.getInstance()
            java.lang.String r3 = r3.getRecentFilePath()
            r1.<init>(r3)
            boolean r3 = r1.exists()
            if (r3 != 0) goto L_0x024d
            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
            r0.stopRecord(r4, r2)
            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
            r0.stopPlay(r2)
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = r13.mObservable
            java.lang.Integer r1 = java.lang.Integer.valueOf(r8)
            r13.update(r0, r1)
            goto L_0x026d
        L_0x024d:
            java.lang.String r1 = r1.getPath()
            boolean r1 = com.sec.android.app.voicenote.provider.StorageProvider.isTempFile(r1)
            if (r1 == 0) goto L_0x025d
            java.lang.String r1 = "EditCancelDialog"
            com.sec.android.app.voicenote.p007ui.dialog.DialogFactory.show(r0, r1, r7)
            goto L_0x026d
        L_0x025d:
            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
            r0.pausePlay()
            com.sec.android.app.voicenote.uicore.VoiceNoteObservable r0 = r13.mObservable
            java.lang.Integer r1 = java.lang.Integer.valueOf(r8)
            r13.update(r0, r1)
        L_0x026d:
            return r4
        L_0x026e:
            java.lang.String r0 = "onBackKeyPressed back key is impossible"
            com.sec.android.app.voicenote.provider.Log.m26i(r1, r0)
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.uicore.FragmentController.onBackKeyPressed():boolean");
    }

    private void removeTrashOfPlayer() {
        Engine.getInstance().setCurrentTime(0);
        Engine.getInstance().stopPlay();
        Engine.getInstance().setOriginalFilePath((String) null);
        MetadataRepository.getInstance().close();
        Engine.getInstance().clearContentItem();
        CursorProvider.getInstance().resetCurrentPlayingItemPosition();
    }

    /* access modifiers changed from: private */
    public void addFragment(String str, int i, int i2, int i3) {
        Trace.beginSection("FrgmControl.addFragment_" + str);
        Log.m26i(TAG, "addFragment - tag: " + str + " event : " + i);
        AbsFragment create = FragmentFactory.create(str);
        if (create == null) {
            Log.m22e(TAG, "addFragment - tag name : " + str + " is null");
            return;
        }
        create.setEvent(i);
        FragmentTransaction beginTransaction = this.mActivity.getSupportFragmentManager().beginTransaction();
        beginTransaction.setCustomAnimations(i2, i3);
        beginTransaction.replace(getContainerViewId(str), create, str);
        beginTransaction.commitAllowingStateLoss();
        Trace.endSection();
    }

    private void addFragmentWithDelay(String str, int i, int i2, int i3, int i4) {
        Log.m19d(TAG, "addFragmentWithDelay delay : " + i4);
        if (CONTROLBUTTON.equals(str)) {
            this.mHandler.removeMessages(1);
            Bundle bundle = new Bundle();
            bundle.putInt("event", i);
            bundle.putInt(TAG_START_ANIMATION, i2);
            bundle.putInt(TAG_END_ANIMATION, i3);
            Handler handler = this.mHandler;
            handler.sendMessageDelayed(handler.obtainMessage(1, bundle), (long) i4);
        }
    }

    private void removeViewOfFragment(String str) {
        FrameLayout frameLayout = (FrameLayout) this.mActivity.getWindow().getDecorView().findViewById(getContainerViewId(str));
        if (frameLayout != null && !CONTROLBUTTON.equals(str) && !IDLEBUTTON.equals(str)) {
            frameLayout.removeAllViewsInLayout();
        }
    }

    private void removeFragment(String str, int i, int i2) {
        Log.m26i(TAG, "removeFragment - tag: " + str);
        AbsFragment absFragment = FragmentFactory.get(str);
        if (str.equals(STT) && absFragment == null) {
            absFragment = (AbsFragment) this.mActivity.getSupportFragmentManager().findFragmentByTag(str);
        }
        if (absFragment == null) {
            Log.m22e(TAG, "removeFragment - tag name : " + str + " is null");
            return;
        }
        FragmentTransaction beginTransaction = this.mActivity.getSupportFragmentManager().beginTransaction();
        beginTransaction.setCustomAnimations(i, i2);
        beginTransaction.remove(absFragment);
        beginTransaction.commitAllowingStateLoss();
        FragmentFactory.remove(str);
    }

    private void refreshFragment(String str, int i, int i2) {
        Log.m26i(TAG, "refreshFragment - tag: " + str);
        AbsFragment absFragment = FragmentFactory.get(str);
        if (absFragment == null) {
            Log.m22e(TAG, "refreshFragment - tag name : " + str + " is null");
            return;
        }
        FragmentTransaction beginTransaction = this.mActivity.getSupportFragmentManager().beginTransaction();
        beginTransaction.setCustomAnimations(i, i2);
        beginTransaction.detach(absFragment);
        beginTransaction.attach(absFragment);
        beginTransaction.commitAllowingStateLoss();
    }

    private void updateFragment(String str, int i) {
        Log.m26i(TAG, "updateFragment : " + str);
        AbsFragment absFragment = FragmentFactory.get(str);
        if (absFragment == null) {
            addFragment(str, i, 0, 0);
        } else {
            absFragment.onUpdate(Integer.valueOf(i));
        }
    }

    private int getContainerViewId(String str) {
        return FRAGMENT_LAYOUT_TABLE.get(str).intValue();
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

    /* JADX WARNING: Code restructure failed: missing block: B:214:0x03b2, code lost:
        r4 = r23;
        r6 = r24;
        r10 = 1;
     */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x0429 A[Catch:{ all -> 0x04d6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x0451 A[Catch:{ all -> 0x04d6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x011e A[SYNTHETIC, Splitter:B:72:0x011e] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:48:0x0082=Splitter:B:48:0x0082, B:273:0x04b3=Splitter:B:273:0x04b3, B:68:0x0109=Splitter:B:68:0x0109} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(java.util.Observable r26, java.lang.Object r27) {
        /*
            r25 = this;
            r7 = r25
            java.lang.String r0 = "FrgmController.update"
            com.sec.android.app.voicenote.common.util.Trace.beginSection(r0)     // Catch:{ all -> 0x04d6 }
            r0 = r27
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ all -> 0x04d6 }
            int r0 = r0.intValue()     // Catch:{ all -> 0x04d6 }
            r8 = 2
            if (r0 != r8) goto L_0x0014
            int r0 = r7.mCurrentEvent     // Catch:{ all -> 0x04d6 }
        L_0x0014:
            java.lang.String r9 = "FragmentController"
            r10 = 1
            if (r0 == r10) goto L_0x04b3
            androidx.appcompat.app.AppCompatActivity r1 = r7.mActivity     // Catch:{ all -> 0x04d6 }
            if (r1 == 0) goto L_0x04b3
            androidx.appcompat.app.AppCompatActivity r1 = r7.mActivity     // Catch:{ all -> 0x04d6 }
            boolean r1 = r1.isDestroyed()     // Catch:{ all -> 0x04d6 }
            if (r1 != 0) goto L_0x04b3
            r1 = 29998(0x752e, float:4.2036E-41)
            if (r0 != r1) goto L_0x002b
            goto L_0x04b3
        L_0x002b:
            r1 = 11
            r11 = 3
            r12 = 4
            if (r0 == r11) goto L_0x00a6
            if (r0 == r12) goto L_0x0086
            if (r0 == r1) goto L_0x0082
            r2 = 12
            if (r0 == r2) goto L_0x0082
            r2 = 15
            if (r0 == r2) goto L_0x0082
            r2 = 951(0x3b7, float:1.333E-42)
            if (r0 == r2) goto L_0x0082
            r2 = 998(0x3e6, float:1.398E-42)
            if (r0 == r2) goto L_0x0070
            r2 = 1001(0x3e9, float:1.403E-42)
            if (r0 == r2) goto L_0x00a6
            r2 = 1007(0x3ef, float:1.411E-42)
            if (r0 == r2) goto L_0x00a6
            r2 = 20
            if (r0 == r2) goto L_0x005e
            r2 = 21
            if (r0 == r2) goto L_0x0082
            r2 = 1003(0x3eb, float:1.406E-42)
            if (r0 == r2) goto L_0x00a6
            r2 = 1004(0x3ec, float:1.407E-42)
            if (r0 == r2) goto L_0x00a6
            goto L_0x00af
        L_0x005e:
            androidx.appcompat.app.AppCompatActivity r2 = r7.mActivity     // Catch:{ all -> 0x04d6 }
            boolean r2 = r2.isInMultiWindowMode()     // Catch:{ all -> 0x04d6 }
            if (r2 != 0) goto L_0x00af
            boolean r2 = r25.isLandscapeMode()     // Catch:{ all -> 0x04d6 }
            if (r2 != 0) goto L_0x00af
            r25.updateMainControlMargin()     // Catch:{ all -> 0x04d6 }
            goto L_0x00af
        L_0x0070:
            java.lang.String r0 = "update hide dialog !!!"
            com.sec.android.app.voicenote.provider.Log.m19d(r9, r0)     // Catch:{ all -> 0x04d6 }
            androidx.appcompat.app.AppCompatActivity r0 = r7.mActivity     // Catch:{ all -> 0x04d6 }
            androidx.fragment.app.FragmentManager r0 = r0.getSupportFragmentManager()     // Catch:{ all -> 0x04d6 }
            com.sec.android.app.voicenote.p007ui.dialog.DialogFactory.clearTopDialog(r0)     // Catch:{ all -> 0x04d6 }
            com.sec.android.app.voicenote.common.util.Trace.endSection()
            return
        L_0x0082:
            r25.updateViewsChange()     // Catch:{ all -> 0x04d6 }
            goto L_0x00af
        L_0x0086:
            com.sec.android.app.voicenote.uicore.VoiceNoteApplication.saveEvent(r8)     // Catch:{ all -> 0x04d6 }
            boolean r2 = r25.isGuideExist()     // Catch:{ all -> 0x04d6 }
            if (r2 == 0) goto L_0x0092
            r25.dismissHelpGuide()     // Catch:{ all -> 0x04d6 }
        L_0x0092:
            androidx.appcompat.app.AppCompatActivity r2 = r7.mActivity     // Catch:{ all -> 0x04d6 }
            androidx.fragment.app.FragmentManager r2 = r2.getSupportFragmentManager()     // Catch:{ all -> 0x04d6 }
            java.lang.String r3 = "ModeNotSupported"
            boolean r3 = com.sec.android.app.voicenote.p007ui.dialog.DialogFactory.isDialogVisible(r2, r3)     // Catch:{ all -> 0x04d6 }
            if (r3 == 0) goto L_0x00af
            java.lang.String r3 = "ModeNotSupported"
            com.sec.android.app.voicenote.p007ui.dialog.DialogFactory.clearDialogByTag(r2, r3)     // Catch:{ all -> 0x04d6 }
            goto L_0x00af
        L_0x00a6:
            boolean r2 = r25.isGuideExist()     // Catch:{ all -> 0x04d6 }
            if (r2 == 0) goto L_0x00af
            r25.dismissHelpGuide()     // Catch:{ all -> 0x04d6 }
        L_0x00af:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x04d6 }
            r2.<init>()     // Catch:{ all -> 0x04d6 }
            java.lang.String r3 = "update - current event : "
            r2.append(r3)     // Catch:{ all -> 0x04d6 }
            int r3 = r7.mCurrentEvent     // Catch:{ all -> 0x04d6 }
            r2.append(r3)     // Catch:{ all -> 0x04d6 }
            java.lang.String r3 = " new event : "
            r2.append(r3)     // Catch:{ all -> 0x04d6 }
            r2.append(r0)     // Catch:{ all -> 0x04d6 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x04d6 }
            com.sec.android.app.voicenote.provider.Log.m26i(r9, r2)     // Catch:{ all -> 0x04d6 }
            android.util.SparseArray<com.sec.android.app.voicenote.uicore.FragmentController$AbsScene> r2 = EVENT_SCENE_TABLE     // Catch:{ all -> 0x04d6 }
            int r3 = r7.mCurrentEvent     // Catch:{ all -> 0x04d6 }
            java.lang.Object r2 = r2.get(r3)     // Catch:{ all -> 0x04d6 }
            com.sec.android.app.voicenote.uicore.FragmentController$AbsScene r2 = (com.sec.android.app.voicenote.uicore.FragmentController.AbsScene) r2     // Catch:{ all -> 0x04d6 }
            int r2 = r2.getScene()     // Catch:{ all -> 0x04d6 }
            r3 = 992(0x3e0, float:1.39E-42)
            r13 = 7
            if (r0 != r3) goto L_0x0104
            if (r2 == r8) goto L_0x0104
            if (r2 == r13) goto L_0x0104
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x04d6 }
            r1.<init>()     // Catch:{ all -> 0x04d6 }
            java.lang.String r3 = "block current scene  : "
            r1.append(r3)     // Catch:{ all -> 0x04d6 }
            r1.append(r2)     // Catch:{ all -> 0x04d6 }
            java.lang.String r2 = " event : "
            r1.append(r2)     // Catch:{ all -> 0x04d6 }
            r1.append(r0)     // Catch:{ all -> 0x04d6 }
            java.lang.String r0 = r1.toString()     // Catch:{ all -> 0x04d6 }
            com.sec.android.app.voicenote.provider.Log.m26i(r9, r0)     // Catch:{ all -> 0x04d6 }
            com.sec.android.app.voicenote.common.util.Trace.endSection()
            return
        L_0x0104:
            if (r2 == r12) goto L_0x0107
            goto L_0x0109
        L_0x0107:
            if (r0 == r12) goto L_0x0493
        L_0x0109:
            android.util.SparseArray<com.sec.android.app.voicenote.uicore.FragmentController$AbsScene> r2 = EVENT_SCENE_TABLE     // Catch:{ all -> 0x04d6 }
            r3 = 0
            java.lang.Object r2 = r2.get(r0, r3)     // Catch:{ all -> 0x04d6 }
            java.lang.String r14 = "Wave"
            java.lang.String r15 = "Info"
            r3 = 2130837516(0x7f02000c, float:1.7279988E38)
            java.lang.String r6 = "Toolbar"
            java.lang.String r4 = "ControlButton"
            r11 = 0
            if (r2 == 0) goto L_0x0414
            android.util.SparseArray<com.sec.android.app.voicenote.uicore.FragmentController$AbsScene> r2 = EVENT_SCENE_TABLE     // Catch:{ all -> 0x04d6 }
            java.lang.Object r2 = r2.get(r0)     // Catch:{ all -> 0x04d6 }
            if (r2 == 0) goto L_0x0414
            android.util.SparseArray<com.sec.android.app.voicenote.uicore.FragmentController$AbsScene> r1 = EVENT_SCENE_TABLE     // Catch:{ all -> 0x04d6 }
            java.lang.Object r1 = r1.get(r0)     // Catch:{ all -> 0x04d6 }
            r16 = r1
            com.sec.android.app.voicenote.uicore.FragmentController$AbsScene r16 = (com.sec.android.app.voicenote.uicore.FragmentController.AbsScene) r16     // Catch:{ all -> 0x04d6 }
            android.util.SparseArray<com.sec.android.app.voicenote.uicore.FragmentController$AbsScene> r1 = EVENT_SCENE_TABLE     // Catch:{ all -> 0x04d6 }
            int r2 = r7.mCurrentEvent     // Catch:{ all -> 0x04d6 }
            java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x04d6 }
            r17 = r1
            com.sec.android.app.voicenote.uicore.FragmentController$AbsScene r17 = (com.sec.android.app.voicenote.uicore.FragmentController.AbsScene) r17     // Catch:{ all -> 0x04d6 }
            r7.mCurrentEvent = r0     // Catch:{ all -> 0x04d6 }
            java.lang.String[] r1 = r17.getTags()     // Catch:{ all -> 0x04d6 }
            r25.reorganizeScene()     // Catch:{ all -> 0x04d6 }
            java.lang.String[] r2 = r16.getTags()     // Catch:{ all -> 0x04d6 }
            java.util.List r18 = r7.getNewFragment(r1, r2)     // Catch:{ all -> 0x04d6 }
            java.util.List r19 = r7.getOldFragment(r1, r2)     // Catch:{ all -> 0x04d6 }
            java.util.List r20 = r7.getReuseFragment(r1, r2)     // Catch:{ all -> 0x04d6 }
            java.util.Iterator r1 = r19.iterator()     // Catch:{ all -> 0x04d6 }
        L_0x0159:
            boolean r2 = r1.hasNext()     // Catch:{ all -> 0x04d6 }
            if (r2 == 0) goto L_0x0254
            java.lang.Object r2 = r1.next()     // Catch:{ all -> 0x04d6 }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ all -> 0x04d6 }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x04d6 }
            r13.<init>()     // Catch:{ all -> 0x04d6 }
            java.lang.String r5 = "removeFragment : "
            r13.append(r5)     // Catch:{ all -> 0x04d6 }
            r13.append(r2)     // Catch:{ all -> 0x04d6 }
            java.lang.String r5 = r13.toString()     // Catch:{ all -> 0x04d6 }
            com.sec.android.app.voicenote.provider.Log.m19d(r9, r5)     // Catch:{ all -> 0x04d6 }
            r5 = -1
            int r13 = r2.hashCode()     // Catch:{ all -> 0x04d6 }
            switch(r13) {
                case 2283726: goto L_0x01c2;
                case 2368702: goto L_0x01b8;
                case 2688793: goto L_0x01b0;
                case 81068824: goto L_0x01a6;
                case 524559195: goto L_0x019e;
                case 1197567695: goto L_0x0196;
                case 1776142171: goto L_0x018c;
                case 2070022486: goto L_0x0182;
                default: goto L_0x0181;
            }     // Catch:{ all -> 0x04d6 }
        L_0x0181:
            goto L_0x01c9
        L_0x0182:
            java.lang.String r13 = "Bookmark"
            boolean r13 = r2.equals(r13)     // Catch:{ all -> 0x04d6 }
            if (r13 == 0) goto L_0x01c9
            r5 = r12
            goto L_0x01c9
        L_0x018c:
            java.lang.String r13 = "IdleControlButton"
            boolean r13 = r2.equals(r13)     // Catch:{ all -> 0x04d6 }
            if (r13 == 0) goto L_0x01c9
            r5 = 6
            goto L_0x01c9
        L_0x0196:
            boolean r13 = r2.equals(r4)     // Catch:{ all -> 0x04d6 }
            if (r13 == 0) goto L_0x01c9
            r5 = 5
            goto L_0x01c9
        L_0x019e:
            boolean r13 = r2.equals(r6)     // Catch:{ all -> 0x04d6 }
            if (r13 == 0) goto L_0x01c9
            r5 = 3
            goto L_0x01c9
        L_0x01a6:
            java.lang.String r13 = "Trash"
            boolean r13 = r2.equals(r13)     // Catch:{ all -> 0x04d6 }
            if (r13 == 0) goto L_0x01c9
            r5 = 7
            goto L_0x01c9
        L_0x01b0:
            boolean r13 = r2.equals(r14)     // Catch:{ all -> 0x04d6 }
            if (r13 == 0) goto L_0x01c9
            r5 = r10
            goto L_0x01c9
        L_0x01b8:
            java.lang.String r13 = "List"
            boolean r13 = r2.equals(r13)     // Catch:{ all -> 0x04d6 }
            if (r13 == 0) goto L_0x01c9
            r5 = r11
            goto L_0x01c9
        L_0x01c2:
            boolean r13 = r2.equals(r15)     // Catch:{ all -> 0x04d6 }
            if (r13 == 0) goto L_0x01c9
            r5 = r8
        L_0x01c9:
            switch(r5) {
                case 0: goto L_0x021f;
                case 1: goto L_0x020e;
                case 2: goto L_0x01fd;
                case 3: goto L_0x01fd;
                case 4: goto L_0x01fd;
                case 5: goto L_0x01ee;
                case 6: goto L_0x01d9;
                case 7: goto L_0x01d1;
                default: goto L_0x01cc;
            }     // Catch:{ all -> 0x04d6 }
        L_0x01cc:
            r7.removeViewOfFragment(r2)     // Catch:{ all -> 0x04d6 }
            goto L_0x024e
        L_0x01d1:
            r5 = 2130837514(0x7f02000a, float:1.7279984E38)
            r7.removeFragment(r2, r11, r5)     // Catch:{ all -> 0x04d6 }
            goto L_0x0251
        L_0x01d9:
            int r5 = r16.getScene()     // Catch:{ all -> 0x04d6 }
            if (r5 != r8) goto L_0x01e6
            r5 = 2130837510(0x7f020006, float:1.7279976E38)
            r7.removeFragment(r2, r11, r5)     // Catch:{ all -> 0x04d6 }
            goto L_0x01e9
        L_0x01e6:
            r7.removeFragment(r2, r11, r11)     // Catch:{ all -> 0x04d6 }
        L_0x01e9:
            r7.removeViewOfFragment(r2)     // Catch:{ all -> 0x04d6 }
            goto L_0x0251
        L_0x01ee:
            r5 = 1010(0x3f2, float:1.415E-42)
            if (r0 == r5) goto L_0x01f6
            r7.updateControlButtonLayout(r11)     // Catch:{ all -> 0x04d6 }
            goto L_0x01f9
        L_0x01f6:
            r7.updateListLayout(r10)     // Catch:{ all -> 0x04d6 }
        L_0x01f9:
            r7.removeFragment(r2, r11, r11)     // Catch:{ all -> 0x04d6 }
            goto L_0x0251
        L_0x01fd:
            r7.removeViewOfFragment(r2)     // Catch:{ all -> 0x04d6 }
            int r5 = r16.getScene()     // Catch:{ all -> 0x04d6 }
            if (r5 != r8) goto L_0x020a
            r7.removeFragment(r2, r11, r3)     // Catch:{ all -> 0x04d6 }
            goto L_0x0251
        L_0x020a:
            r7.removeFragment(r2, r11, r11)     // Catch:{ all -> 0x04d6 }
            goto L_0x0251
        L_0x020e:
            int r5 = r16.getScene()     // Catch:{ all -> 0x04d6 }
            if (r5 != r10) goto L_0x0218
            r7.removeFragment(r2, r11, r3)     // Catch:{ all -> 0x04d6 }
            goto L_0x0251
        L_0x0218:
            r7.removeViewOfFragment(r2)     // Catch:{ all -> 0x04d6 }
            r7.removeFragment(r2, r11, r11)     // Catch:{ all -> 0x04d6 }
            goto L_0x0251
        L_0x021f:
            int r5 = r16.getScene()     // Catch:{ all -> 0x04d6 }
            if (r5 == r10) goto L_0x0247
            if (r5 == r12) goto L_0x0240
            r13 = 13
            if (r5 == r13) goto L_0x0239
            r13 = 7
            if (r5 == r13) goto L_0x0240
            r13 = 8
            if (r5 == r13) goto L_0x0240
            r5 = 2130837522(0x7f020012, float:1.728E38)
            r7.removeFragment(r2, r11, r5)     // Catch:{ all -> 0x04d6 }
            goto L_0x0251
        L_0x0239:
            r5 = 2130837510(0x7f020006, float:1.7279976E38)
            r7.removeFragment(r2, r11, r5)     // Catch:{ all -> 0x04d6 }
            goto L_0x0251
        L_0x0240:
            r7.removeViewOfFragment(r2)     // Catch:{ all -> 0x04d6 }
            r7.removeFragment(r2, r11, r11)     // Catch:{ all -> 0x04d6 }
            goto L_0x0251
        L_0x0247:
            r5 = 2130837514(0x7f02000a, float:1.7279984E38)
            r7.removeFragment(r2, r11, r5)     // Catch:{ all -> 0x04d6 }
            goto L_0x0251
        L_0x024e:
            r7.removeFragment(r2, r11, r11)     // Catch:{ all -> 0x04d6 }
        L_0x0251:
            r13 = 7
            goto L_0x0159
        L_0x0254:
            java.util.Iterator r13 = r18.iterator()     // Catch:{ all -> 0x04d6 }
        L_0x0258:
            boolean r1 = r13.hasNext()     // Catch:{ all -> 0x04d6 }
            if (r1 == 0) goto L_0x03b9
            java.lang.Object r1 = r13.next()     // Catch:{ all -> 0x04d6 }
            r2 = r1
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ all -> 0x04d6 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x04d6 }
            r1.<init>()     // Catch:{ all -> 0x04d6 }
            java.lang.String r3 = "addFragment : "
            r1.append(r3)     // Catch:{ all -> 0x04d6 }
            r1.append(r2)     // Catch:{ all -> 0x04d6 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x04d6 }
            com.sec.android.app.voicenote.provider.Log.m19d(r9, r1)     // Catch:{ all -> 0x04d6 }
            r1 = -1
            int r3 = r2.hashCode()     // Catch:{ all -> 0x04d6 }
            switch(r3) {
                case 2283726: goto L_0x02c2;
                case 2368702: goto L_0x02b8;
                case 2688793: goto L_0x02b0;
                case 81068824: goto L_0x02a6;
                case 524559195: goto L_0x029e;
                case 1197567695: goto L_0x0296;
                case 1776142171: goto L_0x028c;
                case 2070022486: goto L_0x0282;
                default: goto L_0x0281;
            }     // Catch:{ all -> 0x04d6 }
        L_0x0281:
            goto L_0x02c9
        L_0x0282:
            java.lang.String r3 = "Bookmark"
            boolean r3 = r2.equals(r3)     // Catch:{ all -> 0x04d6 }
            if (r3 == 0) goto L_0x02c9
            r1 = r12
            goto L_0x02c9
        L_0x028c:
            java.lang.String r3 = "IdleControlButton"
            boolean r3 = r2.equals(r3)     // Catch:{ all -> 0x04d6 }
            if (r3 == 0) goto L_0x02c9
            r1 = 6
            goto L_0x02c9
        L_0x0296:
            boolean r3 = r2.equals(r4)     // Catch:{ all -> 0x04d6 }
            if (r3 == 0) goto L_0x02c9
            r1 = 5
            goto L_0x02c9
        L_0x029e:
            boolean r3 = r2.equals(r6)     // Catch:{ all -> 0x04d6 }
            if (r3 == 0) goto L_0x02c9
            r1 = 3
            goto L_0x02c9
        L_0x02a6:
            java.lang.String r3 = "Trash"
            boolean r3 = r2.equals(r3)     // Catch:{ all -> 0x04d6 }
            if (r3 == 0) goto L_0x02c9
            r1 = 7
            goto L_0x02c9
        L_0x02b0:
            boolean r3 = r2.equals(r14)     // Catch:{ all -> 0x04d6 }
            if (r3 == 0) goto L_0x02c9
            r1 = r10
            goto L_0x02c9
        L_0x02b8:
            java.lang.String r3 = "List"
            boolean r3 = r2.equals(r3)     // Catch:{ all -> 0x04d6 }
            if (r3 == 0) goto L_0x02c9
            r1 = r11
            goto L_0x02c9
        L_0x02c2:
            boolean r3 = r2.equals(r15)     // Catch:{ all -> 0x04d6 }
            if (r3 == 0) goto L_0x02c9
            r1 = r8
        L_0x02c9:
            switch(r1) {
                case 0: goto L_0x0387;
                case 1: goto L_0x0368;
                case 2: goto L_0x0353;
                case 3: goto L_0x0353;
                case 4: goto L_0x0353;
                case 5: goto L_0x030c;
                case 6: goto L_0x02eb;
                case 7: goto L_0x02da;
                default: goto L_0x02cc;
            }     // Catch:{ all -> 0x04d6 }
        L_0x02cc:
            r23 = r4
            r24 = r6
            r3 = 13
            r10 = 2130837517(0x7f02000d, float:1.727999E38)
            r7.addFragment(r2, r0, r11, r11)     // Catch:{ all -> 0x04d6 }
            goto L_0x03b2
        L_0x02da:
            r1 = 2130837515(0x7f02000b, float:1.7279986E38)
            r7.addFragment(r2, r0, r1, r11)     // Catch:{ all -> 0x04d6 }
            r23 = r4
            r24 = r6
            r3 = 13
        L_0x02e6:
            r10 = 2130837517(0x7f02000d, float:1.727999E38)
            goto L_0x03b2
        L_0x02eb:
            int r1 = r17.getScene()     // Catch:{ all -> 0x04d6 }
            if (r1 == r8) goto L_0x02fe
            int r1 = r17.getScene()     // Catch:{ all -> 0x04d6 }
            r5 = 13
            if (r1 != r5) goto L_0x02fa
            goto L_0x0300
        L_0x02fa:
            r7.addFragment(r2, r0, r11, r11)     // Catch:{ all -> 0x04d6 }
            goto L_0x0306
        L_0x02fe:
            r5 = 13
        L_0x0300:
            r1 = 2130837511(0x7f020007, float:1.7279978E38)
            r7.addFragment(r2, r0, r1, r11)     // Catch:{ all -> 0x04d6 }
        L_0x0306:
            r23 = r4
            r3 = r5
            r24 = r6
            goto L_0x02e6
        L_0x030c:
            r5 = 13
            int r1 = r17.getScene()     // Catch:{ all -> 0x04d6 }
            if (r1 != r8) goto L_0x0334
            int r1 = r16.getScene()     // Catch:{ all -> 0x04d6 }
            if (r1 != r10) goto L_0x0334
            r18 = 0
            r21 = 0
            r22 = 10
            r1 = r25
            r3 = r0
            r23 = r4
            r4 = r18
            r10 = 2130837517(0x7f02000d, float:1.727999E38)
            r5 = r21
            r24 = r6
            r6 = r22
            r1.addFragmentWithDelay(r2, r3, r4, r5, r6)     // Catch:{ all -> 0x04d6 }
            goto L_0x0384
        L_0x0334:
            r23 = r4
            r24 = r6
            r10 = 2130837517(0x7f02000d, float:1.727999E38)
            int r1 = r17.getScene()     // Catch:{ all -> 0x04d6 }
            if (r1 != r8) goto L_0x034b
            int r1 = r16.getScene()     // Catch:{ all -> 0x04d6 }
            if (r1 != r12) goto L_0x034b
            r7.addFragment(r2, r0, r10, r11)     // Catch:{ all -> 0x04d6 }
            goto L_0x0384
        L_0x034b:
            r1 = 1
            r7.updateControlButtonLayout(r1)     // Catch:{ all -> 0x04d6 }
            r7.addFragment(r2, r0, r11, r11)     // Catch:{ all -> 0x04d6 }
            goto L_0x0384
        L_0x0353:
            r23 = r4
            r24 = r6
            r10 = 2130837517(0x7f02000d, float:1.727999E38)
            int r1 = r16.getScene()     // Catch:{ all -> 0x04d6 }
            if (r1 != r12) goto L_0x0364
            r7.addFragment(r2, r0, r10, r11)     // Catch:{ all -> 0x04d6 }
            goto L_0x0384
        L_0x0364:
            r7.addFragment(r2, r0, r11, r11)     // Catch:{ all -> 0x04d6 }
            goto L_0x0384
        L_0x0368:
            r23 = r4
            r24 = r6
            r10 = 2130837517(0x7f02000d, float:1.727999E38)
            int r1 = r17.getScene()     // Catch:{ all -> 0x04d6 }
            r3 = 1
            if (r1 == r3) goto L_0x0381
            int r1 = r17.getScene()     // Catch:{ all -> 0x04d6 }
            if (r1 != r8) goto L_0x037d
            goto L_0x0381
        L_0x037d:
            r7.addFragment(r2, r0, r11, r11)     // Catch:{ all -> 0x04d6 }
            goto L_0x0384
        L_0x0381:
            r7.addFragment(r2, r0, r10, r11)     // Catch:{ all -> 0x04d6 }
        L_0x0384:
            r3 = 13
            goto L_0x03b2
        L_0x0387:
            r23 = r4
            r24 = r6
            r10 = 2130837517(0x7f02000d, float:1.727999E38)
            int r1 = r17.getScene()     // Catch:{ all -> 0x04d6 }
            r3 = 1
            if (r1 == r3) goto L_0x03aa
            r3 = 13
            if (r1 == r3) goto L_0x03a3
            r1 = 2130837520(0x7f020010, float:1.7279996E38)
            r4 = 2130837521(0x7f020011, float:1.7279998E38)
            r7.addFragment(r2, r0, r1, r4)     // Catch:{ all -> 0x04d6 }
            goto L_0x03b2
        L_0x03a3:
            r1 = 2130837511(0x7f020007, float:1.7279978E38)
            r7.addFragment(r2, r0, r1, r11)     // Catch:{ all -> 0x04d6 }
            goto L_0x03b2
        L_0x03aa:
            r3 = 13
            r1 = 2130837515(0x7f02000b, float:1.7279986E38)
            r7.addFragment(r2, r0, r1, r11)     // Catch:{ all -> 0x04d6 }
        L_0x03b2:
            r4 = r23
            r6 = r24
            r10 = 1
            goto L_0x0258
        L_0x03b9:
            r23 = r4
            r3 = 13
            java.util.Iterator r1 = r20.iterator()     // Catch:{ all -> 0x04d6 }
        L_0x03c1:
            boolean r2 = r1.hasNext()     // Catch:{ all -> 0x04d6 }
            if (r2 == 0) goto L_0x03df
            java.lang.Object r2 = r1.next()     // Catch:{ all -> 0x04d6 }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ all -> 0x04d6 }
            r4 = r23
            boolean r5 = r4.equals(r2)     // Catch:{ all -> 0x04d6 }
            if (r5 == 0) goto L_0x03d9
            r5 = 1
            r7.updateControlButtonLayout(r5)     // Catch:{ all -> 0x04d6 }
        L_0x03d9:
            r7.updateFragment(r2, r0)     // Catch:{ all -> 0x04d6 }
            r23 = r4
            goto L_0x03c1
        L_0x03df:
            android.util.SparseArray<com.sec.android.app.voicenote.uicore.FragmentController$AbsScene> r1 = EVENT_SCENE_TABLE     // Catch:{ all -> 0x04d6 }
            java.lang.Object r0 = r1.get(r0)     // Catch:{ all -> 0x04d6 }
            com.sec.android.app.voicenote.uicore.FragmentController$AbsScene r0 = (com.sec.android.app.voicenote.uicore.FragmentController.AbsScene) r0     // Catch:{ all -> 0x04d6 }
            if (r0 == 0) goto L_0x03ef
            int r0 = r0.getScene()     // Catch:{ all -> 0x04d6 }
            r7.mCurrentScene = r0     // Catch:{ all -> 0x04d6 }
        L_0x03ef:
            int r0 = r16.getScene()     // Catch:{ all -> 0x04d6 }
            int r1 = r17.getScene()     // Catch:{ all -> 0x04d6 }
            if (r0 == r1) goto L_0x0489
            int r0 = r16.getScene()     // Catch:{ all -> 0x04d6 }
            if (r0 != r3) goto L_0x0408
            int r0 = r17.getScene()     // Catch:{ all -> 0x04d6 }
            r1 = 1
            if (r0 != r1) goto L_0x0408
            r7.mIsFromMain = r1     // Catch:{ all -> 0x04d6 }
        L_0x0408:
            r25.updateViewsChange()     // Catch:{ all -> 0x04d6 }
            int r0 = r16.getScene()     // Catch:{ all -> 0x04d6 }
            r7.notifyObservers(r0)     // Catch:{ all -> 0x04d6 }
            goto L_0x0489
        L_0x0414:
            r24 = r6
            r10 = 2130837517(0x7f02000d, float:1.727999E38)
            android.util.SparseArray<com.sec.android.app.voicenote.uicore.FragmentController$AbsScene> r2 = EVENT_SCENE_TABLE     // Catch:{ all -> 0x04d6 }
            int r5 = r7.mCurrentEvent     // Catch:{ all -> 0x04d6 }
            java.lang.Object r2 = r2.get(r5)     // Catch:{ all -> 0x04d6 }
            com.sec.android.app.voicenote.uicore.FragmentController$AbsScene r2 = (com.sec.android.app.voicenote.uicore.FragmentController.AbsScene) r2     // Catch:{ all -> 0x04d6 }
            java.lang.String[] r2 = r2.getTags()     // Catch:{ all -> 0x04d6 }
            if (r2 == 0) goto L_0x0441
            int r5 = r2.length     // Catch:{ all -> 0x04d6 }
            r6 = r11
        L_0x042b:
            if (r6 >= r5) goto L_0x0441
            r8 = r2[r6]     // Catch:{ all -> 0x04d6 }
            boolean r9 = r4.equals(r8)     // Catch:{ all -> 0x04d6 }
            if (r9 == 0) goto L_0x043a
            r9 = 1
            r7.updateControlButtonLayout(r9)     // Catch:{ all -> 0x04d6 }
            goto L_0x043b
        L_0x043a:
            r9 = 1
        L_0x043b:
            r7.updateFragment(r8, r0)     // Catch:{ all -> 0x04d6 }
            int r6 = r6 + 1
            goto L_0x042b
        L_0x0441:
            if (r0 == r1) goto L_0x044b
            r1 = 12
            if (r0 == r1) goto L_0x044b
            r1 = 21
            if (r0 != r1) goto L_0x0489
        L_0x044b:
            if (r2 == 0) goto L_0x0489
            int r0 = r2.length     // Catch:{ all -> 0x04d6 }
            r1 = r11
        L_0x044f:
            if (r1 >= r0) goto L_0x0489
            r5 = r2[r1]     // Catch:{ all -> 0x04d6 }
            r6 = r24
            boolean r8 = r6.equals(r5)     // Catch:{ all -> 0x04d6 }
            if (r8 == 0) goto L_0x045e
            r7.refreshFragment(r5, r11, r11)     // Catch:{ all -> 0x04d6 }
        L_0x045e:
            boolean r8 = r4.equals(r5)     // Catch:{ all -> 0x04d6 }
            if (r8 == 0) goto L_0x0467
            r7.refreshFragment(r5, r10, r3)     // Catch:{ all -> 0x04d6 }
        L_0x0467:
            java.lang.String r8 = "Stt"
            boolean r8 = r8.equals(r5)     // Catch:{ all -> 0x04d6 }
            if (r8 == 0) goto L_0x0472
            r7.refreshFragment(r5, r11, r11)     // Catch:{ all -> 0x04d6 }
        L_0x0472:
            boolean r8 = r15.equals(r5)     // Catch:{ all -> 0x04d6 }
            if (r8 == 0) goto L_0x047b
            r7.refreshFragment(r5, r11, r11)     // Catch:{ all -> 0x04d6 }
        L_0x047b:
            boolean r8 = r14.equals(r5)     // Catch:{ all -> 0x04d6 }
            if (r8 == 0) goto L_0x0484
            r7.refreshFragment(r5, r11, r11)     // Catch:{ all -> 0x04d6 }
        L_0x0484:
            int r1 = r1 + 1
            r24 = r6
            goto L_0x044f
        L_0x0489:
            r25.updateViewsChange()     // Catch:{ all -> 0x04d6 }
            r25.removeIfExistSttFragment()     // Catch:{ all -> 0x04d6 }
            com.sec.android.app.voicenote.common.util.Trace.endSection()
            return
        L_0x0493:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x04d6 }
            r1.<init>()     // Catch:{ all -> 0x04d6 }
            java.lang.String r3 = "block current scene : "
            r1.append(r3)     // Catch:{ all -> 0x04d6 }
            r1.append(r2)     // Catch:{ all -> 0x04d6 }
            java.lang.String r2 = " event : "
            r1.append(r2)     // Catch:{ all -> 0x04d6 }
            r1.append(r0)     // Catch:{ all -> 0x04d6 }
            java.lang.String r0 = r1.toString()     // Catch:{ all -> 0x04d6 }
            com.sec.android.app.voicenote.provider.Log.m26i(r9, r0)     // Catch:{ all -> 0x04d6 }
            com.sec.android.app.voicenote.common.util.Trace.endSection()
            return
        L_0x04b3:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x04d6 }
            r1.<init>()     // Catch:{ all -> 0x04d6 }
            java.lang.String r2 = "update just update mCurrentEvent Event : "
            r1.append(r2)     // Catch:{ all -> 0x04d6 }
            r1.append(r0)     // Catch:{ all -> 0x04d6 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x04d6 }
            com.sec.android.app.voicenote.provider.Log.m26i(r9, r1)     // Catch:{ all -> 0x04d6 }
            android.util.SparseArray<com.sec.android.app.voicenote.uicore.FragmentController$AbsScene> r1 = EVENT_SCENE_TABLE     // Catch:{ all -> 0x04d6 }
            r2 = 0
            java.lang.Object r1 = r1.get(r0, r2)     // Catch:{ all -> 0x04d6 }
            if (r1 == 0) goto L_0x04d2
            r7.mCurrentEvent = r0     // Catch:{ all -> 0x04d6 }
        L_0x04d2:
            com.sec.android.app.voicenote.common.util.Trace.endSection()
            return
        L_0x04d6:
            r0 = move-exception
            com.sec.android.app.voicenote.common.util.Trace.endSection()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.uicore.FragmentController.update(java.util.Observable, java.lang.Object):void");
    }

    private void removeIfExistSttFragment() {
        FragmentManager supportFragmentManager;
        int intSettings = Settings.getIntSettings(Settings.KEY_PLAY_MODE, 1);
        if (this.mCurrentScene == 4 && intSettings != 4 && (supportFragmentManager = this.mActivity.getSupportFragmentManager()) != null && supportFragmentManager.findFragmentByTag(STT) != null) {
            supportFragmentManager.beginTransaction().remove(supportFragmentManager.findFragmentByTag(STT)).commitAllowingStateLoss();
        }
    }

    private void updateViewsChange() {
        AppCompatActivity appCompatActivity = this.mActivity;
        if (appCompatActivity == null) {
            Log.m32w(TAG, "updateViewsChange - VNMainActivity is null!!!");
        } else if (DisplayManager.isInMultiWindowMode(appCompatActivity)) {
            if (DisplayManager.isMultiWindowVerticalSplitMode(this.mActivity)) {
                updateMainLayout();
            } else {
                updateMainLayoutMultiWindow(DisplayManager.getMultiwindowMode(), DisplayManager.getVROrientation());
            }
        } else if (DisplayManager.isCurrentWindowOnLandscape(this.mActivity)) {
            updateMainLayoutLandScape();
        } else {
            updateMainLayout();
        }
    }

    public void onDestroy() {
        Log.m26i(TAG, "onDestroy ");
        AppCompatActivity appCompatActivity = this.mActivity;
        if (appCompatActivity != null && !appCompatActivity.isChangingConfigurations()) {
            FragmentFactory.removeAll();
        }
        int i = this.mCurrentScene;
        if (i == 2 || i == 5) {
            removeFragment(LIST, 0, 0);
        }
        if (this.mCurrentScene == 4 && FragmentFactory.get(STT) != null) {
            removeFragment(STT, 0, 0);
        }
        this.mActivity = null;
        unregisterAllSceneChangeListener();
    }

    private static class FragmentFactory {
        private static final String TAG = "FragmentFactory";
        private static final HashMap<String, AbsFragment> mMap = new HashMap<>();

        private FragmentFactory() {
        }

        public static AbsFragment get(String str) {
            return mMap.get(str);
        }

        private static void put(String str, AbsFragment absFragment) {
            if (absFragment != null) {
                mMap.put(str, absFragment);
            }
        }

        public static void remove(String str) {
            mMap.remove(str);
        }

        public static void removeAll() {
            mMap.clear();
        }

        public static AbsFragment create(String str) {
            if (get(str) == null) {
                return createFragment(str);
            }
            return get(str);
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private static com.sec.android.app.voicenote.p007ui.AbsFragment createFragment(java.lang.String r2) {
            /*
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r1 = "createFragment : "
                r0.append(r1)
                r0.append(r2)
                java.lang.String r0 = r0.toString()
                java.lang.String r1 = "FragmentFactory"
                com.sec.android.app.voicenote.provider.Log.m26i(r1, r0)
                int r0 = r2.hashCode()
                switch(r0) {
                    case -1822469688: goto L_0x007a;
                    case 83475: goto L_0x0070;
                    case 2283726: goto L_0x0066;
                    case 2368702: goto L_0x005c;
                    case 2688793: goto L_0x0052;
                    case 81068824: goto L_0x0047;
                    case 524559195: goto L_0x003d;
                    case 1197567695: goto L_0x0033;
                    case 1776142171: goto L_0x0029;
                    case 2070022486: goto L_0x001f;
                    default: goto L_0x001d;
                }
            L_0x001d:
                goto L_0x0085
            L_0x001f:
                java.lang.String r0 = "Bookmark"
                boolean r0 = r2.equals(r0)
                if (r0 == 0) goto L_0x0085
                r0 = 2
                goto L_0x0086
            L_0x0029:
                java.lang.String r0 = "IdleControlButton"
                boolean r0 = r2.equals(r0)
                if (r0 == 0) goto L_0x0085
                r0 = 7
                goto L_0x0086
            L_0x0033:
                java.lang.String r0 = "ControlButton"
                boolean r0 = r2.equals(r0)
                if (r0 == 0) goto L_0x0085
                r0 = 4
                goto L_0x0086
            L_0x003d:
                java.lang.String r0 = "Toolbar"
                boolean r0 = r2.equals(r0)
                if (r0 == 0) goto L_0x0085
                r0 = 6
                goto L_0x0086
            L_0x0047:
                java.lang.String r0 = "Trash"
                boolean r0 = r2.equals(r0)
                if (r0 == 0) goto L_0x0085
                r0 = 9
                goto L_0x0086
            L_0x0052:
                java.lang.String r0 = "Wave"
                boolean r0 = r2.equals(r0)
                if (r0 == 0) goto L_0x0085
                r0 = 1
                goto L_0x0086
            L_0x005c:
                java.lang.String r0 = "List"
                boolean r0 = r2.equals(r0)
                if (r0 == 0) goto L_0x0085
                r0 = 5
                goto L_0x0086
            L_0x0066:
                java.lang.String r0 = "Info"
                boolean r0 = r2.equals(r0)
                if (r0 == 0) goto L_0x0085
                r0 = 0
                goto L_0x0086
            L_0x0070:
                java.lang.String r0 = "Stt"
                boolean r0 = r2.equals(r0)
                if (r0 == 0) goto L_0x0085
                r0 = 3
                goto L_0x0086
            L_0x007a:
                java.lang.String r0 = "Search"
                boolean r0 = r2.equals(r0)
                if (r0 == 0) goto L_0x0085
                r0 = 8
                goto L_0x0086
            L_0x0085:
                r0 = -1
            L_0x0086:
                switch(r0) {
                    case 0: goto L_0x00c1;
                    case 1: goto L_0x00bb;
                    case 2: goto L_0x00b5;
                    case 3: goto L_0x00af;
                    case 4: goto L_0x00a9;
                    case 5: goto L_0x00a3;
                    case 6: goto L_0x009d;
                    case 7: goto L_0x0097;
                    case 8: goto L_0x0091;
                    case 9: goto L_0x008b;
                    default: goto L_0x0089;
                }
            L_0x0089:
                r0 = 0
                goto L_0x00c6
            L_0x008b:
                com.sec.android.app.voicenote.ui.TrashFragment r0 = new com.sec.android.app.voicenote.ui.TrashFragment
                r0.<init>()
                goto L_0x00c6
            L_0x0091:
                com.sec.android.app.voicenote.ui.SearchFragment r0 = new com.sec.android.app.voicenote.ui.SearchFragment
                r0.<init>()
                goto L_0x00c6
            L_0x0097:
                com.sec.android.app.voicenote.ui.IdleControlButtonFragment r0 = new com.sec.android.app.voicenote.ui.IdleControlButtonFragment
                r0.<init>()
                goto L_0x00c6
            L_0x009d:
                com.sec.android.app.voicenote.ui.ToolbarFragment r0 = new com.sec.android.app.voicenote.ui.ToolbarFragment
                r0.<init>()
                goto L_0x00c6
            L_0x00a3:
                com.sec.android.app.voicenote.ui.TabListFragment r0 = new com.sec.android.app.voicenote.ui.TabListFragment
                r0.<init>()
                goto L_0x00c6
            L_0x00a9:
                com.sec.android.app.voicenote.ui.ControlButtonFragment r0 = new com.sec.android.app.voicenote.ui.ControlButtonFragment
                r0.<init>()
                goto L_0x00c6
            L_0x00af:
                com.sec.android.app.voicenote.ui.SttFragment r0 = new com.sec.android.app.voicenote.ui.SttFragment
                r0.<init>()
                goto L_0x00c6
            L_0x00b5:
                com.sec.android.app.voicenote.ui.BookmarkFragment r0 = new com.sec.android.app.voicenote.ui.BookmarkFragment
                r0.<init>()
                goto L_0x00c6
            L_0x00bb:
                com.sec.android.app.voicenote.ui.WaveFragment r0 = new com.sec.android.app.voicenote.ui.WaveFragment
                r0.<init>()
                goto L_0x00c6
            L_0x00c1:
                com.sec.android.app.voicenote.ui.InfoFragment r0 = new com.sec.android.app.voicenote.ui.InfoFragment
                r0.<init>()
            L_0x00c6:
                if (r0 == 0) goto L_0x00cb
                put(r2, r0)
            L_0x00cb:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.uicore.FragmentController.FragmentFactory.createFragment(java.lang.String):com.sec.android.app.voicenote.ui.AbsFragment");
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
            AbsScene absScene = EVENT_SCENE_TABLE.get(this.mCurrentEvent);
            if (absScene != null) {
                onSceneChangeListener.onSceneChange(absScene.getScene());
            }
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
        Log.m26i(TAG, "notifyObservers scene : " + i);
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
        Log.m19d(TAG, "reorganizeScene - scene: " + VoiceNoteApplication.getScene());
        if (VoiceNoteApplication.getScene() != 1) {
            int intSettings = Settings.getIntSettings("record_mode", 1);
            if (intSettings != 4) {
                RECORD_SCENE.removeTag(STT);
            } else if (!RECORD_SCENE.contains(STT)) {
                RECORD_SCENE.addTag(STT);
            }
            if (intSettings == 6) {
                if (RECORD_SCENE.contains(BOOKMARK)) {
                    RECORD_SCENE.removeTag(BOOKMARK);
                }
            } else if (!RECORD_SCENE.contains(BOOKMARK)) {
                RECORD_SCENE.addTag(BOOKMARK);
            }
            if (Settings.getIntSettings(Settings.KEY_PLAY_MODE, 1) == 4) {
                if (!PLAY_SCENE.contains(STT)) {
                    PLAY_SCENE.addTag(STT);
                }
                if (!EDIT_SCENE.contains(STT)) {
                    EDIT_SCENE.addTag(STT);
                    return;
                }
                return;
            }
            PLAY_SCENE.removeTag(STT);
            EDIT_SCENE.removeTag(STT);
            if (!STT_TRANSLATION_SCENE.contains(STT)) {
                STT_TRANSLATION_SCENE.addTag(STT);
            }
        }
    }

    public void updateMainControlMargin() {
        int recordButtonMarginBottom = getRecordButtonMarginBottom();
        RelativeLayout relativeLayout = (RelativeLayout) this.mActivity.getWindow().getDecorView().findViewById(C0690R.C0693id.main_control_button_layout);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) relativeLayout.getLayoutParams();
        layoutParams.bottomMargin = recordButtonMarginBottom;
        relativeLayout.setLayoutParams(layoutParams);
    }

    public void updateToolbarLayoutLandscapeMulti() {
        Log.m19d(TAG, "updateToolbarLayoutLandscapeMulti");
        FrameLayout frameLayout = (FrameLayout) this.mActivity.getWindow().getDecorView().findViewById(C0690R.C0693id.toolbar_skip_silence_icon);
        FrameLayout frameLayout2 = (FrameLayout) this.mActivity.getWindow().getDecorView().findViewById(C0690R.C0693id.toolbar_repeat_icon);
        FrameLayout frameLayout3 = (FrameLayout) this.mActivity.getWindow().getDecorView().findViewById(C0690R.C0693id.toolbar_speed_icon);
        if (frameLayout != null && frameLayout2 != null && frameLayout3 != null) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
            layoutParams.weight = 1.0f;
            frameLayout.setLayoutParams(layoutParams);
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) frameLayout2.getLayoutParams();
            layoutParams2.weight = 1.0f;
            frameLayout2.setLayoutParams(layoutParams2);
            LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) frameLayout3.getLayoutParams();
            layoutParams3.weight = 1.0f;
            frameLayout3.setLayoutParams(layoutParams3);
        }
    }

    private int getRecordButtonMarginBottom() {
        if (!DisplayManager.isInMultiWindowMode(this.mActivity)) {
            int dimensionPixelSize = this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.main_controlbutton_margin_bottom);
            if (isLandscapeMode() && NavigationBarProvider.getInstance().isNavigationBarEnabled() && NavigationBarProvider.getInstance().isFullScreenGesture()) {
                return dimensionPixelSize - NavigationBarProvider.getInstance().getNavigationGestureHeight(this.mActivity);
            }
            if (this.mActivity.isInMultiWindowMode() || isLandscapeMode() || !NavigationBarProvider.getInstance().isNavigationBarEnabled() || HWKeyboardProvider.isHWKeyboard(this.mActivity) || !NavigationBarProvider.getInstance().isFullScreenGesture()) {
                return dimensionPixelSize;
            }
            return dimensionPixelSize + (NavigationBarProvider.getInstance().getNavigationNormalHeight() - NavigationBarProvider.getInstance().getNavigationGestureHeight(this.mActivity));
        } else if (VoiceNoteFeature.FLAG_IS_TABLET) {
            return this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.main_controlbutton_multi_window_margin_bottom_tablet);
        } else {
            return this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.main_controlbutton_multi_window_margin_bottom);
        }
    }

    private void updateIdleControlButtonMarginBottom() {
        FrameLayout frameLayout = (FrameLayout) this.mActivity.getWindow().getDecorView().findViewById(C0690R.C0693id.main_idle_controlbutton);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) frameLayout.getLayoutParams();
        layoutParams.bottomMargin = getRecordButtonMarginBottom();
        frameLayout.setLayoutParams(layoutParams);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:75:0x01e5, code lost:
        if (r10 != r3) goto L_0x01e7;
     */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0311 A[Catch:{ all -> 0x038d }] */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x033e A[Catch:{ all -> 0x038d }] */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0362 A[Catch:{ all -> 0x038d }] */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x023a A[Catch:{ all -> 0x038d }] */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0257 A[Catch:{ all -> 0x038d }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateMainLayout() {
        /*
            r22 = this;
            r8 = r22
            java.lang.String r0 = "FrgmController.updateMainLayout"
            com.sec.android.app.voicenote.common.util.Trace.beginSection(r0)     // Catch:{ all -> 0x038d }
            int r0 = r8.mCurrentScene     // Catch:{ all -> 0x038d }
            r1 = 1
            r2 = 12
            r3 = 8
            r4 = 6
            r5 = -1
            r6 = 4
            java.lang.String r9 = "FragmentController"
            if (r0 == r6) goto L_0x004f
            int r0 = r8.mCurrentScene     // Catch:{ all -> 0x038d }
            if (r0 == r3) goto L_0x004f
            int r0 = r8.mCurrentScene     // Catch:{ all -> 0x038d }
            if (r0 == r4) goto L_0x004f
            int r0 = r8.mCurrentScene     // Catch:{ all -> 0x038d }
            r7 = 11
            if (r0 == r7) goto L_0x004f
            int r0 = r8.mCurrentScene     // Catch:{ all -> 0x038d }
            r7 = 13
            if (r0 == r7) goto L_0x004f
            int r0 = r8.mCurrentScene     // Catch:{ all -> 0x038d }
            if (r0 == r2) goto L_0x004f
            int r0 = r8.mCurrentEvent     // Catch:{ all -> 0x038d }
            r7 = 2005(0x7d5, float:2.81E-42)
            if (r0 == r7) goto L_0x004f
            int r0 = r8.mCurrentEvent     // Catch:{ all -> 0x038d }
            r7 = 2006(0x7d6, float:2.811E-42)
            if (r0 == r7) goto L_0x004f
            java.lang.String r0 = "updateMainLayout - do not update"
            com.sec.android.app.voicenote.provider.Log.m19d(r9, r0)     // Catch:{ all -> 0x038d }
            r8.mOldPlayMode = r5     // Catch:{ all -> 0x038d }
            int r0 = r8.mCurrentScene     // Catch:{ all -> 0x038d }
            r8.mPreviouScene = r0     // Catch:{ all -> 0x038d }
            int r0 = r8.mCurrentScene     // Catch:{ all -> 0x038d }
            if (r0 != r1) goto L_0x004b
            r22.updateIdleControlButtonMarginBottom()     // Catch:{ all -> 0x038d }
        L_0x004b:
            com.sec.android.app.voicenote.common.util.Trace.endSection()
            return
        L_0x004f:
            java.lang.String r0 = "record_mode"
            int r0 = com.sec.android.app.voicenote.provider.Settings.getIntSettings(r0, r5)     // Catch:{ all -> 0x038d }
            java.lang.String r7 = "play_mode"
            int r10 = com.sec.android.app.voicenote.provider.Settings.getIntSettings(r7, r5)     // Catch:{ all -> 0x038d }
            int r5 = r8.mCurrentScene     // Catch:{ all -> 0x038d }
            int r7 = r8.mPreviouScene     // Catch:{ all -> 0x038d }
            if (r5 != r7) goto L_0x0081
            boolean r5 = r8.isNeedUpdateLayout     // Catch:{ all -> 0x038d }
            if (r5 != 0) goto L_0x0081
            int r5 = r8.mOldPlayMode     // Catch:{ all -> 0x038d }
            if (r5 != r10) goto L_0x0081
            java.lang.String r0 = "updateMainLayout - Don't need update layout"
            com.sec.android.app.voicenote.provider.Log.m19d(r9, r0)     // Catch:{ all -> 0x038d }
            int r0 = r8.mCurrentEvent     // Catch:{ all -> 0x038d }
            r1 = 7001(0x1b59, float:9.81E-42)
            if (r0 == r1) goto L_0x007a
            int r0 = r8.mCurrentEvent     // Catch:{ all -> 0x038d }
            r1 = 17
            if (r0 != r1) goto L_0x007d
        L_0x007a:
            r22.updateMainControlMargin()     // Catch:{ all -> 0x038d }
        L_0x007d:
            com.sec.android.app.voicenote.common.util.Trace.endSection()
            return
        L_0x0081:
            androidx.appcompat.app.AppCompatActivity r5 = r8.mActivity     // Catch:{ all -> 0x038d }
            boolean r5 = com.sec.android.app.voicenote.provider.HWKeyboardProvider.isHWKeyboard(r5)     // Catch:{ all -> 0x038d }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x038d }
            r7.<init>()     // Catch:{ all -> 0x038d }
            java.lang.String r11 = "updateMainLayout - recordMode: "
            r7.append(r11)     // Catch:{ all -> 0x038d }
            r7.append(r0)     // Catch:{ all -> 0x038d }
            java.lang.String r11 = " - playMode: "
            r7.append(r11)     // Catch:{ all -> 0x038d }
            r7.append(r10)     // Catch:{ all -> 0x038d }
            java.lang.String r11 = " - mOldPlayMode: "
            r7.append(r11)     // Catch:{ all -> 0x038d }
            int r11 = r8.mOldPlayMode     // Catch:{ all -> 0x038d }
            r7.append(r11)     // Catch:{ all -> 0x038d }
            java.lang.String r11 = " - scene: "
            r7.append(r11)     // Catch:{ all -> 0x038d }
            int r11 = r8.mCurrentScene     // Catch:{ all -> 0x038d }
            r7.append(r11)     // Catch:{ all -> 0x038d }
            java.lang.String r11 = "- "
            r7.append(r11)     // Catch:{ all -> 0x038d }
            int r11 = r8.mPreviouScene     // Catch:{ all -> 0x038d }
            r7.append(r11)     // Catch:{ all -> 0x038d }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x038d }
            com.sec.android.app.voicenote.provider.Log.m19d(r9, r7)     // Catch:{ all -> 0x038d }
            androidx.appcompat.app.AppCompatActivity r7 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.content.res.Resources r7 = r7.getResources()     // Catch:{ all -> 0x038d }
            android.util.DisplayMetrics r7 = r7.getDisplayMetrics()     // Catch:{ all -> 0x038d }
            int r7 = r7.heightPixels     // Catch:{ all -> 0x038d }
            androidx.appcompat.app.AppCompatActivity r11 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.content.res.Resources r11 = r11.getResources()     // Catch:{ all -> 0x038d }
            android.util.DisplayMetrics r11 = r11.getDisplayMetrics()     // Catch:{ all -> 0x038d }
            int r11 = r11.widthPixels     // Catch:{ all -> 0x038d }
            int r12 = r22.getStatusBarHeight()     // Catch:{ all -> 0x038d }
            int r7 = r7 - r12
            int r12 = r22.getActionBarHeight()     // Catch:{ all -> 0x038d }
            int r7 = r7 - r12
            androidx.appcompat.app.AppCompatActivity r12 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.content.res.Resources r12 = r12.getResources()     // Catch:{ all -> 0x038d }
            r13 = 2131165419(0x7f0700eb, float:1.7945055E38)
            int r12 = r12.getDimensionPixelSize(r13)     // Catch:{ all -> 0x038d }
            androidx.appcompat.app.AppCompatActivity r13 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.content.res.Resources r13 = r13.getResources()     // Catch:{ all -> 0x038d }
            r14 = 2131165416(0x7f0700e8, float:1.7945048E38)
            int r13 = r13.getDimensionPixelSize(r14)     // Catch:{ all -> 0x038d }
            int r12 = r12 + r13
            r22.updateMainControlMargin()     // Catch:{ all -> 0x038d }
            androidx.appcompat.app.AppCompatActivity r13 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.content.res.Resources r13 = r13.getResources()     // Catch:{ all -> 0x038d }
            r14 = 2131165279(0x7f07005f, float:1.794477E38)
            int r13 = r13.getDimensionPixelSize(r14)     // Catch:{ all -> 0x038d }
            androidx.appcompat.app.AppCompatActivity r14 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.content.res.Resources r14 = r14.getResources()     // Catch:{ all -> 0x038d }
            r15 = 2131165418(0x7f0700ea, float:1.7945053E38)
            int r14 = r14.getDimensionPixelSize(r15)     // Catch:{ all -> 0x038d }
            boolean r15 = com.sec.android.app.voicenote.provider.VoiceNoteFeature.FLAG_IS_TABLET     // Catch:{ all -> 0x038d }
            if (r15 == 0) goto L_0x0128
            int r15 = r13 * 3
            int r11 = r11 - r15
            int r15 = r14 * 2
            int r11 = r11 - r15
            int r11 = r11 / r6
            int r11 = r11 + r14
            goto L_0x0135
        L_0x0128:
            androidx.appcompat.app.AppCompatActivity r11 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.content.res.Resources r11 = r11.getResources()     // Catch:{ all -> 0x038d }
            r14 = 2131165544(0x7f070168, float:1.7945308E38)
            int r11 = r11.getDimensionPixelSize(r14)     // Catch:{ all -> 0x038d }
        L_0x0135:
            int r14 = r8.mCurrentScene     // Catch:{ all -> 0x038d }
            r15 = 2131165437(0x7f0700fd, float:1.7945091E38)
            if (r14 == r6) goto L_0x0140
            int r14 = r8.mCurrentScene     // Catch:{ all -> 0x038d }
            if (r14 != r4) goto L_0x014b
        L_0x0140:
            androidx.appcompat.app.AppCompatActivity r14 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.content.res.Resources r14 = r14.getResources()     // Catch:{ all -> 0x038d }
            int r14 = r14.getDimensionPixelSize(r15)     // Catch:{ all -> 0x038d }
            int r12 = r12 + r14
        L_0x014b:
            if (r7 > 0) goto L_0x0156
            java.lang.String r0 = "updateMainLayout - mainViewHeight = 0 "
            com.sec.android.app.voicenote.provider.Log.m19d(r9, r0)     // Catch:{ all -> 0x038d }
            com.sec.android.app.voicenote.common.util.Trace.endSection()
            return
        L_0x0156:
            int r14 = r7 * 20
            int r14 = r14 / 100
            androidx.appcompat.app.AppCompatActivity r1 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.content.res.Resources r1 = r1.getResources()     // Catch:{ all -> 0x038d }
            r2 = 2131165969(0x7f070311, float:1.794617E38)
            int r1 = r1.getDimensionPixelSize(r2)     // Catch:{ all -> 0x038d }
            int r14 = r14 - r1
            if (r5 == 0) goto L_0x016d
            r16 = 1
            goto L_0x0170
        L_0x016d:
            r1 = 2
            r16 = r1
        L_0x0170:
            int r16 = r16 * r7
            int r1 = r16 / 100
            androidx.appcompat.app.AppCompatActivity r5 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.content.res.Resources r5 = r5.getResources()     // Catch:{ all -> 0x038d }
            r2 = 2131165236(0x7f070034, float:1.7944683E38)
            int r2 = r5.getDimensionPixelSize(r2)     // Catch:{ all -> 0x038d }
            androidx.appcompat.app.AppCompatActivity r5 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.content.res.Resources r5 = r5.getResources()     // Catch:{ all -> 0x038d }
            r4 = 2131165540(0x7f070164, float:1.79453E38)
            int r4 = r5.getDimensionPixelSize(r4)     // Catch:{ all -> 0x038d }
            int r5 = r2 + r4
            androidx.appcompat.app.AppCompatActivity r6 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.content.res.Resources r6 = r6.getResources()     // Catch:{ all -> 0x038d }
            int r6 = r6.getDimensionPixelSize(r15)     // Catch:{ all -> 0x038d }
            androidx.appcompat.app.AppCompatActivity r15 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.content.res.Resources r15 = r15.getResources()     // Catch:{ all -> 0x038d }
            r3 = 2131165438(0x7f0700fe, float:1.7945093E38)
            int r3 = r15.getDimensionPixelSize(r3)     // Catch:{ all -> 0x038d }
            androidx.appcompat.app.AppCompatActivity r15 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.content.res.Resources r15 = r15.getResources()     // Catch:{ all -> 0x038d }
            r20 = r3
            r3 = 2131165439(0x7f0700ff, float:1.7945095E38)
            int r15 = r15.getDimensionPixelSize(r3)     // Catch:{ all -> 0x038d }
            boolean r3 = com.sec.android.app.voicenote.provider.VoiceNoteFeature.FLAG_IS_TABLET     // Catch:{ all -> 0x038d }
            if (r3 == 0) goto L_0x01ce
            androidx.appcompat.app.AppCompatActivity r3 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.content.res.Resources r3 = r3.getResources()     // Catch:{ all -> 0x038d }
            r21 = r13
            r13 = 2131165554(0x7f070172, float:1.7945328E38)
            int r3 = r3.getDimensionPixelSize(r13)     // Catch:{ all -> 0x038d }
            int r3 = r11 - r3
            r20 = r3
            goto L_0x01d0
        L_0x01ce:
            r21 = r13
        L_0x01d0:
            int r3 = r8.mCurrentScene     // Catch:{ all -> 0x038d }
            r13 = 8
            if (r3 != r13) goto L_0x01da
            r3 = 4
            if (r0 == r3) goto L_0x01f1
            goto L_0x01db
        L_0x01da:
            r3 = 4
        L_0x01db:
            int r13 = r8.mCurrentScene     // Catch:{ all -> 0x038d }
            if (r13 == r3) goto L_0x01e5
            int r13 = r8.mCurrentScene     // Catch:{ all -> 0x038d }
            r3 = 6
            if (r13 != r3) goto L_0x01e7
            r3 = 4
        L_0x01e5:
            if (r10 == r3) goto L_0x01f1
        L_0x01e7:
            int r3 = r8.mCurrentScene     // Catch:{ all -> 0x038d }
            r13 = 12
            if (r3 != r13) goto L_0x01ee
            goto L_0x01f1
        L_0x01ee:
            r3 = 1108082688(0x420c0000, float:35.0)
            goto L_0x01f3
        L_0x01f1:
            r3 = 1101791232(0x41ac0000, float:21.5)
        L_0x01f3:
            float r13 = (float) r7     // Catch:{ all -> 0x038d }
            float r3 = r3 * r13
            r13 = 1120403456(0x42c80000, float:100.0)
            float r3 = r3 / r13
            int r3 = (int) r3     // Catch:{ all -> 0x038d }
            com.sec.android.app.voicenote.provider.WaveProvider r13 = com.sec.android.app.voicenote.provider.WaveProvider.getInstance()     // Catch:{ all -> 0x038d }
            r17 = r11
            androidx.appcompat.app.AppCompatActivity r11 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.content.res.Resources r11 = r11.getResources()     // Catch:{ all -> 0x038d }
            r18 = r15
            r15 = 2131165989(0x7f070325, float:1.794621E38)
            int r11 = r11.getDimensionPixelSize(r15)     // Catch:{ all -> 0x038d }
            int r11 = r3 - r11
            androidx.appcompat.app.AppCompatActivity r15 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.content.res.Resources r15 = r15.getResources()     // Catch:{ all -> 0x038d }
            r19 = r6
            r6 = 2131165969(0x7f070311, float:1.794617E38)
            int r6 = r15.getDimensionPixelSize(r6)     // Catch:{ all -> 0x038d }
            int r11 = r11 - r6
            r6 = 0
            r13.setWaveHeight(r3, r11, r6)     // Catch:{ all -> 0x038d }
            androidx.appcompat.app.AppCompatActivity r11 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.content.res.Resources r11 = r11.getResources()     // Catch:{ all -> 0x038d }
            r13 = 2131165967(0x7f07030f, float:1.7946166E38)
            int r11 = r11.getDimensionPixelSize(r13)     // Catch:{ all -> 0x038d }
            int r3 = r3 + r11
            int r11 = r8.mCurrentScene     // Catch:{ all -> 0x038d }
            boolean r0 = r8.isSttViewLayoutVisible(r11, r10, r0)     // Catch:{ all -> 0x038d }
            if (r0 == 0) goto L_0x0257
            androidx.appcompat.app.AppCompatActivity r0 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ all -> 0x038d }
            r6 = 2131165548(0x7f07016c, float:1.7945316E38)
            int r6 = r0.getDimensionPixelSize(r6)     // Catch:{ all -> 0x038d }
            int r7 = r7 - r14
            int r7 = r7 - r1
            int r7 = r7 - r5
            int r7 = r7 - r3
            int r7 = r7 - r6
            int r7 = r7 - r12
            int r0 = r22.getRecordButtonMarginBottom()     // Catch:{ all -> 0x038d }
            int r7 = r7 - r0
            int r0 = r8.calculateSttActualHeight(r7)     // Catch:{ all -> 0x038d }
            goto L_0x0258
        L_0x0257:
            r0 = r6
        L_0x0258:
            androidx.appcompat.app.AppCompatActivity r5 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.view.Window r5 = r5.getWindow()     // Catch:{ all -> 0x038d }
            android.view.View r5 = r5.getDecorView()     // Catch:{ all -> 0x038d }
            r7 = 2131296601(0x7f090159, float:1.8211123E38)
            android.view.View r5 = r5.findViewById(r7)     // Catch:{ all -> 0x038d }
            android.widget.FrameLayout r5 = (android.widget.FrameLayout) r5     // Catch:{ all -> 0x038d }
            int r7 = r8.mOldPlayMode     // Catch:{ all -> 0x038d }
            if (r7 != r10) goto L_0x0275
            int r7 = r5.getHeight()     // Catch:{ all -> 0x038d }
            if (r7 == r14) goto L_0x028c
        L_0x0275:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x038d }
            r7.<init>()     // Catch:{ all -> 0x038d }
            java.lang.String r11 = "updateMainLayout - infoViewActualHeight "
            r7.append(r11)     // Catch:{ all -> 0x038d }
            r7.append(r14)     // Catch:{ all -> 0x038d }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x038d }
            com.sec.android.app.voicenote.provider.Log.m19d(r9, r7)     // Catch:{ all -> 0x038d }
            r8.updateViewHeight(r5, r14)     // Catch:{ all -> 0x038d }
        L_0x028c:
            androidx.appcompat.app.AppCompatActivity r5 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.view.Window r5 = r5.getWindow()     // Catch:{ all -> 0x038d }
            android.view.View r5 = r5.getDecorView()     // Catch:{ all -> 0x038d }
            r7 = 2131296610(0x7f090162, float:1.8211142E38)
            android.view.View r5 = r5.findViewById(r7)     // Catch:{ all -> 0x038d }
            android.widget.FrameLayout r5 = (android.widget.FrameLayout) r5     // Catch:{ all -> 0x038d }
            int r7 = r8.mOldPlayMode     // Catch:{ all -> 0x038d }
            if (r7 != r10) goto L_0x02a9
            int r7 = r5.getHeight()     // Catch:{ all -> 0x038d }
            if (r7 == r3) goto L_0x02c0
        L_0x02a9:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x038d }
            r7.<init>()     // Catch:{ all -> 0x038d }
            java.lang.String r11 = "updateMainLayout - waveViewActualHeight "
            r7.append(r11)     // Catch:{ all -> 0x038d }
            r7.append(r3)     // Catch:{ all -> 0x038d }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x038d }
            com.sec.android.app.voicenote.provider.Log.m19d(r9, r7)     // Catch:{ all -> 0x038d }
            r8.updateViewHeight(r5, r3)     // Catch:{ all -> 0x038d }
        L_0x02c0:
            androidx.appcompat.app.AppCompatActivity r3 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.view.Window r3 = r3.getWindow()     // Catch:{ all -> 0x038d }
            android.view.View r3 = r3.getDecorView()     // Catch:{ all -> 0x038d }
            r5 = 2131296595(0x7f090153, float:1.8211111E38)
            android.view.View r3 = r3.findViewById(r5)     // Catch:{ all -> 0x038d }
            android.widget.FrameLayout r3 = (android.widget.FrameLayout) r3     // Catch:{ all -> 0x038d }
            int r5 = r8.mOldPlayMode     // Catch:{ all -> 0x038d }
            if (r5 != r10) goto L_0x02dd
            int r5 = r3.getHeight()     // Catch:{ all -> 0x038d }
            if (r5 == r2) goto L_0x02fc
        L_0x02dd:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x038d }
            r5.<init>()     // Catch:{ all -> 0x038d }
            java.lang.String r7 = "updateMainLayout - bookmarkViewActualHeight "
            r5.append(r7)     // Catch:{ all -> 0x038d }
            r5.append(r2)     // Catch:{ all -> 0x038d }
            java.lang.String r7 = " - spaceViewActualHeight : "
            r5.append(r7)     // Catch:{ all -> 0x038d }
            r5.append(r1)     // Catch:{ all -> 0x038d }
            java.lang.String r1 = r5.toString()     // Catch:{ all -> 0x038d }
            com.sec.android.app.voicenote.provider.Log.m19d(r9, r1)     // Catch:{ all -> 0x038d }
            r8.updateViewHeight(r3, r2, r4)     // Catch:{ all -> 0x038d }
        L_0x02fc:
            androidx.appcompat.app.AppCompatActivity r1 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.view.Window r1 = r1.getWindow()     // Catch:{ all -> 0x038d }
            android.view.View r1 = r1.getDecorView()     // Catch:{ all -> 0x038d }
            r2 = 2131296604(0x7f09015c, float:1.821113E38)
            android.view.View r1 = r1.findViewById(r2)     // Catch:{ all -> 0x038d }
            android.widget.FrameLayout r1 = (android.widget.FrameLayout) r1     // Catch:{ all -> 0x038d }
            if (r1 == 0) goto L_0x0328
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x038d }
            r2.<init>()     // Catch:{ all -> 0x038d }
            java.lang.String r3 = "updateMainLayout - sttViewActualHeight "
            r2.append(r3)     // Catch:{ all -> 0x038d }
            r2.append(r0)     // Catch:{ all -> 0x038d }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x038d }
            com.sec.android.app.voicenote.provider.Log.m19d(r9, r2)     // Catch:{ all -> 0x038d }
            r8.updateViewHeight(r1, r0, r6)     // Catch:{ all -> 0x038d }
        L_0x0328:
            androidx.appcompat.app.AppCompatActivity r0 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.view.Window r0 = r0.getWindow()     // Catch:{ all -> 0x038d }
            android.view.View r0 = r0.getDecorView()     // Catch:{ all -> 0x038d }
            r1 = 2131296608(0x7f090160, float:1.8211137E38)
            android.view.View r0 = r0.findViewById(r1)     // Catch:{ all -> 0x038d }
            r2 = r0
            android.widget.FrameLayout r2 = (android.widget.FrameLayout) r2     // Catch:{ all -> 0x038d }
            if (r2 == 0) goto L_0x034c
            r4 = 0
            r1 = r22
            r3 = r19
            r5 = r18
            r6 = r20
            r7 = r20
            r1.updateViewHeight(r2, r3, r4, r5, r6, r7)     // Catch:{ all -> 0x038d }
        L_0x034c:
            androidx.appcompat.app.AppCompatActivity r0 = r8.mActivity     // Catch:{ all -> 0x038d }
            android.view.Window r0 = r0.getWindow()     // Catch:{ all -> 0x038d }
            android.view.View r0 = r0.getDecorView()     // Catch:{ all -> 0x038d }
            r1 = 2131296598(0x7f090156, float:1.8211117E38)
            android.view.View r0 = r0.findViewById(r1)     // Catch:{ all -> 0x038d }
            r2 = r0
            android.widget.FrameLayout r2 = (android.widget.FrameLayout) r2     // Catch:{ all -> 0x038d }
            if (r2 == 0) goto L_0x0383
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x038d }
            r0.<init>()     // Catch:{ all -> 0x038d }
            java.lang.String r1 = "updateMainLayout - controlbuttonView "
            r0.append(r1)     // Catch:{ all -> 0x038d }
            r3 = r21
            r0.append(r3)     // Catch:{ all -> 0x038d }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x038d }
            com.sec.android.app.voicenote.provider.Log.m19d(r9, r0)     // Catch:{ all -> 0x038d }
            r4 = 0
            r5 = 0
            r1 = r22
            r6 = r17
            r7 = r17
            r1.updateViewHeight(r2, r3, r4, r5, r6, r7)     // Catch:{ all -> 0x038d }
        L_0x0383:
            r8.mOldPlayMode = r10     // Catch:{ all -> 0x038d }
            int r0 = r8.mCurrentScene     // Catch:{ all -> 0x038d }
            r8.mPreviouScene = r0     // Catch:{ all -> 0x038d }
            com.sec.android.app.voicenote.common.util.Trace.endSection()
            return
        L_0x038d:
            r0 = move-exception
            com.sec.android.app.voicenote.common.util.Trace.endSection()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.uicore.FragmentController.updateMainLayout():void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:81:0x027c, code lost:
        if (r0.getHeight() != r5) goto L_0x0281;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x02b5, code lost:
        if (r0.getHeight() != r5) goto L_0x02ba;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x02ee, code lost:
        if (r0.getHeight() != r5) goto L_0x02f3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateMainLayoutLandScape() {
        /*
            r23 = this;
            r8 = r23
            int r0 = r8.mCurrentScene
            r9 = 6
            r1 = -1
            r10 = 12
            r11 = 4
            java.lang.String r12 = "FragmentController"
            if (r0 == r11) goto L_0x0049
            r2 = 8
            if (r0 == r2) goto L_0x0049
            if (r0 == r9) goto L_0x0049
            r2 = 11
            if (r0 == r2) goto L_0x0049
            if (r0 == r10) goto L_0x0049
            int r0 = r8.mCurrentEvent
            r2 = 2005(0x7d5, float:2.81E-42)
            if (r0 == r2) goto L_0x0049
            r2 = 2006(0x7d6, float:2.811E-42)
            if (r0 == r2) goto L_0x0049
            java.lang.String r0 = "updateMainLayoutLandScape - do not update"
            com.sec.android.app.voicenote.provider.Log.m19d(r12, r0)
            int r0 = r8.mCurrentScene
            r8.mPreviouScene = r0
            r8.mOldPlayMode = r1
            r1 = 1
            if (r0 != r1) goto L_0x0048
            com.sec.android.app.voicenote.provider.NavigationBarProvider r0 = com.sec.android.app.voicenote.provider.NavigationBarProvider.getInstance()
            boolean r0 = r0.isNavigationBarEnabled()
            if (r0 == 0) goto L_0x0048
            com.sec.android.app.voicenote.provider.NavigationBarProvider r0 = com.sec.android.app.voicenote.provider.NavigationBarProvider.getInstance()
            boolean r0 = r0.isFullScreenGesture()
            if (r0 == 0) goto L_0x0048
            r23.updateIdleControlButtonMarginBottom()
        L_0x0048:
            return
        L_0x0049:
            java.lang.String r0 = "record_mode"
            int r0 = com.sec.android.app.voicenote.provider.Settings.getIntSettings(r0, r1)
            java.lang.String r2 = "play_mode"
            int r13 = com.sec.android.app.voicenote.provider.Settings.getIntSettings(r2, r1)
            int r1 = r8.mCurrentScene
            if (r1 == r10) goto L_0x006b
            int r2 = r8.mPreviouScene
            if (r1 != r2) goto L_0x006b
            boolean r1 = r8.isNeedUpdateLayout
            if (r1 != 0) goto L_0x006b
            int r1 = r8.mOldPlayMode
            if (r1 != r13) goto L_0x006b
            java.lang.String r0 = "updateMainLayoutLandScape - orientation doesn't change. Don't need update"
            com.sec.android.app.voicenote.provider.Log.m19d(r12, r0)
            return
        L_0x006b:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "updateMainLayoutLandScape - recordMode: "
            r1.append(r2)
            r1.append(r0)
            java.lang.String r2 = " - playMode: "
            r1.append(r2)
            r1.append(r13)
            java.lang.String r2 = " - mOldPlayMode: "
            r1.append(r2)
            int r2 = r8.mOldPlayMode
            r1.append(r2)
            java.lang.String r2 = " - scene: "
            r1.append(r2)
            int r2 = r8.mCurrentScene
            r1.append(r2)
            java.lang.String r2 = " - previous scene:"
            r1.append(r2)
            int r2 = r8.mPreviouScene
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r12, r1)
            androidx.appcompat.app.AppCompatActivity r1 = r8.mActivity
            android.content.res.Resources r1 = r1.getResources()
            android.util.DisplayMetrics r1 = r1.getDisplayMetrics()
            int r1 = r1.heightPixels
            androidx.appcompat.app.AppCompatActivity r2 = r8.mActivity
            android.content.res.Resources r2 = r2.getResources()
            android.util.DisplayMetrics r2 = r2.getDisplayMetrics()
            int r14 = r2.widthPixels
            com.sec.android.app.voicenote.provider.NavigationBarProvider r2 = com.sec.android.app.voicenote.provider.NavigationBarProvider.getInstance()
            boolean r2 = r2.isNavigationBarEnabled()
            if (r2 == 0) goto L_0x00d4
            com.sec.android.app.voicenote.provider.NavigationBarProvider r2 = com.sec.android.app.voicenote.provider.NavigationBarProvider.getInstance()
            boolean r2 = r2.isFullScreenGesture()
            if (r2 == 0) goto L_0x00d4
            r23.updateMainControlMargin()
        L_0x00d4:
            int r2 = r23.getActionBarHeight()
            int r2 = r1 - r2
            if (r2 > 0) goto L_0x00e2
            java.lang.String r0 = "updateMainLayoutLandScape - mainViewHeight = 0 "
            com.sec.android.app.voicenote.provider.Log.m19d(r12, r0)
            return
        L_0x00e2:
            androidx.appcompat.app.AppCompatActivity r3 = r8.mActivity
            android.content.res.Resources r3 = r3.getResources()
            r4 = 2131165416(0x7f0700e8, float:1.7945048E38)
            int r15 = r3.getDimensionPixelSize(r4)
            androidx.appcompat.app.AppCompatActivity r3 = r8.mActivity
            android.content.res.Resources r3 = r3.getResources()
            r4 = 2131165419(0x7f0700eb, float:1.7945055E38)
            int r3 = r3.getDimensionPixelSize(r4)
            androidx.appcompat.app.AppCompatActivity r4 = r8.mActivity
            android.content.res.Resources r4 = r4.getResources()
            r5 = 2131165417(0x7f0700e9, float:1.794505E38)
            int r4 = r4.getDimensionPixelSize(r5)
            r23.updateMainControlMargin()
            int r5 = r8.mCurrentScene
            r7 = 0
            if (r5 != r10) goto L_0x0113
            r6 = r7
            goto L_0x0121
        L_0x0113:
            androidx.appcompat.app.AppCompatActivity r5 = r8.mActivity
            android.content.res.Resources r5 = r5.getResources()
            r6 = 2131165236(0x7f070034, float:1.7944683E38)
            int r5 = r5.getDimensionPixelSize(r6)
            r6 = r5
        L_0x0121:
            int r5 = r1 * 51
            int r5 = r5 / 100
            int r10 = r8.mCurrentScene
            boolean r0 = r8.isSttViewLayoutVisible(r10, r13, r0)
            if (r0 == 0) goto L_0x0140
            int r1 = r1 * 40
            int r5 = r1 / 100
            int r2 = r2 - r5
            int r2 = r2 - r15
            int r2 = r2 - r3
            int r2 = r2 - r4
            int r0 = r6 * 3
            int r0 = r0 / 2
            int r2 = r2 - r0
            int r0 = r8.calculateSttActualHeight(r2)
            r10 = r0
            goto L_0x0141
        L_0x0140:
            r10 = r7
        L_0x0141:
            com.sec.android.app.voicenote.provider.WaveProvider r0 = com.sec.android.app.voicenote.provider.WaveProvider.getInstance()
            androidx.appcompat.app.AppCompatActivity r1 = r8.mActivity
            android.content.res.Resources r1 = r1.getResources()
            r2 = 2131165989(0x7f070325, float:1.794621E38)
            int r1 = r1.getDimensionPixelSize(r2)
            int r1 = r5 - r1
            androidx.appcompat.app.AppCompatActivity r2 = r8.mActivity
            android.content.res.Resources r2 = r2.getResources()
            r3 = 2131165969(0x7f070311, float:1.794617E38)
            int r2 = r2.getDimensionPixelSize(r3)
            int r1 = r1 - r2
            r0.setWaveHeight(r5, r1, r7)
            androidx.appcompat.app.AppCompatActivity r0 = r8.mActivity
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2131165967(0x7f07030f, float:1.7946166E38)
            int r0 = r0.getDimensionPixelSize(r1)
            int r5 = r5 + r0
            int r0 = r6 * 3
            int r0 = r0 / 2
            int r4 = r5 + r0
            androidx.appcompat.app.AppCompatActivity r0 = r8.mActivity
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2131165554(0x7f070172, float:1.7945328E38)
            int r16 = r0.getDimensionPixelSize(r1)
            int r0 = r8.mCurrentScene
            if (r0 != r11) goto L_0x0192
            int r0 = r14 / 2
            int r1 = r16 * 2
            r17 = r1
            r2 = r15
            goto L_0x0196
        L_0x0192:
            r0 = r7
            r2 = r0
            r17 = r2
        L_0x0196:
            int r3 = r14 - r0
            int r1 = r17 * 2
            int r18 = r0 - r1
            androidx.appcompat.app.AppCompatActivity r0 = r8.mActivity
            android.view.Window r0 = r0.getWindow()
            android.view.View r0 = r0.getDecorView()
            r1 = 2131296608(0x7f090160, float:1.8211137E38)
            android.view.View r0 = r0.findViewById(r1)
            r1 = r0
            android.widget.FrameLayout r1 = (android.widget.FrameLayout) r1
            int r0 = r8.mOldPlayMode
            if (r0 != r13) goto L_0x01cf
            int r0 = r1.getHeight()
            if (r0 != r2) goto L_0x01cf
            int r0 = r8.mOldPlayMode
            if (r0 != r13) goto L_0x01c7
            int r0 = r8.mCurrentScene
            if (r0 != r11) goto L_0x01c7
            int r0 = r8.mPreviouScene
            if (r0 != r9) goto L_0x01c7
            goto L_0x01cf
        L_0x01c7:
            r9 = r3
            r20 = r4
            r22 = r5
            r21 = r6
            goto L_0x01fc
        L_0x01cf:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r7 = "updateMainLayoutLandScape - toolbarViewActualHeight "
            r0.append(r7)
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r12, r0)
            r7 = 0
            r19 = 0
            r0 = r23
            r9 = r3
            r3 = r18
            r20 = r4
            r4 = r7
            r7 = r5
            r5 = r19
            r21 = r6
            r6 = r17
            r22 = r7
            r7 = r17
            r0.updateViewHeight(r1, r2, r3, r4, r5, r6, r7)
        L_0x01fc:
            androidx.appcompat.app.AppCompatActivity r0 = r8.mActivity
            android.view.Window r0 = r0.getWindow()
            android.view.View r0 = r0.getDecorView()
            r1 = 2131296598(0x7f090156, float:1.8211117E38)
            android.view.View r0 = r0.findViewById(r1)
            r1 = r0
            android.widget.FrameLayout r1 = (android.widget.FrameLayout) r1
            int r0 = r8.mOldPlayMode
            if (r0 != r13) goto L_0x022b
            int r0 = r1.getWidth()
            if (r0 != r9) goto L_0x022b
            int r0 = r8.mCurrentScene
            r2 = 12
            if (r0 == r2) goto L_0x022b
            int r2 = r8.mOldPlayMode
            if (r2 != r13) goto L_0x025f
            if (r0 != r11) goto L_0x025f
            int r0 = r8.mPreviouScene
            r2 = 6
            if (r0 != r2) goto L_0x025f
        L_0x022b:
            int r0 = r8.mCurrentScene
            if (r0 != r11) goto L_0x0234
            int r16 = r16 * 3
            r14 = r16
            goto L_0x0238
        L_0x0234:
            int r14 = r14 * 2
            int r14 = r14 / 7
        L_0x0238:
            r4 = 0
            r5 = 0
            r0 = r23
            r2 = r15
            r3 = r9
            r6 = r14
            r7 = r14
            r0.updateViewHeight(r1, r2, r3, r4, r5, r6, r7)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "updateMainLayoutLandScape - mainControlButton: width = "
            r0.append(r1)
            r0.append(r9)
            java.lang.String r1 = " - "
            r0.append(r1)
            r0.append(r14)
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r12, r0)
        L_0x025f:
            androidx.appcompat.app.AppCompatActivity r0 = r8.mActivity
            android.view.Window r0 = r0.getWindow()
            android.view.View r0 = r0.getDecorView()
            r1 = 2131296601(0x7f090159, float:1.8211123E38)
            android.view.View r0 = r0.findViewById(r1)
            android.widget.FrameLayout r0 = (android.widget.FrameLayout) r0
            int r1 = r8.mOldPlayMode
            if (r1 != r13) goto L_0x027f
            int r1 = r0.getHeight()
            r5 = r20
            if (r1 == r5) goto L_0x0298
            goto L_0x0281
        L_0x027f:
            r5 = r20
        L_0x0281:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "updateMainLayoutLandScape - infoViewActualHeight "
            r1.append(r2)
            r1.append(r5)
            java.lang.String r1 = r1.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r12, r1)
            r8.updateViewHeight(r0, r5)
        L_0x0298:
            androidx.appcompat.app.AppCompatActivity r0 = r8.mActivity
            android.view.Window r0 = r0.getWindow()
            android.view.View r0 = r0.getDecorView()
            r1 = 2131296610(0x7f090162, float:1.8211142E38)
            android.view.View r0 = r0.findViewById(r1)
            android.widget.FrameLayout r0 = (android.widget.FrameLayout) r0
            int r1 = r8.mOldPlayMode
            if (r1 != r13) goto L_0x02b8
            int r1 = r0.getHeight()
            r5 = r22
            if (r1 == r5) goto L_0x02d1
            goto L_0x02ba
        L_0x02b8:
            r5 = r22
        L_0x02ba:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "updateMainLayoutLandScape - waveViewActualHeight "
            r1.append(r2)
            r1.append(r5)
            java.lang.String r1 = r1.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r12, r1)
            r8.updateViewHeight(r0, r5)
        L_0x02d1:
            androidx.appcompat.app.AppCompatActivity r0 = r8.mActivity
            android.view.Window r0 = r0.getWindow()
            android.view.View r0 = r0.getDecorView()
            r1 = 2131296595(0x7f090153, float:1.8211111E38)
            android.view.View r0 = r0.findViewById(r1)
            android.widget.FrameLayout r0 = (android.widget.FrameLayout) r0
            int r1 = r8.mOldPlayMode
            if (r1 != r13) goto L_0x02f1
            int r1 = r0.getHeight()
            r5 = r21
            if (r1 == r5) goto L_0x030b
            goto L_0x02f3
        L_0x02f1:
            r5 = r21
        L_0x02f3:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "updateMainLayoutLandScape - bookmarkViewActualHeight "
            r1.append(r2)
            r1.append(r5)
            java.lang.String r1 = r1.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r12, r1)
            r1 = 0
            r8.updateViewLinearHeight(r0, r5, r1, r1)
        L_0x030b:
            androidx.appcompat.app.AppCompatActivity r0 = r8.mActivity
            android.view.Window r0 = r0.getWindow()
            android.view.View r0 = r0.getDecorView()
            r1 = 2131296604(0x7f09015c, float:1.821113E38)
            android.view.View r0 = r0.findViewById(r1)
            android.widget.FrameLayout r0 = (android.widget.FrameLayout) r0
            int r1 = r8.mOldPlayMode
            if (r1 != r13) goto L_0x0328
            int r1 = r0.getHeight()
            if (r1 == r10) goto L_0x033f
        L_0x0328:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "updateMainLayoutLandScape - sttViewActualHeight "
            r1.append(r2)
            r1.append(r10)
            java.lang.String r1 = r1.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r12, r1)
            r8.updateViewHeight(r0, r10)
        L_0x033f:
            r8.mOldPlayMode = r13
            int r0 = r8.mCurrentScene
            r8.mPreviouScene = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.uicore.FragmentController.updateMainLayoutLandScape():void");
    }

    private void updateMainLayoutMultiWindow(int i, int i2) {
        if (i2 == 2) {
            updatePortraitMultiWindow(i);
        } else if (i2 == 3) {
            updateLandScapeMultiWindow(i);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:55:0x015e  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0193  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x019b  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x01a5  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x01ac  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x01c5  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x01ee  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x020b  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x025d  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0289  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x02b5  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x02e8  */
    /* JADX WARNING: Removed duplicated region for block: B:86:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateLandScapeMultiWindow(int r21) {
        /*
            r20 = this;
            r8 = r20
            int r0 = r8.mCurrentScene
            r1 = 12
            r2 = 4
            java.lang.String r9 = "FragmentController"
            if (r0 == r2) goto L_0x0030
            r3 = 8
            if (r0 == r3) goto L_0x0030
            r3 = 6
            if (r0 == r3) goto L_0x0030
            r3 = 11
            if (r0 == r3) goto L_0x0030
            if (r0 == r1) goto L_0x0030
            int r0 = r8.mCurrentEvent
            r3 = 2005(0x7d5, float:2.81E-42)
            if (r0 == r3) goto L_0x0030
            r3 = 2006(0x7d6, float:2.811E-42)
            if (r0 == r3) goto L_0x0030
            java.lang.String r0 = "updateMainLayoutLandScape - do not update"
            com.sec.android.app.voicenote.provider.Log.m19d(r9, r0)
            int r0 = r8.mCurrentScene
            r1 = 1
            if (r0 != r1) goto L_0x002f
            r20.updateIdleControlButtonMarginBottom()
        L_0x002f:
            return
        L_0x0030:
            r0 = -1
            java.lang.String r3 = "record_mode"
            int r3 = com.sec.android.app.voicenote.provider.Settings.getIntSettings(r3, r0)
            java.lang.String r4 = "play_mode"
            int r0 = com.sec.android.app.voicenote.provider.Settings.getIntSettings(r4, r0)
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "updateLandScapeMultiWindow - recordMode: "
            r4.append(r5)
            r4.append(r3)
            java.lang.String r5 = " - playMode: "
            r4.append(r5)
            r4.append(r0)
            java.lang.String r5 = " - scene: "
            r4.append(r5)
            int r5 = r8.mCurrentScene
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r9, r4)
            androidx.appcompat.app.AppCompatActivity r4 = r8.mActivity
            int r4 = com.sec.android.app.voicenote.common.util.DisplayManager.getMultiWindowCurrentAppHeight(r4)
            androidx.appcompat.app.AppCompatActivity r5 = r8.mActivity
            int r5 = com.sec.android.app.voicenote.common.util.DisplayManager.getCurrentScreenWidth(r5)
            int r6 = r8.mCurrentScene
            boolean r0 = r8.isSttViewLayoutVisible(r6, r0, r3)
            int r3 = r20.getActionBarHeight()
            int r4 = r4 - r3
            if (r4 > 0) goto L_0x0082
            java.lang.String r0 = "updateLandScapeMultiWindow - mainViewHeight = 0 "
            com.sec.android.app.voicenote.provider.Log.m19d(r9, r0)
            return
        L_0x0082:
            androidx.appcompat.app.AppCompatActivity r3 = r8.mActivity
            android.content.res.Resources r3 = r3.getResources()
            r6 = 2131165541(0x7f070165, float:1.7945302E38)
            int r10 = r3.getDimensionPixelSize(r6)
            androidx.appcompat.app.AppCompatActivity r3 = r8.mActivity
            android.content.res.Resources r3 = r3.getResources()
            r7 = 2131165545(0x7f070169, float:1.794531E38)
            int r11 = r3.getDimensionPixelSize(r7)
            boolean r3 = com.sec.android.app.voicenote.provider.VoiceNoteFeature.FLAG_IS_TABLET
            if (r3 == 0) goto L_0x00ae
            androidx.appcompat.app.AppCompatActivity r3 = r8.mActivity
            android.content.res.Resources r3 = r3.getResources()
            r7 = 2131165543(0x7f070167, float:1.7945306E38)
            int r3 = r3.getDimensionPixelSize(r7)
            goto L_0x00bb
        L_0x00ae:
            androidx.appcompat.app.AppCompatActivity r3 = r8.mActivity
            android.content.res.Resources r3 = r3.getResources()
            r7 = 2131165542(0x7f070166, float:1.7945304E38)
            int r3 = r3.getDimensionPixelSize(r7)
        L_0x00bb:
            r12 = r3
            androidx.appcompat.app.AppCompatActivity r3 = r8.mActivity
            android.content.res.Resources r3 = r3.getResources()
            r7 = 2131165236(0x7f070034, float:1.7944683E38)
            int r3 = r3.getDimensionPixelSize(r7)
            int r7 = r3 / 3
            int r13 = r8.mCurrentScene
            if (r13 != r1) goto L_0x00d0
            r7 = 0
        L_0x00d0:
            int r1 = r8.mCurrentScene
            if (r1 != r2) goto L_0x00d8
            int r1 = r5 / 2
            r13 = r10
            goto L_0x00da
        L_0x00d8:
            r1 = 0
            r13 = 0
        L_0x00da:
            androidx.appcompat.app.AppCompatActivity r15 = r8.mActivity
            android.content.res.Resources r15 = r15.getResources()
            r2 = 2131165549(0x7f07016d, float:1.7945318E38)
            int r2 = r15.getDimensionPixelSize(r2)
            int r15 = r4 * 51
            int r15 = r15 / 100
            if (r0 == 0) goto L_0x0114
            int r15 = r4 * 40
            int r15 = r15 / 100
            int r16 = r4 - r15
            int r16 = r16 - r3
            int r17 = r7 * 2
            int r16 = r16 - r17
            int r16 = r16 - r10
            int r16 = r16 - r11
            int r6 = r16 - r12
            if (r6 >= r2) goto L_0x010c
            int r3 = r3 + r17
            int r3 = r3 + r6
            if (r3 >= r2) goto L_0x0109
            r2 = 0
            r3 = 0
            goto L_0x010a
        L_0x0109:
            r2 = 0
        L_0x010a:
            r7 = 0
            goto L_0x010e
        L_0x010c:
            r2 = r3
            r3 = r6
        L_0x010e:
            int r3 = r8.calculateSttActualHeight(r3)
            r6 = r3
            goto L_0x0116
        L_0x0114:
            r2 = r3
            r6 = 0
        L_0x0116:
            androidx.appcompat.app.AppCompatActivity r3 = r8.mActivity
            boolean r3 = com.sec.android.app.voicenote.common.util.DisplayManager.isInDeXExternalMonitor(r3)
            if (r3 == 0) goto L_0x0135
            if (r0 != 0) goto L_0x0135
            int r0 = r15 + r2
            int r3 = r7 * 2
            int r0 = r0 + r3
            int r0 = r0 + r10
            int r0 = r0 + r12
            int r0 = r0 + r11
            if (r0 <= r4) goto L_0x0135
            int r3 = r3 + r2
            int r0 = r0 - r3
            if (r0 <= r4) goto L_0x0132
            int r4 = r4 - r10
            int r4 = r4 - r12
            int r15 = r4 - r11
        L_0x0132:
            r4 = 0
            r7 = 0
            goto L_0x0137
        L_0x0135:
            r4 = r7
            r7 = r2
        L_0x0137:
            com.sec.android.app.voicenote.provider.WaveProvider r0 = com.sec.android.app.voicenote.provider.WaveProvider.getInstance()
            androidx.appcompat.app.AppCompatActivity r2 = r8.mActivity
            android.content.res.Resources r2 = r2.getResources()
            r3 = 2131165989(0x7f070325, float:1.794621E38)
            int r2 = r2.getDimensionPixelSize(r3)
            int r2 = r15 - r2
            androidx.appcompat.app.AppCompatActivity r3 = r8.mActivity
            android.content.res.Resources r3 = r3.getResources()
            r14 = 2131165969(0x7f070311, float:1.794617E38)
            int r3 = r3.getDimensionPixelSize(r14)
            int r2 = r2 - r3
            r3 = 0
            r0.setWaveHeight(r15, r2, r3)
            if (r7 <= 0) goto L_0x016c
            androidx.appcompat.app.AppCompatActivity r0 = r8.mActivity
            android.content.res.Resources r0 = r0.getResources()
            r2 = 2131165967(0x7f07030f, float:1.7946166E38)
            int r0 = r0.getDimensionPixelSize(r2)
            int r15 = r15 + r0
        L_0x016c:
            int r0 = r4 * 2
            int r0 = r0 + r15
            int r14 = r0 + r7
            int r5 = r5 - r1
            androidx.appcompat.app.AppCompatActivity r0 = r8.mActivity
            android.content.res.Resources r0 = r0.getResources()
            r2 = 2131165541(0x7f070165, float:1.7945302E38)
            int r0 = r0.getDimensionPixelSize(r2)
            androidx.appcompat.app.AppCompatActivity r2 = r8.mActivity
            android.content.res.Resources r2 = r2.getResources()
            r3 = 2131165544(0x7f070168, float:1.7945308E38)
            int r2 = r2.getDimensionPixelSize(r3)
            int r0 = r0 * 3
            int r2 = r2 * 2
            int r2 = r2 + r0
            if (r5 <= r2) goto L_0x019b
            int r0 = r5 - r2
            int r0 = r0 / 2
            r17 = r0
            r2 = 4
            goto L_0x01a1
        L_0x019b:
            int r0 = r5 - r0
            r2 = 4
            int r0 = r0 / r2
            r17 = r0
        L_0x01a1:
            int r0 = r8.mCurrentScene
            if (r0 != r2) goto L_0x01ac
            int r0 = r17 * 2
            int r1 = r1 - r0
            r3 = r1
            r16 = r17
            goto L_0x01af
        L_0x01ac:
            r3 = r1
            r16 = 0
        L_0x01af:
            androidx.appcompat.app.AppCompatActivity r0 = r8.mActivity
            android.view.Window r0 = r0.getWindow()
            android.view.View r0 = r0.getDecorView()
            r1 = 2131296608(0x7f090160, float:1.8211137E38)
            android.view.View r0 = r0.findViewById(r1)
            r1 = r0
            android.widget.FrameLayout r1 = (android.widget.FrameLayout) r1
            if (r1 == 0) goto L_0x01ee
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "updateLandScapeMultiWindow - toolbarViewActualHeight "
            r0.append(r2)
            r0.append(r14)
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r9, r0)
            r0 = r20
            r2 = r13
            r13 = r4
            r4 = r11
            r21 = r5
            r5 = r12
            r19 = r6
            r6 = r16
            r18 = r13
            r13 = r7
            r7 = r16
            r0.updateViewHeight(r1, r2, r3, r4, r5, r6, r7)
            goto L_0x01f5
        L_0x01ee:
            r18 = r4
            r21 = r5
            r19 = r6
            r13 = r7
        L_0x01f5:
            androidx.appcompat.app.AppCompatActivity r0 = r8.mActivity
            android.view.Window r0 = r0.getWindow()
            android.view.View r0 = r0.getDecorView()
            r1 = 2131296598(0x7f090156, float:1.8211117E38)
            android.view.View r0 = r0.findViewById(r1)
            r1 = r0
            android.widget.FrameLayout r1 = (android.widget.FrameLayout) r1
            if (r1 == 0) goto L_0x0248
            r20.updateMainControlMargin()
            r4 = 0
            r5 = 0
            r0 = r20
            r2 = r10
            r3 = r21
            r6 = r17
            r7 = r17
            r0.updateViewHeight(r1, r2, r3, r4, r5, r6, r7)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "updateLandScapeMultiWindow - mainControlButton: width, height, top, bot = "
            r0.append(r1)
            r5 = r21
            r0.append(r5)
            java.lang.String r1 = " - "
            r0.append(r1)
            r0.append(r10)
            java.lang.String r1 = " "
            r0.append(r1)
            r0.append(r11)
            r0.append(r1)
            r0.append(r12)
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r9, r0)
        L_0x0248:
            androidx.appcompat.app.AppCompatActivity r0 = r8.mActivity
            android.view.Window r0 = r0.getWindow()
            android.view.View r0 = r0.getDecorView()
            r1 = 2131296601(0x7f090159, float:1.8211123E38)
            android.view.View r0 = r0.findViewById(r1)
            android.widget.FrameLayout r0 = (android.widget.FrameLayout) r0
            if (r0 == 0) goto L_0x0274
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "updateLandScapeMultiWindow - infoViewActualHeight "
            r1.append(r2)
            r1.append(r14)
            java.lang.String r1 = r1.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r9, r1)
            r8.updateViewHeight(r0, r14)
        L_0x0274:
            androidx.appcompat.app.AppCompatActivity r0 = r8.mActivity
            android.view.Window r0 = r0.getWindow()
            android.view.View r0 = r0.getDecorView()
            r1 = 2131296610(0x7f090162, float:1.8211142E38)
            android.view.View r0 = r0.findViewById(r1)
            android.widget.FrameLayout r0 = (android.widget.FrameLayout) r0
            if (r0 == 0) goto L_0x02a0
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "updateLandScapeMultiWindow - waveViewActualHeight "
            r1.append(r2)
            r1.append(r15)
            java.lang.String r1 = r1.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r9, r1)
            r8.updateViewHeight(r0, r15)
        L_0x02a0:
            androidx.appcompat.app.AppCompatActivity r0 = r8.mActivity
            android.view.Window r0 = r0.getWindow()
            android.view.View r0 = r0.getDecorView()
            r1 = 2131296595(0x7f090153, float:1.8211111E38)
            android.view.View r0 = r0.findViewById(r1)
            android.widget.FrameLayout r0 = (android.widget.FrameLayout) r0
            if (r0 == 0) goto L_0x02d3
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "updateLandScapeMultiWindow - bookmarkViewActualHeight "
            r1.append(r2)
            r1.append(r13)
            java.lang.String r2 = " - spaceViewActualHeight : "
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r9, r1)
            r14 = r18
            r8.updateViewLinearHeight(r0, r13, r14, r14)
        L_0x02d3:
            androidx.appcompat.app.AppCompatActivity r0 = r8.mActivity
            android.view.Window r0 = r0.getWindow()
            android.view.View r0 = r0.getDecorView()
            r1 = 2131296604(0x7f09015c, float:1.821113E38)
            android.view.View r0 = r0.findViewById(r1)
            android.widget.FrameLayout r0 = (android.widget.FrameLayout) r0
            if (r0 == 0) goto L_0x0301
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "updateLandScapeMultiWindow - sttViewActualHeight "
            r1.append(r2)
            r14 = r19
            r1.append(r14)
            java.lang.String r1 = r1.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r9, r1)
            r8.updateViewHeight(r0, r14)
        L_0x0301:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.uicore.FragmentController.updateLandScapeMultiWindow(int):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:89:0x02c3, code lost:
        if ((r11 - r23) >= r14) goto L_0x02c5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x02d1, code lost:
        if (r11 >= (r2 - r12)) goto L_0x02d3;
     */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x02de  */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x02f2  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x0314 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x035e  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x0376  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x038e  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0393  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x03a9  */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x03c2  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x03e6  */
    /* JADX WARNING: Removed duplicated region for block: B:137:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0134  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x016d  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x01c8  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x01ec  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x020f  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0218  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0222  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0258  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0298  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x02bf  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updatePortraitMultiWindow(int r26) {
        /*
            r25 = this;
            r7 = r25
            r0 = r26
            int r1 = r7.mCurrentScene
            r2 = 1
            r3 = 6
            java.lang.String r4 = "FragmentController"
            r5 = 12
            r6 = 4
            if (r1 == r6) goto L_0x0032
            r8 = 8
            if (r1 == r8) goto L_0x0032
            if (r1 == r3) goto L_0x0032
            r8 = 11
            if (r1 == r8) goto L_0x0032
            if (r1 == r5) goto L_0x0032
            int r1 = r7.mCurrentEvent
            r8 = 2005(0x7d5, float:2.81E-42)
            if (r1 == r8) goto L_0x0032
            r8 = 2006(0x7d6, float:2.811E-42)
            if (r1 == r8) goto L_0x0032
            java.lang.String r0 = "updatePortraitMultiWindow - do not update"
            com.sec.android.app.voicenote.provider.Log.m19d(r4, r0)
            int r0 = r7.mCurrentScene
            if (r0 != r2) goto L_0x0031
            r25.updateIdleControlButtonMarginBottom()
        L_0x0031:
            return
        L_0x0032:
            r1 = -1
            java.lang.String r8 = "record_mode"
            int r8 = com.sec.android.app.voicenote.provider.Settings.getIntSettings(r8, r1)
            java.lang.String r9 = "play_mode"
            int r1 = com.sec.android.app.voicenote.provider.Settings.getIntSettings(r9, r1)
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "updatePortraitMultiWindow - recordMode: "
            r9.append(r10)
            r9.append(r8)
            java.lang.String r10 = " - playMode: "
            r9.append(r10)
            r9.append(r1)
            java.lang.String r10 = " - scene: "
            r9.append(r10)
            int r10 = r7.mCurrentScene
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r4, r9)
            androidx.appcompat.app.AppCompatActivity r4 = r7.mActivity
            int r4 = com.sec.android.app.voicenote.common.util.DisplayManager.getActionBarHeight(r4)
            androidx.appcompat.app.AppCompatActivity r9 = r7.mActivity
            int r9 = com.sec.android.app.voicenote.common.util.DisplayManager.getMultiWindowCurrentAppHeight(r9)
            int r9 = r9 - r4
            androidx.appcompat.app.AppCompatActivity r4 = r7.mActivity
            int r4 = com.sec.android.app.voicenote.common.util.DisplayManager.getCurrentScreenWidth(r4)
            int r10 = r7.mCurrentScene
            boolean r1 = r7.isSttViewLayoutVisible(r10, r1, r8)
            r25.updateMainControlMargin()
            androidx.appcompat.app.AppCompatActivity r8 = r7.mActivity
            android.content.res.Resources r8 = r8.getResources()
            r10 = 2131165541(0x7f070165, float:1.7945302E38)
            int r8 = r8.getDimensionPixelSize(r10)
            boolean r10 = com.sec.android.app.voicenote.provider.VoiceNoteFeature.FLAG_IS_TABLET
            if (r10 == 0) goto L_0x00a0
            androidx.appcompat.app.AppCompatActivity r10 = r7.mActivity
            android.content.res.Resources r10 = r10.getResources()
            r11 = 2131165543(0x7f070167, float:1.7945306E38)
            int r10 = r10.getDimensionPixelSize(r11)
            goto L_0x00ad
        L_0x00a0:
            androidx.appcompat.app.AppCompatActivity r10 = r7.mActivity
            android.content.res.Resources r10 = r10.getResources()
            r11 = 2131165542(0x7f070166, float:1.7945304E38)
            int r10 = r10.getDimensionPixelSize(r11)
        L_0x00ad:
            androidx.appcompat.app.AppCompatActivity r11 = r7.mActivity
            android.content.res.Resources r11 = r11.getResources()
            r12 = 2131165545(0x7f070169, float:1.794531E38)
            int r11 = r11.getDimensionPixelSize(r12)
            androidx.appcompat.app.AppCompatActivity r12 = r7.mActivity
            android.content.res.Resources r12 = r12.getResources()
            r13 = 2131165544(0x7f070168, float:1.7945308E38)
            int r12 = r12.getDimensionPixelSize(r13)
            androidx.appcompat.app.AppCompatActivity r14 = r7.mActivity
            android.content.res.Resources r14 = r14.getResources()
            r15 = 2131165554(0x7f070172, float:1.7945328E38)
            int r14 = r14.getDimensionPixelSize(r15)
            androidx.appcompat.app.AppCompatActivity r15 = r7.mActivity
            android.content.res.Resources r15 = r15.getResources()
            r2 = 2131165418(0x7f0700ea, float:1.7945053E38)
            int r2 = r15.getDimensionPixelSize(r2)
            int r15 = com.sec.android.app.voicenote.common.util.DisplayManager.getFullScreenWidth()
            if (r4 != r15) goto L_0x0106
            boolean r15 = com.sec.android.app.voicenote.common.util.DisplayManager.isDeviceOnLandscape()
            if (r15 != 0) goto L_0x0106
            boolean r12 = com.sec.android.app.voicenote.provider.VoiceNoteFeature.FLAG_IS_TABLET
            if (r12 == 0) goto L_0x00fa
            int r12 = r8 * 3
            int r4 = r4 - r12
            int r12 = r2 * 2
            int r4 = r4 - r12
            int r4 = r4 / r6
            int r4 = r4 + r2
            goto L_0x0115
        L_0x00fa:
            androidx.appcompat.app.AppCompatActivity r2 = r7.mActivity
            android.content.res.Resources r2 = r2.getResources()
            int r2 = r2.getDimensionPixelSize(r13)
            r12 = r2
            goto L_0x0116
        L_0x0106:
            int r2 = r8 * 3
            int r12 = r12 * 2
            int r13 = r2 + r12
            if (r4 <= r13) goto L_0x0113
            int r4 = r4 - r2
            int r4 = r4 - r12
            int r4 = r4 / 2
            goto L_0x0115
        L_0x0113:
            int r4 = r4 - r2
            int r4 = r4 / r6
        L_0x0115:
            r12 = r4
        L_0x0116:
            androidx.appcompat.app.AppCompatActivity r2 = r7.mActivity
            android.content.res.Resources r2 = r2.getResources()
            r4 = 2131165236(0x7f070034, float:1.7944683E38)
            int r2 = r2.getDimensionPixelSize(r4)
            androidx.appcompat.app.AppCompatActivity r4 = r7.mActivity
            android.content.res.Resources r4 = r4.getResources()
            r13 = 2131165540(0x7f070164, float:1.79453E38)
            int r4 = r4.getDimensionPixelSize(r13)
            int r13 = r7.mCurrentScene
            if (r13 != r5) goto L_0x0136
            r2 = 0
            r4 = 0
        L_0x0136:
            if (r1 != 0) goto L_0x014f
            int r13 = r7.mCurrentScene
            if (r13 == r6) goto L_0x014f
            if (r13 == r3) goto L_0x014f
            if (r13 != r5) goto L_0x0141
            goto L_0x014f
        L_0x0141:
            androidx.appcompat.app.AppCompatActivity r3 = r7.mActivity
            android.content.res.Resources r3 = r3.getResources()
            r5 = 2131165547(0x7f07016b, float:1.7945314E38)
            int r3 = r3.getDimensionPixelSize(r5)
            goto L_0x015c
        L_0x014f:
            androidx.appcompat.app.AppCompatActivity r3 = r7.mActivity
            android.content.res.Resources r3 = r3.getResources()
            r5 = 2131165546(0x7f07016a, float:1.7945312E38)
            int r3 = r3.getDimensionPixelSize(r5)
        L_0x015c:
            androidx.appcompat.app.AppCompatActivity r5 = r7.mActivity
            android.content.res.Resources r5 = r5.getResources()
            r13 = 2131165555(0x7f070173, float:1.794533E38)
            int r5 = r5.getDimensionPixelSize(r13)
            int r15 = r7.mCurrentScene
            if (r15 != r6) goto L_0x01c8
            androidx.appcompat.app.AppCompatActivity r6 = r7.mActivity
            android.content.res.Resources r6 = r6.getResources()
            r11 = 2131165551(0x7f07016f, float:1.7945322E38)
            int r6 = r6.getDimensionPixelSize(r11)
            androidx.appcompat.app.AppCompatActivity r11 = r7.mActivity
            android.content.res.Resources r11 = r11.getResources()
            r15 = 2131165960(0x7f070308, float:1.7946152E38)
            int r11 = r11.getDimensionPixelSize(r15)
            androidx.appcompat.app.AppCompatActivity r15 = r7.mActivity
            int r15 = com.sec.android.app.voicenote.common.util.DisplayManager.getCurrentScreenWidth(r15)
            int r11 = r11 * 2
            int r15 = r15 - r11
            int r15 = r15 / 3
            if (r15 < r6) goto L_0x01ae
            androidx.appcompat.app.AppCompatActivity r6 = r7.mActivity
            boolean r6 = com.sec.android.app.voicenote.common.util.DisplayManager.smallHalfScreen(r6)
            if (r6 == 0) goto L_0x019d
            goto L_0x01ae
        L_0x019d:
            int r6 = r12 - r14
            androidx.appcompat.app.AppCompatActivity r11 = r7.mActivity
            android.content.res.Resources r11 = r11.getResources()
            r14 = 2131165552(0x7f070170, float:1.7945324E38)
            int r11 = r11.getDimensionPixelSize(r14)
            r15 = r11
            goto L_0x01bd
        L_0x01ae:
            androidx.appcompat.app.AppCompatActivity r6 = r7.mActivity
            android.content.res.Resources r6 = r6.getResources()
            r11 = 2131165553(0x7f070171, float:1.7945326E38)
            int r6 = r6.getDimensionPixelSize(r11)
            r15 = r6
            r6 = r12
        L_0x01bd:
            androidx.appcompat.app.AppCompatActivity r11 = r7.mActivity
            android.content.res.Resources r11 = r11.getResources()
            int r11 = r11.getDimensionPixelSize(r13)
            goto L_0x01cb
        L_0x01c8:
            r5 = 0
            r6 = 0
            r15 = 0
        L_0x01cb:
            androidx.appcompat.app.AppCompatActivity r13 = r7.mActivity
            android.content.res.Resources r13 = r13.getResources()
            r14 = 2131165967(0x7f07030f, float:1.7946166E38)
            int r13 = r13.getDimensionPixelSize(r14)
            androidx.appcompat.app.AppCompatActivity r14 = r7.mActivity
            android.content.res.Resources r14 = r14.getResources()
            r17 = r12
            r12 = 2131165560(0x7f070178, float:1.794534E38)
            int r12 = r14.getDimensionPixelSize(r12)
            int r12 = r12 + r13
            boolean r14 = com.sec.android.app.voicenote.provider.VoiceNoteFeature.FLAG_IS_TABLET
            if (r14 == 0) goto L_0x01fa
            androidx.appcompat.app.AppCompatActivity r12 = r7.mActivity
            android.content.res.Resources r12 = r12.getResources()
            r14 = 2131165561(0x7f070179, float:1.7945343E38)
            int r12 = r12.getDimensionPixelSize(r14)
            int r12 = r12 + r13
        L_0x01fa:
            androidx.appcompat.app.AppCompatActivity r14 = r7.mActivity
            android.content.res.Resources r14 = r14.getResources()
            r18 = r6
            r6 = 2131165549(0x7f07016d, float:1.7945318E38)
            int r6 = r14.getDimensionPixelSize(r6)
            boolean r14 = com.sec.android.app.voicenote.common.util.DisplayManager.isDeviceOnLandscape()
            if (r14 == 0) goto L_0x0218
            int r14 = com.sec.android.app.voicenote.common.util.DisplayManager.getFullScreenHeight()
            int r14 = r14 * 19
            int r14 = r14 / 100
            goto L_0x0220
        L_0x0218:
            int r14 = com.sec.android.app.voicenote.common.util.DisplayManager.getFullScreenHeight()
            int r14 = r14 * 16
            int r14 = r14 / 100
        L_0x0220:
            if (r1 == 0) goto L_0x0258
            androidx.appcompat.app.AppCompatActivity r0 = r7.mActivity
            android.content.res.Resources r0 = r0.getResources()
            r19 = r14
            r14 = 2131165548(0x7f07016c, float:1.7945316E38)
            int r0 = r0.getDimensionPixelSize(r14)
            boolean r14 = com.sec.android.app.voicenote.provider.VoiceNoteFeature.FLAG_IS_TABLET
            if (r14 == 0) goto L_0x0245
            androidx.appcompat.app.AppCompatActivity r14 = r7.mActivity
            android.content.res.Resources r14 = r14.getResources()
            r20 = r0
            r0 = 2131165557(0x7f070175, float:1.7945334E38)
            int r0 = r14.getDimensionPixelSize(r0)
            goto L_0x0254
        L_0x0245:
            r20 = r0
            androidx.appcompat.app.AppCompatActivity r0 = r7.mActivity
            android.content.res.Resources r0 = r0.getResources()
            r14 = 2131165556(0x7f070174, float:1.7945332E38)
            int r0 = r0.getDimensionPixelSize(r14)
        L_0x0254:
            int r0 = r0 + r13
            r14 = r0
            r0 = r6
            goto L_0x027e
        L_0x0258:
            r19 = r14
            boolean r0 = com.sec.android.app.voicenote.provider.VoiceNoteFeature.FLAG_IS_TABLET
            if (r0 == 0) goto L_0x026c
            androidx.appcompat.app.AppCompatActivity r0 = r7.mActivity
            android.content.res.Resources r0 = r0.getResources()
            r14 = 2131165559(0x7f070177, float:1.7945339E38)
            int r0 = r0.getDimensionPixelSize(r14)
            goto L_0x0279
        L_0x026c:
            androidx.appcompat.app.AppCompatActivity r0 = r7.mActivity
            android.content.res.Resources r0 = r0.getResources()
            r14 = 2131165558(0x7f070176, float:1.7945337E38)
            int r0 = r0.getDimensionPixelSize(r14)
        L_0x0279:
            int r0 = r0 + r13
            r14 = r0
            r0 = 0
            r20 = 0
        L_0x027e:
            int r21 = r2 + r4
            int r22 = r5 * 2
            int r22 = r15 + r22
            int r11 = r11 + r8
            int r11 = r11 + r10
            int r0 = r0 + r20
            int r23 = r9 - r3
            int r11 = r23 - r11
            int r23 = r12 + r21
            int r23 = r23 + r0
            r24 = r2
            int r2 = r23 + r22
            int r23 = r21 + r22
            if (r1 == 0) goto L_0x02bf
            if (r11 < r2) goto L_0x02a8
            int r0 = r0 + r14
            int r0 = r0 + r21
            int r0 = r0 + r22
            if (r0 <= r11) goto L_0x02a2
            r14 = r12
        L_0x02a2:
            int r11 = r11 - r14
            int r11 = r11 - r21
            int r0 = r11 - r22
            goto L_0x02c5
        L_0x02a8:
            int r2 = r2 - r21
            if (r11 < r2) goto L_0x02b0
            int r11 = r11 - r12
            int r0 = r11 - r22
            goto L_0x02cd
        L_0x02b0:
            int r2 = r2 - r12
            if (r11 < r2) goto L_0x02b6
            int r0 = r11 - r22
            goto L_0x02d3
        L_0x02b6:
            int r0 = r6 + r20
            int r2 = r2 - r0
            if (r11 < r2) goto L_0x02bd
            r0 = 0
            goto L_0x02d3
        L_0x02bd:
            r0 = 0
            goto L_0x02d7
        L_0x02bf:
            if (r11 < r2) goto L_0x02c9
            int r11 = r11 - r23
            if (r11 < r14) goto L_0x02c6
        L_0x02c5:
            r12 = r14
        L_0x02c6:
            r2 = r24
            goto L_0x02dc
        L_0x02c9:
            int r2 = r2 - r21
            if (r11 < r2) goto L_0x02d0
        L_0x02cd:
            r2 = 0
            r4 = 0
            goto L_0x02dc
        L_0x02d0:
            int r2 = r2 - r12
            if (r11 < r2) goto L_0x02d7
        L_0x02d3:
            r2 = 0
            r4 = 0
            r12 = 0
            goto L_0x02dc
        L_0x02d7:
            r2 = 0
            r4 = 0
            r5 = 0
            r12 = 0
            r15 = 0
        L_0x02dc:
            if (r0 <= 0) goto L_0x02f2
            int r14 = r0 - r20
            r0 = r19
            if (r14 <= r0) goto L_0x02e5
            r14 = r0
        L_0x02e5:
            int r0 = r7.calculateSttActualHeight(r14)
            r11 = r0
            r16 = r5
            r14 = r20
            r5 = 1
            r0 = r26
            goto L_0x02f9
        L_0x02f2:
            r0 = r26
            r16 = r5
            r5 = 1
            r11 = 0
            r14 = 0
        L_0x02f9:
            if (r0 == r5) goto L_0x02fd
            if (r0 != 0) goto L_0x0321
        L_0x02fd:
            int r0 = r8 + r10
            int r0 = r0 + r10
            int r0 = r0 + r22
            int r0 = r0 + r11
            int r0 = r0 + r14
            int r0 = r0 + r2
            int r0 = r0 + r4
            int r0 = r0 + r12
            int r0 = r0 + r3
            int r5 = r9 * 35
            int r5 = r5 / 100
            int r10 = r9 * 16
            int r10 = r10 / 100
            int r0 = r0 - r12
            int r0 = r0 + r5
            if (r0 > r9) goto L_0x0321
            if (r1 == 0) goto L_0x0322
            int r0 = r0 - r11
            int r0 = r0 + r10
            if (r0 > r9) goto L_0x0322
            if (r10 < r6) goto L_0x0322
            int r11 = r7.calculateSttActualHeight(r10)
            goto L_0x0322
        L_0x0321:
            r5 = r12
        L_0x0322:
            androidx.appcompat.app.AppCompatActivity r0 = r7.mActivity
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2131165989(0x7f070325, float:1.794621E38)
            int r0 = r0.getDimensionPixelSize(r1)
            androidx.appcompat.app.AppCompatActivity r1 = r7.mActivity
            android.content.res.Resources r1 = r1.getResources()
            r6 = 2131165969(0x7f070311, float:1.794617E38)
            int r1 = r1.getDimensionPixelSize(r6)
            com.sec.android.app.voicenote.provider.WaveProvider r6 = com.sec.android.app.voicenote.provider.WaveProvider.getInstance()
            int r9 = r5 - r13
            int r0 = r9 - r0
            int r0 = r0 - r1
            r1 = 0
            r6.setWaveHeight(r9, r0, r1)
            androidx.appcompat.app.AppCompatActivity r0 = r7.mActivity
            android.view.Window r0 = r0.getWindow()
            android.view.View r0 = r0.getDecorView()
            r1 = 2131296601(0x7f090159, float:1.8211123E38)
            android.view.View r0 = r0.findViewById(r1)
            android.widget.FrameLayout r0 = (android.widget.FrameLayout) r0
            if (r0 == 0) goto L_0x0361
            r7.updateViewHeight(r0, r3)
        L_0x0361:
            androidx.appcompat.app.AppCompatActivity r0 = r7.mActivity
            android.view.Window r0 = r0.getWindow()
            android.view.View r0 = r0.getDecorView()
            r1 = 2131296610(0x7f090162, float:1.8211142E38)
            android.view.View r0 = r0.findViewById(r1)
            android.widget.FrameLayout r0 = (android.widget.FrameLayout) r0
            if (r0 == 0) goto L_0x0379
            r7.updateViewHeight(r0, r5)
        L_0x0379:
            androidx.appcompat.app.AppCompatActivity r0 = r7.mActivity
            android.view.Window r0 = r0.getWindow()
            android.view.View r0 = r0.getDecorView()
            r1 = 2131296595(0x7f090153, float:1.8211111E38)
            android.view.View r0 = r0.findViewById(r1)
            android.widget.FrameLayout r0 = (android.widget.FrameLayout) r0
            if (r0 == 0) goto L_0x0393
            r1 = 0
            r7.updateViewLinearHeight(r0, r2, r4, r1)
            goto L_0x0394
        L_0x0393:
            r1 = 0
        L_0x0394:
            androidx.appcompat.app.AppCompatActivity r0 = r7.mActivity
            android.view.Window r0 = r0.getWindow()
            android.view.View r0 = r0.getDecorView()
            r2 = 2131296604(0x7f09015c, float:1.821113E38)
            android.view.View r0 = r0.findViewById(r2)
            android.widget.FrameLayout r0 = (android.widget.FrameLayout) r0
            if (r0 == 0) goto L_0x03ac
            r7.updateViewLinearHeight(r0, r11, r14, r1)
        L_0x03ac:
            androidx.appcompat.app.AppCompatActivity r0 = r7.mActivity
            android.view.Window r0 = r0.getWindow()
            android.view.View r0 = r0.getDecorView()
            r1 = 2131296608(0x7f090160, float:1.8211137E38)
            android.view.View r0 = r0.findViewById(r1)
            r1 = r0
            android.widget.FrameLayout r1 = (android.widget.FrameLayout) r1
            if (r1 == 0) goto L_0x03d0
            r0 = r25
            r2 = r15
            r3 = r16
            r4 = r16
            r5 = r18
            r6 = r18
            r0.updateViewHeight(r1, r2, r3, r4, r5, r6)
        L_0x03d0:
            androidx.appcompat.app.AppCompatActivity r0 = r7.mActivity
            android.view.Window r0 = r0.getWindow()
            android.view.View r0 = r0.getDecorView()
            r1 = 2131296598(0x7f090156, float:1.8211117E38)
            android.view.View r0 = r0.findViewById(r1)
            r1 = r0
            android.widget.FrameLayout r1 = (android.widget.FrameLayout) r1
            if (r1 == 0) goto L_0x03f2
            r3 = 0
            r4 = 0
            r0 = r25
            r2 = r8
            r5 = r17
            r6 = r17
            r0.updateViewHeight(r1, r2, r3, r4, r5, r6)
        L_0x03f2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.uicore.FragmentController.updatePortraitMultiWindow(int):void");
    }

    private int calculateSttActualHeight(int i) {
        if (i == 0) {
            return i;
        }
        int dimensionPixelSize = this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.multi_window_stt_min_height);
        int lineHeightStt = getLineHeightStt();
        int i2 = i - dimensionPixelSize;
        if (i2 < lineHeightStt) {
            return dimensionPixelSize;
        }
        int i3 = i2 % lineHeightStt;
        return i3 != 0 ? i - i3 : i;
    }

    private int getLineHeightStt() {
        TextPaint textPaint = new TextPaint();
        if (VoiceNoteFeature.FLAG_IS_TABLET) {
            textPaint.setTextSize((float) this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.tablet_stt_text_size));
        } else {
            textPaint.setTextSize((float) this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.stt_text_size));
        }
        return textPaint.getFontMetricsInt((Paint.FontMetricsInt) null) + this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.stt_text_view_line_height) + 1;
    }

    private void updateViewHeight(View view, int i) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        layoutParams.height = i;
        view.setLayoutParams(layoutParams);
    }

    private void updateViewHeight(View view, int i, int i2) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        layoutParams.height = i;
        layoutParams.topMargin = i2;
        view.setLayoutParams(layoutParams);
    }

    private void updateViewLinearHeight(View view, int i, int i2, int i3) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        layoutParams.height = i;
        layoutParams.topMargin = i2;
        layoutParams.bottomMargin = i3;
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

    /* access modifiers changed from: private */
    public void updateControlButtonLayout(boolean z) {
        Log.m19d(TAG, "updateControlButtonLayout - show : " + z);
        FrameLayout frameLayout = (FrameLayout) this.mActivity.getWindow().getDecorView().findViewById(C0690R.C0693id.main_controlbutton);
        if (frameLayout != null) {
            updateListLayout(!z);
            if (z) {
                if (frameLayout.getVisibility() != 0) {
                    Log.m19d(TAG, "updateControlButtonLayout - show");
                    frameLayout.setVisibility(0);
                }
            } else if (frameLayout.getVisibility() != 8) {
                Log.m19d(TAG, "updateControlButtonLayout - hide");
                frameLayout.setVisibility(8);
            }
        }
    }

    private void updateListLayout(boolean z) {
        Log.m26i(TAG, "updateListLayout - show: " + z);
        FrameLayout frameLayout = (FrameLayout) this.mActivity.getWindow().getDecorView().findViewById(C0690R.C0693id.main_list);
        if (frameLayout != null) {
            Log.m26i(TAG, "updateListViewLayout - show");
            frameLayout.setVisibility(0);
        }
    }

    private static class RecordScene extends AbsScene {
        RecordScene() {
            super();
            this.mScene = 8;
            this.mTags.add(FragmentController.INFO);
            this.mTags.add("Wave");
            this.mTags.add(FragmentController.BOOKMARK);
            this.mTags.add(FragmentController.CONTROLBUTTON);
        }
    }

    private static class PreRecordScene extends AbsScene {
        PreRecordScene() {
            super();
            this.mScene = 11;
            this.mTags.add(FragmentController.CONTROLBUTTON);
        }
    }

    private static class MainScene extends AbsScene {
        MainScene() {
            super();
            this.mScene = 1;
            this.mTags.add(FragmentController.IDLEBUTTON);
        }
    }

    private static class ListScene extends AbsScene {
        ListScene() {
            super();
            this.mScene = 2;
            this.mTags.add(FragmentController.LIST);
        }
    }

    private static class PlayScene extends AbsScene {
        PlayScene() {
            super();
            this.mScene = 4;
            this.mTags.add(FragmentController.INFO);
            this.mTags.add("Wave");
            this.mTags.add(FragmentController.BOOKMARK);
            this.mTags.add(FragmentController.TOOLBAR);
            this.mTags.add(FragmentController.CONTROLBUTTON);
            this.mTags.add(FragmentController.LIST);
        }
    }

    private static class MiniPlayScene extends AbsScene {
        MiniPlayScene() {
            super();
            this.mScene = 3;
            this.mTags.add(FragmentController.LIST);
        }
    }

    private static class SelectScene extends AbsScene {
        SelectScene() {
            super();
            this.mScene = 5;
            this.mTags.add(FragmentController.LIST);
        }
    }

    private static class PrivateSelectScene extends AbsScene {
        PrivateSelectScene() {
            super();
            this.mScene = 9;
            this.mTags.add(FragmentController.LIST);
        }
    }

    private static class EditScene extends AbsScene {
        EditScene() {
            super();
            this.mScene = 6;
            this.mTags.add(FragmentController.INFO);
            this.mTags.add("Wave");
            this.mTags.add(FragmentController.CONTROLBUTTON);
            this.mTags.add(FragmentController.LIST);
            this.mTags.add(FragmentController.BOOKMARK);
        }
    }

    private static class SearchScene extends AbsScene {
        SearchScene() {
            super();
            this.mScene = 7;
            this.mTags.add(FragmentController.SEARCH);
        }
    }

    private static class SearchSelectScene extends AbsScene {
        SearchSelectScene() {
            super();
            this.mScene = 10;
            this.mTags.add(FragmentController.SEARCH);
        }
    }

    private static class TrashScene extends AbsScene {
        TrashScene() {
            super();
            this.mScene = 13;
            this.mTags.add(FragmentController.TRASH);
        }
    }

    private static class TrashSelectScene extends AbsScene {
        TrashSelectScene() {
            super();
            this.mScene = 14;
            this.mTags.add(FragmentController.TRASH);
        }
    }

    private static class TrashMiniPlayScene extends AbsScene {
        TrashMiniPlayScene() {
            super();
            this.mScene = 15;
            this.mTags.add(FragmentController.TRASH);
        }
    }

    private static class EmptyScene extends AbsScene {
        EmptyScene() {
            super();
            this.mScene = 0;
            this.mTags.clear();
        }
    }

    private static class TranslationScene extends AbsScene {
        TranslationScene() {
            super();
            this.mScene = 12;
            this.mTags.add(FragmentController.INFO);
            this.mTags.add("Wave");
            this.mTags.add(FragmentController.CONTROLBUTTON);
        }
    }

    private static class AbsScene {
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

    private boolean isGuideExist() {
        AppCompatActivity appCompatActivity = this.mActivity;
        if (appCompatActivity == null) {
            return false;
        }
        View decorView = appCompatActivity.getWindow().getDecorView();
        View findViewById = decorView.findViewById(C0690R.C0693id.help_overwrite);
        View findViewById2 = decorView.findViewById(C0690R.C0693id.help_stt_ok_button);
        View findViewById3 = decorView.findViewById(C0690R.C0693id.help_interview_ok_button);
        View findViewById4 = decorView.findViewById(C0690R.C0693id.help_convert_stt_parent);
        if (findViewById == null && findViewById2 == null && findViewById3 == null && findViewById4 == null) {
            return false;
        }
        return true;
    }

    public static void removeAllFragments(AppCompatActivity appCompatActivity) {
        Log.m26i(TAG, "removeAllFragments");
        String[] strArr = {INFO, "Wave", BOOKMARK, CONTROLBUTTON, LIST, TOOLBAR, STT, SEARCH, TRASH};
        FragmentManager supportFragmentManager = appCompatActivity.getSupportFragmentManager();
        for (String findFragmentByTag : strArr) {
            AbsFragment absFragment = (AbsFragment) supportFragmentManager.findFragmentByTag(findFragmentByTag);
            if (absFragment != null) {
                FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
                beginTransaction.remove(absFragment);
                beginTransaction.commitAllowingStateLoss();
            }
        }
    }

    private boolean isBackPossible() {
        boolean z = true;
        for (String str : new String[]{INFO, "Wave", BOOKMARK, CONTROLBUTTON, LIST, TOOLBAR, STT, SEARCH, TRASH}) {
            AbsFragment absFragment = FragmentFactory.get(str);
            if (absFragment != null) {
                z &= absFragment.isBackPossible();
            }
        }
        return z;
    }

    private void dismissHelpGuide() {
        Log.m19d(TAG, "dismissHelpGuide");
        AppCompatActivity appCompatActivity = this.mActivity;
        if (appCompatActivity != null) {
            View findViewById = appCompatActivity.getWindow().getDecorView().findViewById(C0690R.C0693id.main_activity_root_view);
            View findViewById2 = findViewById.findViewById(C0690R.C0693id.help_overwrite);
            if (findViewById2 != null) {
                ((ViewGroup) findViewById).removeView(findViewById2);
                Settings.setSettings(Settings.KEY_HELP_SHOW_OVERWRITE_GUIDE, false);
            }
            View findViewById3 = findViewById.findViewById(C0690R.C0693id.help_convert_stt_parent);
            if (findViewById3 != null) {
                ((ViewGroup) findViewById).removeView(findViewById3);
                Settings.setSettings(Settings.KEY_HELP_SHOW_CONVERT_STT_GUIDE, false);
            }
        }
    }

    public int getCurrentEvent() {
        return this.mCurrentEvent;
    }

    public void setNeedUpdateLayout(boolean z) {
        this.isNeedUpdateLayout = z;
    }

    public boolean isNeedUpdateLayout() {
        return this.isNeedUpdateLayout;
    }

    private boolean isLandscapeMode() {
        int vROrientation = DisplayManager.getVROrientation();
        return vROrientation == 1 || vROrientation == 3;
    }
}
