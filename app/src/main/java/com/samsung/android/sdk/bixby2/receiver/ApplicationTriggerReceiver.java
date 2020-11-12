package com.samsung.android.sdk.bixby2.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.samsung.android.sdk.bixby2.LogUtil;

public class ApplicationTriggerReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        LogUtil.m18i("ApplicationTriggerReceiver", "onReceived()");
        if (context != null) {
            context.unregisterReceiver(this);
            LogUtil.m18i("ApplicationTriggerReceiver", "ApplicationTriggerReceiver unRegistered");
        }
    }
}
