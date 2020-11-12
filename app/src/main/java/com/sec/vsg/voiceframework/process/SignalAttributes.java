package com.sec.vsg.voiceframework.process;

import com.sec.vsg.voiceframework.SpeechKit;
import com.sec.vsg.voiceframework.SpeechKitWrapper;

public class SignalAttributes {
    public static int computeEnergy(short[] sArr, int i, int i2, int i3) {
        int ChannelConfig = SignalFormat.ChannelConfig(i3);
        if (ChannelConfig == 1) {
            return computeEnergyforMono(sArr, i, i2);
        }
        if (ChannelConfig != 2) {
            return 0;
        }
        return computeEnergyforStereo(sArr, i, i2);
    }

    private static int computeEnergyforMono(short[] sArr, int i, int i2) {
        SpeechKit instance = SpeechKitWrapper.getInstance();
        if (instance != null) {
            return instance.computeEnergyFrame(sArr, i, i2);
        }
        return 0;
    }

    private static int computeEnergyforStereo(short[] sArr, int i, int i2) {
        int i3 = i / 2;
        short[] sArr2 = new short[i3];
        SignalFormat.parseStereoToMono(sArr, sArr2, i3, 0);
        return computeEnergyforMono(sArr2, i3, i2);
    }
}
