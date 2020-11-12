package com.sec.android.app.voicenote.activity;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.ColorRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.p007ui.actionbar.OffsetUpdateListener;
import com.sec.android.app.voicenote.provider.DesktopModeProvider;

public abstract class BaseToolbarActivity extends AppCompatActivity {
    private static final String TAG = "BaseToolbarActivity";
    private AppBarLayout appBarLayout;
    protected ActionBar mActionBar;
    protected CollapsingToolbarLayout mCollapsingToolbarLayout;
    private float mHeightPercent = 0.3967f;
    protected Resources mResource;
    protected Toolbar mToolBar;
    private OffsetUpdateListener offsetUpdateListener;

    public boolean isCollapsingToolbarEnable() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        this.mResource = getResources();
    }

    public void setContentView(View view) {
        ViewGroup viewGroup;
        super.setContentView((int) C0690R.layout.activity_base_toolbar);
        initView();
        if (view != null && (viewGroup = (ViewGroup) findViewById(C0690R.C0693id.layout_container)) != null) {
            viewGroup.addView(view);
        }
    }

    public void setContentView(@LayoutRes int i) {
        super.setContentView((int) C0690R.layout.activity_base_toolbar);
        initView();
        View inflate = getLayoutInflater().inflate(i, (ViewGroup) null);
        ViewGroup viewGroup = (ViewGroup) findViewById(C0690R.C0693id.layout_container);
        if (viewGroup != null) {
            viewGroup.addView(inflate);
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        if (this.appBarLayout != null) {
            this.appBarLayout = null;
        }
        if (this.mActionBar != null) {
            this.mActionBar = null;
        }
        if (this.mCollapsingToolbarLayout != null) {
            this.mCollapsingToolbarLayout = null;
        }
        if (this.mToolBar != null) {
            this.mToolBar = null;
        }
        if (this.offsetUpdateListener != null) {
            this.offsetUpdateListener = null;
        }
        super.onDestroy();
    }

    private void initView() {
        CoordinatorLayout.LayoutParams layoutParams;
        this.mToolBar = (Toolbar) findViewById(C0690R.C0693id.toolbar);
        setSupportActionBar(this.mToolBar);
        this.mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(C0690R.C0693id.collapsing_app_bar);
        this.offsetUpdateListener = new OffsetUpdateListener(this, this.mCollapsingToolbarLayout);
        this.appBarLayout = (AppBarLayout) findViewById(C0690R.C0693id.app_bar_layout);
        this.appBarLayout.setExpanded(false, false);
        this.appBarLayout.setActivated(false);
        setScrollFlags(true);
        this.mActionBar = getSupportActionBar();
        this.mActionBar.setBackgroundDrawable(new ColorDrawable(this.mResource.getColor(C0690R.C0691color.actionbar_color_bg, (Resources.Theme) null)));
        this.appBarLayout.setBackgroundColor(getColor(C0690R.C0691color.actionbar_color_bg));
        if (isCollapsingToolbarEnable()) {
            float dimensionPixelSize = (float) this.mResource.getDimensionPixelSize(C0690R.dimen.sesl_action_bar_default_height);
            if (this.mHeightPercent > 0.0f) {
                float f = ((float) this.mResource.getDisplayMetrics().heightPixels) * this.mHeightPercent;
                if (DesktopModeProvider.isDesktopMode() ? !(this.mResource.getConfiguration().screenHeightDp < 600 || this.mResource.getConfiguration().smallestScreenWidthDp < 600) : !((isInMultiWindowMode() && this.mResource.getConfiguration().smallestScreenWidthDp < 480) || (this.mResource.getConfiguration().orientation == 2 && this.mResource.getConfiguration().smallestScreenWidthDp < 600))) {
                    dimensionPixelSize = f;
                }
                try {
                    layoutParams = (CoordinatorLayout.LayoutParams) this.appBarLayout.getLayoutParams();
                } catch (ClassCastException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                    layoutParams = null;
                }
                if (layoutParams != null) {
                    layoutParams.height = (int) dimensionPixelSize;
                    Log.d(TAG, "onMeasure: LayoutParams :" + layoutParams + " ,lp.height :" + layoutParams.height);
                    this.appBarLayout.setLayoutParams(layoutParams);
                }
            }
        }
    }

    private void setScrollFlags(boolean z) {
        CollapsingToolbarLayout collapsingToolbarLayout = this.mCollapsingToolbarLayout;
        if (collapsingToolbarLayout != null) {
            AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
            if (z) {
                layoutParams.setScrollFlags(3);
            } else {
                layoutParams.setScrollFlags(6);
            }
            this.mCollapsingToolbarLayout.setLayoutParams(layoutParams);
        }
    }

    public void setTitleActivity(@StringRes int i) {
        ActionBar actionBar = this.mActionBar;
        if (actionBar != null) {
            actionBar.setTitle(i);
        }
    }

    public void setTitleActivity(String str) {
        ActionBar actionBar = this.mActionBar;
        if (actionBar != null) {
            actionBar.setTitle((CharSequence) str);
        }
    }

    public void setOverwriteBackgroundToolbar(@ColorRes int i) {
        ActionBar actionBar = this.mActionBar;
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(this.mResource.getColor(i, (Resources.Theme) null)));
        }
    }

    public void setDisplayShowHomeEnabled() {
        ActionBar actionBar = this.mActionBar;
        if (actionBar != null) {
            actionBar.setDisplayOptions(4, 4);
            this.mActionBar.setDisplayHomeAsUpEnabled(true);
            this.mActionBar.setDisplayShowHomeEnabled(true);
        }
    }

    public void setTextViewTitle(TextView textView) {
        CollapsingToolbarLayout collapsingToolbarLayout = this.mCollapsingToolbarLayout;
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setTitleEnabled(true);
            this.offsetUpdateListener.setSelectedTextView(textView);
            this.appBarLayout.addOnOffsetChangedListener((AppBarLayout.OnOffsetChangedListener) this.offsetUpdateListener);
            this.appBarLayout.setBackgroundColor(getColor(C0690R.C0691color.main_window_bg));
        }
    }

    /* access modifiers changed from: protected */
    public void setCollapsingToolbarTitle(String str) {
        if (this.mCollapsingToolbarLayout != null && isCollapsingToolbarEnable()) {
            this.mCollapsingToolbarLayout.setTitle(str);
        }
    }
}
