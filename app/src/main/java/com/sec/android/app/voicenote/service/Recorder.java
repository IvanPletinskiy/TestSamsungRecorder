package com.sec.android.app.voicenote.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DeviceInfo;
import com.sec.android.app.voicenote.p007ui.actionbar.RunOptionMenu;
import com.sec.android.app.voicenote.provider.CallRejectChecker;
import com.sec.android.app.voicenote.provider.DesktopModeProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.SmartTipsProvider;
import com.sec.android.app.voicenote.provider.StatusBarProvider;
import com.sec.android.app.voicenote.provider.StorageProvider;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.service.helper.BluetoothHelper;
import com.sec.android.app.voicenote.service.recognizer.VoiceWorkerForP;
import com.sec.android.app.voicenote.uicore.MediaSessionManager;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class Recorder {
    private static final int AUDIO_FOCUS_RETRY_INTERVAL = 50;
    public static final int CHECK_AVAIABLE_STORAGE = 2000;
    public static final int DEVICE_STORAGE_LOW = 524288000;
    public static final int DONT_NEED_STOP_FILE = -1;
    private static final String FILE_SUFFIX = "_encoded";
    private static final int INCOMING_CALL = 1;
    public static final int INFO_AUDIOFOCUS_LOSS = 1020;
    public static final int INFO_CALL_ACCEPT = 1024;
    public static final int INFO_DURATION_PROGRESS = 1011;
    public static final int INFO_MAX_DURATION_REACHED = 1021;
    public static final int INFO_MAX_FILESIZE_REACHED = 1022;
    public static final int INFO_NOT_ENOUGH_MEMORY = 1023;
    static final int INFO_NO_SOUND_DETECT = 1025;
    static final int INFO_NO_SOUND_DETECT_VIBRATE = 1026;
    public static final int INFO_RECORDER_STATE = 1010;
    private static final int MAX_AUDIO_FOCUS_RETRY_CNT = 10;
    private static final long MAX_NO_SOUND_TIME = 15000;
    public static final int NEED_STOP_FILE = 1;
    private static final int NONE = -1;
    private static final int NO_CALL = 0;
    private static final int OUTGOING_CALL = 2;
    public static final int RECORD_DURATION_INTERVAL = 35;
    public static final int RECORD_MINIMUM_DURATION = 1000;
    public static final int RECORD_SOURCE_ALL = -1;
    private static final String SS_VR_OFF = "g_record_sec_voice_recorder_enable=false";
    private static final String SS_VR_ON = "g_record_sec_voice_recorder_enable=true";
    private static final String TAG = "Recorder";
    private static Recorder mInstance;
    /* access modifiers changed from: private */
    public Context mAppContext = null;
    private final AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {
        public String toString() {
            return super.toString() + Binder.getCallingUid();
        }

        public void onAudioFocusChange(int i) {
            Log.m26i(Recorder.TAG, "onAudioFocusChange - focusChange : " + i);
            if (i == -3) {
                return;
            }
            if (i != -2) {
                if (i != -1) {
                    if (i == 1) {
                        Log.m26i(Recorder.TAG, "mPhoneStateListener - isAutoResumeRecording : " + Recorder.this.isAutoResumeRecording());
                        if (Recorder.this.isAutoResumeRecording() && !Recorder.this.mIsResumeRecordByCall) {
                            Recorder.this.setAutoResumeRecording(false);
                            Toast.makeText(Recorder.this.mAppContext, C0690R.string.recording_resume, 0).show();
                            if (Recorder.this.mAppContext != null) {
//                                LocalBroadcastManager.getInstance(Recorder.this.mAppContext).sendBroadcast(new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_REC_RESUME));
                            }
                        }
                        boolean unused = Recorder.this.mIsCallStateOffHook = false;
                        boolean unused2 = Recorder.this.needAbandonAudioFocus = true;
                    }
                } else if (Recorder.this.mRecorderState != 2 && Recorder.this.mRecorderState != 3) {
                } else {
                    if (Recorder.this.isSaveEnable()) {
                        Recorder.this.notifyObservers(1020, -1, -1);
                        return;
                    }
                    Log.m19d(Recorder.TAG, "AUDIOFOCUS_LOSS - cancel record");
                    new Handler().postDelayed(new Runnable() {
                        public final void run() {
//                            Recorder.C07631.this.lambda$onAudioFocusChange$0$Recorder$1();
                        }
                    }, (long) ((1000 - Recorder.this.mRecordEndTime) + Recorder.this.mRecordStartTime));
                }
            } else if (Recorder.this.mRecorderState == 2 || Recorder.this.mRecorderState == 3) {
                TelephonyManager telephonyManager = (TelephonyManager) Recorder.this.mAppContext.getSystemService("phone");
                if (telephonyManager != null && telephonyManager.getCallState() == 1 && CallRejectChecker.getInstance().getReject()) {
                    Log.m26i(Recorder.TAG, "AUDIOFOCUS_LOSS_TRANSIENT : keep recording");
                } else if (Recorder.this.isSaveEnable()) {
                    if (!Recorder.this.mIsCallStateOffHook || Recorder.this.mCallType == 2) {
                        Recorder.this.notifyObservers(1020, -1, -1);
                        return;
                    }
                    boolean unused3 = Recorder.this.needAbandonAudioFocus = false;
                    int scene = VoiceNoteApplication.getScene();
                    if (VoiceNoteService.Helper.connectionCount() != 1) {
                        if (Recorder.this.mRecorderState == 2) {
                            Recorder.this.setAutoResumeRecording(true);
                            if (Recorder.this.mAppContext != null) {
//                                LocalBroadcastManager.getInstance(Recorder.this.mAppContext).sendBroadcastSync(new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_REC_PAUSE));
                            }
                            if (scene == 8) {
                                Toast.makeText(Recorder.this.mAppContext, C0690R.string.recording_pause, 0).show();
                                Engine.getInstance().startOverwrite(-1);
                            }
                        }
                        if (Recorder.this.mRecorderState == 3) {
                            Engine.getInstance().startOverwrite(-1);
                        }
                    } else if (Recorder.this.mRecorderState == 2) {
                        Engine.getInstance().pauseRecord();
                        if (scene == 8) {
                            Toast.makeText(Recorder.this.mAppContext, C0690R.string.recording_pause, 0).show();
                            Recorder.this.setAutoResumeRecording(true);
                            VoiceNoteObservable.getInstance().notifyObservers(1002);
                            Engine.getInstance().startOverwrite(-1);
                        } else if (scene == 6) {
                            Recorder.this.setAutoResumeRecording(false);
                            VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.EDIT_RECORD_PAUSE));
                        }
                    } else if (scene == 8 && Recorder.this.mRecorderState == 3) {
                        Engine.getInstance().startOverwrite(-1);
                    }
                } else if (Engine.getInstance().getContentItemCount() <= 1 || !Engine.getInstance().isSaveEnable()) {
                    Log.m19d(Recorder.TAG, "AUDIOFOCUS_LOSS_TRANSIENT - cancel record");
                    new Handler().postDelayed(new Runnable() {
                        public final void run() {
//                            Recorder.C07631.this.lambda$onAudioFocusChange$1$Recorder$1();
                        }
                    }, (long) ((1000 - Recorder.this.mRecordEndTime) + Recorder.this.mRecordStartTime));
                } else {
                    Recorder.this.notifyObservers(1020, 1004, -1);
                }
            }
        }

        public /* synthetic */ void lambda$onAudioFocusChange$0$Recorder$1() {
            Recorder.this.notifyObservers(1020, 1006, -1);
        }

        public /* synthetic */ void lambda$onAudioFocusChange$1$Recorder$1() {
            Recorder.this.notifyObservers(1020, 1006, -1);
        }
    };
    private AudioFocusRequest mAudioFocusRequest = null;
    private AudioFormat mAudioFormat;
    /* access modifiers changed from: private */
    public AudioManager mAudioManager = null;
    private boolean mAutoResumeRecording = false;
    /* access modifiers changed from: private */
    public int mCallType = 0;
    private int mCurrentTime = 0;
    /* access modifiers changed from: private */
    public boolean mIsCallStateOffHook = false;
    private boolean mIsRecordForStereoOn = false;
    /* access modifiers changed from: private */
    public boolean mIsRejectCall = false;
    /* access modifiers changed from: private */
    public boolean mIsResumeRecordByCall = false;
    /* access modifiers changed from: private */
    public int mLastPhoneState = 0;
    private final SpeechTime mLeftSpeechTime = new SpeechTime();
    private final ArrayList<WeakReference<OnRecorderListener>> mListeners = new ArrayList<>();
    private MediaRecorder mMediaRecorder = null;
    private Handler mMuteHandler = new VolumeHandler(this);
    private long mNoSoundCheckTime = 0;
    private int mNoSoundCount = 0;
    private boolean mOverwriteByDrag = false;
    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        public void onCallStateChanged(int i, String str) {
            Log.m26i(Recorder.TAG, "mPhoneStateListener : " + i + " mRecorderState : " + Recorder.this.mRecorderState);
            if (i == 0) {
                if (Recorder.this.mIsRejectCall) {
                    Log.m26i(Recorder.TAG, "reject call from user");
                    boolean unused = Recorder.this.mIsRejectCall = false;
                    SmartTipsProvider.getInstance().increaseValueOfKey(SmartTipsProvider.COUT_CANCEL_CALL_WHILE_RECORDING);
                }
                boolean unused2 = Recorder.this.mIsCallStateOffHook = false;
                int unused3 = Recorder.this.mCallType = 0;
            } else if (i == 1) {
                boolean unused4 = Recorder.this.mIsRejectCall = true;
                if (!CallRejectChecker.getInstance().getReject()) {
                    if (!Engine.getInstance().isBluetoothSCOConnected() || !Engine.getInstance().isRecordByBluetoothSCO()) {
                        boolean unused5 = Recorder.this.mIsResumeRecordByCall = false;
                    } else {
                        boolean unused6 = Recorder.this.mIsResumeRecordByCall = true;
                    }
                    boolean unused7 = Recorder.this.mIsCallStateOffHook = true;
                    int unused8 = Recorder.this.mCallType = 1;
                    Log.m26i(Recorder.TAG, "mPhoneStateListener : keep recording");
                } else {
                    return;
                }
            } else if (i == 2) {
                boolean unused9 = Recorder.this.mIsRejectCall = false;
                boolean unused10 = Recorder.this.mIsCallStateOffHook = true;
                if (Recorder.this.mLastPhoneState == 1) {
                    int unused11 = Recorder.this.mCallType = 1;
                } else {
                    int unused12 = Recorder.this.mCallType = 2;
                }
                if (!Engine.getInstance().isBluetoothSCOConnected() || !Engine.getInstance().isRecordByBluetoothSCO()) {
                    boolean unused13 = Recorder.this.mIsResumeRecordByCall = false;
                } else {
                    boolean unused14 = Recorder.this.mIsResumeRecordByCall = true;
                }
                Log.m26i(Recorder.TAG, "mPhoneStateListener - record state : " + Recorder.this.mRecorderState + " - scene : " + VoiceNoteApplication.getScene());
            }
            int unused15 = Recorder.this.mLastPhoneState = i;
        }
    };
    private int mRecordCorrectionTime = 0;
    /* access modifiers changed from: private */
    public int mRecordEndTime = 0;
    private int mRecordMode = 0;
    /* access modifiers changed from: private */
    public int mRecordStartTime = 0;
    /* access modifiers changed from: private */
    public int mRecorderState = 1;
    private final SpeechTime mRightSpeechTime = new SpeechTime();
    /* access modifiers changed from: private */
    public Handler mStorageHandler = new StorageHandler(this);
    private StorageThread mStorageThread = null;
    private TelephonyManager mTelephonyManager = null;
    private int mVibrateWhileRingingState;
    /* access modifiers changed from: private */
    public boolean needAbandonAudioFocus = true;

    interface OnRecorderListener {
        void onRecorderUpdate(int i, int i2, int i3);
    }

    public static class RecordMode {
        public static final int ATTACH = 5;
        public static final int EMPTY = 0;
        public static final int INTERVIEW = 2;
        public static final int LIMIT_FOR_MMS = 6;
        public static final int MEETING = 3;
        public static final int NORMAL = 1;
        public static final int VOICEMEMO = 4;
    }

    public static class RecorderState {
        public static final int IDLE = 1;
        public static final int PAUSED = 3;
        public static final int RECORDING = 2;
        public static final int STOPPED = 4;
    }

    public int getMaxDuration(int i) {
        if (i != 4) {
            return (i == 5 || i == 6) ? -1 : 36000999;
        }
        return 600000;
    }

    private Recorder() {
        Log.m19d(TAG, "Recorder creator !!");
    }

    public static Recorder getInstance() {
        if (mInstance == null) {
            synchronized (Recorder.class) {
                if (mInstance == null) {
                    mInstance = new Recorder();
                }
            }
        }
        return mInstance;
    }

    public void setApplicationContext(Context context) {
        this.mAppContext = context;
        Context context2 = this.mAppContext;
        if (context2 != null) {
            this.mAudioManager = (AudioManager) context2.getSystemService("audio");
            this.mVibrateWhileRingingState = Settings.System.getInt(this.mAppContext.getContentResolver(), "vibrate_when_ringing", 1);
        }
    }

    private void onRecInfo(int i, int i2) {
        int maxAmplitude;
        if (this.mRecorderState != 2) {
            Log.m26i(TAG, "onRecInfo skip - what : " + i + " extra : " + i2);
        } else if (i == 800) {
            Log.m22e(TAG, "onRecInfo - MEDIA_RECORDER_INFO_MAX_DURATION_REACHED : extra = " + i2);
            notifyObservers(1021, i2, -1);
        } else if (i == 801) {
            Log.m22e(TAG, "onRecInfo - MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED : extra = " + i2);
            notifyObservers(1022, i2, -1);
        } else if (i == 901) {
            synchronized (this.mMediaRecorder) {
                maxAmplitude = this.mMediaRecorder.getMaxAmplitude();
            }
            this.mRecordEndTime = this.mRecordStartTime + i2;
            if (this.mAudioFormat.getAudioEncoder() == 1 && this.mRecordCorrectionTime == 0) {
                Log.m32w(TAG, "onRecInfo - First recording time for AMR_NB : " + i2);
                if (i2 > 200) {
                    this.mRecordCorrectionTime = 40 - i2;
                } else {
                    this.mRecordCorrectionTime = -1;
                }
            }
            int i3 = this.mRecordCorrectionTime;
            if (i3 < -1) {
                this.mRecordEndTime += i3;
            }
            setCurrentTime(this.mRecordEndTime);
            int i4 = this.mRecordMode;
            if (i4 == 2) {
                String parameters = this.mAudioManager.getParameters("g_record_conversation_energy_key");
                if (parameters != null && parameters.contains(";")) {
                    Log.m29v(TAG, "conversation_energy : " + parameters);
                    String[] split = parameters.split(";");
                    int parseInt = Integer.parseInt(split[0].split("=")[1]);
                    int parseInt2 = Integer.parseInt(split[1].split("=")[1]);
                    synchronized (this.mLeftSpeechTime) {
                        this.mLeftSpeechTime.calc(this.mRecordEndTime, parseInt);
                    }
                    synchronized (this.mRightSpeechTime) {
                        this.mRightSpeechTime.calc(this.mRecordEndTime, parseInt2);
                    }
                    int i5 = parseInt2 + (parseInt << 16);
                    maxAmplitude = i5 < 0 ? 0 : i5;
                    checkNoSound(2);
                }
            } else if (i4 != 3) {
                maxAmplitude = i4 != 4 ? maxAmplitude / 2 : maxAmplitude / 2;
            } else {
                synchronized (this.mLeftSpeechTime) {
                    this.mLeftSpeechTime.calc(this.mRecordEndTime, maxAmplitude);
                }
            }
            notifyObservers(1011, this.mRecordEndTime, maxAmplitude);
            if (Engine.getInstance().getOverwriteStartTime() != -1) {
                Engine.getInstance().setOverwriteEndTime(this.mRecordEndTime);
            }
            if (Engine.getInstance().getTrimStartTime() != -1 && Engine.getInstance().getTrimEndTime() < this.mRecordEndTime) {
                Engine.getInstance().setTrimEndTime(this.mRecordEndTime, false);
            }
            MetadataRepository instance = MetadataRepository.getInstance();
            if (instance != null) {
                instance.addAmplitudeData(maxAmplitude);
            }
        }
    }

    private boolean isLowBattery() {
        Intent registerReceiver = this.mAppContext.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        if (registerReceiver == null) {
            return false;
        }
        int intExtra = registerReceiver.getIntExtra("status", 1);
        int intExtra2 = registerReceiver.getIntExtra("scale", 100);
        int intExtra3 = registerReceiver.getIntExtra("level", intExtra2);
        if (intExtra2 == 0) {
            return true;
        }
        if ((((float) intExtra3) * 100.0f) / ((float) intExtra2) > 1.0f || intExtra == 2) {
            return false;
        }
        Log.m26i(TAG, "isLowBattery - Battery Level = " + intExtra3 + '/' + intExtra2);
        StringBuilder sb = new StringBuilder();
        sb.append("isLowBattery - Battery Status = ");
        sb.append(intExtra);
        Log.m26i(TAG, sb.toString());
        return true;
    }

    private int prepareRecord(String str, AudioFormat audioFormat) {
        int i;
        int i2;
        Log.m26i(TAG, "prepareRecord");
        Context context = this.mAppContext;
        if (context == null) {
            return -114;
        }
        if (!isMicroPhoneRestricted(context)) {
            if (DesktopModeProvider.isDesktopMode()) {
                DesktopModeProvider.getInstance().checkDualView();
            }
            this.mRecordMode = MetadataRepository.getInstance().getRecordMode();
            if (this.mRecordMode != 0) {
                i2 = MetadataRepository.getInstance().getRecQuality();
                i = MetadataRepository.getInstance().getRecChCount();
            } else {
                i2 = -1;
                i = -1;
            }
            int i3 = this.mRecordMode;
            if (i3 == 0) {
                i3 = com.sec.android.app.voicenote.provider.Settings.getIntSettings("record_mode", 1);
            }
            this.mRecordMode = i3;
            int i4 = this.mRecordMode;
            int i5 = 5;
            if (i4 == 5 || i4 == 6) {
                this.mRecordMode = 1;
            }
            int i6 = this.mRecordMode;
            Log.m26i(TAG, "prepareRecord - recordingMode : " + this.mRecordMode);
            if (StorageProvider.getAvailableStorage(str) <= 0) {
                return -107;
            }
            if (StorageProvider.getAvailableStorage(str) <= 524288000) {
                initProgressCheckFullStorage();
            }
            if (isLowBattery()) {
                return -121;
            }
            try {
                this.mMediaRecorder = new MediaRecorder();
                if (this.mAudioManager == null) {
                    this.mAudioManager = (AudioManager) this.mAppContext.getSystemService("audio");
                }
                if (!requestAudioFocus()) {
                    return -109;
                }
                if (isRecordActive()) {
                    return -120;
                }
                this.mMediaRecorder.setOutputFile(str);
                setupRejectCall();
                int i7 = this.mRecordMode;
                if (i7 == 1) {
                    boolean booleanSettings = com.sec.android.app.voicenote.provider.Settings.getBooleanSettings(com.sec.android.app.voicenote.provider.Settings.KEY_REC_STEREO, false);
                    com.sec.android.app.voicenote.provider.Settings.getBooleanSettings(com.sec.android.app.voicenote.provider.Settings.KEY_BLUETOOTH_SCO_CONNECT, false);
                    if (i <= 0) {
                        i = (!booleanSettings || Engine.getInstance().isBluetoothSCOConnected()) ? 1 : 2;
                    }
                    MediaRecorder mediaRecorder = this.mMediaRecorder;
                    if (i <= 1) {
                        i5 = 1;
                    }
                    mediaRecorder.setAudioSource(i5);
                    this.mMediaRecorder.setAudioChannels(i);
                    this.mAudioManager.setParameters(SS_VR_ON);
                    if (audioFormat.isBluetoothSco()) {
                        BluetoothHelper.getInstance().startRecord();
                    }
                    if (i == 2) {
                        setRecordForStereoOn(true);
                    } else {
                        setRecordForStereoOn(false);
                    }
                    MetadataRepository.getInstance().setRecChCount(i);
                } else if (i7 == 2) {
//                    this.mMediaRecorder.setAudioSource(MediaRecorder.semGetInputSource(9));
                    this.mMediaRecorder.setAudioChannels(2);
                    this.mAudioManager.setParameters("g_record_beamforming_mode=1");
                    this.mLeftSpeechTime.init(1);
                    this.mLeftSpeechTime.setRealTimeMode(true);
                    this.mRightSpeechTime.init(2);
                    this.mRightSpeechTime.setRealTimeMode(true);
                } else if (i7 != 4) {
                    this.mMediaRecorder.setAudioSource(1);
                    this.mMediaRecorder.setAudioChannels(1);
                } else {
                    this.mMediaRecorder.setAudioSource(6);
                    if (i <= 0) {
                        i = 1;
                    }
                    this.mMediaRecorder.setAudioChannels(i);
                }
                if (i2 == -1) {
                    i2 = com.sec.android.app.voicenote.provider.Settings.getIntSettings(com.sec.android.app.voicenote.provider.Settings.KEY_REC_QUALITY, 1);
                }
                audioFormat.setRecordingQuality(i2);
                this.mMediaRecorder.setOutputFormat(audioFormat.getOutputFormat());
                this.mMediaRecorder.setAudioEncoder(audioFormat.getAudioEncoder());
                this.mMediaRecorder.setMaxFileSize(audioFormat.getMaxFileSize(str));
                this.mMediaRecorder.setMaxDuration(audioFormat.getMaxDuration() - this.mCurrentTime);
                this.mMediaRecorder.setAudioEncodingBitRate(audioFormat.getAudioEncodingBitrate());
                this.mMediaRecorder.setAudioSamplingRate(audioFormat.getAudioSamplingRate());
//                this.mMediaRecorder.semSetDurationInterval(35);
                MetadataRepository.getInstance().setRecordMode(i6);
                MetadataRepository.getInstance().setRecQuality(i2);
                try {
                    this.mMediaRecorder.prepare();
                    return 0;
                } catch (IOException e) {
                    Log.m24e(TAG, "IOException", (Throwable) e);
                    MediaRecorder mediaRecorder2 = this.mMediaRecorder;
                    if (mediaRecorder2 != null) {
                        mediaRecorder2.reset();
                        this.mMediaRecorder.release();
                        this.mMediaRecorder = null;
                    }
                    return -114;
                }
            } catch (RuntimeException e2) {
                Log.m24e(TAG, "SecMediaRecorder RuntimeException !", (Throwable) e2);
                return -114;
            }
        } else if (!VoiceNoteFeature.isSupportPSLTE_KOR()) {
            return Engine.ReturnCodes.MICROPHONE_RESTRICTED;
        } else {
            Log.m26i(TAG, "send intent : com.dkitec.mdm.android.action.AUDIT_EVENT");
            Intent intent = new Intent();
            intent.setAction("com.dkitec.mdm.android.action.AUDIT_EVENT");
            intent.putExtra("subject", "MIC");
            this.mAppContext.sendBroadcast(intent);
            return Engine.ReturnCodes.MICROPHONE_RESTRICTED;
        }
    }

    private boolean startBluetoothSCO() {
        Log.m26i(TAG, "startBluetoothSCO");
        Log.m32w(TAG, "startBluetoothSCO - this feature is turned off!!!");
        return false;
    }

    public void setupRejectCall() {
        if (com.sec.android.app.voicenote.provider.Settings.getBooleanSettings(com.sec.android.app.voicenote.provider.Settings.KEY_REC_CALL_REJECT, false) && VoiceNoteApplication.getScene() != 6 && !Engine.getInstance().isSimpleRecorderMode()) {
            RunOptionMenu.getInstance().setRejectCall(true);
        }
    }

    public int startRecord(String str, AudioFormat audioFormat) {
        Log.m26i(TAG, "startRecord - mRecorderState : " + this.mRecorderState + " mCurrentTime : " + this.mCurrentTime);
        if (this.mRecorderState == 2) {
            Log.m32w(TAG, "startRecord - it is already recording state");
            return -108;
        }
        this.mAudioFormat = audioFormat;
        this.mRecordStartTime = this.mCurrentTime;
        this.mRecordEndTime = this.mRecordStartTime;
        this.mRecordCorrectionTime = 0;
        this.mNoSoundCount = 0;
        this.needAbandonAudioFocus = true;
        if (this.mAudioFormat == null) {
            this.mAudioFormat = new AudioFormat(this.mRecordMode);
        }
        Log.m29v(TAG, "    startRecord - mRecordStartTime : " + this.mRecordStartTime + " mRecordEndTime : " + this.mRecordEndTime);
        int prepareRecord = prepareRecord(str, this.mAudioFormat);
        if (prepareRecord != 0) {
            return prepareRecord;
        }
        this.mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            public final void onInfo(MediaRecorder mediaRecorder, int i, int i2) {
                Recorder.this.lambda$startRecord$0$Recorder(mediaRecorder, i, i2);
            }
        });
        try {
            disableSystemSound();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Log.m22e(TAG, e.toString());
            }
            this.mMediaRecorder.start();
            if (this.mRecordMode == 4) {
                VoiceWorkerForP.getInstance().startSTT(this.mAppContext);
            }
            this.mTelephonyManager = (TelephonyManager) this.mAppContext.getSystemService("phone");
            ((TelephonyManager) Objects.requireNonNull(this.mTelephonyManager)).listen(this.mPhoneStateListener, 32);
            SurveyLogProvider.startRecordingLog();
            SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_RECORD, this.mRecordMode);
            setRecorderState(2);
            this.mNoSoundCheckTime = System.currentTimeMillis();
            return prepareRecord;
        } catch (IllegalStateException e2) {
            Log.m24e(TAG, "startRecord failed due to illegalStateException", (Throwable) e2);
            return -114;
        } catch (RuntimeException e3) {
            Log.m24e(TAG, "startRecord failed due to RuntimeException", (Throwable) e3);
            return -114;
        }
    }

    public /* synthetic */ void lambda$startRecord$0$Recorder(MediaRecorder mediaRecorder, int i, int i2) {
        onRecInfo(i, i2);
    }

    public boolean pauseRecord() {
        Log.m26i(TAG, "pauseRecord - mRecorderState : " + this.mRecorderState);
        if (this.mMediaRecorder == null) {
            Log.m22e(TAG, "pauseRecord MediaRecorder is null !!!");
            return false;
        } else if (this.mRecorderState != 2 || this.mRecordEndTime <= 0) {
            return false;
        } else {
            Log.m29v(TAG, "    pauseRecord - mRecordStartTime : " + this.mRecordStartTime + " mRecordEndTime : " + this.mRecordEndTime + " mCurrentTime : " + this.mCurrentTime);
            try {
                this.mMediaRecorder.pause();
            } catch (RuntimeException e) {
                Log.m24e(TAG, "pauseRecord failed", (Throwable) e);
            }
            if (this.mRecordMode == 4) {
                VoiceWorkerForP.getInstance().pauseSTT();
            }
            setRecorderState(3);
            enableSystemSound();
            return true;
        }
    }

    private int resumeRecord(String str) {
        Log.m26i(TAG, "resumeRecord - mRecorderState : " + this.mRecorderState);
        int i = this.mRecorderState;
        if (i != 3 && i != 4 && i != 1) {
            return -110;
        }
        Log.m29v(TAG, "    resumeRecord - mRecordStartTime : " + this.mRecordStartTime + " mRecordEndTime : " + this.mRecordEndTime + " mCurrentTime : " + this.mCurrentTime);
        int i2 = this.mRecorderState;
        int i3 = 0;
        if (i2 == 3) {
            if (!requestAudioFocus()) {
                return -109;
            }
            if (Engine.getInstance().isBluetoothSCOConnected()) {
                Engine.getInstance().setRecordByBluetoothSCO(startBluetoothSCO());
            } else {
                Engine.getInstance().setRecordByBluetoothSCO(false);
            }
            disableSystemSound();
            MediaRecorder mediaRecorder = this.mMediaRecorder;
            if (mediaRecorder != null) {
                try {
                    mediaRecorder.resume();
                } catch (RuntimeException e) {
                    Log.m24e(TAG, "resumeRecord failed", (Throwable) e);
                }
                if (this.mRecordMode == 4) {
                    VoiceWorkerForP.getInstance().resumeSTT();
                }
            }
        } else if (i2 == 4 || i2 == 1) {
            if (this.mOverwriteByDrag || VoiceNoteApplication.getScene() == 6) {
                Engine.getInstance().resetOverwriteTime();
                Engine.getInstance().setOverwriteStartTime(this.mCurrentTime);
            }
            i3 = startRecord(str, this.mAudioFormat);
        }
        if (i3 == 0) {
            setRecorderState(2);
        }
        this.mNoSoundCheckTime = System.currentTimeMillis();
        return i3;
    }

    /* access modifiers changed from: package-private */
    public int resumeRecord(String str, int i) {
        Log.m26i(TAG, "resumeRecord - path : " + str);
        setCurrentTime(i);
        return resumeRecord(str);
    }

    private boolean stopRecordInternal() {
        Log.m26i(TAG, "stopRecordInternal - mRecorderState : " + this.mRecorderState);
        if (this.mMediaRecorder == null) {
            Log.m22e(TAG, "stopRecord MediaRecorder is null !!!");
            return false;
        }
        int i = this.mRecorderState;
        if (i != 2 && i != 3) {
            return false;
        }
        Log.m29v(TAG, "    stopRecord - mRecordStartTime : " + this.mRecordStartTime + " mRecordEndTime : " + this.mRecordEndTime + " mCurrentTime : " + this.mCurrentTime);
        if (this.mRecordMode == 1) {
            this.mAudioManager.setParameters(SS_VR_OFF);
        }
        try {
            this.mMediaRecorder.stop();
            this.mMediaRecorder.reset();
            this.mMediaRecorder.release();
        } catch (IllegalStateException e) {
            Log.m24e(TAG, "IllegalStateException", (Throwable) e);
        } catch (RuntimeException e2) {
            Log.m24e(TAG, "RuntimeException", (Throwable) e2);
        } catch (Throwable th) {
            this.mMediaRecorder = null;
            setRecorderState(4);
            this.mRecordMode = 0;
            enableSystemSound();
            throw th;
        }
        this.mMediaRecorder = null;
        setRecorderState(4);
        this.mRecordMode = 0;
        enableSystemSound();
        abandonAudioFocus();
        if (this.mAudioFormat.isBluetoothSco()) {
            BluetoothHelper.getInstance().stopRecord();
        }
        if (isRecordForStereoOn()) {
            setRecordForStereoOn(false);
        }
        if (Engine.getInstance().ismIsNeedReleaseMediaSession()) {
            MediaSessionManager.getInstance().destroyMediaSession();
            VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.RECORD_RELEASE_MEDIASESSION));
        }
        setOverwriteByDrag(false);
        return true;
    }

    public int getDuration() {
        Log.m26i(TAG, "getDuration - mRecordEndTime= " + this.mRecordEndTime + " - mRecordStartTime= " + this.mRecordStartTime);
        return this.mRecordEndTime - this.mRecordStartTime;
    }

    public boolean isSaveEnable() {
        Log.m26i(TAG, "isSaveEnable - mRecordEndTime= " + this.mRecordEndTime + " - mRecordStartTime= " + this.mRecordStartTime);
        return this.mRecordEndTime - this.mRecordStartTime > 1000;
    }

    /* access modifiers changed from: package-private */
    public boolean cancelRecord() {
        Log.m26i(TAG, "cancelRecord - mRecorderState : " + this.mRecorderState);
        int i = this.mRecorderState;
        if (i != 2 && i != 3 && i != 4) {
            return false;
        }
        Log.m29v(TAG, "    cancelRecord - mRecordStartTime : " + this.mRecordStartTime + " mRecordEndTime : " + this.mRecordEndTime + " mCurrentTime : " + this.mCurrentTime);
        MediaRecorder mediaRecorder = this.mMediaRecorder;
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                this.mMediaRecorder.reset();
                this.mMediaRecorder.release();
            } catch (IllegalStateException e) {
                Log.m24e(TAG, "IllegalStateException", (Throwable) e);
            } catch (RuntimeException e2) {
                Log.m24e(TAG, "RuntimeException", (Throwable) e2);
            } catch (Throwable th) {
                this.mMediaRecorder = null;
                throw th;
            }
            this.mMediaRecorder = null;
            abandonAudioFocus();
        }
        if (this.mRecordMode == 4) {
            VoiceWorkerForP.getInstance().cancelSTT();
        }
        if (this.mAudioFormat.isBluetoothSco()) {
            BluetoothHelper.getInstance().stopRecord();
        }
        if (isRecordForStereoOn()) {
            setRecordForStereoOn(false);
        }
        if (Engine.getInstance().ismIsNeedReleaseMediaSession()) {
            MediaSessionManager.getInstance().destroyMediaSession();
            VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.RECORD_RELEASE_MEDIASESSION));
        }
        setAutoResumeRecording(false);
        setRecorderState(1);
        enableSystemSound();
        MetadataRepository.getInstance().close();
        setOverwriteByDrag(false);
        return true;
    }

    public void setCurrentTime(int i) {
        if (i < 0) {
            i = 0;
        }
        this.mCurrentTime = i;
    }

    /* access modifiers changed from: package-private */
    public void stopSTT() {
        int recordMode = MetadataRepository.getInstance().getRecordMode();
        if (recordMode == 0) {
            recordMode = com.sec.android.app.voicenote.provider.Settings.getIntSettings("record_mode", 1);
        }
        if (recordMode == 4) {
            VoiceWorkerForP.getInstance().stopSTT();
        }
    }

    /* access modifiers changed from: package-private */
    public void abandonAudioFocus() {
        if (this.needAbandonAudioFocus && this.mAudioManager != null && this.mAudioFocusListener != null) {
            Log.m26i(TAG, "abandonAudioFocus");
            this.mAudioManager.abandonAudioFocusRequest(getAudioFocusRequest());
        }
    }

    /* access modifiers changed from: package-private */
    public boolean saveFile(ContentItem contentItem) {
        if (contentItem == null) {
            return false;
        }
        stopRecordInternal();
        if (!isSaveEnable()) {
            cancelRecord();
            return false;
        }
        File file = new File(contentItem.getPath());
        if (!file.exists() || file.length() == 0) {
            Log.m22e(TAG, "exist : " + file.exists() + " size : " + file.length());
            if (Log.ENG) {
                Log.m22e(TAG, "exist : " + file.exists() + " path : " + file.getPath());
            }
            cancelRecord();
            return false;
        }
        contentItem.setStartTime(this.mRecordStartTime);
        contentItem.setEndTime(this.mRecordEndTime);
        contentItem.setDuration(this.mRecordEndTime - this.mRecordStartTime);
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mediaMetadataRetriever.setDataSource(contentItem.getPath());
            contentItem.setDuration(Integer.parseInt(mediaMetadataRetriever.extractMetadata(9)));
        } catch (Exception e) {
            Log.m24e(TAG, "METADATA_KEY_DURATION parsing error", (Throwable) e);
        } catch (Throwable th) {
            mediaMetadataRetriever.release();
            throw th;
        }
        mediaMetadataRetriever.release();
        contentItem.setEndTime(contentItem.getStartTime() + contentItem.getDuration());
        int maxDuration = getMaxDuration(MetadataRepository.getInstance().getRecordMode());
        if (maxDuration != -1 && contentItem.getEndTime() > maxDuration) {
            contentItem.setEndTime(maxDuration);
        }
        Log.m26i(TAG, "saveFile start:" + contentItem.getStartTime() + " end:" + contentItem.getEndTime() + " duration:" + contentItem.getDuration());
        return true;
    }

    /* access modifiers changed from: package-private */
    public void initPhoneStateListener() {
        Log.m26i(TAG, "initPhoneStateListener");
        TelephonyManager telephonyManager = this.mTelephonyManager;
        if (telephonyManager != null) {
            telephonyManager.listen(this.mPhoneStateListener, 0);
        }
    }

    /* access modifiers changed from: package-private */
    public void setAudioFormat(AudioFormat audioFormat) {
        this.mAudioFormat = audioFormat;
    }

    private void setRecorderState(int i) {
        Log.m26i(TAG, "setRecorderState - state : " + i);
        notifyObservers(1010, i, -1);
        if (i == 1) {
            this.mRecordStartTime = 0;
            this.mRecordEndTime = 0;
            this.mRecordCorrectionTime = 0;
            setCurrentTime(0);
            MetadataRepository.getInstance().close();
        } else if (i != 2) {
            if (i != 3 && i == 4) {
                MetadataRepository.getInstance().stopAmplitude(this.mRecordStartTime, this.mRecordEndTime);
            }
        } else if (this.mRecorderState != 3) {
            MetadataRepository.getInstance();
            MetadataRepository.getInstance().initAmplitude();
        }
        this.mRecorderState = i;
    }

    public int getRecorderState() {
        return this.mRecorderState;
    }

    public int getRecordMode() {
        return this.mRecordMode;
    }

    public boolean isAutoResumeRecording() {
        return this.mAutoResumeRecording;
    }

    public void setAutoResumeRecording(boolean z) {
        this.mAutoResumeRecording = z;
    }

    public void setOverwriteByDrag(boolean z) {
        Log.m19d(TAG, "setOverwriteByDrag - state : " + z);
        this.mOverwriteByDrag = z;
    }

    private boolean containsListener(OnRecorderListener onRecorderListener) {
        ArrayList<WeakReference<OnRecorderListener>> arrayList = this.mListeners;
        if (!(arrayList == null || onRecorderListener == null)) {
            Iterator<WeakReference<OnRecorderListener>> it = arrayList.iterator();
            while (it.hasNext()) {
                if (((OnRecorderListener) it.next().get()).equals(onRecorderListener)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void removeListener(OnRecorderListener onRecorderListener) {
        ArrayList<WeakReference<OnRecorderListener>> arrayList = this.mListeners;
        if (arrayList != null && onRecorderListener != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                WeakReference weakReference = this.mListeners.get(size);
                if (weakReference.get() == null || ((OnRecorderListener) weakReference.get()).equals(onRecorderListener)) {
                    this.mListeners.remove(weakReference);
                }
            }
        }
    }

    public void registerListener(OnRecorderListener onRecorderListener) {
        if (onRecorderListener != null && !containsListener(onRecorderListener)) {
            Log.m19d(TAG, "registerListener : " + onRecorderListener.getClass().getSimpleName());
            this.mListeners.add(new WeakReference(onRecorderListener));
            int i = this.mRecorderState;
            if (i != 1) {
                onRecorderListener.onRecorderUpdate(1010, i, -1);
                onRecorderListener.onRecorderUpdate(1011, this.mCurrentTime, -1);
            }
        }
    }

    public void unregisterListener(OnRecorderListener onRecorderListener) {
        if (onRecorderListener != null) {
            Log.m19d(TAG, "unregisterListener : " + onRecorderListener.getClass().getSimpleName());
        }
        if (onRecorderListener != null && containsListener(onRecorderListener)) {
            removeListener(onRecorderListener);
        }
    }

    private void unregisterAllListener() {
        Log.m26i(TAG, "unregisterAllListener");
        this.mListeners.clear();
    }

    /* access modifiers changed from: private */
    public void notifyObservers(int i, int i2, int i3) {
        Iterator<WeakReference<OnRecorderListener>> it = this.mListeners.iterator();
        while (it.hasNext()) {
            WeakReference next = it.next();
            if (next.get() == null) {
                this.mListeners.remove(next);
            } else {
                ((OnRecorderListener) next.get()).onRecorderUpdate(i, i2, i3);
            }
        }
    }

    public void disableSystemSound() {
        Log.m26i(TAG, "disableSystemSound");
        if (this.mAppContext != null) {
            try {
                StatusBarProvider.getInstance().disable(this.mAppContext, 262144);
                setStreamMute(true);
                this.mAudioManager.adjustStreamVolume(2, -100, 0);
                this.mVibrateWhileRingingState = Settings.System.getInt(this.mAppContext.getContentResolver(), "vibrate_when_ringing", 1);
                if (this.mAudioManager.getRingerMode() == 2 && this.mVibrateWhileRingingState == 0) {
                    Settings.System.putInt(this.mAppContext.getContentResolver(), "vibrate_when_ringing", 1);
                }
            } catch (SecurityException e) {
                Log.m32w(TAG, "disableSystemSound : " + e);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void enableSystemSound() {
        if (this.mAppContext != null) {
            Log.m19d(TAG, "enableSystemSound");
            try {
                StatusBarProvider.getInstance().disable(this.mAppContext, 0);
                setStreamMute(false);
                if (getRecorderState() != 3 && this.mAudioManager.getRingerMode() == 2) {
                    this.mAudioManager.adjustStreamVolume(2, 100, 0);
                    Settings.System.putInt(this.mAppContext.getContentResolver(), "vibrate_when_ringing", this.mVibrateWhileRingingState);
                }
            } catch (SecurityException e) {
                Log.m32w(TAG, "enableSystemSound : " + e);
            }
        }
    }

    private void setStreamMute(boolean z) {
        if (z) {
            this.mMuteHandler.removeMessages(1);
            this.mMuteHandler.sendEmptyMessageDelayed(1, 20);
            return;
        }
        this.mMuteHandler.removeMessages(0);
        this.mMuteHandler.sendEmptyMessageDelayed(0, 20);
    }

    private static class VolumeHandler extends Handler {
        WeakReference<Recorder> mWeakRefRecorder;

        VolumeHandler(Recorder recorder) {
            this.mWeakRefRecorder = new WeakReference<>(recorder);
        }

        public void handleMessage(Message message) {
            Recorder recorder = (Recorder) this.mWeakRefRecorder.get();
            if (recorder != null) {
                Log.m19d(Recorder.TAG, "mMuteHandler what : " + message.what + " arg1 : " + message.arg1 + " arg2 : " + message.arg2);
                if (recorder.mAudioManager == null) {
                    Log.m26i(Recorder.TAG, "mMuteHandler mAudioManager is null");
                    return;
                }
                int streamVolume = recorder.mAudioManager.getStreamVolume(1);
                int streamVolume2 = recorder.mAudioManager.getStreamVolume(5);
                Log.m19d(Recorder.TAG, "mMuteHandler - Current STREAM_SYSTEM : " + streamVolume + " STREAM_NOTIFICATION : " + streamVolume2);
                int i = message.what;
                if (i == 0) {
                    if (com.sec.android.app.voicenote.provider.Settings.getBooleanSettings(com.sec.android.app.voicenote.provider.Settings.KEY_ENABLE_SYS_SOUND, false)) {
                        int intSettings = com.sec.android.app.voicenote.provider.Settings.getIntSettings(com.sec.android.app.voicenote.provider.Settings.KEY_VOLUME_STREAM_SYSTEM, 8);
                        Log.m26i(Recorder.TAG, "mMuteHandler - STREAM_SYSTEM Backup Volume : " + intSettings);
                        recorder.mAudioManager.setStreamVolume(1, intSettings, 0);
                        com.sec.android.app.voicenote.provider.Settings.setSettings(com.sec.android.app.voicenote.provider.Settings.KEY_ENABLE_SYS_SOUND, false);
                    }
                    if (com.sec.android.app.voicenote.provider.Settings.getBooleanSettings(com.sec.android.app.voicenote.provider.Settings.KEY_ENABLE_NOTI_SOUND, false)) {
                        int intSettings2 = com.sec.android.app.voicenote.provider.Settings.getIntSettings(com.sec.android.app.voicenote.provider.Settings.KEY_VOLUME_STREAM_NOTIFICATION, 11);
                        Log.m26i(Recorder.TAG, "mMuteHandler - STREAM_NOTIFICATION Backup Volume : " + intSettings2);
                        recorder.mAudioManager.setStreamVolume(5, intSettings2, 0);
                        com.sec.android.app.voicenote.provider.Settings.setSettings(com.sec.android.app.voicenote.provider.Settings.KEY_ENABLE_NOTI_SOUND, false);
                    }
                } else if (i == 1) {
                    if (streamVolume > 0) {
                        Log.m26i(Recorder.TAG, "mMuteHandler - STREAM_SYSTEM Mute");
                        com.sec.android.app.voicenote.provider.Settings.setSettings(com.sec.android.app.voicenote.provider.Settings.KEY_VOLUME_STREAM_SYSTEM, streamVolume);
                        recorder.mAudioManager.setStreamVolume(1, 0, 0);
                        com.sec.android.app.voicenote.provider.Settings.setSettings(com.sec.android.app.voicenote.provider.Settings.KEY_ENABLE_SYS_SOUND, true);
                    }
                    if (streamVolume2 > 0) {
                        Log.m26i(Recorder.TAG, "mMuteHandler - STREAM_NOTIFICATION Mute");
                        com.sec.android.app.voicenote.provider.Settings.setSettings(com.sec.android.app.voicenote.provider.Settings.KEY_VOLUME_STREAM_NOTIFICATION, streamVolume2);
                        recorder.mAudioManager.setStreamVolume(5, 0, 0);
                        com.sec.android.app.voicenote.provider.Settings.setSettings(com.sec.android.app.voicenote.provider.Settings.KEY_ENABLE_NOTI_SOUND, true);
                    }
                }
            }
        }
    }

    private boolean requestAudioFocus() {
        int i = 0;
        for (int i2 = 0; i2 < 10 && (i = this.mAudioManager.requestAudioFocus(getAudioFocusRequest())) != 1; i2++) {
            SystemClock.sleep(50);
        }
        if (i == 0) {
            Log.m22e(TAG, "requestAudioFocus is failed");
            return false;
        }
        Log.m26i(TAG, "requestAudioFocus is success");
        return true;
    }

    private AudioFocusRequest getAudioFocusRequest() {
        if (this.mAudioFocusRequest == null) {
            this.mAudioFocusRequest = new AudioFocusRequest.Builder(1).setAudioAttributes(new AudioAttributes.Builder().setLegacyStreamType(3).build()).setAcceptsDelayedFocusGain(true).setOnAudioFocusChangeListener(this.mAudioFocusListener).build();
        }
        return this.mAudioFocusRequest;
    }

    private boolean isRecordActive() {
        if (this.mAudioManager == null) {
            Context context = this.mAppContext;
            if (context == null) {
                return false;
            }
            this.mAudioManager = (AudioManager) context.getSystemService("audio");
        }
        boolean z = true;
        int i = 0;
        while (true) {
            if (i >= 25) {
                break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Log.m24e(TAG, "InterruptedException !", (Throwable) e);
            }
//            z = this.mAudioManager.semIsRecordActive(5) || this.mAudioManager.semIsRecordActive(1) || this.mAudioManager.semIsRecordActive(MediaRecorder.semGetInputSource(9)) || this.mAudioManager.semIsRecordActive(-1);
            if (!z) {
                Log.m26i(TAG, "GOT AudioFOCUS,but Audio_FW stopInput completely after (x50ms): " + i);
                break;
            }
            i++;
        }
        Log.m26i(TAG, "AnotherRecordActive: " + z);
        return z;
    }

    private void checkNoSound(int i) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.mNoSoundCheckTime >= MAX_NO_SOUND_TIME) {
            if (i == 2) {
                if (this.mLeftSpeechTime.isMute(MAX_NO_SOUND_TIME) && this.mRightSpeechTime.isMute(MAX_NO_SOUND_TIME)) {
                    this.mNoSoundCount++;
                    this.mNoSoundCount %= 3;
                    if (this.mNoSoundCount == 0) {
                        notifyObservers(1026, -1, -1);
                    } else {
                        notifyObservers(1025, -1, -1);
                    }
                    this.mNoSoundCheckTime = currentTimeMillis;
                    return;
                }
            } else if (this.mLeftSpeechTime.isMute(MAX_NO_SOUND_TIME)) {
                this.mNoSoundCount++;
                this.mNoSoundCount %= 3;
                if (this.mNoSoundCount == 0) {
                    notifyObservers(1026, -1, -1);
                } else {
                    notifyObservers(1025, -1, -1);
                }
                this.mNoSoundCheckTime = currentTimeMillis;
                return;
            }
            this.mNoSoundCount = 0;
        }
    }

    public synchronized void onDestroy() {
        unregisterAllListener();
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
        this.mAppContext = null;
        this.mMediaRecorder = null;
        this.mMuteHandler = null;
        this.mAudioManager = null;
        this.mAudioFocusRequest = null;
        this.mStorageHandler = null;
    }

    private static boolean isMicroPhoneRestricted(Context context) {
        return isRestrictedByPolicy(context, "content://com.sec.knox.provider/RestrictionPolicy2", "isMicrophoneEnabled");
    }

    private static boolean isRestrictedByPolicy(Context context, String str, String str2) {
        Cursor query;
        Uri parse = Uri.parse(str);
        if (context == null || (query = context.getContentResolver().query(parse, (String[]) null, str2, new String[]{DeviceInfo.STR_TRUE}, (String) null)) == null) {
            return false;
        }
        try {
            query.moveToFirst();
            if (query.getString(query.getColumnIndex(str2)).equals("false")) {
                Log.m26i(TAG, "isRestrictedByPolicy - Microphone is disabled.");
                return true;
            }
            query.close();
            return false;
        } finally {
            query.close();
        }
    }

    private static class StorageHandler extends Handler {
        WeakReference<Recorder> mWeakRefRecorder;

        StorageHandler(Recorder recorder) {
            this.mWeakRefRecorder = new WeakReference<>(recorder);
        }

        public void handleMessage(Message message) {
            Recorder recorder = (Recorder) this.mWeakRefRecorder.get();
            if (message.what == 2000) {
                Log.m26i(Recorder.TAG, "CHECK_AVAIABLE_STORAGE : " + message.arg1);
                recorder.resultProcessCheckFullStorage(message.arg1);
            }
        }
    }

    private class StorageThread extends Thread implements Runnable {
        private StorageThread() {
        }

        public void run() {
            int i;
            String tempFilePath = StorageProvider.getTempFilePath();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.m22e(Recorder.TAG, e.toString());
            }
            while (true) {
                i = -1;
                if (isInterrupted()) {
                    break;
                } else if (StorageProvider.getAvailableStorage(tempFilePath) < 0) {
                    i = 1;
                    break;
                } else if (Engine.getInstance().getRecorderState() != 2) {
                    break;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e2) {
                        Log.m22e(Recorder.TAG, e2.toString());
                    }
                }
            }
            Message message = new Message();
            message.what = Recorder.CHECK_AVAIABLE_STORAGE;
            message.arg1 = i;
            Recorder.this.mStorageHandler.sendMessage(message);
        }
    }

    public void initProgressCheckFullStorage() {
        interruptThread(this.mStorageThread);
        this.mStorageThread = new StorageThread();
        this.mStorageThread.start();
    }

    public void resultProcessCheckFullStorage(int i) {
        if (i == 1) {
            notifyObservers(1022, -1, -1);
        }
        interruptThread(this.mStorageThread);
    }

    private void interruptThread(Thread thread) {
        Log.m29v(TAG, "interruptThread : " + thread);
        if (isAliveThread(thread)) {
            thread.interrupt();
        }
    }

    private boolean isAliveThread(Thread thread) {
        return thread != null && thread.isAlive();
    }

    public void setRecordStartTime(int i) {
        this.mRecordStartTime = i;
    }

    public void setRecordEndTime(int i) {
        this.mRecordEndTime = i;
    }

    public void setResumeRecordByCall(boolean z) {
        this.mIsResumeRecordByCall = z;
    }

    public boolean isResumeRecordByCall() {
        return this.mIsResumeRecordByCall;
    }

    public void setRecordForStereoOn(boolean z) {
        this.mIsRecordForStereoOn = z;
    }

    public boolean isRecordForStereoOn() {
        return this.mIsRecordForStereoOn;
    }
}