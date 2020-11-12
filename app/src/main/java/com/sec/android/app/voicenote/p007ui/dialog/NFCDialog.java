package com.sec.android.app.voicenote.p007ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.p007ui.AbsDialogFragment;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.NFCProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.NFCDialog */
public class NFCDialog extends AbsDialogFragment {
    private static final int NFC_RETRY_CNT = 10;
    private static final int NFC_RETRY_INTERVAL = 200;
    private static final String TAG = "NFCDialog";
    private AlertDialog mDialog;
    private DialogFactory.DialogResultListener mInterface = null;

    public static NFCDialog newInstance(Bundle bundle, DialogFactory.DialogResultListener dialogResultListener) {
        NFCDialog nFCDialog = new NFCDialog();
        nFCDialog.setArguments(bundle);
        nFCDialog.setListener(dialogResultListener);
        return nFCDialog;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        Log.m26i(TAG, "onCreateDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View inflate = getActivity().getLayoutInflater().inflate(C0690R.layout.dialog_nfc_enable, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(C0690R.C0693id.warning_text);
        if (VoiceNoteFeature.FLAG_SUPPORT_NFC_CARDMODE) {
            textView.setText(C0690R.string.nfc_will_be_turned_on_tags_and_connecting_enable);
        } else {
            textView.setText(C0690R.string.nfc_will_be_turned_on);
        }
        builder.setPositiveButton(C0690R.string.turn_on, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                NFCDialog.this.lambda$onCreateDialog$0$NFCDialog(dialogInterface, i);
            }
        });
        builder.setNegativeButton(17039360, $$Lambda$NFCDialog$PWqu0GG0A6WLQuG0pnkWlsWuOVw.INSTANCE);
        builder.setView(inflate);
        this.mDialog = builder.create();
        this.mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public final void onShow(DialogInterface dialogInterface) {
                NFCDialog.this.lambda$onCreateDialog$2$NFCDialog(dialogInterface);
            }
        });
        return this.mDialog;
    }

    public /* synthetic */ void lambda$onCreateDialog$0$NFCDialog(DialogInterface dialogInterface, int i) {
        Log.m26i(TAG, "onClick - keyCode : " + i);
        if (getDialog() == null) {
            Log.m26i(TAG, "onClick view is null");
            return;
        }
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Log.m26i(TAG, "onClick activity is null");
            return;
        }
        NFCProvider.enableNFC(activity);
        for (int i2 = 0; i2 < 10 && !NFCProvider.isNFCEnabled(activity); i2++) {
            Log.m26i(TAG, "delay 200 ms");
            try {
                Thread.sleep(200);
            } catch (InterruptedException unused) {
                Log.m22e(TAG, "InterruptedException");
            }
        }
        if (this.mInterface != null) {
            Bundle arguments = getArguments();
            arguments.putInt("result_code", i);
            this.mInterface.onDialogResult(this, arguments);
        }
        dismissAllowingStateLoss();
    }

    public /* synthetic */ void lambda$onCreateDialog$2$NFCDialog(DialogInterface dialogInterface) {
        if (this.mDialog != null && getActivity() != null) {
            this.mDialog.getButton(-1).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
            this.mDialog.getButton(-2).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
        }
    }

    public void onResume() {
        super.onResume();
    }

    private void setListener(DialogFactory.DialogResultListener dialogResultListener) {
        this.mInterface = dialogResultListener;
    }
}
