package com.sec.android.app.voicenote.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Network;
import com.sec.android.app.voicenote.provider.SecureFolderProvider;
import com.sec.android.app.voicenote.provider.StorageProvider;
import com.sec.android.app.voicenote.provider.UPSMProvider;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.Recorder;
import com.sec.android.app.voicenote.service.SimpleEngine;
import com.sec.android.app.voicenote.service.SimpleEngineManager;
import com.sec.android.app.voicenote.service.VoiceNoteService;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

public class VoiceNoteIntentReceiver {
    private static final String ACTION_KEYGUARD_STATE_UPDATE = "com.samsung.keyguard.KEYGUARD_STATE_UPDATE";
    private static final String ACTION_RINGER_MODE_CHANGED = "android.media.RINGER_MODE_CHANGED";
    private static final String ACTION_VOLUME_STATE_CHANGED = "android.os.storage.action.VOLUME_STATE_CHANGED";
    private static final String TAG = "VoiceNoteIntentReceiver";
    public static final String TAG_PATH = "tag_path";
    private static final int VOLUME_STATE_CHECKING = 1;
    private static final int VOLUME_STATE_FORMATTING = 4;
    private static final int VOLUME_STATE_MEDIA_BAD_REMOVAL = 8;
    private static final int VOLUME_STATE_MEDIA_EJECTING = 5;
    private static final int VOLUME_STATE_MEDIA_MOUNTED = 2;
    private static final int VOLUME_STATE_MEDIA_UNMOUNTED = 0;
    private static final int VOLUME_STATE_MOUNTED_READ_ONLY = 3;
    private static final int VOLUME_STATE_REMOVED = 7;
    private static final int VOLUME_STATE_UNMOUNTABLE = 6;
    private Context mAppContext;
    private BroadcastReceiver mBroadcastLowStorage = null;
    private BroadcastReceiver mBroadcastNetwork = null;
    private BroadcastReceiver mBroadcastReceiverBattery = null;
    private BroadcastReceiver mBroadcastReceiverEmergencyState = null;
    private BroadcastReceiver mBroadcastReceiverForPlayer = null;
    private BroadcastReceiver mBroadcastReceiverScreenOnOff = null;
    private BroadcastReceiver mBroadcastSDCard = null;
    private BroadcastReceiver mBroadcastShutDown = null;
    private BroadcastReceiver mBroadcastSoundMode = null;
    /* access modifiers changed from: private */
//    public LocalBroadcastManager mLocalBroadcastManager;

    public VoiceNoteIntentReceiver(Context context) {
        this.mAppContext = context;
    }

    public void registerListener() {
        Context context = this.mAppContext;
//        if (context != null && this.mLocalBroadcastManager == null) {
//            this.mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
//        }
        registerBroadcastReceiverForPlayer(true);
        registerBroadcastReceiverScreenOnOff(true);
        registerBroadcastReceiverEmergencyStateChanged(true);
        registerBroadcastReceiverLowBattery(true);
        registerBroadcastReceiverSDCard(true);
        registerBroadcastReceiverShutDown(true);
        registerBroadcastReceiverNetwork(true);
        registerBroadcastReceiverLowStorage(true);
        registerBroadcastReceiverSoundMode(true);
    }

    public void registerListenerForSetting() {
        Context context = this.mAppContext;
//        if (context != null && this.mLocalBroadcastManager == null) {
//            this.mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
//        }
        registerBroadcastReceiverSDCard(true);
    }

    public void unregisterListener() {
        registerBroadcastReceiverForPlayer(false);
        registerBroadcastReceiverScreenOnOff(false);
        registerBroadcastReceiverEmergencyStateChanged(false);
        registerBroadcastReceiverLowBattery(false);
        registerBroadcastReceiverSDCard(false);
        registerBroadcastReceiverShutDown(false);
        registerBroadcastReceiverNetwork(false);
        registerBroadcastReceiverLowStorage(false);
        registerBroadcastReceiverSoundMode(false);
    }

    public void unregisterListenerForSetting() {
        registerBroadcastReceiverSDCard(false);
    }

    private void registerBroadcastReceiverForPlayer(boolean z) {
        if (!z) {
            BroadcastReceiver broadcastReceiver = this.mBroadcastReceiverForPlayer;
            if (broadcastReceiver != null) {
                this.mAppContext.unregisterReceiver(broadcastReceiver);
                this.mBroadcastReceiverForPlayer = null;
            }
        } else if (this.mBroadcastReceiverForPlayer == null) {
            this.mBroadcastReceiverForPlayer = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    if ("android.media.AUDIO_BECOMING_NOISY".equals(intent.getAction())) {
                        Log.m29v(VoiceNoteIntentReceiver.TAG, "AudioManager.ACTION_AUDIO_BECOMING_NOISY");
                        if (Engine.getInstance().getPlayerState() == 3) {
                            Engine.getInstance().pausePlay();
//                            if (VoiceNoteIntentReceiver.this.mLocalBroadcastManager != null) {
//                                VoiceNoteIntentReceiver.this.mLocalBroadcastManager.sendBroadcast(new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_PLAY_PAUSE));
//                            }
                        }
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.media.AUDIO_BECOMING_NOISY");
            this.mAppContext.registerReceiver(this.mBroadcastReceiverForPlayer, intentFilter);
        }
    }

    private void registerBroadcastReceiverScreenOnOff(boolean z) {
        if (!z) {
            BroadcastReceiver broadcastReceiver = this.mBroadcastReceiverScreenOnOff;
            if (broadcastReceiver != null) {
                this.mAppContext.unregisterReceiver(broadcastReceiver);
                this.mBroadcastReceiverScreenOnOff = null;
            }
        } else if (this.mBroadcastReceiverScreenOnOff == null) {
            this.mBroadcastReceiverScreenOnOff = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    Log.m29v(VoiceNoteIntentReceiver.TAG, intent.getAction());
//                    if (VoiceNoteIntentReceiver.this.mLocalBroadcastManager == null) {
//                        return;
//                    }
//                    if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
//                        VoiceNoteIntentReceiver.this.mLocalBroadcastManager.sendBroadcast(new Intent(VoiceNoteService.VOICENOTE_SCREEN_OFF));
//                    } else if (intent.getAction().equals("android.intent.action.SCREEN_ON")) {
//                        VoiceNoteIntentReceiver.this.mLocalBroadcastManager.sendBroadcast(new Intent(VoiceNoteService.VOICENOTE_SCREEN_ON));
//                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            intentFilter.addAction("android.intent.action.SCREEN_ON");
            this.mAppContext.registerReceiver(this.mBroadcastReceiverScreenOnOff, intentFilter);
        }
    }

    private void registerBroadcastReceiverEmergencyStateChanged(boolean z) {
        if (!z) {
            BroadcastReceiver broadcastReceiver = this.mBroadcastReceiverEmergencyState;
            if (broadcastReceiver != null) {
                this.mAppContext.unregisterReceiver(broadcastReceiver);
                this.mBroadcastReceiverEmergencyState = null;
            }
        } else if (this.mBroadcastReceiverEmergencyState == null) {
            this.mBroadcastReceiverEmergencyState = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    Log.m29v(VoiceNoteIntentReceiver.TAG, intent.getAction());
//                    if (VoiceNoteIntentReceiver.this.mLocalBroadcastManager != null && intent.getIntExtra("reason", 0) > 0) {
//                        if (VoiceNoteApplication.getScene() == 12) {
//                            VoiceNoteIntentReceiver.this.mLocalBroadcastManager.sendBroadcast(new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_TRANSLATION_SAVE));
//                            return;
//                        }
//                        VoiceNoteIntentReceiver.this.mLocalBroadcastManager.sendBroadcast(new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_SAVE));
//                        VoiceNoteIntentReceiver.this.mLocalBroadcastManager.sendBroadcast(new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_PLAY_STOP));
//                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(UPSMProvider.EMERGENCY_STATE_CHANGED);
            this.mAppContext.registerReceiver(this.mBroadcastReceiverEmergencyState, intentFilter);
        }
    }

    /* access modifiers changed from: private */
    public boolean isLowBattery(Intent intent) {
        if (!intent.getAction().equals("android.intent.action.BATTERY_CHANGED")) {
            return false;
        }
        int intExtra = intent.getIntExtra("status", 1);
        int intExtra2 = intent.getIntExtra("scale", 100);
        int intExtra3 = intent.getIntExtra("level", intExtra2);
        if (intExtra2 == 0) {
            return true;
        }
        if ((((float) intExtra3) * 100.0f) / ((float) intExtra2) > 1.0f || intExtra == 2) {
            return false;
        }
        Log.m19d(TAG, "Battery Level = " + intExtra3 + '/' + intExtra2);
        StringBuilder sb = new StringBuilder();
        sb.append("Battery Status = ");
        sb.append(intExtra);
        Log.m19d(TAG, sb.toString());
        return true;
    }

    private void registerBroadcastReceiverLowBattery(boolean z) {
        if (!z) {
            BroadcastReceiver broadcastReceiver = this.mBroadcastReceiverBattery;
            if (broadcastReceiver != null) {
                this.mAppContext.unregisterReceiver(broadcastReceiver);
                this.mBroadcastReceiverBattery = null;
            }
        } else if (this.mBroadcastReceiverBattery == null) {
            this.mBroadcastReceiverBattery = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    if (VoiceNoteIntentReceiver.this.isLowBattery(intent)) {
                        Toast.makeText(context, C0690R.string.low_battery_msg, 0).show();
//                        if (VoiceNoteIntentReceiver.this.mLocalBroadcastManager != null) {
//                            VoiceNoteIntentReceiver.this.mLocalBroadcastManager.sendBroadcast(new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_SAVE));
//                        }
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
            this.mAppContext.registerReceiver(this.mBroadcastReceiverBattery, intentFilter);
        }
    }

    private void registerBroadcastReceiverSDCard(boolean z) {
        if (!z) {
            BroadcastReceiver broadcastReceiver = this.mBroadcastSDCard;
            if (broadcastReceiver != null) {
                this.mAppContext.unregisterReceiver(broadcastReceiver);
                this.mBroadcastSDCard = null;
            }
        } else if (this.mBroadcastSDCard == null) {
            this.mBroadcastSDCard = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    Log.m26i(VoiceNoteIntentReceiver.TAG, "BroadcastReceiverSDCard onReceive = " + intent.getAction());
                    if (SecureFolderProvider.isSecureFolderSupported()) {
                        SecureFolderProvider.getKnoxMenuList(context);
                        if (SecureFolderProvider.isInsideSecureFolder()) {
                            return;
                        }
                    }
                    if (!StorageProvider.isPersonalDirectory(intent.getData())) {
                        VoiceNoteIntentReceiver.this.handleSDCardReceiver(intent);
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_VOLUME_STATE_CHANGED);
            this.mAppContext.registerReceiver(this.mBroadcastSDCard, intentFilter);
        }
    }

    private void registerBroadcastReceiverSoundMode(boolean z) {
        if (!z) {
            BroadcastReceiver broadcastReceiver = this.mBroadcastSoundMode;
            if (broadcastReceiver != null) {
                this.mAppContext.unregisterReceiver(broadcastReceiver);
                this.mBroadcastSoundMode = null;
            }
        } else if (this.mBroadcastSoundMode == null) {
            this.mBroadcastSoundMode = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    Log.m26i(VoiceNoteIntentReceiver.TAG, "registerBroadcastReceiverSoundMode onReceive = " + intent.getAction());
                    Recorder instance = Recorder.getInstance();
                    if (instance == null || instance.getRecorderState() != 2) {
                        SimpleEngine activeEngine = SimpleEngineManager.getInstance().getActiveEngine();
                        if (activeEngine != null) {
                            activeEngine.disableSystemSound();
                            return;
                        }
                        return;
                    }
                    instance.disableSystemSound();
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_RINGER_MODE_CHANGED);
            this.mAppContext.registerReceiver(this.mBroadcastSoundMode, intentFilter);
        }
    }

    private void registerBroadcastReceiverShutDown(boolean z) {
        if (!z) {
            BroadcastReceiver broadcastReceiver = this.mBroadcastShutDown;
            if (broadcastReceiver != null) {
                this.mAppContext.unregisterReceiver(broadcastReceiver);
                this.mBroadcastShutDown = null;
            }
        } else if (this.mBroadcastShutDown == null) {
            this.mBroadcastShutDown = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    Log.m26i(VoiceNoteIntentReceiver.TAG, "registerBroadcastReceiverShutDown Receive = " + intent.getAction());
//                    if (VoiceNoteIntentReceiver.this.mLocalBroadcastManager != null) {
//                        VoiceNoteIntentReceiver.this.mLocalBroadcastManager.sendBroadcast(new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_SAVE));
//                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.ACTION_SHUTDOWN");
            this.mAppContext.registerReceiver(this.mBroadcastShutDown, intentFilter);
        }
    }

    private void registerBroadcastReceiverNetwork(boolean z) {
        if (!z) {
            BroadcastReceiver broadcastReceiver = this.mBroadcastNetwork;
            if (broadcastReceiver != null) {
                this.mAppContext.unregisterReceiver(broadcastReceiver);
                this.mBroadcastNetwork = null;
            }
        } else if (this.mBroadcastNetwork == null) {
            this.mBroadcastNetwork = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
//                    if (VoiceNoteIntentReceiver.this.mLocalBroadcastManager != null && VoiceNoteApplication.getScene() == 12) {
//                        if (Engine.getInstance().getTranslationState() != 3 && Engine.getInstance().getTranslationState() != 2) {
//                            return;
//                        }
//                        if (Network.isNetworkConnected(context)) {
//                            VoiceNoteIntentReceiver.this.mLocalBroadcastManager.sendBroadcast(new Intent(VoiceNoteService.VOICENOTE_NETWORK_ON));
//                        } else if (!Network.isNetworkConnected(context)) {
//                            VoiceNoteIntentReceiver.this.mLocalBroadcastManager.sendBroadcast(new Intent(VoiceNoteService.VOICENOTE_NETWORK_OFF));
//                        }
//                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            this.mAppContext.registerReceiver(this.mBroadcastNetwork, intentFilter);
        }
    }

    private void registerBroadcastReceiverLowStorage(boolean z) {
        if (!z) {
            BroadcastReceiver broadcastReceiver = this.mBroadcastLowStorage;
            if (broadcastReceiver != null) {
                this.mAppContext.unregisterReceiver(broadcastReceiver);
                this.mBroadcastLowStorage = null;
            }
        } else if (this.mBroadcastLowStorage == null) {
            this.mBroadcastLowStorage = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    Log.m26i(VoiceNoteIntentReceiver.TAG, "ACTION_DEVICE_STORAGE_LOW : state = " + Engine.getInstance().getRecorderState());
//                    if (VoiceNoteIntentReceiver.this.mLocalBroadcastManager != null && Engine.getInstance().getRecorderState() == 2) {
//                        VoiceNoteIntentReceiver.this.mLocalBroadcastManager.sendBroadcast(new Intent(VoiceNoteService.VOICENOTE_DEVICE_STORAGE_LOW));
//                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.DEVICE_STORAGE_LOW");
            this.mAppContext.registerReceiver(this.mBroadcastLowStorage, intentFilter);
        }
    }

    /* access modifiers changed from: private */
    public void handleSDCardReceiver(Intent intent) {
        String action = intent.getAction();
//        if (this.mLocalBroadcastManager != null && action != null && action.equals(ACTION_VOLUME_STATE_CHANGED)) {
//            String sDCardWritableDirPath = StorageProvider.getSDCardWritableDirPath();
//            int intExtra = intent.getIntExtra("android.os.storage.extra.VOLUME_STATE", -1);
//            Log.m26i(TAG, "SDCard VOLUME_STATE state = " + intExtra);
//            if (intExtra != 0) {
//                if (intExtra != 2) {
//                    if (!(intExtra == 5 || intExtra == 8)) {
//                        return;
//                    }
//                } else if (StorageProvider.isSdCardWriteRestricted(this.mAppContext)) {
//                    Log.m19d(TAG, "handleSDCardReceiver SDcard is writeRestricted.");
//                    return;
//                } else {
//                    this.mLocalBroadcastManager.sendBroadcast(new Intent(VoiceNoteService.VOICENOTE_SD_MOUNT));
//                    VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.MOUNT_SD_CARD));
//                    return;
//                }
//            }
//            this.mLocalBroadcastManager.sendBroadcast(new Intent("com.sec.android.app.voicenote.sd_unmount").putExtra(TAG_PATH, sDCardWritableDirPath));
//            VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.UNMOUNT_SD_CARD));
//        }
    }
}
