package com.sec.android.app.voicenote.service.recognizer;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import com.sec.android.app.voicenote.common.util.TextData;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.StorageProvider;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class VoiceWorker {
    private static final int MSG_BUFFER_READ_START = 1000;
    private static final String STT_FILE = "/voice_note.stt";
    private static final String TAG = "VoiceWorker";
    private static final int mBufferReadSize = 3200;
    private static final int mBufferSizeOfBufferedInputStream = 9600;
    private static VoiceWorker mInstance;
    /* access modifiers changed from: private */
    public static VoiceRecognizer mVoiceRecognizer;
    /* access modifiers changed from: private */
    public AudioManager mAudioManager = null;
    private BufferReadThread mBufferReadThread = null;
    /* access modifiers changed from: private */
    public BufferedInputStream mBufferedInputStream = null;
    /* access modifiers changed from: private */
    public Handler mEventHandler = new Handler(new Handler.Callback() {
        private int fileNotFoundCount = 0;

        public boolean handleMessage(Message message) {
            if (message.what == 1000) {
                Log.m26i(VoiceWorker.TAG, "MSG_BUFFER_READ_START");
                VoiceWorker.mVoiceRecognizer.startRecording();
                try {
                    FileInputStream unused = VoiceWorker.this.mFileInputStream = new FileInputStream(VoiceWorker.this.getSttFilePath(VoiceWorker.this.mReadFileCount));
                    BufferedInputStream unused2 = VoiceWorker.this.mBufferedInputStream = new BufferedInputStream(VoiceWorker.this.mFileInputStream, VoiceWorker.mBufferSizeOfBufferedInputStream);
                    this.fileNotFoundCount = 0;
                } catch (FileNotFoundException e) {
                    this.fileNotFoundCount++;
                    if (this.fileNotFoundCount <= 1) {
                        VoiceWorker.this.mEventHandler.sendEmptyMessageDelayed(1000, 200);
                    } else {
                        this.fileNotFoundCount = 0;
                        Log.m22e(VoiceWorker.TAG, "MSG_BUFFER_READ_START error : " + e);
                    }
                }
                if (VoiceWorker.this.mBufferedInputStream == null) {
                    Log.m22e(VoiceWorker.TAG, "MSG_BUFFER_READ_START mBufferedInputStream is null");
                }
                VoiceWorker.this.restartBufferReadThread();
            }
            return false;
        }
    });
    /* access modifiers changed from: private */
    public FileInputStream mFileInputStream = null;
    /* access modifiers changed from: private */
    public boolean mFromFile = false;
    /* access modifiers changed from: private */
    public boolean mIsStopping = false;
    /* access modifiers changed from: private */
    public int mPcmFileCount = 0;
    private PcmWriteThread mPcmWriteThread = null;
    /* access modifiers changed from: private */
    public int mReadFileCount = 0;
    /* access modifiers changed from: private */
    public String mSttFilePath = null;

    public interface StatusChangedListener {
        void onClearScreen();

        void onError(String str);

        void onIsLastWord(boolean z);

        void onPartialResultWord(ArrayList<TextData> arrayList);

        void onRecognitionStart();

        void onResultWord(ArrayList<TextData> arrayList);
    }

    static /* synthetic */ int access$308(VoiceWorker voiceWorker) {
        int i = voiceWorker.mPcmFileCount;
        voiceWorker.mPcmFileCount = i + 1;
        return i;
    }

    private VoiceWorker() {
        Log.m26i(TAG, "VoiceWorker creator !!");
    }

    public static VoiceWorker getInstance() {
        if (mInstance == null) {
            mInstance = new VoiceWorker();
            mVoiceRecognizer = VoiceRecognizer.getInstance();
        }
        return mInstance;
    }

    public int getPcmFileCount() {
        return this.mPcmFileCount;
    }

    private void deletePcmFile(int i) {
        Locale locale = Locale.US;
        String format = String.format(locale, "%s%2$03d", new Object[]{this.mSttFilePath + STT_FILE, Integer.valueOf(i)});
        File file = new File(format);
        if (file.exists()) {
            Log.m29v(TAG, "deletePcmFile : " + format.substring(format.length() - 6, format.length()));
            if (!file.delete()) {
                Log.m29v(TAG, "deletePcmFile fail");
            }
        }
    }

    private void deleteAllPcmFiles() {
        int max = Math.max(this.mPcmFileCount, 32);
        for (int i = 1; i <= max; i++) {
            deletePcmFile(i);
        }
    }

    public String getSttFilePath(int i) {
        Locale locale = Locale.US;
        return String.format(locale, "%s%2$03d", new Object[]{this.mSttFilePath + STT_FILE, Integer.valueOf(i)});
    }

    private void restartPcmWriteThread() {
        Log.m29v(TAG, "restartPcmWriteThread");
        if (!isAliveThread(this.mPcmWriteThread)) {
            interruptThread(this.mPcmWriteThread);
            this.mPcmWriteThread = new PcmWriteThread();
            this.mPcmWriteThread.start();
        }
    }

    /* access modifiers changed from: private */
    public void restartBufferReadThread() {
        Log.m29v(TAG, "restartBufferReadThread");
        if (!isAliveThread(this.mBufferReadThread)) {
            interruptThread(this.mBufferReadThread);
            this.mBufferReadThread = new BufferReadThread();
            this.mBufferReadThread.start();
        }
    }

    private boolean isAliveThread(Thread thread) {
        return thread != null && thread.isAlive();
    }

    private void interruptThread(Thread thread) {
        Log.m29v(TAG, "interruptThread : " + thread);
        if (isAliveThread(thread)) {
            thread.interrupt();
        }
    }

    private class PcmWriteThread extends Thread {
        static final int PCM_WRITE_INTERVAL = 20000;

        private PcmWriteThread() {
        }

        public void run() {
            String access$200 = VoiceWorker.this.mSttFilePath;
            if (VoiceWorker.this.mSttFilePath != null && VoiceWorker.this.mSttFilePath.contains("storage/emulated")) {
                access$200 = VoiceWorker.this.mSttFilePath.replace("storage/emulated", "data/media");
            }
            while (!isInterrupted()) {
                VoiceWorker.access$308(VoiceWorker.this);
                if (!VoiceWorker.this.mFromFile) {
                    if (VoiceWorker.this.mPcmFileCount == 1) {
                        Log.m29v(VoiceWorker.TAG, "PCM_WRITE_START : " + VoiceWorker.this.mPcmFileCount);
                        if (VoiceWorker.this.mAudioManager != null) {
                            AudioManager access$500 = VoiceWorker.this.mAudioManager;
                            access$500.setParameters("voice_note_path=" + access$200);
                            VoiceWorker.this.mAudioManager.setParameters("voice_note_recording=on");
                        }
                    } else {
                        Log.m29v(VoiceWorker.TAG, "PCM_WRITE_CONTINUE : " + VoiceWorker.this.mPcmFileCount);
                        if (VoiceWorker.this.mAudioManager != null) {
                            VoiceWorker.this.mAudioManager.setParameters("voice_note_recording=conti");
                        }
                    }
                }
                SystemClock.sleep(20000);
            }
        }
    }

    private class BufferReadThread extends Thread {
        static final int BUFFER_READ_INTERVAL = 100;

        private BufferReadThread() {
        }

        /* JADX WARNING: Removed duplicated region for block: B:55:0x01af A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:60:0x000a A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r13 = this;
                java.lang.String r0 = "VoiceWorker"
                r1 = 3200(0xc80, float:4.484E-42)
                byte[] r2 = new byte[r1]
                r3 = 0
                r4 = r3
                r5 = r4
                r6 = r5
            L_0x000a:
                boolean r7 = r13.isInterrupted()
                if (r7 != 0) goto L_0x01af
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r7 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x0194 }
                java.io.BufferedInputStream r7 = r7.mBufferedInputStream     // Catch:{ IOException -> 0x0194 }
                if (r7 == 0) goto L_0x0025
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r7 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x0194 }
                java.io.BufferedInputStream r7 = r7.mBufferedInputStream     // Catch:{ IOException -> 0x0194 }
                int r4 = r7.read(r2, r3, r1)     // Catch:{ IOException -> 0x0194 }
                r6 = r5
                r5 = r3
                goto L_0x0070
            L_0x0025:
                java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0194 }
                r7.<init>()     // Catch:{ IOException -> 0x0194 }
                java.lang.String r8 = "mBufferedInputStream is null, mReadFileCount: "
                r7.append(r8)     // Catch:{ IOException -> 0x0194 }
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r8 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x0194 }
                int r8 = r8.mReadFileCount     // Catch:{ IOException -> 0x0194 }
                r7.append(r8)     // Catch:{ IOException -> 0x0194 }
                java.lang.String r8 = " streamNullCount: "
                r7.append(r8)     // Catch:{ IOException -> 0x0194 }
                r7.append(r4)     // Catch:{ IOException -> 0x0194 }
                java.lang.String r7 = r7.toString()     // Catch:{ IOException -> 0x0194 }
                com.sec.android.app.voicenote.provider.Log.m29v(r0, r7)     // Catch:{ IOException -> 0x0194 }
                int r4 = r4 + 1
                r7 = 5
                if (r4 <= r7) goto L_0x0053
                java.lang.String r7 = "streamNullCount is over 5"
                com.sec.android.app.voicenote.provider.Log.m29v(r0, r7)     // Catch:{ IOException -> 0x0194 }
                goto L_0x01af
            L_0x0053:
                if (r5 <= 0) goto L_0x0063
                com.sec.android.app.voicenote.service.recognizer.VoiceRecognizer r7 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.mVoiceRecognizer     // Catch:{ IOException -> 0x0194 }
                r7.processResultForStop()     // Catch:{ IOException -> 0x0194 }
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r7 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x0194 }
                r7.initializeSTT()     // Catch:{ IOException -> 0x0194 }
                goto L_0x01af
            L_0x0063:
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r7 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x0194 }
                boolean r6 = r7.mIsStopping     // Catch:{ IOException -> 0x0194 }
                if (r6 == 0) goto L_0x006d
                int r5 = r5 + 1
            L_0x006d:
                r6 = r5
                r5 = r4
                r4 = r3
            L_0x0070:
                int r7 = r2.length     // Catch:{ IOException -> 0x018f }
                r8 = 100
                if (r4 >= r7) goto L_0x0130
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r7 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x018f }
                int r7 = r7.mPcmFileCount     // Catch:{ IOException -> 0x018f }
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r10 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x018f }
                int r10 = r10.mReadFileCount     // Catch:{ IOException -> 0x018f }
                if (r7 == r10) goto L_0x0130
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r7 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x018f }
                java.io.BufferedInputStream r7 = r7.mBufferedInputStream     // Catch:{ IOException -> 0x018f }
                r10 = 0
                if (r7 == 0) goto L_0x009a
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r7 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x018f }
                java.io.BufferedInputStream r7 = r7.mBufferedInputStream     // Catch:{ IOException -> 0x018f }
                r7.close()     // Catch:{ IOException -> 0x018f }
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r7 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x018f }
                java.io.BufferedInputStream unused = r7.mBufferedInputStream = r10     // Catch:{ IOException -> 0x018f }
            L_0x009a:
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r7 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x018f }
                java.io.FileInputStream r7 = r7.mFileInputStream     // Catch:{ IOException -> 0x018f }
                if (r7 == 0) goto L_0x00b0
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r7 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x018f }
                java.io.FileInputStream r7 = r7.mFileInputStream     // Catch:{ IOException -> 0x018f }
                r7.close()     // Catch:{ IOException -> 0x018f }
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r7 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x018f }
                java.io.FileInputStream unused = r7.mFileInputStream = r10     // Catch:{ IOException -> 0x018f }
            L_0x00b0:
                if (r4 >= 0) goto L_0x00b3
                r4 = r3
            L_0x00b3:
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r7 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x018f }
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r10 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x018f }
                int r10 = r10.mPcmFileCount     // Catch:{ IOException -> 0x018f }
                java.lang.String r7 = r7.getSttFilePath(r10)     // Catch:{ IOException -> 0x018f }
                java.io.File r10 = new java.io.File     // Catch:{ IOException -> 0x018f }
                r10.<init>(r7)     // Catch:{ IOException -> 0x018f }
                boolean r7 = r10.exists()     // Catch:{ IOException -> 0x018f }
                if (r7 != 0) goto L_0x00cf
                android.os.SystemClock.sleep(r8)     // Catch:{ IOException -> 0x018f }
                goto L_0x018a
            L_0x00cf:
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r7 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x018f }
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r10 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x018f }
                int r10 = r10.mPcmFileCount     // Catch:{ IOException -> 0x018f }
                int unused = r7.mReadFileCount = r10     // Catch:{ IOException -> 0x018f }
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r7 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x018f }
                java.io.FileInputStream r10 = new java.io.FileInputStream     // Catch:{ IOException -> 0x018f }
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r11 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x018f }
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r12 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x018f }
                int r12 = r12.mReadFileCount     // Catch:{ IOException -> 0x018f }
                java.lang.String r11 = r11.getSttFilePath(r12)     // Catch:{ IOException -> 0x018f }
                r10.<init>(r11)     // Catch:{ IOException -> 0x018f }
                java.io.FileInputStream unused = r7.mFileInputStream = r10     // Catch:{ IOException -> 0x018f }
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r7 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x018f }
                java.io.BufferedInputStream r10 = new java.io.BufferedInputStream     // Catch:{ IOException -> 0x018f }
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r11 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x018f }
                java.io.FileInputStream r11 = r11.mFileInputStream     // Catch:{ IOException -> 0x018f }
                r12 = 9600(0x2580, float:1.3452E-41)
                r10.<init>(r11, r12)     // Catch:{ IOException -> 0x018f }
                java.io.BufferedInputStream unused = r7.mBufferedInputStream = r10     // Catch:{ IOException -> 0x018f }
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r7 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x018f }
                java.io.BufferedInputStream r7 = r7.mBufferedInputStream     // Catch:{ IOException -> 0x018f }
                int r10 = r2.length     // Catch:{ IOException -> 0x018f }
                int r10 = r10 - r4
                int r4 = r7.read(r2, r4, r10)     // Catch:{ IOException -> 0x018f }
                java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x018f }
                r7.<init>()     // Catch:{ IOException -> 0x018f }
                java.lang.String r10 = "BufferReadThread : "
                r7.append(r10)     // Catch:{ IOException -> 0x018f }
                r7.append(r4)     // Catch:{ IOException -> 0x018f }
                java.lang.String r10 = ", mReadFileCount : "
                r7.append(r10)     // Catch:{ IOException -> 0x018f }
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r10 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x018f }
                int r10 = r10.mReadFileCount     // Catch:{ IOException -> 0x018f }
                r7.append(r10)     // Catch:{ IOException -> 0x018f }
                java.lang.String r7 = r7.toString()     // Catch:{ IOException -> 0x018f }
                com.sec.android.app.voicenote.provider.Log.m29v(r0, r7)     // Catch:{ IOException -> 0x018f }
            L_0x0130:
                if (r4 >= 0) goto L_0x0163
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x018f }
                r4.<init>()     // Catch:{ IOException -> 0x018f }
                java.lang.String r7 = "BufferReadThread : shortsRead < 0 , stoppingCount:"
                r4.append(r7)     // Catch:{ IOException -> 0x018f }
                r4.append(r6)     // Catch:{ IOException -> 0x018f }
                java.lang.String r4 = r4.toString()     // Catch:{ IOException -> 0x018f }
                com.sec.android.app.voicenote.provider.Log.m29v(r0, r4)     // Catch:{ IOException -> 0x018f }
                if (r6 <= 0) goto L_0x0155
                com.sec.android.app.voicenote.service.recognizer.VoiceRecognizer r4 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.mVoiceRecognizer     // Catch:{ IOException -> 0x018f }
                r4.processResultForStop()     // Catch:{ IOException -> 0x018f }
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r4 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x018f }
                r4.initializeSTT()     // Catch:{ IOException -> 0x018f }
                goto L_0x01af
            L_0x0155:
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r4 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x018f }
                boolean r4 = r4.mIsStopping     // Catch:{ IOException -> 0x018f }
                if (r4 == 0) goto L_0x015f
                int r6 = r6 + 1
            L_0x015f:
                android.os.SystemClock.sleep(r8)     // Catch:{ IOException -> 0x018f }
                goto L_0x018a
            L_0x0163:
                com.sec.android.app.voicenote.service.recognizer.VoiceRecognizer r4 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.mVoiceRecognizer     // Catch:{ IOException -> 0x018f }
                r4.addAudioBuffer(r2)     // Catch:{ IOException -> 0x018f }
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r4 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x018f }
                java.io.BufferedInputStream r4 = r4.mBufferedInputStream     // Catch:{ IOException -> 0x018f }
                if (r4 == 0) goto L_0x0187
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r4 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.this     // Catch:{ IOException -> 0x018f }
                java.io.BufferedInputStream r4 = r4.mBufferedInputStream     // Catch:{ IOException -> 0x018f }
                int r4 = r4.available()     // Catch:{ IOException -> 0x018f }
                int r7 = r2.length     // Catch:{ IOException -> 0x018f }
                int r7 = r7 * 2
                if (r4 <= r7) goto L_0x0187
                r7 = 50
                android.os.SystemClock.sleep(r7)     // Catch:{ IOException -> 0x018f }
                goto L_0x018a
            L_0x0187:
                android.os.SystemClock.sleep(r8)     // Catch:{ IOException -> 0x018f }
            L_0x018a:
                r4 = r5
                r5 = r6
                r6 = r3
                goto L_0x000a
            L_0x018f:
                r7 = move-exception
                r4 = r5
                r5 = r6
                r6 = r3
                goto L_0x0195
            L_0x0194:
                r7 = move-exception
            L_0x0195:
                java.lang.StringBuilder r8 = new java.lang.StringBuilder
                r8.<init>()
                java.lang.String r9 = "readRunnable error : "
                r8.append(r9)
                r8.append(r7)
                java.lang.String r7 = r8.toString()
                com.sec.android.app.voicenote.provider.Log.m22e(r0, r7)
                int r6 = r6 + 1
                r7 = 10
                if (r6 <= r7) goto L_0x000a
            L_0x01af:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.recognizer.VoiceWorker.BufferReadThread.run():void");
        }
    }

    public void makeSttFolder() {
        this.mSttFilePath = StorageProvider.getTempFilePath(0) + "/voice_note";
        Log.m29v(TAG, "makeSttFolder " + this.mSttFilePath);
        File file = new File(this.mSttFilePath);
        if (!file.isDirectory() && file.mkdirs()) {
            Log.m29v(TAG, "makeSttFolder success");
        }
    }

    public void setFromFile(boolean z) {
        this.mFromFile = z;
    }

    public void startSTT(Context context) {
        Log.m26i(TAG, "startSTT");
        if (this.mIsStopping) {
            initializeSTT();
        }
        this.mPcmFileCount = 0;
        this.mReadFileCount = 1;
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
        restartPcmWriteThread();
        this.mEventHandler.removeMessages(1000);
        this.mEventHandler.sendEmptyMessageDelayed(1000, 200);
        this.mIsStopping = false;
        mVoiceRecognizer.startSTT(context);
    }

    public void stopSTT() {
        Log.m26i(TAG, "stopSTT");
        AudioManager audioManager = this.mAudioManager;
        if (audioManager != null) {
            audioManager.setParameters("voice_note_recording=off");
        }
        interruptThread(this.mPcmWriteThread);
        this.mEventHandler.removeMessages(1000);
        this.mIsStopping = true;
        mVoiceRecognizer.stopSTT();
    }

    public void cancelSTT() {
        Log.m26i(TAG, "cancelSTT");
        AudioManager audioManager = this.mAudioManager;
        if (audioManager != null) {
            audioManager.setParameters("voice_note_recording=off");
        }
        interruptThread(this.mPcmWriteThread);
        this.mEventHandler.removeMessages(1000);
        this.mIsStopping = true;
        mVoiceRecognizer.cancelSTT();
        initializeSTT();
    }

    /* access modifiers changed from: private */
    public void initializeSTT() {
        Log.m26i(TAG, "initializeSTT");
        interruptThread(this.mBufferReadThread);
        mVoiceRecognizer.stopRecording();
        try {
            if (this.mBufferedInputStream != null) {
                this.mBufferedInputStream.close();
            }
            if (this.mFileInputStream != null) {
                this.mFileInputStream.close();
            }
        } catch (IOException e) {
            Log.m22e(TAG, "stopSTT IOException : " + e);
        }
        deleteAllPcmFiles();
        this.mAudioManager = null;
        this.mPcmWriteThread = null;
        this.mBufferReadThread = null;
        this.mFileInputStream = null;
        this.mBufferedInputStream = null;
        this.mIsStopping = false;
        mVoiceRecognizer.initializeSTT();
    }

    public void pauseSTT() {
        Log.m26i(TAG, "pauseSTT");
        AudioManager audioManager = this.mAudioManager;
        if (audioManager != null) {
            audioManager.setParameters("voice_note_recording=off");
        }
        this.mEventHandler.removeMessages(1000);
        interruptThread(this.mPcmWriteThread);
        interruptThread(this.mBufferReadThread);
        mVoiceRecognizer.stopRecording();
        mVoiceRecognizer.pauseSTT();
        try {
            if (this.mBufferedInputStream != null) {
                this.mBufferedInputStream.close();
            }
            if (this.mFileInputStream != null) {
                this.mFileInputStream.close();
            }
        } catch (IOException e) {
            Log.m22e(TAG, "stopSTT IOException : " + e);
        }
        deleteAllPcmFiles();
        this.mPcmWriteThread = null;
        this.mBufferReadThread = null;
        this.mFileInputStream = null;
        this.mBufferedInputStream = null;
    }

    public void resumeSTT() {
        Log.m26i(TAG, "resumeSTT");
        this.mPcmFileCount = 0;
        this.mReadFileCount = 1;
        mVoiceRecognizer.resumeSTT();
        restartPcmWriteThread();
        this.mEventHandler.removeMessages(1000);
        this.mEventHandler.sendEmptyMessageDelayed(1000, 200);
    }

    public void registerListener(StatusChangedListener statusChangedListener) {
        Log.m26i(TAG, "registerListener : " + statusChangedListener);
        mVoiceRecognizer.registerListener(statusChangedListener);
    }

    public void unregisterListener(StatusChangedListener statusChangedListener) {
        Log.m26i(TAG, "unregisterListener : " + statusChangedListener);
        mVoiceRecognizer.unregisterListener(statusChangedListener);
    }

    public boolean hasSttFragmentListener() {
        VoiceRecognizer voiceRecognizer = mVoiceRecognizer;
        return voiceRecognizer == null || !voiceRecognizer.unregisteredListener();
    }
}
