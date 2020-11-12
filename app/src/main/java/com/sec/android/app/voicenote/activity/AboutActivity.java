package com.sec.android.app.voicenote.activity;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Process;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.AndroidForWork;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.p007ui.dialog.DataCheckDialog;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.provider.AssistantProvider;
import com.sec.android.app.voicenote.provider.HWKeyboardProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Network;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.StorageProvider;
import com.sec.android.app.voicenote.provider.UPSMProvider;
import com.sec.android.app.voicenote.provider.UpdateProvider;
import com.sec.android.app.voicenote.provider.ViewProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.util.Observable;
import java.util.Observer;

public class AboutActivity extends BaseToolbarActivity implements DialogFactory.DialogResultListener, UpdateProvider.StubListener, Observer {
    private static final int ABOUT_MODE_MAIN = 1;
    private static final int ABOUT_MODE_OPEN_LICENSE = 2;
    private static final String BUNDLE_MODE = "about_mode";
    private static final String EXCHANGE_PACKAGE_NAME = "com.sec.android.app.voicenote";
    private static final String PACKAGE = "package";
    private static final String SETTING_ACTION = "android.settings.APPLICATION_DETAILS_SETTINGS";
    private static final String TAG = "AboutActivity";
    private TextView mAboutAppName;
    private View mAboutBodyBottomEmptyView;
    private View mAboutBodyTopEmptyView;
    private View mAboutLinkBottomEmptyView;
    private int mAboutMode = 1;
    private ScrollView mAboutPageBody;
    private int mIndex = -1;
    private boolean mIsResumed = false;
    private int mOldIndex = -1;
    private Button mOpenSourceLicense;
    private WebView mOpenSourceLicenseView;
    private Button mPrivacyPolicy;
    private Resources mResource;
    private TextView mRetryButton;
    private TextView mUpdateButton;
    private ProgressBar mVersionLoading;
    private TextView mVersionName;
    private TextView mVersionStatus;
    private int mViewHeight;
    private int mViewWidth;

    private int getProperWidth(float f, int i, int i2) {
        double d = (double) f;
        int i3 = (int) (0.61d * d);
        int i4 = (int) (d * 0.75d);
        if (i < i2) {
            i = i2;
        }
        return i < i3 ? i3 : i > i4 ? i4 : i;
    }

    public boolean isCollapsingToolbarEnable() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) C0690R.layout.activity_about);
        this.mResource = getResources();
        this.mActionBar.setBackgroundDrawable(new ColorDrawable(this.mResource.getColor(C0690R.C0691color.main_dialog_bg, (Resources.Theme) null)));
        setDisplayShowHomeEnabled();
        setCollapsingToolbarTitle("");
        this.mCollapsingToolbarLayout.setBackground(new ColorDrawable(this.mResource.getColor(C0690R.C0691color.main_dialog_bg, (Resources.Theme) null)));
        this.mAboutPageBody = (ScrollView) findViewById(C0690R.C0693id.about_page_body);
        this.mAboutAppName = (TextView) findViewById(C0690R.C0693id.about_app_name);
        this.mAboutAppName.setText(getString(C0690R.string.app_name));
        this.mVersionName = (TextView) findViewById(C0690R.C0693id.about_app_version);
        TextView textView = this.mVersionName;
        String string = getString(C0690R.string.version);
        textView.setText(String.format(string, new Object[]{' ' + VoiceNoteApplication.getApkVersionName()}));
        this.mVersionStatus = (TextView) findViewById(C0690R.C0693id.about_version_status);
        this.mVersionLoading = (ProgressBar) findViewById(C0690R.C0693id.about_version_loading);
        getScreenSize();
        float f = (float) this.mViewWidth;
        if (getResources().getConfiguration().orientation == 2) {
            if (DisplayManager.getMultiwindowMode() != 2) {
                getWindow().setFlags(1024, 1024);
            }
            f = ((float) this.mViewWidth) / 2.0f;
        }
        this.mUpdateButton = (TextView) findViewById(C0690R.C0693id.about_update_button);
        int i = (int) (((double) f) * 0.61d);
        this.mUpdateButton.getLayoutParams().width = i;
        this.mRetryButton = (TextView) findViewById(C0690R.C0693id.retry_button);
        this.mRetryButton.getLayoutParams().width = i;
        this.mRetryButton.setContentDescription(AssistantProvider.getInstance().getContentDesToButton(getString(C0690R.string.retry)));
        this.mAboutBodyTopEmptyView = findViewById(C0690R.C0693id.about_body_top_empty_view);
        this.mAboutBodyBottomEmptyView = findViewById(C0690R.C0693id.about_body_bottom_empty_view);
        this.mAboutLinkBottomEmptyView = findViewById(C0690R.C0693id.about_link_bottom_empty_view);
        this.mOpenSourceLicenseView = (WebView) findViewById(C0690R.C0693id.open_source_license_view);
        this.mOpenSourceLicenseView.setBackgroundColor(this.mResource.getColor(C0690R.C0691color.webview_background_color, (Resources.Theme) null));
        this.mOpenSourceLicenseView.getSettings().setUseWideViewPort(true);
        this.mOpenSourceLicenseView.getSettings().setSupportZoom(true);
        this.mOpenSourceLicenseView.getSettings().setBuiltInZoomControls(true);
        this.mOpenSourceLicenseView.getSettings().setDisplayZoomControls(false);
        this.mOpenSourceLicenseView.getSettings().setLoadWithOverviewMode(true);
        int properWidth = getProperWidth(f, getTextWidth(C0690R.string.open_source_licenses), getTextWidth(C0690R.string.privacy_content_title));
        this.mOpenSourceLicense = (Button) findViewById(C0690R.C0693id.about_open_source_license);
        this.mOpenSourceLicense.setText(getString(C0690R.string.open_source_licenses));
        this.mOpenSourceLicense.getLayoutParams().width = properWidth;
        this.mPrivacyPolicy = (Button) findViewById(C0690R.C0693id.about_privacy_policy);
        this.mPrivacyPolicy.setText(getString(C0690R.string.privacy_content_title));
        this.mPrivacyPolicy.getLayoutParams().width = properWidth;
        if (bundle == null) {
            changeAboutPageMode(1, false);
        } else {
            this.mAboutMode = bundle.getInt(BUNDLE_MODE);
            int i2 = this.mAboutMode;
            if (i2 == 0) {
                changeAboutPageMode(1, false);
            } else {
                changeAboutPageMode(i2, false);
            }
        }
        VoiceNoteObservable.getInstance().addObserver(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0690R.C0695menu.menu_about_page, menu);
        if (this.mAboutMode != 1) {
            menu.findItem(C0690R.C0693id.voice_recorder_app_info).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void pageLinkClick(View view) {
        this.mToolBar.setTitleTextColor(getResources().getColor(C0690R.C0691color.actionbar_text_color, (Resources.Theme) null));
        int id = view.getId();
        if (id == C0690R.C0693id.about_open_source_license) {
            changeAboutPageMode(2, false);
        } else if (id == C0690R.C0693id.about_privacy_policy) {
            if (!VoiceNoteFeature.FLAG_SUPPORT_DATA_CHECK_POPUP || !Settings.getBooleanSettings(Settings.KEY_DATA_CHECK_SHOW_AGAIN, true)) {
                try {
                    startActivity(new Intent(this, WebTosActivity.class));
                } catch (ActivityNotFoundException e) {
                    Log.m24e(TAG, "ActivityNotFoundException", (Throwable) e);
                }
            } else {
                showDataCheckDialog(2);
            }
        }
    }

    public void buttonClick(View view) {
        if (VoiceNoteFeature.FLAG_SUPPORT_DATA_CHECK_POPUP && Settings.getBooleanSettings(Settings.KEY_DATA_CHECK_SHOW_AGAIN, true)) {
            showDataCheckDialog(1);
        } else if (Network.isNetworkConnected(this)) {
            int id = view.getId();
            if (id == C0690R.C0693id.about_update_button) {
                Log.m26i(TAG, "Call galaxyApps to update application");
                SALogProvider.insertSALog(this.mResource.getString(C0690R.string.screen_about_page), this.mResource.getString(C0690R.string.event_update));
                UpdateProvider.getInstance().callGalaxyApps(this);
            } else if (id == C0690R.C0693id.retry_button) {
                Log.m26i(TAG, "Check version update");
                startCheckUpdate(false);
            }
        } else {
            Toast.makeText(this, C0690R.string.no_network_msg, 0).show();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        Log.m26i(TAG, "onResume");
        this.mIsResumed = true;
        if (UPSMProvider.getInstance().isUltraPowerSavingMode()) {
            this.mVersionStatus.setVisibility(8);
            this.mUpdateButton.setVisibility(8);
            this.mRetryButton.setVisibility(8);
        } else if (UpdateProvider.getInstance().hasAvailableGalaxyApp(getApplicationContext())) {
            updateUpdateButton(getUpdateCode());
            if (!getUpdateCode().equals("2")) {
                startCheckUpdate(true);
            }
        } else {
            Log.m26i(TAG, "hasAvailableGalaxyApp false");
            updateLayoutNotGalaxyApp();
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        Log.m26i(TAG, "onPause");
        this.mIsResumed = false;
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        int i = this.mAboutMode;
        if (i != 1) {
            bundle.putInt(BUNDLE_MODE, i);
        }
        super.onSaveInstanceState(bundle);
    }

    public void onDestroy() {
        Log.m26i(TAG, "onDestroy");
        VoiceNoteObservable.getInstance().deleteObserver(this);
        updateLoadingView(false);
        super.onDestroy();
    }

    public void update(Observable observable, Object obj) {
        Integer num = (Integer) obj;
        int intValue = num.intValue();
        Log.m26i(TAG, "update - data : " + intValue);
        int intValue2 = num.intValue();
        if (intValue2 == 11 || intValue2 == 12) {
            updateAboutPageLayout();
        } else if (intValue2 != 965) {
            if (intValue2 == 966 && "mounted".equals(StorageProvider.getExternalStorageStateSd()) && this.mIsResumed && !AndroidForWork.getInstance().isAndroidForWorkMode(this)) {
                if (!DialogFactory.isDialogVisible(getSupportFragmentManager(), DialogFactory.SD_CARD_SELECT_DIALOG)) {
                    DialogFactory.show(getSupportFragmentManager(), DialogFactory.SD_CARD_SELECT_DIALOG, (Bundle) null);
                }
                Settings.setSettings(Settings.KEY_SDCARD_PREVIOUS_STATE, 1);
            }
        } else if (this.mIsResumed) {
            Settings.setSettings(Settings.KEY_STORAGE, 0);
            StorageProvider.resetSDCardWritableDir();
            Settings.setSettings(Settings.KEY_SDCARD_PREVIOUS_STATE, 0);
            DialogFactory.clearDialogByTag(getSupportFragmentManager(), DialogFactory.SD_CARD_SELECT_DIALOG);
        }
    }

    public void onBackPressed() {
        if (this.mAboutMode != 2) {
            super.onBackPressed();
        } else {
            changeAboutPageMode(1, true);
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            if (this.mIsResumed) {
                SALogProvider.insertSALog(this.mResource.getString(C0690R.string.screen_about_page), this.mResource.getString(C0690R.string.event_about_page_back));
                onBackPressed();
            }
            return true;
        } else if (itemId != C0690R.C0693id.voice_recorder_app_info) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            startAppInfoActivity();
            return true;
        }
    }

    private void startAppInfoActivity() {
        ((LauncherApps) getSystemService("launcherapps")).startAppDetailsActivity(new ComponentName("com.sec.android.app.voicenote", "com.sec.android.app.voicenote"), Process.myUserHandle(), (Rect) null, (Bundle) null);
    }

    private void updateLoadingView(boolean z) {
        if (z) {
            this.mVersionStatus.setVisibility(8);
            this.mUpdateButton.setVisibility(8);
            this.mRetryButton.setVisibility(8);
            this.mVersionLoading.setVisibility(0);
        } else {
            this.mVersionLoading.setVisibility(8);
            this.mRetryButton.setVisibility(8);
            this.mVersionStatus.setVisibility(0);
            this.mUpdateButton.setVisibility(0);
        }
        refreshEmptyView();
    }

    private void displayRetryView() {
        this.mVersionLoading.setVisibility(8);
        this.mUpdateButton.setVisibility(8);
        this.mVersionStatus.setText(C0690R.string.check_update_fail);
        this.mVersionStatus.setVisibility(0);
        this.mRetryButton.setText(getString(C0690R.string.retry));
        this.mRetryButton.setVisibility(0);
        refreshEmptyView();
    }

    private void updateLayoutNotGalaxyApp() {
        this.mVersionStatus.setVisibility(8);
        this.mRetryButton.setVisibility(8);
        this.mUpdateButton.setVisibility(8);
        refreshEmptyView();
    }

    private void startCheckUpdate(boolean z) {
        Log.m26i(TAG, "start check and do update in AboutActivity");
        int isCheckUpdateAvailable = UpdateProvider.getInstance().isCheckUpdateAvailable(true);
        if (isCheckUpdateAvailable == 1) {
            UpdateProvider.getInstance().checkUpdate(this);
            updateLoadingView(true);
        } else if (isCheckUpdateAvailable == 2 || isCheckUpdateAvailable == 3) {
            updateUpdateButton("1");
        } else if (isCheckUpdateAvailable == 4) {
            displayRetryView();
        } else if (isCheckUpdateAvailable == 5) {
            displayRetryView();
            if (!z) {
                showDataCheckDialog(1);
            }
        }
    }

    private String getUpdateCode() {
        return Settings.getStringSettings(Settings.KEY_UPDATE_CHECK_RESULT_CODE, "1");
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0053  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x008c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateUpdateButton(java.lang.String r7) {
        /*
            r6 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "updateUpdateButton - update code : "
            r0.append(r1)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "AboutActivity"
            com.sec.android.app.voicenote.provider.Log.m26i(r1, r0)
            int r0 = r7.hashCode()
            r1 = 1444(0x5a4, float:2.023E-42)
            r2 = 3
            r3 = 2
            r4 = 1
            r5 = 0
            if (r0 == r1) goto L_0x0044
            switch(r0) {
                case 48: goto L_0x003a;
                case 49: goto L_0x0030;
                case 50: goto L_0x0026;
                default: goto L_0x0025;
            }
        L_0x0025:
            goto L_0x004e
        L_0x0026:
            java.lang.String r0 = "2"
            boolean r7 = r7.equals(r0)
            if (r7 == 0) goto L_0x004e
            r7 = r5
            goto L_0x004f
        L_0x0030:
            java.lang.String r0 = "1"
            boolean r7 = r7.equals(r0)
            if (r7 == 0) goto L_0x004e
            r7 = r3
            goto L_0x004f
        L_0x003a:
            java.lang.String r0 = "0"
            boolean r7 = r7.equals(r0)
            if (r7 == 0) goto L_0x004e
            r7 = r4
            goto L_0x004f
        L_0x0044:
            java.lang.String r0 = "-1"
            boolean r7 = r7.equals(r0)
            if (r7 == 0) goto L_0x004e
            r7 = r2
            goto L_0x004f
        L_0x004e:
            r7 = -1
        L_0x004f:
            r0 = 8
            if (r7 == 0) goto L_0x008c
            if (r7 == r4) goto L_0x0079
            if (r7 == r3) goto L_0x0079
            if (r7 == r2) goto L_0x005a
            goto L_0x00aa
        L_0x005a:
            android.widget.TextView r7 = r6.mVersionStatus
            r1 = 2131755087(0x7f10004f, float:1.9141043E38)
            r7.setText(r1)
            android.widget.TextView r7 = r6.mUpdateButton
            r7.setVisibility(r0)
            android.widget.TextView r7 = r6.mRetryButton
            r0 = 2131755451(0x7f1001bb, float:1.9141782E38)
            java.lang.String r0 = r6.getString(r0)
            r7.setText(r0)
            android.widget.TextView r7 = r6.mRetryButton
            r7.setVisibility(r5)
            goto L_0x00aa
        L_0x0079:
            android.widget.TextView r7 = r6.mVersionStatus
            r1 = 2131755314(0x7f100132, float:1.9141504E38)
            r7.setText(r1)
            android.widget.TextView r7 = r6.mRetryButton
            r7.setVisibility(r0)
            android.widget.TextView r7 = r6.mUpdateButton
            r7.setVisibility(r0)
            goto L_0x00aa
        L_0x008c:
            android.widget.TextView r7 = r6.mVersionStatus
            r1 = 2131755352(0x7f100158, float:1.914158E38)
            r7.setText(r1)
            android.widget.TextView r7 = r6.mRetryButton
            r7.setVisibility(r0)
            android.widget.TextView r7 = r6.mUpdateButton
            r0 = 2131755626(0x7f10026a, float:1.9142137E38)
            java.lang.String r0 = r6.getString(r0)
            r7.setText(r0)
            android.widget.TextView r7 = r6.mUpdateButton
            r7.setVisibility(r5)
        L_0x00aa:
            r6.refreshEmptyView()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.activity.AboutActivity.updateUpdateButton(java.lang.String):void");
    }

    private void changeAboutPageMode(int i, boolean z) {
        String str;
        final String format = String.format("#%06X", new Object[]{Integer.valueOf(ContextCompat.getColor(this, C0690R.C0691color.webview_text_color) & ViewCompat.MEASURED_SIZE_MASK)});
        if (i != 2) {
            this.mOpenSourceLicenseView.loadUrl("about:blank");
            this.mOpenSourceLicenseView.setVisibility(8);
            this.mAboutPageBody.setVisibility(0);
            if (!z) {
                updateAboutPageLayout();
            }
            str = "";
        } else {
            this.mAboutPageBody.setVisibility(8);
            this.mOpenSourceLicenseView.setWebViewClient(new WebViewClient() {
                public void onPageFinished(WebView webView, String str) {
                    webView.loadUrl("javascript:document.body.style.setProperty(\"color\", \"" + format + "\")");
                }
            });
            this.mOpenSourceLicenseView.loadUrl("file:///android_asset/html/Opensource_Announcement.html");
            this.mOpenSourceLicenseView.getSettings().setJavaScriptEnabled(true);
            this.mOpenSourceLicenseView.setVisibility(0);
            str = getString(C0690R.string.open_source_licenses);
        }
        setDisplayShowHomeEnabled();
        setTitleActivity(str);
        setCollapsingToolbarTitle(str);
        this.mAboutMode = i;
        invalidateOptionsMenu();
    }

    private void updateAboutPageLayout() {
        int i;
        int i2;
        int i3;
        int i4;
        Log.m26i(TAG, "updateAboutPageLayout");
        ScrollView scrollView = this.mAboutPageBody;
        if (scrollView != null && scrollView.getVisibility() == 0) {
            if (HWKeyboardProvider.isHWKeyboard(this)) {
                i4 = this.mResource.getDimensionPixelSize(C0690R.dimen.hw_keyboard_about_page_link_item_margin_bottom);
                i3 = this.mResource.getDimensionPixelSize(C0690R.dimen.hw_keyboard_about_version_status_margin_top);
                i2 = this.mResource.getDimensionPixelSize(C0690R.dimen.hw_keyboard_about_app_name_margin_top);
                i = this.mResource.getDimensionPixelSize(C0690R.dimen.hw_keyboard_about_app_version_margin_top);
            } else {
                i4 = this.mResource.getDimensionPixelSize(C0690R.dimen.about_page_link_item_margin_bottom);
                i3 = this.mResource.getDimensionPixelSize(C0690R.dimen.about_version_status_margin_top);
                i2 = this.mResource.getDimensionPixelSize(C0690R.dimen.about_app_name_margin_top);
                i = this.mResource.getDimensionPixelSize(C0690R.dimen.about_app_version_margin_top);
            }
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mPrivacyPolicy.getLayoutParams();
            layoutParams.bottomMargin = i4;
            this.mPrivacyPolicy.setLayoutParams(layoutParams);
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mVersionStatus.getLayoutParams();
            layoutParams2.topMargin = i3;
            this.mVersionStatus.setLayoutParams(layoutParams2);
            LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) this.mVersionLoading.getLayoutParams();
            layoutParams3.topMargin = i3;
            this.mVersionLoading.setLayoutParams(layoutParams3);
            LinearLayout.LayoutParams layoutParams4 = (LinearLayout.LayoutParams) this.mAboutAppName.getLayoutParams();
            layoutParams4.topMargin = i2;
            this.mAboutAppName.setLayoutParams(layoutParams4);
            LinearLayout.LayoutParams layoutParams5 = (LinearLayout.LayoutParams) this.mVersionName.getLayoutParams();
            layoutParams5.topMargin = i;
            this.mVersionName.setLayoutParams(layoutParams5);
            setMaxTextSize();
        }
    }

    private void refreshEmptyView() {
        View view = this.mAboutBodyTopEmptyView;
        if (view != null) {
            view.getLayoutParams().height = (int) (((double) this.mViewHeight) * 0.07d);
        }
        View view2 = this.mAboutBodyBottomEmptyView;
        if (view2 != null) {
            view2.getLayoutParams().height = (int) (((double) this.mViewHeight) * 0.07d);
        }
        View view3 = this.mAboutLinkBottomEmptyView;
        if (view3 != null) {
            view3.getLayoutParams().height = (int) (((double) this.mViewHeight) * 0.05d);
        }
    }

    private void getScreenSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.mViewHeight = displayMetrics.heightPixels;
        this.mViewWidth = displayMetrics.widthPixels;
    }

    private int getTextWidth(int i) {
        String string = getString(i);
        Paint paint = new Paint();
        paint.setTypeface(Typeface.create("sec-roboto-light", 0));
        paint.setTextSize((float) ((int) TypedValue.applyDimension(2, (float) 15, this.mResource.getDisplayMetrics())));
        Rect rect = new Rect();
        paint.getTextBounds(string, 0, string.length(), rect);
        return rect.width();
    }

    private void setMaxTextSize() {
        ViewProvider.setMaxFontSize(getBaseContext(), this.mAboutAppName);
        ViewProvider.setMaxFontSize(getBaseContext(), this.mVersionName);
        ViewProvider.setMaxFontSize(getBaseContext(), this.mVersionStatus);
        ViewProvider.setMaxFontSize(getBaseContext(), this.mUpdateButton);
        ViewProvider.setMaxFontSize(getBaseContext(), this.mRetryButton);
        ViewProvider.setMaxFontSize(getBaseContext(), this.mPrivacyPolicy);
        ViewProvider.setMaxFontSize(getBaseContext(), this.mOpenSourceLicense);
    }

    private void showDataCheckDialog(int i) {
        if (!DialogFactory.isDialogVisible(getSupportFragmentManager(), DialogFactory.DATA_CHECK_DIALOG)) {
            Log.m26i(TAG, "showDataCheckDialog module: 1");
            Bundle bundle = new Bundle();
            bundle.putInt(DialogFactory.BUNDLE_REQUEST_CODE, 8);
            bundle.putInt(DialogFactory.BUNDLE_DATA_CHECK_MODULE, i);
            DialogFactory.show(getSupportFragmentManager(), DialogFactory.DATA_CHECK_DIALOG, bundle, this);
        }
    }

    public void onDialogResult(DialogFragment dialogFragment, Bundle bundle) {
        if (bundle != null) {
            int i = bundle.getInt(DialogFactory.BUNDLE_REQUEST_CODE);
            int i2 = bundle.getInt("result_code");
            int i3 = bundle.getInt(DataCheckDialog.MODULE, 1);
            Log.m26i(TAG, "onDialogResult - requestCode : " + i + " result : " + i2);
            if (i != 8 || i2 != -1) {
                return;
            }
            if (i3 == 2) {
                try {
                    startActivity(new Intent(this, WebTosActivity.class));
                } catch (ActivityNotFoundException e) {
                    Log.m24e(TAG, "ActivityNotFoundException", (Throwable) e);
                }
            } else if (i3 == 1) {
                startCheckUpdate(false);
            }
        }
    }

    public void onUpdateCheckFail(UpdateProvider.StubData stubData) {
        Log.m19d(TAG, "Application update check fail.");
        Settings.setSettings(Settings.KEY_UPDATE_CHECK_RESULT_CODE, UpdateProvider.StubCodes.UPDATE_CHECK_FAIL);
        updateLoadingView(false);
        updateUpdateButton(UpdateProvider.StubCodes.UPDATE_CHECK_FAIL);
    }

    public void onNoMatchingApplication(UpdateProvider.StubData stubData) {
        Log.m19d(TAG, "Application not matched.");
        Settings.setSettings(Settings.KEY_UPDATE_CHECK_RESULT_CODE, "0");
        updateLoadingView(false);
        updateUpdateButton("0");
    }

    public void onUpdateNotNecessary(UpdateProvider.StubData stubData) {
        Log.m19d(TAG, "Application update not necessary.");
        Settings.setSettings(Settings.KEY_UPDATE_CHECK_RESULT_CODE, "1");
        updateLoadingView(false);
        updateUpdateButton("1");
    }

    public void onUpdateAvailable(UpdateProvider.StubData stubData) {
        Log.m19d(TAG, "Application update available: " + stubData.getVersionName());
        Settings.setSettings(Settings.KEY_UPDATE_CHECK_RESULT_CODE, "2");
        Settings.setSettings(Settings.KEY_CURRENT_GALAXY_APP_VERSION, stubData.getVersionCode());
        updateLoadingView(false);
        updateUpdateButton("2");
    }
}
