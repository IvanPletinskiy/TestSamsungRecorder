package com.sec.android.app.voicenote.p007ui.pager;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.provider.TypefaceProvider;
import com.sec.android.app.voicenote.service.Recorder;
import java.util.Locale;

/* renamed from: com.sec.android.app.voicenote.ui.pager.IdleWaveStandard */
public class IdleWaveStandard extends View {
    protected Paint mBackgroundPaint;
    protected int mIntervalTime;
    protected int mLongLineHeight;
    protected int mMarginStart;
    protected Resources mResources;
    protected int mShortLineHeight;
    protected int mSpaceBetweenTwoLines;
    protected int mStartTime;
    protected Paint mTimeLongLinePaint;
    protected Paint mTimeShortLinePaint;
    protected int mTimeTextHeight;
    protected Paint mTimeTextPaint;
    protected int mViewHeight;
    protected int mViewWidth;
    protected int mY_timeLineBottom;
    protected int mY_timeLineTop;
    protected int mY_timeText;

    /* access modifiers changed from: protected */
    public int getWaveTopHeight() {
        return 0;
    }

    public IdleWaveStandard(Context context) {
        super(context);
        init();
    }

    public IdleWaveStandard(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public IdleWaveStandard(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    /* access modifiers changed from: protected */
    public void init() {
        this.mResources = getResources();
        this.mBackgroundPaint = new Paint();
        this.mBackgroundPaint.setColor(this.mResources.getColor(C0690R.C0691color.wave_window_bg, (Resources.Theme) null));
        this.mTimeTextPaint = new Paint();
        this.mTimeTextPaint.setColor(this.mResources.getColor(C0690R.C0691color.wave_time_text, (Resources.Theme) null));
        this.mTimeTextPaint.setTextSize((float) this.mResources.getDimensionPixelSize(C0690R.dimen.wave_time_text_size));
        this.mTimeTextPaint.setTextAlign(Paint.Align.CENTER);
        this.mTimeTextPaint.setTypeface(TypefaceProvider.getRobotoCondensedRegularFont());
        this.mTimeTextPaint.setAntiAlias(true);
        this.mTimeLongLinePaint = new Paint();
        this.mTimeLongLinePaint.setColor(this.mResources.getColor(C0690R.C0691color.wave_time_long_line, (Resources.Theme) null));
        this.mTimeLongLinePaint.setStyle(Paint.Style.STROKE);
        this.mTimeLongLinePaint.setStrokeWidth((float) this.mResources.getDimensionPixelSize(C0690R.dimen.wave_view_line_stroke));
        this.mTimeShortLinePaint = new Paint();
        this.mTimeShortLinePaint.setColor(this.mResources.getColor(C0690R.C0691color.wave_time_short_line, (Resources.Theme) null));
        this.mTimeShortLinePaint.setStyle(Paint.Style.STROKE);
        this.mTimeShortLinePaint.setStrokeWidth((float) this.mResources.getDimensionPixelSize(C0690R.dimen.wave_view_line_stroke));
        this.mTimeTextHeight = getTimeTextHeight();
        this.mY_timeLineTop = getWaveTopHeight();
        int i = this.mY_timeLineTop;
        int i2 = this.mTimeTextHeight;
        this.mY_timeText = ((i2 * 3) / 4) + i;
        this.mY_timeLineBottom = i + i2;
        this.mIntervalTime = 500;
        this.mStartTime = 0;
        this.mMarginStart = this.mResources.getDimensionPixelSize(C0690R.dimen.wave_view_margin_left);
        this.mSpaceBetweenTwoLines = this.mResources.getDimensionPixelSize(C0690R.dimen.wave_view_space_between_two_lines);
        this.mLongLineHeight = this.mResources.getDimensionPixelSize(C0690R.dimen.wave_time_standard_long_line_height);
        this.mShortLineHeight = this.mResources.getDimensionPixelSize(C0690R.dimen.wave_time_standard_short_line_height);
    }

    /* access modifiers changed from: protected */
    public int getTimeTextHeight() {
        return this.mResources.getDimensionPixelSize(C0690R.dimen.wave_time_texts_layout_height_standard);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWaveBackground(canvas);
        drawTimeLine(canvas);
    }

    /* access modifiers changed from: protected */
    public void drawWaveBackground(Canvas canvas) {
        canvas.drawRect(0.0f, (float) this.mY_timeLineBottom, (float) this.mViewWidth, (float) this.mViewHeight, this.mBackgroundPaint);
    }

    /* access modifiers changed from: protected */
    public void drawTimeLine(Canvas canvas) {
        int i = this.mStartTime;
        for (int i2 = this.mMarginStart; i2 < this.mViewWidth; i2 += this.mSpaceBetweenTwoLines) {
            if (i % Recorder.CHECK_AVAIABLE_STORAGE == 0) {
                float f = (float) i2;
                canvas.drawText(getStringBySecond(i / 1000), f, (float) this.mY_timeText, this.mTimeTextPaint);
                int i3 = this.mY_timeLineBottom;
                canvas.drawLine(f, (float) i3, f, (float) (i3 + this.mLongLineHeight), this.mTimeLongLinePaint);
            } else {
                Canvas canvas2 = canvas;
                float f2 = (float) i2;
                int i4 = this.mY_timeLineBottom;
                canvas.drawLine(f2, (float) i4, f2, (float) (i4 + this.mShortLineHeight), this.mTimeShortLinePaint);
            }
            i += this.mIntervalTime;
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.mViewWidth = i;
        this.mViewHeight = i2;
    }

    /* access modifiers changed from: package-private */
    public String getStringBySecond(int i) {
        return String.format(Locale.getDefault(), "%02d:%02d", new Object[]{0, Integer.valueOf(i)});
    }
}
