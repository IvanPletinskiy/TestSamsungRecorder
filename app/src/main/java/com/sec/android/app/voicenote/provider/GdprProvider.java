package com.sec.android.app.voicenote.provider;

import android.content.Context;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DeviceInfo;

public class GdprProvider {
    private static final String TAG = "GdprProvider";
    private static volatile GdprProvider mInstance;
    private Context mAppContext;
    private Boolean mGdprCountry;

    private GdprProvider() {
    }

    public static GdprProvider getInstance() {
        if (mInstance == null) {
            synchronized (GdprProvider.class) {
                mInstance = new GdprProvider();
            }
        }
        return mInstance;
    }

    public void setApplicationContext(Context context) {
        this.mAppContext = context;
    }

    public boolean isGdprCountry() {
        if (this.mGdprCountry == null) {
            String mcc = DeviceInfo.getMCC(this.mAppContext);
            if ("null".equals(mcc)) {
//                String countryIso = SemSystemProperties.getCountryIso();
//                Log.m26i(TAG, "isGdprCountry - countryISO : " + countryIso);
//                String[] stringArray = this.mAppContext.getResources().getStringArray(C0690R.array.gdpr_countries);
//                int length = stringArray.length;
//                for (int i = 0; i < length; i++) {
//                    if (stringArray[i].toLowerCase().equals(countryIso.toLowerCase())) {
//                        this.mGdprCountry = true;
//                        return this.mGdprCountry.booleanValue();
//                    }
//                }
            } else {
                Log.m26i(TAG, "isGdprCountry - mcc : " + mcc);
                String[] stringArray2 = this.mAppContext.getResources().getStringArray(C0690R.array.gdpr_mcc);
                int length2 = stringArray2.length;
                for (int i2 = 0; i2 < length2; i2++) {
                    if (stringArray2[i2].equals(mcc)) {
                        this.mGdprCountry = true;
                        return this.mGdprCountry.booleanValue();
                    }
                }
            }
            this.mGdprCountry = false;
        }
        Log.m26i(TAG, "Gdpr country: " + this.mGdprCountry);
        return this.mGdprCountry.booleanValue();
    }
}
