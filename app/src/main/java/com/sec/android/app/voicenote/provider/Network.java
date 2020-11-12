package com.sec.android.app.voicenote.provider;

public class Network {
    private static final String TAG = "NetWork";

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000a, code lost:
        r2 = r2.getActiveNetworkInfo();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isNetworkConnected(android.content.Context r2) {
        /*
            java.lang.String r0 = "connectivity"
            java.lang.Object r2 = r2.getSystemService(r0)
            android.net.ConnectivityManager r2 = (android.net.ConnectivityManager) r2
            if (r2 == 0) goto L_0x0018
            android.net.NetworkInfo r2 = r2.getActiveNetworkInfo()
            if (r2 == 0) goto L_0x0018
            boolean r2 = r2.isConnected()
            if (r2 == 0) goto L_0x0018
            r2 = 1
            goto L_0x0019
        L_0x0018:
            r2 = 0
        L_0x0019:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "isNetWorkConnected "
            r0.append(r1)
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "NetWork"
            com.sec.android.app.voicenote.provider.Log.m26i(r1, r0)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.provider.Network.isNetworkConnected(android.content.Context):boolean");
    }
}
