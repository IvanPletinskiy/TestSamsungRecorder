package com.sec.android.app.voicenote.p007ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.StaleDataException;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import androidx.core.view.GravityCompat;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.CategoryRepository;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.Log;
import java.util.Locale;

/* renamed from: com.sec.android.app.voicenote.ui.adapter.SpinnerSimpleCursorAdapter */
public class SpinnerSimpleCursorAdapter extends SimpleCursorAdapter {
    private static final String TAG = "SpinnerSimpleCursorAdapter";
    private Context mContext;
    private String[] mFrom;
    private boolean mIsShowAddCategory = true;
    private SparseIntArray mLabelCountArray;
    private int[] mTo;

    public SpinnerSimpleCursorAdapter(Context context, int i, Cursor cursor, String[] strArr, int[] iArr) {
        super(context, i, cursor, strArr, iArr, 0);
        this.mContext = context;
        this.mFrom = strArr;
        this.mTo = iArr;
        if (CategoryListAdapter.getLabelCountArray().size() != 0) {
            this.mLabelCountArray = CategoryListAdapter.getLabelCountArray();
        } else {
            this.mLabelCountArray = CursorProvider.getInstance().getFileCountGroupByLabel(context);
        }
    }

    public boolean isShowAddCategory() {
        return this.mIsShowAddCategory;
    }

    public int getCount() {
        int count = super.getCount();
        this.mIsShowAddCategory = true;
        return count + 1;
    }

    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater from = LayoutInflater.from(this.mContext);
        if (getCursor() == null || getCursor().isClosed()) {
            return from.inflate(C0690R.layout.listrow_spinner_add_category_item, viewGroup, false);
        }
        if (i == getCount() - 1 && this.mIsShowAddCategory) {
            return from.inflate(C0690R.layout.listrow_spinner_add_category_item, viewGroup, false);
        }
        try {
            return super.getDropDownView(i, from.inflate(C0690R.layout.listrow_spinner_list_category_item, viewGroup, false), viewGroup);
        } catch (IllegalStateException e) {
            Log.m31v(TAG, "IllegalStateException occured : attempt to re-open an already-closed object", (Throwable) e);
            return from.inflate(C0690R.layout.listrow_spinner_add_category_item, viewGroup, false);
        }
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater from = LayoutInflater.from(this.mContext);
        if (getCursor() != null && getCursor().isClosed()) {
            Log.m22e(TAG, "getView : cursor is closed! It shouldn't happen here");
        }
        if (getCursor() == null) {
            return newView(this.mContext, getCursor(), viewGroup);
        }
        if (i == getCount() - 1 && this.mIsShowAddCategory) {
            return from.inflate(C0690R.layout.listrow_spinner_list_category_item, viewGroup, false);
        }
        try {
            View view2 = super.getView(i, view, viewGroup);
            TextView textView = (TextView) view2.findViewById(C0690R.C0693id.label_title);
            TextView textView2 = (TextView) view2.findViewById(C0690R.C0693id.number_category);
            textView.setTextColor(this.mContext.getResources().getColor(C0690R.C0691color.spinner_text_color, (Resources.Theme) null));
            textView2.setVisibility(8);
            textView.setGravity(GravityCompat.END);
            setMaxFontScale(this.mContext, textView);
            setMaxFontScale(this.mContext, textView2);
            return view2;
        } catch (IllegalStateException e) {
            Log.m24e(TAG, "IllegalStateException occured: ", (Throwable) e);
            return from.inflate(C0690R.layout.listrow_spinner_list_category_item, viewGroup, false);
        }
    }

    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor == null || cursor.isClosed()) {
            Log.m22e(TAG, "bindView : cursor is null or isClosed ");
        } else if (getCursor().getPosition() != getCount() - 1 || !this.mIsShowAddCategory) {
            int i = cursor.getInt(cursor.getColumnIndex(CategoryRepository.LabelColumn.f102ID));
            TextView textView = (TextView) view.findViewById(this.mTo[0]);
            TextView textView2 = (TextView) view.findViewById(this.mTo[1]);
            String title = getTitle(context, cursor, i);
            textView.setText(title);
            textView.setContentDescription(title);
            int i2 = this.mLabelCountArray.get(i);
            if (i == 0) {
                i2 = CursorProvider.getInstance().getRecordingModeFilesCount(1);
            } else if (i == 1) {
                i2 = CursorProvider.getInstance().getRecordingModeFilesCount(2);
            } else if (i == 2) {
                i2 = CursorProvider.getInstance().getRecordingModeFilesCount(4);
            } else if (i == 3) {
                i2 = CursorProvider.getInstance().getCallHistoryCount();
            }
            textView2.setText(String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(i2)}));
        }
    }

    private String getTitle(Context context, Cursor cursor, int i) {
        if (i == 0) {
            return context.getString(C0690R.string.category_none);
        }
        if (i == 1) {
            return context.getString(C0690R.string.category_interview);
        }
        if (i == 2) {
            return context.getString(C0690R.string.category_speech_to_text);
        }
        if (i == 3) {
            return context.getString(C0690R.string.category_call_history);
        }
        try {
            return cursor.getString(cursor.getColumnIndex(this.mFrom[0]));
        } catch (CursorIndexOutOfBoundsException e) {
            e = e;
            Log.m22e(TAG, e.toString());
            return null;
        } catch (StaleDataException e2) {
//            e = e2;
//            Log.m22e(TAG, e.toString());
            return null;
        }
    }

    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return super.newView(context, cursor, viewGroup);
    }

    private void setMaxFontScale(Context context, TextView textView) {
        if (context != null && textView != null) {
            float f = 1.5f;
            float f2 = context.getResources().getConfiguration().fontScale;
            float textSize = textView.getTextSize() / context.getResources().getDisplayMetrics().scaledDensity;
            if (f2 <= 1.5f) {
                f = f2;
            }
            textView.setTextSize(1, textSize * f);
        }
    }
}
