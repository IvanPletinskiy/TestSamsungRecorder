package com.sec.android.app.voicenote.p007ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TabWidget;
import androidx.core.content.ContextCompat;
import com.sec.android.app.voicenote.C0690R;

/* renamed from: com.sec.android.app.voicenote.ui.view.TabIndicator */
public class TabIndicator extends View {
    private Context mContext;
    private int mCurrentPosition;
    private float mCurrentPositionOffset;
    private Drawable mDrawable;
    private TabWidget mTabWidget;

    public TabIndicator(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public TabIndicator(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mContext = context;
        initView();
    }

    public TabIndicator(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
        initView();
    }

    public void setTabWidget(TabWidget tabWidget, int i) {
        this.mTabWidget = tabWidget;
        this.mCurrentPosition = i;
        this.mCurrentPositionOffset = 0.0f;
    }

    public void setCurrentPosition(int i) {
        this.mCurrentPosition = i;
        this.mCurrentPositionOffset = 0.0f;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        TabWidget tabWidget = this.mTabWidget;
        if (tabWidget != null) {
            int childCount = tabWidget.getChildCount();
            View childAt = this.mTabWidget.getChildAt(this.mCurrentPosition);
            if (childAt != null) {
                float left = (float) childAt.getLeft();
                float right = (float) childAt.getRight();
                float f = this.mCurrentPositionOffset;
                if (f != 0.0f && this.mCurrentPosition < childCount) {
                    float f2 = right - left;
                    left += f * f2;
                    right += f * f2;
                }
                this.mDrawable.setBounds((int) left, 0, (int) right, getHeight());
                this.mDrawable.draw(canvas);
            }
        }
    }

    private void initView() {
        this.mDrawable = ContextCompat.getDrawable(this.mContext, C0690R.C0692drawable.tab_indicator_line_drawable);
    }

    public void updateBottomIndicator(int i, float f) {
        this.mCurrentPosition = i;
        this.mCurrentPositionOffset = f;
        invalidate();
    }
}
