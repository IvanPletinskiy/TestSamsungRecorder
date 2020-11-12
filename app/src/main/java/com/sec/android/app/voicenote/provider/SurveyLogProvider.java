package com.sec.android.app.voicenote.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SurveyLogProvider {
    public static final String EXTRA_EDIT_TYPE_DEL = "Delete";
    public static final String EXTRA_EDIT_TYPE_TRIM = "Trim";
    public static final String EXTRA_EXPAND_MODE = "EXPANDED";
    public static final String EXTRA_FULL_PLAYER = "on full player";
    public static final String EXTRA_MINI_PLAYER = "on mini player";
    public static final String EXTRA_MODE_INTERVIEW = "Interview";
    public static final String EXTRA_MODE_MEMO = "Voice Memo";
    public static final String EXTRA_MODE_NORMAL = "Normal";
    public static final String EXTRA_OVERWRITE_EDIT = "On Edit";
    public static final String EXTRA_OVERWRITE_RECORDING = "On Recording";
    public static final String EXTRA_PLAY_TYPE_FULL = "Full player";
    public static final String EXTRA_PLAY_TYPE_MINI = "Mini player";
    public static final String EXTRA_SEEK_SKIP_INTERVAL = "Skip Interval";
    public static final String EXTRA_SEEK_WAVE = "Wave";
    public static final String EXTRA_SELECT_MODE = "on select mode";
    public static final String EXTRA_SETTINGS_ABOUT = "About voice recorder";
    public static final String EXTRA_SETTINGS_ACCESS = "access settings";
    public static final String EXTRA_SETTINGS_CALL_REJ = "Block calls while recording";
    public static final String EXTRA_SETTINGS_PLAY_CONT = "Play continuously";
    public static final String EXTRA_SETTINGS_QUALITY = "Recording quality";
    public static final String EXTRA_SETTINGS_STEREO = "Record audio in stereo";
    public static final String EXTRA_SHRINK_MODE = "SHRUNK";
    public static final String EXTRA_SURVEY_INTERVIEW = "INTERVIEW";
    public static final String EXTRA_SURVEY_MEETING = "MEETING";
    public static final String EXTRA_SURVEY_NORMAL = "NORMAL";
    public static final String EXTRA_SURVEY_VOICEMEMO = "MEMO";
    private static final String PACKAGE_NAME = "com.sec.android.app.voicenote";
    public static final int SETTINGS_ABOUT = 5;
    public static final int SETTINGS_ACCESS = 3;
    public static final int SETTINGS_CALL_REJ = 4;
    public static final int SETTINGS_PLAY_CONT = 6;
    public static final int SETTINGS_QUALITY = 1;
    public static final int SETTINGS_STEREO = 2;
    public static final String SURVEY_BOOKMARK = "BOOK";
    public static final String SURVEY_BOOKMARK_ALL = "BMAL";
    public static final String SURVEY_CONVERT = "CONVERT";
    public static final String SURVEY_DELETE = "DELE";
    public static final String SURVEY_EDIT = "EDIT";
    public static final String SURVEY_EDIT_TYPE = "EDTY";
    public static final String SURVEY_LANGUAGE = "LANG";
    public static final String SURVEY_LIST_SHARE = "LSHA";
    public static final String SURVEY_MODE = "MODE";
    public static final String SURVEY_MOVE_TO_PRIVATE = "MOVP";
    public static final String SURVEY_NFC_WRITE = "NFCW";
    public static final String SURVEY_OVERWRITE = "OVWR";
    public static final String SURVEY_PLAYER_SHARE = "PSHA";
    public static final String SURVEY_PLAYSPEED = "SPED";
    public static final String SURVEY_PLAY_MODE = "EXPN";
    public static final String SURVEY_PLAY_TYPE = "PLAY";
    public static final String SURVEY_PLAY_VIA_PRIVATE = "PRIV";
    public static final String SURVEY_RECORD = "RECO";
    public static final String SURVEY_REJECT_CALL = "REJC";
    public static final String SURVEY_RENAME = "RNAM";
    public static final String SURVEY_RENAME_NEW_REC = "NNAM";
    public static final String SURVEY_REPEAT = "REPT";
    public static final String SURVEY_SAVE = "SAVE";
    public static final String SURVEY_SEARCH = "SRCH";
    public static final String SURVEY_SEEK = "SEEK";
    public static final String SURVEY_SELECT_TRACK = "SELT";
    public static final String SURVEY_SETTINGS = "SETT";
    public static final String SURVEY_SKIPSILENCE = "SKIP";
    private static final String TAG = "SurveyLogProvider";
    public static final int VALUE_VIA_PRIVATE_OFF = 0;
    public static final int VALUE_VIA_PRIVATE_ON = 1000;
    private static Context mAppContext;
    private static String mStartRecordingUtcTimeForLogging;

    public static void setApplicationContext(Context context) {
        mAppContext = context;
    }

    public static void startRecordingLog() {
        mStartRecordingUtcTimeForLogging = getCurrentUtcTime();
    }

    public static void insertRecordingLog(String str, int i) {
        if (VoiceNoteFeature.FLAG_IS_ENABLE_SURVEY_MODE && isLoggingEnabled()) {
            String str2 = mStartRecordingUtcTimeForLogging;
            String currentUtcTime = getCurrentUtcTime();
            if (str == null || str2 == null || currentUtcTime == null || i < 0) {
                Log.m32w(TAG, "ContextProvider cannot insert log data are invalidated");
                Log.m19d(TAG, "insertLog contentUri : " + str);
                Log.m19d(TAG, "insertLog startTime : " + str2);
                Log.m19d(TAG, "insertLog stopTime : " + currentUtcTime);
                Log.m19d(TAG, "insertLog duration : " + i);
                return;
            }
            Uri parse = Uri.parse("content://com.samsung.android.providers.context.log.record_audio");
            try {
                ContentResolver contentResolver = mAppContext.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put("app_id", "com.sec.android.app.voicenote");
                contentValues.put("uri", str);
                contentValues.put("start_time", str2);
                contentValues.put("stop_time", currentUtcTime);
                contentValues.put("duration", Integer.valueOf(i));
                contentResolver.insert(parse, contentValues);
                Log.m19d(TAG, "ContextProvider insertion operation is performed.");
            } catch (Exception e) {
                Log.m22e(TAG, "Error while using the ContextProvider : " + e);
            }
        }
    }

    public static void insertFeatureLog(String str, int i) {
        String str2 = null;
        if (VoiceNoteFeature.FLAG_IS_ENABLE_SURVEY_MODE && isLoggingEnabled()) {
            if (str == null) {
                Log.m32w(TAG, "ContextProvider cannot insert log data are invalidated");
                Log.m32w(TAG, "insertFeatureLog - feature is NULL");
                return;
            }
            char c = 65535;
            switch (str.hashCode()) {
                case 2042294:
                    if (str.equals(SURVEY_BOOKMARK_ALL)) {
                        c = 2;
                        break;
                    }
                    break;
                case 2094522:
                    if (str.equals(SURVEY_DELETE)) {
                        c = 11;
                        break;
                    }
                    break;
                case 2123274:
                    if (str.equals(SURVEY_EDIT)) {
                        c = 3;
                        break;
                    }
                    break;
                case 2123620:
                    if (str.equals(SURVEY_EDIT_TYPE)) {
                        c = 7;
                        break;
                    }
                    break;
                case 2142705:
                    if (str.equals(SURVEY_PLAY_MODE)) {
                        c = 1;
                        break;
                    }
                    break;
                case 2372003:
                    if (str.equals(SURVEY_MODE)) {
                        c = 13;
                        break;
                    }
                    break;
                case 2393132:
                    if (str.equals(SURVEY_NFC_WRITE)) {
                        c = 5;
                        break;
                    }
                    break;
                case 2438914:
                    if (str.equals(SURVEY_OVERWRITE)) {
                        c = 9;
                        break;
                    }
                    break;
                case 2458420:
                    if (str.equals(SURVEY_PLAY_TYPE)) {
                        c = 6;
                        break;
                    }
                    break;
                case 2519912:
                    if (str.equals(SURVEY_RENAME)) {
                        c = 10;
                        break;
                    }
                    break;
                case 2541176:
                    if (str.equals(SURVEY_SEEK)) {
                        c = 8;
                        break;
                    }
                    break;
                case 2541650:
                    if (str.equals(SURVEY_SETTINGS)) {
                        c = 12;
                        break;
                    }
                    break;
                case 2547071:
                    if (str.equals(SURVEY_SKIPSILENCE)) {
                        c = 0;
                        break;
                    }
                    break;
                case 1669573011:
                    if (str.equals(SURVEY_CONVERT)) {
                        c = 4;
                        break;
                    }
                    break;
            }
            String str3 = null;
            switch (c) {
                case 0:
                    if (i != 1) {
                        str2 = "OFF";
                        break;
                    } else {
                        str2 = "ON";
                        break;
                    }
                case 1:
                    if (i != 1) {
                        str2 = EXTRA_EXPAND_MODE;
                        break;
                    } else {
                        str2 = EXTRA_SHRINK_MODE;
                        break;
                    }
                case 2:
                case 3:
                case 4:
                case 5:
                    break;
                case 6:
                    if (i != 1) {
                        str2 = EXTRA_PLAY_TYPE_FULL;
                        break;
                    } else {
                        str2 = EXTRA_PLAY_TYPE_MINI;
                        break;
                    }
                case 7:
                    if (i != 1) {
                        str2 = EXTRA_EDIT_TYPE_DEL;
                        break;
                    } else {
                        str2 = EXTRA_EDIT_TYPE_TRIM;
                        break;
                    }
                case 8:
                    if (i != 1) {
                        str2 = EXTRA_SEEK_WAVE;
                        break;
                    } else {
                        str2 = EXTRA_SEEK_SKIP_INTERVAL;
                        break;
                    }
                case 9:
                    if (i != 1) {
                        str2 = EXTRA_OVERWRITE_EDIT;
                        break;
                    } else {
                        str2 = EXTRA_OVERWRITE_RECORDING;
                        break;
                    }
                case 10:
                case 11:
                    Log.m19d(TAG, "Test SURVEY_RENAME mode = " + i);
                    if (i != 3) {
                        if (i != 4) {
                            if (i == 5) {
                                str3 = EXTRA_SELECT_MODE;
                                break;
                            }
                        } else {
                            str3 = EXTRA_FULL_PLAYER;
                            break;
                        }
                    } else {
                        str3 = EXTRA_MINI_PLAYER;
                        break;
                    }
                    break;
                case 12:
                    switch (i) {
                        case 1:
                            str3 = EXTRA_SETTINGS_QUALITY;
                            break;
                        case 2:
                            str3 = EXTRA_SETTINGS_STEREO;
                            break;
                        case 3:
                            str3 = EXTRA_SETTINGS_ACCESS;
                            break;
                        case 4:
                            str3 = EXTRA_SETTINGS_CALL_REJ;
                            break;
                        case 5:
                            str3 = EXTRA_SETTINGS_ABOUT;
                            break;
                        case 6:
                            str3 = EXTRA_SETTINGS_PLAY_CONT;
                            break;
                    }
                case 13:
                    if (i != 1) {
                        if (i != 2) {
                            if (i == 4) {
                                str3 = EXTRA_MODE_MEMO;
                                break;
                            }
                        } else {
                            str3 = EXTRA_MODE_INTERVIEW;
                            break;
                        }
                    } else {
                        str3 = EXTRA_MODE_NORMAL;
                        break;
                    }
                    break;
                default:
                    if (i != 1) {
                        if (i != 2) {
                            if (i != 3) {
                                if (i == 4) {
                                    str3 = EXTRA_SURVEY_VOICEMEMO;
                                    break;
                                }
                            } else {
                                str3 = EXTRA_SURVEY_MEETING;
                                break;
                            }
                        } else {
                            str3 = EXTRA_SURVEY_INTERVIEW;
                            break;
                        }
                    } else {
                        str3 = EXTRA_SURVEY_NORMAL;
                        break;
                    }
                    break;
            }
            str3 = str2;
            Uri parse = Uri.parse("content://com.samsung.android.providers.context.log.use_app_feature_survey");
            try {
                ContentResolver contentResolver = mAppContext.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put("app_id", "com.sec.android.app.voicenote");
                contentValues.put("feature", str);
                if (str3 != null) {
                    contentValues.put("extra", str3);
                }
                contentResolver.insert(parse, contentValues);
                Log.m19d(TAG, "ContextProvider insertion operation is performed.");
            } catch (Exception e) {
                Log.m22e(TAG, "Error while using the ContextProvider : " + e);
            }
        }
    }

    public static void insertStatusLog(String str, String str2, int i) {
        if (VoiceNoteFeature.FLAG_IS_ENABLE_SURVEY_MODE && isLoggingEnabled()) {
            if (str == null) {
                Log.m32w(TAG, "ContextProvider cannot insert log data are invalidated");
                Log.m32w(TAG, "insertStatusLog - feature is NULL");
                return;
            }
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("app_id", "com.sec.android.app.voicenote");
                contentValues.put("feature", str);
                if (str2 != null) {
                    contentValues.put("extra", str2);
                }
                if (i != -1) {
                    contentValues.put("value", Integer.valueOf(i));
                }
                Intent intent = new Intent();
                intent.setAction("com.samsung.android.providers.context.log.action.REPORT_APP_STATUS_SURVEY");
                intent.putExtra("data", contentValues);
                intent.setPackage("com.samsung.android.providers.context");
                mAppContext.sendBroadcast(intent);
                Log.m19d(TAG, "ContextProvider insertion operation is performed.");
            } catch (Exception e) {
                Log.m22e(TAG, "Error while using the ContextProvider : " + e);
            }
        }
    }

    private static String getCurrentUtcTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        Date date = new Date();
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(date);
    }

    private static boolean isLoggingEnabled() {
        return getVersionOfContextProviders() > 1;
    }

    private static int getVersionOfContextProviders() {
        PackageInfo packageInfo;
        Context context = mAppContext;
        if (context == null) {
            return -1;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager == null || (packageInfo = packageManager.getPackageInfo("com.samsung.android.providers.context", 128)) == null) {
                return -1;
            }
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException unused) {
            Log.m19d(TAG, "Could not find ContextProvider");
            return -1;
        }
    }
}
