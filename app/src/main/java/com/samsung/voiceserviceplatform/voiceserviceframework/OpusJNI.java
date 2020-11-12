package com.samsung.voiceserviceplatform.voiceserviceframework;

public class OpusJNI {
    public native void encoder_destroy(long j);

    public native long encoder_init(int i, int i2, int i3, int i4, int i5);

    public native int encoder_process(short[] sArr, byte[] bArr, long j);

    static {
        try {
            System.loadLibrary("OPUS");
        } catch (Exception e) {
            System.out.println("Exception while loading library");
            e.printStackTrace();
        }
    }
}
