package com.sec.android.app.voicenote.provider;

import android.content.Context;
import android.media.AudioManager;
import android.telephony.TelephonyManager;


public class PhoneStateProvider {
    private static final String TAG = "SemPhoneState";
    private static PhoneStateProvider mCall;

    private PhoneStateProvider() {
    }

    public static PhoneStateProvider getInstance() {
        if (mCall == null) {
            mCall = new PhoneStateProvider();
        }
        return mCall;
    }

    public boolean isCallIdle(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        if(telephonyManager != null && telephonyManager.getCallState() != 0) {
            Log.m26i(TAG, "isCallIdle - normal call is not idle");
            return false;
            //        } else if (!new SemVoipInterfaceManager().isVoipIdle()) {
            //            Log.m26i(TAG, "isCallIdle - voip call is not idle");
            //            return false;
            //        } else {
            //            AudioManager audioManager = (AudioManager) context.getSystemService("audio");
            //            if ((audioManager != null ? audioManager.getMode() : 0) != 0) {
            //                Log.m26i(TAG, "isCallIdle - communication call is not idle");
            //                return false;
            //            }
            //            Log.m26i(TAG, "call is idle");
            //            return true;
            //        }
        }
        return true;
     }

    public boolean isDuringCall(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        if (telephonyManager == null || telephonyManager.getCallState() != 2) {
            AudioManager audioManager = (AudioManager) context.getSystemService("audio");
//            if (audioManager == null || audioManager.getMode() != 3) {
//                return new SemVoipInterfaceManager().isVoipActivated();
//            }
            Log.m26i(TAG, "isDuringCall - communication is on");
            return true;
        }
        Log.m26i(TAG, "isDuringCall - call is on");
        return true;
    }
}
