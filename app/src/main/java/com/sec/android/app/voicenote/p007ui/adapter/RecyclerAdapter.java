package com.sec.android.app.voicenote.p007ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.p007ui.view.WaveView;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.WaveProvider;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.SimpleEngine;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/* renamed from: com.sec.android.app.voicenote.ui.adapter.RecyclerAdapter */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private static final int MIN_WAVE_SIZE = 3;
    private static final String TAG = "RecyclerAdapter";
    private Context mContext;
    private int mCurrentLatestItem;
    private long mCurrentTime = 0;
    private int mInitTotalLength;
    private boolean mIsSimpleMode;
    private long mLastUpdateLogTime = 0;
    private int mRecordMode = 1;
    private int mWaveViewSize;
    private List<WaveView> waveList = new ArrayList();

    public RecyclerAdapter(Context context, int i, boolean z) {
        this.mIsSimpleMode = z;
        initialize(context, i);
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(C0690R.layout.adapter_recycler, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        WaveView waveView;
        if (i < this.waveList.size()) {
            waveView = this.waveList.get(i);
        } else {
            waveView = new WaveView(this.mContext, this.waveList.size(), this.mRecordMode, this.mIsSimpleMode);
            this.waveList.add(waveView);
        }
        ViewGroup viewGroup = (ViewGroup) waveView.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(waveView);
        }
        if ((waveView.getViewHeight() != WaveProvider.WAVE_VIEW_HEIGHT && !this.mIsSimpleMode) || (waveView.getViewHeight() != WaveProvider.SIMPLE_WAVE_VIEW_HEIGHT && this.mIsSimpleMode)) {
            waveView.requestDraw();
        }
        viewHolder.layout.removeAllViews();
        viewHolder.layout.addView(waveView);
        viewHolder.itemView.setTag(waveView);
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) viewHolder.layout.getLayoutParams();
        layoutParams.width = WaveProvider.WAVE_VIEW_WIDTH;
        viewHolder.layout.setLayoutParams(layoutParams);
        this.mCurrentLatestItem = viewHolder.getAdapterPosition();
    }

    public int getItemCount() {
        if (this.mWaveViewSize > this.waveList.size()) {
            return this.mWaveViewSize;
        }
        return this.waveList.size();
    }

    public void initialize(Context context, int i) {
        initialize(context, i, 3, 0);
    }

    public void initialize(Context context, int i, int i2, int i3) {
        Log.m26i(TAG, "initialize : recordMode = " + i + ", waveViewSize = " + i2 + ", amplitudeSize = " + i3);
        this.mContext = context;
        this.mRecordMode = i;
        this.mWaveViewSize = i2;
        this.mInitTotalLength = i3 * WaveProvider.AMPLITUDE_TOTAL_WIDTH;
        cleanUp();
        for (int i4 = 0; i4 < 3; i4++) {
            this.waveList.add(new WaveView(this.mContext, i4, this.mRecordMode, this.mIsSimpleMode));
        }
        notifyDataSetChanged();
    }

    public void cleanUp() {
        this.waveList.clear();
    }

    public int getCurrentLatestItem() {
        return this.mCurrentLatestItem;
    }

    public void addItem(Context context) {
        Log.m26i(TAG, "addItem - size : " + this.waveList.size());
        if (context == null) {
            Log.m22e(TAG, "addItem Context is NULL !!");
            return;
        }
        List<WaveView> list = this.waveList;
        list.add(new WaveView(context, list.size(), this.mRecordMode, this.mIsSimpleMode));
        notifyDataSetChanged();
    }

    public void setRepeatTimeSimple(SimpleEngine simpleEngine, int i) {
        if (i < this.waveList.size()) {
            int[] repeatPosition = simpleEngine.getRepeatPosition();
            this.waveList.get(i).setRepeatTime(repeatPosition[0], repeatPosition[1]);
        }
    }

    public void setRepeatTime(int i) {
        if (i < this.waveList.size()) {
            int[] repeatPosition = Engine.getInstance().getRepeatPosition();
            this.waveList.get(i).setRepeatTime(repeatPosition[0], repeatPosition[1]);
        }
    }

    public int getTotalWaveViewWidth() {
        int size = (this.waveList.size() - 3) * WaveProvider.WAVE_VIEW_WIDTH;
        List<WaveView> list = this.waveList;
        int amplitudeCount = size + (list.get(list.size() - 2).getAmplitudeCount() * WaveProvider.AMPLITUDE_TOTAL_WIDTH);
        int i = this.mIsSimpleMode ? WaveProvider.SIMPLE_WAVE_AREA_WIDTH : WaveProvider.WAVE_AREA_WIDTH;
        int i2 = this.mInitTotalLength;
        if (amplitudeCount <= i2) {
            amplitudeCount = i2;
        }
        return amplitudeCount + i;
    }

    public void removeLastItem(Context context, int i) {
        Log.m26i(TAG, "removeLastItem - count : " + i);
        for (int i2 = 0; i2 < i; i2++) {
            List<WaveView> list = this.waveList;
            list.remove(list.size() - 1);
        }
        List<WaveView> list2 = this.waveList;
        list2.add(new WaveView(context, list2.size(), this.mRecordMode, this.mIsSimpleMode));
        notifyDataSetChanged();
    }

    public void setRecordMode(int i) {
        Log.m26i(TAG, "setRecordMode - mode : " + i);
        this.mRecordMode = i;
    }

    public int getRecordMode() {
        return this.mRecordMode;
    }

    public void addAmplitude(int i, int i2, int i3, boolean z) {
        if (Log.ENG) {
            this.mCurrentTime = System.currentTimeMillis();
            if (this.mCurrentTime - this.mLastUpdateLogTime > 1000) {
                Log.m29v(TAG, "addAmplitude - " + i + '[' + i2 + "] : " + i3);
                this.mLastUpdateLogTime = this.mCurrentTime;
            }
        }
        if (this.waveList.size() <= i) {
            Log.m22e(TAG, "addAmplitude outOfIndex size : " + this.waveList.size());
            return;
        }
        int amplitudeIndex = this.waveList.get(i).getAmplitudeIndex();
        int i4 = this.mRecordMode;
        if (i4 != 4) {
            if (i4 == 5) {
                this.waveList.get(i).setAmplitudeIndex(i2);
            } else if (amplitudeIndex < i2) {
                this.waveList.get(i).setAmplitudeIndex(i2);
            }
        } else if (i == 1 && i2 >= 2 && i2 <= 20 && amplitudeIndex == 0) {
            Log.m26i(TAG, "addAmplitude recover amplitude for VOICEMEMO");
            for (int i5 = 0; i5 < i2 - 1; i5++) {
                this.waveList.get(i).addAmplitude(0, z);
            }
        } else if (amplitudeIndex < i2) {
            this.waveList.get(i).setAmplitudeIndex(i2);
        }
        this.waveList.get(i).addAmplitude(i3, z);
    }

    public void updateDataArray(Context context, int i, int[] iArr, int i2) {
        Log.m29v(TAG, "updateDataArray - position : " + i);
        while (this.waveList.size() <= i) {
            List<WaveView> list = this.waveList;
            list.add(new WaveView(context, list.size(), this.mRecordMode, this.mIsSimpleMode));
        }
        this.waveList.get(i).updateWaveArray(iArr, i2);
        notifyDataSetChanged();
    }

    public void clearView(int i) {
        Log.m19d(TAG, "clearView - position : " + i);
        int size = this.waveList.size();
        if (size <= i) {
            Log.m32w(TAG, "clearView outOfIndex size : " + size);
            return;
        }
        this.waveList.get(i).initAmplitude();
    }

    public void setIndex(int i, int i2) {
        Log.m32w(TAG, "setAmplitudeIndex - position : " + i + " index : " + i2);
        int size = this.waveList.size();
        if (size <= i) {
            Log.m22e(TAG, "setAmplitudeIndex outOfIndex size : " + size);
            return;
        }
        this.waveList.get(i).setAmplitudeIndex(i2);
        while (true) {
            i++;
            if (i < size) {
                this.waveList.get(i).setAmplitudeIndex(0);
            } else {
                Log.m32w(TAG, "setAmplitudeIndex DONE - size : " + size);
                notifyDataSetChanged();
                return;
            }
        }
    }

    public void setRepeatTime(int i, int i2) {
        Log.m29v(TAG, "setRepeatTime - startTime : " + i + " endTime : " + i2);
        int size = this.waveList.size();
        for (int i3 = 0; i3 < size; i3++) {
            if (this.waveList.get(i3) != null) {
                this.waveList.get(i3).setRepeatTime(i, i2);
            }
        }
        notifyDataSetChanged();
    }

    public void setRepeatStartTime(int i) {
        Log.m29v(TAG, "setRepeatStartTime : " + i);
        int size = this.waveList.size();
        for (int i2 = 1; i2 < size; i2++) {
            this.waveList.get(i2).setRepeatStartTime(i);
        }
        notifyDataSetChanged();
    }

    public void setRepeatEndTime(int i) {
        Log.m29v(TAG, "setRepeatEndTime : " + i);
        for (WaveView repeatEndTime : this.waveList) {
            repeatEndTime.setRepeatEndTime(i);
        }
        notifyDataSetChanged();
    }

    public void addBookmark(int i, int i2) {
        int i3;
        Log.m26i(TAG, "addBookmark - position : " + i + " index : " + i2);
        if (this.waveList.size() <= i) {
            Log.m32w(TAG, "addBookmark - IndexOutOfBoundsException size : " + this.waveList.size() + " invalid index : " + i);
            return;
        }
        try {
            this.waveList.get(i).addBookmark(i2);
            if (i2 < 3) {
                this.waveList.get(i - 1).addBookmark(WaveProvider.NUM_OF_AMPLITUDE + i2);
            } else if (i2 > WaveProvider.NUM_OF_AMPLITUDE - 3 && (i3 = i + 1) < this.waveList.size()) {
                this.waveList.get(i3).addBookmark(i2 - WaveProvider.NUM_OF_AMPLITUDE);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.m22e(TAG, e.toString());
        }
    }

    public boolean removeBookmark(int i, int i2) {
        int i3;
        boolean z = false;
        if (this.waveList.size() <= i || i < 0) {
            Log.m32w(TAG, "removeBookmark - out of index : " + i + " length : " + this.waveList.size());
            return false;
        }
        try {
            WaveView waveView = this.waveList.get(i);
            if (waveView != null) {
                z = waveView.removeBookmark(i2);
                if (i2 < 3) {
                    if (i >= 1) {
                        z = this.waveList.get(i - 1).removeBookmark(WaveProvider.NUM_OF_AMPLITUDE + i2);
                    }
                } else if (i2 > WaveProvider.NUM_OF_AMPLITUDE - 3 && this.waveList.size() > (i3 = i + 1)) {
                    z = this.waveList.get(i3).removeBookmark(i2 - WaveProvider.NUM_OF_AMPLITUDE);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            Log.m24e(TAG, "IndexOutOfBoundsException", (Throwable) e);
        }
        if (z) {
            notifyDataSetChanged();
        }
        return z;
    }

    public void clearBookmarks() {
        for (WaveView next : this.waveList) {
            if (next != null) {
                next.clearBookmarks();
            }
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.adapter.RecyclerAdapter$ViewHolder */
    static class ViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout layout;

        private ViewHolder(View view) {
            super(view);
            this.layout = (FrameLayout) view.findViewById(C0690R.C0693id.recycler_layout);
        }
    }
}
