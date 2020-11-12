package com.sec.vsg.voiceframework;

import com.sec.vsg.voiceframework.process.ProcessLOGS;

public class SpeechKitWrapper {
    private static final String TAG = "SpeechKitWrapper";
    private static SpeechKit uniqueInstance;

    private SpeechKitWrapper() {
    }

    public static synchronized SpeechKit getInstance() {
        SpeechKit speechKit;
        synchronized (SpeechKitWrapper.class) {
            if (uniqueInstance == null) {
                if (SpeechKit.init() == 0) {
                    uniqueInstance = new SpeechKit();
                    String str = TAG;
                    ProcessLOGS.info(str, "VOICEACTIVITY LIBRARY VERSION : " + uniqueInstance.GetVersion());
                } else {
                    ProcessLOGS.warn(TAG, "getInstance() : No VoiceAcitivity Library is exist");
                }
            }
            speechKit = uniqueInstance;
        }
        return speechKit;
    }
}
