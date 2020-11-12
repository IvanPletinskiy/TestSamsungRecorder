package com.sec.android.app.voicenote.p007ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.lang.reflect.Field;

/* renamed from: com.sec.android.app.voicenote.ui.AbsFragment */
public abstract class AbsFragment extends Fragment {
    protected static final String KEY_IS_BOOKMARK_SHOWING = "KEY_IS_BOOKMARK_SHOWING";
    private static Handler mEventHandler = new Handler($$Lambda$AbsFragment$Oapqe1RAvAOUC9Ic3xYbRpAjMkQ.INSTANCE);
    private static VoiceNoteObservable mObservable = VoiceNoteObservable.getInstance();
    protected int mCurrentEvent = 1;
    protected int mStartingEvent = 1;

    public boolean isBackPossible() {
        return true;
    }

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

    /* access modifiers changed from: protected */
    public void postEventDelayed(int i, long j) {
        String tag = getTag();
        Log.m26i(tag, "postEventDelayed : data = " + i + ", delayedTime = " + j);
        mEventHandler.sendEmptyMessageDelayed(i, j);
    }

    /* access modifiers changed from: protected */
    public void postEvent(int i) {
        String tag = getTag();
        Log.m26i(tag, "postEvent : data = " + i);
        mEventHandler.sendEmptyMessage(i);
    }

    /* access modifiers changed from: protected */
    public void sendEvent(int i) {
        String tag = getTag();
        Log.m26i(tag, "sendEvent : data = " + i);
        mObservable.notifyObservers(Integer.valueOf(i));
    }

    /* access modifiers changed from: protected */
    public boolean hasPostEvent(int i) {
        String tag = getTag();
        Log.m26i(tag, "hasPostEvent : data = " + i);
        return mEventHandler.hasMessages(i);
    }

    static /* synthetic */ boolean lambda$static$0(Message message) {
        Log.m26i("Event", "handleMessage : " + message.what);
        mObservable.notifyObservers(Integer.valueOf(message.what));
        return false;
    }
}
