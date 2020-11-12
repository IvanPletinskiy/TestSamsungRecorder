package com.sec.android.app.voicenote.p007ui.pager;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.ViewProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.util.Observable;
import java.util.Observer;

/* renamed from: com.sec.android.app.voicenote.ui.pager.PagerInterviewFragment */
public class PagerInterviewFragment extends AbsPagerFragment implements Observer {
    private static final String TAG = "PagerInterviewFragment";
    private TextView mDescriptionTextView;
    private IdleWaveInterview mIdleWave;
    private LinearLayout mLayoutInterview;
    private LinearLayout mViewInfoDescription;

    static /* synthetic */ boolean lambda$showHelpModeGuide$2(View view, MotionEvent motionEvent) {
        return true;
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        VoiceNoteObservable.getInstance().addObserver(this);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Log.m26i(TAG, "onCreateView");
        View inflate = layoutInflater.inflate(C0690R.layout.fragment_pager_interview, viewGroup, false);
        this.mDescriptionTextView = (TextView) inflate.findViewById(C0690R.C0693id.interview_description);
        this.mLayoutInterview = (LinearLayout) inflate.findViewById(C0690R.C0693id.layout_interview);
        this.mViewInfoDescription = (LinearLayout) inflate.findViewById(C0690R.C0693id.view_info_description);
        this.mIdleWave = (IdleWaveInterview) inflate.findViewById(C0690R.C0693id.idle_wave_interview_layout);
        ViewProvider.setMaxFontSize(getContext(), this.mDescriptionTextView);
        return inflate;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (ModePager.getInstance().getShowHelpModeGuide()) {
            showHelpModeGuide();
        }
        updateMultiWindowLayoutView();
    }

    public void onResume() {
        Log.m26i(TAG, "onResume");
        super.onResume();
        if (getActivity() != null) {
            this.mRootViewTmp = getActivity().getWindow().getDecorView();
            setMoreTextView();
        }
    }

    private void setMoreTextView() {
        Resources resources = getActivity().getResources();
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(resources.getColor(C0690R.C0691color.more_btn, (Resources.Theme) null));
        final String string = resources.getString(C0690R.string.pager_interview_description);
        String str = string + "  " + resources.getString(C0690R.string.more);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
        spannableStringBuilder.setSpan(foregroundColorSpan, string.length() + 2, str.length(), 33);
        spannableStringBuilder.setSpan(new UnderlineSpan(), string.length() + 2, str.length(), 0);
        this.mDescriptionTextView.setText(spannableStringBuilder);
        this.mDescriptionTextView.setMovementMethod(new ScrollingMovementMethod());
        this.mDescriptionTextView.setAccessibilityDelegate(new View.AccessibilityDelegate() {
            public void onPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
                super.onPopulateAccessibilityEvent(view, accessibilityEvent);
                if (accessibilityEvent.getEventType() == 1) {
                    SALogProvider.insertSALog(PagerInterviewFragment.this.getActivity().getResources().getString(C0690R.string.screen_ready_STT), PagerInterviewFragment.this.getActivity().getResources().getString(C0690R.string.event_STT_help));
                    PagerInterviewFragment.this.showHelpModeGuide();
                }
            }
        });
        this.mDescriptionTextView.setOnTouchListener(new View.OnTouchListener() {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = string;
            }

            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return PagerInterviewFragment.this.lambda$setMoreTextView$0$PagerInterviewFragment(this.f$1, view, motionEvent);
            }
        });
    }

    public /* synthetic */ boolean lambda$setMoreTextView$0$PagerInterviewFragment(String str, View view, MotionEvent motionEvent) {
        if (getActivity() == null) {
            return true;
        }
        if (motionEvent.getAction() != 1) {
            return false;
        }
        if (this.mDescriptionTextView.getOffsetForPosition(motionEvent.getX(), motionEvent.getY()) <= str.length() + 2) {
            return false;
        }
        SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_ready_interview), getActivity().getResources().getString(C0690R.string.event_interview_help));
        showHelpModeGuide();
        return false;
    }

    public void onDestroyView() {
        Log.m26i(TAG, "onDestroyView");
        super.onDestroyView();
    }

    public void onDestroy() {
        VoiceNoteObservable.getInstance().deleteObserver(this);
        super.onDestroy();
    }

    /* access modifiers changed from: package-private */
    public void showHelpModeGuide() {
        Log.m26i(TAG, "showHelpModeGuide");
        FragmentActivity activity = getActivity();
        if (activity != null) {
            Window window = activity.getWindow();
            View findViewById = window.getDecorView().findViewById(C0690R.C0693id.main_activity_root_view);
//            findViewById.getRootView().semSetRoundedCorners(0);
            if (findViewById.findViewById(C0690R.C0693id.help_interview_mode) == null) {
                Log.m26i(TAG, "showHelpModeGuide add view");
                blockDescendantsForIdleScene(window.getDecorView(), true);
                ModePager.getInstance().setShowHelpModeGuide(true);
                int statusBarColor = window.getStatusBarColor();
                int navigationBarColor = window.getNavigationBarColor();
                window.setStatusBarColor(activity.getResources().getColor(C0690R.C0691color.mode_help_bg, (Resources.Theme) null));
                window.setNavigationBarColor(activity.getResources().getColor(C0690R.C0691color.mode_help_bg, (Resources.Theme) null));
                window.addFlags(2);
                ViewGroup viewGroup = (ViewGroup) findViewById;
                View inflate = activity.getLayoutInflater().inflate(C0690R.layout.help_interview_mode_guide, viewGroup, false);
                ImageView imageView = (ImageView) inflate.findViewById(C0690R.C0693id.interview_guide_image);
                TextView textView = (TextView) inflate.findViewById(C0690R.C0693id.interview_guide_description);
                if (VoiceNoteFeature.FLAG_IS_TABLET) {
                    imageView.setImageResource(C0690R.C0692drawable.ic_voice_recorder_help_interview_mode_tablet);
                    textView.setText(C0690R.string.help_interview_description_text_tablet);
                } else {
                    imageView.setImageResource(C0690R.C0692drawable.ic_voice_recorder_help_interview_mode);
                    textView.setText(C0690R.string.help_interview_description_text);
                }
                viewGroup.addView(inflate);
//                ((Button) inflate.findViewById(C0690R.C0693id.help_interview_ok_button)).setOnClickListener(new View.OnClickListener(window, statusBarColor, navigationBarColor, findViewById, inflate) {
//                    private final /* synthetic */ Window f$1;
//                    private final /* synthetic */ int f$2;
//                    private final /* synthetic */ int f$3;
//                    private final /* synthetic */ View f$4;
//                    private final /* synthetic */ View f$5;
//
//                    {
//                        this.f$1 = r2;
//                        this.f$2 = r3;
//                        this.f$3 = r4;
//                        this.f$4 = r5;
//                        this.f$5 = r6;
//                    }
//
//                    public final void onClick(View view) {
//                        PagerInterviewFragment.this.lambda$showHelpModeGuide$1$PagerInterviewFragment(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, view);
//                    }
//                });
                inflate.setOnTouchListener($$Lambda$PagerInterviewFragment$q42RAHS29pXN3OQes_CHxD3uRk.INSTANCE);
                if (DisplayManager.isInMultiWindowMode(activity)) {
                    updateHelpGuideLayoutForMultiWindow();
                }
            }
        }
    }

    public /* synthetic */ void lambda$showHelpModeGuide$1$PagerInterviewFragment(Window window, int i, int i2, View view, View view2, View view3) {
        if (getActivity() != null) {
            ModePager.getInstance().setShowHelpModeGuide(false);
            window.setStatusBarColor(i);
            window.setNavigationBarColor(i2);
            window.addFlags(1);
//            view.getRootView().semSetRoundedCorners(12);
            ((ViewGroup) view).removeView(view2);
            blockDescendantsForIdleScene(window.getDecorView(), false);
            setMoreTextView();
        }
    }

    private void updateHelpGuideLayoutForMultiWindow() {
        View decorView;
        FragmentActivity activity = getActivity();
        if (activity != null && (decorView = activity.getWindow().getDecorView()) != null) {
            updateButtonMargin(activity, decorView);
            if (DisplayManager.isCurrentWindowOnLandscape(activity)) {
                updateMarginLeftRightLandscape(activity, decorView);
            }
        }
    }

    private void updateButtonMargin(Activity activity, View view) {
        int i;
        FrameLayout frameLayout = (FrameLayout) view.findViewById(C0690R.C0693id.help_interview_ok_button_layout);
        if (frameLayout != null) {
            int currentScreenHeight = DisplayManager.getCurrentScreenHeight(activity);
            if (DisplayManager.isCurrentWindowOnLandscape(activity)) {
                if (DisplayManager.getFullScreenWidth() < DisplayManager.getFullScreenHeight()) {
                    i = DisplayManager.getFullScreenWidth();
                } else {
                    i = DisplayManager.getFullScreenHeight();
                }
            } else if (DisplayManager.getFullScreenWidth() > DisplayManager.getFullScreenHeight()) {
                i = DisplayManager.getFullScreenWidth();
            } else {
                i = DisplayManager.getFullScreenHeight();
            }
            if (DisplayManager.getMultiwindowMode() == 1 || DisplayManager.isInDeXExternalMonitor(activity)) {
                currentScreenHeight -= activity.getResources().getDimensionPixelSize(C0690R.dimen.multi_popup_caption_height);
            }
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
            layoutParams.topMargin = (activity.getResources().getDimensionPixelSize(C0690R.dimen.help_mode_ok_button_top_margin) * currentScreenHeight) / i;
            frameLayout.setLayoutParams(layoutParams);
        }
    }

    private void updateMarginLeftRightLandscape(Activity activity, View view) {
        int i;
        LinearLayout linearLayout = (LinearLayout) view.findViewById(C0690R.C0693id.content_guide_layout);
        FrameLayout frameLayout = (FrameLayout) view.findViewById(C0690R.C0693id.interview_description_text_layout);
        if (linearLayout != null && frameLayout != null) {
            int currentScreenWidth = DisplayManager.getCurrentScreenWidth(activity);
            if (DisplayManager.getFullScreenWidth() > DisplayManager.getFullScreenHeight()) {
                i = DisplayManager.getFullScreenWidth();
            } else {
                i = DisplayManager.getFullScreenHeight();
            }
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            layoutParams.leftMargin = (activity.getResources().getDimensionPixelSize(C0690R.dimen.item_padding_left) * currentScreenWidth) / i;
            layoutParams.rightMargin = (activity.getResources().getDimensionPixelSize(C0690R.dimen.item_padding_right) * currentScreenWidth) / i;
            linearLayout.setLayoutParams(layoutParams);
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
            layoutParams2.leftMargin = (activity.getResources().getDimensionPixelSize(C0690R.dimen.padding_between_textview_imageview) * currentScreenWidth) / i;
            frameLayout.setLayoutParams(layoutParams2);
        }
    }

    /* access modifiers changed from: package-private */
    public void hideHelpModeGuide() {
        Button button;
        FragmentActivity activity = getActivity();
        if (activity != null && (button = (Button) activity.getWindow().getDecorView().findViewById(C0690R.C0693id.help_interview_ok_button)) != null) {
            button.performClick();
        }
    }

    public void update(Observable observable, Object obj) {
        int intValue = ((Integer) obj).intValue();
        Log.m26i(TAG, "update - data : " + intValue);
        if (intValue == 21) {
            Log.m26i(TAG, "update - screen size view");
            updateMultiWindowLayoutView();
            updateHelpGuideLayoutForMultiWindow();
        } else if (intValue == 22) {
            hideHelpModeGuide();
        }
    }

    private void updateMultiWindowLayoutView() {
        boolean z;
        FragmentActivity activity = getActivity();
        if (activity != null && DisplayManager.isInMultiWindowMode(activity)) {
            Resources resources = activity.getResources();
            int multiWindowCurrentAppHeight = DisplayManager.getMultiWindowCurrentAppHeight(activity);
            int actionBarHeight = DisplayManager.getActionBarHeight(activity);
            int mainTabHeight = ModePager.getInstance().getMainTabHeight();
            int dimensionPixelSize = resources.getDimensionPixelSize(C0690R.dimen.main_tab_list_margin_top);
            int dimensionPixelSize2 = resources.getDimensionPixelSize(C0690R.dimen.multi_idle_tab_description_min_height);
            int dimensionPixelSize3 = resources.getDimensionPixelSize(C0690R.dimen.multi_idle_wave_min_height);
            int dimensionPixelSize4 = resources.getDimensionPixelSize(C0690R.dimen.multi_idle_control_btn_min_height);
            int i = (multiWindowCurrentAppHeight - actionBarHeight) - dimensionPixelSize;
            int i2 = dimensionPixelSize2 + dimensionPixelSize3 + dimensionPixelSize4;
            if (i >= i2) {
                z = true;
            } else {
                i2 -= dimensionPixelSize2 - mainTabHeight;
                z = i >= i2 ? true : true;
            }
            int i3 = 0;
            if (z) {
                this.mLayoutInterview.setVisibility(0);
                this.mDescriptionTextView.setVisibility(0);
                this.mIdleWave.setVisibility(0);
                int i4 = (int) (((float) i) * 0.2f);
                int i5 = i4 - mainTabHeight;
                int i6 = dimensionPixelSize2 - mainTabHeight;
                if (i5 < i6) {
                    i5 = i6;
                    i4 = dimensionPixelSize2;
                }
                int waveHeight = getWaveHeight(i, 0.35f, dimensionPixelSize3);
                int i7 = (i - i4) - waveHeight;
                if (i7 < dimensionPixelSize4) {
                    int i8 = dimensionPixelSize4 - i7;
                    if (waveHeight > dimensionPixelSize3) {
                        int i9 = waveHeight - dimensionPixelSize3;
                        if (i9 >= i8) {
                            dimensionPixelSize3 = waveHeight - i8;
                        } else {
                            i3 = i8 - i9;
                        }
                        waveHeight = dimensionPixelSize3;
                    } else {
                        i3 = i8;
                    }
                    if (i3 > 0) {
                        i5 -= i3;
                    }
                }
                updateViewHeight(this.mViewInfoDescription, i5);
                updateViewHeight(this.mIdleWave, waveHeight);
            } else if (z) {
                int dimensionPixelSize5 = resources.getDimensionPixelSize(C0690R.dimen.multi_idle_tab_margin_bottom);
                if (i - dimensionPixelSize5 < i2) {
                    this.mLayoutInterview.setVisibility(8);
                    return;
                }
                this.mLayoutInterview.setVisibility(0);
                this.mDescriptionTextView.setVisibility(8);
                this.mIdleWave.setVisibility(0);
                int waveHeight2 = getWaveHeight(i, 0.35f, dimensionPixelSize3);
                int i10 = (i - waveHeight2) - mainTabHeight;
                if (i10 < dimensionPixelSize4) {
                    waveHeight2 -= dimensionPixelSize4 - i10;
                }
                updateViewHeight(this.mViewInfoDescription, dimensionPixelSize5);
                updateViewHeight(this.mIdleWave, waveHeight2);
            } else if (z) {
                this.mLayoutInterview.setVisibility(8);
            }
        }
    }
}
