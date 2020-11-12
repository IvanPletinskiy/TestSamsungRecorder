package com.sec.android.app.voicenote.p007ui.adapter;

import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

import com.sec.android.app.voicenote.provider.Log;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;

/* renamed from: com.sec.android.app.voicenote.ui.adapter.VoicePagerAdapter */
public abstract class VoicePagerAdapter extends PagerAdapter {
    protected static final String ALL_FILES = "ALL_FILES";
    protected static final String CATEGORIES = "CATEGORIES";
    private static final String TAG = "VoicePagerAdapter";
    private FragmentTransaction mCurTransaction = null;
    private Fragment mCurrentPrimaryItem = null;
    private final FragmentManager mFragmentManager;
    private ArrayList<Fragment.SavedState> mSavedState = new ArrayList<>();
    protected String[] mTabs = {ALL_FILES, CATEGORIES};

    public abstract Fragment getItem(int i);

    public Parcelable saveState() {
        return null;
    }

    public void startUpdate(ViewGroup viewGroup) {
    }

    public VoicePagerAdapter(FragmentManager fragmentManager) {
        this.mFragmentManager = fragmentManager;
    }

    public Object instantiateItem(ViewGroup viewGroup, int i) {
        Fragment.SavedState savedState;
        if (this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }
        Fragment item = getItem(i);
        if (this.mSavedState.size() > i && (savedState = this.mSavedState.get(i)) != null) {
            item.setInitialSavedState(savedState);
        }
        item.setMenuVisibility(false);
        item.setUserVisibleHint(false);
        this.mCurTransaction.add(viewGroup.getId(), item, this.mTabs[i]);
        return item;
    }

    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        Fragment.SavedState savedState;
        Fragment fragment = (Fragment) obj;
        if (this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }
        while (true) {
            savedState = null;
            if (this.mSavedState.size() > i) {
                break;
            }
            this.mSavedState.add((Fragment.SavedState) null);
        }
        ArrayList<Fragment.SavedState> arrayList = this.mSavedState;
        if (fragment.isAdded()) {
            savedState = this.mFragmentManager.saveFragmentInstanceState(fragment);
        }
        arrayList.set(i, savedState);
        this.mCurTransaction.remove(fragment);
    }

    public void setPrimaryItem(ViewGroup viewGroup, int i, Object obj) {
        Fragment fragment = (Fragment) obj;
        Fragment fragment2 = this.mCurrentPrimaryItem;
        if (fragment != fragment2) {
            if (fragment2 != null) {
                try {
                    fragment2.setMenuVisibility(false);
                    this.mCurrentPrimaryItem.setUserVisibleHint(false);
                } catch (Exception e) {
                    Log.m22e(TAG, e.toString());
                    return;
                }
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            this.mCurrentPrimaryItem = fragment;
        }
    }

    public void finishUpdate(ViewGroup viewGroup) {
        FragmentTransaction fragmentTransaction = this.mCurTransaction;
        if (fragmentTransaction != null) {
            fragmentTransaction.commitAllowingStateLoss();
            this.mCurTransaction = null;
            try {
                this.mFragmentManager.executePendingTransactions();
            } catch (IllegalArgumentException e) {
                Log.m19d(TAG, "finishUpdate() - IllegalArgumentException");
                Log.m22e(TAG, e.toString());
            }
        }
    }

    public boolean isViewFromObject(View view, Object obj) {
        return ((Fragment) obj).getView() == view;
    }

    public void restoreState(Parcelable parcelable, ClassLoader classLoader) {
        super.restoreState(parcelable, classLoader);
    }
}
