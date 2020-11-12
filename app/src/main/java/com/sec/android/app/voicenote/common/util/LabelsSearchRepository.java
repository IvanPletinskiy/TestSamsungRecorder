package com.sec.android.app.voicenote.common.util;

import android.database.sqlite.SQLiteException;
import com.sec.android.app.voicenote.common.util.p006db.VNDatabase;
import com.sec.android.app.voicenote.provider.Log;
import java.util.ArrayList;
import java.util.List;

public class LabelsSearchRepository {
    private static final String TAG = "LabelsSearchRepository";
    private static LabelsSearchRepository mInstance;
    private OnUpdateDBHistorySearch mOnUpdateDBHistorySearch = null;
    private final VNDatabase mVNDatabase;

    public interface OnUpdateDBHistorySearch {
        void updateListHistorySearch();
    }

    private LabelsSearchRepository(VNDatabase vNDatabase) {
        this.mVNDatabase = vNDatabase;
    }

    public static LabelsSearchRepository getInstance(VNDatabase vNDatabase) {
        if (mInstance == null) {
            synchronized (LabelsSearchRepository.class) {
                if (mInstance == null) {
                    mInstance = new LabelsSearchRepository(vNDatabase);
                }
            }
        }
        return mInstance;
    }

    public void insertLabel(String str, long j) {
        Log.m26i(TAG, "insertLabel");
        VNDatabase vNDatabase = this.mVNDatabase;
        if (vNDatabase == null || !vNDatabase.isOpen()) {
            Log.m26i(TAG, "DB did not opened");
            return;
        }
        try {
            if ((this.mVNDatabase.mLabelSearchDao().insertReplace(new LabelHistorySearchInfo(str, j)) > -1) && this.mOnUpdateDBHistorySearch != null) {
                this.mOnUpdateDBHistorySearch.updateListHistorySearch();
            }
        } catch (SQLiteException e) {
            Log.m22e(TAG, "insertLabel: error - " + e.toString());
        }
    }

    public List<LabelHistorySearchInfo> getAllLabelHistory() {
        ArrayList arrayList = new ArrayList();
        Log.m26i(TAG, "getAllLabelHistory");
        VNDatabase vNDatabase = this.mVNDatabase;
        if (vNDatabase == null || !vNDatabase.isOpen()) {
            Log.m26i(TAG, "DB did not opened");
            return arrayList;
        }
        try {
            return this.mVNDatabase.mLabelSearchDao().getAllData();
        } catch (SQLiteException e) {
            Log.m22e(TAG, "getAllLabelHistory: error - " + e.toString());
            return arrayList;
        }
    }

    public boolean deleteSearchHistoryWithName(String str) {
        Log.m26i(TAG, "getAllLabelHistory");
        VNDatabase vNDatabase = this.mVNDatabase;
        boolean z = false;
        if (vNDatabase == null || !vNDatabase.isOpen()) {
            Log.m26i(TAG, "DB did not opened");
            return false;
        }
        try {
            if (this.mVNDatabase.mLabelSearchDao().deleteLabelWithName(str) > 0) {
                z = true;
            }
            if (z && this.mOnUpdateDBHistorySearch != null) {
                this.mOnUpdateDBHistorySearch.updateListHistorySearch();
            }
        } catch (SQLiteException e) {
            Log.m22e(TAG, "deleteSearchHistoryWithName: error - " + e.toString());
        }
        return z;
    }

    public void deleteAllRowSearchHistory() {
        Log.m26i(TAG, "deleteAllRowSearchHistory");
        VNDatabase vNDatabase = this.mVNDatabase;
        if (vNDatabase == null || !vNDatabase.isOpen()) {
            Log.m26i(TAG, "DB did not opened");
            return;
        }
        try {
            this.mVNDatabase.mLabelSearchDao().deleteAllData();
            if (this.mOnUpdateDBHistorySearch != null) {
                this.mOnUpdateDBHistorySearch.updateListHistorySearch();
            }
        } catch (SQLiteException e) {
            Log.m22e(TAG, "deleteSearchHistoryWithName: error - " + e.toString());
        }
    }

    public void registerOnUpdateDBHistorySearch(OnUpdateDBHistorySearch onUpdateDBHistorySearch) {
        this.mOnUpdateDBHistorySearch = onUpdateDBHistorySearch;
    }

    public void unrRegisterOnUpdateDBHistorySearch() {
        this.mOnUpdateDBHistorySearch = null;
    }
}
