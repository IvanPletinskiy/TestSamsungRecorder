package com.sec.android.app.voicenote.provider;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Toast;
import com.sec.android.app.voicenote.C0690R;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class NFCProvider {
    private static final int ENC_NUM = 62;
    private static final char ENC_START = 'A';
    private static final int MAX_NUM = 100;
    public static final String NFC_DB_KEY = "year_name";
    public static final int NFC_DIFFERENT_DEVICE = -2;
    public static final int NFC_FILE_DOES_NOT_EXIST = -1;
    public static final String NFC_TAGGED = "NFC";
    private static final String TAG = "NFCProvider";

    public static boolean isNFCEnabled(Context context) {
        NfcAdapter defaultAdapter = ((NfcManager) context.getSystemService("nfc")).getDefaultAdapter();
        return defaultAdapter != null && defaultAdapter.isEnabled();
    }

    public static void enableNFC(Context context) {
        new EnableNFCTask(context).execute(new Boolean[]{true});
    }

    public static boolean hasTagData(Context context, long j) {
        if (!PermissionProvider.isStorageAccessEnable(context)) {
            return false;
        }
        Cursor query = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, (String[]) null, "_id=" + j, (String[]) null, (String) null);
        String str = null;
        if (query != null) {
            if (query.moveToFirst()) {
                str = query.getString(query.getColumnIndex(NFC_DB_KEY));
            }
            query.close();
        }
        if (str == null || !str.contains(NFC_TAGGED)) {
            return false;
        }
        return true;
    }

    public static int hasTagData(Context context, ArrayList<Long> arrayList) {
        int i = 0;
        int size = arrayList != null ? arrayList.size() : 0;
        if (!PermissionProvider.isStorageAccessEnable(context)) {
            return 0;
        }
        if (size > 0) {
            int i2 = 0;
            while (true) {
                int i3 = i + 100;
                Cursor query = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, (String[]) null, arrayList.subList(i, i3 > size ? size : i3).toString().replace(", ", " or _id=").replace("[", "(_id=").replace("]", ")") + " and " + NFC_DB_KEY + " LIKE '" + NFC_TAGGED + '%' + '\'', (String[]) null, (String) null);
                if (query != null) {
                    query.moveToFirst();
                    while (!query.isAfterLast()) {
                        String string = query.getString(query.getColumnIndex(NFC_DB_KEY));
                        if (string != null && string.contains(NFC_TAGGED)) {
                            i2++;
                        }
                        query.moveToNext();
                    }
                    query.close();
                }
                if (i3 >= size) {
                    break;
                }
                i = i3;
            }
            i = i2;
        }
        Log.m26i(TAG, "hasTagData - count : " + i);
        return i;
    }

    public static void deleteTagsData(Context context, long j) {
        Log.m26i(TAG, "deleteTagsData - id : " + j);
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(NFC_DB_KEY, "");
            context.getContentResolver().update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues, "_id=" + j, (String[]) null);
        } catch (SQLiteConstraintException | IllegalArgumentException e) {
            Log.m24e(TAG, "error occurred while extractMetadata", e);
        } catch (Exception e2) {
            Log.m24e(TAG, "error occurred while input data to MediaStore", (Throwable) e2);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x0094 A[SYNTHETIC, Splitter:B:24:0x0094] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00e9 A[Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x010f A[Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }] */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0157  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0166  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0172  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x017e  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0184  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean updateTaggedInfo(android.content.Context r15, java.lang.String r16, boolean r17) {
        /*
            r0 = r16
            java.lang.String r1 = "year_name"
            java.lang.String r2 = "NFCProvider"
            java.lang.String r3 = "updateTaggedInfo"
            com.sec.android.app.voicenote.provider.Log.m26i(r2, r3)
            r3 = 58
            r4 = 0
            r5 = 0
            int r3 = r0.indexOf(r3)     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            r6 = 47
            int r6 = r0.indexOf(r6)     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            if (r3 <= 0) goto L_0x0020
            java.lang.String r7 = r0.substring(r5, r3)     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            goto L_0x0021
        L_0x0020:
            r7 = r4
        L_0x0021:
            r8 = 1
            if (r6 <= 0) goto L_0x002e
            int r3 = r3 + r8
            java.lang.String r3 = r0.substring(r3, r6)     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            java.lang.String r0 = r0.substring(r6)     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            goto L_0x0030
        L_0x002e:
            r0 = r4
            r3 = r0
        L_0x0030:
            if (r0 != 0) goto L_0x003a
            if (r17 != 0) goto L_0x003a
            java.lang.String r0 = "File does not exist."
            com.sec.android.app.voicenote.provider.Log.m26i(r2, r0)     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            return r8
        L_0x003a:
            if (r7 == 0) goto L_0x005e
            if (r3 != 0) goto L_0x003f
            goto L_0x005e
        L_0x003f:
            java.lang.String r6 = getIDCode(r15)     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            boolean r6 = r7.equals(r6)     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            if (r6 != 0) goto L_0x0063
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            r0.<init>()     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            java.lang.String r1 = "DIFFERENT DEVICE : fileID is different, "
            r0.append(r1)     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            r0.append(r7)     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            java.lang.String r0 = r0.toString()     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            com.sec.android.app.voicenote.provider.Log.m26i(r2, r0)     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            return r5
        L_0x005e:
            java.lang.String r6 = "OLD VERSION : fileID and fileData is null"
            com.sec.android.app.voicenote.provider.Log.m26i(r2, r6)     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
        L_0x0063:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            r6.<init>()     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            r6.setLength(r5)     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            java.lang.String r7 = "_data"
            r6.append(r7)     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            java.lang.String r7 = " = \""
            r6.append(r7)     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            java.lang.String r0 = com.sec.android.app.voicenote.provider.StorageProvider.convertToSDCardReadOnlyPath(r0)     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            r6.append(r0)     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            r0 = 34
            r6.append(r0)     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            java.lang.String r12 = r6.toString()     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            android.content.ContentResolver r9 = r15.getContentResolver()     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            android.net.Uri r10 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            r11 = 0
            r13 = 0
            r14 = 0
            android.database.Cursor r7 = r9.query(r10, r11, r12, r13, r14)     // Catch:{ IllegalArgumentException -> 0x0176, SQLiteConstraintException -> 0x016a, Exception -> 0x015e }
            if (r7 == 0) goto L_0x0155
            boolean r0 = r7.moveToFirst()     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            if (r0 == 0) goto L_0x0146
            java.lang.String r0 = "datetaken"
            int r0 = r7.getColumnIndex(r0)     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            int r9 = r7.getColumnIndex(r1)     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            java.lang.String r0 = r7.getString(r0)     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            java.lang.String r9 = r7.getString(r9)     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            java.lang.String r6 = r6.toString()     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            java.lang.String r10 = "NFC"
            if (r9 == 0) goto L_0x00e6
            boolean r11 = r9.contains(r10)     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            if (r11 == 0) goto L_0x00e6
            int r11 = r9.length()     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            r12 = 3
            if (r11 <= r12) goto L_0x00e6
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            r11.<init>()     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            java.lang.String r13 = "count : "
            r11.append(r13)     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            java.lang.String r13 = r9.substring(r12)     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            r11.append(r13)     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            java.lang.String r11 = r11.toString()     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            com.sec.android.app.voicenote.provider.Log.m26i(r2, r11)     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            java.lang.String r9 = r9.substring(r12)     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            int r9 = r9.intValue()     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            goto L_0x00e7
        L_0x00e6:
            r9 = r5
        L_0x00e7:
            if (r17 == 0) goto L_0x010f
            android.content.ContentValues r0 = new android.content.ContentValues     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            r0.<init>()     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            r3.<init>()     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            r3.append(r10)     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            int r9 = r9 + r8
            java.lang.String r9 = java.lang.String.valueOf(r9)     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            r3.append(r9)     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            java.lang.String r3 = r3.toString()     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            r0.put(r1, r3)     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            android.content.ContentResolver r1 = r15.getContentResolver()     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            android.net.Uri r3 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            r1.update(r3, r0, r6, r4)     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            goto L_0x0146
        L_0x010f:
            boolean r0 = r0.equals(r3)     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            if (r0 == 0) goto L_0x0146
            int r0 = r9 + -1
            if (r0 >= 0) goto L_0x011a
            r0 = r5
        L_0x011a:
            android.content.ContentValues r3 = new android.content.ContentValues     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            r3.<init>()     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            if (r0 <= 0) goto L_0x0138
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            r9.<init>()     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            r9.append(r10)     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            java.lang.String r0 = java.lang.String.valueOf(r0)     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            r9.append(r0)     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            java.lang.String r0 = r9.toString()     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            r3.put(r1, r0)     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            goto L_0x013d
        L_0x0138:
            java.lang.String r0 = ""
            r3.put(r1, r0)     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
        L_0x013d:
            android.content.ContentResolver r0 = r15.getContentResolver()     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            android.net.Uri r1 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            r0.update(r1, r3, r6, r4)     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
        L_0x0146:
            r7.close()     // Catch:{ IllegalArgumentException -> 0x0152, SQLiteConstraintException -> 0x014f, Exception -> 0x014c, all -> 0x014a }
            goto L_0x0155
        L_0x014a:
            r0 = move-exception
            goto L_0x0182
        L_0x014c:
            r0 = move-exception
            r4 = r7
            goto L_0x015f
        L_0x014f:
            r0 = move-exception
            r4 = r7
            goto L_0x016b
        L_0x0152:
            r0 = move-exception
            r4 = r7
            goto L_0x0177
        L_0x0155:
            if (r7 == 0) goto L_0x015a
            r7.close()
        L_0x015a:
            return r8
        L_0x015b:
            r0 = move-exception
            r7 = r4
            goto L_0x0182
        L_0x015e:
            r0 = move-exception
        L_0x015f:
            java.lang.String r1 = "error occurred while input data to MediaStore"
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r2, (java.lang.String) r1, (java.lang.Throwable) r0)     // Catch:{ all -> 0x015b }
            if (r4 == 0) goto L_0x0169
            r4.close()
        L_0x0169:
            return r5
        L_0x016a:
            r0 = move-exception
        L_0x016b:
            java.lang.String r1 = "SQLiteConstraintException"
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r2, (java.lang.String) r1, (java.lang.Throwable) r0)     // Catch:{ all -> 0x015b }
            if (r4 == 0) goto L_0x0175
            r4.close()
        L_0x0175:
            return r5
        L_0x0176:
            r0 = move-exception
        L_0x0177:
            java.lang.String r1 = "IllegalArgumentException"
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r2, (java.lang.String) r1, (java.lang.Throwable) r0)     // Catch:{ all -> 0x015b }
            if (r4 == 0) goto L_0x0181
            r4.close()
        L_0x0181:
            return r5
        L_0x0182:
            if (r7 == 0) goto L_0x0187
            r7.close()
        L_0x0187:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.provider.NFCProvider.updateTaggedInfo(android.content.Context, java.lang.String, boolean):boolean");
    }

    public static long hasValidNFCInfo(Context context, Intent intent) {
        Parcelable[] parcelableArrayExtra;
        Log.m26i(TAG, "hasValidNFCInfo");
        String action = intent.getAction();
        if (("android.nfc.action.TAG_DISCOVERED".equals(action) || "android.nfc.action.TECH_DISCOVERED".equals(action) || "android.nfc.action.NDEF_DISCOVERED".equals(action)) && (parcelableArrayExtra = intent.getParcelableArrayExtra("android.nfc.extra.NDEF_MESSAGES")) != null && parcelableArrayExtra.length > 0) {
            NdefMessage[] ndefMessageArr = new NdefMessage[parcelableArrayExtra.length];
            int i = 0;
            while (i < parcelableArrayExtra.length) {
                try {
                    ndefMessageArr[i] = (NdefMessage) parcelableArrayExtra[i];
                    byte[] payload = ndefMessageArr[i].getRecords()[0].getPayload();
                    if (payload != null) {
                        if (payload.length > 0) {
                            String str = (payload[0] & 128) == 0 ? "UTF-8" : "UTF-16";
                            byte b = (byte) (payload[0] & 63);
                            String str2 = new String(payload, b + 1, (payload.length - b) - 1, str);
                            int isValidTags = isValidTags(context, str2);
                            if (isValidTags == 0) {
                                long idByPath = DBProvider.getInstance().getIdByPath(str2.substring(str2.indexOf(47)));
                                if (idByPath != -1) {
                                    return idByPath;
                                }
                                return -1;
                            } else if (isValidTags == -2) {
                                return -2;
                            } else {
                                return -1;
                            }
                        }
                    }
                    i++;
                } catch (Exception e) {
                    Log.m24e(TAG, "Exception ", (Throwable) e);
                }
            }
        }
        return -1;
    }

    private static int isValidTags(Context context, String str) {
        String str2;
        int indexOf = str.indexOf(58);
        int indexOf2 = str.indexOf(47);
        String str3 = null;
        String substring = indexOf > 0 ? str.substring(0, indexOf) : null;
        if (indexOf2 > 0) {
            str3 = str.substring(indexOf + 1, indexOf2);
            str2 = str.substring(indexOf2);
        } else {
            str2 = null;
        }
        if (str2 == null) {
            Log.m22e(TAG, "File does not exist.");
            return -1;
        } else if (substring == null || substring.equals(getIDCode(context))) {
            Cursor query = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, (String[]) null, "_data = \"" + StorageProvider.convertToSDCardReadOnlyPath(str2) + '\"', (String[]) null, (String) null);
            if (query != null) {
                if (query.moveToFirst()) {
                    String string = query.getString(query.getColumnIndex(NFC_DB_KEY));
                    String string2 = query.getString(query.getColumnIndex("datetaken"));
                    if (substring == null) {
                        Log.m26i(TAG, "OLD VERSION : fileID and filedate is null");
                        if (string.contains(NFC_TAGGED)) {
                            query.close();
                            return 0;
                        }
                    } else {
                        if (!str3.equals(string2)) {
                            Log.m26i(TAG, "DIFFERENT TIME : filedate(" + str3 + "), tagsTime(" + string2 + ')');
                            try {
                                if (Long.valueOf(string2).longValue() - Long.valueOf(str3).longValue() == 1) {
                                    Log.m26i(TAG, "DIFFERENT TIME : same");
                                } else {
                                    query.close();
                                    return -1;
                                }
                            } catch (NumberFormatException e) {
                                Log.m24e(TAG, "NumberFormatException", (Throwable) e);
                                query.close();
                                return -1;
                            }
                        }
                        if (string.contains(NFC_TAGGED)) {
                            query.close();
                            return 0;
                        }
                    }
                }
                query.close();
            }
            return -1;
        } else {
            Log.m22e(TAG, "DIFFERENT DEVICE : fileID is different, " + substring);
            return -2;
        }
    }

    public static String getCurrentLabelInfo(Context context, long j) {
        Cursor query = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, (String[]) null, "_id=" + j, (String[]) null, (String) null);
        String str = null;
        if (query != null) {
            if (query.moveToFirst()) {
                int columnIndex = query.getColumnIndex("_data");
                int columnIndex2 = query.getColumnIndex("datetaken");
                str = (getIDCode(context) + ':' + query.getString(columnIndex2)) + query.getString(columnIndex);
            }
            query.close();
        }
        return str;
    }

    private static String getIDCode(Context context) {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(Settings.Secure.getString(context.getContentResolver(), "android_id"));
        } catch (Exception unused) {
            Log.m26i(TAG, "Invalid AndroidID exception");
            sb.append((char) ((int) 64));
            sb.append((char) ((int) 65));
        }
        return sb.toString();
    }

    public static class EnableNFCTask extends AsyncTask<Boolean, Void, Boolean> {
        private static final int STATE_CARD_MODE_ON = 5;
        private static final String TAG = "EnableNFCTask";
        private Context mAppContext;

        public EnableNFCTask(Context context) {
            this.mAppContext = context;
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(Boolean... boolArr) {
            Object obj;
            boolean z = false;
            boolean booleanValue = boolArr[0].booleanValue();
            NfcAdapter defaultAdapter = NfcAdapter.getDefaultAdapter(this.mAppContext);
            if (defaultAdapter == null) {
                return false;
            }
            Log.m26i(TAG, "Setting NFC enabled state to: " + booleanValue);
            Object obj2 = null;
            if (booleanValue) {
                try {
                    if (VoiceNoteFeature.FLAG_SUPPORT_NFC_CARDMODE) {
                        Class<?> cls = Class.forName(defaultAdapter.getClass().getName());
                        Method declaredMethod = cls.getDeclaredMethod("getAdapterState", new Class[0]);
                        Method declaredMethod2 = cls.getDeclaredMethod("readerEnable", new Class[0]);
                        Method declaredMethod3 = cls.getDeclaredMethod("enableNdefPush", new Class[0]);
                        Method declaredMethod4 = cls.getDeclaredMethod("enable", new Class[0]);
                        int i = 5;
                        try {
                            Field field = cls.getField("STATE_CARD_MODE_ON");
                            if (field.getType() == Integer.TYPE) {
                                i = field.getInt((Object) null);
                            }
                        } catch (NoSuchFieldException unused) {
                            Log.m26i(TAG, "get cardModeOnValue by reflection value failed");
                        }
                        declaredMethod.setAccessible(true);
                        declaredMethod2.setAccessible(true);
                        declaredMethod3.setAccessible(true);
                        declaredMethod4.setAccessible(true);
                        if (((Integer) declaredMethod.invoke(defaultAdapter, new Object[0])).intValue() == i) {
                            obj2 = declaredMethod2.invoke(defaultAdapter, new Object[0]);
                            declaredMethod3.invoke(defaultAdapter, new Object[0]);
                        } else {
                            declaredMethod4.invoke(defaultAdapter, new Object[0]);
                            obj2 = declaredMethod2.invoke(defaultAdapter, new Object[0]);
                            declaredMethod3.invoke(defaultAdapter, new Object[0]);
                        }
                    } else {
                        if (VoiceNoteFeature.FLAG_SUPPORT_NFC_RWP2P) {
                            Method declaredMethod5 = Class.forName(defaultAdapter.getClass().getName()).getDeclaredMethod("setRwP2pMode", new Class[]{Boolean.TYPE});
                            declaredMethod5.setAccessible(true);
                            obj = declaredMethod5.invoke(defaultAdapter, new Object[]{true});
                        } else {
                            Method declaredMethod6 = Class.forName(defaultAdapter.getClass().getName()).getDeclaredMethod("enable", new Class[0]);
                            declaredMethod6.setAccessible(true);
                            obj = declaredMethod6.invoke(defaultAdapter, new Object[0]);
                        }
                        obj2 = obj;
                    }
                } catch (ClassNotFoundException e) {
                    Log.m24e(TAG, "ClassNotFoundException", (Throwable) e);
                } catch (NoSuchMethodException e2) {
                    Log.m24e(TAG, "NoSuchMethodException", (Throwable) e2);
                } catch (IllegalArgumentException e3) {
                    Log.m24e(TAG, "IllegalArgumentException", (Throwable) e3);
                } catch (IllegalAccessException e4) {
                    Log.m24e(TAG, "IllegalAccessException", (Throwable) e4);
                } catch (InvocationTargetException e5) {
                    Log.m24e(TAG, "InvocationTargetException", (Throwable) e5);
                }
            }
            if (obj2 != null) {
                z = ((Boolean) obj2).booleanValue();
            }
            if (z) {
                Log.m26i(TAG, "Successfully changed NFC enabled state to " + booleanValue);
            } else {
                Log.m22e(TAG, "Error setting NFC enabled state to " + booleanValue);
            }
            return Boolean.valueOf(z);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            if (!bool.booleanValue()) {
                Toast.makeText(this.mAppContext, C0690R.string.activation_nfc_failed, 1).show();
            }
        }
    }
}
