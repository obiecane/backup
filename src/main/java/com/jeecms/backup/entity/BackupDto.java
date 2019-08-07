package com.jeecms.backup.entity;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/8/2 14:03
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
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
