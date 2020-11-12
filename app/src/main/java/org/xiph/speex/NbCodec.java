package org.xiph.speex;

public class NbCodec implements Codebook {
    public static final int[] NB_FRAME_SIZE = {5, 43, 119, 160, 220, 300, 364, 492, 79, 1, 1, 1, 1, 1, 1, 1};
    public static final float[] exc_gain_quant_scal1 = {-0.35f, 0.05f};
    public static final float[] exc_gain_quant_scal3 = {-2.79475f, -1.81066f, -1.16985f, -0.848119f, -0.58719f, -0.329818f, -0.063266f, 0.282826f};
    protected float[] awk1;
    protected float[] awk2;
    protected float[] awk3;
    protected int bufSize;
    protected int dtx_enabled;
    protected float[] excBuf;
    protected int excIdx;
    protected Filters filters = new Filters();
    protected int first;
    protected int frameSize;
    protected float[] frmBuf;
    protected int frmIdx;
    protected float gamma1;
    protected float gamma2;
    protected float[] innov;
    protected float[] interp_qlpc;
    protected float[] interp_qlsp;
    protected float lag_factor;
    protected float[] lpc;
    protected int lpcSize;
    protected float lpc_floor;
    protected Lsp m_lsp = new Lsp();
    protected int max_pitch;
    protected float[] mem_sp;
    protected int min_pitch;
    protected int nbSubframes;
    protected float[] old_qlsp;
    protected float[] pi_gain;
    protected float pre_mem;
    protected float preemph;
    protected float[] qlsp;
    protected int subframeSize;
    protected int submodeID;
    protected SubMode[] submodes;
    protected float voc_m1;
    protected float voc_m2;
    protected float voc_mean;
    protected int voc_offset;
    protected int windowSize;

    private static SubMode[] buildNbSubModes() {
        Ltp3Tap ltp3Tap = new Ltp3Tap(Codebook.gain_cdbk_nb, 7, 7);
        Ltp3Tap ltp3Tap2 = new Ltp3Tap(Codebook.gain_cdbk_lbr, 5, 0);
        Ltp3Tap ltp3Tap3 = new Ltp3Tap(Codebook.gain_cdbk_lbr, 5, 7);
        Ltp3Tap ltp3Tap4 = new Ltp3Tap(Codebook.gain_cdbk_lbr, 5, 7);
        LtpForcedPitch ltpForcedPitch = new LtpForcedPitch();
        NoiseSearch noiseSearch = new NoiseSearch();
        SplitShapeSearch splitShapeSearch = new SplitShapeSearch(40, 10, 4, Codebook.exc_10_16_table, 4, 0);
        SplitShapeSearch splitShapeSearch2 = new SplitShapeSearch(40, 10, 4, Codebook.exc_10_32_table, 5, 0);
        SplitShapeSearch splitShapeSearch3 = new SplitShapeSearch(40, 5, 8, Codebook.exc_5_64_table, 6, 0);
        SplitShapeSearch splitShapeSearch4 = new SplitShapeSearch(40, 8, 5, Codebook.exc_8_128_table, 7, 0);
        SplitShapeSearch splitShapeSearch5 = new SplitShapeSearch(40, 5, 8, Codebook.exc_5_256_table, 8, 0);
        SplitShapeSearch splitShapeSearch6 = new SplitShapeSearch(40, 20, 2, Codebook.exc_20_32_table, 5, 0);
        NbLspQuant nbLspQuant = new NbLspQuant();
        SubMode[] subModeArr = new SubMode[16];
        LbrLspQuant lbrLspQuant = new LbrLspQuant();
        subModeArr[1] = new SubMode(0, 1, 0, 0, lbrLspQuant, ltpForcedPitch, noiseSearch, 0.7f, 0.7f, -1.0f, 43);
        subModeArr[2] = new SubMode(0, 0, 0, 0, lbrLspQuant, ltp3Tap2, splitShapeSearch, 0.7f, 0.5f, 0.55f, 119);
        subModeArr[3] = new SubMode(-1, 0, 1, 0, lbrLspQuant, ltp3Tap3, splitShapeSearch2, 0.7f, 0.55f, 0.45f, 160);
        subModeArr[4] = new SubMode(-1, 0, 1, 0, lbrLspQuant, ltp3Tap4, splitShapeSearch4, 0.7f, 0.63f, 0.35f, 220);
        NbLspQuant nbLspQuant2 = nbLspQuant;
        Ltp3Tap ltp3Tap5 = ltp3Tap;
        SubMode[] subModeArr2 = subModeArr;
        subModeArr2[5] = new SubMode(-1, 0, 3, 0, nbLspQuant2, ltp3Tap5, splitShapeSearch3, 0.7f, 0.65f, 0.25f, 300);
        subModeArr2[6] = new SubMode(-1, 0, 3, 0, nbLspQuant2, ltp3Tap5, splitShapeSearch5, 0.68f, 0.65f, 0.1f, 364);
        subModeArr2[7] = new SubMode(-1, 0, 3, 1, nbLspQuant2, ltp3Tap5, splitShapeSearch3, 0.65f, 0.65f, -1.0f, 492);
        subModeArr2[8] = new SubMode(0, 1, 0, 0, lbrLspQuant, ltpForcedPitch, splitShapeSearch6, 0.7f, 0.5f, 0.65f, 79);
        return subModeArr2;
    }

    public float[] getExc() {
        int i = this.frameSize;
        float[] fArr = new float[i];
        System.arraycopy(this.excBuf, this.excIdx, fArr, 0, i);
        return fArr;
    }

    public int getFrameSize() {
        return this.frameSize;
    }

    public float[] getInnov() {
        return this.innov;
    }

    public float[] getPiGain() {
        return this.pi_gain;
    }

    /* access modifiers changed from: protected */
    public void init(int i, int i2, int i3, int i4) {
        this.first = 1;
        this.frameSize = i;
        this.windowSize = (i * 3) / 2;
        this.subframeSize = i2;
        this.nbSubframes = i / i2;
        this.lpcSize = i3;
        this.bufSize = i4;
        this.min_pitch = 17;
        this.max_pitch = 144;
        this.preemph = 0.0f;
        this.pre_mem = 0.0f;
        this.gamma1 = 0.9f;
        this.gamma2 = 0.6f;
        this.lag_factor = 0.01f;
        this.lpc_floor = 1.0001f;
        this.frmBuf = new float[i4];
        int i5 = this.windowSize;
        this.frmIdx = i4 - i5;
        this.excBuf = new float[i4];
        this.excIdx = i4 - i5;
        this.innov = new float[i];
        int i6 = i3 + 1;
        this.lpc = new float[i6];
        this.qlsp = new float[i3];
        this.old_qlsp = new float[i3];
        this.interp_qlsp = new float[i3];
        this.interp_qlpc = new float[i6];
        this.mem_sp = new float[(i3 * 5)];
        this.pi_gain = new float[this.nbSubframes];
        this.awk1 = new float[i6];
        this.awk2 = new float[i6];
        this.awk3 = new float[i6];
        this.voc_mean = 0.0f;
        this.voc_m2 = 0.0f;
        this.voc_m1 = 0.0f;
        this.voc_offset = 0;
        this.dtx_enabled = 0;
    }

    public void nbinit() {
        this.submodes = buildNbSubModes();
        this.submodeID = 5;
        init(160, 40, 10, 640);
    }
}
