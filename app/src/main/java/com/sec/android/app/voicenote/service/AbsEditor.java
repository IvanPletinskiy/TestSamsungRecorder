package com.sec.android.app.voicenote.service;

public abstract class AbsEditor {
    public abstract boolean delete(String str, String str2, int i, int i2);

    public abstract boolean overwrite(String str, String str2, String str3, int i, int i2);

    public abstract boolean trim(String str, String str2, int i, int i2);
}
