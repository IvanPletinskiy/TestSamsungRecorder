package com.samsung.context.sdk.samsunganalytics.internal.setting;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import com.samsung.context.sdk.samsunganalytics.BuildConfig;
import com.samsung.context.sdk.samsunganalytics.Configuration;
import com.samsung.context.sdk.samsunganalytics.internal.executor.AsyncTaskClient;
import com.samsung.context.sdk.samsunganalytics.internal.sender.LogType;
import com.samsung.context.sdk.samsunganalytics.internal.util.Debug;
import com.samsung.context.sdk.samsunganalytics.internal.util.Delimiter;
import com.samsung.context.sdk.samsunganalytics.internal.util.Preferences;
import com.samsung.context.sdk.samsunganalytics.internal.util.Utils;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class BuildClient implements AsyncTaskClient {
    private Configuration config;
    private Context context;
    private Uri logUri = Uri.parse("content://com.sec.android.log.diagmonagent.sa/log");
    private List<String> settings;

    public BuildClient(Context context2, Configuration configuration) {
        this.context = context2;
        this.config = configuration;
    }

    public void run() {
        this.settings = new SettingReader(this.context).read();
    }

    public int onFinish() {
        Uri uri;
        if (!this.config.getUserAgreement().isAgreement()) {
            Debug.LogD("user do not agree setting");
            return 0;
        }
        List<String> list = this.settings;
        if (list == null || list.isEmpty()) {
            Debug.LogD("Setting Sender", "No status log");
            return 0;
        }
        if (this.config.isAlwaysRunningApp()) {
            Utils.registerReceiver(this.context, this.config);
        }
        if (!Utils.compareDays(7, Long.valueOf(Preferences.getPreferences(this.context).getLong("status_sent_date", 0)))) {
            Debug.LogD("do not send setting < 7days");
            return 0;
        }
        Debug.LogD("send setting");
        HashMap hashMap = new HashMap();
        String valueOf = String.valueOf(System.currentTimeMillis());
        hashMap.put("ts", valueOf);
        hashMap.put("t", "st");
        hashMap.put("v", BuildConfig.VERSION_NAME);
        hashMap.put("tz", String.valueOf(TimeUnit.MILLISECONDS.toMinutes((long) TimeZone.getDefault().getRawOffset())));
        if (this.config.isUseAnonymizeIp()) {
            hashMap.put("aip", "1");
            String overrideIp = this.config.getOverrideIp();
            if (overrideIp != null) {
                hashMap.put("oip", overrideIp);
            }
        }
        Delimiter delimiter = new Delimiter();
        boolean z = false;
        for (String put : this.settings) {
            hashMap.put("sti", put);
            ContentValues contentValues = new ContentValues();
            contentValues.put("tcType", Integer.valueOf(this.config.isEnableUseInAppLogging() ? 1 : 0));
            contentValues.put("tid", this.config.getTrackingId());
            contentValues.put("logType", LogType.UIX.getAbbrev());
            contentValues.put("timeStamp", valueOf);
            contentValues.put("body", delimiter.makeDelimiterString(hashMap, Delimiter.Depth.ONE_DEPTH));
            try {
                uri = this.context.getContentResolver().insert(this.logUri, contentValues);
            } catch (IllegalArgumentException unused) {
                uri = null;
            }
            if (uri != null) {
                int parseInt = Integer.parseInt(uri.getLastPathSegment());
                Debug.LogD("Send SettingLog Result = " + parseInt);
                if (parseInt == 0) {
                    z = true;
                }
            }
        }
        if (z) {
            Preferences.getPreferences(this.context).edit().putLong("status_sent_date", System.currentTimeMillis()).apply();
        } else {
            Preferences.getPreferences(this.context).edit().putLong("status_sent_date", 0).apply();
        }
        Debug.LogD("Save Setting Result = " + z);
        return 0;
    }
}
