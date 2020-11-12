package org.xiph.speex;

public class LbrLspQuant extends LspQuant {
    public final void quant(float[] fArr, float[] fArr2, int i, Bits bits) {
        float[] fArr3 = new float[20];
        for (int i2 = 0; i2 < i; i2++) {
            fArr2[i2] = fArr[i2];
        }
        int i3 = 1;
        fArr3[0] = 1.0f / (fArr2[1] - fArr2[0]);
        int i4 = i - 1;
        fArr3[i4] = 1.0f / (fArr2[i4] - fArr2[i - 2]);
        while (i3 < i4) {
            int i5 = i3 - 1;
            float f = 1.0f / (((fArr2[i3] + 0.15f) - fArr2[i5]) * ((fArr2[i3] + 0.15f) - fArr2[i5]));
            int i6 = i3 + 1;
            float f2 = 1.0f / (((fArr2[i6] + 0.15f) - fArr2[i3]) * ((fArr2[i6] + 0.15f) - fArr2[i3]));
            if (f <= f2) {
                f = f2;
            }
            fArr3[i3] = f;
            i3 = i6;
        }
        for (int i7 = 0; i7 < i; i7++) {
            fArr2[i7] = (float) (((double) fArr2[i7]) - ((((double) i7) * 0.25d) + 0.25d));
        }
        for (int i8 = 0; i8 < i; i8++) {
            fArr2[i8] = fArr2[i8] * 256.0f;
        }
        bits.pack(LspQuant.lsp_quant(fArr2, 0, Codebook.cdbk_nb, 64, i), 6);
        for (int i9 = 0; i9 < i; i9++) {
            fArr2[i9] = fArr2[i9] * 2.0f;
        }
        float[] fArr4 = fArr3;
        bits.pack(LspQuant.lsp_weight_quant(fArr2, 0, fArr4, 0, Codebook.cdbk_nb_low1, 64, 5), 6);
        bits.pack(LspQuant.lsp_weight_quant(fArr2, 5, fArr4, 5, Codebook.cdbk_nb_high1, 64, 5), 6);
        for (int i10 = 0; i10 < i; i10++) {
            fArr2[i10] = (float) (((double) fArr2[i10]) * 0.0019531d);
        }
        for (int i11 = 0; i11 < i; i11++) {
            fArr2[i11] = fArr[i11] - fArr2[i11];
        }
    }
}
