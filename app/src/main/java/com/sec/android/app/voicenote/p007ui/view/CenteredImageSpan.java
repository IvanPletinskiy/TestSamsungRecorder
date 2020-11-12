package com.sec.android.app.voicenote.p007ui.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;
import java.lang.ref.WeakReference;

/* renamed from: com.sec.android.app.voicenote.ui.view.CenteredImageSpan */
public class CenteredImageSpan extends ImageSpan {
    private int extraSpace;
    private int initialDescent;
    private WeakReference<Drawable> mDrawableRef;

    public CenteredImageSpan(Drawable drawable) {
        this(drawable, 0);
    }

    public CenteredImageSpan(Drawable drawable, int i) {
        super(drawable, i);
        this.initialDescent = 0;
        this.extraSpace = 0;
    }

    public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
        Drawable cachedDrawable = getCachedDrawable();
        canvas.save();
        int intrinsicHeight = cachedDrawable.getIntrinsicHeight();
        canvas.translate(f, (float) ((i5 - cachedDrawable.getBounds().bottom) + (((intrinsicHeight - paint.getFontMetricsInt().descent) + paint.getFontMetricsInt().ascent) / 2)));
        cachedDrawable.draw(canvas);
        canvas.restore();
    }

    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
        Rect bounds = getCachedDrawable().getBounds();
        if (fontMetricsInt != null) {
            int i3 = bounds.bottom;
            int i4 = fontMetricsInt.descent;
            int i5 = fontMetricsInt.ascent;
            if (i3 >= i4 - i5) {
                this.initialDescent = i4;
                this.extraSpace = i3 - (i4 - i5);
            }
            fontMetricsInt.descent = (this.extraSpace / 2) + this.initialDescent;
            int i6 = fontMetricsInt.descent;
            fontMetricsInt.bottom = i6;
            fontMetricsInt.ascent = (-bounds.bottom) + i6;
            fontMetricsInt.top = fontMetricsInt.ascent;
        }
        return bounds.right;
    }

    private Drawable getCachedDrawable() {
        WeakReference<Drawable> weakReference = this.mDrawableRef;
        Drawable drawable = weakReference != null ? (Drawable) weakReference.get() : null;
        if (drawable != null) {
            return drawable;
        }
        Drawable drawable2 = getDrawable();
        this.mDrawableRef = new WeakReference<>(drawable2);
        return drawable2;
    }
}
