package org.xiph.speex;

public class Vbr {
    public static final float[][] hb_thresh = {new float[]{-1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f}, new float[]{-1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f}, new float[]{11.0f, 11.0f, 9.5f, 8.5f, 7.5f, 6.0f, 5.0f, 3.9f, 3.0f, 2.0f, 1.0f}, new float[]{11.0f, 11.0f, 11.0f, 11.0f, 11.0f, 9.5f, 8.7f, 7.8f, 7.0f, 6.5f, 4.0f}, new float[]{11.0f, 11.0f, 11.0f, 11.0f, 11.0f, 11.0f, 11.0f, 11.0f, 9.8f, 7.5f, 5.5f}};
    public static final float[][] nb_thresh = {new float[]{-1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f}, new float[]{3.5f, 2.5f, 2.0f, 1.2f, 0.5f, 0.0f, -0.5f, -0.7f, -0.8f, -0.9f, -1.0f}, new float[]{10.0f, 6.5f, 5.2f, 4.5f, 3.9f, 3.5f, 3.0f, 2.5f, 2.3f, 1.8f, 1.0f}, new float[]{11.0f, 8.8f, 7.5f, 6.5f, 5.0f, 3.9f, 3.9f, 3.9f, 3.5f, 3.0f, 1.0f}, new float[]{11.0f, 11.0f, 9.9f, 9.0f, 8.0f, 7.0f, 6.5f, 6.0f, 5.0f, 4.0f, 2.0f}, new float[]{11.0f, 11.0f, 11.0f, 11.0f, 9.5f, 9.0f, 8.0f, 7.0f, 6.5f, 5.0f, 3.0f}, new float[]{11.0f, 11.0f, 11.0f, 11.0f, 11.0f, 11.0f, 9.5f, 8.5f, 8.0f, 6.5f, 4.0f}, new float[]{11.0f, 11.0f, 11.0f, 11.0f, 11.0f, 11.0f, 11.0f, 11.0f, 9.8f, 7.5f, 5.5f}, new float[]{8.0f, 5.0f, 3.7f, 3.0f, 2.5f, 2.0f, 1.8f, 1.5f, 1.0f, 0.0f, 0.0f}};
    public static final float[][] uhb_thresh = {new float[]{-1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f}, new float[]{3.9f, 2.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f}};
    private float accum_sum = 0.0f;
    private float average_energy = 0.0f;
    private int consec_noise;
    private float energy_alpha = 0.1f;
    private float last_energy = 1.0f;
    private float[] last_log_energy;
    private float last_pitch_coef = 0.0f;
    private float last_quality = 0.0f;
    private float noise_accum = ((float) (Math.pow(6000.0d, 0.30000001192092896d) * 0.05d));
    private float noise_accum_count = 0.05f;
    private float noise_level = (this.noise_accum / this.noise_accum_count);
    private float soft_pitch = 0.0f;

    public Vbr() {
        this.consec_noise = 0;
        this.last_log_energy = new float[5];
        for (int i = 0; i < 5; i++) {
            this.last_log_energy[i] = (float) Math.log(6000.0d);
        }
    }

    public float analysis(float[] fArr, int i, int i2, float f) {
        int i3;
        float f2;
        int i4;
        int i5 = i;
        float f3 = f;
        int i6 = 0;
        float f4 = 0.0f;
        while (true) {
            i3 = i5 >> 1;
            if (i6 >= i3) {
                break;
            }
            f4 += fArr[i6] * fArr[i6];
            i6++;
        }
        float f5 = 0.0f;
        while (i3 < i5) {
            f5 += fArr[i3] * fArr[i3];
            i3++;
        }
        float f6 = f4 + f5;
        float log = (float) Math.log((double) (f6 + 6000.0f));
        float f7 = 0.0f;
        for (int i7 = 0; i7 < 5; i7++) {
            float[] fArr2 = this.last_log_energy;
            f7 += (log - fArr2[i7]) * (log - fArr2[i7]);
        }
        float f8 = f7 / 150.0f;
        if (f8 > 1.0f) {
            f8 = 1.0f;
        }
        float f9 = f3 - 0.4f;
        float abs = f9 * 3.0f * Math.abs(f9);
        float f10 = this.energy_alpha;
        this.average_energy = ((1.0f - f10) * this.average_energy) + (f10 * f6);
        this.noise_level = this.noise_accum / this.noise_accum_count;
        double d = (double) f6;
        float pow = (float) Math.pow(d, 0.30000001192092896d);
        if (this.noise_accum_count < 0.06f && f6 > 6000.0f) {
            this.noise_accum = pow * 0.05f;
        }
        int i8 = (abs > 0.3f ? 1 : (abs == 0.3f ? 0 : -1));
        if ((i8 >= 0 || f8 >= 0.2f || pow >= this.noise_level * 1.2f) && ((i8 >= 0 || f8 >= 0.05f || pow >= this.noise_level * 1.5f) && ((abs >= 0.4f || f8 >= 0.05f || pow >= this.noise_level * 1.2f) && (abs >= 0.0f || f8 >= 0.05f)))) {
            this.consec_noise = 0;
        } else {
            this.consec_noise++;
            float f11 = this.noise_level;
            float f12 = pow > f11 * 3.0f ? f11 * 3.0f : pow;
            if (this.consec_noise >= 4) {
                this.noise_accum = (this.noise_accum * 0.95f) + (f12 * 0.05f);
                this.noise_accum_count = (this.noise_accum_count * 0.95f) + 0.05f;
            }
        }
        if (pow < this.noise_level && f6 > 6000.0f) {
            this.noise_accum = (this.noise_accum * 0.95f) + (pow * 0.05f);
            this.noise_accum_count = (this.noise_accum_count * 0.95f) + 0.05f;
        }
        if (f6 < 30000.0f) {
            f2 = 6.3f;
            if (f6 < 10000.0f) {
                f2 = 5.6000004f;
            }
            if (f6 < 3000.0f) {
                f2 -= 0.7f;
            }
        } else {
            float f13 = f6 + 1.0f;
            float log2 = (float) Math.log((double) (f13 / (this.last_energy + 1.0f)));
            float log3 = (float) Math.log((double) (f13 / (this.average_energy + 1.0f)));
            float f14 = -5.0f;
            if (log3 >= -5.0f) {
                f14 = log3;
            }
            if (f14 > 2.0f) {
                f14 = 2.0f;
            }
            float f15 = 7.0f;
            if (f14 > 0.0f) {
                f15 = 7.0f + (0.6f * f14);
            }
            if (f14 < 0.0f) {
                f15 += f14 * 0.5f;
            }
            if (log2 > 0.0f) {
                if (log2 > 5.0f) {
                    log2 = 5.0f;
                }
                f15 += log2 * 0.5f;
            }
            f2 = f5 > f4 * 1.6f ? f15 + 0.5f : f15;
        }
        this.last_energy = f6;
        this.soft_pitch = (this.soft_pitch * 0.6f) + (0.4f * f3);
        float f16 = (float) (((double) f2) + (((((double) f3) - 0.4d) + (((double) this.soft_pitch) - 0.4d)) * 2.200000047683716d));
        float f17 = this.last_quality;
        if (f16 < f17) {
            f16 = (f16 * 0.5f) + (f17 * 0.5f);
        }
        if (f16 < 4.0f) {
            f16 = 4.0f;
        }
        if (f16 > 10.0f) {
            f16 = 10.0f;
        }
        if (this.consec_noise >= 3) {
            f16 = 4.0f;
        }
        int i9 = this.consec_noise;
        if (i9 != 0) {
            f16 -= (float) ((Math.log(((double) i9) + 3.0d) - Math.log(3.0d)) * 1.0d);
        }
        if (f16 < 0.0f) {
            f16 = 0.0f;
        }
        if (f6 < 60000.0f) {
            int i10 = this.consec_noise;
            if (i10 > 2) {
                f16 -= (float) ((Math.log(((double) i10) + 3.0d) - Math.log(3.0d)) * 0.5d);
            }
            if (f6 < 10000.0f && (i4 = this.consec_noise) > 2) {
                f16 -= (float) ((Math.log(((double) i4) + 3.0d) - Math.log(3.0d)) * 0.5d);
            }
            float f18 = 0.0f;
            if (f16 >= 0.0f) {
                f18 = f16;
            }
            f16 = f18 + ((float) (Math.log(d / 60000.0d) * 0.3d));
        }
        if (f16 < -1.0f) {
            f16 = -1.0f;
        }
        this.last_pitch_coef = f3;
        this.last_quality = f16;
        for (int i11 = 4; i11 > 0; i11--) {
            float[] fArr3 = this.last_log_energy;
            fArr3[i11] = fArr3[i11 - 1];
        }
        this.last_log_energy[0] = log;
        return f16;
    }
}
