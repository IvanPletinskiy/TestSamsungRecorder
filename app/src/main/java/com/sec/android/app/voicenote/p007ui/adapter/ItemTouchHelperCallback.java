package com.sec.android.app.voicenote.p007ui.adapter;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/* renamed from: com.sec.android.app.voicenote.ui.adapter.ItemTouchHelperCallback */
public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private int dragFrom = -1;
    private int dragTo = -1;
    private ItemTouchHelper mAdapter;

    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    public boolean isLongPressDragEnabled() {
        return false;
    }

    public void onSwipeFailed(RecyclerView.ViewHolder viewHolder, int i) {
    }

    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
    }

    public ItemTouchHelperCallback(ItemTouchHelper itemTouchHelper) {
        this.mAdapter = itemTouchHelper;
    }

    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return ItemTouchHelper.Callback.makeMovementFlags(3, 0);
    }

    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
        int adapterPosition = viewHolder.getAdapterPosition();
        int adapterPosition2 = viewHolder2.getAdapterPosition();
        if (this.dragFrom == -1) {
            this.dragFrom = adapterPosition;
        }
        this.dragTo = adapterPosition2;
//        this.mAdapter.onItemMove(recyclerView, adapterPosition, adapterPosition2, false);
        return true;
    }

    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int i;
        super.clearView(recyclerView, viewHolder);
        int i2 = this.dragFrom;
        if (!(i2 == -1 || (i = this.dragTo) == -1 || i2 == i)) {
//            this.mAdapter.onItemMove(recyclerView, i2, i, true);
        }
        this.dragTo = -1;
        this.dragFrom = -1;
    }
}
