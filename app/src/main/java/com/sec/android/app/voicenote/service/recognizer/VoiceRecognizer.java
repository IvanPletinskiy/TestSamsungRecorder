package com.sec.android.app.voicenote.service.recognizer;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.recognizer.VoiceWorker;

class VoiceRecognizer {
    private static final int MSG_RECOGNIZER_STOP = 1001;
    private static final int MSG_RECOGNIZER_STOP_DELAY_TIME = 5000;
    private static final String TAG = "VoiceRecognizer";
    private static VoiceRecognizer mInstance;
    private Handler mEventHandler = new Handler(new Handler.Callback() {
        public final boolean handleMessage(Message message) {
            return VoiceRecognizer.this.lambda$new$0$VoiceRecognizer(message);
        }
    });
    private VoiceWorker.StatusChangedListener mListener = null;
    private Recognizer mRecognizer = null;

    private VoiceRecognizer() {
        Log.m26i(TAG, "VoiceRecognizer creator !!");
    }

    public static VoiceRecognizer getInstance() {
        if (mInstance == null) {
            mInstance = new VoiceRecognizer();
        }
        return mInstance;
    }

    /* access modifiers changed from: package-private */
    public void startRecording() {
        Log.m26i(TAG, "startRecording");
        Recognizer recognizer = this.mRecognizer;
        if (recognizer != null) {
            recognizer.setLastRecognizer(false);
            this.mRecognizer.startRecording();
        }
    }

    /* access modifiers changed from: package-private */
    public void stopRecording() {
        Log.m26i(TAG, "stopRecording");
    }

    /* access modifiers changed from: package-private */
    public void startSTT(Context context) {
        Log.m26i(TAG, "startSTT");
        this.mEventHandler.removeMessages(1001);
        Recognizer recognizer = this.mRecognizer;
        if (recognizer != null) {
            recognizer.cancelRecognition();
            this.mRecognizer.destroy();
        }
        this.mRecognizer = new Recognizer();
        this.mRecognizer.startRecognition(context);
        registerListener(this.mListener);
    }

    /* access modifiers changed from: package-private */
    public void stopSTT() {
        Log.m26i(TAG, "stopSTT");
        Recognizer recognizer = this.mRecognizer;
        if (recognizer != null) {
            recognizer.stopRecording();
            this.mRecognizer.setLastRecognizer(true);
        }
        this.mEventHandler.removeMessages(1001);
        this.mEventHandler.sendEmptyMessageDelayed(1001, 5000);
    }

    /* access modifiers changed from: package-private */
    public void cancelSTT() {
        Log.m26i(TAG, "cancelSTT");
        Recognizer recognizer = this.mRecognizer;
        if (recognizer != null) {
            recognizer.cancelRecording();
        }
    }

    /* access modifiers changed from: package-private */
    public void initializeSTT() {
        Log.m26i(TAG, "initializeSTT");
        if (!this.mEventHandler.hasMessages(1001)) {
            this.mEventHandler.sendEmptyMessage(1001);
        }
    }

    /* access modifiers changed from: package-private */
    public void pauseSTT() {
        Log.m26i(TAG, "pauseSTT");
        Recognizer recognizer = this.mRecognizer;
        if (recognizer != null) {
            recognizer.stopRecording();
        }
    }

    /* access modifiers changed from: package-private */
    public void resumeSTT() {
        Log.m26i(TAG, "resumeSTT");
        Recognizer recognizer = this.mRecognizer;
        if (recognizer != null) {
            recognizer.setLastRecognizer(false);
        }
    }

    public void stopListening() {
        Recognizer recognizer = this.mRecognizer;
        if (recognizer != null) {
            recognizer.stopListening();
        }
    }

    public void registerListener(VoiceWorker.StatusChangedListener statusChangedListener) {
        Log.m26i(TAG, "registerListener : " + statusChangedListener);
        this.mListener = statusChangedListener;
        Recognizer recognizer = this.mRecognizer;
        if (recognizer != null) {
            recognizer.registerListener(this.mListener);
        }
    }

    public void unregisterListener(VoiceWorker.StatusChangedListener statusChangedListener) {
        Log.m26i(TAG, "unregisterListener : " + statusChangedListener);
        VoiceWorker.StatusChangedListener statusChangedListener2 = this.mListener;
        if (statusChangedListener2 != null && statusChangedListener2.equals(statusChangedListener)) {
            Recognizer recognizer = this.mRecognizer;
            if (recognizer != null) {
                recognizer.registerListener((VoiceWorker.StatusChangedListener) null);
            }
            this.mListener = null;
        }
        if (this.mEventHandler.hasMessages(1001)) {
            this.mEventHandler.removeMessages(1001);
            this.mEventHandler.sendEmptyMessage(1001);
        }
    }

    public boolean unregisteredListener() {
        return this.mListener == null;
    }

    /* access modifiers changed from: package-private */
    public void addAudioBuffer(byte[] bArr) {
        Recognizer recognizer = this.mRecognizer;
        if (recognizer != null) {
            recognizer.queueBuffer(bArr);
        }
    }

    /* access modifiers changed from: package-private */
    public void processResultForStop() {
        Log.m29v(TAG, "processResultForStop");
    }

    public /* synthetic */ boolean lambda$new$0$VoiceRecognizer(Message message) {
        if (message.what != 1001) {
            return false;
        }
        Log.m26i(TAG, "MSG_RECOGNIZER_STOP");
        Recognizer recognizer = this.mRecognizer;
        if (recognizer == null) {
            return false;
        }
        recognizer.cancelRecognition();
        this.mRecognizer.destroy();
        this.mRecognizer = null;
        return false;
    }
}
