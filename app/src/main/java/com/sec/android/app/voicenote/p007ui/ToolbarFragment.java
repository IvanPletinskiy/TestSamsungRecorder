package com.sec.android.app.voicenote.p007ui;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.p007ui.view.PlaySpeedPopup;
import com.sec.android.app.voicenote.provider.AssistantProvider;
import com.sec.android.app.voicenote.provider.ContextMenuProvider;
import com.sec.android.app.voicenote.provider.DesktopModeProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.MouseKeyboardProvider;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.service.AudioFormat;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.MetadataRepository;
import java.util.Locale;

/* renamed from: com.sec.android.app.voicenote.ui.ToolbarFragment */
public class ToolbarFragment extends AbsFragment implements Engine.OnEngineListener, View.OnClickListener, View.OnTouchListener, PlaySpeedPopup.OnPlaySpeedChangeListener {
    private static final float DEFAULT_ALPHA = 1.0f;
    private static final float DISABLE_ALPHA = 0.4f;
    private static final int SKIP_SILENCE_INTERVAL = 400;
    private static final int SKIP_SILENCE_OFF = 2;
    private static final int SKIP_SILENCE_ON = 1;
    private static final String TAG = "ToolbarFragment";
    private View mContainerView;
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
                return ToolbarFragment.this.lambda$onCreate$0$ToolbarFragment(message);
            }
        });
        this.mSkipSilenceHandler = new Handler(new Handler.Callback() {
            public final boolean handleMessage(Message message) {
                return ToolbarFragment.this.lambda$onCreate$1$ToolbarFragment(message);
            }
        });
    }

    public /* synthetic */ boolean lambda$onCreate$0$ToolbarFragment(Message message) {
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

    public /* synthetic */ boolean lambda$onCreate$1$ToolbarFragment(Message message) {
        if (getActivity() != null && isAdded() && !isRemoving()) {
            int i = message.what;
            if (i == 1) {
                Log.m26i(TAG, "mSkipSilenceHandler SKIP_SILENCE_ON");
                Engine.getInstance().setRepeatMode(1);
                Engine.getInstance().setPlaySpeed(-1.0f);
            } else if (i == 2) {
                Log.m26i(TAG, "mSkipSilenceHandler SKIP_SILENCE_OFF");
                Engine.getInstance().setRepeatMode(2);
                Engine.getInstance().setPlaySpeed(1.0f);
            }
        }
        return false;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(C0690R.layout.fragment_toolbar, viewGroup, false);
        this.mContainerView = inflate;
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
        MouseKeyboardProvider.getInstance().mouseClickInteraction(getActivity(), this, this.mContainerView);
        return inflate;
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        initButtons();
        Engine.getInstance().registerListener(this);
    }

    public void onResume() {
        super.onResume();
        PlaySpeedPopup playSpeedPopup = this.mPlaySpeedPanel;
        if (playSpeedPopup != null && playSpeedPopup.isShowing()) {
            showPlaySpeedPanel();
        }
    }

    public void onDestroyView() {
        if (this.mContainerView != null) {
            MouseKeyboardProvider.getInstance().destroyMouseClickInteraction(this, this.mContainerView);
            this.mContainerView = null;
        }
        Engine.getInstance().unregisterListener(this);
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
        super.onDestroy();
    }

    public void onUpdate(Object obj) {
        Log.m19d(TAG, "onUpdate : " + obj);
        int intValue = ((Integer) obj).intValue();
        if (intValue == 15 || intValue == 19) {
            PlaySpeedPopup playSpeedPopup = this.mPlaySpeedPanel;
            if (playSpeedPopup != null && playSpeedPopup.isShowing()) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        ToolbarFragment.this.showPlaySpeedPanel();
                    }
                }, 10);
            }
        } else if (intValue == 2005 || intValue == 2006) {
            initButtons();
            PlaySpeedPopup playSpeedPopup2 = this.mPlaySpeedPanel;
            if (playSpeedPopup2 != null) {
                playSpeedPopup2.dismiss(true);
                this.mPlaySpeedPanel = null;
            }
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
            } else if (id == C0690R.C0693id.toolbar_speed_icon && Engine.getInstance().getSkipSilenceMode() != 3) {
                showPlaySpeedPanel();
                SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_PLAYSPEED, -1);
                SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_player_comm), getActivity().getResources().getString(C0690R.string.event_player_speed));
            }
        }
    }

    private void initButtons() {
        updateRepeatButton(Engine.getInstance().getRepeatMode());
        updatePlaySpeedButton(Engine.getInstance().getPlaySpeed());
        updateSkipSilenceButton(Engine.getInstance().getSkipSilenceMode());
    }

    private void onRepeatIconClick() {
        FragmentActivity activity = getActivity();
        int repeatMode = Engine.getInstance().getRepeatMode();
        Log.m26i(TAG, "onRepeatIconClick currentMode : " + repeatMode);
        if (repeatMode == 2) {
            Engine.getInstance().setRepeatMode(3);
            Engine.getInstance().setSkipSilenceMode(1);
        } else if (repeatMode == 3) {
            Engine.getInstance().setRepeatMode(4);
            SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_REPEAT, -1);
            if (activity != null) {
                SALogProvider.insertSALog(activity.getResources().getString(C0690R.string.screen_player_comm), activity.getResources().getString(C0690R.string.event_player_repeat));
            }
        } else if (repeatMode == 4) {
            Engine.getInstance().setRepeatMode(2);
            Engine.getInstance().setSkipSilenceMode(4);
        }
    }

    /* access modifiers changed from: private */
    public void showPlaySpeedPanel() {
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
            this.mPlaySpeedPanel.setValue(Engine.getInstance().getPlaySpeed());
            this.mPlaySpeedPanel.setLocalPositionParent(this.mToolbarLocalPos);
            this.mPlaySpeedPanel.show();
        }
    }

    private void onSkipSilenceIconClick() {
        int i;
        FragmentActivity activity = getActivity();
        int skipSilenceMode = Engine.getInstance().getSkipSilenceMode();
        Log.m26i(TAG, "onSkipSilenceIconClick currentMode : " + skipSilenceMode);
        if (skipSilenceMode == 2 || skipSilenceMode == 3) {
            if (Engine.getInstance().setSkipSilenceMode(4) == 4) {
                this.mSkipSilenceHandler.removeMessages(2);
                this.mSkipSilenceHandler.sendEmptyMessageDelayed(2, 400);
                SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_SKIPSILENCE, 0);
                if (activity != null) {
                    SALogProvider.insertSALog(activity.getResources().getString(C0690R.string.screen_player_comm), activity.getResources().getString(C0690R.string.event_player_skip_mute), "0");
                }
            }
        } else if (skipSilenceMode == 4) {
            if (MetadataRepository.getInstance().getRecordMode() == 2) {
                i = Engine.getInstance().setSkipSilenceMode(2);
            } else {
                i = Engine.getInstance().setSkipSilenceMode(3);
            }
            if (i == 3 || i == 2) {
                this.mSkipSilenceHandler.removeMessages(1);
                this.mSkipSilenceHandler.sendEmptyMessageDelayed(1, 400);
                SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_SKIPSILENCE, 1);
                if (activity != null) {
                    SALogProvider.insertSALog(activity.getResources().getString(C0690R.string.screen_player_comm), activity.getResources().getString(C0690R.string.event_player_skip_mute), "1");
                }
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
                initTouchState(this.mRepeatButton);
                button.setForeground(getResources().getDrawable(C0690R.C0692drawable.ic_voice_rec_ic_repeat, (Resources.Theme) null));
                textView.setTextColor(getResources().getColor(C0690R.C0691color.toolbar_icon_text_color, (Resources.Theme) null));
                f = DISABLE_ALPHA;
                this.mRepeatButton.setClickable(false);
                this.mRepeatButton.setFocusable(false);
                this.mRepeatButton.setBackground((Drawable) null);
            } else if (i == 3) {
                button.setForeground(getResources().getDrawable(C0690R.C0692drawable.ic_voice_rec_ic_repeat_set, (Resources.Theme) null));
                textView.setTextColor(getResources().getColor(C0690R.C0691color.toolbar_icon_text_repeat_highlight_color, (Resources.Theme) null));
                this.mRepeatButton.setClickable(true);
                this.mRepeatButton.setFocusable(true);
                this.mRepeatButton.setBackgroundResource(C0690R.C0692drawable.voice_ripple_control_big_btn);
            } else if (i != 4) {
                button.setForeground(getResources().getDrawable(C0690R.C0692drawable.ic_voice_rec_ic_repeat, (Resources.Theme) null));
                textView.setTextColor(getResources().getColor(C0690R.C0691color.toolbar_icon_text_color, (Resources.Theme) null));
                this.mRepeatButton.setClickable(true);
                this.mRepeatButton.setFocusable(true);
                this.mRepeatButton.setBackgroundResource(C0690R.C0692drawable.voice_ripple_control_big_btn);
            } else {
                button.setForeground(getResources().getDrawable(C0690R.C0692drawable.ic_voice_rec_ic_repeat_set, (Resources.Theme) null));
                textView.setTextColor(getResources().getColor(C0690R.C0691color.toolbar_icon_text_repeat_highlight_color, (Resources.Theme) null));
                this.mRepeatButton.setClickable(true);
                this.mRepeatButton.setFocusable(true);
                this.mRepeatButton.setBackgroundResource(C0690R.C0692drawable.voice_ripple_control_big_btn);
            }
            this.mRepeatButton.setContentDescription(AssistantProvider.getInstance().getContentDesToButton(getString(C0690R.string.repeat)));
            if (Settings.isEnabledShowButtonBG()) {
                button.setBackgroundResource(C0690R.C0692drawable.voice_note_btn_toolbar_button_background);
            }
            setGoneToolbarText(textView);
            this.mRepeatButton.setAlpha(f);
        }
    }

    private void updatePlaySpeedButton(float f) {
        String str;
        Log.m26i(TAG, "updatePlaySpeedButton - playSpeed : " + f);
        FrameLayout frameLayout = this.mSpeedButton;
        if (frameLayout != null) {
            TextView textView = (TextView) frameLayout.findViewById(C0690R.C0693id.toolbar_speed_icon_button);
            String path = Engine.getInstance().getPath();
            boolean z = path != null && path.contains(AudioFormat.ExtType.EXT_AMR);
            Log.m19d(TAG, "path: " + path + " amr: " + path.contains(AudioFormat.ExtType.EXT_AMR));
            float f2 = 1.0f;
            if (f == -1.0f || z) {
                initTouchState(this.mSpeedButton);
                this.mSpeedButton.setClickable(false);
                this.mSpeedButton.setFocusable(false);
                this.mSpeedButton.setBackground((Drawable) null);
                str = "1.0";
                f2 = 0.4f;
                f = 1.0f;
            } else {
                this.mSpeedButton.setClickable(true);
                this.mSpeedButton.setFocusable(true);
                this.mSpeedButton.setBackgroundResource(C0690R.C0692drawable.voice_ripple_control_big_btn);
                str = String.valueOf(f);
            }
            this.mSpeedButton.setContentDescription(getString(C0690R.string.speed) + ' ' + getString(C0690R.string.button) + ' ' + str);
            updateSpeedButton(textView, f);
            if (Settings.isEnabledShowButtonBG()) {
                textView.setBackgroundResource(C0690R.C0692drawable.voice_note_btn_toolbar_button_background);
            }
            setGoneToolbarText((TextView) this.mSpeedButton.findViewById(C0690R.C0693id.toolbar_speed_icon_button_text));
            this.mSpeedButton.setAlpha(f2);
        }
    }

    private void setGoneToolbarText(TextView textView) {
        int i;
        if (textView != null) {
            FragmentActivity activity = getActivity();
            if (isAdded() && activity != null) {
                if (DisplayManager.isInMultiWindowMode(activity)) {
                    if (textView.getVisibility() == 0) {
                        int vROrientation = DisplayManager.getVROrientation();
                        int dimensionPixelSize = activity.getResources().getDimensionPixelSize(C0690R.dimen.multi_window_toolbar_full_width);
                        int dimensionPixelSize2 = activity.getResources().getDimensionPixelSize(C0690R.dimen.toolbar_icon_left_margin);
                        if (vROrientation == 3) {
                            i = DisplayManager.getCurrentScreenWidth(activity) / 2;
                        } else {
                            i = DisplayManager.getCurrentScreenWidth(activity);
                        }
                        if ((i - (dimensionPixelSize2 * 2)) / 3 < dimensionPixelSize || (vROrientation == 2 && DisplayManager.smallHalfScreen(activity))) {
                            textView.setVisibility(8);
                        }
                    }
                } else if (textView.getVisibility() != 0) {
                    textView.setVisibility(0);
                }
            }
        }
    }

    private void updateSpeedButton(TextView textView, float f) {
        if (textView != null) {
            Log.m19d(TAG, "updateSpeedButton - speed: " + f);
            textView.setText(String.format(Locale.getDefault(), "%.1f", new Object[]{Float.valueOf(f)}) + "x");
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
                initTouchState(this.mSkipMuteButton);
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
                this.mSkipMuteButton.setClickable(true);
                this.mSkipMuteButton.setFocusable(true);
                this.mSkipMuteButton.setBackgroundResource(C0690R.C0692drawable.voice_ripple_control_big_btn);
            }
            this.mSkipMuteButton.setContentDescription(AssistantProvider.getInstance().getContentDesToButton(getString(C0690R.string.skip_muted)));
            if (Settings.isEnabledShowButtonBG()) {
                button.setBackgroundResource(C0690R.C0692drawable.voice_note_btn_toolbar_button_background);
            }
            setGoneToolbarText(textView);
            this.mSkipMuteButton.setAlpha(f);
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.m19d(TAG, "onTouch");
        if (view.isClickable()) {
            return false;
        }
        view.setTag(motionEvent);
        return true;
    }

    private void initTouchState(View view) {
        MotionEvent motionEvent;
        Log.m19d(TAG, "initTouchState");
        if (view != null && (motionEvent = (MotionEvent) view.getTag()) != null && motionEvent.getAction() != 1) {
            MotionEvent obtain = MotionEvent.obtain(motionEvent);
            obtain.setAction(1);
            view.dispatchTouchEvent(obtain);
            obtain.recycle();
        }
    }

    public void onPlaySpeedChange(float f) {
        Engine.getInstance().setPlaySpeed(f);
        if (f == 1.0f) {
            if (Engine.getInstance().getSkipSilenceMode() != 4) {
                Engine.getInstance().setSkipSilenceMode(4);
            }
        } else if (Engine.getInstance().getSkipSilenceMode() != 1) {
            Engine.getInstance().setSkipSilenceMode(1);
        }
    }

    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        ContextMenuProvider.getInstance().createContextMenu(getActivity(), contextMenu, 4, view, this);
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        ContextMenuProvider.getInstance().contextItemSelected((AppCompatActivity) getActivity(), menuItem, 4, this);
        return false;
    }
}
