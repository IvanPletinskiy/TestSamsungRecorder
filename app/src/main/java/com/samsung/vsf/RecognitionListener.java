package com.samsung.vsf;

import android.os.Bundle;
import java.util.Properties;

public interface RecognitionListener {
    void onBeginningOfSpeech();

    void onBufferReceived(short[] sArr);

    void onEndOfSpeech();

    void onError(String str);

    void onErrorString(String str);

    void onPartialResults(Properties properties);

    void onReadyForSpeech(Bundle bundle);

    void onResults(Properties properties);

    void onRmsChanged(float f);
}
