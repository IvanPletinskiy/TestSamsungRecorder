package com.sec.android.app.voicenote.p007ui.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.p007ui.AbsDialogFragment;
import com.sec.android.app.voicenote.p007ui.view.WindowFocusLayout;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.SortByDialogFragment */
public class SortByDialogFragment extends AbsDialogFragment {
    private TextView mOrder;
    private RadioGroup mOrderRadioGroup;
    private RadioGroup mSortRadioGroup;

    public static SortByDialogFragment newInstance(Bundle bundle) {
        SortByDialogFragment sortByDialogFragment = new SortByDialogFragment();
        sortByDialogFragment.setArguments(bundle);
        return sortByDialogFragment;
    }

    public void onStart() {
        super.onStart();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        final FragmentActivity activity = getActivity();
        WindowFocusLayout windowFocusLayout = (WindowFocusLayout) activity.getLayoutInflater().inflate(C0690R.layout.dialog_sort_by, (ViewGroup) null);
        this.mSortRadioGroup = (RadioGroup) windowFocusLayout.findViewById(C0690R.C0693id.sort_radio_group);
        this.mOrderRadioGroup = (RadioGroup) windowFocusLayout.findViewById(C0690R.C0693id.order_radio_group);
        this.mOrder = (TextView) windowFocusLayout.findViewById(C0690R.C0693id.order);
        final int intSettings = Settings.getIntSettings(Settings.KEY_SORT_MODE, 3);
        initCheckRadioGroup(intSettings, this.mSortRadioGroup, this.mOrderRadioGroup);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(C0690R.string.sort_by);
        builder.setNegativeButton(C0690R.string.cancel, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                SortByDialogFragment.this.lambda$onCreateDialog$0$SortByDialogFragment(dialogInterface, i);
            }
        });
        builder.setPositiveButton(C0690R.string.done, new DialogInterface.OnClickListener() {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = intSettings;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                SortByDialogFragment.this.lambda$onCreateDialog$1$SortByDialogFragment(this.f$1, dialogInterface, i);
            }
        });
        builder.setView(windowFocusLayout);
        final AlertDialog create = builder.create();
        create.setOnShowListener(new DialogInterface.OnShowListener() {
            private final /* synthetic */ Activity f$1;
            private final /* synthetic */ AlertDialog f$2;

            {
                this.f$1 = activity;
                this.f$2 = create;
            }

            public final void onShow(DialogInterface dialogInterface) {
                SortByDialogFragment.this.lambda$onCreateDialog$2$SortByDialogFragment(this.f$1, this.f$2, dialogInterface);
            }
        });
        return create;
    }

    public /* synthetic */ void lambda$onCreateDialog$0$SortByDialogFragment(DialogInterface dialogInterface, int i) {
        SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_sort_by), getResources().getString(C0690R.string.event_sort_by_cancel));
        dialogInterface.dismiss();
    }

    public /* synthetic */ void lambda$onCreateDialog$1$SortByDialogFragment(int i, DialogInterface dialogInterface, int i2) {
        int sortType = getSortType(this.mSortRadioGroup, this.mOrderRadioGroup);
        if(sortType != i) {
            Settings.setSettings(Settings.KEY_SORT_MODE, sortType);
            VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.CHANGE_SORT_MODE));
        }
        setSALog(sortType);
        dialogInterface.dismiss();
    }

    public /* synthetic */ void lambda$onCreateDialog$2$SortByDialogFragment(@SuppressLint({"InflateParams"}) Activity activity, AlertDialog alertDialog, DialogInterface dialogInterface) {
        if(activity != null) {
            alertDialog.getButton(-1).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
            alertDialog.getButton(-2).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
        }
    }

    private int getSortType(RadioGroup radioGroup, RadioGroup radioGroup2) {
        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        if(radioGroup2.getCheckedRadioButtonId() == C0690R.C0693id.asc_button) {
            if(checkedRadioButtonId == C0690R.C0693id.duration_button) {
                return 2;
            }
            if(checkedRadioButtonId != C0690R.C0693id.name_button) {
                return checkedRadioButtonId != C0690R.C0693id.time_button ? 3 : 0;
            }
            return 1;
        }
        else
            if(checkedRadioButtonId == C0690R.C0693id.duration_button) {
                return 5;
            }
            else {
                if(checkedRadioButtonId == C0690R.C0693id.name_button) {
                    return 4;
                }
                if(checkedRadioButtonId != C0690R.C0693id.time_button) {
                }
            }
            return 0;
    }


    private void initCheckRadioGroup(int i, RadioGroup radioGroup, RadioGroup radioGroup2) {
        if (i == 0) {
            radioGroup.check(C0690R.C0693id.time_button);
            radioGroup2.check(C0690R.C0693id.asc_button);
        } else if (i == 1) {
            radioGroup.check(C0690R.C0693id.name_button);
            radioGroup2.check(C0690R.C0693id.asc_button);
        } else if (i == 2) {
            radioGroup.check(C0690R.C0693id.duration_button);
            radioGroup2.check(C0690R.C0693id.asc_button);
        } else if (i == 3) {
            radioGroup.check(C0690R.C0693id.time_button);
            radioGroup2.check(C0690R.C0693id.des_button);
        } else if (i == 4) {
            radioGroup.check(C0690R.C0693id.name_button);
            radioGroup2.check(C0690R.C0693id.des_button);
        } else if (i == 5) {
            radioGroup.check(C0690R.C0693id.duration_button);
            radioGroup2.check(C0690R.C0693id.des_button);
        }
    }

    private void setSALog(int i) {
        if (i == 0) {
            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_sort_by), getResources().getString(C0690R.string.event_sort_by_done), "1");
        } else if (i == 1) {
            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_sort_by), getResources().getString(C0690R.string.event_sort_by_done), SALogProvider.QUALITY_LOW);
        } else if (i == 2) {
            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_sort_by), getResources().getString(C0690R.string.event_sort_by_done), "5");
        } else if (i == 3) {
            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_sort_by), getResources().getString(C0690R.string.event_sort_by_done), "2");
        } else if (i == 4) {
            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_sort_by), getResources().getString(C0690R.string.event_sort_by_done), SALogProvider.QUALITY_MMS);
        } else if (i == 5) {
            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_sort_by), getResources().getString(C0690R.string.event_sort_by_done), "6");
        }
    }
}
