package com.samsung.vsf.audio;

public interface IAudioEncoder {
    void destroy();

    byte[] encodeAudio(short[] sArr);

    void init(AudioProcessorConfig audioProcessorConfig);
}
