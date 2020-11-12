package com.sec.android.app.voicenote.bixby.action;

import android.content.Context;
import android.os.AsyncTask;
import com.samsung.android.sdk.bixby2.action.ResponseCallback;
import com.sec.android.app.voicenote.common.util.CategoryRepository;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import org.json.JSONObject;

public class GetRecordedFileCount extends AbstractAction {
    private static final String KEY_ACTION_DESCRIPTION = "actionDescription";
    private static final String KEY_ACTION_RESPOND = "actionResponse";
    private static final String KEY_ACTION_RESULT = "actionResult";
    private static final String KEY_DATE = "date";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_ID = "id";
    private static final String KEY_RECORDING_OBJECT = "RecordingObject";
    private static final String KEY_TITLE = "fileName";
    private static final String TAG = "BixbyGetRecordedFileCount";
    private GetFileAsync mGetFileAsync = null;

    public void executeAction(Context context, String str, ResponseCallback responseCallback) {
        Log.m26i(TAG, "executeAction - cb: " + responseCallback);
        this.mGetFileAsync = new GetFileAsync(context);
        this.mGetFileAsync.execute(new CallbackObject[]{new CallbackObject(responseCallback, str)});
    }

    /* access modifiers changed from: protected */
    public void sendResponse(boolean z, String str) {
        Log.m26i(TAG, "sendResponse - result/cause: " + z + " - " + str);
    }

    public void update(Observable observable, Object obj) {
        Log.m26i(TAG, "update");
    }

    private static class CallbackObject {
        private ResponseCallback callback;
        private String inputParameter;

        private CallbackObject(ResponseCallback responseCallback, String str) {
            this.callback = responseCallback;
            this.inputParameter = str;
        }

        public ResponseCallback getCallback() {
            return this.callback;
        }

        public void setCallback(ResponseCallback responseCallback) {
            this.callback = responseCallback;
        }

        public String getInputParameter() {
            return this.inputParameter;
        }
    }

    private class GetFileAsync extends AsyncTask<CallbackObject, Void, JSONObject> {
        private String[] PROJECTIONS;
        private String SELECTIONS;
        private Context context;
        private String fileName;

        private GetFileAsync(Context context2) {
            this.PROJECTIONS = null;
            this.SELECTIONS = null;
            this.fileName = null;
            this.context = context2;
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            Log.m26i(GetRecordedFileCount.TAG, "onPreExecute");
            this.PROJECTIONS = new String[]{"title", GetRecordedFileCount.KEY_DURATION, "datetaken", CategoryRepository.LabelColumn.f102ID};
            this.SELECTIONS = CursorProvider.getInstance().getBaseQuery().toString();
            super.onPreExecute();
        }

        /* access modifiers changed from: protected */
        public JSONObject doInBackground(CallbackObject... callbackObjectArr) {
            Log.m26i(GetRecordedFileCount.TAG, "doInBackground - fileName: " + callbackObjectArr[0].getInputParameter());
            this.fileName = callbackObjectArr[0].getInputParameter();
            GetRecordedFileCount.this.mResponseCallback = callbackObjectArr[0].getCallback();
            return getFilesDB(this.context, this.PROJECTIONS, this.SELECTIONS);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(JSONObject jSONObject) {
            Log.m26i(GetRecordedFileCount.TAG, "onPostExecute - result: " + jSONObject);
            if (GetRecordedFileCount.this.mResponseCallback != null) {
                Log.m19d(GetRecordedFileCount.TAG, "onPostExecute: " + GetRecordedFileCount.this.mResponseCallback);
                GetRecordedFileCount.this.mResponseCallback.onComplete(jSONObject.toString());
            }
            super.onPostExecute(jSONObject);
        }

        /* JADX WARNING: Removed duplicated region for block: B:50:0x013d A[DONT_GENERATE] */
        /* JADX WARNING: Removed duplicated region for block: B:58:0x0154  */
        /* JADX WARNING: Removed duplicated region for block: B:67:? A[RETURN, SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:70:? A[RETURN, SYNTHETIC] */
        /* JADX WARNING: Unknown top exception splitter block from list: {B:53:0x0145=Splitter:B:53:0x0145, B:45:0x012e=Splitter:B:45:0x012e} */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private org.json.JSONObject getFilesDB(android.content.Context r20, java.lang.String[] r21, java.lang.String r22) {
            /*
                r19 = this;
                r1 = r19
                java.lang.String r0 = "duration"
                org.json.JSONObject r2 = new org.json.JSONObject
                r2.<init>()
                java.lang.String r3 = r1.fileName
                java.lang.String r4 = "null"
                boolean r3 = r3.equals(r4)
                android.content.ContentResolver r4 = r20.getContentResolver()
                android.net.Uri r5 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                r8 = 0
                java.lang.String r9 = "_id DESC"
                r6 = r21
                r7 = r22
                android.database.Cursor r4 = r4.query(r5, r6, r7, r8, r9)
                org.json.JSONArray r5 = new org.json.JSONArray
                r5.<init>()
                java.lang.String r6 = "actionResponse"
                java.lang.String r7 = "No recordings found"
                r8 = 0
                java.lang.String r10 = "actionDescription"
                java.lang.String r11 = "actionResult"
                java.lang.String r12 = "BixbyGetRecordedFileCount"
                if (r4 == 0) goto L_0x00d1
                boolean r13 = r4.moveToFirst()     // Catch:{ JSONException -> 0x00cd, Exception -> 0x00ca }
                if (r13 != 0) goto L_0x005d
                org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x00cd, Exception -> 0x00ca }
                r0.<init>()     // Catch:{ JSONException -> 0x00cd, Exception -> 0x00ca }
                java.lang.String r3 = java.lang.String.valueOf(r8)     // Catch:{ JSONException -> 0x00cd, Exception -> 0x00ca }
                r0.put(r11, r3)     // Catch:{ JSONException -> 0x00cd, Exception -> 0x00ca }
                r0.put(r10, r7)     // Catch:{ JSONException -> 0x00cd, Exception -> 0x00ca }
                r2.put(r6, r0)     // Catch:{ JSONException -> 0x00cd, Exception -> 0x00ca }
                java.lang.String r0 = " getFilesDB - there is no recorded file||"
                com.sec.android.app.voicenote.provider.Log.m32w((java.lang.String) r12, (java.lang.String) r0)     // Catch:{ JSONException -> 0x00cd, Exception -> 0x00ca }
                if (r4 == 0) goto L_0x005c
                boolean r0 = r4.isClosed()
                if (r0 != 0) goto L_0x005c
                r4.close()
            L_0x005c:
                return r2
            L_0x005d:
                java.lang.String r13 = "title"
                int r13 = r4.getColumnIndex(r13)     // Catch:{ JSONException -> 0x00cd, Exception -> 0x00ca }
                java.lang.String r13 = r4.getString(r13)     // Catch:{ JSONException -> 0x00cd, Exception -> 0x00ca }
                int r14 = r4.getColumnIndex(r0)     // Catch:{ JSONException -> 0x00cd, Exception -> 0x00ca }
                long r14 = r4.getLong(r14)     // Catch:{ JSONException -> 0x00cd, Exception -> 0x00ca }
                java.lang.String r9 = "datetaken"
                int r9 = r4.getColumnIndex(r9)     // Catch:{ JSONException -> 0x00cd, Exception -> 0x00ca }
                long r8 = r4.getLong(r9)     // Catch:{ JSONException -> 0x00cd, Exception -> 0x00ca }
                r22 = r12
                java.lang.String r12 = "_id"
                int r12 = r4.getColumnIndex(r12)     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                long r16 = r4.getLong(r12)     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                org.json.JSONObject r12 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                r12.<init>()     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                r18 = r6
                java.lang.String r6 = "fileName"
                r12.put(r6, r13)     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                java.lang.String r6 = r1.stringForTime(r14)     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                r12.put(r0, r6)     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                java.lang.String r6 = "date"
                java.lang.String r8 = r1.getDateFormat(r8)     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                java.lang.String r8 = java.lang.String.valueOf(r8)     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                r12.put(r6, r8)     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                java.lang.String r6 = "id"
                java.lang.String r8 = java.lang.String.valueOf(r16)     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                r12.put(r6, r8)     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                if (r3 == 0) goto L_0x00b4
                r5.put(r12)     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                goto L_0x00bd
            L_0x00b4:
                boolean r6 = r1.isMatching(r13)     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                if (r6 == 0) goto L_0x00bd
                r5.put(r12)     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
            L_0x00bd:
                boolean r6 = r4.moveToNext()     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                if (r6 != 0) goto L_0x00c4
                goto L_0x00d5
            L_0x00c4:
                r12 = r22
                r6 = r18
                r8 = 0
                goto L_0x005d
            L_0x00ca:
                r0 = move-exception
                r3 = r12
                goto L_0x012e
            L_0x00cd:
                r0 = move-exception
                r3 = r12
                goto L_0x0145
            L_0x00d1:
                r18 = r6
                r22 = r12
            L_0x00d5:
                org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                r0.<init>()     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                int r3 = r5.length()     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                if (r3 <= 0) goto L_0x00ee
                r3 = 1
                java.lang.String r3 = java.lang.String.valueOf(r3)     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                r0.put(r11, r3)     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                java.lang.String r3 = "Success"
                r0.put(r10, r3)     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                goto L_0x00f9
            L_0x00ee:
                r3 = 0
                java.lang.String r3 = java.lang.String.valueOf(r3)     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                r0.put(r11, r3)     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                r0.put(r10, r7)     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
            L_0x00f9:
                java.lang.String r3 = "RecordingObject"
                r2.put(r3, r5)     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                r3 = r18
                r2.put(r3, r0)     // Catch:{ JSONException -> 0x0142, Exception -> 0x012b }
                if (r4 == 0) goto L_0x010e
                boolean r0 = r4.isClosed()
                if (r0 != 0) goto L_0x010e
                r4.close()
            L_0x010e:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r3 = "getFilesDB: "
                r0.append(r3)
                java.lang.String r3 = r2.toString()
                r0.append(r3)
                java.lang.String r0 = r0.toString()
                r3 = r22
                com.sec.android.app.voicenote.provider.Log.m19d(r3, r0)
                return r2
            L_0x0129:
                r0 = move-exception
                goto L_0x0159
            L_0x012b:
                r0 = move-exception
                r3 = r22
            L_0x012e:
                java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0129 }
                com.sec.android.app.voicenote.provider.Log.m22e(r3, r0)     // Catch:{ all -> 0x0129 }
                if (r4 == 0) goto L_0x0140
                boolean r0 = r4.isClosed()
                if (r0 != 0) goto L_0x0140
                r4.close()
            L_0x0140:
                r2 = 0
                return r2
            L_0x0142:
                r0 = move-exception
                r3 = r22
            L_0x0145:
                java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0129 }
                com.sec.android.app.voicenote.provider.Log.m22e(r3, r0)     // Catch:{ all -> 0x0129 }
                if (r4 == 0) goto L_0x0157
                boolean r0 = r4.isClosed()
                if (r0 != 0) goto L_0x0157
                r4.close()
            L_0x0157:
                r2 = 0
                return r2
            L_0x0159:
                if (r4 == 0) goto L_0x0164
                boolean r2 = r4.isClosed()
                if (r2 != 0) goto L_0x0164
                r4.close()
            L_0x0164:
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.bixby.action.GetRecordedFileCount.GetFileAsync.getFilesDB(android.content.Context, java.lang.String[], java.lang.String):org.json.JSONObject");
        }

        private boolean isMatching(String str) {
            String[] split = str.split(" ");
            String[] split2 = this.fileName.split(" ");
            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            for (String lowerCase : split) {
                sb.append(lowerCase.toLowerCase());
            }
            for (String lowerCase2 : split2) {
                sb2.append(lowerCase2.toLowerCase());
            }
            if (sb.toString().contains(sb2.toString())) {
                return true;
            }
            return false;
        }

        private String getDateFormat(long j) {
            StringBuilder sb = new StringBuilder(new SimpleDateFormat("dd/MM/yyyy").format(new Date(j)));
            sb.append("");
            Log.m19d(GetRecordedFileCount.TAG, "getDateFormat - " + sb.toString());
            return sb.toString();
        }

        private String stringForTime(long j) {
            long round = (long) (Math.round(((float) j) / 10.0f) / 100);
            return String.format(Locale.getDefault(), "%02d.%02d.%02d", new Object[]{Integer.valueOf((int) (round / 3600)), Integer.valueOf((int) ((round / 60) % 60)), Integer.valueOf((int) (round % 60))});
        }
    }
}
