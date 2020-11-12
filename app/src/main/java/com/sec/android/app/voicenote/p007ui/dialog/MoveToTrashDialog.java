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
import com.sec.android.app.voicenote.p007ui.AbsDialogFragment;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.util.ArrayList;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.MoveToTrashDialog */
public class MoveToTrashDialog extends AbsDialogFragment {
    private static final String TAG = "MoveToTrashDialog";
    private AlertDialog mDialog;
    private VoiceNoteObservable mObservable = VoiceNoteObservable.getInstance();

    public static MoveToTrashDialog newInstance(Bundle bundle) {
        MoveToTrashDialog moveToTrashDialog = new MoveToTrashDialog();
        moveToTrashDialog.setArguments(bundle);
        return moveToTrashDialog;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        ArrayList arrayList = (ArrayList) getArguments().getSerializable(DialogFactory.BUNDLE_IDS);
        boolean z = getArguments().getBoolean(DialogFactory.BUNDLE_DELETE_FILE_IN_SDCARD, false);
        final FragmentActivity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (z) {
            if (arrayList != null) {
                int size = arrayList.size();
                builder.setTitle(activity.getResources().getQuantityString(C0690R.plurals.trash_header_move_file_to_trash, size, new Object[]{Integer.valueOf(size)}));
            }
            builder.setMessage(activity.getResources().getString(C0690R.string.trash_notification_before_removing_sdcard));
        } else if (arrayList != null) {
            int size2 = arrayList.size();
            builder.setTitle(activity.getResources().getQuantityString(C0690R.plurals.trash_header_move_file_to_trash, size2, new Object[]{Integer.valueOf(size2)}));
        }
        builder.setPositiveButton(C0690R.string.trash_move_to_trash, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                MoveToTrashDialog.this.lambda$onCreateDialog$0$MoveToTrashDialog(dialogInterface, i);
            }
        });
        builder.setCancelable(true);
        builder.setNegativeButton(C0690R.string.cancel, $$Lambda$MoveToTrashDialog$wXS3aUWhFvWJ45o_f2dIsHymAWE.INSTANCE);
        this.mDialog = builder.create();
        this.mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            private final /* synthetic */ Activity f$1;

            {
                this.f$1 = activity;
            }

            public final void onShow(DialogInterface dialogInterface) {
                MoveToTrashDialog.this.lambda$onCreateDialog$2$MoveToTrashDialog(this.f$1, dialogInterface);
            }
        });
        return this.mDialog;
    }

    public /* synthetic */ void lambda$onCreateDialog$0$MoveToTrashDialog(DialogInterface dialogInterface, int i) {
        SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_DELETE, getArguments().getInt(DialogFactory.BUNDLE_SCENE));
        this.mObservable.notifyObservers(Integer.valueOf(Event.DELETE));
    }

    public /* synthetic */ void lambda$onCreateDialog$2$MoveToTrashDialog(Activity activity, DialogInterface dialogInterface) {
        this.mDialog.getButton(-1).setTextColor(activity.getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
        this.mDialog.getButton(-2).setTextColor(activity.getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
    }

    public void onResume() {
        super.onResume();
    }
}
