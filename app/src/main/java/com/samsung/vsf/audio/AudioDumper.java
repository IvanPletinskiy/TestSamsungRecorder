package com.samsung.vsf.audio;

import android.os.Environment;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

class AudioDumper {
    private BufferedOutputStream mByteStream;
    private int mCount = 1;
    private int mDataSize;
    private String mFilename;
    private int mSamplingRate;
    private WavFileWriter mWave;

    AudioDumper(int i) {
        this.mSamplingRate = i;
        new File(Environment.getExternalStorageDirectory(), "audio_dumps_svoice").mkdirs();
    }

    /* access modifiers changed from: package-private */
    public void close() {
        WavFileWriter wavFileWriter = this.mWave;
        if (wavFileWriter != null) {
            wavFileWriter.rewriteSize(this.mDataSize);
            this.mWave.close();
            this.mWave = null;
        }
        BufferedOutputStream bufferedOutputStream = this.mByteStream;
        if (bufferedOutputStream != null) {
            try {
                bufferedOutputStream.close();
                this.mByteStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.mFilename = null;
    }

    /* access modifiers changed from: package-private */
    public void dumpWave(byte[] bArr, boolean z) {
        if (this.mWave == null) {
            this.mWave = new WavFileWriter(makeFile("wav").getPath(), this.mSamplingRate, 1, 16);
            this.mDataSize = 0;
        }
        this.mWave.appendPayload(bArr);
        this.mDataSize += bArr.length;
        if (z) {
            this.mWave.rewriteSize(this.mDataSize);
            this.mWave.close();
            this.mWave = null;
            this.mFilename = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void dumpSpeex(byte[] bArr, boolean z) {
        dumpAudio(bArr, z, "spx");
    }

    /* access modifiers changed from: package-private */
    public void dumpOpus(byte[] bArr, boolean z) {
        dumpAudio(bArr, z, "opus");
    }

    private void dumpAudio(byte[] bArr, boolean z, String str) {
        if (this.mByteStream == null) {
            try {
                this.mByteStream = new BufferedOutputStream(new FileOutputStream(makeFile(str), true));
            } catch (IOException e) {
                e.printStackTrace();
                this.mByteStream = null;
            }
        }
        BufferedOutputStream bufferedOutputStream = this.mByteStream;
        if (bufferedOutputStream != null) {
            try {
                bufferedOutputStream.write(bArr);
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            if (z) {
                try {
                    this.mByteStream.close();
                    this.mByteStream = null;
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
                this.mFilename = null;
            }
        }
    }

    private File makeFile(String str) {
        if (this.mFilename == null) {
            this.mFilename = "audio_dumps_svoice/" + this.mCount + ".";
            this.mCount = this.mCount + 1;
        }
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        return new File(externalStorageDirectory, this.mFilename + str);
    }
}
