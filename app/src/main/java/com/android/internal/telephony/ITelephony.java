package com.android.internal.telephony;

import android.os.IInterface;
import android.os.RemoteException;

public interface ITelephony extends IInterface {
    boolean endCall() throws RemoteException;
}
