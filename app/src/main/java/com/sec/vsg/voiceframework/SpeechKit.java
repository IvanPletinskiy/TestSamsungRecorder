package com.sec.vsg.voiceframework;

import com.sec.vsg.voiceframework.process.ProcessLOGS;

public class SpeechKit {
    private static final String TAG = "SpeechKit";

    public native String GetVersion();

    public native int computeEnergyFrame(short[] sArr, int i, int i2);

    public native int freeMemoryDRC(long j);

    public native int freeMemoryDoNS(long j);

    public native int freeMemoryEPD(long j);

    public native long initializeDRC(int i, int i2);

    public native long initializeDoNS(int i, int i2, int i3);

    public native long initializeEPD(int i, int i2);

    public native int processDRC(long j, short[] sArr, int i);

    public native int processDoNSFrame(long j, short[] sArr, int i, short[] sArr2, int i2);

    public native int processEPDFrame(long j, short[] sArr, int i);

    public native int resetEPDparams(long j);

    public static int init() {
        try {
            ProcessLOGS.info(TAG, "Trying to load VoiceActivity.so");
            System.loadLibrary("VoiceActivity");
            ProcessLOGS.info(TAG, "Loading  VoiceActivity.so done");
            return 0;
        } catch (UnsatisfiedLinkError e) {
            String str = TAG;
            ProcessLOGS.error(str, "WARNING: " + e.toString());
            return -2;
        } catch (Exception e2) {
            String str2 = TAG;
            ProcessLOGS.error(str2, "WARNING: " + e2.toString());
            return -2;
        }
    }
}
