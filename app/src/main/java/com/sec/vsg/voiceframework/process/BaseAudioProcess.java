package com.sec.vsg.voiceframework.process;

import com.sec.vsg.voiceframework.EndPointDetector;
import com.sec.vsg.voiceframework.NoiseChecker;
import com.sec.vsg.voiceframework.NoiseReduction;
import com.sec.vsg.voiceframework.SpeechKit;
import com.sec.vsg.voiceframework.SpeechKitWrapper;

public abstract class BaseAudioProcess {
    private static final String TAG = "BaseAudioProcess";
    protected SpeechKit VALib = null;

    /* renamed from: id */
    protected long f110id;
    protected int mChannelConfig;
    protected int mMode;
    private ObjectAudio mProc;
    protected int mSampleRate;

    /* access modifiers changed from: protected */
    public abstract int processUnit(short[] sArr, int i, short[] sArr2, int i2);

    public BaseAudioProcess(Enum enumR, int i, int i2) {
        ProcessLOGS.debug(TAG, "Voiceactivity Framework Version :: 170223");
        this.VALib = SpeechKitWrapper.getInstance();
        this.f110id = -1;
        this.mMode = getMode(enumR);
        String str = TAG;
        ProcessLOGS.debug(str, "mMode: " + this.mMode);
        this.mChannelConfig = SignalFormat.ChannelConfig(i);
        this.mSampleRate = i2;
        int i3 = this.mChannelConfig;
        if (i3 == 1) {
            this.mProc = new MonoObject();
        } else if (i3 != 2) {
            this.mProc = null;
        } else if ((this instanceof EndPointDetector) || (this instanceof NoiseChecker)) {
            this.mProc = new StereoObject(false);
        } else if (!(this instanceof NoiseReduction) || this.mMode != getMode(NoiseReduction.Mode.STEREOTOMONO)) {
            this.mProc = new StereoObject();
        } else {
            this.mProc = new StereoObject(false);
        }
    }

    /* access modifiers changed from: protected */
    public int getMode(Enum enumR) {
        return enumR.ordinal() + SignalFormat.GetSamplingMode(this.mSampleRate);
    }

    public int process(short[] sArr, int i) {
        ObjectAudio objectAudio = this.mProc;
        if (objectAudio != null) {
            return objectAudio.process(sArr, i, sArr, i);
        }
        return 0;
    }

    public int process(short[] sArr, int i, short[] sArr2, int i2) {
        ObjectAudio objectAudio = this.mProc;
        if (objectAudio != null) {
            return objectAudio.process(sArr, i, sArr2, i2);
        }
        return 0;
    }

    class MonoObject extends ObjectAudio {
        MonoObject() {
        }

        public int process(short[] sArr, int i, short[] sArr2, int i2) {
            return BaseAudioProcess.this.processUnit(sArr, i, sArr2, i2);
        }
    }

    class StereoObject extends ObjectAudio {
        boolean is2MicNS = true;

        StereoObject() {
        }

        StereoObject(boolean z) {
            this.is2MicNS = z;
        }

        public int process(short[] sArr, int i, short[] sArr2, int i2) {
            int i3 = i / 2;
            short[] sArr3 = new short[i3];
            if (this.is2MicNS) {
                return BaseAudioProcess.this.processUnit(sArr, i, sArr2, i2);
            }
            SignalFormat.parseStereoToMono(sArr, sArr3, i3, 0);
            return BaseAudioProcess.this.processUnit(sArr3, i3, sArr2, i3);
        }
    }
}
