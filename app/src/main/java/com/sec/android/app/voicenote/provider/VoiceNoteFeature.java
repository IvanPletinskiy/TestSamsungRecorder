package com.sec.android.app.voicenote.provider;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.sec.android.app.voicenote.service.AudioFormat;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.MetadataRepository;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;

public class VoiceNoteFeature {
    private static final boolean FLAG_ENABLE_TRANSLATION = true;
    public static final boolean FLAG_IS_ENABLE_SURVEY_MODE = isSurveyModeEnabled();
    public static final boolean FLAG_IS_OS_UPGRADE = isOsUpgrade();
    public static final boolean FLAG_IS_SEM_AVAILABLE = isSupportSep();
    public static final boolean FLAG_IS_TABLET = isTablet();
    public static final boolean FLAG_KEEP_RECORDING_WHEN_ACCEPT_CALL = true;
    public static final boolean FLAG_KEEP_RECORDING_WHEN_AUDIOFOCUS_LOSS = false;
    public static final boolean FLAG_KEEP_RECORDING_WHEN_OPEN_SPECIAL_APP = true;
    public static final boolean FLAG_SUPPORT_BLE_SPEN_AIR_ACTION = isSupportBleSpenAirAction();
    public static final boolean FLAG_SUPPORT_BLUETOOTH_RECORDING = false;
    public static final boolean FLAG_SUPPORT_CALL_HISTORY = isSupportCallHistory();
    public static final boolean FLAG_SUPPORT_CHINA_WLAN = isSupportWLANString();
    public static final boolean FLAG_SUPPORT_DATA_CHECK_POPUP = isSupportDataPromptPopup();
    public static final boolean FLAG_SUPPORT_INTERVIEW = isSupportInterview();
    public static final boolean FLAG_SUPPORT_KNOX_DESKTOP = isSupportDesktopMode();
    public static final boolean FLAG_SUPPORT_MINIMIZE_SIP = isSupportMinimizeSIP();
    public static final boolean FLAG_SUPPORT_NFC_CARDMODE = isSupportNfcCardMode();
    public static final boolean FLAG_SUPPORT_NFC_RWP2P = isSupportNfcRwp2p();
    public static final boolean FLAG_SUPPORT_SAMSUNG_ANALYTICS = true;
    public static final boolean FLAG_SUPPORT_SHOW_REJECT_CALL_NUMBER = isSupportShowRejectCallCount();
    public static final boolean FLAG_SUPPORT_STEREO = FLAG_SUPPORT_INTERVIEW;
    private static final String[] PRODUCT_NAME_BLOOM = {"bloom", "SCV47"};
    private static final String[] PRODUCT_NAME_WINNER = {"winner", "SCV44", "zodiac"};
    private static final String[] SUPPORTED_MODES = getSupportModeList();
    private static final String TAG = "VoiceNoteFeature";
    private static final String TAG_CALL_HISTORY = "CscFeature_VoiceCall_ConfigRecording";
    private static final String TAG_CARDMODE = "CardMode";
    private static final String TAG_INTERVIEW = "interview";
    private static final String TAG_RECORDING_ALLOWED_BY_MENU = "RecordingAllowedByMenu";
    private static final String TAG_RWP2P = "RwP2p";
    private static final String TAG_VOICEMEMO = "voicememo";
    private static final String TAG_WLAN = "WLAN";
    public static int[] mSupportEditAndVoicememo = {-1, -1};

    public static void init() {
    }

    private static boolean isSupportShowRejectCallCount() {
        return true;
    }

    public static boolean isSupportSep() {
//        try {
//            SemFloatingFeature.getInstance();
//            SemCscFeature.getInstance();
//            return true;
//        } catch (NoClassDefFoundError e) {
//            Log.m24e(TAG, "NoClassDefFoundError !", (Throwable) e);
//            return false;
//        }
        return false;
    }

    private static boolean isSupportBleSpenAirAction() {
//        try {
//            return SemFloatingFeature.getInstance().getBoolean("SEC_FLOATING_FEATURE_COMMON_SUPPORT_BLE_SPEN");
//        } catch (Exception e) {
//            Log.m22e(TAG, e.toString());
//            return false;
//        }
        return false;
    }

    public static boolean FLAG_SUPPORT_VOICE_MEMO(Context context) {
//        int[] iArr = mSupportEditAndVoicememo;
//        if (iArr[1] == -1) {
//            iArr[1] = isSupportVoiceMemo(context);
//        }
//        if (mSupportEditAndVoicememo[1] == 1) {
//            return true;
//        }
        return false;
    }

    public static boolean isGateEnabled() {
//        return SemGateConfig.isGateEnabled() || SemGateConfig.isGateLcdtextEnabled();
        return false;
    }

    public static boolean isSurveyModeEnabled() {
//        try {
//            return SemFloatingFeature.getInstance().getBoolean("SEC_FLOATING_FEATURE_CONTEXTSERVICE_ENABLE_SURVEY_MODE", false);
//        } catch (Exception unused) {
//            return false;
//        }
        return false;
    }

    private static boolean isSupportNfcCardMode() {
//        try {
//            String string = SemCscFeature.getInstance().getString("CscFeature_VoiceRecorder_ConfigNfcMode", (String) null);
//            if (string == null || !string.equalsIgnoreCase(TAG_CARDMODE)) {
//                return false;
//            }
//            return true;
//        } catch (Exception unused) {
//            Log.m22e(TAG, "isSupportNfcCardMode exception");
//            return false;
//        }
        return false;
    }

    private static boolean isSupportNfcRwp2p() {
//        try {
//            String string = SemCscFeature.getInstance().getString("CscFeature_VoiceRecorder_ConfigNfcMode", (String) null);
//            if (string == null || !string.equalsIgnoreCase(TAG_RWP2P)) {
//                return false;
//            }
//            return true;
//        } catch (Exception unused) {
//            Log.m22e(TAG, "isSupportNfcRwp2p exception");
//            return false;
//        }
        return false;
    }

    private static boolean isSupportMinimizeSIP() {
//        try {
//            return SemCscFeature.getInstance().getBoolean("CscFeature_Common_SupportMinimizedSip", false);
//        } catch (Exception unused) {
//            Log.m22e(TAG, "isSupportMinimizeSIP exception");
//            return false;
//        }
        return false;
    }

    private static boolean isSupportWLANString() {
//        String str = null;
//        try {
//            str = SemCscFeature.getInstance().getString("CscFeature_Common_ReplaceStringWifi", (String) null);
//        } catch (Exception unused) {
//            Log.m22e(TAG, "isSupportWlanString exception");
//        }
//        return str != null && str.equalsIgnoreCase(TAG_WLAN);
        return false;
    }

    private static boolean isSupportDataPromptPopup() {
//        try {
//            return SemCscFeature.getInstance().getBoolean("CscFeature_Music_SupportDataPromptPopup", false);
//        } catch (Exception unused) {
//            Log.m22e(TAG, "isSupportMinimizeSIP exception");
//            return false;
//        }
        return false;
    }

    private static String[] getSupportModeList() {
//        String str;
//        try {
//            str = SemFloatingFeature.getInstance().getString("SEC_FLOATING_FEATURE_VOICERECORDER_CONFIG_DEF_MODE");
//        } catch (Exception unused) {
//            str = null;
//        }
//        if (str != null) {
//            return str.split(",");
//        }
        return null;
    }

    private static boolean isSupportInterview() {
        String[] strArr = SUPPORTED_MODES;
        if (strArr == null) {
            return false;
        }
        for (String equalsIgnoreCase : strArr) {
            if (equalsIgnoreCase.equalsIgnoreCase("interview")) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSupportCallHistory() {
//        String str = "";
//        try {
//            str = SemCscFeature.getInstance().getString(TAG_CALL_HISTORY, (String) null);
//            Log.m19d(TAG, "configRecording = " + str);
//        } catch (Exception unused) {
//        }
//        return ("RecordingAllowed".equalsIgnoreCase(str) || TAG_RECORDING_ALLOWED_BY_MENU.equalsIgnoreCase(str)) && !TAG_RECORDING_ALLOWED_BY_MENU.equalsIgnoreCase(str);
        return false;
    }

    private static boolean isSupportVoiceMemo(Context context) {
        String[] strArr = SUPPORTED_MODES;
        if (!(strArr == null || context == null)) {
            int length = strArr.length;
            int i = 0;
            while (i < length) {
                if (!strArr[i].equalsIgnoreCase(TAG_VOICEMEMO)) {
                    i++;
                } else if (!SecureFolderProvider.isSecureFolderSupported()) {
                    return true;
                } else {
                    SecureFolderProvider.getKnoxMenuList(context);
                    if (SecureFolderProvider.isInsideSecureFolder()) {
                        return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean FLAG_IS_FOLDER_PHONE(Context context) {
        return context.getPackageManager().hasSystemFeature("com.sec.feature.folder_type");
    }

    private static boolean isSupportDesktopMode() {
//        try {
//            return SemFloatingFeature.getInstance().getBoolean("SEC_FLOATING_FEATURE_COMMON_SUPPORT_KNOX_DESKTOP", false);
//        } catch (Exception unused) {
//            return false;
//        }
        return false;
    }

    public static boolean FLAG_IS_NOT_SUPPORT_TRANSLATION(Activity activity) {
        if (activity == null) {
            Log.m22e(TAG, "isSupportTranslation - mActivity is null");
            return true;
        }
        MetadataRepository instance = MetadataRepository.getInstance();
        if (!FLAG_SUPPORT_VOICE_MEMO(activity) || Engine.getInstance().getPath().endsWith(AudioFormat.ExtType.EXT_AMR) || Engine.getInstance().getPath().endsWith(AudioFormat.ExtType.EXT_3GA) || DesktopModeProvider.isDesktopMode() || SecureFolderProvider.isInsideSecureFolder() || instance.getRecordMode() == 4 || instance.getRecordMode() == 2) {
            return true;
        }
        return false;
    }

    public static boolean FLAG_IS_WINNER() {
        for (String contains : PRODUCT_NAME_WINNER) {
            if (Build.PRODUCT.contains(contains)) {
                return true;
            }
        }
        return false;
    }

    public static boolean FLAG_IS_BLOOM() {
        for (String contains : PRODUCT_NAME_BLOOM) {
            if (Build.PRODUCT.contains(contains)) {
                return true;
            }
        }
        return false;
    }

    public static boolean FLAG_IS_SUPPORT_LED_COVER() {
//        int i;
//        try {
//            i = SemFloatingFeature.getInstance().getInt("SEC_FLOATING_FEATURE_FRAMEWORK_CONFIG_NFC_LED_COVER_LEVEL");
//        } catch (Exception unused) {
//            i = 0;
//        }
//        return i >= 60;
        return false;
    }

    public static boolean isChina() {
//        return "ChinaNalSecurity".equals(SemCscFeature.getInstance().getString("CscFeature_Common_ConfigLocalSecurityPolicy")) || ("CHINA".equalsIgnoreCase(SemSystemProperties.getCountryCode()) && "PAP".equals(SemSystemProperties.getSalesCode()));
        return false;
    }

    public static boolean isSupportPSLTE_KOR() {
//        return "PSLTE_KOR".equals(SemCscFeature.getInstance().getString("CscFeature_Common_ConfgB2B"));
        return false;
    }

    public static boolean isSupportHoveringUI() {
//        return SemFloatingFeature.getInstance().getInt("SEC_FLOATING_FEATURE_FRAMEWORK_CONFIG_SPEN_VERSION", -1) >= 0;
        return false;
    }

    private static boolean isTablet() {
        PackageManager packageManager = VoiceNoteApplication.getApplication().getPackageManager();
        return packageManager != null && packageManager.hasSystemFeature("com.samsung.feature.device_category_tablet");
    }

    private static boolean isOsUpgrade() {
        int i = Build.VERSION.SDK_INT;
        int intSettings = Settings.getIntSettings(Settings.KEY_LATEST_SDK_VERSION, 0);
        if (intSettings == 0) {
            Settings.setSettings(Settings.KEY_LATEST_SDK_VERSION, i);
            intSettings = i;
        }
        if (i <= intSettings) {
            return false;
        }
        Settings.setSettings(Settings.KEY_LATEST_SDK_VERSION, i);
        return true;
    }
}
