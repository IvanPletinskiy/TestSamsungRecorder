package org.xiph.speex;

public class NbEncoder extends NbCodec implements Encoder {
    public static final int[] NB_QUALITY_MAP = {1, 8, 2, 3, 3, 4, 4, 5, 5, 6, 7};
    protected float abr_count;
    protected float abr_drift;
    protected float abr_drift2;
    protected int abr_enabled;
    private float[] autocorr;
    private int bounded_pitch;
    private float[] buf2;
    private float[] bw_lpc1;
    private float[] bw_lpc2;
    protected int complexity;
    private int dtx_count;
    private float[] exc2Buf;
    private int exc2Idx;
    private float[] innov2;
    private float[] interp_lpc;
    private float[] interp_lsp;
    private float[] lagWindow;
    private float[] lsp;
    private float[] mem_exc;
    private float[] mem_sw;
    private float[] mem_sw_whole;
    private float[] old_lsp;
    private int[] pitch;
    private float pre_mem2;

    /* renamed from: rc */
    private float[] f117rc;
    protected float relative_quality;
    protected int sampling_rate;
    protected int submodeSelect;
    private float[] swBuf;
    private int swIdx;
    protected int vad_enabled;
    private Vbr vbr;
    protected int vbr_enabled;
    protected float vbr_quality;
    private float[] window;

    /* JADX WARNING: Removed duplicated region for block: B:359:0x0a14 A[LOOP:48: B:357:0x0a10->B:359:0x0a14, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x0a52 A[LOOP:49: B:361:0x0a4e->B:363:0x0a52, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x0a66 A[EDGE_INSN: B:425:0x0a66->B:364:0x0a66 ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int encode(org.xiph.speex.Bits r60, float[] r61) {
        /*
            r59 = this;
            r0 = r59
            r15 = r60
            r12 = r61
            float[] r1 = r0.frmBuf
            int r2 = r0.frameSize
            int r3 = r0.bufSize
            int r3 = r3 - r2
            r11 = 0
            java.lang.System.arraycopy(r1, r2, r1, r11, r3)
            float[] r1 = r0.frmBuf
            int r2 = r0.bufSize
            int r3 = r0.frameSize
            int r2 = r2 - r3
            r3 = r12[r11]
            float r4 = r0.preemph
            float r5 = r0.pre_mem
            float r4 = r4 * r5
            float r3 = r3 - r4
            r1[r2] = r3
            r10 = 1
            r1 = r10
        L_0x0024:
            int r2 = r0.frameSize
            if (r1 >= r2) goto L_0x003d
            float[] r3 = r0.frmBuf
            int r4 = r0.bufSize
            int r4 = r4 - r2
            int r4 = r4 + r1
            r2 = r12[r1]
            float r5 = r0.preemph
            int r6 = r1 + -1
            r6 = r12[r6]
            float r5 = r5 * r6
            float r2 = r2 - r5
            r3[r4] = r2
            int r1 = r1 + 1
            goto L_0x0024
        L_0x003d:
            int r1 = r2 + -1
            r1 = r12[r1]
            r0.pre_mem = r1
            float[] r1 = r0.exc2Buf
            int r3 = r0.bufSize
            int r3 = r3 - r2
            java.lang.System.arraycopy(r1, r2, r1, r11, r3)
            float[] r1 = r0.excBuf
            int r2 = r0.frameSize
            int r3 = r0.bufSize
            int r3 = r3 - r2
            java.lang.System.arraycopy(r1, r2, r1, r11, r3)
            float[] r1 = r0.swBuf
            int r2 = r0.frameSize
            int r3 = r0.bufSize
            int r3 = r3 - r2
            java.lang.System.arraycopy(r1, r2, r1, r11, r3)
            r1 = r11
        L_0x0060:
            int r2 = r0.windowSize
            if (r1 >= r2) goto L_0x0077
            float[] r2 = r0.buf2
            float[] r3 = r0.frmBuf
            int r4 = r0.frmIdx
            int r4 = r4 + r1
            r3 = r3[r4]
            float[] r4 = r0.window
            r4 = r4[r1]
            float r3 = r3 * r4
            r2[r1] = r3
            int r1 = r1 + 1
            goto L_0x0060
        L_0x0077:
            float[] r1 = r0.buf2
            float[] r3 = r0.autocorr
            int r4 = r0.lpcSize
            int r4 = r4 + r10
            org.xiph.speex.Lpc.autocorr(r1, r3, r4, r2)
            float[] r1 = r0.autocorr
            r2 = r1[r11]
            r3 = 1092616192(0x41200000, float:10.0)
            float r2 = r2 + r3
            r1[r11] = r2
            r2 = r1[r11]
            float r4 = r0.lpc_floor
            float r2 = r2 * r4
            r1[r11] = r2
            r1 = r11
        L_0x0092:
            int r2 = r0.lpcSize
            int r4 = r2 + 1
            if (r1 >= r4) goto L_0x00a6
            float[] r2 = r0.autocorr
            r4 = r2[r1]
            float[] r5 = r0.lagWindow
            r5 = r5[r1]
            float r4 = r4 * r5
            r2[r1] = r4
            int r1 = r1 + 1
            goto L_0x0092
        L_0x00a6:
            float[] r1 = r0.lpc
            float[] r4 = r0.autocorr
            float[] r5 = r0.f117rc
            org.xiph.speex.Lpc.wld(r1, r4, r5, r2)
            float[] r1 = r0.lpc
            int r2 = r0.lpcSize
            java.lang.System.arraycopy(r1, r11, r1, r10, r2)
            float[] r1 = r0.lpc
            r20 = 1065353216(0x3f800000, float:1.0)
            r1[r11] = r20
            int r2 = r0.lpcSize
            float[] r4 = r0.lsp
            r5 = 1045220557(0x3e4ccccd, float:0.2)
            r9 = 15
            int r1 = org.xiph.speex.Lsp.lpc2lsp(r1, r2, r4, r9, r5)
            int r2 = r0.lpcSize
            r4 = 1028443341(0x3d4ccccd, float:0.05)
            if (r1 != r2) goto L_0x00e4
            r1 = r11
        L_0x00d1:
            int r2 = r0.lpcSize
            if (r1 >= r2) goto L_0x011a
            float[] r2 = r0.lsp
            r5 = r2[r1]
            double r5 = (double) r5
            double r5 = java.lang.Math.acos(r5)
            float r5 = (float) r5
            r2[r1] = r5
            int r1 = r1 + 1
            goto L_0x00d1
        L_0x00e4:
            int r5 = r0.complexity
            if (r5 <= r10) goto L_0x00f2
            float[] r1 = r0.lpc
            float[] r5 = r0.lsp
            r6 = 11
            int r1 = org.xiph.speex.Lsp.lpc2lsp(r1, r2, r5, r6, r4)
        L_0x00f2:
            int r2 = r0.lpcSize
            if (r1 != r2) goto L_0x010a
            r1 = r11
        L_0x00f7:
            int r2 = r0.lpcSize
            if (r1 >= r2) goto L_0x011a
            float[] r2 = r0.lsp
            r5 = r2[r1]
            double r5 = (double) r5
            double r5 = java.lang.Math.acos(r5)
            float r5 = (float) r5
            r2[r1] = r5
            int r1 = r1 + 1
            goto L_0x00f7
        L_0x010a:
            r1 = r11
        L_0x010b:
            int r2 = r0.lpcSize
            if (r1 >= r2) goto L_0x011a
            float[] r2 = r0.lsp
            float[] r5 = r0.old_lsp
            r5 = r5[r1]
            r2[r1] = r5
            int r1 = r1 + 1
            goto L_0x010b
        L_0x011a:
            r1 = r11
            r5 = 0
        L_0x011c:
            int r6 = r0.lpcSize
            if (r1 >= r6) goto L_0x0133
            float[] r6 = r0.old_lsp
            r7 = r6[r1]
            float[] r8 = r0.lsp
            r13 = r8[r1]
            float r7 = r7 - r13
            r6 = r6[r1]
            r8 = r8[r1]
            float r6 = r6 - r8
            float r7 = r7 * r6
            float r5 = r5 + r7
            int r1 = r1 + 1
            goto L_0x011c
        L_0x0133:
            int r1 = r0.first
            if (r1 == 0) goto L_0x0147
            r1 = r11
        L_0x0138:
            int r6 = r0.lpcSize
            if (r1 >= r6) goto L_0x0162
            float[] r6 = r0.interp_lsp
            float[] r7 = r0.lsp
            r7 = r7[r1]
            r6[r1] = r7
            int r1 = r1 + 1
            goto L_0x0138
        L_0x0147:
            r1 = r11
        L_0x0148:
            int r6 = r0.lpcSize
            if (r1 >= r6) goto L_0x0162
            float[] r6 = r0.interp_lsp
            r7 = 1052770304(0x3ec00000, float:0.375)
            float[] r8 = r0.old_lsp
            r8 = r8[r1]
            float r8 = r8 * r7
            r7 = 1059061760(0x3f200000, float:0.625)
            float[] r13 = r0.lsp
            r13 = r13[r1]
            float r13 = r13 * r7
            float r8 = r8 + r13
            r6[r1] = r8
            int r1 = r1 + 1
            goto L_0x0148
        L_0x0162:
            float[] r1 = r0.interp_lsp
            int r6 = r0.lpcSize
            r14 = 990057071(0x3b03126f, float:0.002)
            org.xiph.speex.Lsp.enforce_margin(r1, r6, r14)
            r1 = r11
        L_0x016d:
            int r6 = r0.lpcSize
            if (r1 >= r6) goto L_0x0180
            float[] r6 = r0.interp_lsp
            r7 = r6[r1]
            double r7 = (double) r7
            double r7 = java.lang.Math.cos(r7)
            float r7 = (float) r7
            r6[r1] = r7
            int r1 = r1 + 1
            goto L_0x016d
        L_0x0180:
            org.xiph.speex.Lsp r1 = r0.m_lsp
            float[] r7 = r0.interp_lsp
            float[] r8 = r0.interp_lpc
            r1.lsp2lpc(r7, r8, r6)
            org.xiph.speex.SubMode[] r1 = r0.submodes
            int r6 = r0.submodeID
            r7 = r1[r6]
            r13 = -1
            r21 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            if (r7 == 0) goto L_0x01ae
            int r7 = r0.vbr_enabled
            if (r7 != 0) goto L_0x01ae
            int r7 = r0.vad_enabled
            if (r7 != 0) goto L_0x01ae
            r7 = r1[r6]
            int r7 = r7.forced_pitch_gain
            if (r7 != 0) goto L_0x01ae
            r1 = r1[r6]
            int r1 = r1.lbr_pitch
            if (r1 == r13) goto L_0x01a9
            goto L_0x01ae
        L_0x01a9:
            r10 = r5
            r3 = r11
            r2 = 0
            goto L_0x026e
        L_0x01ae:
            r1 = 6
            int[] r6 = new int[r1]
            float[] r7 = new float[r1]
            float r8 = r0.gamma1
            float[] r14 = r0.interp_lpc
            float[] r9 = r0.bw_lpc1
            int r13 = r0.lpcSize
            org.xiph.speex.Filters.bw_lpc(r8, r14, r9, r13)
            float r8 = r0.gamma2
            float[] r9 = r0.interp_lpc
            float[] r13 = r0.bw_lpc2
            int r14 = r0.lpcSize
            org.xiph.speex.Filters.bw_lpc(r8, r9, r13, r14)
            float[] r8 = r0.frmBuf
            int r9 = r0.frmIdx
            float[] r13 = r0.bw_lpc1
            float[] r14 = r0.bw_lpc2
            float[] r10 = r0.swBuf
            int r3 = r0.swIdx
            int r4 = r0.frameSize
            int r2 = r0.lpcSize
            float[] r1 = r0.mem_sw_whole
            r32 = 0
            r23 = r8
            r24 = r9
            r25 = r13
            r26 = r14
            r27 = r10
            r28 = r3
            r29 = r4
            r30 = r2
            r31 = r1
            org.xiph.speex.Filters.filter_mem2(r23, r24, r25, r26, r27, r28, r29, r30, r31, r32)
            float[] r1 = r0.swBuf
            int r2 = r0.swIdx
            int r3 = r0.min_pitch
            int r4 = r0.max_pitch
            int r8 = r0.frameSize
            r30 = 6
            r23 = r1
            r24 = r2
            r25 = r3
            r26 = r4
            r27 = r8
            r28 = r6
            r29 = r7
            org.xiph.speex.Ltp.open_loop_nbest_pitch(r23, r24, r25, r26, r27, r28, r29, r30)
            r1 = r6[r11]
            r2 = r7[r11]
            r3 = r1
            r1 = 1
        L_0x0215:
            r4 = 6
            if (r1 >= r4) goto L_0x026d
            r8 = r7[r1]
            double r8 = (double) r8
            r13 = 4605831338911806259(0x3feb333333333333, double:0.85)
            r10 = r5
            double r4 = (double) r2
            double r4 = r4 * r13
            int r4 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
            if (r4 <= 0) goto L_0x0269
            r4 = r6[r1]
            double r4 = (double) r4
            double r8 = (double) r3
            r13 = 4611686018427387904(0x4000000000000000, double:2.0)
            double r13 = r8 / r13
            double r4 = r4 - r13
            double r4 = java.lang.Math.abs(r4)
            int r4 = (r4 > r21 ? 1 : (r4 == r21 ? 0 : -1))
            if (r4 <= 0) goto L_0x0267
            r4 = r6[r1]
            double r4 = (double) r4
            r13 = 4613937818241073152(0x4008000000000000, double:3.0)
            double r13 = r8 / r13
            double r4 = r4 - r13
            double r4 = java.lang.Math.abs(r4)
            int r4 = (r4 > r21 ? 1 : (r4 == r21 ? 0 : -1))
            if (r4 <= 0) goto L_0x0267
            r4 = r6[r1]
            double r4 = (double) r4
            r13 = 4616189618054758400(0x4010000000000000, double:4.0)
            double r13 = r8 / r13
            double r4 = r4 - r13
            double r4 = java.lang.Math.abs(r4)
            int r4 = (r4 > r21 ? 1 : (r4 == r21 ? 0 : -1))
            if (r4 <= 0) goto L_0x0267
            r4 = r6[r1]
            double r4 = (double) r4
            r13 = 4617315517961601024(0x4014000000000000, double:5.0)
            double r8 = r8 / r13
            double r4 = r4 - r8
            double r4 = java.lang.Math.abs(r4)
            int r4 = (r4 > r21 ? 1 : (r4 == r21 ? 0 : -1))
            if (r4 > 0) goto L_0x0269
        L_0x0267:
            r3 = r6[r1]
        L_0x0269:
            int r1 = r1 + 1
            r5 = r10
            goto L_0x0215
        L_0x026d:
            r10 = r5
        L_0x026e:
            float[] r1 = r0.frmBuf
            int r4 = r0.frmIdx
            float[] r5 = r0.interp_lpc
            float[] r6 = r0.excBuf
            int r7 = r0.excIdx
            int r8 = r0.frameSize
            int r9 = r0.lpcSize
            float[] r13 = r0.mem_exc
            r23 = r1
            r24 = r4
            r25 = r5
            r26 = r6
            r27 = r7
            r28 = r8
            r29 = r9
            r30 = r13
            org.xiph.speex.Filters.fir_mem2(r23, r24, r25, r26, r27, r28, r29, r30)
            r1 = r11
            r4 = 0
        L_0x0293:
            int r5 = r0.frameSize
            if (r1 >= r5) goto L_0x02a7
            float[] r5 = r0.excBuf
            int r6 = r0.excIdx
            int r7 = r6 + r1
            r7 = r5[r7]
            int r6 = r6 + r1
            r5 = r5[r6]
            float r7 = r7 * r5
            float r4 = r4 + r7
            int r1 = r1 + 1
            goto L_0x0293
        L_0x02a7:
            float r1 = (float) r5
            float r4 = r4 / r1
            float r4 = r4 + r20
            double r4 = (double) r4
            double r4 = java.lang.Math.sqrt(r4)
            float r1 = (float) r4
            org.xiph.speex.Vbr r4 = r0.vbr
            if (r4 == 0) goto L_0x03da
            int r4 = r0.vbr_enabled
            if (r4 != 0) goto L_0x02bd
            int r4 = r0.vad_enabled
            if (r4 == 0) goto L_0x03da
        L_0x02bd:
            int r4 = r0.abr_enabled
            if (r4 == 0) goto L_0x0301
            float r4 = r0.abr_drift2
            float r5 = r0.abr_drift
            float r4 = r4 * r5
            r6 = 0
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            r6 = -1119040307(0xffffffffbd4ccccd, float:-0.05)
            if (r4 <= 0) goto L_0x02e8
            r4 = -1222130260(0xffffffffb727c5ac, float:-1.0E-5)
            float r5 = r5 * r4
            float r4 = r0.abr_count
            float r4 = r4 + r20
            float r4 = r5 / r4
            r5 = 1028443341(0x3d4ccccd, float:0.05)
            int r7 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r7 <= 0) goto L_0x02e2
            r4 = 1028443341(0x3d4ccccd, float:0.05)
        L_0x02e2:
            int r5 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r5 >= 0) goto L_0x02e9
            r4 = r6
            goto L_0x02e9
        L_0x02e8:
            r4 = 0
        L_0x02e9:
            float r5 = r0.vbr_quality
            float r5 = r5 + r4
            r0.vbr_quality = r5
            float r4 = r0.vbr_quality
            r5 = 1092616192(0x41200000, float:10.0)
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 <= 0) goto L_0x02f8
            r0.vbr_quality = r5
        L_0x02f8:
            float r4 = r0.vbr_quality
            r5 = 0
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 >= 0) goto L_0x0301
            r0.vbr_quality = r5
        L_0x0301:
            org.xiph.speex.Vbr r4 = r0.vbr
            int r5 = r0.frameSize
            float r4 = r4.analysis(r12, r5, r3, r2)
            r0.relative_quality = r4
            int r4 = r0.vbr_enabled
            if (r4 == 0) goto L_0x03a8
            r4 = 1120403456(0x42c80000, float:100.0)
            r6 = r4
            r5 = r11
            r4 = 8
        L_0x0315:
            if (r4 <= 0) goto L_0x0354
            float r7 = r0.vbr_quality
            double r7 = (double) r7
            double r7 = java.lang.Math.floor(r7)
            int r7 = (int) r7
            r8 = 10
            if (r7 != r8) goto L_0x032a
            float[][] r8 = org.xiph.speex.Vbr.nb_thresh
            r8 = r8[r4]
            r7 = r8[r7]
            goto L_0x0342
        L_0x032a:
            float r8 = r0.vbr_quality
            float r13 = (float) r7
            float r13 = r8 - r13
            float[][] r14 = org.xiph.speex.Vbr.nb_thresh
            r19 = r14[r4]
            int r9 = r7 + 1
            r19 = r19[r9]
            float r13 = r13 * r19
            float r9 = (float) r9
            float r9 = r9 - r8
            r8 = r14[r4]
            r7 = r8[r7]
            float r9 = r9 * r7
            float r7 = r13 + r9
        L_0x0342:
            float r8 = r0.relative_quality
            int r9 = (r8 > r7 ? 1 : (r8 == r7 ? 0 : -1))
            if (r9 <= 0) goto L_0x0351
            float r9 = r8 - r7
            int r9 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
            if (r9 >= 0) goto L_0x0351
            float r8 = r8 - r7
            r5 = r4
            r6 = r8
        L_0x0351:
            int r4 = r4 + -1
            goto L_0x0315
        L_0x0354:
            if (r5 != 0) goto L_0x0378
            int r4 = r0.dtx_count
            if (r4 == 0) goto L_0x0373
            double r5 = (double) r10
            r7 = 4587366580439587226(0x3fa999999999999a, double:0.05)
            int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r5 > 0) goto L_0x0373
            int r5 = r0.dtx_enabled
            if (r5 == 0) goto L_0x0373
            r5 = 20
            if (r4 <= r5) goto L_0x036d
            goto L_0x0373
        L_0x036d:
            r5 = 1
            int r4 = r4 + r5
            r0.dtx_count = r4
            r5 = r11
            goto L_0x037a
        L_0x0373:
            r5 = 1
            r0.dtx_count = r5
            r5 = 1
            goto L_0x037a
        L_0x0378:
            r0.dtx_count = r11
        L_0x037a:
            r0.setMode(r5)
            int r4 = r0.abr_enabled
            if (r4 == 0) goto L_0x03a6
            int r4 = r59.getBitRate()
            float r5 = r0.abr_drift
            int r6 = r0.abr_enabled
            int r7 = r4 - r6
            float r7 = (float) r7
            float r5 = r5 + r7
            r0.abr_drift = r5
            r5 = 1064514355(0x3f733333, float:0.95)
            float r7 = r0.abr_drift2
            float r7 = r7 * r5
            int r4 = r4 - r6
            float r4 = (float) r4
            r5 = 1028443341(0x3d4ccccd, float:0.05)
            float r4 = r4 * r5
            float r7 = r7 + r4
            r0.abr_drift2 = r7
            float r4 = r0.abr_count
            double r4 = (double) r4
            double r4 = r4 + r21
            float r4 = (float) r4
            r0.abr_count = r4
        L_0x03a6:
            r5 = 1
            goto L_0x03df
        L_0x03a8:
            float r4 = r0.relative_quality
            r5 = 1073741824(0x40000000, float:2.0)
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 >= 0) goto L_0x03d2
            int r4 = r0.dtx_count
            if (r4 == 0) goto L_0x03cd
            double r5 = (double) r10
            r7 = 4587366580439587226(0x3fa999999999999a, double:0.05)
            int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r5 > 0) goto L_0x03cd
            int r5 = r0.dtx_enabled
            if (r5 == 0) goto L_0x03cd
            r5 = 20
            if (r4 <= r5) goto L_0x03c7
            goto L_0x03cd
        L_0x03c7:
            r5 = 1
            int r4 = r4 + r5
            r0.dtx_count = r4
            r4 = r11
            goto L_0x03d7
        L_0x03cd:
            r5 = 1
            r0.dtx_count = r5
            r4 = r5
            goto L_0x03d7
        L_0x03d2:
            r5 = 1
            r0.dtx_count = r11
            int r4 = r0.submodeSelect
        L_0x03d7:
            r0.submodeID = r4
            goto L_0x03df
        L_0x03da:
            r5 = 1
            r4 = -1082130432(0xffffffffbf800000, float:-1.0)
            r0.relative_quality = r4
        L_0x03df:
            r15.pack(r11, r5)
            int r4 = r0.submodeID
            r10 = 4
            r15.pack(r4, r10)
            org.xiph.speex.SubMode[] r4 = r0.submodes
            int r5 = r0.submodeID
            r4 = r4[r5]
            if (r4 != 0) goto L_0x0469
            r1 = r11
        L_0x03f1:
            int r2 = r0.frameSize
            if (r1 >= r2) goto L_0x040e
            float[] r2 = r0.excBuf
            int r3 = r0.excIdx
            int r3 = r3 + r1
            float[] r4 = r0.exc2Buf
            int r5 = r0.exc2Idx
            int r5 = r5 + r1
            float[] r6 = r0.swBuf
            int r7 = r0.swIdx
            int r7 = r7 + r1
            r8 = 0
            r6[r7] = r8
            r4[r5] = r8
            r2[r3] = r8
            int r1 = r1 + 1
            goto L_0x03f1
        L_0x040e:
            r8 = 0
            r1 = r11
        L_0x0410:
            int r2 = r0.lpcSize
            if (r1 >= r2) goto L_0x041c
            float[] r2 = r0.mem_sw
            r2[r1] = r8
            int r1 = r1 + 1
            r8 = 0
            goto L_0x0410
        L_0x041c:
            r1 = 1
            r0.first = r1
            r0.bounded_pitch = r1
            float[] r13 = r0.excBuf
            int r14 = r0.excIdx
            float[] r15 = r0.interp_qlpc
            float[] r1 = r0.frmBuf
            int r3 = r0.frmIdx
            int r4 = r0.frameSize
            float[] r5 = r0.mem_sp
            r16 = r1
            r17 = r3
            r18 = r4
            r19 = r2
            r20 = r5
            org.xiph.speex.Filters.iir_mem2(r13, r14, r15, r16, r17, r18, r19, r20)
            float[] r1 = r0.frmBuf
            int r2 = r0.frmIdx
            r1 = r1[r2]
            float r2 = r0.preemph
            float r3 = r0.pre_mem2
            float r2 = r2 * r3
            float r1 = r1 + r2
            r12[r11] = r1
            r1 = 1
        L_0x044b:
            int r2 = r0.frameSize
            if (r1 >= r2) goto L_0x0462
            float[] r2 = r0.frmBuf
            r0.frmIdx = r1
            r2 = r2[r1]
            float r3 = r0.preemph
            int r4 = r1 + -1
            r4 = r12[r4]
            float r3 = r3 * r4
            float r2 = r2 + r3
            r12[r1] = r2
            int r1 = r1 + 1
            goto L_0x044b
        L_0x0462:
            r1 = 1
            int r2 = r2 - r1
            r1 = r12[r2]
            r0.pre_mem2 = r1
            return r11
        L_0x0469:
            int r4 = r0.first
            if (r4 == 0) goto L_0x047d
            r4 = r11
        L_0x046e:
            int r5 = r0.lpcSize
            if (r4 >= r5) goto L_0x047d
            float[] r5 = r0.old_lsp
            float[] r6 = r0.lsp
            r6 = r6[r4]
            r5[r4] = r6
            int r4 = r4 + 1
            goto L_0x046e
        L_0x047d:
            org.xiph.speex.SubMode[] r4 = r0.submodes
            int r5 = r0.submodeID
            r4 = r4[r5]
            org.xiph.speex.LspQuant r4 = r4.lsqQuant
            float[] r5 = r0.lsp
            float[] r6 = r0.qlsp
            int r7 = r0.lpcSize
            r4.quant(r5, r6, r7, r15)
            org.xiph.speex.SubMode[] r4 = r0.submodes
            int r5 = r0.submodeID
            r4 = r4[r5]
            int r4 = r4.lbr_pitch
            r5 = -1
            if (r4 == r5) goto L_0x04a1
            int r4 = r0.min_pitch
            int r4 = r3 - r4
            r5 = 7
            r15.pack(r4, r5)
        L_0x04a1:
            org.xiph.speex.SubMode[] r4 = r0.submodes
            int r5 = r0.submodeID
            r4 = r4[r5]
            int r4 = r4.forced_pitch_gain
            r5 = 4602678819172646912(0x3fe0000000000000, double:0.5)
            if (r4 == 0) goto L_0x04c8
            r4 = 1097859072(0x41700000, float:15.0)
            float r2 = r2 * r4
            double r7 = (double) r2
            double r7 = r7 + r5
            double r7 = java.lang.Math.floor(r7)
            int r2 = (int) r7
            r9 = 15
            if (r2 <= r9) goto L_0x04bc
            r2 = r9
        L_0x04bc:
            if (r2 >= 0) goto L_0x04bf
            r2 = r11
        L_0x04bf:
            r15.pack(r2, r10)
            r4 = 1032358069(0x3d8888b5, float:0.066667)
            float r2 = (float) r2
            float r2 = r2 * r4
            goto L_0x04ca
        L_0x04c8:
            r9 = 15
        L_0x04ca:
            r24 = r2
            r7 = 4615063718147915776(0x400c000000000000, double:3.5)
            double r1 = (double) r1
            double r1 = java.lang.Math.log(r1)
            double r1 = r1 * r7
            double r1 = r1 + r5
            double r1 = java.lang.Math.floor(r1)
            int r1 = (int) r1
            if (r1 >= 0) goto L_0x04dd
            r1 = r11
        L_0x04dd:
            r2 = 31
            if (r1 <= r2) goto L_0x04e3
            r1 = 31
        L_0x04e3:
            double r4 = (double) r1
            r6 = 4615063718147915776(0x400c000000000000, double:3.5)
            double r4 = r4 / r6
            double r4 = java.lang.Math.exp(r4)
            float r4 = (float) r4
            r2 = 5
            r15.pack(r1, r2)
            int r1 = r0.first
            if (r1 == 0) goto L_0x0504
            r1 = r11
        L_0x04f5:
            int r2 = r0.lpcSize
            if (r1 >= r2) goto L_0x0504
            float[] r2 = r0.old_qlsp
            float[] r5 = r0.qlsp
            r5 = r5[r1]
            r2[r1] = r5
            int r1 = r1 + 1
            goto L_0x04f5
        L_0x0504:
            int r1 = r0.subframeSize
            float[] r2 = new float[r1]
            float[] r14 = new float[r1]
            float[] r13 = new float[r1]
            int r1 = r0.lpcSize
            float[] r8 = new float[r1]
            int r1 = r0.frameSize
            float[] r7 = new float[r1]
            r1 = r11
        L_0x0515:
            int r5 = r0.frameSize
            if (r1 >= r5) goto L_0x0525
            float[] r5 = r0.frmBuf
            int r6 = r0.frmIdx
            int r6 = r6 + r1
            r5 = r5[r6]
            r7[r1] = r5
            int r1 = r1 + 1
            goto L_0x0515
        L_0x0525:
            r6 = r11
        L_0x0526:
            int r1 = r0.nbSubframes
            if (r6 >= r1) goto L_0x09c8
            int r5 = r0.subframeSize
            int r5 = r5 * r6
            int r9 = r0.frmIdx
            int r34 = r9 + r5
            int r9 = r0.excIdx
            int r46 = r9 + r5
            int r9 = r0.swIdx
            int r47 = r9 + r5
            int r9 = r0.exc2Idx
            int r48 = r9 + r5
            double r10 = (double) r6
            double r10 = r10 + r21
            float r9 = (float) r10
            float r1 = (float) r1
            float r9 = r9 / r1
            r1 = 0
        L_0x0544:
            int r10 = r0.lpcSize
            if (r1 >= r10) goto L_0x0560
            float[] r10 = r0.interp_lsp
            float r11 = r20 - r9
            r49 = r4
            float[] r4 = r0.old_lsp
            r4 = r4[r1]
            float r11 = r11 * r4
            float[] r4 = r0.lsp
            r4 = r4[r1]
            float r4 = r4 * r9
            float r11 = r11 + r4
            r10[r1] = r11
            int r1 = r1 + 1
            r4 = r49
            goto L_0x0544
        L_0x0560:
            r49 = r4
            r1 = 0
        L_0x0563:
            int r4 = r0.lpcSize
            if (r1 >= r4) goto L_0x057b
            float[] r4 = r0.interp_qlsp
            float r10 = r20 - r9
            float[] r11 = r0.old_qlsp
            r11 = r11[r1]
            float r10 = r10 * r11
            float[] r11 = r0.qlsp
            r11 = r11[r1]
            float r11 = r11 * r9
            float r10 = r10 + r11
            r4[r1] = r10
            int r1 = r1 + 1
            goto L_0x0563
        L_0x057b:
            float[] r1 = r0.interp_lsp
            r9 = 990057071(0x3b03126f, float:0.002)
            org.xiph.speex.Lsp.enforce_margin(r1, r4, r9)
            float[] r1 = r0.interp_qlsp
            int r4 = r0.lpcSize
            org.xiph.speex.Lsp.enforce_margin(r1, r4, r9)
            r1 = 0
        L_0x058b:
            int r4 = r0.lpcSize
            if (r1 >= r4) goto L_0x059e
            float[] r4 = r0.interp_lsp
            r10 = r4[r1]
            double r10 = (double) r10
            double r10 = java.lang.Math.cos(r10)
            float r10 = (float) r10
            r4[r1] = r10
            int r1 = r1 + 1
            goto L_0x058b
        L_0x059e:
            org.xiph.speex.Lsp r1 = r0.m_lsp
            float[] r10 = r0.interp_lsp
            float[] r11 = r0.interp_lpc
            r1.lsp2lpc(r10, r11, r4)
            r1 = 0
        L_0x05a8:
            int r4 = r0.lpcSize
            if (r1 >= r4) goto L_0x05bb
            float[] r4 = r0.interp_qlsp
            r10 = r4[r1]
            double r10 = (double) r10
            double r10 = java.lang.Math.cos(r10)
            float r10 = (float) r10
            r4[r1] = r10
            int r1 = r1 + 1
            goto L_0x05a8
        L_0x05bb:
            org.xiph.speex.Lsp r1 = r0.m_lsp
            float[] r10 = r0.interp_qlsp
            float[] r11 = r0.interp_qlpc
            r1.lsp2lpc(r10, r11, r4)
            float[] r1 = r0.pi_gain
            r4 = 0
            r1[r6] = r4
            r4 = r20
            r1 = 0
        L_0x05cc:
            int r10 = r0.lpcSize
            if (r1 > r10) goto L_0x05e3
            float[] r10 = r0.pi_gain
            r11 = r10[r6]
            float[] r9 = r0.interp_qlpc
            r9 = r9[r1]
            float r9 = r9 * r4
            float r11 = r11 + r9
            r10[r6] = r11
            float r4 = -r4
            int r1 = r1 + 1
            r9 = 990057071(0x3b03126f, float:0.002)
            goto L_0x05cc
        L_0x05e3:
            float r1 = r0.gamma1
            float[] r4 = r0.interp_lpc
            float[] r9 = r0.bw_lpc1
            org.xiph.speex.Filters.bw_lpc(r1, r4, r9, r10)
            float r1 = r0.gamma2
            r4 = 0
            int r9 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r9 < 0) goto L_0x05ff
            float[] r4 = r0.interp_lpc
            float[] r9 = r0.bw_lpc2
            int r10 = r0.lpcSize
            org.xiph.speex.Filters.bw_lpc(r1, r4, r9, r10)
            r9 = 0
            r11 = 0
            goto L_0x0618
        L_0x05ff:
            float[] r1 = r0.bw_lpc2
            r11 = 0
            r1[r11] = r20
            float r4 = r0.preemph
            float r4 = -r4
            r9 = 1
            r1[r9] = r4
            r1 = 2
        L_0x060b:
            int r4 = r0.lpcSize
            if (r1 > r4) goto L_0x0617
            float[] r4 = r0.bw_lpc2
            r9 = 0
            r4[r1] = r9
            int r1 = r1 + 1
            goto L_0x060b
        L_0x0617:
            r9 = 0
        L_0x0618:
            r1 = r11
        L_0x0619:
            int r4 = r0.subframeSize
            if (r1 >= r4) goto L_0x0627
            float[] r4 = r0.excBuf
            int r10 = r46 + r1
            r4[r10] = r9
            int r1 = r1 + 1
            r9 = 0
            goto L_0x0619
        L_0x0627:
            float[] r1 = r0.excBuf
            r1[r46] = r20
            float[] r9 = r0.interp_qlpc
            float[] r10 = r0.bw_lpc1
            float[] r11 = r0.bw_lpc2
            r19 = r6
            int r6 = r0.lpcSize
            r25 = r1
            r26 = r46
            r27 = r9
            r28 = r10
            r29 = r11
            r30 = r13
            r31 = r4
            r32 = r6
            org.xiph.speex.Filters.syn_percep_zero(r25, r26, r27, r28, r29, r30, r31, r32)
            r1 = 0
        L_0x0649:
            int r4 = r0.subframeSize
            if (r1 >= r4) goto L_0x0657
            float[] r4 = r0.excBuf
            int r6 = r46 + r1
            r9 = 0
            r4[r6] = r9
            int r1 = r1 + 1
            goto L_0x0649
        L_0x0657:
            r9 = 0
            r1 = 0
        L_0x0659:
            int r4 = r0.subframeSize
            if (r1 >= r4) goto L_0x0667
            float[] r4 = r0.exc2Buf
            int r6 = r48 + r1
            r4[r6] = r9
            int r1 = r1 + 1
            r9 = 0
            goto L_0x0659
        L_0x0667:
            r1 = 0
        L_0x0668:
            int r4 = r0.lpcSize
            if (r1 >= r4) goto L_0x0675
            float[] r4 = r0.mem_sp
            r4 = r4[r1]
            r8[r1] = r4
            int r1 = r1 + 1
            goto L_0x0668
        L_0x0675:
            float[] r1 = r0.excBuf
            float[] r6 = r0.interp_qlpc
            int r9 = r0.subframeSize
            r25 = r1
            r26 = r46
            r27 = r6
            r28 = r1
            r29 = r46
            r30 = r9
            r31 = r4
            r32 = r8
            org.xiph.speex.Filters.iir_mem2(r25, r26, r27, r28, r29, r30, r31, r32)
            r1 = 0
        L_0x068f:
            int r4 = r0.lpcSize
            if (r1 >= r4) goto L_0x069c
            float[] r4 = r0.mem_sw
            r4 = r4[r1]
            r8[r1] = r4
            int r1 = r1 + 1
            goto L_0x068f
        L_0x069c:
            float[] r1 = r0.excBuf
            float[] r6 = r0.bw_lpc1
            float[] r9 = r0.bw_lpc2
            r41 = 0
            int r10 = r0.subframeSize
            r45 = 0
            r36 = r1
            r37 = r46
            r38 = r6
            r39 = r9
            r40 = r2
            r42 = r10
            r43 = r4
            r44 = r8
            org.xiph.speex.Filters.filter_mem2(r36, r37, r38, r39, r40, r41, r42, r43, r44, r45)
            r1 = 0
        L_0x06bc:
            int r4 = r0.lpcSize
            if (r1 >= r4) goto L_0x06c9
            float[] r4 = r0.mem_sw
            r4 = r4[r1]
            r8[r1] = r4
            int r1 = r1 + 1
            goto L_0x06bc
        L_0x06c9:
            float[] r1 = r0.frmBuf
            float[] r6 = r0.bw_lpc1
            float[] r9 = r0.bw_lpc2
            float[] r10 = r0.swBuf
            int r11 = r0.subframeSize
            r45 = 0
            r36 = r1
            r37 = r34
            r38 = r6
            r39 = r9
            r40 = r10
            r41 = r47
            r42 = r11
            r43 = r4
            r44 = r8
            org.xiph.speex.Filters.filter_mem2(r36, r37, r38, r39, r40, r41, r42, r43, r44, r45)
            r1 = 0
        L_0x06eb:
            int r4 = r0.subframeSize
            if (r1 >= r4) goto L_0x06fd
            float[] r4 = r0.swBuf
            int r6 = r47 + r1
            r4 = r4[r6]
            r6 = r2[r1]
            float r4 = r4 - r6
            r14[r1] = r4
            int r1 = r1 + 1
            goto L_0x06eb
        L_0x06fd:
            r1 = 0
        L_0x06fe:
            int r4 = r0.subframeSize
            if (r1 >= r4) goto L_0x0712
            float[] r4 = r0.excBuf
            int r6 = r46 + r1
            float[] r9 = r0.exc2Buf
            int r10 = r48 + r1
            r11 = 0
            r9[r10] = r11
            r4[r6] = r11
            int r1 = r1 + 1
            goto L_0x06fe
        L_0x0712:
            r11 = 0
            org.xiph.speex.SubMode[] r1 = r0.submodes
            int r4 = r0.submodeID
            r6 = r1[r4]
            int r6 = r6.lbr_pitch
            r9 = -1
            if (r6 == r9) goto L_0x0749
            r1 = r1[r4]
            int r1 = r1.lbr_pitch
            if (r1 == 0) goto L_0x0741
            int r4 = r0.min_pitch
            int r6 = r4 + r1
            r10 = 1
            int r6 = r6 - r10
            if (r3 >= r6) goto L_0x072f
            int r4 = r4 + r1
            int r3 = r4 + -1
        L_0x072f:
            int r4 = r0.max_pitch
            int r6 = r4 - r1
            if (r3 <= r6) goto L_0x0737
            int r4 = r4 - r1
            r3 = r4
        L_0x0737:
            int r4 = r3 - r1
            int r4 = r4 + r10
            int r1 = r1 + r3
            r58 = r4
            r4 = r3
            r3 = r58
            goto L_0x0744
        L_0x0741:
            r10 = 1
            r1 = r3
            r4 = r1
        L_0x0744:
            r18 = r3
            r33 = r4
            goto L_0x0753
        L_0x0749:
            r10 = 1
            int r1 = r0.min_pitch
            int r4 = r0.max_pitch
            r18 = r1
            r33 = r3
            r1 = r4
        L_0x0753:
            int r3 = r0.bounded_pitch
            if (r3 == 0) goto L_0x075c
            if (r1 <= r5) goto L_0x075c
            r25 = r5
            goto L_0x075e
        L_0x075c:
            r25 = r1
        L_0x075e:
            org.xiph.speex.SubMode[] r1 = r0.submodes
            int r3 = r0.submodeID
            r1 = r1[r3]
            org.xiph.speex.Ltp r1 = r1.ltp
            float[] r3 = r0.swBuf
            float[] r5 = r0.interp_qlpc
            float[] r6 = r0.bw_lpc1
            r35 = r19
            float[] r4 = r0.bw_lpc2
            r50 = r7
            r7 = r4
            float[] r4 = r0.excBuf
            r51 = r8
            r8 = r4
            int r4 = r0.lpcSize
            r53 = r9
            r52 = r13
            r13 = r4
            int r4 = r0.subframeSize
            r54 = r14
            r55 = 990057071(0x3b03126f, float:0.002)
            r14 = r4
            float[] r4 = r0.exc2Buf
            r16 = r4
            int r4 = r0.complexity
            r19 = r4
            r56 = r2
            r57 = r11
            r2 = r54
            r4 = r47
            r11 = 8
            r9 = r46
            r10 = r18
            r11 = r25
            r12 = r24
            r15 = r60
            r17 = r48
            r18 = r52
            int r1 = r1.quant(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19)
            int[] r2 = r0.pitch
            r2[r35] = r1
            float[] r1 = r0.excBuf
            float[] r2 = r0.interp_qlpc
            float[] r3 = r0.bw_lpc1
            float[] r4 = r0.bw_lpc2
            int r5 = r0.subframeSize
            int r6 = r0.lpcSize
            r25 = r1
            r26 = r46
            r27 = r2
            r28 = r3
            r29 = r4
            r30 = r56
            r31 = r5
            r32 = r6
            org.xiph.speex.Filters.syn_percep_zero(r25, r26, r27, r28, r29, r30, r31, r32)
            r1 = 0
        L_0x07cf:
            int r2 = r0.subframeSize
            if (r1 >= r2) goto L_0x07dd
            r2 = r54[r1]
            r3 = r56[r1]
            float r2 = r2 - r3
            r54[r1] = r2
            int r1 = r1 + 1
            goto L_0x07cf
        L_0x07dd:
            int r13 = r35 * r2
            r1 = 0
        L_0x07e0:
            int r2 = r0.subframeSize
            if (r1 >= r2) goto L_0x07ed
            float[] r2 = r0.innov
            int r3 = r13 + r1
            r2[r3] = r57
            int r1 = r1 + 1
            goto L_0x07e0
        L_0x07ed:
            r26 = 0
            float[] r1 = r0.interp_qlpc
            float[] r3 = r0.bw_lpc1
            float[] r4 = r0.bw_lpc2
            float[] r5 = r0.buf2
            int r6 = r0.lpcSize
            r25 = r54
            r27 = r1
            r28 = r3
            r29 = r4
            r30 = r5
            r31 = r2
            r32 = r6
            org.xiph.speex.Filters.residue_percep_zero(r25, r26, r27, r28, r29, r30, r31, r32)
            r2 = r57
            r1 = 0
        L_0x080d:
            int r3 = r0.subframeSize
            if (r1 >= r3) goto L_0x081c
            float[] r3 = r0.buf2
            r4 = r3[r1]
            r3 = r3[r1]
            float r4 = r4 * r3
            float r2 = r2 + r4
            int r1 = r1 + 1
            goto L_0x080d
        L_0x081c:
            r1 = 1036831949(0x3dcccccd, float:0.1)
            float r3 = (float) r3
            float r2 = r2 / r3
            float r2 = r2 + r1
            double r1 = (double) r2
            double r1 = java.lang.Math.sqrt(r1)
            float r1 = (float) r1
            float r1 = r1 / r49
            org.xiph.speex.SubMode[] r2 = r0.submodes
            int r3 = r0.submodeID
            r2 = r2[r3]
            int r2 = r2.have_subframe_gain
            if (r2 == 0) goto L_0x0873
            double r1 = (double) r1
            double r1 = java.lang.Math.log(r1)
            float r1 = (float) r1
            org.xiph.speex.SubMode[] r2 = r0.submodes
            int r3 = r0.submodeID
            r2 = r2[r3]
            int r2 = r2.have_subframe_gain
            r3 = 3
            if (r2 != r3) goto L_0x0859
            float[] r2 = org.xiph.speex.NbCodec.exc_gain_quant_scal3
            r14 = 8
            int r1 = org.xiph.speex.C0911VQ.index(r1, r2, r14)
            r2 = 3
            r15 = r60
            r15.pack(r1, r2)
            float[] r2 = org.xiph.speex.NbCodec.exc_gain_quant_scal3
            r1 = r2[r1]
            r12 = 1
            goto L_0x086c
        L_0x0859:
            r15 = r60
            r14 = 8
            float[] r2 = org.xiph.speex.NbCodec.exc_gain_quant_scal1
            r3 = 2
            int r1 = org.xiph.speex.C0911VQ.index(r1, r2, r3)
            r12 = 1
            r15.pack(r1, r12)
            float[] r2 = org.xiph.speex.NbCodec.exc_gain_quant_scal1
            r1 = r2[r1]
        L_0x086c:
            double r1 = (double) r1
            double r1 = java.lang.Math.exp(r1)
            float r1 = (float) r1
            goto L_0x087a
        L_0x0873:
            r15 = r60
            r12 = 1
            r14 = 8
            r1 = r20
        L_0x087a:
            float r11 = r1 * r49
            float r1 = r20 / r11
            r2 = 0
        L_0x087f:
            int r7 = r0.subframeSize
            if (r2 >= r7) goto L_0x088b
            r3 = r54[r2]
            float r3 = r3 * r1
            r54[r2] = r3
            int r2 = r2 + 1
            goto L_0x087f
        L_0x088b:
            org.xiph.speex.SubMode[] r1 = r0.submodes
            int r2 = r0.submodeID
            r1 = r1[r2]
            org.xiph.speex.CbSearch r1 = r1.innovation
            float[] r3 = r0.interp_qlpc
            float[] r4 = r0.bw_lpc1
            float[] r5 = r0.bw_lpc2
            int r6 = r0.lpcSize
            float[] r8 = r0.innov
            int r10 = r0.complexity
            r2 = r54
            r9 = r13
            r16 = r10
            r10 = r52
            r14 = r11
            r11 = r60
            r15 = r12
            r12 = r16
            r1.quant(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            r1 = 0
        L_0x08b0:
            int r2 = r0.subframeSize
            if (r1 >= r2) goto L_0x08c0
            float[] r2 = r0.innov
            int r3 = r13 + r1
            r4 = r2[r3]
            float r4 = r4 * r14
            r2[r3] = r4
            int r1 = r1 + 1
            goto L_0x08b0
        L_0x08c0:
            r1 = 0
        L_0x08c1:
            int r2 = r0.subframeSize
            if (r1 >= r2) goto L_0x08d7
            float[] r2 = r0.excBuf
            int r3 = r46 + r1
            r4 = r2[r3]
            float[] r5 = r0.innov
            int r6 = r13 + r1
            r5 = r5[r6]
            float r4 = r4 + r5
            r2[r3] = r4
            int r1 = r1 + 1
            goto L_0x08c1
        L_0x08d7:
            org.xiph.speex.SubMode[] r1 = r0.submodes
            int r3 = r0.submodeID
            r1 = r1[r3]
            int r1 = r1.double_codebook
            if (r1 == 0) goto L_0x093d
            float[] r13 = new float[r2]
            r1 = 0
        L_0x08e4:
            int r7 = r0.subframeSize
            if (r1 >= r7) goto L_0x08f7
            r2 = r54[r1]
            double r2 = (double) r2
            r4 = 4612136378390124954(0x400199999999999a, double:2.2)
            double r2 = r2 * r4
            float r2 = (float) r2
            r54[r1] = r2
            int r1 = r1 + 1
            goto L_0x08e4
        L_0x08f7:
            org.xiph.speex.SubMode[] r1 = r0.submodes
            int r2 = r0.submodeID
            r1 = r1[r2]
            org.xiph.speex.CbSearch r1 = r1.innovation
            float[] r3 = r0.interp_qlpc
            float[] r4 = r0.bw_lpc1
            float[] r5 = r0.bw_lpc2
            int r6 = r0.lpcSize
            r9 = 0
            int r12 = r0.complexity
            r2 = r54
            r8 = r13
            r10 = r52
            r11 = r60
            r1.quant(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            r1 = 0
        L_0x0915:
            int r2 = r0.subframeSize
            if (r1 >= r2) goto L_0x092a
            r2 = r13[r1]
            double r2 = (double) r2
            double r4 = (double) r14
            r6 = 4601859982876761367(0x3fdd1745d1745d17, double:0.45454545454545453)
            double r4 = r4 * r6
            double r2 = r2 * r4
            float r2 = (float) r2
            r13[r1] = r2
            int r1 = r1 + 1
            goto L_0x0915
        L_0x092a:
            r1 = 0
        L_0x092b:
            int r2 = r0.subframeSize
            if (r1 >= r2) goto L_0x093d
            float[] r2 = r0.excBuf
            int r3 = r46 + r1
            r4 = r2[r3]
            r5 = r13[r1]
            float r4 = r4 + r5
            r2[r3] = r4
            int r1 = r1 + 1
            goto L_0x092b
        L_0x093d:
            r1 = 0
        L_0x093e:
            int r2 = r0.subframeSize
            if (r1 >= r2) goto L_0x094a
            r2 = r54[r1]
            float r2 = r2 * r14
            r54[r1] = r2
            int r1 = r1 + 1
            goto L_0x093e
        L_0x094a:
            r1 = 0
        L_0x094b:
            int r2 = r0.lpcSize
            if (r1 >= r2) goto L_0x0958
            float[] r2 = r0.mem_sp
            r2 = r2[r1]
            r51[r1] = r2
            int r1 = r1 + 1
            goto L_0x094b
        L_0x0958:
            float[] r1 = r0.excBuf
            float[] r3 = r0.interp_qlpc
            float[] r4 = r0.frmBuf
            int r5 = r0.subframeSize
            float[] r6 = r0.mem_sp
            r25 = r1
            r26 = r46
            r27 = r3
            r28 = r4
            r29 = r34
            r30 = r5
            r31 = r2
            r32 = r6
            org.xiph.speex.Filters.iir_mem2(r25, r26, r27, r28, r29, r30, r31, r32)
            float[] r1 = r0.frmBuf
            float[] r2 = r0.bw_lpc1
            float[] r3 = r0.bw_lpc2
            float[] r4 = r0.swBuf
            int r5 = r0.subframeSize
            int r6 = r0.lpcSize
            float[] r7 = r0.mem_sw
            r45 = 0
            r36 = r1
            r37 = r34
            r38 = r2
            r39 = r3
            r40 = r4
            r41 = r47
            r42 = r5
            r43 = r6
            r44 = r7
            org.xiph.speex.Filters.filter_mem2(r36, r37, r38, r39, r40, r41, r42, r43, r44, r45)
            r1 = 0
        L_0x099b:
            int r2 = r0.subframeSize
            if (r1 >= r2) goto L_0x09ae
            float[] r2 = r0.exc2Buf
            int r3 = r48 + r1
            float[] r4 = r0.excBuf
            int r5 = r46 + r1
            r4 = r4[r5]
            r2[r3] = r4
            int r1 = r1 + 1
            goto L_0x099b
        L_0x09ae:
            int r6 = r35 + 1
            r15 = r60
            r12 = r61
            r3 = r33
            r4 = r49
            r7 = r50
            r8 = r51
            r13 = r52
            r14 = r54
            r2 = r56
            r9 = 15
            r10 = 4
            r11 = 0
            goto L_0x0526
        L_0x09c8:
            r50 = r7
            r15 = 1
            r57 = 0
            int r1 = r0.submodeID
            if (r1 < r15) goto L_0x09f1
            r1 = 0
        L_0x09d2:
            int r2 = r0.lpcSize
            if (r1 >= r2) goto L_0x09e1
            float[] r2 = r0.old_lsp
            float[] r3 = r0.lsp
            r3 = r3[r1]
            r2[r1] = r3
            int r1 = r1 + 1
            goto L_0x09d2
        L_0x09e1:
            r1 = 0
        L_0x09e2:
            int r2 = r0.lpcSize
            if (r1 >= r2) goto L_0x09f1
            float[] r2 = r0.old_qlsp
            float[] r3 = r0.qlsp
            r3 = r3[r1]
            r2[r1] = r3
            int r1 = r1 + 1
            goto L_0x09e2
        L_0x09f1:
            int r1 = r0.submodeID
            if (r1 != r15) goto L_0x0a0a
            int r1 = r0.dtx_count
            if (r1 == 0) goto L_0x0a02
            r1 = r60
            r2 = 4
            r3 = 15
            r1.pack(r3, r2)
            goto L_0x0a0a
        L_0x0a02:
            r1 = r60
            r2 = 4
            r3 = 0
            r1.pack(r3, r2)
            goto L_0x0a0b
        L_0x0a0a:
            r3 = 0
        L_0x0a0b:
            r0.first = r3
            r1 = r3
            r2 = r57
        L_0x0a10:
            int r4 = r0.frameSize
            if (r1 >= r4) goto L_0x0a35
            float[] r4 = r0.frmBuf
            int r5 = r0.frmIdx
            int r6 = r5 + r1
            r6 = r4[r6]
            int r7 = r5 + r1
            r7 = r4[r7]
            float r6 = r6 * r7
            float r57 = r57 + r6
            int r6 = r5 + r1
            r6 = r4[r6]
            r7 = r50[r1]
            float r6 = r6 - r7
            int r5 = r5 + r1
            r4 = r4[r5]
            r5 = r50[r1]
            float r4 = r4 - r5
            float r6 = r6 * r4
            float r2 = r2 + r6
            int r1 = r1 + 1
            goto L_0x0a10
        L_0x0a35:
            float r57 = r57 + r20
            float r2 = r2 + r20
            float r1 = r57 / r2
            double r1 = (double) r1
            java.lang.Math.log(r1)
            float[] r1 = r0.frmBuf
            int r2 = r0.frmIdx
            r1 = r1[r2]
            float r2 = r0.preemph
            float r4 = r0.pre_mem2
            float r2 = r2 * r4
            float r1 = r1 + r2
            r61[r3] = r1
            r1 = r15
        L_0x0a4e:
            int r2 = r0.frameSize
            if (r1 >= r2) goto L_0x0a66
            float[] r2 = r0.frmBuf
            int r4 = r0.frmIdx
            int r4 = r4 + r1
            r2 = r2[r4]
            float r4 = r0.preemph
            int r5 = r1 + -1
            r5 = r61[r5]
            float r4 = r4 * r5
            float r2 = r2 + r4
            r61[r1] = r2
            int r1 = r1 + 1
            goto L_0x0a4e
        L_0x0a66:
            int r2 = r2 - r15
            r1 = r61[r2]
            r0.pre_mem2 = r1
            org.xiph.speex.SubMode[] r1 = r0.submodes
            int r2 = r0.submodeID
            r1 = r1[r2]
            org.xiph.speex.CbSearch r1 = r1.innovation
            boolean r1 = r1 instanceof org.xiph.speex.NoiseSearch
            if (r1 != 0) goto L_0x0a7d
            if (r2 != 0) goto L_0x0a7a
            goto L_0x0a7d
        L_0x0a7a:
            r0.bounded_pitch = r3
            goto L_0x0a7f
        L_0x0a7d:
            r0.bounded_pitch = r15
        L_0x0a7f:
            return r15
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xiph.speex.NbEncoder.encode(org.xiph.speex.Bits, float[]):int");
    }

    public int getBitRate() {
        SubMode[] subModeArr = this.submodes;
        int i = this.submodeID;
        return subModeArr[i] != null ? (this.sampling_rate * subModeArr[i].bits_per_frame) / this.frameSize : (this.sampling_rate * 5) / this.frameSize;
    }

    public int getMode() {
        return this.submodeID;
    }

    public float getRelativeQuality() {
        return this.relative_quality;
    }

    public void init(int i, int i2, int i3, int i4) {
        super.init(i, i2, i3, i4);
        this.complexity = 3;
        this.vbr_enabled = 0;
        this.vad_enabled = 0;
        this.abr_enabled = 0;
        this.vbr_quality = 8.0f;
        this.submodeSelect = 5;
        this.pre_mem2 = 0.0f;
        this.bounded_pitch = 1;
        this.exc2Buf = new float[i4];
        int i5 = this.windowSize;
        this.exc2Idx = i4 - i5;
        this.swBuf = new float[i4];
        this.swIdx = i4 - i5;
        this.window = Misc.window(i5, i2);
        this.lagWindow = Misc.lagWindow(i3, this.lag_factor);
        int i6 = i3 + 1;
        this.autocorr = new float[i6];
        this.buf2 = new float[this.windowSize];
        this.interp_lpc = new float[i6];
        this.interp_qlpc = new float[i6];
        this.bw_lpc1 = new float[i6];
        this.bw_lpc2 = new float[i6];
        this.lsp = new float[i3];
        this.qlsp = new float[i3];
        this.old_lsp = new float[i3];
        this.old_qlsp = new float[i3];
        this.interp_lsp = new float[i3];
        this.interp_qlsp = new float[i3];
        this.f117rc = new float[i3];
        this.mem_sp = new float[i3];
        this.mem_sw = new float[i3];
        this.mem_sw_whole = new float[i3];
        this.mem_exc = new float[i3];
        this.vbr = new Vbr();
        this.dtx_count = 0;
        this.abr_count = 0.0f;
        this.sampling_rate = 8000;
        this.awk1 = new float[i6];
        this.awk2 = new float[i6];
        this.awk3 = new float[i6];
        this.innov2 = new float[40];
        this.filters.init();
        this.pitch = new int[this.nbSubframes];
    }

    public void setMode(int i) {
        if (i < 0) {
            i = 0;
        }
        this.submodeSelect = i;
        this.submodeID = i;
    }

    public void setQuality(int i) {
        if (i < 0) {
            i = 0;
        }
        if (i > 10) {
            i = 10;
        }
        int i2 = NB_QUALITY_MAP[i];
        this.submodeSelect = i2;
        this.submodeID = i2;
    }

    public void setVbr(boolean z) {
        this.vbr_enabled = z ? 1 : 0;
    }

    public void setVbrQuality(float f) {
        if (f < 0.0f) {
            f = 0.0f;
        }
        if (f > 10.0f) {
            f = 10.0f;
        }
        this.vbr_quality = f;
    }
}
