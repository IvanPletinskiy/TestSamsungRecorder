package com.samsung.vsf.audio;

public class EncoderFactory {
    public static IAudioEncoder getEncoderInstance(AudioProcessorConfig audioProcessorConfig) {
        if (audioProcessorConfig.getEncodingType() == 1) {
            return OpusEncoder.getInstance();
        }
        return SpeexEncoder.getInstance();
    }
}
