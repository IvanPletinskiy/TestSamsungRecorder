package com.sec.svoice.api;

public class SVoicePDSNoDatabaseException extends SVoicePDSException {
    public SVoicePDSNoDatabaseException(String str) {
        super(str);
    }

    public static void create(String str, int i) throws SVoicePDSNoDatabaseException {
        if (i == 0) {
            throw new SVoicePDSNoDatabaseException(str);
        }
        throw new SVoicePDSNoDatabaseException(String.format("%s: %s", new Object[]{str, SVoiceException.strerror(i)}));
    }
}
