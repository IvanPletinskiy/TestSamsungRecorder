package com.sec.android.app.voicenote.service.recognizer;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.samsung.vsf.RecognitionListener;
import com.samsung.vsf.SpeechRecognizer;
import com.sec.android.app.voicenote.common.util.TextData;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.MetadataRepository;
import com.sec.android.app.voicenote.service.SimpleEngineManager;
import com.sec.android.app.voicenote.service.SimpleMetadataRepository;
import com.sec.android.app.voicenote.service.SimpleMetadataRepositoryManager;
import com.sec.android.app.voicenote.service.recognizer.VoiceWorker;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Pattern;

public class Recognizer {
    private static final int MSG_RECOGNIZER_RETRY = 1000;
    private static final int MSG_RECOGNIZER_RETRY_TIME = 5000;
    private static final int MSG_SILENCE_MONITOR = 2000;
    private static final int MSG_SILENCE_MONITOR_TIME = 500;
    private static final int RPC_TIMEOUT = 180000;
    private static final String TAG = "Recognizer";
    /* access modifiers changed from: private */
    public boolean mFirstRecognition = false;
    /* access modifiers changed from: private */
    public boolean mIsLastWord = false;
    private long mLastTime = 0;
    /* access modifiers changed from: private */
    public VoiceWorker.StatusChangedListener mListener = null;
    /* access modifiers changed from: private */
    public final Handler mOnErrorHandler = new Handler(new Handler.Callback() {
        public boolean handleMessage(Message message) {
            if (message.what != 1000 || Recognizer.this.mSpeechRecognizer == null) {
                return false;
            }
            Recognizer.this.mSpeechRecognizer.startListening();
            return false;
        }
    });
    /* access modifiers changed from: private */
    public ArrayList<TextData> mPartialSttData = null;
    private int mRecognitionStartTime = 0;
    /* access modifiers changed from: private */
    public final Handler mSilenceMonitorHandler = new Handler(new Handler.Callback() {
        public boolean handleMessage(Message message) {
            if (Engine.getInstance().getRecorderState() != 2) {
                Log.m26i(Recognizer.TAG, "MSG_SILENCE_MONITOR ignore");
                return false;
            }
            if (message.what == 2000) {
                int currentTime = Engine.getInstance().getCurrentTime();
                Log.m26i(Recognizer.TAG, "MSG_SILENCE_MONITOR silence start = " + Recognizer.this.mSilenceStartTime + " end = " + currentTime);
                if (SimpleEngineManager.getInstance().getActiveEngine() != null) {
                    SimpleMetadataRepository metadataRepository = SimpleMetadataRepositoryManager.getInstance().getMetadataRepository(VoiceNoteApplication.getSimpleActivitySession());
                    if (Recognizer.this.mListener != null && metadataRepository.addSilenceSttData(Recognizer.this.mSilenceStartTime, currentTime)) {
                        Recognizer.this.mListener.onResultWord((ArrayList<TextData>) null);
                    }
                } else {
                    MetadataRepository instance = MetadataRepository.getInstance();
                    if (Recognizer.this.mListener != null && instance.addSilenceSttData(Recognizer.this.mSilenceStartTime, currentTime)) {
                        Recognizer.this.mListener.onResultWord((ArrayList<TextData>) null);
                    }
                }
                int unused = Recognizer.this.mSilenceStartTime = Engine.getInstance().getCurrentTime();
                Recognizer.this.mSilenceMonitorHandler.sendEmptyMessageDelayed(2000, 500);
            }
            return false;
        }
    });
    /* access modifiers changed from: private */
    public int mSilenceStartTime = 0;
    /* access modifiers changed from: private */
    public SpeechRecognizer mSpeechRecognizer;

    /* access modifiers changed from: package-private */
    public void startRecognition(Context context) {
        String stringSettings = Settings.getStringSettings(Settings.KEY_STT_LANGUAGE_LOCALE);
        SpeechRecognizer.Config config = new SpeechRecognizer.Config();
        config.setLocale(stringSettings);
        config.setEncodingType(1);
        config.setRPCTimeout(RPC_TIMEOUT);
        this.mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context, config);
        if (config.getServerIP().contains("qa")) {
            Log.m26i(TAG, "connecting to QA");
        } else {
            Log.m26i(TAG, "connecting to Commercialization");
        }
        this.mFirstRecognition = true;
        this.mRecognitionStartTime = Engine.getInstance().getCurrentTime();
        int overwriteStartTime = Engine.getInstance().getOverwriteStartTime();
        if (this.mRecognitionStartTime == 0 && overwriteStartTime != -1) {
            this.mRecognitionStartTime = overwriteStartTime;
        }
        Log.m26i(TAG, "startRecognition : mRecognitionStartTime = " + this.mRecognitionStartTime);
        this.mSpeechRecognizer.setListener(new RecognitionListener() {
            public void onBufferReceived(short[] sArr) {
            }

            public void onReadyForSpeech(Bundle bundle) {
            }

            public void onRmsChanged(float f) {
            }

            public void onBeginningOfSpeech() {
                Log.m26i(Recognizer.TAG, "onBeginningOfSpeech ");
                if (Recognizer.this.mListener != null) {
                    Recognizer.this.mListener.onRecognitionStart();
                }
            }

            public void onPartialResults(Properties properties) {
                String replaceAll = Pattern.compile("\\{[^}]*\\} ").matcher(properties.getProperty("utterance")).replaceAll("");
                String property = properties.getProperty("itn");
                Log.m19d(Recognizer.TAG, "onPartialResults : " + replaceAll);
                Log.m26i(Recognizer.TAG, "onPartialResults time : " + property);
                if (replaceAll != null && !replaceAll.isEmpty() && property != null && !property.isEmpty() && Recognizer.this.mListener != null) {
                    if (Recognizer.this.isOverWriteMode() && Recognizer.this.mSilenceMonitorHandler.hasMessages(2000)) {
                        Recognizer.this.mSilenceMonitorHandler.removeMessages(2000);
                        int unused = Recognizer.this.mSilenceStartTime = Engine.getInstance().getCurrentTime();
                        Recognizer.this.mSilenceMonitorHandler.sendEmptyMessageDelayed(2000, 500);
                    }
                    Recognizer recognizer = Recognizer.this;
                    ArrayList unused2 = recognizer.mPartialSttData = recognizer.getWord(replaceAll, property);
                    Recognizer.this.mListener.onPartialResultWord(Recognizer.this.mPartialSttData);
                }
            }

            public void onResults(Properties properties) {
                ArrayList arrayList;
                String replaceAll = Pattern.compile("\\{[^}]*\\} ").matcher(properties.getProperty("utterance")).replaceAll("");
                String property = properties.getProperty("itn");
                Log.m19d(Recognizer.TAG, "onResults : " + replaceAll);
                Log.m26i(Recognizer.TAG, "onResults time : " + property);
                if (Recognizer.this.mListener != null) {
                    if (Recognizer.this.isOverWriteMode() && Recognizer.this.mSilenceMonitorHandler.hasMessages(2000)) {
                        Recognizer.this.mSilenceMonitorHandler.removeMessages(2000);
                        int unused = Recognizer.this.mSilenceStartTime = Engine.getInstance().getCurrentTime();
                        Recognizer.this.mSilenceMonitorHandler.sendEmptyMessageDelayed(2000, 500);
                    }
                    if (replaceAll.length() < 1) {
                        arrayList = Recognizer.this.getEmptyWord();
                    } else {
                        arrayList = Recognizer.this.getWord(replaceAll, property);
                    }
                    if (SimpleEngineManager.getInstance().getActiveEngine() != null) {
                        SimpleMetadataRepositoryManager.getInstance().getMetadataRepository(VoiceNoteApplication.getSimpleActivitySession()).addSttData(arrayList);
                    } else {
                        MetadataRepository.getInstance().addSttData(arrayList);
                    }
                    Recognizer.this.mListener.onResultWord(arrayList);
                    ArrayList unused2 = Recognizer.this.mPartialSttData = null;
                }
                boolean unused3 = Recognizer.this.mFirstRecognition = false;
            }

            public void onError(String str) {
                Log.m22e(Recognizer.TAG, "onError : " + str);
                if (Recognizer.this.mSpeechRecognizer != null) {
                    Recognizer.this.mSpeechRecognizer.stopListening();
                    Recognizer.this.mSpeechRecognizer.cancelRecognition();
                }
                if (!(Recognizer.this.mListener == null || Recognizer.this.mPartialSttData == null)) {
                    if (!Recognizer.this.mIsLastWord) {
                        if (SimpleEngineManager.getInstance().getActiveEngine() != null) {
                            SimpleMetadataRepositoryManager.getInstance().getMetadataRepository(VoiceNoteApplication.getSimpleActivitySession()).addSttData(Recognizer.this.mPartialSttData);
                        } else {
                            MetadataRepository.getInstance().addSttData(Recognizer.this.mPartialSttData);
                        }
                    }
                    Recognizer.this.mListener.onResultWord(Recognizer.this.mIsLastWord ? Recognizer.this.mPartialSttData : null);
                    ArrayList unused = Recognizer.this.mPartialSttData = null;
                    boolean unused2 = Recognizer.this.mFirstRecognition = false;
                }
                if (!Recognizer.this.mOnErrorHandler.hasMessages(1000)) {
                    Recognizer.this.mOnErrorHandler.sendEmptyMessageDelayed(1000, 5000);
                    if (Recognizer.this.mListener != null) {
                        Recognizer.this.mListener.onError(str);
                    }
                }
            }

            public void onErrorString(String str) {
                Log.m22e(Recognizer.TAG, "onErrorString : " + str);
            }

            public void onEndOfSpeech() {
                Log.m26i(Recognizer.TAG, "onEndOfSpeech ");
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void startRecording() {
        Log.m26i(TAG, "startRecording");
        SpeechRecognizer speechRecognizer = this.mSpeechRecognizer;
        if (speechRecognizer != null) {
            speechRecognizer.startListening();
        }
        this.mLastTime = (long) this.mRecognitionStartTime;
        if (isOverWriteMode() && !this.mSilenceMonitorHandler.hasMessages(2000)) {
            this.mSilenceStartTime = Engine.getInstance().getCurrentTime();
            this.mSilenceMonitorHandler.sendEmptyMessageDelayed(2000, 500);
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void stopRecording() {
        Log.m26i(TAG, "stopRecording");
        this.mIsLastWord = true;
        if (this.mSpeechRecognizer != null) {
            this.mSpeechRecognizer.stopListening();
        }
        removeHandlerMessage();
    }

    /* access modifiers changed from: package-private */
    public synchronized void cancelRecording() {
        Log.m26i(TAG, "cancelRecording");
        this.mIsLastWord = true;
        if (this.mSpeechRecognizer != null) {
            this.mSpeechRecognizer.stopListening();
            this.mSpeechRecognizer.setListener((RecognitionListener) null);
        }
        removeHandlerMessage();
    }

    /* access modifiers changed from: package-private */
    public void cancelRecognition() {
        Log.m26i(TAG, "cancelRecognition");
        SpeechRecognizer speechRecognizer = this.mSpeechRecognizer;
        if (speechRecognizer != null) {
            speechRecognizer.cancelRecognition();
        }
        removeHandlerMessage();
    }

    public void destroy() {
        Log.m26i(TAG, "destroy");
        SpeechRecognizer speechRecognizer = this.mSpeechRecognizer;
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        removeHandlerMessage();
    }

    public void registerListener(VoiceWorker.StatusChangedListener statusChangedListener) {
        Log.m26i(TAG, "registerListener : " + statusChangedListener);
        this.mListener = statusChangedListener;
    }

    /* access modifiers changed from: package-private */
    public void setLastRecognizer(boolean z) {
        Log.m26i(TAG, "setLastRecognizer : isLastWord = " + z);
        this.mIsLastWord = z;
        VoiceWorker.StatusChangedListener statusChangedListener = this.mListener;
        if (statusChangedListener != null) {
            statusChangedListener.onIsLastWord(z);
        }
    }

    /* access modifiers changed from: package-private */
    public void queueBuffer(byte[] bArr) {
        SpeechRecognizer speechRecognizer = this.mSpeechRecognizer;
        if (speechRecognizer != null) {
            speechRecognizer.sendAudio(bArr);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x004c A[Catch:{ Exception -> 0x0042 }] */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0076  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00a9  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00bb  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00ea  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.ArrayList<com.sec.android.app.voicenote.common.util.TextData> getWord(java.lang.String r26, java.lang.String r27) {
        /*
            r25 = this;
            r1 = r25
            r0 = r26
            r2 = r27
            if (r0 == 0) goto L_0x014b
            java.lang.String r3 = " "
            java.lang.String[] r4 = r0.split(r3)
            int r5 = r4.length
            if (r2 == 0) goto L_0x0017
            java.lang.String[] r0 = r2.split(r3)
            r2 = r0
            goto L_0x0018
        L_0x0017:
            r2 = 0
        L_0x0018:
            boolean r3 = r25.isOverWriteMode()
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            java.lang.String r0 = "stt_language_locale"
            java.lang.String r8 = com.sec.android.app.voicenote.provider.Settings.getStringSettings(r0)
            r9 = 0
            r10 = r9
        L_0x0029:
            if (r10 >= r5) goto L_0x0147
            java.lang.String r11 = "_to_"
            java.lang.String r12 = "getWord exception : "
            java.lang.String r13 = "Recognizer"
            r14 = 2
            r15 = 10
            r17 = 0
            if (r2 == 0) goto L_0x0046
            int r0 = r2.length     // Catch:{ Exception -> 0x0042 }
            if (r10 >= r0) goto L_0x0046
            r0 = r2[r10]     // Catch:{ Exception -> 0x0042 }
            java.lang.String[] r0 = r0.split(r11)     // Catch:{ Exception -> 0x0042 }
            goto L_0x0047
        L_0x0042:
            r0 = move-exception
            r19 = r17
            goto L_0x005f
        L_0x0046:
            r0 = 0
        L_0x0047:
            if (r0 == 0) goto L_0x0076
            int r6 = r0.length     // Catch:{ Exception -> 0x0042 }
            if (r6 != r14) goto L_0x0076
            r6 = r0[r9]     // Catch:{ Exception -> 0x0042 }
            long r19 = java.lang.Long.parseLong(r6)     // Catch:{ Exception -> 0x0042 }
            long r19 = r19 * r15
            r6 = 1
            r0 = r0[r6]     // Catch:{ Exception -> 0x005e }
            long r21 = java.lang.Long.parseLong(r0)     // Catch:{ Exception -> 0x005e }
            long r21 = r21 * r15
            goto L_0x007a
        L_0x005e:
            r0 = move-exception
        L_0x005f:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r12)
            r6.append(r0)
            java.lang.String r0 = r6.toString()
            com.sec.android.app.voicenote.provider.Log.m22e(r13, r0)
            r23 = r17
            r14 = r19
            goto L_0x007e
        L_0x0076:
            r19 = r17
            r21 = r19
        L_0x007a:
            r14 = r19
            r23 = r21
        L_0x007e:
            com.sec.android.app.voicenote.common.util.TextData r6 = new com.sec.android.app.voicenote.common.util.TextData
            r6.<init>()
            r6.dataType = r9
            if (r10 != 0) goto L_0x009f
            boolean r0 = r1.mFirstRecognition
            if (r0 == 0) goto L_0x009f
            if (r3 == 0) goto L_0x009f
            int r0 = r1.mRecognitionStartTime
            long r14 = (long) r0
            r6.timeStamp = r14
            long r14 = (long) r0
            r6.elapsedTime = r14
            r14 = r23
            r6.duration = r14
            r9 = r12
            r19 = r13
            r21 = r14
            goto L_0x00c6
        L_0x009f:
            r21 = r23
            int r0 = com.sec.android.app.voicenote.uicore.VoiceNoteApplication.getScene()
            r9 = 12
            if (r0 == r9) goto L_0x00bb
            int r0 = r1.mRecognitionStartTime
            r9 = r12
            r19 = r13
            long r12 = (long) r0
            long r12 = r12 + r14
            r6.timeStamp = r12
            long r12 = (long) r0
            long r12 = r12 + r14
            r6.elapsedTime = r12
            long r12 = r21 - r14
            r6.duration = r12
            goto L_0x00c6
        L_0x00bb:
            r9 = r12
            r19 = r13
            r6.timeStamp = r14
            r6.elapsedTime = r14
            long r12 = r21 - r14
            r6.duration = r12
        L_0x00c6:
            java.lang.String[] r0 = r6.mText
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r13 = r4[r10]
            r12.append(r13)
            r13 = 32
            r12.append(r13)
            java.lang.String r12 = r12.toString()
            r13 = 0
            r0[r13] = r12
            if (r8 == 0) goto L_0x0138
            java.lang.String r0 = "zh"
            boolean r0 = r8.contains(r0)
            if (r0 == 0) goto L_0x0138
            if (r2 == 0) goto L_0x00f8
            int r0 = r10 + 1
            int r12 = r2.length     // Catch:{ Exception -> 0x00f6 }
            if (r0 >= r12) goto L_0x00f8
            r0 = r2[r0]     // Catch:{ Exception -> 0x00f6 }
            java.lang.String[] r0 = r0.split(r11)     // Catch:{ Exception -> 0x00f6 }
            goto L_0x00f9
        L_0x00f6:
            r0 = move-exception
            goto L_0x010a
        L_0x00f8:
            r0 = 0
        L_0x00f9:
            if (r0 == 0) goto L_0x011e
            int r11 = r0.length     // Catch:{ Exception -> 0x00f6 }
            r12 = 2
            if (r11 != r12) goto L_0x011e
            r11 = 0
            r0 = r0[r11]     // Catch:{ Exception -> 0x00f6 }
            long r11 = java.lang.Long.parseLong(r0)     // Catch:{ Exception -> 0x00f6 }
            r13 = 10
            long r11 = r11 * r13
            goto L_0x0120
        L_0x010a:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r9)
            r11.append(r0)
            java.lang.String r0 = r11.toString()
            r9 = r19
            com.sec.android.app.voicenote.provider.Log.m22e(r9, r0)
        L_0x011e:
            r11 = r17
        L_0x0120:
            int r0 = (r11 > r17 ? 1 : (r11 == r17 ? 0 : -1))
            if (r0 <= 0) goto L_0x0138
            long r11 = r11 - r21
            r13 = 200(0xc8, double:9.9E-322)
            int r0 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r0 > 0) goto L_0x0138
            java.lang.String[] r0 = r6.mText
            r9 = 0
            r11 = r0[r9]
            java.lang.String r11 = r11.trim()
            r0[r9] = r11
            goto L_0x0139
        L_0x0138:
            r9 = 0
        L_0x0139:
            int r0 = r1.mRecognitionStartTime
            long r11 = (long) r0
            long r11 = r21 + r11
            r1.mLastTime = r11
            r7.add(r6)
            int r10 = r10 + 1
            goto L_0x0029
        L_0x0147:
            java.util.Collections.sort(r7)
            return r7
        L_0x014b:
            java.util.ArrayList r0 = r25.getEmptyWord()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.recognizer.Recognizer.getWord(java.lang.String, java.lang.String):java.util.ArrayList");
    }

    /* access modifiers changed from: private */
    public ArrayList<TextData> getEmptyWord() {
        this.mLastTime += 100;
        long j = this.mLastTime;
        long j2 = 500 + j;
        ArrayList<TextData> arrayList = new ArrayList<>(1);
        TextData textData = new TextData();
        textData.dataType = 0;
        textData.timeStamp = j;
        textData.elapsedTime = j;
        textData.duration = j2 - j;
        textData.mText[0] = ".... ";
        this.mLastTime = j2;
        arrayList.add(textData);
        return arrayList;
    }

    private void removeHandlerMessage() {
        if (this.mSilenceMonitorHandler.hasMessages(2000)) {
            this.mSilenceMonitorHandler.removeMessages(2000);
            this.mSilenceStartTime = 0;
        }
    }

    /* access modifiers changed from: private */
    public boolean isOverWriteMode() {
        return Engine.getInstance().getContentItemCount() > 1;
    }

    /* access modifiers changed from: package-private */
    public void stopListening() {
        Log.m26i(TAG, "stopListening");
        SpeechRecognizer speechRecognizer = this.mSpeechRecognizer;
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            this.mSpeechRecognizer.setListener((RecognitionListener) null);
        }
    }
}
