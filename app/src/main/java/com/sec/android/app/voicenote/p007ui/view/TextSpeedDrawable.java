package com.sec.android.app.voicenote.p007ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.provider.Log;
import java.util.Locale;

/* renamed from: com.sec.android.app.voicenote.ui.view.TextSpeedDrawable */
public class TextSpeedDrawable extends Drawable {
    private float mIntrinsicHeightNumber = 0.0f;
    private float mIntrinsicHeightX = 0.0f;
    private float mIntrinsicWidthNumber = 0.0f;
    private float mIntrinsicWidthX = 0.0f;
    private float mMaxIntrinsicWidthNumber = 0.0f;
    private Paint mPaintNumber = new Paint();
    private Paint mPaintX = new Paint();
    private int mSize = 96;
    private String mTextSpeed;

    /* renamed from: mX */
    private String f109mX;

    public int getOpacity() {
        return -1;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(@Nullable ColorFilter colorFilter) {
    }

    public TextSpeedDrawable(Context context) {
        init(this.mPaintNumber, context);
        init(this.mPaintX, context);
        setTextSize(context);
    }

    public void init(Paint paint, Context context) {
        paint.setColor(context.getColor(C0690R.C0691color.play_speed_popup_text_color));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, 0));
        paint.setAntiAlias(true);
    }

    public void setTextSpeed(String str, String str2) {
        this.mTextSpeed = str;
        this.f109mX = str2;
        setIntrinsicWidthHeight();
    }

    public void setSize(int i) {
        Log.m26i("TextSpeedDrawable", "setSize - size : " + i);
        this.mSize = i;
        setTextSpeed(String.format(Locale.getDefault(), "%.1f", new Object[]{Double.valueOf(0.5d)}), String.format(Locale.getDefault(), "%s", new Object[]{"x"}));
    }

    public void setTextSize(Context context) {
        Resources resources = context.getResources();
        float applyDimension = TypedValue.applyDimension(1, (float) 16, resources.getDisplayMetrics());
        float applyDimension2 = TypedValue.applyDimension(1, (float) 12, resources.getDisplayMetrics());
        if ("ar".equals(Locale.getDefault().getLanguage())) {
            this.mPaintNumber.setTextSize(applyDimension2);
            this.mPaintX.setTextSize(applyDimension);
            return;
        }
        this.mPaintNumber.setTextSize(applyDimension);
        this.mPaintX.setTextSize(applyDimension2);
    }

    private void setIntrinsicWidthHeight() {
        Rect rect = new Rect();
        Paint paint = this.mPaintNumber;
        String str = this.mTextSpeed;
        paint.getTextBounds(str, 0, str.length(), rect);
        this.mIntrinsicWidthNumber = (float) rect.width();
        float f = this.mIntrinsicWidthNumber;
        float f2 = this.mMaxIntrinsicWidthNumber;
        if (f < f2) {
            this.mIntrinsicWidthNumber = f2;
        } else {
            this.mMaxIntrinsicWidthNumber = f;
        }
        this.mIntrinsicHeightNumber = (float) rect.height();
        Paint paint2 = this.mPaintX;
        String str2 = this.f109mX;
        paint2.getTextBounds(str2, 0, str2.length(), rect);
        this.mIntrinsicWidthX = (float) rect.width();
        this.mIntrinsicHeightX = (float) rect.height();
    }

    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();
        float width = (((float) bounds.width()) - (this.mIntrinsicWidthX + this.mIntrinsicWidthNumber)) / 2.0f;
        float centerX = (((float) bounds.centerX()) - width) - (this.mIntrinsicWidthX / 2.0f);
        float centerX2 = (((float) bounds.centerX()) - width) - (this.mIntrinsicWidthNumber / 2.0f);
        canvas.drawText(this.f109mX, ((float) bounds.centerX()) - centerX, (((float) bounds.height()) + this.mIntrinsicHeightX) / 2.0f, this.mPaintX);
        canvas.drawText(this.mTextSpeed, ((float) bounds.centerX()) + centerX2, (((float) bounds.height()) + this.mIntrinsicHeightNumber) / 2.0f, this.mPaintNumber);
    }

    public int getIntrinsicHeight() {
        return this.mSize;
    }

    public int getIntrinsicWidth() {
        return this.mSize;
    }
}
