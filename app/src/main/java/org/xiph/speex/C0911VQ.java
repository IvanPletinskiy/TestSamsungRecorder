package org.xiph.speex;

/* renamed from: org.xiph.speex.VQ */
public class C0911VQ {
    public static final int index(float f, float[] fArr, int i) {
        float f2 = 0.0f;
        int i2 = 0;
        for (int i3 = 0; i3 < i; i3++) {
            float f3 = f - fArr[i3];
            float f4 = f3 * f3;
            if (i3 == 0 || f4 < f2) {
                i2 = i3;
                f2 = f4;
            }
        }
        return i2;
    }

    public static final void nbest(float[] fArr, int i, float[] fArr2, int i2, int i3, float[] fArr3, int i4, int[] iArr, float[] fArr4) {
        int i5 = i4;
        int i6 = i3;
        int i7 = 0;
        int i8 = 0;
        int i9 = 0;
        while (i7 < i6) {
            float f = fArr3[i7] * 0.5f;
            int i10 = 0;
            int i11 = i8;
            int i12 = i2;
            while (i10 < i12) {
                f -= fArr[i + i10] * fArr2[i11];
                i10++;
                i11++;
            }
            if (i7 < i5 || f < fArr4[i5 - 1]) {
                int i13 = i5 - 1;
                while (i13 >= 1 && (i13 > i9 || f < fArr4[i13 - 1])) {
                    int i14 = i13 - 1;
                    fArr4[i13] = fArr4[i14];
                    iArr[i13] = iArr[i14];
                    i13--;
                }
                fArr4[i13] = f;
                iArr[i13] = i7;
                i9++;
            }
            i7++;
            i8 = i11;
        }
    }

    public static final void nbest_sign(float[] fArr, int i, float[] fArr2, int i2, int i3, float[] fArr3, int i4, int[] iArr, float[] fArr4) {
        boolean z;
        int i5 = i3;
        int i6 = i4;
        int i7 = 0;
        int i8 = 0;
        int i9 = 0;
        while (i7 < i5) {
            int i10 = 0;
            int i11 = i8;
            float f = 0.0f;
            int i12 = i2;
            while (i10 < i12) {
                f -= fArr[i + i10] * fArr2[i11];
                i10++;
                i11++;
            }
            if (f > 0.0f) {
                f = -f;
                z = true;
            } else {
                z = false;
            }
            float f2 = (float) (((double) f) + (((double) fArr3[i7]) * 0.5d));
            if (i7 < i6 || f2 < fArr4[i6 - 1]) {
                int i13 = i6 - 1;
                while (i13 >= 1 && (i13 > i9 || f2 < fArr4[i13 - 1])) {
                    int i14 = i13 - 1;
                    fArr4[i13] = fArr4[i14];
                    iArr[i13] = iArr[i14];
                    i13--;
                }
                fArr4[i13] = f2;
                iArr[i13] = i7;
                i9++;
                if (z) {
                    iArr[i13] = iArr[i13] + i5;
                }
            }
            i7++;
            i8 = i11;
        }
    }
}
