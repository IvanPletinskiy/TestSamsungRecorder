package com.sec.android.app.voicenote.common.util;

import com.sec.android.app.voicenote.common.util.p006db.VNDatabase;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;

public class DataRepository {
    private static DataRepository mInstance;

    public static DataRepository getInstance() {
        if (mInstance == null) {
            synchronized (DataRepository.class) {
                if (mInstance == null) {
                    mInstance = new DataRepository();
                }
            }
        }
        return mInstance;
    }

    private DataRepository() {
    }

    public VNDatabase getVNDatabase() {
        return VNDatabase.getInstance(VoiceNoteApplication.getApplication().getApplicationContext());
    }

    public CategoryRepository getCategoryRepository() {
        return CategoryRepository.getInstance(getVNDatabase());
    }

    public LabelsSearchRepository getLabelSearchRepository() {
        return LabelsSearchRepository.getInstance(getVNDatabase());
    }
}
