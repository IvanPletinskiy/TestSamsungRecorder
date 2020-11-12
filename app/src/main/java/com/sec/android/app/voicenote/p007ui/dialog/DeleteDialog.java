package com.sec.android.app.voicenote.p007ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.p007ui.AbsDialogFragment;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.NFCProvider;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.util.ArrayList;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.DeleteDialog */
public class DeleteDialog extends AbsDialogFragment {
    private static final String TAG = "DeleteDialog";
    int hasTagDataCount = 0;
    private AlertDialog mDialog;
    private VoiceNoteObservable mObservable = VoiceNoteObservable.getInstance();

    public static DeleteDialog newInstance(Bundle bundle) {
        DeleteDialog deleteDialog = new DeleteDialog();
        deleteDialog.setArguments(bundle);
        return deleteDialog;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        final int i = getArguments().getInt(DialogFactory.BUNDLE_SCENE);
        ArrayList arrayList = (ArrayList) getArguments().getSerializable(DialogFactory.BUNDLE_IDS);
        final FragmentActivity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        this.hasTagDataCount = NFCProvider.hasTagData((Context) activity, (ArrayList<Long>) arrayList);
        if (arrayList == null || arrayList.size() <= 1) {
            if (i == 14) {
                builder.setMessage(activity.getResources().getQuantityString(C0690R.plurals.trash_recordings_permanently_deleted, 1, new Object[]{1}));
            } else if (this.hasTagDataCount == 1) {
                builder.setMessage(getString(C0690R.string.delete_nfc_item));
            } else {
                builder.setMessage(getString(C0690R.string.delete_item));
            }
        } else if (i == 14) {
            builder.setMessage(activity.getResources().getQuantityString(C0690R.plurals.trash_recordings_permanently_deleted, arrayList.size(), new Object[]{Integer.valueOf(arrayList.size())}));
        } else {
            int i2 = this.hasTagDataCount;
            if (i2 == 1) {
                builder.setMessage(getString(C0690R.string.delete_voice_label_file, Integer.valueOf(arrayList.size())));
            } else if (i2 <= 1) {
                builder.setMessage(getString(C0690R.string.delete_items, Integer.valueOf(arrayList.size())));
            } else if (i2 == arrayList.size()) {
                builder.setMessage(getString(C0690R.string.delete_nfc_items));
            } else {
                builder.setMessage(getString(C0690R.string.delete_voice_label_files, Integer.valueOf(this.hasTagDataCount), Integer.valueOf(arrayList.size())));
            }
        }
        builder.setPositiveButton(C0690R.string.delete, new DialogInterface.OnClickListener() {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = i;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                DeleteDialog.this.lambda$onCreateDialog$0$DeleteDialog(this.f$1, dialogInterface, i);
            }
        });
        builder.setCancelable(true);
        builder.setNegativeButton(C0690R.string.cancel, $$Lambda$DeleteDialog$GMeLwBtw3kCld0v3qMMlAYHFZfs.INSTANCE);
        this.mDialog = builder.create();
        this.mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            private final /* synthetic */ Activity f$1;

            {
                this.f$1 = activity;
            }

            public final void onShow(DialogInterface dialogInterface) {
                DeleteDialog.this.lambda$onCreateDialog$2$DeleteDialog(this.f$1, dialogInterface);
            }
        });
        return this.mDialog;
    }

    public /* synthetic */ void lambda$onCreateDialog$0$DeleteDialog(int i, DialogInterface dialogInterface, int i2) {
        SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_DELETE, i);
        this.mObservable.notifyObservers(Integer.valueOf(Event.DELETE));
    }

    static /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, int i) {
        Log.m29v("DeleteDialog", "Cancel");
        dialogInterface.dismiss();
    }

    public /* synthetic */ void lambda$onCreateDialog$2$DeleteDialog(Activity activity, DialogInterface dialogInterface) {
        this.mDialog.getButton(-1).setTextColor(activity.getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
        this.mDialog.getButton(-2).setTextColor(activity.getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
    }

    public void onResume() {
        super.onResume();
    }
}
