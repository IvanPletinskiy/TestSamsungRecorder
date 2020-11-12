package com.sec.android.app.voicenote.p007ui.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.CategoryInfo;
import com.sec.android.app.voicenote.common.util.DataRepository;
import com.sec.android.app.voicenote.provider.Log;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* renamed from: com.sec.android.app.voicenote.ui.adapter.CategoryManagementAdapter */
public class CategoryManagementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelper {
    private static final String TAG = "CategoryManagementAdapter";
    private Context mContext;
    private boolean mIsSelectionMode = false;
    /* access modifiers changed from: private */
    public List<CategoryInfo> mListViewItems;
    /* access modifiers changed from: private */
    public OnItemClickListener mListener = null;
    /* access modifiers changed from: private */
    public SerializableSparseBooleanArray mSelectedCategoryArrays = new SerializableSparseBooleanArray();

    /* renamed from: com.sec.android.app.voicenote.ui.adapter.CategoryManagementAdapter$OnItemClickListener */
    public interface OnItemClickListener {
        void onItemClick(View view, int i);

        boolean onItemLongClick(View view, int i);

        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    /* renamed from: com.sec.android.app.voicenote.ui.adapter.CategoryManagementAdapter$ViewType */
    public static class ViewType {
        public static final int FOOTER = 2;
        public static final int ITEM = 1;
    }

    public CategoryManagementAdapter(Context context, List<CategoryInfo> list) {
        this.mContext = context;
        this.mListViewItems = list;
    }

    public void setOnTouchCategoryItemListener(OnItemClickListener onItemClickListener) {
        this.mListener = onItemClickListener;
    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == 1) {
            return new ViewHolder(LayoutInflater.from(this.mContext).inflate(C0690R.layout.category_management_list_item, viewGroup, false));
        }
        return new FooterViewHolder(LayoutInflater.from(this.mContext).inflate(C0690R.layout.category_chooser_addcategory, viewGroup, false));
    }

    public int getItemViewType(int i) {
        if (i == this.mListViewItems.size() - 1 && this.mListViewItems.get(i) == null) {
            return 2;
        }
        return 1;
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        CategoryInfo item = getItem(i);
        if (item != null) {
            ViewHolder viewHolder2 = (ViewHolder) viewHolder;
            viewHolder2.categoryManagementItem.setText(item.getTitle());
            viewHolder2.categoryManagementItem.setContentDescription(item.getTitle());
            if (i < this.mListViewItems.size() - 1) {
                viewHolder2.viewDivider.setVisibility(0);
            } else {
                viewHolder2.viewDivider.setVisibility(8);
            }
            if (this.mIsSelectionMode) {
                viewHolder2.layoutArrowButton.setVisibility(0);
                viewHolder2.categoryManagemenCheckBox.setVisibility(0);
                if (viewHolder2.categoryManagemenCheckBox.isChecked() != this.mSelectedCategoryArrays.get(item.getIdCategory().intValue(), false)) {
                    viewHolder2.categoryManagemenCheckBox.setChecked(this.mSelectedCategoryArrays.get(item.getIdCategory().intValue(), false));
                }
                viewHolder2.itemView.setActivated(this.mSelectedCategoryArrays.get(item.getIdCategory().intValue(), false));
                if (item.getIdCategory().intValue() <= 3) {
                    viewHolder2.categoryManagemenCheckBox.setChecked(false);
                    viewHolder2.categoryManagemenCheckBox.setEnabled(false);
                    return;
                }
                viewHolder2.categoryManagemenCheckBox.setEnabled(true);
                boolean isChecked = viewHolder2.categoryManagemenCheckBox.isChecked();
                return;
            }
            viewHolder2.categoryManagemenCheckBox.setVisibility(8);
            viewHolder2.categoryManagemenCheckBox.setChecked(false);
            viewHolder2.layoutArrowButton.setVisibility(8);
        }
    }

    private CategoryInfo getItem(int i) {
        if (i < 0 || getItemCount() <= i) {
            return null;
        }
        return this.mListViewItems.get(i);
    }

    public int getItemCount() {
        return this.mListViewItems.size();
    }

    private boolean isSelectedCategoryConstantsKey(int i) {
        return this.mSelectedCategoryArrays.size() > 0 && this.mSelectedCategoryArrays.indexOfKey(i) >= 0;
    }

    public void toggleSelection(int i) {
        CategoryInfo item = getItem(i);
        if (item == null) {
            Log.m22e(TAG, "CategoryInfo is null !");
            return;
        }
        boolean z = true;
        if (isSelectedCategoryConstantsKey(item.getIdCategory().intValue())) {
            SerializableSparseBooleanArray serializableSparseBooleanArray = this.mSelectedCategoryArrays;
            int intValue = item.getIdCategory().intValue();
            if (this.mSelectedCategoryArrays.get(item.getIdCategory().intValue())) {
                z = false;
            }
            serializableSparseBooleanArray.put(intValue, z);
            return;
        }
        this.mSelectedCategoryArrays.put(item.getIdCategory().intValue(), true);
    }

    public long getIdSelected() {
        int size;
        SerializableSparseBooleanArray serializableSparseBooleanArray = this.mSelectedCategoryArrays;
        if (serializableSparseBooleanArray == null || (size = serializableSparseBooleanArray.size()) <= 0) {
            return -1;
        }
        for (int i = 0; i <= size - 1; i++) {
            int keyAt = this.mSelectedCategoryArrays.keyAt(i);
            if (this.mSelectedCategoryArrays.get(keyAt)) {
                return (long) keyAt;
            }
        }
        return -1;
    }

    public boolean isChecked(int i) {
        if (isSelectedCategoryConstantsKey(i)) {
            return this.mSelectedCategoryArrays.get(i);
        }
        return false;
    }

    public void removeSelection() {
        for (CategoryInfo idCategory : this.mListViewItems) {
            this.mSelectedCategoryArrays.put(idCategory.getIdCategory().intValue(), false);
        }
    }

    public void selectAll() {
        for (CategoryInfo next : this.mListViewItems) {
            if (next.getIdCategory().intValue() > 3) {
                this.mSelectedCategoryArrays.put(next.getIdCategory().intValue(), true);
            }
        }
    }

    public int getSelectedCount() {
        int size;
        SerializableSparseBooleanArray serializableSparseBooleanArray = this.mSelectedCategoryArrays;
        if (serializableSparseBooleanArray == null || (size = serializableSparseBooleanArray.size()) <= 0) {
            return 0;
        }
        int i = 0;
        for (int i2 = 0; i2 <= size - 1; i2++) {
            if (this.mSelectedCategoryArrays.get(this.mSelectedCategoryArrays.keyAt(i2))) {
                i++;
            }
        }
        return i;
    }

    public long[] getArrayIds() {
        int size;
        ArrayList arrayList = new ArrayList();
        SerializableSparseBooleanArray serializableSparseBooleanArray = this.mSelectedCategoryArrays;
        if (serializableSparseBooleanArray != null && (size = serializableSparseBooleanArray.size()) > 0) {
            for (int i = 0; i <= size - 1; i++) {
                int keyAt = this.mSelectedCategoryArrays.keyAt(i);
                if (this.mSelectedCategoryArrays.get(keyAt)) {
                    arrayList.add(Integer.valueOf(keyAt));
                }
            }
        }
        long[] jArr = new long[arrayList.size()];
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            jArr[i2] = (long) ((Integer) arrayList.get(i2)).intValue();
        }
        return jArr;
    }

    public void removeItems() {
        int i = 0;
        while (i < this.mListViewItems.size()) {
            CategoryInfo categoryInfo = this.mListViewItems.get(i);
            if (isSelectedCategoryConstantsKey(categoryInfo.getIdCategory().intValue()) && this.mSelectedCategoryArrays.get(categoryInfo.getIdCategory().intValue())) {
                this.mSelectedCategoryArrays.delete(categoryInfo.getIdCategory().intValue());
                this.mListViewItems.remove(categoryInfo);
                i--;
            }
            i++;
        }
    }

    public void onDestroy() {
        Log.m26i(TAG, "onDestroy");
        List<CategoryInfo> list = this.mListViewItems;
        if (list != null) {
            list.clear();
            this.mListViewItems = null;
        }
        if (this.mSelectedCategoryArrays != null) {
            this.mSelectedCategoryArrays = null;
        }
    }

    public SerializableSparseBooleanArray getSelectedCategoryArrays() {
        return this.mSelectedCategoryArrays;
    }

    public void setSelectedCategoryArray(SerializableSparseBooleanArray serializableSparseBooleanArray) {
        this.mSelectedCategoryArrays = serializableSparseBooleanArray;
    }

    public void setSelectionMode(boolean z) {
        this.mIsSelectionMode = z;
    }

    public void onItemMove(RecyclerView recyclerView, int i, int i2, boolean z) {
        Log.m26i(TAG, "onOrderChanged source : " + i + " destination : " + i2 + " - isFinished : " + z);
        int size = this.mListViewItems.size();
        if (i >= size || i2 >= size || i < 0 || i2 < 0) {
            Log.m22e(TAG, "source: " + i + ", destination: " + i2 + ", but listSize: " + size);
        } else if (i == i2) {
            Log.m26i(TAG, "source and destination is same");
        } else {
            ArrayList arrayList = new ArrayList();
            if (i < i2) {
                for (int i3 = i; i3 < i2; i3++) {
                    if (z) {
                        arrayList.add(this.mListViewItems.get(i3));
                    } else {
                        swap(i3, i3 + 1);
                    }
                }
            } else {
                for (int i4 = i; i4 > i2; i4--) {
                    if (z) {
                        arrayList.add(this.mListViewItems.get(i4));
                    } else {
                        swap(i4, i4 - 1);
                    }
                }
            }
            if (z) {
                arrayList.add(this.mListViewItems.get(i2));
                DataRepository.getInstance().getCategoryRepository().updatePosition(arrayList);
                notifyDataSetChanged();
                return;
            }
            notifyItemMoved(i, i2);
            if (i2 == 0) {
                recyclerView.announceForAccessibility(this.mContext.getResources().getString(C0690R.string.move_to_top));
            } else if (i2 == size - 1) {
                recyclerView.announceForAccessibility(this.mContext.getResources().getString(C0690R.string.move_to_bottom));
            } else if (i > i2) {
                recyclerView.announceForAccessibility(this.mContext.getResources().getString(C0690R.string.move_up));
            } else if (i < i2) {
                recyclerView.announceForAccessibility(this.mContext.getResources().getString(C0690R.string.move_down));
            }
        }
    }

    private void swap(int i, int i2) {
        int intValue = this.mListViewItems.get(i).getPosition().intValue();
        this.mListViewItems.get(i).setPosition(this.mListViewItems.get(i2).getPosition().intValue());
        this.mListViewItems.get(i2).setPosition(intValue);
        Collections.swap(this.mListViewItems, i, i2);
    }

    /* renamed from: com.sec.android.app.voicenote.ui.adapter.CategoryManagementAdapter$ViewHolder */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener, View.OnLongClickListener {
        CheckBox categoryManagemenCheckBox;
        TextView categoryManagementItem;
        ImageView imageViewChangeOrder;
        LinearLayout layoutArrowButton;
        View viewDivider;

        ViewHolder(View view) {
            super(view);
            this.categoryManagementItem = (TextView) view.findViewById(C0690R.C0693id.category_management_item);
            this.imageViewChangeOrder = (ImageView) view.findViewById(C0690R.C0693id.category_management_recorder_button);
            Context context = view.getContext();
            if (context != null) {
                this.imageViewChangeOrder.setContentDescription(context.getString(C0690R.string.custom_actions_tts) + " , " + context.getString(C0690R.string.reorder_description_tip_tts));
            }
            this.categoryManagemenCheckBox = (CheckBox) view.findViewById(C0690R.C0693id.list_item_cb);
            this.layoutArrowButton = (LinearLayout) view.findViewById(C0690R.C0693id.layout_arrow_button);
            this.layoutArrowButton.setOnTouchListener(this);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            this.viewDivider = view.findViewById(C0690R.C0693id.view_divider);
        }

        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            if (CategoryManagementAdapter.this.mListener != null) {
                CategoryManagementAdapter.this.mListener.onItemClick(view, adapterPosition);
            }
            CheckBox checkBox = this.categoryManagemenCheckBox;
            if (checkBox != null) {
                checkBox.setChecked(CategoryManagementAdapter.this.mSelectedCategoryArrays.get(((CategoryInfo) CategoryManagementAdapter.this.mListViewItems.get(adapterPosition)).getIdCategory().intValue(), false));
            }
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (CategoryManagementAdapter.this.mListener == null) {
                return false;
            }
            CategoryManagementAdapter.this.mListener.onStartDrag(this);
            return false;
        }

        public boolean onLongClick(View view) {
            if (CategoryManagementAdapter.this.mListener != null) {
                return CategoryManagementAdapter.this.mListener.onItemLongClick(view, getAdapterPosition());
            }
            return false;
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.adapter.CategoryManagementAdapter$FooterViewHolder */
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
            if (CategoryManagementAdapter.this.mListener != null) {
                CategoryManagementAdapter.this.mListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.adapter.CategoryManagementAdapter$SerializableSparseBooleanArray */
    public class SerializableSparseBooleanArray extends SparseBooleanArray implements Serializable {
        public SerializableSparseBooleanArray() {
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: java.lang.Object[]} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void writeObject(java.io.ObjectOutputStream r7) throws java.io.IOException {
            /*
                r6 = this;
                int r0 = r6.size()
                java.lang.Object[] r0 = new java.lang.Object[r0]
                int r1 = r0.length
                r2 = 1
                int r1 = r1 - r2
            L_0x0009:
                if (r1 < 0) goto L_0x0028
                r3 = 2
                java.lang.Object[] r3 = new java.lang.Object[r3]
                r4 = 0
                int r5 = r6.keyAt(r1)
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r3[r4] = r5
                boolean r4 = r6.valueAt(r1)
                java.lang.Boolean r4 = java.lang.Boolean.valueOf(r4)
                r3[r2] = r4
                r0[r1] = r3
                int r1 = r1 + -1
                goto L_0x0009
            L_0x0028:
                r7.writeObject(r0)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.adapter.CategoryManagementAdapter.SerializableSparseBooleanArray.writeObject(java.io.ObjectOutputStream):void");
        }

        private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            Object[] objArr = (Object[]) objectInputStream.readObject();
            for (int length = objArr.length - 1; length >= 0; length--) {
                Object[] objArr2 = (Object[]) objArr[length];
                append(((Integer) objArr2[0]).intValue(), ((Boolean) objArr2[1]).booleanValue());
            }
        }
    }
}
