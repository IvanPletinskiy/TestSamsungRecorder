package com.sec.android.app.voicenote.p007ui;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.provider.AssistantProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.service.AudioFormat;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.MetadataRepository;
import com.sec.android.app.voicenote.service.helper.Bookmark;
import com.sec.android.app.voicenote.uicore.FragmentController;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.util.Iterator;

/* renamed from: com.sec.android.app.voicenote.ui.BookmarkFragment */
public class BookmarkFragment extends AbsFragment implements FragmentController.OnSceneChangeListener {
    private static final String TAG = "BookmarkFragment";
    /* access modifiers changed from: private */
    public boolean mAnimationState = false;
    private View mBookmarkButton;
    private boolean mEventFromThis = false;
    private boolean mIsBookmarkListShowing = false;
    private int mScene = 0;
    private View mShowBookmarkListIcon;
    private View mShowBookmarkListLayout;
    private View mShowBookmarkListTextList;
    private View mShowBookmarkListTextWave;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view;
        Log.m26i(TAG, "onCreateView");
        if (DisplayManager.getVROrientation() == 1 || DisplayManager.getVROrientation() == 3) {
            Log.m19d(TAG, "Inflating Layout LAND");
            view = layoutInflater.inflate(C0690R.layout.fragment_bookmark_land, viewGroup, false);
        } else {
            Log.m19d(TAG, "Inflating Layout PORT");
            view = layoutInflater.inflate(C0690R.layout.fragment_bookmark, viewGroup, false);
        }
        view.setOnClickListener((View.OnClickListener) null);
        this.mShowBookmarkListLayout = view.findViewById(C0690R.C0693id.show_bookmark_list);
        this.mShowBookmarkListIcon = view.findViewById(C0690R.C0693id.show_bookmark_list_icon);
        this.mShowBookmarkListTextList = view.findViewById(C0690R.C0693id.show_bookmark_list_text_list);
        this.mShowBookmarkListTextWave = view.findViewById(C0690R.C0693id.show_bookmark_list_text_wave);
        this.mBookmarkButton = view.findViewById(C0690R.C0693id.add_bookmark);
        initialize();
        onUpdate(Integer.valueOf(this.mStartingEvent));
        FragmentController.getInstance().registerSceneChangeListener(this);
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (bundle != null && bundle.getBoolean("KEY_IS_BOOKMARK_SHOWING", false)) {
            this.mIsBookmarkListShowing = true;
        }
        initBookmarkView();
        updateTabletBookmarkLayout();
    }

    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("KEY_IS_BOOKMARK_SHOWING", this.mIsBookmarkListShowing);
    }

    public void onDestroyView() {
        FragmentController.getInstance().unregisterSceneChangeListener(this);
        super.onDestroyView();
    }

    public void onDestroy() {
        super.onDestroy();
        Log.m26i(TAG, "onDestroy");
    }

    public void onUpdate(Object obj) {
        Log.m19d(TAG, "onUpdate : " + obj);
        if (getActivity() == null) {
            Log.m22e(TAG, "Activity is null !!");
        } else if (this.mEventFromThis) {
            this.mEventFromThis = false;
        } else {
            int intValue = ((Integer) obj).intValue();
            if (intValue == 21) {
                return;
            }
            if (intValue == 996) {
                SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_BOOKMARK, -1);
                if (this.mScene != 8) {
                    SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_player_comm), getActivity().getResources().getString(C0690R.string.event_player_make_bookmark));
                } else if (Engine.getInstance().getRecorderState() == 2) {
                    SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_recording_comm), getActivity().getResources().getString(C0690R.string.event_bookmark));
                }
            } else if (intValue != 1001) {
                if (!(intValue == 1004 || intValue == 1006)) {
                    if (intValue == 1993) {
                        return;
                    }
                    if (intValue == 5010) {
                        initialize();
                        this.mBookmarkButton.setVisibility(8);
                        return;
                    } else if (intValue == 5012 || intValue == 5004 || intValue == 5005) {
                        this.mBookmarkButton.setVisibility(8);
                        return;
                    } else {
                        switch (intValue) {
                            case Event.PLAY_STOP:
                                break;
                            case Event.PLAY_NEXT:
                            case Event.PLAY_PREV:
                                if (Engine.getInstance().getPath().endsWith(AudioFormat.ExtType.EXT_AMR) || Engine.getInstance().getPath().endsWith(AudioFormat.ExtType.EXT_3GA)) {
                                    View view = this.mBookmarkButton;
                                    if (view != null) {
                                        view.setVisibility(8);
                                    }
                                    this.mShowBookmarkListLayout.setVisibility(8);
                                } else {
                                    View view2 = this.mBookmarkButton;
                                    if (view2 != null) {
                                        view2.setVisibility(0);
                                    }
                                    this.mShowBookmarkListLayout.setVisibility(0);
                                }
                                if (this.mIsBookmarkListShowing) {
                                    toggleOpenBookmarkList();
                                    return;
                                }
                                return;
                            default:
                                return;
                        }
                    }
                }
                this.mBookmarkButton.setVisibility(8);
                this.mShowBookmarkListLayout.setVisibility(8);
            } else {
                this.mBookmarkButton.setVisibility(0);
                this.mShowBookmarkListLayout.setVisibility(8);
            }
        }
    }

    public void onMultiWindowModeChanged(boolean z) {
        super.onMultiWindowModeChanged(z);
        initBookmarkView();
    }

    private void initialize() {
        Log.m26i(TAG, "initialize");
        if (Settings.isEnabledShowButtonBG()) {
            this.mBookmarkButton.setBackgroundResource(C0690R.C0692drawable.voice_note_btn_show_button_background);
            this.mShowBookmarkListLayout.setBackgroundResource(C0690R.C0692drawable.voice_note_btn_show_button_background);
        } else {
            this.mBookmarkButton.setBackgroundResource(C0690R.C0692drawable.voice_ripple_bookmark_btn);
            this.mShowBookmarkListLayout.setBackgroundResource(C0690R.C0692drawable.voice_ripple_bookmark_btn);
        }
        this.mBookmarkButton.setContentDescription(AssistantProvider.getInstance().getContentDesToButton(getString(C0690R.string.add_bookmark)));
        this.mShowBookmarkListTextList.setContentDescription(AssistantProvider.getInstance().getContentDesToButton(getString(C0690R.string.bookmark_list)));
        this.mShowBookmarkListTextWave.setContentDescription(AssistantProvider.getInstance().getContentDesToButton(getString(C0690R.string.wave)));
        this.mShowBookmarkListLayout.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                BookmarkFragment.this.lambda$initialize$0$BookmarkFragment(view);
            }
        });
        this.mBookmarkButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                BookmarkFragment.this.lambda$initialize$1$BookmarkFragment(view);
            }
        });
        if (Engine.getInstance().getRecorderState() != 1) {
            if (this.mScene == 6) {
                this.mBookmarkButton.setVisibility(8);
                this.mShowBookmarkListLayout.setVisibility(0);
                return;
            }
            this.mBookmarkButton.setVisibility(0);
            this.mShowBookmarkListLayout.setVisibility(8);
        } else if (Engine.getInstance().getPlayerState() == 1) {
        } else {
            if (Engine.getInstance().getPath().endsWith(AudioFormat.ExtType.EXT_AMR) || Engine.getInstance().getPath().endsWith(AudioFormat.ExtType.EXT_3GA)) {
                this.mBookmarkButton.setVisibility(8);
                this.mShowBookmarkListLayout.setVisibility(8);
            } else if (this.mScene == 6) {
                this.mBookmarkButton.setVisibility(8);
                this.mShowBookmarkListLayout.setVisibility(0);
            } else {
                this.mBookmarkButton.setVisibility(0);
                this.mShowBookmarkListLayout.setVisibility(0);
            }
        }
    }

    public /* synthetic */ void lambda$initialize$0$BookmarkFragment(View view) {
        if (this.mAnimationState) {
            Log.m29v(TAG, "mShowBookmarkListLayout onClick block while animating");
            return;
        }
        Log.m26i(TAG, "mShowBookmarkListLayout click ");
        toggleOpenBookmarkList();
    }

    public /* synthetic */ void lambda$initialize$1$BookmarkFragment(View view) {
        if (this.mAnimationState) {
            Log.m29v(TAG, "onClick - add item while animation is working");
            return;
        }
        MetadataRepository instance = MetadataRepository.getInstance();
        if (instance.getBookmarkCount() < 50) {
            int currentTime = Engine.getInstance().getCurrentTime();
            Iterator<Bookmark> it = instance.getBookmarkList().iterator();
            while (it.hasNext()) {
                if (Math.abs(it.next().getElapsed() - currentTime) < 1000) {
                    Log.m29v(TAG, "onClick - similar time slot : " + currentTime);
                    return;
                }
            }
            instance.addBookmark(currentTime);
            postEvent(Event.ADD_BOOKMARK);
            return;
        }
        Toast.makeText(getActivity(), getResources().getString(C0690R.string.bookmark_full, new Object[]{50}), 0).show();
    }

    private void initBookmarkView() {
        if (getView() != null) {
            if (this.mIsBookmarkListShowing) {
                this.mShowBookmarkListTextList.setAlpha(0.0f);
                this.mShowBookmarkListTextWave.setAlpha(1.0f);
                this.mShowBookmarkListTextWave.bringToFront();
                this.mShowBookmarkListIcon.setBackgroundResource(C0690R.C0692drawable.ic_voice_rec_ic_wave);
                return;
            }
            this.mShowBookmarkListTextWave.setAlpha(0.0f);
            this.mShowBookmarkListTextList.setAlpha(1.0f);
            this.mShowBookmarkListTextList.bringToFront();
            this.mShowBookmarkListIcon.setBackgroundResource(C0690R.C0692drawable.ic_voice_rec_ic_bookmark_list);
        }
    }

    private void toggleOpenBookmarkList() {
        toggleOpenBookmarkListIcon();
        toggleOpenBookmarkText();
        this.mIsBookmarkListShowing = !this.mIsBookmarkListShowing;
    }

    private void toggleOpenBookmarkListIcon() {
        if (getView() != null) {
            final View findViewById = getView().findViewById(C0690R.C0693id.show_bookmark_list_icon);
            if (this.mIsBookmarkListShowing) {
                findViewById.setBackgroundResource(C0690R.C0692drawable.ic_voice_rec_ic_bookmark_list);
            } else {
                findViewById.setBackgroundResource(C0690R.C0692drawable.ic_voice_rec_ic_wave);
            }
            findViewById.setAlpha(0.0f);
            Animator loadAnimator = AnimatorInflater.loadAnimator(getContext(), C0690R.animator.anim_show);
            loadAnimator.setTarget(findViewById);
            loadAnimator.addListener(new Animator.AnimatorListener() {
                public void onAnimationRepeat(Animator animator) {
                }

                public void onAnimationStart(Animator animator) {
                    boolean unused = BookmarkFragment.this.mAnimationState = true;
                }

                public void onAnimationEnd(Animator animator) {
                    boolean unused = BookmarkFragment.this.mAnimationState = false;
                }

                public void onAnimationCancel(Animator animator) {
                    findViewById.setAlpha(1.0f);
                }
            });
            loadAnimator.start();
        }
    }

    private void toggleOpenBookmarkText() {
        int i;
        final View view;
        final View view2;
        if (getView() != null) {
            if (this.mIsBookmarkListShowing) {
                View findViewById = getView().findViewById(C0690R.C0693id.show_bookmark_list_text_wave);
                View findViewById2 = getView().findViewById(C0690R.C0693id.show_bookmark_list_text_list);
                i = Event.HIDE_BOOKMARK_LIST;
                View view3 = findViewById2;
                view = findViewById;
                view2 = view3;
            } else {
                view2 = getView().findViewById(C0690R.C0693id.show_bookmark_list_text_wave);
                view = getView().findViewById(C0690R.C0693id.show_bookmark_list_text_list);
                i = Event.SHOW_BOOKMARK_LIST;
            }
            view2.setAlpha(0.0f);
            view.setAlpha(1.0f);
            view2.bringToFront();
            Animator loadAnimator = AnimatorInflater.loadAnimator(getContext(), C0690R.animator.anim_hide);
            Animator loadAnimator2 = AnimatorInflater.loadAnimator(getContext(), C0690R.animator.anim_show);
            loadAnimator2.setTarget(view2);
            loadAnimator2.addListener(new Animator.AnimatorListener() {
                public void onAnimationRepeat(Animator animator) {
                }

                public void onAnimationStart(Animator animator) {
                    boolean unused = BookmarkFragment.this.mAnimationState = true;
                }

                public void onAnimationEnd(Animator animator) {
                    boolean unused = BookmarkFragment.this.mAnimationState = false;
                }

                public void onAnimationCancel(Animator animator) {
                    view2.setAlpha(1.0f);
                    view.setAlpha(0.0f);
                }
            });
            loadAnimator2.start();
            loadAnimator.setTarget(view);
            loadAnimator.start();
            VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(i));
        }
    }

    public void onSceneChange(int i) {
        Log.m29v(TAG, "onSceneChange scene : " + i);
        if (this.mScene == i) {
            Log.m26i(TAG, "onSceneChange : not update");
            return;
        }
        this.mScene = i;
        String path = Engine.getInstance().getPath();
        if (!path.endsWith(AudioFormat.ExtType.EXT_AMR) && !path.endsWith(AudioFormat.ExtType.EXT_3GA)) {
            if (i == 4) {
                this.mBookmarkButton.setVisibility(0);
                this.mShowBookmarkListLayout.setVisibility(0);
            } else if (i == 6) {
                this.mBookmarkButton.setVisibility(8);
                this.mShowBookmarkListLayout.setVisibility(0);
                if (this.mIsBookmarkListShowing) {
                    toggleOpenBookmarkList();
                }
            } else if (i == 8) {
                this.mBookmarkButton.setVisibility(0);
                this.mShowBookmarkListLayout.setVisibility(8);
            } else if (i == 12) {
                this.mBookmarkButton.setVisibility(8);
                this.mShowBookmarkListLayout.setVisibility(8);
                if (this.mIsBookmarkListShowing) {
                    toggleOpenBookmarkList();
                }
            }
        }
    }

    private void updateTabletBookmarkLayout() {
        if (VoiceNoteFeature.FLAG_IS_TABLET && DisplayManager.getVROrientation() == 2) {
            View view = this.mShowBookmarkListLayout;
            if (view != null) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.setMarginStart(getResources().getDimensionPixelSize(C0690R.dimen.tablet_bookmark_icon_margin_end));
                this.mShowBookmarkListLayout.setLayoutParams(layoutParams);
            }
            View view2 = this.mBookmarkButton;
            if (view2 != null) {
                RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) view2.getLayoutParams();
                layoutParams2.setMarginEnd(getResources().getDimensionPixelSize(C0690R.dimen.tablet_bookmark_icon_margin_end));
                this.mBookmarkButton.setLayoutParams(layoutParams2);
            }
        }
    }
}
