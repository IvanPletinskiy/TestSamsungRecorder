package com.sec.android.app.voicenote.p007ui.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.sec.android.app.voicenote.activity.SimpleActivity;
import com.sec.android.app.voicenote.p007ui.AbsDialogFragment;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.MetadataRepository;
import com.sec.android.app.voicenote.service.SimpleMetadataRepositoryManager;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.ModeNotSupportedDialog */
public class ModeNotSupportedDialog extends AbsDialogFragment {
    private static final String TAG = "ModeNotSupportedDialog";
    private AlertDialog mDialog;
    private String mMicNameString;
    private String mModeStr;

    public static ModeNotSupportedDialog newInstance(Bundle bundle) {
        ModeNotSupportedDialog modeNotSupportedDialog = new ModeNotSupportedDialog();
        modeNotSupportedDialog.setArguments(bundle);
        return modeNotSupportedDialog;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        Log.m26i(TAG, "onCreateDialog");
        final FragmentActivity activity = getActivity();
        View inflate = activity.getLayoutInflater().inflate(C0690R.layout.dialog_mode_not_supported, (ViewGroup) null);
        getModeString();
        getMicNameString();
        ((TextView) inflate.findViewById(C0690R.C0693id.mode_not_supported_content)).setText(getString(C0690R.string.mode_is_not_available_and_notify_unplug_mic, this.mModeStr, this.mMicNameString));
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getString(C0690R.string.mode_not_supported_and_unplug_mic, this.mModeStr));
        builder.setView(inflate);
        setCancelable(false);
        builder.setPositiveButton(C0690R.string.f92ok, $$Lambda$ModeNotSupportedDialog$WB7a_scvhdDVUgsUeXjXZhn1pkY.INSTANCE);
        this.mDialog = builder.create();
        this.mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            private final /* synthetic */ Activity f$1;

            {
                this.f$1 = activity;
            }

            public final void onShow(DialogInterface dialogInterface) {
                ModeNotSupportedDialog.this.lambda$onCreateDialog$1$ModeNotSupportedDialog(this.f$1, dialogInterface);
            }
        });
        return this.mDialog;
    }

    static /* synthetic */ void lambda$onCreateDialog$0(DialogInterface dialogInterface, int i) {
        Log.m29v(TAG, "Ok");
        int scene = VoiceNoteApplication.getScene();
        if (scene == 1 || scene == 11) {
            Settings.setSettings("record_mode", 1);
            VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.CHANGE_MODE));
        }
        VoiceNoteObservable.getInstance().notifyObservers(22);
    }

    public /* synthetic */ void lambda$onCreateDialog$1$ModeNotSupportedDialog(@SuppressLint({"InflateParams"}) Activity activity, DialogInterface dialogInterface) {
        this.mDialog.getButton(-1).setTextColor(activity.getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
    }

    public void onResume() {
        super.onResume();
    }

    private void getModeString() {
        int i;
        if (getActivity() instanceof SimpleActivity) {
            i = SimpleMetadataRepositoryManager.getInstance().getMetadataRepository(VoiceNoteApplication.getSimpleActivitySession()).getRecordMode();
        } else if (Engine.getInstance().getScene() == 6) {
            i = MetadataRepository.getInstance().getRecordMode();
        } else {
            i = Settings.getIntSettings("record_mode", 1);
        }
        if (i == 2) {
            this.mModeStr = getString(C0690R.string.interview_mode);
        } else if (i != 4) {
            this.mModeStr = getString(C0690R.string.normal_mode);
        } else {
            this.mModeStr = getString(C0690R.string.speech_to_text_mode);
        }
    }

    public void getMicNameString() {
        this.mMicNameString = getString(C0690R.string.external_mic);
    }
}
