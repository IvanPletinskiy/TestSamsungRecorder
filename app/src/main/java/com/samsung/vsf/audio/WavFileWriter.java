package com.samsung.vsf.audio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

class WavFileWriter {
    private int channels;
    private String filename;
    private RandomAccessFile randomAccessWriter = null;
    private int sampleRate;
    private int sampleSizeInBits;

    WavFileWriter(String str, int i, int i2, int i3) {
        this.filename = str;
        this.sampleRate = i;
        this.channels = i2;
        this.sampleSizeInBits = i3;
        prepareFile();
    }

    private void prepareFile() {
        try {
            open();
            writeHeader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void open() throws FileNotFoundException {
        this.randomAccessWriter = new RandomAccessFile(this.filename, "rw");
    }

    private void writeHeader() {
        int i = this.sampleRate;
        int i2 = this.channels;
        int i3 = this.sampleSizeInBits;
        int i4 = ((i * i2) * i3) / 8;
        int i5 = (i2 * i3) / 8;
        try {
            this.randomAccessWriter.setLength(0);
            this.randomAccessWriter.writeBytes("RIFF");
            this.randomAccessWriter.writeInt(0);
            this.randomAccessWriter.writeBytes("WAVE");
            this.randomAccessWriter.writeBytes("fmt ");
            this.randomAccessWriter.writeInt(Integer.reverseBytes(16));
            this.randomAccessWriter.writeShort(Short.reverseBytes((short) 1));
            this.randomAccessWriter.writeShort(Short.reverseBytes((short) this.channels));
            this.randomAccessWriter.writeInt(Integer.reverseBytes(this.sampleRate));
            this.randomAccessWriter.writeInt(Integer.reverseBytes(i4));
            this.randomAccessWriter.writeShort(Short.reverseBytes((short) i5));
            this.randomAccessWriter.writeShort(Short.reverseBytes((short) this.sampleSizeInBits));
            this.randomAccessWriter.writeBytes("data");
            this.randomAccessWriter.writeInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: package-private */
    public void appendPayload(byte[] bArr) {
        try {
            this.randomAccessWriter.write(bArr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: package-private */
    public void rewriteSize(int i) {
        int i2 = 28 + i + 4;
        try {
            this.randomAccessWriter.seek(4);
            this.randomAccessWriter.writeInt(i2);
            this.randomAccessWriter.seek(40);
            this.randomAccessWriter.writeInt(Integer.reverseBytes(i));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: package-private */
    public void close() {
        try {
            if (this.randomAccessWriter != null) {
                this.randomAccessWriter.close();
                this.randomAccessWriter = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
