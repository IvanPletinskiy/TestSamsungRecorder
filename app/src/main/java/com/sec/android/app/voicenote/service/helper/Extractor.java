package com.sec.android.app.voicenote.service.helper;

import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.helper.ExtractorSound;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Extractor {
    private static final int MAXIMUM_AMPLITUDE = 15000;
    private static final double MIN_SCALE_FACTOR = 0.9d;
    private static final String TAG = "Extractor";
    private static ExtractorSound mExtractorSound;
    private int[] mBuffer = null;
    private int mBufferSize = 0;
    private OnExtractListener mOnExtractListener = null;

    public interface OnExtractListener {
        void onFinished(int[] iArr, int i);
    }

    public void setOnExtractListener(OnExtractListener onExtractListener) {
        this.mOnExtractListener = onExtractListener;
    }

    public static boolean isAMRMode() {
        return mExtractorSound instanceof ExtractorAMR;
    }

    public void startExtract(String str) {
        Log.m29v(TAG, "startExtract");
        try {
            mExtractorSound = ExtractorSound.create(new File(str).getAbsolutePath(), (ExtractorSound.ProgressListener) null);
            if (mExtractorSound != null) {
                computeDoublesForAllZoomLevels();
            }
        } catch (FileNotFoundException e) {
            Log.m24e(TAG, "FileNotFoundException : ", (Throwable) e);
        } catch (IOException e2) {
            Log.m24e(TAG, "IOException : ", (Throwable) e2);
        } finally {
            Log.m19d(TAG, "startExtract FINISHED SIZE " + this.mBufferSize);
            this.mOnExtractListener.onFinished(this.mBuffer, this.mBufferSize);
        }
    }

    private void computeDoublesForAllZoomLevels() {
        double d;
        int i;
        int numFrames = mExtractorSound.getNumFrames();
        int[] frameGains = mExtractorSound.getFrameGains();
        double[] dArr = new double[numFrames];
        if (numFrames == 1) {
            dArr[0] = (double) frameGains[0];
        } else if (numFrames == 2) {
            dArr[0] = (double) frameGains[0];
            dArr[1] = (double) frameGains[1];
        } else if (numFrames > 2) {
            dArr[0] = (((double) frameGains[0]) / 2.0d) + (((double) frameGains[1]) / 2.0d);
            int i2 = 1;
            while (true) {
                i = numFrames - 1;
                if (i2 >= i) {
                    break;
                }
                int i3 = i2 + 1;
                dArr[i2] = (((double) frameGains[i2 - 1]) / 3.0d) + (((double) frameGains[i2]) / 3.0d) + (((double) frameGains[i3]) / 3.0d);
                i2 = i3;
            }
            dArr[i] = (((double) frameGains[numFrames - 2]) / 2.0d) + (((double) frameGains[i]) / 2.0d);
        }
        double d2 = 1.0d;
        for (int i4 = 0; i4 < numFrames; i4++) {
            if (dArr[i4] > d2) {
                d2 = dArr[i4];
            }
        }
        double d3 = d2 > 255.0d ? 255.0d / d2 : 1.0d;
        int[] iArr = new int[256];
        double d4 = 0.0d;
        for (int i5 = 0; i5 < numFrames; i5++) {
            int i6 = (int) (dArr[i5] * d3);
            if (i6 < 0) {
                i6 = 0;
            }
            int i7 = 255;
            if (i6 <= 255) {
                i7 = i6;
            }
            double d5 = (double) i7;
            if (d5 > d4) {
                d4 = d5;
            }
            iArr[i7] = iArr[i7] + 1;
        }
        double d6 = 0.0d;
        int i8 = 0;
        while (d6 < 255.0d && i8 < numFrames / 20) {
            i8 += iArr[(int) d6];
            d6 += 1.0d;
        }
        double d7 = d4;
        int i9 = 0;
        while (d7 > 2.0d && i9 < numFrames / 100) {
            i9 += iArr[(int) d7];
            d7 -= 1.0d;
        }
        if (isAMRMode()) {
            d6 /= d3;
            d7 /= d3;
        }
        double[] dArr2 = new double[numFrames];
        double d8 = d7 - d6;
        for (int i10 = 0; i10 < numFrames; i10++) {
            if (isAMRMode()) {
                double d9 = dArr[i10] - d6 > 0.0d ? (dArr[i10] - d6) / d8 : 0.0d;
                d3 = d9 >= 1.0d ? MIN_SCALE_FACTOR : 1.0d - (((d9 * d9) * d9) * 0.09999999999999998d);
                d = (dArr[i10] - d6) * d3;
            } else {
                d = (dArr[i10] * d3) - d6;
            }
            double d10 = d / d8;
            if (d10 < 0.0d) {
                d10 = 0.0d;
            }
            if (d10 > 1.0d) {
                d10 = 1.0d;
            }
            dArr2[i10] = d10 * d10;
        }
        this.mBufferSize = numFrames * 2;
        int i11 = this.mBufferSize;
        this.mBuffer = new int[i11];
        double[] dArr3 = new double[i11];
        if (numFrames > 0) {
            dArr3[0] = dArr2[0] * 0.5d;
            dArr3[1] = dArr2[0];
            int[] iArr2 = this.mBuffer;
            iArr2[0] = (int) (dArr3[0] * 15000.0d);
            iArr2[1] = (int) (dArr3[1] * 15000.0d);
        }
        for (int i12 = 1; i12 < numFrames; i12++) {
            int i13 = i12 * 2;
            dArr3[i13] = (dArr2[i12 - 1] + dArr2[i12]) * 0.5d;
            int i14 = i13 + 1;
            dArr3[i14] = dArr2[i12];
            int[] iArr3 = this.mBuffer;
            iArr3[i13] = (int) (dArr3[i13] * 15000.0d);
            iArr3[i14] = (int) (dArr3[i14] * 15000.0d);
        }
    }
}
