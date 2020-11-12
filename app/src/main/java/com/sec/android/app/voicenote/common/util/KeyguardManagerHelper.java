package com.sec.android.app.voicenote.common.util;

import android.app.KeyguardManager;
import android.os.UserHandle;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import java.lang.reflect.InvocationTargetException;

public class KeyguardManagerHelper {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int PERSONAL_USER_ID = 0;

    public static boolean isKeyguardLockedBySecure() {
        if (isUserIdLocked(0)) {
            return true;
        }
//        return isUserIdLocked(UserHandle.semGetMyUserId());
        return false;
    }

    private static boolean isUserIdLocked(int i) {
        KeyguardManager keyguardManager = (KeyguardManager) VoiceNoteApplication.getApplication().getSystemService("keyguard");
        try {
            return ((Boolean) keyguardManager.getClass().getMethod("isDeviceLocked", new Class[]{Integer.TYPE}).invoke(keyguardManager, new Object[]{Integer.valueOf(i)})).booleanValue();
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            Log.m22e("KeyguardManagerHelper", e.toString());
            return false;
        }
    }
}
