package com.sec.android.diagmonagent.log.provider;

import android.content.Context;
import android.util.Log;

import com.sec.android.diagmonagent.log.provider.utils.DiagMonUtil;
import com.sec.android.diagmonagent.log.provider.utils.Validator;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EventBuilder {
    private Context mContext;
    private String mDescription = "";
    private String mErrorCode = "";
    private JSONObject mExtData;
    public boolean mIsCalledNetworkMode;
    private String mLogPath = "";
    private boolean mNetworkMode;
    private String mRelayClientType = "";
    private String mRelayClientVer = "";
    private String mServiceDefinedKey = "";
    private String mZipFile = "";
    private oldEventBuilder oldIb;

    public EventBuilder(Context context) {
        this.mContext = context;
        this.mNetworkMode = true;
        this.mIsCalledNetworkMode = false;
        if (DiagMonUtil.checkDMA(this.mContext) == 1) {
            this.oldIb = new oldEventBuilder(context);
        }
    }

    public String getZipPath() {
        return this.mZipFile;
    }

    public void setZipFilePath(String str) {
        this.mZipFile = str;
    }

    public String getLogPath() {
        return this.mLogPath;
    }

    public EventBuilder setLogPath(String str) {
        try {
            if (isConfigured()) {
                return this;
            }
            this.mLogPath = str;
            if (DiagMonUtil.checkDMA(this.mContext) == 1 && this.mLogPath != null) {
                if (!Validator.isValidLogPath(this.mLogPath)) {
                    DiagMonSDK.getConfiguration().getOldConfig().setLogList(makeLogList(this.mLogPath));
                    DiagMonSDK.getElp().setConfiguration(DiagMonSDK.getConfiguration());
                }
            }
            return this;
        } catch (Exception unused) {
        }
        return this;
    }

    public String getServiceDefinedKey() {
        if (DiagMonUtil.checkDMA(this.mContext) == 1) {
            return this.oldIb.getEventId();
        }
        return this.mServiceDefinedKey;
    }

    public EventBuilder setErrorCode(String str) {
        if (isConfigured()) {
            return this;
        }
        this.mErrorCode = str;
        if (DiagMonUtil.checkDMA(this.mContext) == 1) {
            this.oldIb.setResultCode(str);
        }
        return this;
    }

    public String getErrorCode() {
        if (DiagMonUtil.checkDMA(this.mContext) == 1) {
            return this.oldIb.getResultCode();
        }
        return this.mErrorCode;
    }

    public EventBuilder setNetworkMode(boolean z) {
        if (isConfigured()) {
            return this;
        }
        this.mIsCalledNetworkMode = true;
        this.mNetworkMode = z;
        if (DiagMonUtil.checkDMA(this.mContext) == 1) {
            this.oldIb.setWifiOnly(z);
        }
        return this;
    }

    public boolean getNetworkMode() {
        if (DiagMonUtil.checkDMA(this.mContext) == 1) {
            return this.oldIb.getWifiOnly();
        }
        return this.mNetworkMode;
    }

    public String getDescription() {
        if (DiagMonUtil.checkDMA(this.mContext) == 1) {
            return this.oldIb.getDescription();
        }
        return this.mDescription;
    }

    public String getRelayClientVer() {
        if (DiagMonUtil.checkDMA(this.mContext) == 1) {
            return this.oldIb.getRelayClientVer();
        }
        return this.mRelayClientVer;
    }

    public String getRelayClientType() {
        if (DiagMonUtil.checkDMA(this.mContext) == 1) {
            return this.oldIb.getRelayClient();
        }
        return this.mRelayClientType;
    }

    public String getExtData() {
        JSONObject jSONObject = this.mExtData;
        if (jSONObject == null) {
            return "";
        }
        return jSONObject.toString();
    }

    public List<String> makeLogList(String str) {
        ArrayList arrayList = new ArrayList();
        for (File file : new File(str).listFiles()) {
            arrayList.add(file.getPath());
            Log.d(DiagMonUtil.TAG, "found file : " + file.getPath());
        }
        return arrayList;
    }

    class oldEventBuilder {
        private Context mContext;
        private String mDescription = "";
        private String mEventId = "";
        private String mRelayClient = "";
        private String mRelayVer = "";
        private String mResultCode = "";
        private boolean mUiMode = true;
        private boolean mWifiOnly = true;

        public oldEventBuilder(Context context) {
            this.mContext = context;
        }

        public void setResultCode(String str) {
            this.mResultCode = str;
        }

        public String getResultCode() {
            return this.mResultCode;
        }

        public void setWifiOnly(boolean z) {
            this.mWifiOnly = z;
        }

        public boolean getWifiOnly() {
            return this.mWifiOnly;
        }

        public String getEventId() {
            return this.mEventId;
        }

        public String getDescription() {
            return this.mDescription;
        }

        public String getRelayClient() {
            return this.mRelayClient;
        }

        public String getRelayClientVer() {
            return this.mRelayVer;
        }
    }

    private boolean isConfigured() {
        return DiagMonSDK.getConfiguration() == null;
    }
}
