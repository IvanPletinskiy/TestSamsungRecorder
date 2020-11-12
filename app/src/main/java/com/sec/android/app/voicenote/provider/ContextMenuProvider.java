package com.sec.android.app.voicenote.provider;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DataRepository;
import com.sec.android.app.voicenote.p007ui.AbsFragment;
import com.sec.android.app.voicenote.p007ui.AbsListFragment;
import com.sec.android.app.voicenote.p007ui.CategoriesListFragment;
import com.sec.android.app.voicenote.p007ui.ChildListFragment;
import com.sec.android.app.voicenote.p007ui.actionbar.RunOptionMenu;
import com.sec.android.app.voicenote.p007ui.adapter.TrashAdapter;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.MetadataRepository;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

public class ContextMenuProvider {
    private static final String TAG = "ContextMenuProvider";
    private static ContextMenuProvider mInstance;
    private long mCategoryId = -1;
    private long mId = -1;
    private MetadataRepository metadata = null;

    private ContextMenuProvider() {
        Log.m26i(TAG, "ContextMenuProvider creator !!");
    }

    public static ContextMenuProvider getInstance() {
        if (mInstance == null) {
            mInstance = new ContextMenuProvider();
        }
        return mInstance;
    }

    public void setId(long j) {
        this.mId = j;
    }

    public long getId() {
        return this.mId;
    }

    public void setCategoryId(long j) {
        Log.m26i(TAG, "id : " + j);
        this.mCategoryId = j;
    }

    public long getCategoryId() {
        Log.m26i(TAG, "mCategoryId : " + this.mCategoryId);
        return this.mCategoryId;
    }

    private void setItemTitle(long j) {
        String path = CursorProvider.getInstance().getPath(j);
        this.metadata = MetadataRepository.getInstance();
        this.metadata.setPath(path);
    }

    private String getItemTitle() {
        return this.metadata.getTitle();
    }

    public static void showContextMenu(int i, int i2, View view) {
        ContextMenuFactory.showContextMenuForView(i, i2, view);
    }

    private void checkSecureFolderMenu(Activity activity, ContextMenu contextMenu) {
        if (SecureFolderProvider.isSecureFolderSupported()) {
            SecureFolderProvider.getKnoxMenuList(activity);
            if (!SecureFolderProvider.isOutsideSecureFolder()) {
                contextMenu.removeItem(C0690R.C0693id.option_move_to_secure_folder);
            } else {
                MenuItem findItem = contextMenu.findItem(C0690R.C0693id.option_move_to_secure_folder);
                if (findItem != null) {
                    findItem.setTitle(activity.getString(C0690R.string.move_to_secure_folder_ps, new Object[]{SecureFolderProvider.getKnoxName()}));
                }
            }
            if (!SecureFolderProvider.isInsideSecureFolder()) {
                contextMenu.removeItem(C0690R.C0693id.option_remove_from_secure_folder);
                return;
            }
            MenuItem findItem2 = contextMenu.findItem(C0690R.C0693id.option_remove_from_secure_folder);
            if (findItem2 != null) {
                findItem2.setTitle(activity.getString(C0690R.string.move_out_of_secure_folder_ps, new Object[]{SecureFolderProvider.getKnoxName()}));
                return;
            }
            return;
        }
        contextMenu.removeItem(C0690R.C0693id.option_move_to_secure_folder);
        contextMenu.removeItem(C0690R.C0693id.option_remove_from_secure_folder);
    }

    public void createContextMenu(Activity activity, ContextMenu contextMenu, int i, View view, Fragment fragment) {
        Activity activity2 = activity;
        ContextMenu contextMenu2 = contextMenu;
        int i2 = i;
        View view2 = view;
        Fragment fragment2 = fragment;
        Log.m26i(TAG, "CreateContextMenu");
        if (activity2 == null || fragment2 == null || view2 == null) {
            Log.m32w(TAG, "null is occur");
        } else if (!DesktopModeProvider.isDesktopMode()) {
            Log.m32w(TAG, "Activity is not DesktopMode");
        } else {
            switch (i2) {
                case 1:
                    activity.getMenuInflater().inflate(C0690R.C0695menu.main_menu, contextMenu2);
                    contextMenu2.removeItem(C0690R.C0693id.list_recordings);
                    if (!ContactUsProvider.getInstance().isSupportedContactUs() || UPSMProvider.getInstance().isUltraPowerSavingMode()) {
                        contextMenu2.removeItem(C0690R.C0693id.option_contact_us);
                    }
                    MouseKeyboardProvider.getInstance().setSelectModeByEditOption(false);
                    MouseKeyboardProvider.getInstance().setShareSelectMode(false);
                    return;
                case 2:
                case 3:
                case 7:
                    if (!(fragment2 instanceof CategoriesListFragment) || !(view2 instanceof RecyclerView)) {
                        if ((fragment2 instanceof AbsListFragment) && (view2 instanceof RecyclerView)) {
                            RecyclerView recyclerView = (RecyclerView) view2;
                            int touchPosition = MouseKeyboardProvider.getInstance().getTouchPosition();
                            if (touchPosition > -1) {
                                long itemId = recyclerView.getAdapter().getItemId(touchPosition);
                                setId(itemId);
                                setItemTitle(itemId);
                                activity.getMenuInflater().inflate(C0690R.C0695menu.file_contextual_menu, contextMenu2);
                                checkSecureFolderMenu(activity, contextMenu);
                                contextMenu2.setHeaderTitle(getItemTitle());
                            } else if (i2 == 7) {
                                setId(-1);
                                if (recyclerView.getAdapter().getItemCount() > 0) {
                                    if (Engine.getInstance().getID() != -1) {
                                        activity.getMenuInflater().inflate(C0690R.C0695menu.mini_play, contextMenu2);
                                        checkSecureFolderMenu(activity, contextMenu);
                                        contextMenu2.removeItem(C0690R.C0693id.option_stt);
                                    } else {
                                        contextMenu2.add(0, C0690R.C0693id.option_select, 0, C0690R.string.edit);
                                        contextMenu2.add(0, C0690R.C0693id.option_share, 0, C0690R.string.sharevia);
                                    }
                                }
                            } else if (i2 == 3) {
                                setId(-1);
                                activity.getMenuInflater().inflate(C0690R.C0695menu.mini_play, contextMenu2);
                                checkSecureFolderMenu(activity, contextMenu);
                                contextMenu2.removeItem(C0690R.C0693id.option_stt);
                            } else {
                                setId(-1);
                                if (recyclerView.getAdapter().getItemCount() > 0) {
                                    contextMenu2.add(0, C0690R.C0693id.option_select, 0, C0690R.string.edit);
                                    contextMenu2.add(0, C0690R.C0693id.option_share, 0, C0690R.string.sharevia);
                                    contextMenu2.add(0, C0690R.C0693id.option_sort_by, 0, C0690R.string.sort_by);
                                    contextMenu2.add(0, C0690R.C0693id.option_settings, 0, C0690R.string.action_settings);
                                } else {
                                    contextMenu2.add(0, C0690R.C0693id.option_settings, 0, C0690R.string.action_settings);
                                }
                            }
                        }
                        MouseKeyboardProvider.getInstance().setSelectModeByEditOption(false);
                        MouseKeyboardProvider.getInstance().setShareSelectMode(false);
                        return;
                    }
                    contextMenu.clear();
                    RecyclerView recyclerView2 = (RecyclerView) view2;
                    int touchPosition2 = MouseKeyboardProvider.getInstance().getTouchPosition();
                    if (touchPosition2 <= -1) {
                        setCategoryId(-1);
                        if (recyclerView2.getAdapter().getItemCount() > 0) {
                            contextMenu2.add(0, C0690R.C0693id.manage_categories, 0, C0690R.string.manage_categories);
                            contextMenu2.add(0, C0690R.C0693id.option_settings, 0, C0690R.string.action_settings);
                        }
                    } else if (i2 == 7 || touchPosition2 != recyclerView2.getAdapter().getItemCount() - 1) {
                        long itemId2 = recyclerView2.getAdapter().getItemId(touchPosition2);
                        setCategoryId(itemId2);
                        String labelTitle = DataRepository.getInstance().getCategoryRepository().getLabelTitle((int) itemId2, activity2);
                        contextMenu2.add(0, C0690R.C0693id.manage_categories, 0, C0690R.string.manage_categories);
                        if (itemId2 > 3) {
                            contextMenu2.add(0, C0690R.C0693id.manage_label_popup_rename, 0, C0690R.string.rename);
                            contextMenu2.add(0, C0690R.C0693id.action_delete_label, 0, C0690R.string.delete);
                        } else {
                            setCategoryId(-1);
                        }
                        contextMenu2.setHeaderTitle(labelTitle);
                    } else {
                        setCategoryId(-1);
                        contextMenu2.add(0, C0690R.C0693id.manage_categories, 0, C0690R.string.manage_categories);
                    }
                    MouseKeyboardProvider.getInstance().setSelectModeByEditOption(false);
                    MouseKeyboardProvider.getInstance().setShareSelectMode(false);
                    return;
                case 4:
                    setId(-1);
                    activity.getMenuInflater().inflate(C0690R.C0695menu.play, contextMenu2);
                    contextMenu2.removeItem(C0690R.C0693id.option_play_receiver);
                    contextMenu2.removeItem(C0690R.C0693id.option_play_speaker);
                    contextMenu2.removeItem(C0690R.C0693id.option_stt);
                    checkSecureFolderMenu(activity, contextMenu);
                    MouseKeyboardProvider.getInstance().setSelectModeByEditOption(false);
                    MouseKeyboardProvider.getInstance().setShareSelectMode(false);
                    return;
                case 5:
                case 9:
                case 10:
                    if (!MouseKeyboardProvider.getInstance().getShareSelectMode()) {
                        if (MouseKeyboardProvider.getInstance().getSelectModeByEditOption()) {
                            activity.getMenuInflater().inflate(C0690R.C0695menu.select, contextMenu2);
                            contextMenu2.removeItem(C0690R.C0693id.option_rename);
                        } else {
                            activity.getMenuInflater().inflate(C0690R.C0695menu.select_longpress, contextMenu2);
                        }
                        contextMenu2.removeItem(C0690R.C0693id.option_write_to_nfc_tag);
                        contextMenu2.removeItem(C0690R.C0693id.option_remove_from_nfc_tag);
                        contextMenu2.removeItem(C0690R.C0693id.option_share);
                        contextMenu2.removeItem(C0690R.C0693id.option_delete);
                        checkSecureFolderMenu(activity, contextMenu);
                        int checkedItemCount = CheckedItemProvider.getCheckedItemCount();
                        if ((fragment2 instanceof AbsListFragment) && (view2 instanceof RecyclerView)) {
                            RecyclerView recyclerView3 = (RecyclerView) view2;
                            int touchPosition3 = MouseKeyboardProvider.getInstance().getTouchPosition();
                            if (touchPosition3 > -1) {
                                long itemId3 = recyclerView3.getAdapter().getItemId(touchPosition3);
                                if (CheckedItemProvider.isChecked(itemId3)) {
                                    setId(itemId3);
                                    setItemTitle(itemId3);
                                    if (checkedItemCount == 1) {
                                        contextMenu2.setHeaderTitle(getItemTitle());
                                    }
                                } else {
                                    setId(-1);
                                    contextMenu.clear();
                                    return;
                                }
                            } else {
                                setId(-1);
                            }
                            if (checkedItemCount == 0) {
                                contextMenu.clear();
                                return;
                            } else if (checkedItemCount != 1) {
                                contextMenu2.removeItem(C0690R.C0693id.option_rename);
                                return;
                            } else {
                                return;
                            }
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                case 13:
                    if ((fragment2 instanceof AbsFragment) && (view2 instanceof RecyclerView)) {
                        int touchPosition4 = MouseKeyboardProvider.getInstance().getTouchPosition();
                        RecyclerView recyclerView4 = (RecyclerView) view2;
                        if (touchPosition4 > -1) {
                            setId(recyclerView4.getAdapter().getItemId(touchPosition4));
                            this.metadata = MetadataRepository.getInstance();
                            if (recyclerView4.getAdapter() instanceof TrashAdapter) {
                                contextMenu2.setHeaderTitle(((TrashAdapter) recyclerView4.getAdapter()).getItemTitle(touchPosition4));
                            }
                            activity.getMenuInflater().inflate(C0690R.C0695menu.bottom_trash_select_menu, contextMenu2);
                            checkSecureFolderMenu(activity, contextMenu);
                            return;
                        }
                        return;
                    }
                    return;
                case 14:
                    int checkedItemCount2 = CheckedItemProvider.getCheckedItemCount();
                    if (checkedItemCount2 > 0 && (fragment2 instanceof AbsFragment) && (view2 instanceof RecyclerView)) {
                        RecyclerView recyclerView5 = (RecyclerView) view2;
                        int touchPosition5 = MouseKeyboardProvider.getInstance().getTouchPosition();
                        if (touchPosition5 > -1) {
                            long itemId4 = recyclerView5.getAdapter().getItemId(touchPosition5);
                            if (CheckedItemProvider.isChecked(itemId4)) {
                                setId(itemId4);
                                if (checkedItemCount2 == 1) {
                                    this.metadata = MetadataRepository.getInstance();
                                    if (recyclerView5.getAdapter() instanceof TrashAdapter) {
                                        contextMenu2.setHeaderTitle(((TrashAdapter) recyclerView5.getAdapter()).getItemTitle(touchPosition5));
                                    }
                                }
                                activity.getMenuInflater().inflate(C0690R.C0695menu.bottom_trash_select_menu, contextMenu2);
                                return;
                            }
                            setId(-1);
                            return;
                        }
                        setId(-1);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    public void contextItemSelected(AppCompatActivity appCompatActivity, MenuItem menuItem, int i, Fragment fragment) {
        Log.m26i(TAG, "contextItemSelected");
        if (appCompatActivity == null || fragment == null) {
            Log.m32w(TAG, "null is occur");
        } else if ((fragment instanceof ChildListFragment) || (fragment instanceof CategoriesListFragment)) {
            switch (menuItem.getItemId()) {
                case C0690R.C0693id.action_delete_label:
                    VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.SHORTCUT_MOUSE_CATEGORY_DELETE));
                    return;
                case C0690R.C0693id.manage_categories:
                    RunOptionMenu.getInstance().manageCategories();
                    return;
                case C0690R.C0693id.manage_label_popup_rename:
                    VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.SHORTCUT_MOUSE_CATEGORY_RENAME));
                    return;
                default:
                    return;
            }
        } else {
            switch (menuItem.getItemId()) {
                case C0690R.C0693id.action_trash_delete:
                    RunOptionMenu.getInstance().delete(i);
                    return;
                case C0690R.C0693id.action_trash_restore:
                    RunOptionMenu.getInstance().restore(i);
                    return;
                case C0690R.C0693id.import_from_app:
                    RunOptionMenu.getInstance().importFromApp();
                    return;
                case C0690R.C0693id.option_contact_us:
                    RunOptionMenu.getInstance().contactUs();
                    return;
                case C0690R.C0693id.option_delete:
                    RunOptionMenu.getInstance().delete(i);
                    return;
                case C0690R.C0693id.option_details:
                    if (!DialogFactory.isDialogVisible(appCompatActivity.getSupportFragmentManager(), DialogFactory.DETAIL_DIALOG)) {
                        RunOptionMenu.getInstance().showDetails(i);
                        return;
                    }
                    return;
                case C0690R.C0693id.option_edit:
                    RunOptionMenu.getInstance().edit();
                    return;
                case C0690R.C0693id.option_move:
                    RunOptionMenu.getInstance().move();
                    return;
                case C0690R.C0693id.option_move_to_secure_folder:
                    RunOptionMenu.getInstance().moveToSecureFolder(appCompatActivity, i);
                    return;
                case C0690R.C0693id.option_remove_from_nfc_tag:
                    RunOptionMenu.getInstance().startNFCWritingActivity(false, i);
                    return;
                case C0690R.C0693id.option_remove_from_secure_folder:
                    RunOptionMenu.getInstance().removeFromSecureFolder(appCompatActivity, i);
                    return;
                case C0690R.C0693id.option_rename:
                    RunOptionMenu.getInstance().showRenameDialog(appCompatActivity, i);
                    return;
                case C0690R.C0693id.option_select:
                    MouseKeyboardProvider.getInstance().setSelectModeByEditOption(true);
                    RunOptionMenu.getInstance().select();
                    return;
                case C0690R.C0693id.option_settings:
                    RunOptionMenu.getInstance().settings();
                    return;
                case C0690R.C0693id.option_share:
                    if (getId() == -1 && Engine.getInstance().getID() == -1) {
                        MouseKeyboardProvider.getInstance().setShareSelectMode(true);
                        if (i == 7) {
                            VoiceNoteObservable.getInstance().notifyObservers(13);
                            return;
                        } else {
                            VoiceNoteObservable.getInstance().notifyObservers(6);
                            return;
                        }
                    } else {
                        RunOptionMenu.getInstance().share(i);
                        return;
                    }
                case C0690R.C0693id.option_sort_by:
                    RunOptionMenu.getInstance().showSortByDialog(appCompatActivity);
                    return;
                case C0690R.C0693id.option_write_to_nfc_tag:
                    if (PermissionProvider.checkPhonePermission(appCompatActivity, 5, C0690R.string.voice_label, false)) {
                        RunOptionMenu.getInstance().startNFCWritingActivity(true, i);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }
}
