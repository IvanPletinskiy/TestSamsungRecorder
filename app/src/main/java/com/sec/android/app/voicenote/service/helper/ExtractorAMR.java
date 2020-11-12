package com.sec.android.app.voicenote.service.helper;

import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.core.view.PointerIconCompat;

public class ExtractorAMR extends ExtractorSound {
    private static int[] BLOCK_SIZES = {12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0};
    private static int[] GAIN_FAC_MR475 = {812, 128, 542, 140, 2873, 1135, 2266, 3402, 2067, 563, 12677, 647, 4132, 1798, 5601, 5285, 7689, 374, 3735, 441, 10912, 2638, 11807, 2494, 20490, 797, 5218, 675, 6724, 8354, 5282, 1696, 1488, 428, 5882, 452, 5332, 4072, 3583, 1268, 2469, 901, 15894, Event.RECORD_STOP_DELAYED, 14982, 3271, 10331, 4858, 3635, 2021, 2596, 835, 12360, 4892, 12206, 1704, 13432, 1604, 9118, 2341, 3968, 1538, 5479, 9936, 3795, 417, 1359, 414, 3640, 1569, 7995, 3541, 11405, 645, 8552, 635, 4056, 1377, 16608, 6124, 11420, 700, Event.PLAY_FF, 607, 12415, 1578, 11119, 4654, 13680, 1708, 11990, 1229, 7996, 7297, 13231, 5715, 2428, 1159, 2073, 1941, 6218, 6121, 3546, 1804, 8925, 1802, 8679, 1580, 13935, 3576, 13313, 6237, 6142, 1130, 5994, 1734, 14141, 4662, 11271, 3321, 12226, 1551, 13931, 3015, 5081, 10464, 9444, 6706, 1689, 683, 1436, 1306, 7212, 3933, 4082, 2713, 7793, 704, 15070, 802, 6299, 5212, 4337, 5357, 6676, 541, 6062, 626, 13651, 3700, 11498, 2408, 16156, 716, 12177, 751, 8065, 11489, 6314, 2256, 4466, 496, 7293, 523, 10213, 3833, 8394, 3037, 8403, Event.MOUNT_SD_CARD, 14228, 1880, 8703, 5409, 16395, 4863, 7420, 1979, 6089, 1230, 9371, 4398, 14558, 3363, 13559, 2873, 13163, 1465, 5534, 1678, 13138, 14771, 7338, 600, 1318, 548, 4252, 3539, 10044, 2364, 10587, 622, 13088, 669, 14126, 3526, 5039, 9784, 15338, 619, 3115, 590, 16442, 3013, 15542, 4168, 15537, 1611, 15405, 1228, 16023, 9299, 7534, 4976, Event.RECORD_STOP_BY_DEX_CONNECT, 1213, 11447, 1157, 12512, 5519, 9475, 2644, 7716, 2034, 13280, 2239, 16011, 5093, 8066, 6761, 10083, 1413, Event.EDIT_PLAY_PAUSE, 2347, 12523, 5975, 15126, 2899, 18264, 2289, 15827, 2527, 16265, 10254, 14651, 11319, 1797, 337, 3115, 397, 3510, 2928, 4592, 2670, 7519, 628, 11415, 656, 5946, 2435, 6544, 7367, 8238, 829, 4000, 863, 10032, 2492, 16057, 3551, 18204, 1054, 6103, 1454, 5884, 7900, 18752, 3468, 1864, 544, 9198, 683, 11623, 4160, 4594, 1644, 3158, 1157, 15953, 2560, 12349, 3733, 17420, 5260, 6106, Event.PLAY_STOP, 2917, 1742, 16467, 5257, 16787, 1680, 17205, 1759, 4773, 3231, 7386, 6035, 14342, 10012, 4035, 442, 4194, 458, 9214, 2242, 7427, 4217, 12860, 801, 11186, 825, 12648, 2084, 12956, 6554, 9505, Event.ADD_BOOKMARK, 6629, Event.SELECT_ALL, 10537, 2502, 15289, Event.EDIT_TRIM, 12602, 2055, 15484, 1653, 16194, 6921, 14231, 5790, 2626, 828, 5615, 1686, 13663, 5778, 3668, 1554, 11313, 2633, 9770, 1459, 14003, 4733, 15897, 6291, 6278, 1870, 7910, 2285, 16978, 4571, 16576, 3849, 15248, 2311, 16023, 3244, 14459, 17808, 11847, 2763, 1981, 1407, 1400, 876, 4335, 3547, 4391, 4210, 5405, 680, 17461, 781, 6501, 5118, 8091, 7677, 7355, 794, 8333, 1182, 15041, 3160, 14928, 3039, 20421, 880, 14545, 852, 12337, 14708, 6904, 1920, 4225, 933, 8218, 1087, 10659, 4084, 10082, 4533, 2735, 840, 20657, 1081, 16711, 5966, 15873, 4578, 10871, 2574, 3773, 1166, 14519, 4044, 20699, 2627, 15219, 2734, 15274, 2186, 6257, 3226, 13125, 19480, 7196, 930, 2462, 1618, 4515, 3092, 13852, 4277, 10460, 833, 17339, 810, 16891, 2289, 15546, 8217, 13603, 1684, 3197, 1834, 15948, 2820, 15812, 5327, 17006, 2438, 16788, 1326, 15671, 8156, 11726, 8556, 3762, 2053, 9563, 1317, 13561, 6790, 12227, 1936, 8180, 3550, 13287, 1778, 16299, 6599, 16291, 7758, 8521, 2551, 7225, 2645, 18269, 7489, 16885, 2248, 17882, 2884, 17265, 3328, 9417, 20162, 11042, 8320, 1286, 620, 1431, 583, 5993, 2289, 3978, 3626, 5144, 752, 13409, 830, 5553, 2860, 11764, 5908, 10737, 560, 5446, 564, 13321, Event.TRASH_MINI_PLAY_RESUME, 11946, 3683, 19887, 798, 9825, 728, 13663, 8748, 7391, 3053, 2515, 778, 6050, 833, 6469, 5074, 8305, 2463, 6141, 1865, 15308, 1262, 14408, 4547, 13663, 4515, 3137, 2983, 2479, 1259, 15088, 4647, 15382, 2607, 14492, 2392, 12462, 2537, 7539, 2949, 12909, 12060, 5468, 684, 3141, 722, 5081, 1274, 12732, 4200, 15302, 681, 7819, 592, 6534, 2021, 16478, 8737, 13364, 882, 5397, 899, 14656, 2178, 14741, 4227, 14270, 1298, 13929, 2029, 15477, 7482, 15815, 4572, 2521, 2013, 5062, 1804, 5159, 6582, 7130, 3597, 10920, 1611, 11729, 1708, 16903, 3455, 16268, 6640, 9306, 1007, 9369, 2106, 19182, 5037, 12441, 4269, 15919, 1332, 15357, 3512, 11898, 14141, 16101, 6854, 2010, 737, 3779, 861, 11454, 2880, 3564, 3540, 9057, 1241, 12391, 896, 8546, 4629, 11561, 5776, 8129, 589, 8218, 588, 18728, 3755, 12973, 3149, 15729, 758, 16634, 754, 15222, 11138, 15871, 2208, 4673, 610, 10218, 678, 15257, 4146, 5729, 3327, 8377, 1670, 19862, 2321, 15450, 5511, 14054, 5481, 5728, 2888, 7580, 1346, 14384, 5325, 16236, 3950, 15118, 3744, 15306, 1435, 14597, 4070, 12301, 15696, 7617, 1699, 2170, 884, 4459, 4567, 18094, 3306, 12742, 815, 14926, 907, 15016, 4281, 15518, 8368, 17994, 1087, 2358, 865, 16281, 3787, 15679, 4596, 16356, 1534, 16584, 2210, 16833, 9697, 15929, 4513, 3277, 1085, 9643, 2187, 11973, 6068, 9199, 4462, 8955, 1629, 10289, 3062, 16481, 5155, 15466, 7066, 13678, 2543, 5273, 2277, 16746, 6213, 16655, 3408, 20304, 3363, 18688, 1985, 14172, 12867, 15154, 15703, 4473, 1020, 1681, 886, 4311, 4301, 8952, 3657, 5893, 1147, 11647, 1452, 15886, 2227, 4582, 6644, 6929, 1205, 6220, 799, 12415, 3409, 15968, 3877, 19859, 2109, 9689, 2141, 14742, 8830, 14480, 2599, 1817, 1238, 7771, 813, 19079, 4410, 5554, 2064, 3687, 2844, 17435, 2256, 16697, 4486, 16199, 5388, 8028, 2763, 3405, 2119, 17426, 5477, 13698, 2786, 19879, 2720, 9098, 3880, 18172, 4833, 17336, 12207, 5116, Event.ADD_BOOKMARK, 4935, Event.PRIVATE_OPERATION_OPTION_CHANGED, 9888, 3081, 6014, 5371, 15881, 1667, 8405, 1183, 15087, 2366, 19777, Event.TRANSLATION_PAUSE, 11963, 1562, 7279, 1128, 16859, 1532, 15762, 5381, 14708, 2065, 20105, 2155, 17158, 8245, 17911, 6318, 5467, 1504, 4100, 2574, 17421, 6810, 5673, 2888, 16636, 3382, 8975, 1831, 20159, 4737, 19550, 7294, 6658, 2781, 11472, 3321, 19397, 5054, 18878, 4722, 16439, 2373, 20430, 4386, 11353, 26526, 11593, 3068, 2866, 1566, 5108, 1070, 9614, 4915, 4939, 3536, 7541, 878, 20717, 851, 6938, 4395, 16799, 7733, 10137, PointerIconCompat.TYPE_ZOOM_OUT, 9845, Event.DELETE_CATEGORY, 15494, 3955, 15459, 3430, 18863, Event.SIMPLE_MODE_CANCEL, 20120, Event.CHANGE_SORT_MODE, 16876, 12887, 14334, 4200, 6599, 1220, 9222, 814, 16942, 5134, 5661, 4898, 5488, 1798, 20258, 3962, 17005, 6178, 17929, 5929, 9365, 3420, 7474, 1971, 19537, 5177, 19003, Event.TRASH_MINI_PLAY_START, 16454, 3788, 16070, 2367, 8664, 2743, 9445, 26358, 10856, 1287, 3555, 1009, 5606, 3622, 19453, 5512, 12453, 797, 20634, 911, 15427, 3066, 17037, 10275, 18883, 2633, 3913, 1268, 19519, 3371, 18052, 5230, 19291, 1678, 19508, 3172, 18072, 10754, 16625, 6845, 3134, 2298, 10869, 2437, 15580, 6913, 12597, 3381, 11116, 3297, 16762, 2424, 18853, 6715, 17171, 9887, 12743, 2605, 8937, 3140, 19033, 7764, 18347, 3880, 20475, 3682, 19602, 3380, 13044, 19373, 10526, 23124};
    private static int[] GAIN_FAC_MR515 = {28753, 2785, 6594, 7413, 10444, 1269, 4423, 1556, 12820, 2498, 4833, 2498, 7864, 1884, 3153, 1802, 20193, 3031, 5857, 4014, 8970, 1392, 4096, 655, 13926, 3112, 4669, 2703, 6553, 901, 2662, 655, 23511, 2457, 5079, 4096, 8560, 737, 4259, 2088, 12288, 1474, 4628, 1433, Event.TRANSLATION_SAVE, 737, 2252, 1228, 17326, 2334, 5816, 3686, 8601, 778, 3809, 614, 9256, 1761, 3522, 1966, 5529, 737, 3194, 778};
    private static int[] GRAY = {0, 1, 3, 2, 5, 6, 4, 7};
    private static int[] QUA_ENER_MR515 = {17333, -3431, 4235, 5276, 8325, -10422, 683, -8609, 10148, -4398, 1472, -4398, 5802, -6907, -2327, -7303, 14189, -2678, 3181, -180, 6972, -9599, 0, -16305, 10884, -2444, 1165, -3697, 4180, -13468, -3833, -16305, 15543, -4546, 1913, 0, 6556, -15255, 347, -5993, 9771, -9090, 1086, -9341, 4772, -15255, -5321, -10714, 12827, -5002, 3118, -938, 6598, -14774, -646, -16879, 7251, -7508, -1343, -6529, 2668, -15255, -2212, -2454, -14774};
    private static int[] QUA_GAIN_CODE = {159, -3776, -22731, 206, -3394, -20428, 268, -3005, -18088, 349, -2615, -15739, 419, -2345, -14113, 482, -2138, -12867, 554, -1932, -11629, 637, -1726, -10387, 733, -1518, -9139, 842, -1314, -7906, Event.UNBLOCK_CONTROL_BUTTONS, -1106, -6656, 1114, -900, -5416, 1281, -694, -4173, 1473, -487, -2931, 1694, -281, -1688, 1948, -75, -445, 2241, 133, 801, 2577, 339, 2044, 2963, 545, 3285, 3408, 752, 4530, 3919, Event.TOS_ACCEPTED, 5772, 4507, 1165, 7016, 5183, 1371, 8259, 5960, 1577, 9501, 6855, 1784, 10745, 7883, Event.RECORD_BY_LEVEL_ACTIVEKEY, 11988, 9065, 2197, 13231, 10425, 2404, 14474, 12510, 2673, 16096, 16263, 3060, 18429, 21142, 3448, 20763, 27485, 3836, 23097};
    private static int[] QUA_GAIN_PITCH = {0, 3277, 6556, 8192, 9830, 11469, 12288, 13107, 13926, 14746, 15565, 16384, 17203, 18022, 18842, 19661};
    private int[] mFrameGains;
    private int[] mFrameLens;
    private int[] mFrameOffsets;
    private int mMaxFrames;
    private int mMaxGain;
    private int mMinGain;
    private int mNumFrames;
    private int mOffset;

    public int getSampleRate() {
        return 8000;
    }

    public int getSamplesPerFrame() {
        return 40;
    }

    public static ExtractorSound.Factory getFactory() {
        return new ExtractorSound.Factory() {
            public ExtractorSound create() {
                return new ExtractorAMR();
            }

            public String[] getSupportedExtensions() {
                return new String[]{"3gpp", "3gp", "amr"};
            }
        };
    }

    public int getNumFrames() {
        return this.mNumFrames;
    }

    public int[] getFrameOffsets() {
        return this.mFrameOffsets;
    }

    public int[] getFrameLens() {
        return this.mFrameLens;
    }

    public int[] getFrameGains() {
        return this.mFrameGains;
    }

    public void readFile(File file) throws IOException {
        super.readFile(file);
        this.mNumFrames = 0;
        this.mMaxFrames = 64;
        int i = this.mMaxFrames;
        this.mFrameOffsets = new int[i];
        this.mFrameLens = new int[i];
        this.mFrameGains = new int[i];
        this.mMinGain = 1000000000;
        this.mMaxGain = 0;
        this.mOffset = 0;
        int length = (int) this.mInputFile.length();
        if (length >= 128) {
            FileInputStream fileInputStream = new FileInputStream(this.mInputFile);
            try {
                byte[] bArr = new byte[12];
                int read = fileInputStream.read(bArr, 0, 6);
                if (read < 0) {
                    Log.m22e("ExtractorAMR", "readFile readSize:" + read);
                }
                this.mOffset += 6;
                if (bArr[0] == 35 && bArr[1] == 33 && bArr[2] == 65 && bArr[3] == 77 && bArr[4] == 82 && bArr[5] == 10) {
                    parseAMR(fileInputStream, length - 6);
                }
                Log.m29v("ExtractorAMR", "file size : " + length + " stream : " + fileInputStream.available() + " mOffset : " + this.mOffset + " LastFrameOffset : " + (this.mFrameOffsets[this.mNumFrames - 1] + this.mFrameLens[this.mNumFrames - 1]));
                int read2 = fileInputStream.read(bArr, 6, 6);
                if (read2 < 0) {
                    Log.m22e("ExtractorAMR", "readFile readSize:" + read2);
                }
                this.mOffset += 6;
                if (bArr[4] == 102 && bArr[5] == 116 && bArr[6] == 121 && bArr[7] == 112 && bArr[8] == 51 && bArr[9] == 103 && bArr[10] == 112 && bArr[11] == 52) {
                    byte b = (byte) (((bArr[0] & 255) << 24) | ((bArr[1] & 255) << 16) | ((bArr[2] & 255) << 8) | (bArr[3] & 255));
                    if (b < 4 || b > length - 8) {
                        fileInputStream.close();
                        throw new IOException("Didn't find box");
                    }
                    int i2 = b - 12;
                    long skip = fileInputStream.skip((long) i2);
                    if (skip < 0) {
                        Log.m22e("ExtractorAMR", "readFile skipSize:" + skip);
                    }
                    this.mOffset += i2;
                    parse3gpp(fileInputStream, length - b);
                }
                fileInputStream.close();
            } catch (Exception unused) {
                fileInputStream.close();
                throw new IOException("stream exception");
            }
        } else {
            throw new IOException("File too small to parse");
        }
    }

    private void parse3gpp(InputStream inputStream, int i) throws IOException {
        if (i >= 8) {
            byte[] bArr = new byte[8];
            int read = inputStream.read(bArr, 0, 8);
            if (read < 0) {
                Log.m22e("ExtractorAMR", "parse3gpp readSize:" + read);
            }
            this.mOffset += 8;
            byte b = (byte) (((bArr[2] & 255) << 8) | ((bArr[0] & 255) << 24) | ((bArr[1] & 255) << 16) | (bArr[3] & 255));
            if (b <= i && b > 0) {
                if (bArr[4] == 109 && bArr[5] == 100 && bArr[6] == 97 && bArr[7] == 116) {
                    parseAMR(inputStream, b);
                    return;
                }
                int i2 = b - 8;
                long skip = inputStream.skip((long) i2);
                if (skip < 0) {
                    Log.m22e("ExtractorAMR", "parse3gpp skipSize:" + skip);
                }
                this.mOffset += i2;
                parse3gpp(inputStream, i - b);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void parseAMR(InputStream inputStream, int i) throws IOException {
        int[] iArr = new int[4];
        int i2 = 0;
        for (int i3 = 0; i3 < 4; i3++) {
            iArr[i3] = 0;
        }
        int i4 = i;
        while (i4 > 0) {
            int parseAMRFrame = parseAMRFrame(inputStream, i4, iArr);
            i2 += parseAMRFrame;
            i4 -= parseAMRFrame;
            ExtractorSound.ProgressListener progressListener = this.mProgressListener;
            if (progressListener != null && !progressListener.reportProgress((((double) i2) * 1.0d) / ((double) i))) {
                return;
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v0, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v121, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v126, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v127, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v128, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v129, resolved type: byte} */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x0086, code lost:
        r5 = r5;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x01fc A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x015a  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x015d  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0162 A[LOOP:5: B:67:0x0160->B:68:0x0162, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0175 A[LOOP:6: B:70:0x0173->B:71:0x0175, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x01e6  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x01e9  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x01fa  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int parseAMRFrame(java.io.InputStream r23, int r24, int[] r25) throws java.io.IOException {
        /*
            r22 = this;
            r6 = r22
            r0 = r23
            r1 = r24
            int r7 = r6.mOffset
            r8 = 1
            byte[] r2 = new byte[r8]
            r9 = 0
            int r3 = r0.read(r2, r9, r8)
            if (r3 >= 0) goto L_0x0028
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "parseAMRFrame readSize:"
            r4.append(r5)
            r4.append(r3)
            java.lang.String r3 = r4.toString()
            java.lang.String r4 = "ExtractorAMR"
            com.sec.android.app.voicenote.provider.Log.m22e(r4, r3)
        L_0x0028:
            int r3 = r6.mOffset
            int r3 = r3 + r8
            r6.mOffset = r3
            byte r2 = r2[r9]
            r2 = r2 & 255(0xff, float:3.57E-43)
            r10 = 3
            int r2 = r2 >> r10
            int r2 = r2 % 15
            int[] r3 = BLOCK_SIZES
            r3 = r3[r2]
            int r11 = r3 + 1
            if (r11 <= r1) goto L_0x003e
            return r1
        L_0x003e:
            if (r3 != 0) goto L_0x0041
            return r8
        L_0x0041:
            byte[] r1 = new byte[r3]
            int r0 = r0.read(r1, r9, r3)
            if (r0 >= 0) goto L_0x005f
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "parseAMRFrame readSize:"
            r4.append(r5)
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            java.lang.String r4 = "ExtractorAMR"
            com.sec.android.app.voicenote.provider.Log.m22e(r4, r0)
        L_0x005f:
            int r0 = r6.mOffset
            int r0 = r0 + r3
            r6.mOffset = r0
            int r3 = r3 * 8
            int[] r4 = new int[r3]
            byte r0 = r1[r9]
            r0 = r0 & 255(0xff, float:3.57E-43)
            r5 = r0
            r0 = r9
            r12 = r0
        L_0x006f:
            r13 = 7
            if (r0 >= r3) goto L_0x0089
            r14 = r5 & 128(0x80, float:1.794E-43)
            int r14 = r14 >> r13
            r4[r0] = r14
            int r5 = r5 << r8
            r14 = r0 & 7
            if (r14 != r13) goto L_0x0086
            int r13 = r3 + -1
            if (r0 >= r13) goto L_0x0086
            int r12 = r12 + 1
            byte r5 = r1[r12]
            r5 = r5 & 255(0xff, float:3.57E-43)
        L_0x0086:
            int r0 = r0 + 1
            goto L_0x006f
        L_0x0089:
            r14 = 40
            r0 = 32
            r15 = 4
            if (r2 == 0) goto L_0x0303
            if (r2 == r8) goto L_0x0220
            if (r2 == r13) goto L_0x00b0
            java.io.PrintStream r0 = java.lang.System.out
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "Unsupported frame type: "
            r1.append(r3)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.println(r1)
            r6.addFrame(r7, r11, r8)
        L_0x00ad:
            r3 = r6
            goto L_0x03e3
        L_0x00b0:
            int[] r3 = new int[r15]
            int[] r2 = new int[r15]
            int[] r1 = new int[r15]
            int[][] r0 = new int[r15][]
            r5 = r9
        L_0x00b9:
            if (r5 >= r15) goto L_0x00c4
            r12 = 10
            int[] r12 = new int[r12]
            r0[r5] = r12
            int r5 = r5 + 1
            goto L_0x00b9
        L_0x00c4:
            r12 = r0
            r0 = r22
            r16 = r1
            r1 = r4
            r17 = r2
            r2 = r3
            r18 = r3
            r3 = r17
            r4 = r16
            r5 = r12
            r0.getMR122Params(r1, r2, r3, r4, r5)
            int[] r0 = new int[r14]
            r1 = r9
            r2 = r1
        L_0x00db:
            if (r1 >= r15) goto L_0x00ad
            r3 = r9
        L_0x00de:
            if (r3 >= r14) goto L_0x00e5
            r0[r3] = r9
            int r3 = r3 + 1
            goto L_0x00de
        L_0x00e5:
            r3 = r9
        L_0x00e6:
            r4 = 5
            if (r3 >= r4) goto L_0x0120
            r5 = r12[r1]
            r5 = r5[r3]
            int r5 = r5 >> r10
            r5 = r5 & r8
            if (r5 != 0) goto L_0x00f4
            r5 = 4096(0x1000, float:5.74E-42)
            goto L_0x00f6
        L_0x00f4:
            r5 = -4096(0xfffffffffffff000, float:NaN)
        L_0x00f6:
            int[] r19 = GRAY
            r20 = r12[r1]
            r20 = r20[r3]
            r20 = r20 & 7
            r20 = r19[r20]
            int r20 = r20 * 5
            int r15 = r3 + r20
            r20 = r12[r1]
            int r21 = r3 + 5
            r20 = r20[r21]
            r20 = r20 & 7
            r19 = r19[r20]
            int r19 = r19 * 5
            int r4 = r3 + r19
            r0[r15] = r5
            if (r4 >= r15) goto L_0x0117
            int r5 = -r5
        L_0x0117:
            r15 = r0[r4]
            int r15 = r15 + r5
            r0[r4] = r15
            int r3 = r3 + 1
            r15 = 4
            goto L_0x00e6
        L_0x0120:
            r3 = r18[r1]
            if (r1 == 0) goto L_0x013f
            r5 = 2
            if (r1 != r5) goto L_0x0128
            goto L_0x0140
        L_0x0128:
            r15 = 18
            r13 = 143(0x8f, float:2.0E-43)
            int r2 = r2 - r4
            if (r2 >= r15) goto L_0x0130
            goto L_0x0131
        L_0x0130:
            r15 = r2
        L_0x0131:
            int r2 = r15 + 9
            if (r2 <= r13) goto L_0x0137
            r15 = 134(0x86, float:1.88E-43)
        L_0x0137:
            int r3 = r3 + 5
            int r3 = r3 / 6
            int r15 = r15 + r3
            int r15 = r15 - r8
            r2 = r15
            goto L_0x014e
        L_0x013f:
            r5 = 2
        L_0x0140:
            r2 = 463(0x1cf, float:6.49E-43)
            if (r3 >= r2) goto L_0x014b
            int r3 = r3 + 5
            int r3 = r3 / 6
            int r3 = r3 + 17
            goto L_0x014d
        L_0x014b:
            int r3 = r3 + -368
        L_0x014d:
            r2 = r3
        L_0x014e:
            int[] r3 = QUA_GAIN_PITCH
            r4 = r17[r1]
            r3 = r3[r4]
            int r3 = r3 >> r5
            int r3 = r3 << r5
            r4 = 16383(0x3fff, float:2.2957E-41)
            if (r3 <= r4) goto L_0x015d
            r3 = 32767(0x7fff, float:4.5916E-41)
            goto L_0x015f
        L_0x015d:
            int r3 = r3 * 2
        L_0x015f:
            r4 = r2
        L_0x0160:
            if (r4 >= r14) goto L_0x0171
            r13 = r0[r4]
            int r15 = r4 - r2
            r15 = r0[r15]
            int r15 = r15 * r3
            int r15 = r15 >> 15
            int r13 = r13 + r15
            r0[r4] = r13
            int r4 = r4 + 1
            goto L_0x0160
        L_0x0171:
            r3 = r9
            r4 = r3
        L_0x0173:
            if (r3 >= r14) goto L_0x017e
            r13 = r0[r3]
            r15 = r0[r3]
            int r13 = r13 * r15
            int r4 = r4 + r13
            int r3 = r3 + 1
            goto L_0x0173
        L_0x017e:
            r3 = 1073741823(0x3fffffff, float:1.9999999)
            if (r3 <= r4) goto L_0x0189
            if (r4 >= 0) goto L_0x0186
            goto L_0x0189
        L_0x0186:
            int r4 = r4 * 2
            goto L_0x018c
        L_0x0189:
            r4 = 2147483647(0x7fffffff, float:NaN)
        L_0x018c:
            r3 = 32768(0x8000, float:4.5918E-41)
            int r4 = r4 + r3
            int r3 = r4 >> 16
            r4 = 52428(0xcccc, float:7.3467E-41)
            int r3 = r3 * r4
            double r3 = (double) r3
            double r3 = java.lang.Math.log(r3)
            r19 = 4611686018427387904(0x4000000000000000, double:2.0)
            double r19 = java.lang.Math.log(r19)
            double r3 = r3 / r19
            int r13 = (int) r3
            double r14 = (double) r13
            double r3 = r3 - r14
            r14 = 4674736413210574848(0x40e0000000000000, double:32768.0)
            double r3 = r3 * r14
            int r3 = (int) r3
            r4 = 30
            int r13 = r13 - r4
            int r4 = r13 << 16
            int r3 = r3 * r5
            int r4 = r4 + r3
            r3 = r25[r9]
            int r3 = r3 * 44
            r13 = r25[r8]
            int r13 = r13 * 37
            int r3 = r3 + r13
            r13 = r25[r5]
            int r13 = r13 * 22
            int r3 = r3 + r13
            r13 = r25[r10]
            int r13 = r13 * 12
            int r3 = r3 + r13
            int r3 = r3 * r5
            r13 = 783741(0xbf57d, float:1.098255E-39)
            int r3 = r3 + r13
            int r3 = r3 - r4
            int r3 = r3 / r5
            int r4 = r3 >> 16
            int r3 = r3 >> r8
            int r13 = r4 << 15
            int r3 = r3 - r13
            r13 = 4611686018427387904(0x4000000000000000, double:2.0)
            double r5 = (double) r4
            double r3 = (double) r3
            r20 = 4674736413210574848(0x40e0000000000000, double:32768.0)
            double r3 = r3 / r20
            double r5 = r5 + r3
            double r3 = java.lang.Math.pow(r13, r5)
            r5 = 4602678819172646912(0x3fe0000000000000, double:0.5)
            double r3 = r3 + r5
            int r3 = (int) r3
            r4 = 2047(0x7ff, float:2.868E-42)
            if (r3 > r4) goto L_0x01e9
            int r3 = r3 << 4
            goto L_0x01eb
        L_0x01e9:
            r3 = 32767(0x7fff, float:4.5916E-41)
        L_0x01eb:
            r4 = r16[r1]
            int[] r5 = QUA_GAIN_CODE
            int r4 = r4 * r10
            r5 = r5[r4]
            int r3 = r3 * r5
            int r3 = r3 >> 15
            int r3 = r3 << r8
            r5 = r3 & -32768(0xffffffffffff8000, float:NaN)
            if (r5 == 0) goto L_0x01fc
            r3 = 32767(0x7fff, float:4.5916E-41)
        L_0x01fc:
            r5 = r3
            r3 = r22
            r3.addFrame(r7, r11, r5)
            int[] r5 = QUA_GAIN_CODE
            int r4 = r4 + 1
            r4 = r5[r4]
            r5 = 2
            r6 = r25[r5]
            r25[r10] = r6
            r6 = r25[r8]
            r25[r5] = r6
            r5 = r25[r9]
            r25[r8] = r5
            r25[r9] = r4
            int r1 = r1 + 1
            r6 = r3
            r13 = 7
            r14 = 40
            r15 = 4
            goto L_0x00db
        L_0x0220:
            r3 = r6
            r1 = r15
            int[] r2 = new int[r1]
            r5 = 24
            r5 = r4[r5]
            r6 = 25
            r6 = r4[r6]
            r12 = 2
            int r6 = r6 * r12
            int r5 = r5 + r6
            r6 = 26
            r6 = r4[r6]
            int r6 = r6 * r1
            int r5 = r5 + r6
            r1 = 36
            r1 = r4[r1]
            int r1 = r1 * 8
            int r5 = r5 + r1
            r1 = 45
            r1 = r4[r1]
            int r1 = r1 * 16
            int r5 = r5 + r1
            r1 = 55
            r1 = r4[r1]
            int r1 = r1 * r0
            int r5 = r5 + r1
            r2[r9] = r5
            r1 = 27
            r1 = r4[r1]
            r5 = 28
            r5 = r4[r5]
            r6 = 2
            int r5 = r5 * r6
            int r1 = r1 + r5
            r5 = 29
            r5 = r4[r5]
            r6 = 4
            int r5 = r5 * r6
            int r1 = r1 + r5
            r5 = 37
            r5 = r4[r5]
            int r5 = r5 * 8
            int r1 = r1 + r5
            r5 = 46
            r5 = r4[r5]
            int r5 = r5 * 16
            int r1 = r1 + r5
            r5 = 56
            r5 = r4[r5]
            int r5 = r5 * r0
            int r1 = r1 + r5
            r2[r8] = r1
            r1 = 30
            r1 = r4[r1]
            r5 = 31
            r5 = r4[r5]
            r6 = 2
            int r5 = r5 * r6
            int r1 = r1 + r5
            r5 = r4[r0]
            r6 = 4
            int r5 = r5 * r6
            int r1 = r1 + r5
            r5 = 38
            r5 = r4[r5]
            int r5 = r5 * 8
            int r1 = r1 + r5
            r5 = 47
            r5 = r4[r5]
            int r5 = r5 * 16
            int r1 = r1 + r5
            r5 = 57
            r5 = r4[r5]
            int r5 = r5 * r0
            int r1 = r1 + r5
            r5 = 2
            r2[r5] = r1
            r1 = 33
            r1 = r4[r1]
            r6 = 34
            r6 = r4[r6]
            int r6 = r6 * r5
            int r1 = r1 + r6
            r5 = 35
            r5 = r4[r5]
            r6 = 4
            int r5 = r5 * r6
            int r1 = r1 + r5
            r5 = 39
            r5 = r4[r5]
            int r5 = r5 * 8
            int r1 = r1 + r5
            r5 = 48
            r5 = r4[r5]
            int r5 = r5 * 16
            int r1 = r1 + r5
            r5 = 58
            r4 = r4[r5]
            int r4 = r4 * r0
            int r1 = r1 + r4
            r2[r10] = r1
            r0 = r9
        L_0x02c2:
            r1 = 4
            if (r0 >= r1) goto L_0x03e3
            r1 = 385963008(0x17015400, float:4.178817E-25)
            r4 = r25[r9]
            int r4 = r4 * 5571
            int r4 = r4 + r1
            r1 = r25[r8]
            int r1 = r1 * 4751
            int r4 = r4 + r1
            r1 = 2
            r5 = r25[r1]
            int r5 = r5 * 2785
            int r4 = r4 + r5
            r1 = r25[r10]
            int r1 = r1 * 1556
            int r4 = r4 + r1
            int r1 = r4 >> 15
            int[] r4 = QUA_ENER_MR515
            r5 = r2[r0]
            r4 = r4[r5]
            int[] r5 = GAIN_FAC_MR515
            r6 = r2[r0]
            r5 = r5[r6]
            r6 = 2
            r12 = r25[r6]
            r25[r10] = r12
            r12 = r25[r8]
            r25[r6] = r12
            r6 = r25[r9]
            r25[r8] = r6
            r25[r9] = r4
            int r1 = r1 * r5
            int r1 = r1 >> 24
            r3.addFrame(r7, r11, r1)
            int r0 = r0 + 1
            goto L_0x02c2
        L_0x0303:
            r3 = r6
            r1 = r15
            int[] r2 = new int[r1]
            r5 = 28
            r5 = r4[r5]
            r6 = 29
            r6 = r4[r6]
            r12 = 2
            int r6 = r6 * r12
            int r5 = r5 + r6
            r6 = 30
            r6 = r4[r6]
            int r6 = r6 * r1
            int r5 = r5 + r6
            r1 = 31
            r1 = r4[r1]
            int r1 = r1 * 8
            int r5 = r5 + r1
            r1 = 46
            r1 = r4[r1]
            int r1 = r1 * 16
            int r5 = r5 + r1
            r1 = 47
            r1 = r4[r1]
            int r1 = r1 * r0
            int r5 = r5 + r1
            r1 = 48
            r1 = r4[r1]
            int r1 = r1 * 64
            int r5 = r5 + r1
            r1 = 49
            r1 = r4[r1]
            int r1 = r1 * 128
            int r5 = r5 + r1
            r2[r9] = r5
            r1 = r2[r9]
            r2[r8] = r1
            r1 = r4[r0]
            r5 = 33
            r5 = r4[r5]
            r6 = 2
            int r5 = r5 * r6
            int r1 = r1 + r5
            r5 = 34
            r5 = r4[r5]
            r6 = 4
            int r5 = r5 * r6
            int r1 = r1 + r5
            r5 = 35
            r5 = r4[r5]
            int r5 = r5 * 8
            int r1 = r1 + r5
            r5 = 40
            r5 = r4[r5]
            int r5 = r5 * 16
            int r1 = r1 + r5
            r5 = 41
            r5 = r4[r5]
            int r5 = r5 * r0
            int r1 = r1 + r5
            r0 = 42
            r0 = r4[r0]
            int r0 = r0 * 64
            int r1 = r1 + r0
            r0 = 43
            r0 = r4[r0]
            int r0 = r0 * 128
            int r1 = r1 + r0
            r0 = 2
            r2[r0] = r1
            r1 = r2[r0]
            r2[r10] = r1
            r1 = r9
            r4 = 4
        L_0x037b:
            if (r1 >= r4) goto L_0x03e3
            r5 = r2[r1]
            int r5 = r5 * r4
            r6 = r1 & 1
            int r6 = r6 * r0
            int r5 = r5 + r6
            int r5 = r5 + r8
            int[] r0 = GAIN_FAC_MR475
            r0 = r0[r5]
            double r5 = (double) r0
            double r5 = java.lang.Math.log(r5)
            r12 = 4611686018427387904(0x4000000000000000, double:2.0)
            double r12 = java.lang.Math.log(r12)
            double r5 = r5 / r12
            int r12 = (int) r5
            double r13 = (double) r12
            double r5 = r5 - r13
            r13 = 4674736413210574848(0x40e0000000000000, double:32768.0)
            double r5 = r5 * r13
            int r5 = (int) r5
            int r12 = r12 + -12
            r6 = 49320(0xc0a8, float:6.9112E-41)
            int r12 = r12 * r6
            int r5 = r5 * 24660
            int r5 = r5 >> 15
            r6 = 2
            int r5 = r5 * r6
            int r12 = r12 + r5
            int r12 = r12 * 8192
            r5 = 32768(0x8000, float:4.5918E-41)
            int r12 = r12 + r5
            int r5 = r12 >> 16
            r6 = 385963008(0x17015400, float:4.178817E-25)
            r12 = r25[r9]
            int r12 = r12 * 5571
            int r12 = r12 + r6
            r6 = r25[r8]
            int r6 = r6 * 4751
            int r12 = r12 + r6
            r6 = 2
            r13 = r25[r6]
            int r13 = r13 * 2785
            int r12 = r12 + r13
            r13 = r25[r10]
            int r13 = r13 * 1556
            int r12 = r12 + r13
            int r12 = r12 >> 15
            r13 = r25[r6]
            r25[r10] = r13
            r13 = r25[r8]
            r25[r6] = r13
            r13 = r25[r9]
            r25[r8] = r13
            r25[r9] = r5
            int r12 = r12 * r0
            int r0 = r12 >> 24
            r3.addFrame(r7, r11, r0)
            int r1 = r1 + 1
            r0 = r6
            goto L_0x037b
        L_0x03e3:
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.helper.ExtractorAMR.parseAMRFrame(java.io.InputStream, int, int[]):int");
    }

    /* access modifiers changed from: package-private */
    public void addFrame(int i, int i2, int i3) {
        int[] iArr = this.mFrameOffsets;
        int i4 = this.mNumFrames;
        iArr[i4] = i;
        this.mFrameLens[i4] = i2;
        this.mFrameGains[i4] = i3;
        if (i3 < this.mMinGain) {
            this.mMinGain = i3;
        }
        if (i3 > this.mMaxGain) {
            this.mMaxGain = i3;
        }
        this.mNumFrames++;
        int i5 = this.mNumFrames;
        int i6 = this.mMaxFrames;
        if (i5 == i6) {
            int i7 = i6 * 2;
            int[] iArr2 = new int[i7];
            int[] iArr3 = new int[i7];
            int[] iArr4 = new int[i7];
            for (int i8 = 0; i8 < this.mNumFrames; i8++) {
                iArr2[i8] = this.mFrameOffsets[i8];
                iArr3[i8] = this.mFrameLens[i8];
                iArr4[i8] = this.mFrameGains[i8];
            }
            this.mFrameOffsets = iArr2;
            this.mFrameLens = iArr3;
            this.mFrameGains = iArr4;
            this.mMaxFrames = i7;
        }
    }

    /* access modifiers changed from: package-private */
    public void getMR122Params(int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4, int[][] iArr5) {
        iArr2[0] = iArr[45] + (iArr[43] * 2) + (iArr[41] * 4) + (iArr[39] * 8) + (iArr[37] * 16) + (iArr[35] * 32) + (iArr[33] * 64) + (iArr[31] * 128) + (iArr[29] * 256);
        iArr2[1] = iArr[242] + (iArr[79] * 2) + (iArr[77] * 4) + (iArr[75] * 8) + (iArr[73] * 16) + (iArr[71] * 32);
        iArr2[2] = iArr[46] + (iArr[44] * 2) + (iArr[42] * 4) + (iArr[40] * 8) + (iArr[38] * 16) + (iArr[36] * 32) + (iArr[34] * 64) + (iArr[32] * 128) + (iArr[30] * 256);
        iArr2[3] = iArr[243] + (iArr[80] * 2) + (iArr[78] * 4) + (iArr[76] * 8) + (iArr[74] * 16) + (iArr[72] * 32);
        iArr3[0] = iArr[88] + (iArr[55] * 2) + (iArr[51] * 4) + (iArr[47] * 8);
        iArr3[1] = iArr[89] + (iArr[56] * 2) + (iArr[52] * 4) + (iArr[48] * 8);
        iArr3[2] = iArr[90] + (iArr[57] * 2) + (iArr[53] * 4) + (iArr[49] * 8);
        iArr3[3] = iArr[91] + (iArr[58] * 2) + (iArr[54] * 4) + (iArr[50] * 8);
        iArr4[0] = iArr[104] + (iArr[92] * 2) + (iArr[67] * 4) + (iArr[63] * 8) + (iArr[59] * 16);
        iArr4[1] = iArr[105] + (iArr[93] * 2) + (iArr[68] * 4) + (iArr[64] * 8) + (iArr[60] * 16);
        iArr4[2] = iArr[106] + (iArr[94] * 2) + (iArr[69] * 4) + (iArr[65] * 8) + (iArr[61] * 16);
        iArr4[3] = iArr[107] + (iArr[95] * 2) + (iArr[70] * 4) + (iArr[66] * 8) + (iArr[62] * 16);
        iArr5[0][0] = iArr[122] + (iArr[123] * 2) + (iArr[124] * 4) + (iArr[96] * 8);
        iArr5[0][1] = iArr[125] + (iArr[126] * 2) + (iArr[127] * 4) + (iArr[100] * 8);
        iArr5[0][2] = iArr[128] + (iArr[129] * 2) + (iArr[130] * 4) + (iArr[108] * 8);
        iArr5[0][3] = iArr[131] + (iArr[132] * 2) + (iArr[133] * 4) + (iArr[112] * 8);
        iArr5[0][4] = iArr[134] + (iArr[135] * 2) + (iArr[136] * 4) + (iArr[116] * 8);
        iArr5[0][5] = iArr[182] + (iArr[183] * 2) + (iArr[184] * 4);
        iArr5[0][6] = iArr[185] + (iArr[186] * 2) + (iArr[187] * 4);
        iArr5[0][7] = iArr[188] + (iArr[189] * 2) + (iArr[190] * 4);
        iArr5[0][8] = iArr[191] + (iArr[192] * 2) + (iArr[193] * 4);
        iArr5[0][9] = iArr[194] + (iArr[195] * 2) + (iArr[196] * 4);
        iArr5[1][0] = iArr[137] + (iArr[138] * 2) + (iArr[139] * 4) + (iArr[97] * 8);
        iArr5[1][1] = iArr[140] + (iArr[141] * 2) + (iArr[142] * 4) + (iArr[101] * 8);
        iArr5[1][2] = iArr[143] + (iArr[144] * 2) + (iArr[145] * 4) + (iArr[109] * 8);
        iArr5[1][3] = iArr[146] + (iArr[147] * 2) + (iArr[148] * 4) + (iArr[113] * 8);
        iArr5[1][4] = iArr[149] + (iArr[150] * 2) + (iArr[151] * 4) + (iArr[117] * 8);
        iArr5[1][5] = iArr[197] + (iArr[198] * 2) + (iArr[199] * 4);
        iArr5[1][6] = iArr[200] + (iArr[201] * 2) + (iArr[202] * 4);
        iArr5[1][7] = iArr[203] + (iArr[204] * 2) + (iArr[205] * 4);
        iArr5[1][8] = iArr[206] + (iArr[207] * 2) + (iArr[208] * 4);
        iArr5[1][9] = iArr[209] + (iArr[210] * 2) + (iArr[211] * 4);
        iArr5[2][0] = iArr[152] + (iArr[153] * 2) + (iArr[154] * 4) + (iArr[98] * 8);
        iArr5[2][1] = iArr[155] + (iArr[156] * 2) + (iArr[157] * 4) + (iArr[102] * 8);
        iArr5[2][2] = iArr[158] + (iArr[159] * 2) + (iArr[160] * 4) + (iArr[110] * 8);
        iArr5[2][3] = iArr[161] + (iArr[162] * 2) + (iArr[163] * 4) + (iArr[114] * 8);
        iArr5[2][4] = iArr[164] + (iArr[165] * 2) + (iArr[166] * 4) + (iArr[118] * 8);
        iArr5[2][5] = iArr[212] + (iArr[213] * 2) + (iArr[214] * 4);
        iArr5[2][6] = iArr[215] + (iArr[216] * 2) + (iArr[217] * 4);
        iArr5[2][7] = iArr[218] + (iArr[219] * 2) + (iArr[220] * 4);
        iArr5[2][8] = iArr[221] + (iArr[222] * 2) + (iArr[223] * 4);
        iArr5[2][9] = iArr[224] + (iArr[225] * 2) + (iArr[226] * 4);
        iArr5[3][0] = iArr[167] + (iArr[168] * 2) + (iArr[169] * 4) + (iArr[99] * 8);
        iArr5[3][1] = iArr[170] + (iArr[171] * 2) + (iArr[172] * 4) + (iArr[103] * 8);
        iArr5[3][2] = iArr[173] + (iArr[174] * 2) + (iArr[175] * 4) + (iArr[111] * 8);
        iArr5[3][3] = iArr[176] + (iArr[177] * 2) + (iArr[178] * 4) + (iArr[115] * 8);
        iArr5[3][4] = iArr[179] + (iArr[180] * 2) + (iArr[181] * 4) + (iArr[119] * 8);
        iArr5[3][5] = iArr[227] + (iArr[228] * 2) + (iArr[229] * 4);
        iArr5[3][6] = iArr[230] + (iArr[231] * 2) + (iArr[232] * 4);
        iArr5[3][7] = iArr[233] + (iArr[234] * 2) + (iArr[235] * 4);
        iArr5[3][8] = iArr[236] + (iArr[237] * 2) + (iArr[238] * 4);
        iArr5[3][9] = iArr[239] + (iArr[240] * 2) + (iArr[241] * 4);
    }
}
