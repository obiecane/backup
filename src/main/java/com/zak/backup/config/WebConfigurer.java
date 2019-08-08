package com.zak.backup.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author zak
 * @version 1.0
 * @date 2019/8/2 14:23
 */
@Configuration
@AllArgsConstructor
public class WebConfigurer implements WebMvcConfigurer {

    private BackupProperties backupProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截所有请求
        registry.addInterceptor(new IpWhitelistInterceptor(backupProperties)).addPathPatterns("/**");
    }


}