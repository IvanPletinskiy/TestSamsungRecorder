package com.sec.android.app.voicenote.provider;

import android.content.Context;
import android.content.SharedPreferences;
import java.lang.ref.WeakReference;

public class Settings {
    public static final int ADV_REC_INFO_DIALOG_OFF = 1;
    public static final int ADV_REC_INFO_DIALOG_ON = 0;
    public static final boolean BLUETOOTH_SCO_CONNECT_OFF = false;
    public static final boolean BLUETOOTH_SCO_CONNECT_ON = true;
    public static final int CATEGORIZED_LIST = 1;
    public static final int DEFAULT_LIST = 0;
    public static final String KEY_ABOUT = "about";
    public static final String KEY_BIXBY_FILE_NAME_ID = "bixby_file_name_id";
    public static final String KEY_BIXBY_START_DATA = "bixby_start_data";
    public static final String KEY_BLUETOOTH_SCO_CONNECT = "bluetooth_sco_connect";
    public static final String KEY_CALL_REJECT_COUNT = "call_reject_count";
    public static final String KEY_CATEGORY_ID = "category_id";
    public static final String KEY_CATEGORY_LABEL_ID = "category_label_id";
    public static final String KEY_CATEGORY_LABEL_POSITION = "category_label_position";
    public static final String KEY_CATEGORY_LABEL_TITLE = "category_label_title";
    public static final String KEY_CURRENT_GALAXY_APP_VERSION = "current_galaxy_app_version";
    public static final String KEY_DATA_CHECK_SHOW_AGAIN = "data_check_show_again";
    public static final String KEY_ENABLE_NOTI_SOUND = "enable_noti_sound";
    public static final String KEY_ENABLE_SYS_SOUND = "enable_system_sound";
    public static final String KEY_FIRST_DELETE_FILE = "first_delete_file";
    public static final String KEY_FIRST_LAUNCH = "first_launch";
    public static final String KEY_FORCE_SYSTEM_PERMISSION_DIALOG = "force_system_permission_dialog";
    public static final String KEY_FORCE_SYSTEM_PERMISSION_DIALOG_PHONE = "force_system_permission_dialog_phone";
    public static final String KEY_HELP_SHOW_CONVERT_STT_GUIDE = "help_convert_stt";
    public static final String KEY_HELP_SHOW_OVERWRITE_GUIDE = "help_overwrite";
    public static final String KEY_IS_FIRST_DELETE_VOICE_FILE = "is_first_delete_voice_file";
    public static final String KEY_LAST_DISMISS_UPDATE_TIPS_APP_VERSION = "last_dismiss_update_tips_app_version";
    public static final String KEY_LATEST_SDK_VERSION = "latest_sdk_version";
    public static final String KEY_LIST_MODE = "list_mode";
    public static final String KEY_MMS_MAX_SIZE = "mms_max_size";
    public static final String KEY_MMS_MAX_SIZE_RECEIVED = "mms_max_size_received";
    public static final String KEY_NFC_LABEL_INFO = "nfc_label_info";
    public static final String KEY_PLAY_CONTINUOUSLY = "play_continuously";
    public static final String KEY_PLAY_MODE = "play_mode";
    public static final String KEY_PLAY_WITH_RECEIVER = "play_on_receiver";
    public static String KEY_PREFERENCES = "com.sec.android.app.voicenote_preferences";
    public static final String KEY_PRIVATE_DO_NOT_SHOW_AGAIN = "private_do_not_show_again";
    public static final String KEY_PRIVATE_SELECT_MODE = "private_select_mode";
    public static final String KEY_RECORDING_QUALITY = "recording_quality";
    public static final String KEY_RECORD_MODE = "record_mode";
    public static final String KEY_REC_CALL_REJECT = "rec_call_reject";
    public static final String KEY_REC_QUALITY = "rec_quality";
    public static final String KEY_REC_STEREO = "rec_stereo";
    public static final String KEY_SDCARD_PREVIOUS_STATE = "sdcard_previous_state";
    public static final String KEY_SHOW_CONVERSATION_INFO = "show_conversation_info";
    public static final String KEY_SHOW_STEREO_INFO = "show_stereo_info";
    public static final String KEY_SHOW_STT_REC_INFO = "show_stt_rec_info";
    public static final String KEY_SIMPLE_PLAY_MODE = "simple_play_mode";
    public static final String KEY_SIMPLE_RECORD_MODE = "simple_record_mode";
    public static final String KEY_SORT_MODE = "sort_mode";
    public static final String KEY_SPEAKERPHONE_MODE = "speakerphone_mode";
    public static final String KEY_STORAGE = "storage";
    public static final String KEY_STT_LANGUAGE_LOCALE = "stt_language_locale";
    public static final String KEY_STT_LANGUAGE_TEXT = "stt_language_text";
    public static final String KEY_TRASH_IS_TURN_ON = "trash_is_on";
    public static final String KEY_UPDATE_CHECK_FROM_GALAXY_APPS = "update_check_from_galaxy_apps";
    public static final String KEY_UPDATE_CHECK_LAST_DATE = "update_check_last_date";
    public static final String KEY_UPDATE_CHECK_LAST_VERSION = "update_check_last_version";
    public static final String KEY_UPDATE_CHECK_RESULT_CODE = "update_check_result_code";
    public static final String KEY_VIEW_AS = "view_as";
    public static final String KEY_VOLUME_STREAM_NOTIFICATION = "volume_stream_notification";
    public static final String KEY_VOLUME_STREAM_SYSTEM = "volume_stream_system";
    public static final int LABEL_DB_NONE_ID = 0;
    public static final boolean PLAY_CONTINUOUSLY_OFF = false;
    public static final boolean PLAY_CONTINUOUSLY_ON = true;
    public static final int QUALITY_DEPRECATED = -1;
    public static final int QUALITY_MMS = 1;
    public static final int QUALITY_NORMAL = 0;
    public static final boolean RECORDING_CALL_REJECT_OFF = false;
    public static final boolean RECORDING_CALL_REJECT_ON = true;
    public static final int RECORDING_QUALITY_HIGH = 2;
    public static final int RECORDING_QUALITY_LOW = 0;
    public static final int RECORDING_QUALITY_MMS = 3;
    public static final int RECORDING_QUALITY_NORMAL = 1;
    public static final boolean RECORDING_STEREO_OFF = false;
    public static final boolean RECORDING_STEREO_ON = true;
    public static final int SDCARD_MOUNT = 1;
    public static final int SDCARD_UNMOUNT = 0;
    public static final int SORT_DURATION_ASC = 2;
    public static final int SORT_DURATION_DES = 5;
    public static final int SORT_NAME_ASC = 1;
    public static final int SORT_NAME_DES = 4;
    public static final int SORT_TIME_ASC = 0;
    public static final int SORT_TIME_DES = 3;
    public static final int STORAGE_AUTO = 2;
    public static final int STORAGE_MEMORYCARD = 1;
    public static final int STORAGE_PHONE = 0;
    private static final String TAG = "Settings";
    private static WeakReference<Context> mAppContext;

    public static void setApplicationContext(WeakReference<Context> weakReference) {
        mAppContext = weakReference;
        WeakReference<Context> weakReference2 = mAppContext;
        if (weakReference2 != null && weakReference2.get() != null) {
            KEY_PREFERENCES = ((Context) mAppContext.get()).getPackageName() + "_preferences";
        }
    }

    public static void setSettings(String str, int i) {
        WeakReference<Context> weakReference = mAppContext;
        if (weakReference != null && weakReference.get() != null) {
            SharedPreferences.Editor edit = ((Context) mAppContext.get()).getSharedPreferences(KEY_PREFERENCES, 0).edit();
            edit.putString(str, String.valueOf(i));
            edit.apply();
        }
    }

    public static void setSettings(String str, long j) {
        WeakReference<Context> weakReference = mAppContext;
        if (weakReference != null && weakReference.get() != null) {
            SharedPreferences.Editor edit = ((Context) mAppContext.get()).getSharedPreferences(KEY_PREFERENCES, 0).edit();
            edit.putLong(str, j);
            edit.apply();
        }
    }

    public static void setSettings(String str, String str2) {
        WeakReference<Context> weakReference = mAppContext;
        if (weakReference != null && weakReference.get() != null) {
            SharedPreferences.Editor edit = ((Context) mAppContext.get()).getSharedPreferences(KEY_PREFERENCES, 0).edit();
            edit.putString(str, str2);
            edit.apply();
        }
    }

    public static void setSettings(String str, boolean z) {
        WeakReference<Context> weakReference = mAppContext;
        if (weakReference != null && weakReference.get() != null) {
            SharedPreferences.Editor edit = ((Context) mAppContext.get()).getSharedPreferences(KEY_PREFERENCES, 0).edit();
            edit.putBoolean(str, z);
            edit.apply();
        }
    }

    public static boolean getBooleanSettings(String str) {
        WeakReference<Context> weakReference = mAppContext;
        if (weakReference == null || weakReference.get() == null) {
            return false;
        }
        return ((Context) mAppContext.get()).getSharedPreferences(KEY_PREFERENCES, 0).getBoolean(str, false);
    }

    public static boolean getBooleanSettings(String str, boolean z) {
        WeakReference<Context> weakReference = mAppContext;
        return (weakReference == null || weakReference.get() == null) ? z : ((Context) mAppContext.get()).getSharedPreferences(KEY_PREFERENCES, 0).getBoolean(str, z);
    }

    public static String getStringSettings(String str) {
        WeakReference<Context> weakReference = mAppContext;
        if (weakReference == null || weakReference.get() == null) {
            return null;
        }
        return ((Context) mAppContext.get()).getSharedPreferences(KEY_PREFERENCES, 0).getString(str, (String) null);
    }

    public static String getStringSettings(String str, String str2) {
        WeakReference<Context> weakReference = mAppContext;
        return (weakReference == null || weakReference.get() == null) ? str2 : ((Context) mAppContext.get()).getSharedPreferences(KEY_PREFERENCES, 0).getString(str, str2);
    }

    public static int getIntSettings(String str, int i) {
        WeakReference<Context> weakReference = mAppContext;
        if (weakReference == null || weakReference.get() == null) {
            return 0;
        }
        return Integer.valueOf(((Context) mAppContext.get()).getSharedPreferences(KEY_PREFERENCES, 0).getString(str, String.valueOf(i))).intValue();
    }

    public static long getLongSettings(String str, long j) {
        WeakReference<Context> weakReference = mAppContext;
        if (weakReference == null || weakReference.get() == null) {
            return 0;
        }
        return ((Context) mAppContext.get()).getSharedPreferences(KEY_PREFERENCES, 0).getLong(str, j);
    }

    public static void setMmsMaxSize(String str, long j) {
        SharedPreferences.Editor edit = ((Context) mAppContext.get()).getSharedPreferences(KEY_MMS_MAX_SIZE, 0).edit();
        edit.putLong(KEY_MMS_MAX_SIZE, j);
        edit.putString(KEY_MMS_MAX_SIZE_RECEIVED, str);
        edit.apply();
    }

    public static long getMmsMaxSize() {
        SharedPreferences sharedPreferences = ((Context) mAppContext.get()).getSharedPreferences(KEY_MMS_MAX_SIZE, 0);
        if (sharedPreferences.getString(KEY_MMS_MAX_SIZE_RECEIVED, (String) null) == null) {
            PreferenceProvider.getMmsMaxSize((Context) mAppContext.get());
        }
        return sharedPreferences.getLong(KEY_MMS_MAX_SIZE, PreferenceProvider.DEFAULT_MMS_MAX_SIZE);
    }

    public static boolean isEnabledShowButtonBG() {
        WeakReference<Context> weakReference = mAppContext;
//        return (weakReference == null || weakReference.get() == null || Settings.System.getInt(((Context) mAppContext.get()).getContentResolver(), "show_button_background", 0) == 0) ? false : true;
        return false;
    }
}
