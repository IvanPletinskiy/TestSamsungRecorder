package com.samsung.context.sdk.samsunganalytics;

import android.text.TextUtils;
import com.samsung.context.sdk.samsunganalytics.internal.util.Debug;
import com.samsung.context.sdk.samsunganalytics.internal.util.Utils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LogBuilders$SettingPrefBuilder {
    private Map<String, Set<String>> map = new HashMap();

    private LogBuilders$SettingPrefBuilder addAppPref(String str) {
        if (!this.map.containsKey(str) && !TextUtils.isEmpty(str)) {
            this.map.put(str, new HashSet());
        } else if (TextUtils.isEmpty(str)) {
            Utils.throwException("Failure to build logs [setting preference] : Preference name cannot be null.");
        }
        return this;
    }

    public LogBuilders$SettingPrefBuilder addKey(String str, String str2) {
        if (TextUtils.isEmpty(str2)) {
            Utils.throwException("Failure to build logs [setting preference] : Setting key cannot be null.");
        }
        addAppPref(str);
        this.map.get(str).add(str2);
        return this;
    }

    public Map<String, Set<String>> build() {
        Debug.LogENG(this.map.toString());
        return this.map;
    }
}
