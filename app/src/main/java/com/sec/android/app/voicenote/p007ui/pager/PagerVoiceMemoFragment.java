package com.sec.android.app.voicenote.p007ui.pager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
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
import com.sec.android.app.voicenote.activity.SelectLanguageActivity;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.provider.AssistantProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.provider.ViewProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

/* renamed from: com.sec.android.app.voicenote.ui.pager.PagerVoiceMemoFragment */
public class PagerVoiceMemoFragment extends AbsPagerFragment implements Observer {
    private static final String TAG = "PagerVoiceMemoFragment";
    private TextView mDescriptionTextView;
    private IdleWaveStandard mIdleWave;
    private LinearLayout mLayoutStt;
    private TextView mSelectLanguageButton;
    private String[] mSttDefaultLocales;
    private String[] mSttLocales;
    private String[] mSttTexts;
    private LinearLayout mViewInfoDescription;
    private LinearLayout mViewStt;
    private View mViewStt01;
    private View mViewStt02;
    private View mViewStt03;
    private View mViewStt04;

    static /* synthetic */ boolean lambda$showHelpModeGuide$3(View view, MotionEvent motionEvent) {
        return true;
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        VoiceNoteObservable.getInstance().addObserver(this);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Log.m26i(TAG, "onCreateView");
        View inflate = layoutInflater.inflate(C0690R.layout.fragment_pager_voicememo, viewGroup, false);
        this.mDescriptionTextView = (TextView) inflate.findViewById(C0690R.C0693id.stt_description);
        this.mSelectLanguageButton = (TextView) inflate.findViewById(C0690R.C0693id.stt_language_button);
        this.mViewInfoDescription = (LinearLayout) inflate.findViewById(C0690R.C0693id.view_info_description);
        this.mLayoutStt = (LinearLayout) inflate.findViewById(C0690R.C0693id.layout_stt);
        this.mIdleWave = (IdleWaveStandard) inflate.findViewById(C0690R.C0693id.idle_wave_stt);
        this.mViewStt01 = inflate.findViewById(C0690R.C0693id.view_stt_1);
        this.mViewStt02 = inflate.findViewById(C0690R.C0693id.view_stt_2);
        this.mViewStt03 = inflate.findViewById(C0690R.C0693id.view_stt_3);
        this.mViewStt04 = inflate.findViewById(C0690R.C0693id.view_stt_4);
        this.mViewStt = (LinearLayout) inflate.findViewById(C0690R.C0693id.view_stt);
        getVoiceMemoStringArrays();
        String stringSettings = Settings.getStringSettings(Settings.KEY_STT_LANGUAGE_TEXT);
        if (stringSettings == null) {
            stringSettings = getDefaultLanguage();
        }
        SurveyLogProvider.insertStatusLog(SurveyLogProvider.SURVEY_LANGUAGE, stringSettings, -1);
        if (this.mSelectLanguageButton != null) {
            if (getResources().getConfiguration().getLayoutDirection() == 1) {
                this.mSelectLanguageButton.setBackgroundResource(C0690R.C0692drawable.voice_ripple_stt_language_btn_rtl);
            } else {
                this.mSelectLanguageButton.setBackgroundResource(C0690R.C0692drawable.voice_ripple_stt_language_btn);
            }
            this.mSelectLanguageButton.setText(stringSettings);
            this.mSelectLanguageButton.setContentDescription(AssistantProvider.getInstance().getContentDesToButton(stringSettings));
            this.mSelectLanguageButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PagerVoiceMemoFragment.this.lambda$onCreateView$0$PagerVoiceMemoFragment(view);
                }
            });
        }
        ViewProvider.setMaxFontSize(getContext(), this.mDescriptionTextView);
        ViewProvider.setMaxFontSize(getContext(), this.mSelectLanguageButton);
        return inflate;
    }

    public /* synthetic */ void lambda$onCreateView$0$PagerVoiceMemoFragment(View view) {
        openRecordingLanguageActivity();
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
            this.mSelectLanguageButton.setText(Settings.getStringSettings(Settings.KEY_STT_LANGUAGE_TEXT));
            this.mSelectLanguageButton.setContentDescription(AssistantProvider.getInstance().getContentDesToButton(Settings.getStringSettings(Settings.KEY_STT_LANGUAGE_TEXT)));
            this.mRootViewTmp = getActivity().getWindow().getDecorView();
            setMoreTextView();
        }
    }

    private void setMoreTextView() {
        Resources resources = getActivity().getResources();
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(resources.getColor(C0690R.C0691color.more_btn, (Resources.Theme) null));
        final String string = resources.getString(C0690R.string.pager_voice_memo_description, new Object[]{10});
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
                    SALogProvider.insertSALog(PagerVoiceMemoFragment.this.getActivity().getResources().getString(C0690R.string.screen_ready_STT), PagerVoiceMemoFragment.this.getActivity().getResources().getString(C0690R.string.event_STT_help));
                    PagerVoiceMemoFragment.this.showHelpModeGuide();
                }
            }
        });
        this.mDescriptionTextView.setOnTouchListener(new View.OnTouchListener() {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = string;
            }

            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return PagerVoiceMemoFragment.this.lambda$setMoreTextView$1$PagerVoiceMemoFragment(this.f$1, view, motionEvent);
            }
        });
    }

    public /* synthetic */ boolean lambda$setMoreTextView$1$PagerVoiceMemoFragment(String str, View view, MotionEvent motionEvent) {
        if (getActivity() == null) {
            return true;
        }
        if (motionEvent.getAction() != 1) {
            return false;
        }
        if (this.mDescriptionTextView.getOffsetForPosition(motionEvent.getX(), motionEvent.getY()) <= str.length() + 2) {
            return false;
        }
        SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_ready_STT), getActivity().getResources().getString(C0690R.string.event_STT_help));
        showHelpModeGuide();
        return false;
    }

    private void getVoiceMemoStringArrays() {
        this.mSttDefaultLocales = getResources().getStringArray(C0690R.array.stt_language_default_locale);
        this.mSttLocales = getResources().getStringArray(C0690R.array.stt_language_locale);
        this.mSttTexts = getResources().getStringArray(C0690R.array.stt_language_text);
    }

    private String getDefaultLanguage() {
        String locale = Locale.getDefault().toString();
        int i = 0;
        while (true) {
            String[] strArr = this.mSttDefaultLocales;
            if (i >= strArr.length) {
                int integer = getResources().getInteger(C0690R.integer.common_default_locale_index);
                Settings.setSettings(Settings.KEY_STT_LANGUAGE_TEXT, this.mSttTexts[integer]);
                Settings.setSettings(Settings.KEY_STT_LANGUAGE_LOCALE, this.mSttLocales[integer]);
                return this.mSttTexts[integer];
            } else if (locale.contains(strArr[i])) {
                Settings.setSettings(Settings.KEY_STT_LANGUAGE_TEXT, this.mSttTexts[i]);
                Settings.setSettings(Settings.KEY_STT_LANGUAGE_LOCALE, this.mSttLocales[i]);
                return this.mSttTexts[i];
            } else {
                i++;
            }
        }
    }

    private void openRecordingLanguageActivity() {
        if (VoiceNoteApplication.getScene() == 1 || VoiceNoteApplication.getScene() == 11) {
            try {
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    activity.startActivity(new Intent(activity, SelectLanguageActivity.class));
                    SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_ready_STT), getActivity().getResources().getString(C0690R.string.event_language));
                }
            } catch (ActivityNotFoundException e) {
                Log.m24e(TAG, "SelectLanguageActivity not found", (Throwable) e);
            }
        }
    }

    public void onDestroyView() {
        Log.m26i(TAG, "onDestroyView");
        this.mSttDefaultLocales = null;
        this.mSttLocales = null;
        this.mSttTexts = null;
        super.onDestroyView();
    }

    public void onDestroy() {
        VoiceNoteObservable.getInstance().deleteObserver(this);
        super.onDestroy();
    }

    /* access modifiers changed from: package-private */
    @SuppressLint({"StringFormatMatches"})
    public void showHelpModeGuide() {
        Log.m26i(TAG, "showHelpModeGuide");
        FragmentActivity activity = getActivity();
        if (activity != null) {
            Window window = activity.getWindow();
            View findViewById = window.getDecorView().findViewById(C0690R.C0693id.main_activity_root_view);
            View findViewById2 = findViewById.findViewById(C0690R.C0693id.help_stt_mode);
//            findViewById.getRootView().semSetRoundedCorners(0);
            if (findViewById2 == null) {
                Log.m26i(TAG, "showHelpModeGuide add view");
                blockDescendantsForIdleScene(window.getDecorView(), true);
                ModePager.getInstance().setShowHelpModeGuide(true);
                int statusBarColor = window.getStatusBarColor();
                int navigationBarColor = window.getNavigationBarColor();
                window.setStatusBarColor(activity.getResources().getColor(C0690R.C0691color.mode_help_bg, (Resources.Theme) null));
                window.setNavigationBarColor(activity.getResources().getColor(C0690R.C0691color.mode_help_bg, (Resources.Theme) null));
                window.addFlags(2);
                ViewGroup viewGroup = (ViewGroup) findViewById;
                View inflate = activity.getLayoutInflater().inflate(C0690R.layout.help_stt_mode_guide, viewGroup, false);
                ImageView imageView = (ImageView) inflate.findViewById(C0690R.C0693id.stt_guide_image);
                if (VoiceNoteFeature.FLAG_IS_FOLDER_PHONE(activity)) {
                    imageView.setImageResource(C0690R.C0692drawable.voice_recorder_help_stt_mode_for_folder_phone);
                } else if (VoiceNoteFeature.FLAG_IS_TABLET) {
                    imageView.setImageResource(C0690R.C0692drawable.ic_voice_recorder_help_stt_mode_tablet);
                } else {
                    imageView.setImageResource(C0690R.C0692drawable.ic_voice_recorder_help_stt_mode);
                }
                viewGroup.addView(inflate);
                TextView textView = (TextView) inflate.findViewById(C0690R.C0693id.stt_description_text);
                if (textView != null) {
                    if (VoiceNoteFeature.FLAG_IS_TABLET) {
                        textView.setText(activity.getString(C0690R.string.help_stt_description_text_tablet, new Object[]{20}));
                    } else {
                        textView.setText(activity.getString(C0690R.string.help_stt_description_text, new Object[]{8}));
                    }
                }
//                ((Button) inflate.findViewById(C0690R.C0693id.help_stt_ok_button)).setOnClickListener(new View.OnClickListener(window, statusBarColor, navigationBarColor, findViewById, inflate) {
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
//                        PagerVoiceMemoFragment.this.lambda$showHelpModeGuide$2$PagerVoiceMemoFragment(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, view);
//                    }
//                });
                inflate.setOnTouchListener($$Lambda$PagerVoiceMemoFragment$jYVMbhZ5B1cUaP2LMzsIcgvrSOs.INSTANCE);
                if (DisplayManager.isInMultiWindowMode(activity)) {
                    updateHelpGuideLayoutForMultiWindow();
                }
            }
        }
    }

    public /* synthetic */ void lambda$showHelpModeGuide$2$PagerVoiceMemoFragment(Window window, int i, int i2, View view, View view2, View view3) {
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
        FrameLayout frameLayout = (FrameLayout) view.findViewById(C0690R.C0693id.help_stt_ok_button_layout);
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
        FrameLayout frameLayout = (FrameLayout) view.findViewById(C0690R.C0693id.stt_description_text_layout);
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
        if (activity != null && (button = (Button) activity.getWindow().getDecorView().findViewById(C0690R.C0693id.help_stt_ok_button)) != null) {
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
        int i;
        int i2;
        int i3;
        FragmentActivity activity = getActivity();
        if (activity != null && DisplayManager.isInMultiWindowMode(activity)) {
            Resources resources = activity.getResources();
            int multiWindowCurrentAppHeight = DisplayManager.getMultiWindowCurrentAppHeight(activity);
            int actionBarHeight = DisplayManager.getActionBarHeight(activity);
            int dimensionPixelSize = resources.getDimensionPixelSize(C0690R.dimen.main_tab_list_margin_top);
            int mainTabHeight = ModePager.getInstance().getMainTabHeight();
            int dimensionPixelSize2 = resources.getDimensionPixelSize(C0690R.dimen.multi_window_stt_min_height);
            int dimensionPixelSize3 = resources.getDimensionPixelSize(C0690R.dimen.multi_idle_tab_description_min_height);
            int dimensionPixelSize4 = resources.getDimensionPixelSize(C0690R.dimen.multi_idle_wave_min_height);
            int dimensionPixelSize5 = resources.getDimensionPixelSize(C0690R.dimen.multi_idle_control_btn_min_height);
            int i4 = dimensionPixelSize2 + dimensionPixelSize5;
            int i5 = (multiWindowCurrentAppHeight - actionBarHeight) - dimensionPixelSize;
            int i6 = dimensionPixelSize3 + dimensionPixelSize4 + i4;
            if (i5 >= i6) {
                z = true;
            } else {
                i6 -= dimensionPixelSize3 - mainTabHeight;
                z = i5 >= i6 ? true : true;
            }
            int i7 = 0;
            if (z) {
                this.mViewInfoDescription.setVisibility(0);
                this.mDescriptionTextView.setVisibility(0);
                this.mIdleWave.setVisibility(0);
                int i8 = (int) (((float) i5) * 0.2f);
                int i9 = dimensionPixelSize3 - mainTabHeight;
                int i10 = i8 - mainTabHeight;
                if (i10 < i9) {
                    i8 = dimensionPixelSize3;
                } else {
                    i9 = i10;
                }
                int waveHeight = getWaveHeight(i5, 0.2f, dimensionPixelSize4);
                int i11 = (i5 - i8) - waveHeight;
                if (i11 < i4) {
                    int i12 = i4 - i11;
                    if (waveHeight > dimensionPixelSize4) {
                        int i13 = waveHeight - dimensionPixelSize4;
                        if (i13 >= i12) {
                            dimensionPixelSize4 = waveHeight - i12;
                        } else {
                            i7 = i12 - i13;
                        }
                        waveHeight = dimensionPixelSize4;
                    } else {
                        i7 = i12;
                    }
                    if (i7 > 0) {
                        i9 -= i7;
                    }
                    i2 = i9;
                    i = waveHeight;
                    i11 = i4;
                } else {
                    i2 = i9;
                    i = waveHeight;
                }
                updateViewSttMultiWindow(resources, i11, i4, dimensionPixelSize5, dimensionPixelSize2);
                updateViewHeight(this.mViewInfoDescription, i2);
                updateViewHeight(this.mIdleWave, i);
            } else if (z) {
                int dimensionPixelSize6 = resources.getDimensionPixelSize(C0690R.dimen.multi_idle_tab_margin_bottom);
                if (i5 - dimensionPixelSize6 < i6) {
                    hideIdleWaveMultiWindow(resources, i5 - mainTabHeight, dimensionPixelSize5, dimensionPixelSize2);
                    return;
                }
                this.mViewInfoDescription.setVisibility(0);
                this.mDescriptionTextView.setVisibility(8);
                this.mIdleWave.setVisibility(0);
                int waveHeight2 = getWaveHeight(i5, 0.2f, dimensionPixelSize4);
                int i14 = ((i5 - waveHeight2) - mainTabHeight) - dimensionPixelSize6;
                if (i14 < i4) {
                    i3 = waveHeight2 - (i4 - i14);
                    i14 = i4;
                } else {
                    i3 = waveHeight2;
                }
                updateViewSttMultiWindow(resources, i14, i4, dimensionPixelSize5, dimensionPixelSize2);
                updateViewHeight(this.mViewInfoDescription, dimensionPixelSize6);
                updateViewHeight(this.mIdleWave, i3);
            } else if (z) {
                hideIdleWaveMultiWindow(resources, i5 - mainTabHeight, dimensionPixelSize5, dimensionPixelSize2);
            }
        }
    }

    private void updateViewSttMultiWindow(Resources resources, int i, int i2, int i3, int i4) {
        int i5 = 0;
        if (i != i2) {
            int dimensionPixelSize = resources.getDimensionPixelSize(C0690R.dimen.stt_language_button_top_margin_pager);
            int i6 = (int) (((float) i) * 0.36f);
            int i7 = i6 + dimensionPixelSize + i3;
            if (i < i7) {
                int i8 = i7 - i;
                if (i8 <= dimensionPixelSize) {
                    dimensionPixelSize -= i8;
                } else {
                    i6 -= i8 - dimensionPixelSize;
                    i4 = i6;
                }
            }
            i5 = dimensionPixelSize;
            i4 = i6;
        }
        updateLinearViewHeight(resources, this.mViewStt, i4, i5);
    }

    private void hideIdleWaveMultiWindow(Resources resources, int i, int i2, int i3) {
        this.mViewInfoDescription.setVisibility(8);
        this.mIdleWave.setVisibility(8);
        int i4 = (int) (((float) i) * 0.36f);
        if (i - i4 >= i2) {
            i3 = i4;
        }
        updateLinearViewHeight(resources, this.mViewStt, i3, 0);
    }

    private void updateLinearViewHeight(Resources resources, View view, int i, int i2) {
        if (view != null) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
            layoutParams.height = i;
            layoutParams.topMargin = i2;
            if (VoiceNoteFeature.FLAG_IS_TABLET) {
                layoutParams.leftMargin = resources.getDimensionPixelSize(C0690R.dimen.multi_window_tablet_stt_margin_left_right);
                layoutParams.rightMargin = resources.getDimensionPixelSize(C0690R.dimen.multi_window_tablet_stt_margin_left_right);
            }
            view.setLayoutParams(layoutParams);
        }
    }
}
