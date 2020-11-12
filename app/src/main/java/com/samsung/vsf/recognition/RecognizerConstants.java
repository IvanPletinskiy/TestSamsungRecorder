package com.samsung.vsf.recognition;

public class RecognizerConstants {
    public static String CERT_PATH = "/system/etc/security/cacerts/399e7759.0";
    public static boolean USE_TLS = true;
    public static boolean useJSpeexEncoder = true;

    public enum Client {
        UNKNOWN,
        VOICE_MEMO,
        GEAR,
        SIP,
        HALO,
        OTHER,
        SIP_IME
    }
}
