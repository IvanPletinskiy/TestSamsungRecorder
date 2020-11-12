package com.sec.android.app.voicenote.provider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import com.sec.android.app.voicenote.common.util.DeviceInfo;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

public class StorageProvider {
    private static final long LOW_STORAGE_SAFE_THRESHOLD = 20971520;
    private static final long LOW_STORAGE_THRESHOLD = 21329920;
    private static final String READ_ONLY_SD_CARD_PREFIX = "/storage";
    private static String SD_CARD_READONLY_DIR = null;
    private static final String TAG = "StorageProvider";
    private static final String TEMP_FOLDER_NAME = "/.393857";
    private static final String TEMP_WAVE = "/temp_wave";
    private static final String WRITABLE_SD_CARD_PREFIX = "/mnt/media_rw";
    private static Context mAppContext;
    private static String mSDCardWritableDirPath;

    public static void setApplicationContext(Context context) {
        mAppContext = context;
    }

    public static long getAvailableStorage(String str) {
        if (str == null) {
            str = getRootPath(2);
        }
        File file = new File(str);
        if (file.isFile() || !file.exists()) {
            str = file.getParent();
        }
        Log.m19d(TAG, "getAvailableStorage path : " + str);
        try {
            StatFs statFs = new StatFs(str);
            long availableBlocksLong = (statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong()) - LOW_STORAGE_THRESHOLD;
            Log.m29v(TAG, "getAvailableStorage - availableSize = " + availableBlocksLong);
            return availableBlocksLong;
        } catch (RuntimeException unused) {
            Log.m22e(TAG, "getAvailableStorage - exception. return 0");
            return 0;
        }
    }

    public static boolean alternativeStorageRecordEnable() {
        if (Settings.getIntSettings(Settings.KEY_STORAGE, 0) == 0) {
            return recordAvailableStorage(1);
        }
        return recordAvailableStorage(0);
    }

    private static boolean recordAvailableStorage(int i) {
        String str;
        if (i != 1) {
            str = Environment.getExternalStorageDirectory().getPath();
        } else {
            str = getSDCardWritableDirPath();
        }
        if (str == null) {
            return false;
        }
        if (!new File(str).isDirectory() || getAvailableStorage(str) <= 0) {
            return false;
        }
        return true;
    }

    public static void resetSDCardWritableDir() {
        mSDCardWritableDirPath = null;
        SD_CARD_READONLY_DIR = null;
    }

    public static String getSDCardWritableDirPath() {
        if (mSDCardWritableDirPath == null) {
            mSDCardWritableDirPath = getSDCardWritableDirPath((StorageManager) mAppContext.getSystemService(Settings.KEY_STORAGE));
        }
        return mSDCardWritableDirPath;
    }

    public static void initSDCardWritableDirPath(Context context) {
        if (mSDCardWritableDirPath == null) {
            mSDCardWritableDirPath = getSDCardWritableDirPath((StorageManager) context.getSystemService(Settings.KEY_STORAGE));
            Log.m19d(TAG, "initSDCardWritableDirPath = " + mSDCardWritableDirPath);
        }
    }

    public static String getExternalStorageStateSd() {
        Context context = mAppContext;
        String str = null;
        if (context == null) {
            Log.m26i(TAG, "getSDCardState - context is null");
            return null;
        }
        StorageManager storageManager = (StorageManager) context.getSystemService(Settings.KEY_STORAGE);
        String sDCardWritableDirPath = getSDCardWritableDirPath();
        if (sDCardWritableDirPath == null) {
            return null;
        }
        try {
            str = getVolumeState(storageManager, sDCardWritableDirPath);
            Log.m19d(TAG, "getSDCardState state = " + str);
            return str;
        } catch (IllegalArgumentException unused) {
            Log.m19d(TAG, "getSDCardState : " + sDCardWritableDirPath);
            return str;
        }
    }

    private static String getPersonalPageRoot() {
        try {
            return PrivateModeProvider.getPrivateStorageRoot(mAppContext);
        } catch (NoSuchMethodError unused) {
            return null;
        }
    }

    public static boolean isPersonalDirectory(Uri uri) {
        if (uri == null) {
            return false;
        }
        return ("file://" + getPersonalPageRoot()).equals(uri.toString());
    }

    public static String getRootPath(int i) {
        String str;
        if (i == 2) {
            i = Settings.getIntSettings(Settings.KEY_STORAGE, 0) == 1 ? 1 : 0;
        }
        if (i == 1) {
            str = getSDCardWritableDirPath();
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

    public static String getRestoreTempFilePath() {
        String str;
        Log.m19d(TAG, "getRestoreTempFilePath");
        if (Settings.getIntSettings(Settings.KEY_STORAGE, 0) == 1) {
            str = getSDCardWritableDirPath();
            if (str == null) {
                str = Environment.getExternalStorageDirectory().getPath();
                Settings.setSettings(Settings.KEY_STORAGE, 0);
            }
        } else {
            str = Environment.getExternalStorageDirectory().getPath();
        }
        String str2 = str + TEMP_FOLDER_NAME;
        Log.m19d(TAG, "getRestoreTempFilePath = " + str2);
        return str2;
    }

    public static String getTempFilePath() {
        return getTempFilePath(2);
    }

    public static String getTempWavePath() {
        return getTempFilePath(2) + TEMP_WAVE;
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

    public static void clearTempFiles() {
        Log.m26i(TAG, "clearTempFiles");
        deleteFiles(new File(getTempFilePath(0)));
        deleteFiles(new File(getTempFilePath(1)));
    }

    private static void deleteFiles(File file) {
        if (file.isDirectory() && file.exists()) {
            File[] listFiles = file.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file2 : listFiles) {
                    Log.m26i(TAG, "clearTempFiles - path : " + file2.getPath());
                    if (!file2.delete()) {
                        Log.m22e(TAG, "clearTempFiles() : failed to delete file");
                    }
                }
            }
            if (!file.delete()) {
                Log.m22e(TAG, "clearTempFiles() : failed to delete folder");
            }
        }
    }

    private static boolean isRestrictedByPolicy(Context context, String str, String str2) {
        Cursor query;
        Uri parse = Uri.parse(str);
        if (!(context == null || (query = context.getContentResolver().query(parse, new String[]{str2}, str2, new String[]{DeviceInfo.STR_TRUE}, (String) null)) == null)) {
            try {
                query.moveToFirst();
                if (query.getString(query.getColumnIndex(str2)).equals("false")) {
                    query.close();
                    return true;
                }
            } catch (Exception e) {
                Log.m22e(TAG, e.toString());
            } catch (Throwable th) {
                query.close();
                throw th;
            }
            query.close();
        }
        return false;
    }

    public static boolean isSdCardWriteRestricted(Context context) {
        return isRestrictedByPolicy(context, "content://com.sec.knox.provider/RestrictionPolicy4", "isSDCardWriteAllowed");
    }

    public static String getSDCardWritableDirPath(StorageManager storageManager) {
        Iterator<StorageVolume> it = storageManager.getStorageVolumes().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            StorageVolume next = it.next();
//            String semGetSubSystem = next.semGetSubSystem();
            String state = next.getState();
//            if (semGetSubSystem != null && semGetSubSystem.equals("sd") && "mounted".equals(state)) {
//                SD_CARD_READONLY_DIR = next.semGetPath();
//                break;
//            }
        }
        return convertToSDCardWritablePath(SD_CARD_READONLY_DIR);
    }

    public static String getVolumeState(StorageManager storageManager, String str) {
        String convertToSDCardReadOnlyPath = convertToSDCardReadOnlyPath(str);
        for (StorageVolume next : storageManager.getStorageVolumes()) {
//            if (next.semGetPath().equals(convertToSDCardReadOnlyPath)) {
//                return next.getState();
//            }
        }
        return null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:1:0x0002, code lost:
        r0 = SD_CARD_READONLY_DIR;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String convertToSDCardWritablePath(java.lang.String r2) {
        /*
            if (r2 == 0) goto L_0x0014
            java.lang.String r0 = SD_CARD_READONLY_DIR
            if (r0 == 0) goto L_0x0014
            boolean r0 = r2.startsWith(r0)
            if (r0 == 0) goto L_0x0014
            java.lang.String r0 = "/storage"
            java.lang.String r1 = "/mnt/media_rw"
            java.lang.String r2 = r2.replaceFirst(r0, r1)
        L_0x0014:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.provider.StorageProvider.convertToSDCardWritablePath(java.lang.String):java.lang.String");
    }

    public static String convertToSDCardReadOnlyPath(String str) {
        return (str == null || !str.startsWith(WRITABLE_SD_CARD_PREFIX)) ? str : str.replaceFirst(WRITABLE_SD_CARD_PREFIX, READ_ONLY_SD_CARD_PREFIX);
    }

    public static String getExternalSDStorageFsUuid(Context context) {
        Object invoke;
        StorageManager storageManager = (StorageManager) context.getSystemService(Settings.KEY_STORAGE);
        String str = null;
        if (storageManager != null) {
            try {
                for (Object next : (List) storageManager.getClass().getMethod("getVolumes", new Class[0]).invoke(storageManager, new Object[0])) {
                    Class<?> cls = next.getClass();
                    int intValue = ((Integer) cls.getMethod("getState", new Class[0]).invoke(next, new Object[0])).intValue();
                    if ("mounted".equals((String) cls.getMethod("getEnvironmentForState", new Class[]{Integer.TYPE}).invoke(next, new Object[]{Integer.valueOf(intValue)})) && (invoke = cls.getMethod("getDisk", new Class[0]).invoke(next, new Object[0])) != null && ((Boolean) invoke.getClass().getMethod("isSd", new Class[0]).invoke(invoke, new Object[0])).booleanValue()) {
                        str = (String) cls.getMethod("getFsUuid", new Class[0]).invoke(next, new Object[0]);
                    }
                }
            } catch (NoSuchMethodException unused) {
                Log.m22e(TAG, "NoSuchMethodException");
            } catch (IllegalAccessException unused2) {
                Log.m22e(TAG, "IllegalAccessException");
            } catch (InvocationTargetException unused3) {
                Log.m22e(TAG, "InvocationTargetException");
            }
        }
        return str;
    }
}
