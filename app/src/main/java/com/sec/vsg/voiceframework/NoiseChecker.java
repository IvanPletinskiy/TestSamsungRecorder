package com.sec.vsg.voiceframework;

import com.sec.vsg.voiceframework.process.BaseAudioProcess;

public class NoiseChecker extends BaseAudioProcess {
    public NoiseChecker(Enum enumR, int i, int i2) {
        super(enumR, i, i2);
    }

    @Override
    public int processUnit(short[] sArr, int i, short[] sArr2, int i2) {
        return 0;
    }
}
