package com.sec.android.app.voicenote.p007ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.p007ui.AbsDialogFragment;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.TranslationCancelDialog */
public class TranslationCancelDialog extends AbsDialogFragment {
    private static final String TAG = "TranslationCancelDialog";
    private AlertDialog mDialog;
    private String mName;

    public static TranslationCancelDialog newInstance(Bundle bundle) {
        TranslationCancelDialog translationCancelDialog = new TranslationCancelDialog();
        translationCancelDialog.setArguments(bundle);
        return translationCancelDialog;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        int i;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String string = getArguments().getString(DialogFactory.BUNDLE_PATH, (String) null);
        if (string != null) {
            int lastIndexOf = string.lastIndexOf(47);
            int lastIndexOf2 = string.lastIndexOf(46);
            if (lastIndexOf >= 0 && (i = lastIndexOf + 1) < lastIndexOf2 && lastIndexOf2 < string.length()) {
                this.mName = string.substring(i, lastIndexOf2);
            }
        } else {
            Log.m22e("TranslationCancelDialog", "path null");
            dismiss();
        }
        builder.setMessage(C0690R.string.stt_translation_cancel_popup_title);
        builder.setPositiveButton(C0690R.string.save, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                TranslationCancelDialog.this.lambda$onCreateDialog$0$TranslationCancelDialog(dialogInterface, i);
            }
        });
        builder.setNegativeButton(C0690R.string.discard, $$Lambda$TranslationCancelDialog$IKSSe09a0DPCoWMYGdV09L_ho.INSTANCE);
        builder.setNeutralButton(C0690R.string.cancel, $$Lambda$TranslationCancelDialog$cPWJEfoa7ZcgFl8efxUTNOYG0.INSTANCE);
        this.mDialog = builder.create();
        this.mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public final void onShow(DialogInterface dialogInterface) {
                TranslationCancelDialog.this.lambda$onCreateDialog$3$TranslationCancelDialog(dialogInterface);
            }
        });
        return this.mDialog;
    }

    public /* synthetic */ void lambda$onCreateDialog$0$TranslationCancelDialog(DialogInterface dialogInterface, int i) {
        Log.m19d("TranslationCancelDialog", "onClick Save");
        String lowerCase = getString(C0690R.string.prefix_voicememo).toLowerCase();
        Engine.getInstance().setUserSettingName(this.mName + "_" + lowerCase);
        Engine.getInstance().setCategoryID(2);
        Engine.getInstance().stopTranslation(false);
    }

    static /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, int i) {
        Log.m19d("TranslationCancelDialog", "onClick Discard");
        Engine.getInstance().cancelTranslation(false);
        VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.TRANSLATION_CANCEL));
        VoiceNoteObservable.getInstance().notifyObservers(17);
    }

    public /* synthetic */ void lambda$onCreateDialog$3$TranslationCancelDialog(DialogInterface dialogInterface) {
        if (this.mDialog != null && getActivity() != null) {
            this.mDialog.getButton(-1).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
            this.mDialog.getButton(-2).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
            this.mDialog.getButton(-3).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
        }
    }

    public void onDestroy() {
        Log.m19d("TranslationCancelDialog", "onDestroy");
        super.onDestroy();
        this.mDialog = null;
    }

    public void onResume() {
        Log.m19d("TranslationCancelDialog", "onResume");
        super.onResume();
    }

    public void onCancel(DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
        Log.m19d("TranslationCancelDialog", "onCancel");
    }

    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        Log.m19d("TranslationCancelDialog", "onDismiss");
    }
}
