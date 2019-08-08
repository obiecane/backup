package com.jeecms.backup.entity;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author zak
 * @version 1.0
 * @date 2019/8/2 14:03
 */
@Data
@ToString
public class BackupDto {
    @NotNull
    private Integer backupId;
    @NotBlank
    private String jdbcUrl;
    @NotBlank
    private String username;
    private String password;
    /**
     * 备份文件所在路径
     */
    private String bakFilePath;
    /**
     * 备份或还原完成后的回调地址
     */
    @NotBlank
    private String callbackUrl;
}
