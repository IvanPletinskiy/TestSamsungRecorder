package com.samsung.vsf.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PLMUtils {
    public static final Uri BOOKMARKS_CONTENT_URI = Uri.parse("content://com.sec.android.app.sbrowser.browser/bookmarks");
    public static final Uri MYPLACE_CONTENT_URI_K = Uri.parse("content://com.samsung.android.internal.intelligence.useranalysis/place");
    private static char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String getDeviceId(Context context) {
        String str = "";
        try {
            Cursor query = context.getContentResolver().query(Uri.parse("content://com.samsung.svoice.sync/device_id"), (String[]) null, (String) null, (String[]) null, (String) null);
            if (query != null && query.moveToFirst()) {
                str = query.getString(0);
            }
            if (query != null) {
                query.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str.isEmpty() ? generateUniqueDID(context) : str;
    }

    private static String generateUniqueDID(Context context) {
        StringBuilder sb = new StringBuilder();
        String string = Settings.Secure.getString(context.getContentResolver(), "android_id");
        if (string == null || string.isEmpty()) {
            string = Build.SERIAL;
        }
        if (string == null || string.isEmpty()) {
            SVoiceLog.debug("PLMUtils", "PLM :: This device id has no android id or serial");
            sb.append(System.currentTimeMillis());
        } else {
            int length = string.length();
            int i = length / 2;
            sb.append(string.substring(0, i));
            sb.append(Build.MODEL.hashCode());
            sb.append(string.substring(i, length));
        }
        try {
            return new String(encodeHex(MessageDigest.getInstance("SHA-256").digest(sb.toString().getBytes("UTF-8"))));
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return sb.toString();
        }
    }

    private static char[] encodeHex(byte[] bArr) {
        int length = bArr.length;
        char[] cArr = new char[(length << 1)];
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            int i3 = i + 1;
            char[] cArr2 = digits;
            cArr[i] = cArr2[(bArr[i2] & 240) >>> 4];
            i = i3 + 1;
            cArr[i3] = cArr2[bArr[i2] & 15];
        }
        return cArr;
    }
}
