package com.sec.android.app.voicenote.common.util;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.StorageProvider;

public class VNMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {
    private static final String TAG = "VNMediaScanner";
    private String mFilePath = null;
    private MediaScannerConnection mScanner;

    public VNMediaScanner(Context context) {
        this.mScanner = new MediaScannerConnection(context, this);
    }

    public void startScan(String str) {
        String convertToSDCardReadOnlyPath = StorageProvider.convertToSDCardReadOnlyPath(str);
        Log.m19d(TAG, "startScan filepath = " + convertToSDCardReadOnlyPath);
        this.mFilePath = convertToSDCardReadOnlyPath;
        this.mScanner.connect();
    }

    public void onMediaScannerConnected() {
        Log.m19d(TAG, "onMediaScannerConnected mFilepath = " + this.mFilePath);
        String str = this.mFilePath;
        if (str != null) {
            this.mScanner.scanFile(str, (String) null);
            this.mFilePath = null;
        }
    }

    public void onScanCompleted(String str, Uri uri) {
        Log.m19d(TAG, "onScanCompleted path = " + str + " , Uri = " + uri);
        this.mScanner.disconnect();
    }
}
