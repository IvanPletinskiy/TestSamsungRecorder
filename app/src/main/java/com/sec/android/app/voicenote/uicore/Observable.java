package com.sec.android.app.voicenote.uicore;

import com.sec.android.app.voicenote.provider.Log;
import java.util.ArrayList;
import java.util.HashMap;

public class Observable {
    private static final String TAG = "Observable";
    private static volatile Observable mObservable;
    private boolean changed = false;
    private final HashMap<String, ArrayList<Observer>> observersMap = new HashMap<>();

    private Observable() {
        Log.m26i(TAG, "Observable creator !!");
    }

    public static Observable getInstance() {
        if (mObservable == null) {
            synchronized (Observable.class) {
                if (mObservable == null) {
                    mObservable = new Observable();
                }
            }
        }
        return mObservable;
    }

    public synchronized void addObserver(String str, Observer observer) {
        Log.m27i(TAG, "addObserver data : " + observer, str);
        if (observer == null || str == null) {
            throw new NullPointerException();
        } else if (!this.observersMap.containsKey(str)) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(observer);
            this.observersMap.put(str, arrayList);
        } else {
            ArrayList arrayList2 = this.observersMap.get(str);
            if (!arrayList2.contains(observer)) {
                arrayList2.add(observer);
                this.observersMap.put(str, arrayList2);
            }
        }
    }

    public synchronized void deleteObserver(String str, Observer observer) {
        Log.m27i(TAG, "deleteObserver : Observer " + observer, str);
        if (this.observersMap.containsKey(str)) {
            ArrayList arrayList = this.observersMap.get(str);
            arrayList.remove(observer);
            if (arrayList.size() == 0) {
                this.observersMap.remove(str);
            } else {
                this.observersMap.put(str, arrayList);
            }
        } else {
            Log.m27i(TAG, "deleteObserver already deleted : Observer " + observer, str);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0028, code lost:
        r1 = r0.length - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002b, code lost:
        if (r1 < 0) goto L_0x0035;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x002d, code lost:
        r0[r1].update(r3, r5);
        r1 = r1 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0035, code lost:
        com.sec.android.app.voicenote.provider.Log.m27i(TAG, "notifyObservers - data : " + ((java.lang.Integer) r5).intValue(), r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0051, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void notifyObservers(java.lang.String r4, java.lang.Object r5) {
        /*
            r3 = this;
            r3.setChanged()
            monitor-enter(r3)
            boolean r0 = r3.hasChanged()     // Catch:{ all -> 0x0052 }
            if (r0 != 0) goto L_0x000c
            monitor-exit(r3)     // Catch:{ all -> 0x0052 }
            return
        L_0x000c:
            java.util.HashMap<java.lang.String, java.util.ArrayList<com.sec.android.app.voicenote.uicore.Observer>> r0 = r3.observersMap     // Catch:{ all -> 0x0052 }
            java.lang.Object r0 = r0.get(r4)     // Catch:{ all -> 0x0052 }
            java.util.ArrayList r0 = (java.util.ArrayList) r0     // Catch:{ all -> 0x0052 }
            if (r0 != 0) goto L_0x0018
            monitor-exit(r3)     // Catch:{ all -> 0x0052 }
            return
        L_0x0018:
            int r1 = r0.size()     // Catch:{ all -> 0x0052 }
            com.sec.android.app.voicenote.uicore.Observer[] r1 = new com.sec.android.app.voicenote.uicore.Observer[r1]     // Catch:{ all -> 0x0052 }
            java.lang.Object[] r0 = r0.toArray(r1)     // Catch:{ all -> 0x0052 }
            com.sec.android.app.voicenote.uicore.Observer[] r0 = (com.sec.android.app.voicenote.uicore.Observer[]) r0     // Catch:{ all -> 0x0052 }
            r3.clearChanged()     // Catch:{ all -> 0x0052 }
            monitor-exit(r3)     // Catch:{ all -> 0x0052 }
            int r1 = r0.length
            int r1 = r1 + -1
        L_0x002b:
            if (r1 < 0) goto L_0x0035
            r2 = r0[r1]
            r2.update(r3, r5)
            int r1 = r1 + -1
            goto L_0x002b
        L_0x0035:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "notifyObservers - data : "
            r0.append(r1)
            java.lang.Integer r5 = (java.lang.Integer) r5
            int r5 = r5.intValue()
            r0.append(r5)
            java.lang.String r5 = r0.toString()
            java.lang.String r0 = "Observable"
            com.sec.android.app.voicenote.provider.Log.m27i((java.lang.String) r0, (java.lang.String) r5, (java.lang.String) r4)
            return
        L_0x0052:
            r4 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0052 }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.uicore.Observable.notifyObservers(java.lang.String, java.lang.Object):void");
    }

    /* access modifiers changed from: protected */
    public synchronized void setChanged() {
        this.changed = true;
    }

    /* access modifiers changed from: protected */
    public synchronized void clearChanged() {
        this.changed = false;
    }

    public synchronized boolean hasChanged() {
        return this.changed;
    }
}
