package com.sec.android.app.voicenote.provider;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import com.sec.android.app.voicenote.C0690R;
import java.util.UnknownFormatConversionException;

public class AssistantProvider {
    private static final int PLURAL_QUANTITY_OTHER = 9999;
    private static final String TAG = "AssistantProvider";
    private static volatile AssistantProvider mInstance;
    private Context mAppContext;

    private AssistantProvider() {
        Log.m19d(TAG, "AssistantProvider creator !!");
    }

    public static AssistantProvider getInstance() {
        if (mInstance == null) {
            synchronized (AssistantProvider.class) {
                if (mInstance == null) {
                    mInstance = new AssistantProvider();
                }
            }
        }
        return mInstance;
    }

    public void setApplicationContext(Context context) {
        this.mAppContext = context;
    }

    public String stringForReadTime(String str) {
        int i;
        if (this.mAppContext == null) {
            return str;
        }
        String[] split = str.split(":");
        StringBuilder sb = new StringBuilder();
        Resources resources = this.mAppContext.getResources();
        try {
            if (split.length > 2) {
                int intValue = Integer.valueOf(split[0]).intValue();
                int intValue2 = Integer.valueOf(split[1]).intValue();
                if (split[2].length() > 2) {
                    i = Integer.valueOf(split[2].split("\\.")[0]).intValue();
                } else {
                    i = Integer.valueOf(split[2]).intValue();
                }
                if (intValue > 0) {
                    sb.append(getQuantityString(resources, C0690R.plurals.timer_hr, intValue, Integer.valueOf(intValue)));
                    sb.append(", ");
                }
                if (intValue2 > 0) {
                    sb.append(getQuantityString(resources, C0690R.plurals.timer_min, intValue2, Integer.valueOf(intValue2)));
                    sb.append(", ");
                }
                if (i > 0 || (i == 0 && sb.length() == 0)) {
                    sb.append(getQuantityString(resources, C0690R.plurals.timer_sec, i, Integer.valueOf(i)));
                }
            } else if (str.length() > 5) {
                int intValue3 = Integer.valueOf(split[0]).intValue();
                String[] split2 = split[1].split("\\.");
                int intValue4 = Integer.valueOf(split2[0]).intValue();
                int intValue5 = Integer.valueOf(split2[1]).intValue();
                if (intValue3 > 0) {
                    sb.append(getQuantityString(resources, C0690R.plurals.timer_min, intValue3, Integer.valueOf(intValue3)));
                    sb.append(", ");
                }
                if (intValue5 > 0) {
                    sb.append(String.valueOf(intValue4));
                    sb.append('.');
                    sb.append(getQuantityString(resources, C0690R.plurals.timer_sec, intValue5, Integer.valueOf(intValue5)));
                } else {
                    sb.append(getQuantityString(resources, C0690R.plurals.timer_sec, intValue4, Integer.valueOf(intValue4)));
                }
            } else {
                int intValue6 = Integer.valueOf(split[0]).intValue();
                int intValue7 = Integer.valueOf(split[1]).intValue();
                if (intValue6 > 0) {
                    sb.append(getQuantityString(resources, C0690R.plurals.timer_min, intValue6, Integer.valueOf(intValue6)));
                    sb.append(", ");
                }
                if (intValue7 > 0 || (intValue7 == 0 && sb.length() == 0)) {
                    sb.append(getQuantityString(resources, C0690R.plurals.timer_sec, intValue7, Integer.valueOf(intValue7)));
                }
            }
        } catch (ArrayIndexOutOfBoundsException | UnknownFormatConversionException e) {
            Log.m22e(TAG, "stringForReadTime " + e);
        }
        return sb.toString();
    }

    public String getButtonDescriptionForTalkback(int i) {
        Context context = this.mAppContext;
        if (context != null) {
            return context.getResources().getString(i);
        }
        return null;
    }

    private String getQuantityString(Resources resources, int i, int i2, Object... objArr) {
        if (i2 != 1) {
            return resources.getQuantityString(i, PLURAL_QUANTITY_OTHER, objArr);
        }
        return resources.getQuantityString(i, i2, objArr);
    }

    public String getContentDesToButton(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        if (this.mAppContext != null) {
            stringBuffer.append(str);
            stringBuffer.append(", ");
            stringBuffer.append(this.mAppContext.getResources().getString(C0690R.string.button));
        }
        return !stringBuffer.toString().isEmpty() ? stringBuffer.toString() : str;
    }

    public void setBlockDescendants(View view, boolean z) {
        if (view == null) {
            return;
        }
        if (z) {
            view.setImportantForAccessibility(4);
            ((ViewGroup) view).setDescendantFocusability(393216);
            return;
        }
        view.setImportantForAccessibility(1);
        ((ViewGroup) view).setDescendantFocusability(262144);
    }
}
