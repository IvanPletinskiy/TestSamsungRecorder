package com.sec.android.app.voicenote.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.sec.android.app.voicenote.service.IVoiceNoteServiceCallback;

public interface IVoiceNoteService extends IInterface {

    public static class Default implements IVoiceNoteService {
        public IBinder asBinder() {
            return null;
        }

        public void hideNotification() throws RemoteException {
        }

        public void registerCallback(IVoiceNoteServiceCallback iVoiceNoteServiceCallback) throws RemoteException {
        }

        public void showNotification() throws RemoteException {
        }

        public void unregisterCallback(IVoiceNoteServiceCallback iVoiceNoteServiceCallback) throws RemoteException {
        }
    }

    void hideNotification() throws RemoteException;

    void registerCallback(IVoiceNoteServiceCallback iVoiceNoteServiceCallback) throws RemoteException;

    void showNotification() throws RemoteException;

    void unregisterCallback(IVoiceNoteServiceCallback iVoiceNoteServiceCallback) throws RemoteException;

    public static abstract class Stub extends Binder implements IVoiceNoteService {
        private static final String DESCRIPTOR = "com.sec.android.app.voicenote.service.IVoiceNoteService";
        static final int TRANSACTION_hideNotification = 2;
        static final int TRANSACTION_registerCallback = 3;
        static final int TRANSACTION_showNotification = 1;
        static final int TRANSACTION_unregisterCallback = 4;

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IVoiceNoteService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IVoiceNoteService)) {
                return new Proxy(iBinder);
            }
            return (IVoiceNoteService) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1) {
                parcel.enforceInterface(DESCRIPTOR);
                showNotification();
                parcel2.writeNoException();
                return true;
            } else if (i == 2) {
                parcel.enforceInterface(DESCRIPTOR);
                hideNotification();
                parcel2.writeNoException();
                return true;
            } else if (i == 3) {
                parcel.enforceInterface(DESCRIPTOR);
                registerCallback(IVoiceNoteServiceCallback.Stub.asInterface(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            } else if (i == 4) {
                parcel.enforceInterface(DESCRIPTOR);
                unregisterCallback(IVoiceNoteServiceCallback.Stub.asInterface(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
        }

        private static class Proxy implements IVoiceNoteService {
            public static IVoiceNoteService sDefaultImpl;
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

            public void showNotification() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(1, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().showNotification();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void hideNotification() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(2, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().hideNotification();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void registerCallback(IVoiceNoteServiceCallback iVoiceNoteServiceCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iVoiceNoteServiceCallback != null ? iVoiceNoteServiceCallback.asBinder() : null);
                    if (this.mRemote.transact(3, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().registerCallback(iVoiceNoteServiceCallback);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void unregisterCallback(IVoiceNoteServiceCallback iVoiceNoteServiceCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iVoiceNoteServiceCallback != null ? iVoiceNoteServiceCallback.asBinder() : null);
                    if (this.mRemote.transact(4, obtain, obtain2, 0) || Stub.getDefaultImpl() == null) {
                        obtain2.readException();
                        obtain2.recycle();
                        obtain.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().unregisterCallback(iVoiceNoteServiceCallback);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IVoiceNoteService iVoiceNoteService) {
            if (Proxy.sDefaultImpl != null || iVoiceNoteService == null) {
                return false;
            }
            Proxy.sDefaultImpl = iVoiceNoteService;
            return true;
        }

        public static IVoiceNoteService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
