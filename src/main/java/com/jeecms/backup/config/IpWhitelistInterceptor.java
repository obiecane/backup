package com.jeecms.backup.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ip白名单拦截器
 *
 * @author zak
 * @version 1.0
 * @date 2019/8/2 14:21
 */
@Slf4j
@AllArgsConstructor
public class IpWhitelistInterceptor extends HandlerInterceptorAdapter {

    private BackupProperties backupProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String accessIp = getIpAddress(request);
        if (backupProperties.getIpWhitelist().contains(accessIp)) {
            return true;
        } else {
            log.info("已拦截非法访问IP[{}]", accessIp);
            response.sendError(404);
            return false;
        }
    }


    /**
     * 从request中获取请求方IP
     *
     * @param request
     * @return java.lang.String
     * @author zak
     * @date 2019/8/2 15:26
     **/
    private static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
