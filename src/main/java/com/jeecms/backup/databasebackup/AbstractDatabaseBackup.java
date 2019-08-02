package com.jeecms.backup.databasebackup;

import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/30 14:42
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public abstract class AbstractDatabaseBackup implements Backup {

    @Setter
    protected BackupConfig backupConfig;


    @Override
    public String backup() {
        beforeBackup();
        return doBackup();
    }

    @Override
    public boolean recovery() {
        beforeRecovery();
        return doRecovery();
    }

    /**
     * 通用前置操作
     * @author Zhu Kaixiao
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
     * @author Zhu Kaixiao
     * @date 2019/7/30 16:49
     **/
    private void beforeBackup() {
        // 自动生成保存路径
        if (StringUtils.isBlank(backupConfig.getDataSavePath())) {
            try {
                URI uri = this.getClass().getClassLoader().getResource(".").toURI();
                File dir = new File(uri);
                backupConfig.setDataSavePath(dir.getCanonicalPath() + File.separator + System.currentTimeMillis() + ".jcbak");
            } catch (Exception ignore) { }
        } else {
            // 保存路径的扩展名只能是.jcbak或.sql
            String savePath = backupConfig.getDataSavePath();
            String extension = FilenameUtils.getExtension(savePath);
            if (!"jcbak".equalsIgnoreCase(extension) && !"sql".equalsIgnoreCase(extension)) {
                savePath = FilenameUtils.getFullPath(savePath) + FilenameUtils.getBaseName(savePath) + ".jcbak";
                backupConfig.setDataSavePath(savePath);
            }
        }
        before();
    }

    /**
     * 还原前置操作
     * @author Zhu Kaixiao
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
     * @return String 备份文件所在路径
     * @author Zhu Kaixiao
     * @date 2019/7/30 16:49
     **/
    protected abstract String doBackup();

    /**
     * 进行数据库还原操作
     * @return boolean 是否还原成功
     * @author Zhu Kaixiao
     * @date 2019/8/1 17:22
     **/
    protected abstract boolean doRecovery();


    protected static synchronized Connection getConnection(String driverClassName, String url, String user, String passwd){
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
