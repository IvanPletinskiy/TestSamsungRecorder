package com.sec.spp.push.dlc.api;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IDlcService extends IInterface {
    int requestSend(String str, String str2, long j, String str3, String str4, String str5, String str6, String str7) throws RemoteException;

    public static abstract class Stub extends Binder implements IDlcService {
        public static IDlcService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.sec.spp.push.dlc.api.IDlcService");
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IDlcService)) {
                return new Proxy(iBinder);
            }
            return (IDlcService) queryLocalInterface;
        }

        private static class Proxy implements IDlcService {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public int requestSend(String str, String str2, long j, String str3, String str4, String str5, String str6, String str7) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.sec.spp.push.dlc.api.IDlcService");
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeLong(j);
                    obtain.writeString(str3);
                    obtain.writeString(str4);
                    obtain.writeString(str5);
                    obtain.writeString(str6);
                    obtain.writeString(str7);
                    this.mRemote.transact(1, obtain, obtain2, 0);
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
