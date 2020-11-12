package com.sec.android.app.voicenote.provider;

import android.content.Context;
import android.os.StatFs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PrivateModeProvider {
    public static final int CANCELLED = 3;
    private static final int COPY_BUFFER_SIZE = 8192;
    public static final int MOUNTED = 1;
    public static final int PREPARED = 0;
    private static final String TAG = "PrivateModeProvider";
    private static boolean mIsPrivateBoxMode = false;

    public static synchronized boolean isPrivateModeReady(Context context) {
        boolean z;
        synchronized (PrivateModeProvider.class) {
            z = false;
            try {
//                z = SemPrivateModeManager.isPrivateModeReady(context);
            } catch (NoSuchMethodError e) {
                Log.m24e(TAG, "NoSuchMethodError", (Throwable) e);
            } catch (NoClassDefFoundError e2) {
                Log.m24e(TAG, "NoClassDefFoundError", (Throwable) e2);
            } catch (Exception e3) {
                Log.m24e(TAG, "Exception", (Throwable) e3);
            }
        }
        return z;
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized boolean isPrivateMode() {
        /*
            java.lang.Class<com.sec.android.app.voicenote.provider.PrivateModeProvider> r0 = com.sec.android.app.voicenote.provider.PrivateModeProvider.class
            monitor-enter(r0)
            r1 = 0
            int r2 = com.samsung.android.privatemode.SemPrivateModeManager.getState()     // Catch:{ NoSuchMethodError -> 0x000f, all -> 0x000c }
            if (r2 <= 0) goto L_0x000f
            r1 = 1
            goto L_0x000f
        L_0x000c:
            r1 = move-exception
            monitor-exit(r0)
            throw r1
        L_0x000f:
            monitor-exit(r0)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.provider.PrivateModeProvider.isPrivateMode():boolean");
    }

    public static synchronized boolean isPrivateBoxMode() {
        boolean z;
        synchronized (PrivateModeProvider.class) {
            z = mIsPrivateBoxMode;
        }
        return z;
    }

    public static synchronized void setPrivateBoxMode(boolean z) {
        synchronized (PrivateModeProvider.class) {
            mIsPrivateBoxMode = z;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x000b, code lost:
        r1 = false;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized boolean isPrivateStorageMounted(android.content.Context r1) {
        /*
            java.lang.Class<com.sec.android.app.voicenote.provider.PrivateModeProvider> r0 = com.sec.android.app.voicenote.provider.PrivateModeProvider.class
            monitor-enter(r0)
            boolean r1 = com.samsung.android.privatemode.SemPrivateModeManager.isPrivateStorageMounted(r1)     // Catch:{ NoSuchMethodError -> 0x000b, all -> 0x0008 }
            goto L_0x000c
        L_0x0008:
            r1 = move-exception
            monitor-exit(r0)
            throw r1
        L_0x000b:
            r1 = 0
        L_0x000c:
            monitor-exit(r0)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.provider.PrivateModeProvider.isPrivateStorageMounted(android.content.Context):boolean");
    }

    public static boolean isPrivateStorageContent(Context context, long j) {
        return isPrivateStorageContent(context, CursorProvider.getInstance().getPath(j));
    }

    public static boolean isPrivateStorageContent(Context context, String str) {
        return str != null && str.contains(getPrivateStorageRoot(context));
    }

    public static String getPrivateStorageRoot(Context context) {
//        try {
//            return SemPrivateModeManager.getPrivateStoragePath(context);
//        } catch (NoSuchMethodError unused) {
//            return null;
//        }
        return "Blabla stub PrivateModeProvider";
    }

    private static long getAvailableSpace(String str) {
        try {
            StatFs statFs = new StatFs(str);
            return statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
        } catch (RuntimeException unused) {
            Log.m22e(TAG, "getAvailableStorage - exception. return 0");
            return 0;
        }
    }

    public static boolean rename(Context context, String str, String str2) {
        boolean startsWith = str.startsWith(getPrivateStorageRoot(context));
        boolean startsWith2 = str2.startsWith(getPrivateStorageRoot(context));
        File file = new File(str);
        File file2 = new File(str2);
        if (startsWith == startsWith2) {
            return file.renameTo(file2);
        }
        String substring = str2.substring(0, str2.lastIndexOf(47));
        if (getAvailableSpace(substring) == 0) {
            Log.m29v(TAG, "AvailableSpace is zero");
            return false;
        } else if (file.length() > getAvailableSpace(substring)) {
            Log.m22e(TAG, "There is no available space");
            return false;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file2);
                    try {
                        byte[] bArr = new byte[8192];
                        while (true) {
                            int read = fileInputStream.read(bArr, 0, 8192);
                            if (read == -1) {
                                break;
                            }
                            fileOutputStream.write(bArr, 0, read);
                        }
                        fileOutputStream.flush();
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {
                            Log.m22e(TAG, "IOException " + e);
                        }
                        try {
                            fileOutputStream.close();
                        } catch (IOException e2) {
                            Log.m22e(TAG, "IOException " + e2);
                        }
                        if (file2.exists() && file.length() != file2.length()) {
                            if (!file2.delete()) {
                                Log.m22e(TAG, "dstFile.delete() failed");
                            }
                            Log.m19d(TAG, "srcFile and dstFile size are not same");
                        }
                        if (!file.exists() || file.length() != file2.length()) {
                            return false;
                        }
                    } catch (IOException e3) {
                        if (file2.exists() && !file2.delete()) {
                            Log.m22e(TAG, "dstFile.delete() failed");
                        }
                        Log.m22e(TAG, "IOException " + e3);
                        try {
                            fileInputStream.close();
                        } catch (IOException e4) {
                            Log.m22e(TAG, "IOException " + e4);
                        }
                        try {
                            fileOutputStream.close();
                        } catch (IOException e5) {
                            Log.m22e(TAG, "IOException " + e5);
                        }
                        if (file2.exists() && file.length() != file2.length()) {
                            if (!file2.delete()) {
                                Log.m22e(TAG, "dstFile.delete() failed");
                            }
                            Log.m19d(TAG, "srcFile and dstFile size are not same");
                        }
                        if (!file.exists() || file.length() != file2.length()) {
                            return false;
                        }
                    } catch (Exception e6) {
                        Log.m22e(TAG, "Exception " + e6);
                        try {
                            fileInputStream.close();
                        } catch (IOException e7) {
                            Log.m22e(TAG, "IOException " + e7);
                        }
                        try {
                            fileOutputStream.close();
                        } catch (IOException e8) {
                            Log.m22e(TAG, "IOException " + e8);
                        }
                        if (file2.exists() && file.length() != file2.length()) {
                            if (!file2.delete()) {
                                Log.m22e(TAG, "dstFile.delete() failed");
                            }
                            Log.m19d(TAG, "srcFile and dstFile size are not same");
                        }
                        if (!file.exists() || file.length() != file2.length()) {
                            return false;
                        }
                    } catch (Throwable th) {
                        try {
                            fileInputStream.close();
                        } catch (IOException e9) {
                            Log.m22e(TAG, "IOException " + e9);
                        }
                        try {
                            fileOutputStream.close();
                        } catch (IOException e10) {
                            Log.m22e(TAG, "IOException " + e10);
                        }
                        if (file2.exists() && file.length() != file2.length()) {
                            if (!file2.delete()) {
                                Log.m22e(TAG, "dstFile.delete() failed");
                            }
                            Log.m19d(TAG, "srcFile and dstFile size are not same");
                        }
                        if (file.exists() && file.length() == file2.length()) {
                            file.delete();
                        }
                        throw th;
                    }
                    return file.delete();
                } catch (FileNotFoundException e11) {
                    Log.m22e(TAG, "Can not found destination file : " + e11);
                    try {
                        fileInputStream.close();
                    } catch (IOException e12) {
                        Log.m24e(TAG, "IOException", (Throwable) e12);
                    }
                    return false;
                }
            } catch (FileNotFoundException e13) {
                Log.m22e(TAG, "Can not found source file : " + e13);
                return false;
            }
        }
    }
}
