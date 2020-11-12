package com.sec.android.app.voicenote.service;

import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.helper.ExtractorAMR;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AMREditor extends AbsEditor {
    private static final int HEADER_SIZE = 6;
    private static final String TAG = "AMREditor";
    private static AMREditor mInstance;
    private float mDuration = 0.0f;
    private int[] mFrameLens = null;
    private int[] mFrameOffset = null;
    private File mInputFile = null;
    private int mNumberOfFrames = 0;
    private File mOutputFile = null;
    private float mTimePerFrame = 0.0f;

    private AMREditor() {
        Log.m26i(TAG, "AMREditor creator !!");
    }

    public static AMREditor getInstance() {
        if (mInstance == null) {
            mInstance = new AMREditor();
        }
        return mInstance;
    }

    private void setExtractor(ExtractorAMR extractorAMR) {
        this.mNumberOfFrames = extractorAMR.getNumFrames();
        this.mFrameLens = extractorAMR.getFrameLens();
        this.mFrameOffset = extractorAMR.getFrameOffsets();
        this.mTimePerFrame = (((float) extractorAMR.getSamplesPerFrame()) / ((float) extractorAMR.getSampleRate())) * 1000.0f;
        this.mDuration = ((float) this.mNumberOfFrames) * this.mTimePerFrame;
    }

    public boolean trim(String str, String str2, int i, int i2) {
        FileInputStream fileInputStream;
        FileOutputStream fileOutputStream;
        Log.m29v(TAG, "trim - duration : " + this.mDuration + " start : " + i + " end : " + i2);
        this.mInputFile = new File(str);
        this.mOutputFile = new File(str2);
        ExtractorAMR extractorAMR = new ExtractorAMR();
        try {
            extractorAMR.readFile(this.mInputFile);
            setExtractor(extractorAMR);
            int round = Math.round(((float) i) / this.mTimePerFrame);
            int round2 = Math.round(((float) i2) / this.mTimePerFrame);
            if (round < 0) {
                round = 0;
            }
            int i3 = this.mNumberOfFrames;
            if (round2 > i3) {
                round2 = i3;
            }
            FileInputStream fileInputStream2 = null;
            try {
                fileOutputStream = new FileOutputStream(this.mOutputFile);
                try {
                    fileInputStream = new FileInputStream(this.mInputFile);
                    try {
                        byte[] bArr = new byte[100];
                        if (fileInputStream.read(bArr, 0, 6) > 0) {
                            fileOutputStream.write(bArr, 0, 6);
                        }
                        long skip = fileInputStream.skip((long) (this.mFrameOffset[round] - 6));
                        if (skip < 0) {
                            Log.m19d(TAG, "trim skip HEADER_SIZE : " + skip);
                        }
                        while (round < round2) {
                            if (fileInputStream.read(bArr, 0, this.mFrameLens[round]) > 0) {
                                fileOutputStream.write(bArr, 0, this.mFrameLens[round]);
                            }
                            round += 4;
                        }
                        closeQuietly(fileInputStream);
                        closeQuietly(fileOutputStream);
                        return true;
                    } catch (IOException e) {
//                        e = e;
                        fileInputStream2 = fileInputStream;
                        Log.m24e(TAG, "trim IOException", (Throwable) e);
                        closeQuietly(fileInputStream2);
                        closeQuietly(fileOutputStream);
                        return false;
                    } catch (ArrayIndexOutOfBoundsException | NullPointerException e2) {
//                        e = e2;
                        fileInputStream2 = fileInputStream;
                        try {
//                            Log.m24e(TAG, "trim NullPointerException", e);
                            closeQuietly(fileInputStream2);
                            closeQuietly(fileOutputStream);
                            return false;
                        } catch (Throwable th) {
//                            th = th;
                            fileInputStream = fileInputStream2;
                            closeQuietly(fileInputStream);
                            closeQuietly(fileOutputStream);
                            throw th;
                        }
                    } catch (Throwable th2) {
//                        th = th2;
                        closeQuietly(fileInputStream);
                        closeQuietly(fileOutputStream);
//                        throw th;
                    }
                } catch (IOException e3) {
//                    e = e3;
//                    Log.m24e(TAG, "trim IOException", (Throwable) e);
                    closeQuietly(fileInputStream2);
                    closeQuietly(fileOutputStream);
                    return false;
                } catch (ArrayIndexOutOfBoundsException | NullPointerException e4) {
//                    e = e4;
//                    Log.m24e(TAG, "trim NullPointerException", e);
                    closeQuietly(fileInputStream2);
                    closeQuietly(fileOutputStream);
                    return false;
                }
            } catch (IOException e5) {
//                e = e5;
                fileOutputStream = null;
//                Log.m24e(TAG, "trim IOException", (Throwable) e);
                closeQuietly(fileInputStream2);
                closeQuietly(fileOutputStream);
                return false;
            } catch (ArrayIndexOutOfBoundsException | NullPointerException e6) {
//                e = e6;
                fileOutputStream = null;
//                Log.m24e(TAG, "trim NullPointerException", e);
                closeQuietly(fileInputStream2);
                closeQuietly(fileOutputStream);
                return false;
            } catch (Throwable th3) {
//                th = th3;
                fileOutputStream = null;
                fileInputStream = null;
                closeQuietly(fileInputStream);
                closeQuietly(fileOutputStream);
//                throw th;
            }
        } catch (FileNotFoundException e7) {
            Log.m24e(TAG, "FileNotFoundException", (Throwable) e7);
            return false;
        } catch (IOException e8) {
            Log.m24e(TAG, "IOException", (Throwable) e8);
            return false;
        }
        return true;
    }

    public boolean delete(String str, String str2, int i, int i2) {
        FileInputStream fileInputStream;
        FileOutputStream fileOutputStream;
        Log.m19d(TAG, "delete - duration : " + this.mDuration + " start : " + i + " end : " + i2);
        this.mInputFile = new File(str);
        this.mOutputFile = new File(str2);
        ExtractorAMR extractorAMR = new ExtractorAMR();
        try {
            extractorAMR.readFile(this.mInputFile);
            setExtractor(extractorAMR);
            int round = Math.round(((float) i) / this.mTimePerFrame);
            int round2 = Math.round(((float) i2) / this.mTimePerFrame);
            if (round < 0) {
                round = 0;
            }
            int i3 = this.mNumberOfFrames;
            if (round2 >= i3) {
                round2 = i3 - 1;
            }
            FileInputStream fileInputStream2 = null;
            try {
                fileOutputStream = new FileOutputStream(this.mOutputFile);
                try {
                    fileInputStream = new FileInputStream(this.mInputFile);
                    try {
                        byte[] bArr = new byte[100];
                        if (fileInputStream.read(bArr, 0, 6) > 0) {
                            fileOutputStream.write(bArr, 0, 6);
                        }
                        long skip = fileInputStream.skip((long) (this.mFrameOffset[0] - 6));
                        if (skip < 0) {
                            Log.m19d(TAG, "trim skip HEADER_SIZE : " + skip);
                        }
                        for (int i4 = 0; i4 < round; i4 += 4) {
                            if (fileInputStream.read(bArr, 0, this.mFrameLens[i4]) > 0) {
                                fileOutputStream.write(bArr, 0, this.mFrameLens[i4]);
                            }
                        }
                        Log.m26i(TAG, "delete - FrameOffsets : " + this.mFrameOffset.length + " mNumberOfFrames:" + this.mNumberOfFrames + " startFrame : " + round + " endFrame : " + round2);
                        long skip2 = fileInputStream.skip((long) (this.mFrameOffset[round2] - this.mFrameOffset[round]));
                        if (skip2 < 0) {
                            Log.m19d(TAG, "delete skip HEADER_SIZE : " + skip2);
                        }
                        while (round2 < this.mNumberOfFrames) {
                            if (fileInputStream.read(bArr, 0, this.mFrameLens[round2]) > 0) {
                                fileOutputStream.write(bArr, 0, this.mFrameLens[round2]);
                            }
                            round2 += 4;
                        }
                        closeQuietly(fileInputStream);
                        closeQuietly(fileOutputStream);
                        return true;
                    } catch (IOException e) {
                        e = e;
                        fileInputStream2 = fileInputStream;
                        Log.m24e(TAG, "trim IOException", (Throwable) e);
                        closeQuietly(fileInputStream2);
                        closeQuietly(fileOutputStream);
                        return false;
                    } catch (ArrayIndexOutOfBoundsException | NullPointerException e2) {
//                        e = e2;
                        fileInputStream2 = fileInputStream;
                        try {
//                            Log.m24e(TAG, "trim NullPointerException", e);
                            closeQuietly(fileInputStream2);
                            closeQuietly(fileOutputStream);
                            return false;
                        } catch (Throwable th) {
                            th = th;
                            fileInputStream = fileInputStream2;
                            closeQuietly(fileInputStream);
                            closeQuietly(fileOutputStream);
                            throw th;
                        }
                    } catch (Throwable th2) {
//                        th = th2;
                        closeQuietly(fileInputStream);
                        closeQuietly(fileOutputStream);
//                        throw th;
                    }
                } catch (IOException e3) {
//                    e = e3;
//                    Log.m24e(TAG, "trim IOException", (Throwable) e);
                    closeQuietly(fileInputStream2);
                    closeQuietly(fileOutputStream);
                    return false;
                } catch (ArrayIndexOutOfBoundsException | NullPointerException e4) {
//                    e = e4;
//                    Log.m24e(TAG, "trim NullPointerException", e);
                    closeQuietly(fileInputStream2);
                    closeQuietly(fileOutputStream);
                    return false;
                }
            } catch (IOException e5) {
//                e = e5;
                fileOutputStream = null;
//                Log.m24e(TAG, "trim IOException", (Throwable) e);
                closeQuietly(fileInputStream2);
                closeQuietly(fileOutputStream);
                return false;
            } catch (ArrayIndexOutOfBoundsException | NullPointerException e6) {
//                e = e6;
                fileOutputStream = null;
//                Log.m24e(TAG, "trim NullPointerException", e);
                closeQuietly(fileInputStream2);
                closeQuietly(fileOutputStream);
                return false;
            } catch (Throwable th3) {
//                th = th3;
                fileOutputStream = null;
                fileInputStream = null;
                closeQuietly(fileInputStream);
                closeQuietly(fileOutputStream);
//                throw th;
            }
        } catch (FileNotFoundException e7) {
            Log.m24e(TAG, "FileNotFoundException", (Throwable) e7);
            return false;
        } catch (IOException e8) {
            Log.m24e(TAG, "IOException", (Throwable) e8);
            return false;
        }
        return true;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v15, resolved type: java.io.File} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v16, resolved type: java.io.File} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v17, resolved type: java.io.File} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v18, resolved type: java.io.File} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v18, resolved type: java.io.FileInputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v21, resolved type: java.io.File} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v22, resolved type: java.io.File} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v24, resolved type: java.io.FileInputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v37, resolved type: java.io.File} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v38, resolved type: java.io.File} */
    /* JADX WARNING: type inference failed for: r15v3, types: [java.io.Closeable] */
    /* JADX WARNING: type inference failed for: r13v1, types: [java.io.Closeable] */
    /* JADX WARNING: type inference failed for: r15v9, types: [java.io.Closeable] */
    /* JADX WARNING: type inference failed for: r13v7, types: [java.io.Closeable] */
    /* JADX WARNING: type inference failed for: r13v10 */
    /* JADX WARNING: type inference failed for: r15v12 */
    /* JADX WARNING: type inference failed for: r13v17 */
    /* JADX WARNING: type inference failed for: r15v19 */
    /* JADX WARNING: type inference failed for: r15v23 */
    /* JADX WARNING: type inference failed for: r15v26 */
    /* JADX WARNING: type inference failed for: r13v20 */
    /* JADX WARNING: type inference failed for: r15v27 */
    /* JADX WARNING: type inference failed for: r13v21 */
    /* JADX WARNING: type inference failed for: r15v29 */
    /* JADX WARNING: type inference failed for: r13v23 */
    /* JADX WARNING: type inference failed for: r15v31 */
    /* JADX WARNING: type inference failed for: r13v25 */
    /* JADX WARNING: type inference failed for: r13v33 */
    /* JADX WARNING: type inference failed for: r13v37 */
    /* JADX WARNING: type inference failed for: r15v39 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 4 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean overwrite(java.lang.String r11, java.lang.String r12, java.lang.String r13, int r14, int r15) {
        /*
            r10 = this;
            java.lang.String r0 = "overwrite - startFrame : "
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "overwrite - originalPath : "
            r1.append(r2)
            r1.append(r11)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "AMREditor"
            com.sec.android.app.voicenote.provider.Log.m29v(r2, r1)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "overwrite - overwritePath : "
            r1.append(r3)
            r1.append(r12)
            java.lang.String r1 = r1.toString()
            com.sec.android.app.voicenote.provider.Log.m29v(r2, r1)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "overwrite - outputPath : "
            r1.append(r3)
            r1.append(r13)
            java.lang.String r1 = r1.toString()
            com.sec.android.app.voicenote.provider.Log.m29v(r2, r1)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "overwrite - fromTime : "
            r1.append(r3)
            r1.append(r14)
            java.lang.String r1 = r1.toString()
            com.sec.android.app.voicenote.provider.Log.m29v(r2, r1)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "overwrite - toTime : "
            r1.append(r3)
            r1.append(r15)
            java.lang.String r15 = r1.toString()
            com.sec.android.app.voicenote.provider.Log.m29v(r2, r15)
            java.io.File r15 = new java.io.File
            r15.<init>(r11)
            java.io.File r11 = new java.io.File
            r11.<init>(r12)
            r12 = 0
            r1 = 0
            com.sec.android.app.voicenote.service.helper.ExtractorAMR r3 = new com.sec.android.app.voicenote.service.helper.ExtractorAMR     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            r3.<init>()     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            r3.readFile(r15)     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            r10.setExtractor(r3)     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            r4.<init>()     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            java.lang.String r5 = "overwrite - original frame count : "
            r4.append(r5)     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            int r5 = r3.getNumFrames()     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            r4.append(r5)     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            java.lang.String r4 = r4.toString()     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            com.sec.android.app.voicenote.provider.Log.m29v(r2, r4)     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            com.sec.android.app.voicenote.service.helper.ExtractorAMR r4 = new com.sec.android.app.voicenote.service.helper.ExtractorAMR     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            r4.<init>()     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            r4.readFile(r11)     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            r5.<init>()     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            java.lang.String r6 = "overwrite - overwrite frame count : "
            r5.append(r6)     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            int r4 = r4.getNumFrames()     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            r5.append(r4)     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            java.lang.String r4 = r5.toString()     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            com.sec.android.app.voicenote.provider.Log.m29v(r2, r4)     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            float r14 = (float) r14     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            float r4 = r10.mTimePerFrame     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            float r14 = r14 / r4
            int r14 = java.lang.Math.round(r14)     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            r4 = 4194304(0x400000, float:5.877472E-39)
            byte[] r4 = new byte[r4]     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            r5.<init>()     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            r5.append(r0)     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            r5.append(r14)     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            java.lang.String r5 = r5.toString()     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            com.sec.android.app.voicenote.provider.Log.m29v(r2, r5)     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            if (r14 >= 0) goto L_0x00d8
            r14 = r1
        L_0x00d8:
            java.io.RandomAccessFile r5 = new java.io.RandomAccessFile     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            java.lang.String r6 = "rw"
            r5.<init>(r13, r6)     // Catch:{ FileNotFoundException -> 0x0215, IOException -> 0x020c, ArrayIndexOutOfBoundsException -> 0x01fa, all -> 0x01f5 }
            java.io.FileInputStream r13 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x01f0, IOException -> 0x01eb, ArrayIndexOutOfBoundsException -> 0x01e6, all -> 0x01e2 }
            r13.<init>(r15)     // Catch:{ FileNotFoundException -> 0x01f0, IOException -> 0x01eb, ArrayIndexOutOfBoundsException -> 0x01e6, all -> 0x01e2 }
            java.io.FileInputStream r15 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x01df, IOException -> 0x01dc, ArrayIndexOutOfBoundsException -> 0x01d9, all -> 0x01d5 }
            r15.<init>(r11)     // Catch:{ FileNotFoundException -> 0x01df, IOException -> 0x01dc, ArrayIndexOutOfBoundsException -> 0x01d9, all -> 0x01d5 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            r11.<init>()     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            r11.append(r0)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            r11.append(r14)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            java.lang.String r11 = r11.toString()     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            com.sec.android.app.voicenote.provider.Log.m29v(r2, r11)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            r11 = r1
        L_0x00fc:
            int r12 = r13.available()     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            if (r12 <= 0) goto L_0x010c
            int r11 = r13.read(r4)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            if (r11 <= 0) goto L_0x00fc
            r5.write(r4, r1, r11)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            goto L_0x00fc
        L_0x010c:
            int[] r12 = r10.mFrameOffset     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            int r12 = r12.length     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            if (r12 > r14) goto L_0x0130
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            r12.<init>()     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            java.lang.String r0 = "out of index exception - length : "
            r12.append(r0)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            int[] r0 = r10.mFrameOffset     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            int r0 = r0.length     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            r12.append(r0)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            java.lang.String r0 = " startFrame : "
            r12.append(r0)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            r12.append(r14)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            java.lang.String r12 = r12.toString()     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            com.sec.android.app.voicenote.provider.Log.m22e(r2, r12)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
        L_0x0130:
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            r12.<init>()     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            java.lang.String r0 = "overwrite -  overwrite position : "
            r12.append(r0)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            int[] r0 = r10.mFrameOffset     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            r0 = r0[r14]     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            r12.append(r0)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            java.lang.String r0 = ", "
            r12.append(r0)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            r12.append(r11)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            java.lang.String r12 = r12.toString()     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            com.sec.android.app.voicenote.provider.Log.m29v(r2, r12)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            int r12 = r3.getNumFrames()     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            if (r14 >= r12) goto L_0x0164
            java.lang.String r12 = "overwrite - seek to frame position"
            com.sec.android.app.voicenote.provider.Log.m29v(r2, r12)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            int[] r12 = r10.mFrameOffset     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            r12 = r12[r14]     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            long r6 = (long) r12     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            r5.seek(r6)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            goto L_0x0169
        L_0x0164:
            java.lang.String r12 = "overwrite - just add frame info"
            com.sec.android.app.voicenote.provider.Log.m29v(r2, r12)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
        L_0x0169:
            r6 = 6
            long r6 = r15.skip(r6)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            r8 = 0
            int r12 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r12 >= 0) goto L_0x0189
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            r12.<init>()     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            java.lang.String r14 = "overwrite - skip fail HEADER_SIZE : "
            r12.append(r14)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            r12.append(r6)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            java.lang.String r12 = r12.toString()     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            com.sec.android.app.voicenote.provider.Log.m22e(r2, r12)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
        L_0x0189:
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            r12.<init>()     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            java.lang.String r14 = "overwrite - skipped HEADER_SIZE : "
            r12.append(r14)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            r12.append(r6)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            java.lang.String r12 = r12.toString()     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            com.sec.android.app.voicenote.provider.Log.m29v(r2, r12)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
        L_0x019d:
            int r12 = r15.available()     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            if (r12 <= 0) goto L_0x01ad
            int r11 = r15.read(r4)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            if (r11 <= 0) goto L_0x019d
            r5.write(r4, r1, r11)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            goto L_0x019d
        L_0x01ad:
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            r12.<init>()     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            java.lang.String r14 = "overwrite - overwrite data size : "
            r12.append(r14)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            r12.append(r11)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            java.lang.String r11 = r12.toString()     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            com.sec.android.app.voicenote.provider.Log.m29v(r2, r11)     // Catch:{ FileNotFoundException -> 0x01d3, IOException -> 0x01d1, ArrayIndexOutOfBoundsException -> 0x01cf, all -> 0x01cc }
            r11 = 1
            r10.closeQuietly(r5)
            r10.closeQuietly(r13)
            r10.closeQuietly(r15)
            return r11
        L_0x01cc:
            r11 = move-exception
            goto L_0x0220
        L_0x01cf:
            r11 = move-exception
            goto L_0x01e9
        L_0x01d1:
            r11 = move-exception
            goto L_0x01ee
        L_0x01d3:
            r11 = move-exception
            goto L_0x01f3
        L_0x01d5:
            r11 = move-exception
            r15 = r12
            goto L_0x0220
        L_0x01d9:
            r11 = move-exception
            r15 = r12
            goto L_0x01e9
        L_0x01dc:
            r11 = move-exception
            r15 = r12
            goto L_0x01ee
        L_0x01df:
            r11 = move-exception
            r15 = r12
            goto L_0x01f3
        L_0x01e2:
            r11 = move-exception
            r13 = r12
            r15 = r13
            goto L_0x0220
        L_0x01e6:
            r11 = move-exception
            r13 = r12
            r15 = r13
        L_0x01e9:
            r12 = r5
            goto L_0x01fd
        L_0x01eb:
            r11 = move-exception
            r13 = r12
            r15 = r13
        L_0x01ee:
            r12 = r5
            goto L_0x020f
        L_0x01f0:
            r11 = move-exception
            r13 = r12
            r15 = r13
        L_0x01f3:
            r12 = r5
            goto L_0x0218
        L_0x01f5:
            r11 = move-exception
            r13 = r12
            r15 = r13
            r5 = r15
            goto L_0x0220
        L_0x01fa:
            r11 = move-exception
            r13 = r12
            r15 = r13
        L_0x01fd:
            java.lang.String r14 = "ArrayIndexOutOfBoundsException"
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r2, (java.lang.String) r14, (java.lang.Throwable) r11)     // Catch:{ all -> 0x021e }
        L_0x0202:
            r10.closeQuietly(r12)
            r10.closeQuietly(r13)
            r10.closeQuietly(r15)
            return r1
        L_0x020c:
            r11 = move-exception
            r13 = r12
            r15 = r13
        L_0x020f:
            java.lang.String r14 = "IOException"
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r2, (java.lang.String) r14, (java.lang.Throwable) r11)     // Catch:{ all -> 0x021e }
            goto L_0x0202
        L_0x0215:
            r11 = move-exception
            r13 = r12
            r15 = r13
        L_0x0218:
            java.lang.String r14 = "FileNotFoundException"
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r2, (java.lang.String) r14, (java.lang.Throwable) r11)     // Catch:{ all -> 0x021e }
            goto L_0x0202
        L_0x021e:
            r11 = move-exception
            r5 = r12
        L_0x0220:
            r10.closeQuietly(r5)
            r10.closeQuietly(r13)
            r10.closeQuietly(r15)
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.AMREditor.overwrite(java.lang.String, java.lang.String, java.lang.String, int, int):boolean");
    }

    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException unused) {
                Log.m22e(TAG, "closeQuietly fail : " + closeable.getClass().getSimpleName());
            }
        }
    }
}
