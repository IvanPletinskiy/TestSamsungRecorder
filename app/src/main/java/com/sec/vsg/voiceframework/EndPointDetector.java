package com.sec.vsg.voiceframework;

import com.sec.vsg.voiceframework.process.BaseAudioProcess;
import com.sec.vsg.voiceframework.process.ProcessLOGS;

public class EndPointDetector extends BaseAudioProcess {
    private static final String TAG = "EndPointDetector";

    public enum Mode {
        DEFAULT,
        IOT,
        WEARABLE,
        MIRRORING
    }

    public EndPointDetector(Mode mode, int i, int i2) {
        super(mode, i, i2);
        if (this.VALib != null) {
            this.f110id = this.VALib.initializeEPD(getMode(mode), 0);
            String str = TAG;
            ProcessLOGS.info(str, "EPD initialized" + mode);
        }
    }

    public int process(short[] sArr, int i) {
        return super.process(sArr, i);
    }

    /* access modifiers changed from: protected */
    public int processUnit(short[] sArr, int i, short[] sArr2, int i2) {
        SpeechKit speechKit = this.VALib;
        if (speechKit != null) {
            long j = this.f110id;
            if (j != -1) {
                int processEPDFrame = speechKit.processEPDFrame(j, sArr, i);
                String str = TAG;
                ProcessLOGS.info(str, "EPD aar val:" + processEPDFrame);
                return processEPDFrame;
            }
        }
        return 0;
    }

    public void reset() {
        ProcessLOGS.info(TAG, "EPD param reset()");
        SpeechKit speechKit = this.VALib;
        if (speechKit != null) {
            long j = this.f110id;
            if (j != -1) {
                speechKit.resetEPDparams(j);
            }
        }
    }

    public void destroy() {
        ProcessLOGS.info(TAG, "EPD destroy()");
        long j = this.f110id;
        this.f110id = -1;
        SpeechKit speechKit = this.VALib;
        if (speechKit != null && j != -1) {
            speechKit.freeMemoryEPD(j);
        }
    }
}
