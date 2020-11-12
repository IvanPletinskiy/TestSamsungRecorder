package com.sec.svoice.api;

import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

public class SVoice {
    public static final int CALLTYPE_ONEWAY = 0;
    public static final int CALLTYPE_TWOWAY = 1;
    public static final int SP_ASR = 1;
    public static final int SP_NLU = 2;
    public static final int SP_TTS = 8;
    static final int[] seed = {211, 90, 239, 16, 181, 197, 183, 12, 248, 194, 49, 10, 183, 238, 88, 40, 69, 153, 214, 96};
    private String deviceId;
    private String host;
    private long interface_handle = -1;
    private int port;
    private SVoiceSentinel sentinel;
    private long sentinel_handle = -1;

    private native long createhandle(String str, int i, boolean z, String str2, boolean z2);

    private native boolean deletehandle(long j);

    private native long registersentinel(long j, SVoiceSentinel sVoiceSentinel);

    public native void PrintTickCountDebug(String str);

    public native void cancel();

    public native void close();

    public native void connect() throws SVoiceTcpConnectionException, SVoiceDNSLookupException, SVoiceRPCTimeoutException, SVoiceRuntimeException, SVoiceIncompatibilityException;

    public native void enable_log(int i);

    public native Properties getTtsCapacity(String str) throws SVoiceTcpConnectionException, SVoiceDNSLookupException, SVoiceRPCTimeoutException, SVoiceRuntimeException;

    public native int get_firstport();

    public native int get_secondport();

    public native boolean isConnected();

    public native void keepalive();

    public native void nlgRequest(Properties properties, Properties properties2, int i) throws SVoiceFatalException, SVoiceResetException, SVoiceRuntimeException, SVoiceTcpConnectionException, SVoiceRPCTimeoutException;

    public native void nluPrepare(Properties properties) throws SVoiceFatalException, SVoiceResetException, SVoiceTcpConnectionException;

    public native Properties open(Properties properties, String str, int i) throws SVoiceTcpConnectionException, SVoiceDNSLookupException, SVoiceRPCTimeoutException, SVoiceRuntimeException, SVoiceIncompatibilityException;

    public native Properties openParam(Properties properties) throws SVoiceTcpConnectionException, SVoiceDNSLookupException, SVoiceRPCTimeoutException, SVoiceRuntimeException, SVoiceIncompatibilityException;

    public native Properties pds_close(String str, Properties properties) throws SVoiceTcpConnectionException, SVoiceDNSLookupException, SVoiceRPCTimeoutException, SVoiceRuntimeException, SVoicePDSNoDatabaseException, SVoicePDSResetException, SVoiceSessionException;

    public native Properties pds_createpam(String str, Properties properties) throws SVoiceTcpConnectionException, SVoiceDNSLookupException, SVoiceRPCTimeoutException, SVoiceRuntimeException, SVoicePDSNoDatabaseException, SVoicePDSResetException;

    public native Properties pds_open(String str, Properties properties, boolean z) throws SVoiceTcpConnectionException, SVoiceDNSLookupException, SVoiceRPCTimeoutException, SVoiceRuntimeException, SVoicePDSNoDatabaseException, SVoicePDSResetException, SVoiceSessionException;

    public native Properties pds_wipe(String str, Properties properties) throws SVoiceRuntimeException, SVoicePDSResetException;

    public native void preProcess(Properties properties) throws SVoiceFatalException, SVoiceResetException, SVoiceRuntimeException, SVoiceTcpConnectionException;

    public native void prepare(int i, Properties properties, Properties properties2, Properties properties3, int i2) throws SVoiceFatalException, SVoiceResetException, SVoiceTcpConnectionException;

    public native void prepare2(int i, Properties properties, Properties properties2, Properties properties3, int i2) throws SVoiceFatalException, SVoiceResetException, SVoiceTcpConnectionException;

    public native void process(Properties properties) throws SVoiceFatalException, SVoiceResetException, SVoiceRuntimeException, SVoiceTcpConnectionException;

    public native void rewind() throws SVoiceFatalException, SVoiceTcpConnectionException;

    public native void send(byte[] bArr) throws SVoiceFatalException, SVoiceResetException;

    public native Properties smg_register(Properties properties) throws SVoiceFatalException, SVoiceResetException, SVoiceTcpConnectionException, SVoiceRPCTimeoutException;

    public native void time_out(int i);

    static {
        Log.i("svoiceapi_jar", "RELEASE_DATE 2018 Dec 18");
        Log.i("svoiceapi_jar", "RELEASE_VER 1.24_OpenSSLUpdate_1.0.2q");
        try {
            Log.i("svoiceapi_jar", "Loading svoice dll");
            System.loadLibrary("svoicedll");
            Log.i("svoiceapi_jar", "Loading success");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    private static String genDUID(String str) {
        String str2 = "";
        int i = 0;
        while (true) {
            int[] iArr = seed;
            if (i >= iArr.length) {
                try {
                    return SHA256(String.valueOf(str2) + str);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return null;
                } catch (UnsupportedEncodingException e2) {
                    e2.printStackTrace();
                    return null;
                }
            } else {
                String hexString = Integer.toHexString(iArr[i]);
                if (hexString.length() == 1) {
                    str2 = String.valueOf(str2) + "0";
                }
                str2 = String.valueOf(str2) + hexString;
                i++;
            }
        }
    }

    private static String convertToHex(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bArr) {
            byte b2 = (byte) ((b >>> 4) & 15);
            int i = 0;
            while (true) {
                sb.append((char) ((b2 < 0 || b2 > 9) ? (b2 - 10) + 97 : b2 + 48));
                b2 = (byte) (b & 15);
                int i2 = i + 1;
                if (i >= 1) {
                    break;
                }
                i = i2;
            }
        }
        return sb.toString();
    }

    private static String SHA256(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest instance = MessageDigest.getInstance("SHA-256");
        instance.update(str.getBytes("iso-8859-1"), 0, str.length());
        return convertToHex(instance.digest());
    }

    public SVoice(String str, int i, String str2, SVoiceSentinel sVoiceSentinel) {
        this.host = str;
        this.port = i;
        if (str2.startsWith("t")) {
            String[] split = str2.split("-");
            String str3 = "";
            for (int i2 = 0; i2 < split.length; i2++) {
                str3 = String.valueOf(str3) + split[i2];
            }
            this.deviceId = str3;
        } else {
            this.deviceId = genDUID(str2);
        }
        this.sentinel = sVoiceSentinel;
        this.interface_handle = createhandle(this.host, this.port, false, "", false);
        this.sentinel_handle = registersentinel(this.interface_handle, sVoiceSentinel);
    }

    public SVoice(String str, int i, String str2, SVoiceSentinel sVoiceSentinel, boolean z, String str3) {
        this.host = str;
        this.port = i;
        if (str2.startsWith("t")) {
            String[] split = str2.split("-");
            String str4 = "";
            for (int i2 = 0; i2 < split.length; i2++) {
                str4 = String.valueOf(str4) + split[i2];
            }
            this.deviceId = str4;
        } else {
            this.deviceId = genDUID(str2);
        }
        this.sentinel = sVoiceSentinel;
        this.interface_handle = createhandle(this.host, this.port, z, str3, true);
        this.sentinel_handle = registersentinel(this.interface_handle, sVoiceSentinel);
    }

    public SVoice(String str, int i, String str2, SVoiceSentinel sVoiceSentinel, boolean z, String str3, boolean z2) {
        this.host = str;
        this.port = i;
        if (str2.startsWith("t")) {
            String[] split = str2.split("-");
            String str4 = "";
            for (int i2 = 0; i2 < split.length; i2++) {
                str4 = String.valueOf(str4) + split[i2];
            }
            this.deviceId = str4;
        } else {
            this.deviceId = genDUID(str2);
        }
        this.sentinel = sVoiceSentinel;
        this.interface_handle = createhandle(this.host, this.port, z, str3, z2);
        this.sentinel_handle = registersentinel(this.interface_handle, sVoiceSentinel);
    }

    public String GetDeviceID() {
        return this.deviceId;
    }

    public boolean destroy() {
        return deletehandle(this.interface_handle);
    }

    public Properties open(Properties properties) throws SVoiceTcpConnectionException, SVoiceDNSLookupException, SVoiceRPCTimeoutException, SVoiceRuntimeException, SVoiceIncompatibilityException {
        new Properties();
        return open(properties, "none", 1);
    }

    public Properties open(Properties properties, String str) throws SVoiceTcpConnectionException, SVoiceDNSLookupException, SVoiceRPCTimeoutException, SVoiceRuntimeException, SVoiceIncompatibilityException {
        return open(properties, str, 1);
    }

    public void prepare(int i, Properties properties, Properties properties2, Properties properties3) throws SVoiceFatalException, SVoiceResetException, SVoiceTcpConnectionException {
        prepare(i, properties, properties2, properties3, 1);
    }

    public void prepare(int i, Properties properties, Properties properties2) throws SVoiceFatalException, SVoiceResetException, SVoiceTcpConnectionException {
        prepare(i, properties, properties2, new Properties(), 1);
    }

    public void prepare2(int i, Properties properties, Properties properties2, Properties properties3) throws SVoiceFatalException, SVoiceResetException, SVoiceTcpConnectionException {
        Log.i("svoiceapi_jar", "Version 1.24_OpenSSLUpdate_1.0.2q");
        prepare2(i, properties, properties2, properties3, 0);
    }

    public void prepare2(int i, Properties properties, Properties properties2) throws SVoiceFatalException, SVoiceResetException, SVoiceTcpConnectionException {
        Properties properties3 = new Properties();
        Log.i("svoiceapi_jar", "Version 1.24_OpenSSLUpdate_1.0.2q");
        prepare2(i, properties, properties2, properties3, 0);
    }

    public void nlgRequest(Properties properties) throws SVoiceFatalException, SVoiceResetException, SVoiceRuntimeException, SVoiceTcpConnectionException, SVoiceRPCTimeoutException {
        nlgRequest(properties, new Properties(), 0);
    }

    public void nlgRequest(Properties properties, Properties properties2) throws SVoiceFatalException, SVoiceResetException, SVoiceRuntimeException, SVoiceTcpConnectionException, SVoiceRPCTimeoutException {
        nlgRequest(properties, properties2, 0);
    }
}
