package com.sec.android.app.voicenote.provider;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;

import com.sec.android.app.voicenote.common.util.CategoryRepository;
import com.sec.android.app.voicenote.common.util.DataRepository;
import com.sec.android.app.voicenote.common.util.TextData;
import com.sec.android.app.voicenote.common.util.Trace;
import com.sec.android.app.voicenote.service.BookmarkHolder;
import com.sec.android.app.voicenote.service.codec.M4aReader;
import com.sec.android.app.voicenote.service.helper.SttHelper;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class CursorProvider {
    private static final int ALL_RECORDINGS_LOADER_ID = 0;
    private static final int CATEGORIES_LOADER_ID = 2;
    private static final int CHILD_RECORDINGS_LOADER_ID = 1;
    public static final int INVALID_FILE_COUNT = -1;
    /* access modifiers changed from: private */
    public static final String[] LISTITEM_SUMMARY_PROJECTION = {CategoryRepository.LabelColumn.f102ID, "title", "datetaken", "date_modified", "duration", "mime_type", "_data", NFCProvider.NFC_DB_KEY, "recording_mode"};
    public static final Uri SAMSUNG_EXTERNAL_CONTENT_URI = Uri.parse("content://secmedia/audio/media");
    private static final int SIMPLE_RECORDING_LOADER_ID = 3;
    private static final String TAG = "CursorProvider";
    private static final int UNDEFINED = -1;
    private static volatile CursorProvider mInstance = null;
    private static final Uri uri = Uri.parse("content://com.sec.android.app.voicenote.provider.CategoryDBProvider");
    /* access modifiers changed from: private */
    public Context mAppContext;
    private int mCallHistoryCount = 0;
    /* access modifiers changed from: private */
    public Cursor mCategoryCursor;
    /* access modifiers changed from: private */
    public OnCategoryCursorChangeListener mCategoryCursorChangeListener = null;
    private LoaderManager.LoaderCallbacks<Cursor> mCategoryLoaderCallback;
    private String mCategorySearchTag = "";
    /* access modifiers changed from: private */
    public ContentObserver mContentObserver = new ContentObserver((Handler) null) {
        public void onChange(boolean z) {
            Log.m26i(CursorProvider.TAG, "ContentObserver : onChange - " + z);
            int updatedItemCount = CursorProvider.this.updatedItemCount();
            boolean z2 = false;
            boolean z3 = CursorProvider.this.getCurrentFileCount() == -1 || (updatedItemCount > 0 && CursorProvider.this.getCurrentFileCount() == 0) || (updatedItemCount == 0 && CursorProvider.this.getCurrentFileCount() > 0);
            Log.m26i(CursorProvider.TAG, "updateFileCount = " + updatedItemCount + ", currentFileCount = " + CursorProvider.this.getCurrentFileCount() + " - needUpdate: " + z3);
            CursorProvider.this.setCurrentFileCount(updatedItemCount);
            if (z3 && VoiceNoteApplication.getScene() == 1) {
                VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.INVALIDATE_MENU));
            }
            if (CursorProvider.this.mCursorChangeListener != null && !CursorProvider.this.mIsMoveFileToTrash) {
                for (String str : CursorProvider.this.getAllFilePath()) {
                    if (str != null && !new File(str).exists()) {
                        BookmarkHolder.getInstance().remove(str);
                        z2 = true;
                    }
                }
                if (z2) {
                    CursorProvider.this.mCursorChangeListener.notifyDataSetChanged(CursorProvider.this.mCursor);
                }
            }
            if (CursorProvider.this.mSimpleCursorChangeListener != null) {
                CursorProvider.this.mSimpleCursorChangeListener.notifyDataSetChanged();
            }
            super.onChange(z);
        }
    };
    private int mCurrentFileCount = -1;
    private int mCurrentPlayingPosition = -1;
    /* access modifiers changed from: private */
    public Cursor mCursor = null;
    /* access modifiers changed from: private */
    public OnCursorChangeListener mCursorChangeListener = null;
    /* access modifiers changed from: private */
    public DataSetObserver mDataSetObserver = new DataSetObserver() {
        public void onChanged() {
            Log.m26i(CursorProvider.TAG, "onChanged");
            super.onChanged();
        }

        public void onInvalidated() {
            Log.m26i(CursorProvider.TAG, "onInvalidated");
            if (CursorProvider.this.mCursor != null) {
                CursorProvider.this.mCursor.unregisterDataSetObserver(CursorProvider.this.mDataSetObserver);
                CursorProvider.this.mCursor.unregisterContentObserver(CursorProvider.this.mContentObserver);
                Cursor unused = CursorProvider.this.mCursor = null;
            }
            if (CursorProvider.this.mCursorChangeListener != null) {
                CursorProvider.this.mCursorChangeListener.notifyDataSetInvalidated((Cursor) null);
                OnCursorChangeListener unused2 = CursorProvider.this.mCursorChangeListener = null;
            }
            super.onInvalidated();
        }
    };
    private volatile boolean mDoneUpdateFilesCount = true;
    private int mInterviewFileCount = 0;
    /* access modifiers changed from: private */
    public boolean mIsMoveFileToTrash = false;
    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback;
    private int mMemoFileCount = 0;
    private AtomicInteger mRecordFileCount = initRecordFileCount();
    /* access modifiers changed from: private */
    public String mRecordingSearchTag = "";
    private ArrayList<Long> mSearchListIds = new ArrayList<>();
    private String mSearchResult = "";
    /* access modifiers changed from: private */
    public Cursor mSimpleCursor = null;
    /* access modifiers changed from: private */
    public OnSimpleCursorChangeListener mSimpleCursorChangeListener = null;
    private LoaderManager.LoaderCallbacks<Cursor> mSimpleLoaderCallback;
    private int mUnCategorizedFileCount = 0;

    public interface OnCategoryCursorChangeListener {
        void notifyDataSetChanged();

        void onCursorChanged(Cursor cursor);

        void onLoadReset();
    }

    public interface OnCursorChangeListener {
        void notifyDataSetChanged(Cursor cursor);

        void notifyDataSetInvalidated(Cursor cursor);

        void onCursorChanged(Cursor cursor, boolean z);

        void onCursorLoadFail();
    }

    public interface OnSimpleCursorChangeListener {
        void notifyDataSetChanged();
    }

    private AtomicInteger initRecordFileCount() {
        Log.m19d(TAG, "initRecordFileCount : -1");
        return new AtomicInteger(-1);
    }

    private CursorProvider() {
        Log.m19d(TAG, "CursorProvider creator !!");
    }

    public static CursorProvider getInstance() {
        if (mInstance == null) {
            synchronized (CursorProvider.class) {
                if (mInstance == null) {
                    mInstance = new CursorProvider();
                }
            }
        }
        return mInstance;
    }

    public void setApplicationContext(Context context) {
        this.mAppContext = context;
    }

    public void registerContentObservers() {
        ContentResolver contentResolver;
        if (this.mAppContext == null) {
            this.mAppContext = VoiceNoteApplication.getApplication();
        }
        Context context = this.mAppContext;
        if (context != null && (contentResolver = context.getContentResolver()) != null) {
            contentResolver.registerContentObserver(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, false, this.mContentObserver);
        }
    }

    public void unregisterContentObservers() {
        ContentResolver contentResolver;
        Log.m19d(TAG, "unregisterContentObservers");
        Context context = this.mAppContext;
        if (context != null && (contentResolver = context.getContentResolver()) != null) {
            contentResolver.unregisterContentObserver(this.mContentObserver);
        }
    }

    public void registerCursorChangeListener(OnCursorChangeListener onCursorChangeListener) {
        this.mCursorChangeListener = onCursorChangeListener;
    }

    public void unregisterCursorChangeListener(OnCursorChangeListener onCursorChangeListener) {
        if (this.mCursorChangeListener == onCursorChangeListener) {
            this.mCursorChangeListener = null;
        }
    }

    public void registerCategoryCursorChangeListener(OnCategoryCursorChangeListener onCategoryCursorChangeListener) {
        this.mCategoryCursorChangeListener = onCategoryCursorChangeListener;
    }

    public void unregisterCategoryCursorChangeListener(OnCategoryCursorChangeListener onCategoryCursorChangeListener) {
        if (this.mCategoryCursorChangeListener == onCategoryCursorChangeListener) {
            this.mCategoryCursorChangeListener = null;
        }
    }

    public void registerSimpleCursorChangeListener(OnSimpleCursorChangeListener onSimpleCursorChangeListener) {
        this.mSimpleCursorChangeListener = onSimpleCursorChangeListener;
    }

    public void unregisterSimpleCursorChangeListener(OnSimpleCursorChangeListener onSimpleCursorChangeListener) {
        if (this.mSimpleCursorChangeListener == onSimpleCursorChangeListener) {
            this.mSimpleCursorChangeListener = null;
        }
    }

    public void loadRecordFileCountInBG() {
        Trace.beginSection("CP.loadFileCountBG");
        Log.m26i("VNMainActivity", "loadFileCountInBG - Start");
        this.mDoneUpdateFilesCount = false;
        int updatedItemCount = getInstance().updatedItemCount();
        this.mDoneUpdateFilesCount = true;
        Log.m26i("VNMainActivity", "loadFileCountInBG - End:" + updatedItemCount);
        Trace.endSection();
    }

    public void setRecordFileCount(int i) {
        AtomicInteger atomicInteger = this.mRecordFileCount;
        if (atomicInteger != null) {
            atomicInteger.set(i);
        }
    }

    public int getRecordFileCount() {
        AtomicInteger atomicInteger = this.mRecordFileCount;
        if (atomicInteger != null) {
            return atomicInteger.get();
        }
        return -1;
    }

    public boolean isDoneUpdatingFilesCount() {
        return this.mDoneUpdateFilesCount;
    }

    public void setIsMovingFileToTrashTask(boolean z) {
        this.mIsMoveFileToTrash = z;
    }

    private static class CursorLoaderTask extends CursorLoader {
        public static final String TAG = "CursorLoaderTask";

        CursorLoaderTask(Context context, Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
            super(context, uri, strArr, str, strArr2, str2);
        }

        /* access modifiers changed from: protected */
        public void onReset() {
            Log.m26i(TAG, "onReset");
            onStopLoading();
        }
    }

    public void notifyChangeContent() {
        Context context = this.mAppContext;
        if (context != null) {
            context.getContentResolver().notifyChange(uri, (ContentObserver) null);
        }
    }

    public void load(LoaderManager loaderManager) {
        if (PermissionProvider.isStorageAccessEnable(this.mAppContext)) {
            clearSearchListIds();
            if (this.mLoaderCallback == null) {
                this.mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
                    public CursorLoader onCreateLoader(int i, Bundle bundle) {
                        StringBuilder sb;
                        Log.m26i(CursorProvider.TAG, "onCreateLoader mRecordingSearchTag : " + CursorProvider.this.mRecordingSearchTag);
                        if (CursorProvider.this.mRecordingSearchTag == null || CursorProvider.this.mRecordingSearchTag.isEmpty()) {
                            sb = CursorProvider.this.getListQuery();
                        } else {
                            sb = new StringBuilder();
                            ArrayList access$800 = CursorProvider.this.getSearchListIds();
                            if (access$800.isEmpty()) {
                                sb.append("(_id=");
                                sb.append(-1);
                                sb.append(')');
                            } else {
                                sb.append("_id IN (");
                                for (int i2 = 0; i2 < access$800.size(); i2++) {
                                    sb.append(access$800.get(i2));
                                    if (i2 < access$800.size() - 1) {
                                        sb.append(",");
                                    }
                                }
                                sb.append(")");
                            }
                        }
                        return new CursorLoaderTask(CursorProvider.this.mAppContext, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, CursorProvider.LISTITEM_SUMMARY_PROJECTION, sb.toString(), (String[]) null, CursorProvider.getSortQuery());
                    }

                    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                        int id = loader.getId();
                        Log.m26i(CursorProvider.TAG, "onLoadFinished loader id = " + id);
//                        if (DataRepository.getInstance().getCategoryRepository().isChildList() == id) {
//                            if (cursor == null) {
//                                Log.m22e(CursorProvider.TAG, "onLoadFinished - new cursor is NULL");
//                            } else if (cursor.isClosed()) {
//                                Log.m32w(CursorProvider.TAG, "onLoadFinished - new cursor is closed");
//                                if (CursorProvider.this.mCursorChangeListener != null) {
//                                    CursorProvider.this.mCursorChangeListener.onCursorLoadFail();
//                                }
//                            } else {
//                                try {
//                                    cursor.registerDataSetObserver(CursorProvider.this.mDataSetObserver);
//                                    cursor.registerContentObserver(CursorProvider.this.mContentObserver);
//                                } catch (IllegalStateException unused) {
//                                    Log.m32w(CursorProvider.TAG, "cursor is already registered");
//                                }
//                                if (CursorProvider.this.mCursorChangeListener != null) {
//                                    OnCursorChangeListener access$300 = CursorProvider.this.mCursorChangeListener;
//                                    CursorProvider cursorProvider = CursorProvider.this;
//                                    access$300.onCursorChanged(cursor, cursorProvider.isUpdate(cursorProvider.mCursor, cursor));
//                                }
//                                if (!(CursorProvider.this.mCursor == null || CursorProvider.this.mCursor.isClosed() || CursorProvider.this.mCursor == cursor)) {
//                                    Log.m32w(CursorProvider.TAG, "onLoadFinished - prior cursor should be closed");
//                                    CursorProvider.this.close();
//                                }
//                                Cursor unused2 = CursorProvider.this.mCursor = cursor;
//                            }
//                        }
                    }

                    public void onLoaderReset(Loader loader) {
                        Log.m26i(CursorProvider.TAG, "onLoaderReset");
                    }
                };
            }
            boolean isChildList = DataRepository.getInstance().getCategoryRepository().isChildList();
            loaderManager.initLoader(isChildList ? 1 : 0, (Bundle) null, this.mLoaderCallback);
        }
    }

    public void loadSimple(LoaderManager loaderManager, final long j) {
        if (PermissionProvider.isStorageAccessEnable(this.mAppContext)) {
            if (this.mSimpleLoaderCallback == null) {
                this.mSimpleLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
                    public CursorLoader onCreateLoader(int i, Bundle bundle) {
                        return new CursorLoaderTask(CursorProvider.this.mAppContext, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, CursorProvider.LISTITEM_SUMMARY_PROJECTION, "_id=" + j, (String[]) null, (String) null);
                    }

                    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                        if (cursor == null) {
                            Log.m22e(CursorProvider.TAG, "onLoadFinished - new cursor is NULL");
                            return;
                        }
                        try {
                            cursor.registerDataSetObserver(CursorProvider.this.mDataSetObserver);
                            cursor.registerContentObserver(CursorProvider.this.mContentObserver);
                        } catch (IllegalStateException unused) {
                            Log.m32w(CursorProvider.TAG, "simple cursor is already registered");
                        }
                        if (!(CursorProvider.this.mSimpleCursor == null || CursorProvider.this.mSimpleCursor.isClosed() || CursorProvider.this.mSimpleCursor == cursor)) {
                            Log.m32w(CursorProvider.TAG, "onLoadFinished - prior cursor should be closed");
                            CursorProvider.this.mSimpleCursor.unregisterDataSetObserver(CursorProvider.this.mDataSetObserver);
                            CursorProvider.this.mSimpleCursor.unregisterContentObserver(CursorProvider.this.mContentObserver);
                            CursorProvider.this.mSimpleCursor.close();
                            Cursor unused2 = CursorProvider.this.mSimpleCursor = null;
                        }
                        Cursor unused3 = CursorProvider.this.mSimpleCursor = cursor;
                    }

                    public void onLoaderReset(Loader loader) {
                        Log.m26i(CursorProvider.TAG, "onLoaderReset");
                    }
                };
            }
            loaderManager.initLoader(3, (Bundle) null, this.mSimpleLoaderCallback);
        }
    }

    public void reload(LoaderManager loaderManager) {
        Log.m26i(TAG, "reload");
        clearSearchListIds();
        boolean isChildList = DataRepository.getInstance().getCategoryRepository().isChildList();
        if (this.mLoaderCallback != null && PermissionProvider.isStorageAccessEnable(this.mAppContext)) {
            loaderManager.restartLoader(isChildList ? 1 : 0, (Bundle) null, this.mLoaderCallback);
        }
    }

    public void query(LoaderManager loaderManager, String str) {
        Log.m26i(TAG, "query - tag : " + str);
        clearSearchListIds();
        this.mRecordingSearchTag = str;
        boolean isChildList = DataRepository.getInstance().getCategoryRepository().isChildList();
        if (PermissionProvider.isStorageAccessEnable(this.mAppContext)) {
            loaderManager.restartLoader(isChildList ? 1 : 0, (Bundle) null, this.mLoaderCallback);
        }
    }

    public void close() {
        Log.m26i(TAG, "close");
        Cursor cursor = this.mCursor;
        if (cursor != null) {
            cursor.unregisterDataSetObserver(this.mDataSetObserver);
            this.mCursor.unregisterContentObserver(this.mContentObserver);
            this.mCursor.close();
            this.mCursor = null;
        }
    }

    public StringBuilder getBaseQuery() {
        StringBuilder sb = new StringBuilder();
        sb.append("((_data LIKE '%.3ga' and is_music == '0') or ");
        sb.append("_data LIKE '%.amr' or (_data LIKE '%.m4a' and recordingtype == '1'))");
        sb.append(" and (_data NOT LIKE '%/.393857/%')");
        sb.append(" and (mime_type LIKE 'audio/3gpp' or mime_type LIKE 'audio/amr' or mime_type LIKE 'audio/mp4' or mime_type LIKE 'audio/mpeg')");
        sb.append(" and (_size != '0')");
        return sb;
    }

    public StringBuilder getBackupListQuery() {
        return getBaseQuery();
    }

    /* access modifiers changed from: package-private */
    public StringBuilder getListQuery() {
        StringBuilder baseQuery = getBaseQuery();
        int currentCategoryId = DataRepository.getInstance().getCategoryRepository().getCurrentCategoryId();
        Log.m19d(TAG, "CategoryId = " + currentCategoryId);
        if (currentCategoryId == -2) {
            currentCategoryId = 0;
        }
        if (currentCategoryId >= 0) {
            if (currentCategoryId == 0) {
                baseQuery.append(" and ((recorded_number is null");
                baseQuery.append(" and recording_mode != '");
                baseQuery.append(2);
                baseQuery.append("'");
                baseQuery.append(" and recording_mode != '");
                baseQuery.append(4);
                baseQuery.append("'");
                baseQuery.append(" and (label_id == '");
                baseQuery.append(0);
                baseQuery.append("'");
                baseQuery.append("or label_id is null))");
                baseQuery.append(" or label_id == '");
                baseQuery.append(-2);
                baseQuery.append("')");
            } else if (currentCategoryId == 1) {
                baseQuery.append(" and ((recording_mode == '");
                baseQuery.append(2);
                baseQuery.append("'");
                baseQuery.append(" and (label_id == '");
                baseQuery.append(0);
                baseQuery.append("'");
                baseQuery.append(" or label_id is null))");
                baseQuery.append(" or label_id == '");
                baseQuery.append(1);
                baseQuery.append("')");
            } else if (currentCategoryId == 2) {
                baseQuery.append(" and ((recording_mode == '");
                baseQuery.append(4);
                baseQuery.append("'");
                baseQuery.append(" and (label_id == '");
                baseQuery.append(0);
                baseQuery.append("'");
                baseQuery.append(" or label_id is null))");
                baseQuery.append(" or label_id == '");
                baseQuery.append(2);
                baseQuery.append("')");
            } else if (currentCategoryId != 3) {
                baseQuery.append(" and (label_id =='");
                baseQuery.append(currentCategoryId);
                baseQuery.append("')");
            } else {
                baseQuery.append(" and ((recorded_number is not null");
                baseQuery.append(" and (label_id == '");
                baseQuery.append(0);
                baseQuery.append("'");
                baseQuery.append(" or label_id is null))");
                baseQuery.append(" or label_id == '");
                baseQuery.append(3);
                baseQuery.append("')");
            }
        }
        return baseQuery;
    }

    /* access modifiers changed from: package-private */
    public StringBuilder getAllFilesQuery() {
        return getBaseQuery();
    }

    public void setSearchTag(String str) {
        this.mRecordingSearchTag = str;
    }

    public String getSearchResult() {
        return this.mSearchResult;
    }

    public void setSearchResult(String str) {
        this.mSearchResult = str;
    }

    public String getRecordingSearchTag() {
        String str = this.mRecordingSearchTag;
        return str == null ? "" : str;
    }

    public String getCategorySearchTag() {
        String str = this.mCategorySearchTag;
        return str == null ? "" : str;
    }

    public void resetSearchTag() {
        this.mRecordingSearchTag = "";
        this.mCategorySearchTag = "";
    }

    public int getItemCount() {
        Cursor cursor = this.mCursor;
        if (cursor != null && !cursor.isClosed()) {
            return this.mCursor.getCount();
        }
        String str = this.mRecordingSearchTag;
        if (str == null || str.isEmpty()) {
            return updatedItemCount();
        }
        return getSearchListIds().size();
    }

    public int getCategoriesCount() {
        Cursor cursor = this.mCategoryCursor;
        if (cursor != null && !cursor.isClosed()) {
            return this.mCategoryCursor.getCount();
        }
        Cursor categoryCursor = getCategoryCursor(false);
        if (categoryCursor == null || categoryCursor.isClosed()) {
            return 1;
        }
        int count = categoryCursor.getCount();
        categoryCursor.close();
        return count;
    }

    public int updatedItemCount() {
        Cursor query;
        Log.m19d(TAG, "updatedItemCount");
        if (!PermissionProvider.checkSavingEnable(this.mAppContext)) {
            Log.m26i(TAG, "mAppContext == null || permission error");
            setRecordFileCount(-1);
            return -1;
        }
        int i = 0;
        try {
            query = this.mAppContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{CategoryRepository.LabelColumn.f102ID}, getListQuery().toString(), (String[]) null, (String) null);
            if (query != null) {
                i = query.getCount();
                query.close();
            }
            if (query != null) {
                query.close();
            }
        } catch (SQLiteException e) {
            Log.m22e(TAG, "findFileIndex - SQLiteException :" + e);
        } catch (UnsupportedOperationException e2) {
            Log.m22e(TAG, "findFileIndex - UnsupportedOperationException :" + e2);
        } catch (Throwable th) {
//            r3.addSuppressed(th);
        }
        Log.m19d(TAG, "updatedItemCount - count : " + i);
        setRecordFileCount(i);
        return i;
//        throw th;
    }

    public int getRecordingModeFilesCount(int i) {
        if (i == 2) {
            return this.mInterviewFileCount;
        }
        if (i != 4) {
            return this.mUnCategorizedFileCount;
        }
        return this.mMemoFileCount;
    }

    public int getCallHistoryCount() {
        return this.mCallHistoryCount;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00bd, code lost:
        if (r11.isClosed() == false) goto L_0x00bf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00bf, code lost:
        r11.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00d3, code lost:
        if (r11.isClosed() == false) goto L_0x00bf;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.util.SparseIntArray getFileCountGroupByLabel(android.content.Context r14) {
        /*
            r13 = this;
            r0 = 0
            r13.mMemoFileCount = r0
            r13.mInterviewFileCount = r0
            r13.mCallHistoryCount = r0
            r13.mUnCategorizedFileCount = r0
            android.util.SparseIntArray r1 = new android.util.SparseIntArray
            r1.<init>()
            java.lang.String r2 = "recorded_number"
            java.lang.String r3 = "recording_mode"
            java.lang.String r4 = "label_id"
            java.lang.String[] r7 = new java.lang.String[]{r4, r3, r2}
            java.lang.StringBuilder r5 = r13.getAllFilesQuery()
            java.lang.String r8 = r5.toString()
            r11 = 0
            android.content.ContentResolver r5 = r14.getContentResolver()     // Catch:{ Exception -> 0x00c5 }
            android.net.Uri r6 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x00c5 }
            r9 = 0
            r10 = 0
            android.database.Cursor r11 = r5.query(r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x00c5 }
            if (r11 == 0) goto L_0x00b7
            int r14 = r11.getCount()     // Catch:{ Exception -> 0x00c5 }
            if (r14 <= 0) goto L_0x00b7
            r11.moveToFirst()     // Catch:{ Exception -> 0x00c5 }
            r14 = r0
        L_0x0039:
            boolean r5 = r11.isAfterLast()     // Catch:{ Exception -> 0x00c5 }
            if (r5 != 0) goto L_0x00b3
            int r5 = r11.getColumnIndex(r4)     // Catch:{ Exception -> 0x00c5 }
            int r5 = r11.getInt(r5)     // Catch:{ Exception -> 0x00c5 }
            int r6 = r11.getColumnIndex(r3)     // Catch:{ Exception -> 0x00c5 }
            int r6 = r11.getInt(r6)     // Catch:{ Exception -> 0x00c5 }
            int r7 = r11.getColumnIndex(r2)     // Catch:{ Exception -> 0x00c5 }
            java.lang.String r7 = r11.getString(r7)     // Catch:{ Exception -> 0x00c5 }
            r8 = 3
            r9 = 2
            r10 = 1
            if (r5 != r10) goto L_0x0062
            int r6 = r13.mInterviewFileCount     // Catch:{ Exception -> 0x00c5 }
            int r6 = r6 + r10
            r13.mInterviewFileCount = r6     // Catch:{ Exception -> 0x00c5 }
            goto L_0x00a5
        L_0x0062:
            if (r5 != r9) goto L_0x006a
            int r6 = r13.mMemoFileCount     // Catch:{ Exception -> 0x00c5 }
            int r6 = r6 + r10
            r13.mMemoFileCount = r6     // Catch:{ Exception -> 0x00c5 }
            goto L_0x00a5
        L_0x006a:
            if (r5 != r8) goto L_0x0072
            int r6 = r13.mCallHistoryCount     // Catch:{ Exception -> 0x00c5 }
            int r6 = r6 + r10
            r13.mCallHistoryCount = r6     // Catch:{ Exception -> 0x00c5 }
            goto L_0x00a5
        L_0x0072:
            if (r5 != 0) goto L_0x009c
            if (r6 != r9) goto L_0x007d
            int r5 = r13.mInterviewFileCount     // Catch:{ Exception -> 0x00c5 }
            int r5 = r5 + r10
            r13.mInterviewFileCount = r5     // Catch:{ Exception -> 0x00c5 }
            r5 = r10
            goto L_0x00a5
        L_0x007d:
            r12 = 4
            if (r6 != r12) goto L_0x0087
            int r5 = r13.mMemoFileCount     // Catch:{ Exception -> 0x00c5 }
            int r5 = r5 + r10
            r13.mMemoFileCount = r5     // Catch:{ Exception -> 0x00c5 }
            r5 = r9
            goto L_0x00a5
        L_0x0087:
            if (r7 == 0) goto L_0x0096
            boolean r6 = r7.isEmpty()     // Catch:{ Exception -> 0x00c5 }
            if (r6 != 0) goto L_0x0096
            int r5 = r13.mCallHistoryCount     // Catch:{ Exception -> 0x00c5 }
            int r5 = r5 + r10
            r13.mCallHistoryCount = r5     // Catch:{ Exception -> 0x00c5 }
            r5 = r8
            goto L_0x00a5
        L_0x0096:
            int r6 = r13.mUnCategorizedFileCount     // Catch:{ Exception -> 0x00c5 }
            int r6 = r6 + r10
            r13.mUnCategorizedFileCount = r6     // Catch:{ Exception -> 0x00c5 }
            goto L_0x00a5
        L_0x009c:
            r6 = -2
            if (r5 != r6) goto L_0x00a5
            int r5 = r13.mUnCategorizedFileCount     // Catch:{ Exception -> 0x00c5 }
            int r5 = r5 + r10
            r13.mUnCategorizedFileCount = r5     // Catch:{ Exception -> 0x00c5 }
            r5 = r0
        L_0x00a5:
            int r14 = r14 + 1
            int r6 = r1.get(r5)     // Catch:{ Exception -> 0x00c5 }
            int r6 = r6 + r10
            r1.put(r5, r6)     // Catch:{ Exception -> 0x00c5 }
            r11.moveToNext()     // Catch:{ Exception -> 0x00c5 }
            goto L_0x0039
        L_0x00b3:
            r0 = -1
            r1.put(r0, r14)     // Catch:{ Exception -> 0x00c5 }
        L_0x00b7:
            if (r11 == 0) goto L_0x00d6
            boolean r14 = r11.isClosed()
            if (r14 != 0) goto L_0x00d6
        L_0x00bf:
            r11.close()
            goto L_0x00d6
        L_0x00c3:
            r14 = move-exception
            goto L_0x00d7
        L_0x00c5:
            r14 = move-exception
            java.lang.String r0 = "CursorProvider"
            java.lang.String r2 = "Error when query"
            com.sec.android.app.voicenote.provider.Log.m28i((java.lang.String) r0, (java.lang.String) r2, (java.lang.Throwable) r14)     // Catch:{ all -> 0x00c3 }
            if (r11 == 0) goto L_0x00d6
            boolean r14 = r11.isClosed()
            if (r14 != 0) goto L_0x00d6
            goto L_0x00bf
        L_0x00d6:
            return r1
        L_0x00d7:
            if (r11 == 0) goto L_0x00e2
            boolean r0 = r11.isClosed()
            if (r0 != 0) goto L_0x00e2
            r11.close()
        L_0x00e2:
            throw r14
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.provider.CursorProvider.getFileCountGroupByLabel(android.content.Context):android.util.SparseIntArray");
    }

    private boolean isMatch(String str, String[] strArr) {
        for (String contains : strArr) {
            if (!str.contains(contains)) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<Long> search() {
        Cursor query;
        Throwable th;
        Log.m26i(TAG, "search : " + this.mRecordingSearchTag);
        if (!PermissionProvider.checkSavingEnable(this.mAppContext)) {
            Log.m26i(TAG, "mAppContext == null || permission error");
            return new ArrayList<>();
        }
        String sb = getListQuery().toString();
        String[] strArr = {"_data", CategoryRepository.LabelColumn.f102ID, "title", "recording_mode"};
        ArrayList<Long> arrayList = new ArrayList<>();
        String[] split = this.mRecordingSearchTag.toLowerCase().split(" +");
        try {
            query = this.mAppContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, strArr, sb, (String[]) null, "datetaken DESC");
            if (query != null) {
                if (query.getCount() != 0) {
                    int columnIndex = query.getColumnIndex("_data");
                    int columnIndex2 = query.getColumnIndex(CategoryRepository.LabelColumn.f102ID);
                    int columnIndex3 = query.getColumnIndex("title");
                    int columnIndex4 = query.getColumnIndex("recording_mode");
                    StringBuilder sb2 = new StringBuilder();
                    while (query.moveToNext()) {
                        String convertToSDCardWritablePath = StorageProvider.convertToSDCardWritablePath(query.getString(columnIndex));
                        String string = query.getString(columnIndex3);
                        if (query.getInt(columnIndex4) == 4 && convertToSDCardWritablePath != null) {
                            ArrayList<TextData> read = new SttHelper(new M4aReader(convertToSDCardWritablePath).readFile()).read();
                            if (read != null) {
                                Iterator<TextData> it = read.iterator();
                                while (it.hasNext()) {
                                    TextData next = it.next();
                                    if (next.dataType == 0) {
                                        sb2.append(next.mText[0]);
                                    }
                                }
                                String lowerCase = sb2.toString().toLowerCase();
                                sb2.setLength(0);
                                if (isMatch(lowerCase, split)) {
                                    arrayList.add(Long.valueOf(query.getLong(columnIndex2)));
                                }
                            }
                        }
                        if (string != null && isMatch(string.toLowerCase(), split)) {
                            arrayList.add(Long.valueOf(query.getLong(columnIndex2)));
                        }
                    }
                    if (!query.isClosed()) {
                        query.close();
                    }
                    if (query != null) {
                        query.close();
                    }
                    return arrayList;
                }
            }
            if (query != null) {
                query.close();
            }
            if (query != null) {
                query.close();
            }
            return arrayList;
        } catch (SQLiteException e) {
            Log.m22e(TAG, "findStt - SQLiteException :" + e);
        } catch (UnsupportedOperationException e2) {
            Log.m22e(TAG, "findStt - UnsupportedOperationException :" + e2);
        } catch (Throwable th2) {
//            th.addSuppressed(th2);
        }
//        throw th;
        return null;
    }

    private void clearSearchListIds() {
        this.mSearchListIds.clear();
    }

    /* access modifiers changed from: private */
    public ArrayList<Long> getSearchListIds() {
        if (this.mSearchListIds.isEmpty()) {
            this.mSearchListIds = search();
        }
        Log.m26i(TAG, "getSearchListIds - count : " + this.mSearchListIds.size());
        return this.mSearchListIds;
    }

    public ArrayList<Long> getCurrentSearchListIds() {
        return this.mSearchListIds;
    }

    public void setCurrentPlayingItemPosition(int i) {
        Log.m26i(TAG, "setCurrentPlayingItemPosition - position : " + i);
        Cursor cursor = this.mCursor;
        if (cursor == null || cursor.isClosed()) {
            Log.m29v(TAG, "cursor is null or closed");
        } else if (this.mCursor.getCount() <= i) {
            Log.m32w(TAG, "position is invalid");
        }
        this.mCurrentPlayingPosition = i;
    }

    public void resetCurrentPlayingItemPosition() {
        this.mCurrentPlayingPosition = -1;
    }

    public int getCurrentPlayingPosition() {
        return this.mCurrentPlayingPosition;
    }

    public String getPrevFilePath() {
        String str = null;
        if (this.mCurrentPlayingPosition == -1) {
            return null;
        }
        Cursor cursor = this.mCursor;
        if (cursor != null && !cursor.isClosed()) {
            int i = this.mCurrentPlayingPosition;
            if (i == 0) {
                i = this.mCursor.getCount();
            }
            if (this.mCursor.moveToPosition(i - 1)) {
                str = getPath((long) this.mCursor.getInt(0));
            }
        }
        Log.m19d(TAG, "getPrevFilePath - path : " + str);
        return StorageProvider.convertToSDCardWritablePath(str);
    }

    public String getNextFilePath() {
        String str = null;
        if (this.mCurrentPlayingPosition == -1) {
            return null;
        }
        Cursor cursor = this.mCursor;
        if (cursor != null && !cursor.isClosed()) {
            int count = this.mCursor.getCount();
            int i = this.mCurrentPlayingPosition;
            if (this.mCursor.moveToPosition(count <= i + 1 ? 0 : i + 1)) {
                str = getPath((long) this.mCursor.getInt(0));
            }
        }
        Log.m19d(TAG, "getNextFilePath - path : " + str);
        return StorageProvider.convertToSDCardWritablePath(str);
    }

    public String getPath(long j) {
        String str;
        Cursor cursor = this.mCursor;
        if (cursor != null && !cursor.isClosed()) {
            this.mCursor.moveToFirst();
            int columnIndex = this.mCursor.getColumnIndex(CategoryRepository.LabelColumn.f102ID);
            if (columnIndex >= 0) {
                while (true) {
                    if (this.mCursor.isAfterLast()) {
                        str = null;
                        break;
                    } else if (this.mCursor.getLong(columnIndex) == j) {
                        Cursor cursor2 = this.mCursor;
                        str = cursor2.getString(cursor2.getColumnIndex("_data"));
                        break;
                    } else {
                        this.mCursor.moveToNext();
                    }
                }
            } else {
                Log.m26i(TAG, "index is wrong - cursor is unloaded");
                return DBProvider.getInstance().getPathById(j);
            }
        } else {
            Log.m26i(TAG, "getPath - cursor is unloaded");
            str = DBProvider.getInstance().getPathById(j);
        }
        return StorageProvider.convertToSDCardWritablePath(str);
    }

    public long getDuration(long j) {
        Cursor cursor = this.mCursor;
        if (cursor != null && !cursor.isClosed()) {
            this.mCursor.moveToFirst();
            int columnIndex = this.mCursor.getColumnIndex(CategoryRepository.LabelColumn.f102ID);
            while (!this.mCursor.isAfterLast()) {
                if (this.mCursor.getLong(columnIndex) == j) {
                    Cursor cursor2 = this.mCursor;
                    return cursor2.getLong(cursor2.getColumnIndex("duration"));
                }
                this.mCursor.moveToNext();
            }
        }
        return 0;
    }

    public ArrayList<String> getPathByIds(List<Long> list) {
        if (list == null || list.size() == 0) {
            return null;
        }
        ArrayList<String> arrayList = new ArrayList<>();
        Cursor cursor = this.mCursor;
        if (cursor != null && !cursor.isClosed()) {
            this.mCursor.moveToFirst();
            int columnIndex = this.mCursor.getColumnIndex(CategoryRepository.LabelColumn.f102ID);
            int columnIndex2 = this.mCursor.getColumnIndex("_data");
            while (!this.mCursor.isAfterLast()) {
                if (list.contains(Long.valueOf(this.mCursor.getLong(columnIndex)))) {
                    arrayList.add(this.mCursor.getString(columnIndex2));
                }
                if (arrayList.size() == list.size()) {
                    break;
                }
                this.mCursor.moveToNext();
            }
        }
        return arrayList;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002f, code lost:
        r1 = r7.mCursor.getInt(r7.mCursor.getColumnIndex("recording_mode"));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        r0 = com.sec.android.app.voicenote.provider.StorageProvider.convertToSDCardWritablePath(r7.mCursor.getString(r7.mCursor.getColumnIndex("_data")));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x004f, code lost:
        r3 = r1;
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0052, code lost:
        r3 = e;
     */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x007c  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00a7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int getRecordMode(long r8) {
        /*
            r7 = this;
            monitor-enter(r7)
            r0 = 0
            android.database.Cursor r1 = r7.mCursor     // Catch:{ all -> 0x00aa }
            r2 = 1
            if (r1 == 0) goto L_0x0079
            android.database.Cursor r1 = r7.mCursor     // Catch:{ all -> 0x00aa }
            boolean r1 = r1.isClosed()     // Catch:{ all -> 0x00aa }
            if (r1 != 0) goto L_0x0079
            r1 = 0
            android.database.Cursor r3 = r7.mCursor     // Catch:{ IllegalStateException -> 0x006d }
            r3.moveToFirst()     // Catch:{ IllegalStateException -> 0x006d }
            android.database.Cursor r3 = r7.mCursor     // Catch:{ IllegalStateException -> 0x006d }
            java.lang.String r4 = "_id"
            int r3 = r3.getColumnIndex(r4)     // Catch:{ IllegalStateException -> 0x006d }
        L_0x001d:
            android.database.Cursor r4 = r7.mCursor     // Catch:{ IllegalStateException -> 0x006d }
            boolean r4 = r4.isAfterLast()     // Catch:{ IllegalStateException -> 0x006d }
            if (r4 != 0) goto L_0x005a
            android.database.Cursor r4 = r7.mCursor     // Catch:{ IllegalStateException -> 0x006d }
            long r4 = r4.getLong(r3)     // Catch:{ IllegalStateException -> 0x006d }
            int r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r4 != 0) goto L_0x0054
            android.database.Cursor r1 = r7.mCursor     // Catch:{ IllegalStateException -> 0x006d }
            android.database.Cursor r3 = r7.mCursor     // Catch:{ IllegalStateException -> 0x006d }
            java.lang.String r4 = "recording_mode"
            int r3 = r3.getColumnIndex(r4)     // Catch:{ IllegalStateException -> 0x006d }
            int r1 = r1.getInt(r3)     // Catch:{ IllegalStateException -> 0x006d }
            android.database.Cursor r3 = r7.mCursor     // Catch:{ IllegalStateException -> 0x0052 }
            android.database.Cursor r4 = r7.mCursor     // Catch:{ IllegalStateException -> 0x0052 }
            java.lang.String r5 = "_data"
            int r4 = r4.getColumnIndex(r5)     // Catch:{ IllegalStateException -> 0x0052 }
            java.lang.String r3 = r3.getString(r4)     // Catch:{ IllegalStateException -> 0x0052 }
            java.lang.String r0 = com.sec.android.app.voicenote.provider.StorageProvider.convertToSDCardWritablePath(r3)     // Catch:{ IllegalStateException -> 0x0052 }
            r3 = r1
            r1 = r2
            goto L_0x005b
        L_0x0052:
            r3 = move-exception
            goto L_0x006f
        L_0x0054:
            android.database.Cursor r4 = r7.mCursor     // Catch:{ IllegalStateException -> 0x006d }
            r4.moveToNext()     // Catch:{ IllegalStateException -> 0x006d }
            goto L_0x001d
        L_0x005a:
            r3 = r2
        L_0x005b:
            if (r1 != 0) goto L_0x006b
            com.sec.android.app.voicenote.provider.DBProvider r1 = com.sec.android.app.voicenote.provider.DBProvider.getInstance()     // Catch:{ IllegalStateException -> 0x0066 }
            int r1 = r1.getRecordModeById(r8)     // Catch:{ IllegalStateException -> 0x0066 }
            goto L_0x007a
        L_0x0066:
            r1 = move-exception
            r6 = r3
            r3 = r1
            r1 = r6
            goto L_0x006f
        L_0x006b:
            r1 = r3
            goto L_0x007a
        L_0x006d:
            r3 = move-exception
            r1 = r2
        L_0x006f:
            java.lang.String r4 = "CursorProvider"
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x00aa }
            com.sec.android.app.voicenote.provider.Log.m22e(r4, r3)     // Catch:{ all -> 0x00aa }
            goto L_0x007a
        L_0x0079:
            r1 = r2
        L_0x007a:
            if (r1 != 0) goto L_0x00a7
            java.lang.String r1 = "CursorProvider"
            java.lang.String r3 = "getRecordMode - record mode is empty"
            com.sec.android.app.voicenote.provider.Log.m32w((java.lang.String) r1, (java.lang.String) r3)     // Catch:{ all -> 0x00aa }
            com.sec.android.app.voicenote.service.codec.M4aReader r1 = new com.sec.android.app.voicenote.service.codec.M4aReader     // Catch:{ all -> 0x00aa }
            r1.<init>(r0)     // Catch:{ all -> 0x00aa }
            com.sec.android.app.voicenote.service.codec.M4aInfo r0 = r1.readFile()     // Catch:{ all -> 0x00aa }
            if (r0 == 0) goto L_0x00a8
            java.util.HashMap<java.lang.String, java.lang.Boolean> r0 = r0.hasCustomAtom     // Catch:{ all -> 0x00aa }
            java.lang.String r1 = "sttd"
            java.lang.Object r0 = r0.get(r1)     // Catch:{ all -> 0x00aa }
            java.lang.Boolean r0 = (java.lang.Boolean) r0     // Catch:{ all -> 0x00aa }
            boolean r0 = r0.booleanValue()     // Catch:{ all -> 0x00aa }
            if (r0 == 0) goto L_0x00a8
            r2 = 4
            com.sec.android.app.voicenote.provider.DBProvider r0 = com.sec.android.app.voicenote.provider.DBProvider.getInstance()     // Catch:{ all -> 0x00aa }
            r0.updateRecordingModeInMediaDB(r8, r2)     // Catch:{ all -> 0x00aa }
            goto L_0x00a8
        L_0x00a7:
            r2 = r1
        L_0x00a8:
            monitor-exit(r7)
            return r2
        L_0x00aa:
            r8 = move-exception
            monitor-exit(r7)
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.provider.CursorProvider.getRecordMode(long):int");
    }

    public String getMimeType(long j) {
        Cursor cursor = this.mCursor;
        if (cursor == null || cursor.isClosed()) {
            return null;
        }
        try {
            this.mCursor.moveToFirst();
            int columnIndex = this.mCursor.getColumnIndex(CategoryRepository.LabelColumn.f102ID);
            while (!this.mCursor.isAfterLast()) {
                if (this.mCursor.getLong(columnIndex) == j) {
                    return this.mCursor.getString(this.mCursor.getColumnIndex("mime_type"));
                }
                this.mCursor.moveToNext();
            }
            return null;
        } catch (IllegalStateException e) {
            Log.m22e(TAG, e.toString());
            return null;
        }
    }

    public int moveToPrevPosition() {
        Log.m26i(TAG, "moveToPrevPosition");
        Cursor cursor = this.mCursor;
        if (cursor != null && !cursor.isClosed()) {
            int i = this.mCurrentPlayingPosition;
            if (i <= 0) {
                this.mCurrentPlayingPosition = this.mCursor.getCount() - 1;
            } else {
                this.mCurrentPlayingPosition = i - 1;
            }
        }
        Log.m26i(TAG, "moveToPrevPosition - position : " + this.mCurrentPlayingPosition);
        return this.mCurrentPlayingPosition;
    }

    public int moveToNextPosition() {
        Log.m26i(TAG, "moveToNextPosition");
        Cursor cursor = this.mCursor;
        if (cursor != null && !cursor.isClosed()) {
            int count = this.mCursor.getCount();
            int i = this.mCurrentPlayingPosition;
            if (count <= i + 1) {
                this.mCurrentPlayingPosition = 0;
            } else {
                this.mCurrentPlayingPosition = i + 1;
            }
        }
        Log.m26i(TAG, "moveToNextPosition - position : " + this.mCurrentPlayingPosition);
        return this.mCurrentPlayingPosition;
    }

    /* access modifiers changed from: private */
    public List<String> getAllFilePath() {
        ArrayList arrayList = new ArrayList();
        Cursor cursor = this.mCursor;
        if (cursor != null && !cursor.isClosed()) {
            this.mCursor.moveToFirst();
            int columnIndex = this.mCursor.getColumnIndex("_data");
            while (!this.mCursor.isAfterLast()) {
                arrayList.add(StorageProvider.convertToSDCardWritablePath(this.mCursor.getString(columnIndex)));
                this.mCursor.moveToNext();
            }
            this.mCursor.moveToLast();
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0060, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean isUpdate(android.database.Cursor r7, android.database.Cursor r8) {
        /*
            r6 = this;
            monitor-enter(r6)
            java.lang.String r0 = "CursorProvider"
            java.lang.String r1 = "isUpdate"
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r1)     // Catch:{ all -> 0x00df }
            r0 = 0
            if (r7 == 0) goto L_0x00d6
            if (r8 == 0) goto L_0x00d6
            boolean r1 = r7.isClosed()     // Catch:{ all -> 0x00df }
            if (r1 != 0) goto L_0x00d6
            boolean r1 = r8.isClosed()     // Catch:{ all -> 0x00df }
            if (r1 == 0) goto L_0x001b
            goto L_0x00d6
        L_0x001b:
            int r1 = r7.getCount()     // Catch:{ all -> 0x00df }
            int r2 = r8.getCount()     // Catch:{ all -> 0x00df }
            if (r1 == r2) goto L_0x0061
            java.lang.String r1 = "CursorProvider"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00df }
            r2.<init>()     // Catch:{ all -> 0x00df }
            java.lang.String r3 = "isUpdate oldCursor.getCount() : "
            r2.append(r3)     // Catch:{ all -> 0x00df }
            int r7 = r7.getCount()     // Catch:{ all -> 0x00df }
            r2.append(r7)     // Catch:{ all -> 0x00df }
            java.lang.String r7 = " newCursor.getCount() : "
            r2.append(r7)     // Catch:{ all -> 0x00df }
            int r7 = r8.getCount()     // Catch:{ all -> 0x00df }
            r2.append(r7)     // Catch:{ all -> 0x00df }
            java.lang.String r7 = r2.toString()     // Catch:{ all -> 0x00df }
            com.sec.android.app.voicenote.provider.Log.m26i(r1, r7)     // Catch:{ all -> 0x00df }
            com.sec.android.app.voicenote.provider.CursorProvider$OnCategoryCursorChangeListener r7 = r6.mCategoryCursorChangeListener     // Catch:{ all -> 0x00df }
            if (r7 == 0) goto L_0x0054
            com.sec.android.app.voicenote.provider.CursorProvider$OnCategoryCursorChangeListener r7 = r6.mCategoryCursorChangeListener     // Catch:{ all -> 0x00df }
            r7.notifyDataSetChanged()     // Catch:{ all -> 0x00df }
        L_0x0054:
            com.sec.android.app.voicenote.provider.CursorProvider$OnCursorChangeListener r7 = r6.mCursorChangeListener     // Catch:{ all -> 0x00df }
            if (r7 == 0) goto L_0x005f
            com.sec.android.app.voicenote.provider.CursorProvider$OnCursorChangeListener r7 = r6.mCursorChangeListener     // Catch:{ all -> 0x00df }
            android.database.Cursor r8 = r6.mCursor     // Catch:{ all -> 0x00df }
            r7.notifyDataSetChanged(r8)     // Catch:{ all -> 0x00df }
        L_0x005f:
            monitor-exit(r6)
            return r0
        L_0x0061:
            java.lang.String r1 = "_id"
            int r1 = r7.getColumnIndex(r1)     // Catch:{ CursorIndexOutOfBoundsException -> 0x00b1, IllegalStateException -> 0x0095 }
            r7.moveToFirst()     // Catch:{ CursorIndexOutOfBoundsException -> 0x00b1, IllegalStateException -> 0x0095 }
            r8.moveToFirst()     // Catch:{ CursorIndexOutOfBoundsException -> 0x00b1, IllegalStateException -> 0x0095 }
        L_0x006d:
            boolean r2 = r7.isAfterLast()     // Catch:{ CursorIndexOutOfBoundsException -> 0x00b1, IllegalStateException -> 0x0095 }
            if (r2 != 0) goto L_0x00cc
            boolean r2 = r8.isAfterLast()     // Catch:{ CursorIndexOutOfBoundsException -> 0x00b1, IllegalStateException -> 0x0095 }
            if (r2 != 0) goto L_0x00cc
            long r2 = r7.getLong(r1)     // Catch:{ CursorIndexOutOfBoundsException -> 0x00b1, IllegalStateException -> 0x0095 }
            long r4 = r8.getLong(r1)     // Catch:{ CursorIndexOutOfBoundsException -> 0x00b1, IllegalStateException -> 0x0095 }
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 == 0) goto L_0x008e
            java.lang.String r7 = "CursorProvider"
            java.lang.String r8 = "isUpdate return false"
            com.sec.android.app.voicenote.provider.Log.m26i(r7, r8)     // Catch:{ CursorIndexOutOfBoundsException -> 0x00b1, IllegalStateException -> 0x0095 }
            monitor-exit(r6)
            return r0
        L_0x008e:
            r7.moveToNext()     // Catch:{ CursorIndexOutOfBoundsException -> 0x00b1, IllegalStateException -> 0x0095 }
            r8.moveToNext()     // Catch:{ CursorIndexOutOfBoundsException -> 0x00b1, IllegalStateException -> 0x0095 }
            goto L_0x006d
        L_0x0095:
            r7 = move-exception
            java.lang.String r8 = "CursorProvider"
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x00df }
            r0.<init>()     // Catch:{ all -> 0x00df }
            java.lang.String r1 = "isUpdate exception: "
            r0.append(r1)     // Catch:{ all -> 0x00df }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x00df }
            r0.append(r7)     // Catch:{ all -> 0x00df }
            java.lang.String r7 = r0.toString()     // Catch:{ all -> 0x00df }
            com.sec.android.app.voicenote.provider.Log.m22e(r8, r7)     // Catch:{ all -> 0x00df }
            goto L_0x00cc
        L_0x00b1:
            r7 = move-exception
            java.lang.String r8 = "CursorProvider"
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x00df }
            r0.<init>()     // Catch:{ all -> 0x00df }
            java.lang.String r1 = "isUpdate exception: "
            r0.append(r1)     // Catch:{ all -> 0x00df }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x00df }
            r0.append(r7)     // Catch:{ all -> 0x00df }
            java.lang.String r7 = r0.toString()     // Catch:{ all -> 0x00df }
            com.sec.android.app.voicenote.provider.Log.m22e(r8, r7)     // Catch:{ all -> 0x00df }
        L_0x00cc:
            java.lang.String r7 = "CursorProvider"
            java.lang.String r8 = "isUpdate return true "
            com.sec.android.app.voicenote.provider.Log.m26i(r7, r8)     // Catch:{ all -> 0x00df }
            r7 = 1
            monitor-exit(r6)
            return r7
        L_0x00d6:
            java.lang.String r7 = "CursorProvider"
            java.lang.String r8 = "isUpdate oldCursor or newCursor is null"
            com.sec.android.app.voicenote.provider.Log.m26i(r7, r8)     // Catch:{ all -> 0x00df }
            monitor-exit(r6)
            return r0
        L_0x00df:
            r7 = move-exception
            monitor-exit(r6)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.provider.CursorProvider.isUpdate(android.database.Cursor, android.database.Cursor):boolean");
    }

    public ArrayList<Long> getIDs() {
        ArrayList<Long> arrayList = new ArrayList<>();
        Cursor cursor = this.mCursor;
        if (cursor != null && !cursor.isClosed()) {
            this.mCursor.moveToFirst();
            while (!this.mCursor.isAfterLast()) {
                arrayList.add(Long.valueOf(this.mCursor.getLong(0)));
                this.mCursor.moveToNext();
            }
        }
        return arrayList;
    }

    public long getIdInOneItemCase() {
        Log.m26i(TAG, "getIdInOneItemCase");
        Cursor cursor = this.mCursor;
        if (cursor == null || cursor.isClosed() || this.mCursor.getCount() != 1) {
            return -1;
        }
        this.mCursor.moveToFirst();
        Cursor cursor2 = this.mCursor;
        return cursor2.getLong(cursor2.getColumnIndex(CategoryRepository.LabelColumn.f102ID));
    }

    public ArrayList<Long> getCurrentIDs(long j) {
        ArrayList<Long> arrayList = new ArrayList<>();
        Cursor cursor = this.mCursor;
        if (cursor != null && !cursor.isClosed()) {
            this.mCursor.moveToFirst();
            int columnIndex = this.mCursor.getColumnIndex(CategoryRepository.LabelColumn.f102ID);
            if (this.mCursor.getLong(columnIndex) != j) {
                while (!this.mCursor.isAfterLast()) {
                    arrayList.add(Long.valueOf(this.mCursor.getLong(0)));
                    this.mCursor.moveToNext();
                    if (this.mCursor.getLong(columnIndex) == j) {
                        break;
                    }
                }
            } else {
                return arrayList;
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    public static String getSortQuery() {
        int intSettings = Settings.getIntSettings(Settings.KEY_SORT_MODE, 3);
        if (intSettings == 0) {
            return "datetaken ASC";
        }
        if (intSettings == 1) {
            return "_display_name COLLATE LOCALIZED ASC";
        }
        if (intSettings == 2) {
            return "duration ASC";
        }
        if (intSettings == 3) {
            return "datetaken DESC";
        }
        if (intSettings != 4) {
            return intSettings != 5 ? "datetaken DESC" : "duration DESC";
        }
        return "_display_name COLLATE LOCALIZED DESC";
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0064, code lost:
        if (r10.isClosed() == false) goto L_0x0066;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0066, code lost:
        r10.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x007c, code lost:
        if (r10.isClosed() == false) goto L_0x0066;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int findLabelID(android.content.Context r9, long r10) {
        /*
            java.lang.String r0 = "recorded_number"
            java.lang.String r1 = "recording_mode"
            java.lang.String r2 = "label_id"
            java.lang.String[] r5 = new java.lang.String[]{r2, r1, r0}
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "_id="
            r3.append(r4)
            r3.append(r10)
            java.lang.String r6 = r3.toString()
            r10 = 0
            r11 = 0
            android.content.ContentResolver r3 = r9.getContentResolver()     // Catch:{ Exception -> 0x006c }
            android.net.Uri r4 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x006c }
            r7 = 0
            r8 = 0
            android.database.Cursor r10 = r3.query(r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x006c }
            r9 = 2
            if (r10 == 0) goto L_0x005e
            boolean r3 = r10.moveToFirst()     // Catch:{ Exception -> 0x006c }
            if (r3 == 0) goto L_0x005e
            int r2 = r10.getColumnIndex(r2)     // Catch:{ Exception -> 0x006c }
            int r11 = r10.getInt(r2)     // Catch:{ Exception -> 0x006c }
            int r1 = r10.getColumnIndex(r1)     // Catch:{ Exception -> 0x006c }
            int r1 = r10.getInt(r1)     // Catch:{ Exception -> 0x006c }
            int r0 = r10.getColumnIndex(r0)     // Catch:{ Exception -> 0x006c }
            java.lang.String r0 = r10.getString(r0)     // Catch:{ Exception -> 0x006c }
            if (r11 != 0) goto L_0x005e
            if (r1 != r9) goto L_0x0050
            r11 = 1
            goto L_0x005e
        L_0x0050:
            r2 = 4
            if (r1 != r2) goto L_0x0055
            r11 = r9
            goto L_0x005e
        L_0x0055:
            if (r0 == 0) goto L_0x005e
            boolean r9 = r0.isEmpty()     // Catch:{ Exception -> 0x006c }
            if (r9 != 0) goto L_0x005e
            r11 = 3
        L_0x005e:
            if (r10 == 0) goto L_0x007f
            boolean r9 = r10.isClosed()
            if (r9 != 0) goto L_0x007f
        L_0x0066:
            r10.close()
            goto L_0x007f
        L_0x006a:
            r9 = move-exception
            goto L_0x0080
        L_0x006c:
            r9 = move-exception
            java.lang.String r0 = "CursorProvider"
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x006a }
            com.sec.android.app.voicenote.provider.Log.m22e(r0, r9)     // Catch:{ all -> 0x006a }
            if (r10 == 0) goto L_0x007f
            boolean r9 = r10.isClosed()
            if (r9 != 0) goto L_0x007f
            goto L_0x0066
        L_0x007f:
            return r11
        L_0x0080:
            if (r10 == 0) goto L_0x008b
            boolean r11 = r10.isClosed()
            if (r11 != 0) goto L_0x008b
            r10.close()
        L_0x008b:
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.provider.CursorProvider.findLabelID(android.content.Context, long):int");
    }

    /* access modifiers changed from: private */
    public void closeOldCategoryCursor() {
        Cursor cursor = this.mCategoryCursor;
        if (cursor != null) {
            cursor.close();
            this.mCategoryCursor = null;
        }
    }

    public Cursor getCategoryCursor(boolean z) {
        String str;
        ArrayList arrayList = new ArrayList();
        if (z) {
            str = CategoryRepository.getListQueryFromSpinner(this.mAppContext).toString();
        } else {
            str = CategoryRepository.getListQuery(this.mAppContext, true, arrayList).toString();
        }
        try {
            return DataRepository.getInstance().getVNDatabase().getOpenHelper().getWritableDatabase().query(str, arrayList.toArray(new String[arrayList.size()]));
        } catch (SQLiteException | NullPointerException | UnsupportedOperationException e) {
            Log.m22e(TAG, "getCategoryCursor - :" + e);
            return null;
        }
    }

    /* JADX WARNING: type inference failed for: r2v4, types: [java.lang.Object[], android.database.Cursor] */
    /* JADX WARNING: type inference failed for: r2v5, types: [android.database.Cursor] */
    /* JADX WARNING: type inference failed for: r2v7 */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x006c, code lost:
        if (r2.isClosed() == false) goto L_0x0056;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0054, code lost:
        if (r0 == false) goto L_0x0056;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0056, code lost:
        r2.close();
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getMaxCategoryPos() {
        /*
            r5 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "select "
            r0.append(r1)
            java.lang.String r1 = "POSITION"
            r0.append(r1)
            java.lang.String r2 = " from "
            r0.append(r2)
            java.lang.String r2 = "labels"
            r0.append(r2)
            java.lang.String r2 = " order by "
            r0.append(r2)
            r0.append(r1)
            java.lang.String r2 = " ASC"
            r0.append(r2)
            r2 = 0
            r3 = 3
            com.sec.android.app.voicenote.common.util.DataRepository r4 = com.sec.android.app.voicenote.common.util.DataRepository.getInstance()     // Catch:{ Exception -> 0x005c }
            com.sec.android.app.voicenote.common.util.db.VNDatabase r4 = r4.getVNDatabase()     // Catch:{ Exception -> 0x005c }
            androidx.sqlite.db.SupportSQLiteOpenHelper r4 = r4.getOpenHelper()     // Catch:{ Exception -> 0x005c }
            androidx.sqlite.db.SupportSQLiteDatabase r4 = r4.getWritableDatabase()     // Catch:{ Exception -> 0x005c }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x005c }
            android.database.Cursor r2 = r4.query((java.lang.String) r0, (java.lang.Object[]) r2)     // Catch:{ Exception -> 0x005c }
            if (r2 == 0) goto L_0x004e
            r2.moveToLast()     // Catch:{ Exception -> 0x005c }
            int r0 = r2.getColumnIndex(r1)     // Catch:{ Exception -> 0x005c }
            int r0 = r2.getInt(r0)     // Catch:{ Exception -> 0x005c }
            r3 = r0
        L_0x004e:
            if (r2 == 0) goto L_0x006f
            boolean r0 = r2.isClosed()
            if (r0 != 0) goto L_0x006f
        L_0x0056:
            r2.close()
            goto L_0x006f
        L_0x005a:
            r0 = move-exception
            goto L_0x0070
        L_0x005c:
            r0 = move-exception
            java.lang.String r1 = "CursorProvider"
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x005a }
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r0)     // Catch:{ all -> 0x005a }
            if (r2 == 0) goto L_0x006f
            boolean r0 = r2.isClosed()
            if (r0 != 0) goto L_0x006f
            goto L_0x0056
        L_0x006f:
            return r3
        L_0x0070:
            if (r2 == 0) goto L_0x007b
            boolean r1 = r2.isClosed()
            if (r1 != 0) goto L_0x007b
            r2.close()
        L_0x007b:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.provider.CursorProvider.getMaxCategoryPos():int");
    }

    public Cursor getCustomCategoryCursor(SupportSQLiteDatabase supportSQLiteDatabase, int i) {
        ArrayList arrayList = new ArrayList();
        try {
            return supportSQLiteDatabase.query("select _id, TITLE, POSITION from labels where _id >= " + i + " order by POSITION ASC", arrayList.toArray(new String[arrayList.size()]));
        } catch (SQLiteException | NullPointerException | UnsupportedOperationException e) {
            Log.m22e(TAG, "getCustomCategoryCursor - :" + e);
            return null;
        }
    }

    public void loadCategory(LoaderManager loaderManager) {
        if (this.mCategoryLoaderCallback == null) {
            this.mCategoryLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
                @SuppressLint({"StaticFieldLeak"})
                public Loader onCreateLoader(int i, Bundle bundle) {
                    return new CursorLoader(CursorProvider.this.mAppContext, (Uri) null, (String[]) null, (String) null, (String[]) null, (String) null) {
                        public Cursor loadInBackground() {
                            ArrayList arrayList = new ArrayList();
                            try {
                                return DataRepository.getInstance().getVNDatabase().getOpenHelper().getWritableDatabase().query(CategoryRepository.getListQuery(CursorProvider.this.mAppContext, false, arrayList).toString(), arrayList.toArray(new String[arrayList.size()]));
                            } catch (SQLiteException | IllegalStateException | NullPointerException | UnsupportedOperationException e) {
                                Log.m22e(CursorProvider.TAG, "loadCategory - :" + e);
                                return null;
                            }
                        }

                        /* access modifiers changed from: protected */
                        public void onReset() {
                            onStopLoading();
                        }
                    };
                }

                public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                    Log.m26i(CursorProvider.TAG, "loadCategory - onLoadFinished");
                    if (cursor == null) {
                        Log.m22e(CursorProvider.TAG, "loadCategory - onLoadFinished : new cursor is NULL");
                    } else if (cursor.isClosed()) {
                        Log.m32w(CursorProvider.TAG, "loadCategory - onLoadFinished : new cursor is closed");
                    } else {
                        if (!(CursorProvider.this.mCategoryCursor == null || CursorProvider.this.mCategoryCursor.isClosed() || CursorProvider.this.mCategoryCursor == cursor)) {
                            Log.m32w(CursorProvider.TAG, "loadCategory - onLoadFinished : prior cursor should be closed");
                            CursorProvider.this.closeOldCategoryCursor();
                        }
                        if (CursorProvider.this.mCategoryCursorChangeListener != null) {
                            CursorProvider.this.mCategoryCursorChangeListener.onCursorChanged(cursor);
                        }
                        Cursor unused = CursorProvider.this.mCategoryCursor = cursor;
                    }
                }

                public void onLoaderReset(Loader loader) {
                    Log.m26i(CursorProvider.TAG, "loadCategory - onLoaderReset");
                    if (CursorProvider.this.mCategoryCursorChangeListener != null) {
                        CursorProvider.this.mCategoryCursorChangeListener.onLoadReset();
                    }
                }
            };
        }
        loaderManager.destroyLoader(2);
        loaderManager.initLoader(2, (Bundle) null, this.mCategoryLoaderCallback);
    }

    public void reloadCategory(LoaderManager loaderManager) {
        Log.m26i(TAG, "reloadCategory");
        LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = this.mCategoryLoaderCallback;
        if (loaderCallbacks != null) {
            loaderManager.restartLoader(2, (Bundle) null, loaderCallbacks);
        }
    }

    public int getCurrentFileCount() {
        return this.mCurrentFileCount;
    }

    public void setCurrentFileCount(int i) {
        this.mCurrentFileCount = i;
    }
}
