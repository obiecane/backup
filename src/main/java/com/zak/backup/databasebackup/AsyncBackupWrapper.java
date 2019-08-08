package com.zak.backup.databasebackup;

import com.zak.backup.exception.BackupException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 备份任务异步执行包装器
 *
 * @author zak
 * @version 1.0
 * @date 2019/8/5 16:17
 */
@Slf4j
public class AsyncBackupWrapper {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    /**
     * 空闲
     */
    public static final int IDLE = 0;
    /**
     * 备份中
     */
    public static final int IN_BACKUP = 1;
    /**
     * 还原中
     */
    public static final int IN_RECOVERY = 2;

    private volatile int state;

    public void backup(final Backup backup, final Callback<String, String> callback) {
        if (state == IDLE) {
            EXECUTOR.submit(() -> {
                try {
                    state = IN_BACKUP;
                    String errMsg = null;
                    String bakFilePath = null;
                    try {
                        bakFilePath = backup.backup();
                    } catch (Exception e) {
                        errMsg = e.getMessage();
                    }
                    callback.call(bakFilePath, errMsg);
                } finally {
                    state = IDLE;
                }
            });
        } else {
            throw new BackupException("已有" + (state == IN_BACKUP ? "备份" : "还原") + "任务正在执行");
        }
    }

    public void recovery(final Backup backup, final Callback<Boolean, String> callback) {
        if (state == IDLE) {
            EXECUTOR.submit(() -> {
                try {
                    state = IN_RECOVERY;
                    String errMsg = null;
                    boolean success = false;
                    try {
                        success = backup.recovery();
                    } catch (Exception e) {
                        errMsg = e.getMessage();
                    }
                    callback.call(success, errMsg);
                } finally {
                    state = IDLE;
                }
            });
        } else {
            throw new BackupException("已有" + (state == IN_BACKUP ? "备份" : "还原") + "任务正在执行");
        }
    }

    public interface Callback<P, ERR> {
        void call(P param, ERR errorMsg);
    }
}

