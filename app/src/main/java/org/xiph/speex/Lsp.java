package org.xiph.speex;

public class Lsp {

    /* renamed from: pw */
    private float[] f115pw = new float[42];

    public static final float cheb_poly_eva(float[] fArr, float f, int i) {
        int i2 = i >> 1;
        float[] fArr2 = new float[(i2 + 1)];
        fArr2[0] = 1.0f;
        fArr2[1] = f;
        float f2 = fArr[i2] + (fArr[i2 - 1] * f);
        float f3 = f * 2.0f;
        for (int i3 = 2; i3 <= i2; i3++) {
            fArr2[i3] = (fArr2[i3 - 1] * f3) - fArr2[i3 - 2];
            f2 += fArr[i2 - i3] * fArr2[i3];
        }
        return f2;
    }

    public static void enforce_margin(float[] fArr, int i, float f) {
        if (fArr[0] < f) {
            fArr[0] = f;
        }
        int i2 = 1;
        int i3 = i - 1;
        float f2 = 3.1415927f - f;
        if (fArr[i3] > f2) {
            fArr[i3] = f2;
        }
        while (i2 < i3) {
            int i4 = i2 - 1;
            if (fArr[i2] < fArr[i4] + f) {
                fArr[i2] = fArr[i4] + f;
            }
            int i5 = i2 + 1;
            if (fArr[i2] > fArr[i5] - f) {
                fArr[i2] = ((fArr[i2] + fArr[i5]) - f) * 0.5f;
            }
            i2 = i5;
        }
    }

    public static int lpc2lsp(float[] fArr, int i, float[] fArr2, int i2, float f) {
        float[] fArr3;
        float[] fArr4;
        int i3 = i;
        int i4 = i3 / 2;
        int i5 = i4 + 1;
        float[] fArr5 = new float[i5];
        float[] fArr6 = new float[i5];
        fArr6[0] = 1.0f;
        fArr5[0] = 1.0f;
        boolean z = true;
        int i6 = 0;
        int i7 = 0;
        int i8 = 1;
        int i9 = 1;
        int i10 = 1;
        while (i8 <= i4) {
            int i11 = (i3 + 1) - i8;
            fArr6[i9] = (fArr[i8] + fArr[i11]) - fArr6[i6];
            fArr5[i10] = (fArr[i8] - fArr[i11]) + fArr5[i7];
            i8++;
            i10++;
            i9++;
            i7++;
            i6++;
        }
        int i12 = 0;
        int i13 = 0;
        for (int i14 = 0; i14 < i4; i14++) {
            fArr6[i12] = fArr6[i12] * 2.0f;
            fArr5[i13] = fArr5[i13] * 2.0f;
            i12++;
            i13++;
        }
        float f2 = 0.0f;
        float f3 = 0.0f;
        float f4 = 1.0f;
        int i15 = 0;
        int i16 = 0;
        while (i15 < i3) {
            float[] fArr7 = i15 % 2 != 0 ? fArr5 : fArr6;
            float cheb_poly_eva = cheb_poly_eva(fArr7, f4, i3);
            float f5 = f3;
            float f6 = f4;
            int i17 = i16;
            boolean z2 = z;
            while (z2 == z && ((double) f2) >= -1.0d) {
                float[] fArr8 = fArr7;
                double d = (double) f6;
                float f7 = (float) (((double) f) * (1.0d - ((0.9d * d) * d)));
                if (((double) Math.abs(cheb_poly_eva)) < 0.2d) {
                    f7 = (float) (((double) f7) * 0.5d);
                }
                float f8 = f6 - f7;
                float[] fArr9 = fArr8;
                float cheb_poly_eva2 = cheb_poly_eva(fArr9, f8, i3);
                if (((double) (cheb_poly_eva2 * cheb_poly_eva)) < 0.0d) {
                    i17++;
                    int i18 = i2;
                    int i19 = 0;
                    while (i19 <= i18) {
                        f5 = (f6 + f8) / 2.0f;
                        float cheb_poly_eva3 = cheb_poly_eva(fArr9, f5, i3);
                        float[] fArr10 = fArr6;
                        float[] fArr11 = fArr5;
                        if (((double) (cheb_poly_eva3 * cheb_poly_eva)) > 0.0d) {
                            cheb_poly_eva = cheb_poly_eva3;
                            f6 = f5;
                        } else {
                            f8 = f5;
                        }
                        i19++;
                        fArr5 = fArr11;
                        fArr6 = fArr10;
                    }
                    fArr3 = fArr6;
                    fArr4 = fArr5;
                    fArr2[i15] = f5;
                    f2 = f8;
                    f6 = f5;
                    z2 = false;
                } else {
                    int i20 = i2;
                    fArr3 = fArr6;
                    fArr4 = fArr5;
                    f6 = f8;
                    cheb_poly_eva = cheb_poly_eva2;
                    f2 = f6;
                }
                fArr7 = fArr9;
                fArr5 = fArr4;
                fArr6 = fArr3;
                z = true;
            }
            int i21 = i2;
            float f9 = f;
            i15++;
            i16 = i17;
            f4 = f6;
            f3 = f5;
            fArr5 = fArr5;
            fArr6 = fArr6;
            z = true;
        }
        return i16;
    }

    public void lsp2lpc(float[] fArr, float[] fArr2, int i) {
        int i2 = i;
        int i3 = i2 / 2;
        for (int i4 = 0; i4 < (i3 * 4) + 2; i4++) {
            this.f115pw[i4] = 0.0f;
        }
        float f = 1.0f;
        float f2 = 1.0f;
        int i5 = 0;
        int i6 = 0;
        while (i5 <= i2) {
            float f3 = f2;
            int i7 = 0;
            float f4 = f;
            int i8 = 0;
            while (i8 < i3) {
                int i9 = i8 * 4;
                int i10 = i9 + 1;
                int i11 = i10 + 1;
                int i12 = i11 + 1;
                float[] fArr3 = this.f115pw;
                float f5 = (f4 - ((fArr[i7] * 2.0f) * fArr3[i9])) + fArr3[i10];
                float f6 = (f3 - ((fArr[i7 + 1] * 2.0f) * fArr3[i11])) + fArr3[i12];
                fArr3[i10] = fArr3[i9];
                fArr3[i12] = fArr3[i11];
                fArr3[i9] = f4;
                fArr3[i11] = f3;
                i8++;
                i7 += 2;
                i6 = i12;
                f4 = f5;
                f3 = f6;
            }
            float[] fArr4 = this.f115pw;
            int i13 = i6 + 1;
            int i14 = i6 + 2;
            fArr2[i5] = (fArr4[i13] + f4 + (f3 - fArr4[i14])) * 0.5f;
            fArr4[i13] = f4;
            fArr4[i14] = f3;
            i5++;
            f = 0.0f;
            f2 = 0.0f;
        }
    }
}
