package com.sec.svoice.api;

public class SVoiceRuntimeException extends SVoiceException {
    public SVoiceRuntimeException(String str) {
        super(str);
    }

    public static void create(String str, int i) throws SVoiceRuntimeException {
        if (i == 0) {
            throw new SVoiceRuntimeException(str);
        }
        throw new SVoiceRuntimeException(String.format("%s: %s", new Object[]{str, SVoiceException.strerror(i)}));
    }
}
