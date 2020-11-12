package com.sec.android.app.voicenote.common.util;

public class Degree {
    private float mDegree;

    public Degree(float f) {
        this.mDegree = f;
    }

    public float getValue() {
        return this.mDegree;
    }

    public Degree add(float f) {
        this.mDegree += f;
        float f2 = this.mDegree;
        if (((double) f2) > 360.0d) {
            this.mDegree = (float) (((double) f2) - 360.0d);
        }
        float f3 = this.mDegree;
        if (((double) f3) < 0.0d) {
            this.mDegree = (float) (((double) f3) + 360.0d);
        }
        return this;
    }

    public Degree difference(Degree degree) {
        float value = degree.getValue() - this.mDegree;
        double d = (double) value;
        if (d < -180.0d) {
            value = (float) (d + 360.0d);
        }
        double d2 = (double) value;
        if (d2 > 180.0d) {
            value = (float) (d2 - 360.0d);
        }
        return new Degree(value);
    }
}
