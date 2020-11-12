package com.sec.android.app.voicenote.provider;

import android.content.Context;
import android.content.pm.PackageManager;

public class QuickConnectProvider {
    private static final String TAG = "QuickConnectProvider";
    private static volatile QuickConnectProvider mInstance;

    private QuickConnectProvider() {
        Log.m26i(TAG, "QuickConnectProvider creator !!");
    }

    public static QuickConnectProvider getInstance() {
        if (mInstance == null) {
            synchronized (QuickConnectProvider.class) {
                if (mInstance == null) {
                    mInstance = new QuickConnectProvider();
                }
            }
        }
        return mInstance;
    }

    public boolean isInstalledQuickConnect(Context context) {
        try {
            context.getPackageManager().getPackageInfo("com.samsung.android.qconnect", 1);
            Log.m19d(TAG, "QuickConnect is installed");
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.m22e(TAG, "NameNotFoundException:" + e.toString());
            return false;
        } catch (NullPointerException e2) {
            Log.m22e(TAG, "NullPointerException:" + e2.toString());
            return false;
        }
    }
}
