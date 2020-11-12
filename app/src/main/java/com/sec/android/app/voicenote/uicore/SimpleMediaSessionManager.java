package com.sec.android.app.voicenote.uicore;

import android.content.Context;
import android.content.Intent;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import androidx.annotation.NonNull;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.SimpleEngine;
import com.sec.android.app.voicenote.service.SimpleEngineManager;
import java.lang.ref.WeakReference;

public class SimpleMediaSessionManager {
    private static final int ACTION_DOUBLE_CLICK = 2;
    private static final int ACTION_SHORT_PRESS = 1;
    private static final int DOUBLE_CLICK_DELAY = 300;
    private static final String TAG = "SimpleMediaSessionManager";
    /* access modifiers changed from: private */
    public static int mDoubleClick;
    private static SimpleMediaSessionManager mInstance;
    private Context mAppContext;
    /* access modifiers changed from: private */
    public Handler mButtonHandler = new ButtonHandler(this);
    /* access modifiers changed from: private */
    public SimpleEngine mEngine;
    private MediaSession mMediaSession;
    private PlaybackState.Builder mMediaStateBuilder;
    private String mSession;

    static /* synthetic */ int access$108() {
        int i = mDoubleClick;
        mDoubleClick = i + 1;
        return i;
    }

    private SimpleMediaSessionManager() {
        Log.m19d(TAG, "MediaSessionManager creator !!");
    }

    public static SimpleMediaSessionManager getInstance() {
        if (mInstance == null) {
            mInstance = new SimpleMediaSessionManager();
        }
        return mInstance;
    }

    public void setApplicationContext(Context context) {
        this.mAppContext = context;
    }

    public void createMediaSession() {
        Context context = this.mAppContext;
        if (context == null) {
            Log.m22e(TAG, "createMediaSession - mAppContext is NULL !!");
        } else if (this.mMediaSession == null) {
            this.mMediaSession = new MediaSession(context, TAG);
            this.mMediaSession.setCallback(new MediaSessionCallback());
            this.mMediaStateBuilder = new PlaybackState.Builder();
//            if (Build.VERSION.SEM_INT >= 2601) {
//                this.mMediaSession.setFlags(268435456);
//            } else {
//                this.mMediaSession.setFlags(3);
//            }
            this.mMediaStateBuilder.setState(0, 0, 0.0f);
            this.mMediaSession.setActive(false);
            this.mMediaSession.setPlaybackState(this.mMediaStateBuilder.build());
        }
    }

    public void updateMediaSessionState(int i, long j, float f, String str) {
        if (this.mMediaSession != null) {
            if (this.mSession == null || str == null || !SimpleEngineManager.getInstance().isContainEngineSession(this.mSession) || SimpleEngineManager.getInstance().getEngine(this.mSession).getPlayerState() != 3 || SimpleEngineManager.getInstance().getEngine(str).getPlayerState() != 4) {
                String str2 = this.mSession;
                if (str2 == null || !str2.equals(str)) {
                    this.mEngine = SimpleEngineManager.getInstance().getEngine(str);
                    this.mSession = str;
                }
                this.mMediaStateBuilder.setState(i, j, f);
                if (i == 3 || i == 2) {
                    this.mMediaSession.setActive(true);
                    Log.m26i(TAG, "updateMediaSessionState - state : " + i + " setActive true");
                } else {
                    this.mMediaSession.setActive(false);
                    Log.m26i(TAG, "updateMediaSessionState - state : " + i + " setActive false");
                }
                this.mMediaSession.setPlaybackState(this.mMediaStateBuilder.build());
            }
        }
    }

    private static class ButtonHandler extends Handler {
        WeakReference<SimpleMediaSessionManager> mWeakReference;

        ButtonHandler(SimpleMediaSessionManager simpleMediaSessionManager) {
            this.mWeakReference = new WeakReference<>(simpleMediaSessionManager);
        }

        public void handleMessage(Message message) {
            WeakReference<SimpleMediaSessionManager> weakReference = this.mWeakReference;
            if (weakReference != null && weakReference.get() != null) {
                int i = message.what;
                if (i == 1) {
                    int unused = SimpleMediaSessionManager.mDoubleClick = 0;
                } else if (i != 2) {
                    int unused2 = SimpleMediaSessionManager.mDoubleClick = 0;
                } else {
                    if (((SimpleMediaSessionManager) this.mWeakReference.get()).mButtonHandler.hasMessages(1)) {
                        removeMessages(1);
                    }
                    int unused3 = SimpleMediaSessionManager.mDoubleClick = 0;
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
            Log.m26i(SimpleMediaSessionManager.TAG, "onMediaButtonEvent - action : " + keyEvent.getAction() + " key code : " + keyEvent.getKeyCode());
            if (keyEvent.getAction() == 0) {
                SimpleMediaSessionManager.access$108();
                if (SimpleMediaSessionManager.mDoubleClick == 1) {
                    SimpleMediaSessionManager.this.mButtonHandler.sendEmptyMessageDelayed(1, 300);
                }
                int keyCode = keyEvent.getKeyCode();
                if (keyCode == 79 || keyCode == 85) {
                    if (repeatCount == 0 && SimpleMediaSessionManager.mDoubleClick < 2 && SimpleMediaSessionManager.this.mEngine != null) {
                        if (SimpleMediaSessionManager.this.mEngine.getPlayerState() == 3) {
                            SimpleMediaSessionManager.this.mEngine.pausePlay();
                        } else if (SimpleMediaSessionManager.this.mEngine.getPlayerState() == 4) {
                            SimpleMediaSessionManager.this.mEngine.resumePlay();
                        } else if (SimpleMediaSessionManager.this.mEngine.getRecorderState() == 2) {
                            SimpleMediaSessionManager.this.mEngine.pauseRecord();
                        } else if (SimpleMediaSessionManager.this.mEngine.getRecorderState() == 3) {
                            SimpleMediaSessionManager.this.mEngine.resumeRecord();
                        }
                    }
                    if (repeatCount == 0 && SimpleMediaSessionManager.mDoubleClick == 2) {
                        SimpleMediaSessionManager.this.mButtonHandler.sendEmptyMessage(2);
                    }
                } else if ((keyCode == 126 || keyCode == 127) && SimpleMediaSessionManager.this.mEngine != null) {
                    if (SimpleMediaSessionManager.this.mEngine.getPlayerState() == 3) {
                        SimpleMediaSessionManager.this.mEngine.pausePlay();
                    } else if (SimpleMediaSessionManager.this.mEngine.getPlayerState() == 4) {
                        SimpleMediaSessionManager.this.mEngine.resumePlay();
                    } else if (SimpleMediaSessionManager.this.mEngine.getRecorderState() == 2) {
                        SimpleMediaSessionManager.this.mEngine.pauseRecord();
                    } else if (SimpleMediaSessionManager.this.mEngine.getRecorderState() == 3) {
                        SimpleMediaSessionManager.this.mEngine.resumeRecord();
                    }
                }
            }
            return super.onMediaButtonEvent(intent);
        }
    }
}
