package com.samsung.context.sdk.samsunganalytics;

import com.samsung.context.sdk.samsunganalytics.internal.util.Debug;
import com.samsung.context.sdk.samsunganalytics.internal.util.Utils;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public class LogBuilders$SettingBuilder {
    private Map<String, String> map = new HashMap();

    public final LogBuilders$SettingBuilder set(String str, int i) {
        set(str, Integer.toString(i));
        return this;
    }

    public final LogBuilders$SettingBuilder set(String str, String str2) {
        if (str == null) {
            Utils.throwException("Failure to build logs [setting] : Key cannot be null.");
        } else if (str.equalsIgnoreCase("t")) {
            Utils.throwException("Failure to build logs [setting] : 't' is reserved word, choose another word.");
        } else {
            this.map.put(str, str2);
        }
        return this;
    }

    public Map<String, String> build() {
        Debug.LogENG("SettingBuilder API is deprecated. Please use SettingPrefBuilder API.");
        return null;
    }
}
