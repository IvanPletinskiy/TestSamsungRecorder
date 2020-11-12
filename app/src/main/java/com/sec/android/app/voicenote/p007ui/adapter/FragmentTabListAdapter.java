package com.sec.android.app.voicenote.p007ui.adapter;

import android.content.Context;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.sec.android.app.voicenote.p007ui.AbsFragment;
import com.sec.android.app.voicenote.p007ui.RecordingsListFragment;
import com.sec.android.app.voicenote.provider.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/* renamed from: com.sec.android.app.voicenote.ui.adapter.FragmentTabListAdapter */
public class FragmentTabListAdapter extends VoicePagerAdapter {
    private static final String TAG = "FragmentTabListAdapter";
    public static boolean mIsViewCreated = false;
    Context mContext;
    FragmentManager mFragmentManager;

    public FragmentTabListAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.mContext = context;
        this.mFragmentManager = fragmentManager;
        mIsViewCreated = true;
        removeOldChildrenFragment(fragmentManager);
        FragmentFactory.create("ALL_FILES");
        FragmentFactory.create("CATEGORIES");
    }

    public static void removeOldChildrenFragment(FragmentManager fragmentManager) {
        Log.m26i(TAG, "removeOldChildrenFragment");
        if (fragmentManager != null) {
            ArrayList arrayList = new ArrayList();
            arrayList.add("ALL_FILES");
            arrayList.add("CATEGORIES");
            try {
                FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    String str = (String) it.next();
                    AbsFragment absFragment = (AbsFragment) fragmentManager.findFragmentByTag(str);
                    if (absFragment != null) {
                        Log.m26i(TAG, "removeOldChildrenFragment: " + str);
                        beginTransaction.remove(absFragment);
                    }
                    FragmentFactory.remove(str);
                }
                beginTransaction.commitAllowingStateLoss();
            } catch (Exception e) {
                Log.m22e(TAG, e.toString());
            }
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStackImmediate();
            }
        }
        FragmentFactory.removeAll();
    }

    public RecordingsListFragment getListFragment() {
        return (RecordingsListFragment) FragmentFactory.get("ALL_FILES");
    }

    public Fragment getItem(int i) {
        if (i == 0) {
            return FragmentFactory.create("ALL_FILES");
        }
        return FragmentFactory.create("CATEGORIES");
    }

    public void onUpdate(Object obj) {
        Log.m26i(TAG, "onUpdate - data : " + obj);
        AbsFragment absFragment = FragmentFactory.get("ALL_FILES");
        AbsFragment absFragment2 = FragmentFactory.get("CATEGORIES");
        if (absFragment != null) {
            absFragment.onUpdate(obj);
        }
        if (absFragment2 != null) {
            absFragment2.onUpdate(obj);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0009, code lost:
        r0 = com.sec.android.app.voicenote.p007ui.adapter.FragmentTabListAdapter.FragmentFactory.get("ALL_FILES");
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isBackPossible() {
        /*
            r3 = this;
            java.lang.String r0 = "list_mode"
            r1 = 0
            int r0 = com.sec.android.app.voicenote.provider.Settings.getIntSettings(r0, r1)
            if (r0 != 0) goto L_0x0016
            java.lang.String r0 = "ALL_FILES"
            com.sec.android.app.voicenote.ui.AbsFragment r0 = com.sec.android.app.voicenote.p007ui.adapter.FragmentTabListAdapter.FragmentFactory.get(r0)
            if (r0 == 0) goto L_0x0016
            boolean r0 = r0.isBackPossible()
            goto L_0x0017
        L_0x0016:
            r0 = 1
        L_0x0017:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "onBackPress - isBackPossible : "
            r1.append(r2)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "FragmentTabListAdapter"
            com.sec.android.app.voicenote.provider.Log.m26i(r2, r1)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.adapter.FragmentTabListAdapter.isBackPossible():boolean");
    }

    public int getCount() {
        return this.mTabs.length;
    }

    public CharSequence getPageTitle(int i) {
        return this.mTabs[i];
    }

    public void onDestroy() {
        Log.m26i(TAG, "onDestroy");
        removeOldChildrenFragment(this.mFragmentManager);
        this.mFragmentManager = null;
        this.mContext = null;
    }

    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        Log.m26i(TAG, "destroyItem pos:" + i);
        super.destroyItem(viewGroup, i, obj);
    }

    /* renamed from: com.sec.android.app.voicenote.ui.adapter.FragmentTabListAdapter$FragmentFactory */
    private static class FragmentFactory {
        private static final HashMap<String, AbsFragment> mMap = new HashMap<>();

        private FragmentFactory() {
        }

        public static AbsFragment get(String str) {
            return mMap.get(str);
        }

        private static void put(String str, AbsFragment absFragment) {
            if (absFragment != null) {
                mMap.put(str, absFragment);
            }
        }

        public static void remove(String str) {
            mMap.remove(str);
        }

        public static void removeAll() {
            HashMap<String, AbsFragment> hashMap = mMap;
            if (hashMap != null) {
                hashMap.clear();
            }
        }

        public static AbsFragment create(String str) {
            if (get(str) == null) {
                return createFragment(str);
            }
            return get(str);
        }

        /* JADX WARNING: Removed duplicated region for block: B:18:0x0052  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private static com.sec.android.app.voicenote.p007ui.AbsFragment createFragment(java.lang.String r4) {
            /*
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r1 = "createFragment : "
                r0.append(r1)
                r0.append(r4)
                java.lang.String r0 = r0.toString()
                java.lang.String r1 = "FragmentTabListAdapter"
                com.sec.android.app.voicenote.provider.Log.m29v(r1, r0)
                boolean r0 = com.sec.android.app.voicenote.p007ui.adapter.FragmentTabListAdapter.mIsViewCreated
                if (r0 == 0) goto L_0x004f
                r0 = -1
                int r1 = r4.hashCode()
                r2 = -693354983(0xffffffffd6ac3e19, float:-9.4691354E13)
                r3 = 1
                if (r1 == r2) goto L_0x0035
                r2 = 1781608988(0x6a31321c, float:5.3554126E25)
                if (r1 == r2) goto L_0x002b
                goto L_0x003e
            L_0x002b:
                java.lang.String r1 = "CATEGORIES"
                boolean r1 = r4.equals(r1)
                if (r1 == 0) goto L_0x003e
                r0 = r3
                goto L_0x003e
            L_0x0035:
                java.lang.String r1 = "ALL_FILES"
                boolean r1 = r4.equals(r1)
                if (r1 == 0) goto L_0x003e
                r0 = 0
            L_0x003e:
                if (r0 == 0) goto L_0x0049
                if (r0 == r3) goto L_0x0043
                goto L_0x004f
            L_0x0043:
                com.sec.android.app.voicenote.ui.CategoriesListFragment r0 = new com.sec.android.app.voicenote.ui.CategoriesListFragment
                r0.<init>()
                goto L_0x0050
            L_0x0049:
                com.sec.android.app.voicenote.ui.RecordingsListFragment r0 = new com.sec.android.app.voicenote.ui.RecordingsListFragment
                r0.<init>()
                goto L_0x0050
            L_0x004f:
                r0 = 0
            L_0x0050:
                if (r0 == 0) goto L_0x0055
                put(r4, r0)
            L_0x0055:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.adapter.FragmentTabListAdapter.FragmentFactory.createFragment(java.lang.String):com.sec.android.app.voicenote.ui.AbsFragment");
        }
    }
}
