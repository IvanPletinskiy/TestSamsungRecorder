package com.samsung.context.sdk.samsunganalytics.internal.sender.DLC;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import com.samsung.context.sdk.samsunganalytics.internal.Callback;
import com.samsung.context.sdk.samsunganalytics.internal.util.Debug;
import com.sec.spp.push.dlc.api.IDlcService;

public class DLCBinder {
    private static String DLC_LOG_CLASS = "com.sec.spp.push.dlc.writer.WriterService";
    private static String DLC_LOG_PACKAGE = "com.sec.spp.push";
    /* access modifiers changed from: private */
    public Callback callback;
    /* access modifiers changed from: private */
    public Context context;
    /* access modifiers changed from: private */
    public BroadcastReceiver dlcRegisterReplyReceiver;
    /* access modifiers changed from: private */
    public IDlcService dlcService;
    private ServiceConnection dlcServiceConnection;
    /* access modifiers changed from: private */
    public boolean isBindToDLC;
    /* access modifiers changed from: private */
    public boolean onRegisterRequest;
    /* access modifiers changed from: private */
    public String registerFilter;

    public DLCBinder(Context context2) {
        this.isBindToDLC = false;
        this.onRegisterRequest = false;
        this.dlcServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Debug.LogD("DLC Sender", "DLC Client ServiceConnected");
                IDlcService unused = DLCBinder.this.dlcService = IDlcService.Stub.asInterface(iBinder);
                if (DLCBinder.this.dlcRegisterReplyReceiver != null) {
                    DLCBinder.this.context.unregisterReceiver(DLCBinder.this.dlcRegisterReplyReceiver);
                    BroadcastReceiver unused2 = DLCBinder.this.dlcRegisterReplyReceiver = null;
                }
                if (DLCBinder.this.callback != null) {
                    DLCBinder.this.callback.onResult(null);
                }
            }

            public void onServiceDisconnected(ComponentName componentName) {
                Debug.LogD("DLC Sender", "Client ServiceDisconnected");
                IDlcService unused = DLCBinder.this.dlcService = null;
                boolean unused2 = DLCBinder.this.isBindToDLC = false;
            }
        };
        this.context = context2;
        this.registerFilter = context2.getPackageName();
        this.registerFilter += ".REGISTER_FILTER";
    }

    public DLCBinder(Context context2, Callback callback2) {
        this(context2);
        this.callback = callback2;
    }

    public void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(this.registerFilter);
        if (this.dlcRegisterReplyReceiver == null) {
            this.dlcRegisterReplyReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    boolean unused = DLCBinder.this.onRegisterRequest = false;
                    if (intent == null) {
                        Debug.LogD("DLC Sender", "dlc register reply fail");
                        return;
                    }
                    String action = intent.getAction();
                    Bundle extras = intent.getExtras();
                    if (action == null || extras == null) {
                        Debug.LogD("DLC Sender", "dlc register reply fail");
                    } else if (action.equals(DLCBinder.this.registerFilter)) {
                        String string = extras.getString("EXTRA_STR");
                        int i = extras.getInt("EXTRA_RESULT_CODE");
                        Debug.LogD("DLC Sender", "register DLC result:" + string);
                        if (i < 0) {
                            Debug.LogD("DLC Sender", "register DLC result fail:" + string);
                            return;
                        }
                        DLCBinder.this.bindService(extras.getString("EXTRA_STR_ACTION"));
                    }
                }
            };
        }
        this.context.registerReceiver(this.dlcRegisterReplyReceiver, intentFilter);
    }

    public void sendRegisterRequestToDLC() {
        if (this.dlcRegisterReplyReceiver == null) {
            registerReceiver();
        }
        if (!this.onRegisterRequest) {
            Intent intent = new Intent("com.sec.spp.push.REQUEST_REGISTER");
            intent.putExtra("EXTRA_PACKAGENAME", this.context.getPackageName());
            intent.putExtra("EXTRA_INTENTFILTER", this.registerFilter);
            intent.setPackage("com.sec.spp.push");
            this.context.sendBroadcast(intent);
            this.onRegisterRequest = true;
            Debug.LogD("DLCBinder", "send register Request");
            Debug.LogENG("send register Request:" + this.context.getPackageName());
            return;
        }
        Debug.LogD("DLCBinder", "already send register request");
    }

    /* access modifiers changed from: private */
    public void bindService(String str) {
        if (this.isBindToDLC) {
            unbindService();
        }
        try {
            Intent intent = new Intent(str);
            intent.setClassName(DLC_LOG_PACKAGE, DLC_LOG_CLASS);
            this.isBindToDLC = this.context.bindService(intent, this.dlcServiceConnection, 1);
            Debug.LogD("DLCBinder", "bind");
        } catch (Exception e) {
            Debug.LogException(DLCBinder.class, e);
        }
    }

    private void unbindService() {
        if (this.isBindToDLC) {
            try {
                Debug.LogD("DLCBinder", "unbind");
                this.context.unbindService(this.dlcServiceConnection);
                this.isBindToDLC = false;
            } catch (Exception e) {
                Debug.LogException(DLCBinder.class, e);
            }
        }
    }

    public boolean isBindToDLC() {
        return this.isBindToDLC;
    }

    public IDlcService getDlcService() {
        return this.dlcService;
    }
}
