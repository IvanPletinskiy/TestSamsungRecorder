package org.xiph.speex;

public class SbCodec extends NbCodec {
    public static final int[] SB_FRAME_SIZE = {4, 36, 112, 192, 352, -1, -1, -1};
    protected float foldingGain;
    protected int fullFrameSize;
    protected float[] g0_mem;
    protected float[] g1_mem;
    protected float[] high;
    protected float[] x0d;

    /* renamed from: y0 */
    protected float[] f118y0;

    /* renamed from: y1 */
    protected float[] f119y1;

    protected static SubMode[] buildUwbSubModes() {
        SubMode[] subModeArr = new SubMode[8];
        subModeArr[1] = new SubMode(0, 0, 1, 0, new HighLspQuant(), (Ltp) null, (CbSearch) null, 0.75f, 0.75f, -1.0f, 2);
        return subModeArr;
    }

    protected static SubMode[] buildWbSubModes() {
        HighLspQuant highLspQuant = new HighLspQuant();
        SplitShapeSearch splitShapeSearch = new SplitShapeSearch(40, 10, 4, Codebook.hexc_10_32_table, 5, 0);
        SplitShapeSearch splitShapeSearch2 = new SplitShapeSearch(40, 8, 5, Codebook.hexc_table, 7, 1);
        SubMode[] subModeArr = new SubMode[8];
        HighLspQuant highLspQuant2 = highLspQuant;
        subModeArr[1] = new SubMode(0, 0, 1, 0, highLspQuant2, (Ltp) null, (CbSearch) null, 0.75f, 0.75f, -1.0f, 36);
        subModeArr[2] = new SubMode(0, 0, 1, 0, highLspQuant2, (Ltp) null, splitShapeSearch, 0.85f, 0.6f, -1.0f, 112);
        SplitShapeSearch splitShapeSearch3 = splitShapeSearch2;
        subModeArr[3] = new SubMode(0, 0, 1, 0, highLspQuant2, (Ltp) null, splitShapeSearch3, 0.75f, 0.7f, -1.0f, 192);
        subModeArr[4] = new SubMode(0, 0, 1, 1, highLspQuant2, (Ltp) null, splitShapeSearch3, 0.75f, 0.75f, -1.0f, 352);
        return subModeArr;
    }

    public float[] getExc() {
        float[] fArr = new float[this.fullFrameSize];
        for (int i = 0; i < this.frameSize; i++) {
            fArr[i * 2] = this.excBuf[this.excIdx + i] * 2.0f;
        }
        return fArr;
    }

    public int getFrameSize() {
        return this.fullFrameSize;
    }

    public float[] getInnov() {
        return getExc();
    }

    /* access modifiers changed from: protected */
    public void init(int i, int i2, int i3, int i4, float f) {
        super.init(i, i2, i3, i4);
        this.fullFrameSize = i * 2;
        this.foldingGain = f;
        this.lag_factor = 0.002f;
        int i5 = this.fullFrameSize;
        this.high = new float[i5];
        this.f118y0 = new float[i5];
        this.f119y1 = new float[i5];
        this.x0d = new float[i];
        this.g0_mem = new float[64];
        this.g1_mem = new float[64];
    }

    public void uwbinit() {
        this.submodes = buildUwbSubModes();
        this.submodeID = 1;
    }

    public void wbinit() {
        this.submodes = buildWbSubModes();
        this.submodeID = 3;
    }
}
