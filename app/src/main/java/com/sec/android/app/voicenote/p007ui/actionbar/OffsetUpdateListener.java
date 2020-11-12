package com.sec.android.app.voicenote.p007ui.actionbar;

import android.app.Activity;
import android.graphics.Rect;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.sec.android.app.voicenote.C0690R;

/* renamed from: com.sec.android.app.voicenote.ui.actionbar.OffsetUpdateListener */
public class OffsetUpdateListener implements AppBarLayout.OnOffsetChangedListener {
    private Activity mActivity;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private TextView mSelectedTextView;

    public OffsetUpdateListener(Activity activity, CollapsingToolbarLayout collapsingToolbarLayout) {
        this.mActivity = activity;
        this.mCollapsingToolbarLayout = collapsingToolbarLayout;
    }

    public void setSelectedTextView(TextView textView) {
        this.mSelectedTextView = textView;
    }

    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        TextView textView;
        if (this.mCollapsingToolbarLayout != null) {
            appBarLayout.getWindowVisibleDisplayFrame(new Rect());
            int abs = Math.abs(appBarLayout.getTop());
            float height = ((float) this.mCollapsingToolbarLayout.getHeight()) * 0.17999999f;
            if (this.mCollapsingToolbarLayout.isTitleEnabled() && (textView = this.mSelectedTextView) != null) {
                int currentTextColor = textView.getCurrentTextColor();
                if (appBarLayout.getHeight() == this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.sesl_action_bar_default_height)) {
                    this.mSelectedTextView.setTextColor(ColorUtils.setAlphaComponent(currentTextColor, 255));
                    return;
                }
                float f = 255.0f - ((100.0f / height) * (((float) abs) - 0.0f));
                if (f < 0.0f) {
                    f = 0.0f;
                } else if (f > 255.0f) {
                    f = 255.0f;
                }
                this.mSelectedTextView.setTextColor(ColorUtils.setAlphaComponent(currentTextColor, (int) (255.0f - f)));
            }
        }
    }
}
