package org.xiph.speex;

import java.lang.reflect.Array;

public class Ltp3Tap extends Ltp {

    /* renamed from: e */
    private float[][] f116e;
    private float[] gain = new float[3];
    private int gain_bits;
    private int[] gain_cdbk;
    private int pitch_bits;

    public Ltp3Tap(int[] iArr, int i, int i2) {
        this.gain_cdbk = iArr;
        this.gain_bits = i;
        this.pitch_bits = i2;
        this.f116e = (float[][]) Array.newInstance(float.class, new int[]{3, 128});
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v4, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v0, resolved type: float[][]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private float pitch_gain_search_3tap(float[] r24, float[] r25, float[] r26, float[] r27, float[] r28, int r29, int r30, int r31, int r32, org.xiph.speex.Bits r33, float[] r34, int r35, float[] r36, int[] r37) {
        /*
            r23 = this;
            r0 = r23
            r1 = r24
            r10 = r32
            r11 = 3
            float[] r12 = new float[r11]
            int[] r2 = new int[]{r11, r11}
            java.lang.Class<float> r3 = float.class
            java.lang.Object r2 = java.lang.reflect.Array.newInstance(r3, r2)
            r13 = r2
            float[][] r13 = (float[][]) r13
            int r2 = r0.gain_bits
            r14 = 1
            int r15 = r14 << r2
            int[] r2 = new int[]{r11, r10}
            java.lang.Class<float> r3 = float.class
            java.lang.Object r2 = java.lang.reflect.Array.newInstance(r3, r2)
            r16 = r2
            float[][] r16 = (float[][]) r16
            int[] r2 = new int[]{r11, r10}
            java.lang.Class<float> r3 = float.class
            java.lang.Object r2 = java.lang.reflect.Array.newInstance(r3, r2)
            float[][] r2 = (float[][]) r2
            r0.f116e = r2
            r9 = 2
            r8 = r9
        L_0x0039:
            r2 = 0
            r3 = 0
            if (r8 < 0) goto L_0x00c0
            int r4 = r30 + 1
            int r4 = r4 - r8
            r5 = r3
        L_0x0041:
            if (r5 >= r10) goto L_0x006e
            int r6 = r5 - r4
            if (r6 >= 0) goto L_0x0053
            float[][] r6 = r0.f116e
            r6 = r6[r8]
            int r7 = r35 + r5
            int r7 = r7 - r4
            r7 = r34[r7]
            r6[r5] = r7
            goto L_0x006b
        L_0x0053:
            int r6 = r6 - r30
            if (r6 >= 0) goto L_0x0065
            float[][] r6 = r0.f116e
            r6 = r6[r8]
            int r7 = r35 + r5
            int r7 = r7 - r4
            int r7 = r7 - r30
            r7 = r34[r7]
            r6[r5] = r7
            goto L_0x006b
        L_0x0065:
            float[][] r6 = r0.f116e
            r6 = r6[r8]
            r6[r5] = r2
        L_0x006b:
            int r5 = r5 + 1
            goto L_0x0041
        L_0x006e:
            if (r8 != r9) goto L_0x0089
            float[][] r2 = r0.f116e
            r2 = r2[r8]
            r3 = 0
            r7 = r16[r8]
            r4 = r25
            r5 = r26
            r6 = r27
            r17 = r8
            r8 = r32
            r18 = r9
            r9 = r31
            org.xiph.speex.Filters.syn_percep_zero(r2, r3, r4, r5, r6, r7, r8, r9)
            goto L_0x00ba
        L_0x0089:
            r17 = r8
            r18 = r9
            r4 = r3
        L_0x008e:
            int r5 = r10 + -1
            if (r4 >= r5) goto L_0x00a0
            r5 = r16[r17]
            int r6 = r4 + 1
            int r8 = r17 + 1
            r7 = r16[r8]
            r4 = r7[r4]
            r5[r6] = r4
            r4 = r6
            goto L_0x008e
        L_0x00a0:
            r4 = r16[r17]
            r4[r3] = r2
            r2 = r3
        L_0x00a5:
            if (r2 >= r10) goto L_0x00ba
            r4 = r16[r17]
            r5 = r4[r2]
            float[][] r6 = r0.f116e
            r6 = r6[r17]
            r6 = r6[r3]
            r7 = r36[r2]
            float r6 = r6 * r7
            float r5 = r5 + r6
            r4[r2] = r5
            int r2 = r2 + 1
            goto L_0x00a5
        L_0x00ba:
            int r8 = r17 + -1
            r9 = r18
            goto L_0x0039
        L_0x00c0:
            r18 = r9
            r4 = r3
        L_0x00c3:
            if (r4 >= r11) goto L_0x00d0
            r5 = r16[r4]
            float r5 = org.xiph.speex.Ltp.inner_prod(r5, r3, r1, r3, r10)
            r12[r4] = r5
            int r4 = r4 + 1
            goto L_0x00c3
        L_0x00d0:
            r4 = r3
        L_0x00d1:
            if (r4 >= r11) goto L_0x00ec
            r5 = r3
        L_0x00d4:
            if (r5 > r4) goto L_0x00e9
            r6 = r13[r4]
            r7 = r13[r5]
            r8 = r16[r4]
            r9 = r16[r5]
            float r8 = org.xiph.speex.Ltp.inner_prod(r8, r3, r9, r3, r10)
            r7[r4] = r8
            r6[r5] = r8
            int r5 = r5 + 1
            goto L_0x00d4
        L_0x00e9:
            int r4 = r4 + 1
            goto L_0x00d1
        L_0x00ec:
            r4 = 9
            float[] r4 = new float[r4]
            r5 = r12[r18]
            r4[r3] = r5
            r5 = r12[r14]
            r4[r14] = r5
            r5 = r12[r3]
            r4[r18] = r5
            r5 = r13[r14]
            r5 = r5[r18]
            r4[r11] = r5
            r5 = r13[r3]
            r5 = r5[r14]
            r6 = 4
            r4[r6] = r5
            r5 = r13[r3]
            r5 = r5[r18]
            r7 = 5
            r4[r7] = r5
            r5 = r13[r18]
            r5 = r5[r18]
            r8 = 6
            r4[r8] = r5
            r5 = r13[r14]
            r5 = r5[r14]
            r9 = 7
            r4[r9] = r5
            r5 = r13[r3]
            r5 = r5[r3]
            r12 = 8
            r4[r12] = r5
            r17 = r2
            r5 = r3
            r13 = r5
        L_0x012a:
            r19 = 1015021568(0x3c800000, float:0.015625)
            r20 = 1056964608(0x3f000000, float:0.5)
            if (r5 >= r15) goto L_0x01a9
            int r21 = r5 * 3
            int[] r12 = r0.gain_cdbk
            r9 = r12[r21]
            float r9 = (float) r9
            float r9 = r9 * r19
            float r9 = r9 + r20
            int r22 = r21 + 1
            r8 = r12[r22]
            float r8 = (float) r8
            float r8 = r8 * r19
            float r8 = r8 + r20
            int r21 = r21 + 2
            r12 = r12[r21]
            float r12 = (float) r12
            float r12 = r12 * r19
            float r12 = r12 + r20
            r19 = r4[r3]
            float r19 = r19 * r9
            float r19 = r19 + r2
            r21 = r4[r14]
            float r21 = r21 * r8
            float r19 = r19 + r21
            r21 = r4[r18]
            float r21 = r21 * r12
            float r19 = r19 + r21
            r21 = r4[r11]
            float r21 = r21 * r9
            float r21 = r21 * r8
            float r19 = r19 - r21
            r21 = r4[r6]
            float r21 = r21 * r12
            float r21 = r21 * r8
            float r19 = r19 - r21
            r21 = r4[r7]
            float r21 = r21 * r12
            float r21 = r21 * r9
            float r19 = r19 - r21
            r21 = 6
            r22 = r4[r21]
            float r22 = r22 * r20
            float r22 = r22 * r9
            float r22 = r22 * r9
            float r19 = r19 - r22
            r9 = 7
            r22 = r4[r9]
            float r22 = r22 * r20
            float r22 = r22 * r8
            float r22 = r22 * r8
            float r19 = r19 - r22
            r8 = 8
            r22 = r4[r8]
            float r22 = r22 * r20
            float r22 = r22 * r12
            float r22 = r22 * r12
            float r19 = r19 - r22
            int r12 = (r19 > r17 ? 1 : (r19 == r17 ? 0 : -1))
            if (r12 > 0) goto L_0x01a0
            if (r5 != 0) goto L_0x01a3
        L_0x01a0:
            r13 = r5
            r17 = r19
        L_0x01a3:
            int r5 = r5 + 1
            r12 = r8
            r8 = r21
            goto L_0x012a
        L_0x01a9:
            float[] r4 = r0.gain
            int[] r5 = r0.gain_cdbk
            int r6 = r13 * 3
            r7 = r5[r6]
            float r7 = (float) r7
            float r7 = r7 * r19
            float r7 = r7 + r20
            r4[r3] = r7
            int r7 = r6 + 1
            r7 = r5[r7]
            float r7 = (float) r7
            float r7 = r7 * r19
            float r7 = r7 + r20
            r4[r14] = r7
            int r6 = r6 + 2
            r5 = r5[r6]
            float r5 = (float) r5
            float r5 = r5 * r19
            float r5 = r5 + r20
            r4[r18] = r5
            r37[r3] = r13
            r4 = r3
        L_0x01d1:
            if (r4 >= r10) goto L_0x01f5
            int r5 = r29 + r4
            float[] r6 = r0.gain
            r7 = r6[r3]
            float[][] r8 = r0.f116e
            r9 = r8[r18]
            r9 = r9[r4]
            float r7 = r7 * r9
            r9 = r6[r14]
            r11 = r8[r14]
            r11 = r11[r4]
            float r9 = r9 * r11
            float r7 = r7 + r9
            r6 = r6[r18]
            r8 = r8[r3]
            r8 = r8[r4]
            float r6 = r6 * r8
            float r7 = r7 + r6
            r28[r5] = r7
            int r4 = r4 + 1
            goto L_0x01d1
        L_0x01f5:
            r4 = r3
        L_0x01f6:
            if (r4 >= r10) goto L_0x01ff
            r5 = r1[r4]
            r5 = r1[r4]
            int r4 = r4 + 1
            goto L_0x01f6
        L_0x01ff:
            r4 = r2
            r2 = r3
        L_0x0201:
            if (r2 >= r10) goto L_0x023e
            r5 = r1[r2]
            float[] r6 = r0.gain
            r7 = r6[r18]
            r8 = r16[r3]
            r8 = r8[r2]
            float r7 = r7 * r8
            float r5 = r5 - r7
            r7 = r6[r14]
            r8 = r16[r14]
            r8 = r8[r2]
            float r7 = r7 * r8
            float r5 = r5 - r7
            r7 = r6[r3]
            r8 = r16[r18]
            r8 = r8[r2]
            float r7 = r7 * r8
            float r5 = r5 - r7
            r7 = r1[r2]
            r8 = r6[r18]
            r9 = r16[r3]
            r9 = r9[r2]
            float r8 = r8 * r9
            float r7 = r7 - r8
            r8 = r6[r14]
            r9 = r16[r14]
            r9 = r9[r2]
            float r8 = r8 * r9
            float r7 = r7 - r8
            r6 = r6[r3]
            r8 = r16[r18]
            r8 = r8[r2]
            float r6 = r6 * r8
            float r7 = r7 - r6
            float r5 = r5 * r7
            float r4 = r4 + r5
            int r2 = r2 + 1
            goto L_0x0201
        L_0x023e:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xiph.speex.Ltp3Tap.pitch_gain_search_3tap(float[], float[], float[], float[], float[], int, int, int, int, org.xiph.speex.Bits, float[], int, float[], int[]):float");
    }

    public final int quant(float[] fArr, float[] fArr2, int i, float[] fArr3, float[] fArr4, float[] fArr5, float[] fArr6, int i2, int i3, int i4, float f, int i5, int i6, Bits bits, float[] fArr7, int i7, float[] fArr8, int i8) {
        int i9;
        int i10 = i3;
        int i11 = i4;
        int i12 = i6;
        Bits bits2 = bits;
        int[] iArr = new int[1];
        int i13 = 10;
        int i14 = i8;
        if (i14 <= 10) {
            i13 = i14;
        }
        int[] iArr2 = new int[i13];
        float[] fArr9 = new float[i13];
        int i15 = 0;
        if (i13 == 0 || i11 < i10) {
            Bits bits3 = bits2;
            int i16 = i12;
            bits3.pack(0, this.pitch_bits);
            bits3.pack(0, this.gain_bits);
            for (int i17 = 0; i17 < i16; i17++) {
                fArr6[i2 + i17] = 0.0f;
            }
            return i3;
        }
        float[] fArr10 = new float[i12];
        int i18 = (i11 - i10) + 1;
        int i19 = i13 > i18 ? i18 : i13;
        int i20 = i19;
        Ltp.open_loop_nbest_pitch(fArr2, i, i3, i4, i6, iArr2, fArr9, i19);
        float f2 = -1.0f;
        int i21 = 0;
        int i22 = 0;
        int i23 = 0;
        int i24 = 0;
        while (i23 < i19) {
            int i25 = iArr2[i23];
            for (int i26 = i15; i26 < i12; i26++) {
                fArr6[i2 + i26] = 0.0f;
            }
            int i27 = i22;
            int i28 = i23;
            int i29 = i19;
            float[] fArr11 = fArr10;
            int i30 = i15;
            int[] iArr3 = iArr2;
            int[] iArr4 = iArr;
            int i31 = i12;
            float pitch_gain_search_3tap = pitch_gain_search_3tap(fArr, fArr3, fArr4, fArr5, fArr6, i2, i25, i5, i6, bits, fArr7, i7, fArr8, iArr4);
            if (pitch_gain_search_3tap < f2 || f2 < 0.0f) {
                for (int i32 = 0; i32 < i31; i32++) {
                    fArr11[i32] = fArr6[i2 + i32];
                }
                i9 = 0;
                f2 = pitch_gain_search_3tap;
                i22 = iArr4[0];
                i24 = i25;
            } else {
                i22 = i27;
                i9 = 0;
            }
            i23 = i28 + 1;
            int i33 = i3;
            Bits bits4 = bits;
            i15 = i9;
            i12 = i31;
            i21 = i25;
            i19 = i29;
            fArr10 = fArr11;
            iArr2 = iArr3;
            iArr = iArr4;
        }
        int i34 = i22;
        float[] fArr12 = fArr10;
        int i35 = i12;
        Bits bits5 = bits;
        bits5.pack(i24 - i3, this.pitch_bits);
        bits5.pack(i34, this.gain_bits);
        for (int i36 = i15; i36 < i35; i36++) {
            fArr6[i2 + i36] = fArr12[i36];
        }
        return i21;
    }
}
