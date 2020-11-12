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
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.p007ui.AbsDialogFragment;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.NetworkNotConnectedDialog */
public class NetworkNotConnectedDialog extends AbsDialogFragment {
    private static final String TAG = "NetworkNotConnectedDialog";
    private AlertDialog mDialog;
    private DialogFactory.DialogResultListener mInterface = null;

    public static NetworkNotConnectedDialog newInstance(Bundle bundle, DialogFactory.DialogResultListener dialogResultListener) {
        NetworkNotConnectedDialog networkNotConnectedDialog = new NetworkNotConnectedDialog();
        networkNotConnectedDialog.setArguments(bundle);
        networkNotConnectedDialog.setListener(dialogResultListener);
        return networkNotConnectedDialog;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        Log.m26i(TAG, "onCreateDialog");
        View inflate = getActivity().getLayoutInflater().inflate(C0690R.layout.dialog_network_not_connected, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(C0690R.C0693id.network_not_connected_content);
        textView.setText(VoiceNoteFeature.FLAG_SUPPORT_CHINA_WLAN ? C0690R.string.no_network_connection_mgs_for_chn : C0690R.string.no_network_connection_mgs);
        textView.setTextColor(getResources().getColor(C0690R.C0691color.dialog_description_text, (Resources.Theme) null));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(C0690R.string.no_network_connection);
        builder.setView(inflate);
        builder.setPositiveButton(C0690R.string.f92ok, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                NetworkNotConnectedDialog.this.lambda$onCreateDialog$0$NetworkNotConnectedDialog(dialogInterface, i);
            }
        });
        this.mDialog = builder.create();
        this.mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public final void onShow(DialogInterface dialogInterface) {
                NetworkNotConnectedDialog.this.lambda$onCreateDialog$1$NetworkNotConnectedDialog(dialogInterface);
            }
        });
        return this.mDialog;
    }

    public /* synthetic */ void lambda$onCreateDialog$0$NetworkNotConnectedDialog(DialogInterface dialogInterface, int i) {
        Log.m29v(TAG, "Ok");
        Bundle arguments = getArguments();
        if (arguments != null && this.mInterface != null) {
            arguments.putInt(DialogFactory.BUNDLE_REQUEST_CODE, 10);
            arguments.putInt("result_code", -1);
            this.mInterface.onDialogResult(this, arguments);
        }
    }

    public /* synthetic */ void lambda$onCreateDialog$1$NetworkNotConnectedDialog(DialogInterface dialogInterface) {
        if (getActivity() != null) {
            this.mDialog.getButton(-1).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onDismiss(DialogInterface dialogInterface) {
        Bundle arguments = getArguments();
        if (!(arguments == null || this.mInterface == null)) {
            arguments.putInt(DialogFactory.BUNDLE_REQUEST_CODE, 10);
            arguments.putInt("result_code", -1);
            this.mInterface.onDialogResult(this, arguments);
        }
        this.mInterface = null;
        super.onDismiss(dialogInterface);
    }

    private void setListener(DialogFactory.DialogResultListener dialogResultListener) {
        this.mInterface = dialogResultListener;
    }
}
