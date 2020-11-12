package com.sec.android.app.voicenote.provider;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.sec.android.app.voicenote.common.util.DeviceIdManager;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import javax.net.ssl.HttpsURLConnection;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class UpdateProvider implements DeviceIdManager.OnDeviceIdListener {
    private static final int APK_INSTALL_REQUEST_TO_GALAXYAPPS = 1;
    private static final int CONNECTION_TIMEOUT = 3000;
    private static final String GET_CHINA_URL = "https://cn-ms.samsungapps.com/getCNVasURL.as";
    private static final String[] MCC_OF_CHINA = {"460", "461"};
    private static final String PD_TEST_PATH = "go_to_andromeda.test";
    private static final String TAG = "UpdateProvider";
    private static final int TYPE_UPDATE_CHECK = 1;
    private static final long UPDATE_CHECK_THRESHOLD_TIME = 86400000;
    private static final String UPDATE_CHECK_URL = "https://vas.samsungapps.com/stub/stubUpdateCheck.as";
    private static volatile UpdateProvider mInstance;
    private static Context sAppContext;
    private Uri.Builder mBuilder;
    private StubRequest mStubRequest;

    public static class CheckUpdateState {
        public static final int UPDATE_STATE_AVAILABLE = 1;
        public static final int UPDATE_STATE_FIRST_LAUNCH = 2;
        public static final int UPDATE_STATE_NETWORK_CONNECT_FAIL = 4;
        public static final int UPDATE_STATE_NETWORK_USE_DENY = 5;
        public static final int UPDATE_STATE_WITHIN_THRESHOLD_TIME = 3;
    }

    public static class StubCodes {
        public static final String UPDATE_CHECK_FAIL = "-1";
        public static final String UPDATE_CHECK_NO_MATCHING_APPLICATION = "0";
        public static final String UPDATE_CHECK_UPDATE_AVAILABLE = "2";
        public static final String UPDATE_CHECK_UPDATE_NOT_NECESSARY = "1";
    }

    public interface StubListener {
        void onNoMatchingApplication(StubData stubData);

        void onUpdateAvailable(StubData stubData);

        void onUpdateCheckFail(StubData stubData);

        void onUpdateNotNecessary(StubData stubData);
    }

    private UpdateProvider() {
        Log.m19d(TAG, "UpdateProvider creator !!");
    }

    public static UpdateProvider getInstance() {
        if (mInstance == null) {
            synchronized (UpdateProvider.class) {
                if (mInstance == null) {
                    mInstance = new UpdateProvider();
                }
            }
        }
        return mInstance;
    }

    public void onDeviceIdUpdate(int i) {
        Log.m26i(TAG, "onDeviceIdUpdate - status : " + i);
        if (i == 1 || i == 2 || i == 3 || i == 4) {
            runStubRequestForChina(this.mBuilder, this.mStubRequest);
        }
    }

    public static class StubData {
        private String appId;
        private String contentSize;
        private String deltaContentSize;
        private String deltaDownloadURI;
        private String downloadURI;
        private String gSignatureDownloadURL;
        private String productId;
        private String productName;
        private String resultCode;
        private String resultMsg;
        private String signature;
        private String versionCode;
        private String versionName;

        public String getResultCode() {
            return this.resultCode;
        }

        public void setResultCode(String str) {
            this.resultCode = str;
        }

        public String getResultMsg() {
            return this.resultMsg;
        }

        public void setResultMsg(String str) {
            this.resultMsg = str;
        }

        public String getVersionCode() {
            return this.versionCode;
        }

        public void setVersionCode(String str) {
            this.versionCode = str;
        }

        public String getVersionName() {
            return this.versionName;
        }

        public void setVersionName(String str) {
            this.versionName = str;
        }

        public String getContentSize() {
            return this.contentSize;
        }

        public void setContentSize(String str) {
            this.contentSize = str;
        }

        public String getAppId() {
            return this.appId;
        }

        public void setAppId(String str) {
            this.appId = str;
        }

        public String getDownloadURI() {
            return this.downloadURI;
        }

        public void setDownloadURI(String str) {
            this.downloadURI = str;
        }

        public String getDeltaDownloadURI() {
            return this.deltaDownloadURI;
        }

        public void setDeltaDownloadURI(String str) {
            this.deltaDownloadURI = str;
        }

        public String getDeltaContentSize() {
            return this.deltaContentSize;
        }

        public void setDeltaContentSize(String str) {
            this.deltaContentSize = str;
        }

        public String getSignature() {
            return this.signature;
        }

        public void setSignature(String str) {
            this.signature = str;
        }

        public String getgSignatureDownloadURL() {
            return this.gSignatureDownloadURL;
        }

        public void setgSignatureDownloadURL(String str) {
            this.gSignatureDownloadURL = str;
        }

        public String getProductId() {
            return this.productId;
        }

        public void setProductId(String str) {
            this.productId = str;
        }

        public String getProductName() {
            return this.productName;
        }

        public void setProductName(String str) {
            this.productName = str;
        }
    }

    private static class StubRequest extends AsyncTask<String, Void, StubData> {
        private boolean isChina;
        private StubListener listener;
        private int type;
        private String url;

        private StubRequest() {
        }

        public void setType(int i) {
            this.type = i;
        }

        public void setUrl(String str) {
            this.url = str;
        }

        public void setIsChina(boolean z) {
            this.isChina = z;
        }

        public void setListener(StubListener stubListener) {
            this.listener = stubListener;
        }

        public void run() {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{this.url});
        }

        /* access modifiers changed from: protected */
        public StubData doInBackground(String... strArr) {
            StubData stubData = new StubData();
            try {
                String str = strArr[0];
                if (this.isChina) {
                    String access$000 = UpdateProvider.getChinaURL();
                    if (!access$000.isEmpty()) {
                        str = str.replaceFirst("vas.samsungapps.com", access$000);
                    }
                }
                URL url2 = new URL(str);
                Log.m19d(UpdateProvider.TAG, "requestUrl: " + str);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url2.openConnection();
                httpsURLConnection.setConnectTimeout(UpdateProvider.CONNECTION_TIMEOUT);
                httpsURLConnection.setReadTimeout(UpdateProvider.CONNECTION_TIMEOUT);
                httpsURLConnection.setInstanceFollowRedirects(true);
                if (200 == httpsURLConnection.getResponseCode()) {
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
                    while (true) {
                        String readLine = bufferedReader.readLine();
                        if (readLine == null) {
                            break;
                        }
                        Log.m19d(UpdateProvider.TAG, "line:" + readLine);
                        sb.append(readLine);
                    }
                    bufferedReader.close();
                    XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
                    newPullParser.setInput(new StringReader(sb.toString()));
                    for (int eventType = newPullParser.getEventType(); eventType != 1; eventType = newPullParser.next()) {
                        if (eventType == 2) {
                            String name = newPullParser.getName();
                            if ("appId".equalsIgnoreCase(name)) {
                                stubData.setAppId(newPullParser.nextText());
                            } else if ("resultCode".equalsIgnoreCase(name)) {
                                stubData.setResultCode(newPullParser.nextText());
                            } else if ("resultMsg".equalsIgnoreCase(name)) {
                                stubData.setResultMsg(newPullParser.nextText());
                            } else if ("versionCode".equalsIgnoreCase(name)) {
                                stubData.setVersionCode(newPullParser.nextText());
                            } else if ("versionName".equalsIgnoreCase(name)) {
                                stubData.setVersionName(newPullParser.nextText());
                            } else if ("contentSize".equalsIgnoreCase(name)) {
                                stubData.setContentSize(newPullParser.nextText());
                            } else if ("downloadURI".equalsIgnoreCase(name)) {
                                stubData.setDownloadURI(newPullParser.nextText());
                            } else if ("deltaDownloadURI".equalsIgnoreCase(name)) {
                                stubData.setDeltaDownloadURI(newPullParser.nextText());
                            } else if ("deltaContentSize".equalsIgnoreCase(name)) {
                                stubData.setDeltaContentSize(newPullParser.nextText());
                            } else if ("signature".equalsIgnoreCase(name)) {
                                stubData.setSignature(newPullParser.nextText());
                            } else if ("gSignatureDownloadURL".equalsIgnoreCase(name)) {
                                stubData.setgSignatureDownloadURL(newPullParser.nextText());
                            } else if ("productId".equalsIgnoreCase(name)) {
                                stubData.setProductId(newPullParser.nextText());
                            } else if ("productName".equalsIgnoreCase(name)) {
                                stubData.setProductName(newPullParser.nextText());
                            }
                        }
                    }
                    return stubData;
                }
                throw new IOException("status code " + httpsURLConnection.getResponseCode() + " != " + ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e2) {
                Log.m24e(UpdateProvider.TAG, "Exception", (Throwable) e2);
                return null;
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(StubData stubData) {
            if (this.type != 1) {
                return;
            }
            if (stubData == null || UpdateProvider.isError(stubData)) {
                this.listener.onUpdateCheckFail(stubData);
            } else if (UpdateProvider.isNoMatchingApplication(stubData)) {
                this.listener.onNoMatchingApplication(stubData);
            } else if (UpdateProvider.isUpdateNotNecessary(stubData)) {
                this.listener.onUpdateNotNecessary(stubData);
            } else if (UpdateProvider.isUpdateAvailable(stubData)) {
                this.listener.onUpdateAvailable(stubData);
            }
        }
    }

    public void setApplicationContext(Context context) {
        sAppContext = context;
    }

    public int isCheckUpdateAvailable(boolean z) {
//        Log.m19d(TAG, "checkUpdate: start check update available.");
//        if (VoiceNoteFeature.FLAG_SUPPORT_DATA_CHECK_POPUP && Settings.getBooleanSettings(Settings.KEY_DATA_CHECK_SHOW_AGAIN, true)) {
//            Log.m26i(TAG, "checkUpdate: network use deny, allow network using before check update.");
//            return 5;
//        } else if (!Network.isNetworkConnected(sAppContext)) {
//            Log.m26i(TAG, "checkUpdate: network connect fail, open network connection and retry.");
//            return 4;
//        } else {
//            String stringSettings = Settings.getStringSettings(Settings.KEY_UPDATE_CHECK_LAST_VERSION, VoiceNoteApplication.getApkVersionName());
//            String apkVersionName = VoiceNoteApplication.getApkVersionName();
//            long currentTimeMillis = System.currentTimeMillis();
//            String stringSettings2 = Settings.getStringSettings(Settings.KEY_UPDATE_CHECK_RESULT_CODE, "1");
//            if ((!Objects.equals(stringSettings2, "2") && Settings.getBooleanSettings(Settings.KEY_UPDATE_CHECK_FROM_GALAXY_APPS, false)) || z) {
//                forceCheckUpdateAvailable();
//            }
//            if (VoiceNoteFeature.FLAG_IS_OS_UPGRADE) {
//                Log.m26i(TAG, "checkUpdate: after os upgrade, check new version");
//                return 1;
//            }
//            long longSettings = Settings.getLongSettings(Settings.KEY_UPDATE_CHECK_LAST_DATE, 0);
//            StringBuilder sb = new StringBuilder();
//            sb.append("checkUpdate ");
//            long j = currentTimeMillis - longSettings;
//            sb.append(((j / 1000) / 60) / 60);
//            sb.append(" hours ago");
//            sb.append(". last: ");
//            sb.append(DateFormat.getInstance().format(new Date(longSettings)));
//            sb.append(", current: ");
//            sb.append(DateFormat.getInstance().format(new Date(currentTimeMillis)));
//            Log.m19d(TAG, sb.toString());
//            Log.m19d(TAG, "checkUpdate lastVersion: " + stringSettings + ", currentVersion: " + apkVersionName);
//            StringBuilder sb2 = new StringBuilder();
//            sb2.append("checkUpdate updateCode: ");
//            sb2.append(stringSettings2);
//            Log.m19d(TAG, sb2.toString());
//            if (longSettings == 0) {
//                Settings.setSettings(Settings.KEY_UPDATE_CHECK_LAST_DATE, System.currentTimeMillis());
//                Log.m26i(TAG, "checkUpdate: the first launch app, don't check new version.");
//                return 2;
//            } else if (j < UPDATE_CHECK_THRESHOLD_TIME) {
//                Log.m26i(TAG, "checkUpdate: last update within threshold time, don't check new version.");
//                return 3;
//            } else {
//                Log.m19d(TAG, "checkUpdate available.");
//                return 1;
//            }
//        }
        return 1;
    }

    public void forceCheckUpdateAvailable() {
        Log.m19d(TAG, "forceCheckUpdateAvailable");
//        Settings.setSettings(Settings.KEY_UPDATE_CHECK_LAST_DATE, System.currentTimeMillis() - 172800000);
    }

    public void checkUpdate(StubListener stubListener) {
//        Log.m19d(TAG, "checkUpdate started.");
//        Settings.setSettings(Settings.KEY_UPDATE_CHECK_LAST_VERSION, VoiceNoteApplication.getApkVersionName());
//        Settings.setSettings(Settings.KEY_UPDATE_CHECK_LAST_DATE, System.currentTimeMillis());
//        Uri.Builder buildUpon = Uri.parse(UPDATE_CHECK_URL).buildUpon();
//        buildUpon.appendQueryParameter("appId", VoiceNoteApplication.getApkName()).appendQueryParameter("callerId", VoiceNoteApplication.getApkName()).appendQueryParameter("versionCode", VoiceNoteApplication.getApkVersionCode()).appendQueryParameter("deviceId", getDeviceId()).appendQueryParameter("mcc", getMcc()).appendQueryParameter("mnc", getMnc()).appendQueryParameter("csc", getCsc()).appendQueryParameter("sdkVer", getSdkVer()).appendQueryParameter("systemId", getSystemId()).appendQueryParameter("abiType", getAbiType()).appendQueryParameter("pd", getPd());
//        StubRequest stubRequest = new StubRequest();
//        stubRequest.setType(1);
//        stubRequest.setListener(stubListener);
//        stubRequest.setIsChina(isChina());
//        if (!isChina()) {
//            buildUpon.appendQueryParameter("extuk", getSecondaryUniqueId());
//            stubRequest.setUrl(buildUpon.toString());
//            stubRequest.run();
//            return;
//        }
//        this.mBuilder = buildUpon;
//        this.mStubRequest = stubRequest;
//        DeviceIdManager.getInstance(sAppContext).registerListener(this);
//        DeviceIdManager.getInstance(sAppContext).runOAID();
    }

    private void runStubRequestForChina(Uri.Builder builder, StubRequest stubRequest) {
        builder.appendQueryParameter("extuk", getSecondaryUniqueId());
        stubRequest.setUrl(builder.toString());
        stubRequest.run();
    }

    private static String getDeviceId() {
        return Build.MODEL.replaceFirst("SAMSUNG-", "");
    }

    private static String getMcc() {
        Context context = sAppContext;
        if (context == null) {
            Log.m32w(TAG, "getMcc: sAppContext is null!!!");
            return "";
        }
        String simOperator = ((TelephonyManager) context.getSystemService("phone")).getSimOperator();
        if (simOperator == null || simOperator.length() <= 3) {
            return "";
        }
        return simOperator.substring(0, 3);
    }

    private static boolean isChina() {
        return Arrays.asList(MCC_OF_CHINA).contains(getMcc());
    }

    private static String getMnc() {
        Context context = sAppContext;
        if (context == null) {
            Log.m32w(TAG, "getMnc: sAppContext is null!!!");
            return "";
        }
        String simOperator = ((TelephonyManager) context.getSystemService("phone")).getSimOperator();
        if (simOperator == null || simOperator.length() <= 3) {
            return "";
        }
        return simOperator.substring(3);
    }

    private static String getCsc() {
//        String salesCode = SemSystemProperties.getSalesCode();
//        return (salesCode == null || salesCode.isEmpty()) ? "NONE" : salesCode;
        return "NONE";
    }

    static String getSdkVer() {
        return String.valueOf(Build.VERSION.SDK_INT);
    }

    private static String getSystemId() {
        return String.valueOf(System.currentTimeMillis() - SystemClock.elapsedRealtime());
    }

    private static String getAbiType() {
        if (Build.SUPPORTED_64_BIT_ABIS.length > 0) {
            return "64";
        }
        return Build.SUPPORTED_32_BIT_ABIS.length > 0 ? "32" : "ex";
    }

    private static String getSecondaryUniqueId() {
        if (isChina()) {
            String oaid = DeviceIdManager.getInstance(sAppContext).getOAID();
            Log.m19d(TAG, "getSecondaryUniqueId OAID " + oaid);
            if (oaid != null && oaid.length() > 0) {
                return oaid;
            }
        }
        String string = Settings.Secure.getString(sAppContext.getContentResolver(), "android_id");
        Log.m19d(TAG, "getSecondaryUniqueId AndroidID " + string);
        return string;
    }

    static String getPd() {
        return isSamsungAppsQAFolderExisted() ? "1" : "0";
    }

    private static boolean isSamsungAppsQAFolderExisted() {
        if (!new File(Environment.getExternalStorageDirectory(), PD_TEST_PATH).isDirectory()) {
            return false;
        }
        Log.m19d(TAG, "QA folder is true");
        return true;
    }

    public void callGalaxyApps(Activity activity) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("samsungapps://ProductDetail/" + VoiceNoteApplication.getApkName()));
        intent.putExtra("type", "cover");
        intent.addFlags(335544352);
        try {
            activity.startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException e) {
            Log.m24e(TAG, "ActivityNotFoundException", (Throwable) e);
        }
    }

    public boolean hasAvailableGalaxyApp(Context context) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("samsungapps://ProductDetail/" + VoiceNoteApplication.getApkName()));
        intent.putExtra("type", "cover");
        intent.addFlags(335544352);
        return context.getPackageManager().resolveActivity(intent, 0) != null;
    }

    static boolean isNoMatchingApplication(StubData stubData) {
        return "0".equals(stubData.getResultCode());
    }

    static boolean isUpdateNotNecessary(StubData stubData) {
        return "1".equals(stubData.getResultCode());
    }

    static boolean isUpdateAvailable(StubData stubData) {
        return "2".equals(stubData.getResultCode());
    }

    static boolean isError(StubData stubData) {
        String resultCode = stubData.getResultCode();
        return !"0".equals(resultCode) && !"1".equals(resultCode) && !"2".equals(resultCode);
    }

    static String formatSize(String str) {
        try {
            long parseLong = Long.parseLong(str);
            if (parseLong >= 1048576) {
                return (parseLong / 1048576) + "MB";
            } else if (parseLong >= 1024) {
                return (parseLong / 1024) + "KB";
            } else {
                return parseLong + "Bytes";
            }
        } catch (NumberFormatException e) {
            Log.m24e(TAG, "NumberFormatException", (Throwable) e);
            return "Unknown size";
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: java.io.BufferedReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: javax.net.ssl.HttpsURLConnection} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: java.io.BufferedReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: java.io.BufferedReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: java.io.BufferedReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: java.io.BufferedReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v6, resolved type: java.io.BufferedReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v8, resolved type: javax.net.ssl.HttpsURLConnection} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: java.io.BufferedReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v10, resolved type: javax.net.ssl.HttpsURLConnection} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v12, resolved type: javax.net.ssl.HttpsURLConnection} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v13, resolved type: javax.net.ssl.HttpsURLConnection} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v16, resolved type: javax.net.ssl.HttpsURLConnection} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: java.io.BufferedReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v15, resolved type: java.io.BufferedReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v18, resolved type: javax.net.ssl.HttpsURLConnection} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v19, resolved type: javax.net.ssl.HttpsURLConnection} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v20, resolved type: javax.net.ssl.HttpsURLConnection} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v21, resolved type: javax.net.ssl.HttpsURLConnection} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v22, resolved type: java.io.BufferedReader} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0106, code lost:
        if (r5 == 0) goto L_0x018a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0108, code lost:
        r5.disconnect();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x0186, code lost:
        if (r5 == 0) goto L_0x018a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x018a, code lost:
        return r3;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x017a A[SYNTHETIC, Splitter:B:55:0x017a] */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x01c0 A[SYNTHETIC, Splitter:B:71:0x01c0] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x01ce  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getChinaURL() {
        /*
            android.content.Context r0 = sAppContext
            r1 = 0
            java.lang.String r2 = "UpdateProvider"
            android.content.SharedPreferences r0 = r0.getSharedPreferences(r2, r1)
            java.lang.String r3 = ""
            java.lang.String r4 = "cnVasURL"
            java.lang.String r0 = r0.getString(r4, r3)
            android.content.Context r5 = sAppContext
            android.content.SharedPreferences r5 = r5.getSharedPreferences(r2, r1)
            java.lang.String r6 = "cnVasTime"
            r7 = 0
            long r7 = r5.getLong(r6, r7)
            boolean r5 = r0.isEmpty()
            java.lang.String r9 = "cnVasUrl: "
            if (r5 != 0) goto L_0x0046
            long r10 = java.lang.System.currentTimeMillis()
            long r10 = r10 - r7
            r7 = 86400000(0x5265c00, double:4.2687272E-316)
            int r5 = (r10 > r7 ? 1 : (r10 == r7 ? 0 : -1))
            if (r5 > 0) goto L_0x0046
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r9)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r2, r1)
            return r0
        L_0x0046:
            r0 = 0
            java.net.URL r5 = new java.net.URL     // Catch:{ RuntimeException -> 0x018d, Exception -> 0x0145, all -> 0x0141 }
            java.lang.String r7 = "https://cn-ms.samsungapps.com/getCNVasURL.as"
            r5.<init>(r7)     // Catch:{ RuntimeException -> 0x018d, Exception -> 0x0145, all -> 0x0141 }
            java.lang.String r7 = "GET_CHINA_URL: https://cn-ms.samsungapps.com/getCNVasURL.as"
            com.sec.android.app.voicenote.provider.Log.m19d(r2, r7)     // Catch:{ RuntimeException -> 0x018d, Exception -> 0x0145, all -> 0x0141 }
            java.net.URLConnection r5 = r5.openConnection()     // Catch:{ RuntimeException -> 0x018d, Exception -> 0x0145, all -> 0x0141 }
            javax.net.ssl.HttpsURLConnection r5 = (javax.net.ssl.HttpsURLConnection) r5     // Catch:{ RuntimeException -> 0x018d, Exception -> 0x0145, all -> 0x0141 }
            r7 = 3000(0xbb8, float:4.204E-42)
            r5.setConnectTimeout(r7)     // Catch:{ RuntimeException -> 0x013d, Exception -> 0x013b }
            r5.setReadTimeout(r7)     // Catch:{ RuntimeException -> 0x013d, Exception -> 0x013b }
            r7 = 1
            r5.setInstanceFollowRedirects(r7)     // Catch:{ RuntimeException -> 0x013d, Exception -> 0x013b }
            int r8 = r5.getResponseCode()     // Catch:{ RuntimeException -> 0x013d, Exception -> 0x013b }
            r10 = 200(0xc8, float:2.8E-43)
            if (r10 != r8) goto L_0x0118
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x013d, Exception -> 0x013b }
            r8.<init>()     // Catch:{ RuntimeException -> 0x013d, Exception -> 0x013b }
            java.io.BufferedReader r10 = new java.io.BufferedReader     // Catch:{ RuntimeException -> 0x013d, Exception -> 0x013b }
            java.io.InputStreamReader r11 = new java.io.InputStreamReader     // Catch:{ RuntimeException -> 0x013d, Exception -> 0x013b }
            java.io.InputStream r12 = r5.getInputStream()     // Catch:{ RuntimeException -> 0x013d, Exception -> 0x013b }
            r11.<init>(r12)     // Catch:{ RuntimeException -> 0x013d, Exception -> 0x013b }
            r10.<init>(r11)     // Catch:{ RuntimeException -> 0x013d, Exception -> 0x013b }
        L_0x0080:
            java.lang.String r0 = r10.readLine()     // Catch:{ RuntimeException -> 0x0115, Exception -> 0x0111, all -> 0x010d }
            if (r0 == 0) goto L_0x009e
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x0115, Exception -> 0x0111, all -> 0x010d }
            r11.<init>()     // Catch:{ RuntimeException -> 0x0115, Exception -> 0x0111, all -> 0x010d }
            java.lang.String r12 = "line: "
            r11.append(r12)     // Catch:{ RuntimeException -> 0x0115, Exception -> 0x0111, all -> 0x010d }
            r11.append(r0)     // Catch:{ RuntimeException -> 0x0115, Exception -> 0x0111, all -> 0x010d }
            java.lang.String r11 = r11.toString()     // Catch:{ RuntimeException -> 0x0115, Exception -> 0x0111, all -> 0x010d }
            com.sec.android.app.voicenote.provider.Log.m19d(r2, r11)     // Catch:{ RuntimeException -> 0x0115, Exception -> 0x0111, all -> 0x010d }
            r8.append(r0)     // Catch:{ RuntimeException -> 0x0115, Exception -> 0x0111, all -> 0x010d }
            goto L_0x0080
        L_0x009e:
            org.xmlpull.v1.XmlPullParserFactory r0 = org.xmlpull.v1.XmlPullParserFactory.newInstance()     // Catch:{ RuntimeException -> 0x0115, Exception -> 0x0111, all -> 0x010d }
            org.xmlpull.v1.XmlPullParser r0 = r0.newPullParser()     // Catch:{ RuntimeException -> 0x0115, Exception -> 0x0111, all -> 0x010d }
            java.io.StringReader r11 = new java.io.StringReader     // Catch:{ RuntimeException -> 0x0115, Exception -> 0x0111, all -> 0x010d }
            java.lang.String r8 = r8.toString()     // Catch:{ RuntimeException -> 0x0115, Exception -> 0x0111, all -> 0x010d }
            r11.<init>(r8)     // Catch:{ RuntimeException -> 0x0115, Exception -> 0x0111, all -> 0x010d }
            r0.setInput(r11)     // Catch:{ RuntimeException -> 0x0115, Exception -> 0x0111, all -> 0x010d }
            int r8 = r0.getEventType()     // Catch:{ RuntimeException -> 0x0115, Exception -> 0x0111, all -> 0x010d }
        L_0x00b6:
            if (r8 == r7) goto L_0x00d1
            r11 = 2
            if (r8 == r11) goto L_0x00bc
            goto L_0x00cc
        L_0x00bc:
            java.lang.String r8 = r0.getName()     // Catch:{ RuntimeException -> 0x0115, Exception -> 0x0111, all -> 0x010d }
            java.lang.String r11 = "serverURL"
            boolean r8 = r11.equalsIgnoreCase(r8)     // Catch:{ RuntimeException -> 0x0115, Exception -> 0x0111, all -> 0x010d }
            if (r8 == 0) goto L_0x00cc
            java.lang.String r3 = r0.nextText()     // Catch:{ RuntimeException -> 0x0115, Exception -> 0x0111, all -> 0x010d }
        L_0x00cc:
            int r8 = r0.next()     // Catch:{ RuntimeException -> 0x0115, Exception -> 0x0111, all -> 0x010d }
            goto L_0x00b6
        L_0x00d1:
            android.content.Context r0 = sAppContext
            android.content.SharedPreferences r0 = r0.getSharedPreferences(r2, r1)
            android.content.SharedPreferences$Editor r0 = r0.edit()
            r0.putString(r4, r3)
            long r7 = java.lang.System.currentTimeMillis()
            r0.putLong(r6, r7)
            r0.apply()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r9)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r2, r0)
            r10.close()     // Catch:{ IOException -> 0x00fe }
            goto L_0x0106
        L_0x00fe:
            r0 = move-exception
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m22e(r2, r0)
        L_0x0106:
            if (r5 == 0) goto L_0x018a
        L_0x0108:
            r5.disconnect()
            goto L_0x018a
        L_0x010d:
            r0 = move-exception
            r7 = r0
            goto L_0x0194
        L_0x0111:
            r0 = move-exception
            r7 = r0
            r0 = r10
            goto L_0x0148
        L_0x0115:
            r0 = move-exception
            r7 = r0
            goto L_0x013f
        L_0x0118:
            java.io.IOException r7 = new java.io.IOException     // Catch:{ RuntimeException -> 0x013d, Exception -> 0x013b }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x013d, Exception -> 0x013b }
            r8.<init>()     // Catch:{ RuntimeException -> 0x013d, Exception -> 0x013b }
            java.lang.String r11 = "status code "
            r8.append(r11)     // Catch:{ RuntimeException -> 0x013d, Exception -> 0x013b }
            int r11 = r5.getResponseCode()     // Catch:{ RuntimeException -> 0x013d, Exception -> 0x013b }
            r8.append(r11)     // Catch:{ RuntimeException -> 0x013d, Exception -> 0x013b }
            java.lang.String r11 = " != "
            r8.append(r11)     // Catch:{ RuntimeException -> 0x013d, Exception -> 0x013b }
            r8.append(r10)     // Catch:{ RuntimeException -> 0x013d, Exception -> 0x013b }
            java.lang.String r8 = r8.toString()     // Catch:{ RuntimeException -> 0x013d, Exception -> 0x013b }
            r7.<init>(r8)     // Catch:{ RuntimeException -> 0x013d, Exception -> 0x013b }
            throw r7     // Catch:{ RuntimeException -> 0x013d, Exception -> 0x013b }
        L_0x013b:
            r7 = move-exception
            goto L_0x0148
        L_0x013d:
            r7 = move-exception
            r10 = r0
        L_0x013f:
            r0 = r5
            goto L_0x0190
        L_0x0141:
            r5 = move-exception
            r7 = r5
            r5 = r0
            goto L_0x0195
        L_0x0145:
            r5 = move-exception
            r7 = r5
            r5 = r0
        L_0x0148:
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x018b }
            com.sec.android.app.voicenote.provider.Log.m19d(r2, r7)     // Catch:{ all -> 0x018b }
            android.content.Context r7 = sAppContext
            android.content.SharedPreferences r1 = r7.getSharedPreferences(r2, r1)
            android.content.SharedPreferences$Editor r1 = r1.edit()
            r1.putString(r4, r3)
            long r7 = java.lang.System.currentTimeMillis()
            r1.putLong(r6, r7)
            r1.apply()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r9)
            r1.append(r3)
            java.lang.String r1 = r1.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r2, r1)
            if (r0 == 0) goto L_0x0186
            r0.close()     // Catch:{ IOException -> 0x017e }
            goto L_0x0186
        L_0x017e:
            r0 = move-exception
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m22e(r2, r0)
        L_0x0186:
            if (r5 == 0) goto L_0x018a
            goto L_0x0108
        L_0x018a:
            return r3
        L_0x018b:
            r7 = move-exception
            goto L_0x0195
        L_0x018d:
            r5 = move-exception
            r10 = r0
            r7 = r5
        L_0x0190:
            throw r7     // Catch:{ all -> 0x0191 }
        L_0x0191:
            r5 = move-exception
            r7 = r5
            r5 = r0
        L_0x0194:
            r0 = r10
        L_0x0195:
            android.content.Context r8 = sAppContext
            android.content.SharedPreferences r1 = r8.getSharedPreferences(r2, r1)
            android.content.SharedPreferences$Editor r1 = r1.edit()
            r1.putString(r4, r3)
            long r10 = java.lang.System.currentTimeMillis()
            r1.putLong(r6, r10)
            r1.apply()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r9)
            r1.append(r3)
            java.lang.String r1 = r1.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r2, r1)
            if (r0 == 0) goto L_0x01cc
            r0.close()     // Catch:{ IOException -> 0x01c4 }
            goto L_0x01cc
        L_0x01c4:
            r0 = move-exception
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m22e(r2, r0)
        L_0x01cc:
            if (r5 == 0) goto L_0x01d1
            r5.disconnect()
        L_0x01d1:
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.provider.UpdateProvider.getChinaURL():java.lang.String");
    }
}
