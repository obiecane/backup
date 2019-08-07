package com.jeecms.backup.databasebackup;

/**
 * 啥都不干的
 *
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/30 14:53
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
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
