package com.samsung.vsf.recognition;

import java.util.Properties;

public class QueueItems implements Comparable<QueueItems> {
    Properties asrResult;
    int priority;

    QueueItems(int i, Properties properties) {
        this.priority = i;
        this.asrResult = properties;
    }

    /* access modifiers changed from: package-private */
    public int getPriority() {
        return this.priority;
    }

    /* access modifiers changed from: package-private */
    public Properties getASRResult() {
        return this.asrResult;
    }

    public int compareTo(QueueItems queueItems) {
        if (queueItems == null) {
            return 1;
        }
        return getPriority() - queueItems.getPriority();
    }
}
