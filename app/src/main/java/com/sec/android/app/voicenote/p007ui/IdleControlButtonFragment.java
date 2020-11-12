package com.sec.android.app.voicenote.p007ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.activity.WebTosActivity;
import com.sec.android.app.voicenote.common.util.Trace;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;

import androidx.annotation.Nullable;

/* renamed from: com.sec.android.app.voicenote.ui.IdleControlButtonFragment */
public class IdleControlButtonFragment extends AbsFragment {
    private static final String TAG = "IdleControlButtonFragment";
    private int[] mRecordButtonLocation = new int[2];
    private View mView;

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, Bundle bundle) {
        Log.m19d(TAG, "onCreateView");
        Trace.beginSection("IdleControlFrgm.onCreateView");
        View inflate = layoutInflater.inflate(C0690R.layout.fragment_idle_controlbutton, viewGroup, false);
        this.mView = inflate.findViewById(C0690R.C0693id.idle_controlbutton_record_start);
//        this.mView.semSetHoverPopupType(1);
        this.mView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                IdleControlButtonFragment.this.lambda$onCreateView$0$IdleControlButtonFragment(view);
            }
        });
        Trace.endSection();
        return inflate;
    }

    public /* synthetic */ void lambda$onCreateView$0$IdleControlButtonFragment(View view) {
        Log.m26i(TAG, "onClick()");
        postEvent(1009);
    }

    public void onResume() {
        Log.m19d(TAG, "onResume");
        super.onResume();
        this.mView.post(new Runnable() {
            public final void run() {
                IdleControlButtonFragment.this.lambda$onResume$1$IdleControlButtonFragment();
            }
        });
    }

    public /* synthetic */ void lambda$onResume$1$IdleControlButtonFragment() {
        int[] iArr = new int[2];
        this.mView.getLocationOnScreen(iArr);
        int[] iArr2 = this.mRecordButtonLocation;
        if (iArr2[1] == 0) {
            iArr2[1] = iArr[1];
        } else if (iArr2[1] != iArr[1] && iArr2[1] != 0) {
            postEvent(15);
            this.mRecordButtonLocation[1] = iArr[1];
        }
    }

    public void onUpdate(Object obj) {
        Log.m26i(TAG, "onUpdate - data : " + obj);
        int intValue = ((Integer) obj).intValue();
        if (intValue != 5998) {
            if (intValue == 8001) {
                postEvent(1009);
                return;
            } else if (intValue != 29999) {
                switch (intValue) {
                    case Event.RECORD_START_BY_TASK_EDGE:
                    case Event.RECORD_START_BY_SVOICE:
                    case Event.RECORD_RESUME_BY_PERMISSION:
                    case Event.RECORD_START_BY_PERMISSION:
                        break;
                    default:
                        return;
                }
            } else if (!DialogFactory.isDialogVisible(getFragmentManager(), DialogFactory.SD_CARD_SELECT_DIALOG)) {
                postEvent(1009);
                return;
            } else {
                return;
            }
        }
        postEvent(1009);
    }

    private void displayRecognizerTOS(int i) {
        Log.m26i(TAG, "displayRecognizerTOS() : tosResult = " + i);
        if (i != -1 && i == 0) {
            try {
                startActivity(new Intent(getActivity(), WebTosActivity.class));
            } catch (ActivityNotFoundException e) {
                Log.m24e(TAG, "ActivityNotFoundException", (Throwable) e);
            }
        }
    }
}
