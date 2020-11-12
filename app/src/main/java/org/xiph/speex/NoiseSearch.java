package org.xiph.speex;

public class NoiseSearch extends CbSearch {
    public final void quant(float[] fArr, float[] fArr2, float[] fArr3, float[] fArr4, int i, int i2, float[] fArr5, int i3, float[] fArr6, Bits bits, int i4) {
        int i5 = i2;
        float[] fArr7 = new float[i5];
        Filters.residue_percep_zero(fArr, 0, fArr2, fArr3, fArr4, fArr7, i2, i);
        for (int i6 = 0; i6 < i5; i6++) {
            int i7 = i3 + i6;
            fArr5[i7] = fArr5[i7] + fArr7[i6];
        }
        for (int i8 = 0; i8 < i5; i8++) {
            fArr[i8] = 0.0f;
        }
    }
}
