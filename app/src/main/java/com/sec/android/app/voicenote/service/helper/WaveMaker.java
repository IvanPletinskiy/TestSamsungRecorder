package com.sec.android.app.voicenote.service.helper;

import android.os.AsyncTask;

import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.StorageProvider;
import com.sec.android.app.voicenote.service.MetadataRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class WaveMaker {
    private static final String TAG = "WaveMaker";
    /* access modifiers changed from: private */
    public float mDuration = 0.0f;
    private Extractor.OnExtractListener mExtractListener = new Extractor.OnExtractListener() {
        public final void onFinished(int[] iArr, int i) {
            WaveMaker.this.lambda$new$0$WaveMaker(iArr, i);
        }
    };
    /* access modifiers changed from: private */
    public OnWaveMakerListener mListener = null;
    /* access modifiers changed from: private */
    public int[] mNewWaveBuffer = null;
    /* access modifiers changed from: private */
    public String mPath = null;
    /* access modifiers changed from: private */
    public int[] mSampleBuffer = null;
    /* access modifiers changed from: private */
    public int mSampleSize = 0;
    private UpdateAmplitudeTask mUpdateAmplitudeTask = null;

    public interface OnWaveMakerListener {
        void onFinished(String str, int[] iArr);
    }

    public /* synthetic */ void lambda$new$0$WaveMaker(int[] iArr, int i) {
        Log.m19d(TAG, "OnExtractListener bufferSize:" + i + " duration:" + this.mDuration);
        this.mSampleBuffer = iArr;
        this.mSampleSize = i;
        UpdateAmplitudeTask updateAmplitudeTask = this.mUpdateAmplitudeTask;
        if (updateAmplitudeTask != null && updateAmplitudeTask.getStatus() == AsyncTask.Status.RUNNING) {
            this.mUpdateAmplitudeTask.cancel(false);
        }
        this.mUpdateAmplitudeTask = new UpdateAmplitudeTask();
        this.mUpdateAmplitudeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public void registerListener(OnWaveMakerListener onWaveMakerListener) {
        this.mListener = onWaveMakerListener;
    }

    public void setDuration(float f) {
        this.mDuration = f;
    }

    public float getDuration() {
        return this.mDuration;
    }

    public void decode(String str) {
        if (str == null) {
            Log.m22e(TAG, "decode filepath is NULL !!");
            return;
        }
        Log.m19d(TAG, "decode ");
        this.mPath = str;
        Extractor extractor = new Extractor();
        extractor.setOnExtractListener(this.mExtractListener);
        extractor.startExtract(this.mPath);
    }

    public void updateAmpTask(String str) {
        new UpdateWaveDataTask().execute(new String[]{str});
    }

    private class UpdateAmplitudeTask extends AsyncTask<Void, Integer, Void> {
        private UpdateAmplitudeTask() {
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... voidArr) {
            int access$100 = (int) (WaveMaker.this.mDuration / 35.0f);
            float access$200 = (((float) WaveMaker.this.mSampleSize) * 1.0f) / ((float) access$100);
            Log.m19d(WaveMaker.TAG, "UpdateAmplitudeTask newSize:" + access$100);
            int[] unused = WaveMaker.this.mNewWaveBuffer = new int[access$100];
            Arrays.fill(WaveMaker.this.mNewWaveBuffer, 0);
            int i = 0;
            while (i < access$100) {
                int i2 = (int) (((float) i) * access$200);
                int i3 = i + 1;
                int i4 = (int) (((float) i3) * access$200);
                if (i4 > WaveMaker.this.mSampleSize - 1) {
                    i4 = WaveMaker.this.mSampleSize - 1;
                }
                int i5 = 0;
                for (int i6 = i2; i6 <= i4; i6++) {
                    i5 += WaveMaker.this.mSampleBuffer[i6];
                }
                WaveMaker.this.mNewWaveBuffer[i] = i5 == 0 ? 0 : i5 / ((i4 - i2) + 1);
                if (isCancelled()) {
                    Log.m29v(WaveMaker.TAG, "UpdateAmplitudeTask is canceled");
                    return null;
                }
                i = i3;
            }
            return null;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Void voidR) {
            super.onPostExecute(voidR);
            if (WaveMaker.this.mNewWaveBuffer == null || WaveMaker.this.mNewWaveBuffer.length == 0) {
                int[] unused = WaveMaker.this.mNewWaveBuffer = new int[1];
            }
            WaveMaker.this.mNewWaveBuffer[0] = 2;
            WaveMaker.this.mListener.onFinished(WaveMaker.this.mPath, WaveMaker.this.mNewWaveBuffer);
            OnWaveMakerListener unused2 = WaveMaker.this.mListener = null;
        }
    }

    private static class UpdateWaveDataTask extends AsyncTask<String, Integer, Void> {
        String amplitudeTemp;
        BufferedReader bRe;
        FileReader fRe;
        File file;

        private UpdateWaveDataTask() {
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(String... strArr) {
            int[] iArr;
            this.file = new File(StorageProvider.getTempWavePath());
            if (!this.file.exists()) {
                return null;
            }
            String str = strArr[0];
            try {
                this.fRe = new FileReader(this.file);
                this.bRe = new BufferedReader(this.fRe);
                this.amplitudeTemp = this.bRe.readLine();
                if (this.amplitudeTemp != null) {
                    String[] split = this.amplitudeTemp.split(" ");
                    iArr = new int[(split.length + 1)];
                    int i = 0;
                    while (i < split.length) {
                        try {
                            int i2 = i + 1;
                            iArr[i2] = Integer.parseInt(split[i]);
                            i = i2;
                        }  catch (NumberFormatException e2) {
//                            e = e2;
//                            Log.m22e(WaveMaker.TAG, e.getMessage());
                            iArr = new int[1];
                            iArr[0] = 2;
                            MetadataRepository instance2 = MetadataRepository.getInstance();
                            instance2.setWaveData(iArr);
                            instance2.write(str);
                            instance2.resetWaveData();
                            return null;
                        }
                    }
                } else {
                    iArr = null;
                }
                this.fRe.close();
                this.bRe.close();
            } catch (IOException e3) {
//                e = e3;
                iArr = null;
//                Log.m22e(WaveMaker.TAG, e.getMessage());
                iArr = new int[1];
                iArr[0] = 2;
                MetadataRepository instance22 = MetadataRepository.getInstance();
                instance22.setWaveData(iArr);
                instance22.write(str);
                instance22.resetWaveData();
                return null;
            } catch (NumberFormatException e4) {
//                e = e4;
                iArr = null;
//                Log.m22e(WaveMaker.TAG, e.getMessage());
                iArr = new int[1];
                iArr[0] = 2;
                MetadataRepository instance222 = MetadataRepository.getInstance();
                instance222.setWaveData(iArr);
                instance222.write(str);
                instance222.resetWaveData();
                return null;
            }
            if (iArr == null || iArr.length == 0) {
                iArr = new int[1];
            }
            iArr[0] = 2;
            MetadataRepository instance2222 = MetadataRepository.getInstance();
            instance2222.setWaveData(iArr);
            instance2222.write(str);
            instance2222.resetWaveData();
            return null;
        }
    }
}
