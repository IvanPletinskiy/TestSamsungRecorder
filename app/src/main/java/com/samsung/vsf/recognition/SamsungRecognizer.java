package com.samsung.vsf.recognition;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import androidx.core.app.NotificationCompat;
import com.samsung.vsf.SpeechRecognizer;
import com.samsung.vsf.audio.AudioProcessor;
import com.samsung.vsf.audio.AudioProcessorConfig;
import com.samsung.vsf.audio.AudioRecorder;
import com.samsung.vsf.util.ClientLogger;
import com.samsung.vsf.util.DeviceInfo;
import com.samsung.vsf.util.SVoiceLog;
import com.sec.svoice.api.SVoice;
import com.sec.svoice.api.SVoiceSentinel;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Takes buffers from [AudioRecorder].
 */
public class SamsungRecognizer extends Recognizer {
    /* access modifiers changed from: private */
    public PriorityQueue<QueueItems> asrResponseQueue;
    /* access modifiers changed from: private */
    public String deviceId;
    private int epdOffset;
    /* access modifiers changed from: private */
    public boolean firstSend;
    /* access modifiers changed from: private */
    public Map<Integer, SVoiceWrapper> instanceList;
    /* access modifiers changed from: private */
    public ASRResultThread mASRResultThread;
    /* access modifiers changed from: private */
    public AudioProcessor mAudioProcessor;
    private AudioRecorder mAudioRecorder;
    /* access modifiers changed from: private */
    public boolean mCancelled;
    /* access modifiers changed from: private */
    public Context mCtx;
    private SVoiceWrapper mCurInstance;
    /* access modifiers changed from: private */
    public Handler mHandler;
    private int mInstId;
    private SVoiceWrapper mNextInstance;
    private RecState mRecState;
    private State mState;
    /* access modifiers changed from: private */
    public int total;

    enum RecState {
        START,
        LAST,
        END
    }

    enum State {
        IDLE,
        OPEN,
        PREPARED,
        SEND
    }

    public SamsungRecognizer(Context context, Looper looper, SpeechRecognizer.Config config) {
        super(context, looper, config);
        this.total = 0;
        this.deviceId = null;
        this.firstSend = false;
        this.mHandler = new Handler() {
            public void handleMessage(Message message) {
                Object obj;
                SVoiceLog.info("tickcount", "handleMessage called for Instance # : " + ((SVoiceWrapper) message.obj).f86id);
                if (message.what == 1 && SamsungRecognizer.this.instanceList != null && (obj = message.obj) != null && obj != null) {
                    SVoiceLog.info("tickcount", "Remove Instance # : " + ((SVoiceWrapper) message.obj).f86id);
                    try {
                        SamsungRecognizer.this.instanceList.remove(Integer.valueOf(((SVoiceWrapper) message.obj).f86id));
                    } catch (NullPointerException e) {
                        SVoiceLog.info("tickcount", e.getMessage());
                    }
                }
            }
        };
        this.deviceId = DeviceInfo.getInstance(getAndroidContext()).getUniqueDeviceIdentifier();
        int length = this.deviceId.length();
        StringBuilder sb = new StringBuilder();
        int i = length / 2;
        sb.append(this.deviceId.substring(i, length));
        sb.append(this.deviceId.substring(0, i));
        this.deviceId = sb.toString();
        this.mCtx = context;
        setState(State.IDLE);
        this.instanceList = new ConcurrentHashMap();
        newSVoiceInstance();
        setCmdHandler(new SamsungCmdHandler(this));
        startProcessingAudio();
        this.mAudioRecorder = new AudioRecorder(this);
        this.mAudioRecorder.setName("Thread-AudioRecorder");
        this.mAudioRecorder.start();
        this.mASRResultThread = new ASRResultThread();
        this.mASRResultThread.setName("Thread-ASRResultThread");
        this.mASRResultThread.start();
        SVoiceLog.info("tickcount", "SDK Version:: 20191001.1.35");
    }

    public void shutdown() {
        SVoiceLog.info("tickcount", "Shutting down recognizer");
        stopProcessingAudio();
        reset();
        closeAllInstances();
        stopRecordingIfRequired();
        this.mAudioRecorder.shutdown();
        this.mAudioRecorder = null;
        this.mASRResultThread.stopASRResultThread();
        this.mASRResultThread = null;
        this.total = 0;
        this.mCurInstance = null;
        this.mNextInstance = null;
        super.shutdown();
    }

    private void closeAllInstances() {
        this.mHandler.removeMessages(1);
        SVoiceLog.info("tickcount", "Start Total Instances : " + this.instanceList.size());
        for (SVoiceWrapper next : this.instanceList.values()) {
            SVoiceLog.info("tickcount", "closeAllInstances CALLED for instance # " + next.f86id);
            SVoiceLog.info("tickcount", "closing instance # " + next.f86id);
            next.close();
        }
        SVoiceLog.info("tickcount", "End Total Instances : " + this.instanceList.size());
        this.instanceList.clear();
        this.instanceList = null;
    }

    public void abort() {
        SVoiceLog.debug("tickcount", "abort()");
        try {
            if (this.mCurInstance != null && !this.mCurInstance.isClosing() && !this.mCurInstance.isProcessing) {
                this.mCurInstance.cancel();
            }
        } catch (NullPointerException e) {
            SVoiceLog.error("tickcount", e.getMessage());
        }
        try {
            if (this.mNextInstance != null && !this.mNextInstance.isClosing() && !this.mNextInstance.isProcessing) {
                this.mNextInstance.cancel();
            }
        } catch (NullPointerException e2) {
            SVoiceLog.error("tickcount", e2.getMessage());
        }
        ASRResultThread aSRResultThread = this.mASRResultThread;
        if (aSRResultThread != null) {
            int unused = aSRResultThread.currentId = -1;
        }
        PriorityQueue<QueueItems> priorityQueue = this.asrResponseQueue;
        if (priorityQueue != null) {
            priorityQueue.clear();
        }
    }

    /* access modifiers changed from: package-private */
    public void setState(State state) {
        this.mState = state;
    }

    /* access modifiers changed from: package-private */
    public void setRecState(RecState recState) {
        this.mRecState = recState;
    }

    /* access modifiers changed from: package-private */
    public State getState() {
        return this.mState;
    }

    /* access modifiers changed from: private */
    public RecState getRecState() {
        return this.mRecState;
    }

    /* access modifiers changed from: package-private */
    public boolean startRecordingIfRequired() {
        if (getConfig() == null || !getConfig().getIsRecordingRequired()) {
            return true;
        }
        boolean startRecording = this.mAudioRecorder.startRecording();
        AudioProcessor audioProcessor = this.mAudioProcessor;
        if (audioProcessor != null) {
            audioProcessor.init();
        }
        if (startRecording) {
            return startRecording;
        }
        SVoiceLog.debug("tickcount", "Notifying ERROR_RECORDER");
        notifyErrorString("recorder_error");
        return startRecording;
    }

    /* access modifiers changed from: package-private */
    public void stopRecordingIfRequired() {
        AudioRecorder audioRecorder = this.mAudioRecorder;
        if (audioRecorder != null && audioRecorder.isRecording()) {
            this.mAudioRecorder.stopRecording();
        }
        this.mASRResultThread.notifyRecognizerObject();
        AudioProcessor audioProcessor = this.mAudioProcessor;
        if (audioProcessor != null) {
            audioProcessor.reset();
        }
    }

    private void startProcessingAudio() {
        AudioProcessor audioProcessor = this.mAudioProcessor;
        if (audioProcessor == null || !audioProcessor.isAlive()) {
            AudioProcessorConfig audioProcessorConfig = new AudioProcessorConfig();
            audioProcessorConfig.enableNS(getConfig().getIsEnableNoiseSeparation());
            audioProcessorConfig.enableSpeechDetection(getConfig().getIsSpeechDetectionRequired());
            audioProcessorConfig.setEPDThresholdDuration(getConfig().getEPDThresholdDuration());
            audioProcessorConfig.enableEncoding(getConfig().getEncodingType());
            audioProcessorConfig.setSamplingRate(getConfig().getSamplingRate());
            audioProcessorConfig.setIsRecordedBufferRequired(getConfig().getIsRecordedBufferNeeded());
            audioProcessorConfig.enableRecording(getConfig().getIsRecordingRequired());
            audioProcessorConfig.enableRMS(getConfig().getIsRMSrequired());
            audioProcessorConfig.setIsPCMDumpRequired(getConfig().getIsPCMDumpRequired());
            audioProcessorConfig.setIsDumpRequired(getConfig().getIsDumpRequired());
            this.mAudioProcessor = new AudioProcessor(audioProcessorConfig, this);
            this.mAudioProcessor.setName("Thread-AudioProcessor-sdk");
            this.mAudioProcessor.start();
            return;
        }
        SVoiceLog.info("tickcount", "AudioProcessor is non null and alive!!");
    }

    private void stopProcessingAudio() {
        AudioProcessor audioProcessor = this.mAudioProcessor;
        if (audioProcessor != null) {
            audioProcessor.exit();
            this.mAudioProcessor = null;
        }
        clearAudioQueue();
    }

    /* access modifiers changed from: private */
    public void setCancelled() {
        if (!this.mCancelled) {
            SVoiceLog.info("tickcount", "Cancel flag set");
        }
        this.mCancelled = true;
    }

    /* access modifiers changed from: package-private */
    public void clearCancelled() {
        if (this.mCancelled) {
            SVoiceLog.info("tickcount", "Cancel flag cleared");
        }
        this.mCancelled = false;
    }

    /* access modifiers changed from: private */
    public boolean checkIfCancelledCalledAlready() {
        return this.mCancelled;
    }

    /* access modifiers changed from: private */
    public boolean checkIfCancelledCalledParallely(boolean z) {
        return z && !this.mCancelled;
    }

    /* access modifiers changed from: package-private */
    public void openNextInstance() {
        if (!this.mNextInstance.isOpened() && !this.mNextInstance.isOpening()) {
            this.mNextInstance.openAsync();
        }
    }

    /* access modifiers changed from: package-private */
    public void switchInstance() {
        if (getConfig().getSessionMode() == 2) {
            this.mCurInstance = null;
            this.mCurInstance = this.mNextInstance;
            this.mNextInstance = new SVoiceWrapper(this.mInstId);
            this.instanceList.put(Integer.valueOf(this.mInstId), this.mNextInstance);
            this.mInstId++;
            openNextInstance();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean speechTimeLimitExceeded() {
        return this.mCurInstance.isSpeechTimeLimitExceeded();
    }

    /* access modifiers changed from: package-private */
    public boolean svoiceOpen() {
        return this.mCurInstance.open();
    }

    /* access modifiers changed from: package-private */
    public void svoiceOpenAsync() {
        this.mCurInstance.openAsync();
    }

    /* access modifiers changed from: package-private */
    public boolean svoicePrepare() {
        return this.mCurInstance.prepare();
    }

    /* access modifiers changed from: package-private */
    public boolean svoiceSend(byte[] bArr) {
        return this.mCurInstance.send(bArr);
    }

    /* access modifiers changed from: package-private */
    public boolean svoiceProcess(boolean z) {
        if (z) {
            SpeechRecognizer.Config config = getConfig();
            if (config != null) {
                this.epdOffset = config.getEPDThresholdDuration() / 10;
            }
        } else {
            this.epdOffset = 0;
        }
        return this.mCurInstance.process();
    }

    /* access modifiers changed from: package-private */
    public void notifyCCLError(String str) {
        SVoiceWrapper sVoiceWrapper = this.mCurInstance;
        if (sVoiceWrapper != null) {
            sVoiceWrapper.handleException(new Exception(str), true);
            this.mCurInstance.isOpened = false;
        }
    }

    private void reset() {
        setState(State.IDLE);
        clearAudioQueue();
        clearCmds();
        clearCancelled();
    }

    private void newSVoiceInstance() {
        this.mCurInstance = new SVoiceWrapper(this.mInstId);
        this.instanceList.put(Integer.valueOf(this.mInstId), this.mCurInstance);
        this.mInstId++;
        if (getConfig().getSessionMode() == 2) {
            this.mNextInstance = new SVoiceWrapper(this.mInstId);
            this.instanceList.put(Integer.valueOf(this.mInstId), this.mNextInstance);
            this.mInstId++;
        }
    }

    /* access modifiers changed from: package-private */
    public void createInstanceIfDestroyed() {
        SVoiceWrapper sVoiceWrapper = this.mCurInstance;
        if (sVoiceWrapper == null || sVoiceWrapper.mSVoiceClient == null) {
            newSVoiceInstance();
        }
        PriorityQueue<QueueItems> priorityQueue = this.asrResponseQueue;
        if (priorityQueue != null) {
            priorityQueue.clear();
        }
        ASRResultThread aSRResultThread = this.mASRResultThread;
        if (aSRResultThread != null) {
            int unused = aSRResultThread.currentId = this.mCurInstance.f86id;
        }
    }

    /* access modifiers changed from: package-private */
    public void setStartSeqNumber(int i) {
        this.mCurInstance.setStartSeqNumber(i);
    }

    private class SVoiceObjectDestroyable implements Runnable {
        private int mId;
        private SVoice mTempClient;

        SVoiceObjectDestroyable(SVoice sVoice, int i) {
            this.mTempClient = sVoice;
            this.mId = i;
        }

        public void run() {
            if (this.mTempClient != null) {
                SVoiceLog.info("tickcount", "SamsungRecognizer@" + this.mId + " of " + this.mTempClient + " destroying");
                this.mTempClient.destroy();
                this.mTempClient = null;
            }
        }
    }

    private class SVoiceWrapper extends SVoiceSentinel {

        /* renamed from: id */
        int f86id;
        volatile boolean isClosing;
        volatile boolean isOpened;
        volatile boolean isProcessing;
        String mSVSessionId;
        SVoice mSVoiceClient;
        long openLatency;
        Thread openThread;
        long openTimeStamp = 0;
        long processTimeStamp;
        private int startSeqNumber = 0;

        public void resultNLU(int i, Properties properties) {
        }

        SVoiceWrapper(int i) {
            this.f86id = i;
            createSVoiceClient();
        }

        public void resultPrepare(int i, Properties properties) {
            super.resultPrepare(i, properties);
            String property = properties.getProperty(NotificationCompat.CATEGORY_ERROR);
            if (property == null || property.equals("0")) {
                SVoiceLog.info("tickcount", "prepare2 successful. Hence, continue");
                return;
            }
            SVoiceLog.info("tickcount", "prepare2 FAILED. Hence, close the connection");
            handleError(property);
        }

        public void resultASR(int i, Properties properties) {
            String property = properties.getProperty(NotificationCompat.CATEGORY_ERROR);
            if (property != null && !property.equals("0")) {
                SVoiceLog.verbose("tickcount", "In error ASRResult SamsungRecognizer@" + this.f86id + " metadata:-" + properties.getProperty("metadata") + "error: " + property);
                handleError(property);
            } else if (this.isOpened) {
                properties.put("instanceId", Integer.toString(this.f86id));
                properties.put("asrLatency", getProcessToASRLatency());
                String str = null;
                if (SamsungRecognizer.this.getConfig() != null) {
                    str = SamsungRecognizer.this.getConfig().getLocale();
                }
                if (properties.containsKey("utterance")) {
                    properties.put("utterance_original", properties.getProperty("utterance"));
                }
                if ("ja-JP".equals(str) || "zh-CN".equals(str) || "zh-SG".equals(str) || "zh-HK".equals(str) || "zh-TW".equals(str)) {
                    if (properties.containsKey("utterancetoken")) {
                        properties.put("utterance", properties.getProperty("utterancetoken"));
                    }
                } else if (properties.containsKey("utterance")) {
                    properties.put("utterance", properties.getProperty("utterance"));
                }
                if (properties.containsKey("itn")) {
                    properties.put("itn", properties.getProperty("itn"));
                }
                properties.put("startSequenceNumber", "" + this.startSeqNumber);
                SVoiceLog.info("tickcount", "Start Seq Number :" + this.startSeqNumber);
                SVoiceLog.verbose("tickcount", "SamsungRecognizer@" + this.f86id + " resultASR():-" + properties.getProperty("utterance") + " itn:" + properties.getProperty("itn"));
                if (com.sec.android.app.voicenote.common.util.DeviceInfo.STR_TRUE.equalsIgnoreCase(properties.getProperty("islast"))) {
                    SVoiceLog.info("ASRProfiling", "resultASR() " + System.currentTimeMillis());
                    if (SamsungRecognizer.this.getConfig().getSessionMode() == 2) {
                        SamsungRecognizer.this.post(new Runnable() {
                            public void run() {
                                SVoiceWrapper.this.close();
                            }
                        });
                    }
                }
                String property2 = properties.getProperty("itn");
                if (property2.length() > 0 || !"false".equalsIgnoreCase(properties.getProperty("islast"))) {
                    if (com.sec.android.app.voicenote.common.util.DeviceInfo.STR_TRUE.equalsIgnoreCase(properties.getProperty("islast"))) {
                        System.out.println("TEST_PLATFORM: VOICE_SEARCH_COMPLETE");
                    }
                    if (SamsungRecognizer.this.mASRResultThread != null) {
                        SVoiceLog.info("tickcount", "Start Seq Number :" + properties.getProperty("startSeqNumber"));
                        SamsungRecognizer.this.mASRResultThread.addResponseToqueue(this.f86id, properties);
                        return;
                    }
                    return;
                }
                SVoiceLog.info("tickcount", "empty partial ASRResult SamsungRecognizer@" + this.f86id + "utt : " + properties.getProperty("utterance") + " ts: " + property2);
            }
        }

        private void handleError(String str) {
            PrintStream printStream = System.out;
            printStream.println("TEST_PLATFORM: ERROR" + str);
            handleException(new Exception("server_error"), true);
            this.isOpened = false;
            SamsungRecognizer.this.post(new Runnable() {
                public void run() {
                    SVoiceWrapper.this.close();
                }
            });
            if (SamsungRecognizer.this.mASRResultThread != null) {
                SamsungRecognizer.this.mASRResultThread.addResponseToqueue(this.f86id, new Properties());
            }
        }

        /* access modifiers changed from: package-private */
        public boolean isConnected() {
            SVoice sVoice = this.mSVoiceClient;
            boolean z = sVoice != null && sVoice.isConnected();
            SVoiceLog.info("tickcount", "SamsungRecognizer@" + this.f86id + " isConnected : " + z);
            return z;
        }

        /* access modifiers changed from: package-private */
        public boolean open() {
            boolean z;
            boolean z2 = false;
            if (this.isOpened) {
                z = isConnected();
                if (z && !isSessionTimedOut()) {
                    SVoiceLog.info("tickcount", "SamsungRecognizer@" + this.f86id + " already opened");
                    this.openThread = null;
                    boolean access$500 = SamsungRecognizer.this.checkIfCancelledCalledParallely(this.isOpened);
                    this.isOpened = access$500;
                    return access$500;
                }
            } else {
                z = false;
            }
            if (isOpening()) {
                while (!z2) {
                    try {
                        SVoiceLog.info("tickcount", "SamsungRecognizer@" + this.f86id + " open(): waiting for openAsync() to complete");
                        this.openThread.join();
                        z2 = true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                this.openThread = null;
                boolean access$5002 = SamsungRecognizer.this.checkIfCancelledCalledParallely(this.isOpened);
                this.isOpened = access$5002;
                return access$5002;
            }
            if (this.isOpened) {
                if (!z) {
                    this.isOpened = false;
                    if (this.mSVoiceClient != null) {
                        SVoiceLog.info("tickcount", "SamsungRecognizer@" + this.f86id + " open(): connection is teared down. Destroy the object");
                        new Thread(new SVoiceObjectDestroyable(this.mSVoiceClient, this.f86id)).start();
                        this.mSVoiceClient = null;
                    }
                } else if (SamsungRecognizer.this.getConfig().getSessionMode() != 1 || isSessionTimedOut()) {
                    close();
                    this.isOpened = false;
                } else {
                    SVoiceLog.info("tickcount", "SamsungRecognizer@" + this.f86id + " SINGLE_SESSION_MODE and session is not timedout. Hence, re-use the object");
                    boolean access$5003 = SamsungRecognizer.this.checkIfCancelledCalledParallely(true);
                    this.isOpened = access$5003;
                    return access$5003;
                }
            }
            this.openThread = null;
            if (this.mSVoiceClient == null) {
                SVoiceLog.debug("tickcount", "SamasungRecognizer@" + this.f86id + "open(): creating new mSVoiceClient");
                createSVoiceClient();
                boolean unused = SamsungRecognizer.this.mCancelled = false;
            }
            SpeechRecognizer.Config config = SamsungRecognizer.this.getConfig();
            int i = 0;
            boolean z3 = false;
            while (true) {
                if (i >= 3 || z3) {
                    break;
                }
                try {
                    if (this.mSVoiceClient != null) {
                        SVoiceLog.info("tickcount", "SamsungRecognizer@" + this.f86id + getElapsedString() + " open() [try " + (i + 1) + "]" + ", device id: " + SamsungRecognizer.this.deviceId);
                        this.openLatency = System.currentTimeMillis();
                        StringBuilder sb = new StringBuilder();
                        sb.append("open() starts ");
                        sb.append(System.currentTimeMillis());
                        SVoiceLog.info("ASRProfiling", sb.toString());
                        Properties open = this.mSVoiceClient.open(getOpenDeviceInfo(config));
                        SVoiceLog.info("ASRProfiling", "open() ends " + System.currentTimeMillis());
                        this.openLatency = System.currentTimeMillis() - this.openLatency;
                        updateTimeStamp();
                        if (open != null && !open.isEmpty()) {
                            if (!isValidEndpoint(open)) {
                                SVoiceLog.info("tickcount", "open() has failed for SamsungRecognizer@" + this.f86id);
                                String property = open.getProperty("permission");
                                SVoiceLog.info("tickcount", "open() Permission " + property);
                                if (property != null && property.equalsIgnoreCase("1")) {
                                    SVoiceLog.info("tickcount", "open() Device is BLOCKED ");
                                    break;
                                }
                                String property2 = open.getProperty("serverEndpoint");
                                SVoiceLog.info("tickcount", "open() serverEndpoint " + property2);
                                if (property2 != null) {
                                    new CancelThread(this.mSVoiceClient, this.f86id).start();
                                    this.mSVoiceClient = null;
                                    config.setServerDetails(property2, config.getPortNumber());
                                    createSVoiceClient();
                                    boolean unused2 = SamsungRecognizer.this.mCancelled = false;
                                }
                            } else {
                                this.mSVSessionId = open.getProperty("sessionid");
                                SVoiceLog.info("tickcount", "SamsungRecognizer sessionId " + this.mSVSessionId);
                                SVoiceLog.info("tickcount", "open() successful for SamsungRecognizer@" + this.f86id);
                                z3 = true;
                            }
                        }
                    } else {
                        continue;
                    }
                } catch (Exception e2) {
                    handleException(e2, i == 2);
                    if (i == 2) {
                        this.isOpened = false;
                    }
                }
                i++;
            }
            boolean access$5004 = SamsungRecognizer.this.checkIfCancelledCalledParallely(z3);
            this.isOpened = access$5004;
            return access$5004;
        }

        /* access modifiers changed from: package-private */
        public Properties getOpenDeviceInfo(SpeechRecognizer.Config config) {
            Properties properties = new Properties();
            properties.put("locale", config.getLocale());
            properties.put("serviceinfo", DeviceInfo.getInstance(SamsungRecognizer.this.getAndroidContext()).getModelAndVersion());
            properties.put("audiosharing", String.valueOf(config.getIsTOSOptionAccepted()));
            properties.put("usehash", com.sec.android.app.voicenote.common.util.DeviceInfo.STR_TRUE);
            properties.put("client_log", ClientLogger.getOpenLogger(config));
            properties.put("currentendpoint", config.getServerIP());
            properties.put("clientapp", "dict");
            return properties;
        }

        /* access modifiers changed from: package-private */
        public boolean isValidEndpoint(Properties properties) {
            String property = properties.getProperty("isValidEndpoint");
            SVoiceLog.info("tickcount", "open() isValid : " + property);
            return property == null || property.length() <= 0 || property.equalsIgnoreCase(com.sec.android.app.voicenote.common.util.DeviceInfo.STR_TRUE);
        }

        /* access modifiers changed from: package-private */
        public boolean isOpening() {
            Thread thread = this.openThread;
            return thread != null && thread.isAlive();
        }

        /* access modifiers changed from: package-private */
        public boolean isClosing() {
            return this.isClosing;
        }

        /* access modifiers changed from: package-private */
        public boolean isOpened() {
            return this.isOpened;
        }

        /* access modifiers changed from: package-private */
        public void openAsync() {
//            C06883 r1 = new Runnable() {
//                public void run() {
//                    SVoiceLog.info("tickcount", "SamsungRecognizer@" + SVoiceWrapper.this.f86id + " openAsync()");
//                    SVoiceWrapper sVoiceWrapper = SVoiceWrapper.this;
//                    if (sVoiceWrapper.mSVoiceClient == null) {
//                        sVoiceWrapper.createSVoiceClient();
//                    }
//                    SpeechRecognizer.Config config = SamsungRecognizer.this.getConfig();
//                    int i = 0;
//                    boolean z = false;
//                    while (true) {
//                        if (i >= 3 || z) {
//                            break;
//                        }
//                        try {
//                            if (SVoiceWrapper.this.mSVoiceClient != null) {
//                                SVoiceLog.info("ASRProfiling", "open() starts in openAsync " + System.currentTimeMillis());
//                                Properties open = SVoiceWrapper.this.mSVoiceClient.open(SVoiceWrapper.this.getOpenDeviceInfo(config));
//                                SVoiceLog.info("ASRProfiling", "open() ends in openAsync " + System.currentTimeMillis());
//                                SVoiceWrapper.this.updateTimeStamp();
//                                if (open != null) {
//                                    if (!open.isEmpty()) {
//                                        if (!SVoiceWrapper.this.isValidEndpoint(open)) {
//                                            SVoiceLog.info("tickcount", "open() has failed for SamsungRecognizer@" + SVoiceWrapper.this.f86id);
//                                            String property = open.getProperty("permission");
//                                            SVoiceLog.info("tickcount", "open() Permission " + property);
//                                            if (property != null && property.equalsIgnoreCase("1")) {
//                                                SVoiceLog.info("tickcount", "open() Device is BLOCKED ");
//                                                break;
//                                            }
//                                            String property2 = open.getProperty("serverEndpoint");
//                                            SVoiceLog.info("tickcount", "open() serverEndpoint " + property2);
//                                            if (property2 != null) {
//                                                new CancelThread(SVoiceWrapper.this.mSVoiceClient, SVoiceWrapper.this.f86id).start();
//                                                SVoiceWrapper.this.mSVoiceClient = null;
//                                                config.setServerDetails(property2, config.getPortNumber());
//                                                SVoiceWrapper.this.createSVoiceClient();
//                                                boolean unused = SamsungRecognizer.this.mCancelled = false;
//                                            }
//                                        } else {
//                                            SVoiceWrapper.this.mSVSessionId = open.getProperty("sessionid");
//                                            SVoiceLog.info("tickcount", "SamsungRecognizer sessionId " + SVoiceWrapper.this.mSVSessionId);
//                                            SVoiceLog.info("tickcount", "open() successful for SamsungRecognizer@" + SVoiceWrapper.this.f86id);
//                                            z = true;
//                                        }
//                                    }
//                                }
//                                SVoiceLog.info("tickcount", "open() has failed for SamsungRecognizer@" + SVoiceWrapper.this.f86id);
//                            } else {
//                                continue;
//                            }
//                        } catch (Exception e) {
//                            SVoiceWrapper.this.handleException(e, false);
//                            if (i == 2) {
//                                SVoiceWrapper.this.isOpened = false;
//                            }
//                        }
//                        i++;
//                    }
//                    SVoiceWrapper.this.isOpened = z;
//                }
//            };
//            this.openThread = new Thread(r1, "Thread-Recognizer@" + this.f86id);
//            this.openThread.start();
        }

        /* access modifiers changed from: package-private */
        public boolean prepare() {
            SVoiceLog.info("tickcount", "SamsungRecognizer@" + this.f86id + getElapsedString() + " prepare()" + ", device id: " + SamsungRecognizer.this.deviceId);
            boolean z = false;
            try {
                if (!SamsungRecognizer.this.checkIfCancelledCalledAlready() && this.mSVoiceClient != null) {
                    this.mSVoiceClient.time_out(10000);
                    SVoiceLog.info("ASRProfiling", "prepare2() starts " + System.currentTimeMillis());
                    this.mSVoiceClient.prepare2(1, getAsrParams(SamsungRecognizer.this.mAudioProcessor.getMimeType(), SamsungRecognizer.this.getConfig().getSamplingRate(), SamsungRecognizer.this.getConfig().getIsByteOrderLittleEndian(), SamsungRecognizer.this.getConfig().getLocale()), new Properties());
                    SVoiceLog.info("ASRProfiling", "prepare2() ends " + System.currentTimeMillis());
                    boolean unused = SamsungRecognizer.this.firstSend = true;
                    z = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                handleException(e, true);
                this.isOpened = false;
            }
            return SamsungRecognizer.this.checkIfCancelledCalledParallely(z);
        }

        /* access modifiers changed from: package-private */
        public boolean send(byte[] bArr) {
            boolean z = false;
            if (SamsungRecognizer.this.firstSend) {
                SVoiceLog.info("ASRProfiling", "send() starts " + System.currentTimeMillis());
                boolean unused = SamsungRecognizer.this.firstSend = false;
            }
            if (this.isOpened) {
                SVoiceLog.info("tickcount", "SamsungRecognizer@" + this.f86id + getElapsedString() + " send(), device id: " + SamsungRecognizer.this.deviceId);
                try {
                    if (!SamsungRecognizer.this.checkIfCancelledCalledAlready() && this.mSVoiceClient != null) {
                        this.mSVoiceClient.send(bArr);
                        z = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    handleException(e, true);
                    this.isOpened = false;
                }
            }
            return SamsungRecognizer.this.checkIfCancelledCalledParallely(z);
        }

        /* access modifiers changed from: package-private */
        public boolean process() {
            boolean z = false;
            if (this.isOpened) {
                SVoiceLog.info("tickcount", "SamsungRecognizer@" + this.f86id + getElapsedString() + " process() , device id: " + SamsungRecognizer.this.deviceId);
                try {
                    if (!SamsungRecognizer.this.checkIfCancelledCalledAlready() && this.mSVoiceClient != null) {
                        this.processTimeStamp = SystemClock.uptimeMillis();
                        this.isProcessing = true;
                        SVoiceLog.info("ASRProfiling", "process() starts " + System.currentTimeMillis());
                        this.mSVoiceClient.process(new Properties());
                        SVoiceLog.info("ASRProfiling", "process() ends " + System.currentTimeMillis());
                        this.isProcessing = false;
                        z = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    handleException(e, true);
                    this.isOpened = false;
                }
            }
            return SamsungRecognizer.this.checkIfCancelledCalledParallely(z);
        }

        /* access modifiers changed from: package-private */
        public boolean cancel() {
            if (this.isOpened) {
                SamsungRecognizer.this.setCancelled();
                this.isOpened = false;
                SVoice sVoice = this.mSVoiceClient;
                this.mSVoiceClient = null;
                SVoiceLog.info("tickcount", "SamsungRecognizer@" + this.f86id + getElapsedString() + " cancel() , device id: " + SamsungRecognizer.this.deviceId);
                if (sVoice != null) {
                    sVoice.cancel();
                }
                if (sVoice != null && !this.isClosing) {
                    SVoiceLog.info("tickcount", "SamsungRecognizer@" + this.f86id + getElapsedString() + " destroy() , device id: " + SamsungRecognizer.this.deviceId);
                    sVoice.destroy();
                }
                this.mSVSessionId = null;
                this.openThread = null;
                Message obtain = Message.obtain();
                obtain.what = 1;
                obtain.obj = this;
                SamsungRecognizer.this.mHandler.sendMessage(obtain);
            }
            return true;
        }

        /* access modifiers changed from: package-private */
        public boolean close() {
            if (isOpening()) {
                boolean z = false;
                while (!z) {
                    try {
                        SVoiceLog.info("tickcount", "SamsungRecognizer@" + this.f86id + getElapsedString() + " close(): waiting for openAsync() to complete");
                        this.openThread.join();
                        z = true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                this.openThread = null;
            }
            if (this.isOpened) {
                SVoiceLog.info("tickcount", "SamsungRecognizer@" + this.f86id + getElapsedString() + " close(), device id: " + SamsungRecognizer.this.deviceId);
                if (!SamsungRecognizer.this.checkIfCancelledCalledAlready()) {
                    this.isClosing = true;
                    SVoice sVoice = this.mSVoiceClient;
                    if (sVoice != null) {
                        sVoice.close();
                    }
                    if (this.mSVoiceClient != null) {
                        SVoiceLog.info("tickcount", "SamsungRecognizer@" + this.f86id + getElapsedString() + " destroy(), device id: " + SamsungRecognizer.this.deviceId);
                        StringBuilder sb = new StringBuilder();
                        sb.append("destroySVoiceClient");
                        sb.append(this.mSVoiceClient);
                        SVoiceLog.debug("tickcount", sb.toString());
                        this.mSVoiceClient.destroy();
                        this.mSVoiceClient = null;
                    }
                    this.isClosing = false;
                }
                this.isOpened = false;
                Message obtain = Message.obtain();
                obtain.what = 1;
                obtain.obj = this;
                SamsungRecognizer.this.mHandler.sendMessage(obtain);
            }
            this.mSVSessionId = null;
            this.openThread = null;
            return true;
        }

        /* access modifiers changed from: package-private */
        public boolean isSessionTimedOut() {
            return SystemClock.uptimeMillis() - this.openTimeStamp >= 270000;
        }

        /* access modifiers changed from: package-private */
        public boolean isSpeechTimeLimitExceeded() {
            if (SamsungRecognizer.this.getConfig().getSessionMode() != 1 && SystemClock.uptimeMillis() - this.openTimeStamp >= 120000) {
                return true;
            }
            return false;
        }

        /* access modifiers changed from: private */
        public void createSVoiceClient() {
            SpeechRecognizer.Config config = SamsungRecognizer.this.getConfig();
            if (config != null) {
                if (config.getIsTLSUsed()) {
                    this.mSVoiceClient = new SVoice(config.getServerIP(), config.getPortNumber(), DeviceInfo.getInstance(SamsungRecognizer.this.getAndroidContext()).getUniqueDeviceIdentifier(), this, true, config.getCertificatePath());
                } else {
                    this.mSVoiceClient = new SVoice(config.getServerIP(), config.getPortNumber(), DeviceInfo.getInstance(SamsungRecognizer.this.getAndroidContext()).getUniqueDeviceIdentifier(), this);
                }
                this.mSVoiceClient.enable_log(7);
                this.mSVoiceClient.time_out(config.getRPCTimeoutValue());
            }
            SVoiceLog.debug("tickcount", "createSVoiceClient " + this.mSVoiceClient + " for SamsungRecognizer@" + this.f86id);
        }

        private Properties getAsrParams(String str, int i, boolean z, String str2) {
            Properties properties = new Properties();
            SpeechRecognizer.Config config = SamsungRecognizer.this.getConfig();
            String str3 = z ? "LE" : "BE";
            if (str.compareTo("audio/raw") == 0) {
                properties.put("contenttype", str + ";coding=linear;sampleRate=" + i + ";byteorder=" + str3);
            } else if (str.compareTo("audio/x-speex-with-header-byte") == 0 || str.compareTo("audio/x-opus-with-header-byte") == 0) {
                properties.put("contenttype", str + ";rate=" + i + ";channel=" + "1");
            } else {
                properties.put("contenttype", str);
            }
            properties.put("locale", str2);
            String str4 = com.sec.android.app.voicenote.common.util.DeviceInfo.STR_TRUE;
            properties.put("getmetadata", str4);
            properties.put("useplm", config.getIsUsePLMRequired() ? str4 : "false");
            if (config.getASRDictationModel() != null) {
                properties.put("rampcode", config.getASRDictationModel());
            }
            if (!config.getIsCensored()) {
                str4 = "false";
            }
            properties.put("censor", str4);
            properties.put("client_log", ClientLogger.getPrepareLogger(this.openLatency));
            return properties;
        }

        private boolean isNetworkConnected() {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) SamsungRecognizer.this.mCtx.getSystemService("connectivity")).getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        }

        /* access modifiers changed from: private */
        public void handleException(Exception exc, boolean z) {
            SVoiceLog.info("tickcount", "Exception@" + this.f86id + ": " + exc.getMessage() + " notify : " + z);
            if (SamsungRecognizer.this.mASRResultThread != null) {
                SamsungRecognizer samsungRecognizer = SamsungRecognizer.this;
                int unused = samsungRecognizer.total = samsungRecognizer.total + SamsungRecognizer.this.mASRResultThread.tempFrameCount;
                int unused2 = SamsungRecognizer.this.mASRResultThread.tempFrameCount = 0;
                int unused3 = SamsungRecognizer.this.mASRResultThread.currentId = -1;
                SamsungRecognizer.this.asrResponseQueue.clear();
            }
            if (!isNetworkConnected()) {
                SamsungRecognizer.this.notifyErrorString("no_network");
            } else {
                SamsungRecognizer.this.notifyErrorString("server_error");
            }
            SamsungRecognizer.this.clearCmds();
        }

        /* access modifiers changed from: private */
        public void updateTimeStamp() {
            this.openTimeStamp = SystemClock.uptimeMillis();
        }

        private String getElapsedString() {
            long uptimeMillis = SystemClock.uptimeMillis() - this.openTimeStamp;
            return "[t + " + (uptimeMillis / 1000) + "." + (uptimeMillis % 1000) + "s]";
        }

        private String getProcessToASRLatency() {
            long uptimeMillis = SystemClock.uptimeMillis() - this.processTimeStamp;
            return (uptimeMillis / 1000) + "." + (uptimeMillis % 1000);
        }

        /* access modifiers changed from: private */
        public void setStartSeqNumber(int i) {
            this.startSeqNumber = i;
        }
    }

    static class CancelThread extends Thread {

        /* renamed from: id */
        private int f85id;
        private SVoice mClient;

        CancelThread(SVoice sVoice, int i) {
            this.mClient = sVoice;
            this.f85id = i;
            setName("CancelThread@" + i);
        }

        public void run() {
            super.run();
            if (this.mClient != null) {
                SVoiceLog.info("tickcount", "SamsungRecognizer@" + this.f85id + " cancel()");
                this.mClient.cancel();
            }
            if (this.mClient != null) {
                SVoiceLog.info("tickcount", "SamsungRecognizer@" + this.f85id + " destroy()");
                this.mClient.destroy();
            }
            this.mClient = null;
        }
    }

    class ASRResultThread extends Thread {
        /* access modifiers changed from: private */
        public int currentId;
        private boolean isRunning = true;
        private Object recognizerLockObject = new Object();
        /* access modifiers changed from: private */
        public int tempFrameCount;

        ASRResultThread() {
            PriorityQueue unused = SamsungRecognizer.this.asrResponseQueue = new PriorityQueue();
            this.currentId = -1;
            this.tempFrameCount = 0;
        }

        /* JADX WARNING: Removed duplicated region for block: B:38:0x01bc A[Catch:{ InterruptedException -> 0x0298 }] */
        /* JADX WARNING: Removed duplicated region for block: B:39:0x01cd A[Catch:{ InterruptedException -> 0x0298 }] */
        /* JADX WARNING: Removed duplicated region for block: B:42:0x0217 A[Catch:{ InterruptedException -> 0x0298 }] */
        /* JADX WARNING: Removed duplicated region for block: B:48:0x023c A[Catch:{ InterruptedException -> 0x0298 }] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r14 = this;
                super.run()
            L_0x0003:
                boolean r0 = r14.isRunning
                if (r0 == 0) goto L_0x02b3
                java.lang.Object r0 = r14.recognizerLockObject
                if (r0 == 0) goto L_0x02aa
                monitor-enter(r0)
                java.lang.String r1 = "tickcount"
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x02a7 }
                r2.<init>()     // Catch:{ all -> 0x02a7 }
                java.lang.String r3 = "In ASRResultThread :currrenID:"
                r2.append(r3)     // Catch:{ all -> 0x02a7 }
                int r3 = r14.currentId     // Catch:{ all -> 0x02a7 }
                r2.append(r3)     // Catch:{ all -> 0x02a7 }
                java.lang.String r3 = "isRunning:"
                r2.append(r3)     // Catch:{ all -> 0x02a7 }
                boolean r3 = r14.isRunning     // Catch:{ all -> 0x02a7 }
                r2.append(r3)     // Catch:{ all -> 0x02a7 }
                java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x02a7 }
                com.samsung.vsf.util.SVoiceLog.info(r1, r2)     // Catch:{ all -> 0x02a7 }
                com.samsung.vsf.recognition.SamsungRecognizer r1 = com.samsung.vsf.recognition.SamsungRecognizer.this     // Catch:{ all -> 0x02a7 }
                java.util.PriorityQueue r1 = r1.asrResponseQueue     // Catch:{ all -> 0x02a7 }
                if (r1 == 0) goto L_0x029d
            L_0x0036:
                boolean r1 = r14.isRunning     // Catch:{ all -> 0x02a7 }
                if (r1 == 0) goto L_0x0288
                com.samsung.vsf.recognition.SamsungRecognizer r1 = com.samsung.vsf.recognition.SamsungRecognizer.this     // Catch:{ all -> 0x02a7 }
                java.util.PriorityQueue r1 = r1.asrResponseQueue     // Catch:{ all -> 0x02a7 }
                boolean r1 = r1.isEmpty()     // Catch:{ all -> 0x02a7 }
                if (r1 != 0) goto L_0x0288
                boolean r1 = r14.checkForNextElement()     // Catch:{ all -> 0x02a7 }
                if (r1 == 0) goto L_0x0288
                com.samsung.vsf.recognition.SamsungRecognizer r1 = com.samsung.vsf.recognition.SamsungRecognizer.this     // Catch:{ all -> 0x02a7 }
                java.util.PriorityQueue r1 = r1.asrResponseQueue     // Catch:{ all -> 0x02a7 }
                java.lang.Object r1 = r1.poll()     // Catch:{ all -> 0x02a7 }
                com.samsung.vsf.recognition.QueueItems r1 = (com.samsung.vsf.recognition.QueueItems) r1     // Catch:{ all -> 0x02a7 }
                if (r1 == 0) goto L_0x027f
                java.lang.String r2 = "tickcount"
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x02a7 }
                r3.<init>()     // Catch:{ all -> 0x02a7 }
                java.lang.String r4 = "In ASRResultThread SamsungRecognizer@"
                r3.append(r4)     // Catch:{ all -> 0x02a7 }
                int r4 = r1.getPriority()     // Catch:{ all -> 0x02a7 }
                r3.append(r4)     // Catch:{ all -> 0x02a7 }
                java.lang.String r4 = " metadata:-"
                r3.append(r4)     // Catch:{ all -> 0x02a7 }
                java.util.Properties r4 = r1.getASRResult()     // Catch:{ all -> 0x02a7 }
                java.lang.String r5 = "metadata"
                java.lang.String r4 = r4.getProperty(r5)     // Catch:{ all -> 0x02a7 }
                r3.append(r4)     // Catch:{ all -> 0x02a7 }
                java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x02a7 }
                com.samsung.vsf.util.SVoiceLog.verbose(r2, r3)     // Catch:{ all -> 0x02a7 }
                int r2 = r1.getPriority()     // Catch:{ all -> 0x02a7 }
                r14.currentId = r2     // Catch:{ all -> 0x02a7 }
                java.util.Properties r2 = r1.getASRResult()     // Catch:{ all -> 0x02a7 }
                boolean r2 = r2.isEmpty()     // Catch:{ all -> 0x02a7 }
                r3 = 2
                if (r2 != 0) goto L_0x026b
                java.util.Properties r2 = r1.getASRResult()     // Catch:{ all -> 0x02a7 }
                java.lang.String r4 = "startSequenceNumber"
                java.lang.String r2 = r2.getProperty(r4)     // Catch:{ all -> 0x02a7 }
                int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ all -> 0x02a7 }
                java.util.Properties r4 = r1.getASRResult()     // Catch:{ all -> 0x02a7 }
                java.lang.String r5 = "startSequenceNumber"
                r4.remove(r5)     // Catch:{ all -> 0x02a7 }
                java.lang.String r4 = "tickcount"
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x02a7 }
                r5.<init>()     // Catch:{ all -> 0x02a7 }
                java.lang.String r6 = "Start Seq number associated with the instance for which ASRresult is received : "
                r5.append(r6)     // Catch:{ all -> 0x02a7 }
                r5.append(r2)     // Catch:{ all -> 0x02a7 }
                java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x02a7 }
                com.samsung.vsf.util.SVoiceLog.info(r4, r5)     // Catch:{ all -> 0x02a7 }
                java.util.Properties r4 = r1.getASRResult()     // Catch:{ all -> 0x02a7 }
                java.lang.String r5 = "islast"
                java.lang.String r4 = r4.getProperty(r5)     // Catch:{ all -> 0x02a7 }
                java.lang.String r5 = "true"
                boolean r5 = r5.equalsIgnoreCase(r4)     // Catch:{ all -> 0x02a7 }
                r6 = 0
                if (r5 == 0) goto L_0x01ab
                org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ JSONException -> 0x01a3 }
                java.util.Properties r7 = r1.getASRResult()     // Catch:{ JSONException -> 0x01a3 }
                java.lang.String r8 = "metadata"
                java.lang.String r7 = r7.getProperty(r8)     // Catch:{ JSONException -> 0x01a3 }
                r5.<init>(r7)     // Catch:{ JSONException -> 0x01a3 }
                java.lang.String r7 = "nbest"
                org.json.JSONArray r7 = r5.optJSONArray(r7)     // Catch:{ JSONException -> 0x01a3 }
                java.lang.String r8 = "tickcount"
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x01a3 }
                r9.<init>()     // Catch:{ JSONException -> 0x01a3 }
                java.lang.String r10 = "In ASRResultThread SamsungRecognizer@"
                r9.append(r10)     // Catch:{ JSONException -> 0x01a3 }
                int r10 = r1.getPriority()     // Catch:{ JSONException -> 0x01a3 }
                r9.append(r10)     // Catch:{ JSONException -> 0x01a3 }
                java.lang.String r10 = " nbest:-"
                r9.append(r10)     // Catch:{ JSONException -> 0x01a3 }
                org.json.JSONObject r10 = r7.optJSONObject(r6)     // Catch:{ JSONException -> 0x01a3 }
                java.lang.String r10 = r10.toString()     // Catch:{ JSONException -> 0x01a3 }
                r9.append(r10)     // Catch:{ JSONException -> 0x01a3 }
                java.lang.String r9 = r9.toString()     // Catch:{ JSONException -> 0x01a3 }
                com.samsung.vsf.util.SVoiceLog.verbose(r8, r9)     // Catch:{ JSONException -> 0x01a3 }
                org.json.JSONObject r8 = r7.optJSONObject(r6)     // Catch:{ JSONException -> 0x01a3 }
                java.lang.String r9 = "numFrames"
                int r8 = r8.optInt(r9)     // Catch:{ JSONException -> 0x01a3 }
                java.lang.String r9 = "metrics"
                org.json.JSONObject r9 = r5.optJSONObject(r9)     // Catch:{ JSONException -> 0x01a3 }
                if (r9 != 0) goto L_0x012f
                java.lang.String r9 = "frameCount"
                int r5 = r5.optInt(r9)     // Catch:{ JSONException -> 0x01a3 }
                goto L_0x013b
            L_0x012f:
                java.lang.String r9 = "metrics"
                org.json.JSONObject r5 = r5.optJSONObject(r9)     // Catch:{ JSONException -> 0x01a3 }
                java.lang.String r9 = "frameCount"
                int r5 = r5.optInt(r9)     // Catch:{ JSONException -> 0x01a3 }
            L_0x013b:
                org.json.JSONObject r9 = r7.optJSONObject(r6)     // Catch:{ JSONException -> 0x01a1 }
                java.lang.String r10 = "numSpeechFrames"
                int r9 = r9.optInt(r10)     // Catch:{ JSONException -> 0x01a1 }
                org.json.JSONObject r10 = r7.optJSONObject(r6)     // Catch:{ JSONException -> 0x01a1 }
                java.lang.String r11 = "firstSpeechFrame"
                int r10 = r10.optInt(r11)     // Catch:{ JSONException -> 0x01a1 }
                org.json.JSONObject r6 = r7.optJSONObject(r6)     // Catch:{ JSONException -> 0x01a1 }
                java.lang.String r7 = "lastSpeechFrame"
                int r6 = r6.optInt(r7)     // Catch:{ JSONException -> 0x01a1 }
                java.lang.String r7 = "tickcount"
                java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x01a1 }
                r11.<init>()     // Catch:{ JSONException -> 0x01a1 }
                java.lang.String r12 = "In ASRResultThread SamsungRecognizer@"
                r11.append(r12)     // Catch:{ JSONException -> 0x01a1 }
                int r12 = r1.getPriority()     // Catch:{ JSONException -> 0x01a1 }
                r11.append(r12)     // Catch:{ JSONException -> 0x01a1 }
                java.lang.String r12 = " numFrames: "
                r11.append(r12)     // Catch:{ JSONException -> 0x01a1 }
                r11.append(r8)     // Catch:{ JSONException -> 0x01a1 }
                java.lang.String r8 = " frameCount: "
                r11.append(r8)     // Catch:{ JSONException -> 0x01a1 }
                r11.append(r5)     // Catch:{ JSONException -> 0x01a1 }
                java.lang.String r8 = ", numSpeechFrames: "
                r11.append(r8)     // Catch:{ JSONException -> 0x01a1 }
                r11.append(r9)     // Catch:{ JSONException -> 0x01a1 }
                java.lang.String r8 = ", "
                r11.append(r8)     // Catch:{ JSONException -> 0x01a1 }
                java.lang.String r8 = "firstSpeechFrame: "
                r11.append(r8)     // Catch:{ JSONException -> 0x01a1 }
                r11.append(r10)     // Catch:{ JSONException -> 0x01a1 }
                java.lang.String r8 = ", lastSpeechFrame: "
                r11.append(r8)     // Catch:{ JSONException -> 0x01a1 }
                r11.append(r6)     // Catch:{ JSONException -> 0x01a1 }
                java.lang.String r6 = r11.toString()     // Catch:{ JSONException -> 0x01a1 }
                com.samsung.vsf.util.SVoiceLog.info(r7, r6)     // Catch:{ JSONException -> 0x01a1 }
                goto L_0x01ac
            L_0x01a1:
                r6 = move-exception
                goto L_0x01a7
            L_0x01a3:
                r5 = move-exception
                r13 = r6
                r6 = r5
                r5 = r13
            L_0x01a7:
                r6.printStackTrace()     // Catch:{ all -> 0x02a7 }
                goto L_0x01ac
            L_0x01ab:
                r5 = r6
            L_0x01ac:
                java.util.Properties r6 = r1.getASRResult()     // Catch:{ all -> 0x02a7 }
                java.lang.String r7 = "itn"
                java.lang.String r6 = r6.getProperty(r7)     // Catch:{ all -> 0x02a7 }
                boolean r6 = r6.isEmpty()     // Catch:{ all -> 0x02a7 }
                if (r6 != 0) goto L_0x01cd
                com.samsung.vsf.recognition.SamsungRecognizer r5 = com.samsung.vsf.recognition.SamsungRecognizer.this     // Catch:{ all -> 0x02a7 }
                java.util.Properties r6 = r1.getASRResult()     // Catch:{ all -> 0x02a7 }
                java.lang.String r7 = "itn"
                java.lang.String r6 = r6.getProperty(r7)     // Catch:{ all -> 0x02a7 }
                java.lang.String r2 = r5.replaceITNValues(r6, r2)     // Catch:{ all -> 0x02a7 }
                goto L_0x01e4
            L_0x01cd:
                com.samsung.vsf.recognition.SamsungRecognizer r6 = com.samsung.vsf.recognition.SamsungRecognizer.this     // Catch:{ all -> 0x02a7 }
                java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x02a7 }
                r7.<init>()     // Catch:{ all -> 0x02a7 }
                java.lang.String r8 = "0_to_"
                r7.append(r8)     // Catch:{ all -> 0x02a7 }
                r7.append(r5)     // Catch:{ all -> 0x02a7 }
                java.lang.String r5 = r7.toString()     // Catch:{ all -> 0x02a7 }
                java.lang.String r2 = r6.replaceITNValues(r5, r2)     // Catch:{ all -> 0x02a7 }
            L_0x01e4:
                java.lang.String r5 = "tickcount"
                java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x02a7 }
                r6.<init>()     // Catch:{ all -> 0x02a7 }
                java.lang.String r7 = "In ASRResultThread SamsungRecognizer@"
                r6.append(r7)     // Catch:{ all -> 0x02a7 }
                int r7 = r1.getPriority()     // Catch:{ all -> 0x02a7 }
                r6.append(r7)     // Catch:{ all -> 0x02a7 }
                java.lang.String r7 = " ITN value after processing: "
                r6.append(r7)     // Catch:{ all -> 0x02a7 }
                r6.append(r2)     // Catch:{ all -> 0x02a7 }
                java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x02a7 }
                com.samsung.vsf.util.SVoiceLog.info(r5, r6)     // Catch:{ all -> 0x02a7 }
                java.util.Properties r5 = r1.getASRResult()     // Catch:{ all -> 0x02a7 }
                java.lang.String r6 = "itn"
                r5.put(r6, r2)     // Catch:{ all -> 0x02a7 }
                java.lang.String r2 = "true"
                boolean r2 = r2.equalsIgnoreCase(r4)     // Catch:{ all -> 0x02a7 }
                if (r2 == 0) goto L_0x023c
                com.samsung.vsf.recognition.SamsungRecognizer r2 = com.samsung.vsf.recognition.SamsungRecognizer.this     // Catch:{ all -> 0x02a7 }
                com.samsung.vsf.recognition.SamsungRecognizer$RecState r2 = r2.getRecState()     // Catch:{ all -> 0x02a7 }
                com.samsung.vsf.recognition.SamsungRecognizer$RecState r5 = com.samsung.vsf.recognition.SamsungRecognizer.RecState.END     // Catch:{ all -> 0x02a7 }
                if (r2 == r5) goto L_0x022a
                com.samsung.vsf.recognition.SamsungRecognizer r2 = com.samsung.vsf.recognition.SamsungRecognizer.this     // Catch:{ all -> 0x02a7 }
                java.util.Properties r1 = r1.getASRResult()     // Catch:{ all -> 0x02a7 }
                r2.notifyResult(r1)     // Catch:{ all -> 0x02a7 }
            L_0x022a:
                com.samsung.vsf.recognition.SamsungRecognizer r1 = com.samsung.vsf.recognition.SamsungRecognizer.this     // Catch:{ all -> 0x02a7 }
                com.samsung.vsf.recognition.SamsungRecognizer$RecState r1 = r1.getRecState()     // Catch:{ all -> 0x02a7 }
                com.samsung.vsf.recognition.SamsungRecognizer$RecState r2 = com.samsung.vsf.recognition.SamsungRecognizer.RecState.LAST     // Catch:{ all -> 0x02a7 }
                if (r1 != r2) goto L_0x024f
                com.samsung.vsf.recognition.SamsungRecognizer r1 = com.samsung.vsf.recognition.SamsungRecognizer.this     // Catch:{ all -> 0x02a7 }
                com.samsung.vsf.recognition.SamsungRecognizer$RecState r2 = com.samsung.vsf.recognition.SamsungRecognizer.RecState.END     // Catch:{ all -> 0x02a7 }
                r1.setRecState(r2)     // Catch:{ all -> 0x02a7 }
                goto L_0x024f
            L_0x023c:
                com.samsung.vsf.recognition.SamsungRecognizer r2 = com.samsung.vsf.recognition.SamsungRecognizer.this     // Catch:{ all -> 0x02a7 }
                com.samsung.vsf.recognition.SamsungRecognizer$RecState r2 = r2.getRecState()     // Catch:{ all -> 0x02a7 }
                com.samsung.vsf.recognition.SamsungRecognizer$RecState r5 = com.samsung.vsf.recognition.SamsungRecognizer.RecState.END     // Catch:{ all -> 0x02a7 }
                if (r2 == r5) goto L_0x024f
                com.samsung.vsf.recognition.SamsungRecognizer r2 = com.samsung.vsf.recognition.SamsungRecognizer.this     // Catch:{ all -> 0x02a7 }
                java.util.Properties r1 = r1.getASRResult()     // Catch:{ all -> 0x02a7 }
                r2.notifyPartialResult(r1)     // Catch:{ all -> 0x02a7 }
            L_0x024f:
                java.lang.String r1 = "true"
                boolean r1 = r1.equalsIgnoreCase(r4)     // Catch:{ all -> 0x02a7 }
                if (r1 == 0) goto L_0x0036
                com.samsung.vsf.recognition.SamsungRecognizer r1 = com.samsung.vsf.recognition.SamsungRecognizer.this     // Catch:{ all -> 0x02a7 }
                com.samsung.vsf.SpeechRecognizer$Config r1 = r1.getConfig()     // Catch:{ all -> 0x02a7 }
                int r1 = r1.getSessionMode()     // Catch:{ all -> 0x02a7 }
                if (r1 != r3) goto L_0x0036
                int r1 = r14.currentId     // Catch:{ all -> 0x02a7 }
                int r1 = r1 + 1
                r14.currentId = r1     // Catch:{ all -> 0x02a7 }
                goto L_0x0036
            L_0x026b:
                com.samsung.vsf.recognition.SamsungRecognizer r1 = com.samsung.vsf.recognition.SamsungRecognizer.this     // Catch:{ all -> 0x02a7 }
                com.samsung.vsf.SpeechRecognizer$Config r1 = r1.getConfig()     // Catch:{ all -> 0x02a7 }
                int r1 = r1.getSessionMode()     // Catch:{ all -> 0x02a7 }
                if (r1 != r3) goto L_0x0036
                int r1 = r14.currentId     // Catch:{ all -> 0x02a7 }
                int r1 = r1 + 1
                r14.currentId = r1     // Catch:{ all -> 0x02a7 }
                goto L_0x0036
            L_0x027f:
                java.lang.String r1 = "tickcount"
                java.lang.String r2 = "ASRResponseQueue is empty"
                com.samsung.vsf.util.SVoiceLog.info(r1, r2)     // Catch:{ all -> 0x02a7 }
                goto L_0x0036
            L_0x0288:
                boolean r1 = r14.isRunning     // Catch:{ all -> 0x02a7 }
                if (r1 == 0) goto L_0x02a4
                java.lang.Object r1 = r14.recognizerLockObject     // Catch:{ InterruptedException -> 0x0298 }
                if (r1 == 0) goto L_0x02a4
                java.lang.Object r1 = r14.recognizerLockObject     // Catch:{ InterruptedException -> 0x0298 }
                r2 = 1000(0x3e8, double:4.94E-321)
                r1.wait(r2)     // Catch:{ InterruptedException -> 0x0298 }
                goto L_0x02a4
            L_0x0298:
                r1 = move-exception
                r1.printStackTrace()     // Catch:{ all -> 0x02a7 }
                goto L_0x02a4
            L_0x029d:
                java.lang.String r1 = "tickcount"
                java.lang.String r2 = "ASRResponseQueue is null"
                com.samsung.vsf.util.SVoiceLog.info(r1, r2)     // Catch:{ all -> 0x02a7 }
            L_0x02a4:
                monitor-exit(r0)     // Catch:{ all -> 0x02a7 }
                goto L_0x0003
            L_0x02a7:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x02a7 }
                throw r1
            L_0x02aa:
                java.lang.String r0 = "tickcount"
                java.lang.String r1 = "Lock object is NULL. Exit ASRResultThread"
                com.samsung.vsf.util.SVoiceLog.debug(r0, r1)
                goto L_0x0003
            L_0x02b3:
                java.lang.String r0 = "tickcount"
                java.lang.String r1 = "Exiting ASRResultThread"
                com.samsung.vsf.util.SVoiceLog.info(r0, r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.samsung.vsf.recognition.SamsungRecognizer.ASRResultThread.run():void");
        }

        private boolean checkForNextElement() {
            QueueItems queueItems = (QueueItems) SamsungRecognizer.this.asrResponseQueue.peek();
            if (queueItems != null) {
                SVoiceLog.info("tickcount", "In ASRResultThread Item's Priority :" + queueItems.getPriority());
                if (this.currentId == queueItems.getPriority()) {
                    return true;
                }
            }
            if (this.currentId == -1) {
                return true;
            }
            return false;
        }

        /* access modifiers changed from: package-private */
        public void addResponseToqueue(int i, Properties properties) {
            if (!this.isRunning || i < this.currentId) {
                SVoiceLog.debug("tickcount", "received id : " + i + " currentId : " + this.currentId);
                StringBuilder sb = new StringBuilder();
                sb.append("reject the response for ");
                sb.append(i);
                SVoiceLog.debug("tickcount", sb.toString());
                return;
            }
            SamsungRecognizer.this.asrResponseQueue.add(new QueueItems(i, properties));
            notifyRecognizerObject();
        }

        /* access modifiers changed from: package-private */
        public void stopASRResultThread() {
            this.isRunning = false;
            notifyRecognizerObject();
            this.currentId = 0;
            if (SamsungRecognizer.this.asrResponseQueue != null) {
                SamsungRecognizer.this.asrResponseQueue.clear();
                PriorityQueue unused = SamsungRecognizer.this.asrResponseQueue = null;
            }
            this.recognizerLockObject = null;
        }

        /* access modifiers changed from: package-private */
        public void notifyRecognizerObject() {
            Object obj = this.recognizerLockObject;
            if (obj != null) {
                synchronized (obj) {
                    this.recognizerLockObject.notify();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public String replaceITNValues(String str, int i) {
        SVoiceLog.info("tickcount", "replace ITN VALUES:: " + str + ", " + " seqNumber :" + i);
        StringBuilder sb = new StringBuilder();
        ArrayList<Integer> number = getNumber(str);
        for (int i2 = 0; i2 < number.size(); i2++) {
            sb.append(String.valueOf(number.get(i2).intValue() + ((i - 1) * 10)));
            if (i2 % 2 == 0) {
                sb.append("_to_");
            } else if (i2 != number.size() - 1) {
                sb.append(" ");
            }
        }
        number.clear();
        return sb.toString();
    }

    private ArrayList<Integer> getNumber(String str) {
        Matcher matcher = Pattern.compile("\\d+").matcher(str);
        ArrayList<Integer> arrayList = new ArrayList<>();
        while (matcher.find()) {
            arrayList.add(Integer.valueOf(Integer.parseInt(matcher.group())));
        }
        return arrayList;
    }
}
