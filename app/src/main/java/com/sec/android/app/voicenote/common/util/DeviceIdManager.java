package com.sec.android.app.voicenote.common.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import com.samsung.android.deviceidservice.IDeviceIdService;
import com.sec.android.app.voicenote.provider.Log;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

public class DeviceIdManager {
    public static final int DEVICE_ID_SERVICE_NULL = 4;
    public static final int FAIL_TO_BIND_SERVICE = 3;
    public static final int PACKAGE_NOT_EXIST = 2;
    public static final int RETRIEVE_OAID_END = 1;
    /* access modifiers changed from: private */
    public static final String TAG = "DeviceIdManager";
    private static DeviceIdManager mInstance;
    private static String mOAID;
    /* access modifiers changed from: private */
    public boolean mBound = false;
    private ServiceConnection mConnection;
    private Context mContext;
    /* access modifiers changed from: private */
    public IDeviceIdService mDeviceIdService;
    private final ArrayList<WeakReference<OnDeviceIdListener>> mListeners = new ArrayList<>();

    public interface OnDeviceIdListener {
        void onDeviceIdUpdate(int i);
    }

    public static synchronized DeviceIdManager getInstance(Context context) {
        DeviceIdManager deviceIdManager;
        synchronized (DeviceIdManager.class) {
            if (mInstance == null) {
                mInstance = new DeviceIdManager(context.getApplicationContext());
            }
            deviceIdManager = mInstance;
        }
        return deviceIdManager;
    }

    private DeviceIdManager(Context context) {
        this.mContext = context;
    }

    public String getOAID() {
        return mOAID;
    }

    public void runOAID() {
        Log.m26i(TAG, "try to retrieve oaid again");
        if (!isPackageExist(this.mContext, "com.samsung.android.deviceidservice")) {
            Log.m26i(TAG, "package not exist");
            notifyObservers(2);
            return;
        }
        Intent intent = new Intent();
        intent.setClassName("com.samsung.android.deviceidservice", "com.samsung.android.deviceidservice.DeviceIdService");
        if (this.mConnection == null) {
            this.mConnection = new ServiceConnection() {
                public void onServiceDisconnected(ComponentName componentName) {
                    Log.m26i(DeviceIdManager.TAG, "***** Device ID Service is disconnected");
                    IDeviceIdService unused = DeviceIdManager.this.mDeviceIdService = null;
                    boolean unused2 = DeviceIdManager.this.mBound = false;
                }

                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    Log.m26i(DeviceIdManager.TAG, "***** Device ID Service is connected");
                    IDeviceIdService unused = DeviceIdManager.this.mDeviceIdService = IDeviceIdService.Stub.asInterface(iBinder);
                    boolean unused2 = DeviceIdManager.this.mBound = true;
                    DeviceIdManager.this.retrieveOAIDAsync();
                }
            };
        }
        try {
            this.mContext.bindService(intent, this.mConnection, 1);
        } catch (Exception e) {
            String str = TAG;
            Log.m22e(str, "Failed to bind to device id service. exception: " + e);
            notifyObservers(3);
        }
    }

    /* access modifiers changed from: private */
    public void retrieveOAIDAsync() {
        Log.m26i(TAG, "retrieveOAIDAsync");
        if (this.mDeviceIdService == null) {
            Log.m19d(TAG, "mDeviceIdService is null");
            notifyObservers(4);
            return;
        }
        new Thread(new Runnable() {
            public final void run() {
                DeviceIdManager.this.lambda$retrieveOAIDAsync$0$DeviceIdManager();
            }
        }).start();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0039, code lost:
        if (r2 != null) goto L_0x003b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x003b, code lost:
        r2.unbindService(r6.mConnection);
        r6.mBound = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0069, code lost:
        if (r2 != null) goto L_0x003b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$retrieveOAIDAsync$0$DeviceIdManager() {
        /*
            r6 = this;
            r0 = 0
            r1 = 1
            com.samsung.android.deviceidservice.IDeviceIdService r2 = r6.mDeviceIdService     // Catch:{ RemoteException -> 0x0048 }
            if (r2 == 0) goto L_0x0033
            com.samsung.android.deviceidservice.IDeviceIdService r2 = r6.mDeviceIdService     // Catch:{ RemoteException -> 0x0048 }
            java.lang.String r2 = r2.getOAID()     // Catch:{ RemoteException -> 0x0048 }
            if (r2 == 0) goto L_0x0033
            int r3 = r2.length()     // Catch:{ RemoteException -> 0x0048 }
            if (r3 <= 0) goto L_0x0033
            java.lang.String r3 = TAG     // Catch:{ RemoteException -> 0x0048 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x0048 }
            r4.<init>()     // Catch:{ RemoteException -> 0x0048 }
            java.lang.String r5 = "OAID : "
            r4.append(r5)     // Catch:{ RemoteException -> 0x0048 }
            r4.append(r2)     // Catch:{ RemoteException -> 0x0048 }
            java.lang.String r4 = r4.toString()     // Catch:{ RemoteException -> 0x0048 }
            com.sec.android.app.voicenote.provider.Log.m19d(r3, r4)     // Catch:{ RemoteException -> 0x0048 }
            mOAID = r2     // Catch:{ RemoteException -> 0x0048 }
            java.lang.String r2 = TAG     // Catch:{ RemoteException -> 0x0048 }
            java.lang.String r3 = "retrieve OAID success"
            com.sec.android.app.voicenote.provider.Log.m19d(r2, r3)     // Catch:{ RemoteException -> 0x0048 }
        L_0x0033:
            boolean r2 = r6.mBound
            if (r2 == 0) goto L_0x0042
            android.content.Context r2 = r6.mContext
            if (r2 == 0) goto L_0x0042
        L_0x003b:
            android.content.ServiceConnection r3 = r6.mConnection
            r2.unbindService(r3)
            r6.mBound = r0
        L_0x0042:
            r6.notifyObservers(r1)
            goto L_0x006c
        L_0x0046:
            r2 = move-exception
            goto L_0x006d
        L_0x0048:
            r2 = move-exception
            java.lang.String r3 = TAG     // Catch:{ all -> 0x0046 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0046 }
            r4.<init>()     // Catch:{ all -> 0x0046 }
            java.lang.String r5 = "getOAID failed."
            r4.append(r5)     // Catch:{ all -> 0x0046 }
            java.lang.String r2 = r2.getMessage()     // Catch:{ all -> 0x0046 }
            r4.append(r2)     // Catch:{ all -> 0x0046 }
            java.lang.String r2 = r4.toString()     // Catch:{ all -> 0x0046 }
            com.sec.android.app.voicenote.provider.Log.m22e(r3, r2)     // Catch:{ all -> 0x0046 }
            boolean r2 = r6.mBound
            if (r2 == 0) goto L_0x0042
            android.content.Context r2 = r6.mContext
            if (r2 == 0) goto L_0x0042
            goto L_0x003b
        L_0x006c:
            return
        L_0x006d:
            boolean r3 = r6.mBound
            if (r3 == 0) goto L_0x007c
            android.content.Context r3 = r6.mContext
            if (r3 == 0) goto L_0x007c
            android.content.ServiceConnection r4 = r6.mConnection
            r3.unbindService(r4)
            r6.mBound = r0
        L_0x007c:
            r6.notifyObservers(r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.common.util.DeviceIdManager.lambda$retrieveOAIDAsync$0$DeviceIdManager():void");
    }

    private boolean isPackageExist(Context context, String str) {
        try {
            return context.getPackageManager().getPackageInfo(str, 128) != null;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public final void registerListener(OnDeviceIdListener onDeviceIdListener) {
        if (onDeviceIdListener != null && !containsListener(onDeviceIdListener)) {
            this.mListeners.add(new WeakReference(onDeviceIdListener));
        }
    }

    private boolean containsListener(OnDeviceIdListener onDeviceIdListener) {
        ArrayList<WeakReference<OnDeviceIdListener>> arrayList = this.mListeners;
        if (!(arrayList == null || onDeviceIdListener == null)) {
            Iterator<WeakReference<OnDeviceIdListener>> it = arrayList.iterator();
            while (it.hasNext()) {
                if (onDeviceIdListener.equals(it.next().get())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void notifyObservers(int i) {
        int size = this.mListeners.size() - 1;
        while (size >= 0) {
            try {
                WeakReference weakReference = this.mListeners.get(size);
                if (weakReference != null) {
                    if (weakReference.get() == null) {
                        this.mListeners.remove(weakReference);
                    } else {
                        ((OnDeviceIdListener) weakReference.get()).onDeviceIdUpdate(i);
                    }
                    size--;
                } else {
                    return;
                }
            } catch (IndexOutOfBoundsException e) {
                Log.m24e(TAG, "IndexOutOfBoundsException !", (Throwable) e);
            } catch (NullPointerException e2) {
                Log.m24e(TAG, "NullPointerException !", (Throwable) e2);
            }
        }
    }
}
