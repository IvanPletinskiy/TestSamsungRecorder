package com.sec.android.app.voicenote.p007ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.animation.PathInterpolatorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DataRepository;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.main.VNMainActivity;
import com.sec.android.app.voicenote.p007ui.adapter.ListAdapter;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.provider.CheckedItemProvider;
import com.sec.android.app.voicenote.provider.ContextMenuProvider;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.MouseKeyboardProvider;
import com.sec.android.app.voicenote.provider.PhoneStateProvider;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.service.AudioFormat;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.uicore.FragmentController;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import java.util.ArrayList;
import java.util.Locale;

/* renamed from: com.sec.android.app.voicenote.ui.AbsListFragment */
public class AbsListFragment extends AbsFragment implements CursorProvider.OnCursorChangeListener, Engine.OnEngineListener, ListAdapter.OnItemClickListener, FragmentController.OnSceneChangeListener, SeekBar.OnSeekBarChangeListener {
    private static final String CHILD_LIST_TAG = "child_list";
    private static final int DURATION_THRESHOLD_AMR = 180000;
    private static final int DURATION_THRESHOLD_M4A = 10800000;
    private final int SineInOut33 = 1;
    private final int SineInOut80 = 3;
    /* access modifiers changed from: private */
    public String TAG;
    private AnimatorSet mAnimationSet = null;
    /* access modifiers changed from: private */
    public BottomNavigationView mBottomNavigationView;
    private Handler mEngineEventHandler = null;
    /* access modifiers changed from: private */
    public int mExpandedPosition = -1;
    /* access modifiers changed from: private */
    public boolean mIsScrolledToNextPlaying = false;
    private int mItemHeight;
    /* access modifiers changed from: private */
    public int mLastPosSelected = -1;
    protected ListAdapter mListAdapter = null;
    private LinearLayout mListLayout;
    private boolean mNoNeedScrollToTop = false;
    private boolean mPauseBySeek = false;
    private PlayTask mPlayTask = null;
    /* access modifiers changed from: private */
    public ProgressBar mProgressBar;
    /* access modifiers changed from: private */
    public RecyclerView mRecyclerView = null;
    private RoundedDecoration mRoundedDecoration;
    /* access modifiers changed from: private */
    public int mScene = 2;
    /* access modifiers changed from: private */
//    public SeslRoundedCorner mSeslListRoundedCorner;
    private Handler mTaskEventHandler = null;
    private View mViewMarginBottom;

    public void onCreate(Bundle bundle) {
        Log.m26i(this.TAG, "onCreate");
        super.onCreate(bundle);
        this.mEngineEventHandler = new Handler(new Handler.Callback() {
            public final boolean handleMessage(Message message) {
                return AbsListFragment.this.lambda$onCreate$0$AbsListFragment(message);
            }
        });
        this.mTaskEventHandler = new Handler(new Handler.Callback() {
            public final boolean handleMessage(Message message) {
                return AbsListFragment.this.lambda$onCreate$1$AbsListFragment(message);
            }
        });
    }

    public /* synthetic */ boolean lambda$onCreate$0$AbsListFragment(Message message) {
        View childAt;
        View childAt2;
        if (getActivity() != null && isAdded() && !isRemoving()) {
            switch (message.what) {
                case 2010:
                case 2012:
                    if (Engine.getInstance().getPlayerState() != 1) {
                        int currentPlayingPosition = CursorProvider.getInstance().getCurrentPlayingPosition();
                        PlayTask playTask = this.mPlayTask;
                        if (playTask == null || playTask.mPosition == currentPlayingPosition) {
                            int duration = Engine.getInstance().getDuration();
                            int currentTime = Engine.getInstance().getCurrentTime();
                            this.mListAdapter.setSeekBarValue(duration, currentTime);
                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) this.mRecyclerView.getLayoutManager();
                            int findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                            int findLastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                            if (!(findFirstVisibleItemPosition == -1 || findFirstVisibleItemPosition > currentPlayingPosition || findLastVisibleItemPosition == -1 || findLastVisibleItemPosition < currentPlayingPosition || (childAt = this.mRecyclerView.getChildAt(currentPlayingPosition - findFirstVisibleItemPosition)) == null)) {
                                SeekBar seekBar = (SeekBar) childAt.findViewById(C0690R.C0693id.listrow_seekbar);
                                seekBar.setMax(duration);
                                seekBar.setProgress(currentTime);
                                seekBar.setOnSeekBarChangeListener(this);
                                TextView textView = (TextView) childAt.findViewById(C0690R.C0693id.listrow_position);
                                if (message.what == 2012) {
                                    String str = stringForTime(message.arg1) + " / " + stringForTime(duration);
                                    if (!str.equals(textView.getText())) {
                                        textView.setText(str);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    break;
                case 2011:
                    int duration2 = Engine.getInstance().getDuration();
                    int currentPlayingPosition2 = CursorProvider.getInstance().getCurrentPlayingPosition();
                    this.mListAdapter.setSeekBarValue(Engine.getInstance().getDuration(), 0);
                    LinearLayoutManager linearLayoutManager2 = (LinearLayoutManager) this.mRecyclerView.getLayoutManager();
                    int findFirstVisibleItemPosition2 = linearLayoutManager2.findFirstVisibleItemPosition();
                    int findLastVisibleItemPosition2 = linearLayoutManager2.findLastVisibleItemPosition();
                    if (!(findFirstVisibleItemPosition2 == -1 || findFirstVisibleItemPosition2 > currentPlayingPosition2 || findLastVisibleItemPosition2 == -1 || findLastVisibleItemPosition2 < currentPlayingPosition2 || (childAt2 = this.mRecyclerView.getChildAt(currentPlayingPosition2 - findFirstVisibleItemPosition2)) == null)) {
                        this.mListAdapter.changePlayerIcon(4, (ListAdapter.ViewHolder) this.mRecyclerView.getChildViewHolder(childAt2));
                        ((TextView) childAt2.findViewById(C0690R.C0693id.listrow_position)).setText(stringForTime(message.arg1) + " / " + stringForTime(duration2));
                        break;
                    }
            }
        }
        return false;
    }

    public /* synthetic */ boolean lambda$onCreate$1$AbsListFragment(Message message) {
        if (getActivity() != null && isAdded() && !isRemoving()) {
            this.mTaskEventHandler.removeMessages(message.what);
            int i = message.what;
            if (i == -119) {
                Toast.makeText(getActivity(), C0690R.string.please_wait, 0).show();
            } else if (i == -115) {
                Toast.makeText(getActivity(), C0690R.string.playback_failed_msg, 0).show();
            } else if (i == -103) {
                Toast.makeText(getActivity(), C0690R.string.no_play_during_call, 0).show();
            } else if (i == 2001) {
                startPlayFromDC(message.arg1, false, i);
            } else if (i == 3001) {
                startPlayFromDC(message.arg1, true, i);
            }
        }
        return false;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Log.m26i(this.TAG, "onCreateView");
        View inflate = layoutInflater.inflate(C0690R.layout.fragment_list, viewGroup, false);
        this.mListLayout = (LinearLayout) inflate.findViewById(C0690R.C0693id.layout_list);
        updateLayoutInTabletMultiWindow(getActivity());
        this.mViewMarginBottom = inflate.findViewById(C0690R.C0693id.view_margin_bottom);
        this.mProgressBar = (ProgressBar) inflate.findViewById(C0690R.C0693id.list_progressbar);
        this.mRecyclerView = (RecyclerView) inflate.findViewById(C0690R.C0693id.list);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.mBottomNavigationView = (BottomNavigationView) getActivity().findViewById(C0690R.C0693id.bottom_navigation);
        this.mRecyclerView.setBackgroundResource(C0690R.C0691color.main_window_bg);
        this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                super.onScrollStateChanged(recyclerView, i);
                if (i == 1) {
                    AbsListFragment.this.postEvent(Event.MINIMIZE_SIP);
                }
            }

            public void onScrolled(@NonNull RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
                int currentPlayingPosition = CursorProvider.getInstance().getCurrentPlayingPosition();
                if (AbsListFragment.this.mIsScrolledToNextPlaying) {
                    AbsListFragment.this.setMiniPlayNext(currentPlayingPosition);
                    boolean unused = AbsListFragment.this.mIsScrolledToNextPlaying = false;
                }
            }
        });
        setPenSelectMode();
        setLongPressMultiSelection();
        addOnItemTouchListener();
//        this.mRecyclerView.seslSetGoToTopEnabled(true);
//        this.mRecyclerView.seslSetGoToTopBottomPadding(getResources().getDimensionPixelOffset(C0690R.dimen.go_to_top_bottom_padding));
//        this.mSeslListRoundedCorner = new SeslRoundedCorner(getContext());
//        this.mSeslListRoundedCorner.setRoundedCorners(15);
        this.mRoundedDecoration = new RoundedDecoration();
        this.mRecyclerView.addItemDecoration(this.mRoundedDecoration);
        setNestedScrollRecyclerView(false);
        if (this.mListAdapter != null) {
            CursorProvider.getInstance().close();
        }
        this.mListAdapter = new ListAdapter(getActivity(), (Cursor) null);
        this.mListAdapter.setHasStableIds(true);
        this.mListAdapter.registerListener(this);
        FragmentController.getInstance().registerSceneChangeListener(this);
        FragmentController.getInstance().registerSceneChangeListener(this.mListAdapter);
        this.mRecyclerView.setAdapter(this.mListAdapter);
        CursorProvider.getInstance().registerCursorChangeListener(this);
        CursorProvider.getInstance().load(getLoaderManager());
        this.mRecyclerView.scrollTo(0, 0);
        setScrollbarPosition();
//        this.mRecyclerView.seslSetFastScrollerEnabled(true);
        this.mRecyclerView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.mScene = VoiceNoteApplication.getScene();
        int i = this.mScene;
        if (i == 4 || i == 6) {
            changeListVisibility(false);
        } else {
            changeListVisibility(true);
        }
        MouseKeyboardProvider.getInstance().mouseClickInteraction(getActivity(), this, this.mRecyclerView);
        this.mProgressBar.setOnTouchListener(new View.OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return AbsListFragment.this.lambda$onCreateView$2$AbsListFragment(view, motionEvent);
            }
        });
        return inflate;
    }

    public /* synthetic */ boolean lambda$onCreateView$2$AbsListFragment(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() != 11 || Engine.getInstance().getPlayerState() != 3) {
            return false;
        }
        this.mPauseBySeek = true;
        Engine.getInstance().pausePlay();
        return false;
    }

    private void updateLayoutInTabletMultiWindow(Activity activity) {
        if (VoiceNoteFeature.FLAG_IS_TABLET && this.mListLayout != null && DisplayManager.isCurrentWindowOnLandscape(activity)) {
            if ((DisplayManager.getMultiwindowMode() == 2 && getResources().getConfiguration().densityDpi < 480) || ((DisplayManager.getMultiwindowMode() != 2 && getResources().getConfiguration().densityDpi >= 480) || (DisplayManager.getMultiwindowMode() == 1 && getResources().getConfiguration().screenWidthDp < 960))) {
                int i = (int) (((double) getResources().getDisplayMetrics().widthPixels) * 0.05d);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -2);
                layoutParams.leftMargin = i;
                layoutParams.rightMargin = i;
                this.mListLayout.setLayoutParams(layoutParams);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void setTAG(String str) {
        this.TAG = str;
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        Engine.getInstance().registerListener(this);
    }

    public void onStart() {
        super.onStart();
        Log.m26i(this.TAG, "onStart");
        Cursor cursor = this.mListAdapter.getCursor();
        if (cursor != null && cursor.isClosed()) {
            Log.m32w(this.TAG, "onStart - cursor is closed !!");
            CursorProvider.getInstance().reload(getLoaderManager());
        }
        if (CursorProvider.getInstance().getCurrentPlayingPosition() >= 0) {
            PlayTask playTask = this.mPlayTask;
            if (playTask != null && playTask.mPosition == CursorProvider.getInstance().getCurrentPlayingPosition()) {
                return;
            }
            if (CursorProvider.getInstance().getCurrentPlayingPosition() >= 0) {
                updatePlayTask();
            } else {
                this.mPlayTask = null;
            }
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.mListAdapter;
        if (listAdapter != null) {
            listAdapter.updateTimeFormat();
        }
        if (this.mScene != 2) {
            return;
        }
        if (Settings.getIntSettings(Settings.KEY_LIST_MODE, 0) == 0 || DataRepository.getInstance().getCategoryRepository().isChildList()) {
            DialogFactory.clearDialogByTag(getFragmentManager(), DialogFactory.DELETE_DIALOG);
            DialogFactory.clearDialogByTag(getFragmentManager(), DialogFactory.CATEGORY_RENAME);
            DialogFactory.clearDialogByTag(getFragmentManager(), DialogFactory.RENAME_DIALOG);
            DialogFactory.clearDialogByTag(getFragmentManager(), DialogFactory.RECORD_CANCEL_DIALOG);
            DialogFactory.clearDialogByTag(getFragmentManager(), DialogFactory.EDIT_SAVE_DIALOG);
            DialogFactory.clearDialogByTag(getFragmentManager(), DialogFactory.TRANSLATION_CANCEL_DIALOG);
            DialogFactory.clearDialogByTag(getFragmentManager(), DialogFactory.EDIT_CANCEL_DIALOG);
        }
    }

    public void onStop() {
        super.onStop();
        Log.m26i(this.TAG, "onStop");
    }

    public void onDestroyView() {
        SeekBar seekBar;
        Log.m26i(this.TAG, "onDestroyView");
        changeListVisibility(false);
        Engine.getInstance().unregisterListener(this);
        if (this.mRecyclerView != null) {
            MouseKeyboardProvider.getInstance().destroyMouseClickInteraction(this, this.mRecyclerView);
            if (!(getView() == null || (seekBar = (SeekBar) getView().findViewById(C0690R.C0693id.listrow_seekbar)) == null)) {
//                seekBar.semSetOnSeekBarHoverListener((SeekBar.SemOnSeekBarHoverListener) null);
            }
        }
        FragmentController.getInstance().unregisterSceneChangeListener(this.mListAdapter);
        FragmentController.getInstance().unregisterSceneChangeListener(this);
        CursorProvider.getInstance().unregisterCursorChangeListener(this);
        ProgressBar progressBar = this.mProgressBar;
        if (progressBar != null) {
            progressBar.setOnTouchListener((View.OnTouchListener) null);
            this.mProgressBar = null;
        }
        super.onDestroyView();
    }

    public void onDestroy() {
        Log.m26i(this.TAG, "onDestroy");
        this.mEngineEventHandler = null;
        this.mTaskEventHandler = null;
        super.onDestroy();
    }

    public void onDetach() {
        Log.m26i(this.TAG, "onDetach");
        AnimatorSet animatorSet = this.mAnimationSet;
        if (animatorSet != null) {
            if (animatorSet.isRunning()) {
                this.mAnimationSet.cancel();
            }
            this.mAnimationSet = null;
        }
        RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView != null) {
            int childCount = recyclerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                SeekBar seekBar = (SeekBar) this.mRecyclerView.getChildAt(i).findViewById(C0690R.C0693id.listrow_seekbar);
                ListAdapter listAdapter = this.mListAdapter;
                if (listAdapter != null) {
                    listAdapter.setProgressHoverWindow(seekBar, false);
                }
                seekBar.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) null);
            }
            this.mRecyclerView.setAdapter((RecyclerView.Adapter) null);
            CursorProvider.getInstance().unregisterCursorChangeListener(this);
            this.mRecyclerView = null;
        }
        ListAdapter listAdapter2 = this.mListAdapter;
        if (listAdapter2 != null) {
            listAdapter2.onDestroy();
            this.mListAdapter.registerListener((ListAdapter.OnItemClickListener) null);
            this.mListAdapter = null;
        }
        super.onDetach();
    }

    public void setNestedScrollRecyclerView(boolean z) {
        RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView != null) {
            recyclerView.setNestedScrollingEnabled(z);
        }
    }

    /* access modifiers changed from: private */
    public void setMiniPlayNext(int i) {
        PlayTask playTask = this.mPlayTask;
        if (playTask == null) {
            return;
        }
        if (playTask.mPosition != i) {
            this.mPlayTask.updatePosition(i);
            if (isResumed()) {
                setExpandListAnimation(i);
            } else {
                updateExpandListValue();
                notifyDataSetChangedToAdapter();
            }
            postEvent(Event.HIDE_SIP);
        } else if (this.mListAdapter != null) {
            if (Settings.getBooleanSettings(Settings.KEY_PLAY_CONTINUOUSLY, false) && this.mListAdapter.getItemCount() == 1) {
                doAnimation(this.mRecyclerView.getChildAt(0), 0);
            }
            this.mListAdapter.notifyDataSetChanged();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0087, code lost:
        r0 = com.sec.android.app.voicenote.provider.CursorProvider.getInstance().getCurrentPlayingPosition();
        r1 = (androidx.recyclerview.widget.LinearLayoutManager) r8.mRecyclerView.getLayoutManager();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x009b, code lost:
        if (r0 < r1.findFirstVisibleItemPosition()) goto L_0x00a9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00a1, code lost:
        if (r0 <= r1.findLastVisibleItemPosition()) goto L_0x00a4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00a4, code lost:
        setMiniPlayNext(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00a9, code lost:
        r8.mRecyclerView.scrollToPosition(r0);
        r8.mIsScrolledToNextPlaying = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00b2, code lost:
        r2 = com.sec.android.app.voicenote.provider.CursorProvider.getInstance().getCurrentPlayingPosition();
        r3 = (androidx.recyclerview.widget.LinearLayoutManager) r8.mRecyclerView.getLayoutManager();
        r4 = r3.findFirstVisibleItemPosition();
        r3 = r3.findLastVisibleItemPosition();
        r8.mListAdapter.setSeekBarValue(com.sec.android.app.voicenote.service.Engine.getInstance().getDuration(), com.sec.android.app.voicenote.service.Engine.getInstance().getCurrentTime());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00df, code lost:
        if (r4 == -1) goto L_0x018a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00e1, code lost:
        if (r4 > r2) goto L_0x018a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00e3, code lost:
        if (r3 == -1) goto L_0x018a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00e5, code lost:
        if (r3 < r2) goto L_0x018a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00e7, code lost:
        r1 = r8.mRecyclerView.getChildAt(r2 - r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00ee, code lost:
        if (r1 == null) goto L_0x018a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00f2, code lost:
        if (r9 == 3002) goto L_0x0108;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00f6, code lost:
        if (r9 != 6002) goto L_0x00f9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00f9, code lost:
        r8.mListAdapter.changePlayerIcon(3, (com.sec.android.app.voicenote.p007ui.adapter.ListAdapter.ViewHolder) r8.mRecyclerView.getChildViewHolder(r1));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x0108, code lost:
        r8.mListAdapter.changePlayerIcon(4, (com.sec.android.app.voicenote.p007ui.adapter.ListAdapter.ViewHolder) r8.mRecyclerView.getChildViewHolder(r1));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x0118, code lost:
        moveToPlayingPosition();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onUpdate(java.lang.Object r9) {
        /*
            r8 = this;
            java.lang.String r0 = r8.TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "onUpdate : "
            r1.append(r2)
            r1.append(r9)
            java.lang.String r1 = r1.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r0, r1)
            java.lang.Integer r9 = (java.lang.Integer) r9
            int r9 = r9.intValue()
            com.sec.android.app.voicenote.ui.adapter.ListAdapter r0 = r8.mListAdapter
            if (r0 != 0) goto L_0x0028
            java.lang.String r9 = r8.TAG
            java.lang.String r0 = "onUpdate - ListAdapter is null"
            com.sec.android.app.voicenote.provider.Log.m32w((java.lang.String) r9, (java.lang.String) r0)
            return
        L_0x0028:
            android.database.Cursor r0 = r0.getCursor()
            if (r0 == 0) goto L_0x0193
            boolean r0 = r0.isClosed()
            if (r0 == 0) goto L_0x0036
            goto L_0x0193
        L_0x0036:
            r0 = 3
            r1 = -1
            if (r9 == r0) goto L_0x0174
            r2 = 951(0x3b7, float:1.333E-42)
            if (r9 == r2) goto L_0x0154
            r2 = 963(0x3c3, float:1.35E-42)
            if (r9 == r2) goto L_0x0147
            r2 = 991(0x3df, float:1.389E-42)
            if (r9 == r2) goto L_0x018a
            r2 = 993(0x3e1, float:1.391E-42)
            r3 = 1
            if (r9 == r2) goto L_0x0144
            r2 = 1004(0x3ec, float:1.407E-42)
            if (r9 == r2) goto L_0x0140
            r2 = 6
            if (r9 == r2) goto L_0x0137
            r2 = 7
            if (r9 == r2) goto L_0x0121
            r2 = 13
            if (r9 == r2) goto L_0x0137
            r2 = 14
            if (r9 == r2) goto L_0x0121
            r2 = 984(0x3d8, float:1.379E-42)
            if (r9 == r2) goto L_0x011d
            r2 = 985(0x3d9, float:1.38E-42)
            if (r9 == r2) goto L_0x011d
            switch(r9) {
                case 2001: goto L_0x0140;
                case 2002: goto L_0x0140;
                case 2003: goto L_0x0140;
                case 2004: goto L_0x0140;
                default: goto L_0x0068;
            }
        L_0x0068:
            switch(r9) {
                case 3001: goto L_0x0118;
                case 3002: goto L_0x00b2;
                case 3003: goto L_0x00b2;
                case 3004: goto L_0x0087;
                case 3005: goto L_0x0087;
                default: goto L_0x006b;
            }
        L_0x006b:
            switch(r9) {
                case 6001: goto L_0x0118;
                case 6002: goto L_0x00b2;
                case 6003: goto L_0x00b2;
                case 6004: goto L_0x0080;
                default: goto L_0x006e;
            }
        L_0x006e:
            switch(r9) {
                case 6008: goto L_0x0073;
                case 6009: goto L_0x0087;
                case 6010: goto L_0x0087;
                default: goto L_0x0071;
            }
        L_0x0071:
            goto L_0x018a
        L_0x0073:
            com.sec.android.app.voicenote.provider.CursorProvider r0 = com.sec.android.app.voicenote.provider.CursorProvider.getInstance()
            androidx.loader.app.LoaderManager r1 = r8.getLoaderManager()
            r0.reload(r1)
            goto L_0x018a
        L_0x0080:
            r8.notifyDataSetChangedToAdapter()
            r8.mExpandedPosition = r1
            goto L_0x018a
        L_0x0087:
            com.sec.android.app.voicenote.provider.CursorProvider r0 = com.sec.android.app.voicenote.provider.CursorProvider.getInstance()
            int r0 = r0.getCurrentPlayingPosition()
            androidx.recyclerview.widget.RecyclerView r1 = r8.mRecyclerView
            androidx.recyclerview.widget.RecyclerView$LayoutManager r1 = r1.getLayoutManager()
            androidx.recyclerview.widget.LinearLayoutManager r1 = (androidx.recyclerview.widget.LinearLayoutManager) r1
            int r2 = r1.findFirstVisibleItemPosition()
            if (r0 < r2) goto L_0x00a9
            int r1 = r1.findLastVisibleItemPosition()
            if (r0 <= r1) goto L_0x00a4
            goto L_0x00a9
        L_0x00a4:
            r8.setMiniPlayNext(r0)
            goto L_0x018a
        L_0x00a9:
            androidx.recyclerview.widget.RecyclerView r1 = r8.mRecyclerView
            r1.scrollToPosition(r0)
            r8.mIsScrolledToNextPlaying = r3
            goto L_0x018a
        L_0x00b2:
            com.sec.android.app.voicenote.provider.CursorProvider r2 = com.sec.android.app.voicenote.provider.CursorProvider.getInstance()
            int r2 = r2.getCurrentPlayingPosition()
            androidx.recyclerview.widget.RecyclerView r3 = r8.mRecyclerView
            androidx.recyclerview.widget.RecyclerView$LayoutManager r3 = r3.getLayoutManager()
            androidx.recyclerview.widget.LinearLayoutManager r3 = (androidx.recyclerview.widget.LinearLayoutManager) r3
            int r4 = r3.findFirstVisibleItemPosition()
            int r3 = r3.findLastVisibleItemPosition()
            com.sec.android.app.voicenote.service.Engine r5 = com.sec.android.app.voicenote.service.Engine.getInstance()
            int r5 = r5.getDuration()
            com.sec.android.app.voicenote.service.Engine r6 = com.sec.android.app.voicenote.service.Engine.getInstance()
            int r6 = r6.getCurrentTime()
            com.sec.android.app.voicenote.ui.adapter.ListAdapter r7 = r8.mListAdapter
            r7.setSeekBarValue(r5, r6)
            if (r4 == r1) goto L_0x018a
            if (r4 > r2) goto L_0x018a
            if (r3 == r1) goto L_0x018a
            if (r3 < r2) goto L_0x018a
            androidx.recyclerview.widget.RecyclerView r1 = r8.mRecyclerView
            int r2 = r2 - r4
            android.view.View r1 = r1.getChildAt(r2)
            if (r1 == 0) goto L_0x018a
            r2 = 3002(0xbba, float:4.207E-42)
            if (r9 == r2) goto L_0x0108
            r2 = 6002(0x1772, float:8.41E-42)
            if (r9 != r2) goto L_0x00f9
            goto L_0x0108
        L_0x00f9:
            com.sec.android.app.voicenote.ui.adapter.ListAdapter r2 = r8.mListAdapter
            androidx.recyclerview.widget.RecyclerView r3 = r8.mRecyclerView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r3.getChildViewHolder(r1)
            com.sec.android.app.voicenote.ui.adapter.ListAdapter$ViewHolder r1 = (com.sec.android.app.voicenote.p007ui.adapter.ListAdapter.ViewHolder) r1
            r2.changePlayerIcon(r0, r1)
            goto L_0x018a
        L_0x0108:
            com.sec.android.app.voicenote.ui.adapter.ListAdapter r0 = r8.mListAdapter
            r2 = 4
            androidx.recyclerview.widget.RecyclerView r3 = r8.mRecyclerView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r3.getChildViewHolder(r1)
            com.sec.android.app.voicenote.ui.adapter.ListAdapter$ViewHolder r1 = (com.sec.android.app.voicenote.p007ui.adapter.ListAdapter.ViewHolder) r1
            r0.changePlayerIcon(r2, r1)
            goto L_0x018a
        L_0x0118:
            r8.moveToPlayingPosition()
            goto L_0x018a
        L_0x011d:
            r8.notifyDataSetChangedToAdapter()
            goto L_0x018a
        L_0x0121:
            com.sec.android.app.voicenote.provider.CheckedItemProvider.initCheckedList()
            com.sec.android.app.voicenote.ui.adapter.ListAdapter r0 = r8.mListAdapter
            r1 = 0
            r0.setSelectionMode(r1)
            r8.notifyDataSetChangedToAdapter()
            androidx.fragment.app.FragmentManager r0 = r8.getFragmentManager()
            java.lang.String r1 = "DeleteDialog"
            com.sec.android.app.voicenote.p007ui.dialog.DialogFactory.clearDialogByTag(r0, r1)
            goto L_0x018a
        L_0x0137:
            com.sec.android.app.voicenote.ui.adapter.ListAdapter r0 = r8.mListAdapter
            r0.setSelectionMode(r3)
            r8.notifyDataSetChangedToAdapter()
            goto L_0x018a
        L_0x0140:
            r8.notifyDataSetChangedToAdapter()
            goto L_0x018a
        L_0x0144:
            r8.mNoNeedScrollToTop = r3
            goto L_0x018a
        L_0x0147:
            com.sec.android.app.voicenote.provider.CursorProvider r0 = com.sec.android.app.voicenote.provider.CursorProvider.getInstance()
            androidx.loader.app.LoaderManager r1 = r8.getLoaderManager()
            r2 = 0
            r0.query(r1, r2)
            goto L_0x018a
        L_0x0154:
            androidx.recyclerview.widget.RecyclerView r0 = r8.mRecyclerView
            if (r0 == 0) goto L_0x018a
            androidx.recyclerview.widget.RecyclerView$LayoutManager r0 = r0.getLayoutManager()
            androidx.recyclerview.widget.LinearLayoutManager r0 = (androidx.recyclerview.widget.LinearLayoutManager) r0
            int r1 = r0.findLastVisibleItemPosition()
            int r0 = r0.findLastCompletelyVisibleItemPosition()
            int r2 = r8.mLastPosSelected
            if (r1 == r2) goto L_0x016c
            if (r0 != r2) goto L_0x018a
        L_0x016c:
            androidx.recyclerview.widget.RecyclerView r0 = r8.mRecyclerView
            int r1 = r8.mLastPosSelected
            r0.scrollToPosition(r1)
            goto L_0x018a
        L_0x0174:
            androidx.recyclerview.widget.RecyclerView r0 = r8.mRecyclerView
            float r0 = r0.getAlpha()
            r2 = 1065353216(0x3f800000, float:1.0)
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x0185
            androidx.recyclerview.widget.RecyclerView r0 = r8.mRecyclerView
            r0.setAlpha(r2)
        L_0x0185:
            r8.notifyDataSetChangedToAdapter()
            r8.mExpandedPosition = r1
        L_0x018a:
            boolean r0 = com.sec.android.app.voicenote.provider.Event.isConvertibleEvent(r9)
            if (r0 == 0) goto L_0x0192
            r8.mCurrentEvent = r9
        L_0x0192:
            return
        L_0x0193:
            java.lang.String r9 = r8.TAG
            java.lang.String r0 = "onUpdate - cursor is null or closed so reload it"
            com.sec.android.app.voicenote.provider.Log.m32w((java.lang.String) r9, (java.lang.String) r0)
            com.sec.android.app.voicenote.provider.CursorProvider r9 = com.sec.android.app.voicenote.provider.CursorProvider.getInstance()
            androidx.loader.app.LoaderManager r0 = r8.getLoaderManager()
            r9.reload(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.AbsListFragment.onUpdate(java.lang.Object):void");
    }

    private void startPlayFromDC(int i, boolean z, int i2) {
        String str = this.TAG;
        Log.m19d(str, "startPlayFromDC position : " + i + " miniMode : " + z);
        View childAt = this.mRecyclerView.getChildAt(i - ((LinearLayoutManager) this.mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition());
        if (childAt != null && this.mListAdapter.getItemId(i) >= 0) {
            if (z) {
                onHeaderClick(childAt, i);
            } else {
                childAt.performClick();
            }
        }
    }

    public void onEngineUpdate(int i, int i2, int i3) {
        Handler handler = this.mEngineEventHandler;
        if (handler != null) {
            handler.sendMessage(handler.obtainMessage(i, i2, i3));
        }
    }

    public void onCursorChanged(Cursor cursor, boolean z) {
        Log.m26i(this.TAG, "onCursorChanged");
        VNMainActivity vNMainActivity = null;
        if (isRemoving() || getActivity() == null) {
            Log.m22e(this.TAG, "onCursorChanged - removing or getActivity is null");
            this.mRecyclerView.setAdapter((RecyclerView.Adapter) null);
        } else if (cursor == null || cursor.isClosed()) {
            Log.m22e(this.TAG, "onCursorChanged - cursor is closed");
        } else if (this.mListAdapter == null) {
            Log.m22e(this.TAG, "onCursorChanged - mListAdapter is null");
        } else {
            if (cursor.getCount() == 0) {
                int i = this.mScene;
                if (!(i == 4 || i == 6)) {
                    showEmptyView(true);
                }
                if (this.mScene != 7) {
                    postEvent(Event.INVALIDATE_MENU);
                }
            } else {
                showEmptyView(false);
            }
            this.mListAdapter.swapCursor(cursor);
            if (!z && !this.mNoNeedScrollToTop) {
                this.mRecyclerView.postDelayed(new Runnable() {
                    public final void run() {
                        AbsListFragment.this.lambda$onCursorChanged$3$AbsListFragment();
                    }
                }, 300);
            }
            this.mNoNeedScrollToTop = false;
            long id = Engine.getInstance().getID();
            if (id != -1) {
                int itemCount = this.mListAdapter.getItemCount();
                for (int i2 = 0; i2 < itemCount; i2++) {
                    if (this.mListAdapter.getItemId(i2) == id) {
                        CursorProvider.getInstance().setCurrentPlayingItemPosition(i2);
                        PlayTask playTask = this.mPlayTask;
                        if (playTask != null) {
                            playTask.updatePosition(i2);
                        }
                        updateExpandListValue();
                    }
                }
            }
            ComponentActivity activity = getActivity();
            if (activity instanceof VNMainActivity) {
                vNMainActivity = (VNMainActivity) activity;
            }
            if (activity != null && vNMainActivity != null && vNMainActivity.isActivityResumed() && this.mScene != 7) {
                activity.invalidateOptionsMenu();
                int i3 = this.mScene;
                if (i3 == 5 || i3 == 9 || i3 == 10) {
                    postEvent(Event.INVALIDATE_MENU);
                }
            }
        }
    }

    public /* synthetic */ void lambda$onCursorChanged$3$AbsListFragment() {
        this.mRecyclerView.smoothScrollToPosition(0);
    }

    public void onCursorLoadFail() {
        CursorProvider.getInstance().reload(getLoaderManager());
    }

    public void notifyDataSetChanged(Cursor cursor) {
        Log.m26i(this.TAG, "notifyDataSetChanged !!");
        int i = this.mScene;
        if (i == 5) {
            postEvent(7);
        } else if (i == 10) {
            postEvent(14);
        }
    }

    public void notifyDataSetInvalidated(Cursor cursor) {
        Log.m26i(this.TAG, "notifyDataSetInvalidated !!");
        this.mListAdapter.swapCursor((Cursor) null);
    }

    private void setPenSelectMode() {
//        this.mRecyclerView.seslSetOnMultiSelectedListener(new RecyclerView.SeslOnMultiSelectedListener() {
//            private int mEndPosition;
//            private int mStartPosition;
//
//            public void onMultiSelected(RecyclerView recyclerView, View view, int i, long j) {
//            }
//
//            public void onMultiSelectStart(int i, int i2) {
//                this.mStartPosition = AbsListFragment.this.mRecyclerView.getChildLayoutPosition(AbsListFragment.this.mRecyclerView.findChildViewUnder((float) i, (float) i2));
//            }
//
//            public void onMultiSelectStop(int i, int i2) {
//                this.mEndPosition = AbsListFragment.this.mRecyclerView.getChildLayoutPosition(AbsListFragment.this.mRecyclerView.findChildViewUnder((float) i, (float) i2));
//                if (this.mStartPosition != -1 || this.mEndPosition != -1 || i2 < 0) {
//                    if (this.mStartPosition == -1) {
//                        this.mStartPosition = AbsListFragment.this.mListAdapter.getItemCount() - 1;
//                    }
//                    if (this.mEndPosition == -1) {
//                        if (i2 < 0) {
//                            this.mEndPosition = 0;
//                        } else {
//                            this.mEndPosition = ((LinearLayoutManager) AbsListFragment.this.mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
//                        }
//                    }
//                    int i3 = this.mStartPosition;
//                    int i4 = this.mEndPosition;
//                    if (i3 > i4) {
//                        i3 = i4;
//                    }
//                    int i5 = this.mStartPosition;
//                    int i6 = this.mEndPosition;
//                    if (i5 <= i6) {
//                        i5 = i6;
//                    }
//                    if (i5 >= 0) {
//                        if (i3 < 0) {
//                            i3 = 0;
//                        }
//                        if (Settings.getIntSettings(Settings.KEY_LIST_MODE, 0) != 1 || DataRepository.getInstance().getCategoryRepository().isChildList()) {
//                            if (!(AbsListFragment.this.mScene == 5 || AbsListFragment.this.mScene == 10)) {
//                                CheckedItemProvider.initCheckedList();
//                                if (AbsListFragment.this.mScene == 7) {
//                                    AbsListFragment.this.postEvent(13);
//                                } else {
//                                    AbsListFragment.this.postEvent(6);
//                                }
//                            }
//                            if (Engine.getInstance().getPlayerState() != 1) {
//                                Engine.getInstance().stopPlay();
//                            }
//                            while (i3 <= i5) {
//                                CheckedItemProvider.toggle(AbsListFragment.this.mListAdapter.getItemId(i3));
//                                i3++;
//                            }
//                            AbsListFragment.this.notifyDataSetChangedToAdapter();
//                            AbsListFragment.this.postEvent(Event.SELECT);
//                            if (AbsListFragment.this.mBottomNavigationView != null && AbsListFragment.this.mBottomNavigationView.getVisibility() == 8) {
//                                AbsListFragment.this.postEvent(Event.SHOW_BOTTOM_NAVIGATION_BAR);
//                            }
//                        }
//                    }
//                }
//            }
//        });
    }

    public void onItemClick(View view, int i, long j) {
        SeekBar seekBar;
        String str = this.TAG;
        Log.m26i(str, "onItemClick - position : " + i + " id : " + j);
        if (Engine.getInstance().getRecorderState() != 1) {
            String str2 = this.TAG;
            Log.m26i(str2, "onItemClick - recorder is not idle, recorderState : " + Engine.getInstance().getRecorderState());
            return;
        }
        AnimatorSet animatorSet = this.mAnimationSet;
        if (animatorSet != null && animatorSet.isRunning()) {
            Log.m26i(this.TAG, "animation is running ");
        } else if (this.mListAdapter == null) {
            Log.m22e(this.TAG, "onItemClick - mListAdapter is null");
        } else {
            int i2 = this.mScene;
            if (i2 == 10 || i2 == 5 || i2 == 9) {
                CheckedItemProvider.toggle(j);
                this.mLastPosSelected = i;
                if (this.mBottomNavigationView.getVisibility() == 8) {
                    postEvent(Event.SHOW_BOTTOM_NAVIGATION_BAR);
                }
                postEvent(Event.SELECT);
                view.setActivated(CheckedItemProvider.isChecked(j));
                return;
            }
            PlayTask playTask = this.mPlayTask;
            if ((playTask != null && playTask.isRunning()) || this.mRecyclerView.getVisibility() != 0) {
                Log.m26i(this.TAG, "PlayTask is running or mRecyclerView is not visible, return this operation");
            } else if (!PhoneStateProvider.getInstance().isCallIdle(getActivity())) {
                Log.m26i(this.TAG, "onItemClick Call is not idle");
                Toast.makeText(getActivity(), C0690R.string.no_play_during_call, 0).show();
            } else {
                if (this.mScene == 7) {
                    DataRepository.getInstance().getLabelSearchRepository().insertLabel(CursorProvider.getInstance().getRecordingSearchTag(), System.currentTimeMillis());
                    SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_search_result), getActivity().getResources().getString(C0690R.string.event_search_play_on_full));
                } else {
                    SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_list), getActivity().getResources().getString(C0690R.string.event_play_on_full_screen));
                }
                int playerState = Engine.getInstance().getPlayerState();
                if (playerState == 1) {
                    postEvent(Event.HIDE_SIP);
                    startPlayTask(false, j, i);
                    view.sendAccessibilityEvent(65536);
                } else if (playerState != 3) {
                    if (playerState == 4) {
                        if (Engine.getInstance().getID() == j) {
                            int i3 = this.mScene;
                            if (i3 == 3 || i3 == 7) {
                                postEvent(Event.HIDE_SIP);
                                postEvent(Event.OPEN_FULL_PLAYER);
                            } else {
                                int resumePlay = Engine.getInstance().resumePlay();
                                if (resumePlay == -103) {
                                    Toast.makeText(getActivity(), C0690R.string.no_play_during_call, 0).show();
                                } else if (resumePlay == 0) {
                                    postEvent(Event.PLAY_RESUME);
                                }
                            }
                        } else {
                            postEvent(Event.HIDE_SIP);
                            startPlayTask(false, j, i);
                        }
                    }
                } else if (Engine.getInstance().getID() == j) {
                    int i4 = this.mScene;
                    if (i4 != 3 && i4 != 7) {
                        Engine.getInstance().pausePlay();
                        postEvent(Event.PLAY_PAUSE);
                    }
//                    } else if (((InputMethodManager) getActivity().getSystemService("input_method")).semIsInputMethodShown()) {
//                        postEvent(Event.HIDE_SIP);
//                        postEventDelayed(Event.OPEN_FULL_PLAYER, 350);
//                    } else {
//                        postEvent(Event.OPEN_FULL_PLAYER);
//                    }
                } else {
                    postEvent(Event.HIDE_SIP);
                    startPlayTask(false, j, i);
                }
                if (view != null && (seekBar = (SeekBar) view.findViewById(C0690R.C0693id.listrow_seekbar)) != null) {
//                    seekBar.semSetOnSeekBarHoverListener((SeekBar.SemOnSeekBarHoverListener) null);
                }
            }
        }
    }

    public boolean onItemLongClick(View view, int i) {
        long itemId = this.mListAdapter.getItemId(i);
        if (Engine.getInstance().getPlayerState() != 1) {
            Engine.getInstance().stopPlay();
        }
        String str = this.TAG;
        Log.m29v(str, "onItemLongClick - position : " + i);
        this.mLastPosSelected = i;
        int i2 = this.mScene;
        if (i2 != 5 && i2 != 9 && i2 != 10) {
            CheckedItemProvider.initCheckedList();
            CheckedItemProvider.toggle(itemId);
            if (this.mRecyclerView != null && i == this.mListAdapter.getItemCount() - 1) {
                this.mRecyclerView.postDelayed(new Runnable() {
                    public final void run() {
                        AbsListFragment.this.lambda$onItemLongClick$4$AbsListFragment();
                    }
                }, 50);
            }
            if (this.mScene != 7) {
                postEvent(6);
            } else {
                SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_search_result), getActivity().getResources().getString(C0690R.string.event_search_selection_mode));
                postEvent(13);
            }
        } else if (!CheckedItemProvider.isChecked(itemId)) {
            CheckedItemProvider.toggle(itemId);
            CheckBox checkBox = (CheckBox) view.findViewById(C0690R.C0693id.listrow_checkbox);
            if (checkBox != null) {
                checkBox.toggle();
            }
            postEvent(Event.INVALIDATE_MENU);
            view.setActivated(CheckedItemProvider.isChecked(itemId));
        }
//        this.mRecyclerView.seslStartLongPressMultiSelection();
        return true;
    }

    public /* synthetic */ void lambda$onItemLongClick$4$AbsListFragment() {
        this.mRecyclerView.smoothScrollBy(getResources().getDimensionPixelSize(C0690R.dimen.fast_option_view_height), 0);
    }

    public boolean onHeaderClick(View view, int i) {
        RecyclerView recyclerView;
        long itemId = this.mListAdapter.getItemId(i);
        String str = this.TAG;
        Log.m26i(str, "onHeaderClick  - position : " + i + " id : " + itemId);
        PlayTask playTask = this.mPlayTask;
        if ((playTask != null && playTask.isRunning()) || ((recyclerView = this.mRecyclerView) != null && recyclerView.getVisibility() != 0)) {
            Log.m26i(this.TAG, "onHeaderClick PlayTask is running or mRecyclerView is not visible, return this operation");
            return false;
        } else if (Engine.getInstance().getRecorderState() != 1) {
            String str2 = this.TAG;
            Log.m26i(str2, "onHeaderClick - recorder is not idle, recorderState : " + Engine.getInstance().getRecorderState());
            return false;
        } else if (this.mListAdapter == null) {
            Log.m22e(this.TAG, "onHeaderClick - mListAdapter is null");
            return false;
        } else {
            int i2 = this.mScene;
            if (i2 == 5 || i2 == 10) {
                CheckedItemProvider.toggle(itemId);
                CheckBox checkBox = (CheckBox) view.findViewById(C0690R.C0693id.listrow_checkbox);
                if (checkBox != null) {
                    checkBox.toggle();
                }
                postEvent(Event.SELECT);
                return true;
            }
            if (Engine.getInstance().getID() != itemId) {
                if (this.mScene == 7) {
                    SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_search_result), getActivity().getResources().getString(C0690R.string.event_play_on_search_list));
                } else {
                    SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_list), getActivity().getResources().getString(C0690R.string.event_play_on_list));
                }
                startPlayTask(true, itemId, i);
            } else if (Engine.getInstance().getPlayerState() == 3) {
                Engine.getInstance().pausePlay();
                SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_list_miniplayer), getActivity().getResources().getString(C0690R.string.event_pause_on_list));
                int i3 = this.mScene;
                if (i3 == 3) {
                    postEvent(Event.MINI_PLAY_PAUSE);
                } else if (i3 == 7) {
                    postEvent(Event.SEARCH_PLAY_PAUSE);
                }
            } else {
                int resumePlay = Engine.getInstance().resumePlay();
                if (resumePlay == -103) {
                    Toast.makeText(getActivity(), C0690R.string.no_play_during_call, 0).show();
                } else if (resumePlay == 0) {
                    int i4 = this.mScene;
                    if (i4 == 3) {
                        SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_list), getActivity().getResources().getString(C0690R.string.event_play_on_list));
                        postEvent(Event.MINI_PLAY_RESUME);
                    } else if (i4 == 7) {
                        SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_search_result), getActivity().getResources().getString(C0690R.string.event_play_on_search_list));
                        postEvent(Event.SEARCH_PLAY_RESUME);
                    }
                }
            }
            return true;
        }
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        String str = this.TAG;
        Log.m26i(str, "onKeyEvent keyCode : " + keyEvent.getKeyCode() + " eventAction" + keyEvent.getAction());
        RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView == null) {
            return false;
        }
        recyclerView.findContainingViewHolder(view).getAdapterPosition();
        ImageButton imageButton = (ImageButton) view.findViewById(C0690R.C0693id.listrow_play_icon);
        ImageButton imageButton2 = (ImageButton) view.findViewById(C0690R.C0693id.listrow_pause_icon);
        int keyCode = keyEvent.getKeyCode();
        if (keyCode != 66) {
            switch (keyCode) {
                case 19:
                case 20:
                    if (keyEvent.getAction() == 0) {
                        imageButton.setPressed(false);
                        imageButton2.setPressed(false);
                        break;
                    }
                    break;
                case 21:
                    if (keyEvent.getAction() == 1) {
                        if (imageButton == null || !imageButton.isEnabled() || imageButton.getVisibility() != 0) {
                            if (imageButton2 != null && imageButton2.isEnabled() && imageButton2.getVisibility() == 0) {
                                imageButton2.setPressed(true);
                                break;
                            }
                        } else {
                            imageButton.setPressed(true);
                            break;
                        }
                    }
                    break;
                case 22:
                    if (keyEvent.getAction() == 0) {
                        if (imageButton != null && imageButton.isPressed()) {
                            this.mListAdapter.setListItemSelected(true, false);
                            imageButton.setPressed(false);
                            return true;
                        } else if (imageButton2 != null && imageButton2.isPressed()) {
                            this.mListAdapter.setListItemSelected(true, false);
                            imageButton2.setPressed(false);
                            return true;
                        }
                    }
                    int i2 = this.mScene;
                    if (i2 == 3 || i2 == 7) {
                        return true;
                    }
                    break;
                case 23:
                    break;
            }
        }
        if (imageButton == null || !imageButton.isPressed()) {
            if (imageButton2 != null && imageButton2.isPressed() && Engine.getInstance().getPlayerState() == 3) {
                if (keyEvent.getAction() == 1) {
                    this.mListAdapter.setListItemSelected(true, true);
                    imageButton2.setPressed(false);
                    imageButton2.performClick();
                    return true;
                } else if (keyEvent.getAction() == 0) {
                    return true;
                }
            }
            return false;
        } else if (keyEvent.getAction() == 1) {
            this.mListAdapter.setListItemSelected(true, true);
            imageButton.setPressed(false);
            imageButton.performClick();
            return true;
        } else if (keyEvent.getAction() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void notifyDataSetChangedToAdapter() {
        if (this.mListAdapter == null) {
            Log.m22e(this.TAG, "notifyDataSetChangedToAdapter adapter is null");
            return;
        }
        terminateExpandListAnimation();
        this.mListAdapter.notifyDataSetChanged();
    }

    /* renamed from: com.sec.android.app.voicenote.ui.AbsListFragment$PlayTask */
    private class PlayTask extends AsyncTask<Void, Integer, Boolean> {
        private final long mId;
        private boolean mIsNeedScroll;
        private final boolean mMini;
        private boolean mPlayResult;
        /* access modifiers changed from: private */
        public int mPosition;
        private int mTaskState;
        private final Object syncObj;

        /* renamed from: com.sec.android.app.voicenote.ui.AbsListFragment$PlayTask$TaskState */
        private class TaskState {
            private static final int FINISH = 2;
            private static final int INIT = 0;
            private static final int RUNNING = 1;

            private TaskState() {
            }
        }

        private PlayTask(boolean z, long j, int i) {
            this.syncObj = new Object();
            this.mMini = z;
            this.mId = j;
            this.mPosition = i;
            ListAdapter listAdapter = AbsListFragment.this.mListAdapter;
            boolean z2 = true;
            this.mIsNeedScroll = (listAdapter == null || !this.mMini || this.mPosition != listAdapter.getItemCount() - 1) ? false : z2;
            this.mTaskState = 0;
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(Void... voidArr) {
            this.mTaskState = 1;
            this.mPlayResult = AbsListFragment.this.startPlay(this.mMini, this.mId, this.mPosition);
            return true;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            Log.m26i(AbsListFragment.this.TAG, "onPostExecute");
            if (AbsListFragment.this.mProgressBar != null) {
                AbsListFragment.this.mProgressBar.setVisibility(8);
            }
            if (AbsListFragment.this.mRecyclerView != null) {
                AbsListFragment.this.mRecyclerView.setEnabled(true);
                AbsListFragment.this.mRecyclerView.setAlpha(1.0f);
            }
            if (!this.mPlayResult || !this.mMini) {
                int unused = AbsListFragment.this.mExpandedPosition = -1;
            } else {
                synchronized (this.syncObj) {
                    if (AbsListFragment.this.mRecyclerView != null) {
                        if (AbsListFragment.this.mListAdapter != null) {
                            AbsListFragment.this.setExpandListAnimation(this.mPosition);
                        }
                    }
                    this.mTaskState = 2;
                    return;
                }
            }
            this.mTaskState = 2;
        }

        /* access modifiers changed from: package-private */
        public boolean isRunning() {
            return this.mTaskState == 1;
        }

        /* access modifiers changed from: package-private */
        public void updatePosition(int i) {
            this.mPosition = i;
            ListAdapter listAdapter = AbsListFragment.this.mListAdapter;
            boolean z = true;
            if (listAdapter == null || !this.mMini || this.mPosition != listAdapter.getItemCount() - 1) {
                z = false;
            }
            this.mIsNeedScroll = z;
        }
    }

    /* access modifiers changed from: private */
    public void setExpandListAnimation(int i) {
        String str = this.TAG;
        Log.m19d(str, "setExpandListAnimation position = " + i + " mExpandedPosition = " + this.mExpandedPosition);
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) this.mRecyclerView.getLayoutManager();
        int findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
        int findLastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
        if (findFirstVisibleItemPosition == -1 || findFirstVisibleItemPosition > i || findLastVisibleItemPosition == -1 || i > findLastVisibleItemPosition) {
            notifyDataSetChangedToAdapter();
            int i2 = this.mExpandedPosition;
            if (i2 <= i) {
                linearLayoutManager.scrollToPositionWithOffset(i2, 0);
            } else if (i == 0) {
                doAnimation(this.mRecyclerView.getChildAt(0), 0);
            } else {
                linearLayoutManager.scrollToPositionWithOffset(i, 0);
            }
            updateExpandListValue();
            return;
        }
        String str2 = this.TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("setExpandListAnimation expandListHeight : ");
        int i3 = i - findFirstVisibleItemPosition;
        sb.append(i3);
        Log.m19d(str2, sb.toString());
        View childAt = this.mRecyclerView.getChildAt(i3);
        if (childAt != null) {
            this.mItemHeight = childAt.getHeight();
            expandListHeight(childAt);
            doAnimation(childAt, i);
            shrinkItemAtPosition(this.mExpandedPosition);
        }
    }

    private void shrinkItemAtPosition(int i) {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) this.mRecyclerView.getLayoutManager();
        int findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
        int findLastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
        if (findFirstVisibleItemPosition > i || i > findLastVisibleItemPosition) {
            this.mListAdapter.notifyItemChanged(i);
            return;
        }
        String str = this.TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("setExpandListAnimation shrinkListHeight : ");
        int i2 = i - findFirstVisibleItemPosition;
        sb.append(i2);
        Log.m19d(str, sb.toString());
        shrinkListHeight(this.mRecyclerView.getChildAt(i2));
    }

    private void shrinkListHeight(final View view) {
        if (view == null) {
            Log.m19d(this.TAG, "shrinkListHeight item is null");
            return;
        }
        final int height = view.getHeight();
        final int i = this.mItemHeight;
        if (height > i) {
            Animation r2 = new Animation() {
                public boolean willChangeBounds() {
                    return true;
                }

                /* access modifiers changed from: protected */
                public void applyTransformation(float f, Transformation transformation) {
                    if (view.findViewById(C0690R.C0693id.listrow_seekbar).getVisibility() != 8) {
                        view.findViewById(C0690R.C0693id.listrow_seekbar).setVisibility(8);
                    }
                    if (f >= 1.0f) {
                        view.getLayoutParams().height = -2;
                    } else {
                        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                        int i = height;
                        layoutParams.height = (int) (((float) i) + (((float) (i - i)) * f));
                    }
                    view.requestLayout();
                }
            };
            r2.setDuration(300);
            setInterpolator((Animation) r2, 3);
            r2.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                    view.findViewById(C0690R.C0693id.listrow_position).setVisibility(8);
                    view.findViewById(C0690R.C0693id.listrow_duration).setVisibility(0);
                    view.findViewById(C0690R.C0693id.listrow_date).setVisibility(0);
                    ((TextView) view.findViewById(C0690R.C0693id.listrow_title)).setTextColor(AbsListFragment.this.getResources().getColor(C0690R.C0691color.listview_title_normal, (Resources.Theme) null));
                    AbsListFragment absListFragment = AbsListFragment.this;
                    ListAdapter listAdapter = absListFragment.mListAdapter;
                    if (listAdapter != null) {
                        listAdapter.changePlayerIcon(4, (ListAdapter.ViewHolder) absListFragment.mRecyclerView.getChildViewHolder(view));
                    }
                }

                public void onAnimationEnd(Animation animation) {
                    view.getLayoutParams().height = -2;
                    view.requestLayout();
                }
            });
            view.startAnimation(r2);
        }
    }

    private void expandListHeight(final View view) {
        if (view == null) {
            Log.m19d(this.TAG, "expandListHeight item is null");
            return;
        }
        final int height = view.getHeight();
        final SeekBar seekBar = (SeekBar) view.findViewById(C0690R.C0693id.listrow_seekbar);
        final int dimensionPixelSize = height + getResources().getDimensionPixelSize(C0690R.dimen.listrow_seekbar_height);
        final int dimensionPixelSize2 = getResources().getDimensionPixelSize(C0690R.dimen.listrow_miniplay_min_height);
        final View view2 = view;
        Animation r1 = new Animation() {
            public boolean willChangeBounds() {
                return true;
            }

            /* access modifiers changed from: protected */
            public void applyTransformation(float f, Transformation transformation) {
                int i;
                if (f >= 1.0f) {
                    i = dimensionPixelSize;
                } else {
                    int i2 = height;
                    i = (int) (((float) i2) + (((float) (dimensionPixelSize - i2)) * f));
                }
                view2.getLayoutParams().height = i;
                view2.requestLayout();
                if (i >= dimensionPixelSize2 && seekBar.getVisibility() != 0) {
                    seekBar.setVisibility(0);
                }
            }
        };
        r1.setDuration(300);
        setInterpolator((Animation) r1, 3);
        r1.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                animation.cancel();
                view.clearAnimation();
                view.getLayoutParams().height = -2;
                view.requestLayout();
            }
        });
        view.startAnimation(r1);
    }

    /* access modifiers changed from: private */
    public void updateExpandListValue(int i) {
        PlayTask playTask = this.mPlayTask;
        if (playTask == null) {
            Log.m22e(this.TAG, "updateExpandListValue mPlayTask is null");
            return;
        }
        if (i != playTask.mPosition) {
            shrinkItemAtPosition(i);
        }
        this.mExpandedPosition = this.mPlayTask.mPosition;
    }

    private void updateExpandListValue() {
        PlayTask playTask = this.mPlayTask;
        if (playTask == null) {
            Log.m22e(this.TAG, "updateExpandListValue mPlayTask is null");
        } else {
            this.mExpandedPosition = playTask.mPosition;
        }
    }

    private void terminateExpandListAnimation() {
        AnimatorSet animatorSet = this.mAnimationSet;
        if (animatorSet != null) {
            if (animatorSet.isRunning()) {
                this.mAnimationSet.cancel();
            }
            if (this.mAnimationSet.getChildAnimations() != null) {
                this.mAnimationSet.getChildAnimations().clear();
            }
        }
    }

    private void setInterpolator(ObjectAnimator objectAnimator, int i) {
        if (i == 1) {
            objectAnimator.setInterpolator(PathInterpolatorCompat.create(0.33f, 0.0f, 0.67f, 1.0f));
        } else if (i != 3) {
            objectAnimator.setInterpolator(PathInterpolatorCompat.create(0.33f, 0.0f, 0.83f, 0.83f));
        } else {
            objectAnimator.setInterpolator(PathInterpolatorCompat.create(0.33f, 0.0f, 0.2f, 1.0f));
        }
    }

    private void setInterpolator(Animation animation, int i) {
        if (i == 1) {
            animation.setInterpolator(PathInterpolatorCompat.create(0.33f, 0.0f, 0.67f, 1.0f));
        } else if (i != 3) {
            animation.setInterpolator(PathInterpolatorCompat.create(0.33f, 0.0f, 0.83f, 0.83f));
        } else {
            animation.setInterpolator(PathInterpolatorCompat.create(0.33f, 0.0f, 0.2f, 1.0f));
        }
    }

    private boolean isMaxFont() {
        Configuration configuration = getResources().getConfiguration();
        if (configuration == null) {
            return false;
        }
        float f = configuration.fontScale;
        TypedValue typedValue = new TypedValue();
        getResources().getValue(C0690R.dimen.font_scale_extra_large, typedValue, true);
        if (f >= typedValue.getFloat()) {
            return true;
        }
        return false;
    }

    private void doAnimation(View view, int i) {
        if (view != null) {
            this.mAnimationSet = new AnimatorSet();
            ArrayList arrayList = new ArrayList();
            final ImageButton imageButton = (ImageButton) view.findViewById(C0690R.C0693id.listrow_play_icon);
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(imageButton, View.ALPHA, new float[]{1.0f, 0.0f});
            ofFloat.setDuration(200);
            setInterpolator(ofFloat, 1);
            arrayList.add(ofFloat);
            ImageButton imageButton2 = (ImageButton) view.findViewById(C0690R.C0693id.listrow_pause_icon);
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(imageButton2, View.ALPHA, new float[]{0.0f, 1.0f});
            ofFloat2.setDuration(200);
            setInterpolator(ofFloat2, 1);
            arrayList.add(ofFloat2);
            arrayList.add(ObjectAnimator.ofArgb((TextView) view.findViewById(C0690R.C0693id.listrow_title), "textColor", new int[]{getResources().getColor(C0690R.C0691color.listview_title_normal, (Resources.Theme) null), getResources().getColor(C0690R.C0691color.listview_title_play, (Resources.Theme) null)}).setDuration(200));
            TextView textView = (TextView) view.findViewById(C0690R.C0693id.listrow_date);
            textView.setVisibility(8);
            arrayList.add(ObjectAnimator.ofFloat(textView, View.ALPHA, new float[]{1.0f, 0.0f}));
            TextView textView2 = (TextView) view.findViewById(C0690R.C0693id.listrow_duration);
            arrayList.add(ObjectAnimator.ofFloat(textView2, View.ALPHA, new float[]{1.0f, 0.0f}));
            final TextView textView3 = (TextView) view.findViewById(C0690R.C0693id.listrow_position);
            arrayList.add(ObjectAnimator.ofFloat(view.findViewById(C0690R.C0693id.listrow_position), View.ALPHA, new float[]{0.0f, 1.0f}));
            SeekBar seekBar = (SeekBar) view.findViewById(C0690R.C0693id.listrow_seekbar);
            ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(seekBar, View.ALPHA, new float[]{0.0f, 1.0f});
            ofFloat3.setDuration(300);
            setInterpolator(ofFloat3, 1);
            arrayList.add(ofFloat3);
            this.mListAdapter.setProgressHoverWindow(seekBar, true);
            final ImageButton imageButton3 = imageButton2;
            final SeekBar seekBar2 = seekBar;
            final TextView textView4 = textView;
            final TextView textView5 = textView2;
            final ArrayList arrayList2 = arrayList;
            final int i2 = i;
            this.mAnimationSet.addListener(new Animator.AnimatorListener() {
                public void onAnimationRepeat(Animator animator) {
                }

                public void onAnimationStart(Animator animator) {
                    AbsListFragment.this.mRecyclerView.setEnabled(false);
                    imageButton3.setVisibility(0);
                    textView3.setVisibility(0);
                    SeekBar seekBar = seekBar2;
                    AbsListFragment absListFragment = AbsListFragment.this;
                    seekBar.setThumbTintList(absListFragment.mListAdapter.colorToColorStateList(absListFragment.getResources().getColor(C0690R.C0691color.listrow_seekbar_fg_color, (Resources.Theme) null)));
                    imageButton.setEnabled(false);
                    imageButton3.setEnabled(false);
                }

                public void onAnimationEnd(Animator animator) {
                    Log.m19d(AbsListFragment.this.TAG, "doAnimation onAnimationEnd");
                    imageButton3.setVisibility(0);
                    textView3.setVisibility(0);
                    seekBar2.setVisibility(0);
                    textView4.setAlpha(1.0f);
                    textView5.setAlpha(1.0f);
                    textView5.setVisibility(4);
                    seekBar2.setAlpha(1.0f);
                    imageButton.setAlpha(1.0f);
                    imageButton.setEnabled(true);
                    imageButton.setVisibility(8);
                    imageButton3.setAlpha(1.0f);
                    imageButton3.setEnabled(true);
                    textView3.setAlpha(1.0f);
                    arrayList2.clear();
                    AbsListFragment.this.updateExpandListValue(i2);
                    if (AbsListFragment.this.mRecyclerView != null) {
                        AbsListFragment.this.mRecyclerView.setEnabled(true);
                    }
                    if (AbsListFragment.this.mScene != 7) {
                        AbsListFragment.this.postEvent(Event.MINI_PLAY_START);
                    } else {
                        AbsListFragment.this.postEvent(Event.SEARCH_PLAY_START);
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    Log.m26i(AbsListFragment.this.TAG, "onAnimationCancel");
                    AbsListFragment.this.updateExpandListValue(i2);
                    if (AbsListFragment.this.mRecyclerView != null) {
                        AbsListFragment.this.mRecyclerView.setEnabled(true);
                    }
                }
            });
            this.mAnimationSet.playTogether(arrayList);
            this.mAnimationSet.start();
        }
    }

    private void startPlayTask(boolean z, long j, int i) {
        postEvent(Event.BLOCK_CONTROL_BUTTONS);
        String path = CursorProvider.getInstance().getPath(j);
        long duration = CursorProvider.getInstance().getDuration(j);
        if (path == null) {
            String str = this.TAG;
            Log.m22e(str, "id " + j + " is not valid. file not found");
            return;
        }
        if ((path.endsWith(AudioFormat.ExtType.EXT_AMR) && duration > 180000) || (path.endsWith(AudioFormat.ExtType.EXT_M4A) && duration > 10800000)) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                recyclerView.setAlpha(0.5f);
                this.mRecyclerView.setEnabled(false);
            }
            ProgressBar progressBar = this.mProgressBar;
            if (progressBar != null) {
                progressBar.setVisibility(0);
            }
        }
        PlayTask playTask = this.mPlayTask;
        if (playTask != null && playTask.getStatus() == AsyncTask.Status.RUNNING) {
            this.mPlayTask.cancel(false);
        }
        AnimatorSet animatorSet = this.mAnimationSet;
        if (animatorSet != null) {
            if (animatorSet.isRunning()) {
                this.mAnimationSet.cancel();
            }
            this.mAnimationSet = null;
        }
        this.mPlayTask = new PlayTask(z, j, i);
        this.mPlayTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private void updatePlayTask() {
        this.mPlayTask = new PlayTask(this.mScene == 3, Engine.getInstance().getID(), CursorProvider.getInstance().getCurrentPlayingPosition());
    }

    /* access modifiers changed from: private */
    public boolean startPlay(boolean z, long j, int i) {
        String str = this.TAG;
        Log.m26i(str, "startPlay - mini : " + z + " id : " + j + " position : " + i);
        Engine.getInstance().clearContentItem();
        int startPlay = Engine.getInstance().startPlay(j, true);
        if (z) {
            SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_PLAY_TYPE, 1);
        } else {
            SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_PLAY_TYPE, -1);
        }
        if (Settings.getBooleanSettings(Settings.KEY_SPEAKERPHONE_MODE, false)) {
            SurveyLogProvider.insertStatusLog(SurveyLogProvider.SURVEY_PLAY_VIA_PRIVATE, (String) null, 1000);
        } else {
            SurveyLogProvider.insertStatusLog(SurveyLogProvider.SURVEY_PLAY_VIA_PRIVATE, (String) null, 0);
        }
        if (startPlay == -119 || startPlay == -115 || startPlay == -103) {
            Handler handler = this.mTaskEventHandler;
            if (handler != null) {
                handler.sendEmptyMessage(startPlay);
            }
            postEventDelayed(Event.UNBLOCK_CONTROL_BUTTONS, 100);
            return false;
        } else if (startPlay != 0) {
            postEventDelayed(Event.UNBLOCK_CONTROL_BUTTONS, 100);
            return false;
        } else {
            CursorProvider.getInstance().setCurrentPlayingItemPosition(i);
            if (z) {
                return true;
            }
            if (this.mScene != 7) {
                postEvent(Event.PLAY_START);
            } else {
                postEvent(Event.PLAY_START);
            }
            postEvent(Event.UPDATE_FILE_NAME);
            return true;
        }
    }

    public void onSceneChange(int i) {
        String str = this.TAG;
        Log.m26i(str, "onSceneChange - scene : " + i);
        if (!isAdded() || isRemoving()) {
            Log.m19d(this.TAG, "onSceneChange - it is not added");
            return;
        }
        int i2 = this.mScene;
        if (!((i2 != 7 && i2 != 9) || this.mScene == i || i == 10)) {
            Log.m29v(this.TAG, "search results or private list should be removed");
            CursorProvider.getInstance().query(getLoaderManager(), "");
        }
        this.mScene = i;
        MouseKeyboardProvider.getInstance().setCurrentScene(this.mScene);
        int i3 = this.mScene;
        if (i3 == 5 || i3 == 10) {
            this.mViewMarginBottom.setVisibility(8);
        } else {
            this.mViewMarginBottom.setVisibility(0);
        }
        ListAdapter listAdapter = this.mListAdapter;
        if (listAdapter != null) {
            int i4 = this.mScene;
            listAdapter.setSelectionMode(i4 == 10 || i4 == 5 || i4 == 9);
        }
        if (this.mRecyclerView != null) {
            int i5 = this.mScene;
            if (i5 == 4 || i5 == 6) {
                changeListVisibility(false);
                showEmptyView(false);
                return;
            }
            changeListVisibility(true);
            if (this.mListAdapter.getItemCount() == 0) {
                showEmptyView(true);
            }
        }
    }

    private void changeListVisibility(boolean z) {
        String str = this.TAG;
        Log.m26i(str, "changeListVisibility : " + z);
        if (getActivity() == null || getActivity().getWindow() == null) {
            Log.m22e(this.TAG, "changeListVisibility getActivity or getWindow return null");
        } else if (z) {
            this.mRecyclerView.setVisibility(0);
            WindowManager.LayoutParams attributes = getActivity().getWindow().getAttributes();
//            attributes.semAddExtensionFlags(1);
            getActivity().getWindow().setAttributes(attributes);
        } else {
            this.mRecyclerView.setVisibility(4);
            this.mViewMarginBottom.setVisibility(8);
            WindowManager.LayoutParams attributes2 = getActivity().getWindow().getAttributes();
//            attributes2.semAddExtensionFlags(0);
            getActivity().getWindow().setAttributes(attributes2);
        }
    }

    private String stringForTime(int i) {
        int i2 = i / 1000;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", new Object[]{Integer.valueOf(i2 / 3600), Integer.valueOf((i2 / 60) % 60), Integer.valueOf(i2 % 60)});
    }

    private void showEmptyView(boolean z) {
        View findViewById;
        if (getView() != null && (findViewById = getView().findViewById(C0690R.C0693id.empty)) != null) {
            if (z) {
                View findViewById2 = findViewById.findViewById(C0690R.C0693id.list_empty_recorded_list);
                View findViewById3 = findViewById.findViewById(C0690R.C0693id.list_empty_search_list);
                TextView textView = (TextView) findViewById.findViewById(C0690R.C0693id.no_recordings_description);
                if (getTag() != null && getTag().equals(CHILD_LIST_TAG)) {
                    textView.setText(getString(C0690R.string.no_recordings_category_description));
                }
                if (this.mScene == 7) {
                    findViewById3.setVisibility(0);
                    findViewById2.setVisibility(8);
                    getActivity().getWindow().setSoftInputMode(16);
                } else {
                    findViewById3.setVisibility(8);
                    findViewById2.setVisibility(0);
                }
                findViewById.setVisibility(0);
                return;
            }
            getActivity().getWindow().setSoftInputMode(48);
            findViewById.setVisibility(8);
        }
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        if (z) {
            Engine.getInstance().seekTo(seekBar.getProgress());
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_list_miniplayer), getActivity().getResources().getString(C0690R.string.event_seek_bar));
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        if (this.mPauseBySeek) {
            int resumePlay = Engine.getInstance().resumePlay();
            if (resumePlay == -103) {
                Toast.makeText(getActivity(), C0690R.string.no_play_during_call, 0).show();
            } else if (resumePlay == 0) {
                this.mPauseBySeek = false;
            }
        }
    }

    private void moveToPlayingPosition() {
        RecyclerView recyclerView;
        int currentPlayingPosition = CursorProvider.getInstance().getCurrentPlayingPosition();
        if (currentPlayingPosition >= 0 && (recyclerView = this.mRecyclerView) != null) {
            recyclerView.smoothScrollToPosition(currentPlayingPosition);
        }
    }

    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        ContextMenuProvider.getInstance().createContextMenu(getActivity(), contextMenu, this.mScene, this.mRecyclerView, this);
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        ContextMenuProvider.getInstance().contextItemSelected((AppCompatActivity) getActivity(), menuItem, this.mScene, this);
        return false;
    }

    private void setLongPressMultiSelection() {
//        this.mRecyclerView.seslSetLongPressMultiSelectionListener(new RecyclerView.SeslLongPressMultiSelectionListener() {
//            public void onLongPressMultiSelectionStarted(int i, int i2) {
//            }
//
//            public void onItemSelected(RecyclerView recyclerView, View view, int i, long j) {
//                if (AbsListFragment.this.mRecyclerView != null) {
//                    AbsListFragment absListFragment = AbsListFragment.this;
//                    if (absListFragment.mListAdapter != null) {
//                        if (absListFragment.mScene == 5 || AbsListFragment.this.mScene == 10) {
//                            CheckedItemProvider.toggle(AbsListFragment.this.mListAdapter.getItemId(i));
//                            int unused = AbsListFragment.this.mLastPosSelected = i;
//                            CheckBox checkBox = (CheckBox) view.findViewById(C0690R.C0693id.listrow_checkbox);
//                            if (checkBox != null) {
//                                checkBox.setChecked(!checkBox.isChecked());
//                            }
//                            AbsListFragment.this.postEvent(Event.INVALIDATE_MENU);
//                            AbsListFragment.this.notifyDataSetChangedToAdapter();
//                        }
//                    }
//                }
//            }
//
//            public void onLongPressMultiSelectionEnded(int i, int i2) {
//                AbsListFragment.this.postEvent(Event.ENABLE_MARGIN_BOTTOM_LIST);
//            }
//        });
    }

    public boolean isBackPossible() {
        PlayTask playTask = this.mPlayTask;
        return (playTask == null || !playTask.isRunning()) && (Engine.getInstance().getPlayerState() == 1 || this.mScene != 2) && super.isBackPossible();
    }

    private void addOnItemTouchListener() {
        this.mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            public void onRequestDisallowInterceptTouchEvent(boolean z) {
            }

            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            }

            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                if ((AbsListFragment.this.mScene != 5 && AbsListFragment.this.mScene != 10) || motionEvent.getAction() != 1 || AbsListFragment.this.mBottomNavigationView == null || AbsListFragment.this.mBottomNavigationView.getVisibility() != 8 || CheckedItemProvider.getCheckedItemCount() <= 0) {
                    return false;
                }
                AbsListFragment.this.postEvent(Event.SHOW_BOTTOM_NAVIGATION_BAR);
                return false;
            }
        });
    }

    private void setScrollbarPosition() {
        if (getContext().getResources().getConfiguration().getLayoutDirection() == 1) {
            this.mRecyclerView.setVerticalScrollbarPosition(1);
        } else {
            this.mRecyclerView.setVerticalScrollbarPosition(2);
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.AbsListFragment$RoundedDecoration */
    private class RoundedDecoration extends RecyclerView.ItemDecoration {
        private RoundedDecoration() {
        }

        public void seslOnDispatchDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
//            super.seslOnDispatchDraw(canvas, recyclerView, state);
//            AbsListFragment.this.mSeslListRoundedCorner.drawRoundedCorner(canvas);
        }
    }
}
