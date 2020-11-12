package com.sec.android.app.voicenote.p007ui.actionbar;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DataRepository;
import com.sec.android.app.voicenote.common.util.EmoticonUtils;
import com.sec.android.app.voicenote.common.util.Trace;
import com.sec.android.app.voicenote.common.util.TrashHelper;
import com.sec.android.app.voicenote.main.VNMainActivity;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.p007ui.pager.ModePager;
import com.sec.android.app.voicenote.provider.CheckedItemProvider;
import com.sec.android.app.voicenote.provider.ContactUsProvider;
import com.sec.android.app.voicenote.provider.ContextMenuProvider;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.DBProvider;
import com.sec.android.app.voicenote.provider.DesktopModeProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.HWKeyboardProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.MouseKeyboardProvider;
import com.sec.android.app.voicenote.provider.PermissionProvider;
import com.sec.android.app.voicenote.provider.PhoneStateProvider;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.SecureFolderProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.provider.UPSMProvider;
import com.sec.android.app.voicenote.provider.UpdateProvider;
import com.sec.android.app.voicenote.provider.ViewProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.MetadataRepository;
import com.sec.android.app.voicenote.uicore.FragmentController;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

/* renamed from: com.sec.android.app.voicenote.ui.actionbar.MainActionbar */
public class MainActionbar implements View.OnClickListener, FragmentController.OnSceneChangeListener, Observer, Engine.OnEngineListener, UpdateProvider.StubListener, DialogFactory.DialogResultListener, BottomNavigationView.OnNavigationItemSelectedListener, TrashHelper.OnTrashProgressListener {
    private static final String DISABLE_EMOTICON_FLAG = "disableEmoticonInput=true";
    private static final String DISABLE_GIF_FLAG = "disableGifKeyboard=true";
    private static final String DISABLE_LIVE_MESSAGE = "disableLiveMessage=true";
    private static final String DISABLE_STICKER_FLAG = "disableSticker=true";
    private static final String IS_RELEASED_FINGER = "is_released_finger";
    private static final String IS_SEARCH_FOCUS = "is_search_focus";
    private static final String IS_SHARE_PRESS = "is_share_press";
    private static final int MAX_LENGTH = 50;
    private static final int MIN_TIME_EDIT_PROGRESS_DIALOG = 300000;
    private static final String NEED_TO_SHOW_NAVIGATIONBAR_AGAIN = "need_to_show_navigationbar_again";
    private static final String PRIVATE_INTENT_ACTION = "voicenote.intent.action.privatebox";
    private static final String SELECT_MODE = "select_mode";
    private static final String TAG = "MainActionbar";
    public static final int TYPE_NOT_SELECTED = 0;
    public static final int TYPE_SELECTED_ITEM = 1;
    public static final int TYPE_SELECTED_ITEM_LONGPRESS = 3;
    public static final int TYPE_SELECTED_MULTI = 2;
    public static final int TYPE_SELECTED_MULTI_LONGPRESS = 4;
    public static final int TYPE_SELECTED_SHARE = 5;
    private static final String VOICEINPUT_OFF = "disableVoiceInput=true";
    private AppBarLayout appBarLayout;
    private ActionBar mActionbar;
    private AppCompatActivity mActivity;
    /* access modifiers changed from: private */
    public BottomNavigationView mBottomNavigationView;
    private CheckBox mCheckBox = null;
    private LinearLayout mCheckBoxContainer;
    private TextView mCheckBoxCountView = null;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private float mHeightPercent = 0.3967f;
    private boolean mIsBackPress = false;
    private boolean mIsFirstTimeAddListMenu = false;
    private boolean mIsInvalidateMenu = false;
    /* access modifiers changed from: private */
    public boolean mIsReleasedFinger = false;
    private boolean mIsRenameFile = false;
    private boolean mIsSearchFocus = false;
    private boolean mIsSharePress = false;
    /* access modifiers changed from: private */
    public boolean mIsShowingBottomNavigationBar = false;
    private FrameLayout mListView;
    private Menu mMenu = null;
    private boolean mNeedToShowBottomNavigationBarAgain = false;
    /* access modifiers changed from: private */
    public VoiceNoteObservable mObservable;
    private int mPrevScene;
    private Resources mResource;
    private RunOptionMenu mRunOptionMenu;
    /* access modifiers changed from: private */
    public int mScene;
    /* access modifiers changed from: private */
    public SearchView mSearchView = null;
    private Toolbar mToolbar;
    private VNMainActivity mVNMainActivity = null;
    private OffsetUpdateListener offsetUpdateListener;
    private final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        /* access modifiers changed from: private */
        public String mNewText;
        @SuppressLint({"HandlerLeak"})
        private final Handler mTimer = new Handler() {
            public void handleMessage(Message message) {
                Log.m26i(MainActionbar.TAG, "handleMessage - text change timer");
                if (MainActionbar.this.mScene != 7) {
                    Log.m22e(MainActionbar.TAG, "handleMessage - text change timer: exited Search Scene");
                    return;
                }
//                CursorProvider.getInstance().setSearchTag(C08632.this.mNewText);
                if (MainActionbar.this.mObservable == null) {
                    VoiceNoteObservable unused = MainActionbar.this.mObservable = VoiceNoteObservable.getInstance();
                }
                MainActionbar.this.mObservable.notifyObservers(Integer.valueOf(Event.SEARCH_RECORDINGS));
                MainActionbar.this.mObservable.notifyObservers(Integer.valueOf(Event.SEARCH_TEXT_CHANGED));
            }
        };

        public boolean onQueryTextSubmit(String str) {
            Log.m26i(MainActionbar.TAG, "onQueryTextSubmit - query : " + str);
            CursorProvider.getInstance().setSearchTag(str);
            DataRepository.getInstance().getLabelSearchRepository().insertLabel(str, System.currentTimeMillis());
            MainActionbar.this.mSearchView.clearFocus();
            MainActionbar.this.mSearchView.setFocusable(false);
            MainActionbar.this.mObservable.notifyObservers(Integer.valueOf(Event.SEARCH_RECORDINGS));
            MainActionbar.this.mObservable.notifyObservers(Integer.valueOf(Event.SEARCH_TEXT_CHANGED));
            return false;
        }

        public boolean onQueryTextChange(String str) {
            Log.m26i(MainActionbar.TAG, "onQueryTextChange - newText : " + str);
            this.mNewText = str;
            this.mTimer.removeMessages(0);
            this.mTimer.sendEmptyMessageDelayed(0, 300);
            return false;
        }
    };

    public MainActionbar(AppCompatActivity appCompatActivity) {
        Trace.beginSection("VNMainActionBar()");
        this.mActivity = appCompatActivity;
        this.mResource = this.mActivity.getResources();
        AppCompatActivity appCompatActivity2 = this.mActivity;
        if (appCompatActivity2 instanceof VNMainActivity) {
            this.mVNMainActivity = (VNMainActivity) appCompatActivity2;
        }
        this.mToolbar = (Toolbar) appCompatActivity.findViewById(C0690R.C0693id.toolbar);
        this.mCollapsingToolbarLayout = (CollapsingToolbarLayout) appCompatActivity.findViewById(C0690R.C0693id.collapsing_app_bar);
        this.appBarLayout = (AppBarLayout) appCompatActivity.findViewById(C0690R.C0693id.app_bar_layout);
        appCompatActivity.setSupportActionBar(this.mToolbar);
        this.mActionbar = appCompatActivity.getSupportActionBar();
        ActionBar actionBar = this.mActionbar;
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(this.mResource.getColor(C0690R.C0691color.main_window_bg, (Resources.Theme) null)));
        }
        this.mScene = 1;
        this.mPrevScene = 0;
        Trace.beginSection("VNMainActionBar.initToolbar");
        this.mMenu = this.mToolbar.getMenu();
        if (VoiceNoteApplication.getScene() == 0 || VoiceNoteApplication.getScene() == 1) {
            appCompatActivity.getMenuInflater().inflate(C0690R.C0695menu.main_menu, this.mMenu);
            int recordFileCount = CursorProvider.getInstance().getRecordFileCount();
            Log.m26i(TAG, "initToolbar - Item Size : " + recordFileCount);
            if (recordFileCount <= 0) {
                this.mMenu.removeItem(C0690R.C0693id.list_recordings);
            }
        }
        Trace.endSection();
        FragmentController.getInstance().registerSceneChangeListener(this);
        Engine.getInstance().registerListener(this);
        TrashHelper.getInstance().registerOnTrashProgressListener(this);
        this.mObservable = VoiceNoteObservable.getInstance();
        this.mObservable.addObserver(this);
        this.mRunOptionMenu = RunOptionMenu.getInstance();
        this.mRunOptionMenu.setContext(this.mActivity);
        this.mBottomNavigationView = (BottomNavigationView) this.mActivity.findViewById(C0690R.C0693id.bottom_navigation);
        BottomNavigationView bottomNavigationView = this.mBottomNavigationView;
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnNavigationItemSelectedListener(this);
        }
        updateBottomButtonShape();
        this.mListView = (FrameLayout) this.mActivity.findViewById(C0690R.C0693id.main_list);
        Trace.endSection();
    }

    public MainActionbar(AppCompatActivity appCompatActivity, int i, Bundle bundle) {
        this.mActivity = appCompatActivity;
        this.mResource = this.mActivity.getResources();
        AppCompatActivity appCompatActivity2 = this.mActivity;
        if (appCompatActivity2 instanceof VNMainActivity) {
            this.mVNMainActivity = (VNMainActivity) appCompatActivity2;
        }
        if (bundle != null) {
            onRestoreInstanceState(bundle);
        }
        this.mToolbar = (Toolbar) appCompatActivity.findViewById(C0690R.C0693id.toolbar);
        this.mCollapsingToolbarLayout = (CollapsingToolbarLayout) appCompatActivity.findViewById(C0690R.C0693id.collapsing_app_bar);
        this.appBarLayout = (AppBarLayout) appCompatActivity.findViewById(C0690R.C0693id.app_bar_layout);
        appCompatActivity.setSupportActionBar(this.mToolbar);
        this.mActionbar = appCompatActivity.getSupportActionBar();
        ActionBar actionBar = this.mActionbar;
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(this.mResource.getColor(C0690R.C0691color.main_window_bg, (Resources.Theme) null)));
        }
        onSceneChange(i);
        FragmentController.getInstance().registerSceneChangeListener(this);
        Engine.getInstance().registerListener(this);
        TrashHelper.getInstance().registerOnTrashProgressListener(this);
        this.mObservable = VoiceNoteObservable.getInstance();
        this.mObservable.addObserver(this);
        this.mRunOptionMenu = RunOptionMenu.getInstance();
        this.mRunOptionMenu.setContext(this.mActivity);
        this.mBottomNavigationView = (BottomNavigationView) this.mActivity.findViewById(C0690R.C0693id.bottom_navigation);
        BottomNavigationView bottomNavigationView = this.mBottomNavigationView;
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnNavigationItemSelectedListener(this);
        }
        updateBottomButtonShape();
        this.mListView = (FrameLayout) this.mActivity.findViewById(C0690R.C0693id.main_list);
    }

    private void onRestoreInstanceState(Bundle bundle) {
        MouseKeyboardProvider.getInstance().setSelectModeByEditOption(bundle.getBoolean(SELECT_MODE));
        this.mNeedToShowBottomNavigationBarAgain = bundle.getBoolean(NEED_TO_SHOW_NAVIGATIONBAR_AGAIN);
        this.mIsReleasedFinger = bundle.getBoolean(IS_RELEASED_FINGER);
        this.mIsSharePress = bundle.getBoolean(IS_SHARE_PRESS);
        this.mIsSearchFocus = bundle.getBoolean(IS_SEARCH_FOCUS);
    }

    public void onDestroy() {
        Log.m26i(TAG, "onDestroy");
        AppCompatActivity appCompatActivity = this.mActivity;
        if (appCompatActivity != null) {
            DialogFactory.clearDialogByTag(appCompatActivity.getSupportFragmentManager(), DialogFactory.EDIT_PROGRESS_DIALOG);
            this.mActivity = null;
            this.mMenu = null;
            this.mVNMainActivity = null;
            FragmentController.getInstance().unregisterSceneChangeListener(this);
            Engine.getInstance().unregisterListener(this);
        }
        ActionBar actionBar = this.mActionbar;
        if (actionBar != null) {
            View customView = actionBar.getCustomView();
            if (!(customView == null || customView.getTag() == null)) {
                ((Animator) customView.getTag()).cancel();
            }
            this.mActionbar = null;
        }
        VoiceNoteObservable voiceNoteObservable = this.mObservable;
        if (voiceNoteObservable != null) {
            voiceNoteObservable.deleteObserver(this);
            this.mObservable = null;
        }
        RunOptionMenu runOptionMenu = this.mRunOptionMenu;
        if (runOptionMenu != null) {
            runOptionMenu.onDestroy();
            this.mRunOptionMenu = null;
        }
        if (this.mBottomNavigationView != null) {
            this.mBottomNavigationView = null;
        }
        if (this.mListView != null) {
            this.mListView = null;
        }
        if (this.mToolbar != null) {
            this.mToolbar = null;
        }
        if (this.mCollapsingToolbarLayout != null) {
            this.mCollapsingToolbarLayout = null;
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        bundle.putBoolean(SELECT_MODE, MouseKeyboardProvider.getInstance().getSelectModeByEditOption());
        bundle.putBoolean(NEED_TO_SHOW_NAVIGATIONBAR_AGAIN, this.mIsShowingBottomNavigationBar);
        bundle.putBoolean(IS_RELEASED_FINGER, this.mIsReleasedFinger);
        bundle.putBoolean(IS_SHARE_PRESS, this.mIsSharePress);
        SearchView searchView = this.mSearchView;
        if (searchView != null) {
            bundle.putBoolean(IS_SEARCH_FOCUS, searchView.hasFocus());
        }
    }

    private int getTabCount() {
        if (DesktopModeProvider.isDesktopMode()) {
            return 1;
        }
        int i = !VoiceNoteFeature.FLAG_SUPPORT_INTERVIEW ? 2 : 3;
        return !VoiceNoteFeature.FLAG_SUPPORT_VOICE_MEMO(this.mActivity) ? i - 1 : i;
    }

    private void showMain() {
        Log.m19d(TAG, "showMain ");
        Trace.beginSection("VNMainActionBar.showMain");
        ActionBar actionBar = this.mActionbar;
        if (actionBar == null || this.mActivity == null) {
            Log.m32w(TAG, "showMain - Actionbar : " + this.mActionbar + " Activity : " + this.mActivity);
        } else {
            actionBar.setDisplayShowHomeEnabled(false);
            this.mActionbar.setDisplayHomeAsUpEnabled(false);
            this.mActionbar.setDisplayUseLogoEnabled(false);
            this.mActionbar.setDisplayShowTitleEnabled(true);
            this.mActionbar.setTitle((int) C0690R.string.app_name);
            if (Engine.getInstance().getRecorderState() == 1 && Engine.getInstance().getPlayerState() == 1) {
                ModePager.getInstance().start();
            }
            this.mActivity.invalidateOptionsMenu();
            this.mActionbar.show();
            int i = this.mPrevScene;
            boolean z = i == 2 || i == 13;
            if (Settings.getIntSettings("record_mode", 1) == 6 || getTabCount() == 1) {
                ModePager.getInstance().hideTab(false);
            } else {
                ModePager.getInstance().showTab(z);
                ModePager.getInstance().setSpaceTab();
            }
            ModePager.getInstance().showContentTab(z);
        }
        Trace.endSection();
    }

    private void showRecord() {
        Log.m26i(TAG, "showRecord ");
        if (this.mActionbar == null || this.mActivity == null) {
            Log.m32w(TAG, "showMain - Actionbar : " + this.mActionbar + " Activity : " + this.mActivity);
            return;
        }
        ModePager.getInstance().stop();
        this.mActionbar.setDisplayOptions(8);
        String str = null;
        int intSettings = Settings.getIntSettings("record_mode", 1);
        if (intSettings == 1) {
            str = this.mActivity.getString(C0690R.string.normal_mode);
        } else if (intSettings == 2) {
            str = this.mActivity.getString(C0690R.string.interview_mode);
        } else if (intSettings == 4) {
            str = this.mActivity.getString(C0690R.string.speech_to_text_mode);
        } else if (intSettings == 6 && (str = Engine.getInstance().getUserSettingName()) == null) {
            str = DBProvider.getInstance().createNewFileName(0);
        }
        this.mActionbar.setTitle((CharSequence) str);
        this.mActionbar.setHomeAsUpIndicator(getHomeIcon());
        this.mActionbar.setDisplayOptions(4, 4);
        this.mActionbar.setDisplayHomeAsUpEnabled(true);
        this.mActionbar.setDisplayShowHomeEnabled(false);
        this.mActionbar.setDisplayUseLogoEnabled(false);
        this.mActivity.invalidateOptionsMenu();
        this.mActionbar.show();
    }

    private void showLibrary() {
        Log.m26i(TAG, "showLibrary ");
        if (this.mActionbar != null && this.mActivity != null) {
            ModePager.getInstance().stop();
            ModePager.getInstance().hideTab(true);
            ModePager.getInstance().hideContentTab(true);
            showLibraryInternal();
        }
    }

    private void showTrash() {
        Log.m26i(TAG, "showLibrary ");
        if (this.mActionbar != null && this.mActivity != null) {
            ModePager.getInstance().stop();
            ModePager.getInstance().hideTab(true);
            ModePager.getInstance().hideContentTab(true);
            this.mActionbar.setDisplayOptions(8);
            this.mActionbar.setTitle((CharSequence) this.mActivity.getString(C0690R.string.trash_header));
            int numberTrashItem = TrashHelper.getInstance().getNumberTrashItem(this.mActivity);
            if (numberTrashItem > 0) {
                this.mActionbar.setSubtitle((CharSequence) this.mActivity.getResources().getQuantityString(C0690R.plurals.trash_sub_header, numberTrashItem, new Object[]{Integer.valueOf(numberTrashItem)}));
            }
            this.mActionbar.setHomeAsUpIndicator(getHomeIcon());
            this.mActionbar.setDisplayOptions(4, 4);
            this.mActionbar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setBackgroundActionbar(boolean z) {
        ActionBar actionBar = this.mActionbar;
        if (actionBar != null && this.mActivity != null) {
            if (z) {
                actionBar.setBackgroundDrawable(new ColorDrawable(this.mResource.getColor(C0690R.C0691color.actionbar_color_bg, (Resources.Theme) null)));
                this.mActivity.getWindow().setStatusBarColor(this.mResource.getColor(C0690R.C0691color.actionbar_color_bg, (Resources.Theme) null));
                this.mActivity.getWindow().setNavigationBarColor(this.mResource.getColor(C0690R.C0691color.actionbar_color_bg, (Resources.Theme) null));
                this.appBarLayout.setBackgroundDrawable(new ColorDrawable(this.mResource.getColor(C0690R.C0691color.actionbar_color_bg, (Resources.Theme) null)));
                return;
            }
            actionBar.setBackgroundDrawable(new ColorDrawable(this.mResource.getColor(C0690R.C0691color.main_window_bg, (Resources.Theme) null)));
            this.mActivity.getWindow().setStatusBarColor(this.mResource.getColor(C0690R.C0691color.main_window_bg, (Resources.Theme) null));
            this.mActivity.getWindow().setNavigationBarColor(this.mResource.getColor(C0690R.C0691color.main_window_bg, (Resources.Theme) null));
            this.appBarLayout.setBackgroundDrawable(new ColorDrawable(this.mResource.getColor(C0690R.C0691color.main_window_bg, (Resources.Theme) null)));
        }
    }

    private void showLibraryInternal() {
        AppCompatActivity appCompatActivity;
        Log.m26i(TAG, "showLibraryInternal ");
        if (this.mActionbar != null && (appCompatActivity = this.mActivity) != null) {
            appCompatActivity.invalidateOptionsMenu();
            this.mActionbar.setDisplayOptions(8);
            this.mActionbar.setTitle((CharSequence) this.mActivity.getString(C0690R.string.list));
            this.mActionbar.setHomeAsUpIndicator(getHomeIcon());
            this.mActionbar.setDisplayOptions(4, 4);
            this.mActionbar.setDisplayHomeAsUpEnabled(true);
            this.mActionbar.show();
        }
    }

    private void updateCheckBox(boolean z) {
        int i;
        int i2;
        String str;
        Log.m26i(TAG, "updateCheckBox ");
        if (z) {
            i = CheckedItemProvider.getCheckedItemCount();
            i2 = TrashHelper.getInstance().getNumberTrashItem(this.mActivity);
        } else {
            i = CheckedItemProvider.getCheckedItemCount();
            i2 = CursorProvider.getInstance().getItemCount();
        }
        Log.m26i(TAG, "updateCheckBox count : " + i + " total : " + i2);
        CheckBox checkBox = this.mCheckBox;
        if (checkBox != null) {
            if (i2 == i || i2 == 1) {
                this.mCheckBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
        }
        if (this.mCheckBoxCountView != null) {
            Log.m29v(TAG, "selected count : " + i);
            if (i == 0) {
                str = this.mResource.getString(C0690R.string.select_recordings);
            } else {
                str = this.mResource.getString(C0690R.string.selected, new Object[]{Integer.valueOf(i)});
            }
            setContentDescription();
            this.mCheckBoxCountView.setText(str);
            CollapsingToolbarLayout collapsingToolbarLayout = this.mCollapsingToolbarLayout;
            if (collapsingToolbarLayout != null) {
                collapsingToolbarLayout.setTitle(str);
            }
        }
    }

    private void showChildList() {
        Log.m26i(TAG, "showChildList ");
        if (this.mActionbar != null && this.mActivity != null) {
            ModePager.getInstance().stop();
            ModePager.getInstance().hideTab(true);
            this.mActionbar.setDisplayOptions(8);
            this.mActionbar.setTitle((CharSequence) DataRepository.getInstance().getCategoryRepository().getCurrentCategoryTitle(this.mActivity));
            this.mActionbar.setHomeAsUpIndicator(getHomeIcon());
            this.mActionbar.setDisplayOptions(4, 4);
            this.mActionbar.setDisplayHomeAsUpEnabled(true);
            this.mActionbar.show();
        }
    }

    private void showSelect(int i) {
        AppCompatActivity appCompatActivity;
        Log.m26i(TAG, "showSelect ");
        if (this.mActionbar != null && (appCompatActivity = this.mActivity) != null) {
            View inflate = LayoutInflater.from(appCompatActivity).inflate(C0690R.layout.optionbar_edit_title, (ViewGroup) null);
            this.mCheckBoxCountView = (TextView) inflate.findViewById(C0690R.C0693id.optionbar_title);
            ViewProvider.setMaxFontSize(this.mActivity, this.mCheckBoxCountView);
            this.mCheckBoxContainer = (LinearLayout) inflate.findViewById(C0690R.C0693id.checkbox_container);
            this.mCheckBox = (CheckBox) inflate.findViewById(C0690R.C0693id.optionbar_checkbox);
//            if (Build.VERSION.SEM_INT < 2401) {
//                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mCheckBox.getLayoutParams();
//                layoutParams.topMargin = this.mResource.getDimensionPixelSize(C0690R.dimen.actionbar_checkbox_top_margin);
//                this.mCheckBox.setLayoutParams(layoutParams);
//            }
            this.mCheckBox.setOnClickListener(this);
            this.mActionbar.setCustomView(inflate, new ActionBar.LayoutParams(-1, -1));
            this.mActionbar.setDisplayOptions(16);
            this.mActionbar.show();
            if (i == 14) {
                updateCheckBox(true);
            } else {
                updateCheckBox(false);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void setContentDescription() {
        if (this.mCheckBoxContainer != null) {
            int checkedItemCount = CheckedItemProvider.getCheckedItemCount();
            int itemCount = CursorProvider.getInstance().getItemCount();
            String string = this.mResource.getString(C0690R.string.tts_double_tap_select_all);
            String string2 = this.mResource.getString(C0690R.string.tts_double_tap_deselect_all);
            String string3 = this.mResource.getString(C0690R.string.tts_tick_box_t_tts);
            String string4 = this.mResource.getString(C0690R.string.tts_not_ticked_t_tts);
            String string5 = this.mResource.getString(C0690R.string.tts_ticked_t_tts);
            String string6 = this.mResource.getString(C0690R.string.tts_nothing_selected);
            String string7 = this.mResource.getString(C0690R.string.tts_selected, new Object[]{Integer.valueOf(checkedItemCount)});
            StringBuilder sb = new StringBuilder();
            if (checkedItemCount == 0) {
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
            } else if (checkedItemCount < itemCount) {
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

    private void showEdit() {
        Log.m26i(TAG, "showEdit");
        if (this.mActionbar != null && this.mActivity != null) {
            ModePager.getInstance().stop();
            this.mActionbar.setDisplayOptions(8);
            this.mActionbar.setTitle((int) C0690R.string.edit);
            this.mActionbar.setHomeAsUpIndicator(getHomeIcon());
            this.mActionbar.setDisplayOptions(4, 4);
            this.mActionbar.setDisplayHomeAsUpEnabled(true);
            this.mActionbar.show();
        }
    }

    private void showPlay() {
        Log.m26i(TAG, "showPlay ");
        if (this.mActionbar != null && this.mActivity != null) {
            ModePager.getInstance().stop();
            this.mActionbar.setDisplayOptions(8);
            MetadataRepository instance = MetadataRepository.getInstance();
            instance.setPath(Engine.getInstance().getPath());
            this.mActionbar.setTitle((CharSequence) instance.getTitle());
            this.mActionbar.setHomeAsUpIndicator(getHomeIcon());
            this.mActionbar.setDisplayOptions(4, 4);
            this.mActionbar.setDisplayHomeAsUpEnabled(true);
            this.mActivity.invalidateOptionsMenu();
            this.mActionbar.show();
        }
    }

    private void showSearch() {
        Log.m26i(TAG, "showSearch ");
        if (this.mActionbar == null || this.mActivity == null) {
            Log.m32w(TAG, "showSearch - Actionbar : " + this.mActionbar + " Activity : " + this.mActivity);
            return;
        }
        ModePager.getInstance().stop();
        this.mActionbar.setHomeButtonEnabled(false);
        this.mActionbar.setDisplayHomeAsUpEnabled(false);
        this.mActionbar.setDisplayUseLogoEnabled(false);
        this.mActionbar.setDisplayShowTitleEnabled(false);
        this.mActionbar.setDisplayShowCustomEnabled(false);
        this.mActionbar.setTitle((CharSequence) null);
        this.mActivity.invalidateOptionsMenu();
        this.mActionbar.show();
    }

    private void hide() {
        Log.m26i(TAG, "hide ");
        this.mActionbar.hide();
    }

    public void onClick(View view) {
        VNMainActivity vNMainActivity;
        if (this.mActivity == null || (vNMainActivity = this.mVNMainActivity) == null || !vNMainActivity.isActivityResumed()) {
            Log.m32w(TAG, "onClick - activity is not resumed");
        } else if (view.getId() == C0690R.C0693id.optionbar_checkbox && (view instanceof CheckBox)) {
            if (((CheckBox) view).isChecked()) {
                if (this.mScene == 14) {
                    this.mObservable.notifyObservers(Integer.valueOf(Event.TRASH_SELECT_ALL));
                    updateCheckBox(true);
                    showBottomMenu(this.mBottomNavigationView);
                } else {
                    Iterator<Long> it = CursorProvider.getInstance().getIDs().iterator();
                    while (it.hasNext()) {
                        CheckedItemProvider.setChecked(it.next().longValue(), true);
                    }
                    showBottomMenu(this.mBottomNavigationView);
                    this.mObservable.notifyObservers(Integer.valueOf(Event.SELECT_ALL));
                }
            } else if (this.mScene == 14) {
                CheckedItemProvider.initCheckedList();
                this.mObservable.notifyObservers(Integer.valueOf(Event.TRASH_DESELECT_ALL));
                updateCheckBox(true);
                hideBottomMenu(this.mBottomNavigationView);
            } else {
                CheckedItemProvider.initCheckedList();
                hideBottomMenu(this.mBottomNavigationView);
                this.mObservable.notifyObservers(Integer.valueOf(Event.DESELECT_ALL));
            }
            setContentDescription();
            int i = this.mScene;
            if (i == 5 || i == 10) {
                SALogProvider.insertSALog(this.mResource.getString(C0690R.string.screen_edit), this.mResource.getString(C0690R.string.event_select_all));
            }
        }
    }

    public void onSceneChange(int i) {
        Log.m26i(TAG, "onSceneChange - scene : " + i);
        if (this.mActivity != null) {
            int i2 = this.mScene;
            this.mPrevScene = i2;
            if (!(i2 == 2 && i2 == 7 && i2 == 5 && i2 == 10 && i2 == 3 && i2 == 13 && i2 == 14 && i2 == 15) && (i == 2 || i == 7 || i == 5 || i == 10 || i == 3 || i == 13 || i == 14 || i == 15)) {
                setBackgroundActionbar(true);
            } else {
                int i3 = this.mScene;
                if ((i3 == 2 || i3 == 7 || i3 == 5 || i3 == 10 || i3 == 3 || i3 == 13 || i3 == 14 || i3 == 15) && !(i == 2 && i == 7 && i == 5 && i == 10 && i == 3 && i == 13 && i == 14 && i == 15)) {
                    setBackgroundActionbar(false);
                }
            }
            if (!(this.mActionbar == null || this.mPrevScene == 1)) {
                ModePager.getInstance().hideTab(false);
                ModePager.getInstance().hideContentTab(false);
            }
            if (i != 7) {
                this.mActivity.getWindow().setSoftInputMode(48);
            }
            if (!(i == 5 || i == 10)) {
                MouseKeyboardProvider.getInstance().setShareSelectMode(false);
                MouseKeyboardProvider.getInstance().setSelectModeByEditOption(false);
            }
            if (i != 13) {
                this.mActionbar.setSubtitle((CharSequence) null);
            }
            switch (i) {
                case 1:
                    if (this.mScene != 1) {
                        showMain();
                        break;
                    }
                    break;
                case 2:
                    if (DataRepository.getInstance().getCategoryRepository().isChildList()) {
                        showChildList();
                    } else {
                        showLibrary();
                    }
                    this.mIsRenameFile = false;
                    break;
                case 3:
                    if (this.mScene == 1) {
                        this.mScene = 2;
                    }
                    if (!DataRepository.getInstance().getCategoryRepository().isChildList()) {
                        showLibrary();
                        break;
                    } else {
                        showChildList();
                        break;
                    }
                case 4:
                    showPlay();
                    break;
                case 5:
                    showSelect(i);
                    break;
                case 6:
                    showEdit();
                    SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_EDIT, -1);
                    break;
                case 7:
                    showSearch();
                    this.mActivity.invalidateOptionsMenu();
                    break;
                case 8:
                    showRecord();
                    break;
                case 9:
                    showSelect(i);
                    break;
                case 10:
                    showSelect(i);
                    break;
                case 11:
                    showMain();
                    break;
                case 12:
                    showPlay();
                    break;
                case 13:
                    showTrash();
                    break;
                case 14:
                    showSelect(i);
                    break;
                case 15:
                    if (this.mScene == 1) {
                        this.mScene = 13;
                    }
                    showTrash();
                    break;
                default:
                    hide();
                    break;
            }
            if (this.mToolbar != null) {
                int dimensionPixelSize = this.mResource.getDimensionPixelSize(C0690R.dimen.toolbar_content_inset_end);
                if (i == 5 || i == 7 || i == 10 || i == 14) {
                    this.mToolbar.setContentInsetsAbsolute(0, dimensionPixelSize);
                } else {
                    this.mToolbar.setContentInsetsAbsolute(dimensionPixelSize, dimensionPixelSize);
                }
            }
            if (i == 5 || i == 10 || i == 14) {
                setCollapsingToolbarEnable(true);
            } else {
                setCollapsingToolbarEnable(false);
                BottomNavigationView bottomNavigationView = this.mBottomNavigationView;
                if (!(bottomNavigationView == null || bottomNavigationView.getVisibility() == 8)) {
                    hideBottomMenu(this.mBottomNavigationView);
                }
            }
            this.mActivity.invalidateOptionsMenu();
            if (!this.mIsInvalidateMenu) {
                invalidateOptionMenu();
            }
            this.mScene = i;
        }
    }

    public void invalidateOptionMenu() {
        Log.m19d(TAG, "invalidateOptionMenu");
        this.mIsInvalidateMenu = true;
    }

    public void prepareMenu(Menu menu, int i, Activity activity) {
        Trace.beginSection("VNActionBar.prepareMenu");
        Log.m19d(TAG, "prepareMenu - scene : " + i);
        this.mMenu = menu;
        if (this.mIsInvalidateMenu || menu.size() <= 0) {
            switch (i) {
                case 1:
                case 11:
                    addMainOptionMenu(menu);
                    break;
                case 2:
                    if (Settings.getIntSettings(Settings.KEY_LIST_MODE, 0) == 1 && !DataRepository.getInstance().getCategoryRepository().isChildList()) {
                        addCategoryListOptionMenu(menu, activity);
                        break;
                    } else {
                        addListOptionMenu(menu, activity);
                        break;
                    }
//                    break;
                case 3:
                    addMiniPlayOptionMenu(menu, activity);
                    break;
                case 4:
                    addPlayOptionMenu(menu, activity);
                    break;
                case 5:
                    addSelectionMenu(menu, activity);
                    break;
                case 6:
                    addEditOptionMenu(menu, activity);
                    break;
                case 7:
                    addSearchOptionMenu(menu, activity);
                    break;
                case 9:
                    addPrivateSelectionMenu(menu, activity);
                    break;
                case 10:
                    addSelectionMenu(menu, activity);
                    break;
                case 12:
                    break;
                case 13:
                    addTrashMenu(menu, activity);
                    break;
                case 14:
                    addTrashSelectionMenu(menu);
                    break;
                case 15:
                    clearMenu(menu);
                    clearNullMenu(menu);
                    break;
                default:
                    menu.clear();
                    break;
            }
            FrameLayout frameLayout = this.mListView;
            if (frameLayout != null) {
                if (i == 12) {
                    frameLayout.setVisibility(8);
                } else if (frameLayout.getVisibility() != 0) {
                    this.mListView.setVisibility(0);
                }
            }
            updateBadge(menu);
            this.mIsInvalidateMenu = false;
            Trace.endSection();
            return;
        }
        Log.m19d(TAG, "don't need to prepareMenu again!");
    }

    private void clearMenu(Menu menu) {
        Log.m19d(TAG, "clearMenu");
        if (menu == null) {
            Log.m19d(TAG, "clearMenu - menu is null");
            return;
        }
        menu.add("");
        while (menu.size() > 1) {
            menu.removeItem(menu.getItem(0).getItemId());
        }
        BottomNavigationView bottomNavigationView = this.mBottomNavigationView;
        if (bottomNavigationView != null) {
            bottomNavigationView.getMenu().clear();
        }
    }

    private void clearNullMenu(Menu menu) {
        Log.m19d(TAG, "clearNullMenu");
        if (menu.size() > 0) {
            menu.removeItem(menu.getItem(0).getItemId());
        }
    }

    private void addPrivateSelectionMenu(Menu menu, Activity activity) {
        Log.m19d(TAG, "addPrivateSelectionMenu");
        clearMenu(menu);
        activity.getMenuInflater().inflate(C0690R.C0695menu.private_select, menu);
        clearNullMenu(menu);
    }

    private void addSelectionMenu(Menu menu, Activity activity) {
        Log.m19d(TAG, "addSelectionMenu");
        clearMenu(menu);
        int checkedItemCount = CheckedItemProvider.getCheckedItemCount();
        this.mBottomNavigationView.inflateMenu(C0690R.C0695menu.bottom_select_menu);
        setupMenuOnBottomView(this.mBottomNavigationView, checkedItemCount);
        if (checkedItemCount == 0) {
            menu.clear();
            hideBottomMenu(this.mBottomNavigationView);
        } else if ((this.mIsReleasedFinger || CursorProvider.getInstance().getIdInOneItemCase() != -1) && !this.mIsShowingBottomNavigationBar) {
            showBottomMenu(this.mBottomNavigationView);
        }
        if (MouseKeyboardProvider.getInstance().getSelectModeByEditOption()) {
            activity.getMenuInflater().inflate(C0690R.C0695menu.select, menu);
        } else {
            activity.getMenuInflater().inflate(C0690R.C0695menu.select_longpress, menu);
        }
        checkSecureFolderMenu(activity, menu);
        if (checkedItemCount == 0) {
            menu.clear();
        } else {
            menu.removeItem(C0690R.C0693id.option_write_to_nfc_tag);
            menu.removeItem(C0690R.C0693id.option_remove_from_nfc_tag);
        }
        clearNullMenu(menu);
    }

    private void addTrashSelectionMenu(Menu menu) {
        Log.m19d(TAG, "addTrashSelectionMenu");
        clearMenu(menu);
        int checkedItemCount = CheckedItemProvider.getCheckedItemCount();
        this.mBottomNavigationView.inflateMenu(C0690R.C0695menu.bottom_trash_select_menu);
        setItemOnMenu(this.mBottomNavigationView.getMenu(), C0690R.C0693id.action_trash_restore, true);
        setItemOnMenu(this.mBottomNavigationView.getMenu(), C0690R.C0693id.action_trash_delete, true);
        if (checkedItemCount == 0) {
            menu.clear();
            hideBottomMenu(this.mBottomNavigationView);
        } else {
            showBottomMenu(this.mBottomNavigationView);
        }
        clearNullMenu(menu);
    }

    /* access modifiers changed from: private */
    public void enableMarginBottomList(boolean z) {
        FrameLayout frameLayout = this.mListView;
        if (frameLayout != null) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) frameLayout.getLayoutParams();
            if (z) {
                layoutParams.bottomMargin = this.mResource.getDimensionPixelSize(C0690R.dimen.fast_option_view_height);
            } else {
                layoutParams.bottomMargin = 0;
            }
            this.mListView.setLayoutParams(layoutParams);
        }
    }

    private void addMainOptionMenu(Menu menu) {
        int i;
        Log.m19d(TAG, "addMainOptionMenu");
        clearMenu(menu);
        this.mActivity.getMenuInflater().inflate(C0690R.C0695menu.main_menu, menu);
        if (!ContactUsProvider.getInstance().isSupportedContactUs() || UPSMProvider.getInstance().isUltraPowerSavingMode()) {
            menu.removeItem(C0690R.C0693id.option_contact_us);
        }
        if (!CursorProvider.getInstance().isDoneUpdatingFilesCount() || CursorProvider.getInstance().getRecordFileCount() == -1 || this.mPrevScene == 2 || Engine.getInstance().isRestoreTempFile()) {
            i = CursorProvider.getInstance().updatedItemCount();
            Log.m26i(TAG, "addMainOptionMenu - Item Size on main query :" + i);
        } else {
            i = CursorProvider.getInstance().getRecordFileCount();
            Log.m26i(TAG, "addMainOptionMenu - Cached Item Size : " + i);
        }
        if (i <= 0) {
            menu.removeItem(C0690R.C0693id.list_recordings);
        }
        clearNullMenu(menu);
    }

    private void addTrashMenu(Menu menu, Activity activity) {
        Log.m19d(TAG, "addTrashMenu");
        if (!this.mActionbar.isShowing()) {
            this.mActionbar.show();
        }
        clearMenu(menu);
        int numberTrashItem = TrashHelper.getInstance().getNumberTrashItem(this.mActivity);
        if (numberTrashItem > 0) {
            activity.getMenuInflater().inflate(C0690R.C0695menu.trash, menu);
            this.mActionbar.setSubtitle((CharSequence) this.mActivity.getResources().getQuantityString(C0690R.plurals.trash_sub_header, numberTrashItem, new Object[]{Integer.valueOf(numberTrashItem)}));
        } else {
            this.mActionbar.setSubtitle((CharSequence) null);
        }
        clearNullMenu(menu);
    }

    private void addListOptionMenu(Menu menu, Activity activity) {
        Log.m19d(TAG, "addListOptionMenu");
        clearMenu(menu);
        int itemCount = CursorProvider.getInstance().getItemCount();
        if (itemCount != 0 || Engine.getInstance().getEditorState() == 3) {
            Log.m26i(TAG, "addListOptionMenu - list item count: " + itemCount);
            if (!this.mIsFirstTimeAddListMenu) {
                inflateListMenu(menu, activity);
            } else {
//                new Handler().postDelayed(new Runnable(menu, activity) {
////                    private final /* synthetic */ Menu f$1;
//                    private final /* synthetic */ Activity f$2;
//
////                    {
////                        this.f$1 = r2;
////                        this.f$2 = r3;
////                    }
//
//                    public final void run() {
//                        MainActionbar.this.lambda$addListOptionMenu$0$MainActionbar(this.f$1, this.f$2);
//                    }
//                }, 0);
            }
        } else {
            Log.m29v(TAG, "addListOptionMenu - item count : " + itemCount);
            setTempListMenu(menu.add("      "));
            setTempListMenu(menu.add("      "));
            menu.add(0, C0690R.C0693id.option_settings, 0, C0690R.string.action_settings);
            menu.add(0, C0690R.C0693id.option_trash, 0, C0690R.string.trash);
            checkPlayWithReceiver(menu);
            clearNullMenu(menu);
        }
    }

    public /* synthetic */ void lambda$addListOptionMenu$0$MainActionbar(Menu menu, Activity activity) {
        inflateListMenu(menu, activity);
        this.mIsFirstTimeAddListMenu = false;
    }

    private void setTempListMenu(MenuItem menuItem) {
        menuItem.setShowAsAction(6);
        menuItem.setIcon(17170445);
        menuItem.setEnabled(false);
    }

    private void inflateListMenu(Menu menu, Activity activity) {
        activity.getMenuInflater().inflate(C0690R.C0695menu.list, menu);
        AppCompatActivity appCompatActivity = this.mActivity;
        if (appCompatActivity != null && !PRIVATE_INTENT_ACTION.equals(appCompatActivity.getIntent().getAction())) {
            menu.removeItem(C0690R.C0693id.import_from_app);
        }
        checkPlayWithReceiver(menu);
        clearNullMenu(menu);
    }

    private void addCategoryListOptionMenu(Menu menu, Activity activity) {
        Log.m26i(TAG, "addCategoryListOptionMenu");
        clearMenu(menu);
        activity.getMenuInflater().inflate(C0690R.C0695menu.category_list, menu);
        AppCompatActivity appCompatActivity = this.mActivity;
        if (appCompatActivity != null && !PRIVATE_INTENT_ACTION.equals(appCompatActivity.getIntent().getAction())) {
            menu.removeItem(C0690R.C0693id.import_from_app);
        }
        checkPlayWithReceiver(menu);
        clearNullMenu(menu);
    }

    private void addSearchOptionMenu(Menu menu, Activity activity) {
        Log.m26i(TAG, "addSearchOptionMenu");
        clearMenu(menu);
        activity.getMenuInflater().inflate(C0690R.C0695menu.search, menu);
        clearNullMenu(menu);
        View inflate = activity.getLayoutInflater().inflate(C0690R.layout.search_layout, (ViewGroup) null);
        menu.findItem(C0690R.C0693id.option_search_view).setActionView(inflate);
        this.mSearchView = (SearchView) inflate.findViewById(C0690R.C0693id.searchView);
        this.mSearchView.setSearchableInfo(((SearchManager) activity.getSystemService("search")).getSearchableInfo(activity.getComponentName()));
        this.mSearchView.setIconifiedByDefault(false);
        this.mSearchView.setQueryHint(activity.getResources().getString(C0690R.string.search));
//        ImageView seslGetUpButton = this.mSearchView.seslGetUpButton();
//        seslGetUpButton.setVisibility(0);
//        seslGetUpButton.setColorFilter(ContextCompat.getColor(this.mActivity, C0690R.C0691color.actionbar_back_icon_color));
//        seslGetUpButton.setBackground(ContextCompat.getDrawable(this.mActivity, C0690R.C0692drawable.basic_button_ripple));
//        seslGetUpButton.setOnClickListener(new View.OnClickListener() {
//            public final void onClick(View view) {
//                MainActionbar.this.lambda$addSearchOptionMenu$1$MainActionbar(view);
//            }
//        });
        TextView textView = (TextView) this.mSearchView.findViewById(C0690R.C0693id.search_src_text);
        if (textView != null) {
            textView.setFilters(getNameFilter(activity));
            String recordingSearchTag = CursorProvider.getInstance().getRecordingSearchTag();
            textView.setText(recordingSearchTag);
            textView.setPrivateImeOptions(DISABLE_EMOTICON_FLAG + ";" + DISABLE_GIF_FLAG + ";" + DISABLE_LIVE_MESSAGE + ";" + DISABLE_STICKER_FLAG);
            textView.setTypeface(Typeface.create("sec-roboto-light", 0));
            textView.setImeOptions(268435459);
            textView.setHintTextColor(this.mActivity.getResources().getColor(C0690R.C0691color.listview_empty_text_color, (Resources.Theme) null));
            if (recordingSearchTag != null && !recordingSearchTag.isEmpty()) {
                ((EditText) this.mSearchView.findViewById(C0690R.C0693id.search_src_text)).setSelection(recordingSearchTag.length());
            }
        }
        this.mSearchView.setOnQueryTextListener(this.queryTextListener);
        this.mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            public final void onFocusChange(View view, boolean z) {
                MainActionbar.this.lambda$addSearchOptionMenu$3$MainActionbar(view, z);
            }
        });
        if (!this.mIsRenameFile) {
            int i = this.mPrevScene;
            int i2 = this.mScene;
            if (i != i2 || (i == i2 && this.mIsSearchFocus)) {
                this.mSearchView.requestFocus();
                this.mIsSearchFocus = false;
            }
        }
    }

    public /* synthetic */ void lambda$addSearchOptionMenu$1$MainActionbar(View view) {
        AppCompatActivity appCompatActivity = this.mActivity;
        if (appCompatActivity != null) {
            appCompatActivity.onBackPressed();
        }
    }

    public /* synthetic */ void lambda$addSearchOptionMenu$3$MainActionbar(final View view, boolean z) {
        if (z) {
            view.postDelayed(new Runnable() {

                public final void run() {
                    MainActionbar.this.lambda$null$2$MainActionbar(view);
                }
            }, 200);
        }
    }

    public /* synthetic */ void lambda$null$2$MainActionbar(View view) {
        AppCompatActivity appCompatActivity = this.mActivity;
        if (appCompatActivity != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) appCompatActivity.getSystemService("input_method");
//            if (!inputMethodManager.semIsInputMethodShown() && Engine.getInstance().getPlayerState() == 1) {
//                if (inputMethodManager.semIsAccessoryKeyboard()) {
//                    inputMethodManager.hideSoftInputFromWindow(this.mSearchView.getWindowToken(), 0);
//                } else {
//                    inputMethodManager.showSoftInput(view.findFocus(), 0);
//                }
//            }
        }
    }

    public static InputFilter[] getNameFilter(final Activity activity) {
        final Toast makeText = Toast.makeText(activity, C0690R.string.max_char_reached_msg, 0);
        return new InputFilter[]{new InputFilter.LengthFilter(50) {
            public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
                Toast toast;
                CharSequence filter = super.filter(charSequence, i, i2, spanned, i3, i4);
                if (filter != null && charSequence.length() > 0 && (toast = makeText) != null && !toast.getView().isShown()) {
                    makeText.show();
                }
                return filter;
            }
        }, new InputFilter() {
            private final /* synthetic */ Activity f$0;

            {
                this.f$0 = activity;
            }

            public final CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
                return MainActionbar.lambda$getNameFilter$4(this.f$0, charSequence, i, i2, spanned, i3, i4);
            }
        }};
    }

    static /* synthetic */ CharSequence lambda$getNameFilter$4(Activity activity, CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
        String charSequence2 = spanned.subSequence(i3, i4).toString();
        if (!EmoticonUtils.hasEmoticon(charSequence)) {
            return charSequence;
        }
        Toast.makeText(activity, activity.getString(C0690R.string.invalid_character), 0).show();
        return charSequence2;
    }

    private void addEditOptionMenu(Menu menu, Activity activity) {
        Log.m19d(TAG, "addEditOptionMenu");
        clearMenu(menu);
        activity.getMenuInflater().inflate(C0690R.C0695menu.edit, menu);
        String recentFilePath = Engine.getInstance().getRecentFilePath();
        if (!(recentFilePath == null || recentFilePath.isEmpty() || new File(recentFilePath).getParent() == null)) {
            menu.findItem(C0690R.C0693id.option_edit_save).setEnabled(Engine.getInstance().isEditSaveEnable());
        }
        clearNullMenu(menu);
    }

    private void addPlayOptionMenu(Menu menu, Activity activity) {
        Log.m26i(TAG, "addPlayOptionMenu");
        clearMenu(menu);
        activity.getMenuInflater().inflate(C0690R.C0695menu.play, menu);
        MetadataRepository.getInstance().setPath(Engine.getInstance().getPath());
        if (VoiceNoteFeature.FLAG_IS_NOT_SUPPORT_TRANSLATION(this.mActivity)) {
            menu.removeItem(C0690R.C0693id.option_stt);
        }
        checkSecureFolderMenu(activity, menu);
        checkPlayWithReceiver(menu);
        clearNullMenu(menu);
    }

    private void checkPlayWithReceiver(Menu menu) {
        MenuItem findItem;
        MenuItem findItem2;
        Log.m19d(TAG, "checkPlayWithReceiver");
        if (!VoiceNoteFeature.FLAG_IS_TABLET) {
            boolean booleanSettings = Settings.getBooleanSettings(Settings.KEY_PLAY_WITH_RECEIVER, true);
            Settings.setSettings(Settings.KEY_PLAY_WITH_RECEIVER, booleanSettings);
            Engine.getInstance().setPlayWithReceiver(!booleanSettings);
            Log.m19d(TAG, "checkPlayWithReceiver - is play with receiver : " + booleanSettings);
            if (booleanSettings) {
                menu.removeItem(C0690R.C0693id.option_play_speaker);
                if ((Engine.getInstance().isWiredHeadSetConnected() || Engine.getInstance().isBluetoothHeadSetConnected()) && (findItem2 = menu.findItem(C0690R.C0693id.option_play_receiver)) != null) {
                    findItem2.setEnabled(false);
                    findItem2.setIconTintList(ContextCompat.getColorStateList(this.mActivity, C0690R.C0691color.play_through_receiver_disable));
                }
                AppCompatActivity appCompatActivity = this.mActivity;
                if (appCompatActivity == null) {
                    return;
                }
                if (this.mScene == 2) {
                    SALogProvider.insertSALog(appCompatActivity.getResources().getString(C0690R.string.screen_list), this.mActivity.getResources().getString(C0690R.string.event_list_play_through_speaker), "1");
                } else {
                    SALogProvider.insertSALog(appCompatActivity.getResources().getString(C0690R.string.screen_player_comm), this.mActivity.getResources().getString(C0690R.string.event_player_play_through_speaker), "1");
                }
            } else {
                menu.removeItem(C0690R.C0693id.option_play_receiver);
                if ((Engine.getInstance().isWiredHeadSetConnected() || Engine.getInstance().isBluetoothHeadSetConnected()) && (findItem = menu.findItem(C0690R.C0693id.option_play_speaker)) != null) {
                    findItem.setEnabled(false);
                    findItem.setIconTintList(ContextCompat.getColorStateList(this.mActivity, C0690R.C0691color.play_through_receiver_disable));
                }
                AppCompatActivity appCompatActivity2 = this.mActivity;
                if (appCompatActivity2 == null) {
                    return;
                }
                if (this.mScene == 2) {
                    SALogProvider.insertSALog(appCompatActivity2.getResources().getString(C0690R.string.screen_list), this.mActivity.getResources().getString(C0690R.string.event_list_play_through_receiver), "0");
                } else {
                    SALogProvider.insertSALog(appCompatActivity2.getResources().getString(C0690R.string.screen_player_comm), this.mActivity.getResources().getString(C0690R.string.event_player_play_through_receiver), "0");
                }
            }
        } else {
            Log.m19d(TAG, "checkPlayWithReceiver - don't support on tablet devices");
            Settings.setSettings(Settings.KEY_PLAY_WITH_RECEIVER, false);
            Engine.getInstance().setPlayWithReceiver(false);
            menu.removeItem(C0690R.C0693id.option_play_receiver);
            menu.removeItem(C0690R.C0693id.option_play_speaker);
        }
    }

    private void checkSecureFolderMenu(Activity activity, Menu menu) {
        if (SecureFolderProvider.isSecureFolderSupported()) {
            SecureFolderProvider.getKnoxMenuList(activity);
            if (!SecureFolderProvider.isOutsideSecureFolder()) {
                menu.removeItem(C0690R.C0693id.option_move_to_secure_folder);
            } else {
                MenuItem findItem = menu.findItem(C0690R.C0693id.option_move_to_secure_folder);
                if (findItem != null) {
                    findItem.setTitle(this.mActivity.getString(C0690R.string.move_to_secure_folder_ps, new Object[]{SecureFolderProvider.getKnoxName()}));
                }
            }
            if (!SecureFolderProvider.isInsideSecureFolder()) {
                menu.removeItem(C0690R.C0693id.option_remove_from_secure_folder);
                return;
            }
            MenuItem findItem2 = menu.findItem(C0690R.C0693id.option_remove_from_secure_folder);
            if (findItem2 != null) {
                findItem2.setTitle(this.mActivity.getString(C0690R.string.move_out_of_secure_folder_ps, new Object[]{SecureFolderProvider.getKnoxName()}));
                return;
            }
            return;
        }
        menu.removeItem(C0690R.C0693id.option_move_to_secure_folder);
        menu.removeItem(C0690R.C0693id.option_remove_from_secure_folder);
    }

    private void addMiniPlayOptionMenu(Menu menu, Activity activity) {
        Log.m26i(TAG, "addMiniPlayOptionMenu");
        if (menu != null) {
            clearMenu(menu);
            activity.getMenuInflater().inflate(C0690R.C0695menu.mini_play, menu);
            MetadataRepository.getInstance().setPath(Engine.getInstance().getPath());
            if (VoiceNoteFeature.FLAG_IS_NOT_SUPPORT_TRANSLATION(this.mActivity)) {
                menu.removeItem(C0690R.C0693id.option_stt);
            }
            checkPlayWithReceiver(menu);
            checkSecureFolderMenu(activity, menu);
            clearNullMenu(menu);
        }
    }

    public void selectOption(int i, AppCompatActivity appCompatActivity) {
        if (this.mActivity == null) {
            Log.m22e(TAG, "selectOption mActivity is null !!");
            return;
        }
        ContextMenuProvider.getInstance().setId(-1);
        switch (i) {
            case 16908332:
                this.mRunOptionMenu.home(this.mScene);
                return;
            case C0690R.C0693id.import_from_app:
                this.mRunOptionMenu.importFromApp();
                return;
            case C0690R.C0693id.list_recordings:
                if (!this.mIsBackPress) {
                    this.mRunOptionMenu.openList();
                    this.mIsFirstTimeAddListMenu = true;
                    return;
                }
                return;
            case C0690R.C0693id.manage_categories:
                this.mRunOptionMenu.manageCategories();
                return;
            default:
                switch (i) {
                    case C0690R.C0693id.option_contact_us:
                        this.mRunOptionMenu.contactUs();
                        return;
                    case C0690R.C0693id.option_delete:
                        this.mRunOptionMenu.delete(this.mScene);
                        return;
                    case C0690R.C0693id.option_details:
                        this.mRunOptionMenu.showDetails(this.mScene);
                        return;
                    case C0690R.C0693id.option_edit:
                        this.mRunOptionMenu.edit();
                        return;
                    case C0690R.C0693id.option_edit_save:
                        this.mRunOptionMenu.editSave();
                        return;
                    case C0690R.C0693id.option_move:
                        this.mRunOptionMenu.move();
                        return;
                    case C0690R.C0693id.option_move_to_secure_folder:
                        this.mRunOptionMenu.moveToSecureFolder(appCompatActivity, this.mScene);
                        return;
                    case C0690R.C0693id.option_play_receiver:
                        RunOptionMenu runOptionMenu = this.mRunOptionMenu;
                        if (!runOptionMenu.mDisableSpeakerOrReceive) {
                            runOptionMenu.playWithReceiver(this.mScene, true);
                            return;
                        }
                        return;
                    case C0690R.C0693id.option_play_speaker:
                        RunOptionMenu runOptionMenu2 = this.mRunOptionMenu;
                        if (!runOptionMenu2.mDisableSpeakerOrReceive) {
                            runOptionMenu2.playWithReceiver(this.mScene, false);
                            return;
                        }
                        return;
                    case C0690R.C0693id.option_remove_from_nfc_tag:
                        this.mRunOptionMenu.startNFCWritingActivity(false, this.mScene);
                        return;
                    case C0690R.C0693id.option_remove_from_secure_folder:
                        this.mRunOptionMenu.removeFromSecureFolder(appCompatActivity, this.mScene);
                        return;
                    case C0690R.C0693id.option_rename:
                        this.mRunOptionMenu.showRenameDialog(appCompatActivity, this.mScene);
                        return;
                    case C0690R.C0693id.option_search:
                        this.mRunOptionMenu.search();
                        return;
                    default:
                        switch (i) {
                            case C0690R.C0693id.option_select:
                                MouseKeyboardProvider.getInstance().setSelectModeByEditOption(true);
                                this.mRunOptionMenu.select();
                                return;
                            case C0690R.C0693id.option_settings:
                                this.mRunOptionMenu.settings();
                                return;
                            case C0690R.C0693id.option_share:
                                share();
                                return;
                            default:
                                switch (i) {
                                    case C0690R.C0693id.option_sort_by:
                                        this.mRunOptionMenu.showSortByDialog(this.mActivity);
                                        return;
                                    case C0690R.C0693id.option_stt:
                                        if (PhoneStateProvider.getInstance().isCallIdle(appCompatActivity)) {
                                            this.mRunOptionMenu.translate();
                                            return;
                                        }
                                        return;
                                    case C0690R.C0693id.option_trash:
                                        this.mRunOptionMenu.trash();
                                        return;
                                    case C0690R.C0693id.option_write_to_nfc_tag:
                                        if (PermissionProvider.checkPhonePermission(appCompatActivity, 5, C0690R.string.voice_label, false)) {
                                            this.mRunOptionMenu.startNFCWritingActivity(true, this.mScene);
                                            return;
                                        }
                                        return;
                                    default:
                                        switch (i) {
                                            case C0690R.C0693id.trash_edit:
                                                this.mRunOptionMenu.editTrash();
                                                return;
                                            case C0690R.C0693id.trash_empty:
                                                this.mRunOptionMenu.emptyTrash();
                                                return;
                                            default:
                                                return;
                                        }
                                }
                        }
                }
        }
    }

    private void deleteFile() {
        Log.m26i(TAG, "deleteFile");
        this.mRunOptionMenu.deleteFile(this.mScene, this.mSearchView);
    }

    private void share() {
        Log.m26i(TAG, "share");
        this.mIsSharePress = true;
        int i = this.mScene;
        if (i != 2) {
            this.mRunOptionMenu.share(i);
            return;
        }
        long idInOneItemCase = CursorProvider.getInstance().getIdInOneItemCase();
        if (idInOneItemCase != -1) {
            CheckedItemProvider.initCheckedList();
            CheckedItemProvider.toggle(idInOneItemCase);
        }
        this.mObservable.notifyObservers(6);
        MouseKeyboardProvider.getInstance().setShareSelectMode(true);
        SALogProvider.insertSALog(this.mResource.getString(C0690R.string.screen_list), this.mResource.getString(C0690R.string.event_list_share));
    }

    public void update(Observable observable, Object obj) {
        Log.m26i(TAG, "update - event : " + obj + " scene : " + this.mScene);
        if (this.mActivity == null) {
            Log.m22e(TAG, "update mActivity is null ");
            return;
        }
        switch (((Integer) obj).intValue()) {
            case 2:
                int i = this.mScene;
                if (i != 2) {
                    if (i == 4) {
                        showPlay();
                        return;
                    } else if (i == 6) {
                        showEdit();
                        return;
                    } else if (i == 7) {
                        SearchView searchView = this.mSearchView;
                        if (searchView != null && searchView.hasFocus()) {
                            this.mSearchView.clearFocus();
                            this.mSearchView.requestFocus();
                        }
                        showSearch();
                        return;
                    } else {
                        return;
                    }
                } else if (DataRepository.getInstance().getCategoryRepository().isChildList()) {
                    showChildList();
                    return;
                } else {
                    showLibrary();
                    return;
                }
            case 4:
                DialogFactory.clearDialogByTag(this.mActivity.getSupportFragmentManager(), DialogFactory.EDIT_SAVE_DIALOG);
                DialogFactory.clearDialogByTag(this.mActivity.getSupportFragmentManager(), DialogFactory.RECORD_CANCEL_DIALOG);
                DialogFactory.clearDialogByTag(this.mActivity.getSupportFragmentManager(), DialogFactory.RENAME_DIALOG);
                DialogFactory.clearDialogByTag(this.mActivity.getSupportFragmentManager(), DialogFactory.CATEGORY_RENAME);
                DialogFactory.clearDialogByTag(this.mActivity.getSupportFragmentManager(), DialogFactory.EDIT_CANCEL_DIALOG);
                return;
            case 13:
            case Event.HIDE_SIP:
            case Event.SEARCH_PLAY_START:
            case Event.SEARCH_PLAY_PAUSE:
            case Event.SEARCH_PLAY_RESUME:
                SearchView searchView2 = this.mSearchView;
                if (searchView2 != null) {
                    searchView2.postDelayed(new Runnable() {
                        public final void run() {
                            MainActionbar.this.lambda$update$5$MainActionbar();
                        }
                    }, 300);
                    return;
                }
                return;
            case Event.TRASH_UPDATE_CHECKBOX:
            case Event.TRASH_SELECT:
                updateCheckBox(true);
                return;
            case Event.RESTORE_COMPLETE:
            case Event.DELETE_COMPLETE:
                this.mRunOptionMenu.dismissProgressMoveFileDialog();
                return;
            case Event.ENABLE_MARGIN_BOTTOM_LIST:
                enableMarginBottomList(true);
                return;
            case Event.SHOW_BOTTOM_NAVIGATION_BAR:
                showBottomMenu(this.mBottomNavigationView);
                return;
            case Event.EXIT_CATEGORY:
                showLibrary();
                return;
            case Event.ENTER_CATEGORY:
                if (Engine.getInstance().getPlayerState() == 1 && Engine.getInstance().getRecorderState() == 1 && this.mScene == 2) {
                    showChildList();
                    return;
                }
                return;
            case Event.CHANGE_LIST_MODE:
                this.mActivity.invalidateOptionsMenu();
                return;
            case Event.INVALIDATE_MENU:
                this.mActivity.runOnUiThread(new Runnable() {
                    public final void run() {
                        MainActionbar.this.lambda$update$6$MainActionbar();
                    }
                });
                return;
            case Event.MINIMIZE_SIP:
                if (this.mSearchView != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) this.mActivity.getSystemService("input_method");
                    if (!VoiceNoteFeature.FLAG_SUPPORT_MINIMIZE_SIP || HWKeyboardProvider.isHWKeyboard(this.mActivity)) {
                        this.mSearchView.clearFocus();
                        this.mSearchView.setFocusable(false);
                        inputMethodManager.hideSoftInputFromWindow(this.mSearchView.getWindowToken(), 2);
                        return;
                    }
//                    inputMethodManager.semMinimizeSoftInput(this.mSearchView.getWindowToken(), 22);
                    this.mActivity.getWindow().setSoftInputMode(16);
                    return;
                }
                return;
            case Event.DESELECT_ALL:
            case Event.SELECT_ALL:
            case Event.SELECT:
                int i2 = this.mScene;
                if (i2 == 5 || i2 == 9 || i2 == 10) {
                    updateCheckBox(false);
                    return;
                }
                return;
            case Event.UPDATE_FILE_NAME:
                if (this.mScene == 4) {
                    MetadataRepository instance = MetadataRepository.getInstance();
                    setBackgroundActionbar(false);
                    this.mActionbar.setTitle((CharSequence) instance.getTitle());
                }
                this.mIsRenameFile = true;
                return;
            case Event.STOP_SEARCH:
                SearchView searchView3 = this.mSearchView;
                if (searchView3 != null) {
                    searchView3.setVisibility(8);
                }
                CursorProvider.getInstance().resetSearchTag();
                this.mActivity.invalidateOptionsMenu();
                return;
            case Event.DELETE:
                deleteFile();
                return;
            case 1001:
                showRecord();
                return;
            case 1003:
            case 1007:
                if (Engine.getInstance().getPlayerState() == 3 || Engine.getInstance().getRecorderState() == 2) {
                    DialogFactory.clearDialogByTag(this.mActivity.getSupportFragmentManager(), DialogFactory.RENAME_DIALOG);
                    DialogFactory.clearDialogByTag(this.mActivity.getSupportFragmentManager(), DialogFactory.CATEGORY_RENAME);
                    return;
                }
                return;
            case 1006:
                showMain();
                return;
            case Event.PLAY_PAUSE:
            case Event.PLAY_RESUME:
                if (this.mScene == 4) {
                    showPlay();
                    return;
                }
                return;
            case Event.PLAY_NEXT:
            case Event.PLAY_PREV:
                showPlay();
                DialogFactory.clearDialogByTag(this.mActivity.getSupportFragmentManager(), DialogFactory.RENAME_DIALOG);
                DialogFactory.clearDialogByTag(this.mActivity.getSupportFragmentManager(), DialogFactory.CATEGORY_RENAME);
                DialogFactory.clearDialogByTag(this.mActivity.getSupportFragmentManager(), DialogFactory.DELETE_DIALOG);
                DialogFactory.clearDialogByTag(this.mActivity.getSupportFragmentManager(), DialogFactory.DETAIL_DIALOG);
                DialogFactory.clearDialogByTag(this.mActivity.getSupportFragmentManager(), DialogFactory.EDIT_BOOKMARK_TITLE);
                this.mActivity.invalidateOptionsMenu();
                return;
            case Event.MINI_PLAY_START:
                addMiniPlayOptionMenu(this.mMenu, this.mActivity);
                return;
            case Event.MINI_PLAY_NEXT:
            case Event.MINI_PLAY_PREV:
                DialogFactory.clearDialogByTag(this.mActivity.getSupportFragmentManager(), DialogFactory.RENAME_DIALOG);
                DialogFactory.clearDialogByTag(this.mActivity.getSupportFragmentManager(), DialogFactory.CATEGORY_RENAME);
                DialogFactory.clearDialogByTag(this.mActivity.getSupportFragmentManager(), DialogFactory.DELETE_DIALOG);
                DialogFactory.clearDialogByTag(this.mActivity.getSupportFragmentManager(), DialogFactory.DETAIL_DIALOG);
                this.mActivity.invalidateOptionsMenu();
                return;
            case Event.SEARCH_VOICE_INPUT:
                String stringExtra = this.mActivity.getIntent().getStringExtra("query");
                SearchView searchView4 = this.mSearchView;
                if (searchView4 != null) {
                    searchView4.setQuery(stringExtra, false);
                    return;
                }
                return;
            case Event.SEARCH_HISTORY_INPUT:
                String recordingSearchTag = CursorProvider.getInstance().getRecordingSearchTag();
                SearchView searchView5 = this.mSearchView;
                if (searchView5 != null) {
                    searchView5.setQuery(recordingSearchTag, false);
                    return;
                }
                return;
            case Event.TRANSLATION_RESUME:
                if (Engine.getInstance().getTranslationState() == 2) {
                    DialogFactory.clearDialogByTag(this.mActivity.getSupportFragmentManager(), DialogFactory.RENAME_DIALOG);
                    DialogFactory.clearDialogByTag(this.mActivity.getSupportFragmentManager(), DialogFactory.TRANSLATION_CANCEL_DIALOG);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public /* synthetic */ void lambda$update$5$MainActionbar() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.mActivity.getSystemService("input_method");
        if (inputMethodManager.isActive()) {
            Log.m29v(TAG, "update - hide sip");
//            inputMethodManager.semForceHideSoftInput();
            this.mSearchView.clearFocus();
        }
    }

    public /* synthetic */ void lambda$update$6$MainActionbar() {
        AppCompatActivity appCompatActivity = this.mActivity;
        if (appCompatActivity != null) {
            appCompatActivity.invalidateOptionsMenu();
        }
        int i = this.mScene;
        if (i == 5 || i == 9 || i == 10) {
            updateCheckBox(false);
        }
    }

    public void onResume() {
        Log.m26i(TAG, "onResume");
        if (this.mScene == 1) {
            showMain();
        }
        if (this.mIsSharePress && this.mMenu != null) {
            int i = this.mScene;
            if (i == 10 || i == 5) {
                addSelectionMenu(this.mMenu, this.mActivity);
            }
            if (this.mScene == 3) {
                addMiniPlayOptionMenu(this.mMenu, this.mActivity);
            }
            this.mIsSharePress = false;
        }
        new Handler().postDelayed(new Runnable() {
            public final void run() {
                MainActionbar.this.lambda$onResume$7$MainActionbar();
            }
        }, 1000);
    }

    public /* synthetic */ void lambda$onResume$7$MainActionbar() {
        if (PermissionProvider.checkPermission(this.mActivity, (ArrayList<Integer>) null, false)) {
            checkNewVersionAvailable();
        }
    }

    public boolean onBackKeyPressed() {
        Log.m26i(TAG, "onBackKeyPressed");
        this.mIsBackPress = true;
        this.mIsRenameFile = false;
        new Handler().postDelayed(new Runnable() {
            public final void run() {
                MainActionbar.this.lambda$onBackKeyPressed$8$MainActionbar();
            }
        }, 350);
        SearchView searchView = this.mSearchView;
        if (searchView != null && searchView.getVisibility() == 0) {
            if (Engine.getInstance().getPlayerState() != 1) {
                int i = this.mScene;
                if (i == 6) {
                    Engine.getInstance().pausePlay();
                } else if (i == 7) {
                    return false;
                }
            }
            if (this.mScene == 10) {
                return false;
            }
            this.mSearchView.setVisibility(8);
            this.mObservable.notifyObservers(Integer.valueOf(Event.STOP_SEARCH));
        }
        if (this.mScene == 1 && DesktopModeProvider.isDesktopMode()) {
            ModePager.getInstance().enterRecordModesDataInDex();
        }
        return false;
    }

    public /* synthetic */ void lambda$onBackKeyPressed$8$MainActionbar() {
        this.mIsBackPress = false;
    }

    public void onEngineUpdate(int i, int i2, int i3) {
        VNMainActivity vNMainActivity;
        if (i != 1010) {
            if (i == 3010) {
                Log.m26i(TAG, "onEditorState - status : " + i + " arg1 : " + i2 + " arg2 : " + i3);
                if (i2 == 0 || i2 == 1 || i2 == 3 || i2 == 4 || i2 == 17 || i2 == 18) {
                    this.mActivity.invalidateOptionsMenu();
                }
            }
        } else if (this.mActivity != null && (vNMainActivity = this.mVNMainActivity) != null && vNMainActivity.isActivityResumed() && i2 == 2) {
            this.mActivity.invalidateOptionsMenu();
        }
    }

    private Drawable getHomeIcon() {
        AppCompatActivity appCompatActivity = this.mActivity;
        if (appCompatActivity == null) {
            return null;
        }
        Drawable drawable = appCompatActivity.getDrawable(C0690R.C0692drawable.tw_ic_ab_back_mtrl);
        if (drawable != null) {
            drawable.setAutoMirrored(true);
            drawable.setTint(this.mResource.getColor(C0690R.C0691color.actionbar_back_icon_color, (Resources.Theme) null));
        }
        return drawable;
    }

    private void checkNewVersionAvailable() {
        if (UpdateProvider.getInstance().isCheckUpdateAvailable(false) == 1) {
            UpdateProvider.getInstance().checkUpdate(this);
        }
    }

    private void updateBadge(Menu menu) {
        MenuItem menuItem;
        Log.m19d(TAG, "updateBadge");
        int size = menu.size();
        int i = 0;
        while (true) {
            if (i >= size) {
                menuItem = null;
                break;
            }
            menuItem = menu.getItem(i);
            if (menuItem.getItemId() == C0690R.C0693id.option_settings) {
                break;
            }
            i++;
        }
        boolean equals = Settings.getStringSettings(Settings.KEY_UPDATE_CHECK_RESULT_CODE, "1").equals("2");
        if (menuItem == null) {
            return;
        }
        if (equals) {
            try {
//                ((SeslMenuItem) menuItem).setBadgeText("1");
                StringBuilder sb = new StringBuilder(this.mActivity.getString(C0690R.string.action_settings));
                sb.append(", ");
                sb.append(this.mActivity.getString(C0690R.string.update_available));
                sb.append(", ");
                sb.append(this.mActivity.getString(C0690R.string.button_tts));
                menuItem.setContentDescription(sb);
                LinearLayout linearLayout = (LinearLayout) this.mActivity.findViewById(this.mResource.getIdentifier("android:id/badge_bg", (String) null, (String) null));
                if (linearLayout != null) {
                    linearLayout.removeAllViews();
                    linearLayout.setGravity(17);
                    TextView textView = new TextView(this.mActivity);
                    textView.setTextColor(this.mActivity.getColor(C0690R.C0691color.time_window_bg));
                    textView.setTextSize(1, 12.0f);
                    textView.setText("1");
                    textView.setGravity(17);
                    linearLayout.addView(textView);
                }
            } catch (NoClassDefFoundError | NoSuchMethodError e) {
                Log.m24e(TAG, "NoClassDefFoundError | NoSuchMethodError occur. SupportMenuItem or setBadgeText", e);
            } catch (Exception e2) {
                Log.m24e(TAG, "Exception occur", (Throwable) e2);
            }
        } else {
//            ((SeslMenuItem) menuItem).setBadgeText((String) null);
        }
    }

    public void onUpdateCheckFail(UpdateProvider.StubData stubData) {
        Log.m19d(TAG, "Application update check fail.");
        Settings.setSettings(Settings.KEY_UPDATE_CHECK_RESULT_CODE, UpdateProvider.StubCodes.UPDATE_CHECK_FAIL);
    }

    public void onNoMatchingApplication(UpdateProvider.StubData stubData) {
        Log.m19d(TAG, "Application not matched.");
        Settings.setSettings(Settings.KEY_UPDATE_CHECK_RESULT_CODE, "0");
    }

    public void onUpdateNotNecessary(UpdateProvider.StubData stubData) {
        Log.m19d(TAG, "Application update not necessary.");
        Settings.setSettings(Settings.KEY_UPDATE_CHECK_RESULT_CODE, "1");
        if (Settings.getBooleanSettings(Settings.KEY_UPDATE_CHECK_FROM_GALAXY_APPS, false)) {
            Settings.setSettings(Settings.KEY_UPDATE_CHECK_FROM_GALAXY_APPS, false);
            AppCompatActivity appCompatActivity = this.mActivity;
            if (appCompatActivity != null) {
                appCompatActivity.invalidateOptionsMenu();
            }
        }
    }

    public void onUpdateAvailable(UpdateProvider.StubData stubData) {
        Log.m19d(TAG, "Application update available: " + stubData.getVersionName());
        Settings.setSettings(Settings.KEY_UPDATE_CHECK_RESULT_CODE, "2");
        Settings.setSettings(Settings.KEY_CURRENT_GALAXY_APP_VERSION, stubData.getVersionCode());
        AppCompatActivity appCompatActivity = this.mActivity;
        if (appCompatActivity != null) {
            appCompatActivity.invalidateOptionsMenu();
        }
    }

    public void updateSearchTag(String str) {
        SearchView searchView = this.mSearchView;
        if (searchView == null || searchView.isIconified() || !this.mSearchView.isEnabled()) {
            Log.m22e(TAG, "updateSearchTag called but can not update!!");
            return;
        }
        Log.m22e(TAG, "updateSearchTag tag : " + str);
        this.mSearchView.setQuery(str, true);
    }

    private void showDataCheckDialog() {
        if (!DialogFactory.isDialogVisible(this.mActivity.getSupportFragmentManager(), DialogFactory.DATA_CHECK_DIALOG)) {
            Log.m26i(TAG, "showDataCheckDialog bar module: 1");
            Bundle bundle = new Bundle();
            bundle.putInt(DialogFactory.BUNDLE_REQUEST_CODE, 8);
            bundle.putInt(DialogFactory.BUNDLE_DATA_CHECK_MODULE, 1);
            DialogFactory.show(this.mActivity.getSupportFragmentManager(), DialogFactory.DATA_CHECK_DIALOG, bundle, this);
        }
    }

    private void updateBottomButtonShape() {
        if (this.mBottomNavigationView == null) {
            return;
        }
        if (Settings.isEnabledShowButtonBG()) {
            this.mBottomNavigationView.setItemBackgroundResource(C0690R.C0692drawable.bottom_navigation_item_background);
            this.mBottomNavigationView.setItemTextColor(ColorStateList.valueOf(this.mResource.getColor(C0690R.C0691color.control_activate_color)));
            return;
        }
        this.mBottomNavigationView.setItemBackgroundResource(C0690R.C0692drawable.bottom_button_ripple);
    }

    public void onDialogResult(DialogFragment dialogFragment, Bundle bundle) {
        if (bundle != null) {
            int i = bundle.getInt(DialogFactory.BUNDLE_REQUEST_CODE);
            int i2 = bundle.getInt("result_code");
            Log.m26i(TAG, "onDialogResult - requestCode : " + i + " result : " + i2);
            if (i == 8 && i2 == -1 && UpdateProvider.getInstance().isCheckUpdateAvailable(false) == 1) {
                UpdateProvider.getInstance().checkUpdate(this);
            }
        }
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case C0690R.C0693id.action_delete:
                this.mRunOptionMenu.delete(this.mScene);
                return true;
            case C0690R.C0693id.action_move:
                this.mRunOptionMenu.move();
                return true;
            case C0690R.C0693id.action_rename:
                this.mRunOptionMenu.showRenameDialog(this.mActivity, this.mScene);
                return true;
            case C0690R.C0693id.action_share:
                share();
                return true;
            case C0690R.C0693id.action_trash_delete:
                this.mRunOptionMenu.delete(this.mScene);
                return true;
            case C0690R.C0693id.action_trash_restore:
                this.mRunOptionMenu.restore(this.mScene);
                return true;
            default:
                return true;
        }
    }

    private void setCollapsingToolbarEnable(boolean z) {
        CollapsingToolbarLayout collapsingToolbarLayout;
        CoordinatorLayout.LayoutParams layoutParams;
        Log.m19d(TAG, "setCollapsingToolbarEnable - enable : " + z);
        AppCompatActivity appCompatActivity = this.mActivity;
        if (appCompatActivity == null || (collapsingToolbarLayout = this.mCollapsingToolbarLayout) == null) {
            Log.m22e(TAG, "setCollapsingToolbarEnable - something is null");
            return;
        }
        if (this.offsetUpdateListener == null) {
            this.offsetUpdateListener = new OffsetUpdateListener(appCompatActivity, collapsingToolbarLayout);
        }
        this.offsetUpdateListener.setSelectedTextView(this.mCheckBoxCountView);
        this.appBarLayout.addOnOffsetChangedListener((AppBarLayout.OnOffsetChangedListener) this.offsetUpdateListener);
        this.appBarLayout.setExpanded(false, false);
        setScrollFlags(z);
        float f = 0.0f;
        if (z) {
            float dimensionPixelSize = (float) this.mResource.getDimensionPixelSize(C0690R.dimen.sesl_action_bar_default_height);
            f = ((float) this.mResource.getDisplayMetrics().heightPixels) * this.mHeightPercent;
            if (DesktopModeProvider.isDesktopMode() ? this.mResource.getConfiguration().screenHeightDp < 600 || this.mResource.getConfiguration().smallestScreenWidthDp < 600 : (this.mActivity.isInMultiWindowMode() && this.mResource.getConfiguration().smallestScreenWidthDp < 480) || (this.mResource.getConfiguration().orientation == 2 && this.mResource.getConfiguration().smallestScreenWidthDp < 600)) {
                f = dimensionPixelSize;
            }
        } else {
            TypedValue typedValue = new TypedValue();
            if (this.mActivity.getTheme().resolveAttribute(16843499, typedValue, true)) {
                f = (float) TypedValue.complexToDimensionPixelSize(typedValue.data, this.mResource.getDisplayMetrics());
            }
        }
        try {
            layoutParams = (CoordinatorLayout.LayoutParams) this.appBarLayout.getLayoutParams();
        } catch (ClassCastException e) {
            Log.m22e(TAG, e.toString());
            layoutParams = null;
        }
        if (layoutParams != null) {
            int i = (int) f;
            if (layoutParams.height != i) {
                layoutParams.height = i;
                Log.m19d(TAG, "setCollapsingToolbarEnable: LayoutParams :" + layoutParams + " ,lp.height :" + layoutParams.height);
                this.appBarLayout.setLayoutParams(layoutParams);
                return;
            }
            Log.m19d(TAG, "setCollapsingToolbarEnable: height is same");
        }
    }

    private void setScrollFlags(boolean z) {
        CollapsingToolbarLayout collapsingToolbarLayout = this.mCollapsingToolbarLayout;
        if (collapsingToolbarLayout != null) {
            AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
            if (z) {
                layoutParams.setScrollFlags(3);
            } else {
                layoutParams.setScrollFlags(6);
            }
            this.mCollapsingToolbarLayout.setLayoutParams(layoutParams);
        }
    }

    private void showBottomMenu(View view) {
        if (view != null) {
            Animation animation = view.getAnimation();
            if (animation != null && !animation.hasEnded()) {
                animation.cancel();
                view.clearAnimation();
            }
            Log.m19d(TAG, "showBottomMenu");
            TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, (float) ((int) view.getContext().getResources().getDimension(C0690R.dimen.fast_option_view_height)), 0.0f);
            translateAnimation.setInterpolator(view.getContext(), C0690R.anim.sin_in_out_90);
            translateAnimation.setDuration((long) 400);
            translateAnimation.setFillAfter(true);
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                    boolean unused = MainActionbar.this.mIsReleasedFinger = true;
                    boolean unused2 = MainActionbar.this.mIsShowingBottomNavigationBar = true;
                }

                public void onAnimationEnd(Animation animation) {
                    if (MainActionbar.this.mIsShowingBottomNavigationBar) {
                        MainActionbar.this.enableMarginBottomList(true);
                        if (MainActionbar.this.mObservable != null) {
                            MainActionbar.this.mObservable.notifyObservers(951);
                        }
                    }
                }
            });
            view.setVisibility(0);
            view.startAnimation(translateAnimation);
        }
    }

    private void hideBottomMenu(View view) {
        BottomNavigationView bottomNavigationView;
        if (view != null) {
            if (view.getVisibility() != 8 || (bottomNavigationView = this.mBottomNavigationView) == null) {
                Animation animation = view.getAnimation();
                if (animation != null && !animation.hasEnded()) {
                    animation.cancel();
                    view.clearAnimation();
                }
                Log.m19d(TAG, "hideBottomMenu");
                TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, (float) ((int) view.getContext().getResources().getDimension(C0690R.dimen.fast_option_view_height)));
                translateAnimation.setInterpolator(view.getContext(), C0690R.anim.sin_in_out_90);
                translateAnimation.setDuration((long) 400);
                translateAnimation.setFillAfter(false);
                translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationRepeat(Animation animation) {
                    }

                    public void onAnimationStart(Animation animation) {
                        boolean unused = MainActionbar.this.mIsShowingBottomNavigationBar = false;
                    }

                    public void onAnimationEnd(Animation animation) {
                        if (!MainActionbar.this.mIsShowingBottomNavigationBar && MainActionbar.this.mBottomNavigationView != null) {
                            MainActionbar.this.mBottomNavigationView.getMenu().clear();
                            MainActionbar.this.mBottomNavigationView.setVisibility(8);
                            boolean unused = MainActionbar.this.mIsReleasedFinger = false;
                        }
                    }
                });
                enableMarginBottomList(false);
                view.startAnimation(translateAnimation);
                return;
            }
            bottomNavigationView.getMenu().clear();
            this.mBottomNavigationView.setVisibility(8);
            enableMarginBottomList(false);
        }
    }

    public void setItemOnMenu(Menu menu, int i, boolean z) {
        MenuItem findItem = menu.findItem(i);
        if (findItem != null && !z) {
            menu.removeItem(i);
        }
        if (findItem == null && z) {
            menu.add(i);
        }
    }

    public int getType(int i) {
        if (i == 0) {
            return 0;
        }
        if (!MouseKeyboardProvider.getInstance().getShareSelectMode() || i == 0) {
            return MouseKeyboardProvider.getInstance().getSelectModeByEditOption() ? i == 1 ? 1 : 2 : i == 1 ? 3 : 4;
        }
        return 5;
    }

    public void setupMenuOnBottomView(BottomNavigationView bottomNavigationView, int i) {
        int type = getType(i);
        if (type == 0) {
            setItemOnMenu(bottomNavigationView.getMenu(), C0690R.C0693id.action_move, false);
            setItemOnMenu(bottomNavigationView.getMenu(), C0690R.C0693id.action_rename, false);
            setItemOnMenu(bottomNavigationView.getMenu(), C0690R.C0693id.action_delete, false);
            setItemOnMenu(bottomNavigationView.getMenu(), C0690R.C0693id.action_share, false);
        } else if (type == 1) {
            setItemOnMenu(bottomNavigationView.getMenu(), C0690R.C0693id.action_move, true);
            setItemOnMenu(bottomNavigationView.getMenu(), C0690R.C0693id.action_rename, true);
            setItemOnMenu(bottomNavigationView.getMenu(), C0690R.C0693id.action_delete, true);
            setItemOnMenu(bottomNavigationView.getMenu(), C0690R.C0693id.action_share, false);
            bottomNavigationView.setVisibility(0);
        } else if (type == 2) {
            setItemOnMenu(bottomNavigationView.getMenu(), C0690R.C0693id.action_move, true);
            setItemOnMenu(bottomNavigationView.getMenu(), C0690R.C0693id.action_rename, false);
            setItemOnMenu(bottomNavigationView.getMenu(), C0690R.C0693id.action_delete, true);
            setItemOnMenu(bottomNavigationView.getMenu(), C0690R.C0693id.action_share, false);
            bottomNavigationView.setVisibility(0);
        } else if (type == 3) {
            setItemOnMenu(bottomNavigationView.getMenu(), C0690R.C0693id.action_move, true);
            setItemOnMenu(bottomNavigationView.getMenu(), C0690R.C0693id.action_rename, true);
            setItemOnMenu(bottomNavigationView.getMenu(), C0690R.C0693id.action_delete, true);
            setItemOnMenu(bottomNavigationView.getMenu(), C0690R.C0693id.action_share, true);
        } else if (type == 4) {
            setItemOnMenu(bottomNavigationView.getMenu(), C0690R.C0693id.action_move, true);
            setItemOnMenu(bottomNavigationView.getMenu(), C0690R.C0693id.action_rename, false);
            setItemOnMenu(bottomNavigationView.getMenu(), C0690R.C0693id.action_delete, true);
            setItemOnMenu(bottomNavigationView.getMenu(), C0690R.C0693id.action_share, true);
            bottomNavigationView.setVisibility(0);
        } else if (type == 5) {
            setItemOnMenu(bottomNavigationView.getMenu(), C0690R.C0693id.action_move, false);
            setItemOnMenu(bottomNavigationView.getMenu(), C0690R.C0693id.action_rename, false);
            setItemOnMenu(bottomNavigationView.getMenu(), C0690R.C0693id.action_delete, false);
            setItemOnMenu(bottomNavigationView.getMenu(), C0690R.C0693id.action_share, true);
            bottomNavigationView.setVisibility(0);
        }
    }

    public void onTrashProgressUpdate(int i, int i2, int i3) {
        RunOptionMenu runOptionMenu;
        if (i == 939 && (runOptionMenu = this.mRunOptionMenu) != null) {
            runOptionMenu.updateProgressMoveFileDialog(i2, i3);
        }
    }
}
