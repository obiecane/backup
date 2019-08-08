package com.jeecms.backup.databasebackup;

/**
 * 啥都不干的
 *
 * @author zak
 * @version 1.0
 * @date 2019/7/30 14:53
 */
public class NonBackup extends AbstractDatabaseBackup {
    @Override
    public String doBackup() {
        // do nothing
        return null;
    }

    @Override
    public boolean doRecovery() {
        // do nothing
        return false;
    }

    @Override
    protected void before() {
        // do nothing
    }
}
