package com.sec.android.app.voicenote.uicore;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.VolumeProvider;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.ThreadUtil;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.PhoneStateProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.Player;
import com.sec.android.app.voicenote.service.Recorder;
import com.sec.android.app.voicenote.service.VoiceNoteService;
import com.sec.android.app.voicenote.service.decoder.Decoder;
import com.sec.android.app.voicenote.uicore.KeyIntervalTimer;
import com.sec.android.app.voicenote.uicore.MediaSessionManager;
import java.lang.ref.WeakReference;

public class MediaSessionManager implements Engine.OnEngineListener {
    private static final int ACTION_DOUBLE_CLICK = 2;
    private static final int ACTION_SHORT_PRESS = 1;
    private static final long BLUETOOTH_LONG_KEY_INTERVAL = 400;
    private static final int DOUBLE_CLICK_DELAY = 300;
    private static final String KEY_ADVERTISEMENT = "android.media.metadata.ADVERTISEMENT";
    private static final String TAG = "MediaSessionManager";
    /* access modifiers changed from: private */
    public static int mDoubleClick;
    private static MediaSessionManager mInstance;
    /* access modifiers changed from: private */
    public Context mAppContext;
    /* access modifiers changed from: private */
    public Handler mButtonHandler = new ButtonHandler(this);
    private boolean mIsTranslation = false;
    /* access modifiers changed from: private */
    public KeyIntervalTimer mKeyIntervalTimer;
    /* access modifiers changed from: private */
    public int mLongKeyCnt = 0;
    private MediaSession mMediaSession;
    private PlaybackState.Builder mMediaStateBuilder;

    static /* synthetic */ int access$108() {
        int i = mDoubleClick;
        mDoubleClick = i + 1;
        return i;
    }

    private MediaSessionManager() {
        Log.m19d(TAG, "MediaSessionManager creator !!");
    }

    public static MediaSessionManager getInstance() {
        if (mInstance == null) {
            mInstance = new MediaSessionManager();
        }
        return mInstance;
    }

    public void setApplicationContext(Context context) {
        this.mAppContext = context;
    }

    public /* synthetic */ void lambda$onEngineUpdate$0$MediaSessionManager() {
        getInstance().createMediaSession();
        updateMetadata(true);
        updateMediaSessionState(3, (long) Recorder.getInstance().getDuration(), 0.0f);
        setMediaSessionVolumeProvider(3);
    }

    public /* synthetic */ void lambda$onEngineUpdate$1$MediaSessionManager() {
        getInstance().createMediaSession();
        updateMetadata(false);
        updateMediaSessionState(3, (long) Player.getInstance().getPosition(), 0.0f);
    }

    public void onEngineUpdate(int i, int i2, int i3) {
        if (i != 1010) {
            if (i != 2010) {
                if (i == 2011) {
                    updateMediaSessionState(2, 0, 0.0f);
                }
            } else if (i2 != 3) {
                if (i2 == 4) {
                    updateMediaSessionState(2, (long) Player.getInstance().getPosition(), 0.0f);
                } else if (Recorder.getInstance().getRecorderState() != 2) {
                    setIsTranslation(false);
                    releaseMediaSession();
                }
            } else if (this.mMediaSession == null) {
                ThreadUtil.postOnUiThread(new Runnable() {
                    public final void run() {
                        MediaSessionManager.this.lambda$onEngineUpdate$1$MediaSessionManager();
                    }
                });
            } else {
                updateMetadata(false);
                updateMediaSessionState(3, (long) Player.getInstance().getPosition(), 0.0f);
            }
        } else if (i2 != 2) {
            if (i2 == 3 || i2 == 4) {
                updateMediaSessionState(2, (long) Recorder.getInstance().getDuration(), 0.0f);
                setMediaSessionVolumeProvider(2);
                return;
            }
            setIsTranslation(false);
            releaseMediaSession();
        } else if (this.mMediaSession == null) {
            ThreadUtil.postOnUiThread(new Runnable() {
                public final void run() {
                    MediaSessionManager.this.lambda$onEngineUpdate$0$MediaSessionManager();
                }
            });
        } else {
            updateMetadata(true);
            updateMediaSessionState(3, (long) Recorder.getInstance().getDuration(), 0.0f);
            setMediaSessionVolumeProvider(3);
        }
    }

    public void createMediaSession() {
        Context context = this.mAppContext;
        if (context == null) {
            Log.m22e(TAG, "createMediaSession - mAppContext is NULL !!");
        } else if (this.mMediaSession == null) {
            this.mMediaSession = new MediaSession(context, TAG);
            this.mMediaSession.setCallback(new MediaSessionCallback());
            this.mMediaStateBuilder = new PlaybackState.Builder();
            this.mMediaStateBuilder.setActions(0);
            this.mMediaSession.setPlaybackState(this.mMediaStateBuilder.build());
            this.mMediaSession.setMetadata(new MediaMetadata.Builder().build());
//            if (Build.VERSION.SEM_INT >= 2601) {
//                this.mMediaSession.setFlags(268435456);
//            } else {
//                this.mMediaSession.setFlags(3);
//            }
            Engine.getInstance().registerListener(this);
            initMediaSession();
        }
    }

    public void setIsTranslation(boolean z) {
        this.mIsTranslation = z;
    }

    private void initMediaSession() {
        int playerState = Engine.getInstance().getPlayerState();
        int recorderState = Engine.getInstance().getRecorderState();
        if (playerState == 3 || recorderState == 2) {
            updateMediaSessionState(3, 0, 0.0f);
        } else if (playerState == 4 || recorderState == 3 || recorderState == 4) {
            updateMediaSessionState(2, 0, 0.0f);
        } else {
            updateMediaSessionState(0, 0, 0.0f);
        }
    }

    private void updateMetadata(boolean z) {
        if (z) {
            MediaMetadata.Builder builder = new MediaMetadata.Builder();
            builder.putString("android.media.metadata.TITLE", "");
            builder.putLong("android.media.metadata.DURATION", (long) Recorder.getInstance().getDuration());
            builder.putString("android.media.metadata.WRITER", "recording by samsung voice");
            this.mMediaSession.setMetadata(builder.build());
            this.mMediaStateBuilder.setActions(0);
            this.mMediaSession.setPlaybackState(this.mMediaStateBuilder.build());
            return;
        }
        MediaMetadata.Builder builder2 = new MediaMetadata.Builder();
        String path = Player.getInstance().getPath();
        if (!path.isEmpty()) {
            builder2.putString("android.media.metadata.TITLE", path.substring(path.lastIndexOf(47) + 1, path.lastIndexOf(46)));
            if (this.mIsTranslation) {
                builder2.putLong(KEY_ADVERTISEMENT, 1);
            }
            builder2.putLong("android.media.metadata.DURATION", (long) Player.getInstance().getDuration());
            this.mMediaSession.setMetadata(builder2.build());
            this.mMediaStateBuilder.setActions(632);
            this.mMediaSession.setPlaybackState(this.mMediaStateBuilder.build());
        }
    }

    private void releaseMediaSession() {
        MediaSession mediaSession = this.mMediaSession;
        if (mediaSession != null) {
            mediaSession.setCallback((MediaSession.Callback) null);
            this.mMediaSession.release();
        }
        this.mMediaSession = null;
        this.mMediaStateBuilder = null;
    }

    public void destroyMediaSession() {
        releaseMediaSession();
        Engine.getInstance().unregisterListener(this);
    }

    private void updateMediaSessionState(int i, long j, float f) {
        MediaSession mediaSession = this.mMediaSession;
        if (mediaSession != null) {
            if (i == 3 || i == 2) {
                this.mMediaSession.setActive(true);
                Log.m19d(TAG, "updateMediaSessionState - state : " + i + " setActive true");
            } else {
                mediaSession.setActive(false);
                Log.m19d(TAG, "updateMediaSessionState - state : " + i + " setActive false");
            }
            this.mMediaStateBuilder.setState(i, j, f);
            this.mMediaSession.setPlaybackState(this.mMediaStateBuilder.build());
        }
    }

    private void setMediaSessionVolumeProvider(int i) {
        Log.m19d(TAG, "setMediaSessionVolumeProvider - state : " + i);
        if (this.mMediaSession != null) {
            if (i == 3) {
                this.mMediaSession.setPlaybackToRemote(new VolumeProvider(0, 0, 0) {
                    public void onAdjustVolume(int i) {
                        Log.m26i(MediaSessionManager.TAG, "setMediaSessionVolumeProvider - onAdjustVolume direction : " + i);
                    }
                });
                return;
            }
            this.mMediaSession.setPlaybackToLocal(new AudioAttributes.Builder().setLegacyStreamType(3).build());
        }
    }

    private static class ButtonHandler extends Handler {
        WeakReference<MediaSessionManager> mWeakReference;

        ButtonHandler(MediaSessionManager mediaSessionManager) {
            this.mWeakReference = new WeakReference<>(mediaSessionManager);
        }

        public void handleMessage(Message message) {
            WeakReference<MediaSessionManager> weakReference = this.mWeakReference;
            if (weakReference != null && weakReference.get() != null) {
                int i = message.what;
                if (i == 1) {
                    int unused = MediaSessionManager.mDoubleClick = 0;
                } else if (i != 2) {
                    int unused2 = MediaSessionManager.mDoubleClick = 0;
                } else {
                    if (((MediaSessionManager) this.mWeakReference.get()).mButtonHandler.hasMessages(1)) {
                        removeMessages(1);
                    }
                    int unused3 = MediaSessionManager.mDoubleClick = 0;
                }
            }
        }
    }

    private class MediaSessionCallback extends MediaSession.Callback {
        private MediaSessionCallback() {
        }

        public boolean onMediaButtonEvent(@NonNull Intent intent) {
            KeyEvent keyEvent = (KeyEvent) intent.getParcelableExtra("android.intent.extra.KEY_EVENT");
            if (keyEvent == null) {
                return false;
            }
            int repeatCount = keyEvent.getRepeatCount();
            Log.m26i(MediaSessionManager.TAG, "onMediaButtonEvent - action : " + keyEvent.getAction() + " key code : " + keyEvent.getKeyCode());
            int action = keyEvent.getAction();
            if (action == 0) {
                MediaSessionManager.access$108();
                if (MediaSessionManager.mDoubleClick == 1) {
                    MediaSessionManager.this.mButtonHandler.sendEmptyMessageDelayed(1, 300);
                }
                int keyCode = keyEvent.getKeyCode();
                if (keyCode != 79 && keyCode != 85) {
                    if (keyCode != 126 && keyCode != 127) {
                        switch (keyCode) {
                            case 87:
                                MediaSessionManager.this.doNextPlay();
                                break;
                            case 88:
                                MediaSessionManager.this.doPrevPlay();
                                break;
                            case 89:
                            case 90:
                                if (MediaSessionManager.this.mKeyIntervalTimer == null) {
                                    KeyIntervalTimer unused = MediaSessionManager.this.mKeyIntervalTimer = new KeyIntervalTimer(MediaSessionManager.BLUETOOTH_LONG_KEY_INTERVAL, new KeyIntervalTimer.keyCallback() {
                                        public final void onTick(int i) {
                                            MediaSessionManager.MediaSessionCallback.this.mo15048x66cc8d98(i);
                                        }
                                    });
                                }
                                MediaSessionManager.this.mKeyIntervalTimer.setDownKey(keyEvent.getKeyCode());
                                break;
                        }
                    } else if (!(Engine.getInstance().getScene() == 12 && Engine.getInstance().getTranslationState() == 2 && !Engine.getInstance().isSaveTranslatable())) {
                        if (Engine.getInstance().getPlayerState() == 4) {
                            MediaSessionManager.this.doPlayerResume();
                        } else if (Engine.getInstance().getRecorderState() == 3 || (VoiceNoteApplication.getScene() == 6 && Engine.getInstance().getRecorderState() == 4)) {
                            MediaSessionManager.this.doRecorderResume();
                        } else if (Engine.getInstance().getPlayerState() == 3) {
                            MediaSessionManager.this.doPlayerPause();
                        } else if (Engine.getInstance().getRecorderState() == 2) {
                            MediaSessionManager.this.doRecorderPause();
                        }
                    }
                } else if (!(Engine.getInstance().getScene() == 12 && Engine.getInstance().getTranslationState() == 2 && !Engine.getInstance().isSaveTranslatable())) {
                    if (repeatCount != 0 || MediaSessionManager.mDoubleClick >= 2) {
                        if (repeatCount == 0 && MediaSessionManager.mDoubleClick == 2) {
                            if (Engine.getInstance().getRecorderState() == 1) {
                                MediaSessionManager.this.doNextPlay();
                            }
                            MediaSessionManager.this.mButtonHandler.sendEmptyMessage(2);
                        }
                    } else if (Engine.getInstance().getPlayerState() == 3) {
                        MediaSessionManager.this.doPlayerPause();
                    } else if (Engine.getInstance().getPlayerState() == 4) {
                        if (!(VoiceNoteApplication.getScene() == 6 && VoiceNoteService.Helper.connectionCount() == 0)) {
                            MediaSessionManager.this.doPlayerResume();
                        }
                    } else if (Engine.getInstance().getRecorderState() == 2) {
                        MediaSessionManager.this.doRecorderPause();
                    } else if (Engine.getInstance().getRecorderState() == 3 || ((VoiceNoteApplication.getScene() == 6 || VoiceNoteApplication.getScene() == 8) && Engine.getInstance().getRecorderState() == 4)) {
                        MediaSessionManager.this.doRecorderResume();
                    }
                }
            } else if (action == 1) {
                int keyCode2 = keyEvent.getKeyCode();
                if (keyCode2 == 86) {
                    Engine.getInstance().stopPlay();
                    CursorProvider.getInstance().resetCurrentPlayingItemPosition();
                    if (VoiceNoteService.Helper.connectionCount() == 0) {
                        VoiceNoteApplication.saveEvent(3);
                    }
                    MediaSessionManager.this.mAppContext.sendBroadcast(new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_HIDE_NOTIFICATION));
                } else if (keyCode2 == 89 || keyCode2 == 90) {
                    int unused2 = MediaSessionManager.this.mLongKeyCnt = 0;
                    if (MediaSessionManager.this.mKeyIntervalTimer != null) {
                        MediaSessionManager.this.mKeyIntervalTimer.setUpKey(keyEvent.getKeyCode());
                    }
                }
            }
            return super.onMediaButtonEvent(intent);
        }

        /* renamed from: lambda$onMediaButtonEvent$0$MediaSessionManager$MediaSessionCallback */
        public /* synthetic */ void mo15048x66cc8d98(int i) {
            int i2;
            int i3;
            if (Engine.getInstance().getPlayerState() != 1) {
                if (i == 90) {
                    i2 = 1000;
                } else if (i == 89) {
                    i2 = NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
                } else {
                    return;
                }
                if (MediaSessionManager.this.mLongKeyCnt > 40) {
                    i3 = 4;
                } else {
                    i3 = MediaSessionManager.this.mLongKeyCnt / 10;
                }
                Engine.getInstance().skipInterval(i2 * ((int) Math.pow(2.0d, (double) i3)));
                MediaSessionManager mediaSessionManager = MediaSessionManager.this;
                int unused = mediaSessionManager.mLongKeyCnt = mediaSessionManager.mLongKeyCnt + 10;
            }
        }

        public void onSeekTo(long j) {
            Player.getInstance().seekTo((int) j);
        }
    }

    private void showNetworkNotConnectedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this.mAppContext, C0690R.style.DialogForService));
        builder.setTitle(C0690R.string.no_network_connection);
        builder.setMessage(this.mAppContext.getString(VoiceNoteFeature.FLAG_SUPPORT_CHINA_WLAN ? C0690R.string.no_network_connection_mgs_for_chn : C0690R.string.no_network_connection_mgs));
        builder.setPositiveButton(C0690R.string.f92ok, $$Lambda$MediaSessionManager$yxRTgibKmCE3Kprx4QL_fC93llc.INSTANCE);
        AlertDialog create = builder.create();
        create.getWindow().setType(2014);
        create.show();
    }

    private void notifyEvent(int i) {
        int scene = VoiceNoteApplication.getScene();
        VoiceNoteObservable instance = VoiceNoteObservable.getInstance();
        if (i != 12) {
            if (i != 13) {
                if (i != 21) {
                    if (i != 22) {
                        if (i == 51) {
                            instance.notifyObservers(Integer.valueOf(Event.HIDE_DIALOG));
                        } else if (i == 61) {
                            instance.notifyObservers(Integer.valueOf(Event.TRANSLATION_PAUSE));
                        } else if (i == 62) {
                            instance.notifyObservers(Integer.valueOf(Event.TRANSLATION_RESUME));
                        }
                    } else if (scene == 3) {
                        instance.notifyObservers(Integer.valueOf(Event.MINI_PLAY_RESUME));
                    } else if (scene == 7) {
                        instance.notifyObservers(Integer.valueOf(Event.SEARCH_PLAY_RESUME));
                    }
                } else if (scene == 3) {
                    instance.notifyObservers(Integer.valueOf(Event.MINI_PLAY_PAUSE));
                } else if (scene == 7) {
                    instance.notifyObservers(Integer.valueOf(Event.SEARCH_PLAY_PAUSE));
                }
            } else if (scene == 3) {
                instance.notifyObservers(Integer.valueOf(Event.MINI_PLAY_PREV));
            } else if (scene == 4) {
                instance.notifyObservers(Integer.valueOf(Event.PLAY_STOP));
                instance.notifyObservers(Integer.valueOf(Event.PLAY_PREV));
                instance.notifyObservers(Integer.valueOf(Event.UPDATE_FILE_NAME));
            }
        } else if (scene == 3) {
            instance.notifyObservers(Integer.valueOf(Event.MINI_PLAY_NEXT));
        } else if (scene == 4) {
            instance.notifyObservers(Integer.valueOf(Event.PLAY_STOP));
            instance.notifyObservers(Integer.valueOf(Event.PLAY_NEXT));
            instance.notifyObservers(Integer.valueOf(Event.UPDATE_FILE_NAME));
        }
    }

    /* access modifiers changed from: private */
    public void doNextPlay() {
        if (!Engine.getInstance().isSimplePlayerMode()) {
            if (VoiceNoteApplication.getScene() == 6) {
                Toast.makeText(this.mAppContext, C0690R.string.unable_to_play_while_recording, 0).show();
            } else if (VoiceNoteApplication.getScene() == 12) {
                Log.m26i(TAG, "Not allow play next in the translation scene");
            } else {
                String nextFilePath = CursorProvider.getInstance().getNextFilePath();
                if (nextFilePath != null) {
                    notifyEvent(51);
                    if (VoiceNoteService.Helper.connectionCount() == 0) {
                        if (Engine.getInstance().getRecorderState() != 2 && Engine.getInstance().getRecorderState() != 3) {
                            Engine.getInstance().clearContentItem();
                            int startPlay = Engine.getInstance().startPlay(nextFilePath);
                            if (startPlay == -103) {
                                Toast.makeText(this.mAppContext, C0690R.string.no_play_during_call, 0).show();
                            } else if (startPlay == 0) {
                                Intent intent = new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_UPDATE_NOTIFICATION);
                                intent.putExtra("type", 1);
                                this.mAppContext.sendBroadcast(intent);
                                CursorProvider.getInstance().moveToNextPosition();
                                VoiceNoteApplication.saveEvent(Event.PLAY_NEXT);
                                if (VoiceNoteApplication.getScene() == 4) {
                                    VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.PLAY_NEXT));
                                }
                            }
                        }
                    } else if (VoiceNoteApplication.getScene() == 4 || VoiceNoteApplication.getScene() == 3) {
                        Engine.getInstance().clearContentItem();
                        int startPlay2 = Engine.getInstance().startPlay(nextFilePath);
                        if (startPlay2 == -103) {
                            Toast.makeText(this.mAppContext, C0690R.string.no_play_during_call, 0).show();
                        } else if (startPlay2 == 0) {
                            CursorProvider.getInstance().moveToNextPosition();
                            notifyEvent(12);
                            Engine.getInstance().setCurrentTime(0);
                        }
                    } else {
                        notifyEvent(12);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void doPrevPlay() {
        if (!Engine.getInstance().isSimplePlayerMode()) {
            if (VoiceNoteApplication.getScene() == 6) {
                Toast.makeText(this.mAppContext, C0690R.string.unable_to_play_while_recording, 0).show();
            } else if (VoiceNoteApplication.getScene() == 12) {
                Log.m26i(TAG, "Not allow play prev in the translation scene");
            } else {
                String prevFilePath = CursorProvider.getInstance().getPrevFilePath();
                if (prevFilePath != null) {
                    notifyEvent(51);
                    if (VoiceNoteService.Helper.connectionCount() == 0) {
                        if (Engine.getInstance().getRecorderState() != 2 && Engine.getInstance().getRecorderState() != 3) {
                            Engine.getInstance().clearContentItem();
                            int startPlay = Engine.getInstance().startPlay(prevFilePath);
                            if (startPlay == -103) {
                                Toast.makeText(this.mAppContext, C0690R.string.no_play_during_call, 0).show();
                            } else if (startPlay == 0) {
                                Intent intent = new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_UPDATE_NOTIFICATION);
                                intent.putExtra("type", 1);
                                this.mAppContext.sendBroadcast(intent);
                                CursorProvider.getInstance().moveToPrevPosition();
                                VoiceNoteApplication.saveEvent(Event.PLAY_PREV);
                                if (VoiceNoteApplication.getScene() == 4) {
                                    VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.PLAY_PREV));
                                }
                            }
                        }
                    } else if (VoiceNoteApplication.getScene() == 4 || VoiceNoteApplication.getScene() == 3) {
                        Engine.getInstance().clearContentItem();
                        int startPlay2 = Engine.getInstance().startPlay(prevFilePath);
                        if (startPlay2 == -103) {
                            Toast.makeText(this.mAppContext, C0690R.string.no_play_during_call, 0).show();
                        } else if (startPlay2 == 0) {
                            CursorProvider.getInstance().moveToPrevPosition();
                            notifyEvent(13);
                            Engine.getInstance().setCurrentTime(0);
                        }
                    } else {
                        notifyEvent(13);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void doPlayerResume() {
        if (VoiceNoteApplication.getScene() != 12) {
            int resumePlay = Engine.getInstance().resumePlay();
            if (resumePlay == -103) {
                Toast.makeText(this.mAppContext, C0690R.string.no_play_during_call, 0).show();
            } else if (resumePlay != 0) {
                Log.m32w(TAG, "doPlayerResume return code : " + resumePlay);
            } else {
                if (VoiceNoteService.Helper.connectionCount() == 0) {
                    VoiceNoteApplication.saveEvent(Event.PLAY_RESUME);
                }
                notifyEvent(22);
            }
        } else if (Decoder.getInstance().getTranslationState() == 1) {
            if (VoiceNoteService.Helper.connectionCount() == 0) {
                Log.m32w(TAG, "Translation start does not support in background");
            } else if (!Engine.getInstance().isTranslateable()) {
                Log.m26i(TAG, "Translation cannot start");
            } else {
                VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.TRANSLATION_FROM_HEADSET));
            }
        } else if (Engine.getInstance().isTranslationComplete()) {
            Toast.makeText(this.mAppContext, C0690R.string.unable_to_play, 0).show();
        } else {
            int resumeTranslation = Engine.getInstance().resumeTranslation();
            if (resumeTranslation == -103) {
                Toast.makeText(this.mAppContext, C0690R.string.no_play_during_call, 0).show();
            } else if (resumeTranslation != 0) {
                Log.m32w(TAG, "doPlayerResume return code during STT : " + resumeTranslation);
            } else {
                if (VoiceNoteService.Helper.connectionCount() == 0) {
                    VoiceNoteApplication.saveEvent(Event.TRANSLATION_RESUME);
                }
                notifyEvent(62);
            }
        }
    }

    /* access modifiers changed from: private */
    public void doPlayerPause() {
        if (Decoder.getInstance().getTranslationState() != 1) {
            Engine.getInstance().pauseTranslation(false);
            if (VoiceNoteService.Helper.connectionCount() == 0) {
                VoiceNoteApplication.saveEvent(Event.TRANSLATION_PAUSE);
            }
            notifyEvent(61);
            return;
        }
        Engine.getInstance().pausePlay();
        if (VoiceNoteService.Helper.connectionCount() == 0) {
            VoiceNoteApplication.saveEvent(Event.PLAY_PAUSE);
        }
        notifyEvent(21);
    }

    /* access modifiers changed from: private */
    public void doRecorderPause() {
        if (Engine.getInstance().isSaveEnable() && Engine.getInstance().pauseRecord()) {
            int i = VoiceNoteApplication.getScene() == 6 ? Event.EDIT_RECORD_PAUSE : 1002;
            if (VoiceNoteService.Helper.connectionCount() == 0) {
                VoiceNoteApplication.saveEvent(i);
            }
            VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(i));
        }
    }

    /* access modifiers changed from: private */
    public void doRecorderResume() {
//        ((AudioManager) this.mAppContext.getSystemService("audio")).semDismissVolumePanel();
        int resumeRecord = Engine.getInstance().resumeRecord();
        if (resumeRecord == -120) {
            Toast.makeText(this.mAppContext, C0690R.string.recording_now, 0).show();
            Log.m22e(TAG, "ANOTHER_RECORDER_ALREADY_RUNNING !!!!");
        } else if (resumeRecord != -102) {
            if (resumeRecord != 0) {
                switch (resumeRecord) {
                    case -106:
                        showNetworkNotConnectedDialog();
                        return;
                    case -105:
                        String string = this.mAppContext.getString(C0690R.string.speech_to_text_mode);
                        Toast.makeText(this.mAppContext, this.mAppContext.getString(C0690R.string.mode_is_not_available, new Object[]{string, string}), 0).show();
                        return;
                    case -104:
                        String string2 = this.mAppContext.getString(C0690R.string.interview_mode);
                        Toast.makeText(this.mAppContext, this.mAppContext.getString(C0690R.string.mode_is_not_available, new Object[]{string2, string2}), 0).show();
                        return;
                    default:
                        return;
                }
            } else {
                if (VoiceNoteService.Helper.connectionCount() == 0) {
                    if (VoiceNoteApplication.getScene() == 8) {
                        VoiceNoteApplication.saveEvent(1003);
                    } else if (VoiceNoteApplication.getScene() == 6) {
                        VoiceNoteApplication.saveEvent(Event.EDIT_RECORD);
                    }
                }
                if (VoiceNoteApplication.getScene() == 8) {
                    VoiceNoteObservable.getInstance().notifyObservers(1003);
                } else if (VoiceNoteApplication.getScene() == 6) {
                    VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.EDIT_RECORD));
                }
            }
        } else if (PhoneStateProvider.getInstance().isDuringCall(this.mAppContext)) {
            Toast.makeText(this.mAppContext, C0690R.string.no_rec_during_call, 0).show();
        } else {
            Toast.makeText(this.mAppContext, C0690R.string.no_rec_during_incoming_calls, 0).show();
        }
    }
}
