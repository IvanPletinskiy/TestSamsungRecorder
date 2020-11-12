package com.sec.android.app.voicenote.p007ui.remote;

import android.content.Context;
import android.widget.RemoteViews;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.provider.AssistantProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Network;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.decoder.Decoder;

/* renamed from: com.sec.android.app.voicenote.ui.remote.NotiRemoteViewBuilder */
public class NotiRemoteViewBuilder extends AbsRemoteViewBuilder {
    private static final String TAG = "NotiRemoteViewBuilder";
    private static NotiRemoteViewBuilder mInstance;
    protected RemoteViews mNotiRemoteView;

    private NotiRemoteViewBuilder() {
        Log.m26i(TAG, "NotiRemoteViewBuilder creator !!");
    }

    public static NotiRemoteViewBuilder getInstance() {
        if (mInstance == null) {
            mInstance = new NotiRemoteViewBuilder();
        }
        return mInstance;
    }

    public static void release() {
        mInstance = null;
    }

    public void setRemoteView(RemoteViews remoteViews) {
        this.mNotiRemoteView = remoteViews;
    }

    public RemoteViews build() {
        return this.mNotiRemoteView;
    }

    public void createRemoteview(Context context, int i) {
        super.createRemoteview(context, i);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x00b1, code lost:
        if (r13 != 4) goto L_0x0109;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void createRecordButtons(int r12, int r13, int r14) {
        /*
            r11 = this;
            super.createRecordButtons(r12, r13, r14)
            android.widget.RemoteViews r0 = r11.mNotiRemoteView
            com.sec.android.app.voicenote.provider.AssistantProvider r1 = com.sec.android.app.voicenote.provider.AssistantProvider.getInstance()
            r2 = 2131755071(0x7f10003f, float:1.914101E38)
            java.lang.String r1 = r1.getButtonDescriptionForTalkback(r2)
            r2 = 2131296715(0x7f0901cb, float:1.8211355E38)
            r0.setContentDescription(r2, r1)
            android.widget.RemoteViews r0 = r11.mNotiRemoteView
            r1 = 2131296702(0x7f0901be, float:1.8211328E38)
            r11.setSavePendingIntent(r0, r1)
            android.widget.RemoteViews r0 = r11.mNotiRemoteView
            r1 = 2131296694(0x7f0901b6, float:1.8211312E38)
            r11.setCancelPendingIntent(r0, r1)
            android.widget.RemoteViews r0 = r11.mNotiRemoteView
            r1 = 2131296700(0x7f0901bc, float:1.8211324E38)
            r11.setRecordPlayPendingIntent(r0, r1)
            r0 = 2131755393(0x7f100181, float:1.9141664E38)
            r2 = 2
            r3 = 8
            r4 = 3
            r5 = 0
            r6 = 4
            if (r12 == r4) goto L_0x0058
            if (r12 != r6) goto L_0x003c
            goto L_0x0058
        L_0x003c:
            if (r12 != r2) goto L_0x0109
            android.widget.RemoteViews r12 = r11.mNotiRemoteView
            r13 = 2131296699(0x7f0901bb, float:1.8211322E38)
            r11.setRecordPausePendingIntent(r12, r13)
            android.widget.RemoteViews r12 = r11.mNotiRemoteView
            r13 = 2131296717(0x7f0901cd, float:1.8211359E38)
            com.sec.android.app.voicenote.provider.AssistantProvider r14 = com.sec.android.app.voicenote.provider.AssistantProvider.getInstance()
            java.lang.String r14 = r14.getButtonDescriptionForTalkback(r0)
            r12.setContentDescription(r13, r14)
            goto L_0x0109
        L_0x0058:
            android.widget.RemoteViews r12 = r11.mNotiRemoteView
            r7 = 2131296701(0x7f0901bd, float:1.8211326E38)
            r11.setRecordResumePendingIntent(r12, r7)
            android.widget.RemoteViews r12 = r11.mNotiRemoteView
            com.sec.android.app.voicenote.provider.AssistantProvider r8 = com.sec.android.app.voicenote.provider.AssistantProvider.getInstance()
            r9 = 2131755450(0x7f1001ba, float:1.914178E38)
            java.lang.String r8 = r8.getButtonDescriptionForTalkback(r9)
            r9 = 2131296723(0x7f0901d3, float:1.821137E38)
            r12.setContentDescription(r9, r8)
            r12 = 102(0x66, float:1.43E-43)
            java.lang.String r8 = "setAlpha"
            java.lang.String r10 = "setEnabled"
            if (r14 != r6) goto L_0x0096
            android.widget.RemoteViews r13 = r11.mNotiRemoteView
            r13.setViewVisibility(r1, r3)
            com.sec.android.app.voicenote.service.Engine r13 = com.sec.android.app.voicenote.service.Engine.getInstance()
            boolean r13 = r13.isEditRecordable()
            if (r13 != 0) goto L_0x0109
            android.widget.RemoteViews r13 = r11.mNotiRemoteView
            r13.setBoolean(r7, r10, r5)
            android.widget.RemoteViews r13 = r11.mNotiRemoteView
            r13.setInt(r9, r8, r12)
            goto L_0x0109
        L_0x0096:
            boolean r14 = com.sec.android.app.voicenote.common.util.KeyguardManagerHelper.isKeyguardLockedBySecure()
            if (r14 == 0) goto L_0x00a2
            android.widget.RemoteViews r14 = r11.mNotiRemoteView
            r14.setViewVisibility(r1, r6)
            goto L_0x00a7
        L_0x00a2:
            android.widget.RemoteViews r14 = r11.mNotiRemoteView
            r14.setViewVisibility(r1, r5)
        L_0x00a7:
            r14 = 1
            r1 = 2131296722(0x7f0901d2, float:1.8211369E38)
            if (r13 == r14) goto L_0x00f9
            if (r13 == r2) goto L_0x00d4
            if (r13 == r4) goto L_0x00b4
            if (r13 == r6) goto L_0x00d4
            goto L_0x0109
        L_0x00b4:
            android.widget.RemoteViews r13 = r11.mNotiRemoteView
            r13.setBoolean(r7, r10, r5)
            android.widget.RemoteViews r13 = r11.mNotiRemoteView
            r13.setInt(r9, r8, r12)
            android.widget.RemoteViews r12 = r11.mNotiRemoteView
            com.sec.android.app.voicenote.provider.AssistantProvider r13 = com.sec.android.app.voicenote.provider.AssistantProvider.getInstance()
            java.lang.String r13 = r13.getButtonDescriptionForTalkback(r0)
            r12.setContentDescription(r1, r13)
            android.widget.RemoteViews r12 = r11.mNotiRemoteView
            r13 = 2131231178(0x7f0801ca, float:1.807843E38)
            r12.setImageViewResource(r1, r13)
            goto L_0x0109
        L_0x00d4:
            android.widget.RemoteViews r12 = r11.mNotiRemoteView
            r12.setBoolean(r7, r10, r14)
            android.widget.RemoteViews r12 = r11.mNotiRemoteView
            r13 = 255(0xff, float:3.57E-43)
            r12.setInt(r9, r8, r13)
            android.widget.RemoteViews r12 = r11.mNotiRemoteView
            com.sec.android.app.voicenote.provider.AssistantProvider r13 = com.sec.android.app.voicenote.provider.AssistantProvider.getInstance()
            r14 = 2131755404(0x7f10018c, float:1.9141686E38)
            java.lang.String r13 = r13.getButtonDescriptionForTalkback(r14)
            r12.setContentDescription(r1, r13)
            android.widget.RemoteViews r12 = r11.mNotiRemoteView
            r13 = 2131231179(0x7f0801cb, float:1.8078432E38)
            r12.setImageViewResource(r1, r13)
            goto L_0x0109
        L_0x00f9:
            android.widget.RemoteViews r12 = r11.mNotiRemoteView
            com.sec.android.app.voicenote.provider.AssistantProvider r13 = com.sec.android.app.voicenote.provider.AssistantProvider.getInstance()
            r14 = 2131755404(0x7f10018c, float:1.9141686E38)
            java.lang.String r13 = r13.getButtonDescriptionForTalkback(r14)
            r12.setContentDescription(r1, r13)
        L_0x0109:
            com.sec.android.app.voicenote.provider.CallRejectChecker r12 = com.sec.android.app.voicenote.provider.CallRejectChecker.getInstance()
            boolean r12 = r12.getReject()
            r13 = 2131296714(0x7f0901ca, float:1.8211352E38)
            if (r12 == 0) goto L_0x0122
            boolean r12 = com.sec.android.app.voicenote.provider.SecureFolderProvider.isInSecureFolder()
            if (r12 != 0) goto L_0x0122
            android.widget.RemoteViews r12 = r11.mNotiRemoteView
            r12.setViewVisibility(r13, r5)
            goto L_0x0127
        L_0x0122:
            android.widget.RemoteViews r12 = r11.mNotiRemoteView
            r12.setViewVisibility(r13, r3)
        L_0x0127:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.remote.NotiRemoteViewBuilder.createRecordButtons(int, int, int):void");
    }

    public void createPlayButtons(int i) {
        super.createPlayButtons(i);
        setQuitPendingIntent(this.mNotiRemoteView, C0690R.C0693id.quick_panel_quit);
        setPrevPendingIntent(this.mNotiRemoteView, C0690R.C0693id.quick_panel_prev);
        setNextPendingIntent(this.mNotiRemoteView, C0690R.C0693id.quick_panel_next);
        setPlayerPendingIntent(this.mNotiRemoteView, C0690R.C0693id.quick_panel_player);
        if (i == 4 || i == 2) {
            this.mNotiRemoteView.setContentDescription(C0690R.C0693id.remote_play_pause_button, AssistantProvider.getInstance().getButtonDescriptionForTalkback(C0690R.string.play));
            this.mNotiRemoteView.setImageViewResource(C0690R.C0693id.remote_play_pause_button, C0690R.C0692drawable.voice_recorder_quick_panel_control_play);
        } else if (i == 3) {
            this.mNotiRemoteView.setContentDescription(C0690R.C0693id.remote_play_pause_button, AssistantProvider.getInstance().getButtonDescriptionForTalkback(C0690R.string.pause));
            this.mNotiRemoteView.setImageViewResource(C0690R.C0693id.remote_play_pause_button, C0690R.C0692drawable.voice_recorder_quick_panel_control_pause);
        }
    }

    public void createTranslateButtons(int i) {
        super.createTranslateButtons(i);
        setTranslatePendingIntent(this.mNotiRemoteView, C0690R.C0693id.quick_panel_translate_translation);
        setTranslateSavePendingIntent(this.mNotiRemoteView, C0690R.C0693id.quick_panel_translate_save);
        setTranslateCancelPendingIntent(this.mNotiRemoteView, C0690R.C0693id.quick_panel_translate_cancel);
        setNoActionPendingIntent(this.mNotiRemoteView, C0690R.C0693id.remote_translation_saving_text);
        setTranslateFilePlayPendingIntent(this.mNotiRemoteView, C0690R.C0693id.remote_translation_complete_text);
        if (i == 4) {
            this.mNotiRemoteView.setImageViewResource(C0690R.C0693id.remote_translate_resume_pause_button, C0690R.C0692drawable.voice_recorder_quick_panel_control_play);
            this.mNotiRemoteView.setContentDescription(C0690R.C0693id.remote_translate_resume_pause_button, AssistantProvider.getInstance().getButtonDescriptionForTalkback(C0690R.string.play));
        } else if (i == 3) {
            this.mNotiRemoteView.setImageViewResource(C0690R.C0693id.remote_translate_resume_pause_button, C0690R.C0692drawable.voice_recorder_quick_panel_control_pause);
            this.mNotiRemoteView.setContentDescription(C0690R.C0693id.remote_translate_resume_pause_button, AssistantProvider.getInstance().getButtonDescriptionForTalkback(C0690R.string.pause));
        }
        if (!Network.isNetworkConnected(this.mContext) || Engine.getInstance().getDuration() <= Engine.getInstance().getCurrentTime()) {
            this.mNotiRemoteView.setBoolean(C0690R.C0693id.quick_panel_translate_translation, "setEnabled", false);
            this.mNotiRemoteView.setInt(C0690R.C0693id.remote_translate_resume_pause_button, "setAlpha", 102);
            return;
        }
        this.mNotiRemoteView.setBoolean(C0690R.C0693id.quick_panel_translate_translation, "setEnabled", true);
        this.mNotiRemoteView.setInt(C0690R.C0693id.remote_translate_resume_pause_button, "setAlpha", 255);
    }

    public void setRecordTextView(int i, int i2, int i3, int i4) {
        super.setRecordTextView(i, i2, i3, i4);
        this.mDisplayTime = this.mCurrentTime;
        if (i3 == 1 && Engine.getInstance().getPlayerState() != 1) {
            this.mDisplayTime = Engine.getInstance().getCurrentTime() / 1000;
        }
        String changeDurationToTimeText = AbsRemoteViewBuilder.changeDurationToTimeText((long) this.mDisplayTime);
        this.mNotiRemoteView.setTextViewText(C0690R.C0693id.quick_panel_time, changeDurationToTimeText);
        this.mNotiRemoteView.setContentDescription(C0690R.C0693id.quick_panel_time, AssistantProvider.getInstance().stringForReadTime(changeDurationToTimeText));
    }

    public void setTranslateTextView() {
        String str;
        super.setTranslateTextView();
        int progressTime = Decoder.getInstance().getProgressTime();
        if (Engine.getInstance().isTranslationComplete()) {
            str = getContext().getResources().getString(C0690R.string.stt_translation_conpleted_to_text);
            this.mNotiRemoteView.setInt(C0690R.C0693id.remote_translate_resume_pause_button, "setAlpha", 102);
        } else {
            str = getContext().getResources().getString(C0690R.string.stt_translation_converting, new Object[]{Integer.valueOf(progressTime)});
        }
        this.mNotiRemoteView.setTextViewText(C0690R.C0693id.remote_translate_converting, str);
        this.mNotiRemoteView.setProgressBar(C0690R.C0693id.remote_translate_progress, 100, progressTime, false);
    }
}
