package com.sec.android.app.voicenote.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.VolumeShaper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.SpeechTimeDataTreeSet;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.DBProvider;
import com.sec.android.app.voicenote.provider.DNDModeProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.PhoneStateProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.service.AudioFormat;
import com.sec.android.app.voicenote.service.SkipSilenceTask;
import com.sec.android.app.voicenote.service.VoiceNoteService;
import com.sec.android.app.voicenote.service.decoder.Decoder;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Player implements MediaPlayer.OnCompletionListener, SkipSilenceTask.OnSkipSilenceTaskListener, MediaPlayer.OnErrorListener {
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
    public static final int INTERVAL_TIME_FADE_OUT = 500;
    private static final int KEY_PARAMETER_USE_SW_DECODER = 33000;
    private static final int MAX_AUDIO_FOCUS_RETRY_CNT = 10;
    private static final int MIN_REPEAT_INTERVAL = 1000;
    private static final int MSG_SKIP_SILENCE = 2;
    private static final int RECOVER = 4012;
    private static final int STATE_REPEAT_NOT_SET = -1;
    private static final int STOP_PLAY_INTERNAL_WITH_FADE_OUT = 4013;
    private static final int STREAM_VOICENOTE = 13;
    private static final String TAG = "Player";
    private static final float VOLUME_DEFAULT = 1.0f;
    private static Player mInstance;
    private final Object SYN_OBJECT = new Object();
    /* access modifiers changed from: private */
    public Context mAppContext = null;
    private AudioDeviceInfo mAudioDeviceInfo = null;
    private final AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {
        public final void onAudioFocusChange(int i) {
            Player.this.lambda$new$1$Player(i);
        }
    };
    private AudioFocusRequest mAudioFocusRequest = null;
    private AudioManager mAudioManager = null;
    /* access modifiers changed from: private */
    public PlayerHandler mHandler = new PlayerHandler(this);
    private long mID = -1;
    /* access modifiers changed from: private */
    public boolean mIsDisableSkipMutedForCall = false;
    private boolean mIsFadeDown = false;
    private boolean mIsPausedForaWhile = false;
    private boolean mIsPlayWithReceiver = false;
    private boolean mIsRunningSwitchSkipMuted = false;
    private boolean mIsStopPlayWithFadeOut = false;
    private boolean mLeftMute = false;
    private final ArrayList<WeakReference<OnPlayerListener>> mListeners = new ArrayList<>();
    /* access modifiers changed from: private */
    public MediaPlayer mMediaPlayer = null;
    private int mMediaPlayerState = 1;
    private boolean mNeedSleepTime = false;
    private String mPath = null;
    /* access modifiers changed from: private */
    public boolean mPausedByCall = false;
    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        public void onCallStateChanged(int i, String str) {
            Log.m26i(Player.TAG, "mPhoneStateListener : " + i);
            if (i == 0) {
                Log.m19d(Player.TAG, "mPhoneStateListener: mPausedByCall = " + Player.this.mPausedByCall);
                if (Player.this.getPlayerState() == 4 && Player.this.mPausedByCall && Player.this.requestAudioFocus()) {
                    Log.m26i(Player.TAG, "request audio focus success");
                    if (Player.this.mState == 4 && Player.this.mPausedByCall) {
                        boolean unused = Player.this.mPausedByCall = false;
                        if (Decoder.getInstance().getTranslationState() == 1) {
                            if (VoiceNoteApplication.getScene() != 12 && Player.this.resumePlay() == 0) {
                                if (VoiceNoteService.Helper.connectionCount() == 0) {
                                    VoiceNoteApplication.saveEvent(Event.PLAY_RESUME);
                                } else {
                                    Player.this.notifyObservers(1015, -1, -1);
                                }
                            }
                        } else if (Engine.getInstance().resumeTranslation() == 0) {
                            if (VoiceNoteService.Helper.connectionCount() == 0) {
                                VoiceNoteApplication.saveEvent(Event.TRANSLATION_RESUME);
                            } else {
                                Player.this.notifyObservers(1015, -1, -1);
                            }
                        }
                    }
                }
                if (!Player.this.isPlaySpeedActivated() && !Player.this.isRepeatActivated() && !Player.this.isSkipSilenceActivated() && Player.this.mIsDisableSkipMutedForCall) {
                    boolean unused2 = Player.this.mIsDisableSkipMutedForCall = false;
                    Player.this.enableSkipSilenceMode(4);
                }
            } else if (i != 1 && i != 2) {
            } else {
                if (DNDModeProvider.getDoNotDisturb(Player.this.mAppContext)) {
                    Log.m26i(Player.TAG, "Ringing or Off hook but DnD enabled");
                    return;
                }
                if (!Player.this.isSkipSilenceActivated()) {
                    Player.this.enableSkipSilenceMode(1);
                    boolean unused3 = Player.this.mIsDisableSkipMutedForCall = true;
                }
                if (Decoder.getInstance().getTranslationState() == 1) {
                    if (Player.this.mState == 3) {
                        boolean unused4 = Player.this.mPausedByCall = true;
                        Player.this.pausePlay();
                        if (VoiceNoteService.Helper.connectionCount() == 0) {
                            VoiceNoteApplication.saveEvent(Event.PLAY_PAUSE);
                        } else {
                            Player.this.notifyObservers(1016, -1, -1);
                        }
                    }
                } else if (Player.this.mState == 3) {
                    boolean unused5 = Player.this.mPausedByCall = true;
                    Engine.getInstance().pauseTranslation(false);
                    if (VoiceNoteService.Helper.connectionCount() == 0) {
                        VoiceNoteApplication.saveEvent(Event.TRANSLATION_PAUSE);
                    } else {
                        Player.this.notifyObservers(1016, -1, -1);
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
    private int mSkipSilenceMode = 4;
    /* access modifiers changed from: private */
    public SkipSilenceTask mSkipSilenceTask = null;
    private final SoundHandler mSoundHandler = new SoundHandler(this);
    private float mSpeed = 1.0f;
    private float mSpeedInContinuePlayMode = -1.0f;
    /* access modifiers changed from: private */
    public int mState = 1;
    private TelephonyManager mTelephonyManager = null;
    private VolumeShaper mVolumeShaper = null;
    private VolumeShaper.Configuration mVolumeShaperConfig = null;

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

    private static class MediaPlayerState {
        public static final int ERROR = 5;
        public static final int IDLE = 1;
        public static final int PAUSED = 4;
        public static final int PREPARED = 2;
        public static final int STARTED = 3;

        private MediaPlayerState() {
        }
    }

    private Player() {
        Log.m19d(TAG, "Player creator !!");
    }

    public static Player getInstance() {
        if (mInstance == null) {
            synchronized (Player.class) {
                if (mInstance == null) {
                    mInstance = new Player();
                }
            }
        }
        return mInstance;
    }

    public void setApplicationContext(Context context) {
        this.mAppContext = context;
    }

    public void onCompletion(MediaPlayer mediaPlayer) {
        int i;
        Log.m26i(TAG, "onCompletion");
        Timer timer = this.mPlayerTimer;
        if (timer != null) {
            timer.cancel();
            this.mPlayerTimer = null;
        }
        if (this.mRepeatMode == 4) {
            int[] iArr = this.mRepeatPosition;
            if (iArr[0] < iArr[1]) {
                seekTo(iArr[0]);
            } else {
                seekTo(iArr[1]);
            }
            if (getPlayerState() == 3) {
                resumePlay();
            }
        } else if (Engine.getInstance().getTrimEndTime() > 0) {
            setPlayerState(4);
            setMediaPlayerState(4);
            seekTo(Engine.getInstance().getTrimEndTime());
        } else {
            setPlayerState(4);
            setMediaPlayerState(4);
            notifyObservers(2011, 0, -1);
            if (mediaPlayer != null) {
                try {
                    i = mediaPlayer.getDuration();
                } catch (Exception e) {
                    Log.m22e(TAG, "getDuration exception : " + e);
                    i = 0;
                }
            } else {
                i = this.mMediaPlayer.getDuration();
            }
            seekTo(i);
            AudioManager audioManager = this.mAudioManager;
            if (audioManager != null) {
                audioManager.abandonAudioFocusRequest(getAudioFocusRequest());
            }
        }
        if (Settings.getBooleanSettings(Settings.KEY_PLAY_CONTINUOUSLY, false)) {
            Activity topActivity = VoiceNoteApplication.getApplication().getTopActivity();
            this.mSpeedInContinuePlayMode = this.mSpeed;
            if (VoiceNoteService.Helper.connectionCount() == 0) {
                lambda$onCompletion$0$Player();
            } else if (topActivity != null) {
                topActivity.runOnUiThread(new Runnable() {
                    public final void run() {
                        Player.this.lambda$onCompletion$0$Player();
                    }
                });
            }
        } else {
            this.mSpeedInContinuePlayMode = -1.0f;
        }
    }

    /* renamed from: doNextPlay */
    public void lambda$onCompletion$0$Player() {
        Log.m26i(TAG, "doNextPlay");
        if (!Engine.getInstance().isSimplePlayerMode() && Engine.getInstance().getRepeatMode() != 4 && VoiceNoteApplication.getScene() != 6 && VoiceNoteApplication.getScene() != 8) {
            this.mSpeedInContinuePlayMode = this.mSpeed;
            String nextFilePath = CursorProvider.getInstance().getNextFilePath();
            if (isSkipSilenceActivated()) {
                if (CursorProvider.getInstance().getRecordMode(DBProvider.getInstance().getIdByPath(nextFilePath)) == 2) {
                    this.mSkipSilenceMode = 2;
                } else {
                    this.mSkipSilenceMode = 3;
                }
            }
            if (nextFilePath != null) {
                notifyEvent(51);
                if (nextFilePath.contains(AudioFormat.ExtType.EXT_AMR)) {
                    this.mSpeed = 1.0f;
                    this.mSpeedInContinuePlayMode = 1.0f;
                }
                if (VoiceNoteService.Helper.connectionCount() == 0) {
                    Log.m26i(TAG, "doNextPlay in background");
//                    LocalBroadcastManager.getInstance(this.mAppContext).sendBroadcast(new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_PLAY_NEXT));
                } else if (VoiceNoteApplication.getScene() == 4 || VoiceNoteApplication.getScene() == 3 || VoiceNoteApplication.getScene() == 7) {
                    Engine.getInstance().clearContentItem();
                    int startPlay = Engine.getInstance().startPlay(nextFilePath);
                    if (startPlay == -103) {
                        Toast.makeText(this.mAppContext, C0690R.string.no_play_during_call, 0).show();
                    } else if (startPlay == 0) {
                        CursorProvider.getInstance().moveToNextPosition();
                        notifyEvent(12);
                        Engine.getInstance().setRepeatMode(2);
                        if (Engine.getInstance().getSkipSilenceMode() == 3 || Engine.getInstance().getSkipSilenceMode() == 2) {
                            Engine.getInstance().setRepeatMode(1);
                            Engine.getInstance().setPlaySpeed(-1.0f);
                        }
                        Engine.getInstance().setCurrentTime(0);
                    }
                } else {
                    notifyEvent(12);
                }
            }
        }
    }

    private void notifyEvent(int i) {
        int scene = VoiceNoteApplication.getScene();
        VoiceNoteObservable instance = VoiceNoteObservable.getInstance();
        if (i != 12) {
            if (i == 51) {
                instance.notifyObservers(Integer.valueOf(Event.HIDE_DIALOG));
            }
        } else if (scene == 3) {
            instance.notifyObservers(Integer.valueOf(Event.MINI_PLAY_NEXT));
        } else if (scene == 4) {
            instance.notifyObservers(Integer.valueOf(Event.PLAY_STOP));
            instance.notifyObservers(Integer.valueOf(Event.PLAY_NEXT));
            instance.notifyObservers(Integer.valueOf(Event.UPDATE_FILE_NAME));
        } else if (scene == 7) {
            instance.notifyObservers(Integer.valueOf(Event.SEARCH_MINI_PLAY_NEXT));
        }
    }

    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        Log.m22e(TAG, "onError occur stopPlay  - what : " + i + " extra : " + i2);
        notifyObservers(2010, 5, i2);
        return stopPlay(false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:40:0x00de A[Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00e9 A[Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean preparePlay(java.lang.String r6, int r7, boolean r8) {
        /*
            r5 = this;
            java.lang.String r0 = "Player"
            java.lang.String r1 = "preparePlay"
            com.sec.android.app.voicenote.provider.Log.m29v(r0, r1)
            android.media.MediaPlayer r1 = new android.media.MediaPlayer
            r1.<init>()
            r5.mMediaPlayer = r1
            android.content.Context r1 = r5.mAppContext
            java.lang.String r2 = "audio"
            java.lang.Object r1 = r1.getSystemService(r2)
            android.media.AudioManager r1 = (android.media.AudioManager) r1
            r5.mAudioManager = r1
            android.content.Context r1 = r5.mAppContext
            java.lang.String r2 = "phone"
            java.lang.Object r1 = r1.getSystemService(r2)
            android.telephony.TelephonyManager r1 = (android.telephony.TelephonyManager) r1
            r5.mTelephonyManager = r1
            android.telephony.TelephonyManager r1 = r5.mTelephonyManager
            android.telephony.PhoneStateListener r2 = r5.mPhoneStateListener
            r3 = 32
            r1.listen(r2, r3)
            android.media.MediaPlayer r1 = r5.mMediaPlayer
            r2 = 0
            if (r1 != 0) goto L_0x0035
            return r2
        L_0x0035:
            if (r8 == 0) goto L_0x003e
            int r8 = r5.getPlayerState()
            r1 = 4
            if (r8 == r1) goto L_0x0045
        L_0x003e:
            boolean r8 = r5.requestAudioFocus()
            if (r8 != 0) goto L_0x0045
            return r2
        L_0x0045:
            android.media.MediaPlayer r8 = r5.mMediaPlayer     // Catch:{ Exception -> 0x0138 }
            r1 = 3
            r8.setAudioStreamType(r1)     // Catch:{ Exception -> 0x0138 }
            com.sec.android.app.voicenote.service.Engine r8 = com.sec.android.app.voicenote.service.Engine.getInstance()
            boolean r8 = r8.isWiredHeadSetConnected()
            r3 = 2
            r4 = 1
            if (r8 != 0) goto L_0x0061
            com.sec.android.app.voicenote.service.Engine r8 = com.sec.android.app.voicenote.service.Engine.getInstance()
            boolean r8 = r8.isBluetoothHeadSetConnected()
            if (r8 == 0) goto L_0x0066
        L_0x0061:
            if (r7 != r3) goto L_0x0066
            r5.setMonoMode(r4)
        L_0x0066:
            r7 = 5
            android.media.MediaPlayer r8 = r5.mMediaPlayer     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            r8.setDataSource(r6)     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            int r6 = r5.mSkipSilenceMode     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            if (r6 == r1) goto L_0x0088
            int r6 = r5.mSkipSilenceMode     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            if (r6 != r3) goto L_0x0075
            goto L_0x0088
        L_0x0075:
            android.media.AudioAttributes$Builder r6 = new android.media.AudioAttributes$Builder     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            r6.<init>()     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            android.media.AudioAttributes$Builder r6 = r6.setUsage(r4)     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            android.media.AudioAttributes r6 = r6.build()     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            android.media.MediaPlayer r8 = r5.mMediaPlayer     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            r8.setAudioAttributes(r6)     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            goto L_0x00c4
        L_0x0088:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            r6.<init>()     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            java.lang.String r8 = "preparePlay - mSkipSilenceMode : "
            r6.append(r8)     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            int r8 = r5.mSkipSilenceMode     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            r6.append(r8)     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            java.lang.String r6 = r6.toString()     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r6)     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            android.media.AudioAttributes$Builder r6 = new android.media.AudioAttributes$Builder     // Catch:{ IllegalArgumentException -> 0x00be, Exception -> 0x00b7 }
            r6.<init>()     // Catch:{ IllegalArgumentException -> 0x00be, Exception -> 0x00b7 }
            android.media.AudioAttributes$Builder r6 = r6.setUsage(r4)     // Catch:{ IllegalArgumentException -> 0x00be, Exception -> 0x00b7 }
            java.lang.String r8 = "STREAM_VOICENOTE"
            android.media.AudioAttributes$Builder r6 = r6.semAddAudioTag(r8)     // Catch:{ IllegalArgumentException -> 0x00be, Exception -> 0x00b7 }
            android.media.AudioAttributes r6 = r6.build()     // Catch:{ IllegalArgumentException -> 0x00be, Exception -> 0x00b7 }
            android.media.MediaPlayer r8 = r5.mMediaPlayer     // Catch:{ IllegalArgumentException -> 0x00be, Exception -> 0x00b7 }
            r8.setAudioAttributes(r6)     // Catch:{ IllegalArgumentException -> 0x00be, Exception -> 0x00b7 }
            goto L_0x00c4
        L_0x00b7:
            r6 = move-exception
            java.lang.String r8 = "UnKnownException occur "
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r0, (java.lang.String) r8, (java.lang.Throwable) r6)     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            goto L_0x00c4
        L_0x00be:
            r6 = move-exception
            java.lang.String r8 = "IllegalArgumentException occur "
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r0, (java.lang.String) r8, (java.lang.Throwable) r6)     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
        L_0x00c4:
            boolean r6 = r5.isPlayWithReceiver()     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            if (r6 == 0) goto L_0x00e9
            com.sec.android.app.voicenote.service.Engine r6 = com.sec.android.app.voicenote.service.Engine.getInstance()     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            boolean r6 = r6.isWiredHeadSetConnected()     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            if (r6 != 0) goto L_0x00e9
            com.sec.android.app.voicenote.service.Engine r6 = com.sec.android.app.voicenote.service.Engine.getInstance()     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            boolean r6 = r6.isBluetoothHeadSetConnected()     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            if (r6 != 0) goto L_0x00e9
            android.media.AudioDeviceInfo r6 = r5.mAudioDeviceInfo     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            if (r6 != 0) goto L_0x00f8
            android.media.AudioDeviceInfo r6 = r5.getAudioDeviceInfo(r4)     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            r5.mAudioDeviceInfo = r6     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            goto L_0x00f8
        L_0x00e9:
            android.media.AudioDeviceInfo r6 = r5.mAudioDeviceInfo     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            if (r6 == 0) goto L_0x00f8
            android.media.AudioDeviceInfo r6 = r5.mAudioDeviceInfo     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            int r6 = r6.getType()     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            if (r6 != r4) goto L_0x00f8
            r6 = 0
            r5.mAudioDeviceInfo = r6     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
        L_0x00f8:
            android.media.MediaPlayer r6 = r5.mMediaPlayer     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            android.media.AudioDeviceInfo r8 = r5.mAudioDeviceInfo     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            r6.setPreferredDevice(r8)     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            android.media.MediaPlayer r6 = r5.mMediaPlayer     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            r6.prepare()     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            android.media.MediaPlayer r6 = r5.mMediaPlayer     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            r8 = 33000(0x80e8, float:4.6243E-41)
            r6.semSetParameter(r8, r4)     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            r6 = 1065353216(0x3f800000, float:1.0)
            r5.createVolumeShapeConfig(r6)     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            android.media.MediaPlayer r6 = r5.mMediaPlayer     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            r5.createVolumeShaper(r6)     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            r5.setMediaPlayerState(r3)     // Catch:{ IOException -> 0x012e, IllegalStateException -> 0x0124, RuntimeException -> 0x011a }
            return r4
        L_0x011a:
            r6 = move-exception
            java.lang.String r8 = "RuntimeException"
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r0, (java.lang.String) r8, (java.lang.Throwable) r6)
            r5.setMediaPlayerState(r7)
            return r2
        L_0x0124:
            r6 = move-exception
            java.lang.String r8 = "IllegalStateException"
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r0, (java.lang.String) r8, (java.lang.Throwable) r6)
            r5.setMediaPlayerState(r7)
            return r2
        L_0x012e:
            r6 = move-exception
            java.lang.String r8 = "IOException"
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r0, (java.lang.String) r8, (java.lang.Throwable) r6)
            r5.setMediaPlayerState(r7)
            return r2
        L_0x0138:
            r6 = move-exception
            java.lang.String r6 = r6.toString()
            com.sec.android.app.voicenote.provider.Log.m22e(r0, r6)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.Player.preparePlay(java.lang.String, int, boolean):boolean");
    }

    private AudioDeviceInfo getAudioDeviceInfo(int i) {
        Log.m26i(TAG, "getAudioDeviceInfo - flag: " + i);
        AudioManager audioManager = this.mAudioManager;
        if (audioManager == null) {
            Log.m22e(TAG, "AudioManager is null - return NULL!!");
            return null;
        }
        for (AudioDeviceInfo audioDeviceInfo : audioManager.getDevices(2)) {
            if (audioDeviceInfo.getType() == i) {
                Log.m26i(TAG, "getAudioDeviceInfo - type: " + audioDeviceInfo.getType());
                return audioDeviceInfo;
            }
        }
        return null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:61:0x014f, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean startPlay(java.lang.String r10, long r11, boolean r13) {
        /*
            r9 = this;
            monitor-enter(r9)
            java.lang.String r0 = "Player"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0158 }
            r1.<init>()     // Catch:{ all -> 0x0158 }
            java.lang.String r2 = "startPlay - id : "
            r1.append(r2)     // Catch:{ all -> 0x0158 }
            r1.append(r11)     // Catch:{ all -> 0x0158 }
            java.lang.String r2 = " title : "
            r1.append(r2)     // Catch:{ all -> 0x0158 }
            r2 = 47
            int r2 = r10.lastIndexOf(r2)     // Catch:{ all -> 0x0158 }
            r3 = 1
            int r2 = r2 + r3
            r4 = 46
            int r4 = r10.lastIndexOf(r4)     // Catch:{ all -> 0x0158 }
            java.lang.String r2 = r10.substring(r2, r4)     // Catch:{ all -> 0x0158 }
            r1.append(r2)     // Catch:{ all -> 0x0158 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0158 }
            com.sec.android.app.voicenote.provider.Log.m19d(r0, r1)     // Catch:{ all -> 0x0158 }
            int r0 = r9.mState     // Catch:{ all -> 0x0158 }
            r1 = 0
            r2 = 4
            r4 = 3
            if (r0 == r4) goto L_0x003c
            int r0 = r9.mState     // Catch:{ all -> 0x0158 }
            if (r0 != r2) goto L_0x0068
        L_0x003c:
            java.util.Timer r0 = r9.mPlayerTimer     // Catch:{ all -> 0x0158 }
            if (r0 == 0) goto L_0x0047
            java.util.Timer r0 = r9.mPlayerTimer     // Catch:{ all -> 0x0158 }
            r0.cancel()     // Catch:{ all -> 0x0158 }
            r9.mPlayerTimer = r1     // Catch:{ all -> 0x0158 }
        L_0x0047:
            com.sec.android.app.voicenote.service.MetadataRepository r0 = com.sec.android.app.voicenote.service.MetadataRepository.getInstance()     // Catch:{ all -> 0x0158 }
            java.lang.String r5 = r0.getPath()     // Catch:{ all -> 0x0158 }
            if (r5 == 0) goto L_0x0062
            java.lang.String r5 = r0.getPath()     // Catch:{ all -> 0x0158 }
            java.lang.String r6 = r9.mPath     // Catch:{ all -> 0x0158 }
            boolean r5 = r5.equals(r6)     // Catch:{ all -> 0x0158 }
            if (r5 == 0) goto L_0x0062
            java.lang.String r5 = r9.mPath     // Catch:{ all -> 0x0158 }
            r0.write(r5)     // Catch:{ all -> 0x0158 }
        L_0x0062:
            r0.initialize()     // Catch:{ all -> 0x0158 }
            r9.stopPlayInternal()     // Catch:{ all -> 0x0158 }
        L_0x0068:
            com.sec.android.app.voicenote.service.Player$PlayerHandler r0 = r9.mHandler     // Catch:{ all -> 0x0158 }
            r5 = 2
            r0.removeMessages(r5)     // Catch:{ all -> 0x0158 }
            com.sec.android.app.voicenote.service.MetadataRepository r0 = com.sec.android.app.voicenote.service.MetadataRepository.getInstance()     // Catch:{ all -> 0x0158 }
            r0.initialize()     // Catch:{ all -> 0x0158 }
            r0.read(r10)     // Catch:{ all -> 0x0158 }
            java.lang.String r6 = "play_mode"
            int r7 = r0.getRecordMode()     // Catch:{ all -> 0x0158 }
            com.sec.android.app.voicenote.provider.Settings.setSettings((java.lang.String) r6, (int) r7)     // Catch:{ all -> 0x0158 }
            com.sec.android.app.voicenote.service.SkipSilenceTask r6 = r9.mSkipSilenceTask     // Catch:{ all -> 0x0158 }
            if (r6 == 0) goto L_0x008c
            com.sec.android.app.voicenote.service.SkipSilenceTask r6 = r9.mSkipSilenceTask     // Catch:{ all -> 0x0158 }
            r6.setSkipSilenceTaskListener(r1)     // Catch:{ all -> 0x0158 }
            r9.mSkipSilenceTask = r1     // Catch:{ all -> 0x0158 }
        L_0x008c:
            int r1 = r0.getRecordMode()     // Catch:{ all -> 0x0158 }
            r6 = 1065353216(0x3f800000, float:1.0)
            if (r1 != r5) goto L_0x00ab
            r1 = 0
            boolean r7 = r0.isEnabledPerson(r1)     // Catch:{ all -> 0x0158 }
            r8 = 1127481344(0x43340000, float:180.0)
            boolean r8 = r0.isEnabledPerson(r8)     // Catch:{ all -> 0x0158 }
            if (r7 == 0) goto L_0x00a3
            r7 = r6
            goto L_0x00a4
        L_0x00a3:
            r7 = r1
        L_0x00a4:
            if (r8 == 0) goto L_0x00a7
            r1 = r6
        L_0x00a7:
            r9.setVolume(r7, r1)     // Catch:{ all -> 0x0158 }
            goto L_0x00ae
        L_0x00ab:
            r9.setVolume(r6, r6)     // Catch:{ all -> 0x0158 }
        L_0x00ae:
            float r1 = r9.mSpeedInContinuePlayMode     // Catch:{ all -> 0x0158 }
            r7 = -1082130432(0xffffffffbf800000, float:-1.0)
            int r1 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
            if (r1 != 0) goto L_0x00b9
            r9.setPlaySpeed(r6)     // Catch:{ all -> 0x0158 }
        L_0x00b9:
            int r0 = r0.getRecordMode()     // Catch:{ all -> 0x0158 }
            r1 = 0
            boolean r0 = r9.preparePlay(r10, r0, r1)     // Catch:{ all -> 0x0158 }
            if (r0 == 0) goto L_0x0150
            android.media.MediaPlayer r0 = r9.mMediaPlayer     // Catch:{ all -> 0x0158 }
            r0.setOnCompletionListener(r9)     // Catch:{ all -> 0x0158 }
            android.media.MediaPlayer r0 = r9.mMediaPlayer     // Catch:{ all -> 0x0158 }
            r0.setOnErrorListener(r9)     // Catch:{ all -> 0x0158 }
            if (r13 == 0) goto L_0x012b
            int r0 = r9.mMediaPlayerState     // Catch:{ all -> 0x0158 }
            if (r0 == r5) goto L_0x00d8
            int r0 = r9.mMediaPlayerState     // Catch:{ all -> 0x0158 }
            if (r0 != r2) goto L_0x012b
        L_0x00d8:
            android.media.MediaPlayer r0 = r9.mMediaPlayer     // Catch:{ IllegalArgumentException -> 0x0108, IllegalStateException -> 0x00ec }
            android.media.MediaPlayer r6 = r9.mMediaPlayer     // Catch:{ IllegalArgumentException -> 0x0108, IllegalStateException -> 0x00ec }
            android.media.PlaybackParams r6 = r6.getPlaybackParams()     // Catch:{ IllegalArgumentException -> 0x0108, IllegalStateException -> 0x00ec }
            float r7 = r9.getPlaySpeed()     // Catch:{ IllegalArgumentException -> 0x0108, IllegalStateException -> 0x00ec }
            android.media.PlaybackParams r6 = r6.setSpeed(r7)     // Catch:{ IllegalArgumentException -> 0x0108, IllegalStateException -> 0x00ec }
            r0.setPlaybackParams(r6)     // Catch:{ IllegalArgumentException -> 0x0108, IllegalStateException -> 0x00ec }
            goto L_0x0123
        L_0x00ec:
            r0 = move-exception
            java.lang.String r6 = "Player"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x0158 }
            r7.<init>()     // Catch:{ all -> 0x0158 }
            java.lang.String r8 = "startPlay - the internal player engine has not been initialized or has been released - "
            r7.append(r8)     // Catch:{ all -> 0x0158 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0158 }
            r7.append(r0)     // Catch:{ all -> 0x0158 }
            java.lang.String r0 = r7.toString()     // Catch:{ all -> 0x0158 }
            com.sec.android.app.voicenote.provider.Log.m22e(r6, r0)     // Catch:{ all -> 0x0158 }
            goto L_0x0123
        L_0x0108:
            r0 = move-exception
            java.lang.String r6 = "Player"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x0158 }
            r7.<init>()     // Catch:{ all -> 0x0158 }
            java.lang.String r8 = "startPlay - params is not supported - "
            r7.append(r8)     // Catch:{ all -> 0x0158 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0158 }
            r7.append(r0)     // Catch:{ all -> 0x0158 }
            java.lang.String r0 = r7.toString()     // Catch:{ all -> 0x0158 }
            com.sec.android.app.voicenote.provider.Log.m22e(r6, r0)     // Catch:{ all -> 0x0158 }
        L_0x0123:
            android.media.MediaPlayer r0 = r9.mMediaPlayer     // Catch:{ all -> 0x0158 }
            r0.start()     // Catch:{ all -> 0x0158 }
            r9.setMediaPlayerState(r4)     // Catch:{ all -> 0x0158 }
        L_0x012b:
            r9.mPath = r10     // Catch:{ all -> 0x0158 }
            r9.mID = r11     // Catch:{ all -> 0x0158 }
            if (r13 == 0) goto L_0x0135
            r9.setPlayerState(r4)     // Catch:{ all -> 0x0158 }
            goto L_0x0138
        L_0x0135:
            r9.setPlayerState(r5)     // Catch:{ all -> 0x0158 }
        L_0x0138:
            r9.setRepeatMode(r5)     // Catch:{ all -> 0x0158 }
            java.lang.String r10 = "play_continuously"
            boolean r10 = com.sec.android.app.voicenote.provider.Settings.getBooleanSettings(r10, r1)     // Catch:{ all -> 0x0158 }
            if (r10 == 0) goto L_0x014b
            int r10 = r9.mRepeatMode     // Catch:{ all -> 0x0158 }
            if (r10 != r5) goto L_0x014e
            int r10 = r9.mSkipSilenceMode     // Catch:{ all -> 0x0158 }
            if (r10 != r3) goto L_0x014e
        L_0x014b:
            r9.enableSkipSilenceMode(r2)     // Catch:{ all -> 0x0158 }
        L_0x014e:
            monitor-exit(r9)
            return r3
        L_0x0150:
            r9.stopPlayInternal()     // Catch:{ all -> 0x0158 }
            r9.setPlayerState(r3)     // Catch:{ all -> 0x0158 }
            monitor-exit(r9)
            return r1
        L_0x0158:
            r10 = move-exception
            monitor-exit(r9)
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.Player.startPlay(java.lang.String, long, boolean):boolean");
    }

    private synchronized void reStartPlay(long j, int i, int i2) {
        String str = this.mPath;
        if (str != null && !str.isEmpty()) {
            if (i != 1) {
                Log.m19d(TAG, "reStartPlay - id : " + j + " title : " + str.substring(str.lastIndexOf(47) + 1, str.lastIndexOf(46)) + " state : " + i);
                if (this.mState == 3 || this.mState == 4) {
                    MetadataRepository instance = MetadataRepository.getInstance();
                    if (instance.getPath() != null && instance.getPath().equals(this.mPath)) {
                        instance.write(this.mPath);
                    }
                    instance.initialize();
                    stopPlayInternal();
                }
                MetadataRepository instance2 = MetadataRepository.getInstance();
                instance2.initialize();
                instance2.read(str);
                instance2.setRecordMode(CursorProvider.getInstance().getRecordMode(j));
                Settings.setSettings(Settings.KEY_PLAY_MODE, instance2.getRecordMode());
                if (instance2.getRecordMode() == 2) {
                    float f = 0.0f;
                    boolean isEnabledPerson = instance2.isEnabledPerson(0.0f);
                    float f2 = instance2.isEnabledPerson(180.0f) ? 1.0f : 0.0f;
                    if (isEnabledPerson) {
                        f = 1.0f;
                    }
                    setVolume(f2, f);
                } else {
                    setVolume(1.0f, 1.0f);
                }
                if (preparePlay(str, instance2.getRecordMode(), true)) {
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
                    this.mPath = str;
                    this.mID = j;
                    return;
                }
                stopPlayInternal();
                setPlayerState(1);
                return;
            }
        }
        Log.m26i(TAG, "reStartPlay : invalid id or path or player already stopped.");
    }

    public synchronized boolean pausePlay() {
        Log.m26i(TAG, "pausePlay - State : " + this.mState);
        if (this.mMediaPlayer == null) {
            Log.m22e(TAG, "pausePlay MediaPlayer is null !!!");
            return false;
        } else if (this.mState == 2) {
            setPlayerState(2);
            Log.m22e(TAG, "pausePlay PlayerState.PREPARED return !!!");
            return false;
        } else {
            try {
                if (!this.mPausedByCall && !this.mIsPausedForaWhile) {
                    this.mAudioManager.abandonAudioFocusRequest(getAudioFocusRequest());
                }
                this.mMediaPlayer.pause();
                setMediaPlayerState(4);
                notifyObservers(2012, getPosition(), -1);
                setPlayerState(4);
                releasePreferredDevice();
                return true;
            } catch (IllegalStateException unused) {
                Log.m26i(TAG, "pausePlay IllegalStateException !!");
                setMediaPlayerState(5);
                return false;
            }
        }
    }

    public synchronized int resumePlay() {
        Log.m26i(TAG, "resumePlay");
        if (this.mMediaPlayer == null) {
            Log.m22e(TAG, "resumePlay MediaPlayer is null !!!");
            return -115;
        } else if (!requestAudioFocus()) {
            return -109;
        } else {
            resetFadedVolume();
            if (this.mRepeatMode == 4) {
                int i = this.mRepeatPosition[0] < this.mRepeatPosition[1] ? this.mRepeatPosition[0] : this.mRepeatPosition[1];
                if (getPosition() < i) {
                    seekTo(i);
                }
            }
            try {
                if (!isPlayWithReceiver() || Engine.getInstance().isWiredHeadSetConnected() || Engine.getInstance().isBluetoothHeadSetConnected()) {
                    if (this.mAudioDeviceInfo != null && this.mAudioDeviceInfo.getType() == 1) {
                        this.mAudioDeviceInfo = null;
                        this.mMediaPlayer.setPreferredDevice(this.mAudioDeviceInfo);
                        this.mNeedSleepTime = true;
                    }
                } else if (this.mAudioDeviceInfo == null || !(this.mAudioDeviceInfo == null || this.mAudioDeviceInfo.getType() == 1)) {
                    this.mAudioDeviceInfo = getAudioDeviceInfo(1);
                    this.mMediaPlayer.setPreferredDevice(this.mAudioDeviceInfo);
                    this.mNeedSleepTime = true;
                }
                if (this.mNeedSleepTime) {
                    try {
                        Thread.sleep(150);
                        this.mNeedSleepTime = false;
                    } catch (InterruptedException e) {
                        Log.m22e(TAG, "resumePlay - sleep thread - " + e.toString());
                    }
                    this.mNeedSleepTime = false;
                }
                try {
                    this.mMediaPlayer.setPlaybackParams(this.mMediaPlayer.getPlaybackParams().setSpeed(getPlaySpeed()));
                } catch (IllegalArgumentException e2) {
                    Log.m22e(TAG, "resumePlay - params is not supported - " + e2.toString());
                } catch (IllegalStateException e3) {
                    Log.m22e(TAG, "resumePlay - the internal player engine has not been initialized or has been released - " + e3.toString());
                }
                this.mMediaPlayer.start();
                setMediaPlayerState(3);
                setPlayerState(3);
                this.mPausedByCall = false;
                return 0;
            } catch (IllegalStateException unused) {
                Log.m26i(TAG, "resumePlay IllegalStateException !!");
                return -115;
            } catch (Throwable th) {
                this.mNeedSleepTime = false;
                throw th;
            }
        }
    }

    public boolean stopPlay() {
        return stopPlay(true);
    }

    public synchronized boolean initPlay() {
        Log.m26i(TAG, "initPlay - PlayerState : " + this.mState);
        if (this.mState == 1) {
            return false;
        }
        MetadataRepository.getInstance().write(this.mPath);
        this.mLeftMute = false;
        this.mRightMute = false;
        return true;
    }

    public synchronized boolean stopPlay(boolean z) {
        Log.m26i(TAG, "stopPlay - updateMetadata : " + z + " PlayerState : " + this.mState);
        if (this.mState == 1) {
            return false;
        }
        if (this.mTelephonyManager != null) {
            this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
        }
        setPlayerState(1);
        setRepeatMode(2, -1);
        enableSkipSilenceMode(4);
        this.mAudioManager.abandonAudioFocusRequest(getAudioFocusRequest());
        MetadataRepository instance = MetadataRepository.getInstance();
        if (z) {
            instance.write(this.mPath);
        }
        this.mPath = null;
        this.mID = -1;
        setVolume(1.0f, 1.0f);
        this.mLeftMute = false;
        this.mRightMute = false;
        if (this.mIsStopPlayWithFadeOut) {
            stopPlayInternalWithFadeOut();
        } else {
            stopPlayInternal();
        }
        this.mSpeedInContinuePlayMode = -1.0f;
        this.mIsRunningSwitchSkipMuted = false;
        return true;
    }

    /* access modifiers changed from: private */
    public void stopPlayInternal() {
        Log.m32w(TAG, "stopPlayInternal E");
        Timer timer = this.mPlayerTimer;
        if (timer != null) {
            timer.cancel();
            this.mPlayerTimer = null;
        }
        synchronized (this.SYN_OBJECT) {
            if (this.mMediaPlayer != null) {
                try {
                    this.mMediaPlayer.stop();
                    this.mMediaPlayer.release();
                    if (this.mVolumeShaper != null) {
                        clearVolumeShaper();
                    }
                    setMediaPlayerState(1);
                } catch (IllegalStateException unused) {
                    try {
                        Log.m22e(TAG, "stopPlayInternal IllegalStateException");
                    } catch (Throwable th) {
                        this.mMediaPlayer = null;
                        throw th;
                    }
                }
                this.mMediaPlayer = null;
            }
        }
        setMonoMode(false);
        Log.m32w(TAG, "stopPlayInternal X");
    }

    private boolean seek(int i) {
        synchronized (this.SYN_OBJECT) {
            if (this.mMediaPlayer == null) {
                return false;
            }
//            this.mMediaPlayer.semSeekTo(i, 1);
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
        Log.m29v(TAG, "setMediaPlayerState state : " + i);
        this.mMediaPlayerState = i;
    }

    private void setPlayerState(int i) {
        Log.m26i(TAG, "setPlayerState - state : " + i);
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
                if (Player.this.mMediaPlayer == null || Player.this.mState != 3) {
                    cancel();
                    return;
                }
                try {
                    int position = Player.this.getPosition();
                    if (VoiceNoteApplication.getScene() != 12) {
                        if (Player.this.mRepeatMode == 4) {
                            if (Player.this.mRepeatPosition[0] < Player.this.mRepeatPosition[1]) {
                                if (Player.this.mRepeatPosition[1] < position || Player.this.mRepeatPosition[0] > position + 20) {
                                    Log.m26i(Player.TAG, "Repeat currentPosition A-B : " + position + " repeatTime : " + Player.this.mRepeatPosition[0] + '~' + Player.this.mRepeatPosition[1]);
                                    Player player = Player.this;
                                    player.seekTo(player.mRepeatPosition[0]);
                                    return;
                                }
                            } else if (Player.this.mRepeatPosition[0] < position || Player.this.mRepeatPosition[1] > position + 20) {
                                Log.m26i(Player.TAG, "Repeat currentPosition B-A : " + position + " repeatTime : " + Player.this.mRepeatPosition[0] + '~' + Player.this.mRepeatPosition[1]);
                                Player player2 = Player.this;
                                player2.seekTo(player2.mRepeatPosition[1]);
                                return;
                            }
                        }
                        int trimEndTime = Engine.getInstance().getTrimEndTime();
                        if (trimEndTime > 0) {
                            int trimStartTime = Engine.getInstance().getTrimStartTime();
                            if (position >= trimEndTime) {
                                Log.m26i(Player.TAG, "Trim currentPosition : " + position + " trimTime : " + trimStartTime + '~' + trimEndTime);
                                Player.this.pausePlay();
                                Player.this.mHandler.sendEmptyMessage(2016);
                                Player.this.seekTo(trimEndTime);
                                return;
                            } else if (position + 20 < trimStartTime) {
                                Log.m26i(Player.TAG, "Trim currentPosition : " + position + " trimTime : " + trimStartTime + '~' + trimEndTime);
                                Player.this.seekTo(trimStartTime);
                                return;
                            }
                        }
                    }
                    if (Player.this.mSkipSilenceTask != null && !Player.this.mSkipSilenceTask.checkMuteSection(position)) {
                        position = Player.this.getDuration();
                    }
                    Player.this.notifyObservers(2012, position, -1);
                } catch (Exception e) {
                    Log.m22e(Player.TAG, e.getMessage());
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

    public int setDCRepeatMode(int i, int i2) {
        return setRepeatMode(i, i2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x005c  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0064  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int setRepeatMode(int r6, int r7) {
        /*
            r5 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "setRepeatMode - state : "
            r0.append(r1)
            r0.append(r6)
            java.lang.String r1 = " position : "
            r0.append(r1)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "Player"
            com.sec.android.app.voicenote.provider.Log.m26i(r1, r0)
            int r0 = r5.mState
            r1 = 2
            r2 = 1
            if (r0 != r2) goto L_0x0025
            r6 = r1
        L_0x0025:
            r5.mRepeatMode = r6
            int r6 = r5.mRepeatMode
            r0 = -1
            r3 = 0
            if (r6 == r2) goto L_0x0053
            if (r6 == r1) goto L_0x0053
            r1 = 3
            if (r6 == r1) goto L_0x004a
            r1 = 4
            if (r6 == r1) goto L_0x0036
            goto L_0x005a
        L_0x0036:
            int[] r6 = r5.mRepeatPosition
            r1 = r6[r3]
            int r1 = r7 - r1
            r4 = 1000(0x3e8, float:1.401E-42)
            if (r1 >= r4) goto L_0x0045
            r6 = r6[r3]
            int r6 = r6 - r7
            if (r6 < r4) goto L_0x005a
        L_0x0045:
            int[] r6 = r5.mRepeatPosition
            r6[r2] = r7
            goto L_0x0059
        L_0x004a:
            if (r7 < 0) goto L_0x005a
            int[] r6 = r5.mRepeatPosition
            r6[r3] = r7
            r6[r2] = r0
            goto L_0x0059
        L_0x0053:
            int[] r6 = r5.mRepeatPosition
            r6[r3] = r0
            r6[r2] = r0
        L_0x0059:
            r3 = r2
        L_0x005a:
            if (r3 == 0) goto L_0x0064
            r6 = 2013(0x7dd, float:2.821E-42)
            int r7 = r5.mRepeatMode
            r5.notifyObservers(r6, r7, r0)
            goto L_0x0069
        L_0x0064:
            int r6 = r5.mRepeatMode
            int r6 = r6 - r2
            r5.mRepeatMode = r6
        L_0x0069:
            int r6 = r5.mRepeatMode
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.Player.setRepeatMode(int, int):int");
    }

    public int setRepeatMode(int i) {
        if (this.mMediaPlayer != null) {
            return setRepeatMode(i, getPosition());
        }
        Log.m22e(TAG, "setRepeatMode mMediaPlayer is null !!");
        this.mRepeatMode = 2;
        return this.mRepeatMode;
    }

    public void setRepeatTime(int i, int i2) {
        Log.m26i(TAG, "setRepeatTime - from : " + i + " to : " + i2);
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
        Log.m26i(TAG, "setPlaySpeed - speed : " + f);
        if (this.mMediaPlayer == null) {
            Log.m22e(TAG, "setPlaySpeed MediaPlayer is null !!!");
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
        if (Settings.getBooleanSettings(Settings.KEY_PLAY_CONTINUOUSLY, false)) {
            this.mSpeedInContinuePlayMode = f;
        }
        try {
            if (this.mMediaPlayerState == 3) {
                this.mMediaPlayer.setPlaybackParams(this.mMediaPlayer.getPlaybackParams().setSpeed(f));
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
        Log.m26i(TAG, "getSkipSilenceMode - mode : " + this.mSkipSilenceMode);
        return this.mSkipSilenceMode;
    }

    private static class PlayerHandler extends Handler {
        Message mMessage = null;
        WeakReference<Player> mWeakRefPlayer;

        PlayerHandler(Player player) {
            this.mWeakRefPlayer = new WeakReference<>(player);
        }

        /* access modifiers changed from: package-private */
        public void setMessage(Message message) {
            this.mMessage = message;
        }

        /* access modifiers changed from: package-private */
        public Message getMessage() {
            return this.mMessage;
        }

        public void handleMessage(Message message) {
            Player player = (Player) this.mWeakRefPlayer.get();
            this.mMessage = null;
            if (player != null) {
                int i = message.what;
                if (i == 2) {
                    player.switchSkipSilenceMode(true, message.getData());
                } else if (i == 2016) {
                    player.notifyObservers(2016, 0, -1);
                } else if (i == Player.STOP_PLAY_INTERNAL_WITH_FADE_OUT) {
                    player.stopPlayInternal();
                }
            }
        }
    }

    private static class SoundHandler extends Handler {
        float mCurrentVolumeL = 1.0f;
        float mCurrentVolumeR = 1.0f;
        WeakReference<Player> mPlayer;

        SoundHandler(Player player) {
            this.mPlayer = new WeakReference<>(player);
        }

        public void handleMessage(Message message) {
            Player player = (Player) this.mPlayer.get();
            if (player != null) {
                switch (message.what) {
                    case Player.FADE_DOWN /*4010*/:
                        this.mCurrentVolumeL -= 0.05f;
                        this.mCurrentVolumeR -= 0.05f;
                        if (this.mCurrentVolumeL <= 0.2f || this.mCurrentVolumeR <= 0.2f) {
                            this.mCurrentVolumeL = 0.2f;
                            this.mCurrentVolumeR = 0.2f;
                        } else {
                            removeMessages(Player.FADE_DOWN);
                            sendEmptyMessageDelayed(Player.FADE_DOWN, 10);
                        }
                        player.setVolume(this.mCurrentVolumeL, this.mCurrentVolumeR);
                        return;
                    case Player.FADE_UP /*4011*/:
                        this.mCurrentVolumeL += 0.01f;
                        this.mCurrentVolumeR += 0.01f;
                        if (this.mCurrentVolumeL >= 1.0f || this.mCurrentVolumeR >= 1.0f) {
                            this.mCurrentVolumeL = 1.0f;
                            this.mCurrentVolumeR = 1.0f;
                        } else {
                            removeMessages(Player.FADE_UP);
                            sendEmptyMessageDelayed(Player.FADE_UP, 10);
                        }
                        player.setVolume(this.mCurrentVolumeL, this.mCurrentVolumeR);
                        return;
                    case Player.RECOVER /*4012*/:
                        this.mCurrentVolumeL = (float) message.arg1;
                        this.mCurrentVolumeR = (float) message.arg2;
                        player.setVolume(this.mCurrentVolumeL, this.mCurrentVolumeR);
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
        Log.m19d(TAG, "renamePath : " + str);
        this.mPath = str;
    }

    public long getID() {
        return this.mID;
    }

    public void setMute(boolean z, boolean z2) {
        if (this.mMediaPlayer != null) {
            try {
                Log.m29v(TAG, "setMute - left : " + z + " right : " + z2);
                this.mLeftMute = z;
                this.mRightMute = z2;
                MediaPlayer mediaPlayer = this.mMediaPlayer;
                float f = 0.0f;
                float f2 = z ? 0.0f : 1.0f;
                if (!z2) {
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

    public int enableSkipSilenceMode(int i) {
        Log.m26i(TAG, "enableSkipSilenceMode - current mode : " + this.mSkipSilenceMode + " new mode : " + i + " - mState : " + this.mState);
        if (this.mState == 1) {
            Log.m32w(TAG, "enableSkipSilenceMode - state is idle");
            this.mSkipSilenceMode = 4;
            notifyObservers(2014, this.mSkipSilenceMode, -1);
            return 4;
        } else if (this.mSkipSilenceMode == i) {
            Log.m32w(TAG, "enableSkipSilenceMode - mode is not changed");
            return i;
        } else if (i == 1 || (!isRepeatActivated() && !isPlaySpeedActivated() && !this.mIsDisableSkipMutedForCall)) {
            MetadataRepository instance = MetadataRepository.getInstance();
            if (i == 1) {
                int i2 = this.mSkipSilenceMode;
                if (i2 == 2) {
                    switchSkipSilenceMode((SpeechTimeDataTreeSet) null);
                } else if (i2 == 3) {
                    switchSkipSilenceMode(false, (Bundle) null);
                }
            } else if (i == 2) {
                Log.m29v(TAG, "enableSkipSilenceMode - top : " + instance.isEnabledPerson(180.0f) + " bottom : " + instance.isEnabledPerson(0.0f));
                switchSkipSilenceMode(instance.getPlaySection());
            } else if (i == 3) {
                switchSkipSilenceMode(false, (Bundle) null);
            } else if (i == 4) {
                int i3 = this.mSkipSilenceMode;
                if (i3 == 2) {
                    switchSkipSilenceMode((SpeechTimeDataTreeSet) null);
                } else if (i3 == 3) {
                    switchSkipSilenceMode(false, (Bundle) null);
                }
            }
            this.mSkipSilenceMode = i;
            notifyObservers(2014, this.mSkipSilenceMode, -1);
            return this.mSkipSilenceMode;
        } else {
            Log.m29v(TAG, "enableSkipSilenceMode - repeat or playSpeed or mPausedByCall is activated");
            return this.mSkipSilenceMode;
        }
    }

    public void switchSkipSilenceMode(SpeechTimeDataTreeSet speechTimeDataTreeSet) {
        if (this.mMediaPlayer == null) {
            Log.m22e(TAG, "switchSkipSilenceMode MediaPlayer is null !!!");
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

    public void updateTrack() {
        SkipSilenceTask skipSilenceTask = this.mSkipSilenceTask;
        if (skipSilenceTask != null) {
            skipSilenceTask.updatePlaySection(MetadataRepository.getInstance().getPlaySection());
            this.mSkipSilenceTask.refreshUpcomingSectionsList(getPosition());
        }
    }

    public void switchSkipSilenceMode(boolean z, Bundle bundle) {
        Log.m26i(TAG, "switchSkipSilenceMode isPrepared : " + z);
        if (!z) {
            if (this.mMediaPlayer == null) {
                Log.m22e(TAG, "MediaPlayer is null !!");
                return;
            }
            this.mIsRunningSwitchSkipMuted = true;
            Message message = new Message();
            message.what = 2;
            Bundle bundle2 = new Bundle();
            bundle2.putLong(DialogFactory.BUNDLE_ID, this.mID);
            bundle2.putInt("currentPos", getPosition());
            bundle2.putInt("state", (!this.mPausedByCall || this.mState != 4) ? this.mState : 3);
            message.setData(bundle2);
            stopPlayInternal();
            PlayerHandler playerHandler = this.mHandler;
            if (playerHandler != null) {
                playerHandler.sendMessageDelayed(message, 150);
            }
        } else if (bundle != null) {
            if (getPlayerState() != 1 || VoiceNoteApplication.getScene() == 4) {
                if (!PhoneStateProvider.getInstance().isCallIdle(this.mAppContext) && this.mHandler != null) {
                    Message message2 = new Message();
                    message2.what = 2;
                    message2.setData(bundle);
                    this.mHandler.setMessage(message2);
                }
                reStartPlay(bundle.getLong(DialogFactory.BUNDLE_ID), bundle.getInt("state"), bundle.getInt("currentPos"));
                this.mIsRunningSwitchSkipMuted = false;
            }
        }
    }

    private void resetFadedVolume() {
        if (this.mIsFadeDown) {
            this.mIsFadeDown = false;
            int i = this.mState;
            if (i == 3) {
                this.mSoundHandler.removeMessages(FADE_DOWN);
                this.mSoundHandler.sendEmptyMessage(FADE_UP);
            } else if (i == 4) {
                float f = 0.0f;
                float f2 = this.mLeftMute ? 0.0f : 1.0f;
                if (!this.mRightMute) {
                    f = 1.0f;
                }
                setVolume(f2, f);
            }
        }
    }

    public void setMonoMode(boolean z) {
        if (this.mAudioManager != null) {
            Log.m26i(TAG, "setMonoMode - value : " + z);
            AudioManager audioManager = this.mAudioManager;
            audioManager.setParameters("g_effect_to_mono_enable=" + z);
        }
    }

    public void seekTo(int i) {
        Log.m26i(TAG, "seekTo : " + i);
        if (seek(i)) {
            SkipSilenceTask skipSilenceTask = this.mSkipSilenceTask;
            if (skipSilenceTask != null) {
                skipSilenceTask.refreshUpcomingSectionsList(i);
            }
            notifyObservers(2012, i, -1);
        }
    }

    public void playComplete() {
        Log.m26i(TAG, "playComplete");
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            setMediaPlayerState(4);
            onCompletion((MediaPlayer) null);
        }
    }

    public void setVolume(float f, float f2) {
        Log.m26i(TAG, String.format(Locale.US, "setVolume L : %f / R : %f", new Object[]{Float.valueOf(f), Float.valueOf(f2)}));
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
            Log.m22e(TAG, "getDuration exception : " + e);
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

    /* access modifiers changed from: package-private */
    public void setPausedByCall(boolean z) {
        this.mPausedByCall = z;
    }

    /* access modifiers changed from: private */
    public boolean requestAudioFocus() {
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

    public /* synthetic */ void lambda$new$1$Player(int i) {
        Log.m26i(TAG, "onAudioFocusChange - focusChange : " + i);
        if (i != -3) {
            if (i != -2) {
                if (i == -1) {
                    notifyObservers(2017, -1, -1);
                    if (Decoder.getInstance().getTranslationState() == 1) {
                        int i2 = this.mState;
                        if (i2 == 3) {
                            releasePreferredDevice();
                            pausePlay();
                            if (VoiceNoteService.Helper.connectionCount() == 0) {
                                VoiceNoteApplication.saveEvent(Event.PLAY_PAUSE);
                            } else {
                                notifyObservers(1016, -1, -1);
                            }
                            VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(VoiceNoteApplication.convertEvent(Event.PLAY_PAUSE)));
                        } else if (i2 == 4) {
                            this.mIsPausedForaWhile = false;
                        }
                    } else if (this.mState == 3) {
                        Engine.getInstance().pauseTranslation(false);
                        if (VoiceNoteService.Helper.connectionCount() == 0) {
                            VoiceNoteApplication.saveEvent(Event.TRANSLATION_PAUSE);
                        } else {
                            notifyObservers(1016, -1, -1);
                        }
                        VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(VoiceNoteApplication.convertEvent(Event.TRANSLATION_PAUSE)));
                    }
                } else if (i == 1) {
                    Log.m19d(TAG, "mAudioFocusListener: mPausedByCall = " + this.mPausedByCall);
                    if (this.mState == 4 && (this.mPausedByCall || this.mIsPausedForaWhile)) {
                        this.mPausedByCall = false;
                        this.mIsPausedForaWhile = false;
                        if (!isSkipSilenceActivated()) {
                            enableSkipSilenceMode(4);
                        } else {
                            enableSkipSilenceMode(this.mSkipSilenceMode);
                        }
                        if (Engine.getInstance().getTranslationState() == 1) {
                            if (VoiceNoteApplication.getScene() != 12) {
                                if (Engine.getInstance().isPlayWithReceiver()) {
                                    new Handler() {
                                        public void handleMessage(Message message) {
                                            int i = message.what;
                                            if (i == 1) {
                                                Player.this.releasePreferredDevice();
                                                sendEmptyMessageDelayed(2, 200);
                                            } else if (i == 2) {
                                                Player.this.resumePlayByFocusGain();
                                            }
                                            super.handleMessage(message);
                                        }
                                    }.sendEmptyMessageDelayed(1, 0);
                                } else {
                                    resumePlayByFocusGain();
                                }
                            }
                        } else if (Engine.getInstance().resumeTranslation() == 0) {
                            if (VoiceNoteService.Helper.connectionCount() == 0) {
                                VoiceNoteApplication.saveEvent(Event.TRANSLATION_RESUME);
                            } else {
                                notifyObservers(1015, -1, -1);
                            }
                        }
                    } else if (this.mIsFadeDown) {
                        this.mIsFadeDown = false;
                        int i3 = this.mState;
                        if (i3 == 3) {
                            this.mSoundHandler.removeMessages(FADE_DOWN);
                            this.mSoundHandler.sendEmptyMessage(FADE_UP);
                        } else if (i3 == 4) {
                            setVolume(1.0f, 1.0f);
                        }
                    }
                    PlayerHandler playerHandler = this.mHandler;
                    if (!(playerHandler == null || playerHandler.getMessage() == null)) {
                        if (this.mHandler.getMessage().getData().getInt("state") == 3) {
                            VoiceNoteApplication.saveEvent(Event.PLAY_RESUME);
                        }
                        PlayerHandler playerHandler2 = this.mHandler;
                        playerHandler2.sendMessageDelayed(playerHandler2.getMessage(), 400);
                    }
                    if (!isSkipSilenceActivated()) {
                        return;
                    }
                    if (VoiceNoteApplication.getScene() == 6 || VoiceNoteApplication.getScene() == 12) {
                        enableSkipSilenceMode(4);
                    }
                }
            } else if (this.mState == 3) {
                this.mIsPausedForaWhile = true;
                if (Decoder.getInstance().getTranslationState() == 1) {
                    pausePlay();
                    if (VoiceNoteService.Helper.connectionCount() == 0) {
                        VoiceNoteApplication.saveEvent(Event.PLAY_PAUSE);
                        return;
                    }
                    return;
                }
                Engine.getInstance().pauseTranslation(false);
                if (VoiceNoteService.Helper.connectionCount() == 0) {
                    VoiceNoteApplication.saveEvent(Event.TRANSLATION_PAUSE);
                }
            }
        } else if (this.mState == 3 && Engine.getInstance().getTranslationState() == 1) {
            Log.m19d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK : fade down until volume 20%");
            this.mIsFadeDown = true;
            this.mSoundHandler.removeMessages(FADE_UP);
            this.mSoundHandler.sendEmptyMessage(FADE_DOWN);
        }
    }

    private AudioFocusRequest getAudioFocusRequest() {
        if (this.mAudioFocusRequest == null) {
            this.mAudioFocusRequest = new AudioFocusRequest.Builder(1).setAudioAttributes(new AudioAttributes.Builder().setLegacyStreamType(3).build()).setAcceptsDelayedFocusGain(true).setOnAudioFocusChangeListener(this.mAudioFocusListener).build();
        }
        return this.mAudioFocusRequest;
    }

    /* access modifiers changed from: private */
    public void resumePlayByFocusGain() {
        if (resumePlay() != 0) {
            return;
        }
        if (VoiceNoteService.Helper.connectionCount() == 0) {
            VoiceNoteApplication.saveEvent(Event.PLAY_RESUME);
        } else {
            notifyObservers(1015, -1, -1);
        }
    }

    public void releasePreferredDevice() {
        Log.m26i(TAG, "releasePreferredDevice");
        if (this.mIsPlayWithReceiver) {
            if (this.mMediaPlayer == null) {
                Log.m32w(TAG, "MediaPlayer is null!!!");
                return;
            }
            AudioDeviceInfo audioDeviceInfo = this.mAudioDeviceInfo;
            if (audioDeviceInfo != null && audioDeviceInfo.getType() == 1) {
                Log.m26i(TAG, "Release preferred device");
                this.mAudioDeviceInfo = null;
                this.mMediaPlayer.setPreferredDevice(this.mAudioDeviceInfo);
            }
        }
    }

    public void onDestroy() {
        unregisterAllListener();
        this.mAppContext = null;
        this.mMediaPlayer = null;
        this.mAudioManager = null;
        this.mAudioFocusRequest = null;
    }

    public boolean isValidMediaFile(String str) {
        Log.m26i(TAG, "isValidMediaFile");
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(str);
            mediaPlayer.prepare();
            mediaPlayer.reset();
            mediaPlayer.release();
            return true;
        } catch (IOException e) {
            Log.m24e(TAG, "IOException !", (Throwable) e);
            mediaPlayer.reset();
            mediaPlayer.release();
            return false;
        } catch (IllegalStateException e2) {
            Log.m24e(TAG, "IllegalStateException", (Throwable) e2);
            mediaPlayer.reset();
            mediaPlayer.release();
            return false;
        } catch (Throwable th) {
            mediaPlayer.reset();
            mediaPlayer.release();
            throw th;
        }
    }

    public boolean isIsRunningSwitchSkipMuted() {
        return this.mIsRunningSwitchSkipMuted;
    }

    public void resetPauseNotByUser() {
        this.mPausedByCall = false;
    }

    public boolean isPlayWithReceiver() {
        return this.mIsPlayWithReceiver;
    }

    public void setPlayWithReceiver(boolean z) {
        this.mIsPlayWithReceiver = z;
    }

    public void setStopPlayWithFadeOut(boolean z) {
        this.mIsStopPlayWithFadeOut = z;
    }

    private void createVolumeShapeConfig(float f) {
        Log.m26i(TAG, "create volume shaper config - current volume: " + f);
        if (this.mVolumeShaperConfig == null) {
            this.mVolumeShaperConfig = new VolumeShaper.Configuration.Builder().setDuration(500).setCurve(new float[]{0.0f, 1.0f}, new float[]{f, 0.0f}).setInterpolatorType(1).build();
        }
    }

    private void createVolumeShaper(MediaPlayer mediaPlayer) {
        if (mediaPlayer == null) {
            Log.m22e(TAG, "MediaPlayer is null!!!");
        }
        if (this.mVolumeShaper == null) {
            this.mVolumeShaper = mediaPlayer.createVolumeShaper(this.mVolumeShaperConfig);
        }
    }

    private void stopPlayInternalWithFadeOut() {
        VolumeShaper volumeShaper = this.mVolumeShaper;
        if (volumeShaper != null) {
            volumeShaper.apply(VolumeShaper.Operation.PLAY);
            this.mHandler.removeMessages(STOP_PLAY_INTERNAL_WITH_FADE_OUT);
            this.mHandler.sendEmptyMessageDelayed(STOP_PLAY_INTERNAL_WITH_FADE_OUT, 500);
        }
    }

    private void clearVolumeShaper() {
        Log.m19d(TAG, "clearVolumeShaper");
        VolumeShaper volumeShaper = this.mVolumeShaper;
        if (volumeShaper != null) {
            volumeShaper.close();
            this.mVolumeShaper = null;
        }
        if (this.mVolumeShaperConfig != null) {
            this.mVolumeShaperConfig = null;
        }
        this.mIsStopPlayWithFadeOut = false;
    }

    private float getCurrentVolume() {
        return this.mVolumeShaper.getVolume();
    }
}
