package com.sec.android.app.voicenote.p007ui;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.AndroidForWork;
import com.sec.android.app.voicenote.common.util.TrashHelper;
import com.sec.android.app.voicenote.common.util.TwoPhoneModeUtils;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.p007ui.view.PopUpPreference;
import com.sec.android.app.voicenote.provider.DesktopModeProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.SecureFolderProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.StorageProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.receiver.VoiceNoteIntentReceiver;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

import java.util.Observable;
import java.util.Observer;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.AndroidResources;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import androidx.recyclerview.widget.RecyclerView;

/* renamed from: com.sec.android.app.voicenote.ui.PreferenceSettingFragment */
public class PreferenceSettingFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener, Observer {
    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    private static final int MSG_UPDATE_ABOUT_VIEW = 1;
    private static final int MSG_UPDATE_STEREO_VIEW = 0;
    private static final String TAG = "PreferenceSettingFragment";
    private Preference mAboutPreference;
    @SuppressLint({"HandlerLeak"})
    private Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            View view = PreferenceSettingFragment.this.getView();
            Log.m26i(PreferenceSettingFragment.TAG, "handleMessage - what : " + message.what);
            int i = message.what;
            if (i == 0) {
                if (view != null) {
//                    SwitchCompat unused = PreferenceSettingFragment.this.mStereoSwitch = (SwitchCompat) view.findViewById(AndroidResources.ANDROID_R_SWITCH_WIDGET);
                }
                if (PreferenceSettingFragment.this.mStereoSwitch == null) {
                    Log.m22e(PreferenceSettingFragment.TAG, "stereo layout view update fail.");
                } else if (DesktopModeProvider.isDesktopMode()) {
                    PreferenceSettingFragment.this.mStereoSwitch.setEnabled(false);
                } else {
                    PreferenceSettingFragment.this.mStereoSwitch.setChecked(Settings.getBooleanSettings(Settings.KEY_REC_STEREO, false));
                    PreferenceSettingFragment.this.mStereoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
//                            PreferenceSettingFragment.C08291.this.lambda$handleMessage$0$PreferenceSettingFragment$1(compoundButton, z);
                        }
                    });
                }
            } else if (i == 1) {
                boolean equals = Settings.getStringSettings(Settings.KEY_UPDATE_CHECK_RESULT_CODE, "1").equals("2");
                if (view != null) {
                    LinearLayout linearLayout = (LinearLayout) view.findViewById(C0690R.C0693id.about_badge_bg);
                    if (linearLayout == null) {
                        Log.m22e(PreferenceSettingFragment.TAG, "version update icon view update fail.");
                    } else if (equals) {
                        linearLayout.setVisibility(0);
                    } else {
                        linearLayout.setVisibility(8);
                    }
                }
            }
        }

        public /* synthetic */ void lambda$handleMessage$0$PreferenceSettingFragment$1(CompoundButton compoundButton, boolean z) {
            Settings.setSettings(Settings.KEY_REC_STEREO, z);
            PreferenceSettingFragment preferenceSettingFragment = PreferenceSettingFragment.this;
            preferenceSettingFragment.setSummaryString(preferenceSettingFragment.mStereoPreference);
            SALogProvider.insertSALog(PreferenceSettingFragment.this.getActivity().getResources().getString(C0690R.string.screen_settings), PreferenceSettingFragment.this.getActivity().getResources().getString(C0690R.string.event_stereo_switch), z ? "1" : "0");
        }
    };
    private PopUpPreference mPopUpQualityPreference;
    private PopUpPreference mPopUpStoragePreference;
    /* access modifiers changed from: private */
    public Preference mStereoPreference;
    /* access modifiers changed from: private */
    public SwitchCompat mStereoSwitch = null;
    private SwitchPreferenceCompat mSwitchCallPreference;
    private SwitchPreferenceCompat mSwitchPlayPreference;
    private SwitchPreferenceCompat mSwitchTurnOnTrash;
    private PreferenceScreen mVRPreferenceScreen;
    private VoiceNoteIntentReceiver mVoiceNoteIntentReceiver = null;

    public void onCreatePreferences(Bundle bundle, String str) {
        addPreferencesFromResource(C0690R.xml.settings_preference);
//        seslSetRoundedCornerType(2);
        initPreferenceScreen();
        VoiceNoteObservable.getInstance().addObserver(this);
        this.mVoiceNoteIntentReceiver = new VoiceNoteIntentReceiver(getContext());
        this.mVoiceNoteIntentReceiver.registerListenerForSetting();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        SwitchPreferenceCompat switchPreferenceCompat = this.mSwitchCallPreference;
        if (switchPreferenceCompat != null && this.mSwitchPlayPreference != null && this.mSwitchTurnOnTrash != null) {
            switchPreferenceCompat.setLayoutResource(C0690R.layout.preference_switch_layout);
            this.mSwitchPlayPreference.setLayoutResource(C0690R.layout.preference_switch_layout);
            this.mSwitchTurnOnTrash.setLayoutResource(C0690R.layout.preference_switch_layout);
        }
    }

    public void onResume() {
        Log.m26i(TAG, "onResume");
        super.onResume();
        updatePopupQualityPreference();
        updateStereoPreference();
        updateBlockCallPreference();
        updatePlayContinuouslyPreference();
        updateStoragePreference();
        updateTrashStatusPreference();
        updateAboutPreference();
    }

    public void onDestroy() {
        Log.m26i(TAG, "onDestroy");
        VoiceNoteObservable.getInstance().deleteObserver(this);
        DialogFactory.clearDialogByTag(getFragmentManager(), DialogFactory.SD_CARD_SELECT_DIALOG);
        this.mVoiceNoteIntentReceiver.unregisterListenerForSetting();
        this.mVoiceNoteIntentReceiver = null;
        this.mHandler = null;
        super.onDestroy();
    }

    private void initPreferenceScreen() {
        Log.m26i(TAG, "initPreferenceScreen");
//        this.mVRPreferenceScreen = getPreferenceScreen();
        this.mPopUpQualityPreference = (PopUpPreference) findPreference(Settings.KEY_REC_QUALITY);
        updatePopupQualityPreference();
//        this.mStereoPreference = findPreference(Settings.KEY_REC_STEREO);
        updateStereoPreference();
        this.mSwitchCallPreference = (SwitchPreferenceCompat) findPreference(Settings.KEY_REC_CALL_REJECT);
        updateBlockCallPreference();
        this.mSwitchPlayPreference = (SwitchPreferenceCompat) findPreference(Settings.KEY_PLAY_CONTINUOUSLY);
        updatePlayContinuouslyPreference();
        this.mPopUpStoragePreference = (PopUpPreference) findPreference(Settings.KEY_STORAGE);
        updateStoragePreference();
        this.mSwitchTurnOnTrash = (SwitchPreferenceCompat) findPreference(Settings.KEY_TRASH_IS_TURN_ON);
//        this.mSwitchTurnOnTrash.seslSetSummaryColor(getActivity().getColor(C0690R.C0691color.listview_sub_text_color));
        this.mSwitchTurnOnTrash.setSummary((CharSequence) getActivity().getResources().getQuantityString(C0690R.plurals.trash_keep_deleted_recording_before_deleted_forever, TrashHelper.getInstance().getKeepInTrashDays(), new Object[]{Integer.valueOf(TrashHelper.getInstance().getKeepInTrashDays())}));
        updateTrashStatusPreference();
//        this.mAboutPreference = findPreference(Settings.KEY_ABOUT);
        updateAboutPreference();
    }

    private void updatePopupQualityPreference() {
        PopUpPreference popUpPreference = this.mPopUpQualityPreference;
        if (popUpPreference != null) {
            popUpPreference.setSummary(popUpPreference.getSavedPosition());
//            this.mPopUpQualityPreference.seslSetSummaryColor(ContextCompat.getColor(getActivity(), C0690R.C0691color.settings_spinner_item_text_checked_color));
            return;
        }
        Log.m22e(TAG, "recording quality not exist !!");
    }

    private void updateStereoPreference() {
        Preference preference = this.mStereoPreference;
        if (preference == null) {
            Log.m22e(TAG, "recording stereo not exist !!");
        } else if (!VoiceNoteFeature.FLAG_SUPPORT_STEREO) {
            this.mVRPreferenceScreen.removePreference(preference);
        } else if (DesktopModeProvider.isDesktopMode()) {
            setSummaryString(this.mStereoPreference);
            Preference preference2 = this.mStereoPreference;
            preference2.setTitle((CharSequence) getTitleString(preference2));
            this.mStereoPreference.setEnabled(false);
        } else if (Engine.getInstance().isBluetoothSCOConnected()) {
            this.mVRPreferenceScreen.removePreference(this.mStereoPreference);
            Settings.setSettings(Settings.KEY_BLUETOOTH_SCO_CONNECT, true);
        } else {
            if (!Settings.getBooleanSettings(Settings.KEY_BLUETOOTH_SCO_CONNECT, true)) {
                this.mVRPreferenceScreen.addPreference(this.mStereoPreference);
                Settings.setSettings(Settings.KEY_BLUETOOTH_SCO_CONNECT, false);
            }
            setSummaryString(this.mStereoPreference);
            this.mStereoPreference.setOnPreferenceClickListener(this);
            Preference preference3 = this.mStereoPreference;
            preference3.setTitle((CharSequence) getTitleString(preference3));
            this.mHandler.removeMessages(0);
            this.mHandler.sendEmptyMessageDelayed(0, 30);
        }
    }

    private void updateBlockCallPreference() {
        Log.m26i(TAG, "updateBlockCallPreference");
        if (this.mSwitchCallPreference == null) {
            Log.m22e(TAG, "recording call reject not exist !!");
        } else if (TwoPhoneModeUtils.getInstance().IsTwoPhoneMode(VoiceNoteApplication.getApplication()) || AndroidForWork.getInstance().isAndroidForWorkMode(getActivity())) {
//            this.mVRPreferenceScreen.removePreference(this.mSwitchCallPreference);
        } else if (!Engine.getInstance().isSupportBlockCall() || SecureFolderProvider.isInSecureFolder()) {
//            this.mVRPreferenceScreen.removePreference(this.mSwitchCallPreference);
        } else {
            this.mSwitchCallPreference.setChecked(Settings.getBooleanSettings(Settings.KEY_REC_CALL_REJECT, false));
//            this.mSwitchCallPreference.setOnPreferenceClickListener(this);
//            this.mSwitchCallPreference.setOnPreferenceChangeListener(this);
        }
    }

    private void updatePlayContinuouslyPreference() {
        SwitchPreferenceCompat switchPreferenceCompat = this.mSwitchPlayPreference;
        if (switchPreferenceCompat != null) {
            switchPreferenceCompat.setChecked(Settings.getBooleanSettings(Settings.KEY_PLAY_CONTINUOUSLY, false));
//            this.mSwitchPlayPreference.setOnPreferenceChangeListener(this);
            return;
        }
        Log.m22e(TAG, "play continuously not exist !!");
    }

    private void updateStoragePreference() {
        if (this.mPopUpStoragePreference != null) {
            String externalStorageStateSd = StorageProvider.getExternalStorageStateSd();
            if (externalStorageStateSd == null || !externalStorageStateSd.equals("mounted") || StorageProvider.isSdCardWriteRestricted(getContext()) || AndroidForWork.getInstance().isAndroidForWorkMode(getActivity())) {
//                this.mVRPreferenceScreen.removePreference(this.mPopUpStoragePreference);
                return;
            }
//            this.mVRPreferenceScreen.addPreference(this.mPopUpStoragePreference);
            PopUpPreference popUpPreference = this.mPopUpStoragePreference;
            popUpPreference.setSummary((CharSequence) popUpPreference.getEntry());
            this.mPopUpStoragePreference.setSelectedItemSpinner();
//            this.mPopUpStoragePreference.seslSetSummaryColor(ContextCompat.getColor(getActivity(), C0690R.C0691color.settings_spinner_item_text_checked_color));
            if (SecureFolderProvider.isSecureFolderSupported()) {
                SecureFolderProvider.getKnoxMenuList(getActivity());
                if (SecureFolderProvider.isInsideSecureFolder()) {
                    this.mPopUpStoragePreference.setEnabled(false);
                    return;
                }
                return;
            }
            return;
        }
        Log.m22e(TAG, "storage not exist !!");
    }

    private void updateTrashStatusPreference() {
        SwitchPreferenceCompat switchPreferenceCompat = this.mSwitchTurnOnTrash;
        if (switchPreferenceCompat != null) {
            switchPreferenceCompat.setChecked(Settings.getBooleanSettings(Settings.KEY_TRASH_IS_TURN_ON, false));
//            this.mSwitchTurnOnTrash.setOnPreferenceChangeListener(this);
            return;
        }
        Log.m22e(TAG, "trash not exist !!");
    }

    private void updateAboutPreference() {
        if (this.mAboutPreference != null) {
            this.mHandler.removeMessages(1);
            this.mHandler.sendEmptyMessageDelayed(1, 30);
            this.mAboutPreference.setTitle((CharSequence) getString(C0690R.string.about_voice_recorder));
            this.mAboutPreference.setOnPreferenceClickListener(this);
//            this.mAboutPreference.seslSetRoundedBg(15);
            return;
        }
        Log.m22e(TAG, "about not exist !!");
    }

    private SpannableStringBuilder getTitleString(Preference preference) {
        ForegroundColorSpan foregroundColorSpan;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        String key = preference.getKey();
        if (((key.hashCode() == -536339833 && key.equals(Settings.KEY_REC_STEREO)) ? (char) 0 : 65535) == 0) {
            if (DesktopModeProvider.isDesktopMode() || Engine.getInstance().isBluetoothSCOConnected()) {
                foregroundColorSpan = new ForegroundColorSpan(getActivity().getColor(C0690R.C0691color.settings_spinner_item_color_dim));
            } else {
                foregroundColorSpan = new ForegroundColorSpan(getActivity().getColor(C0690R.C0691color.settings_spinner_item_color));
            }
            spannableStringBuilder.insert(0, getString(C0690R.string.recording_stereo));
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, spannableStringBuilder.length(), 33);
        }
        return spannableStringBuilder;
    }

    /* access modifiers changed from: private */
    public void setSummaryString(Preference preference) {
        if (!preference.getKey().equals(Settings.KEY_REC_STEREO)) {
            return;
        }
        if (!Settings.getBooleanSettings(Settings.KEY_REC_STEREO, false) || DesktopModeProvider.isDesktopMode() || Engine.getInstance().isBluetoothSCOConnected()) {
            preference.setSummary((CharSequence) null);
            if (DesktopModeProvider.isDesktopMode() || Engine.getInstance().isBluetoothSCOConnected()) {
//                preference.seslSetSummaryColor(getActivity().getColor(C0690R.C0691color.settings_spinner_detail_color_dim));
            } else {
//                preference.seslSetSummaryColor(getActivity().getColor(C0690R.C0691color.settings_spinner_detail_color));
            }
        } else {
//            preference.seslSetSummaryColor(getActivity().getColor(C0690R.C0691color.settings_spinner_item_text_checked_color));
            preference.setSummary((CharSequence) getString(C0690R.string.f93on));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0045  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00be  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onPreferenceChange(androidx.preference.Preference r8, java.lang.Object r9) {
        /*
            r7 = this;
            java.lang.String r8 = r8.getKey()
            int r0 = r8.hashCode()
            r1 = -1742599161(0xffffffff98220c07, float:-2.0944082E-24)
            r2 = 2
            r3 = 0
            r4 = 1
            if (r0 == r1) goto L_0x002f
            r1 = 631893873(0x25a9ef71, float:2.9479079E-16)
            if (r0 == r1) goto L_0x0025
            r1 = 1041000781(0x3e0c694d, float:0.13712044)
            if (r0 == r1) goto L_0x001b
            goto L_0x0039
        L_0x001b:
            java.lang.String r0 = "trash_is_on"
            boolean r8 = r8.equals(r0)
            if (r8 == 0) goto L_0x0039
            r8 = r2
            goto L_0x003a
        L_0x0025:
            java.lang.String r0 = "rec_call_reject"
            boolean r8 = r8.equals(r0)
            if (r8 == 0) goto L_0x0039
            r8 = r3
            goto L_0x003a
        L_0x002f:
            java.lang.String r0 = "play_continuously"
            boolean r8 = r8.equals(r0)
            if (r8 == 0) goto L_0x0039
            r8 = r4
            goto L_0x003a
        L_0x0039:
            r8 = -1
        L_0x003a:
            java.lang.String r0 = "SETT"
            java.lang.String r1 = "1"
            java.lang.String r5 = "0"
            r6 = 2131755493(0x7f1001e5, float:1.9141867E38)
            if (r8 == 0) goto L_0x00be
            if (r8 == r4) goto L_0x006d
            if (r8 == r2) goto L_0x004a
            return r3
        L_0x004a:
            java.lang.Boolean r9 = (java.lang.Boolean) r9
            boolean r8 = r9.booleanValue()
            if (r8 != 0) goto L_0x006c
            androidx.preference.SwitchPreferenceCompat r8 = r7.mSwitchTurnOnTrash
            r8.setChecked(r4)
            androidx.fragment.app.FragmentManager r8 = r7.getFragmentManager()
            java.lang.String r9 = "TurnOnOffTrashDialog"
            boolean r8 = com.sec.android.app.voicenote.p007ui.dialog.DialogFactory.isDialogVisible(r8, r9)
            if (r8 != 0) goto L_0x006b
            androidx.fragment.app.FragmentManager r8 = r7.getFragmentManager()
            r0 = 0
            com.sec.android.app.voicenote.p007ui.dialog.DialogFactory.show(r8, r9, r0)
        L_0x006b:
            return r3
        L_0x006c:
            return r4
        L_0x006d:
            r8 = 6
            com.sec.android.app.voicenote.provider.SurveyLogProvider.insertFeatureLog(r0, r8)
            java.lang.Boolean r8 = java.lang.Boolean.valueOf(r4)
            boolean r8 = r9.equals(r8)
            java.lang.String r9 = "5406"
            r0 = 2131755191(0x7f1000b7, float:1.9141254E38)
            if (r8 == 0) goto L_0x009f
            androidx.fragment.app.FragmentActivity r8 = r7.getActivity()
            android.content.res.Resources r8 = r8.getResources()
            java.lang.String r8 = r8.getString(r6)
            androidx.fragment.app.FragmentActivity r2 = r7.getActivity()
            android.content.res.Resources r2 = r2.getResources()
            java.lang.String r0 = r2.getString(r0)
            com.sec.android.app.voicenote.provider.SALogProvider.insertSALog((java.lang.String) r8, (java.lang.String) r0, (java.lang.String) r1)
            com.sec.android.app.voicenote.provider.SALogProvider.insertStatusLog((java.lang.String) r9, (java.lang.String) r1)
            goto L_0x00bd
        L_0x009f:
            androidx.fragment.app.FragmentActivity r8 = r7.getActivity()
            android.content.res.Resources r8 = r8.getResources()
            java.lang.String r8 = r8.getString(r6)
            androidx.fragment.app.FragmentActivity r1 = r7.getActivity()
            android.content.res.Resources r1 = r1.getResources()
            java.lang.String r0 = r1.getString(r0)
            com.sec.android.app.voicenote.provider.SALogProvider.insertSALog((java.lang.String) r8, (java.lang.String) r0, (java.lang.String) r5)
            com.sec.android.app.voicenote.provider.SALogProvider.insertStatusLog((java.lang.String) r9, (java.lang.String) r5)
        L_0x00bd:
            return r4
        L_0x00be:
            r8 = 4
            com.sec.android.app.voicenote.provider.SurveyLogProvider.insertFeatureLog(r0, r8)
            java.lang.String r8 = "no_tips"
            com.sec.android.app.voicenote.provider.Settings.setSettings((java.lang.String) r8, (boolean) r4)
            java.lang.Boolean r8 = java.lang.Boolean.valueOf(r4)
            boolean r8 = r9.equals(r8)
            java.lang.String r9 = "5404"
            r0 = 2131755130(0x7f10007a, float:1.914113E38)
            if (r8 == 0) goto L_0x00f5
            androidx.fragment.app.FragmentActivity r8 = r7.getActivity()
            android.content.res.Resources r8 = r8.getResources()
            java.lang.String r8 = r8.getString(r6)
            androidx.fragment.app.FragmentActivity r2 = r7.getActivity()
            android.content.res.Resources r2 = r2.getResources()
            java.lang.String r0 = r2.getString(r0)
            com.sec.android.app.voicenote.provider.SALogProvider.insertSALog((java.lang.String) r8, (java.lang.String) r0, (java.lang.String) r1)
            com.sec.android.app.voicenote.provider.SALogProvider.insertStatusLog((java.lang.String) r9, (java.lang.String) r1)
            goto L_0x0113
        L_0x00f5:
            androidx.fragment.app.FragmentActivity r8 = r7.getActivity()
            android.content.res.Resources r8 = r8.getResources()
            java.lang.String r8 = r8.getString(r6)
            androidx.fragment.app.FragmentActivity r1 = r7.getActivity()
            android.content.res.Resources r1 = r1.getResources()
            java.lang.String r0 = r1.getString(r0)
            com.sec.android.app.voicenote.provider.SALogProvider.insertSALog((java.lang.String) r8, (java.lang.String) r0, (java.lang.String) r5)
            com.sec.android.app.voicenote.provider.SALogProvider.insertStatusLog((java.lang.String) r9, (java.lang.String) r5)
        L_0x0113:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.PreferenceSettingFragment.onPreferenceChange(androidx.preference.Preference, java.lang.Object):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x005d  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00a6  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onPreferenceClick(androidx.preference.Preference r9) {
        /*
            r8 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "onPreferenceClick key : "
            r0.append(r1)
            java.lang.String r1 = r9.getKey()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "PreferenceSettingFragment"
            com.sec.android.app.voicenote.provider.Log.m26i(r1, r0)
            java.lang.String r9 = r9.getKey()
            int r0 = r9.hashCode()
            r2 = -536339833(0xffffffffe0081a87, float:-3.92292E19)
            java.lang.String r3 = "rec_call_reject"
            r4 = 0
            r5 = 2
            r6 = 1
            if (r0 == r2) goto L_0x0049
            r2 = 92611469(0x585238d, float:1.2520319E-35)
            if (r0 == r2) goto L_0x003f
            r2 = 631893873(0x25a9ef71, float:2.9479079E-16)
            if (r0 == r2) goto L_0x0037
            goto L_0x0053
        L_0x0037:
            boolean r9 = r9.equals(r3)
            if (r9 == 0) goto L_0x0053
            r9 = r5
            goto L_0x0054
        L_0x003f:
            java.lang.String r0 = "about"
            boolean r9 = r9.equals(r0)
            if (r9 == 0) goto L_0x0053
            r9 = r4
            goto L_0x0054
        L_0x0049:
            java.lang.String r0 = "rec_stereo"
            boolean r9 = r9.equals(r0)
            if (r9 == 0) goto L_0x0053
            r9 = r6
            goto L_0x0054
        L_0x0053:
            r9 = -1
        L_0x0054:
            r0 = 2131755493(0x7f1001e5, float:1.9141867E38)
            java.lang.String r2 = "ActivityNotFoundException"
            java.lang.String r7 = "SETT"
            if (r9 == 0) goto L_0x00a6
            if (r9 == r6) goto L_0x0071
            if (r9 == r5) goto L_0x0063
            goto L_0x00db
        L_0x0063:
            boolean r9 = com.sec.android.app.voicenote.provider.Settings.getBooleanSettings(r3)
            if (r9 == 0) goto L_0x006d
            com.sec.android.app.voicenote.service.VoiceNoteService.setCallRejectingServiceEnabled(r6, r6)
            goto L_0x00db
        L_0x006d:
            com.sec.android.app.voicenote.service.VoiceNoteService.setCallRejectingServiceEnabled(r4, r6)
            goto L_0x00db
        L_0x0071:
            com.sec.android.app.voicenote.provider.SurveyLogProvider.insertFeatureLog(r7, r5)     // Catch:{ ActivityNotFoundException -> 0x00a1 }
            androidx.fragment.app.FragmentActivity r9 = r8.getActivity()     // Catch:{ ActivityNotFoundException -> 0x00a1 }
            android.content.res.Resources r9 = r9.getResources()     // Catch:{ ActivityNotFoundException -> 0x00a1 }
            java.lang.String r9 = r9.getString(r0)     // Catch:{ ActivityNotFoundException -> 0x00a1 }
            androidx.fragment.app.FragmentActivity r0 = r8.getActivity()     // Catch:{ ActivityNotFoundException -> 0x00a1 }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ ActivityNotFoundException -> 0x00a1 }
            r3 = 2131755259(0x7f1000fb, float:1.9141392E38)
            java.lang.String r0 = r0.getString(r3)     // Catch:{ ActivityNotFoundException -> 0x00a1 }
            com.sec.android.app.voicenote.provider.SALogProvider.insertSALog(r9, r0)     // Catch:{ ActivityNotFoundException -> 0x00a1 }
            android.content.Intent r9 = new android.content.Intent     // Catch:{ ActivityNotFoundException -> 0x00a1 }
            androidx.fragment.app.FragmentActivity r0 = r8.getActivity()     // Catch:{ ActivityNotFoundException -> 0x00a1 }
            java.lang.Class<com.sec.android.app.voicenote.activity.RecordStereoActivity> r3 = com.sec.android.app.voicenote.activity.RecordStereoActivity.class
            r9.<init>(r0, r3)     // Catch:{ ActivityNotFoundException -> 0x00a1 }
            r8.startActivity(r9)     // Catch:{ ActivityNotFoundException -> 0x00a1 }
            goto L_0x00db
        L_0x00a1:
            r9 = move-exception
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r1, (java.lang.String) r2, (java.lang.Throwable) r9)
            goto L_0x00db
        L_0x00a6:
            r9 = 5
            com.sec.android.app.voicenote.provider.SurveyLogProvider.insertFeatureLog(r7, r9)     // Catch:{ ActivityNotFoundException -> 0x00d7 }
            androidx.fragment.app.FragmentActivity r9 = r8.getActivity()     // Catch:{ ActivityNotFoundException -> 0x00d7 }
            android.content.res.Resources r9 = r9.getResources()     // Catch:{ ActivityNotFoundException -> 0x00d7 }
            java.lang.String r9 = r9.getString(r0)     // Catch:{ ActivityNotFoundException -> 0x00d7 }
            androidx.fragment.app.FragmentActivity r0 = r8.getActivity()     // Catch:{ ActivityNotFoundException -> 0x00d7 }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ ActivityNotFoundException -> 0x00d7 }
            r3 = 2131755124(0x7f100074, float:1.9141118E38)
            java.lang.String r0 = r0.getString(r3)     // Catch:{ ActivityNotFoundException -> 0x00d7 }
            com.sec.android.app.voicenote.provider.SALogProvider.insertSALog(r9, r0)     // Catch:{ ActivityNotFoundException -> 0x00d7 }
            android.content.Intent r9 = new android.content.Intent     // Catch:{ ActivityNotFoundException -> 0x00d7 }
            androidx.fragment.app.FragmentActivity r0 = r8.getActivity()     // Catch:{ ActivityNotFoundException -> 0x00d7 }
            java.lang.Class<com.sec.android.app.voicenote.activity.AboutActivity> r3 = com.sec.android.app.voicenote.activity.AboutActivity.class
            r9.<init>(r0, r3)     // Catch:{ ActivityNotFoundException -> 0x00d7 }
            r8.startActivity(r9)     // Catch:{ ActivityNotFoundException -> 0x00d7 }
            goto L_0x00db
        L_0x00d7:
            r9 = move-exception
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r1, (java.lang.String) r2, (java.lang.Throwable) r9)
        L_0x00db:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.PreferenceSettingFragment.onPreferenceClick(androidx.preference.Preference):boolean");
    }

    private void highlightPreference() {
        Log.m26i(TAG, "highlightPreference");
        int i = VoiceNoteFeature.FLAG_SUPPORT_STEREO ? 2 : 1;
        RecyclerView recyclerView = null;
        try {
            recyclerView = getListView();
        } catch (Error | Exception e) {
            Log.m22e(TAG, e.toString());
        }
        if (recyclerView != null && i < recyclerView.getChildCount()) {
            final View childAt = recyclerView.getChildAt(i);
            int width = childAt.getWidth() / 2;
            int height = childAt.getHeight() / 2;
            Log.m26i(TAG, "highlightPreference centerX : " + width + " centerY : " + height);
            childAt.getBackground().setHotspot((float) width, (float) height);
            childAt.setPressed(true);
            new Handler().postDelayed(new Runnable() {
                private final /* synthetic */ View f$0;

                {
                    this.f$0 = childAt;
                }

                public final void run() {
                    this.f$0.setPressed(false);
                }
            }, 50);
        }
    }

    public void update(Observable observable, Object obj) {
        int intValue = ((Integer) obj).intValue();
        Log.m26i(TAG, "update - data : " + intValue);
        if (intValue == 16) {
            highlightPreference();
        } else if (intValue == 948) {
            updateTrashStatusPreference();
        } else if (intValue != 949) {
            switch (intValue) {
                case Event.UNMOUNT_SD_CARD:
                    updateStoragePreference();
                    if (isResumed()) {
                        Settings.setSettings(Settings.KEY_STORAGE, 0);
                        StorageProvider.resetSDCardWritableDir();
                        Settings.setSettings(Settings.KEY_SDCARD_PREVIOUS_STATE, 0);
                        DialogFactory.clearDialogByTag(getFragmentManager(), DialogFactory.SD_CARD_SELECT_DIALOG);
                        return;
                    }
                    return;
                case Event.MOUNT_SD_CARD:
                    if (!AndroidForWork.getInstance().isAndroidForWorkMode(getActivity())) {
                        updateStoragePreference();
                        if ("mounted".equals(StorageProvider.getExternalStorageStateSd()) && isResumed()) {
                            if (!DialogFactory.isDialogVisible(getFragmentManager(), DialogFactory.SD_CARD_SELECT_DIALOG)) {
                                DialogFactory.show(getFragmentManager(), DialogFactory.SD_CARD_SELECT_DIALOG, (Bundle) null);
                            }
                            Settings.setSettings(Settings.KEY_SDCARD_PREVIOUS_STATE, 1);
                            return;
                        }
                        return;
                    }
                    return;
                case Event.CHANGE_STORAGE:
                    PopUpPreference popUpPreference = this.mPopUpStoragePreference;
                    if (popUpPreference != null) {
                        popUpPreference.setKey(getString(C0690R.string.memory_card));
                        PopUpPreference popUpPreference2 = this.mPopUpStoragePreference;
                        popUpPreference2.setSummary((CharSequence) popUpPreference2.getEntry());
                        updateStoragePreference();
                        return;
                    }
                    return;
                default:
                    return;
            }
        } else {
            updateStereoPreference();
        }
    }
}
