package com.jeecms.backup.databasebackup;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author zak
 * @version 1.0
 * @date 2019/7/30 14:42
 */
@Slf4j
public abstract class AbstractDatabaseBackup implements Backup {

    @Setter
    protected BackupConfig backupConfig;


    @Override
    public String backup() {
        beforeBackup();
        String backup = doBackup();
        log.info("备份{}: {}", backup == null ? "失败" : "成功", backupConfig);
        return backup;
    }

    @Override
    public boolean recovery() {
        beforeRecovery();
        boolean success = doRecovery();
        log.info("还原{}: {}", success ? "成功" : "失败", backupConfig);
        return success;
    }

    /**
     * 通用前置操作
     *
     * @author zak
     * @date 2019/8/1 17:21
     **/
    protected void before() {
        if (StringUtils.isBlank(backupConfig.getUsername())) {
            throw new IllegalArgumentException("数据库用户名不能为空");
        }

        if (backupConfig.getHost() == null) {
            backupConfig.setHost("127.0.0.1");
        }
    }

    /**
     * 备份前置操作
     *
     * @author zak
     * @date 2019/7/30 16:49
     **/
    private void beforeBackup() {
        // 自动生成保存路径
        if (StringUtils.isBlank(backupConfig.getDataSavePath())) {
            try {
                File dir = new File("./");
                String filename = backupConfig.getDatabaseName() + "_" + backupConfig.getUsername() + "_" + System.currentTimeMillis();
                backupConfig.setDataSavePath(dir.getCanonicalPath() + File.separator + "database-backup" + File.separator + filename + ".jcbak");
            } catch (Exception e) {
                log.error("自动生成保存路径失败", e);
            }
        } else {
            // 保存路径的扩展名只能是.jcbak或.sql
            String savePath = backupConfig.getDataSavePath();
            String extension = FilenameUtils.getExtension(savePath);
            if (!"jcbak".equalsIgnoreCase(extension) && !"sql".equalsIgnoreCase(extension)) {
                savePath = FilenameUtils.getFullPath(savePath) + FilenameUtils.getBaseName(savePath) + ".jcbak";
                backupConfig.setDataSavePath(savePath);
            }
        }

        File parentDir = new File(backupConfig.getDataSavePath()).getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        before();
    }

    /**
     * 还原前置操作
     *
     * @author zak
     * @date 2019/7/30 16:49
     **/
    private void beforeRecovery() {
        // 自动生成保存路径
        if (StringUtils.isBlank(backupConfig.getDataSavePath())) {
            throw new IllegalArgumentException("备份文件路径不能为空");
        }
        before();
    }

    /**
     * 进行数据库备份操作
     *
     * @return String 备份文件所在路径
     * @author zak
     * @date 2019/7/30 16:49
     **/
    protected abstract String doBackup();

    /**
     * 进行数据库还原操作
     *
     * @return boolean 是否还原成功
     * @author zak
     * @date 2019/8/1 17:22
     **/
    protected abstract boolean doRecovery();


    protected static synchronized Connection getConnection(String driverClassName, String url, String user, String passwd) {
        Connection conn;
        try {
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url, user, passwd);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }
}
