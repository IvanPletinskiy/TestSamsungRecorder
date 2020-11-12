package com.sec.android.app.voicenote.provider;

import android.util.LongSparseArray;
import java.util.ArrayList;

public class CheckedItemProvider {
    protected static LongSparseArray<Boolean> mCheckedIDArray = new LongSparseArray<>();

    public static void initCheckedList() {
        mCheckedIDArray.clear();
    }

    public static void setChecked(long j, boolean z) {
        mCheckedIDArray.put(j, Boolean.valueOf(z));
    }

    public static boolean isChecked(long j) {
        return mCheckedIDArray.get(j, false).booleanValue();
    }

    public static void toggle(long j) {
        if (mCheckedIDArray.indexOfKey(j) < 0) {
            mCheckedIDArray.put(j, true);
        } else {
            mCheckedIDArray.delete(j);
        }
    }

    public static ArrayList<Long> getCheckedItems() {
        ArrayList<Long> arrayList = new ArrayList<>();
        int size = mCheckedIDArray.size();
        for (int i = 0; i < size; i++) {
            if (mCheckedIDArray.valueAt(i).booleanValue()) {
                arrayList.add(Long.valueOf(mCheckedIDArray.keyAt(i)));
            }
        }
        return arrayList;
    }

    public static int getCheckedItemCount() {
        return mCheckedIDArray.size();
    }
}
