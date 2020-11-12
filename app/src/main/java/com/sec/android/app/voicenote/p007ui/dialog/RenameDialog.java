package com.sec.android.app.voicenote.p007ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.CategoryRepository;
import com.sec.android.app.voicenote.common.util.DataRepository;
import com.sec.android.app.voicenote.common.util.EmoticonUtils;
import com.sec.android.app.voicenote.p007ui.AbsDialogFragment;
import com.sec.android.app.voicenote.p007ui.adapter.CategoryListAdapter;
import com.sec.android.app.voicenote.p007ui.adapter.SpinnerSimpleCursorAdapter;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.p007ui.view.WindowFocusLayout;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.DBProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.NFCProvider;
import com.sec.android.app.voicenote.provider.PermissionProvider;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.service.BookmarkHolder;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.MetadataRepository;
import com.sec.android.app.voicenote.service.Player;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Pattern;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.RenameDialog */
public class RenameDialog extends AbsDialogFragment implements AdapterView.OnItemSelectedListener, DialogFactory.DialogResultListener, Observer {
    private static final String BUNDLE_ERROR_TYPE = "error_type";
    private static final String BUNDLE_NAME_CHANGED = "name_change";
    private static final String DISABLE_EMOTICON_FLAG = "disableEmoticonInput=true";
    private static final String DISABLE_GIF_FLAG = "disableGifKeyboard=true";
    private static final String DISABLE_LIVE_MESSAGE = "disableLiveMessage=true";
    private static final String DISABLE_PREDICTION_FLAG = "inputType=PredictionOff";
    private static final String DISABLE_STICKER_FLAG = "disableSticker=true";
    private static final String DISABLE_SYMBLE_FLAG = "inputType=filename";
    private static final int MAX_LENGTH = 50;
    private static final String NO_MICROPHONE_KEY = "noMicrophoneKey";
    private static final String TAG = "RenameDialog";
    private static final String VOICEINPUT_OFF = "disableVoiceInput=true";
    private LinearLayout mCancelButton = null;
    /* access modifiers changed from: private */
    public AppCompatSpinner mCategorySpinner = null;
    /* access modifiers changed from: private */
    public TextView mCategoryTextView = null;
    private int mCurrentLabelPos = 0;
    private Cursor mCursor = null;
    private AlertDialog mDialog;
    private int mInputErrorType = -1;
    /* access modifiers changed from: private */
    public TextInputLayout mInputLayout = null;
    /* access modifiers changed from: private */
    public EditText mInputView = null;
    private DialogFactory.DialogResultListener mInterface = null;
    /* access modifiers changed from: private */
    public boolean mIsKeyboardVisible = true;
    /* access modifiers changed from: private */
    public boolean mIsNameChanged;
    /* access modifiers changed from: private */
    public boolean mIsNewRecording = false;
    private IntentFilter mKeyboardIntentFilter;
    private BroadcastReceiver mKeyboardReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            long unused = RenameDialog.this.mReceiveTime = System.currentTimeMillis();
            boolean unused2 = RenameDialog.this.mIsKeyboardVisible = intent.getBooleanExtra("AxT9IME.isVisibleWindow", true);
        }
    };
    /* access modifiers changed from: private */
    public int mLabelID = 0;
    private LinearLayout mLinearLayoutSpinner = null;
    private SpinnerSimpleCursorAdapter mListAdapter = null;
    private int mMaxPosition = 0;
    /* access modifiers changed from: private */
    public LinearLayout mOKButton = null;
    private VoiceNoteObservable mObservable;
    private String mOriginalPath = null;
    /* access modifiers changed from: private */
    public String mOriginalTitle = null;
    /* access modifiers changed from: private */
    public String mPreviousName = null;
    private ProgressBar mProgressBar = null;
    /* access modifiers changed from: private */
    public long mReceiveTime;
    private int mRequestCode = -1;
    /* access modifiers changed from: private */
    public int mTotalLength;
    private TextView mTvCancelButton = null;
    /* access modifiers changed from: private */
    public TextView mTvOkButton = null;

    private long convertRecordingDurationForSAData(int i) {
        float f = (((float) i) / 1000.0f) / 60.0f;
        if (f < 0.25f) {
            return 1;
        }
        if (f < 1.0f) {
            return 2;
        }
        if (f < 3.0f) {
            return 3;
        }
        if (f < 5.0f) {
            return 4;
        }
        if (f < 10.0f) {
            return 5;
        }
        if (f < 20.0f) {
            return 6;
        }
        if (f < 30.0f) {
            return 7;
        }
        if (f < 60.0f) {
            return 8;
        }
        if (f < 90.0f) {
            return 9;
        }
        return f < 120.0f ? 10 : 11;
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    /* renamed from: com.sec.android.app.voicenote.ui.dialog.RenameDialog$InputErrorType */
    private static class InputErrorType {
        static final int FILE_NAME_ALREADY_EXISTS = 2;
        static final int INVALID_CHARACTER = 1;
        static final int MAX_CHAR_REACHED_MSG = 0;

        private InputErrorType() {
        }
    }

    public static RenameDialog newInstance(Bundle bundle, DialogFactory.DialogResultListener dialogResultListener) {
        RenameDialog renameDialog = new RenameDialog();
        renameDialog.setArguments(bundle);
        renameDialog.setListener(dialogResultListener);
        return renameDialog;
    }

    @SuppressLint({"ClickableViewAccessibility"})
    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        int i;
        WindowFocusLayout windowFocusLayout = (WindowFocusLayout) getActivity().getLayoutInflater().inflate(C0690R.layout.rename, (ViewGroup) null);
        View inflate = getActivity().getLayoutInflater().inflate(C0690R.layout.dialog_title, (ViewGroup) null);
        this.mInputLayout = (TextInputLayout) windowFocusLayout.findViewById(C0690R.C0693id.rename_input_wrapper);
        this.mInputView = (EditText) windowFocusLayout.findViewById(C0690R.C0693id.rename_input);
        this.mCategorySpinner = (AppCompatSpinner) windowFocusLayout.findViewById(C0690R.C0693id.dialog_category_select);
        this.mLinearLayoutSpinner = (LinearLayout) windowFocusLayout.findViewById(C0690R.C0693id.layout_spinner);
        this.mCategoryTextView = (TextView) windowFocusLayout.findViewById(C0690R.C0693id.category);
        TextView textView = (TextView) windowFocusLayout.findViewById(C0690R.C0693id.rename_message);
        this.mOKButton = (LinearLayout) windowFocusLayout.findViewById(C0690R.C0693id.button_ok);
        this.mTvOkButton = (TextView) windowFocusLayout.findViewById(C0690R.C0693id.tvOkButton);
        this.mCancelButton = (LinearLayout) windowFocusLayout.findViewById(C0690R.C0693id.button_cancel);
        this.mTvCancelButton = (TextView) windowFocusLayout.findViewById(C0690R.C0693id.tvCancelButton);
        this.mProgressBar = (ProgressBar) windowFocusLayout.findViewById(C0690R.C0693id.progress);
        this.mCategorySpinner.setOnItemSelectedListener(this);
        this.mLabelID = getArguments().getInt(DialogFactory.BUNDLE_LABEL_ID, -1);
        int i2 = getArguments().getInt("record_mode", 1);
        long j = getArguments().getLong(DialogFactory.BUNDLE_ID, -1);
        this.mRequestCode = getArguments().getInt(DialogFactory.BUNDLE_REQUEST_CODE, -1);
        if (this.mLabelID == 0 && j > -1 && DBProvider.getInstance().isCallHistoryFile(j)) {
            this.mLabelID = 3;
        }
        this.mOKButton.setVisibility(0);
        this.mProgressBar.setVisibility(8);
        this.mObservable = VoiceNoteObservable.getInstance();
        this.mObservable.addObserver(this);
        if (Settings.isEnabledShowButtonBG()) {
            this.mCategorySpinner.setBackgroundResource(C0690R.C0692drawable.voice_note_btn_spinner_button_background);
            this.mTvCancelButton.setTextColor(getActivity().getResources().getColor(C0690R.C0691color.main_dialog_bg, (Resources.Theme) null));
            this.mTvCancelButton.setBackgroundResource(C0690R.C0692drawable.dialog_button_shape_bg);
            this.mTvOkButton.setTextColor(getActivity().getResources().getColor(C0690R.C0691color.main_dialog_bg, (Resources.Theme) null));
            this.mTvOkButton.setBackgroundResource(C0690R.C0692drawable.dialog_button_shape_bg);
        }
        int i3 = this.mLabelID;
        if (i3 == -2) {
            this.mLabelID = 0;
        } else if (i3 == -1) {
            if (i2 == 2) {
                this.mLabelID = 1;
            } else if (i2 != 4) {
                this.mLabelID = 0;
            } else {
                this.mLabelID = 2;
            }
        }
        this.mCategorySpinner.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                RenameDialog.this.mCategorySpinner.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                RenameDialog.this.mCategorySpinner.setGravity(8388659);
                RenameDialog.this.mCategorySpinner.setDropDownVerticalOffset((((-RenameDialog.this.mCategorySpinner.getHeight()) - RenameDialog.this.getResources().getDimensionPixelOffset(C0690R.dimen.margin_popup_spinner)) + RenameDialog.this.getResources().getDimensionPixelOffset(C0690R.dimen.margin_bottom_input_layout_popup_spinner)) - RenameDialog.this.mCategoryTextView.getHeight());
            }
        });
        this.mCategorySpinner.setOnTouchListener(new View.OnTouchListener() {
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return RenameDialog.this.lambda$onCreateDialog$0$RenameDialog(view, motionEvent);
            }
        });
        listBinding();
        int positionById = getPositionById(this.mLabelID);
        if (positionById != -1) {
            this.mCategorySpinner.setSelection(positionById);
        }
        this.mOriginalPath = getArguments().getString(DialogFactory.BUNDLE_PATH, (String) null);
        String str = this.mOriginalPath;
        if (str != null) {
            int lastIndexOf = str.lastIndexOf(47);
            int lastIndexOf2 = this.mOriginalPath.lastIndexOf(46);
            if (lastIndexOf >= 0 && (i = lastIndexOf + 1) < lastIndexOf2 && lastIndexOf2 < this.mOriginalPath.length()) {
                this.mOriginalTitle = this.mOriginalPath.substring(i, lastIndexOf2);
            }
        } else {
            this.mOriginalTitle = getArguments().getString(DialogFactory.BUNDLE_NAME, (String) null);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        TextView textView2 = (TextView) inflate.findViewById(C0690R.C0693id.title_dialog);
        this.mInputLayout.setErrorEnabled(false);
        int i4 = this.mRequestCode;
        int i5 = C0690R.string.save;
        if (i4 == 11) {
            this.mIsNewRecording = true;
            textView2.setText(C0690R.string.new_recording_dialog_title);
            builder.setCustomTitle(inflate);
            this.mInputView.setText(this.mOriginalTitle);
        } else if (i4 != 17) {
            this.mIsNewRecording = false;
            i5 = C0690R.string.rename;
            textView2.setText(C0690R.string.rename_recording);
            builder.setCustomTitle(inflate);
            this.mInputView.setText(this.mOriginalTitle);
        } else {
            this.mIsNewRecording = true;
            textView2.setText(C0690R.string.save_convert_file_stt_dialog_title);
            builder.setCustomTitle(inflate);
            if (!Engine.getInstance().isTranslationComplete()) {
                textView.setText(C0690R.string.save_convert_file_stt_not_complete_message);
                textView.setVisibility(0);
            } else {
                textView.setVisibility(8);
            }
            this.mOriginalTitle += "_" + getString(C0690R.string.prefix_voicememo).toLowerCase();
            if (DBProvider.getInstance().isSameFileInLibrary(this.mOriginalTitle)) {
                this.mOriginalTitle = DBProvider.getInstance().createNewTitle(this.mOriginalTitle);
            }
            this.mInputView.setText(this.mOriginalTitle);
        }
        this.mTvOkButton.setText(i5);
        this.mOKButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                RenameDialog.this.lambda$onCreateDialog$1$RenameDialog(view);
            }
        });
        this.mTvCancelButton.setText(C0690R.string.cancel);
        this.mCancelButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                RenameDialog.this.lambda$onCreateDialog$2$RenameDialog(view);
            }
        });
        builder.setView(windowFocusLayout);
        String str2 = this.mOriginalTitle;
        if (str2 != null && str2.length() > 50) {
            setInputErrorMessage(0);
        }
        this.mInputView.setSelected(false);
        this.mInputView.setFocusable(true);
        EditText editText = this.mInputView;
        String str3 = this.mOriginalTitle;
        editText.setFilters(getNameFilter(true, str3 != null ? str3.length() : 0));
        this.mInputView.selectAll();
        this.mInputView.requestFocus();
        StringBuilder sb = new StringBuilder();
        sb.append(DISABLE_PREDICTION_FLAG);
        sb.append(";");
        sb.append(DISABLE_EMOTICON_FLAG);
        sb.append(";");
        sb.append(DISABLE_SYMBLE_FLAG);
        sb.append(";");
        sb.append(DISABLE_GIF_FLAG);
        sb.append(";");
        sb.append(DISABLE_LIVE_MESSAGE);
        sb.append(";");
        sb.append(DISABLE_STICKER_FLAG);
        if (this.mIsNewRecording) {
            sb.append(";");
            sb.append(VOICEINPUT_OFF);
            sb.append(";");
            sb.append(NO_MICROPHONE_KEY);
        }
        this.mInputView.setPrivateImeOptions(sb.toString());
        windowFocusLayout.setOnWindowFocusChangeListener(new WindowFocusLayout.OnWindowFocusChangeListener() {
            public final void onWindowFocusChanged(boolean z) {
                RenameDialog.this.lambda$onCreateDialog$4$RenameDialog(z);
            }
        });
        final AlertDialog create = builder.create();
        this.mDialog = create;
        updateLayoutDialog(1.0f, true);
        if (bundle != null) {
            this.mIsNameChanged = bundle.getBoolean(BUNDLE_NAME_CHANGED, false);
            this.mInputErrorType = bundle.getInt(BUNDLE_ERROR_TYPE, -1);
        }
        if (this.mPreviousName == null) {
            this.mPreviousName = this.mInputView.getText().toString();
        }
        this.mInputView.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            /* JADX WARNING: Removed duplicated region for block: B:23:0x00b9  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public synchronized void afterTextChanged(android.text.Editable r6) {
                /*
                    r5 = this;
                    monitor-enter(r5)
                    android.app.AlertDialog r0 = r0     // Catch:{ all -> 0x00e4 }
                    if (r0 == 0) goto L_0x00d9
                    android.app.AlertDialog r0 = r0     // Catch:{ all -> 0x00e4 }
                    boolean r0 = r0.isShowing()     // Catch:{ all -> 0x00e4 }
                    if (r0 == 0) goto L_0x00d9
                    com.sec.android.app.voicenote.ui.dialog.RenameDialog r0 = com.sec.android.app.voicenote.p007ui.dialog.RenameDialog.this     // Catch:{ all -> 0x00e4 }
                    android.widget.EditText r0 = r0.mInputView     // Catch:{ all -> 0x00e4 }
                    android.text.Editable r0 = r0.getText()     // Catch:{ all -> 0x00e4 }
                    java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x00e4 }
                    java.lang.String r0 = r0.trim()     // Catch:{ all -> 0x00e4 }
                    boolean r1 = r0.isEmpty()     // Catch:{ all -> 0x00e4 }
                    r2 = 0
                    if (r1 != 0) goto L_0x0074
                    com.sec.android.app.voicenote.ui.dialog.RenameDialog r1 = com.sec.android.app.voicenote.p007ui.dialog.RenameDialog.this     // Catch:{ all -> 0x00e4 }
                    java.lang.String r1 = r1.mOriginalTitle     // Catch:{ all -> 0x00e4 }
                    boolean r0 = r0.equals(r1)     // Catch:{ all -> 0x00e4 }
                    if (r0 == 0) goto L_0x0050
                    com.sec.android.app.voicenote.ui.dialog.RenameDialog r0 = com.sec.android.app.voicenote.p007ui.dialog.RenameDialog.this     // Catch:{ all -> 0x00e4 }
                    boolean r0 = r0.mIsNewRecording     // Catch:{ all -> 0x00e4 }
                    if (r0 != 0) goto L_0x0050
                    com.sec.android.app.voicenote.ui.dialog.RenameDialog r0 = com.sec.android.app.voicenote.p007ui.dialog.RenameDialog.this     // Catch:{ all -> 0x00e4 }
                    int r0 = r0.mLabelID     // Catch:{ all -> 0x00e4 }
                    long r0 = (long) r0     // Catch:{ all -> 0x00e4 }
                    com.sec.android.app.voicenote.ui.dialog.RenameDialog r3 = com.sec.android.app.voicenote.p007ui.dialog.RenameDialog.this     // Catch:{ all -> 0x00e4 }
                    androidx.appcompat.widget.AppCompatSpinner r3 = r3.mCategorySpinner     // Catch:{ all -> 0x00e4 }
                    long r3 = r3.getSelectedItemId()     // Catch:{ all -> 0x00e4 }
                    int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
                    if (r0 != 0) goto L_0x0050
                    goto L_0x0074
                L_0x0050:
                    com.sec.android.app.voicenote.ui.dialog.RenameDialog r0 = com.sec.android.app.voicenote.p007ui.dialog.RenameDialog.this     // Catch:{ all -> 0x00e4 }
                    android.widget.LinearLayout r0 = r0.mOKButton     // Catch:{ all -> 0x00e4 }
                    r1 = 1
                    r0.setEnabled(r1)     // Catch:{ all -> 0x00e4 }
                    com.sec.android.app.voicenote.ui.dialog.RenameDialog r0 = com.sec.android.app.voicenote.p007ui.dialog.RenameDialog.this     // Catch:{ all -> 0x00e4 }
                    android.widget.LinearLayout r0 = r0.mOKButton     // Catch:{ all -> 0x00e4 }
                    r0.setFocusable(r1)     // Catch:{ all -> 0x00e4 }
                    com.sec.android.app.voicenote.ui.dialog.RenameDialog r0 = com.sec.android.app.voicenote.p007ui.dialog.RenameDialog.this     // Catch:{ all -> 0x00e4 }
                    android.widget.TextView r0 = r0.mTvOkButton     // Catch:{ all -> 0x00e4 }
                    r3 = 1065353216(0x3f800000, float:1.0)
                    r0.setAlpha(r3)     // Catch:{ all -> 0x00e4 }
                    com.sec.android.app.voicenote.ui.dialog.RenameDialog r0 = com.sec.android.app.voicenote.p007ui.dialog.RenameDialog.this     // Catch:{ all -> 0x00e4 }
                    boolean unused = r0.mIsNameChanged = r1     // Catch:{ all -> 0x00e4 }
                    goto L_0x0097
                L_0x0074:
                    com.sec.android.app.voicenote.ui.dialog.RenameDialog r0 = com.sec.android.app.voicenote.p007ui.dialog.RenameDialog.this     // Catch:{ all -> 0x00e4 }
                    android.widget.LinearLayout r0 = r0.mOKButton     // Catch:{ all -> 0x00e4 }
                    r0.setEnabled(r2)     // Catch:{ all -> 0x00e4 }
                    com.sec.android.app.voicenote.ui.dialog.RenameDialog r0 = com.sec.android.app.voicenote.p007ui.dialog.RenameDialog.this     // Catch:{ all -> 0x00e4 }
                    android.widget.LinearLayout r0 = r0.mOKButton     // Catch:{ all -> 0x00e4 }
                    r0.setFocusable(r2)     // Catch:{ all -> 0x00e4 }
                    com.sec.android.app.voicenote.ui.dialog.RenameDialog r0 = com.sec.android.app.voicenote.p007ui.dialog.RenameDialog.this     // Catch:{ all -> 0x00e4 }
                    android.widget.TextView r0 = r0.mTvOkButton     // Catch:{ all -> 0x00e4 }
                    r1 = 1050253722(0x3e99999a, float:0.3)
                    r0.setAlpha(r1)     // Catch:{ all -> 0x00e4 }
                    com.sec.android.app.voicenote.ui.dialog.RenameDialog r0 = com.sec.android.app.voicenote.p007ui.dialog.RenameDialog.this     // Catch:{ all -> 0x00e4 }
                    boolean unused = r0.mIsNameChanged = r2     // Catch:{ all -> 0x00e4 }
                L_0x0097:
                    com.sec.android.app.voicenote.ui.dialog.RenameDialog r0 = com.sec.android.app.voicenote.p007ui.dialog.RenameDialog.this     // Catch:{ all -> 0x00e4 }
                    java.lang.String r0 = r0.mPreviousName     // Catch:{ all -> 0x00e4 }
                    java.lang.String r1 = r6.toString()     // Catch:{ all -> 0x00e4 }
                    boolean r0 = r0.equals(r1)     // Catch:{ all -> 0x00e4 }
                    if (r0 != 0) goto L_0x00d9
                    com.sec.android.app.voicenote.ui.dialog.RenameDialog r0 = com.sec.android.app.voicenote.p007ui.dialog.RenameDialog.this     // Catch:{ all -> 0x00e4 }
                    int r0 = r0.mTotalLength     // Catch:{ all -> 0x00e4 }
                    r1 = 50
                    if (r0 > r1) goto L_0x00d9
                    com.sec.android.app.voicenote.ui.dialog.RenameDialog r0 = com.sec.android.app.voicenote.p007ui.dialog.RenameDialog.this     // Catch:{ all -> 0x00e4 }
                    com.google.android.material.textfield.TextInputLayout r0 = r0.mInputLayout     // Catch:{ all -> 0x00e4 }
                    if (r0 == 0) goto L_0x00c2
                    com.sec.android.app.voicenote.ui.dialog.RenameDialog r0 = com.sec.android.app.voicenote.p007ui.dialog.RenameDialog.this     // Catch:{ all -> 0x00e4 }
                    com.google.android.material.textfield.TextInputLayout r0 = r0.mInputLayout     // Catch:{ all -> 0x00e4 }
                    r0.setErrorEnabled(r2)     // Catch:{ all -> 0x00e4 }
                L_0x00c2:
                    com.sec.android.app.voicenote.ui.dialog.RenameDialog r0 = com.sec.android.app.voicenote.p007ui.dialog.RenameDialog.this     // Catch:{ all -> 0x00e4 }
                    android.widget.EditText r0 = r0.mInputView     // Catch:{ all -> 0x00e4 }
                    com.sec.android.app.voicenote.ui.dialog.RenameDialog r1 = com.sec.android.app.voicenote.p007ui.dialog.RenameDialog.this     // Catch:{ all -> 0x00e4 }
                    android.content.res.Resources r1 = r1.getResources()     // Catch:{ all -> 0x00e4 }
                    r2 = 2131099747(0x7f060063, float:1.7811856E38)
                    r3 = 0
                    android.content.res.ColorStateList r1 = r1.getColorStateList(r2, r3)     // Catch:{ all -> 0x00e4 }
                    r0.setBackgroundTintList(r1)     // Catch:{ all -> 0x00e4 }
                L_0x00d9:
                    com.sec.android.app.voicenote.ui.dialog.RenameDialog r0 = com.sec.android.app.voicenote.p007ui.dialog.RenameDialog.this     // Catch:{ all -> 0x00e4 }
                    java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x00e4 }
                    java.lang.String unused = r0.mPreviousName = r6     // Catch:{ all -> 0x00e4 }
                    monitor-exit(r5)
                    return
                L_0x00e4:
                    r6 = move-exception
                    monitor-exit(r5)
                    throw r6
                */
                throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.dialog.RenameDialog.C08902.afterTextChanged(android.text.Editable):void");
            }
        });
        this.mInputView.setOnKeyListener(new View.OnKeyListener() {
            public final boolean onKey(View view, int i, KeyEvent keyEvent) {
                return RenameDialog.this.lambda$onCreateDialog$5$RenameDialog(view, i, keyEvent);
            }
        });
        this.mInputView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return RenameDialog.this.lambda$onCreateDialog$6$RenameDialog(textView, i, keyEvent);
            }
        });
        create.setOnShowListener(new DialogInterface.OnShowListener() {
            public final void onShow(DialogInterface dialogInterface) {
                RenameDialog.this.lambda$onCreateDialog$8$RenameDialog(dialogInterface);
            }
        });
        if (bundle != null) {
            DialogFactory.setDialogResultListener(getFragmentManager(), DialogFactory.CATEGORY_RENAME, this);
        }
        return create;
    }

    public /* synthetic */ boolean lambda$onCreateDialog$0$RenameDialog(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() != 1) {
            return false;
        }
        if (VoiceNoteApplication.getScene() != 12) {
            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_save_rec), getResources().getString(C0690R.string.event_save_popup_category));
            return false;
        } else if (!Engine.getInstance().isTranslationComplete()) {
            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_save_convert_stt), getResources().getString(C0690R.string.event_save_convert_category));
            return false;
        } else {
            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_save_convert_stt1), getResources().getString(C0690R.string.event_save_convert_category1));
            return false;
        }
    }

    public /* synthetic */ void lambda$onCreateDialog$1$RenameDialog(View view) {
        dismissAllowingStateLoss();
    }

    public /* synthetic */ void lambda$onCreateDialog$2$RenameDialog(View view) {
        Log.m29v("RenameDialog", "Cancel");
        if (VoiceNoteApplication.getScene() != 12) {
            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_save_rec), getResources().getString(C0690R.string.event_save_popup_cancel));
        } else if (!Engine.getInstance().isTranslationComplete()) {
            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_save_convert_stt), getResources().getString(C0690R.string.event_save_convert_cancel));
        } else {
            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_save_convert_stt1), getResources().getString(C0690R.string.event_save_convert_cancel1));
        }
        dismissAllowingStateLoss();
    }

    public /* synthetic */ void lambda$onCreateDialog$4$RenameDialog(boolean z) {
        if (z) {
            this.mInputView.postDelayed(new Runnable() {
                public final void run() {
                    RenameDialog.this.lambda$null$3$RenameDialog();
                }
            }, 50);
        }
    }

    public /* synthetic */ void lambda$null$3$RenameDialog() {
        if (getActivity() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService("input_method");
//            if (!inputMethodManager.semIsAccessoryKeyboard()) {
//                inputMethodManager.showSoftInput(this.mInputView, 0);
//            }
        }
    }

    public /* synthetic */ boolean lambda$onCreateDialog$5$RenameDialog(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() != 66) {
            return false;
        }
        excutePositiveEvent(this.mInputView);
        return false;
    }

    public /* synthetic */ boolean lambda$onCreateDialog$6$RenameDialog(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        excutePositiveEvent(this.mInputView);
        return false;
    }

    public /* synthetic */ void lambda$onCreateDialog$8$RenameDialog(DialogInterface dialogInterface) {
        this.mOKButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                RenameDialog.this.lambda$null$7$RenameDialog(view);
            }
        });
    }

    public /* synthetic */ void lambda$null$7$RenameDialog(View view) {
        String str;
        if (getActivity() != null && isAdded()) {
            Log.m19d("RenameDialog", "onClick");
            String replaceAll = this.mInputView.getText().toString().trim().replaceAll("\n", " ");
            Bundle arguments = getArguments();
            String str2 = this.mOriginalTitle;
            if ((str2 == null || str2.equals(replaceAll)) && ((long) this.mLabelID) == this.mCategorySpinner.getSelectedItemId() && !this.mIsNewRecording) {
                String str3 = this.mOriginalTitle;
                if (str3 != null && str3.equals(replaceAll)) {
                    setInputErrorMessage(2);
                }
            } else if (!DBProvider.getInstance().isSameFileInLibrary(replaceAll) || (((str = this.mOriginalTitle) == null || str.equals(replaceAll)) && this.mRequestCode != 17)) {
                int recordingDuration = Engine.getInstance().getRecordingDuration();
                String str4 = this.mOriginalPath;
                if (str4 != null && !str4.isEmpty()) {
                    File file = new File(this.mOriginalPath);
                    int lastIndexOf = this.mOriginalPath.lastIndexOf(46);
                    String str5 = this.mOriginalPath;
                    String str6 = file.getParent() + '/' + replaceAll + str5.substring(lastIndexOf, str5.length());
                    if (((long) this.mLabelID) != this.mCategorySpinner.getSelectedItemId() && VoiceNoteApplication.getScene() == 3 && DataRepository.getInstance().getCategoryRepository().isChildList()) {
                        Player.getInstance().stopPlay();
                        VoiceNoteObservable.getInstance().notifyObservers(3);
                    }
                    if (this.mRequestCode == 17) {
                        Engine.getInstance().setUserSettingName(replaceAll);
                        Engine.getInstance().setCategoryID(this.mCategorySpinner.getSelectedItemId());
                        Engine.getInstance().stopTranslation(false);
                    } else {
                        NFCProvider.deleteTagsData(getActivity(), DBProvider.getInstance().getIdByPath(this.mOriginalPath));
                        renameFile(this.mOriginalPath, str6);
                    }
                } else if (this.mInterface != null) {
                    arguments.putString(DialogFactory.BUNDLE_NAME, replaceAll);
                    arguments.putInt("result_code", -1);
                    arguments.putLong(DialogFactory.BUNDLE_LABEL_ID, this.mCategorySpinner.getSelectedItemId());
                    this.mInterface.onDialogResult(this, arguments);
                } else if (replaceAll != null && !replaceAll.isEmpty()) {
                    Engine.getInstance().setUserSettingName(replaceAll);
                    Engine.getInstance().setCategoryID(this.mCategorySpinner.getSelectedItemId());
                    if (PermissionProvider.checkPermission((AppCompatActivity) getActivity(), (ArrayList<Integer>) null, false)) {
                        long stopRecord = Engine.getInstance().stopRecord(true, false);
                        if (stopRecord >= 0 || stopRecord == -2) {
                            CursorProvider.getInstance().resetCurrentPlayingItemPosition();
                            if (Settings.getIntSettings("record_mode", 1) == 4) {
                                VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.RECORD_STOP_DELAYED));
                            } else {
                                VoiceNoteObservable.getInstance().notifyObservers(1004);
                            }
                        }
                    } else {
                        Log.m26i("RenameDialog", "Event.RECORD_STOP show permission dialog. Is this possible??");
                    }
                }
                if (this.mIsNameChanged) {
                    if (this.mIsNewRecording) {
                        SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_RENAME_NEW_REC, -1);
                    } else {
                        SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_RENAME, arguments.getInt(DialogFactory.BUNDLE_SCENE));
                    }
                    if (VoiceNoteApplication.getScene() != 12) {
                        int selectedItemId = (int) this.mCategorySpinner.getSelectedItemId();
                        if (selectedItemId == 0) {
                            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_save_rec), getResources().getString(C0690R.string.event_save_popup_save), "1", convertRecordingDurationForSAData(recordingDuration));
                        } else if (selectedItemId == 1) {
                            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_save_rec), getResources().getString(C0690R.string.event_save_popup_save), "2", convertRecordingDurationForSAData(recordingDuration));
                        } else if (selectedItemId != 2) {
                            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_save_rec), getResources().getString(C0690R.string.event_save_popup_save), SALogProvider.QUALITY_MMS, convertRecordingDurationForSAData(recordingDuration));
                        } else {
                            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_save_rec), getResources().getString(C0690R.string.event_save_popup_save), SALogProvider.QUALITY_LOW, convertRecordingDurationForSAData(recordingDuration));
                        }
                    } else if (!Engine.getInstance().isTranslationComplete()) {
                        int selectedItemId2 = (int) this.mCategorySpinner.getSelectedItemId();
                        if (selectedItemId2 == 0) {
                            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_save_convert_stt), getResources().getString(C0690R.string.event_save_convert_save), "1", convertRecordingDurationForSAData(recordingDuration));
                        } else if (selectedItemId2 == 1) {
                            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_save_convert_stt), getResources().getString(C0690R.string.event_save_convert_save), "2", convertRecordingDurationForSAData(recordingDuration));
                        } else if (selectedItemId2 != 2) {
                            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_save_convert_stt), getResources().getString(C0690R.string.event_save_convert_save), SALogProvider.QUALITY_MMS, convertRecordingDurationForSAData(recordingDuration));
                        } else {
                            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_save_convert_stt), getResources().getString(C0690R.string.event_save_convert_save), SALogProvider.QUALITY_LOW, convertRecordingDurationForSAData(recordingDuration));
                        }
                    } else {
                        int selectedItemId3 = (int) this.mCategorySpinner.getSelectedItemId();
                        if (selectedItemId3 == 0) {
                            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_save_convert_stt1), getResources().getString(C0690R.string.event_save_convert_save1), "1", convertRecordingDurationForSAData(recordingDuration));
                        } else if (selectedItemId3 == 1) {
                            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_save_convert_stt1), getResources().getString(C0690R.string.event_save_convert_save1), "2", convertRecordingDurationForSAData(recordingDuration));
                        } else if (selectedItemId3 != 2) {
                            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_save_convert_stt1), getResources().getString(C0690R.string.event_save_convert_save1), SALogProvider.QUALITY_MMS, convertRecordingDurationForSAData(recordingDuration));
                        } else {
                            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_save_convert_stt1), getResources().getString(C0690R.string.event_save_convert_save1), SALogProvider.QUALITY_LOW, convertRecordingDurationForSAData(recordingDuration));
                        }
                    }
                }
                if (VoiceNoteApplication.getScene() != 12) {
                    dismissAllowingStateLoss();
                }
            } else {
                setInputErrorMessage(2);
            }
        }
    }

    public void onStart() {
        super.onStart();
        if (!this.mIsNameChanged && !this.mIsNewRecording && ((long) this.mLabelID) == this.mCategorySpinner.getSelectedItemId()) {
            this.mOKButton.setEnabled(false);
            this.mTvOkButton.setAlpha(0.3f);
        }
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.mInterface = null;
        super.onDismiss(dialogInterface);
    }

    public void update(Observable observable, Object obj) {
        AlertDialog alertDialog;
        int intValue = ((Integer) obj).intValue();
        Log.m26i("RenameDialog", "update - data : " + intValue);
        if (intValue == 7010) {
            AlertDialog alertDialog2 = this.mDialog;
            if (alertDialog2 != null && alertDialog2.isShowing()) {
                this.mOKButton.setVisibility(8);
                this.mProgressBar.setVisibility(0);
                ((InputMethodManager) getActivity().getSystemService("input_method")).hideSoftInputFromWindow(this.mInputView.getWindowToken(), 0);
                this.mInputView.setFocusable(false);
                updateLayoutDialog(0.5f, false);
            }
        } else if (intValue == 7011 && (alertDialog = this.mDialog) != null && alertDialog.isShowing()) {
            this.mOKButton.setVisibility(0);
            this.mProgressBar.setVisibility(8);
            updateLayoutDialog(1.0f, true);
            dismissAllowingStateLoss();
        }
    }

    private void updateLayoutDialog(float f, boolean z) {
        this.mInputLayout.setAlpha(f);
        this.mInputLayout.setClickable(z);
        this.mCategoryTextView.setAlpha(f);
        this.mLinearLayoutSpinner.setAlpha(f);
        this.mLinearLayoutSpinner.setClickable(z);
        this.mCancelButton.setEnabled(z);
        this.mDialog.setCancelable(z);
    }

    private void setHorizontalOffsetSpinner() {
        if (getResources().getConfiguration().getLayoutDirection() == 1) {
            this.mCategorySpinner.setDropDownHorizontalOffset((this.mLinearLayoutSpinner.getWidth() - this.mCategorySpinner.getWidth()) + this.mCategoryTextView.getWidth());
            return;
        }
        AppCompatSpinner appCompatSpinner = this.mCategorySpinner;
        appCompatSpinner.setDropDownHorizontalOffset((appCompatSpinner.getWidth() - this.mInputLayout.getWidth()) - getResources().getDimensionPixelOffset(C0690R.dimen.margin_start_popup_spinner));
    }

    private void renameFile(String str, String str2) {
        Log.m26i("RenameDialog", "renameFile");
        File file = new File(str);
        long idByPath = DBProvider.getInstance().getIdByPath(str);
        File file2 = new File(str2);
        if (file.renameTo(file2)) {
            DBProvider.getInstance().updateFileFromDB(idByPath, this.mCategorySpinner.getSelectedItemId(), file2);
            BookmarkHolder.getInstance().replace(str, str2);
            MetadataRepository.getInstance().rename(str, str2);
            if (Engine.getInstance().getPlayerState() != 1) {
                Engine.getInstance().renamePath(str2);
            }
        } else {
            DBProvider.getInstance().updateCategoryIdFileFromDB(idByPath, this.mCategorySpinner.getSelectedItemId());
        }
        VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.UPDATE_FILE_NAME));
        int scene = VoiceNoteApplication.getScene();
        if (scene == 5) {
            VoiceNoteObservable.getInstance().notifyObservers(7);
        } else if (scene == 10) {
            VoiceNoteObservable.getInstance().notifyObservers(14);
            VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.SEARCH_RECORDINGS));
        }
    }

    public InputFilter[] getNameFilter(boolean z, int i) {
        InputFilter[] inputFilterArr = new InputFilter[2];
        inputFilterArr[0] = new InputFilter() {
            public final CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
                return RenameDialog.this.lambda$getNameFilter$9$RenameDialog(charSequence, i, i2, spanned, i3, i4);
            }
        };
        if (!z || 50 >= i) {
            i = 50;
        }
        inputFilterArr[1] = new InputFilter.LengthFilter(i) {
            public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
                CharSequence filter = super.filter(charSequence, i, i2, spanned, i3, i4);
                int unused = RenameDialog.this.mTotalLength = (charSequence.subSequence(i, i2).toString().length() + spanned.length()) - spanned.subSequence(i3, i4).toString().length();
                if (filter != null && charSequence.length() > 0) {
                    RenameDialog.this.setInputErrorMessage(0);
                }
                return filter;
            }
        };
        return inputFilterArr;
    }

    public /* synthetic */ CharSequence lambda$getNameFilter$9$RenameDialog(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
        if (i2 - i > 50) {
            i2 = i + 50;
        }
        String charSequence2 = charSequence.subSequence(i, i2).toString();
        String charSequence3 = spanned.subSequence(i3, i4).toString();
        boolean z = false;
        if (checkInvalidChars(charSequence2) || EmoticonUtils.hasEmoticon(charSequence2)) {
            z = true;
        }
        if (!z) {
            return null;
        }
        setInputErrorMessage(1);
        return charSequence3;
    }

    private boolean checkInvalidChars(String str) {
        return Pattern.compile("[\\*/\\\\\\?:<>\\|\"]+").matcher(str).find();
    }

    private void excutePositiveEvent(EditText editText) {
        String str;
        if (this.mDialog == null) {
            return;
        }
        if (this.mOKButton.isEnabled()) {
            this.mOKButton.callOnClick();
            return;
        }
        String replaceAll = editText.getText().toString().trim().replaceAll("\n", " ");
        String str2 = this.mOriginalTitle;
        if (((str2 != null && !str2.equals(replaceAll)) || ((long) this.mLabelID) != this.mCategorySpinner.getSelectedItemId() || this.mIsNewRecording) && DBProvider.getInstance().isSameFileInLibrary(replaceAll) && (str = this.mOriginalTitle) != null && !str.equals(replaceAll)) {
            setInputErrorMessage(2);
        }
    }

    public void onResume() {
        EditText editText;
        super.onResume();
        if (getShowsDialog() && getDialog() != null && (editText = this.mInputView) != null) {
            editText.requestFocus();
            if (this.mIsKeyboardVisible) {
                this.mInputView.postDelayed(new Runnable() {
                    public final void run() {
                        RenameDialog.this.lambda$onResume$10$RenameDialog();
                    }
                }, 250);
            }
            if (this.mKeyboardIntentFilter == null) {
                this.mKeyboardIntentFilter = new IntentFilter();
                this.mKeyboardIntentFilter.addAction("ResponseAxT9Info");
            }
            if (this.mKeyboardReceiver != null && getActivity() != null) {
                getActivity().getApplicationContext().registerReceiver(this.mKeyboardReceiver, this.mKeyboardIntentFilter);
            }
        }
    }

    public /* synthetic */ void lambda$onResume$10$RenameDialog() {
        if (getActivity() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService("input_method");
            inputMethodManager.hideSoftInputFromInputMethod(this.mInputView.getWindowToken(), 2);
//            if (inputMethodManager.semIsAccessoryKeyboard()) {
//                inputMethodManager.hideSoftInputFromWindow(this.mInputView.getWindowToken(), 0);
//            } else {
//                inputMethodManager.showSoftInput(this.mInputView, 0);
//            }
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        bundle.putBoolean(BUNDLE_NAME_CHANGED, this.mIsNameChanged);
        bundle.putInt(BUNDLE_ERROR_TYPE, this.mInputErrorType);
        super.onSaveInstanceState(bundle);
    }

    private void setListener(DialogFactory.DialogResultListener dialogResultListener) {
        this.mInterface = dialogResultListener;
    }

    public void onPause() {
        if (System.currentTimeMillis() - this.mReceiveTime < 150) {
            this.mIsKeyboardVisible = true;
        }
        if (!(this.mKeyboardReceiver == null || getActivity() == null)) {
            try {
                getActivity().getApplicationContext().unregisterReceiver(this.mKeyboardReceiver);
            } catch (IllegalArgumentException unused) {
                Log.m32w("RenameDialog", "onPause() - IllegalArgumentException");
            }
        }
        super.onPause();
    }

    /* access modifiers changed from: private */
    public void setInputErrorMessage(int i) {
        TextInputLayout textInputLayout = this.mInputLayout;
        if (textInputLayout == null) {
            return;
        }
        if (i == 0) {
            if (!textInputLayout.isErrorEnabled() || (this.mInputLayout.getError() != null && !this.mInputLayout.getError().equals(getActivity().getString(C0690R.string.max_char_reached_msg)))) {
                this.mInputLayout.setError(getActivity().getString(C0690R.string.max_char_reached_msg));
            }
            this.mInputErrorType = 0;
        } else if (i == 1) {
            if (!textInputLayout.isErrorEnabled() || (this.mInputLayout.getError() != null && !this.mInputLayout.getError().equals(getActivity().getString(C0690R.string.invalid_character)))) {
                this.mInputLayout.setError(getActivity().getString(C0690R.string.invalid_character));
            }
            this.mInputErrorType = 1;
        } else if (i == 2) {
            textInputLayout.setError(getActivity().getString(C0690R.string.file_name_already_exists));
            if (this.mDialog != null) {
                this.mOKButton.setEnabled(false);
                this.mTvOkButton.setAlpha(0.3f);
            }
            this.mInputErrorType = 2;
        }
    }

    private int getPositionById(int i) {
        Cursor categoryCursor = CursorProvider.getInstance().getCategoryCursor(true);
        int i2 = -1;
        if (categoryCursor == null) {
            Log.m22e("RenameDialog", "getPositionById : cursor null");
            return -1;
        }
        int count = categoryCursor.getCount();
        if (count > 0) {
            int i3 = 0;
            while (true) {
                if (i3 >= count) {
                    break;
                }
                categoryCursor.moveToPosition(i3);
                if (categoryCursor.getInt(categoryCursor.getColumnIndex(CategoryRepository.LabelColumn.f102ID)) == i) {
                    i2 = i3;
                    break;
                }
                i3++;
            }
        }
        if (!categoryCursor.isClosed()) {
            categoryCursor.close();
        }
        return i2;
    }

    private void listBinding() {
        String[] strArr = {CategoryRepository.LabelColumn.TITLE, CategoryRepository.LabelColumn.f102ID};
        int[] iArr = {C0690R.C0693id.label_title, C0690R.C0693id.number_category};
        if (CategoryListAdapter.getLabelCountArray().size() == 0) {
            CursorProvider.getInstance().getFileCountGroupByLabel(getContext());
        }
        this.mCursor = CursorProvider.getInstance().getCategoryCursor(true);
        Cursor cursor = this.mCursor;
        if (cursor == null) {
            Log.m22e("RenameDialog", "listBinding() : cursor null");
        } else if (this.mCategorySpinner == null) {
            Log.m29v("RenameDialog", "listBinding(): mCategory null");
        } else {
            try {
                cursor.moveToLast();
                this.mMaxPosition = this.mCursor.getInt(this.mCursor.getColumnIndex("POSITION"));
                if (this.mMaxPosition <= 3) {
                    this.mMaxPosition = 3;
                }
            } catch (IndexOutOfBoundsException e) {
                Log.m24e("RenameDialog", "IndexOutOfBoundsException", (Throwable) e);
            }
            this.mCategorySpinner.setAdapter((SpinnerAdapter) null);
            this.mListAdapter = new SpinnerSimpleCursorAdapter(getActivity(), C0690R.layout.listrow_spinner_list_category_item, this.mCursor, strArr, iArr);
            this.mCategorySpinner.setAdapter((SpinnerAdapter) this.mListAdapter);
        }
    }

    private String createNewNameCategory() {
        String string = getResources().getString(C0690R.string.category);
        String str = string + " " + String.format(Locale.getDefault(), "%d", new Object[]{1});
        int i = 1;
        while (DataRepository.getInstance().getCategoryRepository().isExitSameTitle(getContext(), str)) {
            i++;
            str = string + " " + String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(i)});
        }
        return str;
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        if (i != adapterView.getCount() - 1 || !this.mListAdapter.isShowAddCategory()) {
            this.mCurrentLabelPos = i;
            if (this.mPreviousName.trim().isEmpty() || (!this.mIsNewRecording && ((this.mPreviousName.equals(this.mOriginalTitle) || this.mInputErrorType == 2) && ((long) this.mLabelID) == this.mCategorySpinner.getSelectedItemId()))) {
                this.mOKButton.setEnabled(false);
                this.mOKButton.setFocusable(false);
                this.mTvOkButton.setAlpha(0.3f);
                this.mIsNameChanged = false;
                return;
            }
            this.mOKButton.setEnabled(true);
            this.mOKButton.setFocusable(true);
            this.mTvOkButton.setAlpha(1.0f);
            this.mIsNameChanged = true;
        } else if (getActivity() != null) {
            Bundle bundle = new Bundle();
            bundle.putString(DialogFactory.BUNDLE_NAME, createNewNameCategory());
            bundle.putInt(DialogFactory.BUNDLE_REQUEST_CODE, 13);
            bundle.putInt(DialogFactory.BUNDLE_TITLE_ID, C0690R.string.addnewlabel);
            bundle.putInt(DialogFactory.BUNDLE_POSITIVE_BTN_ID, C0690R.string.add_category_button);
            bundle.putInt(DialogFactory.BUNDLE_NEGATIVE_BTN_ID, 17039360);
            this.mCategorySpinner.setSelection(this.mCurrentLabelPos);
            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_save_rec), getResources().getString(C0690R.string.event_save_popup_add_category));
            DialogFactory.show(getFragmentManager(), DialogFactory.CATEGORY_RENAME, bundle, this);
        }
    }

    public void onDialogResult(DialogFragment dialogFragment, Bundle bundle) {
        if (bundle != null) {
            int i = bundle.getInt(DialogFactory.BUNDLE_REQUEST_CODE);
            int i2 = bundle.getInt("result_code");
            String string = bundle.getString(DialogFactory.BUNDLE_NAME);
            Log.m26i("RenameDialog", "onDialogResult - requestCode : " + i + " result : " + i2);
            if (i == 13) {
                DataRepository.getInstance().getCategoryRepository().loadLabelToMap();
                DataRepository.getInstance().getCategoryRepository().insertColumn(string, this.mMaxPosition + 1);
                if (this.mListAdapter != null && this.mCategorySpinner != null) {
                    this.mCursor = CursorProvider.getInstance().getCategoryCursor(true);
                    Cursor cursor = this.mCursor;
                    if (cursor != null) {
                        this.mListAdapter.changeCursor(cursor);
                        this.mMaxPosition++;
                        this.mCategorySpinner.setSelection(this.mCursor.getCount() - 1);
                    }
                }
            }
        }
    }

    public void onDestroy() {
        Log.m26i("RenameDialog", "onDestroy");
        this.mInterface = null;
        this.mCategorySpinner = null;
        this.mInputLayout = null;
        Cursor cursor = this.mCursor;
        if (cursor != null && !cursor.isClosed()) {
            this.mCursor.close();
            this.mCursor = null;
        }
        SpinnerSimpleCursorAdapter spinnerSimpleCursorAdapter = this.mListAdapter;
        if (spinnerSimpleCursorAdapter != null) {
            spinnerSimpleCursorAdapter.changeCursor((Cursor) null);
            this.mListAdapter = null;
        }
        VoiceNoteObservable voiceNoteObservable = this.mObservable;
        if (voiceNoteObservable != null) {
            voiceNoteObservable.deleteObserver(this);
            this.mObservable = null;
        }
        this.mDialog = null;
        super.onDestroy();
    }
}
