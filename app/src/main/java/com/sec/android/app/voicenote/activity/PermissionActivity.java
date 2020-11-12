package com.sec.android.app.voicenote.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.sec.android.app.voicenote.C0690R;

public class PermissionActivity extends AppCompatActivity {
    AlertDialog.Builder mBuilder = null;
    Dialog mDialog = null;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mBuilder = new AlertDialog.Builder(this);
        this.mBuilder.setCancelable(true);
        this.mBuilder.setTitle(getResources().getString(C0690R.string.permission_title));
        this.mBuilder.setPositiveButton(C0690R.string.f92ok, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PermissionActivity.this.lambda$onCreate$0$PermissionActivity(dialogInterface, i);
            }
        });
        this.mBuilder.setCancelable(true);
        this.mBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public final void onCancel(DialogInterface dialogInterface) {
                PermissionActivity.this.lambda$onCreate$1$PermissionActivity(dialogInterface);
            }
        });
        this.mBuilder.setMessage(getResources().getString(C0690R.string.permission_content_intro) + "\n\n" + getResources().getString(C0690R.string.permission_require) + "\n• " + getResources().getString(C0690R.string.permission_mic) + "\n• " + getResources().getString(C0690R.string.permission_storage) + "\n\n" + getResources().getString(C0690R.string.permission_optional) + "\n• " + getResources().getString(C0690R.string.permission_call));
        this.mDialog = this.mBuilder.create();
        this.mDialog.setCanceledOnTouchOutside(true);
        this.mDialog.show();
    }

    public /* synthetic */ void lambda$onCreate$0$PermissionActivity(DialogInterface dialogInterface, int i) {
        this.mDialog.hide();
        finish();
    }

    public /* synthetic */ void lambda$onCreate$1$PermissionActivity(DialogInterface dialogInterface) {
        Dialog dialog = this.mDialog;
        if (dialog != null) {
            dialog.hide();
        }
        finish();
    }
}
