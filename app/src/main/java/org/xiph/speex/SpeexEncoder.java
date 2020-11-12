package org.xiph.speex;

public class SpeexEncoder {
    private Bits bits = new Bits();
    private int channels;
    private Encoder encoder;
    private int frameSize;
    private float[] rawData;
    private int sampleRate;

    public static void mapPcm16bitLittleEndian2Float(byte[] bArr, int i, float[] fArr, int i2, int i3) {
        if (bArr.length - i < i3 * 2) {
            throw new IllegalArgumentException("Insufficient Samples to convert to floats");
        } else if (fArr.length - i2 >= i3) {
            for (int i4 = 0; i4 < i3; i4++) {
                int i5 = (i4 * 2) + i;
                fArr[i2 + i4] = (float) ((bArr[i5 + 1] << 8) | (bArr[i5] & 255));
            }
        } else {
            throw new IllegalArgumentException("Insufficient float buffer to convert the samples");
        }
    }

    public Encoder getEncoder() {
        return this.encoder;
    }

    public int getFrameSize() {
        return this.frameSize;
    }

    public int getProcessedData(byte[] bArr, int i) {
        int bufferSize = this.bits.getBufferSize();
        System.arraycopy(this.bits.getBuffer(), 0, bArr, i, bufferSize);
        this.bits.init();
        return bufferSize;
    }

    public int getProcessedDataByteSize() {
        return this.bits.getBufferSize();
    }

    public boolean init(int i, int i2, int i3, int i4) {
        if (i == 0) {
            this.encoder = new NbEncoder();
            ((NbEncoder) this.encoder).nbinit();
        } else if (i == 1) {
            this.encoder = new SbEncoder();
            ((SbEncoder) this.encoder).wbinit();
        } else if (i != 2) {
            return false;
        } else {
            this.encoder = new SbEncoder();
            ((SbEncoder) this.encoder).uwbinit();
        }
        this.encoder.setQuality(i2);
        this.frameSize = this.encoder.getFrameSize();
        this.sampleRate = i3;
        this.channels = i4;
        this.rawData = new float[(i4 * this.frameSize)];
        this.bits.init();
        return true;
    }

    public boolean processData(byte[] bArr, int i, int i2) {
        int i3 = i2 / 2;
        mapPcm16bitLittleEndian2Float(bArr, i, this.rawData, 0, i3);
        return processData(this.rawData, i3);
    }

    public boolean processData(float[] fArr, int i) {
        int i2 = this.channels;
        int i3 = this.frameSize;
        int i4 = i2 * i3;
        if (i == i4) {
            if (i2 == 2) {
                Stereo.encode(this.bits, fArr, i3);
            }
            this.encoder.encode(this.bits, fArr);
            return true;
        }
        throw new IllegalArgumentException("SpeexEncoder requires " + i4 + " samples to process a Frame, not " + i);
    }
}
