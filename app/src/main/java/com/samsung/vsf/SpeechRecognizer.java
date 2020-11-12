package com.samsung.vsf;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.samsung.vsf.recognition.BufferObject;
import com.samsung.vsf.recognition.Recognizer;
import com.samsung.vsf.recognition.RecognizerConstants;
import com.samsung.vsf.recognition.SamsungRecognizer;
import com.samsung.vsf.recognition.cmds.CancelCmd;
import com.samsung.vsf.recognition.cmds.CreateCmd;
import com.samsung.vsf.recognition.cmds.DestroyCmd;
import com.samsung.vsf.recognition.cmds.StartCmd;
import com.samsung.vsf.recognition.cmds.StopCmd;
import com.samsung.vsf.util.DeviceInfo;
import com.samsung.vsf.util.SVoiceLog;
import com.sec.android.app.voicenote.service.Recorder;
import java.util.Properties;
import java.util.concurrent.Executors;

public final class SpeechRecognizer {
    private Config mConfig;
    private Context mContext;
    private Looper mRecognitionLooper;
    private Recognizer mRecognizer;
    private InternalResponseHandler mResponseHandler;
    private HandlerThread mThread = new HandlerThread("Recognizer Thread");

    public static class Config {
        private Bundle config = new Bundle();

        public boolean getIsUsePLMRequired() {
            return false;
        }

        public Config setLocale(String str) {
            this.config.putString("locale", str);
            return this;
        }

        public Config setEncodingType(int i) {
            this.config.putInt("encodingType", i);
            return this;
        }

        public Config setServerDetails(String str, int i) {
            this.config.putString("serverAddress", str);
            this.config.putInt("portNumber", i);
            return this;
        }

        public Config setRPCTimeout(int i) {
            this.config.putInt("rpc_timeout", i);
            return this;
        }

        public String getASRDictationModel() {
            String string = this.config.getString("asrDictModels", (String) null);
            if (string != null) {
                return string;
            }
            this.config.putString("asrDictModels", "dash_dict");
            return "dash_dict";
        }

        public int getEncodingType() {
            return this.config.getInt("encodingType", 2);
        }

        public String getSDKClient() {
            return this.config.getString("clientType", RecognizerConstants.Client.VOICE_MEMO.name());
        }

        public boolean getIsByteOrderLittleEndian() {
            return this.config.getBoolean("isByteOrderLittleEndian", true);
        }

        public String getLocale() {
            return this.config.getString("locale", "en-US");
        }

        public int getSamplingRate() {
            return this.config.getInt("samplingRate", 16000);
        }

        public boolean getIsRecordingRequired() {
            return this.config.getBoolean("clientOwnsRecorder", false);
        }

        public boolean getIsRMSrequired() {
            return this.config.getBoolean("clientNeedsRMS", false);
        }

        public boolean getIsSpeechDetectionNotificationRequired() {
            return this.config.getBoolean("requireSpeechDetection", true);
        }

        public int getEPDThresholdDuration() {
            return this.config.getInt("epdDurationThreshHold", Recorder.CHECK_AVAIABLE_STORAGE);
        }

        public boolean getIsEnableNoiseSeparation() {
            return this.config.getBoolean("clientOwnsNoiseSeparation", true);
        }

        public boolean getIsSpeechDetectionRequired() {
            return this.config.getBoolean("clientOwnsSpeechDetector", true);
        }

        public boolean getIsPCMDumpRequired() {
            return this.config.getBoolean("pcmDumpNeeded", false);
        }

        @Deprecated
        public boolean getIsSPXDumpRequired() {
            return this.config.getBoolean("spxDumpNeeded", false);
        }

        public boolean getIsDumpRequired() {
            if (getEncodingType() != 2 || !getIsSPXDumpRequired()) {
                return this.config.getBoolean("dumpNeeded", false);
            }
            return true;
        }

        public String getServerIP() {
            String string = this.config.getString("serverAddress", (String) null);
            if (string == null) {
                if (DeviceInfo.isChineseDevice()) {
                    string = "voiceapi-cn.samsung-svoice.cn";
                } else {
                    String locale = getLocale();
                    if (locale.equalsIgnoreCase("de-DE") || locale.equalsIgnoreCase("it-IT") || locale.equalsIgnoreCase("es-ES") || locale.equalsIgnoreCase("pt-BR") || locale.equalsIgnoreCase("ru-RU") || locale.equalsIgnoreCase("en-GB") || locale.equalsIgnoreCase("en-US") || locale.equalsIgnoreCase("fr-FR") || locale.equalsIgnoreCase("es-US")) {
                        string = "voiceapi-us.samsung-svoice.com";
                    } else {
                        if (!locale.equalsIgnoreCase("ko-KR") && !locale.equalsIgnoreCase("ja-JP") && !locale.equalsIgnoreCase("zh-CN") && !locale.equalsIgnoreCase("zh-TW") && !locale.equalsIgnoreCase("zh-SG")) {
                            boolean equalsIgnoreCase = locale.equalsIgnoreCase("zh-HK");
                        }
                        string = "voiceapi-kr.samsung-svoice.com";
                    }
                }
                this.config.putString("serverAddress", string);
            }
            return string;
        }

        public int getPortNumber() {
            int i = this.config.getInt("portNumber", 0);
            if (i != 0) {
                return i;
            }
            if (!DeviceInfo.isChineseDevice()) {
                String locale = getLocale();
                if (!locale.equalsIgnoreCase("de-DE") && !locale.equalsIgnoreCase("it-IT") && !locale.equalsIgnoreCase("es-ES") && !locale.equalsIgnoreCase("pt-BR") && !locale.equalsIgnoreCase("ru-RU") && !locale.equalsIgnoreCase("en-GB") && !locale.equalsIgnoreCase("ko-KR") && !locale.equalsIgnoreCase("ja-JP") && !locale.equalsIgnoreCase("zh-CN") && !locale.equalsIgnoreCase("zh-TW") && !locale.equalsIgnoreCase("zh-SG") && !locale.equalsIgnoreCase("en-US") && !locale.equalsIgnoreCase("fr-FR") && !locale.equalsIgnoreCase("es-US")) {
                    boolean equalsIgnoreCase = locale.equalsIgnoreCase("zh-HK");
                }
            }
            this.config.putInt("portNumber", 443);
            return 443;
        }

        public boolean getIsTLSUsed() {
            return this.config.getBoolean("useTLS", RecognizerConstants.USE_TLS);
        }

        public String getCertificatePath() {
            String str = RecognizerConstants.CERT_PATH;
            if (DeviceInfo.isChineseDevice()) {
                str = "/system/etc/security/cacerts/00673b5b.0";
            }
            return this.config.getString("certPath", str);
        }

        public boolean getIsRecordedBufferNeeded() {
            return this.config.getBoolean("bufferNeeded", false);
        }

        public boolean getIsTOSOptionAccepted() {
            return this.config.getBoolean("tos_optional", false);
        }

        public int getRPCTimeoutValue() {
            return this.config.getInt("rpc_timeout", 60000);
        }

        public int getSessionMode() {
            return this.config.getInt("sessionMode", 2);
        }

        public boolean getIsCensored() {
            return this.config.getBoolean("censor", false);
        }
    }

    private SpeechRecognizer(Context context, Config config) {
        SVoiceLog.info("tickcount", "SamsungSpeechRecognizer : SpeechRecognizer()");
        this.mContext = context;
        this.mConfig = config;
        this.mThread.start();
        this.mRecognitionLooper = this.mThread.getLooper();
        this.mRecognizer = new SamsungRecognizer(context, this.mRecognitionLooper, config);
        this.mResponseHandler = new InternalResponseHandler();
        this.mRecognizer.setResponseHandler(this.mResponseHandler);
        this.mRecognizer.postCommand(new CreateCmd());
    }

    public static SpeechRecognizer createSpeechRecognizer(Context context, Config config) {
        SVoiceLog.info("tickcount", "SamsungSpeechRecognizer : createSpeechRecognizer");
        return new SpeechRecognizer(context, config);
    }

    public void setListener(RecognitionListener recognitionListener) {
        SVoiceLog.info("tickcount", "SamsungSpeechRecognizer : setListener()");
        checkIsCalledFromMainThread();
        RecognitionListener unused = this.mResponseHandler.client = recognitionListener;
    }

    public void startListening() {
        SVoiceLog.info("tickcount", "SamsungSpeechRecognizer : startListening()");
        System.out.println("TEST_PLATFORM: SPEAK_NOW");
        SVoiceLog.info("ASRProfiling", "startListening() " + System.currentTimeMillis());
        checkIsCalledFromMainThread();
        Recognizer recognizer = this.mRecognizer;
        if (recognizer != null) {
            recognizer.postCommand(new StartCmd());
        }
    }

    public void stopListening() {
        SVoiceLog.info("tickcount", "SamsungSpeechRecognizer : stopListening()");
        SVoiceLog.info("ASRProfiling", "stopListening() " + System.currentTimeMillis());
        checkIsCalledFromMainThread();
        Recognizer recognizer = this.mRecognizer;
        if (recognizer != null) {
            recognizer.postCommand(new StopCmd());
        }
    }

    public void cancelRecognition() {
        SVoiceLog.info("tickcount", "SamsungSpeechRecognizer : cancelRecognition");
        checkIsCalledFromMainThread();
        Recognizer recognizer = this.mRecognizer;
        if (recognizer != null) {
            CancelTask cancelTask = new CancelTask();
            cancelTask.setRecognizer(recognizer);
            cancelTask.executeOnExecutor(Executors.newSingleThreadExecutor(), null);
        }
    }

    private static class CancelTask extends AsyncTask<Recognizer, Void, Recognizer> {
        private Recognizer result;

        private CancelTask() {
        }

        public void setRecognizer(Recognizer recognizer) {
            this.result = recognizer;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Recognizer recognizer) {
            if (this.result != null) {
                SVoiceLog.info("tickcount", "SamsungSpeechRecognizer : CancelTask : posting cancel command");
                this.result.postCommand2(new CancelCmd());
            }
            this.result = null;
        }

        /* access modifiers changed from: protected */
        public Recognizer doInBackground(Recognizer... recognizerArr) {
            if (this.result != null) {
                SVoiceLog.info("tickcount", "SamsungSpeechRecognizer : CancelTask : calling abort from doInBackground");
                this.result.abort();
            }
            return this.result;
        }
    }

    public void sendAudio(byte[] bArr) {
        sendAudio(bArr, false);
    }

    public void sendAudio(byte[] bArr, boolean z) {
        SVoiceLog.debug("tickcount", "SendAudio - length is :" + bArr.length);
        BufferObject bufferObject = new BufferObject(bArr, z);
        Recognizer recognizer = this.mRecognizer;
        if (recognizer != null) {
            recognizer.queueBuffer(bufferObject);
        }
    }

    public void destroy() {
        SVoiceLog.info("tickcount", "SamsungSpeechRecognizer : destroy");
        checkIsCalledFromMainThread();
        Recognizer recognizer = this.mRecognizer;
        if (recognizer != null) {
            recognizer.postCommand2(new DestroyCmd());
            this.mRecognizer = null;
        }
    }

    /* access modifiers changed from: private */
    public void destroyThread() {
        SVoiceLog.info("tickcount", "SamsungSpeechRecognizer : onDestroy");
        Looper looper = this.mRecognitionLooper;
        if (looper != null) {
            looper.quitSafely();
            this.mRecognitionLooper = null;
        }
        InternalResponseHandler internalResponseHandler = this.mResponseHandler;
        if (internalResponseHandler != null) {
            RecognitionListener unused = internalResponseHandler.client = null;
            Handler unused2 = this.mResponseHandler.mInternalHandler = null;
            this.mResponseHandler = null;
        }
        this.mThread.quitSafely();
        this.mThread = null;
        this.mConfig = null;
    }

    private static void checkIsCalledFromMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new RuntimeException("SpeechRecognizer should be used only from the application's main thread");
        }
    }

    private class InternalResponseHandler implements Recognizer.ResponseHandler {
        private final int ERROR_DELAY;
        /* access modifiers changed from: private */
        public RecognitionListener client;
        /* access modifiers changed from: private */
        public Handler mInternalHandler;

        private InternalResponseHandler() {
            this.ERROR_DELAY = 3000;
            this.mInternalHandler = new Handler() {
                public void handleMessage(Message message) {
                    if (InternalResponseHandler.this.client != null) {
                        switch (message.what) {
                            case 1:
                                InternalResponseHandler.this.client.onBeginningOfSpeech();
                                return;
                            case 2:
                                InternalResponseHandler.this.client.onEndOfSpeech();
                                return;
                            case 3:
                                InternalResponseHandler.this.client.onPartialResults((Properties) message.obj);
                                return;
                            case 4:
                                InternalResponseHandler.this.client.onResults((Properties) message.obj);
                                return;
                            case 5:
                                InternalResponseHandler.this.client.onError((String) message.obj);
                                return;
                            case 6:
                                InternalResponseHandler.this.client.onRmsChanged((float) ((Integer) message.obj).intValue());
                                return;
                            case 7:
                                InternalResponseHandler.this.client.onErrorString((String) message.obj);
                                InternalResponseHandler.this.client.onError((String) message.obj);
                                return;
                            case 8:
                                InternalResponseHandler.this.client.onReadyForSpeech((Bundle) message.obj);
                                return;
                            case 9:
                                SpeechRecognizer.this.destroyThread();
                                return;
                            default:
                                return;
                        }
                    }
                }
            };
        }

        public void onSpeechStarted() {
            Handler handler = this.mInternalHandler;
            if (handler != null) {
                Message.obtain(handler, 1).sendToTarget();
            }
        }

        public void onSpeechEnded() {
            Handler handler = this.mInternalHandler;
            if (handler != null) {
                Message.obtain(handler, 2).sendToTarget();
            }
        }

        public void onPartialResult(Properties properties) {
            Handler handler = this.mInternalHandler;
            if (handler != null) {
                Message.obtain(handler, 3, properties).sendToTarget();
            }
        }

        public void onResult(Properties properties) {
            Handler handler = this.mInternalHandler;
            if (handler != null) {
                Message.obtain(handler, 4, properties).sendToTarget();
            }
        }

        public void onError(String str) {
            Handler handler = this.mInternalHandler;
            if (handler != null && !handler.hasMessages(10)) {
                Log.d("RAVISH", "sendError");
                this.mInternalHandler.sendEmptyMessageDelayed(10, 3000);
                Message.obtain(this.mInternalHandler, 5, str).sendToTarget();
            }
        }

        public void onRMSresult(int i) {
            Handler handler = this.mInternalHandler;
            if (handler != null) {
                Message.obtain(handler, 6, Integer.valueOf(i)).sendToTarget();
            }
        }

        public void onErrorString(String str) {
            Handler handler = this.mInternalHandler;
            if (handler != null) {
                Message.obtain(handler, 7, str).sendToTarget();
            }
        }

        public void onBufferReceived(short[] sArr) {
            RecognitionListener recognitionListener = this.client;
            if (recognitionListener != null) {
                recognitionListener.onBufferReceived(sArr);
            }
        }

        public void onDestroy() {
            Handler handler = this.mInternalHandler;
            if (handler != null) {
                handler.removeMessages(10);
                Message.obtain(this.mInternalHandler, 9).sendToTarget();
            }
        }
    }
}
