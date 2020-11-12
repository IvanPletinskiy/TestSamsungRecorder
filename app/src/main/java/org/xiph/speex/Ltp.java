package org.xiph.speex;

public abstract class Ltp {
    protected static float inner_prod(float[] fArr, int i, float[] fArr2, int i2, int i3) {
        float f = 0.0f;
        float f2 = 0.0f;
        float f3 = 0.0f;
        float f4 = 0.0f;
        for (int i4 = 0; i4 < i3; i4 += 4) {
            int i5 = i + i4;
            int i6 = i2 + i4;
            f += fArr[i5] * fArr2[i6];
            f2 += fArr[i5 + 1] * fArr2[i6 + 1];
            f3 += fArr[i5 + 2] * fArr2[i6 + 2];
            f4 += fArr[i5 + 3] * fArr2[i6 + 3];
        }
        return f + f2 + f3 + f4;
    }

    protected static void open_loop_nbest_pitch(float[] fArr, int i, int i2, int i3, int i4, int[] iArr, float[] fArr2, int i5) {
        float[] fArr3 = fArr;
        int i6 = i;
        int i7 = i3;
        int i8 = i4;
        int i9 = i5;
        float[] fArr4 = new float[i9];
        int i10 = i7 - i2;
        int i11 = i10 + 1;
        float[] fArr5 = new float[i11];
        float[] fArr6 = new float[(i10 + 2)];
        float[] fArr7 = new float[i11];
        for (int i12 = 0; i12 < i9; i12++) {
            fArr4[i12] = -1.0f;
            fArr2[i12] = 0.0f;
            iArr[i12] = i2;
        }
        int i13 = i6 - i2;
        fArr6[0] = inner_prod(fArr3, i13, fArr3, i13, i8);
        float inner_prod = inner_prod(fArr3, i6, fArr3, i6, i8);
        for (int i14 = i2; i14 <= i7; i14++) {
            int i15 = i14 - i2;
            int i16 = i15 + 1;
            int i17 = i6 - i14;
            int i18 = i17 - 1;
            int i19 = (i17 + i8) - 1;
            fArr6[i16] = (fArr6[i15] + (fArr3[i18] * fArr3[i18])) - (fArr3[i19] * fArr3[i19]);
            if (fArr6[i16] < 1.0f) {
                fArr6[i16] = 1.0f;
            }
        }
        for (int i20 = i2; i20 <= i7; i20++) {
            int i21 = i20 - i2;
            fArr5[i21] = 0.0f;
            fArr7[i21] = 0.0f;
        }
        for (int i22 = i2; i22 <= i7; i22++) {
            int i23 = i22 - i2;
            fArr5[i23] = inner_prod(fArr3, i6, fArr3, i6 - i22, i8);
            fArr7[i23] = (fArr5[i23] * fArr5[i23]) / (fArr6[i23] + 1.0f);
        }
        for (int i24 = i2; i24 <= i7; i24++) {
            int i25 = i24 - i2;
            int i26 = i9 - 1;
            if (fArr7[i25] > fArr4[i26]) {
                float f = fArr5[i25] / (fArr6[i25] + 10.0f);
                float sqrt = (float) Math.sqrt((double) ((fArr5[i25] * f) / (10.0f + inner_prod)));
                if (sqrt > f) {
                    sqrt = f;
                }
                if (sqrt < 0.0f) {
                    sqrt = 0.0f;
                }
                int i27 = 0;
                while (true) {
                    if (i27 >= i9) {
                        break;
                    } else if (fArr7[i25] > fArr4[i27]) {
                        while (i26 > i27) {
                            int i28 = i26 - 1;
                            fArr4[i26] = fArr4[i28];
                            iArr[i26] = iArr[i28];
                            fArr2[i26] = fArr2[i28];
                            i26--;
                        }
                        fArr4[i27] = fArr7[i25];
                        iArr[i27] = i24;
                        fArr2[i27] = sqrt;
                    } else {
                        i27++;
                    }
                }
            }
        }
    }

    public abstract int quant(float[] fArr, float[] fArr2, int i, float[] fArr3, float[] fArr4, float[] fArr5, float[] fArr6, int i2, int i3, int i4, float f, int i5, int i6, Bits bits, float[] fArr7, int i7, float[] fArr8, int i8);
}
