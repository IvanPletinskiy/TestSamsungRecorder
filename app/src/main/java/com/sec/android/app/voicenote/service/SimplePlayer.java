package com.sec.android.app.voicenote.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import com.sec.android.app.voicenote.common.util.SpeechTimeDataTreeSet;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.provider.DBProvider;
import com.sec.android.app.voicenote.provider.DNDModeProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.service.SkipSilenceTask;
import com.sec.android.app.voicenote.service.VoiceNoteService;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SimplePlayer implements MediaPlayer.OnCompletionListener, SkipSilenceTask.OnSkipSilenceTaskListener, SensorEventListener, MediaPlayer.OnErrorListener {
    private static final int AUDIO_FOCUS_RETRY_INTERVAL = 50;
    private static final int DURATION_INTERVAL = 35;
    private static final int FADE_DOWN = 4010;
    private static final int FADE_UP = 4011;
    static final int INFO_AUDIOFOCUS_GAIN = 1015;
    static final int INFO_AUDIOFOCUS_LOSS = 1016;
    public static final int INFO_DURATION_PROGRESS = 2012;
    public static final int INFO_PLAYER_STATE = 2010;
    public static final int INFO_PLAY_CALL_STATE = 2018;
    public static final int INFO_PLAY_COMPLETE = 2011;
    public static final int INFO_PLAY_PAUSE_BY_LOSS = 2017;
    public static final int INFO_PLAY_PAUSE_BY_TRIM = 2016;
    public static final int INFO_PLAY_REPEAT = 2013;
    public static final int INFO_PLAY_SPEED = 2015;
    public static final int INFO_SKIP_SILENCE = 2014;
    private static final int MAX_AUDIO_FOCUS_RETRY_CNT = 10;
    private static final int MIN_REPEAT_INTERVAL = 1000;
    private static final int MSG_SKIP_SILENCE = 2;
    private static final int RECOVER = 4012;
    private static final int STATE_REPEAT_NOT_SET = -1;
    private static final int STREAM_VOICENOTE = 13;
    /* access modifiers changed from: private */
    public static String TAG = "SimplePlayer";
    private static SimplePlayer mInstance;
    private final Object SYN_OBJECT = new Object();
    /* access modifiers changed from: private */
    public Context mAppContext;
    private final AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int i) {
            String access$200 = SimplePlayer.TAG;
            Log.m27i(access$200, "onAudioFocusChange - focusChange : " + i, SimplePlayer.this.mSession);
            if (i != -3) {
                if (i != -2) {
                    if (i == -1) {
                        SimplePlayer.this.notifyObservers(2017, -1, -1);
                        if (SimplePlayer.this.mState == 3) {
                            SimplePlayer.this.pausePlay();
                            if (VoiceNoteService.Helper.connectionCount() != 0) {
                                SimplePlayer.this.notifyObservers(1016, -1, -1);
                            }
                        } else if (SimplePlayer.this.mState == 4) {
                            boolean unused = SimplePlayer.this.mPausedByCall = false;
                            boolean unused2 = SimplePlayer.this.mIsPausedForaWhile = false;
                        }
                    } else if (i == 1) {
                        if (SimplePlayer.this.mState == 4 && (SimplePlayer.this.mPausedByCall || SimplePlayer.this.mIsPausedForaWhile)) {
                            boolean unused3 = SimplePlayer.this.mPausedByCall = false;
                            SimplePlayer.this.resumePlay();
                            if (VoiceNoteService.Helper.connectionCount() != 0) {
                                SimplePlayer.this.notifyObservers(1015, -1, -1);
                            }
                        } else if (SimplePlayer.this.mIsFadeDown) {
                            boolean unused4 = SimplePlayer.this.mIsFadeDown = false;
                            if (SimplePlayer.this.mState == 3) {
                                SimplePlayer.this.mSoundHandler.removeMessages(SimplePlayer.FADE_DOWN);
                                SimplePlayer.this.mSoundHandler.sendEmptyMessage(SimplePlayer.FADE_UP);
                            } else if (SimplePlayer.this.mState == 4) {
                                SimplePlayer.this.setVolume(1.0f, 1.0f);
                            }
                        }
                        boolean unused5 = SimplePlayer.this.mIsPausedForaWhile = false;
                    }
                } else if (SimplePlayer.this.mState == 3) {
                    boolean unused6 = SimplePlayer.this.mPausedByCall = true;
                    boolean unused7 = SimplePlayer.this.mIsPausedForaWhile = true;
                    SimplePlayer.this.pausePlay();
                    int connectionCount = VoiceNoteService.Helper.connectionCount();
                }
            } else if (SimplePlayer.this.mState == 3) {
                Log.m20d(SimplePlayer.TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK : fade down until volume 20%", SimplePlayer.this.mSession);
                boolean unused8 = SimplePlayer.this.mIsFadeDown = true;
                SimplePlayer.this.mSoundHandler.removeMessages(SimplePlayer.FADE_UP);
                SimplePlayer.this.mSoundHandler.sendEmptyMessage(SimplePlayer.FADE_DOWN);
            }
        }
    };
    private AudioManager mAudioManager = null;
    private Handler mHandler = new PlayerHandler(this);
    private long mID = -1;
    private boolean mIsDetectedNearSensor = false;
    /* access modifiers changed from: private */
    public boolean mIsFadeDown = false;
    /* access modifiers changed from: private */
    public boolean mIsPausedForaWhile = false;
    private boolean mLeftMute = false;
    private final ArrayList<WeakReference<OnPlayerListener>> mListeners = new ArrayList<>();
    /* access modifiers changed from: private */
    public MediaPlayer mMediaPlayer = null;
    private int mMediaPlayerState = 1;
    private String mPath = null;
    /* access modifiers changed from: private */
    public boolean mPausedByCall = false;
    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        public void onCallStateChanged(int i, String str) {
            String access$200 = SimplePlayer.TAG;
            Log.m27i(access$200, "mPhoneStateListener : " + i, SimplePlayer.this.mSession);
            SimplePlayer.this.notifyObservers(2018, i, -1);
            if (i == 0) {
                if (SimplePlayer.this.getPlayerState() == 4 && ((SimplePlayer.this.mPausedByCall || SimplePlayer.this.mIsPausedForaWhile) && SimplePlayer.this.requestAudioFocus())) {
                    Log.m27i(SimplePlayer.TAG, "request audio focus success", SimplePlayer.this.mSession);
                    if (SimplePlayer.this.mState == 4 && (SimplePlayer.this.mPausedByCall || SimplePlayer.this.mIsPausedForaWhile)) {
                        boolean unused = SimplePlayer.this.mPausedByCall = false;
                        SimplePlayer.this.resumePlay();
                        if (VoiceNoteService.Helper.connectionCount() != 0) {
                            SimplePlayer.this.notifyObservers(1015, -1, -1);
                        }
                    } else if (SimplePlayer.this.mIsFadeDown) {
                        boolean unused2 = SimplePlayer.this.mIsFadeDown = false;
                        if (SimplePlayer.this.mState == 3) {
                            SimplePlayer.this.mSoundHandler.removeMessages(SimplePlayer.FADE_DOWN);
                            SimplePlayer.this.mSoundHandler.sendEmptyMessage(SimplePlayer.FADE_UP);
                        } else if (SimplePlayer.this.mState == 4) {
                            SimplePlayer.this.setVolume(1.0f, 1.0f);
                        }
                    }
                    boolean unused3 = SimplePlayer.this.mIsPausedForaWhile = false;
                }
                if (!SimplePlayer.this.isPlaySpeedActivated() && !SimplePlayer.this.isRepeatActivated() && !SimplePlayer.this.isSkipSilenceActivated()) {
                    new Handler() {
                        public void handleMessage(Message message) {
                            super.handleMessage(message);
                            SimplePlayer.this.enableSkipSilenceMode(4);
                        }
                    }.sendEmptyMessageDelayed(0, 300);
                }
            } else if (i != 1 && i != 2) {
            } else {
                if (DNDModeProvider.getDoNotDisturb(SimplePlayer.this.mAppContext)) {
                    Log.m27i(SimplePlayer.TAG, "Ringing or Off hook but DnD enabled", SimplePlayer.this.mSession);
                } else if (SimplePlayer.this.mState == 3) {
                    boolean unused4 = SimplePlayer.this.mPausedByCall = true;
                    boolean unused5 = SimplePlayer.this.mIsPausedForaWhile = true;
                    SimplePlayer.this.pausePlay();
                    if (VoiceNoteService.Helper.connectionCount() != 0) {
                        SimplePlayer.this.notifyObservers(1016, -1, -1);
                    }
                }
            }
        }
    };
    private Timer mPlayerTimer = null;
    /* access modifiers changed from: private */
    public int mRepeatMode = 2;
    /* access modifiers changed from: private */
    public int[] mRepeatPosition = {-1, -1};
    private boolean mRightMute = false;
    /* access modifiers changed from: private */
    public String mSession;
    SimpleMetadataRepository mSimpleMetadata;
    private int mSkipSilenceMode = 4;
    /* access modifiers changed from: private */
    public SkipSilenceTask mSkipSilenceTask = null;
    /* access modifiers changed from: private */
    public final SoundHandler mSoundHandler = new SoundHandler(this);
    private float mSpeed = 1.0f;
    /* access modifiers changed from: private */
    public int mState = 1;
    private TelephonyManager mTelephonyManager = null;

    public interface OnPlayerListener {
        void onPlayerUpdate(int i, int i2, int i3);
    }

    public static class PlaySpeed {
        public static final float DEFAULT_SPEED = 1.0f;
        public static final float DISABLE_SPEED = -1.0f;
        public static final float INTERVAL = 0.1f;
        public static final float MAX_SPEED = 2.0f;
        public static final float MIN_SPEED = 0.5f;
        public static final int MULTIPLIER = 10;
    }

    public static class PlayerState {
        public static final int ERROR = 5;
        public static final int IDLE = 1;
        public static final int PAUSED = 4;
        public static final int PLAYING = 3;
        public static final int PREPARED = 2;
    }

    public static class RepeatMode {
        public static final int DISABLE = 1;
        public static final int SET_ALL = 4;
        public static final int SET_FRONT = 3;
        public static final int UNSET = 2;
    }

    public static class SkipSilenceMode {
        public static final int DISABLE = 1;
        public static final int INTERVIEW = 2;
        public static final int SET = 3;
        public static final int UNSET = 4;
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private static class MediaPlayerState {
        public static final int ERROR = 5;
        public static final int IDLE = 1;
        public static final int PAUSED = 4;
        public static final int PREPARED = 2;
        public static final int STARTED = 3;

        private MediaPlayerState() {
        }
    }

    private static class SpeakerPhoneMode {
        public static final boolean FORCE_EARPIECE = true;
        public static final boolean FORCE_NONE = false;

        private SpeakerPhoneMode() {
        }
    }

    public SimplePlayer(Context context, String str) {
        this.mAppContext = context;
        this.mSession = str;
        this.mSimpleMetadata = SimpleMetadataRepositoryManager.getInstance().getMetadataRepository(this.mSession);
    }

    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.m27i(TAG, "onCompletion", this.mSession);
        Timer timer = this.mPlayerTimer;
        if (timer != null) {
            timer.cancel();
            this.mPlayerTimer = null;
        }
        int i = 0;
        if (this.mRepeatMode == 4) {
            int[] iArr = this.mRepeatPosition;
            if (iArr[0] < iArr[1]) {
                seekTo(iArr[0]);
            } else {
                seekTo(iArr[1]);
            }
            if (getPlayerState() == 3) {
                resumePlay();
                return;
            }
            return;
        }
        setPlayerState(4);
        setMediaPlayerState(4);
        notifyObservers(2011, 0, -1);
        try {
            i = mediaPlayer.getDuration();
        } catch (Exception e) {
            String str = TAG;
            Log.m23e(str, "getDuration exception : " + e, this.mSession);
        }
        seekTo(i);
        AudioManager audioManager = this.mAudioManager;
        if (audioManager != null) {
            audioManager.abandonAudioFocus(this.mAudioFocusListener);
        }
    }

    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        String str = TAG;
        Log.m23e(str, "onError occur stopPlay  - what : " + i + " extra : " + i2, this.mSession);
        notifyObservers(2010, 5, i2);
        return stopPlay(false);
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        String str = TAG;
        Log.m23e(str, "onSensorChanged - event : " + sensorEvent, this.mSession);
        if (sensorEvent.values[0] == 0.0f && sensorEvent.sensor.getType() == 8) {
            this.mIsDetectedNearSensor = true;
        }
    }

    private boolean preparePlay(String str, int i) {
        MediaPlayer mediaPlayer;
        Log.m30v(TAG, "preparePlay", this.mSession);
        this.mMediaPlayer = new MediaPlayer();
        Context context = this.mAppContext;
        if (context == null) {
            Log.m22e(TAG, "mAppContext is null");
            return false;
        }
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
        SensorManager sensorManager = (SensorManager) this.mAppContext.getSystemService("sensor");
        this.mTelephonyManager = (TelephonyManager) this.mAppContext.getSystemService("phone");
        this.mTelephonyManager.listen(this.mPhoneStateListener, 32);
        if (requestAudioFocus() && (mediaPlayer = this.mMediaPlayer) != null) {
            try {
                mediaPlayer.setAudioStreamType(3);
                if ((Engine.getInstance().isWiredHeadSetConnected() || Engine.getInstance().isBluetoothHeadSetConnected() || this.mAudioManager.isBluetoothA2dpOn()) && i == 2) {
                    setMonoMode(true);
                }
                try {
                    this.mMediaPlayer.setDataSource(str);
                    if (this.mSkipSilenceMode == 3) {
                        String str2 = TAG;
                        Log.m27i(str2, "preparePlay - mSkipSilenceMode : " + this.mSkipSilenceMode, this.mSession);
                        try {
//                            this.mMediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(1).semAddAudioTag("STREAM_VOICENOTE").build());
                        } catch (IllegalArgumentException e) {
                            Log.m24e(TAG, "IllegalArgumentException occur ", (Throwable) e);
                        } catch (Exception e2) {
                            Log.m24e(TAG, "UnKnownException occur ", (Throwable) e2);
                        }
                    } else {
                        this.mMediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(1).build());
                    }
                    this.mMediaPlayer.prepare();
                    setMediaPlayerState(2);
                    return true;
                } catch (IOException e3) {
                    Log.m24e(TAG, "IOException", (Throwable) e3);
                    setMediaPlayerState(5);
                    return false;
                } catch (IllegalStateException e4) {
                    Log.m24e(TAG, "IllegalStateException", (Throwable) e4);
                    setMediaPlayerState(5);
                    return false;
                } catch (RuntimeException e5) {
                    Log.m24e(TAG, "RuntimeException", (Throwable) e5);
                    setMediaPlayerState(5);
                    return false;
                }
            } catch (Exception e6) {
                Log.m22e(TAG, e6.toString());
            }
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00e2, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean initPlay(java.lang.String r9, long r10, boolean r12) {
        /*
            r8 = this;
            monitor-enter(r8)
            java.lang.String r0 = TAG     // Catch:{ all -> 0x00ec }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ec }
            r1.<init>()     // Catch:{ all -> 0x00ec }
            java.lang.String r2 = "initPlay - id : "
            r1.append(r2)     // Catch:{ all -> 0x00ec }
            r1.append(r10)     // Catch:{ all -> 0x00ec }
            java.lang.String r2 = " path : "
            r1.append(r2)     // Catch:{ all -> 0x00ec }
            r1.append(r9)     // Catch:{ all -> 0x00ec }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x00ec }
            java.lang.String r2 = r8.mSession     // Catch:{ all -> 0x00ec }
            com.sec.android.app.voicenote.provider.Log.m20d((java.lang.String) r0, (java.lang.String) r1, (java.lang.String) r2)     // Catch:{ all -> 0x00ec }
            int r0 = r8.mState     // Catch:{ all -> 0x00ec }
            r1 = 4
            r2 = 0
            r3 = 3
            if (r0 == r3) goto L_0x002c
            int r0 = r8.mState     // Catch:{ all -> 0x00ec }
            if (r0 != r1) goto L_0x005c
        L_0x002c:
            java.util.Timer r0 = r8.mPlayerTimer     // Catch:{ all -> 0x00ec }
            if (r0 == 0) goto L_0x0037
            java.util.Timer r0 = r8.mPlayerTimer     // Catch:{ all -> 0x00ec }
            r0.cancel()     // Catch:{ all -> 0x00ec }
            r8.mPlayerTimer = r2     // Catch:{ all -> 0x00ec }
        L_0x0037:
            com.sec.android.app.voicenote.service.SimpleMetadataRepository r0 = r8.mSimpleMetadata     // Catch:{ all -> 0x00ec }
            java.lang.String r0 = r0.getPath()     // Catch:{ all -> 0x00ec }
            if (r0 == 0) goto L_0x0054
            com.sec.android.app.voicenote.service.SimpleMetadataRepository r0 = r8.mSimpleMetadata     // Catch:{ all -> 0x00ec }
            java.lang.String r0 = r0.getPath()     // Catch:{ all -> 0x00ec }
            java.lang.String r4 = r8.mPath     // Catch:{ all -> 0x00ec }
            boolean r0 = r0.equals(r4)     // Catch:{ all -> 0x00ec }
            if (r0 == 0) goto L_0x0054
            com.sec.android.app.voicenote.service.SimpleMetadataRepository r0 = r8.mSimpleMetadata     // Catch:{ all -> 0x00ec }
            java.lang.String r4 = r8.mPath     // Catch:{ all -> 0x00ec }
            r0.write(r4)     // Catch:{ all -> 0x00ec }
        L_0x0054:
            com.sec.android.app.voicenote.service.SimpleMetadataRepository r0 = r8.mSimpleMetadata     // Catch:{ all -> 0x00ec }
            r0.initialize()     // Catch:{ all -> 0x00ec }
            r8.stopPlayInternal()     // Catch:{ all -> 0x00ec }
        L_0x005c:
            android.os.Handler r0 = r8.mHandler     // Catch:{ all -> 0x00ec }
            r4 = 2
            r0.removeMessages(r4)     // Catch:{ all -> 0x00ec }
            com.sec.android.app.voicenote.service.SimpleMetadataRepository r0 = r8.mSimpleMetadata     // Catch:{ all -> 0x00ec }
            r0.initialize()     // Catch:{ all -> 0x00ec }
            com.sec.android.app.voicenote.service.SimpleMetadataRepository r0 = r8.mSimpleMetadata     // Catch:{ all -> 0x00ec }
            r0.read(r9)     // Catch:{ all -> 0x00ec }
            java.lang.String r0 = "simple_play_mode"
            com.sec.android.app.voicenote.service.SimpleMetadataRepository r5 = r8.mSimpleMetadata     // Catch:{ all -> 0x00ec }
            int r5 = r5.getRecordMode()     // Catch:{ all -> 0x00ec }
            com.sec.android.app.voicenote.provider.Settings.setSettings((java.lang.String) r0, (int) r5)     // Catch:{ all -> 0x00ec }
            com.sec.android.app.voicenote.service.SkipSilenceTask r0 = r8.mSkipSilenceTask     // Catch:{ all -> 0x00ec }
            if (r0 == 0) goto L_0x0082
            com.sec.android.app.voicenote.service.SkipSilenceTask r0 = r8.mSkipSilenceTask     // Catch:{ all -> 0x00ec }
            r0.setSkipSilenceTaskListener(r2)     // Catch:{ all -> 0x00ec }
            r8.mSkipSilenceTask = r2     // Catch:{ all -> 0x00ec }
        L_0x0082:
            com.sec.android.app.voicenote.service.SimpleMetadataRepository r0 = r8.mSimpleMetadata     // Catch:{ all -> 0x00ec }
            int r0 = r0.getRecordMode()     // Catch:{ all -> 0x00ec }
            r2 = 1065353216(0x3f800000, float:1.0)
            if (r0 != r4) goto L_0x00a8
            com.sec.android.app.voicenote.service.SimpleMetadataRepository r0 = r8.mSimpleMetadata     // Catch:{ all -> 0x00ec }
            r5 = 0
            boolean r0 = r0.isEnabledPerson(r5)     // Catch:{ all -> 0x00ec }
            com.sec.android.app.voicenote.service.SimpleMetadataRepository r6 = r8.mSimpleMetadata     // Catch:{ all -> 0x00ec }
            r7 = 1127481344(0x43340000, float:180.0)
            boolean r6 = r6.isEnabledPerson(r7)     // Catch:{ all -> 0x00ec }
            if (r0 == 0) goto L_0x009f
            r0 = r2
            goto L_0x00a0
        L_0x009f:
            r0 = r5
        L_0x00a0:
            if (r6 == 0) goto L_0x00a3
            goto L_0x00a4
        L_0x00a3:
            r2 = r5
        L_0x00a4:
            r8.setVolume(r0, r2)     // Catch:{ all -> 0x00ec }
            goto L_0x00ab
        L_0x00a8:
            r8.setVolume(r2, r2)     // Catch:{ all -> 0x00ec }
        L_0x00ab:
            com.sec.android.app.voicenote.service.SimpleMetadataRepository r0 = r8.mSimpleMetadata     // Catch:{ all -> 0x00ec }
            int r0 = r0.getRecordMode()     // Catch:{ all -> 0x00ec }
            boolean r0 = r8.preparePlay(r9, r0)     // Catch:{ all -> 0x00ec }
            r2 = 1
            if (r0 == 0) goto L_0x00e3
            android.media.MediaPlayer r0 = r8.mMediaPlayer     // Catch:{ all -> 0x00ec }
            r0.setOnCompletionListener(r8)     // Catch:{ all -> 0x00ec }
            android.media.MediaPlayer r0 = r8.mMediaPlayer     // Catch:{ all -> 0x00ec }
            r0.setOnErrorListener(r8)     // Catch:{ all -> 0x00ec }
            if (r12 == 0) goto L_0x00d4
            int r0 = r8.mMediaPlayerState     // Catch:{ all -> 0x00ec }
            if (r0 == r4) goto L_0x00cc
            int r0 = r8.mMediaPlayerState     // Catch:{ all -> 0x00ec }
            if (r0 != r1) goto L_0x00d4
        L_0x00cc:
            android.media.MediaPlayer r0 = r8.mMediaPlayer     // Catch:{ all -> 0x00ec }
            r0.start()     // Catch:{ all -> 0x00ec }
            r8.setMediaPlayerState(r3)     // Catch:{ all -> 0x00ec }
        L_0x00d4:
            r8.mPath = r9     // Catch:{ all -> 0x00ec }
            r8.mID = r10     // Catch:{ all -> 0x00ec }
            if (r12 == 0) goto L_0x00de
            r8.setPlayerState(r3)     // Catch:{ all -> 0x00ec }
            goto L_0x00e1
        L_0x00de:
            r8.setPlayerState(r4)     // Catch:{ all -> 0x00ec }
        L_0x00e1:
            monitor-exit(r8)
            return r2
        L_0x00e3:
            r8.stopPlayInternal()     // Catch:{ all -> 0x00ec }
            r8.setPlayerState(r2)     // Catch:{ all -> 0x00ec }
            r9 = 0
            monitor-exit(r8)
            return r9
        L_0x00ec:
            r9 = move-exception
            monitor-exit(r8)
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.SimplePlayer.initPlay(java.lang.String, long, boolean):boolean");
    }

    private synchronized void reStartPlay(long j, int i, int i2) {
        String pathById = DBProvider.getInstance().getPathById(j);
        Log.m27i(TAG, "reStartPlay - id : " + j + " path : " + pathById, this.mSession);
        if (!(j == -1 || pathById == null || pathById.isEmpty())) {
            if (this.mState != 1) {
                if (this.mState == 3 || this.mState == 4) {
                    if (this.mSimpleMetadata.getPath() != null && this.mSimpleMetadata.getPath().equals(this.mPath)) {
                        this.mSimpleMetadata.write(this.mPath);
                    }
                    this.mSimpleMetadata.initialize();
                    stopPlayInternal();
                }
                this.mSimpleMetadata.initialize();
                this.mSimpleMetadata.read(pathById);
                this.mSimpleMetadata.setRecordMode(DBProvider.getInstance().getRecordModeByPath(pathById));
                Settings.setSettings(Settings.KEY_SIMPLE_PLAY_MODE, this.mSimpleMetadata.getRecordMode());
                if (this.mSimpleMetadata.getRecordMode() == 2) {
                    float f = 0.0f;
                    boolean isEnabledPerson = this.mSimpleMetadata.isEnabledPerson(0.0f);
                    float f2 = this.mSimpleMetadata.isEnabledPerson(180.0f) ? 1.0f : 0.0f;
                    if (isEnabledPerson) {
                        f = 1.0f;
                    }
                    setVolume(f2, f);
                } else {
                    setVolume(1.0f, 1.0f);
                }
                if (preparePlay(pathById, this.mSimpleMetadata.getRecordMode())) {
                    this.mMediaPlayer.setOnCompletionListener(this);
                    if (i == 3) {
                        this.mMediaPlayer.start();
                        setMediaPlayerState(3);
                        setPlayerState(3);
                    } else if (i == 4) {
                        this.mMediaPlayer.start();
                        setMediaPlayerState(3);
                        this.mMediaPlayer.pause();
                        setMediaPlayerState(4);
                    }
                    seekTo(i2);
                    this.mPath = pathById;
                    this.mID = j;
                    return;
                }
                stopPlayInternal();
                setPlayerState(1);
                return;
            }
        }
        Log.m27i(TAG, "reStartPlay : invalid id or path or player already stopped.", this.mSession);
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(4:23|24|25|26) */
    /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
        com.sec.android.app.voicenote.provider.Log.m27i(TAG, "pausePlay IllegalStateException !!", r5.mSession);
        setMediaPlayerState(5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0074, code lost:
        return false;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:23:0x0066 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean pausePlay() {
        /*
            r5 = this;
            monitor-enter(r5)
            java.lang.String r0 = TAG     // Catch:{ all -> 0x0075 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0075 }
            r1.<init>()     // Catch:{ all -> 0x0075 }
            java.lang.String r2 = "pausePlay - State : "
            r1.append(r2)     // Catch:{ all -> 0x0075 }
            int r2 = r5.mState     // Catch:{ all -> 0x0075 }
            r1.append(r2)     // Catch:{ all -> 0x0075 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0075 }
            java.lang.String r2 = r5.mSession     // Catch:{ all -> 0x0075 }
            com.sec.android.app.voicenote.provider.Log.m27i((java.lang.String) r0, (java.lang.String) r1, (java.lang.String) r2)     // Catch:{ all -> 0x0075 }
            android.media.MediaPlayer r0 = r5.mMediaPlayer     // Catch:{ all -> 0x0075 }
            r1 = 0
            if (r0 != 0) goto L_0x002b
            java.lang.String r0 = TAG     // Catch:{ all -> 0x0075 }
            java.lang.String r2 = "pausePlay MediaPlayer is null !!!"
            java.lang.String r3 = r5.mSession     // Catch:{ all -> 0x0075 }
            com.sec.android.app.voicenote.provider.Log.m23e((java.lang.String) r0, (java.lang.String) r2, (java.lang.String) r3)     // Catch:{ all -> 0x0075 }
            monitor-exit(r5)
            return r1
        L_0x002b:
            int r0 = r5.mState     // Catch:{ all -> 0x0075 }
            r2 = 2
            if (r0 != r2) goto L_0x003e
            r5.setPlayerState(r2)     // Catch:{ all -> 0x0075 }
            java.lang.String r0 = TAG     // Catch:{ all -> 0x0075 }
            java.lang.String r2 = "pausePlay PlayerState.PREPARED return !!!"
            java.lang.String r3 = r5.mSession     // Catch:{ all -> 0x0075 }
            com.sec.android.app.voicenote.provider.Log.m23e((java.lang.String) r0, (java.lang.String) r2, (java.lang.String) r3)     // Catch:{ all -> 0x0075 }
            monitor-exit(r5)
            return r1
        L_0x003e:
            boolean r0 = r5.mPausedByCall     // Catch:{ IllegalStateException -> 0x0066 }
            if (r0 != 0) goto L_0x004d
            boolean r0 = r5.mIsPausedForaWhile     // Catch:{ IllegalStateException -> 0x0066 }
            if (r0 != 0) goto L_0x004d
            android.media.AudioManager r0 = r5.mAudioManager     // Catch:{ IllegalStateException -> 0x0066 }
            android.media.AudioManager$OnAudioFocusChangeListener r2 = r5.mAudioFocusListener     // Catch:{ IllegalStateException -> 0x0066 }
            r0.abandonAudioFocus(r2)     // Catch:{ IllegalStateException -> 0x0066 }
        L_0x004d:
            android.media.MediaPlayer r0 = r5.mMediaPlayer     // Catch:{ IllegalStateException -> 0x0066 }
            r0.pause()     // Catch:{ IllegalStateException -> 0x0066 }
            r0 = 4
            r5.setMediaPlayerState(r0)     // Catch:{ IllegalStateException -> 0x0066 }
            r2 = 2012(0x7dc, float:2.82E-42)
            int r3 = r5.getPosition()     // Catch:{ IllegalStateException -> 0x0066 }
            r4 = -1
            r5.notifyObservers(r2, r3, r4)     // Catch:{ IllegalStateException -> 0x0066 }
            r5.setPlayerState(r0)     // Catch:{ IllegalStateException -> 0x0066 }
            r0 = 1
            monitor-exit(r5)
            return r0
        L_0x0066:
            java.lang.String r0 = TAG     // Catch:{ all -> 0x0075 }
            java.lang.String r2 = "pausePlay IllegalStateException !!"
            java.lang.String r3 = r5.mSession     // Catch:{ all -> 0x0075 }
            com.sec.android.app.voicenote.provider.Log.m27i((java.lang.String) r0, (java.lang.String) r2, (java.lang.String) r3)     // Catch:{ all -> 0x0075 }
            r0 = 5
            r5.setMediaPlayerState(r0)     // Catch:{ all -> 0x0075 }
            monitor-exit(r5)
            return r1
        L_0x0075:
            r0 = move-exception
            monitor-exit(r5)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.SimplePlayer.pausePlay():boolean");
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(4:27|28|29|30) */
    /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
        com.sec.android.app.voicenote.provider.Log.m27i(TAG, "resumePlay IllegalStateException !!", r5.mSession);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0064, code lost:
        return -115;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:27:0x005a */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int resumePlay() {
        /*
            r5 = this;
            monitor-enter(r5)
            java.lang.String r0 = TAG     // Catch:{ all -> 0x0065 }
            java.lang.String r1 = "resumePlay"
            java.lang.String r2 = r5.mSession     // Catch:{ all -> 0x0065 }
            com.sec.android.app.voicenote.provider.Log.m27i((java.lang.String) r0, (java.lang.String) r1, (java.lang.String) r2)     // Catch:{ all -> 0x0065 }
            android.media.MediaPlayer r0 = r5.mMediaPlayer     // Catch:{ all -> 0x0065 }
            r1 = -115(0xffffffffffffff8d, float:NaN)
            if (r0 != 0) goto L_0x001b
            java.lang.String r0 = TAG     // Catch:{ all -> 0x0065 }
            java.lang.String r2 = "resumePlay MediaPlayer is null !!!"
            java.lang.String r3 = r5.mSession     // Catch:{ all -> 0x0065 }
            com.sec.android.app.voicenote.provider.Log.m23e((java.lang.String) r0, (java.lang.String) r2, (java.lang.String) r3)     // Catch:{ all -> 0x0065 }
            monitor-exit(r5)
            return r1
        L_0x001b:
            boolean r0 = r5.requestAudioFocus()     // Catch:{ all -> 0x0065 }
            if (r0 != 0) goto L_0x0025
            r0 = -109(0xffffffffffffff93, float:NaN)
            monitor-exit(r5)
            return r0
        L_0x0025:
            int r0 = r5.mRepeatMode     // Catch:{ all -> 0x0065 }
            r2 = 4
            r3 = 0
            if (r0 != r2) goto L_0x0048
            int[] r0 = r5.mRepeatPosition     // Catch:{ all -> 0x0065 }
            r0 = r0[r3]     // Catch:{ all -> 0x0065 }
            int[] r2 = r5.mRepeatPosition     // Catch:{ all -> 0x0065 }
            r4 = 1
            r2 = r2[r4]     // Catch:{ all -> 0x0065 }
            if (r0 >= r2) goto L_0x003b
            int[] r0 = r5.mRepeatPosition     // Catch:{ all -> 0x0065 }
            r0 = r0[r3]     // Catch:{ all -> 0x0065 }
            goto L_0x003f
        L_0x003b:
            int[] r0 = r5.mRepeatPosition     // Catch:{ all -> 0x0065 }
            r0 = r0[r4]     // Catch:{ all -> 0x0065 }
        L_0x003f:
            int r2 = r5.getPosition()     // Catch:{ all -> 0x0065 }
            if (r2 >= r0) goto L_0x0048
            r5.seekTo(r0)     // Catch:{ all -> 0x0065 }
        L_0x0048:
            android.media.MediaPlayer r0 = r5.mMediaPlayer     // Catch:{ IllegalStateException -> 0x005a }
            r0.start()     // Catch:{ IllegalStateException -> 0x005a }
            r0 = 3
            r5.setMediaPlayerState(r0)     // Catch:{ IllegalStateException -> 0x005a }
            r5.setPlayerState(r0)     // Catch:{ IllegalStateException -> 0x005a }
            r5.mPausedByCall = r3     // Catch:{ IllegalStateException -> 0x005a }
            r5.mIsPausedForaWhile = r3     // Catch:{ IllegalStateException -> 0x005a }
            monitor-exit(r5)
            return r3
        L_0x005a:
            java.lang.String r0 = TAG     // Catch:{ all -> 0x0065 }
            java.lang.String r2 = "resumePlay IllegalStateException !!"
            java.lang.String r3 = r5.mSession     // Catch:{ all -> 0x0065 }
            com.sec.android.app.voicenote.provider.Log.m27i((java.lang.String) r0, (java.lang.String) r2, (java.lang.String) r3)     // Catch:{ all -> 0x0065 }
            monitor-exit(r5)
            return r1
        L_0x0065:
            r0 = move-exception
            monitor-exit(r5)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.SimplePlayer.resumePlay():int");
    }

    public boolean stopPlay() {
        return stopPlay(true);
    }

    public synchronized boolean initPlay() {
        String str = TAG;
        Log.m27i(str, "initPlay - PlayerState : " + this.mState, this.mSession);
        if (this.mState == 1) {
            return false;
        }
        setRepeatMode(2, -1);
        setPlaySpeed(1.0f);
        enableSkipSilenceMode(4);
        this.mSimpleMetadata.write(this.mPath);
        this.mLeftMute = false;
        this.mRightMute = false;
        return true;
    }

    public synchronized boolean stopPlay(boolean z) {
        String str = TAG;
        Log.m27i(str, "stopPlay - updateMetadata : " + z + " PlayerState : " + this.mState, this.mSession);
        if (this.mState == 1) {
            return false;
        }
        if (this.mTelephonyManager != null) {
            this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
        }
        setPlayerState(1);
        setRepeatMode(2, -1);
        setPlaySpeed(1.0f);
        enableSkipSilenceMode(4);
        this.mAudioManager.abandonAudioFocus(this.mAudioFocusListener);
        if (z) {
            this.mSimpleMetadata.write(this.mPath);
        }
        this.mPath = null;
        this.mID = -1;
        setVolume(1.0f, 1.0f);
        this.mLeftMute = false;
        this.mRightMute = false;
        stopPlayInternal();
        return true;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(2:13|14) */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002b, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        com.sec.android.app.voicenote.provider.Log.m23e(TAG, "stopPlayInternal IllegalStateException", r5.mSession);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        r5.mMediaPlayer = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0039, code lost:
        throw r2;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x002d */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void stopPlayInternal() {
        /*
            r5 = this;
            java.lang.String r0 = TAG
            java.lang.String r1 = r5.mSession
            java.lang.String r2 = "stopPlayInternal E"
            com.sec.android.app.voicenote.provider.Log.m33w((java.lang.String) r0, (java.lang.String) r2, (java.lang.String) r1)
            java.util.Timer r0 = r5.mPlayerTimer
            r1 = 0
            if (r0 == 0) goto L_0x0013
            r0.cancel()
            r5.mPlayerTimer = r1
        L_0x0013:
            java.lang.Object r0 = r5.SYN_OBJECT
            monitor-enter(r0)
            android.media.MediaPlayer r2 = r5.mMediaPlayer     // Catch:{ all -> 0x0049 }
            if (r2 == 0) goto L_0x003a
            android.media.MediaPlayer r2 = r5.mMediaPlayer     // Catch:{ IllegalStateException -> 0x002d }
            r2.stop()     // Catch:{ IllegalStateException -> 0x002d }
            android.media.MediaPlayer r2 = r5.mMediaPlayer     // Catch:{ IllegalStateException -> 0x002d }
            r2.release()     // Catch:{ IllegalStateException -> 0x002d }
            r2 = 1
            r5.setMediaPlayerState(r2)     // Catch:{ IllegalStateException -> 0x002d }
        L_0x0028:
            r5.mMediaPlayer = r1     // Catch:{ all -> 0x0049 }
            goto L_0x003a
        L_0x002b:
            r2 = move-exception
            goto L_0x0037
        L_0x002d:
            java.lang.String r2 = TAG     // Catch:{ all -> 0x002b }
            java.lang.String r3 = "stopPlayInternal IllegalStateException"
            java.lang.String r4 = r5.mSession     // Catch:{ all -> 0x002b }
            com.sec.android.app.voicenote.provider.Log.m23e((java.lang.String) r2, (java.lang.String) r3, (java.lang.String) r4)     // Catch:{ all -> 0x002b }
            goto L_0x0028
        L_0x0037:
            r5.mMediaPlayer = r1     // Catch:{ all -> 0x0049 }
            throw r2     // Catch:{ all -> 0x0049 }
        L_0x003a:
            monitor-exit(r0)     // Catch:{ all -> 0x0049 }
            r0 = 0
            r5.setMonoMode(r0)
            java.lang.String r0 = TAG
            java.lang.String r1 = r5.mSession
            java.lang.String r2 = "stopPlayInternal X"
            com.sec.android.app.voicenote.provider.Log.m33w((java.lang.String) r0, (java.lang.String) r2, (java.lang.String) r1)
            return
        L_0x0049:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0049 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.SimplePlayer.stopPlayInternal():void");
    }

    private boolean seek(int i) {
        synchronized (this.SYN_OBJECT) {
            if (this.mMediaPlayer == null) {
                return false;
            }
            this.mMediaPlayer.seekTo(i);
            return true;
        }
    }

    private boolean containsListener(OnPlayerListener onPlayerListener) {
        if (onPlayerListener == null) {
            return false;
        }
        Iterator<WeakReference<OnPlayerListener>> it = this.mListeners.iterator();
        while (it.hasNext()) {
            if (((OnPlayerListener) it.next().get()).equals(onPlayerListener)) {
                return true;
            }
        }
        return false;
    }

    private void removeListener(OnPlayerListener onPlayerListener) {
        if (onPlayerListener != null) {
            for (int size = this.mListeners.size() - 1; size >= 0; size--) {
                WeakReference weakReference = this.mListeners.get(size);
                if (weakReference.get() == null || ((OnPlayerListener) weakReference.get()).equals(onPlayerListener)) {
                    this.mListeners.remove(weakReference);
                }
            }
        }
    }

    public final void registerListener(OnPlayerListener onPlayerListener) {
        if (onPlayerListener != null && !containsListener(onPlayerListener)) {
            this.mListeners.add(new WeakReference(onPlayerListener));
            int i = this.mState;
            if (i != 1 && this.mMediaPlayer != null) {
                onPlayerListener.onPlayerUpdate(2010, i, -1);
                onPlayerListener.onPlayerUpdate(2012, getPosition(), -1);
            }
        }
    }

    public final void unregisterListener(OnPlayerListener onPlayerListener) {
        if (onPlayerListener != null && containsListener(onPlayerListener)) {
            removeListener(onPlayerListener);
        }
    }

    private void unregisterAllListener() {
        this.mListeners.clear();
    }

    private void setMediaPlayerState(int i) {
        String str = TAG;
        Log.m30v(str, "setMediaPlayerState state : " + i, this.mSession);
        this.mMediaPlayerState = i;
    }

    private void setPlayerState(int i) {
        String str = TAG;
        Log.m27i(str, "setPlayerState - state : " + i, this.mSession);
        this.mState = i;
        notifyObservers(2010, this.mState, -1);
        if (i != 3) {
            Timer timer = this.mPlayerTimer;
            if (timer != null) {
                timer.cancel();
                this.mPlayerTimer = null;
                return;
            }
            return;
        }
        if (this.mPlayerTimer == null) {
            this.mPlayerTimer = new Timer();
        }
        this.mPlayerTimer.schedule(new TimerTask() {
            public void run() {
                if (SimplePlayer.this.mMediaPlayer == null || SimplePlayer.this.mState != 3) {
                    cancel();
                    return;
                }
                try {
                    int position = SimplePlayer.this.getPosition();
                    if (SimplePlayer.this.mRepeatMode == 4) {
                        if (SimplePlayer.this.mRepeatPosition[0] < SimplePlayer.this.mRepeatPosition[1]) {
                            if (SimplePlayer.this.mRepeatPosition[1] < position || SimplePlayer.this.mRepeatPosition[0] > position + 20) {
                                String access$200 = SimplePlayer.TAG;
                                Log.m26i(access$200, "Repeat currentPosition A-B : " + position + " repeatTime : " + SimplePlayer.this.mRepeatPosition[0] + '~' + SimplePlayer.this.mRepeatPosition[1]);
                                SimplePlayer simplePlayer = SimplePlayer.this;
                                simplePlayer.seekTo(simplePlayer.mRepeatPosition[0]);
                                return;
                            }
                        } else if (SimplePlayer.this.mRepeatPosition[0] < position || SimplePlayer.this.mRepeatPosition[1] > position + 20) {
                            String access$2002 = SimplePlayer.TAG;
                            Log.m26i(access$2002, "Repeat currentPosition B-A : " + position + " repeatTime : " + SimplePlayer.this.mRepeatPosition[0] + '~' + SimplePlayer.this.mRepeatPosition[1]);
                            SimplePlayer simplePlayer2 = SimplePlayer.this;
                            simplePlayer2.seekTo(simplePlayer2.mRepeatPosition[1]);
                            return;
                        }
                    }
                    if (SimplePlayer.this.mSkipSilenceTask != null && !SimplePlayer.this.mSkipSilenceTask.checkMuteSection(position)) {
                        position = SimplePlayer.this.getDuration();
                    }
                    SimplePlayer.this.notifyObservers(2012, position, -1);
                } catch (Exception e) {
                    Log.m23e(SimplePlayer.TAG, e.getMessage(), SimplePlayer.this.mSession);
                }
            }
        }, 0, 35);
    }

    public int getPlayerState() {
        return this.mState;
    }

    /* access modifiers changed from: private */
    public void notifyObservers(int i, int i2, int i3) {
        Iterator<WeakReference<OnPlayerListener>> it = this.mListeners.iterator();
        while (it.hasNext()) {
            WeakReference next = it.next();
            if (next.get() == null) {
                this.mListeners.remove(next);
            } else {
                ((OnPlayerListener) next.get()).onPlayerUpdate(i, i2, i3);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x005e  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0066  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int setRepeatMode(int r6, int r7) {
        /*
            r5 = this;
            java.lang.String r0 = TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "setRepeatMode - state : "
            r1.append(r2)
            r1.append(r6)
            java.lang.String r2 = " position : "
            r1.append(r2)
            r1.append(r7)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = r5.mSession
            com.sec.android.app.voicenote.provider.Log.m27i((java.lang.String) r0, (java.lang.String) r1, (java.lang.String) r2)
            int r0 = r5.mState
            r1 = 2
            r2 = 1
            if (r0 != r2) goto L_0x0027
            r6 = r1
        L_0x0027:
            r5.mRepeatMode = r6
            int r6 = r5.mRepeatMode
            r0 = -1
            r3 = 0
            if (r6 == r2) goto L_0x0055
            if (r6 == r1) goto L_0x0055
            r1 = 3
            if (r6 == r1) goto L_0x004c
            r1 = 4
            if (r6 == r1) goto L_0x0038
            goto L_0x005c
        L_0x0038:
            int[] r6 = r5.mRepeatPosition
            r1 = r6[r3]
            int r1 = r7 - r1
            r4 = 1000(0x3e8, float:1.401E-42)
            if (r1 > r4) goto L_0x0047
            r6 = r6[r3]
            int r6 = r6 - r7
            if (r6 <= r4) goto L_0x005c
        L_0x0047:
            int[] r6 = r5.mRepeatPosition
            r6[r2] = r7
            goto L_0x005b
        L_0x004c:
            if (r7 < 0) goto L_0x005c
            int[] r6 = r5.mRepeatPosition
            r6[r3] = r7
            r6[r2] = r0
            goto L_0x005b
        L_0x0055:
            int[] r6 = r5.mRepeatPosition
            r6[r3] = r0
            r6[r2] = r0
        L_0x005b:
            r3 = r2
        L_0x005c:
            if (r3 == 0) goto L_0x0066
            r6 = 2013(0x7dd, float:2.821E-42)
            int r7 = r5.mRepeatMode
            r5.notifyObservers(r6, r7, r0)
            goto L_0x006b
        L_0x0066:
            int r6 = r5.mRepeatMode
            int r6 = r6 - r2
            r5.mRepeatMode = r6
        L_0x006b:
            int r6 = r5.mRepeatMode
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.SimplePlayer.setRepeatMode(int, int):int");
    }

    public int setRepeatMode(int i) {
        if (this.mMediaPlayer != null) {
            return setRepeatMode(i, getPosition());
        }
        Log.m23e(TAG, "setRepeatMode mMediaPlayer is null !!", this.mSession);
        this.mRepeatMode = 2;
        return this.mRepeatMode;
    }

    public void setRepeatTime(int i, int i2) {
        String str = TAG;
        Log.m27i(str, "setRepeatTime - from : " + i + " to : " + i2, this.mSession);
        if (i >= 0) {
            this.mRepeatPosition[0] = i;
        }
        if (i2 >= 0) {
            this.mRepeatPosition[1] = i2;
        }
    }

    public int getRepeatMode() {
        return this.mRepeatMode;
    }

    public int[] getRepeatPosition() {
        return this.mRepeatPosition;
    }

    public float setPlaySpeed(float f) {
        if (this.mMediaPlayer == null) {
            Log.m23e(TAG, "setPlaySpeed MediaPlayer is null !!!", this.mSession);
            this.mSpeed = 1.0f;
            return this.mSpeed;
        }
        if (this.mState == 1) {
            this.mSpeed = 1.0f;
        }
        if (f == -1.0f) {
            this.mSpeed = f;
            f = 1.0f;
        } else if (f < 0.5f) {
            this.mSpeed = 0.5f;
            f = 0.5f;
        } else if (f > 2.0f) {
            this.mSpeed = 2.0f;
            f = 2.0f;
        } else {
            this.mSpeed = f;
        }
        try {
            this.mMediaPlayer.setPlaybackParams(this.mMediaPlayer.getPlaybackParams().setSpeed(f));
            if (this.mMediaPlayerState == 2 || this.mMediaPlayerState == 4) {
                this.mMediaPlayer.pause();
            }
        } catch (Exception e) {
            Log.m22e(TAG, "setPlaySpeed Exception : " + e);
        }
        notifyObservers(2015, (int) (this.mSpeed * 10.0f), 10);
        return this.mSpeed;
    }

    public float getPlaySpeed() {
        return this.mSpeed;
    }

    public int getSkipSilenceMode() {
        String str = TAG;
        Log.m27i(str, "getSkipSilenceMode - mode : " + this.mSkipSilenceMode, this.mSession);
        return this.mSkipSilenceMode;
    }

    private static class PlayerHandler extends Handler {
        WeakReference<SimplePlayer> mWeakRefPlayer;

        PlayerHandler(SimplePlayer simplePlayer) {
            this.mWeakRefPlayer = new WeakReference<>(simplePlayer);
        }

        public void handleMessage(Message message) {
            SimplePlayer simplePlayer = (SimplePlayer) this.mWeakRefPlayer.get();
            if (simplePlayer != null && message.what == 2) {
                simplePlayer.switchSkipSilenceMode(true, message.getData());
            }
        }
    }

    private static class SoundHandler extends Handler {
        float mCurrentVolumeL = 1.0f;
        float mCurrentVolumeR = 1.0f;
        WeakReference<SimplePlayer> mPlayer;

        SoundHandler(SimplePlayer simplePlayer) {
            this.mPlayer = new WeakReference<>(simplePlayer);
        }

        private void setCurrentVolume(float f, float f2) {
            SimplePlayer simplePlayer = (SimplePlayer) this.mPlayer.get();
            if (simplePlayer != null) {
                this.mCurrentVolumeL = f;
                this.mCurrentVolumeR = f2;
                simplePlayer.setVolume(this.mCurrentVolumeL, this.mCurrentVolumeR);
            }
        }

        public void handleMessage(Message message) {
            SimplePlayer simplePlayer = (SimplePlayer) this.mPlayer.get();
            if (simplePlayer != null) {
                switch (message.what) {
                    case SimplePlayer.FADE_DOWN /*4010*/:
                        this.mCurrentVolumeL -= 0.05f;
                        this.mCurrentVolumeR -= 0.05f;
                        if (this.mCurrentVolumeL <= 0.2f || this.mCurrentVolumeR <= 0.2f) {
                            this.mCurrentVolumeL = 0.2f;
                            this.mCurrentVolumeR = 0.2f;
                        } else {
                            removeMessages(SimplePlayer.FADE_DOWN);
                            sendEmptyMessageDelayed(SimplePlayer.FADE_DOWN, 10);
                        }
                        simplePlayer.setVolume(this.mCurrentVolumeL, this.mCurrentVolumeR);
                        return;
                    case SimplePlayer.FADE_UP /*4011*/:
                        this.mCurrentVolumeL += 0.01f;
                        this.mCurrentVolumeR += 0.01f;
                        if (this.mCurrentVolumeL >= 1.0f || this.mCurrentVolumeR >= 1.0f) {
                            this.mCurrentVolumeL = 1.0f;
                            this.mCurrentVolumeR = 1.0f;
                        } else {
                            removeMessages(SimplePlayer.FADE_UP);
                            sendEmptyMessageDelayed(SimplePlayer.FADE_UP, 10);
                        }
                        simplePlayer.setVolume(this.mCurrentVolumeL, this.mCurrentVolumeR);
                        return;
                    case SimplePlayer.RECOVER /*4012*/:
                        this.mCurrentVolumeL = (float) message.arg1;
                        this.mCurrentVolumeR = (float) message.arg2;
                        simplePlayer.setVolume(this.mCurrentVolumeL, this.mCurrentVolumeR);
                        return;
                    default:
                        return;
                }
            }
        }
    }

    public String getPath() {
        String str = this.mPath;
        return str == null ? "" : str;
    }

    public void renamePath(String str) {
        String str2 = TAG;
        Log.m20d(str2, "renamePath : " + str, this.mSession);
        this.mPath = str;
    }

    public long getID() {
        return this.mID;
    }

    public void setMute(boolean z, boolean z2) {
        if (this.mMediaPlayer != null) {
            try {
                Log.m30v(TAG, "setMute - left : " + z + " right : " + z2, this.mSession);
                this.mLeftMute = z;
                this.mRightMute = z2;
                MediaPlayer mediaPlayer = this.mMediaPlayer;
                float f = 0.0f;
                float f2 = this.mLeftMute ? 0.0f : 1.0f;
                if (!this.mRightMute) {
                    f = 1.0f;
                }
                mediaPlayer.setVolume(f2, f);
            } catch (IllegalStateException e) {
                Log.m24e(TAG, "IllegalStateException", (Throwable) e);
            }
        }
    }

    public boolean isRepeatActivated() {
        int i = this.mRepeatMode;
        return i == 4 || i == 3;
    }

    public boolean isPlaySpeedActivated() {
        float f = this.mSpeed;
        return (f == 1.0f || f == -1.0f) ? false : true;
    }

    public boolean isSkipSilenceActivated() {
        int i = this.mSkipSilenceMode;
        return i == 3 || i == 2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0078, code lost:
        if (r9 != 4) goto L_0x00c3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int enableSkipSilenceMode(int r9) {
        /*
            r8 = this;
            java.lang.String r0 = TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "enableSkipSilenceMode - current mode : "
            r1.append(r2)
            int r2 = r8.mSkipSilenceMode
            r1.append(r2)
            java.lang.String r2 = " new mode : "
            r1.append(r2)
            r1.append(r9)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = r8.mSession
            com.sec.android.app.voicenote.provider.Log.m27i((java.lang.String) r0, (java.lang.String) r1, (java.lang.String) r2)
            int r0 = r8.mState
            r1 = -1
            r2 = 2014(0x7de, float:2.822E-42)
            r3 = 4
            r4 = 1
            if (r0 != r4) goto L_0x003a
            java.lang.String r9 = TAG
            java.lang.String r0 = "enableSkipSilenceMode - state is idle"
            com.sec.android.app.voicenote.provider.Log.m32w((java.lang.String) r9, (java.lang.String) r0)
            r8.mSkipSilenceMode = r3
            int r9 = r8.mSkipSilenceMode
            r8.notifyObservers(r2, r9, r1)
            return r3
        L_0x003a:
            int r0 = r8.mSkipSilenceMode
            if (r0 != r9) goto L_0x0048
            java.lang.String r0 = TAG
            java.lang.String r1 = r8.mSession
            java.lang.String r2 = "enableSkipSilenceMode - mode is not changed"
            com.sec.android.app.voicenote.provider.Log.m33w((java.lang.String) r0, (java.lang.String) r2, (java.lang.String) r1)
            return r9
        L_0x0048:
            if (r9 == r4) goto L_0x006e
            boolean r0 = r8.isRepeatActivated()
            if (r0 != 0) goto L_0x0062
            boolean r0 = r8.isPlaySpeedActivated()
            if (r0 != 0) goto L_0x0062
            com.sec.android.app.voicenote.provider.PhoneStateProvider r0 = com.sec.android.app.voicenote.provider.PhoneStateProvider.getInstance()
            android.content.Context r5 = r8.mAppContext
            boolean r0 = r0.isCallIdle(r5)
            if (r0 != 0) goto L_0x006e
        L_0x0062:
            java.lang.String r9 = TAG
            java.lang.String r0 = r8.mSession
            java.lang.String r1 = "enableSkipSilenceMode - repeat or playSpeed or mPausedByCall is activated"
            com.sec.android.app.voicenote.provider.Log.m30v((java.lang.String) r9, (java.lang.String) r1, (java.lang.String) r0)
            int r9 = r8.mSkipSilenceMode
            return r9
        L_0x006e:
            r0 = 0
            r5 = 3
            r6 = 2
            r7 = 0
            if (r9 == r4) goto L_0x00b6
            if (r9 == r6) goto L_0x007f
            if (r9 == r5) goto L_0x007b
            if (r9 == r3) goto L_0x00b6
            goto L_0x00c3
        L_0x007b:
            r8.switchSkipSilenceMode(r0, r7)
            goto L_0x00c3
        L_0x007f:
            java.lang.String r0 = TAG
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "enableSkipSilenceMode - top : "
            r3.append(r4)
            com.sec.android.app.voicenote.service.SimpleMetadataRepository r4 = r8.mSimpleMetadata
            r5 = 1127481344(0x43340000, float:180.0)
            boolean r4 = r4.isEnabledPerson(r5)
            r3.append(r4)
            java.lang.String r4 = " bottom : "
            r3.append(r4)
            com.sec.android.app.voicenote.service.SimpleMetadataRepository r4 = r8.mSimpleMetadata
            r5 = 0
            boolean r4 = r4.isEnabledPerson(r5)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            com.sec.android.app.voicenote.provider.Log.m29v(r0, r3)
            com.sec.android.app.voicenote.service.SimpleMetadataRepository r0 = r8.mSimpleMetadata
            com.sec.android.app.voicenote.common.util.SpeechTimeDataTreeSet r0 = r0.getPlaySection()
            r8.switchSkipSilenceMode(r0)
            goto L_0x00c3
        L_0x00b6:
            int r3 = r8.mSkipSilenceMode
            if (r3 != r6) goto L_0x00be
            r8.switchSkipSilenceMode(r7)
            goto L_0x00c3
        L_0x00be:
            if (r3 != r5) goto L_0x00c3
            r8.switchSkipSilenceMode(r0, r7)
        L_0x00c3:
            r8.mSkipSilenceMode = r9
            int r9 = r8.mSkipSilenceMode
            r8.notifyObservers(r2, r9, r1)
            int r9 = r8.mSkipSilenceMode
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.SimplePlayer.enableSkipSilenceMode(int):int");
    }

    public void switchSkipSilenceMode(SpeechTimeDataTreeSet speechTimeDataTreeSet) {
        if (this.mMediaPlayer == null) {
            Log.m23e(TAG, "switchSkipSilenceMode MediaPlayer is null !!!", this.mSession);
        } else if (speechTimeDataTreeSet == null) {
            Log.m26i(TAG, "enableSkipSilenceMode - section is null");
            SkipSilenceTask skipSilenceTask = this.mSkipSilenceTask;
            if (skipSilenceTask != null) {
                skipSilenceTask.setSkipSilenceTaskListener((SkipSilenceTask.OnSkipSilenceTaskListener) null);
                this.mSkipSilenceTask = null;
            }
        } else {
            Log.m26i(TAG, "enableSkipSilenceMode - section is not null");
            SkipSilenceTask skipSilenceTask2 = this.mSkipSilenceTask;
            if (skipSilenceTask2 == null) {
                this.mSkipSilenceTask = new SkipSilenceTask(speechTimeDataTreeSet);
                this.mSkipSilenceTask.refreshUpcomingSectionsList(getPosition());
                this.mSkipSilenceTask.setSkipSilenceTaskListener(this);
                Log.m26i(TAG, "ready to run SkipSilenceTask");
                return;
            }
            skipSilenceTask2.refreshUpcomingSectionsList(getPosition());
        }
    }

    public void switchSkipSilenceMode(boolean z, Bundle bundle) {
        String str = TAG;
        Log.m27i(str, "switchSkipSilenceMode isPrepared : " + z, this.mSession);
        if (!z) {
            if (this.mMediaPlayer == null) {
                Log.m22e(TAG, "MediaPlayer is null !!");
                return;
            }
            Message message = new Message();
            message.what = 2;
            Bundle bundle2 = new Bundle();
            bundle2.putLong(DialogFactory.BUNDLE_ID, this.mID);
            bundle2.putInt("currentPos", getPosition());
            bundle2.putInt("state", this.mState);
            message.setData(bundle2);
            stopPlayInternal();
            Handler handler = this.mHandler;
            if (handler != null) {
                handler.sendMessageDelayed(message, 400);
            }
        } else if (bundle != null) {
            reStartPlay(bundle.getLong(DialogFactory.BUNDLE_ID), bundle.getInt("state"), bundle.getInt("currentPos"));
        }
    }

    public void setMonoMode(boolean z) {
        if (this.mAudioManager != null) {
            String str = TAG;
            Log.m26i(str, "setMonoMode - value : " + z);
            AudioManager audioManager = this.mAudioManager;
            audioManager.setParameters("g_effect_to_mono_enable=" + z);
        }
    }

    public void seekTo(int i) {
        String str = TAG;
        Log.m27i(str, "seekTo : " + i, this.mSession);
        if (seek(i)) {
            SkipSilenceTask skipSilenceTask = this.mSkipSilenceTask;
            if (skipSilenceTask != null) {
                skipSilenceTask.refreshUpcomingSectionsList(i);
            }
            notifyObservers(2012, i, -1);
        }
    }

    public void playComplete() {
        Log.m27i(TAG, "playComplete", this.mSession);
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            setMediaPlayerState(4);
            onCompletion(this.mMediaPlayer);
        }
    }

    public void setVolume(float f, float f2) {
        Log.m27i(TAG, String.format(Locale.US, "setVolume L : %f / R : %f", new Object[]{Float.valueOf(f), Float.valueOf(f2)}), this.mSession);
        try {
            if (this.mMediaPlayer != null) {
                this.mMediaPlayer.setVolume(f, f2);
            }
        } catch (IllegalStateException e) {
            Log.m24e(TAG, "IllegalStateException", (Throwable) e);
        }
    }

    public int getDuration() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer == null) {
            return -1;
        }
        try {
            return mediaPlayer.getDuration();
        } catch (Exception e) {
            String str = TAG;
            Log.m23e(str, "getDuration exception : " + e, this.mSession);
            return 0;
        }
    }

    public int getPosition() {
        synchronized (this.SYN_OBJECT) {
            if (this.mMediaPlayer == null) {
                return -1;
            }
            int currentPosition = this.mMediaPlayer.getCurrentPosition();
            return currentPosition;
        }
    }

    public void skipInterval(int i) {
        int position = getPosition() + i;
        int duration = getDuration();
        int i2 = 0;
        if (this.mRepeatMode == 4) {
            int[] iArr = this.mRepeatPosition;
            if (iArr[0] < iArr[1]) {
                i2 = iArr[0];
                duration = iArr[1];
            } else {
                int i3 = iArr[1];
                duration = iArr[0];
                i2 = i3;
            }
        }
        if (position <= i2) {
            seekTo(i2);
        } else if (position >= duration) {
            seekTo(duration);
        } else {
            seekTo(position);
        }
    }

    /* access modifiers changed from: private */
    public boolean requestAudioFocus() {
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

    public void onDestroy() {
        unregisterAllListener();
        this.mAppContext = null;
        this.mMediaPlayer = null;
    }
}
