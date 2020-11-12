package com.samsung.vsf.recognition;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.samsung.vsf.SpeechRecognizer;
import com.samsung.vsf.recognition.cmds.StopCmd;
import com.samsung.vsf.util.SVoiceLog;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Recognizer extends Handler {
    private LinkedBlockingQueue<BufferObject> mBufferQueue;
    private CmdHandler mCmdHandler;
    private SpeechRecognizer.Config mConfig;
    private Context mContext;
    private Object mLockObject = new Object();
    private ResponseHandler mResponseHandler;

    public interface CmdHandler {
        void cancel(Cmd cmd);

        void create(Cmd cmd);

        void destroy(Cmd cmd);

        void send(Cmd cmd);

        void start(Cmd cmd);

        void stop(Cmd cmd);
    }

    public interface ResponseHandler {
        void onBufferReceived(short[] sArr);

        void onDestroy();

        void onError(String str);

        void onErrorString(String str);

        void onPartialResult(Properties properties);

        void onRMSresult(int i);

        void onResult(Properties properties);

        void onSpeechEnded();

        void onSpeechStarted();
    }

    public abstract void abort();

    public boolean onCmd(Cmd cmd) {
        return false;
    }

    public Recognizer(Context context, Looper looper, SpeechRecognizer.Config config) {
        super(looper);
        this.mContext = context;
        this.mConfig = config;
        this.mBufferQueue = new LinkedBlockingQueue<>();
    }

    public void shutdown() {
        synchronized (this.mLockObject) {
            if (this.mResponseHandler != null) {
                this.mResponseHandler.onDestroy();
                this.mResponseHandler = null;
            }
        }
        this.mContext = null;
        this.mBufferQueue = null;
        this.mCmdHandler = null;
    }

    public Context getAndroidContext() {
        return this.mContext;
    }

    public SpeechRecognizer.Config getConfig() {
        return this.mConfig;
    }

    public void setCmdHandler(CmdHandler cmdHandler) {
        this.mCmdHandler = cmdHandler;
    }

    public void setResponseHandler(ResponseHandler responseHandler) {
        this.mResponseHandler = responseHandler;
    }

    public void postCommand(Cmd cmd) {
        if (getLooper() != null) {
            if (cmd.shouldClearQueue()) {
                clearCmds();
            }
            obtainMessage(1, cmd).sendToTarget();
        }
    }

    public void postCommand2(Cmd cmd) {
        if (getLooper() != null) {
            if (cmd.shouldClearQueue()) {
                clearCmds();
            }
            obtainMessage(2, cmd).sendToTarget();
        }
    }

    public void queueBuffer(BufferObject bufferObject) {
        SVoiceLog.info("tickcount::", "Enqueue audio buffer");
        LinkedBlockingQueue<BufferObject> linkedBlockingQueue = this.mBufferQueue;
        if (linkedBlockingQueue != null) {
            linkedBlockingQueue.offer(bufferObject);
        }
    }

    public BufferObject readAudioBuffer() {
        LinkedBlockingQueue<BufferObject> linkedBlockingQueue = this.mBufferQueue;
        if (linkedBlockingQueue != null) {
            try {
                return linkedBlockingQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void clearAudioQueue() {
        LinkedBlockingQueue<BufferObject> linkedBlockingQueue = this.mBufferQueue;
        if (linkedBlockingQueue != null) {
            linkedBlockingQueue.clear();
            this.mBufferQueue.offer(new BufferObject(new byte[0], false));
        }
        Thread.yield();
    }

    public void handleMessage(Message message) {
        Cmd cmd = (Cmd) message.obj;
        if (cmd == null) {
            SVoiceLog.info("tickcount::", "handleMessage: cmd is null!");
        } else if (!onCmd(cmd) && this.mCmdHandler != null) {
            SVoiceLog.info("tickcount::", "Recognizer execute " + cmd.getClass().getSimpleName());
            cmd.execute(this.mCmdHandler);
        }
    }

    public void clearCmds() {
        removeMessages(1);
    }

    public void notifyStartOfSpeech() {
        synchronized (this.mLockObject) {
            if (!(this.mConfig == null || !this.mConfig.getIsSpeechDetectionNotificationRequired() || this.mResponseHandler == null)) {
                SVoiceLog.info("tickcount::", "Recognizer notifyStartOfSpeech() ");
                this.mResponseHandler.onSpeechStarted();
            }
        }
    }

    public void notifyEndOfSpeech() {
        synchronized (this.mLockObject) {
            if (!(this.mConfig == null || !this.mConfig.getIsSpeechDetectionNotificationRequired() || this.mResponseHandler == null)) {
                SVoiceLog.info("tickcount::", "Recognizer notifyEndOfSpeech() ");
                this.mResponseHandler.onSpeechEnded();
            }
        }
        if (getConfig().getSessionMode() == 1) {
            postCommand(new StopCmd());
        }
    }

    public void notifyPartialResult(Properties properties) {
        synchronized (this.mLockObject) {
            if (this.mResponseHandler != null) {
                SVoiceLog.info("tickcount::", "Recognizer notifyPartialResult() ");
                this.mResponseHandler.onPartialResult(properties);
            }
        }
    }

    public void notifyResult(Properties properties) {
        synchronized (this.mLockObject) {
            if (this.mResponseHandler != null) {
                SVoiceLog.info("tickcount::", "Recognizer onResult() ");
                this.mResponseHandler.onResult(properties);
            }
        }
    }

    public void notifyErrorString(String str) {
        synchronized (this.mLockObject) {
            if (this.mResponseHandler != null) {
                SVoiceLog.info("tickcount::", "Recognizer notifyErrorString() ");
                this.mResponseHandler.onErrorString(str);
                this.mResponseHandler.onError(str);
            }
        }
    }

    public void notifyRMSresult(int i) {
        synchronized (this.mLockObject) {
            if (this.mResponseHandler != null) {
                SVoiceLog.info("tickcount::", "Recognizer notifyRMSresult() ");
                this.mResponseHandler.onRMSresult(i);
            }
        }
    }

    public void notifyRecordedBuffer(short[] sArr) {
        synchronized (this.mLockObject) {
            if (this.mResponseHandler != null) {
                SVoiceLog.info("tickcount::", "Recognizer notifyRecordedBuffer() ");
                this.mResponseHandler.onBufferReceived(sArr);
            }
        }
    }
}
