package com.samsung.vsf.recognition.cmds;

import com.samsung.vsf.recognition.Cmd;
import com.samsung.vsf.recognition.Recognizer;

public class CancelCmd extends Cmd {
    public CancelCmd() {
        setClearQueue();
    }

    public void execute(Recognizer.CmdHandler cmdHandler) {
        cmdHandler.cancel(this);
    }
}
