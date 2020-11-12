package com.sec.android.diagmonagent.log.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import com.samsung.context.sdk.samsunganalytics.BuildConfig;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public abstract class newAbstractLogProvider extends ContentProvider {
    public static Bundle data;

    /* access modifiers changed from: protected */
    public void enforceSelfOrSystem() {
    }

    /* access modifiers changed from: protected */
    public abstract String getAuthority();

    /* access modifiers changed from: protected */
    public abstract List<String> setLogList();

    /* access modifiers changed from: protected */
    public abstract List<String> setPlainLogList();

    public boolean onCreate() {
        data = new Bundle();
        data.putBundle("diagmonSupportV1VersionName", getDiagmonSupportV1VersionNameBundle());
        data.putBundle("diagmonSupportV1VersionCode", getDiagmonSupportV1VersionCodeBundle());
        return true;
    }

    private Bundle getDiagmonSupportV1VersionNameBundle() {
        Bundle bundle = new Bundle();
        try {
            Object obj = BuildConfig.class.getDeclaredField("VERSION_NAME").get((Object) null);
            if (obj instanceof String) {
                bundle.putString("diagmonSupportV1VersionName", String.class.cast(obj));
            }
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException unused) {
        }
        return bundle;
    }

    private Bundle getDiagmonSupportV1VersionCodeBundle() {
        Bundle bundle = new Bundle();
        try {
            bundle.putInt("diagmonSupportV1VersionCode", BuildConfig.class.getDeclaredField("VERSION_CODE").getInt((Object) null));
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException unused) {
        }
        return bundle;
    }

    /* access modifiers changed from: protected */
    public Bundle makeLogListBundle(List<String> list) {
        Bundle bundle = new Bundle();
        for (String next : list) {
            try {
                next = new File(next).getCanonicalPath();
            } catch (IOException unused) {
            }
            bundle.putParcelable(next, new Uri.Builder().scheme("content").authority(getAuthority()).path(next).build());
        }
        return bundle;
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        enforceSelfOrSystem();
        if ("clear".equals(str)) {
            return clear();
        }
        if ("set".equals(str)) {
            return set(str2, bundle);
        }
        if ("get".equals(str) && !contains(str2) && data.getBundle(str2) != null) {
            return data.getBundle(str2);
        }
        if ("get".equals(str)) {
            return get(str2);
        }
        return super.call(str, str2, bundle);
    }

    public ParcelFileDescriptor openFile(Uri uri, String str) throws FileNotFoundException {
        enforceSelfOrSystem();
        String path = uri.getPath();
        if (data.getBundle("logList") == null || data.getBundle("plainLogList") == null) {
            throw new RuntimeException("Data is corrupted");
        } else if (data.getBundle("logList").containsKey(path) || data.getBundle("plainLogList").containsKey(path)) {
            return openParcelFileDescriptor(path);
        } else {
            throw new FileNotFoundException();
        }
    }

    /* access modifiers changed from: protected */
    public ParcelFileDescriptor openParcelFileDescriptor(String str) throws FileNotFoundException {
        return ParcelFileDescriptor.open(new File(str), 268435456);
    }

    public int delete(Uri uri, String str, String[] strArr) {
        throw new RuntimeException("Operation not supported");
    }

    public String getType(Uri uri) {
        enforceSelfOrSystem();
        return "text/plain";
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        throw new RuntimeException("Operation not supported");
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        throw new RuntimeException("Operation not supported");
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        throw new RuntimeException("Operation not supported");
    }

    /* access modifiers changed from: protected */
    public SharedPreferences getDiagMonSharedPreferences() {
        return getContext().getSharedPreferences("diagmon_preferences", 0);
    }

    /* access modifiers changed from: protected */
    public Bundle clear() {
        SharedPreferences.Editor edit = getDiagMonSharedPreferences().edit();
        edit.clear();
        edit.apply();
        return Bundle.EMPTY;
    }

    /* access modifiers changed from: protected */
    public Bundle set(String str, Bundle bundle) {
        SharedPreferences.Editor edit = getDiagMonSharedPreferences().edit();
        Object obj = bundle.get(str);
        if (obj instanceof Boolean) {
            edit.putBoolean(str, ((Boolean) obj).booleanValue());
        }
        if (obj instanceof Float) {
            edit.putFloat(str, ((Float) obj).floatValue());
        }
        if (obj instanceof Integer) {
            edit.putInt(str, ((Integer) obj).intValue());
        }
        if (obj instanceof Long) {
            edit.putLong(str, ((Long) obj).longValue());
        }
        if (obj instanceof String) {
            edit.putString(str, (String) obj);
        }
        edit.apply();
        return Bundle.EMPTY;
    }

    /* access modifiers changed from: protected */
    public boolean contains(String str) {
        return getDiagMonSharedPreferences().contains(str);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0019 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.os.Bundle get(java.lang.String r5) {
        /*
            r4 = this;
            android.content.SharedPreferences r0 = r4.getDiagMonSharedPreferences()
            android.os.Bundle r1 = new android.os.Bundle
            r1.<init>()
            r2 = 0
            boolean r3 = r0.getBoolean(r5, r2)     // Catch:{ ClassCastException -> 0x0011 }
            r1.putBoolean(r5, r3)     // Catch:{ ClassCastException -> 0x0011 }
        L_0x0011:
            r3 = 0
            float r3 = r0.getFloat(r5, r3)     // Catch:{ ClassCastException -> 0x0019 }
            r1.putFloat(r5, r3)     // Catch:{ ClassCastException -> 0x0019 }
        L_0x0019:
            int r2 = r0.getInt(r5, r2)     // Catch:{ ClassCastException -> 0x0020 }
            r1.putInt(r5, r2)     // Catch:{ ClassCastException -> 0x0020 }
        L_0x0020:
            r2 = 0
            long r2 = r0.getLong(r5, r2)     // Catch:{ ClassCastException -> 0x0029 }
            r1.putLong(r5, r2)     // Catch:{ ClassCastException -> 0x0029 }
        L_0x0029:
            r2 = 0
            java.lang.String r0 = r0.getString(r5, r2)     // Catch:{ ClassCastException -> 0x0031 }
            r1.putString(r5, r0)     // Catch:{ ClassCastException -> 0x0031 }
        L_0x0031:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.diagmonagent.log.provider.newAbstractLogProvider.get(java.lang.String):android.os.Bundle");
    }
}
