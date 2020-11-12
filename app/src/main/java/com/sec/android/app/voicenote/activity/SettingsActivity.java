package com.sec.android.app.voicenote.activity;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.p007ui.PreferenceSettingFragment;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.UpdateProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.receiver.AudioDeviceReceiver;

public class SettingsActivity extends BaseToolbarActivity implements View.OnClickListener {
    private static final String TAG = "SettingsActivity";
    private AudioDeviceReceiver mAudioDeviceReceiver = null;
    private FrameLayout mSettingsContentLayout;

    public boolean isCollapsingToolbarEnable() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) C0690R.layout.activity_settings);
        setDisplayShowHomeEnabled();
        setTitleActivity((int) C0690R.string.voice_recorder_settings);
        setCollapsingToolbarTitle(getResources().getString(C0690R.string.voice_recorder_settings));
        if (getResources().getConfiguration().orientation == 2 && DisplayManager.getMultiwindowMode() != 2) {
            getWindow().setFlags(1024, 1024);
        }
        this.mSettingsContentLayout = (FrameLayout) findViewById(C0690R.C0693id.settings_content);
        getSupportFragmentManager().beginTransaction().replace(C0690R.C0693id.settings_content, new PreferenceSettingFragment()).commit();
        updateLayoutTablet();
        showTipsIfNeeded();
        this.mAudioDeviceReceiver = new AudioDeviceReceiver(this);
        this.mAudioDeviceReceiver.registerListener();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_settings), getResources().getString(C0690R.string.event_setting_back));
        finish();
        return true;
    }

    public void onConfigurationChanged(@NonNull Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateLayoutTablet();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        AudioDeviceReceiver audioDeviceReceiver = this.mAudioDeviceReceiver;
        if (audioDeviceReceiver != null) {
            audioDeviceReceiver.unregisterListener();
            this.mAudioDeviceReceiver = null;
        }
        super.onDestroy();
    }

    private void showTipsIfNeeded() {
        Log.m19d(TAG, "showTipsIfNeeded");
        if (isUpdateTipsNeeded()) {
            setVisibilityUpdateTips(0);
            updateTipsLayout();
            return;
        }
        setVisibilityUpdateTips(8);
    }

    @SuppressLint({"StringFormatInvalid"})
    private void updateTipsLayout() {
        Log.m19d(TAG, "updateTipsLayout");
        ((TextView) findViewById(C0690R.C0693id.update_tips_description)).setText(getString(C0690R.string.settings_update_tips_description, new Object[]{getString(C0690R.string.app_name)}));
        findViewById(C0690R.C0693id.update_tips_update_button).setOnClickListener(this);
        findViewById(C0690R.C0693id.update_tips_clear_button).setOnClickListener(this);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == C0690R.C0693id.update_tips_clear_button) {
            clearTipsLayout();
        } else if (id == C0690R.C0693id.update_tips_update_button) {
            moveToGalaxyApps();
        }
    }

    private void updateLayoutTablet() {
        if (VoiceNoteFeature.FLAG_IS_TABLET) {
            double d = 2.5d;
            if (DisplayManager.isCurrentWindowOnLandscape(this)) {
                d = 6.25d;
            }
            int currentScreenWidth = (int) ((d / 100.0d) * ((double) DisplayManager.getCurrentScreenWidth(this)));
            FrameLayout frameLayout = this.mSettingsContentLayout;
            if (frameLayout != null) {
                frameLayout.setPadding(currentScreenWidth, frameLayout.getPaddingTop(), currentScreenWidth, this.mSettingsContentLayout.getPaddingBottom());
            }
        }
    }

    private void setVisibilityUpdateTips(int i) {
        Log.m19d(TAG, "setVisibilityUpdateTips visibility : " + i);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(C0690R.C0693id.update_tips_parent);
//        if (i == 0) {
//            relativeLayout.semSetRoundedCorners(15);
//        }
        relativeLayout.setVisibility(i);
    }

    private void moveToGalaxyApps() {
        Log.m19d(TAG, "moveToGalaxyApps");
        UpdateProvider.getInstance().callGalaxyApps(this);
        Settings.setSettings(Settings.KEY_LAST_DISMISS_UPDATE_TIPS_APP_VERSION, Settings.getStringSettings(Settings.KEY_CURRENT_GALAXY_APP_VERSION));
    }

    private void clearTipsLayout() {
        Log.m19d(TAG, "clearTipsLayout");
        setVisibilityUpdateTips(8);
        Settings.setSettings(Settings.KEY_LAST_DISMISS_UPDATE_TIPS_APP_VERSION, Settings.getStringSettings(Settings.KEY_CURRENT_GALAXY_APP_VERSION));
    }

    private boolean isUpdateTipsNeeded() {
        Log.m19d(TAG, "isUpdateTipsNeeded");
        if (!"2".equals(Settings.getStringSettings(Settings.KEY_UPDATE_CHECK_RESULT_CODE, UpdateProvider.StubCodes.UPDATE_CHECK_FAIL))) {
            return false;
        }
        String stringSettings = Settings.getStringSettings(Settings.KEY_LAST_DISMISS_UPDATE_TIPS_APP_VERSION);
        String stringSettings2 = Settings.getStringSettings(Settings.KEY_CURRENT_GALAXY_APP_VERSION);
        Log.m19d(TAG, "isUpdateTipsNeeded dismissVersion : " + stringSettings);
        Log.m19d(TAG, "isUpdateTipsNeeded serverVersion : " + stringSettings2);
        if (stringSettings == null || stringSettings.isEmpty() || !stringSettings.equals(stringSettings2)) {
            return true;
        }
        return false;
    }
}
