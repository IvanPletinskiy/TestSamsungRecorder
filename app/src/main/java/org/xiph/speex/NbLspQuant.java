package org.xiph.speex;

public class NbLspQuant extends LspQuant {
    public final void quant(float[] fArr, float[] fArr2, int i, Bits bits) {
        int i2;
        float[] fArr3 = fArr2;
        int i3 = i;
        Bits bits2 = bits;
        float[] fArr4 = new float[20];
        for (int i4 = 0; i4 < i3; i4++) {
            fArr3[i4] = fArr[i4];
        }
        int i5 = 1;
        fArr4[0] = 1.0f / (fArr3[1] - fArr3[0]);
        int i6 = i3 - 1;
        fArr4[i6] = 1.0f / (fArr3[i6] - fArr3[i3 - 2]);
        while (i5 < i6) {
            int i7 = i5 - 1;
            float f = 1.0f / (((fArr3[i5] + 0.15f) - fArr3[i7]) * ((fArr3[i5] + 0.15f) - fArr3[i7]));
            int i8 = i5 + 1;
            float f2 = 1.0f / (((fArr3[i8] + 0.15f) - fArr3[i5]) * ((fArr3[i8] + 0.15f) - fArr3[i5]));
            if (f <= f2) {
                f = f2;
            }
            fArr4[i5] = f;
            i5 = i8;
        }
        for (int i9 = 0; i9 < i3; i9++) {
            fArr3[i9] = (float) (((double) fArr3[i9]) - ((((double) i9) * 0.25d) + 0.25d));
        }
        for (int i10 = 0; i10 < i3; i10++) {
            fArr3[i10] = fArr3[i10] * 256.0f;
        }
        bits2.pack(LspQuant.lsp_quant(fArr3, 0, Codebook.cdbk_nb, 64, i3), 6);
        for (int i11 = 0; i11 < i3; i11++) {
            fArr3[i11] = fArr3[i11] * 2.0f;
        }
        bits2.pack(LspQuant.lsp_weight_quant(fArr2, 0, fArr4, 0, Codebook.cdbk_nb_low1, 64, 5), 6);
        int i12 = 0;
        while (true) {
            if (i12 >= 5) {
                break;
            }
            fArr3[i12] = fArr3[i12] * 2.0f;
            i12++;
        }
        float[] fArr5 = fArr4;
        bits2.pack(LspQuant.lsp_weight_quant(fArr2, 0, fArr5, 0, Codebook.cdbk_nb_low2, 64, 5), 6);
        bits2.pack(LspQuant.lsp_weight_quant(fArr2, 5, fArr5, 5, Codebook.cdbk_nb_high1, 64, 5), 6);
        for (i2 = 5; i2 < 10; i2++) {
            fArr3[i2] = fArr3[i2] * 2.0f;
        }
        bits2.pack(LspQuant.lsp_weight_quant(fArr2, 5, fArr4, 5, Codebook.cdbk_nb_high2, 64, 5), 6);
        for (int i13 = 0; i13 < i3; i13++) {
            fArr3[i13] = (float) (((double) fArr3[i13]) * 9.7656E-4d);
        }
        for (int i14 = 0; i14 < i3; i14++) {
            fArr3[i14] = fArr[i14] - fArr3[i14];
        }
    }
}
