package com.sec.android.app.voicenote.p007ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.sec.android.app.voicenote.p007ui.adapter.RecyclerAdapter;
import com.sec.android.app.voicenote.p007ui.view.CustomFastScroll;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.WaveProvider;

/* renamed from: com.sec.android.app.voicenote.ui.view.WaveRecyclerView */
public class WaveRecyclerView extends RecyclerView {
    private static final String TAG = "WaveRecyclerView";
    private CustomFastScroll customFastScroll;
    private long mCurrentTime = 0;
    private boolean mIsSimpleMode;
    private long mLastUpdateLogTime = 0;
    private int mMinX;
    private FrameLayout parent;

    public WaveRecyclerView(Context context) {
        super(context);
    }

    public WaveRecyclerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public WaveRecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void init(boolean z) {
        this.mIsSimpleMode = z;
        if (this.mIsSimpleMode) {
            this.mMinX = WaveProvider.WAVE_VIEW_WIDTH - (WaveProvider.SIMPLE_WAVE_AREA_WIDTH / 2);
        } else {
            this.mMinX = WaveProvider.WAVE_VIEW_WIDTH - (WaveProvider.WAVE_AREA_WIDTH / 2);
        }
    }

    public int getHorizontalScrollOffset() {
        int computeHorizontalScrollOffset = computeHorizontalScrollOffset() - this.mMinX;
        if (computeHorizontalScrollOffset < 0) {
            return 0;
        }
        return computeHorizontalScrollOffset;
    }

    public int getMaxScrollRange() {
        int totalWaveViewWidth;
        int i;
        if (this.mIsSimpleMode) {
            totalWaveViewWidth = ((RecyclerAdapter) getAdapter()).getTotalWaveViewWidth();
            i = WaveProvider.SIMPLE_WAVE_AREA_WIDTH;
        } else {
            totalWaveViewWidth = ((RecyclerAdapter) getAdapter()).getTotalWaveViewWidth();
            i = WaveProvider.WAVE_AREA_WIDTH;
        }
        return totalWaveViewWidth - i;
    }

    public void scrollByPosition(int i) {
        int i2 = i + this.mMinX;
        int computeHorizontalScrollOffset = computeHorizontalScrollOffset();
        int i3 = i2 - computeHorizontalScrollOffset;
        if (i3 != 0) {
            if (Log.ENG) {
                this.mCurrentTime = System.currentTimeMillis();
                if (this.mCurrentTime - this.mLastUpdateLogTime > 1000) {
                    Log.m19d(TAG, "scrollByPosition - position : " + i2 + " currentX : " + computeHorizontalScrollOffset + " scrollBy : " + i3);
                    this.mLastUpdateLogTime = this.mCurrentTime;
                }
            }
            try {
                super.scrollBy(i3, 0);
                if (i2 == this.mMinX || i2 == getMaxScrollRange() + this.mMinX) {
                    stopScroll();
                }
            } catch (IndexOutOfBoundsException e) {
                Log.m22e(TAG, "scrollByPosition Exception : " + e);
            }
        } else {
            Log.m19d(TAG, "scrollByPosition - current position : " + computeHorizontalScrollOffset);
        }
    }

    public void smoothScrollByPosition(int i) {
        int horizontalScrollOffset = getHorizontalScrollOffset();
        int i2 = i - horizontalScrollOffset;
        if (i2 < 0) {
            i2 = 0;
        }
        super.smoothScrollBy(i2, 0);
        Log.m19d(TAG, "smoothScrollByPosition - position : " + i + " currentX : " + horizontalScrollOffset + " scrollBy : " + i2);
    }

    public void setFastScrollEnable(boolean z) {
        if (!z) {
            CustomFastScroll customFastScroll2 = this.customFastScroll;
            if (customFastScroll2 != null) {
                customFastScroll2.setScrollEnable(z);
                this.customFastScroll.hide();
            }
        } else if (this.parent != null) {
            this.customFastScroll = new CustomFastScroll(getContext());
            this.customFastScroll.attachToRecyclerView(this);
            this.parent.addView(this.customFastScroll);
        }
    }

    public void setShowHideScrollBar(boolean z) {
        if (this.parent != null) {
            if (z) {
                this.customFastScroll.setVisibility(0);
            } else {
                this.customFastScroll.setVisibility(8);
            }
        }
    }

    public void hideScrollBar() {
        this.customFastScroll.hide();
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        CustomFastScroll customFastScroll2 = this.customFastScroll;
        if (customFastScroll2 != null) {
            customFastScroll2.recyclerViewSizeChange(i, i2);
        }
    }

    public void setParent(FrameLayout frameLayout) {
        this.parent = frameLayout;
    }

    public void setScrollBarStateChangeListener(CustomFastScroll.onScrollBarStateChangeListener onscrollbarstatechangelistener) {
        CustomFastScroll customFastScroll2 = this.customFastScroll;
        if (customFastScroll2 != null) {
            customFastScroll2.setScrollBarStateChangeListener(onscrollbarstatechangelistener);
        }
    }

    public void setHorizontalScrollBarEnabled(boolean z) {
        super.setHorizontalScrollBarEnabled(z);
        CustomFastScroll customFastScroll2 = this.customFastScroll;
        if (customFastScroll2 != null) {
            customFastScroll2.setScrollEnable(z);
        }
    }
}
