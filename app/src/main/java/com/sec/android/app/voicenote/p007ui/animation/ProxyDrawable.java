package com.sec.android.app.voicenote.p007ui.animation;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;

/* renamed from: com.sec.android.app.voicenote.ui.animation.ProxyDrawable */
public class ProxyDrawable extends Drawable {
    private boolean mMutated;
    private Drawable mProxy;

    public ProxyDrawable(Drawable drawable) {
        this.mProxy = drawable;
    }

    public Drawable getProxy() {
        return this.mProxy;
    }

    public void draw(@NonNull Canvas canvas) {
        Drawable drawable = this.mProxy;
        if (drawable != null) {
            drawable.draw(canvas);
        }
    }

    public int getIntrinsicWidth() {
        Drawable drawable = this.mProxy;
        if (drawable != null) {
            return drawable.getIntrinsicWidth();
        }
        return -1;
    }

    public int getIntrinsicHeight() {
        Drawable drawable = this.mProxy;
        if (drawable != null) {
            return drawable.getIntrinsicHeight();
        }
        return -1;
    }

    public int getOpacity() {
        Drawable drawable = this.mProxy;
        if (drawable != null) {
            return drawable.getOpacity();
        }
        return -2;
    }

    public void setFilterBitmap(boolean z) {
        Drawable drawable = this.mProxy;
        if (drawable != null) {
            drawable.setFilterBitmap(z);
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
        Drawable drawable = this.mProxy;
        if (drawable != null) {
            drawable.setColorFilter(colorFilter);
        }
    }

    public void setAlpha(int i) {
        Drawable drawable = this.mProxy;
        if (drawable != null) {
            drawable.setAlpha(i);
        }
    }

    @NonNull
    public Drawable mutate() {
        if (this.mProxy != null && !this.mMutated && super.mutate() == this) {
            this.mProxy.mutate();
            this.mMutated = true;
        }
        return this;
    }
}
