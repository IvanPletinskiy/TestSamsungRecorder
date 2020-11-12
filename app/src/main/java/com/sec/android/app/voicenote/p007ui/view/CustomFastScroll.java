package com.sec.android.app.voicenote.p007ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.RecyclerView;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.p007ui.adapter.RecyclerAdapter;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.SimpleEngine;
import com.sec.android.app.voicenote.service.SimpleEngineManager;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@VisibleForTesting
/* renamed from: com.sec.android.app.voicenote.ui.view.CustomFastScroll */
public class CustomFastScroll extends FrameLayout implements RecyclerView.OnItemTouchListener {
    private static final int ANIMATION_STATE_FADING_IN = 1;
    private static final int ANIMATION_STATE_FADING_OUT = 3;
    private static final int ANIMATION_STATE_IN = 2;
    private static final int ANIMATION_STATE_OUT = 0;
    private static final int DRAG_NONE = 0;
    private static final int DRAG_X = 1;
    private static final int HIDE_DELAY_AFTER_DRAGGING_MS = 1200;
    private static final int HIDE_DELAY_AFTER_VISIBLE_MS = 1500;
    private static final int HIDE_DURATION_MS = 500;
    private static final int SHOW_DURATION_MS = 500;
    public static final int STATE_DRAGGING = 2;
    public static final int STATE_HIDDEN = 0;
    public static final int STATE_VISIBLE = 1;
    private boolean enable = false;
    private boolean isTouching = false;
    private onScrollBarStateChangeListener listener;
    /* access modifiers changed from: private */
    public int mAnimationState = 0;
    private int mDragState = 0;
    private final Runnable mHideRunnable = new Runnable() {
        public final void run() {
            CustomFastScroll.this.lambda$new$0$CustomFastScroll();
        }
    };
    @VisibleForTesting
    float mHorizontalDragX;
    private final int[] mHorizontalRange = new int[2];
    @VisibleForTesting
    double mHorizontalThumbCenterX;
    private ImageView mHorizontalThumbDrawable;
    private int mHorizontalThumbHeight;
    @VisibleForTesting
    int mHorizontalThumbWidth;
    private int mMargin;
    private final RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            CustomFastScroll customFastScroll = CustomFastScroll.this;
            customFastScroll.updateScrollPosition(customFastScroll.mRecyclerView.getHorizontalScrollOffset());
        }
    };
    /* access modifiers changed from: private */
    public WaveRecyclerView mRecyclerView;
    private int mRecyclerViewHeight = 0;
    private int mRecyclerViewWidth = 0;
    /* access modifiers changed from: private */
    public final ValueAnimator mShowHideAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
    private int mState = 0;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: com.sec.android.app.voicenote.ui.view.CustomFastScroll$AnimationState */
    private @interface AnimationState {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: com.sec.android.app.voicenote.ui.view.CustomFastScroll$DragState */
    private @interface DragState {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: com.sec.android.app.voicenote.ui.view.CustomFastScroll$State */
    private @interface State {
    }

    /* renamed from: com.sec.android.app.voicenote.ui.view.CustomFastScroll$onScrollBarStateChangeListener */
    public interface onScrollBarStateChangeListener {
        void onStateChange(int i, int i2, boolean z);
    }

    public void onRequestDisallowInterceptTouchEvent(boolean z) {
    }

    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
    }

    public /* synthetic */ void lambda$new$0$CustomFastScroll() {
        hide(500);
    }

    public CustomFastScroll(Context context) {
        super(context);
        initView();
    }

    public CustomFastScroll(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustomFastScroll(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    private void initView() {
        Resources resources = getContext().getResources();
        this.mMargin = 0;
        this.mHorizontalThumbHeight = (int) resources.getDimension(C0690R.dimen.recycleview_scroll_bar_height);
        this.mHorizontalThumbWidth = (int) resources.getDimension(C0690R.dimen.recycleview_scroll_bar_width);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, this.mHorizontalThumbHeight);
        layoutParams.gravity = 48;
        layoutParams.topMargin = 0;
        setLayoutParams(layoutParams);
        this.mHorizontalThumbDrawable = new ImageView(getContext());
        this.mHorizontalThumbDrawable.setLayoutParams(new FrameLayout.LayoutParams((int) resources.getDimension(C0690R.dimen.recycleview_scroll_bar_width), this.mHorizontalThumbHeight));
        Drawable drawable = getContext().getDrawable(C0690R.C0692drawable.voice_recorder_fastscroll_thumb_mtrl_alpha);
        drawable.setColorFilter(resources.getColor(C0690R.C0691color.primary_color, (Resources.Theme) null), PorterDuff.Mode.SRC_IN);
        this.mHorizontalThumbDrawable.setBackground(drawable);
        addView(this.mHorizontalThumbDrawable);
        this.mShowHideAnimator.addListener(new AnimatorListener());
        this.mShowHideAnimator.addUpdateListener(new AnimatorUpdater());
    }

    public void recyclerViewSizeChange(int i, int i2) {
        if (this.mRecyclerViewWidth != i || this.mRecyclerViewHeight != i2) {
            this.mRecyclerViewWidth = i;
            ValueAnimator valueAnimator = this.mShowHideAnimator;
            if (valueAnimator != null && valueAnimator.isRunning()) {
                this.mShowHideAnimator.cancel();
            }
            this.mAnimationState = 0;
            setState(0);
            if (this.mRecyclerViewHeight != i2) {
                this.mRecyclerViewHeight = i2;
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
                layoutParams.topMargin = i2 - this.mHorizontalThumbHeight;
                setLayoutParams(layoutParams);
                invalidate();
            }
        }
    }

    public void setScrollBarStateChangeListener(onScrollBarStateChangeListener onscrollbarstatechangelistener) {
        this.listener = onscrollbarstatechangelistener;
    }

    public void setScrollEnable(boolean z) {
        this.enable = z;
    }

    public void attachToRecyclerView(@Nullable WaveRecyclerView waveRecyclerView) {
        WaveRecyclerView waveRecyclerView2 = this.mRecyclerView;
        if (waveRecyclerView2 != waveRecyclerView) {
            if (waveRecyclerView2 != null) {
                destroyCallbacks();
            }
            this.mRecyclerView = waveRecyclerView;
            if (this.mRecyclerView != null) {
                setupCallbacks();
            }
        }
    }

    private void setupCallbacks() {
        this.mRecyclerView.addOnItemTouchListener(this);
        this.mRecyclerView.addOnScrollListener(this.mOnScrollListener);
    }

    private void destroyCallbacks() {
        this.mRecyclerView.removeOnItemTouchListener(this);
        this.mRecyclerView.removeOnScrollListener(this.mOnScrollListener);
        cancelHide();
    }

    /* access modifiers changed from: private */
    public void requestRedraw(float f) {
        this.mHorizontalThumbDrawable.setImageAlpha((int) (255.0f * f));
        if (f == 0.0f) {
            if (this.mHorizontalThumbDrawable.getVisibility() != 4) {
                this.mHorizontalThumbDrawable.setVisibility(4);
            }
        } else if (this.mHorizontalThumbDrawable.getVisibility() != 0) {
            this.mHorizontalThumbDrawable.setVisibility(0);
        }
    }

    /* access modifiers changed from: private */
    public void setState(int i) {
        if (i == 2 && this.mState != 2) {
            cancelHide();
        }
        if (i == 0) {
            requestRedraw(0.0f);
        } else {
            show();
        }
        if (this.mState == 2 && i != 2) {
            resetHideDelay(HIDE_DELAY_AFTER_DRAGGING_MS);
        } else if (i == 1) {
            resetHideDelay(HIDE_DELAY_AFTER_VISIBLE_MS);
        }
        onScrollBarStateChangeListener onscrollbarstatechangelistener = this.listener;
        if (onscrollbarstatechangelistener != null) {
            onscrollbarstatechangelistener.onStateChange(i, this.mState, this.isTouching);
        }
        this.mState = i;
    }

    public boolean isDragging() {
        return this.mState == 2;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isVisible() {
        return this.mState == 1;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isHidden() {
        return this.mState == 0;
    }

    public void show() {
        int i = this.mAnimationState;
        if (i == 0) {
            setAnimationStateOut();
        } else if (i == 3) {
            this.mShowHideAnimator.cancel();
            setAnimationStateOut();
        }
    }

    public void hide() {
        hide(0);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void hide(int i) {
        int i2 = this.mAnimationState;
        if (i2 == 1) {
            this.mShowHideAnimator.cancel();
            setAnimationStateIn(i);
        } else if (i2 == 2) {
            setAnimationStateIn(i);
        }
    }

    private void cancelHide() {
        this.mRecyclerView.removeCallbacks(this.mHideRunnable);
    }

    private void resetHideDelay(int i) {
        cancelHide();
        this.mRecyclerView.postDelayed(this.mHideRunnable, (long) i);
    }

    private void drawHorizontalScrollbar() {
        this.mHorizontalThumbDrawable.setTranslationX((float) (((int) this.mHorizontalThumbCenterX) + this.mMargin));
    }

    /* access modifiers changed from: package-private */
    public void updateScrollPosition(int i) {
        int computeHorizontalScrollRange = this.mRecyclerView.computeHorizontalScrollRange();
        int i2 = this.mRecyclerViewWidth;
        if (computeHorizontalScrollRange - i2 > 0) {
            this.mHorizontalThumbCenterX = (((double) ((i2 - this.mHorizontalThumbWidth) - this.mMargin)) * ((double) i)) / ((double) (((RecyclerAdapter) this.mRecyclerView.getAdapter()).getTotalWaveViewWidth() - i2));
            drawHorizontalScrollbar();
        } else if (this.mState != 0) {
            setState(0);
        }
    }

    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        if (isRecording()) {
            return false;
        }
        setState(1);
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!this.enable || isRecording()) {
            return false;
        }
        int i = this.mState;
        if (i == 1) {
            boolean isPointInsideHorizontalThumb = isPointInsideHorizontalThumb(motionEvent.getX(), motionEvent.getY());
            if (motionEvent.getAction() != 0 || !isPointInsideHorizontalThumb) {
                setState(1);
                return false;
            }
            this.isTouching = true;
            this.mDragState = 1;
            this.mHorizontalDragX = (float) ((int) motionEvent.getX());
            setState(2);
        } else if (i != 2) {
            setState(1);
            return false;
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.mState == 0 || isRecording()) {
            return false;
        }
        if (motionEvent.getAction() == 0) {
            if (isPointInsideHorizontalThumb(motionEvent.getX(), motionEvent.getY())) {
                this.mDragState = 1;
                this.mHorizontalDragX = (float) ((int) motionEvent.getX());
                this.isTouching = true;
                setState(2);
            }
        } else if (motionEvent.getAction() == 1 && this.mState == 2) {
            this.mHorizontalDragX = 0.0f;
            this.isTouching = false;
            setState(1);
            this.mDragState = 0;
        } else if (motionEvent.getAction() == 2 && this.mState == 2) {
            show();
            if (this.mDragState == 1) {
                horizontalScrollTo(motionEvent.getX());
                this.isTouching = true;
            }
        }
        return true;
    }

    private void horizontalScrollTo(float f) {
        int[] horizontalRange = getHorizontalRange();
        float max = Math.max((float) horizontalRange[0], Math.min((float) horizontalRange[1], f));
        if (Math.abs(this.mHorizontalThumbCenterX - ((double) max)) >= 2.0d) {
            int scrollTo = scrollTo(this.mHorizontalDragX, max, horizontalRange, this.mRecyclerView.computeHorizontalScrollRange(), this.mRecyclerView.getHorizontalScrollOffset(), this.mRecyclerViewWidth);
            if (scrollTo != 0) {
                this.mRecyclerView.scrollBy(scrollTo, 0);
            }
            this.mHorizontalDragX = max;
        }
    }

    private int scrollTo(float f, float f2, int[] iArr, int i, int i2, int i3) {
        int i4 = iArr[1] - iArr[0];
        if (i4 == 0) {
            return 0;
        }
        int i5 = i - i3;
        int i6 = (int) (((f2 - f) / ((float) i4)) * ((float) i5));
        int i7 = i2 + i6;
        if (i7 >= i5 || i7 < 0) {
            return 0;
        }
        return i6;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isPointInsideHorizontalThumb(float f, float f2) {
        double d = (double) f;
        double d2 = this.mHorizontalThumbCenterX;
        return d >= d2 && d <= d2 + ((double) this.mHorizontalThumbWidth);
    }

    /* access modifiers changed from: package-private */
    public boolean isRecording() {
        SimpleEngine activeEngine = SimpleEngineManager.getInstance().getActiveEngine();
        return Engine.getInstance().getRecorderState() == 2 || (activeEngine != null ? activeEngine.getRecorderState() == 2 : SimpleEngineManager.getInstance().getEngineSize() > 0);
    }

    private int[] getHorizontalRange() {
        int[] iArr = this.mHorizontalRange;
        int i = this.mMargin;
        iArr[0] = i;
        iArr[1] = this.mRecyclerViewWidth - i;
        return iArr;
    }

    /* renamed from: com.sec.android.app.voicenote.ui.view.CustomFastScroll$AnimatorListener */
    private class AnimatorListener extends AnimatorListenerAdapter {
        private boolean mCanceled;

        private AnimatorListener() {
            this.mCanceled = false;
        }

        public void onAnimationEnd(Animator animator) {
            if (this.mCanceled) {
                this.mCanceled = false;
                return;
            }
            float floatValue = ((Float) CustomFastScroll.this.mShowHideAnimator.getAnimatedValue()).floatValue();
            if (floatValue == 0.0f) {
                int unused = CustomFastScroll.this.mAnimationState = 0;
                CustomFastScroll.this.setState(0);
                return;
            }
            int unused2 = CustomFastScroll.this.mAnimationState = 2;
            CustomFastScroll.this.requestRedraw(floatValue);
        }

        public void onAnimationCancel(Animator animator) {
            this.mCanceled = true;
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.view.CustomFastScroll$AnimatorUpdater */
    private class AnimatorUpdater implements ValueAnimator.AnimatorUpdateListener {
        private AnimatorUpdater() {
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            CustomFastScroll.this.requestRedraw(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }
    }

    private void setAnimationStateIn(int i) {
        this.mAnimationState = 3;
        ValueAnimator valueAnimator = this.mShowHideAnimator;
        valueAnimator.setFloatValues(new float[]{((Float) valueAnimator.getAnimatedValue()).floatValue(), 0.0f});
        this.mShowHideAnimator.setDuration((long) i);
        this.mShowHideAnimator.start();
    }

    private void setAnimationStateOut() {
        this.mAnimationState = 1;
        ValueAnimator valueAnimator = this.mShowHideAnimator;
        valueAnimator.setFloatValues(new float[]{((Float) valueAnimator.getAnimatedValue()).floatValue(), 1.0f});
        this.mShowHideAnimator.setDuration(500);
        this.mShowHideAnimator.setStartDelay(0);
        this.mShowHideAnimator.start();
    }
}
