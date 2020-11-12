package com.sec.android.app.voicenote.p007ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.p007ui.AbsDialogFragment;
import com.sec.android.app.voicenote.provider.Log;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.SleepingAppWarningDialog */
public class SleepingAppWarningDialog extends AbsDialogFragment {
    private static final String TAG = "SleepingAppWarningDialog";
    private AlertDialog mDialog;

    public static SleepingAppWarningDialog newInstance() {
        return new SleepingAppWarningDialog();
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(C0690R.string.sleeping_warning_dialog_title);
        builder.setMessage(C0690R.string.sleeping_warning_dialog_message);
        builder.setPositiveButton(C0690R.string.sleeping_warning_dialog_turn_off, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                SleepingAppWarningDialog.this.lambda$onCreateDialog$0$SleepingAppWarningDialog(dialogInterface, i);
            }
        });
        builder.setNegativeButton(C0690R.string.sleeping_warning_dialog_dont_turn_off, $$Lambda$SleepingAppWarningDialog$ChQ_0SdzAhe6P3GBpcAxOJnJCow.INSTANCE);
        this.mDialog = builder.create();
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null) {
            alertDialog.setCanceledOnTouchOutside(false);
            this.mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                public final void onShow(DialogInterface dialogInterface) {
                    SleepingAppWarningDialog.this.lambda$onCreateDialog$2$SleepingAppWarningDialog(dialogInterface);
                }
            });
        }
        return this.mDialog;
    }

    public /* synthetic */ void lambda$onCreateDialog$0$SleepingAppWarningDialog(DialogInterface dialogInterface, int i) {
        Log.m19d(TAG, "onClick OK");
        startSleepingAppsActivity();
    }

    static /* synthetic */ void lambda$onCreateDialog$1(DialogInterface dialogInterface, int i) {
        Log.m19d(TAG, "onClick Cancel");
        dialogInterface.dismiss();
    }

    public /* synthetic */ void lambda$onCreateDialog$2$SleepingAppWarningDialog(DialogInterface dialogInterface) {
        if (this.mDialog != null && getActivity() != null) {
            this.mDialog.getButton(-1).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
            this.mDialog.getButton(-2).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
        }
    }

    private void startSleepingAppsActivity() {
        Intent intent = new Intent();
        intent.setAction("com.samsung.android.sm.ACTION_OPEN_CHECKABLE_LISTACTIVITY");
        intent.setPackage(getSmartManagerPkgName());
        intent.putExtra("startPackage", "com.sec.android.app.voicenote");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            Log.m22e(TAG, "Can not found Sleeping App activity");
        }
    }

    private String getSmartManagerPkgName() {
//        return SemFloatingFeature.getInstance().getString("SEC_FLOATING_FEATURE_SMARTMANAGER_CONFIG_PACKAGE_NAME", "com.samsung.android.lool");
        return "Blabla stub";
    }

    public void onDestroy() {
        Log.m19d(TAG, "onDestroy");
        super.onDestroy();
        this.mDialog = null;
    }

    public void onResume() {
        Log.m19d(TAG, "onResume");
        super.onResume();
    }

    public void onCancel(DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
        Log.m19d(TAG, "onCancel");
    }

    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        Log.m19d(TAG, "onDismiss");
    }
}
