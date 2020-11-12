package com.sec.android.app.voicenote.service;

import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.StorageProvider;

public class AudioFormat {
    private static final int ENCODING_BPS_128 = 128000;
    private static final int ENCODING_BPS_256 = 256000;
    private static final int ENCODING_BPS_32 = 32000;
    private static final int ENCODING_BPS_64 = 64000;
    private static final int ENCODING_BPS_FOR_AMR = 12200;
    public static final long MAX_DURATION = 36000999;
    public static final long MAX_DURATION_VOICEMEMO = 600000;
    public static final long MAX_DURATION_VOICEMEMO_ERROR = 50;
    private static final int SAMPLING_RATE_24000 = 24000;
    private static final int SAMPLING_RATE_44100 = 44100;
    private static final int SAMPLING_RATE_48000 = 48000;
    private static final String TAG = "AudioFormat";
    private boolean mBluetoothSco;
    private long mMaxDuration;
    private long mMaxFileSize;
    private String mMimeType;
    private int mRecordingQuality;

    public static class ExtType {
        public static final String EXT_3GA = ".3ga";
        public static final String EXT_AMR = ".amr";
        public static final String EXT_M4A = ".m4a";
    }

    public static class MimeType {
        public static final String AMR = "audio/amr";
        public static final String MP4 = "audio/mp4";
    }

    public static String getMineType(int i) {
        return (i == 5 || i == 6) ? MimeType.AMR : MimeType.MP4;
    }

    public AudioFormat(int i) {
        this.mMimeType = MimeType.MP4;
        this.mBluetoothSco = false;
        this.mBluetoothSco = false;
        init(i);
    }

    public AudioFormat(int i, boolean z) {
        this.mMimeType = MimeType.MP4;
        this.mBluetoothSco = false;
        this.mBluetoothSco = z;
        init(i);
    }

    private void init(int i) {
        this.mMimeType = getMineType(i);
        if (i == 4) {
            this.mMaxDuration = MAX_DURATION_VOICEMEMO;
        } else if (i == 5) {
            Log.m22e(TAG, "Use other constructor for Attach mode. We can not set max file size");
        } else if (i != 6) {
            this.mMaxDuration = MAX_DURATION;
        } else {
            this.mMaxDuration = getDurationBySize(this.mMimeType, Settings.getMmsMaxSize());
        }
    }

    public AudioFormat(String str, long j) {
        this.mMimeType = MimeType.MP4;
        this.mBluetoothSco = false;
        if (str != null) {
            this.mMimeType = str;
        }
        if (j != 0) {
            this.mMaxFileSize = j;
            this.mMaxDuration = getDurationBySize(this.mMimeType, j);
        }
    }

    public int getAudioEncoder() {
        return MimeType.AMR.equals(this.mMimeType) ? 1 : 3;
    }

    public int getAudioEncodingBitrate() {
        if (MimeType.AMR.equals(this.mMimeType)) {
            return ENCODING_BPS_FOR_AMR;
        }
        int i = this.mRecordingQuality;
        if (i != 0) {
            return (i == 1 || i != 2) ? ENCODING_BPS_128 : ENCODING_BPS_256;
        }
        return ENCODING_BPS_64;
    }

    public int getAudioSamplingRate() {
        if (MimeType.AMR.equals(this.mMimeType)) {
            return 8000;
        }
        return this.mRecordingQuality != 2 ? SAMPLING_RATE_44100 : SAMPLING_RATE_48000;
    }

    public static long getDurationBySize(String str, long j) {
        return (long) ((((((double) j) / 1024.0d) * 8.0d) / ((double) (!MimeType.AMR.equals(str) ? 128.0f : 12.51f))) * 1000.0d);
    }

    public String getExtension() {
        return getExtension(this.mMimeType);
    }

    public static String getExtension(String str) {
        return MimeType.AMR.equals(str) ? ExtType.EXT_AMR : ExtType.EXT_M4A;
    }

    public int getMaxDuration() {
        return (int) this.mMaxDuration;
    }

    public long getMaxFileSize(String str) {
        if (MimeType.AMR.equals(this.mMimeType)) {
            return this.mMaxFileSize;
        }
        return StorageProvider.getAvailableStorage(str);
    }

    public void setMimeType(String str) {
        this.mMimeType = str;
    }

    public String getMimeType() {
        return this.mMimeType;
    }

    public int getOutputFormat() {
        return MimeType.AMR.equals(this.mMimeType) ? 3 : 1;
    }

    public void setRecordingQuality(int i) {
        this.mRecordingQuality = i;
    }

    public boolean isBluetoothSco() {
        return this.mBluetoothSco;
    }
}
