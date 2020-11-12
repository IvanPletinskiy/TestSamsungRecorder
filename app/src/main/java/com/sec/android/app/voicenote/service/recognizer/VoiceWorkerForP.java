package com.sec.android.app.voicenote.service.recognizer;

import android.content.Context;
import android.media.AudioRecord;
import com.sec.android.app.voicenote.provider.Log;

public class VoiceWorkerForP {
    private static final String TAG = "VoiceWorkerForP";
    private static VoiceWorkerForP mInstance;
    /* access modifiers changed from: private */
    public static VoiceRecognizer mVoiceRecognizer;
    /* access modifiers changed from: private */
    public AudioRecord mAudioRecord = null;
    private RecordingThread recordingThread = null;

    public static VoiceWorkerForP getInstance() {
        if (mInstance == null) {
            mInstance = new VoiceWorkerForP();
            mVoiceRecognizer = VoiceRecognizer.getInstance();
        }
        return mInstance;
    }

    public void startSTT(Context context) {
        Log.m26i(TAG, "startSTT");
        startRecordingSTT();
        mVoiceRecognizer.startSTT(context);
        mVoiceRecognizer.startRecording();
    }

    private void startRecordingSTT() {
        this.mAudioRecord = new AudioRecord(6, 16000, 16, 2, 3200);
        this.mAudioRecord.startRecording();
        this.recordingThread = new RecordingThread();
        this.recordingThread.start();
    }

    private void stopRecordingSTT() {
        RecordingThread recordingThread2 = this.recordingThread;
        if (recordingThread2 != null) {
            recordingThread2.interrupt();
        }
        AudioRecord audioRecord = this.mAudioRecord;
        if (audioRecord != null) {
            audioRecord.stop();
            this.mAudioRecord.release();
            this.mAudioRecord = null;
        }
    }

    public void stopSTT() {
        Log.m26i(TAG, "stopSTT");
        mVoiceRecognizer.stopSTT();
        stopRecordingSTT();
    }

    public void cancelSTT() {
        Log.m26i(TAG, "cancelSTT");
        mVoiceRecognizer.cancelSTT();
        stopRecordingSTT();
    }

    public void resumeSTT() {
        Log.m26i(TAG, "resumeSTT");
        startRecordingSTT();
        mVoiceRecognizer.resumeSTT();
        mVoiceRecognizer.startRecording();
    }

    public void pauseSTT() {
        Log.m26i(TAG, "pauseSTT");
        stopRecordingSTT();
        mVoiceRecognizer.stopRecording();
        mVoiceRecognizer.pauseSTT();
    }

    private class RecordingThread extends Thread {
        private RecordingThread() {
        }

        public void run() {
            byte[] bArr = new byte[3200];
            while (!isInterrupted()) {
                VoiceWorkerForP.this.mAudioRecord.read(bArr, 0, 3200);
                VoiceWorkerForP.mVoiceRecognizer.addAudioBuffer(bArr);
            }
        }
    }
}
