package com.samsung.context.sdk.samsunganalytics.internal.policy;

import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;

import com.samsung.context.sdk.samsunganalytics.internal.Callback;
import com.samsung.context.sdk.samsunganalytics.internal.connection.API;
import com.samsung.context.sdk.samsunganalytics.internal.connection.Directory;
import com.samsung.context.sdk.samsunganalytics.internal.connection.Domain;
import com.samsung.context.sdk.samsunganalytics.internal.executor.AsyncTaskClient;
import com.samsung.context.sdk.samsunganalytics.internal.security.CertificateManager;
import com.samsung.context.sdk.samsunganalytics.internal.util.Debug;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class GetPolicyClient implements AsyncTaskClient {
    private API api;
    private Callback<Void, Boolean> callback;
    private HttpsURLConnection conn = null;
    private SharedPreferences pref;
    private Map<String, String> qParams;

    public GetPolicyClient(API api2, Map<String, String> map, SharedPreferences sharedPreferences, Callback<Void, Boolean> callback2) {
        this.api = api2;
        this.qParams = map;
        this.pref = sharedPreferences;
        this.callback = callback2;
    }

    public void run() {
        try {
            Uri.Builder buildUpon = Uri.parse(this.api.getUrl()).buildUpon();
            for (String next : this.qParams.keySet()) {
                buildUpon.appendQueryParameter(next, this.qParams.get(next));
            }
            this.conn = (HttpsURLConnection) new URL(buildUpon.build().toString()).openConnection();
            this.conn.setSSLSocketFactory(CertificateManager.getInstance().getSSLContext().getSocketFactory());
            this.conn.setRequestMethod(this.api.getMethod());
            this.conn.setConnectTimeout(3000);
        } catch (Exception unused) {
            Debug.LogE("Fail to get Policy");
        }
    }

    public int onFinish() {
        int i;
        String string;
        BufferedReader bufferedReader = null;
        try {
            if (this.conn.getResponseCode() != 200) {
                Debug.LogE("Fail to get Policy. Response code : " + this.conn.getResponseCode());
                i = -61;
            } else {
                i = 0;
            }
            BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(this.conn.getInputStream()));
            try {
                String readLine = bufferedReader2.readLine();
                Debug.LogENG(readLine);
                JSONObject jSONObject = new JSONObject(readLine);
                int i2 = jSONObject.getInt("rc");
                if (i2 != 1000) {
                    Debug.LogE("Fail to get Policy; Invalid Message. Result code : " + i2);
                    i = -61;
                } else {
                    Debug.LogD("GetPolicyClient", "Get Policy Success");
                    if (TextUtils.isEmpty(this.pref.getString("lgt", "")) && this.callback != null && (string = jSONObject.getString("lgt")) != null && string.equals("rtb")) {
                        this.callback.onResult(true);
                    }
                    save(jSONObject);
                }
                if (this.conn != null) {
                    this.conn.disconnect();
                }
                cleanUp(bufferedReader2);
            } catch (Exception unused) {
                bufferedReader = bufferedReader2;
                try {
                    Debug.LogE("Fail to get Policy");
                    cleanUp(bufferedReader);
                    i = -61;
                    boolean isEmpty = TextUtils.isEmpty(this.pref.getString("dom", ""));
                    this.pref.edit().putLong("policy_received_date", System.currentTimeMillis()).apply();
                    return i;
                } catch (Throwable th) {
//                    th = th;
//                    bufferedReader2 = bufferedReader;
//                    cleanUp(bufferedReader2);
//                    throw th;
                }
            } catch (Throwable th2) {
//                th = th2;
//                cleanUp(bufferedReader2);
//                throw th;
            }
        } catch (Exception unused2) {
            Debug.LogE("Fail to get Policy");
            cleanUp(bufferedReader);
            i = -61;
            boolean isEmpty2 = TextUtils.isEmpty(this.pref.getString("dom", ""));
            this.pref.edit().putLong("policy_received_date", System.currentTimeMillis()).apply();
            return i;
        }
        boolean isEmpty22 = TextUtils.isEmpty(this.pref.getString("dom", ""));
        if (i == -61 && !isEmpty22) {
            this.pref.edit().putLong("policy_received_date", System.currentTimeMillis()).apply();
        }
        return i;
    }

    private void cleanUp(BufferedReader bufferedReader) {
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException unused) {
                return;
            }
        }
        if (this.conn != null) {
            this.conn.disconnect();
        }
    }

    public void save(JSONObject jSONObject) {
        try {
            SharedPreferences.Editor putInt = this.pref.edit().putInt("oq-3g", jSONObject.getInt("oq-3g") * 1024).putInt("dq-3g", jSONObject.getInt("dq-3g") * 1024).putInt("oq-w", jSONObject.getInt("oq-w") * 1024).putInt("dq-w", jSONObject.getInt("dq-w") * 1024);
            putInt.putString("dom", "https://" + jSONObject.getString("dom")).putString("uri", jSONObject.getString("uri")).putString("bat-uri", jSONObject.getString("bat-uri")).putString("lgt", jSONObject.getString("lgt")).putInt("rint", jSONObject.getInt("rint")).putLong("policy_received_date", System.currentTimeMillis()).apply();
            Domain domain = Domain.DLS;
            domain.setDomain("https://" + jSONObject.getString("dom"));
            Directory.DLS_DIR.setDirectory(jSONObject.getString("uri"));
            Directory.DLS_DIR_BAT.setDirectory(jSONObject.getString("bat-uri"));
            Debug.LogENG("dq-3g: " + (jSONObject.getInt("dq-3g") * 1024) + ", dq-w: " + (jSONObject.getInt("dq-w") * 1024) + ", oq-3g: " + (jSONObject.getInt("oq-3g") * 1024) + ", oq-w: " + (jSONObject.getInt("oq-w") * 1024));
        } catch (JSONException e) {
            Debug.LogE("Fail to get Policy");
            Debug.LogENG("[GetPolicyClient] " + e.getMessage());
        }
    }
}
