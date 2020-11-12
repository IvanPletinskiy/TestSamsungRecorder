package com.sec.android.app.voicenote.p007ui.remote;

import android.content.Context;
import android.os.SystemClock;
import android.widget.RemoteViews;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.provider.AssistantProvider;
import com.sec.android.app.voicenote.provider.CallRejectChecker;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.SecureFolderProvider;
import com.sec.android.app.voicenote.service.Engine;

/* renamed from: com.sec.android.app.voicenote.ui.remote.CoverRemoteViewBuilder */
public class CoverRemoteViewBuilder extends AbsRemoteViewBuilder {
    private static final String TAG = "CoverRemoteViewBuilder";
    private static CoverRemoteViewBuilder mInstance;
    protected RemoteViews mCoverRemoteView;

    private CoverRemoteViewBuilder() {
        Log.m26i(TAG, "CoverRemoteViewBuilder creator !!");
    }

    public static CoverRemoteViewBuilder getInstance() {
        if (mInstance == null) {
            mInstance = new CoverRemoteViewBuilder();
        }
        return mInstance;
    }

    public void setRemoteView(RemoteViews remoteViews) {
        this.mCoverRemoteView = remoteViews;
    }

    public RemoteViews build() {
        return this.mCoverRemoteView;
    }

    public void createRemoteview(Context context, int i) {
        super.createRemoteview(context, i);
    }

    public void createRecordButtons(int i, int i2, int i3) {
        super.createRecordButtons(i, i2, i3);
        this.mCoverRemoteView.setContentDescription(C0690R.C0693id.remote_stop_button, AssistantProvider.getInstance().getButtonDescriptionForTalkback(C0690R.string.save));
        this.mCoverRemoteView.setContentDescription(C0690R.C0693id.remote_record_play_pause_button, AssistantProvider.getInstance().getButtonDescriptionForTalkback(C0690R.string.play));
        setSavePendingIntent(this.mCoverRemoteView, C0690R.C0693id.cover_save);
        if (i == 3 || i == 4) {
            this.mCoverRemoteView.setContentDescription(C0690R.C0693id.remote_resume_button, this.mContext.getResources().getString(C0690R.string.resume));
            setRecordResumePendingIntent(this.mCoverRemoteView, C0690R.C0693id.remote_resume_button);
            if (i3 != 4) {
                setRecordPlayPendingIntent(this.mCoverRemoteView, C0690R.C0693id.quick_panel_record_play_pause);
                this.mCoverRemoteView.setInt(C0690R.C0693id.remote_record_play_pause_button, "setAlpha", 255);
            } else {
                this.mCoverRemoteView.setInt(C0690R.C0693id.remote_record_play_pause_button, "setAlpha", 102);
                if (!Engine.getInstance().isEditRecordable()) {
                    this.mCoverRemoteView.setBoolean(C0690R.C0693id.remote_resume_button, "setEnabled", false);
                    this.mCoverRemoteView.setInt(C0690R.C0693id.remote_resume_button, "setAlpha", 102);
                }
            }
            if (i2 == 4 || i2 == 2) {
                this.mCoverRemoteView.setBoolean(C0690R.C0693id.remote_resume_button, "setEnabled", true);
                this.mCoverRemoteView.setInt(C0690R.C0693id.remote_resume_button, "setAlpha", 255);
                this.mCoverRemoteView.setImageViewResource(C0690R.C0693id.remote_record_play_pause_button, C0690R.C0692drawable.clear_recorder_ic_small_play);
            } else if (i2 == 3) {
                this.mCoverRemoteView.setBoolean(C0690R.C0693id.remote_resume_button, "setEnabled", false);
                this.mCoverRemoteView.setInt(C0690R.C0693id.remote_resume_button, "setAlpha", 102);
                this.mCoverRemoteView.setImageViewResource(C0690R.C0693id.remote_record_play_pause_button, C0690R.C0692drawable.clear_recorder_ic_small_pause);
                this.mCoverRemoteView.setContentDescription(C0690R.C0693id.remote_record_play_pause_button, AssistantProvider.getInstance().getButtonDescriptionForTalkback(C0690R.string.pause));
            }
        } else if (i == 2) {
            this.mCoverRemoteView.setContentDescription(C0690R.C0693id.remote_pause_button, this.mContext.getResources().getString(C0690R.string.pause));
            setRecordPausePendingIntent(this.mCoverRemoteView, C0690R.C0693id.remote_pause_button);
        }
        if (!CallRejectChecker.getInstance().getReject() || SecureFolderProvider.isInSecureFolder()) {
            this.mCoverRemoteView.setViewVisibility(C0690R.C0693id.remote_callreject, 8);
            return;
        }
        if (getContext().getResources().getConfiguration().fontScale > 1.2f) {
            this.mCoverRemoteView.setTextViewTextSize(C0690R.C0693id.remote_callreject, 1, ((float) 16) * 1.2f);
        }
        this.mCoverRemoteView.setViewVisibility(C0690R.C0693id.remote_callreject, 0);
    }

    public void createPlayButtons(int i) {
        super.createPlayButtons(i);
        setPrevPendingIntent(this.mCoverRemoteView, C0690R.C0693id.remote_prev_button);
        setNextPendingIntent(this.mCoverRemoteView, C0690R.C0693id.remote_next_button);
        setPlayerPendingIntent(this.mCoverRemoteView, C0690R.C0693id.remote_play_pause_button);
        this.mCoverRemoteView.setInt(C0690R.C0693id.remote_playing_clipname, "setSelected", 1);
        if (i == 4 || i == 2) {
            this.mCoverRemoteView.setContentDescription(C0690R.C0693id.remote_play_pause_button, this.mContext.getResources().getString(C0690R.string.play));
            this.mCoverRemoteView.setImageViewResource(C0690R.C0693id.remote_play_pause_button, C0690R.C0692drawable.clear_recorder_ic_play);
        } else if (i == 3) {
            this.mCoverRemoteView.setContentDescription(C0690R.C0693id.remote_play_pause_button, this.mContext.getResources().getString(C0690R.string.pause));
            this.mCoverRemoteView.setImageViewResource(C0690R.C0693id.remote_play_pause_button, C0690R.C0692drawable.clear_recorder_ic_pause);
        }
    }

    public void createTranslateButtons(int i) {
        super.createTranslateButtons(i);
        setTranslatePendingIntent(this.mCoverRemoteView, C0690R.C0693id.remote_translate_play_pause_button);
        setTranslateSavePendingIntent(this.mCoverRemoteView, C0690R.C0693id.remote_translate_save_button);
        setTranslateCancelPendingIntent(this.mCoverRemoteView, C0690R.C0693id.remote_translate_cancel_button);
        if (i == 4 || i == 2) {
            this.mCoverRemoteView.setContentDescription(C0690R.C0693id.remote_translate_play_pause_button, this.mContext.getResources().getString(C0690R.string.stt_convert));
            this.mCoverRemoteView.setImageViewResource(C0690R.C0693id.remote_translate_play_pause_button, C0690R.C0692drawable.clear_recorder_ic_play);
        } else if (i == 3) {
            this.mCoverRemoteView.setContentDescription(C0690R.C0693id.remote_translate_play_pause_button, this.mContext.getResources().getString(C0690R.string.pause));
            this.mCoverRemoteView.setImageViewResource(C0690R.C0693id.remote_translate_play_pause_button, C0690R.C0692drawable.clear_recorder_ic_pause);
        }
    }

    public void setRecordTextView(int i, int i2, int i3, int i4) {
        int i5 = i;
        super.setRecordTextView(i, i2, i3, i4);
        if (i5 == 3 || i5 == 4) {
            if (i2 == 3) {
                this.mCoverRemoteView.setChronometer(C0690R.C0693id.cover_recording_timer, SystemClock.elapsedRealtime() - ((long) this.mCurrentTime), (String) null, true);
            } else {
                this.mCoverRemoteView.setChronometer(C0690R.C0693id.cover_recording_timer, SystemClock.elapsedRealtime() - ((long) this.mCurrentTime), (String) null, false);
            }
        } else if (i5 == 2) {
            this.mCoverRemoteView.setChronometer(C0690R.C0693id.cover_recording_timer, SystemClock.elapsedRealtime() - ((long) this.mCurrentTime), (String) null, true);
        }
    }

    public void setTranslateTextView() {
        super.setTranslateTextView();
    }
}
