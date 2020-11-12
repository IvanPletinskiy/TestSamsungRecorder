package com.sec.android.app.voicenote.p007ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import androidx.annotation.NonNull;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.p007ui.AbsDialogFragment;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.DataCheckDialog */
public class DataCheckDialog extends AbsDialogFragment {
    public static final String MODULE = "Module";
    private static final String TAG = "DataCheckDialog";
    private static int module = -1;
    private AlertDialog mDialog = null;
    private DialogFactory.DialogResultListener mInterface = null;
    private DialogInterface.OnKeyListener mOnKeyListener = $$Lambda$DataCheckDialog$BNNlGLJX7jWw3dALSVumzrMHUT8.INSTANCE;

    /* renamed from: com.sec.android.app.voicenote.ui.dialog.DataCheckDialog$DataCheckModule */
    public static class DataCheckModule {
        public static final int PRIVACY_POLICY = 2;
        public static final int TRANSLATE = 3;
        public static final int UPDATE_CHECK = 1;
        public static final int VOICE_MEMO = 0;
    }

    public static DataCheckDialog newInstance(Bundle bundle, DialogFactory.DialogResultListener dialogResultListener) {
        DataCheckDialog dataCheckDialog = new DataCheckDialog();
        dataCheckDialog.setArguments(bundle);
        dataCheckDialog.setListener(dialogResultListener);
        return dataCheckDialog;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        module = getArguments().getInt(DialogFactory.BUNDLE_DATA_CHECK_MODULE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String string = getString(C0690R.string.app_name);
        builder.setMessage(String.format(getString(C0690R.string.use_network_connection_mgs), new Object[]{string}));
        builder.setPositiveButton(C0690R.string.allow, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                DataCheckDialog.this.lambda$onCreateDialog$0$DataCheckDialog(dialogInterface, i);
            }
        });
        builder.setNegativeButton(C0690R.string.deny, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                DataCheckDialog.this.lambda$onCreateDialog$1$DataCheckDialog(dialogInterface, i);
            }
        });
        this.mDialog = builder.create();
        this.mDialog.setCanceledOnTouchOutside(false);
        this.mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public final void onShow(DialogInterface dialogInterface) {
                DataCheckDialog.this.lambda$onCreateDialog$2$DataCheckDialog(dialogInterface);
            }
        });
        return this.mDialog;
    }

    public /* synthetic */ void lambda$onCreateDialog$0$DataCheckDialog(DialogInterface dialogInterface, int i) {
        Log.m29v("DataCheckDialog", "Allow - module : " + module);
        Settings.setSettings(Settings.KEY_DATA_CHECK_SHOW_AGAIN, false);
        if (this.mInterface != null && getArguments() != null) {
            Bundle arguments = getArguments();
            arguments.putInt(DialogFactory.BUNDLE_REQUEST_CODE, 8);
            arguments.putInt("result_code", -1);
            arguments.putInt(MODULE, module);
            this.mInterface.onDialogResult(this, arguments);
        }
    }

    public /* synthetic */ void lambda$onCreateDialog$1$DataCheckDialog(DialogInterface dialogInterface, int i) {
        Log.m29v("DataCheckDialog", "Deny - module : " + module);
        int i2 = module;
        if (i2 == 0) {
            Settings.setSettings("record_mode", 1);
            VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.CHANGE_MODE));
        } else if (i2 != 3) {
        } else {
            if (this.mInterface != null && getArguments() != null) {
                Bundle arguments = getArguments();
                arguments.putInt(DialogFactory.BUNDLE_REQUEST_CODE, 8);
                arguments.putInt("result_code", -2);
                arguments.putInt(MODULE, module);
                this.mInterface.onDialogResult(this, arguments);
            } else if (this.mInterface == null) {
                VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.OPEN_FULL_PLAYER));
            }
        }
    }

    public /* synthetic */ void lambda$onCreateDialog$2$DataCheckDialog(DialogInterface dialogInterface) {
        if (getActivity() != null && this.mDialog != null && isAdded()) {
            this.mDialog.getButton(-1).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
            this.mDialog.getButton(-2).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
        }
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.mInterface = null;
        this.mDialog.setOnKeyListener((DialogInterface.OnKeyListener) null);
        super.onDismiss(dialogInterface);
    }

    private void setListener(DialogFactory.DialogResultListener dialogResultListener) {
        this.mInterface = dialogResultListener;
    }

    public void onResume() {
        AlertDialog alertDialog;
        super.onResume();
        if (getShowsDialog() && (alertDialog = this.mDialog) != null) {
            alertDialog.setOnKeyListener(this.mOnKeyListener);
        }
    }

    static /* synthetic */ boolean lambda$new$3(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 84) {
            return true;
        }
        if (i != 4 || keyEvent.getAction() != 1) {
            return false;
        }
        Log.m29v("DataCheckDialog", "Back - module : " + module);
        if (module != 0) {
            return false;
        }
        Settings.setSettings("record_mode", 1);
        VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.CHANGE_MODE));
        return false;
    }
}
