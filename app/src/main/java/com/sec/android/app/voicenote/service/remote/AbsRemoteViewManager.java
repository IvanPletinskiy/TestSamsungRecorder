package com.sec.android.app.voicenote.service.remote;

import android.widget.RemoteViews;

public abstract class AbsRemoteViewManager {
    public static final int REMOTEVIEWSREQ = 117506050;

    /* access modifiers changed from: protected */
    public abstract RemoteViews buildRemoteView(int i, int i2, int i3, int i4);

    public abstract RemoteViews createRemoteView(int i, int i2, int i3);

    public abstract void hide(int i);

    public abstract void show(int i, int i2, int i3);

    public abstract void start(int i, int i2, int i3);

    public abstract void stop(int i);

    public abstract void update(int i, int i2, int i3, int i4);
}
