package com.sec.android.app.voicenote.p007ui.remote;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.provider.AssistantProvider;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.MetadataRepository;
import com.sec.android.app.voicenote.service.VoiceNoteService;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import java.util.Locale;

/* renamed from: com.sec.android.app.voicenote.ui.remote.AbsRemoteViewBuilder */
public abstract class AbsRemoteViewBuilder {
    private static final int REMOTEVIEWSREQ = 117506050;
    protected Context mContext;
    protected int mCurrentTime = 0;
    protected int mDisplayTime = -1;
    private String mTitleText = null;

    public abstract RemoteViews build();

    public abstract void setRemoteView(RemoteViews remoteViews);

    public Context getContext() {
        return this.mContext;
    }

    public void createRemoteview(Context context, int i) {
        this.mContext = context;
        if (this.mContext == null) {
            this.mContext = VoiceNoteApplication.getApplication().getApplicationContext();
        }
        setRemoteView(new RemoteViews(this.mContext.getPackageName(), i));
    }

    public void createRecordButtons(int i, int i2, int i3) {
        build().setContentDescription(C0690R.C0693id.remote_save_button, AssistantProvider.getInstance().getButtonDescriptionForTalkback(C0690R.string.stop));
    }

    public void createPlayButtons(int i) {
        RemoteViews build = build();
        build.setContentDescription(C0690R.C0693id.remote_prev_button, AssistantProvider.getInstance().getButtonDescriptionForTalkback(C0690R.string.previous));
        build.setContentDescription(C0690R.C0693id.remote_next_button, AssistantProvider.getInstance().getButtonDescriptionForTalkback(C0690R.string.next));
        build.setContentDescription(C0690R.C0693id.remote_quit_button, AssistantProvider.getInstance().getButtonDescriptionForTalkback(C0690R.string.cancel));
    }

    public void createTranslateButtons(int i) {
        RemoteViews build = build();
        build.setContentDescription(C0690R.C0693id.remote_translate_save_button, AssistantProvider.getInstance().getButtonDescriptionForTalkback(C0690R.string.save));
        build.setContentDescription(C0690R.C0693id.remote_translate_cancel_button, AssistantProvider.getInstance().getButtonDescriptionForTalkback(C0690R.string.cancel));
    }

    public void setRecordTextView(int i, int i2, int i3, int i4) {
        this.mCurrentTime = i4;
    }

    public void setPlayTextView() {
        RemoteViews build = build();
        MetadataRepository instance = MetadataRepository.getInstance();
        instance.setPath(Engine.getInstance().getPath());
        build.setTextViewText(C0690R.C0693id.remote_playing_clipname, instance.getTitle());
        this.mTitleText = instance.getTitle();
    }

    public void setTranslateTextView() {
        RemoteViews build = build();
        MetadataRepository instance = MetadataRepository.getInstance();
        instance.setPath(Engine.getInstance().getPath());
        build.setTextViewText(C0690R.C0693id.remote_playing_clipname, instance.getTitle());
        this.mTitleText = instance.getTitle();
    }

    public String getTitle() {
        return this.mTitleText;
    }

    /* access modifiers changed from: protected */
    public void setSavePendingIntent(RemoteViews remoteViews, int i) {
        remoteViews.setOnClickPendingIntent(i, PendingIntent.getBroadcast(this.mContext, 117506050, new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_SAVE), 0));
    }

    /* access modifiers changed from: protected */
    public void setCancelPendingIntent(RemoteViews remoteViews, int i) {
        remoteViews.setOnClickPendingIntent(i, PendingIntent.getBroadcast(this.mContext, 117506050, new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_CANCEL_KEYGUARD), 0));
    }

    /* access modifiers changed from: protected */
    public void setQuitPendingIntent(RemoteViews remoteViews, int i) {
        remoteViews.setOnClickPendingIntent(i, PendingIntent.getBroadcast(this.mContext, 117506050, new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_PLAY_STOP_QUIT), 0));
    }

    /* access modifiers changed from: protected */
    public void setPrevPendingIntent(RemoteViews remoteViews, int i) {
        remoteViews.setOnClickPendingIntent(i, PendingIntent.getBroadcast(this.mContext, 117506050, new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_PLAY_PREV), 0));
    }

    /* access modifiers changed from: protected */
    public void setNextPendingIntent(RemoteViews remoteViews, int i) {
        remoteViews.setOnClickPendingIntent(i, PendingIntent.getBroadcast(this.mContext, 117506050, new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_PLAY_NEXT), 0));
    }

    /* access modifiers changed from: protected */
    public void setPlayerPendingIntent(RemoteViews remoteViews, int i) {
        remoteViews.setOnClickPendingIntent(i, PendingIntent.getBroadcast(this.mContext, 117506050, new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_PLAY_TOGGLE), 0));
    }

    /* access modifiers changed from: protected */
    public void setRecordResumePendingIntent(RemoteViews remoteViews, int i) {
        remoteViews.setOnClickPendingIntent(i, PendingIntent.getBroadcast(this.mContext, 117506050, new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_REC_RESUME), 0));
    }

    /* access modifiers changed from: protected */
    public void setRecordPausePendingIntent(RemoteViews remoteViews, int i) {
        remoteViews.setOnClickPendingIntent(i, PendingIntent.getBroadcast(this.mContext, 117506050, new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_REC_PAUSE), 0));
    }

    /* access modifiers changed from: protected */
    public void setRecordPlayPendingIntent(RemoteViews remoteViews, int i) {
        remoteViews.setOnClickPendingIntent(i, PendingIntent.getBroadcast(this.mContext, 117506050, new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_REC_PLAY_TOGGLE), 0));
    }

    /* access modifiers changed from: protected */
    public void setTranslatePendingIntent(RemoteViews remoteViews, int i) {
        remoteViews.setOnClickPendingIntent(i, PendingIntent.getBroadcast(this.mContext, 117506050, new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_TRANSLATION_TOGGLE), 0));
    }

    /* access modifiers changed from: protected */
    public void setTranslateSavePendingIntent(RemoteViews remoteViews, int i) {
        remoteViews.setOnClickPendingIntent(i, PendingIntent.getBroadcast(this.mContext, 117506050, new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_TRANSLATION_SAVE), 0));
    }

    /* access modifiers changed from: protected */
    public void setTranslateCancelPendingIntent(RemoteViews remoteViews, int i) {
        remoteViews.setOnClickPendingIntent(i, PendingIntent.getBroadcast(this.mContext, 117506050, new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_TRANSLATION_CANCEL_KEYGUARD), 0));
    }

    /* access modifiers changed from: protected */
    public void setTranslateFilePlayPendingIntent(RemoteViews remoteViews, int i) {
        remoteViews.setOnClickPendingIntent(i, PendingIntent.getBroadcast(this.mContext, 117506050, new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_TRANSLATION_FILE_PLAY), 0));
    }

    /* access modifiers changed from: protected */
    public void setNoActionPendingIntent(RemoteViews remoteViews, int i) {
        remoteViews.setOnClickPendingIntent(i, PendingIntent.getBroadcast(this.mContext, 117506050, new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_NO_ACTION), 0));
    }

    protected static String changeDurationToTimeText(long j) {
        int i = (int) j;
        int i2 = i / 3600;
        int i3 = (i / 60) % 60;
        int i4 = i % 60;
        if (i2 > 0) {
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4)});
        }
        return String.format(Locale.getDefault(), "%02d:%02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(i4)});
    }
}
