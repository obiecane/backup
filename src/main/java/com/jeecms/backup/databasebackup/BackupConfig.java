package com.jeecms.backup.databasebackup;

import com.jeecms.backup.entity.BackupDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/30 14:47
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Slf4j
@Accessors(chain = true)
@Data
@ToString
public class BackupConfig {
    public static final int DB_TYPE_MYSQL = 1;
    public static final int DB_TYPE_SQL_SERVER = 2;
    public static final int DB_TYPE_ORACLE = 3;
    public static final int DB_TYPE_UNKNOWN = 9999;

    /**
     * 数据库类型
     */
    private Integer databaseType;
    /**
     * 数据库名, 如果是oracle则是oracle的实例名或服务名
     */
    private String databaseName;
    /**
     * 主机
     */
    private String host;
    /**
     * 端口
     */
    private Integer port;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 数据保存路径
     */
    private String dataSavePath;

    @Setter(AccessLevel.PRIVATE)
    private String jdbcUrl;
//    /** 日志保存路径 */
//    private String logSavePath;



    public static BackupConfig newInstance() {
        return new BackupConfig();
    }

    public static BackupConfig of(BackupDto dto) {
        BackupConfig backupConfig = newInstance();
        // 提取host port
        String jdbcurl = dto.getJdbcUrl();
        int dbType = resolveDataTypeFromJdbcUrl(jdbcurl);
        if (dbType == DB_TYPE_MYSQL) {
            resolveMysqlUrl(jdbcurl, backupConfig);
        } else if (dbType == DB_TYPE_SQL_SERVER) {
            resolveSqlServerUrl(jdbcurl, backupConfig);
        } else if (dbType == DB_TYPE_ORACLE) {
            resolveOracleUrl(jdbcurl, backupConfig);
        }
        String username = dto.getUsername();
        String password = dto.getPassword();
        String bakFilePath = dto.getBakFilePath();
        backupConfig.setUsername(username);
        backupConfig.setPassword(password);
        backupConfig.setDatabaseType(dbType);
        backupConfig.setJdbcUrl(jdbcurl);
        backupConfig.setDataSavePath(bakFilePath);
        return backupConfig;
    }

    private static int resolveDataTypeFromJdbcUrl(String jdbcUrl) {
        jdbcUrl = jdbcUrl.toLowerCase();
        if (jdbcUrl.startsWith("jdbc:mysql")) {
            return DB_TYPE_MYSQL;
        } else if (jdbcUrl.startsWith("jdbc:sqlserver")) {
            return DB_TYPE_SQL_SERVER;
        } else if (jdbcUrl.startsWith("jdbc:oracle")) {
            return DB_TYPE_ORACLE;
        }

        return DB_TYPE_UNKNOWN;
    }

    private static void resolveMysqlUrl(String jdbcUrl, BackupConfig backupConfig) {
        // jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=GBK
        String tmp = jdbcUrl.split("://")[1];
        Integer port = null;
        String host = StringUtils.substringBefore(tmp, "/");
        if (host.contains(":")) {
            String[] sp = host.split(":");
            host = sp[0];
            try {
                port = Integer.parseInt(sp[1]);
            } catch (Exception ignore) {

            }
        }

        String dbName = StringUtils.substringBefore(StringUtils.substringAfter(tmp, "/"), "?");

        backupConfig.setHost(host);
        backupConfig.setPort(port);
        backupConfig.setDatabaseName(dbName);
    }

    private static void resolveSqlServerUrl(String jdbcUrl, BackupConfig backupConfig) {
        // jdbc:sqlserver://192.168.1.130:1433;database=ahos;user=sa;password=ahtec";
        String tmp = jdbcUrl.split("://")[1];
        Integer port = null;
        String host = StringUtils.substringBefore(tmp, ";");
        if (host.contains(":")) {
            String[] sp = host.split(":");
            host = sp[0];
            try {
                port = Integer.parseInt(sp[1]);
            } catch (Exception ignore) {

            }
        }

        String dbName = null;
        for (String s : tmp.split(";")) {
            s = s.toLowerCase();
            if (s.startsWith("database=") || s.startsWith("databasename=")) {
                dbName = s.split("=")[1];
            }
        }

        backupConfig.setHost(host);
        backupConfig.setPort(port);
        backupConfig.setDatabaseName(dbName);
    }

    private static void resolveOracleUrl(String jdbcUrl, BackupConfig backupConfig) {
        // jdbc:oracle:thin:@192.168.1.250:1521:devdb
        String tmp = jdbcUrl.split("@")[1];
        Integer port = null;
        String dbName;
        String host = StringUtils.substringBefore(tmp, ":");
        tmp = StringUtils.substringAfter(tmp, ":");
        // 实例名以:分隔, 服务名以/分隔
        if (tmp.contains(":") || tmp.contains("/")) {
            String[] sp = tmp.split("[:/]");
            port = Integer.valueOf(sp[0]);
            dbName = sp[1];
        } else {
            dbName = tmp;
        }
        dbName = dbName.split("\\?")[0];
        backupConfig.setHost(host);
        backupConfig.setPort(port);
        backupConfig.setDatabaseName(dbName);
    }


    public int getPort() {
        if (port != null) {
            return port;
        }
        return defaultPort(databaseType);
    }

    private static int defaultPort(int dbType) {
        switch (dbType) {
            case DB_TYPE_MYSQL:
                return 3306;
            case DB_TYPE_SQL_SERVER:
                return 1433;
            case DB_TYPE_ORACLE:
                return 1521;
            default:
                return 1;
        }
    }

    public String getJdbcUrl() {
        if (StringUtils.isNotBlank(jdbcUrl)) {
            return jdbcUrl;
        }
        if (getDatabaseType() == DB_TYPE_ORACLE) {
            // jdbc:oracle:thin:@192.168.1.250:1521:devdb
            return String.format("jdbc:oracle:thin:@%s:%d:%s", getHost(), getPort(), getDatabaseName());
        } else if (getDatabaseType() == DB_TYPE_SQL_SERVER) {
            // jdbc:sqlserver://192.168.1.130:1433;database=ahos;user=sa;password=ahtec";
            return String.format("jdbc:sqlserver://%s:%d;database=%s;", getHost(), getPort(), getDatabaseName());
        } else if (getDatabaseType() == DB_TYPE_MYSQL) {
            // jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=GBK
            return String.format("jdbc:mysql://%s:%d/%s;", getHost(), getPort(), getDatabaseName());
        }
        return null;
    }
}
