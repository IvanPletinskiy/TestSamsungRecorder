package com.sec.android.app.voicenote.common.util;

import android.os.Parcel;
import android.os.Parcelable;

public class DegreeBooleanData implements Parcelable {
    public static final Parcelable.Creator<DegreeBooleanData> CREATOR = new Parcelable.Creator<DegreeBooleanData>() {
        public DegreeBooleanData createFromParcel(Parcel parcel) {
            return new DegreeBooleanData(parcel);
        }

        public DegreeBooleanData[] newArray(int i) {
            return new DegreeBooleanData[i];
        }
    };
    public boolean bPlaying;
    public float degree;

    public int describeContents() {
        return 0;
    }

    public DegreeBooleanData(float f, boolean z) {
        this.degree = f;
        this.bPlaying = z;
    }

    public DegreeBooleanData(Parcel parcel) {
        readFromParcel(parcel);
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(this.degree);
        parcel.writeByte(this.bPlaying ? (byte) 1 : 0);
    }

    private void readFromParcel(Parcel parcel) {
        this.degree = parcel.readFloat();
        this.bPlaying = parcel.readByte() != 0;
    }
}
