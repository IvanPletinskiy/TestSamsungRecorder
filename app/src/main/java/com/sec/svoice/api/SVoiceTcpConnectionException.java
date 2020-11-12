package com.sec.svoice.api;

public class SVoiceTcpConnectionException extends SVoiceException {
    public SVoiceTcpConnectionException(String str) {
        super(str);
    }

    public static void create(String str, int i) throws SVoiceTcpConnectionException {
        if (i == 0) {
            throw new SVoiceTcpConnectionException(str);
        }
        throw new SVoiceTcpConnectionException(String.format("%s: %s", new Object[]{str, SVoiceException.strerror(i)}));
    }
}
