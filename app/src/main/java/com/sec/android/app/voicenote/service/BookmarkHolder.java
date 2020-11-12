package com.sec.android.app.voicenote.service;

import android.util.LruCache;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.StorageProvider;
import com.sec.android.app.voicenote.service.codec.M4aReader;
import com.sec.android.app.voicenote.service.helper.BookmarksHelper;
import java.io.File;

public class BookmarkHolder {
    private static final int BOOKMARKS_CACHE_SIZE = 1200;
    private static final String TAG = "BookmarkHolder";
    private static BookmarkHolder mInstance;
    private LruCache<String, Boolean> mBookmarkCache = new LruCache<>(BOOKMARKS_CACHE_SIZE);

    private BookmarkHolder() {
        Log.m26i(TAG, "BookmarkHolder creator !!");
    }

    public static BookmarkHolder getInstance() {
        if (mInstance == null) {
            mInstance = new BookmarkHolder();
        }
        return mInstance;
    }

    public void set(String str, boolean z) {
        if (!StorageProvider.isTempFile(str)) {
            try {
                this.mBookmarkCache.put(str, Boolean.valueOf(z));
            } catch (NullPointerException e) {
                Log.m24e(TAG, "NullPointerException", (Throwable) e);
            }
        }
    }

    public boolean get(String str) {
        Boolean bool = this.mBookmarkCache.get(str);
        if (bool == null) {
            boolean z = false;
            if (!new File(str).exists()) {
                return false;
            }
            if (new BookmarksHelper(new M4aReader(str).readFile()).getBookmarksCount() > 0) {
                z = true;
            }
            bool = Boolean.valueOf(z);
            this.mBookmarkCache.put(str, bool);
        }
        return bool.booleanValue();
    }

    public void remove(String str) {
        this.mBookmarkCache.remove(str);
    }

    public void replace(String str, String str2) {
        Boolean bool = this.mBookmarkCache.get(str);
        if (bool != null) {
            this.mBookmarkCache.put(str2, bool);
            this.mBookmarkCache.remove(str);
        }
    }
}
