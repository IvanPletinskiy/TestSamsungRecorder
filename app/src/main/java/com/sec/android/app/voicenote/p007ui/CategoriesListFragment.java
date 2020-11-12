package com.sec.android.app.voicenote.p007ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.activity.ManageCategoriesActivity;
import com.sec.android.app.voicenote.common.util.CategoryRepository;
import com.sec.android.app.voicenote.common.util.DataRepository;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.main.VNMainActivity;
import com.sec.android.app.voicenote.p007ui.adapter.CategoryListAdapter;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.provider.ContextMenuProvider;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.DBProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.MouseKeyboardProvider;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.uicore.FragmentController;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.util.ArrayList;
import java.util.Locale;

/* renamed from: com.sec.android.app.voicenote.ui.CategoriesListFragment */
public class CategoriesListFragment extends AbsFragment implements FragmentController.OnSceneChangeListener, CategoryRepository.OnUpdateDBCategory, DialogFactory.DialogResultListener, CursorProvider.OnCategoryCursorChangeListener, CategoryListAdapter.OnTouchCategoryItemListener {
    private static final int REFRESH_CATEGORY_LIST = 1;
    private static final String TAG = "CategoriesListFragment";
    /* access modifiers changed from: private */
    public CategoryListAdapter mCategoryAdapter = null;
    private LinearLayout mCategoryListLayout;
    private boolean mIsNeedScrollToEnd = false;
    /* access modifiers changed from: private */
    public RecyclerView mRecyclerView;
    private int mRefreshCount = 0;
    private Handler mRefreshHandler = null;
    private RoundedDecoration mRoundedDecoration;
    private int mScene = 2;
    /* access modifiers changed from: private */
//    public SeslRoundedCorner mSeslListRoundedCorner;
    private View mViewMarginBottom;
    private View mViewParent = null;

    /* JADX WARNING: type inference failed for: r3v6, types: [long[], java.io.Serializable] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0102  */
    /* JADX WARNING: Removed duplicated region for block: B:47:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onUpdate(java.lang.Object r7) {
        /*
            r6 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "onUpdate : "
            r0.append(r1)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "CategoriesListFragment"
            com.sec.android.app.voicenote.provider.Log.m19d(r1, r0)
            java.lang.Integer r7 = (java.lang.Integer) r7
            int r7 = r7.intValue()
            com.sec.android.app.voicenote.ui.adapter.CategoryListAdapter r0 = r6.mCategoryAdapter
            if (r0 != 0) goto L_0x0026
            java.lang.String r7 = "onUpdate - mListAdapter is null"
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r7)
            return
        L_0x0026:
            r1 = 7
            if (r7 == r1) goto L_0x00f2
            r1 = 957(0x3bd, float:1.341E-42)
            if (r7 == r1) goto L_0x00f2
            r1 = 960(0x3c0, float:1.345E-42)
            if (r7 == r1) goto L_0x00ee
            r1 = 964(0x3c4, float:1.351E-42)
            if (r7 == r1) goto L_0x00df
            r1 = 989(0x3dd, float:1.386E-42)
            if (r7 == r1) goto L_0x00f2
            r1 = 993(0x3e1, float:1.391E-42)
            if (r7 == r1) goto L_0x00d0
            r0 = 6007(0x1777, float:8.418E-42)
            if (r7 == r0) goto L_0x00c4
            r0 = 0
            switch(r7) {
                case 40994: goto L_0x0099;
                case 40995: goto L_0x0078;
                case 40996: goto L_0x004c;
                case 40997: goto L_0x0047;
                default: goto L_0x0045;
            }
        L_0x0045:
            goto L_0x00fc
        L_0x0047:
            r6.contextualMenuRename()
            goto L_0x00fc
        L_0x004c:
            androidx.fragment.app.FragmentManager r1 = r6.getFragmentManager()
            java.lang.String r2 = "DeleteCategoryDialog"
            boolean r1 = com.sec.android.app.voicenote.p007ui.dialog.DialogFactory.isDialogVisible(r1, r2)
            if (r1 != 0) goto L_0x00fc
            android.os.Bundle r1 = new android.os.Bundle
            r1.<init>()
            r3 = 1
            long[] r3 = new long[r3]
            com.sec.android.app.voicenote.provider.ContextMenuProvider r4 = com.sec.android.app.voicenote.provider.ContextMenuProvider.getInstance()
            long r4 = r4.getCategoryId()
            r3[r0] = r4
            java.lang.String r0 = "ids"
            r1.putSerializable(r0, r3)
            androidx.fragment.app.FragmentManager r0 = r6.getFragmentManager()
            com.sec.android.app.voicenote.p007ui.dialog.DialogFactory.show(r0, r2, r1)
            goto L_0x00fc
        L_0x0078:
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
        L_0x007d:
            com.sec.android.app.voicenote.ui.adapter.CategoryListAdapter r2 = r6.mCategoryAdapter
            int r2 = r2.getCount()
            if (r0 >= r2) goto L_0x0095
            com.sec.android.app.voicenote.ui.adapter.CategoryListAdapter r2 = r6.mCategoryAdapter
            long r2 = r2.getID(r0)
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            r1.add(r2)
            int r0 = r0 + 1
            goto L_0x007d
        L_0x0095:
            r6.shortCutSelect(r1)
            goto L_0x00fc
        L_0x0099:
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            com.sec.android.app.voicenote.provider.MouseKeyboardProvider r2 = com.sec.android.app.voicenote.provider.MouseKeyboardProvider.getInstance()
            int r2 = r2.getTouchPosition()
            com.sec.android.app.voicenote.ui.adapter.CategoryListAdapter r3 = r6.mCategoryAdapter
            int r3 = r3.getCount()
            if (r2 >= r3) goto L_0x00fc
        L_0x00ae:
            if (r0 > r2) goto L_0x00c0
            com.sec.android.app.voicenote.ui.adapter.CategoryListAdapter r3 = r6.mCategoryAdapter
            long r3 = r3.getID(r0)
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            r1.add(r3)
            int r0 = r0 + 1
            goto L_0x00ae
        L_0x00c0:
            r6.shortCutSelect(r1)
            goto L_0x00fc
        L_0x00c4:
            com.sec.android.app.voicenote.provider.CursorProvider r0 = com.sec.android.app.voicenote.provider.CursorProvider.getInstance()
            androidx.loader.app.LoaderManager r1 = r6.getLoaderManager()
            r0.reloadCategory(r1)
            goto L_0x00fc
        L_0x00d0:
            r0.notifyDataSetChanged()
            com.sec.android.app.voicenote.provider.CursorProvider r0 = com.sec.android.app.voicenote.provider.CursorProvider.getInstance()
            androidx.loader.app.LoaderManager r1 = r6.getLoaderManager()
            r0.reloadCategory(r1)
            goto L_0x00fc
        L_0x00df:
            com.sec.android.app.voicenote.provider.ContextMenuProvider r0 = com.sec.android.app.voicenote.provider.ContextMenuProvider.getInstance()
            long r0 = r0.getCategoryId()
            int r0 = (int) r0
            if (r0 < 0) goto L_0x00fc
            r6.deleteCategory(r0)
            goto L_0x00fc
        L_0x00ee:
            r0.notifyDataSetChanged()
            goto L_0x00fc
        L_0x00f2:
            com.sec.android.app.voicenote.ui.adapter.CategoryListAdapter r0 = r6.mCategoryAdapter
            r0.notifyDataSetInvalidated()
            com.sec.android.app.voicenote.ui.adapter.CategoryListAdapter r0 = r6.mCategoryAdapter
            r0.notifyDataSetChanged()
        L_0x00fc:
            boolean r0 = com.sec.android.app.voicenote.provider.Event.isConvertibleEvent(r7)
            if (r0 == 0) goto L_0x0104
            r6.mCurrentEvent = r7
        L_0x0104:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.CategoriesListFragment.onUpdate(java.lang.Object):void");
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(false);
        this.mRefreshHandler = new Handler(new Handler.Callback() {
            public final boolean handleMessage(Message message) {
                return CategoriesListFragment.this.lambda$onCreate$0$CategoriesListFragment(message);
            }
        });
        if (bundle == null && getFragmentManager().findFragmentByTag(DialogFactory.RENAME_DIALOG) == null) {
            DialogFactory.setDialogResultListener(getFragmentManager(), DialogFactory.CATEGORY_RENAME, this);
        }
        Log.m26i(TAG, "onCreate");
    }

    public /* synthetic */ boolean lambda$onCreate$0$CategoriesListFragment(Message message) {
        CategoryListAdapter categoryListAdapter;
        if (getActivity() != null && isAdded() && !isRemoving()) {
            Log.m26i(TAG, "handleMessage " + message.what);
            this.mRefreshHandler.removeMessages(message.what);
            if (message.what == 1 && (categoryListAdapter = this.mCategoryAdapter) != null) {
                this.mRefreshCount = 0;
                categoryListAdapter.notifyDataSetChanged();
                CursorProvider.getInstance().reloadCategory(getLoaderManager());
            }
        }
        return false;
    }

    public void onViewCreated(View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (DataRepository.getInstance().getCategoryRepository().isChildList()) {
            VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.ENTER_CATEGORY));
        }
    }

    @SuppressLint({"InflateParams"})
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Log.m26i(TAG, "onCreateView");
        FragmentActivity activity = getActivity();
        this.mViewParent = layoutInflater.inflate(C0690R.layout.fragment_category_list, viewGroup, false);
        this.mViewMarginBottom = this.mViewParent.findViewById(C0690R.C0693id.view_margin_bottom);
        updateLayoutInTabletMultiWindow(activity, this.mViewParent);
        this.mRecyclerView = (RecyclerView) this.mViewParent.findViewById(C0690R.C0693id.category_list);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DataRepository.getInstance().getCategoryRepository().loadLabelToMap();
        DataRepository.getInstance().getCategoryRepository().registerUpdateCategoryListener(this);
        this.mRecyclerView.setVisibility(0);
        if (this.mCategoryAdapter == null) {
            this.mCategoryAdapter = new CategoryListAdapter(getContext(), (Cursor) null);
            this.mCategoryAdapter.setOnTouchCategoryItemListener(this);
            this.mRecyclerView.setAdapter(this.mCategoryAdapter);
        }
        if (bundle == null) {
            CursorProvider.getInstance().registerCategoryCursorChangeListener(this);
        }
        CursorProvider.getInstance().loadCategory(getLoaderManager());
        this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                super.onScrollStateChanged(recyclerView, i);
                if (i == 1) {
                    CategoriesListFragment.this.postEvent(Event.MINIMIZE_SIP);
                }
            }
        });
//        this.mRecyclerView.seslSetGoToTopEnabled(true);
//        this.mRecyclerView.seslSetGoToTopBottomPadding(getResources().getDimensionPixelOffset(C0690R.dimen.go_to_top_bottom_padding));
//        this.mRecyclerView.seslSetGoToTopBottomPadding(getResources().getDimensionPixelOffset(C0690R.dimen.go_to_top_bottom_padding));
//        this.mSeslListRoundedCorner = new SeslRoundedCorner(getContext());
//        this.mSeslListRoundedCorner.setRoundedCorners(15);
        this.mRoundedDecoration = new RoundedDecoration();
        this.mRecyclerView.addItemDecoration(this.mRoundedDecoration);
        setPenSelectMode();
        this.mRecyclerView.scrollTo(0, 0);
        setScrollbarPosition();
//        this.mRecyclerView.seslSetFastScrollerEnabled(true);
        this.mScene = VoiceNoteApplication.getScene();
        int i = this.mScene;
        if (i == 4 || i == 6) {
            changeListVisibility(false);
        } else {
            changeListVisibility(true);
        }
        FragmentController.getInstance().registerSceneChangeListener(this);
        MouseKeyboardProvider.getInstance().mouseClickInteraction(activity, this, this.mViewParent);
        MouseKeyboardProvider.getInstance().mouseClickInteraction(activity, this, this.mRecyclerView);
        this.mRecyclerView.setOnKeyListener(new View.OnKeyListener() {
            public final boolean onKey(View view, int i, KeyEvent keyEvent) {
                return CategoriesListFragment.this.lambda$onCreateView$1$CategoriesListFragment(view, i, keyEvent);
            }
        });
        return this.mViewParent;
    }

    public /* synthetic */ boolean lambda$onCreateView$1$CategoriesListFragment(View view, int i, KeyEvent keyEvent) {
        return keyEvent.getKeyCode() == 21 && this.mScene == 7;
    }

    private void updateLayoutInTabletMultiWindow(Activity activity, View view) {
        this.mCategoryListLayout = (LinearLayout) view.findViewById(C0690R.C0693id.layout_list_category);
        if (VoiceNoteFeature.FLAG_IS_TABLET && this.mCategoryListLayout != null && DisplayManager.isCurrentWindowOnLandscape(activity)) {
            if ((DisplayManager.getMultiwindowMode() == 2 && getResources().getConfiguration().densityDpi < 480) || ((DisplayManager.getMultiwindowMode() != 2 && getResources().getConfiguration().densityDpi >= 480) || (DisplayManager.getMultiwindowMode() == 1 && getResources().getConfiguration().screenWidthDp < 960))) {
                int i = (int) (((double) getResources().getDisplayMetrics().widthPixels) * 0.05d);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -2);
                layoutParams.leftMargin = i;
                layoutParams.rightMargin = i;
                this.mCategoryListLayout.setLayoutParams(layoutParams);
            }
        }
    }

    public void onDialogResult(DialogFragment dialogFragment, Bundle bundle) {
        if (bundle != null) {
            int i = bundle.getInt(DialogFactory.BUNDLE_REQUEST_CODE);
            int i2 = bundle.getInt("result_code");
            String string = bundle.getString(DialogFactory.BUNDLE_NAME);
            Log.m26i(TAG, "onDialogResult - requestCode : " + i + " result : " + i2);
            if (i == 1) {
                DataRepository.getInstance().getCategoryRepository().updateColumn((int) ContextMenuProvider.getInstance().getCategoryId(), string);
                ContextMenuProvider.getInstance().setCategoryId(-1);
            } else if (i == 13) {
                DataRepository.getInstance().getCategoryRepository().insertColumn(string, CursorProvider.getInstance().getMaxCategoryPos() + 1);
                this.mIsNeedScrollToEnd = true;
            }
        }
    }

    public void onStart() {
        super.onStart();
        Log.m26i(TAG, "onStart");
        Cursor cursor = this.mCategoryAdapter.getCursor();
        if (cursor != null && cursor.isClosed()) {
            Log.m32w(TAG, "onStart - cursor is closed !!");
            this.mCategoryAdapter.swapCursor((Cursor) null);
            CursorProvider.getInstance().reloadCategory(getLoaderManager());
        }
    }

    public void onDestroy() {
        Log.m26i(TAG, "onDestroy");
        this.mRefreshHandler = null;
        changeListVisibility(false);
        DataRepository.getInstance().getCategoryRepository().unregisterUpdateCategoryListener();
        super.onDestroy();
    }

    public void onDestroyView() {
        Log.m26i(TAG, "onDestroyView");
        MouseKeyboardProvider.getInstance().destroyMouseClickInteraction(this, this.mViewParent);
        MouseKeyboardProvider.getInstance().destroyMouseClickInteraction(this, this.mRecyclerView);
        FragmentController.getInstance().unregisterSceneChangeListener(this);
        CursorProvider.getInstance().unregisterCategoryCursorChangeListener(this);
        super.onDestroyView();
        this.mViewParent = null;
    }

    private void setPenSelectMode() {
        RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView != null) {
//            recyclerView.seslSetOnMultiSelectedListener(new RecyclerView.SeslOnMultiSelectedListener() {
//                private int mEndPosition;
//                private int mStartPosition;
//
//                public void onMultiSelected(RecyclerView recyclerView, View view, int i, long j) {
//                }
//
//                public void onMultiSelectStart(int i, int i2) {
//                    this.mStartPosition = CategoriesListFragment.this.mRecyclerView.getChildLayoutPosition(CategoriesListFragment.this.mRecyclerView.findChildViewUnder((float) i, (float) i2));
//                    if (this.mStartPosition == CategoriesListFragment.this.mCategoryAdapter.getItemCount() - 1) {
//                        this.mStartPosition = -1;
//                    }
//                }
//
//                public void onMultiSelectStop(int i, int i2) {
//                    this.mEndPosition = CategoriesListFragment.this.mRecyclerView.getChildLayoutPosition(CategoriesListFragment.this.mRecyclerView.findChildViewUnder((float) i, (float) i2));
//                    if (this.mEndPosition == CategoriesListFragment.this.mCategoryAdapter.getItemCount() - 1) {
//                        this.mEndPosition = -1;
//                    }
//                    if (this.mStartPosition != -1 || this.mEndPosition != -1 || i2 < 0) {
//                        if (this.mStartPosition == -1) {
//                            this.mStartPosition = CategoriesListFragment.this.mCategoryAdapter.getItemCount() - 2;
//                        }
//                        if (this.mEndPosition == -1) {
//                            if (i2 < 0) {
//                                this.mEndPosition = 0;
//                            } else {
//                                this.mEndPosition = CategoriesListFragment.this.mCategoryAdapter.getItemCount() - 2;
//                            }
//                        }
//                        int i3 = this.mStartPosition;
//                        int i4 = this.mEndPosition;
//                        if (i3 > i4) {
//                            i3 = i4;
//                        }
//                        int i5 = this.mStartPosition;
//                        int i6 = this.mEndPosition;
//                        if (i5 <= i6) {
//                            i5 = i6;
//                        }
//                        ArrayList arrayList = new ArrayList();
//                        if (i5 >= 0) {
//                            if (i3 < 0) {
//                                i3 = 0;
//                            }
//                            Cursor cursor = CategoriesListFragment.this.mCategoryAdapter.getCursor();
//                            if (cursor == null || cursor.isClosed()) {
//                                Log.m26i(CategoriesListFragment.TAG, "onMultiSelectStop - cursor is null");
//                                return;
//                            }
//                            while (i3 <= i5) {
//                                cursor.moveToPosition(i3);
//                                arrayList.add(Long.valueOf(cursor.getLong(cursor.getColumnIndex(CategoryRepository.LabelColumn.f102ID))));
//                                i3++;
//                            }
//                            if (Settings.getIntSettings(Settings.KEY_LIST_MODE, 0) != 0 && !DataRepository.getInstance().getCategoryRepository().isChildList()) {
//                                Intent intent = new Intent(CategoriesListFragment.this.getActivity(), ManageCategoriesActivity.class);
//                                Bundle bundle = new Bundle();
//                                bundle.putSerializable("category_id", arrayList);
//                                bundle.putInt(ManageCategoriesActivity.KEY_BUNDLE_ENTER_MODE, 12);
//                                intent.putExtras(bundle);
//                                try {
//                                    CategoriesListFragment.this.getActivity().startActivity(intent);
//                                } catch (ActivityNotFoundException e) {
//                                    Log.m24e(CategoriesListFragment.TAG, "ActivityNotFoundException", (Throwable) e);
//                                }
//                            }
//                        }
//                    }
//                }
//            });
        }
    }

    public void onDetach() {
        Log.m26i(TAG, "onDetach");
        super.onDetach();
    }

    public void onSceneChange(int i) {
        Log.m26i(TAG, "onSceneChange - scene : " + i);
        if (this.mScene == i) {
            Log.m19d(TAG, "onSceneChange - same old scene");
        } else if (!isAdded() || isRemoving()) {
            Log.m19d(TAG, "onSceneChange - it is not added");
        } else {
            int i2 = this.mScene;
            if ((i2 == 7 || i2 == 9) && this.mScene != i) {
                Log.m29v(TAG, "search results should be removed");
                CursorProvider.getInstance().reloadCategory(getLoaderManager());
            }
            this.mScene = i;
            if (this.mRecyclerView != null) {
                int i3 = this.mScene;
                if (i3 == 4 || i3 == 6) {
                    changeListVisibility(false);
                } else {
                    changeListVisibility(true);
                }
                if (this.mScene == 7) {
                    this.mCategoryAdapter.setNeedShowFooterView(false);
                }
            }
        }
    }

    public void updateListCategory(boolean z) {
        if (z) {
            Log.m26i(TAG, "updateListCategory");
            if (isRemoving() || getActivity() == null) {
                Log.m22e(TAG, "updateListCategory - removing or getActivity is null");
            } else {
                CursorProvider.getInstance().reloadCategory(getLoaderManager());
            }
        }
    }

    private void insertSALogForItemClick(int i) {
        SALogProvider.insertSALog(getActivity().getResources().getString(C0690R.string.screen_list_category), getActivity().getResources().getString(i != 0 ? i != 1 ? i != 2 ? i != 3 ? i < 0 ? C0690R.string.event_list_add_category : C0690R.string.event_user_category : C0690R.string.event_call_history_category : C0690R.string.event_stt_category : C0690R.string.event_interview_category : C0690R.string.event_uncategorized_category));
    }

    private String createNewNameCategory() {
        String string = getResources().getString(C0690R.string.category);
        String str = string + " " + String.format(Locale.getDefault(), "%d", new Object[]{1});
        int i = 1;
        while (DataRepository.getInstance().getCategoryRepository().isExitSameTitle(getActivity(), str)) {
            i++;
            str = string + " " + String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(i)});
        }
        return str;
    }

    private void addNewCategory() {
        Bundle bundle = new Bundle();
        bundle.putString(DialogFactory.BUNDLE_NAME, createNewNameCategory());
        bundle.putInt(DialogFactory.BUNDLE_REQUEST_CODE, 13);
        bundle.putInt(DialogFactory.BUNDLE_TITLE_ID, C0690R.string.addnewlabel);
        bundle.putInt(DialogFactory.BUNDLE_POSITIVE_BTN_ID, C0690R.string.add_category_button);
        bundle.putInt(DialogFactory.BUNDLE_NEGATIVE_BTN_ID, 17039360);
        DialogFactory.show(getFragmentManager(), DialogFactory.CATEGORY_RENAME, bundle, this);
    }

    public void notifyDataSetChanged() {
        Handler handler = this.mRefreshHandler;
        if (handler != null) {
            handler.removeMessages(1);
            int i = this.mRefreshCount;
            if (i <= 10) {
                this.mRefreshHandler.sendEmptyMessageDelayed(1, 300);
                this.mRefreshCount++;
                return;
            }
            this.mRefreshCount = i % 10;
            this.mRefreshHandler.sendEmptyMessage(1);
        }
    }

    public void onCursorChanged(Cursor cursor) {
        Log.m26i(TAG, "onCursorChanged");
        if (isRemoving() || getActivity() == null) {
            Log.m22e(TAG, "onCursorChanged - removing or getActivity is null");
            this.mRecyclerView.setAdapter((RecyclerView.Adapter) null);
        } else if (cursor == null || cursor.isClosed()) {
            Log.m22e(TAG, "onCursorChanged - cursor is closed");
        } else {
            CategoryListAdapter categoryListAdapter = this.mCategoryAdapter;
            if (categoryListAdapter == null) {
                Log.m22e(TAG, "onCursorChanged - mListAdapter is null");
                return;
            }
            categoryListAdapter.changeCursor(cursor);
            int count = this.mCategoryAdapter.getCount();
            if (CursorProvider.getInstance().getCallHistoryCount() > 0) {
                SALogProvider.insertStatusLog(SALogProvider.KEY_SA_USER_CATEGORY_STATUS, count - 4);
            } else {
                SALogProvider.insertStatusLog(SALogProvider.KEY_SA_USER_CATEGORY_STATUS, (count - 4) + 1);
            }
            if (this.mScene != 7) {
                this.mCategoryAdapter.setNeedShowFooterView(true);
            } else {
                this.mCategoryAdapter.setNeedShowFooterView(false);
            }
            if (this.mIsNeedScrollToEnd) {
                this.mRecyclerView.postDelayed(new Runnable() {
                    public final void run() {
                        CategoriesListFragment.this.lambda$onCursorChanged$2$CategoriesListFragment();
                    }
                }, 300);
                this.mIsNeedScrollToEnd = false;
            }
        }
    }

    public /* synthetic */ void lambda$onCursorChanged$2$CategoriesListFragment() {
        this.mRecyclerView.smoothScrollToPosition(this.mCategoryAdapter.getItemCount());
    }

    public void onLoadReset() {
        CategoryListAdapter categoryListAdapter = this.mCategoryAdapter;
        if (categoryListAdapter != null) {
            categoryListAdapter.swapCursor((Cursor) null);
        }
    }

    private void changeListVisibility(boolean z) {
        Log.m26i(TAG, "changeListVisibility : " + z);
        if (getActivity() == null || getActivity().getWindow() == null) {
            Log.m22e(TAG, "changeListVisibility getActivity or getWindow return null");
            return;
        }
        changeViewVisibility(z);
        if (z) {
            WindowManager.LayoutParams attributes = getActivity().getWindow().getAttributes();
//            attributes.semAddExtensionFlags(1);
            getActivity().getWindow().setAttributes(attributes);
            return;
        }
        WindowManager.LayoutParams attributes2 = getActivity().getWindow().getAttributes();
//        attributes2.semAddExtensionFlags(0);
        getActivity().getWindow().setAttributes(attributes2);
    }

    private void changeViewVisibility(boolean z) {
        if (this.mRecyclerView == null) {
            return;
        }
        if (z) {
            this.mViewMarginBottom.setVisibility(0);
            this.mRecyclerView.setVisibility(0);
            return;
        }
        this.mViewMarginBottom.setVisibility(8);
        this.mRecyclerView.setVisibility(4);
    }

    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        ContextMenuProvider.getInstance().createContextMenu(getActivity(), contextMenu, this.mScene, this.mRecyclerView, this);
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        ContextMenuProvider.getInstance().contextItemSelected((AppCompatActivity) getActivity(), menuItem, this.mScene, this);
        return false;
    }

    private void contextualMenuRename() {
        String labelTitle = DataRepository.getInstance().getCategoryRepository().getLabelTitle((int) ContextMenuProvider.getInstance().getCategoryId(), getContext());
        Bundle bundle = new Bundle();
        bundle.putString(DialogFactory.BUNDLE_NAME, labelTitle);
        bundle.putInt(DialogFactory.BUNDLE_REQUEST_CODE, 1);
        bundle.putInt(DialogFactory.BUNDLE_TITLE_ID, C0690R.string.rename_category);
        bundle.putInt(DialogFactory.BUNDLE_POSITIVE_BTN_ID, C0690R.string.rename);
        bundle.putInt(DialogFactory.BUNDLE_NEGATIVE_BTN_ID, 17039360);
        DialogFactory.show(getFragmentManager(), DialogFactory.CATEGORY_RENAME, bundle, this);
    }

    private void deleteCategory(int i) {
        DataRepository.getInstance().getCategoryRepository().deleteColumn(i);
        DBProvider.getInstance().updateCategoryID(getContext(), i, Settings.getIntSettings(Settings.KEY_CATEGORY_LABEL_ID, 0));
        ContextMenuProvider.getInstance().setCategoryId(-1);
        DataRepository.getInstance().getCategoryRepository().deleteCompleted();
    }

    private void shortCutSelect(ArrayList<Long> arrayList) {
        Intent intent = new Intent(getActivity(), ManageCategoriesActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("category_id", arrayList);
        bundle.putInt(ManageCategoriesActivity.KEY_BUNDLE_ENTER_MODE, 12);
        intent.putExtras(bundle);
        try {
            getActivity().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.m24e(TAG, "ActivityNotFoundException", (Throwable) e);
        }
    }

    private void setScrollbarPosition() {
        if (getContext().getResources().getConfiguration().getLayoutDirection() == 1) {
            this.mRecyclerView.setVerticalScrollbarPosition(1);
        } else {
            this.mRecyclerView.setVerticalScrollbarPosition(2);
        }
    }

    public void onItemClick(View view, int i) {
        Log.m26i(TAG, "onItemClick");
        FragmentActivity activity = getActivity();
        VNMainActivity vNMainActivity = activity instanceof VNMainActivity ? (VNMainActivity) activity : null;
        if (activity != null && vNMainActivity != null && !vNMainActivity.isActivityResumed()) {
            return;
        }
        if (!this.mCategoryAdapter.isNeedShowFooterView() || this.mCategoryAdapter.getCount() != i) {
            int itemId = (int) this.mCategoryAdapter.getItemId(i);
            DataRepository.getInstance().getCategoryRepository().setCurrentCategoryID(itemId);
            insertSALogForItemClick(itemId);
            if (this.mScene == 7) {
                postEvent(Event.STOP_SEARCH);
                postEvent(3);
            }
            postEvent(Event.ENTER_CATEGORY);
        } else if (!DialogFactory.isDialogVisible(getFragmentManager(), DialogFactory.CATEGORY_RENAME)) {
            addNewCategory();
        }
    }

    public boolean onItemLongClick(View view, int i) {
        Log.m26i(TAG, "onItemLongClick");
        if (this.mCategoryAdapter.getCount() == i) {
            return true;
        }
        long itemId = this.mCategoryAdapter.getItemId(i);
        ArrayList arrayList = new ArrayList();
        arrayList.add(Long.valueOf(itemId));
        ContextMenuProvider.getInstance().setId(-1);
        Intent intent = new Intent(getActivity(), ManageCategoriesActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("category_id", arrayList);
        bundle.putInt(ManageCategoriesActivity.KEY_BUNDLE_ENTER_MODE, 12);
        intent.putExtras(bundle);
        try {
            getActivity().startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Log.m24e(TAG, "ActivityNotFoundException", (Throwable) e);
            return false;
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.CategoriesListFragment$RoundedDecoration */
    private class RoundedDecoration extends RecyclerView.ItemDecoration {
        private RoundedDecoration() {
        }

        public void seslOnDispatchDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
//            super.seslOnDispatchDraw(canvas, recyclerView, state);
//            CategoriesListFragment.this.mSeslListRoundedCorner.drawRoundedCorner(canvas);
        }
    }
}
