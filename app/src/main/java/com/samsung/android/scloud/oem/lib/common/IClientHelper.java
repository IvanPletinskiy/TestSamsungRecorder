package com.samsung.android.scloud.oem.lib.common;

import android.content.Context;
import android.os.Bundle;
import com.samsung.android.scloud.oem.lib.LOG;

public abstract class IClientHelper {
    private static String TAG = "IClientHelper";

    public abstract Object getClient(String str);

    public abstract IServiceHandler getServiceHandler(String str);

    public Bundle handleRequest(Context context, String str, String str2, Bundle bundle) {
        IServiceHandler serviceHandler = getServiceHandler(str);
        if (serviceHandler != null) {
            return serviceHandler.handleServiceAction(context, getClient(str2), str2, bundle);
        }
        String str3 = TAG;
        LOG.m15i(str3, "handleRequest can't find method:" + str);
        return null;
    }
}
