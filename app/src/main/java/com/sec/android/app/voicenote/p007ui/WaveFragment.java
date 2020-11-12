package com.sec.android.app.voicenote.p007ui;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.VelocityTrackerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.p007ui.WaveFragment;
import com.sec.android.app.voicenote.p007ui.adapter.BookmarkListAdapter;
import com.sec.android.app.voicenote.p007ui.adapter.RecyclerAdapter;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.p007ui.view.CustomFastScroll;
import com.sec.android.app.voicenote.p007ui.view.FloatingView;
import com.sec.android.app.voicenote.p007ui.view.HandlerView;
import com.sec.android.app.voicenote.p007ui.view.WaveRecyclerView;
import com.sec.android.app.voicenote.p007ui.view.ZoomView;
import com.sec.android.app.voicenote.provider.AssistantProvider;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.DBProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.HWKeyboardProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.MouseKeyboardProvider;
import com.sec.android.app.voicenote.provider.PhoneStateProvider;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.StorageProvider;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.provider.ViewProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.provider.WaveProvider;
import com.sec.android.app.voicenote.service.AudioFormat;
import com.sec.android.app.voicenote.service.ContentItem;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.MetadataRepository;
import com.sec.android.app.voicenote.service.Recorder;
import com.sec.android.app.voicenote.service.helper.Bookmark;
import com.sec.android.app.voicenote.uicore.FragmentController;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/* renamed from: com.sec.android.app.voicenote.ui.WaveFragment */
public class WaveFragment extends AbsFragment implements Engine.OnEngineListener, CompoundButton.OnCheckedChangeListener, FragmentController.OnSceneChangeListener, DialogFactory.DialogResultListener {
    private static final int LOAD_WAVE_CONTINUE = 2;
    private static final int LOAD_WAVE_END = 3;
    private static final int MIN_TRIM_TIME = 1022;
    private static final int ONE_SEC = 1022;
    private static final String TAG = "WaveFragment";
//    private SemAddDeleteListAnimator mAddDeleteListAnimator;
    private int mAddTime;
    /* access modifiers changed from: private */
    public AsyncLoadWave mAsyncLoadWave = null;
    private View mBookmarkEmptyView = null;
    private boolean mBookmarkKeepState = false;
    private ListView mBookmarkList = null;
    /* access modifiers changed from: private */
    public BookmarkListAdapter mBookmarkListAdapter = null;
    /* access modifiers changed from: private */
    public LinearLayout mBookmarkListArea = null;
    private FrameLayout mBookmarkListAreaFrame = null;
    private TextView mBookmarkListTitle;
    private HandlerView mCurrentLineView;
    /* access modifiers changed from: private */
    public int mCurrentMaxScrollSpeed;
    /* access modifiers changed from: private */
    public FrameLayout mCurrentTimeLayout;
    /* access modifiers changed from: private */
    public String mCurrentWavePath = null;
    private int mDefaultMaxScrollSpeed;
    private Handler mDelayHandler = new DelayHandler();
    private int mDeletePosition;
    /* access modifiers changed from: private */
    public int mDuration = 0;
    private HandlerView mEditCurrentLineView;
    /* access modifiers changed from: private */
    public FloatingView mEditCurrentTimeHandler;
    /* access modifiers changed from: private */
    public EditMode mEditMode = new EditMode();
    private Handler mEngineEventHandler = null;
    private boolean mIsBookmarkShowing;
    /* access modifiers changed from: private */
    public boolean mIsLoadCompleted = true;
    /* access modifiers changed from: private */
    public boolean mIsTouchingScrollBar = false;
    /* access modifiers changed from: private */
    public int mLastVelocity = -1;
    /* access modifiers changed from: private */
    public FloatingView mLeftRepeatHandler;
    /* access modifiers changed from: private */
    public ImageView mLeftTrimHandlerImageView;
    /* access modifiers changed from: private */
    public FrameLayout mLeftTrimHandlerLayout;
    /* access modifiers changed from: private */
    public View mLeftTrimHandlerLineView;
    /* access modifiers changed from: private */
    public TextView mLeftTrimHandlerTime;
    private FrameLayout mLeftTrimHandlerTouchLayout;
    /* access modifiers changed from: private */
    public Handler mLoadWaveHandler = new LoadWaveHandler(this);
    private boolean mNeedToUpdateLayout = false;
    private TextView mNoBookmarksDescription;
    private int mOldMaxAmplitude = -1;
    private View.OnTouchListener mOnEditCurrentTouchListener = null;
    private View.OnTouchListener mOnLeftRepeatListener = null;
    private View.OnTouchListener mOnLeftTrimListener = null;
    private View.OnTouchListener mOnRightRepeatListener = null;
    private View.OnTouchListener mOnRightTrimListener = null;
    private int mOverwriteReady = 0;
    /* access modifiers changed from: private */
    public boolean mPlayBarIsMoving = false;
    /* access modifiers changed from: private */
    public boolean mRecordFromLeft;
    /* access modifiers changed from: private */
    public RecyclerAdapter mRecyclerAdapter = null;
    /* access modifiers changed from: private */
    public WaveRecyclerView mRecyclerView = null;
    /* access modifiers changed from: private */
    public int mRecyclerViewOffset = 0;
    /* access modifiers changed from: private */
    public FloatingView mRightRepeatHandler;
    /* access modifiers changed from: private */
    public ImageView mRightTrimHandlerImageView;
    /* access modifiers changed from: private */
    public FrameLayout mRightTrimHandlerLayout;
    /* access modifiers changed from: private */
    public View mRightTrimHandlerLineView;
    /* access modifiers changed from: private */
    public TextView mRightTrimHandlerTime;
    private FrameLayout mRightTrimHandlerTouchLayout;
    /* access modifiers changed from: private */
    public ScaleGestureDetector mScaleGestureDetector = null;
    /* access modifiers changed from: private */
    public int mScene = 0;
    /* access modifiers changed from: private */
    public int mScrollPointerId = -1;
    /* access modifiers changed from: private */
    public boolean mScrollable = false;
    private ScrollerHandler mScrollerHandler = new ScrollerHandler(this);
    private boolean mSkipScrollByResizeWaveView = false;
    /* access modifiers changed from: private */
    public VelocityTracker mVelocityTracker = null;
    /* access modifiers changed from: private */
    public FrameLayout mWaveBgArea = null;
    /* access modifiers changed from: private */
    public FrameLayout mZoomScrollViewChildLayout = null;
    /* access modifiers changed from: private */
    public HorizontalScrollView mZoomScrollbarView = null;
    private View.OnTouchListener mZoomScrollbarViewTouchListener = null;
    /* access modifiers changed from: private */
    public ZoomView mZoomView = null;

    public void onCreate(Bundle bundle) {
        Log.m26i(TAG, "onCreate - bundle : " + bundle);
        super.onCreate(bundle);
        this.mEngineEventHandler = new Handler(new Handler.Callback() {
            public final boolean handleMessage(Message message) {
                return WaveFragment.this.lambda$onCreate$2$WaveFragment(message);
            }
        });
        WaveProvider.getInstance().init();
        this.mRecyclerAdapter = new RecyclerAdapter(getActivity(), getRecordMode(), false);
        if (this.mAsyncLoadWave == null) {
            this.mAsyncLoadWave = new AsyncLoadWave();
        }
        if (Engine.getInstance().getPlayerState() != 1) {
            this.mAsyncLoadWave.start(Engine.getInstance().getPath());
        } else if (Engine.getInstance().getRecorderState() != 1) {
            this.mAsyncLoadWave.start(Engine.getInstance().getRecentFilePath());
        }
    }

    public /* synthetic */ boolean lambda$onCreate$2$WaveFragment(Message message) {
        int i;
        if (getActivity() == null || !isAdded() || isRemoving()) {
            Log.m22e(TAG, "mEngineEventHandler RETURN by : " + getActivity() + ',' + isAdded() + ',' + isAdded());
            return false;
        }
        int i2 = message.what;
        boolean z = true;
        if (i2 == 105) {
            int i3 = message.arg1;
            if (i3 == 2) {
                MouseKeyboardProvider.getInstance().changePointerIcon(getActivity().getWindow().getDecorView(), getActivity().getWindow().getDecorView().getContext(), 1);
            } else if (i3 == 3) {
                MouseKeyboardProvider.getInstance().changePointerIcon(getActivity().getWindow().getDecorView(), getActivity().getWindow().getDecorView().getContext(), 4);
            }
        } else if (i2 == 3010) {
            Log.m19d(TAG, "onEditorUpdate - INFO_EDITOR_STATE : " + message.arg1);
            int i4 = message.arg1;
            if (!(i4 == 0 || i4 == 1)) {
                if (i4 == 3) {
                    this.mAsyncLoadWave.stop();
                    Engine.getInstance().resetOverwriteTime();
                    setTrimEnabled(false);
                    setScrollEnable(false);
                } else if (i4 == 4) {
                    loadBookmarkData();
                    updateBookmarkListAdapter();
                    postEvent(Event.EDIT_REFRESH_BOOKMARK);
                    this.mEditMode.init();
                } else if (i4 == 5) {
                    Toast.makeText(getActivity(), C0690R.string.trim_failed, 0).show();
                    this.mEditMode.init();
                } else if (i4 == 8) {
                    Toast.makeText(getActivity(), C0690R.string.delete_failed, 0).show();
                    this.mEditMode.init();
                } else if (i4 != 11) {
                    setTrimEnabled(true);
                } else {
                    updateInterviewLayout(getView());
                }
            }
        } else if (i2 == 1010) {
            Log.m19d(TAG, "onRecorderUpdate - INFO_RECORDER_STATE : " + message.arg1);
            int i5 = message.arg1;
            if (i5 == 1) {
                if (this.mScene != 4 && !Engine.getInstance().isSimpleRecorderMode()) {
                    initialize();
                    Engine.getInstance().setCurrentTime(0);
                }
                this.mOverwriteReady = 0;
                this.mOldMaxAmplitude = -1;
                Engine.getInstance().resetOverwriteTime();
            } else if (i5 == 2) {
                setScrollEnable(false);
                this.mOverwriteReady++;
                this.mCurrentWavePath = Engine.getInstance().getRecentFilePath();
                this.mCurrentLineView.setVisibility(0);
                updateCurrentTimeLayout();
                this.mRecyclerView.hideScrollBar();
            } else if (i5 == 3) {
                if (this.mDuration == 0) {
                    this.mDuration = Engine.getInstance().getDuration();
                }
                this.mRecordFromLeft = false;
                updateCurrentTimeLayout();
                this.mOverwriteReady = 0;
                this.mOldMaxAmplitude = -1;
                this.mRecyclerView.postDelayed(new Runnable() {
                    public final void run() {
                        WaveFragment.this.lambda$null$0$WaveFragment();
                    }
                }, 30);
                if (this.mScene == 6 && Engine.getInstance().startOverwrite(-1) == 0) {
                    this.mCurrentWavePath = Engine.getInstance().getRecentFilePath();
                    if (this.mDuration + 70 > Engine.getInstance().getDuration()) {
                        this.mDuration = Engine.getInstance().getDuration();
                        Engine.getInstance().setCurrentTime(this.mDuration, true);
                    }
                }
            } else if (i5 == 4) {
                this.mOldMaxAmplitude = -1;
            }
        } else if (i2 != 1011) {
            switch (i2) {
                case 2010:
                    Log.m19d(TAG, "onPlayerUpdate - INFO_PLAYER_STATE : " + message.arg1);
                    int i6 = message.arg1;
                    if (i6 != 1 && i6 != 2) {
                        if (i6 != 3) {
                            if (i6 == 4) {
                                setScrollEnable(true);
                                this.mSkipScrollByResizeWaveView = false;
                                this.mEditMode.setEnableAutoScroll(false);
                                break;
                            }
                        } else {
                            setScrollEnable(true);
                            if (this.mEditMode.isShrinkMode()) {
                                Log.m32w(TAG, "PlayerState.PLAYING ZoomView : " + this.mZoomView.getStartTime() + "~" + this.mZoomView.getEndTime() + "  " + this.mDuration + " " + this.mEditCurrentTimeHandler.getX());
                                enableAutoScroll(true, 0);
                            }
                            this.mSkipScrollByResizeWaveView = true;
                            this.mCurrentLineView.setVisibility(0);
                            updateCurrentTimeLayout();
                            break;
                        }
                    } else {
                        setScrollEnable(true);
                        if (Engine.getInstance().getRecorderState() == 1 && Engine.getInstance().getPlayerState() != 3) {
                            this.mDuration = 0;
                            Engine.getInstance().setCurrentTime(this.mDuration);
                            Engine.getInstance().setCurrentTime(this.mDuration, true);
                            break;
                        }
                    }
                    break;
                case 2011:
                    setScrollEnable(false);
                    View view = getView();
                    if (view != null) {
                        view.postDelayed(new Runnable() {
                            public final void run() {
                                WaveFragment.this.lambda$null$1$WaveFragment();
                            }
                        }, 30);
                        break;
                    }
                    break;
                case 2012:
                    int playerState = Engine.getInstance().getPlayerState();
                    if ((playerState == 3 || playerState == 4 || playerState == 2) && !this.mPlayBarIsMoving) {
                        this.mDuration = message.arg1;
                        if (this.mSkipScrollByResizeWaveView) {
                            this.mSkipScrollByResizeWaveView = false;
                            Log.m32w(TAG, "SKIP scrollTo by mSkipScrollByResizeWaveView");
                        } else {
                            scrollTo((int) (((float) this.mDuration) / WaveProvider.MS_PER_PX));
                        }
                        Engine.getInstance().setCurrentTime(this.mDuration, true);
                        this.mWaveBgArea.setContentDescription(getResources().getString(C0690R.string.wave_tts) + " , " + getResources().getString(C0690R.string.swipe_left_or_right_with_two_fingers));
                        if (this.mBookmarkListAdapter.setPlayingPosition(this.mDuration)) {
                            this.mBookmarkListAdapter.notifyDataSetChanged();
                        }
                        if (VoiceNoteApplication.getScene() == 12) {
                            this.mZoomView.invalidateZoomView(true);
                            break;
                        }
                    }
                    break;
                case 2013:
                    int repeatMode = Engine.getInstance().getRepeatMode();
                    int[] repeatPosition = Engine.getInstance().getRepeatPosition();
                    if (repeatMode != 2) {
                        if (repeatMode != 3) {
                            if (repeatMode == 4) {
                                Log.m19d(TAG, "repeat mode b : " + repeatPosition[1]);
                                this.mLeftRepeatHandler.setVisibility(0);
                                setRepeatHandler(this.mLeftRepeatHandler, repeatPosition[0]);
                                this.mRightRepeatHandler.setVisibility(0);
                                setRepeatHandler(this.mRightRepeatHandler, repeatPosition[1]);
                                this.mRecyclerAdapter.setRepeatTime(repeatPosition[0], repeatPosition[1]);
                                break;
                            } else {
                                Log.m19d(TAG, "repeat mode default");
                                break;
                            }
                        } else {
                            Log.m19d(TAG, "repeat mode a : " + repeatPosition[0]);
                            this.mLeftRepeatHandler.setVisibility(0);
                            setRepeatHandler(this.mLeftRepeatHandler, repeatPosition[0]);
                            break;
                        }
                    } else {
                        Log.m19d(TAG, "repeat mode none");
                        setRepeatVisibility(8);
                        this.mRecyclerAdapter.setRepeatTime(-1, -1);
                        break;
                    }
                case 2014:
                    Log.m19d(TAG, "INFO_SKIP_SILENCE - mode : " + message.arg1);
                    updateInterviewLayout(getView());
                    break;
            }
        } else {
            this.mDuration = message.arg1;
            if (!this.mRecordFromLeft || this.mDuration >= (i = WaveProvider.START_RECORD_MARGIN)) {
                scrollTo((int) (((float) this.mDuration) / WaveProvider.MS_PER_PX));
                if (this.mRecordFromLeft) {
                    this.mRecordFromLeft = false;
                    updateCurrentTimeLayout();
                }
            } else {
                scrollTo((int) (((float) i) / WaveProvider.MS_PER_PX));
                updateCurrentTimeLayout();
            }
            if (Engine.getInstance().getRecorderState() != 1) {
                Engine.getInstance().setCurrentTime(this.mDuration, true);
            }
            int i7 = this.mDuration;
            int i8 = i7 <= 0 ? 1 : (i7 / WaveProvider.DURATION_PER_WAVEVIEW) + 1;
            int i9 = this.mDuration;
            int i10 = i9 <= 0 ? 0 : (i9 % WaveProvider.DURATION_PER_WAVEVIEW) / 70;
            if (this.mOverwriteReady >= 2) {
                this.mOverwriteReady = 0;
                this.mRecyclerAdapter.setIndex(i8, i10);
            }
            if (MetadataRepository.getInstance().removeRoughBookmark(this.mDuration)) {
                this.mRecyclerAdapter.removeBookmark(i8, i10);
                deleteBookmark(this.mBookmarkListAdapter.expectDeletePosition(this.mDuration));
                postEvent(Event.REMOVE_BOOKMARK);
            }
            if (message.arg2 != -1) {
                while (this.mRecyclerAdapter.getItemCount() - 1 <= i8) {
                    this.mRecyclerAdapter.addItem(getActivity());
                }
                int i11 = this.mOldMaxAmplitude;
                if (i11 == -1) {
                    this.mOldMaxAmplitude = message.arg2;
                } else {
                    int i12 = message.arg2;
                    int i13 = ((((i11 >> 16) + (i12 >> 16)) / 2) << 16) + (((i11 & SupportMenu.USER_MASK) + (i12 & SupportMenu.USER_MASK)) / 2);
                    RecyclerAdapter recyclerAdapter = this.mRecyclerAdapter;
                    if (!isResumed() && Engine.getInstance().getScreenOff()) {
                        z = false;
                    }
                    recyclerAdapter.addAmplitude(i8, i10, i13, z);
                    this.mOldMaxAmplitude = -1;
                }
            }
        }
        return false;
    }

    public /* synthetic */ void lambda$null$0$WaveFragment() {
        if (Engine.getInstance().getPlayerState() != 3) {
            setScrollEnable(true);
            if (this.mRecyclerView.getScrollState() != 2) {
                scrollTo((int) ((((float) this.mDuration) / WaveProvider.MS_PER_PX) + ((float) WaveProvider.AMPLITUDE_TOTAL_WIDTH)));
            }
        }
    }

    public /* synthetic */ void lambda$null$1$WaveFragment() {
        setScrollEnable(true);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Log.m26i(TAG, "onCreateView - bundle : " + bundle);
        int vROrientation = DisplayManager.getVROrientation();
        if (vROrientation == 1 || vROrientation == 3) {
            WaveProvider.getInstance().setWaveAreaWidth(DisplayManager.getCurrentScreenWidth(getActivity()) / 2, false);
        } else {
            WaveProvider.getInstance().setWaveAreaWidth(DisplayManager.getCurrentScreenWidth(getActivity()), false);
        }
        View inflate = layoutInflater.inflate(C0690R.layout.fragment_wave, viewGroup, false);
        this.mRecyclerView = (WaveRecyclerView) inflate.findViewById(C0690R.C0693id.recycler_view);
        this.mRecyclerView.setParent((FrameLayout) inflate.findViewById(C0690R.C0693id.wave_area));
        this.mRecyclerView.init(false);
        this.mZoomView = (ZoomView) inflate.findViewById(C0690R.C0693id.zoom_view);
        setProgressHoverWindow(this.mZoomView, true);
        this.mRecyclerView.setFastScrollEnable(true);
//        this.mRecyclerView.seslSetHoverScrollEnabled(false);
        this.mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            public void onRequestDisallowInterceptTouchEvent(boolean z) {
            }

            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            }

            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                Log.m26i(WaveFragment.TAG, "onInterceptTouchEvent  getAction() : " + motionEvent.getAction() + " getX() :" + motionEvent.getX());
                if (WaveFragment.this.mVelocityTracker == null) {
                    VelocityTracker unused = WaveFragment.this.mVelocityTracker = VelocityTracker.obtain();
                }
                WaveFragment.this.mVelocityTracker.addMovement(motionEvent);
                int action = motionEvent.getAction();
                if (action == 0) {
                    int unused2 = WaveFragment.this.mScrollPointerId = MotionEventCompat.getPointerId(motionEvent, 0);
                } else if (action == 1) {
                    WaveFragment.this.mVelocityTracker.computeCurrentVelocity(1000, 64000.0f);
                    WaveFragment waveFragment = WaveFragment.this;
                    int unused3 = waveFragment.mLastVelocity = (int) (-VelocityTrackerCompat.getXVelocity(waveFragment.mVelocityTracker, WaveFragment.this.mScrollPointerId));
                    Log.m26i(WaveFragment.TAG, "mLastVelocity : " + WaveFragment.this.mLastVelocity);
                    WaveFragment.this.mVelocityTracker.recycle();
                    VelocityTracker unused4 = WaveFragment.this.mVelocityTracker = null;
                }
                return false;
            }
        });
        this.mLeftTrimHandlerLayout = (FrameLayout) inflate.findViewById(C0690R.C0693id.wave_left_trim_handler_layout);
        this.mRightTrimHandlerLayout = (FrameLayout) inflate.findViewById(C0690R.C0693id.wave_right_trim_handler_layout);
        this.mLeftTrimHandlerTouchLayout = (FrameLayout) inflate.findViewById(C0690R.C0693id.wave_left_trim_handler_image_touch_layout);
        this.mRightTrimHandlerTouchLayout = (FrameLayout) inflate.findViewById(C0690R.C0693id.wave_right_trim_handler_image_touch_layout);
        this.mEditCurrentTimeHandler = (FloatingView) inflate.findViewById(C0690R.C0693id.wave_edit_current_line_layout);
        this.mCurrentTimeLayout = (FrameLayout) inflate.findViewById(C0690R.C0693id.wave_current_line_layout);
        this.mLeftTrimHandlerTime = (TextView) inflate.findViewById(C0690R.C0693id.wave_left_trim_handler_time);
        this.mRightTrimHandlerTime = (TextView) inflate.findViewById(C0690R.C0693id.wave_right_trim_handler_time);
        this.mLeftTrimHandlerImageView = (ImageView) inflate.findViewById(C0690R.C0693id.wave_left_trim_handler_image);
        this.mRightTrimHandlerImageView = (ImageView) inflate.findViewById(C0690R.C0693id.wave_right_trim_handler_image);
        this.mLeftRepeatHandler = (FloatingView) inflate.findViewById(C0690R.C0693id.wave_left_repeat_handler_layout);
        this.mRightRepeatHandler = (FloatingView) inflate.findViewById(C0690R.C0693id.wave_right_repeat_handler_layout);
        this.mLeftTrimHandlerLineView = inflate.findViewById(C0690R.C0693id.wave_left_trim_handler_line);
        this.mRightTrimHandlerLineView = inflate.findViewById(C0690R.C0693id.wave_right_trim_handler_line);
        this.mCurrentLineView = (HandlerView) inflate.findViewById(C0690R.C0693id.wave_current_line);
        this.mEditCurrentLineView = (HandlerView) inflate.findViewById(C0690R.C0693id.wave_edit_current_line);
        this.mZoomScrollbarView = (HorizontalScrollView) inflate.findViewById(C0690R.C0693id.zoom_scrollbar_view);
        this.mZoomScrollViewChildLayout = (FrameLayout) inflate.findViewById(C0690R.C0693id.zoom_scrollview_layout);
        this.mWaveBgArea = (FrameLayout) inflate.findViewById(C0690R.C0693id.recycler_view_wave_area);
        this.mBookmarkListAreaFrame = (FrameLayout) inflate.findViewById(C0690R.C0693id.bookmark_list_area_frame);
        this.mBookmarkListArea = (LinearLayout) inflate.findViewById(C0690R.C0693id.bookmark_list_area);
        this.mNoBookmarksDescription = (TextView) inflate.findViewById(C0690R.C0693id.no_bookmarks_description);
        this.mBookmarkListTitle = (TextView) inflate.findViewById(C0690R.C0693id.bookmark_list_text);
        ViewProvider.setMaxFontSize(getContext(), this.mBookmarkListTitle);
        TextView textView = this.mBookmarkListTitle;
        textView.setContentDescription(this.mBookmarkListTitle.getText().toString() + ", " + getString(C0690R.string.header));
        setScrollEnable(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(0);
        this.mRecyclerView.setAdapter(this.mRecyclerAdapter);
        this.mRecyclerView.setFocusable(false);
        this.mRecyclerView.setLayoutManager(linearLayoutManager);
        this.mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.mDefaultMaxScrollSpeed = getCurrentMaxScrollSpeed();
        this.mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return WaveFragment.this.lambda$onCreateView$3$WaveFragment(view, motionEvent);
            }
        });
        this.mZoomScrollbarView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            public final void onScrollChange(View view, int i, int i2, int i3, int i4) {
                WaveFragment.this.lambda$onCreateView$4$WaveFragment(view, i, i2, i3, i4);
            }
        });
        this.mRecyclerView.setScrollBarStateChangeListener(new CustomFastScroll.onScrollBarStateChangeListener() {
            public final void onStateChange(int i, int i2, boolean z) {
                WaveFragment.this.lambda$onCreateView$5$WaveFragment(i, i2, z);
            }
        });
        this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int mDx;
            private int mMultiple = 1;
            private int mScrollState = 0;

            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                Log.m26i(WaveFragment.TAG, "onScrollStateChanged - newState : " + i + " has event : " + WaveFragment.this.hasPostEvent(1003));
                super.onScrollStateChanged(recyclerView, i);
                WaveFragment waveFragment = WaveFragment.this;
                int unused = waveFragment.mRecyclerViewOffset = waveFragment.mRecyclerView.getHorizontalScrollOffset();
                if (WaveFragment.this.mScrollable && !WaveFragment.this.hasPostEvent(1003)) {
                    if (i == 0) {
                        this.mMultiple = 1;
                        WaveFragment.this.handleScrollStateIdle();
                        this.mScrollState = i;
                    } else if (i == 1) {
                        WaveFragment.this.handleScrollStateDragging();
                    } else if (i == 2) {
                        if (this.mScrollState != 0) {
                            int i2 = this.mMultiple;
                            if (i2 <= 8) {
                                this.mMultiple = i2 * 2;
                            }
                            Log.m26i(WaveFragment.TAG, "onScrollStateChanged pre : " + this.mScrollState + " new : " + i + " mLastVelocity : " + WaveFragment.this.mLastVelocity + " mMultiple : " + this.mMultiple);
                            if (WaveFragment.this.mLastVelocity != -1) {
                                if (WaveFragment.this.mCurrentMaxScrollSpeed < Math.abs(WaveFragment.this.mLastVelocity * this.mMultiple)) {
                                    WaveFragment waveFragment2 = WaveFragment.this;
                                    waveFragment2.changeMaxScrollSpeed(Math.abs(waveFragment2.mLastVelocity * this.mMultiple));
                                }
                                WaveFragment.this.mRecyclerView.fling(WaveFragment.this.mLastVelocity * this.mMultiple, 0);
                                int unused2 = WaveFragment.this.mLastVelocity = -1;
                            }
                        }
                        this.mScrollState = i;
                    }
                }
                if (i == 0) {
                    WaveFragment.this.postEvent(Event.UNBLOCK_CONTROL_BUTTONS);
                } else if (i != 2) {
                    WaveFragment.this.postEvent(Event.BLOCK_CONTROL_BUTTONS);
                } else {
                    WaveFragment.this.postEvent(Event.BLOCK_CONTROL_BUTTONS);
                }
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                boolean z;
                int i3 = i * this.mMultiple;
                super.onScrolled(recyclerView, i3, i2);
                this.mDx = i3;
                int horizontalScrollOffset = WaveFragment.this.mRecyclerView.getHorizontalScrollOffset();
                if (horizontalScrollOffset == 0) {
                    WaveFragment.this.scrollTo(0);
                }
                if (WaveFragment.this.mScrollable) {
                    int maxScrollRange = WaveFragment.this.mRecyclerView.getMaxScrollRange();
                    if (horizontalScrollOffset >= maxScrollRange) {
                        WaveFragment.this.scrollTo(maxScrollRange);
                    }
                    WaveFragment.this.mEditMode.updateTrimHandler();
                    if (horizontalScrollOffset > 0 && horizontalScrollOffset < maxScrollRange) {
                        WaveFragment waveFragment = WaveFragment.this;
                        waveFragment.moveRepeatHandler(waveFragment.mLeftRepeatHandler, (((float) this.mDx) * 1.0f) / ((float) this.mMultiple), horizontalScrollOffset);
                        WaveFragment waveFragment2 = WaveFragment.this;
                        waveFragment2.moveRepeatHandler(waveFragment2.mRightRepeatHandler, (((float) this.mDx) * 1.0f) / ((float) this.mMultiple), horizontalScrollOffset);
                    }
                }
                if (recyclerView.getScrollState() != 0 || WaveFragment.this.mIsTouchingScrollBar) {
                    int duration = Engine.getInstance().getDuration();
                    int unused = WaveFragment.this.mDuration = (int) (((float) horizontalScrollOffset) * WaveProvider.MS_PER_PX);
                    if (WaveFragment.this.mDuration > duration) {
                        if (recyclerView.getScrollState() != 2 || WaveFragment.this.mDuration > WaveProvider.START_RECORD_MARGIN) {
                            WaveFragment.this.scrollTo((int) (((float) duration) / WaveProvider.MS_PER_PX));
                        }
                        int unused2 = WaveFragment.this.mDuration = duration;
                        z = true;
                    } else {
                        z = false;
                    }
                    if (WaveFragment.this.mScrollable && WaveFragment.this.mScene == 6) {
                        if (WaveFragment.this.mDuration > Engine.getInstance().getTrimEndTime()) {
                            int unused3 = WaveFragment.this.mDuration = Engine.getInstance().getTrimEndTime();
                            WaveFragment waveFragment3 = WaveFragment.this;
                            waveFragment3.scrollTo((int) (((float) waveFragment3.mDuration) / WaveProvider.MS_PER_PX));
                            z = true;
                        } else if (WaveFragment.this.mDuration < Engine.getInstance().getTrimStartTime()) {
                            int unused4 = WaveFragment.this.mDuration = Engine.getInstance().getTrimStartTime();
                            WaveFragment waveFragment4 = WaveFragment.this;
                            waveFragment4.scrollTo((int) (((float) waveFragment4.mDuration) / WaveProvider.MS_PER_PX));
                        }
                        WaveFragment.this.mEditMode.updateCurrentTime(WaveFragment.this.mDuration);
                    }
                    Engine.getInstance().setCurrentTime(WaveFragment.this.mDuration, true);
                    if (z && Engine.getInstance().getPlayerState() == 3) {
                        Engine.getInstance().pausePlay();
                        if (Settings.getBooleanSettings(Settings.KEY_PLAY_CONTINUOUSLY, false)) {
                            new Handler().postDelayed($$Lambda$WaveFragment$2$c3e0KFt_O4uBc2ywDarWsiRKE.INSTANCE, 100);
                        }
                    }
                }
            }
        });
        this.mOnEditCurrentTouchListener = new View.OnTouchListener() {
            private float mStartPoint;

            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    this.mStartPoint = motionEvent.getX();
                    boolean unused = WaveFragment.this.mPlayBarIsMoving = true;
                    WaveFragment.this.postEvent(Event.BLOCK_CONTROL_BUTTONS);
                    if (Engine.getInstance().getRecorderState() != 1 && Engine.getInstance().startOverwrite(-1) == 0) {
                        String unused2 = WaveFragment.this.mCurrentWavePath = Engine.getInstance().getRecentFilePath();
                    }
                    if (WaveFragment.this.mScene == 12) {
                        SALogProvider.insertSALog(WaveFragment.this.getActivity().getResources().getString(C0690R.string.screen_ready_convert_stt), WaveFragment.this.getActivity().getResources().getString(C0690R.string.event_convert_seek_wave));
                    }
                } else if (action == 1) {
                    view.performClick();
                    boolean unused3 = WaveFragment.this.mPlayBarIsMoving = false;
                    WaveFragment.this.postEvent(Event.UNBLOCK_CONTROL_BUTTONS);
                    if (Engine.getInstance().getPlayerState() != 1) {
                        Engine.getInstance().seekTo(WaveFragment.this.mDuration);
                    } else if (Engine.getInstance().getRecorderState() != 1) {
                        Engine.getInstance().setCurrentTime(WaveFragment.this.mDuration);
                    }
                } else if (action == 2) {
                    WaveFragment waveFragment = WaveFragment.this;
                    int unused4 = waveFragment.mDuration = (int) (((float) waveFragment.mDuration) - (WaveFragment.this.getDistance(this.mStartPoint, motionEvent) * WaveFragment.this.mEditMode.getMsPerPx()));
                    if (VoiceNoteApplication.getScene() == 12) {
                        WaveFragment.this.updateTranslationStartTime();
                    } else {
                        if (WaveFragment.this.mDuration < Engine.getInstance().getTrimStartTime()) {
                            int unused5 = WaveFragment.this.mDuration = Engine.getInstance().getTrimStartTime();
                        } else if (WaveFragment.this.mDuration > Engine.getInstance().getTrimEndTime()) {
                            int unused6 = WaveFragment.this.mDuration = Engine.getInstance().getTrimEndTime();
                        }
                        if (WaveFragment.this.mDuration > Engine.getInstance().getDuration()) {
                            int unused7 = WaveFragment.this.mDuration = Engine.getInstance().getDuration();
                        }
                    }
                    Engine.getInstance().setCurrentTime(WaveFragment.this.mDuration, true);
                    WaveFragment waveFragment2 = WaveFragment.this;
                    waveFragment2.scrollTo((int) (((float) waveFragment2.mDuration) / WaveProvider.MS_PER_PX));
                } else if (action == 3) {
                    boolean unused8 = WaveFragment.this.mPlayBarIsMoving = false;
                    WaveFragment.this.postEvent(Event.UNBLOCK_CONTROL_BUTTONS);
                }
                return true;
            }
        };
        this.mOnLeftTrimListener = new View.OnTouchListener() {
            private float mStartPoint;

            /* JADX WARNING: Code restructure failed: missing block: B:6:0x000e, code lost:
                if (r0 != 3) goto L_0x01f8;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public boolean onTouch(android.view.View r5, android.view.MotionEvent r6) {
                /*
                    r4 = this;
                    int r0 = r6.getAction()
                    r1 = 1
                    r2 = 0
                    if (r0 == 0) goto L_0x019d
                    if (r0 == r1) goto L_0x011c
                    r3 = 2
                    if (r0 == r3) goto L_0x0012
                    r6 = 3
                    if (r0 == r6) goto L_0x011c
                    goto L_0x01f8
                L_0x0012:
                    com.sec.android.app.voicenote.service.Engine r5 = com.sec.android.app.voicenote.service.Engine.getInstance()
                    int r5 = r5.getTrimStartTime()
                    com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                    int r0 = r0.getTrimEndTime()
                    com.sec.android.app.voicenote.ui.WaveFragment r2 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    com.sec.android.app.voicenote.ui.WaveFragment$EditMode r2 = r2.mEditMode
                    boolean r2 = r2.isShrinkMode()
                    if (r2 == 0) goto L_0x0042
                    float r5 = (float) r5
                    com.sec.android.app.voicenote.ui.WaveFragment r2 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    float r3 = r4.mStartPoint
                    float r6 = r2.getDistance(r3, r6)
                    com.sec.android.app.voicenote.ui.WaveFragment r2 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    com.sec.android.app.voicenote.ui.WaveFragment$EditMode r2 = r2.mEditMode
                    float r2 = r2.getMsPerPx()
                    goto L_0x004d
                L_0x0042:
                    float r5 = (float) r5
                    com.sec.android.app.voicenote.ui.WaveFragment r2 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    float r3 = r4.mStartPoint
                    float r6 = r2.getDistance(r3, r6)
                    float r2 = com.sec.android.app.voicenote.provider.WaveProvider.MS_PER_PX
                L_0x004d:
                    float r6 = r6 * r2
                    float r5 = r5 - r6
                    int r5 = (int) r5
                    if (r5 >= 0) goto L_0x0054
                    r5 = 0
                    goto L_0x0059
                L_0x0054:
                    int r6 = r0 + -1022
                    if (r5 <= r6) goto L_0x0059
                    r5 = r6
                L_0x0059:
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    com.sec.android.app.voicenote.ui.WaveFragment$EditMode r6 = r6.mEditMode
                    boolean r6 = r6.isShrinkMode()
                    if (r6 == 0) goto L_0x009c
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    int r6 = r6.mDuration
                    if (r6 >= r5) goto L_0x00f8
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    int unused = r6.mDuration = r5
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    com.sec.android.app.voicenote.ui.WaveFragment$EditMode r6 = r6.mEditMode
                    com.sec.android.app.voicenote.ui.WaveFragment r0 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    int r0 = r0.mDuration
                    r6.updateCurrentTime(r0)
                    com.sec.android.app.voicenote.service.Engine r6 = com.sec.android.app.voicenote.service.Engine.getInstance()
                    com.sec.android.app.voicenote.ui.WaveFragment r0 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    int r0 = r0.mDuration
                    r6.setCurrentTime(r0, r1)
                    com.sec.android.app.voicenote.service.Engine r6 = com.sec.android.app.voicenote.service.Engine.getInstance()
                    com.sec.android.app.voicenote.ui.WaveFragment r0 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    int r0 = r0.mDuration
                    r6.seekTo(r0)
                    goto L_0x00f8
                L_0x009c:
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    int r6 = r6.mDuration
                    com.sec.android.app.voicenote.provider.WaveProvider r0 = com.sec.android.app.voicenote.provider.WaveProvider.getInstance()
                    float r0 = r0.getWaveViewWidthDimension()
                    com.sec.android.app.voicenote.ui.WaveFragment r2 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.content.res.Resources r2 = r2.getResources()
                    r3 = 2131165996(0x7f07032c, float:1.7946225E38)
                    float r2 = r2.getDimension(r3)
                    float r0 = r0 - r2
                    float r2 = com.sec.android.app.voicenote.provider.WaveProvider.MS_PER_PX
                    float r0 = r0 * r2
                    int r0 = (int) r0
                    int r6 = r6 - r0
                    com.sec.android.app.voicenote.ui.WaveFragment r0 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    int r0 = r0.mDuration
                    if (r5 <= r0) goto L_0x00db
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    int unused = r6.mDuration = r5
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    int r6 = r6.mDuration
                    float r6 = (float) r6
                    float r0 = com.sec.android.app.voicenote.provider.WaveProvider.MS_PER_PX
                    float r6 = r6 / r0
                    int r6 = (int) r6
                    com.sec.android.app.voicenote.ui.WaveFragment r0 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    r0.scrollTo(r6)
                    goto L_0x00f8
                L_0x00db:
                    if (r5 >= r6) goto L_0x00f8
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    int r0 = r6.mDuration
                    int r0 = r0 + -200
                    int unused = r6.mDuration = r0
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    int r6 = r6.mDuration
                    float r6 = (float) r6
                    float r0 = com.sec.android.app.voicenote.provider.WaveProvider.MS_PER_PX
                    float r6 = r6 / r0
                    int r6 = (int) r6
                    com.sec.android.app.voicenote.ui.WaveFragment r0 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    r0.scrollTo(r6)
                L_0x00f8:
                    com.sec.android.app.voicenote.service.Engine r6 = com.sec.android.app.voicenote.service.Engine.getInstance()
                    r6.setTrimStartTime(r5)
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    com.sec.android.app.voicenote.ui.WaveFragment$EditMode r6 = r6.mEditMode
                    r6.updateLeftTrimHandler(r5)
                    com.sec.android.app.voicenote.ui.WaveFragment r5 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    com.sec.android.app.voicenote.ui.WaveFragment$EditMode r5 = r5.mEditMode
                    r5.updateWave()
                    com.sec.android.app.voicenote.ui.WaveFragment r5 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    com.sec.android.app.voicenote.ui.adapter.RecyclerAdapter r5 = r5.mRecyclerAdapter
                    r5.notifyDataSetChanged()
                    goto L_0x01f8
                L_0x011c:
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.widget.ImageView r6 = r6.mLeftTrimHandlerImageView
                    com.sec.android.app.voicenote.ui.WaveFragment r0 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.content.res.Resources r0 = r0.getResources()
                    r3 = 2131100222(0x7f06023e, float:1.781282E38)
                    int r0 = r0.getColor(r3, r2)
                    r6.setColorFilter(r0)
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.widget.TextView r6 = r6.mLeftTrimHandlerTime
                    com.sec.android.app.voicenote.ui.WaveFragment r0 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.content.res.Resources r0 = r0.getResources()
                    int r0 = r0.getColor(r3, r2)
                    r6.setTextColor(r0)
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.view.View r6 = r6.mLeftTrimHandlerLineView
                    com.sec.android.app.voicenote.ui.WaveFragment r0 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.content.res.Resources r0 = r0.getResources()
                    int r0 = r0.getColor(r3, r2)
                    r6.setBackgroundColor(r0)
                    com.sec.android.app.voicenote.service.Engine r6 = com.sec.android.app.voicenote.service.Engine.getInstance()
                    int r6 = r6.getPlayerState()
                    if (r6 == r1) goto L_0x0163
                    goto L_0x0192
                L_0x0163:
                    com.sec.android.app.voicenote.service.Engine r6 = com.sec.android.app.voicenote.service.Engine.getInstance()
                    int r6 = r6.getRecorderState()
                    if (r6 == r1) goto L_0x0192
                    com.sec.android.app.voicenote.service.Engine r6 = com.sec.android.app.voicenote.service.Engine.getInstance()
                    com.sec.android.app.voicenote.ui.WaveFragment r0 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    int r0 = r0.mDuration
                    r6.setCurrentTime(r0)
                    com.sec.android.app.voicenote.service.Engine r6 = com.sec.android.app.voicenote.service.Engine.getInstance()
                    r0 = -1
                    int r6 = r6.startOverwrite(r0)
                    if (r6 != 0) goto L_0x0192
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                    java.lang.String r0 = r0.getRecentFilePath()
                    java.lang.String unused = r6.mCurrentWavePath = r0
                L_0x0192:
                    r5.performClick()
                    com.sec.android.app.voicenote.ui.WaveFragment r5 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    r6 = 969(0x3c9, float:1.358E-42)
                    r5.postEvent(r6)
                    goto L_0x01f8
                L_0x019d:
                    float r5 = r6.getX()
                    r4.mStartPoint = r5
                    com.sec.android.app.voicenote.ui.WaveFragment r5 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.widget.ImageView r5 = r5.mLeftTrimHandlerImageView
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.content.res.Resources r6 = r6.getResources()
                    r0 = 2131100221(0x7f06023d, float:1.7812817E38)
                    int r6 = r6.getColor(r0, r2)
                    r5.setColorFilter(r6)
                    com.sec.android.app.voicenote.ui.WaveFragment r5 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.view.View r5 = r5.mLeftTrimHandlerLineView
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.content.res.Resources r6 = r6.getResources()
                    int r6 = r6.getColor(r0, r2)
                    r5.setBackgroundColor(r6)
                    com.sec.android.app.voicenote.ui.WaveFragment r5 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.widget.TextView r5 = r5.mLeftTrimHandlerTime
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.content.res.Resources r6 = r6.getResources()
                    r0 = 2131100224(0x7f060240, float:1.7812823E38)
                    int r6 = r6.getColor(r0, r2)
                    r5.setTextColor(r6)
                    com.sec.android.app.voicenote.ui.WaveFragment r5 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.widget.TextView r5 = r5.mRightTrimHandlerTime
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.content.res.Resources r6 = r6.getResources()
                    r0 = 2131100223(0x7f06023f, float:1.7812821E38)
                    int r6 = r6.getColor(r0, r2)
                    r5.setTextColor(r6)
                L_0x01f8:
                    return r1
                */
                throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.WaveFragment.C08564.onTouch(android.view.View, android.view.MotionEvent):boolean");
            }
        };
        this.mOnRightTrimListener = new View.OnTouchListener() {
            private float mStartPoint;

            /* JADX WARNING: Code restructure failed: missing block: B:6:0x000e, code lost:
                if (r0 != 3) goto L_0x0200;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public boolean onTouch(android.view.View r6, android.view.MotionEvent r7) {
                /*
                    r5 = this;
                    int r0 = r7.getAction()
                    r1 = 1
                    r2 = 0
                    if (r0 == 0) goto L_0x01a5
                    if (r0 == r1) goto L_0x0124
                    r3 = 2
                    if (r0 == r3) goto L_0x0012
                    r7 = 3
                    if (r0 == r7) goto L_0x0124
                    goto L_0x0200
                L_0x0012:
                    com.sec.android.app.voicenote.service.Engine r6 = com.sec.android.app.voicenote.service.Engine.getInstance()
                    int r6 = r6.getDuration()
                    com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                    int r0 = r0.getTrimStartTime()
                    com.sec.android.app.voicenote.service.Engine r2 = com.sec.android.app.voicenote.service.Engine.getInstance()
                    int r2 = r2.getTrimEndTime()
                    com.sec.android.app.voicenote.ui.WaveFragment r3 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    com.sec.android.app.voicenote.ui.WaveFragment$EditMode r3 = r3.mEditMode
                    boolean r3 = r3.isShrinkMode()
                    if (r3 == 0) goto L_0x004a
                    float r2 = (float) r2
                    com.sec.android.app.voicenote.ui.WaveFragment r3 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    float r4 = r5.mStartPoint
                    float r7 = r3.getDistance(r4, r7)
                    com.sec.android.app.voicenote.ui.WaveFragment r3 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    com.sec.android.app.voicenote.ui.WaveFragment$EditMode r3 = r3.mEditMode
                    float r3 = r3.getMsPerPx()
                    goto L_0x0055
                L_0x004a:
                    float r2 = (float) r2
                    com.sec.android.app.voicenote.ui.WaveFragment r3 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    float r4 = r5.mStartPoint
                    float r7 = r3.getDistance(r4, r7)
                    float r3 = com.sec.android.app.voicenote.provider.WaveProvider.MS_PER_PX
                L_0x0055:
                    float r7 = r7 * r3
                    float r2 = r2 - r7
                    int r7 = (int) r2
                    if (r7 <= r6) goto L_0x005b
                    goto L_0x0061
                L_0x005b:
                    int r6 = r0 + 1022
                    if (r7 >= r6) goto L_0x0060
                    goto L_0x0061
                L_0x0060:
                    r6 = r7
                L_0x0061:
                    com.sec.android.app.voicenote.ui.WaveFragment r7 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    com.sec.android.app.voicenote.ui.WaveFragment$EditMode r7 = r7.mEditMode
                    boolean r7 = r7.isShrinkMode()
                    if (r7 == 0) goto L_0x00a4
                    com.sec.android.app.voicenote.ui.WaveFragment r7 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    int r7 = r7.mDuration
                    if (r7 <= r6) goto L_0x0100
                    com.sec.android.app.voicenote.ui.WaveFragment r7 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    int unused = r7.mDuration = r6
                    com.sec.android.app.voicenote.ui.WaveFragment r7 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    com.sec.android.app.voicenote.ui.WaveFragment$EditMode r7 = r7.mEditMode
                    com.sec.android.app.voicenote.ui.WaveFragment r0 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    int r0 = r0.mDuration
                    r7.updateCurrentTime(r0)
                    com.sec.android.app.voicenote.service.Engine r7 = com.sec.android.app.voicenote.service.Engine.getInstance()
                    com.sec.android.app.voicenote.ui.WaveFragment r0 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    int r0 = r0.mDuration
                    r7.setCurrentTime(r0, r1)
                    com.sec.android.app.voicenote.service.Engine r7 = com.sec.android.app.voicenote.service.Engine.getInstance()
                    com.sec.android.app.voicenote.ui.WaveFragment r0 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    int r0 = r0.mDuration
                    r7.seekTo(r0)
                    goto L_0x0100
                L_0x00a4:
                    com.sec.android.app.voicenote.ui.WaveFragment r7 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    int r7 = r7.mDuration
                    com.sec.android.app.voicenote.provider.WaveProvider r0 = com.sec.android.app.voicenote.provider.WaveProvider.getInstance()
                    float r0 = r0.getWaveViewWidthDimension()
                    com.sec.android.app.voicenote.ui.WaveFragment r2 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.content.res.Resources r2 = r2.getResources()
                    r3 = 2131165996(0x7f07032c, float:1.7946225E38)
                    float r2 = r2.getDimension(r3)
                    float r0 = r0 - r2
                    float r2 = com.sec.android.app.voicenote.provider.WaveProvider.MS_PER_PX
                    float r0 = r0 * r2
                    int r0 = (int) r0
                    int r7 = r7 + r0
                    com.sec.android.app.voicenote.ui.WaveFragment r0 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    int r0 = r0.mDuration
                    if (r6 >= r0) goto L_0x00e3
                    com.sec.android.app.voicenote.ui.WaveFragment r7 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    int unused = r7.mDuration = r6
                    com.sec.android.app.voicenote.ui.WaveFragment r7 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    int r7 = r7.mDuration
                    float r7 = (float) r7
                    float r0 = com.sec.android.app.voicenote.provider.WaveProvider.MS_PER_PX
                    float r7 = r7 / r0
                    int r7 = (int) r7
                    com.sec.android.app.voicenote.ui.WaveFragment r0 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    r0.scrollTo(r7)
                    goto L_0x0100
                L_0x00e3:
                    if (r6 <= r7) goto L_0x0100
                    com.sec.android.app.voicenote.ui.WaveFragment r7 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    int r0 = r7.mDuration
                    int r0 = r0 + 200
                    int unused = r7.mDuration = r0
                    com.sec.android.app.voicenote.ui.WaveFragment r7 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    int r7 = r7.mDuration
                    float r7 = (float) r7
                    float r0 = com.sec.android.app.voicenote.provider.WaveProvider.MS_PER_PX
                    float r7 = r7 / r0
                    int r7 = (int) r7
                    com.sec.android.app.voicenote.ui.WaveFragment r0 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    r0.scrollTo(r7)
                L_0x0100:
                    com.sec.android.app.voicenote.service.Engine r7 = com.sec.android.app.voicenote.service.Engine.getInstance()
                    r7.setTrimEndTime(r6)
                    com.sec.android.app.voicenote.ui.WaveFragment r7 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    com.sec.android.app.voicenote.ui.WaveFragment$EditMode r7 = r7.mEditMode
                    r7.updateRightTrimHandler(r6)
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    com.sec.android.app.voicenote.ui.WaveFragment$EditMode r6 = r6.mEditMode
                    r6.updateWave()
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    com.sec.android.app.voicenote.ui.adapter.RecyclerAdapter r6 = r6.mRecyclerAdapter
                    r6.notifyDataSetChanged()
                    goto L_0x0200
                L_0x0124:
                    com.sec.android.app.voicenote.ui.WaveFragment r7 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.widget.ImageView r7 = r7.mRightTrimHandlerImageView
                    com.sec.android.app.voicenote.ui.WaveFragment r0 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.content.res.Resources r0 = r0.getResources()
                    r3 = 2131100222(0x7f06023e, float:1.781282E38)
                    int r0 = r0.getColor(r3, r2)
                    r7.setColorFilter(r0)
                    com.sec.android.app.voicenote.ui.WaveFragment r7 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.widget.TextView r7 = r7.mRightTrimHandlerTime
                    com.sec.android.app.voicenote.ui.WaveFragment r0 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.content.res.Resources r0 = r0.getResources()
                    int r0 = r0.getColor(r3, r2)
                    r7.setTextColor(r0)
                    com.sec.android.app.voicenote.ui.WaveFragment r7 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.view.View r7 = r7.mRightTrimHandlerLineView
                    com.sec.android.app.voicenote.ui.WaveFragment r0 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.content.res.Resources r0 = r0.getResources()
                    int r0 = r0.getColor(r3, r2)
                    r7.setBackgroundColor(r0)
                    com.sec.android.app.voicenote.service.Engine r7 = com.sec.android.app.voicenote.service.Engine.getInstance()
                    int r7 = r7.getPlayerState()
                    if (r7 == r1) goto L_0x016b
                    goto L_0x019a
                L_0x016b:
                    com.sec.android.app.voicenote.service.Engine r7 = com.sec.android.app.voicenote.service.Engine.getInstance()
                    int r7 = r7.getRecorderState()
                    if (r7 == r1) goto L_0x019a
                    com.sec.android.app.voicenote.service.Engine r7 = com.sec.android.app.voicenote.service.Engine.getInstance()
                    com.sec.android.app.voicenote.ui.WaveFragment r0 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    int r0 = r0.mDuration
                    r7.setCurrentTime(r0)
                    com.sec.android.app.voicenote.service.Engine r7 = com.sec.android.app.voicenote.service.Engine.getInstance()
                    r0 = -1
                    int r7 = r7.startOverwrite(r0)
                    if (r7 != 0) goto L_0x019a
                    com.sec.android.app.voicenote.ui.WaveFragment r7 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                    java.lang.String r0 = r0.getRecentFilePath()
                    java.lang.String unused = r7.mCurrentWavePath = r0
                L_0x019a:
                    r6.performClick()
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    r7 = 969(0x3c9, float:1.358E-42)
                    r6.postEvent(r7)
                    goto L_0x0200
                L_0x01a5:
                    float r6 = r7.getX()
                    r5.mStartPoint = r6
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.widget.ImageView r6 = r6.mRightTrimHandlerImageView
                    com.sec.android.app.voicenote.ui.WaveFragment r7 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.content.res.Resources r7 = r7.getResources()
                    r0 = 2131100221(0x7f06023d, float:1.7812817E38)
                    int r7 = r7.getColor(r0, r2)
                    r6.setColorFilter(r7)
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.view.View r6 = r6.mRightTrimHandlerLineView
                    com.sec.android.app.voicenote.ui.WaveFragment r7 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.content.res.Resources r7 = r7.getResources()
                    int r7 = r7.getColor(r0, r2)
                    r6.setBackgroundColor(r7)
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.widget.TextView r6 = r6.mRightTrimHandlerTime
                    com.sec.android.app.voicenote.ui.WaveFragment r7 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.content.res.Resources r7 = r7.getResources()
                    r0 = 2131100224(0x7f060240, float:1.7812823E38)
                    int r7 = r7.getColor(r0, r2)
                    r6.setTextColor(r7)
                    com.sec.android.app.voicenote.ui.WaveFragment r6 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.widget.TextView r6 = r6.mLeftTrimHandlerTime
                    com.sec.android.app.voicenote.ui.WaveFragment r7 = com.sec.android.app.voicenote.p007ui.WaveFragment.this
                    android.content.res.Resources r7 = r7.getResources()
                    r0 = 2131100223(0x7f06023f, float:1.7812821E38)
                    int r7 = r7.getColor(r0, r2)
                    r6.setTextColor(r7)
                L_0x0200:
                    return r1
                */
                throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.WaveFragment.C08575.onTouch(android.view.View, android.view.MotionEvent):boolean");
            }
        };
        this.mZoomScrollbarViewTouchListener = new View.OnTouchListener() {
            private float mOldX;

            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getPointerCount() == 2) {
                    return WaveFragment.this.mScaleGestureDetector.onTouchEvent(motionEvent);
                }
                int action = motionEvent.getAction();
                if (action == 0) {
                    this.mOldX = motionEvent.getX();
                } else if (action == 1) {
                    if (Math.abs(motionEvent.getX() - this.mOldX) < 10.0f) {
                        WaveFragment waveFragment = WaveFragment.this;
                        int unused = waveFragment.mDuration = (int) (waveFragment.mZoomView.getStartTime() + (motionEvent.getX() * WaveFragment.this.mZoomView.getMsPerPx()));
                        Log.m26i(WaveFragment.TAG, "ZoomScrollbarView JUMP to " + WaveFragment.this.mDuration);
                        if (VoiceNoteApplication.getScene() == 12) {
                            WaveFragment.this.updateTranslationStartTime();
                        } else {
                            if (WaveFragment.this.mDuration < Engine.getInstance().getTrimStartTime()) {
                                int unused2 = WaveFragment.this.mDuration = Engine.getInstance().getTrimStartTime();
                            } else if (WaveFragment.this.mDuration > Engine.getInstance().getTrimEndTime()) {
                                int unused3 = WaveFragment.this.mDuration = Engine.getInstance().getTrimEndTime();
                            }
                            if (WaveFragment.this.mDuration > Engine.getInstance().getDuration()) {
                                int unused4 = WaveFragment.this.mDuration = Engine.getInstance().getDuration();
                            }
                        }
                        WaveFragment.this.mEditMode.updateCurrentTime(WaveFragment.this.mDuration);
                        Engine.getInstance().setCurrentTime(WaveFragment.this.mDuration, true);
                        if (Engine.getInstance().getPlayerState() != 1) {
                            Engine.getInstance().seekTo(WaveFragment.this.mDuration);
                        }
                    }
                    SALogProvider.insertSALog(WaveFragment.this.getActivity().getResources().getString(C0690R.string.screen_edit_comm), WaveFragment.this.getActivity().getResources().getString(C0690R.string.event_edit_seek_wave));
                    WaveFragment.this.enableAutoScroll(true, 2000);
                } else if (action == 2) {
                    WaveFragment.this.mZoomScrollbarView.setAlpha(1.0f);
                    WaveFragment.this.enableAutoScroll(false, 0);
                }
                if (WaveFragment.this.mEditMode.mIsZooming || WaveFragment.this.mZoomScrollbarView.onTouchEvent(motionEvent)) {
                    return true;
                }
                return false;
            }
        };
        this.mOnLeftRepeatListener = new View.OnTouchListener() {
            private float mStartPoint;
            final int[] positions = Engine.getInstance().getRepeatPosition();

            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    this.mStartPoint = motionEvent.getX();
                } else if (action == 1) {
                    Engine.getInstance().setRepeatTime(WaveFragment.this.mLeftRepeatHandler.getTime(WaveFragment.this.mRecyclerView.getHorizontalScrollOffset()), -1);
                    view.performClick();
                    WaveFragment.this.postEvent(Event.UNBLOCK_CONTROL_BUTTONS);
                } else if (action == 2) {
                    int horizontalScrollOffset = WaveFragment.this.mRecyclerView.getHorizontalScrollOffset();
                    if (WaveFragment.this.mScrollable) {
                        WaveFragment waveFragment = WaveFragment.this;
                        waveFragment.moveRepeatHandler(waveFragment.mLeftRepeatHandler, WaveFragment.this.getDistance(this.mStartPoint, motionEvent), horizontalScrollOffset);
                    }
                    if (WaveFragment.this.mLeftRepeatHandler.getVisibility() == 0) {
                        int time = WaveFragment.this.mLeftRepeatHandler.getTime(horizontalScrollOffset);
                        if (WaveFragment.this.mRightRepeatHandler.getVisibility() == 0) {
                            int[] iArr = this.positions;
                            if (iArr[0] <= iArr[1]) {
                                if (time > iArr[1] - 1022) {
                                    time = iArr[1] - 1022;
                                    WaveFragment.this.mLeftRepeatHandler.setTime(time, (float) horizontalScrollOffset);
                                }
                            } else if (time < iArr[1] + 1022) {
                                time = iArr[1] + 1022;
                                WaveFragment.this.mLeftRepeatHandler.setTime(time, (float) horizontalScrollOffset);
                            }
                        }
                        WaveFragment.this.mRecyclerAdapter.setRepeatStartTime(time);
                    }
                }
                return true;
            }
        };
        this.mOnRightRepeatListener = new View.OnTouchListener() {
            private float mStartPoint;
            final int[] positions = Engine.getInstance().getRepeatPosition();

            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    this.mStartPoint = motionEvent.getX();
                } else if (action == 1) {
                    Engine.getInstance().setRepeatTime(-1, WaveFragment.this.mRightRepeatHandler.getTime(WaveFragment.this.mRecyclerView.getHorizontalScrollOffset()));
                    view.performClick();
                    WaveFragment.this.postEvent(Event.UNBLOCK_CONTROL_BUTTONS);
                } else if (action == 2) {
                    int horizontalScrollOffset = WaveFragment.this.mRecyclerView.getHorizontalScrollOffset();
                    if (WaveFragment.this.mScrollable) {
                        WaveFragment waveFragment = WaveFragment.this;
                        waveFragment.moveRepeatHandler(waveFragment.mRightRepeatHandler, WaveFragment.this.getDistance(this.mStartPoint, motionEvent), horizontalScrollOffset);
                    }
                    if (WaveFragment.this.mRightRepeatHandler.getVisibility() == 0) {
                        int time = WaveFragment.this.mRightRepeatHandler.getTime(horizontalScrollOffset);
                        int[] iArr = this.positions;
                        if (iArr[0] < iArr[1]) {
                            if (time < iArr[0] + 1022) {
                                time = iArr[0] + 1022;
                                WaveFragment.this.mRightRepeatHandler.setTime(time, (float) horizontalScrollOffset);
                            }
                        } else if (time > iArr[0] - 1022) {
                            time = iArr[0] - 1022;
                            WaveFragment.this.mRightRepeatHandler.setTime(time, (float) horizontalScrollOffset);
                        }
                        WaveFragment.this.mRecyclerAdapter.setRepeatEndTime(time);
                    }
                }
                return true;
            }
        };
        inflate.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            public final boolean onGenericMotion(View view, MotionEvent motionEvent) {
                return WaveFragment.this.lambda$onCreateView$6$WaveFragment(view, motionEvent);
            }
        });
        this.mEditCurrentTimeHandler.setOnTouchListener(this.mOnEditCurrentTouchListener);
        this.mLeftTrimHandlerTouchLayout.setOnTouchListener(this.mOnLeftTrimListener);
        this.mRightTrimHandlerTouchLayout.setOnTouchListener(this.mOnRightTrimListener);
        this.mZoomScrollbarView.setOnTouchListener(this.mZoomScrollbarViewTouchListener);
        this.mLeftRepeatHandler.setOnTouchListener(this.mOnLeftRepeatListener);
        this.mRightRepeatHandler.setOnTouchListener(this.mOnRightRepeatListener);
        if (Engine.getInstance().getTranslationState() == 2 || Engine.getInstance().getTranslationState() == 3) {
            setScrollEnableInTranslation(false);
        }
        this.mScaleGestureDetector = new ScaleGestureDetector(getActivity(), new ScaleListener());
        onUpdate(Integer.valueOf(this.mStartingEvent));
        return inflate;
    }

    public /* synthetic */ boolean lambda$onCreateView$3$WaveFragment(View view, MotionEvent motionEvent) {
        if (!this.mScrollable || motionEvent.getPointerCount() != 2) {
            return !this.mScrollable;
        }
        this.mScaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    public /* synthetic */ void lambda$onCreateView$4$WaveFragment(View view, int i, int i2, int i3, int i4) {
        Log.m19d(TAG, "onScrollChange - scrollX:" + i + " oldScrollX:" + i3 + " dX:" + (i3 - i));
        if (i < 0) {
            i = 0;
        } else if (i > this.mZoomScrollViewChildLayout.getWidth() - WaveProvider.WAVE_AREA_WIDTH) {
            i = this.mZoomScrollViewChildLayout.getWidth() - WaveProvider.WAVE_AREA_WIDTH;
        }
        this.mZoomView.scrollTo(i, false);
        this.mEditMode.updateCurrentTime(this.mDuration);
        this.mEditMode.updateTrimHandler();
    }

    public /* synthetic */ void lambda$onCreateView$5$WaveFragment(int i, int i2, boolean z) {
        this.mIsTouchingScrollBar = z;
        if (z) {
            if (i == 2) {
                handleScrollStateDragging();
                postEvent(Event.BLOCK_CONTROL_BUTTONS);
            }
        } else if (i2 == 2) {
            handleScrollStateIdle();
            postEvent(Event.UNBLOCK_CONTROL_BUTTONS);
        }
    }

    public /* synthetic */ boolean lambda$onCreateView$6$WaveFragment(View view, MotionEvent motionEvent) {
        if (!MouseKeyboardProvider.getInstance().isCtrlPressed() || motionEvent.getAction() != 8 || this.mScene != 6) {
            return false;
        }
        float axisValue = motionEvent.getAxisValue(9);
        Log.m26i(TAG, "axisValue : " + axisValue);
        if (axisValue < 0.0f) {
            ScaleStart(1.0f - ((0.0f - axisValue) / 10.0f));
            ScaleEnd();
        } else {
            ScaleStart(axisValue + 0.1f);
            ScaleEnd();
        }
        return false;
    }

    public void onViewCreated(View view, Bundle bundle) {
        Log.m26i(TAG, "onViewCreated - bundle : " + bundle);
        super.onViewCreated(view, bundle);
        Handler handler = this.mEngineEventHandler;
        handler.sendMessage(handler.obtainMessage(2013, 0, 0));
        Engine.getInstance().registerListener(this);
        FragmentController.getInstance().registerSceneChangeListener(this);
        HorizontalScrollView horizontalScrollView = this.mZoomScrollbarView;
        if (horizontalScrollView != null) {
            horizontalScrollView.setImportantForAccessibility(2);
        }
        updateTabletBookmarkListLayout();
        this.mBookmarkList = (ListView) view.findViewById(C0690R.C0693id.bookmark_list_area_list);
        this.mBookmarkListAdapter = new BookmarkListAdapter(getActivity(), -1, loadItem(), getActivity().getLayoutInflater());
        this.mBookmarkListAdapter.registerItemDetailTouchListener(new BookmarkListAdapter.OnItemDetailClickListener() {
            public void onTimeClick(View view, int i, long j, int i2) {
                WaveFragment waveFragment = WaveFragment.this;
                waveFragment.moveToSelectedPosition(waveFragment.mBookmarkListAdapter.getTime(i));
            }

            public void onTitleClick(View view, int i, long j, int i2) {
                WaveFragment.this.showEditTitleDialog(view, i);
            }

            public void onDeleteClick(View view, int i, long j, int i2) {
                WaveFragment.this.postEvent(Event.REMOVE_BOOKMARK);
                int access$2000 = WaveFragment.this.mScene;
                if (access$2000 == 4) {
                    SALogProvider.insertSALog(WaveFragment.this.getActivity().getResources().getString(C0690R.string.screen_player_comm), WaveFragment.this.getActivity().getResources().getString(C0690R.string.event_player_delete_bkm));
                } else if (access$2000 == 6) {
                    SALogProvider.insertSALog(WaveFragment.this.getActivity().getResources().getString(C0690R.string.screen_edit_comm), WaveFragment.this.getActivity().getResources().getString(C0690R.string.event_edit_delete_bkm));
                }
                WaveFragment.this.mBookmarkListArea.sendAccessibilityEvent(8);
                WaveFragment.this.deleteBookmark(i);
                view.clearFocus();
            }
        });
        this.mBookmarkList.setAdapter(this.mBookmarkListAdapter);
        this.mBookmarkList.setDivider((Drawable) null);
        this.mBookmarkEmptyView = view.findViewById(C0690R.C0693id.bookmark_list_empty_view);
        if (VoiceNoteFeature.FLAG_IS_SEM_AVAILABLE) {
//            this.mAddDeleteListAnimator = new SemAddDeleteListAnimator(getContext(), this.mBookmarkList);
//            this.mAddDeleteListAnimator.setOnAddDeleteListener(new SemAddDeleteListAnimator.OnAddDeleteListener() {
//                public void onAnimationEnd(boolean z) {
//                }
//
//                public void onAnimationStart(boolean z) {
//                }
//
//                public void onDelete() {
//                    WaveFragment.this.removeFromBookmarkListAdapter();
//                }
//
//                public void onAdd() {
//                    WaveFragment.this.addToBookmarkListAdapter();
//                }
//            });
        }
        boolean z = true;
        showBookmarkEmptyView(this.mBookmarkListAdapter.getCount() == 0);
        if (bundle != null) {
            if (bundle.getBoolean("KEY_IS_BOOKMARK_SHOWING", false)) {
                showBookmarkList();
            }
        } else if (this.mBookmarkKeepState) {
            showBookmarkList();
            this.mBookmarkKeepState = false;
        }
        if (this.mScene != 6) {
            z = false;
        }
        blockBackGestureOnWaveArea(z);
    }

    /* access modifiers changed from: private */
    public void handleScrollStateDragging() {
        if (Engine.getInstance().getRecorderState() != 1) {
            if (Engine.getInstance().startOverwrite(-1) == 0) {
                this.mCurrentWavePath = Engine.getInstance().getRecentFilePath();
            }
            Recorder.getInstance().setOverwriteByDrag(true);
        }
        if (Engine.getInstance().getPlayerState() == 3) {
            Log.m32w(TAG, "onScrollStateChanged SCROLL_STATE_DRAGGING && PlayerState.PLAYING");
            this.mPlayBarIsMoving = true;
        }
    }

    /* access modifiers changed from: private */
    public void handleScrollStateIdle() {
        initMaxScrollSpeed();
        if (Engine.getInstance().getPlayerState() != 1) {
            int duration = Engine.getInstance().getDuration();
            Log.m26i(TAG, "onScrollStateChanged Play - mRecyclerViewOffset : " + this.mRecyclerViewOffset + " mDuration : " + this.mDuration + " actualDuration : " + duration);
            if (this.mDuration > duration) {
                this.mDuration = duration;
            }
            this.mEngineEventHandler.removeMessages(2012);
            Engine.getInstance().seekTo(this.mDuration);
            SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_SEEK, -1);
            SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_player_comm), getActivity().getResources().getString(C0690R.string.event_player_seek_wave));
            this.mPlayBarIsMoving = false;
        } else if (Engine.getInstance().getRecorderState() != 1) {
            int duration2 = Engine.getInstance().getDuration();
            Log.m26i(TAG, "onScrollStateChanged Record - mRecyclerViewOffset : " + this.mRecyclerViewOffset + " mDuration : " + this.mDuration + " actualDuration : " + duration2);
            if (this.mDuration > duration2) {
                this.mDuration = duration2;
            }
            SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_recording_comm), getActivity().getResources().getString(C0690R.string.event_seek_wave));
            Engine.getInstance().setCurrentTime(this.mDuration);
        } else {
            Log.m22e(TAG, "what ??");
        }
        MetadataRepository.getInstance().resetLastAddedBookmarkTime();
    }

    private void updateWaveFragmentLayout() {
        Log.m26i(TAG, "updateWaveFragmentLayout");
        setWaveViewBackgroundColor();
        updateMainWaveLayout();
        updateInterviewLayout(getView());
        updateHandlerView();
        updateHandlerLayout();
    }

    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("KEY_IS_BOOKMARK_SHOWING", this.mIsBookmarkShowing);
    }

    public void onStart() {
        Log.m26i(TAG, "onStart - mScene:" + this.mScene + " ShrinkMode:" + this.mEditMode.isShrinkMode() + " PlayerState:" + Engine.getInstance().getPlayerState() + " RecorderState:" + Engine.getInstance().getRecorderState());
        super.onStart();
        if (Engine.getInstance().getPlayerState() == 4 || Engine.getInstance().getRecorderState() == 3) {
            this.mRecyclerView.postDelayed(new Runnable() {
                public final void run() {
                    WaveFragment.this.lambda$onStart$7$WaveFragment();
                }
            }, 30);
        }
        if (this.mScene == 6 && this.mEditMode.isShrinkMode()) {
            this.mEditMode.show();
        }
    }

    public /* synthetic */ void lambda$onStart$7$WaveFragment() {
        scrollTo((int) (((float) Engine.getInstance().getCurrentTime()) / WaveProvider.MS_PER_PX));
    }

    public void onResume() {
        Log.m26i(TAG, "onResume");
        super.onResume();
        if (this.mScene == 12) {
            setInterViewLayoutVisibility(8, false, false);
            updateSttTranslationLayout(false);
        } else {
            updateWaveFragmentLayout();
        }
        if (this.mScene == 6) {
            if (Engine.getInstance().getRecorderState() == 1) {
                setTrimVisibility(0);
            }
            this.mRecyclerView.setShowHideScrollBar(false);
        }
        updateCurrentTimeLayout();
    }

    public void onPause() {
        Log.m26i(TAG, "onPause");
        super.onPause();
    }

    public void onUpdate(Object obj) {
        Log.m26i(TAG, "onUpdate : " + obj);
        String str = null;
        if (this.mAsyncLoadWave == null) {
            this.mAsyncLoadWave = new AsyncLoadWave();
        }
        Integer num = (Integer) obj;
        int intValue = num.intValue();
        if (intValue == 11 || intValue == 12) {
            RecyclerAdapter recyclerAdapter = this.mRecyclerAdapter;
            if (recyclerAdapter != null) {
                recyclerAdapter.notifyDataSetChanged();
            }
            setWaveViewBackgroundColor();
        } else {
            int i = 1;
            if (intValue == 17) {
                this.mNeedToUpdateLayout = true;
                this.mZoomView.invalidateZoomView(true);
            } else if (intValue == 21) {
                RecyclerAdapter recyclerAdapter2 = this.mRecyclerAdapter;
                if (recyclerAdapter2 != null) {
                    recyclerAdapter2.notifyDataSetChanged();
                }
                setWaveViewBackgroundColor();
                if (this.mIsBookmarkShowing) {
                    this.mBookmarkKeepState = true;
                }
            } else if (intValue == 975) {
                this.mNeedToUpdateLayout = true;
                String str2 = this.mCurrentWavePath;
                if (str2 != null && !str2.equals(Engine.getInstance().getPath())) {
                    this.mAsyncLoadWave.start(Engine.getInstance().getPath());
                }
                updateCurrentTimeLayout();
                updateInterviewLayout(getView());
            } else if (intValue == 978) {
                int lastRemovedBookmarkTime = MetadataRepository.getInstance().getLastRemovedBookmarkTime();
                Log.m19d(TAG, "onUpdate - REMOVE_BOOKMARK time: " + lastRemovedBookmarkTime);
                if (lastRemovedBookmarkTime != 0) {
                    i = 1 + (lastRemovedBookmarkTime / WaveProvider.DURATION_PER_WAVEVIEW);
                }
                this.mRecyclerAdapter.removeBookmark(i, (lastRemovedBookmarkTime % WaveProvider.DURATION_PER_WAVEVIEW) / 70);
                if (this.mScene == 6 && this.mEditMode.isShrinkMode()) {
                    this.mEditMode.updateBookmark();
                }
                if (this.mBookmarkListAdapter.setPlayingPosition(this.mDuration)) {
                    this.mBookmarkListAdapter.notifyDataSetChanged();
                }
                DialogFactory.clearDialogByTag(getFragmentManager(), DialogFactory.EDIT_BOOKMARK_TITLE);
            } else if (intValue == 996) {
                int lastAddedBookmarkTime = MetadataRepository.getInstance().getLastAddedBookmarkTime();
                Log.m19d(TAG, "onUpdate - ADD_BOOKMARK time: " + lastAddedBookmarkTime);
                if (lastAddedBookmarkTime != 0) {
                    i = 1 + (lastAddedBookmarkTime / WaveProvider.DURATION_PER_WAVEVIEW);
                }
                this.mRecyclerAdapter.addBookmark(i, (lastAddedBookmarkTime % WaveProvider.DURATION_PER_WAVEVIEW) / 70);
                showBookmarkEmptyView(false);
                addBookmarkToBookmarkList(lastAddedBookmarkTime);
                if (this.mBookmarkListAdapter.setPlayingPosition(this.mDuration)) {
                    this.mBookmarkListAdapter.notifyDataSetChanged();
                }
            } else if (intValue == 1993) {
                initialize();
            } else if (intValue == 1998) {
                initialize();
                Engine.getInstance().setCurrentTime(0);
            } else if (intValue != 2001) {
                if (intValue != 5012) {
                    if (intValue == 7005) {
                        this.mDuration = Engine.getInstance().getDuration();
                        updateTranslationStartTime();
                        setScrollEnableInTranslation(true);
                        setScrollEnable(true);
                        updateZoomViewAfterDiscard();
                    } else if (intValue == 955) {
                        hideBookmarkList();
                        scrollTo((int) (((float) this.mDuration) / WaveProvider.MS_PER_PX));
                        updateCurrentTimeLayout();
                    } else if (intValue == 956) {
                        showBookmarkList();
                    } else if (intValue == 5004) {
                        this.mOverwriteReady++;
                        setTrimVisibility(4);
                        this.mEditMode.showExpandMode();
                    } else if (intValue != 5005) {
                        if (intValue == 7001) {
                            updateZoomViewAfterStart();
                            setScrollEnableInTranslation(false);
                        } else if (intValue != 7002) {
                            switch (intValue) {
                                case 1001:
                                    int recorderState = Engine.getInstance().getRecorderState();
                                    if (recorderState == 3 || recorderState == 4) {
                                        this.mRecordFromLeft = false;
                                        setScrollEnable(true);
                                    } else {
                                        this.mRecordFromLeft = true;
                                        setScrollEnable(false);
                                    }
                                    RecyclerAdapter recyclerAdapter3 = this.mRecyclerAdapter;
                                    if (recyclerAdapter3 != null) {
                                        recyclerAdapter3.setRecordMode(getRecordMode());
                                    }
                                    updateInterviewLayout(getView());
                                    break;
                                case 1002:
                                    if (Engine.getInstance().isSaveEnable()) {
                                        setScrollEnable(true);
                                        break;
                                    }
                                    break;
                                case 1003:
                                    this.mOverwriteReady++;
                                    break;
                                default:
                                    switch (intValue) {
                                        case 1006:
                                            initialize();
                                            Engine.getInstance().setCurrentTime(0);
                                            break;
                                        case 1007:
                                        case 1008:
                                            break;
                                        default:
                                            switch (intValue) {
                                                case Event.PLAY_STOP:
                                                    this.mAsyncLoadWave.stop();
                                                    initialize();
                                                    Engine.getInstance().setCurrentTime(0);
                                                    break;
                                                case Event.PLAY_NEXT:
                                                case Event.PLAY_PREV:
                                                    hideBookmarkList();
                                                    String str3 = this.mCurrentWavePath;
                                                    if (str3 == null) {
                                                        this.mAsyncLoadWave.start(Engine.getInstance().getPath());
                                                    } else if (!str3.equals(Engine.getInstance().getPath())) {
                                                        this.mAsyncLoadWave.stop();
                                                        this.mAsyncLoadWave.start(Engine.getInstance().getPath());
                                                    }
                                                    setWaveViewBackgroundColor();
                                                    updateMainWaveLayout();
                                                    updateInterviewLayout(getView());
                                                    updateHandlerView();
                                                    updateHandlerLayout();
                                                    updateBookmarkListAdapter();
                                                    break;
                                            }
                                    }
                            }
                        } else {
                            setScrollEnableInTranslation(false);
                        }
                    }
                }
                this.mEditMode.show();
            } else {
                if (Engine.getInstance().getPlayerState() != 1) {
                    str = Engine.getInstance().getPath();
                } else if (Engine.getInstance().getRecorderState() != 1) {
                    str = Engine.getInstance().getRecentFilePath();
                }
                String str4 = this.mCurrentWavePath;
                if (str4 != null && !str4.equals(str)) {
                    this.mAsyncLoadWave.start(Engine.getInstance().getPath());
                }
            }
        }
        this.mCurrentEvent = num.intValue();
    }

    /* access modifiers changed from: private */
    public float getDistance(float f, MotionEvent motionEvent) {
        int historySize = motionEvent.getHistorySize();
        float f2 = 0.0f;
        float f3 = f;
        int i = 0;
        while (i < historySize) {
            float historicalX = motionEvent.getHistoricalX(0, i);
            f2 -= historicalX - f3;
            i++;
            f3 = historicalX;
        }
        return f2 - (motionEvent.getX(0) - f3);
    }

    /* access modifiers changed from: private */
    public void moveRepeatHandler(FloatingView floatingView, float f, int i) {
        floatingView.setX(floatingView.getX() - f, (float) i);
    }

    private void updateRepeatHandler(FloatingView floatingView, int i) {
        floatingView.update((float) i);
    }

    private void setRepeatHandler(FloatingView floatingView, int i) {
        floatingView.setTime(i, (float) this.mRecyclerView.getHorizontalScrollOffset());
    }

    /* access modifiers changed from: private */
    public void scrollTo(int i) {
        if (isResumed() || !Engine.getInstance().getScreenOff()) {
            this.mRecyclerView.scrollByPosition(i);
            int horizontalScrollOffset = this.mRecyclerView.getHorizontalScrollOffset();
            updateRepeatHandler(this.mLeftRepeatHandler, horizontalScrollOffset);
            updateRepeatHandler(this.mRightRepeatHandler, horizontalScrollOffset);
            this.mEditMode.updateTrimHandler();
            this.mEditMode.updateCurrentTime(this.mDuration);
        }
    }

    private void setInterViewLayoutVisibility(int i, boolean z, boolean z2) {
        Log.m26i(TAG, "setInterViewLayoutVisibility : " + i);
        View view = getView();
        if (getActivity() != null && view != null) {
            view.findViewById(C0690R.C0693id.wave_interview_person_layout).setVisibility(i);
            CheckBox checkBox = (CheckBox) view.findViewById(C0690R.C0693id.wave_interview_top_checkbox);
            CheckBox checkBox2 = (CheckBox) view.findViewById(C0690R.C0693id.wave_interview_bottom_checkbox);
            if (z) {
                checkBox.setVisibility(0);
            } else {
                checkBox.setVisibility(8);
            }
            if (z2) {
                checkBox2.setVisibility(0);
            } else {
                checkBox2.setVisibility(8);
            }
        }
    }

    private void updateMainWaveLayout() {
        Log.m26i(TAG, "updateMainWaveLayout");
        WaveRecyclerView waveRecyclerView = this.mRecyclerView;
        if (waveRecyclerView == null) {
            Log.m32w(TAG, "updateMainWaveLayout - mRecyclerView is null");
            return;
        }
        ViewGroup.LayoutParams layoutParams = waveRecyclerView.getLayoutParams();
        layoutParams.height = WaveProvider.WAVE_HEIGHT;
        this.mRecyclerView.setLayoutParams(layoutParams);
    }

    /* access modifiers changed from: private */
    public void updateZoomViewScrollbarLayout() {
        HorizontalScrollView horizontalScrollView;
        Log.m26i(TAG, "updateZoomViewScrollbarLayout");
        if (getActivity() != null && (horizontalScrollView = this.mZoomScrollbarView) != null) {
            ViewGroup.LayoutParams layoutParams = horizontalScrollView.getLayoutParams();
            layoutParams.height = WaveProvider.WAVE_HEIGHT;
            this.mZoomScrollbarView.setLayoutParams(layoutParams);
        }
    }

    private void updateInterviewLayout(View view) {
        int i;
        StringBuilder sb;
        String str;
        String str2;
        View view2 = view;
        if (getActivity() != null && view2 != null) {
            int recordMode = getRecordMode();
            Log.m26i(TAG, "updateInterviewLayout - mode:" + recordMode + " scene:" + this.mScene);
            if (recordMode != 2 || (i = this.mScene) == 8 || i == 6) {
                setInterViewLayoutVisibility(8, false, false);
                view2.findViewById(C0690R.C0693id.wave_interview_top_overlay_view).setVisibility(8);
                view2.findViewById(C0690R.C0693id.wave_interview_bottom_overlay_view).setVisibility(8);
                return;
            }
            MetadataRepository instance = MetadataRepository.getInstance();
            boolean isEnabledPerson = instance.isEnabledPerson(180.0f);
            boolean isEnabledPerson2 = instance.isEnabledPerson(0.0f);
            String str3 = AssistantProvider.getInstance().getButtonDescriptionForTalkback(C0690R.string.Top_microphone_recording) + ", ";
            String str4 = AssistantProvider.getInstance().getButtonDescriptionForTalkback(C0690R.string.Bottom_microphone_recording) + ", ";
            if (isEnabledPerson2) {
                sb = new StringBuilder();
                sb.append(str3);
                str = getString(C0690R.string.f93on);
            } else {
                sb = new StringBuilder();
                sb.append(str3);
                str = getString(C0690R.string.off);
            }
            sb.append(str);
            String sb2 = sb.toString();
            if (isEnabledPerson) {
                str2 = str4 + getString(C0690R.string.f93on);
            } else {
                str2 = str4 + getString(C0690R.string.off);
            }
            boolean isExistedPerson = instance.isExistedPerson(180.0f);
            boolean isExistedPerson2 = instance.isExistedPerson(0.0f);
            if (this.mScene != 6) {
                FrameLayout frameLayout = (FrameLayout) view2.findViewById(C0690R.C0693id.wave_interview_person_layout);
                frameLayout.setVisibility(0);
                frameLayout.bringToFront();
                CheckBox checkBox = (CheckBox) view2.findViewById(C0690R.C0693id.wave_interview_top_checkbox);
//                checkBox.semSetHoverPopupType(1);
                checkBox.setChecked(isEnabledPerson2);
                checkBox.setOnCheckedChangeListener(this);
                enableCheckBox(checkBox);
                CheckBox checkBox2 = (CheckBox) view2.findViewById(C0690R.C0693id.wave_interview_bottom_checkbox);
//                checkBox2.semSetHoverPopupType(1);
                checkBox2.setChecked(isEnabledPerson);
                checkBox2.setOnCheckedChangeListener(this);
                enableCheckBox(checkBox2);
                view2.findViewById(C0690R.C0693id.wave_interview_top_checkbox_layout).setContentDescription(sb2);
                view2.findViewById(C0690R.C0693id.wave_interview_bottom_checkbox_layout).setContentDescription(str2);
                setInterViewLayoutVisibility(0, isExistedPerson2, isExistedPerson);
            }
            if (!isExistedPerson2) {
                view2.findViewById(C0690R.C0693id.wave_interview_top_overlay_view).setVisibility(0);
            } else if (isEnabledPerson2) {
                view2.findViewById(C0690R.C0693id.wave_interview_top_overlay_view).setVisibility(8);
                Engine.getInstance().setMute(false, false);
            } else {
                view2.findViewById(C0690R.C0693id.wave_interview_top_overlay_view).setVisibility(0);
                Engine.getInstance().setMute(true, false);
            }
            if (!isExistedPerson) {
                view2.findViewById(C0690R.C0693id.wave_interview_bottom_overlay_view).setVisibility(0);
            } else if (isEnabledPerson) {
                view2.findViewById(C0690R.C0693id.wave_interview_bottom_overlay_view).setVisibility(8);
                if (!isExistedPerson2 || isEnabledPerson2) {
                    Engine.getInstance().setMute(false, false);
                } else {
                    Engine.getInstance().setMute(true, false);
                }
            } else {
                view2.findViewById(C0690R.C0693id.wave_interview_bottom_overlay_view).setVisibility(0);
                Engine.getInstance().setMute(false, true);
            }
        }
    }

    private void enableCheckBox(View view) {
        if (Engine.getInstance().getSkipSilenceMode() == 3) {
            view.setEnabled(false);
            view.setFocusable(false);
            return;
        }
        view.setEnabled(true);
        view.setFocusable(true);
    }

    private void updateCurrentTimeLayout() {
        float f;
        float f2;
        int i;
        if (!this.mRecordFromLeft || (i = this.mDuration) >= WaveProvider.START_RECORD_MARGIN) {
            f2 = ((float) WaveProvider.WAVE_AREA_WIDTH) * 1.0f;
            f = 2.0f;
        } else {
            f2 = (((float) i) + WaveProvider.DURATION_PER_HAFT_OF_WAVE_AREA) - ((float) WaveProvider.START_RECORD_MARGIN);
            f = WaveProvider.MS_PER_PX;
        }
//        this.mCurrentTimeLayout.post(new Runnable(f2 / f) {
//            private final /* synthetic */ float f$1;
//
//            {
//                this.f$1 = r2;
//            }
//
//            public final void run() {
//                WaveFragment.this.lambda$updateCurrentTimeLayout$8$WaveFragment(this.f$1);
//            }
//        });
    }

    public /* synthetic */ void lambda$updateCurrentTimeLayout$8$WaveFragment(float f) {
        this.mCurrentTimeLayout.setX(f);
    }

    private void updateHandlerLayout() {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        Log.m26i(TAG, "updateHandlerLayout");
        View view = getView();
        if (view == null) {
            Log.m32w(TAG, "updateHandlerLayout - root view is null");
            return;
        }
        View findViewById = view.findViewById(C0690R.C0693id.wave_left_trim_handler_image_wrapper);
        View findViewById2 = view.findViewById(C0690R.C0693id.wave_right_trim_handler_image_wrapper);
        ViewGroup.LayoutParams layoutParams = this.mCurrentLineView.getLayoutParams();
        int height = findViewById.getHeight();
        int i8 = layoutParams.height;
        int recordMode = getRecordMode();
        if (recordMode != 2) {
            i5 = WaveProvider.WAVE_VIEW_HEIGHT;
            i4 = getResources().getDimensionPixelSize(C0690R.dimen.wave_bookmark_top_margin);
            i3 = WaveProvider.WAVE_HEIGHT;
            i2 = WaveProvider.WAVE_VIEW_HEIGHT;
            i = WaveProvider.WAVE_HEIGHT - getResources().getDimensionPixelSize(C0690R.dimen.wave_bookmark_top_margin);
            i7 = getResources().getDimensionPixelSize(C0690R.dimen.wave_current_bar_margin_top);
            i6 = 0;
        } else {
            i5 = WaveProvider.WAVE_VIEW_HEIGHT + getResources().getDimensionPixelSize(C0690R.dimen.wave_time_text_height);
            i3 = WaveProvider.WAVE_HEIGHT;
            i2 = WaveProvider.WAVE_VIEW_HEIGHT + getResources().getDimensionPixelSize(C0690R.dimen.wave_time_text_height);
            int i9 = WaveProvider.WAVE_VIEW_HEIGHT;
            i = i9 / 2;
            i6 = i9 / 2;
            i7 = getResources().getDimensionPixelSize(C0690R.dimen.wave_bookmark_top_margin);
            i4 = 0;
        }
        Log.m26i(TAG, "updateHandlerLayout - mode : " + recordMode);
        if (!(height == i5 && i8 == i2)) {
            changeHeightView(findViewById, i5);
            changeMarginTopView(this.mLeftTrimHandlerLayout, i4);
            changeHeightView(this.mLeftTrimHandlerLayout.findViewById(C0690R.C0693id.handler_top_part), i);
            changeHeightView(this.mLeftTrimHandlerLayout.findViewById(C0690R.C0693id.handler_bottom_part), i6);
            changeHeightView(findViewById2, i5);
            changeMarginTopView(this.mRightTrimHandlerLayout, i4);
            changeHeightView(this.mRightTrimHandlerLayout.findViewById(C0690R.C0693id.handler_top_part), i);
            changeHeightView(this.mRightTrimHandlerLayout.findViewById(C0690R.C0693id.handler_bottom_part), i6);
            changeHeightView(this.mRightRepeatHandler, i3);
            changeHeightView(this.mRightRepeatHandler.findViewById(C0690R.C0693id.handler_top_part), i);
            changeHeightView(this.mRightRepeatHandler.findViewById(C0690R.C0693id.handler_bottom_part), i6);
            changeHeightView(this.mLeftRepeatHandler, i3);
            changeHeightView(this.mLeftRepeatHandler.findViewById(C0690R.C0693id.handler_top_part), i);
            changeHeightView(this.mLeftRepeatHandler.findViewById(C0690R.C0693id.handler_bottom_part), i6);
            changeHeightView(this.mCurrentLineView, i2);
            changeHeightView(this.mCurrentLineView.findViewById(C0690R.C0693id.handler_top_part), i);
            changeHeightView(this.mCurrentLineView.findViewById(C0690R.C0693id.handler_bottom_part), i6);
            changeHeightView(this.mEditCurrentLineView, i2);
            changeHeightView(this.mEditCurrentLineView.findViewById(C0690R.C0693id.handler_top_part), i);
            changeHeightView(this.mEditCurrentLineView.findViewById(C0690R.C0693id.handler_bottom_part), i6);
            changeMarginTopView(this.mCurrentTimeLayout, i7);
            changeMarginTopView(this.mEditCurrentTimeHandler, i7);
            if (recordMode == 2) {
                View findViewById3 = getView().findViewById(C0690R.C0693id.wave_interview_top_overlay_view);
                View findViewById4 = getView().findViewById(C0690R.C0693id.wave_interview_bottom_overlay_view);
                View findViewById5 = getView().findViewById(C0690R.C0693id.wave_interview_top_checkbox_layout);
                View findViewById6 = getView().findViewById(C0690R.C0693id.wave_interview_bottom_checkbox_layout);
                changeHeightView(findViewById3, i);
                changeHeightView(findViewById4, i6);
                changeMarginTopView(findViewById3, i7);
                int i10 = WaveProvider.WAVE_VIEW_HEIGHT / 2;
                int dimensionPixelSize = getResources().getDimensionPixelSize(C0690R.dimen.wave_time_text_height) + i10 + i7;
                int dimensionPixelSize2 = getResources().getDimensionPixelSize(C0690R.dimen.wave_interview_checkbox_layout_height);
                int dimensionPixelSize3 = getResources().getDimensionPixelSize(C0690R.dimen.wave_interview_checkbox_layout_margin_top);
                if (i10 < dimensionPixelSize2 + dimensionPixelSize3 && (dimensionPixelSize3 = i10 - dimensionPixelSize2) < 0) {
                    dimensionPixelSize3 = 0;
                }
                changeMarginTopView(findViewById5, i7 + dimensionPixelSize3);
                changeMarginTopView(findViewById6, dimensionPixelSize3 + dimensionPixelSize);
                changeMarginTopView(findViewById4, dimensionPixelSize);
            }
        }
        int i11 = this.mScene;
        if (i11 == 6 || i11 == 4) {
            this.mCurrentLineView.setVisibility(0);
        } else if (i11 == 8) {
            int recorderState = Engine.getInstance().getRecorderState();
            if (recorderState == 3 || recorderState == 4) {
                this.mRecordFromLeft = false;
            }
            if (Engine.getInstance().getRecorderState() == 1 || (this.mRecordFromLeft && this.mDuration <= 0)) {
                this.mCurrentLineView.setVisibility(8);
            } else {
                this.mCurrentLineView.setVisibility(0);
            }
        }
    }

    private void changeHeightView(View view, int i) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = i;
        view.setLayoutParams(layoutParams);
    }

    private void changeMarginTopView(View view, int i) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        layoutParams.topMargin = i;
        view.setLayoutParams(layoutParams);
    }

    /* access modifiers changed from: private */
    public void setTrimVisibility(int i) {
        Log.m26i(TAG, "setTrimVisibility : " + i);
        this.mLeftTrimHandlerLayout.setVisibility(i);
        this.mRightTrimHandlerLayout.setVisibility(i);
    }

    /* access modifiers changed from: private */
    public void setTrimEnabled(boolean z) {
        Log.m26i(TAG, "setTrimEnabled : " + z);
        this.mLeftTrimHandlerImageView.setEnabled(z);
        this.mLeftTrimHandlerImageView.setFocusable(z);
        this.mRightTrimHandlerImageView.setEnabled(z);
        this.mRightTrimHandlerImageView.setFocusable(z);
    }

    private void setRepeatVisibility(int i) {
        this.mLeftRepeatHandler.setVisibility(i);
        this.mRightRepeatHandler.setVisibility(i);
    }

    public void onEngineUpdate(int i, int i2, int i3) {
        Handler handler = this.mEngineEventHandler;
        if (handler != null) {
            handler.sendMessage(handler.obtainMessage(i, i2, i3));
        }
    }

    public void onDestroyView() {
        Log.m26i(TAG, "onDestroyView");
        Engine.getInstance().unregisterListener(this);
        FragmentController.getInstance().unregisterSceneChangeListener(this);
        initialize();
        setProgressHoverWindow(this.mZoomView, false);
        if (getView() != null) {
            ((CheckBox) getView().findViewById(C0690R.C0693id.wave_interview_top_checkbox)).setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
            ((CheckBox) getView().findViewById(C0690R.C0693id.wave_interview_bottom_checkbox)).setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
        }
        this.mBookmarkList.postDelayed(new Runnable() {
            public final void run() {
                WaveFragment.this.lambda$onDestroyView$9$WaveFragment();
            }
        }, 200);
        this.mOnEditCurrentTouchListener = null;
        this.mOnLeftTrimListener = null;
        this.mOnRightTrimListener = null;
        this.mZoomScrollbarViewTouchListener = null;
        this.mOnLeftRepeatListener = null;
        this.mOnRightRepeatListener = null;
        super.onDestroyView();
    }

    public /* synthetic */ void lambda$onDestroyView$9$WaveFragment() {
        DialogFactory.clearDialogByTag(getFragmentManager(), DialogFactory.EDIT_BOOKMARK_TITLE);
    }

    public void onDestroy() {
        Log.m26i(TAG, "onDestroy");
        this.mAsyncLoadWave.stop();
        this.mRecyclerAdapter.cleanUp();
        this.mCurrentWavePath = null;
        this.mEngineEventHandler = null;
        super.onDestroy();
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        CompoundButton compoundButton2 = compoundButton;
        Log.m26i(TAG, "onCheckedChanged - isChecked : " + z);
        if (getView() == null || compoundButton2 == null || !isResumed()) {
        } else if (Engine.getInstance().getPlayerState() == 1) {
            compoundButton2.setChecked(!compoundButton.isChecked());
            Log.m32w(TAG, "onCheckedChanged - play state is idle");
        } else {
            SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_SELECT_TRACK, -1);
            MetadataRepository instance = MetadataRepository.getInstance();
            boolean isExistedPerson = instance.isExistedPerson(180.0f);
            boolean isExistedPerson2 = instance.isExistedPerson(0.0f);
            boolean isEnabledPerson = instance.isEnabledPerson(180.0f);
            boolean isEnabledPerson2 = instance.isEnabledPerson(0.0f);
            int id = compoundButton.getId();
            if (id != C0690R.C0693id.wave_interview_bottom_checkbox) {
                if (id == C0690R.C0693id.wave_interview_top_checkbox && isExistedPerson2) {
                    if (compoundButton.isChecked()) {
                        getView().findViewById(C0690R.C0693id.wave_interview_top_overlay_view).setVisibility(8);
                        instance.enablePersonal(0.0f, true);
                        Engine.getInstance().setMute(false, false);
                        Engine.getInstance().updateTrack();
                        SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_player_interview), getActivity().getResources().getString(C0690R.string.event_top_voice), "1");
                    } else if (isEnabledPerson) {
                        getView().findViewById(C0690R.C0693id.wave_interview_top_overlay_view).setVisibility(0);
                        instance.enablePersonal(0.0f, false);
                        Engine.getInstance().setMute(true, false);
                        Engine.getInstance().updateTrack();
                        SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_player_interview), getActivity().getResources().getString(C0690R.string.event_top_voice), "0");
                    } else {
                        compoundButton2.setChecked(true);
                        Toast.makeText(getActivity(), C0690R.string.track_list_warning, 0).show();
                    }
                }
            } else if (isExistedPerson) {
                if (compoundButton.isChecked()) {
                    getView().findViewById(C0690R.C0693id.wave_interview_bottom_overlay_view).setVisibility(8);
                    instance.enablePersonal(180.0f, true);
                    Engine.getInstance().setMute(false, false);
                    Engine.getInstance().updateTrack();
                    SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_player_interview), getActivity().getResources().getString(C0690R.string.event_bottom_voice), "1");
                } else if (isEnabledPerson2) {
                    getView().findViewById(C0690R.C0693id.wave_interview_bottom_overlay_view).setVisibility(0);
                    instance.enablePersonal(180.0f, false);
                    Engine.getInstance().setMute(false, true);
                    Engine.getInstance().updateTrack();
                    SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_player_interview), getActivity().getResources().getString(C0690R.string.event_bottom_voice), "0");
                } else {
                    compoundButton2.setChecked(true);
                    Toast.makeText(getActivity(), C0690R.string.track_list_warning, 0).show();
                }
            }
            updateInterviewLayout(getView());
        }
    }

    private void initialize() {
        Log.m26i(TAG, "initialize");
        scrollTo((int) (((float) Engine.getInstance().getCurrentTime()) / WaveProvider.MS_PER_PX));
        setScrollEnable(false);
        this.mDuration = 0;
        updateMainWaveLayout();
        updateInterviewLayout(getView());
        updateHandlerLayout();
        setTrimVisibility(8);
        this.mPlayBarIsMoving = false;
        this.mRecordFromLeft = false;
    }

    private void setScrollEnableInTranslation(boolean z) {
        Log.m19d(TAG, " setScrollEnableInTranslation : " + z);
        if (z) {
            this.mEditCurrentTimeHandler.setOnTouchListener(this.mOnEditCurrentTouchListener);
            this.mZoomScrollbarView.setOnTouchListener(this.mZoomScrollbarViewTouchListener);
            return;
        }
        this.mEditCurrentTimeHandler.setOnTouchListener((View.OnTouchListener) null);
        this.mZoomScrollbarView.setOnTouchListener((View.OnTouchListener) null);
    }

    /* access modifiers changed from: private */
    public void setScrollEnable(boolean z) {
        if (z && Engine.getInstance().getRecorderState() == 2) {
            Log.m32w(TAG, "setScrollEnable SKIP while RECORDING");
        } else if (!z || !(Engine.getInstance().getTranslationState() == 2 || Engine.getInstance().getTranslationState() == 3)) {
            Log.m26i(TAG, "setScrollEnable : " + z);
            this.mScrollable = z;
            if (this.mScene != 12) {
                this.mRecyclerView.setHorizontalScrollBarEnabled(z);
            }
            if (this.mScrollable) {
                this.mEditCurrentTimeHandler.setOnTouchListener(this.mOnEditCurrentTouchListener);
                this.mLeftTrimHandlerTouchLayout.setOnTouchListener(this.mOnLeftTrimListener);
                this.mRightTrimHandlerTouchLayout.setOnTouchListener(this.mOnRightTrimListener);
                return;
            }
            this.mEditCurrentTimeHandler.setOnTouchListener((View.OnTouchListener) null);
            this.mLeftTrimHandlerTouchLayout.setOnTouchListener((View.OnTouchListener) null);
            this.mRightTrimHandlerTouchLayout.setOnTouchListener((View.OnTouchListener) null);
        } else {
            Log.m32w(TAG, "setScrollEnable SKIP while Converting STT");
        }
    }

    /* access modifiers changed from: private */
    public void loadBookmarkData() {
        int i;
        int i2;
        Log.m26i(TAG, "loadBookmarkData");
        if (this.mRecyclerAdapter == null) {
            Log.m22e(TAG, "mRecyclerAdapter is null");
            return;
        }
        ArrayList<Bookmark> bookmarkList = MetadataRepository.getInstance().getBookmarkList();
        if (bookmarkList == null) {
            i = 0;
        } else {
            i = bookmarkList.size();
        }
        this.mRecyclerAdapter.clearBookmarks();
        for (int i3 = 0; i3 < i; i3++) {
            try {
                i2 = bookmarkList.get(i3).getElapsed();
            } catch (IndexOutOfBoundsException e) {
                Log.m24e(TAG, "IndexOutOfBoundsException", (Throwable) e);
                i2 = -1;
            }
            if (i2 >= 0) {
                Log.m19d(TAG, "loadBookmarkData - duration : " + i2);
                int i4 = 1;
                if (i2 != 0) {
                    i4 = 1 + (i2 / WaveProvider.DURATION_PER_WAVEVIEW);
                }
                this.mRecyclerAdapter.addBookmark(i4, (i2 % WaveProvider.DURATION_PER_WAVEVIEW) / 70);
            }
        }
    }

    /* access modifiers changed from: private */
    public String getStringByDuration(int i) {
        int round = Math.round(((float) i) / 10.0f) / 100;
        int i2 = round / 3600;
        int i3 = (round / 60) % 60;
        int i4 = round % 60;
        if (i2 > 10) {
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4)});
        } else if (i2 > 0) {
            return String.format(Locale.getDefault(), "%01d:%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4)});
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(i4)});
        }
    }

    public void onSceneChange(int i) {
        int i2;
        int i3;
        int i4;
        Log.m26i(TAG, "onSceneChange scene : " + i + " mScene : " + this.mScene);
        if (isAdded() && !isRemoving() && (i2 = this.mScene) != i) {
            if (i2 == 6 && i == 4) {
                this.mScene = i;
                loadBookmarkData();
                updateBookmarkListAdapter();
            } else {
                this.mScene = i;
            }
            Engine.getInstance().setScene(this.mScene);
            int i5 = this.mScene;
            boolean z = true;
            if (i5 == 4) {
                if (this.mNeedToUpdateLayout) {
                    updateWaveFragmentLayout();
                    this.mNeedToUpdateLayout = false;
                } else {
                    updateInterviewLayout(getView());
                }
                this.mRecyclerAdapter.setRepeatTime(-1, -1);
                this.mRecyclerView.setShowHideScrollBar(true);
                Engine.getInstance().resetOverwriteTime();
                Engine.getInstance().resetTrimTime();
                this.mEditMode.hideEditMode();
                this.mEditMode.showExpandMode();
                this.mWaveBgArea.setVisibility(0);
            } else if (i5 == 6) {
                this.mEngineEventHandler.removeMessages(2012);
                setRepeatVisibility(8);
                RecyclerAdapter recyclerAdapter = this.mRecyclerAdapter;
                if (recyclerAdapter != null) {
                    recyclerAdapter.setRepeatStartTime(-1);
                    this.mRecyclerAdapter.setRepeatEndTime(-1);
                }
                int trimEndTime = Engine.getInstance().getTrimEndTime();
                if (Engine.getInstance().getTrimStartTime() < 0) {
                    Engine.getInstance().setTrimStartTime(0);
                }
                if (trimEndTime < 0) {
                    Engine.getInstance().setTrimEndTime(Engine.getInstance().getDuration());
                }
                this.mDuration = Engine.getInstance().getCurrentTime();
                setInterViewLayoutVisibility(8, false, false);
                updateWaveFragmentLayout();
                this.mWaveBgArea.setVisibility(0);
                if (Engine.getInstance().getRepeatMode() == 4) {
                    int[] repeatPosition = Engine.getInstance().getRepeatPosition();
                    if (repeatPosition[0] > repeatPosition[1]) {
                        i4 = repeatPosition[0];
                        i3 = repeatPosition[1];
                    } else {
                        i4 = repeatPosition[1];
                        i3 = repeatPosition[0];
                    }
                    if (i4 - i3 > 1000) {
                        Engine.getInstance().setTrimStartTime(i3);
                        Engine.getInstance().setTrimEndTime(i4);
//                        this.mLeftTrimHandlerLayout.postDelayed(new Runnable(i3, i4) {
//                            private final /* synthetic */ int f$1;
//                            private final /* synthetic */ int f$2;
//
//                            {
//                                this.f$1 = r2;
//                                this.f$2 = r3;
//                            }
//
//                            public final void run() {
//                                WaveFragment.this.lambda$onSceneChange$10$WaveFragment(this.f$1, this.f$2);
//                            }
//                        }, 10);
                        postEvent(Event.UNBLOCK_CONTROL_BUTTONS);
                    }
                }
                WaveRecyclerView waveRecyclerView = this.mRecyclerView;
                if (waveRecyclerView != null) {
                    waveRecyclerView.stopScroll();
                    this.mRecyclerView.setShowHideScrollBar(false);
                }
                if (this.mRecyclerAdapter != null) {
                    this.mEditMode.show();
                }
                Engine.getInstance().setRepeatMode(2);
                Engine.getInstance().setPlaySpeed(1.0f);
                Engine.getInstance().setSkipSilenceMode(4);
                resetInterviewPersonal();
            } else if (i5 != 12) {
                if (Engine.getInstance().getRecorderState() == 1) {
                    Engine.getInstance().resetOverwriteTime();
                }
                Engine.getInstance().resetTrimTime();
                this.mEditMode.hideEditMode();
            } else {
                updateSttTranslationLayout(true);
                Engine.getInstance().setRepeatMode(2);
                Engine.getInstance().setPlaySpeed(1.0f);
                Engine.getInstance().setSkipSilenceMode(4);
                resetInterviewPersonal();
                WaveRecyclerView waveRecyclerView2 = this.mRecyclerView;
                if (waveRecyclerView2 != null) {
                    waveRecyclerView2.stopScroll();
                    this.mRecyclerView.setFastScrollEnable(false);
                }
            }
            if (this.mScene != 6) {
                z = false;
            }
            blockBackGestureOnWaveArea(z);
        }
    }

    public /* synthetic */ void lambda$onSceneChange$10$WaveFragment(int i, int i2) {
        this.mEditMode.updateLeftTrimHandler(i);
        this.mEditMode.updateRightTrimHandler(i2);
    }

    private void setWaveViewBackgroundColor() {
        int i;
        View view = getView();
        if (view == null) {
            Log.m22e(TAG, "setWaveViewBackgroundColor - root view is null");
            return;
        }
        FrameLayout frameLayout = (FrameLayout) view.findViewById(C0690R.C0693id.recycler_view_wave_bg);
        FrameLayout frameLayout2 = (FrameLayout) view.findViewById(C0690R.C0693id.recycler_view_time_bg);
        if (frameLayout == null || frameLayout2 == null) {
            Log.m22e(TAG, "setWaveViewBackgroundColor - FrameLayout is null");
            return;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) frameLayout.getLayoutParams();
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) frameLayout2.getLayoutParams();
        if (getRecordMode() != 2) {
            i = 0;
        } else {
            i = WaveProvider.WAVE_VIEW_HEIGHT / 2;
        }
        layoutParams.height = (WaveProvider.WAVE_HEIGHT - getResources().getDimensionPixelSize(C0690R.dimen.wave_repeat_margin_bottom)) - getResources().getDimensionPixelSize(C0690R.dimen.wave_repeat_icon_size);
        layoutParams2.topMargin = i;
        frameLayout.setLayoutParams(layoutParams);
        frameLayout2.setLayoutParams(layoutParams2);
    }

    public void onDialogResult(DialogFragment dialogFragment, Bundle bundle) {
        if (bundle != null && bundle.getInt(DialogFactory.BUNDLE_REQUEST_CODE) == 16) {
            String string = bundle.getString(DialogFactory.BUNDLE_NAME);
            this.mBookmarkListAdapter.updateTitle(bundle.getInt("result_code"), string);
            this.mBookmarkListAdapter.notifyDataSetChanged();
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.WaveFragment$ScaleListener */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private ScaleListener() {
        }

        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            if (WaveFragment.this.mScene != 6) {
                return false;
            }
            float scaleFactor = scaleGestureDetector.getScaleFactor();
            Log.m26i(WaveFragment.TAG, "ScaleListener scale : " + scaleFactor);
            WaveFragment.this.ScaleStart(scaleFactor);
            return false;
        }

        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            super.onScaleEnd(scaleGestureDetector);
            WaveFragment.this.ScaleEnd();
        }
    }

    /* access modifiers changed from: private */
    public void ScaleStart(float f) {
        Log.m26i(TAG, "onScaleStart scale : " + f);
        this.mZoomView.setZoomScale(f, false);
        boolean unused = this.mEditMode.mIsZooming = true;
        enableAutoScroll(false, 0);
        this.mEditMode.updateCurrentTime(this.mDuration);
        this.mEditMode.updateTrimHandler();
        View childAt = this.mZoomScrollViewChildLayout.getChildAt(0);
        if (childAt != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
            layoutParams.width = this.mZoomView.getTotalWidth();
            childAt.setLayoutParams(layoutParams);
        }
    }

    /* access modifiers changed from: private */
    public void ScaleEnd() {
        Log.m26i(TAG, "onScaleEnd scale : ");
        this.mZoomScrollbarView.scrollTo((int) this.mZoomView.setZoomEnd(), 0);
        this.mZoomScrollbarView.postDelayed(new Runnable() {
            public final void run() {
                WaveFragment.this.lambda$ScaleEnd$11$WaveFragment();
            }
        }, 500);
        enableAutoScroll(true, 2000);
        SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_edit_comm), getActivity().getResources().getString(C0690R.string.event_zoom));
    }

    public /* synthetic */ void lambda$ScaleEnd$11$WaveFragment() {
        boolean unused = this.mEditMode.mIsZooming = false;
    }

    /* renamed from: com.sec.android.app.voicenote.ui.WaveFragment$LoadWaveHandler */
    private static class LoadWaveHandler extends Handler {
        WeakReference<WaveFragment> mWeakRef;

        LoadWaveHandler(WaveFragment waveFragment) {
            this.mWeakRef = new WeakReference<>(waveFragment);
        }

        public void handleMessage(Message message) {
            WaveFragment waveFragment = (WaveFragment) this.mWeakRef.get();
            if (waveFragment != null) {
                int i = message.what;
                if (i == 2) {
                    waveFragment.mAsyncLoadWave.onLoadWaveContinue();
                } else if (i == 3) {
                    waveFragment.mAsyncLoadWave.onLoadWaveEnd();
                }
                super.handleMessage(message);
            }
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.WaveFragment$AsyncLoadWave */
    private class AsyncLoadWave {
        private static final int NUMBER_WAVE_DRAW_PER_TIME = 2;
        private int[] amplitudeData;
        private int amplitudePos;
        private int[] buf;
        private int bufSize;
        private int drewCount;
        private boolean isDone;
        private boolean[] isDrewItem;
        private float mDuration;
        private int mWaveViewSize;
        private int[] newWave;
        private int size;
        private int viewIndexLeft;
        private int viewIndexRight;

        private AsyncLoadWave() {
            this.amplitudePos = 0;
            this.viewIndexRight = 1;
            this.viewIndexLeft = 1;
            this.buf = null;
            this.isDone = true;
            this.mWaveViewSize = 0;
            this.mDuration = 0.0f;
        }

        public void start(String str) {
            int i;
            Log.m19d(WaveFragment.TAG, "AsyncLoadWave.start - newTitle : " + WaveFragment.this.getTitle(str));
            StringBuilder sb = new StringBuilder();
            sb.append("AsyncLoadWave.start - mCurrentWaveTitle : ");
            WaveFragment waveFragment = WaveFragment.this;
            sb.append(waveFragment.getTitle(waveFragment.mCurrentWavePath));
            Log.m19d(WaveFragment.TAG, sb.toString());
            if (!str.equals(WaveFragment.this.mCurrentWavePath)) {
                MetadataRepository instance = MetadataRepository.getInstance();
                this.mDuration = (float) instance.getDuration();
                if (WaveFragment.this.mRecyclerAdapter != null) {
                    WaveFragment.this.mRecyclerAdapter.clearBookmarks();
                    WaveFragment.this.mRecyclerAdapter.setRepeatStartTime(-1);
                    WaveFragment.this.mRecyclerAdapter.setRepeatEndTime(-1);
                }
                if ((str.endsWith(AudioFormat.ExtType.EXT_M4A) || str.endsWith(AudioFormat.ExtType.EXT_AMR) || str.endsWith(AudioFormat.ExtType.EXT_3GA)) && instance.isWaveMakerWorking()) {
                    instance.registerListener(new MetadataRepository.OnVoiceMetadataListener() {
                        public final void onWaveMakerFinished(int i, int i2) {
                            WaveFragment.AsyncLoadWave.this.lambda$start$1$WaveFragment$AsyncLoadWave(i, i2);
                        }
                    });
                    Log.m32w(WaveFragment.TAG, "AsyncLoadWave.start - WAIT for waveMaker");
                    return;
                }
                if (Engine.getInstance().getContentItemCount() > 1) {
                    ContentItem peekContentItem = Engine.getInstance().peekContentItem();
                    if (peekContentItem != null) {
                        this.amplitudeData = instance.getOverWriteWaveData(peekContentItem.getStartTime(), Engine.getInstance().getCurrentTime());
                        int[] iArr = this.amplitudeData;
                        this.size = iArr == null ? 0 : iArr.length;
                        this.mDuration = (float) Engine.getInstance().getDuration();
                        RecyclerAdapter access$3400 = WaveFragment.this.mRecyclerAdapter;
                        int i2 = this.size;
                        int i3 = WaveProvider.NUM_OF_AMPLITUDE;
                        access$3400.setIndex((i2 / i3) + 1, i2 % i3);
                        Log.m32w(WaveFragment.TAG, "AsyncLoadWave.start - getOverWriteWaveData : " + this.size);
                    }
                } else {
                    this.amplitudeData = instance.getWaveData();
                    this.size = instance.getWaveDataSize();
                    if (this.size < instance.getAmplitudeCollectorSize()) {
                        this.amplitudeData = instance.getAmplitudeCollector();
                        this.size = instance.getAmplitudeCollectorSize();
                        Log.m32w(WaveFragment.TAG, "AsyncLoadWave.start - getAmplitudeCollectorSize : " + this.size);
                    } else {
                        Log.m32w(WaveFragment.TAG, "AsyncLoadWave.start - getWaveDataSize : " + this.size);
                    }
                }
                if (this.size <= 0) {
                    Log.m32w(WaveFragment.TAG, "AsyncLoadWave.start - wave size is under 0 : " + this.size);
                    return;
                }
                if (this.mDuration == 0.0f) {
                    this.mDuration = (float) Engine.getInstance().getDuration();
                }
                Log.m26i(WaveFragment.TAG, "AsyncLoadWave.start - version : " + this.amplitudeData[0]);
                Log.m26i(WaveFragment.TAG, "AsyncLoadWave.start - duration : " + this.mDuration);
                this.size = (int) Math.ceil((double) (this.mDuration / 35.0f));
                Log.m26i(WaveFragment.TAG, "AsyncLoadWave.start - convert size : " + this.size);
                this.size = (int) Math.ceil((double) (((float) this.size) / 2.0f));
                this.newWave = new int[this.size];
                for (int i4 = 0; i4 < this.size; i4++) {
                    int i5 = i4 * 2;
                    int i6 = i5 + 1;
                    int[] iArr2 = this.amplitudeData;
                    if (i5 > iArr2.length - 1) {
                        i5 = iArr2.length - 1;
                    }
                    int[] iArr3 = this.amplitudeData;
                    if (i6 > iArr3.length - 1) {
                        i6 = iArr3.length - 1;
                    }
                    int[] iArr4 = this.amplitudeData;
                    this.newWave[i4] = ((((iArr4[i5] >> 16) + (iArr4[i6] >> 16)) / 2) << 16) + (((iArr4[i5] & SupportMenu.USER_MASK) + (iArr4[i6] & SupportMenu.USER_MASK)) / 2);
                }
                Log.m26i(WaveFragment.TAG, "AsyncLoadWave.start - newWave amplitude size : " + this.size);
                this.isDone = false;
                String unused = WaveFragment.this.mCurrentWavePath = str;
                this.mWaveViewSize = (int) Math.ceil((double) (((this.mDuration * 1.0f) / 70.0f) / ((float) WaveProvider.NUM_OF_AMPLITUDE)));
                Log.m26i(WaveFragment.TAG, "AsyncLoadWave.start - WaveView size : " + this.mWaveViewSize);
                if (WaveFragment.this.mCurrentWavePath.isEmpty() || DBProvider.getInstance().getIdByPath(WaveFragment.this.mCurrentWavePath) == -1) {
                    i = WaveFragment.this.getRecordMode();
                } else {
                    i = DBProvider.getInstance().getRecordModeByPath(WaveFragment.this.mCurrentWavePath);
                }
                if (i != WaveFragment.this.mRecyclerAdapter.getRecordMode()) {
                    WaveFragment.this.mRecyclerAdapter.setRecordMode(i);
                    WaveFragment.this.sendEvent(951);
                }
                WaveFragment.this.mRecyclerAdapter.initialize(WaveFragment.this.getActivity(), WaveFragment.this.getRecordMode(), this.mWaveViewSize + 2, this.size);
                this.isDrewItem = new boolean[(this.mWaveViewSize + 2)];
                this.drewCount = 0;
                WaveFragment.this.mLoadWaveHandler.removeMessages(2);
                WaveFragment.this.mLoadWaveHandler.removeMessages(3);
                boolean unused2 = WaveFragment.this.mIsLoadCompleted = false;
                WaveFragment.this.mLoadWaveHandler.sendEmptyMessage(2);
            } else if (Engine.getInstance().getPlayerState() == 4 || Engine.getInstance().getRecorderState() == 3) {
                WaveFragment.this.mRecyclerView.postDelayed(new Runnable() {
                    public final void run() {
                        WaveFragment.AsyncLoadWave.this.lambda$start$0$WaveFragment$AsyncLoadWave();
                    }
                }, 30);
            }
        }

        public /* synthetic */ void lambda$start$0$WaveFragment$AsyncLoadWave() {
            WaveFragment.this.scrollTo((int) (((float) Engine.getInstance().getCurrentTime()) / WaveProvider.MS_PER_PX));
        }

        public /* synthetic */ void lambda$start$1$WaveFragment$AsyncLoadWave(int i, int i2) {
            Log.m32w(WaveFragment.TAG, "AsyncLoadWave.start - onWaveMakerFinished");
            WaveFragment.this.mAsyncLoadWave.start(Engine.getInstance().getPath());
        }

        public void stop() {
            Log.m26i(WaveFragment.TAG, "AsyncLoadWave.stop");
            this.isDone = true;
            this.viewIndexRight = 1;
            this.viewIndexLeft = 1;
            this.drewCount = 0;
            boolean[] zArr = this.isDrewItem;
            if (zArr != null) {
                Arrays.fill(zArr, false);
            }
            WaveFragment.this.mLoadWaveHandler.removeMessages(2);
            WaveFragment.this.mLoadWaveHandler.removeMessages(3);
        }

        public boolean isDone() {
            return this.isDone;
        }

        private void onDrawWaveContinue() {
            int currentLatestItem = WaveFragment.this.mRecyclerAdapter.getCurrentLatestItem();
            if (currentLatestItem > 0 && currentLatestItem <= this.mWaveViewSize && !this.isDrewItem[currentLatestItem]) {
                this.viewIndexRight = currentLatestItem;
                this.viewIndexLeft = currentLatestItem - 1;
            }
            for (int i = 0; i < 2; i++) {
                int i2 = this.viewIndexRight;
                if (i2 <= this.mWaveViewSize) {
                    if (!this.isDrewItem[i2]) {
                        drawOneWaveView(i2);
                    }
                    this.viewIndexRight++;
                }
                int i3 = this.viewIndexLeft;
                if (i3 > 0) {
                    if (!this.isDrewItem[i3]) {
                        drawOneWaveView(i3);
                    }
                    this.viewIndexLeft--;
                }
            }
        }

        private void drawOneWaveView(int i) {
            this.amplitudePos = (i - 1) * WaveProvider.NUM_OF_AMPLITUDE;
            if (this.amplitudePos < this.size) {
                int[] iArr = this.buf;
                if (iArr != null) {
                    Arrays.fill(iArr, -1);
                }
                try {
                    if (this.size - this.amplitudePos < WaveProvider.NUM_OF_AMPLITUDE) {
                        this.buf = Arrays.copyOfRange(this.newWave, this.amplitudePos, this.size);
                        this.bufSize = this.size % WaveProvider.NUM_OF_AMPLITUDE;
                    } else {
                        this.buf = Arrays.copyOfRange(this.newWave, this.amplitudePos, this.amplitudePos + WaveProvider.NUM_OF_AMPLITUDE);
                        this.bufSize = WaveProvider.NUM_OF_AMPLITUDE;
                    }
                    Log.m19d(WaveFragment.TAG, "AsyncLoadWave - update item index : " + i);
                    if (!(this.mWaveViewSize != i || Engine.getInstance().getRecorderState() == 2 || Engine.getInstance().getRecorderState() == 3)) {
                        WaveFragment.this.mRecyclerAdapter.clearView(i);
                    }
                    if (this.buf != null) {
                        WaveFragment.this.mRecyclerAdapter.updateDataArray(WaveFragment.this.getContext(), i, this.buf, this.bufSize);
                        addBookmarkForWaveView(i);
                    }
                    WaveFragment.this.mRecyclerAdapter.setRepeatTime(i);
                    this.isDrewItem[i] = true;
                    this.drewCount++;
                } catch (ArrayIndexOutOfBoundsException unused) {
                    Log.m22e(WaveFragment.TAG, "amplitudePos < 0 or amplitudePos > amplitudeData.length");
                } catch (IllegalArgumentException unused2) {
                    Log.m22e(WaveFragment.TAG, "amplitudePos > amplitudePos + WaveProvider.NUM_OF_AMPLITUDE");
                } catch (NullPointerException unused3) {
                    Log.m22e(WaveFragment.TAG, "amplitudeData = null");
                }
            }
        }

        private void addBookmarkForWaveView(int i) {
            ArrayList<Bookmark> bookmarkList = MetadataRepository.getInstance().getBookmarkList();
            if (bookmarkList != null && !bookmarkList.isEmpty()) {
                int size2 = bookmarkList.size();
                ArrayList arrayList = new ArrayList();
                for (int i2 = 0; i2 < size2; i2++) {
                    int elapsed = bookmarkList.get(i2).getElapsed();
                    int i3 = 1;
                    if (elapsed != 0) {
                        i3 = 1 + (elapsed / WaveProvider.DURATION_PER_WAVEVIEW);
                    }
                    if (i3 == i) {
                        int i4 = (elapsed % WaveProvider.DURATION_PER_WAVEVIEW) / 70;
                        if (Engine.getInstance().getOverwriteStartTime() > elapsed || elapsed > Engine.getInstance().getOverwriteEndTime()) {
                            Log.m26i(WaveFragment.TAG, "addBookmarkForWaveView add bookmark viewIndex = " + i);
                            WaveFragment.this.mRecyclerAdapter.addBookmark(i3, i4);
                        } else {
                            Log.m26i(WaveFragment.TAG, "addBookmarkForWaveView remove bookmark viewIndex = " + i);
                            WaveFragment.this.mRecyclerAdapter.removeBookmark(i3, i4);
                            arrayList.add(Integer.valueOf(elapsed));
                        }
                    } else if (i3 > i) {
                        break;
                    }
                }
                if (!arrayList.isEmpty()) {
                    for (int i5 = 0; i5 < arrayList.size(); i5++) {
                        WaveFragment waveFragment = WaveFragment.this;
                        waveFragment.deleteBookmark(waveFragment.mBookmarkListAdapter.expectDeletePosition(((Integer) arrayList.get(i5)).intValue()));
                    }
                    WaveFragment.this.postEvent(Event.REMOVE_BOOKMARK);
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void onLoadWaveContinue() {
            if (this.drewCount < this.mWaveViewSize) {
                onDrawWaveContinue();
                WaveFragment.this.mLoadWaveHandler.sendEmptyMessageDelayed(2, 30);
                return;
            }
            WaveFragment.this.mLoadWaveHandler.sendEmptyMessage(3);
        }

        /* access modifiers changed from: package-private */
        public void onLoadWaveEnd() {
            Log.m19d(WaveFragment.TAG, "AsyncLoadWave - end size : " + this.drewCount);
            WaveFragment.this.loadBookmarkData();
            WaveFragment.this.updateBookmarkListAdapter();
            this.isDone = true;
            this.viewIndexRight = 1;
            this.viewIndexLeft = 1;
            this.drewCount = 0;
            boolean[] zArr = this.isDrewItem;
            if (zArr != null) {
                Arrays.fill(zArr, false);
            }
            WaveFragment.this.mRecyclerView.postDelayed(new Runnable() {
                public final void run() {
                    WaveFragment.AsyncLoadWave.this.lambda$onLoadWaveEnd$2$WaveFragment$AsyncLoadWave();
                }
            }, 30);
            boolean unused = WaveFragment.this.mIsLoadCompleted = true;
        }

        public /* synthetic */ void lambda$onLoadWaveEnd$2$WaveFragment$AsyncLoadWave() {
            Log.m19d(WaveFragment.TAG, "do updateScrollViewWidth");
            if (!WaveFragment.this.mRecordFromLeft || this.mDuration > ((float) WaveProvider.START_RECORD_MARGIN)) {
                WaveFragment.this.scrollTo((int) (((float) Engine.getInstance().getCurrentTime()) / WaveProvider.MS_PER_PX));
            }
        }
    }

    private void updateHandlerView() {
        if (getView() == null) {
            Log.m26i(TAG, "updateHandlerView but getView returns null");
            return;
        }
        Log.m26i(TAG, "updateHandlerView");
        this.mCurrentLineView.update();
        this.mEditCurrentLineView.update();
        ((HandlerView) getView().findViewById(C0690R.C0693id.wave_left_repeat_handler)).update();
        ((HandlerView) getView().findViewById(C0690R.C0693id.wave_right_repeat_handler)).update();
        ((HandlerView) getView().findViewById(C0690R.C0693id.wave_left_trim_handler_line)).update();
        ((HandlerView) getView().findViewById(C0690R.C0693id.wave_right_trim_handler_line)).update();
    }

    /* access modifiers changed from: private */
    public int getRecordMode() {
        int recordMode = MetadataRepository.getInstance().getRecordMode();
        Log.m26i(TAG, "getRecordMode() : mScene = " + this.mScene + ", metadata recordMode = " + recordMode);
        if (Engine.getInstance().isSimpleRecorderMode()) {
            recordMode = Settings.getIntSettings("record_mode", 1);
        } else if (recordMode == 0) {
            int i = this.mScene;
            if (i == 8 || i == 1 || Engine.getInstance().isSimpleRecorderMode()) {
                recordMode = Settings.getIntSettings("record_mode", 1);
            } else {
                recordMode = Settings.getIntSettings(Settings.KEY_PLAY_MODE, 1);
            }
        } else if (this.mScene == 12) {
            recordMode = 4;
        }
        Log.m26i(TAG, "getRecordMode() : mScene = " + this.mScene + ", final recordMode = " + recordMode);
        return recordMode;
    }

    private void resetInterviewPersonal() {
        if (getRecordMode() == 2) {
            Log.m26i(TAG, "resetInterviewPersonal");
            MetadataRepository instance = MetadataRepository.getInstance();
            boolean isExistedPerson = instance.isExistedPerson(180.0f);
            if (instance.isExistedPerson(0.0f)) {
                instance.enablePersonal(0.0f, true);
            }
            if (isExistedPerson) {
                instance.enablePersonal(180.0f, true);
            }
            Engine.getInstance().setMute(false, false);
        }
    }

    private void initMaxScrollSpeed() {
        int i = this.mDefaultMaxScrollSpeed;
        this.mCurrentMaxScrollSpeed = i;
        changeMaxScrollSpeed(i);
    }

    /* access modifiers changed from: private */
    public void changeMaxScrollSpeed(int i) {
        try {
            Field declaredField = this.mRecyclerView.getClass().getSuperclass().getDeclaredField("mMaxFlingVelocity");
            declaredField.setAccessible(true);
            declaredField.set(this.mRecyclerView, Integer.valueOf(i));
            this.mCurrentMaxScrollSpeed = i;
        } catch (NoSuchFieldException e) {
            Log.m24e(TAG, "changeMaxScrollSpeed NoSuchFieldException", (Throwable) e);
        } catch (IllegalAccessException e2) {
            Log.m24e(TAG, "changeMaxScrollSpeed IllegalAccessException", (Throwable) e2);
        }
    }

    private int getCurrentMaxScrollSpeed() {
        try {
            Field declaredField = this.mRecyclerView.getClass().getSuperclass().getDeclaredField("mMaxFlingVelocity");
            declaredField.setAccessible(true);
            return declaredField.getInt(this.mRecyclerView);
        } catch (NoSuchFieldException e) {
            Log.m24e(TAG, "getCurrentMaxScrollSpeed NoSuchMethodException", (Throwable) e);
            return 32000;
        } catch (IllegalAccessException e2) {
            Log.m24e(TAG, "getCurrentMaxScrollSpeed IllegalAccessException", (Throwable) e2);
            return 32000;
        }
    }

    private void setProgressHoverWindow(final View view, boolean z) {
        if (view != null && getActivity() != null && VoiceNoteFeature.isSupportHoveringUI()) {
            if (z) {
//                view.semSetHoverPopupType(3);
//                final SemHoverPopupWindow semGetHoverPopup = view.semGetHoverPopup(true);
                view.setOnHoverListener(new View.OnHoverListener() {
                    static final int IGNORE_MOVE_EVENT_COUNT = 2;
                    int hoverPositionX;
                    int hoverPositionY;
                    int hoverTime;
                    private TextView mTime;
                    int mode = WaveFragment.this.getRecordMode();
                    int moveEventCount = 0;

                    public boolean onHover(View view, MotionEvent motionEvent) {
                        int action = motionEvent.getAction();
                        if (action == 7) {
                            int i = this.moveEventCount;
                            this.moveEventCount = i + 1;
                            if (i == 0) {
                                updateProgressbarPreviewView(motionEvent.getX(), motionEvent.getY());
                            } else if (this.moveEventCount > 2) {
                                this.moveEventCount = 0;
                            }
                        } else if (action == 9) {
                            createHoverPopupWindow();
                            updateProgressbarPreviewView(motionEvent.getX(), motionEvent.getY());
                        }
                        return false;
                    }

                    private int getPixel(int i) {
                        Resources resources = WaveFragment.this.getResources();
                        if (resources == null) {
                            return 0;
                        }
                        return resources.getDimensionPixelSize(i);
                    }

                    @SuppressLint({"InflateParams"})
                    private void createHoverPopupWindow() {
                        this.mTime = (TextView) LayoutInflater.from(WaveFragment.this.getActivity()).inflate(C0690R.layout.hover_window_layout, (ViewGroup) null);
//                        SemHoverPopupWindow semHoverPopupWindow = semGetHoverPopup;
//                        if (semHoverPopupWindow != null && this.mTime != null) {
//                            semHoverPopupWindow.setGravity(3);
//                        }
                    }

                    private boolean checkBoundary(float f, float f2) {
                        int i = WaveProvider.WAVE_VIEW_HEIGHT;
                        int pixel = getPixel(C0690R.dimen.wave_time_text_height) + getPixel(C0690R.dimen.wave_bookmark_top_margin);
                        int pixel2 = i + pixel + getPixel(C0690R.dimen.wave_time_text_height);
                        if (f2 < ((float) pixel) || f2 > ((float) pixel2)) {
                            return false;
                        }
                        if (view != WaveFragment.this.mZoomView) {
                            return true;
                        }
                        this.hoverTime = (int) (WaveFragment.this.mZoomView.getStartTime() + (f * WaveFragment.this.mZoomView.getMsPerPx()));
                        if (this.hoverTime < Engine.getInstance().getTrimStartTime() || this.hoverTime > Engine.getInstance().getTrimEndTime()) {
                            return false;
                        }
                        return true;
                    }

                    private String getFixedTimeFormat(int i) {
                        int i2 = i / 1000;
                        return String.format(Locale.US, "%02d:%02d:%02d", new Object[]{Integer.valueOf(i2 / 3600), Integer.valueOf((i2 / 60) % 60), Integer.valueOf(i2 % 60)});
                    }

                    private void updateProgressbarPreviewView(float f, float f2) {
                        int i;
//                        if (semGetHoverPopup != null && this.mTime != null) {
//                            if (!checkBoundary(f, f2)) {
//                                try {
//                                    semGetHoverPopup.dismiss();
//                                } catch (Exception e) {
//                                    Log.m22e(WaveFragment.TAG, e.toString());
//                                }
//                            } else if (!semGetHoverPopup.isShowing()) {
//                                semGetHoverPopup.show();
//                            }
//                            this.hoverPositionX = ((int) f) - (this.mTime.getWidth() / 2);
//                            if (!HWKeyboardProvider.isHWKeyboard(WaveFragment.this.getContext())) {
//                                i = getPixel(C0690R.dimen.player_amplitude_time_hover_y);
//                            } else {
//                                i = getPixel(C0690R.dimen.hw_keyboard_player_amplitude_time_hover_y);
//                            }
//                            this.hoverPositionY = i;
//                            this.mTime.setText(getFixedTimeFormat(this.hoverTime));
//                            try {
//                                semGetHoverPopup.setOffset(this.hoverPositionX, this.hoverPositionY);
//                                semGetHoverPopup.setContent(this.mTime);
//                                semGetHoverPopup.update();
//                            } catch (Exception e2) {
//                                Log.m22e(WaveFragment.TAG, e2.toString());
//                            }
//                        }
                    }
                });
                return;
            }
            view.setOnHoverListener((View.OnHoverListener) null);
        }
    }

    public void enableAutoScroll(boolean z, long j) {
        this.mScrollerHandler.removeMessages(0);
        this.mScrollerHandler.removeMessages(1);
        this.mScrollerHandler.sendEmptyMessageDelayed(z ? 1 : 0, j);
    }

    /* access modifiers changed from: private */
    public String getTitle(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        return str.substring(str.lastIndexOf(47) + 1, str.lastIndexOf(46));
    }

    /* renamed from: com.sec.android.app.voicenote.ui.WaveFragment$EditMode */
    private class EditMode {
        private boolean mEnableAutoScroll;
        /* access modifiers changed from: private */
        public boolean mIsZooming;
        private boolean mShrinkMode;

        private EditMode() {
            this.mShrinkMode = false;
            this.mEnableAutoScroll = false;
            this.mIsZooming = false;
        }

        /* access modifiers changed from: private */
        public boolean isShrinkMode() {
            return this.mShrinkMode;
        }

        public void init() {
            WaveFragment.this.setTrimEnabled(true);
            WaveFragment.this.setScrollEnable(true);
            int unused = WaveFragment.this.mDuration = 0;
            Engine.getInstance().setCurrentTime(WaveFragment.this.mDuration, true);
            Engine.getInstance().startPlay(Engine.getInstance().getRecentFilePath(), -1, false);
            WaveFragment.this.mAsyncLoadWave.start(Engine.getInstance().getRecentFilePath());
            int duration = Engine.getInstance().getDuration();
            Engine.getInstance().setTrimStartTime(0);
            Engine.getInstance().setTrimEndTime(duration);
            show();
        }

        public void show() {
            showZoomMode();
        }

        private void showZoomMode() {
            if (Engine.getInstance().getRecorderState() == 2) {
                Log.m32w(WaveFragment.TAG, "showZoomMode RETURN by RECORDING");
                return;
            }
            Log.m26i(WaveFragment.TAG, "showZoomMode");
            this.mShrinkMode = true;
            WaveFragment.this.mEditCurrentTimeHandler.setVisibility(0);
            WaveFragment.this.setTrimVisibility(0);
            WaveFragment.this.mRecyclerView.setVisibility(4);
            WaveFragment.this.mCurrentTimeLayout.setVisibility(8);
            WaveFragment.this.mZoomView.startZoom(true);
            WaveFragment.this.mZoomView.setVisibility(0);
            WaveFragment.this.mZoomScrollbarView.setVisibility(0);
            WaveFragment.this.mZoomScrollbarView.setAlpha(0.0f);
            WaveFragment.this.updateZoomViewScrollbarLayout();
            if (WaveFragment.this.mZoomScrollViewChildLayout != null) {
                WaveFragment.this.mZoomScrollViewChildLayout.removeAllViews();
                WaveFragment.this.mZoomScrollViewChildLayout.addView(new View(WaveFragment.this.getActivity()), WaveFragment.this.mZoomView.getTotalWidth(), 1);
            }
            updateCurrentTime(WaveFragment.this.mDuration);
            updateTrimHandler();
        }

        /* access modifiers changed from: private */
        public void updateWave() {
            WaveFragment.this.mZoomView.invalidateZoomView(false);
        }

        /* access modifiers changed from: private */
        public void updateBookmark() {
            WaveFragment.this.mZoomView.invalidateZoomView(false);
        }

        /* access modifiers changed from: private */
        public void showExpandMode() {
            Log.m26i(WaveFragment.TAG, "showExpandMode");
            this.mShrinkMode = false;
            WaveFragment.this.mRecyclerView.setVisibility(0);
            WaveFragment.this.mCurrentTimeLayout.setVisibility(0);
            WaveFragment.this.mZoomView.dismissPercentPopup();
            WaveFragment.this.mZoomView.setVisibility(8);
            WaveFragment.this.mZoomScrollbarView.setVisibility(8);
            WaveFragment.this.mEditCurrentTimeHandler.setVisibility(8);
            WaveFragment.this.mEditMode.updateTrimHandler();
        }

        /* access modifiers changed from: private */
        public void hideEditMode() {
            WaveFragment.this.mRecyclerView.setVisibility(0);
            WaveFragment.this.mCurrentTimeLayout.setVisibility(0);
            WaveFragment.this.mZoomView.setVisibility(8);
            WaveFragment.this.mZoomScrollbarView.setVisibility(8);
            WaveFragment.this.mEditCurrentTimeHandler.setVisibility(8);
            WaveFragment.this.setTrimVisibility(8);
        }

        /* access modifiers changed from: private */
        public float getMsPerPx() {
            return WaveFragment.this.mZoomView.getMsPerPx();
        }

        /* access modifiers changed from: private */
        public void updateCurrentTime(int i) {
            if (WaveFragment.this.getActivity() != null && WaveFragment.this.getView() != null) {
                if (WaveFragment.this.mScene == 6 || WaveFragment.this.mScene == 12 || this.mShrinkMode) {
                    float f = -WaveFragment.this.getResources().getDimension(C0690R.dimen.wave_edit_current_line_view_width);
                    if (Engine.getInstance().getTranslationState() != 1) {
                        i -= Engine.getInstance().getTrimStartTime();
                    }
                    float startTime = ((((float) i) - WaveFragment.this.mZoomView.getStartTime()) / WaveFragment.this.mZoomView.getMsPerPx()) + f;
                    float dimension = ((float) WaveProvider.WAVE_AREA_WIDTH) - WaveFragment.this.getResources().getDimension(C0690R.dimen.wave_edit_shrink_view_margin);
                    if (startTime < dimension || !this.mEnableAutoScroll) {
                        WaveFragment.this.mEditCurrentTimeHandler.setX(startTime);
                    } else {
                        WaveFragment.this.mEditCurrentTimeHandler.setX(dimension);
                        WaveFragment.this.mZoomScrollbarView.scrollBy((int) (startTime - dimension), 0);
                    }
                    FloatingView access$6500 = WaveFragment.this.mEditCurrentTimeHandler;
                    access$6500.setContentDescription(WaveFragment.this.getResources().getString(C0690R.string.seek_control) + ", " + AssistantProvider.getInstance().stringForReadTime(WaveFragment.this.getStringByDuration(i)));
                    FrameLayout access$7100 = WaveFragment.this.mWaveBgArea;
                    access$7100.setContentDescription(WaveFragment.this.getResources().getString(C0690R.string.seek_control) + ", " + AssistantProvider.getInstance().stringForReadTime(WaveFragment.this.getStringByDuration(i)));
                }
            }
        }

        /* access modifiers changed from: private */
        public void updateTrimHandler() {
            updateLeftTrimHandler(Engine.getInstance().getTrimStartTime());
            updateRightTrimHandler(Engine.getInstance().getTrimEndTime());
        }

        /* access modifiers changed from: private */
        public void updateLeftTrimHandler(int i) {
            if (WaveFragment.this.getActivity() != null && WaveFragment.this.getView() != null && WaveFragment.this.mScene == 6) {
                Log.m26i(WaveFragment.TAG, "updateLeftTrimHandler : " + i);
                String access$7000 = WaveFragment.this.getStringByDuration(i);
                String stringForReadTime = AssistantProvider.getInstance().stringForReadTime(access$7000);
                if (this.mShrinkMode) {
                    float startTime = ((((float) i) - WaveFragment.this.mZoomView.getStartTime()) / WaveFragment.this.mZoomView.getMsPerPx()) + ((-WaveFragment.this.getResources().getDimension(C0690R.dimen.wave_trim_handler_margin_start)) - WaveFragment.this.getResources().getDimension(C0690R.dimen.wave_edit_shrink_view_margin));
                    WaveFragment.this.mLeftTrimHandlerTime.setText(access$7000);
                    WaveFragment.this.mLeftTrimHandlerTime.setContentDescription(stringForReadTime);
                    WaveFragment.this.mLeftTrimHandlerLayout.setX(startTime);
                    ImageView access$2700 = WaveFragment.this.mLeftTrimHandlerImageView;
                    access$2700.setContentDescription(stringForReadTime + ", " + WaveFragment.this.getResources().getString(C0690R.string.drag_with_two_fingers_to_trim));
                    return;
                }
                float waveViewWidthDimension = WaveProvider.getInstance().getWaveViewWidthDimension();
                float dimension = WaveFragment.this.getResources().getDimension(C0690R.dimen.wave_trim_handler_layout_width);
                float horizontalScrollOffset = (((((float) i) / WaveProvider.MS_PER_PX) - ((float) WaveFragment.this.mRecyclerView.getHorizontalScrollOffset())) + waveViewWidthDimension) - (dimension / 2.0f);
                WaveFragment.this.mLeftTrimHandlerTime.setText(access$7000);
                WaveFragment.this.mLeftTrimHandlerTime.setContentDescription(AssistantProvider.getInstance().stringForReadTime(access$7000));
                WaveFragment.this.mLeftTrimHandlerLayout.setX(horizontalScrollOffset);
            }
        }

        /* access modifiers changed from: private */
        public void updateRightTrimHandler(int i) {
            if (WaveFragment.this.getActivity() != null && WaveFragment.this.getView() != null && WaveFragment.this.mScene == 6) {
                Log.m26i(WaveFragment.TAG, "updateRightTrimHandler : " + i);
                String access$7000 = WaveFragment.this.getStringByDuration(i);
                String stringForReadTime = AssistantProvider.getInstance().stringForReadTime(access$7000);
                if (this.mShrinkMode) {
                    float startTime = ((((float) i) - WaveFragment.this.mZoomView.getStartTime()) / WaveFragment.this.mZoomView.getMsPerPx()) + (((float) ((-WaveFragment.this.getResources().getDimensionPixelOffset(C0690R.dimen.wave_trim_handler_margin_end)) - 4)) - WaveFragment.this.getResources().getDimension(C0690R.dimen.wave_edit_shrink_view_margin));
                    WaveFragment.this.mRightTrimHandlerTime.setText(access$7000);
                    WaveFragment.this.mRightTrimHandlerTime.setContentDescription(stringForReadTime);
                    WaveFragment.this.mRightTrimHandlerLayout.setX(startTime);
                    ImageView access$3500 = WaveFragment.this.mRightTrimHandlerImageView;
                    access$3500.setContentDescription(stringForReadTime + ", " + WaveFragment.this.getResources().getString(C0690R.string.drag_with_two_fingers_to_trim));
                    return;
                }
                float waveViewWidthDimension = WaveProvider.getInstance().getWaveViewWidthDimension();
                float dimension = WaveFragment.this.getResources().getDimension(C0690R.dimen.wave_trim_handler_layout_width);
                int horizontalScrollOffset = WaveFragment.this.mRecyclerView.getHorizontalScrollOffset();
                WaveFragment.this.mRightTrimHandlerTime.setText(access$7000);
                WaveFragment.this.mRightTrimHandlerTime.setContentDescription(stringForReadTime);
                WaveFragment.this.mRightTrimHandlerLayout.setX((((((float) i) / WaveProvider.MS_PER_PX) - ((float) horizontalScrollOffset)) + waveViewWidthDimension) - (dimension / 2.0f));
            }
        }

        /* access modifiers changed from: private */
        public void setEnableAutoScroll(boolean z) {
            this.mEnableAutoScroll = z;
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.WaveFragment$ScrollerHandler */
    private static class ScrollerHandler extends Handler {
        WeakReference<WaveFragment> mWeakRef;

        ScrollerHandler(WaveFragment waveFragment) {
            this.mWeakRef = new WeakReference<>(waveFragment);
        }

        public void handleMessage(Message message) {
            WaveFragment waveFragment = (WaveFragment) this.mWeakRef.get();
            if (waveFragment != null) {
                int i = message.what;
                if (i == 0) {
                    waveFragment.mEditMode.setEnableAutoScroll(false);
                } else if (i == 1 && Engine.getInstance().getPlayerState() == 3) {
                    if (((float) waveFragment.mDuration) < waveFragment.mZoomView.getStartTime() || ((float) waveFragment.mDuration) > waveFragment.mZoomView.getEndTime()) {
                        int access$1900 = (int) (((float) waveFragment.mDuration) * waveFragment.mZoomView.getPxPerMs());
                        Log.m19d(WaveFragment.TAG, "ZoomView SCROLL to : " + access$1900 + " duration : " + waveFragment.mDuration);
                        waveFragment.mZoomScrollbarView.smoothScrollTo(access$1900, 0);
                    }
                    waveFragment.mEditMode.setEnableAutoScroll(true);
                }
            }
        }
    }

    private void showBookmarkList() {
        int playingPosition;
        Log.m19d(TAG, "showBookmarkList");
        swapView(getView(), C0690R.C0693id.bookmark_list_area, C0690R.C0693id.wave_area);
        this.mIsBookmarkShowing = true;
        int i = this.mScene;
        if (i == 4) {
            SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_player_comm), getActivity().getResources().getString(C0690R.string.event_player_change_wave_to_bkm_list));
        } else if (i == 6) {
            SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_edit_comm), getActivity().getResources().getString(C0690R.string.event_edit_change_wave_to_bkm_list));
        }
        if (this.mBookmarkList != null && (playingPosition = this.mBookmarkListAdapter.getPlayingPosition()) >= 0) {
            this.mBookmarkList.smoothScrollToPosition(playingPosition);
        }
    }

    private void hideBookmarkList() {
        Log.m19d(TAG, "hideBookmarkList");
        swapView(getView(), C0690R.C0693id.wave_area, C0690R.C0693id.bookmark_list_area);
        this.mIsBookmarkShowing = false;
        int i = this.mScene;
        if (i == 4) {
            SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_player_comm), getActivity().getResources().getString(C0690R.string.event_player_change_bkm_list_to_wave));
        } else if (i == 6) {
            SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_edit_comm), getActivity().getResources().getString(C0690R.string.event_edit_change_bkm_list_to_wave));
        }
    }

    private void swapView(View view, int i, int i2) {
        if (view != null) {
            View findViewById = view.findViewById(i);
            View findViewById2 = view.findViewById(i2);
            if (findViewById.getAlpha() != 1.0f || findViewById2.getAlpha() != 0.0f) {
                findViewById.bringToFront();
                findViewById.setVisibility(0);
                findViewById.setAlpha(0.0f);
                findViewById2.setVisibility(8);
                Animator loadAnimator = AnimatorInflater.loadAnimator(getContext(), C0690R.animator.anim_hide);
                Animator loadAnimator2 = AnimatorInflater.loadAnimator(getContext(), C0690R.animator.anim_show);
                loadAnimator2.setTarget(findViewById);
                loadAnimator2.start();
                loadAnimator.setTarget(findViewById2);
                loadAnimator.start();
            }
        }
    }

    private void addBookmarkToBookmarkList(int i) {
        if (i != -1) {
            this.mAddTime = i;
            if (VoiceNoteFeature.FLAG_IS_SEM_AVAILABLE) {
                int expectInsertPosition = this.mBookmarkListAdapter.expectInsertPosition(i);
                ArrayList arrayList = new ArrayList();
                arrayList.add(Integer.valueOf(expectInsertPosition));
//                this.mAddDeleteListAnimator.setInsert(arrayList);
                return;
            }
            addToBookmarkListAdapter();
        }
    }

    /* access modifiers changed from: private */
    public void showEditTitleDialog(View view, int i) {
        if (!DialogFactory.isDialogVisible(getFragmentManager(), DialogFactory.EDIT_BOOKMARK_TITLE) && this.mBookmarkListArea.getAlpha() == 1.0f) {
            String charSequence = ((TextView) view.findViewById(C0690R.C0693id.bookmark_item_title)).getText().toString();
            Bundle bundle = new Bundle();
            bundle.putString(DialogFactory.BUNDLE_NAME, charSequence);
            bundle.putInt(DialogFactory.BUNDLE_REQUEST_CODE, 16);
            bundle.putInt("result_code", i);
            bundle.putInt(DialogFactory.BUNDLE_TITLE_ID, C0690R.string.bookmark_text_edit);
            bundle.putInt(DialogFactory.BUNDLE_POSITIVE_BTN_ID, C0690R.string.add_note);
            bundle.putInt(DialogFactory.BUNDLE_NEGATIVE_BTN_ID, C0690R.string.cancel);
            DialogFactory.show(getFragmentManager(), DialogFactory.EDIT_BOOKMARK_TITLE, bundle, this);
            int i2 = this.mScene;
            if (i2 == 4) {
                SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_player_comm), getActivity().getResources().getString(C0690R.string.event_player_enter_bkm_memo));
            } else if (i2 == 6) {
                SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_edit_comm), getActivity().getResources().getString(C0690R.string.event_edit_enter_bkm_memo));
            }
        }
    }

    /* access modifiers changed from: private */
    public void moveToSelectedPosition(int i) {
        Log.m26i(TAG, "moveToSelectedPosition - time : " + i);
        Engine instance = Engine.getInstance();
        int recorderState = instance.getRecorderState();
        if (recorderState == 3) {
            CursorProvider.getInstance().resetCurrentPlayingItemPosition();
            int resumePlay = instance.resumePlay(false);
            if (resumePlay < 0) {
                errorHandler(resumePlay);
            }
            Message message = new Message();
            message.arg1 = i;
            message.arg2 = 10;
            message.what = 0;
            this.mDelayHandler.removeMessages(0);
            this.mDelayHandler.sendMessageDelayed(message, 0);
        } else if (recorderState != 2) {
            if (instance.getPlayerState() == 1) {
                CursorProvider.getInstance().resetCurrentPlayingItemPosition();
                int resumePlay2 = instance.resumePlay(false);
                if (resumePlay2 < 0) {
                    errorHandler(resumePlay2);
                }
                instance.pausePlay();
            }
            if (this.mScene != 6 || (instance.getTrimStartTime() <= i && instance.getTrimEndTime() >= i)) {
                instance.seekTo(i);
            }
        }
        if (Engine.getInstance().getPlayerState() == 4) {
            int resumePlay3 = Engine.getInstance().resumePlay();
            if (resumePlay3 == -103) {
                Toast.makeText(getActivity(), C0690R.string.no_play_during_call, 0).show();
            } else if (resumePlay3 == 0 && this.mScene == 4) {
                postEvent(Event.PLAY_RESUME);
                SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_player_comm), getActivity().getResources().getString(C0690R.string.event_player_play));
            }
        }
        int i2 = this.mScene;
        if (i2 == 4) {
            SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_player_comm), getActivity().getResources().getString(C0690R.string.event_player_go_to_bkm));
        } else if (i2 == 6) {
            SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_edit_comm), getActivity().getResources().getString(C0690R.string.event_edit_go_to_bkm));
        }
    }

    private void errorHandler(int i) {
        if (i < -100) {
            Log.m26i(TAG, "errorHandler - errorCode : " + i);
            switch (i) {
                case -120:
                    Toast.makeText(getActivity(), C0690R.string.recording_now, 0).show();
                    Log.m22e(TAG, "ANOTHER_RECORDER_ALREADY_RUNNING !!!!");
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
                    DialogFactory.show(getFragmentManager(), DialogFactory.MODE_NOT_SUPPORTED, (Bundle) null);
                    return;
                case -104:
                    DialogFactory.show(getFragmentManager(), DialogFactory.MODE_NOT_SUPPORTED, (Bundle) null);
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

    /* renamed from: com.sec.android.app.voicenote.ui.WaveFragment$DelayHandler */
    private static class DelayHandler extends Handler {
        private DelayHandler() {
        }

        public void handleMessage(Message message) {
            if (message.arg2 > 0) {
                Log.m29v(WaveFragment.TAG, "handleMessage - state : " + Engine.getInstance().getPlayerState());
                if (Engine.getInstance().getPlayerState() == 1) {
                    Message message2 = new Message();
                    message2.what = message.what;
                    message2.arg1 = message.arg1;
                    message2.arg2 = message.arg2 - 1;
                    sendMessageDelayed(message2, 100);
                } else {
                    Engine.getInstance().pausePlay();
                    Engine.getInstance().seekTo(message.arg1);
                }
                super.handleMessage(message);
            }
        }
    }

    /* access modifiers changed from: private */
    public void deleteBookmark(int i) {
        this.mDeletePosition = i;
        if (VoiceNoteFeature.FLAG_IS_SEM_AVAILABLE) {
            ArrayList arrayList = new ArrayList();
            arrayList.clear();
            arrayList.add(Integer.valueOf(i));
//            this.mAddDeleteListAnimator.setDelete(arrayList);
            showBookmarkEmptyView(this.mBookmarkListAdapter.getCount() == 0);
            return;
        }
        removeFromBookmarkListAdapter();
    }

    /* access modifiers changed from: private */
    public void addToBookmarkListAdapter() {
        Log.m19d(TAG, "addToBookmarkListAdapter mAddTime : " + this.mAddTime);
        this.mBookmarkListAdapter.addBookmark(this.mAddTime);
        this.mBookmarkListAdapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    public void removeFromBookmarkListAdapter() {
        Log.m19d(TAG, "removeFromBookmarkListAdapter mDeletePosition : " + this.mDeletePosition);
        this.mBookmarkListAdapter.deleteBookmark(this.mDeletePosition);
        this.mBookmarkListAdapter.notifyDataSetChanged();
        showBookmarkEmptyView(this.mBookmarkListAdapter.getCount() == 0);
    }

    private synchronized List<BookmarkListAdapter.BookmarkItem> loadItem() {
        ArrayList arrayList;
        Log.m26i(TAG, "loadItem");
        ArrayList<Bookmark> bookmarkList = MetadataRepository.getInstance().getBookmarkList();
        arrayList = new ArrayList();
        Iterator<Bookmark> it = bookmarkList.iterator();
        while (it.hasNext()) {
            Bookmark next = it.next();
            arrayList.add(new BookmarkListAdapter.BookmarkItem(next.getElapsed(), next.getTitle()));
        }
        Collections.sort(arrayList);
        return arrayList;
    }

    private void showBookmarkEmptyView(boolean z) {
        Log.m19d(TAG, "showBookmarkEmptyView show : " + z);
        View view = this.mBookmarkEmptyView;
        if (view != null) {
            if (z) {
                view.setVisibility(0);
                this.mBookmarkListTitle.setVisibility(8);
                return;
            }
            view.setVisibility(8);
            this.mBookmarkListTitle.setVisibility(0);
        }
    }

    /* access modifiers changed from: private */
    public void updateBookmarkListAdapter() {
        BookmarkListAdapter bookmarkListAdapter = this.mBookmarkListAdapter;
        if (bookmarkListAdapter != null) {
            bookmarkListAdapter.updateItemList(loadItem());
            showBookmarkEmptyView(this.mBookmarkListAdapter.getCount() == 0);
        }
    }

    private void updateSttTranslationLayout(boolean z) {
        ZoomView zoomView;
        if (this.mRecyclerView == null || (zoomView = this.mZoomView) == null) {
            Log.m22e(TAG, " updateSttTranslationLayout return");
            return;
        }
        zoomView.startZoom(true);
        this.mZoomView.setVisibility(0);
        this.mZoomView.setRecordMode(4);
        this.mZoomScrollbarView.setVisibility(0);
        this.mZoomScrollbarView.setAlpha(0.0f);
        FrameLayout frameLayout = this.mZoomScrollViewChildLayout;
        if (frameLayout != null) {
            frameLayout.removeAllViews();
            this.mZoomScrollViewChildLayout.addView(new View(getActivity()), this.mZoomView.getTotalWidth(), 1);
        }
        this.mRecyclerView.setVisibility(4);
        this.mCurrentTimeLayout.setVisibility(4);
        this.mEditCurrentTimeHandler.setVisibility(0);
        if (z) {
            initTranslationStartTime();
        }
        updateZoomViewScrollbarLayout();
        updateHandlerLayout();
        setWaveViewBackgroundColor();
    }

    private void updateZoomViewAfterStart() {
        this.mZoomView.changeLengthOfTime();
        this.mZoomView.invalidateZoomView(true);
    }

    private void updateZoomViewAfterDiscard() {
        this.mZoomView.changeLengthOfTime();
        this.mZoomView.invalidateZoomView(true);
    }

    private void initTranslationStartTime() {
        int currentTime = Engine.getInstance().getTranslationState() != 1 ? Engine.getInstance().getCurrentTime() : 0;
        Engine.getInstance().setCurrentTime(currentTime);
        this.mEditMode.updateCurrentTime(currentTime);
        this.mDuration = currentTime;
    }

    /* access modifiers changed from: private */
    public void updateTranslationStartTime() {
        int i = this.mDuration;
        if (i < 0) {
            i = 0;
        }
        this.mDuration = i;
        this.mDuration = this.mDuration > Engine.getInstance().getDuration() ? Engine.getInstance().getDuration() : this.mDuration;
    }

    private void updateTabletBookmarkListLayout() {
        if (VoiceNoteFeature.FLAG_IS_TABLET && !DisplayManager.isDeviceOnLandscape() && DisplayManager.getMultiwindowMode() != 1) {
            FrameLayout frameLayout = this.mBookmarkListAreaFrame;
            if (frameLayout != null) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
                layoutParams.setMarginStart(getResources().getDimensionPixelSize(C0690R.dimen.tablet_bookmark_list_margin_start));
                this.mBookmarkListAreaFrame.setLayoutParams(layoutParams);
            }
            TextView textView = this.mBookmarkListTitle;
            if (textView != null) {
                textView.setPadding(getResources().getDimensionPixelSize(C0690R.dimen.tablet_bookmark_list_margin_start), 0, 0, 0);
            }
        }
    }

    private void blockBackGestureOnWaveArea(boolean z) {
        View view = getView();
        if (view != null) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(new Rect());
            if (z) {
                view.getWindowVisibleDisplayFrame((Rect) arrayList.get(0));
            }
            DisplayManager.setSystemGestureExclusionRects(view, arrayList);
        }
    }
}
