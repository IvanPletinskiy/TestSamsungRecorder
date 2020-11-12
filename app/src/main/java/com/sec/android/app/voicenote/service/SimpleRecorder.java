package com.sec.android.app.voicenote.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
//import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.ITelephony;
import com.sec.android.app.voicenote.provider.CallRejectChecker;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.StatusBarProvider;
import com.sec.android.app.voicenote.provider.StorageProvider;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.service.SimpleRecorder;
import com.sec.android.app.voicenote.service.recognizer.VoiceWorker;
import com.sec.android.app.voicenote.service.recognizer.VoiceWorkerForP;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

public class SimpleRecorder {
    private static final int AUDIO_FOCUS_RETRY_INTERVAL = 50;
    public static final int INFO_AUDIOFOCUS_LOSS = 1020;
    public static final int INFO_CALL_ACCEPT = 1024;
    public static final int INFO_DURATION_PROGRESS = 1011;
    public static final int INFO_MAX_AMPLITUDE = 1012;
    public static final int INFO_MAX_DURATION_REACHED = 1021;
    public static final int INFO_MAX_FILESIZE_REACHED = 1022;
    public static final int INFO_NOT_ENOUGH_MEMORY = 1023;
    public static final int INFO_NO_SOUND_DETECT = 1025;
    public static final int INFO_NO_SOUND_DETECT_VIBRATE = 1026;
    public static final int INFO_RECORDER_STATE = 1010;
    private static final int MAX_AUDIO_FOCUS_RETRY_CNT = 10;
    private static final long MAX_NO_SOUND_TIME = 15000;
    private static final int NONE = -1;
    public static final int RECORD_DURATION_INTERVAL = 35;
    public static final int RECORD_MINIMUM_DURATION = 1000;
    public static final int SIMPLE_RECORD_SOURCE_ALL = -1;
    /* access modifiers changed from: private */
    public static String TAG = "SimpleRecorder";
    /* access modifiers changed from: private */
    public Context mAppContext;
    private final AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int i) {
            String access$000 = SimpleRecorder.TAG;
            Log.m27i(access$000, "onAudioFocusChange - focusChange : " + i, SimpleRecorder.this.mSession);
            if (i == -3) {
                return;
            }
            if (i != -2) {
                if (i == -1) {
                    if (SimpleRecorder.this.mRecorderState != 2 && SimpleRecorder.this.mRecorderState != 3) {
                        return;
                    }
                    if (SimpleRecorder.this.isSaveEnable()) {
                        SimpleRecorder.this.notifyObservers(1020, -1, -1);
                        return;
                    }
                    Log.m19d(SimpleRecorder.TAG, "AUDIOFOCUS_LOSS - cancel record");
                    new Handler().postDelayed(new Runnable() {
                        public final void run() {
//                            SimpleRecorder.C07721.this.lambda$onAudioFocusChange$0$SimpleRecorder$1();
                        }
                    }, 700);
                }
            } else if (SimpleRecorder.this.mRecorderState != 2 && SimpleRecorder.this.mRecorderState != 3) {
            } else {
                if (((TelephonyManager) SimpleRecorder.this.mAppContext.getSystemService("phone")).getCallState() != 1) {
                    SimpleRecorder.this.notifyObservers(1020, -1, -1);
                } else if (CallRejectChecker.getInstance().getReject()) {
                    Log.m27i(SimpleRecorder.TAG, "AUDIOFOCUS_LOSS_TRANSIENT : keep recording", SimpleRecorder.this.mSession);
                }
            }
        }

        public /* synthetic */ void lambda$onAudioFocusChange$0$SimpleRecorder$1() {
            SimpleRecorder.this.notifyObservers(1020, 1006, -1);
        }
    };
    private AudioFormat mAudioFormat;
    /* access modifiers changed from: private */
    public AudioManager mAudioManager = null;
    private int mCurrentTime = 0;
    private final SpeechTime mLeftSpeechTime = new SpeechTime();
    private final ArrayList<WeakReference<OnRecorderListener>> mListeners = new ArrayList<>();
    /* access modifiers changed from: private */
    public MediaRecorder mMediaRecorder = null;
    /* access modifiers changed from: private */
    public Handler mMuteHandler = new VolumeHandler(this);
    private long mNoSoundCheckTime = 0;
    private int mNoSoundCount = 0;
    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        public void onCallStateChanged(int i, String str) {
            String access$000 = SimpleRecorder.TAG;
            Log.m27i(access$000, "mPhoneStateListener : " + i + " mRecorderState : " + SimpleRecorder.this.mRecorderState, SimpleRecorder.this.mSession);
            if (i == 0) {
                return;
            }
            if (i != 1) {
                if (i == 2 && SimpleRecorder.this.mRecorderState != 1) {
                    SimpleRecorder.this.notifyObservers(1020, -1, -1);
                }
            } else if (CallRejectChecker.getInstance().getReject()) {
                SimpleRecorder.this.endCall();
            } else {
                Log.m27i(SimpleRecorder.TAG, "mPhoneStateListener : keep recording", SimpleRecorder.this.mSession);
                boolean reject = CallRejectChecker.getInstance().getReject();
            }
        }

        private /* synthetic */ void lambda$onCallStateChanged$0() {
            if (SimpleRecorder.this.mRecorderState == 1) {
                Log.m30v(SimpleRecorder.TAG, "Recorder state is IDLE", SimpleRecorder.this.mSession);
                return;
            }
            TelephonyManager telephonyManager = (TelephonyManager) SimpleRecorder.this.mAppContext.getSystemService("phone");
            String access$000 = SimpleRecorder.TAG;
            Log.m27i(access$000, "mPhoneStateListener : state after delay " + telephonyManager.getCallState(), SimpleRecorder.this.mSession);
            if (telephonyManager.getCallState() != 1) {
                return;
            }
            if (SimpleRecorder.this.mRecorderState == 2 || SimpleRecorder.this.mRecorderState == 3) {
                SimpleRecorder.this.notifyObservers(1024, 0, -1);
            }
        }
    };
    private int mRecordCorrectionTime = 0;
    private int mRecordEndTime = 0;
    private int mRecordMode = 0;
    private int mRecordStartTime = 0;
    /* access modifiers changed from: private */
    public int mRecorderState = 1;
    private final SpeechTime mRightSpeechTime = new SpeechTime();
    /* access modifiers changed from: private */
    public String mSession;
    private SimpleMetadataRepository mSimpleMetadata;
    private TelephonyManager mTelephonyManager = null;
    private int mVibrateWhileRingingState;
    private VoiceWorker mVoiceWorker = VoiceWorker.getInstance();

    public interface OnRecorderListener {
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

    public SimpleRecorder(Context context, String str) {
        this.mAppContext = context;
        this.mSession = str;
        Context context2 = this.mAppContext;
        if (context2 != null) {
            this.mAudioManager = (AudioManager) context2.getSystemService("audio");
        }
        this.mSimpleMetadata = SimpleMetadataRepositoryManager.getInstance().getMetadataRepository(this.mSession);
    }

    public void onRecInfo(int i, int i2) {
        int maxAmplitude;
        if (this.mRecorderState != 2) {
            Log.m27i(TAG, "onRecInfo skip - what : " + i + " extra : " + i2, this.mSession);
        } else if (i == 800) {
            Log.m23e(TAG, "onRecInfo - MEDIA_RECORDER_INFO_MAX_DURATION_REACHED : extra = " + i2, this.mSession);
            notifyObservers(1021, i2, -1);
        } else if (i == 801) {
            Log.m23e(TAG, "onRecInfo - MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED : extra = " + i2, this.mSession);
            notifyObservers(1022, i2, -1);
        } else if (i == 901) {
            synchronized (this.mMediaRecorder) {
                maxAmplitude = this.mMediaRecorder.getMaxAmplitude();
            }
            this.mRecordEndTime = this.mRecordStartTime + i2;
            if (this.mAudioFormat.getAudioEncoder() == 1 && this.mRecordCorrectionTime == 0) {
                Log.m33w(TAG, "onRecInfo - First recording time for AMR_NB : " + i2, this.mSession);
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
                if (parameters.contains(";")) {
                    Log.m30v(TAG, "conversation_energy : " + parameters, this.mSession);
                    String[] split = parameters.split(";");
                    int parseInt = Integer.parseInt(split[0].split("=")[1]);
                    int parseInt2 = Integer.parseInt(split[1].split("=")[1]);
                    synchronized (this.mLeftSpeechTime) {
                        this.mLeftSpeechTime.calc(this.mRecordEndTime, parseInt);
                    }
                    synchronized (this.mRightSpeechTime) {
                        this.mRightSpeechTime.calc(this.mRecordEndTime, parseInt2);
                    }
                    maxAmplitude = parseInt2 + (parseInt << 16);
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
            SimpleMetadataRepository simpleMetadataRepository = this.mSimpleMetadata;
            if (simpleMetadataRepository != null) {
                simpleMetadataRepository.addAmplitudeData(maxAmplitude);
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
        String str = TAG;
        Log.m27i(str, "isLowBattery - Battery Level = " + intExtra3 + '/' + intExtra2, this.mSession);
        String str2 = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("isLowBattery - Battery Status = ");
        sb.append(intExtra);
        Log.m27i(str2, sb.toString(), this.mSession);
        return true;
    }

    private int prepareRecord(String str, AudioFormat audioFormat) {
        int i;
        int i2;
        Log.m26i(TAG, "prepareRecord");
        if (this.mAppContext == null) {
            return -114;
        }
        this.mRecordMode = this.mSimpleMetadata.getRecordMode();
        if (this.mRecordMode != 0) {
            i2 = this.mSimpleMetadata.getRecQuality();
            i = this.mSimpleMetadata.getRecChCount();
        } else {
            i2 = -1;
            i = -1;
        }
        int i3 = this.mRecordMode;
        int i4 = 5;
        if (i3 == 5 || i3 == 6) {
            this.mRecordMode = 1;
        }
        int i5 = this.mRecordMode;
        Log.m27i(TAG, "prepareRecord - recordingMode : " + this.mRecordMode, this.mSession);
        if (StorageProvider.getAvailableStorage(str) <= 0) {
            return -107;
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
            this.mMediaRecorder.setMaxDuration(audioFormat.getMaxDuration() - this.mCurrentTime);
            int i6 = this.mRecordMode;
            if (i6 == 1) {
                if (i <= 0) {
                    i = com.sec.android.app.voicenote.provider.Settings.getBooleanSettings(Settings.KEY_REC_STEREO, false) ? 2 : 1;
                }
                MediaRecorder mediaRecorder = this.mMediaRecorder;
                if (i <= 1) {
                    i4 = 1;
                }
                mediaRecorder.setAudioSource(i4);
                this.mMediaRecorder.setAudioChannels(i);
                this.mSimpleMetadata.setRecChCount(i);
            } else if (i6 == 2) {
//                this.mMediaRecorder.setAudioSource(MediaRecorder.semGetInputSource(9));
                this.mMediaRecorder.setAudioChannels(2);
                this.mAudioManager.setParameters("g_record_beamforming_mode=1");
                this.mLeftSpeechTime.init(1);
                this.mLeftSpeechTime.setRealTimeMode(true);
                this.mRightSpeechTime.init(2);
                this.mRightSpeechTime.setRealTimeMode(true);
            } else if (i6 != 4) {
                this.mMediaRecorder.setAudioSource(1);
                this.mMediaRecorder.setAudioChannels(1);
            } else {
                this.mVoiceWorker.makeSttFolder();
                this.mMediaRecorder.setAudioSource(6);
                this.mMediaRecorder.setAudioChannels(1);
            }
            if (i2 == -1) {
                i2 = Settings.getIntSettings(Settings.KEY_REC_QUALITY, 1);
            }
            audioFormat.setRecordingQuality(i2);
            this.mMediaRecorder.setOutputFormat(audioFormat.getOutputFormat());
            this.mMediaRecorder.setAudioEncoder(audioFormat.getAudioEncoder());
            this.mMediaRecorder.setMaxFileSize(audioFormat.getMaxFileSize(str));
            this.mMediaRecorder.setAudioEncodingBitRate(audioFormat.getAudioEncodingBitrate());
            this.mMediaRecorder.setAudioSamplingRate(audioFormat.getAudioSamplingRate());
//            this.mMediaRecorder.semSetDurationInterval(35);
            this.mSimpleMetadata.setRecQuality(i2);
            try {
                this.mMediaRecorder.prepare();
                return 0;
            } catch (IOException e) {
                Log.m25e(TAG, "IOException", e, this.mSession);
                return -114;
            }
        } catch (RuntimeException e2) {
            Log.m25e(TAG, "SecMediaRecorder RuntimeException !", e2, this.mSession);
            return -114;
        }
    }

    public int startRecord(String str, AudioFormat audioFormat) {
        String str2 = TAG;
        Log.m27i(str2, "startRecord - mRecorderState : " + this.mRecorderState + " mCurrentTime : " + this.mCurrentTime, this.mSession);
        if (this.mRecorderState == 2) {
            Log.m33w(TAG, "startRecord - it is already recording state", this.mSession);
            return -108;
        }
        this.mAudioFormat = audioFormat;
        this.mRecordStartTime = this.mCurrentTime;
        this.mRecordEndTime = 0;
        this.mRecordCorrectionTime = 0;
        this.mNoSoundCount = 0;
        if (this.mAudioFormat == null) {
            this.mAudioFormat = new AudioFormat(this.mRecordMode);
        }
        String str3 = TAG;
        Log.m30v(str3, "    startRecord - mRecordStartTime : " + this.mRecordStartTime + " mRecordEndTime : " + this.mRecordEndTime, this.mSession);
        int prepareRecord = prepareRecord(str, this.mAudioFormat);
        if (prepareRecord != 0) {
            return prepareRecord;
        }
        this.mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            public final void onInfo(MediaRecorder mediaRecorder, int i, int i2) {
                SimpleRecorder.this.lambda$startRecord$0$SimpleRecorder(mediaRecorder, i, i2);
            }
        });
        try {
            disableSystemSound();
            this.mMediaRecorder.start();
            if (this.mRecordMode == 4) {
                VoiceWorkerForP.getInstance().startSTT(this.mAppContext);
            }
            this.mTelephonyManager = (TelephonyManager) this.mAppContext.getSystemService("phone");
            this.mTelephonyManager.listen(this.mPhoneStateListener, 32);
            SurveyLogProvider.startRecordingLog();
            SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_RECORD, this.mRecordMode);
            setRecorderState(2);
            this.mNoSoundCheckTime = System.currentTimeMillis();
            return prepareRecord;
        } catch (IllegalStateException e) {
            Log.m25e(TAG, "startRecord failed due to illegalStateException", e, this.mSession);
            return -114;
        } catch (RuntimeException e2) {
            Log.m25e(TAG, "startRecord failed due to RuntimeException", e2, this.mSession);
            return -114;
        }
    }

    public /* synthetic */ void lambda$startRecord$0$SimpleRecorder(MediaRecorder mediaRecorder, int i, int i2) {
        onRecInfo(i, i2);
    }

    public boolean pauseRecord() {
        String str = TAG;
        Log.m27i(str, "pauseRecord - mRecorderState : " + this.mRecorderState, this.mSession);
        if (this.mMediaRecorder == null) {
            Log.m23e(TAG, "pauseRecord MediaRecorder is null !!!", this.mSession);
            return false;
        } else if (this.mRecorderState != 2 || this.mRecordEndTime <= 0) {
            return false;
        } else {
            String str2 = TAG;
            Log.m30v(str2, "    pauseRecord - mRecordStartTime : " + this.mRecordStartTime + " mRecordEndTime : " + this.mRecordEndTime + " mCurrentTime : " + this.mCurrentTime, this.mSession);
            try {
                this.mMediaRecorder.pause();
            } catch (RuntimeException e) {
                Log.m25e(TAG, "pauseRecord failed", e, this.mSession);
            }
            if (this.mRecordMode == 4) {
                VoiceWorkerForP.getInstance().pauseSTT();
            }
            setRecorderState(3);
            enableSystemSound();
            return true;
        }
    }

    public int resumeRecord(String str) {
        String str2 = TAG;
        Log.m27i(str2, "resumeRecord - mRecorderState : " + this.mRecorderState, this.mSession);
        int i = this.mRecorderState;
        if (i != 3 && i != 4 && i != 1) {
            return -110;
        }
        String str3 = TAG;
        Log.m30v(str3, "    resumeRecord - mRecordStartTime : " + this.mRecordStartTime + " mRecordEndTime : " + this.mRecordEndTime + " mCurrentTime : " + this.mCurrentTime, this.mSession);
        int i2 = 0;
        int i3 = this.mRecorderState;
        if (i3 == 3) {
            if (!requestAudioFocus()) {
                return -109;
            }
            if (!isSystemSoundDisabled()) {
                disableSystemSound();
            }
            MediaRecorder mediaRecorder = this.mMediaRecorder;
            if (mediaRecorder != null) {
                try {
                    mediaRecorder.resume();
                } catch (RuntimeException e) {
                    Log.m25e(TAG, "resumeRecord failed", e, this.mSession);
                }
                if (this.mRecordMode == 4) {
                    VoiceWorkerForP.getInstance().resumeSTT();
                }
            }
        } else if (i3 == 4 || i3 == 1) {
            i2 = startRecord(str, this.mAudioFormat);
        }
        if (i2 == 0) {
            setRecorderState(2);
        }
        this.mNoSoundCheckTime = System.currentTimeMillis();
        return i2;
    }

    public int resumeRecord(String str, int i) {
        String str2 = TAG;
        Log.m27i(str2, "resumeRecord - path : " + str, this.mSession);
        setCurrentTime(i);
        return resumeRecord(str);
    }

    private boolean stopRecordInternal() {
        String str = TAG;
        Log.m27i(str, "stopRecordInternal - mRecorderState : " + this.mRecorderState, this.mSession);
        if (this.mMediaRecorder == null) {
            Log.m23e(TAG, "stopRecord MediaRecorder is null !!!", this.mSession);
            return false;
        }
        int i = this.mRecorderState;
        if (i != 2 && i != 3) {
            return false;
        }
        String str2 = TAG;
        Log.m30v(str2, "    stopRecord - mRecordStartTime : " + this.mRecordStartTime + " mRecordEndTime : " + this.mRecordEndTime + " mCurrentTime : " + this.mCurrentTime, this.mSession);
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
        return true;
    }

    public int getDuration() {
        return this.mRecordEndTime - this.mRecordStartTime;
    }

    public int getRecordEndTime() {
        return this.mRecordEndTime;
    }

    public boolean isSaveEnable() {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("isSaveEnabled - ");
        sb.append(this.mRecordEndTime - this.mRecordStartTime > 1000);
        Log.m26i(str, sb.toString());
        if (this.mRecordEndTime - this.mRecordStartTime > 1000) {
            return true;
        }
        return false;
    }

    public boolean cancelRecord() {
        AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener;
        String str = TAG;
        Log.m26i(str, "cancelRecord - mRecorderState : " + this.mRecorderState);
        int i = this.mRecorderState;
        if (i != 2 && i != 3 && i != 4) {
            return false;
        }
        String str2 = TAG;
        Log.m29v(str2, "    cancelRecord - mRecordStartTime : " + this.mRecordStartTime + " mRecordEndTime : " + this.mRecordEndTime + " mCurrentTime : " + this.mCurrentTime);
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
        }
        if (this.mRecordMode == 4) {
            VoiceWorkerForP.getInstance().cancelSTT();
        }
        AudioManager audioManager = this.mAudioManager;
        if (!(audioManager == null || (onAudioFocusChangeListener = this.mAudioFocusListener) == null)) {
            audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        }
        setRecorderState(1);
        enableSystemSound();
        return true;
    }

    public void setCurrentTime(int i) {
        if (i < 0) {
            i = 0;
        }
        this.mCurrentTime = i;
    }

    public void stopSTT() {
        int recordMode = this.mSimpleMetadata.getRecordMode();
        if (recordMode == 0) {
            recordMode = Settings.getIntSettings(Settings.KEY_SIMPLE_RECORD_MODE, 1);
        }
        if (recordMode == 4) {
            VoiceWorkerForP.getInstance().stopSTT();
        }
    }

    public boolean saveFile(ContentItem contentItem) {
        AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener;
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
            String str = TAG;
            Log.m23e(str, "exist : " + file.exists() + " size : " + file.length() + " path : " + file.getPath(), this.mSession);
            cancelRecord();
            return false;
        }
        AudioManager audioManager = this.mAudioManager;
        if (!(audioManager == null || (onAudioFocusChangeListener = this.mAudioFocusListener) == null)) {
            audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        }
        contentItem.setStartTime(this.mRecordStartTime);
        contentItem.setEndTime(this.mRecordEndTime);
        contentItem.setDuration(this.mRecordEndTime - this.mRecordStartTime);
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mediaMetadataRetriever.setDataSource(contentItem.getPath());
            contentItem.setDuration(Integer.parseInt(mediaMetadataRetriever.extractMetadata(9)));
        } catch (Exception e) {
            Log.m25e(TAG, "METADATA_KEY_DURATION parsing error", e, this.mSession);
        } catch (Throwable th) {
            mediaMetadataRetriever.release();
            throw th;
        }
        mediaMetadataRetriever.release();
        contentItem.setEndTime(contentItem.getStartTime() + contentItem.getDuration());
        String str2 = TAG;
        Log.m27i(str2, "saveFile start:" + contentItem.getStartTime() + " end:" + contentItem.getEndTime() + " duration:" + contentItem.getDuration(), this.mSession);
        return true;
    }

    public void initPhoneStateListener() {
        Log.m27i(TAG, "initPhoneStateListener", this.mSession);
        TelephonyManager telephonyManager = this.mTelephonyManager;
        if (telephonyManager != null) {
            telephonyManager.listen(this.mPhoneStateListener, 0);
        }
    }

    public void setAudioFormat(AudioFormat audioFormat) {
        this.mAudioFormat = audioFormat;
    }

    private void setRecorderState(int i) {
        String str = TAG;
        Log.m27i(str, "setRecorderState - state : " + i, this.mSession);
        notifyObservers(1010, i, -1);
        if (i == 1) {
            this.mRecordStartTime = 0;
            this.mRecordEndTime = 0;
            this.mRecordCorrectionTime = 0;
            setCurrentTime(0);
            if (Engine.getInstance().getPlayerState() == 1) {
                this.mSimpleMetadata.close();
            }
        } else if (i != 2) {
            if (i != 3 && i == 4) {
                this.mSimpleMetadata.stopAmplitude(this.mRecordStartTime, this.mRecordEndTime);
            }
        } else if (this.mRecorderState != 3) {
            this.mSimpleMetadata.initAmplitude();
        }
        this.mRecorderState = i;
    }

    public int getRecorderState() {
        return this.mRecorderState;
    }

    public int getRecordMode() {
        return this.mRecordMode;
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
            String str = TAG;
            Log.m27i(str, "registerListener : " + onRecorderListener.getClass().getSimpleName(), this.mSession);
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
            String str = TAG;
            Log.m27i(str, "unregisterListener : " + onRecorderListener.getClass().getSimpleName(), this.mSession);
        }
        if (onRecorderListener != null && containsListener(onRecorderListener)) {
            removeListener(onRecorderListener);
        }
    }

    private void unregisterAllListener() {
        Log.m27i(TAG, "unregisterAllListener", this.mSession);
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

    /* access modifiers changed from: protected */
    public void disableSystemSound() {
        Log.m27i(TAG, "disableSystemSound", this.mSession);
        try {
            StatusBarProvider.getInstance().disable(this.mAppContext, 262144);
            setStreamMute(true);
            this.mAudioManager.adjustStreamVolume(2, -100, 0);
//            this.mVibrateWhileRingingState = Settings.System.getInt(this.mAppContext.getContentResolver(), "vibrate_when_ringing", 1);
            if (this.mAudioManager.getRingerMode() == 2 && this.mVibrateWhileRingingState == 0) {
//                Settings.System.putInt(this.mAppContext.getContentResolver(), "vibrate_when_ringing", 1);
            }
        } catch (SecurityException e) {
            String str = TAG;
            Log.m33w(str, "disableSystemSound : " + e, this.mSession);
        }
    }

    /* access modifiers changed from: protected */
    public void enableSystemSound() {
        if (this.mAppContext != null) {
            Log.m27i(TAG, "enableSystemSound", this.mSession);
            try {
                StatusBarProvider.getInstance().disable(this.mAppContext, 0);
                setStreamMute(false);
                if (getRecorderState() != 3 && this.mAudioManager.getRingerMode() == 2) {
                    this.mAudioManager.adjustStreamVolume(2, 100, 0);
//                    Settings.System.putInt(this.mAppContext.getContentResolver(), "vibrate_when_ringing", this.mVibrateWhileRingingState);
                }
            } catch (SecurityException e) {
                String str = TAG;
                Log.m33w(str, "enableSystemSound : " + e, this.mSession);
            }
        }
    }

    public void setStreamMute(boolean z) {
        if (z) {
            this.mMuteHandler.removeMessages(1);
            this.mMuteHandler.sendEmptyMessageDelayed(1, 20);
            return;
        }
        this.mMuteHandler.removeMessages(0);
        this.mMuteHandler.sendEmptyMessageDelayed(0, 20);
    }

    private static class VolumeHandler extends Handler {
        WeakReference<SimpleRecorder> mWeakRefRecorder;

        VolumeHandler(SimpleRecorder simpleRecorder) {
            this.mWeakRefRecorder = new WeakReference<>(simpleRecorder);
        }

        public void handleMessage(Message message) {
            SimpleRecorder simpleRecorder = (SimpleRecorder) this.mWeakRefRecorder.get();
            if (simpleRecorder != null) {
                String access$000 = SimpleRecorder.TAG;
                Log.m19d(access$000, "mMuteHandler what : " + message.what + " arg1 : " + message.arg1 + " arg2 : " + message.arg2);
                if (simpleRecorder.mAudioManager == null) {
                    Log.m26i(SimpleRecorder.TAG, "mMuteHandler mAudioManager is null");
                    return;
                }
                int streamVolume = simpleRecorder.mAudioManager.getStreamVolume(1);
                int streamVolume2 = simpleRecorder.mAudioManager.getStreamVolume(5);
                String access$0002 = SimpleRecorder.TAG;
                Log.m19d(access$0002, "mMuteHandler - Current STREAM_SYSTEM : " + streamVolume + " STREAM_NOTIFICATION : " + streamVolume2);
                int i = message.what;
                if (i == 0) {
                    if (com.sec.android.app.voicenote.provider.Settings.getBooleanSettings(com.sec.android.app.voicenote.provider.Settings.KEY_ENABLE_SYS_SOUND, false)) {
                        int intSettings = com.sec.android.app.voicenote.provider.Settings.getIntSettings(com.sec.android.app.voicenote.provider.Settings.KEY_VOLUME_STREAM_SYSTEM, 8);
                        String access$0003 = SimpleRecorder.TAG;
                        Log.m26i(access$0003, "mMuteHandler - STREAM_SYSTEM Backup Volume : " + intSettings);
                        simpleRecorder.mAudioManager.setStreamVolume(1, intSettings, 0);
                        com.sec.android.app.voicenote.provider.Settings.setSettings(com.sec.android.app.voicenote.provider.Settings.KEY_ENABLE_SYS_SOUND, false);
                    }
                    if (com.sec.android.app.voicenote.provider.Settings.getBooleanSettings(com.sec.android.app.voicenote.provider.Settings.KEY_ENABLE_NOTI_SOUND, false)) {
                        int intSettings2 = com.sec.android.app.voicenote.provider.Settings.getIntSettings(com.sec.android.app.voicenote.provider.Settings.KEY_VOLUME_STREAM_NOTIFICATION, 11);
                        String access$0004 = SimpleRecorder.TAG;
                        Log.m26i(access$0004, "mMuteHandler - STREAM_NOTIFICATION Backup Volume : " + intSettings2);
                        simpleRecorder.mAudioManager.setStreamVolume(5, intSettings2, 0);
                        com.sec.android.app.voicenote.provider.Settings.setSettings(com.sec.android.app.voicenote.provider.Settings.KEY_ENABLE_NOTI_SOUND, false);
                    }
                    if (simpleRecorder.mMuteHandler == null && simpleRecorder.mMediaRecorder == null) {
                        AudioManager unused = simpleRecorder.mAudioManager = null;
                    }
                } else if (i == 1) {
                    if (streamVolume > 0) {
                        Log.m26i(SimpleRecorder.TAG, "mMuteHandler - STREAM_SYSTEM Mute");
                        com.sec.android.app.voicenote.provider.Settings.setSettings(com.sec.android.app.voicenote.provider.Settings.KEY_VOLUME_STREAM_SYSTEM, streamVolume);
                        simpleRecorder.mAudioManager.setStreamVolume(1, 0, 0);
                        com.sec.android.app.voicenote.provider.Settings.setSettings(com.sec.android.app.voicenote.provider.Settings.KEY_ENABLE_SYS_SOUND, true);
                    }
                    if (streamVolume2 > 0) {
                        Log.m26i(SimpleRecorder.TAG, "mMuteHandler - STREAM_NOTIFICATION Mute");
                        com.sec.android.app.voicenote.provider.Settings.setSettings(com.sec.android.app.voicenote.provider.Settings.KEY_VOLUME_STREAM_NOTIFICATION, streamVolume2);
                        simpleRecorder.mAudioManager.setStreamVolume(5, 0, 0);
                        com.sec.android.app.voicenote.provider.Settings.setSettings(com.sec.android.app.voicenote.provider.Settings.KEY_ENABLE_NOTI_SOUND, true);
                    }
                }
            }
        }
    }

    private boolean requestAudioFocus() {
        Log.m27i(TAG, "requestAudioFocus()", this.mSession);
        int i = 0;
        for (int i2 = 0; i2 < 10 && (i = this.mAudioManager.requestAudioFocus(this.mAudioFocusListener, 3, 1)) != 1; i2++) {
            SystemClock.sleep(50);
        }
        if (i != 0) {
            return true;
        }
        Log.m23e(TAG, "requestAudioFocus is failed", this.mSession);
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean isRecordActive() {
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
                Log.m25e(TAG, "InterruptedException !", e, this.mSession);
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

    /* access modifiers changed from: private */
    public void endCall() {
        String str = TAG;
        Log.m27i(str, "endCall - mRejectCall : " + CallRejectChecker.getInstance().getReject(), this.mSession);
        try {
            Method declaredMethod = Class.forName(this.mTelephonyManager.getClass().getName()).getDeclaredMethod("getITelephony", new Class[0]);
            declaredMethod.setAccessible(true);
            ((ITelephony) declaredMethod.invoke(this.mTelephonyManager, new Object[0])).endCall();
            CallRejectChecker.getInstance().increaseRejectCallCount();
        } catch (Exception e) {
            Log.m25e(TAG, "endCall fail !!!", e, this.mSession);
        }
    }

    public void checkNoSound(int i) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.mNoSoundCheckTime >= MAX_NO_SOUND_TIME) {
            if (i == 2) {
                if (this.mLeftSpeechTime.isMute(MAX_NO_SOUND_TIME) && this.mRightSpeechTime.isMute(MAX_NO_SOUND_TIME)) {
                    this.mNoSoundCount++;
                    this.mNoSoundCount %= 3;
                    if (this.mNoSoundCount == 0) {
                        notifyObservers(INFO_NO_SOUND_DETECT_VIBRATE, -1, -1);
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
                    notifyObservers(INFO_NO_SOUND_DETECT_VIBRATE, -1, -1);
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
        if (!(this.mTelephonyManager == null || this.mPhoneStateListener == null)) {
            this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
        }
        this.mAppContext = null;
        this.mMediaRecorder = null;
        if (this.mMuteHandler == null || !this.mMuteHandler.hasMessages(0)) {
            this.mAudioManager = null;
        }
        this.mMuteHandler = null;
    }

    private boolean isSystemSoundDisabled() {
        AudioManager audioManager = this.mAudioManager;
        if (audioManager == null) {
            return false;
        }
        int streamVolume = audioManager.getStreamVolume(1);
        int streamVolume2 = this.mAudioManager.getStreamVolume(5);
        if (streamVolume == 0 && streamVolume2 == 0) {
            return true;
        }
        return false;
    }
}
