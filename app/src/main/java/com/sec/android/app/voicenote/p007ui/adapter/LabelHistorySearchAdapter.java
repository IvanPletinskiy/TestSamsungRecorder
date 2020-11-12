package com.sec.android.app.voicenote.p007ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.LabelHistorySearchInfo;
import com.sec.android.app.voicenote.p007ui.adapter.LabelHistorySearchAdapter;
import java.util.List;

/* renamed from: com.sec.android.app.voicenote.ui.adapter.LabelHistorySearchAdapter */
public class LabelHistorySearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<LabelHistorySearchInfo> mHistorySearchInfoList;
    /* access modifiers changed from: private */
    public ListenerItemSearchClick mItemSearchClick;

    /* renamed from: com.sec.android.app.voicenote.ui.adapter.LabelHistorySearchAdapter$ListenerItemSearchClick */
    public interface ListenerItemSearchClick {
        void clickDeleteItem(int i);

        void clickItem(int i);
    }

    public LabelHistorySearchAdapter(Context context, List<LabelHistorySearchInfo> list, ListenerItemSearchClick listenerItemSearchClick) {
        this.mContext = context;
        this.mHistorySearchInfoList = list;
        this.mItemSearchClick = listenerItemSearchClick;
    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new LabelHistoryViewHolder(LayoutInflater.from(this.mContext).inflate(C0690R.layout.adapter_label_search_history, viewGroup, false));
    }

    public int getItemViewType(int i) {
        return super.getItemViewType(i);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((LabelHistoryViewHolder) viewHolder).mTitleView.setText(this.mHistorySearchInfoList.get(viewHolder.getAdapterPosition()).getLabel());
    }

    public int getItemCount() {
        List<LabelHistorySearchInfo> list = this.mHistorySearchInfoList;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    /* renamed from: com.sec.android.app.voicenote.ui.adapter.LabelHistorySearchAdapter$LabelHistoryViewHolder */
    private class LabelHistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView mDeleteView;
        TextView mTitleView;

        LabelHistoryViewHolder(View view) {
            super(view);
            this.mTitleView = (TextView) view.findViewById(C0690R.C0693id.search_history_label_text);
            this.mDeleteView = (ImageView) view.findViewById(C0690R.C0693id.search_history_item_remove);
            view.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    LabelHistorySearchAdapter.LabelHistoryViewHolder.this.lambda$new$0$LabelHistorySearchAdapter$LabelHistoryViewHolder(view);
                }
            });
            this.mDeleteView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    LabelHistorySearchAdapter.LabelHistoryViewHolder.this.lambda$new$1$LabelHistorySearchAdapter$LabelHistoryViewHolder(view);
                }
            });
        }

        public /* synthetic */ void lambda$new$0$LabelHistorySearchAdapter$LabelHistoryViewHolder(View view) {
            LabelHistorySearchAdapter.this.mItemSearchClick.clickItem(getAdapterPosition());
        }

        public /* synthetic */ void lambda$new$1$LabelHistorySearchAdapter$LabelHistoryViewHolder(View view) {
            LabelHistorySearchAdapter.this.mItemSearchClick.clickDeleteItem(getAdapterPosition());
        }
    }
}
