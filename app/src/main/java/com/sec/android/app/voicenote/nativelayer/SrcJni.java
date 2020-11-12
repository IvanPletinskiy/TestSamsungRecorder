package com.sec.android.app.voicenote.nativelayer;

public class SrcJni {
    public static final int SOUNDALIVE_BITWIDTH_16 = 0;
    public static final int SOUNDALIVE_BITWIDTH_24 = 1;
    public static final int Soundalive_SRC_16000k = 3;
    public static final int Soundalive_SRC_24000k = 5;
    public static final int Soundalive_SRC_32000k = 6;
    public static final int Soundalive_SRC_44100k = 7;
    public static final int Soundalive_SRC_48000k = 8;
    public static final int Soundalive_SRC_8000k = 0;

    public native void create();

    public native void destroy();

    public native int exe(short[] sArr, short[] sArr2, int i);

    public native int init(int i, int i2, int i3, int i4);

    static {
        System.loadLibrary("ResampleWrapper");
        System.loadLibrary("_SoundAlive_SRC192_ver205");
    }
}
