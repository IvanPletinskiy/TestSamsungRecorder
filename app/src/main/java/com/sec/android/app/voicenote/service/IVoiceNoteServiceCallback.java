package com.sec.android.app.voicenote.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IVoiceNoteServiceCallback extends IInterface {

    public static class Default implements IVoiceNoteServiceCallback {
        public IBinder asBinder() {
            return null;
        }

        public void messageCallback(int i, int i2) throws RemoteException {
        }
    }

    void messageCallback(int i, int i2) throws RemoteException;

    public static abstract class Stub extends Binder implements IVoiceNoteServiceCallback {
        private static final String DESCRIPTOR = "com.sec.android.app.voicenote.service.IVoiceNoteServiceCallback";
        static final int TRANSACTION_messageCallback = 1;

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IVoiceNoteServiceCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IVoiceNoteServiceCallback)) {
                return new Proxy(iBinder);
            }
            return (IVoiceNoteServiceCallback) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1) {
                parcel.enforceInterface(DESCRIPTOR);
                messageCallback(parcel.readInt(), parcel.readInt());
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IVoiceNoteServiceCallback {
            public static IVoiceNoteServiceCallback sDefaultImpl;
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void messageCallback(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    if (this.mRemote.transact(1, obtain, (Parcel) null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().messageCallback(i, i2);
                    }
                } finally {
                    obtain.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IVoiceNoteServiceCallback iVoiceNoteServiceCallback) {
            if (Proxy.sDefaultImpl != null || iVoiceNoteServiceCallback == null) {
                return false;
            }
            Proxy.sDefaultImpl = iVoiceNoteServiceCallback;
            return true;
        }

        public static IVoiceNoteServiceCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
