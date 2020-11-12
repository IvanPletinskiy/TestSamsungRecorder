package com.sec.android.diagmonagent.log.provider.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.samsung.context.sdk.samsunganalytics.BuildConfig;
import com.sec.android.app.voicenote.bixby.constant.BixbyConstant;
import com.sec.android.diagmonagent.log.provider.DiagMonConfig;
import com.sec.android.diagmonagent.log.provider.DiagMonSDK;
import com.sec.android.diagmonagent.log.provider.EventBuilder;
import java.io.File;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

public class DiagMonUtil {
    public static final String TAG = ("DIAGMON_SDK[" + DiagMonSDK.getSDKVersion() + "]");
    static boolean hasDMA = false;

    public static Bundle generateSRobj(DiagMonConfig diagMonConfig) {
        Bundle bundle = new Bundle();
        bundle.putString("serviceId", diagMonConfig.getServiceId());
        bundle.putString("serviceVersion", getPackageVersion(diagMonConfig.getContext()));
        bundle.putString("serviceAgreeType", diagMonConfig.getAgreeAsString());
        bundle.putString("deviceId", diagMonConfig.getDeviceId());
        bundle.putString("trackingId", diagMonConfig.getTrackingId());
        bundle.putString("sdkVersion", DiagMonSDK.getSDKVersion());
        bundle.putString("sdkType", DiagMonSDK.getSDKtype());
        Log.i(TAG, "generated SR object");
        return bundle;
    }

    public static Intent makeEventobjAsIntent(Context context, DiagMonConfig diagMonConfig, EventBuilder eventBuilder) {
        Intent intent;
        JSONObject jSONObject = new JSONObject();
        if (getUid(context) == 1000) {
            intent = new Intent("com.sec.android.diagmonagent.intent.REPORT_ERROR_V2");
        } else {
            intent = new Intent("com.sec.android.diagmonagent.intent.REPORT_ERROR_APP");
        }
        Bundle bundle = new Bundle();
        intent.addFlags(32);
        bundle.putBundle("DiagMon", new Bundle());
        bundle.getBundle("DiagMon").putBundle("CFailLogUpload", new Bundle());
        bundle.getBundle("DiagMon").getBundle("CFailLogUpload").putString("ServiceID", diagMonConfig.getServiceId());
        bundle.getBundle("DiagMon").getBundle("CFailLogUpload").putBundle("Ext", new Bundle());
        bundle.getBundle("DiagMon").getBundle("CFailLogUpload").getBundle("Ext").putString("ClientV", getPackageVersion(context));
        if (!TextUtils.isEmpty(eventBuilder.getRelayClientType())) {
            bundle.getBundle("DiagMon").getBundle("CFailLogUpload").getBundle("Ext").putString("RelayClient", eventBuilder.getRelayClientType());
        }
        if (!TextUtils.isEmpty(eventBuilder.getRelayClientVer())) {
            bundle.getBundle("DiagMon").getBundle("CFailLogUpload").getBundle("Ext").putString("RelayClientV", eventBuilder.getRelayClientVer());
        }
        bundle.getBundle("DiagMon").getBundle("CFailLogUpload").getBundle("Ext").putString("UiMode", "0");
        bundle.getBundle("DiagMon").getBundle("CFailLogUpload").getBundle("Ext").putString("ResultCode", eventBuilder.getErrorCode());
        if (!TextUtils.isEmpty(eventBuilder.getServiceDefinedKey())) {
            bundle.getBundle("DiagMon").getBundle("CFailLogUpload").getBundle("Ext").putString("EventID", eventBuilder.getServiceDefinedKey());
        }
        try {
            jSONObject.put("SasdkV", BuildConfig.VERSION_NAME);
            jSONObject.put("SdkV", DiagMonSDK.getSDKVersion());
            jSONObject.put("TrackingID", diagMonConfig.getTrackingId());
            jSONObject.put("Description", eventBuilder.getDescription());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bundle.getBundle("DiagMon").getBundle("CFailLogUpload").getBundle("Ext").putString("Description", jSONObject.toString());
        if (eventBuilder.getNetworkMode()) {
            bundle.getBundle("DiagMon").getBundle("CFailLogUpload").getBundle("Ext").putString("WifiOnlyFeature", "1");
        } else {
            bundle.getBundle("DiagMon").getBundle("CFailLogUpload").getBundle("Ext").putString("WifiOnlyFeature", "0");
        }
        intent.putExtra("uploadMO", bundle);
        intent.setFlags(32);
        Log.i(TAG, "EventObject is generated");
        return intent;
    }

    public static Bundle makeEventObjAsBundle(Context context, DiagMonConfig diagMonConfig, EventBuilder eventBuilder) {
        Bundle bundle = new Bundle();
        try {
            bundle.putParcelable("fileDescriptor", collectLogs(context, eventBuilder));
            bundle.putString("serviceId", diagMonConfig.getServiceId());
            bundle.putString("serviceVersion", diagMonConfig.getServiceVer());
            bundle.putString("serviceDefinedKey", eventBuilder.getServiceDefinedKey());
            bundle.putString("errorCode", eventBuilder.getErrorCode());
            bundle.putBoolean("wifiOnly", eventBuilder.getNetworkMode());
            bundle.putString("errorDesc", eventBuilder.getDescription());
            bundle.putString("relayClientVersion", eventBuilder.getRelayClientVer());
            bundle.putString("relayClientType", eventBuilder.getRelayClientType());
            bundle.putString("extension", eventBuilder.getExtData());
            bundle.putString("deviceId", diagMonConfig.getDeviceId());
            bundle.putString("sdkVersion", DiagMonSDK.getSDKVersion());
            bundle.putString("sdkType", DiagMonSDK.getSDKtype());
            Log.d(TAG, "Generated EventObject");
            return bundle;
        } catch (Exception unused) {
            return null;
        }
    }

    public static int checkDMA(Context context) {
        try {
            return context.getPackageManager().getPackageInfo("com.sec.android.diagmonagent", 0).versionCode < 600000000 ? 1 : 2;
        } catch (PackageManager.NameNotFoundException e) {
            String str = TAG;
            Log.w(str, "DiagMonAgent isn't found: " + e.getMessage());
            return 0;
        }
    }

    public static String getPackageVersion(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager != null) {
                return packageManager.getPackageInfo(context.getPackageName(), 0).versionName;
            }
            return "";
        } catch (PackageManager.NameNotFoundException unused) {
            String str = TAG;
            Log.e(str, context.getPackageName() + " is not found");
            return "";
        }
    }

    public static int getUid(Context context) {
        return context.getApplicationInfo().uid;
    }

    public static boolean isErrorLogAgreed(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "samsung_errorlog_agree", 0) == 1;
    }

    public static ParcelFileDescriptor collectLogs(Context context, EventBuilder eventBuilder) throws Exception {
        ParcelFileDescriptor parcelFileDescriptor;
        if (eventBuilder.getLogPath() == null || TextUtils.isEmpty(eventBuilder.getLogPath())) {
            Log.w(TAG, "No Log Path, You have to set LogPath to report logs");
            throw new IOException("Not found");
        }
        try {
            String valueOf = String.valueOf(System.currentTimeMillis());
            File file = new File(context.getFilesDir().getAbsolutePath() + "/zip");
            file.mkdir();
            String absolutePath = file.getAbsolutePath();
            String str = absolutePath + "/" + valueOf + ".zip";
            ZipHelper.zip(eventBuilder.getLogPath(), str);
            parcelFileDescriptor = null;
            try {
                parcelFileDescriptor = ParcelFileDescriptor.open(new File(str), 268435456);
                eventBuilder.setZipFilePath(str);
                Log.d(TAG, "Zipping logs is completed");
                Log.d(TAG, "Zipped file size : " + String.valueOf(parcelFileDescriptor.getStatSize()));
                return parcelFileDescriptor;
            } catch (IOException e) {
                Log.w(TAG, e.getMessage());
            } catch (Throwable unused) {
            }
        } catch (Exception e2) {
            Log.w(TAG, "Zipping failure");
            Log.w(TAG, "Exception : " + e2.getMessage());
            throw e2;
        }
        return parcelFileDescriptor;
    }

    public static void removeZipFile(String str) {
        File file = new File(str);
        if (!file.exists()) {
            String str2 = TAG;
            Log.w(str2, "File is not found : " + str);
        } else if (file.delete()) {
            String str3 = TAG;
            Log.d(str3, "Removed zipFile : " + str);
        } else {
            String str4 = TAG;
            Log.w(str4, "Coudn't removed zipFile : " + str);
        }
    }

    public static void printResultfromDMA(Bundle bundle) {
        try {
            String string = bundle.getString("serviceId");
            String string2 = bundle.getString("result");
            String string3 = bundle.getString(BixbyConstant.ResponseOutputParameter.CAUSE);
            if (string3 == null) {
                String str = TAG;
                Log.i(str, "Service ID : " + string + ", results : " + string2);
                return;
            }
            String str2 = TAG;
            Log.i(str2, "Service ID : " + string + ", Results : " + string2 + ", Cause : " + string3);
        } catch (NullPointerException e) {
            Log.w(TAG, e.getMessage());
        } catch (Exception e2) {
            Log.w(TAG, e2.getMessage());
        }
    }
}
