package com.sec.android.app.voicenote.service.remote;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.main.VNMainActivity;
import com.sec.android.app.voicenote.p007ui.remote.NotiRemoteViewBuilder;
import com.sec.android.app.voicenote.provider.AssistantProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.MetadataRepository;
import com.sec.android.app.voicenote.service.VoiceNoteService;
import com.sec.android.app.voicenote.service.decoder.Decoder;

import java.util.Locale;

import androidx.core.app.NotificationCompat;

public class NotiRemoteViewManager extends AbsRemoteViewManager {
    private static final String CHANNEL_ID = "voice_note_notification_channel";
    private static final String TAG = "NotiRemoteViewManager";
    private static NotiRemoteViewManager mInstance;
    private Context mContext = null;
    private int mCurrentTime = 0;
    private Notification mNotification = null;

    private NotiRemoteViewManager() {
        Log.m19d(TAG, "NotiRemoteViewManager creator !!");
    }

    public static NotiRemoteViewManager getInstance() {
        if (mInstance == null) {
            mInstance = new NotiRemoteViewManager();
        }
        return mInstance;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public void start(int i, int i2, int i3) {
        Context context;
        Log.m26i(TAG, "start() - recordStatus : " + i + " playStatus : " + i2 + " type : " + i3);
        RemoteViews createRemoteView = createRemoteView(i, i2, i3);
        if (createRemoteView == null || (context = this.mContext) == null) {
            Log.m29v(TAG, "start notification remoteViews or mService is null");
            return;
        }
        this.mNotification = createNotification(i, i2, i3, context);
        this.mNotification.contentView = createRemoteView;
        NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService("notification");
        createNotificationChannel(notificationManager);
        if (notificationManager != null) {
            notificationManager.notify(AbsRemoteViewManager.REMOTEVIEWSREQ, this.mNotification);
        }
        try {
            PendingIntent.getBroadcast(this.mContext, AbsRemoteViewManager.REMOTEVIEWSREQ, new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_QUICK_PANEL_SHOW), 0).send();
            ((Service) this.mContext).startForeground(AbsRemoteViewManager.REMOTEVIEWSREQ, this.mNotification);
        } catch (IllegalArgumentException e) {
            Log.m24e(TAG, "IllegalArgumentException", (Throwable) e);
        } catch (PendingIntent.CanceledException e2) {
            Log.m24e(TAG, "CanceledException", (Throwable) e2);
        }
    }

    public void stop(int i) {
        Log.m19d(TAG, "stop() - type : " + i);
        Context context = this.mContext;
        if (context == null) {
            Log.m32w(TAG, "stop - context is null");
            return;
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        if (notificationManager != null) {
            notificationManager.cancel(AbsRemoteViewManager.REMOTEVIEWSREQ);
        }
        try {
            PendingIntent.getBroadcast(this.mContext, AbsRemoteViewManager.REMOTEVIEWSREQ, new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_QUICK_PANEL_HIDE), 0).send();
        } catch (PendingIntent.CanceledException e) {
            Log.m34w(TAG, "CanceledException", (Throwable) e);
        }
        ((Service) this.mContext).stopForeground(true);
        this.mNotification = null;
    }

    public void show(int i, int i2, int i3) {
        Log.m26i(TAG, "show() - recordStatus : " + i + " playStatus : " + i2 + " type : " + i3);
        int recorderState = Engine.getInstance().getRecorderState();
        int playerState = Engine.getInstance().getPlayerState();
        if (!(recorderState == 1 && playerState == 1) && (i3 != 4 || playerState == 1)) {
            start(i, i2, i3);
            return;
        }
        RemoteViewManager.getInstance().enableEngineUpdateForNoti(false);
        hide(i3);
    }

    public void update(int i, int i2, int i3, int i4) {
        if (this.mNotification != null && this.mContext != null) {
//            if (VoiceNoteFeature.FLAG_IS_BLOOM() && SemWindowManager.getInstance().isFolded()) {
//                return;
//            }
            if (RemoteViewManager.getInstance().getIsEnableUpdate() || i4 == 11 || i4 == 12) {
                NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService("notification");
                if (i4 == 2 && i == 2) {
                    RemoteViews buildRemoteView = buildRemoteView(i, i2, i3, C0690R.layout.remoteview_record_recording);
                    if (buildRemoteView == null) {
                        Log.m29v(TAG, "update remoteViews is null");
                        return;
                    }
                    this.mNotification.contentView = buildRemoteView;
                }
                if (i4 == 1) {
                    MetadataRepository instance = MetadataRepository.getInstance();
                    instance.setPath(Engine.getInstance().getPath());
                    this.mNotification.contentView.setTextViewText(C0690R.C0693id.remote_playing_clipname, instance.getTitle());
                } else if (i4 != 2) {
                    switch (i4) {
                        case 7:
                            createNotificationChannel(notificationManager);
                            break;
                        case 8:
                            this.mNotification.contentView.setViewVisibility(C0690R.C0693id.remote_playing_clipname, 8);
                            RemoteViews remoteViews = this.mNotification.contentView;
                            remoteViews.setTextViewText(C0690R.C0693id.remote_translate_converting, this.mContext.getResources().getString(C0690R.string.stt_translation_network_error, new Object[]{Engine.getInstance().getCurrentFileName()}) + "\n" + this.mContext.getString(C0690R.string.stt_translation_network_try_again));
                            break;
                        case 9:
                            this.mNotification.contentView.setViewVisibility(C0690R.C0693id.remote_translation_complete_text, 8);
                            this.mNotification.contentView.setViewVisibility(C0690R.C0693id.remote_translation_saving_text, 0);
                            break;
                        case 10:
                            this.mNotification.contentView.setViewVisibility(C0690R.C0693id.remote_translation_saving_text, 8);
                            this.mNotification.contentView.setViewVisibility(C0690R.C0693id.remote_translation_complete_text, 0);
                            this.mNotification.contentView.setTextViewText(C0690R.C0693id.remote_translation_complete_text, this.mContext.getResources().getString(C0690R.string.stt_translation_complete, new Object[]{Engine.getInstance().getLastSavedFileName()}));
                            break;
                        case 11:
                            this.mNotification.contentView.setViewVisibility(C0690R.C0693id.quick_panel_record_play_pause, 4);
                            break;
                        case 12:
                            this.mNotification.contentView.setViewVisibility(C0690R.C0693id.quick_panel_record_play_pause, 0);
                            break;
                    }
                } else if (i3 == 5) {
                    int progressTime = Decoder.getInstance().getProgressTime();
                    String string = this.mContext.getResources().getString(C0690R.string.stt_translation_converting, new Object[]{Integer.valueOf(progressTime)});
                    this.mNotification.contentView.setViewVisibility(C0690R.C0693id.remote_playing_clipname, 0);
                    this.mNotification.contentView.setTextViewText(C0690R.C0693id.remote_translate_converting, string);
                    this.mNotification.contentView.setProgressBar(C0690R.C0693id.remote_translate_progress, 100, progressTime, false);
                } else {
                    int displayTime = RemoteViewManager.getInstance().getDisplayTime();
                    String changeDurationToTimeText = changeDurationToTimeText((long) displayTime);
                    this.mNotification.contentView.setTextViewText(C0690R.C0693id.quick_panel_time, changeDurationToTimeText);
                    this.mNotification.contentView.setContentDescription(C0690R.C0693id.quick_panel_time, AssistantProvider.getInstance().stringForReadTime(changeDurationToTimeText));
                    this.mCurrentTime = displayTime;
                }
                if (notificationManager != null) {
                    notificationManager.notify(AbsRemoteViewManager.REMOTEVIEWSREQ, this.mNotification);
                }
            }
        }
    }

    public void hide(int i) {
        Log.m19d(TAG, "hide() - type : " + i);
        stop(i);
        this.mContext = null;
        release();
    }

    public static boolean isRunning() {
        return mInstance != null;
    }

    public static void release() {
        NotiRemoteViewBuilder.release();
        mInstance = null;
    }

    public RemoteViews createRemoteView(int i, int i2, int i3) {
        Log.m26i(TAG, "createRemoteView() - recordStatus : " + i + " playStatus : " + i2 + " type : " + i3);
        if (i3 != 1) {
            if (i3 != 2) {
                if (i3 != 4) {
                    if (i3 != 5) {
                        return null;
                    }
                    if (Engine.getInstance().getTranslationState() == 2 || Engine.getInstance().getTranslationState() == 3) {
                        return buildRemoteView(i, i2, i3, C0690R.layout.remoteview_translate);
                    }
                    if (Engine.getInstance().getTranslationState() == 1) {
                        return buildRemoteView(i, i2, i3, C0690R.layout.remoteview_translate_complete);
                    }
                    return null;
                }
            }
            if (i2 != 3 || i2 == 4) {
                return buildRemoteView(i, i2, i3, C0690R.layout.remoteview_player);
            }
            return null;
        }
        this.mCurrentTime = RemoteViewManager.getInstance().getCurrentTime();
        if (i == 3 || i == 4) {
            return buildRemoteView(i, i2, i3, C0690R.layout.remoteview_record_paused);
        }
        if (i == 2) {
            return buildRemoteView(i, i2, i3, C0690R.layout.remoteview_record_recording);
        }
        if (i2 != 3) {
        }
        return buildRemoteView(i, i2, i3, C0690R.layout.remoteview_player);
    }

    public RemoteViews buildRemoteView(int i, int i2, int i3, int i4) {
        NotiRemoteViewBuilder instance = NotiRemoteViewBuilder.getInstance();
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
                instance.setTranslateTextView();
                return instance.build();
            }
        }
        instance.createRecordButtons(i, i2, i3);
        instance.setRecordTextView(i, i2, i3, this.mCurrentTime);
        return instance.build();
    }

    private static String changeDurationToTimeText(long j) {
        int i = (int) j;
        int i2 = i / 3600;
        int i3 = (i / 60) % 60;
        int i4 = i % 60;
        if (i2 > 0) {
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4)});
        }
        return String.format(Locale.getDefault(), "%02d:%02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(i4)});
    }

    private void createNotificationChannel(NotificationManager notificationManager) {
        Log.m26i(TAG, "createNotificationChannel");
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, this.mContext.getResources().getString(C0690R.string.app_name), 3);
        notificationChannel.setSound((Uri) null, Notification.AUDIO_ATTRIBUTES_DEFAULT);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(-16776961);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    /* access modifiers changed from: protected */
    public Notification createNotification(int i, int i2, int i3, Context context) {
        Notification.Builder builder = new Notification.Builder(context, CHANNEL_ID);
        Intent intent = new Intent(context, VNMainActivity.class);
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setFlags(268435456);
        intent.addFlags(67108864);
        builder.setWhen(System.currentTimeMillis());
        builder.setShowWhen(false);
        builder.setOngoing(true);
        builder.setContentIntent(PendingIntent.getActivity(context, 0, intent, 0));
        builder.setTicker(context.getResources().getText(C0690R.string.app_name));
        builder.setSmallIcon(getIcon(i, i3));
        builder.setStyle(new Notification.DecoratedCustomViewStyle());
        builder.setColor(context.getColor(C0690R.C0691color.quick_panel_bg));
        builder.setColorized(true);
        builder.setVisibility(1);
        Notification build = builder.build();
        build.category = NotificationCompat.CATEGORY_PROGRESS;
        return build;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000b, code lost:
        if (r6 != 4) goto L_0x0033;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getIcon(int r5, int r6) {
        /*
            r4 = this;
            r0 = 1
            r1 = 3
            r2 = 2131231085(0x7f08016d, float:1.8078241E38)
            r3 = 2
            if (r6 == r0) goto L_0x001a
            r0 = 4
            if (r6 == r3) goto L_0x000e
            if (r6 == r0) goto L_0x001a
            goto L_0x0033
        L_0x000e:
            if (r5 != r0) goto L_0x0014
            r5 = 2131231087(0x7f08016f, float:1.8078245E38)
            return r5
        L_0x0014:
            if (r5 != r1) goto L_0x0033
            r5 = 2131231088(0x7f080170, float:1.8078247E38)
            return r5
        L_0x001a:
            if (r5 == r1) goto L_0x0033
            boolean r6 = com.sec.android.app.voicenote.provider.VoiceNoteFeature.FLAG_IS_BLOOM()
            if (r6 == 0) goto L_0x002d
            com.samsung.android.view.SemWindowManager r6 = com.samsung.android.view.SemWindowManager.getInstance()
            boolean r6 = r6.isFolded()
            if (r6 == 0) goto L_0x002d
            goto L_0x0033
        L_0x002d:
            if (r5 != r3) goto L_0x0033
            r5 = 2131231084(0x7f08016c, float:1.807824E38)
            return r5
        L_0x0033:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.remote.NotiRemoteViewManager.getIcon(int, int):int");
    }
}
