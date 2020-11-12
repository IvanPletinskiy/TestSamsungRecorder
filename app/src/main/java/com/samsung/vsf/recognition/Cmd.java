package com.samsung.vsf.recognition;

import com.samsung.vsf.recognition.Recognizer;

public abstract class Cmd {
    protected boolean shouldClear = false;

    public abstract void execute(Recognizer.CmdHandler cmdHandler);

    /* access modifiers changed from: protected */
    public void setClearQueue() {
        this.shouldClear = true;
    }

    public boolean shouldClearQueue() {
        return this.shouldClear;
    }
}
