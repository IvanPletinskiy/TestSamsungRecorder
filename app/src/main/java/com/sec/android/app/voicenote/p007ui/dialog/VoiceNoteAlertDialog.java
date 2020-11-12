package com.sec.android.app.voicenote.p007ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.CategoryRepository;
import com.sec.android.app.voicenote.p007ui.AbsDialogFragment;
import com.sec.android.app.voicenote.p007ui.actionbar.RunOptionMenu;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.p007ui.view.WindowFocusLayout;
import com.sec.android.app.voicenote.provider.DBProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.ViewProvider;
import com.sec.android.app.voicenote.service.BookmarkHolder;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.MetadataRepository;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.VoiceNoteAlertDialog */
public class VoiceNoteAlertDialog extends AbsDialogFragment implements Observer, View.OnClickListener {
    private static final String TAG = "VoiceNoteAlertDialog";
    public static final int UNDEFINED = -1;
    private Button mCancelButton;
    private TextView mDescription;
    private AlertDialog mDialog;
    private Button mDiscardButton;
    private View mDividerOne;
    private View mDividerTwo;
    private DialogFactory.DialogResultListener mInterface = null;
    private VoiceNoteObservable mObservable = VoiceNoteObservable.getInstance();
    private ProgressBar mProgress;
    private TextView mSaveAsNewFile;
    private Button mSaveButton;
    private TextView mSaveOriginalFile;

    private int getButtonName(int i) {
        switch (i) {
            case C0690R.C0693id.button_cancel:
                return -2;
            case C0690R.C0693id.button_discard:
                return -3;
            case C0690R.C0693id.button_save:
                return -1;
            default:
                return 0;
        }
    }

    public static VoiceNoteAlertDialog newInstance(Bundle bundle, DialogFactory.DialogResultListener dialogResultListener) {
        VoiceNoteAlertDialog voiceNoteAlertDialog = new VoiceNoteAlertDialog();
        voiceNoteAlertDialog.setArguments(bundle);
        voiceNoteAlertDialog.setListener(dialogResultListener);
        return voiceNoteAlertDialog;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mObservable.addObserver(this);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        this.mDialog = initDialogView().create();
        this.mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public final void onShow(DialogInterface dialogInterface) {
                VoiceNoteAlertDialog.this.lambda$onCreateDialog$0$VoiceNoteAlertDialog(dialogInterface);
            }
        });
        ListView listView = this.mDialog.getListView();
        if (listView != null) {
            ViewProvider.setBackgroundListView(getActivity(), listView);
            listView.setSelector(ContextCompat.getDrawable(getActivity(), C0690R.C0692drawable.voice_ripple_rectangle));
        }
        return this.mDialog;
    }

    public /* synthetic */ void lambda$onCreateDialog$0$VoiceNoteAlertDialog(DialogInterface dialogInterface) {
        if (getActivity() != null && this.mDialog != null && isAdded()) {
            this.mDialog.getButton(-1).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
            this.mDialog.getButton(-2).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
            this.mDialog.getButton(-3).setTextColor(getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
        }
    }

    private AlertDialog.Builder initDialogView() {
        WindowFocusLayout windowFocusLayout = (WindowFocusLayout) getActivity().getLayoutInflater().inflate(C0690R.layout.dialog_voicenote_alert, (ViewGroup) null);
        this.mSaveButton = (Button) windowFocusLayout.findViewById(C0690R.C0693id.button_save);
        this.mCancelButton = (Button) windowFocusLayout.findViewById(C0690R.C0693id.button_cancel);
        this.mDiscardButton = (Button) windowFocusLayout.findViewById(C0690R.C0693id.button_discard);
        this.mDescription = (TextView) windowFocusLayout.findViewById(C0690R.C0693id.dialog_description);
        this.mProgress = (ProgressBar) windowFocusLayout.findViewById(C0690R.C0693id.progress);
        this.mSaveAsNewFile = (TextView) windowFocusLayout.findViewById(C0690R.C0693id.save_as_new_file);
        this.mSaveOriginalFile = (TextView) windowFocusLayout.findViewById(C0690R.C0693id.save_as_original_file);
        this.mDividerOne = windowFocusLayout.findViewById(C0690R.C0693id.divider_1);
        this.mDividerTwo = windowFocusLayout.findViewById(C0690R.C0693id.divider_2);
        LinearLayout linearLayout = (LinearLayout) windowFocusLayout.findViewById(C0690R.C0693id.layout_button);
        LinearLayout linearLayout2 = (LinearLayout) windowFocusLayout.findViewById(C0690R.C0693id.layout_item_save);
        int[] intArray = getArguments().getIntArray(DialogFactory.BUNDLE_MESSAGE_LIST_IDS);
        int i = 1;
        if (intArray != null) {
            linearLayout2.setVisibility(0);
            int length = intArray.length;
            String[] strArr = new String[length];
            for (int i2 = 0; i2 < length; i2++) {
                strArr[i2] = getString(intArray[i2]);
            }
            this.mSaveAsNewFile.setVisibility(0);
            this.mSaveOriginalFile.setVisibility(0);
            this.mSaveAsNewFile.setText(strArr[0]);
            this.mSaveOriginalFile.setText(strArr[1]);
            this.mSaveAsNewFile.setOnClickListener(this);
            this.mSaveOriginalFile.setOnClickListener(this);
        }
        this.mSaveButton.setOnClickListener(this);
        this.mCancelButton.setOnClickListener(this);
        this.mDiscardButton.setOnClickListener(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(windowFocusLayout);
        int i3 = getArguments().getInt(DialogFactory.BUNDLE_TITLE_ID, -1);
        int i4 = getArguments().getInt(DialogFactory.BUNDLE_MESSAGE_ID, -1);
        int i5 = getArguments().getInt(DialogFactory.BUNDLE_POSITIVE_BTN_ID, -1);
        int i6 = getArguments().getInt(DialogFactory.BUNDLE_NEGATIVE_BTN_ID, -1);
        int i7 = getArguments().getInt(DialogFactory.BUNDLE_NEUTRAL_BTN_ID, -1);
        String string = getArguments().getString(DialogFactory.BUNDLE_WORD, (String) null);
        if (i3 != -1) {
            builder.setTitle(i3);
        }
        if (i4 != -1) {
            this.mDescription.setText(i4);
        } else if (string != null) {
            this.mDescription.setText(string);
        } else {
            this.mDescription.setVisibility(8);
        }
        if (i5 != -1) {
            this.mSaveButton.setText(i5);
            i = 0;
        } else {
            this.mSaveButton.setVisibility(8);
            this.mDividerTwo.setVisibility(8);
        }
        if (i6 != -1) {
            this.mDiscardButton.setText(i6);
        } else {
            i++;
            this.mDiscardButton.setVisibility(8);
            this.mDividerOne.setVisibility(8);
        }
        if (i7 != -1) {
            this.mCancelButton.setText(i7);
        } else {
            i++;
            this.mCancelButton.setVisibility(8);
            this.mDividerOne.setVisibility(8);
        }
        if (i == 3) {
            linearLayout.setVisibility(8);
        }
        return builder;
    }

    private void saveAsNewFile() {
        Log.m26i(TAG, "onClick which : Save As new File");
        int recordMode = MetadataRepository.getInstance().getRecordMode();
        this.mObservable.notifyObservers(Integer.valueOf(Event.HIDE_DIALOG));
        String originalFilePath = Engine.getInstance().getOriginalFilePath();
        if (originalFilePath == null) {
            Engine.getInstance().setUserSettingName(DBProvider.getInstance().createNewFileName(Settings.getIntSettings(Settings.KEY_PLAY_MODE, 1)));
        } else {
            String name = new File(originalFilePath).getName();
            Engine.getInstance().setUserSettingName(name.substring(0, name.lastIndexOf(46)));
        }
        Engine.getInstance().setCategoryID((long) CategoryRepository.getCategoryId(recordMode));
        Engine.getInstance().stopPlay(false);
        Engine.getInstance().stopRecord(true, false);
        Engine.getInstance().resetTrimTime();
        if (recordMode == 0) {
            recordMode = Settings.getIntSettings(Settings.KEY_PLAY_MODE, 1);
        }
        if (recordMode == 4) {
            this.mObservable.notifyObservers(Integer.valueOf(Event.RECORD_STOP_DELAYED));
        } else {
            this.mObservable.notifyObservers(3);
        }
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.mDialog.dismiss();
        }
        SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_save_edited), getResources().getString(C0690R.string.event_edited_popup_new_file));
    }

    private void saveToOriginalFile() {
        Log.m26i(TAG, "onClick which : Save Original File");
        MetadataRepository instance = MetadataRepository.getInstance();
        int recordMode = instance.getRecordMode();
        BookmarkHolder.getInstance().set(Engine.getInstance().getOriginalFilePath(), instance.getBookmarkCount() > 0);
        long labelIdByPath = DBProvider.getInstance().getLabelIdByPath(Engine.getInstance().getOriginalFilePath());
        Engine.getInstance().setCategoryID(labelIdByPath);
        long stopRecord = Engine.getInstance().stopRecord(false, false);
        Log.m26i(TAG, "save_to_original - id : " + stopRecord + " label_id : " + labelIdByPath);
        this.mObservable.notifyObservers(Integer.valueOf(Event.HIDE_DIALOG));
        Engine.getInstance().stopPlay(false);
        Engine.getInstance().resetTrimTime();
        if (recordMode == 0) {
            recordMode = Settings.getIntSettings(Settings.KEY_PLAY_MODE, 1);
        }
        if (recordMode == 4) {
            this.mObservable.notifyObservers(Integer.valueOf(Event.RECORD_STOP_DELAYED));
        } else {
            this.mObservable.notifyObservers(3);
        }
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.mDialog.dismiss();
        }
        SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_save_edited), getResources().getString(C0690R.string.event_edited_popup_origin_file));
    }

    public void onResume() {
        super.onResume();
    }

    public void onDestroy() {
        this.mObservable.deleteObserver(this);
        super.onDestroy();
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.mObservable.notifyObservers(Integer.valueOf(Event.HIDE_DIALOG));
    }

    private void setListener(DialogFactory.DialogResultListener dialogResultListener) {
        this.mInterface = dialogResultListener;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setSALog(java.lang.String r8, int r9) {
        /*
            r7 = this;
            if (r8 != 0) goto L_0x0003
            return
        L_0x0003:
            int r0 = r8.hashCode()
            r1 = 3
            r2 = 2
            r3 = 1
            r4 = 0
            r5 = -1
            switch(r0) {
                case 487913235: goto L_0x002e;
                case 545201068: goto L_0x0024;
                case 1046437747: goto L_0x001a;
                case 1853190547: goto L_0x0010;
                default: goto L_0x000f;
            }
        L_0x000f:
            goto L_0x0038
        L_0x0010:
            java.lang.String r0 = "TranslationCancelDialog"
            boolean r8 = r8.equals(r0)
            if (r8 == 0) goto L_0x0038
            r8 = r3
            goto L_0x0039
        L_0x001a:
            java.lang.String r0 = "RecordCancelDialog"
            boolean r8 = r8.equals(r0)
            if (r8 == 0) goto L_0x0038
            r8 = r4
            goto L_0x0039
        L_0x0024:
            java.lang.String r0 = "EditCancelDialog"
            boolean r8 = r8.equals(r0)
            if (r8 == 0) goto L_0x0038
            r8 = r2
            goto L_0x0039
        L_0x002e:
            java.lang.String r0 = "StorageChangeDialog"
            boolean r8 = r8.equals(r0)
            if (r8 == 0) goto L_0x0038
            r8 = r1
            goto L_0x0039
        L_0x0038:
            r8 = r5
        L_0x0039:
            r0 = -3
            r6 = -2
            if (r8 == 0) goto L_0x0163
            if (r8 == r3) goto L_0x0112
            if (r8 == r2) goto L_0x00bf
            if (r8 == r1) goto L_0x0045
            goto L_0x01b1
        L_0x0045:
            r8 = 2131755483(0x7f1001db, float:1.9141847E38)
            r0 = 2131755484(0x7f1001dc, float:1.9141849E38)
            java.lang.String r1 = "storage"
            if (r9 == r6) goto L_0x0089
            if (r9 == r5) goto L_0x0053
            goto L_0x01b1
        L_0x0053:
            int r9 = com.sec.android.app.voicenote.provider.Settings.getIntSettings(r1, r4)
            if (r9 != 0) goto L_0x0071
            android.content.res.Resources r9 = r7.getResources()
            java.lang.String r8 = r9.getString(r8)
            android.content.res.Resources r9 = r7.getResources()
            r0 = 2131755271(0x7f100107, float:1.9141417E38)
            java.lang.String r9 = r9.getString(r0)
            com.sec.android.app.voicenote.provider.SALogProvider.insertSALog(r8, r9)
            goto L_0x01b1
        L_0x0071:
            android.content.res.Resources r8 = r7.getResources()
            java.lang.String r8 = r8.getString(r0)
            android.content.res.Resources r9 = r7.getResources()
            r0 = 2131755272(0x7f100108, float:1.9141419E38)
            java.lang.String r9 = r9.getString(r0)
            com.sec.android.app.voicenote.provider.SALogProvider.insertSALog(r8, r9)
            goto L_0x01b1
        L_0x0089:
            int r9 = com.sec.android.app.voicenote.provider.Settings.getIntSettings(r1, r4)
            if (r9 != 0) goto L_0x00a7
            android.content.res.Resources r9 = r7.getResources()
            java.lang.String r8 = r9.getString(r8)
            android.content.res.Resources r9 = r7.getResources()
            r0 = 2131755268(0x7f100104, float:1.914141E38)
            java.lang.String r9 = r9.getString(r0)
            com.sec.android.app.voicenote.provider.SALogProvider.insertSALog(r8, r9)
            goto L_0x01b1
        L_0x00a7:
            android.content.res.Resources r8 = r7.getResources()
            java.lang.String r8 = r8.getString(r0)
            android.content.res.Resources r9 = r7.getResources()
            r0 = 2131755269(0x7f100105, float:1.9141413E38)
            java.lang.String r9 = r9.getString(r0)
            com.sec.android.app.voicenote.provider.SALogProvider.insertSALog(r8, r9)
            goto L_0x01b1
        L_0x00bf:
            r8 = 2131755473(0x7f1001d1, float:1.9141826E38)
            if (r9 == r0) goto L_0x00fa
            if (r9 == r6) goto L_0x00e2
            if (r9 == r5) goto L_0x00ca
            goto L_0x01b1
        L_0x00ca:
            android.content.res.Resources r9 = r7.getResources()
            java.lang.String r8 = r9.getString(r8)
            android.content.res.Resources r9 = r7.getResources()
            r0 = 2131755156(0x7f100094, float:1.9141183E38)
            java.lang.String r9 = r9.getString(r0)
            com.sec.android.app.voicenote.provider.SALogProvider.insertSALog(r8, r9)
            goto L_0x01b1
        L_0x00e2:
            android.content.res.Resources r9 = r7.getResources()
            java.lang.String r8 = r9.getString(r8)
            android.content.res.Resources r9 = r7.getResources()
            r0 = 2131755155(0x7f100093, float:1.9141181E38)
            java.lang.String r9 = r9.getString(r0)
            com.sec.android.app.voicenote.provider.SALogProvider.insertSALog(r8, r9)
            goto L_0x01b1
        L_0x00fa:
            android.content.res.Resources r9 = r7.getResources()
            java.lang.String r8 = r9.getString(r8)
            android.content.res.Resources r9 = r7.getResources()
            r0 = 2131755154(0x7f100092, float:1.914118E38)
            java.lang.String r9 = r9.getString(r0)
            com.sec.android.app.voicenote.provider.SALogProvider.insertSALog(r8, r9)
            goto L_0x01b1
        L_0x0112:
            r8 = 2131755480(0x7f1001d8, float:1.914184E38)
            if (r9 == r0) goto L_0x014c
            if (r9 == r6) goto L_0x0135
            if (r9 == r5) goto L_0x011d
            goto L_0x01b1
        L_0x011d:
            android.content.res.Resources r9 = r7.getResources()
            java.lang.String r8 = r9.getString(r8)
            android.content.res.Resources r9 = r7.getResources()
            r0 = 2131755223(0x7f1000d7, float:1.914132E38)
            java.lang.String r9 = r9.getString(r0)
            com.sec.android.app.voicenote.provider.SALogProvider.insertSALog(r8, r9)
            goto L_0x01b1
        L_0x0135:
            android.content.res.Resources r9 = r7.getResources()
            java.lang.String r8 = r9.getString(r8)
            android.content.res.Resources r9 = r7.getResources()
            r0 = 2131755222(0x7f1000d6, float:1.9141317E38)
            java.lang.String r9 = r9.getString(r0)
            com.sec.android.app.voicenote.provider.SALogProvider.insertSALog(r8, r9)
            goto L_0x01b1
        L_0x014c:
            android.content.res.Resources r9 = r7.getResources()
            java.lang.String r8 = r9.getString(r8)
            android.content.res.Resources r9 = r7.getResources()
            r0 = 2131755221(0x7f1000d5, float:1.9141315E38)
            java.lang.String r9 = r9.getString(r0)
            com.sec.android.app.voicenote.provider.SALogProvider.insertSALog(r8, r9)
            goto L_0x01b1
        L_0x0163:
            r8 = 2131755481(0x7f1001d9, float:1.9141842E38)
            if (r9 == r0) goto L_0x019b
            if (r9 == r6) goto L_0x0184
            if (r9 == r5) goto L_0x016d
            goto L_0x01b1
        L_0x016d:
            android.content.res.Resources r9 = r7.getResources()
            java.lang.String r8 = r9.getString(r8)
            android.content.res.Resources r9 = r7.getResources()
            r0 = 2131755265(0x7f100101, float:1.9141404E38)
            java.lang.String r9 = r9.getString(r0)
            com.sec.android.app.voicenote.provider.SALogProvider.insertSALog(r8, r9)
            goto L_0x01b1
        L_0x0184:
            android.content.res.Resources r9 = r7.getResources()
            java.lang.String r8 = r9.getString(r8)
            android.content.res.Resources r9 = r7.getResources()
            r0 = 2131755264(0x7f100100, float:1.9141402E38)
            java.lang.String r9 = r9.getString(r0)
            com.sec.android.app.voicenote.provider.SALogProvider.insertSALog(r8, r9)
            goto L_0x01b1
        L_0x019b:
            android.content.res.Resources r9 = r7.getResources()
            java.lang.String r8 = r9.getString(r8)
            android.content.res.Resources r9 = r7.getResources()
            r0 = 2131755263(0x7f1000ff, float:1.91414E38)
            java.lang.String r9 = r9.getString(r0)
            com.sec.android.app.voicenote.provider.SALogProvider.insertSALog(r8, r9)
        L_0x01b1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.dialog.VoiceNoteAlertDialog.setSALog(java.lang.String, int):void");
    }

    public void update(Observable observable, Object obj) {
        AlertDialog alertDialog;
        int intValue = ((Integer) obj).intValue();
        Log.m26i(TAG, "update - data : " + intValue);
        if (intValue == 998) {
            dismissAllowingStateLoss();
        } else if (intValue == 7010) {
            AlertDialog alertDialog2 = this.mDialog;
            if (alertDialog2 != null && alertDialog2.isShowing()) {
                this.mSaveButton.setVisibility(8);
                this.mProgress.setVisibility(0);
                this.mDialog.setCancelable(false);
                updateLayoutDialog(false);
            }
        } else if (intValue == 7011 && (alertDialog = this.mDialog) != null && alertDialog.isShowing()) {
            this.mObservable.notifyObservers(Integer.valueOf(Event.HIDE_DIALOG));
            this.mSaveButton.setVisibility(0);
            this.mProgress.setVisibility(8);
            this.mDialog.setCancelable(true);
            updateLayoutDialog(true);
            dismissAllowingStateLoss();
        }
    }

    private void updateLayoutDialog(boolean z) {
        this.mCancelButton.setEnabled(z);
        this.mDiscardButton.setEnabled(z);
        this.mDialog.setCancelable(z);
    }

    public void onClick(View view) {
        if (!isAdded()) {
            Log.m22e(TAG, "OnCLick - Fragment dialog is not attached to Activity ");
            return;
        }
        int id = view.getId();
        if (id == C0690R.C0693id.save_as_new_file) {
            saveAsNewFile();
        } else if (id == C0690R.C0693id.save_as_original_file) {
            saveToOriginalFile();
        } else {
            setSALog(getArguments().getString(DialogFactory.BUNDLE_DIALOG_TAG, (String) null), getButtonName(id));
            int i = Event.HIDE_DIALOG;
            switch (id) {
                case C0690R.C0693id.button_cancel:
                    i = getArguments().getInt(DialogFactory.BUNDLE_NEUTRAL_BTN_EVENT, Event.HIDE_DIALOG);
                    break;
                case C0690R.C0693id.button_discard:
                    i = getArguments().getInt(DialogFactory.BUNDLE_NEGATIVE_BTN_EVENT, Event.HIDE_DIALOG);
                    break;
                case C0690R.C0693id.button_save:
                    i = getArguments().getInt(DialogFactory.BUNDLE_POSITIVE_BTN_EVENT, Event.HIDE_DIALOG);
                    break;
            }
            handleButtonClick(id, i);
        }
    }

    private void handleButtonClick(int i, int i2) {
        Log.m26i(TAG, "onClick event : " + i2);
        if (i2 == 967) {
            this.mObservable.notifyObservers(Integer.valueOf(Event.HIDE_DIALOG));
            if (Settings.getIntSettings(Settings.KEY_STORAGE, 0) == 0) {
                Settings.setSettings(Settings.KEY_STORAGE, 1);
                Settings.setSettings(Settings.KEY_SDCARD_PREVIOUS_STATE, 1);
            } else {
                Settings.setSettings(Settings.KEY_STORAGE, 0);
                Settings.setSettings(Settings.KEY_SDCARD_PREVIOUS_STATE, 1);
            }
            if (this.mInterface != null && getArguments() != null) {
                Bundle arguments = getArguments();
                arguments.putInt("result_code", Settings.getIntSettings(Settings.KEY_STORAGE, 0));
                this.mInterface.onDialogResult(this, arguments);
            }
        } else if (i2 == 971) {
            this.mObservable.notifyObservers(Integer.valueOf(Event.HIDE_DIALOG));
            Engine.getInstance().cancelRecord();
            Engine.getInstance().stopPlay(false);
            VoiceNoteApplication.saveEvent(4);
            VoiceNoteApplication.saveScene(0);
            this.mObservable.notifyObservers(4);
            FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.finish();
            }
        } else if (i2 == 5007) {
            RunOptionMenu.getInstance().editSave();
            this.mObservable.notifyObservers(Integer.valueOf(Event.HIDE_DIALOG));
        } else if (i2 != 5008) {
            switch (i2) {
                case 1004:
                case Event.RECORD_STOP_DELAYED:
                    Engine.getInstance().setCategoryID((long) CategoryRepository.getCategoryId(MetadataRepository.getInstance().getRecordMode()));
                    Engine.getInstance().stopPlay();
                    Engine.getInstance().stopRecord(true, false);
                    Settings.setSettings(Settings.KEY_LIST_MODE, 0);
                    this.mObservable.notifyObservers(Integer.valueOf(i2));
                    return;
                case 1006:
                    Engine.getInstance().stopPlay();
                    Engine.getInstance().cancelRecord();
                    this.mObservable.notifyObservers(Integer.valueOf(Event.HIDE_DIALOG));
                    this.mObservable.notifyObservers(Integer.valueOf(i2));
                    return;
                default:
                    if (this.mInterface == null || getArguments() == null) {
                        Log.m26i(TAG, "onClick - which : " + getButtonName(i));
                    } else {
                        Log.m26i(TAG, "onClick - which : " + getButtonName(i) + " request code : " + getArguments().getInt(DialogFactory.BUNDLE_REQUEST_CODE, -1));
                        getArguments().putInt("result_code", i);
                        this.mInterface.onDialogResult(this, getArguments());
                    }
                    this.mObservable.notifyObservers(Integer.valueOf(Event.HIDE_DIALOG));
                    return;
            }
        } else {
            String originalFilePath = Engine.getInstance().getOriginalFilePath();
            long idByPath = DBProvider.getInstance().getIdByPath(originalFilePath);
            Engine.getInstance().cancelRecord();
            Engine.getInstance().stopPlay();
            Engine.getInstance().clearContentItem();
            this.mObservable.notifyObservers(Integer.valueOf(Event.HIDE_DIALOG));
            if (Engine.getInstance().startPlay(originalFilePath, idByPath, true) != -115) {
                Engine.getInstance().seekTo(0);
                Engine.getInstance().pausePlay();
                this.mObservable.notifyObservers(Integer.valueOf(Event.OPEN_FULL_PLAYER));
                this.mObservable.notifyObservers(Integer.valueOf(Event.PLAY_PAUSE));
                return;
            }
            this.mObservable.notifyObservers(3);
        }
    }
}
