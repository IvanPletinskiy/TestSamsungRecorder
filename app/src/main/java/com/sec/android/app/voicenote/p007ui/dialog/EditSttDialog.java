package com.sec.android.app.voicenote.p007ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.p007ui.AbsDialogFragment;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.p007ui.view.WindowFocusLayout;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.ViewProvider;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.EditSttDialog */
public class EditSttDialog extends AbsDialogFragment implements TextView.OnEditorActionListener {
    private static final int MAX_LENGTH = 50;
    private static final String TAG = "EditSttDialog";
    /* access modifiers changed from: private */
    public EditText mEditText;
    private DialogFactory.DialogResultListener mInterface = null;
    private Runnable mShowSIP = new Runnable() {
        public void run() {
            FragmentActivity activity = EditSttDialog.this.getActivity();
            if (activity != null) {
                ((InputMethodManager) activity.getSystemService("input_method")).showSoftInput(EditSttDialog.this.mEditText, 1);
            } else {
                Log.m19d("EditSttDialog", "onResume() activity is null");
            }
        }
    };

    /* renamed from: com.sec.android.app.voicenote.ui.dialog.EditSttDialog$EditSttResult */
    public static class EditSttResult {
        public static final int DISMISS = 1;
        public static final int EDITED = 0;
    }

    public static EditSttDialog newInstance(Bundle bundle, DialogFactory.DialogResultListener dialogResultListener) {
        EditSttDialog editSttDialog = new EditSttDialog();
        editSttDialog.setArguments(bundle);
        editSttDialog.setListener(dialogResultListener);
        return editSttDialog;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        WindowFocusLayout windowFocusLayout = (WindowFocusLayout) getActivity().getLayoutInflater().inflate(C0690R.layout.edit_stt, (ViewGroup) null);
        windowFocusLayout.setOnWindowFocusChangeListener(new WindowFocusLayout.OnWindowFocusChangeListener() {
            public final void onWindowFocusChanged(boolean z) {
                EditSttDialog.this.lambda$onCreateDialog$0$EditSttDialog(z);
            }
        });
        this.mEditText = (EditText) windowFocusLayout.findViewById(C0690R.C0693id.edit_stt_field);
        ViewProvider.setMaxFontSize(getContext(), this.mEditText);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(windowFocusLayout);
        String string = getArguments().getString(DialogFactory.BUNDLE_WORD);
        this.mEditText.setText(string);
        this.mEditText.selectAll();
        this.mEditText.setInputType(16384);
        this.mEditText.setPrivateImeOptions("disableVoiceInput=true");
        this.mEditText.setFilters(getNameFilter(getActivity(), true, string.length()));
        this.mEditText.setOnEditorActionListener(this);
        ((ImageButton) windowFocusLayout.findViewById(C0690R.C0693id.edit_stt_delete)).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                EditSttDialog.this.lambda$onCreateDialog$1$EditSttDialog(view);
            }
        });
        AlertDialog create = builder.create();
        create.requestWindowFeature(1);
        create.getWindow().setGravity(80);
        create.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        return create;
    }

    public /* synthetic */ void lambda$onCreateDialog$0$EditSttDialog(boolean z) {
        if (z) {
            this.mEditText.postDelayed(this.mShowSIP, 50);
        }
    }

    public /* synthetic */ void lambda$onCreateDialog$1$EditSttDialog(View view) {
        if (this.mEditText.getText().length() > 0) {
            this.mEditText.setText((CharSequence) null);
        }
    }

    public void onResume() {
        super.onResume();
        showSoftInputKeyboard();
    }

    public void onPause() {
        super.onPause();
    }

    public void onDismiss(DialogInterface dialogInterface) {
        if (!(this.mInterface == null || getArguments() == null)) {
            FragmentActivity activity = getActivity();
            if (!(activity == null || activity.getWindow() == null)) {
                activity.getWindow().setSoftInputMode(48);
            }
            Bundle arguments = getArguments();
            arguments.putInt("result_code", 1);
            this.mInterface.onDialogResult(this, arguments);
            this.mInterface = null;
        }
        super.onDismiss(dialogInterface);
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (6 == i || (keyEvent != null && keyEvent.getKeyCode() == 66)) {
            if (!(this.mInterface == null || getArguments() == null)) {
                Bundle arguments = getArguments();
                arguments.putInt("result_code", 0);
                arguments.putString(DialogFactory.BUNDLE_WORD, this.mEditText.getText().toString());
                this.mInterface.onDialogResult(this, arguments);
            }
            dismissEditSttDialog();
        }
        return false;
    }

    private void showSoftInputKeyboard() {
        this.mEditText.postDelayed(new Runnable() {
            public final void run() {
                EditSttDialog.this.lambda$showSoftInputKeyboard$2$EditSttDialog();
            }
        }, 50);
    }

    public /* synthetic */ void lambda$showSoftInputKeyboard$2$EditSttDialog() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.getWindow().setSoftInputMode(32);
            ((InputMethodManager) activity.getSystemService("input_method")).showSoftInput(this.mEditText, 1);
            this.mEditText.requestFocus();
            return;
        }
        Log.m19d("EditSttDialog", "try to show SIP but activity is null");
    }

    public static InputFilter[] getNameFilter(Activity activity, boolean z, int i) {
        InputFilter[] inputFilterArr = new InputFilter[1];
        final Toast makeText = Toast.makeText(activity, C0690R.string.max_char_reached_msg, 0);
        if (!z || 50 >= i) {
            i = 50;
        }
        inputFilterArr[0] = new InputFilter.LengthFilter(i) {
            public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
                Toast toast;
                CharSequence filter = super.filter(charSequence, i, i2, spanned, i3, i4);
                if (filter != null && charSequence.length() > 0 && (toast = makeText) != null && !toast.getView().isShown()) {
                    makeText.show();
                }
                return filter;
            }
        };
        return inputFilterArr;
    }

    private void setListener(DialogFactory.DialogResultListener dialogResultListener) {
        this.mInterface = dialogResultListener;
    }

    private void dismissEditSttDialog() {
        DialogFactory.clearDialogByTag(getFragmentManager(), "EditSttDialog");
    }
}
