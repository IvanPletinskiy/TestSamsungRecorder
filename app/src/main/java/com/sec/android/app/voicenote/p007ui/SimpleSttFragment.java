package com.sec.android.app.voicenote.p007ui;

import android.app.ProgressDialog;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.activity.SelectLanguageActivity;
import com.sec.android.app.voicenote.activity.SimpleActivity;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.p007ui.recognizer.RecognizerData;
import com.sec.android.app.voicenote.p007ui.view.CenteredImageSpan;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.HWKeyboardProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.ViewProvider;
import com.sec.android.app.voicenote.service.SimpleEngine;
import com.sec.android.app.voicenote.service.VoiceNoteService;
import com.sec.android.app.voicenote.service.recognizer.VoiceWorker;
import com.sec.android.app.voicenote.service.remote.RemoteViewManager;
import com.sec.android.app.voicenote.uicore.Observable;
import com.sec.android.app.voicenote.uicore.SimpleFragmentController;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

/* renamed from: com.sec.android.app.voicenote.ui.SimpleSttFragment */
public class SimpleSttFragment extends AbsSimpleFragment implements VoiceWorker.StatusChangedListener, DialogFactory.DialogResultListener, SimpleFragmentController.OnSceneChangeListener, SimpleEngine.OnSimpleEngineListener {
    private static final int FLAG_EXCLUSIVE = 33;
    private static final int MAX_DELAY_TIME = 4500;
    private static final int MAX_DOT_ANIMATION_TIME = 100;
    private static final int MAX_KEEP_SCROLL_TIME = 2000;
    private static final String MOVE_DOWN = "down";
    private static final String MOVE_UP = "up";
    private static final int MSG_DELAY_SAVE = 1000;
    private static final int MSG_DOT_ANIMATION = 1003;
    private static final int MSG_HIDE_PROGRESS = 1005;
    private static final int MSG_SCROLL_MOVE = 1002;
    private static final int MSG_SHOW_ERROR = 1001;
    private static final int MSG_SHOW_PROGRESS = 1004;
    private static final String TAG = "SimpleSttFragment";
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
    private SpannableStringBuilder mDisplayString;
    private Handler mEngineEventHandler;
    /* access modifiers changed from: private */
    public final Handler mEventHandler = new Handler(new Handler.Callback() {
        public boolean handleMessage(Message message) {
            if (!SimpleSttFragment.this.isValidFragment()) {
                return false;
            }
            switch (message.what) {
                case 1000:
                    Log.m26i(SimpleSttFragment.TAG, "handleMessage() : MSG_DELAY_SAVE");
                    SimpleSttFragment.this.saveSttData();
                    break;
                case 1001:
                    if (SimpleSttFragment.this.isResumed()) {
                        Toast.makeText(SimpleSttFragment.this.getActivity(), C0690R.string.network_error, 0).show();
                        break;
                    } else {
                        return false;
                    }
                case 1002:
                    boolean unused = SimpleSttFragment.this.mIsScrollMoved = false;
                    break;
                case 1003:
                    SimpleSttFragment.access$308(SimpleSttFragment.this);
                    if (SimpleSttFragment.this.mCurrentFrameNumber >= SimpleSttFragment.mDotFrames.length) {
                        int unused2 = SimpleSttFragment.this.mCurrentFrameNumber = 0;
                    }
                    SimpleSttFragment.this.drawDotAnimation();
                    SimpleSttFragment.this.mEventHandler.sendEmptyMessageDelayed(1003, 100);
                    break;
                case 1004:
                    SimpleSttFragment simpleSttFragment = SimpleSttFragment.this;
                    ProgressDialog unused3 = simpleSttFragment.mProgressDialog = ProgressDialog.show(simpleSttFragment.getActivity(), "", SimpleSttFragment.this.getString(C0690R.string.please_wait));
                    break;
                case 1005:
                    if (SimpleSttFragment.this.mProgressDialog != null && SimpleSttFragment.this.mProgressDialog.isShowing() && !SimpleSttFragment.this.isDetached()) {
                        SimpleSttFragment.this.mProgressDialog.dismiss();
                        ProgressDialog unused4 = SimpleSttFragment.this.mProgressDialog = null;
                        break;
                    }
            }
            return false;
        }
    });
    private boolean mIsLastWord = false;
    private boolean mIsLastWordSaved = false;
    private boolean mIsLongPressed;
    private boolean mIsNeedPlayResume;
    /* access modifiers changed from: private */
    public boolean mIsScrollMoved;
    private boolean mIsSttViewMoved;
    private boolean mIsTouched;
    private int mNumberOfRecognition;
    /* access modifiers changed from: private */
    public ProgressDialog mProgressDialog;
    private RecognizerData mRecognizerData;
    private int mScene = 0;
    private ScrollView mScrollView;
    private String mSearchResult;
    private TextView mSelectLanguageButton;
    private String[] mSttDefaultLocales;
    private String[] mSttLocales;
    private TextView mSttTextView;
    private String[] mSttTexts;

    static /* synthetic */ int access$308(SimpleSttFragment simpleSttFragment) {
        int i = simpleSttFragment.mCurrentFrameNumber;
        simpleSttFragment.mCurrentFrameNumber = i + 1;
        return i;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Resources resources = getResources();
        mForegroundBlackSpan = new ForegroundColorSpan(resources.getColor(C0690R.C0691color.stt_black_span, (Resources.Theme) null));
        mForegroundSearchedSpan = new ForegroundColorSpan(resources.getColor(C0690R.C0691color.stt_searched_span, (Resources.Theme) null));
        mForegroundRepeatSpan = new ForegroundColorSpan(resources.getColor(C0690R.C0691color.stt_repeat_span, (Resources.Theme) null));
        mForegroundInactiveSpan = new ForegroundColorSpan(resources.getColor(C0690R.C0691color.stt_inactive_span, (Resources.Theme) null));
        mForegroundPlayedSpan = new ForegroundColorSpan(resources.getColor(C0690R.C0691color.stt_played_span, (Resources.Theme) null));
        mForegroundBookmarkSpan = new ForegroundColorSpan(resources.getColor(C0690R.C0691color.stt_bookmark_span, (Resources.Theme) null));
        mForegroundSelectedSpan = new ForegroundColorSpan(resources.getColor(C0690R.C0691color.stt_selected_span, (Resources.Theme) null));
        this.mEngineEventHandler = new Handler(new Handler.Callback() {
            public final boolean handleMessage(Message message) {
                return SimpleSttFragment.this.lambda$onCreate$0$SimpleSttFragment(message);
            }
        });
    }

    public /* synthetic */ boolean lambda$onCreate$0$SimpleSttFragment(Message message) {
        if (!isValidFragment()) {
            return false;
        }
        int i = message.what;
        if (i == 1010) {
            if (message.arg1 == 3) {
                Log.m26i(TAG, "mEngineEventHandler : RecorderState.PAUSED " + this.mSimpleEngine.getContentItemCount());
                this.mNumberOfRecognition = 0;
                this.mEventHandler.removeMessages(1003);
                this.mRecognizerData.addLastWordPartialText();
                this.mRecognizerData.loadSttDataFromFile();
                this.mRecognizerData.updatePlayedTime(this.mSimpleEngine.getCurrentTime());
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
                        if ((this.mSimpleEngine.getRecorderState() == 1 && this.mSimpleMetadata.getRecordMode() == 4) || (!this.mIsLastWord && this.mSimpleEngine.getRecorderState() != 2)) {
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
                        if (this.mSimpleEngine.getRepeatMode() == 4) {
                            int[] repeatPosition = this.mSimpleEngine.getRepeatPosition();
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
            int repeatMode = this.mSimpleEngine.getRepeatMode();
            int[] repeatPosition2 = this.mSimpleEngine.getRepeatPosition();
            this.mRecognizerData.updateRepeatTime(repeatPosition2[0], repeatPosition2[1]);
            if (repeatMode == 2 || (repeatMode != 3 && repeatMode == 4)) {
                paintPlayingData();
            }
        }
        return false;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        boolean z;
        Log.m26i(TAG, "onCreateView() : savedInstanceState = " + bundle);
        this.mRecognizerData = new RecognizerData();
        this.mRecognizerData.setSession(this.mSession);
        if (getActivity() instanceof SimpleActivity) {
            Log.m19d(TAG, "onCreateView() : Called from SimpleActivity");
            z = false;
        } else {
            z = true;
        }
        View inflate = layoutInflater.inflate(C0690R.layout.fragment_stt, viewGroup, false);
        this.mSttTextView = (TextView) inflate.findViewById(C0690R.C0693id.stt_textview);
        if (Build.VERSION.SDK_INT >= 28) {
            this.mSttTextView.setFallbackLineSpacing(false);
        }
        this.mSttTextView.setTextIsSelectable(z);
//        this.mSttTextView.semSetMultiSelectionEnabled(false);
        this.mSttTextView.setRawInputType(0);
        this.mSttTextView.setImportantForAccessibility(2);
        if (z) {
            this.mSttTextView.setOnTouchListener(new View.OnTouchListener() {
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    return SimpleSttFragment.this.lambda$onCreateView$1$SimpleSttFragment(view, motionEvent);
                }
            });
            this.mSttTextView.setOnLongClickListener(new View.OnLongClickListener() {
                public final boolean onLongClick(View view) {
                    return SimpleSttFragment.this.lambda$onCreateView$2$SimpleSttFragment(view);
                }
            });
        }
        this.mDisplayString = new SpannableStringBuilder();
        VoiceWorker.getInstance().registerListener(this);
        this.mScrollView = (ScrollView) inflate.findViewById(C0690R.C0693id.stt_scrollview);
        this.mScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            public final void onScrollChange(View view, int i, int i2, int i3, int i4) {
                SimpleSttFragment.this.lambda$onCreateView$3$SimpleSttFragment(view, i, i2, i3, i4);
            }
        });
        this.mScrollView.setOnTouchListener(new View.OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return SimpleSttFragment.this.lambda$onCreateView$4$SimpleSttFragment(view, motionEvent);
            }
        });
        this.mScrollView.smoothScrollTo(0, 0);
        this.mSelectLanguageButton = (TextView) inflate.findViewById(C0690R.C0693id.stt_language_button);
        getVoiceMemoStringArrays();
        String stringSettings = Settings.getStringSettings(Settings.KEY_STT_LANGUAGE_TEXT);
        if (stringSettings == null) {
            stringSettings = getDefaultLanguage();
        }
        TextView textView = this.mSelectLanguageButton;
        if (textView != null) {
            if (textView.getVisibility() != 0) {
                this.mSelectLanguageButton.setVisibility(0);
            }
            if (getResources().getConfiguration().getLayoutDirection() == 1) {
                this.mSelectLanguageButton.setBackgroundResource(C0690R.C0692drawable.voice_ripple_stt_language_btn_rtl);
            } else {
                this.mSelectLanguageButton.setBackgroundResource(C0690R.C0692drawable.voice_ripple_stt_language_btn);
            }
            this.mSelectLanguageButton.setText(stringSettings);
            this.mSelectLanguageButton.setContentDescription(stringSettings);
            this.mSelectLanguageButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    SimpleSttFragment.this.lambda$onCreateView$5$SimpleSttFragment(view);
                }
            });
        }
        ViewProvider.setMaxFontSize(getContext(), this.mSelectLanguageButton);
        initialize(false);
        onUpdate(Integer.valueOf(this.mStartingEvent));
        return inflate;
    }

    public /* synthetic */ boolean lambda$onCreateView$1$SimpleSttFragment(View view, MotionEvent motionEvent) {
        if (getActivity() == null) {
            return true;
        }
        int action = motionEvent.getAction();
        if (action != 0) {
            if (action == 1) {
                this.mIsTouched = false;
                if (this.mIsLongPressed) {
                    this.mIsLongPressed = false;
                    return false;
                } else if (HWKeyboardProvider.isHWKeyboard(getActivity())) {
                    return false;
                } else {
                    int touchedViewIndex = getTouchedViewIndex();
                    if (this.mScene == 3) {
                        long touchedWordTimeStamp = this.mRecognizerData.getTouchedWordTimeStamp(touchedViewIndex);
                        if (touchedWordTimeStamp > 0) {
                            this.mSimpleEngine.seekTo((int) touchedWordTimeStamp);
                        }
                    } else {
                        this.mRecognizerData.updateTouchedWordIndex(touchedViewIndex);
                        if (this.mRecognizerData.getTouchedIndex() == -1) {
                            Log.m26i(TAG, "cannot find word at character index : " + touchedViewIndex);
                            return true;
                        }
                        if (this.mSimpleEngine.getPlayerState() == 3) {
                            this.mSimpleEngine.pausePlay();
                            this.mIsNeedPlayResume = true;
                        } else {
                            this.mIsNeedPlayResume = false;
                        }
                        this.mRecognizerData.updateTouchedXY(-1, -1);
                        paintPlayingData();
                        moveSttView(MOVE_UP);
                        showEditSttDialog();
                    }
                }
            } else if (action == 3) {
                this.mRecognizerData.updateTouchedXY(-1, -1);
                this.mIsTouched = false;
            }
        } else if (!(this.mScene == 1 || this.mSimpleEngine.getRecorderState() == 3 || this.mSimpleEngine.getRecorderState() == 2)) {
            this.mRecognizerData.updateTouchedXY((int) motionEvent.getX(), (int) motionEvent.getY());
            this.mIsTouched = true;
        }
        return false;
    }

    public /* synthetic */ boolean lambda$onCreateView$2$SimpleSttFragment(View view) {
        this.mRecognizerData.updateTouchedWordIndex(getTouchedViewIndex());
        if (this.mRecognizerData.getTouchedIndex() == -1) {
            return false;
        }
        this.mIsLongPressed = true;
        if (this.mSimpleEngine.getPlayerState() == 3) {
            this.mSimpleEngine.pausePlay();
            if (this.mScene == 3) {
                postEvent(Event.PLAY_PAUSE);
            }
        }
        resetTouchedArea();
        return false;
    }

    public /* synthetic */ void lambda$onCreateView$3$SimpleSttFragment(View view, int i, int i2, int i3, int i4) {
        this.mIsScrollMoved = true;
        this.mEventHandler.removeMessages(1002);
        this.mEventHandler.sendEmptyMessageDelayed(1002, 2000);
    }

    public /* synthetic */ boolean lambda$onCreateView$4$SimpleSttFragment(View view, MotionEvent motionEvent) {
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

    public /* synthetic */ void lambda$onCreateView$5$SimpleSttFragment(View view) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            Observable.getInstance().notifyObservers(VoiceNoteApplication.getSimpleActivitySession(), Integer.valueOf(Event.CHOOSE_STT_LANGUAGE));
            activity.startActivity(new Intent(activity, SelectLanguageActivity.class));
            SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_ready_convert_stt), getActivity().getResources().getString(C0690R.string.event_convert_language));
        }
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

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mSimpleEngine.registerListener(this);
    }

    public void onResume() {
        Log.m26i(TAG, "onResume()");
        super.onResume();
        TextView textView = this.mSelectLanguageButton;
        if (textView != null) {
            textView.setText(Settings.getStringSettings(Settings.KEY_STT_LANGUAGE_TEXT));
            this.mSelectLanguageButton.setContentDescription(Settings.getStringSettings(Settings.KEY_STT_LANGUAGE_TEXT));
        }
        FrameLayout frameLayout = (FrameLayout) getActivity().findViewById(C0690R.C0693id.main_stt_view);
        if (frameLayout != null) {
            int i = this.mScene;
            if (i == 3) {
                frameLayout.setPadding(0, getResources().getDimensionPixelSize(C0690R.dimen.main_stt_play_padding_top), 0, 0);
            } else if (i == 1) {
                frameLayout.setPadding(0, getResources().getDimensionPixelSize(C0690R.dimen.main_stt_record_padding_top), 0, 0);
            }
        }
        if (this.mSimpleEngine.getRecorderState() != 1) {
            updateDisplayText();
        }
        if (this.mSimpleEngine.getRecorderState() == 2) {
            getActivity().getWindow().addFlags(128);
            paintRecordingData();
            return;
        }
        if (this.mSimpleEngine.getPlayerState() == 3) {
            getActivity().getWindow().addFlags(128);
        }
        paintPlayingData();
    }

    public void onPause() {
        Log.m26i(TAG, "onPause()");
        super.onPause();
        getActivity().getWindow().clearFlags(128);
    }

    public void onStop() {
        Log.m26i(TAG, "onStop()");
        super.onStop();
        if (this.mSimpleEngine.getRecorderState() != 1 || this.mSimpleEngine.getPlayerState() != 1) {
            return;
        }
        if (!this.mIsLastWord || this.mIsLastWordSaved) {
            onClearScreen();
        }
    }

    public void onDestroyView() {
        Log.m26i(TAG, "onDestroyView()");
        this.mSimpleEngine.unregisterListener(this);
        VoiceWorker.getInstance().unregisterListener(this);
        this.mEventHandler.removeMessages(1000);
        this.mEventHandler.removeMessages(1003);
        this.mEventHandler.removeMessages(1004);
        this.mEventHandler.removeMessages(1005);
        if (this.mIsLastWord && !this.mIsLastWordSaved) {
            this.mIsLastWordSaved = true;
            this.mRecognizerData.writeMetaData(getContext());
        }
        SpannableStringBuilder spannableStringBuilder = this.mDisplayString;
        if (spannableStringBuilder != null) {
            spannableStringBuilder.clear();
            this.mDisplayString = null;
        }
        ProgressDialog progressDialog = this.mProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            this.mProgressDialog.dismiss();
            this.mProgressDialog = null;
        }
        this.mSearchResult = null;
        CursorProvider.getInstance().setSearchResult("");
        moveSttView(MOVE_DOWN);
        this.mRecognizerData.clearVariables(true);
        if (getActivity() != null && !getActivity().isDestroyed()) {
            DialogFactory.clearDialogByTag(getFragmentManager(), DialogFactory.EDIT_STT_DIALOG);
        }
        this.mSttDefaultLocales = null;
        this.mSttLocales = null;
        this.mSttTexts = null;
        super.onDestroyView();
    }

    public void onDestroy() {
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
        TextView textView;
        StringBuilder sb = new StringBuilder();
        sb.append("onUpdate : ");
        sb.append(obj);
        sb.append("> ");
        Integer num = (Integer) obj;
        sb.append(Event.getEventName(num.intValue()));
        Log.m26i(TAG, sb.toString());
        if (isValidFragment()) {
            int intValue = num.intValue();
            if (intValue == 1) {
                TextView textView2 = this.mSelectLanguageButton;
                if (textView2 != null && textView2.getVisibility() != 0) {
                    this.mSelectLanguageButton.setVisibility(0);
                }
            } else if (intValue == 5) {
                this.mRecognizerData.resetOverwriteArea();
                this.mSearchResult = null;
                CursorProvider.getInstance().setSearchResult("");
            } else if (intValue == 975) {
                TextView textView3 = this.mSelectLanguageButton;
                if (!(textView3 == null || textView3.getVisibility() == 8)) {
                    this.mSelectLanguageButton.setVisibility(8);
                }
                initialize(true);
            } else if (intValue == 978 || intValue == 996) {
                this.mRecognizerData.updateBookmark();
                if (this.mSimpleEngine.getRecorderState() == 2) {
                    paintRecordingData();
                } else {
                    paintPlayingData();
                }
            } else if (intValue == 1005) {
                Log.m26i(TAG, "onUpdate : Event.RECORD_STOP_DELAYED : " + this.mIsLastWord);
                if (!this.mIsLastWord && !this.mEventHandler.hasMessages(1000)) {
                    postEvent(1004);
                }
            } else if (intValue == 2003) {
                TextView textView4 = this.mSelectLanguageButton;
                if (!(textView4 == null || textView4.getVisibility() == 8)) {
                    this.mSelectLanguageButton.setVisibility(8);
                }
                if (VoiceNoteService.Helper.connectionCount() == 0) {
                    DialogFactory.clearDialogByTag(getFragmentManager(), DialogFactory.EDIT_STT_DIALOG);
                    initialize(true);
                    paintPlayingData();
                }
            } else if (intValue == 1002) {
            } else {
                if (intValue == 1003) {
                    this.mRecognizerData.updateOverwriteTime();
                    this.mSttTextView.setTextIsSelectable(false);
                    this.mIsLastWordSaved = false;
                    this.mNumberOfRecognition = 0;
                    paintRecordingData();
                } else if (intValue == 2005 || intValue == 2006) {
                    DialogFactory.clearDialogByTag(getFragmentManager(), DialogFactory.EDIT_STT_DIALOG);
                    initialize(true);
                    paintPlayingData();
                } else if (intValue == 5004) {
                    this.mRecognizerData.updateOverwriteTime();
                    this.mSttTextView.setTextIsSelectable(false);
                    this.mIsLastWordSaved = false;
                    this.mNumberOfRecognition = 0;
                    this.mRecognizerData.loadSttDataFromFile();
                    paintRecordingData();
                } else if (intValue != 5005) {
                    switch (intValue) {
                        case Event.SIMPLE_RECORD_OPEN:
                            if (this.mSimpleEngine.getRecorderState() == 2 && (textView = this.mSelectLanguageButton) != null && textView.getVisibility() != 8) {
                                this.mSelectLanguageButton.setVisibility(8);
                                return;
                            }
                            return;
                        case Event.SIMPLE_RECORD_START:
                            TextView textView5 = this.mSelectLanguageButton;
                            if (!(textView5 == null || textView5.getVisibility() == 8)) {
                                this.mSelectLanguageButton.setVisibility(8);
                            }
                            this.mSttTextView.setTextIsSelectable(false);
                            this.mIsLastWordSaved = false;
                            this.mNumberOfRecognition = 0;
                            return;
                        case Event.SIMPLE_RECORD_STOP:
                        case Event.SIMPLE_RECORD_PLAY_START:
                        case Event.SIMPLE_RECORD_PLAY_PAUSE:
                            TextView textView6 = this.mSelectLanguageButton;
                            if (textView6 != null && textView6.getVisibility() != 8) {
                                this.mSelectLanguageButton.setVisibility(8);
                                return;
                            }
                            return;
                        case Event.SIMPLE_PLAY_OPEN:
                        case Event.SIMPLE_PLAY_START:
                            TextView textView7 = this.mSelectLanguageButton;
                            if (!(textView7 == null || textView7.getVisibility() == 8)) {
                                this.mSelectLanguageButton.setVisibility(8);
                            }
                            this.mSttTextView.setTextIsSelectable(false);
                            this.mIsLastWordSaved = false;
                            this.mNumberOfRecognition = 0;
                            return;
                        default:
                            return;
                    }
                } else {
                    this.mRecognizerData.updateTrimTime();
                    this.mSttTextView.setTextIsSelectable(true);
                    paintTrimArea();
                    paintPlayingData();
                }
            }
        }
    }

    public void onSceneChange(int i) {
        Log.m26i(TAG, "onSceneChange() : mScene = " + this.mScene + " scene = " + i);
        this.mScene = i;
        this.mRecognizerData.updateScene(i);
        if (this.mScene == 3 && this.mDisplayString != null && this.mSttTextView != null) {
            this.mSearchResult = CursorProvider.getInstance().getSearchResult();
            paintPlayingData();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0091, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0093, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void onResultWord(java.util.ArrayList<com.sec.android.app.voicenote.common.util.TextData> r4) {
        /*
            r3 = this;
            monitor-enter(r3)
            java.lang.String r0 = "SimpleSttFragment"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0094 }
            r1.<init>()     // Catch:{ all -> 0x0094 }
            java.lang.String r2 = "onResultWord() : mNumberOfRecognition = "
            r1.append(r2)     // Catch:{ all -> 0x0094 }
            int r2 = r3.mNumberOfRecognition     // Catch:{ all -> 0x0094 }
            int r2 = r2 + -1
            r3.mNumberOfRecognition = r2     // Catch:{ all -> 0x0094 }
            r1.append(r2)     // Catch:{ all -> 0x0094 }
            java.lang.String r2 = ", Record state = "
            r1.append(r2)     // Catch:{ all -> 0x0094 }
            com.sec.android.app.voicenote.service.SimpleEngine r2 = r3.mSimpleEngine     // Catch:{ all -> 0x0094 }
            int r2 = r2.getRecorderState()     // Catch:{ all -> 0x0094 }
            r1.append(r2)     // Catch:{ all -> 0x0094 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0094 }
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r1)     // Catch:{ all -> 0x0094 }
            java.lang.String r0 = "SimpleSttFragment"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0094 }
            r1.<init>()     // Catch:{ all -> 0x0094 }
            java.lang.String r2 = "onResultWord() : mIsLastWord = "
            r1.append(r2)     // Catch:{ all -> 0x0094 }
            boolean r2 = r3.mIsLastWord     // Catch:{ all -> 0x0094 }
            r1.append(r2)     // Catch:{ all -> 0x0094 }
            java.lang.String r2 = ", mIsLastWordSaved = "
            r1.append(r2)     // Catch:{ all -> 0x0094 }
            boolean r2 = r3.mIsLastWordSaved     // Catch:{ all -> 0x0094 }
            r1.append(r2)     // Catch:{ all -> 0x0094 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0094 }
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r1)     // Catch:{ all -> 0x0094 }
            android.os.Handler r0 = r3.mEventHandler     // Catch:{ all -> 0x0094 }
            r1 = 1003(0x3eb, float:1.406E-42)
            r0.removeMessages(r1)     // Catch:{ all -> 0x0094 }
            boolean r0 = r3.mIsLastWordSaved     // Catch:{ all -> 0x0094 }
            if (r0 != 0) goto L_0x0092
            com.sec.android.app.voicenote.service.SimpleEngine r0 = r3.mSimpleEngine     // Catch:{ all -> 0x0094 }
            int r0 = r0.getRecorderState()     // Catch:{ all -> 0x0094 }
            r1 = 3
            if (r0 == r1) goto L_0x0092
            com.sec.android.app.voicenote.service.SimpleEngine r0 = r3.mSimpleEngine     // Catch:{ all -> 0x0094 }
            int r0 = r0.getRecorderState()     // Catch:{ all -> 0x0094 }
            r1 = 4
            if (r0 != r1) goto L_0x006b
            goto L_0x0092
        L_0x006b:
            boolean r0 = r3.mIsLastWord     // Catch:{ all -> 0x0094 }
            if (r0 == 0) goto L_0x0075
            com.sec.android.app.voicenote.ui.recognizer.RecognizerData r0 = r3.mRecognizerData     // Catch:{ all -> 0x0094 }
            r0.addLastWordText(r4)     // Catch:{ all -> 0x0094 }
            goto L_0x007a
        L_0x0075:
            com.sec.android.app.voicenote.ui.recognizer.RecognizerData r4 = r3.mRecognizerData     // Catch:{ all -> 0x0094 }
            r4.addResultText()     // Catch:{ all -> 0x0094 }
        L_0x007a:
            r3.updateDisplayText()     // Catch:{ all -> 0x0094 }
            r3.paintRecordingData()     // Catch:{ all -> 0x0094 }
            boolean r4 = r3.mIsLastWord     // Catch:{ all -> 0x0094 }
            if (r4 == 0) goto L_0x0090
            android.os.Handler r4 = r3.mEventHandler     // Catch:{ all -> 0x0094 }
            r0 = 1000(0x3e8, float:1.401E-42)
            r4.removeMessages(r0)     // Catch:{ all -> 0x0094 }
            android.os.Handler r4 = r3.mEventHandler     // Catch:{ all -> 0x0094 }
            r4.sendEmptyMessage(r0)     // Catch:{ all -> 0x0094 }
        L_0x0090:
            monitor-exit(r3)
            return
        L_0x0092:
            monitor-exit(r3)
            return
        L_0x0094:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.SimpleSttFragment.onResultWord(java.util.ArrayList):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0080, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0082, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void onPartialResultWord(java.util.ArrayList<com.sec.android.app.voicenote.common.util.TextData> r4) {
        /*
            r3 = this;
            monitor-enter(r3)
            java.lang.String r0 = "SimpleSttFragment"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0083 }
            r1.<init>()     // Catch:{ all -> 0x0083 }
            java.lang.String r2 = "onPartialResultWord() : mNumberOfRecognition = "
            r1.append(r2)     // Catch:{ all -> 0x0083 }
            int r2 = r3.mNumberOfRecognition     // Catch:{ all -> 0x0083 }
            r1.append(r2)     // Catch:{ all -> 0x0083 }
            java.lang.String r2 = ", Record state = "
            r1.append(r2)     // Catch:{ all -> 0x0083 }
            com.sec.android.app.voicenote.service.SimpleEngine r2 = r3.mSimpleEngine     // Catch:{ all -> 0x0083 }
            int r2 = r2.getRecorderState()     // Catch:{ all -> 0x0083 }
            r1.append(r2)     // Catch:{ all -> 0x0083 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0083 }
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r1)     // Catch:{ all -> 0x0083 }
            java.lang.String r0 = "SimpleSttFragment"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0083 }
            r1.<init>()     // Catch:{ all -> 0x0083 }
            java.lang.String r2 = "onPartialResultWord() : mIsLastWord = "
            r1.append(r2)     // Catch:{ all -> 0x0083 }
            boolean r2 = r3.mIsLastWord     // Catch:{ all -> 0x0083 }
            r1.append(r2)     // Catch:{ all -> 0x0083 }
            java.lang.String r2 = ", mIsLastWordSaved = "
            r1.append(r2)     // Catch:{ all -> 0x0083 }
            boolean r2 = r3.mIsLastWordSaved     // Catch:{ all -> 0x0083 }
            r1.append(r2)     // Catch:{ all -> 0x0083 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0083 }
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r1)     // Catch:{ all -> 0x0083 }
            android.os.Handler r0 = r3.mEventHandler     // Catch:{ all -> 0x0083 }
            r1 = 1003(0x3eb, float:1.406E-42)
            r0.removeMessages(r1)     // Catch:{ all -> 0x0083 }
            boolean r0 = r3.mIsLastWordSaved     // Catch:{ all -> 0x0083 }
            if (r0 != 0) goto L_0x0081
            com.sec.android.app.voicenote.service.SimpleEngine r0 = r3.mSimpleEngine     // Catch:{ all -> 0x0083 }
            int r0 = r0.getRecorderState()     // Catch:{ all -> 0x0083 }
            r1 = 3
            if (r0 == r1) goto L_0x0081
            com.sec.android.app.voicenote.service.SimpleEngine r0 = r3.mSimpleEngine     // Catch:{ all -> 0x0083 }
            int r0 = r0.getRecorderState()     // Catch:{ all -> 0x0083 }
            r1 = 4
            if (r0 != r1) goto L_0x0067
            goto L_0x0081
        L_0x0067:
            if (r4 == 0) goto L_0x007f
            com.sec.android.app.voicenote.ui.recognizer.RecognizerData r0 = r3.mRecognizerData     // Catch:{ all -> 0x0083 }
            r0.addPartialText(r4)     // Catch:{ all -> 0x0083 }
            com.sec.android.app.voicenote.service.SimpleMetadataRepository r4 = r3.mSimpleMetadata     // Catch:{ all -> 0x0083 }
            com.sec.android.app.voicenote.ui.recognizer.RecognizerData r0 = r3.mRecognizerData     // Catch:{ all -> 0x0083 }
            java.util.ArrayList r0 = r0.getDisplayedSttData()     // Catch:{ all -> 0x0083 }
            r4.setDisplayedSttData(r0)     // Catch:{ all -> 0x0083 }
            r3.updateDisplayText()     // Catch:{ all -> 0x0083 }
            r3.paintRecordingData()     // Catch:{ all -> 0x0083 }
        L_0x007f:
            monitor-exit(r3)
            return
        L_0x0081:
            monitor-exit(r3)
            return
        L_0x0083:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.SimpleSttFragment.onPartialResultWord(java.util.ArrayList):void");
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
        if (!this.mIsLastWord && this.mSimpleEngine.getRecorderState() == 2) {
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
        if (this.mIsLastWord) {
            this.mEventHandler.sendEmptyMessage(1004);
            this.mEventHandler.removeMessages(1003);
            this.mEventHandler.removeMessages(1000);
            this.mEventHandler.sendEmptyMessageDelayed(1000, this.mNumberOfRecognition > 0 ? 4500 : 0);
            paintRecordingData();
            return;
        }
        this.mIsLastWordSaved = false;
    }

    private void initialize(boolean z) {
        Log.m29v(TAG, "initialize(" + z + ')');
        this.mIsTouched = false;
        this.mIsLongPressed = false;
        this.mIsScrollMoved = false;
        this.mIsSttViewMoved = false;
        this.mRecognizerData.resetAdvancedPlayer();
        String peek = DialogFactory.peek();
        if (peek == null || !peek.equals(DialogFactory.EDIT_STT_DIALOG)) {
            resetTouchedArea();
        }
        this.mIsLastWord = false;
        this.mIsLastWordSaved = false;
        if (this.mSimpleEngine.getRecorderState() != 1 && !z) {
            this.mSttTextView.setTextIsSelectable(false);
            this.mRecognizerData.loadSttDataFromFile();
            this.mIsLastWord = this.mEventHandler.hasMessages(1000);
        } else if (this.mSimpleEngine.getPlayerState() != 1) {
            this.mEventHandler.removeMessages(1003);
            this.mRecognizerData.updatePlayedTime(this.mSimpleEngine.getCurrentTime());
            this.mSttTextView.setTextIsSelectable(true);
            this.mRecognizerData.loadSttDataFromFile();
            updateDisplayText();
            this.mRecognizerData.updateBookmark();
        }
    }

    private void updateDisplayText() {
        SpannableStringBuilder spannableStringBuilder = this.mDisplayString;
        if (spannableStringBuilder != null) {
            spannableStringBuilder.replace(0, spannableStringBuilder.length(), this.mRecognizerData.getDisplayText());
        }
    }

    private boolean isOverWriteMode() {
        return this.mSimpleEngine.getContentItemCount() > 1;
    }

    /* access modifiers changed from: private */
    public void saveSttData() {
        if (this.mIsLastWordSaved) {
            Log.m19d(TAG, "saveSttData already saved");
            return;
        }
        this.mIsLastWordSaved = true;
        this.mRecognizerData.writeMetaData(getContext());
        postEvent(1004);
        this.mEventHandler.sendEmptyMessage(1005);
    }

    private void paintPlayingData() {
        SpannableStringBuilder spannableStringBuilder = this.mDisplayString;
        if (spannableStringBuilder != null && !this.mIsTouched && !this.mIsSttViewMoved) {
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
            if (lineCount > 0 && !this.mIsScrollMoved) {
                Rect rect = new Rect();
                this.mSttTextView.getLineBounds(lineCount - 1, rect);
                double scrollY = (double) (rect.bottom - this.mScrollView.getScrollY());
                double height = ((double) this.mScrollView.getHeight()) * 0.3d;
                double height2 = ((double) this.mScrollView.getHeight()) * 0.7d;
                int dimension = (int) getResources().getDimension(C0690R.dimen.stt_line_margin_top_text);
                if (height2 < scrollY || height > scrollY) {
                    this.mScrollView.smoothScrollTo(0, ((((int) (((double) rect.bottom) - height2)) / this.mSttTextView.getLineHeight()) * this.mSttTextView.getLineHeight()) + dimension);
                }
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
            this.mSttTextView.getLineBounds(lineCount - 1, rect);
            double scrollY = (double) (rect.bottom - this.mScrollView.getScrollY());
            double height = ((double) this.mScrollView.getHeight()) * 0.3d;
            double height2 = ((double) this.mScrollView.getHeight()) * 0.7d;
            if (height2 < scrollY || height > scrollY) {
                this.mScrollView.smoothScrollTo(0, (int) (((double) rect.bottom) - height2));
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
                    if (this.mSimpleEngine.getPlayerState() == 4) {
                        int resumePlay = this.mSimpleEngine.resumePlay();
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
                    }
                } else if (i2 == 1) {
                    Log.m29v(TAG, "onDialogResult() : EditSttResult.DISMISS");
                    moveSttView(MOVE_DOWN);
                    resetTouchedArea();
                    if (this.mIsNeedPlayResume) {
                        if (this.mSimpleEngine.getPlayerState() == 4) {
                            int resumePlay2 = this.mSimpleEngine.resumePlay();
                            if (resumePlay2 == -103) {
                                Toast.makeText(getActivity(), C0690R.string.no_play_during_call, 0).show();
                            } else if (resumePlay2 == 0) {
                                postEvent(VoiceNoteApplication.convertEvent(Event.PLAY_RESUME));
                            }
                        }
                        this.mIsNeedPlayResume = false;
                    }
                    updateDisplayText();
                    paintPlayingData();
                }
            }
        }
    }

    private void moveSttView(String str) {
        View findViewById;
        Log.m26i(TAG, "moveSttView() : " + str + ", mTouchedWordIndex = " + this.mRecognizerData.getTouchedIndex());
        if (getActivity() != null && (findViewById = getActivity().getWindow().getDecorView().findViewById(C0690R.C0693id.simple_stt)) != null) {
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
}
