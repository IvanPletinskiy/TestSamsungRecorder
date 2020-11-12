package org.xiph.speex;

public abstract class LspQuant implements Codebook {
    protected LspQuant() {
    }

    protected static int lsp_quant(float[] fArr, int i, int[] iArr, int i2, int i3) {
        float f = 0.0f;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        while (i4 < i2) {
            float f2 = 0.0f;
            int i7 = i6;
            int i8 = 0;
            while (i8 < i3) {
                float f3 = fArr[i + i8] - ((float) iArr[i7]);
                f2 += f3 * f3;
                i8++;
                i7++;
            }
            if (f2 < f || i4 == 0) {
                i5 = i4;
                f = f2;
            }
            i4++;
            i6 = i7;
        }
        for (int i9 = 0; i9 < i3; i9++) {
            int i10 = i + i9;
            fArr[i10] = fArr[i10] - ((float) iArr[(i5 * i3) + i9]);
        }
        return i5;
    }

    protected static int lsp_weight_quant(float[] fArr, int i, float[] fArr2, int i2, int[] iArr, int i3, int i4) {
        int i5 = i4;
        int i6 = i3;
        float f = 0.0f;
        int i7 = 0;
        int i8 = 0;
        int i9 = 0;
        while (i7 < i6) {
            float f2 = 0.0f;
            int i10 = i9;
            int i11 = 0;
            while (i11 < i5) {
                float f3 = fArr[i + i11] - ((float) iArr[i10]);
                f2 += fArr2[i2 + i11] * f3 * f3;
                i11++;
                i10++;
            }
            if (f2 < f || i7 == 0) {
                i8 = i7;
                f = f2;
            }
            i7++;
            i9 = i10;
        }
        for (int i12 = 0; i12 < i5; i12++) {
            int i13 = i + i12;
            fArr[i13] = fArr[i13] - ((float) iArr[(i8 * i5) + i12]);
        }
        return i8;
    }

    public abstract void quant(float[] fArr, float[] fArr2, int i, Bits bits);
}
