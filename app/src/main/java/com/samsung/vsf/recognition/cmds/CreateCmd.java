package com.samsung.vsf.recognition.cmds;

import com.samsung.vsf.recognition.Cmd;
import com.samsung.vsf.recognition.Recognizer;

public class CreateCmd extends Cmd {
    public void execute(Recognizer.CmdHandler cmdHandler) {
        cmdHandler.create(this);
    }
}
