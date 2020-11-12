package org.xiph.speex;

public class Lpc {
    public static void autocorr(float[] fArr, float[] fArr2, int i, int i2) {
        while (true) {
            int i3 = i - 1;
            if (i > 0) {
                float f = 0.0f;
                for (int i4 = i3; i4 < i2; i4++) {
                    f += fArr[i4] * fArr[i4 - i3];
                }
                fArr2[i3] = f;
                i = i3;
            } else {
                return;
            }
        }
    }

    public static float wld(float[] fArr, float[] fArr2, float[] fArr3, int i) {
        float f = fArr2[0];
        if (fArr2[0] == 0.0f) {
            for (int i2 = 0; i2 < i; i2++) {
                fArr3[i2] = 0.0f;
            }
            return 0.0f;
        }
        float f2 = f;
        int i3 = 0;
        while (i3 < i) {
            int i4 = i3 + 1;
            float f3 = -fArr2[i4];
            for (int i5 = 0; i5 < i3; i5++) {
                f3 -= fArr[i5] * fArr2[i3 - i5];
            }
            float f4 = f3 / f2;
            fArr3[i3] = f4;
            fArr[i3] = f4;
            int i6 = 0;
            while (i6 < i3 / 2) {
                float f5 = fArr[i6];
                int i7 = (i3 - 1) - i6;
                fArr[i6] = fArr[i6] + (fArr[i7] * f4);
                fArr[i7] = fArr[i7] + (f5 * f4);
                i6++;
            }
            if (i3 % 2 != 0) {
                fArr[i6] = fArr[i6] + (fArr[i6] * f4);
            }
            f2 = (float) (((double) f2) * (1.0d - ((double) (f4 * f4))));
            i3 = i4;
        }
        return f2;
    }
}
