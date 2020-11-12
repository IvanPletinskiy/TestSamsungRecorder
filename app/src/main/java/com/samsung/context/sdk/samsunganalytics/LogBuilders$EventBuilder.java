package com.samsung.context.sdk.samsunganalytics;

import android.text.TextUtils;
import com.samsung.context.sdk.samsunganalytics.internal.util.Utils;
import java.util.Map;

public class LogBuilders$EventBuilder extends LogBuilders$LogBuilder<LogBuilders$EventBuilder> {
    /* access modifiers changed from: protected */
    public LogBuilders$EventBuilder getThis() {
        return this;
    }

    public /* bridge */ /* synthetic */ long getTimeStamp() {
        return super.getTimeStamp();
    }

    public LogBuilders$EventBuilder setEventName(String str) {
        if (TextUtils.isEmpty(str)) {
            Utils.throwException("Failure to build Log : Event name cannot be null");
        }
        set("en", str);
        return this;
    }

    public LogBuilders$EventBuilder setEventValue(long j) {
        set("ev", String.valueOf(j));
        return this;
    }

    public Map<String, String> build() {
        if (!this.logs.containsKey("en")) {
            Utils.throwException("Failure to build Log : Event name cannot be null");
        }
        set("t", "ev");
        return super.build();
    }
}
