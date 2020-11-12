package com.sec.android.app.voicenote.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.provider.DesktopModeProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.util.Observable;
import java.util.Observer;

public class RecordStereoActivity extends BaseToolbarActivity implements Observer {
    private static final String TAG = "RecordStereoActivity";
    private RelativeLayout mStereoLayout;
    private LinearLayout mStereoMainLayout;
    /* access modifiers changed from: private */
    public TextView mStereoText;
    private SwitchCompat mSwitchStereo;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        Log.m26i(TAG, "onCreate " + bundle);
        super.onCreate(bundle);
        setContentView((int) C0690R.layout.activity_record_stereo);
        setDisplayShowHomeEnabled();
        setTitleActivity((int) C0690R.string.recording_stereo);
        if (getResources().getConfiguration().orientation == 2 && DisplayManager.getMultiwindowMode() != 2) {
            getWindow().setFlags(1024, 1024);
        }
        this.mStereoLayout = (RelativeLayout) findViewById(C0690R.C0693id.stereo_layout);
        this.mSwitchStereo = (SwitchCompat) findViewById(C0690R.C0693id.switch_stereo);
        TextView textView = (TextView) findViewById(C0690R.C0693id.image_text);
        LinearLayout linearLayout = (LinearLayout) findViewById(C0690R.C0693id.stereo_text_layout);
        this.mStereoMainLayout = (LinearLayout) findViewById(C0690R.C0693id.stereo_main_layout);
        if (VoiceNoteFeature.FLAG_IS_TABLET) {
            textView.setText(getString(C0690R.string.tablet_recording_stereo_summary));
        } else {
            textView.setText(getString(C0690R.string.recording_stereo_summary));
        }
        updateLayoutTablet();
        this.mStereoText = (TextView) findViewById(C0690R.C0693id.stereo_text);
        this.mSwitchStereo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                RecordStereoActivity.this.lambda$onCreate$0$RecordStereoActivity(compoundButton, z);
            }
        });
        this.mStereoLayout.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                RecordStereoActivity.this.lambda$onCreate$1$RecordStereoActivity(view);
            }
        });
        this.mStereoLayout.post(new Runnable() {
            public final void run() {
                RecordStereoActivity.this.lambda$onCreate$2$RecordStereoActivity();
            }
        });
        VoiceNoteObservable.getInstance().addObserver(this);
    }

    public /* synthetic */ void lambda$onCreate$0$RecordStereoActivity(CompoundButton compoundButton, boolean z) {
        if (this.mSwitchStereo.isChecked()) {
            Settings.setSettings(Settings.KEY_REC_STEREO, true);
            this.mStereoText.setText(this.mResource.getString(C0690R.string.f93on));
            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_stereo), this.mResource.getString(C0690R.string.event_record_audio_in_stereo), "1");
            SALogProvider.insertStatusLog(SALogProvider.KEY_SA_RECORD_AUDIO_IN_STEREO_TYPE, "1");
        } else {
            Settings.setSettings(Settings.KEY_REC_STEREO, false);
            this.mStereoText.setText(this.mResource.getString(C0690R.string.off));
            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_stereo), this.mResource.getString(C0690R.string.event_record_audio_in_stereo), "0");
            SALogProvider.insertStatusLog(SALogProvider.KEY_SA_RECORD_AUDIO_IN_STEREO_TYPE, "0");
        }
        updateStereoLayout(this.mSwitchStereo.isChecked());
    }

    public /* synthetic */ void lambda$onCreate$1$RecordStereoActivity(View view) {
        if (this.mSwitchStereo.isChecked()) {
            this.mSwitchStereo.setChecked(false);
        } else {
            this.mSwitchStereo.setChecked(true);
        }
    }

    public /* synthetic */ void lambda$onCreate$2$RecordStereoActivity() {
        this.mStereoLayout.setAccessibilityDelegate(new View.AccessibilityDelegate() {
            public void sendAccessibilityEvent(View view, int i) {
                super.sendAccessibilityEvent(view, i);
                if (i == 32768) {
                    view.setContentDescription(RecordStereoActivity.this.mResource.getString(C0690R.string.record_in_stereo) + " , " + RecordStereoActivity.this.mStereoText.getText() + " , " + RecordStereoActivity.this.mResource.getString(C0690R.string.switch_tts));
                }
            }
        });
    }

    private void updateStereoLayout(boolean z) {
        RelativeLayout relativeLayout = this.mStereoLayout;
        if (relativeLayout == null) {
            return;
        }
        if (z) {
            relativeLayout.setBackground(ContextCompat.getDrawable(this, C0690R.C0692drawable.master_on_off_switch_bg));
            this.mStereoText.setTextColor(ContextCompat.getColor(this, C0690R.C0691color.stereo_recording_on_text_color));
            return;
        }
        relativeLayout.setBackground(ContextCompat.getDrawable(this, C0690R.C0692drawable.master_on_off_switch_unswitch_bg));
        this.mStereoText.setTextColor(ContextCompat.getColor(this, C0690R.C0691color.stereo_recording_off_text_color));
    }

    public void onConfigurationChanged(@NonNull Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateLayoutTablet();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        Log.m26i(TAG, "onResume");
        if (DesktopModeProvider.isDesktopMode()) {
            this.mStereoLayout.setEnabled(false);
            this.mSwitchStereo.setEnabled(false);
        }
        boolean booleanSettings = Settings.getBooleanSettings(Settings.KEY_REC_STEREO, false);
        if (booleanSettings) {
            this.mSwitchStereo.setChecked(true);
            this.mStereoText.setText(this.mResource.getString(C0690R.string.f93on));
        } else {
            this.mSwitchStereo.setChecked(false);
            this.mStereoText.setText(this.mResource.getString(C0690R.string.off));
        }
        updateStereoLayout(booleanSettings);
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        Log.m26i(TAG, "onPause");
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        VoiceNoteObservable.getInstance().deleteObserver(this);
        if (this.mSwitchStereo != null) {
            this.mSwitchStereo = null;
        }
        super.onDestroy();
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            SALogProvider.insertSALog(this.mResource.getString(C0690R.string.screen_stereo), this.mResource.getString(C0690R.string.event_stereo_back));
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void update(Observable observable, Object obj) {
        Log.m26i(TAG, "update - data : " + ((Integer) obj).intValue());
    }

    private void updateLayoutTablet() {
        if (VoiceNoteFeature.FLAG_IS_TABLET) {
            double d = 2.5d;
            if (DisplayManager.isCurrentWindowOnLandscape(this)) {
                d = 6.25d;
            }
            int currentScreenWidth = (int) ((d / 100.0d) * ((double) DisplayManager.getCurrentScreenWidth(this)));
            LinearLayout linearLayout = this.mStereoMainLayout;
            if (linearLayout != null) {
                linearLayout.setPadding(currentScreenWidth, linearLayout.getPaddingTop(), currentScreenWidth, this.mStereoMainLayout.getPaddingBottom());
            }
        }
    }
}
