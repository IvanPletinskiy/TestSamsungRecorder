package com.sec.android.app.voicenote.p007ui.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.sec.android.app.voicenote.p007ui.AbsDialogFragment;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.AudioFormat;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.EditProgressDialog */
public class EditProgressDialog extends AbsDialogFragment {
    private static final String TAG = "EditProgressDialog";
    /* access modifiers changed from: private */
    public static int mPercentage;
    private ProgressUpdater mProgressUpdater = new ProgressUpdater();

    public static EditProgressDialog newInstance(Bundle bundle) {
        EditProgressDialog editProgressDialog = new EditProgressDialog();
        editProgressDialog.setArguments(bundle);
        return editProgressDialog;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setCancelable(false);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        ProgressDialog progressDialog = new ProgressDialog(getActivity(), getTheme());
        progressDialog.setProgressStyle(1);
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        setCancelable(false);
        progressDialog.setOwnerActivity(getActivity());
        this.mProgressUpdater.start(progressDialog);
        Engine.getInstance().setPointerIcon(3);
        return progressDialog;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        Engine.getInstance().setPointerIcon(2);
        FragmentActivity activity = getActivity();
        if (activity != null && activity.isDestroyed()) {
            Log.m26i("EditProgressDialog", "ownerActivity is already Destroyed !!");
        } else if (mPercentage == 100 || (activity != null && activity.isFinishing())) {
            mPercentage = 0;
            this.mProgressUpdater.stop();
            super.onDismiss(dialogInterface);
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.dialog.EditProgressDialog$ProgressUpdater */
    private static class ProgressUpdater extends Handler {
        private static final int PROGRESS_AMR_RATIO = 150;
        private static final int PROGRESS_DISMISS = 1;
        private static final int PROGRESS_M4A_RATIO = 450;
        private static final int PROGRESS_UPDATE = 0;
        private static final int PROGRESS_UPDATE_INTERVAL = 50;
        private ProgressDialog mDialog;
        private long mDuration;
        private boolean mIsAMR;
        private long mOldTime;
        private long mProcessingTime;

        private ProgressUpdater() {
            this.mDuration = 0;
            this.mProcessingTime = 0;
            this.mOldTime = 0;
            this.mIsAMR = false;
            this.mDialog = null;
        }

        /* access modifiers changed from: private */
        public void start(ProgressDialog progressDialog) {
            Log.m26i("EditProgressDialog", "ProgressUpdater start");
            removeMessages(0);
            removeMessages(1);
            this.mDuration = (long) Engine.getInstance().getDuration();
            this.mIsAMR = Engine.getInstance().getRecentFilePath().endsWith(AudioFormat.ExtType.EXT_AMR);
            this.mProcessingTime = 0;
            this.mDialog = progressDialog;
            if (this.mDuration > 120000) {
                this.mDialog.setProgress(0);
                this.mOldTime = System.currentTimeMillis();
                sendEmptyMessage(0);
                return;
            }
            int unused = EditProgressDialog.mPercentage = 100;
            this.mDialog.setProgress(100);
        }

        /* access modifiers changed from: private */
        public void stop() {
            Log.m26i("EditProgressDialog", "ProgressUpdater stop - mPercentage : " + EditProgressDialog.mPercentage);
            removeMessages(0);
            removeMessages(1);
            this.mDialog = null;
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                long currentTimeMillis = System.currentTimeMillis();
                this.mProcessingTime += (currentTimeMillis - this.mOldTime) * ((long) (this.mIsAMR ? PROGRESS_AMR_RATIO : PROGRESS_M4A_RATIO));
                int unused = EditProgressDialog.mPercentage = (int) ((this.mProcessingTime * 100) / this.mDuration);
                if (EditProgressDialog.mPercentage > 100) {
                    int unused2 = EditProgressDialog.mPercentage = 100;
                }
                Log.m29v("EditProgressDialog", "ProgressUpdater update - dx:" + (currentTimeMillis - this.mOldTime) + " mProcessingTime:" + this.mProcessingTime + " mPercentage:" + EditProgressDialog.mPercentage);
                this.mDialog.setProgress(EditProgressDialog.mPercentage);
                this.mOldTime = currentTimeMillis;
                if (EditProgressDialog.mPercentage == 100) {
                    sendEmptyMessageDelayed(1, 50);
                } else {
                    sendEmptyMessageDelayed(0, 50);
                }
            } else if (i == 1) {
                Log.m26i("EditProgressDialog", "ProgressUpdater notifyObservers HIDE_EDIT_PROGRESS_DIALOG");
                VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.HIDE_EDIT_PROGRESS_DIALOG));
            }
        }
    }
}
