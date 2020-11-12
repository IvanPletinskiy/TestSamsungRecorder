package com.sec.android.app.voicenote.p007ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.TrashHelper;
import com.sec.android.app.voicenote.p007ui.AbsDialogFragment;
import com.sec.android.app.voicenote.provider.Log;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.EmptyTrashDialog */
public class EmptyTrashDialog extends AbsDialogFragment {
    private static final String TAG = "EmptyTrashDialog";
    private AlertDialog mDialog;

    public static EmptyTrashDialog newInstance() {
        return new EmptyTrashDialog();
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        final FragmentActivity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        int numberTrashItem = TrashHelper.getInstance().getNumberTrashItem(activity);
        builder.setTitle(activity.getResources().getString(C0690R.string.trash_empty_the_trash));
        builder.setMessage(activity.getResources().getQuantityString(C0690R.plurals.trash_recordings_permanently_deleted, numberTrashItem, new Object[]{Integer.valueOf(numberTrashItem)}));
        builder.setPositiveButton(C0690R.string.trash_empty_trash, new DialogInterface.OnClickListener() {
            private final /* synthetic */ Activity f$0;

            {
                this.f$0 = activity;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                TrashHelper.getInstance().emptyTrash(this.f$0);
            }
        });
        builder.setNegativeButton(C0690R.string.cancel, $$Lambda$EmptyTrashDialog$vhPZp5gh4XdpJuqrYgQ0m9OyUp8.INSTANCE);
        this.mDialog = builder.create();
        this.mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            private final /* synthetic */ Activity f$1;

            {
                this.f$1 = activity;
            }

            public final void onShow(DialogInterface dialogInterface) {
                EmptyTrashDialog.this.lambda$onCreateDialog$2$EmptyTrashDialog(this.f$1, dialogInterface);
            }
        });
        return this.mDialog;
    }

    static /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, int i) {
        Log.m19d("EmptyTrashDialog", "onClick Cancel");
        dialogInterface.dismiss();
    }

    public /* synthetic */ void lambda$onCreateDialog$2$EmptyTrashDialog(Activity activity, DialogInterface dialogInterface) {
        this.mDialog.getButton(-1).setTextColor(activity.getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
        this.mDialog.getButton(-2).setTextColor(activity.getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
    }

    public void onResume() {
        super.onResume();
    }
}
