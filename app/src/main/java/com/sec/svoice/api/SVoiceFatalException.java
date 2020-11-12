package com.sec.svoice.api;

public class SVoiceFatalException extends SVoiceException {
    public SVoiceFatalException(String str) {
        super(str);
    }

    public static void create(String str, int i) throws SVoiceFatalException {
        if (i == 0) {
            throw new SVoiceFatalException(str);
        }
        throw new SVoiceFatalException(String.format("%s: %s", new Object[]{str, SVoiceException.strerror(i)}));
    }
}
