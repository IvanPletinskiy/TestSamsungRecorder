package com.sec.android.app.voicenote.p007ui;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import com.sec.android.app.voicenote.p007ui.adapter.ListAdapter;
import com.sec.android.app.voicenote.provider.CursorProvider;

/* renamed from: com.sec.android.app.voicenote.ui.RecordingsListSearchFragment */
public class RecordingsListSearchFragment extends AbsListFragment {
    private OnHeaderClickListener mListener = null;

    /* renamed from: com.sec.android.app.voicenote.ui.RecordingsListSearchFragment$OnHeaderClickListener */
    interface OnHeaderClickListener {
        void onHeaderClick();
    }

    public RecordingsListSearchFragment() {
        setTAG("RecordingsListSearchFragment");
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(false);
    }

    public void setNestedScrollRecyclerView(boolean z) {
        super.setNestedScrollRecyclerView(z);
    }

    public void registerListener(OnHeaderClickListener onHeaderClickListener) {
        this.mListener = onHeaderClickListener;
    }

    public boolean onHeaderClick(View view, int i) {
        OnHeaderClickListener onHeaderClickListener = this.mListener;
        if (onHeaderClickListener != null) {
            onHeaderClickListener.onHeaderClick();
        }
        return super.onHeaderClick(view, i);
    }

    public void onUpdate(Object obj) {
        ListAdapter listAdapter;
        int intValue = ((Integer) obj).intValue();
        if (intValue == 961 && (listAdapter = this.mListAdapter) != null) {
            listAdapter.swapCursor((Cursor) null);
        }
        if (intValue == 960) {
            CursorProvider.getInstance().registerCursorChangeListener(this);
        }
        super.onUpdate(obj);
    }
}
