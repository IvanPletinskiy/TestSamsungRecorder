package com.samsung.vsf.audio;

import android.os.Environment;
import com.samsung.vsf.util.SVoiceLog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class PcmDump {
    private FileOutputStream fStream;

    public void openFile(String str) {
        try {
            this.fStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), str), false);
        } catch (FileNotFoundException e) {
            SVoiceLog.error("SV_PcmDump", e.getMessage());
        }
    }

    public void closeFile() {
        FileOutputStream fileOutputStream = this.fStream;
        if (fileOutputStream != null) {
            try {
                fileOutputStream.flush();
                this.fStream.close();
            } catch (Exception e) {
                SVoiceLog.error("SV_PcmDump", e.getMessage());
            }
        }
        this.fStream = null;
    }

    public void writeData(byte[] bArr) {
        FileOutputStream fileOutputStream = this.fStream;
        if (fileOutputStream != null) {
            try {
                fileOutputStream.write(bArr);
            } catch (Exception e) {
                SVoiceLog.error("SV_PcmDump", e.getMessage());
            }
        }
    }
}
