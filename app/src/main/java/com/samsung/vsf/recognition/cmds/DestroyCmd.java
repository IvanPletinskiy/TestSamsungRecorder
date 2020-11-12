package com.samsung.vsf.recognition.cmds;

import com.samsung.vsf.recognition.Cmd;
import com.samsung.vsf.recognition.Recognizer;

public class DestroyCmd extends Cmd {
    public DestroyCmd() {
        setClearQueue();
    }

    public void execute(Recognizer.CmdHandler cmdHandler) {
        cmdHandler.destroy(this);
    }
}
