package com.sec.android.app.voicenote.provider;

import android.view.View;

public class ContextMenuFactory {
    private static final String TAG = "ContextMenuFactory";

    private ContextMenuFactory() {
    }

    public static void showContextMenuForView(int i, int i2, View view) {
        try {
            Log.m26i(TAG, "showContextMenuForView");
            view.showContextMenu((float) i, (float) i2);
        } catch (Exception e) {
            Log.m24e(TAG, "Exception", (Throwable) e);
        }
    }
}
