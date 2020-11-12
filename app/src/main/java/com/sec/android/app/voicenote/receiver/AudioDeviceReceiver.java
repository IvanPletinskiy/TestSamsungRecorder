package com.sec.android.app.voicenote.receiver;

import android.content.Context;
import android.content.Intent;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.SimpleEngine;
import com.sec.android.app.voicenote.service.SimpleEngineManager;
import com.sec.android.app.voicenote.service.VoiceNoteService;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

public class AudioDeviceReceiver {
    private static final String TAG = "AudioDeviceReceiver";
    private Context mAppContext;
    private AudioDeviceCallback mAudioDeviceCallback = null;
    /* access modifiers changed from: private */
    public AudioManager mAudioManager = null;
    /* access modifiers changed from: private */
    public AudioDeviceInfo mBluetoothDeviceInfo = null;
    private Handler mDelayHandler = new DelayHandler();

    public AudioDeviceReceiver(Context context) {
        this.mAppContext = context;
        Context context2 = this.mAppContext;
        if (context2 != null) {
            this.mAudioManager = (AudioManager) context2.getSystemService("audio");
        }
    }

    public void registerListener() {
        registerAudioDeviceCallback(true);
    }

    public void unregisterListener() {
        registerAudioDeviceCallback(false);
    }

    /* access modifiers changed from: private */
    public void audioDeviceCallbackFunction(boolean z, int i) {
        VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.INVALIDATE_MENU));
        int scene = VoiceNoteApplication.getScene();
        int intSettings = Settings.getIntSettings("record_mode", 1);
        int recorderState = Engine.getInstance().getRecorderState();
        Engine.getInstance().setWiredHeadSetConnected(z);
        SimpleEngineManager.getInstance().setWiredHeadSetConnected(z);
        SimpleEngine activeEngine = SimpleEngineManager.getInstance().getActiveEngine();
        Engine.getInstance().setExternalMicAlert();
        if (activeEngine != null && activeEngine.getRecorderState() == 2) {
            activeEngine.pauseRecord();
        }
        if (scene == 4 || scene == 6) {
            intSettings = Settings.getIntSettings(Settings.KEY_PLAY_MODE, 1);
        }
        Log.m26i(TAG, "audioDeviceCallbackFunction : deviceConnected = " + z + " type = " + i + " scene = " + scene);
        if (z && needExternalMicAlert(false, intSettings)) {
            String string = this.mAppContext.getString(C0690R.string.external_mic);
            Context context = this.mAppContext;
            Toast.makeText(context, context.getString(C0690R.string.headset_connect_alert, new Object[]{string}), 1).show();
        }
        if (scene == 8 || scene == 1 || scene == 11 || scene == 6 || Engine.getInstance().isSimpleRecorderMode()) {
            if (recorderState == 2) {
                if (Engine.getInstance().isSimpleRecorderMode() || VoiceNoteService.Helper.connectionCount() > 0) {
                    pauseRecordDelay();
                } else {
                    this.mAppContext.sendBroadcast(new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_REC_PAUSE));
                }
            }
            if ((intSettings == 2 || intSettings == 4) && scene != 6) {
                if (z) {
                    this.mAppContext.sendBroadcast(new Intent(VoiceNoteService.VOICENOTE_SHOW_MODE_NOT_SUPPORTED));
                } else {
                    this.mAppContext.sendBroadcast(new Intent(VoiceNoteService.VOICENOTE_HIDE_MODE_NOT_SUPPORTED));
                }
            }
        }
        if (scene == 4 || scene == 3) {
            if (!z || (!Engine.getInstance().isWiredHeadSetConnected() && !Engine.getInstance().isBluetoothHeadSetConnected())) {
                Engine.getInstance().setMonoMode(false);
            } else if (intSettings == 2) {
                Engine.getInstance().setMonoMode(true);
            }
            if (Engine.getInstance().isPlayWithReceiver()) {
                if (Engine.getInstance().getPlayerState() == 3) {
                    Log.m26i(TAG, "deviceConnect - isPlayWithReceiver: " + Engine.getInstance().isPlayWithReceiver());
                    Engine.getInstance().pausePlay();
                    if (Engine.getInstance().getPlayerState() == 4) {
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                Engine.getInstance().resumePlay();
                                VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(VoiceNoteApplication.convertEvent(Event.PLAY_RESUME)));
                            }
                        }, 200);
                    }
                }
                Engine.getInstance().releasePreferredDevice();
            }
        }
    }

    private void registerAudioDeviceCallback(boolean z) {
        if (z) {
            if (this.mAudioDeviceCallback != null) {
                Log.m26i(TAG, "mAudioDeviceCallback is null");
                return;
            }
            this.mAudioDeviceCallback = new AudioDeviceCallback() {
                public void onAudioDevicesAdded(AudioDeviceInfo[] audioDeviceInfoArr) {
                    Log.m19d(AudioDeviceReceiver.TAG, "onAudioDevicesAdded");
                    boolean z = false;
                    for (AudioDeviceInfo audioDeviceInfo : AudioDeviceReceiver.this.mAudioManager.getDevices(1)) {
                        if (((audioDeviceInfo.getType() == 3 && audioDeviceInfo.isSource()) || audioDeviceInfo.getType() == 11 || audioDeviceInfo.getType() == 12 || audioDeviceInfo.getType() == 22) && !Engine.getInstance().isWiredHeadSetConnected()) {
                            Log.m26i(AudioDeviceReceiver.TAG, "onAudioDevicesAdded - attach WiredHeadSet - type " + audioDeviceInfo.getType());
                            AudioDeviceReceiver.this.audioDeviceCallbackFunction(true, audioDeviceInfo.getType());
                        }
                        if (audioDeviceInfo.getType() == 7) {
                            Log.m26i(AudioDeviceReceiver.TAG, "onAudioDevicesAdded - attach BT SCO - address:" + audioDeviceInfo.getAddress());
                            AudioDeviceInfo unused = AudioDeviceReceiver.this.mBluetoothDeviceInfo = audioDeviceInfo;
                            z = true;
                        }
                        if ((z || audioDeviceInfo.getType() == 8) && !Engine.getInstance().isBluetoothHeadSetConnected()) {
                            Log.m26i(AudioDeviceReceiver.TAG, "onAudioDevicesAdded - attach BluetoothHeadSet - type " + audioDeviceInfo.getType());
                            Engine.getInstance().setBluetoothHeadSetConnected(true);
                        }
                    }
                    if (z && !Engine.getInstance().isBluetoothSCOConnected()) {
                        AudioDeviceReceiver.this.audioDeviceCallbackForSCOConnect(true);
                    }
                }

                public void onAudioDevicesRemoved(AudioDeviceInfo[] audioDeviceInfoArr) {
                    Log.m19d(AudioDeviceReceiver.TAG, "onAudioDevicesRemoved");
                    boolean z = false;
                    boolean z2 = false;
                    for (AudioDeviceInfo audioDeviceInfo : AudioDeviceReceiver.this.mAudioManager.getDevices(1)) {
                        if (audioDeviceInfo.getType() == 3 || audioDeviceInfo.getType() == 11 || audioDeviceInfo.getType() == 12 || audioDeviceInfo.getType() == 22) {
                            if (audioDeviceInfo.getType() == 3) {
                                Log.m19d(AudioDeviceReceiver.TAG, "set default value mPauseByCall = false when remove earphone");
                                Engine.getInstance().setPausedByCall(false);
                            }
                            z = true;
                        }
                        if (audioDeviceInfo.getType() == 8) {
                            z2 = true;
                        }
                    }
                    if (!z) {
                        Log.m26i(AudioDeviceReceiver.TAG, "onAudioDevicesRemoved : WiredHeadSet removed");
                        AudioDeviceReceiver.this.audioDeviceCallbackFunction(false, 0);
                    }
                    if (!z2) {
                        Log.m26i(AudioDeviceReceiver.TAG, "onAudioDevicesRemoved : BluetoothHeadSet removed");
                        Engine.getInstance().setBluetoothHeadSetConnected(false);
                    }
                    Log.m26i(AudioDeviceReceiver.TAG, "onAudioDevicesRemoved : BluetoothSCO removed");
                    AudioDeviceInfo unused = AudioDeviceReceiver.this.mBluetoothDeviceInfo = null;
                    AudioDeviceReceiver.this.audioDeviceCallbackForSCOConnect(false);
                }
            };
            this.mAudioManager.registerAudioDeviceCallback(this.mAudioDeviceCallback, (Handler) null);
        } else if (this.mAudioDeviceCallback != null) {
            Log.m26i(TAG, "unregisterAudioDeviceCallback");
            this.mAudioManager.unregisterAudioDeviceCallback(this.mAudioDeviceCallback);
            this.mAudioDeviceCallback = null;
        }
    }

    private boolean needExternalMicAlert(boolean z, int i) {
        Log.m26i(TAG, "needExternalMicAlert - is bt: " + z);
        int scene = VoiceNoteApplication.getScene();
        int recorderState = Engine.getInstance().getRecorderState();
        int playerState = Engine.getInstance().getPlayerState();
        if (scene == 12) {
            return false;
        }
        boolean z2 = true;
        if (!Engine.getInstance().isSimpleRecorderMode() ? i == 2 || i == 4 || scene == 4 || (recorderState == 1 && VoiceNoteService.Helper.connectionCount() <= 0) : !(recorderState == 1 && playerState == 1)) {
            z2 = false;
        }
        if (!z || (!Engine.getInstance().isWiredHeadSetConnected() && !Engine.getInstance().isRecordForStereoOn())) {
            return z2;
        }
        Log.m19d(TAG, "needExternalMicAlert - ignore!!!");
        return false;
    }

    private void pauseRecordDelay() {
        this.mDelayHandler.sendEmptyMessageDelayed(0, (long) (1000 > Engine.getInstance().getCurrentTime() ? 1000 - Engine.getInstance().getCurrentTime() : 0));
    }

    private class DelayHandler extends Handler {
        private DelayHandler() {
        }

        public void handleMessage(Message message) {
            if (message.what == 0) {
                if (Engine.getInstance().isSaveEnable()) {
                    AudioDeviceReceiver.this.pauseRecord();
                } else {
                    sendEmptyMessageDelayed(0, (long) (1000 - Engine.getInstance().getCurrentTime()));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void pauseRecord() {
        Log.m26i(TAG, "pauseRecord");
        if (!Engine.getInstance().pauseRecord()) {
            return;
        }
        if (VoiceNoteApplication.getScene() == 6) {
            VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.EDIT_RECORD_PAUSE));
        } else {
            VoiceNoteObservable.getInstance().notifyObservers(1002);
        }
    }

    /* access modifiers changed from: private */
    public void audioDeviceCallbackForSCOConnect(boolean z) {
        Log.m26i(TAG, "audioDeviceCallbackForSCOConnect - " + z);
        VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.INVALIDATE_MENU));
        Log.m26i(TAG, "audioDeviceCallbackForSCOConnect - this feature is turned off!!!");
    }
}
