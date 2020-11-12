package com.sec.android.app.voicenote.service;

import android.content.Context;
import com.sec.android.app.voicenote.provider.Log;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleMetadataRepositoryManager {
    private static final String TAG = "SimpleMetaDataRepositoryPool";
    private Context mAppContext;
    private Map<String, SimpleMetadataRepository> metadataMap;

    private SimpleMetadataRepositoryManager() {
        this.mAppContext = null;
        this.metadataMap = new ConcurrentHashMap();
        Log.m19d(TAG, "MetadataPool creator !!");
    }

    private static class SingletonHelper {
        /* access modifiers changed from: private */
        public static SimpleMetadataRepositoryManager mInstance = new SimpleMetadataRepositoryManager();

        private SingletonHelper() {
        }
    }

    public static SimpleMetadataRepositoryManager getInstance() {
        return SingletonHelper.mInstance;
    }

    public void setApplicationContext(Context context) {
        this.mAppContext = context;
    }

    public SimpleMetadataRepository getMetadataRepository(String str) {
        Log.m26i(TAG, "getMetadataRepository session:" + str);
        Map<String, SimpleMetadataRepository> map = this.metadataMap;
        if (map != null && !map.containsKey(str)) {
            this.metadataMap.put(str, new SimpleMetadataRepository(this.mAppContext, str));
        }
        return this.metadataMap.get(str);
    }

    public void deleteMetadataRepository(String str) {
        SimpleMetadataRepository remove;
        Log.m26i(TAG, "deleteMetadataRepository session:" + str);
        Map<String, SimpleMetadataRepository> map = this.metadataMap;
        if (map != null && (remove = map.remove(str)) != null) {
            remove.close();
        }
    }
}
