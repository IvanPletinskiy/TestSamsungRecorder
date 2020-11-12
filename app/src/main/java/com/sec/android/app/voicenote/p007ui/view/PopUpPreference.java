package com.sec.android.app.voicenote.p007ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/* renamed from: com.sec.android.app.voicenote.ui.view.PopUpPreference */
public class PopUpPreference extends Preference {
    private static final int QUALITY_POSITION_HIGH = 0;
    private static final int QUALITY_POSITION_LOW = 2;
    private static final int QUALITY_POSITION_MMS = 3;
    private static final int QUALITY_POSITION_NORMAL = 1;
    private static final String TAG = "PopUpPreference";
    /* access modifiers changed from: private */
    public static int mSelectedPosition = -1;
    private static String qualityAMRSummary;
    private static String qualityHighSummary;
    private static String qualityLowSummary;
    private static String qualityNormalSummary;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public ArrayList<String> mEntry;
    private ArrayList<String> mEntryValues;
    /* access modifiers changed from: private */
    public boolean mIsInitialCall;
    /* access modifiers changed from: private */
    public ArrayList<String> mQualityEntries = new ArrayList<>();
    private AppCompatSpinner mSpinner;
    protected String whichPreference;

    static /* synthetic */ boolean lambda$initSpinner$1(Preference preference, Object obj) {
        return true;
    }

    public PopUpPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
        init();
        initEntry();
        initSpinner();
    }

    public String getKey() {
        return super.getKey();
    }

    public String getEntry() {
        return this.mEntry.get(getSavedPosition());
    }

    public void setSelectedItemSpinner() {
        AppCompatSpinner appCompatSpinner = this.mSpinner;
        if (appCompatSpinner != null) {
            appCompatSpinner.setSelection(getSavedPosition());
        }
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        AppCompatSpinner appCompatSpinner = this.mSpinner;
        if (appCompatSpinner != null && !preferenceViewHolder.itemView.equals(appCompatSpinner.getParent())) {
            if (this.mSpinner.getParent() != null) {
                ((ViewGroup) this.mSpinner.getParent()).removeView(this.mSpinner);
            }
            ((ViewGroup) preferenceViewHolder.itemView).addView(this.mSpinner, 0);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mSpinner.getLayoutParams();
            layoutParams.width = 0;
            layoutParams.height = 0;
            layoutParams.gravity = 48;
            this.mSpinner.setLayoutParams(layoutParams);
        }
    }

    /* access modifiers changed from: protected */
    public void init() {
        this.whichPreference = getKey();
        if (!this.whichPreference.equals(Settings.KEY_REC_QUALITY)) {
            return;
        }
        if (this.mContext.getResources().getConfiguration().getLayoutDirection() == 1) {
            qualityLowSummary = String.format(Locale.getDefault(), " kbps%d, kHz%d.%d", new Object[]{64, 44, 1});
            qualityNormalSummary = String.format(Locale.getDefault(), " kbps%d, kHz%d.%d", new Object[]{128, 44, 1});
            qualityHighSummary = String.format(Locale.getDefault(), " kbps%d, kHz%d", new Object[]{256, 48});
            qualityAMRSummary = String.format(Locale.getDefault(), " AMR - kbps%d.%d, kHz%d", new Object[]{12, 2, 8});
            return;
        }
        qualityLowSummary = String.format(Locale.getDefault(), " %dkbps, %d.%dkHz", new Object[]{64, 44, 1});
        qualityNormalSummary = String.format(Locale.getDefault(), " %dkbps, %d.%dkHz", new Object[]{128, 44, 1});
        qualityHighSummary = String.format(Locale.getDefault(), " %dkbps, %dkHz", new Object[]{256, 48});
        qualityAMRSummary = String.format(Locale.getDefault(), " AMR - %d.%dkbps, %dkHz", new Object[]{12, 2, 8});
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0029  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x004b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void initEntry() {
        /*
            r4 = this;
            java.lang.String r0 = r4.whichPreference
            int r1 = r0.hashCode()
            r2 = -1884274053(0xffffffff8fb0427b, float:-1.7380547E-29)
            r3 = 1
            if (r1 == r2) goto L_0x001c
            r2 = -1196912560(0xffffffffb8a89050, float:-8.0377446E-5)
            if (r1 == r2) goto L_0x0012
            goto L_0x0026
        L_0x0012:
            java.lang.String r1 = "rec_quality"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0026
            r0 = r3
            goto L_0x0027
        L_0x001c:
            java.lang.String r1 = "storage"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0026
            r0 = 0
            goto L_0x0027
        L_0x0026:
            r0 = -1
        L_0x0027:
            if (r0 == 0) goto L_0x004b
            if (r0 == r3) goto L_0x002c
            goto L_0x0069
        L_0x002c:
            android.content.Context r0 = r4.mContext
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2130903042(0x7f030002, float:1.741289E38)
            java.util.ArrayList r0 = r4.loadStringArray(r0, r1)
            r4.mEntry = r0
            android.content.Context r0 = r4.mContext
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2130903050(0x7f03000a, float:1.7412907E38)
            java.util.ArrayList r0 = r4.loadStringArray(r0, r1)
            r4.mEntryValues = r0
            goto L_0x0069
        L_0x004b:
            android.content.Context r0 = r4.mContext
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2130903044(0x7f030004, float:1.7412895E38)
            java.util.ArrayList r0 = r4.loadStringArray(r0, r1)
            r4.mEntry = r0
            android.content.Context r0 = r4.mContext
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2130903049(0x7f030009, float:1.7412905E38)
            java.util.ArrayList r0 = r4.loadStringArray(r0, r1)
            r4.mEntryValues = r0
        L_0x0069:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.view.PopUpPreference.initEntry():void");
    }

    private void initSpinner() {
        int savedPosition = getSavedPosition();
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this.mContext, 17367049, this.mEntry);
        this.mSpinner = new AppCompatSpinner(this.mContext);
        this.mSpinner.setVisibility(4);
        this.mSpinner.setAdapter((android.widget.SpinnerAdapter) spinnerAdapter);
        this.mSpinner.setSelection(savedPosition);
        this.mIsInitialCall = true;
        this.mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                if (PopUpPreference.this.mIsInitialCall) {
                    boolean unused = PopUpPreference.this.mIsInitialCall = false;
                    return;
                }
                Log.m26i(PopUpPreference.TAG, "onItemSelected - position : " + i);
                PopUpPreference.this.setSummary(i);
                int unused2 = PopUpPreference.mSelectedPosition = i;
                if (Settings.KEY_REC_QUALITY.equals(PopUpPreference.this.whichPreference)) {
                    if (PopUpPreference.mSelectedPosition == 3) {
                        Settings.setSettings("record_mode", 6);
                    } else if (Settings.getIntSettings("record_mode", 1) == 6) {
                        Settings.setSettings("record_mode", 1);
                    }
                    int intSettings = Settings.getIntSettings(Settings.KEY_REC_QUALITY, 1);
                    if (intSettings == 0) {
                        SALogProvider.insertSALog(PopUpPreference.this.getContext().getResources().getString(C0690R.string.screen_settings), PopUpPreference.this.getContext().getResources().getString(C0690R.string.event_rec_quality), SALogProvider.QUALITY_LOW);
                        SALogProvider.insertStatusLog(SALogProvider.KEY_SA_RECORDING_QUALITY_TYPE, SALogProvider.QUALITY_LOW);
                    } else if (intSettings == 1) {
                        SALogProvider.insertSALog(PopUpPreference.this.getContext().getResources().getString(C0690R.string.screen_settings), PopUpPreference.this.getContext().getResources().getString(C0690R.string.event_rec_quality), "2");
                        SALogProvider.insertStatusLog(SALogProvider.KEY_SA_RECORDING_QUALITY_TYPE, "2");
                    } else if (intSettings == 2) {
                        SALogProvider.insertSALog(PopUpPreference.this.getContext().getResources().getString(C0690R.string.screen_settings), PopUpPreference.this.getContext().getResources().getString(C0690R.string.event_rec_quality), "1");
                        SALogProvider.insertStatusLog(SALogProvider.KEY_SA_RECORDING_QUALITY_TYPE, "1");
                    } else if (intSettings == 3) {
                        SALogProvider.insertSALog(PopUpPreference.this.getContext().getResources().getString(C0690R.string.screen_settings), PopUpPreference.this.getContext().getResources().getString(C0690R.string.event_rec_quality), SALogProvider.QUALITY_MMS);
                        SALogProvider.insertStatusLog(SALogProvider.KEY_SA_RECORDING_QUALITY_TYPE, SALogProvider.QUALITY_MMS);
                    }
                } else if (Settings.KEY_STORAGE.equals(PopUpPreference.this.whichPreference)) {
                    int intSettings2 = Settings.getIntSettings(Settings.KEY_STORAGE, 0);
                    if (intSettings2 == 0) {
                        SALogProvider.insertSALog(PopUpPreference.this.getContext().getResources().getString(C0690R.string.screen_settings), PopUpPreference.this.getContext().getResources().getString(C0690R.string.event_storage_location), "1");
                        SALogProvider.insertStatusLog(SALogProvider.KEY_SA_STORAGE_LOCATION_TYPE, "1");
                    } else if (intSettings2 == 1) {
                        SALogProvider.insertSALog(PopUpPreference.this.getContext().getResources().getString(C0690R.string.screen_settings), PopUpPreference.this.getContext().getResources().getString(C0690R.string.event_storage_location), "2");
                        SALogProvider.insertStatusLog(SALogProvider.KEY_SA_STORAGE_LOCATION_TYPE, "2");
                    }
                }
            }
        });
        setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public final boolean onPreferenceClick(Preference preference) {
                return PopUpPreference.this.lambda$initSpinner$0$PopUpPreference(preference);
            }
        });
        setOnPreferenceChangeListener($$Lambda$PopUpPreference$RkUNt_B21PijdHo2CZb52qDGnZY.INSTANCE);
    }

    public /* synthetic */ boolean lambda$initSpinner$0$PopUpPreference(Preference preference) {
        this.mSpinner.setSoundEffectsEnabled(false);
        this.mSpinner.performClick();
        mSelectedPosition = getSavedPosition();
        if (Settings.KEY_REC_QUALITY.equals(this.whichPreference)) {
            SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_SETTINGS, 1);
        }
        return true;
    }

    public int getSavedPosition() {
        if (this.whichPreference.equalsIgnoreCase(Settings.KEY_STORAGE)) {
            return Settings.getIntSettings(Settings.KEY_STORAGE, 0);
        }
        if (!this.whichPreference.equalsIgnoreCase(Settings.KEY_REC_QUALITY)) {
            return 0;
        }
        int intSettings = Settings.getIntSettings(Settings.KEY_REC_QUALITY, 1);
        if (intSettings == 0) {
            return 2;
        }
        if (intSettings == 1 || intSettings != 2) {
            return 1;
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public ArrayList<String> loadStringArray(Resources resources, int i) {
        String[] stringArray = resources.getStringArray(i);
        if (i == C0690R.array.recording_quality) {
            String str = null;
            for (int i2 = 0; i2 < stringArray.length; i2++) {
                if (i2 == 0) {
                    str = qualityHighSummary;
                } else if (i2 == 1) {
                    str = qualityNormalSummary;
                } else if (i2 == 2) {
                    str = qualityLowSummary;
                } else if (i2 == 3) {
                    str = qualityAMRSummary;
                }
                this.mQualityEntries.add(str);
            }
        }
        return new ArrayList<>(Arrays.asList(stringArray));
    }

    public void setSummary(int i) {
        String valueOf = String.valueOf(i);
        if (this.whichPreference.equalsIgnoreCase(Settings.KEY_STORAGE) || this.whichPreference.equalsIgnoreCase(Settings.KEY_REC_QUALITY)) {
            valueOf = this.mEntryValues.get(i);
        }
        Settings.setSettings(this.whichPreference, valueOf);
        if (this.whichPreference.equalsIgnoreCase(Settings.KEY_STORAGE)) {
            Settings.setSettings(Settings.KEY_SDCARD_PREVIOUS_STATE, 1);
        }
        String str = this.mEntry.get(i);
        if (this.whichPreference.equalsIgnoreCase(Settings.KEY_REC_QUALITY)) {
            if (i == 0) {
                str = str + qualityHighSummary;
            } else if (i == 1) {
                str = str + qualityNormalSummary;
            } else if (i == 2) {
                str = str + qualityLowSummary;
            } else if (i == 3) {
                str = str + qualityAMRSummary;
            }
        }
        setSummary((CharSequence) str);
    }

    public CharSequence getSummary() {
        if (this.whichPreference.equalsIgnoreCase(Settings.KEY_REC_QUALITY)) {
            return super.getSummary();
        }
        return this.mEntry.get(getSavedPosition());
    }

    public void setSummary(CharSequence charSequence) {
        super.setSummary(charSequence);
    }

    /* renamed from: com.sec.android.app.voicenote.ui.view.PopUpPreference$SpinnerItemHolder */
    static class SpinnerItemHolder {
        ImageView imgChecked;
        TextView mainText;
        TextView subText;

        SpinnerItemHolder() {
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.view.PopUpPreference$SpinnerAdapter */
    private class SpinnerAdapter extends ArrayAdapter<String> {
        public SpinnerAdapter(Context context, int i, ArrayList<String> arrayList) {
            super(context, i, arrayList);
        }

        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
            SpinnerItemHolder spinnerItemHolder;
            if (view == null) {
                view = LayoutInflater.from(PopUpPreference.this.mContext).inflate(C0690R.layout.setting_spinner_item, viewGroup, false);
                spinnerItemHolder = new SpinnerItemHolder();
                spinnerItemHolder.mainText = (TextView) view.findViewById(C0690R.C0693id.main_text);
                spinnerItemHolder.subText = (TextView) view.findViewById(C0690R.C0693id.sub_text);
                spinnerItemHolder.imgChecked = (ImageView) view.findViewById(C0690R.C0693id.checked);
                view.setTag(spinnerItemHolder);
            } else {
                spinnerItemHolder = (SpinnerItemHolder) view.getTag();
            }
            spinnerItemHolder.mainText.setText((CharSequence) PopUpPreference.this.mEntry.get(i));
            if (!PopUpPreference.this.mQualityEntries.isEmpty()) {
                spinnerItemHolder.subText.setText((CharSequence) PopUpPreference.this.mQualityEntries.get(i));
                spinnerItemHolder.subText.setVisibility(0);
            } else {
                spinnerItemHolder.subText.setVisibility(4);
            }
            String str = "";
            if (PopUpPreference.mSelectedPosition == i) {
                spinnerItemHolder.mainText.setTextColor(ContextCompat.getColor(PopUpPreference.this.mContext, C0690R.C0691color.settings_spinner_item_text_checked_color));
                spinnerItemHolder.subText.setTextColor(ContextCompat.getColor(PopUpPreference.this.mContext, C0690R.C0691color.settings_spinner_item_text_checked_color));
                spinnerItemHolder.imgChecked.setVisibility(0);
                StringBuilder sb = new StringBuilder();
                sb.append(((String) PopUpPreference.this.mEntry.get(i)).concat(", "));
                if (!PopUpPreference.this.mQualityEntries.isEmpty()) {
                    str = ((String) PopUpPreference.this.mQualityEntries.get(i)).concat(", ");
                }
                sb.append(str);
                sb.append(PopUpPreference.this.mContext.getString(C0690R.string.tts_ticked_t_tts));
                view.setContentDescription(sb.toString());
            } else {
                spinnerItemHolder.mainText.setTextColor(ContextCompat.getColor(PopUpPreference.this.mContext, C0690R.C0691color.settings_spinner_item_color));
                spinnerItemHolder.subText.setTextColor(ContextCompat.getColor(PopUpPreference.this.mContext, C0690R.C0691color.settings_spinner_item_sub_text_color));
                spinnerItemHolder.imgChecked.setVisibility(4);
                StringBuilder sb2 = new StringBuilder();
                sb2.append(((String) PopUpPreference.this.mEntry.get(i)).concat(", "));
                if (!PopUpPreference.this.mQualityEntries.isEmpty()) {
                    str = ((String) PopUpPreference.this.mQualityEntries.get(i)).concat(", ");
                }
                sb2.append(str);
                sb2.append(PopUpPreference.this.mContext.getString(C0690R.string.tts_not_ticked_t_tts));
                view.setContentDescription(sb2.toString());
            }
            return view;
        }
    }
}
