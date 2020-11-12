package com.sec.svoice.api;

public class SVoiceResetException extends SVoiceException {
    public SVoiceResetException(String str) {
        super(str);
    }

    public static void create(String str, int i) throws SVoiceResetException {
        if (i == 0) {
            throw new SVoiceResetException(str);
        }
        throw new SVoiceResetException(String.format("%s: %s", new Object[]{str, SVoiceException.strerror(i)}));
    }
}
