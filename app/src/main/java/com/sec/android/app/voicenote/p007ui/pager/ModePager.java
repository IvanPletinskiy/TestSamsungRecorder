package com.sec.android.app.voicenote.p007ui.pager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.common.util.Trace;
import com.sec.android.app.voicenote.main.VNMainActivity;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.provider.DesktopModeProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.provider.UPSMProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.uicore.FragmentController;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.util.Observable;
import java.util.Observer;

/* renamed from: com.sec.android.app.voicenote.ui.pager.ModePager */
public class ModePager implements Observer, DialogFactory.DialogResultListener, FragmentController.OnSceneChangeListener {
    private static final int POSITION_NONE = -1;
    private static final String TAG = "ModePager";
    @SuppressLint({"StaticFieldLeak"})
    private static ModePager mInstance;
    private static final SparseIntArray mRecordModes = new SparseIntArray();
    private AppCompatActivity mActivity = null;
    private RelativeLayout mContentTabLayout = null;
    private int mCurrentTabSelected = -1;
    private boolean mIsMaxPowerErrorMsgShowing = false;
    private boolean mIsShowingHelpModeGuide = false;
    private int mMainTabHeight = 0;
    private HorizontalScrollView mMainTabLayout = null;
    private int mOldMode = -1;
    private VNMainActivity mVNMainActivity = null;

    private ModePager() {
        Log.m19d(TAG, "ModePager creator !!");
    }

    public static ModePager getInstance() {
        if (mInstance == null) {
            mInstance = new ModePager();
        }
        return mInstance;
    }

    public void setContext(Context context) {
        this.mActivity = (AppCompatActivity) context;
        if (context instanceof VNMainActivity) {
            this.mVNMainActivity = (VNMainActivity) context;
        }
    }

    private void updateTabLayoutInTabletMultiWindow(int i) {
        if (!DisplayManager.isDeviceOnLandscape()) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mMainTabLayout.getLayoutParams();
            int i2 = (int) (((double) this.mActivity.getResources().getDisplayMetrics().widthPixels) * 0.125d);
            marginLayoutParams.rightMargin = i2;
            marginLayoutParams.leftMargin = i2;
            this.mMainTabLayout.setLayoutParams(marginLayoutParams);
            return;
        }
        DisplayMetrics displayMetrics = this.mActivity.getResources().getDisplayMetrics();
        int dimensionPixelSize = this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.main_tab_vertical_margin);
        int i3 = (int) (((double) displayMetrics.heightPixels) * 0.015d);
        ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) this.mMainTabLayout.getLayoutParams();
        if (DisplayManager.getVROrientation() == 3) {
            int i4 = (int) (((double) displayMetrics.widthPixels) * 0.185d);
            marginLayoutParams2.rightMargin = i4;
            marginLayoutParams2.leftMargin = i4;
            marginLayoutParams2.topMargin = i3;
            this.mMainTabLayout.setLayoutParams(marginLayoutParams2);
        } else if (DisplayManager.getVROrientation() == 2) {
            int i5 = displayMetrics.widthPixels;
            if ((((double) i5) - ((((double) i5) * 0.185d) * 2.0d)) / ((double) getTabCount()) > ((double) i)) {
                dimensionPixelSize = (int) (((double) displayMetrics.widthPixels) * 0.185d);
            }
            marginLayoutParams2.rightMargin = dimensionPixelSize;
            marginLayoutParams2.leftMargin = dimensionPixelSize;
            marginLayoutParams2.topMargin = i3;
            this.mMainTabLayout.setLayoutParams(marginLayoutParams2);
        }
    }

    private void initTab(int i) {
        Log.m26i(TAG, "init tabCount = " + i);
        Trace.beginSection("ModePager.initTab");
        this.mMainTabLayout = (HorizontalScrollView) this.mActivity.findViewById(C0690R.C0693id.tab_view_layout);
        this.mContentTabLayout = (RelativeLayout) this.mActivity.findViewById(C0690R.C0693id.content);
        if (i == 1) {
            addTab(0, 1, this.mActivity.getString(C0690R.string.normal_mode));
        } else if (i != 2) {
            addTab(0, 3, this.mActivity.getString(C0690R.string.normal_mode));
            addTab(1, 3, this.mActivity.getString(C0690R.string.interview_mode));
            addTab(2, 3, this.mActivity.getString(C0690R.string.speech_to_text_mode));
        } else if (VoiceNoteFeature.FLAG_SUPPORT_INTERVIEW) {
            addTab(0, 2, this.mActivity.getString(C0690R.string.normal_mode));
            addTab(1, 2, this.mActivity.getString(C0690R.string.interview_mode));
        } else if (VoiceNoteFeature.FLAG_SUPPORT_VOICE_MEMO(this.mActivity)) {
            addTab(0, 2, this.mActivity.getString(C0690R.string.normal_mode));
            addTab(1, 2, this.mActivity.getString(C0690R.string.speech_to_text_mode));
        }
        setSpaceTab();
        updateTabSelected();
        Trace.endSection();
    }

    public void setSpaceTab() {
        View view;
        HorizontalScrollView horizontalScrollView = this.mMainTabLayout;
        if (horizontalScrollView != null && horizontalScrollView.getChildAt(0) != null && ((LinearLayout) this.mMainTabLayout.getChildAt(0)).getChildAt(0) != null) {
            final View childAt = this.mMainTabLayout.getChildAt(0);
            if (this.mCurrentTabSelected != 0 || ((LinearLayout) childAt).getChildCount() == 1) {
                view = ((ViewGroup) childAt).getChildAt(0);
            } else {
                view = ((ViewGroup) childAt).getChildAt(1);
            }
            int i = -1;
            if (view instanceof TextView) {
                String string = this.mActivity.getString(C0690R.string.speech_to_text_mode);
                i = (int) ((TextView) view).getPaint().measureText(string, 0, string.length());
            }
            if (VoiceNoteFeature.FLAG_IS_TABLET && DisplayManager.getMultiwindowMode() == 2) {
                updateTabLayoutInTabletMultiWindow((i * 3) / 2);
            }
            final int dimensionPixelSize = this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.main_tab_layout_space_size);
            final int finalI = i;
            this.mMainTabLayout.post(new Runnable() {
                private final /* synthetic */ View f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = childAt;
                    this.f$2 = dimensionPixelSize;
                    this.f$3 = finalI + dimensionPixelSize;
                }

                public final void run() {
                    ModePager.this.lambda$setSpaceTab$0$ModePager(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$setSpaceTab$0$ModePager(View view, int i, int i2) {
        if (this.mMainTabLayout != null) {
            int paddingEnd = view.getPaddingEnd() + view.getPaddingStart();
            ViewGroup viewGroup = (ViewGroup) this.mMainTabLayout.getChildAt(0);
            int childCount = viewGroup.getChildCount();
            int i3 = childCount - 1;
            int width = ((this.mMainTabLayout.getWidth() - (i * i3)) / childCount) - this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.main_tab_layout_item_width_reduce);
            this.mMainTabHeight = getDefaultTabHeight();
            Log.m19d(TAG, "width = " + width + " - " + i2 + " - " + paddingEnd + ", space = " + i);
            int i4 = i2 + paddingEnd;
            if (width > i4) {
                i4 = width;
            }
            for (int i5 = 0; i5 < childCount; i5++) {
                TextView textView = (TextView) viewGroup.getChildAt(i5);
                textView.setMinimumWidth(i4);
                textView.setMinimumHeight(this.mMainTabHeight);
                if (i5 != i3) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
                    marginLayoutParams.setMarginEnd(i);
                    textView.setLayoutParams(marginLayoutParams);
                }
            }
            ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) this.mMainTabLayout.getLayoutParams();
            marginLayoutParams2.height = this.mMainTabHeight;
            if (!VoiceNoteFeature.FLAG_IS_TABLET && DisplayManager.getMultiwindowMode() == 2 && DisplayManager.getVROrientation() == 2) {
                marginLayoutParams2.topMargin = this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.multi_main_tab_pager_margin_top);
            }
            this.mMainTabLayout.setLayoutParams(marginLayoutParams2);
            updateTabSelected();
        }
    }

    private void addTab(final int i, int i2, String str) {
        TextView textView = (TextView) this.mActivity.getLayoutInflater().inflate(C0690R.layout.custom_tab_layout, (ViewGroup) null);
        textView.setBackground(this.mActivity.getResources().getDrawable(C0690R.C0692drawable.tab_background_selector, this.mActivity.getTheme()));
        textView.setText(str);
        textView.setTextSize(0, this.mActivity.getResources().getDimension(C0690R.dimen.main_tab_text_size));
        textView.setClickable(true);
        textView.setContentDescription(str + this.mActivity.getString(C0690R.string.tts_tab_n_of_n, new Object[]{Integer.valueOf(i + 1), Integer.valueOf(i2)}));
        textView.setOnClickListener(new View.OnClickListener() {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = i;
            }

            public final void onClick(View view) {
                ModePager.this.lambda$addTab$1$ModePager(this.f$1, view);
            }
        });
        ((LinearLayout) this.mMainTabLayout.getChildAt(0)).addView(textView);
    }

    public /* synthetic */ void lambda$addTab$1$ModePager(int i, View view) {
        if (this.mCurrentTabSelected == i) {
            checkModeNotSupported();
            return;
        }
        this.mCurrentTabSelected = i;
        onViewTabSelected();
        updateTabViewStyle(i);
    }

    public void hideTab(boolean z) {
        Log.m19d(TAG, "hideTab withAnimation : " + z);
        HorizontalScrollView horizontalScrollView = this.mMainTabLayout;
        if (horizontalScrollView != null && horizontalScrollView.getVisibility() != 8) {
            if (z) {
                this.mMainTabLayout.startAnimation(AnimationUtils.loadAnimation(this.mActivity, C0690R.animator.ani_actionbar_hide));
                return;
            }
            this.mMainTabLayout.setVisibility(8);
        }
    }

    public void hideContentTab(boolean z) {
        AppCompatActivity appCompatActivity;
        Log.m19d(TAG, "hideContentTab");
        if (this.mContentTabLayout == null && (appCompatActivity = this.mActivity) != null) {
            this.mContentTabLayout = (RelativeLayout) appCompatActivity.findViewById(C0690R.C0693id.content);
        }
        RelativeLayout relativeLayout = this.mContentTabLayout;
        if (relativeLayout != null && relativeLayout.getVisibility() != 8) {
            if (z) {
                this.mContentTabLayout.startAnimation(AnimationUtils.loadAnimation(this.mActivity, C0690R.animator.ani_idle_pager_hide));
                return;
            }
            this.mContentTabLayout.setVisibility(8);
        }
    }

    public void showContentTab(boolean z) {
        Log.m19d(TAG, "showContentTab");
        RelativeLayout relativeLayout = this.mContentTabLayout;
        if (relativeLayout != null && relativeLayout.getVisibility() != 0) {
            if (z) {
                this.mContentTabLayout.startAnimation(AnimationUtils.loadAnimation(this.mActivity, C0690R.animator.ani_idle_pager_show));
            }
            this.mContentTabLayout.setAlpha(1.0f);
            this.mContentTabLayout.setVisibility(0);
        }
    }

    public void showTab(boolean z) {
        Log.m19d(TAG, "showTab");
        HorizontalScrollView horizontalScrollView = this.mMainTabLayout;
        if (horizontalScrollView != null && horizontalScrollView.getVisibility() != 0) {
            if (z) {
                this.mMainTabLayout.startAnimation(AnimationUtils.loadAnimation(this.mActivity, C0690R.animator.ani_actionbar_show));
            }
            this.mMainTabLayout.setAlpha(1.0f);
            this.mMainTabLayout.setVisibility(0);
        }
    }

    public void hideTips() {
        FragmentManager supportFragmentManager;
        if (this.mMainTabLayout != null && (supportFragmentManager = this.mActivity.getSupportFragmentManager()) != null) {
            Fragment findFragmentById = supportFragmentManager.findFragmentById(C0690R.C0693id.content);
            if (findFragmentById instanceof AbsPagerFragment) {
                ((AbsPagerFragment) findFragmentById).hideHelpModeGuide();
            }
        }
    }

    public void start() {
        HorizontalScrollView horizontalScrollView;
        Log.m19d(TAG, "start");
        AppCompatActivity appCompatActivity = this.mActivity;
        if (appCompatActivity == null || appCompatActivity.isDestroyed()) {
            Log.m32w(TAG, "start - activity is not valid");
            return;
        }
        if (this.mMainTabLayout == null || mRecordModes.size() < getTabCount() || ((horizontalScrollView = this.mMainTabLayout) != null && (horizontalScrollView.getChildCount() < 1 || (this.mMainTabLayout.getChildCount() > 0 && ((LinearLayout) this.mMainTabLayout.getChildAt(0)).getChildCount() < 1)))) {
            EnterRecordModesData();
            init();
        } else {
            updateTabSelected();
            checkModeNotSupported();
        }
        VoiceNoteObservable.getInstance().addObserver(this);
    }

    public void stop() {
        Log.m26i(TAG, "stop");
        VoiceNoteObservable.getInstance().deleteObserver(this);
    }

    public void onDestroy(int i) {
        Log.m26i(TAG, "onDestroy");
        AppCompatActivity appCompatActivity = this.mActivity;
        if (appCompatActivity == null || i == appCompatActivity.hashCode()) {
            this.mActivity = null;
            this.mVNMainActivity = null;
            this.mMainTabLayout = null;
            this.mContentTabLayout = null;
            FragmentController.getInstance().unregisterSceneChangeListener(this);
            PagerFactory.removeAll();
            return;
        }
        Log.m32w(TAG, "onDestroy - current activity : " + this.mActivity.hashCode() + " destroying activity : " + i);
    }

    public void onSceneChange(int i) {
        if (i == 1 && Settings.getIntSettings("record_mode", 1) == 4 && this.mActivity != null && UPSMProvider.getInstance().isUltraPowerSavingMode()) {
            if (UPSMProvider.getInstance().supportMaxMode()) {
                AppCompatActivity appCompatActivity = this.mActivity;
                Toast.makeText(appCompatActivity, appCompatActivity.getString(C0690R.string.max_power_mode_error_msg, new Object[]{appCompatActivity.getString(C0690R.string.speech_to_text_mode)}), 0).show();
                this.mIsMaxPowerErrorMsgShowing = true;
                new Handler().postDelayed(new Runnable() {
                    public final void run() {
                        ModePager.this.lambda$onSceneChange$2$ModePager();
                    }
                }, 200);
                return;
            }
            Toast.makeText(this.mActivity, C0690R.string.ups_mode_error_msg, 0).show();
        }
    }

    public /* synthetic */ void lambda$onSceneChange$2$ModePager() {
        this.mIsMaxPowerErrorMsgShowing = false;
    }

    private void updateTabViewStyle(int i) {
        int childCount = ((LinearLayout) this.mMainTabLayout.getChildAt(0)).getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            TextView textView = (TextView) ((LinearLayout) this.mMainTabLayout.getChildAt(0)).getChildAt(i2);
            if (i2 == i) {
                textView.setSelected(true);
                textView.setTypeface(Typeface.create("sans-serif-medium", 0));
                scrollToSelectedTab(textView);
            } else {
                textView.setSelected(false);
                textView.setTypeface(Typeface.create("sans-serif", 0));
            }
        }
    }

    private void onViewTabSelected() {
        Log.m19d(TAG, "onTabSelected");
        if (VoiceNoteApplication.getScene() == 0 || VoiceNoteApplication.getScene() == 1 || VoiceNoteApplication.getScene() == 11) {
            int intSettings = Settings.getIntSettings("record_mode", 1);
            SparseIntArray sparseIntArray = mRecordModes;
            if (!(sparseIntArray == null || intSettings == 6)) {
                intSettings = sparseIntArray.get(this.mCurrentTabSelected);
            }
            checkModeNotSupported(intSettings);
            if (intSettings == 1) {
                SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_ready_common), this.mActivity.getResources().getString(C0690R.string.event_standard_mode));
            } else if (intSettings == 2) {
                SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_ready_common), this.mActivity.getResources().getString(C0690R.string.event_interview_mode));
            } else if (intSettings == 4) {
                if (UPSMProvider.getInstance().isUltraPowerSavingMode()) {
                    if (!UPSMProvider.getInstance().supportMaxMode()) {
                        Toast.makeText(this.mActivity, C0690R.string.ups_mode_error_msg, 0).show();
                    } else if (!this.mIsMaxPowerErrorMsgShowing) {
                        AppCompatActivity appCompatActivity = this.mActivity;
                        Toast.makeText(appCompatActivity, appCompatActivity.getString(C0690R.string.max_power_mode_error_msg, new Object[]{appCompatActivity.getString(C0690R.string.speech_to_text_mode)}), 0).show();
                    }
                } else if (VoiceNoteFeature.FLAG_SUPPORT_DATA_CHECK_POPUP && Settings.getBooleanSettings(Settings.KEY_DATA_CHECK_SHOW_AGAIN, true)) {
                    showDataCheckDialog();
                }
                SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_ready_common), this.mActivity.getResources().getString(C0690R.string.event_stt_mode));
            }
            SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_MODE, intSettings);
            if (intSettings != 6 && this.mMainTabLayout != null) {
                replaceFragment(intSettings);
            }
        }
    }

    private void replaceFragment(int i) {
        if (this.mActivity != null) {
            if (this.mOldMode != i || !this.mIsShowingHelpModeGuide) {
                Log.m19d(TAG, "replaceFragment = " + i + " - mOldMode : " + this.mOldMode);
                Settings.setSettings("record_mode", i);
                this.mOldMode = i;
                this.mActivity.getSupportFragmentManager().beginTransaction().replace(C0690R.C0693id.content, PagerFactory.create(i), (String) null).commitAllowingStateLoss();
                if (i == 6) {
                    setPagingEnabled(false);
                } else {
                    setPagingEnabled(true);
                }
            }
        }
    }

    private void checkModeNotSupported() {
        int intSettings = Settings.getIntSettings("record_mode", 1);
        if ((Engine.getInstance().isWiredHeadSetConnected() || Engine.getInstance().isBluetoothSCOConnected()) && intSettings != 1 && !DialogFactory.isDialogVisible(this.mActivity.getSupportFragmentManager(), DialogFactory.MODE_NOT_SUPPORTED)) {
            DialogFactory.clearDialogByTag(this.mActivity.getSupportFragmentManager(), DialogFactory.MODE_NOT_SUPPORTED);
            DialogFactory.show(this.mActivity.getSupportFragmentManager(), DialogFactory.MODE_NOT_SUPPORTED, (Bundle) null);
        }
    }

    private void init() {
        Log.m19d(TAG, "initPager ");
        int tabCount = getTabCount();
        if (tabCount == 1) {
            this.mMainTabLayout = (HorizontalScrollView) this.mActivity.findViewById(C0690R.C0693id.tab_view_layout);
            replaceFragment(1);
            return;
        }
        initTab(tabCount);
        FragmentController.getInstance().registerSceneChangeListener(this);
    }

    private void EnterRecordModesData() {
        int i = 0;
        if (getTabCount() == 1) {
            mRecordModes.put(0, 1);
            return;
        }
        mRecordModes.clear();
        Log.m19d(TAG, "Layout normal");
        mRecordModes.put(0, 1);
        if (VoiceNoteFeature.FLAG_SUPPORT_INTERVIEW) {
            Log.m29v(TAG, "EnterRecordModesData - interview page is added");
            mRecordModes.put(1, 2);
            i = 1;
        }
        if (VoiceNoteFeature.FLAG_SUPPORT_VOICE_MEMO(this.mActivity)) {
            Log.m29v(TAG, "EnterRecordModesData - voice memo page is added");
            mRecordModes.put(i + 1, 4);
        }
    }

    public void updateTabSelected() {
        HorizontalScrollView horizontalScrollView = this.mMainTabLayout;
        if (horizontalScrollView != null) {
            horizontalScrollView.post(new Runnable() {
                public final void run() {
                    ModePager.this.lambda$updateTabSelected$3$ModePager();
                }
            });
            replaceFragment(Settings.getIntSettings("record_mode", 1));
        }
    }

    public /* synthetic */ void lambda$updateTabSelected$3$ModePager() {
        if (this.mMainTabLayout != null) {
            Log.m19d(TAG, "updateTabSelected");
            this.mMainTabLayout.clearFocus();
            View childAt = ((ViewGroup) this.mMainTabLayout.getChildAt(0)).getChildAt(getTabPosition());
            if (childAt != null) {
                this.mCurrentTabSelected = getTabPosition();
                updateTabViewStyle(this.mCurrentTabSelected);
                scrollToSelectedTab(childAt);
            }
        }
    }

    public void scrollToSelectedTab(View view) {
        if (view != null) {
            this.mMainTabLayout.smoothScrollTo((view.getLeft() + (view.getWidth() / 2)) - (getWindowWidth(this.mVNMainActivity) / 2), view.getTop());
        }
    }

    private int getWindowWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public void setPagingEnabled(boolean z) {
        View childAt;
        HorizontalScrollView horizontalScrollView = this.mMainTabLayout;
        if (horizontalScrollView != null && (childAt = ((ViewGroup) horizontalScrollView.getChildAt(0)).getChildAt(this.mCurrentTabSelected)) != null) {
            childAt.setEnabled(z);
            if (!z) {
                childAt.setAlpha(0.6f);
            } else {
                childAt.setAlpha(1.0f);
            }
        }
    }

    public void update(Observable observable, Object obj) {
        int tabPosition;
        int intValue = ((Integer) obj).intValue();
        if (intValue == 21) {
            setSpaceTab();
        } else if (intValue == 1998) {
            if (mInstance == null) {
                Log.m22e(TAG, "update - instance is not created yet");
            } else if (this.mActivity == null) {
                Log.m22e(TAG, "update - mActivity is null");
            } else if (this.mMainTabLayout != null && (tabPosition = getTabPosition()) != this.mCurrentTabSelected) {
                this.mCurrentTabSelected = tabPosition;
                updateTabSelected();
            }
        }
    }

    private int getTabPosition() {
        int i;
        int intSettings = Settings.getIntSettings("record_mode", 1);
        Log.m19d(TAG, "getTabPosition() - mode = " + intSettings);
        if (intSettings == 6) {
            return 0;
        }
        if (mRecordModes != null) {
            i = 0;
            for (int i2 = 0; i2 < mRecordModes.size(); i2++) {
                i = mRecordModes.keyAt(i2);
                if (mRecordModes.get(i) == intSettings) {
                    break;
                }
            }
        } else {
            i = 0;
        }
        Log.m19d(TAG, "pos = " + i);
        return i;
    }

    /* renamed from: com.sec.android.app.voicenote.ui.pager.ModePager$PagerFactory */
    private static class PagerFactory {
        private static final String TAG = "PagerFactory";
        private static SparseArray<AbsPagerFragment> mMap = new SparseArray<>();

        private PagerFactory() {
        }

        public static AbsPagerFragment get(int i) {
            return mMap.get(i);
        }

        private static void put(int i, AbsPagerFragment absPagerFragment) {
            if (absPagerFragment != null) {
                mMap.put(i, absPagerFragment);
            }
        }

        public static void remove(int i) {
            mMap.remove(i);
        }

        static void removeAll() {
            mMap.clear();
        }

        public static AbsPagerFragment create(int i) {
            if (get(i) == null) {
                return createPagerFragment(i);
            }
            return get(i);
        }

        private static AbsPagerFragment createPagerFragment(int i) {
            AbsPagerFragment absPagerFragment;
            Log.m29v(TAG, "createPagerFragment - mode: " + i);
            if (i == 1) {
                absPagerFragment = new PagerNormalFragment();
            } else if (i == 2) {
                absPagerFragment = new PagerInterviewFragment();
            } else if (i != 4) {
                absPagerFragment = null;
            } else {
                absPagerFragment = new PagerVoiceMemoFragment();
            }
            if (absPagerFragment != null) {
                put(i, absPagerFragment);
            }
            return absPagerFragment;
        }
    }

    private void checkModeNotSupported(int i) {
        if ((Engine.getInstance().isWiredHeadSetConnected() || Engine.getInstance().isBluetoothSCOConnected()) && i != 1) {
            DialogFactory.clearDialogByTag(this.mActivity.getSupportFragmentManager(), DialogFactory.MODE_NOT_SUPPORTED);
            DialogFactory.show(this.mActivity.getSupportFragmentManager(), DialogFactory.MODE_NOT_SUPPORTED, (Bundle) null);
        }
    }

    private void showDataCheckDialog() {
        Bundle bundle = new Bundle();
        bundle.putInt(DialogFactory.BUNDLE_REQUEST_CODE, 8);
        bundle.putInt(DialogFactory.BUNDLE_DATA_CHECK_MODULE, 0);
        DialogFactory.show(this.mActivity.getSupportFragmentManager(), DialogFactory.DATA_CHECK_DIALOG, bundle, this);
    }

    public void onDialogResult(DialogFragment dialogFragment, Bundle bundle) {
        if (bundle != null) {
            bundle.getInt(DialogFactory.BUNDLE_REQUEST_CODE);
            bundle.getInt("result_code");
        }
    }

    private int getTabCount() {
        int i = !VoiceNoteFeature.FLAG_SUPPORT_INTERVIEW ? 2 : 3;
        if (!VoiceNoteFeature.FLAG_SUPPORT_VOICE_MEMO(this.mActivity)) {
            i--;
        }
        if (i <= 1 || !DesktopModeProvider.isDesktopMode()) {
            return i;
        }
        return 1;
    }

    public void enterRecordModesDataInDex() {
        Log.m19d(TAG, "enterRecordModesDataInDex");
        mRecordModes.clear();
        mRecordModes.put(0, 1);
    }

    public void setShowHelpModeGuide(boolean z) {
        this.mIsShowingHelpModeGuide = z;
    }

    public boolean getShowHelpModeGuide() {
        return this.mIsShowingHelpModeGuide;
    }

    public int getMainTabHeight() {
        return getDefaultTabHeight();
    }

    private int getDefaultTabHeight() {
        int i;
        if (DisplayManager.isInMultiWindowMode(this.mActivity)) {
            i = this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.multiwindow_main_tab_height);
        } else {
            i = this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.main_tab_height);
        }
        int dimensionPixelSize = this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.main_tab_layout_height) + this.mActivity.getResources().getDimensionPixelSize(C0690R.dimen.main_sub_tab_padding);
        Log.m19d(TAG, "defaultHeight = " + i + ", newHeight = " + dimensionPixelSize);
        return i < dimensionPixelSize ? dimensionPixelSize : i;
    }
}
