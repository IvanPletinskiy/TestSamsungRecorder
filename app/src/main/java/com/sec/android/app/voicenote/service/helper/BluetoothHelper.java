package com.sec.android.app.voicenote.service.helper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.VoiceNoteService;
import java.util.List;

public class BluetoothHelper {
    private static final String TAG = "BluetoothHelper";
    private static BluetoothHelper mInstance;
    private BluetoothAdapter mBluetoothAdapter = null;
    private List<BluetoothDevice> mBluetoothDevices = null;
    /* access modifiers changed from: private */
    public BluetoothHeadset mBluetoothHeadset = null;
    private String mBluetoothName;
    private BluetoothSCOReceiver mBluetoothReceiver = null;
    /* access modifiers changed from: private */
    public int mConnectionState = 10;
    private BluetoothProfile.ServiceListener mServiceListener = new BluetoothProfile.ServiceListener() {
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            if (i == 1) {
                Log.m26i(BluetoothHelper.TAG, "onServiceConnected");
                BluetoothHeadset unused = BluetoothHelper.this.mBluetoothHeadset = (BluetoothHeadset) bluetoothProfile;
            }
        }

        public void onServiceDisconnected(int i) {
            if (i == 1) {
                Log.m26i(BluetoothHelper.TAG, "onServiceDisconnected");
                BluetoothHeadset unused = BluetoothHelper.this.mBluetoothHeadset = null;
            }
        }
    };

    private BluetoothHelper() {
    }

    public static BluetoothHelper getInstance() {
        if (mInstance == null) {
            mInstance = new BluetoothHelper();
        }
        return mInstance;
    }

    public void setApplicationContext(Context context) {
        Log.m26i(TAG, "setApplicationContext");
        if (this.mBluetoothAdapter == null) {
            this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            this.mBluetoothAdapter.getProfileProxy(context, this.mServiceListener, 1);
        }
        initBluetoothReceiver(context);
    }

    private void initBluetoothReceiver(Context context) {
        if (this.mBluetoothReceiver == null) {
            this.mBluetoothReceiver = new BluetoothSCOReceiver(context);
        }
        this.mBluetoothReceiver.registerBluetoothSCOReceiver();
    }

    public boolean startRecord() {
        Log.m26i(TAG, "startRecord");
        BluetoothHeadset bluetoothHeadset = this.mBluetoothHeadset;
        if (bluetoothHeadset == null || bluetoothHeadset.getConnectedDevices().isEmpty()) {
            Log.m22e(TAG, "startRecord : There is no BluetoothHeadset !!");
            return false;
        }
        BluetoothDevice bluetoothDevice = this.mBluetoothHeadset.getConnectedDevices().get(0);
        boolean startVoiceRecognition = this.mBluetoothHeadset.startVoiceRecognition(bluetoothDevice);
        Log.m26i(TAG, "startRecord deviceName/start successful: " + bluetoothDevice.getName() + " " + startVoiceRecognition);
        return startVoiceRecognition;
    }

    public boolean stopRecord() {
        Log.m26i(TAG, "stopRecord");
        BluetoothHeadset bluetoothHeadset = this.mBluetoothHeadset;
        if (bluetoothHeadset == null || bluetoothHeadset.getConnectedDevices().isEmpty()) {
            Log.m22e(TAG, "stopRecord : There is no BluetoothHeadset !!");
            return false;
        }
        this.mBluetoothHeadset.stopVoiceRecognition(this.mBluetoothHeadset.getConnectedDevices().get(0));
        return true;
    }

    public void onDestroy() {
        Log.m26i(TAG, "onDestroy");
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter != null) {
            bluetoothAdapter.closeProfileProxy(1, this.mBluetoothHeadset);
        }
        BluetoothSCOReceiver bluetoothSCOReceiver = this.mBluetoothReceiver;
        if (bluetoothSCOReceiver != null) {
            bluetoothSCOReceiver.unregisterBluetoothSCOReceiver();
        }
        if (this.mConnectionState == 12) {
            this.mConnectionState = 10;
        }
        this.mBluetoothReceiver = null;
        this.mBluetoothAdapter = null;
        this.mBluetoothHeadset = null;
    }

    public boolean isAudioSCOConnected() {
        BluetoothHeadset bluetoothHeadset = this.mBluetoothHeadset;
        if (bluetoothHeadset == null || bluetoothHeadset.getConnectedDevices().isEmpty()) {
            Log.m22e(TAG, "isAudioSCOConnected : There is no BluetoothHeadset !!");
            return false;
        }
        this.mBluetoothDevices = this.mBluetoothHeadset.getConnectedDevices();
        int size = this.mBluetoothDevices.size();
        boolean z = false;
        for (int i = 0; i < size; i++) {
            if (this.mBluetoothHeadset.isAudioConnected(this.mBluetoothDevices.get(i))) {
                z = true;
            }
        }
        Log.m19d(TAG, "isAudioSCOConnected connectionState: " + z);
        return z;
    }

    public String getBluetoothName(String str) {
        Log.m19d(TAG, "getBluetoothName - address " + str);
        this.mBluetoothName = str;
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter == null) {
            Log.m22e(TAG, "mBluetoothAdapter is null!!!");
            return str;
        }
        try {
            String name = bluetoothAdapter.getRemoteDevice(str).getName();
            this.mBluetoothName = name;
            return name;
        } catch (IllegalArgumentException e) {
            Log.m22e(TAG, "IllegalArgumentException " + e);
            return str;
        }
    }

    public String getBluetoothName() {
        return this.mBluetoothName;
    }

    public BluetoothDevice getCurrentBtDevice() {
        BluetoothHeadset bluetoothHeadset = this.mBluetoothHeadset;
        if (bluetoothHeadset == null || bluetoothHeadset.getConnectedDevices().isEmpty()) {
            return null;
        }
        List<BluetoothDevice> connectedDevices = this.mBluetoothHeadset.getConnectedDevices();
        int i = 0;
        BluetoothDevice bluetoothDevice = connectedDevices.get(0);
        while (true) {
            if (i >= connectedDevices.size()) {
                break;
            } else if (this.mBluetoothHeadset.isAudioConnected(connectedDevices.get(i))) {
                bluetoothDevice = connectedDevices.get(i);
                Log.m19d(TAG, "getCurrentBtDevice " + bluetoothDevice.getName());
                break;
            } else {
                i++;
            }
        }
        Log.m19d(TAG, "getCurrentBtDevice - " + bluetoothDevice.getName());
        return bluetoothDevice;
    }

    private class BluetoothSCOReceiver extends BroadcastReceiver {
        private final Context mContext;
        private boolean mIsRegistered = false;

        public BluetoothSCOReceiver(Context context) {
            this.mContext = context;
        }

        public void registerBluetoothSCOReceiver() {
            Log.m26i(BluetoothHelper.TAG, "registerBluetoothSCOReceiver - " + this.mIsRegistered);
            if (!this.mIsRegistered) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED");
                this.mContext.registerReceiver(this, intentFilter);
            }
            this.mIsRegistered = true;
        }

        public void unregisterBluetoothSCOReceiver() {
            Log.m26i(BluetoothHelper.TAG, "unregisterBluetoothSCOReceiver");
            if (this.mIsRegistered) {
                this.mContext.unregisterReceiver(this);
            }
            this.mIsRegistered = false;
        }

        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                Log.m22e(BluetoothHelper.TAG, "onReceive: - intent is null!!!");
            } else if (intent.getAction() == null) {
                Log.m22e(BluetoothHelper.TAG, "onReceive: SCO AUDIO action is null!!!");
            } else {
                int intExtra = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 10);
                int intExtra2 = intent.getIntExtra("android.bluetooth.profile.extra.PREVIOUS_STATE", 10);
                int unused = BluetoothHelper.this.mConnectionState = intExtra;
                Log.m26i(BluetoothHelper.TAG, "enter AUDIO onReceive: current/pre state -  " + intExtra + " - " + intExtra2);
                if (intExtra == 10) {
                    if (Engine.getInstance().isResumeRecordByCall()) {
                        Log.m26i(BluetoothHelper.TAG, "resume record by phone call finish incoming state!!");
                        if (Engine.getInstance().isAutoResumeRecording()) {
                            Engine.getInstance().setAutoResumeRecording(false);
                            Engine.getInstance().setResumeRecordByCall(false);
                            Toast.makeText(this.mContext, C0690R.string.recording_resume, 0).show();
                            Context context2 = this.mContext;
                            if (context2 != null) {
//                                LocalBroadcastManager.getInstance(context2).sendBroadcast(new Intent(VoiceNoteService.BACKGROUND_VOICENOTE_REC_RESUME));
                            }
                        }
                    }
                }
            }
        }
    }
}
