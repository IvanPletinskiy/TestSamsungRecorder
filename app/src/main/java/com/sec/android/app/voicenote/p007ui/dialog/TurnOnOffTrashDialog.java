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
import com.sec.android.app.voicenote.common.util.TrashHelper;
import com.sec.android.app.voicenote.p007ui.AbsDialogFragment;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.StorageProvider;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.util.ArrayList;
import java.util.Iterator;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.TurnOnOffTrashDialog */
public class TurnOnOffTrashDialog extends AbsDialogFragment {
    private static final String TAG = "TurnOnOffTrashDialog";
    private AlertDialog mDialog;
    private VoiceNoteObservable mObservable = VoiceNoteObservable.getInstance();

    public static TurnOnOffTrashDialog newInstance(Bundle bundle) {
        TurnOnOffTrashDialog turnOnOffTrashDialog = new TurnOnOffTrashDialog();
        turnOnOffTrashDialog.setArguments(bundle);
        return turnOnOffTrashDialog;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        FragmentActivity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (Settings.getBooleanSettings(Settings.KEY_TRASH_IS_TURN_ON, false)) {
            builder.setTitle(activity.getResources().getString(C0690R.string.trash_turn_off_the_trash));
            builder.setMessage(activity.getResources().getString(C0690R.string.trash_all_recording_deleted));
//            builder.setPositiveButton(C0690R.string.sleeping_warning_dialog_turn_off, new DialogInterface.OnClickListener(activity) {
//                private final /* synthetic */ Activity f$1;
//
//                {
//                    this.f$1 = r2;
//                }
//
//                public final void onClick(DialogInterface dialogInterface, int i) {
//                    TurnOnOffTrashDialog.this.lambda$onCreateDialog$0$TurnOnOffTrashDialog(this.f$1, dialogInterface, i);
//                }
//            });
        } else {
            builder.setTitle(activity.getResources().getString(C0690R.string.trash_turn_on_the_trash));
            builder.setMessage(activity.getResources().getQuantityString(C0690R.plurals.trash_recordings_stay_in_the_trash_for_days, TrashHelper.getInstance().getKeepInTrashDays(), new Object[]{Integer.valueOf(TrashHelper.getInstance().getKeepInTrashDays())}));
            builder.setPositiveButton(C0690R.string.trash_turn_on_trash, new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    TurnOnOffTrashDialog.this.lambda$onCreateDialog$1$TurnOnOffTrashDialog(dialogInterface, i);
                }
            });
        }
        builder.setNegativeButton(C0690R.string.cancel, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                TurnOnOffTrashDialog.this.lambda$onCreateDialog$2$TurnOnOffTrashDialog(dialogInterface, i);
            }
        });
        this.mDialog = builder.create();
//        this.mDialog.setOnShowListener(new DialogInterface.OnShowListener(activity) {
//            private final /* synthetic */ Activity f$1;
//
//            {
//                this.f$1 = r2;
//            }
//
//            public final void onShow(DialogInterface dialogInterface) {
//                TurnOnOffTrashDialog.this.lambda$onCreateDialog$3$TurnOnOffTrashDialog(this.f$1, dialogInterface);
//            }
//        });
        return this.mDialog;
    }

    public /* synthetic */ void lambda$onCreateDialog$0$TurnOnOffTrashDialog(Activity activity, DialogInterface dialogInterface, int i) {
        Settings.setSettings(Settings.KEY_TRASH_IS_TURN_ON, false);
        Settings.setSettings(Settings.KEY_IS_FIRST_DELETE_VOICE_FILE, true);
        TrashHelper.getInstance().emptyTrash(activity);
        this.mObservable.notifyObservers(Integer.valueOf(Event.TRASH_STATUS_CHANGED));
    }

    public /* synthetic */ void lambda$onCreateDialog$1$TurnOnOffTrashDialog(DialogInterface dialogInterface, int i) {
        Settings.setSettings(Settings.KEY_TRASH_IS_TURN_ON, true);
        if (getArguments() == null || !getArguments().getBoolean(DialogFactory.BUNDLE_DELETING_FILE, false)) {
            this.mObservable.notifyObservers(Integer.valueOf(Event.OPEN_TRASH));
            return;
        }
        int i2 = getArguments().getInt(DialogFactory.BUNDLE_SCENE);
        ArrayList arrayList = (ArrayList) getArguments().getSerializable(DialogFactory.BUNDLE_IDS);
        Bundle bundle = new Bundle();
        bundle.putSerializable(DialogFactory.BUNDLE_IDS, arrayList);
        bundle.putInt(DialogFactory.BUNDLE_SCENE, i2);
        String externalStorageStateSd = StorageProvider.getExternalStorageStateSd();
        if (externalStorageStateSd != null && externalStorageStateSd.equals("mounted")) {
            Iterator it = arrayList.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (CursorProvider.getInstance().getPath(((Long) it.next()).longValue()).startsWith(StorageProvider.getRootPath(1))) {
                        bundle.putBoolean(DialogFactory.BUNDLE_DELETE_FILE_IN_SDCARD, true);
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        DialogFactory.show(getActivity().getSupportFragmentManager(), DialogFactory.MOVE_TO_TRASH_DIALOG, bundle);
    }

    public /* synthetic */ void lambda$onCreateDialog$2$TurnOnOffTrashDialog(DialogInterface dialogInterface, int i) {
        Log.m19d("TurnOnOffTrashDialog", "onClick Cancel");
        if (getArguments() != null && getArguments().getBoolean(DialogFactory.BUNDLE_DELETING_FILE, false)) {
            int i2 = getArguments().getInt(DialogFactory.BUNDLE_SCENE);
            Bundle bundle = new Bundle();
            bundle.putSerializable(DialogFactory.BUNDLE_IDS, (ArrayList) getArguments().getSerializable(DialogFactory.BUNDLE_IDS));
            bundle.putInt(DialogFactory.BUNDLE_SCENE, i2);
            DialogFactory.show(getActivity().getSupportFragmentManager(), DialogFactory.DELETE_DIALOG, bundle);
        }
        dialogInterface.dismiss();
    }

    public /* synthetic */ void lambda$onCreateDialog$3$TurnOnOffTrashDialog(Activity activity, DialogInterface dialogInterface) {
        this.mDialog.getButton(-1).setTextColor(activity.getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
        this.mDialog.getButton(-2).setTextColor(activity.getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
    }

    public void onResume() {
        super.onResume();
    }
}
