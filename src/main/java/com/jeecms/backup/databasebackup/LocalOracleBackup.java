package com.jeecms.backup.databasebackup;

import com.jeecms.backup.utils.CmdExcutor;
import com.jeecms.backup.utils.OSUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jeecms.backup.utils.CmdExcutor.CmdResult;
import static com.jeecms.backup.utils.CmdExcutor.executeCommand;

/**
 * 本地Oracle数据库备份还原
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/31 11:39
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Slf4j
public class LocalOracleBackup extends AbstractDatabaseBackup {

    private static final String DRIVER_CLASS_NAME = "oracle.jdbc.driver.OracleDriver";

    private String directory;
    private String dumpfile;

    public static void main( String[] args ) {
        DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setUsername("zkx");
        dataSourceProperties.setPassword("123456");
        dataSourceProperties.setUrl("jdbc:oracle:thin:@localhost:1521:orcl");

        BackupConfig backupConfig = BackupConfig
                .of(dataSourceProperties)
                .setDataSavePath("F:\\test\\12580051111955.JCBAK");

        Backup databaseBackup = DatabaseBackuperFactory.createBackuper(backupConfig);
        String backup = databaseBackup.backup();
        System.out.println("备份文件:" + backup);
//        databaseBackup.recovery();
    }

    @Override
    protected String doBackup() {
        List<String> cmdList = new LinkedList<>();
        cmdList.add("expdp");
        cmdList.add(String.format("%s/%s@%s:%d/%s", backupConfig.getUsername(), backupConfig.getPassword(),
                backupConfig.getHost(), backupConfig.getPort(), backupConfig.getDatabaseName()));
        cmdList.add("schemas=" + backupConfig.getUsername());
//        cmdList.add(String.format("directory=\"%s\"", directory)); 该目录需要先再oracle中设置
        cmdList.add(String.format("dumpfile=\"%s\"", dumpfile));
        CmdResult cmdResult = CmdExcutor.executeCommand(cmdList);

        return cmdResult.getCode() == 0 ? fetchBakFilePath(cmdResult) : null;
    }

    @Override
    protected boolean doRecovery() {
        String dumpDir = null;
        // 1. 获取DATA_PUMP_DIR
        try (Connection connection = getConnection(DRIVER_CLASS_NAME,
                backupConfig.getJdbcUrl(), backupConfig.getUsername(), backupConfig.getPassword())) {
            String sql = "select * from dba_directories";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String directoryName = resultSet.getString("directory_name");
                if ("DATA_PUMP_DIR".equalsIgnoreCase(directoryName)) {
                    dumpDir = resultSet.getString("directory_path");
                    break;
                }
            }
        } catch (Exception e) {
            log.error("数据库[{}]还原失败!\t无法获取[DATA_PUMP_DIR]", backupConfig.getDatabaseName());
            return false;
        }
        try {
            // 先把文件拷贝到oracle指定的文件夹下
            FileUtils.copyFile(new File(backupConfig.getDataSavePath()), new File(dumpDir, dumpfile));
        } catch (IOException e) {
            log.error("数据库[{}]还原失败!\t复制备份文件到DATA_PUMP_DIR失败]", backupConfig.getDatabaseName());
            return false;
        }

        List<String> cmdList = new LinkedList<>();
        cmdList.add("impdp");
        cmdList.add(String.format("%s/%s@%s:%d/%s", backupConfig.getUsername(), backupConfig.getPassword(),
                backupConfig.getHost(), backupConfig.getPort(), backupConfig.getDatabaseName()));
        cmdList.add("FULL=y");
        cmdList.add("table_exists_action=replace");
        cmdList.add(String.format("dumpfile=\"%s\"", dumpfile));
        CmdResult cmdResult = executeCommand(cmdList);

        if (cmdResult.getCode() == 0 || cmdResult.getError().contains("已经完成")) {
            return true;
        } else {
            log.error("数据库[{}]还原失败!\t执行还原命令失败!\t{}", backupConfig.getDatabaseName(), cmdResult.getError());
            return false;
        }
    }

    @Override
    protected void before() {
        super.before();
        Objects.requireNonNull(backupConfig.getDatabaseName());
        directory = FilenameUtils.getFullPathNoEndSeparator(backupConfig.getDataSavePath());
        dumpfile = FilenameUtils.getName(backupConfig.getDataSavePath());
    }


    private static final Pattern WINDOWS_PATH_PATTERN = Pattern.compile("[a-z]:\\\\.+?\\.((jcbak)|(sql))", Pattern.CASE_INSENSITIVE);
    private static final Pattern LINUX_PATH_PATTERN = Pattern.compile("/.+?\\.((jcbak)|(sql))", Pattern.CASE_INSENSITIVE);
    private String fetchBakFilePath(CmdResult cmdResult) {
        Pattern pathPattern = OSUtil.currentPlatform() == OSUtil.PLATFORM_WINDOWS
                ? WINDOWS_PATH_PATTERN
                : LINUX_PATH_PATTERN;
        // 1. 从命令行执行结果中提取文件路径
        if (cmdResult.getCode() == 0) {
            String str = cmdResult.getOut() + cmdResult.getError();
            Matcher matcher = pathPattern.matcher(str);
            int mStart = 0;
            while (matcher.find(mStart)) {
                String match = matcher.group();
                try {
                    File file = new File(match);
                    return file.getCanonicalPath();
                } catch (Exception e) {
                    mStart = matcher.start() + 1;
                }
            }
        }

        // 2. 拷贝文件到指定目录
        return null;
    }
}
