package com.sec.vsg.voiceframework;

import com.sec.vsg.voiceframework.process.BaseAudioProcess;
import com.sec.vsg.voiceframework.process.ProcessLOGS;

public class NoiseReduction extends BaseAudioProcess {
    public static final String TAG = "NoiseReduction";

    public enum Mode {
        DEFAULT,
        LESS,
        FOREPD,
        STEREOTOMONO
    }

    public NoiseReduction(Mode mode, int i, int i2) {
        super(mode, i, i2);
        if (this.VALib != null) {
            this.f110id = this.VALib.initializeDoNS(getMode(mode), this.mChannelConfig, 0);
            ProcessLOGS.info(TAG, "NS initialized");
        }
    }

    public int process(short[] sArr, int i) {
        return super.process(sArr, i, sArr, i);
    }

    /* access modifiers changed from: protected */
    public int processUnit(short[] sArr, int i, short[] sArr2, int i2) {
        SpeechKit speechKit = this.VALib;
        if (speechKit != null) {
            long j = this.f110id;
            if (j != -1) {
                return speechKit.processDoNSFrame(j, sArr, i, sArr2, i2);
            }
        }
        return 0;
    }

    public void destroy() {
        ProcessLOGS.info(TAG, "NS destroy()");
        long j = this.f110id;
        this.f110id = -1;
        SpeechKit speechKit = this.VALib;
        if (speechKit != null && j != -1) {
            speechKit.freeMemoryDoNS(j);
        }
    }
}
