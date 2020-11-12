package com.samsung.context.sdk.samsunganalytics;

import android.text.TextUtils;
import com.samsung.context.sdk.samsunganalytics.LogBuilders$LogBuilder;
import com.samsung.context.sdk.samsunganalytics.internal.policy.Validation;
import com.samsung.context.sdk.samsunganalytics.internal.util.Delimiter;
import com.samsung.context.sdk.samsunganalytics.internal.util.Utils;
import java.util.HashMap;
import java.util.Map;

public abstract class LogBuilders$LogBuilder<T extends LogBuilders$LogBuilder> {
    protected Map<String, String> logs = new HashMap();

    /* access modifiers changed from: protected */
    public abstract T getThis();

    protected LogBuilders$LogBuilder() {
    }

    public final T set(String str, String str2) {
        if (str != null) {
            this.logs.put(str, str2);
        }
        return getThis();
    }

    public long getTimeStamp() {
        return System.currentTimeMillis();
    }

    public Map<String, String> build() {
        set("ts", String.valueOf(getTimeStamp()));
        return this.logs;
    }

    public T setScreenView(String str) {
        if (TextUtils.isEmpty(str)) {
            Utils.throwException("Failure to build logs [PropertyBuilder] : Key cannot be null.");
        } else {
            set("pn", str);
        }
        return getThis();
    }

    public T setDimension(Map<String, String> map) {
        set("cd", new Delimiter().makeDelimiterString(Validation.checkSizeLimit(map), Delimiter.Depth.TWO_DEPTH));
        return getThis();
    }
}
