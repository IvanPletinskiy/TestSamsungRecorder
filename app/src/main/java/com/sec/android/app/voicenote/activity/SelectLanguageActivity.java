package com.sec.android.app.voicenote.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import androidx.core.content.ContextCompat;

public class SelectLanguageActivity extends BaseToolbarActivity implements AdapterView.OnItemClickListener, Observer {
    private static final String TAG = "SelectLanguageActivity";
    private LanguageAdapter mAdapter;
    private RelativeLayout mLanguageListLayout;
    private ListView mListView;

    public void onCreate(Bundle bundle) {
        Log.m26i(TAG, "onCreate");
        super.onCreate(bundle);
        View inflate = LayoutInflater.from(this).inflate(C0690R.layout.activity_select_language, (ViewGroup) null);
        this.mListView = (ListView) inflate.findViewById(C0690R.C0693id.list);
        setContentView(inflate);
        updateLayoutListInTabletMultiWindow(this, inflate);
        setDisplayShowHomeEnabled();
        setTitleActivity((int) C0690R.string.language);
        if (getResources().getConfiguration().orientation == 2 && DisplayManager.getMultiwindowMode() != 2) {
            getWindow().setFlags(1024, 1024);
        }
        List asList = Arrays.asList(getResources().getStringArray(C0690R.array.stt_language_text));
        List asList2 = Arrays.asList(getResources().getStringArray(C0690R.array.stt_language_description));
        inflate.setBackgroundColor(ContextCompat.getColor(this, C0690R.C0691color.actionbar_color_bg));
        ListView listView = this.mListView;
        if (listView != null) {
//            listView.semSetRoundedCorners(15);
//            this.mListView.semSetRoundedCornerColor(15, ContextCompat.getColor(this, C0690R.C0691color.actionbar_color_bg));
//            this.mListView.semSetBottomColor(ContextCompat.getColor(this, C0690R.C0691color.actionbar_color_bg));
            this.mListView.setOnItemClickListener(this);
            this.mAdapter = new LanguageAdapter(this, C0690R.layout.select_language_list_item, C0690R.C0693id.language_text, asList, asList2);
            this.mListView.setAdapter(this.mAdapter);
            this.mListView.setChoiceMode(1);
            this.mListView.setSelected(true);
            int languageIndex = getLanguageIndex(Settings.getStringSettings(Settings.KEY_STT_LANGUAGE_LOCALE));
            if (languageIndex < 0) {
                languageIndex = 0;
            }
            this.mListView.setItemChecked(languageIndex, true);
        }
        VoiceNoteObservable.getInstance().addObserver(this);
    }

    private void updateLayoutListInTabletMultiWindow(Activity activity, View view) {
        this.mLanguageListLayout = (RelativeLayout) view.findViewById(C0690R.C0693id.layout_language_list);
        if (VoiceNoteFeature.FLAG_IS_TABLET && this.mLanguageListLayout != null && DisplayManager.isCurrentWindowOnLandscape(activity)) {
            if ((DisplayManager.getMultiwindowMode() == 2 && getResources().getConfiguration().densityDpi < 480) || (DisplayManager.getMultiwindowMode() != 2 && getResources().getConfiguration().densityDpi >= 480)) {
                int i = (int) (((double) getResources().getDisplayMetrics().widthPixels) * 0.05d);
                this.mLanguageListLayout.setPadding(i, 0, i, 0);
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        VoiceNoteObservable.getInstance().deleteObserver(this);
        this.mLanguageListLayout = null;
        this.mListView = null;
        if (this.mAdapter != null) {
            this.mAdapter = null;
        }
        super.onDestroy();
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    /* access modifiers changed from: private */
    public int getLanguageIndex(String str) {
        String[] stringArray = getResources().getStringArray(C0690R.array.stt_language_locale);
        for (int i = 0; i < stringArray.length; i++) {
            if (stringArray[i].equals(str)) {
                return i;
            }
        }
        return -1;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(C0690R.C0693id.language_text);
        if (checkedTextView != null) {
            checkedTextView.setChecked(true);
        }
        this.mAdapter.notifyDataSetChanged();
        String[] stringArray = getResources().getStringArray(C0690R.array.stt_language_text);
        String[] stringArray2 = getResources().getStringArray(C0690R.array.stt_language_locale);
        Settings.setSettings(Settings.KEY_STT_LANGUAGE_TEXT, stringArray[i]);
        Settings.setSettings(Settings.KEY_STT_LANGUAGE_LOCALE, stringArray2[i]);
        SurveyLogProvider.insertStatusLog(SurveyLogProvider.SURVEY_LANGUAGE, stringArray[i], -1);
        VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.CHANGE_STT_LANGUAGE));
        finish();
    }

    public void update(Observable observable, Object obj) {
        int intValue = ((Integer) obj).intValue();
        Log.m26i(TAG, "update - data : " + intValue);
    }

    public class LanguageAdapter extends ArrayAdapter<String> {
        List<String> description;
        int mLayoutId;

        LanguageAdapter(Context context, int i, int i2, List<String> list, List<String> list2) {
            super(context, i, i2, list);
            this.mLayoutId = i;
            this.description = list2;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v3, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: com.sec.android.app.voicenote.activity.SelectLanguageActivity$ViewHolder} */
        /* JADX WARNING: Multi-variable type inference failed */
        @androidx.annotation.NonNull
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public android.view.View getView(int r8, android.view.View r9, @androidx.annotation.NonNull android.view.ViewGroup r10) {
            /*
                r7 = this;
                r0 = 0
                r1 = 1
                if (r9 != 0) goto L_0x00bd
                android.util.TypedValue r9 = new android.util.TypedValue
                r9.<init>()
                com.sec.android.app.voicenote.activity.SelectLanguageActivity r2 = com.sec.android.app.voicenote.activity.SelectLanguageActivity.this
                android.content.res.Resources r2 = r2.getResources()
                r3 = 2131165354(0x7f0700aa, float:1.7944923E38)
                r2.getValue(r3, r9, r1)
                float r2 = r9.getFloat()
                com.sec.android.app.voicenote.activity.SelectLanguageActivity r3 = com.sec.android.app.voicenote.activity.SelectLanguageActivity.this
                android.content.res.Resources r3 = r3.getResources()
                r4 = 2131165351(0x7f0700a7, float:1.7944917E38)
                r3.getValue(r4, r9, r1)
                float r3 = r9.getFloat()
                com.sec.android.app.voicenote.activity.SelectLanguageActivity r4 = com.sec.android.app.voicenote.activity.SelectLanguageActivity.this
                android.content.res.Resources r4 = r4.getResources()
                r5 = 2131165353(0x7f0700a9, float:1.794492E38)
                r4.getValue(r5, r9, r1)
                float r9 = r9.getFloat()
                com.sec.android.app.voicenote.activity.SelectLanguageActivity$ViewHolder r4 = new com.sec.android.app.voicenote.activity.SelectLanguageActivity$ViewHolder
                r4.<init>()
                android.content.Context r5 = r7.getContext()
                android.view.LayoutInflater r5 = android.view.LayoutInflater.from(r5)
                int r6 = r7.mLayoutId
                android.view.View r5 = r5.inflate(r6, r10, r0)
                r6 = 2131296543(0x7f09011f, float:1.8211006E38)
                android.view.View r6 = r5.findViewById(r6)
                android.widget.CheckedTextView r6 = (android.widget.CheckedTextView) r6
                r4.checkedText = r6
                com.sec.android.app.voicenote.activity.SelectLanguageActivity r6 = com.sec.android.app.voicenote.activity.SelectLanguageActivity.this
                android.content.res.Resources r6 = r6.getResources()
                android.content.res.Configuration r6 = r6.getConfiguration()
                if (r6 == 0) goto L_0x00b8
                float r6 = r6.fontScale
                int r2 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
                if (r2 > 0) goto L_0x007c
                com.sec.android.app.voicenote.activity.SelectLanguageActivity r9 = com.sec.android.app.voicenote.activity.SelectLanguageActivity.this
                android.content.res.Resources r9 = r9.getResources()
                r2 = 2131165408(0x7f0700e0, float:1.7945032E38)
                int r9 = r9.getDimensionPixelSize(r2)
                android.widget.CheckedTextView r2 = r4.checkedText
                r2.setHeight(r9)
                goto L_0x00b8
            L_0x007c:
                int r2 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
                if (r2 > 0) goto L_0x0093
                com.sec.android.app.voicenote.activity.SelectLanguageActivity r9 = com.sec.android.app.voicenote.activity.SelectLanguageActivity.this
                android.content.res.Resources r9 = r9.getResources()
                r2 = 2131165395(0x7f0700d3, float:1.7945006E38)
                int r9 = r9.getDimensionPixelSize(r2)
                android.widget.CheckedTextView r2 = r4.checkedText
                r2.setHeight(r9)
                goto L_0x00b8
            L_0x0093:
                int r9 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
                if (r9 > 0) goto L_0x00aa
                com.sec.android.app.voicenote.activity.SelectLanguageActivity r9 = com.sec.android.app.voicenote.activity.SelectLanguageActivity.this
                android.content.res.Resources r9 = r9.getResources()
                r2 = 2131165407(0x7f0700df, float:1.794503E38)
                int r9 = r9.getDimensionPixelSize(r2)
                android.widget.CheckedTextView r2 = r4.checkedText
                r2.setHeight(r9)
                goto L_0x00b8
            L_0x00aa:
                android.widget.CheckedTextView r9 = r4.checkedText
                android.view.ViewGroup$LayoutParams r9 = r9.getLayoutParams()
                r2 = -2
                r9.height = r2
                android.widget.CheckedTextView r2 = r4.checkedText
                r2.setLayoutParams(r9)
            L_0x00b8:
                r5.setTag(r4)
                r9 = r5
                goto L_0x00c4
            L_0x00bd:
                java.lang.Object r2 = r9.getTag()
                r4 = r2
                com.sec.android.app.voicenote.activity.SelectLanguageActivity$ViewHolder r4 = (com.sec.android.app.voicenote.activity.SelectLanguageActivity.ViewHolder) r4
            L_0x00c4:
                java.lang.String r2 = "stt_language_locale"
                java.lang.String r2 = com.sec.android.app.voicenote.provider.Settings.getStringSettings(r2)
                com.sec.android.app.voicenote.activity.SelectLanguageActivity r3 = com.sec.android.app.voicenote.activity.SelectLanguageActivity.this
                int r2 = r3.getLanguageIndex(r2)
                android.widget.CheckedTextView r3 = r4.checkedText
                if (r3 == 0) goto L_0x00ea
                if (r8 != r2) goto L_0x00da
                r3.setChecked(r1)
                goto L_0x00dd
            L_0x00da:
                r3.setChecked(r0)
            L_0x00dd:
                android.widget.CheckedTextView r0 = r4.checkedText
                java.util.List<java.lang.String> r1 = r7.description
                java.lang.Object r1 = r1.get(r8)
                java.lang.CharSequence r1 = (java.lang.CharSequence) r1
                r0.setContentDescription(r1)
            L_0x00ea:
                android.view.View r8 = super.getView(r8, r9, r10)
                return r8
            */
            throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.activity.SelectLanguageActivity.LanguageAdapter.getView(int, android.view.View, android.view.ViewGroup):android.view.View");
        }
    }

    static class ViewHolder {
        CheckedTextView checkedText;

        ViewHolder() {
        }
    }
}
