package com.sec.svoice.api;

import java.util.Properties;

public abstract class SVoiceSentinel {
    public void notifyToClient(int i, Properties properties) {
    }

    public abstract void resultASR(int i, Properties properties);

    public void resultNLG(int i, Properties properties) {
    }

    public void resultNLG(int i, Properties properties, int i2, Properties properties2, byte[] bArr) {
    }

    public abstract void resultNLU(int i, Properties properties);

    public void resultNLU(int i, Properties properties, int i2, Properties properties2, byte[] bArr) {
    }

    public void resultPreNLU(int i, Properties properties) {
    }

    public void resultPrepare(int i, Properties properties) {
    }
}
