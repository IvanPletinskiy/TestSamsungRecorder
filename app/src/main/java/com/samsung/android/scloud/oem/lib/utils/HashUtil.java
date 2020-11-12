package com.samsung.android.scloud.oem.lib.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String encodeHexString(byte[] bArr) {
        return new String(encodeHex(bArr));
    }

    public static char[] encodeHex(byte[] bArr) {
        return encodeHex(bArr, true);
    }

    public static char[] encodeHex(byte[] bArr, boolean z) {
        return encodeHex(bArr, z ? DIGITS_LOWER : DIGITS_UPPER);
    }

    protected static char[] encodeHex(byte[] bArr, char[] cArr) {
        int length = bArr.length;
        char[] cArr2 = new char[(length << 1)];
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            int i3 = i + 1;
            cArr2[i] = cArr[(bArr[i2] & 240) >>> 4];
            i = i3 + 1;
            cArr2[i3] = cArr[bArr[i2] & 15];
        }
        return cArr2;
    }

    public static MessageDigest getDigest(String str) {
        try {
            return MessageDigest.getInstance(str);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String getFileSHA256(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            MessageDigest digest = getDigest("SHA-256");
            byte[] bArr = new byte[1024];
            while (true) {
                try {
                    int read = inputStream.read(bArr, 0, 1024);
                    if (read <= 0) {
                        break;
                    }
                    digest.update(bArr, 0, read);
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return encodeHexString(digest.digest());
        }
        throw new FileNotFoundException("InputStream param is null");
    }

    public static String getFileSHA256(File file) throws IOException {
        if (file != null) {
            return getFileSHA256((InputStream) new FileInputStream(file));
        }
        throw new FileNotFoundException("File Object param is null");
    }

    public static String getMD5HashString(InputStream inputStream) {
        int i;
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            byte[] bArr = new byte[1024];
            while (true) {
                int read = inputStream.read(bArr, 0, 1024);
                if (read <= 0) {
                    break;
                }
                instance.update(bArr, 0, read);
            }
            byte[] digest = instance.digest();
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : digest) {
                stringBuffer.append(Integer.toString((b & 255) + 256, 16).substring(1));
            }
            String stringBuffer2 = stringBuffer.toString();
            try {
                inputStream.close();
                return stringBuffer2;
            } catch (IOException e) {
                e.printStackTrace();
                return stringBuffer2;
            }
        } catch (IOException | NoSuchAlgorithmException e2) {
            e2.printStackTrace();
            try {
                inputStream.close();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
            return "";
        } catch (Throwable th) {
            try {
                inputStream.close();
            } catch (IOException e4) {
                e4.printStackTrace();
            }
            throw th;
        }
    }
}
