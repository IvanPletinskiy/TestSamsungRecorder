package com.sec.android.app.voicenote.provider;

import android.content.Context;

import java.util.ArrayList;

public class SecureFolderProvider {
    private static final int INSIDE_SECURE_FOLDER = 1;
    private static final int KNOX_CONTAINER_VERSION = 280;
    private static final int OUTSIDE_SECURE_FOLDER = 0;
    private static final String TAG = "SecureFolderProvider";
    private static final int VOICE_RECORDER_SECURE_FOLDER_ID = 16;
    private static int[] mContainerID = new int[2];
    private static String mContainerName;
    private static boolean[] mSecureFolderStatus = new boolean[2];

    public static boolean isSecureFolderSupported() {
//        return SemPersonaManager.isKnoxVersionSupported(KNOX_CONTAINER_VERSION);
        return false;
    }

    public static void getKnoxMenuList(Context context) {
        clearList();
//        ArrayList moveToKnoxMenuList = ((SemPersonaManager) context.getSystemService("persona")).getMoveToKnoxMenuList(context);
//        if (moveToKnoxMenuList != null && !moveToKnoxMenuList.isEmpty()) {
//            for (int i = 0; i < moveToKnoxMenuList.size(); i++) {
//                Bundle bundle = (Bundle) moveToKnoxMenuList.get(i);
//                if (bundle != null) {
//                    int i2 = bundle.getInt("com.sec.knox.moveto.containerType");
//                    if (i2 == 1002) {
//                        mContainerID[0] = bundle.getInt("com.sec.knox.moveto.containerId");
//                        mContainerName = bundle.getString("com.sec.knox.moveto.name");
//                        mSecureFolderStatus[0] = true;
//                    }
//                    if (i2 == 1003) {
//                        mContainerID[1] = bundle.getInt("com.sec.knox.moveto.containerId");
//                        mContainerName = bundle.getString("com.sec.knox.moveto.name");
//                        mSecureFolderStatus[1] = true;
//                    }
//                }
//            }
//        }
    }

    public static boolean isInSecureFolder() {
//        return SemPersonaManager.isSecureFolderId(UserHandle.semGetMyUserId());
        return false;
    }

    public static String getKnoxName() {
        return mContainerName;
    }

    public static boolean isInsideSecureFolder() {
        return mSecureFolderStatus[1];
    }

    public static boolean isOutsideSecureFolder() {
        return mSecureFolderStatus[0];
    }

    private static void clearList() {
        boolean[] zArr = mSecureFolderStatus;
        zArr[1] = false;
        zArr[0] = false;
    }

    public static void moveFilesToSecureFolder(Context context, ArrayList<Long> arrayList) {
        ArrayList<String> pathByIds = CursorProvider.getInstance().getPathByIds(arrayList);
//        if (pathByIds != null) {
//            try {
//                ((SemRemoteContentManager) context.getSystemService("rcp")).moveFiles(16, pathByIds, pathByIds, mContainerID[0]);
//            } catch (RemoteException e) {
//                Log.m22e(TAG, e.toString());
//            }
//        }
    }

    public static void moveFilesOutOfSecureFolder(Context context, ArrayList<Long> arrayList) {
        ArrayList<String> pathByIds = CursorProvider.getInstance().getPathByIds(arrayList);
//        if (pathByIds != null) {
//            try {
//                ((SemRemoteContentManager) context.getSystemService("rcp")).moveFiles(16, pathByIds, pathByIds, mContainerID[1]);
//            } catch (RemoteException e) {
//                Log.m22e(TAG, e.toString());
//            }
//        }
    }
}
