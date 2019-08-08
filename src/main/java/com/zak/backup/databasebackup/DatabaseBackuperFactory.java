package com.zak.backup.databasebackup;

import javax.validation.constraints.NotNull;

/**
 * @author zak
 * @version 1.0
 * @date 2019/7/30 14:45
 */
public class DatabaseBackuperFactory {

    public static Backup createBackuper(@NotNull BackupConfig backupConfig) {
        AbstractDatabaseBackup backuper;
        switch (backupConfig.getDatabaseType()) {
            case BackupConfig.DB_TYPE_MYSQL:
                backuper = new LocalMysqlBackup();
                break;
            case BackupConfig.DB_TYPE_SQL_SERVER:
                backuper = new LocalSqlServerBackup();
                break;
            case BackupConfig.DB_TYPE_ORACLE:
                backuper = new LocalOracleBackup();
                break;
            default:
                backuper = new NonBackup();
                break;
        }
        backuper.setBackupConfig(backupConfig);
        return backuper;
    }
}
