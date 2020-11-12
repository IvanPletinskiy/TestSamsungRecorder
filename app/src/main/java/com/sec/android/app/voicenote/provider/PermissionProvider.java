package com.sec.android.app.voicenote.provider;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import java.util.ArrayList;
import java.util.Iterator;

public class PermissionProvider {
    private static final String TAG = "PermissionProvider";

    public static class RequestCode {
        public static final int ENABLE_AUTO_CALL_REJECT = 4;
        public static final int OVERWRITE = 3;
        public static final int RECORD = 2;
        public static final int STORAGE = 1;
        public static final int WRITE_TO_VOICE_LABEL = 5;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void runApplicationSettings(androidx.appcompat.app.AppCompatActivity r11, java.util.ArrayList<java.lang.Integer> r12, int r13, boolean r14) {
        /*
            if (r11 != 0) goto L_0x000a
            java.lang.String r11 = "PermissionProvider"
            java.lang.String r12 = "runApplicationSettings activity is null"
            com.sec.android.app.voicenote.provider.Log.m22e(r11, r12)
            return
        L_0x000a:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            r1 = 7
            java.lang.String r2 = "request_code"
            r0.putInt(r2, r1)
            r1 = 2131755539(0x7f100213, float:1.914196E38)
            java.lang.String r2 = "positive_btn_id"
            r0.putInt(r2, r1)
            r1 = 17039360(0x1040000, float:2.424457E-38)
            java.lang.String r2 = "negative_btn_id"
            r0.putInt(r2, r1)
            r1 = 972(0x3cc, float:1.362E-42)
            java.lang.String r2 = "positive_btn_event"
            r0.putInt(r2, r1)
            java.lang.String r1 = "ids"
            r0.putSerializable(r1, r12)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = -1
            r3 = 0
            r4 = 1
            if (r13 != r2) goto L_0x0050
            r13 = 2131755400(0x7f100188, float:1.9141678E38)
            java.lang.Object[] r5 = new java.lang.Object[r4]
            r6 = 2131755055(0x7f10002f, float:1.9140978E38)
            java.lang.String r6 = r11.getString(r6)
            r5[r3] = r6
            java.lang.String r13 = r11.getString(r13, r5)
            r1.append(r13)
            goto L_0x0062
        L_0x0050:
            r5 = 2131755399(0x7f100187, float:1.9141676E38)
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r13 = r11.getString(r13)
            r6[r3] = r13
            java.lang.String r13 = r11.getString(r5, r6)
            r1.append(r13)
        L_0x0062:
            java.util.ArrayList r13 = new java.util.ArrayList
            r13.<init>()
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            java.util.Iterator r6 = r12.iterator()
        L_0x0070:
            boolean r7 = r6.hasNext()
            if (r7 == 0) goto L_0x0088
            java.lang.Object r7 = r6.next()
            java.lang.Integer r7 = (java.lang.Integer) r7
            int r7 = r7.intValue()
            java.util.ArrayList r7 = getPermissionByRequestCode(r7)
            r5.addAll(r7)
            goto L_0x0070
        L_0x0088:
            java.util.Iterator r5 = r5.iterator()
        L_0x008c:
            boolean r6 = r5.hasNext()
            r7 = 3
            r8 = 2
            if (r6 == 0) goto L_0x0102
            java.lang.Object r6 = r5.next()
            java.lang.String r6 = (java.lang.String) r6
            int r9 = r6.hashCode()
            r10 = 4
            switch(r9) {
                case -406040016: goto L_0x00cb;
                case -5573545: goto L_0x00c1;
                case 112197485: goto L_0x00b7;
                case 1365911975: goto L_0x00ad;
                case 1831139720: goto L_0x00a3;
                default: goto L_0x00a2;
            }
        L_0x00a2:
            goto L_0x00d5
        L_0x00a3:
            java.lang.String r9 = "android.permission.RECORD_AUDIO"
            boolean r6 = r6.equals(r9)
            if (r6 == 0) goto L_0x00d5
            r6 = r10
            goto L_0x00d6
        L_0x00ad:
            java.lang.String r9 = "android.permission.WRITE_EXTERNAL_STORAGE"
            boolean r6 = r6.equals(r9)
            if (r6 == 0) goto L_0x00d5
            r6 = r4
            goto L_0x00d6
        L_0x00b7:
            java.lang.String r9 = "android.permission.CALL_PHONE"
            boolean r6 = r6.equals(r9)
            if (r6 == 0) goto L_0x00d5
            r6 = r8
            goto L_0x00d6
        L_0x00c1:
            java.lang.String r9 = "android.permission.READ_PHONE_STATE"
            boolean r6 = r6.equals(r9)
            if (r6 == 0) goto L_0x00d5
            r6 = r7
            goto L_0x00d6
        L_0x00cb:
            java.lang.String r9 = "android.permission.READ_EXTERNAL_STORAGE"
            boolean r6 = r6.equals(r9)
            if (r6 == 0) goto L_0x00d5
            r6 = r3
            goto L_0x00d6
        L_0x00d5:
            r6 = r2
        L_0x00d6:
            if (r6 == 0) goto L_0x00f7
            if (r6 == r4) goto L_0x00f7
            if (r6 == r8) goto L_0x00ec
            if (r6 == r7) goto L_0x00ec
            if (r6 == r10) goto L_0x00e1
            goto L_0x008c
        L_0x00e1:
            r6 = 2131755326(0x7f10013e, float:1.9141528E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r13.add(r6)
            goto L_0x008c
        L_0x00ec:
            r6 = 2131755403(0x7f10018b, float:1.9141684E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r13.add(r6)
            goto L_0x008c
        L_0x00f7:
            r6 = 2131755560(0x7f100228, float:1.9142003E38)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r13.add(r6)
            goto L_0x008c
        L_0x0102:
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "word"
            r0.putString(r2, r1)
            java.lang.String r1 = "permission_list_id"
            r0.putIntegerArrayList(r1, r13)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r4)
            boolean r13 = r12.contains(r13)
            if (r13 != 0) goto L_0x0130
            java.lang.Integer r13 = java.lang.Integer.valueOf(r8)
            boolean r13 = r12.contains(r13)
            if (r13 != 0) goto L_0x0130
            java.lang.Integer r13 = java.lang.Integer.valueOf(r7)
            boolean r12 = r12.contains(r13)
            if (r12 != 0) goto L_0x0130
            if (r14 == 0) goto L_0x0137
        L_0x0130:
            r12 = 971(0x3cb, float:1.36E-42)
            java.lang.String r13 = "negative_btn_event"
            r0.putInt(r13, r12)
        L_0x0137:
            androidx.fragment.app.FragmentManager r11 = r11.getSupportFragmentManager()
            java.lang.String r12 = "PermissionDialog"
            com.sec.android.app.voicenote.p007ui.dialog.DialogFactory.show(r11, r12, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.provider.PermissionProvider.runApplicationSettings(androidx.appcompat.app.AppCompatActivity, java.util.ArrayList, int, boolean):void");
    }

    private static boolean hasPermission(Context context, int i) {
        if (context == null) {
            Log.m22e(TAG, "hasPermission activity is null");
            return false;
        }
        Iterator<String> it = getPermissionByRequestCode(i).iterator();
        while (it.hasNext()) {
            if (ContextCompat.checkSelfPermission(context, it.next()) != 0) {
                Log.m29v(TAG, "hasPermission requestCode : " + i + " denied");
                return false;
            }
        }
        return true;
    }

    public static boolean checkPermission(AppCompatActivity appCompatActivity, @Nullable ArrayList<Integer> arrayList, boolean z) {
        if (appCompatActivity == null) {
            Log.m22e(TAG, "checkPermission activity is null");
            return false;
        }
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        arrayList2.add(2);
        arrayList2.add(1);
        if (arrayList != null && arrayList.size() > 0) {
            arrayList2.addAll(arrayList);
        }
        Iterator it = arrayList2.iterator();
        boolean z2 = false;
        boolean z3 = true;
        while (it.hasNext()) {
            Integer num = (Integer) it.next();
            Iterator<String> it2 = getPermissionByRequestCode(num.intValue()).iterator();
            while (it2.hasNext()) {
                String next = it2.next();
                if (ContextCompat.checkSelfPermission(appCompatActivity, next) != 0) {
                    Log.m29v(TAG, "checkSelfPermission permission : " + next + " ret : " + "PERMISSION_DENIED");
                    if (!arrayList4.contains(next)) {
                        if (z3) {
                            if (next.equals("android.permission.CALL_PHONE") || next.equals("android.permission.READ_PHONE_STATE")) {
                                z3 = Settings.getBooleanSettings(Settings.KEY_FORCE_SYSTEM_PERMISSION_DIALOG_PHONE, true);
                            } else {
                                z3 = Settings.getBooleanSettings(Settings.KEY_FORCE_SYSTEM_PERMISSION_DIALOG, true);
                            }
                        }
                        arrayList4.add(next);
                        arrayList3.add(num);
                        if (!z2) {
                            z2 = !ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity, next);
                        }
                    }
                }
            }
        }
        if (arrayList4.isEmpty()) {
            Log.m29v(TAG, "checkPermission requested permission size is zero ");
            return true;
        }
        String peek = DialogFactory.peek();
        if (peek != null && peek.equals(DialogFactory.PERMISSION_DIALOG)) {
            return false;
        }
        Log.m26i(TAG, "checkPermission - needAppDialog : " + z2 + " isFirstEnter : " + z3);
        if (!z2 || z3) {
            ActivityCompat.requestPermissions(appCompatActivity, (String[]) arrayList4.toArray(new String[arrayList4.size()]), 1);
        } else {
            runApplicationSettings(appCompatActivity, arrayList3, -1, z);
        }
        return false;
    }

    private static ArrayList<String> getPermissionByRequestCode(int i) {
        ArrayList<String> arrayList = new ArrayList<>();
        if (i == 1) {
            arrayList.add("android.permission.READ_EXTERNAL_STORAGE");
            arrayList.add("android.permission.WRITE_EXTERNAL_STORAGE");
        } else if (i == 2 || i == 3) {
            arrayList.add("android.permission.RECORD_AUDIO");
        } else if (i == 4 || i == 5) {
            arrayList.add("android.permission.CALL_PHONE");
            arrayList.add("android.permission.READ_PHONE_STATE");
        }
        return arrayList;
    }

    public static boolean checkStoragePermission(AppCompatActivity appCompatActivity, int i, int i2) {
        String peek = DialogFactory.peek();
        if (peek != null && peek.equals(DialogFactory.PERMISSION_DIALOG)) {
            return false;
        }
        if (ContextCompat.checkSelfPermission(appCompatActivity, "android.permission.READ_EXTERNAL_STORAGE") == 0 && ContextCompat.checkSelfPermission(appCompatActivity, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            return true;
        }
        if (!ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity, "android.permission.READ_EXTERNAL_STORAGE")) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(Integer.valueOf(i));
            runApplicationSettings(appCompatActivity, arrayList, i2, false);
        } else {
            ActivityCompat.requestPermissions(appCompatActivity, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 1);
        }
        return false;
    }

    public static boolean checkRecordPermission(AppCompatActivity appCompatActivity, int i, int i2) {
        String peek = DialogFactory.peek();
        if (peek != null && peek.equals(DialogFactory.PERMISSION_DIALOG)) {
            return false;
        }
        if (ContextCompat.checkSelfPermission(appCompatActivity, "android.permission.RECORD_AUDIO") == 0) {
            return true;
        }
        if (!ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity, "android.permission.RECORD_AUDIO")) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(Integer.valueOf(i));
            runApplicationSettings(appCompatActivity, arrayList, i2, false);
        } else {
            ActivityCompat.requestPermissions(appCompatActivity, new String[]{"android.permission.RECORD_AUDIO"}, i);
        }
        return false;
    }

    public static boolean checkPhonePermission(AppCompatActivity appCompatActivity, int i, int i2, boolean z) {
        boolean z2;
        Log.m26i(TAG, "checkPhonePermission requestCode : " + i + " text : " + appCompatActivity.getString(i2));
        String peek = DialogFactory.peek();
        if (peek != null && peek.equals(DialogFactory.PERMISSION_DIALOG)) {
            return false;
        }
        String[] strArr = {"android.permission.CALL_PHONE", "android.permission.READ_PHONE_STATE"};
        int length = strArr.length;
        int i3 = 0;
        while (true) {
            if (i3 >= length) {
                z2 = true;
                break;
            }
            String str = strArr[i3];
            if (ContextCompat.checkSelfPermission(appCompatActivity, str) != 0) {
                Log.m29v(TAG, "permission not granted : " + str);
                z2 = false;
                break;
            }
            i3++;
        }
        if (!z2) {
            boolean booleanSettings = Settings.getBooleanSettings(Settings.KEY_FORCE_SYSTEM_PERMISSION_DIALOG_PHONE, true);
            boolean z3 = !ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity, "android.permission.READ_PHONE_STATE");
            Log.m26i(TAG, "checkPhonePermission - needAppDialog : " + z3 + " isFirstEnter : " + booleanSettings);
            if (!z3 || booleanSettings) {
                ActivityCompat.requestPermissions(appCompatActivity, strArr, i);
            } else {
                ArrayList arrayList = new ArrayList();
                arrayList.add(Integer.valueOf(i));
                runApplicationSettings(appCompatActivity, arrayList, i2, z);
            }
            Log.m26i(TAG, "checkPhonePermission permission not granted");
            return false;
        }
        Log.m26i(TAG, "checkPhonePermission all permission granted");
        return true;
    }

    public static boolean checkSavingEnable(Context context) {
        return context != null && hasPermission(context, 1);
    }

    public static boolean isStorageAccessEnable(Context context) {
        return context != null && hasPermission(context, 1);
    }

    public static boolean isRecordEnable(Context context) {
        return context != null && hasPermission(context, 2);
    }

    public static boolean isCallRejectEnable(Context context) {
        return context != null && hasPermission(context, 4);
    }
}
