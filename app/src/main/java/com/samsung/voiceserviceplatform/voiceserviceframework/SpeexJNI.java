package com.samsung.voiceserviceplatform.voiceserviceframework;

public class SpeexJNI {
    public static final int SPEEX_GET_FRAME_SIZE = 3;
    public static final int SPEEX_SET_QUALITY = 4;
    public static final int SPEEX_SET_VBR = 12;
    public static final int SPEEX_SET_VBR_QUALITY = 14;
    private static boolean jniLibraryLoaded = true;

    public static native int startEncode(byte[] bArr);

    public native void speex_bits_destroy(SpeexBits speexBits);

    public native void speex_bits_init(SpeexBits speexBits);

    public native void speex_bits_reset(SpeexBits speexBits);

    public native int speex_bits_write(SpeexBits speexBits, byte[] bArr, int i);

    public native int speex_encode_int(long j, short[] sArr, SpeexBits speexBits);

    public native int speex_encoder_ctl(long j, int i, Long l);

    public native int speex_encoder_ctl_get_vbr(long j, int i, Long l);

    public native void speex_encoder_destroy(long j);

    public native long speex_encoder_init(int i);

    static {
        if (!jniLibraryLoaded) {
            try {
                System.loadLibrary("speex-jni");
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    public final boolean isSpeexLoaded() {
        return jniLibraryLoaded;
    }
}
