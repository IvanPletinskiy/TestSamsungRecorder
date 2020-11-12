package com.sec.android.app.voicenote.provider;

import android.content.Context;
import android.os.Environment;
import java.io.File;
import java.io.FilenameFilter;

public class SimpleStorageProvider {
    private static final long LOW_STORAGE_SAFE_THRESHOLD = 20971520;
    private static final long LOW_STORAGE_THRESHOLD = 21329920;
    private static final String TAG = "StorageProvider";
    private static final String TEMP_FOLDER_NAME = "/.393858";
    private static Context mAppContext;
    private static String mSDCardWritableDirPath;

    public static void setApplicationContext(Context context) {
        mAppContext = context;
    }

    public static void resetSDCardWritableDir() {
        StorageProvider.resetSDCardWritableDir();
    }

    private static String getWritableSDCardDirPath() {
        if (mSDCardWritableDirPath == null) {
            mSDCardWritableDirPath = StorageProvider.getSDCardWritableDirPath();
        }
        return mSDCardWritableDirPath;
    }

    public static String getRootPath(int i) {
        String str;
        if (i == 2) {
            i = Settings.getIntSettings(Settings.KEY_STORAGE, 0) == 1 ? 1 : 0;
        }
        if (i == 1) {
            str = getWritableSDCardDirPath();
            if (str == null) {
                str = Environment.getExternalStorageDirectory().getPath();
                Settings.setSettings(Settings.KEY_STORAGE, 0);
            } else if (!new File(str).exists()) {
                str = Environment.getExternalStorageDirectory().getPath();
                Settings.setSettings(Settings.KEY_STORAGE, 0);
            }
        } else {
            str = Environment.getExternalStorageDirectory().getPath();
        }
        File file = new File(str);
        if (!file.isDirectory() && !file.mkdirs()) {
            Log.m22e(TAG, "mkdirs failed");
        }
        return str;
    }

    public static String getVoiceRecorderPath() {
        return getVoiceRecorderPath(2);
    }

    public static String getVoiceRecorderPath(int i) {
        String str = getRootPath(i) + "/Voice Recorder";
        File file = new File(str);
        if (!file.isDirectory() && !file.mkdirs()) {
            Log.m22e(TAG, "mkdirs failed");
        }
        return str;
    }

    public static String getTempFilePath() {
        return getTempFilePath(2);
    }

    public static String getTempFilePath(int i) {
        String str = getRootPath(i) + TEMP_FOLDER_NAME;
        File file = new File(str);
        if (file.isDirectory()) {
            Log.m26i(TAG, "getTempFilePath - path : " + str + " is exist !!!");
        } else if (!file.mkdirs()) {
            Log.m22e(TAG, "getTempFilePath(): dir.mkdirs() failed");
        }
        return str;
    }

    public static boolean isTempFile(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        File file = new File(str);
        if (file.isDirectory() || !file.isHidden() || file.getPath().indexOf("393857") <= 0) {
            return false;
        }
        return true;
    }

    public static boolean isExistFile(String str) {
        if (str != null) {
            return isExistFile(new File(str));
        }
        Log.m22e(TAG, "isExistFile path is null");
        return false;
    }

    public static boolean isExistFile(final File file) {
        if (file == null) {
            Log.m22e(TAG, "isExistFile file is null");
            return false;
        }
        File parentFile = file.getParentFile();
        if (parentFile == null) {
            Log.m22e(TAG, "isExistFile parentDirectory is null");
            return false;
        }
        try {
            File[] listFiles = parentFile.listFiles(new FilenameFilter() {
                private final /* synthetic */ File f$0;

                {
                    this.f$0 = file;
                }

                public final boolean accept(File file, String str) {
                    return str.contentEquals(this.f$0.getName());
                }
            });
            if (listFiles == null || listFiles.length <= 0) {
                return false;
            }
            return true;
        } catch (NullPointerException e) {
            Log.m24e(TAG, "NullPointerException", (Throwable) e);
            return false;
        }
    }

    public static String createTempFile(String str) {
        return getTempFilePath() + "/." + System.currentTimeMillis() + str;
    }

    public static String createTempFile(String str, String str2) {
        String str3;
        if (str == null || str.isEmpty()) {
            return createTempFile(str2);
        }
        if (str.startsWith(getRootPath(0))) {
            str3 = getTempFilePath(0) + "/." + System.currentTimeMillis() + str2;
        } else {
            str3 = getTempFilePath(1) + "/." + System.currentTimeMillis() + str2;
        }
        Log.m29v(TAG, "createTempFile ref : " + str + " tempPath : " + str3);
        return str3;
    }
}
