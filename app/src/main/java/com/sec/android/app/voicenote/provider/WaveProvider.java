package com.sec.android.app.voicenote.provider;

import android.content.Context;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.p007ui.view.WaveView;

public class WaveProvider {
    public static int AMPLITUDE_SPACE = 0;
    public static int AMPLITUDE_STROKE_WIDTH = 0;
    public static int AMPLITUDE_TOTAL_WIDTH = 0;
    public static final int DURATION_INTERVAL = 70;
    public static float DURATION_PER_HAFT_OF_WAVE_AREA = 0.0f;
    public static int DURATION_PER_WAVEVIEW = 0;
    public static float MS_PER_PX = 0.0f;
    public static int NUM_OF_AMPLITUDE = 0;
    public static float PX_PER_MS = 0.0f;
    public static float SIMPLE_DURATION_PER_HAFT_OF_WAVE_AREA = 0.0f;
    public static int SIMPLE_START_RECORD_MARGIN = 0;
    public static int SIMPLE_WAVE_AREA_WIDTH = 0;
    public static int SIMPLE_WAVE_HEIGHT = 0;
    public static int SIMPLE_WAVE_VIEW_HEIGHT = 0;
    public static int START_RECORD_MARGIN = 0;
    private static final String TAG = "WaveProvider";
    public static final int VERSION_1 = 0;
    public static final int VERSION_2 = 2;
    public static int WAVE_AREA_WIDTH = 0;
    public static int WAVE_HEIGHT = 0;
    public static int WAVE_THICKNESS = 4;
    public static int WAVE_VIEW_HEIGHT;
    public static int WAVE_VIEW_WIDTH;
    private static WaveProvider mInstance;
    private Context mAppContext = null;

    public static WaveProvider getInstance() {
        if (mInstance == null) {
            mInstance = new WaveProvider();
        }
        return mInstance;
    }

    private WaveProvider() {
    }

    public void init() {
        AMPLITUDE_STROKE_WIDTH = this.mAppContext.getResources().getDimensionPixelSize(C0690R.dimen.wave_view_stroke);
        int i = AMPLITUDE_STROKE_WIDTH;
        AMPLITUDE_SPACE = i * 2;
        AMPLITUDE_TOTAL_WIDTH = AMPLITUDE_SPACE + i;
        WAVE_VIEW_WIDTH = initWaveViewWidth();
        int i2 = WAVE_VIEW_WIDTH;
        int i3 = AMPLITUDE_TOTAL_WIDTH;
        NUM_OF_AMPLITUDE = i2 / i3;
        int i4 = NUM_OF_AMPLITUDE;
        if (i3 * i4 != i2) {
            NUM_OF_AMPLITUDE = i4 + 1;
            while (true) {
                int i5 = AMPLITUDE_TOTAL_WIDTH * NUM_OF_AMPLITUDE;
                int i6 = WAVE_VIEW_WIDTH;
                if (i5 == i6) {
                    break;
                }
                WAVE_VIEW_WIDTH = i6 + 1;
            }
        }
        int i7 = AMPLITUDE_TOTAL_WIDTH;
        PX_PER_MS = (((float) i7) * 1.0f) / 70.0f;
        MS_PER_PX = 70.0f / ((float) i7);
        DURATION_PER_WAVEVIEW = NUM_OF_AMPLITUDE * 70;
        Log.m26i(TAG, " - AMPLITUDE_STROKE_WIDTH : " + AMPLITUDE_STROKE_WIDTH);
        Log.m26i(TAG, " - AMPLITUDE_TOTAL_WIDTH : " + AMPLITUDE_TOTAL_WIDTH);
        Log.m26i(TAG, " - NUM_OF_AMPLITUDE_PER_WAVE_VIEW : " + NUM_OF_AMPLITUDE);
        Log.m26i(TAG, " - WAVE_VIEW_WIDTH : " + WAVE_VIEW_WIDTH);
        Log.m26i(TAG, " - DURATION_PER_WAVE_VIEW : " + DURATION_PER_WAVEVIEW);
        Log.m26i(TAG, " - PX_PER_MS : " + PX_PER_MS);
        Log.m26i(TAG, " - MS_PER_PX : " + MS_PER_PX);
    }

    public void setWaveAreaWidth(int i, boolean z) {
        Log.m26i(TAG, " - WAVE_AREA_WIDTH: " + i);
        if (z) {
            SIMPLE_WAVE_AREA_WIDTH = i;
            SIMPLE_DURATION_PER_HAFT_OF_WAVE_AREA = ((((float) (DURATION_PER_WAVEVIEW * SIMPLE_WAVE_AREA_WIDTH)) * 1.0f) / 2.0f) / ((float) WAVE_VIEW_WIDTH);
            SIMPLE_START_RECORD_MARGIN = (int) (SIMPLE_DURATION_PER_HAFT_OF_WAVE_AREA * 0.8f);
        } else {
            WAVE_AREA_WIDTH = i;
            DURATION_PER_HAFT_OF_WAVE_AREA = ((((float) (DURATION_PER_WAVEVIEW * WAVE_AREA_WIDTH)) * 1.0f) / 2.0f) / ((float) WAVE_VIEW_WIDTH);
            START_RECORD_MARGIN = (int) (DURATION_PER_HAFT_OF_WAVE_AREA * 0.8f);
        }
        WaveView.updateWaveAttributes();
    }

    public void setApplicationContext(Context context) {
        this.mAppContext = context;
    }

    public float getWaveViewWidthDimension() {
        return (float) WAVE_VIEW_WIDTH;
    }

    public void setWaveHeight(int i, int i2, boolean z) {
        if (z) {
            SIMPLE_WAVE_HEIGHT = i;
            SIMPLE_WAVE_VIEW_HEIGHT = i2;
            return;
        }
        WAVE_HEIGHT = i;
        WAVE_VIEW_HEIGHT = i2;
    }

    private int initWaveViewWidth() {
        int fullScreenWidth = DisplayManager.getFullScreenWidth();
        int fullScreenHeight = DisplayManager.getFullScreenHeight();
        return fullScreenWidth > fullScreenHeight ? fullScreenWidth / 2 : fullScreenHeight / 2;
    }
}
