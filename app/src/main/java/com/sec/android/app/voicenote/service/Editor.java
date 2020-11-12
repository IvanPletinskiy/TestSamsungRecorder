package com.sec.android.app.voicenote.service;

import android.os.AsyncTask;

import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;

import java.util.ArrayList;
import java.util.Iterator;

public class Editor {
    public static final int DELETE_AFTER_OVERWRITE = 16;
    public static final int DELETE_COMPLETE = 7;
    public static final int DELETE_ERROR = 8;
    public static final int DELETE_START = 6;
    public static final int INFO_EDITOR_PROGRESS = 3011;
    public static final int INFO_EDITOR_STATE = 3010;
    public static final int OVERWRITE_COMPLETE = 1;
    public static final int OVERWRITE_ERROR = 2;
    public static final int OVERWRITE_START = 0;
    public static final int PLAY_AFTER_OVERWRITE = 15;
    public static final int SAVE_AFTER_OVERWRITE = 11;
    public static final int SAVE_AFTER_TRIM = 13;
    public static final int SAVE_AFTER_WRITE_STT_DATA = 20;
    public static final int SAVE_AS_NEW_AFTER_OVERWRITE = 12;
    public static final int SAVE_AS_NEW_AFTER_TRIM = 14;
    private static final String TAG = "Editor";
    public static final int TRIM_AFTER_OVERWRITE = 10;
    public static final int TRIM_COMPLETE = 4;
    public static final int TRIM_ERROR = 5;
    public static final int TRIM_MIN_INTERVAL = 1000;
    public static final int TRIM_START = 3;
    public static final int TRIM_TRANSLATION_COMPLETE = 18;
    public static final int TRIM_TRANSLATION_ERROR = 19;
    public static final int TRIM_TRANSLATION_START = 17;
    private static Editor mInstance;
    private DeleteTask mDeleteTask = null;
    /* access modifiers changed from: private */
    public boolean mIsTranslationFile = false;
    private final ArrayList<OnEditorListener> mListeners = new ArrayList<>();
    private OverwriteTask mOverwriteTask = null;
    /* access modifiers changed from: private */
    public int mState = 1;
    private TrimTask mTrimTask = null;

    public static class EditorState {
        public static final int IDLE = 1;
        public static final int OVERWRITING = 3;
        public static final int TRIMMING = 2;
    }

    public interface OnEditorListener {
        void onEditorUpdate(int i, int i2);
    }

    private Editor() {
        Log.m19d(TAG, "Editor creator !!");
    }

    public static Editor getInstance() {
        if (mInstance == null) {
            synchronized (Editor.class) {
                if (mInstance == null) {
                    mInstance = new Editor();
                }
            }
        }
        return mInstance;
    }

    public final void registerListener(OnEditorListener onEditorListener) {
        if (onEditorListener != null && !this.mListeners.contains(onEditorListener)) {
            this.mListeners.add(onEditorListener);
        }
    }

    public final void unregisterListener(OnEditorListener onEditorListener) {
        if (onEditorListener != null && this.mListeners.contains(onEditorListener)) {
            this.mListeners.remove(onEditorListener);
        }
    }

    /* access modifiers changed from: private */
    public void unregisterAllListener() {
        synchronized (this.mListeners) {
            this.mListeners.clear();
        }
    }

    /* access modifiers changed from: private */
    public void notifyObservers(int i, int i2) {
        synchronized (this.mListeners) {
            Iterator<OnEditorListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                it.next().onEditorUpdate(i, i2);
            }
        }
    }

    public int getEditorState() {
        return this.mState;
    }

    public void setTranslationFile(boolean z) {
        this.mIsTranslationFile = z;
    }

    public void trim(String str, String str2, int i, int i2) {
        Log.m19d(TAG, "trim - path : " + str + "outputPath : " + str2 + " from : " + i + " to : " + i2);
        if (i < 0 || i2 < 0) {
            Log.m19d(TAG, "something wrong !!!");
            return;
        }
        TrimTask trimTask = this.mTrimTask;
        if (trimTask != null && trimTask.getStatus() == AsyncTask.Status.RUNNING) {
            this.mTrimTask.cancel(false);
        }
        if (str.endsWith(AudioFormat.ExtType.EXT_M4A) || str.endsWith(AudioFormat.ExtType.EXT_3GA)) {
            this.mTrimTask = new TrimTask(M4AEditor.getInstance(), i, i2);
        } else {
            this.mTrimTask = new TrimTask(AMREditor.getInstance(), i, i2);
        }
        this.mTrimTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{str, str2});
        SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_EDIT_TYPE, 1);
    }

    public void delete(String str, String str2, int i, int i2) {
        Log.m19d(TAG, "delete - path : " + str + "outputPath : " + str2 + " from : " + i + " to : " + i2);
        if (i < 0 || i2 < 0) {
            Log.m19d(TAG, "something wrong !!!");
            return;
        }
        DeleteTask deleteTask = this.mDeleteTask;
        if (deleteTask != null && deleteTask.getStatus() == AsyncTask.Status.RUNNING) {
            this.mDeleteTask.cancel(true);
        }
        if (str.endsWith(AudioFormat.ExtType.EXT_M4A) || str.endsWith(AudioFormat.ExtType.EXT_3GA)) {
            this.mDeleteTask = new DeleteTask(M4AEditor.getInstance(), i, i2);
        } else {
            this.mDeleteTask = new DeleteTask(AMREditor.getInstance(), i, i2);
        }
        this.mDeleteTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{str, str2});
        SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_EDIT_TYPE, -1);
    }

    public void overwrite(String str, String str2, String str3, int i, int i2) {
        overwrite(str, str2, str3, i, i2, -1);
    }

    public void overwrite(String str, String str2, String str3, int i, int i2, int i3) {
        //str - next(peek)
        //str2 - current(pop)
        String str4 = str;
        String str5 = str2;
        String str6 = str3;
        Log.m19d(TAG, "overwrite - originalPath : " + str + " overwritePath : " + str2 + " outputPath : " + str3 + " from : " + i + " to : " + i2);
        OverwriteTask overwriteTask = this.mOverwriteTask;
        if (overwriteTask != null && overwriteTask.getStatus() == AsyncTask.Status.RUNNING) {
            this.mOverwriteTask.cancel(true);
        }
        if (str.endsWith(AudioFormat.ExtType.EXT_M4A) || str.endsWith(AudioFormat.ExtType.EXT_3GA)) {
            this.mOverwriteTask = new OverwriteTask(M4AEditor.getInstance(), i, i2, i3);
        } else {
            this.mOverwriteTask = new OverwriteTask(AMREditor.getInstance(), i, i2, i3);
        }
        this.mOverwriteTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{str4, str5, str6});
        if (Engine.getInstance().getOriginalFilePath() == null) {
            SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_OVERWRITE, 1);
        } else {
            SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_OVERWRITE, -1);
        }
    }

    private class TrimTask extends AsyncTask<String, Integer, Boolean> {
        private AbsEditor mEditor;
        private int mEventTrimComplete;
        private int mEventTrimError;
        private int mEventTrimStart;
        private int mFromTime;
        private int mToTime;

        public TrimTask(AbsEditor absEditor, int i, int i2) {
            this.mEditor = absEditor;
            this.mFromTime = i;
            this.mToTime = i2;
            if (Editor.this.mIsTranslationFile) {
                this.mEventTrimStart = 17;
                this.mEventTrimComplete = 18;
                this.mEventTrimError = 19;
                return;
            }
            this.mEventTrimStart = 3;
            this.mEventTrimComplete = 4;
            this.mEventTrimError = 5;
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            super.onPreExecute();
            Log.m26i(Editor.TAG, "onPreExecute");
            int unused = Editor.this.mState = 2;
            Editor.this.notifyObservers(Editor.INFO_EDITOR_STATE, this.mEventTrimStart);
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(String... strArr) {
            Log.m26i(Editor.TAG, "doInBackground");
            String str = strArr[0];
            String str2 = strArr[1];
            MetadataRepository instance = MetadataRepository.getInstance();
            if (!Editor.this.mIsTranslationFile) {
                instance.initialize();
                instance.read(str);
            }
            if (this.mEditor.trim(str, str2, this.mFromTime, this.mToTime)) {
                instance.trim(this.mFromTime, this.mToTime);
                MetadataRepository.getInstance().rename(str, str2);
                instance.write(str2);
                Log.m26i(Editor.TAG, "doInBackground success");
                return true;
            }
            Log.m26i(Editor.TAG, "doInBackground fail");
            return false;
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Integer... numArr) {
            super.onProgressUpdate(numArr);
            Editor.this.notifyObservers(Editor.INFO_EDITOR_PROGRESS, numArr[0].intValue());
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            Log.m26i(Editor.TAG, "onPostExecute - result : " + bool);
            int unused = Editor.this.mState = 1;
            if (bool.booleanValue()) {
                Editor.this.notifyObservers(Editor.INFO_EDITOR_STATE, this.mEventTrimComplete);
            } else {
                Editor.this.notifyObservers(Editor.INFO_EDITOR_STATE, this.mEventTrimError);
            }
            Editor.this.unregisterAllListener();
        }

        /* access modifiers changed from: protected */
        public void onCancelled(Boolean bool) {
            super.onCancelled(bool);
            Log.m26i(Editor.TAG, "onCancelled - result : " + bool);
            int unused = Editor.this.mState = 1;
            Editor.this.notifyObservers(Editor.INFO_EDITOR_STATE, this.mEventTrimError);
            Editor.this.unregisterAllListener();
        }
    }

    private class DeleteTask extends AsyncTask<String, Integer, Boolean> {
        private AbsEditor mEditor;
        private int mFromTime;
        private int mToTime;

        public DeleteTask(AbsEditor absEditor, int i, int i2) {
            this.mEditor = absEditor;
            this.mFromTime = i;
            this.mToTime = i2;
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            super.onPreExecute();
            Log.m26i(Editor.TAG, "onPreExecute");
            int unused = Editor.this.mState = 2;
            Editor.this.notifyObservers(Editor.INFO_EDITOR_STATE, 3);
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(String... strArr) {
            Log.m26i(Editor.TAG, "doInBackground");
            String str = strArr[0];
            String str2 = strArr[1];
            MetadataRepository instance = MetadataRepository.getInstance();
            instance.initialize();
            instance.read(str);
            if (this.mEditor.delete(str, str2, this.mFromTime, this.mToTime)) {
                instance.delete(this.mFromTime, this.mToTime);
                instance.rename(str, str2);
                instance.write(str2);
                Log.m26i(Editor.TAG, "doInBackground success");
                return true;
            }
            Log.m26i(Editor.TAG, "doInBackground fail");
            return false;
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Integer... numArr) {
            super.onProgressUpdate(numArr);
            Editor.this.notifyObservers(Editor.INFO_EDITOR_PROGRESS, numArr[0].intValue());
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            Log.m26i(Editor.TAG, "onPostExecute - result : " + bool);
            int unused = Editor.this.mState = 1;
            if (bool.booleanValue()) {
                Editor.this.notifyObservers(Editor.INFO_EDITOR_STATE, 4);
            } else {
                Editor.this.notifyObservers(Editor.INFO_EDITOR_STATE, 5);
            }
            Editor.this.unregisterAllListener();
        }

        /* access modifiers changed from: protected */
        public void onCancelled(Boolean bool) {
            super.onCancelled(bool);
            Log.m26i(Editor.TAG, "onCancelled - result : " + bool);
            int unused = Editor.this.mState = 1;
            Editor.this.notifyObservers(Editor.INFO_EDITOR_STATE, 5);
            Editor.this.unregisterAllListener();
        }
    }

    private class OverwriteTask extends AsyncTask<String, Integer, Boolean> {
        AbsEditor mEditor;
        private int mFromTime;
        private int mToTime;
        private int mUserEvent;

        public OverwriteTask(AbsEditor absEditor, int i, int i2, int i3) {
            this.mEditor = absEditor;
            this.mFromTime = i;
            this.mToTime = i2;
            this.mUserEvent = i3;
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            super.onPreExecute();
            int unused = Editor.this.mState = 3;
            Editor.this.notifyObservers(Editor.INFO_EDITOR_STATE, 0);
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(String... strArr) {
            String str = strArr[0];
            String str2 = strArr[1];
            String str3 = strArr[2];
            if (!this.mEditor.overwrite(str, str2, str3, this.mFromTime, this.mToTime)) {
                return false;
            }
            MetadataRepository instance = MetadataRepository.getInstance();
            instance.rename(str, str3);
            instance.write(str3);
            return true;
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Integer... numArr) {
            super.onProgressUpdate(numArr);
            Editor.this.notifyObservers(Editor.INFO_EDITOR_PROGRESS, numArr[0].intValue());
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            Log.m26i(Editor.TAG, "onPostExecute - result : " + bool + " mUserEvent : " + this.mUserEvent);
            int unused = Editor.this.mState = 1;
            if (bool.booleanValue()) {
                int i = this.mUserEvent;
                if (i != -1) {
                    Editor.this.notifyObservers(Editor.INFO_EDITOR_STATE, i);
                } else {
                    Editor.this.notifyObservers(Editor.INFO_EDITOR_STATE, 1);
                }
            } else {
                Editor.this.notifyObservers(Editor.INFO_EDITOR_STATE, 2);
            }
            int i2 = this.mUserEvent;
            if (i2 != 10 || i2 != 16) {
                Editor.this.unregisterAllListener();
            }
        }

        /* access modifiers changed from: protected */
        public void onCancelled(Boolean bool) {
            super.onCancelled(bool);
            Log.m26i(Editor.TAG, "onCancelled - result : " + bool);
            int unused = Editor.this.mState = 1;
            Editor.this.notifyObservers(Editor.INFO_EDITOR_STATE, 5);
            Editor.this.unregisterAllListener();
        }
    }
}
