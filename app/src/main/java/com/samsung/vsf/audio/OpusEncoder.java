package com.samsung.vsf.audio;

import com.samsung.voiceserviceplatform.voiceserviceframework.OpusJNI;
import com.samsung.vsf.util.SVoiceLog;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public class OpusEncoder implements IAudioEncoder {
    private static OpusEncoder mOpusEncoderInstance;
    private OpusJNI mOpusEncoder;
    private long mOpusEncoderId;

    public static synchronized OpusEncoder getInstance() {
        synchronized (OpusEncoder.class) {
            SVoiceLog.debug("OpusEncoder", "inside getInstance");
            if (mOpusEncoderInstance == null) {
                OpusEncoder opusEncoder = new OpusEncoder();
                return opusEncoder;
            }
            OpusEncoder opusEncoder2 = mOpusEncoderInstance;
            return opusEncoder2;
        }
    }

    public synchronized void init(AudioProcessorConfig audioProcessorConfig) {
        int samplingRate = audioProcessorConfig.getSamplingRate();
        SVoiceLog.debug("OpusEncoder", "initOpusEncoder");
        this.mOpusEncoder = new OpusJNI();
        this.mOpusEncoderId = this.mOpusEncoder.encoder_init(samplingRate, 24000, 1, 10, 1);
        SVoiceLog.debug("OpusEncoder", "The opus encoder id is " + this.mOpusEncoderId);
    }

    public byte[] encodeAudio(short[] sArr) {
        byte[] bArr = new byte[640];
        if (sArr == null) {
            return null;
        }
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(sArr.length * 2);
        ShortBuffer wrap = ShortBuffer.wrap(sArr);
        int length = sArr.length / 320;
        if (sArr.length % 320 > 0) {
            length++;
        }
        for (int i = 0; i < length; i++) {
            short[] sArr2 = new short[320];
            if (i != length - 1) {
                wrap.get(sArr2);
            } else {
                wrap.get(sArr2, 0, wrap.remaining());
            }
            int encoder_process = this.mOpusEncoder.encoder_process(sArr2, bArr, this.mOpusEncoderId);
            byte[] bArr2 = new byte[2];
            String hexString = Integer.toHexString(encoder_process);
            if (hexString.length() < 3) {
                bArr2[0] = 0;
                bArr2[1] = Integer.valueOf(hexString, 16).byteValue();
            } else {
                bArr2[0] = Integer.valueOf(hexString.substring(0, hexString.length() - 2), 16).byteValue();
                bArr2[1] = Integer.valueOf(hexString.substring(hexString.length() - 2, hexString.length()), 16).byteValue();
            }
            allocateDirect.put(bArr2);
            allocateDirect.put(bArr, 0, encoder_process);
        }
        byte[] bArr3 = new byte[allocateDirect.position()];
        allocateDirect.position(0);
        allocateDirect.get(bArr3);
        allocateDirect.clear();
        return bArr3;
    }

    public void destroy() {
        SVoiceLog.debug("OpusEncoder", "inside destroy");
        this.mOpusEncoder.encoder_destroy(this.mOpusEncoderId);
        this.mOpusEncoderId = 0;
    }
}
