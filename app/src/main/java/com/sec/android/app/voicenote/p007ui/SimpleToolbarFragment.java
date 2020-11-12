package com.sec.android.app.voicenote.p007ui;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.p007ui.view.PlaySpeedPopup;
import com.sec.android.app.voicenote.provider.AssistantProvider;
import com.sec.android.app.voicenote.provider.DesktopModeProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.service.AudioFormat;
import com.sec.android.app.voicenote.service.SimpleEngine;
import java.util.Locale;

/* renamed from: com.sec.android.app.voicenote.ui.SimpleToolbarFragment */
public class SimpleToolbarFragment extends AbsSimpleFragment implements SimpleEngine.OnSimpleEngineListener, View.OnClickListener, View.OnTouchListener, PlaySpeedPopup.OnPlaySpeedChangeListener {
    private static final float DEFAULT_ALPHA = 1.0f;
    private static final float DISABLE_ALPHA = 0.4f;
    private static final int SKIP_SILENCE_INTERVAL = 400;
    private static final int SKIP_SILENCE_OFF = 2;
    private static final int SKIP_SILENCE_ON = 1;
    private static final String TAG = "SimpleToolbarFragment";
    private View mContainerView = null;
    private Handler mEngineEventHandler = null;
    private PlaySpeedPopup mPlaySpeedPanel = null;
    private FrameLayout mRepeatButton = null;
    private FrameLayout mSkipMuteButton = null;
    private Handler mSkipSilenceHandler = null;
    private FrameLayout mSpeedButton = null;
    private int[] mToolbarLocalPos = new int[2];

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mEngineEventHandler = new Handler(new Handler.Callback() {
            public final boolean handleMessage(Message message) {
                return SimpleToolbarFragment.this.lambda$onCreate$0$SimpleToolbarFragment(message);
            }
        });
        this.mSkipSilenceHandler = new Handler(new Handler.Callback() {
            public final boolean handleMessage(Message message) {
                return SimpleToolbarFragment.this.lambda$onCreate$1$SimpleToolbarFragment(message);
            }
        });
    }

    public /* synthetic */ boolean lambda$onCreate$0$SimpleToolbarFragment(Message message) {
        if (getActivity() != null && isAdded() && !isRemoving()) {
            switch (message.what) {
                case 2013:
                    Log.m26i(TAG, "INFO_PLAY_REPEAT - mode : " + message.arg1);
                    updateRepeatButton(message.arg1);
                    break;
                case 2014:
                    Log.m26i(TAG, "INFO_SKIP_SILENCE - mode : " + message.arg1);
                    updateSkipSilenceButton(message.arg1);
                    break;
                case 2015:
                    Log.m26i(TAG, "INFO_PLAY_SPEED - speed : " + (((float) message.arg1) / ((float) message.arg2)));
                    updatePlaySpeedButton(((float) message.arg1) / ((float) message.arg2));
                    break;
            }
        }
        return false;
    }

    public /* synthetic */ boolean lambda$onCreate$1$SimpleToolbarFragment(Message message) {
        if (getActivity() != null && isAdded() && !isRemoving()) {
            int i = message.what;
            if (i == 1) {
                Log.m26i(TAG, "mSkipSilenceHandler SKIP_SILENCE_ON");
                this.mSimpleEngine.setRepeatMode(1);
                this.mSimpleEngine.setPlaySpeed(-1.0f);
            } else if (i == 2) {
                Log.m26i(TAG, "mSkipSilenceHandler SKIP_SILENCE_OFF");
                this.mSimpleEngine.setRepeatMode(2);
                this.mSimpleEngine.setPlaySpeed(1.0f);
            }
        }
        return false;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(C0690R.layout.fragment_toolbar, viewGroup, false);
        this.mRepeatButton = (FrameLayout) inflate.findViewById(C0690R.C0693id.toolbar_repeat_icon);
        this.mRepeatButton.setOnClickListener(this);
        this.mRepeatButton.setOnTouchListener(this);
        this.mSpeedButton = (FrameLayout) inflate.findViewById(C0690R.C0693id.toolbar_speed_icon);
        this.mSpeedButton.setOnClickListener(this);
        this.mSpeedButton.setOnTouchListener(this);
        this.mSkipMuteButton = (FrameLayout) inflate.findViewById(C0690R.C0693id.toolbar_skip_silence_icon);
        this.mSkipMuteButton.setOnClickListener(this);
        this.mSkipMuteButton.setOnTouchListener(this);
        onUpdate(Integer.valueOf(this.mStartingEvent));
        this.mContainerView = inflate;
        return inflate;
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        updateRepeatButton(this.mSimpleEngine.getRepeatMode());
        updatePlaySpeedButton(this.mSimpleEngine.getPlaySpeed());
        updateSkipSilenceButton(this.mSimpleEngine.getSkipSilenceMode());
        this.mSimpleEngine.registerListener(this);
    }

    public void onDestroyView() {
        this.mSimpleEngine.unregisterListener(this);
        PlaySpeedPopup playSpeedPopup = this.mPlaySpeedPanel;
        if (playSpeedPopup != null) {
            playSpeedPopup.dismiss(true);
            this.mPlaySpeedPanel = null;
        }
        super.onDestroyView();
    }

    public void onDestroy() {
        this.mEngineEventHandler = null;
        this.mSkipSilenceHandler = null;
        if (this.mContainerView != null) {
            this.mContainerView = null;
        }
        super.onDestroy();
    }

    public void onUpdate(Object obj) {
        Log.m19d(TAG, "onUpdate : " + obj);
        int intValue = ((Integer) obj).intValue();
        if (intValue == 15) {
            PlaySpeedPopup playSpeedPopup = this.mPlaySpeedPanel;
            if (playSpeedPopup != null && playSpeedPopup.isShowing()) {
                showPlaySpeedPanel();
            }
        } else if (intValue == 2005 || intValue == 2006) {
            initButtons();
        }
    }

    private void showPlaySpeedPanel() {
        if (getContext() != null && getView() != null) {
            PlaySpeedPopup playSpeedPopup = this.mPlaySpeedPanel;
            if (playSpeedPopup != null) {
                playSpeedPopup.dismiss(true);
            }
            if (DesktopModeProvider.isDesktopMode() || DisplayManager.isInMultiWindowMode(getActivity())) {
                this.mContainerView.getLocationInWindow(this.mToolbarLocalPos);
            } else {
                this.mContainerView.getLocationOnScreen(this.mToolbarLocalPos);
            }
            int measuredWidth = this.mContainerView.getMeasuredWidth();
            int[] iArr = this.mToolbarLocalPos;
            iArr[0] = iArr[0] + getActivity().getResources().getDimensionPixelSize(C0690R.dimen.main_controlbutton_margin_left);
            this.mPlaySpeedPanel = PlaySpeedPopup.getInstance(getContext(), getView().findViewById(C0690R.C0693id.toolbar_speed_icon_button), measuredWidth);
            this.mPlaySpeedPanel.setOnVolumeChangeListener(this);
            this.mPlaySpeedPanel.setValue(this.mSimpleEngine.getPlaySpeed());
            this.mPlaySpeedPanel.show();
        }
    }

    public void onEngineUpdate(int i, int i2, int i3) {
        Handler handler = this.mEngineEventHandler;
        if (handler != null) {
            handler.sendMessage(handler.obtainMessage(i, i2, i3));
        }
    }

    public void onClick(View view) {
        if (isResumed()) {
            if (this.mSkipSilenceHandler.hasMessages(1)) {
                Log.m29v(TAG, "onClick but SKIP_SILENCE_ON message exist. name : " + view.getContentDescription());
                return;
            }
            int id = view.getId();
            if (id == C0690R.C0693id.toolbar_repeat_icon) {
                onRepeatIconClick();
            } else if (id == C0690R.C0693id.toolbar_skip_silence_icon) {
                onSkipSilenceIconClick();
            } else if (id == C0690R.C0693id.toolbar_speed_icon) {
                showVolumeBar();
                SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_PLAYSPEED, -1);
            }
        }
    }

    private void initButtons() {
        updateRepeatButton(2);
        updatePlaySpeedButton(1.0f);
        updateSkipSilenceButton(4);
    }

    private void onRepeatIconClick() {
        int repeatMode = this.mSimpleEngine.getRepeatMode();
        Log.m26i(TAG, "onRepeatIconClick currentMode : " + repeatMode);
        if (repeatMode == 2) {
            this.mSimpleEngine.setRepeatMode(3);
            this.mSimpleEngine.setSkipSilenceMode(1);
        } else if (repeatMode == 3) {
            this.mSimpleEngine.setRepeatMode(4);
            SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_REPEAT, -1);
        } else if (repeatMode == 4) {
            this.mSimpleEngine.setRepeatMode(2);
            this.mSimpleEngine.setSkipSilenceMode(4);
        }
    }

    private void showVolumeBar() {
        if (getContext() != null && getView() != null) {
            if (DesktopModeProvider.isDesktopMode() || DisplayManager.isInMultiWindowMode(getActivity())) {
                this.mContainerView.getLocationInWindow(this.mToolbarLocalPos);
            } else {
                this.mContainerView.getLocationOnScreen(this.mToolbarLocalPos);
            }
            int measuredWidth = this.mContainerView.getMeasuredWidth();
            int[] iArr = this.mToolbarLocalPos;
            iArr[0] = iArr[0] + getActivity().getResources().getDimensionPixelSize(C0690R.dimen.main_controlbutton_margin_left);
            this.mPlaySpeedPanel = PlaySpeedPopup.getInstance(getContext(), getView().findViewById(C0690R.C0693id.toolbar_speed_icon_button), measuredWidth);
            this.mPlaySpeedPanel.setOnVolumeChangeListener(this);
            this.mPlaySpeedPanel.setValue(this.mSimpleEngine.getPlaySpeed());
            this.mPlaySpeedPanel.setLocalPositionParent(this.mToolbarLocalPos);
            this.mPlaySpeedPanel.show();
        }
    }

    private void onSkipSilenceIconClick() {
        int i;
        int skipSilenceMode = this.mSimpleEngine.getSkipSilenceMode();
        Log.m26i(TAG, "onSkipSilenceIconClick currentMode : " + skipSilenceMode);
        if (skipSilenceMode == 2 || skipSilenceMode == 3) {
            if (this.mSimpleEngine.setSkipSilenceMode(4) == 4) {
                this.mSkipSilenceHandler.removeMessages(2);
                this.mSkipSilenceHandler.sendEmptyMessageDelayed(2, 400);
                SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_SKIPSILENCE, 0);
            }
        } else if (skipSilenceMode == 4) {
            if (this.mSimpleMetadata.getRecordMode() == 2) {
                i = this.mSimpleEngine.setSkipSilenceMode(2);
            } else {
                i = this.mSimpleEngine.setSkipSilenceMode(3);
            }
            if (i == 3 || i == 2) {
                this.mSkipSilenceHandler.removeMessages(1);
                this.mSkipSilenceHandler.sendEmptyMessageDelayed(1, 400);
                SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_SKIPSILENCE, 1);
            }
        }
    }

    private void updateRepeatButton(int i) {
        Log.m26i(TAG, "updateRepeatButton - repeatState : " + i);
        FrameLayout frameLayout = this.mRepeatButton;
        if (frameLayout != null) {
            Button button = (Button) frameLayout.findViewById(C0690R.C0693id.toolbar_repeat_icon_button);
            TextView textView = (TextView) this.mRepeatButton.findViewById(C0690R.C0693id.toolbar_repeat_icon_button_text);
            float f = 1.0f;
            if (i == 1) {
                initTouchState(this.mRepeatButton.findViewById(C0690R.C0693id.toolbar_repeat_icon));
                button.setForeground(getResources().getDrawable(C0690R.C0692drawable.ic_voice_rec_ic_repeat, (Resources.Theme) null));
                textView.setTextColor(getResources().getColor(C0690R.C0691color.toolbar_icon_text_color, (Resources.Theme) null));
                f = DISABLE_ALPHA;
                this.mRepeatButton.setEnabled(false);
                this.mRepeatButton.setFocusable(false);
                this.mRepeatButton.setClickable(false);
            } else if (i == 3) {
                button.setForeground(getResources().getDrawable(C0690R.C0692drawable.ic_voice_rec_ic_repeat_set, (Resources.Theme) null));
                textView.setTextColor(getResources().getColor(C0690R.C0691color.toolbar_icon_text_repeat_highlight_color, (Resources.Theme) null));
                this.mRepeatButton.setEnabled(true);
                this.mRepeatButton.setFocusable(true);
                this.mRepeatButton.setClickable(true);
            } else if (i != 4) {
                button.setForeground(getResources().getDrawable(C0690R.C0692drawable.ic_voice_rec_ic_repeat, (Resources.Theme) null));
                textView.setTextColor(getResources().getColor(C0690R.C0691color.toolbar_icon_text_color, (Resources.Theme) null));
                this.mRepeatButton.setEnabled(true);
                this.mRepeatButton.setFocusable(true);
                this.mRepeatButton.setClickable(true);
            } else {
                button.setForeground(getResources().getDrawable(C0690R.C0692drawable.ic_voice_rec_ic_repeat_set, (Resources.Theme) null));
                textView.setTextColor(getResources().getColor(C0690R.C0691color.toolbar_icon_text_repeat_highlight_color, (Resources.Theme) null));
                this.mRepeatButton.setEnabled(true);
                this.mRepeatButton.setFocusable(true);
                this.mRepeatButton.setClickable(true);
            }
            this.mRepeatButton.setContentDescription(AssistantProvider.getInstance().getButtonDescriptionForTalkback(C0690R.string.repeat));
            if (Settings.isEnabledShowButtonBG()) {
                button.setBackgroundResource(C0690R.C0692drawable.voice_note_btn_show_button_background);
            }
            this.mRepeatButton.setAlpha(f);
        }
    }

    private void updatePlaySpeedButton(float f) {
        String str;
        Log.m26i(TAG, "updatePlaySpeedButton - playSpeed : " + f);
        FrameLayout frameLayout = this.mSpeedButton;
        if (frameLayout != null) {
            TextView textView = (TextView) frameLayout.findViewById(C0690R.C0693id.toolbar_speed_icon_button);
            String path = this.mSimpleEngine.getPath();
            boolean z = path != null && path.contains(AudioFormat.ExtType.EXT_AMR);
            float f2 = 1.0f;
            if (f == -1.0f || z) {
                initTouchState(this.mSpeedButton.findViewById(C0690R.C0693id.toolbar_speed_icon));
                this.mSpeedButton.setEnabled(false);
                this.mSpeedButton.setFocusable(false);
                this.mSpeedButton.setClickable(false);
                str = String.valueOf(1.0f);
                f2 = 0.4f;
                f = 1.0f;
            } else {
                this.mSpeedButton.setEnabled(true);
                this.mSpeedButton.setFocusable(true);
                this.mSpeedButton.setClickable(true);
                str = String.valueOf(f);
            }
            this.mSpeedButton.findViewById(C0690R.C0693id.toolbar_speed_icon).setContentDescription(getString(C0690R.string.speed) + ' ' + getString(C0690R.string.button) + ' ' + str);
            updateSpeedButton(textView, f);
            if (Settings.isEnabledShowButtonBG()) {
                textView.setBackgroundResource(C0690R.C0692drawable.voice_note_btn_show_button_background);
            }
            this.mSpeedButton.setAlpha(f2);
        }
    }

    private void updateSkipSilenceButton(int i) {
        Log.m26i(TAG, "updateSkipSilenceButton - skipSilenceMode : " + i);
        FrameLayout frameLayout = this.mSkipMuteButton;
        if (frameLayout != null) {
            Button button = (Button) frameLayout.findViewById(C0690R.C0693id.toolbar_skip_silence_icon_button);
            TextView textView = (TextView) this.mSkipMuteButton.findViewById(C0690R.C0693id.toolbar_skip_silence_text);
            float f = 1.0f;
            if (i == 1) {
                initTouchState(this.mSkipMuteButton.findViewById(C0690R.C0693id.toolbar_skip_silence_icon));
                button.setForeground(getResources().getDrawable(C0690R.C0692drawable.ic_voice_rec_ic_mute, (Resources.Theme) null));
                textView.setTextColor(getResources().getColor(C0690R.C0691color.toolbar_icon_text_color, (Resources.Theme) null));
                f = DISABLE_ALPHA;
                this.mSkipMuteButton.setClickable(false);
                this.mSkipMuteButton.setFocusable(false);
                this.mSkipMuteButton.setBackground((Drawable) null);
            } else if (i == 2 || i == 3) {
                button.setForeground(getResources().getDrawable(C0690R.C0692drawable.ic_voice_rec_ic_mute_set, (Resources.Theme) null));
                textView.setTextColor(getResources().getColor(C0690R.C0691color.toolbar_icon_text_skip_highlight_color, (Resources.Theme) null));
                this.mSkipMuteButton.setClickable(true);
                this.mSkipMuteButton.setFocusable(true);
                this.mSkipMuteButton.setBackgroundResource(C0690R.C0692drawable.voice_ripple_control_big_btn);
            } else {
                button.setForeground(getResources().getDrawable(C0690R.C0692drawable.ic_voice_rec_ic_mute, (Resources.Theme) null));
                textView.setTextColor(getResources().getColor(C0690R.C0691color.toolbar_icon_text_color, (Resources.Theme) null));
                this.mSkipMuteButton.setFocusable(true);
                this.mSkipMuteButton.setClickable(true);
            }
            this.mSkipMuteButton.setContentDescription(AssistantProvider.getInstance().getButtonDescriptionForTalkback(C0690R.string.skip_muted));
            if (Settings.isEnabledShowButtonBG()) {
                button.setBackgroundResource(C0690R.C0692drawable.voice_note_btn_show_button_background);
            }
            this.mSkipMuteButton.setAlpha(f);
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.isClickable()) {
            return false;
        }
        view.setTag(motionEvent);
        return true;
    }

    private void initTouchState(View view) {
        MotionEvent motionEvent;
        if (view != null && (motionEvent = (MotionEvent) view.getTag()) != null && motionEvent.getAction() != 1) {
            MotionEvent obtain = MotionEvent.obtain(motionEvent);
            obtain.setAction(1);
            view.dispatchTouchEvent(obtain);
            obtain.recycle();
        }
    }

    public void onPlaySpeedChange(float f) {
        this.mSimpleEngine.setPlaySpeed(f);
        if (f == 1.0f) {
            if (this.mSimpleEngine.getSkipSilenceMode() != 4) {
                this.mSimpleEngine.setSkipSilenceMode(4);
            }
        } else if (this.mSimpleEngine.getSkipSilenceMode() != 1) {
            this.mSimpleEngine.setSkipSilenceMode(1);
        }
    }

    private void updateSpeedButton(TextView textView, float f) {
        if (textView != null) {
            Log.m19d(TAG, "updateSpeedButton - speed: " + f);
            textView.setText(String.format(Locale.getDefault(), "%.1f", new Object[]{Float.valueOf(f)}) + "x");
        }
    }
}
