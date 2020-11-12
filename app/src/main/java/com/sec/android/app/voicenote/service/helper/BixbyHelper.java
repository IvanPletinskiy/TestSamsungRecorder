package com.sec.android.app.voicenote.service.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

public class BixbyHelper {
    private static final String TAG = "BixbyHelper";
    private static BixbyHelper instance;
    private static Handler mEventHandler = new Handler($$Lambda$BixbyHelper$YBRjqoQOlnE9IvVwi9D0XVRP3Y.INSTANCE);
    private static VoiceNoteObservable mObservable = VoiceNoteObservable.getInstance();
    private Context mContext = null;
    private long mId;
    private BixbyPlayTask mPlayTask;

    public static BixbyHelper getInstance() {
        if (instance == null) {
            instance = new BixbyHelper();
        }
        return instance;
    }

    public void setBixbyPlayHelperContext(Context context, long j) {
        this.mContext = context;
        this.mId = j;
    }

    public void startPlayTask() {
        Log.m26i(TAG, "startPlayTask: " + this.mId);
        if (CursorProvider.getInstance().getPath(this.mId) == null) {
            Log.m22e(TAG, "id " + this.mId + " is not valid. file not found");
            return;
        }
        BixbyPlayTask bixbyPlayTask = this.mPlayTask;
        if (bixbyPlayTask != null && bixbyPlayTask.getStatus() == AsyncTask.Status.RUNNING) {
            this.mPlayTask.cancel(false);
        }
        this.mPlayTask = new BixbyPlayTask(this.mId);
        this.mPlayTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    /* access modifiers changed from: private */
    public void startPlay(long j) {
        Log.m26i(TAG, "startPlay  id : " + j);
        Engine.getInstance().clearContentItem();
        int startPlay = Engine.getInstance().startPlay(j, true);
        SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_PLAY_TYPE, -1);
        if (Settings.getBooleanSettings(Settings.KEY_SPEAKERPHONE_MODE, false)) {
            SurveyLogProvider.insertStatusLog(SurveyLogProvider.SURVEY_PLAY_VIA_PRIVATE, (String) null, 1000);
        } else {
            SurveyLogProvider.insertStatusLog(SurveyLogProvider.SURVEY_PLAY_VIA_PRIVATE, (String) null, 0);
        }
        if (startPlay == -119) {
            Toast.makeText(this.mContext, C0690R.string.please_wait, 0).show();
            postEventDelayed(Event.UNBLOCK_CONTROL_BUTTONS, 100);
        } else if (startPlay == -115) {
            Toast.makeText(this.mContext, C0690R.string.playback_failed_msg, 0).show();
            postEventDelayed(Event.UNBLOCK_CONTROL_BUTTONS, 100);
        } else if (startPlay == -103) {
            Toast.makeText(this.mContext, C0690R.string.no_play_during_call, 0).show();
            postEventDelayed(Event.UNBLOCK_CONTROL_BUTTONS, 100);
        } else if (startPlay != 0) {
            postEventDelayed(Event.UNBLOCK_CONTROL_BUTTONS, 100);
        } else {
            if (VoiceNoteApplication.getScene() != 2) {
                postEvent(Event.PLAY_START);
            } else {
                postEvent(Event.PLAY_START);
            }
            postEvent(Event.UPDATE_FILE_NAME);
        }
    }

    private class BixbyPlayTask extends AsyncTask<Void, Integer, Boolean> {
        private final long mId;
        private int mTaskState;

        private class TaskState {
            private static final int FINISH = 2;
            private static final int INIT = 0;
            private static final int RUNNING = 1;

            private TaskState() {
            }
        }

        private BixbyPlayTask(long j) {
            this.mId = j;
            this.mTaskState = 0;
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(Void... voidArr) {
            this.mTaskState = 1;
            BixbyHelper.this.startPlay(this.mId);
            return true;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            this.mTaskState = 2;
        }

        /* access modifiers changed from: package-private */
        public boolean isRunning() {
            return this.mTaskState == 1;
        }
    }

    public long getId() {
        return this.mId;
    }

    public void setId(long j) {
        this.mId = j;
    }

    static /* synthetic */ boolean lambda$static$0(Message message) {
        Log.m26i(TAG, "handleMessage : " + message.what);
        mObservable.notifyObservers(Integer.valueOf(message.what));
        return false;
    }

    private void postEventDelayed(int i, long j) {
        Log.m26i(TAG, "postEventDelayed : data = " + i + ", delayedTime = " + j);
        mEventHandler.sendEmptyMessageDelayed(i, j);
    }

    /* access modifiers changed from: protected */
    public void postEvent(int i) {
        Log.m26i(TAG, "postEvent : data = " + i);
        mEventHandler.sendEmptyMessage(i);
    }

    public boolean isBackPosible() {
        BixbyPlayTask bixbyPlayTask = this.mPlayTask;
        if ((bixbyPlayTask == null || !bixbyPlayTask.isRunning()) && (Engine.getInstance().getPlayerState() == 1 || VoiceNoteApplication.getScene() != 2)) {
            return true;
        }
        return false;
    }
}
