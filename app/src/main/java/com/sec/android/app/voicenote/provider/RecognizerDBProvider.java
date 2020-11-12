package com.sec.android.app.voicenote.provider;

public class RecognizerDBProvider {
    private static final String LAST_TOS_QUERY_TIME_PATH = "tos_accepted_time";
    private static final String TAG = "RecognizerDBProvider";
    private static final String TOS_PATH = "tos_accepted";

    public static class TOSReturnType {
        public static final int ACCEPTED = 1;
        public static final int REFUSED = 0;
        public static final int UNKNOWN = -1;
    }

    public static int getTOSAcceptedState() {
        int intSettings = Settings.getIntSettings(TOS_PATH, 0);
        Log.m26i(TAG, "getTOSAcceptedState - tosAccepted : " + intSettings);
        return intSettings;
    }

    public static void setTOSAccepted(int i) {
        Log.m26i(TAG, "setTOSAccepted - value : " + i);
        Settings.setSettings(TOS_PATH, i);
    }

    public static void setLastTOSQueryTime(long j) {
        Log.m26i(TAG, "setLastTOSQueryTime - time : " + j);
        Settings.setSettings(LAST_TOS_QUERY_TIME_PATH, j);
    }
}
