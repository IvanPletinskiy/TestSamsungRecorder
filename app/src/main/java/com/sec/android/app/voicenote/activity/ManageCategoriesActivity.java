package com.sec.android.app.voicenote.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.CategoryInfo;
import com.sec.android.app.voicenote.common.util.CategoryRepository;
import com.sec.android.app.voicenote.common.util.DataRepository;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.p007ui.adapter.CategoryManagementAdapter;
import com.sec.android.app.voicenote.p007ui.adapter.ItemTouchHelperCallback;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.DBProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.MouseKeyboardProvider;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.ViewProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class ManageCategoriesActivity extends BaseToolbarActivity implements DialogFactory.DialogResultListener, View.OnClickListener, Observer, BottomNavigationView.OnNavigationItemSelectedListener, CategoryManagementAdapter.OnItemClickListener {
    public static final String KEY_BUNDLE_CATEGORY_ID = "category_id";
    public static final String KEY_BUNDLE_ENTER_MODE = "enter_mode";
    public static final String KEY_BUNDLE_FIRST_POSITION = "start_position";
    public static final String KEY_BUNDLE_LAST_POSITION = "last_position";
    public static final String KEY_HASH_MAP_RESTORE = "hash_map_restore";
    private static final int MAX_CONTENT_PROVIDER_OPERATIONS_SIZE = 500;
    public static final int MODE_CATEGORY_SEARCH_SELECT = 15;
    public static final int MODE_LONG_PRESS = 12;
    public static final int MODE_MANAGE_CATEGORY = 14;
    public static final int MODE_MOVE = 13;
    private static final String TAG = "ManageCategoriesActivity";
    private final String SELECT_MODE_STATE = "select_mode_state";
    private BottomNavigationView mBottomNavigationView = null;
    private CheckBox mCheckBox;
    private LinearLayout mCheckBoxContainer;
    private TextView mCheckBoxCountView;
    private DeleteTask mDeleteCategory = null;
    private int mEnterMode;
    private boolean mIsCollapsingToolbarEnable;
    /* access modifiers changed from: private */
    public boolean mIsLongpress = false;
    /* access modifiers changed from: private */
    public boolean mIsSelectMode = false;
    private ItemTouchHelper mItemTouchHelper;
    /* access modifiers changed from: private */
    public CategoryManagementAdapter mListAdapter = null;
    /* access modifiers changed from: private */
    public ArrayList<Long> mListIdsMove;
    private ArrayList<Integer> mListPositionSelected;
    /* access modifiers changed from: private */
    public ArrayList<CategoryInfo> mListViewItems;
    private RelativeLayout mManageCategoriesLayout;
    private Menu mMenu = null;
    private int mNumberCategoryDefault = 0;
    /* access modifiers changed from: private */
    public RecyclerView mRecyclerView = null;
    private RoundedDecoration mRoundedDecoration;
    /* access modifiers changed from: private */
//    public SeslRoundedCorner mSeslListRoundedCorner;
    private Handler mTaskEventHandler = null;
    private UpdateTask mUpdateCategory = null;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.m29v(TAG, "onCreate");
        ArrayList arrayList = new ArrayList();
        this.mListViewItems = new ArrayList<>();
        this.mListIdsMove = new ArrayList<>();
        this.mListPositionSelected = new ArrayList<>();
        if (getIntent() != null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                this.mEnterMode = extras.getInt(KEY_BUNDLE_ENTER_MODE, 14);
                int i = this.mEnterMode;
                if (i == 12) {
                    this.mIsCollapsingToolbarEnable = true;
                    arrayList = (ArrayList) extras.getSerializable("category_id");
                } else if (i == 13) {
                    this.mListIdsMove = (ArrayList) extras.getSerializable("category_id");
                }
            } else {
                this.mIsCollapsingToolbarEnable = true;
                this.mEnterMode = 14;
            }
        }
        setContentView((int) C0690R.layout.activity_voice_note_managelabel);
        if (getResources().getConfiguration().orientation == 2 && DisplayManager.getMultiwindowMode() != 2) {
            getWindow().setFlags(1024, 1024);
        }
        DataRepository.getInstance().getCategoryRepository().loadLabelToMap();
        if (arrayList != null) {
            listBinding(arrayList);
            arrayList.clear();
        }
        VoiceNoteObservable.getInstance().addObserver(this);
        this.mCollapsingToolbarLayout.setBackground(new ColorDrawable(this.mResource.getColor(C0690R.C0691color.actionbar_color_bg, (Resources.Theme) null)));
        initView();
        int i2 = this.mEnterMode;
        if (i2 == 15 || i2 == 14) {
            this.mIsSelectMode = true;
            startSelectMode();
        } else if (this.mListPositionSelected.size() != 0) {
            Iterator<Integer> it = this.mListPositionSelected.iterator();
            while (it.hasNext()) {
                int intValue = it.next().intValue();
                if (this.mListViewItems.get(intValue).getIdCategory().intValue() > 3) {
                    this.mListAdapter.toggleSelection(intValue);
                    if (intValue == this.mListAdapter.getItemCount() - 1) {
                        this.mRecyclerView.postDelayed(new Runnable() {
                            public final void run() {
                                ManageCategoriesActivity.this.lambda$onCreate$0$ManageCategoriesActivity();
                            }
                        }, 50);
                    }
                }
            }
            startSelectMode();
            this.mRecyclerView.scrollToPosition(this.mListPositionSelected.get(0).intValue());
        } else {
            showManageCategories();
//            this.mListViewItems.add((Object) null);
        }
        if (bundle != null) {
            DialogFactory.setDialogResultListener(getSupportFragmentManager(), DialogFactory.CATEGORY_RENAME, this);
        }
    }

    public /* synthetic */ void lambda$onCreate$0$ManageCategoriesActivity() {
        this.mRecyclerView.smoothScrollBy(getResources().getDimensionPixelSize(C0690R.dimen.fast_option_view_height), 0);
    }

    private void showManageCategories() {
        Log.m26i(TAG, "showManageCategories");
        int i = this.mEnterMode == 13 ? C0690R.string.select_category : C0690R.string.manage_categories;
        setDisplayShowHomeEnabled();
        setTitleActivity(i);
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        CheckBox checkBox;
        Log.m26i(TAG, "dispatchKeyEvent");
        if (keyEvent.isCtrlPressed() && keyEvent.getKeyCode() == 29 && keyEvent.getAction() == 0 && (checkBox = (CheckBox) getWindow().getDecorView().findViewById(C0690R.C0693id.optionbar_checkbox)) != null) {
            checkBox.performClick();
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    public void onBackPressed() {
        Log.m26i(TAG, "onBackPressed");
        if (!isFinishing() || !isDestroyed()) {
            super.onBackPressed();
        }
    }

    public boolean isCollapsingToolbarEnable() {
        return this.mIsCollapsingToolbarEnable;
    }

    @SuppressLint({"InflateParams"})
    private void initView() {
        Log.m26i(TAG, "initView");
        if (this.mListAdapter == null) {
            this.mListAdapter = new CategoryManagementAdapter(this, this.mListViewItems);
        }
        this.mIsSelectMode = false;
        if (this.mBottomNavigationView == null) {
            this.mBottomNavigationView = (BottomNavigationView) findViewById(C0690R.C0693id.bottom_navigation_category);
            this.mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        }
        updateBottomButtonShape();
        this.mManageCategoriesLayout = (RelativeLayout) findViewById(C0690R.C0693id.layout_manage_categories);
        updateLayoutInTabletMultiWindow(this);
        if (this.mRecyclerView == null) {
            this.mRecyclerView = (RecyclerView) findViewById(C0690R.C0693id.category_list);
            this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            this.mRecyclerView.setOnCreateContextMenuListener(this);
            if (this.mEnterMode != 13) {
                setLongPressMultiSelection();
                setPenSelectMode();
            }
            this.mListAdapter.setOnTouchCategoryItemListener(this);
            this.mRecyclerView.setAdapter(this.mListAdapter);
//            this.mRecyclerView.seslSetFastScrollerEnabled(true);
//            this.mRecyclerView.seslSetGoToTopEnabled(true);
//            this.mRecyclerView.seslSetGoToTopBottomPadding(getResources().getDimensionPixelOffset(C0690R.dimen.go_to_top_bottom_padding));
//            this.mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(this.mListAdapter));
            this.mItemTouchHelper.attachToRecyclerView(this.mRecyclerView);
//            this.mSeslListRoundedCorner = new SeslRoundedCorner(this);
//            this.mSeslListRoundedCorner.setRoundedCorners(15);
            this.mRoundedDecoration = new RoundedDecoration();
            this.mRecyclerView.addItemDecoration(this.mRoundedDecoration);
        }
    }

    private void updateLayoutInTabletMultiWindow(Activity activity) {
        if (VoiceNoteFeature.FLAG_IS_TABLET && this.mManageCategoriesLayout != null && DisplayManager.isCurrentWindowOnLandscape(activity)) {
            if ((DisplayManager.getMultiwindowMode() == 2 && getResources().getConfiguration().densityDpi < 480) || ((DisplayManager.getMultiwindowMode() != 2 && getResources().getConfiguration().densityDpi >= 480) || (DisplayManager.getMultiwindowMode() == 1 && getResources().getConfiguration().screenWidthDp < 960))) {
                int i = (int) (((double) getResources().getDisplayMetrics().widthPixels) * 0.05d);
                this.mManageCategoriesLayout.setPadding(i, 0, i, 0);
            }
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        this.mMenu = menu;
        prepareMenu(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    private void prepareMenu(Menu menu) {
        Log.m26i(TAG, "prepareMenu");
        clearBottomNavigationView();
        if (this.mEnterMode == 13) {
            menu.clear();
        } else if (this.mIsSelectMode) {
            menu.clear();
            this.mBottomNavigationView.getMenu().clear();
            getMenuInflater().inflate(C0690R.C0695menu.actionmode_menu_label, menu);
            this.mBottomNavigationView.inflateMenu(C0690R.C0695menu.bottom_actionmode_menu_label);
            if (this.mListAdapter.getSelectedCount() != 0) {
                enableMarginBottomList(true);
                this.mBottomNavigationView.setVisibility(0);
                if (this.mListAdapter.getSelectedCount() != 1) {
                    this.mBottomNavigationView.getMenu().removeItem(C0690R.C0693id.action_rename_category);
                }
            }
            menu.findItem(C0690R.C0693id.manage_label_popup_rename).setVisible(false);
            menu.findItem(C0690R.C0693id.action_delete_label).setVisible(false);
        } else {
            menu.clear();
            clearBottomNavigationView();
        }
    }

    private void clearBottomNavigationView() {
        BottomNavigationView bottomNavigationView = this.mBottomNavigationView;
        if (bottomNavigationView != null) {
            bottomNavigationView.getMenu().clear();
            this.mBottomNavigationView.setVisibility(8);
        }
        enableMarginBottomList(false);
    }

    private void enableMarginBottomList(boolean z) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mRecyclerView.getLayoutParams();
        if (z) {
            layoutParams.bottomMargin = getResources().getDimensionPixelSize(C0690R.dimen.fast_option_view_height);
        } else {
            layoutParams.bottomMargin = 0;
        }
        this.mRecyclerView.setLayoutParams(layoutParams);
    }

    /* JADX WARNING: type inference failed for: r1v4, types: [long[], java.io.Serializable] */
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_select_category), getResources().getString(C0690R.string.event_select_category_back));
            finish();
        } else if (itemId != C0690R.C0693id.action_delete_label) {
            if (itemId != C0690R.C0693id.manage_label_popup_rename) {
                return super.onOptionsItemSelected(menuItem);
            }
            String labelTitle = DataRepository.getInstance().getCategoryRepository().getLabelTitle((int) this.mListAdapter.getIdSelected(), this);
            Bundle bundle = new Bundle();
            bundle.putString(DialogFactory.BUNDLE_NAME, labelTitle);
            bundle.putInt(DialogFactory.BUNDLE_REQUEST_CODE, 1);
            bundle.putInt(DialogFactory.BUNDLE_TITLE_ID, C0690R.string.rename_category);
            bundle.putInt(DialogFactory.BUNDLE_POSITIVE_BTN_ID, C0690R.string.rename);
            bundle.putInt(DialogFactory.BUNDLE_NEGATIVE_BTN_ID, 17039360);
            DialogFactory.show(getSupportFragmentManager(), DialogFactory.CATEGORY_RENAME, bundle, this);
            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_manage_category), getResources().getString(C0690R.string.event_category_rename));
        } else if (!DialogFactory.isDialogVisible(getSupportFragmentManager(), DialogFactory.DELETE_CATEGORY_DIALOG)) {
            Bundle bundle2 = new Bundle();
            bundle2.putSerializable(DialogFactory.BUNDLE_IDS, this.mListAdapter.getArrayIds());
            DialogFactory.show(getSupportFragmentManager(), DialogFactory.DELETE_CATEGORY_DIALOG, bundle2);
            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_manage_category), getResources().getString(C0690R.string.event_category_del));
        }
        return true;
    }

    private void showCagetoriesSelect() {
        Log.m26i(TAG, "showCagetoriesSelect ");
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            View inflate = LayoutInflater.from(this).inflate(C0690R.layout.optionbar_edit_title, (ViewGroup) null);
            this.mCheckBoxCountView = (TextView) inflate.findViewById(C0690R.C0693id.optionbar_title);
            ViewProvider.setMaxFontSize(getBaseContext(), this.mCheckBoxCountView);
            this.mCheckBoxContainer = (LinearLayout) inflate.findViewById(C0690R.C0693id.checkbox_container);
            this.mCheckBox = (CheckBox) inflate.findViewById(C0690R.C0693id.optionbar_checkbox);
//            if (Build.VERSION.SEM_INT < 2401) {
//                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mCheckBox.getLayoutParams();
//                layoutParams.topMargin = getResources().getDimensionPixelSize(C0690R.dimen.actionbar_checkbox_top_margin);
//                this.mCheckBox.setLayoutParams(layoutParams);
//            }
            this.mCheckBox.setOnClickListener(this);
            supportActionBar.setCustomView(inflate, new ActionBar.LayoutParams(-1, -1, 16));
            supportActionBar.setDisplayOptions(16);
            supportActionBar.setBackgroundDrawable(new ColorDrawable(this.mResource.getColor(C0690R.C0691color.actionbar_color_bg, (Resources.Theme) null)));
            supportActionBar.show();
            updateCheckBox();
            setTextViewTitle(this.mCheckBoxCountView);
        }
    }

    /* access modifiers changed from: private */
    public void updateCheckBox() {
        String str;
        Log.m26i(TAG, "updateCheckBox ");
        int selectedCount = this.mListAdapter.getSelectedCount();
        int size = this.mListViewItems.size() - this.mNumberCategoryDefault;
        Log.m26i(TAG, "updateCheckBox count : " + selectedCount + " total : " + size);
        if (this.mCheckBox != null) {
            if (this.mListViewItems.size() == this.mNumberCategoryDefault) {
                this.mCheckBox.setEnabled(false);
            }
            if (size != selectedCount || selectedCount == 0) {
                this.mCheckBox.setChecked(false);
            } else {
                this.mCheckBox.setChecked(true);
            }
        }
        if (this.mCheckBoxCountView != null) {
            Log.m29v(TAG, "selected count : " + selectedCount);
            if (selectedCount == 0) {
                str = getResources().getString(C0690R.string.select_categories);
            } else {
                str = getResources().getString(C0690R.string.selected, new Object[]{Integer.valueOf(selectedCount)});
            }
            setContentDescription(selectedCount, size);
            this.mCheckBoxCountView.setText(str);
            setCollapsingToolbarTitle(str);
        }
    }

    /* access modifiers changed from: protected */
    public void setContentDescription(int i, int i2) {
        if (this.mCheckBoxContainer != null) {
            Resources resources = getResources();
            String string = resources.getString(C0690R.string.tts_double_tap_select_all);
            String string2 = resources.getString(C0690R.string.tts_double_tap_deselect_all);
            String string3 = resources.getString(C0690R.string.tts_tick_box_t_tts);
            String string4 = resources.getString(C0690R.string.tts_not_ticked_t_tts);
            String string5 = resources.getString(C0690R.string.tts_ticked_t_tts);
            String string6 = resources.getString(C0690R.string.tts_nothing_selected);
            String string7 = resources.getString(C0690R.string.tts_selected, new Object[]{Integer.valueOf(i)});
            StringBuilder sb = new StringBuilder();
            if (i == 0) {
                try {
                    sb.append(string6);
                    sb.append(" , ");
                    sb.append(string);
                    sb.append(" , ");
                    sb.append(string3);
                    sb.append(" , ");
                    sb.append(string4);
                } catch (IllegalStateException e) {
                    Log.m22e(TAG, e.toString());
                }
            } else if (i < i2) {
                sb.append(string7);
                sb.append(" , ");
                sb.append(string);
                sb.append(" , ");
                sb.append(string3);
                sb.append(" , ");
                sb.append(string4);
            } else {
                sb.append(string7);
                sb.append(" , ");
                sb.append(string2);
                sb.append(" , ");
                sb.append(string3);
                sb.append(" , ");
                sb.append(string5);
            }
            this.mCheckBoxContainer.setContentDescription(sb.toString());
        }
    }

    private void addNewCategory() {
        Bundle bundle = new Bundle();
        bundle.putString(DialogFactory.BUNDLE_NAME, createNewNameCategory());
        bundle.putInt(DialogFactory.BUNDLE_REQUEST_CODE, 13);
        bundle.putInt(DialogFactory.BUNDLE_TITLE_ID, C0690R.string.addnewlabel);
        bundle.putInt(DialogFactory.BUNDLE_POSITIVE_BTN_ID, C0690R.string.add_category_button);
        bundle.putInt(DialogFactory.BUNDLE_NEGATIVE_BTN_ID, 17039360);
        DialogFactory.show(getSupportFragmentManager(), DialogFactory.CATEGORY_RENAME, bundle, this);
    }

    private void startSelectMode() {
        this.mIsSelectMode = true;
        this.mListAdapter.setSelectionMode(true);
        showCagetoriesSelect();
        invalidateOptionsMenu();
    }

    private String createNewNameCategory() {
        String string = getResources().getString(C0690R.string.category);
        String str = string + " " + String.format(Locale.getDefault(), "%d", new Object[]{1});
        int i = 1;
        while (DataRepository.getInstance().getCategoryRepository().isExitSameTitle(this, str)) {
            i++;
            str = string + " " + String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(i)});
        }
        return str;
    }

    private void listBinding(ArrayList<Long> arrayList) {
        Log.m19d(TAG, "listBinding");
        Cursor categoryCursor = CursorProvider.getInstance().getCategoryCursor(false);
        if (categoryCursor == null || categoryCursor.isClosed()) {
            Log.m22e(TAG, "listBinding() : cursor null");
            return;
        }
        this.mListViewItems.clear();
        try {
            categoryCursor.moveToFirst();
            int columnIndex = categoryCursor.getColumnIndex(CategoryRepository.LabelColumn.f102ID);
            int columnIndex2 = categoryCursor.getColumnIndex("POSITION");
            while (!categoryCursor.isAfterLast()) {
                int i = categoryCursor.getInt(columnIndex);
                this.mListViewItems.add(new CategoryInfo(i, getCategoryName(categoryCursor, i, this), categoryCursor.getInt(columnIndex2)));
                if (i <= 3 && this.mEnterMode != 13) {
                    this.mNumberCategoryDefault++;
                }
                if (arrayList.contains(Long.valueOf((long) i))) {
                    this.mListPositionSelected.add(Integer.valueOf(this.mListViewItems.size() - 1));
                }
                categoryCursor.moveToNext();
            }
        } catch (Exception e) {
            Log.m24e(TAG, "Exception", (Throwable) e);
        }
        if (!categoryCursor.isClosed()) {
            categoryCursor.close();
        }
    }

    private String getCategoryName(Cursor cursor, int i, Context context) {
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

    /* access modifiers changed from: protected */
    public void onDestroy() {
        Log.m29v(TAG, "onDestroy");
        VoiceNoteObservable.getInstance().deleteObserver(this);
        UpdateTask updateTask = this.mUpdateCategory;
        if (updateTask != null && updateTask.getStatus() == AsyncTask.Status.RUNNING) {
            Log.m29v(TAG, "onDestroy - mUpdateCategory cancel");
            this.mUpdateCategory.cancel(true);
        }
        DeleteTask deleteTask = this.mDeleteCategory;
        if (deleteTask != null && deleteTask.getStatus() == AsyncTask.Status.RUNNING) {
            Log.m29v(TAG, "onDestroy - mDeleteCategory cancel");
            this.mDeleteCategory.cancel(true);
        }
        this.mUpdateCategory = null;
        this.mDeleteCategory = null;
        RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView != null) {
            recyclerView.setAdapter((RecyclerView.Adapter) null);
            this.mRecyclerView = null;
        }
        CategoryManagementAdapter categoryManagementAdapter = this.mListAdapter;
        if (categoryManagementAdapter != null) {
            categoryManagementAdapter.onDestroy();
            this.mListAdapter = null;
        }
        ArrayList<CategoryInfo> arrayList = this.mListViewItems;
        if (arrayList != null) {
            arrayList.clear();
            this.mListViewItems = null;
        }
        ArrayList<Long> arrayList2 = this.mListIdsMove;
        if (arrayList2 != null) {
            arrayList2.clear();
            this.mListIdsMove = null;
        }
        ArrayList<Integer> arrayList3 = this.mListPositionSelected;
        if (arrayList3 != null) {
            arrayList3.clear();
            this.mListPositionSelected = null;
        }
        super.onDestroy();
    }

    public void onResume() {
        Log.m29v(TAG, "onResume");
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        Log.m26i(TAG, "onPause");
        super.onPause();
    }

    public void onDialogResult(DialogFragment dialogFragment, Bundle bundle) {
        if (bundle != null) {
            int i = bundle.getInt(DialogFactory.BUNDLE_REQUEST_CODE);
            int i2 = bundle.getInt("result_code");
            String string = bundle.getString(DialogFactory.BUNDLE_NAME);
            Log.m26i(TAG, "onDialogResult - requestCode : " + i + " result : " + i2 + " mEnterMode : " + this.mEnterMode);
            if (i == 1) {
                int findPosById = findPosById(this.mListAdapter.getIdSelected());
                if (findPosById != -1) {
                    DataRepository.getInstance().getCategoryRepository().updateColumn(this.mListViewItems.get(findPosById).getIdCategory().intValue(), string);
                    int i3 = this.mEnterMode;
                    if (i3 == 12 || i3 == 14 || i3 == 15) {
                        finish();
                    } else {
                        this.mListViewItems.get(findPosById).setTitle(string);
                    }
                }
            } else if (i == 13) {
                int maxCategoryPos = CursorProvider.getInstance().getMaxCategoryPos() + 1;
                int insertColumn = DataRepository.getInstance().getCategoryRepository().insertColumn(string, maxCategoryPos);
                ArrayList<CategoryInfo> arrayList = this.mListViewItems;
                arrayList.add(arrayList.size() - 1, new CategoryInfo(insertColumn, string, maxCategoryPos));
                invalidateOptionsMenu();
                this.mRecyclerView.postDelayed(new Runnable() {
                    public final void run() {
                        ManageCategoriesActivity.this.lambda$onDialogResult$1$ManageCategoriesActivity();
                    }
                }, 300);
            }
        }
    }

    public /* synthetic */ void lambda$onDialogResult$1$ManageCategoriesActivity() {
        this.mRecyclerView.smoothScrollToPosition(this.mListAdapter.getItemCount());
    }

    private int findPosById(long j) {
        for (int i = 0; i < this.mListViewItems.size(); i++) {
            if (j == ((long) this.mListViewItems.get(i).getIdCategory().intValue())) {
                return i;
            }
        }
        return -1;
    }

    public void update(Observable observable, Object obj) {
        int intValue = ((Integer) obj).intValue();
        Log.m26i(TAG, "update - event : " + intValue);
        if (intValue == 964) {
            this.mDeleteCategory = new DeleteTask();
            this.mDeleteCategory.execute(new Long[0]);
        }
    }

    public void onClick(View view) {
        Log.m26i(TAG, "onClick");
        if (view.getId() == C0690R.C0693id.optionbar_checkbox) {
            if (this.mListAdapter.getSelectedCount() < this.mListAdapter.getItemCount() - this.mNumberCategoryDefault) {
                this.mListAdapter.selectAll();
            } else {
                this.mListAdapter.removeSelection();
            }
            updateCheckBox();
            invalidateOptionsMenu();
            this.mListAdapter.notifyDataSetChanged();
            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_manage_category), getResources().getString(C0690R.string.event_category_select_all));
        }
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
//                    this.mStartPosition = ManageCategoriesActivity.this.mRecyclerView.getChildLayoutPosition(ManageCategoriesActivity.this.mRecyclerView.findChildViewUnder((float) i, (float) i2));
//                }
//
//                public void onMultiSelectStop(int i, int i2) {
//                    this.mEndPosition = ManageCategoriesActivity.this.mRecyclerView.getChildLayoutPosition(ManageCategoriesActivity.this.mRecyclerView.findChildViewUnder((float) i, (float) i2));
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
//                        while (i3 <= i5) {
//                            if (((CategoryInfo) ManageCategoriesActivity.this.mListViewItems.get(i3)).getIdCategory().intValue() > 3) {
//                                ManageCategoriesActivity.this.mListAdapter.toggleSelection(i3);
//                            }
//                            i3++;
//                        }
//                        if (ManageCategoriesActivity.this.mIsSelectMode) {
//                            ManageCategoriesActivity.this.updateCheckBox();
//                            ManageCategoriesActivity.this.invalidateOptionsMenu();
//                            ManageCategoriesActivity.this.mListAdapter.notifyDataSetChanged();
//                        }
//                    }
//                }
//            });
        }
    }

    private void setLongPressMultiSelection() {
//        this.mRecyclerView.seslSetLongPressMultiSelectionListener(new RecyclerView.SeslLongPressMultiSelectionListener() {
//            public void onItemSelected(RecyclerView recyclerView, View view, int i, long j) {
//                if (ManageCategoriesActivity.this.mRecyclerView != null && ManageCategoriesActivity.this.mIsSelectMode && i < ManageCategoriesActivity.this.mListAdapter.getItemCount() && ((CategoryInfo) ManageCategoriesActivity.this.mListViewItems.get(i)).getIdCategory().intValue() > 3) {
//                    ManageCategoriesActivity.this.mListAdapter.toggleSelection(i);
//                    ManageCategoriesActivity.this.updateCheckBox();
//                    ManageCategoriesActivity.this.invalidateOptionsMenu();
//                    boolean isChecked = ManageCategoriesActivity.this.mListAdapter.isChecked(((CategoryInfo) ManageCategoriesActivity.this.mListViewItems.get(i)).getIdCategory().intValue());
//                    CheckBox checkBox = (CheckBox) view.findViewById(C0690R.C0693id.list_item_cb);
//                    if (checkBox != null) {
//                        checkBox.setChecked(isChecked);
//                    }
//                    view.setActivated(isChecked);
//                }
//            }
//
//            public void onLongPressMultiSelectionStarted(int i, int i2) {
//                boolean unused = ManageCategoriesActivity.this.mIsLongpress = true;
//            }
//
//            public void onLongPressMultiSelectionEnded(int i, int i2) {
//                boolean unused = ManageCategoriesActivity.this.mIsLongpress = false;
//            }
//        });
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle bundle) {
        Log.m26i(TAG, "onRestoreInstanceState");
        if (bundle != null) {
            this.mIsSelectMode = bundle.getBoolean("select_mode_state");
            if (this.mIsSelectMode) {
                this.mListAdapter.setSelectedCategoryArray((CategoryManagementAdapter.SerializableSparseBooleanArray) bundle.getSerializable(KEY_HASH_MAP_RESTORE));
                startSelectMode();
            }
        }
        super.onRestoreInstanceState(bundle);
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        if (this.mEnterMode != 13) {
            Log.m26i(TAG, "onSaveInstanceState");
            bundle.putSerializable(KEY_HASH_MAP_RESTORE, this.mListAdapter.getSelectedCategoryArrays());
            bundle.putBoolean("select_mode_state", this.mIsSelectMode);
        }
        super.onSaveInstanceState(bundle);
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == C0690R.C0693id.action_delete_category) {
            onOptionsItemSelected(this.mMenu.findItem(C0690R.C0693id.action_delete_label));
            return true;
        } else if (itemId != C0690R.C0693id.action_rename_category) {
            return true;
        } else {
            onOptionsItemSelected(this.mMenu.findItem(C0690R.C0693id.manage_label_popup_rename));
            return true;
        }
    }

    public void onItemClick(View view, int i) {
        Log.m26i(TAG, "onItemClick() - position : " + i);
        if (this.mEnterMode == 13) {
            if (i < this.mListViewItems.size() - 1) {
                long intValue = (long) this.mListViewItems.get(i).getIdCategory().intValue();
                UpdateTask updateTask = this.mUpdateCategory;
                if (updateTask == null || updateTask.getStatus() != AsyncTask.Status.RUNNING) {
                    this.mUpdateCategory = new UpdateTask();
                    this.mUpdateCategory.execute(new Long[]{Long.valueOf(intValue)});
                    SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_select_category), getResources().getString(C0690R.string.event_select_category));
                } else {
                    Log.m26i(TAG, "onItemClick() - mUpdateCategory is running.");
                    return;
                }
            } else {
                SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_select_category), getResources().getString(C0690R.string.event_add_category));
                if (!DialogFactory.isDialogVisible(getSupportFragmentManager(), DialogFactory.CATEGORY_RENAME)) {
                    addNewCategory();
                }
            }
        }
        if (this.mIsSelectMode && this.mListViewItems.size() != i && this.mListViewItems.get(i).getIdCategory().intValue() > 3) {
            this.mListAdapter.toggleSelection(i);
            if (this.mBottomNavigationView.getVisibility() == 8) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) this.mRecyclerView.getLayoutManager();
                if (i == this.mListAdapter.getItemCount() - 2) {
                    this.mRecyclerView.postDelayed(new Runnable() {
                        public final void run() {
                            ManageCategoriesActivity.this.lambda$onItemClick$2$ManageCategoriesActivity();
                        }
                    }, 50);
                } else if (i == linearLayoutManager.findLastVisibleItemPosition() || i == linearLayoutManager.findLastVisibleItemPosition() - 1) {
                    this.mRecyclerView.smoothScrollBy(getResources().getDimensionPixelSize(C0690R.dimen.fast_option_view_height), 0);
                }
            }
            updateCheckBox();
            invalidateOptionsMenu();
            this.mListAdapter.notifyItemChanged(i);
        }
    }

    public /* synthetic */ void lambda$onItemClick$2$ManageCategoriesActivity() {
        this.mRecyclerView.smoothScrollBy(getResources().getDimensionPixelSize(C0690R.dimen.fast_option_view_height), 0);
    }

    public boolean onItemLongClick(View view, int i) {
        if (!this.mIsSelectMode || this.mEnterMode == 13 || this.mListViewItems.size() <= i) {
            return true;
        }
        int intValue = this.mListViewItems.get(i).getIdCategory().intValue();
        Log.m26i(TAG, "onItemLongClick - id : " + intValue);
        if (!this.mListAdapter.isChecked(intValue)) {
            if (intValue > 3) {
                this.mListAdapter.toggleSelection(i);
            }
            updateCheckBox();
            invalidateOptionsMenu();
            this.mListAdapter.notifyItemChanged(i);
        }
//        this.mRecyclerView.seslStartLongPressMultiSelection();
        return true;
    }

    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        if (this.mItemTouchHelper != null && !this.mIsLongpress) {
            SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_manage_category), getResources().getString(C0690R.string.event_category_reorder));
            this.mItemTouchHelper.startDrag(viewHolder);
        }
    }

    private class UpdateTask extends AsyncTask<Long, Integer, Boolean> {
        private int mListIdMoveSize;
        private ProgressDialog mProgressDialog;
        private int mRecordingsMoved;
        private int mRecordingsMovedError;
        private int mRecordingsStorageError;

        private UpdateTask() {
            this.mProgressDialog = null;
            this.mRecordingsMoved = 0;
            this.mRecordingsMovedError = 0;
            this.mRecordingsStorageError = 0;
            this.mListIdMoveSize = 0;
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            super.onPreExecute();
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            Log.m29v(ManageCategoriesActivity.TAG, " UpdateTask - onPostExecute");
            super.onPostExecute(bool);
            if (this.mRecordingsStorageError > 0) {
                ManageCategoriesActivity manageCategoriesActivity = ManageCategoriesActivity.this;
                Toast.makeText(manageCategoriesActivity, manageCategoriesActivity.getString(C0690R.string.unable_to_move_recordings_storage_error), 1).show();
            } else if (this.mRecordingsMovedError == 0 && this.mRecordingsMoved == 0) {
                ManageCategoriesActivity manageCategoriesActivity2 = ManageCategoriesActivity.this;
                Toast.makeText(manageCategoriesActivity2, manageCategoriesActivity2.getString(C0690R.string.unable_to_move_recordings), 1).show();
            } else {
                int i = this.mRecordingsMovedError;
                if (i == 0) {
                    int i2 = this.mListIdMoveSize;
                    if (i2 > 1) {
                        ManageCategoriesActivity manageCategoriesActivity3 = ManageCategoriesActivity.this;
                        Toast.makeText(manageCategoriesActivity3, manageCategoriesActivity3.getString(C0690R.string.move_files_to_category, new Object[]{Integer.valueOf(i2)}), 1).show();
                    } else {
                        ManageCategoriesActivity manageCategoriesActivity4 = ManageCategoriesActivity.this;
                        Toast.makeText(manageCategoriesActivity4, manageCategoriesActivity4.getString(C0690R.string.move_file_to_category), 1).show();
                    }
                } else if (i == this.mListIdMoveSize) {
                    ManageCategoriesActivity manageCategoriesActivity5 = ManageCategoriesActivity.this;
                    Toast.makeText(manageCategoriesActivity5, manageCategoriesActivity5.getString(C0690R.string.unable_to_move_recordings), 1).show();
                } else {
                    int i3 = this.mRecordingsMoved;
                    if (i3 == 1) {
                        if (i == 1) {
                            ManageCategoriesActivity manageCategoriesActivity6 = ManageCategoriesActivity.this;
                            Toast.makeText(manageCategoriesActivity6, manageCategoriesActivity6.getString(C0690R.string.one_recording_moved_one_error), 1).show();
                        } else {
                            ManageCategoriesActivity manageCategoriesActivity7 = ManageCategoriesActivity.this;
                            Toast.makeText(manageCategoriesActivity7, manageCategoriesActivity7.getString(C0690R.string.one_recording_moved_many_error, new Object[]{Integer.valueOf(i)}), 1).show();
                        }
                    } else if (i3 > 1) {
                        if (i == 1) {
                            ManageCategoriesActivity manageCategoriesActivity8 = ManageCategoriesActivity.this;
                            Toast.makeText(manageCategoriesActivity8, manageCategoriesActivity8.getString(C0690R.string.many_recording_moved_one_error, new Object[]{Integer.valueOf(i3)}), 1).show();
                        } else {
                            ManageCategoriesActivity manageCategoriesActivity9 = ManageCategoriesActivity.this;
                            Toast.makeText(manageCategoriesActivity9, manageCategoriesActivity9.getString(C0690R.string.many_recording_moved_many_error, new Object[]{Integer.valueOf(i3), Integer.valueOf(this.mRecordingsMovedError)}), 1).show();
                        }
                    }
                }
            }
            CursorProvider.getInstance().resetSearchTag();
            if (VoiceNoteApplication.getScene() != 3) {
                VoiceNoteObservable.getInstance().notifyObservers(14);
                VoiceNoteObservable.getInstance().notifyObservers(7);
            } else {
                VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.UPDATE_CATEGORY_AFTER_MOVE));
            }
            ProgressDialog progressDialog = this.mProgressDialog;
            if (progressDialog != null) {
                progressDialog.dismiss();
                this.mProgressDialog = null;
            }
            ManageCategoriesActivity.this.finish();
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Integer... numArr) {
            Log.m29v(ManageCategoriesActivity.TAG, " update categories operation is in progress - " + numArr[0] + " / " + this.mListIdMoveSize);
            super.onProgressUpdate(numArr);
            if (this.mListIdMoveSize > 1) {
                if (this.mProgressDialog == null) {
                    this.mProgressDialog = new ProgressDialog(ManageCategoriesActivity.this);
                    this.mProgressDialog.setMessage(ManageCategoriesActivity.this.getString(C0690R.string.move));
                    this.mProgressDialog.setProgressStyle(1);
                    this.mProgressDialog.setCancelable(false);
                    if (this.mProgressDialog.getWindow() != null) {
                        this.mProgressDialog.getWindow().addFlags(128);
                    }
                    this.mProgressDialog.setMax(this.mListIdMoveSize);
                    if (!ManageCategoriesActivity.this.isFinishing()) {
                        this.mProgressDialog.show();
                    }
                }
                this.mProgressDialog.setProgress(numArr[0].intValue());
            }
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(Long... lArr) {
            long longValue = lArr[0].longValue();
            if (ManageCategoriesActivity.this.mListIdsMove == null) {
                Log.m32w(ManageCategoriesActivity.TAG, "updateCategories list is null");
                return false;
            } else if (ManageCategoriesActivity.this.isDestroyed()) {
                Log.m32w(ManageCategoriesActivity.TAG, "ManageCategoriesActivity is destroyed");
                return false;
            } else {
                Log.m19d(ManageCategoriesActivity.TAG, "move record to idCategory = " + longValue);
                if (longValue == 0) {
                    longValue = -2;
                } else if (longValue == -2) {
                    longValue = 0;
                }
                this.mListIdMoveSize = ManageCategoriesActivity.this.mListIdsMove.size();
                List<Long> filesInCategory = DBProvider.getInstance().getFilesInCategory(longValue);
                ArrayList arrayList = new ArrayList();
                if (filesInCategory != null) {
                    Iterator it = ManageCategoriesActivity.this.mListIdsMove.iterator();
                    int i = 1;
                    while (it.hasNext()) {
                        long longValue2 = ((Long) it.next()).longValue();
                        if (filesInCategory.contains(Long.valueOf(longValue2))) {
                            this.mRecordingsMovedError++;
                        } else {
                            arrayList.add(ContentProviderOperation.newUpdate(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI).withValue(DialogFactory.BUNDLE_LABEL_ID, Long.valueOf(longValue)).withSelection("_id=?", new String[]{String.valueOf(longValue2)}).build());
                        }
                        int i2 = i + 1;
                        publishProgress(new Integer[]{Integer.valueOf(i)});
                        if (arrayList.size() == 500) {
                            applyBatch(arrayList);
                            arrayList.clear();
                        }
                        i = i2;
                    }
                    if (arrayList.size() > 0) {
                        applyBatch(arrayList);
                        arrayList.clear();
                    }
                    filesInCategory.clear();
                }
                return true;
            }
        }

        private void applyBatch(ArrayList<ContentProviderOperation> arrayList) {
            try {
                for (ContentProviderResult contentProviderResult : ManageCategoriesActivity.this.getContentResolver().applyBatch("media", arrayList)) {
                    if (contentProviderResult.count.intValue() == 1) {
                        this.mRecordingsMoved++;
                    } else {
                        this.mRecordingsStorageError++;
                    }
                }
            } catch (RemoteException e) {
                Log.m22e(ManageCategoriesActivity.TAG, "AUTHORITY RemoteException " + Arrays.toString(e.getStackTrace()));
            } catch (OperationApplicationException e2) {
                Log.m22e(ManageCategoriesActivity.TAG, "OperationApplicationException " + Arrays.toString(e2.getStackTrace()));
            }
        }
    }

    private class DeleteTask extends AsyncTask<Long, Integer, Boolean> {
        private int mListSize;
        private ProgressDialog mProgressDialog;

        private DeleteTask() {
            this.mListSize = 0;
            this.mProgressDialog = null;
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            super.onPreExecute();
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            ProgressDialog progressDialog = this.mProgressDialog;
            if (progressDialog != null) {
                progressDialog.dismiss();
                this.mProgressDialog = null;
            }
            ManageCategoriesActivity.this.cancelDeleteDialog();
            MouseKeyboardProvider.getInstance().changePointerIcon(ManageCategoriesActivity.this.getWindow().getDecorView(), ManageCategoriesActivity.this.getWindow().getDecorView().getContext(), 1);
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Integer... numArr) {
            Log.m29v(ManageCategoriesActivity.TAG, " delete categories operation is in progress - " + numArr[0] + " / " + this.mListSize);
            super.onProgressUpdate(numArr);
            if (this.mListSize > 1) {
                if (this.mProgressDialog == null) {
                    this.mProgressDialog = new ProgressDialog(ManageCategoriesActivity.this);
                    this.mProgressDialog.setMessage(ManageCategoriesActivity.this.getString(C0690R.string.delete));
                    this.mProgressDialog.setProgressStyle(1);
                    this.mProgressDialog.setCancelable(false);
                    if (this.mProgressDialog.getWindow() != null) {
                        this.mProgressDialog.getWindow().addFlags(128);
                    }
                    this.mProgressDialog.setMax(this.mListSize);
                    this.mProgressDialog.show();
                }
                this.mProgressDialog.setProgress(numArr[0].intValue());
                MouseKeyboardProvider.getInstance().changePointerIcon(ManageCategoriesActivity.this.getWindow().getDecorView(), ManageCategoriesActivity.this.getWindow().getDecorView().getContext(), 4);
            }
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(Long... lArr) {
            if (ManageCategoriesActivity.this.mListAdapter == null) {
                Log.m32w(ManageCategoriesActivity.TAG, "delete Category list is null");
                return false;
            } else if (ManageCategoriesActivity.this.isDestroyed()) {
                Log.m32w(ManageCategoriesActivity.TAG, "ManageCategoriesActivity is destroyed");
                return false;
            } else {
                ArrayList arrayList = new ArrayList();
                long[] arrayIds = ManageCategoriesActivity.this.mListAdapter.getArrayIds();
                if (arrayIds != null && arrayIds.length > 0) {
                    this.mListSize = arrayIds.length;
                    int length = arrayIds.length;
                    int i = 0;
                    int i2 = 1;
                    while (i < length) {
                        arrayList.add(ContentProviderOperation.newUpdate(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI).withValue(DialogFactory.BUNDLE_LABEL_ID, 0).withSelection("label_id == ? AND (_data LIKE '%.amr' or (_data LIKE '%.m4a' and recordingtype == '1'))", new String[]{String.valueOf(arrayIds[i])}).build());
                        int i3 = i2 + 1;
                        publishProgress(new Integer[]{Integer.valueOf(i2)});
                        if (arrayList.size() == 500) {
                            applyBatch(arrayList);
                            arrayList.clear();
                        }
                        i++;
                        i2 = i3;
                    }
                    if (arrayList.size() > 0) {
                        applyBatch(arrayList);
                        arrayList.clear();
                    }
                    DataRepository.getInstance().getCategoryRepository().deleteColums(arrayIds);
                }
                return true;
            }
        }

        private void applyBatch(ArrayList<ContentProviderOperation> arrayList) {
            try {
                ManageCategoriesActivity.this.getContentResolver().applyBatch("media", arrayList);
            } catch (OperationApplicationException | RemoteException e) {
                Log.m22e(ManageCategoriesActivity.TAG, e.toString());
            }
        }
    }

    /* access modifiers changed from: private */
    public void cancelDeleteDialog() {
        DataRepository.getInstance().getCategoryRepository().deleteCompleted();
        if (this.mEnterMode == 12) {
            finish();
            return;
        }
        this.mListAdapter.removeItems();
        this.mListAdapter.notifyDataSetChanged();
        updateCheckBox();
        invalidateOptionsMenu();
    }

    private void updateBottomButtonShape() {
        if (this.mBottomNavigationView == null) {
            return;
        }
        if (Settings.isEnabledShowButtonBG()) {
            this.mBottomNavigationView.setItemBackgroundResource(C0690R.C0692drawable.bottom_navigation_item_background);
            this.mBottomNavigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(C0690R.C0691color.control_activate_color)));
            return;
        }
        this.mBottomNavigationView.setItemBackgroundResource(C0690R.C0692drawable.bottom_button_ripple);
    }

    private class RoundedDecoration extends RecyclerView.ItemDecoration {
        private RoundedDecoration() {
        }

        public void seslOnDispatchDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
//            super.seslOnDispatchDraw(canvas, recyclerView, state);
//            ManageCategoriesActivity.this.mSeslListRoundedCorner.drawRoundedCorner(canvas);
        }
    }
}
