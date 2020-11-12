package com.sec.android.app.voicenote.service.helper;

import android.util.SparseArray;

import com.sec.android.app.voicenote.provider.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ExtractorAAC extends ExtractorSound {
    private static final String TAG = "ExtractorAAC";
    public static final int kDINF = 1684631142;
    public static final int kHDLR = 1751411826;
    public static final int kMDAT = 1835295092;
    public static final int kMDHD = 1835296868;
    public static final int kMDIA = 1835297121;
    public static final int kMINF = 1835626086;
    public static final int kMOOV = 1836019574;
    public static final int kMVHD = 1836476516;

    public static final int kSMHD = 1936549988;
    public static final int kSTBL = 1937007212;
    public static final int kSTSD = 1937011556;
    public static final int kSTSZ = 1937011578;
    public static final int kSTTS = 1937011827;
    public static final int kTKHD = 1953196132;
    public static final int kTRAK = 1953653099;
    public static final int[] kRequiredAtoms = {kDINF, kHDLR, kMDHD, kMDIA, kMINF, kMOOV, kMVHD, kSMHD, kSTBL, kSTSD, kSTSZ, kSTTS, kTKHD, kTRAK};
    public static final int[] kSaveDataAtoms = {kDINF, kHDLR, kMDHD, kMVHD, kSMHD, kTKHD, kSTSD};
    private SparseArray<Atom> mAtomArray;
    private int mChannels;
    private int mFileSize;
    private int[] mFrameGains;
    private int[] mFrameLens;
    private int[] mFrameOffsets;
    private int mMaxGain;
    private int mMdatLength;
    private int mMdatOffset;
    private int mMinGain;
    private int mNumFrames;
    private int mOffset;
    private int mSampleRate;
    private int mSamplesPerFrame;

    public String getFiletype() {
        return "AAC";
    }

    public static ExtractorSound.Factory getFactory() {
        return new ExtractorSound.Factory() {
            public ExtractorSound create() {
                return new ExtractorAAC();
            }

            public String[] getSupportedExtensions() {
                return new String[]{"aac", "m4a", "3ga"};
            }
        };
    }

    static class Atom {
        public byte[] data;
        public int len;
        public int start;

        Atom() {
        }
    }

    public int getNumFrames() {
        return this.mNumFrames;
    }

    public int getSamplesPerFrame() {
        return this.mSamplesPerFrame;
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

    public int getFileSizeBytes() {
        return this.mFileSize;
    }

    public int getAvgBitrateKbps() {
        return this.mFileSize / (this.mNumFrames * this.mSamplesPerFrame);
    }

    public int getSampleRate() {
        return this.mSampleRate;
    }

    public int getChannels() {
        return this.mChannels;
    }

    public String atomToString(int i) {
        return ((("" + ((char) ((i >> 24) & 255))) + ((char) ((i >> 16) & 255))) + ((char) ((i >> 8) & 255))) + ((char) (i & 255));
    }

    public void readFile(File file) throws IOException {
        super.readFile(file);
        this.mChannels = 0;
        this.mSampleRate = 0;
        this.mSamplesPerFrame = 0;
        this.mNumFrames = 0;
        this.mMinGain = 255;
        this.mMaxGain = 0;
        this.mOffset = 0;
        this.mMdatOffset = -1;
        this.mMdatLength = -1;
        this.mAtomArray = new SparseArray<>();
        this.mFileSize = (int) this.mInputFile.length();
        Log.m19d(TAG, "readFile File size = " + this.mFileSize);
        if (this.mFileSize >= 128) {
            FileInputStream fileInputStream = new FileInputStream(this.mInputFile);
            byte[] bArr = new byte[8];
            try {
                int read = fileInputStream.read(bArr, 0, 8);
                if (read < 0) {
                    Log.m22e(TAG, "readFile readSize = " + read);
                    fileInputStream.close();
                    throw new IOException("stream.read exception");
                } else if (bArr[0] == 0 && bArr[4] == 102 && bArr[5] == 116 && bArr[6] == 121 && bArr[7] == 112) {
                    fileInputStream.close();
                    FileInputStream fileInputStream2 = new FileInputStream(this.mInputFile);
                    parseMp4(fileInputStream2, this.mFileSize);
                    if (this.mMdatOffset <= 0 || this.mMdatLength <= 0) {
                        fileInputStream2.close();
                        throw new IOException("Didn't find mdat");
                    }
                    fileInputStream2.close();
                    try {
                        FileInputStream fileInputStream3 = new FileInputStream(this.mInputFile);
                        long skip = fileInputStream3.skip((long) this.mMdatOffset);
                        if (skip < 0) {
                            Log.m22e(TAG, "readFile skipSize = " + skip);
                        }
                        this.mOffset = this.mMdatOffset;
                        try {
                            parseMdat(fileInputStream3, this.mMdatLength);
                            boolean z = false;
                            for (int i : kRequiredAtoms) {
                                if (this.mAtomArray.get(i, (Atom) null) == null) {
                                    System.out.println("Missing atom: " + atomToString(i));
                                    z = true;
                                }
                            }
                            if (!z) {
                                fileInputStream3.close();
                            } else {
                                fileInputStream3.close();
                                throw new IOException("Could not parse MP4 file");
                            }
                        } catch (Exception e) {
                            fileInputStream3.close();
                            Log.m22e(TAG, "parseMdat exception : " + e);
                            throw new IOException("stream.read exception");
                        }
                    } catch (Exception unused) {
                        fileInputStream2.close();
                        throw new IOException("new FileInputStream exception");
                    }
                } else {
                    fileInputStream.close();
                    throw new IOException("Unknown file format");
                }
            } catch (Exception unused2) {
                fileInputStream.close();
                throw new IOException("stream.read exception");
            }
        } else {
            throw new IOException("File too small to parse");
        }
    }

    private void parseMp4(InputStream inputStream, int i) throws IOException {
        InputStream inputStream2 = inputStream;
        byte[] bArr = new byte[8];
        int i2 = i;
        for (byte b = 8; i2 > b; b = 8) {
            int i3 = this.mOffset;
            int i4 = 0;
            int read = inputStream2.read(bArr, 0, b);
            if (read < 0) {
                Log.m22e(TAG, "parseMp4 readSize = " + read);
            }
            int b2 =  (((bArr[0] & 255) << 24) | ((bArr[1] & 255) << 16) | ((bArr[2] & 255) << b) | (bArr[3] & 255));
            if (b2 > i2) {
                b2 = (byte) i2;
            }
            int b3 =  (((bArr[4] & 255) << 24) | ((bArr[5] & 255) << 16) | ((bArr[6] & 255) << b) | (bArr[7] & 255));
            Atom atom = new Atom();
            atom.start = this.mOffset;
            atom.len = b2;
            this.mAtomArray.put(b3, atom);
            this.mOffset += b;
            switch (b3) {
                case kMDAT /*1835295092*/:
                    this.mMdatOffset = this.mOffset;
                    this.mMdatLength = b2 - 8;
                    if (b2 == 1 && inputStream2.read(bArr, 0, b) > 0) {
                        b2 =  (((bArr[0] & 255) << 56) | ((bArr[1] & 255) << 48) | ((bArr[2] & 255) << 40) | ((bArr[3] & 255) << 32) | ((bArr[4] & 255) << 24) | ((bArr[5] & 255) << 16) | ((bArr[6] & 255) << b) | (bArr[7] & 255));
                        atom.start = this.mOffset;
                        atom.len = b2;
                        this.mAtomArray.put(b3, atom);
                        this.mOffset += b;
                        this.mMdatOffset = this.mOffset;
                        this.mMdatLength = b2 - 8;
                        Log.m19d(TAG, "parseMp4() 8byte mdat length:" + b2);
                        break;
                    }
                case kMDIA /*1835297121*/:
                case kMINF /*1835626086*/:
                case kMOOV /*1836019574*/:
                case kSTBL /*1937007212*/:
                case kTRAK /*1953653099*/:
                    parseMp4(inputStream2, b2);
                    break;
                case kSTSZ /*1937011578*/:
                    parseStsz(inputStream2, b2 - 8);
                    break;
                case kSTTS /*1937011827*/:
                    parseStts(inputStream2, b2 - 8);
                    break;
                default:
                    int[] iArr = kSaveDataAtoms;
                    int length = iArr.length;
                    int i5 = 0;
                    while (i5 < length) {
                        if (iArr[i5] == b3) {
                            int i6 = b2 - 8;
                            byte[] bArr2 = new byte[i6];
                            int read2 = inputStream2.read(bArr2, i4, i6);
                            if (read2 < 0) {
                                Log.m22e(TAG, "parseMp4 readsize = " + read2);
                            }
                            this.mOffset += i6;
                            Atom atom2 = this.mAtomArray.get(b3);
                            if (atom2 != null) {
                                atom2.data = bArr2;
                            }
                        }
                        i5++;
                        i4 = 0;
                    }
                    break;
            }
            if (b3 == 1937011556) {
                parseMp4aFromStsd();
            }
            i2 -= b2;
            int i7 = b2 - (this.mOffset - i3);
            if (i7 < 0) {
                Log.m22e(TAG, "Went over by " + (-i7) + " bytes" + " , maxLen is " + i2);
                i2 = 0;
                i7 = 0;
            }
            long skip = inputStream2.skip((long) i7);
            if (skip < 0) {
                Log.m22e(TAG, "parseMp4 skipsize = " + skip);
            }
            this.mOffset += i7;
        }
    }

    /* access modifiers changed from: package-private */
    public void parseStts(InputStream inputStream, int i) throws IOException {
        byte[] bArr = new byte[16];
        int read = inputStream.read(bArr, 0, 16);
        if (read < 0) {
            Log.m22e(TAG, "parseStts readSize = " + read);
            return;
        }
        this.mOffset += 16;
        this.mSamplesPerFrame = ((bArr[12] & 255) << 24) | ((bArr[13] & 255) << 16) | ((bArr[14] & 255) << 8) | (bArr[15] & 255);
    }

    /* access modifiers changed from: package-private */
    public void parseStsz(InputStream inputStream, int i) throws IOException {
        byte[] bArr = new byte[12];
        int read = inputStream.read(bArr, 0, 12);
        if (read < 0) {
            Log.m22e(TAG, "parseStsz readSize = " + read);
            return;
        }
        this.mOffset += 12;
        this.mNumFrames = (bArr[11] & 255) | ((bArr[8] & 255) << 24) | ((bArr[9] & 255) << 16) | ((bArr[10] & 255) << 8);
        Log.m19d(TAG, "mNumFrames = " + this.mNumFrames);
        int i2 = this.mNumFrames;
        this.mFrameOffsets = new int[i2];
        this.mFrameLens = new int[i2];
        this.mFrameGains = new int[i2];
        byte[] bArr2 = new byte[(i2 * 4)];
        int read2 = inputStream.read(bArr2, 0, i2 * 4);
        if (read2 < 0) {
            Log.m22e(TAG, "parseStsz readsize:" + read2);
        }
        this.mOffset += this.mNumFrames * 4;
        for (int i3 = 0; i3 < this.mNumFrames; i3++) {
            int i4 = i3 * 4;
            this.mFrameLens[i3] = (bArr2[i4 + 3] & 255) | ((bArr2[i4 + 0] & 255) << 24) | ((bArr2[i4 + 1] & 255) << 16) | ((bArr2[i4 + 2] & 255) << 8);
        }
    }

    /* access modifiers changed from: package-private */
    public void parseMp4aFromStsd() {
        Atom atom = this.mAtomArray.get(kSTSD);
        if (atom != null) {
            byte[] bArr = atom.data;
            this.mChannels = ((bArr[32] & 255) << 8) | (bArr[33] & 255);
            this.mSampleRate = (bArr[41] & 255) | ((bArr[40] & 255) << 8);
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00a3  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00a8  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00b4  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00c0  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00d9 A[LOOP:0: B:1:0x0007->B:36:0x00d9, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00dd A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void parseMdat(java.io.InputStream r12, int r13) throws java.io.IOException {
        /*
            r11 = this;
            int r0 = r11.mOffset
            r1 = 0
            r2 = 0
            r4 = r1
            r3 = r2
            r2 = r4
        L_0x0007:
            int r5 = r11.mNumFrames
            if (r2 >= r5) goto L_0x00dd
            int[] r5 = r11.mFrameOffsets
            int r6 = r11.mOffset
            r5[r2] = r6
            java.lang.String r5 = "megaByte readSize = "
            java.lang.String r6 = "ExtractorAAC"
            if (r3 != 0) goto L_0x0035
            r3 = 1048576(0x100000, float:1.469368E-39)
            byte[] r3 = new byte[r3]
            int r4 = r12.read(r3)
            if (r4 >= 0) goto L_0x0033
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r5)
            r7.append(r4)
            java.lang.String r4 = r7.toString()
            com.sec.android.app.voicenote.provider.Log.m22e(r6, r4)
        L_0x0033:
            r4 = r1
            goto L_0x0097
        L_0x0035:
            int r7 = r3.length
            if (r7 >= r4) goto L_0x0072
            int r7 = r3.length
            int r4 = r4 - r7
            long r7 = (long) r4
            long r7 = r12.skip(r7)
            r9 = 0
            int r4 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r4 >= 0) goto L_0x0059
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r9 = "megaByte skipSize = "
            r4.append(r9)
            r4.append(r7)
            java.lang.String r4 = r4.toString()
            com.sec.android.app.voicenote.provider.Log.m22e(r6, r4)
        L_0x0059:
            int r4 = r12.read(r3)
            if (r4 >= 0) goto L_0x0033
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r5)
            r7.append(r4)
            java.lang.String r4 = r7.toString()
            com.sec.android.app.voicenote.provider.Log.m22e(r6, r4)
            goto L_0x0033
        L_0x0072:
            int r7 = r3.length
            int r7 = r7 + -8
            if (r7 >= r4) goto L_0x0097
            int r7 = r3.length
            int r7 = r7 - r4
            java.lang.System.arraycopy(r3, r4, r3, r1, r7)
            int r4 = r3.length
            int r4 = r4 - r7
            int r4 = r12.read(r3, r7, r4)
            if (r4 >= 0) goto L_0x0033
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r5)
            r7.append(r4)
            java.lang.String r4 = r7.toString()
            com.sec.android.app.voicenote.provider.Log.m22e(r6, r4)
            goto L_0x0033
        L_0x0097:
            int r5 = r11.mOffset
            int r5 = r5 - r0
            int[] r6 = r11.mFrameLens
            r6 = r6[r2]
            int r5 = r5 + r6
            int r6 = r13 + -8
            if (r5 <= r6) goto L_0x00a8
            int[] r5 = r11.mFrameGains
            r5[r2] = r1
            goto L_0x00ac
        L_0x00a8:
            int r4 = r11.readFrameAndComputeGain(r3, r2, r4)
        L_0x00ac:
            int[] r5 = r11.mFrameGains
            r6 = r5[r2]
            int r7 = r11.mMinGain
            if (r6 >= r7) goto L_0x00b8
            r5 = r5[r2]
            r11.mMinGain = r5
        L_0x00b8:
            int[] r5 = r11.mFrameGains
            r6 = r5[r2]
            int r7 = r11.mMaxGain
            if (r6 <= r7) goto L_0x00c4
            r5 = r5[r2]
            r11.mMaxGain = r5
        L_0x00c4:
            com.sec.android.app.voicenote.service.helper.ExtractorSound$ProgressListener r5 = r11.mProgressListener
            if (r5 == 0) goto L_0x00d9
            int r6 = r11.mOffset
            double r6 = (double) r6
            r8 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            double r6 = r6 * r8
            int r8 = r11.mFileSize
            double r8 = (double) r8
            double r6 = r6 / r8
            boolean r5 = r5.reportProgress(r6)
            if (r5 != 0) goto L_0x00d9
            goto L_0x00dd
        L_0x00d9:
            int r2 = r2 + 1
            goto L_0x0007
        L_0x00dd:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.helper.ExtractorAAC.parseMdat(java.io.InputStream, int):void");
    }

    /* access modifiers changed from: package-private */
    public int readFrameAndComputeGain(byte[] bArr, int i, int i2) {
        int i3;
        int i4;
        int i5;
        int b;
        byte[] bArr2 = bArr;
        int i6 = i;
        int i7 = i2;
        int[] iArr = this.mFrameLens;
        if (iArr[i6] < 4) {
            this.mFrameGains[i6] = 0;
            return iArr[i6] + i7;
        }
        int i8 = this.mOffset;
        int i9 = i7 + 4;
        byte[] copyOfRange = Arrays.copyOfRange(bArr2, i7, i9);
        this.mOffset += 4;
        int i10 = (copyOfRange[0] & 224) >> 5;
        if (i10 == 0) {
            int i11 = ((copyOfRange[0] & 1) << 7) | ((copyOfRange[1] & 254) >> 1);
            int[] iArr2 = this.mFrameGains;
            iArr2[i6] = i11;
            if (i6 < 13 && copyOfRange[0] == 1 && copyOfRange[1] == 64) {
                iArr2[i6] = 0;
            }
        } else if (i10 == 1) {
            if (((copyOfRange[1] & 96) >> 5) == 2) {
                b = copyOfRange[1] & 15;
                i4 = (copyOfRange[2] & 254) >> 1;
                i5 = ((copyOfRange[3] & 128) >> 7) | ((copyOfRange[2] & 1) << 1);
                i3 = 25;
            } else {
                b = ((copyOfRange[1] & 15) << 2) | ((copyOfRange[2] & 192) >> 6);
                i4 = -1;
                i5 = (copyOfRange[2] & 24) >> 3;
                i3 = 21;
            }
            if (i5 == 1) {
                int i12 = 0;
                for (int i13 = 0; i13 < 7; i13++) {
                    if ((i4 & (1 << i13)) == 0) {
                        i12++;
                    }
                }
                i3 += b * (i12 + 1);
            }
            int i14 = ((i3 + 7) / 8) + 1;
            byte[] bArr3 = new byte[i14];
            bArr3[0] = copyOfRange[0];
            bArr3[1] = copyOfRange[1];
            bArr3[2] = copyOfRange[2];
            bArr3[3] = copyOfRange[3];
            int i15 = i14 - 4;
            System.arraycopy(bArr2, i9, bArr3, 4, i15);
            this.mOffset += i15;
            i9 += i15;
            int i16 = 0;
            for (int i17 = 0; i17 < 8; i17++) {
                int i18 = i17 + i3;
                int i19 = i18 / 8;
                int i20 = 7 - (i18 % 8);
                i16 += ((bArr3[i19] & (1 << i20)) >> i20) << (7 - i17);
            }
            int[] iArr3 = this.mFrameGains;
            iArr3[i6] = i16;
            if (i6 < 13 && bArr3[0] == 33 && bArr3[1] == 17) {
                iArr3[i6] = 0;
            }
        } else if (i6 > 0) {
            int[] iArr4 = this.mFrameGains;
            iArr4[i6] = iArr4[i6 - 1];
        } else {
            this.mFrameGains[i6] = 0;
        }
        int i21 = this.mFrameLens[i6];
        int i22 = this.mOffset;
        int i23 = i21 - (i22 - i8);
        this.mOffset = i22 + i23;
        return i9 + i23;
    }
}
