package com.sec.android.app.voicenote.provider;

import android.os.Build;
import android.util.SparseArray;
import java.lang.reflect.Field;
import java.util.Locale;

public class Event {
    public static final int ADD_BOOKMARK = 996;
    public static final int ADD_HW_KEYBOARD = 11;
    private static final int BASE_EVENT_LAST = 999;
    private static final int BASE_EVENT_START = 0;
    private static final int BIXBY_EVENT_START = 30000;
    public static final int BIXBY_READY_TO_START_RECORDING = 29998;
    public static final int BIXBY_START_PLAYING = 29995;
    public static final int BIXBY_START_RECORDING = 29999;
    public static final int BIXBY_START_RECORDING_RESULT_FAIL = 29996;
    public static final int BIXBY_START_RECORDING_RESULT_SUCCESS = 29997;
    public static final int BLE_SPEN_ADD_BOOKMARK = 8003;
    private static final int BLE_SPEN_EVENT_START = 8000;
    public static final int BLE_SPEN_RECORD_PAUSE_RESUME = 8002;
    public static final int BLE_SPEN_RECORD_START = 8001;
    public static final int BLOCK_CONTROL_BUTTONS = 970;
    public static final int BLUETOOTH_SCO = 949;
    public static final int CHANGE_LIST_MODE = 962;
    public static final int CHANGE_MODE = 1998;
    public static final int CHANGE_SORT_MODE = 963;
    public static final int CHANGE_STORAGE = 967;
    public static final int CHANGE_STT_LANGUAGE = 981;
    public static final int CHOOSE_STT_LANGUAGE = 980;
    public static final int CHOOSE_WEB_TOS = 979;
    public static final int DELETE = 994;
    public static final int DELETE_CATEGORY = 964;
    public static final int DELETE_COMPLETE = 993;
    public static final int DELETE_TRASH_COMPLETE = 941;
    public static final int DESELECT_ALL = 984;
    public static final int DESELECT_MODE = 7;
    public static final int DIALOG_PROGRESS_MOVE_FILE = 939;
    public static final int EDIT = 5;
    public static final int EDIT_CANCEL = 5008;
    private static final int EDIT_EVENT_LAST = 5999;
    private static final int EDIT_EVENT_START = 5000;
    public static final int EDIT_PLAY_PAUSE = 5002;
    public static final int EDIT_PLAY_RESUME = 5003;
    public static final int EDIT_PLAY_START = 5001;
    public static final int EDIT_RECORD = 5004;
    public static final int EDIT_RECORD_BY_PERMISSION = 5998;
    public static final int EDIT_RECORD_PAUSE = 5005;
    public static final int EDIT_RECORD_SAVE = 5012;
    public static final int EDIT_REFRESH_BOOKMARK = 5010;
    public static final int EDIT_SAVE = 5007;
    public static final int EDIT_SHOW_TRIM_POPUP = 5009;
    public static final int EDIT_TRIM = 5006;
    public static final int EDIT_TRIM_IN_PROGRESS = 5011;
    public static final int ENABLE_MARGIN_BOTTOM_LIST = 950;
    public static final int ENTER_CATEGORY = 961;
    public static final int ENTRY_SETTINGS_VIA_SMART_TIPS = 16;
    private static final int ERROR_EVENT_LAST = 9999;
    private static final int ERROR_EVENT_START = 9000;
    public static final int EXIT_CATEGORY = 960;
    public static final int FINISH_ACTIVITY = 971;
    public static final int HIDE_BOOKMARK_LIST = 955;
    public static final int HIDE_DIALOG = 998;
    public static final int HIDE_EDIT_PROGRESS_DIALOG = 953;
    public static final int HIDE_HELP_MODE_GUIDE = 22;
    public static final int HIDE_SIP = 974;
    public static final int HIDE_SMART_TIPS = 23;
    public static final int INIT = 1;
    public static final int INVALIDATE_MENU = 968;
    private static final boolean IS_ENG_BUILD = "eng".equals(Build.TYPE);
    public static final int MAIN_MULTIWINDOW_SIZE_CHANGE = 21;
    public static final int MINIMIZE_SIP = 973;
    private static final int MINI_PLAY_EVENT_START = 3000;
    public static final int MINI_PLAY_NEXT = 3004;
    public static final int MINI_PLAY_PAUSE = 3002;
    public static final int MINI_PLAY_PREV = 3005;
    public static final int MINI_PLAY_RESUME = 3003;
    public static final int MINI_PLAY_START = 3001;
    public static final int MOUNT_SD_CARD = 966;
    public static final int NAVIGATION_BAR_CHANGE = 15;
    public static final int NAVIGATION_MODE_CHANGE = 20;
    public static final int OPEN_FULL_PLAYER = 975;
    public static final int OPEN_LIST = 3;
    public static final int OPEN_MAIN = 4;
    public static final int OPEN_TRASH = 947;
    public static final int PERMISSION_CHECK = 972;
    private static final int PLAY_EVENT_START = 2000;
    public static final int PLAY_FF = 2007;
    public static final int PLAY_NEXT = 2005;
    public static final int PLAY_PAUSE = 2002;
    public static final int PLAY_PREV = 2006;
    public static final int PLAY_RESUME = 2003;
    public static final int PLAY_RW = 2008;
    public static final int PLAY_START = 2001;
    public static final int PLAY_STOP = 2004;
    public static final int PRIVATE_OPERATION_CANCEL = 987;
    public static final int PRIVATE_OPERATION_OPTION_CHANGED = 988;
    public static final int PRIVATE_SELECT_MODE = 10;
    public static final int RECORD_BY_LEVEL_ACTIVEKEY = 1991;
    public static final int RECORD_CALL_ALLOW = 1996;
    public static final int RECORD_CALL_REJECT = 1997;
    public static final int RECORD_CANCEL = 1006;
    private static final int RECORD_EVENT_LAST = 1999;
    private static final int RECORD_EVENT_START = 1000;
    public static final int RECORD_PAUSE = 1002;
    public static final int RECORD_PLAY_PAUSE = 1008;
    public static final int RECORD_PLAY_START = 1007;
    public static final int RECORD_PRESTART = 1009;
    public static final int RECORD_PRESTART_CANCEL = 1010;
    public static final int RECORD_RELEASE_MEDIASESSION = 1989;
    public static final int RECORD_RESUME = 1003;
    public static final int RECORD_RESUME_BY_PERMISSION = 1994;
    public static final int RECORD_START = 1001;
    public static final int RECORD_START_BY_PERMISSION = 1995;
    public static final int RECORD_START_BY_SVOICE = 1993;
    public static final int RECORD_START_BY_TASK_EDGE = 1992;
    public static final int RECORD_STOP = 1004;
    public static final int RECORD_STOP_BY_DEX_CONNECT = 1990;
    public static final int RECORD_STOP_DELAYED = 1005;
    public static final int REFRESH = 2;
    public static final int REFRESH_MAIN = 18;
    public static final int REFRESH_POPUP_VIEW = 19;
    public static final int REMOVE_BOOKMARK = 978;
    public static final int REMOVE_HW_KEYBOARD = 12;
    public static final int RESTORE_COMPLETE = 942;
    public static final int SCROLL_RECYCLER_VIEW = 951;
    public static final int SEARCH_CATEGORY = 6007;
    public static final int SEARCH_DESELECT_MODE = 14;
    private static final int SEARCH_EVENT_START = 6000;
    public static final int SEARCH_HISTORY_INPUT = 6011;
    public static final int SEARCH_LIST_UPDATE = 6006;
    public static final int SEARCH_MINI_PLAY_NEXT = 6009;
    public static final int SEARCH_MINI_PLAY_PREV = 6010;
    public static final int SEARCH_PLAY_PAUSE = 6002;
    public static final int SEARCH_PLAY_RESUME = 6003;
    public static final int SEARCH_PLAY_START = 6001;
    public static final int SEARCH_PLAY_STOP = 6004;
    public static final int SEARCH_RECORDINGS = 6008;
    public static final int SEARCH_SELECT_MODE = 13;
    public static final int SEARCH_TEXT_CHANGED = 959;
    public static final int SEARCH_VOICE_INPUT = 6005;
    public static final int SELECT = 986;
    public static final int SELECT_ALL = 985;
    public static final int SELECT_MODE = 6;
    public static final int SHORTCUT_CTRLA_SELECT = 40995;
    public static final int SHORTCUT_EDIT_TRIM_DIALOG = 40998;
    private static final int SHORTCUT_EVENT_LAST = 40999;
    private static final int SHORTCUT_EVENT_START = 40000;
    public static final int SHORTCUT_MOUSE_CATEGORY_DELETE = 40996;
    public static final int SHORTCUT_MOUSE_CATEGORY_RENAME = 40997;
    public static final int SHORTCUT_SHIFT_SELECT = 40994;
    public static final int SHOW_BOOKMARK_LIST = 956;
    public static final int SHOW_BOTTOM_NAVIGATION_BAR = 952;
    public static final int SHOW_ENCODING_PROGRESS_DIALOG = 954;
    private static final int SIMPLE_EVENT_START = 50000;
    public static final int SIMPLE_MODE_CANCEL = 982;
    public static final int SIMPLE_MODE_DONE = 983;
    public static final int SIMPLE_PLAY_FF = 50014;
    public static final int SIMPLE_PLAY_NEXT = 50011;
    public static final int SIMPLE_PLAY_OPEN = 50006;
    public static final int SIMPLE_PLAY_PAUSE = 50008;
    public static final int SIMPLE_PLAY_PREV = 50012;
    public static final int SIMPLE_PLAY_RESUME = 50009;
    public static final int SIMPLE_PLAY_RW = 50013;
    public static final int SIMPLE_PLAY_START = 50007;
    public static final int SIMPLE_PLAY_STOP = 50010;
    public static final int SIMPLE_RECORD_OPEN = 50001;
    public static final int SIMPLE_RECORD_PLAY_PAUSE = 50005;
    public static final int SIMPLE_RECORD_PLAY_START = 50004;
    public static final int SIMPLE_RECORD_START = 50002;
    public static final int SIMPLE_RECORD_STOP = 50003;
    public static final int START_SEARCH = 992;
    public static final int STOP_SEARCH = 991;
    private static final String TAG = "Event";
    public static final int TOS_ACCEPTED = 958;
    public static final int TRANSLATION = 17;
    public static final int TRANSLATION_CANCEL = 7005;
    public static final int TRANSLATION_EVENT_START = 7000;
    public static final int TRANSLATION_FILE_PLAY = 7007;
    public static final int TRANSLATION_FROM_GDPR_DIALOG = 7009;
    public static final int TRANSLATION_FROM_HEADSET = 7008;
    public static final int TRANSLATION_HIDE_PROGRESS = 7011;
    public static final int TRANSLATION_NETWORK_DIALOG = 7006;
    public static final int TRANSLATION_PAUSE = 7002;
    public static final int TRANSLATION_RESUME = 7003;
    public static final int TRANSLATION_SAVE = 7004;
    public static final int TRANSLATION_SHOW_PROGRESS = 7010;
    public static final int TRANSLATION_START = 7001;
    public static final int TRASH_DESELECT = 943;
    public static final int TRASH_DESELECT_ALL = 944;
    public static final int TRASH_MINI_PLAY_PAUSE = 3007;
    public static final int TRASH_MINI_PLAY_RESUME = 3008;
    public static final int TRASH_MINI_PLAY_START = 3006;
    public static final int TRASH_SELECT = 946;
    public static final int TRASH_SELECT_ALL = 945;
    public static final int TRASH_STATUS_CHANGED = 948;
    public static final int TRASH_UPDATE_CHECKBOX = 940;
    public static final int UNBLOCK_CONTROL_BUTTONS = 969;
    public static final int UNMOUNT_SD_CARD = 965;
    public static final int UPDATE_CATEGORY_AFTER_MOVE = 957;
    public static final int UPDATE_FILE_NAME = 989;
    public static final int WAVE_LAYOUT_CHANGED = 951;
    private static SparseArray<String> mFieldNameArray = new SparseArray<>();

    public static boolean isConvertibleEvent(int i) {
        return i % 1000 < 900;
    }

    private static void makeFieldMap(boolean z) {
        if (mFieldNameArray.size() == 0) {
            Event event = new Event();
            for (Field field : Event.class.getDeclaredFields()) {
                try {
                    if (field.getType().equals(Integer.TYPE)) {
                        mFieldNameArray.put(((Integer) field.get(event)).intValue(), field.getName());
                    }
                } catch (IllegalAccessException unused) {
                    Log.m32w(TAG, "IllegalAccessException");
                }
            }
        }
        if (z) {
            printAll(mFieldNameArray);
        }
    }

    private static void printAll(SparseArray<String> sparseArray) {
        int size = sparseArray.size();
        for (int i = 0; i < size; i++) {
            int keyAt = sparseArray.keyAt(i);
            Log.m19d(TAG, String.format(Locale.US, "%04d> ", new Object[]{Integer.valueOf(keyAt)}) + sparseArray.get(keyAt));
        }
    }

    public static String getEventName(int i) {
        if (!IS_ENG_BUILD) {
            return "";
        }
        makeFieldMap(false);
        return mFieldNameArray.get(i, "");
    }
}
