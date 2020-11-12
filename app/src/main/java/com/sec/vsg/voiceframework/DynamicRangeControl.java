package com.sec.vsg.voiceframework;

import com.sec.vsg.voiceframework.process.BaseAudioProcess;
import com.sec.vsg.voiceframework.process.ProcessLOGS;

public class DynamicRangeControl extends BaseAudioProcess {
    static final String TAG = "DynamicRangeControl";
    private boolean isDRCon = true;
    private boolean isFirstFrame = true;

    public enum Mode {
        DEFAULT
    }

    public DynamicRangeControl(Mode mode, int i, int i2) {
        super(mode, i, i2);
        ProcessLOGS.info(TAG, "DRC initialize()");
        SpeechKit speechKit = this.VALib;
        if (speechKit != null) {
            this.f110id = speechKit.initializeDRC(this.mSampleRate, 0);
        }
    }

    public int process(short[] sArr, int i) {
        return super.process(sArr, i);
    }

    /* access modifiers changed from: protected */
    public int processUnit(short[] sArr, int i, short[] sArr2, int i2) {
        return this.VALib.processDRC(this.f110id, sArr, i);
    }

    public void destroy() {
        ProcessLOGS.info(TAG, "DRC destroy()");
        long j = this.f110id;
        this.f110id = -1;
        SpeechKit speechKit = this.VALib;
        if (speechKit != null && j != -1) {
            speechKit.freeMemoryDRC(j);
        }
    }
}
