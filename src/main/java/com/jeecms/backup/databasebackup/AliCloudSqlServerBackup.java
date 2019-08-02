package com.jeecms.backup.databasebackup;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/31 17:00
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class AliCloudSqlServerBackup extends AbstractDatabaseBackup {


    @Override
    protected String doBackup() {
        return null;
    }

    @Override
    protected boolean doRecovery() {
        return true;
    }

    @Override
    protected void before() {

    }
}
