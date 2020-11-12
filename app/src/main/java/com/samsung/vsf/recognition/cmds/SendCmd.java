package com.samsung.vsf.recognition.cmds;

import com.samsung.vsf.recognition.Cmd;
import com.samsung.vsf.recognition.Recognizer;

public class SendCmd extends Cmd {
    private int duration;
    private boolean isBufferBeforeEPD = false;
    private byte[] mBuffer;
    private SpeechDetectionResult mEPDResult = SpeechDetectionResult.NONE;
    private int mRMSValue;
    private int seqNumber = 0;

    public enum SpeechDetectionResult {
        NONE,
        SPEECH_START,
        SPEECH,
        SPEECH_END
    }

    public SendCmd(int i) {
        this.seqNumber = i;
    }

    public void execute(Recognizer.CmdHandler cmdHandler) {
        cmdHandler.send(this);
    }

    public void setSpeechDetectionResult(SpeechDetectionResult speechDetectionResult) {
        this.mEPDResult = speechDetectionResult;
    }

    public SpeechDetectionResult getSpeechDetectionResult() {
        return this.mEPDResult;
    }

    public void setRMSValue(int i) {
        this.mRMSValue = i;
    }

    public void setBuffer(byte[] bArr) {
        this.mBuffer = bArr;
    }

    public byte[] getAudioBuffer() {
        return this.mBuffer;
    }

    public void setDuration(int i) {
        this.duration = i;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setIsBufferBeforeEPD(boolean z) {
        this.isBufferBeforeEPD = z;
    }

    public boolean isBufferBeforeEPD() {
        return this.isBufferBeforeEPD;
    }

    public int getSequenceNumber() {
        return this.seqNumber;
    }
}
