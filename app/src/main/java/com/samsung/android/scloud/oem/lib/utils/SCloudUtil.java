package com.samsung.android.scloud.oem.lib.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SCloudUtil {
    public static String makeSHA1Hash(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest instance = MessageDigest.getInstance("SHA1");
        instance.reset();
        instance.update(str.getBytes("UTF-8"));
        byte[] digest = instance.digest();
        String str2 = "";
        for (int i = 0; i < digest.length; i++) {
            str2 = str2 + Integer.toString((digest[i] & 255) + 256, 16).substring(1);
        }
        return str2;
    }
}
