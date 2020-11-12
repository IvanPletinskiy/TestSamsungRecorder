package com.sec.android.diagmonagent.p008sa;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* renamed from: com.sec.android.diagmonagent.sa.IDMAInterface */
public interface IDMAInterface extends IInterface {
    String checkToken() throws RemoteException;

    int sendCommon(int i, String str, String str2, String str3) throws RemoteException;

    int sendLog(int i, String str, String str2, long j, String str3) throws RemoteException;

    /* renamed from: com.sec.android.diagmonagent.sa.IDMAInterface$Stub */
    public static abstract class Stub extends Binder implements IDMAInterface {
        public static IDMAInterface asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.sec.android.diagmonagent.sa.IDMAInterface");
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IDMAInterface)) {
                return new Proxy(iBinder);
            }
            return (IDMAInterface) queryLocalInterface;
        }

        /* renamed from: com.sec.android.diagmonagent.sa.IDMAInterface$Stub$Proxy */
        private static class Proxy implements IDMAInterface {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String checkToken() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.sec.android.diagmonagent.sa.IDMAInterface");
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int sendCommon(int i, String str, String str2, String str3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.sec.android.diagmonagent.sa.IDMAInterface");
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int sendLog(int i, String str, String str2, long j, String str3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.sec.android.diagmonagent.sa.IDMAInterface");
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeLong(j);
                    obtain.writeString(str3);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}
