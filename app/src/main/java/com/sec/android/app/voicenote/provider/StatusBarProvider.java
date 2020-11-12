package com.sec.android.app.voicenote.provider;


import android.content.Context;
import android.content.Intent;

public class StatusBarProvider {
    public static final int DISABLE_NONE = 0;
    public static final int DISABLE_NOTIFICATION_ALERTS = 262144;
    private static StatusBarProvider mStatusBar;

    private StatusBarProvider() {
    }

    public static StatusBarProvider getInstance() {
        if (mStatusBar == null) {
            mStatusBar = new StatusBarProvider();
        }
        return mStatusBar;
    }

    public void disable(Context context, int i) {
//        SemStatusBarManager semStatusBarManager = (SemStatusBarManager) context.getSystemService("sem_statusbar");
//        if (semStatusBarManager != null) {
//            semStatusBarManager.disable(i);
//        }
    }

    public void collapsePanels(Context context) {
        context.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
    }
}
