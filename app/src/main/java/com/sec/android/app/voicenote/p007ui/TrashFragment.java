package com.sec.android.app.voicenote.p007ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.common.util.TrashHelper;
import com.sec.android.app.voicenote.common.util.p006db.VNDatabase;
import com.sec.android.app.voicenote.data.trash.TrashInfo;
import com.sec.android.app.voicenote.p007ui.adapter.TrashAdapter;
import com.sec.android.app.voicenote.provider.CheckedItemProvider;
import com.sec.android.app.voicenote.provider.ContextMenuProvider;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.MouseKeyboardProvider;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.StorageProvider;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.service.AudioFormat;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.uicore.FragmentController;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.animation.PathInterpolatorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/* renamed from: com.sec.android.app.voicenote.ui.TrashFragment */
public class TrashFragment extends AbsFragment implements FragmentController.OnSceneChangeListener, TrashAdapter.OnItemClickListener, Engine.OnEngineListener, SeekBar.OnSeekBarChangeListener {
    private static final String BUNDLE_AVOID_ANIMATION = "avoid_animation";
    private static final String BUNDLE_PLAYING_STATE = "playing_state";
    private static final int DURATION_THRESHOLD_AMR = 180000;
    private static final int DURATION_THRESHOLD_M4A = 10800000;
    private static final String TAG = "TrashFragment";
    private final int SineInOut33 = 1;
    private final int SineInOut80 = 3;
    private AnimatorSet mAnimationSet = null;
    /* access modifiers changed from: private */
    public BottomNavigationView mBottomNavigationView;
    private Context mContext;
    /* access modifiers changed from: private */
    public int mCurrentScene = 0;
    private Handler mEngineEventHandler = null;
    /* access modifiers changed from: private */
    public int mExpandedPosition = -1;
    private boolean mIsNeedResumePlay = false;
    private int mItemHeight;
    /* access modifiers changed from: private */
    public int mLastPosSelected = -1;
    private RelativeLayout mLayoutTrashEmpty;
    private LinearLayout mLayoutTrashList;
    /* access modifiers changed from: private */
    public TrashAdapter mListAdapter = null;
    private List<TrashInfo> mListViewItems;
    private boolean mNeedToAvoidAnimation = false;
    private boolean mPauseBySeek = false;
    private TrashFragment.PlayTask mPlayTask = null;
    /* access modifiers changed from: private */
    public ProgressBar mProgressBar;
    /* access modifiers changed from: private */
    public RecyclerView mRecyclerView = null;
    private RoundedDecoration mRoundedDecoration;
    /* access modifiers changed from: private */
//    public SeslRoundedCorner mSeslListRoundedCorner;
//    /* access modifiers changed from: private */
//    public SeslRoundedCorner mSeslRoundedCorner;
    private TextView mTvEmptyTrashDescription;
    private View mViewMarginBottom;

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onCreate(Bundle bundle) {
        Log.m26i(TAG, "onCreate");
        super.onCreate(bundle);
        this.mEngineEventHandler = new Handler(new Handler.Callback() {
            public final boolean handleMessage(Message message) {
                return TrashFragment.this.lambda$onCreate$0$TrashFragment(message);
            }
        });
    }

    public /* synthetic */ boolean lambda$onCreate$0$TrashFragment(Message message) {
        View childAt;
        View childAt2;
        if (getActivity() != null && isAdded() && !isRemoving()) {
            switch (message.what) {
                case 2010:
                case 2012:
                    if (Engine.getInstance().getPlayerState() != 1) {
                        int currentPlayingPosition = CursorProvider.getInstance().getCurrentPlayingPosition();
                        PlayTask playTask = this.mPlayTask;
                        if (playTask == null || playTask.mPosition == currentPlayingPosition) {
                            int duration = Engine.getInstance().getDuration();
                            int currentTime = Engine.getInstance().getCurrentTime();
                            this.mListAdapter.setSeekBarValue(duration, currentTime);
                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) this.mRecyclerView.getLayoutManager();
                            int findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                            int findLastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                            if (!(findFirstVisibleItemPosition == -1 || findFirstVisibleItemPosition > currentPlayingPosition || findLastVisibleItemPosition == -1 || findLastVisibleItemPosition < currentPlayingPosition || (childAt = this.mRecyclerView.getChildAt(currentPlayingPosition - findFirstVisibleItemPosition)) == null)) {
                                SeekBar seekBar = (SeekBar) childAt.findViewById(C0690R.C0693id.listrow_seekbar);
                                seekBar.setMax(duration);
                                seekBar.setProgress(currentTime);
                                seekBar.setOnSeekBarChangeListener(this);
                                TextView textView = (TextView) childAt.findViewById(C0690R.C0693id.listrow_position);
                                if (message.what == 2012) {
                                    String str = stringForTime(message.arg1) + " / " + stringForTime(duration);
                                    if (!str.equals(textView.getText())) {
                                        textView.setText(str);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    break;
                case 2011:
                    int duration2 = Engine.getInstance().getDuration();
                    int currentPlayingPosition2 = CursorProvider.getInstance().getCurrentPlayingPosition();
                    this.mListAdapter.setSeekBarValue(Engine.getInstance().getDuration(), 0);
                    LinearLayoutManager linearLayoutManager2 = (LinearLayoutManager) this.mRecyclerView.getLayoutManager();
                    int findFirstVisibleItemPosition2 = linearLayoutManager2.findFirstVisibleItemPosition();
                    int findLastVisibleItemPosition2 = linearLayoutManager2.findLastVisibleItemPosition();
                    if (!(findFirstVisibleItemPosition2 == -1 || findFirstVisibleItemPosition2 > currentPlayingPosition2 || findLastVisibleItemPosition2 == -1 || findLastVisibleItemPosition2 < currentPlayingPosition2 || (childAt2 = this.mRecyclerView.getChildAt(currentPlayingPosition2 - findFirstVisibleItemPosition2)) == null)) {
                        this.mListAdapter.changePlayerIcon(4, (TrashAdapter.ViewHolder) this.mRecyclerView.getChildViewHolder(childAt2));
                        ((TextView) childAt2.findViewById(C0690R.C0693id.listrow_position)).setText(stringForTime(message.arg1) + " / " + stringForTime(duration2));
                        break;
                    }
            }
        }
        return false;
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Log.m26i(TAG, "onCreateView");
        this.mContext = getContext();
        View inflate = LayoutInflater.from(getActivity()).inflate(C0690R.layout.fragment_trash, viewGroup, false);
        start(inflate);
        MouseKeyboardProvider.getInstance().mouseClickInteraction(getActivity(), this, this.mRecyclerView);
        FragmentController.getInstance().registerSceneChangeListener(this);
        if (bundle != null) {
            this.mIsNeedResumePlay = bundle.getBoolean(BUNDLE_PLAYING_STATE);
            Log.m26i(TAG, "mIsNeedResumePlay: " + this.mIsNeedResumePlay);
            if (this.mIsNeedResumePlay) {
                int resumePlay = Engine.getInstance().resumePlay();
                if (resumePlay == -103) {
                    Toast.makeText(getActivity(), C0690R.string.no_play_during_call, 0).show();
                } else if (resumePlay == 0 && this.mCurrentScene == 15) {
                    SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_list), getActivity().getResources().getString(C0690R.string.event_play_on_list));
                    postEvent(Event.TRASH_MINI_PLAY_RESUME);
                }
            }
            this.mIsNeedResumePlay = false;
            this.mNeedToAvoidAnimation = bundle.getBoolean(BUNDLE_AVOID_ANIMATION, false);
        }
        return inflate;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        Engine.getInstance().registerListener(this);
    }

    public void onStart() {
        Log.m26i(TAG, "onstart");
        super.onStart();
        if (CursorProvider.getInstance().getCurrentPlayingPosition() >= 0) {
            PlayTask playTask = this.mPlayTask;
            if (playTask != null && playTask.mPosition == CursorProvider.getInstance().getCurrentPlayingPosition()) {
                return;
            }
            if (CursorProvider.getInstance().getCurrentPlayingPosition() >= 0) {
//                updatePlayTask();
            } else {
                this.mPlayTask = null;
            }
        }
    }

    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        ContextMenuProvider.getInstance().createContextMenu(getActivity(), contextMenu, this.mCurrentScene, this.mRecyclerView, this);
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        ContextMenuProvider.getInstance().contextItemSelected((AppCompatActivity) getActivity(), menuItem, this.mCurrentScene, this);
        return false;
    }

    @Nullable
    public Animation onCreateAnimation(int i, boolean z, int i2) {
        boolean z2 = this.mNeedToAvoidAnimation;
        this.mNeedToAvoidAnimation = false;
        Log.m26i(TAG, "onCreateAnimation: " + z2);
        return z2 ? new Animation() {
        } : super.onCreateAnimation(i, z, i2);
    }

    public void onDestroyView() {
        SeekBar seekBar;
        Log.m26i(TAG, "onDestroyView");
        if (this.mRecyclerView != null) {
            MouseKeyboardProvider.getInstance().destroyMouseClickInteraction(this, this.mRecyclerView);
            if (!(getView() == null || (seekBar = (SeekBar) getView().findViewById(C0690R.C0693id.listrow_seekbar)) == null)) {
//                seekBar.semSetOnSeekBarHoverListener((SeekBar.SemOnSeekBarHoverListener) null);
            }
        }
        Engine.getInstance().unregisterListener(this);
        FragmentController.getInstance().unregisterSceneChangeListener(this);
        ProgressBar progressBar = this.mProgressBar;
        if (progressBar != null) {
            progressBar.setOnTouchListener((View.OnTouchListener) null);
            this.mProgressBar = null;
        }
        super.onDestroyView();
    }

    public void onDestroy() {
        Log.m26i(TAG, "onDestroy");
        this.mEngineEventHandler = null;
        super.onDestroy();
    }

    public void onDetach() {
        Log.m26i(TAG, "onDetach");
        AnimatorSet animatorSet = this.mAnimationSet;
        if (animatorSet != null) {
            if (animatorSet.isRunning()) {
                this.mAnimationSet.cancel();
            }
            this.mAnimationSet = null;
        }
        RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView != null) {
            int childCount = recyclerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                SeekBar seekBar = (SeekBar) this.mRecyclerView.getChildAt(i).findViewById(C0690R.C0693id.listrow_seekbar);
                if (seekBar != null) {
                    TrashAdapter trashAdapter = this.mListAdapter;
                    if (trashAdapter != null) {
                        trashAdapter.setProgressHoverWindow(seekBar, false);
                    }
                    seekBar.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) null);
                }
            }
            this.mRecyclerView.setAdapter((RecyclerView.Adapter) null);
            this.mRecyclerView = null;
        }
        TrashAdapter trashAdapter2 = this.mListAdapter;
        if (trashAdapter2 != null) {
            trashAdapter2.onDestroy();
            this.mListAdapter = null;
        }
        if (this.mContext != null) {
            this.mContext = null;
        }
        super.onDetach();
    }

    public void onResume() {
        Log.m26i(TAG, "onResume");
        TrashHelper.getInstance().checkFileInTrashToDelete();
        super.onResume();
    }

    public void onEngineUpdate(int i, int i2, int i3) {
        Handler handler = this.mEngineEventHandler;
        if (handler != null) {
            handler.sendMessage(handler.obtainMessage(i, i2, i3));
        }
    }

    public void onUpdate(Object obj) {
        int intValue = ((Integer) obj).intValue();
        Log.m26i(TAG, "onUpdate - " + obj);
        TrashAdapter trashAdapter = this.mListAdapter;
        if (trashAdapter == null) {
            Log.m32w(TAG, "onUpdate - ListAdapter is null");
        } else if (intValue != 951) {
            if (intValue != 993) {
                if (intValue == 965 || intValue == 966) {
                    if (Engine.getInstance().getPlayerState() != 1) {
                        Engine.getInstance().stopPlay();
                    }
                    if (this.mCurrentScene != 13) {
                        postEvent(Event.OPEN_TRASH);
                    }
                    updateTrashListView();
                    return;
                } else if (intValue == 3007 || intValue == 3008) {
                    updatePlayPauseIcon(intValue);
                    return;
                } else {
                    switch (intValue) {
                        case Event.DELETE_TRASH_COMPLETE:
                        case Event.RESTORE_COMPLETE:
                            break;
                        case Event.TRASH_DESELECT:
                        case Event.TRASH_DESELECT_ALL:
                        case Event.OPEN_TRASH:
                            trashAdapter.notifyDataSetChanged();
                            this.mExpandedPosition = -1;
                            return;
                        case Event.TRASH_SELECT_ALL:
                            for (int i = 1; i < this.mListViewItems.size(); i++) {
                                CheckedItemProvider.setChecked((long) this.mListViewItems.get(i).getIdFile().intValue(), true);
                            }
                            this.mListAdapter.notifyDataSetChanged();
                            return;
                        case Event.TRASH_SELECT:
                            trashAdapter.setSelectionMode(true);
                            if (TrashHelper.getInstance().getNumberTrashItem(this.mContext) == 1) {
                                for (int i2 = 1; i2 < this.mListViewItems.size(); i2++) {
                                    CheckedItemProvider.setChecked((long) this.mListViewItems.get(i2).getIdFile().intValue(), true);
                                }
                            }
                            this.mListAdapter.notifyDataSetChanged();
//                            this.mRecyclerView.seslStartLongPressMultiSelection();
                            this.mExpandedPosition = -1;
                            return;
                        default:
                            return;
                    }
                }
            }
            updateTrashListView();
        } else {
            scrollRecyclerViewToPosition();
        }
    }

    private void scrollRecyclerViewToPosition() {
        RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView != null) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int findLastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
            int findLastCompletelyVisibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
            int i = this.mLastPosSelected;
            if (findLastVisibleItemPosition == i || findLastCompletelyVisibleItemPosition == i) {
                this.mRecyclerView.scrollToPosition(this.mLastPosSelected);
            }
        }
    }

    private void updatePlayPauseIcon(int i) {
        View childAt;
        int currentPlayingPosition = CursorProvider.getInstance().getCurrentPlayingPosition();
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) this.mRecyclerView.getLayoutManager();
        int findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
        int findLastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
        this.mListAdapter.setSeekBarValue(Engine.getInstance().getDuration(), Engine.getInstance().getCurrentTime());
        if (findFirstVisibleItemPosition != -1 && findFirstVisibleItemPosition <= currentPlayingPosition && findLastVisibleItemPosition != -1 && findLastVisibleItemPosition >= currentPlayingPosition && (childAt = this.mRecyclerView.getChildAt(currentPlayingPosition - findFirstVisibleItemPosition)) != null) {
            if (i == 3007) {
                this.mListAdapter.changePlayerIcon(4, (TrashAdapter.ViewHolder) this.mRecyclerView.getChildViewHolder(childAt));
            } else {
                this.mListAdapter.changePlayerIcon(3, (TrashAdapter.ViewHolder) this.mRecyclerView.getChildViewHolder(childAt));
            }
        }
    }

    private void updateTrashListView() {
        loadData();
        Log.m26i(TAG, "updateTrashListView - update size: " + this.mListViewItems.size());
        this.mListAdapter.notifyDataSetChanged();
        List<TrashInfo> list = this.mListViewItems;
        if (list == null || list.size() <= 1) {
            this.mLayoutTrashList.setVisibility(8);
            this.mLayoutTrashEmpty.setVisibility(0);
        } else {
            if (this.mLayoutTrashList.getVisibility() != 0) {
                this.mLayoutTrashList.setVisibility(0);
            }
            if (this.mLayoutTrashEmpty.getVisibility() != 8) {
                this.mLayoutTrashEmpty.setVisibility(8);
            }
        }
        getActivity().invalidateOptionsMenu();
    }

    public void onPause() {
        Log.m26i(TAG, "onPause TrashFragment");
        super.onPause();
    }

    private String stringForTime(int i) {
        int i2 = i / 1000;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", new Object[]{Integer.valueOf(i2 / 3600), Integer.valueOf((i2 / 60) % 60), Integer.valueOf(i2 % 60)});
    }

    public void start(View view) {
        Log.m26i(TAG, "start");
        this.mListViewItems = new ArrayList();
        loadData();
        init(view);
    }

    private void loadData() {
        List<TrashInfo> list;
        String externalSDStorageFsUuid = StorageProvider.getExternalSDStorageFsUuid(this.mContext);
        new ArrayList();
        if (externalSDStorageFsUuid == null) {
            try {
                list = VNDatabase.getInstance(getContext()).mTrashDao().getAllData();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        } else {
            list = VNDatabase.getInstance(getContext()).mTrashDao().getAllData(externalSDStorageFsUuid.toLowerCase());
        }
        if (list != null) {
            this.mListViewItems.clear();
            this.mListViewItems.addAll(list);
            this.mListViewItems.add(0, (TrashInfo) null);
            Log.m26i(TAG, "Trash size: " + list.size() + " - " + this.mListViewItems.size());
        }
    }

    private void init(View view) {
        Log.m26i(TAG, "init");
        if (this.mListAdapter == null) {
            this.mListAdapter = new TrashAdapter(this.mContext, this.mListViewItems);
            this.mListAdapter.setHasStableIds(true);
        }
        this.mLayoutTrashList = (LinearLayout) view.findViewById(C0690R.C0693id.list_trash_view);
        this.mLayoutTrashEmpty = (RelativeLayout) view.findViewById(C0690R.C0693id.empty_trash_view);
        this.mTvEmptyTrashDescription = (TextView) view.findViewById(C0690R.C0693id.empty_trash_description);
        this.mBottomNavigationView = (BottomNavigationView) getActivity().findViewById(C0690R.C0693id.bottom_navigation);
        this.mViewMarginBottom = view.findViewById(C0690R.C0693id.view_margin_bottom);
        this.mProgressBar = (ProgressBar) view.findViewById(C0690R.C0693id.list_progressbar);
        this.mRecyclerView = (RecyclerView) view.findViewById(C0690R.C0693id.trash_list);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this.mContext));
        this.mRecyclerView.setOnCreateContextMenuListener(this);
        this.mListAdapter.setOnTouchTrashItemListener(this);
        this.mRecyclerView.setAdapter(this.mListAdapter);
        this.mRecyclerView.setNestedScrollingEnabled(false);
        updateLayoutInTabletMultiWindow(getActivity());
//        this.mRecyclerView.seslSetFastScrollerEnabled(true);
//        this.mRecyclerView.seslSetGoToTopEnabled(true);
//        this.mRecyclerView.seslSetGoToTopBottomPadding(getResources().getDimensionPixelOffset(C0690R.dimen.go_to_top_bottom_padding));
//        this.mSeslRoundedCorner = new SeslRoundedCorner(this.mContext);
//        this.mSeslListRoundedCorner = new SeslRoundedCorner(this.mContext);
//        this.mSeslListRoundedCorner.setRoundedCorners(12);
        this.mRoundedDecoration = new RoundedDecoration();
        this.mRecyclerView.addItemDecoration(this.mRoundedDecoration);
        this.mRecyclerView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.mCurrentScene = VoiceNoteApplication.getScene();
        if (this.mCurrentScene == 4) {
            changeListVisibility(false);
        } else {
            changeListVisibility(true);
        }
        List<TrashInfo> list = this.mListViewItems;
        if (list == null || list.size() <= 1) {
            this.mLayoutTrashList.setVisibility(8);
            this.mLayoutTrashEmpty.setVisibility(0);
        }
        this.mTvEmptyTrashDescription.setText(getActivity().getResources().getQuantityString(C0690R.plurals.trash_recordings_description, TrashHelper.getInstance().getKeepInTrashDays(), new Object[]{Integer.valueOf(TrashHelper.getInstance().getKeepInTrashDays())}));
        setPenSelectMode();
        setLongPressMultiSelection();
    }

    private void updateLayoutInTabletMultiWindow(Activity activity) {
        if (VoiceNoteFeature.FLAG_IS_TABLET && this.mLayoutTrashList != null && DisplayManager.isCurrentWindowOnLandscape(activity)) {
            if ((DisplayManager.getMultiwindowMode() == 2 && getResources().getConfiguration().densityDpi < 480) || ((DisplayManager.getMultiwindowMode() != 2 && getResources().getConfiguration().densityDpi >= 480) || (DisplayManager.getMultiwindowMode() == 1 && getResources().getConfiguration().screenWidthDp < 960))) {
                int i = (int) (((double) getResources().getDisplayMetrics().widthPixels) * 0.05d);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -2);
                layoutParams.leftMargin = i;
                layoutParams.rightMargin = i;
                this.mLayoutTrashList.setLayoutParams(layoutParams);
            }
        }
    }

    private void setLongPressMultiSelection() {
//        this.mRecyclerView.seslSetLongPressMultiSelectionListener(new RecyclerView.SeslLongPressMultiSelectionListener() {
//            public void onLongPressMultiSelectionStarted(int i, int i2) {
//            }
//
//            public void onItemSelected(RecyclerView recyclerView, View view, int i, long j) {
//                if (TrashFragment.this.mRecyclerView != null && TrashFragment.this.mListAdapter != null && TrashFragment.this.mCurrentScene == 14) {
//                    CheckedItemProvider.toggle(TrashFragment.this.mListAdapter.getItemId(i));
//                    int unused = TrashFragment.this.mLastPosSelected = i;
//                    CheckBox checkBox = (CheckBox) view.findViewById(C0690R.C0693id.listrow_checkbox);
//                    if (checkBox != null) {
//                        checkBox.setChecked(!checkBox.isChecked());
//                    }
//                    TrashFragment.this.sendEvent(Event.TRASH_UPDATE_CHECKBOX);
//                    TrashFragment.this.notifyDataSetChangedToAdapter();
//                }
//            }
//
//            public void onLongPressMultiSelectionEnded(int i, int i2) {
//                TrashFragment.this.postEvent(Event.ENABLE_MARGIN_BOTTOM_LIST);
//            }
//        });
    }

    private void setPenSelectMode() {
//        this.mRecyclerView.seslSetOnMultiSelectedListener(new RecyclerView.SeslOnMultiSelectedListener() {
//            private int mEndPosition;
//            private int mStartPosition;
//
//            public void onMultiSelected(RecyclerView recyclerView, View view, int i, long j) {
//            }
//
//            public void onMultiSelectStart(int i, int i2) {
//                this.mStartPosition = TrashFragment.this.mRecyclerView.getChildLayoutPosition(TrashFragment.this.mRecyclerView.findChildViewUnder((float) i, (float) i2));
//            }
//
//            public void onMultiSelectStop(int i, int i2) {
//                this.mEndPosition = TrashFragment.this.mRecyclerView.getChildLayoutPosition(TrashFragment.this.mRecyclerView.findChildViewUnder((float) i, (float) i2));
//                if (this.mStartPosition != -1 || this.mEndPosition != -1 || i2 < 0) {
//                    if (this.mStartPosition == -1) {
//                        this.mStartPosition = TrashFragment.this.mListAdapter.getItemCount() - 1;
//                    }
//                    if (this.mEndPosition == -1) {
//                        if (i2 < 0) {
//                            this.mEndPosition = 0;
//                        } else {
//                            this.mEndPosition = ((LinearLayoutManager) TrashFragment.this.mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
//                        }
//                    }
//                    int i3 = this.mStartPosition;
//                    int i4 = this.mEndPosition;
//                    if (i3 > i4) {
//                        i3 = i4;
//                    }
//                    int i5 = this.mStartPosition;
//                    int i6 = this.mEndPosition;
//                    if (i5 <= i6) {
//                        i5 = i6;
//                    }
//                    if (i5 >= 0) {
//                        if (i3 < 0) {
//                            i3 = 0;
//                        }
//                        Settings.getIntSettings(Settings.KEY_LIST_MODE, 0);
//                        if (TrashFragment.this.mCurrentScene != 14) {
//                            CheckedItemProvider.initCheckedList();
//                            TrashFragment.this.postEvent(Event.TRASH_SELECT);
//                        }
//                        if (Engine.getInstance().getPlayerState() != 1) {
//                            Engine.getInstance().stopPlay();
//                        }
//                        while (i3 <= i5) {
//                            CheckedItemProvider.toggle(TrashFragment.this.mListAdapter.getItemId(i3));
//                            i3++;
//                        }
//                        TrashFragment.this.notifyDataSetChangedToAdapter();
//                        TrashFragment.this.postEvent(Event.TRASH_SELECT);
//                        if (TrashFragment.this.mBottomNavigationView != null && TrashFragment.this.mBottomNavigationView.getVisibility() == 8) {
//                            TrashFragment.this.postEvent(Event.SHOW_BOTTOM_NAVIGATION_BAR);
//                        }
//                    }
//                }
//            }
//        });
    }

    public void onSceneChange(int i) {
        Log.m26i(TAG, "onSceneChange - scene : " + i);
        this.mCurrentScene = i;
        TrashAdapter trashAdapter = this.mListAdapter;
        if (trashAdapter != null) {
            trashAdapter.setSelectionMode(this.mCurrentScene == 14);
            this.mListAdapter.notifyDataSetChanged();
        }
        if (this.mCurrentScene == 14 && CheckedItemProvider.getCheckedItemCount() > 0) {
            postEvent(Event.SHOW_BOTTOM_NAVIGATION_BAR);
        }
        int i2 = this.mCurrentScene;
        if (i2 == 14 || i2 == 15) {
            this.mViewMarginBottom.setVisibility(8);
        } else {
            this.mViewMarginBottom.setVisibility(0);
        }
        if (this.mRecyclerView == null) {
            return;
        }
        if (this.mCurrentScene == 4) {
            changeListVisibility(false);
        } else {
            changeListVisibility(true);
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        Log.m26i(TAG, "onSaveInstanceState");
        bundle.putBoolean(BUNDLE_PLAYING_STATE, this.mIsNeedResumePlay);
        bundle.putBoolean(BUNDLE_AVOID_ANIMATION, true);
        super.onSaveInstanceState(bundle);
    }

    public void onStop() {
        Log.m26i(TAG, "onStop");
        super.onStop();
        if (Engine.getInstance().getPlayerState() == 3) {
            Engine.getInstance().pausePlay();
            SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_list_miniplayer), getActivity().getResources().getString(C0690R.string.event_pause_on_list));
            this.mIsNeedResumePlay = true;
        }
    }

    private void startPlayTask(boolean z, int i, String str, long j) {
        postEvent(Event.BLOCK_CONTROL_BUTTONS);
        if (str == null) {
            Log.m22e(TAG, str + " is not valid. file not found");
            return;
        }
        if ((str.endsWith(AudioFormat.ExtType.EXT_AMR) && j > 180000) || (str.endsWith(AudioFormat.ExtType.EXT_M4A) && j > 10800000)) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                recyclerView.setAlpha(0.5f);
                this.mRecyclerView.setEnabled(false);
            }
            ProgressBar progressBar = this.mProgressBar;
            if (progressBar != null) {
                progressBar.setVisibility(0);
            }
        }
        PlayTask playTask = this.mPlayTask;
        if (playTask != null && playTask.getStatus() == AsyncTask.Status.RUNNING) {
            this.mPlayTask.cancel(false);
        }
        AnimatorSet animatorSet = this.mAnimationSet;
        if (animatorSet != null) {
            if (animatorSet.isRunning()) {
                this.mAnimationSet.cancel();
            }
            this.mAnimationSet = null;
        }
        this.mPlayTask = new TrashFragment.PlayTask(z, i, str, j);
        this.mPlayTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public boolean onHeaderClick(View view, int i) {
        RecyclerView recyclerView;
        long itemId = this.mListAdapter.getItemId(i);
        Log.m26i(TAG, "onHeaderClick  - position : " + i + " id : " + itemId);
        PlayTask playTask = this.mPlayTask;
        if ((playTask != null && playTask.isRunning()) || ((recyclerView = this.mRecyclerView) != null && recyclerView.getVisibility() != 0)) {
            Log.m26i(TAG, "onHeaderClick PlayTask is running or mRecyclerView is not visible, return this operation");
            return false;
        } else if (Engine.getInstance().getRecorderState() != 1) {
            Log.m26i(TAG, "onHeaderClick - recorder is not idle, recorderState : " + Engine.getInstance().getRecorderState());
            return false;
        } else if (this.mListAdapter == null || i == 0) {
            Log.m22e(TAG, "onHeaderClick - mListAdapter is null");
            return false;
        } else if (this.mCurrentScene == 14) {
            int checkedItemCount = CheckedItemProvider.getCheckedItemCount();
            CheckedItemProvider.toggle((long) this.mListViewItems.get(i).getIdFile().intValue());
//            updateSelectionItem(view, CheckedItemProvider.isChecked((long) this.mListViewItems.get(i).getIdFile().intValue()));
            needUpdateMenu(checkedItemCount, CheckedItemProvider.getCheckedItemCount());
            return true;
        } else {
            if (!Engine.getInstance().getPath().equals(this.mListViewItems.get(i).getPath())) {
                startPlayTask(true, i, this.mListViewItems.get(i).getPath(), this.mListViewItems.get(i).getDuration());
            } else if (Engine.getInstance().getPlayerState() == 3) {
                Engine.getInstance().pausePlay();
                SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_list_miniplayer), getActivity().getResources().getString(C0690R.string.event_pause_on_list));
                if (this.mCurrentScene == 15) {
                    postEvent(Event.TRASH_MINI_PLAY_PAUSE);
                }
            } else {
                int resumePlay = Engine.getInstance().resumePlay();
                if (resumePlay == -103) {
                    Toast.makeText(getActivity(), C0690R.string.no_play_during_call, 0).show();
                } else if (resumePlay == 0 && this.mCurrentScene == 15) {
                    SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_list), getActivity().getResources().getString(C0690R.string.event_play_on_list));
                    postEvent(Event.TRASH_MINI_PLAY_RESUME);
                }
            }
            return true;
        }
    }

    public void onItemClick(View view, int i) {
        RecyclerView recyclerView;
        Log.m26i(TAG, "onItemClick - position: " + i);
        if (i > 0) {
            if (this.mCurrentScene == 14) {
                int checkedItemCount = CheckedItemProvider.getCheckedItemCount();
                CheckedItemProvider.toggle((long) this.mListViewItems.get(i).getIdFile().intValue());
                this.mLastPosSelected = i;
//                updateSelectionItem(view, CheckedItemProvider.isChecked((long) this.mListViewItems.get(i).getIdFile().intValue()));
                needUpdateMenu(checkedItemCount, CheckedItemProvider.getCheckedItemCount());
                return;
            }
            Engine.getInstance().getPlayerState();
            if (this.mPlayTask == null || !((Engine.getInstance().getPlayerState() == 3 || Engine.getInstance().getPlayerState() == 4) && i == this.mPlayTask.mPosition)) {
                PlayTask playTask = this.mPlayTask;
                if ((playTask != null && playTask.isRunning()) || ((recyclerView = this.mRecyclerView) != null && recyclerView.getVisibility() != 0)) {
                    Log.m26i(TAG, "onHeaderClick PlayTask is running or mRecyclerView is not visible, return this operation");
                } else if (Engine.getInstance().getRecorderState() != 1) {
                    Log.m26i(TAG, "onHeaderClick - recorder is not idle, recorderState : " + Engine.getInstance().getRecorderState());
                } else {
                    startPlayTask(true, i, this.mListViewItems.get(i).getPath(), this.mListViewItems.get(i).getDuration());
                    view.sendAccessibilityEvent(65536);
                }
            }
        }
    }

    private void needUpdateMenu(int i, int i2) {
        if (i2 == 0 || (i == 0 && i2 == 1)) {
            getActivity().invalidateOptionsMenu();
        }
        sendEvent(Event.TRASH_UPDATE_CHECKBOX);
    }

    public boolean onItemLongClick(View view, int i) {
        Log.m29v(TAG, "onItemLongClick - position : " + i);
        if (i == 0) {
            return false;
        }
        if (Engine.getInstance().getPlayerState() != 1) {
            Engine.getInstance().stopPlay();
        }
        this.mLastPosSelected = i;
        long intValue = (long) this.mListViewItems.get(i).getIdFile().intValue();
        if (!CheckedItemProvider.isChecked(intValue)) {
            CheckedItemProvider.toggle(intValue);
            if (this.mCurrentScene == 14) {
//                updateSelectionItem(view, CheckedItemProvider.isChecked(intValue));
            }
            if (CheckedItemProvider.getCheckedItemCount() == 1) {
                getActivity().invalidateOptionsMenu();
            }
            sendEvent(Event.TRASH_UPDATE_CHECKBOX);
        }
        if (this.mCurrentScene != 14) {
            sendEvent(Event.TRASH_SELECT);
        }
//        this.mRecyclerView.seslStartLongPressMultiSelection();
        return true;
    }

    private void changeListVisibility(boolean z) {
        Log.m26i(TAG, "changeListVisibility : " + z);
        if (getActivity() == null || getActivity().getWindow() == null) {
            Log.m22e(TAG, "changeListVisibility getActivity or getWindow return null");
        } else if (z) {
            this.mRecyclerView.setVisibility(0);
            WindowManager.LayoutParams attributes = getActivity().getWindow().getAttributes();
//            attributes.semAddExtensionFlags(1);
            getActivity().getWindow().setAttributes(attributes);
        } else {
            this.mRecyclerView.setVisibility(4);
            this.mViewMarginBottom.setVisibility(8);
            WindowManager.LayoutParams attributes2 = getActivity().getWindow().getAttributes();
//            attributes2.semAddExtensionFlags(0);
            getActivity().getWindow().setAttributes(attributes2);
        }
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        if (z) {
            Engine.getInstance().seekTo(seekBar.getProgress());
        }
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        if (this.mPauseBySeek) {
            int resumePlay = Engine.getInstance().resumePlay();
            if (resumePlay == -103) {
                Toast.makeText(getActivity(), C0690R.string.no_play_during_call, 0).show();
            } else if (resumePlay == 0) {
                this.mPauseBySeek = false;
            }
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.TrashFragment$RoundedDecoration */
    private class RoundedDecoration extends RecyclerView.ItemDecoration {
        private RoundedDecoration() {
        }

        public void seslOnDispatchDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
            RecyclerView.ViewHolder childViewHolder;
            //            super.seslOnDispatchDraw(canvas, recyclerView, state);
            //            if (recyclerView != null) {
            //                int childCount = recyclerView.getChildCount();
            //                int i = 0;
            //                while (i < childCount) {
            //                    View childAt = recyclerView.getChildAt(i);
            //                    if (childAt != null && (childViewHolder = recyclerView.getChildViewHolder(childAt)) != null) {
            //                        if (childViewHolder instanceof TrashAdapter.ViewHolder) {
            //                            if (((TrashAdapter.ViewHolder) childViewHolder).mRoundMode != 3) {
            //                                TrashFragment.this.mSeslRoundedCorner.setRoundedCorners(0);
            //                                TrashFragment.this.mSeslRoundedCorner.drawRoundedCorner(childAt, canvas);
            //                            } else {
            //                                TrashFragment.this.mSeslRoundedCorner.setRoundedCorners(3);
            //                                TrashFragment.this.mSeslRoundedCorner.drawRoundedCorner(childAt, canvas);
            //                            }
            //                        }
            //                        i++;
            //                    } else {
            //                        return;
            //                    }
            //                }
            //                View childAt2 = recyclerView.getChildAt(0);
            //                if (childAt2 != null) {
            //                    if (recyclerView.getChildViewHolder(childAt2) instanceof TrashAdapter.DescriptionViewHolder) {
            //                        TrashFragment.this.mSeslListRoundedCorner.setRoundedCorners(12);
            //                    } else {
            //                        TrashFragment.this.mSeslListRoundedCorner.setRoundedCorners(15);
            //                    }
            //                    TrashFragment.this.mSeslListRoundedCorner.drawRoundedCorner(canvas);
            //                }
            //            }
            //        }
        }
    }

        /* renamed from: com.sec.android.app.voicenote.ui.TrashFragment$PlayTask */
        class PlayTask extends AsyncTask<Void, Integer, Boolean> {
            private long mDuration;
            private boolean mIsNeedScroll;
            private final boolean mMini;
            private final String mPath;
            private boolean mPlayResult;
            /* access modifiers changed from: private */
            public int mPosition;
            private int mTaskState;
            private final Object syncObj;

            /* renamed from: com.sec.android.app.voicenote.ui.TrashFragment$PlayTask$TaskState */
            private class TaskState {
                private static final int FINISH = 2;
                private static final int INIT = 0;
                private static final int RUNNING = 1;

                private TaskState() {
                }
            }

            private PlayTask(boolean z, int i, String str, long j) {
                this.syncObj = new Object();
                Log.m26i(TrashFragment.TAG, "init play task: " + i);
                this.mMini = z;
                this.mPath = str;
                this.mDuration = j;
                this.mPosition = i;
                this.mTaskState = 0;
            }

            /* access modifiers changed from: protected */
            public Boolean doInBackground(Void... voidArr) {
                this.mTaskState = 1;
                this.mPlayResult = startPlay(this.mMini, this.mPath, this.mDuration);
                return true;
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Boolean bool) {
                super.onPostExecute(bool);
                Log.m26i(TrashFragment.TAG, "onPostExecute");
                if(TrashFragment.this.mProgressBar != null) {
                    TrashFragment.this.mProgressBar.setVisibility(8);
                }
                if(TrashFragment.this.mRecyclerView != null) {
                    TrashFragment.this.mRecyclerView.setEnabled(true);
                    TrashFragment.this.mRecyclerView.setAlpha(1.0f);
                }
                if(!this.mPlayResult || !this.mMini) {
                    int unused = TrashFragment.this.mExpandedPosition = -1;
                }
                else {
                    synchronized(this.syncObj) {
                        if(TrashFragment.this.mRecyclerView != null) {
                            if(TrashFragment.this.mListAdapter != null) {
                                setExpandListAnimation(this.mPosition);
                            }
                        }
                        this.mTaskState = 2;
                        return;
                    }
                }
                this.mTaskState = 2;
            }

            /* access modifiers changed from: package-private */
            public boolean isRunning() {
                return this.mTaskState == 1;
            }

            /* access modifiers changed from: package-private */
            public void updatePosition(int i) {
                this.mPosition = i;
            }
        }

        /* access modifiers changed from: private */
        public void setExpandListAnimation(int i) {
            Log.m19d(TAG, "setExpandListAnimation position = " + i + " mExpandedPosition = " + mExpandedPosition);
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            int findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            int findLastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
            Log.m19d(TAG, "setExpandListAnimation firstVisiblePosition = " + findFirstVisibleItemPosition + " lastVisiblePosition = " + findLastVisibleItemPosition);
            if(findFirstVisibleItemPosition == -1 || findFirstVisibleItemPosition > i || findLastVisibleItemPosition == -1 || i > findLastVisibleItemPosition) {
                notifyDataSetChangedToAdapter();
                int i2 = mExpandedPosition;
                if(i2 <= i) {
                    linearLayoutManager.scrollToPositionWithOffset(i2, 0);
                }
                else
                    if(i == 0) {
                        doAnimation(mRecyclerView.getChildAt(0));
                    }
                    else {
                        linearLayoutManager.scrollToPositionWithOffset(i, 0);
                    }
                updateExpandListValue();
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("setExpandListAnimation expandListHeight : ");
            int i3 = i - findFirstVisibleItemPosition;
            sb.append(i3);
            Log.m19d(TAG, sb.toString());
            View childAt = mRecyclerView.getChildAt(i3);
            if(childAt != null) {
                mItemHeight = childAt.getHeight();
                expandListHeight(childAt);
                doAnimation(childAt);
                int i4 = mExpandedPosition;
                if(findFirstVisibleItemPosition > i4 || i4 > findLastVisibleItemPosition) {
                    mListAdapter.notifyItemChanged(mExpandedPosition);
                    return;
                }

                shrinkListHeight(mRecyclerView.getChildAt(mExpandedPosition - findFirstVisibleItemPosition));
            }
        }

        private void updateSelectionItem(View view, boolean z) {
            view.setActivated(z);
            ((CheckBox) view.findViewById(C0690R.C0693id.listrow_checkbox)).setChecked(z);
        }

        /* access modifiers changed from: private */
        public void notifyDataSetChangedToAdapter() {
            if(mListAdapter == null) {
                Log.m22e(TAG, "notifyDataSetChangedToAdapter adapter is null");
                return;
            }
            terminateExpandListAnimation();
            mListAdapter.notifyDataSetChanged();
        }

        private void doAnimation(View view) {
            mAnimationSet = new AnimatorSet();
            ArrayList arrayList = new ArrayList();
            RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(C0690R.C0693id.main_row);
            final ImageButton imageButton = (ImageButton) view.findViewById(C0690R.C0693id.listrow_play_icon);
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(imageButton, View.ALPHA, new float[]{1.0f, 0.0f});
            ofFloat.setDuration(200);
            setInterpolator(ofFloat, 1);
            arrayList.add(ofFloat);
            ImageButton imageButton2 = (ImageButton) view.findViewById(C0690R.C0693id.listrow_pause_icon);
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(imageButton2, View.ALPHA, new float[]{0.0f, 1.0f});
            ofFloat2.setDuration(200);
            setInterpolator(ofFloat2, 1);
            arrayList.add(ofFloat2);
            arrayList.add(ObjectAnimator.ofArgb((TextView) view.findViewById(C0690R.C0693id.listrow_title), "textColor", new int[]{getResources().getColor(C0690R.C0691color.listview_title_normal, (Resources.Theme) null), getResources().getColor(C0690R.C0691color.listview_title_play, (Resources.Theme) null)}).setDuration(200));
            TextView textView = (TextView) view.findViewById(C0690R.C0693id.listrow_date);
            textView.setVisibility(8);
            arrayList.add(ObjectAnimator.ofFloat(textView, View.ALPHA, new float[]{1.0f, 0.0f}));
            TextView textView2 = (TextView) view.findViewById(C0690R.C0693id.listrow_duration);
            arrayList.add(ObjectAnimator.ofFloat(textView2, View.ALPHA, new float[]{1.0f, 0.0f}));
            final TextView textView3 = (TextView) view.findViewById(C0690R.C0693id.listrow_position);
            arrayList.add(ObjectAnimator.ofFloat(view.findViewById(C0690R.C0693id.listrow_position), View.ALPHA, new float[]{0.0f, 1.0f}));
            SeekBar seekBar = (SeekBar) view.findViewById(C0690R.C0693id.listrow_seekbar);
            ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(seekBar, View.ALPHA, new float[]{0.0f, 1.0f});
            ofFloat3.setDuration(300);
            setInterpolator(ofFloat3, 1);
            arrayList.add(ofFloat3);
            mListAdapter.setProgressHoverWindow(seekBar, true);
            final ImageButton imageButton3 = imageButton2;
            final SeekBar seekBar2 = seekBar;
            final TextView textView4 = textView;
            final TextView textView5 = textView2;
            final ArrayList arrayList2 = arrayList;
            mAnimationSet.addListener(new Animator.AnimatorListener() {
                public void onAnimationRepeat(Animator animator) {
                }

                public void onAnimationStart(Animator animator) {
                    TrashFragment.this.mRecyclerView.setEnabled(false);
                    imageButton3.setVisibility(0);
                    textView3.setVisibility(0);
                    seekBar2.setThumbTintList(TrashFragment.this.mListAdapter.colorToColorStateList(TrashFragment.this.getResources().getColor(C0690R.C0691color.listrow_seekbar_fg_color, (Resources.Theme) null)));
                    imageButton.setEnabled(false);
                    imageButton3.setEnabled(false);
                }

                public void onAnimationEnd(Animator animator) {
                    Log.m19d(TrashFragment.TAG, "doAnimation onAnimationEnd");
                    imageButton3.setVisibility(0);
                    textView3.setVisibility(0);
                    seekBar2.setVisibility(0);
                    textView4.setAlpha(1.0f);
                    textView5.setAlpha(1.0f);
                    textView5.setVisibility(4);
                    seekBar2.setAlpha(1.0f);
                    imageButton.setAlpha(1.0f);
                    imageButton.setEnabled(true);
                    imageButton.setVisibility(8);
                    imageButton3.setAlpha(1.0f);
                    imageButton3.setEnabled(true);
                    textView3.setAlpha(1.0f);
                    arrayList2.clear();
                    updateExpandListValue();
                    if(TrashFragment.this.mRecyclerView != null) {
                        TrashFragment.this.mRecyclerView.setEnabled(true);
                    }
                    TrashFragment.this.postEvent(Event.TRASH_MINI_PLAY_START);
                }

                public void onAnimationCancel(Animator animator) {
                    Log.m26i(TrashFragment.TAG, "onAnimationCancel");
                    updateExpandListValue();
                    if(TrashFragment.this.mRecyclerView != null) {
                        TrashFragment.this.mRecyclerView.setEnabled(true);
                    }
                }
            });
            mAnimationSet.playTogether(arrayList);
            mAnimationSet.start();
        }

        private void shrinkListHeight(final View view) {
            if(view == null) {
                Log.m19d(TAG, "shrinkListHeight item is null");
                return;
            }
            final int height = view.getHeight();
            final int i = mItemHeight;
            if(height > i) {
                Animation r2 = new Animation() {
                    public boolean willChangeBounds() {
                        return true;
                    }

                    /* access modifiers changed from: protected */
                    public void applyTransformation(float f, Transformation transformation) {
                        int i = 0;
                        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                        if(f >= 1.0f) {
                            i = i;
                        }
                        else {
                            int i2 = height;
                            i = (int) (((float) i2) + (((float) (i - i2)) * f));
                        }
                        layoutParams.height = i;
                        view.requestLayout();
                        view.findViewById(C0690R.C0693id.listrow_seekbar).setVisibility(8);
                    }
                };
                r2.setDuration(300);
                setInterpolator((Animation) r2, 3);
                r2.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationRepeat(Animation animation) {
                    }

                    public void onAnimationStart(Animation animation) {
                        view.findViewById(C0690R.C0693id.listrow_position).setVisibility(8);
                        view.findViewById(C0690R.C0693id.listrow_duration).setVisibility(0);
                        view.findViewById(C0690R.C0693id.listrow_date).setVisibility(0);
                        ((TextView) view.findViewById(C0690R.C0693id.listrow_title)).setTextColor(TrashFragment.this.getResources().getColor(C0690R.C0691color.listview_title_normal, (Resources.Theme) null));
                        if(TrashFragment.this.mListAdapter != null) {
                            TrashFragment.this.mListAdapter.changePlayerIcon(4, (TrashAdapter.ViewHolder) TrashFragment.this.mRecyclerView.getChildViewHolder(view));
                        }
                    }

                    public void onAnimationEnd(Animation animation) {
                        view.getLayoutParams().height = -2;
                        view.requestLayout();
                    }
                });
                view.startAnimation(r2);
            }
        }

        private void expandListHeight(final View view) {
            if(view == null) {
                Log.m19d(TAG, "expandListHeight item is null");
                return;
            }
            final int height = view.getHeight();
            final SeekBar seekBar = (SeekBar) view.findViewById(C0690R.C0693id.listrow_seekbar);
            final int dimensionPixelSize = height + getResources().getDimensionPixelSize(C0690R.dimen.listrow_seekbar_height);
            final int dimensionPixelSize2 = getResources().getDimensionPixelSize(C0690R.dimen.listrow_miniplay_min_height);
            final View view2 = view;
            Animation r1 = new Animation() {
                public boolean willChangeBounds() {
                    return true;
                }

                /* access modifiers changed from: protected */
                public void applyTransformation(float f, Transformation transformation) {
                    int i;
                    if(f >= 1.0f) {
                        i = dimensionPixelSize;
                    }
                    else {
                        int i2 = height;
                        i = (int) (((float) i2) + (((float) (dimensionPixelSize - i2)) * f));
                    }
                    view2.getLayoutParams().height = i;
                    view2.requestLayout();
                    if(i >= dimensionPixelSize2 && seekBar.getVisibility() != 0) {
                        seekBar.setVisibility(0);
                    }
                }
            };
            r1.setDuration(300);
            setInterpolator((Animation) r1, 3);
            r1.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    animation.cancel();
                    view.clearAnimation();
                    view.getLayoutParams().height = -2;
                    view.requestLayout();
                }
            });
            view.startAnimation(r1);
        }

        /* access modifiers changed from: private */
        public void updateExpandListValue() {
            PlayTask playTask = mPlayTask;
            if(playTask == null) {
                Log.m22e(TAG, "updateExpandListValue mPlayTask is null");
                return;
            }
            mExpandedPosition = playTask.mPosition;

        }

        private void terminateExpandListAnimation() {
            AnimatorSet animatorSet = mAnimationSet;
            if(animatorSet != null) {
                if(animatorSet.isRunning()) {
                    mAnimationSet.cancel();
                }
                if(mAnimationSet.getChildAnimations() != null) {
                    mAnimationSet.getChildAnimations().clear();
                }
            }
        }

        private void setInterpolator(ObjectAnimator objectAnimator, int i) {
            if(i == 1) {
                objectAnimator.setInterpolator(PathInterpolatorCompat.create(0.33f, 0.0f, 0.67f, 1.0f));
            }
            else
                if(i != 3) {
                    objectAnimator.setInterpolator(PathInterpolatorCompat.create(0.33f, 0.0f, 0.83f, 0.83f));
                }
                else {
                    objectAnimator.setInterpolator(PathInterpolatorCompat.create(0.33f, 0.0f, 0.2f, 1.0f));
                }
        }

        private void setInterpolator(Animation animation, int i) {
            if(i == 1) {
                animation.setInterpolator(PathInterpolatorCompat.create(0.33f, 0.0f, 0.67f, 1.0f));
            }
            else
                if(i != 3) {
                    animation.setInterpolator(PathInterpolatorCompat.create(0.33f, 0.0f, 0.83f, 0.83f));
                }
                else {
                    animation.setInterpolator(PathInterpolatorCompat.create(0.33f, 0.0f, 0.2f, 1.0f));
                }
        }

        private void updatePlayTask() {
            boolean z = mCurrentScene == 3;
            Engine.getInstance().getID();
            mPlayTask = new PlayTask(z, CursorProvider.getInstance().getCurrentPlayingPosition(), Engine.getInstance().getPath(), (long) Engine.getInstance().getDuration());
        }

        /* access modifiers changed from: private */
        public boolean startPlay(boolean z, String str, long j) {
            Log.m26i(TAG, "startPlay - mini : " + z + " path : " + str + " duration : " + j);
            Engine.getInstance().clearContentItem();
            int startPlay = Engine.getInstance().startPlay(str);
            if(z) {
                SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_PLAY_TYPE, 1);
            }
            else {
                SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_PLAY_TYPE, -1);
            }
            if(Settings.getBooleanSettings(Settings.KEY_SPEAKERPHONE_MODE, false)) {
                SurveyLogProvider.insertStatusLog(SurveyLogProvider.SURVEY_PLAY_VIA_PRIVATE, (String) null, 1000);
            }
            else {
                SurveyLogProvider.insertStatusLog(SurveyLogProvider.SURVEY_PLAY_VIA_PRIVATE, (String) null, 0);
            }
            if(startPlay == -119 || startPlay == -115 || startPlay == -103) {
                postEventDelayed(Event.UNBLOCK_CONTROL_BUTTONS, 100);
                return false;
            }
            else
                if(startPlay != 0) {
                    postEventDelayed(Event.UNBLOCK_CONTROL_BUTTONS, 100);
                    return false;
                }
                else {
                    //            CursorProvider.getInstance().setCurrentPlayingItemPosition(this.mPlayTask.mPosition);
                    if(z) {
                        return true;
                    }
                    postEvent(Event.PLAY_START);
                    postEvent(Event.UPDATE_FILE_NAME);
                    return true;
                }
        }
    }

