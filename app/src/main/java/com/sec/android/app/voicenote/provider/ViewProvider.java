package com.sec.android.app.voicenote.provider;

import android.content.Context;
import android.util.TypedValue;
import android.widget.ListView;
import android.widget.TextView;

import com.sec.android.app.voicenote.C0690R;

public class ViewProvider {
    private static final String TAG = "ViewProvider";
    private static float mMaxFontSize = -1.0f;

    public static void setMaxFontSize(Context context, TextView textView) {
        Log.m26i(TAG, "setMaxFontSize");
        if (context == null) {
            Log.m22e(TAG, "setMaxFontSize - context is null");
        } else if (textView == null) {
            Log.m22e(TAG, "setMaxFontSize - textView is null");
        } else {
            if (mMaxFontSize < 0.0f) {
                TypedValue typedValue = new TypedValue();
                context.getResources().getValue(C0690R.dimen.font_scale_large, typedValue, true);
                mMaxFontSize = typedValue.getFloat();
            }
            float f = context.getResources().getConfiguration().fontScale;
            if (f > mMaxFontSize) {
                textView.setTextSize(0, (textView.getTextSize() / f) * mMaxFontSize);
            }
        }
    }

    public static void setBackgroundListView(Context context, ListView listView) {
        if (listView != null) {
//            listView.semSetBottomColor(ContextCompat.getColor(context, C0690R.C0691color.main_window_bg));
        }
    }
}
