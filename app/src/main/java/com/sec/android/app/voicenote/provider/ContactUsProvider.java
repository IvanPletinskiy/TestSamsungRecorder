package com.sec.android.app.voicenote.provider;

import android.content.Context;
import android.content.pm.PackageManager;

public class ContactUsProvider {
    private static final String TAG = "ContactUsProvider";
    private static volatile ContactUsProvider mInstance;
    private Context mAppContext;
    private Boolean mSupportContactUs;

    private ContactUsProvider() {
        Log.m19d(TAG, "ContactUsProvider creator !!");
    }

    public static ContactUsProvider getInstance() {
        if (mInstance == null) {
            synchronized (ContactUsProvider.class) {
                if (mInstance == null) {
                    mInstance = new ContactUsProvider();
                }
            }
        }
        return mInstance;
    }

    public void setApplicationContext(Context context) {
        this.mAppContext = context;
    }

    public boolean isSupportedContactUs() {
        if (this.mSupportContactUs == null) {
            this.mSupportContactUs = Boolean.valueOf(isPackageInstalled("com.samsung.android.voc") && isSupportedVersion("com.samsung.android.voc"));
        }
        return this.mSupportContactUs.booleanValue();
    }

    private boolean isPackageInstalled(String str) {
        try {
            this.mAppContext.getPackageManager().getPackageInfo(str, 1);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    private boolean isSupportedVersion(String str) {
        try {
            if (this.mAppContext.getPackageManager().getPackageInfo(str, 0).getLongVersionCode() >= 170001000) {
                return true;
            }
            return false;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }
}
