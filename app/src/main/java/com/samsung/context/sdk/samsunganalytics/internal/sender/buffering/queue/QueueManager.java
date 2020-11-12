package com.samsung.context.sdk.samsunganalytics.internal.sender.buffering.queue;

import com.samsung.context.sdk.samsunganalytics.internal.sender.SimpleLog;
import com.samsung.context.sdk.samsunganalytics.internal.util.Debug;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueManager {
    protected LinkedBlockingQueue<SimpleLog> logQueue;

    public QueueManager(int i) {
        if (i < 25) {
            this.logQueue = new LinkedBlockingQueue<>(25);
        } else if (i > 100) {
            this.logQueue = new LinkedBlockingQueue<>(100);
        } else {
            this.logQueue = new LinkedBlockingQueue<>(i);
        }
    }

    public void insert(SimpleLog simpleLog) {
        if (!this.logQueue.offer(simpleLog)) {
            Debug.LogD("QueueManager", "queue size over. remove oldest log");
            this.logQueue.poll();
            this.logQueue.offer(simpleLog);
        }
    }

    public Queue<SimpleLog> getAll() {
        return this.logQueue;
    }
}
