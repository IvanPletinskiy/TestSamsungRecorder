package com.sec.android.app.voicenote.p007ui.animation;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import androidx.annotation.NonNull;

/* renamed from: com.sec.android.app.voicenote.ui.animation.AnimateDrawable */
public class AnimateDrawable extends ProxyDrawable {
    private Animation mAnimation;
    private final Transformation mTransformation = new Transformation();

    public AnimateDrawable(Drawable drawable) {
        super(drawable);
    }

    public Animation getAnimation() {
        return this.mAnimation;
    }

    public void setAnimation(Animation animation) {
        this.mAnimation = animation;
    }

    public boolean hasStarted() {
        Animation animation = this.mAnimation;
        return animation != null && animation.hasStarted();
    }

    public boolean hasEnded() {
        Animation animation = this.mAnimation;
        return animation == null || animation.hasEnded();
    }

    public void draw(@NonNull Canvas canvas) {
        Drawable proxy = getProxy();
        if (proxy != null) {
            int save = canvas.save();
            Animation animation = this.mAnimation;
            if (animation != null) {
                animation.getTransformation(AnimationUtils.currentAnimationTimeMillis(), this.mTransformation);
                canvas.concat(this.mTransformation.getMatrix());
            }
            proxy.draw(canvas);
            canvas.restoreToCount(save);
        }
    }
}
