package com.sec.svoice.api;

public class SVoiceDNSLookupException extends SVoiceException {
    public SVoiceDNSLookupException(String str) {
        super(str);
    }

    public static void create(String str, int i) throws SVoiceDNSLookupException {
        if (i == 0) {
            throw new SVoiceDNSLookupException(str);
        }
        throw new SVoiceDNSLookupException(String.format("%s: %s", new Object[]{str, SVoiceException.strerror(i)}));
    }
}
