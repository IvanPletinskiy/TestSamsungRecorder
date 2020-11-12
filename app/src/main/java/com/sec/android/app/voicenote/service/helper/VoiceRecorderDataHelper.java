package com.sec.android.app.voicenote.service.helper;

import com.sec.android.app.voicenote.common.util.VoiceRecorderData;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.codec.M4aConsts;
import com.sec.android.app.voicenote.service.codec.M4aInfo;
import com.sec.android.app.voicenote.service.codec.M4aSerializableAtomHelper;
import java.nio.ByteBuffer;

public class VoiceRecorderDataHelper extends M4aSerializableAtomHelper {
    private static final String TAG = "SttHelper";
    private final byte[] newVRDT = {0, 0, 0, 0, 118, 114, 100, 116};

    public VoiceRecorderDataHelper(M4aInfo m4aInfo) {
        super(m4aInfo);
    }

    public VoiceRecorderData read() {
        M4aInfo m4aInfo = this.inf;
        if (m4aInfo == null || !m4aInfo.hasCustomAtom.get(M4aConsts.VRDT).booleanValue()) {
            return null;
        }
        return (VoiceRecorderData) readAtom(this.inf.customAtomPosition.get(M4aConsts.VRDT).longValue());
    }

    public void overwrite(VoiceRecorderData voiceRecorderData) {
        long j;
        if (this.inf == null) {
            Log.m22e(TAG, "overwrite() inf is NULL");
            return;
        }
        ByteBuffer wrap = ByteBuffer.wrap(this.newVRDT);
        if (this.inf.hasCustomAtom.get(M4aConsts.VRDT).booleanValue()) {
            j = this.inf.customAtomPosition.get(M4aConsts.VRDT).longValue();
        } else {
            j = this.inf.udtaPos + 8;
        }
        if (overwriteAtom(voiceRecorderData, j, wrap)) {
            this.inf.hasCustomAtom.put(M4aConsts.VRDT, true);
            this.inf.customAtomPosition.put(M4aConsts.VRDT, Long.valueOf(j));
        }
    }
}
