package com.sec.android.app.voicenote.p007ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.provider.AssistantProvider;
import com.sec.android.app.voicenote.provider.WaveProvider;
import com.sec.android.app.voicenote.service.Engine;
import java.util.Locale;

/* renamed from: com.sec.android.app.voicenote.ui.view.FloatingView */
public class FloatingView extends LinearLayout {
    private static final String TAG = "FloatingView";
    private float mEmptyWidth = 0.0f;
    private boolean mIsFixed = false;
    private float mScrollOffset = 0.0f;
    private float mViewWidth = 0.0f;

    public FloatingView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public FloatingView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public FloatingView(Context context) {
        super(context);
        init();
    }

    public void init() {
        this.mEmptyWidth = (((float) WaveProvider.WAVE_AREA_WIDTH) * 1.0f) / 2.0f;
        this.mViewWidth = getResources().getDimension(C0690R.dimen.wave_floating_view_width);
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i == 0) {
            init();
        }
    }

    public int getTime(int i) {
        return (int) ((((getX() + ((float) i)) - this.mEmptyWidth) + (this.mViewWidth / 2.0f)) * WaveProvider.MS_PER_PX);
    }

    public void setX(float f, float f2) {
        if (getVisibility() != 8) {
            if (!this.mIsFixed) {
                float duration = ((float) Engine.getInstance().getDuration()) / WaveProvider.MS_PER_PX;
                float f3 = this.mEmptyWidth;
                float f4 = duration + (f3 * 2.0f);
                float f5 = this.mViewWidth;
                if (f < (f3 - f2) - (f5 / 2.0f)) {
                    f = (f3 - f2) - (f5 / 2.0f);
                } else {
                    float f6 = f4 - f2;
                    if (f > (f6 - f3) - (f5 / 2.0f)) {
                        f = (f6 - f3) - (f5 / 2.0f);
                    }
                }
                setX(f);
                this.mScrollOffset = f2;
            }
            setContentDescription(AssistantProvider.getInstance().stringForReadTime(getStringByDuration(getTime((int) this.mScrollOffset))) + ", " + getResources().getString(C0690R.string.swipe_with_two_fingers_to_control));
        }
    }

    public void update(float f) {
        if (getVisibility() != 8 && !this.mIsFixed) {
            setX(getX() - (f - this.mScrollOffset));
            this.mScrollOffset = f;
        }
    }

    public void setTime(int i, float f) {
        float f2 = (((((float) i) / WaveProvider.MS_PER_PX) + this.mEmptyWidth) - f) - (this.mViewWidth / 2.0f);
        setX(f2);
        if (getTime((int) f) < i) {
            setX(f2 + 1.0f);
        }
        this.mScrollOffset = f;
        int id = getId();
        if (id == C0690R.C0693id.wave_left_repeat_handler_layout) {
            setContentDescription(getResources().getString(C0690R.string.start_point_repeating_section_tts) + ", " + AssistantProvider.getInstance().stringForReadTime(getStringByDuration(i)) + ", " + getResources().getString(C0690R.string.swipe_with_two_fingers_to_control));
        } else if (id != C0690R.C0693id.wave_right_repeat_handler_layout) {
            setContentDescription(AssistantProvider.getInstance().stringForReadTime(getStringByDuration(i)) + ", " + getResources().getString(C0690R.string.swipe_with_two_fingers_to_control));
        } else {
            setContentDescription(getResources().getString(C0690R.string.end_point_repeating_section_tts) + ", " + AssistantProvider.getInstance().stringForReadTime(getStringByDuration(i)) + ", " + getResources().getString(C0690R.string.swipe_with_two_fingers_to_control));
        }
    }

    private String getStringByDuration(int i) {
        int i2 = i / 1000;
        int i3 = i2 / 3600;
        int i4 = (i2 / 60) % 60;
        int i5 = i2 % 60;
        if (i3 > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5)});
        }
        return String.format(Locale.US, "%02d:%02d", new Object[]{Integer.valueOf(i4), Integer.valueOf(i5)});
    }
}
