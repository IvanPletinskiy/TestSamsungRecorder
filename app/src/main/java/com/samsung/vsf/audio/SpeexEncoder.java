package com.samsung.vsf.audio;

import android.util.Log;
import com.samsung.voiceserviceplatform.voiceserviceframework.SpeexBits;
import com.samsung.voiceserviceplatform.voiceserviceframework.SpeexJNI;
import com.samsung.vsf.recognition.RecognizerConstants;
import com.samsung.vsf.util.SVoiceLog;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public class SpeexEncoder implements IAudioEncoder {
    private static SpeexEncoder mSpeexEncoderInstance;
    private SpeexBits bits;
    private long mEncoderId;
    private int mFrameSize;
    private SpeexJNI mSpeexEncoder;
    private org.xiph.speex.SpeexEncoder spx_enc = null;

    public void init(AudioProcessorConfig audioProcessorConfig) {
        int samplingRate = audioProcessorConfig.getSamplingRate();
        if (RecognizerConstants.useJSpeexEncoder) {
            initJspeexEncoder(samplingRate);
        } else {
            initNativeSpeex(samplingRate);
        }
    }

    public byte[] encodeAudio(short[] sArr) {
        ByteBuffer allocate = ByteBuffer.allocate(sArr.length * 2);
        if (!RecognizerConstants.useJSpeexEncoder) {
            return encodeSpeex(sArr);
        }
        if (allocate == null) {
            return null;
        }
        allocate.order(ByteOrder.nativeOrder());
        for (short putShort : sArr) {
            allocate.putShort(putShort);
        }
        return encodeSpeex(allocate.array());
    }

    private synchronized boolean initJspeexEncoder(int i) {
        SVoiceLog.debug("SpeexEncoder", "initJSpeex");
        this.spx_enc = new org.xiph.speex.SpeexEncoder();
        if (i > 8000) {
            this.spx_enc.init(1, 10, i, 1);
        } else {
            this.spx_enc.init(0, 10, i, 1);
        }
        this.spx_enc.getEncoder().setVbr(true);
        this.spx_enc.getEncoder().setVbrQuality(10.0f);
        this.mFrameSize = this.spx_enc.getFrameSize();
        return true;
    }

    private synchronized boolean initNativeSpeex(int i) {
        SVoiceLog.debug("SpeexEncoder", "initNativeSpeex ");
        this.bits = new SpeexBits();
        this.mSpeexEncoder = new SpeexJNI();
        this.mEncoderId = 0;
        this.mEncoderId = this.mSpeexEncoder.speex_encoder_init(i);
        this.mFrameSize = this.mSpeexEncoder.speex_encoder_ctl(this.mEncoderId, 3, 0L);
        SVoiceLog.debug("SpeexEncoder", "Speex EncoderFactory frame size is " + this.mFrameSize + " AND encoderId is : " + this.mEncoderId);
        return this.mEncoderId != 0;
    }

    private byte[] encodeSpeex(short[] sArr) {
        byte[] bArr;
        short[] sArr2 = sArr;
        Log.d("SpeexEncoder", "Inside native encode speex");
        int i = this.mFrameSize;
        if (sArr2 == null) {
            return null;
        }
        try {
            ByteBuffer allocateDirect = ByteBuffer.allocateDirect(sArr2.length * 2);
            ShortBuffer wrap = ShortBuffer.wrap(sArr);
            int length = sArr2.length / i;
            if (sArr2.length % i > 0) {
                length++;
            }
            for (int i2 = 0; i2 < length; i2++) {
                short[] sArr3 = new short[i];
                if (i2 != length - 1) {
                    wrap.get(sArr3);
                } else {
                    wrap.get(sArr3, 0, wrap.remaining());
                }
                this.mSpeexEncoder.speex_bits_reset(this.bits);
                this.mSpeexEncoder.speex_encode_int(this.mEncoderId, sArr3, this.bits);
                byte[] bArr2 = new byte[(i * 2)];
                int speex_bits_write = this.mSpeexEncoder.speex_bits_write(this.bits, bArr2, bArr2.length);
                byte[] bArr3 = new byte[2];
                String hexString = Integer.toHexString(speex_bits_write);
                if (hexString.length() < 3) {
                    bArr3[0] = 0;
                    bArr3[1] = Integer.valueOf(hexString, 16).byteValue();
                } else {
                    bArr3[0] = Integer.valueOf(hexString.substring(0, hexString.length() - 2), 16).byteValue();
                    bArr3[1] = Integer.valueOf(hexString.substring(hexString.length() - 2, hexString.length()), 16).byteValue();
                }
                allocateDirect.put(bArr3);
                allocateDirect.put(bArr2, 0, speex_bits_write);
            }
            bArr = new byte[allocateDirect.position()];
            try {
                allocateDirect.position(0);
                allocateDirect.get(bArr);
                allocateDirect.clear();
                return bArr;
            } catch (Exception e) {
                e = e;
            }
        } catch (Exception e2) {
//            e = e2;
            bArr = null;
//            Log.e("SpeexEncoder", "Error occured during encoding");
//            e.printStackTrace();
            return bArr;
        }
        return null;
    }

    private byte[] encodeSpeex(byte[] bArr) {
        int i = this.mFrameSize * 2;
        if (bArr == null) {
            return null;
        }
        try {
            ByteBuffer allocateDirect = ByteBuffer.allocateDirect(bArr.length);
            ByteBuffer allocateDirect2 = ByteBuffer.allocateDirect(bArr.length);
            allocateDirect.put(bArr);
            allocateDirect.position(0);
            int length = bArr.length / i;
            if (bArr.length % i > 0) {
                length++;
            }
            for (int i2 = 0; i2 < length; i2++) {
                byte[] bArr2 = new byte[i];
                if (i2 != length - 1) {
                    allocateDirect.get(bArr2);
                } else {
                    allocateDirect.get(bArr2, 0, allocateDirect.remaining());
                }
                this.spx_enc.processData(bArr2, 0, bArr2.length);
                byte[] bArr3 = new byte[(this.spx_enc.getProcessedDataByteSize() + 1)];
                bArr3[0] = (byte) this.spx_enc.getProcessedData(bArr3, 1);
                allocateDirect2.put(bArr3, 0, bArr3.length);
            }
            byte[] bArr4 = new byte[allocateDirect2.position()];
            allocateDirect2.position(0);
            allocateDirect2.get(bArr4);
            allocateDirect2.clear();
            allocateDirect.clear();
            return bArr4;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void destroy() {
        SVoiceLog.debug("SpeexEncoder", "inside destroy");
        this.spx_enc = null;
        if (this.mSpeexEncoder != null) {
            SVoiceLog.debug("SpeexEncoder", "shutdown : " + this.mEncoderId);
            this.mSpeexEncoder.speex_bits_destroy(this.bits);
            this.mSpeexEncoder.speex_encoder_destroy(this.mEncoderId);
            this.mSpeexEncoder = null;
            this.mEncoderId = 0;
        }
        this.bits = null;
    }

    public static SpeexEncoder getInstance() {
        SVoiceLog.debug("SpeexEncoder", "inside getInstance");
        SpeexEncoder speexEncoder = mSpeexEncoderInstance;
        return speexEncoder == null ? new SpeexEncoder() : speexEncoder;
    }
}
