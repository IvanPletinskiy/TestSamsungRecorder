package com.sec.android.app.voicenote.p007ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.CategoryRepository;
import com.sec.android.app.voicenote.provider.CheckedItemProvider;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.MouseKeyboardProvider;
import com.sec.android.app.voicenote.provider.PhoneStateProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.uicore.FragmentController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/* renamed from: com.sec.android.app.voicenote.ui.adapter.ListAdapter */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> implements FragmentController.OnSceneChangeListener {
    private static final String TAG = "ListAdapter";
    private boolean isFocusOnFirstPart = false;
    private boolean isListItemSelected = false;
    /* access modifiers changed from: private */
    public Context mContext;
    private int mCurrentDate;
    private String mCurrentLanguage;
    private int mCurrentMonth;
    protected int mCurrentPosition = 0;
    private int mCurrentYear;
    private Cursor mCursor = null;
    protected int mDuration = 0;
    protected boolean mIsSelectionMode = false;
    /* access modifiers changed from: private */
    public OnItemClickListener mListener = null;
    /* access modifiers changed from: private */
    public boolean mPauseBySeek = false;
    private int mScene = 2;
    private int mTimeFormat;

    /* renamed from: com.sec.android.app.voicenote.ui.adapter.ListAdapter$OnItemClickListener */
    public interface OnItemClickListener {
        boolean onHeaderClick(View view, int i);

        void onItemClick(View view, int i, long j);

        boolean onItemLongClick(View view, int i);

        boolean onKey(View view, int i, KeyEvent keyEvent);
    }

    public ListAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
        Calendar instance = Calendar.getInstance();
        this.mCurrentDate = instance.get(5);
        this.mCurrentMonth = instance.get(2);
        this.mCurrentYear = instance.get(1);
        this.mCurrentLanguage = Locale.getDefault().getLanguage();
        updateTimeFormat();
    }

    public void updateTimeFormat() {
        Context context = this.mContext;
        if (context == null) {
            Log.m26i(TAG, "updateTimeFormat mContext is null!!");
            return;
        }
        if (DateFormat.is24HourFormat(context)) {
            this.mTimeFormat = 24;
        } else {
            this.mTimeFormat = 12;
        }
        String str = TAG;
        Log.m26i(str, "updateTimeFormat mTimeFormat = " + this.mTimeFormat);
    }

    public void onDestroy() {
        this.mContext = null;
    }

    public Cursor getCursor() {
        return this.mCursor;
    }

    public void swapCursor(Cursor cursor) {
        String str = TAG;
        Log.m26i(str, "swapCursor : " + cursor);
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(C0690R.layout.adapter_list, viewGroup, false));
    }

    /* JADX WARNING: Removed duplicated region for block: B:27:0x00f1  */
    /* JADX WARNING: Removed duplicated region for block: B:99:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onBindViewHolder(@androidx.annotation.NonNull com.sec.android.app.voicenote.p007ui.adapter.ListAdapter.ViewHolder r24, int r25) {
        /*
            r23 = this;
            r1 = r23
            r0 = r24
            java.lang.String r2 = "Exception"
            android.database.Cursor r3 = r1.mCursor
            if (r3 == 0) goto L_0x03bc
            boolean r3 = r3.isClosed()
            if (r3 == 0) goto L_0x0012
            goto L_0x03bc
        L_0x0012:
            android.database.Cursor r3 = r1.mCursor     // Catch:{ Exception -> 0x03b5 }
            r4 = r25
            r3.moveToPosition(r4)     // Catch:{ Exception -> 0x03b5 }
            android.database.Cursor r3 = r1.mCursor     // Catch:{ Exception -> 0x03b5 }
            android.database.Cursor r4 = r1.mCursor     // Catch:{ Exception -> 0x03b5 }
            java.lang.String r5 = "title"
            int r4 = r4.getColumnIndex(r5)     // Catch:{ Exception -> 0x03b5 }
            java.lang.String r3 = r3.getString(r4)     // Catch:{ Exception -> 0x03b5 }
            android.database.Cursor r4 = r1.mCursor     // Catch:{ Exception -> 0x03b5 }
            android.database.Cursor r5 = r1.mCursor     // Catch:{ Exception -> 0x03b5 }
            java.lang.String r6 = "datetaken"
            int r5 = r5.getColumnIndex(r6)     // Catch:{ Exception -> 0x03b5 }
            long r4 = r4.getLong(r5)     // Catch:{ Exception -> 0x03b5 }
            android.database.Cursor r6 = r1.mCursor     // Catch:{ Exception -> 0x03b5 }
            android.database.Cursor r7 = r1.mCursor     // Catch:{ Exception -> 0x03b5 }
            java.lang.String r8 = "date_modified"
            int r7 = r7.getColumnIndex(r8)     // Catch:{ Exception -> 0x03b5 }
            long r6 = r6.getLong(r7)     // Catch:{ Exception -> 0x03b5 }
            android.database.Cursor r8 = r1.mCursor     // Catch:{ Exception -> 0x03b5 }
            android.database.Cursor r9 = r1.mCursor     // Catch:{ Exception -> 0x03b5 }
            java.lang.String r10 = "duration"
            int r9 = r9.getColumnIndex(r10)     // Catch:{ Exception -> 0x03b5 }
            long r8 = r8.getLong(r9)     // Catch:{ Exception -> 0x03b5 }
            android.database.Cursor r10 = r1.mCursor     // Catch:{ Exception -> 0x03b5 }
            android.database.Cursor r11 = r1.mCursor     // Catch:{ Exception -> 0x03b5 }
            java.lang.String r12 = "mime_type"
            int r11 = r11.getColumnIndex(r12)     // Catch:{ Exception -> 0x03b5 }
            java.lang.String r10 = r10.getString(r11)     // Catch:{ Exception -> 0x03b5 }
            android.database.Cursor r11 = r1.mCursor     // Catch:{ Exception -> 0x03b5 }
            android.database.Cursor r12 = r1.mCursor     // Catch:{ Exception -> 0x03b5 }
            java.lang.String r13 = "_data"
            int r12 = r12.getColumnIndex(r13)     // Catch:{ Exception -> 0x03b5 }
            java.lang.String r11 = r11.getString(r12)     // Catch:{ Exception -> 0x03b5 }
            java.lang.String r11 = com.sec.android.app.voicenote.provider.StorageProvider.convertToSDCardWritablePath(r11)     // Catch:{ Exception -> 0x03b5 }
            android.database.Cursor r12 = r1.mCursor     // Catch:{ Exception -> 0x03b5 }
            android.database.Cursor r13 = r1.mCursor     // Catch:{ Exception -> 0x03b5 }
            java.lang.String r14 = "_id"
            int r13 = r13.getColumnIndex(r14)     // Catch:{ Exception -> 0x03b5 }
            long r12 = r12.getLong(r13)     // Catch:{ Exception -> 0x03b5 }
            android.database.Cursor r14 = r1.mCursor     // Catch:{ Exception -> 0x03b5 }
            android.database.Cursor r15 = r1.mCursor     // Catch:{ Exception -> 0x03b5 }
            r25 = r10
            java.lang.String r10 = "year_name"
            int r10 = r15.getColumnIndex(r10)     // Catch:{ Exception -> 0x03b5 }
            java.lang.String r10 = r14.getString(r10)     // Catch:{ Exception -> 0x03b5 }
            android.database.Cursor r14 = r1.mCursor     // Catch:{ Exception -> 0x03b5 }
            android.database.Cursor r15 = r1.mCursor     // Catch:{ Exception -> 0x03b5 }
            r16 = r10
            java.lang.String r10 = "recording_mode"
            int r10 = r15.getColumnIndex(r10)     // Catch:{ Exception -> 0x03b5 }
            int r10 = r14.getInt(r10)     // Catch:{ Exception -> 0x03b5 }
            r14 = 0
            int r17 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r17 > 0) goto L_0x00f5
            r3 = 1000(0x3e8, double:4.94E-321)
            android.media.MediaMetadataRetriever r5 = new android.media.MediaMetadataRetriever     // Catch:{ Exception -> 0x00d4, all -> 0x00d1 }
            r5.<init>()     // Catch:{ Exception -> 0x00d4, all -> 0x00d1 }
            r5.setDataSource(r11)     // Catch:{ Exception -> 0x00cf }
            r0 = 1026(0x402, float:1.438E-42)
            java.lang.String r0 = r5.extractMetadata(r0)     // Catch:{ Exception -> 0x00cf }
            long r8 = java.lang.Long.parseLong(r0)     // Catch:{ Exception -> 0x00cf }
            r10 = 0
            int r0 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r0 > 0) goto L_0x00c1
            long r8 = r6 * r3
        L_0x00c1:
            r18 = r8
            com.sec.android.app.voicenote.provider.DBProvider r14 = com.sec.android.app.voicenote.provider.DBProvider.getInstance()     // Catch:{ Exception -> 0x00cf }
            android.content.Context r15 = r1.mContext     // Catch:{ Exception -> 0x00cf }
            r16 = r12
            r14.updateDateTakenInMediaDB(r15, r16, r18)     // Catch:{ Exception -> 0x00cf }
            goto L_0x00ea
        L_0x00cf:
            r0 = move-exception
            goto L_0x00d6
        L_0x00d1:
            r0 = move-exception
            r5 = 0
            goto L_0x00ef
        L_0x00d4:
            r0 = move-exception
            r5 = 0
        L_0x00d6:
            java.lang.String r8 = TAG     // Catch:{ all -> 0x00ee }
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r8, (java.lang.String) r2, (java.lang.Throwable) r0)     // Catch:{ all -> 0x00ee }
            com.sec.android.app.voicenote.provider.DBProvider r14 = com.sec.android.app.voicenote.provider.DBProvider.getInstance()     // Catch:{ all -> 0x00ee }
            android.content.Context r15 = r1.mContext     // Catch:{ all -> 0x00ee }
            long r18 = r6 * r3
            r16 = r12
            r14.updateDateTakenInMediaDB(r15, r16, r18)     // Catch:{ all -> 0x00ee }
            if (r5 == 0) goto L_0x00ed
        L_0x00ea:
            r5.release()
        L_0x00ed:
            return
        L_0x00ee:
            r0 = move-exception
        L_0x00ef:
            if (r5 == 0) goto L_0x00f4
            r5.release()
        L_0x00f4:
            throw r0
        L_0x00f5:
            java.lang.String r2 = r1.stringForTime(r8)
            java.lang.String r6 = r1.getMediumDateFormat(r4)
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>(r3)
            android.widget.TextView r15 = r0.titleView
            r15.setText(r3)
            android.widget.TextView r15 = r0.titleView
            android.content.Context r14 = r1.mContext
            android.content.res.Resources r14 = r14.getResources()
            r18 = r11
            r11 = 2131099779(0x7f060083, float:1.781192E38)
            r19 = r10
            r10 = 0
            int r11 = r14.getColor(r11, r10)
            r15.setTextColor(r11)
            android.widget.TextView r10 = r0.titleView
            r10.setContentDescription(r7)
            android.widget.TextView r10 = r0.dateView
            r10.setText(r6)
            android.widget.TextView r6 = r0.dateView
            java.lang.String r4 = r1.getMediumDateFormat(r4)
            r6.setContentDescription(r4)
            android.widget.TextView r4 = r0.durationView
            r4.setText(r2)
            android.widget.TextView r4 = r0.durationView
            java.lang.StringBuilder r5 = r1.getDurationContentDescription(r8)
            r4.setContentDescription(r5)
            android.database.Cursor r4 = r1.mCursor
            boolean r4 = r4.isLast()
            r5 = 8
            r6 = 0
            if (r4 == 0) goto L_0x0150
            android.widget.ImageView r4 = r0.divider
            r4.setVisibility(r5)
            goto L_0x0155
        L_0x0150:
            android.widget.ImageView r4 = r0.divider
            r4.setVisibility(r6)
        L_0x0155:
            java.lang.String r4 = "audio/amr"
            r8 = r25
            boolean r4 = r4.equals(r8)
            if (r4 == 0) goto L_0x0165
            android.widget.TextView r4 = r0.mmsView
            r4.setVisibility(r6)
            goto L_0x016a
        L_0x0165:
            android.widget.TextView r4 = r0.mmsView
            r4.setVisibility(r5)
        L_0x016a:
            com.sec.android.app.voicenote.service.Engine r4 = com.sec.android.app.voicenote.service.Engine.getInstance()
            int r4 = r4.getPlayerState()
            r8 = 7
            r9 = 2131099780(0x7f060084, float:1.7811923E38)
            r10 = 2131099771(0x7f06007b, float:1.7811905E38)
            java.lang.String r11 = " / "
            r15 = 3
            r5 = 4
            if (r4 == r15) goto L_0x0238
            if (r4 == r5) goto L_0x018d
            r23.setNormalItem(r24)
            android.widget.TextView r2 = r0.titleView
            android.content.Context r4 = r1.mContext
            r1.setTitleViewColor(r2, r3, r4)
            goto L_0x02c8
        L_0x018d:
            com.sec.android.app.voicenote.service.Engine r4 = com.sec.android.app.voicenote.service.Engine.getInstance()
            long r20 = r4.getID()
            int r4 = (r20 > r12 ? 1 : (r20 == r12 ? 0 : -1))
            if (r4 != 0) goto L_0x0229
            int r3 = r1.mScene
            if (r3 == r15) goto L_0x019f
            if (r3 != r8) goto L_0x0213
        L_0x019f:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            int r4 = r1.mCurrentPosition
            long r14 = (long) r4
            java.lang.String r4 = r1.stringForTime(r14)
            r3.append(r4)
            r3.append(r11)
            r3.append(r2)
            java.lang.String r2 = r3.toString()
            android.widget.SeekBar r3 = r0.seekbar
            r3.setVisibility(r6)
            android.content.Context r3 = r1.mContext
            android.content.res.Resources r3 = r3.getResources()
            r4 = 0
            int r3 = r3.getColor(r10, r4)
            android.content.res.ColorStateList r3 = r1.colorToColorStateList(r3)
            android.widget.SeekBar r4 = r0.seekbar
            r8 = 1
            r4.semSetFluidEnabled(r8)
            android.widget.SeekBar r4 = r0.seekbar
            r4.setThumbTintList(r3)
            android.widget.SeekBar r3 = r0.seekbar
            int r4 = r1.mDuration
            r3.setMax(r4)
            android.widget.SeekBar r3 = r0.seekbar
            int r4 = r1.mCurrentPosition
            r3.setProgress(r4)
            android.widget.SeekBar r3 = r0.seekbar
            com.sec.android.app.voicenote.ui.adapter.ListAdapter$1 r4 = new com.sec.android.app.voicenote.ui.adapter.ListAdapter$1
            r4.<init>()
            r3.setOnSeekBarChangeListener(r4)
            android.widget.TextView r3 = r0.positionView
            r3.setVisibility(r6)
            android.widget.TextView r3 = r0.positionView
            r3.setText(r2)
            android.widget.TextView r2 = r0.durationView
            r2.setVisibility(r5)
            android.widget.TextView r2 = r0.dateView
            r2.setVisibility(r5)
            android.widget.TextView r2 = r0.titleView
            android.content.Context r3 = r1.mContext
            android.content.res.Resources r3 = r3.getResources()
            r4 = 0
            int r3 = r3.getColor(r9, r4)
            r2.setTextColor(r3)
        L_0x0213:
            boolean r2 = r1.isListItemSelected
            if (r2 == 0) goto L_0x0233
            boolean r2 = r1.isFocusOnFirstPart
            if (r2 == 0) goto L_0x0233
            android.widget.ImageButton r2 = r0.pauseIcon
            boolean r2 = r2.isPressed()
            if (r2 == 0) goto L_0x0233
            android.widget.ImageButton r2 = r0.pauseIcon
            r2.setPressed(r6)
            goto L_0x0233
        L_0x0229:
            r23.setNormalItem(r24)
            android.widget.TextView r2 = r0.titleView
            android.content.Context r4 = r1.mContext
            r1.setTitleViewColor(r2, r3, r4)
        L_0x0233:
            r1.changePlayerIcon(r5, r0)
            goto L_0x02c8
        L_0x0238:
            com.sec.android.app.voicenote.service.Engine r4 = com.sec.android.app.voicenote.service.Engine.getInstance()
            long r21 = r4.getID()
            int r4 = (r21 > r12 ? 1 : (r21 == r12 ? 0 : -1))
            if (r4 != 0) goto L_0x02be
            int r3 = r1.mScene
            if (r3 == r15) goto L_0x024a
            if (r3 != r8) goto L_0x02ba
        L_0x024a:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            int r4 = r1.mCurrentPosition
            long r9 = (long) r4
            java.lang.String r4 = r1.stringForTime(r9)
            r3.append(r4)
            r3.append(r11)
            r3.append(r2)
            java.lang.String r2 = r3.toString()
            android.widget.SeekBar r3 = r0.seekbar
            r3.setVisibility(r6)
            android.content.Context r3 = r1.mContext
            android.content.res.Resources r3 = r3.getResources()
            r4 = 2131099771(0x7f06007b, float:1.7811905E38)
            r8 = 0
            int r3 = r3.getColor(r4, r8)
            android.content.res.ColorStateList r3 = r1.colorToColorStateList(r3)
            android.widget.SeekBar r4 = r0.seekbar
            r4.setThumbTintList(r3)
            android.widget.SeekBar r3 = r0.seekbar
            int r4 = r1.mDuration
            r3.setMax(r4)
            android.widget.SeekBar r3 = r0.seekbar
            int r4 = r1.mCurrentPosition
            r3.setProgress(r4)
            android.widget.TextView r3 = r0.positionView
            r3.setVisibility(r6)
            android.widget.TextView r3 = r0.positionView
            r3.setText(r2)
            android.widget.TextView r2 = r0.dateView
            r2.setVisibility(r5)
            android.widget.TextView r2 = r0.durationView
            r2.setVisibility(r5)
            android.widget.TextView r2 = r0.titleView
            android.content.Context r3 = r1.mContext
            android.content.res.Resources r3 = r3.getResources()
            r4 = 2131099780(0x7f060084, float:1.7811923E38)
            r8 = 0
            int r3 = r3.getColor(r4, r8)
            r2.setTextColor(r3)
            android.widget.SeekBar r2 = r0.seekbar
            r3 = 1
            r1.setProgressHoverWindow(r2, r3)
        L_0x02ba:
            r1.changePlayerIcon(r15, r0)
            goto L_0x02c8
        L_0x02be:
            r23.setNormalItem(r24)
            android.widget.TextView r2 = r0.titleView
            android.content.Context r4 = r1.mContext
            r1.setTitleViewColor(r2, r3, r4)
        L_0x02c8:
            android.widget.ImageView r2 = r0.nfcIcon
            if (r2 == 0) goto L_0x02ea
            if (r16 == 0) goto L_0x02e3
            java.lang.String r2 = "NFC"
            r3 = r16
            boolean r2 = r3.contains(r2)
            if (r2 == 0) goto L_0x02e3
            android.widget.ImageView r2 = r0.nfcIcon
            r2.setVisibility(r6)
            java.lang.String r2 = ", NFC"
            r7.append(r2)
            goto L_0x02ea
        L_0x02e3:
            android.widget.ImageView r2 = r0.nfcIcon
            r3 = 8
            r2.setVisibility(r3)
        L_0x02ea:
            java.lang.String r2 = ", "
            r3 = r19
            if (r3 != r5) goto L_0x0309
            android.widget.ImageView r3 = r0.memoIcon
            r3.setVisibility(r6)
            r7.append(r2)
            android.content.Context r3 = r1.mContext
            android.content.res.Resources r3 = r3.getResources()
            r4 = 2131755079(0x7f100047, float:1.9141027E38)
            java.lang.String r3 = r3.getString(r4)
            r7.append(r3)
            goto L_0x0310
        L_0x0309:
            android.widget.ImageView r3 = r0.memoIcon
            r4 = 8
            r3.setVisibility(r4)
        L_0x0310:
            com.sec.android.app.voicenote.service.BookmarkHolder r3 = com.sec.android.app.voicenote.service.BookmarkHolder.getInstance()
            r4 = r18
            boolean r3 = r3.get(r4)
            if (r3 == 0) goto L_0x0335
            android.widget.ImageView r3 = r0.bookmarkIcon
            r3.setVisibility(r6)
            r7.append(r2)
            android.content.Context r3 = r1.mContext
            android.content.res.Resources r3 = r3.getResources()
            r5 = 2131755059(0x7f100033, float:1.9140987E38)
            java.lang.String r3 = r3.getString(r5)
            r7.append(r3)
            goto L_0x033c
        L_0x0335:
            android.widget.ImageView r3 = r0.bookmarkIcon
            r5 = 8
            r3.setVisibility(r5)
        L_0x033c:
            java.lang.String r3 = com.sec.android.app.voicenote.provider.StorageProvider.getExternalStorageStateSd()
            if (r3 == 0) goto L_0x0370
            java.lang.String r5 = "mounted"
            boolean r3 = r3.equals(r5)
            if (r3 == 0) goto L_0x0370
            r3 = 1
            java.lang.String r3 = com.sec.android.app.voicenote.provider.StorageProvider.getRootPath(r3)
            boolean r3 = r4.startsWith(r3)
            if (r3 == 0) goto L_0x0370
            android.widget.ImageView r3 = r0.sdCardIcon
            r3.setVisibility(r6)
            r7.append(r2)
            android.content.Context r2 = r1.mContext
            android.content.res.Resources r2 = r2.getResources()
            r3 = 2131755325(0x7f10013d, float:1.9141526E38)
            java.lang.String r2 = r2.getString(r3)
            r7.append(r2)
            r3 = 8
            goto L_0x0377
        L_0x0370:
            android.widget.ImageView r2 = r0.sdCardIcon
            r3 = 8
            r2.setVisibility(r3)
        L_0x0377:
            boolean r2 = r1.mIsSelectionMode
            if (r2 == 0) goto L_0x039b
            android.widget.FrameLayout r2 = r0.playPauseIcon
            r2.setVisibility(r3)
            android.widget.CheckBox r2 = r0.checkBox
            r4 = 1065353216(0x3f800000, float:1.0)
            r2.setAlpha(r4)
            android.widget.CheckBox r2 = r0.checkBox
            boolean r4 = com.sec.android.app.voicenote.provider.CheckedItemProvider.isChecked(r12)
            r2.setChecked(r4)
            android.widget.ImageButton r2 = r0.playIcon
            r2.setVisibility(r3)
            android.widget.ImageButton r2 = r0.pauseIcon
            r2.setVisibility(r3)
            goto L_0x03ab
        L_0x039b:
            android.widget.CheckBox r2 = r0.checkBox
            r2.setChecked(r6)
            android.widget.CheckBox r2 = r0.checkBox
            r3 = 0
            r2.setAlpha(r3)
            android.widget.FrameLayout r2 = r0.playPauseIcon
            r2.setVisibility(r6)
        L_0x03ab:
            android.view.View r0 = r0.itemView
            boolean r2 = com.sec.android.app.voicenote.provider.CheckedItemProvider.isChecked(r12)
            r0.setActivated(r2)
            return
        L_0x03b5:
            r0 = move-exception
            java.lang.String r3 = TAG
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r3, (java.lang.String) r2, (java.lang.Throwable) r0)
            return
        L_0x03bc:
            java.lang.String r0 = TAG
            java.lang.String r2 = "bindView - cursor index is zero or closed"
            com.sec.android.app.voicenote.provider.Log.m22e(r0, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.adapter.ListAdapter.onBindViewHolder(com.sec.android.app.voicenote.ui.adapter.ListAdapter$ViewHolder, int):void");
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
        } catch (IllegalStateException unused) {
            Log.m26i(TAG, "getItemId cursor is not initiazlied. Return 0");
            return 0;
        } catch (IndexOutOfBoundsException unused2) {
            String str = TAG;
            Log.m22e(str, "getItemId index error count : " + this.mCursor.getCount() + " position : " + i);
            return 0;
        } catch (Exception e) {
            Log.m22e(TAG, e.toString());
            return 0;
        }
    }

    public int getItemCount() {
        Cursor cursor = this.mCursor;
        if (cursor != null && !cursor.isClosed()) {
            return this.mCursor.getCount();
        }
        Log.m32w(TAG, "getItemCount - cursor is closed");
        return 0;
    }

    public int getPosition(String str) {
        Cursor cursor = this.mCursor;
        if (cursor == null || cursor.isClosed() || str == null || str.isEmpty()) {
            Log.m22e(TAG, "getItemId cursor is invalid or abnormal position. Return 0");
            return -1;
        }
        this.mCursor.moveToFirst();
        do {
            Cursor cursor2 = this.mCursor;
            if (str.compareToIgnoreCase(cursor2.getString(cursor2.getColumnIndex("title"))) == 0) {
                return this.mCursor.getPosition();
            }
            this.mCursor.moveToNext();
        } while (!this.mCursor.isAfterLast());
        return -1;
    }

    public void setListItemSelected(boolean z, boolean z2) {
        this.isListItemSelected = z;
        this.isFocusOnFirstPart = z2;
    }

    public void setSelectionMode(boolean z) {
        this.mIsSelectionMode = z;
        if (!z) {
            CheckedItemProvider.initCheckedList();
        }
    }

    public void changePlayerIcon(int i, ViewHolder viewHolder) {
        if (i == 3) {
            viewHolder.playIcon.setVisibility(8);
            viewHolder.pauseIcon.setVisibility(0);
        } else if (i == 4) {
            viewHolder.pauseIcon.setVisibility(8);
            viewHolder.playIcon.setVisibility(0);
        }
    }

    /* access modifiers changed from: protected */
    public void setNormalItem(ViewHolder viewHolder) {
        viewHolder.seekbar.setVisibility(8);
        viewHolder.positionView.setVisibility(8);
        viewHolder.dateView.setVisibility(0);
        viewHolder.durationView.setVisibility(0);
        changePlayerIcon(4, viewHolder);
    }

    /* access modifiers changed from: protected */
    public String stringForTime(long j) {
        long round = (long) (Math.round(((float) j) / 10.0f) / 100);
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", new Object[]{Integer.valueOf((int) (round / 3600)), Integer.valueOf((int) ((round / 60) % 60)), Integer.valueOf((int) (round % 60))});
    }

    private StringBuilder getDurationContentDescription(long j) {
        StringBuilder sb = new StringBuilder();
        long round = (long) (Math.round(((float) j) / 10.0f) / 100);
        int i = (int) (round % 60);
        int i2 = (int) ((round / 60) % 60);
        if (((int) (round / 3600)) > 0) {
            sb.append(stringForTime(j));
        } else if (i2 > 0) {
            sb.append(this.mContext.getResources().getQuantityString(C0690R.plurals.timer_min, i2, new Object[]{Integer.valueOf(i2)}));
            sb.append(this.mContext.getResources().getQuantityString(C0690R.plurals.timer_sec, i, new Object[]{Integer.valueOf(i)}));
        } else {
            sb.append(this.mContext.getResources().getQuantityString(C0690R.plurals.timer_sec, i, new Object[]{Integer.valueOf(i)}));
        }
        return sb;
    }

    /* access modifiers changed from: protected */
    public String getMediumDateFormat(long j) {
        SimpleDateFormat simpleDateFormat;
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(j);
        if (instance.get(1) < this.mCurrentYear) {
            if (Locale.KOREAN.getLanguage().equals(this.mCurrentLanguage)) {
                simpleDateFormat = new SimpleDateFormat("yyyy'년' MMM d'일'");
            } else if (Locale.JAPANESE.getLanguage().equals(this.mCurrentLanguage) || Locale.CHINA.getLanguage().equals(this.mCurrentLanguage) || Locale.CHINESE.getLanguage().equals(this.mCurrentLanguage) || Locale.TAIWAN.getLanguage().equals(this.mCurrentLanguage) || Locale.SIMPLIFIED_CHINESE.getLanguage().equals(this.mCurrentLanguage)) {
                simpleDateFormat = new SimpleDateFormat("yyyy'年' MMM d'日'");
            } else if (Locale.GERMAN.getLanguage().equals(this.mCurrentLanguage) || Locale.GERMANY.getLanguage().equals(this.mCurrentLanguage)) {
                simpleDateFormat = new SimpleDateFormat("d'.' MMM yyyy");
            } else {
                simpleDateFormat = new SimpleDateFormat("d MMM yyyy");
            }
        } else if (instance.get(5) == this.mCurrentDate && instance.get(2) == this.mCurrentMonth) {
            if (this.mTimeFormat == 24) {
                simpleDateFormat = new SimpleDateFormat("HH:mm");
            } else if (Locale.KOREAN.getLanguage().equals(this.mCurrentLanguage) || Locale.CHINA.getLanguage().equals(this.mCurrentLanguage) || Locale.CHINESE.getLanguage().equals(this.mCurrentLanguage) || Locale.TAIWAN.getLanguage().equals(this.mCurrentLanguage) || Locale.SIMPLIFIED_CHINESE.getLanguage().equals(this.mCurrentLanguage)) {
                simpleDateFormat = new SimpleDateFormat("a h:mm");
            } else if (Locale.JAPANESE.getLanguage().equals(this.mCurrentLanguage)) {
                simpleDateFormat = new SimpleDateFormat("a K:mm");
            } else {
                simpleDateFormat = new SimpleDateFormat("h:mm a");
            }
        } else if (Locale.KOREAN.getLanguage().equals(this.mCurrentLanguage)) {
            simpleDateFormat = new SimpleDateFormat("MMM d'일'");
        } else if (Locale.CHINA.getLanguage().equals(this.mCurrentLanguage) || Locale.CHINESE.getLanguage().equals(this.mCurrentLanguage) || Locale.TAIWAN.getLanguage().equals(this.mCurrentLanguage) || Locale.SIMPLIFIED_CHINESE.getLanguage().equals(this.mCurrentLanguage)) {
            simpleDateFormat = new SimpleDateFormat("MMM d'日'");
        } else if (Locale.JAPANESE.getLanguage().equals(this.mCurrentLanguage)) {
            simpleDateFormat = new SimpleDateFormat("MMMd'日'");
        } else if (Locale.GERMAN.getLanguage().equals(this.mCurrentLanguage) || Locale.GERMANY.getLanguage().equals(this.mCurrentLanguage)) {
            simpleDateFormat = new SimpleDateFormat("d'.' MMM");
        } else {
            simpleDateFormat = new SimpleDateFormat("d MMM");
        }
        StringBuilder sb = new StringBuilder(simpleDateFormat.format(instance.getTime()));
        String language = Locale.getDefault().getLanguage();
        if ("ar".equals(language) || "fa".equals(language) || "ur".equals(language) || "iw".equals(language)) {
            sb.append("‏‎");
        } else {
            sb.append("");
        }
        return sb.toString();
    }

    public void setSeekBarValue(int i, int i2) {
        this.mDuration = i;
        this.mCurrentPosition = i2;
    }

    public final void registerListener(OnItemClickListener onItemClickListener) {
        this.mListener = onItemClickListener;
    }

    public void onSceneChange(int i) {
        this.mScene = i;
    }

    /* access modifiers changed from: protected */
    public void setTitleViewColor(TextView textView, String str, Context context) {
        String trim = CursorProvider.getInstance().getRecordingSearchTag().trim();
        if (!trim.isEmpty()) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
            String[] split = trim.split(" +");
            ArrayList arrayList = new ArrayList();
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(context.getResources().getColor(C0690R.C0691color.listview_search_highlight, (Resources.Theme) null));
            for (String str2 : split) {
                int length = str2.length();
//                char[] semGetPrefixCharForSpan = TextUtils.semGetPrefixCharForSpan(textView.getPaint(), str, str2.toCharArray());
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
//                    int indexOf2 = str.toLowerCase().indexOf(str2.toLowerCase());
//                    while (indexOf2 >= 0 && indexOf2 <= str.length()) {
//                        arrayList.add(Integer.valueOf(indexOf2));
//                        indexOf2 = str.toLowerCase().indexOf(str2.toLowerCase(), indexOf2 + 1);
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
            textView.setText(spannableStringBuilder);
        }
    }

    public void setProgressHoverWindow(SeekBar seekBar, boolean z) {
        if (!VoiceNoteFeature.isSupportHoveringUI()) {
            return;
        }
        if (z) {
//            seekBar.semSetHoverPopupType(3);
//            final SemHoverPopupWindow semGetHoverPopup = seekBar.semGetHoverPopup(true);
//            seekBar.semSetOnSeekBarHoverListener(new SeekBar.SemOnSeekBarHoverListener() {
//                private TextView mTime = null;
//
//                public void onStopTrackingHover(SeekBar seekBar) {
//                }
//
//                @SuppressLint({"InflateParams"})
//                public void onStartTrackingHover(SeekBar seekBar, int i) {
//                    TextView textView;
//                    this.mTime = (TextView) LayoutInflater.from(ListAdapter.this.mContext).inflate(C0690R.layout.hover_window_layout, (ViewGroup) null);
//                    if (semGetHoverPopup != null && (textView = this.mTime) != null) {
//                        textView.setText(convertTimeFormat(i));
//                        semGetHoverPopup.setContent(this.mTime);
//                    }
//                }
//
//                public void onHoverChanged(SeekBar seekBar, int i, boolean z) {
//                    SemHoverPopupWindow semHoverPopupWindow = semGetHoverPopup;
//                    if (semHoverPopupWindow != null && semHoverPopupWindow.getContentView() != null) {
//                        TextView textView = this.mTime;
//                        if (textView == null) {
//                            ((TextView) semGetHoverPopup.getContentView()).setText(convertTimeFormat(i));
//                            return;
//                        }
//                        textView.setText(convertTimeFormat(i));
//                        semGetHoverPopup.setContent(this.mTime);
//                    }
//                }
//
//                private String convertTimeFormat(int i) {
//                    int i2 = i / 1000;
//                    return String.format(Locale.US, "%02d:%02d:%02d", new Object[]{Integer.valueOf(i2 / 3600), Integer.valueOf((i2 / 60) % 60), Integer.valueOf(i2 % 60)});
//                }
//            });
            return;
        }
//        seekBar.semSetOnSeekBarHoverListener((SeekBar.SemOnSeekBarHoverListener) null);
    }

    public ColorStateList colorToColorStateList(int i) {
        return new ColorStateList(new int[][]{new int[0]}, new int[]{i});
    }

    /* renamed from: com.sec.android.app.voicenote.ui.adapter.ListAdapter$ViewHolder */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, View.OnKeyListener {
        ImageView bookmarkIcon;
        CheckBox checkBox;
        TextView dateView;
        ImageView divider;
        TextView durationView;
        public int mRoundMode = 0;
        ImageView memoIcon;
        TextView mmsView;
        ImageView nfcIcon;
        ImageButton pauseIcon;
        ImageButton playIcon;
        FrameLayout playPauseIcon;
        TextView positionView;
        ImageView sdCardIcon;
        SeekBar seekbar;
        TextView titleView;

        ViewHolder(View view) {
            super(view);
            view.setHapticFeedbackEnabled(true);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            view.setOnKeyListener(this);
            this.playPauseIcon = (FrameLayout) view.findViewById(C0690R.C0693id.listrow_play_pause_icon);
            this.titleView = (TextView) view.findViewById(C0690R.C0693id.listrow_title);
            this.positionView = (TextView) view.findViewById(C0690R.C0693id.listrow_position);
            this.dateView = (TextView) view.findViewById(C0690R.C0693id.listrow_date);
            this.mmsView = (TextView) view.findViewById(C0690R.C0693id.listrow_mms_label);
            this.playIcon = (ImageButton) view.findViewById(C0690R.C0693id.listrow_play_icon);
            this.pauseIcon = (ImageButton) view.findViewById(C0690R.C0693id.listrow_pause_icon);
            this.bookmarkIcon = (ImageView) view.findViewById(C0690R.C0693id.listrow_bookmark_label);
            this.memoIcon = (ImageView) view.findViewById(C0690R.C0693id.listrow_voicememo_label);
            this.nfcIcon = (ImageView) view.findViewById(C0690R.C0693id.listrow_nfc_label);
            this.checkBox = (CheckBox) view.findViewById(C0690R.C0693id.listrow_checkbox);
            this.seekbar = (SeekBar) view.findViewById(C0690R.C0693id.listrow_seekbar);
            this.durationView = (TextView) view.findViewById(C0690R.C0693id.listrow_duration);
            this.sdCardIcon = (ImageView) view.findViewById(C0690R.C0693id.listrow_sdCard_label);
            this.divider = (ImageView) view.findViewById(C0690R.C0693id.listrow_divider);
            ImageButton imageButton = this.playIcon;
            if (!(imageButton == null || this.pauseIcon == null || this.playPauseIcon == null)) {
                imageButton.setOnClickListener(this);
                this.playIcon.setFocusable(false);
//                this.playIcon.semSetHoverPopupType(1);
                this.pauseIcon.setOnClickListener(this);
                this.pauseIcon.setFocusable(false);
//                this.pauseIcon.semSetHoverPopupType(1);
                this.playPauseIcon.setOnClickListener(this);
                this.playPauseIcon.setFocusable(false);
            }
            View findViewById = view.findViewById(C0690R.C0693id.listrow_first_part);
            MouseKeyboardProvider.getInstance().mouseClickInteraction(view.findViewById(C0690R.C0693id.listrow_seekbar_layout));
            MouseKeyboardProvider.getInstance().mouseClickInteraction(findViewById);
            MouseKeyboardProvider.getInstance().mouseClickInteraction(this.playIcon);
            MouseKeyboardProvider.getInstance().mouseClickInteraction(this.pauseIcon);
            MouseKeyboardProvider.getInstance().mouseClickInteraction(this.checkBox);
        }

        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            switch (view.getId()) {
                case C0690R.C0693id.listrow_layout:
                    if (ListAdapter.this.mListener != null) {
                        long itemId = ListAdapter.this.getItemId(adapterPosition);
                        ListAdapter.this.mListener.onItemClick(view, adapterPosition, itemId);
                        CheckBox checkBox2 = this.checkBox;
                        if (checkBox2 != null) {
                            checkBox2.setChecked(CheckedItemProvider.isChecked(itemId));
                            return;
                        }
                        return;
                    }
                    return;
                case C0690R.C0693id.listrow_pause_icon:
                case C0690R.C0693id.listrow_play_icon:
                case C0690R.C0693id.listrow_play_pause_icon:
                    if (!PhoneStateProvider.getInstance().isCallIdle(ListAdapter.this.mContext)) {
                        Toast.makeText(ListAdapter.this.mContext, C0690R.string.no_play_during_call, 0).show();
                        return;
                    } else if (ListAdapter.this.mListener != null) {
                        ListAdapter.this.mListener.onHeaderClick(view, adapterPosition);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public boolean onLongClick(View view) {
            if (ListAdapter.this.mListener != null) {
                return ListAdapter.this.mListener.onItemLongClick(view, getAdapterPosition());
            }
            return false;
        }

        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (ListAdapter.this.mListener != null) {
                return ListAdapter.this.mListener.onKey(view, i, keyEvent);
            }
            return false;
        }
    }
}
