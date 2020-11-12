package com.sec.svoice.api;

public class SVoiceException extends Exception {
    private static String[] errMessages = {"", "uncategorized error", "connection is not established", "Return Value is empty", "session already exist", "session creation failed", "illegal operation for the state", "SID is missing", "given SID may be expired", "ASR preparation failed", "NLU preparation failed", "PDSS preparation failed", "PDSS IncorrectClientParamsError", "PDSS DB Network Error", "PDSS No Posted data", "PDSS Internal Error", "Invalid Params", "PDSS DB NOT EXIST", "Need Upgrade", "Unable to establish network connection", "DNS lookup of network address failed", "Remote call timeout exceeded", "Backend server is not available", "Force Sync Limit exceeded", "Sync server is not available", "Compress/Decompress of Params failed", "Authentication Failed", "ASR timeout Error", "ASR Recording Timeout Error", "NLU Timeout Error", ""};

    public static String strerror(int i) {
        if (i >= 0) {
            String[] strArr = errMessages;
            if (i < strArr.length - 1) {
                return strArr[i];
            }
        }
        return errMessages[1];
    }

    public SVoiceException(String str) {
        super(str);
    }

    public static void create(String str, int i) throws SVoiceException {
        if (i == 0) {
            throw new SVoiceException(str);
        }
        throw new SVoiceException(String.format("%s: %s", new Object[]{str, strerror(i)}));
    }
}
