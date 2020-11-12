package org.xiph.speex;

public class SubMode {
    public int bits_per_frame;
    public float comb_gain;
    public int double_codebook;
    public int forced_pitch_gain;
    public int have_subframe_gain;
    public CbSearch innovation;
    public int lbr_pitch;
    public float lpc_enh_k1;
    public float lpc_enh_k2;
    public LspQuant lsqQuant;
    public Ltp ltp;

    public SubMode(int i, int i2, int i3, int i4, LspQuant lspQuant, Ltp ltp2, CbSearch cbSearch, float f, float f2, float f3, int i5) {
        this.lbr_pitch = i;
        this.forced_pitch_gain = i2;
        this.have_subframe_gain = i3;
        this.double_codebook = i4;
        this.lsqQuant = lspQuant;
        this.ltp = ltp2;
        this.innovation = cbSearch;
        this.lpc_enh_k1 = f;
        this.lpc_enh_k2 = f2;
        this.comb_gain = f3;
        this.bits_per_frame = i5;
    }
}
