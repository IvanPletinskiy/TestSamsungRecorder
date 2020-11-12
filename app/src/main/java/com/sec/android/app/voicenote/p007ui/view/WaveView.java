package com.sec.android.app.voicenote.p007ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.core.internal.view.SupportMenu;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.main.VNMainActivity;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.TypefaceProvider;
import com.sec.android.app.voicenote.provider.WaveProvider;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

/* renamed from: com.sec.android.app.voicenote.ui.view.WaveView */
public class WaveView extends View {
    private static final int MAX_AMPLITUDE = 15000;
    private static final int PAINT_DIM = 1;
    private static final int PAINT_NORMAL = 0;
    private static final int PAINT_OVERWRITE = 2;
    private static final int RECOVER_THRESHOLD = 500;
    private static final int SCENE_EDIT = 6;
    private static final String TAG = "WaveView";
    private static final int VOLUME_THRESHOLD = 5000;
    private static Drawable mBookmarkIcon = null;
    private static Paint[] mPaintBookmark = new Paint[2];
    private static Paint[] mPaint_amplitude1 = new Paint[8];
    private static Paint mPaint_repeat = null;
    private static Paint mPaint_selected_region = null;
    private static Paint[] mPaint_timeLongLine = new Paint[2];
    private static Paint[] mPaint_timeShortLine = new Paint[2];
    private static Paint mPaint_timeText = null;
    private int mAmplitudeIndex;
    private ArrayList<Integer> mBookmarks = new ArrayList<>();
    private Context mContext;
    private long mCurrentTime = 0;
    private int mEndRepeatTime = -1;
    private int mHalfVolumeMultiply;
    private boolean mIgnoreTimeLine;
    private boolean mIsSimpleMode;
    private int mItemIndex;
    private long mLastUpdateLogTime = 0;
    private float[] mLongLineEndY = new float[5];
    private float[] mLongLineStartY = new float[5];
    private int mOldAmplitude_down;
    private int mOldAmplitude_up;
    private int mRecordMode;
    private float[] mShortLineEndY = new float[5];
    private float[] mShortLineStartY = new float[5];
    private ArrayList<Integer> mSize_Down = new ArrayList<>();
    private ArrayList<Integer> mSize_Up = new ArrayList<>();
    private int mStartRepeatTime = -1;
    private int mViewHeight;
    private int mViewWidth;
    private int mVolumeMultiply;
    private int mWaveReduce;
    private float mY_timeText;
    private float mY_waveDown;
    private float mY_waveUp;

    private static int getAlphaInt(float f) {
        if (f <= 0.0f) {
            return 0;
        }
        if (f > 1.0f) {
            return 255;
        }
        return (int) (f * 255.0f);
    }

    public WaveView(Context context, int i, int i2, boolean z) {
        super(context);
        this.mContext = context;
        this.mItemIndex = i;
        this.mRecordMode = i2;
        this.mIsSimpleMode = z;
        initAmplitude();
        updateWaveViewSize();
        setPivotX(0.0f);
    }

    public void initAmplitude() {
        this.mAmplitudeIndex = 0;
        this.mSize_Up.clear();
        this.mSize_Down.clear();
        this.mBookmarks.clear();
        for (int i = 0; i < WaveProvider.NUM_OF_AMPLITUDE; i++) {
            this.mSize_Up.add(-1);
            this.mSize_Down.add(-1);
        }
        postInvalidate();
    }

    private void updateWaveViewSize() {
        this.mViewWidth = WaveProvider.WAVE_VIEW_WIDTH;
        if (this.mIsSimpleMode) {
            this.mViewHeight = WaveProvider.SIMPLE_WAVE_HEIGHT;
            this.mVolumeMultiply = WaveProvider.SIMPLE_WAVE_VIEW_HEIGHT;
        } else {
            this.mViewHeight = WaveProvider.WAVE_HEIGHT;
            this.mVolumeMultiply = WaveProvider.WAVE_VIEW_HEIGHT;
        }
        this.mHalfVolumeMultiply = this.mVolumeMultiply / 2;
        this.mIgnoreTimeLine = this.mRecordMode == 2 && ((float) this.mHalfVolumeMultiply) * 0.75f < getResources().getDimension(C0690R.dimen.wave_time_standard_long_line_height);
        this.mWaveReduce = getResources().getDimensionPixelOffset(C0690R.dimen.wave_view_column_height_reduce);
        resetWaveUpnDown(this.mRecordMode);
        this.mLongLineStartY[1] = (float) (getResources().getDimensionPixelSize(C0690R.dimen.wave_time_text_height) + getResources().getDimensionPixelSize(C0690R.dimen.wave_bookmark_top_margin));
        this.mLongLineEndY[1] = this.mLongLineStartY[1] + getResources().getDimension(C0690R.dimen.wave_time_standard_long_line_height);
        float[] fArr = this.mShortLineStartY;
        fArr[1] = this.mLongLineStartY[1];
        this.mShortLineEndY[1] = fArr[1] + getResources().getDimension(C0690R.dimen.wave_time_standard_short_line_height);
        int dimensionPixelSize = getResources().getDimensionPixelSize(C0690R.dimen.wave_time_text_height) + (this.mVolumeMultiply / 2);
        this.mLongLineStartY[2] = (float) (dimensionPixelSize - getResources().getDimensionPixelSize(C0690R.dimen.wave_time_standard_long_line_height));
        float f = (float) dimensionPixelSize;
        this.mLongLineEndY[2] = f;
        this.mShortLineStartY[2] = (float) (dimensionPixelSize - getResources().getDimensionPixelSize(C0690R.dimen.wave_time_standard_short_line_height));
        float[] fArr2 = this.mShortLineEndY;
        fArr2[2] = f;
        float[] fArr3 = this.mLongLineStartY;
        fArr3[4] = fArr3[1];
        float[] fArr4 = this.mLongLineEndY;
        fArr4[4] = fArr4[1];
        float[] fArr5 = this.mShortLineStartY;
        fArr5[4] = fArr5[1];
        fArr2[4] = fArr2[1];
    }

    private void resetWaveUpnDown(int i) {
        if (i != 2) {
            this.mY_waveUp = -1.0f;
            this.mY_waveDown = (float) (getResources().getDimensionPixelOffset(C0690R.dimen.wave_time_text_height) + getResources().getDimensionPixelOffset(C0690R.dimen.wave_bookmark_top_margin) + (this.mVolumeMultiply / 2));
            this.mY_timeText = (float) ((getResources().getDimensionPixelOffset(C0690R.dimen.wave_time_text_height) + getResources().getDimensionPixelOffset(C0690R.dimen.wave_bookmark_top_margin)) - getResources().getDimensionPixelOffset(C0690R.dimen.wave_time_text_normal_margin_bottom));
            return;
        }
        this.mY_waveUp = (float) (getResources().getDimensionPixelOffset(C0690R.dimen.wave_bookmark_top_margin) + (this.mVolumeMultiply / 4));
        int dimensionPixelOffset = getResources().getDimensionPixelOffset(C0690R.dimen.wave_time_text_height) + getResources().getDimensionPixelOffset(C0690R.dimen.wave_bookmark_top_margin);
        int i2 = this.mVolumeMultiply;
        this.mY_waveDown = (float) (dimensionPixelOffset + (i2 / 2) + (i2 / 4));
        this.mY_timeText = (float) (((getResources().getDimensionPixelOffset(C0690R.dimen.wave_time_text_height) + getResources().getDimensionPixelOffset(C0690R.dimen.wave_bookmark_top_margin)) + (this.mVolumeMultiply / 2)) - getResources().getDimensionPixelOffset(C0690R.dimen.wave_time_text_normal_margin_bottom));
    }

    public static void updateWaveAttributes() {
        Paint[] paintArr;
        Paint[] paintArr2;
        Resources resources = VoiceNoteApplication.getApplication().getResources();
        int i = 0;
        while (true) {
            paintArr = mPaint_amplitude1;
            if (i >= paintArr.length) {
                break;
            }
            paintArr[i] = new Paint();
            i++;
        }
        paintArr[0].setColor(resources.getColor(C0690R.C0691color.wave_normal, (Resources.Theme) null));
        mPaint_amplitude1[0].setStrokeCap(Paint.Cap.ROUND);
        mPaint_amplitude1[0].setStyle(Paint.Style.FILL);
        mPaint_amplitude1[0].setStrokeWidth((float) WaveProvider.AMPLITUDE_STROKE_WIDTH);
        mPaint_amplitude1[0].setAlpha(getAlphaInt(1.0f));
        Paint[] paintArr3 = mPaint_amplitude1;
        paintArr3[1].set(paintArr3[0]);
        mPaint_amplitude1[1].setAlpha(getAlphaInt(0.1f));
        Paint[] paintArr4 = mPaint_amplitude1;
        paintArr4[2].set(paintArr4[0]);
        mPaint_amplitude1[2].setColor(resources.getColor(C0690R.C0691color.wave_overwrite, (Resources.Theme) null));
        Paint[] paintArr5 = mPaint_amplitude1;
        paintArr5[3].set(paintArr5[2]);
        mPaint_amplitude1[3].setAlpha(getAlphaInt(0.1f));
        mPaint_timeText = new Paint();
        mPaint_timeText.setColor(resources.getColor(C0690R.C0691color.wave_time_text, (Resources.Theme) null));
        mPaint_timeText.setTextSize((float) resources.getDimensionPixelSize(C0690R.dimen.wave_time_text_size));
        mPaint_timeText.setTextAlign(Paint.Align.CENTER);
        mPaint_timeText.setTypeface(TypefaceProvider.getRobotoCondensedRegularFont());
        mPaint_timeText.setAntiAlias(true);
        int i2 = 0;
        while (true) {
            paintArr2 = mPaint_timeLongLine;
            if (i2 >= paintArr2.length) {
                break;
            }
            paintArr2[i2] = new Paint();
            mPaint_timeShortLine[i2] = new Paint();
            i2++;
        }
        paintArr2[0].setColor(resources.getColor(C0690R.C0691color.wave_time_long_line, (Resources.Theme) null));
        mPaint_timeLongLine[0].setStyle(Paint.Style.STROKE);
        mPaint_timeLongLine[0].setStrokeWidth((float) resources.getDimensionPixelSize(C0690R.dimen.wave_view_line_stroke));
        mPaint_timeShortLine[0].setColor(resources.getColor(C0690R.C0691color.wave_time_short_line, (Resources.Theme) null));
        mPaint_timeShortLine[0].setStyle(Paint.Style.STROKE);
        mPaint_timeShortLine[0].setStrokeWidth((float) resources.getDimensionPixelSize(C0690R.dimen.wave_view_line_stroke));
        Paint[] paintArr6 = mPaint_timeLongLine;
        paintArr6[1].set(paintArr6[0]);
        Paint[] paintArr7 = mPaint_timeShortLine;
        paintArr7[1].set(paintArr7[0]);
        mPaint_timeLongLine[1].setAlpha(getAlphaInt(0.15f));
        mPaint_timeShortLine[1].setAlpha(getAlphaInt(0.05f));
        int i3 = 0;
        while (true) {
            Paint[] paintArr8 = mPaintBookmark;
            if (i3 < paintArr8.length) {
                paintArr8[i3] = new Paint();
                i3++;
            } else {
                paintArr8[0].setColor(resources.getColor(C0690R.C0691color.recording_time_bookmark, (Resources.Theme) null));
                mPaintBookmark[0].setStyle(Paint.Style.STROKE);
                mPaintBookmark[0].setStrokeWidth(resources.getDimension(C0690R.dimen.wave_bookmark_width));
                Paint[] paintArr9 = mPaintBookmark;
                paintArr9[1].set(paintArr9[0]);
                mPaintBookmark[1].setAlpha(getAlphaInt(0.15f));
                mPaint_repeat = new Paint();
                mPaint_repeat.setColor(resources.getColor(C0690R.C0691color.wave_repeat_regine, (Resources.Theme) null));
                mPaint_repeat.setStrokeCap(Paint.Cap.SQUARE);
                mPaint_repeat.setStyle(Paint.Style.FILL);
                mBookmarkIcon = resources.getDrawable(C0690R.C0692drawable.ic_voice_rec_ic_bookmark, (Resources.Theme) null);
                int dimensionPixelSize = resources.getDimensionPixelSize(C0690R.dimen.bookmark_image_width_height);
                mBookmarkIcon.setBounds(0, 0, dimensionPixelSize, dimensionPixelSize);
                mPaint_selected_region = new Paint();
                mPaint_selected_region.setColor(resources.getColor(C0690R.C0691color.wave_selected_regine, (Resources.Theme) null));
                mPaint_selected_region.setStrokeCap(Paint.Cap.SQUARE);
                mPaint_selected_region.setStyle(Paint.Style.FILL);
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        Canvas canvas2 = canvas;
        super.onDraw(canvas);
        int i8 = WaveProvider.DURATION_PER_WAVEVIEW;
        int i9 = this.mItemIndex * i8;
        int i10 = i9 - i8;
        if (this.mContext instanceof VNMainActivity) {
            i2 = Engine.getInstance().getTrimStartTime();
            i = Engine.getInstance().getTrimEndTime();
        } else {
            i2 = -1;
            i = -1;
        }
        if (i9 == 0) {
            i3 = 0;
        } else {
            i3 = Math.round((((float) i9) * 1.0f) / 1000.0f);
        }
        if (i10 == 0) {
            i4 = 0;
        } else {
            i4 = i10 / 1000;
        }
        int i11 = i4 - 2;
        if (i11 > 0) {
            i4 = i11;
        }
        while (true) {
            i5 = 2;
            if (i4 > i3) {
                break;
            }
            if (i4 % 2 == 0) {
                int i12 = i4 * 1000;
                float dimensionPixelSize = (((float) (i12 - i10)) * WaveProvider.PX_PER_MS) + ((float) getResources().getDimensionPixelSize(C0690R.dimen.wave_view_line_stroke));
                if (i4 >= 0) {
                    canvas2.drawText(getStringBySecond(i4), dimensionPixelSize, this.mY_timeText, mPaint_timeText);
                }
                if (!this.mIgnoreTimeLine) {
                    drawTimeLine(canvas2, dimensionPixelSize, i12, 2);
                }
            }
            i4++;
        }
        int size = this.mSize_Down.size();
        int overwriteStartTime = Engine.getInstance().getOverwriteStartTime();
        int overwriteEndTime = Engine.getInstance().getOverwriteEndTime();
        int i13 = 0;
        while (i13 < size) {
            float f = (float) (WaveProvider.AMPLITUDE_TOTAL_WIDTH * i13);
            int i14 = (i13 * 70) + i10;
            if (this.mContext instanceof VNMainActivity) {
                int i15 = (overwriteStartTime == -1 || i14 < overwriteStartTime || overwriteEndTime == -1 || i14 > overwriteEndTime + 35) ? 0 : i5;
                if ((i2 != -1 && i14 < i2) || (i != -1 && i14 > i)) {
                    i15++;
                }
                i7 = i15;
            } else {
                i7 = 0;
            }
            float f2 = this.mY_waveUp;
            float intValue = (float) this.mSize_Up.get(i13).intValue();
            float f3 = f2;
            Canvas canvas3 = canvas;
            float f4 = f;
            int i16 = i13;
            float f5 = f3;
            int i17 = overwriteEndTime;
            float f6 = intValue;
            int i18 = overwriteStartTime;
            int i19 = i7;
            drawOneAmplitude(canvas3, f, f5, f6, i19);
            drawOneAmplitude(canvas3, f, this.mY_waveDown, (float) this.mSize_Down.get(i16).intValue(), i19);
            i13 = i16 + 1;
            overwriteStartTime = i18;
            overwriteEndTime = i17;
            i5 = 2;
        }
        int leftPos = getLeftPos(i2, i, i10);
        int rightPos = getRightPos(i2, i, i10);
        int size2 = this.mBookmarks.size();
        for (int i20 = 0; i20 < size2; i20++) {
            float intValue2 = (float) (this.mBookmarks.get(i20).intValue() * WaveProvider.AMPLITUDE_TOTAL_WIDTH);
            if ((i2 == -1 || intValue2 >= ((float) leftPos)) && (i == -1 || intValue2 <= ((float) rightPos))) {
                drawBookmark(canvas2, intValue2, 0.0f, 0);
            } else {
                drawBookmark(canvas2, intValue2, 0.0f, 1);
            }
        }
        int i21 = this.mStartRepeatTime;
        if (!(i21 == -1 || (i6 = this.mEndRepeatTime) == -1)) {
            int leftPos2 = getLeftPos(i21, i6, i10);
            int rightPos2 = getRightPos(this.mStartRepeatTime, this.mEndRepeatTime, i10);
            if (leftPos2 <= this.mViewWidth && rightPos2 >= 0) {
                drawRegion(canvas2, leftPos2, rightPos2, mPaint_repeat);
            }
        }
        if ((this.mContext instanceof VNMainActivity) && VoiceNoteApplication.getScene() == 6 && Engine.getInstance().getRecorderState() == 1 && leftPos <= this.mViewWidth && rightPos >= 0) {
            drawRegion(canvas2, leftPos, rightPos, mPaint_selected_region);
        }
        Drawable drawable = mBookmarkIcon;
        if (drawable != null) {
            drawable.setAlpha(getAlphaInt(1.0f));
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
    }

    public void requestDraw() {
        updateWaveViewSize();
        postInvalidate();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        setMeasuredDimension(this.mViewWidth, this.mViewHeight);
    }

    private int getLeftPos(int i, int i2, int i3) {
        if (i >= i2) {
            i = i2;
        }
        int i4 = i - i3;
        if (i4 > 0) {
            return (int) (((float) i4) * WaveProvider.PX_PER_MS);
        }
        return -1;
    }

    private int getRightPos(int i, int i2, int i3) {
        if (i < i2) {
            i = i2;
        }
        int i4 = i - i3;
        if (i4 > 0) {
            return (int) (((float) i4) * WaveProvider.PX_PER_MS);
        }
        return -1;
    }

    public void setRecordMode(int i) {
        this.mRecordMode = i;
        resetWaveUpnDown(this.mRecordMode);
    }

    public void setRepeatStartTime(int i) {
        this.mStartRepeatTime = i;
    }

    public void setRepeatEndTime(int i) {
        this.mEndRepeatTime = i;
    }

    public void setRepeatTime(int i, int i2) {
        this.mStartRepeatTime = i;
        this.mEndRepeatTime = i2;
    }

    public void setAmplitudeIndex(int i) {
        Log.m19d(TAG, "setAmplitudeIndex - index : " + i);
        this.mAmplitudeIndex = i;
    }

    public void setItemIndex(int i) {
        this.mItemIndex = i;
    }

    public int getAmplitudeIndex() {
        return this.mAmplitudeIndex;
    }

    public int getAmplitudeCount() {
        for (int size = this.mSize_Down.size() - 1; size >= 0; size--) {
            if (this.mSize_Down.get(size).intValue() != -1) {
                return size + 1;
            }
        }
        return 0;
    }

    public int getViewWidth() {
        return this.mViewWidth;
    }

    public int getViewHeight() {
        return this.mViewHeight;
    }

    public void addBookmark(int i) {
        Log.m19d(TAG, "addBookmark - index : " + i);
        this.mBookmarks.add(Integer.valueOf(i));
        postInvalidate();
    }

    public boolean removeBookmark(int i) {
        return this.mBookmarks.remove(Integer.valueOf(i));
    }

    public void updateWaveArray(int[] iArr, int i) {
        double d;
        double d2;
        int[] iArr2 = iArr;
        int i2 = i;
        if (iArr2 == null) {
            Log.m22e(TAG, "updateWaveArray - array is null");
            return;
        }
        Log.m19d(TAG, "updateWaveArray - [" + this.mItemIndex + "] amplitude size  : " + iArr2.length + " real size : " + i2);
        this.mOldAmplitude_up = 0;
        this.mOldAmplitude_down = 0;
        int i3 = 0;
        for (int i4 : iArr2) {
            int i5 = i4 >> 16;
            int i6 = i4 & SupportMenu.USER_MASK;
            if (this.mRecordMode == 2) {
                i5 = (int) (((double) i5) * 0.8d);
                i6 = (int) (((double) i6) * 0.8d);
            }
            if (i5 < VOLUME_THRESHOLD) {
                d = Math.log10((double) ((((float) i5) / 1500.0f) + 1.0f)) * 8000.0d;
            } else {
                d = Math.pow((double) i5, 2.0d) / 15000.0d;
            }
            int i7 = (int) d;
            if (i6 < VOLUME_THRESHOLD) {
                d2 = Math.log10((double) ((((float) i6) / 1500.0f) + 1.0f)) * 8000.0d;
            } else {
                d2 = Math.pow((double) i6, 2.0d) / 15000.0d;
            }
            int i8 = (int) d2;
            int i9 = this.mOldAmplitude_up;
            int i10 = i9 - i7;
            int i11 = this.mOldAmplitude_down - i8;
            if (i7 > 10000 && i9 > 10000) {
                i7 -= new Random().nextInt(i7 / 2);
                Log.m19d(TAG, "    Reduce - mAmplitudeIndex : " + this.mAmplitudeIndex + " old_up : " + this.mOldAmplitude_up + " amplitude_up : " + i7);
            } else if (i10 > 500) {
                i7 = this.mOldAmplitude_up - new Random().nextInt(i10 / 2);
                Log.m19d(TAG, "    recover - mAmplitudeIndex : " + this.mAmplitudeIndex + " old_up : " + this.mOldAmplitude_up + " amplitude_up : " + i7);
            }
            if (i8 > 10000 && this.mOldAmplitude_down > 10000) {
                i8 -= new Random().nextInt(i8 / 2);
                Log.m19d(TAG, "    Reduce - mAmplitudeIndex : " + this.mAmplitudeIndex + " old_down : " + this.mOldAmplitude_down + " amplitude_down : " + i8);
            } else if (i11 > 500) {
                i8 = this.mOldAmplitude_down - new Random().nextInt(i11 / 2);
                Log.m19d(TAG, "    recover - mAmplitudeIndex : " + this.mAmplitudeIndex + " old_down : " + this.mOldAmplitude_down + " amplitude_down : " + i8);
            }
            int i12 = this.mRecordMode;
            if (i12 == 2) {
                this.mSize_Up.set(i3, Integer.valueOf(i7));
                this.mSize_Down.set(i3, Integer.valueOf(i8));
            } else if (i12 != 4) {
                this.mSize_Down.set(i3, Integer.valueOf(i8));
            } else {
                this.mSize_Down.set(i3, Integer.valueOf(i8));
            }
            i3++;
            this.mOldAmplitude_up = i7;
            this.mOldAmplitude_down = i8;
            if (i3 >= WaveProvider.NUM_OF_AMPLITUDE || i3 >= i2) {
                break;
            }
        }
        postInvalidate();
    }

    public void addAmplitude(int i, boolean z) {
        double d;
        int i2;
        int i3 = i;
        if (this.mAmplitudeIndex < WaveProvider.NUM_OF_AMPLITUDE) {
            if (Log.ENG) {
                this.mCurrentTime = System.currentTimeMillis();
                if (this.mCurrentTime - this.mLastUpdateLogTime > 1000) {
                    Log.m19d(TAG, "addAmplitude - Item : " + this.mItemIndex + '[' + this.mAmplitudeIndex + "] amplitude : " + i3);
                    this.mLastUpdateLogTime = this.mCurrentTime;
                }
            }
            int i4 = i3 >> 16;
            int i5 = i3 & SupportMenu.USER_MASK;
            if (this.mRecordMode == 2) {
                i4 = (int) (((double) i4) * 0.8d);
                i5 = (int) (((double) i5) * 0.8d);
            }
            if (i4 < VOLUME_THRESHOLD) {
                d = Math.log10((double) ((((float) i4) / 1500.0f) + 1.0f)) * 8000.0d;
            } else {
                d = Math.pow((double) i4, 2.0d) / 15000.0d;
            }
            int i6 = (int) d;
            if (i5 < VOLUME_THRESHOLD) {
                i2 = (int) (Math.log10((double) ((((float) i5) / 1500.0f) + 1.0f)) * 8000.0d);
            } else {
                i2 = (int) (Math.pow((double) i5, 2.0d) / 15000.0d);
            }
            int i7 = this.mOldAmplitude_up;
            int i8 = i7 - i6;
            int i9 = this.mOldAmplitude_down - i2;
            if (i6 > 10000 && i7 > 10000) {
                i6 -= new Random().nextInt(i6 / 2);
                Log.m19d(TAG, "    Reduce - mAmplitudeIndex : " + this.mAmplitudeIndex + " old_up : " + this.mOldAmplitude_up + " amplitude_up : " + i6);
            } else if (i8 > 500) {
                i6 = this.mOldAmplitude_up - new Random().nextInt(i8 / 2);
                Log.m19d(TAG, "    recover - mAmplitudeIndex : " + this.mAmplitudeIndex + " old_up : " + this.mOldAmplitude_up + " amplitude_up : " + i6);
            }
            if (i2 > 10000 && this.mOldAmplitude_down > 10000) {
                i2 -= new Random().nextInt(i2 / 2);
                Log.m19d(TAG, "    Reduce - mAmplitudeIndex : " + this.mAmplitudeIndex + " old_down : " + this.mOldAmplitude_down + " amplitude_down : " + i2);
            } else if (i9 > 500) {
                i2 = this.mOldAmplitude_down - new Random().nextInt(i9 / 2);
                Log.m19d(TAG, "    recover - mAmplitudeIndex : " + this.mAmplitudeIndex + " old_down : " + this.mOldAmplitude_down + " amplitude_down : " + i2);
            }
            int i10 = this.mRecordMode;
            if (i10 == 2) {
                this.mSize_Up.set(this.mAmplitudeIndex, Integer.valueOf(i6));
                this.mSize_Down.set(this.mAmplitudeIndex, Integer.valueOf(i2));
            } else if (i10 != 4) {
                this.mSize_Down.set(this.mAmplitudeIndex, Integer.valueOf(i2));
            } else {
                this.mSize_Down.set(this.mAmplitudeIndex, Integer.valueOf(i2));
            }
            for (int i11 = 0; i11 < 5; i11++) {
                int i12 = this.mAmplitudeIndex;
                if (i12 > i11 && this.mSize_Down.get((i12 - i11) - 1).intValue() == -1) {
                    ArrayList<Integer> arrayList = this.mSize_Up;
                    int i13 = this.mAmplitudeIndex;
                    arrayList.set((i13 - i11) - 1, arrayList.get(i13));
                    ArrayList<Integer> arrayList2 = this.mSize_Down;
                    int i14 = this.mAmplitudeIndex;
                    arrayList2.set((i14 - i11) - 1, arrayList2.get(i14));
                    StringBuilder sb = new StringBuilder();
                    sb.append("    add missing data - mAmplitudeIndex : ");
                    sb.append((this.mAmplitudeIndex - i11) - 1);
                    Log.m26i(TAG, sb.toString());
                }
            }
            this.mAmplitudeIndex++;
            this.mOldAmplitude_up = i6;
            this.mOldAmplitude_down = i2;
            if (z) {
                postInvalidate();
            }
        }
    }

    private void drawOneAmplitude(Canvas canvas, float f, float f2, float f3, int i) {
        float f4 = f + ((((float) WaveProvider.AMPLITUDE_STROKE_WIDTH) * 1.0f) / 2.0f);
        if (f2 >= 0.0f && f3 >= 0.0f) {
            float amplitudeSizes = getAmplitudeSizes(f3);
            canvas.drawLine(f4, f2 - amplitudeSizes, f4, f2 + amplitudeSizes, mPaint_amplitude1[i]);
        }
    }

    private void drawBookmark(Canvas canvas, float f, float f2, int i) {
        int i2;
        int i3;
        int i4;
        int i5;
        if (this.mRecordMode != 2) {
            i5 = getResources().getDimensionPixelOffset(C0690R.dimen.wave_bookmark_top_margin);
            i4 = this.mViewHeight - i5;
            i3 = -1;
            i2 = -1;
        } else {
            i5 = getResources().getDimensionPixelOffset(C0690R.dimen.wave_bookmark_top_margin);
            i3 = getResources().getDimensionPixelOffset(C0690R.dimen.wave_bookmark_top_margin) + (this.mVolumeMultiply / 2) + getResources().getDimensionPixelOffset(C0690R.dimen.wave_time_text_height);
            i2 = this.mVolumeMultiply / 2;
            i4 = this.mVolumeMultiply / 2;
        }
        canvas.save();
        canvas.translate(f - (((float) mBookmarkIcon.getBounds().right) / 2.0f), 0.0f);
        mBookmarkIcon.setAlpha(getAlphaInt(i == 0 ? 1.0f : 0.3f));
        mBookmarkIcon.draw(canvas);
        canvas.restore();
        canvas.drawLine(f, (float) i5, f, (float) (i4 + i5), mPaintBookmark[i]);
        if (i3 != -1 && i2 != -1) {
            canvas.drawLine(f, (float) i3, f, (float) (i2 + i3), mPaintBookmark[i]);
        }
    }

    private void drawTimeLine(Canvas canvas, float f, int i, int i2) {
        int i3;
        int i4;
        int i5 = i2 * ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION;
        float f2 = ((float) i5) * WaveProvider.PX_PER_MS;
        float f3 = f + f2;
        float f4 = f3 + f2;
        float f5 = f2 + f4;
        float dimension = getResources().getDimension(C0690R.dimen.wave_time_text_height);
        float dimension2 = getResources().getDimension(C0690R.dimen.wave_time_standard_long_line_height);
        float dimension3 = getResources().getDimension(C0690R.dimen.wave_time_standard_short_line_height);
        if (this.mContext instanceof VNMainActivity) {
            i4 = Engine.getInstance().getTrimStartTime();
            i3 = Engine.getInstance().getTrimEndTime();
        } else {
            i4 = -1;
            i3 = -1;
        }
        int[] iArr = new int[4];
        int i6 = 0;
        int i7 = 0;
        while (i7 < iArr.length) {
            int i8 = i + (i5 * i7);
            iArr[i7] = i6;
            if (Engine.getInstance().getScene() == 6 && ((i4 != -1 && i8 < i4) || (i3 != -1 && i8 > i3))) {
                iArr[i7] = iArr[i7] + 1;
            }
            i7++;
            i6 = 0;
        }
        int i9 = this.mRecordMode;
        if (i9 == 2) {
            canvas.drawLine(f, this.mLongLineStartY[i9], f, this.mLongLineEndY[i9], mPaint_timeLongLine[iArr[0]]);
            float[] fArr = this.mShortLineStartY;
            int i10 = this.mRecordMode;
            canvas.drawLine(f3, fArr[i10], f3, this.mShortLineEndY[i10], mPaint_timeShortLine[iArr[1]]);
            float[] fArr2 = this.mShortLineStartY;
            int i11 = this.mRecordMode;
            canvas.drawLine(f4, fArr2[i11], f4, this.mShortLineEndY[i11], mPaint_timeShortLine[iArr[2]]);
            float[] fArr3 = this.mShortLineStartY;
            int i12 = this.mRecordMode;
            canvas.drawLine(f5, fArr3[i12], f5, this.mShortLineEndY[i12], mPaint_timeShortLine[iArr[3]]);
            float[] fArr4 = this.mLongLineStartY;
            int i13 = this.mRecordMode;
            canvas.drawLine(f, fArr4[i13] + dimension + dimension2, f, this.mLongLineEndY[i13] + dimension + dimension2, mPaint_timeLongLine[iArr[0]]);
            float[] fArr5 = this.mShortLineStartY;
            int i14 = this.mRecordMode;
            canvas.drawLine(f3, fArr5[i14] + dimension + dimension3, f3, this.mShortLineEndY[i14] + dimension + dimension3, mPaint_timeShortLine[iArr[1]]);
            float[] fArr6 = this.mShortLineStartY;
            int i15 = this.mRecordMode;
            canvas.drawLine(f4, fArr6[i15] + dimension + dimension3, f4, this.mShortLineEndY[i15] + dimension + dimension3, mPaint_timeShortLine[iArr[2]]);
            float[] fArr7 = this.mShortLineStartY;
            int i16 = this.mRecordMode;
            canvas.drawLine(f5, fArr7[i16] + dimension + dimension3, f5, this.mShortLineEndY[i16] + dimension + dimension3, mPaint_timeShortLine[iArr[3]]);
        } else if (i9 != 4) {
            canvas.drawLine(f, this.mLongLineStartY[1], f, this.mLongLineEndY[1], mPaint_timeLongLine[iArr[0]]);
            canvas.drawLine(f3, this.mShortLineStartY[1], f3, this.mShortLineEndY[1], mPaint_timeShortLine[iArr[1]]);
            canvas.drawLine(f4, this.mShortLineStartY[1], f4, this.mShortLineEndY[1], mPaint_timeShortLine[iArr[2]]);
            canvas.drawLine(f5, this.mShortLineStartY[1], f5, this.mShortLineEndY[1], mPaint_timeShortLine[iArr[3]]);
        } else {
            canvas.drawLine(f, this.mLongLineStartY[i9], f, this.mLongLineEndY[i9], mPaint_timeLongLine[iArr[0]]);
            float[] fArr8 = this.mShortLineStartY;
            int i17 = this.mRecordMode;
            canvas.drawLine(f3, fArr8[i17], f3, this.mShortLineEndY[i17], mPaint_timeShortLine[iArr[1]]);
            float[] fArr9 = this.mShortLineStartY;
            int i18 = this.mRecordMode;
            canvas.drawLine(f4, fArr9[i18], f4, this.mShortLineEndY[i18], mPaint_timeShortLine[iArr[2]]);
            float[] fArr10 = this.mShortLineStartY;
            int i19 = this.mRecordMode;
            canvas.drawLine(f5, fArr10[i19], f5, this.mShortLineEndY[i19], mPaint_timeShortLine[iArr[3]]);
        }
    }

    private void drawRegion(Canvas canvas, int i, int i2, Paint paint) {
        if (i < 0) {
            i = 0;
        }
        int i3 = this.mViewWidth;
        if (i2 > i3) {
            i2 = i3;
        }
        if (this.mRecordMode != 2) {
            Canvas canvas2 = canvas;
            canvas2.drawRect((float) i, (float) (getResources().getDimensionPixelOffset(C0690R.dimen.wave_bookmark_top_margin) + getResources().getDimensionPixelOffset(C0690R.dimen.wave_time_text_height)), (float) i2, (float) this.mViewHeight, paint);
            return;
        }
        int dimensionPixelOffset = getResources().getDimensionPixelOffset(C0690R.dimen.wave_bookmark_top_margin);
        int i4 = (this.mVolumeMultiply / 2) + dimensionPixelOffset;
        Canvas canvas3 = canvas;
        float f = (float) i;
        float f2 = (float) i2;
        Paint paint2 = paint;
        canvas3.drawRect(f, (float) dimensionPixelOffset, f2, (float) i4, paint2);
        int dimensionPixelOffset2 = i4 + getResources().getDimensionPixelOffset(C0690R.dimen.wave_time_text_height);
        canvas3.drawRect(f, (float) dimensionPixelOffset2, f2, (float) ((this.mVolumeMultiply / 2) + dimensionPixelOffset2), paint2);
    }

    private float getAmplitudeSizes(float f) {
        int i = this.mVolumeMultiply;
        int i2 = this.mRecordMode;
        if (i2 == 2 || i2 == 4) {
            i = this.mHalfVolumeMultiply;
        }
        if (f < 0.0f) {
            f = 0.0f;
        }
        float f2 = 4.0f;
        float f3 = ((f + 1.0f) / 15000.0f) * ((float) i);
        if (f3 >= 4.0f) {
            int i3 = this.mWaveReduce;
            f2 = f3 > ((float) (i - i3)) ? (float) (i - i3) : f3;
        }
        return f2 / 2.0f;
    }

    private String getStringBySecond(int i) {
        int i2 = i / 3600;
        int i3 = (i / 60) % 60;
        int i4 = i % 60;
        if (i2 > 10) {
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4)});
        } else if (i2 > 0) {
            return String.format(Locale.getDefault(), "%01d:%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4)});
        } else if (i >= 0) {
            return String.format(Locale.getDefault(), "%02d:%02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(i4)});
        } else {
            return String.format(Locale.getDefault(), "-%02d:%02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(-i4)});
        }
    }

    public void clearBookmarks() {
        this.mBookmarks.clear();
    }
}
