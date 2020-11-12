package org.xiph.speex;

import java.lang.reflect.Array;

public class SplitShapeSearch extends CbSearch {

    /* renamed from: E */
    private float[] f121E = new float[this.shape_cb_size];

    /* renamed from: e */
    private float[] f122e;
    private int have_sign;
    private int[] ind;
    private int nb_subvect;
    private int[][] nind;

    /* renamed from: nt */
    private float[][] f123nt;
    private int[][] oind;

    /* renamed from: ot */
    private float[][] f124ot;

    /* renamed from: r2 */
    private float[] f125r2;
    private int shape_bits;
    private int[] shape_cb;
    private int shape_cb_size;
    private int[] signs;
    private int subframesize;
    private int subvect_size;

    /* renamed from: t */
    private float[] f126t;

    public SplitShapeSearch(int i, int i2, int i3, int[] iArr, int i4, int i5) {
        this.subframesize = i;
        this.subvect_size = i2;
        this.nb_subvect = i3;
        this.shape_cb = iArr;
        this.shape_bits = i4;
        this.have_sign = i5;
        this.ind = new int[i3];
        this.signs = new int[i3];
        this.shape_cb_size = 1 << i4;
        this.f124ot = (float[][]) Array.newInstance(float.class, new int[]{10, i});
        this.f123nt = (float[][]) Array.newInstance(float.class, new int[]{10, i});
        this.oind = (int[][]) Array.newInstance(int.class, new int[]{10, i3});
        this.nind = (int[][]) Array.newInstance(int.class, new int[]{10, i3});
        this.f126t = new float[i];
        this.f122e = new float[i];
        this.f125r2 = new float[i];
    }

    public final void quant(float[] fArr, float[] fArr2, float[] fArr3, float[] fArr4, int i, int i2, float[] fArr5, int i3, float[] fArr6, Bits bits, int i4) {
        float f;
        int i5;
        float f2;
        int i6;
        float f3;
        int i7 = i2;
        int i8 = 10;
        int i9 = i4;
        if (i9 <= 10) {
            i8 = i9;
        }
        float[] fArr7 = new float[(this.shape_cb_size * this.subvect_size)];
        int[] iArr = new int[i8];
        float[] fArr8 = new float[i8];
        float[] fArr9 = new float[i8];
        float[] fArr10 = new float[i8];
        for (int i10 = 0; i10 < i8; i10++) {
            for (int i11 = 0; i11 < this.nb_subvect; i11++) {
                int[] iArr2 = this.nind[i10];
                this.oind[i10][i11] = -1;
                iArr2[i11] = -1;
            }
        }
        for (int i12 = 0; i12 < i8; i12++) {
            for (int i13 = 0; i13 < i7; i13++) {
                this.f124ot[i12][i13] = fArr[i13];
            }
        }
        int i14 = 0;
        while (i14 < this.shape_cb_size) {
            int i15 = this.subvect_size;
            int i16 = i14 * i15;
            int i17 = i15 * i14;
            int i18 = 0;
            while (i18 < this.subvect_size) {
                int i19 = i16 + i18;
                fArr7[i19] = 0.0f;
                int i20 = 0;
                while (i20 <= i18) {
                    fArr7[i19] = (float) (((double) fArr7[i19]) + (((double) this.shape_cb[i17 + i20]) * 0.03125d * ((double) fArr6[i18 - i20])));
                    i20++;
                    int i21 = i2;
                    i17 = i17;
                    fArr8 = fArr8;
                    iArr = iArr;
                }
                int[] iArr3 = iArr;
                float[] fArr11 = fArr8;
                int i22 = i17;
                i18++;
                int i23 = i2;
            }
            int[] iArr4 = iArr;
            float[] fArr12 = fArr8;
            this.f121E[i14] = 0.0f;
            for (int i24 = 0; i24 < this.subvect_size; i24++) {
                float[] fArr13 = this.f121E;
                int i25 = i16 + i24;
                fArr13[i14] = fArr13[i14] + (fArr7[i25] * fArr7[i25]);
            }
            i14++;
            int i26 = i2;
            fArr8 = fArr12;
            iArr = iArr4;
        }
        int[] iArr5 = iArr;
        float[] fArr14 = fArr8;
        for (int i27 = 0; i27 < i8; i27++) {
            fArr10[i27] = 0.0f;
        }
        int i28 = 0;
        while (true) {
            float f4 = 0.03125f;
            float f5 = -1.0f;
            if (i28 >= this.nb_subvect) {
                break;
            }
            int i29 = this.subvect_size * i28;
            for (int i30 = 0; i30 < i8; i30++) {
                fArr9[i30] = -1.0f;
            }
            int i31 = 0;
            while (true) {
                if (i31 >= i8) {
                    int i32 = i2;
                    break;
                }
                if (this.have_sign != 0) {
                    i5 = i31;
                    C0911VQ.nbest_sign(this.f124ot[i31], i29, fArr7, this.subvect_size, this.shape_cb_size, this.f121E, i8, iArr5, fArr14);
                } else {
                    i5 = i31;
                    C0911VQ.nbest(this.f124ot[i5], i29, fArr7, this.subvect_size, this.shape_cb_size, this.f121E, i8, iArr5, fArr14);
                }
                int i33 = 0;
                while (i33 < i8) {
                    float[] fArr15 = this.f124ot[i5];
                    for (int i34 = i29; i34 < this.subvect_size + i29; i34++) {
                        this.f126t[i34] = fArr15[i34];
                    }
                    int i35 = iArr5[i33];
                    int i36 = this.shape_cb_size;
                    if (i35 >= i36) {
                        i35 -= i36;
                        f2 = f5;
                    } else {
                        f2 = 1.0f;
                    }
                    int i37 = i35 * this.subvect_size;
                    if (f2 > 0.0f) {
                        for (int i38 = 0; i38 < this.subvect_size; i38++) {
                            float[] fArr16 = this.f126t;
                            int i39 = i29 + i38;
                            fArr16[i39] = fArr16[i39] - fArr7[i37 + i38];
                        }
                    } else {
                        for (int i40 = 0; i40 < this.subvect_size; i40++) {
                            float[] fArr17 = this.f126t;
                            int i41 = i29 + i40;
                            fArr17[i41] = fArr17[i41] + fArr7[i37 + i40];
                        }
                    }
                    float f6 = fArr10[i5];
                    for (int i42 = i29; i42 < this.subvect_size + i29; i42++) {
                        float[] fArr18 = this.f126t;
                        f6 += fArr18[i42] * fArr18[i42];
                    }
                    int i43 = i8 - 1;
                    if (f6 < fArr9[i43] || ((double) fArr9[i43]) < -0.5d) {
                        int i44 = i2;
                        for (int i45 = this.subvect_size + i29; i45 < i44; i45++) {
                            this.f126t[i45] = fArr15[i45];
                        }
                        int i46 = 0;
                        while (i46 < this.subvect_size) {
                            int i47 = iArr5[i33];
                            int i48 = this.shape_cb_size;
                            if (i47 >= i48) {
                                i6 = i47 - i48;
                                f3 = -1.0f;
                            } else {
                                i6 = i47;
                                f3 = 1.0f;
                            }
                            float f7 = f3 * f4;
                            int[] iArr6 = this.shape_cb;
                            int i49 = this.subvect_size;
                            float f8 = f7 * ((float) iArr6[(i6 * i49) + i46]);
                            int i50 = i49 - i46;
                            int i51 = i49 + i29;
                            while (i51 < i44) {
                                float[] fArr19 = this.f126t;
                                fArr19[i51] = fArr19[i51] - (fArr6[i50] * f8);
                                i51++;
                                i50++;
                            }
                            i46++;
                            f4 = 0.03125f;
                        }
                        int i52 = 0;
                        while (true) {
                            if (i52 >= i8) {
                                break;
                            } else if (f6 >= fArr9[i52] && ((double) fArr9[i52]) >= -0.5d) {
                                i52++;
                            }
                        }
                        while (i43 > i52) {
                            for (int i53 = this.subvect_size + i29; i53 < i44; i53++) {
                                float[][] fArr20 = this.f123nt;
                                fArr20[i43][i53] = fArr20[i43 - 1][i53];
                            }
                            for (int i54 = 0; i54 < this.nb_subvect; i54++) {
                                int[][] iArr7 = this.nind;
                                iArr7[i43][i54] = iArr7[i43 - 1][i54];
                            }
                            fArr9[i43] = fArr9[i43 - 1];
                            i43--;
                        }
                        for (int i55 = this.subvect_size + i29; i55 < i44; i55++) {
                            this.f123nt[i52][i55] = this.f126t[i55];
                        }
                        for (int i56 = 0; i56 < this.nb_subvect; i56++) {
                            this.nind[i52][i56] = this.oind[i5][i56];
                        }
                        this.nind[i52][i28] = iArr5[i33];
                        fArr9[i52] = f6;
                    } else {
                        int i57 = i2;
                    }
                    i33++;
                    f4 = 0.03125f;
                    f5 = -1.0f;
                }
                int i58 = i2;
                if (i28 == 0) {
                    break;
                }
                i31 = i5 + 1;
                f4 = 0.03125f;
                f5 = -1.0f;
            }
            float[][] fArr21 = this.f124ot;
            this.f124ot = this.f123nt;
            this.f123nt = fArr21;
            for (int i59 = 0; i59 < i8; i59++) {
                for (int i60 = 0; i60 < this.nb_subvect; i60++) {
                    this.oind[i59][i60] = this.nind[i59][i60];
                }
            }
            for (int i61 = 0; i61 < i8; i61++) {
                fArr10[i61] = fArr9[i61];
            }
            i28++;
        }
        int i62 = i2;
        for (int i63 = 0; i63 < this.nb_subvect; i63++) {
            int[] iArr8 = this.ind;
            iArr8[i63] = this.nind[0][i63];
            bits.pack(iArr8[i63], this.shape_bits + this.have_sign);
        }
        for (int i64 = 0; i64 < this.nb_subvect; i64++) {
            int i65 = this.ind[i64];
            int i66 = this.shape_cb_size;
            if (i65 >= i66) {
                i65 -= i66;
                f = -1.0f;
            } else {
                f = 1.0f;
            }
            int i67 = 0;
            while (true) {
                int i68 = this.subvect_size;
                if (i67 >= i68) {
                    break;
                }
                this.f122e[(i68 * i64) + i67] = f * 0.03125f * ((float) this.shape_cb[(i68 * i65) + i67]);
                i67++;
            }
        }
        for (int i69 = 0; i69 < i62; i69++) {
            int i70 = i3 + i69;
            fArr5[i70] = fArr5[i70] + this.f122e[i69];
        }
        Filters.syn_percep_zero(this.f122e, 0, fArr2, fArr3, fArr4, this.f125r2, i2, i);
        for (int i71 = 0; i71 < i62; i71++) {
            fArr[i71] = fArr[i71] - this.f125r2[i71];
        }
    }
}
