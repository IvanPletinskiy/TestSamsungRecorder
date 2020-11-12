package com.sec.android.app.voicenote.p007ui;

import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.provider.AssistantProvider;
import com.sec.android.app.voicenote.provider.CallRejectChecker;
import com.sec.android.app.voicenote.provider.DBProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.HWKeyboardProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.PermissionProvider;
import com.sec.android.app.voicenote.provider.SecureFolderProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.service.AudioFormat;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.Player;
import com.sec.android.app.voicenote.service.Recorder;
import com.sec.android.app.voicenote.uicore.FragmentController;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import java.util.Locale;

/* renamed from: com.sec.android.app.voicenote.ui.InfoFragment */
public class InfoFragment extends AbsFragment implements Engine.OnEngineListener, FragmentController.OnSceneChangeListener {
    private static final String TAG = "InfoFragment";
    private int mDuration = 0;
    private Handler mEventHandler = null;
    private long mLastUpdateLogTime = 0;
    private TextView mMaxDuration = null;
    private LinearLayout mMaxLayout = null;
    private String mMaxLengthText = null;
    private TextView mMaxTextView = null;
    private int mOldTextTimeLength = -1;
    private int mRecordMode;
    private ImageView mRejectCall = null;
    private int mScene = 0;
    private TextView mTimeDotTextView = null;
    private TextView mTimeHmsTextView = null;
    private LinearLayout mTimeLayout = null;
    private TextView mTimeMsTextView = null;
    private ForegroundColorSpan mTimeTextDimSpan;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mEventHandler = new Handler(new Handler.Callback() {
            public final boolean handleMessage(Message message) {
                return InfoFragment.this.lambda$onCreate$0$InfoFragment(message);
            }
        });
    }

    public /* synthetic */ boolean lambda$onCreate$0$InfoFragment(Message message) {
        int i;
        if (getActivity() != null && isAdded() && !isRemoving() && (isResumed() || !Engine.getInstance().getScreenOff())) {
            int i2 = message.what;
            if (i2 == 101) {
                updateCurrentTime(message.arg1);
                if (this.mScene == 6 && this.mDuration < (i = message.arg1)) {
                    this.mDuration = i;
                }
            } else if (i2 == 1010) {
                Log.m26i(TAG, "INFO_RECORDER_STATE - state : " + message.arg1);
                int i3 = message.arg1;
                if (i3 == 3) {
                    updateCurrentTime(Engine.getInstance().getCurrentTime());
                } else if (i3 == 4 && Engine.getInstance().isSimpleRecorderMode() && Engine.getInstance().getSimpleModeItem() != -1) {
                    showDuration();
                }
            } else if (i2 == 2010) {
                Log.m26i(TAG, "INFO_PLAYER_STATE - state : " + message.arg1);
                int i4 = message.arg1;
                if (i4 == 3) {
                    showDuration();
                    updateCurrentTime(Engine.getInstance().getCurrentTime());
                } else if (i4 == 4) {
                    if (this.mScene == 12) {
                        if (Player.getInstance().isIsRunningSwitchSkipMuted()) {
                            new Handler().postDelayed(new Runnable() {
                                public final void run() {
                                    InfoFragment.this.showDuration();
                                }
                            }, 400);
                        } else {
                            showDuration();
                        }
                    }
                    updateCurrentTime(Engine.getInstance().getCurrentTime());
                }
            } else if (i2 == 3010) {
                Log.m26i(TAG, "INFO_EDITOR_STATE - state : " + message.arg1);
                int i5 = message.arg1;
                if (i5 != 1) {
                    if (i5 == 4) {
                        showDuration();
                    }
                } else if (this.mScene == 6) {
                    showDuration();
                }
            } else if (i2 == 1022) {
                DialogFactory.show(getActivity().getSupportFragmentManager(), DialogFactory.STORAGE_FULL_DIALOG, (Bundle) null);
            } else if (i2 == 1023) {
                Toast.makeText(getActivity(), C0690R.string.not_enough_memory, 0).show();
            }
        }
        return false;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Log.m26i(TAG, "onCreateView");
        View inflate = layoutInflater.inflate(C0690R.layout.fragment_info, viewGroup, false);
        inflate.setOnClickListener((View.OnClickListener) null);
        this.mOldTextTimeLength = -1;
        this.mMaxLengthText = null;
        this.mTimeTextDimSpan = new ForegroundColorSpan(getResources().getColor(C0690R.C0691color.recording_time_dim, (Resources.Theme) null));
        this.mTimeLayout = (LinearLayout) inflate.findViewById(C0690R.C0693id.info_recording_time_layout);
        this.mTimeHmsTextView = (TextView) inflate.findViewById(C0690R.C0693id.info_recording_time_hms);
        this.mTimeMsTextView = (TextView) inflate.findViewById(C0690R.C0693id.info_recording_time_ms);
        this.mTimeDotTextView = (TextView) inflate.findViewById(C0690R.C0693id.info_recording_time_dot);
        if (DisplayManager.isInMultiWindowMode(getActivity())) {
            this.mTimeHmsTextView.setTextSize((((float) getResources().getDimensionPixelSize(C0690R.dimen.info_recording_time_text_size_multi_window)) / getResources().getDisplayMetrics().density) / getResources().getConfiguration().fontScale);
            this.mTimeMsTextView.setTextSize((((float) getResources().getDimensionPixelSize(C0690R.dimen.info_recording_time_text_size_multi_window)) / getResources().getDisplayMetrics().density) / getResources().getConfiguration().fontScale);
            this.mTimeDotTextView.setTextSize((((float) getResources().getDimensionPixelSize(C0690R.dimen.info_recording_time_text_size_multi_window)) / getResources().getDisplayMetrics().density) / getResources().getConfiguration().fontScale);
        } else {
            this.mTimeHmsTextView.setTextSize((((float) getResources().getDimensionPixelSize(C0690R.dimen.info_recording_time_text_size)) / getResources().getDisplayMetrics().density) / getResources().getConfiguration().fontScale);
            this.mTimeMsTextView.setTextSize((((float) getResources().getDimensionPixelSize(C0690R.dimen.info_recording_time_text_size)) / getResources().getDisplayMetrics().density) / getResources().getConfiguration().fontScale);
            this.mTimeDotTextView.setTextSize((((float) getResources().getDimensionPixelSize(C0690R.dimen.info_recording_time_text_size)) / getResources().getDisplayMetrics().density) / getResources().getConfiguration().fontScale);
        }
        int currentTime = Engine.getInstance().getCurrentTime();
        this.mRecordMode = Settings.getIntSettings("record_mode", 1);
        String stringByDuration = getStringByDuration(currentTime, true);
        setTextTimeView(stringByDuration, currentTime);
        this.mTimeLayout.setContentDescription(AssistantProvider.getInstance().stringForReadTime(stringByDuration));
        this.mMaxLayout = (LinearLayout) inflate.findViewById(C0690R.C0693id.info_max_layout);
        this.mMaxTextView = (TextView) inflate.findViewById(C0690R.C0693id.info_max_text);
        this.mMaxDuration = (TextView) inflate.findViewById(C0690R.C0693id.info_max_duration);
        int i = this.mRecordMode;
        if (i == 4 || i == 5 || i == 6) {
            String stringByDuration2 = getStringByDuration(getMaxDuration(), false);
            this.mMaxDuration.setText(stringByDuration2);
            LinearLayout linearLayout = this.mMaxLayout;
            linearLayout.setContentDescription(getActivity().getString(C0690R.string.play_time) + ", " + AssistantProvider.getInstance().stringForReadTime(stringByDuration2));
            if (HWKeyboardProvider.isHWKeyboard(getActivity())) {
                this.mMaxTextView.setVisibility(8);
                this.mMaxDuration.setVisibility(8);
            } else {
                this.mMaxTextView.setVisibility(0);
                this.mMaxDuration.setVisibility(0);
            }
        }
        this.mRejectCall = (ImageView) inflate.findViewById(C0690R.C0693id.info_reject_call);
//        this.mRejectCall.semSetHoverPopupType(1);
        if (needToShowRejectCallIcon()) {
            this.mRejectCall.setVisibility(0);
        } else {
            this.mRejectCall.setVisibility(8);
        }
        if (VoiceNoteApplication.getScene() == 6) {
            this.mScene = 6;
        }
        if (this.mScene == 6 && Engine.getInstance().getRecorderState() == 2) {
            onUpdate(Integer.valueOf(Event.EDIT_RECORD));
        } else {
            onUpdate(Integer.valueOf(this.mStartingEvent));
        }
        return inflate;
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        FragmentController.getInstance().registerSceneChangeListener(this);
        Engine.getInstance().registerListener(this);
    }

    public void onDestroyView() {
        FragmentController.getInstance().unregisterSceneChangeListener(this);
        Engine.getInstance().unregisterListener(this);
        super.onDestroyView();
    }

    public void onDestroy() {
        this.mEventHandler = null;
        super.onDestroy();
    }

    public void onUpdate(Object obj) {
        if (!isAdded()) {
            Log.m22e(TAG, "Skip onUpdate data : " + obj);
            return;
        }
        Log.m19d(TAG, "onUpdate : " + obj);
        int intValue = ((Integer) obj).intValue();
        if (intValue == 4) {
            this.mRecordMode = Settings.getIntSettings("record_mode", 1);
            updateCurrentTime(0);
        } else if (intValue == 5) {
            this.mRecordMode = Settings.getIntSettings(Settings.KEY_PLAY_MODE, 1);
        } else if (intValue == 17) {
            showDuration();
        } else if (intValue != 975) {
            if (!(intValue == 5001 || intValue == 5012 || intValue == 7001)) {
                if (intValue != 1001) {
                    if (intValue == 1002) {
                        updateCurrentTime(Engine.getInstance().getCurrentTime());
                        return;
                    } else if (intValue == 1006) {
                        this.mRejectCall.setVisibility(8);
                        this.mMaxDuration.setVisibility(8);
                        return;
                    } else if (intValue != 1007) {
                        if (intValue == 1996) {
                            this.mRejectCall.setVisibility(8);
                            return;
                        } else if (intValue != 1997) {
                            if (!(intValue == 2005 || intValue == 2006)) {
                                if (intValue != 5004) {
                                    if (intValue != 5005) {
                                        switch (intValue) {
                                            case Event.PLAY_START:
                                            case Event.PLAY_PAUSE:
                                            case Event.PLAY_RESUME:
                                                break;
                                            default:
                                                return;
                                        }
                                    }
                                }
                            }
                        } else if (needToShowRejectCallIcon()) {
                            this.mRejectCall.setVisibility(0);
                            return;
                        } else {
                            return;
                        }
                    }
                }
                if (this.mScene == 6) {
                    this.mRecordMode = Settings.getIntSettings(Settings.KEY_PLAY_MODE, 1);
                } else {
                    this.mRecordMode = Settings.getIntSettings("record_mode", 1);
                }
                showMaxTime(this.mRecordMode);
                if (needToShowRejectCallIcon()) {
                    this.mRejectCall.setVisibility(0);
                    return;
                }
                return;
            }
            showDuration();
            updateCurrentTime(Engine.getInstance().getCurrentTime());
            this.mRejectCall.setVisibility(8);
        } else {
            if (Engine.getInstance().getPlayerState() == 4) {
                Handler handler = this.mEventHandler;
                handler.sendMessage(handler.obtainMessage(2012, Engine.getInstance().getCurrentTime(), -1));
            }
            showDuration();
        }
    }

    public void onSceneChange(int i) {
        Log.m26i(TAG, "onSceneChange - scene : " + i);
        if (isAdded() && !isRemoving()) {
            this.mScene = i;
            if (i == 6 && Recorder.getInstance().getRecorderState() != 2) {
                if (Player.getInstance().isIsRunningSwitchSkipMuted()) {
                    new Handler().postDelayed(new Runnable() {
                        public final void run() {
                            InfoFragment.this.showDuration();
                        }
                    }, 400);
                } else {
                    showDuration();
                }
            }
        }
    }

    private void updateCurrentTime(int i) {
        long j = (long) i;
        if (j - this.mLastUpdateLogTime > 1000) {
            Log.m26i(TAG, "updateCurrentTime : " + i);
            this.mLastUpdateLogTime = j;
        }
        if (this.mScene == 12 && i > 0) {
            i -= Engine.getInstance().getTrimStartTime();
        }
        String stringByDuration = getStringByDuration(i, true);
        setTextTimeView(stringByDuration, i);
        this.mTimeLayout.setContentDescription(AssistantProvider.getInstance().stringForReadTime(stringByDuration));
    }

    private void showMaxTime(int i) {
        Log.m26i(TAG, "showMaxTime - recordMode : " + i);
        if (i == 4 || i == 6) {
            int maxDuration = Engine.getInstance().getAudioFormat().getMaxDuration();
            this.mMaxTextView.setVisibility(0);
            this.mMaxDuration.setVisibility(0);
            String stringByDuration = getStringByDuration(maxDuration, false);
            this.mMaxDuration.setText(stringByDuration);
            LinearLayout linearLayout = this.mMaxLayout;
            linearLayout.setContentDescription(getActivity().getString(C0690R.string.play_time) + ", " + AssistantProvider.getInstance().stringForReadTime(stringByDuration));
            return;
        }
        this.mMaxTextView.setVisibility(8);
        this.mMaxDuration.setVisibility(8);
    }

    /* access modifiers changed from: private */
    public void showDuration() {
        String str;
        Log.m26i(TAG, "showDuration");
        if (this.mScene == 8) {
            Log.m26i(TAG, "SKIP showDuration Scene : " + this.mScene);
            return;
        }
        if (Engine.getInstance().isSimpleRecorderMode()) {
            this.mDuration = (int) DBProvider.getInstance().getFileDuration(Engine.getInstance().getSimpleModeItem());
        } else {
            this.mDuration = Engine.getInstance().getDuration();
        }
        if (this.mScene == 12) {
            str = getStringByDuration(this.mDuration - Engine.getInstance().getTrimStartTime(), true);
        } else {
            str = getStringByDuration(this.mDuration, true);
        }
        TextView textView = this.mMaxDuration;
        if (textView != null) {
            textView.setVisibility(0);
            this.mMaxDuration.setText(str);
        }
        TextView textView2 = this.mMaxTextView;
        if (textView2 != null) {
            textView2.setVisibility(8);
        }
        LinearLayout linearLayout = this.mMaxLayout;
        if (linearLayout != null) {
            linearLayout.setContentDescription(getActivity().getString(C0690R.string.play_time) + ", " + AssistantProvider.getInstance().stringForReadTime(str));
        }
    }

    public void onEngineUpdate(int i, int i2, int i3) {
        Handler handler = this.mEventHandler;
        if (handler != null) {
            handler.sendMessage(handler.obtainMessage(i, i2, i3));
        }
    }

    private int getMaxDuration() {
        long durationBySize;
        int i = this.mRecordMode;
        if (i == 4) {
            return 600000;
        }
        if (i == 5) {
            durationBySize = AudioFormat.getDurationBySize(getActivity().getIntent().getStringExtra("mime_type"), getActivity().getIntent().getLongExtra("android.provider.MediaStore.extra.MAX_BYTES", 10247680));
        } else if (i != 6) {
            return 36000999;
        } else {
            durationBySize = AudioFormat.getDurationBySize(AudioFormat.getMineType(i), Settings.getMmsMaxSize());
        }
        return (int) durationBySize;
    }

    private String getStringByDuration(int i, boolean z) {
        int i2 = i / 1000;
        int i3 = (i / 10) - (i2 * 100);
        int i4 = i2 / 3600;
        int i5 = (i2 / 60) % 60;
        int i6 = i2 % 60;
        if (z) {
            if (i4 > 0) {
                return String.format(Locale.getDefault(), "%d:%02d:%02d.%02d", new Object[]{Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6), Integer.valueOf(i3)});
            }
            return String.format(Locale.getDefault(), "%02d:%02d.%02d", new Object[]{Integer.valueOf(i5), Integer.valueOf(i6), Integer.valueOf(i3)});
        } else if (i4 > 0) {
            return String.format(Locale.getDefault(), "%d:%02d:%02d", new Object[]{Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6)});
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", new Object[]{Integer.valueOf(i5), Integer.valueOf(i6)});
        }
    }

    private void setTextTimeView(String str, int i) {
        int i2;
        String[] split = str.split("\\.");
        int i3 = i / 1000;
        int i4 = (i3 / 60) % 60;
        int i5 = (i3 / 3600 != 0 || i4 >= 10) ? 0 : i4 >= 1 ? 1 : (i3 < 10 || i3 > 59) ? (i3 < 1 || i3 > 9) ? 5 : 4 : 3;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.insert(0, split[0]);
        spannableStringBuilder.setSpan(this.mTimeTextDimSpan, 0, i5, 17);
        if (this.mOldTextTimeLength != str.length()) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mTimeHmsTextView.getLayoutParams();
            if (str.length() > 10) {
                i2 = getMaxWidthTextInfo(this.mTimeHmsTextView.getPaint(), 8);
            } else if (str.length() > 8) {
                i2 = getMaxWidthTextInfo(this.mTimeHmsTextView.getPaint(), 7);
            } else {
                i2 = getMaxWidthTextInfo(this.mTimeHmsTextView.getPaint(), 5);
            }
            layoutParams.width = i2;
            this.mTimeHmsTextView.setLayoutParams(layoutParams);
            int maxWidthTextInfo = getMaxWidthTextInfo(this.mTimeMsTextView.getPaint(), 2);
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mTimeMsTextView.getLayoutParams();
            layoutParams2.width = maxWidthTextInfo;
            this.mTimeMsTextView.setLayoutParams(layoutParams2);
            this.mOldTextTimeLength = str.length();
        }
        this.mTimeHmsTextView.setText(spannableStringBuilder);
        this.mTimeMsTextView.setText(split[1]);
    }

    private int getMaxWidthTextInfo(Paint paint, int i) {
        if (this.mMaxLengthText == null) {
            int i2 = -1;
            for (int i3 = 0; i3 <= 9; i3++) {
                String format = String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(i3)});
                int measureText = ((int) paint.measureText(format)) + 1;
                if (measureText > i2) {
                    this.mMaxLengthText = format;
                    i2 = measureText;
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        if (i == 2) {
            sb.append(this.mMaxLengthText);
            sb.append(this.mMaxLengthText);
        } else if (i == 5) {
            sb.append(this.mMaxLengthText);
            sb.append(this.mMaxLengthText);
            sb.append(":");
            sb.append(this.mMaxLengthText);
            sb.append(this.mMaxLengthText);
        } else if (i == 7) {
            sb.append(this.mMaxLengthText);
            sb.append(":");
            sb.append(this.mMaxLengthText);
            sb.append(this.mMaxLengthText);
            sb.append(":");
            sb.append(this.mMaxLengthText);
            sb.append(this.mMaxLengthText);
        } else if (i == 8) {
            sb.append(this.mMaxLengthText);
            sb.append(this.mMaxLengthText);
            sb.append(":");
            sb.append(this.mMaxLengthText);
            sb.append(this.mMaxLengthText);
            sb.append(":");
            sb.append(this.mMaxLengthText);
            sb.append(this.mMaxLengthText);
        }
        return ((int) paint.measureText(sb.toString())) + i + 1;
    }

    private boolean needToShowRejectCallIcon() {
        if (SecureFolderProvider.isInSecureFolder() || Recorder.getInstance().getRecorderState() == 1) {
            return false;
        }
        if (!PermissionProvider.isCallRejectEnable(getActivity()) || !Settings.getBooleanSettings(Settings.KEY_REC_CALL_REJECT, false)) {
            return CallRejectChecker.getInstance().getReject();
        }
        return true;
    }
}
