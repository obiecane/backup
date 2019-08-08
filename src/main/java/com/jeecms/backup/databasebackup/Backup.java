package com.jeecms.backup.databasebackup;

/**
 * @author zak
 * @version 1.0
 * @date 2019/7/30 14:38
 */
public interface Backup {

    /**
     * 备份
     *
     * @return String 生成的备份文件路径
     * @author zak
     * @date 2019/7/30 14:41
     **/
    String backup();

    /**
     * 还原
     *
     * @return boolean 是否还原成功
     * @author zak
     * @date 2019/7/30 14:41
     **/
    boolean recovery();
}
