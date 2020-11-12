package com.sec.android.app.voicenote.p007ui.animation;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.LottieListener;
import com.airbnb.lottie.LottieTask;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.p007ui.view.ViewStateProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.Engine;

/* renamed from: com.sec.android.app.voicenote.ui.animation.AnimationFactory */
public class AnimationFactory {
    private static final int START_DELAY = 150;
    private static final String TAG = "AnimationFactory";
    private static Context mAppContext;

    public static void setApplicationContext(Context context) {
        mAppContext = context;
    }

    public static void changeButton(View view, View view2, boolean z) {
        if (view != null || view2 != null) {
            if (view == null) {
                showButton(view2, START_DELAY, z);
            } else if (view2 == null) {
                hideButton(view);
            } else if (view.getId() != view2.getId()) {
                if (view.getTag() != null) {
                    ((Animator) view.getTag()).cancel();
                    view.setTag((Object) null);
                }
                if (view2.getTag() != null) {
                    ((Animator) view2.getTag()).cancel();
                    view2.setTag((Object) null);
                }
                if (view.getId() == C0690R.C0693id.controlbutton_edit_record_start || view.getId() == C0690R.C0693id.controlbutton_edit_record_pause) {
                    if (view.getId() == C0690R.C0693id.controlbutton_edit_record_start) {
                        showButtonInternal(view2, true);
                        view2.requestFocus();
                        hideButtonInternal(view);
                        return;
                    }
                    if (Engine.getInstance().getPlayerState() == 3) {
                        showButtonInternal(view2, false);
                    } else {
                        showButtonInternal(view2, true);
                    }
                    view2.setVisibility(0);
                    hideButtonInternal(view);
                } else if (view.getId() == C0690R.C0693id.controlbutton_pre_play) {
                    showButtonInternal(view2, true);
                    view2.requestFocus();
                    hideButtonInternal(view);
                } else if (view2.getId() == C0690R.C0693id.controlbutton_play_pause || ((view.getId() == C0690R.C0693id.controlbutton_edit_play && view2.getId() == C0690R.C0693id.controlbutton_edit_pause) || view.getId() == C0690R.C0693id.simple_play_start || view.getId() == C0690R.C0693id.simple_record_play_start)) {
                    showAnimationButton(view, view2, C0690R.C0692drawable.voice_recorder_control_bar_ic_play_pause_anim);
                } else if (view.getId() == C0690R.C0693id.controlbutton_play_pause || view.getId() == C0690R.C0693id.controlbutton_edit_pause || view.getId() == C0690R.C0693id.simple_play_pause || view.getId() == C0690R.C0693id.simple_record_play_pause) {
                    showAnimationButton(view, view2, C0690R.C0692drawable.voice_recorder_control_bar_ic_pause_play_anim);
                } else if (view2.getId() == C0690R.C0693id.controlbutton_record_pause && (view.getId() == C0690R.C0693id.controlbutton_record_start || view.getId() == C0690R.C0693id.controlbutton_record_resume)) {
                    showAnimationRecordPauseButton(view, view2);
                } else if (view.getId() == C0690R.C0693id.controlbutton_record_pause) {
                    showAnimationRecordPauseButton(view, view2);
                } else {
                    hideButton(view);
                    if (view.getContentDescription() == view2.getContentDescription()) {
                        showButton(view2, 0, true);
                    } else {
                        showButton(view2, START_DELAY, true);
                    }
                }
            } else if (view2.getVisibility() != 0) {
                showButton(view2, START_DELAY, true);
            } else {
                Log.m26i(TAG, "changeButton old : " + view.getContentDescription() + " new : " + view2.getContentDescription());
                switch (view2.getId()) {
                    case C0690R.C0693id.controlbutton_edit_play:
                    case C0690R.C0693id.controlbutton_pre_play:
                        if (Engine.getInstance().getRecorderState() != 2) {
                            if (view2.getTag() != null) {
                                showButton(view2, START_DELAY, true);
                                return;
                            } else {
                                showButtonInternal(view2, true);
                                return;
                            }
                        } else if (view2.getTag() == null) {
                            showButtonInternal(view2, false);
                            return;
                        } else {
                            return;
                        }
                    case C0690R.C0693id.controlbutton_edit_record_start:
                    case C0690R.C0693id.controlbutton_record_resume:
                        if (view2.getTag() != null) {
                            ((Animator) view2.getTag()).cancel();
                            view2.setTag((Object) null);
                        }
                        if (Engine.getInstance().isEditRecordable()) {
                            showButtonInternal(view2, true);
                            return;
                        } else {
                            showButtonInternal(view2, false);
                            return;
                        }
                    case C0690R.C0693id.controlbutton_edit_trim_button:
                        if (Engine.getInstance().isTrimEnable() || Engine.getInstance().isDeleteEnable()) {
                            view2.setAlpha(1.0f);
                            view2.setEnabled(true);
                            view2.setFocusable(true);
                            return;
                        }
                        view2.setAlpha(0.2f);
                        view2.setEnabled(false);
                        view2.setFocusable(false);
                        return;
                    default:
                        return;
                }
            }
        }
    }

    private static void showAnimationRecordPauseButton(final View view, final View view2) {
        String str;
        Log.m26i(TAG, "showAnimationRecordPauseButton, oldView = " + view.getContentDescription() + ", newView = " + view2.getContentDescription());
        setRecordButtonDrawable(view2, true);
        showButtonInternal(view2, true);
        if (view2.getId() == C0690R.C0693id.controlbutton_record_pause) {
            str = DisplayManager.isDarkMode(mAppContext) ? "lotties/dark_rec_to_pause.json" : "lotties/light_rec_to_pause.json";
        } else {
            str = DisplayManager.isDarkMode(mAppContext) ? "lotties/dark_pause_to_rec.json" : "lotties/light_pause_to_rec.json";
        }
        LottieTask<LottieComposition> fromAsset = LottieCompositionFactory.fromAsset(mAppContext, str);
        fromAsset.addFailureListener(new LottieListener() {

            public final void onResult(Object obj) {
                AnimationFactory.lambda$showAnimationRecordPauseButton$0(view2, (Throwable) obj);
            }
        });
        fromAsset.addListener(new LottieListener() {
            public final void onResult(Object obj) {
                AnimationFactory.playAnimation(view2, (LottieComposition) obj);
            }
        });
        view2.requestFocus();
        hideButtonInternal(view);
    }

    static /* synthetic */ void lambda$showAnimationRecordPauseButton$0(View view, Throwable th) {
        Log.m22e(TAG, "Load lottie animation drawable failed, -> set Record/Pause button by image drawable");
        Log.m22e(TAG, th.getMessage());
        setRecordButtonDrawable(view, false);
    }

    /* access modifiers changed from: private */
    public static void playAnimation(final View view, LottieComposition lottieComposition) {
        LottieDrawable lottieDrawable = new LottieDrawable();
        lottieDrawable.setComposition(lottieComposition);
        view.setBackground((Drawable) null);
        ((ImageButton) view).setImageDrawable(lottieDrawable);
        lottieDrawable.playAnimation();
        Log.m26i(TAG, "Play animation for change button state");
        lottieDrawable.addAnimatorListener(new Animator.AnimatorListener() {
            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
                AnimationFactory.setRecordButtonDrawable(view, false);
            }
        });
    }

    /* access modifiers changed from: private */
    public static void setRecordButtonDrawable(View view, boolean z) {
        Drawable drawable;
        if (view.getId() == C0690R.C0693id.controlbutton_record_pause) {
            if (z) {
                drawable = getDrawable(C0690R.C0692drawable.voice_recorder_control_bar_ic_rec);
            } else {
                drawable = getDrawable(C0690R.C0692drawable.voice_recorder_control_bar_ic_pause);
            }
        } else if (z) {
            drawable = getDrawable(C0690R.C0692drawable.voice_recorder_control_bar_ic_pause);
        } else {
            drawable = getDrawable(C0690R.C0692drawable.voice_recorder_control_bar_ic_rec);
        }
        if (drawable != null) {
            view.setBackground(drawable);
            ((ImageButton) view).setImageDrawable((Drawable) null);
        }
    }

    private static Drawable getDrawable(int i) {
        return mAppContext.getDrawable(i);
    }

    private static void showAnimationButton(View view, View view2, int i) {
        showButtonInternal(view2, true);
        view2.requestFocus();
        view2.setBackgroundResource(i);
        ((AnimationDrawable) view2.getBackground()).start();
        hideButtonInternal(view);
    }

    /* access modifiers changed from: private */
    public static void showButtonInternal(View view, boolean z) {
        Log.m26i(TAG, "showButtonInternal, view = " + view.getContentDescription());
        if (z) {
            view.setAlpha(1.0f);
//            view.semSetHoverPopupType(1);
        } else {
            view.setAlpha(0.4f);
//            view.semSetHoverPopupType(0);
        }
        view.setVisibility(0);
        view.setEnabled(z);
        view.setFocusable(z);
    }

    /* access modifiers changed from: private */
    public static void hideButtonInternal(View view) {
        Log.m26i(TAG, "hideButtonInternal, view = " + view.getContentDescription());
        view.setVisibility(8);
        view.setAlpha(0.0f);
        view.setEnabled(false);
        view.setFocusable(false);
    }

    private static void showButton(final View view, int i, boolean z) {
        Animator animator = (Animator) view.getTag();
        if (animator != null) {
            animator.end();
        }
        Log.m26i(TAG, "showButton button : " + view.getContentDescription());
        int id = view.getId();
        int recorderState = Engine.getInstance().getRecorderState();
        int i2 = C0690R.animator.ani_button_show_disable;
        if (!(recorderState == 2 && (id == C0690R.C0693id.controlbutton_pre_play || id == C0690R.C0693id.controlbutton_edit_play)) && ((!(id == C0690R.C0693id.controlbutton_record_resume || id == C0690R.C0693id.controlbutton_edit_record_start) || Engine.getInstance().isEditRecordable()) && ((id != C0690R.C0693id.controlbutton_edit_trim_button || Engine.getInstance().isTrimEnable() || Engine.getInstance().isDeleteEnable()) && (!(id == C0690R.C0693id.controlbutton_translation_resume && Engine.getInstance().getDuration() == Engine.getInstance().getCurrentTime()) && (id != C0690R.C0693id.controlbutton_translation_start || Engine.getInstance().isTranslateable()))))) {
            i2 = C0690R.animator.ani_button_show;
        }
        if (z) {
            if (view.getId() == C0690R.C0693id.controlbutton_edit_trim_button) {
                i = 0;
            }
            Animator loadAnimator = AnimatorInflater.loadAnimator(mAppContext, i2);
            loadAnimator.setStartDelay((long) i);
            loadAnimator.setTarget(view);
            loadAnimator.addListener(new Animator.AnimatorListener() {
                public void onAnimationCancel(Animator animator) {
                }

                public void onAnimationRepeat(Animator animator) {
                }

                public void onAnimationStart(Animator animator) {
                    if (view.getId() == C0690R.C0693id.controlbutton_translation_start) {
                        ViewStateProvider.getInstance().setConvertAnimationState(true);
                    }
                    view.setVisibility(0);
                    view.setAlpha(0.0f);
                    view.setEnabled(false);
                }

                public void onAnimationEnd(Animator animator) {
                    view.setTag((Object) null);
                    view.setVisibility(0);
                    switch (view.getId()) {
                        case C0690R.C0693id.controlbutton_edit_play:
                        case C0690R.C0693id.controlbutton_pre_play:
                            if (Engine.getInstance().getRecorderState() != 2) {
                                AnimationFactory.showButtonInternal(view, true);
                                return;
                            } else {
                                AnimationFactory.showButtonInternal(view, false);
                                return;
                            }
                        case C0690R.C0693id.controlbutton_edit_record_start:
                        case C0690R.C0693id.controlbutton_record_resume:
                            if (Engine.getInstance().isEditRecordable()) {
                                AnimationFactory.showButtonInternal(view, true);
                                return;
                            } else {
                                AnimationFactory.showButtonInternal(view, false);
                                return;
                            }
                        case C0690R.C0693id.controlbutton_edit_trim_button:
                            if (Engine.getInstance().isTrimEnable() || Engine.getInstance().isDeleteEnable()) {
                                AnimationFactory.showButtonInternal(view, true);
                                return;
                            } else {
                                AnimationFactory.showButtonInternal(view, false);
                                return;
                            }
                        case C0690R.C0693id.controlbutton_translation_resume:
                            if (Engine.getInstance().getDuration() == Engine.getInstance().getCurrentTime()) {
                                AnimationFactory.showButtonInternal(view, false);
                                return;
                            } else {
                                AnimationFactory.showButtonInternal(view, true);
                                return;
                            }
                        case C0690R.C0693id.controlbutton_translation_start:
                            if (Engine.getInstance().isTranslateable() || Engine.getInstance().isRunningSwitchSkipMuted()) {
                                AnimationFactory.showButtonInternal(view, true);
                            } else {
                                AnimationFactory.showButtonInternal(view, false);
                            }
                            ViewStateProvider.getInstance().setConvertAnimationState(false);
                            return;
                        default:
                            AnimationFactory.showButtonInternal(view, true);
                            return;
                    }
                }
            });
            view.setTag(loadAnimator);
            loadAnimator.start();
        } else if (i2 == C0690R.animator.ani_button_show) {
            showButtonInternal(view, true);
        } else {
            showButtonInternal(view, false);
        }
    }

    private static void hideButton(final View view) {
        Animator animator;
        Animator animator2 = (Animator) view.getTag();
        if (animator2 != null) {
            animator2.end();
        }
        Log.m26i(TAG, "hideButton button : " + view.getContentDescription());
        if (view.getAlpha() < 1.0f || !view.isEnabled()) {
            animator = AnimatorInflater.loadAnimator(mAppContext, C0690R.animator.ani_button_hide_disable);
        } else {
            animator = AnimatorInflater.loadAnimator(mAppContext, C0690R.animator.ani_button_hide);
        }
        animator.setTarget(view);
        animator.addListener(new Animator.AnimatorListener() {
            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
                view.setVisibility(0);
            }

            public void onAnimationEnd(Animator animator) {
                view.setTag((Object) null);
                AnimationFactory.hideButtonInternal(view);
            }
        });
        view.setTag(animator);
        animator.start();
    }
}
