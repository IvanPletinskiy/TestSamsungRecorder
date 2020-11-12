package com.sec.android.app.voicenote.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.activity.WebTosActivity;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Network;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint({"SetJavaScriptEnabled"})
public class WebTosActivity extends BaseToolbarActivity implements DialogFactory.DialogResultListener {
    private static final String EXTRA = "extra";

    /* renamed from: ID */
    private static final String f99ID = "id";
    private static final String ISO_COUNTRY_CODE_KOREA = "KR";
    private static final int LAUNCH_COMMON_WEB_TOS = 0;
    private static final int LAUNCH_KOREA_WEB_TOS = 1;
    private static final int LAUNCH_PRIVACY_CONTENT = 2;
    private static final String MANDATORY = "mandatory";
    private static final String NAME = "name";
    private static final String RESULT_CODE = "resultCode";
    private static final String RESULT_MESSAGE = "resultMessage";
    private static final String SUCCESS = "0";
    private static final String TAG = "WebTosActivity";
    private static final String TERMS = "terms";
    private static final String URL = "url";
    private final String SAVED_URL = "SAVED_URL";
    /* access modifiers changed from: private */
    public ProgressBar loadingView;
    private FetchDetailsFromWeb mFetchAsyncTask = null;
    private int mLaunchMode;
    private LinearLayout mLayoutButtonNext = null;
    private LinearLayout mMandatoryLayout;
    private ImageView mNextButtonImg = null;
    private LinearLayout mOptionalLayout;
    private String mSavedURL = null;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.m26i(TAG, "onCreate()");
        if (getResources().getConfiguration().orientation == 2 && DisplayManager.getMultiwindowMode() != 2) {
            getWindow().setFlags(1024, 1024);
        }
        this.mLaunchMode = 0;
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("from_button", false)) {
            this.mLaunchMode = 2;
        } else if (ISO_COUNTRY_CODE_KOREA.equalsIgnoreCase(getSystemsCountryISOCode())) {
            this.mLaunchMode = 1;
        }
        if (this.mLaunchMode == 1) {
            setContentView((int) C0690R.layout.activity_web_tos);
            setDisplayShowHomeEnabled();
            setTitleActivity((int) C0690R.string.privacy_content_title);
            this.mMandatoryLayout = (LinearLayout) findViewById(C0690R.C0693id.disclaimer_checkbox_list_view);
            this.mOptionalLayout = (LinearLayout) findViewById(C0690R.C0693id.disclaimer_radio_list_view);
            ((TextView) findViewById(C0690R.C0693id.main_tos)).setText(getStringTosContentBasedOnCountry());
        } else {
            setContentView((int) C0690R.layout.activity_privacy_content);
            setDisplayShowHomeEnabled();
            if (this.mLaunchMode == 2) {
                setTitleActivity((int) C0690R.string.privacy_content_title);
                RelativeLayout relativeLayout = (RelativeLayout) findViewById(C0690R.C0693id.privacy_policy_layout);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
                layoutParams.removeRule(2);
                relativeLayout.setLayoutParams(layoutParams);
            }
        }
        this.loadingView = (ProgressBar) findViewById(C0690R.C0693id.loading_view);
        if (bundle != null) {
            restoreSavedURL(bundle);
            return;
        }
        this.mFetchAsyncTask = new FetchDetailsFromWeb();
        this.mFetchAsyncTask.execute(new Void[0]);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
    }

    public void onDestroy() {
        Log.m26i(TAG, "onDestroy()");
        FetchDetailsFromWeb fetchDetailsFromWeb = this.mFetchAsyncTask;
        if (fetchDetailsFromWeb != null && fetchDetailsFromWeb.getStatus() == AsyncTask.Status.RUNNING) {
            this.mFetchAsyncTask.cancel(true);
            this.mFetchAsyncTask = null;
        }
        if (this.loadingView != null) {
            this.loadingView = null;
        }
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        WebView webView = (WebView) findViewById(C0690R.C0693id.webview);
        if (webView != null) {
            bundle.putString("SAVED_URL", webView.getUrl());
        }
        super.onSaveInstanceState(bundle);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finishWebTos();
        return false;
    }

    public void onBackPressed() {
        finishWebTos();
        super.onBackPressed();
    }

    public void onDialogResult(DialogFragment dialogFragment, Bundle bundle) {
        int i = bundle.getInt(DialogFactory.BUNDLE_REQUEST_CODE);
        int i2 = bundle.getInt("result_code");
        if (i == 10 && i2 == -1) {
            finishWebTos();
        }
    }

    private void enableNextButton(boolean z) {
        LinearLayout linearLayout = this.mLayoutButtonNext;
        if (linearLayout != null) {
            linearLayout.setEnabled(z);
            this.mLayoutButtonNext.setFocusable(z);
            this.mLayoutButtonNext.setAlpha(z ? 1.0f : 0.25f);
            if (Settings.isEnabledShowButtonBG()) {
                this.mLayoutButtonNext.setBackgroundResource(z ? C0690R.C0692drawable.voice_note_prev_next_btn_background : C0690R.C0692drawable.voice_note_prev_next_btn_shape_drawable_dim);
            }
        }
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getStringTosContentBasedOnCountry() {
        /*
            r2 = this;
            java.util.Locale r0 = java.util.Locale.getDefault()
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "stt_language_locale"
            java.lang.String r0 = com.sec.android.app.voicenote.provider.Settings.getStringSettings(r1, r0)
            int r1 = r0.hashCode()
            switch(r1) {
                case 95406413: goto L_0x00af;
                case 96598143: goto L_0x00a5;
                case 96598594: goto L_0x009b;
                case 96747053: goto L_0x0091;
                case 96747549: goto L_0x0087;
                case 97640813: goto L_0x007d;
                case 100471053: goto L_0x0073;
                case 100828572: goto L_0x0068;
                case 102169200: goto L_0x005d;
                case 106935481: goto L_0x0053;
                case 108812813: goto L_0x0047;
                case 115813226: goto L_0x003b;
                case 115813378: goto L_0x002f;
                case 115813715: goto L_0x0023;
                case 115813762: goto L_0x0017;
                default: goto L_0x0015;
            }
        L_0x0015:
            goto L_0x00b9
        L_0x0017:
            java.lang.String r1 = "zh-TW"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00b9
            r0 = 11
            goto L_0x00ba
        L_0x0023:
            java.lang.String r1 = "zh-SG"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00b9
            r0 = 13
            goto L_0x00ba
        L_0x002f:
            java.lang.String r1 = "zh-HK"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00b9
            r0 = 12
            goto L_0x00ba
        L_0x003b:
            java.lang.String r1 = "zh-CN"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00b9
            r0 = 10
            goto L_0x00ba
        L_0x0047:
            java.lang.String r1 = "ru-RU"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00b9
            r0 = 8
            goto L_0x00ba
        L_0x0053:
            java.lang.String r1 = "pt-BR"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00b9
            r0 = 7
            goto L_0x00ba
        L_0x005d:
            java.lang.String r1 = "ko-KR"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00b9
            r0 = 9
            goto L_0x00ba
        L_0x0068:
            java.lang.String r1 = "ja-JP"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00b9
            r0 = 14
            goto L_0x00ba
        L_0x0073:
            java.lang.String r1 = "it-IT"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00b9
            r0 = 6
            goto L_0x00ba
        L_0x007d:
            java.lang.String r1 = "fr-FR"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00b9
            r0 = 5
            goto L_0x00ba
        L_0x0087:
            java.lang.String r1 = "es-US"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00b9
            r0 = 3
            goto L_0x00ba
        L_0x0091:
            java.lang.String r1 = "es-ES"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00b9
            r0 = 4
            goto L_0x00ba
        L_0x009b:
            java.lang.String r1 = "en-US"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00b9
            r0 = 2
            goto L_0x00ba
        L_0x00a5:
            java.lang.String r1 = "en-GB"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00b9
            r0 = 1
            goto L_0x00ba
        L_0x00af:
            java.lang.String r1 = "de-DE"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00b9
            r0 = 0
            goto L_0x00ba
        L_0x00b9:
            r0 = -1
        L_0x00ba:
            r1 = 2131755645(0x7f10027d, float:1.9142175E38)
            switch(r0) {
                case 0: goto L_0x016e;
                case 1: goto L_0x0162;
                case 2: goto L_0x0159;
                case 3: goto L_0x014d;
                case 4: goto L_0x0141;
                case 5: goto L_0x0135;
                case 6: goto L_0x0129;
                case 7: goto L_0x011d;
                case 8: goto L_0x0111;
                case 9: goto L_0x0105;
                case 10: goto L_0x00f9;
                case 11: goto L_0x00ed;
                case 12: goto L_0x00e1;
                case 13: goto L_0x00d5;
                case 14: goto L_0x00c9;
                default: goto L_0x00c0;
            }
        L_0x00c0:
            android.content.res.Resources r0 = r2.getResources()
            java.lang.String r0 = r0.getString(r1)
            return r0
        L_0x00c9:
            android.content.res.Resources r0 = r2.getResources()
            r1 = 2131755650(0x7f100282, float:1.9142185E38)
            java.lang.String r0 = r0.getString(r1)
            return r0
        L_0x00d5:
            android.content.res.Resources r0 = r2.getResources()
            r1 = 2131755656(0x7f100288, float:1.9142197E38)
            java.lang.String r0 = r0.getString(r1)
            return r0
        L_0x00e1:
            android.content.res.Resources r0 = r2.getResources()
            r1 = 2131755655(0x7f100287, float:1.9142195E38)
            java.lang.String r0 = r0.getString(r1)
            return r0
        L_0x00ed:
            android.content.res.Resources r0 = r2.getResources()
            r1 = 2131755657(0x7f100289, float:1.91422E38)
            java.lang.String r0 = r0.getString(r1)
            return r0
        L_0x00f9:
            android.content.res.Resources r0 = r2.getResources()
            r1 = 2131755654(0x7f100286, float:1.9142193E38)
            java.lang.String r0 = r0.getString(r1)
            return r0
        L_0x0105:
            android.content.res.Resources r0 = r2.getResources()
            r1 = 2131755651(0x7f100283, float:1.9142187E38)
            java.lang.String r0 = r0.getString(r1)
            return r0
        L_0x0111:
            android.content.res.Resources r0 = r2.getResources()
            r1 = 2131755653(0x7f100285, float:1.9142191E38)
            java.lang.String r0 = r0.getString(r1)
            return r0
        L_0x011d:
            android.content.res.Resources r0 = r2.getResources()
            r1 = 2131755652(0x7f100284, float:1.914219E38)
            java.lang.String r0 = r0.getString(r1)
            return r0
        L_0x0129:
            android.content.res.Resources r0 = r2.getResources()
            r1 = 2131755649(0x7f100281, float:1.9142183E38)
            java.lang.String r0 = r0.getString(r1)
            return r0
        L_0x0135:
            android.content.res.Resources r0 = r2.getResources()
            r1 = 2131755648(0x7f100280, float:1.9142181E38)
            java.lang.String r0 = r0.getString(r1)
            return r0
        L_0x0141:
            android.content.res.Resources r0 = r2.getResources()
            r1 = 2131755646(0x7f10027e, float:1.9142177E38)
            java.lang.String r0 = r0.getString(r1)
            return r0
        L_0x014d:
            android.content.res.Resources r0 = r2.getResources()
            r1 = 2131755647(0x7f10027f, float:1.914218E38)
            java.lang.String r0 = r0.getString(r1)
            return r0
        L_0x0159:
            android.content.res.Resources r0 = r2.getResources()
            java.lang.String r0 = r0.getString(r1)
            return r0
        L_0x0162:
            android.content.res.Resources r0 = r2.getResources()
            r1 = 2131755644(0x7f10027c, float:1.9142173E38)
            java.lang.String r0 = r0.getString(r1)
            return r0
        L_0x016e:
            android.content.res.Resources r0 = r2.getResources()
            r1 = 2131755643(0x7f10027b, float:1.9142171E38)
            java.lang.String r0 = r0.getString(r1)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.activity.WebTosActivity.getStringTosContentBasedOnCountry():java.lang.String");
    }

    public String getSystemLocale() {
        Locale locale;
        try {
            locale = new Locale(Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
            Log.m19d(TAG, "current Locale : " + locale);
        } catch (Exception e) {
            Log.m22e(TAG, e.toString());
            locale = Locale.getDefault();
        }
        String replace = locale.toString().replace("_", "-");
        String[] stringArray = getResources().getStringArray(C0690R.array.stt_language_locale);
        for (String str : stringArray) {
            if (str.equals(replace)) {
                return str;
            }
        }
        return stringArray[getResources().getInteger(C0690R.integer.common_default_locale_index)];
    }

    /* access modifiers changed from: private */
    public String getURLString() {
        String systemLocale = getSystemLocale();
        String str = Build.MODEL;
        if (str.startsWith("SAMSUNG-")) {
            str = str.substring(8);
        }
        String str2 = "https://tos.samsung-svoice.com/getTermsVersion?countryCode=" + getSystemsCountryISOCode() + "&cultureCode=" + systemLocale + "&deviceModel=" + str + "&serviceCode=" + "7pz1640152";
        Log.m19d(TAG, "Web TOS URL : " + str2);
        return str2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:?, code lost:
        r2.close();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String covertInputStreamToString(java.io.InputStream r6) throws java.io.IOException {
        /*
            r5 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "inputStream received is : "
            r0.append(r1)
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "WebTosActivity"
            com.sec.android.app.voicenote.provider.Log.m19d(r1, r0)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.io.BufferedReader r2 = new java.io.BufferedReader     // Catch:{ IOException -> 0x0052 }
            java.io.InputStreamReader r3 = new java.io.InputStreamReader     // Catch:{ IOException -> 0x0052 }
            java.nio.charset.Charset r4 = java.nio.charset.Charset.defaultCharset()     // Catch:{ IOException -> 0x0052 }
            r3.<init>(r6, r4)     // Catch:{ IOException -> 0x0052 }
            r2.<init>(r3)     // Catch:{ IOException -> 0x0052 }
            r3 = 0
        L_0x002a:
            java.lang.String r4 = r2.readLine()     // Catch:{ Throwable -> 0x003f }
            if (r4 == 0) goto L_0x0039
            r0.append(r4)     // Catch:{ Throwable -> 0x003f }
            r4 = 10
            r0.append(r4)     // Catch:{ Throwable -> 0x003f }
            goto L_0x002a
        L_0x0039:
            r2.close()     // Catch:{ IOException -> 0x0052 }
            goto L_0x0058
        L_0x003d:
            r4 = move-exception
            goto L_0x0041
        L_0x003f:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x003d }
        L_0x0041:
            if (r3 == 0) goto L_0x004c
            r2.close()     // Catch:{ Throwable -> 0x0047 }
            goto L_0x004f
        L_0x0047:
            r2 = move-exception
            r3.addSuppressed(r2)     // Catch:{ IOException -> 0x0052 }
            goto L_0x004f
        L_0x004c:
            r2.close()     // Catch:{ IOException -> 0x0052 }
        L_0x004f:
            throw r4     // Catch:{ IOException -> 0x0052 }
        L_0x0050:
            r0 = move-exception
            goto L_0x0060
        L_0x0052:
            r2 = move-exception
            java.lang.String r3 = "IOException"
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r1, (java.lang.String) r3, (java.lang.Throwable) r2)     // Catch:{ all -> 0x0050 }
        L_0x0058:
            r6.close()
            java.lang.String r6 = r0.toString()
            return r6
        L_0x0060:
            r6.close()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.activity.WebTosActivity.covertInputStreamToString(java.io.InputStream):java.lang.String");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0044, code lost:
        if (r9 == null) goto L_0x0112;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0046, code lost:
        r9.disconnect();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0099, code lost:
        if (r9 == null) goto L_0x0112;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00d2, code lost:
        if (r9 == null) goto L_0x0112;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x010e, code lost:
        if (r9 == null) goto L_0x0112;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x0112, code lost:
        return r2;
     */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x007e A[SYNTHETIC, Splitter:B:33:0x007e] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00b7 A[SYNTHETIC, Splitter:B:43:0x00b7] */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00f3 A[SYNTHETIC, Splitter:B:53:0x00f3] */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0116 A[SYNTHETIC, Splitter:B:61:0x0116] */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0133  */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:30:0x0064=Splitter:B:30:0x0064, B:50:0x00d9=Splitter:B:50:0x00d9, B:40:0x009f=Splitter:B:40:0x009f} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.json.JSONArray getJSONArray(java.lang.String r9) {
        /*
            r8 = this;
            java.lang.String r0 = "IOException ==> "
            java.lang.String r1 = "WebTosActivity"
            r2 = 0
            java.net.URL r3 = new java.net.URL     // Catch:{ MalformedURLException -> 0x00d6, IOException -> 0x009c, JSONException -> 0x0061, all -> 0x005b }
            r3.<init>(r9)     // Catch:{ MalformedURLException -> 0x00d6, IOException -> 0x009c, JSONException -> 0x0061, all -> 0x005b }
            java.net.URLConnection r9 = r3.openConnection()     // Catch:{ MalformedURLException -> 0x00d6, IOException -> 0x009c, JSONException -> 0x0061, all -> 0x005b }
            javax.net.ssl.HttpsURLConnection r9 = (javax.net.ssl.HttpsURLConnection) r9     // Catch:{ MalformedURLException -> 0x00d6, IOException -> 0x009c, JSONException -> 0x0061, all -> 0x005b }
            java.io.InputStream r3 = r9.getInputStream()     // Catch:{ MalformedURLException -> 0x0057, IOException -> 0x0054, JSONException -> 0x0051, all -> 0x004b }
            if (r3 == 0) goto L_0x0027
            java.lang.String r4 = r8.covertInputStreamToString(r3)     // Catch:{ MalformedURLException -> 0x0024, IOException -> 0x0021, JSONException -> 0x001f }
            org.json.JSONArray r2 = r8.parseJSON(r4)     // Catch:{ MalformedURLException -> 0x0024, IOException -> 0x0021, JSONException -> 0x001f }
            goto L_0x0027
        L_0x001f:
            r4 = move-exception
            goto L_0x0064
        L_0x0021:
            r4 = move-exception
            goto L_0x009f
        L_0x0024:
            r4 = move-exception
            goto L_0x00d9
        L_0x0027:
            if (r3 == 0) goto L_0x0044
            r3.close()     // Catch:{ IOException -> 0x002d }
            goto L_0x0044
        L_0x002d:
            r3 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            java.lang.String r0 = r3.getMessage()
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r0)
        L_0x0044:
            if (r9 == 0) goto L_0x0112
        L_0x0046:
            r9.disconnect()
            goto L_0x0112
        L_0x004b:
            r3 = move-exception
            r7 = r3
            r3 = r2
            r2 = r7
            goto L_0x0114
        L_0x0051:
            r4 = move-exception
            r3 = r2
            goto L_0x0064
        L_0x0054:
            r4 = move-exception
            r3 = r2
            goto L_0x009f
        L_0x0057:
            r4 = move-exception
            r3 = r2
            goto L_0x00d9
        L_0x005b:
            r9 = move-exception
            r3 = r2
            r2 = r9
            r9 = r3
            goto L_0x0114
        L_0x0061:
            r4 = move-exception
            r9 = r2
            r3 = r9
        L_0x0064:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0113 }
            r5.<init>()     // Catch:{ all -> 0x0113 }
            java.lang.String r6 = "JSONException ==> "
            r5.append(r6)     // Catch:{ all -> 0x0113 }
            java.lang.String r4 = r4.getMessage()     // Catch:{ all -> 0x0113 }
            r5.append(r4)     // Catch:{ all -> 0x0113 }
            java.lang.String r4 = r5.toString()     // Catch:{ all -> 0x0113 }
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r4)     // Catch:{ all -> 0x0113 }
            if (r3 == 0) goto L_0x0099
            r3.close()     // Catch:{ IOException -> 0x0082 }
            goto L_0x0099
        L_0x0082:
            r3 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            java.lang.String r0 = r3.getMessage()
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r0)
        L_0x0099:
            if (r9 == 0) goto L_0x0112
            goto L_0x0046
        L_0x009c:
            r4 = move-exception
            r9 = r2
            r3 = r9
        L_0x009f:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0113 }
            r5.<init>()     // Catch:{ all -> 0x0113 }
            r5.append(r0)     // Catch:{ all -> 0x0113 }
            java.lang.String r4 = r4.getMessage()     // Catch:{ all -> 0x0113 }
            r5.append(r4)     // Catch:{ all -> 0x0113 }
            java.lang.String r4 = r5.toString()     // Catch:{ all -> 0x0113 }
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r4)     // Catch:{ all -> 0x0113 }
            if (r3 == 0) goto L_0x00d2
            r3.close()     // Catch:{ IOException -> 0x00bb }
            goto L_0x00d2
        L_0x00bb:
            r3 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            java.lang.String r0 = r3.getMessage()
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r0)
        L_0x00d2:
            if (r9 == 0) goto L_0x0112
            goto L_0x0046
        L_0x00d6:
            r4 = move-exception
            r9 = r2
            r3 = r9
        L_0x00d9:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0113 }
            r5.<init>()     // Catch:{ all -> 0x0113 }
            java.lang.String r6 = "MalformedURLException ==> "
            r5.append(r6)     // Catch:{ all -> 0x0113 }
            java.lang.String r4 = r4.getMessage()     // Catch:{ all -> 0x0113 }
            r5.append(r4)     // Catch:{ all -> 0x0113 }
            java.lang.String r4 = r5.toString()     // Catch:{ all -> 0x0113 }
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r4)     // Catch:{ all -> 0x0113 }
            if (r3 == 0) goto L_0x010e
            r3.close()     // Catch:{ IOException -> 0x00f7 }
            goto L_0x010e
        L_0x00f7:
            r3 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            java.lang.String r0 = r3.getMessage()
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r0)
        L_0x010e:
            if (r9 == 0) goto L_0x0112
            goto L_0x0046
        L_0x0112:
            return r2
        L_0x0113:
            r2 = move-exception
        L_0x0114:
            if (r3 == 0) goto L_0x0131
            r3.close()     // Catch:{ IOException -> 0x011a }
            goto L_0x0131
        L_0x011a:
            r3 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            java.lang.String r0 = r3.getMessage()
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r0)
        L_0x0131:
            if (r9 == 0) goto L_0x0136
            r9.disconnect()
        L_0x0136:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.activity.WebTosActivity.getJSONArray(java.lang.String):org.json.JSONArray");
    }

    private JSONArray parseJSON(String str) throws JSONException {
        Log.m19d(TAG, "jsonString received is : " + str);
        JSONObject jSONObject = new JSONObject(str);
        String optString = jSONObject.optString(RESULT_CODE);
        String optString2 = jSONObject.optString(RESULT_MESSAGE);
        if (optString == null || optString.isEmpty() || !optString.equalsIgnoreCase("0")) {
            if (optString == null || optString.isEmpty()) {
                Log.m22e(TAG, "Error in getting result code or result code is NULL");
            } else {
                Log.m22e(TAG, "Error : " + optString2);
            }
            return null;
        }
        JSONArray optJSONArray = jSONObject.optJSONArray(TERMS);
        Log.m19d(TAG, "Complete JSON array is : " + optJSONArray);
        return optJSONArray;
    }

    private void loadWebView(WebView webView, String str, final ProgressBar progressBar) {
        final String format = String.format("#%06X", new Object[]{Integer.valueOf(ContextCompat.getColor(this, C0690R.C0691color.webview_text_color) & ViewCompat.MEASURED_SIZE_MASK)});
        if (webView == null) {
            Log.m22e(TAG, "webview is null");
            return;
        }
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                webView.loadUrl(str);
                return true;
            }

            public void onPageFinished(WebView webView, String str) {
                webView.loadUrl("javascript:document.body.style.setProperty(\"color\", \"" + format + "\")");
                progressBar.setVisibility(8);
                webView.setVisibility(0);
            }
        });
        webView.setBackgroundColor(getColor(C0690R.C0691color.webview_background_color));
        webView.setScrollBarStyle(0);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setJavaScriptEnabled(true);
        if (str != null && !str.isEmpty()) {
            webView.loadUrl(str);
        }
    }

    /* access modifiers changed from: private */
    public void launchWebView(String str, String str2) {
        Log.m26i(TAG, "launchWebView");
        View inflate = getLayoutInflater().inflate(C0690R.layout.disclaimer_web_view, (ViewGroup) null);
        WebView webView = (WebView) inflate.findViewById(C0690R.C0693id.webview);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, C0690R.style.WebTos);
        builder.setView(inflate);
        TextView textView = (TextView) inflate.findViewById(C0690R.C0693id.title_view);
        ProgressBar progressBar = (ProgressBar) inflate.findViewById(C0690R.C0693id.disclaimer_loading_view);
//        if (Build.VERSION.SEM_INT < 2401) {
//            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) textView.getLayoutParams();
//            layoutParams.topMargin = getResources().getDimensionPixelSize(C0690R.dimen.webview_title_top_margin);
//            textView.setLayoutParams(layoutParams);
//        }
        textView.setText(str2);
        loadWebView(webView, str, progressBar);
        builder.show();
    }

    private String getSystemsCountryISOCode() {
//        return SemSystemProperties.getCountryIso();
        return "Blabla stub";
    }

    /* access modifiers changed from: private */
    public void finishWebTos() {
        int i;
        if (VoiceNoteApplication.getScene() == 1 && ((i = this.mLaunchMode) == 0 || i == 1)) {
            restoreSttMode();
        }
        finish();
    }

    private void restoreSttMode() {
        Settings.setSettings("record_mode", 4);
        VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.CHANGE_MODE));
    }

    private class FetchDetailsFromWeb extends AsyncTask<Void, Void, ArrayList<TosDetails>> {
        private ArrayList<TosDetails> result;

        private FetchDetailsFromWeb() {
            this.result = null;
        }

        /* access modifiers changed from: protected */
        public ArrayList<TosDetails> doInBackground(Void... voidArr) {
            Log.m19d(WebTosActivity.TAG, "doInBackground");
            getTosDetails(WebTosActivity.this.getURLString());
            return this.result;
        }

        private void getTosDetails(String str) {
            Log.m19d(WebTosActivity.TAG, "getTosDetails urlString : " + str);
            JSONArray access$200 = WebTosActivity.this.getJSONArray(str);
            if (access$200 != null) {
                this.result = new ArrayList<>();
                int length = access$200.length();
                int i = 0;
                while (i < length) {
                    try {
                        JSONObject jSONObject = access$200.getJSONObject(i);
                        TosDetails tosDetails = new TosDetails();
                        tosDetails.setId(jSONObject.optString("id"));
                        tosDetails.setName(jSONObject.optString("name"));
                        tosDetails.setUrlString(jSONObject.optString(WebTosActivity.URL));
                        tosDetails.setMandatory(Boolean.valueOf(jSONObject.optString(WebTosActivity.MANDATORY)).booleanValue());
                        tosDetails.setExtraValue(jSONObject.optString(WebTosActivity.EXTRA));
                        this.result.add(tosDetails);
                        i++;
                    } catch (JSONException unused) {
                        Log.m19d(WebTosActivity.TAG, "getTosDetails : JSONException");
                        return;
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(ArrayList<TosDetails> arrayList) {
            Log.m26i(WebTosActivity.TAG, "onPostExecute");
            if (!Network.isNetworkConnected(WebTosActivity.this.getApplicationContext())) {
                Bundle bundle = new Bundle();
                bundle.putInt(DialogFactory.BUNDLE_REQUEST_CODE, 10);
                if (WebTosActivity.this.getSupportFragmentManager() != null && !WebTosActivity.this.getSupportFragmentManager().isDestroyed()) {
                    DialogFactory.show(WebTosActivity.this.getSupportFragmentManager(), DialogFactory.NETWORK_NOT_CONNECTED, bundle, WebTosActivity.this);
                }
                WebTosActivity.this.loadingView.setVisibility(8);
            } else if (arrayList == null || arrayList.isEmpty()) {
                Toast.makeText(WebTosActivity.this, C0690R.string.server_error_msg, 0).show();
                WebTosActivity.this.loadingView.setVisibility(8);
                WebTosActivity.this.finishWebTos();
            } else {
                WebTosActivity.this.handleLinkFromServer(arrayList);
            }
            super.onPostExecute(arrayList);
        }
    }

    protected static class TermsViewHolder {
        TextView mUrlName;

        protected TermsViewHolder() {
        }
    }

    private class MandatoryTermsAdapter extends ArrayAdapter<TosDetails> {
        private List<TosDetails> mMandatoryList;

        public MandatoryTermsAdapter(int i, List<TosDetails> list) {
            super(WebTosActivity.this, i, list);
            this.mMandatoryList = list;
        }

        @NonNull
        public View getView(int i, View view, @NonNull ViewGroup viewGroup) {
            List<TosDetails> list = this.mMandatoryList;
            if (list == null) {
                return view;
            }
            String urlString = list.get(i).getUrlString();
            if (view == null) {
                view = ((LayoutInflater) WebTosActivity.this.getSystemService("layout_inflater")).inflate(C0690R.layout.item_disclaimer_mandatory, viewGroup, false);
                TermsViewHolder termsViewHolder = new TermsViewHolder();
                termsViewHolder.mUrlName = (TextView) view.findViewById(C0690R.C0693id.url_link);
                view.setTag(termsViewHolder);
            }
            TermsViewHolder termsViewHolder2 = (TermsViewHolder) view.getTag();
            TextView textView = termsViewHolder2.mUrlName;
            textView.setText(Html.fromHtml("<u>" + this.mMandatoryList.get(i).getName() + "", 0));
            if (urlString != null && !urlString.isEmpty()) {
//                termsViewHolder2.mUrlName.setOnClickListener(new View.OnClickListener(urlString, i) {
//                    private final /* synthetic */ String f$1;
//                    private final /* synthetic */ int f$2;

//                    {
////                        this.f$1 = r2;
////                        this.f$2 = r3;
//                    }
//
//                    public final void onClick(View view) {
////                        WebTosActivity.MandatoryTermsAdapter.this.lambda$getView$0$WebTosActivity$MandatoryTermsAdapter(this.f$1, this.f$2, view);
//                    }
//                });
            }
            return view;
        }

        public /* synthetic */ void lambda$getView$0$WebTosActivity$MandatoryTermsAdapter(String str, int i, View view) {
            WebTosActivity.this.launchWebView(str, this.mMandatoryList.get(i).getName());
        }
    }

    private class OptionalTermsAdapter extends ArrayAdapter<TosDetails> {
        private List<TosDetails> mOptionalList;

        public OptionalTermsAdapter(int i, List<TosDetails> list) {
            super(WebTosActivity.this, i, list);
            this.mOptionalList = list;
        }

        @NonNull
        public View getView(int i, View view, @NonNull ViewGroup viewGroup) {
            if (view == null) {
                view = View.inflate(WebTosActivity.this.getBaseContext(), C0690R.layout.item_disclaimer_optional, (ViewGroup) null);
                TermsViewHolder termsViewHolder = new TermsViewHolder();
                termsViewHolder.mUrlName = (TextView) view.findViewById(C0690R.C0693id.url_link);
                view.setTag(termsViewHolder);
            }
            TermsViewHolder termsViewHolder2 = (TermsViewHolder) view.getTag();
            TextView textView = termsViewHolder2.mUrlName;
            textView.setText(Html.fromHtml("<u>" + this.mOptionalList.get(i).getName() + "", 0));
//            termsViewHolder2.mUrlName.setOnClickListener(new View.OnClickListener(i) {
//                private final /* synthetic */ int f$1;
//
//                {
//                    this.f$1 = r2;
//                }
//
//                public final void onClick(View view) {
//                    WebTosActivity.OptionalTermsAdapter.this.lambda$getView$0$WebTosActivity$OptionalTermsAdapter(this.f$1, view);
//                }
//            });
            return view;
        }

        public /* synthetic */ void lambda$getView$0$WebTosActivity$OptionalTermsAdapter(int i, View view) {
            String urlString = this.mOptionalList.get(i).getUrlString();
            if (urlString != null && !urlString.isEmpty()) {
                WebTosActivity.this.launchWebView(urlString, this.mOptionalList.get(i).getName());
            }
        }
    }

    protected static class TosDetails {
        private String extra;

        /* renamed from: id */
        private String f100id;
        private boolean isMandatory;
        private String name;
        /* access modifiers changed from: private */
        public String urlString;

        protected TosDetails() {
        }

        public String getId() {
            return this.f100id;
        }

        public void setId(String str) {
            this.f100id = str;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String str) {
            this.name = str;
        }

        public boolean isMandatory() {
            return this.isMandatory;
        }

        public void setMandatory(boolean z) {
            this.isMandatory = z;
        }

        public String getUrlString() {
            return this.urlString;
        }

        public void setUrlString(String str) {
            this.urlString = str;
        }

        public void setExtraValue(String str) {
            this.extra = str;
        }
    }

    /* access modifiers changed from: private */
    public void handleLinkFromServer(List<TosDetails> list) {
        int i = this.mLaunchMode;
        if (i != 0) {
            if (i == 1) {
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                for (TosDetails next : list) {
                    if (next.isMandatory()) {
                        arrayList.add(next);
                    } else {
                        arrayList2.add(next);
                    }
                }
                if (!arrayList.isEmpty()) {
                    this.mMandatoryLayout.setVisibility(0);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
                    MandatoryTermsAdapter mandatoryTermsAdapter = new MandatoryTermsAdapter(C0690R.layout.item_disclaimer_mandatory, arrayList);
                    this.mMandatoryLayout.removeAllViews();
                    int count = mandatoryTermsAdapter.getCount();
                    for (int i2 = 0; i2 < count; i2++) {
                        this.mMandatoryLayout.addView(mandatoryTermsAdapter.getView(i2, (View) null, (ViewGroup) null), layoutParams);
                    }
                }
                int size = arrayList2.size();
                if (size > 0) {
                    this.mOptionalLayout.setVisibility(0);
                    LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -2);
                    OptionalTermsAdapter optionalTermsAdapter = new OptionalTermsAdapter(C0690R.layout.item_disclaimer_optional, arrayList2);
                    this.mOptionalLayout.removeAllViews();
                    for (int i3 = 0; i3 < size; i3++) {
                        this.mOptionalLayout.addView(optionalTermsAdapter.getView(i3, (View) null, (ViewGroup) null), layoutParams2);
                    }
                }
                ProgressBar progressBar = this.loadingView;
                if (progressBar != null) {
                    progressBar.setVisibility(8);
                    return;
                }
                return;
            } else if (i != 2) {
                return;
            }
        }
        if (list.size() > 1) {
            loadDataOnWebView(list.get(1).urlString);
        }
    }

    private void loadDataOnWebView(String str) {
        WebView webView = (WebView) findViewById(C0690R.C0693id.webview);
        if (webView != null) {
            webView.setWebViewClient(new WebViewClient() {
                public void onPageFinished(WebView webView, String str) {
                    if (WebTosActivity.this.loadingView != null) {
                        WebTosActivity.this.loadingView.setVisibility(8);
                    }
                    String format = String.format("#%06X", new Object[]{Integer.valueOf(16777215 & ContextCompat.getColor(WebTosActivity.this, C0690R.C0691color.webview_text_color))});
                    webView.loadUrl("javascript:document.body.style.setProperty(\"color\", \"" + format + "\")");
                    webView.setVisibility(0);
                }
            });
            webView.setScrollBarStyle(33554432);
            webView.setBackgroundColor(getResources().getColor(C0690R.C0691color.webview_background_color, (Resources.Theme) null));
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDefaultFontSize(15);
            if (str != null && !str.isEmpty()) {
                webView.loadUrl(str);
            }
        }
    }

    private void restoreSavedURL(Bundle bundle) {
        this.mSavedURL = bundle.getString("SAVED_URL");
        String str = this.mSavedURL;
        if (str == null || str.isEmpty() || this.mSavedURL.equals(getURLString())) {
            this.mFetchAsyncTask = new FetchDetailsFromWeb();
            this.mFetchAsyncTask.execute(new Void[0]);
            return;
        }
        loadDataOnWebView(this.mSavedURL);
    }
}
