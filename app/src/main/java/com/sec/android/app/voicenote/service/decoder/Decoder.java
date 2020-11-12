package com.sec.android.app.voicenote.service.decoder;

import android.content.Context;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.StorageProvider;
import com.sec.android.app.voicenote.service.recognizer.VoiceWorker;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Decoder {
    private static final long SYNC_DELAY = 100;
    private static final String TAG = "Decoder";
    private static Decoder mInstance;
    /* access modifiers changed from: private */
    public int index;
    /* access modifiers changed from: private */
    public long mCurrentFileStartTime = -1;
    /* access modifiers changed from: private */
    public onDecoderListener mDecoderListener = null;
    private DecoderThread mDecoderThread = null;
    /* access modifiers changed from: private */
    public long mDecodingStartTime;
    private boolean mIsComplete = false;
    /* access modifiers changed from: private */
    public String mMediaPath = null;
    /* access modifiers changed from: private */
    public String mPcmFileName = null;
    /* access modifiers changed from: private */
    public long mProgressTime;
    private int mState = 1;

    public static class TranslatorState {
        public static final int IDLE = 1;
        public static final int PAUSED = 3;
        public static final int TRANSLATION = 2;
    }

    public interface onDecoderListener {
        void onDecoderProgress(int i);

        void onDecoderStop();

        void onPartialDecodeComplete(String str);
    }

    public static Decoder getInstance() {
        if (mInstance == null) {
            mInstance = new Decoder();
        }
        return mInstance;
    }

    private Decoder() {
        Log.m19d(TAG, "Decoder create");
    }

    public void setMediaPath(String str) {
        this.mMediaPath = str;
        Log.m19d(TAG, "setMediaPath mMediaPath : " + this.mMediaPath);
        VoiceWorker.getInstance().makeSttFolder();
    }

    public void setStartTime(long j) {
        this.mDecodingStartTime = j;
    }

    /* access modifiers changed from: private */
    public FileOutputStream getNextOutputStream() {
        this.index++;
        try {
            this.mPcmFileName = VoiceWorker.getInstance().getSttFilePath(this.index);
            Log.m19d(TAG, "getNextOutputStream : " + this.mPcmFileName);
            return new FileOutputStream(this.mPcmFileName);
        } catch (FileNotFoundException e) {
            Log.m21d(TAG, "FileNotFoundException", (Throwable) e);
            return null;
        }
    }

    /* access modifiers changed from: private */
    public void clearAllFiles() {
        File[] listFiles;
        StringBuilder sb = new StringBuilder();
        sb.append(StorageProvider.getTempFilePath(0));
        sb.append("/voice_note");
        String sb2 = sb.toString();
        Log.m19d(TAG, "clearAllFiles : " + sb2);
        File file = new File(sb2);
        if (file.exists() && file.isDirectory() && (listFiles = file.listFiles()) != null && listFiles.length > 0) {
            for (File file2 : listFiles) {
                if (file2.exists()) {
                    if (!file2.delete()) {
                        Log.m19d(TAG, "delete fail : " + file2.getName());
                    } else {
                        Log.m19d(TAG, "delete : " + file2.getName());
                    }
                }
            }
        }
    }

    private class DecoderThread extends Thread {
        private DecoderThread() {
        }

        /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r30 = this;
                r1 = r30
                com.sec.android.app.voicenote.nativelayer.SrcJni r2 = new com.sec.android.app.voicenote.nativelayer.SrcJni
                r2.<init>()
                r2.create()
                android.media.MediaExtractor r3 = new android.media.MediaExtractor
                r3.<init>()
                com.sec.android.app.voicenote.service.decoder.Decoder r0 = com.sec.android.app.voicenote.service.decoder.Decoder.this     // Catch:{ Exception -> 0x036f }
                java.lang.String r0 = r0.mMediaPath     // Catch:{ Exception -> 0x036f }
                r3.setDataSource(r0)     // Catch:{ Exception -> 0x036f }
                com.sec.android.app.voicenote.service.decoder.Decoder r0 = com.sec.android.app.voicenote.service.decoder.Decoder.this
                r0.clearAllFiles()
                r4 = 0
                android.media.MediaFormat r0 = r3.getTrackFormat(r4)
                java.lang.String r5 = "mime"
                java.lang.String r5 = r0.getString(r5)
                java.lang.String r6 = "channel-count"
                int r6 = r0.getInteger(r6)
                java.lang.String r7 = "sample-rate"
                int r7 = r0.getInteger(r7)
                java.lang.String r8 = "bitrate"
                int r8 = r0.getInteger(r8)
                java.lang.StringBuilder r9 = new java.lang.StringBuilder
                r9.<init>()
                java.lang.String r10 = "BitRate = "
                r9.append(r10)
                r9.append(r8)
                java.lang.String r8 = " : SampleRate = "
                r9.append(r8)
                r9.append(r7)
                java.lang.String r8 = " : ChannelCount = "
                r9.append(r8)
                r9.append(r6)
                java.lang.String r8 = r9.toString()
                java.lang.String r9 = "Decoder"
                com.sec.android.app.voicenote.provider.Log.m26i(r9, r8)
                android.media.MediaCodec r8 = android.media.MediaCodec.createDecoderByType(r5)     // Catch:{  }
                r10 = 0
                r8.configure(r0, r10, r10, r4)
                r8.start()
                r0 = 44100(0xac44, float:6.1797E-41)
                r10 = 3
                if (r7 != r0) goto L_0x0076
                r0 = 7
                r2.init(r10, r0, r6, r4)
                goto L_0x007b
            L_0x0076:
                r0 = 8
                r2.init(r10, r0, r6, r4)
            L_0x007b:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r7 = "start - mime : "
                r0.append(r7)
                r0.append(r5)
                java.lang.String r0 = r0.toString()
                com.sec.android.app.voicenote.provider.Log.m26i(r9, r0)
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r5 = "start - mDecodingStartTime : "
                r0.append(r5)
                com.sec.android.app.voicenote.service.decoder.Decoder r5 = com.sec.android.app.voicenote.service.decoder.Decoder.this
                long r10 = r5.mDecodingStartTime
                r0.append(r10)
                java.lang.String r0 = r0.toString()
                com.sec.android.app.voicenote.provider.Log.m26i(r9, r0)
                r3.selectTrack(r4)
                com.sec.android.app.voicenote.service.decoder.Decoder r0 = com.sec.android.app.voicenote.service.decoder.Decoder.this
                long r10 = r0.mDecodingStartTime
                r14 = 0
                int r0 = (r10 > r14 ? 1 : (r10 == r14 ? 0 : -1))
                r17 = 1000(0x3e8, double:4.94E-321)
                r5 = 2
                if (r0 <= 0) goto L_0x00c6
                com.sec.android.app.voicenote.service.decoder.Decoder r0 = com.sec.android.app.voicenote.service.decoder.Decoder.this
                long r10 = r0.mDecodingStartTime
                long r10 = r10 * r17
                r3.seekTo(r10, r5)
            L_0x00c6:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r7 = "start - seekTo : "
                r0.append(r7)
                long r10 = r3.getSampleTime()
                long r10 = r10 / r17
                r0.append(r10)
                java.lang.String r0 = r0.toString()
                com.sec.android.app.voicenote.provider.Log.m26i(r9, r0)
                android.media.MediaCodec$BufferInfo r7 = new android.media.MediaCodec$BufferInfo
                r7.<init>()
                r13 = 50
                com.sec.android.app.voicenote.service.decoder.Decoder r0 = com.sec.android.app.voicenote.service.decoder.Decoder.this
                int unused = r0.index = r4
                com.sec.android.app.voicenote.service.decoder.Decoder r0 = com.sec.android.app.voicenote.service.decoder.Decoder.this
                long r10 = r0.mCurrentFileStartTime
                r19 = -1
                int r0 = (r10 > r19 ? 1 : (r10 == r19 ? 0 : -1))
                if (r0 != 0) goto L_0x0101
                com.sec.android.app.voicenote.service.decoder.Decoder r0 = com.sec.android.app.voicenote.service.decoder.Decoder.this
                long r10 = r3.getSampleTime()
                long unused = r0.mCurrentFileStartTime = r10
            L_0x0101:
                com.sec.android.app.voicenote.service.Engine r0 = com.sec.android.app.voicenote.service.Engine.getInstance()
                int r0 = r0.getDuration()
                long r10 = (long) r0
                long r19 = r10 * r17
                com.sec.android.app.voicenote.service.decoder.Decoder r0 = com.sec.android.app.voicenote.service.decoder.Decoder.this
                long unused = r0.mProgressTime = r14
                com.sec.android.app.voicenote.service.decoder.Decoder r0 = com.sec.android.app.voicenote.service.decoder.Decoder.this
                java.io.FileOutputStream r0 = r0.getNextOutputStream()
                r21 = r0
                r0 = r4
                r10 = r0
            L_0x011b:
                if (r0 != 0) goto L_0x0327
                if (r10 >= r13) goto L_0x0327
                boolean r11 = r30.isInterrupted()
                if (r11 != 0) goto L_0x0327
                long r11 = r3.getSampleTime()
                long r11 = r11 / r17
                com.sec.android.app.voicenote.service.Engine r16 = com.sec.android.app.voicenote.service.Engine.getInstance()
                int r13 = r16.getCurrentTime()
                long r14 = (long) r13
                int r13 = (r11 > r14 ? 1 : (r11 == r14 ? 0 : -1))
                r25 = r6
                r5 = 100
                if (r13 < 0) goto L_0x0166
                java.lang.StringBuilder r13 = new java.lang.StringBuilder
                r13.<init>()
                java.lang.String r4 = "decoding too fast sampleTime : "
                r13.append(r4)
                r13.append(r11)
                java.lang.String r4 = " playingTime : "
                r13.append(r4)
                r13.append(r14)
                java.lang.String r4 = r13.toString()
                com.sec.android.app.voicenote.provider.Log.m19d(r9, r4)
                long r11 = r11 - r14
                long r11 = r11 + r5
                android.os.SystemClock.sleep(r11)
                r6 = r25
                r4 = 0
                r5 = 2
                r13 = 50
                r14 = 0
                goto L_0x011b
            L_0x0166:
                r14 = 10000(0x2710, double:4.9407E-320)
                r4 = 1
                if (r0 != 0) goto L_0x0207
                int r27 = r10 + 1
                int r11 = r8.dequeueInputBuffer(r14)
                if (r11 < 0) goto L_0x01eb
                java.nio.ByteBuffer r10 = r8.getInputBuffer(r11)
                r12 = 0
                int r10 = r3.readSampleData(r10, r12)
                if (r10 >= 0) goto L_0x0188
                java.lang.String r0 = "saw input EOS."
                com.sec.android.app.voicenote.provider.Log.m19d(r9, r0)
                r0 = r4
                r13 = 0
                r28 = 0
                goto L_0x018f
            L_0x0188:
                long r12 = r3.getSampleTime()
                r28 = r12
                r13 = r10
            L_0x018f:
                r12 = 0
                if (r0 == 0) goto L_0x0196
                r10 = 4
                r16 = r10
                goto L_0x0198
            L_0x0196:
                r16 = 0
            L_0x0198:
                r10 = r8
                r22 = 50
                r23 = 0
                r14 = r28
                r10.queueInputBuffer(r11, r12, r13, r14, r16)
                int r10 = (r28 > r23 ? 1 : (r28 == r23 ? 0 : -1))
                if (r10 != 0) goto L_0x01b6
                com.sec.android.app.voicenote.service.decoder.Decoder r10 = com.sec.android.app.voicenote.service.decoder.Decoder.this
                long r10 = r10.mProgressTime
                int r10 = (r10 > r23 ? 1 : (r10 == r23 ? 0 : -1))
                if (r10 == 0) goto L_0x01b6
                com.sec.android.app.voicenote.service.decoder.Decoder r10 = com.sec.android.app.voicenote.service.decoder.Decoder.this
                long unused = r10.mProgressTime = r5
                goto L_0x01cd
            L_0x01b6:
                com.sec.android.app.voicenote.service.decoder.Decoder r10 = com.sec.android.app.voicenote.service.decoder.Decoder.this
                long r11 = r10.mCurrentFileStartTime
                long r28 = r28 - r11
                long r28 = r28 * r5
                com.sec.android.app.voicenote.service.decoder.Decoder r5 = com.sec.android.app.voicenote.service.decoder.Decoder.this
                long r5 = r5.mCurrentFileStartTime
                long r5 = r19 - r5
                long r5 = r28 / r5
                long unused = r10.mProgressTime = r5
            L_0x01cd:
                com.sec.android.app.voicenote.service.decoder.Decoder r5 = com.sec.android.app.voicenote.service.decoder.Decoder.this
                com.sec.android.app.voicenote.service.decoder.Decoder$onDecoderListener r5 = r5.mDecoderListener
                if (r5 == 0) goto L_0x01e5
                com.sec.android.app.voicenote.service.decoder.Decoder r5 = com.sec.android.app.voicenote.service.decoder.Decoder.this
                com.sec.android.app.voicenote.service.decoder.Decoder$onDecoderListener r5 = r5.mDecoderListener
                com.sec.android.app.voicenote.service.decoder.Decoder r6 = com.sec.android.app.voicenote.service.decoder.Decoder.this
                long r10 = r6.mProgressTime
                int r6 = (int) r10
                r5.onDecoderProgress(r6)
            L_0x01e5:
                if (r0 != 0) goto L_0x0203
                r3.advance()
                goto L_0x0203
            L_0x01eb:
                r22 = 50
                r23 = 0
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "inputBufIndex "
                r5.append(r6)
                r5.append(r11)
                java.lang.String r5 = r5.toString()
                com.sec.android.app.voicenote.provider.Log.m22e(r9, r5)
            L_0x0203:
                r5 = r0
                r10 = 10000(0x2710, double:4.9407E-320)
                goto L_0x020f
            L_0x0207:
                r22 = 50
                r23 = 0
                r5 = r0
                r27 = r10
                r10 = r14
            L_0x020f:
                int r6 = r8.dequeueOutputBuffer(r7, r10)
                if (r6 < 0) goto L_0x02e3
                int r0 = r7.size
                if (r0 <= 0) goto L_0x021b
                r27 = 0
            L_0x021b:
                java.nio.ByteBuffer r0 = r8.getOutputBuffer(r6)
                int r10 = r7.size
                int r10 = r10 / r25
                java.nio.ByteBuffer r10 = java.nio.ByteBuffer.allocate(r10)
                java.nio.ByteOrder r11 = r0.order()
                r10.order(r11)
                int r11 = r7.size
                int r12 = r11 / 2
                short[] r12 = new short[r12]
                r13 = 2
                int r11 = r11 / r13
                short[] r11 = new short[r11]
                java.nio.ShortBuffer r0 = r0.asShortBuffer()
                r0.get(r12)
                int r0 = r12.length
                int r0 = r0 / r25
                int r0 = r2.exe(r11, r12, r0)
                if (r0 <= 0) goto L_0x02d8
                r12 = r25
                if (r12 != r4) goto L_0x0259
                java.nio.ShortBuffer r4 = r10.asShortBuffer()
                r4.put(r11)
                r28 = r5
                r4 = 0
                r25 = 2
                goto L_0x0286
            L_0x0259:
                short[] r4 = new short[r0]
                int r13 = r0 * 2
                r14 = 0
            L_0x025e:
                if (r14 >= r13) goto L_0x027a
                int r15 = r14 / 2
                short r16 = r11[r14]
                r25 = 2
                int r16 = r16 / 2
                int r26 = r14 + 1
                short r26 = r11[r26]
                int r26 = r26 / 2
                r28 = r5
                int r5 = r16 + r26
                short r5 = (short) r5
                r4[r15] = r5
                int r14 = r14 + 2
                r5 = r28
                goto L_0x025e
            L_0x027a:
                r28 = r5
                r25 = 2
                java.nio.ShortBuffer r5 = r10.asShortBuffer()
                r5.put(r4)
                r4 = 0
            L_0x0286:
                r10.position(r4)
                int r0 = r0 * 2
                r10.limit(r0)
                com.sec.android.app.voicenote.service.recognizer.VoiceWorker r0 = com.sec.android.app.voicenote.service.recognizer.VoiceWorker.getInstance()     // Catch:{ IOException -> 0x02cf }
                int r0 = r0.getPcmFileCount()     // Catch:{ IOException -> 0x02cf }
                com.sec.android.app.voicenote.service.decoder.Decoder r4 = com.sec.android.app.voicenote.service.decoder.Decoder.this     // Catch:{ IOException -> 0x02cf }
                int r4 = r4.index     // Catch:{ IOException -> 0x02cf }
                if (r0 <= r4) goto L_0x02c5
                if (r21 == 0) goto L_0x02a6
                r21.flush()     // Catch:{ IOException -> 0x02cf }
                r21.close()     // Catch:{ IOException -> 0x02cf }
            L_0x02a6:
                com.sec.android.app.voicenote.service.decoder.Decoder r0 = com.sec.android.app.voicenote.service.decoder.Decoder.this     // Catch:{ IOException -> 0x02cf }
                com.sec.android.app.voicenote.service.decoder.Decoder$onDecoderListener r0 = r0.mDecoderListener     // Catch:{ IOException -> 0x02cf }
                if (r0 == 0) goto L_0x02bd
                com.sec.android.app.voicenote.service.decoder.Decoder r0 = com.sec.android.app.voicenote.service.decoder.Decoder.this     // Catch:{ IOException -> 0x02cf }
                com.sec.android.app.voicenote.service.decoder.Decoder$onDecoderListener r0 = r0.mDecoderListener     // Catch:{ IOException -> 0x02cf }
                com.sec.android.app.voicenote.service.decoder.Decoder r4 = com.sec.android.app.voicenote.service.decoder.Decoder.this     // Catch:{ IOException -> 0x02cf }
                java.lang.String r4 = r4.mPcmFileName     // Catch:{ IOException -> 0x02cf }
                r0.onPartialDecodeComplete(r4)     // Catch:{ IOException -> 0x02cf }
            L_0x02bd:
                com.sec.android.app.voicenote.service.decoder.Decoder r0 = com.sec.android.app.voicenote.service.decoder.Decoder.this     // Catch:{ IOException -> 0x02cf }
                java.io.FileOutputStream r0 = r0.getNextOutputStream()     // Catch:{ IOException -> 0x02cf }
                r21 = r0
            L_0x02c5:
                if (r21 == 0) goto L_0x02de
                java.nio.channels.FileChannel r0 = r21.getChannel()     // Catch:{ IOException -> 0x02cf }
                r0.write(r10)     // Catch:{ IOException -> 0x02cf }
                goto L_0x02de
            L_0x02cf:
                r0 = move-exception
                java.lang.String r0 = r0.toString()
                com.sec.android.app.voicenote.provider.Log.m22e(r9, r0)
                goto L_0x02de
            L_0x02d8:
                r28 = r5
                r12 = r25
                r25 = 2
            L_0x02de:
                r4 = 0
                r8.releaseOutputBuffer(r6, r4)
                goto L_0x031a
            L_0x02e3:
                r28 = r5
                r12 = r25
                r4 = 0
                r25 = 2
                r0 = -2
                if (r6 != r0) goto L_0x0306
                android.media.MediaFormat r0 = r8.getOutputFormat()
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "output format has changed to "
                r5.append(r6)
                r5.append(r0)
                java.lang.String r0 = r5.toString()
                com.sec.android.app.voicenote.provider.Log.m19d(r9, r0)
                goto L_0x031a
            L_0x0306:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r5 = "dequeueOutputBuffer returned "
                r0.append(r5)
                r0.append(r6)
                java.lang.String r0 = r0.toString()
                com.sec.android.app.voicenote.provider.Log.m19d(r9, r0)
            L_0x031a:
                r10 = r27
                r6 = r12
                r13 = r22
                r14 = r23
                r5 = r25
                r0 = r28
                goto L_0x011b
            L_0x0327:
                com.sec.android.app.voicenote.service.decoder.Decoder r0 = com.sec.android.app.voicenote.service.decoder.Decoder.this
                com.sec.android.app.voicenote.service.decoder.Decoder$onDecoderListener r0 = r0.mDecoderListener
                if (r0 == 0) goto L_0x033e
                com.sec.android.app.voicenote.service.decoder.Decoder r0 = com.sec.android.app.voicenote.service.decoder.Decoder.this
                com.sec.android.app.voicenote.service.decoder.Decoder$onDecoderListener r0 = r0.mDecoderListener
                com.sec.android.app.voicenote.service.decoder.Decoder r4 = com.sec.android.app.voicenote.service.decoder.Decoder.this
                java.lang.String r4 = r4.mPcmFileName
                r0.onPartialDecodeComplete(r4)
            L_0x033e:
                com.sec.android.app.voicenote.service.decoder.Decoder r0 = com.sec.android.app.voicenote.service.decoder.Decoder.this
                com.sec.android.app.voicenote.service.decoder.Decoder$onDecoderListener r0 = r0.mDecoderListener
                if (r0 == 0) goto L_0x034f
                com.sec.android.app.voicenote.service.decoder.Decoder r0 = com.sec.android.app.voicenote.service.decoder.Decoder.this
                com.sec.android.app.voicenote.service.decoder.Decoder$onDecoderListener r0 = r0.mDecoderListener
                r0.onDecoderStop()
            L_0x034f:
                java.lang.String r0 = "stopping..."
                com.sec.android.app.voicenote.provider.Log.m19d(r9, r0)
                r8.stop()
                r8.release()
                r3.release()
                r2.destroy()
                if (r21 == 0) goto L_0x036f
                r21.close()     // Catch:{ IOException -> 0x0366 }
                goto L_0x036f
            L_0x0366:
                r0 = move-exception
                r2 = r0
                java.lang.String r0 = r2.toString()
                com.sec.android.app.voicenote.provider.Log.m22e(r9, r0)
            L_0x036f:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.decoder.Decoder.DecoderThread.run():void");
        }
    }

    private boolean restartDecoderThread() {
        if (isAliveThread(this.mDecoderThread)) {
            return false;
        }
        interruptThread(this.mDecoderThread);
        this.mDecoderThread = new DecoderThread();
        this.mDecoderThread.start();
        setTranslationState(2);
        this.mIsComplete = false;
        return true;
    }

    public void start(Context context) {
        if (restartDecoderThread()) {
            VoiceWorker.getInstance().setFromFile(true);
            VoiceWorker.getInstance().startSTT(context);
        }
    }

    public void stop() {
        interruptThread(this.mDecoderThread);
        VoiceWorker.getInstance().stopSTT();
        setTranslationState(1);
        this.mIsComplete = false;
        this.mCurrentFileStartTime = -1;
    }

    public void cancel() {
        interruptThread(this.mDecoderThread);
        VoiceWorker.getInstance().cancelSTT();
        setTranslationState(1);
        this.mIsComplete = false;
        this.mCurrentFileStartTime = -1;
    }

    public void pause(boolean z) {
        interruptThread(this.mDecoderThread);
        VoiceWorker.getInstance().pauseSTT();
        setTranslationState(3);
        if (z) {
            this.mProgressTime = SYNC_DELAY;
            this.mIsComplete = true;
        }
    }

    public void resume() {
        if (restartDecoderThread()) {
            VoiceWorker.getInstance().resumeSTT();
        }
    }

    public int getTranslationState() {
        return this.mState;
    }

    public boolean isComplete() {
        return this.mIsComplete;
    }

    private void setTranslationState(int i) {
        Log.m19d(TAG, "setTranslationState : " + i);
        this.mState = i;
    }

    public int getProgressTime() {
        return (int) this.mProgressTime;
    }

    public void registerListener(onDecoderListener ondecoderlistener) {
        this.mDecoderListener = ondecoderlistener;
    }

    public void unregisterListener() {
        this.mDecoderListener = null;
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
}
