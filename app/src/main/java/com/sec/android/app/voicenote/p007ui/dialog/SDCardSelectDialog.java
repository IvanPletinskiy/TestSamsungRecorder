package com.sec.android.app.voicenote.p007ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.p007ui.AbsDialogFragment;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.SDCardSelectDialog */
public class SDCardSelectDialog extends AbsDialogFragment {
    private static final String EDGE_INTENT_RECORD_START = "voicenote.intent.action.edge_start_record";
    private static final String TAG = "SDCardSelectDialog";
    private static volatile SDCardSelectDialog dialogFragment;
    private AlertDialog mDialog;
    private VoiceNoteObservable mObservable = VoiceNoteObservable.getInstance();

    public static SDCardSelectDialog newInstance(Bundle bundle) {
        if (dialogFragment == null) {
            synchronized (SDCardSelectDialog.class) {
                if (dialogFragment == null) {
                    dialogFragment = new SDCardSelectDialog();
                    dialogFragment.setArguments(bundle);
                }
            }
        }
        return dialogFragment;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(C0690R.string.change_storage_location_title);
        if (VoiceNoteFeature.FLAG_IS_TABLET) {
            builder.setMessage(C0690R.string.change_storage_location_body_tablet);
        } else {
            builder.setMessage(C0690R.string.change_storage_location_body);
        }
        builder.setPositiveButton(C0690R.string.save_to_SD_card, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                SDCardSelectDialog.this.lambda$onCreateDialog$0$SDCardSelectDialog(dialogInterface, i);
            }
        });
        builder.setNegativeButton(C0690R.string.save_to_internal_storage, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                SDCardSelectDialog.this.lambda$onCreateDialog$1$SDCardSelectDialog(dialogInterface, i);
            }
        });
        this.mDialog = builder.create();
        this.mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public final void onShow(DialogInterface dialogInterface) {
                SDCardSelectDialog.this.lambda$onCreateDialog$2$SDCardSelectDialog(dialogInterface);
            }
        });
        return this.mDialog;
    }

    public /* synthetic */ void lambda$onCreateDialog$0$SDCardSelectDialog(DialogInterface dialogInterface, int i) {
        Log.m19d("SDCardSelectDialog", "onClick ok - set STORAGE_MEMORYCARD");
        Settings.setSettings(Settings.KEY_STORAGE, 1);
        Settings.setSettings(Settings.KEY_SDCARD_PREVIOUS_STATE, 1);
        VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.CHANGE_STORAGE));
        SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_storage), getResources().getString(C0690R.string.event_storage_popup_ok));
    }

    public /* synthetic */ void lambda$onCreateDialog$1$SDCardSelectDialog(DialogInterface dialogInterface, int i) {
        Log.m19d("SDCardSelectDialog", "onClick cancel - set STORAGE_PHONE");
        Settings.setSettings(Settings.KEY_STORAGE, 0);
        Settings.setSettings(Settings.KEY_SDCARD_PREVIOUS_STATE, 1);
        SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_storage), getResources().getString(C0690R.string.event_storage_popup_cancel));
    }

    public /* synthetic */ void lambda$onCreateDialog$2$SDCardSelectDialog(DialogInterface dialogInterface) {
        if (this.mDialog != null && getActivity() != null) {
            this.mDialog.getButton(-1).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
            this.mDialog.getButton(-2).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
        }
    }

    public void onDestroy() {
        Log.m19d("SDCardSelectDialog", "onDestroy");
        super.onDestroy();
        this.mDialog = null;
    }

    public void onResume() {
        Log.m19d("SDCardSelectDialog", "onResume");
        super.onResume();
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.mObservable.notifyObservers(Integer.valueOf(Event.HIDE_DIALOG));
    }

    public void onDismiss(DialogInterface dialogInterface) {
        Intent intent;
        super.onDismiss(dialogInterface);
        if (getActivity() != null && (intent = getActivity().getIntent()) != null && EDGE_INTENT_RECORD_START.equals(intent.getAction())) {
            VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.RECORD_START_BY_TASK_EDGE));
        }
    }
}
