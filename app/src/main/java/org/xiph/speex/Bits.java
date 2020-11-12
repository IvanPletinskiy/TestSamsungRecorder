package org.xiph.speex;

public class Bits {
    private int bitPtr;
    private int bytePtr;
    private byte[] bytes;
    int origLen;

    public byte[] getBuffer() {
        return this.bytes;
    }

    public int getBufferSize() {
        return this.bytePtr + (this.bitPtr > 0 ? 1 : 0);
    }

    public void init() {
        this.bytes = new byte[1024];
        this.bytePtr = 0;
        this.bitPtr = 0;
        this.origLen = 0;
    }

    public void pack(int i, int i2) {
        while (true) {
            int i3 = this.bytePtr + ((this.bitPtr + i2) >> 3);
            byte[] bArr = this.bytes;
            if (i3 < bArr.length) {
                break;
            }
            byte[] bArr2 = new byte[(bArr.length * 2)];
            System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
            this.bytes = bArr2;
        }
        while (i2 > 0) {
            byte[] bArr3 = this.bytes;
            int i4 = this.bytePtr;
            byte b = bArr3[i4];
            int i5 = this.bitPtr;
            bArr3[i4] = (byte) ((((i >> (i2 - 1)) & 1) << (7 - i5)) | b);
            this.bitPtr = i5 + 1;
            if (this.bitPtr == 8) {
                this.bitPtr = 0;
                this.bytePtr = i4 + 1;
            }
            i2--;
        }
    }
}
