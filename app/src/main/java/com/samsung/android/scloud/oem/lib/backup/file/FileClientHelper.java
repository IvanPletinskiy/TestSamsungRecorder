package com.samsung.android.scloud.oem.lib.backup.file;

import android.content.Context;
import android.text.TextUtils;
import android.util.JsonWriter;
import com.samsung.android.scloud.oem.lib.LOG;
import com.samsung.android.scloud.oem.lib.utils.HashUtil;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.json.JSONObject;

public class FileClientHelper {
    private static String TAG = "FileClientHelper";
    private Context context = null;
    private JsonWriter jsonWriter;
    private String sourceKey = null;

    public FileClientHelper(Context context2, String str, JsonWriter jsonWriter2) {
        this.jsonWriter = jsonWriter2;
        this.sourceKey = str;
        this.context = context2;
    }

    /* access modifiers changed from: protected */
    public void open() {
        String str = TAG;
        LOG.m12d(str, "[" + this.sourceKey + "] open");
        JsonWriter jsonWriter2 = this.jsonWriter;
        if (jsonWriter2 != null) {
            try {
                jsonWriter2.beginArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void release() {
        String str = TAG;
        LOG.m12d(str, "[" + this.sourceKey + "] release");
        try {
            if (this.jsonWriter != null) {
                this.jsonWriter.endArray();
                this.jsonWriter.flush();
                this.jsonWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addFileMetaAndRecord(String str, String str2, JSONObject jSONObject, List<File> list) {
        int i;
        String jSONObject2 = jSONObject == null ? "" : jSONObject.toString();
        if (list == null) {
            i = 0;
        } else {
            i = list.size();
        }
        LOG.m15i(TAG, "[" + this.sourceKey + "] id: " + str + ", timeStamp: " + str2 + ", fileSize: " + i + ", record: " + jSONObject2);
        try {
            this.jsonWriter.beginObject();
            File file = list.get(0);
            this.jsonWriter.name(DialogFactory.BUNDLE_ID).value(!TextUtils.isEmpty(str) ? str : Long.toString((long) file.getAbsolutePath().hashCode()));
            JsonWriter name = this.jsonWriter.name("timestamp");
            if (TextUtils.isEmpty(str)) {
                str2 = file.lastModified() + "";
            }
            name.value(str2);
            if (!TextUtils.isEmpty(jSONObject2)) {
                this.jsonWriter.name("record").value(jSONObject2);
            }
            this.jsonWriter.name("files");
            this.jsonWriter.beginArray();
            for (File next : list) {
                this.jsonWriter.beginObject();
                this.jsonWriter.name(DialogFactory.BUNDLE_PATH).value(next.getAbsolutePath());
                this.jsonWriter.name("size").value(next.length());
                this.jsonWriter.name("hash").value(HashUtil.getFileSHA256(next));
                this.jsonWriter.endObject();
            }
            this.jsonWriter.endArray();
            this.jsonWriter.endObject();
            this.jsonWriter.flush();
        } catch (Exception e) {
            LOG.m14e(TAG, "[" + this.sourceKey + "] Exception", e);
        }
    }
}
