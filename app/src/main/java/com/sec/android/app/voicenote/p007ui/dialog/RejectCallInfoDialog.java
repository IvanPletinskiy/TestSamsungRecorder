package com.sec.android.app.voicenote.p007ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.p007ui.AbsDialogFragment;
import com.sec.android.app.voicenote.provider.CallRejectChecker;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.StatusBarProvider;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.RejectCallInfoDialog */
public class RejectCallInfoDialog extends AbsDialogFragment {
    private static final String TAG = "RejectCallInfoDialog";
    private AlertDialog mDialog = null;

    public static RejectCallInfoDialog newInstance(Bundle bundle) {
        RejectCallInfoDialog rejectCallInfoDialog = new RejectCallInfoDialog();
        rejectCallInfoDialog.setArguments(bundle);
        return rejectCallInfoDialog;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        FragmentActivity activity = getActivity();
        StatusBarProvider.getInstance().collapsePanels(activity);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        int rejectCallCount = CallRejectChecker.getInstance().getRejectCallCount();
        builder.setTitle(getResources().getQuantityString(C0690R.plurals.call_blocked_title, rejectCallCount, new Object[]{Integer.valueOf(rejectCallCount)}));
        if (rejectCallCount > 1) {
            builder.setMessage(getString(C0690R.string.more_calls_missed_record));
        } else {
            builder.setMessage(getString(C0690R.string.a_call_missed_record));
        }
        builder.setPositiveButton(C0690R.string.category_call_history, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                RejectCallInfoDialog.this.lambda$onCreateDialog$0$RejectCallInfoDialog(dialogInterface, i);
            }
        });
        builder.setCancelable(true);
        builder.setNegativeButton(C0690R.string.cancel, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                RejectCallInfoDialog.this.lambda$onCreateDialog$1$RejectCallInfoDialog(dialogInterface, i);
            }
        });
        this.mDialog = builder.create();
        this.mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public final void onShow(DialogInterface dialogInterface) {
                RejectCallInfoDialog.this.lambda$onCreateDialog$2$RejectCallInfoDialog(dialogInterface);
            }
        });
        return this.mDialog;
    }

    public /* synthetic */ void lambda$onCreateDialog$0$RejectCallInfoDialog(DialogInterface dialogInterface, int i) {
        Intent intent = new Intent("com.android.phone.action.RECENT_CALLS");
        intent.setFlags(268435456);
        try {
            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_call_history), getResources().getString(C0690R.string.event_call_history_popup_history));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.m24e("RejectCallInfoDialog", "ActivityNotFoundException", (Throwable) e);
        }
    }

    public /* synthetic */ void lambda$onCreateDialog$1$RejectCallInfoDialog(DialogInterface dialogInterface, int i) {
        Log.m29v("RejectCallInfoDialog", "Cancel");
        SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_call_history), getResources().getString(C0690R.string.event_call_history_popup_cancel));
        dialogInterface.dismiss();
    }

    public /* synthetic */ void lambda$onCreateDialog$2$RejectCallInfoDialog(DialogInterface dialogInterface) {
        if (this.mDialog != null && getActivity() != null) {
            this.mDialog.getButton(-1).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
            this.mDialog.getButton(-2).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
        }
    }

    public void onResume() {
        super.onResume();
    }
}
