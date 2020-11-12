package com.sec.android.app.voicenote.common.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.p006db.VNDatabase;
import com.sec.android.app.voicenote.provider.CheckedItemProvider;
import com.sec.android.app.voicenote.provider.ContextMenuProvider;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.DesktopModeProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CategoryRepository {
    public static final int CALL_HISTORY_CATEGORY_ID = 3;
    public static final int CUSTOM_CATEGORY_START_ID = 100;
    public static final int ID_MOVE_TO_NONE_CATEGORY = -2;
    public static final int INTERVIEW_CATEGORY_ID = 1;
    public static final int MAX_CATEGORIES_DEFAULT = 4;
    public static final int MAX_CATEGORIES_ID_DEFAULT = 3;
    public static final int NONE_CATEGORY_ID = 0;
    public static final int STT_CATEGORY_ID = 2;
    private static final String TAG = "CategoryRepository";
    public static final String _TABLENAME = "labels";
    public static int mCurrentCategoryId = -1;
    private static CategoryRepository mInstance;
    @SuppressLint({"UseSparseArrays"})
    private static final Map<Integer, CategoryInfo> mLabelList = new HashMap();
    private OnUpdateDBCategory mUpdateCategoryListener = null;
    private final VNDatabase mVNDatabase;

    public static final class LabelColumn {

        /* renamed from: ID */
        public static final String f102ID = "_id";
        public static final String POSITION = "POSITION";
        public static final String TITLE = "TITLE";
    }

    public interface OnUpdateDBCategory {
        void updateListCategory(boolean z);
    }

    public static int getCategoryId(int i) {
        if (i != 2) {
            return i != 4 ? 0 : 2;
        }
        return 1;
    }

    private CategoryRepository(VNDatabase vNDatabase) {
        this.mVNDatabase = vNDatabase;
    }

    public static CategoryRepository getInstance(VNDatabase vNDatabase) {
        if (mInstance == null) {
            synchronized (CategoryRepository.class) {
                if (mInstance == null) {
                    mInstance = new CategoryRepository(vNDatabase);
                }
            }
        }
        return mInstance;
    }

    public void unregisterUpdateCategoryListener() {
        this.mUpdateCategoryListener = null;
    }

    public void registerUpdateCategoryListener(OnUpdateDBCategory onUpdateDBCategory) {
        this.mUpdateCategoryListener = onUpdateDBCategory;
    }

    public static boolean needShowingCallHistoryCategory() {
        return VoiceNoteFeature.FLAG_SUPPORT_CALL_HISTORY || CursorProvider.getInstance().getCallHistoryCount() > 0;
    }

    public static boolean needShowingInterviewCategory() {
        return VoiceNoteFeature.FLAG_SUPPORT_INTERVIEW || CursorProvider.getInstance().getRecordingModeFilesCount(2) > 0;
    }

    public static boolean needShowingSTTCategory(Context context) {
        return VoiceNoteFeature.FLAG_SUPPORT_VOICE_MEMO(context) || CursorProvider.getInstance().getRecordingModeFilesCount(4) > 0;
    }

    private static boolean isMatch(String str, String[] strArr) {
        for (String contains : strArr) {
            if (!str.contains(contains)) {
                return false;
            }
        }
        return true;
    }

    public static StringBuilder getListQuery(Context context, boolean z, List list) {
        Context context2 = context;
        StringBuilder sb = new StringBuilder();
        String categorySearchTag = CursorProvider.getInstance().getCategorySearchTag();
        int i = 1;
        boolean z2 = categorySearchTag != null && !categorySearchTag.isEmpty();
        boolean needShowingCallHistoryCategory = needShowingCallHistoryCategory();
        boolean needShowingInterviewCategory = needShowingInterviewCategory();
        boolean needShowingSTTCategory = needShowingSTTCategory(context);
        if (z2) {
            String[] split = categorySearchTag.toLowerCase().split(" ");
            sb.append("select _id, TITLE, POSITION from labels where ( _id > '");
            sb.append(3);
            sb.append("'");
            sb.append(" AND ");
            for (int i2 = 0; i2 < split.length; i2++) {
                sb.append("TITLE like ?");
                list.add('%' + split[i2] + '%');
                if (i2 < split.length - 1) {
                    sb.append(" AND ");
                }
            }
            sb.append(" )");
            if (needShowingInterviewCategory && isMatch(context2.getString(C0690R.string.category_interview).toLowerCase(), split)) {
                sb.append(" or _id == '");
                sb.append(1);
                sb.append("'");
            }
            if (needShowingSTTCategory && isMatch(context2.getString(C0690R.string.category_speech_to_text).toLowerCase(), split)) {
                sb.append(" or _id == '");
                sb.append(2);
                sb.append("'");
            }
            if (needShowingCallHistoryCategory && isMatch(context2.getString(C0690R.string.category_call_history).toLowerCase(), split)) {
                sb.append(" or _id == '");
                sb.append(3);
                sb.append("'");
            }
            if (isMatch(context2.getString(C0690R.string.uncategorized).toLowerCase(), split)) {
                sb.append(" or _id == '");
                sb.append(0);
                sb.append("'");
            }
        } else {
            sb.append("select _id, TITLE, POSITION from labels where _id >= 0");
            if (!needShowingInterviewCategory) {
                sb.append(" and not _id=='");
                sb.append(1);
                sb.append("'");
            }
            if (!needShowingSTTCategory) {
                sb.append(" and not _id=='");
                sb.append(2);
                sb.append("'");
            }
            if (!needShowingCallHistoryCategory) {
                sb.append(" and not _id=='");
                sb.append(3);
                sb.append("'");
            }
        }
        if (z) {
            int i3 = mCurrentCategoryId;
            Log.m19d(TAG, "currentCategoryId = " + i3);
            if (i3 != -1) {
                if (i3 == -2) {
                    i3 = 0;
                }
                sb.append(" and _id != ");
                sb.append(i3);
            } else {
                ArrayList<Long> checkedItems = CheckedItemProvider.getCheckedItems();
                Log.m19d(TAG, "selectedIDs size = " + checkedItems.size());
                if (checkedItems.size() == 0) {
                    if (ContextMenuProvider.getInstance().getId() != -1 && DesktopModeProvider.isDesktopMode()) {
                        int findLabelID = CursorProvider.findLabelID(context2, ContextMenuProvider.getInstance().getId());
                        if (findLabelID == -2) {
                            findLabelID = 0;
                        }
                        sb.append(" and _id != ");
                        sb.append(findLabelID);
                    }
                } else if (checkedItems.size() == 1) {
                    int findLabelID2 = CursorProvider.findLabelID(context2, checkedItems.get(0).longValue());
                    if (findLabelID2 == -2) {
                        findLabelID2 = 0;
                    }
                    sb.append(" and _id != ");
                    sb.append(findLabelID2);
                } else {
                    long findLabelID3 = (long) CursorProvider.findLabelID(context2, checkedItems.get(0).longValue());
                    while (i < checkedItems.size() && findLabelID3 == ((long) CursorProvider.findLabelID(context2, checkedItems.get(i).longValue()))) {
                        i++;
                    }
                    if (i == checkedItems.size()) {
                        sb.append(" and _id != ");
                        sb.append(findLabelID3);
                    }
                }
            }
        }
        sb.append(" order by POSITION ASC");
        return sb;
    }

    public static StringBuilder getListQueryFromSpinner(Context context) {
        StringBuilder sb = new StringBuilder();
        boolean needShowingCallHistoryCategory = needShowingCallHistoryCategory();
        boolean needShowingInterviewCategory = needShowingInterviewCategory();
        boolean needShowingSTTCategory = needShowingSTTCategory(context);
        sb.append("select _id, TITLE, POSITION from labels where _id >= 0");
        if (!needShowingInterviewCategory) {
            sb.append(" and not _id=='");
            sb.append(1);
            sb.append("'");
        }
        if (!needShowingSTTCategory) {
            sb.append(" and not _id=='");
            sb.append(2);
            sb.append("'");
        }
        if (!needShowingCallHistoryCategory) {
            sb.append(" and not _id=='");
            sb.append(3);
            sb.append("'");
        }
        sb.append(" order by _id ASC");
        return sb;
    }

    public int insertColumn(String str, int i) {
        long j;
        Log.m26i(TAG, "insertColumn");
        VNDatabase vNDatabase = this.mVNDatabase;
        if (vNDatabase == null || !vNDatabase.isOpen()) {
            Log.m26i(TAG, "DB did not opened");
            return -1;
        }
        try {
            CategoryInfo categoryInfo = new CategoryInfo(str, i);
            if (i == 4) {
                categoryInfo.setIdCategory(100);
            }
            j = this.mVNDatabase.mCategoryDao().insertReplace(categoryInfo);
            if (j > -1) {
                try {
                    mLabelList.put(Integer.valueOf((int) j), new CategoryInfo(str));
                    if (this.mUpdateCategoryListener != null) {
                        this.mUpdateCategoryListener.updateListCategory(true);
                    }
                    CursorProvider.getInstance().notifyChangeContent();
                } catch (SQLiteException e) {
                    e = e;
                    Log.m22e(TAG, "insertColumn: error - " + e.toString());
                    return (int) j;
                }
            }
        } catch (SQLiteException e2) {
//            e = e2;
            j = -1;
//            Log.m22e(TAG, "insertColumn: error - " + e.toString());
            return (int) j;
        }
        return (int) j;
    }

    public long insertColumnFromBackup(String str, int i) {
        Log.m26i(TAG, "insertColumn");
        VNDatabase vNDatabase = this.mVNDatabase;
        if (vNDatabase == null || !vNDatabase.isOpen()) {
            Log.m26i(TAG, "DB did not opened");
            return -1;
        }
        try {
            CategoryInfo categoryInfo = new CategoryInfo(str, i);
            if (i == 4) {
                categoryInfo.setIdCategory(100);
            }
            return this.mVNDatabase.mCategoryDao().insertReplace(categoryInfo);
        } catch (SQLiteException e) {
            Log.m22e(TAG, "insertColumn: error - " + e.toString());
            return -1;
        }
    }

    public void updateColumn(int i, String str) {
        Log.m26i(TAG, "updateColumn");
        VNDatabase vNDatabase = this.mVNDatabase;
        if (vNDatabase == null || !vNDatabase.isOpen()) {
            Log.m26i(TAG, "DB did not opened");
            return;
        }
        try {
            if (this.mVNDatabase.mCategoryDao().upDateCategory(str, i) > 0) {
                mLabelList.put(Integer.valueOf(i), new CategoryInfo(str));
                if (this.mUpdateCategoryListener != null) {
                    this.mUpdateCategoryListener.updateListCategory(true);
                }
                CursorProvider.getInstance().notifyChangeContent();
            }
        } catch (SQLiteException e) {
            Log.m22e(TAG, "updateColumn: error - " + e.toString());
        }
    }

    public void updatePosition(List<CategoryInfo> list) {
        Log.m26i(TAG, "updatePosition");
        VNDatabase vNDatabase = this.mVNDatabase;
        if (vNDatabase == null || !vNDatabase.isOpen()) {
            Log.m26i(TAG, "DB did not opened");
            return;
        }
        try {
            if (this.mVNDatabase.mCategoryDao().updateListReplace(list) > 0) {
                CursorProvider.getInstance().notifyChangeContent();
            }
        } catch (SQLiteException e) {
            Log.m22e(TAG, "updatePosition: error - " + e.toString());
        }
        OnUpdateDBCategory onUpdateDBCategory = this.mUpdateCategoryListener;
        if (onUpdateDBCategory != null) {
            onUpdateDBCategory.updateListCategory(true);
        }
    }

    public void deleteColumn(int i) {
        Log.m26i(TAG, "deleteColumn id : " + i);
        VNDatabase vNDatabase = this.mVNDatabase;
        if (vNDatabase == null || !vNDatabase.isOpen()) {
            Log.m26i(TAG, "DB did not opened");
            return;
        }
        try {
            if (this.mVNDatabase.mCategoryDao().deleteDataWithID(i) > 0) {
                mLabelList.remove(Integer.valueOf(i));
            }
            CursorProvider.getInstance().notifyChangeContent();
        } catch (SQLiteException e) {
            Log.m22e(TAG, "deleteColumn: error - " + e.toString());
        }
    }

    public void deleteColums(long[] jArr) {
        Log.m26i(TAG, "deleteColums");
        VNDatabase vNDatabase = this.mVNDatabase;
        if (vNDatabase == null || !vNDatabase.isOpen()) {
            Log.m26i(TAG, "DB did not opened");
            return;
        }
        try {
            for (long j : jArr) {
                int i = (int) j;
                if (this.mVNDatabase.mCategoryDao().deleteDataWithID(i) > 0) {
                    mLabelList.remove(Integer.valueOf(i));
                    CursorProvider.getInstance().notifyChangeContent();
                }
            }
        } catch (SQLiteException e) {
            Log.m22e(TAG, "deleteColumn: error - " + e.toString());
        }
    }

    public void loadLabelToMap() {
        if (this.mVNDatabase == null) {
            Log.m32w(TAG, "loadLabelToMap - mDB is null");
        } else if (mLabelList.size() <= 0) {
            Log.m26i(TAG, "loadLabelToMap");
            mLabelList.clear();
            try {
                List<CategoryInfo> allData = this.mVNDatabase.mCategoryDao().getAllData();
                if (allData != null) {
                    for (int i = 0; i < allData.size(); i++) {
                        mLabelList.put(allData.get(i).getIdCategory(), allData.get(i));
                    }
                }
            } catch (Exception e) {
                Log.m22e(TAG, e.toString());
            }
        }
    }

    public void setCurrentCategoryID(int i) {
        mCurrentCategoryId = i;
    }

    public int getCurrentCategoryId() {
        return mCurrentCategoryId;
    }

    public boolean isChildList() {
        return mCurrentCategoryId >= 0;
    }

    public void resetCategoryId() {
        setCurrentCategoryID(-1);
    }

    private String getCategoryName(int i, Context context) {
        if (context == null) {
            return null;
        }
        if (i == 0) {
            return context.getString(C0690R.string.uncategorized);
        }
        if (i == 1) {
            return context.getString(C0690R.string.category_interview);
        }
        if (i == 2) {
            return context.getString(C0690R.string.category_speech_to_text);
        }
        if (i != 3) {
            return null;
        }
        return context.getString(C0690R.string.category_call_history);
    }

    public String getLabelTitle(int i, Context context) {
        String categoryName = getCategoryName(i, context);
        if (categoryName != null) {
            return categoryName;
        }
        if (mLabelList.size() == 0) {
            loadLabelToMap();
        }
        if (mLabelList.get(Integer.valueOf(i)) == null) {
            return null;
        }
        return mLabelList.get(Integer.valueOf(i)).getTitle();
    }

    public String getCurrentCategoryTitle(Context context) {
        return getLabelTitle(mCurrentCategoryId, context);
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x008c  */
    /* JADX WARNING: Removed duplicated region for block: B:28:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isExitSameTitle(android.content.Context r5, java.lang.String r6) {
        /*
            r4 = this;
            java.lang.String r0 = "CategoryRepository"
            java.lang.String r1 = "isExitSameTitle"
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r1)
            com.sec.android.app.voicenote.common.util.db.VNDatabase r1 = r4.mVNDatabase
            r2 = 0
            if (r1 == 0) goto L_0x008f
            boolean r1 = r1.isOpen()
            if (r1 == 0) goto L_0x008f
            if (r5 != 0) goto L_0x0016
            goto L_0x008f
        L_0x0016:
            r1 = 2131755076(0x7f100044, float:1.9141021E38)
            java.lang.String r1 = r5.getString(r1)
            boolean r1 = r6.equalsIgnoreCase(r1)
            r3 = 1
            if (r1 != 0) goto L_0x008e
            r1 = 2131755079(0x7f100047, float:1.9141027E38)
            java.lang.String r1 = r5.getString(r1)
            boolean r1 = r6.equalsIgnoreCase(r1)
            if (r1 != 0) goto L_0x008e
            r1 = 2131755075(0x7f100043, float:1.914102E38)
            java.lang.String r1 = r5.getString(r1)
            boolean r1 = r6.equalsIgnoreCase(r1)
            if (r1 != 0) goto L_0x008e
            r1 = 2131755624(0x7f100268, float:1.9142133E38)
            java.lang.String r5 = r5.getString(r1)
            boolean r5 = r6.equalsIgnoreCase(r5)
            if (r5 == 0) goto L_0x004c
            goto L_0x008e
        L_0x004c:
            com.sec.android.app.voicenote.common.util.db.VNDatabase r5 = r4.mVNDatabase     // Catch:{ SQLiteException -> 0x0074, UnsupportedOperationException -> 0x005e }
            com.sec.android.app.voicenote.common.util.db.CategoryDao r5 = r5.mCategoryDao()     // Catch:{ SQLiteException -> 0x0074, UnsupportedOperationException -> 0x005e }
            r1 = 3
            java.util.List r5 = r5.checkIsSameTitle(r1, r6)     // Catch:{ SQLiteException -> 0x0074, UnsupportedOperationException -> 0x005e }
            if (r5 == 0) goto L_0x0089
            int r5 = r5.size()     // Catch:{ SQLiteException -> 0x0074, UnsupportedOperationException -> 0x005e }
            goto L_0x008a
        L_0x005e:
            r5 = move-exception
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r1 = "isExitSameTitle - UnsupportedOperationException :"
            r6.append(r1)
            r6.append(r5)
            java.lang.String r5 = r6.toString()
            com.sec.android.app.voicenote.provider.Log.m22e(r0, r5)
            goto L_0x0089
        L_0x0074:
            r5 = move-exception
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r1 = "isExitSameTitle - SQLiteException :"
            r6.append(r1)
            r6.append(r5)
            java.lang.String r5 = r6.toString()
            com.sec.android.app.voicenote.provider.Log.m22e(r0, r5)
        L_0x0089:
            r5 = r2
        L_0x008a:
            if (r5 <= 0) goto L_0x008d
            r2 = r3
        L_0x008d:
            return r2
        L_0x008e:
            return r3
        L_0x008f:
            java.lang.String r5 = "DB did not opened"
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r5)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.common.util.CategoryRepository.isExitSameTitle(android.content.Context, java.lang.String):boolean");
    }

    public void deleteCompleted() {
        OnUpdateDBCategory onUpdateDBCategory = this.mUpdateCategoryListener;
        if (onUpdateDBCategory != null) {
            onUpdateDBCategory.updateListCategory(true);
        }
    }

    public Set<Integer> getAllCategoryId() {
        loadLabelToMap();
        if (mLabelList.isEmpty()) {
            return null;
        }
        return mLabelList.keySet();
    }

    public Map<String, Integer> getAllUserCategory() {
        HashMap hashMap = new HashMap();
        Set<Integer> allCategoryId = getAllCategoryId();
        if (allCategoryId != null) {
            for (Integer next : allCategoryId) {
                if (next.intValue() >= 100) {
                    hashMap.put(mLabelList.get(next).getTitle(), next);
                }
            }
        }
        return hashMap;
    }

    public Map<Integer, CategoryInfo> getLabelList() {
        return mLabelList;
    }
}
