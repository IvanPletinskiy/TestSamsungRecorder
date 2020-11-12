package com.sec.svoice.api;

public class SVoiceIncompatibilityException extends SVoiceException {
    public SVoiceIncompatibilityException(String str) {
        super(str);
    }

    public static void create(String str, int i) throws SVoiceIncompatibilityException {
        if (i == 0) {
            throw new SVoiceIncompatibilityException(str);
        }
        throw new SVoiceIncompatibilityException(String.format("%s: %s", new Object[]{str, SVoiceException.strerror(i)}));
    }
}
