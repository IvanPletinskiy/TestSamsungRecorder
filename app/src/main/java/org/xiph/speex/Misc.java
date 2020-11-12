package org.xiph.speex;

public class Misc {
    public static float[] lagWindow(int i, float f) {
        int i2 = i + 1;
        float[] fArr = new float[i2];
        for (int i3 = 0; i3 < i2; i3++) {
            double d = ((double) f) * 6.283185307179586d * ((double) i3);
            fArr[i3] = (float) Math.exp(-0.5d * d * d);
        }
        return fArr;
    }

    public static float[] window(int i, int i2) {
        int i3 = (i2 * 7) / 2;
        int i4 = (i2 * 5) / 2;
        float[] fArr = new float[i];
        for (int i5 = 0; i5 < i3; i5++) {
            fArr[i5] = (float) (0.54d - (Math.cos((((double) i5) * 3.141592653589793d) / ((double) i3)) * 0.46d));
        }
        for (int i6 = 0; i6 < i4; i6++) {
            fArr[i3 + i6] = (float) ((Math.cos((((double) i6) * 3.141592653589793d) / ((double) i4)) * 0.46d) + 0.54d);
        }
        return fArr;
    }
}
