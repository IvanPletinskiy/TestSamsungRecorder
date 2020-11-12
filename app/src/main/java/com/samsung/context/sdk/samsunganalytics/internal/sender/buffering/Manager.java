package com.samsung.context.sdk.samsunganalytics.internal.sender.buffering;

import android.content.Context;
import com.samsung.context.sdk.samsunganalytics.Configuration;
import com.samsung.context.sdk.samsunganalytics.DBOpenHelper;
import com.samsung.context.sdk.samsunganalytics.internal.policy.PolicyUtils;
import com.samsung.context.sdk.samsunganalytics.internal.sender.LogType;
import com.samsung.context.sdk.samsunganalytics.internal.sender.SimpleLog;
import com.samsung.context.sdk.samsunganalytics.internal.sender.buffering.database.DbManager;
import com.samsung.context.sdk.samsunganalytics.internal.sender.buffering.queue.QueueManager;
import com.samsung.context.sdk.samsunganalytics.internal.util.Debug;
import com.samsung.context.sdk.samsunganalytics.internal.util.Preferences;
import com.samsung.context.sdk.samsunganalytics.internal.util.Utils;
import java.util.List;
import java.util.Queue;

public class Manager {
    private static Manager instance;
    private DbManager dbManager;
    private QueueManager queueManager;
    private boolean useDatabase;

    private Manager(Context context, boolean z, int i) {
        if (z) {
            this.dbManager = new DbManager(context);
        }
        this.queueManager = new QueueManager(i);
        this.useDatabase = z;
    }

    private Manager(DBOpenHelper dBOpenHelper, int i) {
        this.dbManager = new DbManager(dBOpenHelper);
        this.queueManager = new QueueManager(i);
        this.useDatabase = true;
    }

    public static Manager getInstance(Context context, Configuration configuration) {
        if (instance == null) {
            synchronized (Manager.class) {
                int queueSize = configuration.getQueueSize();
                if (PolicyUtils.getSenderType() != 0) {
                    instance = new Manager(context, false, queueSize);
                } else if (Preferences.getPreferences(context).getString("lgt", "").equals("rtb")) {
                    DBOpenHelper dbOpenHelper = configuration.getDbOpenHelper();
                    if (dbOpenHelper != null) {
                        instance = new Manager(dbOpenHelper, queueSize);
                    } else {
                        instance = new Manager(context, true, queueSize);
                    }
                } else {
                    instance = new Manager(context, false, queueSize);
                }
            }
        }
        return instance;
    }

    public void enableDatabaseBuffering(Context context) {
        enableDatabaseBuffering(new DbManager(context));
    }

    public void enableDatabaseBuffering(DBOpenHelper dBOpenHelper) {
        enableDatabaseBuffering(new DbManager(dBOpenHelper));
    }

    public void enableDatabaseBuffering(DbManager dbManager2) {
        this.useDatabase = true;
        this.dbManager = dbManager2;
        mergeQueueToDb();
    }

    private void mergeQueueToDb() {
        if (!this.queueManager.getAll().isEmpty()) {
            for (SimpleLog insert : this.queueManager.getAll()) {
                this.dbManager.insert(insert);
            }
            this.queueManager.getAll().clear();
        }
    }

    public boolean isEnabledDatabaseBuffering() {
        return this.useDatabase;
    }

    public void insert(SimpleLog simpleLog) {
        if (this.useDatabase) {
            this.dbManager.insert(simpleLog);
        } else {
            this.queueManager.insert(simpleLog);
        }
    }

    public void insert(long j, String str, LogType logType) {
        insert(new SimpleLog(j, str, logType));
    }

    public void delete() {
        if (this.useDatabase) {
            this.dbManager.delete(Utils.getDaysAgo(5));
        }
    }

    public Queue<SimpleLog> get() {
        return get(0);
    }

    public Queue<SimpleLog> get(int i) {
        Queue<SimpleLog> queue;
        if (this.useDatabase) {
            delete();
            if (i <= 0) {
                queue = this.dbManager.selectAll();
            } else {
                queue = this.dbManager.selectSome(i);
            }
        } else {
            queue = this.queueManager.getAll();
        }
        if (!queue.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("get log from ");
            sb.append(this.useDatabase ? "Database " : "Queue ");
            sb.append("(");
            sb.append(queue.size());
            sb.append(")");
            Debug.LogENG(sb.toString());
        }
        return queue;
    }

    public void remove(List<String> list) {
        if (!list.isEmpty() && this.useDatabase) {
            this.dbManager.delete(list);
        }
    }
}
