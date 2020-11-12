package com.sec.android.diagmonagent.log.provider;

import android.content.Context;
import com.samsung.context.sdk.samsunganalytics.internal.util.Debug;
import com.sec.android.diagmonagent.log.provider.DiagMonSDK;
import com.sec.android.diagmonagent.log.provider.utils.DiagMonUtil;
import java.io.File;
import java.io.IOException;
import java.lang.Thread;
import java.util.ArrayList;

public class DiagMonLogger implements Thread.UncaughtExceptionHandler {
    private static DiagMonConfig diagmonConfig;
    private static EventBuilder eventBuilder;
    private final String DIRECTORY;
    private String agree;
    private Context application;
    private Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;
    private boolean networkMode = true;

    public DiagMonLogger(Context context, Thread.UncaughtExceptionHandler uncaughtExceptionHandler, DiagMonConfig diagMonConfig, boolean z, String str) {
        this.application = context;
        this.DIRECTORY = context.getApplicationInfo().dataDir + "/exception/";
        this.defaultUncaughtExceptionHandler = uncaughtExceptionHandler;
        this.agree = str;
        this.networkMode = z;
        diagmonConfig = diagMonConfig;
        setConfiguration();
    }

    private void setConfiguration() {
        if (DiagMonSDK.isEnableDefaultConfiguration()) {
            DiagMonSDK.DiagMonHelper.setConfiguration(diagmonConfig);
        }
        if (DiagMonUtil.checkDMA(this.application) == 1) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.DIRECTORY + "/" + "diagmon.log");
            EventBuilder eventBuilder2 = new EventBuilder(this.application);
            eventBuilder2.setNetworkMode(this.networkMode);
            eventBuilder2.setErrorCode("fatal exception");
            eventBuilder = eventBuilder2;
        } else if (DiagMonUtil.checkDMA(this.application) == 2) {
            EventBuilder eventBuilder3 = new EventBuilder(this.application);
            eventBuilder3.setLogPath(this.DIRECTORY);
            eventBuilder3.setNetworkMode(this.networkMode);
            eventBuilder3.setErrorCode("fatal exception");
            eventBuilder = eventBuilder3;
        }
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x0067 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void uncaughtException(java.lang.Thread r4, java.lang.Throwable r5) {
        /*
            r3 = this;
            com.sec.android.diagmonagent.log.provider.DiagMonConfig r0 = diagmonConfig
            boolean r0 = r0.getAgree()
            if (r0 == 0) goto L_0x0070
            java.lang.String r0 = com.sec.android.diagmonagent.log.provider.utils.DiagMonUtil.TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Agreement for ueHandler : "
            r1.append(r2)
            com.sec.android.diagmonagent.log.provider.DiagMonConfig r2 = diagmonConfig
            boolean r2 = r2.getAgree()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r0, r1)
            java.lang.String r0 = com.sec.android.diagmonagent.log.provider.utils.DiagMonUtil.TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Agreement for ueHandler : "
            r1.append(r2)
            com.sec.android.diagmonagent.log.provider.DiagMonConfig r2 = diagmonConfig
            java.lang.String r2 = r2.getAgreeAsString()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r0, r1)
            java.lang.String r0 = r3.DIRECTORY
            java.lang.String r1 = "diagmon.log"
            java.io.File r0 = r3.makeFile(r0, r1)
            r3.write(r0, r5)
            android.content.Context r0 = r3.application
            int r0 = com.sec.android.diagmonagent.log.provider.utils.DiagMonUtil.checkDMA(r0)
            r1 = 1
            if (r0 != r1) goto L_0x005b
            com.sec.android.diagmonagent.log.provider.EventBuilder r0 = eventBuilder
            java.lang.String r1 = r3.DIRECTORY
            r0.setLogPath(r1)
        L_0x005b:
            r3.eventReport()
            monitor-enter(r3)
            r0 = 3000(0xbb8, double:1.482E-320)
            r3.wait(r0)     // Catch:{ Exception -> 0x0067 }
            goto L_0x0067
        L_0x0065:
            r4 = move-exception
            goto L_0x006e
        L_0x0067:
            monitor-exit(r3)     // Catch:{ all -> 0x0065 }
            java.lang.Thread$UncaughtExceptionHandler r0 = r3.defaultUncaughtExceptionHandler
            r0.uncaughtException(r4, r5)
            return
        L_0x006e:
            monitor-exit(r3)     // Catch:{ all -> 0x0065 }
            throw r4
        L_0x0070:
            java.lang.String r4 = com.sec.android.diagmonagent.log.provider.utils.DiagMonUtil.TAG
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r0 = "not agreed : "
            r5.append(r0)
            com.sec.android.diagmonagent.log.provider.DiagMonConfig r0 = diagmonConfig
            java.lang.String r0 = r0.getAgreeAsString()
            r5.append(r0)
            java.lang.String r5 = r5.toString()
            android.util.Log.d(r4, r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.diagmonagent.log.provider.DiagMonLogger.uncaughtException(java.lang.Thread, java.lang.Throwable):void");
    }

    private void eventReport() {
        DiagMonSDK.DiagMonHelper.eventReport(this.application.getApplicationContext(), diagmonConfig, eventBuilder);
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0021 A[SYNTHETIC, Splitter:B:17:0x0021] */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0027 A[SYNTHETIC, Splitter:B:21:0x0027] */
    /* JADX WARNING: Removed duplicated region for block: B:27:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void write(java.io.File r4, java.lang.Throwable r5) {
        /*
            r3 = this;
            r0 = 0
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch:{ FileNotFoundException -> 0x001a }
            r2 = 0
            r1.<init>(r4, r2)     // Catch:{ FileNotFoundException -> 0x001a }
            java.io.PrintStream r4 = new java.io.PrintStream     // Catch:{ FileNotFoundException -> 0x0016, all -> 0x0013 }
            r4.<init>(r1)     // Catch:{ FileNotFoundException -> 0x0016, all -> 0x0013 }
            r5.printStackTrace(r4)     // Catch:{ FileNotFoundException -> 0x0016, all -> 0x0013 }
            r1.close()     // Catch:{ IOException -> 0x0024 }
            goto L_0x0024
        L_0x0013:
            r4 = move-exception
            r0 = r1
            goto L_0x0025
        L_0x0016:
            r0 = r1
            goto L_0x001a
        L_0x0018:
            r4 = move-exception
            goto L_0x0025
        L_0x001a:
            java.lang.String r4 = "Failed to write."
            com.samsung.context.sdk.samsunganalytics.internal.util.Debug.LogENG(r4)     // Catch:{ all -> 0x0018 }
            if (r0 == 0) goto L_0x0024
            r0.close()     // Catch:{ IOException -> 0x0024 }
        L_0x0024:
            return
        L_0x0025:
            if (r0 == 0) goto L_0x002a
            r0.close()     // Catch:{ IOException -> 0x002a }
        L_0x002a:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.diagmonagent.log.provider.DiagMonLogger.write(java.io.File, java.lang.Throwable):void");
    }

    private File makeDir(String str) {
        File file = new File(str);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    private File makeFile(String str, String str2) {
        if (!makeDir(str).isDirectory()) {
            return null;
        }
        File file = new File(str + "/" + str2);
        try {
            file.createNewFile();
            return file;
        } catch (IOException e) {
            Debug.LogENG(e.getLocalizedMessage());
            return file;
        }
    }
}
