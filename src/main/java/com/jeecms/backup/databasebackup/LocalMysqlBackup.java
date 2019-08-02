package com.jeecms.backup.databasebackup;

import com.jeecms.backup.utils.CmdExcutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import java.util.LinkedList;
import java.util.List;

import static com.jeecms.backup.utils.CmdExcutor.CmdResult;
import static com.jeecms.backup.utils.CmdExcutor.executeCommand;

/**
 * 本地MySql数据库备份还原
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/30 13:50
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class LocalMysqlBackup extends AbstractDatabaseBackup {

    public static void main(String[] args) {
        DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setUsername("root");
        dataSourceProperties.setPassword("123456");
        dataSourceProperties.setUrl("jdbc:mysql://127.0.0.1:3306/test");

        BackupConfig backupConfig = BackupConfig
                .of(dataSourceProperties)
                .setDataSavePath("F:\\code\\jeecms-parent\\jeecms-backup\\target\\classes\\1564650505937.jcbak");

        Backup databaseBackup = DatabaseBackuperFactory.createBackuper(backupConfig);
//        String backup = databaseBackup.backup();
//        System.out.println("备份文件:" + backup);
        databaseBackup.recovery();
    }

    /**
     * 备份
     * 参考备份命令:
     * mysqldump -h127.0.0.1 -uroot -p123456 --databases Test >D:/12580.sql
     * @author Zhu Kaixiao
     * @date 2019/7/31 9:11
     **/
    @Override
    public String doBackup() {
        List<String> cmdList = new LinkedList<>();
        cmdList.add("mysqldump");
        cmdList.add("-h" + backupConfig.getHost());
        cmdList.add("-P" + backupConfig.getPort());
        cmdList.add("-u" + backupConfig.getUsername());
        if (StringUtils.isNotBlank(backupConfig.getPassword())) {
            cmdList.add("-p" + backupConfig.getPassword());
        }

        // 参数中加了--databases或--all-databases, 那么生成的sql文件中就会有create database语句
        if (StringUtils.isNotBlank(backupConfig.getDatabaseName())) {
            cmdList.add("--databases");
            cmdList.add(backupConfig.getDatabaseName());
        } else {
            cmdList.add("--all-databases");
        }
        cmdList.add(">\"" + backupConfig.getDataSavePath() + "\"");
        CmdResult cmdResult = CmdExcutor.executeCommand(cmdList);
        return cmdResult.getCode() == 0 ? backupConfig.getDataSavePath() : null;
    }


    /**
     * 因为现在在备份时是全量备份, 并且备份时加了--databases参数
     * 所以生成的sql文件中已经有了create database语句
     * 所以在恢复时不需要指定数据库名称
     * 参考恢复命令:
     *  mysql -uroot -h127.0.0.1 -p123456 Test <D:/12580.sql
     * @author Zhu Kaixiao
     * @date 2019/7/31 9:06
     **/
    @Override
    public boolean doRecovery() {
        List<String> cmdList = new LinkedList<>();
        cmdList.add("mysql");
        cmdList.add("-h" + backupConfig.getHost());
        cmdList.add("-P" + backupConfig.getPort());
        cmdList.add("-u" + backupConfig.getUsername());
        if (StringUtils.isNotBlank(backupConfig.getPassword())) {
            cmdList.add("-p" + backupConfig.getPassword());
        }
//        if (StringUtils.isNotBlank(backupConfig.getDatabaseName())) {
//            cmdList.add(backupConfig.getDatabaseName());
//        }
        cmdList.add("--default-character-set=utf8");
        cmdList.add("<\"" + backupConfig.getDataSavePath() + "\"");
        CmdResult cmdResult = executeCommand(cmdList);
        return cmdResult.getCode() == 0;
    }

}
