package com.samsung.vsf.recognition;

public class BufferObject {
    private byte[] buffer;
    private boolean isEPD;

    public BufferObject(byte[] bArr, boolean z) {
        this.buffer = bArr;
        this.isEPD = z;
    }

    public byte[] getByteBuffer() {
        return this.buffer;
    }

    public boolean isEPDDetected() {
        return this.isEPD;
    }
}
