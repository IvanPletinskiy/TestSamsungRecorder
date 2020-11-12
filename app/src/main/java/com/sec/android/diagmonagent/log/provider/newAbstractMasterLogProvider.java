package com.sec.android.diagmonagent.log.provider;

import android.os.Bundle;
import java.util.List;

public abstract class newAbstractMasterLogProvider extends newAbstractLogProvider {
    private void enforceAgreement() {
    }

    /* access modifiers changed from: protected */
    public abstract List<String> setAuthorityList();

    /* access modifiers changed from: protected */
    public abstract String setDeviceId();

    /* access modifiers changed from: protected */
    public abstract String setServiceName();

    /* access modifiers changed from: protected */
    public boolean setSupportPush() {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean setUploadWiFiOnly() {
        return true;
    }

    public boolean onCreate() {
        if (!super.onCreate()) {
            return false;
        }
        newAbstractLogProvider.data.putBundle("registered", makeBundle("registered", false));
        newAbstractLogProvider.data.putBundle("pushRegistered", makeBundle("pushRegistered", false));
        newAbstractLogProvider.data.putBundle("tryRegistering", makeBundle("tryRegistering", true));
        newAbstractLogProvider.data.putBundle("nonce", makeBundle("nonce", ""));
        newAbstractLogProvider.data.putBundle("authorityList", makeAuthorityListBundle(setAuthorityList()));
        newAbstractLogProvider.data.putBundle("serviceName", makeBundle("serviceName", setServiceName()));
        newAbstractLogProvider.data.putBundle("deviceId", makeBundle("deviceId", setDeviceId()));
        newAbstractLogProvider.data.putBundle("deviceInfo", setDeviceInfo());
        newAbstractLogProvider.data.putBundle("uploadWifionly", makeBundle("uploadWifionly", setUploadWiFiOnly()));
        newAbstractLogProvider.data.putBundle("supportPush", makeBundle("supportPush", setSupportPush()));
        newAbstractLogProvider.data.putBundle("logList", makeLogListBundle(setLogList()));
        newAbstractLogProvider.data.putBundle("plainLogList", makeLogListBundle(setPlainLogList()));
        return true;
    }

    public void setConfiguration(DiagMonConfig diagMonConfig) {
        newAbstractLogProvider.data.putBundle("authorityList", makeAuthorityListBundle(diagMonConfig.getOldConfig().getAuthorityList()));
        newAbstractLogProvider.data.putBundle("serviceName", makeBundle("serviceName", diagMonConfig.getOldConfig().getServiceName()));
        newAbstractLogProvider.data.putBundle("deviceId", makeBundle("deviceId", diagMonConfig.getDeviceId()));
        newAbstractLogProvider.data.putBundle("agreed", makeBundle("agreed", diagMonConfig.getAgree()));
        newAbstractLogProvider.data.putBundle("logList", makeLogListBundle(diagMonConfig.getOldConfig().getLogList()));
        newAbstractLogProvider.data.putBundle("plainLogList", makeLogListBundle(setPlainLogList()));
    }

    private Bundle makeBundle(String str, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(str, z);
        return bundle;
    }

    private Bundle makeBundle(String str, String str2) {
        Bundle bundle = new Bundle();
        bundle.putString(str, str2);
        return bundle;
    }

    private Bundle makeAuthorityListBundle(List<String> list) {
        Bundle bundle = new Bundle();
        for (String next : list) {
            bundle.putString(next, next);
        }
        return bundle;
    }

    /* access modifiers changed from: protected */
    public Bundle setDeviceInfo() {
        return newPackageInformation.instance.getDeviceInfoBundle(getContext());
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        enforceSelfOrSystem();
        if ("get".equals(str) && "registered".equals(str2)) {
            enforceAgreement();
        }
        return super.call(str, str2, bundle);
    }
}
