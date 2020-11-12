package com.sec.android.app.voicenote.p007ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.view.KeyEvent;
import androidx.fragment.app.DialogFragment;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;

/* renamed from: com.sec.android.app.voicenote.ui.AbsDialogFragment */
public class AbsDialogFragment extends DialogFragment {
    private static final String TAG = "AbsDialogFragment";
    private final String PID_APP = "pid_app";
    private DialogFactory.DialogDestroyListener mDialogDestroyListener;
    private DialogInterface.OnKeyListener mOnKeyListener = $$Lambda$AbsDialogFragment$EF4__nYRNGIH9FEct7AJ0L07BIk.INSTANCE;

    public void setDialogResultListener(DialogFactory.DialogResultListener dialogResultListener) {
    }

    @SuppressLint({"HandlerLeak"})
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null && Process.myPid() != bundle.getInt("pid_app")) {
            new Handler() {
                public void handleMessage(Message message) {
                    super.handleMessage(message);
                    AbsDialogFragment.this.dismissAllowingStateLoss();
                }
            }.sendEmptyMessageDelayed(0, 300);
        }
    }

    public void onPause() {
        super.onPause();
    }

    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt("pid_app", Process.myPid());
        super.onSaveInstanceState(bundle);
    }

    public void onResume() {
        super.onResume();
        if (getDialog() != null) {
            getDialog().setOnKeyListener(this.mOnKeyListener);
        }
    }

    public void onDestroy() {
        if (getDialog() != null) {
            getDialog().setOnKeyListener((DialogInterface.OnKeyListener) null);
        }
        DialogFactory.DialogDestroyListener dialogDestroyListener = this.mDialogDestroyListener;
        if (dialogDestroyListener != null) {
            dialogDestroyListener.onDialogDestroy(getFragmentManager());
        }
        super.onDestroy();
    }

    public void onDetach() {
        super.onDetach();
    }

    static /* synthetic */ boolean lambda$new$0(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
        return keyEvent.getKeyCode() == 84;
    }

    public void setDialogDestroyListener(DialogFactory.DialogDestroyListener dialogDestroyListener) {
        this.mDialogDestroyListener = dialogDestroyListener;
    }
}
