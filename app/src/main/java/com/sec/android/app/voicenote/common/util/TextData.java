package com.sec.android.app.voicenote.common.util;

import androidx.annotation.NonNull;
import java.io.Serializable;
import java.util.Arrays;

public class TextData implements Comparable<TextData>, Serializable, Cloneable {
    private static final long serialVersionUID = -7074386552926531709L;
    public double ConfidenceScore;
    public int dataType;
    public long duration;
    public long elapsedTime;
    public String[] mText;
    public long timeStamp;

    public TextData() {
        this.dataType = 0;
        this.mText = new String[10];
    }

    public TextData(TextData textData) {
        this.dataType = 0;
        int length = textData.mText.length;
        this.mText = new String[length];
        System.arraycopy(textData.mText, 0, this.mText, 0, length);
        this.ConfidenceScore = textData.ConfidenceScore;
        this.timeStamp = textData.timeStamp;
        this.elapsedTime = textData.elapsedTime;
    }

    public int compareTo(@NonNull TextData textData) {
        return this.timeStamp > textData.timeStamp ? 1 : -1;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof TextData)) {
            return false;
        }
        TextData textData = (TextData) obj;
        if (Arrays.equals(this.mText, textData.mText) && this.ConfidenceScore == textData.ConfidenceScore && this.timeStamp == textData.timeStamp && this.elapsedTime == textData.elapsedTime) {
            return true;
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
