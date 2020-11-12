package com.samsung.vsf.util;

import android.content.Context;
import android.location.LocationManager;
import android.os.Build;

import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import java.util.Locale;

public class DeviceInfo {
    /* access modifiers changed from: private */
    public static String isSMSAvailable;
    /* access modifiers changed from: private */
    public static String isVoiceCallSupported;
    private static DeviceInfo mDeviceInfo;
    private String Country = Locale.getDefault().getDisplayCountry();
    private String Firmware_ver = Build.VERSION.RELEASE;
    private String Maker = Build.MANUFACTURER;
    private String Model = Build.MODEL;
    private String OS_Language = Locale.getDefault().getDisplayLanguage();
    private int OS_Ver = Build.VERSION.SDK_INT;
    String locale = "en-US";
    private LocationManager locationManager;
    private Context mContext;
    /* access modifiers changed from: private */
    public TelephonyManager telephonyManager;
    private String unique_uuid = null;

    private DeviceInfo(Context context) {
        this.mContext = context;
        this.telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        this.telephonyManager.listen(new PhoneStateListener() {
            public void onDataConnectionStateChanged(int i) {
            }

            public void onServiceStateChanged(ServiceState serviceState) {
                String str = "False";
                String unused = DeviceInfo.isSMSAvailable = DeviceInfo.this.telephonyManager.getSimState() == 5 ? "True" : str;
                if (DeviceInfo.this.telephonyManager.getPhoneType() != 0 && DeviceInfo.this.telephonyManager.getSimState() == 5) {
                    str = "True";
                }
                String unused2 = DeviceInfo.isVoiceCallSupported = str;
            }
        }, 1);
        this.locationManager = (LocationManager) this.mContext.getSystemService("location");
    }

    public static DeviceInfo getInstance(Context context) {
        if (mDeviceInfo == null) {
            mDeviceInfo = new DeviceInfo(context);
        }
        return mDeviceInfo;
    }

    public String getModelAndVersion() {
        return Build.MODEL + "_N66";
    }

    public String getUniqueDeviceIdentifier() {
        return PLMUtils.getDeviceId(this.mContext);
    }

    public static boolean isChineseDevice() {
//      return "CN".equalsIgnoreCase(SemSystemProperties.get("ro.csc.countryiso_code"));
      return false;
    }
}
