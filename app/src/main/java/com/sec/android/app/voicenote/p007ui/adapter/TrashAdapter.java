package com.sec.android.app.voicenote.p007ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.TrashHelper;
import com.sec.android.app.voicenote.data.trash.TrashInfo;
import com.sec.android.app.voicenote.p007ui.adapter.ListAdapter;
import com.sec.android.app.voicenote.provider.CheckedItemProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.NFCProvider;
import com.sec.android.app.voicenote.provider.PhoneStateProvider;
import com.sec.android.app.voicenote.provider.StorageProvider;
import com.sec.android.app.voicenote.service.AudioFormat;
import com.sec.android.app.voicenote.service.BookmarkHolder;
import com.sec.android.app.voicenote.service.Engine;
import java.util.List;

/* renamed from: com.sec.android.app.voicenote.ui.adapter.TrashAdapter */
public class TrashAdapter extends ListAdapter {
    private static final String TAG = "TrashAdapter";
    private static int TYPE_ITEM = 2;
    private static int TYPE_TEXT = 1;
    /* access modifiers changed from: private */
    public Context mContext;
    private List<TrashInfo> mListViewItems;
    /* access modifiers changed from: private */
    public OnItemClickListener mListener = null;
    /* access modifiers changed from: private */
    public boolean mPauseBySeek = false;

    /* renamed from: com.sec.android.app.voicenote.ui.adapter.TrashAdapter$OnItemClickListener */
    public interface OnItemClickListener {
        boolean onHeaderClick(View view, int i);

        void onItemClick(View view, int i);

        boolean onItemLongClick(View view, int i);
    }

    public TrashAdapter(Context context, List<TrashInfo> list) {
        super(context, (Cursor) null);
        this.mContext = context;
        this.mListViewItems = list;
    }

    public void setOnTouchTrashItemListener(OnItemClickListener onItemClickListener) {
        this.mListener = onItemClickListener;
    }

    @NonNull
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == TYPE_TEXT) {
            return new DescriptionViewHolder(LayoutInflater.from(this.mContext).inflate(C0690R.layout.trash_description_item, viewGroup, false));
        }
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(C0690R.layout.trash_list_item, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder viewHolder, int i) {
        if (getItemViewType(i) == TYPE_TEXT) {
            viewHolder.mRoundMode = 0;
            return;
        }
        TrashInfo trashInfo = this.mListViewItems.get(i);
        if (trashInfo != null) {
            ViewHolder viewHolder2 = (ViewHolder) viewHolder;
            if (i == 1) {
                viewHolder2.mRoundMode = 3;
            } else {
                viewHolder2.mRoundMode = 0;
            }
            viewHolder2.trashItemName.setText(trashInfo.getName());
            viewHolder2.trashItemName.setTextColor(this.mContext.getResources().getColor(C0690R.C0691color.listview_title_normal, (Resources.Theme) null));
            viewHolder2.trashItemDuration.setText(stringForTime(trashInfo.getDuration()));
            if (i < this.mListViewItems.size() - 1) {
                viewHolder2.viewDivider.setVisibility(0);
            } else {
                viewHolder2.viewDivider.setVisibility(8);
            }
            if (this.mIsSelectionMode) {
                viewHolder2.trashCheckBox.setAlpha(1.0f);
                viewHolder2.playPauseIcon.setVisibility(8);
                viewHolder2.trashCheckBox.setChecked(CheckedItemProvider.isChecked((long) trashInfo.getIdFile().intValue()));
            } else {
                viewHolder2.trashCheckBox.setAlpha(0.0f);
                viewHolder2.playPauseIcon.setVisibility(0);
                viewHolder2.trashCheckBox.setChecked(false);
            }
            String stringForTime = stringForTime(trashInfo.getDuration());
            int playerState = Engine.getInstance().getPlayerState();
            if (playerState != 3) {
                if (playerState != 4) {
                    setNormalItem(viewHolder2);
                    setTitleViewColor(viewHolder2.trashItemName, trashInfo.getName(), this.mContext);
                } else {
                    if (Engine.getInstance().getPath().equals(trashInfo.getPath())) {
                        viewHolder2.seekbar.setVisibility(0);
                        ColorStateList colorToColorStateList = colorToColorStateList(this.mContext.getResources().getColor(C0690R.C0691color.listrow_seekbar_fg_color, (Resources.Theme) null));
//                        viewHolder2.seekbar.semSetFluidEnabled(true);
                        viewHolder2.seekbar.setThumbTintList(colorToColorStateList);
                        viewHolder2.seekbar.setMax(this.mDuration);
                        viewHolder2.seekbar.setProgress(this.mCurrentPosition);
                        viewHolder2.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                                if (z) {
                                    Engine.getInstance().seekTo(seekBar.getProgress());
                                }
                            }

                            public void onStartTrackingTouch(SeekBar seekBar) {
                                if (Engine.getInstance().getPlayerState() == 3) {
                                    boolean unused = TrashAdapter.this.mPauseBySeek = true;
                                    Engine.getInstance().pausePlay();
                                }
                            }

                            public void onStopTrackingTouch(SeekBar seekBar) {
                                if (TrashAdapter.this.mPauseBySeek) {
                                    int resumePlay = Engine.getInstance().resumePlay();
                                    if (resumePlay == -103) {
                                        Toast.makeText(TrashAdapter.this.mContext, C0690R.string.no_play_during_call, 0).show();
                                    } else if (resumePlay == 0) {
                                        boolean unused = TrashAdapter.this.mPauseBySeek = false;
                                    }
                                }
                            }
                        });
                        viewHolder2.positionView.setVisibility(0);
                        viewHolder2.positionView.setText(stringForTime((long) this.mCurrentPosition) + " / " + stringForTime);
                        viewHolder2.trashItemDuration.setVisibility(4);
                        viewHolder2.dateView.setVisibility(4);
                        viewHolder2.trashItemName.setTextColor(this.mContext.getResources().getColor(C0690R.C0691color.listview_title_play, (Resources.Theme) null));
                    } else {
                        setNormalItem(viewHolder2);
                        setTitleViewColor(viewHolder2.trashItemName, trashInfo.getName(), this.mContext);
                    }
                    changePlayerIcon(4, viewHolder2);
                }
            } else if (Engine.getInstance().getPath().equals(trashInfo.getPath())) {
                viewHolder2.seekbar.setVisibility(0);
                viewHolder2.seekbar.setThumbTintList(colorToColorStateList(this.mContext.getResources().getColor(C0690R.C0691color.listrow_seekbar_fg_color, (Resources.Theme) null)));
                viewHolder2.seekbar.setMax(this.mDuration);
                viewHolder2.seekbar.setProgress(this.mCurrentPosition);
                viewHolder2.positionView.setVisibility(0);
                viewHolder2.positionView.setText(stringForTime((long) this.mCurrentPosition) + " / " + stringForTime);
                viewHolder2.dateView.setVisibility(4);
                viewHolder2.trashItemDuration.setVisibility(4);
                viewHolder2.trashItemName.setTextColor(this.mContext.getResources().getColor(C0690R.C0691color.listview_title_play, (Resources.Theme) null));
                setProgressHoverWindow(viewHolder2.seekbar, true);
                changePlayerIcon(3, viewHolder2);
            } else {
                setNormalItem(viewHolder2);
                setTitleViewColor(viewHolder2.trashItemName, trashInfo.getName(), this.mContext);
            }
            if (trashInfo.getYearName() == null || !trashInfo.getYearName().contains(NFCProvider.NFC_TAGGED)) {
                viewHolder2.nfcIcon.setVisibility(8);
            } else {
                viewHolder2.nfcIcon.setVisibility(0);
            }
            if (trashInfo.getRecordingMode() == 4) {
                viewHolder2.memoIcon.setVisibility(0);
            } else {
                viewHolder2.memoIcon.setVisibility(8);
            }
            if (BookmarkHolder.getInstance().get(trashInfo.getPath())) {
                viewHolder2.bookmarkIcon.setVisibility(0);
            } else {
                viewHolder2.bookmarkIcon.setVisibility(8);
            }
            if (AudioFormat.MimeType.AMR.equals(trashInfo.getMimeType())) {
                viewHolder2.mmsView.setVisibility(0);
            } else {
                viewHolder2.mmsView.setVisibility(8);
            }
            String externalStorageStateSd = StorageProvider.getExternalStorageStateSd();
            String convertToSDCardReadOnlyPath = StorageProvider.convertToSDCardReadOnlyPath(StorageProvider.getRootPath(1));
            if (externalStorageStateSd == null || !externalStorageStateSd.equals("mounted") || !trashInfo.getPath().startsWith(convertToSDCardReadOnlyPath)) {
                viewHolder2.sdCardIcon.setVisibility(8);
            } else {
                viewHolder2.sdCardIcon.setVisibility(0);
            }
            viewHolder2.dateView.setText(getMediumDateFormat(trashInfo.getDateTaken()));
            viewHolder2.itemView.setActivated(CheckedItemProvider.isChecked((long) trashInfo.getIdFile().intValue()));
        }
    }

    public int getItemViewType(int i) {
        if (i == 0) {
            return TYPE_TEXT;
        }
        return TYPE_ITEM;
    }

    public int getItemCount() {
        List<TrashInfo> list = this.mListViewItems;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public long getItemId(int i) {
        if (getItemViewType(i) == TYPE_TEXT) {
            return -1;
        }
        return (long) this.mListViewItems.get(i).getIdFile().intValue();
    }

    public String getItemTitle(int i) {
        return this.mListViewItems.get(i).getName();
    }

    public void onDestroy() {
        Log.m26i(TAG, "onDestroy");
        List<TrashInfo> list = this.mListViewItems;
        if (list != null) {
            list.clear();
            this.mListViewItems = null;
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.adapter.TrashAdapter$DescriptionViewHolder */
    public class DescriptionViewHolder extends ListAdapter.ViewHolder {
        TextView txtDescription;

        DescriptionViewHolder(View view) {
            super(view);
            this.txtDescription = (TextView) view.findViewById(C0690R.C0693id.trash_description);
            String externalStorageStateSd = StorageProvider.getExternalStorageStateSd();
            if (externalStorageStateSd == null || !externalStorageStateSd.equals("mounted")) {
                this.txtDescription.setText(TrashAdapter.this.mContext.getResources().getQuantityString(C0690R.plurals.trash_body_notification_no_sdcard, TrashHelper.getInstance().getKeepInTrashDays(), new Object[]{Integer.valueOf(TrashHelper.getInstance().getKeepInTrashDays())}));
                return;
            }
            this.txtDescription.setText(TrashAdapter.this.mContext.getResources().getQuantityString(C0690R.plurals.trash_body_notification_with_sdcard, TrashHelper.getInstance().getKeepInTrashDays(), new Object[]{Integer.valueOf(TrashHelper.getInstance().getKeepInTrashDays())}));
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.adapter.TrashAdapter$ViewHolder */
    public class ViewHolder extends ListAdapter.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView bookmarkIcon;
        TextView dateView;
        RelativeLayout mainRow;
        ImageView memoIcon;
        TextView mmsView;
        ImageView nfcIcon;
        ImageButton pauseIcon;
        ImageButton playIcon;
        FrameLayout playPauseIcon;
        TextView positionView;
        ImageView sdCardIcon;
        SeekBar seekbar;
        CheckBox trashCheckBox;
        TextView trashItemDuration;
        TextView trashItemName;
        ImageView viewDivider;

        ViewHolder(View view) {
            super(view);
            view.setHapticFeedbackEnabled(true);
            this.mainRow = (RelativeLayout) view.findViewById(C0690R.C0693id.main_row);
            this.playPauseIcon = (FrameLayout) view.findViewById(C0690R.C0693id.listrow_play_pause_icon);
            this.trashItemName = (TextView) view.findViewById(C0690R.C0693id.listrow_title);
            this.trashItemDuration = (TextView) view.findViewById(C0690R.C0693id.listrow_duration);
            this.positionView = (TextView) view.findViewById(C0690R.C0693id.listrow_position);
            this.trashCheckBox = (CheckBox) view.findViewById(C0690R.C0693id.listrow_checkbox);
            this.playIcon = (ImageButton) view.findViewById(C0690R.C0693id.listrow_play_icon);
            this.pauseIcon = (ImageButton) view.findViewById(C0690R.C0693id.listrow_pause_icon);
            this.mmsView = (TextView) view.findViewById(C0690R.C0693id.listrow_mms_label);
            this.bookmarkIcon = (ImageView) view.findViewById(C0690R.C0693id.listrow_bookmark_label);
            this.memoIcon = (ImageView) view.findViewById(C0690R.C0693id.listrow_voicememo_label);
            this.nfcIcon = (ImageView) view.findViewById(C0690R.C0693id.listrow_nfc_label);
            this.sdCardIcon = (ImageView) view.findViewById(C0690R.C0693id.listrow_sdCard_label);
            this.dateView = (TextView) view.findViewById(C0690R.C0693id.listrow_date);
            this.seekbar = (SeekBar) view.findViewById(C0690R.C0693id.listrow_seekbar);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            this.viewDivider = (ImageView) view.findViewById(C0690R.C0693id.view_divider);
            this.playIcon.setOnClickListener(this);
            this.playIcon.setFocusable(false);
//            this.playIcon.semSetHoverPopupType(1);
            this.pauseIcon.setOnClickListener(this);
            this.pauseIcon.setFocusable(false);
//            this.pauseIcon.semSetHoverPopupType(1);
            this.playPauseIcon.setOnClickListener(this);
            this.playPauseIcon.setFocusable(false);
        }

        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            switch (view.getId()) {
                case C0690R.C0693id.listrow_layout:
                    if (TrashAdapter.this.mListener != null) {
                        TrashAdapter.this.mListener.onItemClick(view, adapterPosition);
                        return;
                    }
                    return;
                case C0690R.C0693id.listrow_pause_icon:
                case C0690R.C0693id.listrow_play_icon:
                    if (!PhoneStateProvider.getInstance().isCallIdle(TrashAdapter.this.mContext)) {
                        Toast.makeText(TrashAdapter.this.mContext, C0690R.string.no_play_during_call, 0).show();
                        return;
                    } else if (TrashAdapter.this.mListener != null) {
                        TrashAdapter.this.mListener.onHeaderClick(view, adapterPosition);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public boolean onLongClick(View view) {
            if (TrashAdapter.this.mListener != null) {
                return TrashAdapter.this.mListener.onItemLongClick(view, getAdapterPosition());
            }
            return false;
        }
    }
}
