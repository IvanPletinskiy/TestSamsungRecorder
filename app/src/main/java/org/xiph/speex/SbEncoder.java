package org.xiph.speex;

public class SbEncoder extends SbCodec implements Encoder {
    public static final int[] NB_QUALITY_MAP = {1, 8, 2, 3, 4, 5, 5, 6, 6, 7, 7};
    public static final int[] UWB_QUALITY_MAP = {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    public static final int[] WB_QUALITY_MAP = {1, 1, 1, 1, 1, 1, 2, 2, 3, 3, 4};
    protected float abr_count;
    protected float abr_drift;
    protected float abr_drift2;
    protected int abr_enabled;
    private float[] autocorr;
    private float[] buf;
    private float[] bw_lpc1;
    private float[] bw_lpc2;
    protected int complexity;
    private float[] h0_mem;
    private float[] interp_lpc;
    private float[] interp_lsp;
    private float[] lagWindow;
    protected Encoder lowenc;
    private float[] lsp;
    private float[] mem_sp2;
    private float[] mem_sw;
    protected int nb_modes;
    private float[] old_lsp;

    /* renamed from: rc */
    private float[] f120rc;
    protected float relative_quality;
    private float[] res;
    protected int sampling_rate;
    protected int submodeSelect;
    private float[] swBuf;
    private float[] target;
    private boolean uwb;
    protected int vad_enabled;
    protected int vbr_enabled;
    protected float vbr_quality;
    private float[] window;
    private float[] x1d;

    public int encode(Bits bits, float[] fArr) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        char c;
        float[] fArr2;
        float[] fArr3;
        float[] fArr4;
        float f;
        int i7;
        float f2;
        float[] fArr5;
        int i8;
        int i9;
        int i10;
        int i11;
        int i12;
        int i13;
        int i14;
        int i15;
        int i16;
        float f3;
        float f4;
        Bits bits2 = bits;
        Filters.qmf_decomp(fArr, Codebook.f112h0, this.x0d, this.x1d, this.fullFrameSize, 64, this.h0_mem);
        this.lowenc.encode(bits2, this.x0d);
        int i17 = 0;
        int i18 = 0;
        while (true) {
            int i19 = this.windowSize;
            int i20 = this.frameSize;
            if (i18 >= i19 - i20) {
                break;
            }
            float[] fArr6 = this.high;
            fArr6[i18] = fArr6[i20 + i18];
            i18++;
        }
        int i21 = 0;
        while (true) {
            i = this.frameSize;
            if (i21 >= i) {
                break;
            }
            this.high[(this.windowSize - i) + i21] = this.x1d[i21];
            i21++;
        }
        float[] fArr7 = this.excBuf;
        System.arraycopy(fArr7, i, fArr7, 0, this.bufSize - i);
        float[] piGain = this.lowenc.getPiGain();
        float[] exc = this.lowenc.getExc();
        float[] innov = this.lowenc.getInnov();
        int i22 = 1;
        boolean z = this.lowenc.getMode() == 0;
        int i23 = 0;
        while (true) {
            i2 = this.windowSize;
            if (i23 >= i2) {
                break;
            }
            this.buf[i23] = this.high[i23] * this.window[i23];
            i23++;
        }
        Lpc.autocorr(this.buf, this.autocorr, this.lpcSize + 1, i2);
        float[] fArr8 = this.autocorr;
        fArr8[0] = fArr8[0] + 1.0f;
        fArr8[0] = fArr8[0] * this.lpc_floor;
        int i24 = 0;
        while (true) {
            i3 = this.lpcSize;
            if (i24 >= i3 + 1) {
                break;
            }
            float[] fArr9 = this.autocorr;
            fArr9[i24] = fArr9[i24] * this.lagWindow[i24];
            i24++;
        }
        Lpc.wld(this.lpc, this.autocorr, this.f120rc, i3);
        float[] fArr10 = this.lpc;
        System.arraycopy(fArr10, 0, fArr10, 1, this.lpcSize);
        float[] fArr11 = this.lpc;
        fArr11[0] = 1.0f;
        int lpc2lsp = Lsp.lpc2lsp(fArr11, this.lpcSize, this.lsp, 15, 0.2f);
        int i25 = this.lpcSize;
        if (lpc2lsp != i25 && Lsp.lpc2lsp(this.lpc, i25, this.lsp, 11, 0.02f) != this.lpcSize) {
            int i26 = 0;
            while (true) {
                int i27 = this.lpcSize;
                if (i26 >= i27) {
                    break;
                }
                int i28 = i26 + 1;
                this.lsp[i26] = (float) Math.cos((((double) ((float) i28)) * 3.141592653589793d) / ((double) (i27 + 1)));
                i26 = i28;
            }
        }
        for (int i29 = 0; i29 < this.lpcSize; i29++) {
            float[] fArr12 = this.lsp;
            fArr12[i29] = (float) Math.acos((double) fArr12[i29]);
        }
        for (int i30 = 0; i30 < this.lpcSize; i30++) {
            float[] fArr13 = this.old_lsp;
            float f5 = fArr13[i30];
            float[] fArr14 = this.lsp;
            float f6 = fArr14[i30];
            float f7 = fArr13[i30];
            float f8 = fArr14[i30];
        }
        float f9 = 0.05f;
        float f10 = 0.0f;
        if (!(this.vbr_enabled == 0 && this.vad_enabled == 0) && !z) {
            if (this.abr_enabled != 0) {
                float f11 = this.abr_drift2;
                float f12 = this.abr_drift;
                if (f11 * f12 > 0.0f) {
                    float f13 = (f12 * -1.0E-5f) / (this.abr_count + 1.0f);
                    f4 = 0.1f;
                    if (f13 <= 0.1f) {
                        f4 = f13;
                    }
                    if (f4 < -0.1f) {
                        f4 = -0.1f;
                    }
                } else {
                    f4 = 0.0f;
                }
                this.vbr_quality += f4;
                if (this.vbr_quality > 10.0f) {
                    this.vbr_quality = 10.0f;
                }
                if (this.vbr_quality < 0.0f) {
                    this.vbr_quality = 0.0f;
                }
            }
            float f14 = 0.0f;
            float f15 = 0.0f;
            for (int i31 = 0; i31 < this.frameSize; i31++) {
                float[] fArr15 = this.x0d;
                f15 += fArr15[i31] * fArr15[i31];
                float[] fArr16 = this.high;
                f14 += fArr16[i31] * fArr16[i31];
            }
            float log = (float) Math.log((double) ((f14 + 1.0f) / (f15 + 1.0f)));
            this.relative_quality = this.lowenc.getRelativeQuality();
            if (log < -4.0f) {
                log = -4.0f;
            }
            if (log > 2.0f) {
                log = 2.0f;
            }
            if (this.vbr_enabled != 0) {
                int i32 = this.nb_modes - 1;
                this.relative_quality = (float) (((double) this.relative_quality) + (((double) (log + 2.0f)) * 1.0d));
                if (this.relative_quality < -1.0f) {
                    this.relative_quality = -1.0f;
                }
                while (i32 != 0) {
                    int floor = (int) Math.floor((double) this.vbr_quality);
                    if (floor == 10) {
                        f3 = Vbr.hb_thresh[i32][floor];
                    } else {
                        float f16 = this.vbr_quality;
                        float[][] fArr17 = Vbr.hb_thresh;
                        int i33 = floor + 1;
                        f3 = ((f16 - ((float) floor)) * fArr17[i32][i33]) + ((((float) i33) - f16) * fArr17[i32][floor]);
                    }
                    if (this.relative_quality >= f3) {
                        break;
                    }
                    i32--;
                }
                setMode(i32);
                if (this.abr_enabled != 0) {
                    int bitRate = getBitRate();
                    float f17 = this.abr_drift;
                    int i34 = this.abr_enabled;
                    this.abr_drift = f17 + ((float) (bitRate - i34));
                    this.abr_drift2 = (this.abr_drift2 * 0.95f) + (((float) (bitRate - i34)) * 0.05f);
                    this.abr_count = (float) (((double) this.abr_count) + 1.0d);
                }
            } else {
                this.submodeID = ((double) this.relative_quality) < 2.0d ? 1 : this.submodeSelect;
            }
        }
        bits2.pack(1, 1);
        if (z) {
            bits2.pack(0, 3);
        } else {
            bits2.pack(this.submodeID, 3);
        }
        if (!z) {
            SubMode[] subModeArr = this.submodes;
            int i35 = this.submodeID;
            if (subModeArr[i35] != null) {
                subModeArr[i35].lsqQuant.quant(this.lsp, this.qlsp, this.lpcSize, bits2);
                if (this.first != 0) {
                    for (int i36 = 0; i36 < this.lpcSize; i36++) {
                        this.old_lsp[i36] = this.lsp[i36];
                    }
                    for (int i37 = 0; i37 < this.lpcSize; i37++) {
                        this.old_qlsp[i37] = this.qlsp[i37];
                    }
                }
                float[] fArr18 = new float[this.lpcSize];
                int i38 = this.subframeSize;
                float[] fArr19 = new float[i38];
                float[] fArr20 = new float[i38];
                int i39 = 0;
                while (true) {
                    int i40 = this.nbSubframes;
                    if (i39 >= i40) {
                        break;
                    }
                    int i41 = this.subframeSize * i39;
                    int i42 = this.excIdx + i41;
                    float f18 = (((float) i39) + 1.0f) / ((float) i40);
                    for (int i43 = i17; i43 < this.lpcSize; i43++) {
                        this.interp_lsp[i43] = ((1.0f - f18) * this.old_lsp[i43]) + (this.lsp[i43] * f18);
                    }
                    int i44 = 0;
                    while (true) {
                        i5 = this.lpcSize;
                        if (i44 >= i5) {
                            break;
                        }
                        this.interp_qlsp[i44] = ((1.0f - f18) * this.old_qlsp[i44]) + (this.qlsp[i44] * f18);
                        i44++;
                    }
                    Lsp.enforce_margin(this.interp_lsp, i5, f9);
                    Lsp.enforce_margin(this.interp_qlsp, this.lpcSize, f9);
                    for (int i45 = 0; i45 < this.lpcSize; i45++) {
                        float[] fArr21 = this.interp_lsp;
                        fArr21[i45] = (float) Math.cos((double) fArr21[i45]);
                    }
                    int i46 = 0;
                    while (true) {
                        i6 = this.lpcSize;
                        if (i46 >= i6) {
                            break;
                        }
                        float[] fArr22 = this.interp_qlsp;
                        fArr22[i46] = (float) Math.cos((double) fArr22[i46]);
                        i46++;
                    }
                    this.m_lsp.lsp2lpc(this.interp_lsp, this.interp_lpc, i6);
                    this.m_lsp.lsp2lpc(this.interp_qlsp, this.interp_qlpc, this.lpcSize);
                    Filters.bw_lpc(this.gamma1, this.interp_lpc, this.bw_lpc1, this.lpcSize);
                    Filters.bw_lpc(this.gamma2, this.interp_lpc, this.bw_lpc2, this.lpcSize);
                    this.pi_gain[i39] = f10;
                    float f19 = f10;
                    float f20 = 1.0f;
                    for (int i47 = 0; i47 <= this.lpcSize; i47++) {
                        float[] fArr23 = this.interp_qlpc;
                        f19 += fArr23[i47] * f20;
                        f20 = -f20;
                        float[] fArr24 = this.pi_gain;
                        fArr24[i39] = fArr24[i39] + fArr23[i47];
                    }
                    float abs = Math.abs((1.0f / (Math.abs(f19) + 0.01f)) + 0.01f) / (Math.abs(1.0f / (Math.abs(piGain[i39]) + 0.01f)) + 0.01f);
                    int i48 = (abs > 5.0f ? 1 : (abs == 5.0f ? 0 : -1));
                    Filters.fir_mem2(this.high, i41, this.interp_qlpc, this.excBuf, i42, this.subframeSize, this.lpcSize, this.mem_sp2);
                    float f21 = 0.0f;
                    for (int i49 = 0; i49 < this.subframeSize; i49++) {
                        float[] fArr25 = this.excBuf;
                        int i50 = i42 + i49;
                        f21 += fArr25[i50] * fArr25[i50];
                    }
                    if (this.submodes[this.submodeID].innovation == null) {
                        float f22 = 0.0f;
                        for (int i51 = 0; i51 < this.subframeSize; i51++) {
                            int i52 = i41 + i51;
                            f22 += innov[i52] * innov[i52];
                        }
                        int floor2 = (int) Math.floor((Math.log(((double) (((float) Math.sqrt((double) (f21 / (f22 + 0.01f)))) * abs)) + 1.0E-4d) * 8.0d) + 10.5d);
                        if (floor2 < 0) {
                            floor2 = 0;
                        }
                        if (floor2 > 31) {
                            floor2 = 31;
                        }
                        bits2.pack(floor2, 5);
                        Math.exp(((double) floor2) / 9.4d);
                        i7 = i39;
                        fArr3 = fArr20;
                        fArr2 = fArr18;
                        fArr4 = piGain;
                        i8 = 1;
                        f2 = 0.05f;
                        f = 0.0f;
                        c = 15;
                        fArr5 = fArr19;
                    } else {
                        float f23 = 0.0f;
                        for (int i53 = 0; i53 < this.subframeSize; i53++) {
                            int i54 = i41 + i53;
                            f23 += exc[i54] * exc[i54];
                        }
                        float f24 = f23 + 1.0f;
                        fArr4 = piGain;
                        int floor3 = (int) Math.floor(((Math.log((double) ((float) ((Math.sqrt((double) (f21 + 1.0f)) * ((double) abs)) / Math.sqrt((double) (((float) this.subframeSize) * f24))))) + 2.0d) * 3.7d) + 0.5d);
                        int i55 = floor3 < 0 ? 0 : floor3;
                        if (i55 > 15) {
                            i55 = 15;
                        }
                        bits2.pack(i55, 4);
                        float exp = (((float) Math.exp((((double) i55) * 0.27027027027027023d) - 2.0d)) * ((float) Math.sqrt((double) f24))) / abs;
                        float f25 = 1.0f / exp;
                        int i56 = 0;
                        while (true) {
                            i10 = this.subframeSize;
                            if (i56 >= i10) {
                                break;
                            }
                            this.excBuf[i42 + i56] = 0.0f;
                            i56++;
                        }
                        float[] fArr26 = this.excBuf;
                        fArr26[i42] = 1.0f;
                        Filters.syn_percep_zero(fArr26, i42, this.interp_qlpc, this.bw_lpc1, this.bw_lpc2, fArr19, i10, this.lpcSize);
                        for (int i57 = 0; i57 < this.subframeSize; i57++) {
                            this.excBuf[i42 + i57] = 0.0f;
                        }
                        int i58 = 0;
                        while (true) {
                            i11 = this.lpcSize;
                            if (i58 >= i11) {
                                break;
                            }
                            fArr18[i58] = this.mem_sp[i58];
                            i58++;
                        }
                        float[] fArr27 = this.excBuf;
                        Filters.iir_mem2(fArr27, i42, this.interp_qlpc, fArr27, i42, this.subframeSize, i11, fArr18);
                        int i59 = 0;
                        while (true) {
                            i12 = this.lpcSize;
                            if (i59 >= i12) {
                                break;
                            }
                            fArr18[i59] = this.mem_sw[i59];
                            i59++;
                        }
                        Filters.filter_mem2(this.excBuf, i42, this.bw_lpc1, this.bw_lpc2, this.res, i41, this.subframeSize, i12, fArr18, 0);
                        int i60 = 0;
                        while (true) {
                            i13 = this.lpcSize;
                            if (i60 >= i13) {
                                break;
                            }
                            fArr18[i60] = this.mem_sw[i60];
                            i60++;
                        }
                        Filters.filter_mem2(this.high, i41, this.bw_lpc1, this.bw_lpc2, this.swBuf, i41, this.subframeSize, i13, fArr18, 0);
                        for (int i61 = 0; i61 < this.subframeSize; i61++) {
                            int i62 = i41 + i61;
                            this.target[i61] = this.swBuf[i62] - this.res[i62];
                        }
                        for (int i63 = 0; i63 < this.subframeSize; i63++) {
                            this.excBuf[i42 + i63] = 0.0f;
                        }
                        for (int i64 = 0; i64 < this.subframeSize; i64++) {
                            float[] fArr28 = this.target;
                            fArr28[i64] = fArr28[i64] * f25;
                        }
                        int i65 = 0;
                        while (true) {
                            i14 = this.subframeSize;
                            if (i65 >= i14) {
                                break;
                            }
                            fArr20[i65] = 0.0f;
                            i65++;
                        }
                        i7 = i39;
                        fArr3 = fArr20;
                        fArr5 = fArr19;
                        fArr2 = fArr18;
                        f = 0.0f;
                        f2 = 0.05f;
                        c = 15;
                        i8 = 1;
                        this.submodes[this.submodeID].innovation.quant(this.target, this.interp_qlpc, this.bw_lpc1, this.bw_lpc2, this.lpcSize, i14, fArr3, 0, fArr5, bits, (this.complexity + 1) >> 1);
                        int i66 = 0;
                        while (true) {
                            i15 = this.subframeSize;
                            if (i66 >= i15) {
                                break;
                            }
                            float[] fArr29 = this.excBuf;
                            int i67 = i42 + i66;
                            fArr29[i67] = fArr29[i67] + (fArr3[i66] * exp);
                            i66++;
                        }
                        if (this.submodes[this.submodeID].double_codebook != 0) {
                            float[] fArr30 = new float[i15];
                            for (int i68 = 0; i68 < this.subframeSize; i68++) {
                                fArr30[i68] = 0.0f;
                            }
                            int i69 = 0;
                            while (true) {
                                i16 = this.subframeSize;
                                if (i69 >= i16) {
                                    break;
                                }
                                float[] fArr31 = this.target;
                                fArr31[i69] = (float) (((double) fArr31[i69]) * 2.5d);
                                i69++;
                            }
                            float[] fArr32 = fArr30;
                            this.submodes[this.submodeID].innovation.quant(this.target, this.interp_qlpc, this.bw_lpc1, this.bw_lpc2, this.lpcSize, i16, fArr30, 0, fArr5, bits, (this.complexity + 1) >> 1);
                            for (int i70 = 0; i70 < this.subframeSize; i70++) {
                                fArr32[i70] = (float) (((double) fArr32[i70]) * ((double) exp) * 0.4d);
                            }
                            for (int i71 = 0; i71 < this.subframeSize; i71++) {
                                float[] fArr33 = this.excBuf;
                                int i72 = i42 + i71;
                                fArr33[i72] = fArr33[i72] + fArr32[i71];
                            }
                        }
                    }
                    int i73 = 0;
                    while (true) {
                        i9 = this.lpcSize;
                        if (i73 >= i9) {
                            break;
                        }
                        fArr2[i73] = this.mem_sp[i73];
                        i73++;
                    }
                    Filters.iir_mem2(this.excBuf, i42, this.interp_qlpc, this.high, i41, this.subframeSize, i9, this.mem_sp);
                    Filters.filter_mem2(this.high, i41, this.bw_lpc1, this.bw_lpc2, this.swBuf, i41, this.subframeSize, this.lpcSize, this.mem_sw, 0);
                    i39 = i7 + 1;
                    i22 = i8;
                    fArr19 = fArr5;
                    f9 = f2;
                    f10 = f;
                    piGain = fArr4;
                    fArr20 = fArr3;
                    fArr18 = fArr2;
                    char c2 = c;
                    i17 = 0;
                    bits2 = bits;
                }
                int i74 = i22;
                this.filters.fir_mem_up(this.x0d, Codebook.f112h0, this.f118y0, this.fullFrameSize, 64, this.g0_mem);
                this.filters.fir_mem_up(this.high, Codebook.f113h1, this.f119y1, this.fullFrameSize, 64, this.g1_mem);
                for (int i75 = 0; i75 < this.fullFrameSize; i75++) {
                    fArr[i75] = (this.f118y0[i75] - this.f119y1[i75]) * 2.0f;
                }
                for (int i76 = 0; i76 < this.lpcSize; i76++) {
                    this.old_lsp[i76] = this.lsp[i76];
                }
                for (int i77 = 0; i77 < this.lpcSize; i77++) {
                    this.old_qlsp[i77] = this.qlsp[i77];
                }
                this.first = 0;
                return i74;
            }
        }
        for (int i78 = 0; i78 < this.frameSize; i78++) {
            this.swBuf[i78] = 0.0f;
            this.excBuf[this.excIdx + i78] = 0.0f;
        }
        int i79 = 0;
        while (true) {
            i4 = this.lpcSize;
            if (i79 >= i4) {
                break;
            }
            this.mem_sw[i79] = 0.0f;
            i79++;
        }
        this.first = 1;
        Filters.iir_mem2(this.excBuf, this.excIdx, this.interp_qlpc, this.high, 0, this.subframeSize, i4, this.mem_sp);
        this.filters.fir_mem_up(this.x0d, Codebook.f112h0, this.f118y0, this.fullFrameSize, 64, this.g0_mem);
        this.filters.fir_mem_up(this.high, Codebook.f113h1, this.f119y1, this.fullFrameSize, 64, this.g1_mem);
        for (int i80 = 0; i80 < this.fullFrameSize; i80++) {
            fArr[i80] = (this.f118y0[i80] - this.f119y1[i80]) * 2.0f;
        }
        return z ? 0 : 1;
    }

    public int getBitRate() {
        int bitRate;
        int i;
        if (this.submodes[this.submodeID] != null) {
            bitRate = this.lowenc.getBitRate();
            i = this.sampling_rate * this.submodes[this.submodeID].bits_per_frame;
        } else {
            bitRate = this.lowenc.getBitRate();
            i = this.sampling_rate * 4;
        }
        return bitRate + (i / this.frameSize);
    }

    public int getMode() {
        return this.submodeID;
    }

    public float getRelativeQuality() {
        return this.relative_quality;
    }

    public void init(int i, int i2, int i3, int i4, float f) {
        super.init(i, i2, i3, i4, f);
        this.complexity = 3;
        this.vbr_enabled = 0;
        this.vad_enabled = 0;
        this.abr_enabled = 0;
        this.vbr_quality = 8.0f;
        this.submodeSelect = this.submodeID;
        this.x1d = new float[i];
        this.h0_mem = new float[64];
        int i5 = this.windowSize;
        this.buf = new float[i5];
        this.swBuf = new float[i];
        this.res = new float[i];
        this.target = new float[i2];
        this.window = Misc.window(i5, i2);
        this.lagWindow = Misc.lagWindow(i3, this.lag_factor);
        this.f120rc = new float[i3];
        int i6 = i3 + 1;
        this.autocorr = new float[i6];
        this.lsp = new float[i3];
        this.old_lsp = new float[i3];
        this.interp_lsp = new float[i3];
        this.interp_lpc = new float[i6];
        this.bw_lpc1 = new float[i6];
        this.bw_lpc2 = new float[i6];
        this.mem_sp2 = new float[i3];
        this.mem_sw = new float[i3];
        this.abr_count = 0.0f;
    }

    public void setMode(int i) {
        if (i < 0) {
            i = 0;
        }
        this.submodeSelect = i;
        this.submodeID = i;
    }

    public void setQuality(int i) {
        int i2;
        if (i < 0) {
            i = 0;
        }
        if (i > 10) {
            i = 10;
        }
        if (this.uwb) {
            this.lowenc.setQuality(i);
            i2 = UWB_QUALITY_MAP[i];
        } else {
            this.lowenc.setMode(NB_QUALITY_MAP[i]);
            i2 = WB_QUALITY_MAP[i];
        }
        setMode(i2);
    }

    public void setVbr(boolean z) {
        this.vbr_enabled = z ? 1 : 0;
        this.lowenc.setVbr(z);
    }

    public void setVbrQuality(float f) {
        this.vbr_quality = f;
        float f2 = 0.6f + f;
        if (f2 > 10.0f) {
            f2 = 10.0f;
        }
        this.lowenc.setVbrQuality(f2);
        int floor = (int) Math.floor(((double) f) + 0.5d);
        if (floor > 10) {
            floor = 10;
        }
        setQuality(floor);
    }

    public void uwbinit() {
        this.lowenc = new SbEncoder();
        ((SbEncoder) this.lowenc).wbinit();
        super.uwbinit();
        init(320, 80, 8, 1280, 0.7f);
        this.uwb = true;
        this.nb_modes = 2;
        this.sampling_rate = 32000;
    }

    public void wbinit() {
        this.lowenc = new NbEncoder();
        ((NbEncoder) this.lowenc).nbinit();
        super.wbinit();
        init(160, 40, 8, 640, 0.9f);
        this.uwb = false;
        this.nb_modes = 5;
        this.sampling_rate = 16000;
    }
}
