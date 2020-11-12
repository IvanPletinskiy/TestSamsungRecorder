package com.sec.android.app.voicenote.common.util;

import android.content.Context;
import android.net.Uri;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import com.sec.android.app.voicenote.provider.Log;
import java.util.List;

public class AndroidForWork {
    public static final Uri MEDIA_URI = Uri.parse("content://media");
//    public static final UserHandle OWNER = UserHandle.SEM_OWNER;
    public static final UserHandle OWNER = null;
    private static final String TAG = "AndroidForWork";
    private static AndroidForWork mAFW = null;

    private AndroidForWork() {
    }

    public static AndroidForWork getInstance() {
        if (mAFW == null) {
            mAFW = new AndroidForWork();
        }
        return mAFW;
    }

    public Uri changeUriForAndroidForWorkMode(Uri uri) {
        if (uri == null || !uri.toString().startsWith(MEDIA_URI.toString())) {
            return uri;
        }
//        int semGetMyUserId = UserHandle.semGetMyUserId();
//        String uri2 = uri.toString();
//        return Uri.parse(uri2.replace("content://", "content://" + semGetMyUserId + '@'));
        return null;
    }

    public boolean isAndroidForWorkMode(Context context) {
        UserManager userManager = (UserManager) context.getSystemService("user");
        boolean z = false;
        if (userManager != null && userManager.getUserCount() > 1) {
//            List<UserHandle> userProfiles = userManager.getUserProfiles();
//            UserHandle myUserHandle = Process.myUserHandle();
//            if (myUserHandle != null && !myUserHandle.equals(UserHandle.SEM_OWNER)) {
//                if (userProfiles != null && userProfiles.contains(myUserHandle)) {
//                    z = true;
//                }
//                Log.m26i(TAG, "isAndroidForWorkMode: " + z);
//            }
        }
        return z;
    }
}
