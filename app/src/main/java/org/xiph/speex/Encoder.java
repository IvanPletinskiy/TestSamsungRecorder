package org.xiph.speex;

public interface Encoder {
    int encode(Bits bits, float[] fArr);

    int getBitRate();

    float[] getExc();

    int getFrameSize();

    float[] getInnov();

    int getMode();

    float[] getPiGain();

    float getRelativeQuality();

    void setMode(int i);

    void setQuality(int i);

    void setVbr(boolean z);

    void setVbrQuality(float f);
}
