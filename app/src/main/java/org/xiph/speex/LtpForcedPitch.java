package org.xiph.speex;

public class LtpForcedPitch extends Ltp {
    public final int quant(float[] fArr, float[] fArr2, int i, float[] fArr3, float[] fArr4, float[] fArr5, float[] fArr6, int i2, int i3, int i4, float f, int i5, int i6, Bits bits, float[] fArr7, int i7, float[] fArr8, int i8) {
        float f2 = 0.99f;
        if (f <= 0.99f) {
            f2 = f;
        }
        int i9 = i6;
        for (int i10 = 0; i10 < i9; i10++) {
            int i11 = i2 + i10;
            fArr6[i11] = fArr6[i11 - i3] * f2;
        }
        return i3;
    }
}
