package com.sec.android.app.voicenote.p007ui.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DataRepository;
import com.sec.android.app.voicenote.common.util.EmoticonUtils;
import com.sec.android.app.voicenote.p007ui.AbsDialogFragment;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.p007ui.view.WindowFocusLayout;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.CategoryRenameDialog */
public class CategoryRenameDialog extends AbsDialogFragment {
    private static final String BUNDLE_NAME_ALREADY_EXIST = "name_exist";
    private static final String BUNDLE_NAME_CHANGED = "name_change";
    private static final String DISABLE_EMOTICON_FLAG = "disableEmoticonInput=true";
    private static final String DISABLE_GIF_FLAG = "disableGifKeyboard=true";
    private static final String DISABLE_LIVE_MESSAGE = "disableLiveMessage=true";
    private static final String DISABLE_PREDICTION_FLAG = "inputType=PredictionOff";
    private static final String DISABLE_STICKER_FLAG = "disableSticker=true";
    private static final String DISABLE_SYMBLE_FLAG = "inputType=filename";
    private static final int MAX_LENGTH = 50;
    private static final String NO_MICROPHONE_KEY = "noMicrophoneKey";
    private static final String RULE_3011 = "VoiceRecorder_3011";
    private static final String RULE_3013 = "VoiceRecorder_3013";
    private static final String RULE_3031 = "VoiceRecorder_3031";
    private static final String TAG = "CategoryRenameDialog";
    private static final String VOICEINPUT_OFF = "disableVoiceInput=true";
    /* access modifiers changed from: private */
    public boolean bNameChanged;
    private AlertDialog mDialog;
    /* access modifiers changed from: private */
    public TextInputLayout mInputLayout = null;
    private DialogFactory.DialogResultListener mInterface = null;
    /* access modifiers changed from: private */
    public boolean mIsAddCategory = false;
    private boolean mIsFileNameExists;
    /* access modifiers changed from: private */
    public boolean mIsKeyboardVisible = true;
    private IntentFilter mKeyboardIntentFilter;
    private BroadcastReceiver mKeyboardReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            long unused = CategoryRenameDialog.this.mReceiveTime = System.currentTimeMillis();
            boolean unused2 = CategoryRenameDialog.this.mIsKeyboardVisible = intent.getBooleanExtra("AxT9IME.isVisibleWindow", true);
        }
    };
    /* access modifiers changed from: private */
    public String mOriginalTitle = null;
    /* access modifiers changed from: private */
    public String mPreviousName = null;
    /* access modifiers changed from: private */
    public long mReceiveTime;
    /* access modifiers changed from: private */
    public int mTotalLength;

    /* renamed from: com.sec.android.app.voicenote.ui.dialog.CategoryRenameDialog$InputErrorType */
    static class InputErrorType {
        static final int FILE_NAME_ALREADY_EXISTS = 2;
        static final int INVALID_CHARACTER = 1;
        static final int MAX_CHAR_REACHED_MSG = 0;

        InputErrorType() {
        }
    }

    public static CategoryRenameDialog newInstance(Bundle bundle, DialogFactory.DialogResultListener dialogResultListener) {
        CategoryRenameDialog categoryRenameDialog = new CategoryRenameDialog();
        categoryRenameDialog.setArguments(bundle);
        categoryRenameDialog.setDialogResultListener(dialogResultListener);
        return categoryRenameDialog;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        final FragmentActivity activity = getActivity();
        WindowFocusLayout windowFocusLayout = (WindowFocusLayout) activity.getLayoutInflater().inflate(C0690R.layout.dialog_category_rename, (ViewGroup) null);
        this.mInputLayout = (TextInputLayout) windowFocusLayout.findViewById(C0690R.C0693id.rename_input_wrapper);
        final EditText editText = (EditText) windowFocusLayout.findViewById(C0690R.C0693id.rename_input);
        this.mOriginalTitle = getArguments().getString(DialogFactory.BUNDLE_NAME, (String) null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        int i = getArguments().getInt(DialogFactory.BUNDLE_POSITIVE_BTN_ID, -1);
        int i2 = getArguments().getInt(DialogFactory.BUNDLE_TITLE_ID, -1);
        if (i != -1 && getString(i).equals(getString(C0690R.string.add_category_button))) {
            this.mIsAddCategory = true;
        }
        builder.setTitle(i2);
        builder.setView(windowFocusLayout);
        this.mInputLayout.setErrorEnabled(false);
        editText.setText(this.mOriginalTitle);
        String str = this.mOriginalTitle;
        if (str != null && str.length() > 50) {
            setInputErrorMessage(0);
        }
        editText.setSelected(true);
        editText.setFocusable(true);
        String str2 = this.mOriginalTitle;
        editText.setFilters(getNameFilter(true, str2 != null ? str2.length() : 0));
        editText.selectAll();
        editText.requestFocus();
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
        if (VoiceNoteApplication.getScene() == 8) {
            sb.append(";");
            sb.append(VOICEINPUT_OFF);
            sb.append(";");
            sb.append(NO_MICROPHONE_KEY);
        }
        editText.setPrivateImeOptions(sb.toString());
        if (bundle != null) {
            this.bNameChanged = bundle.getBoolean(BUNDLE_NAME_CHANGED, false);
            this.mIsFileNameExists = bundle.getBoolean(BUNDLE_NAME_ALREADY_EXIST, false);
        }
        windowFocusLayout.setOnWindowFocusChangeListener(new WindowFocusLayout.OnWindowFocusChangeListener() {
            private final /* synthetic */ EditText f$0;
            private final /* synthetic */ Activity f$1;

            {
                this.f$0 = editText;
                this.f$1 = activity;
            }

            public final void onWindowFocusChanged(boolean z) {
                CategoryRenameDialog.lambda$onCreateDialog$1(this.f$0, this.f$1, z);
            }
        });
        builder.setPositiveButton(i, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                CategoryRenameDialog.this.lambda$onCreateDialog$2$CategoryRenameDialog(dialogInterface, i);
            }
        });
        builder.setNegativeButton(C0690R.string.cancel, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                CategoryRenameDialog.this.lambda$onCreateDialog$3$CategoryRenameDialog(dialogInterface, i);
            }
        });
        final AlertDialog create = builder.create();
        this.mDialog = create;
        if (this.mPreviousName == null) {
            this.mPreviousName = editText.getText().toString();
        }
        editText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            /* JADX WARNING: Removed duplicated region for block: B:19:0x0082  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public synchronized void afterTextChanged(android.text.Editable r5) {
                /*
                    r4 = this;
                    monitor-enter(r4)
                    android.app.AlertDialog r0 = r11     // Catch:{ all -> 0x00a9 }
                    if (r0 == 0) goto L_0x009e
                    android.widget.EditText r0 = r3     // Catch:{ all -> 0x00a9 }
                    android.text.Editable r0 = r0.getText()     // Catch:{ all -> 0x00a9 }
                    java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x00a9 }
                    java.lang.String r0 = r0.trim()     // Catch:{ all -> 0x00a9 }
                    boolean r1 = r0.isEmpty()     // Catch:{ all -> 0x00a9 }
                    r2 = 0
                    r3 = -1
                    if (r1 != 0) goto L_0x0049
                    com.sec.android.app.voicenote.ui.dialog.CategoryRenameDialog r1 = com.sec.android.app.voicenote.p007ui.dialog.CategoryRenameDialog.this     // Catch:{ all -> 0x00a9 }
                    boolean r1 = r1.mIsAddCategory     // Catch:{ all -> 0x00a9 }
                    if (r1 != 0) goto L_0x0030
                    com.sec.android.app.voicenote.ui.dialog.CategoryRenameDialog r1 = com.sec.android.app.voicenote.p007ui.dialog.CategoryRenameDialog.this     // Catch:{ all -> 0x00a9 }
                    java.lang.String r1 = r1.mOriginalTitle     // Catch:{ all -> 0x00a9 }
                    boolean r0 = r0.equals(r1)     // Catch:{ all -> 0x00a9 }
                    if (r0 == 0) goto L_0x0030
                    goto L_0x0049
                L_0x0030:
                    android.app.AlertDialog r0 = r11     // Catch:{ all -> 0x00a9 }
                    android.widget.Button r0 = r0.getButton(r3)     // Catch:{ all -> 0x00a9 }
                    r1 = 1
                    r0.setEnabled(r1)     // Catch:{ all -> 0x00a9 }
                    android.app.AlertDialog r0 = r11     // Catch:{ all -> 0x00a9 }
                    android.widget.Button r0 = r0.getButton(r3)     // Catch:{ all -> 0x00a9 }
                    r0.setFocusable(r1)     // Catch:{ all -> 0x00a9 }
                    com.sec.android.app.voicenote.ui.dialog.CategoryRenameDialog r0 = com.sec.android.app.voicenote.p007ui.dialog.CategoryRenameDialog.this     // Catch:{ all -> 0x00a9 }
                    boolean unused = r0.bNameChanged = r1     // Catch:{ all -> 0x00a9 }
                    goto L_0x0060
                L_0x0049:
                    android.app.AlertDialog r0 = r11     // Catch:{ all -> 0x00a9 }
                    android.widget.Button r0 = r0.getButton(r3)     // Catch:{ all -> 0x00a9 }
                    r0.setEnabled(r2)     // Catch:{ all -> 0x00a9 }
                    android.app.AlertDialog r0 = r11     // Catch:{ all -> 0x00a9 }
                    android.widget.Button r0 = r0.getButton(r3)     // Catch:{ all -> 0x00a9 }
                    r0.setFocusable(r2)     // Catch:{ all -> 0x00a9 }
                    com.sec.android.app.voicenote.ui.dialog.CategoryRenameDialog r0 = com.sec.android.app.voicenote.p007ui.dialog.CategoryRenameDialog.this     // Catch:{ all -> 0x00a9 }
                    boolean unused = r0.bNameChanged = r2     // Catch:{ all -> 0x00a9 }
                L_0x0060:
                    com.sec.android.app.voicenote.ui.dialog.CategoryRenameDialog r0 = com.sec.android.app.voicenote.p007ui.dialog.CategoryRenameDialog.this     // Catch:{ all -> 0x00a9 }
                    java.lang.String r0 = r0.mPreviousName     // Catch:{ all -> 0x00a9 }
                    java.lang.String r1 = r5.toString()     // Catch:{ all -> 0x00a9 }
                    boolean r0 = r0.equals(r1)     // Catch:{ all -> 0x00a9 }
                    if (r0 != 0) goto L_0x009e
                    com.sec.android.app.voicenote.ui.dialog.CategoryRenameDialog r0 = com.sec.android.app.voicenote.p007ui.dialog.CategoryRenameDialog.this     // Catch:{ all -> 0x00a9 }
                    int r0 = r0.mTotalLength     // Catch:{ all -> 0x00a9 }
                    r1 = 50
                    if (r0 > r1) goto L_0x009e
                    com.sec.android.app.voicenote.ui.dialog.CategoryRenameDialog r0 = com.sec.android.app.voicenote.p007ui.dialog.CategoryRenameDialog.this     // Catch:{ all -> 0x00a9 }
                    com.google.android.material.textfield.TextInputLayout r0 = r0.mInputLayout     // Catch:{ all -> 0x00a9 }
                    if (r0 == 0) goto L_0x008b
                    com.sec.android.app.voicenote.ui.dialog.CategoryRenameDialog r0 = com.sec.android.app.voicenote.p007ui.dialog.CategoryRenameDialog.this     // Catch:{ all -> 0x00a9 }
                    com.google.android.material.textfield.TextInputLayout r0 = r0.mInputLayout     // Catch:{ all -> 0x00a9 }
                    r0.setErrorEnabled(r2)     // Catch:{ all -> 0x00a9 }
                L_0x008b:
                    android.widget.EditText r0 = r3     // Catch:{ all -> 0x00a9 }
                    com.sec.android.app.voicenote.ui.dialog.CategoryRenameDialog r1 = com.sec.android.app.voicenote.p007ui.dialog.CategoryRenameDialog.this     // Catch:{ all -> 0x00a9 }
                    android.content.res.Resources r1 = r1.getResources()     // Catch:{ all -> 0x00a9 }
                    r2 = 2131099747(0x7f060063, float:1.7811856E38)
                    r3 = 0
                    android.content.res.ColorStateList r1 = r1.getColorStateList(r2, r3)     // Catch:{ all -> 0x00a9 }
                    r0.setBackgroundTintList(r1)     // Catch:{ all -> 0x00a9 }
                L_0x009e:
                    com.sec.android.app.voicenote.ui.dialog.CategoryRenameDialog r0 = com.sec.android.app.voicenote.p007ui.dialog.CategoryRenameDialog.this     // Catch:{ all -> 0x00a9 }
                    java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x00a9 }
                    java.lang.String unused = r0.mPreviousName = r5     // Catch:{ all -> 0x00a9 }
                    monitor-exit(r4)
                    return
                L_0x00a9:
                    r5 = move-exception
                    monitor-exit(r4)
                    throw r5
                */
                throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.dialog.CategoryRenameDialog.C08821.afterTextChanged(android.text.Editable):void");
            }
        });
        editText.setOnKeyListener(new View.OnKeyListener() {
            private final /* synthetic */ EditText f$1;

            {
                this.f$1 = editText;
            }

            public final boolean onKey(View view, int i, KeyEvent keyEvent) {
                return CategoryRenameDialog.this.lambda$onCreateDialog$4$CategoryRenameDialog(this.f$1, view, i, keyEvent);
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            private final /* synthetic */ EditText f$1;

            {
                this.f$1 = editText;
            }

            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return CategoryRenameDialog.this.lambda$onCreateDialog$5$CategoryRenameDialog(this.f$1, textView, i, keyEvent);
            }
        });
        create.setOnShowListener(new DialogInterface.OnShowListener() {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ Activity f$2;
            private final /* synthetic */ EditText f$3;

            {
                this.f$1 = create;
                this.f$2 = activity;
                this.f$3 = editText;
            }

            public final void onShow(DialogInterface dialogInterface) {
                CategoryRenameDialog.this.lambda$onCreateDialog$7$CategoryRenameDialog(this.f$1, this.f$2, this.f$3, dialogInterface);
            }
        });
        return create;
    }

    static /* synthetic */ void lambda$onCreateDialog$1(final EditText editText, @SuppressLint({"InflateParams"}) final Activity activity, boolean z) {
        if (z) {
            editText.postDelayed(new Runnable() {
                private final /* synthetic */ Activity f$0;
                private final /* synthetic */ EditText f$1;

                {
                    this.f$0 = activity;
                    this.f$1 = editText;
                }

                public final void run() {
                    CategoryRenameDialog.lambda$null$0(this.f$0, this.f$1);
                }
            }, 50);
        }
    }

    static /* synthetic */ void lambda$null$0(@SuppressLint({"InflateParams"}) Activity activity, EditText editText) {
        if (activity != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService("input_method");
            inputMethodManager.hideSoftInputFromInputMethod(editText.getWindowToken(), 2);
//            if (inputMethodManager.semIsAccessoryKeyboard()) {
//                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
//            } else {
//                inputMethodManager.showSoftInput(editText, 0);
//            }
        }
    }

    public /* synthetic */ void lambda$onCreateDialog$2$CategoryRenameDialog(DialogInterface dialogInterface, int i) {
        if (isVisible()) {
            dismissAllowingStateLoss();
        }
    }

    public /* synthetic */ void lambda$onCreateDialog$3$CategoryRenameDialog(DialogInterface dialogInterface, int i) {
        Log.m29v("CategoryRenameDialog", "Cancel");
        SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_add_category), getResources().getString(C0690R.string.event_add_category_cancel));
        if (isVisible()) {
            dismissAllowingStateLoss();
        }
    }

    public /* synthetic */ boolean lambda$onCreateDialog$4$CategoryRenameDialog(EditText editText, View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() != 66) {
            return false;
        }
        excutePositiveEvent(editText);
        return false;
    }

    public /* synthetic */ boolean lambda$onCreateDialog$5$CategoryRenameDialog(EditText editText, TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        excutePositiveEvent(editText);
        return false;
    }

    public /* synthetic */ void lambda$onCreateDialog$7$CategoryRenameDialog(AlertDialog alertDialog, @SuppressLint({"InflateParams"}) Activity activity, final EditText editText, DialogInterface dialogInterface) {
        Log.m26i("CategoryRenameDialog", "onShow");
        if (isAdded()) {
            alertDialog.getButton(-1).setTextColor(activity.getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
            alertDialog.getButton(-2).setTextColor(activity.getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
        }
        alertDialog.getButton(-1).setOnClickListener(new View.OnClickListener() {
            private final /* synthetic */ EditText f$1;

            {
                this.f$1 = editText;
            }

            public final void onClick(View view) {
                CategoryRenameDialog.this.lambda$null$6$CategoryRenameDialog(this.f$1, view);
            }
        });
    }

    public /* synthetic */ void lambda$null$6$CategoryRenameDialog(EditText editText, View view) {
        String str;
        Log.m26i("CategoryRenameDialog", "onClick");
        String replaceAll = editText.getText().toString().trim().replaceAll("\n", " ");
        Bundle arguments = getArguments();
        int i = arguments.getInt(DialogFactory.BUNDLE_REQUEST_CODE, -1);
        if ((i == 1 && (str = this.mOriginalTitle) != null && !str.equals(replaceAll)) || i == 13) {
            if (DataRepository.getInstance().getCategoryRepository().isExitSameTitle(getContext(), replaceAll)) {
                setInputErrorMessage(2);
                return;
            }
            this.mIsFileNameExists = false;
            if (this.mInterface != null) {
                arguments.putString(DialogFactory.BUNDLE_NAME, replaceAll);
                arguments.putInt("result_code", i);
                this.mInterface.onDialogResult(this, arguments);
            }
            if (this.bNameChanged) {
                SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_add_category), getResources().getString(C0690R.string.event_add_category_save), "2");
            } else {
                SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_add_category), getResources().getString(C0690R.string.event_add_category_save), "1");
            }
            dismiss();
        }
    }

    public void onStart() {
        super.onStart();
        int i = getArguments().getInt(DialogFactory.BUNDLE_REQUEST_CODE, -1);
        if (!this.bNameChanged && i == 1) {
            this.mDialog.getButton(-1).setEnabled(false);
        }
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.mInterface = null;
        super.onDismiss(dialogInterface);
    }

    public InputFilter[] getNameFilter(boolean z, int i) {
        InputFilter[] inputFilterArr = new InputFilter[2];
        inputFilterArr[0] = new InputFilter() {
            public final CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
                return CategoryRenameDialog.this.lambda$getNameFilter$8$CategoryRenameDialog(charSequence, i, i2, spanned, i3, i4);
            }
        };
        if (!z || 50 >= i) {
            i = 50;
        }
        inputFilterArr[1] = new InputFilter.LengthFilter(i) {
            public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
                CharSequence filter = super.filter(charSequence, i, i2, spanned, i3, i4);
                int unused = CategoryRenameDialog.this.mTotalLength = (charSequence.subSequence(i, i2).toString().length() + spanned.length()) - spanned.subSequence(i3, i4).toString().length();
                if (filter != null && charSequence.length() > 0) {
                    CategoryRenameDialog.this.setInputErrorMessage(0);
                }
                return filter;
            }
        };
        return inputFilterArr;
    }

    public /* synthetic */ CharSequence lambda$getNameFilter$8$CategoryRenameDialog(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
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
        char charAt;
        boolean z = false;
        for (String str2 : new String[]{"\\", "/", ":", "*", "?", "\"", "<", ">", "|"}) {
            if (str.contains(str2) || ((charAt = str2.charAt(0)) >= '!' && charAt < '~' && charAt != '?' && str.indexOf((char) (charAt + 65248)) >= 0)) {
                z = true;
            }
        }
        return z;
    }

    private void excutePositiveEvent(EditText editText) {
        if (this.mDialog.getButton(-1).isEnabled()) {
            this.mDialog.getButton(-1).callOnClick();
            return;
        }
        String replaceAll = editText.getText().toString().trim().replaceAll("\n", " ");
        int i = getArguments().getInt(DialogFactory.BUNDLE_REQUEST_CODE, -1);
        if (((i == 1 && this.mOriginalTitle != null) || i == 13) && DataRepository.getInstance().getCategoryRepository().isExitSameTitle(getContext(), replaceAll)) {
            setInputErrorMessage(2);
        }
    }

    public void onResume() {
        final EditText editText;
        super.onResume();
        if (getShowsDialog() && getDialog() != null && (editText = (EditText) getDialog().findViewById(C0690R.C0693id.rename_input)) != null) {
            if (this.mIsKeyboardVisible) {
                editText.postDelayed(new Runnable() {
                    private final /* synthetic */ EditText f$1;

                    {
                        this.f$1 = editText;
                    }

                    public final void run() {
                        CategoryRenameDialog.this.lambda$onResume$9$CategoryRenameDialog(this.f$1);
                    }
                }, 250);
                editText.requestFocus();
            }
            if (this.mKeyboardIntentFilter == null) {
                this.mKeyboardIntentFilter = new IntentFilter();
                this.mKeyboardIntentFilter.addAction("ResponseAxT9Info");
            }
            if (!(this.mKeyboardReceiver == null || getActivity() == null)) {
                getActivity().getApplicationContext().registerReceiver(this.mKeyboardReceiver, this.mKeyboardIntentFilter);
            }
            if (this.mIsFileNameExists) {
                setInputErrorMessage(2);
            }
        }
    }

    public /* synthetic */ void lambda$onResume$9$CategoryRenameDialog(EditText editText) {
        if (getActivity() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService("input_method");
            inputMethodManager.hideSoftInputFromInputMethod(editText.getWindowToken(), 2);
//            if (inputMethodManager.semIsAccessoryKeyboard()) {
//                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
//            } else {
//                inputMethodManager.showSoftInput(editText, 0);
//            }
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        bundle.putBoolean(BUNDLE_NAME_CHANGED, this.bNameChanged);
        bundle.putBoolean(BUNDLE_NAME_ALREADY_EXIST, this.mIsFileNameExists);
        super.onSaveInstanceState(bundle);
    }

    public void setDialogResultListener(DialogFactory.DialogResultListener dialogResultListener) {
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
                Log.m32w("CategoryRenameDialog", "onPause() - IllegalArgumentException");
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
        if (i != 0) {
            if (i != 1) {
                if (i == 2) {
                    textInputLayout.setError(getActivity().getString(C0690R.string.category_name_already_exists));
                    AlertDialog alertDialog = this.mDialog;
                    if (alertDialog != null) {
                        alertDialog.getButton(-1).setEnabled(false);
                        this.mIsFileNameExists = true;
                    }
                }
            } else if (!textInputLayout.isErrorEnabled() || (this.mInputLayout.getError() != null && !this.mInputLayout.getError().equals(getActivity().getString(C0690R.string.invalid_character)))) {
                this.mInputLayout.setError(getActivity().getString(C0690R.string.invalid_character));
            }
        } else if (!textInputLayout.isErrorEnabled() || (this.mInputLayout.getError() != null && !this.mInputLayout.getError().equals(getActivity().getString(C0690R.string.max_char_reached_msg)))) {
            this.mInputLayout.setError(getActivity().getString(C0690R.string.max_char_reached_msg));
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
