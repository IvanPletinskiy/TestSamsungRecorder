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
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.CategoryDeleteDialog */
public class CategoryDeleteDialog extends AbsDialogFragment {
    private static final String TAG = "CategoryDeleteDialog";
    private AlertDialog mDialog;
    private VoiceNoteObservable mObservable = VoiceNoteObservable.getInstance();

    public static CategoryDeleteDialog newInstance(Bundle bundle) {
        CategoryDeleteDialog categoryDeleteDialog = new CategoryDeleteDialog();
        categoryDeleteDialog.setArguments(bundle);
        return categoryDeleteDialog;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        long[] jArr = (long[]) getArguments().getSerializable(DialogFactory.BUNDLE_IDS);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        int length = jArr != null ? jArr.length : 0;
        builder.setTitle(getResources().getQuantityString(C0690R.plurals.delete_category_title, length, new Object[]{Integer.valueOf(length)}));
        if (length > 1) {
            builder.setMessage(getString(C0690R.string.delete_categories_items));
        } else {
            builder.setMessage(getString(C0690R.string.delete_categories_item));
        }
        builder.setPositiveButton(C0690R.string.delete, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                CategoryDeleteDialog.this.lambda$onCreateDialog$0$CategoryDeleteDialog(dialogInterface, i);
            }
        });
        builder.setCancelable(true);
        builder.setNegativeButton(C0690R.string.cancel, $$Lambda$CategoryDeleteDialog$iqCbsjc3vk1rZRFg58jRP3vqg4k.INSTANCE);
        this.mDialog = builder.create();
        this.mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public final void onShow(DialogInterface dialogInterface) {
                CategoryDeleteDialog.this.lambda$onCreateDialog$2$CategoryDeleteDialog(dialogInterface);
            }
        });
        return this.mDialog;
    }

    public /* synthetic */ void lambda$onCreateDialog$0$CategoryDeleteDialog(DialogInterface dialogInterface, int i) {
        this.mObservable.notifyObservers(Integer.valueOf(Event.DELETE_CATEGORY));
    }

    static /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, int i) {
        Log.m29v(TAG, "Cancel");
        dialogInterface.dismiss();
    }

    public /* synthetic */ void lambda$onCreateDialog$2$CategoryDeleteDialog(DialogInterface dialogInterface) {
        if (this.mDialog != null && getActivity() != null) {
            this.mDialog.getButton(-1).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
            this.mDialog.getButton(-2).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
        }
    }

    public void onResume() {
        super.onResume();
    }
}
