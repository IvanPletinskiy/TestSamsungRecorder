package com.sec.android.app.voicenote.p007ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DataRepository;
import com.sec.android.app.voicenote.common.util.LabelHistorySearchInfo;
import com.sec.android.app.voicenote.common.util.LabelsSearchRepository;
import com.sec.android.app.voicenote.p007ui.adapter.LabelHistorySearchAdapter;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Settings;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.sec.android.app.voicenote.ui.SearchFragment */
public class SearchFragment extends AbsFragment implements LabelsSearchRepository.OnUpdateDBHistorySearch, LabelHistorySearchAdapter.ListenerItemSearchClick {
    private static final String TAG = "SearchFragment";
    private final int SHOW_VIEW_DELAY_TIME = 50;
    private Button mClearAllHistorySearch;
    private RelativeLayout mContentHistorySearch;
    private FrameLayout mContentListSearch;
    private List<LabelHistorySearchInfo> mHistorySearchInfoList;
    private LabelHistorySearchAdapter mLabelHistorySearchAdapter;
    private TextView mNoHistorySearches;
    private RecordingsListSearchFragment mRecordingsListSearchFragment = null;
    private RecyclerView mRecyclerView;
    private View mSearchView;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public void onUpdate(Object obj) {
        int intValue = ((Integer) obj).intValue();
        Log.m26i(TAG, "onUpdate event - " + intValue);
        if (intValue == 959) {
            showHideViewWhenSearch(!CursorProvider.getInstance().getRecordingSearchTag().isEmpty());
        }
        RecordingsListSearchFragment recordingsListSearchFragment = this.mRecordingsListSearchFragment;
        if (recordingsListSearchFragment != null) {
            recordingsListSearchFragment.onUpdate(obj);
        }
    }

    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Log.m26i(TAG, "onCreateView: " + bundle);
        View inflate = layoutInflater.inflate(C0690R.layout.fragment_search, viewGroup, false);
        this.mSearchView = inflate;
        DataRepository.getInstance().getLabelSearchRepository().registerOnUpdateDBHistorySearch(this);
        initView(inflate);
        if (bundle != null) {
            this.mContentHistorySearch.setVisibility(4);
        }
        initListHistorySearch();
        initFragment();
        clearAllHistorySearch();
        return inflate;
    }

    private void clearAllHistorySearch() {
        Button button = this.mClearAllHistorySearch;
        if (button != null) {
            button.setOnClickListener($$Lambda$SearchFragment$DBEjXqkzBXU60An1oGshgZvVxuM.INSTANCE);
        }
    }

    static /* synthetic */ void lambda$clearAllHistorySearch$0(View view) {
        Log.m19d(TAG, "clearAllHistorySearch");
        DataRepository.getInstance().getLabelSearchRepository().deleteAllRowSearchHistory();
    }

    private void initListHistorySearch() {
        this.mHistorySearchInfoList = new ArrayList();
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.mLabelHistorySearchAdapter = new LabelHistorySearchAdapter(getContext(), this.mHistorySearchInfoList, this);
        this.mRecyclerView.setAdapter(this.mLabelHistorySearchAdapter);
        setDataList();
    }

    private void initFragment() {
        Log.m26i(TAG, "initFragment");
        if (isAdded()) {
            this.mRecordingsListSearchFragment = new RecordingsListSearchFragment();
            FragmentTransaction beginTransaction = getChildFragmentManager().beginTransaction();
            beginTransaction.setCustomAnimations(C0690R.animator.depth_in_current_view, C0690R.animator.depth_in_previous_view);
            beginTransaction.replace(C0690R.C0693id.content_list_search, this.mRecordingsListSearchFragment);
            beginTransaction.commitAllowingStateLoss();
        }
    }

    private void setDataList() {
        Log.m26i(TAG, "setDataList");
        this.mHistorySearchInfoList.clear();
        this.mHistorySearchInfoList.addAll(DataRepository.getInstance().getLabelSearchRepository().getAllLabelHistory());
        this.mLabelHistorySearchAdapter.notifyDataSetChanged();
        showHideViewWhenSearch(!CursorProvider.getInstance().getRecordingSearchTag().isEmpty());
    }

    private void initView(View view) {
        this.mClearAllHistorySearch = (Button) view.findViewById(C0690R.C0693id.clear_search_history_text);
        this.mRecyclerView = (RecyclerView) view.findViewById(C0690R.C0693id.recent_search_list);
        this.mRecyclerView.setNestedScrollingEnabled(false);
        this.mContentHistorySearch = (RelativeLayout) view.findViewById(C0690R.C0693id.content_recent_search);
        this.mContentListSearch = (FrameLayout) view.findViewById(C0690R.C0693id.content_list_search);
        this.mNoHistorySearches = (TextView) view.findViewById(C0690R.C0693id.list_empty_history_search_list);
        if (Settings.isEnabledShowButtonBG()) {
            this.mClearAllHistorySearch.setTextColor(getResources().getColor(C0690R.C0691color.main_tab_selected_text_color));
            this.mClearAllHistorySearch.setBackgroundResource(C0690R.C0692drawable.voice_note_clear_search_history_btn);
        }
    }

    private void showHideViewWhenSearch(boolean z) {
        Log.m26i(TAG, "showHideViewWhenSearch - isSearching: " + z);
        if (z) {
            View view = this.mSearchView;
            if (view != null) {
                view.postDelayed(new Runnable() {
                    public final void run() {
                        SearchFragment.this.lambda$showHideViewWhenSearch$1$SearchFragment();
                    }
                }, 50);
                return;
            }
            return;
        }
        List<LabelHistorySearchInfo> list = this.mHistorySearchInfoList;
        if (list == null || list.isEmpty()) {
            RelativeLayout relativeLayout = this.mContentHistorySearch;
            if (!(relativeLayout == null || relativeLayout.getVisibility() == 8)) {
                this.mContentHistorySearch.setVisibility(8);
            }
            TextView textView = this.mNoHistorySearches;
            if (!(textView == null || textView.getVisibility() == 0)) {
                showSoftInputKeyboard();
            }
        } else {
            RelativeLayout relativeLayout2 = this.mContentHistorySearch;
            if (!(relativeLayout2 == null || relativeLayout2.getVisibility() == 0)) {
                this.mContentHistorySearch.setVisibility(0);
            }
            TextView textView2 = this.mNoHistorySearches;
            if (!(textView2 == null || textView2.getVisibility() == 8)) {
                this.mNoHistorySearches.setVisibility(8);
            }
        }
        FrameLayout frameLayout = this.mContentListSearch;
        if (frameLayout != null && frameLayout.getVisibility() != 8) {
            this.mContentListSearch.setVisibility(8);
        }
    }

    public /* synthetic */ void lambda$showHideViewWhenSearch$1$SearchFragment() {
        RelativeLayout relativeLayout = this.mContentHistorySearch;
        if (!(relativeLayout == null || relativeLayout.getVisibility() == 8)) {
            this.mContentHistorySearch.setVisibility(8);
        }
        FrameLayout frameLayout = this.mContentListSearch;
        if (!(frameLayout == null || frameLayout.getVisibility() == 0)) {
            this.mContentListSearch.setVisibility(0);
        }
        TextView textView = this.mNoHistorySearches;
        if (textView != null && textView.getVisibility() != 8) {
            this.mNoHistorySearches.setVisibility(8);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        Log.m26i(TAG, "onDestroy");
        DataRepository.getInstance().getLabelSearchRepository().unrRegisterOnUpdateDBHistorySearch();
        FragmentActivity activity = getActivity();
        if (!(activity == null || activity.getWindow() == null)) {
            activity.getWindow().setSoftInputMode(48);
        }
        this.mRecordingsListSearchFragment = null;
    }

    private void showSoftInputKeyboard() {
        this.mNoHistorySearches.postDelayed(new Runnable() {
            public final void run() {
                SearchFragment.this.lambda$showSoftInputKeyboard$2$SearchFragment();
            }
        }, 50);
    }

    public /* synthetic */ void lambda$showSoftInputKeyboard$2$SearchFragment() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.getWindow().setSoftInputMode(16);
            ((InputMethodManager) activity.getSystemService("input_method")).showSoftInput(this.mNoHistorySearches, 1);
        } else {
            Log.m19d(TAG, "try to show SIP but activity is null");
        }
        this.mNoHistorySearches.setVisibility(0);
    }

    public void updateListHistorySearch() {
        Log.m26i(TAG, "updateListHistorySearch");
        setDataList();
    }

    public void clickItem(int i) {
        Log.m26i(TAG, "clickItem - position: " + i);
        List<LabelHistorySearchInfo> list = this.mHistorySearchInfoList;
        if (list != null && list.size() > i) {
            String label = this.mHistorySearchInfoList.get(i).getLabel();
            Log.m19d(TAG, "clickItem - label: " + label);
            CursorProvider.getInstance().setSearchTag(label);
            postEvent(Event.SEARCH_HISTORY_INPUT);
        }
    }

    public void clickDeleteItem(int i) {
        Log.m26i(TAG, "clickDeleteItem - position: " + i);
        List<LabelHistorySearchInfo> list = this.mHistorySearchInfoList;
        if (list != null && list.size() > i) {
            String label = this.mHistorySearchInfoList.get(i).getLabel();
            Log.m19d(TAG, "clickDeleteItem - label: " + label);
            DataRepository.getInstance().getLabelSearchRepository().deleteSearchHistoryWithName(label);
        }
    }
}
