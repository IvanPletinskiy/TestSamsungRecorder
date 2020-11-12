package com.sec.android.app.voicenote.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.provider.DBProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.NFCProvider;
import com.sec.android.app.voicenote.provider.PermissionProvider;
import com.sec.android.app.voicenote.provider.PhoneStateProvider;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.util.Observable;
import java.util.Observer;

public class NFCPlaySoundActivity extends AppCompatActivity implements Observer {
    private static final String TAG = "NFCPlaySoundActivity";

    private void playSound(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            Log.m19d(TAG, "playSound but intent or action is null");
            return;
        }
        Log.m19d(TAG, "playSound");
        long hasValidNFCInfo = NFCProvider.hasValidNFCInfo(this, intent);
        if (hasValidNFCInfo > 0) {
            AudioManager audioManager = (AudioManager) getSystemService("audio");
//            if (audioManager.semIsRecordActive(5) || audioManager.semIsRecordActive(1) || audioManager.semIsRecordActive(MediaRecorder.semGetInputSource(9))) {
//                Toast.makeText(this, C0690R.string.unable_to_play, 0).show();
//            } else if (!PhoneStateProvider.getInstance().isCallIdle(this)) {
//                Toast.makeText(this, C0690R.string.no_play_during_call, 0).show();
//            } else if (Engine.getInstance().getRecorderState() != 1) {
//                Toast.makeText(this, C0690R.string.unable_to_play, 0).show();
//            } else {
//                Intent intent2 = new Intent();
//                intent2.setAction("android.intent.action.VIEW");
//                intent2.setData(DBProvider.getInstance().getContentURI(hasValidNFCInfo));
//                intent2.setFlags(1);
//                try {
//                    SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_voice_label), getResources().getString(C0690R.string.event_play_voice_label));
//                    startActivity(intent2);
//                } catch (ActivityNotFoundException e) {
//                    Log.m24e(TAG, "No Activity found to play NFC tagged file", (Throwable) e);
//                    Toast.makeText(this, C0690R.string.unable_to_play, 0).show();
//                }
//            }
        } else if (hasValidNFCInfo == -2) {
            Toast.makeText(this, C0690R.string.voice_label_error_msg1, 0).show();
        } else if (hasValidNFCInfo == -1) {
            Toast.makeText(this, C0690R.string.file_does_not_exist, 0).show();
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.m19d(TAG, "onCreate");
        setContentView((int) C0690R.layout.activity_nfc_play_sound);
        if (getResources().getConfiguration().orientation == 2 && DisplayManager.getMultiwindowMode() != 2) {
            getWindow().setFlags(1024, 1024);
        }
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        Log.m26i(TAG, "onStart");
        VoiceNoteObservable.getInstance().addObserver(this);
        if (PermissionProvider.checkPhonePermission(this, 5, C0690R.string.voice_label, true)) {
            playSound(getIntent());
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        Log.m26i(TAG, "onResume");
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        Log.m26i(TAG, "onStop");
        VoiceNoteObservable.getInstance().deleteObserver(this);
        super.onStop();
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        Log.m26i(TAG, "onNewIntent");
        super.onNewIntent(intent);
        if (PermissionProvider.checkPhonePermission(this, 5, C0690R.string.voice_label, false)) {
            playSound(getIntent());
            finish();
        }
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        Log.m26i(TAG, "onRequestPermissionsResult - requestCode : " + i);
        if (strArr.length == 0 || iArr.length == 0) {
            Log.m26i(TAG, "onRequestPermissionsResult - permissions or grantResults size is zero");
            return;
        }
        if (i == 5) {
            Settings.setSettings(Settings.KEY_FORCE_SYSTEM_PERMISSION_DIALOG_PHONE, false);
            if (iArr[0] == 0) {
                playSound(getIntent());
            }
        } else {
            super.onRequestPermissionsResult(i, strArr, iArr);
        }
        finish();
    }

    public void update(Observable observable, Object obj) {
        int intValue = ((Integer) obj).intValue();
        Log.m26i(TAG, "update event : " + intValue + " name : " + Event.getEventName(intValue));
        if (intValue == 998) {
            DialogFactory.clearTopDialog(getSupportFragmentManager());
        }
    }
}
