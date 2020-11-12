package com.samsung.android.scloud.oem.lib.common;

import android.content.Context;
import android.os.Bundle;

public interface IServiceHandler {
    Bundle handleServiceAction(Context context, Object obj, String str, Bundle bundle);
}
