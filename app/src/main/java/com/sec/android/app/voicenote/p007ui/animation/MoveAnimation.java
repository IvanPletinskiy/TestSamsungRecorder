package com.sec.android.app.voicenote.p007ui.animation;

import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/* renamed from: com.sec.android.app.voicenote.ui.animation.MoveAnimation */
public class MoveAnimation extends Animation {
    private float mFromAlpha = 1.0f;
    private float mFromScaleX = 1.0f;
    private float mFromScaleY = 1.0f;
    private float mFromXDelta;
    private float mFromYDelta;
    private long mMoveDuration = 300;
    private float mPivotX = 0.0f;
    private float mPivotY = 0.0f;
    private float mToAlpha = 1.0f;
    private float mToScaleX = 1.0f;
    private float mToScaleY = 1.0f;
    private float mToXDelta;
    private float mToYDelta;

    public MoveAnimation(float f, float f2, float f3, float f4) {
        this.mFromXDelta = f;
        this.mToXDelta = f2;
        this.mFromYDelta = f3;
        this.mToYDelta = f4;
        setDuration(this.mMoveDuration);
        setFillAfter(true);
        initialize(10, 10, 10, 10);
    }

    public void setFillAfter(boolean z) {
        super.setFillAfter(z);
    }

    public void setScale(float f, float f2, float f3, float f4) {
        this.mFromScaleX = f;
        this.mToScaleX = f2;
        this.mFromScaleY = f3;
        this.mToScaleY = f4;
    }

    public void setPivot(float f, float f2) {
        this.mPivotX = f;
        this.mPivotY = f2;
    }

    public void setAlpha(float f, float f2) {
        this.mFromAlpha = f;
        this.mToAlpha = f2;
    }

    public final void setDuration(long j) {
        this.mMoveDuration = j;
        super.setDuration(j);
    }

    /* access modifiers changed from: protected */
    public void applyTransformation(float f, Transformation transformation) {
        float f2;
        float f3;
        float f4 = this.mFromXDelta;
        float f5 = this.mFromYDelta;
        transformation.clear();
        Transformation transformation2 = new Transformation();
        float f6 = this.mFromXDelta;
        float f7 = this.mToXDelta;
        if (f6 != f7) {
            f4 = f6 + ((f7 - f6) * f);
        }
        float f8 = this.mFromYDelta;
        float f9 = this.mToYDelta;
        if (f8 != f9) {
            f5 = f8 + ((f9 - f8) * f);
        }
        if (Float.compare(f4, f5) != 0) {
            transformation2.getMatrix().setTranslate(f4, f5);
            transformation.compose(transformation2);
            transformation2.clear();
        }
        if (this.mFromScaleX == 1.0f && this.mToScaleX == 1.0f) {
            f2 = 1.0f;
        } else {
            float f10 = this.mFromScaleX;
            f2 = f10 + ((this.mToScaleX - f10) * f);
        }
        if (this.mFromScaleY == 1.0f && this.mToScaleY == 1.0f) {
            f3 = 1.0f;
        } else {
            float f11 = this.mFromScaleY;
            f3 = f11 + ((this.mToScaleY - f11) * f);
        }
        if (!(f2 == 1.0f && f3 == 1.0f)) {
            float[] fArr = new float[9];
            transformation.getMatrix().getValues(fArr);
            if (this.mPivotX == 0.0f && this.mPivotY == 0.0f) {
                transformation2.getMatrix().setScale(f2, f3, 0.0f, 0.0f);
            } else {
                transformation2.getMatrix().setScale(f2, f3, fArr[2] + this.mPivotX, fArr[5] + this.mPivotY);
            }
            transformation.compose(transformation2);
            transformation2.clear();
        }
        if (this.mFromAlpha != 1.0f || this.mToAlpha != 1.0f) {
            float f12 = this.mFromAlpha;
            transformation2.setAlpha(f12 + (f * (this.mToAlpha - f12)));
            transformation.compose(transformation2);
        }
    }

    private void swapMoveXY() {
        float f = this.mFromXDelta;
        float f2 = this.mFromYDelta;
        this.mFromXDelta = this.mToXDelta;
        this.mToXDelta = f;
        this.mFromYDelta = this.mToYDelta;
        this.mToYDelta = f2;
    }

    public void reverseAnimation() {
        long uptimeMillis = SystemClock.uptimeMillis();
        long duration = getDuration() - (uptimeMillis - getStartTime());
        swapMoveXY();
        if (!hasEnded()) {
            setStartTime(uptimeMillis - duration);
        } else {
            setStartTime(-1);
        }
    }
}
