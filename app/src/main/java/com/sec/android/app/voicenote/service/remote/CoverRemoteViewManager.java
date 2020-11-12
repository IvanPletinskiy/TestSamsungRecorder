package com.sec.android.app.voicenote.service.remote;

import android.content.Context;
import android.widget.RemoteViews;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.AndroidForWork;
import com.sec.android.app.voicenote.p007ui.remote.CoverRemoteViewBuilder;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.MetadataRepository;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;

public class CoverRemoteViewManager extends AbsRemoteViewManager {
    private static final String TAG = "CoverRemoteViewManager";
    private static CoverRemoteViewManager mInstance;
    private Context mContext;
    private int mCoverStatus = 0;
    private int mCurrentTime = 0;
    private boolean mIsRunningInBackground = false;

    public static class CoverStatus {
        public static final int NONE = 0;
        public static final int PLAYING = 3;
        public static final int PLAY_STOP = 4;
        public static final int RECORDING = 1;
        public static final int RECORD_STOP = 2;
    }

    private int getCoverStatus(int i, int i2, int i3) {
        if (i3 != 1) {
            if (i3 == 2) {
                if (i2 != 2) {
                    if (i2 == 3) {
                        return 3;
                    }
                    if (i2 != 4) {
                        return 0;
                    }
                }
                return 4;
            } else if (i3 != 4) {
                return 0;
            }
        }
        if (i != 2) {
            return (i == 3 || i == 4) ? 2 : 0;
        }
        return 1;
    }

    private CoverRemoteViewManager() {
        Log.m26i(TAG, "CoverRemoteViewManager creator !!");
    }

    public static CoverRemoteViewManager getInstance() {
        if (mInstance == null) {
            mInstance = new CoverRemoteViewManager();
        }
        return mInstance;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public void start(int i, int i2, int i3) {
        Log.m26i(TAG, "start() - recordStatus : " + i + " playStatus : " + i2 + " type : " + i3);
        if (i3 == 0) {
            hide(i3);
        } else if (i3 != 1) {
            if (i3 != 2) {
                if (i3 != 4) {
                    if (i3 != 5) {
                        return;
                    }
                } else if (i == 2 || i == 3 || i == 4) {
                    show(i, i2, i3);
                    return;
                } else {
                    hide(i3);
                    return;
                }
            }
            if (i2 == 3 || i2 == 4 || i2 == 2) {
                if (Engine.getInstance().isSimplePlayerMode()) {
                    hide(i3);
                } else {
                    show(i, i2, i3);
                }
            } else if (i2 == 1 && !RemoteViewManager.getInstance().isCoverClosed()) {
                hide(i3);
            }
        } else if (i == 2 || i == 3 || i == 4) {
            show(i, i2, i3);
        } else if (i == 1) {
            hide(i3);
        }
    }

    public void stop(int i) {
        Log.m26i(TAG, "stop() - type : " + i);
    }

    public void show(int i, int i2, int i3) {
        Log.m26i(TAG, "show() - recordStatus : " + i + " playStatus : " + i2 + " type : " + i3);
        if (!isCoverAttachedNeedRemoteView()) {
            return;
        }
        if (getVisibilityForCover(i, i2, i3)) {
            createCoverView(true, i, i2, i3);
        } else {
            createCoverView(false, i, i2, i3);
        }
    }

    public void update(int i, int i2, int i3, int i4) {
        Log.m26i(TAG, "update() - recordStatus : " + i + " playStatus : " + i2 + " type : " + i3 + " updateType : " + i4);
        if (!isCoverAttachedNeedRemoteView()) {
            return;
        }
        if (i3 == 0) {
            hide(i3);
        } else if (i3 == 1 || i3 == 2 || i3 == 4 || i3 == 5) {
            updateCoverView(i, i2, i3, i4);
        }
    }

    public void hide(int i) {
        Log.m26i(TAG, "hide() - type : " + i);
        if (isCoverAttachedNeedRemoteView()) {
            createCoverView(false, 0, 0, i);
        }
        this.mContext = null;
    }

    public RemoteViews createRemoteView(int i, int i2, int i3) {
        Log.m26i(TAG, "createRemoteView() - recordStatus : " + i + " playStatus : " + i2 + " type : " + i3);
        if (i3 != 1) {
            if (i3 == 2) {
                MetadataRepository.getInstance().setPath(Engine.getInstance().getPath());
                return buildRemoteView(i, i2, i3, C0690R.layout.cover_remoteview_player);
            } else if (i3 != 4) {
                if (i3 != 5) {
                    return null;
                }
                return buildRemoteView(i, i2, i3, C0690R.layout.cover_remoteview_translate);
            }
        }
        this.mCurrentTime = Engine.getInstance().getCurrentTime();
        if (i3 == 4 && i2 != 1) {
            return null;
        }
        if (i == 2) {
            return buildRemoteView(i, i2, i3, C0690R.layout.cover_remoteview_recorder_recording);
        }
        if (i != 3 && i != 4) {
            return null;
        }
        this.mCoverStatus = 2;
        return buildRemoteView(i, i2, i3, C0690R.layout.cover_remoteview_recorder_paused);
    }

    public RemoteViews buildRemoteView(int i, int i2, int i3, int i4) {
        CoverRemoteViewBuilder instance = CoverRemoteViewBuilder.getInstance();
        instance.createRemoteview(this.mContext, i4);
        if (i3 != 1) {
            if (i3 == 2) {
                instance.createPlayButtons(i2);
                instance.setPlayTextView();
                return instance.build();
            } else if (i3 != 4) {
                if (i3 != 5) {
                    return null;
                }
                instance.createTranslateButtons(i2);
                instance.setPlayTextView();
                return instance.build();
            }
        }
        instance.createRecordButtons(i, i2, i3);
        instance.setRecordTextView(i, i2, i3, this.mCurrentTime);
        return instance.build();
    }

    private void createCoverView(boolean z, int i, int i2, int i3) {
        Log.m19d(TAG, "createCoverView : " + z);
        if (isNeedNewCoverView(i, i2, i3)) {
            this.mContext.sendBroadcastAsUser(makeCoverIntent(z, i, i2, i3, createRemoteView(i, i2, i3)), AndroidForWork.OWNER);
        }
    }

    private void updateCoverView(int i, int i2, int i3, int i4) {
        Log.m26i(TAG, "updateCoverView() - recorderStatus : " + i + " playStatus : " + i2 + " type : " + i3 + " updateType : " + i4);
        if (isNeedNewCoverView(i, i2, i3)) {
            RemoteViews createRemoteView = createRemoteView(i, i2, i3);
            if (i4 != 1) {
                if (i4 != 2) {
                    if (i4 == 5) {
                        hide(i3);
                    }
                } else if (i3 == 1 || i3 == 2 || i3 == 4 || i3 == 5) {
                    this.mCurrentTime = Engine.getInstance().getCurrentTime();
                    this.mContext.sendBroadcastAsUser(makeCoverIntent(true, i, i2, i3, createRemoteView), AndroidForWork.OWNER);
                }
            } else if (i3 == 2) {
                this.mContext.sendBroadcastAsUser(makeCoverIntent(true, i, i2, i3, createRemoteView), AndroidForWork.OWNER);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0037, code lost:
        if (r3 != 5) goto L_0x00fe;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.content.Intent makeCoverIntent(boolean r17, int r18, int r19, int r20, android.widget.RemoteViews r21) {
        /*
            r16 = this;
            r0 = r16
            r1 = r18
            r2 = r19
            r3 = r20
            r4 = r21
            android.content.Intent r5 = new android.content.Intent
            java.lang.String r6 = "com.samsung.cover.REMOTEVIEWS_UPDATE"
            r5.<init>(r6)
            com.sec.android.app.voicenote.service.remote.RemoteViewManager r6 = com.sec.android.app.voicenote.service.remote.RemoteViewManager.getInstance()
            int r6 = r6.getAttachedCoverType()
            java.lang.String r7 = "type"
            java.lang.String r8 = "voice_recorder"
            r5.putExtra(r7, r8)
            java.lang.String r7 = "CoverRemoteViewManager"
            java.lang.String r8 = "voice_recorder_status"
            java.lang.String r10 = "visibility"
            if (r17 == 0) goto L_0x00ed
            java.lang.String r12 = "makeCoverIntent() - voice_recorder_status : "
            java.lang.String r13 = "remote"
            r15 = 4
            r11 = 2
            r9 = 3
            r14 = 1
            if (r3 == r14) goto L_0x009a
            if (r3 == r11) goto L_0x003b
            if (r3 == r15) goto L_0x009a
            r1 = 5
            if (r3 == r1) goto L_0x003b
            goto L_0x00fe
        L_0x003b:
            java.lang.String r1 = "isPlaying"
            if (r2 != r9) goto L_0x005d
            r0.mCoverStatus = r9
            r5.putExtra(r10, r14)
            r2 = 8
            if (r6 != r2) goto L_0x0050
            r5.putExtra(r13, r4)
            r5.putExtra(r1, r14)
            goto L_0x00fe
        L_0x0050:
            r1 = 7
            if (r6 != r1) goto L_0x00fe
            r5.putExtra(r8, r11)
            java.lang.String r1 = "makeCoverIntent() - voice_recorder_status : 2"
            com.sec.android.app.voicenote.provider.Log.m26i(r7, r1)
            goto L_0x00fe
        L_0x005d:
            if (r2 == r15) goto L_0x0061
            if (r2 != r11) goto L_0x00fe
        L_0x0061:
            r0.mCoverStatus = r15
            r3 = 8
            if (r6 != r3) goto L_0x0073
            r3 = 0
            r5.putExtra(r1, r3)
            r5.putExtra(r10, r14)
            r5.putExtra(r13, r4)
            goto L_0x00fe
        L_0x0073:
            r1 = 7
            r3 = 0
            if (r6 != r1) goto L_0x00fe
            r5.putExtra(r10, r3)
            boolean r1 = com.sec.android.app.voicenote.provider.VoiceNoteFeature.FLAG_IS_SUPPORT_LED_COVER()
            if (r1 == 0) goto L_0x0083
            if (r2 != r15) goto L_0x0083
            goto L_0x0084
        L_0x0083:
            r9 = 0
        L_0x0084:
            r5.putExtra(r8, r9)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r12)
            r1.append(r9)
            java.lang.String r1 = r1.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r7, r1)
            goto L_0x00fe
        L_0x009a:
            if (r1 != r11) goto L_0x00b5
            r0.mCoverStatus = r14
            r5.putExtra(r10, r14)
            r1 = 8
            if (r6 != r1) goto L_0x00a9
            r5.putExtra(r13, r4)
            goto L_0x00fe
        L_0x00a9:
            r1 = 7
            if (r6 != r1) goto L_0x00fe
            r5.putExtra(r8, r14)
            java.lang.String r1 = "makeCoverIntent() - voice_recorder_status : 1"
            com.sec.android.app.voicenote.provider.Log.m26i(r7, r1)
            goto L_0x00fe
        L_0x00b5:
            if (r1 == r9) goto L_0x00b9
            if (r1 != r15) goto L_0x00fe
        L_0x00b9:
            r0.mCoverStatus = r11
            r2 = 8
            if (r6 != r2) goto L_0x00c6
            r5.putExtra(r10, r14)
            r5.putExtra(r13, r4)
            goto L_0x00fe
        L_0x00c6:
            r2 = 7
            if (r6 != r2) goto L_0x00fe
            r2 = 0
            r5.putExtra(r10, r2)
            boolean r2 = com.sec.android.app.voicenote.provider.VoiceNoteFeature.FLAG_IS_SUPPORT_LED_COVER()
            if (r2 == 0) goto L_0x00d6
            if (r1 != r9) goto L_0x00d6
            goto L_0x00d7
        L_0x00d6:
            r9 = 0
        L_0x00d7:
            r5.putExtra(r8, r9)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r12)
            r1.append(r9)
            java.lang.String r1 = r1.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r7, r1)
            goto L_0x00fe
        L_0x00ed:
            r1 = 0
            r0.mCoverStatus = r1
            r5.putExtra(r10, r1)
            r2 = 7
            if (r6 != r2) goto L_0x00fe
            r5.putExtra(r8, r1)
            java.lang.String r1 = "makeCoverIntent() - voice_recorder_status : 0"
            com.sec.android.app.voicenote.provider.Log.m26i(r7, r1)
        L_0x00fe:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.remote.CoverRemoteViewManager.makeCoverIntent(boolean, int, int, int, android.widget.RemoteViews):android.content.Intent");
    }

    private boolean getVisibilityForCover(int i, int i2, int i3) {
        if (i3 != 1) {
            if (i3 == 2) {
                i = Engine.getInstance().getRecorderState();
            } else if (i3 != 4) {
                i = Engine.getInstance().getRecorderState();
                i2 = Engine.getInstance().getPlayerState();
            }
        }
        int editorState = Engine.getInstance().getEditorState();
        int scene = VoiceNoteApplication.getScene();
        if (this.mContext != null) {
            if (scene == 6 && i2 == 1) {
                return true;
            }
            if ((scene == 6 || i2 == 1) && i == 1 && editorState == 1) {
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean isCoverAttachedNeedRemoteView() {
        int attachedCoverType = RemoteViewManager.getInstance().getAttachedCoverType();
        return (this.mContext != null && attachedCoverType == 8) || attachedCoverType == 7;
    }

    private boolean isNeedNewCoverView(int i, int i2, int i3) {
        if (this.mContext == null) {
            Log.m19d(TAG, "isNeedNewCoverView - mContextlabel is null");
            return false;
        }
        if (getCoverStatus(i, i2, i3) == this.mCoverStatus) {
            MetadataRepository instance = MetadataRepository.getInstance();
            if (i3 == 1 || i3 == 4) {
                instance.setPath(Engine.getInstance().getRecentFilePath());
            } else {
                instance.setPath(Engine.getInstance().getPath());
            }
            if (instance.getTitle() == null) {
                Log.m19d(TAG, "isNeedNewCoverView - metadata title is wrong");
                return false;
            }
        }
        return true;
    }

    public void setRunningInBackground(boolean z) {
        this.mIsRunningInBackground = z;
    }
}
