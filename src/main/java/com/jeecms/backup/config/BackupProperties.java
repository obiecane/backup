package com.jeecms.backup.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/8/2 14:18
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Data
@ToString
@Component
@ConfigurationProperties(prefix = "backup")
@PropertySource(value = "application.properties")
public class BackupProperties {

    @PostConstruct
    private void initConstant() {
        Constant.IGNORE_TABLES = ignoreTables;
    }

    /**
     * ip白名单
     */
    private List<String> ipWhitelist = Collections.emptyList();

    /**
     * 需要忽略的表
     */
    private List<String> ignoreTables = Collections.emptyList();
}
