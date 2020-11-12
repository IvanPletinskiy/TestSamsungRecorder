package com.samsung.vsf.audio;

import android.media.AudioRecord;
import com.samsung.vsf.recognition.BufferObject;
import com.samsung.vsf.recognition.Recognizer;
import com.samsung.vsf.util.SVoiceLog;

/**
 * Contains Android SDK AudioRecord
 */
public class AudioRecorder extends Thread {
    private boolean isRecording = true;
    private final Object mLockObject = new Object();
    private Recognizer mRecognizer;
    private AudioRecord mRecorder = new AudioRecord(6, 16000, 16, 2, 6400);
    private boolean mRun = false;

    public AudioRecorder(Recognizer recognizer) {
        this.mRecognizer = recognizer;
    }

    public boolean startRecording() {
        SVoiceLog.debug("AudioRecorder", "Recording state : " + this.mRecorder.getState());
        if (!this.mRun) {
            if (this.mRecorder.getState() == 1) {
                this.mRun = true;
                synchronized (this.mLockObject) {
                    this.mLockObject.notify();
                }
            } else {
                SVoiceLog.debug("AudioRecorder", "Recording not started");
            }
        }
        synchronized (this.mLockObject) {
            try {
                this.mLockObject.wait(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        SVoiceLog.debug("AudioRecorder", "Recorder flag : " + this.mRun);
        return this.mRun;
    }

    public void stopRecording() {
        SVoiceLog.debug("AudioRecorder", "Stopping the recorder");
        this.mRun = false;
        synchronized (this.mLockObject) {
            this.mLockObject.notify();
        }
    }

    public boolean isRecording() {
        SVoiceLog.info("AudioRecorder", "isRecording : " + this.mRun);
        return this.mRun;
    }

    public void shutdown() {
        this.isRecording = false;
        this.mRun = false;
        synchronized (this.mLockObject) {
            this.mLockObject.notify();
        }
    }

    public void run() {
        while (this.isRecording) {
            if (this.mRun) {
                performRecording();
            }
            try {
                synchronized (this.mLockObject) {
                    this.mLockObject.wait(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.mRecorder.release();
        this.mRecorder = null;
    }

    private void performRecording() {
        if (this.mRecorder.getState() == 1) {
            this.mRecorder.startRecording();
        }
        if (this.mRecorder.getRecordingState() == 3) {
            SVoiceLog.info("AudioRecorder", "Recording Started");
            while (this.mRun) {
                byte[] bArr = new byte[3200];
                int read = this.mRecorder.read(bArr, 0, 3200);
                BufferObject bufferObject = new BufferObject(bArr, false);
                if (read == -2 || read == -3) {
                    this.mRun = false;
                } else {
                    Recognizer recognizer = this.mRecognizer;
                    if (recognizer != null) {
                        recognizer.queueBuffer(bufferObject);
                    } else {
                        SVoiceLog.info("AudioRecorder", "mRecognizer is null");
                    }
                }
            }
            this.mRecorder.stop();
            SVoiceLog.info("AudioRecorder", "Recording Stoppped");
            return;
        }
        this.mRun = false;
        SVoiceLog.error("AudioRecorder", "Failed to startRecording");
    }
}
