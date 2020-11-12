package org.xiph.speex;

public class Filters {
    private int last_pitch;
    private float[] last_pitch_gain = new float[3];
    private float smooth_gain;

    /* renamed from: xx */
    private float[] f114xx = new float[1024];

    public static final void bw_lpc(float f, float[] fArr, float[] fArr2, int i) {
        float f2 = 1.0f;
        for (int i2 = 0; i2 < i + 1; i2++) {
            fArr2[i2] = fArr[i2] * f2;
            f2 *= f;
        }
    }

    public static final void filter_mem2(float[] fArr, int i, float[] fArr2, float[] fArr3, float[] fArr4, int i2, int i3, int i4, float[] fArr5, int i5) {
        int i6 = i3;
        for (int i7 = 0; i7 < i6; i7++) {
            float f = fArr[i + i7];
            int i8 = i2 + i7;
            fArr4[i8] = (fArr2[0] * f) + fArr5[0];
            float f2 = fArr4[i8];
            int i9 = 0;
            while (i9 < i4 - 1) {
                int i10 = i5 + i9;
                i9++;
                fArr5[i10] = (fArr5[i10 + 1] + (fArr2[i9] * f)) - (fArr3[i9] * f2);
            }
            fArr5[(i5 + i4) - 1] = (fArr2[i4] * f) - (fArr3[i4] * f2);
        }
    }

    public static final void fir_mem2(float[] fArr, int i, float[] fArr2, float[] fArr3, int i2, int i3, int i4, float[] fArr4) {
        int i5;
        for (int i6 = 0; i6 < i3; i6++) {
            float f = fArr[i + i6];
            fArr3[i2 + i6] = (fArr2[0] * f) + fArr4[0];
            int i7 = 0;
            while (true) {
                i5 = i4 - 1;
                if (i7 >= i5) {
                    break;
                }
                int i8 = i7 + 1;
                fArr4[i7] = fArr4[i8] + (fArr2[i8] * f);
                i7 = i8;
            }
            fArr4[i5] = fArr2[i4] * f;
        }
    }

    public static final void iir_mem2(float[] fArr, int i, float[] fArr2, float[] fArr3, int i2, int i3, int i4, float[] fArr4) {
        int i5;
        for (int i6 = 0; i6 < i3; i6++) {
            int i7 = i2 + i6;
            fArr3[i7] = fArr[i + i6] + fArr4[0];
            int i8 = 0;
            while (true) {
                i5 = i4 - 1;
                if (i8 >= i5) {
                    break;
                }
                int i9 = i8 + 1;
                fArr4[i8] = fArr4[i9] - (fArr2[i9] * fArr3[i7]);
                i8 = i9;
            }
            fArr4[i5] = (-fArr2[i4]) * fArr3[i7];
        }
    }

    public static final void qmf_decomp(float[] fArr, float[] fArr2, float[] fArr3, float[] fArr4, int i, int i2, float[] fArr5) {
        int i3 = i;
        int i4 = i2;
        float[] fArr6 = new float[i4];
        float[] fArr7 = new float[((i3 + i4) - 1)];
        int i5 = i4 - 1;
        int i6 = i4 >> 1;
        for (int i7 = 0; i7 < i4; i7++) {
            fArr6[(i4 - i7) - 1] = fArr2[i7];
        }
        for (int i8 = 0; i8 < i5; i8++) {
            fArr7[i8] = fArr5[(i4 - i8) - 2];
        }
        for (int i9 = 0; i9 < i3; i9++) {
            fArr7[(i9 + i4) - 1] = fArr[i9];
        }
        int i10 = 0;
        int i11 = 0;
        while (i10 < i3) {
            fArr3[i11] = 0.0f;
            fArr4[i11] = 0.0f;
            int i12 = 0;
            while (i12 < i6) {
                int i13 = i10 + i12;
                int i14 = i5 + i10;
                int i15 = i14 - i12;
                fArr3[i11] = fArr3[i11] + (fArr6[i12] * (fArr7[i13] + fArr7[i15]));
                fArr4[i11] = fArr4[i11] - (fArr6[i12] * (fArr7[i13] - fArr7[i15]));
                int i16 = i12 + 1;
                int i17 = i10 + i16;
                int i18 = i14 - i16;
                fArr3[i11] = fArr3[i11] + (fArr6[i16] * (fArr7[i17] + fArr7[i18]));
                fArr4[i11] = fArr4[i11] + (fArr6[i16] * (fArr7[i17] - fArr7[i18]));
                i12 = i16 + 1;
            }
            i10 += 2;
            i11++;
        }
        for (int i19 = 0; i19 < i5; i19++) {
            fArr5[i19] = fArr[(i3 - i19) - 1];
        }
    }

    public static final void residue_percep_zero(float[] fArr, int i, float[] fArr2, float[] fArr3, float[] fArr4, float[] fArr5, int i2, int i3) {
        int i4 = i3;
        float[] fArr6 = new float[i4];
        filter_mem2(fArr, i, fArr2, fArr3, fArr5, 0, i2, i3, fArr6, 0);
        for (int i5 = 0; i5 < i4; i5++) {
            fArr6[i5] = 0.0f;
        }
        fir_mem2(fArr5, 0, fArr4, fArr5, 0, i2, i3, fArr6);
    }

    public static final void syn_percep_zero(float[] fArr, int i, float[] fArr2, float[] fArr3, float[] fArr4, float[] fArr5, int i2, int i3) {
        int i4 = i3;
        float[] fArr6 = new float[i4];
        filter_mem2(fArr, i, fArr3, fArr2, fArr5, 0, i2, i3, fArr6, 0);
        for (int i5 = 0; i5 < i4; i5++) {
            fArr6[i5] = 0.0f;
        }
        iir_mem2(fArr5, 0, fArr4, fArr5, 0, i2, i3, fArr6);
    }

    public void fir_mem_up(float[] fArr, float[] fArr2, float[] fArr3, int i, int i2, float[] fArr4) {
        int i3;
        int i4 = i;
        int i5 = i2;
        int i6 = 0;
        while (true) {
            int i7 = i4 / 2;
            if (i6 >= i7) {
                break;
            }
            this.f114xx[i6 * 2] = fArr[(i7 - 1) - i6];
            i6++;
        }
        int i8 = 0;
        while (true) {
            i3 = i5 - 1;
            if (i8 >= i3) {
                break;
            }
            this.f114xx[i4 + i8] = fArr4[i8 + 1];
            i8 += 2;
        }
        for (int i9 = 0; i9 < i4; i9 += 4) {
            float f = 0.0f;
            float f2 = 0.0f;
            float f3 = 0.0f;
            float f4 = this.f114xx[(i4 - 4) - i9];
            int i10 = 0;
            float f5 = 0.0f;
            while (i10 < i5) {
                float f6 = fArr2[i10];
                float f7 = fArr2[i10 + 1];
                float[] fArr5 = this.f114xx;
                float f8 = fArr5[((i4 - 2) + i10) - i9];
                float f9 = f5 + (f6 * f8);
                float f10 = f2 + (f6 * f4);
                float f11 = f3 + (f7 * f4);
                float f12 = fArr2[i10 + 2];
                float f13 = fArr2[i10 + 3];
                float f14 = fArr5[(i4 + i10) - i9];
                f5 = f9 + (f12 * f14);
                f = f + (f7 * f8) + (f13 * f14);
                f2 = f10 + (f12 * f8);
                f3 = f11 + (f13 * f8);
                i10 += 4;
                f4 = f14;
            }
            fArr3[i9] = f5;
            fArr3[i9 + 1] = f;
            fArr3[i9 + 2] = f2;
            fArr3[i9 + 3] = f3;
        }
        for (int i11 = 0; i11 < i3; i11 += 2) {
            fArr4[i11 + 1] = this.f114xx[i11];
        }
    }

    public void init() {
        this.last_pitch = 0;
        float[] fArr = this.last_pitch_gain;
        fArr[2] = 0.0f;
        fArr[1] = 0.0f;
        fArr[0] = 0.0f;
        this.smooth_gain = 1.0f;
    }
}
