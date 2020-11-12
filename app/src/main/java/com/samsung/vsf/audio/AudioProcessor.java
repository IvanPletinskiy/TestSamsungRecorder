package com.samsung.vsf.audio;

import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import androidx.core.content.ContextCompat;
import com.samsung.vsf.recognition.BufferObject;
import com.samsung.vsf.recognition.Recognizer;
import com.samsung.vsf.recognition.cmds.SendCmd;
import com.samsung.vsf.util.SVoiceLog;
import com.sec.vsg.voiceframework.DynamicRangeControl;
import com.sec.vsg.voiceframework.EndPointDetector;
import com.sec.vsg.voiceframework.NoiseReduction;
import com.sec.vsg.voiceframework.process.SignalAttributes;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AudioProcessor extends Thread {
    private static boolean DUMP_OPUS = false;
    private static boolean DUMP_PCM = false;
    private static boolean DUMP_SPEEX = false;
    private static boolean DUMP_WAVE = false;
    private static int sCountPCM;
    /* access modifiers changed from: private */
    public boolean isInitDone;
    private boolean isPCMInjectionEnabled = false;
    private AudioDumper mAudioDumper;
    /* access modifiers changed from: private */
    public AudioProcessorConfig mConfig;
    private DynamicRangeControl mDRC = null;
    private boolean mDumpEnable;
    /* access modifiers changed from: private */
    public IAudioEncoder mEncoder;
    private EndPointDetector mEpd = null;
    private NoiseReduction mNs = null;
    /* access modifiers changed from: private */
    public Recognizer mRecognizer;
    private ReleaseHandler mReleaseHandler = null;
    /* access modifiers changed from: private */
    public boolean mRun;
    private volatile int mSeqNumber;
    private int mSilenceTot;
    private int mSpeechTot;
    private final Object mWaitObject = new Object();
    private PcmDump pcmDumper;

    public AudioProcessor(AudioProcessorConfig audioProcessorConfig, Recognizer recognizer) {
        boolean z = false;
        this.mConfig = audioProcessorConfig;
        this.mRecognizer = recognizer;
        this.isInitDone = false;
        if (this.mReleaseHandler == null) {
            this.mReleaseHandler = new ReleaseHandler();
        }
        this.mReleaseHandler.sendEmptyMessageDelayed(1, 0);
        SVoiceLog.info("AudioProcessor", "Config details: isrecordingAtSDK: " + this.mConfig.shouldPerformRecording() + ", isEPDAtSDK: " + this.mConfig.shouldPerformSpeechDetection() + ", isEncoding: " + this.mConfig.shouldPerformEncoding());
        this.mSeqNumber = 0;
        this.mDumpEnable = (this.mConfig.shouldPerformRecording() || this.mConfig.shouldPerformSpeechDetection() || this.mConfig.shouldPerformEncoding()) && this.mConfig.isDumpRequired();
        if (ContextCompat.checkSelfPermission(recognizer.getAndroidContext(), "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            DUMP_PCM = Build.TYPE.equalsIgnoreCase("eng");
            int encodingType = this.mConfig.getEncodingType();
            DUMP_SPEEX = this.mDumpEnable && encodingType == 2;
            if (this.mDumpEnable && encodingType == 1) {
                z = true;
            }
            DUMP_OPUS = z;
        } else {
            SVoiceLog.warn("AudioProcessor", "Write external storage permission is denied");
        }
        Log.d("AudioProcessor", "DUMP Flag : DUMP_PCM " + DUMP_PCM + " DUMP_SPEEX " + DUMP_SPEEX + " DUMP_OPUS " + DUMP_OPUS);
        if (DUMP_PCM || DUMP_SPEEX || DUMP_WAVE || DUMP_OPUS) {
            new File(Environment.getExternalStorageDirectory(), "audio_dumps_svoice_sdk").mkdirs();
        }
        if (DUMP_WAVE || DUMP_SPEEX || DUMP_OPUS) {
            this.mAudioDumper = new AudioDumper(this.mConfig.getSamplingRate());
        }
        if (DUMP_PCM) {
            this.pcmDumper = new PcmDump();
            this.pcmDumper.openFile("audio_dumps_svoice_sdk/" + System.currentTimeMillis() + "_" + sCountPCM + ".pcm");
            sCountPCM = sCountPCM + 1;
        }
    }

    public void init() {
        SVoiceLog.debug("AudioProcessor", "Audio Processor init()");
        this.mSilenceTot = 0;
        this.mSpeechTot = 0;
    }

    public void run() {
        AudioDumper audioDumper;
        AudioDumper audioDumper2;
        AudioDumper audioDumper3;
        AudioDumper audioDumper4;
        AudioDumper audioDumper5;
        SVoiceLog.info("AudioProcessor", "AudioProcessor thread started");
        while (!this.isInitDone) {
            SVoiceLog.debug("AudioProcessor", "init is not complete yet. Hence, wait..");
            synchronized (this.mWaitObject) {
                try {
                    this.mWaitObject.wait(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        while (true) {
            byte[] bArr = null;
            boolean z = false;
            if (!this.mRun) {
                break;
            }
            if (this.mConfig.shouldPerformSpeechDetection()) {
                initSpeechDetector(this.mConfig.getSamplingRate());
            }
            BufferObject readAudioBuffer = this.mRecognizer.readAudioBuffer();
            SVoiceLog.info("AudioProcessor", "AudioProcessor Read Buffer ");
            if (readAudioBuffer != null) {
                byte[] byteBuffer = readAudioBuffer.getByteBuffer();
                if (byteBuffer != null && byteBuffer.length > 0) {
                    this.mSeqNumber++;
                    SendCmd sendCmd = new SendCmd(this.mSeqNumber);
                    SVoiceLog.info("AudioProcessor", "SendCmd object created with sequenceNumber " + this.mSeqNumber);
                    if (!this.mConfig.shouldPerformSpeechDetection()) {
                        if (readAudioBuffer.isEPDDetected()) {
                            sendCmd.setSpeechDetectionResult(SendCmd.SpeechDetectionResult.SPEECH_END);
                        } else {
                            sendCmd.setSpeechDetectionResult(SendCmd.SpeechDetectionResult.SPEECH);
                        }
                    }
                    sendCmd.setDuration(sizeToDuration(byteBuffer.length / 2));
                    if (this.mConfig.shouldPerformNS() || this.mConfig.shouldPerformSpeechDetection() || this.mConfig.shouldPerformRMSComputation() || this.mConfig.shouldPerformEncoding()) {
                        ByteBuffer wrap = ByteBuffer.wrap(byteBuffer);
                        wrap.order(ByteOrder.nativeOrder());
                        int length = byteBuffer.length / 2;
                        short[] sArr = new short[length];
                        wrap.asShortBuffer().get(sArr);
                        if (this.mConfig.shouldPerformSpeechDetection()) {
                            SVoiceLog.info("AudioProcessor", "AudioProcessor Will detect speech now");
                            int i = -1;
                            NoiseReduction noiseReduction = this.mNs;
                            if (noiseReduction != null) {
                                noiseReduction.process(sArr, length);
                            } else {
                                SVoiceLog.debug("AudioProcessor", "NS library is already destroyed");
                            }
                            EndPointDetector endPointDetector = this.mEpd;
                            if (endPointDetector != null) {
                                i = endPointDetector.process(sArr, length);
                            } else {
                                SVoiceLog.debug("AudioProcessor", "EPD library is already destroyed");
                            }
                            SVoiceLog.info("AudioProcessor", "AudioProcessor detected speech " + i);
                            if (i == 0) {
                                this.mSilenceTot += sizeToDuration(length);
                                SVoiceLog.info("AudioProcessor", "AudioProcessor silence detected : " + this.mSilenceTot + ":" + this.mSpeechTot);
                                if (this.mSpeechTot > this.mConfig.getSPDThresholdDuration() && this.mSilenceTot < this.mConfig.getEPDThresholdDuration()) {
                                    sendCmd.setSpeechDetectionResult(SendCmd.SpeechDetectionResult.SPEECH);
                                } else if ((this.mSpeechTot != 0 || this.mSilenceTot < 5000) && (this.mSpeechTot <= this.mConfig.getSPDThresholdDuration() || this.mSilenceTot < this.mConfig.getEPDThresholdDuration())) {
                                    sendCmd.setSpeechDetectionResult(SendCmd.SpeechDetectionResult.NONE);
                                } else {
                                    sendCmd.setSpeechDetectionResult(SendCmd.SpeechDetectionResult.SPEECH_END);
                                    this.mRecognizer.notifyEndOfSpeech();
                                    this.mSpeechTot = 0;
                                    this.mSilenceTot = 0;
                                    SVoiceLog.info("AudioProcessor", "EPD happened : destroySpeechDetector");
                                    destroySpeechDetector();
                                }
                            } else if (i == 1) {
                                if (this.mSpeechTot > this.mConfig.getSPDThresholdDuration()) {
                                    sendCmd.setSpeechDetectionResult(SendCmd.SpeechDetectionResult.SPEECH);
                                } else {
                                    sendCmd.setSpeechDetectionResult(SendCmd.SpeechDetectionResult.SPEECH_START);
                                    this.mRecognizer.notifyStartOfSpeech();
                                }
                                this.mSpeechTot += sizeToDuration(length);
                                this.mSilenceTot = 0;
                            } else {
                                SVoiceLog.info("AudioProcessor", "EPD is neither silence nor speech");
                            }
                            SVoiceLog.info("AudioProcessor", "Speech Detection Result: " + sendCmd.getSpeechDetectionResult() + ", detectionResult::" + i);
                        }
                        if (this.mConfig.shouldPerformDRC()) {
                            this.mDRC.process(sArr, length);
                        }
                        if (this.mConfig.shouldPerformRMSComputation()) {
                            int computeEnergy = SignalAttributes.computeEnergy(sArr, length, this.mConfig.getSamplingRate(), 16);
                            sendCmd.setRMSValue(computeEnergy);
                            this.mRecognizer.notifyRMSresult(computeEnergy);
                        }
                        if (!this.isPCMInjectionEnabled && sendCmd.getSpeechDetectionResult() == SendCmd.SpeechDetectionResult.SPEECH_END) {
                            z = true;
                        }
                        if (DUMP_WAVE && (audioDumper5 = this.mAudioDumper) != null) {
                            audioDumper5.dumpWave(byteBuffer, z);
                        }
                        if (DUMP_SPEEX && (audioDumper4 = this.mAudioDumper) != null) {
                            audioDumper4.dumpSpeex(byteBuffer, z);
                        }
                        if (DUMP_OPUS && (audioDumper3 = this.mAudioDumper) != null) {
                            audioDumper3.dumpOpus(byteBuffer, z);
                        }
                        bArr = this.mConfig.shouldPerformEncoding() ? this.mEncoder.encodeAudio(sArr) : byteBuffer;
                        if (DUMP_PCM) {
                            this.pcmDumper.writeData(byteBuffer);
                        }
                        if (this.mConfig.shouldPerformRecording() && this.mConfig.isRecordedBufferRequired()) {
                            this.mRecognizer.notifyRecordedBuffer(sArr);
                        }
                    }
                    sendCmd.setBuffer(bArr);
                    Recognizer recognizer = this.mRecognizer;
                    if (recognizer != null) {
                        recognizer.postCommand(sendCmd);
                    }
                }
                if (this.isPCMInjectionEnabled && byteBuffer != null && byteBuffer.length == 0) {
                    if ((DUMP_WAVE || DUMP_SPEEX || DUMP_OPUS) && (audioDumper2 = this.mAudioDumper) != null) {
                        audioDumper2.close();
                    }
                }
            }
        }
        ReleaseHandler releaseHandler = this.mReleaseHandler;
        if (releaseHandler != null) {
            releaseHandler.sendEmptyMessageDelayed(0, 0);
        }
        if (DUMP_PCM) {
            this.pcmDumper.closeFile();
            this.pcmDumper = null;
        }
        if ((DUMP_WAVE || DUMP_SPEEX || DUMP_OPUS) && (audioDumper = this.mAudioDumper) != null) {
            audioDumper.close();
            this.mAudioDumper = null;
        }
        SVoiceLog.info("AudioProcessor", "AudioProcessor thread exiting");
    }

    private synchronized void destroySpeechDetector() {
        SVoiceLog.info("AudioProcessor", "destroySpeechDetector");
        if (this.mEpd != null) {
            this.mEpd.destroy();
            this.mEpd = null;
        }
        if (this.mNs != null) {
            this.mNs.destroy();
            this.mNs = null;
        }
        if (this.mDRC != null) {
            this.mDRC.destroy();
            this.mDRC = null;
        }
    }

    private void initSpeechDetector(int i) {
        SVoiceLog.debug("AudioProcessor", "Initializing NoiseReduction with AudioFormat.CHANNEL_IN_MONO by default!");
        if (this.mNs == null) {
            this.mNs = new NoiseReduction(NoiseReduction.Mode.DEFAULT, 16, i);
        }
        if (this.mEpd == null) {
            this.mEpd = new EndPointDetector(EndPointDetector.Mode.DEFAULT, 16, i);
            this.mEpd.reset();
        }
        if (this.mDRC == null) {
            this.mDRC = new DynamicRangeControl(DynamicRangeControl.Mode.DEFAULT, 16, i);
        }
    }

    private class ReleaseHandler extends Handler {
        private ReleaseHandler() {
        }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            int i = message.what;
            if (i != 0) {
                if (i == 1) {
                    boolean unused = AudioProcessor.this.isInitDone = false;
                    if (AudioProcessor.this.mEncoder == null && AudioProcessor.this.mConfig.shouldPerformEncoding()) {
                        AudioProcessor audioProcessor = AudioProcessor.this;
                        IAudioEncoder unused2 = audioProcessor.mEncoder = EncoderFactory.getEncoderInstance(audioProcessor.mConfig);
                        AudioProcessor.this.mEncoder.init(AudioProcessor.this.mConfig);
                    }
                    boolean unused3 = AudioProcessor.this.mRun = true;
                    boolean unused4 = AudioProcessor.this.isInitDone = true;
                }
            } else if (AudioProcessor.this.mRun || !AudioProcessor.this.isInitDone) {
                SVoiceLog.info("AudioProcessor", "isInitDone : " + AudioProcessor.this.isInitDone);
            } else {
                if (AudioProcessor.this.mConfig.shouldPerformEncoding() && AudioProcessor.this.mEncoder != null) {
                    AudioProcessor.this.mEncoder.destroy();
                    IAudioEncoder unused5 = AudioProcessor.this.mEncoder = null;
                }
                Recognizer unused6 = AudioProcessor.this.mRecognizer = null;
                boolean unused7 = AudioProcessor.this.isInitDone = false;
            }
        }
    }

    public void exit() {
        this.mRun = false;
    }

    public void reset() {
        this.mSilenceTot = 0;
        this.mSpeechTot = 0;
        SVoiceLog.info("AudioProcessor", "Reset AudioProcessor : destroySpeechDetector");
        destroySpeechDetector();
    }

    private int sizeToDuration(int i) {
        return (i * 1000) / this.mConfig.getSamplingRate();
    }

    public String getMimeType() {
        int encodingType = this.mConfig.getEncodingType();
        if (encodingType == 1) {
            return "audio/x-opus-with-header-byte";
        }
        if (encodingType != 2) {
            return encodingType != 3 ? "audio/raw" : "audio/wav";
        }
        return "audio/x-speex-with-header-byte";
    }
}
