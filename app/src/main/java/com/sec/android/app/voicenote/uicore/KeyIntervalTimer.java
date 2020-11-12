package com.sec.android.app.voicenote.uicore;

import com.sec.android.app.voicenote.provider.Log;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class KeyIntervalTimer {
    private static final String TAG = "KeyIntervalTimer";
    private keyCallback mCallBack;
    private long mInterval;
    private Vector<Integer> mPressedKeys = new Vector<>();
    private Timer mTimer = null;

    public interface keyCallback {
        void onTick(int i);
    }

    public KeyIntervalTimer(long j, keyCallback keycallback) {
        this.mInterval = j;
        this.mCallBack = keycallback;
    }

    public void setDownKey(int i) {
        if (!this.mPressedKeys.contains(Integer.valueOf(i))) {
            Log.m19d(TAG, "setDownKey : " + i);
            this.mPressedKeys.add(Integer.valueOf(i));
            if (this.mPressedKeys.size() == 1) {
                startTimer();
            } else {
                stopTimer();
            }
        }
    }

    public void setUpKey(int i) {
        if (this.mPressedKeys.contains(Integer.valueOf(i))) {
            Log.m19d(TAG, "setUpKey : " + i);
            Vector<Integer> vector = this.mPressedKeys;
            vector.remove(vector.indexOf(Integer.valueOf(i)));
            if (this.mPressedKeys.size() == 1) {
                startTimer();
            } else {
                stopTimer();
            }
        }
    }

    private void startTimer() {
        Log.m19d(TAG, "startTimer : " + this.mInterval);
        if (this.mTimer != null) {
            stopTimer();
        }
        this.mTimer = new Timer();
        this.mTimer.schedule(new TimerTask() {
            public void run() {
                KeyIntervalTimer.this.report();
            }
        }, 0, this.mInterval);
    }

    private void stopTimer() {
        Log.m19d(TAG, "stopTimer");
        Timer timer = this.mTimer;
        if (timer != null) {
            timer.cancel();
            this.mTimer = null;
        }
    }

    /* access modifiers changed from: private */
    public void report() {
        Log.m19d(TAG, "report : " + this.mPressedKeys.size());
        if (this.mPressedKeys.size() == 1) {
            try {
                this.mCallBack.onTick(this.mPressedKeys.get(0).intValue());
            } catch (Exception e) {
                Log.m22e(TAG, e.toString());
            }
        }
    }
}
