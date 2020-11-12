package com.samsung.android.sdk.bixby2.state;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import com.samsung.android.sdk.bixby2.AppMetaInfo;
import com.samsung.android.sdk.bixby2.LogUtil;
import com.samsung.android.sdk.bixby2.Sbixby;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class StateHandler {
    private static final String TAG = "StateHandler";
    private static StateHandler mInstance;
    private Callback mCallback = null;

    public static abstract class Callback {
        public abstract String onAppStateRequested();

        public abstract String onCapsuleIdRequested();
    }

    private StateHandler() {
    }

    public static synchronized StateHandler getInstance() {
        StateHandler stateHandler;
        synchronized (StateHandler.class) {
            if (mInstance == null) {
                mInstance = new StateHandler();
            }
            stateHandler = mInstance;
        }
        return stateHandler;
    }

    private AppMetaInfo getDefaultAppMetaInfo(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            String packageName = context.getPackageName();
            Bundle bundle = packageManager.getApplicationInfo(packageName, 128).metaData;
            if (bundle != null && bundle.containsKey("com.samsung.android.bixby.capsuleId")) {
                return new AppMetaInfo(bundle.getString("com.samsung.android.bixby.capsuleId"), packageManager.getPackageInfo(packageName, 0).versionCode);
            }
            String str = TAG;
            LogUtil.m17e(str, "Can't get Capsule ID from Meta data:" + packageName);
            return null;
        } catch (PackageManager.NameNotFoundException | NullPointerException e) {
            String str2 = TAG;
            LogUtil.m17e(str2, "Failed to get Meta data info: " + e.getMessage());
            return null;
        }
    }

    public String getAppState(Context context) {
        AppMetaInfo appMetaInfo;
        Callback callback = this.mCallback;
        if (callback == null) {
            LogUtil.m17e(TAG, "StateHandler.Callback instance is null");
            return null;
        }
        String onAppStateRequested = callback.onAppStateRequested();
        if (TextUtils.isEmpty(onAppStateRequested)) {
            LogUtil.m17e(TAG, "state info is empty.");
            return null;
        }
        String onCapsuleIdRequested = this.mCallback.onCapsuleIdRequested();
        Map<String, AppMetaInfo> appMetaInfoMap = Sbixby.getInstance().getAppMetaInfoMap();
        if (TextUtils.isEmpty(onCapsuleIdRequested)) {
            LogUtil.m17e(TAG, "capsuleId is empty");
            if (appMetaInfoMap == null || (appMetaInfoMap != null && appMetaInfoMap.size() == 0)) {
                appMetaInfo = getDefaultAppMetaInfo(context);
            } else if (appMetaInfoMap.size() == 1) {
                LogUtil.m18i(TAG, "Map for App Meta Info. has only one");
                appMetaInfo = (AppMetaInfo) appMetaInfoMap.entrySet().iterator().next().getValue();
            } else {
                LogUtil.m17e(TAG, "No Capsule Id and multiple App Meta Info. Can't pick one");
                return null;
            }
        } else if (appMetaInfoMap == null || !appMetaInfoMap.containsKey(onCapsuleIdRequested)) {
            LogUtil.m17e(TAG, "Map for App Meta Info. is empty");
            AppMetaInfo defaultAppMetaInfo = getDefaultAppMetaInfo(context);
            if (defaultAppMetaInfo != null) {
                defaultAppMetaInfo.setCapsuleId(onCapsuleIdRequested);
            }
            appMetaInfo = defaultAppMetaInfo;
        } else {
            appMetaInfo = appMetaInfoMap.get(onCapsuleIdRequested);
        }
        if (appMetaInfo == null) {
            LogUtil.m17e(TAG, "App Meta Info. is null");
            return null;
        }
        try {
            JSONObject jSONObject = new JSONObject(onAppStateRequested);
            jSONObject.put("capsuleId", appMetaInfo.getCapsuleId());
            jSONObject.put("appId", context.getPackageName());
            jSONObject.put("appVersionCode", appMetaInfo.getAppVersionCode());
            String str = TAG;
            LogUtil.m16d(str, "state info: " + jSONObject.toString());
            return jSONObject.toString();
        } catch (JSONException e) {
            LogUtil.m17e(TAG, e.getMessage());
            return null;
        }
    }
}
