package com.sec.android.app.voicenote.p007ui.dialog;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.p007ui.AbsDialogFragment;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.StorageProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import java.util.Stack;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.DialogFactory */
public class DialogFactory {
    public static final String BUNDLE_DATA_CHECK_MODULE = "data_check_module";
    public static final String BUNDLE_DELETE_FILE_IN_SDCARD = "delete_file_in_sdcard";
    public static final String BUNDLE_DELETING_FILE = "deleting_file";
    public static final String BUNDLE_DIALOG_TAG = "dialog_tag";
    public static final String BUNDLE_FROM_EDGE_STATE = "fromEdge";
    public static final String BUNDLE_GDPR_COUNTRY = "gdpr";
    public static final String BUNDLE_ID = "id";
    public static final String BUNDLE_IDS = "ids";
    public static final String BUNDLE_LABEL_ID = "label_id";
    public static final String BUNDLE_MESSAGE_ID = "message_id";
    public static final String BUNDLE_MESSAGE_LIST_IDS = "message_list_id";
    public static final String BUNDLE_NAME = "name";
    public static final String BUNDLE_NEGATIVE_BTN_EVENT = "negative_btn_event";
    public static final String BUNDLE_NEGATIVE_BTN_ID = "negative_btn_id";
    public static final String BUNDLE_NEUTRAL_BTN_EVENT = "neutral_btn_event";
    public static final String BUNDLE_NEUTRAL_BTN_ID = "neutral_btn_id";
    public static final String BUNDLE_PATH = "path";
    public static final String BUNDLE_PERMISSION_LIST_IDS = "permission_list_id";
    public static final String BUNDLE_POSITIVE_BTN_EVENT = "positive_btn_event";
    public static final String BUNDLE_POSITIVE_BTN_ID = "positive_btn_id";
    public static final String BUNDLE_PRIVATE_MODE = "private_mode";
    public static final String BUNDLE_RECORD_MODE = "record_mode";
    public static final String BUNDLE_REQUEST_CODE = "request_code";
    public static final String BUNDLE_RESULT_CODE = "result_code";
    public static final String BUNDLE_SCENE = "scene";
    public static final String BUNDLE_SHARE_VOICE_MEMO = "share_memo";
    public static final String BUNDLE_TITLE_ID = "title_id";
    public static final String BUNDLE_URIS = "uris";
    public static final String BUNDLE_WORD = "word";
    public static final String CATEGORY_RENAME = "CategoryRenameDialog";
    public static final String DATA_CHECK_DIALOG = "DataCheckDialog";
    public static final String DELETE_CATEGORY_DIALOG = "DeleteCategoryDialog";
    public static final String DELETE_DIALOG = "DeleteDialog";
    public static final String DETAIL_DIALOG = "DetailDialog";
    private static Stack<String> DIALOG_STACK = new Stack<>();
    public static final String DISABLE_NFC_DIALOG = "DisableNFCDialog";
    public static final String EDIT_BOOKMARK_TITLE = "EditBookmarkTitle";
    public static final String EDIT_CANCEL_DIALOG = "EditCancelDialog";
    public static final String EDIT_PROGRESS_DIALOG = "EditProgressDialog";
    public static final String EDIT_SAVE_DIALOG = "EditSaveDialog";
    public static final String EDIT_STT_DIALOG = "EditSttDialog";
    public static final String EMPTY_TRASH_DIALOG = "EmptyTrashDialog";
    public static final String ENABLE_NFC_DIALOG = "EnableNFCDialog";
    public static final String ENCODING_PROGRESS_DIALOG = "EncodingProgressDialog";
    public static final String MODE_NOT_SUPPORTED = "ModeNotSupported";
    public static final String MOVE_TO_TRASH_DIALOG = "MoveToTrashDialog";
    public static final String NETWORK_NOT_CONNECTED = "NetworkNotConnected";
    public static final String PERMISSION_DIALOG = "PermissionDialog";
    public static final String PRIVACY_POLICY_DIALOG = "PrivacyPolicyDialog";
    public static final String RECORD_CANCEL_DIALOG = "RecordCancelDialog";
    public static final String REJECT_CALL_INFO_DIALOG = "RejectCallInfoDialog";
    public static final String RENAME_DIALOG = "RenameDialog";
    public static final String RESET_NFC_TAG_DIALOG = "ResetNFCTagDialog";
    public static final String SD_CARD_SELECT_DIALOG = "SDCardSelectDialog";
    public static final String SELECT_SHARE_CONTENT_DIALOG = "SelectShareContentDialog";
    public static final String SLEEPING_APP_WARNING = "SleepingAppWarning";
    public static final String SORT_BY_DIALOG = "SortBy";
    public static final String STORAGE_CHANGE_DIALOG = "StorageChangeDialog";
    public static final String STORAGE_FULL_DIALOG = "StorageFullDialog";
    private static final String TAG = "DialogFactory";
    public static final String TRANSLATION_CANCEL_DIALOG = "TranslationCancelDialog";
    public static final String TURN_ON_TRASH_DIALOG = "TurnOnOffTrashDialog";
    public static final String UNABLE_NFC_TAG_DIALOG = "UnableNFCTagDialog";
    private static DialogDestroyListener sDialogDestroyListener = $$Lambda$DialogFactory$iJn99fYh74yWQQopJ1x426FRM.INSTANCE;

    /* renamed from: com.sec.android.app.voicenote.ui.dialog.DialogFactory$DialogDestroyListener */
    public interface DialogDestroyListener {
        void onDialogDestroy(FragmentManager fragmentManager);
    }

    /* renamed from: com.sec.android.app.voicenote.ui.dialog.DialogFactory$DialogResultListener */
    public interface DialogResultListener {
        void onDialogResult(DialogFragment dialogFragment, Bundle bundle);
    }

    /* renamed from: com.sec.android.app.voicenote.ui.dialog.DialogFactory$RequestCode */
    public static class RequestCode {
        public static final int ADD_CATEGORY = 13;
        public static final int CHANGE_STORAGE = 12;
        public static final int DATA_CHECK_POPUP = 8;
        public static final int DISABLE_NFC_TAG = 4;
        public static final int EDIT_STT_UPDATE = 3;
        public static final int ENABLE_NFC = 14;
        public static final int MOVE_TO_PRIVATE = 6;
        public static final int NETWORK_NOT_CONNECTION = 10;
        public static final int PERMISSION = 7;
        public static final int POLICY_INFO = 9;
        public static final int RENAME = 1;
        public static final int RENAME_BOOKMARK_TITLE = 16;
        public static final int RESET_NFC_TAG = 5;
        public static final int SAVE_CONVERT_FILE_STT = 17;
        public static final int SAVE_NEWNAME = 11;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static androidx.fragment.app.DialogFragment show(androidx.fragment.app.FragmentManager r2, java.lang.String r3, android.os.Bundle r4, com.sec.android.app.voicenote.p007ui.dialog.DialogFactory.DialogResultListener r5) {
        /*
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "show - tag : "
            r0.append(r1)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "DialogFactory"
            com.sec.android.app.voicenote.provider.Log.m26i(r1, r0)
            int r0 = r3.hashCode()
            switch(r0) {
                case -1839575106: goto L_0x0177;
                case -1812727115: goto L_0x016c;
                case -1537998255: goto L_0x0161;
                case -1520791372: goto L_0x0156;
                case -1507366677: goto L_0x014b;
                case -1442764755: goto L_0x0140;
                case -1355245456: goto L_0x0135;
                case -1035823373: goto L_0x012a;
                case -932804956: goto L_0x011f;
                case -402732474: goto L_0x0113;
                case -353690601: goto L_0x0107;
                case -296579514: goto L_0x00fc;
                case -230890678: goto L_0x00f0;
                case -219794785: goto L_0x00e4;
                case 158766899: goto L_0x00d8;
                case 227179110: goto L_0x00cc;
                case 253433113: goto L_0x00c1;
                case 487913235: goto L_0x00b6;
                case 489238161: goto L_0x00aa;
                case 545201068: goto L_0x009f;
                case 632220390: goto L_0x0093;
                case 841759870: goto L_0x0087;
                case 944913971: goto L_0x007b;
                case 1046437747: goto L_0x0070;
                case 1204841199: goto L_0x0065;
                case 1273390450: goto L_0x005a;
                case 1397686456: goto L_0x004e;
                case 1726133509: goto L_0x0042;
                case 1759572580: goto L_0x0037;
                case 1853190547: goto L_0x002b;
                case 2056492194: goto L_0x001f;
                default: goto L_0x001d;
            }
        L_0x001d:
            goto L_0x0182
        L_0x001f:
            java.lang.String r0 = "PrivacyPolicyDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 26
            goto L_0x0183
        L_0x002b:
            java.lang.String r0 = "TranslationCancelDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 25
            goto L_0x0183
        L_0x0037:
            java.lang.String r0 = "CategoryRenameDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 5
            goto L_0x0183
        L_0x0042:
            java.lang.String r0 = "SDCardSelectDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 22
            goto L_0x0183
        L_0x004e:
            java.lang.String r0 = "EditBookmarkTitle"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 23
            goto L_0x0183
        L_0x005a:
            java.lang.String r0 = "StorageFullDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 6
            goto L_0x0183
        L_0x0065:
            java.lang.String r0 = "EditSaveDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 2
            goto L_0x0183
        L_0x0070:
            java.lang.String r0 = "RecordCancelDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 0
            goto L_0x0183
        L_0x007b:
            java.lang.String r0 = "RejectCallInfoDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 13
            goto L_0x0183
        L_0x0087:
            java.lang.String r0 = "ModeNotSupported"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 15
            goto L_0x0183
        L_0x0093:
            java.lang.String r0 = "ResetNFCTagDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 12
            goto L_0x0183
        L_0x009f:
            java.lang.String r0 = "EditCancelDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 1
            goto L_0x0183
        L_0x00aa:
            java.lang.String r0 = "DeleteCategoryDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 18
            goto L_0x0183
        L_0x00b6:
            java.lang.String r0 = "StorageChangeDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 7
            goto L_0x0183
        L_0x00c1:
            java.lang.String r0 = "DetailDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 3
            goto L_0x0183
        L_0x00cc:
            java.lang.String r0 = "SleepingAppWarning"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 27
            goto L_0x0183
        L_0x00d8:
            java.lang.String r0 = "EmptyTrashDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 29
            goto L_0x0183
        L_0x00e4:
            java.lang.String r0 = "EditProgressDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 24
            goto L_0x0183
        L_0x00f0:
            java.lang.String r0 = "UnableNFCTagDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 11
            goto L_0x0183
        L_0x00fc:
            java.lang.String r0 = "RenameDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 4
            goto L_0x0183
        L_0x0107:
            java.lang.String r0 = "PermissionDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 21
            goto L_0x0183
        L_0x0113:
            java.lang.String r0 = "DataCheckDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 20
            goto L_0x0183
        L_0x011f:
            java.lang.String r0 = "NetworkNotConnected"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 16
            goto L_0x0183
        L_0x012a:
            java.lang.String r0 = "DeleteDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 17
            goto L_0x0183
        L_0x0135:
            java.lang.String r0 = "EnableNFCDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 9
            goto L_0x0183
        L_0x0140:
            java.lang.String r0 = "TurnOnOffTrashDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 28
            goto L_0x0183
        L_0x014b:
            java.lang.String r0 = "DisableNFCDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 10
            goto L_0x0183
        L_0x0156:
            java.lang.String r0 = "MoveToTrashDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 30
            goto L_0x0183
        L_0x0161:
            java.lang.String r0 = "EditSttDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 8
            goto L_0x0183
        L_0x016c:
            java.lang.String r0 = "SortBy"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 19
            goto L_0x0183
        L_0x0177:
            java.lang.String r0 = "SelectShareContentDialog"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0182
            r0 = 14
            goto L_0x0183
        L_0x0182:
            r0 = -1
        L_0x0183:
            switch(r0) {
                case 0: goto L_0x022b;
                case 1: goto L_0x0226;
                case 2: goto L_0x0221;
                case 3: goto L_0x021c;
                case 4: goto L_0x0217;
                case 5: goto L_0x0212;
                case 6: goto L_0x020d;
                case 7: goto L_0x0208;
                case 8: goto L_0x0203;
                case 9: goto L_0x01fe;
                case 10: goto L_0x01f9;
                case 11: goto L_0x01f4;
                case 12: goto L_0x01ef;
                case 13: goto L_0x01ea;
                case 14: goto L_0x01e5;
                case 15: goto L_0x01e0;
                case 16: goto L_0x01db;
                case 17: goto L_0x01d6;
                case 18: goto L_0x01d1;
                case 19: goto L_0x01cb;
                case 20: goto L_0x01c5;
                case 21: goto L_0x01bf;
                case 22: goto L_0x01b9;
                case 23: goto L_0x01b3;
                case 24: goto L_0x01ad;
                case 25: goto L_0x01a7;
                case 26: goto L_0x01a1;
                case 27: goto L_0x019b;
                case 28: goto L_0x0195;
                case 29: goto L_0x018f;
                case 30: goto L_0x0189;
                default: goto L_0x0186;
            }
        L_0x0186:
            r4 = 0
            goto L_0x022f
        L_0x0189:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = moveToTrashDialog(r4)
            goto L_0x022f
        L_0x018f:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createEmptyTrashDialog()
            goto L_0x022f
        L_0x0195:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createTurnOnTrashDialog(r4)
            goto L_0x022f
        L_0x019b:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createSleepingAppWarningDialog()
            goto L_0x022f
        L_0x01a1:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createPrivacyPolicyDialog(r4)
            goto L_0x022f
        L_0x01a7:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createTranslationCancelDialog(r4)
            goto L_0x022f
        L_0x01ad:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createProgressFragmentDialog(r4)
            goto L_0x022f
        L_0x01b3:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createEditBookmarkTitleDialog(r4, r5)
            goto L_0x022f
        L_0x01b9:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createSDCardSelectDialog(r4)
            goto L_0x022f
        L_0x01bf:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createPermissionDialog(r4)
            goto L_0x022f
        L_0x01c5:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createDataCheckDialog(r4, r5)
            goto L_0x022f
        L_0x01cb:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createSortByDialog(r4)
            goto L_0x022f
        L_0x01d1:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createDeleteCategoryDialog(r4)
            goto L_0x022f
        L_0x01d6:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createDeleteDialog(r4)
            goto L_0x022f
        L_0x01db:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createNetworkNotConnectedDialog(r4, r5)
            goto L_0x022f
        L_0x01e0:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createModeNotSupportedDialog(r4)
            goto L_0x022f
        L_0x01e5:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createSelectShareContentDialog(r4)
            goto L_0x022f
        L_0x01ea:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createRejectCallInfoDialog(r4)
            goto L_0x022f
        L_0x01ef:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createResetNFCTagDialog(r4, r5)
            goto L_0x022f
        L_0x01f4:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createUnableNFCTagDialog(r4)
            goto L_0x022f
        L_0x01f9:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createDisableNFCDialog(r4, r5)
            goto L_0x022f
        L_0x01fe:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createEnableNFCDialog(r4, r5)
            goto L_0x022f
        L_0x0203:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createEditSttDialog(r4, r5)
            goto L_0x022f
        L_0x0208:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createStorageChangeDialog(r4, r5)
            goto L_0x022f
        L_0x020d:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createStorageFullDialog(r4)
            goto L_0x022f
        L_0x0212:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createRenameCategoryDialog(r4, r5)
            goto L_0x022f
        L_0x0217:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createRenameDialog(r4, r5)
            goto L_0x022f
        L_0x021c:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createDetailDialog(r4)
            goto L_0x022f
        L_0x0221:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createEditSaveDialog(r4)
            goto L_0x022f
        L_0x0226:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createEditCancelDialog(r4)
            goto L_0x022f
        L_0x022b:
            com.sec.android.app.voicenote.ui.AbsDialogFragment r4 = createRecordCancelDialog(r4)
        L_0x022f:
            if (r4 == 0) goto L_0x025f
            boolean r5 = r4.isAdded()
            if (r5 != 0) goto L_0x025f
            java.util.Stack<java.lang.String> r5 = DIALOG_STACK
            r5.push(r3)
            r4.show((androidx.fragment.app.FragmentManager) r2, (java.lang.String) r3)     // Catch:{ IllegalStateException -> 0x0245 }
            com.sec.android.app.voicenote.ui.dialog.DialogFactory$DialogDestroyListener r2 = sDialogDestroyListener     // Catch:{ IllegalStateException -> 0x0245 }
            r4.setDialogDestroyListener(r2)     // Catch:{ IllegalStateException -> 0x0245 }
            goto L_0x025f
        L_0x0245:
            r2 = move-exception
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "show IllegalStateException : "
            r3.append(r5)
            r3.append(r2)
            java.lang.String r2 = r3.toString()
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r2)
            java.util.Stack<java.lang.String> r2 = DIALOG_STACK
            r2.pop()
        L_0x025f:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.dialog.DialogFactory.show(androidx.fragment.app.FragmentManager, java.lang.String, android.os.Bundle, com.sec.android.app.voicenote.ui.dialog.DialogFactory$DialogResultListener):androidx.fragment.app.DialogFragment");
    }

    public static DialogFragment show(FragmentManager fragmentManager, String str, Bundle bundle) {
        return show(fragmentManager, str, bundle, (DialogResultListener) null);
    }

    public static boolean clearTopDialog(FragmentManager fragmentManager) {
        String pop;
        DialogFragment dialogFragment;
        if (DIALOG_STACK.isEmpty() || (pop = DIALOG_STACK.pop()) == null || (dialogFragment = (DialogFragment) fragmentManager.findFragmentByTag(pop)) == null) {
            return false;
        }
        dialogFragment.dismissAllowingStateLoss();
        return true;
    }

    public static boolean isDialogVisible(FragmentManager fragmentManager, String str) {
        if (DIALOG_STACK.isEmpty() || !DIALOG_STACK.contains(str)) {
            return false;
        }
        try {
            fragmentManager.executePendingTransactions();
            DialogFragment dialogFragment = (DialogFragment) fragmentManager.findFragmentByTag(str);
            if (dialogFragment == null || !dialogFragment.isAdded()) {
                return false;
            }
            return true;
        } catch (IllegalStateException e) {
            Log.m24e(TAG, "IllegalStateException occur ", (Throwable) e);
            DialogFragment dialogFragment2 = (DialogFragment) fragmentManager.findFragmentByTag(str);
            if (dialogFragment2 == null || !dialogFragment2.isAdded()) {
                return false;
            }
            return true;
        } catch (Throwable unused) {
            DialogFragment dialogFragment3 = (DialogFragment) fragmentManager.findFragmentByTag(str);
            if (dialogFragment3 == null || !dialogFragment3.isAdded()) {
                return false;
            }
            return true;
        }
    }

    public static boolean isDialogWithTagVisible(FragmentManager fragmentManager, String str) {
        DialogFragment dialogFragment = (DialogFragment) fragmentManager.findFragmentByTag(str);
        return dialogFragment != null && dialogFragment.isAdded();
    }

    public static String peek() {
        Stack<String> stack = DIALOG_STACK;
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        return DIALOG_STACK.peek();
    }

    public static void clearAllDialog(FragmentManager fragmentManager) {
        while (!DIALOG_STACK.isEmpty()) {
            DialogFragment dialogFragment = (DialogFragment) fragmentManager.findFragmentByTag(DIALOG_STACK.pop());
            if (dialogFragment != null) {
                dialogFragment.dismissAllowingStateLoss();
            }
        }
    }

    public static void clearDialogByTag(FragmentManager fragmentManager, String str) {
        if (fragmentManager == null) {
            Log.m22e(TAG, "clearDialogByTag - FragmentManager is null");
            return;
        }
        DialogFragment dialogFragment = (DialogFragment) fragmentManager.findFragmentByTag(str);
        if (dialogFragment != null) {
            try {
                dialogFragment.dismissAllowingStateLoss();
            } catch (IllegalStateException e) {
                Log.m24e(TAG, "IllegalStateException", (Throwable) e);
            }
            DIALOG_STACK.remove(str);
        }
    }

    public static void setDialogResultListener(FragmentManager fragmentManager, String str, DialogResultListener dialogResultListener) {
        AbsDialogFragment absDialogFragment = (AbsDialogFragment) fragmentManager.findFragmentByTag(str);
        if (absDialogFragment != null && absDialogFragment.isAdded()) {
            absDialogFragment.setDialogResultListener(dialogResultListener);
        }
    }

    public static void clickDialogButton(FragmentManager fragmentManager, String str, int i) {
        Button button;
        Log.m26i(TAG, "clickDialogButton");
        DialogFragment dialogFragment = (DialogFragment) fragmentManager.findFragmentByTag(str);
        if (dialogFragment != null) {
            AlertDialog alertDialog = (AlertDialog) dialogFragment.getDialog();
            if (alertDialog != null && (button = alertDialog.getButton(i)) != null) {
                button.performClick();
                return;
            }
            return;
        }
        Log.m22e(TAG, "clickDialogButton can not found dialog tag : " + str);
    }

    private static AbsDialogFragment createRecordCancelDialog(Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
            bundle.putInt(BUNDLE_TITLE_ID, -1);
            bundle.putInt(BUNDLE_MESSAGE_ID, C0690R.string.stop_recording_message);
            bundle.putInt(BUNDLE_POSITIVE_BTN_ID, C0690R.string.save);
            bundle.putInt(BUNDLE_NEUTRAL_BTN_ID, C0690R.string.cancel);
            bundle.putInt(BUNDLE_NEGATIVE_BTN_ID, C0690R.string.discard);
            bundle.putString(BUNDLE_DIALOG_TAG, RECORD_CANCEL_DIALOG);
            if (Settings.getIntSettings("record_mode", 1) == 4) {
                bundle.putInt(BUNDLE_POSITIVE_BTN_EVENT, Event.RECORD_STOP_DELAYED);
            } else {
                bundle.putInt(BUNDLE_POSITIVE_BTN_EVENT, 1004);
            }
            bundle.putInt(BUNDLE_NEGATIVE_BTN_EVENT, 1006);
            bundle.putInt(BUNDLE_NEUTRAL_BTN_EVENT, Event.HIDE_DIALOG);
        }
        return VoiceNoteAlertDialog.newInstance(bundle, (DialogResultListener) null);
    }

    private static AbsDialogFragment createEditCancelDialog(Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
            bundle.putInt(BUNDLE_TITLE_ID, -1);
            bundle.putInt(BUNDLE_MESSAGE_ID, C0690R.string.discard_changes);
            bundle.putInt(BUNDLE_POSITIVE_BTN_ID, C0690R.string.save);
            bundle.putInt(BUNDLE_NEUTRAL_BTN_ID, C0690R.string.cancel);
            bundle.putInt(BUNDLE_NEGATIVE_BTN_ID, C0690R.string.discard);
            bundle.putInt(BUNDLE_POSITIVE_BTN_EVENT, Event.EDIT_SAVE);
            bundle.putInt(BUNDLE_NEGATIVE_BTN_EVENT, Event.EDIT_CANCEL);
            bundle.putInt(BUNDLE_NEUTRAL_BTN_EVENT, Event.HIDE_DIALOG);
            bundle.putString(BUNDLE_DIALOG_TAG, EDIT_CANCEL_DIALOG);
        }
        return VoiceNoteAlertDialog.newInstance(bundle, (DialogResultListener) null);
    }

    private static AbsDialogFragment createEditSaveDialog(Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
            bundle.putInt(BUNDLE_TITLE_ID, C0690R.string.edit_title);
            bundle.putIntArray(BUNDLE_MESSAGE_LIST_IDS, new int[]{C0690R.string.save_as_new, C0690R.string.save_to_original});
            bundle.putString(BUNDLE_DIALOG_TAG, EDIT_SAVE_DIALOG);
        }
        return VoiceNoteAlertDialog.newInstance(bundle, (DialogResultListener) null);
    }

    private static AbsDialogFragment createDetailDialog(Bundle bundle) {
        return DetailsDialog.newInstance(bundle);
    }

    private static AbsDialogFragment createRenameDialog(Bundle bundle, DialogResultListener dialogResultListener) {
        return RenameDialog.newInstance(bundle, dialogResultListener);
    }

    private static AbsDialogFragment createRenameCategoryDialog(Bundle bundle, DialogResultListener dialogResultListener) {
        return CategoryRenameDialog.newInstance(bundle, dialogResultListener);
    }

    private static AbsDialogFragment createStorageFullDialog(Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
            bundle.putInt(BUNDLE_TITLE_ID, C0690R.string.not_enough_storage);
            if (!"mounted".equals(StorageProvider.getExternalStorageStateSd())) {
                bundle.putInt(BUNDLE_MESSAGE_ID, C0690R.string.unable_to_record_delete_some_files_and_try_again_internal);
            } else if (VoiceNoteFeature.FLAG_IS_TABLET) {
                bundle.putInt(BUNDLE_MESSAGE_ID, C0690R.string.unable_to_keep_recording_tablet);
            } else {
                bundle.putInt(BUNDLE_MESSAGE_ID, C0690R.string.unable_to_record_delete_some_files_and_try_again);
            }
            bundle.putInt(BUNDLE_POSITIVE_BTN_ID, 17039370);
        }
        return VoiceNoteAlertDialog.newInstance(bundle, (DialogResultListener) null);
    }

    private static AbsDialogFragment createStorageChangeDialog(Bundle bundle, DialogResultListener dialogResultListener) {
        if (bundle == null) {
            bundle = new Bundle();
            bundle.putInt(BUNDLE_REQUEST_CODE, 12);
            bundle.putInt(BUNDLE_TITLE_ID, C0690R.string.change_storage_location);
            if (Settings.getIntSettings(Settings.KEY_STORAGE, 0) == 0) {
                bundle.putInt(BUNDLE_MESSAGE_ID, C0690R.string.the_device_storage_is_full);
            } else {
                bundle.putInt(BUNDLE_MESSAGE_ID, C0690R.string.an_sd_card_is_full);
            }
            bundle.putInt(BUNDLE_POSITIVE_BTN_ID, C0690R.string.save_to_SD_card);
            bundle.putInt(BUNDLE_POSITIVE_BTN_EVENT, Event.CHANGE_STORAGE);
            bundle.putInt(BUNDLE_NEGATIVE_BTN_ID, C0690R.string.save_to_internal_storage);
            bundle.putString(BUNDLE_DIALOG_TAG, STORAGE_CHANGE_DIALOG);
        }
        return VoiceNoteAlertDialog.newInstance(bundle, dialogResultListener);
    }

    private static AbsDialogFragment createEditSttDialog(Bundle bundle, DialogResultListener dialogResultListener) {
        return EditSttDialog.newInstance(bundle, dialogResultListener);
    }

    private static AbsDialogFragment createEnableNFCDialog(Bundle bundle, DialogResultListener dialogResultListener) {
        if (bundle == null) {
            return null;
        }
        return NFCDialog.newInstance(bundle, dialogResultListener);
    }

    private static AbsDialogFragment createDisableNFCDialog(Bundle bundle, DialogResultListener dialogResultListener) {
        if (bundle == null) {
            return null;
        }
        return VoiceNoteAlertDialog.newInstance(bundle, dialogResultListener);
    }

    private static AbsDialogFragment createUnableNFCTagDialog(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        return VoiceNoteAlertDialog.newInstance(bundle, (DialogResultListener) null);
    }

    private static AbsDialogFragment createResetNFCTagDialog(Bundle bundle, DialogResultListener dialogResultListener) {
        if (bundle == null) {
            return null;
        }
        return VoiceNoteAlertDialog.newInstance(bundle, dialogResultListener);
    }

    private static AbsDialogFragment createRejectCallInfoDialog(Bundle bundle) {
        return RejectCallInfoDialog.newInstance(bundle);
    }

    private static AbsDialogFragment createSelectShareContentDialog(Bundle bundle) {
        return SelectShareContentsDialog.newInstance(bundle);
    }

    private static AbsDialogFragment createModeNotSupportedDialog(Bundle bundle) {
        return ModeNotSupportedDialog.newInstance(bundle);
    }

    private static AbsDialogFragment createNetworkNotConnectedDialog(Bundle bundle, DialogResultListener dialogResultListener) {
        return NetworkNotConnectedDialog.newInstance(bundle, dialogResultListener);
    }

    private static AbsDialogFragment createDeleteDialog(Bundle bundle) {
        return DeleteDialog.newInstance(bundle);
    }

    private static AbsDialogFragment createDeleteCategoryDialog(Bundle bundle) {
        return CategoryDeleteDialog.newInstance(bundle);
    }

    private static AbsDialogFragment createSortByDialog(Bundle bundle) {
        return SortByDialogFragment.newInstance(bundle);
    }

    private static AbsDialogFragment createPrivacyPolicyDialog(Bundle bundle) {
        return PrivacyPolicyDialog.newInstance(bundle);
    }

    private static AbsDialogFragment createDataCheckDialog(Bundle bundle, DialogResultListener dialogResultListener) {
        return DataCheckDialog.newInstance(bundle, dialogResultListener);
    }

    private static AbsDialogFragment createPermissionDialog(Bundle bundle) {
        return PermissionDialog.newInstance(bundle);
    }

    private static AbsDialogFragment createSDCardSelectDialog(Bundle bundle) {
        return SDCardSelectDialog.newInstance(bundle);
    }

    private static AbsDialogFragment createEditBookmarkTitleDialog(Bundle bundle, DialogResultListener dialogResultListener) {
        return BookmarkRenameDialog.newInstance(bundle, dialogResultListener);
    }

    private static AbsDialogFragment createProgressFragmentDialog(Bundle bundle) {
        return EditProgressDialog.newInstance(bundle);
    }

    private static AbsDialogFragment createTranslationCancelDialog(Bundle bundle) {
        return TranslationCancelDialog.newInstance(bundle);
    }

    private static AbsDialogFragment createSleepingAppWarningDialog() {
        return SleepingAppWarningDialog.newInstance();
    }

    private static AbsDialogFragment createTurnOnTrashDialog(Bundle bundle) {
        return TurnOnOffTrashDialog.newInstance(bundle);
    }

    private static AbsDialogFragment createEmptyTrashDialog() {
        return EmptyTrashDialog.newInstance();
    }

    private static AbsDialogFragment moveToTrashDialog(Bundle bundle) {
        return MoveToTrashDialog.newInstance(bundle);
    }

    static /* synthetic */ void lambda$static$0(FragmentManager fragmentManager) {
        for (Object obj : DIALOG_STACK.toArray()) {
            Fragment findFragmentByTag = fragmentManager.findFragmentByTag((String) obj);
            if (findFragmentByTag == null || findFragmentByTag.isRemoving()) {
                DIALOG_STACK.remove(obj);
            }
        }
    }
}
