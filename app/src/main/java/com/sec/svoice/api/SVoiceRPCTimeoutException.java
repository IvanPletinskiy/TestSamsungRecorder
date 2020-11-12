package com.sec.svoice.api;

public class SVoiceRPCTimeoutException extends SVoiceException {
    public SVoiceRPCTimeoutException(String str) {
        super(str);
    }

    public static void create(String str, int i) throws SVoiceRPCTimeoutException {
        if (i == 0) {
            throw new SVoiceRPCTimeoutException(str);
        }
        throw new SVoiceRPCTimeoutException(String.format("%s: %s", new Object[]{str, SVoiceException.strerror(i)}));
    }
}
