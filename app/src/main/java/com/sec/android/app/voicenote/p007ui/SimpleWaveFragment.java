package com.sec.android.app.voicenote.p007ui;

import android.annotation.SuppressLint;
import android.content.res.Resources;
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
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.VelocityTrackerCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.p007ui.SimpleWaveFragment;
import com.sec.android.app.voicenote.p007ui.adapter.RecyclerAdapter;
import com.sec.android.app.voicenote.p007ui.view.CustomFastScroll;
import com.sec.android.app.voicenote.p007ui.view.HandlerView;
import com.sec.android.app.voicenote.p007ui.view.SimpleFloatingView;
import com.sec.android.app.voicenote.p007ui.view.WaveRecyclerView;
import com.sec.android.app.voicenote.p007ui.view.ZoomView;
import com.sec.android.app.voicenote.provider.AssistantProvider;
import com.sec.android.app.voicenote.provider.DBProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.HWKeyboardProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.MouseKeyboardProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.provider.WaveProvider;
import com.sec.android.app.voicenote.service.AudioFormat;
import com.sec.android.app.voicenote.service.ContentItem;
import com.sec.android.app.voicenote.service.SimpleEngine;
import com.sec.android.app.voicenote.service.SimpleMetadataRepository;
import com.sec.android.app.voicenote.service.helper.Bookmark;
import com.sec.android.app.voicenote.uicore.SimpleFragmentController;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/* renamed from: com.sec.android.app.voicenote.ui.SimpleWaveFragment */
public class SimpleWaveFragment extends AbsSimpleFragment implements SimpleEngine.OnSimpleEngineListener, CompoundButton.OnCheckedChangeListener, SimpleFragmentController.OnSceneChangeListener {
    private static final int LOAD_WAVE_CONTINUE = 2;
    private static final int LOAD_WAVE_END = 3;
    private static final int ONE_SEC = 1001;
    private static final String TAG = "SimpleWaveFragment";
    /* access modifiers changed from: private */
    public AsyncLoadWave mAsyncLoadWave = null;
    private HandlerView mCurrentLineView;
    /* access modifiers changed from: private */
    public int mCurrentMaxScrollSpeed;
    private FrameLayout mCurrentTimeLayout;
    /* access modifiers changed from: private */
    public String mCurrentWavePath = null;
    private int mDefaultMaxScrollSpeed;
    /* access modifiers changed from: private */
    public int mDuration = 0;
    private HandlerView mEditCurrentLineView;
    private SimpleFloatingView mEditCurrentTimeHandler;
    private Handler mEngineEventHandler = null;
    /* access modifiers changed from: private */
    public boolean mIsLoadCompleted = true;
    /* access modifiers changed from: private */
    public boolean mIsTouchingScrollBar = false;
    /* access modifiers changed from: private */
    public int mLastVelocity = -1;
    /* access modifiers changed from: private */
    public SimpleFloatingView mLeftRepeatHandler;
    private ImageView mLeftTrimHandlerImageView;
    private FrameLayout mLeftTrimHandlerLayout;
    private View mLeftTrimHandlerLineView;
    private TextView mLeftTrimHandlerTime;
    private FrameLayout mLeftTrimHandlerTouchLayout;
    /* access modifiers changed from: private */
    public Handler mLoadWaveHandler = new LoadWaveHandler(this);
    private int mOldMaxAmplitude = -1;
    private View.OnTouchListener mOnEditCurrentTouchListener = null;
    private View.OnTouchListener mOnLeftRepeatListener = null;
    private View.OnTouchListener mOnLeftTrimListener = null;
    private View.OnTouchListener mOnRightRepeatListener = null;
    private View.OnTouchListener mOnRightTrimListener = null;
    private int mOverwriteReady = 0;
    private boolean mPlayBarIsMoving = false;
    private boolean mRecordFromLeft;
    /* access modifiers changed from: private */
    public RecyclerAdapter mRecyclerAdapter = null;
    /* access modifiers changed from: private */
    public WaveRecyclerView mRecyclerView = null;
    /* access modifiers changed from: private */
    public int mRecyclerViewOffset = 0;
    /* access modifiers changed from: private */
    public SimpleFloatingView mRightRepeatHandler;
    private ImageView mRightTrimHandlerImageView;
    private FrameLayout mRightTrimHandlerLayout;
    private View mRightTrimHandlerLineView;
    private TextView mRightTrimHandlerTime;
    private FrameLayout mRightTrimHandlerTouchLayout;
    /* access modifiers changed from: private */
    public ScaleGestureDetector mScaleGestureDetector = null;
    private int mScene = 0;
    /* access modifiers changed from: private */
    public int mScrollPointerId = -1;
    /* access modifiers changed from: private */
    public boolean mScrollable = false;
    private ScrollerHandler mScrollerHandler = new ScrollerHandler(this);
    private boolean mSkipScrollByResizeWaveView = false;
    /* access modifiers changed from: private */
    public VelocityTracker mVelocityTracker = null;
    private FrameLayout mZoomScrollViewChildLayout = null;
    /* access modifiers changed from: private */
    public HorizontalScrollView mZoomScrollbarView = null;
    /* access modifiers changed from: private */
    public ZoomView mZoomView = null;

    static /* synthetic */ void lambda$ScaleEnd$12() {
    }

    public void onCreate(Bundle bundle) {
        Log.m26i(TAG, "onCreate - bundle : " + bundle);
        super.onCreate(bundle);
        this.mEngineEventHandler = new Handler(new Handler.Callback() {
            public final boolean handleMessage(Message message) {
                return SimpleWaveFragment.this.lambda$onCreate$3$SimpleWaveFragment(message);
            }
        });
        WaveProvider.getInstance().init();
        this.mRecyclerAdapter = new RecyclerAdapter(getActivity(), getRecordMode(), true);
        if (this.mAsyncLoadWave == null) {
            this.mAsyncLoadWave = new AsyncLoadWave();
        }
    }

    public /* synthetic */ boolean lambda$onCreate$3$SimpleWaveFragment(Message message) {
        if (getActivity() == null || !isAdded() || isRemoving()) {
            Log.m22e(TAG, "mEngineEventHandler RETURN by : " + getActivity() + ',' + isAdded() + ',' + isAdded());
            return false;
        }
        int i = message.what;
        boolean z = true;
        if (i == 1010) {
            Log.m19d(TAG, "onRecorderUpdate - INFO_RECORDER_STATE : " + message.arg1);
            int i2 = message.arg1;
            if (i2 == 1) {
                if (this.mScene != 3 && !this.mSimpleEngine.isSimpleRecorderMode()) {
                    initialize();
                    this.mSimpleEngine.setCurrentTime(0);
                }
                this.mOverwriteReady = 0;
                this.mOldMaxAmplitude = -1;
            } else if (i2 == 2) {
                setScrollEnable(false);
                this.mOverwriteReady++;
                this.mCurrentWavePath = this.mSimpleEngine.getRecentFilePath();
                this.mCurrentLineView.setVisibility(0);
            } else if (i2 == 3) {
                if (this.mDuration == 0) {
                    this.mDuration = this.mSimpleEngine.getDuration();
                }
                if (this.mRecordFromLeft && this.mDuration < WaveProvider.SIMPLE_START_RECORD_MARGIN) {
                    this.mCurrentTimeLayout.setX((float) (WaveProvider.SIMPLE_WAVE_AREA_WIDTH / 2));
                    this.mRecyclerView.smoothScrollByPosition((int) (((float) this.mDuration) / WaveProvider.MS_PER_PX));
                }
                this.mRecordFromLeft = false;
                this.mOverwriteReady = 0;
                this.mOldMaxAmplitude = -1;
                this.mRecyclerView.postDelayed(new Runnable() {
                    public final void run() {
                        SimpleWaveFragment.this.lambda$null$0$SimpleWaveFragment();
                    }
                }, 30);
            } else if (i2 == 4) {
                this.mOldMaxAmplitude = -1;
                if (this.mSimpleEngine.isSimpleRecorderMode()) {
                    this.mRecyclerView.postDelayed(new Runnable() {
                        public final void run() {
                            SimpleWaveFragment.this.lambda$null$1$SimpleWaveFragment();
                        }
                    }, 30);
                }
            }
        } else if (i != 1011) {
            switch (i) {
                case 2010:
                    Log.m19d(TAG, "onPlayerUpdate - INFO_PLAYER_STATE : " + message.arg1);
                    int i3 = message.arg1;
                    if (i3 != 1 && i3 != 2) {
                        if (i3 != 3) {
                            if (i3 == 4) {
                                setScrollEnable(true);
                                this.mSkipScrollByResizeWaveView = false;
                                this.mCurrentLineView.setVisibility(0);
                                break;
                            }
                        } else {
                            setScrollEnable(true);
                            this.mSkipScrollByResizeWaveView = true;
                            this.mCurrentLineView.setVisibility(0);
                            break;
                        }
                    } else {
                        setScrollEnable(true);
                        if (this.mSimpleEngine.getRecorderState() == 1 && this.mSimpleEngine.getPlayerState() != 3) {
                            this.mDuration = 0;
                            this.mSimpleEngine.setCurrentTime(this.mDuration);
                            this.mSimpleEngine.setCurrentTime(this.mDuration, true);
                        }
                        this.mCurrentLineView.setVisibility(0);
                        break;
                    }
                    break;
                case 2011:
                    setScrollEnable(false);
                    View view = getView();
                    if (view != null) {
                        view.postDelayed(new Runnable() {
                            public final void run() {
                                SimpleWaveFragment.this.lambda$null$2$SimpleWaveFragment();
                            }
                        }, 30);
                        break;
                    }
                    break;
                case 2012:
                    int playerState = this.mSimpleEngine.getPlayerState();
                    if ((playerState == 3 || playerState == 4 || playerState == 2) && !this.mPlayBarIsMoving) {
                        this.mDuration = message.arg1;
                        if (this.mSkipScrollByResizeWaveView) {
                            this.mSkipScrollByResizeWaveView = false;
                            Log.m32w(TAG, "SKIP scrollTo by mSkipScrollByResizeWaveView");
                        } else {
                            scrollTo((int) (((float) this.mDuration) / WaveProvider.MS_PER_PX));
                        }
                        this.mSimpleEngine.setCurrentTime(this.mDuration, true);
                        break;
                    }
                case 2013:
                    int repeatMode = this.mSimpleEngine.getRepeatMode();
                    int[] repeatPosition = this.mSimpleEngine.getRepeatPosition();
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
            float f = ((((float) this.mDuration) + WaveProvider.SIMPLE_DURATION_PER_HAFT_OF_WAVE_AREA) - ((float) WaveProvider.SIMPLE_START_RECORD_MARGIN)) / WaveProvider.MS_PER_PX;
            if (!this.mRecordFromLeft || f > ((float) (WaveProvider.SIMPLE_WAVE_AREA_WIDTH / 2))) {
                scrollTo((int) ((((float) this.mDuration) / WaveProvider.MS_PER_PX) + ((float) this.mCurrentTimeLayout.getWidth())));
            } else {
                scrollTo((int) (((float) WaveProvider.SIMPLE_START_RECORD_MARGIN) / WaveProvider.MS_PER_PX));
                this.mCurrentTimeLayout.setX(f);
            }
            if (this.mSimpleEngine.getRecorderState() != 1) {
                this.mSimpleEngine.setCurrentTime(this.mDuration, true);
            }
            int i4 = this.mDuration;
            int i5 = i4 <= 0 ? 1 : (i4 / WaveProvider.DURATION_PER_WAVEVIEW) + 1;
            int i6 = this.mDuration;
            int round = i6 <= 0 ? 0 : Math.round(((float) (i6 % WaveProvider.DURATION_PER_WAVEVIEW)) / 70.0f);
            if (this.mOverwriteReady >= 2) {
                this.mOverwriteReady = 0;
                this.mRecyclerAdapter.setIndex(i5, round);
            }
            if (this.mSimpleMetadata.removeRoughBookmark(this.mDuration)) {
                this.mRecyclerAdapter.removeBookmark(i5, round);
                postEvent(Event.REMOVE_BOOKMARK);
            }
            if (message.arg2 != -1) {
                while (this.mRecyclerAdapter.getItemCount() - 1 <= i5) {
                    this.mRecyclerAdapter.addItem(getActivity());
                }
                int i7 = this.mOldMaxAmplitude;
                if (i7 == -1) {
                    this.mOldMaxAmplitude = message.arg2;
                } else {
                    int i8 = message.arg2;
                    int i9 = ((((i7 >> 16) + (i8 >> 16)) / 2) << 16) + (((i7 & SupportMenu.USER_MASK) + (i8 & SupportMenu.USER_MASK)) / 2);
                    RecyclerAdapter recyclerAdapter = this.mRecyclerAdapter;
                    if (!isResumed() && this.mSimpleEngine.getScreenOff()) {
                        z = false;
                    }
                    recyclerAdapter.addAmplitude(i5, round, i9, z);
                    this.mOldMaxAmplitude = -1;
                }
            }
        }
        return false;
    }

    public /* synthetic */ void lambda$null$0$SimpleWaveFragment() {
        if (this.mSimpleEngine.getPlayerState() != 3) {
            setScrollEnable(true);
        }
    }

    public /* synthetic */ void lambda$null$1$SimpleWaveFragment() {
        if (this.mSimpleEngine.getPlayerState() != 3) {
            setScrollEnable(true);
            scrollTo(0);
            this.mCurrentTimeLayout.setX((float) (WaveProvider.SIMPLE_WAVE_AREA_WIDTH / 2));
            this.mRecordFromLeft = false;
            this.mSimpleEngine.setCurrentTime(0, true);
        }
    }

    public /* synthetic */ void lambda$null$2$SimpleWaveFragment() {
        setScrollEnable(true);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Log.m26i(TAG, "onCreateView - bundle : " + bundle);
        if (DisplayManager.isDeviceOnLandscape()) {
            WaveProvider.getInstance().setWaveAreaWidth(DisplayManager.getCurrentScreenWidth(getActivity()) / 2, true);
        } else {
            WaveProvider.getInstance().setWaveAreaWidth(DisplayManager.getCurrentScreenWidth(getActivity()), true);
        }
        View inflate = layoutInflater.inflate(C0690R.layout.simple_fragment_wave, viewGroup, false);
        this.mRecyclerView = (WaveRecyclerView) inflate.findViewById(C0690R.C0693id.recycler_view);
        this.mRecyclerView.setParent((FrameLayout) inflate.findViewById(C0690R.C0693id.wave_area));
        this.mRecyclerView.init(true);
        this.mZoomView = (ZoomView) inflate.findViewById(C0690R.C0693id.zoom_view);
        setProgressHoverWindow(this.mZoomView, true);
        this.mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            public void onRequestDisallowInterceptTouchEvent(boolean z) {
            }

            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            }

            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                Log.m26i(SimpleWaveFragment.TAG, "onInterceptTouchEvent  getAction() : " + motionEvent.getAction() + " getX() :" + motionEvent.getX());
                if (SimpleWaveFragment.this.mVelocityTracker == null) {
                    VelocityTracker unused = SimpleWaveFragment.this.mVelocityTracker = VelocityTracker.obtain();
                }
                SimpleWaveFragment.this.mVelocityTracker.addMovement(motionEvent);
                int action = motionEvent.getAction();
                if (action == 0) {
                    int unused2 = SimpleWaveFragment.this.mScrollPointerId = MotionEventCompat.getPointerId(motionEvent, 0);
                } else if (action == 1) {
                    SimpleWaveFragment.this.mVelocityTracker.computeCurrentVelocity(1000, 64000.0f);
                    SimpleWaveFragment simpleWaveFragment = SimpleWaveFragment.this;
                    int unused3 = simpleWaveFragment.mLastVelocity = (int) (-VelocityTrackerCompat.getXVelocity(simpleWaveFragment.mVelocityTracker, SimpleWaveFragment.this.mScrollPointerId));
                    Log.m26i(SimpleWaveFragment.TAG, "mLastVelocity : " + SimpleWaveFragment.this.mLastVelocity);
                    SimpleWaveFragment.this.mVelocityTracker.recycle();
                    VelocityTracker unused4 = SimpleWaveFragment.this.mVelocityTracker = null;
                }
                return false;
            }
        });
        this.mLeftTrimHandlerLayout = (FrameLayout) inflate.findViewById(C0690R.C0693id.wave_left_trim_handler_layout);
        this.mRightTrimHandlerLayout = (FrameLayout) inflate.findViewById(C0690R.C0693id.wave_right_trim_handler_layout);
        this.mLeftTrimHandlerTouchLayout = (FrameLayout) inflate.findViewById(C0690R.C0693id.wave_left_trim_handler_image_touch_layout);
        this.mRightTrimHandlerTouchLayout = (FrameLayout) inflate.findViewById(C0690R.C0693id.wave_right_trim_handler_image_touch_layout);
        this.mEditCurrentTimeHandler = (SimpleFloatingView) inflate.findViewById(C0690R.C0693id.wave_edit_current_line_layout);
        this.mCurrentTimeLayout = (FrameLayout) inflate.findViewById(C0690R.C0693id.wave_current_line_layout);
        this.mLeftTrimHandlerTime = (TextView) inflate.findViewById(C0690R.C0693id.wave_left_trim_handler_time);
        this.mRightTrimHandlerTime = (TextView) inflate.findViewById(C0690R.C0693id.wave_right_trim_handler_time);
        this.mLeftTrimHandlerImageView = (ImageView) inflate.findViewById(C0690R.C0693id.wave_left_trim_handler_image);
        this.mRightTrimHandlerImageView = (ImageView) inflate.findViewById(C0690R.C0693id.wave_right_trim_handler_image);
        this.mLeftRepeatHandler = (SimpleFloatingView) inflate.findViewById(C0690R.C0693id.wave_left_repeat_handler_layout);
        this.mRightRepeatHandler = (SimpleFloatingView) inflate.findViewById(C0690R.C0693id.wave_right_repeat_handler_layout);
        this.mLeftTrimHandlerLineView = inflate.findViewById(C0690R.C0693id.wave_left_trim_handler_line);
        this.mRightTrimHandlerLineView = inflate.findViewById(C0690R.C0693id.wave_right_trim_handler_line);
        this.mCurrentLineView = (HandlerView) inflate.findViewById(C0690R.C0693id.wave_current_line);
        this.mEditCurrentLineView = (HandlerView) inflate.findViewById(C0690R.C0693id.wave_edit_current_line);
        this.mZoomScrollbarView = (HorizontalScrollView) inflate.findViewById(C0690R.C0693id.zoom_scrollbar_view);
        this.mZoomScrollViewChildLayout = (FrameLayout) inflate.findViewById(C0690R.C0693id.zoom_scrollview_layout);
        setScrollEnable(false);
        this.mLeftRepeatHandler.setSession(this.mSession);
        this.mRightRepeatHandler.setSession(this.mSession);
        this.mCurrentLineView.setSession(this.mSession);
        this.mEditCurrentLineView.setSession(this.mSession);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(0);
        this.mRecyclerView.setAdapter(this.mRecyclerAdapter);
        this.mRecyclerView.setFocusable(false);
        this.mRecyclerView.setLayoutManager(linearLayoutManager);
        this.mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.mDefaultMaxScrollSpeed = getCurrentMaxScrollSpeed();
        this.mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return SimpleWaveFragment.this.lambda$onCreateView$4$SimpleWaveFragment(view, motionEvent);
            }
        });
        this.mZoomScrollbarView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            public final void onScrollChange(View view, int i, int i2, int i3, int i4) {
                SimpleWaveFragment.this.lambda$onCreateView$5$SimpleWaveFragment(view, i, i2, i3, i4);
            }
        });
        this.mZoomScrollbarView.setOnTouchListener(new View.OnTouchListener() {
            private float mOldX;

            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getPointerCount() == 2) {
                    return SimpleWaveFragment.this.mScaleGestureDetector.onTouchEvent(motionEvent);
                }
                int action = motionEvent.getAction();
                if (action == 0) {
                    this.mOldX = motionEvent.getX();
                } else if (action == 1) {
                    if (Math.abs(motionEvent.getX() - this.mOldX) < 10.0f) {
                        SimpleWaveFragment simpleWaveFragment = SimpleWaveFragment.this;
                        int unused = simpleWaveFragment.mDuration = (int) (simpleWaveFragment.mZoomView.getStartTime() + (motionEvent.getX() * SimpleWaveFragment.this.mZoomView.getMsPerPx()));
                        Log.m26i(SimpleWaveFragment.TAG, "ZoomScrollbarView JUMP to " + SimpleWaveFragment.this.mDuration);
                        SimpleWaveFragment simpleWaveFragment2 = SimpleWaveFragment.this;
                        simpleWaveFragment2.mSimpleEngine.setCurrentTime(simpleWaveFragment2.mDuration, true);
                        if (SimpleWaveFragment.this.mSimpleEngine.getPlayerState() != 1) {
                            SimpleWaveFragment simpleWaveFragment3 = SimpleWaveFragment.this;
                            simpleWaveFragment3.mSimpleEngine.seekTo(simpleWaveFragment3.mDuration);
                        }
                    }
                    SimpleWaveFragment.this.enableAutoScroll(true, 2000);
                } else if (action == 2) {
                    SimpleWaveFragment.this.mZoomScrollbarView.setAlpha(1.0f);
                    SimpleWaveFragment.this.enableAutoScroll(false, 0);
                }
                return SimpleWaveFragment.this.mZoomScrollbarView.onTouchEvent(motionEvent);
            }
        });
        this.mRecyclerView.setFastScrollEnable(true);
        this.mRecyclerView.setScrollBarStateChangeListener(new CustomFastScroll.onScrollBarStateChangeListener() {
            public final void onStateChange(int i, int i2, boolean z) {
                SimpleWaveFragment.this.lambda$onCreateView$6$SimpleWaveFragment(i, i2, z);
            }
        });
        this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int mDx;
            private int mMultiple = 1;
            private int mScrollState = 0;

            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                Log.m26i(SimpleWaveFragment.TAG, "onScrollStateChanged - newState : " + i + " has event : " + SimpleWaveFragment.this.hasPostEvent(1003));
                super.onScrollStateChanged(recyclerView, i);
                SimpleWaveFragment simpleWaveFragment = SimpleWaveFragment.this;
                int unused = simpleWaveFragment.mRecyclerViewOffset = simpleWaveFragment.mRecyclerView.getHorizontalScrollOffset();
                if (SimpleWaveFragment.this.mScrollable && !SimpleWaveFragment.this.hasPostEvent(1003)) {
                    if (i == 0) {
                        this.mMultiple = 1;
                        SimpleWaveFragment.this.handleScrollStateIdle();
                        this.mScrollState = i;
                    } else if (i == 1) {
                        SimpleWaveFragment.this.handleScrollStateDragging();
                    } else if (i == 2) {
                        if (this.mScrollState != 0) {
                            int i2 = this.mMultiple;
                            if (i2 <= 8) {
                                this.mMultiple = i2 * 2;
                            }
                            Log.m26i(SimpleWaveFragment.TAG, "onScrollStateChanged pre : " + this.mScrollState + " new : " + i + " mLastVelocity : " + SimpleWaveFragment.this.mLastVelocity + " mMultiple : " + this.mMultiple);
                            if (SimpleWaveFragment.this.mLastVelocity != -1) {
                                if (SimpleWaveFragment.this.mCurrentMaxScrollSpeed < Math.abs(SimpleWaveFragment.this.mLastVelocity * this.mMultiple)) {
                                    SimpleWaveFragment simpleWaveFragment2 = SimpleWaveFragment.this;
                                    simpleWaveFragment2.changeMaxScrollSpeed(Math.abs(simpleWaveFragment2.mLastVelocity * this.mMultiple));
                                }
                                SimpleWaveFragment.this.mRecyclerView.fling(SimpleWaveFragment.this.mLastVelocity * this.mMultiple, 0);
                                int unused2 = SimpleWaveFragment.this.mLastVelocity = -1;
                            }
                        }
                        this.mScrollState = i;
                    }
                }
                if (i == 0) {
                    SimpleWaveFragment.this.postEvent(Event.UNBLOCK_CONTROL_BUTTONS);
                } else if (i != 2) {
                    SimpleWaveFragment.this.postEvent(Event.BLOCK_CONTROL_BUTTONS);
                } else {
                    SimpleWaveFragment.this.postEvent(Event.BLOCK_CONTROL_BUTTONS);
                }
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                int i3 = i * this.mMultiple;
                super.onScrolled(recyclerView, i3, i2);
                this.mDx = i3;
                int horizontalScrollOffset = SimpleWaveFragment.this.mRecyclerView.getHorizontalScrollOffset();
                boolean z = false;
                if (horizontalScrollOffset == 0) {
                    SimpleWaveFragment.this.scrollTo(0);
                }
                if (SimpleWaveFragment.this.mScrollable) {
                    int maxScrollRange = SimpleWaveFragment.this.mRecyclerView.getMaxScrollRange();
                    if (horizontalScrollOffset > maxScrollRange) {
                        SimpleWaveFragment.this.scrollTo(maxScrollRange);
                    }
                    if (horizontalScrollOffset > 0 && horizontalScrollOffset < maxScrollRange) {
                        SimpleWaveFragment simpleWaveFragment = SimpleWaveFragment.this;
                        simpleWaveFragment.moveRepeatHandler(simpleWaveFragment.mLeftRepeatHandler, (((float) this.mDx) * 1.0f) / ((float) this.mMultiple), horizontalScrollOffset);
                        SimpleWaveFragment simpleWaveFragment2 = SimpleWaveFragment.this;
                        simpleWaveFragment2.moveRepeatHandler(simpleWaveFragment2.mRightRepeatHandler, (((float) this.mDx) * 1.0f) / ((float) this.mMultiple), horizontalScrollOffset);
                    }
                }
                if (recyclerView.getScrollState() != 0 || SimpleWaveFragment.this.mIsTouchingScrollBar) {
                    int duration = SimpleWaveFragment.this.mSimpleEngine.getDuration();
                    int unused = SimpleWaveFragment.this.mDuration = (int) (((float) horizontalScrollOffset) * WaveProvider.MS_PER_PX);
                    if (SimpleWaveFragment.this.mDuration > duration) {
                        int unused2 = SimpleWaveFragment.this.mDuration = duration;
                        if (recyclerView.getScrollState() != 2) {
                            SimpleWaveFragment simpleWaveFragment3 = SimpleWaveFragment.this;
                            simpleWaveFragment3.scrollTo((int) (((float) simpleWaveFragment3.mDuration) / WaveProvider.MS_PER_PX));
                        }
                        z = true;
                    }
                    SimpleWaveFragment simpleWaveFragment4 = SimpleWaveFragment.this;
                    simpleWaveFragment4.mSimpleEngine.setCurrentTime(simpleWaveFragment4.mDuration, true);
                    if (z && SimpleWaveFragment.this.mSimpleEngine.getPlayerState() == 3) {
                        SimpleWaveFragment.this.mSimpleEngine.pausePlay();
                    }
                }
            }
        });
        this.mOnEditCurrentTouchListener = new View.OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return SimpleWaveFragment.this.lambda$onCreateView$7$SimpleWaveFragment(view, motionEvent);
            }
        };
        this.mOnLeftTrimListener = new View.OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return SimpleWaveFragment.this.lambda$onCreateView$8$SimpleWaveFragment(view, motionEvent);
            }
        };
        this.mOnRightTrimListener = new View.OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return SimpleWaveFragment.this.lambda$onCreateView$9$SimpleWaveFragment(view, motionEvent);
            }
        };
        this.mOnLeftRepeatListener = new View.OnTouchListener() {
            private float mStartPoint;
            final int[] positions = SimpleWaveFragment.this.mSimpleEngine.getRepeatPosition();

            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    this.mStartPoint = motionEvent.getX();
                } else if (action == 1) {
                    SimpleWaveFragment simpleWaveFragment = SimpleWaveFragment.this;
                    simpleWaveFragment.mSimpleEngine.setRepeatTime(simpleWaveFragment.mLeftRepeatHandler.getTime(SimpleWaveFragment.this.mRecyclerView.getHorizontalScrollOffset()), -1);
                    view.performClick();
                    SimpleWaveFragment.this.postEvent(Event.UNBLOCK_CONTROL_BUTTONS);
                } else if (action == 2) {
                    int horizontalScrollOffset = SimpleWaveFragment.this.mRecyclerView.getHorizontalScrollOffset();
                    if (SimpleWaveFragment.this.mScrollable) {
                        SimpleWaveFragment simpleWaveFragment2 = SimpleWaveFragment.this;
                        simpleWaveFragment2.moveRepeatHandler(simpleWaveFragment2.mLeftRepeatHandler, SimpleWaveFragment.this.getDistance(this.mStartPoint, motionEvent), horizontalScrollOffset);
                    }
                    if (SimpleWaveFragment.this.mLeftRepeatHandler.getVisibility() == 0) {
                        int time = SimpleWaveFragment.this.mLeftRepeatHandler.getTime(horizontalScrollOffset);
                        if (SimpleWaveFragment.this.mRightRepeatHandler.getVisibility() == 0) {
                            int[] iArr = this.positions;
                            if (iArr[0] <= iArr[1]) {
                                if (time > iArr[1] - 1001) {
                                    time = iArr[1] - 1001;
                                    SimpleWaveFragment.this.mLeftRepeatHandler.setTime(time, (float) horizontalScrollOffset);
                                }
                            } else if (time < iArr[1] + 1001) {
                                time = iArr[1] + 1001;
                                SimpleWaveFragment.this.mLeftRepeatHandler.setTime(time, (float) horizontalScrollOffset);
                            }
                        }
                        SimpleWaveFragment.this.mRecyclerAdapter.setRepeatStartTime(time);
                    }
                }
                return true;
            }
        };
        this.mOnRightRepeatListener = new View.OnTouchListener() {
            private float mStartPoint;
            final int[] positions = SimpleWaveFragment.this.mSimpleEngine.getRepeatPosition();

            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    this.mStartPoint = motionEvent.getX();
                } else if (action == 1) {
                    SimpleWaveFragment simpleWaveFragment = SimpleWaveFragment.this;
                    simpleWaveFragment.mSimpleEngine.setRepeatTime(-1, simpleWaveFragment.mRightRepeatHandler.getTime(SimpleWaveFragment.this.mRecyclerView.getHorizontalScrollOffset()));
                    view.performClick();
                    SimpleWaveFragment.this.postEvent(Event.UNBLOCK_CONTROL_BUTTONS);
                } else if (action == 2) {
                    int horizontalScrollOffset = SimpleWaveFragment.this.mRecyclerView.getHorizontalScrollOffset();
                    if (SimpleWaveFragment.this.mScrollable) {
                        SimpleWaveFragment simpleWaveFragment2 = SimpleWaveFragment.this;
                        simpleWaveFragment2.moveRepeatHandler(simpleWaveFragment2.mRightRepeatHandler, SimpleWaveFragment.this.getDistance(this.mStartPoint, motionEvent), horizontalScrollOffset);
                    }
                    if (SimpleWaveFragment.this.mRightRepeatHandler.getVisibility() == 0) {
                        int time = SimpleWaveFragment.this.mRightRepeatHandler.getTime(horizontalScrollOffset);
                        int[] iArr = this.positions;
                        if (iArr[0] < iArr[1]) {
                            if (time < iArr[0] + 1001) {
                                time = iArr[0] + 1001;
                                SimpleWaveFragment.this.mRightRepeatHandler.setTime(time, (float) horizontalScrollOffset);
                            }
                        } else if (time > iArr[0] - 1001) {
                            time = iArr[0] - 1001;
                            SimpleWaveFragment.this.mRightRepeatHandler.setTime(time, (float) horizontalScrollOffset);
                        }
                        SimpleWaveFragment.this.mRecyclerAdapter.setRepeatEndTime(time);
                    }
                }
                return true;
            }
        };
        inflate.setOnGenericMotionListener($$Lambda$SimpleWaveFragment$dxY2W9L0arsdSq1hl6aiK_j0cEM.INSTANCE);
        this.mEditCurrentTimeHandler.setOnTouchListener(this.mOnEditCurrentTouchListener);
        this.mLeftTrimHandlerTouchLayout.setOnTouchListener(this.mOnLeftTrimListener);
        this.mRightTrimHandlerTouchLayout.setOnTouchListener(this.mOnRightTrimListener);
        this.mLeftRepeatHandler.setOnTouchListener(this.mOnLeftRepeatListener);
        this.mRightRepeatHandler.setOnTouchListener(this.mOnRightRepeatListener);
        this.mScaleGestureDetector = new ScaleGestureDetector(getActivity(), new ScaleListener());
        onUpdate(Integer.valueOf(this.mStartingEvent));
        return inflate;
    }

    public /* synthetic */ boolean lambda$onCreateView$4$SimpleWaveFragment(View view, MotionEvent motionEvent) {
        if (!this.mScrollable || motionEvent.getPointerCount() != 2) {
            return !this.mScrollable;
        }
        this.mScaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    public /* synthetic */ void lambda$onCreateView$5$SimpleWaveFragment(View view, int i, int i2, int i3, int i4) {
        Log.m19d(TAG, "onScrollChange - scrollX:" + i + " oldScrollX:" + i3 + " dX:" + (i3 - i));
        if (i < 0) {
            i = 0;
        } else if (i > this.mZoomScrollViewChildLayout.getWidth() - WaveProvider.SIMPLE_WAVE_AREA_WIDTH) {
            i = this.mZoomScrollViewChildLayout.getWidth() - WaveProvider.SIMPLE_WAVE_AREA_WIDTH;
        }
        this.mZoomView.scrollTo(i, false);
    }

    public /* synthetic */ void lambda$onCreateView$6$SimpleWaveFragment(int i, int i2, boolean z) {
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

    public /* synthetic */ boolean lambda$onCreateView$7$SimpleWaveFragment(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            this.mPlayBarIsMoving = true;
        } else if (action == 1) {
            view.performClick();
            this.mPlayBarIsMoving = false;
            if (this.mSimpleEngine.getPlayerState() != 1) {
                this.mSimpleEngine.seekTo(this.mDuration);
            } else if (this.mSimpleEngine.getRecorderState() != 1) {
                this.mSimpleEngine.setCurrentTime(this.mDuration);
            }
        } else if (action == 2) {
            if (this.mDuration > this.mSimpleEngine.getDuration()) {
                this.mDuration = this.mSimpleEngine.getDuration();
            }
            this.mSimpleEngine.setCurrentTime(this.mDuration, true);
            scrollTo((int) (((float) this.mDuration) / WaveProvider.MS_PER_PX));
        } else if (action == 3) {
            this.mPlayBarIsMoving = false;
        }
        return true;
    }

    public /* synthetic */ boolean lambda$onCreateView$8$SimpleWaveFragment(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            this.mLeftTrimHandlerImageView.setColorFilter(getResources().getColor(C0690R.C0691color.wave_trim_handler_time_select_color, (Resources.Theme) null));
            this.mLeftTrimHandlerLineView.setBackgroundColor(getResources().getColor(C0690R.C0691color.wave_trim_handler_time_select_color, (Resources.Theme) null));
            this.mLeftTrimHandlerTime.setTextColor(getResources().getColor(C0690R.C0691color.wave_trim_handler_time_select_color, (Resources.Theme) null));
            this.mRightTrimHandlerTime.setTextColor(getResources().getColor(C0690R.C0691color.wave_trim_handler_time_dim_color, (Resources.Theme) null));
        } else if (action == 1 || action == 3) {
            this.mLeftTrimHandlerImageView.setColorFilter(getResources().getColor(C0690R.C0691color.wave_trim_handler_time_color, (Resources.Theme) null));
            this.mLeftTrimHandlerTime.setTextColor(getResources().getColor(C0690R.C0691color.wave_trim_handler_time_color, (Resources.Theme) null));
            this.mLeftTrimHandlerLineView.setBackgroundColor(getResources().getColor(C0690R.C0691color.wave_trim_handler_time_color, (Resources.Theme) null));
            if (this.mSimpleEngine.getRecorderState() != 1) {
                this.mSimpleEngine.setCurrentTime(this.mDuration);
            }
            view.performClick();
            postEvent(Event.UNBLOCK_CONTROL_BUTTONS);
        }
        return true;
    }

    public /* synthetic */ boolean lambda$onCreateView$9$SimpleWaveFragment(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            this.mRightTrimHandlerImageView.setColorFilter(getResources().getColor(C0690R.C0691color.wave_trim_handler_time_select_color, (Resources.Theme) null));
            this.mRightTrimHandlerLineView.setBackgroundColor(getResources().getColor(C0690R.C0691color.wave_trim_handler_time_select_color, (Resources.Theme) null));
            this.mRightTrimHandlerTime.setTextColor(getResources().getColor(C0690R.C0691color.wave_trim_handler_time_select_color, (Resources.Theme) null));
            this.mLeftTrimHandlerTime.setTextColor(getResources().getColor(C0690R.C0691color.wave_trim_handler_time_dim_color, (Resources.Theme) null));
        } else if (action == 1 || action == 3) {
            this.mRightTrimHandlerImageView.setColorFilter(getResources().getColor(C0690R.C0691color.wave_trim_handler_time_color, (Resources.Theme) null));
            this.mRightTrimHandlerTime.setTextColor(getResources().getColor(C0690R.C0691color.wave_trim_handler_time_color, (Resources.Theme) null));
            this.mRightTrimHandlerLineView.setBackgroundColor(getResources().getColor(C0690R.C0691color.wave_trim_handler_time_color, (Resources.Theme) null));
            if (this.mSimpleEngine.getRecorderState() != 1) {
                this.mSimpleEngine.setCurrentTime(this.mDuration);
            }
            view.performClick();
            postEvent(Event.UNBLOCK_CONTROL_BUTTONS);
        }
        return true;
    }

    static /* synthetic */ boolean lambda$onCreateView$10(View view, MotionEvent motionEvent) {
        if (!MouseKeyboardProvider.getInstance().isCtrlPressed() || motionEvent.getAction() != 8) {
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void handleScrollStateIdle() {
        initMaxScrollSpeed();
        if (this.mSimpleEngine.getPlayerState() != 1) {
            int duration = this.mSimpleEngine.getDuration();
            Log.m26i(TAG, "onScrollStateChanged Play - mRecyclerViewOffset : " + this.mRecyclerViewOffset + " mDuration : " + this.mDuration + " actualDuration : " + duration);
            if (this.mDuration > duration) {
                this.mDuration = duration;
            }
            this.mEngineEventHandler.removeMessages(2012);
            this.mSimpleEngine.seekTo(this.mDuration);
            SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_SEEK, -1);
            this.mPlayBarIsMoving = false;
        } else if (this.mSimpleEngine.getRecorderState() != 1) {
            int duration2 = this.mSimpleEngine.getDuration();
            Log.m26i(TAG, "onScrollStateChanged Record - mRecyclerViewOffset : " + this.mRecyclerViewOffset + " mDuration : " + this.mDuration + " actualDuration : " + duration2);
            if (this.mDuration > duration2) {
                this.mDuration = duration2;
            }
            this.mSimpleEngine.setCurrentTime(this.mDuration);
        } else {
            Log.m22e(TAG, "what ??");
        }
        this.mSimpleMetadata.resetLastAddedBookmarkTime();
    }

    /* access modifiers changed from: private */
    public void handleScrollStateDragging() {
        if (this.mSimpleEngine.getPlayerState() == 3) {
            Log.m32w(TAG, "onScrollStateChanged SCROLL_STATE_DRAGGING && PlayerState.PLAYING");
            this.mPlayBarIsMoving = true;
        }
    }

    public void onViewCreated(View view, Bundle bundle) {
        Log.m26i(TAG, "onViewCreated - bundle : " + bundle);
        super.onViewCreated(view, bundle);
        Handler handler = this.mEngineEventHandler;
        handler.sendMessage(handler.obtainMessage(2013, 0, 0));
        this.mSimpleEngine.registerListener(this);
        HorizontalScrollView horizontalScrollView = this.mZoomScrollbarView;
        if (horizontalScrollView != null) {
            horizontalScrollView.setImportantForAccessibility(2);
        }
        if (this.mSimpleEngine.isSimpleRecorderMode()) {
            this.mRecyclerView.post(new Runnable() {
                public final void run() {
                    SimpleWaveFragment.this.lambda$onViewCreated$11$SimpleWaveFragment();
                }
            });
        }
    }

    public /* synthetic */ void lambda$onViewCreated$11$SimpleWaveFragment() {
        if (this.mSimpleEngine.getRecorderState() == 1 && this.mSimpleEngine.getPlayerState() == 1) {
            scrollTo((int) (((float) WaveProvider.SIMPLE_START_RECORD_MARGIN) / WaveProvider.MS_PER_PX));
            this.mCurrentTimeLayout.setX(((((float) this.mDuration) + WaveProvider.SIMPLE_DURATION_PER_HAFT_OF_WAVE_AREA) - ((float) WaveProvider.SIMPLE_START_RECORD_MARGIN)) / WaveProvider.MS_PER_PX);
            this.mRecordFromLeft = true;
        }
    }

    public void onStart() {
        Log.m26i(TAG, "onStart - mScene:" + this.mScene + " PlayerState:" + this.mSimpleEngine.getPlayerState() + " RecorderState:" + this.mSimpleEngine.getRecorderState());
        super.onStart();
        setWaveViewBackgroundColor();
        updateMainWaveLayout();
        updateInterviewLayout(getView());
        updateHandlerView();
        updateHandlerLayout();
        if (this.mSimpleEngine.getPlayerState() != 1) {
            this.mAsyncLoadWave.start(this.mSimpleEngine.getPath());
        } else if (this.mSimpleEngine.getRecorderState() != 1) {
            this.mAsyncLoadWave.start(this.mSimpleEngine.getRecentFilePath());
        }
    }

    public void onResume() {
        Log.m26i(TAG, "onResume");
        super.onResume();
    }

    public void onPause() {
        Log.m26i(TAG, "onPause");
        super.onPause();
    }

    public void onUpdate(Object obj) {
        Log.m26i(TAG, "onUpdate : " + obj);
        Integer num = (Integer) obj;
        int intValue = num.intValue();
        if (intValue == 11 || intValue == 12) {
            RecyclerAdapter recyclerAdapter = this.mRecyclerAdapter;
            if (recyclerAdapter != null) {
                recyclerAdapter.notifyDataSetChanged();
            }
            setWaveViewBackgroundColor();
        } else if (intValue != 975) {
            int i = 1;
            if (intValue == 978) {
                int lastRemovedBookmarkTime = this.mSimpleMetadata.getLastRemovedBookmarkTime();
                Log.m29v(TAG, "onUpdate - REMOVE_BOOKMARK time: " + lastRemovedBookmarkTime);
                if (lastRemovedBookmarkTime != 0) {
                    i = 1 + (lastRemovedBookmarkTime / WaveProvider.DURATION_PER_WAVEVIEW);
                }
                this.mRecyclerAdapter.removeBookmark(i, (lastRemovedBookmarkTime % WaveProvider.DURATION_PER_WAVEVIEW) / 70);
            } else if (intValue == 996) {
                int lastAddedBookmarkTime = this.mSimpleMetadata.getLastAddedBookmarkTime();
                Log.m29v(TAG, "onUpdate - ADD_BOOKMARK time: " + lastAddedBookmarkTime);
                if (lastAddedBookmarkTime != 0) {
                    i = 1 + (lastAddedBookmarkTime / WaveProvider.DURATION_PER_WAVEVIEW);
                }
                this.mRecyclerAdapter.addBookmark(i, (lastAddedBookmarkTime % WaveProvider.DURATION_PER_WAVEVIEW) / 70);
            } else if (intValue == 1993) {
                initialize();
            } else if (intValue == 1998) {
                initialize();
                this.mSimpleEngine.setCurrentTime(0);
            } else if (intValue != 2001) {
                switch (intValue) {
                    case 1001:
                        this.mRecordFromLeft = true;
                        this.mRecyclerAdapter.setRecordMode(getRecordMode());
                        setScrollEnable(false);
                        updateInterviewLayout(getView());
                        break;
                    case 1002:
                        break;
                    case 1003:
                        this.mOverwriteReady++;
                        break;
                    default:
                        switch (intValue) {
                            case 1006:
                                initialize();
                                this.mSimpleEngine.setCurrentTime(0);
                                break;
                            case 1007:
                            case 1008:
                                break;
                            default:
                                switch (intValue) {
                                    case Event.PLAY_STOP:
                                        this.mAsyncLoadWave.stop();
                                        initialize();
                                        this.mSimpleEngine.setCurrentTime(0);
                                        break;
                                    case Event.PLAY_NEXT:
                                        this.mAsyncLoadWave.start(this.mSimpleEngine.getPath());
                                        setWaveViewBackgroundColor();
                                        updateMainWaveLayout();
                                        updateInterviewLayout(getView());
                                        updateHandlerView();
                                        updateHandlerLayout();
                                        break;
                                    case Event.PLAY_PREV:
                                        this.mAsyncLoadWave.start(this.mSimpleEngine.getPath());
                                        setWaveViewBackgroundColor();
                                        updateMainWaveLayout();
                                        updateInterviewLayout(getView());
                                        updateHandlerView();
                                        updateHandlerLayout();
                                        break;
                                }
                        }
                }
            } else {
                this.mAsyncLoadWave.start(this.mSimpleEngine.getPath());
            }
        } else {
            this.mAsyncLoadWave.start(this.mSimpleEngine.getPath());
            updateInterviewLayout(getView());
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
    public void moveRepeatHandler(SimpleFloatingView simpleFloatingView, float f, int i) {
        simpleFloatingView.setX(simpleFloatingView.getX() - f, (float) i);
    }

    private void updateRepeatHandler(SimpleFloatingView simpleFloatingView, int i) {
        simpleFloatingView.update((float) i);
    }

    private void setRepeatHandler(SimpleFloatingView simpleFloatingView, int i) {
        simpleFloatingView.setTime(i, (float) this.mRecyclerView.getHorizontalScrollOffset());
    }

    /* access modifiers changed from: private */
    public void scrollTo(int i) {
        if (isResumed() || !this.mSimpleEngine.getScreenOff()) {
            this.mRecyclerView.scrollByPosition(i);
            int horizontalScrollOffset = this.mRecyclerView.getHorizontalScrollOffset();
            updateRepeatHandler(this.mLeftRepeatHandler, horizontalScrollOffset);
            updateRepeatHandler(this.mRightRepeatHandler, horizontalScrollOffset);
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
        layoutParams.height = WaveProvider.SIMPLE_WAVE_HEIGHT;
        this.mRecyclerView.setLayoutParams(layoutParams);
    }

    private void updateInterviewLayout(View view) {
        StringBuilder sb;
        String str;
        String str2;
        if (getActivity() != null && view != null) {
            int recordMode = getRecordMode();
            Log.m26i(TAG, "updateInterviewLayout - mode:" + recordMode + " scene:" + this.mScene);
            if (recordMode != 2 || this.mScene == 1) {
                setInterViewLayoutVisibility(8, false, false);
                view.findViewById(C0690R.C0693id.wave_interview_top_overlay_view).setVisibility(8);
                view.findViewById(C0690R.C0693id.wave_interview_bottom_overlay_view).setVisibility(8);
                return;
            }
            boolean isEnabledPerson = this.mSimpleMetadata.isEnabledPerson(180.0f);
            boolean isEnabledPerson2 = this.mSimpleMetadata.isEnabledPerson(0.0f);
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
            boolean isExistedPerson = this.mSimpleMetadata.isExistedPerson(180.0f);
            boolean isExistedPerson2 = this.mSimpleMetadata.isExistedPerson(0.0f);
            view.findViewById(C0690R.C0693id.wave_interview_person_layout).setVisibility(0);
            CheckBox checkBox = (CheckBox) view.findViewById(C0690R.C0693id.wave_interview_top_checkbox);
//            checkBox.semSetHoverPopupType(1);
            checkBox.setChecked(isEnabledPerson2);
            checkBox.setOnCheckedChangeListener(this);
            enableCheckBox(checkBox);
            CheckBox checkBox2 = (CheckBox) view.findViewById(C0690R.C0693id.wave_interview_bottom_checkbox);
//            checkBox2.semSetHoverPopupType(1);
            checkBox2.setChecked(isEnabledPerson);
            checkBox2.setOnCheckedChangeListener(this);
            enableCheckBox(checkBox2);
            view.findViewById(C0690R.C0693id.wave_interview_top_checkbox_layout).setContentDescription(sb2);
            view.findViewById(C0690R.C0693id.wave_interview_bottom_checkbox_layout).setContentDescription(str2);
            setInterViewLayoutVisibility(0, isExistedPerson2, isExistedPerson);
            if (!isExistedPerson2) {
                view.findViewById(C0690R.C0693id.wave_interview_top_overlay_view).setVisibility(0);
            } else if (isEnabledPerson2) {
                view.findViewById(C0690R.C0693id.wave_interview_top_overlay_view).setVisibility(8);
                this.mSimpleEngine.setMute(false, false);
            } else {
                view.findViewById(C0690R.C0693id.wave_interview_top_overlay_view).setVisibility(0);
                this.mSimpleEngine.setMute(true, false);
            }
            if (!isExistedPerson) {
                view.findViewById(C0690R.C0693id.wave_interview_bottom_overlay_view).setVisibility(0);
            } else if (isEnabledPerson) {
                view.findViewById(C0690R.C0693id.wave_interview_bottom_overlay_view).setVisibility(8);
                if (!isExistedPerson2 || isEnabledPerson2) {
                    this.mSimpleEngine.setMute(false, false);
                } else {
                    this.mSimpleEngine.setMute(true, false);
                }
            } else {
                view.findViewById(C0690R.C0693id.wave_interview_bottom_overlay_view).setVisibility(0);
                this.mSimpleEngine.setMute(false, true);
            }
        }
    }

    private void enableCheckBox(View view) {
        if (this.mSimpleEngine.getSkipSilenceMode() == 3) {
            view.setEnabled(false);
            view.setFocusable(false);
            return;
        }
        view.setEnabled(true);
        view.setFocusable(true);
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
            i5 = WaveProvider.SIMPLE_WAVE_VIEW_HEIGHT;
            i4 = getResources().getDimensionPixelSize(C0690R.dimen.wave_bookmark_top_margin);
            i3 = WaveProvider.SIMPLE_WAVE_HEIGHT;
            i2 = WaveProvider.SIMPLE_WAVE_VIEW_HEIGHT;
            i = WaveProvider.SIMPLE_WAVE_HEIGHT - getResources().getDimensionPixelSize(C0690R.dimen.wave_bookmark_top_margin);
            i7 = getResources().getDimensionPixelSize(C0690R.dimen.wave_current_bar_margin_top);
            i6 = 0;
        } else {
            i5 = WaveProvider.SIMPLE_WAVE_VIEW_HEIGHT + getResources().getDimensionPixelSize(C0690R.dimen.wave_time_text_height);
            i3 = WaveProvider.SIMPLE_WAVE_HEIGHT;
            i2 = WaveProvider.SIMPLE_WAVE_VIEW_HEIGHT + getResources().getDimensionPixelSize(C0690R.dimen.wave_time_text_height);
            int i9 = WaveProvider.SIMPLE_WAVE_VIEW_HEIGHT;
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
                int dimensionPixelSize = getResources().getDimensionPixelSize(C0690R.dimen.wave_time_text_height) + (WaveProvider.SIMPLE_WAVE_VIEW_HEIGHT / 2) + i7;
                changeMarginTopView(findViewById4, dimensionPixelSize);
                changeMarginTopView(findViewById5, i7 + getResources().getDimensionPixelSize(C0690R.dimen.wave_interview_checkbox_layout_margin_top));
                changeMarginTopView(findViewById6, dimensionPixelSize + getResources().getDimensionPixelSize(C0690R.dimen.wave_interview_checkbox_layout_margin_top));
            }
        }
        int i10 = this.mScene;
        if (i10 == 3) {
            this.mCurrentLineView.setVisibility(0);
        } else if (i10 != 1) {
        } else {
            if (this.mSimpleEngine.getRecorderState() == 1) {
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

    private void setTrimVisibility(int i) {
        Log.m26i(TAG, "setTrimVisibility : " + i);
        this.mLeftTrimHandlerLayout.setVisibility(i);
        this.mRightTrimHandlerLayout.setVisibility(i);
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
        this.mAsyncLoadWave.stop();
        this.mSimpleEngine.unregisterListener(this);
        initialize();
        this.mRecyclerAdapter.cleanUp();
        setProgressHoverWindow(this.mZoomView, false);
        if (getView() != null) {
            ((CheckBox) getView().findViewById(C0690R.C0693id.wave_interview_top_checkbox)).setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
            ((CheckBox) getView().findViewById(C0690R.C0693id.wave_interview_bottom_checkbox)).setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
        }
        this.mOnLeftTrimListener = null;
        this.mOnRightTrimListener = null;
        this.mOnLeftRepeatListener = null;
        this.mOnRightRepeatListener = null;
        super.onDestroyView();
    }

    public void onDestroy() {
        Log.m29v(TAG, "onDestroy");
        this.mEngineEventHandler = null;
        super.onDestroy();
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        Log.m26i(TAG, "onCheckedChanged - isChecked : " + z);
        if (getView() != null && compoundButton != null && isResumed()) {
            if (this.mSimpleEngine.getPlayerState() == 1) {
                compoundButton.setChecked(!compoundButton.isChecked());
                Log.m32w(TAG, "onCheckedChanged - play state is idle");
                return;
            }
            SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_SELECT_TRACK, -1);
            boolean isExistedPerson = this.mSimpleMetadata.isExistedPerson(180.0f);
            boolean isExistedPerson2 = this.mSimpleMetadata.isExistedPerson(0.0f);
            boolean isEnabledPerson = this.mSimpleMetadata.isEnabledPerson(180.0f);
            boolean isEnabledPerson2 = this.mSimpleMetadata.isEnabledPerson(0.0f);
            int id = compoundButton.getId();
            if (id != C0690R.C0693id.wave_interview_bottom_checkbox) {
                if (id == C0690R.C0693id.wave_interview_top_checkbox && isExistedPerson2) {
                    if (compoundButton.isChecked()) {
                        getView().findViewById(C0690R.C0693id.wave_interview_top_overlay_view).setVisibility(8);
                        this.mSimpleMetadata.enablePersonal(0.0f, true);
                        this.mSimpleEngine.setMute(false, false);
                    } else if (isEnabledPerson) {
                        getView().findViewById(C0690R.C0693id.wave_interview_top_overlay_view).setVisibility(0);
                        this.mSimpleMetadata.enablePersonal(0.0f, false);
                        this.mSimpleEngine.setMute(true, false);
                    } else {
                        compoundButton.setChecked(true);
                        Toast.makeText(getActivity(), C0690R.string.track_list_warning, 0).show();
                    }
                }
            } else if (isExistedPerson) {
                if (compoundButton.isChecked()) {
                    getView().findViewById(C0690R.C0693id.wave_interview_bottom_overlay_view).setVisibility(8);
                    this.mSimpleMetadata.enablePersonal(180.0f, true);
                    this.mSimpleEngine.setMute(false, false);
                } else if (isEnabledPerson2) {
                    getView().findViewById(C0690R.C0693id.wave_interview_bottom_overlay_view).setVisibility(0);
                    this.mSimpleMetadata.enablePersonal(180.0f, false);
                    this.mSimpleEngine.setMute(false, true);
                } else {
                    compoundButton.setChecked(true);
                    Toast.makeText(getActivity(), C0690R.string.track_list_warning, 0).show();
                }
            }
            updateInterviewLayout(getView());
        }
    }

    private void initialize() {
        Log.m29v(TAG, "initialize");
        scrollTo((int) (((float) this.mSimpleEngine.getCurrentTime()) / WaveProvider.MS_PER_PX));
        setScrollEnable(false);
        this.mDuration = 0;
        updateMainWaveLayout();
        updateInterviewLayout(getView());
        updateHandlerLayout();
        setTrimVisibility(8);
        this.mCurrentWavePath = null;
        this.mPlayBarIsMoving = false;
        this.mRecordFromLeft = false;
    }

    private void setScrollEnable(boolean z) {
        if (!z || this.mSimpleEngine.getRecorderState() != 2) {
            Log.m29v(TAG, "setScrollEnable : " + z);
            this.mScrollable = z;
            this.mRecyclerView.setHorizontalScrollBarEnabled(z);
            if (this.mScrollable) {
                this.mEditCurrentTimeHandler.setOnTouchListener(this.mOnEditCurrentTouchListener);
                this.mLeftTrimHandlerTouchLayout.setOnTouchListener(this.mOnLeftTrimListener);
                this.mRightTrimHandlerTouchLayout.setOnTouchListener(this.mOnRightTrimListener);
                return;
            }
            this.mEditCurrentTimeHandler.setOnTouchListener((View.OnTouchListener) null);
            this.mLeftTrimHandlerTouchLayout.setOnTouchListener((View.OnTouchListener) null);
            this.mRightTrimHandlerTouchLayout.setOnTouchListener((View.OnTouchListener) null);
            return;
        }
        Log.m32w(TAG, "setScrollEnable SKIP while RECORDING");
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
        ArrayList<Bookmark> bookmarkList = this.mSimpleMetadata.getBookmarkList();
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

    public void onSceneChange(int i) {
        Log.m26i(TAG, "onSceneChange scene : " + i + " mScene : " + this.mScene);
        if (isAdded() && !isRemoving() && this.mScene != i) {
            if (i == 3) {
                this.mScene = i;
                loadBookmarkData();
            } else {
                this.mScene = i;
            }
            this.mSimpleEngine.setScene(this.mScene);
            int i2 = this.mScene;
            if (i2 != 1 && i2 == 3) {
                this.mRecyclerAdapter.setRepeatTime(-1, -1);
                updateInterviewLayout(getView());
            }
        }
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
            i = WaveProvider.SIMPLE_WAVE_VIEW_HEIGHT / 2;
        }
        layoutParams.height = (WaveProvider.SIMPLE_WAVE_HEIGHT - getResources().getDimensionPixelSize(C0690R.dimen.wave_repeat_margin_bottom)) - getResources().getDimensionPixelSize(C0690R.dimen.wave_repeat_icon_size);
        layoutParams2.topMargin = i;
        frameLayout.setLayoutParams(layoutParams);
        frameLayout2.setLayoutParams(layoutParams2);
    }

    /* renamed from: com.sec.android.app.voicenote.ui.SimpleWaveFragment$ScaleListener */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            return false;
        }

        private ScaleListener() {
        }

        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            super.onScaleEnd(scaleGestureDetector);
            SimpleWaveFragment.this.ScaleEnd();
        }
    }

    /* access modifiers changed from: private */
    public void ScaleEnd() {
        Log.m26i(TAG, "onScaleEnd scale : ");
        this.mZoomScrollbarView.scrollTo((int) this.mZoomView.setZoomEnd(), 0);
        this.mZoomScrollbarView.postDelayed($$Lambda$SimpleWaveFragment$VwErYOkyajtJpx3fjpZcej63RQ.INSTANCE, 500);
        enableAutoScroll(true, 2000);
    }

    /* renamed from: com.sec.android.app.voicenote.ui.SimpleWaveFragment$LoadWaveHandler */
    private static class LoadWaveHandler extends Handler {
        WeakReference<SimpleWaveFragment> mWeakRef;

        LoadWaveHandler(SimpleWaveFragment simpleWaveFragment) {
            this.mWeakRef = new WeakReference<>(simpleWaveFragment);
        }

        public void handleMessage(Message message) {
            SimpleWaveFragment simpleWaveFragment = (SimpleWaveFragment) this.mWeakRef.get();
            if (simpleWaveFragment != null) {
                int i = message.what;
                if (i == 2) {
                    simpleWaveFragment.mAsyncLoadWave.onLoadWaveContinue();
                } else if (i == 3) {
                    simpleWaveFragment.mAsyncLoadWave.onLoadWaveEnd();
                }
                super.handleMessage(message);
            }
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.SimpleWaveFragment$AsyncLoadWave */
    private class AsyncLoadWave {
        private int[] amplitudeData;
        private int amplitudePos;
        private int[] buf;
        private int bufSize;
        private boolean isDone;
        private float mDuration;
        private int mWaveViewSize;
        private int[] newWave;
        private int size;
        private int viewIndex;

        private AsyncLoadWave() {
            this.amplitudePos = 0;
            this.viewIndex = 1;
            this.buf = null;
            this.isDone = true;
            this.mWaveViewSize = 0;
            this.mDuration = 0.0f;
        }

        public void start(String str) {
            int i;
            Log.m19d(SimpleWaveFragment.TAG, "AsyncLoadWave.start - newPath : " + str);
            Log.m19d(SimpleWaveFragment.TAG, "                    - mCurrentWavePath : " + SimpleWaveFragment.this.mCurrentWavePath);
            if (!str.equals(SimpleWaveFragment.this.mCurrentWavePath)) {
                this.mDuration = (float) SimpleWaveFragment.this.mSimpleMetadata.getDuration();
                if ((str.endsWith(AudioFormat.ExtType.EXT_M4A) || str.endsWith(AudioFormat.ExtType.EXT_AMR) || str.endsWith(AudioFormat.ExtType.EXT_3GA)) && SimpleWaveFragment.this.mSimpleMetadata.isWaveMakerWorking()) {
                    SimpleWaveFragment.this.mSimpleMetadata.registerListener(new SimpleMetadataRepository.OnVoiceMetadataListener() {
                        public final void onWaveMakerFinished(int i, int i2) {
                            SimpleWaveFragment.AsyncLoadWave.this.lambda$start$1$SimpleWaveFragment$AsyncLoadWave(i, i2);
                        }
                    });
                    Log.m32w(SimpleWaveFragment.TAG, "AsyncLoadWave.start - WAIT for waveMaker");
                    return;
                }
                if (SimpleWaveFragment.this.mSimpleEngine.getContentItemCount() > 1) {
                    ContentItem peekContentItem = SimpleWaveFragment.this.mSimpleEngine.peekContentItem();
                    if (peekContentItem != null) {
                        this.amplitudeData = SimpleWaveFragment.this.mSimpleMetadata.getOverWriteWaveData(peekContentItem.getStartTime(), SimpleWaveFragment.this.mSimpleEngine.getCurrentTime());
                        int[] iArr = this.amplitudeData;
                        this.size = iArr == null ? 0 : iArr.length;
                        this.mDuration = (float) SimpleWaveFragment.this.mSimpleEngine.getDuration();
                        RecyclerAdapter access$2100 = SimpleWaveFragment.this.mRecyclerAdapter;
                        int i2 = this.size;
                        int i3 = WaveProvider.NUM_OF_AMPLITUDE;
                        access$2100.setIndex((i2 / i3) + 1, i2 % i3);
                        Log.m32w(SimpleWaveFragment.TAG, "AsyncLoadWave.start - getOverWriteWaveData : " + this.size);
                    }
                } else {
                    this.amplitudeData = SimpleWaveFragment.this.mSimpleMetadata.getWaveData();
                    this.size = SimpleWaveFragment.this.mSimpleMetadata.getWaveDataSize();
                    if (this.size < SimpleWaveFragment.this.mSimpleMetadata.getAmplitudeCollectorSize()) {
                        this.amplitudeData = SimpleWaveFragment.this.mSimpleMetadata.getAmplitudeCollector();
                        this.size = SimpleWaveFragment.this.mSimpleMetadata.getAmplitudeCollectorSize();
                        Log.m32w(SimpleWaveFragment.TAG, "AsyncLoadWave.start - getAmplitudeCollectorSize : " + this.size);
                    } else {
                        Log.m32w(SimpleWaveFragment.TAG, "AsyncLoadWave.start - getWaveDataSize : " + this.size);
                    }
                }
                if (this.size <= 0) {
                    Log.m32w(SimpleWaveFragment.TAG, "AsyncLoadWave.start - wave size is under 0 : " + this.size);
                    return;
                }
                if (this.mDuration == 0.0f) {
                    this.mDuration = (float) SimpleWaveFragment.this.mSimpleEngine.getDuration();
                }
                Log.m26i(SimpleWaveFragment.TAG, "AsyncLoadWave.start - version : " + this.amplitudeData[0]);
                Log.m26i(SimpleWaveFragment.TAG, "AsyncLoadWave.start - duration : " + this.mDuration);
                this.size = (int) Math.ceil((double) (this.mDuration / 35.0f));
                Log.m26i(SimpleWaveFragment.TAG, "AsyncLoadWave.start - convert size : " + this.size);
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
                Log.m26i(SimpleWaveFragment.TAG, "AsyncLoadWave.start - newWave size : " + this.size);
                this.isDone = false;
                String unused = SimpleWaveFragment.this.mCurrentWavePath = str;
                this.mWaveViewSize = (int) Math.ceil((double) (((((float) SimpleWaveFragment.this.mSimpleEngine.getDuration()) * 1.0f) / 70.0f) / ((float) WaveProvider.NUM_OF_AMPLITUDE)));
                if (SimpleWaveFragment.this.mCurrentWavePath.isEmpty() || DBProvider.getInstance().getIdByPath(SimpleWaveFragment.this.mCurrentWavePath) == -1) {
                    i = SimpleWaveFragment.this.getRecordMode();
                } else {
                    i = DBProvider.getInstance().getRecordModeByPath(SimpleWaveFragment.this.mCurrentWavePath);
                }
                if (i != SimpleWaveFragment.this.mRecyclerAdapter.getRecordMode()) {
                    SimpleWaveFragment.this.mRecyclerAdapter.setRecordMode(i);
                }
                SimpleWaveFragment.this.mRecyclerAdapter.initialize(SimpleWaveFragment.this.getActivity(), SimpleWaveFragment.this.getRecordMode());
                SimpleWaveFragment.this.mLoadWaveHandler.removeMessages(2);
                SimpleWaveFragment.this.mLoadWaveHandler.removeMessages(3);
                boolean unused2 = SimpleWaveFragment.this.mIsLoadCompleted = false;
                SimpleWaveFragment.this.mLoadWaveHandler.sendEmptyMessage(2);
            } else if (SimpleWaveFragment.this.mSimpleEngine.getPlayerState() == 4 || SimpleWaveFragment.this.mSimpleEngine.getRecorderState() == 3) {
                SimpleWaveFragment.this.mRecyclerView.postDelayed(new Runnable() {
                    public final void run() {
                        SimpleWaveFragment.AsyncLoadWave.this.lambda$start$0$SimpleWaveFragment$AsyncLoadWave();
                    }
                }, 30);
            }
        }

        public /* synthetic */ void lambda$start$0$SimpleWaveFragment$AsyncLoadWave() {
            SimpleWaveFragment simpleWaveFragment = SimpleWaveFragment.this;
            simpleWaveFragment.scrollTo((int) (((float) simpleWaveFragment.mSimpleEngine.getCurrentTime()) / WaveProvider.MS_PER_PX));
        }

        public /* synthetic */ void lambda$start$1$SimpleWaveFragment$AsyncLoadWave(int i, int i2) {
            Log.m32w(SimpleWaveFragment.TAG, "AsyncLoadWave.start - onWaveMakerFinished");
            SimpleWaveFragment.this.mAsyncLoadWave.start(SimpleWaveFragment.this.mSimpleEngine.getPath());
        }

        public void stop() {
            Log.m26i(SimpleWaveFragment.TAG, "AsyncLoadWave.stop");
            this.isDone = true;
            this.amplitudePos = 0;
            this.viewIndex = 1;
            SimpleWaveFragment.this.mLoadWaveHandler.removeMessages(2);
            SimpleWaveFragment.this.mLoadWaveHandler.removeMessages(3);
        }

        public boolean isDone() {
            return this.isDone;
        }

        private void drawOneWaveView(int i) {
            for (int i2 = 0; i2 < i && this.amplitudePos < this.size; i2++) {
                int[] iArr = this.buf;
                if (iArr != null) {
                    Arrays.fill(iArr, -1);
                }
                try {
                    if (this.size - this.amplitudePos < WaveProvider.NUM_OF_AMPLITUDE) {
                        this.buf = Arrays.copyOfRange(this.newWave, this.amplitudePos, this.size);
                        this.amplitudePos += this.size - this.amplitudePos;
                        this.bufSize = this.amplitudePos % WaveProvider.NUM_OF_AMPLITUDE;
                    } else {
                        this.buf = Arrays.copyOfRange(this.newWave, this.amplitudePos, this.amplitudePos + WaveProvider.NUM_OF_AMPLITUDE);
                        this.amplitudePos += WaveProvider.NUM_OF_AMPLITUDE;
                        this.bufSize = WaveProvider.NUM_OF_AMPLITUDE;
                    }
                    Log.m29v(SimpleWaveFragment.TAG, "AsyncLoadWave - update item index : " + this.viewIndex);
                    if (this.mWaveViewSize == this.viewIndex && SimpleWaveFragment.this.mSimpleEngine.getRecorderState() != 2) {
                        SimpleWaveFragment.this.mRecyclerAdapter.clearView(this.viewIndex);
                    }
                    if (this.buf != null) {
                        SimpleWaveFragment.this.mRecyclerAdapter.updateDataArray(SimpleWaveFragment.this.getContext(), this.viewIndex, this.buf, this.bufSize);
                        addBookmarkForWaveView(this.viewIndex);
                    }
                    SimpleWaveFragment.this.mRecyclerAdapter.setRepeatTimeSimple(SimpleWaveFragment.this.mSimpleEngine, this.viewIndex);
                    this.viewIndex++;
                } catch (ArrayIndexOutOfBoundsException unused) {
                    Log.m22e(SimpleWaveFragment.TAG, "amplitudePos < 0 or amplitudePos > amplitudeData.length");
                } catch (IllegalArgumentException unused2) {
                    Log.m22e(SimpleWaveFragment.TAG, "amplitudePos > amplitudePos + WaveProvider.NUM_OF_AMPLITUDE");
                } catch (NullPointerException unused3) {
                    Log.m22e(SimpleWaveFragment.TAG, "amplitudeData = null");
                }
            }
        }

        private void addBookmarkForWaveView(int i) {
            ArrayList<Bookmark> bookmarkList = SimpleWaveFragment.this.mSimpleMetadata.getBookmarkList();
            if (bookmarkList != null && !bookmarkList.isEmpty()) {
                int size2 = bookmarkList.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    int elapsed = bookmarkList.get(i2).getElapsed();
                    int i3 = 1;
                    if (elapsed != 0) {
                        i3 = 1 + (elapsed / WaveProvider.DURATION_PER_WAVEVIEW);
                    }
                    if (i3 == i) {
                        Log.m26i(SimpleWaveFragment.TAG, "addBookmarkForWaveView viewIndex = " + i);
                        SimpleWaveFragment.this.mRecyclerAdapter.addBookmark(i3, (elapsed % WaveProvider.DURATION_PER_WAVEVIEW) / 70);
                    } else if (i3 > i) {
                        return;
                    }
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void onLoadWaveContinue() {
            if (this.amplitudePos < this.size) {
                drawOneWaveView(1);
                SimpleWaveFragment.this.mLoadWaveHandler.sendEmptyMessageDelayed(2, 50);
                return;
            }
            SimpleWaveFragment.this.mLoadWaveHandler.sendEmptyMessage(3);
        }

        /* access modifiers changed from: package-private */
        public void onLoadWaveEnd() {
            if (this.viewIndex == SimpleWaveFragment.this.mRecyclerAdapter.getItemCount()) {
                SimpleWaveFragment.this.mRecyclerAdapter.addItem(SimpleWaveFragment.this.getContext());
            }
            if (SimpleWaveFragment.this.mSimpleEngine.getRecorderState() != 2) {
                SimpleWaveFragment.this.mRecyclerAdapter.removeLastItem(SimpleWaveFragment.this.getActivity(), SimpleWaveFragment.this.mRecyclerAdapter.getItemCount() - this.viewIndex);
            }
            Log.m19d(SimpleWaveFragment.TAG, "AsyncLoadWave - end size : " + SimpleWaveFragment.this.mRecyclerAdapter.getItemCount());
            SimpleWaveFragment.this.loadBookmarkData();
            int[] repeatPosition = SimpleWaveFragment.this.mSimpleEngine.getRepeatPosition();
            SimpleWaveFragment.this.mRecyclerAdapter.setRepeatTime(repeatPosition[0], repeatPosition[1]);
            this.isDone = true;
            this.amplitudePos = 0;
            this.viewIndex = 1;
            SimpleWaveFragment.this.mRecyclerView.postDelayed(new Runnable() {
                public final void run() {
                    SimpleWaveFragment.AsyncLoadWave.this.lambda$onLoadWaveEnd$2$SimpleWaveFragment$AsyncLoadWave();
                }
            }, 30);
            boolean unused = SimpleWaveFragment.this.mIsLoadCompleted = true;
        }

        public /* synthetic */ void lambda$onLoadWaveEnd$2$SimpleWaveFragment$AsyncLoadWave() {
            Log.m29v(SimpleWaveFragment.TAG, "do updateScrollViewWidth");
            SimpleWaveFragment simpleWaveFragment = SimpleWaveFragment.this;
            simpleWaveFragment.scrollTo((int) (((float) simpleWaveFragment.mSimpleEngine.getCurrentTime()) / WaveProvider.MS_PER_PX));
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
        int recordMode = this.mSimpleMetadata.getRecordMode();
        Log.m26i(TAG, "getRecordMode() : mScene = " + this.mScene + ", mSimpleMetadata recordMode = " + recordMode);
        if (this.mSimpleEngine.isSimpleRecorderMode()) {
            recordMode = Settings.getIntSettings(Settings.KEY_SIMPLE_RECORD_MODE, 1);
        } else if (recordMode == 0) {
            if (this.mScene == 1 || this.mSimpleEngine.isSimpleRecorderMode()) {
                recordMode = Settings.getIntSettings(Settings.KEY_SIMPLE_RECORD_MODE, 1);
            } else {
                recordMode = Settings.getIntSettings(Settings.KEY_SIMPLE_PLAY_MODE, 1);
            }
        }
        Log.m26i(TAG, "getRecordMode() : mScene = " + this.mScene + ", final recordMode = " + recordMode);
        return recordMode;
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

    private void setProgressHoverWindow(View view, boolean z) {
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
                    int mode = SimpleWaveFragment.this.getRecordMode();
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
                        Resources resources = SimpleWaveFragment.this.getResources();
                        if (resources == null) {
                            return 0;
                        }
                        return resources.getDimensionPixelSize(i);
                    }

                    @SuppressLint({"InflateParams"})
                    private void createHoverPopupWindow() {
                        this.mTime = (TextView) LayoutInflater.from(SimpleWaveFragment.this.getActivity()).inflate(C0690R.layout.hover_window_layout, (ViewGroup) null);
//                        SemHoverPopupWindow semHoverPopupWindow = semGetHoverPopup;
//                        if (semHoverPopupWindow != null && this.mTime != null) {
//                            semHoverPopupWindow.setGravity(3);
//                        }
                    }

                    private boolean checkBoundary(float f, float f2) {
                        int i = WaveProvider.SIMPLE_WAVE_VIEW_HEIGHT;
                        int pixel = getPixel(C0690R.dimen.wave_time_text_height) + getPixel(C0690R.dimen.wave_bookmark_top_margin);
                        return f2 >= ((float) pixel) && f2 <= ((float) ((i + pixel) + getPixel(C0690R.dimen.wave_time_text_height)));
                    }

                    private String getFixedTimeFormat(int i) {
                        int i2 = i / 1000;
                        return String.format(Locale.US, "%02d:%02d:%02d", new Object[]{Integer.valueOf(i2 / 3600), Integer.valueOf((i2 / 60) % 60), Integer.valueOf(i2 % 60)});
                    }

                    private void updateProgressbarPreviewView(float f, float f2) {
                        int i;
//                        if (semGetHoverPopup != null && this.mTime != null) {
//                            if (!checkBoundary(f, f2)) {
//                                semGetHoverPopup.dismiss();
//                            } else if (!semGetHoverPopup.isShowing()) {
//                                semGetHoverPopup.show();
//                            }
//                            this.hoverPositionX = ((int) f) - (this.mTime.getWidth() / 2);
//                            if (!HWKeyboardProvider.isHWKeyboard(SimpleWaveFragment.this.getContext())) {
//                                i = getPixel(C0690R.dimen.player_amplitude_time_hover_y);
//                            } else {
//                                i = getPixel(C0690R.dimen.hw_keyboard_player_amplitude_time_hover_y);
//                            }
//                            this.hoverPositionY = i;
//                            this.mTime.setText(getFixedTimeFormat(this.hoverTime));
//                            semGetHoverPopup.setOffset(this.hoverPositionX, this.hoverPositionY);
//                            semGetHoverPopup.setContent(this.mTime);
//                            semGetHoverPopup.update();
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

    /* renamed from: com.sec.android.app.voicenote.ui.SimpleWaveFragment$ScrollerHandler */
    private class ScrollerHandler extends Handler {
        WeakReference<SimpleWaveFragment> mWeakRef;

        ScrollerHandler(SimpleWaveFragment simpleWaveFragment) {
            this.mWeakRef = new WeakReference<>(simpleWaveFragment);
        }

        public void handleMessage(Message message) {
            int i;
            SimpleWaveFragment simpleWaveFragment = (SimpleWaveFragment) this.mWeakRef.get();
            if (simpleWaveFragment == null || (i = message.what) == 0 || i != 1 || SimpleWaveFragment.this.mSimpleEngine.getPlayerState() != 3) {
                return;
            }
            if (((float) simpleWaveFragment.mDuration) < simpleWaveFragment.mZoomView.getStartTime() || ((float) simpleWaveFragment.mDuration) > simpleWaveFragment.mZoomView.getEndTime()) {
                int access$600 = (int) (((float) simpleWaveFragment.mDuration) * simpleWaveFragment.mZoomView.getPxPerMs());
                Log.m19d(SimpleWaveFragment.TAG, "ZoomView SCROLL to : " + access$600 + " duration : " + simpleWaveFragment.mDuration);
                simpleWaveFragment.mZoomScrollbarView.smoothScrollTo(access$600, 0);
            }
        }
    }
}
