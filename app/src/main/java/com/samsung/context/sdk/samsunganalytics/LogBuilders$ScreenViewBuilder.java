package com.samsung.context.sdk.samsunganalytics;

import android.text.TextUtils;
import com.samsung.context.sdk.samsunganalytics.internal.util.Utils;
import java.util.Map;

public class LogBuilders$ScreenViewBuilder extends LogBuilders$LogBuilder<LogBuilders$ScreenViewBuilder> {
    /* access modifiers changed from: protected */
    public LogBuilders$ScreenViewBuilder getThis() {
        return this;
    }

    public /* bridge */ /* synthetic */ long getTimeStamp() {
        return super.getTimeStamp();
    }

    public Map<String, String> build() {
        if (TextUtils.isEmpty(this.logs.get("pn"))) {
            Utils.throwException("Failure to build Log : Screen name cannot be null");
        } else {
            set("t", "pv");
        }
        return super.build();
    }
}
