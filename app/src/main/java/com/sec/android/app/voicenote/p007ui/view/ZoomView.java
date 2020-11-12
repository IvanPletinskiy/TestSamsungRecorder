package com.sec.android.app.voicenote.p007ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.core.internal.view.SupportMenu;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.TypefaceProvider;
import com.sec.android.app.voicenote.provider.WaveProvider;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.helper.Bookmark;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

/* renamed from: com.sec.android.app.voicenote.ui.view.ZoomView */
public class ZoomView extends View {
    private static final int HOUR = 3600;
    private static final int MAX_AMPLITUDE = 15000;
    private static final int MIN = 60;
    private static final float MS_PER_WAVE = 70.0f;
    private static final int PAINT_DIM = 1;
    private static final int PAINT_NORMAL = 0;
    private static final int PAINT_OVERWRITE = 2;
    private static final int PAINT_TRANSLATE = 8;
    private static final int RECOVER_THRESHOLD = 500;
    private static final String TAG = "ZoomView";
    private static final int[] TIME_INTERVAL = {2, 5, 10, 30, 60, 120, 300, 600, 900, 1800, HOUR, 7200, 7200};
    private static final int VOLUME_THRESHOLD = 5000;
    private float MAX_WAVE_SIZE = 180.0f;
    private float MS_PER_PX;
    private float PX_PER_MS;
    private int[] mAmplitudeData;
    private Bitmap mBookmarkBitmap = null;
    private int mBookmarkBitmapSize;
    private Drawable mBookmarkDrawable = null;
    private ArrayList<Bookmark> mBookmarks = null;
    private float mDuration = 0.0f;
    private float mEndMinTime = 0.0f;
    private float mEndTime = 0.0f;
    private int mHalfVolumeMultiply;
    private boolean mIsZooming = false;
    private float[] mLongLineEndY = new float[5];
    private float[] mLongLineStartY = new float[5];
    private float mNumOfWave = 1.0f;
    private Paint[] mPaint_amplitude1 = new Paint[10];
    private Paint[] mPaint_bookmark = new Paint[2];
    private Paint mPaint_selected_region = null;
    private Paint[] mPaint_timeLongLine = new Paint[2];
    private Paint[] mPaint_timeShortLine = new Paint[2];
    private Paint mPaint_timeTextConvert = null;
    private Paint mPaint_timeTextEdit = null;
    private TextView mPercent;
//    private Handler mPercentHandler = new EventHandler();
    private Handler mPercentHandler = new EventHandler(this);
    private PopupWindow mPercentPopup;
    private int mRecordMode = 1;
    private float[] mShortLineEndY = new float[5];
    private float[] mShortLineStartY = new float[5];
    private float mStartMinTime = 0.0f;
    private float mStartTime = 0.0f;
    private float mTotalWidth;
    private int mViewMargin;
    private int mVolumeMultiply;
    private int mWaveReduce;
    private float mY_timeText;
    private float mY_waveDown;
    private float mY_waveUp;
    private float mZoomDuration = 1.0f;
    private float mZoomEndTime = 0.0f;
    private int mZoomRatio = 100;
    private float mZoomStartTime = 0.0f;

    private static final int[] DURATION_INTERVAL = {15, 40, 80, 180, 360, 900, 1800, HOUR, 7200, 14400, 14400, 43200, 86400};

    private int getAlphaInt(float f) {
        return (int) (f * 255.0f);
    }

    public ZoomView(Context context) {
        super(context);
        init();
    }

    public ZoomView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public ZoomView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        Paint[] paintArr;
        Paint[] paintArr2;
        this.mViewMargin = getResources().getDimensionPixelOffset(C0690R.dimen.wave_edit_shrink_view_margin);
        this.mWaveReduce = getResources().getDimensionPixelOffset(C0690R.dimen.wave_view_column_height_reduce);
        updateZoomViewSize();
        int i = 0;
        while (true) {
            paintArr = this.mPaint_amplitude1;
            if (i >= paintArr.length) {
                break;
            }
            paintArr[i] = new Paint();
            i++;
        }
        paintArr[0].setColor(getResources().getColor(C0690R.C0691color.wave_normal, (Resources.Theme) null));
        this.mPaint_amplitude1[0].setStrokeCap(Paint.Cap.ROUND);
        this.mPaint_amplitude1[0].setStyle(Paint.Style.FILL);
        this.mPaint_amplitude1[0].setStrokeWidth((float) WaveProvider.AMPLITUDE_STROKE_WIDTH);
        this.mPaint_amplitude1[0].setAlpha(getAlphaInt(1.0f));
        Paint[] paintArr3 = this.mPaint_amplitude1;
        paintArr3[1].set(paintArr3[0]);
        this.mPaint_amplitude1[1].setAlpha(getAlphaInt(0.1f));
        Paint[] paintArr4 = this.mPaint_amplitude1;
        paintArr4[2].set(paintArr4[0]);
        this.mPaint_amplitude1[2].setColor(getResources().getColor(C0690R.C0691color.wave_overwrite, (Resources.Theme) null));
        Paint[] paintArr5 = this.mPaint_amplitude1;
        paintArr5[3].set(paintArr5[2]);
        this.mPaint_amplitude1[3].setAlpha(getAlphaInt(0.1f));
        Paint[] paintArr6 = this.mPaint_amplitude1;
        paintArr6[8].set(paintArr6[0]);
        this.mPaint_amplitude1[8].setColor(getResources().getColor(C0690R.C0691color.wave_translate, (Resources.Theme) null));
        Paint[] paintArr7 = this.mPaint_amplitude1;
        paintArr7[9].set(paintArr7[8]);
        this.mPaint_amplitude1[9].setAlpha(getAlphaInt(0.1f));
        this.mPaint_timeTextEdit = new Paint();
        this.mPaint_timeTextEdit.setColor(getResources().getColor(C0690R.C0691color.wave_time_text, (Resources.Theme) null));
        this.mPaint_timeTextEdit.setTextSize((float) getResources().getDimensionPixelSize(C0690R.dimen.wave_time_text_size));
        this.mPaint_timeTextEdit.setStrokeWidth(1.0f);
        this.mPaint_timeTextEdit.setAlpha(getAlphaInt(0.2f));
        this.mPaint_timeTextEdit.setTextAlign(Paint.Align.CENTER);
        this.mPaint_timeTextEdit.setTypeface(TypefaceProvider.getRobotoCondensedRegularFont());
        this.mPaint_timeTextEdit.setAntiAlias(true);
        this.mPaint_timeTextConvert = new Paint();
        this.mPaint_timeTextConvert.setColor(getResources().getColor(C0690R.C0691color.wave_time_text, (Resources.Theme) null));
        this.mPaint_timeTextConvert.setTextSize((float) getResources().getDimensionPixelSize(C0690R.dimen.wave_time_text_size));
        this.mPaint_timeTextConvert.setStrokeWidth(1.0f);
        this.mPaint_timeTextConvert.setAlpha(getAlphaInt(0.8f));
        this.mPaint_timeTextConvert.setTextAlign(Paint.Align.CENTER);
        this.mPaint_timeTextConvert.setTypeface(TypefaceProvider.getRobotoCondensedRegularFont());
        this.mPaint_timeTextConvert.setAntiAlias(true);
        int i2 = 0;
        while (true) {
            paintArr2 = this.mPaint_bookmark;
            if (i2 >= paintArr2.length) {
                break;
            }
            paintArr2[i2] = new Paint();
            i2++;
        }
        paintArr2[0].setColor(getResources().getColor(C0690R.C0691color.recording_time_bookmark, (Resources.Theme) null));
        this.mPaint_bookmark[0].setStyle(Paint.Style.STROKE);
        this.mPaint_bookmark[0].setStrokeWidth((float) getResources().getDimensionPixelOffset(C0690R.dimen.wave_bookmark_width));
        Paint[] paintArr8 = this.mPaint_bookmark;
        paintArr8[1].set(paintArr8[0]);
        this.mPaint_bookmark[1].setAlpha(getAlphaInt(0.15f));
        this.mBookmarkDrawable = getResources().getDrawable(C0690R.C0692drawable.ic_voice_rec_ic_bookmark, (Resources.Theme) null);
//        SemPathRenderingDrawable semPathRenderingDrawable = this.mBookmarkDrawable;
//        if (semPathRenderingDrawable instanceof SemPathRenderingDrawable) {
//            this.mBookmarkBitmap = semPathRenderingDrawable.getBitmap();
//        } else if (semPathRenderingDrawable instanceof VectorDrawable) {
//            this.mBookmarkBitmapSize = getResources().getDimensionPixelSize(C0690R.dimen.bookmark_image_width_height);
//            this.mBookmarkBitmap = getBitmapFromVectorDrawable(this.mBookmarkDrawable);
//        } else {
//            this.mBookmarkBitmap = BitmapFactory.decodeResource(getResources(), C0690R.C0692drawable.ic_voice_rec_ic_bookmark);
//        }
        int i3 = 0;
        while (true) {
            Paint[] paintArr9 = this.mPaint_timeLongLine;
            if (i3 < paintArr9.length) {
                paintArr9[i3] = new Paint();
                this.mPaint_timeShortLine[i3] = new Paint();
                i3++;
            } else {
                paintArr9[0].setColor(getResources().getColor(C0690R.C0691color.wave_time_long_line, (Resources.Theme) null));
                this.mPaint_timeLongLine[0].setStyle(Paint.Style.STROKE);
                this.mPaint_timeLongLine[0].setStrokeWidth(1.0f);
                this.mPaint_timeShortLine[0].setColor(getResources().getColor(C0690R.C0691color.wave_time_short_line, (Resources.Theme) null));
                this.mPaint_timeShortLine[0].setStyle(Paint.Style.STROKE);
                this.mPaint_timeShortLine[0].setStrokeWidth(1.0f);
                Paint[] paintArr10 = this.mPaint_timeLongLine;
                paintArr10[1].set(paintArr10[0]);
                Paint[] paintArr11 = this.mPaint_timeShortLine;
                paintArr11[1].set(paintArr11[0]);
                this.mPaint_timeLongLine[1].setAlpha(getAlphaInt(0.15f));
                this.mPaint_timeShortLine[1].setAlpha(getAlphaInt(0.05f));
                this.mPaint_selected_region = new Paint();
                this.mPaint_selected_region.setColor(getResources().getColor(C0690R.C0691color.wave_selected_regine, (Resources.Theme) null));
                this.mPaint_selected_region.setStrokeCap(Paint.Cap.SQUARE);
                this.mPaint_selected_region.setStyle(Paint.Style.FILL);
                this.mLongLineStartY[1] = (float) (getResources().getDimensionPixelSize(C0690R.dimen.wave_time_text_height) + getResources().getDimensionPixelSize(C0690R.dimen.wave_bookmark_top_margin));
                this.mLongLineEndY[1] = this.mLongLineStartY[1] + getResources().getDimension(C0690R.dimen.wave_time_standard_long_line_height);
                float[] fArr = this.mShortLineStartY;
                fArr[1] = this.mLongLineStartY[1];
                this.mShortLineEndY[1] = fArr[1] + getResources().getDimension(C0690R.dimen.wave_time_standard_short_line_height);
                int dimensionPixelSize = getResources().getDimensionPixelSize(C0690R.dimen.wave_time_text_height) + (WaveProvider.WAVE_VIEW_HEIGHT / 2);
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
                return;
            }
        }
    }

    private Bitmap getBitmapFromVectorDrawable(Drawable drawable) {
        Bitmap createBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        int i = this.mBookmarkBitmapSize;
        drawable.setBounds(0, 0, i, i);
        drawable.draw(canvas);
        return createBitmap;
    }

    private void updateZoomViewSize() {
        int i = WaveProvider.WAVE_VIEW_HEIGHT;
        this.mVolumeMultiply = i;
        this.mHalfVolumeMultiply = i / 2;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int subSampling;
        Canvas canvas2 = canvas;
        super.onDraw(canvas);
        int trimStartTime = Engine.getInstance().getTrimStartTime();
        int trimEndTime = Engine.getInstance().getTrimEndTime();
        int overwriteStartTime = Engine.getInstance().getOverwriteStartTime();
        int overwriteEndTime = Engine.getInstance().getOverwriteEndTime();
        float f = this.mEndTime;
        int i7 = (int) (f - this.mStartTime);
        if (f == 0.0f) {
            i = 0;
        } else {
            float f2 = (float) i7;
            i = this.mDuration > f2 ? Math.round(f / 1000.0f) : (int) (f2 / 1000.0f);
        }
        int timeInterval = getTimeInterval((int) (((float) i7) / 1000.0f));
        for (int i8 = 0; i8 <= i; i8 += timeInterval) {
            int i9 = i8 * 1000;
            float f3 = this.mStartTime;
            float f4 = this.PX_PER_MS;
            float f5 = (((float) i9) - f3) * f4;
            if (f5 < ((float) WaveProvider.WAVE_AREA_WIDTH) * 0.9f || f5 < (this.mDuration - f3) * f4) {
                int scene = VoiceNoteApplication.getScene();
                if (scene == 6) {
                    canvas2.drawText(getStringBySecond(i8), f5, this.mY_timeText, this.mPaint_timeTextEdit);
                } else if (scene != 12) {
                    canvas2.drawText(getStringBySecond(i8), f5, this.mY_timeText, this.mPaint_timeTextEdit);
                } else {
                    canvas2.drawText(getStringBySecond(i8), f5, this.mY_timeText, this.mPaint_timeTextConvert);
                }
            }
            drawTimeLine(canvas2, f5, i9, timeInterval);
        }
        updateZoomViewSize();
        this.mNumOfWave = ((this.mEndTime - this.mStartTime) / MS_PER_WAVE) / this.MAX_WAVE_SIZE;
        if (this.mNumOfWave < 1.0f) {
            this.mNumOfWave = 1.0f;
        }
        float f6 = this.mStartTime;
        float f7 = f6 / MS_PER_WAVE;
        float f8 = this.mNumOfWave;
        int i10 = (int) (f7 / f8);
        int i11 = (int) ((f6 - ((((float) i10) * MS_PER_WAVE) * f8)) * this.PX_PER_MS);
        int i12 = 1;
        int round = Engine.getInstance().getTranslationState() != 1 ? Math.round(((((float) this.mAmplitudeData.length) / ((float) Engine.getInstance().getDuration())) * ((float) Engine.getInstance().getTrimStartTime())) / this.mNumOfWave) : 0;
        int i13 = 0;
        while (((float) i13) < this.MAX_WAVE_SIZE) {
            float f9 = (float) ((WaveProvider.AMPLITUDE_TOTAL_WIDTH * i13) - i11);
            int i14 = (int) (this.mStartTime + (this.MS_PER_PX * f9));
            int currentTime = Engine.getInstance().getCurrentTime();
            if (VoiceNoteApplication.getScene() != 12) {
                int i15 = (overwriteStartTime == -1 || (i14 < overwriteStartTime && ((float) i14) + MS_PER_WAVE < ((float) overwriteStartTime)) || overwriteEndTime == -1 || i14 > overwriteEndTime) ? 0 : 2;
                if ((trimStartTime != -1 && i14 < trimStartTime) || (trimEndTime != -1 && i14 > trimEndTime)) {
                    i15++;
                }
                i2 = i15;
                i3 = 1;
            } else {
                i3 = 1;
                i2 = (Engine.getInstance().getTranslationState() == 1 || i14 > currentTime - trimStartTime) ? 0 : 8;
            }
            int i16 = i10 + i13;
            if (i16 >= 0 && (subSampling = getSubSampling(i16 + round, this.mNumOfWave)) >= 0) {
                int i17 = this.mRecordMode;
                if (i17 == 2) {
                    i6 = i13;
                    i5 = i3;
                    i4 = i11;
                    float f10 = f9;
                    int i18 = i2;
                    drawOneAmplitude(canvas, f10, this.mY_waveUp, getAmplitudeSizes((float) (subSampling >> 16), this.mHalfVolumeMultiply), i18);
                    drawOneAmplitude(canvas, f10, this.mY_waveDown, getAmplitudeSizes((float) (subSampling & SupportMenu.USER_MASK), this.mHalfVolumeMultiply), i18);
                } else if (i17 != 4) {
                    i6 = i13;
                    i5 = 1;
                    i4 = i11;
                    drawOneAmplitude(canvas, f9, this.mY_waveDown, getAmplitudeSizes((float) (subSampling & SupportMenu.USER_MASK), this.mVolumeMultiply), i2);
                } else {
                    i6 = i13;
                    i5 = i3;
                    i4 = i11;
                    drawOneAmplitude(canvas, f9, this.mY_waveDown, getAmplitudeSizes((float) (subSampling & SupportMenu.USER_MASK), this.mHalfVolumeMultiply), i2);
                }
            } else {
                i6 = i13;
                i5 = i3;
                i4 = i11;
            }
            i13 = i6 + 1;
            i12 = i5;
            i11 = i4;
        }
        int i19 = i12;
        if (VoiceNoteApplication.getScene() == 12) {
            int size = this.mBookmarks.size();
            for (int i20 = 0; i20 < size; i20++) {
                int elapsed = this.mBookmarks.get(i20).getElapsed();
                if (trimStartTime == -1 || elapsed >= trimStartTime) {
                    float f11 = (float) elapsed;
                    float f12 = this.mStartTime;
                    float f13 = this.PX_PER_MS;
                    float f14 = (f11 - f12) * f13;
                    if (trimStartTime != -1) {
                        f14 = ((f11 - f12) - ((float) trimStartTime)) * f13;
                    }
                    drawBookmark(canvas2, f14, 0);
                }
            }
        } else {
            int size2 = this.mBookmarks.size();
            for (int i21 = 0; i21 < size2; i21++) {
                int elapsed2 = this.mBookmarks.get(i21).getElapsed();
                int i22 = ((trimStartTime == -1 || elapsed2 >= trimStartTime) && (trimEndTime == -1 || elapsed2 <= trimEndTime)) ? 0 : i19;
                float f15 = (float) elapsed2;
                float f16 = this.mStartTime;
                if (f15 > f16 && f15 < this.mEndMinTime) {
                    drawBookmark(canvas2, (f15 - f16) * this.PX_PER_MS, i22);
                }
            }
        }
        if (VoiceNoteApplication.getScene() != 12 && Engine.getInstance().getRecorderState() != 2) {
            float f17 = this.mStartTime;
            float f18 = this.PX_PER_MS;
            float f19 = (((float) trimStartTime) - f17) * f18;
            float f20 = (((float) trimEndTime) - f17) * f18;
            if (f19 <= ((float) WaveProvider.WAVE_AREA_WIDTH) && f20 >= 0.0f) {
                drawRegion(canvas2, f19, f20, this.mPaint_selected_region);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /* JADX WARNING: Removed duplicated region for block: B:56:0x01fd  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x020d  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0210  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void startZoom(boolean r20) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "startZoom - invalidate : "
            r2.append(r3)
            r2.append(r1)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "ZoomView"
            com.sec.android.app.voicenote.provider.Log.m26i(r3, r2)
            com.sec.android.app.voicenote.service.MetadataRepository r2 = com.sec.android.app.voicenote.service.MetadataRepository.getInstance()
            com.sec.android.app.voicenote.service.Engine r4 = com.sec.android.app.voicenote.service.Engine.getInstance()
            int r4 = r4.getDuration()
            float r4 = (float) r4
            r0.mDuration = r4
            int r4 = r2.getRecordMode()
            r0.mRecordMode = r4
            java.util.ArrayList r4 = r2.getBookmarkList()
            r0.mBookmarks = r4
            int r4 = r0.mRecordMode
            r0.setRecordMode(r4)
            java.lang.String r4 = r2.getPath()
            if (r4 == 0) goto L_0x006c
            java.lang.String r5 = ".m4a"
            boolean r5 = r4.endsWith(r5)
            if (r5 != 0) goto L_0x0058
            java.lang.String r5 = ".amr"
            boolean r5 = r4.endsWith(r5)
            if (r5 != 0) goto L_0x0058
            java.lang.String r5 = ".3ga"
            boolean r4 = r4.endsWith(r5)
            if (r4 == 0) goto L_0x006c
        L_0x0058:
            boolean r4 = r2.isWaveMakerWorking()
            if (r4 == 0) goto L_0x006c
            com.sec.android.app.voicenote.ui.view.-$$Lambda$ZoomView$eWYhDaUJ78trBv0a4bcl-Wh0zd4 r4 = new com.sec.android.app.voicenote.ui.view.-$$Lambda$ZoomView$eWYhDaUJ78trBv0a4bcl-Wh0zd4
            r4.<init>(r1)
            r2.registerListener(r4)
            java.lang.String r1 = "startZoom - WAIT for waveMaker"
            com.sec.android.app.voicenote.provider.Log.m32w((java.lang.String) r3, (java.lang.String) r1)
            return
        L_0x006c:
            com.sec.android.app.voicenote.service.Engine r4 = com.sec.android.app.voicenote.service.Engine.getInstance()
            int r4 = r4.getContentItemCount()
            r5 = 0
            r6 = 1
            if (r4 <= r6) goto L_0x009f
            com.sec.android.app.voicenote.service.Engine r4 = com.sec.android.app.voicenote.service.Engine.getInstance()
            com.sec.android.app.voicenote.service.ContentItem r4 = r4.peekContentItem()
            if (r4 == 0) goto L_0x0097
            int r4 = r4.getStartTime()
            com.sec.android.app.voicenote.service.Engine r7 = com.sec.android.app.voicenote.service.Engine.getInstance()
            int r7 = r7.getDuration()
            int[] r2 = r2.getOverWriteWaveData(r4, r7)
            if (r2 != 0) goto L_0x0095
            goto L_0x0098
        L_0x0095:
            int r4 = r2.length
            goto L_0x0099
        L_0x0097:
            r2 = 0
        L_0x0098:
            r4 = r5
        L_0x0099:
            r18 = r4
            r4 = r2
            r2 = r18
            goto L_0x00b7
        L_0x009f:
            int[] r4 = r2.getWaveData()
            int r7 = r2.getWaveDataSize()
            int r8 = r2.getAmplitudeCollectorSize()
            if (r7 >= r8) goto L_0x00b6
            int[] r4 = r2.getAmplitudeCollector()
            int r2 = r2.getAmplitudeCollectorSize()
            goto L_0x00b7
        L_0x00b6:
            r2 = r7
        L_0x00b7:
            if (r4 == 0) goto L_0x0230
            int r7 = r4.length
            if (r7 != 0) goto L_0x00be
            goto L_0x0230
        L_0x00be:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "startZoom - version : "
            r7.append(r8)
            r8 = r4[r5]
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r3, r7)
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "startZoom - duration : "
            r7.append(r8)
            float r8 = r0.mDuration
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r3, r7)
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "startZoom - wave size : "
            r7.append(r8)
            r7.append(r2)
            java.lang.String r2 = r7.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r3, r2)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r7 = "startZoom - recordMode : "
            r2.append(r7)
            int r7 = r0.mRecordMode
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r3, r2)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r7 = "startZoom - mBookmarks size : "
            r2.append(r7)
            java.util.ArrayList<com.sec.android.app.voicenote.service.helper.Bookmark> r7 = r0.mBookmarks
            int r7 = r7.size()
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r3, r2)
            float r2 = r0.mDuration
            r7 = 1108082688(0x420c0000, float:35.0)
            float r2 = r2 / r7
            r7 = 1073741824(0x40000000, float:2.0)
            float r2 = r2 / r7
            double r7 = (double) r2
            double r7 = java.lang.Math.ceil(r7)
            int r2 = (int) r7
            int[] r7 = new int[r2]
            r0.mAmplitudeData = r7
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "startZoom - new wave size : "
            r7.append(r8)
            int[] r8 = r0.mAmplitudeData
            int r8 = r8.length
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r3, r7)
            r3 = r5
            r7 = r3
        L_0x0159:
            if (r5 >= r2) goto L_0x022a
            int r8 = r5 * 2
            int r9 = r8 + 1
            int r10 = r4.length
            int r10 = r10 - r6
            if (r8 <= r10) goto L_0x0165
            int r8 = r4.length
            int r8 = r8 - r6
        L_0x0165:
            int r10 = r4.length
            int r10 = r10 - r6
            if (r9 <= r10) goto L_0x016b
            int r9 = r4.length
            int r9 = r9 - r6
        L_0x016b:
            r10 = r4[r8]
            int r10 = r10 >> 16
            r11 = r4[r9]
            int r11 = r11 >> 16
            int r10 = r10 + r11
            r11 = 2
            int r10 = r10 / r11
            r8 = r4[r8]
            r12 = 65535(0xffff, float:9.1834E-41)
            r8 = r8 & r12
            r9 = r4[r9]
            r9 = r9 & r12
            int r8 = r8 + r9
            int r8 = r8 / r11
            int r9 = r0.mRecordMode
            if (r9 != r11) goto L_0x0190
            double r9 = (double) r10
            r11 = 4605380978949069210(0x3fe999999999999a, double:0.8)
            double r9 = r9 * r11
            int r10 = (int) r9
            double r8 = (double) r8
            double r8 = r8 * r11
            int r8 = (int) r8
        L_0x0190:
            r11 = 4665518107723300864(0x40bf400000000000, double:8000.0)
            r9 = 1065353216(0x3f800000, float:1.0)
            r13 = 1153138688(0x44bb8000, float:1500.0)
            r15 = r7
            r6 = 4611686018427387904(0x4000000000000000, double:2.0)
            r14 = 5000(0x1388, float:7.006E-42)
            if (r10 >= r14) goto L_0x01ab
            float r10 = (float) r10
            float r10 = r10 / r13
            float r10 = r10 + r9
            double r9 = (double) r10
            double r9 = java.lang.Math.log10(r9)
            double r9 = r9 * r11
            goto L_0x01b7
        L_0x01ab:
            double r9 = (double) r10
            double r9 = java.lang.Math.pow(r9, r6)
            r16 = 4669471951536783360(0x40cd4c0000000000, double:15000.0)
            double r9 = r9 / r16
        L_0x01b7:
            int r9 = (int) r9
            if (r8 >= r14) goto L_0x01c6
            float r6 = (float) r8
            float r6 = r6 / r13
            r7 = 1065353216(0x3f800000, float:1.0)
            float r6 = r6 + r7
            double r6 = (double) r6
            double r6 = java.lang.Math.log10(r6)
            double r6 = r6 * r11
            goto L_0x01d1
        L_0x01c6:
            double r10 = (double) r8
            double r6 = java.lang.Math.pow(r10, r6)
            r10 = 4669471951536783360(0x40cd4c0000000000, double:15000.0)
            double r6 = r6 / r10
        L_0x01d1:
            int r6 = (int) r6
            int r7 = r3 - r9
            int r8 = r15 - r6
            r10 = 500(0x1f4, float:7.0E-43)
            r11 = 10000(0x2710, float:1.4013E-41)
            if (r9 <= r11) goto L_0x01eb
            if (r3 <= r11) goto L_0x01eb
            java.util.Random r3 = new java.util.Random
            r3.<init>()
            int r7 = r9 / 2
            int r3 = r3.nextInt(r7)
            int r9 = r9 - r3
            goto L_0x01fa
        L_0x01eb:
            if (r7 <= r10) goto L_0x01fa
            java.util.Random r9 = new java.util.Random
            r9.<init>()
            int r7 = r7 / 2
            int r7 = r9.nextInt(r7)
            int r3 = r3 - r7
            goto L_0x01fb
        L_0x01fa:
            r3 = r9
        L_0x01fb:
            if (r6 <= r11) goto L_0x020d
            r7 = r15
            if (r7 <= r11) goto L_0x020e
            java.util.Random r7 = new java.util.Random
            r7.<init>()
            int r8 = r6 / 2
            int r7 = r7.nextInt(r8)
            int r6 = r6 - r7
            goto L_0x021d
        L_0x020d:
            r7 = r15
        L_0x020e:
            if (r8 <= r10) goto L_0x021d
            java.util.Random r6 = new java.util.Random
            r6.<init>()
            int r8 = r8 / 2
            int r6 = r6.nextInt(r8)
            int r7 = r7 - r6
            goto L_0x021e
        L_0x021d:
            r7 = r6
        L_0x021e:
            int[] r6 = r0.mAmplitudeData
            int r8 = r3 << 16
            int r8 = r8 + r7
            r6[r5] = r8
            int r5 = r5 + 1
            r6 = 1
            goto L_0x0159
        L_0x022a:
            r19.changeLengthOfTime()
            r19.invalidateZoomView(r20)
        L_0x0230:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.view.ZoomView.startZoom(boolean):void");
    }

    public /* synthetic */ void lambda$startZoom$0$ZoomView(boolean z, int i, int i2) {
        Log.m32w(TAG, "startZoom - onWaveMakerFinished");
        startZoom(z);
    }

    public void changeLengthOfTime() {
        Log.m26i(TAG, "changeLengthOfTime");
        int trimStartTime = Engine.getInstance().getTrimStartTime();
        if (VoiceNoteApplication.getScene() != 12 || trimStartTime <= 0) {
            this.mDuration = (float) Engine.getInstance().getDuration();
        } else {
            this.mDuration = (float) (Engine.getInstance().getDuration() - trimStartTime);
        }
        int i = ((int) WaveProvider.DURATION_PER_HAFT_OF_WAVE_AREA) * 2;
        float f = this.mDuration;
        float f2 = (float) i;
        int i2 = (int) (((float) this.mViewMargin) * (f > f2 ? f / ((float) WaveProvider.WAVE_AREA_WIDTH) : f2 / ((float) WaveProvider.WAVE_AREA_WIDTH)));
        float f3 = (float) (-i2);
        this.mZoomStartTime = f3;
        this.mStartMinTime = f3;
        this.mStartTime = f3;
        float f4 = this.mDuration;
        float f5 = (float) (i - i2);
        if (((float) i2) + f4 > f5) {
            f5 = (float) (((int) f4) + i2);
        }
        this.mZoomEndTime = f5;
        this.mEndMinTime = f5;
        this.mEndTime = f5;
        this.mZoomDuration = this.mEndTime - this.mStartTime;
        Log.m26i(TAG, "changeLengthOfTime : " + this.mStartTime + " ~ " + this.mEndTime + " marginTime : " + i2);
        computeUnits(this.mZoomDuration);
    }

    public void setZoomScale(float f, boolean z) {
        float f2;
        float f3 = this.mZoomDuration;
        float f4 = (f3 - (f3 / f)) / 2.0f;
        float f5 = this.mZoomStartTime + f4;
        float f6 = this.mZoomEndTime - f4;
        this.mNumOfWave = ((f6 - f5) / MS_PER_WAVE) / this.MAX_WAVE_SIZE;
        this.mIsZooming = true;
        if (this.mNumOfWave < 1.0f) {
            Log.m22e(TAG, "setZoomScale mNumOfWave is UNDER 1 : " + this.mNumOfWave);
            this.mNumOfWave = 1.0f;
            float f7 = this.mZoomEndTime;
            float f8 = this.mZoomStartTime;
            f2 = ((f7 - f8) - (this.MAX_WAVE_SIZE * MS_PER_WAVE)) / 2.0f;
            this.mStartTime = f8 + f2;
            this.mEndTime = f7 - f2;
        } else {
            float f9 = this.mStartMinTime;
            if (f5 < f9) {
                this.mStartTime = f9;
                Log.m22e(TAG, "setZoomScale mStartTime is mStartMinTime");
            } else {
                this.mStartTime = f5;
            }
            float f10 = this.mEndMinTime;
            if (f6 > f10) {
                this.mEndTime = f10;
                Log.m22e(TAG, "setZoomScale mEndTime is mEndMinTime");
            } else {
                this.mEndTime = f6;
            }
            f2 = f4;
        }
        Log.m26i(TAG, "setZoomScale dx:" + f2 + " " + this.mStartTime + "~" + this.mEndTime);
        float f11 = this.mEndTime;
        float f12 = this.mStartTime;
        this.mNumOfWave = ((f11 - f12) / MS_PER_WAVE) / this.MAX_WAVE_SIZE;
        computeUnits(f11 - f12);
        showPercentPopup();
        invalidateZoomView(z);
    }

    public float setZoomEnd() {
        this.mIsZooming = false;
        this.mZoomStartTime = this.mStartTime;
        this.mZoomEndTime = this.mEndTime;
        this.mZoomDuration = this.mZoomEndTime - this.mZoomStartTime;
        Log.m26i(TAG, "setZoomEnd - mZoomStartTime:" + this.mZoomStartTime + " mZoomEndTime:" + this.mZoomEndTime);
        return (this.mZoomStartTime * this.PX_PER_MS) + ((float) this.mViewMargin);
    }

    public void scrollTo(int i, boolean z) {
        if (!this.mIsZooming) {
            float f = ((float) (i - this.mViewMargin)) * this.MS_PER_PX;
            float f2 = this.mZoomDuration + f;
            if (f < this.mStartMinTime || f2 > this.mEndMinTime) {
                Log.m32w(TAG, "scrollTo - SKIP SCROLL");
                return;
            }
            Log.m19d(TAG, "scrollTo - scroll:" + i + " startTime:" + f + " endTime:" + f2);
            this.mStartTime = f;
            this.mZoomStartTime = f;
            this.mEndTime = f2;
            this.mZoomEndTime = f2;
            invalidateZoomView(z);
        }
    }

    private void drawOneAmplitude(Canvas canvas, float f, float f2, float[] fArr, int i) {
        float f3 = f + (((float) WaveProvider.AMPLITUDE_STROKE_WIDTH) / 2.0f);
        if (f2 >= 0.0f && fArr[1] >= 0.0f) {
            canvas.drawLine(f3, f2 - fArr[1], f3, f2 + fArr[1], this.mPaint_amplitude1[i]);
        }
    }

    private void drawTimeLine(Canvas canvas, float f, int i, int i2) {
        int i3 = i2 * ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION;
        float f2 = ((float) i3) * this.PX_PER_MS;
        float f3 = f + f2;
        float f4 = f3 + f2;
        float f5 = f2 + f4;
        float dimension = getResources().getDimension(C0690R.dimen.wave_time_text_height);
        float dimension2 = getResources().getDimension(C0690R.dimen.wave_time_standard_long_line_height);
        float dimension3 = getResources().getDimension(C0690R.dimen.wave_time_standard_short_line_height);
        int trimStartTime = Engine.getInstance().getTrimStartTime();
        int trimEndTime = Engine.getInstance().getTrimEndTime();
        int[] iArr = new int[4];
        for (int i4 = 0; i4 < iArr.length; i4++) {
            int i5 = i + (i3 * i4);
            if (VoiceNoteApplication.getScene() == 12) {
                i5 += trimStartTime;
            }
            iArr[i4] = 0;
            if ((trimStartTime != -1 && i5 < trimStartTime) || (trimEndTime != -1 && i5 > trimEndTime)) {
                iArr[i4] = iArr[i4] + 1;
            }
        }
        int i6 = this.mRecordMode;
        if (i6 == 2) {
            canvas.drawLine(f, this.mLongLineStartY[i6], f, this.mLongLineEndY[i6], this.mPaint_timeLongLine[iArr[0]]);
            float[] fArr = this.mShortLineStartY;
            int i7 = this.mRecordMode;
            canvas.drawLine(f3, fArr[i7], f3, this.mShortLineEndY[i7], this.mPaint_timeShortLine[iArr[1]]);
            float[] fArr2 = this.mShortLineStartY;
            int i8 = this.mRecordMode;
            canvas.drawLine(f4, fArr2[i8], f4, this.mShortLineEndY[i8], this.mPaint_timeShortLine[iArr[2]]);
            float[] fArr3 = this.mShortLineStartY;
            int i9 = this.mRecordMode;
            canvas.drawLine(f5, fArr3[i9], f5, this.mShortLineEndY[i9], this.mPaint_timeShortLine[iArr[3]]);
            float[] fArr4 = this.mLongLineStartY;
            int i10 = this.mRecordMode;
            canvas.drawLine(f, fArr4[i10] + dimension + dimension2, f, this.mLongLineEndY[i10] + dimension + dimension2, this.mPaint_timeLongLine[iArr[0]]);
            float[] fArr5 = this.mShortLineStartY;
            int i11 = this.mRecordMode;
            canvas.drawLine(f3, fArr5[i11] + dimension + dimension3, f3, this.mShortLineEndY[i11] + dimension + dimension3, this.mPaint_timeShortLine[iArr[1]]);
            float[] fArr6 = this.mShortLineStartY;
            int i12 = this.mRecordMode;
            canvas.drawLine(f4, fArr6[i12] + dimension + dimension3, f4, this.mShortLineEndY[i12] + dimension + dimension3, this.mPaint_timeShortLine[iArr[2]]);
            float[] fArr7 = this.mShortLineStartY;
            int i13 = this.mRecordMode;
            canvas.drawLine(f5, fArr7[i13] + dimension + dimension3, f5, this.mShortLineEndY[i13] + dimension + dimension3, this.mPaint_timeShortLine[iArr[3]]);
        } else if (i6 != 4) {
            canvas.drawLine(f, this.mLongLineStartY[1], f, this.mLongLineEndY[1], this.mPaint_timeLongLine[iArr[0]]);
            canvas.drawLine(f3, this.mShortLineStartY[1], f3, this.mShortLineEndY[1], this.mPaint_timeShortLine[iArr[1]]);
            canvas.drawLine(f4, this.mShortLineStartY[1], f4, this.mShortLineEndY[1], this.mPaint_timeShortLine[iArr[2]]);
            canvas.drawLine(f5, this.mShortLineStartY[1], f5, this.mShortLineEndY[1], this.mPaint_timeShortLine[iArr[3]]);
        } else {
            canvas.drawLine(f, this.mLongLineStartY[i6], f, this.mLongLineEndY[i6], this.mPaint_timeLongLine[iArr[0]]);
            float[] fArr8 = this.mShortLineStartY;
            int i14 = this.mRecordMode;
            canvas.drawLine(f3, fArr8[i14], f3, this.mShortLineEndY[i14], this.mPaint_timeShortLine[iArr[1]]);
            float[] fArr9 = this.mShortLineStartY;
            int i15 = this.mRecordMode;
            canvas.drawLine(f4, fArr9[i15], f4, this.mShortLineEndY[i15], this.mPaint_timeShortLine[iArr[2]]);
            float[] fArr10 = this.mShortLineStartY;
            int i16 = this.mRecordMode;
            canvas.drawLine(f5, fArr10[i16], f5, this.mShortLineEndY[i16], this.mPaint_timeShortLine[iArr[3]]);
        }
    }

    private void drawRegion(Canvas canvas, float f, float f2, Paint paint) {
        if (f < 0.0f) {
            f = 0.0f;
        }
        int i = WaveProvider.WAVE_AREA_WIDTH;
        if (f2 > ((float) i)) {
            f2 = (float) i;
        }
        if (this.mRecordMode != 2) {
            int dimensionPixelOffset = getResources().getDimensionPixelOffset(C0690R.dimen.wave_bookmark_top_margin) + getResources().getDimensionPixelOffset(C0690R.dimen.wave_time_text_height);
            canvas.drawRect(f, (float) dimensionPixelOffset, f2, (float) (WaveProvider.WAVE_VIEW_HEIGHT + dimensionPixelOffset), paint);
            return;
        }
        int dimensionPixelOffset2 = getResources().getDimensionPixelOffset(C0690R.dimen.wave_bookmark_top_margin);
        int i2 = dimensionPixelOffset2 + (WaveProvider.WAVE_VIEW_HEIGHT / 2);
        Canvas canvas2 = canvas;
        float f3 = f;
        float f4 = f2;
        Paint paint2 = paint;
        canvas2.drawRect(f3, (float) dimensionPixelOffset2, f4, (float) i2, paint2);
        int dimensionPixelOffset3 = i2 + getResources().getDimensionPixelOffset(C0690R.dimen.wave_time_text_height);
        canvas2.drawRect(f3, (float) dimensionPixelOffset3, f4, (float) ((WaveProvider.WAVE_VIEW_HEIGHT / 2) + dimensionPixelOffset3), paint2);
    }

    private void drawBookmark(Canvas canvas, float f, int i) {
        int i2;
        int i3;
        int i4;
        int i5;
        if (this.mRecordMode != 2) {
            i5 = getResources().getDimensionPixelOffset(C0690R.dimen.wave_bookmark_top_margin);
            i4 = WaveProvider.WAVE_HEIGHT - i5;
            i3 = -1;
            i2 = -1;
        } else {
            i5 = getResources().getDimensionPixelOffset(C0690R.dimen.wave_bookmark_top_margin);
            i3 = getResources().getDimensionPixelOffset(C0690R.dimen.wave_bookmark_top_margin) + (WaveProvider.WAVE_VIEW_HEIGHT / 2) + getResources().getDimensionPixelOffset(C0690R.dimen.wave_time_text_height);
            i2 = WaveProvider.WAVE_VIEW_HEIGHT / 2;
            i4 = WaveProvider.WAVE_VIEW_HEIGHT / 2;
        }
        if (this.mBookmarkDrawable instanceof VectorDrawable) {
            canvas.drawBitmap(this.mBookmarkBitmap, f - (((float) this.mBookmarkBitmapSize) / 2.0f), (float) getResources().getDimensionPixelOffset(C0690R.dimen.wave_bookmark_y), this.mPaint_bookmark[i]);
        } else {
            Bitmap bitmap = this.mBookmarkBitmap;
            canvas.drawBitmap(bitmap, f - (((float) bitmap.getWidth()) / 2.0f), (float) getResources().getDimensionPixelOffset(C0690R.dimen.wave_bookmark_y), this.mPaint_bookmark[i]);
        }
        canvas.drawLine(f, (float) i5, f, (float) (i4 + i5), this.mPaint_bookmark[i]);
        if (i3 != -1 && i2 != -1) {
            canvas.drawLine(f, (float) i3, f, (float) (i2 + i3), this.mPaint_bookmark[i]);
        }
    }

    private int getTimeInterval(int i) {
        int binarySearch = Arrays.binarySearch(DURATION_INTERVAL, i);
        if (binarySearch < 0) {
            binarySearch = -(binarySearch + 1);
        } else {
            int[] iArr = TIME_INTERVAL;
            if (binarySearch > iArr.length - 1) {
                binarySearch = iArr.length - 1;
            }
        }
        return TIME_INTERVAL[binarySearch];
    }

    private String getStringBySecond(int i) {
        int i2 = i / HOUR;
        int i3 = (i / 60) % 60;
        int i4 = i % 60;
        if (i2 > 10) {
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4)});
        } else if (i2 > 0) {
            return String.format(Locale.getDefault(), "%01d:%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4)});
        } else if (i >= 0) {
            return String.format(Locale.getDefault(), "%02d:%02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(i4)});
        } else {
            return String.format(Locale.getDefault(), "-%02d:%02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(i4)});
        }
    }

    public void invalidateZoomView(boolean z) {
        if (z) {
            invalidate();
        } else {
            postInvalidateOnAnimation();
        }
    }

    public void setRecordMode(int i) {
        this.mRecordMode = i;
        if (this.mRecordMode != 2) {
            this.mY_waveUp = -1.0f;
            this.mY_waveDown = (float) (getResources().getDimensionPixelOffset(C0690R.dimen.wave_time_text_height) + getResources().getDimensionPixelOffset(C0690R.dimen.wave_bookmark_top_margin) + (WaveProvider.WAVE_VIEW_HEIGHT / 2));
            this.mY_timeText = (float) ((getResources().getDimensionPixelOffset(C0690R.dimen.wave_time_text_height) + getResources().getDimensionPixelOffset(C0690R.dimen.wave_bookmark_top_margin)) - getResources().getDimensionPixelOffset(C0690R.dimen.wave_time_text_normal_margin_bottom));
            return;
        }
        this.mY_waveUp = (float) (getResources().getDimensionPixelOffset(C0690R.dimen.wave_bookmark_top_margin) + (WaveProvider.WAVE_VIEW_HEIGHT / 4));
        int dimensionPixelOffset = getResources().getDimensionPixelOffset(C0690R.dimen.wave_time_text_height) + getResources().getDimensionPixelOffset(C0690R.dimen.wave_bookmark_top_margin);
        int i2 = WaveProvider.WAVE_VIEW_HEIGHT;
        this.mY_waveDown = (float) (dimensionPixelOffset + (i2 / 2) + (i2 / 4));
        this.mY_timeText = (float) (((getResources().getDimensionPixelOffset(C0690R.dimen.wave_time_text_height) + getResources().getDimensionPixelOffset(C0690R.dimen.wave_bookmark_top_margin)) + (WaveProvider.WAVE_VIEW_HEIGHT / 2)) - getResources().getDimensionPixelOffset(C0690R.dimen.wave_time_text_normal_margin_bottom));
    }

    private int getSubSampling(int i, float f) {
        int[] iArr = this.mAmplitudeData;
        if (!(iArr == null || iArr.length == 0)) {
            int i2 = (int) (((float) i) * f);
            int i3 = (int) (f * ((float) (i + 1)));
            if (i2 >= 0 && (i2 <= iArr.length - 1 || i3 <= iArr.length - 1)) {
                int[] iArr2 = this.mAmplitudeData;
                if (i2 > iArr2.length - 1) {
                    i2 = iArr2.length - 1;
                }
                int[] iArr3 = this.mAmplitudeData;
                if (i3 > iArr3.length - 1) {
                    i3 = iArr3.length - 1;
                }
                int i4 = 0;
                int i5 = 0;
                for (int i6 = i2; i6 <= i3; i6++) {
                    int[] iArr4 = this.mAmplitudeData;
                    i4 += iArr4[i6] >> 16;
                    i5 += iArr4[i6] & SupportMenu.USER_MASK;
                }
                int i7 = (i3 - i2) + 1;
                return ((i4 / i7) << 16) + (i5 / i7);
            }
        }
        return -1;
    }

    private float[] getAmplitudeSizes(float f, int i) {
        int i2;
        int i3;
        if (f < 0.0f) {
            f = 0.0f;
        }
        float f2 = ((f + 1.0f) / 15000.0f) * ((float) i);
        if (f2 < 4.0f) {
            f2 = 4.0f;
        } else {
            int i4 = this.mWaveReduce;
            if (f2 > ((float) (i - i4))) {
                f2 = (float) (i - i4);
            }
        }
        Random random = new Random((long) f2);
        if (f2 > 10.0f) {
            int i5 = (int) (((double) f2) * 0.7d);
            i3 = random.nextInt(i5);
            i2 = random.nextInt(i5);
        } else {
            i2 = 1;
            i3 = 1;
        }
        float[] fArr = new float[WaveProvider.WAVE_THICKNESS];
        fArr[0] = (f2 - ((float) i3)) / 2.0f;
        fArr[1] = f2 / 2.0f;
        float f3 = f2 - ((float) i2);
        fArr[2] = f3 / 4.0f;
        fArr[3] = f3 / 2.0f;
        return fArr;
    }

    public int getTotalWidth() {
        return (int) this.mTotalWidth;
    }

    public float getMsPerPx() {
        return this.MS_PER_PX;
    }

    public float getPxPerMs() {
        return this.PX_PER_MS;
    }

    public float getStartTime() {
        return this.mStartTime;
    }

    public float getEndTime() {
        return this.mEndTime;
    }

    /* access modifiers changed from: protected */
    public void computeUnits(float f) {
        int i = WaveProvider.WAVE_AREA_WIDTH;
        this.PX_PER_MS = ((float) i) / f;
        this.MS_PER_PX = f / ((float) i);
        this.MAX_WAVE_SIZE = ((float) i) / ((float) WaveProvider.AMPLITUDE_TOTAL_WIDTH);
        if (this.MS_PER_PX < WaveProvider.MS_PER_PX) {
            this.PX_PER_MS = WaveProvider.PX_PER_MS;
            this.MS_PER_PX = WaveProvider.MS_PER_PX;
        }
        this.mTotalWidth = this.mDuration * this.PX_PER_MS;
        if (this.mStartTime > this.mStartMinTime || this.mEndTime < this.mEndMinTime) {
            this.mTotalWidth += (float) (this.mViewMargin * 2);
        }
        this.mZoomRatio = (int) Math.ceil((double) ((WaveProvider.MS_PER_PX / this.MS_PER_PX) * 100.0f));
        int i2 = this.mZoomRatio;
        if (i2 < 1) {
            this.mZoomRatio = 1;
        } else if (i2 > 100) {
            this.mZoomRatio = 100;
        }
        Log.m26i(TAG, "computeUnits - mZoomRatio:" + this.mZoomRatio + " PX_PER_MS:" + this.PX_PER_MS + " MS_PER_PX:" + this.MS_PER_PX + " mTotalWidth:" + this.mTotalWidth + " MAX_WAVE_SIZE:" + this.MAX_WAVE_SIZE);
    }

    @SuppressLint({"InflateParams"})
    public void showPercentPopup() {
        if (this.mPercent == null) {
            this.mPercent = (TextView) LayoutInflater.from(getContext()).inflate(C0690R.layout.edit_percent_zoomview_popup, (ViewGroup) null);
        }
        if (this.mPercentPopup == null) {
            this.mPercentPopup = new PopupWindow(this.mPercent, -2, -2);
        }
        this.mPercent.setText(String.format(Locale.getDefault(), "%d%%", new Object[]{Integer.valueOf(this.mZoomRatio)}));
        this.mPercentPopup.setContentView(this.mPercent);
        this.mPercentPopup.showAtLocation(this, 49, 0, getResources().getDimensionPixelOffset(C0690R.dimen.edit_percent_popup_y));
        this.mPercentHandler.removeMessages(0);
        this.mPercentHandler.sendEmptyMessageDelayed(0, 2000);
    }

    public void dismissPercentPopup() {
        PopupWindow popupWindow = this.mPercentPopup;
        if (popupWindow != null && popupWindow.isShowing()) {
            this.mPercentPopup.dismiss();
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.view.ZoomView$EventHandler */
    private static class EventHandler extends Handler {
        WeakReference<ZoomView> mWeakRef;

        private EventHandler(ZoomView zoomView) {
            this.mWeakRef = new WeakReference<>(zoomView);
        }

        public void handleMessage(Message message) {
            WeakReference<ZoomView> weakReference = this.mWeakRef;
            if (weakReference != null && weakReference.get() != null) {
                ((ZoomView) this.mWeakRef.get()).dismissPercentPopup();
            }
        }
    }
}
