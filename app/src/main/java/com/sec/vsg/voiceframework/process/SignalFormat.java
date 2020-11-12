package com.sec.vsg.voiceframework.process;

public class SignalFormat {
    public static int ChannelConfig(int i) {
        if (i != 2) {
            if (i == 3 || i == 12) {
                return 2;
            }
            if (i != 16) {
                return i;
            }
        }
        return 1;
    }

    public static int GetSamplingMode(int i) {
        return i != 8000 ? 0 : 1000;
    }

    public static void parseStereoToMono(short[] sArr, short[] sArr2, int i, int i2) {
        for (int i3 = 0; i3 < i; i3++) {
            sArr2[i3] = sArr[(i3 << 1) + i2];
        }
    }
}
