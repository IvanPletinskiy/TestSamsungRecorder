package com.samsung.context.sdk.samsunganalytics.internal.sender.DLS;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.samsung.context.sdk.samsunganalytics.Configuration;
import com.samsung.context.sdk.samsunganalytics.internal.executor.AsyncTaskCallback;
import com.samsung.context.sdk.samsunganalytics.internal.policy.PolicyUtils;
import com.samsung.context.sdk.samsunganalytics.internal.sender.BaseLogSender;
import com.samsung.context.sdk.samsunganalytics.internal.sender.LogType;
import com.samsung.context.sdk.samsunganalytics.internal.sender.SimpleLog;
import com.samsung.context.sdk.samsunganalytics.internal.util.Debug;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class DLSLogSender extends BaseLogSender {
    public DLSLogSender(Context context, Configuration configuration) {
        super(context, configuration);
    }

    private int getNetworkType() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) this.context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            return -4;
        }
        return activeNetworkInfo.getType();
    }

    private int checkAvailableLogging(int i) {
        if (i == -4) {
            Debug.LogD("DLS Sender", "Network unavailable.");
            return -4;
        } else if (PolicyUtils.isPolicyExpired(this.context)) {
            Debug.LogD("DLS Sender", "policy expired. request policy");
            return -6;
        } else if (this.configuration.getRestrictedNetworkType() != i) {
            return 0;
        } else {
            Debug.LogD("DLS Sender", "Network unavailable by restrict option:" + i);
            return -4;
        }
    }

    private void sendSum(int i, LogType logType, Queue<SimpleLog> queue, int i2, AsyncTaskCallback asyncTaskCallback) {
        PolicyUtils.useQuota(this.context, i, i2);
        this.executor.execute(new DLSAPIClient(logType, queue, this.configuration.getTrackingId(), this.configuration.getNetworkTimeoutInMilliSeconds(), asyncTaskCallback));
    }

    private int flushBufferedLogs(int i, LogType logType, Queue<SimpleLog> queue, AsyncTaskCallback asyncTaskCallback) {
        ArrayList arrayList = new ArrayList();
        Iterator it = queue.iterator();
        while (true) {
            int i2 = 0;
            if (!it.hasNext()) {
                return 0;
            }
            LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue();
            int remainingQuota = PolicyUtils.getRemainingQuota(this.context, i);
            if (51200 <= remainingQuota) {
                remainingQuota = 51200;
            }
            while (it.hasNext()) {
                SimpleLog simpleLog = (SimpleLog) it.next();
                if (simpleLog.getType() == logType) {
                    if (simpleLog.getData().getBytes().length + i2 > remainingQuota) {
                        break;
                    }
                    i2 += simpleLog.getData().getBytes().length;
                    linkedBlockingQueue.add(simpleLog);
                    it.remove();
                    arrayList.add(simpleLog.getId());
                    if (queue.isEmpty()) {
                        this.manager.remove(arrayList);
                        queue = this.manager.get(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
                        it = queue.iterator();
                    }
                }
            }
            if (linkedBlockingQueue.isEmpty()) {
                return -1;
            }
            this.manager.remove(arrayList);
            sendSum(i, logType, linkedBlockingQueue, i2, asyncTaskCallback);
            Debug.LogD("DLSLogSender", "send packet : num(" + linkedBlockingQueue.size() + ") size(" + i2 + ")");
        }
    }

    private int sendOne(int i, SimpleLog simpleLog, AsyncTaskCallback asyncTaskCallback, boolean z) {
        if (simpleLog == null) {
            return -100;
        }
        int length = simpleLog.getData().getBytes().length;
        int hasQuota = PolicyUtils.hasQuota(this.context, i, length);
        if (hasQuota != 0) {
            return hasQuota;
        }
        PolicyUtils.useQuota(this.context, i, length);
        DLSAPIClient dLSAPIClient = new DLSAPIClient(simpleLog, this.configuration.getTrackingId(), this.configuration.getNetworkTimeoutInMilliSeconds(), asyncTaskCallback);
        if (z) {
            Debug.LogENG("sync send");
            dLSAPIClient.run();
            return dLSAPIClient.onFinish();
        }
        this.executor.execute(dLSAPIClient);
        return 0;
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x006a A[LOOP:0: B:12:0x006a->B:15:0x007a, LOOP_START, PHI: r2 
      PHI: (r2v3 int) = (r2v1 int), (r2v6 int) binds: [B:10:0x005d, B:15:0x007a] A[DONT_GENERATE, DONT_INLINE]] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int send(java.util.Map<java.lang.String, java.lang.String> r7) {
        /*
            r6 = this;
            int r0 = r6.getNetworkType()
            int r1 = r6.checkAvailableLogging(r0)
            if (r1 == 0) goto L_0x0021
            r6.insert(r7)
            r7 = -6
            if (r1 != r7) goto L_0x0020
            android.content.Context r7 = r6.context
            com.samsung.context.sdk.samsunganalytics.Configuration r0 = r6.configuration
            com.samsung.context.sdk.samsunganalytics.internal.executor.Executor r2 = r6.executor
            com.samsung.context.sdk.samsunganalytics.internal.device.DeviceInfo r3 = r6.deviceInfo
            com.samsung.context.sdk.samsunganalytics.internal.policy.PolicyUtils.getPolicy(r7, r0, r2, r3)
            com.samsung.context.sdk.samsunganalytics.internal.sender.buffering.Manager r7 = r6.manager
            r7.delete()
        L_0x0020:
            return r1
        L_0x0021:
            com.samsung.context.sdk.samsunganalytics.internal.sender.DLS.DLSLogSender$1 r1 = new com.samsung.context.sdk.samsunganalytics.internal.sender.DLS.DLSLogSender$1
            r1.<init>(r0)
            com.samsung.context.sdk.samsunganalytics.internal.sender.SimpleLog r2 = new com.samsung.context.sdk.samsunganalytics.internal.sender.SimpleLog
            java.lang.String r3 = "ts"
            java.lang.Object r3 = r7.get(r3)
            java.lang.String r3 = (java.lang.String) r3
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            long r3 = r3.longValue()
            r6.setCommonParamToLog(r7)
            java.lang.String r5 = r6.makeBodyString(r7)
            com.samsung.context.sdk.samsunganalytics.internal.sender.LogType r7 = r6.getLogType(r7)
            r2.<init>(r3, r5, r7)
            r7 = 0
            int r2 = r6.sendOne(r0, r2, r1, r7)
            r3 = -1
            if (r2 != r3) goto L_0x004f
            return r2
        L_0x004f:
            com.samsung.context.sdk.samsunganalytics.internal.sender.buffering.Manager r4 = r6.manager
            r5 = 200(0xc8, float:2.8E-43)
            java.util.Queue r4 = r4.get(r5)
            com.samsung.context.sdk.samsunganalytics.internal.sender.buffering.Manager r5 = r6.manager
            boolean r5 = r5.isEnabledDatabaseBuffering()
            if (r5 == 0) goto L_0x006a
            com.samsung.context.sdk.samsunganalytics.internal.sender.LogType r7 = com.samsung.context.sdk.samsunganalytics.internal.sender.LogType.UIX
            r6.flushBufferedLogs(r0, r7, r4, r1)
            com.samsung.context.sdk.samsunganalytics.internal.sender.LogType r7 = com.samsung.context.sdk.samsunganalytics.internal.sender.LogType.DEVICE
            r6.flushBufferedLogs(r0, r7, r4, r1)
            goto L_0x007c
        L_0x006a:
            boolean r5 = r4.isEmpty()
            if (r5 != 0) goto L_0x007c
            java.lang.Object r2 = r4.poll()
            com.samsung.context.sdk.samsunganalytics.internal.sender.SimpleLog r2 = (com.samsung.context.sdk.samsunganalytics.internal.sender.SimpleLog) r2
            int r2 = r6.sendOne(r0, r2, r1, r7)
            if (r2 != r3) goto L_0x006a
        L_0x007c:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.samsung.context.sdk.samsunganalytics.internal.sender.DLS.DLSLogSender.send(java.util.Map):int");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0033, code lost:
        if (r1 != 0) goto L_0x0035;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int sendSync(java.util.Map<java.lang.String, java.lang.String> r7) {
        /*
            r6 = this;
            int r0 = r6.getNetworkType()
            int r1 = r6.checkAvailableLogging(r0)
            r2 = 0
            if (r1 == 0) goto L_0x0036
            r3 = -6
            if (r1 != r3) goto L_0x0035
            android.content.Context r1 = r6.context
            com.samsung.context.sdk.samsunganalytics.Configuration r3 = r6.configuration
            com.samsung.context.sdk.samsunganalytics.internal.device.DeviceInfo r4 = r6.deviceInfo
            com.samsung.context.sdk.samsunganalytics.internal.policy.GetPolicyClient r1 = com.samsung.context.sdk.samsunganalytics.internal.policy.PolicyUtils.makeGetPolicyClient(r1, r3, r4, r2)
            r1.run()
            int r1 = r1.onFinish()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "get policy sync "
            r3.append(r4)
            r3.append(r1)
            java.lang.String r3 = r3.toString()
            com.samsung.context.sdk.samsunganalytics.internal.util.Debug.LogENG(r3)
            if (r1 == 0) goto L_0x0036
        L_0x0035:
            return r1
        L_0x0036:
            com.samsung.context.sdk.samsunganalytics.internal.sender.SimpleLog r1 = new com.samsung.context.sdk.samsunganalytics.internal.sender.SimpleLog
            java.lang.String r3 = "ts"
            java.lang.Object r3 = r7.get(r3)
            java.lang.String r3 = (java.lang.String) r3
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            long r3 = r3.longValue()
            r6.setCommonParamToLog(r7)
            java.lang.String r5 = r6.makeBodyString(r7)
            com.samsung.context.sdk.samsunganalytics.internal.sender.LogType r7 = r6.getLogType(r7)
            r1.<init>(r3, r5, r7)
            r7 = 1
            int r7 = r6.sendOne(r0, r1, r2, r7)
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.samsung.context.sdk.samsunganalytics.internal.sender.DLS.DLSLogSender.sendSync(java.util.Map):int");
    }
}
