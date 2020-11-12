package com.samsung.android.sdk.bixby2;

import android.content.Context;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import com.samsung.android.sdk.bixby2.action.ActionHandler;
import com.samsung.android.sdk.bixby2.provider.CapsuleProvider;
import com.samsung.android.sdk.bixby2.state.StateHandler;
import java.util.Map;

public class Sbixby {
    private static final String TAG = (Sbixby.class.getSimpleName() + "_" + "1.0.12");
    private static Map<String, AppMetaInfo> appMetaInfoMap;
    private static Context mContext;
    private static Sbixby mInstance;
    private static String mPackageName;

    private Sbixby(Context context) {
        mContext = context;
    }

    public static void initialize(Context context) {
        if (context != null) {
            if (mInstance == null) {
                mInstance = new Sbixby(context);
            }
            mInstance.setPackageName(context.getPackageName());
            CapsuleProvider.setAppInitialized(true);
            String str = TAG;
            LogUtil.m16d(str, "initialized in package " + mPackageName);
            return;
        }
        throw new IllegalArgumentException("App Context is NULL. pass valid context.");
    }

    public static synchronized Sbixby getInstance() throws IllegalStateException {
        Sbixby sbixby;
        synchronized (Sbixby.class) {
            if (mInstance != null) {
                LogUtil.m16d(TAG, " getInstance()");
                sbixby = mInstance;
            } else {
                throw new IllegalStateException("The Sbixby instance is NULL. do initialize Sbixby before accessing instance.");
            }
        }
        return sbixby;
    }

    public static StateHandler getStateHandler() {
        LogUtil.m16d(TAG, " getStateHandler()");
        return StateHandler.getInstance();
    }

    public void addActionHandler(String str, @NonNull ActionHandler actionHandler) {
        if (TextUtils.isEmpty(str) || actionHandler == null) {
            throw new IllegalArgumentException("Action handler is NULL. pass valid app action handler implementation.");
        }
        String str2 = TAG;
        LogUtil.m16d(str2, " addActionHandler: action Id --> " + str);
        CapsuleProvider.addActionHandler(str, actionHandler);
    }

    private void setPackageName(@NonNull String str) {
        if (!TextUtils.isEmpty(str)) {
            mPackageName = str;
            return;
        }
        throw new IllegalArgumentException("package name is null or empty.");
    }

    public Map<String, AppMetaInfo> getAppMetaInfoMap() {
        return appMetaInfoMap;
    }
}
