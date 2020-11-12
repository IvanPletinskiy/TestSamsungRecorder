package com.sec.android.app.voicenote.p007ui.pager;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.service.Recorder;

/* renamed from: com.sec.android.app.voicenote.ui.pager.IdleWaveInterview */
public class IdleWaveInterview extends IdleWaveStandard {
    public IdleWaveInterview(Context context) {
        super(context);
    }

    public IdleWaveInterview(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public IdleWaveInterview(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public int getTimeTextHeight() {
        return this.mResources.getDimensionPixelSize(C0690R.dimen.wave_time_texts_layout_height_interview);
    }

    /* access modifiers changed from: protected */
    public int getWaveTopHeight() {
        return this.mResources.getDimensionPixelSize(C0690R.dimen.wave_view_interview_top_bottom_height);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.mY_timeLineTop = (i2 - this.mResources.getDimensionPixelSize(C0690R.dimen.wave_time_texts_layout_height_interview)) / 2;
        int i5 = this.mY_timeLineTop;
        int i6 = this.mTimeTextHeight;
        this.mY_timeText = ((i6 * 3) / 4) + i5;
        this.mY_timeLineBottom = i5 + i6;
    }

    /* access modifiers changed from: protected */
    public void drawWaveBackground(Canvas canvas) {
        super.drawWaveBackground(canvas);
        canvas.drawRect(0.0f, 0.0f, (float) this.mViewWidth, (float) this.mY_timeLineTop, this.mBackgroundPaint);
    }

    /* access modifiers changed from: protected */
    public void drawTimeLine(Canvas canvas) {
        super.drawTimeLine(canvas);
        int i = this.mStartTime;
        for (int i2 = this.mMarginStart; i2 < this.mViewWidth; i2 += this.mSpaceBetweenTwoLines) {
            if (i % Recorder.CHECK_AVAIABLE_STORAGE == 0) {
                float f = (float) i2;
                int i3 = this.mY_timeLineTop;
                canvas.drawLine(f, (float) i3, f, (float) (i3 - this.mLongLineHeight), this.mTimeLongLinePaint);
            } else {
                float f2 = (float) i2;
                int i4 = this.mY_timeLineTop;
                canvas.drawLine(f2, (float) i4, f2, (float) (i4 - this.mShortLineHeight), this.mTimeShortLinePaint);
            }
            i += this.mIntervalTime;
        }
    }
}
