package com.sec.android.app.voicenote.service;

import android.content.Context;
import com.sec.android.app.voicenote.provider.Log;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleEngineManager {
    public static final int ENGINEFOCUS_GAIN = 1;
    public static final int ENGINEFOCUS_LOSS = -1;
    public static final int ENGINEFOCUS_NONE = 0;
    public static final int ENGINEFOCUS_REQUEST_FAILED = 0;
    public static final int ENGINEFOCUS_REQUEST_GRANTED = 1;
    private static final String TAG = "SimpleEngineManager";
    private static volatile SimpleEngineManager mInstance;
    private Map<String, SimpleEngine> engineMap = new ConcurrentHashMap();
    private String mActiveSession = null;
    private Context mAppContext = null;
    private Map<String, WeakReference<OnEngineFocusChangeListener>> mEngineFocusListener = new ConcurrentHashMap();
    boolean mIsAllRecordingStopped = true;
    private boolean mIsWiredHeadSetConnected;

    public interface OnEngineFocusChangeListener {
        void onEngineFocusChange(int i);
    }

    private SimpleEngineManager() {
        Log.m19d(TAG, "SimpleEngineManager creator !!");
    }

    public static SimpleEngineManager getInstance() {
        if (mInstance == null) {
            synchronized (SimpleEngineManager.class) {
                if (mInstance == null) {
                    mInstance = new SimpleEngineManager();
                }
            }
        }
        return mInstance;
    }

    public void setApplicationContext(Context context) {
        this.mAppContext = context;
    }

    public SimpleEngine getEngine(String str) {
        Log.m26i(TAG, "getEngine session:" + str);
        Map<String, SimpleEngine> map = this.engineMap;
        if (map != null && !map.containsKey(str)) {
            this.engineMap.put(str, new SimpleEngine(this.mAppContext, str));
        }
        return this.engineMap.get(str);
    }

    public boolean isContainEngineSession(String str) {
        Map<String, SimpleEngine> map = this.engineMap;
        return map != null && map.containsKey(str);
    }

    public int getEngineSize() {
        Log.m26i(TAG, "getEngineSize : " + this.engineMap.size());
        return this.engineMap.size();
    }

    public void deleteEngine(String str) {
        SimpleEngine remove;
        Log.m26i(TAG, "deleteEngine session:" + str);
        Map<String, SimpleEngine> map = this.engineMap;
        if (!(map == null || (remove = map.remove(str)) == null)) {
            remove.onDestroy();
        }
        String str2 = this.mActiveSession;
        if (str2 != null) {
            this.mActiveSession = str2.equals(str) ? null : this.mActiveSession;
        }
    }

    public synchronized int requestEngineFocus(String str, OnEngineFocusChangeListener onEngineFocusChangeListener) {
        this.mIsAllRecordingStopped = true;
        if (onEngineFocusChangeListener == null) {
            Log.m27i(TAG, "requestEngineFocus - ENGINEFOCUS_REQUEST_FAILED", str);
            return 0;
        }
        for (Map.Entry<String, WeakReference<OnEngineFocusChangeListener>> key : this.mEngineFocusListener.entrySet()) {
            String str2 = (String) key.getKey();
            SimpleEngine simpleEngine = this.engineMap.get(str2);
            if (simpleEngine != null && !simpleEngine.isSimplePlayerMode()) {
                if (!simpleEngine.isSaveEnable() && simpleEngine.getRecorderState() == 2) {
                    Log.m27i(TAG, "Recording cannot be stopped - ENGINEFOCUS_REQUEST_FAILED", str);
                    this.mIsAllRecordingStopped = false;
                } else if (simpleEngine.getRecorderState() != 1) {
                    notifyFocusChange(str2);
                }
            }
        }
        if (!this.mIsAllRecordingStopped) {
            return 0;
        }
        this.mEngineFocusListener.put(str, new WeakReference(onEngineFocusChangeListener));
        this.mActiveSession = str;
        Log.m27i(TAG, "requestEngineFocus - ENGINEFOCUS_REQUEST_GRANTED", str);
        return 1;
    }

    private void notifyFocusChange(String str) {
        WeakReference remove;
        OnEngineFocusChangeListener onEngineFocusChangeListener;
        Log.m20d(TAG, "NotifyFocusChange ", str);
        if (!this.mEngineFocusListener.isEmpty() && (remove = this.mEngineFocusListener.remove(str)) != null && (onEngineFocusChangeListener = (OnEngineFocusChangeListener) remove.get()) != null) {
            onEngineFocusChangeListener.onEngineFocusChange(-1);
            Log.m27i(TAG, "notifyFocusChange - ENGINEFOCUS_LOSS", str);
        }
    }

    public boolean isEngineFocus(String str) {
        return this.mEngineFocusListener.containsKey(str);
    }

    public synchronized void abandonEngineFocus(String str) {
        Log.m27i(TAG, "abandonEngineFocus : ", str);
        notifyFocusChange(str);
    }

    public SimpleEngine getActiveEngine() {
        if (this.mActiveSession != null && !this.engineMap.isEmpty()) {
            return this.engineMap.get(this.mActiveSession);
        }
        return null;
    }

    public String getActiveSessionName() {
        Log.m27i(TAG, "getActiveSessionName", this.mActiveSession);
        return this.mActiveSession;
    }

    public boolean isAnyRecordingActive() {
        return this.mIsAllRecordingStopped;
    }

    public void finishActiveSession() {
        Log.m20d(TAG, "finishActiveSession", this.mActiveSession);
        String str = this.mActiveSession;
        if (str != null) {
            notifyFocusChange(str);
        }
    }

    public boolean isWiredHeadSetConnected() {
        return this.mIsWiredHeadSetConnected;
    }

    public void setWiredHeadSetConnected(boolean z) {
        this.mIsWiredHeadSetConnected = z;
    }
}
