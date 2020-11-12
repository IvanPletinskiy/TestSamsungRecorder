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
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
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
import com.sec.android.app.voicenote.common.util.EmoticonUtils;
import com.sec.android.app.voicenote.p007ui.AbsDialogFragment;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.p007ui.view.WindowFocusLayout;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.service.Engine;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.BookmarkRenameDialog */
public class BookmarkRenameDialog extends AbsDialogFragment {
    private static final String BUNDLE_NAME_CHANGED = "name_change";
    private static final String DISABLE_EMOTICON_FLAG = "disableEmoticonInput=true";
    private static final String DISABLE_GIF_FLAG = "disableGifKeyboard=true";
    private static final String DISABLE_LIVE_MESSAGE = "disableLiveMessage=true";
    private static final String DISABLE_PREDICTION_FLAG = "inputType=PredictionOff";
    private static final String DISABLE_STICKER_FLAG = "disableSticker=true";
    private static final String DISABLE_SYMBOL_FLAG = "inputType=filename";
    private static final int MAX_LENGTH = 50;
    private static final String TAG = "BookmarkRenameDialog";
    private static final String VOICEINPUT_OFF = "disableVoiceInput=true";
    private AlertDialog mDialog;
    /* access modifiers changed from: private */
    public TextInputLayout mInputLayout = null;
    private DialogFactory.DialogResultListener mInterface = null;
    /* access modifiers changed from: private */
    public boolean mIsKeyboardVisible = true;
    /* access modifiers changed from: private */
    public boolean mIsNameChanged;
    private IntentFilter mKeyboardIntentFilter;
    private BroadcastReceiver mKeyboardReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            long unused = BookmarkRenameDialog.this.mReceiveTime = System.currentTimeMillis();
            boolean unused2 = BookmarkRenameDialog.this.mIsKeyboardVisible = intent.getBooleanExtra("AxT9IME.isVisibleWindow", true);
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

    /* renamed from: com.sec.android.app.voicenote.ui.dialog.BookmarkRenameDialog$InputErrorType */
    static class InputErrorType {
        static final int INVALID_CHARACTER = 1;
        static final int MAX_CHAR_REACHED_MSG = 0;

        InputErrorType() {
        }
    }

    public static BookmarkRenameDialog newInstance(Bundle bundle, DialogFactory.DialogResultListener dialogResultListener) {
        BookmarkRenameDialog bookmarkRenameDialog = new BookmarkRenameDialog();
        bookmarkRenameDialog.setArguments(bundle);
        bookmarkRenameDialog.setListener(dialogResultListener);
        return bookmarkRenameDialog;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        final FragmentActivity activity = getActivity();
        WindowFocusLayout windowFocusLayout = (WindowFocusLayout) activity.getLayoutInflater().inflate(C0690R.layout.dialog_bookmark_add_note, (ViewGroup) null);
        this.mInputLayout = (TextInputLayout) windowFocusLayout.findViewById(C0690R.C0693id.rename_input_wrapper);
        final EditText editText = (EditText) windowFocusLayout.findViewById(C0690R.C0693id.rename_input);
        this.mOriginalTitle = getArguments().getString(DialogFactory.BUNDLE_NAME, "");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        int i = getArguments().getInt(DialogFactory.BUNDLE_POSITIVE_BTN_ID, -1);
        builder.setTitle(getArguments().getInt(DialogFactory.BUNDLE_TITLE_ID, -1));
        builder.setView(windowFocusLayout);
        this.mInputLayout.setErrorEnabled(false);
        if (this.mOriginalTitle.isEmpty()) {
            editText.setText(getResources().getString(C0690R.string.bookmark_list_item_hint));
        } else {
            editText.setText(this.mOriginalTitle);
            String str = this.mOriginalTitle;
            if (str != null && str.length() > 50) {
                setInputErrorMessage(0);
            }
        }
        boolean z = true;
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
        sb.append(DISABLE_SYMBOL_FLAG);
        sb.append(";");
        sb.append(DISABLE_GIF_FLAG);
        sb.append(";");
        sb.append(DISABLE_LIVE_MESSAGE);
        sb.append(";");
        sb.append(DISABLE_STICKER_FLAG);
        if (Engine.getInstance().getRecorderState() == 2) {
            sb.append(";");
            sb.append(VOICEINPUT_OFF);
        }
        editText.setPrivateImeOptions(sb.toString());
        if (bundle == null || !bundle.getBoolean(BUNDLE_NAME_CHANGED, false)) {
            z = false;
        }
        this.mIsNameChanged = z;
        windowFocusLayout.setOnWindowFocusChangeListener(new WindowFocusLayout.OnWindowFocusChangeListener() {
            private final /* synthetic */ EditText f$1;

            {
                this.f$1 = editText;
            }

            public final void onWindowFocusChanged(boolean z) {
                BookmarkRenameDialog.this.lambda$onCreateDialog$1$BookmarkRenameDialog(this.f$1, z);
            }
        });
        builder.setPositiveButton(i, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                BookmarkRenameDialog.this.lambda$onCreateDialog$2$BookmarkRenameDialog(dialogInterface, i);
            }
        });
        builder.setNegativeButton(C0690R.string.cancel, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                BookmarkRenameDialog.this.lambda$onCreateDialog$3$BookmarkRenameDialog(dialogInterface, i);
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

            public synchronized void afterTextChanged(Editable editable) {
                if (create != null && create.isShowing()) {
                    String trim = editText.getText().toString().trim();
                    if (trim == null || !trim.equals(BookmarkRenameDialog.this.mOriginalTitle)) {
                        create.getButton(-1).setEnabled(true);
                        create.getButton(-1).setFocusable(true);
                        boolean unused = BookmarkRenameDialog.this.mIsNameChanged = true;
                    } else {
                        create.getButton(-1).setEnabled(false);
                        create.getButton(-1).setFocusable(false);
                        boolean unused2 = BookmarkRenameDialog.this.mIsNameChanged = false;
                    }
                    if (!BookmarkRenameDialog.this.mPreviousName.equals(editable.toString()) && BookmarkRenameDialog.this.mTotalLength <= 50) {
                        if (BookmarkRenameDialog.this.mInputLayout != null) {
                            BookmarkRenameDialog.this.mInputLayout.setErrorEnabled(false);
                        }
                        editText.setBackgroundTintList(BookmarkRenameDialog.this.getResources().getColorStateList(C0690R.C0691color.edit_background_tint_color, (Resources.Theme) null));
                    }
                }
                String unused3 = BookmarkRenameDialog.this.mPreviousName = editable.toString();
            }
        });
        editText.setOnKeyListener(new View.OnKeyListener() {
            public final boolean onKey(View view, int i, KeyEvent keyEvent) {
                return BookmarkRenameDialog.this.lambda$onCreateDialog$4$BookmarkRenameDialog(view, i, keyEvent);
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return BookmarkRenameDialog.this.lambda$onCreateDialog$5$BookmarkRenameDialog(textView, i, keyEvent);
            }
        });
        create.setOnShowListener(new DialogInterface.OnShowListener() {
            private final /* synthetic */ Activity f$1;
            private final /* synthetic */ AlertDialog f$2;
            private final /* synthetic */ EditText f$3;

            {
                this.f$1 = activity;
                this.f$2 = create;
                this.f$3 = editText;
            }

            public final void onShow(DialogInterface dialogInterface) {
                BookmarkRenameDialog.this.lambda$onCreateDialog$7$BookmarkRenameDialog(this.f$1, this.f$2, this.f$3, dialogInterface);
            }
        });
        return create;
    }

    public /* synthetic */ void lambda$onCreateDialog$1$BookmarkRenameDialog(final EditText editText, boolean z) {
        if (z) {
            editText.postDelayed(new Runnable() {
                private final /* synthetic */ EditText f$1;

                {
                    this.f$1 = editText;
                }

                public final void run() {
                    BookmarkRenameDialog.this.lambda$null$0$BookmarkRenameDialog(this.f$1);
                }
            }, 50);
        }
    }

    public /* synthetic */ void lambda$null$0$BookmarkRenameDialog(EditText editText) {
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

    public /* synthetic */ void lambda$onCreateDialog$2$BookmarkRenameDialog(DialogInterface dialogInterface, int i) {
        if (isVisible()) {
            dismissAllowingStateLoss();
        }
    }

    public /* synthetic */ void lambda$onCreateDialog$3$BookmarkRenameDialog(DialogInterface dialogInterface, int i) {
        Log.m29v(TAG, "Cancel");
        SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_bookmark_memo), getResources().getString(C0690R.string.event_bookmark_cancel));
        if (isVisible()) {
            dismissAllowingStateLoss();
        }
    }

    public /* synthetic */ boolean lambda$onCreateDialog$4$BookmarkRenameDialog(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() != 66) {
            return false;
        }
        executePositiveEvent();
        return false;
    }

    public /* synthetic */ boolean lambda$onCreateDialog$5$BookmarkRenameDialog(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        executePositiveEvent();
        return false;
    }

    public /* synthetic */ void lambda$onCreateDialog$7$BookmarkRenameDialog(@SuppressLint({"InflateParams"}) Activity activity, AlertDialog alertDialog, final EditText editText, DialogInterface dialogInterface) {
        Log.m26i(TAG, "onShow");
        ColorStateList colorStateList = activity.getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null);
        alertDialog.getButton(-1).setTextColor(colorStateList);
        alertDialog.getButton(-2).setTextColor(colorStateList);
        alertDialog.getButton(-1).setOnClickListener(new View.OnClickListener() {
            private final /* synthetic */ EditText f$1;

            {
                this.f$1 = editText;
            }

            public final void onClick(View view) {
                BookmarkRenameDialog.this.lambda$null$6$BookmarkRenameDialog(this.f$1, view);
            }
        });
    }

    public /* synthetic */ void lambda$null$6$BookmarkRenameDialog(EditText editText, View view) {
        Log.m26i(TAG, "onClick");
        SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_bookmark_memo), getResources().getString(C0690R.string.event_bookmark_done));
        String replaceAll = editText.getText().toString().trim().replaceAll("\n", " ");
        Bundle arguments = getArguments();
        if (this.mInterface != null) {
            arguments.putString(DialogFactory.BUNDLE_NAME, replaceAll);
            this.mInterface.onDialogResult(this, arguments);
        }
        dismiss();
    }

    public void onStart() {
        super.onStart();
        if (this.mIsNameChanged) {
            this.mDialog.getButton(-1).setEnabled(true);
        } else if (!this.mOriginalTitle.isEmpty()) {
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
                return BookmarkRenameDialog.this.lambda$getNameFilter$8$BookmarkRenameDialog(charSequence, i, i2, spanned, i3, i4);
            }
        };
        if (!z || 50 >= i) {
            i = 50;
        }
        inputFilterArr[1] = new InputFilter.LengthFilter(i) {
            public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
                CharSequence filter = super.filter(charSequence, i, i2, spanned, i3, i4);
                int unused = BookmarkRenameDialog.this.mTotalLength = (charSequence.subSequence(i, i2).toString().length() + spanned.length()) - spanned.subSequence(i3, i4).toString().length();
                if (filter != null && charSequence.length() > 0) {
                    BookmarkRenameDialog.this.setInputErrorMessage(0);
                }
                return filter;
            }
        };
        return inputFilterArr;
    }

    public /* synthetic */ CharSequence lambda$getNameFilter$8$BookmarkRenameDialog(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
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

    private void executePositiveEvent() {
        if (this.mDialog.getButton(-1).isEnabled()) {
            this.mDialog.getButton(-1).callOnClick();
        }
    }

    public void onResume() {
        final EditText editText;
        super.onResume();
        if (getShowsDialog() && getDialog() != null && (editText = (EditText) getDialog().findViewById(C0690R.C0693id.rename_input)) != null) {
            editText.requestFocus();
            if (this.mIsKeyboardVisible) {
                editText.postDelayed(new Runnable() {
                    private final /* synthetic */ EditText f$1;

                    {
                        this.f$1 = editText;
                    }

                    public final void run() {
                        BookmarkRenameDialog.this.lambda$onResume$9$BookmarkRenameDialog(this.f$1);
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

    public /* synthetic */ void lambda$onResume$9$BookmarkRenameDialog(EditText editText) {
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
        bundle.putBoolean(BUNDLE_NAME_CHANGED, this.mIsNameChanged);
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
            } catch (IllegalArgumentException e) {
                Log.m34w(TAG, "onPause() - IllegalArgumentException", (Throwable) e);
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
            if (i == 1) {
                if (!textInputLayout.isErrorEnabled() || (this.mInputLayout.getError() != null && !this.mInputLayout.getError().equals(getActivity().getString(C0690R.string.invalid_character)))) {
                    this.mInputLayout.setError(getActivity().getString(C0690R.string.invalid_character));
                }
            }
        } else if (!textInputLayout.isErrorEnabled() || (this.mInputLayout.getError() != null && !this.mInputLayout.getError().equals(getActivity().getString(C0690R.string.max_char_reached_msg)))) {
            this.mInputLayout.setError(getActivity().getString(C0690R.string.max_char_reached_msg));
        }
    }
}
