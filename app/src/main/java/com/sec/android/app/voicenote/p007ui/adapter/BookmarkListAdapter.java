package com.sec.android.app.voicenote.p007ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.provider.AssistantProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.ViewProvider;
import com.sec.android.app.voicenote.service.MetadataRepository;

import java.util.List;
import java.util.Locale;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

/* renamed from: com.sec.android.app.voicenote.ui.adapter.BookmarkListAdapter */
public class BookmarkListAdapter extends ArrayAdapter<BookmarkListAdapter.BookmarkItem> implements View.OnClickListener {
    private static final String TAG = "BookmarkListAdapter";
    private LayoutInflater mInflater;
    private int mPlayingPosition = -1;
    private OnItemDetailClickListener onItemDetailClickListener = null;

    /* renamed from: com.sec.android.app.voicenote.ui.adapter.BookmarkListAdapter$DetailType */
    public static class DetailType {
        public static int DELETE = 2;
        public static int TIME = 0;
        public static int TITLE = 1;
    }

    /* renamed from: com.sec.android.app.voicenote.ui.adapter.BookmarkListAdapter$OnItemDetailClickListener */
    public interface OnItemDetailClickListener {
        void onDeleteClick(View view, int i, long j, int i2);

        void onTimeClick(View view, int i, long j, int i2);

        void onTitleClick(View view, int i, long j, int i2);
    }

    public boolean hasStableIds() {
        return true;
    }

    public BookmarkListAdapter(@NonNull Context context, @LayoutRes int i, @NonNull List<BookmarkItem> list, LayoutInflater layoutInflater) {
        super(context, i, list);
        this.mInflater = layoutInflater;
    }

    public long getItemId(int i) {
        return (long) getTime(i);
    }

    @NonNull
    public View getView(int i, View view, @NonNull ViewGroup viewGroup) {
        Log.m19d(TAG, "getView  position : " + i);
        if (view == null) {
            view = getCustomView(viewGroup);
        }
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.position = i;
        BookmarkItem bookmarkItem = (BookmarkItem) getItem(i);
        if (bookmarkItem != null) {
            String stringByDuration = getStringByDuration(bookmarkItem.mTime);
            viewHolder.timeView.setText(stringByDuration);
            TextView textView = viewHolder.timeView;
            textView.setContentDescription(stringByDuration + ", " + getContext().getString(C0690R.string.go_to_bookmark) + ", " + getContext().getString(C0690R.string.button));
            ImageView imageView = viewHolder.deleteView;
            StringBuilder sb = new StringBuilder();
            sb.append(stringByDuration);
            sb.append(", ");
            sb.append(getContext().getString(C0690R.string.delete_bookmark));
            imageView.setContentDescription(sb.toString());
            if (bookmarkItem.title == null) {
                bookmarkItem.title = "";
            }
            viewHolder.descriptionView.setText(bookmarkItem.title);
            if (bookmarkItem.title.equals("")) {
                viewHolder.descriptionView.setHint(C0690R.string.bookmark_list_item_hint);
            } else {
                viewHolder.descriptionView.setHint("");
            }
            viewHolder.descriptionView.setContentDescription(AssistantProvider.getInstance().getContentDesToButton(bookmarkItem.title));
        }
        if (this.mPlayingPosition == i) {
            viewHolder.timeView.setTextColor(getContext().getColor(C0690R.C0691color.bookmark_list_text_select_color));
            viewHolder.timeView.setBackgroundTintList(getContext().getColorStateList(C0690R.C0691color.bookmark_list_text_bg_select_color));
            viewHolder.descriptionView.setTextColor(getContext().getColor(C0690R.C0691color.bookmark_list_description_bg_select_color));
        } else {
            viewHolder.timeView.setTextColor(getContext().getColor(C0690R.C0691color.bookmark_list_text_color));
            viewHolder.timeView.setBackgroundTintList(getContext().getColorStateList(C0690R.C0691color.bookmark_list_text_bg_color));
            viewHolder.descriptionView.setTextColor(getContext().getColor(C0690R.C0691color.bookmark_list_text_color));
        }
        return view;
    }

    private View getCustomView(ViewGroup viewGroup) {
        View inflate = this.mInflater.inflate(C0690R.layout.bookmark_list_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.deleteView = (ImageView) inflate.findViewById(C0690R.C0693id.bookmark_item_delete);
        ImageView imageView = viewHolder.deleteView;
        if (imageView != null) {
            imageView.setOnClickListener(this);
//            viewHolder.deleteView.semSetHoverPopupType(1);
        }
        viewHolder.timeView = (TextView) inflate.findViewById(C0690R.C0693id.bookmark_item_time);
        ViewProvider.setMaxFontSize(getContext(), viewHolder.timeView);
        viewHolder.timeView.setOnClickListener(this);
        viewHolder.descriptionView = (TextView) inflate.findViewById(C0690R.C0693id.bookmark_item_title);
        ViewProvider.setMaxFontSize(getContext(), viewHolder.descriptionView);
        viewHolder.descriptionView.setOnClickListener(this);
        inflate.setTag(viewHolder);
        return inflate;
    }

    private String getStringByDuration(int i) {
        int i2 = i / 1000;
        int i3 = i2 / 3600;
        int i4 = (i2 / 60) % 60;
        int i5 = i2 % 60;
        if (i3 > 0) {
            return String.format(Locale.getDefault(), "%d:%02d:%02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5)});
        }
        return String.format(Locale.getDefault(), "%02d:%02d", new Object[]{Integer.valueOf(i4), Integer.valueOf(i5)});
    }

    public void addBookmark(int i) {
        insert(new BookmarkItem(i, (String) null), expectInsertPosition(i));
    }

    public void updateTitle(int i, String str) {
        BookmarkItem bookmarkItem;
        if (i < getCount() && (bookmarkItem = (BookmarkItem) getItem(i)) != null) {
            bookmarkItem.title = str;
            MetadataRepository.getInstance().updateBookmarkTitle(bookmarkItem.mTime, str);
        }
    }

    public int getTime(int i) {
        BookmarkItem bookmarkItem;
        if (i < getCount() && (bookmarkItem = (BookmarkItem) getItem(i)) != null) {
            return bookmarkItem.mTime;
        }
        return -1;
    }

    public String getTitle(int i) {
        BookmarkItem bookmarkItem;
        if (i < getCount() && (bookmarkItem = (BookmarkItem) getItem(i)) != null) {
            return bookmarkItem.title;
        }
        return null;
    }

    public void deleteBookmark(int i) {
        BookmarkItem bookmarkItem;
        if (i < getCount() && (bookmarkItem = (BookmarkItem) getItem(i)) != null) {
            MetadataRepository.getInstance().removeBookmark(bookmarkItem.mTime);
            remove(bookmarkItem);
        }
    }

    public int expectInsertPosition(int i) {
        int count = getCount();
        int i2 = 0;
        for (int i3 = 0; i3 < count; i3++) {
            BookmarkItem bookmarkItem = (BookmarkItem) getItem(i3);
            if (bookmarkItem != null) {
                if (bookmarkItem.mTime > i) {
                    break;
                }
                i2++;
            }
        }
        return i2;
    }

    public int expectDeletePosition(int i) {
        int count = getCount();
        int i2 = 0;
        for (int i3 = 0; i3 < count; i3++) {
            BookmarkItem bookmarkItem = (BookmarkItem) getItem(i3);
            if (bookmarkItem != null) {
                if (bookmarkItem.mTime >= i) {
                    break;
                }
                i2++;
            }
        }
        return i2;
    }

    public boolean setPlayingPosition(int i) {
        int count = getCount();
        if (count == 0) {
            return false;
        }
        int i2 = count - 1;
        int i3 = -1;
        int i4 = 0;
        while (true) {
            if (i4 >= count) {
                break;
            } else if (((BookmarkItem) getItem(i4)).mTime > i) {
                i2 = i3;
                break;
            } else {
                i3++;
                i4++;
            }
        }
        Log.m19d(TAG, "setPlayingPosition time : " + i + " playing position  : " + i2 + " cnt : " + count);
        if (i2 == this.mPlayingPosition) {
            return false;
        }
        this.mPlayingPosition = i2;
        return true;
    }

    public int getPlayingPosition() {
        return this.mPlayingPosition;
    }

    public void onClick(View view) {
        if (this.onItemDetailClickListener != null) {
            View view2 = (View) view.getParent();
            ViewHolder viewHolder = (ViewHolder) view2.getTag();
            switch (view.getId()) {
                case C0690R.C0693id.bookmark_item_delete:
                    OnItemDetailClickListener onItemDetailClickListener2 = this.onItemDetailClickListener;
                    int i = viewHolder.position;
                    onItemDetailClickListener2.onDeleteClick(view2, i, (long) i, DetailType.DELETE);
                    return;
                case C0690R.C0693id.bookmark_item_time:
                    OnItemDetailClickListener onItemDetailClickListener3 = this.onItemDetailClickListener;
                    int i2 = viewHolder.position;
                    onItemDetailClickListener3.onTimeClick(view2, i2, (long) i2, DetailType.TIME);
                    return;
                case C0690R.C0693id.bookmark_item_title:
                    OnItemDetailClickListener onItemDetailClickListener4 = this.onItemDetailClickListener;
                    int i3 = viewHolder.position;
                    onItemDetailClickListener4.onTitleClick(view2, i3, (long) i3, DetailType.TITLE);
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.adapter.BookmarkListAdapter$ViewHolder */
    private static class ViewHolder {
        ImageView deleteView;
        TextView descriptionView;
        int position;
        TextView timeView;

        private ViewHolder() {
        }
    }

    public void registerItemDetailTouchListener(OnItemDetailClickListener onItemDetailClickListener2) {
        this.onItemDetailClickListener = onItemDetailClickListener2;
    }

    /* renamed from: com.sec.android.app.voicenote.ui.adapter.BookmarkListAdapter$BookmarkItem */
    public static class BookmarkItem implements Comparable<BookmarkItem> {
        public int mTime;
        public String title;

        public BookmarkItem(int i, String str) {
            this.mTime = i;
            this.title = str;
        }

        public int compareTo(@NonNull BookmarkItem bookmarkItem) {
            return Integer.compare(this.mTime, bookmarkItem.mTime);
        }
    }

    public void updateItemList(List<BookmarkItem> list) {
        clear();
        addAll(list);
        notifyDataSetChanged();
    }
}
