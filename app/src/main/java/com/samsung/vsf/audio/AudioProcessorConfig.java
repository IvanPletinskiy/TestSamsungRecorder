package com.samsung.vsf.audio;

import com.sec.android.app.voicenote.service.Recorder;

public class AudioProcessorConfig {
    private boolean enableDRC;
    private boolean enableDump;
    private int enableEncoding;
    private boolean enableNS;
    private boolean enablePCMDump;
    private boolean enableRMSComputation;
    private boolean enableRecording;
    private boolean enableSpeechDetection;
    private int epdThreshDur = Recorder.CHECK_AVAIABLE_STORAGE;
    private boolean isRecordedBufferRequired;
    private int samplingRate;
    private int spdThreshDur = 1;

    public AudioProcessorConfig setSamplingRate(int i) {
        this.samplingRate = i;
        return this;
    }

    public AudioProcessorConfig enableEncoding(int i) {
        this.enableEncoding = i;
        return this;
    }

    public AudioProcessorConfig enableSpeechDetection(boolean z) {
        this.enableSpeechDetection = z;
        return this;
    }

    public AudioProcessorConfig enableRecording(boolean z) {
        this.enableRecording = z;
        return this;
    }

    public AudioProcessorConfig enableNS(boolean z) {
        this.enableNS = z;
        return this;
    }

    public AudioProcessorConfig enableRMS(boolean z) {
        this.enableRMSComputation = z;
        return this;
    }

    public AudioProcessorConfig setEPDThresholdDuration(int i) {
        this.epdThreshDur = i;
        return this;
    }

    public AudioProcessorConfig setIsPCMDumpRequired(boolean z) {
        this.enablePCMDump = z;
        return this;
    }

    public AudioProcessorConfig setIsDumpRequired(boolean z) {
        this.enableDump = z;
        return this;
    }

    public AudioProcessorConfig setIsRecordedBufferRequired(boolean z) {
        this.isRecordedBufferRequired = z;
        return this;
    }

    public boolean shouldPerformEncoding() {
        return this.enableEncoding != 0;
    }

    public boolean shouldPerformSpeechDetection() {
        return this.enableSpeechDetection;
    }

    public boolean shouldPerformNS() {
        return this.enableNS;
    }

    public boolean shouldPerformRecording() {
        return this.enableRecording;
    }

    public boolean shouldPerformDRC() {
        return this.enableDRC;
    }

    public boolean shouldPerformRMSComputation() {
        return this.enableRMSComputation;
    }

    public int getSamplingRate() {
        return this.samplingRate;
    }

    public int getEPDThresholdDuration() {
        return this.epdThreshDur;
    }

    public int getEncodingType() {
        return this.enableEncoding;
    }

    public int getSPDThresholdDuration() {
        return this.spdThreshDur;
    }

    public boolean isRecordedBufferRequired() {
        return this.isRecordedBufferRequired;
    }

    public boolean isDumpRequired() {
        return this.enableDump;
    }
}
