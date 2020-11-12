package com.sec.android.app.voicenote.p007ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.activity.SelectLanguageActivity;
import com.sec.android.app.voicenote.activity.SimpleActivity;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.p007ui.recognizer.RecognizerData;
import com.sec.android.app.voicenote.p007ui.view.CenteredImageSpan;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.DesktopModeProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.HWKeyboardProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.MetadataRepository;
import com.sec.android.app.voicenote.service.Recorder;
import com.sec.android.app.voicenote.service.VoiceNoteService;
import com.sec.android.app.voicenote.service.decoder.Decoder;
import com.sec.android.app.voicenote.service.recognizer.VoiceWorker;
import com.sec.android.app.voicenote.service.remote.RemoteViewManager;
import com.sec.android.app.voicenote.uicore.FragmentController;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

/* renamed from: com.sec.android.app.voicenote.ui.SttFragment */
public class SttFragment extends AbsFragment implements Engine.OnEngineListener, VoiceWorker.StatusChangedListener, DialogFactory.DialogResultListener, FragmentController.OnSceneChangeListener, Decoder.onDecoderListener {
    private static final int FLAG_EXCLUSIVE = 33;
    private static final int MAX_DELAY_TIME = 4500;
    private static final int MAX_DOT_ANIMATION_TIME = 100;
    private static final int MAX_EDIT_TIME = 2000;
    private static final int MAX_KEEP_SCROLL_TIME = 2000;
    private static final String MOVE_DOWN = "down";
    private static final String MOVE_UP = "up";
    private static final int MSG_DELAY_SAVE = 1000;
    private static final int MSG_DOT_ANIMATION = 1003;
    private static final int MSG_HIDE_PROGRESS = 1005;
    private static final int MSG_SCROLL_MOVE = 1002;
    private static final int MSG_SHOW_ERROR = 1001;
    private static final int MSG_SHOW_PROGRESS = 1004;
    private static final String TAG = "SttFragment";
    private static final StyleSpan mBoldSpan = new StyleSpan(1);
    /* access modifiers changed from: private */
    public static final int[] mDotFrames = {C0690R.C0692drawable.vi_memo_dot_01, C0690R.C0692drawable.vi_memo_dot_02, C0690R.C0692drawable.vi_memo_dot_03};
    private static ForegroundColorSpan mForegroundBlackSpan;
    private static ForegroundColorSpan mForegroundBookmarkSpan;
    private static ForegroundColorSpan mForegroundInactiveSpan;
    private static ForegroundColorSpan mForegroundPlayedSpan;
    private static ForegroundColorSpan mForegroundRepeatSpan;
    private static ForegroundColorSpan mForegroundSearchedSpan;
    private static ForegroundColorSpan mForegroundSelectedSpan;
    /* access modifiers changed from: private */
    public int mCurrentFrameNumber;
    private long mCurrentTime = 0;
    private Handler mDelayHandler = new DelayHandler();
    private SpannableStringBuilder mDisplayString;
    private Handler mEngineEventHandler;
    /* access modifiers changed from: private */
    public final Handler mEventHandler = new Handler(new Handler.Callback() {
        public boolean handleMessage(Message message) {
            if (!SttFragment.this.isValidFragment()) {
                return false;
            }
            switch (message.what) {
                case 1000:
                    Log.m26i(SttFragment.TAG, "handleMessage() : MSG_DELAY_SAVE");
                    SttFragment.this.saveSttData();
                    break;
                case 1001:
                    if (SttFragment.this.isResumed()) {
                        Toast.makeText(SttFragment.this.getActivity(), C0690R.string.network_error, 0).show();
                        break;
                    } else {
                        return false;
                    }
                case 1002:
                    boolean unused = SttFragment.this.mIsScrollMoved = false;
                    break;
                case 1003:
                    SttFragment.access$408(SttFragment.this);
                    if (SttFragment.this.mCurrentFrameNumber >= SttFragment.mDotFrames.length) {
                        int unused2 = SttFragment.this.mCurrentFrameNumber = 0;
                    }
                    if (Engine.getInstance().getPlayerState() != 3 && Engine.getInstance().getRecorderState() != 2) {
                        SttFragment.this.mEventHandler.removeMessages(1003);
                        break;
                    } else {
                        SttFragment.this.drawDotAnimation();
                        SttFragment.this.mEventHandler.sendEmptyMessageDelayed(1003, 100);
                        break;
                    }
//                    break;
                case 1004:
                    VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.TRANSLATION_SHOW_PROGRESS));
                    break;
                case 1005:
                    VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.TRANSLATION_HIDE_PROGRESS));
                    break;
            }
            return false;
        }
    });
    private boolean mIsLastWord = false;
    private boolean mIsLastWordSaved = false;
    private boolean mIsLongPressed;
    private boolean mIsNeedPlayResume;
    private boolean mIsNeedScrollToFocus;
    /* access modifiers changed from: private */
    public boolean mIsScrollMoved;
    private boolean mIsSttViewMoved;
    private boolean mIsTouched;
    private int mNumberOfRecognition;
    private RecognizerData mRecognizerData;
    private int mScene = 0;
    private ScrollView mScrollView;
    private RelativeLayout mScrollViewRelative;
    private String mSearchResult;
    private TextView mSelectLanguageButton;
    private boolean mSttDataChanged = false;
    private String[] mSttDefaultLocales;
    private String[] mSttLocales;
    private TextView mSttTextView;
    private String[] mSttTexts;

    public void onDecoderProgress(int i) {
    }

    static /* synthetic */ int access$408(SttFragment sttFragment) {
        int i = sttFragment.mCurrentFrameNumber;
        sttFragment.mCurrentFrameNumber = i + 1;
        return i;
    }

    public void onCreate(Bundle bundle) {
        Log.m26i(TAG, "onCreate() : savedInstanceState = " + bundle);
        super.onCreate(bundle);
        Resources resources = getResources();
        mForegroundSearchedSpan = new ForegroundColorSpan(resources.getColor(C0690R.C0691color.stt_searched_span, (Resources.Theme) null));
        mForegroundRepeatSpan = new ForegroundColorSpan(resources.getColor(C0690R.C0691color.stt_repeat_span, (Resources.Theme) null));
        mForegroundInactiveSpan = new ForegroundColorSpan(resources.getColor(C0690R.C0691color.stt_inactive_span, (Resources.Theme) null));
        mForegroundPlayedSpan = new ForegroundColorSpan(resources.getColor(C0690R.C0691color.stt_played_span, (Resources.Theme) null));
        mForegroundBookmarkSpan = new ForegroundColorSpan(resources.getColor(C0690R.C0691color.stt_bookmark_span, (Resources.Theme) null));
        mForegroundSelectedSpan = new ForegroundColorSpan(resources.getColor(C0690R.C0691color.stt_selected_span, (Resources.Theme) null));
        this.mEngineEventHandler = new Handler(new Handler.Callback() {
            public final boolean handleMessage(Message message) {
                return SttFragment.this.lambda$onCreate$0$SttFragment(message);
            }
        });
    }

    public /* synthetic */ boolean lambda$onCreate$0$SttFragment(Message message) {
        if (!isValidFragment()) {
            return false;
        }
        Log.m29v(TAG, "mEngineEventHandler : msg.what = " + message.what + ", msg.arg1 = " + message.arg1);
        updateDisplayTextList();
        int i = message.what;
        if (i == 1010) {
            if (message.arg1 == 3) {
                Log.m26i(TAG, "mEngineEventHandler : RecorderState.PAUSED " + Engine.getInstance().getContentItemCount());
                this.mNumberOfRecognition = 0;
                this.mEventHandler.removeMessages(1003);
                this.mRecognizerData.addLastWordPartialText();
                this.mRecognizerData.loadSttDataFromFile();
                this.mRecognizerData.updatePlayedTime(Engine.getInstance().getCurrentTime());
                this.mIsTouched = false;
                paintPlayingData();
            }
            if (message.arg1 == 2) {
                getActivity().getWindow().addFlags(128);
            } else {
                getActivity().getWindow().clearFlags(128);
            }
        } else if (i != 2013) {
            if (i == 3010) {
                switch (message.arg1) {
                    case 0:
                    case 1:
                    case 2:
                    case 10:
                    case 11:
                    case 12:
                    case 15:
                    case 16:
                        Log.m19d(TAG, "mEngineEventHandler : Editor OVERWRITE Event = " + message.arg1);
                        break;
                    case 3:
                    case 5:
                    case 13:
                    case 14:
                        Log.m19d(TAG, "mEngineEventHandler : Editor TRIM Event = " + message.arg1);
                        break;
                    case 4:
                        Log.m26i(TAG, "mEngineEventHandler : Editor.TRIM_COMPLETE");
                        initialize(true);
                        paintPlayingData();
                        this.mRecognizerData.loadSttDataFromFile();
                        break;
                    case 6:
                    case 8:
                        Log.m19d(TAG, "mEngineEventHandler : Editor DELETE Event = " + message.arg1);
                        break;
                    case 7:
                        Log.m26i(TAG, "mEngineEventHandler : Editor.DELETE_COMPLETE");
                        initialize(true);
                        paintPlayingData();
                        this.mRecognizerData.loadSttDataFromFile();
                        break;
                }
            } else if (i == 2010) {
                if (message.arg1 == 1) {
                    this.mRecognizerData.updatePlayedTime(0);
                    paintPlayingData();
                }
                if (message.arg1 == 3) {
                    getActivity().getWindow().addFlags(128);
                } else {
                    getActivity().getWindow().clearFlags(128);
                }
            } else if (i != 2011) {
                switch (i) {
                    case 101:
                        this.mRecognizerData.updatePlayedTime(message.arg1);
                        if (!(this.mIsLastWord || Engine.getInstance().getRecorderState() == 2 || Engine.getInstance().getTranslationState() == 2)) {
                            paintPlayingData();
                            break;
                        }
                    case 102:
                        this.mRecognizerData.updateTrimTime();
                        paintPlayingData();
                        break;
                    case 103:
                        Log.m26i(TAG, "mEngineEventHandler : Engine.INFO_SAVED_ID : " + message.arg1);
                        if (message.arg1 > 0 && (!this.mEventHandler.hasMessages(1000) || RemoteViewManager.isRunning())) {
                            this.mEventHandler.sendEmptyMessage(1000);
                            break;
                        }
                    case 104:
                        Log.m19d(TAG, "INFO_REPEAT_TIME");
                        if (Engine.getInstance().getRepeatMode() == 4) {
                            int[] repeatPosition = Engine.getInstance().getRepeatPosition();
                            this.mRecognizerData.updateRepeatTime(repeatPosition[0], repeatPosition[1]);
                            paintPlayingData();
                            break;
                        }
                        break;
                }
            } else {
                ScrollView scrollView = this.mScrollView;
                if (scrollView != null) {
                    scrollView.smoothScrollTo(0, 0);
                }
            }
        } else {
            int repeatMode = Engine.getInstance().getRepeatMode();
            int[] repeatPosition2 = Engine.getInstance().getRepeatPosition();
            this.mRecognizerData.updateRepeatTime(repeatPosition2[0], repeatPosition2[1]);
            if (repeatMode == 2 || (repeatMode != 3 && repeatMode == 4)) {
                paintPlayingData();
            }
        }
        return false;
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        boolean z;
        Log.m26i(TAG, "onCreateView() : savedInstanceState = " + bundle);
        mForegroundBlackSpan = new ForegroundColorSpan(getResources().getColor(C0690R.C0691color.stt_black_span, (Resources.Theme) null));
        this.mRecognizerData = new RecognizerData();
        if (getActivity() instanceof SimpleActivity) {
            Log.m19d(TAG, "onCreateView() : Called from SimpleActivity");
            z = false;
        } else {
            z = true;
        }
        View inflate = layoutInflater.inflate(C0690R.layout.fragment_stt, viewGroup, false);
        this.mSttTextView = (TextView) inflate.findViewById(C0690R.C0693id.stt_textview);
        if (VoiceNoteFeature.FLAG_IS_TABLET) {
            this.mSttTextView.setTextSize(0, (float) getResources().getDimensionPixelSize(C0690R.dimen.tablet_stt_text_size));
        }
        if (Build.VERSION.SDK_INT >= 28) {
            this.mSttTextView.setFallbackLineSpacing(false);
        }
        this.mSttTextView.setTextIsSelectable(z);
//        this.mSttTextView.semSetMultiSelectionEnabled(false);
        this.mSttTextView.setRawInputType(0);
        this.mSttTextView.setImportantForAccessibility(2);
        this.mSttTextView.setFocusable(false);
        if (z) {
            this.mSttTextView.setOnTouchListener(new View.OnTouchListener() {
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    return SttFragment.this.lambda$onCreateView$1$SttFragment(view, motionEvent);
                }
            });
            this.mSttTextView.setOnLongClickListener(new View.OnLongClickListener() {
                public final boolean onLongClick(View view) {
                    return SttFragment.this.lambda$onCreateView$2$SttFragment(view);
                }
            });
        }
        this.mDisplayString = new SpannableStringBuilder();
        FragmentController.getInstance().registerSceneChangeListener(this);
        VoiceWorker.getInstance().registerListener(this);
        Decoder.getInstance().registerListener(this);
        this.mScrollViewRelative = (RelativeLayout) inflate.findViewById(C0690R.C0693id.stt_scrollview_relative_layout);
        this.mScrollView = (ScrollView) inflate.findViewById(C0690R.C0693id.stt_scrollview);
        this.mScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            public final void onScrollChange(View view, int i, int i2, int i3, int i4) {
                SttFragment.this.lambda$onCreateView$3$SttFragment(view, i, i2, i3, i4);
            }
        });
        this.mScrollView.setOnTouchListener(new View.OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return SttFragment.this.lambda$onCreateView$4$SttFragment(view, motionEvent);
            }
        });
        this.mScrollView.smoothScrollTo(0, 0);
        this.mSelectLanguageButton = (TextView) inflate.findViewById(C0690R.C0693id.stt_language_button);
        getVoiceMemoStringArrays();
        String stringSettings = Settings.getStringSettings(Settings.KEY_STT_LANGUAGE_TEXT);
        if (stringSettings == null) {
            stringSettings = getDefaultLanguage();
        }
        if (this.mSelectLanguageButton != null) {
            if (getResources().getConfiguration().getLayoutDirection() == 1) {
                this.mSelectLanguageButton.setBackgroundResource(C0690R.C0692drawable.voice_ripple_stt_language_btn_rtl);
            } else {
                this.mSelectLanguageButton.setBackgroundResource(C0690R.C0692drawable.voice_ripple_stt_language_btn);
            }
            this.mSelectLanguageButton.setText(stringSettings);
            this.mSelectLanguageButton.setContentDescription(stringSettings);
            this.mSelectLanguageButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    SttFragment.this.lambda$onCreateView$5$SttFragment(view);
                }
            });
        }
        initialize(false);
        onUpdate(Integer.valueOf(this.mStartingEvent));
        return inflate;
    }

    public /* synthetic */ boolean lambda$onCreateView$1$SttFragment(View view, MotionEvent motionEvent) {
        if (getActivity() == null) {
            return true;
        }
        int action = motionEvent.getAction();
        if (action == 0) {
            int recorderState = Engine.getInstance().getRecorderState();
            if (recorderState != 2) {
                if (this.mScene == 8 && (recorderState == 3 || recorderState == 4)) {
                    this.mRecognizerData.updateTouchedXY((int) motionEvent.getX(), (int) motionEvent.getY());
                    int touchedViewIndex = getTouchedViewIndex();
                    long touchedWordTimeStamp = this.mRecognizerData.getTouchedWordTimeStamp(touchedViewIndex);
                    SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_recording_STT), getActivity().getResources().getString(C0690R.string.event_seek_text));
                    if (touchedWordTimeStamp == -1) {
                        Log.m26i(TAG, "cannot find word at character index : " + touchedViewIndex);
                    } else {
                        moveTimeHandlerToTextPos((int) touchedWordTimeStamp);
                    }
                } else {
                    this.mRecognizerData.updateTouchedXY((int) motionEvent.getX(), (int) motionEvent.getY());
                    this.mIsTouched = true;
                }
            }
        } else if (action == 1) {
            this.mIsTouched = false;
            if (this.mIsLongPressed) {
                this.mIsLongPressed = false;
                return false;
            }
            int touchedViewIndex2 = getTouchedViewIndex();
            if (this.mScene == 4) {
                long touchedWordTimeStamp2 = this.mRecognizerData.getTouchedWordTimeStamp(touchedViewIndex2);
                if (touchedWordTimeStamp2 > 0) {
                    Engine.getInstance().seekTo((int) touchedWordTimeStamp2);
                    SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_player_STT), getActivity().getResources().getString(C0690R.string.event_player_seek_text));
                }
            } else if (HWKeyboardProvider.isHWKeyboard(getActivity())) {
                return false;
            } else {
                this.mRecognizerData.updateTouchedWordIndex(touchedViewIndex2);
                if (this.mRecognizerData.getTouchedIndex() == -1) {
                    Log.m26i(TAG, "cannot find word at character index : " + touchedViewIndex2);
                    return false;
                }
                if (Engine.getInstance().getPlayerState() == 3) {
                    Engine.getInstance().pausePlay();
                    this.mIsNeedPlayResume = true;
                } else {
                    this.mIsNeedPlayResume = false;
                }
                if (VoiceNoteApplication.getScene() == 12) {
                    postEvent(Event.TRANSLATION_PAUSE);
                    Engine.getInstance().pauseTranslation(false);
                }
                this.mRecognizerData.updateTouchedXY(-1, -1);
                paintPlayingData();
                moveSttView(MOVE_UP);
                showEditSttDialog();
            }
        } else if (action == 3) {
            this.mRecognizerData.updateTouchedXY(-1, -1);
            this.mIsTouched = false;
        }
        return false;
    }

    public /* synthetic */ boolean lambda$onCreateView$2$SttFragment(View view) {
        this.mSttTextView.setFocusableInTouchMode(true);
        this.mRecognizerData.updateTouchedWordIndex(getTouchedViewIndex());
        if (this.mRecognizerData.getTouchedIndex() != -1) {
            this.mIsLongPressed = true;
            if (Engine.getInstance().getPlayerState() == 3) {
                Engine.getInstance().pausePlay();
                int i = this.mScene;
                if (i == 4) {
                    postEvent(Event.PLAY_PAUSE);
                } else if (i == 6) {
                    postEvent(Event.EDIT_PLAY_PAUSE);
                }
            }
            if (VoiceNoteApplication.getScene() == 12) {
                postEvent(Event.TRANSLATION_PAUSE);
                Engine.getInstance().pauseTranslation(false);
            }
            resetTouchedArea();
        }
        return false;
    }

    public /* synthetic */ void lambda$onCreateView$3$SttFragment(View view, int i, int i2, int i3, int i4) {
        this.mIsScrollMoved = true;
        this.mEventHandler.removeMessages(1002);
        this.mEventHandler.sendEmptyMessageDelayed(1002, 2000);
    }

    public /* synthetic */ boolean lambda$onCreateView$4$SttFragment(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 1) {
            this.mEventHandler.removeMessages(1002);
            this.mEventHandler.sendEmptyMessageDelayed(1002, 2000);
            return false;
        } else if (action != 2) {
            return false;
        } else {
            this.mIsScrollMoved = true;
            this.mEventHandler.removeMessages(1002);
            return false;
        }
    }

    public /* synthetic */ void lambda$onCreateView$5$SttFragment(View view) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.startActivity(new Intent(activity, SelectLanguageActivity.class));
            SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_ready_convert_stt), getActivity().getResources().getString(C0690R.string.event_convert_language));
        }
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        Engine.getInstance().registerListener(this);
        updateTabletMultiWindowLayoutView();
    }

    public void onStart() {
        super.onStart();
        if (this.mScene == 12 && Engine.getInstance().getTranslationState() == 1) {
            onUpdate(17);
        }
    }

    public void onResume() {
        Log.m26i(TAG, "onResume()");
        super.onResume();
        TextView textView = this.mSelectLanguageButton;
        if (textView != null) {
            textView.setText(Settings.getStringSettings(Settings.KEY_STT_LANGUAGE_TEXT));
            this.mSelectLanguageButton.setContentDescription(Settings.getStringSettings(Settings.KEY_STT_LANGUAGE_TEXT));
        }
        if (Engine.getInstance().getRecorderState() != 1) {
            updateDisplayText();
        }
        if (Engine.getInstance().getTranslationState() != 1) {
            updateDisplayText();
        }
        paintSttData();
    }

    public void onPause() {
        Log.m26i(TAG, "onPause()");
        super.onPause();
        if (this.mScene == 12 && (Engine.getInstance().getPlayerState() == 3 || Engine.getInstance().getPlayerState() == 4)) {
            Engine.getInstance().setVolume(0.0f, 0.0f);
        }
        getActivity().getWindow().clearFlags(128);
    }

    public void onStop() {
        Log.m26i(TAG, "onStop()");
        super.onStop();
        if (Engine.getInstance().getRecorderState() != 1 || Engine.getInstance().getPlayerState() != 1 || this.mScene == 6) {
            return;
        }
        if (!this.mIsLastWord || this.mIsLastWordSaved) {
            onClearScreen();
        }
    }

    public void onDestroyView() {
        Log.m26i(TAG, "onDestroyView()");
        Engine.getInstance().unregisterListener(this);
        VoiceWorker.getInstance().unregisterListener(this);
        Decoder.getInstance().unregisterListener();
        FragmentController.getInstance().unregisterSceneChangeListener(this);
        this.mEventHandler.removeMessages(1000);
        this.mEventHandler.removeMessages(1003);
        this.mEventHandler.removeMessages(1004);
        this.mEventHandler.removeMessages(1005);
        if (this.mIsLastWord && !this.mIsLastWordSaved) {
            this.mIsLastWordSaved = true;
            this.mRecognizerData.writeMetaData(getContext());
            Engine.getInstance().updateLastWord();
        }
        SpannableStringBuilder spannableStringBuilder = this.mDisplayString;
        if (spannableStringBuilder != null) {
            spannableStringBuilder.clear();
            this.mDisplayString = null;
        }
        this.mSearchResult = null;
        moveSttView(MOVE_DOWN);
        this.mRecognizerData.clearVariables(true);
        if (getActivity() != null && !getActivity().isDestroyed()) {
            DialogFactory.clearDialogByTag(getFragmentManager(), DialogFactory.EDIT_STT_DIALOG);
        }
        super.onDestroyView();
    }

    public void onDestroy() {
        Log.m26i(TAG, "onDestroy()");
        this.mEngineEventHandler = null;
        super.onDestroy();
    }

    public void onEngineUpdate(int i, int i2, int i3) {
        Handler handler = this.mEngineEventHandler;
        if (handler != null) {
            handler.sendMessage(handler.obtainMessage(i, i2, i3));
        }
    }

    public void onUpdate(Object obj) {
        StringBuilder sb = new StringBuilder();
        sb.append("onUpdate : ");
        sb.append(obj);
        sb.append("> ");
        Integer num = (Integer) obj;
        sb.append(Event.getEventName(num.intValue()));
        Log.m26i(TAG, sb.toString());
        if (isValidFragment()) {
            int intValue = num.intValue();
            this.mCurrentEvent = intValue;
            this.mStartingEvent = intValue;
            int intValue2 = num.intValue();
            if (intValue2 == 5) {
                this.mRecognizerData.resetOverwriteArea();
                this.mSearchResult = null;
            } else if (intValue2 == 17) {
                Log.m26i(TAG, " TRANSLATION");
                TextView textView = this.mSelectLanguageButton;
                if (textView != null) {
                    textView.setVisibility(0);
                }
                TextView textView2 = this.mSttTextView;
                if (textView2 != null) {
                    textView2.setTextIsSelectable(false);
                }
                ScrollView scrollView = this.mScrollView;
                if (scrollView != null) {
                    scrollView.scrollTo(0, 0);
                }
            } else if (intValue2 == 975) {
                initialize(true);
            } else if (intValue2 == 978 || intValue2 == 996) {
                this.mRecognizerData.updateBookmark();
                if (Engine.getInstance().getRecorderState() == 2) {
                    paintRecordingData();
                } else {
                    paintPlayingData();
                }
            } else if (intValue2 == 1005) {
                Log.m26i(TAG, "onUpdate : Event.RECORD_STOP_DELAYED : " + this.mIsLastWord);
                if (!this.mIsLastWord && !this.mEventHandler.hasMessages(1000)) {
                    if (this.mScene == 6) {
                        postEvent(3);
                    } else {
                        postEvent(1004);
                    }
                }
            } else if (intValue2 != 2003) {
                if (intValue2 != 5012) {
                    if (intValue2 == 7005) {
                        onClearScreen();
                        updateDisplayText();
                        return;
                    } else if (intValue2 == 7007) {
                        this.mIsLastWord = false;
                        this.mRecognizerData.loadSttDataFromFile();
                        return;
                    } else if (intValue2 == 2005 || intValue2 == 2006) {
                        DialogFactory.clearDialogByTag(getFragmentManager(), DialogFactory.EDIT_STT_DIALOG);
                        initialize(true);
                        paintPlayingData();
                        return;
                    } else if (intValue2 == 5004) {
                        this.mRecognizerData.updateOverwriteTime();
                        this.mSttTextView.setTextIsSelectable(false);
                        this.mIsLastWordSaved = false;
                        this.mNumberOfRecognition = 0;
                        this.mRecognizerData.loadSttDataFromFile();
                        paintRecordingData();
                        return;
                    } else if (intValue2 != 5005) {
                        switch (intValue2) {
                            case 1001:
                                this.mSttTextView.setTextIsSelectable(false);
                                this.mIsLastWordSaved = false;
                                this.mNumberOfRecognition = 0;
                                return;
                            case 1002:
                                return;
                            case 1003:
                                this.mRecognizerData.updateOverwriteTime();
                                this.mSttTextView.setTextIsSelectable(false);
                                this.mIsLastWordSaved = false;
                                this.mNumberOfRecognition = 0;
                                paintRecordingData();
                                return;
                            default:
                                switch (intValue2) {
                                    case Event.TRANSLATION_START:
                                        Log.m26i(TAG, " TRANSLATION_START");
                                        TextView textView3 = this.mSelectLanguageButton;
                                        if (textView3 != null && textView3.getVisibility() == 0) {
                                            this.mSelectLanguageButton.setVisibility(8);
                                            return;
                                        }
                                        return;
                                    case Event.TRANSLATION_PAUSE:
                                        TextView textView4 = this.mSelectLanguageButton;
                                        if (textView4 != null && textView4.getVisibility() == 0) {
                                            this.mSelectLanguageButton.setVisibility(8);
                                        }
                                        this.mSttTextView.setTextIsSelectable(false);
                                        return;
                                    case Event.TRANSLATION_RESUME:
                                        TextView textView5 = this.mSelectLanguageButton;
                                        if (textView5 != null && textView5.getVisibility() == 0) {
                                            this.mSelectLanguageButton.setVisibility(8);
                                        }
                                        paintTranslationData();
                                        return;
                                    default:
                                        return;
                                }
                        }
                    }
                }
                this.mRecognizerData.updateTrimTime();
                this.mSttTextView.setTextIsSelectable(true);
                paintTrimArea();
                paintPlayingData();
            } else {
                if (VoiceNoteService.Helper.connectionCount() == 0) {
                    DialogFactory.clearDialogByTag(getFragmentManager(), DialogFactory.EDIT_STT_DIALOG);
                    initialize(true);
                    paintPlayingData();
                }
                this.mSttTextView.setFocusable(false);
            }
        }
    }

    public void onSceneChange(int i) {
        Log.m26i(TAG, "onSceneChange() : mScene = " + this.mScene + " scene = " + i);
        if (this.mScene == 6 && i == 4 && this.mSttDataChanged) {
            String recentFilePath = Engine.getInstance().getRecentFilePath();
            if (recentFilePath != null && !recentFilePath.isEmpty()) {
                MetadataRepository.getInstance().writeSttDataInFile(recentFilePath, this.mRecognizerData.getDisplayedSttData());
            }
            this.mSttDataChanged = false;
        }
        this.mScene = i;
        this.mRecognizerData.updateScene(i);
        if (this.mScene == 4 && this.mDisplayString != null && this.mSttTextView != null) {
            this.mSearchResult = CursorProvider.getInstance().getRecordingSearchTag();
            paintPlayingData();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x00a2, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void onResultWord(java.util.ArrayList<com.sec.android.app.voicenote.common.util.TextData> r4) {
        /*
            r3 = this;
            monitor-enter(r3)
            java.lang.String r0 = "SttFragment"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a3 }
            r1.<init>()     // Catch:{ all -> 0x00a3 }
            java.lang.String r2 = "onResultWord() : mNumberOfRecognition = "
            r1.append(r2)     // Catch:{ all -> 0x00a3 }
            int r2 = r3.mNumberOfRecognition     // Catch:{ all -> 0x00a3 }
            int r2 = r2 + -1
            r3.mNumberOfRecognition = r2     // Catch:{ all -> 0x00a3 }
            r1.append(r2)     // Catch:{ all -> 0x00a3 }
            java.lang.String r2 = ", Record state = "
            r1.append(r2)     // Catch:{ all -> 0x00a3 }
            com.sec.android.app.voicenote.service.Engine r2 = com.sec.android.app.voicenote.service.Engine.getInstance()     // Catch:{ all -> 0x00a3 }
            int r2 = r2.getRecorderState()     // Catch:{ all -> 0x00a3 }
            r1.append(r2)     // Catch:{ all -> 0x00a3 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x00a3 }
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r1)     // Catch:{ all -> 0x00a3 }
            java.lang.String r0 = "SttFragment"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a3 }
            r1.<init>()     // Catch:{ all -> 0x00a3 }
            java.lang.String r2 = "onResultWord() : mIsLastWord = "
            r1.append(r2)     // Catch:{ all -> 0x00a3 }
            boolean r2 = r3.mIsLastWord     // Catch:{ all -> 0x00a3 }
            r1.append(r2)     // Catch:{ all -> 0x00a3 }
            java.lang.String r2 = ", mIsLastWordSaved = "
            r1.append(r2)     // Catch:{ all -> 0x00a3 }
            boolean r2 = r3.mIsLastWordSaved     // Catch:{ all -> 0x00a3 }
            r1.append(r2)     // Catch:{ all -> 0x00a3 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x00a3 }
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r1)     // Catch:{ all -> 0x00a3 }
            android.os.Handler r0 = r3.mEventHandler     // Catch:{ all -> 0x00a3 }
            r1 = 1003(0x3eb, float:1.406E-42)
            r0.removeMessages(r1)     // Catch:{ all -> 0x00a3 }
            boolean r0 = r3.mIsLastWordSaved     // Catch:{ all -> 0x00a3 }
            if (r0 == 0) goto L_0x005c
            monitor-exit(r3)
            return
        L_0x005c:
            boolean r0 = r3.mIsLastWord     // Catch:{ all -> 0x00a3 }
            if (r0 == 0) goto L_0x0066
            com.sec.android.app.voicenote.ui.recognizer.RecognizerData r0 = r3.mRecognizerData     // Catch:{ all -> 0x00a3 }
            r0.addLastWordText(r4)     // Catch:{ all -> 0x00a3 }
            goto L_0x006b
        L_0x0066:
            com.sec.android.app.voicenote.ui.recognizer.RecognizerData r4 = r3.mRecognizerData     // Catch:{ all -> 0x00a3 }
            r4.addResultText()     // Catch:{ all -> 0x00a3 }
        L_0x006b:
            r3.updateDisplayText()     // Catch:{ all -> 0x00a3 }
            int r4 = r3.mScene     // Catch:{ all -> 0x00a3 }
            r0 = 12
            if (r4 != r0) goto L_0x0078
            r3.paintTranslationData()     // Catch:{ all -> 0x00a3 }
            goto L_0x007b
        L_0x0078:
            r3.paintRecordingData()     // Catch:{ all -> 0x00a3 }
        L_0x007b:
            boolean r4 = r3.mIsLastWord     // Catch:{ all -> 0x00a3 }
            if (r4 == 0) goto L_0x00a1
            com.sec.android.app.voicenote.service.Engine r4 = com.sec.android.app.voicenote.service.Engine.getInstance()     // Catch:{ all -> 0x00a3 }
            int r4 = r4.getPlayerState()     // Catch:{ all -> 0x00a3 }
            r0 = 3
            if (r4 == r0) goto L_0x00a1
            com.sec.android.app.voicenote.service.Engine r4 = com.sec.android.app.voicenote.service.Engine.getInstance()     // Catch:{ all -> 0x00a3 }
            int r4 = r4.getPlayerState()     // Catch:{ all -> 0x00a3 }
            r0 = 4
            if (r4 == r0) goto L_0x00a1
            android.os.Handler r4 = r3.mEventHandler     // Catch:{ all -> 0x00a3 }
            r0 = 1000(0x3e8, float:1.401E-42)
            r4.removeMessages(r0)     // Catch:{ all -> 0x00a3 }
            android.os.Handler r4 = r3.mEventHandler     // Catch:{ all -> 0x00a3 }
            r4.sendEmptyMessage(r0)     // Catch:{ all -> 0x00a3 }
        L_0x00a1:
            monitor-exit(r3)
            return
        L_0x00a3:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.SttFragment.onResultWord(java.util.ArrayList):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0092, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0094, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void onPartialResultWord(java.util.ArrayList<com.sec.android.app.voicenote.common.util.TextData> r4) {
        /*
            r3 = this;
            monitor-enter(r3)
            java.lang.String r0 = "SttFragment"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0095 }
            r1.<init>()     // Catch:{ all -> 0x0095 }
            java.lang.String r2 = "onPartialResultWord() : mNumberOfRecognition = "
            r1.append(r2)     // Catch:{ all -> 0x0095 }
            int r2 = r3.mNumberOfRecognition     // Catch:{ all -> 0x0095 }
            r1.append(r2)     // Catch:{ all -> 0x0095 }
            java.lang.String r2 = ", Record state = "
            r1.append(r2)     // Catch:{ all -> 0x0095 }
            com.sec.android.app.voicenote.service.Engine r2 = com.sec.android.app.voicenote.service.Engine.getInstance()     // Catch:{ all -> 0x0095 }
            int r2 = r2.getRecorderState()     // Catch:{ all -> 0x0095 }
            r1.append(r2)     // Catch:{ all -> 0x0095 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0095 }
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r1)     // Catch:{ all -> 0x0095 }
            java.lang.String r0 = "SttFragment"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0095 }
            r1.<init>()     // Catch:{ all -> 0x0095 }
            java.lang.String r2 = "onPartialResultWord() : mIsLastWord = "
            r1.append(r2)     // Catch:{ all -> 0x0095 }
            boolean r2 = r3.mIsLastWord     // Catch:{ all -> 0x0095 }
            r1.append(r2)     // Catch:{ all -> 0x0095 }
            java.lang.String r2 = ", mIsLastWordSaved = "
            r1.append(r2)     // Catch:{ all -> 0x0095 }
            boolean r2 = r3.mIsLastWordSaved     // Catch:{ all -> 0x0095 }
            r1.append(r2)     // Catch:{ all -> 0x0095 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0095 }
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r1)     // Catch:{ all -> 0x0095 }
            android.os.Handler r0 = r3.mEventHandler     // Catch:{ all -> 0x0095 }
            r1 = 1003(0x3eb, float:1.406E-42)
            r0.removeMessages(r1)     // Catch:{ all -> 0x0095 }
            boolean r0 = r3.mIsLastWordSaved     // Catch:{ all -> 0x0095 }
            if (r0 != 0) goto L_0x0093
            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()     // Catch:{ all -> 0x0095 }
            int r0 = r0.getRecorderState()     // Catch:{ all -> 0x0095 }
            r1 = 3
            if (r0 == r1) goto L_0x0093
            com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()     // Catch:{ all -> 0x0095 }
            int r0 = r0.getRecorderState()     // Catch:{ all -> 0x0095 }
            r1 = 4
            if (r0 != r1) goto L_0x006d
            goto L_0x0093
        L_0x006d:
            if (r4 == 0) goto L_0x0091
            com.sec.android.app.voicenote.ui.recognizer.RecognizerData r0 = r3.mRecognizerData     // Catch:{ all -> 0x0095 }
            r0.addPartialText(r4)     // Catch:{ all -> 0x0095 }
            com.sec.android.app.voicenote.service.MetadataRepository r4 = com.sec.android.app.voicenote.service.MetadataRepository.getInstance()     // Catch:{ all -> 0x0095 }
            com.sec.android.app.voicenote.ui.recognizer.RecognizerData r0 = r3.mRecognizerData     // Catch:{ all -> 0x0095 }
            java.util.ArrayList r0 = r0.getDisplayedSttData()     // Catch:{ all -> 0x0095 }
            r4.setDisplayedSttData(r0)     // Catch:{ all -> 0x0095 }
            r3.updateDisplayText()     // Catch:{ all -> 0x0095 }
            int r4 = r3.mScene     // Catch:{ all -> 0x0095 }
            r0 = 12
            if (r4 != r0) goto L_0x008e
            r3.paintTranslationData()     // Catch:{ all -> 0x0095 }
            goto L_0x0091
        L_0x008e:
            r3.paintRecordingData()     // Catch:{ all -> 0x0095 }
        L_0x0091:
            monitor-exit(r3)
            return
        L_0x0093:
            monitor-exit(r3)
            return
        L_0x0095:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.SttFragment.onPartialResultWord(java.util.ArrayList):void");
    }

    public void onRecognitionStart() {
        StringBuilder sb = new StringBuilder();
        sb.append("onRecognitionStart() : mNumberOfRecognition = ");
        int i = this.mNumberOfRecognition + 1;
        this.mNumberOfRecognition = i;
        sb.append(i);
        Log.m26i(TAG, sb.toString());
        if (!this.mEventHandler.hasMessages(1003) && !this.mIsLastWord) {
            sendMessageDotAnimation();
        }
    }

    public void onError(String str) {
        Log.m26i(TAG, "onError() : " + str + ", mNumberOfRecognition = " + this.mNumberOfRecognition);
        this.mNumberOfRecognition = 0;
        if (!this.mIsLastWord && Engine.getInstance().getRecorderState() == 2) {
            this.mEventHandler.removeMessages(1001);
            this.mEventHandler.sendEmptyMessage(1001);
        }
    }

    public void onClearScreen() {
        Log.m29v(TAG, "onClearScreen()");
        TextView textView = this.mSttTextView;
        if (textView != null) {
            textView.setText("");
        }
        this.mRecognizerData.clearVariables(false);
        SpannableStringBuilder spannableStringBuilder = this.mDisplayString;
        if (spannableStringBuilder != null) {
            spannableStringBuilder.clear();
        }
    }

    public void onIsLastWord(boolean z) {
        Log.m26i(TAG, "onIsLastWord() : " + z + ", mNumberOfRecognition = " + this.mNumberOfRecognition);
        this.mIsLastWord = z;
        int i = 0;
        if (this.mIsLastWord) {
            this.mEventHandler.sendEmptyMessage(1004);
            this.mEventHandler.removeMessages(1003);
            this.mEventHandler.removeMessages(1000);
            if (this.mNumberOfRecognition > 0) {
                i = MAX_DELAY_TIME;
            } else if (Engine.getInstance().getEditorState() != 1) {
                i = Recorder.CHECK_AVAIABLE_STORAGE;
            }
            Log.m26i(TAG, "onIsLastWord() delay time :  " + i);
            this.mEventHandler.sendEmptyMessageDelayed(1000, (long) i);
            if (this.mScene == 12) {
                paintTranslationData();
            } else {
                paintRecordingData();
            }
        } else {
            this.mIsLastWordSaved = false;
        }
    }

    private void initialize(boolean z) {
        Log.m29v(TAG, "initialize(" + z + ')');
        this.mIsTouched = false;
        this.mIsLongPressed = false;
        this.mIsScrollMoved = false;
        this.mIsSttViewMoved = false;
        this.mIsNeedScrollToFocus = true;
        this.mRecognizerData.resetAdvancedPlayer();
        String peek = DialogFactory.peek();
        if (peek == null || !peek.equals(DialogFactory.EDIT_STT_DIALOG)) {
            resetTouchedArea();
        }
        this.mIsLastWord = false;
        this.mIsLastWordSaved = false;
        if (Engine.getInstance().getRecorderState() != 1 && !z) {
            this.mSttTextView.setTextIsSelectable(false);
            this.mRecognizerData.loadSttDataFromFile();
            this.mIsLastWord = this.mEventHandler.hasMessages(1000);
        } else if (Engine.getInstance().getPlayerState() != 1 || this.mScene == 6) {
            this.mEventHandler.removeMessages(1003);
            this.mRecognizerData.updatePlayedTime(Engine.getInstance().getCurrentTime());
            this.mSttTextView.setTextIsSelectable(true);
            this.mRecognizerData.loadSttDataFromFile();
            updateDisplayText();
            this.mRecognizerData.updateBookmark();
        }
        if (this.mScene == 6) {
            this.mRecognizerData.updateTrimTime();
            paintTrimArea();
            paintPlayingData();
        }
        if (Engine.getInstance().getRepeatMode() == 4) {
            int[] repeatPosition = Engine.getInstance().getRepeatPosition();
            this.mRecognizerData.updateRepeatTime(repeatPosition[0], repeatPosition[1]);
            paintPlayingData();
        }
    }

    private void updateDisplayText() {
        SpannableStringBuilder spannableStringBuilder = this.mDisplayString;
        if (spannableStringBuilder != null) {
            spannableStringBuilder.replace(0, spannableStringBuilder.length(), this.mRecognizerData.getDisplayText());
        }
    }

    private void updateDisplayTextList() {
        if (this.mDisplayString != null) {
            if (System.currentTimeMillis() - this.mCurrentTime >= 1000 || this.mScene == 6) {
                int i = this.mScene;
                if (i == 4 || i == 6) {
                    try {
                        Log.m19d(TAG, "updateDisplayTextList");
                        this.mCurrentTime = System.currentTimeMillis();
                        this.mDisplayString.replace(0, this.mDisplayString.length(), this.mRecognizerData.getDisplayTextList());
                    } catch (Exception e) {
                        Log.m22e(TAG, e.toString());
                    }
                }
            }
        }
    }

    private boolean isOverWriteMode() {
        return Engine.getInstance().getContentItemCount() > 1;
    }

    /* access modifiers changed from: private */
    public void saveSttData() {
        if (this.mIsLastWordSaved) {
            Log.m19d(TAG, "saveSttData already saved");
            return;
        }
        boolean z = Engine.getInstance().getEditorState() != 1;
        Log.m26i(TAG, "saveSttData() : " + Engine.getInstance().getContentItemCount() + ", isWritingOnEditor = " + z);
        if (z) {
            Log.m22e(TAG, "impossible saveSttData when editing");
            this.mEventHandler.sendEmptyMessage(1005);
            Engine.getInstance().updateLastWord();
            return;
        }
        this.mIsLastWordSaved = true;
        this.mRecognizerData.writeMetaData(getContext());
        if (!DesktopModeProvider.getInstance().isLoadingDesktopMode() && getActivity().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
            int i = this.mScene;
            if (i == 6) {
                postEvent(3);
            } else if (i == 8) {
                postEvent(1004);
            }
        }
        this.mEventHandler.sendEmptyMessage(1005);
        Engine.getInstance().updateLastWord();
    }

    private void paintPlayingData() {
        SpannableStringBuilder spannableStringBuilder = this.mDisplayString;
        if (spannableStringBuilder != null && this.mScene != 12 && !this.mIsTouched && !this.mIsSttViewMoved) {
            spannableStringBuilder.clearSpans();
            paintPlainText();
            paintRepeatArea();
            scrollPlayed(paintPlayed());
            paintTrimArea();
            paintBookmark();
            paintSearchResult();
            paintTouchedWord();
            this.mSttTextView.clearFocus();
            this.mSttTextView.setText(this.mDisplayString);
        }
    }

    private void paintTranslationData() {
        SpannableStringBuilder spannableStringBuilder = this.mDisplayString;
        if (spannableStringBuilder != null) {
            spannableStringBuilder.clearSpans();
            paintPlainText();
            this.mSttTextView.clearFocus();
            scrollRecord();
        }
    }

    private void paintRecordingData() {
        SpannableStringBuilder spannableStringBuilder = this.mDisplayString;
        if (spannableStringBuilder != null) {
            spannableStringBuilder.clearSpans();
            paintPlainText();
            paintBookmark();
            paintOverWriteWord();
            this.mSttTextView.clearFocus();
            scrollRecord();
        }
    }

    private void paintPlainText() {
        SpannableStringBuilder spannableStringBuilder = this.mDisplayString;
        if (spannableStringBuilder != null) {
            paintSetSpan(mForegroundBlackSpan, 0, checkEnd(spannableStringBuilder.length()), 33);
        }
    }

    private void paintRepeatArea() {
        RecognizerData.PaintIndexInfo repeatIndexInfo = this.mRecognizerData.getRepeatIndexInfo();
        if (repeatIndexInfo.getStartIndex() != -1 && repeatIndexInfo.getEndIndex() != -1) {
            paintSetSpan(mForegroundRepeatSpan, repeatIndexInfo.getStartIndex(), repeatIndexInfo.getEndIndex(), 33);
        }
    }

    private int paintPlayed() {
        RecognizerData.PaintIndexInfo playedIndexInfo = this.mRecognizerData.getPlayedIndexInfo();
        if (this.mDisplayString != null && playedIndexInfo.getStartIndex() < playedIndexInfo.getEndIndex()) {
            paintSetSpan(mForegroundPlayedSpan, playedIndexInfo.getStartIndex(), playedIndexInfo.getEndIndex(), 33);
        }
        return playedIndexInfo.getPaintedLength();
    }

    private void paintTrimArea() {
        RecognizerData.PaintIndexInfo trimIndexInfo = this.mRecognizerData.getTrimIndexInfo();
        int startIndex = trimIndexInfo.getStartIndex();
        int endIndex = trimIndexInfo.getEndIndex();
        if (this.mDisplayString != null && startIndex <= endIndex && startIndex != -1 && endIndex != -1) {
            paintSetSpan(CharacterStyle.wrap(mForegroundInactiveSpan), 0, checkEnd(startIndex), 33);
            paintSetSpan(CharacterStyle.wrap(mForegroundInactiveSpan), endIndex, checkEnd(this.mDisplayString.length()), 33);
        }
    }

    private void paintBookmark() {
        ArrayList<RecognizerData.PaintIndexInfo> bookmarkIndexInfo = this.mRecognizerData.getBookmarkIndexInfo();
        if (bookmarkIndexInfo != null) {
            Iterator<RecognizerData.PaintIndexInfo> it = bookmarkIndexInfo.iterator();
            while (it.hasNext()) {
                RecognizerData.PaintIndexInfo next = it.next();
                paintSetSpan(CharacterStyle.wrap(mForegroundBookmarkSpan), next.getStartIndex(), next.getEndIndex(), 33);
            }
        }
    }

    private void paintSearchResult() {
        ArrayList<RecognizerData.PaintIndexInfo> searchedIndexInfo;
        String str = this.mSearchResult;
        if (str != null && !str.isEmpty() && (searchedIndexInfo = this.mRecognizerData.getSearchedIndexInfo(this.mSearchResult)) != null) {
            Iterator<RecognizerData.PaintIndexInfo> it = searchedIndexInfo.iterator();
            while (it.hasNext()) {
                RecognizerData.PaintIndexInfo next = it.next();
                paintSetSpan(CharacterStyle.wrap(mForegroundSearchedSpan), next.getStartIndex(), next.getEndIndex(), 33);
                paintSetSpan(CharacterStyle.wrap(mBoldSpan), next.getStartIndex(), next.getEndIndex(), 33);
            }
        }
    }

    private void paintTouchedWord() {
        if (this.mRecognizerData.hasSavedSttData() && this.mRecognizerData.getTouchedIndex() != -1) {
            int touchedCharStart = this.mRecognizerData.getTouchedCharStart();
            paintSetSpan(mForegroundSelectedSpan, touchedCharStart, checkEnd(this.mRecognizerData.getTouchedWord().length() + touchedCharStart), 33);
        }
    }

    private void paintOverWriteWord() {
        if (this.mDisplayString != null && isOverWriteMode()) {
            int length = this.mDisplayString.length();
            int[] overwriteIndex = this.mRecognizerData.getOverwriteIndex();
            int i = overwriteIndex[1];
            if (length >= i) {
                length = i;
            }
            Log.m19d(TAG, "paint overwrite startIdx = " + overwriteIndex[0] + " endIdx = " + length);
            if (overwriteIndex[0] < length) {
                paintSetSpan(mForegroundSelectedSpan, overwriteIndex[0], length, 33);
            }
        }
    }

    private void paintSetSpan(Object obj, int i, int i2, int i3) {
        SpannableStringBuilder spannableStringBuilder = this.mDisplayString;
        if (spannableStringBuilder != null) {
            int length = spannableStringBuilder.length();
            if (i < 0 || i2 < 0 || i2 < i || i > length || i2 > length) {
                Log.m19d(TAG, "paintSetSpan fail : start = " + i + ", end = " + i2 + ", length = " + length);
                return;
            }
            this.mDisplayString.setSpan(obj, i, i2, i3);
        }
    }

    private void scrollPlayed(int i) {
        int length = this.mDisplayString.length();
        if (i > 0 && length > 0 && i <= length) {
            this.mSttTextView.setText(this.mDisplayString.toString().toCharArray(), 0, i);
            int lineCount = this.mSttTextView.getLineCount();
            if (lineCount <= 0) {
                return;
            }
            if (!this.mIsScrollMoved || this.mIsNeedScrollToFocus) {
                Rect rect = new Rect();
                int i2 = lineCount - 1;
                this.mSttTextView.getLineBounds(i2, rect);
                int height = this.mScrollView.getHeight();
                int lineHeight = this.mSttTextView.getLineHeight();
                if (((double) height) * 0.5d != ((double) (rect.bottom - this.mScrollView.getScrollY()))) {
                    if (lineHeight <= height) {
                        this.mScrollView.smoothScrollTo(0, (lineCount - Math.round(((float) height) / (((float) lineHeight) * 2.0f))) * lineHeight);
                    } else {
                        this.mScrollView.smoothScrollTo(0, i2 * lineHeight);
                    }
                }
                this.mIsNeedScrollToFocus = false;
            }
        }
    }

    private synchronized void scrollRecord() {
        int currentTextIdx = this.mRecognizerData.getCurrentTextIdx();
        if (currentTextIdx <= this.mSttTextView.length()) {
            this.mSttTextView.setText(this.mDisplayString.toString().toCharArray(), 0, currentTextIdx);
        }
        int lineCount = this.mSttTextView.getLineCount();
        if (lineCount > 0 && !this.mIsScrollMoved) {
            Rect rect = new Rect();
            boolean z = true;
            this.mSttTextView.getLineBounds(lineCount - 1, rect);
            double scrollY = (double) (rect.bottom - this.mScrollView.getScrollY());
            int height = this.mScrollView.getHeight();
            if (this.mSttTextView.getLineHeight() * 2 <= height) {
                z = false;
            }
            double d = (double) height;
            double d2 = (z ? 0.1d : 0.3d) * d;
            double d3 = d * (z ? 0.8d : 0.7d);
            if (d3 <= scrollY || d2 > scrollY) {
                this.mScrollView.smoothScrollTo(0, (int) (((double) rect.bottom) - d3));
            }
        }
        this.mSttTextView.setText(this.mDisplayString);
    }

    private int getTouchedViewIndex() {
        int totalPaddingStart = this.mRecognizerData.getTouchedXY()[0] - this.mSttTextView.getTotalPaddingStart();
        int totalPaddingTop = this.mRecognizerData.getTouchedXY()[1] - this.mSttTextView.getTotalPaddingTop();
        Layout layout = this.mSttTextView.getLayout();
        return layout.getOffsetForHorizontal(layout.getLineForVertical(totalPaddingTop), (float) totalPaddingStart);
    }

    private void showEditSttDialog() {
        Bundle bundle = new Bundle();
        bundle.putString(DialogFactory.BUNDLE_WORD, this.mRecognizerData.getTouchedWord());
        bundle.putInt(DialogFactory.BUNDLE_REQUEST_CODE, 3);
        DialogFactory.show(getFragmentManager(), DialogFactory.EDIT_STT_DIALOG, bundle, this);
    }

    public void onDialogResult(DialogFragment dialogFragment, Bundle bundle) {
        if (bundle != null) {
            int i = bundle.getInt(DialogFactory.BUNDLE_REQUEST_CODE);
            int i2 = bundle.getInt("result_code");
            if (getActivity() == null) {
                if (i == 3 && i2 == 1 && this.mIsNeedPlayResume) {
                    if (Engine.getInstance().getPlayerState() == 4) {
                        int resumePlay = Engine.getInstance().resumePlay();
                        if (resumePlay == -103) {
                            Toast.makeText(getActivity(), C0690R.string.no_play_during_call, 0).show();
                        } else if (resumePlay == 0) {
                            postEvent(VoiceNoteApplication.convertEvent(Event.PLAY_RESUME));
                        }
                    }
                    this.mIsNeedPlayResume = false;
                }
            } else if (i == 3) {
                if (i2 == 0) {
                    Log.m29v(TAG, "onDialogResult() : EditSttResult.EDITED");
                    String string = bundle.getString(DialogFactory.BUNDLE_WORD);
                    if (string != null) {
                        this.mRecognizerData.editText(string);
                        this.mSttDataChanged = true;
                    }
                } else if (i2 == 1) {
                    Log.m29v(TAG, "onDialogResult() : EditSttResult.DISMISS");
                    moveSttView(MOVE_DOWN);
                    resetTouchedArea();
                    if (this.mIsNeedPlayResume) {
                        if (Engine.getInstance().getPlayerState() == 4) {
                            int resumePlay2 = Engine.getInstance().resumePlay();
                            if (resumePlay2 == -103) {
                                Toast.makeText(getActivity(), C0690R.string.no_play_during_call, 0).show();
                            } else if (resumePlay2 == 0) {
                                if (VoiceNoteApplication.getScene() == 12) {
                                    postEvent(Event.TRANSLATION_RESUME);
                                    Engine.getInstance().resumeTranslation();
                                } else {
                                    postEvent(VoiceNoteApplication.convertEvent(Event.PLAY_RESUME));
                                }
                            }
                        }
                        this.mIsNeedPlayResume = false;
                    }
                    this.mRecognizerData.getDisplayText();
                    updateDisplayTextList();
                    paintPlayingData();
                }
            }
        }
    }

    private void moveSttView(String str) {
        View findViewById;
        Log.m26i(TAG, "moveSttView() : " + str + ", mTouchedWordIndex = " + this.mRecognizerData.getTouchedIndex());
        if (getActivity() != null && (findViewById = getActivity().getWindow().getDecorView().findViewById(C0690R.C0693id.main_stt)) != null) {
            int dimensionPixelSize = getResources().getDimensionPixelSize(C0690R.dimen.edit_stt_dialog_height);
            if (str.equals(MOVE_UP)) {
                TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, (float) (-dimensionPixelSize));
                translateAnimation.setDuration(300);
                translateAnimation.setFillAfter(true);
                findViewById.startAnimation(translateAnimation);
                this.mIsSttViewMoved = true;
            } else if (str.equals(MOVE_DOWN) && this.mRecognizerData.getTouchedIndex() != -1) {
                TranslateAnimation translateAnimation2 = new TranslateAnimation(0.0f, 0.0f, (float) (-dimensionPixelSize), 0.0f);
                translateAnimation2.setDuration(300);
                translateAnimation2.setFillAfter(true);
                findViewById.startAnimation(translateAnimation2);
                this.mIsSttViewMoved = false;
            }
        }
    }

    private void sendMessageDotAnimation() {
        if (this.mDisplayString != null) {
            this.mEventHandler.sendEmptyMessage(1003);
            int dotIdx = this.mRecognizerData.getDotIdx();
            if (dotIdx == 0) {
                this.mDisplayString.insert(0, " ", 0, 1);
            }
            Log.m19d(TAG, "sendMessageDotAnimation() : dotIdx = " + dotIdx);
        }
    }

    /* access modifiers changed from: private */
    public void drawDotAnimation() {
        Drawable drawable;
        TextView textView;
        if (getActivity() != null && (drawable = getActivity().getResources().getDrawable(mDotFrames[this.mCurrentFrameNumber], (Resources.Theme) null)) != null && (textView = this.mSttTextView) != null && this.mDisplayString != null) {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            int lineHeight = (textView.getLineHeight() - ((int) this.mSttTextView.getLineSpacingExtra())) - intrinsicHeight;
            drawable.setBounds(0, lineHeight, intrinsicWidth, intrinsicHeight + lineHeight);
            CenteredImageSpan centeredImageSpan = new CenteredImageSpan(drawable);
            int dotIdx = this.mRecognizerData.getDotIdx();
            int length = this.mDisplayString.length();
            if (dotIdx >= 0 && dotIdx <= length) {
                this.mDisplayString.clearSpans();
                paintSetSpan(centeredImageSpan, dotIdx, checkEnd(dotIdx + 1), 33);
                this.mSttTextView.setText(this.mDisplayString);
            }
        }
    }

    private int checkEnd(int i) {
        int length = this.mDisplayString.length();
        return i > length ? length : i;
    }

    private void resetTouchedArea() {
        this.mRecognizerData.resetTouchedArea();
    }

    /* access modifiers changed from: private */
    public boolean isValidFragment() {
        return getActivity() != null && isAdded() && !isRemoving() && !isDetached();
    }

    private void moveTimeHandlerToTextPos(int i) {
        if (Engine.getInstance().getRecorderState() == 3) {
            CursorProvider.getInstance().resetCurrentPlayingItemPosition();
            int resumePlay = Engine.getInstance().resumePlay(false);
            if (resumePlay < 0) {
                errorHandler(resumePlay);
            }
            Message message = new Message();
            message.arg1 = i;
            message.arg2 = 10;
            message.what = 0;
            this.mDelayHandler.removeMessages(0);
            this.mDelayHandler.sendMessageDelayed(message, 0);
        } else if (Engine.getInstance().getRecorderState() != 2) {
            if (Engine.getInstance().getPlayerState() == 1) {
                CursorProvider.getInstance().resetCurrentPlayingItemPosition();
                int resumePlay2 = Engine.getInstance().resumePlay(false);
                if (resumePlay2 < 0) {
                    errorHandler(resumePlay2);
                }
                Engine.getInstance().pausePlay();
            }
            Engine.getInstance().seekTo(i);
        }
    }

    private void errorHandler(int i) {
        if (i < -100) {
            Log.m26i(TAG, "errorHandler - errorCode : " + i);
            if (i == -116) {
                Toast.makeText(getActivity(), C0690R.string.trim_failed, 0).show();
                Log.m22e(TAG, "trim - TRIM_FAIL !!!!");
            } else if (i == -103) {
                Toast.makeText(getActivity(), C0690R.string.no_play_during_call, 0).show();
            }
        }
    }

    private void updateTabletMultiWindowLayoutView() {
        FragmentActivity activity;
        if (VoiceNoteFeature.FLAG_IS_TABLET && (activity = getActivity()) != null && DisplayManager.isInMultiWindowMode(activity) && this.mScrollViewRelative != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mScrollView.getLayoutParams();
            layoutParams.leftMargin = getResources().getDimensionPixelSize(C0690R.dimen.multi_window_tablet_stt_margin_left_right) / 2;
            layoutParams.rightMargin = getResources().getDimensionPixelSize(C0690R.dimen.multi_window_tablet_stt_margin_left_right) / 2;
            this.mScrollViewRelative.setLayoutParams(layoutParams);
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.SttFragment$DelayHandler */
    private static class DelayHandler extends Handler {
        private DelayHandler() {
        }

        public void handleMessage(Message message) {
            if (message.arg2 > 0) {
                Log.m29v(SttFragment.TAG, "handleMessage - state : " + Engine.getInstance().getPlayerState());
                if (Engine.getInstance().getPlayerState() == 1) {
                    Message message2 = new Message();
                    message2.what = message.what;
                    message2.arg1 = message.arg1;
                    message2.arg2 = message.arg2 - 1;
                    sendMessageDelayed(message2, 100);
                } else {
                    Engine.getInstance().pausePlay();
                    Engine.getInstance().seekTo(message.arg1);
                }
                super.handleMessage(message);
            }
        }
    }

    public void onDecoderStop() {
        Log.m19d(TAG, "DecoderListener onStop");
    }

    public void onPartialDecodeComplete(String str) {
        Log.m19d(TAG, "DecoderListener onPartialDecodeComplete : " + str);
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

    private void paintSttData() {
        int i = this.mScene;
        if (i == 4) {
            if (Engine.getInstance().getPlayerState() == 3) {
                getActivity().getWindow().addFlags(128);
            }
            paintPlayingData();
        } else if (i == 8) {
            if (Engine.getInstance().getRecorderState() == 2) {
                getActivity().getWindow().addFlags(128);
            }
            paintRecordingData();
        } else if (i == 12) {
            if (Engine.getInstance().getTranslationState() == 2) {
                getActivity().getWindow().addFlags(128);
            }
            if (Engine.getInstance().getPlayerState() == 3 || Engine.getInstance().getPlayerState() == 4) {
                Engine.getInstance().setVolume(1.0f, 1.0f);
            }
            paintTranslationData();
        }
    }
}
