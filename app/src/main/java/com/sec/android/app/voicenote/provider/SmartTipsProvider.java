package com.sec.android.app.voicenote.provider;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View;

import com.sec.android.app.voicenote.activity.SettingsActivity;

public class SmartTipsProvider {
    public static final String COUT_CANCEL_CALL_WHILE_RECORDING = "count_cancel_call_while_recording";
    public static final String COUT_SHOW_BLOCK_CALLS_TIPS = "cout_show_blocl_calls_tips";
    public static final String NO_TIPS = "no_tips";
    private static final String TAG = "SmartTipsProvider";
    private static volatile SmartTipsProvider mInstance;
//    private SemTipPopup mSemTipPopup = null;

    public static SmartTipsProvider getInstance() {
        if (mInstance == null) {
            synchronized (CallRejectChecker.class) {
                if (mInstance == null) {
                    mInstance = new SmartTipsProvider();
                }
            }
        }
        return mInstance;
    }

    /* JADX WARNING: Removed duplicated region for block: B:39:0x01a7  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x01b9  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void createSmartTips(android.app.Activity r11, android.view.View r12) {
        /*
            r10 = this;
            java.lang.String r0 = "SmartTipsProvider"
            java.lang.String r1 = "createSmartTips()"
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r1)
            if (r11 == 0) goto L_0x01cb
            android.view.Window r1 = r11.getWindow()
            android.view.View r1 = r1.getDecorView()
            android.os.IBinder r1 = r1.getWindowToken()
            if (r1 == 0) goto L_0x01cb
            com.samsung.android.widget.SemTipPopup r1 = r10.mSemTipPopup
            if (r1 == 0) goto L_0x001d
            goto L_0x01cb
        L_0x001d:
            r10.resetRejectCallCount()
            java.lang.String r1 = "cout_show_blocl_calls_tips"
            r10.increaseValueOfKey(r1)
            com.samsung.android.widget.SemTipPopup r1 = new com.samsung.android.widget.SemTipPopup     // Catch:{ Error -> 0x01c2 }
            r1.<init>(r12)     // Catch:{ Error -> 0x01c2 }
            android.content.res.Resources r12 = r11.getResources()
            r2 = 2131100172(0x7f06020c, float:1.7812718E38)
            r3 = 0
            int r12 = r12.getColor(r2, r3)
            r1.setBackgroundColor(r12)
            r12 = 2131755550(0x7f10021e, float:1.9141982E38)
            java.lang.String r12 = r11.getString(r12)
            r1.setMessage(r12)
            r12 = 2131755044(0x7f100024, float:1.9140956E38)
            java.lang.String r12 = r11.getString(r12)
            java.lang.String r12 = r12.toUpperCase()
            com.sec.android.app.voicenote.provider.-$$Lambda$SmartTipsProvider$uiS1sZCW__VK4GxAD6YAcVWN8YA r2 = new com.sec.android.app.voicenote.provider.-$$Lambda$SmartTipsProvider$uiS1sZCW__VK4GxAD6YAcVWN8YA
            r2.<init>(r11)
            r1.setAction(r12, r2)
            android.view.Window r12 = r11.getWindow()
            android.view.View r12 = r12.getDecorView()
            r2 = 16908335(0x102002f, float:2.387736E-38)
            android.view.View r12 = r12.findViewById(r2)
            int r2 = com.sec.android.app.voicenote.common.util.DisplayManager.getVROrientation()
            r3 = 3
            r4 = 0
            r5 = 1
            r6 = 2
            r7 = 2131165922(0x7f0702e2, float:1.7946075E38)
            r8 = 2131165921(0x7f0702e1, float:1.7946073E38)
            if (r2 == 0) goto L_0x0151
            int r2 = com.sec.android.app.voicenote.common.util.DisplayManager.getMultiwindowMode()
            if (r2 != r6) goto L_0x0083
            int r2 = com.sec.android.app.voicenote.common.util.DisplayManager.getVROrientation()
            if (r2 != r6) goto L_0x0083
            goto L_0x0151
        L_0x0083:
            int r0 = com.sec.android.app.voicenote.common.util.DisplayManager.getVROrientation()
            if (r0 != r5) goto L_0x00c4
            android.view.Window r12 = r11.getWindow()
            android.view.View r12 = r12.getDecorView()
            int r12 = r12.getWidth()
            android.content.res.Resources r0 = r11.getResources()
            int r0 = r0.getDimensionPixelOffset(r8)
            int r12 = r12 - r0
            boolean r0 = com.sec.android.app.voicenote.provider.VoiceNoteFeature.FLAG_IS_TABLET
            if (r0 != 0) goto L_0x00b9
            android.content.res.Resources r0 = r11.getResources()
            android.content.res.Resources r2 = r11.getResources()
            java.lang.String r4 = "navigation_bar_height"
            java.lang.String r8 = "dimen"
            java.lang.String r9 = "android"
            int r2 = r2.getIdentifier(r4, r8, r9)
            int r0 = r0.getDimensionPixelOffset(r2)
            int r12 = r12 - r0
        L_0x00b9:
            r4 = r12
            android.content.res.Resources r12 = r11.getResources()
            int r12 = r12.getDimensionPixelOffset(r7)
            goto L_0x0199
        L_0x00c4:
            int r0 = com.sec.android.app.voicenote.common.util.DisplayManager.getMultiwindowMode()
            if (r0 != r6) goto L_0x00f4
            int r0 = com.sec.android.app.voicenote.common.util.DisplayManager.getVROrientation()
            if (r0 != r3) goto L_0x00f4
            android.view.Window r0 = r11.getWindow()
            android.view.View r0 = r0.getDecorView()
            int r0 = r0.getWidth()
            android.content.res.Resources r2 = r11.getResources()
            int r2 = r2.getDimensionPixelOffset(r8)
            int r4 = r0 - r2
            int r12 = r12.getBottom()
            android.content.res.Resources r0 = r11.getResources()
            int r0 = r0.getDimensionPixelOffset(r7)
            goto L_0x0191
        L_0x00f4:
            int r12 = com.sec.android.app.voicenote.common.util.DisplayManager.getMultiwindowMode()
            r0 = 2131165537(0x7f070161, float:1.7945294E38)
            if (r12 != r5) goto L_0x0124
            android.view.Window r12 = r11.getWindow()
            android.view.View r12 = r12.getDecorView()
            int r12 = r12.getWidth()
            android.content.res.Resources r2 = r11.getResources()
            int r2 = r2.getDimensionPixelOffset(r8)
            int r4 = r12 - r2
            android.content.res.Resources r12 = r11.getResources()
            int r12 = r12.getDimensionPixelOffset(r7)
            android.content.res.Resources r2 = r11.getResources()
            int r0 = r2.getDimensionPixelOffset(r0)
            goto L_0x0191
        L_0x0124:
            boolean r12 = com.sec.android.app.voicenote.common.util.DisplayManager.isInDeXExternalMonitor(r11)
            if (r12 == 0) goto L_0x0198
            android.view.Window r12 = r11.getWindow()
            android.view.View r12 = r12.getDecorView()
            int r12 = r12.getWidth()
            android.content.res.Resources r2 = r11.getResources()
            int r2 = r2.getDimensionPixelOffset(r8)
            int r4 = r12 - r2
            android.content.res.Resources r12 = r11.getResources()
            int r12 = r12.getDimensionPixelOffset(r7)
            android.content.res.Resources r2 = r11.getResources()
            int r0 = r2.getDimensionPixelOffset(r0)
            goto L_0x0191
        L_0x0151:
            if (r12 == 0) goto L_0x0193
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "statusWidth = "
            r2.append(r4)
            int r4 = r12.getMeasuredWidth()
            r2.append(r4)
            java.lang.String r4 = ", statusHeight = "
            r2.append(r4)
            int r4 = r12.getBottom()
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r0, r2)
            int r0 = r12.getMeasuredWidth()
            android.content.res.Resources r2 = r11.getResources()
            int r2 = r2.getDimensionPixelOffset(r8)
            int r4 = r0 - r2
            int r12 = r12.getBottom()
            android.content.res.Resources r0 = r11.getResources()
            int r0 = r0.getDimensionPixelOffset(r7)
        L_0x0191:
            int r12 = r12 + r0
            goto L_0x0199
        L_0x0193:
            java.lang.String r12 = "statusBar = null"
            com.sec.android.app.voicenote.provider.Log.m19d(r0, r12)
        L_0x0198:
            r12 = r4
        L_0x0199:
            android.content.res.Resources r0 = r11.getResources()
            android.content.res.Configuration r0 = r0.getConfiguration()
            int r0 = r0.getLayoutDirection()
            if (r0 != r5) goto L_0x01b9
            android.content.res.Resources r11 = r11.getResources()
            r0 = 2131165920(0x7f0702e0, float:1.794607E38)
            int r11 = r11.getDimensionPixelOffset(r0)
            r1.setTargetPosition(r11, r12)
            r1.show(r3)
            goto L_0x01bf
        L_0x01b9:
            r1.setTargetPosition(r4, r12)
            r1.show(r6)
        L_0x01bf:
            r10.mSemTipPopup = r1
            return
        L_0x01c2:
            r11 = move-exception
            java.lang.String r11 = r11.toString()
            com.sec.android.app.voicenote.provider.Log.m22e(r0, r11)
            return
        L_0x01cb:
            java.lang.String r11 = "activity or token is null!!!"
            com.sec.android.app.voicenote.provider.Log.m19d(r0, r11)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.provider.SmartTipsProvider.createSmartTips(android.app.Activity, android.view.View):void");
    }

    static /* synthetic */ void lambda$createSmartTips$1(Activity activity, View view) {
        activity.startActivity(new Intent(activity, SettingsActivity.class));
        new Handler().postDelayed($$Lambda$SmartTipsProvider$qI8m44K4dWm69zPoCMwknMwHKQ.INSTANCE, 1200);
    }

    public boolean isSupportSmartTips() {
//        if (Build.VERSION.SEM_INT >= 2502) {
//            Log.m26i(TAG, "Binary support smart tips");
//            return true;
//        }
        Log.m22e(TAG, "Binary does not support smart tips");
        return false;
    }

    public void increaseValueOfKey(String str) {
        int intSettings = Settings.getIntSettings(str, 0) + 1;
        Settings.setSettings(str, intSettings);
        Log.m26i(TAG, "increaseValueOfKey : " + str + " - value : " + intSettings);
    }

    public void resetRejectCallCount() {
        Settings.setSettings(COUT_CANCEL_CALL_WHILE_RECORDING, 0);
    }

    public boolean isAbleToShowSmartTips() {
        if (Settings.getBooleanSettings(NO_TIPS) || Settings.getIntSettings(COUT_CANCEL_CALL_WHILE_RECORDING, 0) < 2 || Settings.getIntSettings(COUT_SHOW_BLOCK_CALLS_TIPS, 0) > 3) {
            return false;
        }
        return true;
    }

    public void dismissSmartTips(Activity activity) {
        if (activity == null || activity.getWindow().getDecorView().getWindowToken() == null) {
            Log.m19d(TAG, "activity or token is null!!!");
            return;
        }
//        SemTipPopup semTipPopup = this.mSemTipPopup;
//        if (semTipPopup != null) {
//            if (semTipPopup.isShowing()) {
//                Log.m26i(TAG, "dismissSmartTips");
//                this.mSemTipPopup.dismiss(false);
//                this.mSemTipPopup = null;
//            }
//            if (this.mSemTipPopup != null) {
//                this.mSemTipPopup = null;
//            }
//        }
    }
}
