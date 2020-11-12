package com.sec.svoice.api;

public class SVoicePDSResetException extends SVoicePDSException {
    public SVoicePDSResetException(String str) {
        super(str);
    }

    public static void create(String str, int i) throws SVoicePDSResetException {
        if (i == 0) {
            throw new SVoicePDSResetException(str);
        }
        throw new SVoicePDSResetException(String.format("%s: %s", new Object[]{str, SVoiceException.strerror(i)}));
    }
}
