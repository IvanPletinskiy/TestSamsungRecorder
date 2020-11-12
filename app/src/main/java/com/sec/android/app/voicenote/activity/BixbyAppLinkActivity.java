package com.sec.android.app.voicenote.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.sec.android.app.voicenote.bixby.constant.BixbyConstant;
import com.sec.android.app.voicenote.main.VNMainActivity;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.UpdateProvider;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class BixbyAppLinkActivity extends AppCompatActivity implements Observer {
    private static final String TAG = "BixbyAppLinkActivity";
    private String mBixbyStartData;
    private String mId;
    private final VoiceNoteObservable mObservable = VoiceNoteObservable.getInstance();

    public static class RecordingMode {
        public static final String INTERVIEW = "interview";
        public static final String SPEECH_TO_TEXT = "speech-to-text";
        public static final String STANDARD = "standard";
    }

    private void addObserver(Observer observer) {
        this.mObservable.addObserver(observer);
    }

    private void deleteObserver(Observer observer) {
        this.mObservable.deleteObserver(observer);
    }

    public void update(Observable observable, Object obj) {
        int intValue = ((Integer) obj).intValue();
        Log.m26i(TAG, "update " + intValue);
        switch (intValue) {
            case Event.BIXBY_START_RECORDING_RESULT_FAIL /*29996*/:
            case Event.BIXBY_START_RECORDING_RESULT_SUCCESS /*29997*/:
            case Event.BIXBY_READY_TO_START_RECORDING /*29998*/:
                finish();
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        Log.m19d(TAG, "onCreate appLinkAction " + action);
        Log.m19d(TAG, "onCreate appLinkData " + data);
        addObserver(this);
        prepare(data);
    }

    private void prepare(Uri uri) {
        if (uri == null) {
            Log.m22e(TAG, "prepare - applinkData is null!!!");
            return;
        }
        List<String> pathSegments = uri.getPathSegments();
        if (pathSegments == null) {
            Log.m22e(TAG, "prepare - data segment is null!!!");
            return;
        }
        String str = pathSegments.get(0);
        if (str == null) {
            Log.m22e(TAG, "action is null|||");
            return;
        }
        this.mBixbyStartData = BixbyConstant.BixbyStartMode.BIXBY_START_DEFAULT;
        this.mId = UpdateProvider.StubCodes.UPDATE_CHECK_FAIL;
        Log.m26i(TAG, "Action name: " + str);
        char c = 65535;
        int hashCode = str.hashCode();
        if (hashCode != -1598966521) {
            if (hashCode == -1349266015 && str.equals(BixbyConstant.BixbyActions.ACTION_START_RECORDING)) {
                c = 0;
            }
        } else if (str.equals(BixbyConstant.BixbyActions.ACTION_PLAY_RECORDING_FILE)) {
            c = 1;
        }
        if (c == 0) {
            prepareStartRecording(uri);
        } else if (c == 1) {
            preparePlayRecordedFile(uri);
        }
    }

    private void preparePlayRecordedFile(Uri uri) {
        this.mId = getFileNameID(uri);
        this.mBixbyStartData = BixbyConstant.BixbyStartMode.BIXBY_START_PLAY;
        startMainActivity(getApplicationContext());
    }

    private String getFileNameID(Uri uri) {
        Log.m19d(TAG, "getFileNameID appLinkData " + uri.toString());
        try {
            String[] split = uri.toString().split("=");
            return split[split.length - 1];
        } catch (Exception unused) {
            Log.m19d(TAG, "getFileNameID occur exception");
            return UpdateProvider.StubCodes.UPDATE_CHECK_FAIL;
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        Log.m19d(TAG, "onResume");
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        Log.m19d(TAG, "onDestroy");
        deleteObserver(this);
        super.onDestroy();
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0045  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0056  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void prepareStartRecording(android.net.Uri r6) {
        /*
            r5 = this;
            java.lang.String r0 = "BixbyAppLinkActivity"
            java.lang.String r1 = "prepareStartRecording"
            com.sec.android.app.voicenote.provider.Log.m19d(r0, r1)
            java.lang.String r6 = r5.getRecordingMode(r6)
            int r0 = r6.hashCode()
            r1 = 503107969(0x1dfcd181, float:6.6920467E-21)
            r2 = -1
            r3 = 2
            r4 = 1
            if (r0 == r1) goto L_0x0036
            r1 = 1182472020(0x467b1754, float:16069.832)
            if (r0 == r1) goto L_0x002c
            r1 = 1312628413(0x4e3d1ebd, float:7.9322707E8)
            if (r0 == r1) goto L_0x0022
            goto L_0x0040
        L_0x0022:
            java.lang.String r0 = "standard"
            boolean r6 = r6.equals(r0)
            if (r6 == 0) goto L_0x0040
            r6 = 0
            goto L_0x0041
        L_0x002c:
            java.lang.String r0 = "speech-to-text"
            boolean r6 = r6.equals(r0)
            if (r6 == 0) goto L_0x0040
            r6 = r3
            goto L_0x0041
        L_0x0036:
            java.lang.String r0 = "interview"
            boolean r6 = r6.equals(r0)
            if (r6 == 0) goto L_0x0040
            r6 = r4
            goto L_0x0041
        L_0x0040:
            r6 = r2
        L_0x0041:
            java.lang.String r0 = "record_mode"
            if (r6 == 0) goto L_0x0056
            if (r6 == r4) goto L_0x0052
            if (r6 == r3) goto L_0x004d
            com.sec.android.app.voicenote.provider.Settings.setSettings((java.lang.String) r0, (int) r4)
            goto L_0x0059
        L_0x004d:
            r6 = 4
            com.sec.android.app.voicenote.provider.Settings.setSettings((java.lang.String) r0, (int) r6)
            goto L_0x0059
        L_0x0052:
            com.sec.android.app.voicenote.provider.Settings.setSettings((java.lang.String) r0, (int) r3)
            goto L_0x0059
        L_0x0056:
            com.sec.android.app.voicenote.provider.Settings.setSettings((java.lang.String) r0, (int) r4)
        L_0x0059:
            com.sec.android.app.voicenote.provider.CursorProvider r6 = com.sec.android.app.voicenote.provider.CursorProvider.getInstance()
            r6.resetSearchTag()
            com.sec.android.app.voicenote.common.util.DataRepository r6 = com.sec.android.app.voicenote.common.util.DataRepository.getInstance()
            com.sec.android.app.voicenote.common.util.CategoryRepository r6 = r6.getCategoryRepository()
            r6.setCurrentCategoryID(r2)
            java.lang.String r6 = "bixbyStartRecord"
            r5.mBixbyStartData = r6
            android.content.Context r6 = r5.getApplicationContext()
            r5.startMainActivity(r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.activity.BixbyAppLinkActivity.prepareStartRecording(android.net.Uri):void");
    }

    private String getRecordingMode(Uri uri) {
        Log.m19d(TAG, "getRecordingMode appLinkData " + uri.toString());
        try {
            String[] split = uri.toString().split("=");
            String str = split[split.length - 1];
            Log.m19d(TAG, "getRecordingMode recordingMode " + str);
            return str;
        } catch (Exception unused) {
            Log.m19d(TAG, "getRecordingMode occur exception");
            return "standard";
        }
    }

    private void startMainActivity(Context context) {
        Log.m26i(TAG, "startMainActivity");
        Intent intent = new Intent(context, VNMainActivity.class);
        intent.setAction("android.intent.action.MAIN");
        intent.addFlags(335544320);
        intent.putExtra(BixbyConstant.BixbyStartMode.BIXBY_START_DATA, this.mBixbyStartData);
        if (this.mBixbyStartData.equals(BixbyConstant.BixbyStartMode.BIXBY_START_PLAY)) {
            intent.putExtra(BixbyConstant.InputParameter.FILE_NAME_ID, this.mId);
        }
        context.startActivity(intent);
    }
}
