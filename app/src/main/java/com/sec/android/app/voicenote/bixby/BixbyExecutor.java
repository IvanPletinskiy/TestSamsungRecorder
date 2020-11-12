package com.sec.android.app.voicenote.bixby;

import android.app.Application;
import com.samsung.android.sdk.bixby2.Sbixby;
import com.sec.android.app.voicenote.bixby.constant.BixbyConstant;
import com.sec.android.app.voicenote.provider.Log;

public class BixbyExecutor {
    private static final String TAG = "BixbyExecutor";
    private Sbixby mSbixby = Sbixby.getInstance();

    public BixbyExecutor(Application application) {
        Log.m26i(TAG, TAG);
        Sbixby.initialize(application.getApplicationContext());
    }

    public void addActionHandler() {
        Log.m26i(TAG, "addActionHandler");
        this.mSbixby.addActionHandler(BixbyConstant.BixbyActions.ACTION_START_RECORDING, new BixbyActionHandler());
        this.mSbixby.addActionHandler(BixbyConstant.BixbyActions.ACTION_SPEECH_TO_TEXT_INFO, new BixbyActionHandler());
        this.mSbixby.addActionHandler(BixbyConstant.BixbyActions.ACTION_START_TNC, new BixbyActionHandler());
        this.mSbixby.addActionHandler(BixbyConstant.BixbyActions.ACTION_GET_RECORDING_INFO, new BixbyActionHandler());
        this.mSbixby.addActionHandler(BixbyConstant.BixbyActions.ACTION_PLAY_RECORDING_FILE, new BixbyActionHandler());
        this.mSbixby.addActionHandler(BixbyConstant.BixbyActions.ACTION_GET_RECORDED_FILE_COUNT, new BixbyActionHandler());
    }
}
