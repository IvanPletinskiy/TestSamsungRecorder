package com.sec.android.app.voicenote.p007ui.actionbar;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.util.LongSparseArray;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;

import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.activity.ManageCategoriesActivity;
import com.sec.android.app.voicenote.activity.NFCWritingActivity;
import com.sec.android.app.voicenote.activity.SettingsActivity;
import com.sec.android.app.voicenote.common.util.AndroidForWork;
import com.sec.android.app.voicenote.common.util.TrashHelper;
import com.sec.android.app.voicenote.common.util.VNMediaScanner;
import com.sec.android.app.voicenote.p007ui.actionbar.RunOptionMenu;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.p007ui.dialog.SelectShareContentsDialog;
import com.sec.android.app.voicenote.provider.CallRejectChecker;
import com.sec.android.app.voicenote.provider.CheckedItemProvider;
import com.sec.android.app.voicenote.provider.ContextMenuProvider;
import com.sec.android.app.voicenote.provider.CursorProvider;
import com.sec.android.app.voicenote.provider.DBProvider;
import com.sec.android.app.voicenote.provider.DesktopModeProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.MouseKeyboardProvider;
import com.sec.android.app.voicenote.provider.NFCProvider;
import com.sec.android.app.voicenote.provider.PermissionProvider;
import com.sec.android.app.voicenote.provider.QuickConnectProvider;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.SecureFolderProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.StorageProvider;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.provider.UPSMProvider;
import com.sec.android.app.voicenote.receiver.ShareTaskReceiver;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.decoder.Decoder;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

/* renamed from: com.sec.android.app.voicenote.ui.actionbar.RunOptionMenu */
public class RunOptionMenu implements DialogFactory.DialogResultListener {
    private static final String APP_ID_FOR_CONTACT_US = "85gt322971";
    private static final String APP_NAME_FOR_CONTACT_US = "Voice Recorder";
    private static final String EASY_SHARE_MORE_QUICK_CONNECT = "more_actions_quick_connect";
    private static final String STRING_AUDIO_FILE_URI = "content://media/external/audio/media/";
    private static final String TAG = "RunOptionMenu";
    private static RunOptionMenu mRunOptionMenu;
    /* access modifiers changed from: private */
    public AppCompatActivity mActivity = null;
    boolean mDisableSpeakerOrReceive = false;
    /* access modifiers changed from: private */
    public VoiceNoteObservable mObservable = VoiceNoteObservable.getInstance();
    private IBinder mPrivateModeBinder = null;
//    private SemPrivateModeManager.StateListener mPrivateModeListener;
//    private SemPrivateModeManager mPrivateModeManager = null;
    private ProgressDialog mProgressMoveFileDialog = null;
    private int mScene;
    private SearchView mSearchView = null;
    /* access modifiers changed from: private */
    public shareTask mShareTask = null;

    private RunOptionMenu() {
        Log.m26i(TAG, "RunOptionMenu creator !!");
    }

    public static RunOptionMenu getInstance() {
        if (mRunOptionMenu == null) {
            mRunOptionMenu = new RunOptionMenu();
        }
        return mRunOptionMenu;
    }

    public void setContext(AppCompatActivity appCompatActivity) {
        this.mActivity = appCompatActivity;
    }

    public void onDestroy() {
        cancelTask();
        this.mActivity = null;
        this.mObservable = null;
        mRunOptionMenu = null;
    }

    public void onDialogResult(DialogFragment dialogFragment, Bundle bundle) {
        ActionBar supportActionBar;
        View customView;
        TextView textView;
        String string;
        IBinder iBinder;
        if (bundle != null) {
            int i = bundle.getInt(DialogFactory.BUNDLE_REQUEST_CODE);
            int i2 = bundle.getInt("result_code");
            Log.m26i(TAG, "onDialogResult - requestCode : " + i + " result : " + i2);
            if (i != 1) {
                if (i == 14) {
                    Log.m26i(TAG, "startNFCWritingActivity NFC is enabled");
                    Intent intent = new Intent(this.mActivity, NFCWritingActivity.class);
                    String string2 = bundle.getString(NFCWritingActivity.TAG_LABEL_INFO);
                    intent.putExtra(NFCWritingActivity.TAG_LABEL_INFO, string2);
                    Settings.setSettings(Settings.KEY_NFC_LABEL_INFO, string2);
                    try {
                        this.mActivity.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Log.m24e(TAG, "ActivityNotFoundException", (Throwable) e);
                    }
                } else if (i != 4) {
                    if (i != 5) {
                        if (i == 6) {
//                            SemPrivateModeManager semPrivateModeManager = this.mPrivateModeManager;
//                            if (!(semPrivateModeManager == null || (iBinder = this.mPrivateModeBinder) == null)) {
//                                if (i2 == -1) {
//                                    semPrivateModeManager.unregisterListener(iBinder, true);
//                                } else {
//                                    semPrivateModeManager.unregisterListener(iBinder, false);
//                                }
//                                this.mPrivateModeManager = null;
//                                this.mPrivateModeBinder = null;
//                            }
                            AppCompatActivity appCompatActivity = this.mActivity;
                            if (appCompatActivity != null) {
                                appCompatActivity.invalidateOptionsMenu();
                            }
                        }
                    } else if (i2 == -1) {
                        long j = bundle.getLong(DialogFactory.BUNDLE_ID, -1);
                        if (j >= 0) {
                            bundle.clear();
                            String path = CursorProvider.getInstance().getPath(j);
                            bundle.putLong(DialogFactory.BUNDLE_ID, j);
                            bundle.putString(DialogFactory.BUNDLE_PATH, path);
                            bundle.putInt(DialogFactory.BUNDLE_LABEL_ID, CursorProvider.findLabelID(this.mActivity, j));
                            bundle.putInt(DialogFactory.BUNDLE_REQUEST_CODE, 1);
                            DialogFactory.show(this.mActivity.getSupportFragmentManager(), DialogFactory.RENAME_DIALOG, bundle);
                        }
                    }
                } else if (i2 == -1) {
                    long j2 = bundle.getLong(DialogFactory.BUNDLE_ID, -1);
                    if (j2 >= 0) {
                        NFCProvider.deleteTagsData(this.mActivity, j2);
                    }
                    this.mObservable.notifyObservers(2);
                }
            } else if (i2 == -1 && (supportActionBar = this.mActivity.getSupportActionBar()) != null && (customView = supportActionBar.getCustomView()) != null && (textView = (TextView) customView.findViewById(C0690R.C0693id.optionbar_title)) != null && (string = bundle.getString(DialogFactory.BUNDLE_NAME)) != null && !string.isEmpty()) {
                textView.setText(string);
                Engine.getInstance().setUserSettingName(string);
            }
        }
    }

    public void showDetails(int i) {
        Log.m26i(TAG, "showDetails");
        if (this.mActivity == null) {
            Log.m26i(TAG, "showDetails mActivity is null");
            return;
        }
        Bundle bundle = new Bundle();
        if (ContextMenuProvider.getInstance().getId() != -1 && DesktopModeProvider.isDesktopMode()) {
            bundle.putLong(DialogFactory.BUNDLE_ID, ContextMenuProvider.getInstance().getId());
        } else if (i == 5 || i == 10) {
            ArrayList<Long> checkedItems = CheckedItemProvider.getCheckedItems();
            if (checkedItems.size() == 1) {
                bundle.putLong(DialogFactory.BUNDLE_ID, checkedItems.get(0).longValue());
            } else {
                return;
            }
        } else if (i == 4 || i == 3 || i == 7) {
            if (Engine.getInstance().getPlayerState() != 1) {
                if (i == 3) {
                    SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_list_miniplayer), this.mActivity.getResources().getString(C0690R.string.event_details_on_list));
                } else {
                    SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_player_comm), this.mActivity.getResources().getString(C0690R.string.event_player_detail));
                }
                bundle.putLong(DialogFactory.BUNDLE_ID, Engine.getInstance().getID());
            } else {
                return;
            }
        }
        DialogFactory.show(this.mActivity.getSupportFragmentManager(), DialogFactory.DETAIL_DIALOG, bundle);
    }

    public void translate() {
        Log.m26i(TAG, "translate");
        if (UPSMProvider.getInstance().isUltraPowerSavingMode()) {
            Log.m26i(TAG, " Ultra Power saving Mode enabled can not convert");
            AppCompatActivity appCompatActivity = this.mActivity;
            Toast.makeText(appCompatActivity, appCompatActivity.getString(C0690R.string.stt_convert_max_power_mode_error_msg), 0).show();
        } else if (StorageProvider.getAvailableStorage(Engine.getInstance().getPath()) <= 0) {
            DialogFactory.show(this.mActivity.getSupportFragmentManager(), DialogFactory.STORAGE_FULL_DIALOG, (Bundle) null);
        } else {
            if (Engine.getInstance().getPlayerState() == 3) {
                Engine.getInstance().pausePlay();
                this.mObservable.notifyObservers(Integer.valueOf(Event.PLAY_PAUSE));
            }
            Engine.getInstance().saveBookmarkBeforeTranslation();
            this.mObservable.notifyObservers(17);
            SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_CONVERT, -1);
        }
    }

    public void select() {
        Log.m26i(TAG, "select");
        if (this.mScene == 7 || MouseKeyboardProvider.getInstance().getCurrentScene() == 7) {
            this.mObservable.notifyObservers(13);
        } else {
            long idInOneItemCase = CursorProvider.getInstance().getIdInOneItemCase();
            if (idInOneItemCase != -1) {
                CheckedItemProvider.initCheckedList();
                CheckedItemProvider.toggle(idInOneItemCase);
            }
            this.mObservable.notifyObservers(6);
        }
        SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_list), this.mActivity.getResources().getString(C0690R.string.event_edit));
    }

    public void move() {
        Log.m26i(TAG, "move");
        ArrayList<Long> checkedItems = CheckedItemProvider.getCheckedItems();
        if (checkedItems.size() == 0 && ContextMenuProvider.getInstance().getId() != -1 && DesktopModeProvider.isDesktopMode()) {
            checkedItems = new ArrayList<>();
            checkedItems.add(Long.valueOf(ContextMenuProvider.getInstance().getId()));
            if (Engine.getInstance().getPlayerState() != 1) {
                Engine.getInstance().stopPlay();
                if (VoiceNoteApplication.getScene() == 3) {
                    VoiceNoteObservable.getInstance().notifyObservers(3);
                } else if (VoiceNoteApplication.getScene() == 7) {
                    VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.SEARCH_PLAY_STOP));
                }
            }
        }
        Intent intent = new Intent(this.mActivity, ManageCategoriesActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(ManageCategoriesActivity.KEY_BUNDLE_ENTER_MODE, 13);
        bundle.putSerializable("category_id", checkedItems);
        intent.putExtras(bundle);
        try {
            SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_edit), this.mActivity.getResources().getString(C0690R.string.event_edit_move));
            this.mActivity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.m24e(TAG, "ActivityNotFoundException", (Throwable) e);
        }
    }

    public void edit() {
        Log.m26i(TAG, "edit");
        if (Engine.getInstance().isRunningSwitchSkipMuted()) {
            Log.m26i(TAG, "edit - switching skip muted is running. return!!");
        } else if (StorageProvider.getAvailableStorage(Engine.getInstance().getPath()) <= 0) {
            DialogFactory.show(this.mActivity.getSupportFragmentManager(), DialogFactory.STORAGE_FULL_DIALOG, (Bundle) null);
        } else {
            if (Engine.getInstance().getPlayerState() == 3) {
                Engine.getInstance().pausePlay();
                this.mObservable.notifyObservers(Integer.valueOf(Event.PLAY_PAUSE));
            }
            this.mObservable.notifyObservers(5);
            SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_player_comm), this.mActivity.getResources().getString(C0690R.string.event_player_editor));
        }
    }

    public void home(int i) {
        Log.m26i(TAG, "home");
        AppCompatActivity appCompatActivity = this.mActivity;
        if (appCompatActivity == null) {
            Log.m22e(TAG, "mActivity object is null, so return here");
            return;
        }
        if (i == 2) {
            SALogProvider.insertSALog(appCompatActivity.getResources().getString(C0690R.string.screen_list), this.mActivity.getResources().getString(C0690R.string.event_list_back));
        } else if (i == 3) {
            Engine.getInstance().stopPlay();
            this.mObservable.notifyObservers(3);
            SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_list), this.mActivity.getResources().getString(C0690R.string.event_list_back));
        } else if (i == 4) {
            SALogProvider.insertSALog(appCompatActivity.getResources().getString(C0690R.string.screen_player_comm), this.mActivity.getResources().getString(C0690R.string.event_player_back));
        } else if (i == 6) {
            SALogProvider.insertSALog(appCompatActivity.getResources().getString(C0690R.string.screen_edit_comm), this.mActivity.getResources().getString(C0690R.string.event_edit_back));
        } else if (i != 7) {
            if (i == 8) {
                SALogProvider.insertSALog(appCompatActivity.getResources().getString(C0690R.string.screen_recording_comm), this.mActivity.getResources().getString(C0690R.string.event_back));
            } else if (i == 12) {
                if (Decoder.getInstance().getTranslationState() == 1) {
                    SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_ready_convert_stt), this.mActivity.getResources().getString(C0690R.string.event_ready_convert_back));
                } else {
                    SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_convert_stt_progress), this.mActivity.getResources().getString(C0690R.string.event_convert_progress_back));
                }
            }
        }
        this.mActivity.onBackPressed();
    }

    public void showSortByDialog(AppCompatActivity appCompatActivity) {
        SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_list), this.mActivity.getResources().getString(C0690R.string.event_sort_by));
        DialogFactory.show(appCompatActivity.getSupportFragmentManager(), DialogFactory.SORT_BY_DIALOG, (Bundle) null);
    }

    public void showRenameDialog(AppCompatActivity appCompatActivity, int i) {
        String str;
        long j;
        Log.m26i(TAG, "showRenameDialog - scene : " + i);
        if (!DialogFactory.isDialogVisible(appCompatActivity.getSupportFragmentManager(), DialogFactory.RESET_NFC_TAG_DIALOG) && !DialogFactory.isDialogVisible(appCompatActivity.getSupportFragmentManager(), DialogFactory.RENAME_DIALOG)) {
            if (ContextMenuProvider.getInstance().getId() == -1 || !DesktopModeProvider.isDesktopMode()) {
                if (i != 3) {
                    if (i != 4) {
                        if (i != 5) {
                            if (i != 7) {
                                if (i != 10) {
                                    return;
                                }
                            }
                        }
                        ArrayList<Long> checkedItems = CheckedItemProvider.getCheckedItems();
                        if (checkedItems.isEmpty()) {
                            Log.m26i(TAG, "showRenameDialog list is empty");
                            return;
                        }
                        long longValue = checkedItems.get(0).longValue();
                        if (NFCProvider.hasTagData((Context) appCompatActivity, longValue)) {
                            showNFCRenameDialog(appCompatActivity, longValue);
                            return;
                        }
                        String path = CursorProvider.getInstance().getPath(checkedItems.get(0).longValue());
                        SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_edit), this.mActivity.getResources().getString(C0690R.string.event_edit_rename));
                        long j2 = longValue;
                        str = path;
                        j = j2;
                    } else {
                        str = Engine.getInstance().getPath();
                        j = DBProvider.getInstance().getIdByPath(str);
                        SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_player_comm), this.mActivity.getResources().getString(C0690R.string.event_player_rename));
                        if (NFCProvider.hasTagData((Context) appCompatActivity, j)) {
                            showNFCRenameDialog(appCompatActivity, j);
                            return;
                        }
                    }
                }
                if (Engine.getInstance().getPlayerState() != 1) {
                    SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_list_miniplayer), this.mActivity.getResources().getString(C0690R.string.event_rename_on_list));
                    str = Engine.getInstance().getPath();
                    j = DBProvider.getInstance().getIdByPath(str);
                    if (NFCProvider.hasTagData((Context) appCompatActivity, j)) {
                        showNFCRenameDialog(appCompatActivity, j);
                        return;
                    }
                } else {
                    return;
                }
            } else {
                Log.m26i(TAG, "showRenameDialog in ? ");
                j = ContextMenuProvider.getInstance().getId();
                str = CursorProvider.getInstance().getPath(j);
                if (NFCProvider.hasTagData((Context) appCompatActivity, j)) {
                    showNFCRenameDialog(appCompatActivity, j);
                    return;
                }
            }
            Bundle bundle = new Bundle();
            bundle.putString(DialogFactory.BUNDLE_PATH, str);
            bundle.putInt("record_mode", CursorProvider.getInstance().getRecordMode(j));
            bundle.putLong(DialogFactory.BUNDLE_ID, j);
            bundle.putInt(DialogFactory.BUNDLE_LABEL_ID, CursorProvider.findLabelID(this.mActivity, j));
            bundle.putInt(DialogFactory.BUNDLE_REQUEST_CODE, 1);
            bundle.putInt(DialogFactory.BUNDLE_SCENE, i);
            DialogFactory.show(appCompatActivity.getSupportFragmentManager(), DialogFactory.RENAME_DIALOG, bundle);
        }
    }

    private void showNFCRenameDialog(AppCompatActivity appCompatActivity, long j) {
        Log.m26i(TAG, "showNFCRenameDialog has tag data - id : " + j);
        Bundle bundle = new Bundle();
        bundle.putLong(DialogFactory.BUNDLE_ID, j);
        bundle.putInt(DialogFactory.BUNDLE_LABEL_ID, CursorProvider.findLabelID(this.mActivity, j));
        bundle.putInt(DialogFactory.BUNDLE_REQUEST_CODE, 5);
        bundle.putInt(DialogFactory.BUNDLE_TITLE_ID, C0690R.string.rename);
        bundle.putInt(DialogFactory.BUNDLE_MESSAGE_ID, C0690R.string.tag_will_be_reset);
        bundle.putInt(DialogFactory.BUNDLE_POSITIVE_BTN_ID, 17039370);
        bundle.putInt(DialogFactory.BUNDLE_NEGATIVE_BTN_ID, 17039360);
        DialogFactory.show(appCompatActivity.getSupportFragmentManager(), DialogFactory.RESET_NFC_TAG_DIALOG, bundle, this);
    }

    public void delete(int i) {
        ArrayList<Long> arrayList;
        Log.m26i(TAG, "delete - scene : " + i);
        if (!this.mActivity.isDestroyed() && !DialogFactory.isDialogVisible(this.mActivity.getSupportFragmentManager(), DialogFactory.DELETE_DIALOG) && !DialogFactory.isDialogVisible(this.mActivity.getSupportFragmentManager(), DialogFactory.RENAME_DIALOG)) {
            if (ContextMenuProvider.getInstance().getId() == -1 || !DesktopModeProvider.isDesktopMode()) {
                if (!(i == 3 || i == 4)) {
                    if (i != 5) {
                        if (i != 7) {
                            if (i != 10) {
                                if (i == 14) {
                                    ArrayList<Long> checkedItems = CheckedItemProvider.getCheckedItems();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(DialogFactory.BUNDLE_IDS, checkedItems);
                                    bundle.putInt(DialogFactory.BUNDLE_SCENE, i);
                                    DialogFactory.show(this.mActivity.getSupportFragmentManager(), DialogFactory.DELETE_DIALOG, bundle);
                                    return;
                                }
                                return;
                            }
                        }
                    }
                    arrayList = CheckedItemProvider.getCheckedItems();
                    SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_edit), this.mActivity.getResources().getString(C0690R.string.event_edit_del), (long) arrayList.size());
                }
                if (Engine.getInstance().getPlayerState() != 1) {
                    ArrayList<Long> arrayList2 = new ArrayList<>();
                    arrayList2.add(Long.valueOf(Engine.getInstance().getID()));
                    if (i == 3) {
                        SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_list_miniplayer), this.mActivity.getResources().getString(C0690R.string.event_del_on_list));
                    } else {
                        SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_player_comm), this.mActivity.getResources().getString(C0690R.string.event_player_del));
                    }
                    arrayList = arrayList2;
                } else {
                    return;
                }
            } else {
                arrayList = new ArrayList<>();
                arrayList.add(Long.valueOf(ContextMenuProvider.getInstance().getId()));
                if (i == 13 || i == 14) {
                    Bundle bundle2 = new Bundle();
                    bundle2.putSerializable(DialogFactory.BUNDLE_IDS, arrayList);
                    bundle2.putInt(DialogFactory.BUNDLE_SCENE, 14);
                    DialogFactory.show(this.mActivity.getSupportFragmentManager(), DialogFactory.DELETE_DIALOG, bundle2);
                    return;
                }
            }
            boolean booleanSettings = Settings.getBooleanSettings(Settings.KEY_IS_FIRST_DELETE_VOICE_FILE, true);
            boolean booleanSettings2 = Settings.getBooleanSettings(Settings.KEY_TRASH_IS_TURN_ON, false);
            if (!booleanSettings || booleanSettings2) {
                Settings.setSettings(Settings.KEY_IS_FIRST_DELETE_VOICE_FILE, false);
                Bundle bundle3 = new Bundle();
                bundle3.putSerializable(DialogFactory.BUNDLE_IDS, arrayList);
                bundle3.putInt(DialogFactory.BUNDLE_SCENE, i);
                if (booleanSettings2 || DialogFactory.isDialogVisible(this.mActivity.getSupportFragmentManager(), DialogFactory.DELETE_DIALOG)) {
                    String externalStorageStateSd = StorageProvider.getExternalStorageStateSd();
                    if (externalStorageStateSd != null && externalStorageStateSd.equals("mounted")) {
                        Iterator<Long> it = arrayList.iterator();
                        while (true) {
                            if (it.hasNext()) {
                                if (CursorProvider.getInstance().getPath(it.next().longValue()).startsWith(StorageProvider.getRootPath(1))) {
                                    bundle3.putBoolean(DialogFactory.BUNDLE_DELETE_FILE_IN_SDCARD, true);
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                    if (!DialogFactory.isDialogVisible(this.mActivity.getSupportFragmentManager(), DialogFactory.MOVE_TO_TRASH_DIALOG)) {
                        DialogFactory.show(this.mActivity.getSupportFragmentManager(), DialogFactory.MOVE_TO_TRASH_DIALOG, bundle3);
                        return;
                    }
                    return;
                }
                DialogFactory.show(this.mActivity.getSupportFragmentManager(), DialogFactory.DELETE_DIALOG, bundle3);
                return;
            }
            Bundle bundle4 = new Bundle();
            bundle4.putBoolean(DialogFactory.BUNDLE_DELETING_FILE, true);
            bundle4.putSerializable(DialogFactory.BUNDLE_IDS, arrayList);
            bundle4.putInt(DialogFactory.BUNDLE_SCENE, i);
            if (!DialogFactory.isDialogVisible(this.mActivity.getSupportFragmentManager(), DialogFactory.TURN_ON_TRASH_DIALOG)) {
                DialogFactory.show(this.mActivity.getSupportFragmentManager(), DialogFactory.TURN_ON_TRASH_DIALOG, bundle4);
            }
            Settings.setSettings(Settings.KEY_IS_FIRST_DELETE_VOICE_FILE, false);
        }
    }

    public void restore(int i) {
        this.mScene = i;
        Log.m26i(TAG, "restore");
        if (!this.mActivity.isDestroyed() && !DialogFactory.isDialogVisible(this.mActivity.getSupportFragmentManager(), DialogFactory.DELETE_DIALOG) && !DialogFactory.isDialogVisible(this.mActivity.getSupportFragmentManager(), DialogFactory.RENAME_DIALOG)) {
            ArrayList<Long> checkedItems = CheckedItemProvider.getCheckedItems();
            if (checkedItems.size() == 0 && DesktopModeProvider.isDesktopMode() && ContextMenuProvider.getInstance().getId() != -1) {
                checkedItems.add(Long.valueOf(ContextMenuProvider.getInstance().getId()));
            }
            TrashHelper.getInstance().startRestoreTask(checkedItems, i);
        }
    }

    public void search() {
        Log.m26i(TAG, "search");
        VoiceNoteObservable voiceNoteObservable = this.mObservable;
        if (voiceNoteObservable != null) {
            voiceNoteObservable.notifyObservers(Integer.valueOf(Event.START_SEARCH));
            SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_SEARCH, -1);
            SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_list), this.mActivity.getResources().getString(C0690R.string.event_search));
        }
    }

    public void trash() {
        Log.m26i(TAG, "trash");
        if (this.mObservable == null) {
            return;
        }
        if (Settings.getBooleanSettings(Settings.KEY_TRASH_IS_TURN_ON)) {
            this.mObservable.notifyObservers(Integer.valueOf(Event.OPEN_TRASH));
            SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_SEARCH, -1);
            SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_list), this.mActivity.getResources().getString(C0690R.string.event_trash));
            return;
        }
        DialogFactory.show(this.mActivity.getSupportFragmentManager(), DialogFactory.TURN_ON_TRASH_DIALOG, (Bundle) null);
    }

    public void editTrash() {
        Log.m26i(TAG, "editTrash");
        this.mObservable.notifyObservers(Integer.valueOf(Event.TRASH_SELECT));
    }

    public void emptyTrash() {
        Log.m26i(TAG, "emptyTrash");
        DialogFactory.show(this.mActivity.getSupportFragmentManager(), DialogFactory.EMPTY_TRASH_DIALOG, new Bundle());
    }

    public void share(int i) {
        if (this.mShareTask == null) {
            this.mShareTask = new shareTask(i);
            this.mShareTask.execute(new ArrayList[]{getSelectedList(i)});
        }
        if (i == 3) {
            SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_list_miniplayer), this.mActivity.getResources().getString(C0690R.string.event_share_on_list));
        } else if (i == 4) {
            SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_player_comm), this.mActivity.getResources().getString(C0690R.string.event_player_share));
        } else if (i == 5 || i == 10) {
            SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_edit), this.mActivity.getResources().getString(C0690R.string.event_edit_share), (long) CheckedItemProvider.getCheckedItems().size());
        }
    }

    public void manageCategories() {
        Log.m26i(TAG, "manageCategories");
        try {
            this.mActivity.startActivity(new Intent(this.mActivity, ManageCategoriesActivity.class));
            ContextMenuProvider.getInstance().setId(-1);
            SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_list_miniplayer), this.mActivity.getResources().getString(C0690R.string.event_manage_category));
        } catch (ActivityNotFoundException e) {
            Log.m24e(TAG, "ActivityNotFoundException", (Throwable) e);
        }
    }

    public void moveToSecureFolder(Activity activity, int i) {
        Log.m26i(TAG, "moveToSecureFolder");
        ArrayList<Long> selectedList = getSelectedList(i);
        if (i == 5) {
            this.mObservable.notifyObservers(7);
        } else if (i != 10) {
            Engine.getInstance().stopPlay();
        } else {
            this.mObservable.notifyObservers(14);
        }
        this.mObservable.notifyObservers(3);
        if (selectedList == null) {
            Log.m26i(TAG, "list is null - scene : " + i);
            return;
        }
        if (i == 4) {
            SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_player_comm), this.mActivity.getResources().getString(C0690R.string.event_player_move_to_secure));
        } else {
            SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_list_miniplayer), this.mActivity.getResources().getString(C0690R.string.event_move_to_secure_folder));
        }
        SecureFolderProvider.moveFilesToSecureFolder(activity, selectedList);
    }

    public void playWithReceiver(int i, boolean z) {
        Log.m26i(TAG, "playWithReceiver - " + z);
        this.mDisableSpeakerOrReceive = true;
        Settings.setSettings(Settings.KEY_PLAY_WITH_RECEIVER, z ^ true);
        if (i == 2) {
            Engine.getInstance().setPlayWithReceiver(z);
            this.mDisableSpeakerOrReceive = false;
            this.mObservable.notifyObservers(Integer.valueOf(Event.INVALIDATE_MENU));
        } else if (i != 3 && i != 4) {
        } else {
            if (Engine.getInstance().getPlayerState() == 3) {
                this.mObservable.notifyObservers(Integer.valueOf(VoiceNoteApplication.convertEvent(Event.PLAY_PAUSE)));
                Engine.getInstance().setPlayWithReceiver(z);
                Engine.getInstance().pausePlay();
                if (Engine.getInstance().getPlayerState() == 4) {
                    new Handler() {
                        public void handleMessage(Message message) {
                            int i = message.what;
                            if (i == 0) {
                                Engine.getInstance().resumePlay();
                                RunOptionMenu.this.mObservable.notifyObservers(Integer.valueOf(VoiceNoteApplication.convertEvent(Event.PLAY_RESUME)));
                                sendEmptyMessageDelayed(1, 300);
                            } else if (i == 1) {
                                RunOptionMenu runOptionMenu = RunOptionMenu.this;
                                runOptionMenu.mDisableSpeakerOrReceive = false;
                                runOptionMenu.mObservable.notifyObservers(Integer.valueOf(Event.INVALIDATE_MENU));
                            }
                            super.handleMessage(message);
                        }
                    }.sendEmptyMessageDelayed(0, 200);
                    return;
                }
                return;
            }
            Engine.getInstance().setPlayWithReceiver(z);
            this.mDisableSpeakerOrReceive = false;
            this.mObservable.notifyObservers(Integer.valueOf(Event.INVALIDATE_MENU));
        }
    }

    public void removeFromSecureFolder(Activity activity, int i) {
        Log.m26i(TAG, "removeFromSecureFolder");
        ArrayList<Long> selectedList = getSelectedList(i);
        if (i == 5) {
            this.mObservable.notifyObservers(7);
        } else if (i != 10) {
            Engine.getInstance().stopPlay();
        } else {
            this.mObservable.notifyObservers(14);
        }
        this.mObservable.notifyObservers(3);
        if (selectedList == null) {
            Log.m26i(TAG, "list is null - scene : " + i);
            return;
        }
        SecureFolderProvider.moveFilesOutOfSecureFolder(activity, selectedList);
    }

    public void importFromApp() {
        CursorProvider.getInstance().reload(this.mActivity.getSupportLoaderManager());
        this.mObservable.notifyObservers(10);
    }

    public void startNFCWritingActivity(boolean z, int i) {
        ArrayList<Long> arrayList;
        long j;
        Log.m26i(TAG, "startNFCWritingActivity - Scene : " + i + " bSet : " + z);
        if (z) {
            if (ContextMenuProvider.getInstance().getId() != -1 && DesktopModeProvider.isDesktopMode()) {
                j = ContextMenuProvider.getInstance().getId();
                this.mObservable.notifyObservers(7);
            } else if (i != 4) {
                if (i != 5) {
                    if (i != 10) {
                        Log.m26i(TAG, "startNFCWritingActivity abnormal scene");
                        return;
                    } else if (CheckedItemProvider.getCheckedItemCount() != 1) {
                        Log.m26i(TAG, "startNFCWritingActivity getCheckedItemCount() != 1");
                        return;
                    } else {
                        j = CheckedItemProvider.getCheckedItems().get(0).longValue();
                        this.mObservable.notifyObservers(14);
                    }
                } else if (CheckedItemProvider.getCheckedItemCount() != 1) {
                    Log.m26i(TAG, "startNFCWritingActivity getCheckedItemCount() != 1");
                    return;
                } else {
                    j = CheckedItemProvider.getCheckedItems().get(0).longValue();
                    this.mObservable.notifyObservers(7);
                }
            } else if (Engine.getInstance().getPlayerState() == 1) {
                Log.m26i(TAG, "startNFCWritingActivity PlayerState.IDLE");
                return;
            } else {
                j = Engine.getInstance().getID();
            }
            String currentLabelInfo = NFCProvider.getCurrentLabelInfo(this.mActivity, j);
            Log.m26i(TAG, "startNFCWritingActivity - labelInfo : " + currentLabelInfo);
            if (NFCProvider.isNFCEnabled(this.mActivity)) {
                Log.m26i(TAG, "startNFCWritingActivity NFC is enabled");
                Intent intent = new Intent(this.mActivity, NFCWritingActivity.class);
                intent.putExtra(NFCWritingActivity.TAG_LABEL_INFO, currentLabelInfo);
                Settings.setSettings(Settings.KEY_NFC_LABEL_INFO, currentLabelInfo);
                try {
                    this.mActivity.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.m24e(TAG, "ActivityNotFoundException", (Throwable) e);
                }
            } else {
                Log.m26i(TAG, "startNFCWritingActivity NFC is not enabled and do show dialog");
                Bundle bundle = new Bundle();
                bundle.putLong(DialogFactory.BUNDLE_ID, j);
                bundle.putString(NFCWritingActivity.TAG_LABEL_INFO, currentLabelInfo);
                DialogFactory.show(this.mActivity.getSupportFragmentManager(), DialogFactory.ENABLE_NFC_DIALOG, bundle);
            }
        } else {
            if (ContextMenuProvider.getInstance().getId() != -1 && DesktopModeProvider.isDesktopMode()) {
                arrayList = new ArrayList<>();
                arrayList.add(Long.valueOf(ContextMenuProvider.getInstance().getId()));
                this.mObservable.notifyObservers(7);
            } else if (i != 4) {
                if (i == 5) {
                    arrayList = CheckedItemProvider.getCheckedItems();
                    this.mObservable.notifyObservers(7);
                } else if (i == 10) {
                    arrayList = CheckedItemProvider.getCheckedItems();
                    this.mObservable.notifyObservers(14);
                } else {
                    return;
                }
            } else if (Engine.getInstance().getPlayerState() != 1) {
                arrayList = new ArrayList<>();
                arrayList.add(Long.valueOf(Engine.getInstance().getID()));
            } else {
                return;
            }
            if (arrayList.isEmpty()) {
                Log.m26i(TAG, "Disable NFC tag - selectedIDs is empty");
                return;
            }
            Log.m26i(TAG, "Disable NFC tag - id : " + arrayList.get(0));
            Bundle bundle2 = new Bundle();
            bundle2.putLong(DialogFactory.BUNDLE_ID, arrayList.get(0).longValue());
            bundle2.putInt(DialogFactory.BUNDLE_TITLE_ID, C0690R.string.remove_from_nfc_tag);
            bundle2.putInt(DialogFactory.BUNDLE_MESSAGE_ID, C0690R.string.untagged_item);
            bundle2.putInt(DialogFactory.BUNDLE_POSITIVE_BTN_ID, C0690R.string.remove);
            bundle2.putInt(DialogFactory.BUNDLE_NEGATIVE_BTN_ID, C0690R.string.cancel);
            bundle2.putInt(DialogFactory.BUNDLE_REQUEST_CODE, 4);
            DialogFactory.show(this.mActivity.getSupportFragmentManager(), DialogFactory.DISABLE_NFC_DIALOG, bundle2, this);
        }
    }

    public void setRejectCall(boolean z) {
        Log.m26i(TAG, "setRejectCall set = " + z);
        AppCompatActivity appCompatActivity = this.mActivity;
        if (appCompatActivity == null) {
            Log.m26i(TAG, "setRejectCall mActivity is null");
        } else if (!z) {
            CallRejectChecker.getInstance().setReject(false);
            this.mObservable.notifyObservers(Integer.valueOf(Event.RECORD_CALL_ALLOW));
        } else if (PermissionProvider.checkPhonePermission(appCompatActivity, 4, C0690R.string.call_reject_recording, false)) {
            CallRejectChecker.getInstance().setReject(true);
            this.mObservable.notifyObservers(Integer.valueOf(Event.RECORD_CALL_REJECT));
            SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_REJECT_CALL, -1);
        }
    }

    public void editSave() {
        Log.m26i(TAG, "editSave");
        if (Engine.getInstance().getRecorderState() != 1 && !Engine.getInstance().isSaveEnable()) {
            Log.m22e(TAG, "Can not save !!!");
        } else if (Engine.getInstance().getEngineState() == 2) {
            Toast.makeText(this.mActivity, C0690R.string.please_wait, 0).show();
            Log.m22e(TAG, "Engine BUSY !!!!");
        } else if (DialogFactory.isDialogVisible(this.mActivity.getSupportFragmentManager(), DialogFactory.EDIT_SAVE_DIALOG)) {
            Log.m26i(TAG, "EDIT_SAVE_DIALOG already exist !!");
        } else if (Engine.getInstance().getRecorderState() == 2) {
            if (Engine.getInstance().pauseRecord()) {
                this.mObservable.notifyObservers(Integer.valueOf(Event.EDIT_RECORD_SAVE));
            }
        } else if (Engine.getInstance().isEditSaveEnable()) {
            SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_edit_comm), this.mActivity.getResources().getString(C0690R.string.event_edit_save));
            DialogFactory.show(this.mActivity.getSupportFragmentManager(), DialogFactory.EDIT_SAVE_DIALOG, (Bundle) null);
        } else {
            Log.m22e(TAG, "Can not save !!!");
            Engine.getInstance().stopPlay();
            Engine.getInstance().clearContentItem();
            this.mObservable.notifyObservers(3);
        }
    }

    public void settings() {
        Log.m26i(TAG, "settings");
        try {
            Intent intent = new Intent(this.mActivity, SettingsActivity.class);
            SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_SETTINGS, 3);
            SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_ready_common), this.mActivity.getResources().getString(C0690R.string.event_settings));
            this.mActivity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.m24e(TAG, "ActivityNotFoundException", (Throwable) e);
        } catch (NullPointerException e2) {
            Log.m24e(TAG, "NullPointerException", (Throwable) e2);
        }
    }

    public void contactUs() {
        Log.m26i(TAG, "contactUs");
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("voc://view/contactUs"));
            intent.putExtra("packageName", this.mActivity.getPackageName());
            intent.putExtra("appId", APP_ID_FOR_CONTACT_US);
            intent.putExtra("appName", APP_NAME_FOR_CONTACT_US);
            SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_ready_common), this.mActivity.getResources().getString(C0690R.string.event_contact_us));
            this.mActivity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.m24e(TAG, "ActivityNotFoundException", (Throwable) e);
        } catch (NullPointerException e2) {
            Log.m24e(TAG, "NullPointerException", (Throwable) e2);
        }
    }

    /* access modifiers changed from: package-private */
    public void openList() {
        Log.m26i(TAG, "Event.OPEN_LIST");
        if (Engine.getInstance().getRecorderState() != 1) {
            Log.m26i(TAG, "Event.OPEN_LIST but recorder is running");
        } else if (this.mObservable != null) {
            Settings.setSettings(Settings.KEY_LIST_MODE, 0);
            SALogProvider.insertSALog(this.mActivity.getResources().getString(C0690R.string.screen_ready_common), this.mActivity.getResources().getString(C0690R.string.event_list));
            this.mObservable.notifyObservers(3);
        }
    }

    /* renamed from: com.sec.android.app.voicenote.ui.actionbar.RunOptionMenu$shareTask */
    private class shareTask extends AsyncTask<ArrayList<Long>, Integer, Boolean> {
        private Bundle mBundle;
        private ProgressDialog mProgressDialog;
        private int mScene;
        private Intent mShareIntent;

        private shareTask(int i) {
            this.mProgressDialog = null;
            this.mBundle = null;
            this.mShareIntent = null;
            this.mScene = i;
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            Log.m26i(RunOptionMenu.TAG, "shareTask onPreExecute");
            if (RunOptionMenu.this.mActivity == null) {
                Log.m22e(RunOptionMenu.TAG, "ActivityNotFoundException");
                return;
            }
            this.mProgressDialog = new ProgressDialog(RunOptionMenu.this.mActivity);
            this.mProgressDialog.setMessage(RunOptionMenu.this.mActivity.getString(C0690R.string.please_wait));
            this.mProgressDialog.setProgressStyle(0);
            this.mProgressDialog.setCancelable(false);
            this.mProgressDialog.getWindow().addFlags(128);
            this.mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public final void onCancel(DialogInterface dialogInterface) {
                    RunOptionMenu.shareTask.this.lambda$onPreExecute$0$RunOptionMenu$shareTask(dialogInterface);
                }
            });
            if (CheckedItemProvider.getCheckedItemCount() > 200) {
                this.mProgressDialog.show();
            }
            MouseKeyboardProvider.getInstance().changePointerIcon(RunOptionMenu.this.mActivity.getWindow().getDecorView(), RunOptionMenu.this.mActivity.getWindow().getDecorView().getContext(), 4);
            super.onPreExecute();
        }

        public /* synthetic */ void lambda$onPreExecute$0$RunOptionMenu$shareTask(DialogInterface dialogInterface) {
            Log.m29v(RunOptionMenu.TAG, "shareTask : Cancel interrupt!!!");
            this.mProgressDialog.dismiss();
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(ArrayList<Long>[] arrayListArr) {
            ArrayList<Long> arrayList = arrayListArr[0];
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            if (arrayList == null) {
                Log.m32w(RunOptionMenu.TAG, "shareFile list is null");
                return false;
            }
            LongSparseArray<String> listPathByIds = DBProvider.getInstance().getListPathByIds(arrayList);
            boolean isAndroidForWorkMode = AndroidForWork.getInstance().isAndroidForWorkMode(RunOptionMenu.this.mActivity);
            Iterator<Long> it = arrayList.iterator();
            while (it.hasNext()) {
                long longValue = it.next().longValue();
                if (longValue < 0) {
                    Log.m32w(RunOptionMenu.TAG, "shareTask doInBackground - wrong id");
                } else {
                    Uri parse = Uri.parse(RunOptionMenu.STRING_AUDIO_FILE_URI + longValue);
                    if ((listPathByIds != null ? listPathByIds.get(longValue) : null) == null) {
                        Log.m32w(RunOptionMenu.TAG, "shareTask doInBackground - path is null");
                        DBProvider.getInstance().getPathById(longValue);
                    }
                    if (isAndroidForWorkMode) {
                        parse = AndroidForWork.getInstance().changeUriForAndroidForWorkMode(parse);
                    }
                    arrayList2.add(parse);
                    if (DBProvider.getInstance().getRecordModeById(longValue) == 4) {
                        arrayList2.remove(parse);
                        arrayList3.add(parse);
                    }
                }
            }
            if (arrayList3.size() > 0) {
                this.mBundle = new Bundle();
                this.mBundle.putParcelableArrayList(SelectShareContentsDialog.SHARE_VOICE_FILE, arrayList2);
                this.mBundle.putParcelableArrayList(SelectShareContentsDialog.SHARE_MEMO_FILE, arrayList3);
                if (arrayList3.size() > 1) {
                    this.mBundle.putInt(DialogFactory.BUNDLE_SHARE_VOICE_MEMO, 2);
                } else {
                    this.mBundle.putInt(DialogFactory.BUNDLE_SHARE_VOICE_MEMO, 1);
                    long parseLong = Long.parseLong((String) Objects.requireNonNull(Uri.parse(String.valueOf(arrayList3.get(0))).getLastPathSegment()));
                    this.mBundle.putLong(DialogFactory.BUNDLE_ID, parseLong);
                    String checkSTT = SelectShareContentsDialog.checkSTT(parseLong);
                    if (checkSTT != null && DBProvider.getInstance().getContentExistCheckFromFiles(checkSTT) == -1) {
                        new VNMediaScanner(RunOptionMenu.this.mActivity).startScan(checkSTT);
                    }
                }
            } else if (arrayList2.size() > 1) {
                this.mShareIntent = new Intent("android.intent.action.SEND_MULTIPLE");
                this.mShareIntent.setType("audio/*");
                this.mShareIntent.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList2);
            } else if (arrayList2.size() != 1) {
                return false;
            } else {
                this.mShareIntent = new Intent("android.intent.action.SEND");
                this.mShareIntent.setType("audio/*");
                this.mShareIntent.putExtra("android.intent.extra.STREAM", (Parcelable) arrayList2.get(0));
            }
            if (QuickConnectProvider.getInstance().isInstalledQuickConnect(RunOptionMenu.this.mActivity)) {
                this.mShareIntent.putExtra(RunOptionMenu.EASY_SHARE_MORE_QUICK_CONNECT, 1);
            }
            return true;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            Log.m26i(RunOptionMenu.TAG, "shareTask onPostExecute result : " + bool);
            ProgressDialog progressDialog = this.mProgressDialog;
            if (progressDialog != null) {
                progressDialog.dismiss();
                this.mProgressDialog = null;
            }
            if (bool.booleanValue()) {
                if (this.mBundle != null) {
                    Log.m26i(RunOptionMenu.TAG, "shareTask onPostExecute show select dialog");
                    this.mBundle.putInt(DialogFactory.BUNDLE_SCENE, this.mScene);
                    DialogFactory.show(RunOptionMenu.this.mActivity.getSupportFragmentManager(), DialogFactory.SELECT_SHARE_CONTENT_DIALOG, this.mBundle);
                } else if (this.mShareIntent != null) {
                    Log.m26i(RunOptionMenu.TAG, "shareTask onPostExecute show chooser activity");
                    try {
                        this.mShareIntent.addFlags(64);
                        try {
                            if (this.mScene != 3) {
                                if (this.mScene != 4) {
                                    SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_LIST_SHARE, -1);
                                    RunOptionMenu.this.mActivity.startActivity(Intent.createChooser(this.mShareIntent, RunOptionMenu.this.mActivity.getString(C0690R.string.sharevia), PendingIntent.getBroadcast(RunOptionMenu.this.mActivity, 0, new Intent(RunOptionMenu.this.mActivity, ShareTaskReceiver.class), 134217728).getIntentSender()));
                                }
                            }
                            SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_PLAYER_SHARE, -1);
                            RunOptionMenu.this.mActivity.startActivity(Intent.createChooser(this.mShareIntent, RunOptionMenu.this.mActivity.getString(C0690R.string.sharevia), PendingIntent.getBroadcast(RunOptionMenu.this.mActivity, 0, new Intent(RunOptionMenu.this.mActivity, ShareTaskReceiver.class), 134217728).getIntentSender()));
                        } catch (ActivityNotFoundException e) {
                            Log.m24e(RunOptionMenu.TAG, "ActivityNotFoundException", (Throwable) e);
                        }
                    } catch (ActivityNotFoundException e2) {
                        Log.m24e("SendAppListDialog", "activity not found!", (Throwable) e2);
                    }
                }
            }
            MouseKeyboardProvider.getInstance().changePointerIcon(RunOptionMenu.this.mActivity.getWindow().getDecorView(), RunOptionMenu.this.mActivity.getWindow().getDecorView().getContext(), 1);
            shareTask unused = RunOptionMenu.this.mShareTask = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void deleteFile(int i, SearchView searchView) {
        this.mScene = i;
        this.mSearchView = searchView;
        TrashHelper.getInstance().startDeleteTask(getSelectedList(i), i);
    }

    public void initMoveFileDialog(int i, String str) {
        if (this.mProgressMoveFileDialog == null) {
            this.mProgressMoveFileDialog = new ProgressDialog(this.mActivity);
            this.mProgressMoveFileDialog.setMessage(str);
            this.mProgressMoveFileDialog.setProgressStyle(1);
            this.mProgressMoveFileDialog.setCancelable(false);
            this.mProgressMoveFileDialog.getWindow().addFlags(128);
            this.mProgressMoveFileDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public final void onCancel(DialogInterface dialogInterface) {
                    RunOptionMenu.this.lambda$initMoveFileDialog$0$RunOptionMenu(dialogInterface);
                }
            });
            this.mProgressMoveFileDialog.setProgress(0);
            this.mProgressMoveFileDialog.setMax(i);
            this.mProgressMoveFileDialog.show();
        }
    }

    public /* synthetic */ void lambda$initMoveFileDialog$0$RunOptionMenu(DialogInterface dialogInterface) {
        Log.m29v(TAG, "DeleteFiles : Cancel interrupt!!!");
        this.mProgressMoveFileDialog.dismiss();
    }

    public void updateProgressMoveFileDialog(int i, int i2) {
        ProgressDialog progressDialog = this.mProgressMoveFileDialog;
        if (progressDialog != null) {
            progressDialog.getProgress();
            this.mProgressMoveFileDialog.setProgress(i);
            return;
        }
        int selectedFileCount = TrashHelper.getInstance().getSelectedFileCount();
        if (selectedFileCount != 0) {
            initMoveFileDialog(i2, getTrashProgressMessage(selectedFileCount));
            this.mProgressMoveFileDialog.setProgress(i);
        }
    }

    private String getTrashProgressMessage(int i) {
        int state = TrashHelper.getInstance().getState();
        if (state == 2) {
            if (i > 1) {
                return this.mActivity.getResources().getString(C0690R.string.moving_recordings_to_the_trash);
            }
            return this.mActivity.getResources().getString(C0690R.string.moving_recording_to_the_trash);
        } else if (state == 5 || state == 4) {
            if (i > 1) {
                return this.mActivity.getResources().getString(C0690R.string.deleting_recordings);
            }
            return this.mActivity.getResources().getString(C0690R.string.deleting_recording);
        } else if (state != 3) {
            return "";
        } else {
            if (i > 1) {
                return this.mActivity.getResources().getString(C0690R.string.restoring_recordings);
            }
            return this.mActivity.getResources().getString(C0690R.string.restoring_recording);
        }
    }

    public void dismissProgressMoveFileDialog() {
        ProgressDialog progressDialog = this.mProgressMoveFileDialog;
        if (progressDialog != null) {
            progressDialog.dismiss();
            this.mProgressMoveFileDialog = null;
        }
    }

    private void cancelTask() {
        shareTask sharetask = this.mShareTask;
        if (sharetask != null) {
            sharetask.cancel(true);
            this.mShareTask = null;
        }
    }

    private ArrayList<Long> getSelectedList(int i) {
        if (!(i == 2 || i == 3)) {
            if (i != 4) {
                if (i != 5) {
                    if (i != 7) {
                        if (!(i == 9 || i == 10)) {
                            if (i != 13) {
                                if (i != 14) {
                                    return null;
                                }
                                return CheckedItemProvider.getCheckedItems();
                            }
                        }
                    }
                }
                return CheckedItemProvider.getCheckedItems();
            }
            ArrayList<Long> arrayList = new ArrayList<>();
            arrayList.add(Long.valueOf(Engine.getInstance().getID()));
            return arrayList;
        }
        if (ContextMenuProvider.getInstance().getId() != -1 && DesktopModeProvider.isDesktopMode()) {
            ArrayList<Long> arrayList2 = new ArrayList<>();
            arrayList2.add(Long.valueOf(ContextMenuProvider.getInstance().getId()));
            return arrayList2;
        }
        ArrayList<Long> arrayList3 = new ArrayList<>();
        arrayList3.add(Long.valueOf(Engine.getInstance().getID()));
        return arrayList3;
    }

    private String getTitle(String str) {
        String substring = str.substring(str.lastIndexOf(47) + 1);
        return substring.substring(0, substring.lastIndexOf(46));
    }
}
