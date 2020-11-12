package com.sec.android.app.voicenote.common.util;

import android.content.ContentValues;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.LongSparseArray;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.TrashHelper;
import com.sec.android.app.voicenote.common.util.p006db.VNDatabase;
import com.sec.android.app.voicenote.data.trash.TrashInfo;
import com.sec.android.app.voicenote.data.trash.TrashObjectInfo;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.DBProvider;
import com.sec.android.app.voicenote.provider.DesktopModeProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.NFCProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.StorageProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.service.BookmarkHolder;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.MetadataRepository;
import com.sec.android.app.voicenote.service.SimpleRecorder;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class TrashHelper {
    private static final int KEEP_IN_TRASH_DAYS = 30;
    private static final String STORAGE_PREFIX = "/storage";
    private static final String TAG = "TrashHelper";
    private static final String TRASH_DIR_NAME = ".Trash";
    private static TrashHelper mInstance;
    /* access modifiers changed from: private */
    public Context mAppContext = null;
    /* access modifiers changed from: private */
    public DeleteTask mDeleteTask = null;
    /* access modifiers changed from: private */
    public int mItemCount = 0;
    /* access modifiers changed from: private */
    public OnTrashProgressListener mListener;
    private ArrayList<TrashObjectInfo> mRestoreObjectInfo;
    /* access modifiers changed from: private */
    public RestoreTask mRestoreTask = null;
    private int mState = 1;
    /* access modifiers changed from: private */
    public ArrayList<TrashObjectInfo> mTrashObjectInfo;

    enum EXCEPTION_CAUSE {
        UNKNOWN,
        FAIL_SAME_NAME,
        FAIL_DEST_EXIST,
        FAIL_SRC_NOT_FOUND
    }

    public interface OnTrashProgressListener {
        void onTrashProgressUpdate(int i, int i2, int i3);
    }

    public static class TrashState {
        public static final int DELETE = 4;
        public static final int DELETE_IN_TRASH = 5;
        public static final int IDLE = 1;
        public static final int MOVE_TO_TRASH = 2;
        public static final int RESTORE = 3;
    }

    public int getKeepInTrashDays() {
        return 30;
    }

    public static TrashHelper getInstance() {
        if (mInstance == null) {
            synchronized (TrashHelper.class) {
                if (mInstance == null) {
                    mInstance = new TrashHelper();
                }
            }
        }
        return mInstance;
    }

    public void setApplicationContext(Context context) {
        this.mAppContext = context;
    }

    public int getState() {
        return this.mState;
    }

    public int getSelectedFileCount() {
        return this.mItemCount;
    }

    public void registerOnTrashProgressListener(OnTrashProgressListener onTrashProgressListener) {
        this.mListener = onTrashProgressListener;
    }

    public String getTrashStringPath(Context context, String str) {
        String externalSDStorageFsUuid = StorageProvider.getExternalSDStorageFsUuid(context);
        File externalFilesDir = context.getExternalFilesDir(TRASH_DIR_NAME);
        if (externalSDStorageFsUuid == null || !str.contains(externalSDStorageFsUuid)) {
            return externalFilesDir.getAbsolutePath();
        }
        String absolutePath = externalFilesDir.getAbsolutePath();
        String path = Environment.getExternalStorageDirectory().getPath();
        return absolutePath.replace(path, "/storage/" + externalSDStorageFsUuid);
    }

    private String addPostfix(String str, int i) {
        int lastIndexOf = str.lastIndexOf(46);
        if (lastIndexOf != -1) {
            String substring = str.substring(0, lastIndexOf);
            String substring2 = str.substring(lastIndexOf, str.length());
            return substring + "-" + i + substring2;
        }
        return str + "-" + i;
    }

    private String getNewFilePath(@NonNull String str) {
        String substring = str.substring(str.lastIndexOf(47) + 1);
        File file = new File(str.substring(0, str.lastIndexOf(47)));
        File file2 = new File(file, substring);
        int i = 1;
        while (file2.exists()) {
            file2 = new File(file, addPostfix(substring, i));
            i++;
        }
        return file2.getAbsolutePath();
    }

    /* access modifiers changed from: private */
    public String move(@NonNull String str, @NonNull String str2, boolean z) throws IOException {
        String str3;
        if (!str.equals(str2)) {
            File file = new File(str2);
            if (!file.exists()) {
                str3 = file.getAbsolutePath();
            } else if (z) {
                str3 = getNewFilePath(file.getAbsolutePath());
            } else {
                throw new IOException("A target already exist : ", new Throwable(EXCEPTION_CAUSE.FAIL_DEST_EXIST.name()));
            }
            if (!new File(str).exists()) {
                Log.m26i(TAG, "srcPath not exist: " + str);
            }
            if (moveFile(str, str3)) {
                return str3;
            }
            throw new IOException("File.renameTo() returns false: " + str + " -> " + str3, new Throwable(EXCEPTION_CAUSE.UNKNOWN.name()));
        }
        throw new IOException("File name is same : " + str, new Throwable(EXCEPTION_CAUSE.FAIL_SAME_NAME.name()));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0010, code lost:
        r1 = r4.getParentFile();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean moveFile(@androidx.annotation.NonNull java.lang.String r4, @androidx.annotation.NonNull java.lang.String r5) {
        /*
            r3 = this;
            java.io.File r0 = new java.io.File
            r0.<init>(r4)
            java.io.File r4 = new java.io.File
            r4.<init>(r5)
            boolean r5 = r0.renameTo(r4)
            if (r5 != 0) goto L_0x0027
            java.io.File r1 = r4.getParentFile()
            if (r1 == 0) goto L_0x0027
            boolean r2 = r1.exists()
            if (r2 != 0) goto L_0x0027
            boolean r1 = r1.mkdirs()
            if (r1 == 0) goto L_0x0027
            boolean r4 = r0.renameTo(r4)
            return r4
        L_0x0027:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.common.util.TrashHelper.moveFile(java.lang.String, java.lang.String):boolean");
    }

    private int getCategoryId(Context context, int i, String str) {
        if (i <= 3) {
            return i;
        }
        if (str == null || str.isEmpty()) {
            return 0;
        }
        CategoryInfo categoryFromTitle = VNDatabase.getInstance(context).mCategoryDao().getCategoryFromTitle(str);
        if (categoryFromTitle != null) {
            return categoryFromTitle.getIdCategory().intValue();
        }
        return DataRepository.getInstance().getCategoryRepository().insertColumn(str, CursorProvider.getInstance().getMaxCategoryPos() + 1);
    }

    public int getNumberTrashItem(Context context) {
        String externalSDStorageFsUuid = StorageProvider.getExternalSDStorageFsUuid(context);
        if (externalSDStorageFsUuid != null) {
            return VNDatabase.getInstance(context).mTrashDao().numberItem(externalSDStorageFsUuid.toLowerCase());
        }
        return VNDatabase.getInstance(context).mTrashDao().numberItem();
    }

    public void startDeleteTask(ArrayList<Long> arrayList, int i) {
        this.mTrashObjectInfo = new ArrayList<>();
        if (i == 14 || (i == 13 && DesktopModeProvider.isDesktopMode())) {
            this.mState = 5;
            List<TrashInfo> dataWithListId = VNDatabase.getInstance(this.mAppContext).mTrashDao().getDataWithListId(arrayList);
            if (dataWithListId != null) {
                this.mItemCount = dataWithListId.size();
                Log.m26i(TAG, "Delete list size: " + this.mItemCount);
                for (int i2 = 0; i2 < dataWithListId.size(); i2++) {
                    this.mTrashObjectInfo.add(new TrashObjectInfo(0, dataWithListId.get(i2), 0));
                }
            }
        } else if (Settings.getBooleanSettings(Settings.KEY_TRASH_IS_TURN_ON)) {
            this.mState = 2;
            LongSparseArray<TrashInfo> listInfoByIds = DBProvider.getInstance().getListInfoByIds(arrayList);
            if (listInfoByIds != null) {
                this.mItemCount = listInfoByIds.size();
                Log.m26i(TAG, "Delete list size: " + this.mItemCount);
//                arrayList.forEach(new Consumer(listInfoByIds) {
//                    private final /* synthetic */ LongSparseArray f$1;
//
//                    {
//                        this.f$1 = r2;
//                    }
//
//                    public final void accept(Object obj) {
//                        TrashHelper.this.lambda$startDeleteTask$0$TrashHelper(this.f$1, (Long) obj);
//                    }
//                });
            }
        } else {
            LongSparseArray<String> listPathByIds = DBProvider.getInstance().getListPathByIds(arrayList);
            if (listPathByIds != null) {
                this.mItemCount = listPathByIds.size();
                Log.m26i(TAG, "Delete list size: " + this.mItemCount);
//                arrayList.forEach(new Consumer(listPathByIds) {
//                    private final /* synthetic */ LongSparseArray f$1;
//
//                    {
//                        this.f$1 = r2;
//                    }
//
//                    public final void accept(Object obj) {
//                        TrashHelper.this.lambda$startDeleteTask$1$TrashHelper(this.f$1, (Long) obj);
//                    }
//                });
            }
        }
        if (!this.mTrashObjectInfo.isEmpty()) {
            this.mDeleteTask = new DeleteTask(i);
            this.mDeleteTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new ArrayList[]{this.mTrashObjectInfo});
        }
    }

    public /* synthetic */ void lambda$startDeleteTask$0$TrashHelper(LongSparseArray longSparseArray, Long l) {
        if (longSparseArray.get(l.longValue()) != null) {
            this.mTrashObjectInfo.add(new TrashObjectInfo(l.longValue(), (TrashInfo) longSparseArray.get(l.longValue()), 0));
        }
    }

    public /* synthetic */ void lambda$startDeleteTask$1$TrashHelper(LongSparseArray longSparseArray, Long l) {
        if (longSparseArray.get(l.longValue()) != null) {
            this.mTrashObjectInfo.add(new TrashObjectInfo(l.longValue(), (String) longSparseArray.get(l.longValue()), 0));
        }
    }

    /* access modifiers changed from: private */
    public void preExcuteDelete(int i) {
        if (i == 10) {
            VoiceNoteObservable.getInstance().notifyObservers(14);
        } else {
            if (i == 5) {
                VoiceNoteObservable.getInstance().notifyObservers(7);
            } else if (i == 14) {
                VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.TRASH_DESELECT));
            } else {
                Engine.getInstance().stopPlay();
                Engine.getInstance().setOriginalFilePath((String) null);
                Engine.getInstance().clearContentItem();
                MetadataRepository.getInstance().close();
            }
            if (i == 14 || i == 13) {
                VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.OPEN_TRASH));
            } else {
                VoiceNoteObservable.getInstance().notifyObservers(3);
            }
        }
        CursorProvider.getInstance().setIsMovingFileToTrashTask(true);
    }

    /* access modifiers changed from: private */
    public void postExcuteDelete(int i) {
        CursorProvider.getInstance().setIsMovingFileToTrashTask(false);
        VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.DELETE_COMPLETE));
        if (i == 5) {
            VoiceNoteObservable.getInstance().notifyObservers(7);
        }
        if (i == 10) {
            VoiceNoteObservable.getInstance().notifyObservers(14);
        } else if (i == 14) {
            VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.TRASH_DESELECT));
        } else if (i == 13) {
            VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.OPEN_TRASH));
        } else {
            VoiceNoteObservable.getInstance().notifyObservers(3);
        }
        this.mDeleteTask = null;
    }

    private class DeleteTask extends AsyncTask<ArrayList<TrashObjectInfo>, Integer, Boolean> {
        private boolean isNeedStop = false;
        private int mScene;
        private int progressCount;

        public DeleteTask(int i) {
            this.mScene = i;
        }

        /* access modifiers changed from: package-private */
        public void setCancelTask(boolean z) {
            this.isNeedStop = z;
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            TrashHelper.this.preExcuteDelete(this.mScene);
            super.onPreExecute();
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            DeleteTask unused = TrashHelper.this.mDeleteTask = null;
            ArrayList unused2 = TrashHelper.this.mTrashObjectInfo = null;
            int unused3 = TrashHelper.this.mItemCount = 0;
            TrashHelper.this.postExcuteDelete(this.mScene);
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(ArrayList<TrashObjectInfo>[] arrayListArr) {
            Log.m26i(TrashHelper.TAG, "doInBackground");
            int i = 0;
            ArrayList<TrashObjectInfo> arrayList = arrayListArr[0];
            this.progressCount = 0;
            if (arrayList == null) {
                Log.m22e(TrashHelper.TAG, "deleteFile list is null");
                return false;
            }
            int i2 = this.mScene;
            if (i2 == 14 || (i2 == 13 && DesktopModeProvider.isDesktopMode())) {
                while (i < arrayList.size()) {
                    TrashObjectInfo trashObjectInfo = arrayList.get(i);
                    i++;
                    this.progressCount = i;
                    if (TrashHelper.this.mListener == null) {
                        return true;
                    }
                    ThreadUtil.postOnUiThread(new Runnable() {
                        public final void run() {
                            TrashHelper.DeleteTask.this.lambda$doInBackground$0$TrashHelper$DeleteTask();
                        }
                    });
                    if (trashObjectInfo != null) {
                        TrashHelper trashHelper = TrashHelper.this;
                        trashHelper.deleteInTrash(trashHelper.mAppContext, trashObjectInfo.getTrashInfo());
                    } else {
                        Log.m22e(TrashHelper.TAG, "fileInfo is null");
                    }
                }
            } else if (Settings.getBooleanSettings(Settings.KEY_TRASH_IS_TURN_ON)) {
                Log.m26i(TrashHelper.TAG, "doInBackground - START DELETE - size: " + arrayList.size());
                while (i < arrayList.size()) {
                    TrashObjectInfo trashObjectInfo2 = (TrashObjectInfo) TrashHelper.this.mTrashObjectInfo.get(i);
                    Log.m26i(TrashHelper.TAG, "START " + i + ": " + trashObjectInfo2.getTrashInfo().getName());
                    if (this.isNeedStop) {
                        return true;
                    }
                    if (TrashHelper.this.mListener == null) {
                        return true;
                    }
                    int i3 = i + 1;
                    this.progressCount = i3;
                    ThreadUtil.postOnUiThread(new Runnable() {
                        public final void run() {
                            TrashHelper.DeleteTask.this.lambda$doInBackground$1$TrashHelper$DeleteTask();
                        }
                    });
                    if (trashObjectInfo2 != null) {
                        TrashHelper.this.updateObjectFileList(i, 1);
                        int moveFileToTrash = moveFileToTrash(i, trashObjectInfo2.getId(), trashObjectInfo2.getTrashInfo());
                        Log.m26i(TrashHelper.TAG, "END " + i + ": " + trashObjectInfo2.getTrashInfo().getName() + " - " + moveFileToTrash);
                    } else {
                        Log.m22e(TrashHelper.TAG, "END " + i + ": fileInfo is null");
                    }
                    i = i3;
                }
            } else {
                while (i < arrayList.size()) {
                    TrashObjectInfo trashObjectInfo3 = arrayList.get(i);
                    i++;
                    this.progressCount = i;
                    if (TrashHelper.this.mListener == null) {
                        return true;
                    }
                    ThreadUtil.postOnUiThread(new Runnable() {
                        public final void run() {
                            TrashHelper.DeleteTask.this.lambda$doInBackground$2$TrashHelper$DeleteTask();
                        }
                    });
                    if (trashObjectInfo3 != null) {
                        deleteFile(trashObjectInfo3.getId(), trashObjectInfo3.getPath());
                    } else {
                        Log.m22e(TrashHelper.TAG, "fileInfo is null");
                    }
                }
            }
            return true;
        }

        public /* synthetic */ void lambda$doInBackground$0$TrashHelper$DeleteTask() {
            if (TrashHelper.this.mListener != null) {
                TrashHelper.this.mListener.onTrashProgressUpdate(Event.DIALOG_PROGRESS_MOVE_FILE, this.progressCount, TrashHelper.this.mItemCount);
            }
        }

        public /* synthetic */ void lambda$doInBackground$1$TrashHelper$DeleteTask() {
            if (TrashHelper.this.mListener != null) {
                TrashHelper.this.mListener.onTrashProgressUpdate(Event.DIALOG_PROGRESS_MOVE_FILE, this.progressCount, TrashHelper.this.mItemCount);
            }
        }

        public /* synthetic */ void lambda$doInBackground$2$TrashHelper$DeleteTask() {
            if (TrashHelper.this.mListener != null) {
                TrashHelper.this.mListener.onTrashProgressUpdate(Event.DIALOG_PROGRESS_MOVE_FILE, this.progressCount, TrashHelper.this.mItemCount);
            }
        }

        private void deleteFile(long j, String str) {
            if (str != null && new File(str).delete()) {
                TrashHelper.this.mAppContext.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "_id=" + j, (String[]) null);
                if (VoiceNoteFeature.isGateEnabled()) {
                    android.util.Log.i("GATE", "<GATE-M> AUDIO_DELETED </GATE-M>");
                }
                BookmarkHolder.getInstance().remove(str);
                int lastIndexOf = str.lastIndexOf(46);
                if (lastIndexOf > 0) {
                    String str2 = str.substring(0, lastIndexOf) + "_memo.txt";
                    File file = new File(str2);
                    if (!file.exists()) {
                        return;
                    }
                    if (!file.delete()) {
                        Log.m32w(TrashHelper.TAG, "Delete the text file : fail");
                        return;
                    }
                    TrashHelper.this.mAppContext.getContentResolver().delete(MediaStore.Files.getContentUri("external"), "_data=\"" + StorageProvider.convertToSDCardReadOnlyPath(str2) + '\"', (String[]) null);
                }
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:14:0x00cb  */
        /* JADX WARNING: Removed duplicated region for block: B:20:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private int moveFileToTrash(int r10, long r11, com.sec.android.app.voicenote.data.trash.TrashInfo r13) {
            /*
                r9 = this;
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r1 = "move start: "
                r0.append(r1)
                r0.append(r11)
                java.lang.String r0 = r0.toString()
                java.lang.String r1 = "TrashHelper"
                com.sec.android.app.voicenote.provider.Log.m26i(r1, r0)
                java.lang.String r0 = r13.getRestorePath()
                r2 = 0
                r3 = 1
                if (r0 == 0) goto L_0x00f4
                com.sec.android.app.voicenote.common.util.TrashHelper r0 = com.sec.android.app.voicenote.common.util.TrashHelper.this
                android.content.Context r4 = r0.mAppContext
                java.lang.String r5 = r13.getRestorePath()
                java.lang.String r0 = r0.getTrashStringPath(r4, r5)
                java.lang.String r4 = r13.getRestorePath()
                r5 = 46
                int r4 = r4.lastIndexOf(r5)
                java.lang.String r5 = r13.getRestorePath()
                java.lang.String r5 = r5.substring(r4)
                long r6 = java.lang.System.currentTimeMillis()
                java.lang.StringBuilder r8 = new java.lang.StringBuilder
                r8.<init>()
                r8.append(r0)
                java.lang.String r0 = "/"
                r8.append(r0)
                java.lang.String r0 = r13.getName()
                r8.append(r0)
                java.lang.String r0 = "_"
                r8.append(r0)
                r8.append(r6)
                java.lang.String r0 = r8.toString()
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                r6.append(r0)
                r6.append(r5)
                java.lang.String r0 = r6.toString()
                r13.setPath(r0)
                if (r4 <= 0) goto L_0x009c
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r5 = r13.getRestorePath()
                java.lang.String r4 = r5.substring(r2, r4)
                r0.append(r4)
                java.lang.String r4 = "_memo.txt"
                r0.append(r4)
                java.lang.String r0 = r0.toString()
                java.io.File r4 = new java.io.File
                r4.<init>(r0)
                boolean r0 = r4.exists()
                if (r0 == 0) goto L_0x009c
                r0 = r3
                goto L_0x009d
            L_0x009c:
                r0 = r2
            L_0x009d:
                r13.setIsMemo(r0)
                com.sec.android.app.voicenote.common.util.TrashHelper r0 = com.sec.android.app.voicenote.common.util.TrashHelper.this     // Catch:{ IOException -> 0x00af }
                java.lang.String r4 = r13.getRestorePath()     // Catch:{ IOException -> 0x00af }
                java.lang.String r5 = r13.getPath()     // Catch:{ IOException -> 0x00af }
                java.lang.String r0 = r0.move(r4, r5, r3)     // Catch:{ IOException -> 0x00af }
                goto L_0x00c9
            L_0x00af:
                r0 = move-exception
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "Move failed. "
                r4.append(r5)
                java.lang.String r0 = r0.getMessage()
                r4.append(r0)
                java.lang.String r0 = r4.toString()
                com.sec.android.app.voicenote.provider.Log.m22e(r1, r0)
                r0 = 0
            L_0x00c9:
                if (r0 == 0) goto L_0x00f4
                com.sec.android.app.voicenote.common.util.TrashHelper r0 = com.sec.android.app.voicenote.common.util.TrashHelper.this
                r0.updateObjectFileList(r10, r3)
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r2 = "moved success : "
                r0.append(r2)
                r0.append(r11)
                java.lang.String r0 = r0.toString()
                com.sec.android.app.voicenote.provider.Log.m26i(r1, r0)
                com.sec.android.app.voicenote.common.util.TrashHelper r0 = com.sec.android.app.voicenote.common.util.TrashHelper.this
                int r11 = r0.insertInfoToTrashDb(r11, r13)
                if (r11 != r3) goto L_0x00f3
                r2 = 2
                com.sec.android.app.voicenote.common.util.TrashHelper r11 = com.sec.android.app.voicenote.common.util.TrashHelper.this
                r11.updateObjectFileList(r10, r2)
                goto L_0x00f4
            L_0x00f3:
                r2 = r3
            L_0x00f4:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.common.util.TrashHelper.DeleteTask.moveFileToTrash(int, long, com.sec.android.app.voicenote.data.trash.TrashInfo):int");
        }
    }

    /* access modifiers changed from: private */
    public synchronized void updateObjectFileList(int i, int i2) {
        TrashObjectInfo trashObjectInfo = this.mTrashObjectInfo.get(i);
        trashObjectInfo.setStatus(i2);
        this.mTrashObjectInfo.set(i, trashObjectInfo);
    }

    /* access modifiers changed from: private */
    public synchronized void updateRestoreObjectFileList(int i, int i2, String str) {
        TrashObjectInfo trashObjectInfo = this.mRestoreObjectInfo.get(i);
        trashObjectInfo.setStatus(i2);
        trashObjectInfo.setPath(str);
        this.mRestoreObjectInfo.set(i, trashObjectInfo);
    }

    /* access modifiers changed from: private */
    public int insertInfoToTrashDb(long j, TrashInfo trashInfo) {
        long j2;
        String str;
        Context context = this.mAppContext;
        if (context == null) {
            return 0;
        }
        context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "_id=" + j, (String[]) null);
        if (VoiceNoteFeature.isGateEnabled()) {
            android.util.Log.i("GATE", "<GATE-M> AUDIO_DELETED </GATE-M>");
        }
        BookmarkHolder.getInstance().remove(trashInfo.getRestorePath());
        int lastIndexOf = trashInfo.getRestorePath().lastIndexOf(46);
        String replace = trashInfo.getPath().replace(trashInfo.getRestorePath().substring(lastIndexOf), "");
        Log.m26i(TAG, "check memo file");
        if (trashInfo.getIsMemo() > 0) {
            Log.m26i(TAG, "move memo file");
            String str2 = trashInfo.getRestorePath().substring(0, lastIndexOf) + "_memo.txt";
            try {
                str = move(str2, replace + "_memo.txt", true);
            } catch (IOException e) {
                Log.m22e(TAG, "Move failed. " + e.getMessage());
                str = null;
            }
            if (str == null) {
                Log.m32w(TAG, "Delete the text file : fail");
            } else {
                this.mAppContext.getContentResolver().delete(MediaStore.Files.getContentUri("external"), "_data=\"" + StorageProvider.convertToSDCardReadOnlyPath(str2) + '\"', (String[]) null);
            }
        }
        if (trashInfo.getDateTaken() <= 0) {
            try {
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(trashInfo.getPath());
                j2 = Long.parseLong(mediaMetadataRetriever.extractMetadata(SimpleRecorder.INFO_NO_SOUND_DETECT_VIBRATE));
            } catch (Exception unused) {
                j2 = trashInfo.getDateModified() * 1000;
            }
            trashInfo.setDateTaken(j2);
        }
        Log.m26i(TAG, "insert to Trash db: " + j);
        if (DataRepository.getInstance().getVNDatabase().mTrashDao().trashInfoWithPath(trashInfo.getPath()) != null) {
            return 1;
        }
        if (DataRepository.getInstance().getVNDatabase().mTrashDao().insertReplace(trashInfo) < 0) {
            Log.m22e(TAG, "error insert: " + trashInfo.getRestorePath());
            return 0;
        }
        Log.m26i(TAG, "insert db success.");
        List<TrashInfo> trashListWithPath = DataRepository.getInstance().getVNDatabase().mTrashDao().trashListWithPath(trashInfo.getPath());
        if (trashListWithPath == null || trashListWithPath.size() <= 1) {
            return 1;
        }
        for (int i = 1; i < trashListWithPath.size(); i++) {
            VNDatabase.getInstance(this.mAppContext).mTrashDao().deleteDataWithID(trashListWithPath.get(i).getIdFile().intValue());
        }
        return 1;
    }

    public void startRestoreTask(ArrayList<Long> arrayList, int i) {
        this.mRestoreObjectInfo = new ArrayList<>();
        this.mState = 3;
        List<TrashInfo> dataWithListId = VNDatabase.getInstance(this.mAppContext).mTrashDao().getDataWithListId(arrayList);
        if (dataWithListId != null) {
            this.mItemCount = dataWithListId.size();
            Log.m26i(TAG, "Restore list size: " + this.mItemCount);
            for (int i2 = 0; i2 < dataWithListId.size(); i2++) {
                this.mRestoreObjectInfo.add(new TrashObjectInfo(0, dataWithListId.get(i2), 0));
            }
        }
        if (!this.mRestoreObjectInfo.isEmpty()) {
            this.mRestoreTask = new RestoreTask(i);
            this.mRestoreTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new ArrayList[]{this.mRestoreObjectInfo});
        }
    }

    private class RestoreTask extends AsyncTask<ArrayList<TrashObjectInfo>, Integer, Boolean> {
        private boolean isNeedStop = false;
        private int mScene;
        private int progressCount;

        public RestoreTask(int i) {
            this.mScene = i;
        }

        /* access modifiers changed from: package-private */
        public void setCancelTask(boolean z) {
            this.isNeedStop = z;
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            if (this.mScene == 14) {
                VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.TRASH_DESELECT));
            } else {
                Engine.getInstance().stopPlay();
                Engine.getInstance().setOriginalFilePath((String) null);
                Engine.getInstance().clearContentItem();
                MetadataRepository.getInstance().close();
            }
            super.onPreExecute();
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.RESTORE_COMPLETE));
            VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.TRASH_DESELECT));
            VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.OPEN_TRASH));
            if (bool.booleanValue()) {
                if (TrashHelper.this.mItemCount <= 1) {
                    Toast.makeText(TrashHelper.this.mAppContext, TrashHelper.this.mAppContext.getResources().getString(C0690R.string.trash_recording_restored), 0).show();
                } else {
                    Toast.makeText(TrashHelper.this.mAppContext, TrashHelper.this.mAppContext.getResources().getString(C0690R.string.trash_recordings_restored), 0).show();
                }
            }
            int unused = TrashHelper.this.mItemCount = 0;
            RestoreTask unused2 = TrashHelper.this.mRestoreTask = null;
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(ArrayList<TrashObjectInfo>[] arrayListArr) {
            int i = 0;
            ArrayList<TrashObjectInfo> arrayList = arrayListArr[0];
            this.progressCount = 0;
            if (arrayList == null) {
                Log.m22e(TrashHelper.TAG, "restoreFile list is null");
                return false;
            }
            Log.m26i(TrashHelper.TAG, "doInBackground - START RESTORE - size: " + arrayList.size());
            while (i < arrayList.size()) {
                TrashObjectInfo trashObjectInfo = arrayList.get(i);
                Log.m22e(TrashHelper.TAG, "START " + i + ": " + trashObjectInfo.getTrashInfo().getName());
                if (this.isNeedStop) {
                    return true;
                }
                if (TrashHelper.this.mListener == null) {
                    return true;
                }
                int i2 = i + 1;
                this.progressCount = i2;
                ThreadUtil.postOnUiThread(new Runnable() {
                    public final void run() {
                        TrashHelper.RestoreTask.this.lambda$doInBackground$0$TrashHelper$RestoreTask();
                    }
                });
                if (trashObjectInfo != null) {
                    int restoreFile = restoreFile(i, trashObjectInfo.getTrashInfo());
                    Log.m26i(TrashHelper.TAG, "END " + i + ": " + trashObjectInfo.getTrashInfo().getName() + " - " + restoreFile);
                } else {
                    Log.m22e(TrashHelper.TAG, "END " + i + ": fileInfo is null");
                }
                i = i2;
            }
            return true;
        }

        public /* synthetic */ void lambda$doInBackground$0$TrashHelper$RestoreTask() {
            if (TrashHelper.this.mListener != null) {
                TrashHelper.this.mListener.onTrashProgressUpdate(Event.DIALOG_PROGRESS_MOVE_FILE, this.progressCount, TrashHelper.this.mItemCount);
            }
        }

        private int restoreFile(int i, TrashInfo trashInfo) {
            String str;
            try {
                str = TrashHelper.this.move(trashInfo.getPath(), trashInfo.getRestorePath(), true);
            } catch (IOException e) {
                Log.m22e(TrashHelper.TAG, "Move failed. " + e.getMessage());
                str = null;
            }
            if (str == null) {
                return 0;
            }
            Log.m26i(TrashHelper.TAG, "move success");
            TrashHelper.this.updateRestoreObjectFileList(i, 1, str);
            Log.m26i(TrashHelper.TAG, "moved success : " + trashInfo.getIdFile());
            if (TrashHelper.this.updateMediaData(str, trashInfo) != 1) {
                return 1;
            }
            TrashHelper.this.updateRestoreObjectFileList(i, 2, str);
            return 2;
        }
    }

    /* access modifiers changed from: private */
    public int updateMediaData(String str, TrashInfo trashInfo) {
        int lastIndexOf;
        Log.m26i(TAG, "resultInsert: " + (DataRepository.getInstance().getVNDatabase().mTrashDao().deleteDataWithID(trashInfo.getIdFile().intValue()) > -1));
        String convertToSDCardReadOnlyPath = StorageProvider.convertToSDCardReadOnlyPath(str);
        ContentValues contentValues = new ContentValues();
        contentValues.put("recordingtype", Integer.valueOf(trashInfo.getRecordingType()));
        contentValues.put("recording_mode", Integer.valueOf(trashInfo.getRecordingMode()));
        contentValues.put(DialogFactory.BUNDLE_LABEL_ID, Integer.valueOf(getCategoryId(this.mAppContext, trashInfo.getCategoryId(), trashInfo.getCategoryName())));
        contentValues.put("is_memo", Integer.valueOf(trashInfo.getIsMemo()));
        contentValues.put("duration", Long.valueOf(trashInfo.getDuration()));
        String substring = convertToSDCardReadOnlyPath.substring(convertToSDCardReadOnlyPath.lastIndexOf(47) + 1);
        contentValues.put("title", substring.substring(0, substring.lastIndexOf(46)));
        contentValues.put("_data", convertToSDCardReadOnlyPath);
        contentValues.put(NFCProvider.NFC_DB_KEY, trashInfo.getYearName());
        contentValues.put("mime_type", trashInfo.getMimeType());
        contentValues.put("datetaken", Long.valueOf(trashInfo.getDateTaken()));
        contentValues.put("date_modified", Long.valueOf(trashInfo.getDateModified()));
        if (DBProvider.getInstance().insertDB(convertToSDCardReadOnlyPath, contentValues) != null) {
            Log.m29v(TAG, "saveFileToMediaDB - insertDB success : " + convertToSDCardReadOnlyPath);
        } else {
            Log.m22e(TAG, "saveFileToMediaDB - insert failed");
        }
        if (trashInfo.getIsMemo() == 1 && (lastIndexOf = trashInfo.getPath().lastIndexOf(46)) > 0) {
            String str2 = trashInfo.getPath().substring(0, lastIndexOf) + "_memo.txt";
            int lastIndexOf2 = trashInfo.getRestorePath().lastIndexOf(46);
            if (lastIndexOf2 > 0) {
                try {
                    move(str2, trashInfo.getRestorePath().substring(0, lastIndexOf2) + "_memo.txt", true);
                } catch (IOException e) {
                    Log.m22e(TAG, "Move failed. " + e.getMessage());
                }
            }
        }
        return 1;
    }

    /* access modifiers changed from: private */
    public void deleteInTrash(Context context, TrashInfo trashInfo) {
        deleteFile(context, trashInfo);
    }

    public void emptyTrash(Context context) {
        List<TrashInfo> list;
        String externalSDStorageFsUuid = StorageProvider.getExternalSDStorageFsUuid(context);
        if (externalSDStorageFsUuid != null) {
            list = VNDatabase.getInstance(context).mTrashDao().getAllData(externalSDStorageFsUuid.toLowerCase());
        } else {
            list = VNDatabase.getInstance(context).mTrashDao().getAllData();
        }
        if (list.size() > 0) {
            new EmptyTrashTask(context).execute(new List[]{list});
        }
    }

    private class EmptyTrashTask extends AsyncTask<List<TrashInfo>, Integer, Boolean> {
        private Context mContext;

        public EmptyTrashTask(Context context) {
            this.mContext = context;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.DELETE_TRASH_COMPLETE));
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(List<TrashInfo>[] listArr) {
            List<TrashInfo> list = listArr[0];
            if (list == null) {
                return false;
            }
            ((Stream) list.stream().parallel()).forEach(new Consumer() {
                public final void accept(Object obj) {
                    TrashHelper.EmptyTrashTask.this.lambda$doInBackground$0$TrashHelper$EmptyTrashTask((TrashInfo) obj);
                }
            });
            return true;
        }

        public /* synthetic */ void lambda$doInBackground$0$TrashHelper$EmptyTrashTask(TrashInfo trashInfo) {
            Log.m26i(TrashHelper.TAG, "delete trash info:" + trashInfo.getPath());
            TrashHelper.this.deleteFile(this.mContext, trashInfo);
        }
    }

    public void checkFileInTrashToDelete() {
        new CheckTrashToDelFileTask().execute(new ArrayList[0]);
    }

    private class CheckTrashToDelFileTask extends AsyncTask<ArrayList<Long>, Integer, Boolean> {
        private boolean isNeedUpdate;

        private CheckTrashToDelFileTask() {
            this.isNeedUpdate = false;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            if (this.isNeedUpdate) {
                VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.DELETE_TRASH_COMPLETE));
            }
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(ArrayList<Long>[] arrayListArr) {
            List<TrashInfo> list;
            Log.m26i(TrashHelper.TAG, "doInBackground: check time deleted of file in Trash db");
            String externalSDStorageFsUuid = StorageProvider.getExternalSDStorageFsUuid(TrashHelper.this.mAppContext);
            if (externalSDStorageFsUuid != null) {
                Log.m26i(TrashHelper.TAG, "fsuuid:" + externalSDStorageFsUuid);
                list = VNDatabase.getInstance(TrashHelper.this.mAppContext).mTrashDao().getAllData(externalSDStorageFsUuid.toLowerCase());
            } else {
                list = VNDatabase.getInstance(TrashHelper.this.mAppContext).mTrashDao().getAllData();
            }
            if (list == null) {
                return false;
            }
            ((Stream) list.stream().parallel()).forEach(new Consumer() {
                public final void accept(Object obj) {
                    TrashHelper.CheckTrashToDelFileTask.this.lambda$doInBackground$0$TrashHelper$CheckTrashToDelFileTask((TrashInfo) obj);
                }
            });
            return true;
        }

        public /* synthetic */ void lambda$doInBackground$0$TrashHelper$CheckTrashToDelFileTask(TrashInfo trashInfo) {
            if (TrashHelper.this.checkExistFile(trashInfo)) {
                if (isNeedDelFile(System.currentTimeMillis() - trashInfo.getDeleteTime())) {
                    TrashHelper trashHelper = TrashHelper.this;
                    trashHelper.deleteFile(trashHelper.mAppContext, trashInfo);
                    this.isNeedUpdate = true;
                }
            }
        }

        private boolean isNeedDelFile(long j) {
            return ((int) (j / 1000)) / 3600 > 720;
        }
    }

    /* access modifiers changed from: private */
    public boolean checkExistFile(TrashInfo trashInfo) {
        String path = trashInfo.getPath();
        if (path == null) {
            return false;
        }
        if (new File(path).exists()) {
            return true;
        }
        int deleteDataWithID = VNDatabase.getInstance(this.mAppContext).mTrashDao().deleteDataWithID(trashInfo.getIdFile().intValue());
        Log.m32w(TAG, "Not exist file, delete in db: " + deleteDataWithID);
        return false;
    }

    /* access modifiers changed from: private */
    public void deleteFile(Context context, TrashInfo trashInfo) {
        String path = trashInfo.getPath();
        if (path != null) {
            if (new File(path).delete()) {
                int lastIndexOf = path.lastIndexOf(46);
                if (lastIndexOf > 0) {
                    File file = new File(path.substring(0, lastIndexOf) + "_memo.txt");
                    if (file.exists()) {
                        if (!file.delete()) {
                            Log.m32w(TAG, "Delete the text file : fail");
                        } else {
                            Log.m32w(TAG, "Delete the text file : success");
                        }
                    }
                }
            } else {
                Log.m32w(TAG, "Delete the file : fail");
            }
            int deleteDataWithID = VNDatabase.getInstance(context).mTrashDao().deleteDataWithID(trashInfo.getIdFile().intValue());
            Log.m32w(TAG, "Delete result: " + deleteDataWithID);
        }
    }

    public synchronized void onDestroy(boolean z) {
        Log.m26i(TAG, "onDestroy: " + z);
        if (!z) {
            if (this.mDeleteTask != null) {
                this.mDeleteTask.setCancelTask(true);
                if (this.mDeleteTask.cancel(true) && this.mTrashObjectInfo != null && this.mTrashObjectInfo.size() > 0) {
                    for (int i = 0; i < this.mTrashObjectInfo.size(); i++) {
                        TrashObjectInfo trashObjectInfo = this.mTrashObjectInfo.get(i);
                        if (trashObjectInfo.getStatus() == 1) {
                            Log.m26i(TAG, "resume delete file info: " + trashObjectInfo.getId() + " - status: " + trashObjectInfo.getStatus());
                            insertInfoToTrashDb(trashObjectInfo.getId(), trashObjectInfo.getTrashInfo());
                        }
                    }
                }
                this.mDeleteTask = null;
            }
            if (this.mRestoreTask != null) {
                this.mRestoreTask.setCancelTask(true);
                if (this.mRestoreTask.cancel(true) && this.mRestoreObjectInfo != null && this.mRestoreObjectInfo.size() > 0) {
                    for (int i2 = 0; i2 < this.mRestoreObjectInfo.size(); i2++) {
                        TrashObjectInfo trashObjectInfo2 = this.mRestoreObjectInfo.get(i2);
                        if (trashObjectInfo2.getStatus() == 1) {
                            Log.m26i(TAG, "resume restore file - updateMediaData: " + trashObjectInfo2.getPath());
                            updateMediaData(trashObjectInfo2.getPath(), trashObjectInfo2.getTrashInfo());
                        }
                    }
                }
                this.mRestoreTask = null;
            }
            this.mListener = null;
        }
    }
}
