package com.sec.android.app.voicenote.p007ui;

import android.animation.Animator;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DataRepository;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.p007ui.RecordingsListFragment;
import com.sec.android.app.voicenote.p007ui.adapter.FragmentTabListAdapter;
import com.sec.android.app.voicenote.p007ui.view.DeativatableViewPager;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.SmartTipsProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.uicore.FragmentController;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

/* renamed from: com.sec.android.app.voicenote.ui.TabListFragment */
public class TabListFragment extends AbsFragment implements FragmentController.OnSceneChangeListener, RecordingsListFragment.OnHeaderClickListener, TabLayout.OnTabSelectedListener {
    private static final int CATEGORIES_LIST = 1;
    private static final String CHILD_LIST_TAG = "child_list";
    private static final int DEFAULT_LIST = 0;
    private static final String TAG = "TabListFragment";
    private static final int TOTAL_PAGE = 2;
    /* access modifiers changed from: private */
    public static final SparseIntArray mListModes = new SparseIntArray();
    private RelativeLayout mChildView = null;
    private int mCurrentScene = 0;
    private boolean mIsDisableAllTab = false;
    private TabLayout mListTabView = null;
    /* access modifiers changed from: private */
    public RelativeLayout mMainView = null;
    private ViewPager.OnPageChangeListener mPageChangeListener = null;
    private FragmentTabListAdapter mTabListAdapter;
    /* access modifiers changed from: private */
    public DeativatableViewPager mViewPager = null;

    public void onTabReselected(TabLayout.Tab tab) {
    }

    public void onTabSelected(TabLayout.Tab tab) {
    }

    public void onTabUnselected(TabLayout.Tab tab) {
    }

    public void onCreate(Bundle bundle) {
        Log.m26i(TAG, "onCreate");
        super.onCreate(bundle);
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Log.m26i(TAG, "onCreateView");
        if (bundle != null) {
            removeChildListFragment();
        }
        View inflate = LayoutInflater.from(getActivity()).inflate(C0690R.layout.fragment_tab_list, viewGroup, false);
        start(inflate);
        FragmentController.getInstance().registerSceneChangeListener(this);
        return inflate;
    }

    public void onDestroyView() {
        Log.m26i(TAG, "onDestroyView");
        SmartTipsProvider.getInstance().dismissSmartTips(getActivity());
        FragmentController.getInstance().unregisterSceneChangeListener(this);
        if (this.mTabListAdapter != null) {
            FragmentTabListAdapter.mIsViewCreated = false;
        }
        this.mViewPager.removeOnPageChangeListener(this.mPageChangeListener);
        super.onDestroyView();
    }

    public void onDestroy() {
        Log.m26i(TAG, "onDestroy");
        super.onDestroy();
    }

    public void onDetach() {
        Log.m26i(TAG, "onDetach");
        FragmentTabListAdapter fragmentTabListAdapter = this.mTabListAdapter;
        if (fragmentTabListAdapter != null) {
            if (fragmentTabListAdapter.getListFragment() != null) {
                this.mTabListAdapter.getListFragment().registerListener((RecordingsListFragment.OnHeaderClickListener) null);
            }
            this.mTabListAdapter.onDestroy();
            this.mTabListAdapter = null;
        }
        this.mViewPager = null;
        removeChildListFragment();
        super.onDetach();
    }

    public void onResume() {
        Log.m26i(TAG, "onResume");
        super.onResume();
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x00ab  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00b2  */
    /* JADX WARNING: Removed duplicated region for block: B:29:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onUpdate(java.lang.Object r6) {
        /*
            r5 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "onUpdate - event: "
            r0.append(r1)
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "TabListFragment"
            com.sec.android.app.voicenote.provider.Log.m26i(r1, r0)
            r0 = r6
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            androidx.fragment.app.FragmentManager r1 = r5.getFragmentManager()
            java.lang.String r2 = "child_list"
            r3 = 0
            if (r1 == 0) goto L_0x002d
            androidx.fragment.app.Fragment r1 = r1.findFragmentByTag(r2)
            com.sec.android.app.voicenote.ui.AbsFragment r1 = (com.sec.android.app.voicenote.p007ui.AbsFragment) r1
            goto L_0x002e
        L_0x002d:
            r1 = r3
        L_0x002e:
            r4 = 3
            if (r0 == r4) goto L_0x00a5
            r4 = 7
            if (r0 == r4) goto L_0x00a5
            r4 = 23
            if (r0 == r4) goto L_0x0099
            r4 = 1004(0x3ec, float:1.407E-42)
            if (r0 == r4) goto L_0x00a5
            switch(r0) {
                case 960: goto L_0x0081;
                case 961: goto L_0x0056;
                case 962: goto L_0x0040;
                default: goto L_0x003f;
            }
        L_0x003f:
            goto L_0x00a8
        L_0x0040:
            com.sec.android.app.voicenote.ui.view.DeativatableViewPager r0 = r5.mViewPager
            if (r0 == 0) goto L_0x00a8
            int r0 = r5.getTabPosition()
            com.sec.android.app.voicenote.ui.view.DeativatableViewPager r2 = r5.mViewPager
            int r2 = r2.getCurrentItem()
            if (r0 == r2) goto L_0x00a8
            com.sec.android.app.voicenote.ui.view.DeativatableViewPager r2 = r5.mViewPager
            r2.setCurrentItem(r0)
            goto L_0x00a8
        L_0x0056:
            com.sec.android.app.voicenote.ui.ChildListFragment r3 = new com.sec.android.app.voicenote.ui.ChildListFragment
            r3.<init>()
            androidx.fragment.app.FragmentManager r0 = r5.getFragmentManager()
            androidx.fragment.app.FragmentTransaction r0 = r0.beginTransaction()
            r1 = 2130837520(0x7f020010, float:1.7279996E38)
            r4 = 2130837521(0x7f020011, float:1.7279998E38)
            r0.setCustomAnimations(r1, r4)
            r1 = 2131296386(0x7f090082, float:1.8210687E38)
            androidx.fragment.app.FragmentTransaction r1 = r0.replace(r1, r3, r2)
            r1.attach(r3)
            r0.commitAllowingStateLoss()
            r0 = 0
            r5.hideMainView(r0)
            r5.showChildrenView()
            goto L_0x00a9
        L_0x0081:
            r5.removeChildListFragment()
            r5.hideChildrenView()
            r5.showMainView()
            r5.setSpaceTab()
            com.sec.android.app.voicenote.common.util.DataRepository r0 = com.sec.android.app.voicenote.common.util.DataRepository.getInstance()
            com.sec.android.app.voicenote.common.util.CategoryRepository r0 = r0.getCategoryRepository()
            r0.resetCategoryId()
            goto L_0x00a9
        L_0x0099:
            com.sec.android.app.voicenote.provider.SmartTipsProvider r0 = com.sec.android.app.voicenote.provider.SmartTipsProvider.getInstance()
            androidx.fragment.app.FragmentActivity r2 = r5.getActivity()
            r0.dismissSmartTips(r2)
            goto L_0x00a8
        L_0x00a5:
            r5.setSpaceTab()
        L_0x00a8:
            r3 = r1
        L_0x00a9:
            if (r3 == 0) goto L_0x00ae
            r3.onUpdate(r6)
        L_0x00ae:
            com.sec.android.app.voicenote.ui.adapter.FragmentTabListAdapter r0 = r5.mTabListAdapter
            if (r0 == 0) goto L_0x00b5
            r0.onUpdate(r6)
        L_0x00b5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.TabListFragment.onUpdate(java.lang.Object):void");
    }

    public void onPause() {
        Log.m26i(TAG, "onPause TabListFragment");
        super.onPause();
        if (this.mTabListAdapter != null) {
            FragmentTabListAdapter.mIsViewCreated = false;
        }
        if (this.mViewPager == null && getFragmentManager() != null) {
            Log.m26i(TAG, "Remove Fragment from pager: " + getFragmentManager().getFragments().size());
            for (Fragment next : getFragmentManager().getFragments()) {
                if ((next instanceof CategoriesListFragment) || (next instanceof RecordingsListFragment)) {
                    getFragmentManager().beginTransaction().remove(next).commit();
                }
            }
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStackImmediate();
            }
        }
    }

    public boolean isBackPossible() {
        if (DataRepository.getInstance().getCategoryRepository().isChildList()) {
            AbsFragment absFragment = (AbsFragment) getFragmentManager().findFragmentByTag(CHILD_LIST_TAG);
            if (absFragment != null) {
                return absFragment.isBackPossible();
            }
            return true;
        }
        FragmentTabListAdapter fragmentTabListAdapter = this.mTabListAdapter;
        if (fragmentTabListAdapter != null) {
            return fragmentTabListAdapter.isBackPossible();
        }
        return true;
    }

    private void removeChildListFragment() {
        Log.m26i(TAG, "removeChildListFragment");
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            try {
                AbsFragment absFragment = (AbsFragment) fragmentManager.findFragmentByTag(CHILD_LIST_TAG);
                if (absFragment != null) {
                    fragmentManager.beginTransaction().remove(absFragment).commitAllowingStateLoss();
                }
            } catch (Exception e) {
                Log.m22e(TAG, e.toString());
            }
        }
    }

    public void start(View view) {
        Log.m26i(TAG, "start");
        if (this.mViewPager == null) {
            enterListModesData();
            init(view);
        }
        clearViewPagerAnimation();
        this.mViewPager.setVisibility(0);
        this.mViewPager.setAlpha(1.0f);
        this.mViewPager.setFocusable(false);
        this.mViewPager.setCurrentItem(getTabPosition(), false);
    }

    private int getTabPosition() {
        return Settings.getIntSettings(Settings.KEY_LIST_MODE, 0) == 0 ? 0 : 1;
    }

    private void init(View view) {
        Log.m26i(TAG, "init ");
        this.mMainView = (RelativeLayout) view.findViewById(C0690R.C0693id.tab_list_layout);
        this.mChildView = (RelativeLayout) view.findViewById(C0690R.C0693id.children_list_layout);
        this.mViewPager = (DeativatableViewPager) view.findViewById(C0690R.C0693id.list_mode_pager);
        try {
            this.mTabListAdapter = new FragmentTabListAdapter(getFragmentManager(), getContext());
            this.mViewPager.setAdapter(this.mTabListAdapter);
        } catch (Exception e) {
            Log.m22e(TAG, "init TabListAdapter error:" + e.getMessage());
        }
        initTab(view);
        if (this.mTabListAdapter.getListFragment() != null) {
            this.mTabListAdapter.getListFragment().registerListener(this);
        }
        if (this.mPageChangeListener == null) {
            this.mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
                public void onPageSelected(int i) {
                    super.onPageSelected(i);
                    Log.m26i(TabListFragment.TAG, "onPageSelected - position : " + i);
                    if (TabListFragment.this.mViewPager == null) {
                        Log.m32w(TabListFragment.TAG, "onPageSelected - pager is null");
                    } else if (TabListFragment.this.mViewPager.getVisibility() == 8) {
                        Log.m32w(TabListFragment.TAG, "onPageSelected - pager is gone");
                    } else {
                        int i2 = TabListFragment.mListModes.get(i);
                        if (i2 != Settings.getIntSettings(Settings.KEY_LIST_MODE, 0)) {
                            Settings.setSettings(Settings.KEY_LIST_MODE, i2);
                            VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.CHANGE_LIST_MODE));
                        }
                    }
                }

                public void onPageScrolled(int i, float f, int i2) {
                    super.onPageScrolled(i, f, i2);
                }
            };
        }
        this.mViewPager.addOnPageChangeListener(this.mPageChangeListener);
        this.mViewPager.setOffscreenPageLimit(2);
        this.mMainView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                TabListFragment.this.mMainView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (SmartTipsProvider.getInstance().isSupportSmartTips() && SmartTipsProvider.getInstance().isAbleToShowSmartTips()) {
                    SmartTipsProvider.getInstance().createSmartTips(TabListFragment.this.getActivity(), TabListFragment.this.mMainView);
                }
            }
        });
    }

    private void setSpaceTab() {
        TabLayout.Tab tabAt;
        if (getActivity() == null) {
            Log.m22e(TAG, "setSpaceTab - Activity is null !!");
            return;
        }
        TabLayout tabLayout = this.mListTabView;
        if (tabLayout != null && (tabAt = tabLayout.getTabAt(0)) != null) {
//            TextView seslGetTextView = tabAt.seslGetTextView();
            final View childAt = ((LinearLayout) this.mListTabView.getChildAt(0)).getChildAt(0);
            int i = -1;
//            if (seslGetTextView != null) {
//                String string = getActivity().getString(C0690R.string.categories);
//                i = (int) seslGetTextView.getPaint().measureText(string, 0, string.length());
//            }
            final int finalI = i;
            this.mListTabView.post(new Runnable() {
                private final /* synthetic */ View f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = childAt;
                    this.f$2 = finalI;
                }

                public final void run() {
                    TabListFragment.this.lambda$setSpaceTab$0$TabListFragment(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$setSpaceTab$0$TabListFragment(View view, int i) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            int paddingEnd = view.getPaddingEnd() + view.getPaddingStart();
            ViewGroup viewGroup = (ViewGroup) this.mListTabView.getChildAt(0);
            int childCount = viewGroup.getChildCount();
            int width = (this.mListTabView.getWidth() - (activity.getResources().getDimensionPixelSize(C0690R.dimen.main_tab_layout_space_size_list) * (childCount - 1))) / childCount;
            Log.m19d(TAG, "width = " + width + " - " + i + " - " + paddingEnd);
            int i2 = i + paddingEnd;
            if (width > i2) {
                i2 = width;
            }
            for (int i3 = 0; i3 < childCount; i3++) {
                ((ViewGroup) viewGroup.getChildAt(i3)).setMinimumWidth(i2);
            }
//            TextView seslGetTextView = this.mListTabView.getTabAt(0).seslGetTextView();
            int dimensionPixelSize = activity.getResources().getDimensionPixelSize(C0690R.dimen.sub_tab_height);
//            int height = seslGetTextView.getHeight();
            int dimensionPixelSize2 = activity.getResources().getDimensionPixelSize(C0690R.dimen.main_sub_tab_padding);
//            Log.m19d(TAG, "defaultHeight = " + dimensionPixelSize + ", newHeight = " + height);
//            if (height == 0) {
//                height = activity.getResources().getDimensionPixelSize(C0690R.dimen.sub_tab_layout_height);
//            }
//            if (dimensionPixelSize < height) {
//                dimensionPixelSize = height + dimensionPixelSize2;
//            }
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mListTabView.getLayoutParams();
            int dimensionPixelSize3 = (activity.getResources().getDimensionPixelSize(C0690R.dimen.main_tablayout_height) - dimensionPixelSize) / 2;
            marginLayoutParams.bottomMargin = dimensionPixelSize3;
            marginLayoutParams.topMargin = dimensionPixelSize3;
            marginLayoutParams.height = dimensionPixelSize;
            this.mListTabView.setLayoutParams(marginLayoutParams);
        }
    }

    private void enterListModesData() {
        mListModes.clear();
        mListModes.put(0, 0);
        mListModes.put(1, 1);
    }

    private void clearViewPagerAnimation() {
        DeativatableViewPager deativatableViewPager = this.mViewPager;
        if (deativatableViewPager != null) {
            Object tag = deativatableViewPager.getTag();
            if (tag instanceof Animator) {
                ((Animator) tag).cancel();
                this.mViewPager.setTag((Object) null);
            }
        }
    }

    private void showChildrenView() {
        RelativeLayout relativeLayout = this.mChildView;
        if (relativeLayout != null) {
            relativeLayout.setVisibility(0);
        }
    }

    private void hideChildrenView() {
        RelativeLayout relativeLayout = this.mChildView;
        if (relativeLayout != null) {
            relativeLayout.setVisibility(8);
        }
    }

    private void showMainView() {
        RelativeLayout relativeLayout = this.mMainView;
        if (relativeLayout != null) {
            relativeLayout.setVisibility(0);
        }
    }

    private void hideMainView(boolean z) {
        RelativeLayout relativeLayout = this.mMainView;
        if (relativeLayout == null) {
            return;
        }
        if (z) {
            relativeLayout.setVisibility(8);
        } else {
            relativeLayout.setVisibility(4);
        }
    }

    public void initTab(View view) {
        this.mListTabView = (TabLayout) view.findViewById(C0690R.C0693id.tab_list);
        updateTabLayoutInTabletMultiWindow();
        this.mListTabView.setupWithViewPager(this.mViewPager);
        this.mListTabView.setTabMode(1);
//        this.mListTabView.seslSetSubTabStyle();
        addTab(0, 2, getActivity().getString(C0690R.string.all));
        addTab(1, 2, getActivity().getString(C0690R.string.categories));
        this.mListTabView.addOnTabSelectedListener((TabLayout.OnTabSelectedListener) this);
        this.mIsDisableAllTab = false;
        setSpaceTab();
    }

    private void updateTabLayoutInTabletMultiWindow() {
        if (VoiceNoteFeature.FLAG_IS_TABLET && DisplayManager.isCurrentWindowOnLandscape(getActivity())) {
            if ((DisplayManager.getMultiwindowMode() == 2 && getResources().getConfiguration().densityDpi < 480) || ((DisplayManager.getMultiwindowMode() != 2 && getResources().getConfiguration().densityDpi >= 480) || (DisplayManager.getMultiwindowMode() == 1 && getResources().getConfiguration().screenWidthDp < 960))) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mListTabView.getLayoutParams();
                int i = (int) (((double) getResources().getDisplayMetrics().widthPixels) * 0.125d);
                marginLayoutParams.rightMargin = i;
                marginLayoutParams.leftMargin = i;
                this.mListTabView.setLayoutParams(marginLayoutParams);
            }
        }
    }

    private void addTab(int i, int i2, String str) {
        TextView seslGetTextView;
        TabLayout.Tab tabAt = this.mListTabView.getTabAt(i);
//        if (tabAt != null && (seslGetTextView = tabAt.seslGetTextView()) != null) {
//            seslGetTextView.setTextSize(0, getResources().getDimension(C0690R.dimen.sub_tab_text_size));
//            seslGetTextView.setText(str);
//            seslGetTextView.setContentDescription(str);
//        }
    }

    private void disableTab(int i, boolean z) {
        View childAt = ((ViewGroup) this.mListTabView.getChildAt(0)).getChildAt(i);
        if (childAt != null) {
            childAt.setEnabled(!z);
            if (z) {
                childAt.setAlpha(0.6f);
                childAt.setImportantForAccessibility(2);
                return;
            }
            childAt.setAlpha(1.0f);
            childAt.setImportantForAccessibility(1);
        }
    }

    private void setAccessibitityOfTextView(int i, boolean z) {
        TextView seslGetTextView;
        TabLayout.Tab tabAt = this.mListTabView.getTabAt(i);
//        if (tabAt != null && (seslGetTextView = tabAt.seslGetTextView()) != null) {
//            seslGetTextView.setImportantForAccessibility(!z ? 1 : 2);
//        }
    }

    public void onSceneChange(int i) {
        this.mCurrentScene = i;
        if (this.mCurrentScene == 7) {
            hideMainView(true);
            hideChildrenView();
        }
        if (i != 2) {
            disableAllTab();
        } else {
            enableAllTab();
        }
    }

    public void onHeaderClick() {
        disableAllTab();
    }

    private void disableAllTab() {
        Log.m26i(TAG, "disableAllTab");
        int i = this.mCurrentScene;
        if (i == 2 || i == 5 || i == 3 || i == 13) {
            this.mMainView.setBackground(new ColorDrawable(getActivity().getResources().getColor(C0690R.C0691color.search_main_background_color, (Resources.Theme) null)));
            this.mChildView.setBackground(new ColorDrawable(getActivity().getResources().getColor(C0690R.C0691color.search_main_background_color, (Resources.Theme) null)));
        } else {
            this.mMainView.setBackground((Drawable) null);
            this.mChildView.setBackground((Drawable) null);
        }
        if (!this.mIsDisableAllTab) {
            this.mListTabView.setEnabled(false);
            this.mViewPager.setPagingEnable(false);
            disableTab(0, true);
            setAccessibitityOfTextView(0, true);
            disableTab(1, true);
            setAccessibitityOfTextView(1, true);
            this.mIsDisableAllTab = true;
            this.mListTabView.setImportantForAccessibility(2);
        }
    }

    private void enableAllTab() {
        Log.m26i(TAG, "enableAllTab");
        this.mMainView.setBackground(new ColorDrawable(getActivity().getResources().getColor(C0690R.C0691color.search_main_background_color, (Resources.Theme) null)));
        this.mChildView.setBackground(new ColorDrawable(getActivity().getResources().getColor(C0690R.C0691color.search_main_background_color, (Resources.Theme) null)));
        this.mListTabView.setEnabled(true);
        this.mViewPager.setPagingEnable(true);
        disableTab(0, false);
        setAccessibitityOfTextView(0, false);
        disableTab(1, false);
        setAccessibitityOfTextView(1, false);
        this.mIsDisableAllTab = false;
        this.mListTabView.setImportantForAccessibility(1);
    }
}
