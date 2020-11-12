package com.sec.android.app.voicenote.provider;

import android.content.Context;
import android.net.Uri;

public class PreferenceProvider {
    public static final long DEFAULT_MMS_MAX_SIZE = 302080;
    private static final String KEY_MSG_MMS_MAX_SIZE = "pref_key_mms_max_size";
    private static final Uri MSG_PREFERENCE = Uri.parse("content://com.android.mms.csc.PreferenceProvider/key");
    private static final String TAG = "PreferenceProvider";

    public static long getMmsMaxSize(Context context) {
        Object obj = null;
        if (context != null) {
            obj = getValue(context, MSG_PREFERENCE, KEY_MSG_MMS_MAX_SIZE, "STRING", (Object) null);
        }
        long j = DEFAULT_MMS_MAX_SIZE;
        String str = (obj == null || !(obj instanceof String)) ? "300" : (String) obj;
        char c = 65535;
        int hashCode = str.hashCode();
        if (hashCode != 1628) {
            if (hashCode != 53430) {
                if (hashCode == 1505624 && str.equals("1.2m")) {
                    c = 2;
                }
            } else if (str.equals("600")) {
                c = 1;
            }
        } else if (str.equals("1m")) {
            c = 0;
        }
        if (c == 0) {
            j = 1018880;
            Settings.setMmsMaxSize(str, 1018880);
        } else if (c == 1) {
            j = 609280;
            Settings.setMmsMaxSize(str, 609280);
        } else if (c != 2) {
            Settings.setMmsMaxSize("cannot catch the mms max size : " + str, DEFAULT_MMS_MAX_SIZE);
        } else {
            j = 1223680;
            Settings.setMmsMaxSize(str, 1223680);
        }
        Log.m29v(TAG, "getMmsMaxSize mms_max_size : " + str + " lMmsMaxSize : " + j);
        return j;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x006c, code lost:
        if (r11 == null) goto L_0x0080;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x006e, code lost:
        r11.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x007d, code lost:
        if (r11 != null) goto L_0x006e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0080, code lost:
        com.sec.android.app.voicenote.provider.Log.m29v(TAG, r10 + " = " + r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0097, code lost:
        return r14;
     */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x009b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.Object getValue(android.content.Context r10, android.net.Uri r11, java.lang.String r12, java.lang.String r13, java.lang.Object r14) {
        /*
            java.lang.String r0 = "PreferenceProvider"
            android.content.ContentResolver r1 = r10.getContentResolver()
            r3 = 0
            r10 = 0
            r7 = 1
            java.lang.String[] r5 = new java.lang.String[r7]     // Catch:{ Exception -> 0x0077, all -> 0x0072 }
            r8 = 0
            r5[r8] = r12     // Catch:{ Exception -> 0x0077, all -> 0x0072 }
            r6 = 0
            r2 = r11
            r4 = r13
            android.database.Cursor r11 = r1.query(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x0077, all -> 0x0072 }
            if (r11 == 0) goto L_0x0067
            boolean r12 = r11.moveToFirst()     // Catch:{ Exception -> 0x0078 }
            if (r12 == 0) goto L_0x0067
            java.lang.String r12 = "STRING"
            boolean r12 = r12.equalsIgnoreCase(r13)     // Catch:{ Exception -> 0x0078 }
            if (r12 == 0) goto L_0x002a
            java.lang.String r12 = r11.getString(r8)     // Catch:{ Exception -> 0x0078 }
            goto L_0x0061
        L_0x002a:
            java.lang.String r12 = "INT"
            boolean r12 = r12.equalsIgnoreCase(r13)     // Catch:{ Exception -> 0x0078 }
            if (r12 == 0) goto L_0x003b
            int r12 = r11.getInt(r8)     // Catch:{ Exception -> 0x0078 }
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)     // Catch:{ Exception -> 0x0078 }
            goto L_0x0061
        L_0x003b:
            java.lang.String r12 = "BOOLEAN"
            boolean r12 = r12.equalsIgnoreCase(r13)     // Catch:{ Exception -> 0x0078 }
            if (r12 == 0) goto L_0x004f
            int r12 = r11.getInt(r8)     // Catch:{ Exception -> 0x0078 }
            if (r7 != r12) goto L_0x004a
            r8 = r7
        L_0x004a:
            java.lang.Boolean r12 = java.lang.Boolean.valueOf(r8)     // Catch:{ Exception -> 0x0078 }
            goto L_0x0061
        L_0x004f:
            java.lang.String r12 = "LONG"
            boolean r12 = r12.equalsIgnoreCase(r13)     // Catch:{ Exception -> 0x0078 }
            if (r12 == 0) goto L_0x0060
            long r12 = r11.getLong(r8)     // Catch:{ Exception -> 0x0078 }
            java.lang.Long r12 = java.lang.Long.valueOf(r12)     // Catch:{ Exception -> 0x0078 }
            goto L_0x0061
        L_0x0060:
            r12 = r14
        L_0x0061:
            java.lang.String r10 = r11.getString(r7)     // Catch:{ Exception -> 0x0078 }
            r14 = r12
            goto L_0x006c
        L_0x0067:
            java.lang.String r12 = "cursor is null"
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r12)     // Catch:{ Exception -> 0x0078 }
        L_0x006c:
            if (r11 == 0) goto L_0x0080
        L_0x006e:
            r11.close()
            goto L_0x0080
        L_0x0072:
            r11 = move-exception
            r9 = r11
            r11 = r10
            r10 = r9
            goto L_0x0099
        L_0x0077:
            r11 = r10
        L_0x0078:
            java.lang.String r12 = "It doesn't support PreferenceProvider"
            com.sec.android.app.voicenote.provider.Log.m29v(r0, r12)     // Catch:{ all -> 0x0098 }
            if (r11 == 0) goto L_0x0080
            goto L_0x006e
        L_0x0080:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r10)
            java.lang.String r10 = " = "
            r11.append(r10)
            r11.append(r14)
            java.lang.String r10 = r11.toString()
            com.sec.android.app.voicenote.provider.Log.m29v(r0, r10)
            return r14
        L_0x0098:
            r10 = move-exception
        L_0x0099:
            if (r11 == 0) goto L_0x009e
            r11.close()
        L_0x009e:
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.provider.PreferenceProvider.getValue(android.content.Context, android.net.Uri, java.lang.String, java.lang.String, java.lang.Object):java.lang.Object");
    }
}
