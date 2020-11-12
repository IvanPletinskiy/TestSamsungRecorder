package org.xiph.speex;

public class Stereo {
    public static final float[] e_ratio_quant = {0.25f, 0.315f, 0.397f, 0.5f};

    public static void encode(Bits bits, float[] fArr, int i) {
        float f = 0.0f;
        float f2 = 0.0f;
        float f3 = 0.0f;
        for (int i2 = 0; i2 < i; i2++) {
            int i3 = i2 * 2;
            f += fArr[i3] * fArr[i3];
            int i4 = i3 + 1;
            f2 += fArr[i4] * fArr[i4];
            fArr[i2] = (fArr[i3] + fArr[i4]) * 0.5f;
            f3 += fArr[i2] * fArr[i2];
        }
        float f4 = f + 1.0f;
        float f5 = f3 / (f4 + f2);
        bits.pack(14, 5);
        bits.pack(9, 4);
        float log = (float) (Math.log((double) (f4 / (1.0f + f2))) * 4.0d);
        if (log > 0.0f) {
            bits.pack(0, 1);
        } else {
            bits.pack(1, 1);
        }
        float floor = (float) Math.floor((double) (Math.abs(log) + 0.5f));
        if (floor > 30.0f) {
            floor = 31.0f;
        }
        bits.pack((int) floor, 5);
        bits.pack(C0911VQ.index(f5, e_ratio_quant, 4), 2);
    }
}
