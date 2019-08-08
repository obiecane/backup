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
 * @author zak
 * @version 1.0
 * @date 2019/8/2 14:18
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
