package com.sec.android.app.voicenote.p007ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.CategoryRepository;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/* renamed from: com.sec.android.app.voicenote.ui.adapter.CategoryListAdapter */
public class CategoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "CategoryListAdapter";
    private static SparseIntArray mLabelCountArray = new SparseIntArray();
    private Context mContext;
    private Cursor mCursor = null;
    /* access modifiers changed from: private */
    public OnTouchCategoryItemListener mListener = null;
    private boolean needShowFooterView = false;

    /* renamed from: com.sec.android.app.voicenote.ui.adapter.CategoryListAdapter$OnTouchCategoryItemListener */
    public interface OnTouchCategoryItemListener {
        void onItemClick(View view, int i);

        boolean onItemLongClick(View view, int i);
    }

    /* renamed from: com.sec.android.app.voicenote.ui.adapter.CategoryListAdapter$ViewType */
    public static class ViewType {
        public static final int FOOTER = 2;
        public static final int ITEM = 1;
    }

    public CategoryListAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
        mLabelCountArray = CursorProvider.getInstance().getFileCountGroupByLabel(context);
    }

    public void setNeedShowFooterView(boolean z) {
        this.needShowFooterView = z;
    }

    public boolean isNeedShowFooterView() {
        return this.needShowFooterView;
    }

    public void setOnTouchCategoryItemListener(OnTouchCategoryItemListener onTouchCategoryItemListener) {
        this.mListener = onTouchCategoryItemListener;
    }

    public void swapCursor(Cursor cursor) {
        Log.m26i(TAG, "swapCusor");
        if (cursor == null || cursor.isClosed()) {
            Log.m29v(TAG, "swapCusor - cursor is closed!!");
            return;
        }
        mLabelCountArray = CursorProvider.getInstance().getFileCountGroupByLabel(this.mContext);
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    public void changeCursor(Cursor cursor) {
        Log.m26i(TAG, "changeCursor");
        swapCursor(cursor);
    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == 1) {
            return new ViewHolder(LayoutInflater.from(this.mContext).inflate(C0690R.layout.adapter_category_list, viewGroup, false));
        }
        return new FooterViewHolder(LayoutInflater.from(this.mContext).inflate(C0690R.layout.category_chooser_addcategory, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Cursor cursor = this.mCursor;
        if (cursor == null || cursor.isClosed()) {
            Log.m22e(TAG, "bindView - cursor index is zero or closed");
        } else if (!this.needShowFooterView || this.mCursor.getCount() != i) {
            this.mCursor.moveToPosition(i);
            Cursor cursor2 = this.mCursor;
            int i2 = cursor2.getInt(cursor2.getColumnIndex(CategoryRepository.LabelColumn.f102ID));
            ViewHolder viewHolder2 = (ViewHolder) viewHolder;
            int i3 = mLabelCountArray.get(i2);
            if (i2 == 0) {
                i3 = CursorProvider.getInstance().getRecordingModeFilesCount(1);
            } else if (i2 == 1) {
                i3 = CursorProvider.getInstance().getRecordingModeFilesCount(2);
            } else if (i2 == 2) {
                i3 = CursorProvider.getInstance().getRecordingModeFilesCount(4);
            } else if (i2 == 3) {
                i3 = CursorProvider.getInstance().getCallHistoryCount();
            }
            String categoryName = getCategoryName(this.mCursor, i2);
            StringBuilder sb = new StringBuilder(categoryName);
            setTitleViewColor(viewHolder2, categoryName, "(" + String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(i3)}) + ")", this.mContext);
            View view = viewHolder2.itemView;
            sb.append(this.mContext.getResources().getQuantityString(C0690R.plurals.item, i3, new Object[]{Integer.valueOf(i3)}));
            view.setContentDescription(sb);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r1.mCursor;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getItemViewType(int r2) {
        /*
            r1 = this;
            boolean r0 = r1.needShowFooterView
            if (r0 == 0) goto L_0x0010
            android.database.Cursor r0 = r1.mCursor
            if (r0 == 0) goto L_0x0010
            int r0 = r0.getCount()
            if (r0 != r2) goto L_0x0010
            r2 = 2
            return r2
        L_0x0010:
            r2 = 1
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.adapter.CategoryListAdapter.getItemViewType(int):int");
    }

    public long getItemId(int i) {
        Cursor cursor = this.mCursor;
        if (cursor == null || cursor.isClosed() || this.mCursor.getCount() <= i) {
            Log.m22e(TAG, "getItemId cursor is invalid or abnormal position. Return 0");
            return 0;
        }
        try {
            this.mCursor.moveToPosition(i);
            return (long) this.mCursor.getInt(this.mCursor.getColumnIndex(CategoryRepository.LabelColumn.f102ID));
        } catch (IndexOutOfBoundsException unused) {
            String str = TAG;
            Log.m22e(str, "getItemId index error count : " + this.mCursor.getCount() + " position : " + i);
            return 0;
        }
    }

    public int getItemCount() {
        Cursor cursor = this.mCursor;
        if (cursor == null || cursor.isClosed()) {
            return 0;
        }
        if (this.needShowFooterView) {
            return this.mCursor.getCount() + 1;
        }
        return this.mCursor.getCount();
    }

    public int getCount() {
        Cursor cursor = this.mCursor;
        if (cursor == null || cursor.isClosed()) {
            return 0;
        }
        return this.mCursor.getCount();
    }

    public Cursor getCursor() {
        return this.mCursor;
    }

    public int getPosition(String str) {
        Cursor cursor = getCursor();
        if (cursor == null || cursor.isClosed() || str == null || str.isEmpty()) {
            Log.m22e(TAG, "getItemId cursor is invalid or abnormal position. Return 0");
            return -1;
        }
        cursor.moveToFirst();
        while (str.compareToIgnoreCase(getCategoryName(cursor, cursor.getInt(cursor.getColumnIndex(CategoryRepository.LabelColumn.f102ID)))) != 0) {
            cursor.moveToNext();
            if (cursor.isAfterLast()) {
                return -1;
            }
        }
        return cursor.getPosition();
    }

    public long getID(int i) {
        Cursor cursor = getCursor();
        if (cursor == null || cursor.isClosed() || i < 0 || i >= cursor.getCount()) {
            Log.m22e(TAG, "getPath cursor is invalid or abnormal position. Return 0");
            return -1;
        }
        cursor.moveToPosition(i);
        return cursor.getLong(cursor.getColumnIndex(CategoryRepository.LabelColumn.f102ID));
    }

    public static SparseIntArray getLabelCountArray() {
        return mLabelCountArray;
    }

    private void setTitleViewColor(ViewHolder viewHolder, String str, String str2, Context context) {
        String trim = CursorProvider.getInstance().getCategorySearchTag().trim();
        viewHolder.titleView.setText(str);
        viewHolder.countView.setText(str2);
        if (!trim.isEmpty()) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
            String[] split = trim.split("\\s+");
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(context.getResources().getColor(C0690R.C0691color.listview_search_highlight, (Resources.Theme) null));
            ArrayList arrayList = new ArrayList();
            for (String str3 : split) {
                int length = str3.length();
//                char[] semGetPrefixCharForSpan = TextUtils.semGetPrefixCharForSpan(viewHolder.titleView.getPaint(), str, str3.toCharArray());
                arrayList.clear();
//                if (semGetPrefixCharForSpan != null) {
//                    String lowerCase = new String(semGetPrefixCharForSpan).toLowerCase();
//                    int indexOf = str.toLowerCase().indexOf(lowerCase);
//                    int length2 = lowerCase.length();
//                    while (indexOf >= 0 && indexOf <= str.length()) {
//                        arrayList.add(Integer.valueOf(indexOf));
//                        indexOf = str.toLowerCase().indexOf(lowerCase, indexOf + 1);
//                    }
//                    length = length2;
//                } else {
//                    int indexOf2 = str.toLowerCase().indexOf(str3.toLowerCase());
//                    while (indexOf2 >= 0 && indexOf2 <= str.length()) {
//                        arrayList.add(Integer.valueOf(indexOf2));
//                        indexOf2 = str.toLowerCase().indexOf(str3.toLowerCase(), indexOf2 + 1);
//                    }
//                }
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    Integer num = (Integer) it.next();
                    if (num.intValue() >= 0 && num.intValue() + length <= str.length()) {
                        spannableStringBuilder.setSpan(CharacterStyle.wrap(foregroundColorSpan), num.intValue(), num.intValue() + length, 33);
                    }
                }
            }
            viewHolder.titleView.setText(spannableStringBuilder);
        }
    }

    private String getCategoryName(Cursor cursor, int i) {
        Context context = this.mContext;
        if (context != null) {
            if (i == 0) {
                return context.getString(C0690R.string.uncategorized);
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
        }
        return cursor.getString(cursor.getColumnIndex(CategoryRepository.LabelColumn.TITLE));
    }

    public void notifyDataSetInvalidated() {
        mLabelCountArray = CursorProvider.getInstance().getFileCountGroupByLabel(this.mContext);
    }

    /* renamed from: com.sec.android.app.voicenote.ui.adapter.CategoryListAdapter$ViewHolder */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView countView;
        TextView titleView;

        ViewHolder(View view) {
            super(view);
            this.titleView = (TextView) view.findViewById(C0690R.C0693id.category_text_item);
            this.countView = (TextView) view.findViewById(C0690R.C0693id.category_count_item);
            view.setHapticFeedbackEnabled(true);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            if (CategoryListAdapter.this.mListener != null) {
                CategoryListAdapter.this.mListener.onItemClick(view, adapterPosition);
            }
        }

        public boolean onLongClick(View view) {
            if (CategoryListAdapter.this.mListener != null) {
                return CategoryListAdapter.this.mListener.onItemLongClick(view, getAdapterPosition());
            }
            return false;
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.adapter.CategoryListAdapter$FooterViewHolder */
    public class FooterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public FooterViewHolder(View view) {
            super(view);
            TextView textView = (TextView) view.findViewById(C0690R.C0693id.category_chooser_add_category_name);
            Context context = view.getContext();
            if (context != null) {
                textView.setContentDescription(context.getString(C0690R.string.addnewlabel) + ", " + context.getString(C0690R.string.button_tts));
            }
            view.setOnClickListener(this);
        }

        public void onClick(View view) {
            if (CategoryListAdapter.this.mListener != null) {
                CategoryListAdapter.this.mListener.onItemClick(view, getAdapterPosition());
            }
        }
    }
}
