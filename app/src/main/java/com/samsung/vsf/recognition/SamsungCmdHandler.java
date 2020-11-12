package com.samsung.vsf.recognition;

import com.samsung.vsf.recognition.Recognizer;
import com.samsung.vsf.recognition.SamsungRecognizer;
import com.samsung.vsf.recognition.cmds.SendCmd;
import com.samsung.vsf.util.SVoiceLog;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class SamsungCmdHandler implements Recognizer.CmdHandler {
    private static final String TAG = ("tickcount:" + SamsungCmdHandler.class.getSimpleName());
    private BlockingQueue<SendCmd> linkedListB4EPD;
    private SamsungRecognizer mRecognizer;

    SamsungCmdHandler(SamsungRecognizer samsungRecognizer) {
        this.mRecognizer = samsungRecognizer;
    }

    public void create(Cmd cmd) {
        String str = TAG;
        SVoiceLog.info(str, "create() called in state " + this.mRecognizer.getState());
        this.linkedListB4EPD = new LinkedBlockingQueue();
        if (this.mRecognizer.getConfig().getSessionMode() != 2) {
            return;
        }
        if (this.mRecognizer.getState() == SamsungRecognizer.State.IDLE) {
            this.mRecognizer.svoiceOpenAsync();
            this.mRecognizer.setState(SamsungRecognizer.State.OPEN);
            return;
        }
        String str2 = TAG;
        SVoiceLog.info(str2, "Ignoring " + cmd.getClass().getSimpleName());
    }

    public void start(Cmd cmd) {
        String str = TAG;
        SVoiceLog.info(str, "start() called in state  " + this.mRecognizer.getState());
        this.mRecognizer.setRecState(SamsungRecognizer.RecState.START);
        if (this.mRecognizer.getState() == SamsungRecognizer.State.IDLE || this.mRecognizer.getState() == SamsungRecognizer.State.OPEN) {
            if (this.mRecognizer.startRecordingIfRequired()) {
                this.mRecognizer.createInstanceIfDestroyed();
                if (!this.mRecognizer.svoiceOpen() || !this.mRecognizer.svoicePrepare()) {
                    this.mRecognizer.notifyCCLError("Network error occurred");
                    this.mRecognizer.setState(SamsungRecognizer.State.IDLE);
                    return;
                }
                if (this.mRecognizer.getConfig().getSessionMode() == 2) {
                    this.mRecognizer.openNextInstance();
                }
                this.mRecognizer.setState(SamsungRecognizer.State.PREPARED);
                return;
            }
            String str2 = TAG;
            SVoiceLog.debug(str2, "Recording not started in state " + this.mRecognizer.getState());
            this.mRecognizer.abort();
        } else if (this.mRecognizer.getState() != SamsungRecognizer.State.PREPARED) {
            String str3 = TAG;
            SVoiceLog.info(str3, "Ignoring " + cmd.getClass().getSimpleName());
        } else if (this.mRecognizer.startRecordingIfRequired()) {
            this.mRecognizer.createInstanceIfDestroyed();
        } else {
            String str4 = TAG;
            SVoiceLog.debug(str4, "Recording not started in state " + SamsungRecognizer.State.PREPARED);
            this.mRecognizer.abort();
        }
    }

    public void stop(Cmd cmd) {
        String str = TAG;
        SVoiceLog.info(str, "stop() called in state " + this.mRecognizer.getState());
        this.mRecognizer.setRecState(SamsungRecognizer.RecState.LAST);
        this.mRecognizer.stopRecordingIfRequired();
        if (this.mRecognizer.getState() == SamsungRecognizer.State.SEND) {
            if (this.mRecognizer.svoiceProcess(false)) {
                this.mRecognizer.switchInstance();
                this.mRecognizer.clearAudioQueue();
            }
            this.mRecognizer.setState(SamsungRecognizer.State.IDLE);
        } else {
            String str2 = TAG;
            SVoiceLog.info(str2, "Ignoring " + cmd.getClass().getSimpleName());
        }
        this.linkedListB4EPD.clear();
    }

    public void send(Cmd cmd) {
        SendCmd sendCmd = (SendCmd) cmd;
        String str = TAG;
        SVoiceLog.info(str, "send() called in state " + this.mRecognizer.getState() + " and detection result " + sendCmd.getSpeechDetectionResult());
        if (!sendCmd.isBufferBeforeEPD()) {
            if (!this.linkedListB4EPD.isEmpty() && this.linkedListB4EPD.size() >= 500 / sendCmd.getDuration()) {
                this.linkedListB4EPD.remove();
            }
            this.linkedListB4EPD.offer(sendCmd);
        }
        if (this.mRecognizer.getState() == SamsungRecognizer.State.PREPARED) {
            if (this.mRecognizer.svoiceSend(sendCmd.getAudioBuffer())) {
                String str2 = TAG;
                SVoiceLog.info(str2, "For the current instance, first send() called with seqNumber " + sendCmd.getSequenceNumber());
                this.mRecognizer.setStartSeqNumber(sendCmd.getSequenceNumber());
                this.mRecognizer.setState(SamsungRecognizer.State.SEND);
                return;
            }
            this.mRecognizer.notifyCCLError("Network error occured");
            this.mRecognizer.setState(SamsungRecognizer.State.IDLE);
        } else if (this.mRecognizer.getState() != SamsungRecognizer.State.SEND) {
            String str3 = TAG;
            SVoiceLog.info(str3, "Ignoring " + cmd.getClass().getSimpleName());
            this.linkedListB4EPD.clear();
        } else if (!this.mRecognizer.svoiceSend(sendCmd.getAudioBuffer())) {
            this.mRecognizer.notifyCCLError("Network error occured");
            this.mRecognizer.setState(SamsungRecognizer.State.IDLE);
        } else if (sendCmd.isBufferBeforeEPD()) {
            SVoiceLog.debug(TAG, "Not a buffer before EPD");
        } else if (this.mRecognizer.speechTimeLimitExceeded() || sendCmd.getSpeechDetectionResult() == SendCmd.SpeechDetectionResult.SPEECH_END) {
            this.mRecognizer.svoiceProcess(true);
            if (this.mRecognizer.getConfig().getSessionMode() == 2) {
                this.mRecognizer.switchInstance();
                if (!this.mRecognizer.svoiceOpen() || !this.mRecognizer.svoicePrepare()) {
                    this.mRecognizer.stopRecordingIfRequired();
                    this.mRecognizer.notifyCCLError("Network error occurred");
                    this.mRecognizer.setState(SamsungRecognizer.State.IDLE);
                    this.linkedListB4EPD.clear();
                    return;
                }
                this.mRecognizer.setState(SamsungRecognizer.State.PREPARED);
                while (!this.linkedListB4EPD.isEmpty()) {
                    SVoiceLog.info(TAG, "send() dumping silence buffer ");
                    SendCmd sendCmd2 = (SendCmd) this.linkedListB4EPD.poll();
                    sendCmd2.setIsBufferBeforeEPD(true);
                    send(sendCmd2);
                }
                return;
            }
            SVoiceLog.debug(TAG, "SINGLE_SESSION_MODE : do not start next session");
        } else {
            SVoiceLog.debug(TAG, "EPD is not yet detected");
        }
    }

    public void cancel(Cmd cmd) {
        String str = TAG;
        SVoiceLog.info(str, "cancel() called in state " + this.mRecognizer.getState());
        this.mRecognizer.clearCancelled();
        this.mRecognizer.stopRecordingIfRequired();
        this.mRecognizer.setState(SamsungRecognizer.State.IDLE);
        this.linkedListB4EPD.clear();
    }

    public void destroy(Cmd cmd) {
        String str = TAG;
        SVoiceLog.info(str, "destroy() called in state " + this.mRecognizer.getState());
        BlockingQueue<SendCmd> blockingQueue = this.linkedListB4EPD;
        if (blockingQueue != null) {
            blockingQueue.clear();
            this.linkedListB4EPD = null;
        }
        SamsungRecognizer samsungRecognizer = this.mRecognizer;
        if (samsungRecognizer != null) {
            samsungRecognizer.stopRecordingIfRequired();
            this.mRecognizer.setState(SamsungRecognizer.State.IDLE);
            this.mRecognizer.shutdown();
            this.mRecognizer = null;
        }
    }
}
