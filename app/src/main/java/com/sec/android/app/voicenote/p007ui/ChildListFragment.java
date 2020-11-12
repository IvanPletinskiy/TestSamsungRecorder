package com.sec.android.app.voicenote.p007ui;

import android.os.Bundle;

/* renamed from: com.sec.android.app.voicenote.ui.ChildListFragment */
public class ChildListFragment extends AbsListFragment {
    public ChildListFragment() {
        setTAG("ChildListFragment");
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(false);
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
