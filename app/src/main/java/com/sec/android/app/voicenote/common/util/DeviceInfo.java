package com.sec.android.app.voicenote.common.util;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.LocaleList;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.provider.Log;
import java.util.ArrayList;
import java.util.Locale;

public class DeviceInfo {
    private static final String DEFAULT_MNC = "00";
    private static final Object LOCK_DISPLAY = new Object();
    private static final Object LOCK_REAL_DISPLAY = new Object();
    private static final Object LOCK_ROTATION = new Object();
    private static final Object LOCK_SCREEN = new Object();
    public static final String STR_TRUE = "true";
    public static final String TAG = "DeviceInfo";
    private static int sDeviceHeight;
    private static int sDeviceWidth;
    private static volatile DisplayMetrics sDisplayMetrics;
    private static volatile DisplayMetrics sRealDisplayMetrics;
    private static volatile Integer sRotation;
    private static volatile Point sScreenSize;
    private static volatile Integer sStatusBarHeight;

    public static String getMCC(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        if (telephonyManager == null) {
            Log.m22e(TAG, "_getMCC::telMgr is null.");
        } else {
            String simOperator = telephonyManager.getSimOperator();
            if (simOperator == null || simOperator.length() < 3) {
                simOperator = "";
            }
            if (telephonyManager.getPhoneType() != 0) {
                if (!TextUtils.isEmpty(simOperator)) {
                    try {
                        return simOperator.substring(0, 3);
                    } catch (IndexOutOfBoundsException unused) {
                        Log.m22e(TAG, "getMCC::IndexOutOfBoundsException 2");
                    }
                }
            } else if (!TextUtils.isEmpty(simOperator)) {
                try {
                    return simOperator.substring(0, 3);
                } catch (IndexOutOfBoundsException unused2) {
                    Log.m22e(TAG, "getMCC::IndexOutOfBoundsException 1");
                }
            } else if (!STR_TRUE.equalsIgnoreCase(getDeviceInfo("ro.product.noCP"))) {
                Log.m32w(TAG, "getMCC::noCp is false");
            } else {
                String deviceInfo = getDeviceInfo("ro.virtual.value.mcc");
                if ("Unknown".equals(deviceInfo)) {
                    Log.m32w(TAG, "getMCC::virtual mcc is unknown.");
                } else {
                    String str = TAG;
                    Log.m26i(str, "getMCC::virtual mcc is " + deviceInfo);
                    return deviceInfo;
                }
            }
        }
        return "null";
    }

    private static String getDeviceInfo(String str) {
        if (str.length() == 0) {
            return "Don't Input get Property command";
        }
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            Object[] objArr = {str};
            String str2 = (String) cls.getMethod("get", new Class[]{String.class}).invoke(cls, objArr);
            if (str2.length() == 0) {
                return "Unknown";
            }
            return str2;
        } catch (Exception unused) {
            Log.m22e(TAG, "deviceInfo::Fail to read systemProperty");
            return null;
        }
    }

    public static boolean isEngBinary() {
        return "eng".equals(Build.TYPE);
    }

    private static Display getDefaultDisplay(Context context) {
        if (context != null) {
            WindowManager windowManager = (WindowManager) context.getSystemService("window");
            if (windowManager != null) {
                return windowManager.getDefaultDisplay();
            }
            throw new AssertionError("fail to get window service");
        }
        throw new IllegalArgumentException("context is null");
    }

    public static DisplayMetrics getRealDisplayMetrics(Context context) {
        DisplayMetrics displayMetrics;
        synchronized (LOCK_REAL_DISPLAY) {
            if (sRealDisplayMetrics == null) {
                sRealDisplayMetrics = new DisplayMetrics();
                getDefaultDisplay(context).getRealMetrics(sRealDisplayMetrics);
            }
            displayMetrics = sRealDisplayMetrics;
        }
        return displayMetrics;
    }

    public static void logDeviceInfo(Context context, Configuration configuration) {
        if (isEngBinary()) {
            String str = TAG;
            Log.m19d(str, "DeviceConfig = " + resourceQualifierString(context, configuration));
        }
    }

    public static String resourceQualifierString(Context context, Configuration configuration) {
        ArrayList arrayList = new ArrayList();
        loadConfigBasics(configuration, arrayList);
        loadScreenLayoutSize(configuration, arrayList);
        loadConfigDensityDpi(configuration, arrayList);
        loadConfigOrientation(configuration, arrayList);
        loadCurrentValueFolder(context, arrayList);
        loadConfigMetrics(getRealDisplayMetrics(context), arrayList);
        loadConfigNightMode(configuration, arrayList);
        loadScreenLayoutLong(configuration, arrayList);
        loadScreenLayoutRound(configuration, arrayList);
        loadConfigColor(configuration, arrayList);
        loadConfigUiMode(configuration, arrayList);
        loadConfigTouch(configuration, arrayList);
        loadConfigKeyboard(configuration, arrayList);
        loadConfigNavigation(configuration, arrayList);
        return TextUtils.join("-", arrayList);
    }

    private static void loadConfigMetrics(DisplayMetrics displayMetrics, ArrayList<String> arrayList) {
        if (displayMetrics != null) {
            int i = displayMetrics.widthPixels;
            int i2 = displayMetrics.heightPixels;
            if (i >= i2) {
                int i3 = i;
                i = i2;
                i2 = i3;
            }
            arrayList.add(i2 + "x" + i);
        }
    }

    private static void loadConfigNavigation(Configuration configuration, ArrayList<String> arrayList) {
        int i = configuration.navigationHidden;
        if (i == 1) {
            arrayList.add("navexposed");
        } else if (i == 2) {
            arrayList.add("navhidden");
        }
        int i2 = configuration.navigation;
        if (i2 == 1) {
            arrayList.add("nonav");
        } else if (i2 == 2) {
            arrayList.add("dpad");
        } else if (i2 == 3) {
            arrayList.add("trackball");
        } else if (i2 == 4) {
            arrayList.add("wheel");
        }
    }

    private static void loadConfigKeyboard(Configuration configuration, ArrayList<String> arrayList) {
        int i = configuration.keyboardHidden;
        if (i == 1) {
            arrayList.add("keysexposed");
        } else if (i == 2) {
            arrayList.add("keyshidden");
        }
        int i2 = configuration.keyboard;
        if (i2 == 1) {
            arrayList.add("nokeys");
        } else if (i2 == 2) {
            arrayList.add("qwerty");
        } else if (i2 == 3) {
            arrayList.add("12key");
        }
    }

    private static void loadConfigTouch(Configuration configuration, ArrayList<String> arrayList) {
        int i = configuration.touchscreen;
        if (i == 1) {
            arrayList.add("notouch");
        } else if (i == 3) {
            arrayList.add("finger");
        }
    }

    private static void loadConfigDensityDpi(Configuration configuration, ArrayList<String> arrayList) {
        String str = TAG;
        Log.m19d(str, "Density = " + (((float) configuration.densityDpi) / 160.0f));
        int i = configuration.densityDpi;
        if (i == 120) {
            arrayList.add("ldpi");
        } else if (i == 160) {
            arrayList.add("mdpi");
        } else if (i == 213) {
            arrayList.add("tvdpi");
        } else if (i == 240) {
            arrayList.add("hdpi");
        } else if (i == 320) {
            arrayList.add("xhdpi");
        } else if (i == 480) {
            arrayList.add("xxhdpi");
        } else if (i != 640) {
            arrayList.add("[ " + configuration.densityDpi + "dpi ]");
        } else {
            arrayList.add("xxxhdpi");
        }
    }

    private static void loadConfigNightMode(Configuration configuration, ArrayList<String> arrayList) {
        int i = configuration.uiMode & 48;
        if (i == 16) {
            arrayList.add("notnight");
        } else if (i == 32) {
            arrayList.add("night");
        }
    }

    private static void loadConfigUiMode(Configuration configuration, ArrayList<String> arrayList) {
        switch (configuration.uiMode & 15) {
            case 2:
                arrayList.add("desk");
                return;
            case 3:
                arrayList.add("car");
                return;
            case 4:
                arrayList.add("television");
                return;
            case 5:
                arrayList.add("appliance");
                return;
            case 6:
                arrayList.add("watch");
                return;
            case 7:
                arrayList.add("vrheadset");
                return;
            default:
                return;
        }
    }

    private static void loadConfigOrientation(Configuration configuration, ArrayList<String> arrayList) {
        int i = configuration.orientation;
        if (i == 1) {
            arrayList.add("port");
        } else if (i == 2) {
            arrayList.add("land");
        }
    }

    private static void loadConfigColor(Configuration configuration, ArrayList<String> arrayList) {
        int i = configuration.colorMode & 12;
        if (i == 4) {
            arrayList.add("lowdr");
        } else if (i == 8) {
            arrayList.add("highdr");
        }
        int i2 = configuration.colorMode & 3;
        if (i2 == 1) {
            arrayList.add("nowidecg");
        } else if (i2 == 2) {
            arrayList.add("widecg");
        }
    }

    private static void loadScreenLayoutRound(Configuration configuration, ArrayList<String> arrayList) {
        int i = configuration.screenLayout & 768;
        if (i == 256) {
            arrayList.add("notround");
        } else if (i == 512) {
            arrayList.add("round");
        }
    }

    private static void loadScreenLayoutLong(Configuration configuration, ArrayList<String> arrayList) {
        int i = configuration.screenLayout & 48;
        if (i == 16) {
            arrayList.add("notlong");
        } else if (i == 32) {
            arrayList.add("long");
        }
    }

    private static void loadScreenLayoutSize(Configuration configuration, ArrayList<String> arrayList) {
        int i = configuration.screenLayout & 15;
        if (i == 1) {
            arrayList.add("small");
        } else if (i == 2) {
            arrayList.add("normal");
        } else if (i == 3) {
            arrayList.add("large");
        } else if (i == 4) {
            arrayList.add("xlarge");
        }
    }

    private static void loadConfigBasics(Configuration configuration, ArrayList<String> arrayList) {
        if (configuration.mcc != 0) {
            arrayList.add("mcc" + configuration.mcc);
            if (configuration.mnc != 0) {
                arrayList.add("mnc" + configuration.mnc);
            }
        }
        if (!configuration.getLocales().isEmpty()) {
            String localesToResourceQualifier = localesToResourceQualifier(configuration.getLocales());
            if (!localesToResourceQualifier.isEmpty()) {
                arrayList.add(localesToResourceQualifier);
            }
        }
        int i = configuration.screenLayout & 192;
        if (i == 64) {
            arrayList.add("ldltr");
        } else if (i == 128) {
            arrayList.add("ldrtl");
        }
        if (configuration.smallestScreenWidthDp != 0) {
            arrayList.add("[ sw" + configuration.smallestScreenWidthDp + "dp");
        }
        if (configuration.screenWidthDp != 0) {
            arrayList.add("w" + configuration.screenWidthDp + "dp");
        }
        if (configuration.screenHeightDp != 0) {
            arrayList.add("h" + configuration.screenHeightDp + "dp ]");
        }
    }

    private static String localesToResourceQualifier(LocaleList localeList) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < localeList.size(); i++) {
            Locale locale = localeList.get(i);
            int length = locale.getLanguage().length();
            if (length != 0) {
                int length2 = locale.getScript().length();
                int length3 = locale.getCountry().length();
                int length4 = locale.getVariant().length();
                if (sb.length() != 0) {
                    sb.append(",");
                }
                if (length == 2 && length2 == 0 && ((length3 == 0 || length3 == 2) && length4 == 0)) {
                    sb.append(locale.getLanguage());
                    if (length3 == 2) {
                        sb.append("-r");
                        sb.append(locale.getCountry());
                    }
                } else {
                    sb.append("b+");
                    sb.append(locale.getLanguage());
                    if (length2 != 0) {
                        sb.append("+");
                        sb.append(locale.getScript());
                    }
                    if (length3 != 0) {
                        sb.append("+");
                        sb.append(locale.getCountry());
                    }
                    if (length4 != 0) {
                        sb.append("+");
                        sb.append(locale.getVariant());
                    }
                }
            }
        }
        return sb.toString();
    }

    private static void loadCurrentValueFolder(Context context, ArrayList<String> arrayList) {
        arrayList.add("[ " + context.getResources().getString(C0690R.string.valuefolder) + " ]");
    }
}
