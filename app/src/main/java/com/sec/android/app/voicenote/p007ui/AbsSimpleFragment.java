package com.sec.android.app.voicenote.p007ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import com.sec.android.app.voicenote.activity.SimpleActivity;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.SimpleEngine;
import com.sec.android.app.voicenote.service.SimpleEngineManager;
import com.sec.android.app.voicenote.service.SimpleMetadataRepository;
import com.sec.android.app.voicenote.service.SimpleMetadataRepositoryManager;
import com.sec.android.app.voicenote.uicore.Observable;
import java.lang.reflect.Field;

/* renamed from: com.sec.android.app.voicenote.ui.AbsSimpleFragment */
public abstract class AbsSimpleFragment extends Fragment {
    private static final String TAG = "AbsFragment";
    private static Observable mObservable = Observable.getInstance();
    protected int mCurrentEvent = 1;
    private Handler mEventHandler = new Handler(new Handler.Callback() {
        public final boolean handleMessage(Message message) {
            return AbsSimpleFragment.this.lambda$new$0$AbsSimpleFragment(message);
        }
    });
    protected SimpleActivity.LaunchMode mLaunchMode;
    protected String mSession = null;
    protected SimpleEngine mSimpleEngine = null;
    protected SimpleMetadataRepository mSimpleMetadata = null;
    protected int mStartingEvent = 1;

    public abstract void onUpdate(Object obj);

    public void onCreate(Bundle bundle) {
        Log.m26i(getTag(), "onCreate");
        setRetainInstance(true);
        super.onCreate(bundle);
    }

    public void onDestroy() {
        Log.m26i(getTag(), "onDestroy");
        super.onDestroy();
        this.mStartingEvent = 1;
        this.mCurrentEvent = 1;
    }

    public void onDetach() {
        super.onDetach();
        try {
            Field declaredField = Fragment.class.getDeclaredField("mChildFragmentManager");
            declaredField.setAccessible(true);
            declaredField.set(this, (Object) null);
        } catch (NoSuchFieldException e) {
            Log.m24e(getTag(), "NoSuchFieldException", (Throwable) e);
            throw new RuntimeException(e);
        } catch (IllegalAccessException e2) {
            Log.m24e(getTag(), "IllegalAccessException", (Throwable) e2);
            throw new RuntimeException(e2);
        }
    }

    public void setEvent(int i) {
        this.mStartingEvent = i;
    }

    public void setSession(String str) {
        this.mSession = str;
        initEngine(str);
        initMetadataRepository(str);
    }

    public void setmLaunchMode(SimpleActivity.LaunchMode launchMode) {
        this.mLaunchMode = launchMode;
    }

    /* access modifiers changed from: protected */
    public void postEventDelayed(int i, long j) {
        String tag = getTag();
        Log.m26i(tag, "postEventDelayed : data = " + i + ", delayedTime = " + j);
        this.mEventHandler.sendEmptyMessageDelayed(i, j);
    }

    /* access modifiers changed from: protected */
    public void postEvent(int i) {
        String tag = getTag();
        Log.m26i(tag, "postEvent : data = " + i);
        this.mEventHandler.sendEmptyMessage(i);
    }

    /* access modifiers changed from: protected */
    public boolean hasPostEvent(int i) {
        String tag = getTag();
        Log.m26i(tag, "hasPostEvent : data = " + i);
        return this.mEventHandler.hasMessages(i);
    }

    /* access modifiers changed from: protected */
    public void removePostEvent(int i) {
        String tag = getTag();
        Log.m26i(tag, "removePostEvent : data = " + i);
        this.mEventHandler.removeMessages(i);
    }

    public /* synthetic */ boolean lambda$new$0$AbsSimpleFragment(Message message) {
        Log.m26i("Event", "handleMessage : " + message.what);
        mObservable.notifyObservers(this.mSession, Integer.valueOf(message.what));
        return false;
    }

    private void initEngine(String str) {
        this.mSimpleEngine = SimpleEngineManager.getInstance().getEngine(str);
    }

    private void initMetadataRepository(String str) {
        this.mSimpleMetadata = SimpleMetadataRepositoryManager.getInstance().getMetadataRepository(str);
    }
}
